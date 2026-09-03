# Parser defects and enhancements workplan

Date: 2026-08-14  
Status: Draft — work through later  
Audience: PSS parser project (share alongside example briefs)

Living list of parser defects and enhancements discovered from RMCP / DBT scan examples. Add new phases at the bottom. Keep each phase self-contained: problem, expected behavior, reproduction, acceptance, and links to any external brief.

## How to add a phase

1. Add a row to the progress tracker.
2. Add a `## Phase N` section with: problem, expected behavior, reproduction, acceptance, out of scope.
3. If the write-up is long, put it in a sibling markdown and **reference that file** from the phase instead of duplicating it here.

## Progress tracker

| Phase | Item | Kind | Status | Detail |
|------|------|------|--------|--------|
| 1 | Snowflake PIVOT quoted unaliased identifiers | Enhancement | Not started | See external brief |
| 2 | Set-op interfaces, `VALUES`, ordered aggregates, `CASE`, parse performance, and cross-version source/diagnostic differences | Defect / enhancement | **Reopened** — 2.1–2.8, **2.9 complete**; **2.10** open; **2.11 in progress** (W1 done) | This file (2.1–2.11) |
| 3 | Snowflake `PARSE_URL` / PARSE functions with `:` field access | Enhancement | Not started | This file |
| 4 | Snowflake `DATEADD` / date-part keywords vs column resolution | Defect | Not started | This file |
| 5 | Snowflake ARRAY syntax and functions | Enhancement | Not started | This file (5.1–5.12) |
| 6 | Simple JINJA substitutions support | Enhancement | Not started | This file (6.1–6.4); **6.4** closes Phase **2.5** Jinja fixture |
| 7 | Deep JINJA language support | Enhancement (optional) | Not started | This file |

---

## Phase 1 — Snowflake PIVOT quoted unaliased identifiers

**Kind:** Enhancement (older Snowflake PIVOT column-naming form)

**Status:** Not started

Do **not** duplicate the specification here. The full problem statement, reproducing query, current parser failures, `derivation.source_columns` / location-reference expectations, tests, and out-of-scope notes live in:

- [snowflake-pivot-quoted-identifier-parser-brief.md](./snowflake-pivot-quoted-identifier-parser-brief.md)

**Summary:** Lexer/parser must treat older unaliased PIVOT output names as delimited identifiers — both `"'diq_entry_year'"` (string IN-list, quotes in the name) and `"1"` (numeric IN-list, digit-string name). Register those IN-list values in `derivation.source_columns` and resolve them as normal column references. Newer `IN ('x' AS alias)` / `IN (1 AS rule1)` must keep working.

**Acceptance:** Criteria in the external brief. Both fixtures (string `"'diq_entry_year'"` and numeric `"1"`…`"6"` / `IN (1,2,3,4,5,6)`) must parse with no FATAL / no recovery; `derivation.source_columns` and location references must treat the delimited identifiers as normal column refs.

---

## Phase 2 — Set-operation branch sources, `VALUES`, and ordered aggregates

**Kind:** Defect (set-op interface, `VALUES` FROM syntax, WITH final-query source finalization) plus standalone grammar enhancements

**Status:** **Reopened** (2026-08-19) — subtasks 2.1–2.8 and **2.9** complete; **2.10** open; **2.11 in progress** (**W1** done). **2.5** SQL guardrail complete; Jinja-authored fixture closure → **Phase 6.4**.

**Theme:** UNION / INTERSECT / EXCEPT branches must keep a usable FROM/JOIN scope and a sensible output interface (2.1–2.2). **2.3** is `WITHIN GROUP` on ordered aggregates. **2.4** is nested searched `CASE`. **2.5** is a parse hang / `PARSE_TIMEOUT` on a multi-CTE rollup query — diagnose root cause, then fix termination. **2.6** is a flat searched `CASE` that extracts `product` from `cat.title` via `POSITION`, nested `SPLIT`/`SPLIT_PART`, and `NULLIF`/`TRIM`/`COALESCE`. **2.7** (complete) locked WITH CTE physical/tuple trailing-clause collection in global `tableDictionary` and confirmed CTE aliases belong in query/symbol structures only. **2.8** investigates broad 5.1.3 parse latency by timing parse-tree construction, event walking / semantic diagnostics, and result return separately before optimizing the dominant paths. **2.9** investigates remaining 5.0.0-3 versus 5.1.3 source and diagnostic differences and determines which version is semantically correct before changing behavior.

### Recommended implementation order (updated 2026-08-19)

Completed order was **2.1 → 2.2 → 2.6 → 2.3 → 2.4 → 2.5 → 2.7**. Characterize **2.9** before editing shared finalization/resolution code for live Panto rows; 2.8 instrumentation can proceed independently. Choose implementation order from confirmed root causes and measured impact. Rationale:

| Order | Step | Effort | Impact | Why |
|------|------|--------|--------|-----|
| 1 | **2.1** | Low — walker-only (`SqlASTWalkerHelper`, ~3 column-count comparison sites) | High — common dbt `SELECT *` UNION explicit-column pattern | **Complete** — skip when fewer side has `*` or both sides have `*`. Tests in `SqlEventWalkerSubqueriesAndClauseSemanticsTests`. |
| 2 | **2.2** | Medium — focused grammar (`FROM VALUES … AS alias (cols)` without outer parens) + table binding | High — `COALESCE(pso.sort_order, …)` fatal is a recovery cascade, not a resolution bug | **Complete** — `values_statement` accepts unparenthesized `VALUES values_matrix`; walker unchanged. Tests in `SqlEventWalkerUnparenthesizedValuesTests`. |
| 3 | **2.6** | Medium — `SPLIT` / `SPLIT_PART` / `expr[n]` subscript (shared with 5.4) | Medium — flat `CASE` attribution models | **Complete** — postfix `arraySubscriptSuffix` + bracket/subscript disambiguation; tests in `SqlEventWalkerArraySubscriptTests` and `SqlEventWalkerBracketedIdentifierTests`. |
| 4 | **2.3** | Medium–high — new shared `WITHIN GROUP (ORDER BY …)` production | High — `LISTAGG` and ordered aggregates in analytics models | **Complete** — `ordered_aggregate_expression` + walker `within_group_ordered_by`; tests in `SqlEventWalkerWithinGroupOrderedAggregateTests`. |
| 5 | **2.4** | Low–medium after 2.6 — nested `CASE` likely already parses once `SPLIT(…)[n]` works | Medium | **Complete** — grammar already allowed nested `CASE`; blocker was `SPLIT(…)[n]` (2.6). Exemplar tests in `SqlEventWalkerArraySubscriptTests`. |
| 6 | **2.5** | Unknown — investigate-first bisect | Low breadth, high severity per query | **Complete** (SQL guardrail) — table-stubbed exemplar in `dncEmailRollupMultiCteExemplarV0Test`. Jinja-authored fixture (§2.5 starter query) → **Phase 6.4**. |
| 7 | **2.7** | Medium — WITH CTE physical-source / tuple-substitution finalization | High — trailing-clause tuple columns must land on global physical/tuple keys | **Complete** (2026-09-01) — CTE aliases intentionally absent from global `tableDictionary`; goldens in `SqlEventWalkerWithCteTupleSubstitutionTests`. |
| 8 | **2.8** | Investigation first — stage timing, 74-query corpus characterization, 5.0.0-3 comparison, then targeted profiling | High — 5.1.3 times out after 90 seconds on queries that complete on 5.0.0-3 | **Complete (2026-09-02)** — E1–E3; **74/74** rows &lt; 90 s; C2.2 fixed α≈2; walker residuals tracked in §2.8 **W-track** → **2.11** |
| 9 | **2.9** | Medium — dual-parse, message capture, semantic adjudication, then cluster-specific fixes | High — 5.1.3 emits new FATALs or loses non-CTE source evidence on live queries | **Complete** (2026-09-01) — all eight Panto degradations adjudicated; see [panto-tabledict-degradations-2026-08-19.md](../docs/rmcp-handoff/5.1.3-panto-outstanding/panto-tabledict-degradations-2026-08-19.md). |
| 10 | **2.10** | SLL→LL two-stage prediction policy in `SqlParserAccess` | Medium — production parse latency on some rows (e.g. 4176) | **Not started** |
| 11 | **2.11** | Panto timeout corpus residuals: walk-gate follow-up + fast-FATAL rows (11) | High — correctness / diagnostics on live RMCP SQL | **In progress** — §2.8 **W1–W3** done; **W4** Part 1 done; **W5** + Cluster B open |

**Dependencies:** 2.1–2.7 complete. Characterize **2.9** for residual functional source/diagnostic differences; 2.8 instrumentation and corpus characterization can proceed independently. **Fixtures:** `SqlEventWalkerSubqueriesAndClauseSemanticsTests.unionWildcardBranchAgainstExplicitColumnListTest` (2.1); `SqlEventWalkerUnparenthesizedValuesTests` (2.2); `SqlEventWalkerArraySubscriptTests` (2.4 starter/isolation/`SPLIT_PART(…,-1)` plus **placement** — nested `CASE` in `WHERE` / `JOIN ON` / `HAVING` / `UPDATE SET`; product `CASE` in `WHERE` / `HAVING` / `UPDATE SET`; all with six extractor goldens) (2.4, 2.6); `SqlEventWalkerWithinGroupOrderedAggregateTests` (2.3); `SqlEventWalkerSubqueriesAndClauseSemanticsTests.dncEmailRollupMultiCteExemplarV0Test` (2.5 guardrail); `SqlEventWalkerWithCteTupleSubstitutionTests` (2.7 — complete); use Panto rows 3150, 3870, 4648, 4726, 5410, 5455, and clusters A–E in [panto-tabledict-degradations-2026-08-19.md](../docs/rmcp-handoff/5.1.3-panto-outstanding/panto-tabledict-degradations-2026-08-19.md) for **2.9** (CTE keys absent from global `tableDictionary` alone are not defects); use all 74 timeout rows indexed by `parse/docs/rmcp-handoff/5.1.3-panto-outstanding/panto-513-parse-timeouts-2026-08-19.md` or `panto_513_outstanding_issues.csv` for 2.8.

### Subtask tracker

| Step | Construction | Status |
|------|----------------|--------|
| 2.1 | Wildcard `*` matches any set-op column count | **Complete** |
| 2.2 | `VALUES (…)` `AS alias (col, …)` plus JOIN / `COALESCE` on the joined table | **Complete** |
| 2.3 | `WITHIN GROUP (ORDER BY …)` on `LISTAGG` and other ordered aggregates (`OVER` included) | **Complete** |
| 2.4 | Nested searched `CASE` (`CASE` as `THEN`/`ELSE` of `CASE`) plus inner `SPLIT`/`SPLIT_PART` predicands | **Complete** |
| 2.5 | Parse hang / `PARSE_TIMEOUT` on multi-CTE email DNC rollup (investigate + fix) | **Complete** (SQL guardrail); Jinja fixture → **6.4** |
| 2.6 | Flat searched `CASE` for `product` from `cat.title` (`POSITION`, nested `SPLIT`/`SPLIT_PART`, `NULLIF`/`TRIM`/`COALESCE`) | **Complete** |
| 2.7 | WITH CTE physical-source and tuple-substitution finalization | **Complete** (2026-09-01) — CTE aliases not in global `tableDictionary` by design |
| 2.8 | 5.1.3 slow-parse investigation: isolate parse, walker/diagnostics, and result-return time; optimize measured bottlenecks | **Complete (2026-09-02)** — E1–E3; timeout problem closed; see §2.8 |
| 2.9 | Adjudicate and resolve non-CTE / residual 5.0.0-3 versus 5.1.3 source and diagnostic differences | **Complete** (2026-09-01) |
| 2.10 | SLL→LL two-stage prediction in `SqlParserAccess` | **Not started** |
| 2.11 | Panto corpus residuals: walk-gate follow-up (W-track) + fast-FATAL clusters | **In progress** — W1 done; see §2.8 W-track |

---

### 2.1 — Set-operation column count: wildcard matches any interface

**Kind:** Defect (`SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH`)

**Status:** **Complete** (2026-08-15)

**Component:** `SqlASTWalkerHelper` (ast-walk)

#### Problem

For `UNION` / `INTERSECT` / `EXCEPT`, the walker compares branch interfaces by **column count**. A select-list `*` is counted as **one** column (`*`), so a wildcard branch is reported as width 1 and fatally mismatches an explicit N-column branch.

That is wrong for this checker: a wildcard means “whatever columns the source has,” so it should **match any width** on the other side and **must not** raise a column-miscount error.

### Observed diagnostic

```
FATAL: UNION has different column counts. Expected 1 columns (*) at (l:115 c:14) but there were 34 (primary_student_id, eab_entry_year_academic, eab_entry_term, program, inquiry, app_start, app_incomplete, app_submit, app_comp, deny, waitlist, cond_admit, admit, withdrawn, gross_deposit, melt, future_defer, previous_defer, enroll, readmit, inquiry_dt, app_start_dt, incomplete_dt, app_submit_dt, app_completion_dt, deny_dt, waitlist_dt, cond_admit_dt, admit_dt, withdrawal_dt, deposit_dt, deferral_dt, enroll_dt, intake_dt) at (l:120 c:7).
(SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH, ast-walk, SqlASTWalkerHelper, line 120, char 7)
```

Observed diagnostic (representative scan; line numbers may shift):

```
FATAL: UNION has different column counts. Expected 1 columns (*) at (l:122 c:14) but there were 34 (primary_student_id, eab_entry_year_academic, ...) at (l:127 c:7).
(SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH, ast-walk, SqlASTWalkerHelper, line 127, char 7)
```

Shape in the reproducing query:

```sql
status_logic_p1 AS
(
       SELECT * from cte_applicants

       UNION

       SELECT
       primary_student_id
       ,eab_entry_year_academic
       ,eab_entry_term
       -- ... 31 more explicit columns ...
```

`SELECT *` is a valid union with an explicit 34-column select list. The warehouse expands `*` to the CTE interface; the parser should not treat `*` as a single-column interface for this check.

#### Expected behavior

1. If **either** side of `UNION`, `UNION ALL`, `INTERSECT`, or `EXCEPT` has a wildcard select item (`*` or `alias.*`), **do not** emit `SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH`.
2. A wildcard matches **any** column count on the other side (1 vs 34, 34 vs 1, or `*` vs `*`).
3. Keep the fatal when **both** sides have **concrete** (non-wildcard) counts that differ — e.g. 5 named columns vs 4 named columns is still a real mismatch.
4. After skipping the miscount, continue normal column-resolution / interface alignment using the non-wildcard side’s names when one side is explicit, or source expansion when both sides are wildcards (existing resolution paths; this step is specifically “do not false-fatal on count”).

#### Suggested tests

| Case | Result |
|------|--------|
| `SELECT * FROM t UNION SELECT a, b, c FROM u` | No `SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH` |
| Explicit list `UNION SELECT *` (wildcard on the right) | Same: no miscount |
| Same pattern for `UNION ALL`, `INTERSECT`, `EXCEPT` | Same: no miscount |
| `SELECT * FROM t UNION SELECT * FROM u` | No miscount |
| `SELECT a FROM t UNION SELECT a, b FROM u` (no wildcard) | Still FATAL miscount |
| Qualified wildcard `SELECT cte_applicants.* UNION SELECT ...` (34 cols) | No miscount |
| CTE fixture `unionWildcardBranchAgainstExplicitColumnListTest` | Goldens pass; `assertNoFatalErrors` / `assertNoWalkerDiagnostics` pass after fix |

#### Acceptance

- The reproducing `SELECT *` vs explicit-column UNION above no longer reports `SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH`.
- Concrete vs concrete width mismatches still fatal.
- No change to Phase 1 PIVOT identifier work.

#### Out of scope (2.1)

- Expanding `*` to a fully named interface when the source table/CTE dictionary is missing (may still warn elsewhere).
- Rewriting models to replace `SELECT *` with explicit column lists.
- Other fatals on the same file (ambiguous columns, Snowflake dialect notes).

---

### 2.2 — `VALUES … AS alias (cols)` as a UNION branch source (JOIN + `COALESCE`)

**Kind:** Defect (parse strategy + qualified column resolution)

**Status:** **Complete** (2026-08-15)

**Component:** grammar (`SQLSelectParser.g4` `values_statement`); existing walker `exitValues_*` handlers unchanged

#### Diagnosis (why `COALESCE(pso.sort_order, 999)` fatals)

Surface fatal in scan is **not** that `COALESCE` is unknown. It is:

```
FATAL: Source Table not found for Column 'sort_order' at (l:25 c:77). No alias or table called 'pso'.
(QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE, ast-walk)
```

on:

```sql
, CAST(COALESCE(pso.sort_order, 999) AS VARCHAR(255)) AS product_sort_order
```

The **root** parse error is one line later:

```
WARNING RECOVER_INLINE @31:0: Invalid syntax near '('
```

that `(` opens the **derived column list** on a Snowflake `VALUES` constructor:

```sql
FROM VALUES ('ae6ed46df2dfe2d7a86be2824ea42503','Unknown','Unknown','Unknown',NULL,NULL) AS default_stream
(stream_key,product,sub_product,sub_product_division,channel,fiscal_year)
LEFT JOIN {{ ref('sort_orders') }} AS pso
ON pso.sort_field_value = default_stream.product
    AND pso.sort_field_name = 'product_sort_order'
```

Because the grammar does not accept `AS default_stream (col, …)`, recovery drops the rest of that `FROM` clause. `LEFT JOIN … AS pso` never enters the symbol table, so `pso.sort_order` inside `COALESCE` is reported as “no alias `pso`”. The same recovery historically also lost `default_stream` itself (`No alias or table called 'default_stream'`).

`COALESCE` / `CAST` here are ordinary; they must work once `pso` is a visible join alias. The first UNION branch already does `COALESCE(pso.sort_order, 999)` against a normal table and is not the problem.

#### Reproducing query (UNION ALL second branch)

```sql
SELECT CAST(default_stream.stream_key AS VARCHAR(255))                    AS stream_key
     , CAST(default_stream.product AS VARCHAR(255))                       AS product
     , CAST(COALESCE(pso.sort_order, 999) AS VARCHAR(255))                AS product_sort_order
     , CAST(default_stream.sub_product AS VARCHAR(255))                   AS sub_product
     , CAST(default_stream.sub_product_division AS VARCHAR(255))          AS sub_product_division
     , CAST(default_stream.channel AS VARCHAR(255))                       AS channel
     , CAST(default_stream.fiscal_year AS VARCHAR(255)) AS campaign_fiscal_year_date
FROM VALUES ('ae6ed46df2dfe2d7a86be2824ea42503','Unknown','Unknown','Unknown',NULL,NULL) AS default_stream
(stream_key,product,sub_product,sub_product_division,channel,fiscal_year)
LEFT JOIN {{ ref('sort_orders') }} AS pso
ON pso.sort_field_value = default_stream.product
    AND pso.sort_field_name = 'product_sort_order'
```

#### Expected behavior

1. Parse `FROM VALUES (row[, row…]) AS <alias> (<col>[, <col>…])` as a table source whose interface is those column names (including `NULL` cells).
2. `LEFT JOIN … AS pso` after that `VALUES` is a normal join; `pso` is in scope for SELECT / ON / `COALESCE`.
3. `COALESCE(pso.sort_order, 999)` resolves `pso.sort_order` like any other qualified column; literal `999` is not a column.
4. `default_stream.product` (and the other VALUES columns) resolve against the derived column list, not against `pso`.
5. As a UNION ALL branch, this SELECT’s seven-column interface must match the first branch (already explicit) — no false `SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH` once both parse.
6. Same `VALUES … AS alias (cols)` works **outside** UNION (standalone SELECT / INSERT SELECT / CTE).

#### Suggested tests

Happy path (no FATAL / no recovery), plus goldens for the six extractor objects where that is the project convention:

| Case | Result |
|------|--------|
| Exact reproducing query (VALUES + column list + LEFT JOIN + `COALESCE(pso.sort_order, 999)`) | `pso` and `default_stream` both in scope; no `QUALIFIED_COLUMN_NOT_FOUND` |
| Same `VALUES` constructor as the only FROM (no JOIN) | `default_stream.stream_key` etc. resolve |
| Multi-row `VALUES (…), (…)` `AS t (a, b)` | Interface `(a, b)` |
| `COALESCE(pso.sort_order, 999)` when `pso` is a real table (same query, table-based `FROM` instead of `VALUES`) | Still resolves (no regression) |
| Unresolved: `COALESCE(missing.sort_order, 999)` with no `missing` alias | Still `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE` |
| Unresolved VALUES col: `default_stream.not_a_col` | Column-not-found on `default_stream`, not a parse fail on `(` |
| `INSERT … SELECT` / CTE wrapping the same `VALUES AS alias (cols)` | Permitted sites parse |

#### Acceptance

- **Done:** `values_statement` production accepts unparenthesized `VALUES values_matrix` in addition to `(VALUES values_matrix)` at every `values_statement_primary` site (FROM/JOIN, CTE body, script DML, VALUES endpoint). No new rule targets; event walker unchanged.
- **Done:** `SqlEventWalkerUnparenthesizedValuesTests` — one golden test per site (FROM, CTE, script DML, VALUES endpoint).
- **Done:** VALUES regression suite clean (parenthesized `(VALUES …)` unchanged). User corrected unrelated `table_alias` golden ordering in `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests`.
- The reproducing query no longer reports invalid syntax near `(` on the VALUES column list; JOIN/`COALESCE` resolve once the FROM clause parses (recovery cascade eliminated).
- 2.1 wildcard behavior unchanged. Phase 1 / 3–5 unchanged.

#### Out of scope (2.2)

- Rewriting the model to a CTE/`SELECT … FROM (SELECT …) default_stream` instead of `VALUES`.
- Jinja `{{ ref('sort_orders') }}` (treat as a table name for parse tests).
- Unrelated `%` fatals on other models.

---

### 2.3 — `WITHIN GROUP` ordered aggregates (`LISTAGG` and peers)

**Kind:** Enhancement (standalone grammar: `WITHIN GROUP (ORDER BY …)` after an aggregate, optional `OVER`)

**Status:** **Complete** (2026-08-15)

**Component:** grammar — `WITHIN GROUP` is currently rejected (`Invalid syntax near 'GROUP'` / unexpected `GROUP` after `WITHIN`). Share this production with Phase 5.3 `ARRAY_AGG … WITHIN GROUP` rather than teaching `GROUP` only on `ARRAY_AGG`.

#### Problem

Snowflake/Oracle-style ordered aggregates use:

```text
<agg>( … ) WITHIN GROUP ( ORDER BY <expr> [ ASC | DESC ] [ NULLS FIRST | LAST ] [, ...] )
         [ OVER ( [ PARTITION BY … ] [ ORDER BY … ] [ <window_frame> ] ) ]
```

The parser accepts `WITHIN` then fails on **`GROUP`**. That blocks `LISTAGG` (this starter) and every other aggregate that takes the same extension (`ARRAY_AGG`, `PERCENTILE_CONT` / `PERCENTILE_DISC`, others documented for Snowflake).

#### Starter query

```sql
SELECT contact_key, eab_entry_year_academic, eab_entry_term, program,
        LISTAGG(regexp_replace(program, ' ') || '[' || eab_entry_term || '' || eab_entry_year_academic || ']'
                || '{' || enrollment_status || '}', '||')
        WITHIN GROUP (ORDER BY funnel_priority, sort_order asc) OVER (partition by contact_key) as fun_program_agg
     FROM all_funnel_status_sort
```

A second `LISTAGG … WITHIN GROUP … OVER` in the same CTE family (`cur_program_agg`) should parse with the same production. `ARRAY_AGG(…) WITHIN GROUP (ORDER BY …)` (Phase 5.3) must share this `WITHIN GROUP` grammar — fix the **shared** production, then test each supporting aggregate.

#### Expected behavior

1. `WITHIN GROUP (ORDER BY …)` is legal immediately after an ordered aggregate call. `GROUP` is the second keyword of that clause, not a `GROUP BY`.
2. Optional `OVER (PARTITION BY …)` after `WITHIN GROUP` (starter query). Also `WITHIN GROUP` without `OVER` (plain `GROUP BY` query).
3. `ORDER BY` inside `WITHIN GROUP` supports multiple keys, `ASC`/`DESC`, `NULLS FIRST`/`LAST`.
4. `LISTAGG` args: expression, optional delimiter (`'||'`), optional `DISTINCT`.
5. Column refs in the agg expr, delimiter (if a column), `WITHIN GROUP` sort keys, and `OVER` partition/order keys all resolve (qualified / unqualified / unresolved / ambiguous as usual).
6. **Every Snowflake aggregate that documents `WITHIN GROUP`** gets at least one happy-path test (not only `LISTAGG`). Confirm the current Snowflake list; include at least:
   - `LISTAGG` (starter: with delimiter, `WITHIN GROUP`, `OVER`)
   - `LISTAGG` with `WITHIN GROUP` and no `OVER`
   - `ARRAY_AGG` / `ARRAYAGG` with `WITHIN GROUP` (aligns with 5.3)
   - `PERCENTILE_CONT` / `PERCENTILE_DISC` if they use `WITHIN GROUP (ORDER BY …)`
   - any other documented peer found while implementing
7. Six extractor goldens on the starter `LISTAGG` query.

#### Suggested tests

| Case | Result |
|------|--------|
| Exact starter `LISTAGG(…, '\|\|') WITHIN GROUP (ORDER BY funnel_priority, sort_order ASC) OVER (PARTITION BY contact_key)` | Parse; `fun_program_agg` in interface |
| `LISTAGG(col) WITHIN GROUP (ORDER BY col)` with `GROUP BY` (no `OVER`) | Parse |
| `LISTAGG(DISTINCT col, ',') WITHIN GROUP (ORDER BY col)` | Parse if Snowflake allows |
| `ARRAY_AGG(x) WITHIN GROUP (ORDER BY x NULLS LAST)` | Parse (shared production; 5.3 owns ARRAY-specific extras) |
| `PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY x)` (if in dialect) | Parse |
| Unresolved sort key in `WITHIN GROUP (ORDER BY missing)` | Column-not-found, not unexpected `GROUP` |
| Negative: `WITHIN GROUP` on an aggregate that Snowflake does not allow | Optional; only if the grammar should reject |

#### Acceptance

- **Done:** Starter `LISTAGG … WITHIN GROUP … OVER` no longer fatals on `GROUP`.
- **Done:** `ordered_aggregate_expression` grammar + walker buckets (`within_group_ordered_by`, `window_partition_by` when `OVER` present).
- **Done:** Tests in `SqlEventWalkerWithinGroupOrderedAggregateTests` — SELECT clauses, JOIN ON, UPDATE/INSERT/DELETE, prior select-list alias in `WITHIN GROUP` / `PARTITION BY`.
- 2.1 / 2.2 unchanged. 5.3 reuses this `WITHIN GROUP` production.
- **Deferred:** `ARRAY_AGG` / `PERCENTILE_CONT` peer happy-path tests (grammar production is shared; add when 5.3 needs them).

#### Out of scope (2.3)

- Interpreting `regexp_replace` / `CHARINDEX` (only needed so the starter parses).
- `{% %}` Jinja (Phase 6–7).
- ARRAY-only functions beyond sharing `WITHIN GROUP` (Phase 5).

---

### 2.4 — Nested searched `CASE` (and inner string/array predicands)

**Kind:** Enhancement (standalone: `CASE` as a result of `CASE`; plus Snowflake `SPLIT`/`SPLIT_PART`/`POSITION` used in the starter)

**Status:** **Complete** (2026-08-15) — blocked on Phase 2.6 `expr[n]`; nested `CASE` grammar was already present

**Component:** grammar for searched `CASE` nesting; predicands inside `WHEN`/`THEN`/`ELSE` (including `expr[0]` — share with 5.4 if that production exists)

#### Problem

Searched `CASE` (`CASE WHEN pred THEN expr … ELSE expr END`) must allow another full `CASE … END` as the `THEN` or `ELSE` result. The starter is that shape: outer `WHEN POSITION(…) > 0 THEN CASE … END ELSE CASE … END`.

The starter query below currently dies **inside** the first inner `WHEN`, at `SPLIT(…)[0]`:

- FATAL unexpected `'0'` @24:94 (`SPLIT(…)[0]`)
- Then unexpected `'WHEN'` @22:4 (recovery cascade on the outer `CASE`)

So this subtask is two layers:

1. **Nested searched `CASE`:** `THEN`/`ELSE` may be a `CASE` expression (not only a scalar). Outer `WHEN`/`THEN`/`ELSE`/`END` pairing must survive nested `END`s.
2. **Inner predicands this query needs** so the starter actually parses: `POSITION(substr, str)`, `SPLIT(str, delim)[n]`, `SPLIT_PART(str, delim, n)` with **negative** `n` (`-1` = last part), `REPLACE`, `LOWER`, `TRIM`, comparison to `LOWER('…')`.

If `expr[n]` is implemented in 5.4, reuse it; do not wait on the rest of Phase 5 ARRAY work. `SPLIT_PART(…, -1)` is Snowflake, not a CASE issue.

#### Starter query

```sql
CASE
    WHEN POSITION('p={', ACA.title) > 0 THEN
        CASE
            WHEN LOWER(REPLACE(SPLIT(SPLIT_PART(SPLIT_PART(ACA.title, 'f={',2), '}', 1), '|')[0], '"', '')) = LOWER('Email')
                THEN 'EAB Web form'
            WHEN LOWER(REPLACE(SPLIT(SPLIT_PART(SPLIT_PART(ACA.title, 'f={',2), '}', 1), '|')[0], '"', '')) = LOWER('Facebook')
                THEN 'Facebook'
            WHEN LOWER(REPLACE(SPLIT(SPLIT_PART(SPLIT_PART(ACA.title, 'f={',2), '}', 1), '|')[0], '"', '')) = LOWER('LinkedIn')
                THEN 'LinkedIn'
            WHEN LOWER(REPLACE(SPLIT(SPLIT_PART(SPLIT_PART(ACA.title, 'f={',2), '}', 1), '|')[0], '"', '')) = LOWER('Paid Search')
                THEN 'Paid Search'
            WHEN LOWER(REPLACE(SPLIT(SPLIT_PART(SPLIT_PART(ACA.title, 'f={',2), '}', 1), '|')[0], '"', '')) = LOWER('.EDU Web Form')
                THEN '.EDU Web form'
            ELSE 'EAB Web form'
        END
    ELSE
        CASE
            WHEN LOWER(TRIM(SPLIT_PART(ACA.title, ';', -1))) = LOWER('Email')
                THEN 'EAB Web form'
            WHEN LOWER(TRIM(SPLIT_PART(ACA.title, ';', -1))) = LOWER('Facebook')
                THEN 'Facebook'
            WHEN LOWER(TRIM(SPLIT_PART(ACA.title, ';', -1))) = LOWER('LinkedIn')
                THEN 'LinkedIn'
            WHEN LOWER(TRIM(SPLIT_PART(ACA.title, ';', -1))) = LOWER('Paid Search')
                THEN 'Paid Search'
            WHEN LOWER(TRIM(SPLIT_PART(ACA.title, ';', -1))) = LOWER('.EDU Web Form')
                THEN '.EDU Web form'
            ELSE 'EAB Web form'
        END
END AS source_type,
```

#### Expected behavior

1. Nested searched `CASE` in `THEN` and in `ELSE` parses; each `END` binds to the innermost open `CASE`.
2. Simple (value) `CASE expr WHEN …` still works (no regression); nesting of mixed searched/simple `CASE` if Snowflake allows.
3. `WHEN` predicands and `THEN`/`ELSE` results are ordinary predicands: function calls, `||`, comparisons, subscripts.
4. `ACA.title` (and other qualified columns) resolve; unresolved `ACA.missing` still column-not-found, not unexpected `WHEN`.
5. `SPLIT(…)[0]` is a subscript on the `SPLIT` result (array); `SPLIT_PART(col, ';', -1)` accepts a negative index.
6. Locations: SELECT list (starter), `WHERE`/`ON`/`HAVING`/`UPDATE SET` at least one nested `CASE` each.
7. Six extractor goldens on the starter (or a self-contained `SELECT <starter> FROM acs__categories ACA`).

#### Suggested tests

| Case | Result |
|------|--------|
| Exact starter nested `CASE` | Parse; `source_type` in interface |
| Nested `CASE` with only `WHEN 1=1 THEN CASE WHEN 1=1 THEN 'a' ELSE 'b' END ELSE 'c' END` | Parse (isolates nesting from `SPLIT`) |
| `SPLIT(x, '\|')[0]` inside `WHEN` | Parse (or shared 5.4) |
| `SPLIT_PART(x, ';', -1)` | Parse |
| `POSITION('p={', col) > 0` | Parse |
| Unresolved `ACA.title` | Column diagnostic, not `WHEN`/`0` surprise |
| Simple `CASE col WHEN 'Email' THEN … END` | No regression |

#### Acceptance

- **Done:** Starter no longer fatals on `'0'` / `'WHEN'` once 2.6 `SPLIT(…)[n]` landed.
- **Done:** `SqlEventWalkerArraySubscriptTests` — workplan nested `source_type` starter, isolation nested `CASE`, `SPLIT_PART(…, -1)` in `WHEN`; each with six extractor goldens (AST, interface, substitutions, table dictionary, query dictionary, symbol table).
- **Done:** Placement coverage — nested searched `CASE` in `WHERE`, `JOIN ON` (qualified `t.col` in inner branch), `HAVING`, and `UPDATE SET` (`nestedSearchedCaseIn*PlacementTest`); same six-extractor assertions.
- 2.1–2.3 unchanged. Shared `expr[n]` from 2.6 / 5.4.

#### Out of scope (2.4)

- Full ARRAY function family (Phase 5) beyond `SPLIT(…)[n]` needed here.
- Jinja (Phase 6–7).
- Rewriting the model to flatten nested `CASE`.

---

### 2.5 — Parse hang / `PARSE_TIMEOUT` on multi-CTE email DNC rollup

**Kind:** Defect (non-terminating parse or pathological work — investigate root cause, then fix)

**Status:** **Complete** (2026-08-15, SQL guardrail) — table-stubbed exemplar parses on current tree (`dncEmailRollupMultiCteExemplarV0Test`). **Jinja-authored fixture** (starter query below, with `{{ ref }}` / `{{ source }}`) is retained in this section; parse/timeout validation after Jinja tuple support lands → **Phase 6.4**. Phase **7** not required for this query (no `{% %}` control flow in the body).

**Component:** parse strategy / grammar / walker — **unknown until bisected**; do not assume a single construct

**Start here — reproduce and bisect.** The query below is valid Snowflake/dbt SQL as authored. RMCP scan reports `EXEC_TIMEOUT: PARSE_TIMEOUT` (parser runs until timeout, not a clean syntax error). Goal: find which construct(s) cause non-termination or unbounded work, fix termination, and keep parse time bounded on the full query.

#### Observed failure

```
EXEC_TIMEOUT: PARSE_TIMEOUT
```

(on the full query below; no reliable line/column for the hang)

#### Starter query (full text — use as primary fixture)

```sql
WITH email_common_format AS (
 SELECT  con.email AS email_address,
         TRUE AS suppress_email_ind,
         dnc.comments AS suppress_email_reason,
         dnc.dnc_date_added AS dnc_added_dt,
         rit.intake_type_label,
         con.acs_replication_date AS intake_dt

FROM {{ ref ('acs__contacts')}} AS con
INNER JOIN {{ ref ('prc__contact_acquia_xwalk') }} AS xwalk
        ON con.acs_contact_id = xwalk.acs_contact_id
INNER JOIN  {{ ref ('prc__acs_contacts_dnc') }} AS dnc
        ON dnc.acs_contact_id = con.acs_contact_id
       AND lower(dnc.channel) = 'email'
       AND email_address IS NOT NULL
    INNER JOIN {{ source('COMMON', 'ref__intake_types_alr')}} AS rit
            ON LOWER(intake_type_label) = 'acs'

UNION ALL

SELECT
         email_address,
         suppress_email_ind,
         IFF(pcf.suppress_email_ind = 'TRUE',COALESCE(pcf.suppress_email_reason, CONCAT('PDP-Sourced-',COALESCE(pcf.intake_type_label,''))),pcf.suppress_email_reason) as suppress_email_reason,
         NULL as  dnc_added_dt,
         intake_type_label,
         intake_dt
FROM {{ ref ('prc__srccon_contact_common_format') }}  AS pcf
WHERE email_address IS NOT NULL
) ,
--CTE to determine how many of the comments have the pre-existing comment of 'PDP-Sourced' and rollup valid_ind
aggregation_check AS (
     SELECT
       COUNT(CASE WHEN lower(ecf.intake_type_label) = 'acs' AND ecf.suppress_email_ind = 'TRUE' AND
      (ecf.suppress_email_reason ILIKE 'PDP-Sourced%' OR ecf.suppress_email_reason in ('PDP', 'None')) THEN 1 END) AS acquia_dnc_pdp_sourced,
      COUNT(CASE WHEN lower(ecf.intake_type_label) = 'acs' AND ecf.suppress_email_ind = 'TRUE' AND
      (ecf.suppress_email_reason ilike 'Matched to Down-Funnel Applicant%') THEN 1 END) AS
      acquia_dnc_ddm_sourced,
      COUNT(CASE WHEN suppress_email_ind = 'TRUE' then 1 end) AS all_dncs,
      all_dncs = acquia_dnc_ddm_sourced AS acquia_dnc_ddm_only, -- DDM is the only DNC for this email
      all_dncs = acquia_dnc_pdp_sourced AS acquia_dnc_pdp_only,-- PDP is the only DNC for this email
       lower( email_address) as email_address
    FROM email_common_format ecf
    GROUP BY lower(email_address)
),
dnc_prioritization AS (
SELECT   ecf.email_address
        ,CASE
                WHEN suppress_email_ind = 'TRUE' AND acquia_dnc_pdp_only THEN NULL
                ELSE suppress_email_ind
        END AS suppress_email_ind_calc
        ,CASE
                WHEN suppress_email_ind = 'TRUE' AND acquia_dnc_pdp_only THEN NULL
                ELSE suppress_email_reason
        END AS dnc_comment
        ,CASE
                WHEN suppress_email_ind = 'TRUE' AND acquia_dnc_pdp_only THEN NULL
                ELSE dnc_added_dt
        END AS dnc_added_dt
        ,intake_dt
        ,CASE
                WHEN suppress_email_ind_calc = 'TRUE' THEN 1
                WHEN suppress_email_ind_calc = 'FALSE' THEN 2
                WHEN suppress_email_ind_calc IS NULL THEN 3
        END AS suppression_priority
        ,CASE WHEN
               dnc_comment ILIKE 'PDP-Sourced%' OR dnc_comment in ('PDP', 'None') THEN 2
               WHEN dnc_comment ilike 'Matched to Down-Funnel Applicant%' THEN 3
               WHEN dnc_comment IS NULL THEN 4
               ELSE 1
               END AS comment_priority
FROM email_common_format as ecf
INNER JOIN aggregation_check AS ag
        ON lower(ecf.email_address) = lower(ag.email_address)
QUALIFY ROW_NUMBER() OVER (PARTITION BY lower(ecf.email_address) ORDER BY suppression_priority ASC NULLS LAST, comment_priority ASC NULLS LAST, dnc_added_dt ASC NULLS LAST,ecf.intake_dt ASC NULLS LAST) = 1
)

SELECT
        pr.email_address
        ,suppress_email_ind_calc AS suppress_email_ind
        ,CASE WHEN suppress_email_ind_calc = 'TRUE' THEN COALESCE(dnc_comment,'PDP-Sourced') END AS dnc_email_comment
        ,CASE WHEN suppress_email_ind_calc IS NOT NULL THEN COALESCE(dnc_added_dt, intake_dt, CURRENT_TIMESTAMP) END AS dnc_email_dt
        ,CASE WHEN pr.suppress_email_ind_calc = 'TRUE'
             THEN ( CASE ---- Categorize DNC based on DNC Comment.
             WHEN pr.dnc_comment ILIKE 'PDP-Sourced-partner' THEN 'Partner'
             WHEN (COALESCE(pr.dnc_comment,'PDP-Sourced') ilike 'PDP-Sourced%' OR
             LOWER(pr.dnc_comment) IN ('pdp-sourced: default opt-out', 'pdp-default', 'pdp', 'none')) THEN 'EAB Other'
             WHEN pr.dnc_comment ilike 'Matched to Down-Funnel Applicant%' ----— Data Duplication Management ---(DDM)
             THEN 'EAB Matching'
             WHEN (pr.dnc_comment ilike 'user%' OR LOWER(pr.dnc_comment) = 'unsubscribed') THEN 'Student'
             ELSE 'Other' -----— Including NULL
             END)
             ELSE NULL
             END AS suppress_email_category
FROM dnc_prioritization AS pr
```

#### Constructs to investigate (bisect — do not assume one is the culprit)

| Area | Examples in this query |
|------|-------------------------|
| CTE + `UNION ALL` | `email_common_format` first branch joins + second branch `SELECT` |
| Same-SELECT alias forward refs | `all_dncs = acquia_dnc_ddm_sourced`, `suppress_email_ind_calc` referenced in later expressions in the same SELECT |
| `QUALIFY` + window | `ROW_NUMBER() OVER (… ORDER BY suppression_priority, comment_priority, …) = 1` |
| Nested `CASE` | Outer categorization `CASE` wrapping inner `CASE`; multiple `CASE` in `dnc_prioritization` |
| `IFF`, `ILIKE`, `CONCAT`, `COALESCE` | Throughout branches |
| Jinja substitutions | `{{ ref ('…')}}` with spaces inside parens; `{{ source('COMMON', …)}}` — stub as table names for isolated parser tests |

#### Suggested approach

1. **Reproduce** with `SCRIPT` or `SQL` endpoint and a parse timeout; confirm `PARSE_TIMEOUT` on the full fixture.
2. **Bisect** by commenting out CTEs / SELECT branches / expressions until parse completes; re-enable one construct at a time.
3. **Minimal repro** — smallest sub-query that still hangs gets its own golden fixture.
4. **Fix** non-termination or exponential blow-up in the identified path (lexer, parser, recovery, or walker).
5. **Guardrail** — bounded parse work or cycle detection so pathological input fails fast with a diagnostic instead of hanging.
6. For Jinja in fixtures, treat `{{ ref('…') }}` / `{{ source('…') }}` as tuple substitutions (Phase 6) or plain table identifiers so the hang is not masked by unrelated Jinja fatals.

#### Expected behavior

1. Full starter query parses within normal time limits (no `PARSE_TIMEOUT`).
2. No infinite loop / unbounded recursion in parse or early walk.
3. After parse: CTEs `email_common_format`, `aggregation_check`, `dnc_prioritization` appear in scope; outer SELECT columns resolve or produce ordinary column-not-found diagnostics — not hang.
4. Same-SELECT alias forward references (`suppress_email_ind_calc` in a later `CASE` in the same select list) behave per Snowflake semantics (document chosen behavior if PSS models these differently).
5. `QUALIFY` + `ROW_NUMBER() OVER (…)` parses (grammar may be separate from hang fix).

#### Suggested tests

| Case | Result |
|------|--------|
| Full starter query | Completes parse; no `PARSE_TIMEOUT` |
| `email_common_format` CTE only | Completes |
| `aggregation_check` only (with stub CTE body) | Completes; alias forward refs `all_dncs = acquia_dnc_ddm_sourced` parse |
| `dnc_prioritization` with `QUALIFY` + window ORDER BY same-SELECT aliases | Completes |
| Outer SELECT nested `CASE` categorization block alone | Completes |
| Minimal repro from bisect | Documented fixture; completes after fix |

#### Acceptance

- **Done:** Full starter exemplar (table-stubbed `ref`/`source` names) completes parse in ~1s; no `PARSE_TIMEOUT`.
- **Done:** `SqlEventWalkerSubqueriesAndClauseSemanticsTests.dncEmailRollupMultiCteExemplarV0Test` — CTE chain, `UNION ALL`, same-SELECT alias forward refs, `QUALIFY` + `ROW_NUMBER()`, nested categorization `CASE`.
- **Pending (Phase 6.4):** Parse the **Jinja-authored** starter query below (spaces inside `{{ ref (…) }}`, `{{ source('COMMON', …) }}`) without `PARSE_TIMEOUT`. Fixture text: this section only (not in Panto CSV).
- 2.1–2.4 unchanged.

#### Out of scope (2.5)

- Rewriting the query to remove `QUALIFY`, flatten `CASE`, or eliminate alias forward refs (unless needed only for a **temporary** bisect).
- Full Jinja interpretation (Phase 6–7) except stubbing refs for parse tests.
- Semantic validation of DNC business rules.

---

### 2.6 — Flat searched `CASE` for `product` (`POSITION` + nested `SPLIT`/`SPLIT_PART`)

**Kind:** Enhancement (standalone: searched `CASE` with string-parse predicands in `WHEN`/`THEN`; shares `SPLIT(…)[n]` with 2.4 / 5.4)

**Status:** **Complete** (2026-08-15)

**Component:** grammar for `SPLIT` result subscript `expr[n]`; predicands `POSITION`, `SPLIT_PART`, `REPLACE`, `NULLIF`, `TRIM`, `COALESCE` inside `CASE` branches; bracketed-identifier vs subscript disambiguation

**Start here — confirm SQL is valid before changing grammar.** Snowflake accepts this shape. The parser currently dies on the **array subscript** in the first `THEN` branch, not on `CASE` nesting (contrast **2.4**).

#### Problem

A single searched `CASE` chooses a `product` value from a category title column (`cat.title` or `ctg.title` — same logic, different alias) using two `POSITION`-gated branches:

1. When `p={` appears in the title: prefer `prod_abbr.eab_std_value` from a join, else parse the title with nested `SPLIT_PART` / `SPLIT` / `REPLACE`, take element `[0]`, wrap in `NULLIF(TRIM(…), '')`.
2. When `;` appears: `NULLIF(TRIM(SPLIT_PART(title, ';', 1)), '')`.
3. Else `NULL`.

The starter fails at `SPLIT(…)[0]`:

```
FATAL: unexpected input: '0' at (l:2 c:127)  — token after '[' in SPLIT(...)[0]
WARNING RECOVER_INLINE: Invalid syntax near 'WHEN'  (recovery cascade on the outer CASE)
FATAL: Exception when walking the parse tree: Cannot invoke "java.util.Map.remove(Object)" because "subMap" is null
```

Same root class as **2.4** (`SPLIT(…)[0]` inside a `CASE` branch), but this fixture is **not** nested `CASE` — it tests the flat `WHEN`/`THEN`/`ELSE` form plus `NULLIF`/`COALESCE(prod_abbr.col, …)` with a join alias. Two author variants below (`cat` vs `ctg` alias, with/without leading comment and line-broken `COALESCE`) must both parse.

#### Fixture 1 — `cat.title` (compact `COALESCE`)

```sql
CASE WHEN POSITION('p={', cat.title) > 0
       THEN NULLIF(TRIM(COALESCE(prod_abbr.eab_std_value, REPLACE(SPLIT(SPLIT_PART(SPLIT_PART(cat.title, 'p={',2),'}',1), '|')[0], '"', ''))),'')
       WHEN POSITION(';', cat.title) > 0
       THEN NULLIF(TRIM(SPLIT_PART(cat.title,';',1)),'')
       ELSE NULL
    END AS product,
```

#### Fixture 2 — `ctg.title` (comment + line-broken `COALESCE`)

```sql
   -- Product extraction (handles both formats)
  CASE WHEN POSITION('p={', ctg.title) > 0
       THEN NULLIF(TRIM(COALESCE(prod_abbr.eab_std_value, 
       REPLACE(SPLIT(SPLIT_PART(SPLIT_PART(ctg.title, 'p={',2),'}',1), '|')[0], '"', ''))),'')
       WHEN POSITION(';', ctg.title) > 0
       THEN NULLIF(TRIM(SPLIT_PART(ctg.title,';',1)),'')
       ELSE NULL
  END AS product
```

#### Minimal parse wrapper (fixture 1 — substitute `ctg` for fixture 2)

```sql
SELECT
    CASE WHEN POSITION('p={', cat.title) > 0
           THEN NULLIF(TRIM(COALESCE(prod_abbr.eab_std_value, REPLACE(SPLIT(SPLIT_PART(SPLIT_PART(cat.title, 'p={',2),'}',1), '|')[0], '"', ''))),'')
           WHEN POSITION(';', cat.title) > 0
           THEN NULLIF(TRIM(SPLIT_PART(cat.title,';',1)),'')
           ELSE NULL
        END AS product
FROM acs__categories AS cat
LEFT JOIN ref__standard_value_mapping_alr AS prod_abbr
    ON prod_abbr.client_value = REPLACE(SPLIT(SPLIT_PART(SPLIT_PART(cat.title, 'p={',2),'}',1), '|')[0], '"', '')
   AND prod_abbr.field_name = 'product'
```

(Substitute any table aliases for parse tests; `prod_abbr` must be in scope for `prod_abbr.eab_std_value` to resolve.)

#### Expected behavior

1. Both fixtures parse with no FATAL / no recovery on `'0'` or `'WHEN'`.
2. `SPLIT(SPLIT_PART(SPLIT_PART(<alias>.title, 'p={', 2), '}', 1), '|')[0]` is a subscript on the `SPLIT` array result; `[1]` sibling index should also work (same pattern drives `sub_product` in related expressions).
3. `POSITION('p={', <alias>.title) > 0` and `POSITION(';', <alias>.title) > 0` are valid `WHEN` predicands.
4. `NULLIF(TRIM(expr), '')`, `COALESCE(prod_abbr.eab_std_value, expr)` (including when `COALESCE` args break across lines), and `REPLACE(…, '"', '')` compose inside `THEN` without parse errors.
5. `cat.title` / `ctg.title` and `prod_abbr.eab_std_value` resolve when sources are in scope; unresolved aliases still column-not-found, not subscript/`WHEN` surprises.
6. Output interface includes `product`.
7. Six extractor goldens on the minimal `SELECT` fixture.

#### Relationship to other phases

| Phase | Overlap |
|-------|---------|
| **2.4** | Also needs `SPLIT(…)[n]` and `SPLIT_PART`; 2.4 adds **nested** `CASE` inside `THEN`/`ELSE` |
| **5.4** | Owns general `arr[n]` / array-literal subscript production; **reuse** here — do not block 2.6 on all of Phase 5 |
| **5.11** | `SPLIT(string, delim)` as array producer feeds the subscript |

Implement `expr[n]` once; both 2.4 and 2.6 must pass with the same production.

#### Suggested tests

| Case | Result |
|------|--------|
| Fixture 1 (`cat.title`, compact `COALESCE`) | Parse; `product` in interface |
| Fixture 2 (`ctg.title`, comment + line-broken `COALESCE`) | Parse; `product` in interface |
| Minimal `SELECT` + `FROM cat LEFT JOIN prod_abbr` wrapper | Parse; `cat.title` and `prod_abbr.eab_std_value` resolve |
| Same wrapper with fixture 2 (`ctg` alias) | Parse |
| First branch only: `CASE WHEN POSITION('p={', cat.title) > 0 THEN NULLIF(TRIM(REPLACE(SPLIT(SPLIT_PART(SPLIT_PART(cat.title, 'p={',2),'}',1), '|')[0], '"', '')),'') ELSE NULL END` | Parse (isolates subscript without `COALESCE`/join) |
| Second branch only: `CASE WHEN POSITION(';', cat.title) > 0 THEN NULLIF(TRIM(SPLIT_PART(cat.title,';',1)),'') ELSE NULL END` | Parse |
| `SPLIT(x, '|')[1]` in `THEN` | Parse (sibling index) |
| `COALESCE(prod_abbr.eab_std_value, 'literal')` when `prod_abbr` missing | `QUALIFIED_COLUMN_NOT_FOUND`, not `[0]` fatal |
| Nested `CASE` regression from **2.4** | Unchanged |

#### Acceptance

- **Done:** Postfix `arraySubscriptSuffix` on `value_expression_primary_core` with `subscript_index` (`column_reference | value_expression`); AST `{subscript={array, index}}` via `exitArraySubscriptSuffix` / `exitSubscript_index`.
- **Done:** Bracketed logical identifiers disambiguated from subscript brackets — lexer `Bracket_Identifier` requires space or hyphen inside brackets; dotted single-token names (`[Entity]`, `[schema.table]`) use `logical_identifier` grammar path (`bracket_identifier_body` with `Identifier | DOT`); `exitLogical_identifier` promotes full bracketed text.
- **Done:** `SqlEventWalkerArraySubscriptTests` — literal indices (`[0]`, `[1]`), column arg + literal index, bare column index (`[idx]`), qualified column index (`[tab1.b]`), parenthesized index, and product `CASE` fixtures v3–v6 (flat branches, full cat/ctg fixtures with JOIN).
- **Done:** Placement coverage — compact product `CASE` (`POSITION` + `SPLIT_PART`) in `WHERE`, `HAVING`, and `UPDATE SET` (`productCaseIn*PlacementTest`); six extractor goldens each.
- **Done:** `SqlEventWalkerBracketedIdentifierTests` — `[Entity]` / `[Result]` / `t.[Metric]` case-preserved in `table_dictionary`.
- **Done:** Full parse module suite clean (2037 tests, 0 failures).
- Both fixtures no longer fatal on `'0'` at `SPLIT(…)[0]` or recovery-cascade `'WHEN'`.
- Minimal `SELECT` wrapper parses with join alias `prod_abbr` in scope for each title alias (`cat` / `ctg`).
- Shared `expr[n]` production reusable by 2.4 and 5.4.

#### Out of scope (2.6)

- `sub_product` / `marketing_audience_raw` sibling expressions (same file family; covered by the same `SPLIT`/`SPLIT_PART` production once this fixture passes).
- `REGEXP_REPLACE` in audience branch (only needed if a separate fixture fails).
- Jinja `{{ ref/source }}` on join tables (stub aliases for parse tests; Phase 6).
- Rewriting to avoid `[0]` subscript.

---

### 2.7 — WITH CTE physical-source and tuple-substitution finalization

**Kind:** Defect (trailing-clause physical / tuple-substitution collection in global `tableDictionary`) — **closed**

**Status:** **Complete** (2026-09-01)

**Component:** WITH / query-finalizer walk; global `tableDictionary` for **physical and tuple substitution sources** only

**Canonical spec:** [phase-2.7-with-conditionless-join-finalizer.md](../docs/rmcp-handoff/5.1.3-panto-outstanding/phase-2.7-with-conditionless-join-finalizer.md)

**Problem (summary):** Live Panto query with `CROSS JOIN last_delivered_cte` appeared to lose tuple-substitution evidence when `<Contact Deleted Dt>` appeared only in a trailing `WHERE CASE`. Characterization in `SqlEventWalkerWithCteTupleSubstitutionTests` shows physical/tuple columns are collected correctly across join shapes and final clauses.

**5.1.3 policy (closure decision):** **CTE aliases must never appear in global `tableDictionary`.** CTE row sources are documented in `query_dictionary` and `def_*` symbol-table structures (`context_list`, `table_alias`, `interface`, `filters`). Restoring 5.0.0-3-style CTE keys in the physical dictionary is obsolete and was **not** pursued.

**Delivered:**

1. ~~Add characterization tests with six extractor goldens.~~ Done — `SqlEventWalkerWithCteTupleSubstitutionTests` (50 tests).
2. ~~Confirm trailing-clause tuple / physical columns on global physical/tuple keys.~~ Met across join and clause variants.
3. ~~Adjudicate CTE keys missing from global `tableDictionary` vs 5.0.0-3.~~ Intentional under 5.1.3; not a defect.

**Re-routed:** Live Panto clusters A–E and any residual functional source loss → **2.9**. Nested-WITH wrapper template → optional future characterization, not a 2.7 blocker.

**Intentionally unsupported (2026-09-01):** `NATURAL FULL OUTER JOIN` (and `NATURAL FULL JOIN`). Valid in ANSI SQL, Snowflake, and PostgreSQL, but **out of PSS scope** — walker emits fatal `NATURAL_FULL_OUTER_JOIN_UNSUPPORTED`. Characterization tests assert that diagnostic. Supported natural joins: `NATURAL JOIN`, `NATURAL LEFT [OUTER] JOIN`, `NATURAL RIGHT [OUTER] JOIN`. Supported full outer: explicit `FULL [OUTER] JOIN … ON` / `USING`.

---

### 2.8 — Investigate and reduce 5.1.3 slow-parse regressions

**Kind:** Performance defect / investigation (many queries exceed 90 seconds and regress materially from 5.0.0-3)

**Status:** **Complete (2026-09-02)** — timeout mission closed (E1–E3). Walker residuals tracked in **W-track** below → **2.11**; production SLL policy → **2.10**.

**Component:** end-to-end parser pipeline; likely more than one algorithm (set-op interface matching is not the only timeout shape)

#### Progress (2026-09)

- **Done:** Opt-in stage-timing instrumentation — `ParseLatencyDiagnosticService`, `ParseLatencyReport`, and `ParseLatencyDiagnosticTest` (`parse/src/main/java/sql/latency/`, `parse/src/test/java/sql/latency/`).
- **Done (candidate fix, needs corpus validation):** Incremental set-op `def_*` payload cache in `SqlParseSymbolTreeHelper` / `SqlParseEventWalker` to avoid repeated O(n²) scans on UNION/INTERSECT branches.
- **Done (2026-09-01):** Construction clustering of all **74** timeout rows (SQL shape only; parser not run). Eighteen buckets below. Set-op header walking is the dominant *family* (Buckets 1–2 plus smaller UNION shapes) but **not** the only likely hotspot — several buckets have **zero** set-ops.
- **Done (2026-09-02):** Set-op scoping **S1–S4** (outer-only snapshot, INTERSECT parity, reference-directed bundle, sibling exclusion) + S4 sibling-isolation SQL test + agent rule `.cursor/rules/set-op-convert-egress-scoping.mdc`.
- **Done (2026-09-02):** Set-op scoping **S5** — audited exit-time-only participant merge/validation; gate delegates for UNION/INTERSECT/EXCEPT interface FATALs + nested INTERSECT validation.
- **Done (2026-09-02):** Post-S5 profiling — `ParseLatencyDiagnosticTest` convert-egress probes, `WalkerHotspotProfiler`, shared-table comparison (`SqlEventWalkerSetOpTimingProbeSharedTableComparisonTests`), N10 vs N50 scaling. **Revised performance plan (B/C/D/E)** documented below; do not re-diagnose from scratch.
- **Done (2026-09-02):** Phase **B1** — cache `getAncestorSymbolTables()` per symbol-table stack generation (`e525ddc`). Profiler: `getAncestorSymbolTables_levelsScanned` 19,384 → **256** at N=50 M=20; walk time flat (~9.1 s). Gates green.
- **Skipped (2026-09-02):** Phase **B2** / **B3** — implemented locally, measured, **reverted** (no dependency from other landed work). Per-convert outer-visible-scope cache (B2) and frozen ancestor list (B3) cut redundant profiler call counts but **did not improve wall time**; B2 trended ~0.5–0.7 s slower vs B1 (JVM noise band). Convert egress + scope reconstruction remain **&lt;1%** of walk — further B-track work abandoned; see **Phase B outcomes** below.
- **Done (2026-09-02):** Phase **C2.1** — `hotspotScope` nanosecond timing on walker helper paths (`WalkerHotspotProfiler`, dual-mode harness).
- **Done (2026-09-02):** Phase **C2.2** — removed eager `showTrace` / `asTree.toString()` from `enterEveryRule` / `exitEveryRule` (root cause of α≈2). N=50 M=20 walk **~900 ms** (was **~10.5 s** pre-solution); E1 fitted **α ≈ 0.94**, **β ≈ 0.92**.
- **Done (2026-09-02):** Phase **E1** — full calibration matrix re-run (`setOpTimingProbeE1CalibrationMatrixTest`); post-C baselines recorded below.
- **Done (2026-09-02):** Post-C2.2 batch re-run of all **74** `timeout_513` rows (`PantoTimeoutBatchBenchmarkTest`) — **0/74** still timeout (~4 s total). Former **20** subMap skip-list rows cleared after parse-phase walk gate (see **W-track** below).
- **Done (2026-09-02):** Phase **E2** — full row **475** (`EAB.Country`, 248 `UNION`, 41,829 chars) embedded in `sql/csv-row-475.sql` + `ParseLatencyDiagnosticTest#pantoRow475_eabCountry`; diagnostic **walk=89 ms**, **total=173 ms** (5.0.0-3 ~7.4 s walk; pre-C2.2 **90 s** kill).
- **Done (2026-09-02):** Phase **E3** — `PantoTimeoutCorpusE3GateTest`: **74/74** `timeout_513` rows under **90 s** (`maxWalkMs=81` row **475**, `maxTotalMs=174` row **605**); named canaries **2.8-1** (475, 476, 1827, 1828), **2.8-2** (**1837**), non-UNION **5261, 4647, 4197, 130** pass. **11** rows emit FATAL diagnostics but complete fast → **2.11** Cluster B.
- **Done (2026-09-02):** **W1** — parse-phase walk gate on `SqlParserAccess` + `ParseLatencyDiagnosticService` (`ParsePhaseErrorGate`); `AST_WALK_SKIPPED_DUE_TO_PARSE_ERRORS` WARNING with first parse error line/pos; `ParseLatencyReport.diagnostics`; `PantoSubMapSkipListRegressionTest`; skip list cleared (`e8abd91`).
- **Done (2026-09-02):** **W2** — `requireNodeMap` at `exitSql` + `abortWalk` empty-map fallback on `removeNodeMap`; `SqlParserAccess` maps `subMap` NPE → `AST_WALKER_STACK_MISALIGN`; `AbstractASTWalkerHelperTest#removeNodeMapOnMissingFrameRecordsStackMisalignFatal`.
- **Abandoned (2026-09-02):** **D1** (defer global merge), **D2** (`LinkedHashSet` dedup), **C2** optional micro-opts (clause-flatten batching, local FROM fast path) — no E3 regression; will not implement.
- **Done (2026-09-02):** Phase **C1** — `WalkerHotspotProfiler` extended with `walkerExit_*`, `columnCapture_*`, `columnArchive_*`, `columnResolution_<round>_*` counters (`SqlParseEventWalker`, `SqlASTWalkerHelper`, `SqlParseSymbolTreeHelper`).
- **Done (2026-09-01):** Set-op scoping implementation plan and JVM timing probes (`setOpTimingProbeTenUnionAllJoinersV0Test`, `setOpTimingProbeTenIntersectJoinersV0Test` in `SqlEventWalkerSubqueriesAndClauseSemanticsTests`) — baseline recorded below.

#### Set-op scoping — safe implementation steps (2.8)

**Problem (confirmed):** `buildConvertEgressScopeBundle` merges every `def_*` payload from ancestor symbol-table frames. Set-op siblings (`UNION` / `INTERSECT` / `EXCEPT` participants) publish onto the same parent frame, so each later branch convert re-materializes all prior sibling trees even though SQL gives no cross-branch visibility. The incremental `setOpDefinitionPayloadCacheStack` reduces ancestor *scan* cost but still grows the visible payload set with every sibling publish.

**Invariant (matches `symbol-table-bucket-reference` JOIN/WITH rules):** A set-op participant may see only (a) its own `FROM` / `table_alias`, (b) inherited outer `context_list` / correlated aliases, and (c) explicitly referenced `queryN` / `unionN` / `intersectN` keys — **not** sibling participant payloads on the same set-op frame.

| Step | Change | Safety / test gate |
|------|--------|-------------------|
| **S0** | Land timing probes (11 branches, 3 columns, distinct literals per branch) | `setOpTimingProbeTenUnionAllJoinersV0Test`, `setOpTimingProbeTenIntersectJoinersV0Test` — smoke semantics only; record baseline ms below |
| **S1** | Rename intent: `setOpDefinitionPayloadCacheStack` → **outer-only** snapshot at frame entry; **do not** append sibling `publishQueryLikeScope` payloads to the cache | S0 probes + full `SqlEventWalkerSubqueriesAndClauseSemanticsTests` suite (functional goldens unchanged) |
| **S2** | Call outer-only snapshot at **`enterIntersected_query`** as well as `enterUnionized_query` | `SqlEventWalkerJoinsAndTableResolutionTests.withCteLeftJoinUnionThenTrailingJoinSubqueryKeepsUnionAliasV0Test`; nested `UNION` inside `INTERSECT` fixtures |
| **S3** | Replace wholesale `mergeDefinitionPayloadsFromSymbolTable(ancestor, …)` in `buildConvertEgressScopeBundle` with **reference-directed** collection: seed from frozen outer snapshot + walk `context_list`, local `table_alias`, `dependent_queries`; open `def_*` **lazily** by live ref only | Existing CTE / correlated subquery matrix; `SqlEventWalkerBareValueExpressionTests` WITH cases |
| **S4** | When converting inside an active set-op frame, **exclude** sibling participant keys (`def_query*`, `def_union*`, … per `isSetOperationParticipantKey`) from the immediate set-op parent frame | S4 sibling-isolation SQL test; ~15–20% probe win post-S5 — **not** sufficient for α → 1 (see profiling section) |
| **S5** | Keep one-shot participant iteration only at `exitUnionized_query` / `exitIntersected_query` (`finalizeSetOperationScopeSymbolTable`, `validateSingleSetOperationInterface`) | Set-op FATAL / interface goldens in `SqlEventWalkerLiveSampleQueriesTests` / `SqlParseEventWalkerWithAccessObjectTest` |
| **S6** | **Revised:** Record post-S5 baselines + publish profiler tooling; **do not** gate on probe α → 1 alone | `WalkerHotspotProfiler`, calibration matrix, `ParseLatencyDiagnosticTest` probes — see **Post-S5 baselines** and **Profiling conclusions** |
| **S7** | **Renamed intent → E3:** Panto timeout buckets **2.8-1**, **2.8-2** under 90s | **Done (E3)** — 54-row corpus gate + bucket tracker updated |

**Out of scope for S1–S4:** Changing set-op interface validation rules, relaxing `DUPLICATE_INTERFACE_COLUMNS` / `SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH`, or deferring per-branch convert to statement end (each branch still publishes its own `def_queryN` artifacts).

#### Profiling conclusions and revised plan (2026-09-02, post-S5)

**Purpose:** Future sessions should read this section before re-running diagnosis. S1–S5 fixed a **real correctness + partial performance** problem (sibling `def_*` re-merge). Profiling shows the **remaining superlinear wall time** is **not** primarily in convert egress or table-dictionary merge on the synthetic probe shape.

##### Instrumentation (landed)

| Tool | Path | Enable |
|------|------|--------|
| Parse vs walk split | `ParseLatencyDiagnosticService`, `ParseLatencyDiagnosticTest` | `mvn -pl parse -Dtest=ParseLatencyDiagnosticTest#…` |
| Method-call counters + convert breakdown | `WalkerHotspotProfiler` | `-Dpss.walker.hotspot.profile=true` |
| N10 vs N50 scaling + distinct vs shared table mode | `SqlEventWalkerSetOpHotspotProfileTest#setOpHotspotProfileDistinctAndSharedN10vsN50` | `@Ignore` — **always run both** `DISTINCT_PER_BRANCH` and `SHARED_SINGLE_TABLE`; uses `reportTableModeComparison`, `reportDisproportionateTableModeScaling`, `reportWalkerExitTimingScaling` |
| Distinct vs shared wall time (parse/walk split) | `ParseLatencyDiagnosticTest#probe_setOpConvertEgress_distinctVsShared_N50_M20_unionAll` | CI-safe paired walk comparison |
| Distinct vs shared FROM table | `SetOpTimingProbeFixtures.BranchTableMode`, `SqlEventWalkerSetOpTimingProbeSharedTableComparisonTests` | `@Ignore` manual |

Hooks instrument: `convertSymbolTableToTableDictionary`, `classifyColumnRefAtConvertEgress`, inner resolution (`resolveQualified…`, `collectUnqualifiedSourceReferences`, …), `getAncestorSymbolTables`, per-convert timing in `convertEnd`.

##### What we measured (distinct `probe_branch_NNN` per branch, M=20)

**Wall time (parse + walk):** N=10 → ~1 s; N=50 → ~9–10 s (~**9×** for **4.6×** branches → superlinear).

**Parse vs walk (`ParseLatencyDiagnosticTest`):** At N=50 M=20, walk ≈ **8 s**, parse ≈ **3 ms**, `sllFallback=0`. Bottleneck is event walker, not grammar prediction.

**Post-S5 vs pre-S1–S4:** Calibration matrix ~**15–20%** faster; fitted **α still ≈ 2** (doubling N ≈ 4× time). S1–S4 helped; did **not** linearize probes.

**Inner `classifyColumnRef` call counts (N=10 → N=50):** All scale **~4.64×** (linear in branch count): `classifyColumnRef_qualifiedPath` 220→1020, `resolveQualifiedColumnAgainstVisibleScope` 550→2550, `getTableDictionaryForReference` 1210→5610, `mergeColumnEntry` 660→3060, `getAncestorSymbolTables` 1401→6481 (~127 calls per convert).

**Per-convert timing:** All **51** `convertSymbolTableToTableDictionary` invocations at N=50 sum to **~1 ms total**. Convert + classify is **&lt;1%** of walk time on this probe.

**Set-op exit validation:** `validateSetOperationInterface` = **1** call regardless of N (S5 path is negligible).

**Shared-table control (same `probe_shared_table` every branch):** ~**5–8% slower** than distinct tables at N≥25 — **not** faster. `mergeColumnEntry_dedupContainsCheck` = **0** on distinct-table probe (first-write per column per table, no repeated `ArrayList.contains()` merges).

##### C1.1 / C1.2 timing findings (2026-09-02, re-run with dual-mode harness)

**Wall time (parse + walk, M=20 UNION ALL):** distinct N10 **1.58 s** → N50 **10.3 s** (~**6.5×** for **4.64×** branches); shared N10 **0.68 s** → N50 **10.7 s** (~**15.7×**). At N=50, shared and distinct are within noise; shared is faster only at small N.

**C1.1 — scoped walker exits (`walkerExitNanos_*`):** Instrumented exits sum to **~69 ms** (N10) / **~126 ms** (N50 distinct) vs **~10 s** wall — **~1–1.2%** of walk time is inside scoped listener exits. Dominant scoped exits at N50: `query_specification` ~31 ms, `finalizeQueryScopeSymbolTable` ~28 ms, `column_reference` ~31 ms, `select_item` ~29 ms (each ~25% of scoped total).

**Superlinear per-exit time (nanos ratio &gt; hits ratio 4.64):**

| Exit | Distinct nanos× | Shared nanos× | Notes |
|------|-----------------|---------------|-------|
| `column_reference` | 3.79 | **8.28** | Shared mode worst offender |
| `select_item` | 3.48 | **7.64** | Interface flatten + dependency harvest |
| `finalizeQueryScopeSymbolTable` | 1.21 | **5.06** | Convert publish; shared global dict merge |
| `query_specification` | 1.27 | **5.04** | Sublinear on distinct; superlinear on shared |

**C1.2 — grammar `exitEveryRule` (`ruleExitNanos_*`):** **~17 ms** (N10) / **~53 ms** (N50) — also **&lt;1%** of wall. Top rules: `simple_identifier`, `identifier`, `column_reference` (all scale ~linearly in hits). Dedicated exits (`exitQuery_specification`, `exitSelect_item`, …) hold most semantic work; generic rule exit is not the bottleneck.

**Implication for C2:** Superlinear wall time lives **outside** both scoped exits and `exitEveryRule` — likely **enter** listeners, stack/map churn, set-op frame publish, or uncaptured helper paths. C2 should target `select_item` / `column_reference` per-call cost growth (especially shared table) and broaden timing to set-op frame + `flattenSubTreeForDependencyColumns` if needed.

##### C2.1 helper-path timing findings (2026-09-02)

**`astEnterEveryRule` is the dominant cost** — accounts for **~88–90%** of walk wall time at N=50:

| Mode | N10 enter ms | N50 enter ms | Wall N50 | Nanos ratio (N10→N50) | Hits ratio |
|------|-------------|-------------|----------|----------------------|------------|
| Distinct | 611 | **9,457** | 10,784 | **15.5×** | 4.63× |
| Shared | 483 | **11,304** | 12,550 | **23.4×** | 4.63× |

Per-call `astEnterEveryRule` avg grows **122 µs → 408 µs** (distinct) and **97 µs → 487 µs** (shared) — superlinear cost inside `pushStack` / `collectNewRuleMap` / `asTree` growth, not hit count.

**Secondary (small) superlinear paths:** `collectUnresolvedColumnReference` (~22 ms N50, 3–7× nanos), `flattenSubTreeForDependencyColumns` (~5 ms), `mergeUnknownEntries` (~3 ms). **Not** convert, dict dedup, or set-op finalize.

**C2.2 target:** ~~`enterEveryRule` infrastructure~~ **Done (2026-09-02):** root cause was eager `showTrace(..., "... " + asTree)` in `enterEveryRule` — Java evaluates `asTree.toString()` even when `showParse` is false. Removed all `showTrace` machinery from AST walker helpers. Post-fix N50 wall **~2.3 s distinct / ~1.9 s shared** (was **~10.8 s / ~12.6 s**); `astEnterEveryRule` **~19 ms** total (was **~9.5 s**); scaling **linear** in branches. **E1** full matrix (below) confirms **α ≈ 0.94**, **β ≈ 0.92**.


Profiler recorded **global walker `table_dictionary` top-level key count at each branch convert exit**, then **summed** those snapshots:

- After branch 1: 1 table key; after branch 2: 2 keys; … after branch 51: 51 keys.
- Sum = 1+2+…+51 = **1326** = n(n+1)/2 (triangular number).

This measures **accumulation of distinct physical table names** in the global dict as branches complete — **not** O(n²) cost per dictionary operation, and **not** column-name diversity.

**Column reuse vs variety (probe conclusions):**

| Variation | Effect |
|-----------|--------|
| Same **column names** (`col_00`…), **different** table per branch | Default probe — each table is its own dict key; reusing column names does **not** collapse buckets |
| Same **table** name every branch | One dict key; slight **slowdown** (merge/dedup overhead); does not fix superlinear walk |
| Higher **M** (more unique columns per branch) | Was superlinear in M (β ≈ 1.7 pre-S5); **post-C2.2 β ≈ 0.92** (near-linear); independent of triangular table-key count |

**Implication:** ~~Optimizing `ArrayList.contains()` dedup (old S11 / **D2**)~~ and ~~defer global table merge (**D1**)~~ — both **abandoned** after E3. Timeout fix track was **B1 + C2.2** only.

##### Revised root-cause model (post-S5)

| Hypothesis | Verdict |
|------------|---------|
| Sibling `def_*` re-merge in `buildConvertEgressScopeBundle` | **Partially true** — S1–S4 fixed; ~15–20% win; not dominant remainder |
| Global `table_dictionary` / token-list dedup on distinct tables | **Not primary** on synthetic probe; shared-table slower; convert sub-ms |
| Recursive derived lineage phase B (`runConvertEgressRelationalModifierDerivedLineagePhaseB`) | **Linear** call count (11→51); not N² multiplier |
| Repeated **`getAncestorSymbolTables()`** reconstruction | **Confirmed waste** — ~6.5k calls at N=50 pre-B1; **B1** cuts level scans ~98% (19,384 → 256) but walk ms unchanged — not the dominant cost |
| **`SqlParseEventWalker` AST enter/exit** (symbol capture before convert) | **Confirmed dominant** — eager `asTree.toString()` in `showTrace` caused α≈2; **fixed in C2.2** |

##### Revised implementation tracks (execute in order)

**Phase A — Done (frozen):** S1–S5 scoping + diagnostics (S0, `ParseLatencyDiagnosticTest`, `WalkerHotspotProfiler`, shared-table probes).

**Phase B — Scope reconstruction cache (B1 done; B2–B3 skipped)**

| Step | Status | Change | Outcome |
|------|--------|--------|---------|
| **B1** | **Done** (`e525ddc`) | Cache `getAncestorSymbolTables()` per `symbolTable` stack generation; invalidate on push/pop only (`SqlASTWalkerHelper.symbolTableStackGeneration`) | `getAncestorSymbolTables_levelsScanned` **19,384 → 256** at N=50 M=20; `getAncestorSymbolTables_cacheHit` ~98%; walk ~**9.1 s** (flat vs post-S5 ~8.8–9.0 s); gates green |
| **B2** | **Skipped** (reverted) | Cache `collectOuterVisibleScope` once per convert; copy outer maps before merge in `buildEffectiveVisible*` | `collectOuterVisibleScope_cacheHit` ~94% (1,683/1,787); `getAncestorSymbolTables` calls 6,481 → 4,798; walk ~**9.9 s** avg — **no win** |
| **B3** | **Skipped** (reverted) | Freeze ancestor list for duration of one convert (`activeConvertEgressAncestorSymbolTables`) | `getAncestorSymbolTables_convertEgressReuse` ~3,213; walk ~**9.8 s** avg — **no win**; explicit per-convert materialize raised `levelsScanned` (256 → 409) vs B1-only cache hits |

**Phase B outcomes (2026-09-02):** B2 and B3 optimized paths that profiling already showed are **sub-millisecond** in aggregate (`convertTiming_totalMs` ≈ 2 at N=50). Reducing call counts there cannot move a ~10 s walk. **No subsequent step depends on B2/B3** — only `SqlParseSymbolTreeHelper` was touched; S1–S5 scoping, B1, `WalkerHotspotProfiler`, and gate tests are unaffected. **Do not revisit B2/B3** unless C1 shows convert-egress or `collectOuterVisibleScope` unexpectedly hot on corpus shapes (e.g. 2.8-2).

**Target after B (revised):** B1 retained for correctness-neutral profiler hygiene only. **Wall-time gains require Phase C.** Optional **E1** after C (not after B alone).

**Phase C — Walker hot path (P0–P1, where α→1 likely lives)**

**Performance gate policy (2026-09-02):** Every synthetic performance gate (**C1** substeps, **C2**, **E1** calibration) must run **both** `SetOpTimingProbeFixtures.BranchTableMode.DISTINCT_PER_BRANCH` and `SHARED_SINGLE_TABLE` in the same session. Do not ship a walker optimization that improves only one table mode without explaining the regression on the other.

| Step | Change | Gate |
|------|--------|------|
| **C1** | **Done** — call-count counters on top walker exits + column capture/archive/resolution | `walkerExit_*`, `columnCapture_*`, `columnResolution_*` in N10 vs N50 scaling report |
| **C1.1** | **Done** — per-exit nanosecond timing (`walkerExitScope` on instrumented exits + `finalizeQueryScopeSymbolTable`) | `reportWalkerExitTiming` + `reportWalkerExitTimingScaling` (distinct **and** shared N10→N50) |
| **C1.2** | **Done** — grammar-rule timing in `exitEveryRule` (`ruleExitBegin`/`ruleExitEnd`; lightweight JFR substitute) | `reportRuleExitTiming` per probe run; optional external JFR: `java -XX:StartFlightRecording=filename=walker.jfr,dumponexit=true -Dpss.walker.hotspot.profile=true …` |
| **C1.3** | Gate every **C2** change with distinct **and** shared probes | `setOpHotspotProfileDistinctAndSharedN10vsN50` before/after each C2 patch |
| **C1.4** | Defer SELECT/ORDER BY resolution memo on current probe | **Skip for now** — probe ORDER BY (`sort_col_*`) and SELECT (`col_*`) are disjoint; no duplicate qualified resolution |
| **C1.5** | ~~**D2** (`LinkedHashSet` dedup)~~ | **Abandoned** — spike (2026-09-02) showed slower wall time; will not implement |
| **C2.1** | **Done** — helper-path nanosecond timing (`hotspotScope` on `astEnterEveryRule`, `astExitEveryRule`, `handlePushDown`, `collectUnresolvedColumnReference`, `flattenSubTreeForDependencyColumns`, `captureClauseDependencies`, `finalizeSetOperationScopeSymbolTable`, `mergeUnknownEntries`) | `reportHotspotTiming` + scaling in dual-mode harness |
| **C2.2** | **Done** — remove eager `showTrace` / `asTree.toString()` from `enterEveryRule` / `exitEveryRule`; delete trace flags from `AbstractASTWalkerHelper` | E1 α → ~1; N=50 M=20 walk **~900 ms** (was ~10.5 s pre-solution) |
| **C2** | ~~Optional micro-opts: batch/defer clause flattening; qualified local FROM fast path; avoid duplicate capture+convert work~~ | **Abandoned (2026-09-02)** — E3 showed no need; will not implement unless a future regression is profiled |

**Phase D — Dictionary policy (reprioritized; was S8–S11)**

| Step | Change | When | Gate |
|------|--------|------|------|
| **D1** | ~~Defer global `table_dictionary` merge for set-op participants until `exitUnionized_query` / `exitIntersected_query` (old **S10**)~~ | **Abandoned (2026-09-02)** — E3: all **20** **2.8-2** rows &lt; 90 s; canary **1837** walk **9–71 ms**. **Will not implement.** | — |
| **D2** | ~~`LinkedHashSet` ref dedup instead of `ArrayList.contains()` (old **S11**)~~ | **Abandoned (2026-09-02)** — spike slower on wall time; will not implement | — |

**Phase E — Corpus validation (revised S6 / S7)**

| Step | Change | Gate |
|------|--------|------|
| **E1** | **Done** — full calibration matrix after **C2.2**; post-C baselines below | **α ≈ 0.94**, **β ≈ 0.92** (OLS log-log) |
| **E2** | **Done** — full row **475** in `sql/csv-row-475.sql` + `ParseLatencyDiagnosticTest#pantoRow475_eabCountry` | **walk=89 ms**, **total=173 ms** (chars=41,829; 248 `UNION`) |
| **E3** | **Done** — `PantoTimeoutCorpusE3GateTest`; **74/74** rows &lt; 90 s; **2.8-1** + **2.8-2** canary **1837** + non-UNION **5261, 4647, 4197, 130** | Bucket tracker **Complete** (timeout); **11** fast-FATAL rows + **W2–W5** walker residuals documented |

**Suggested execution order:** `A` (done) → `B1` (done) → `C1`→`C2.2` → `E1` → `E2` → `E3` → **W1** (done) → **W2** (done) → **W3** (done) → **W4** (row **130** full CSV) → **2.11.2** (fast-FATAL cluster) → **2.10** (SLL policy). ~~`B2`→`B3`~~ skipped. ~~`D1`~~, ~~`D2`~~, ~~`C2` micro-opts~~ **abandoned**.

#### Parse-phase walk gate + walker stack residuals (**W-track**, 2.8 → 2.11)

**Problem:** After parse-error recovery, walking a partial AST mis-aligns walker stack state (`removeNodeMap()` → `subMap` null → NPE in `exitSql`). Syntax-corrupt corpus SQL should surface **only** the parse-phase FATAL; legitimate-complex SQL may parse without a parse-phase FATAL but still mis-align the walker.

**Goal:** Trap failures as structured diagnostics — not Java stack traces — and route `syntax_corrupt` rows to corpus-quality classification (no walker fix).

| Step | Change | Status / gate |
|------|--------|---------------|
| **W1** | **Gate AST walk** when `ParsePhaseErrorGate.hasParsePhaseErrors()` — `SqlParserAccess`, `ParseLatencyDiagnosticService`; emit **`AST_WALK_SKIPPED_DUE_TO_PARSE_ERRORS`** WARNING (line/pos/token from first parse FATAL/ERROR); mirror on `ParseLatencyReport.diagnostics` | **Done (2026-09-02)** — `ParsePhaseErrorGate`, `PantoSubMapSkipListRegressionTest`; commits `95e66c8`, `e8abd91` |
| **W2** | **`requireNodeMap` null-guard** in `AbstractASTWalkerHelper` — opt-in at `exitSql`; after first misalign `abortWalk` makes `removeNodeMap` return empty map; `SqlParserAccess` maps residual `subMap` NPEs to **`AST_WALKER_STACK_MISALIGN`** | **Done (2026-09-02)** — manifest row **130** completes clean; unit test + access catch |
| **W3** | **Tighten `SqlParserAccess.generateAST()` catch** — map stack misalignment NPE to structured diagnostic; dedupe; `ParseLatencyDiagnosticService` parity | **Done** — `WalkerWalkExceptionGate`; access + diagnostic catch paths |
| **W4** | **Tests + corpus gates** — row **130** regression on **full CSV SQL**; optional zero-FATAL assertion on E3 for Cluster B when adjudicated | **Partial** — `csv-row-130.sql` + `PantoRow130FullCsvRegressionTest`; Cluster B zero-FATAL gates pending **2.11.2** |
| **W5** | **Workplan / corpus bookkeeping** — reclassify **`syntax_corrupt`** rows (**28–32, 41, 314–315**) as author-error (no parser fix after W1); keep skip list empty unless batch reproduces walker throw **without** parse-phase errors | **Partial** — skip list cleared; bucket tracker updated; author-error notes pending |

**Cluster A disposition (former 20-row skip list):**

| Category | csv_rows | W1 outcome | Remaining work |
|----------|----------|------------|----------------|
| **syntax_corrupt** | 28–32, 41, 314–315 | Walk skipped; parse FATAL only | **W5** — document as corpus-quality / author-error |
| **legitimate_complex** | 130, 1814, 2120, 4163, 5860–5863 | May still walk if no parse-phase FATAL | **W2** null-guard + targeted fix |
| **dev_template** | 4157–4158, 4164, 4170–4171 | Same as legitimate_complex | **W2** + classify whether SQL is in-scope for parser |

**Next step:** **W2** (null-guard), starting with row **130** on full `panto_513_outstanding_issues.csv` SQL.

##### Legacy step mapping (for grep / old notes)

| Old | New |
|-----|-----|
| S6 α→1 gate | **E1** — baselines only; α→1 expected after **C**, not S1–S5 alone |
| S7 corpus | **E3** |
| S8 profile 1837 | **C1** + **E3** (partially done via shared-table + convert probes) |
| S9 shared-table probe | **Done** — `BranchTableMode.SHARED_SINGLE_TABLE` |
| S10 defer global merge | **D1** — **abandoned** (E3; will not implement) |
| S11 LinkedHashSet dedup | **D2** — **abandoned** (will not implement) |
| S12 2.8-2 acceptance | **E3** |

#### Set-op timing probe baselines (S0)

Probe shape (shared builder `SetOpTimingProbeFixtures`): **N** joiners (**N+1** branches), **M** select-list columns (`col_00`…), **M/2** fully qualified `ORDER BY` keys on non-output columns (`sort_col_*`), distinct `probe_branch_NNN` table per branch. Smoke probes in `SqlEventWalkerSubqueriesAndClauseSemanticsTests` print full extractor output; calibration uses timing-only (`SqlEventWalkerSetOpTimingProbePreSolutionTests`). Re-record after set-op scoping lands (S6).

##### Pre-solution estimation matrix (2026-09-02, local JVM)

**Convention:** N = joiner count; M = select-list column count; ORDER BY column count = M/2. Times in **ms** (parse + walk only).

| Fixed dimension | Sweep | UNION ALL | INTERSECT |
|-----------------|-------|-----------|-----------|
| N = 50 | M = 6 | 1,766 | 1,370 |
| N = 50 | M = 10 | 2,955 | 2,946 |
| N = 50 | M = 20 | 10,502 | 10,403 |
| N = 50 | M = 30 | 23,055 | 22,130 |
| N = 50 | M = 40 | 40,764 | 40,812 |
| N = 50 | M = 50 | **66,477** | **66,778** |
| M = 20 | N = 10 | 644 | 628 |
| M = 20 | N = 25 | 2,850 | 2,843 |
| M = 20 | N = 50 | 10,380 | 10,467 |
| M = 20 | N = 75 | 23,659 | 23,402 |
| M = 20 | N = 100 | 43,868 | 43,223 |
| M = 20 | N = 114 | **56,908** | **58,331** |
| M = 20 | N = 150 | 112,201 | 112,190 |

**~60s one-minute extreme settings (pre-solution manual regression):**

| Bound | Set op | N (joiners) | M (select cols) | Measured ms | Test |
|-------|--------|-------------|-----------------|------------:|------|
| N-bound | UNION ALL | 114 | 20 | 56,908 | `setOpTimingProbePreSolutionUnionOneMinuteNBoundTest` |
| N-bound | INTERSECT | 114 | 20 | 58,331 | `setOpTimingProbePreSolutionIntersectOneMinuteNBoundTest` |
| M-bound | UNION ALL | 50 | 50 | 66,477 | `setOpTimingProbePreSolutionUnionOneMinuteMBoundTest` |
| M-bound | INTERSECT | 50 | 50 | 66,778 | `setOpTimingProbePreSolutionIntersectOneMinuteMBoundTest` |

All four one-minute tests live in `SqlEventWalkerSetOpTimingProbePreSolutionTests` (`@Ignore` — enable for manual regression during S1–S6).

**Fitted complexity (pre-solution, UNION ALL calibration points):**

\[
T(N,M) \approx c \cdot N^{\alpha} \cdot M^{\beta}, \quad \alpha \approx 2.0,\; \beta \approx 1.7
\]

Consistent with **superlinear walk time** on long UNION chains. Pre-S5 model attributed this primarily to sibling `def_*` re-merge; **post-S5 profiling** shows convert egress is sub-ms and call counts inside classify scale linearly — remaining α≈2 is attributed to **walker AST hot path + redundant scope reconstruction** (see workplan §2.8 profiling conclusions).

Run calibration matrix: `mvn -pl parse -Dtest=SqlEventWalkerSetOpTimingProbePreSolutionTests#setOpTimingProbeE1CalibrationMatrixTest test` (full N/M grid, `@Ignore` — remove or use IDE run). Subset + boundary sweeps: `#setOpTimingProbePreSolutionCalibrationMatrixTest`.

~~After set-op scoping (S1–S4), re-run matrix and one-minute probes; expect **α → ~1.0** (linear in N) while **β** unchanged.~~ **Superseded (2026-09-02):** Post-S5 re-run shows **α still ≈ 2**. S1–S4 gave ~15–20% improvement only. **B1** did not change walk ms; **B2/B3** skipped. ~~Linear N scaling requires **Phase C** (walker hot path).~~ **Updated (2026-09-02):** **C2.2** (`showTrace` removal) linearized probes — see **Post-C baselines** below.

##### Post-S5 baselines (2026-09-02, local JVM, after S1–S5)

| Fixed dimension | Sweep | UNION ALL pre-S5 | UNION ALL post-S5 (~) |
|-----------------|-------|------------------|------------------------|
| M = 20 | N = 50 | 10,502 | ~8,830 |
| M = 20 | N = 114 | 56,908 | ~46,400 |
| M = 20 | N = 150 | 112,201 | ~91,300 |

Fitted **α ≈ 2** unchanged; **β ≈ 1.7** unchanged.

##### Post-C baselines (2026-09-02, local JVM, after C2.2 showTrace removal)

**Convention:** N = joiner count; M = select-list column count; ORDER BY column count = M/2. Times in **ms** (parse + walk only). Source: `setOpTimingProbeE1CalibrationMatrixTest`.

| Fixed dimension | Sweep | UNION ALL | INTERSECT |
|-----------------|-------|-----------|-----------|
| N = 50 | M = 6 | 1,184 | 355 |
| N = 50 | M = 10 | 484 | 507 |
| N = 50 | M = 20 | 957 | 964 |
| N = 50 | M = 30 | 1,516 | 1,426 |
| N = 50 | M = 40 | 1,777 | 1,766 |
| N = 50 | M = 50 | 2,185 | 2,136 |
| M = 20 | N = 10 | 224 | 194 |
| M = 20 | N = 25 | 481 | 427 |
| M = 20 | N = 50 | 900 | 838 |
| M = 20 | N = 75 | 1,304 | 1,316 |
| M = 20 | N = 100 | 1,945 | 1,747 |
| M = 20 | N = 114 | 2,042 | 1,943 |
| M = 20 | N = 150 | 2,599 | 2,522 |

**Fitted complexity (post-C2.2, OLS log-log on full E1 grid):**

\[
T(N,M) \approx c \cdot N^{\alpha} \cdot M^{\beta}, \quad \alpha \approx 0.94,\; \beta \approx 0.92
\]

**Key comparisons (N=50 M=20):** pre-S5 **10,502 ms** → post-S5 **~8,830 ms** → post-C2.2 **900 ms** (~**11.7×** vs pre-S5). **N=150 M=20:** pre-S5 **112,201 ms** → post-C2.2 **2,599 ms** (~**43×**).

**Note:** First E1 point (N=50 M=6 UNION ALL) may include JVM cold-start noise (1,184 ms vs 484 ms at M=10).

**`ParseLatencyDiagnosticTest` (row 475 / 2.8-1 canary, post-C2.2):**

| Test | Shape | Walk | Total |
|------|-------|-----:|------:|
| `pantoRow475_eabCountry` | **Full** row 475 — 248 `UNION`, 41,829 chars | **89 ms** | **173 ms** |
| `probe_union250` | 250-term UNION (synthetic equivalent) | **143 ms** | **287 ms** |
| `pantoRow475_eabCountry_trimSmoke` | 20-branch trim (fast smoke) | ~5 ms | ~9 ms |

Pre-fix **5.0.0-3** full row ~7.4 s walk; **5.1.3** pre-C2.2 hit **90 s** kill.

**Day-to-day smoke probes** (`SqlEventWalkerSubqueriesAndClauseSemanticsTests`, N=50, M=20):

| Test | Set op | Pre-S5 (ms) | Post-S5 (ms) |
|------|--------|-------------|--------------|
| `setOpTimingProbeTenUnionAllJoinersV0Test` | UNION ALL | **~10,500** | **~8,830** |
| `setOpTimingProbeTenIntersectJoinersV0Test` | INTERSECT | **~10,400** | **~8,800** |

#### Corpus validation reminders — buckets 2.8-1 / 2.8-2 (check as S1–S7 land)

**Purpose:** The synthetic N/M probes (`SetOpTimingProbeFixtures`) model **table-qualified convert egress**. Panto buckets **2.8-1** and **2.8-2** are the first corpus targets; re-check them after each major step so a probe-only win is not mistaken for a full fix.

**N/M model fit (pre-solution, 2026-09-02):** \(T \approx c \cdot N^{2.0} \cdot M^{1.7}\) from calibration probes.

| Bucket | Canary rows | Shape (approx.) | 5.0.0-3 | 5.1.3 | Model predict. | Predictable by N/M? |
|--------|-------------|-----------------|---------|-------|----------------|---------------------|
| **2.8-1** | **475**, 476, 1827, 1828 | `UNION` literal lookup; **N≈248**, **M=6**, no substitutions | ~7 s | **~0.3 s** (`probe_union250` post-C2.2) | **~33 s** (pre-C model) | **Yes** for N — α→1 fix collapses timeout; validate full SQL in **E2** / **E3** |
| **2.8-2** | **1837** (canary), 1819–1821, 1986–1987 (worst **N≈105**) | `UNION ALL` PCM convert; **N≈71–106**, **M=4**, **~180–290** `<…>` subs, GROUP BY per branch | ~1.7–2.7 s | **90 s** kill | **~1.4–3 s** | **No** — N/M alone fails (~30–60×); need substitution / GROUP BY / parse-vs-walk split |

**Takeaways (updated post-S5 profiling — do not revert to pre-profile assumptions):**

1. **2.8-1 (EAB.Country)** — **E2/E3 complete.** Full row **475** walk **36–89 ms**; all four **2.8-1** rows sub-second.
2. **2.8-2 (PCM convert)** — **E3 complete** for all **20** runnable rows; canary **1837** walk **11–71 ms**. **D1 abandoned** — no 90 s regression; defer-global-merge will not be pursued.
3. **Non-UNION guardrails** — **5261, 4647, 4197, 130** pass E3 timeout gate (**4197** emits 32 FATALs in **~2 ms**). Row **130** is **W2** canary (null-guard).

**When to run what (progress checklist):**

| After step | Synthetic gate | Corpus / diagnostic (manual) |
|------------|----------------|------------------------------|
| **S1–S5** | Day-to-day probes + `setOpSiblingIsolationInvariantV0Test` + set-op gate tests | — |
| **B1** | `WalkerHotspotProfiler` N10 vs N50; `levelsScanned` drop | Done — walk flat |
| ~~**B2–B3**~~ | — | Skipped (reverted) |
| **E1** | **Done** — post-C baselines in this doc | `setOpTimingProbeE1CalibrationMatrixTest` |
| **C2.2** | **Done** — `showTrace` removal; E1 α≈1 | `probe_union250` ~143 ms walk |
| **C2** | Optional micro-opts only if E3 shows need | — |
| **E2** | — | **Done** — full row **475** (`walk=89 ms`, `total=173 ms`) |
| **E3** | — | **Done** — 54-row corpus gate + named canaries |
| **D1** | — | **Abandoned** — E3 cleared **2.8-2** without global-merge deferral |

**Parse latency probes (`ParseLatencyDiagnosticTest`):**

- **E3:** `PantoTimeoutCorpusE3GateTest` — 54-row runnable corpus gate (`mvn -pl parse -Dtest=PantoTimeoutCorpusE3GateTest test`). Manifest from `tools/benchmark_panto_timeout_rows.py`.
- **E2:** `pantoRow475_eabCountry` loads full row **475** from [sql/csv-row-475.sql](../docs/rmcp-handoff/5.1.3-panto-outstanding/sql/csv-row-475.sql) (248 `UNION`, E2 gate **walk &lt; 90 s**).
- **E3 / 2.8-2:** `pantoRow1837_pcmConvert` in `ParseLatencyDiagnosticTest` (canary diagnostic).
- **Convert-egress probes:** `probe_setOpConvertEgress_*` methods (distinct/shared table, N=50 M=20) — parse vs walk split reference.

#### Repeated-table dictionary merge — post-S5 conclusions (2.8-2)

**Original hypothesis (2026-09-02):** When many branches reference the **same physical or bound table**, each branch convert merges into **one global `table_dictionary` entry** with **`ArrayList.contains()` dedup** — potentially O(N²) per shared column.

**Profiling verdict (2026-09-02, post-S5):**

| Check | Result |
|-------|--------|
| Shared-table synthetic probe (`BranchTableMode.SHARED_SINGLE_TABLE`) | **~5–8% slower** than distinct tables at N≥25 — not faster |
| `mergeColumnEntry_dedupContainsCheck` on distinct-table probe | **0** — no repeated merges on same column key |
| `convertSymbolTableToTableDictionary` total at N=50 | **~1 ms** for all 51 converts — not walk bottleneck |
| Triangular `convertTiming_globalTableDictSizeSum` (1+2+…+N) | Measures **distinct table keys** accumulating globally; not proof of O(n²) **time** per operation |

**E3 verdict (2026-09-02):** All **20** **2.8-2** PCM convert rows complete in **&lt; 1 s** on the diagnostic path (canary **1837** walk **9–71 ms**). Combined with post-S5 profiling (convert egress ≪ 1% of walk on probes), **D1** (defer global merge until set-op exit) is **abandoned** — unnecessary for the timeout problem and **will not be implemented**.

**D2 (`LinkedHashSet` dedup):** **Abandoned (2026-09-02)** — see D2 spike table below; will not implement.

~~| Step | Recommendation | Rationale |~~
~~| **S8** | **Profile** row **1837** …~~
~~| **S9** | Add synthetic probe: shared table …~~ — **S9 done** (`SetOpTimingProbeFixtures`, shared-table comparison tests).

| Step | Recommendation | Rationale |
|------|----------------|-----------|
| ~~**D1**~~ | ~~Defer global `table_dictionary` merge for set-op participants until set-op frame exit (old S10)~~ | **Abandoned (2026-09-02)** — E3 corpus + row **1837** show no timeout benefit; policy change rejected permanently |
| ~~**D2**~~ | ~~`LinkedHashSet` ref dedup (old **S11**)~~ | **Abandoned (2026-09-02)** — spike slower on wall time; will not implement |

**D2 spike (2026-09-02):** Replaced `mergeColumnEntry` `ArrayList.contains()` loop with `LinkedHashSet` → `ArrayList` (then **reverted** — spike only).

| Metric | Baseline (`ArrayList.contains`) | D2 spike (`LinkedHashSet`) |
|--------|--------------------------------|----------------------------|
| Shared N50 wall ms | 10,716 | 12,530 (**~1.8 s slower**) |
| Distinct N50 wall ms | 10,305 | 11,150 (**~0.8 s slower**) |
| Shared N50 `convertTiming_totalMicros` | 21,384 | 31,337 (**worse** — alloc overhead on small lists) |
| Shared N50 dedup events | 1,500 `dedupContainsCheck` | 1,500 `linkedHashSetDedup` |

**Verdict:** `contains()` dedup is **real on shared-table shapes** but **not material** at probe scale (convert ≪ 1% of walk). **D2 abandoned** — spike was slower on wall time; **will not implement**.

#### subMap walker skip list (cleared 2026-09-02)

Post-C2.2 batch (`PantoTimeoutBatchBenchmarkTest`, 74 rows, ~4 s): **18** rows logged `walker threw` in stderr before **W1**. **W1** (parse-phase walk gate) cleared the automated skip list; all **74** rows now run in `PantoTimeoutCorpusE3GateTest`.

**Canonical file:** [panto-submap-walker-skip-list.json](../docs/rmcp-handoff/5.1.3-panto-outstanding/panto-submap-walker-skip-list.json) — **`row_count: 0`**. Re-open a row only if full-parse batch reproduces walker stack mis-align **without** parse-phase errors.

**Former skip-list rows** (for **W2** / **W5** tracking):

| csv_row | query_key (short) | Former batch exception | Category | W-track |
|--------:|-------------------|------------------------|----------|---------|
| 28 | Acquia/PDP_Acquia_Export | `subMap is null` | syntax_corrupt | **W1** ✓ — **W5** document author-error |
| 30 | Acquia/PDP_Acquia_Export | `subMap is null` | syntax_corrupt | **W1** ✓ — **W5** |
| 31 | Acquia/PDP_Acquia_Export | `subMap is null` | syntax_corrupt | **W1** ✓ — **W5** |
| 32 | Acquia/PDP_Acquia_Export | `subMap is null` | syntax_corrupt | **W1** ✓ — **W5** |
| 41 | Acquia/PDP_Acquia_Export_v2 | `subMap is null` | syntax_corrupt | **W1** ✓ — **W5** |
| 130 | ALR/transformation_query_applicants | `subMap is null` | legitimate_complex | **W2** canary |
| 314 | ALR/transformation_query_inquiry | `subMap is null` | syntax_corrupt | **W1** ✓ — **W5** |
| 315 | ALR/transformation_query_inquiry | `subMap is null` | syntax_corrupt | **W1** ✓ — **W5** |
| 1814 | Enroll360/Partner Processed Census Student Term Attributes.final | `subMap is null` | legitimate_complex | **W2** |
| 2120 | Enroll360/Project Atlas Migration Checks.stud_race | `walker threw: null` | legitimate_complex | **W2** |
| 4157 | Enroll360/DataOrgPilot.StudentYearFundsLogicTesting | `walker threw: null` | dev_template | **W2** + classify |
| 4158 | Enroll360/DataOrgPilot.StudentYearFundsLogicTesting | `walker threw: null` | dev_template | **W2** + classify |
| 4163 | Enroll360/FundAmountLogicTesting_Student Year Funds | `walker threw: null` | legitimate_complex | **W2** |
| 4164 | Enroll360/funds logic testing | `walker threw: null` | dev_template | **W2** + classify |
| 4170 | Enroll360/TESTING_DataOrgPilot.StudentYearFundsLogicTesting | `walker threw: null` | dev_template | **W2** + classify |
| 4171 | Enroll360/TESTING_DataOrgPilot.StudentYearFundsLogicTesting | `walker threw: null` | dev_template | **W2** + classify |
| 5860 | PDP_ALR_V2/par_intake_student_last_validated | `walker threw: null` | legitimate_complex | **W2** |
| 5861 | PDP_ALR_V2/par_intake_student | *(same family)* | legitimate_complex | **W2** |
| 5862 | PDP_ALR_V2/par_intake_student | *(same family)* | legitimate_complex | **W2** |
| 5863 | PDP_ALR_V2/par_intake_student | `walker threw: null` | legitimate_complex | **W2** |

**Consumers:** `PantoCorpusSkipList` (allows empty list), `tools/benchmark_panto_timeout_rows.py` (default exclude when non-empty), `PantoTimeoutBatchBenchmarkTest` (skip unless `-Dpanto.skip.list.include=true`).

#### E3 fast-FATAL rows (11 — complete under 90s, emit FATAL diagnostics)

`PantoTimeoutCorpusE3GateTest` (2026-09-02, updated post-**W1**): **74/74** rows pass the **90 s** timeout gate. **11** rows emit one or more FATAL diagnostics but finish in **&lt; 200 ms** total — **Cluster B** (distinct from former subMap skip-list / **W2** path).

| csv_row | Bucket | max symptom |
|--------:|--------|-------------|
| 605, 606, 623, 635, 636 | 2.8-7 Student Address | 7 FATALs each, walk ≤ 5 ms |
| 4197 | 2.8-15 fulfillment star-join | 32 FATALs, ~2 ms total |
| 5453, 5454 | 2.8-17 wide contact projection | 3 FATALs each |
| 5592, 5593, 5594 | 2.8-9 intake `UNION` | 16 FATALs each |

**Regression gate:** `mvn -pl parse -Dtest=PantoTimeoutCorpusE3GateTest test` (auto-generates manifest if missing).

#### Construction-bucket tracker (74 timeouts)

Update **Status** as work lands. Counts are of CSV rows (all `timeout_513`). SQL lives in [panto_513_outstanding_issues.csv](../docs/rmcp-handoff/5.1.3-panto-outstanding/panto_513_outstanding_issues.csv); do not paste full queries here.

| Bucket | Rows | Construction | Likely hotspot (unprofiled) | Canary CSV row | Status |
|--------|-----:|--------------|-----------------------------|----------------|--------|
| 2.8-1 | 4 | Giant constant-row `UNION` lookup (~248 branches, no JOIN/CTE) | Walker AST hot path + scope reconstruction (**post-S5**); convert egress not dominant on probe | 475 | **Complete (E3)** |
| 2.8-2 | 20 | Long `UNION ALL` of PCM “convert” slices (~66–94 branches + `<…>` substitutions) | Walker hot path (C2.2 fixed dominant cost); ~~D1 defer-global-merge~~ **abandoned** | 1837 | **Complete (E3)** |
| 2.8-3 | 3 | Medium `UNION ALL` of typed attribute slices (N = 2–18) | Same set-op matching, smaller N | 1436 | **Complete (E3)** |
| 2.8-4 | 1 | Many CTEs, then `UNION ALL` of attribute categories + windows | CTE finalization **and** set-op matching | 1814 | **2.11** — row **1814** **W2** |
| 2.8-5 | 4 | Wide Student.final: many JOINs + nested derived tables ± source `UNION ALL` | Joins / derived tables; multifile also set-ops | 2325 (1 UNION; join-heavy probe) | **Complete (E3)** |
| 2.8-6 | 5 | Wide Acquia export: many JOINs + translations + one `UNION` | Joins / substitutions / one set-op | 28 | **Complete (E3)** — rows **28–32, 41** **W1** (syntax_corrupt) |
| 2.8-7 | 5 | Student Address: nested agg + `UNION ALL` of address sources | Derived table + set-op + join | 605 | **2.11** Cluster B — E3 pass; **7 FATALs**/row (fast) |
| 2.8-8 | 1 | Small `UNION ALL` of identical-width “slice” SELECTs (N≈4) | Set-op even at small N, or `SELECT *` / bind expansion | 2110 | **Complete (E3)** |
| 2.8-9 | 7 | Two-branch `UNION` of wide CAST/substitution lists (intake) | Per-column substitution/interface, not branch count | 5592 | **2.11** — **5860–5863** **W2**; **5592–5594** Cluster B fast FATALs |
| 2.8-10 | 2 | Nested `SELECT *` + many `ROW_NUMBER() OVER` (Colleague PIT) | Star expansion / windows / nested scopes (**5261 has 0 UNION**) | 5261 | **Complete (E3)** |
| 2.8-11 | 2 | Long `WITH` chain (~20 CTEs) + dense `<downfillcolmap.*>` (~80k chars) | CTE / substitution / scope finalization (no UNION) | 4647 | **Complete (E3)** |
| 2.8-12 | 3 | Giant searched `CASE` (hundreds of `WHEN`) | `CASE` walk (row 130 already ~20s on 5.0.0-3) | 130 | **2.11** — row **130** **W2** canary |
| 2.8-13 | 3 | Many modest `CASE`s + large `IN (…)` / regexp (no set-ops) | Expression / IN-list resolution | 314 | **Complete (E3)** — rows **314–315** **W1** (syntax_corrupt) |
| 2.8-14 | 6 | `WITH funds_data` + many PCM LEFT JOINs + nested fund `CASE` (no UNION) | Join / dictionary / substitution | 4157 | **2.11** — dev-template rows **W2** |
| 2.8-15 | 1 | Star-join of many bound fulfillment tables (no UNION) | Many-join scope / bind keys | 4197 | **2.11** Cluster B — E3 pass; **32 FATALs** (fast) |
| 2.8-16 | 3 | Nested derived-table JOINs (Project Atlas, no UNION) | Nested scopes / joins | 2116 | **2.11** — row **2120** **W2** |
| 2.8-17 | 3 | Wide contact projection + translation JOINs + substitutions (no UNION) | Wide interface + substitutions | 5453 | **2.11** Cluster B — **5453–5454** fast FATALs |
| 2.8-18 | 1 | Donor intake: `WITH` + several JOINs + one `UNION` | Mix of CTE + set-op + joins | 6025 | **Complete (E3)** |

**Suggested order:** 2.8-1 → 2.8-2 (largest N, matches UNION-header hypothesis) → non-UNION canaries **2.8-10 (5261), 2.8-11 (4647), 2.8-12 (130), 2.8-15 (4197)** → remaining UNION-mix and join/substitution buckets.

**Algorithm families (for grouping code fixes):**

| Family | Buckets | Row count |
|--------|---------|----------:|
| Set-op interface / column-header walk **O(N²) on branches** | 2.8-1, 2.8-2, and smaller UNION families 2.8-3, 2.8-4, 2.8-5 (multifile), 2.8-7, 2.8-8 | 4 + 20 plus smaller UNION families — **fix track: B1 + C2.2** (showTrace removal); ~~D1~~ abandoned |
| Giant searched-`CASE` walk | 2.8-12 | 3 |
| Deep CTE + substitution finalization | 2.8-11 | 2 |
| Many-join / nested derived table / `SELECT *` / windows (0–1 UNION) | 2.8-5 (2325), 2.8-6, 2.8-10, 2.8-14, 2.8-15, 2.8-16 | many |
| Wide select-list + substitutions (few branches) | 2.8-9, 2.8-17 | 10 |

A bucket is **Complete** when every listed CSV row parses on 5.1.3 without the 90s abort (or has a documented blocker + active-stage capture), with extractor semantics unchanged except for separately approved defect fixes.

#### Bucket membership (CSV row + query name)

**2.8-1 — Giant constant-row `UNION` lookup (~248 branches).** `SELECT 'Afghanistan' AS country, … UNION SELECT 'Albania' …` with no JOIN, CTE, or substitutions. 5.0.0-3 already ~7s. Rows **475, 476** `EAB.Country` (ALR); **1827, 1828** `EAB.Country` (Enroll360).

**2.8-2 — Long `UNION ALL` of PCM convert slices (~66–94 branches + substitutions).** Repeated `SELECT 'field_name', <substitution>, partner_value, COUNT(*) FROM <[Enroll360]….> GROUP BY … UNION ALL`. Rows **1819** `DataOrgPilot.Enroll360.Partner Code Mapping.convert_sis`; **1820** `…convert_sis.tenant_v2`; **1821** `…convert_sis.Tenant.v2`; **1837, 1838, 1839, 1851** `Enroll360.Partner Code Mapping.convert`; **1860** `…convert_CRM_pilot_v1`; **1890, 1891** `…convert_sis`; **1897** `…convert_tenant`; **1927** `…convert_ug_applicants`; **1933** `…convert_ug_applicants_v2`; **1957** `…convert_ug_inquiries`; **1986** `INACTIVE_DataOrgPilot.…convert_sis.Tenant.v1`; **1987** `INACTIVE_DataOrgPilot.…convert_sis.wStudentType`; **1998, 2001** `INACTIVE_Enroll360.Partner Code Mapping.convert`; **2031** `INACTIVE_Enroll360.…convert_sis_pilot_v1`; **2054** `INACTIVE_Enroll360.…convert_ug_applicants`.

**2.8-3 — Medium `UNION ALL` of typed attribute slices.** Outer SELECT over `(SELECT … UNION ALL SELECT …)` with the same donor-attribute schema and a different literal `attribute_type`. Rows **1436** (18 `UNION ALL`), **1467** (6), **1439** (2) — all `par_intake_donor_attributes_AMS_V2.0`.

**2.8-4 — Many CTEs, then `UNION ALL` of attribute categories + windows.** `WITH` several `cte_*` (row_number / latest-record), then 10 `UNION ALL` of INNER JOIN slices. Row **1814** `Enroll360.Partner Processed Census Student Term Attributes.final`.

**2.8-5 — Wide Student.final.** Large distinct student projection, ~26–29 JOINs, nested `FROM (SELECT …)`, many `CASE`/`regexp_replace`. Multifile adds 5–9 `UNION ALL`. Rows **2201** `Enroll360.Student.final.multifile Bill test` (9 set-ops); **2444** `XWALK_TEST_Enroll360.Student.final.multifile.tenant` (9); **2352** `INACTIVE_Enroll360.Student.final.multifile_v4` (5); **2325** `INACTIVE_Enroll360.Student.final` (1 set-op, 29 joins — join-heavy probe after set-op cache).

**2.8-6 — Wide Acquia export.** `SELECT DISTINCT`, ~18–24 JOINs, many `<Field>` substitutions, nested derived tables (`CTS_PIVOT` is an alias, not SQL `PIVOT`), one `UNION`. Rows **28, 30, 31, 32** `PDP_Acquia_Export`; **41** `PDP_Acquia_Export_v2`.

**2.8-7 — Student Address.** `SELECT DISTINCT` of regexp/CASE-cleaned columns from `agg2`, one `UNION ALL`, 4–5 JOINs. Rows **605, 606** `ALR.Student Address.final`; **623** `ALR.Student Address.final.BryanCollege.ExcludeAdultHistorical`; **635** `EHC.ALR.Student Address.final`; **636** `Goshen_ALR.Student Address.final`.

**2.8-8 — Small slice `UNION ALL`.** Same `prelim.*` plus a different slice column, 4 branches, 3 JOINs. Row **2110** `Enroll360.Project Atlas Migration Checks.prelim_stud_data_sliced`.

**2.8-9 — Two-branch `UNION` of wide CAST/substitution lists.** Two similar wide SELECT lists (`UNION`, often not `ALL`), modest joins. Rows **5592, 5593** `src_intake_custom_contacts_PDPv0.2`; **5594** `src_intake_custom_contacts_PDPv0.2_test`; **5860** `par_intake_student_last_validated_PDP_ALR_V2_v2.0` (CTE + UNION); **5861, 5862, 5863** `par_intake_student_PDP_ALR_V2_v2.0`.

**2.8-10 — Nested `SELECT *` + windows (Colleague PIT).** Deep derived tables, `SELECT *`, 5–8 windows, passthrough substitutions. Rows **5216** `colleague_cat_course` (1 `UNION ALL`); **5261** `colleague_cat_section` (**0** set-ops — non-UNION canary).

**2.8-11 — Long WITH chain + downfill substitutions.** `WITH AA0_… AS (SELECT <downfillcolmap.out_…> …), …, ST_student_term_sweep` (~20 CTEs, ~80k chars). **No UNION.** Related to 2.5/2.7 Foundation downfill shape, but these timed out. Rows **4647** `Old STD`; **4725** `Updated INFA Student Academic Summary Intermediate_m__st_student_term_downfill_backfill__ Query`.

**2.8-12 — Giant searched `CASE`.** Almost no JOIN/UNION. Row **130** `transformation_query_applicants` (~998 `WHEN` in one `CASE` for `school_program`; 5.0.0-3 ~20s). Rows **4176** `MSUBozeman.ApplyResponder` and **4177** `MSUBozeman.CultivateResponder` (fixed-width `||` concatenation; two ~50-`WHEN` major maps duplicated for length calc).

**2.8-13 — Modest `CASE` + large `IN` / regexp.** Flat SELECT from one bound table (or one JOIN). Rows **314, 315** `transformation_query_inquiry`; **652** `ALR.Student Attributes.Final` (duplicated `CASE`/`IN` in SELECT and WHERE).

**2.8-14 — Student Year Funds CTE + PCM joins (no UNION).** CTE wrapping `SELECT DISTINCT` with 6–12 JOINs to partner-code-mapping tables and several amount `CASE`s. Rows **4157, 4158** `DataOrgPilot.Enroll360.StudentYearFundsLogicTesting`; **4163** `FundAmountLogicTesting_Enroll360.Student Year Funds.final`; **4164** `funds logic testing`; **4170** `TESTING_DataOrgPilot.Enroll360.StudentYearFundsLogicTesting`; **4171** `TESTING_DataOrgPilot.Enroll360.StudentYearFundsLogicTestingDT`.

**2.8-15 — Star-join of bound fulfillment tables (no UNION).** Many `COUNT(DISTINCT …)` with 19 LEFT JOINs of `<[enrollment_services].[…]>` and repeated 4-key `COALESCE` predicates. Row **4197** `Migration checks fulfillment counts`.

**2.8-16 — Nested derived-table JOINs (Project Atlas, no UNION).** Parenthesized inner SELECT with window/`CASE`, then JOINs to bound PDP tables. Rows **2116** `Enroll360.Project Atlas Migration Checks.st_sta_data`; **2117** `…st_sta_data_undeduped_apps`; **2120** `…stud_race_eth`.

**2.8-17 — Wide contact projection + substitutions (no UNION).** One SELECT, 3–4 JOINs, many `<First Name>`-style tokens. Rows **5453, 5454** `con_contact_assigned_cappex_common_format`; **6542** `Hofstra  YouVisit INQ International` (~39 substitutions, some `IN` lists).

**2.8-18 — Donor intake mix.** Two CTEs (`count_intake_dt_cte`, `donor_email_CTE`), several JOINs, one `UNION` (test-email retain). Row **6025** `pdp_ams_rsc_donor_intake`.

The eight closed 2.9 degradations are **not** in this tracker.

#### Problem

A substantial set of queries takes longer than 90 seconds through the 5.1.3 parser and is significantly slower than the production 5.0.0-3 parser. The current end-to-end duration does not show whether time is spent building the parse tree, walking it and producing semantic diagnostics, or materializing and returning the access-object results. Do not optimize based only on total elapsed time or assume that every slow query has the same cause.

The identifying information, 5.0.0-3 timings, 5.1.3 timeout observations, and full SQL text for every known case are maintained in [panto-513-parse-timeouts-2026-08-19.md](../docs/rmcp-handoff/5.1.3-panto-outstanding/panto-513-parse-timeouts-2026-08-19.md) and the machine-readable [panto_513_outstanding_issues.csv](../docs/rmcp-handoff/5.1.3-panto-outstanding/panto_513_outstanding_issues.csv) / [outstanding-issues-index.md](../docs/rmcp-handoff/5.1.3-panto-outstanding/outstanding-issues-index.md). That brief is the authoritative fixture index; do not duplicate its 74 full queries here.

#### Required stage breakdown

Record monotonic wall-clock duration, and CPU/allocation data where practical, for these non-overlapping stages:

1. **Parse phase:** input handoff through lexer/parser execution and parse-tree construction. If inexpensive to expose, report lexing and grammar parsing separately as a subordinate breakdown.
2. **Event-walker / semantic phase:** parse-tree walk, AST and symbol-table construction, source/interface resolution, semantic diagnostics, and final query/CTE reconciliation.
3. **Return phase:** construction, copying, serialization, or conversion of the final `Snippet` / access-object payload after walking is complete, through return to the caller. Keep diagnostic generation in stage 2; count only packaging and transfer work here.
4. **End-to-end control:** total caller-observed duration. Verify that stage totals plus explicitly measured overhead approximately reconcile with this value.

Place timing boundaries at stable pipeline ownership points rather than around test helpers or console output. Instrumentation must be opt-in and must not change normal parser output.

#### Investigation method

1. Run each supplied query against 5.1.3 and 5.0.0-3 under the same machine, JVM, heap, endpoint, parser flags, timeout, and input text.
2. Separate cold-start/class-loading measurements from warmed runs. Use at least one warm-up and multiple measured iterations for queries that complete within the timeout; report median and range, not a single run.
3. For runs stopped at 90 seconds, record the active stage and timeout as censored data rather than inventing a completed duration. Add a diagnostic/progress marker capable of identifying the active grammar rule or walker/finalizer operation without flooding logs.
4. Capture query size and shape metadata: characters, tokens, statement count, CTE count/depth, join count, set-operation count, nested expressions, substitutions, and emitted diagnostic count where available.
5. Group slow queries by dominant stage and recurring SQL shape. Minimize at least one representative query from each cluster while preserving the slowdown.
6. Profile the dominant stage for each cluster before editing code. Look for repeated full-tree scans, repeated dictionary merges/copies, diagnostic rescans, exponential ambiguity/recovery behavior, pathological lookahead, duplicate finalization, and expensive payload conversion.
7. Make the smallest optimization supported by the profile, preserve parser semantics, and rerun the same stage benchmark plus focused correctness tests.

#### Live query corpus

Use the CSV row number from the detailed brief as the stable case ID. All 74 listed cases completed on 5.0.0-3 but were killed after approximately 90 seconds on 5.1.3 without a completed payload.

| Corpus | Query / fixture source | 5.0.0-3 total | 5.1.3 total | Dominant 5.1.3 stage | Status / notes |
|--------|------------------------|---------------|-------------|----------------------|----------------|
| 74 Panto rows | [Detailed timeout brief](../docs/rmcp-handoff/5.1.3-panto-outstanding/panto-513-parse-timeouts-2026-08-19.md) | Every row completes; typically hundreds of ms to a few seconds, with row 130 near 20s | Every row aborted at approximately 90s | Unknown until staged instrumentation | Construction clustered into **2.8-1 … 2.8-18** (see tracker above); profile per bucket |

Retain both the original full query and any minimized reproduction. Redact or parameterize sensitive literals without changing the structural feature responsible for the timing.

#### Suggested deliverables and tests

1. Opt-in stage-timing instrumentation with a structured result containing parse, walker/semantic, return, overhead, and total durations. **Done** — see `ParseLatencyDiagnosticService`.
2. A repeatable benchmark or integration-test harness that runs one query or the full corpus and emits machine-readable results for version comparison. **Done** — `PantoTimeoutCorpusE3GateTest` + `tools/benchmark_panto_timeout_rows.py`.
3. Characterization results for every supplied query, including timeout stage, warm/cold distinction, query-shape metadata, and 5.0.0-3 versus 5.1.3 ratio.
4. A minimized fixture and focused performance regression check for each distinct root-cause cluster. Keep hard timing assertions out of ordinary noisy unit tests unless the threshold has ample headroom; use operation counts or dedicated benchmark thresholds where more stable.
5. Existing six extractor outputs and diagnostics must remain semantically unchanged for successful optimizations, except for separately approved defect fixes.
6. A before/after report showing stage-level and end-to-end improvement, along with any query that remains above 90 seconds and why.

#### Acceptance

- Every supplied slow query is assigned a dominant stage or an explicitly documented unresolved measurement result.
- Every row in the 74-query timeout brief completes on 5.1.3 without the 90-second abort, **or** has a documented blocker routed to **2.11** (**W-track** / Cluster B) with active-stage capture.
- Stage boundaries reconcile with caller-observed total time closely enough to identify where the missing time is spent.
- 5.0.0-3 and 5.1.3 comparisons use equivalent runtime settings and distinguish cold from warm execution.
- Each implemented optimization is justified by profile evidence and has a focused performance regression check plus correctness coverage.
- No optimized query loses AST, interface, symbol-table, table-dictionary, substitution, message, or diagnostic content.
- The final report quantifies improvements and records remaining cases over 90 seconds; completion does not require speculative changes where profiling finds no safe optimization.
- Phase 2.1–2.7 behavior remains unchanged unless a measured shared root cause requires a separately reviewed correction.

#### Out of scope (2.8)

- Treating the existing 2.5 guardrail as representative of the broader slow-query corpus.
- Raising timeouts or suppressing diagnostics as the primary performance fix.
- Comparing versions on different hardware, JVM settings, endpoints, parser flags, or materially different SQL text.
- Rewriting customer queries solely to avoid a parser performance defect.
- Committing to a universal duration target before the corpus and dominant stages are measured.

---

### 2.9 — Adjudicate remaining 5.0.0-3 versus 5.1.3 differences

**Kind:** Investigation first; defect only after semantic adjudication

**Status:** **Complete** (2026-09-01)

**Component:** grammar, recovery, semantic diagnostics, substitution/table-source collection, or comparison policy — assign ownership per case after dual-parse evidence

The detailed corpus is [panto-tabledict-degradations-2026-08-19.md](../docs/rmcp-handoff/5.1.3-panto-outstanding/panto-tabledict-degradations-2026-08-19.md). All eight Panto degradation fixtures are **closed**. No parser changes required beyond documentation and comparison-policy updates.

#### Closure summary (2026-09-01)

| Cluster | Rows | Outcome | Policy doc |
|---------|------|---------|------------|
| A | 583, 2139 | CTE `table_alias` → `queryN`; physical tuples retained | [global-table-dictionary-cte-alias-policy.md](global-table-dictionary-cte-alias-policy.md) |
| B | 3870 | Same | same |
| C | 4648, 4726 | Same (query-substitution CTE body) | same |
| D | 3150 | Set-op FATALs canonical | [set-operation-interface-duplicate-output-names-policy.md](set-operation-interface-duplicate-output-names-policy.md) |
| E | 5455 | CTE + tuple on substitution key; local alias not in global `tableDictionary` | [global-table-dictionary-cte-alias-policy.md](global-table-dictionary-cte-alias-policy.md) |
| F | 5410 | Set-op FATALs canonical | [set-operation-interface-duplicate-output-names-policy.md](set-operation-interface-duplicate-output-names-policy.md) |

**Consumer rule:** Global `tableDictionary` is physical/tuple sources only under 5.1.3. Trace CTE and query-backed aliases via **`symbolTable` → `table_alias` → `def_queryN`**. Do not restore 5.0.0-3 CTE keys in the physical dictionary.

**Timeout routing:** the 74 cases where 5.1.3 exceeded 90 seconds while 5.0.0-3 completed are **closed under 2.8** (E3). Residual walker/diagnostic issues on those rows → **2.11**.

#### Investigation-first rule

For every difference, first explain:

1. The exact parse, AST, scope, dictionary, interface, and diagnostic behavior in each version.
2. Whether 5.1.3 lost information, moved it to a more accurate documented bucket, or correctly rejects SQL that 5.0.0-3 accepted through permissive recovery.
3. The first parser/walker operation where outputs diverge and the root cause of that divergence.
4. Which behavior should be canonical and why. Compatibility with 5.0.0-3 is evidence, not automatic proof that 5.0.0-3 is correct.

Do not implement parity changes until this explanation is recorded for the case.

**Comparison-policy note (post–2.7 dual-parse):** A CTE name that 5.0.0-3 listed in global `tableDictionary` but 5.1.3 omits is **not** automatically a regression. In 5.1.3, WITH members are intentionally registered on the enclosing scope’s **`table_alias`** as **`{cte_name=queryN}`** bindings under `def_queryN` (e.g. row 583: `latest_applications=query1`, `activity_prospect_map=query2`, `campus_visit_activity=query3`). That symbol-table wiring is a **5.1.3 enhancement** — richer documented row-source registration than 5.0.0-3’s CTE promotion into the physical dictionary. Score as improvement when `table_alias` → `queryN` evidence is present; score as defect only when physical/tuple sources or CTE symbol-tree registration is functionally absent.

#### Initial residual clusters

| Cluster | Live case | Difference to adjudicate |
|---------|-----------|---------------------------|
| A | 583, 2139 | **Complete (2026-09-01)** — CTE names absent from global `tableDictionary`; `table_alias` → `queryN` on enclosing scope. |
| B | 3870 | **Complete (2026-09-01)** — `student_term_crm=query7`. |
| C | 4648, 4726 | **Complete (2026-09-01)** — `ST_student_term_sweep=query0`. |
| D | 3150 | **Complete (2026-09-01)** — Set-op FATALs canonical. |
| E | 5455 | **Complete (2026-09-01)** — CTE + tuple substitution key; not a collection defect. |
| F | 5410 | **Complete (2026-09-01)** — Set-op FATALs canonical. |

#### Tests and deliverables

1. Fresh dual-parse outputs for each live case using identical SQL, endpoint, flags, and folded-case settings.
2. Message text, code, stage, location, and offending symbol for every 5.1.3-only FATAL.
3. One minimized fixture per independently confirmed root cause, plus the unchanged full live query as an integration regression.
4. A written adjudication table: `5.0.0-3 behavior`, `5.1.3 behavior`, `canonical behavior`, `root cause`, `fix or comparison-policy action`.
5. At minimum, lock `messages` and `tableDictionary`; also lock `sqlTree`, `symbolTable`, and `interface` when the divergence occurs during parse recovery or walking.
6. If 5.1.3 is more correct, preserve it and document why the legacy output should not be restored. If 5.1.3 is a regression, fix the owning grammar/walker path without deleting newer nested evidence.

#### Acceptance — **met** (2026-09-01)

- Every initial residual cluster has an explicit root-cause and canonical-behavior decision.
- Rows **3150** and **5410**: FATALs justified and retained — [set-operation-interface-duplicate-output-names-policy.md](set-operation-interface-duplicate-output-names-policy.md).
- Rows **583, 2139, 3870, 4648, 4726, 5455**: no functional source loss; CTE/tuple policy — [global-table-dictionary-cte-alias-policy.md](global-table-dictionary-cte-alias-policy.md).
- Dual-parse evidence captured 2026-09-01; comparison tools should adopt post–2.7 scoring.
- Phase 2.8 (timeouts) **complete** (2026-09-02); residuals → **2.11**.

#### Out of scope (2.9)

- Schema qualification or folded-case spelling of the same physical source.
- Removing richer 5.1.3 nested `def_*` or query-dictionary evidence to make raw output resemble 5.0.0-3.
- Timeouts and performance regressions owned by **2.8** (**complete**); fast-FATAL / subMap residuals → **2.11**.
- Assuming all comparison differences share the 2.7 finalizer root cause.

---

### 2.10 — SLL→LL two-stage prediction policy (`SqlParserAccess`)

**Kind:** Performance / correctness (production parse path)

**Status:** **Not started**

**Component:** `SqlParserAccess` / ANTLR prediction mode

**Problem:** `ParseLatencyDiagnosticService` forces **SLL first, then LL on cancellation**; production `SqlParserAccess` uses **LL only**. On some live rows (e.g. **4176** MSU Bozeman fixed-width export) diagnostic parse is **~54 ms** vs production **~5 s**. A prior attempt to enable SLL-only in `SqlParserAccess` produced **silent partial parses** (`tableDictKeys=0`) because `ParseErrorCollector` recovers instead of failing fast.

**Goal:** Land a **safe** two-stage policy: try SLL, fall back to LL on ambiguity/cancellation, and **reject** partial trees when prediction fails (compatible bailout strategy for ANTLR 4.13 in this repo).

**Tests / gates:**

- `SqlParserAccessSllPredictionSafetyTest` — representative rows + optional full corpus sweep
- `ClusterBSllRegressionTest` — **10** Cluster B rows adjudicated as SLL-only E3 false positives (`cluster-b-sll-regression-rows.json` → `pending_2_10_csv_rows`)
- Row **4176** / **4177** production-path regression (`ParseLatencyDiagnosticTest`)
- After 2.10 lands: E3 `walkerFatalCount == 0` (or parity with access path) on `pending_2_10_csv_rows`
- No increase in `walkerFatal` or empty `tableDictionary` on corpus sample

**Pending fixes (Cluster B → 2.10, adjudicated 2026-09-02):**

Frozen SQL fixtures already in `sql/csv-row-<n>.sql`. Index: `cluster-b-sll-regression-rows.json`.

| csv_row | Bucket | SLL fatals | Default (LL) fatals | Symptom |
|--------:|--------|----------:|--------------------:|---------|
| 605–636 | 2.8-7 Student Address | 7 each | 0 | SLL builds `def_query0` without `agg2=union6` alias |
| 5453–5454 | 2.8-17 wide contact | 3 each | 0 | SLL drops `table_alias` map present under default |
| 5592–5594 | 2.8-9 intake UNION | 16 each | 0 | SLL `def_query0` vs default `def_union2` |

**Deferred (not 2.10-only):** row **4197** — `SqlParserAccess` parse failure at line 85; remains **2.11**.

**Tooling:** `python3 tools/compare_cluster_b_sll_vs_default.py` — regenerate comparison; `SllVsDefaultClusterBProbe` — per-row probe.

**Out of scope:** Changing grammar; altering diagnostic-only SLL behavior in `ParseLatencyDiagnosticService` unless needed for parity.

---

### 2.11 — Panto timeout corpus residuals (W-track + fast-FATAL rows)

**Kind:** Defect / investigation (walker correctness + semantic diagnostics on live RMCP SQL)

**Status:** **In progress** — **W1** done (§2.8 W-track); **W2–W5** + Cluster B open

**Component:** event walker (`AbstractASTWalkerHelper` / `exitSql` null `subMap`), parse-error recovery, semantic FATAL emission

**Background:** Phase **2.8** closed the **90 s timeout** problem (E3: **74/74** rows). Residual semantic work:

| Cluster | Count | Symptom | Tracker |
|---------|------:|---------|---------|
| **A — former subMap skip** | **20** | Pre-**W1**: `subMap` NPE after parse-error recovery; post-**W1**: syntax_corrupt rows clean; **legitimate_complex** may still need **W2** | §2.8 **W-track** + [panto-submap-walker-skip-list.json](../docs/rmcp-handoff/5.1.3-panto-outstanding/panto-submap-walker-skip-list.json) (cleared) |
| **B — fast-FATAL** | **11** | Finish in **&lt; 200 ms** but emit one or more FATAL diagnostics | §2.8 E3 fast-FATAL table — **10** adjudicated → **2.10**; **4197** deferred |

**Suggested approach:**

1. **Cluster A** — **W1** done for parse-phase failures; **W2** null-guard for rows that still walk; **W5** document `syntax_corrupt` as author-error.
2. **Cluster B** — **Adjudicated (2026-09-02):** all 11 E3 fast-FATAL rows are SLL-vs-default divergences. **10** (`605–636`, `5453–5454`, `5592–5594`) are SLL-only false positives vs production `SqlParserAccess` → **`cluster-b-sll-regression-rows.json`** / **2.10**. Row **4197** has production parse failure at line 85 → remains **2.11**.
3. E3 already includes all **74** rows post-**W1**; add zero-FATAL assertions when Cluster B is adjudicated (**W4**).
4. **Cleanup** — Delete one-off triage CLIs after **W2** + Cluster B classification (**2.11.4**). **Keep** `ParseLatencyDiagnosticService`, `WalkerHotspotProfiler`, and E3 gate tests.

| Step | Task | Status |
|------|------|--------|
| 2.11.1 | **Cluster A** — **W2** null-guard + **W5** syntax_corrupt classification | **Partial** — **W1** + **W2** done |
| 2.11.2 | **Cluster B** — fast-FATAL (11 rows) | **Adjudicated** — **10** routed to **2.10** (SLL-only E3 false positives); **4197** deferred (production parse failure) |
| 2.11.3 | E3 / skip-list hygiene | **Done** — **74/74** in E3; skip list cleared |
| 2.11.4 | Remove one-off investigation CLIs after classification | **Partial** — some probes remain (`PantoSubMapRowProbe`) |

**Deliverables:**

- Updated skip list (shrinking as rows are fixed or reclassified)
- Focused regression test per confirmed fix
- Bucket tracker rows currently marked **2.11** move to **Complete** when every listed CSV row in that bucket is clean
- One-off triage CLIs removed once classification is complete (**2.11.4**)

**Out of scope:** Re-opening **2.8** timeout work; **D1** / **D2** / **C2** micro-opts (all abandoned).

---

## Phase 3 — Snowflake PARSE functions with `:` field access

**Kind:** Enhancement (grammar + AST construction; at least `PARSE_URL`)

**Status:** Not started

**Component:** grammar / parse strategy, then column resolution on function arguments

### Problem

Snowflake `PARSE_URL` returns an OBJECT. Callers then use the **single-colon** semi-structured field operator (`:field`) and often a `::type` cast:

```sql
PARSE_URL(ht.hit_url, 1):scheme::varchar
```

The current grammar does not accept `:` immediately after a function call. Observed on the reproducing fragment below:

- FATAL `REPORT_ERROR` near `:` (`NoViableAltException`): unexpected input: `:`
- Recovery then poisons the surrounding `CASE` (`Invalid syntax near 'WHEN'`)
- Follow-on walker `APPLICATION_ISSUE_FATAL`

`:` here is **not** a bind variable, a type-cast (`::`), or a qualified identifier (`.`). It is Snowflake VARIANT/OBJECT path access. `PARSE_URL` is the motivating function; other PARSE helpers (`PARSE_JSON`, `PARSE_XML`, `PARSE_IP`, `TRY_PARSE_JSON`) return semi-structured values that use the same `:` operator. Minimum scope is `PARSE_URL`; extend to other PARSE functions if the construction is shared.

### Reproducing predicand

Use this exact fragment (as a SELECT-list predicand inside `LEFT` / `CONCAT` / `COALESCE` / `CASE`):

```sql
LEFT(
                   CONCAT(
                     PARSE_URL(ht.hit_url, 1):scheme::varchar
                     , '://'
                     , PARSE_URL(ht.hit_url, 1):host::varchar
                     , '/'
                     , COALESCE(parse_url(ht.hit_url, 1):path::varchar, '')
                   ), 200
                 )
```

Pieces that must all parse:

| Piece | Role |
|-------|------|
| `PARSE_URL(ht.hit_url, 1)` / `parse_url(...)` | Function (case-insensitive), two arguments |
| `ht.hit_url` | Ordinary qualified column reference **inside** the function argument list |
| `:scheme`, `:host`, `:path` | Single-colon field access on the OBJECT result |
| `::varchar` | Cast of the **field**, not of the function call |

Typical wrapping (as in production models):

```sql
CASE WHEN NOT CONTAINS(ht.hit_url, '://')
     THEN LEFT(CONCAT(
            PARSE_URL(ht.hit_url, 1):scheme::varchar, '://',
            PARSE_URL(ht.hit_url, 1):host::varchar, '/',
            COALESCE(parse_url(ht.hit_url, 1):path::varchar, '')
          ), 200)
     ELSE NULL
END AS clean_url
```

### AST — open (must decide in this phase)

The AST shape is **not** specified yet. This phase includes choosing and documenting it, then implementing against that choice.

Questions to answer before coding walkers/resolution:

1. Is `expr : identifier` a general postfix **semi-structured field access** on any predicand (preferred if `PARSE_JSON(...):foo` and `variant_col:foo` should share a node), or a dedicated production only for PARSE_* function calls?
2. How do `:field` and `::type` compose? Expected reading of `PARSE_URL(...):scheme::varchar` is `( (PARSE_URL(...)) : scheme ) :: varchar` — field access first, then cast.
3. Are chained paths in scope (`PARSE_URL(...):query:foo`, `PARSE_URL(...):parameters:utm_source`)? If yes, is that a chain of the same postfix node?
4. Bracket / string-key forms (`PARSE_URL(...)['host']`, `PARSE_URL(...):"host"`) — in or out of this phase?
5. What node names / payload fields should consumers see (function name, args, path steps, cast type) so `derivation` / location references stay stable?

Record the chosen AST in parser docs (and a fixture JSON) as part of acceptance. Do not silently pick a shape that makes `ht.hit_url` unresolvable.

### Expected behavior

1. **Grammar:** `PARSE_URL(<predicand>, <literal>) : <identifier> [ :: <type> ]` is a valid predicand. Function name is case-insensitive.
2. **Column references in the function list:** `ht.hit_url` (and any other args) use the same column-resolution and location-reference paths as a normal function argument. Table alias `ht` must resolve; `hit_url` is a column of that source, not a path step.
3. **Field names** (`scheme`, `host`, `path`) are path keys on the PARSE result, **not** column references to `ht`.
4. The whole expression is a scalar predicand usable wherever a predicand is legal.

### Required tests — predicand locations

Parse (no FATAL / no recovery on `:`) and resolve inner columns in at least:

| Location | Sketch |
|----------|--------|
| SELECT list | the `LEFT(CONCAT(...))` example, with and without `AS clean_url` |
| `CASE` result / `CASE WHEN` | the `clean_url` `CASE` wrapper above |
| Function argument | already nested in `CONCAT` / `COALESCE` / `LEFT`; also a lone `PARSE_URL(col, 1):host::varchar` |
| WHERE | `WHERE PARSE_URL(ht.hit_url, 1):host::varchar = 'example.com'` |
| JOIN ON | `ON PARSE_URL(a.url, 1):host = PARSE_URL(b.url, 1):host` |
| ORDER BY / GROUP BY | predicand in each |

Plus argument-list column-reference tests:

- Qualified `ht.hit_url` resolves to table/alias `ht`, column `hit_url`
- Unqualified `hit_url` when `ht` is the only source
- Two sources: unqualified `hit_url` remains ambiguous if both expose it
- Nested: `PARSE_URL(COALESCE(ht.hit_url, other.url), 1):path::varchar` resolves both columns

### Acceptance

- The reproducing `PARSE_URL` / `:` / `::varchar` fragment (and `clean_url` `CASE` wrapper) no longer fails at `:`.
- Chosen AST is written down (docs + at least one golden parse JSON).
- Predicand-location tests and inner column-resolution tests above pass.
- `::varchar` still parses as a cast (do not treat `:` of `::` as field access).
- Phases 1–2 behavior unchanged.

### Out of scope (unless the AST choice makes them free)

- Rewriting models to `GET_PATH(PARSE_URL(...), 'scheme')` or `PARSE_URL(...)['scheme']` as a workaround.
- Full VARIANT JSON path language (`:a.b[0].c`, flattening) beyond what this PARSE_URL example needs — call that a later phase if the general postfix `:` node is introduced.
- Other fatals in the same model unrelated to this construction.

---

## Phase 4 — Snowflake DATEADD date-part keywords vs column resolution

**Kind:** Defect (`UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES` / `UNRESOLVED_UNQUALIFIED_COLUMNS` / `AMBIGUOUS_COLUMN_REFERENCE`)

**Status:** Not started

**Component:** unresolved-column handler (Snowflake constant-function filter); possibly grammar if date-part is still parsed as an identifier

### Problem

Snowflake `DATEADD` / `DATEDIFF` (and related date functions) take a **date-part keyword** as the first argument (`year`, `month`, `day`, …). The walker currently treats that token as an **unqualified column**. That is harmless only when some visible source happens to expose a column of the same name. It fails when the source is a subquery/CTE that has no such column, and it can also raise **ambiguous column** when several sources do.

`year` is not a column in these calls. It is a date-part, analogous to how `CURRENT_TIMESTAMP` is excluded from column resolution.

### Reproducing expression

```sql
CASE WHEN DATEADD(year, DATEDIFF(year, fec.last_load_dt, fec.conversion_dt), fec.last_load_dt) >= TO_DATE(fec.conversion_dt)
           THEN DATEDIFF(year, fec.conversion_dt, fec.last_load_dt)
           ELSE DATEDIFF(year, fec.conversion_dt, fec.last_load_dt) - 1
      END AS conversion_years_prior_to_refdate
```

`fec` is subquery/CTE `first_conversion_CTE`. Its interface has `stream_key`, `person_id`, `current_grade_level`, `conversion_dt`, `credit_fiscal_year`, `first_conversion`, `last_load_dt` — **no `year` column**.

Observed on the reproducing expression:

```
Unresolved unqualified column reference(s): [year [(l:136 c:24), (l:136 c:39), (l:137 c:25), (l:138 c:25)]]
FATAL: Unqualified column 'year' at (l:136 c:24) was not found in output interface of any visible query alias [..., fec, ...]
(UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES, ast-walk)
```

The four `year` hits are the date-parts of `DATEADD(year, …)` and three `DATEDIFF(year, …)` calls. Real columns (`fec.last_load_dt`, `fec.conversion_dt`) are already qualified and should keep resolving.

Related (same pattern, different symptom): when several joins expose a column named `year`, the walker may report `AMBIGUOUS_COLUMN_REFERENCE` for the **date-part** `year` inside `DATEADD`/`DATEDIFF`. That is still wrong — the date-part is not a column reference.

### Suggested approach

Extend the **Snowflake constant-function filter** in the unresolved-column handler (the path that already skips `CURRENT_TIMESTAMP` and similar), **but do not treat `year` as a global constant**.

Restrict the skip to **date-part argument context** of Snowflake date functions:

- In `DATEADD(year, …)` / `DATEDIFF(year, …)` (and the other date functions in scope), `year` is **excluded from column resolution**, same idea as `CURRENT_TIMESTAMP`.
- Bare `year` **not** in that context still goes through normal resolution: bind to a column if one exists, or throw unresolved/ambiguous as today.

A global “`year` is never a column” filter would hide real columns named `year`.

Likely function list (confirm against Snowflake docs; start with the two in the example):

| Function | Date-part position |
|----------|-------------------|
| `DATEADD` / `TIMEADD` / `TIMESTAMPADD` | 1st arg |
| `DATEDIFF` / `TIMEDIFF` / `TIMESTAMPDIFF` | 1st arg |
| `DATE_TRUNC` | 1st arg |
| `DATE_PART` / `EXTRACT` | 1st arg (or `EXTRACT(YEAR FROM …)` form if already handled) |
| `LAST_DAY` | optional 2nd arg |

Date-part tokens to recognize at least: `year`, `quarter`, `month`, `week`, `day`, `hour`, `minute`, `second` (plus common aliases if the grammar already folds them). Case-insensitive.

If the grammar currently parses the date-part as a column identifier, either retag it as a date-part / keyword in that argument slot, or keep the identifier and skip it in the unresolved-column handler **only when the parent call is one of these functions and the token is that date-part argument**. The context restriction is the important part.

### Expected behavior

1. In the reproducing `CASE`, none of the four `year` tokens are column references. No `UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES` / `UNRESOLVED_UNQUALIFIED_COLUMNS` / `AMBIGUOUS_COLUMN_REFERENCE` for them.
2. `fec.last_load_dt` and `fec.conversion_dt` still resolve against `fec`.
3. `SELECT year FROM fec` (no date function) still unresolved if `fec` has no `year` column.
4. `SELECT year FROM t` still binds to column `year` when `t` has that column.
5. `DATEADD(year, 1, fec.last_load_dt)` with a real column also named `year` on `fec` must **not** steal the date-part into a column ref; a later `fec.year` or bare `year` in SELECT still can.

### Suggested tests

| Case | Result |
|------|--------|
| The exact `CASE WHEN DATEADD(year, DATEDIFF(year, fec.last_load_dt, fec.conversion_dt), …)` against subquery `fec` with no `year` column | Parse + resolve; no unresolved `year` |
| Same with `month` / `day` date-parts | Same |
| `SELECT year FROM fec` (no date fn, no column) | Still unresolved |
| `SELECT year FROM t` where `t.year` exists | Column bind |
| `DATEADD(year, 1, dt)` when some other visible source has a `year` column | No ambiguous `year` on the date-part |
| `DATEDIFF(year, a, b)` nested inside `DATEADD(year, …)` | Both date-parts skipped |
| `CURRENT_TIMESTAMP` skip unchanged | No regression |

### Acceptance

- The reproducing `CASE WHEN DATEADD(year, DATEDIFF(year, …), …)` expression no longer fatals on unqualified `year` at the date-parts.
- Queries where multiple sources expose a `year` column no longer flag **date-part** `year` inside `DATEADD`/`DATEDIFF` as ambiguous.
- Bare `year` outside date-function date-part position still resolves or errors as a column.
- `CURRENT_TIMESTAMP` (and other existing constant-function skips) unchanged.
- Phases 1–3 unchanged.

### Out of scope

- Rewriting models to `DATEADD('year', …)` string literals (Snowflake accepts both; not required of authors).
- Treating `YEAR(date)` / `MONTH(date)` extract *functions* (different syntax).
- Unrelated fatals on the same models (e.g. later qualified columns on other aliases).

---

## Phase 5 — Snowflake ARRAY syntax and functions

**Kind:** Enhancement (grammar + resolution; several independent constructions)

**Status:** Not started — work 5.1–5.12 independently where syntax does not share a production

**Theme:** Teach PSS to parse and resolve Snowflake `ARRAY` / VARIANT array functions and related syntax that already appear in RMCP DBT models (and the rest of the Snowflake array surface those models imply).

Each subtask is one **independent construction** (function family or unique secondary syntax). Do not block 5.2 on 5.1, etc., unless a later step truly reuses an earlier production (e.g. 5.1 argument arrays may be `ARRAY_CONSTRUCT` from 5.2).

### Shared test contract (every 5.x)

Exemplar tests are **required** for each subtask. Construct them in the usual PSS extractor/golden style:

1. **Happy path:** parse with **no diagnostics and no FATAL / recovery**. Assert that, then assert **full-golden strings** for the typical **six extractor objects**:
   - `substitutions`
   - `symbolTable`
   - `interface`
   - `tableDictionary`
   - `sqlTree`
   - `messages`
2. **Locations:** at least one happy-path fixture for **every SQL and DML site** where that construction is permitted. If a site is illegal in Snowflake (e.g. `ARRAY_AGG` directly in `WHERE`), do **not** require a silent parse; add a negative test only if the grammar must reject it. Default location matrix (drop rows that do not apply to the construction):

   | Kind | Sites |
   |------|--------|
   | Query | SELECT list, `WHERE`, `HAVING`, `GROUP BY`, `ORDER BY`, `JOIN ON`, `CASE WHEN` / `THEN` / `ELSE`, CTE body, subquery, `QUALIFY` |
   | DML | `INSERT … VALUES`, `INSERT … SELECT`, `UPDATE SET`, `UPDATE … WHERE`, `DELETE … WHERE`, `MERGE` `ON` / `WHEN MATCHED THEN UPDATE` / `INSERT` |

3. **Column references (critical argument positions):** for each construction, cover:
   - **Qualified** refs (`t.col`) that bind
   - **Unqualified** refs that bind when a single source exposes the column
   - **Unresolved** unqualified / qualified refs → expected diagnostics (`UNRESOLVED_UNQUALIFIED_COLUMNS`, `UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES`, `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE`)
   - **Ambiguous** unqualified refs when two visible sources expose the same name
4. Goldens must show those column refs in `symbolTable` / `tableDictionary` / location data, not only a clean `messages` array.
5. Function names are case-insensitive (`ARRAY_CONTAINS` / `array_contains`).
6. **Compound nesting test (required once 5.3, 5.9, and 5.10 exist):** add a fixture based on this exact SELECT-list statement. It nests `ARRAY_AGG` + `WITHIN GROUP` (5.3) inside `ARRAY_DISTINCT` (5.10) inside `ARRAY_TO_STRING` (5.9). Happy path: no FATAL / no recovery; six extractor goldens; resolve `source_list.all_src_bln_listagg` and the `WITHIN GROUP` keys (`contact_key`, `all_src_bln_listagg`).

```sql
                ARRAY_TO_STRING(ARRAY_DISTINCT(ARRAY_AGG(source_list.all_src_bln_listagg)
                                                    WITHIN GROUP (ORDER BY contact_key,all_src_bln_listagg)), '|') AS all_sources_blended
```

Individual 5.x tests may land first; this compound case is an **integration** golden, not a substitute for per-function fixtures.

### Subtask tracker

| Step | Construction | Unique secondary syntax | Status |
|------|----------------|-------------------------|--------|
| 5.1 | `ARRAY_CONTAINS` | VARIANT value (`::VARIANT`, `TO_VARIANT`, chained casts) | Not started |
| 5.2 | `ARRAY_CONSTRUCT` | Variadic / empty; compact and structured variants | Not started |
| 5.3 | `ARRAY_AGG` | `DISTINCT`, `WITHIN GROUP (ORDER BY …)`, `OVER (…)` | Not started |
| 5.4 | Array literals and subscripts | `[ … ]`, `arr[n]`, `GET` | Not started |
| 5.5 | `FLATTEN` | `LATERAL FLATTEN`, named args `INPUT` / `PATH` / `OUTER` / `RECURSIVE` / `MODE` | Not started |
| 5.6 | `ARRAY_UNION_AGG` / `ARRAY_UNIQUE_AGG` | Aggregate-of-arrays (not `ARRAY_AGG`) | Not started |
| 5.7 | Binary / set array ops | `ARRAY_CAT`, `ARRAY_EXCEPT`, `ARRAY_INTERSECTION`, `ARRAYS_OVERLAP`, `ARRAYS_ZIP`, `ARRAYS_TO_OBJECT` | Not started |
| 5.8 | Element mutate | `ARRAY_APPEND` / `PREPEND` / `INSERT` / `REMOVE` / `REMOVE_AT` | Not started |
| 5.9 | Transforms with extra args | `ARRAY_TO_STRING`, `ARRAY_SORT`, `ARRAY_SLICE`, `ARRAY_POSITION`, `ARRAY_REPEAT`, `ARRAY_GENERATE_RANGE` | Not started |
| 5.10 | Unary array functions | `ARRAY_SIZE`, `ARRAY_COMPACT`, `ARRAY_DISTINCT`, `ARRAY_FLATTEN`, `ARRAY_REVERSE`, `ARRAY_MAX`, `ARRAY_MIN` | Not started |
| 5.11 | Array conversion / predicates / split | `TO_ARRAY`, `AS_ARRAY`, `IS_ARRAY`, `SPLIT`, `STRTOK_TO_ARRAY` | Not started |
| 5.12 | Higher-order array fns | `FILTER` / `TRANSFORM` / `REDUCE` lambdas (`e -> expr`) | Not started |

---

### 5.1 — `ARRAY_CONTAINS` and VARIANT syntax

**Problem:** `ARRAY_CONTAINS(<value>, <array>)` is a boolean predicand. For semi-structured arrays the value must be a **VARIANT**. Models use `::VARIANT`, `::variant`, `TO_VARIANT(...)`, and chained casts such as `col::INTEGER::VARIANT` / `CAST(x AS INT)::variant`.

Example shapes:

```sql
-- JOIN ON
ARRAY_CONTAINS(mail_offer_history.campaign_product_id_src ::INTEGER::VARIANT, strm.legacy_product_types)

-- WHERE
ARRAY_CONTAINS('paper'::variant, strm.channel)

-- TO_VARIANT + SPLIT as array arg
ARRAY_CONTAINS(TO_VARIANT(x), SPLIT(col, '|'))
```

**Grammar / AST notes (open):** value vs array argument order; VARIANT cast as postfix `::` on the **value** (and possibly the array); do not treat `::` as Phase 3 `:` field access.

**Locations:** boolean sites (`WHERE`, `ON`, `CASE WHEN`, `HAVING`, `QUALIFY`) plus SELECT-list boolean; DML `WHERE` / `MERGE ON`.

**Column refs:** both arguments (`value_expr` and `array`). Unresolved `strm.channel` vs resolved `strm.legacy_product_types`.

---

### 5.2 — `ARRAY_CONSTRUCT` (and compact / structured)

**Problem:** `ARRAY_CONSTRUCT( [ expr [, ...] ] )` builds an ARRAY from zero or more predicands. Unique syntax: **empty** `ARRAY_CONSTRUCT()`, mixed types including SQL NULL, and use as hash input (`MD5(TO_VARCHAR(ARRAY_CONSTRUCT(...)))`).

Related unique signatures (same subtask unless they need a separate production):

- `ARRAY_CONSTRUCT_COMPACT(...)` — drops SQL NULLs
- `ARRAY_CONSTRUCT_STRUCTURED(<type>, ...)` — explicit element type

Examples:

```sql
MD5(TO_VARCHAR(ARRAY_CONSTRUCT(col_a, col_b, NULL)))
ARRAY_CONSTRUCT_COMPACT(a, NULL, b)
```

**Locations:** SELECT list, `INSERT`/`UPDATE` array columns, join/hash expressions, nested inside `TO_VARCHAR` / `MD5`.

**Column refs:** each variadic element. Unresolved element must diagnostic without killing sibling elements’ resolution.

---

### 5.3 — `ARRAY_AGG` with `WITHIN GROUP` and `OVER`

**Problem:** Aggregate/window array pivot. Current scan fails on `ARRAY_AGG` / `OVER` (unexpected `ARRAY_AGG` and `OVER`).

Syntax:

```sql
ARRAY_AGG( [ DISTINCT ] <expr> ) [ WITHIN GROUP ( ORDER BY <expr> [ ASC | DESC ] [ NULLS FIRST | LAST ] [, ...] ) ]
ARRAY_AGG( [ DISTINCT ] <expr> )
  [ WITHIN GROUP ( ORDER BY ... ) ]
  OVER ( [ PARTITION BY ... ] [ ORDER BY ... ] [ <window_frame> ] )
```

Secondary syntax unique to this step: `DISTINCT`, `WITHIN GROUP`, `NULLS LAST`, window `PARTITION BY` / `ORDER BY` / frame (`ROWS BETWEEN …`). Alias `ARRAYAGG`.

Examples:

```sql
ARRAY_AGG(rr.rule_key) WITHIN GROUP (ORDER BY rr.rule_key NULLS LAST)

ARRAY_SORT(ARRAY_AGG(DISTINCT acs_email) OVER (PARTITION BY group_name))

ARRAY_AGG(DISTINCT ck_2) WITHIN GROUP (ORDER BY ck_2 NULLS LAST)
```

**Locations:** SELECT list with `GROUP BY`; window SELECT list (no `GROUP BY` required); `HAVING` only if Snowflake allows (confirm; otherwise negative). Not a scalar in `WHERE` without subquery.

**Column refs:** agg expr, `WITHIN GROUP` sort keys, `PARTITION BY` / `OVER` `ORDER BY` keys. Unresolved `rr.rule_key` vs resolved.

**Note:** `WITHIN GROUP` grammar is **2.3** (shared with `LISTAGG` and other ordered aggregates). 5.3 owns `ARRAY_AGG`-specific pieces (`DISTINCT`, window `OVER` on ARRAY_AGG, compound nest with 5.9/5.10). Do not implement a second, ARRAY-only `WITHIN GROUP` production.

**Note:** `ARRAY_SORT` wrapping `ARRAY_AGG` may also need 5.9; this step’s goldens may compose with 5.9 once both exist. **Required compound test** (with 5.9 + 5.10): `ARRAY_TO_STRING(ARRAY_DISTINCT(ARRAY_AGG(...) WITHIN GROUP (...)), '|')` — see shared test contract item 6.

---

### 5.4 — Array literals and subscripts

**Independent syntax (not a named `ARRAY_*` function):** Snowflake ARRAY constants `[1, 'a', NULL]` and element access `arr[0]`, `arr[col]`, `GET(arr, n)`, optional `GET_PATH`.

Example:

```sql
ARRAY_SORT(ARRAY_AGG(hpi.sourcecontact_id))[0]
```

**Locations:** any predicand site; subscript on a column, on `ARRAY_CONSTRUCT` / `ARRAY_AGG` result, and on a literal.

**Column refs:** expressions inside `[ … ]`, index expressions, and the array being indexed. Unresolved index vs unresolved array.

---

### 5.5 — `FLATTEN` / `LATERAL FLATTEN`

**Table function**, not a scalar. Unique named-argument syntax:

```sql
LATERAL FLATTEN( INPUT => <array_expr> [, PATH => '...' ] [, OUTER => TRUE | FALSE ]
                 [, RECURSIVE => TRUE | FALSE ] [, MODE => 'OBJECT' | 'ARRAY' | 'BOTH' ] )
```

Also `FLATTEN(...)` in `FROM` / `TABLE(FLATTEN(...))`. Output columns (`SEQ`, `KEY`, `PATH`, `INDEX`, `VALUE`, `THIS`) must appear in `interface` / `tableDictionary` goldens.

Example:

```sql
FROM t, LATERAL FLATTEN(input => SPLIT(race_col, '|')) f
```

**Locations:** `FROM`, `JOIN` / `LEFT JOIN LATERAL`, comma-style `FROM t, LATERAL FLATTEN(...)`. Not a SELECT-list scalar.

**Column refs:** `INPUT` (and `PATH` if expression). Unresolved input array; flattened `VALUE` used later as a column.

---

### 5.6 — `ARRAY_UNION_AGG` / `ARRAY_UNIQUE_AGG`

Aggregates that take an **array-typed** expr (union / distinct-union of arrays), distinct from 5.3’s scalar-to-array `ARRAY_AGG`. Confirm `DISTINCT` / `WITHIN GROUP` / `OVER` against current Snowflake docs and implement only documented clauses.

**Locations:** same aggregate/window sites as 5.3.

**Column refs:** the array argument and any `PARTITION BY` / order keys.

---

### 5.7 — Binary / set array operations

Functions whose unique syntax is **two (or more) array arguments** (not value-in-array):

| Function | Notes |
|----------|--------|
| `ARRAY_CAT(a, b)` | Concatenate |
| `ARRAY_EXCEPT(a, b)` | Set difference |
| `ARRAY_INTERSECTION(a, b)` | Set intersection |
| `ARRAYS_OVERLAP(a, b)` | Boolean overlap |
| `ARRAYS_ZIP(a, b [, ...])` | Variadic arrays → array of objects |
| `ARRAYS_TO_OBJECT(keys, values)` | Parallel key/value arrays |

`ARRAYS_OVERLAP` is a boolean (same location matrix as 5.1). Others are array predicands (SELECT / DML assignments / nested).

**Column refs:** each array argument independently (qualified / unqualified / unresolved / ambiguous).

---

### 5.8 — Element mutate (`APPEND` / `PREPEND` / `INSERT` / `REMOVE` / `REMOVE_AT`)

Unique positional syntax:

- `ARRAY_APPEND(array, element)` / `ARRAY_PREPEND(array, element)`
- `ARRAY_INSERT(array, pos, element)`
- `ARRAY_REMOVE(array, element)` vs `ARRAY_REMOVE_AT(array, pos)`

Element may need VARIANT (reuse 5.1 cast syntax). Position is a numeric predicand (column or literal).

**Column refs:** array, element, and position (for insert/remove-at). Unresolved position must not be mistaken for a date-part (Phase 4) or a VARIANT key (Phase 3).

---

### 5.9 — Transforms with extra arguments

Each has secondary args that are **not** just “one array”:

| Function | Unique args |
|----------|-------------|
| `ARRAY_TO_STRING(array, delimiter_string)` | Delimiter predicand (often `','`) |
| `ARRAY_SORT(array [, sort_ascending [, nulls_first ]])` | Optional booleans |
| `ARRAY_SLICE(array, from, to)` | Two index predicands |
| `ARRAY_POSITION(value, array)` | Value-in-array like 5.1; VARIANT on value |
| `ARRAY_REPEAT(element, count)` | Count predicand |
| `ARRAY_GENERATE_RANGE(start, stop [, step])` | Numeric range; may have no column array at all |

Examples: `ARRAY_TO_STRING(acs_emails_per_group, ',')`; `ARRAY_SORT(ARRAY_AGG(...))`. **Required compound test** with 5.3 + 5.10: `ARRAY_TO_STRING(ARRAY_DISTINCT(ARRAY_AGG(...) WITHIN GROUP (...)), '|')` (delimiter `'|'`).

**Column refs:** array **and** delimiter / flags / indices / count / range bounds.

---

### 5.10 — Unary array → array or scalar

Shared shape `FN(array)` — one production with a function-name set is fine, but **each function still needs its own happy-path golden**:

`ARRAY_SIZE`, `ARRAY_COMPACT`, `ARRAY_DISTINCT`, `ARRAY_FLATTEN`, `ARRAY_REVERSE`, `ARRAY_MAX`, `ARRAY_MIN`

`ARRAY_SIZE` returns a number (usable in `WHERE` / `HAVING`). `ARRAY_FLATTEN` requires an array-of-arrays.

**Column refs:** the single array argument (qualified / unqualified / unresolved / ambiguous).

**Required compound test** with 5.3 + 5.9: `ARRAY_DISTINCT` wrapping `ARRAY_AGG(...) WITHIN GROUP` inside `ARRAY_TO_STRING(..., '|')` — see shared test contract item 6.

---

### 5.11 — Conversion, predicates, and split-to-array

Supporting syntax that **produces or tests** arrays without being an `ARRAY_*` manipulator:

- `TO_ARRAY(expr)`, `AS_ARRAY(variant)`, `IS_ARRAY(expr)`
- `SPLIT(string, delimiter)` and `STRTOK_TO_ARRAY(string [, delimiter])` — string → ARRAY (feeds 5.1 / 5.5 in models)

`IS_ARRAY` is boolean (5.1 location matrix). `SPLIT` appears inside `FLATTEN(INPUT => SPLIT(...))` and `ARRAY_CONTAINS(..., SPLIT(...))`.

**Column refs:** string being split, delimiter, and variant being converted/tested.

---

### 5.12 — Higher-order `FILTER` / `TRANSFORM` / `REDUCE`

Unique **lambda** syntax on arrays:

```sql
FILTER(array, a -> predicate)
TRANSFORM(array, a -> expr)
REDUCE(array, init, (acc, x) -> expr)
```

Lambda parameters are **not** columns of the FROM clause; they must not go through ordinary unresolved-column handling (same spirit as Phase 4 date-parts: context-restricted). Body expressions may mix lambda params with real columns (`t.threshold`).

**Locations:** scalar predicand sites (SELECT, WHERE if boolean `FILTER` is not used that way — `FILTER` returns array; `TRANSFORM` returns array; typically SELECT / nested).

**Column refs:** the input array; outer columns inside the lambda; **negative:** lambda param `a` must **not** emit `UNQUALIFIED_COLUMN_NOT_FOUND` when no column `a` exists.

---

### Phase 5 acceptance (overall)

- 5.1–5.12 each have goldens for the six extractor objects and the location / column-ref matrices above.
- Compound nested statement `ARRAY_TO_STRING(ARRAY_DISTINCT(ARRAY_AGG(...) WITHIN GROUP (...)), '|') AS all_sources_blended` parses with goldens (shared contract item 6).
- Known fatals tied to these constructions are gone or reduced to unrelated issues, including:
  - `ARRAY_AGG` / `OVER` window forms
  - `ARRAY_AGG(...) WITHIN GROUP` ordered aggregates
  - `ARRAY_CONTAINS(...::VARIANT, …)` in activity / intake models
  - `LATERAL FLATTEN(INPUT => SPLIT(...))` in attribute snippets
- Phases 1–4 unchanged. `::VARIANT` must not be parsed as Phase 3 `:` field access. Date-part `year` inside unrelated functions stays Phase 4.

### Out of scope for Phase 5

- Full OBJECT / MAP function families (`OBJECT_CONSTRUCT`, `MAP_*`) except where an array function returns/consumes them (`ARRAYS_TO_OBJECT`, `ARRAYS_ZIP`).
- Rewriting models to avoid ARRAY syntax.
- JavaScript UDF array APIs.

---

## Phase 6 — Simple JINJA substitutions support

**Kind:** Enhancement (Jinja/dbt relation substitutions in PSS tuple positions, plus other simple `{{ … }}` forms)

**Status:** Not started

**Theme:** PSS already accepts a **subset** of Jinja/dbt formats for table and view references wherever a **TUPLE** substitution variable may appear (`FROM` / `JOIN` / equivalent). Expand that subset to other **standard, straight-forward** formats, skip leading Jinja as unexamined comments (6.3), and treat in-query `{{ }}` operands as substitutions (6.2). This phase is **not** a Jinja interpreter (that is Phase 7).

### Subtask tracker

| Step | Construction | Status |
|------|----------------|--------|
| 6.1 | Tuple / table-reference format expansion (`source` / `ref` quote and spacing variants) | Not started |
| 6.2 | Simple predicand / operand (and qualified column) Jinja substitutions | Not started |
| 6.3 | File-prefix Jinja: skip/collect as unexamined comments (bracket-matched) | Not started |
| 6.4 | **Phase 2.5 closure** — parse Jinja-authored DNC rollup (§2.5 starter query) without `PARSE_TIMEOUT` | Not started (depends on **6.1** for inline `ref`/`source`) |

Keep interpreting `{% %}` / macros / filters for Phase 7. 6.3 only **skips** prefix (and optional trailer) material. **2.5** does not require Phase **7** (no in-body `{% %}`).

---

### 6.1 — Tuple substitution variable formats

**Kind:** Enhancement (lexer/grammar of `{{ source(...) }}` / `{{ ref(...) }}` in TUPLE positions)

**Start here** with the query below. It is valid dbt Jinja and is **not** accepted yet. Then add other straight-forward tuple/table formats in the same subtask.

#### Starter query (not accepted)

```sql
 select partner_details.zip5 as zip5_1,
       zip_1.latitude as lat_2,
       zip_1.longitude as long_2
 from
    {{ source("COMMON","ref__partner_details") }} as partner_details
     inner join
    {{ source("COMMON","ref__zip_codes") }} as zip_1
     on
           trim(partner_details.zip5) = trim(zip_1.zip5)
```

Observed (scan): unexpected `,` and `)` inside `source("COMMON","ref__partner_details")` — e.g. FATAL `REPORT_ERROR` near `','`. Likely the inner **double-quoted** arguments are tokenized as SQL delimited identifiers, so the comma between them is not a Jinja argument separator. Confirm that diagnosis before changing the grammar.

#### Problem

TUPLE substitutions today cover only a subset of relation-reference spellings (commonly single-quoted `{{ ref('model') }}` / `{{ source('schema', 'table') }}`, and/or PSS `<[schema].[table].[pop]>`). Standard dbt also uses:

- Double-quoted string args: `source("COMMON","ref__partner_details")`
- Tight vs spaced commas and braces
- Two-arg `ref('package', 'model')`
- `source` / `ref` with mixed whitespace

Those must parse as **one substitution tuple**, then bind like any other table/view: alias, column resolution, `substitutions` extractor object.

#### Additional straight-forward formats (same subtask)

Include at least these variants wherever a TUPLE may appear (`FROM`, `JOIN`, CTE `FROM`, subquery `FROM`). Happy-path: no FATAL / no recovery; `substitutions` lists the tuple; alias columns resolve.

| Variant | Example |
|---------|---------|
| Double-quoted `source` (starter) | `{{ source("COMMON","ref__partner_details") }}` |
| Single-quoted `source` (already accepted — no regression) | `{{ source('COMMON', 'ref__partner_details') }}` |
| Spaces around args / parens | `{{ source( "COMMON" , "ref__zip_codes" ) }}` |
| No space after `{{` | `{{source("COMMON","ref__zip_codes")}}` |
| Double-quoted `ref` | `{{ ref("sort_orders") }}` |
| Single-quoted `ref` (no regression) | `{{ ref('sort_orders') }}` |
| Two-arg `ref` (package, model) | `{{ ref('my_pkg', 'sort_orders') }}` |
| `source` + `AS` alias (starter) | `{{ source("COMMON","ref__zip_codes") }} as zip_1` |
| Mixed quote styles if dbt allows | `{{ source('COMMON', "ref__zip_codes") }}` |

Also include other **simple** (non-control-flow) relation-adjacent forms that are still TUPLE-shaped (`{{ this }}` as a JOIN target). Operand / `var()` value substitutions belong in **6.2**, not here. Do **not** pull in `{% if %}`, macros, or `dbt_utils` packages here (Phase 7).

#### Expected behavior

1. Each accepted form is a single TUPLE (or simple value) substitution, not SQL identifiers/`"` strings.
2. `FROM` / `JOIN` of that substitution plus `AS alias` puts `alias` in scope; `partner_details.zip5` / `zip_1.latitude` resolve.
3. `substitutions` / `tableDictionary` / `symbolTable` goldens name the source/ref arguments (schema/table or model).
4. Formats already accepted must not regress.
5. Unresolved columns on the alias still diagnostic as today (`zip_1.not_a_col`).

#### Suggested tests

- Exact starter query (FROM + INNER JOIN, double-quoted `source`)
- Each extra format row above in `FROM` and in `JOIN`
- TUPLE parser endpoint on a lone `{{ source("COMMON","ref__zip_codes") }}`
- SQL `SELECT` / `INSERT … SELECT` using the substitution as the relation
- Qualified vs unqualified columns from the aliased substitution
- Unresolved / ambiguous column diagnostics unchanged in meaning
- Six extractor goldens (`substitutions`, `symbolTable`, `interface`, `tableDictionary`, `sqlTree`, `messages`) on the starter

#### Acceptance

- The starter query no longer fatals on `,` / `)` inside `source("…","…")`.
- Starter query and the additional format table parse; tuple substitutions appear in `substitutions`.
- Phase 7 not required for this to land. Phases 1–5 unchanged.

#### Out of scope (6.1)

- `{% if %}` / `{% for %}` / `{% set %}` / macros / filters (`|`) / `{{ dbt_utils.… }}` (Phase 7).
- Compiling Jinja to warehouse SQL (still parse substitutions in place).
- Rewriting models to single-quoted `source()`.

---

### 6.2 — Simple predicand / operand Jinja substitutions (and column form)

**Kind:** Enhancement (Jinja `{{ … }}` as PSS **predicand** substitutions; also **column** if qualified form is in scope)

**Component:** lexer/grammar of simple `{{ }}` operand calls; map onto existing PSS predicand (and column) substitution handling. **No `{% %}`.**

#### Investigation note — can Jinja be a qualified column?

**Yes.** Jinja is textual interpolation, so `select a.{{ ... }} from tab a` is valid Jinja and, after compile, is ordinary qualified SQL if the substitution expands to an identifier.

- `SELECT a.{{ var('col_name') }} FROM tab a` → `SELECT a.foo FROM tab a`
- Unqualified `SELECT {{ var('col_name') }} FROM tab a` is a column or predicand depending on whether the expansion is a name vs an expression
- Qualifier-side Jinja (`{{ var('alias') }}.col`) is the same idea (identifier, not a tuple)

In-repo evidence that authors already write `alias.{{ … }}`:

```sql
acs_con.{{ results_col_list[i] }}
first_value(fs.{{field}}) OVER (...) AS {{field}}
```

Those examples usually sit inside `{% for %}` / `{% set %}` (Phase 7). **6.2 still must accept the `alias.{{ simple_operand }}` shape** when the inner `{{ }}` is a simple operand (`var('…')`, `var("…")`, or a simple Jinja name), without requiring statement tags.

**Implication for this task:** support **both** PSS substitution kinds for these Jinja refs:

| SQL shape | PSS `VariableType` |
|-----------|-------------------|
| Operand / scalar expr (`WHERE x = {{ var('n') }}`, `SELECT {{ var('n') }} + 1`) | `predicand` |
| Unqualified identifier in a column position (`SELECT {{ var('col') }} FROM t`) | `column` |
| Qualified identifier (`SELECT a.{{ var('col') }} FROM t a`) | `column` (qualified) |

If investigation during implementation finds a case where `a.{{ }}` cannot be a column (e.g. expansion is always a full expression), document that and keep predicand-only for that shape — but the default is **support both**, because compiled SQL is a qualified column.

#### Problem

Simple Jinja operands (`{{ var("tenant_sk") }}`, `{{ var('x') }}`, `{{ some_name }}`) appear in SELECT lists, `WHERE`, `ON`, function args, and string literals. They should be handled like existing PSS **predicand** substitution variables (and **column** when they sit in identifier position, including `alias.{{ }}`).

Do **not** parse `{% %}` blocks, `for` indexes, filters (`|`), or macros here.

#### Starter shapes

```sql
-- predicand / operand
WHERE lower(partner_details.tenant_sk) = '{{ var("tenant_sk") }}'

-- predicand not wrapped in a SQL string
WHERE partner_details.tenant_sk = {{ var("tenant_sk") }}

-- column (unqualified)
SELECT {{ var("zip_col") }} FROM partner_details

-- column (qualified) — in scope for this task
SELECT a.{{ var("zip_col") }} FROM partner_details AS a
```

#### Expected behavior

1. A simple `{{ var(...) }}` / `{{ ident }}` in an expression is a **predicand** substitution (same resolution/diagnostics path as PSS predicand vars).
2. The same call after `.` (or in a bare column slot) is a **column** substitution; `a` still resolves as the table alias; unresolved `a.{{ var('missing') }}` uses column-not-found diagnostics, not “unexpected `{{`”.
3. Quote/spacing variants match 6.1 (`var("x")` vs `var('x')`, optional default arg `var('x', 'def')` if that stays a literal).
4. `'{{ var("tenant_sk") }}'` inside a SQL string: treat the inner `{{ }}` as a substitution occupying the literal (or as predicand-in-string — pick one approach and golden it). Do not require `{% %}`.
5. `{% if %}` / `{% for %}` / `{{ x \| lower }}` remain Phase 7.

#### Suggested tests

- `WHERE col = {{ var("tenant_sk") }}` and `= '{{ var("tenant_sk") }}'`
- SELECT-list predicand: `SELECT {{ var("n") }} + 1`
- `JOIN ON` / `CASE` / function argument operands
- Unqualified column: `SELECT {{ var("zip5") }} FROM t`
- Qualified column: `SELECT a.{{ var("zip5") }} FROM t a` and `SELECT a.{{ zip_col }} FROM t a`
- PREDICAND and COLUMN parser endpoints on the lone `{{ }}` forms
- Unresolved / ambiguous diagnostics for the column form
- Six extractor goldens on at least one predicand and one qualified-column fixture
- No `{% %}` in 6.2 fixtures

#### Acceptance

- Simple operand Jinja parses as predicand substitutions; qualified `a.{{ … }}` parses as column substitutions.
- `{% %}` still not recognized (Phase 7).
- 6.1 tuple forms unchanged.

#### Out of scope (6.2)

- `{% %}` statement tags, loops, `{% set %}`, macros, filters (Phase 7).
- `{{ source }}` / `{{ ref }}` / `{{ this }}` as relations (6.1).
- Dynamic `{{ results_col_list[i] }}` **index expressions** that only make sense inside `{% for %}` — the **qualified shape** is in 6.2; the loop that supplies `i` is Phase 7.

---

### 6.3 — Jinja file-prefix variations (unexamined comments)

**Kind:** Enhancement (leading Jinja before the SQL query; skip, do not interpret)

**Component:** grammar / lexer prefix skipper; store raw text in a **non-emitted** bucket

#### Problem

dbt/Jinja files often start with material that is not the query: `{{ config(...) }}`, `{% set ... %}`, `{% snapshot ... %}`, `{# ... #}`, or long multi-statement instruction blocks with nested language constructs. That prefix can be mundane commentary or quite large. Today it collides with SQL parse (`unexpected '{{'`, `'%'`, etc.) before the real `SELECT`/`WITH`/`INSERT` is seen.

**6.3 does not parse Jinja.** Modify the grammar so that everything from the start of the file until the actual query starts is collected as **unexamined comments**. Track **opening and closing brackets of each type** so nested/long prefixes still close correctly. Put the collected text in its **own bucket for now, without emitting** it on the parse payload (or emit only if an existing comments channel already exists — default is store, do not surface to consumers yet).

Phase **7** gets a subtask to later parse that bucket into the **SCRIPT** grammar (see 7.1).

#### Bracket types to match (non-interpreting)

Keep a nesting/pairing count; do not evaluate contents:

| Open | Close | Typical prefix use |
|------|--------|-------------------|
| `{{` / `{{-` | `}}` / `-}}` | `config`, `this` in hooks |
| `{%` / `{%-` | `%}` / `-%}` | `set`, `snapshot`, `macro`, `if`/`for` in the prefix |
| `{#` | `#}` | comments |
| `(` / `[` / `{` | `)` / `]` / `}` | inside a Jinja tag (e.g. `config( … )`, `{% set fields = [ … ] %}`) |

String literals inside tags (`'...'`, `"..."`) must not count brackets. Nested `{% if %}…{% endif %}` in the prefix is still only bracket-matched, not understood.

Stop skipping when a **SQL statement** begins (`SELECT`, `WITH`, `INSERT`, `UPDATE`, `DELETE`, `MERGE`, `CREATE`, …), not when the first `{% %}` closes — a prefix may be many statements.

#### Trailer (same skipper, still unexamined)

Some files wrap the query (`{% snapshot %} … SELECT … {% endsnapshot %}`). 6.3’s required job is the **prefix**. If the same skipper can swallow a **trailing** Jinja block after the SQL without interpreting it, include that so snapshot-style files parse the query body. Do not require SCRIPT semantics.

Examples:

- Short: `{{ config(materialized='view') }}` then `SELECT …`
- Longer `{% set fields = [ … ] %}` then SQL (multi-field form-last-answers pattern)
- Multi-construct prefix + trailer: `{% snapshot %}`, `{{ config(...) }}`, `SELECT`, `{% endsnapshot %}`

#### Expected behavior

1. Leading Jinja is consumed as unexamined comments; the following SQL parses as today (6.1/6.2 substitutions still apply **in** the query).
2. Bracket pairing is recorded (type + nest depth / spans) so Phase 7 can re-parse the bucket.
3. Bucket is **not emitted** on the normal six extractor objects yet (or lives under a new unused field). `messages` / `sqlTree` of the SQL body must not include prefix fatals.
4. Unbalanced prefix brackets → a clear diagnostic at the prefix, not a random SQL `unexpected '%'`.
5. `{% %}` **inside** the SQL body (e.g. `{% if %}` around a WHERE) is **not** this task (Phase 7), except an optional trailing wrapper after the statement.

#### Suggested tests

- File with only `{{ config(...) }}` prefix + simple `SELECT`
- `{% set … %}` list prefix + `SELECT` (form-last-answers shape)
- Nested `{% if %}…{% endif %}` in the prefix, then `SELECT`
- `{# comment #}` only prefix
- Snapshot wrap: prefix `{% snapshot %}` + `config` + `SELECT` + trailing `{% endsnapshot %}`
- Unbalanced `{%` with no close before `SELECT` → prefix diagnostic
- Six goldens on the **SQL body** (prefix absent from `sqlTree` / `messages`); prefix stored in the new bucket for a unit assertion that it was captured

#### Acceptance

- Prefix Jinja no longer prevents parsing the query that follows.
- Collected prefix is bracket-tracked, bucketed, not required on consumer output.
- 6.1 / 6.2 in-query `{{ }}` unchanged. No Jinja SCRIPT interpretation (7.1).

#### Out of scope (6.3)

- Interpreting `{% if %}` / macros / filters (Phase 7).
- Expanding prefix Jinja into SQL.
- Changing how in-query `{{ ref/source/var }}` works (6.1 / 6.2).

---

### 6.4 — Phase 2.5 closure (Jinja-authored DNC rollup)

**Depends on:** **6.1** (tuple `ref`/`source` in `FROM` / `JOIN`, including spaced forms like `{{ ref ('acs__contacts')}}`).

**Fixture:** Full Jinja-authored starter query in [§2.5](#25--parse-hang--parse_timeout-on-multi-cte-email-dnc-rollup) (`email_common_format` CTE chain, `UNION ALL`, `QUALIFY`, nested `CASE`). Not in Panto CSV; copy from workplan or add `parse/documents/fixtures/dnc-email-rollup-jinja.sql` when implementing.

**Acceptance:**

1. Parse completes without `PARSE_TIMEOUT` on the Jinja-authored text (not table-stubbed).
2. CTEs `email_common_format`, `aggregation_check`, `dnc_prioritization` resolve; outer SELECT interface matches guardrail (`dncEmailRollupMultiCteExemplarV0Test`).
3. Add regression test (e.g. `dncEmailRollupJinjaAuthoredV0Test`) beside the existing stubbed guardrail.

**Out of scope:** Phase **7** (`{% if %}`, macros) — not present in this fixture.

---

## Phase 7 — Deep JINJA language support *(optional)*

**Kind:** Enhancement (optional; full-ish Jinja/dbt in SQL files)

**Status:** Not started — **do not start until Phase 6 simple substitutions are accepted**, and only if product still needs compiled-template constructs.

**2.5 note:** The Phase **2.5** email DNC rollup uses inline `{{ ref }}` / `{{ source }}` only — **not** a Phase **7** dependency. Closure is **6.4** after **6.1**.

**Theme:** Phase 6 is literal in-query `{{ ref/source/var }}` plus **skipping** file-prefix Jinja as comments (6.3). This phase **interprets** language features that change SQL shape at compile time, including the prefix bucket 6.3 stored.

### Subtask tracker

| Step | Construction | Status |
|------|----------------|--------|
| 7.1 | Parse 6.3 prefix (and trailer) comments into SCRIPT grammar | Not started |

Further 7.x (if / for / macros / filters / dbt_utils) to be added when this phase is activated.

---

### 7.1 — Prefix Jinja → SCRIPT (uses 6.3 bucket)

**Depends on:** 6.3 having captured the unexamined prefix (bracket-matched, not emitted).

Take the 6.3 comment bucket and parse/organize those preliminary statements under the PSS **SCRIPT** endpoint/grammar so consumers can understand `config`, `set`, `snapshot`, and other leading constructs instead of treating them as opaque comments. Improve handling of multi-statement prefix Jinja here — not in 6.3.

Starter material: the same prefixes 6.3 skipped (`{{ config }}`, `{% set %}`, `{% snapshot %}…{% endsnapshot %}`).

---

In scope when activated (additional 7.x later):

- Statements: `{% if %}` / `{% elif %}` / `{% else %}` / `{% endif %}`, `{% for %}` / `{% endfor %}`, `{% set %}`
- Macros: `{% macro %}`, `{{ my_macro(...) }}`, `{% import %}` / `{% from … import %}`
- Filters and tests: `{{ col | lower }}`, `is defined`, etc.
- Nested / dynamic relation args: `{{ source(var('src'), 't') }}`, `{{ ref(model_name) }}` where args are not string literals
- Package calls: `{{ dbt_utils.star(...) }}`, `{{ dbt_utils.union_relations(...) }}` (scan already fatals on `{{ dbt_utils`)
- Comments `{# … #}` and whitespace control `{{-` / `-}}`
- Interaction with PSS substitution objects after a *logical* expansion (document whether PSS parses pre- or post-render)

**Not required** for Phase 6 acceptance. Treat as a follow-on epic: each 7.x needs its own starter fixture, goldens, and a decision on how much Jinja to evaluate vs leave as opaque substitution nodes.

#### Out of scope even for Phase 7 unless explicitly added

- Running dbt compile as a substitute for parser support.
- Arbitrary Python/Jinja plugins.

---

## Later phases

_(Add Phase 8+ here.)_
