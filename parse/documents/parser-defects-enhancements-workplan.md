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
| 2 | Set-op interfaces, `VALUES`, `WITHIN GROUP`, nested `CASE`, parse hang, product `CASE` | Defect / enhancement | **Complete** (2.1–2.6) | This file (2.1–2.6) |
| 3 | Snowflake `PARSE_URL` / PARSE functions with `:` field access | Enhancement | Not started | This file |
| 4 | Snowflake `DATEADD` / date-part keywords vs column resolution | Defect | Not started | This file |
| 5 | Snowflake ARRAY syntax and functions | Enhancement | Not started | This file (5.1–5.12) |
| 6 | Simple JINJA substitutions support | Enhancement | Not started | This file (6.1–6.3) |
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

**Kind:** Defect (set-op interface + `VALUES` FROM syntax) plus standalone grammar for `WITHIN GROUP`

**Status:** **Complete** (2026-08-15) — all subtasks 2.1–2.6 landed

**Theme:** UNION / INTERSECT / EXCEPT branches must keep a usable FROM/JOIN scope and a sensible output interface (2.1–2.2). **2.3** is `WITHIN GROUP` on ordered aggregates. **2.4** is nested searched `CASE`. **2.5** is a parse hang / `PARSE_TIMEOUT` on a multi-CTE rollup query — diagnose root cause, then fix termination. **2.6** is a flat searched `CASE` that extracts `product` from `cat.title` via `POSITION`, nested `SPLIT`/`SPLIT_PART`, and `NULLIF`/`TRIM`/`COALESCE`.

### Recommended implementation order (2026-08-15 assessment)

Work **2.1 → 2.2 → 2.6 → 2.3 → 2.4 → 2.5**. Rationale:

| Order | Step | Effort | Impact | Why |
|------|------|--------|--------|-----|
| 1 | **2.1** | Low — walker-only (`SqlASTWalkerHelper`, ~3 column-count comparison sites) | High — common dbt `SELECT *` UNION explicit-column pattern | **Complete** — skip when fewer side has `*` or both sides have `*`. Tests in `SqlEventWalkerSubqueriesAndClauseSemanticsTests`. |
| 2 | **2.2** | Medium — focused grammar (`FROM VALUES … AS alias (cols)` without outer parens) + table binding | High — `COALESCE(pso.sort_order, …)` fatal is a recovery cascade, not a resolution bug | **Complete** — `values_statement` accepts unparenthesized `VALUES values_matrix`; walker unchanged. Tests in `SqlEventWalkerUnparenthesizedValuesTests`. |
| 3 | **2.6** | Medium — `SPLIT` / `SPLIT_PART` / `expr[n]` subscript (shared with 5.4) | Medium — flat `CASE` attribution models | **Complete** — postfix `arraySubscriptSuffix` + bracket/subscript disambiguation; tests in `SqlEventWalkerArraySubscriptTests` and `SqlEventWalkerBracketedIdentifierTests`. |
| 4 | **2.3** | Medium–high — new shared `WITHIN GROUP (ORDER BY …)` production | High — `LISTAGG` and ordered aggregates in analytics models | **Complete** — `ordered_aggregate_expression` + walker `within_group_ordered_by`; tests in `SqlEventWalkerWithinGroupOrderedAggregateTests`. |
| 5 | **2.4** | Low–medium after 2.6 — nested `CASE` likely already parses once `SPLIT(…)[n]` works | Medium | **Complete** — grammar already allowed nested `CASE`; blocker was `SPLIT(…)[n]` (2.6). Exemplar tests in `SqlEventWalkerArraySubscriptTests`. |
| 6 | **2.5** | Unknown — investigate-first bisect | Low breadth, high severity per query | **Complete** (guardrail) — full DNC rollup exemplar parses in ~1s with no `PARSE_TIMEOUT`; regression test in `SqlEventWalkerSubqueriesAndClauseSemanticsTests`. Original RMCP hang not reproduced on current tree. |

**Dependencies:** Phase 2 complete. **Fixtures:** `SqlEventWalkerSubqueriesAndClauseSemanticsTests.unionWildcardBranchAgainstExplicitColumnListTest` (2.1); `SqlEventWalkerUnparenthesizedValuesTests` (2.2); `SqlEventWalkerArraySubscriptTests` (2.4, 2.6); `SqlEventWalkerWithinGroupOrderedAggregateTests` (2.3); `SqlEventWalkerSubqueriesAndClauseSemanticsTests.dncEmailRollupMultiCteExemplarV0Test` (2.5).

### Subtask tracker

| Step | Construction | Status |
|------|----------------|--------|
| 2.1 | Wildcard `*` matches any set-op column count | **Complete** |
| 2.2 | `VALUES (…)` `AS alias (col, …)` plus JOIN / `COALESCE` on the joined table | **Complete** |
| 2.3 | `WITHIN GROUP (ORDER BY …)` on `LISTAGG` and other ordered aggregates (`OVER` included) | **Complete** |
| 2.4 | Nested searched `CASE` (`CASE` as `THEN`/`ELSE` of `CASE`) plus inner `SPLIT`/`SPLIT_PART` predicands | **Complete** |
| 2.5 | Parse hang / `PARSE_TIMEOUT` on multi-CTE email DNC rollup (investigate + fix) | **Complete** (guardrail — hang not reproduced) |
| 2.6 | Flat searched `CASE` for `product` from `cat.title` (`POSITION`, nested `SPLIT`/`SPLIT_PART`, `NULLIF`/`TRIM`/`COALESCE`) | **Complete** |

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
- **Done:** `SqlEventWalkerArraySubscriptTests` — workplan nested `source_type` starter, isolation nested `CASE`, `SPLIT_PART(…, -1)` in `WHEN`.
- 2.1–2.3 unchanged. Shared `expr[n]` from 2.6 / 5.4.

#### Out of scope (2.4)

- Full ARRAY function family (Phase 5) beyond `SPLIT(…)[n]` needed here.
- Jinja (Phase 6–7).
- Rewriting the model to flatten nested `CASE`.

---

### 2.5 — Parse hang / `PARSE_TIMEOUT` on multi-CTE email DNC rollup

**Kind:** Defect (non-terminating parse or pathological work — investigate root cause, then fix)

**Status:** **Complete** (2026-08-15, guardrail) — full exemplar parses on current tree; original RMCP `PARSE_TIMEOUT` not reproduced

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
- **Note:** Root cause of the original RMCP hang was not isolated — current tree may have fixed it cumulatively, or the timeout was environment-specific. Re-open if `PARSE_TIMEOUT` recurs on the Jinja-authored fixture.
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

Keep interpreting `{% %}` / macros / filters for Phase 7. 6.3 only **skips** prefix (and optional trailer) material.

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

## Phase 7 — Deep JINJA language support *(optional)*

**Kind:** Enhancement (optional; full-ish Jinja/dbt in SQL files)

**Status:** Not started — **do not start until Phase 6 simple substitutions are accepted**, and only if product still needs compiled-template constructs.

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
