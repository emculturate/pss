# Defect: subquery aliases resolved in the outer scope when the subquery is inside a SELECT-list expression (PSS 5.1.3)

**Parser:** `pss-parse-5.1.3-1-fat.jar` (`symbolTableFormat: "5.1.3"`). Reproduced through the direct API (`access.SqlParserAccess` / `access.Snippet`) and through `cli.SqlParseMCP`; both return the same diagnostics.
**Baseline:** `pss-parse-5.0.0-3.jar` parses every example below with 0 FATALs.
**Diagnostic:** `FATAL` `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE`, phase `ast-walk`, source `SqlASTWalkerHelper`, `recoverable: false`.
**Severity:** blocking. Valid SQL is rejected with a FATAL, so any consumer that gates on `getFatalErrorCount() > 0` will refuse production queries and snippets that work today.

## Summary

When a subquery (scalar, `IN (subquery)`, `NOT IN (subquery)`, or `EXISTS (subquery)`) appears **inside an expression in a SELECT list**, 5.1.3 resolves the qualified column references *inside* that subquery against the **outer** query block's aliases. The aliases are defined only inside the subquery, so resolution fails and 5.1.3 raises:

```text
Source Table not found for Column '<col>' at (l:L c:C). No alias or table called '<inner alias>'.
```

The reported position is the select item's output alias token in the outer query (for example the `r` in `... end as r`), not the column inside the subquery. This position is itself a clue that the check runs during outer interface validation.

## Original example: snippet 9530 (`query` snippet, `QUERY` endpoint)

From `project_simulation/src/panto_extracted_queries/extracted_panto_snippets_sept2026.csv`, row 9530. Valid Snowflake SQL; the alias `g` is defined in each `IN` subquery and used only there.

```sql
select d.src_donor_id,
case
	when d.contact_optout_indicator = 'Yes' then 1
	when d.calculated_field_11 = 'Yes' then 1
	when d.calculated_field_12 = 'Yes' then 1
	when d.DONOR_CATEGORY_DESCRIPTION = 'Friend' and d.src_donor_id in (
	  select src_donor_id
	  from <[AMS].[gifts].{fulfillment}> as g
	  where 1=1
	  and g.calculated_field_8 in ('Athletics','School of Law')
	  )
	  and
	  d.src_donor_id not in (
		select soft_credit_id
	    from <[AMS].[gifts_allocation].{fulfillment}> as g
	    where 1=1
	    and g.calculated_field_2 not in ('Athletics','School of Law')
	    ) then 1
	else 0
	end as contact_optout_ind,
case
	when d.mail_optout_indicator = 'Yes' then 1
	else 0
	end as mail_optout_ind,
case
	when d.email_optout_indicator = 'Yes' then 1
	else 0
	end as email_optout_ind,
case
	when d.solicit_optout_indicator = 'Yes' then 1
	else 0
	end as solicit_optout_ind
FROM <[AMS].[donor].{fulfillment}> as d
```

| Parser | FATALs |
|---|---|
| 5.0.0-3 | 0 |
| 5.1.3 | 2: `Source Table not found for Column 'calculated_field_8' at (l:20 c:8). No alias or table called 'g'.` and the same for `calculated_field_2` |

Line 20, column 8 is the outer alias `contact_optout_ind`. Renaming the two inner aliases to distinct names (`g1`, `g2`) does **not** help. 5.1.3 then reports `g1` and `g2`, so this is not an alias-collision problem.

## Earlier examples of the same defect

These were found first, during the 5.0.0-3 vs 5.1.3 snippet comparison. They use scalar subqueries as arithmetic or comparison operands.

### Snippet 8520 (`tuple` snippet, percentage check)

```sql
(
select  distinct ((
   select ifnull(sum(pp.record_count),0) as unknown_total
		from <Base Tbl> as pp where field_name = 'address_type' and pp.eab_standard_value in ('Unmapped Value')
	   ) /
		(
		  select sum(pq.record_count)+1 as total
			from <Base Tbl> as pq where pq.field_name = 'address_type'
		)) as missing_pct
from <Base Tbl> as pr
where ifnull(pr.partner_value,'') <>''
)
```

5.0.0-3: 0 FATALs. 5.1.3: 4 FATALs, all at `(l:9 c:8)`, the outer alias `missing_pct`:

```text
Source Table not found for Column 'record_count' at (l:9 c:8). No alias or table called 'pp'.
Source Table not found for Column 'eab_standard_value' at (l:9 c:8). No alias or table called 'pp'.
Source Table not found for Column 'record_count' at (l:9 c:8). No alias or table called 'pq'.
Source Table not found for Column 'field_name' at (l:9 c:8). No alias or table called 'pq'.
```

This percentage/threshold tuple pattern (aliases `pp`, `pq`, `bt`) is repeated across the snippet corpus. It accounts for most of the roughly 1,000 "Source Table not found" messages on `tuple` snippets in the September comparison.

### Isolated reproductions from that investigation

| SQL | 5.1.3 FATALs |
|---|---|
| `select (select sum(pp.x) as a from t as pp) as r from t as pr` | 0 |
| `select (select sum(pp.x) as a from t as pp) / (select sum(pq.x) as b from t as pq) as r from t as pr` | 2 (`pp`, `pq`) |
| `select ((select sum(pp.x) as a from t as pp) / (select sum(pq.x) as b from t as pq)) as r from t as pr` | 2 (`pp`, `pq`) |
| `select case when (select sum(pp.x) as a from t as pp) > 0 then 1 end as r from t as pr` | 1 (`pp`) |

## Boundary of the defect (5.1.3, `SQL` endpoint, all 0 FATALs on 5.0.0-3)

### Fails

| Case | SQL | Unresolved alias |
|---|---|---|
| Scalar subquery as arithmetic operand | `select (select max(pp.x) from t as pp) + 0 as r from t as pr` | `pp` |
| Scalar subquery as function argument | `select coalesce((select sum(pp.x) as a from t as pp), 0) as r from t as pr` | `pp` |
| `IN (subquery)` as a select item | `select d.id in (select x from t as g where g.y = 1) as r from t as d` | `g` |
| `IN (subquery)` in `CASE WHEN` | `select case when d.id in (select x from t as g where g.y = 1) then 1 else 0 end as r from t as d` | `g` |
| Same, alias used only in inner select list | `select case when d.id in (select g.x from t as g) then 1 else 0 end as r from t as d` | `g` |
| `NOT IN (subquery)` in `CASE WHEN` | `select case when d.id not in (select x from t as g where g.y = 1) then 1 else 0 end as r from t as d` | `g` |
| Two `IN` subqueries with distinct aliases | `select case when d.id in (select x from t as g1 where g1.y = 1) and d.id not in (select z from u as g2 where g2.y = 2) then 1 else 0 end as r from t as d` | `g1`, `g2` |
| `EXISTS` (correlated) in `CASE WHEN` | `select case when exists (select 1 from t as g where g.y = d.id) then 1 else 0 end as r from t as d` | `g` |
| Correlated `IN` | `select case when d.id in (select g.x from t as g where g.y = d.k) then 1 else 0 end as r from t as d` | `g` (twice) |
| No outer `FROM` at all | `select case when 1 in (select x from t as g where g.y = 1) then 1 else 0 end as r` | `g` |
| Inside a CTE body's select list | `with c as (select case when d.id in (select x from t as g where g.y = 1) then 1 else 0 end as r from t as d) select c.r from c` | `g` |
| Inside a derived table's select list | `select s.r from (select case when d.id in (select x from t as g where g.y = 1) then 1 else 0 end as r from t as d) as s` | `g` |

### Passes

| Case | SQL |
|---|---|
| Bare scalar subquery as the whole select item | `select (select sum(pp.x) as a from t as pp) as r from t as pr` |
| Inner columns unqualified | `select (select sum(x) as a from t as pp) / (select sum(x) as b from t as pq) as r from t as pr` |
| Inner alias declared but never referenced | `select case when d.id in (select x from t as g) then 1 else 0 end as r from t as d` |
| Scalar subquery comparison in `WHERE` | `select pr.id from t as pr where (select sum(pp.x) as a from t as pp) > 0` |
| `IN (subquery)` in `WHERE` | `select d.id from t as d where d.id in (select g.x from t as g where g.y = 1)` |
| `IN (subquery)` inside `CASE` in `WHERE` | `select d.id from t as d where case when d.id in (select x from t as g where g.y = 1) then 1 else 0 end = 1` |
| `IN (subquery)` in a `JOIN ... ON` | `select d.id from t as d join u as e on e.id = d.id and d.id in (select x from t as g where g.y = 1)` |
| Scalar subquery in `HAVING` | `select d.k from t as d group by d.k having count(*) > (select count(*) from t as g where g.y = 1)` |
| `IN (subquery)` inside `CASE` in `ORDER BY` | `select d.id from t as d order by case when d.id in (select x from t as g where g.y = 1) then 1 else 0 end` |

**Trigger:** all three must hold:

1. A subquery is nested inside an **expression** (operator, `CASE`, function call, `IN`, `EXISTS`) and is not the entire select item.
2. That expression is a **SELECT-list item** of any query block (top level, CTE body, or derived table).
3. The subquery contains at least one **qualified** column reference using an alias declared inside the subquery.

The same subquery is accepted in `WHERE`, `ON`, `HAVING`, and `ORDER BY`, and accepted as a bare select item.

## Diagnosis evidence

5.1.3 flattens each outer interface item into a list of source columns, and that flattening descends into nested subqueries and keeps their **inner-scope** `table_ref` values.

For `select case when d.id in (select x from t as g where g.y = 1) then 1 else 0 end as r from t as d`, 5.1.3 emits:

```json
{
  "def_query2": {
    "table_alias": { "d": "t" },
    "interface": {
      "r": [
        { "name": "id", "table_ref": "d" },
        { "name": "x",  "table_ref": "t" },
        { "name": "y",  "table_ref": "g" }
      ]
    },
    "dependent_queries": { "in_list1": { "query": "query0", "type": "interface" } },
    "def_query0": {
      "table_alias": { "g": "t" },
      "filters": [ { "name": "y", "table_ref": "g" } ],
      "interface": { "x": [ { "name": "x", "table_ref": "t" } ] }
    }
  }
}
```

The subquery block `def_query0` resolves `g` correctly (`table_alias: { "g": "t" }`). But the outer item `r` also carries `{ "name": "y", "table_ref": "g" }`, which was lifted from the subquery's `WHERE` filter. When that outer interface entry is validated against `def_query2.table_alias` (which only has `d`), `g` is not found and the FATAL is raised at the alias `r`.

Two more observations narrow down the code path:

- For the bare select item `select (select sum(pp.x) as a from t as pp) as r from t as pr`, 5.1.3 also emits `"r": [{ "name": "x", "table_ref": "pp" }]` with an inner alias, but raises **no** FATAL. The bare-subquery path appears to skip the outer-scope check, or resolves it through `dependent_queries`. The expression path does not.
- 5.0.0-3 kept the subquery as a nested `lookup` / `in_list` node in the interface, so its aliases were never resolved in the outer scope.

A related tree-shape inconsistency points to where the problem starts. A bare scalar-subquery select item keeps its `lookup` wrapper in the sqlTree (`"select":{"1":{"lookup":{...},"alias":"r"}}`). When the same subquery is an operand, 5.1.3 drops the wrapper and inlines its `select`/`from` into the expression (`"calc":{"left":{"select":{...},"from":{...}}}`). 5.0.0-3 kept `lookup` in both positions. Without the wrapper, the outer item's column collection has no subquery boundary to stop at.

## Suspected cause and suggested fix direction

The outer-scope check of qualified column references runs over the flattened column list of each SELECT-list expression. That list includes columns collected from nested subquery nodes, which still carry inner-scope aliases. Possible fixes, from narrowest to broadest:

1. While collecting the columns of a select-list expression, **stop at subquery boundaries**: scalar subquery, `in_list` subquery and `exists`. Represent each subquery by its `dependent_queries` entry, or by its own resolved interface columns, rather than by its raw inner references.
2. Alternatively, keep the flattened lineage but resolve each lifted reference in the **scope where it was declared** (the subquery's `table_alias`). Only references that are still unresolved there, true outer correlations such as `d.k`, should be checked against the outer scope.
3. Apply whatever rule the bare-subquery select item already uses (which correctly accepts `pp`) to subqueries nested inside expressions.

**Lineage question for the parser team.** Even where no FATAL is raised, the outer interface lists columns that are only **filters** inside a subquery, such as `g.y` above, as sources of `r`. Consumers doing column lineage will read these as value sources of the output column. Please decide whether subquery filter columns belong in outer interface lineage. If they do, mark them as inner-scope dependencies, for example by resolving `table_ref` to the base table (`t`) or tagging the subquery, rather than keeping a bare inner alias.

## Acceptance tests

Every SQL in the "Fails" table, snippet 9530 and snippet 8520 should parse with **0 FATALs** on the fixed 5.1.3 build. Every SQL in the "Passes" table should stay at 0 FATALs. Genuine outer-scope errors must still be reported. For example, this must still FATAL on `zz`:

```sql
select case when d.id in (select x from t as g where zz.y = 1) then 1 else 0 end as r from t as d
```

Current 5.1.3 correctly reports `No alias or table called 'zz'` for this case, and for the same subquery in `WHERE`. 5.0.0-3 reports nothing for either, so the fix should narrow the outer-scope check rather than revert to 5.0.0-3 behavior.

Not caused by this defect (correct 5.1.3 FATALs on real authoring errors, kept here so they are not "fixed" by accident):

- Query row 3994: `calc_grants.ti_grant` references an alias that is never defined.
- Query row 3577: `pcm_deferral_prev_ind` and `pcm_deferral_future_ind` are only defined in a commented-out join.
- Query row 32: `CAC` is never defined. `xwalk` exposes `Contact_key`, not `<Contact Key>`.
- Query row 3070: the first UNION branch aliases the column `eab_race_unknowny` (typo), so `agg.eab_race_unknown` is not in the interface.

## How to reproduce

All of the cases above, with machine-checkable expectations, are in `rmcp/docs/workplans/pss-5.1.3-parser-fix/acceptance-cases.json` (tasks `T1` and `T5`). Run them with `node run-acceptance.js <jar> --task T1,T5` from that folder. This defect is task T1 in `pss-5.1.3-parser-fix-workplan.md`.


Run with any harness that calls the direct API:

```java
SqlParserAccess accessor = new SqlParserAccess(false, false, false);
accessor.executeTheParse(sql, "SQL");            // "QUERY" for snippet 9530 and 8520
Snippet snippet = accessor.getSnippet();
snippet.getFatalErrorCount();                    // expected 0
snippet.getParserDiagnosticListJson();           // QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE entries
snippet.getSymbolTableJson();                    // see outer interface lineage above
```

Inside the RMCP repository, the same diagnostics come from:

```bash
node rmcp/tools/cli-mcp-parse.js --file <case.sql> --parser-profile candidate --component messages --status
```
