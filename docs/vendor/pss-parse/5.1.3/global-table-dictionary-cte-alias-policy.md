# Global table dictionary — CTE and query-backed alias policy (5.1.3+)

**API version:** 5.1.3  
**Status:** Canonical consumer contract  
**Related:** Phase 2.7 (complete), Phase 2.9 adjudication (closed 2026-09-01)  
**See also:** [table-and-query-dictionary-design.md](table-and-query-dictionary-design.md), [symbol-table-bucket-reference.md](symbol-table-bucket-reference.md) (`table_alias`, `def_queryN`, `interface`)

---

## Decision

Under **5.1.3**, the **global** `tableDictionary` (access object `parse.tableDictionary`) indexes **physical tables**, **schema-qualified tables**, and **tuple substitution sources** (`<[…]>`) only.

The following must **not** appear as keys in global `tableDictionary`:

| Key type | Examples | Where it lives in 5.1.3 |
|----------|----------|-------------------------|
| **WITH CTE names** | `latest_applications`, `race_data`, `student_term_crm` | `def_queryN.table_alias` as `{cte_name=queryM}` |
| **Subquery / derived-table aliases** | `agg`, `sub` | `table_alias` → `queryN` on the enclosing scope |
| **Local FROM aliases** for tuples | `comb_common` on `<[Partner_Data_Platform].[Contact].{combined_common_format}> AS comb_common` | Tuple key `<[…]>` in `tableDictionary`; alias in `table_alias` inside the CTE scope |

**5.0.0-3** often promoted CTE names and local aliases into global `tableDictionary`. That behavior is **obsolete**. Do not restore it for parity.

This is intentional — not a finalization defect. Phase 2.7 locked physical/tuple trailing-clause collection; Phase 2.9 dual-parse confirmed that apparent “missing CTE keys” in Panto clusters **A, B, C, and E** are comparison-policy artifacts when `table_alias` evidence is present.

---

## What belongs in global `tableDictionary`

Collect when the walker proves token sites on:

- Physical relations: `pdp.crf__intake_type`, `es_pdp_common.common.dim_funnel_status`
- Tuple substitutions: `<[ECRM].[Prospects].{fulfillment}>`, `<[Partner_Data_Platform].[Contact].{combined_common_format}>`
- Query substitution bodies referenced as **sources** (not as CTE alias names): `<sorter_srt_student_term_asc>`, `<student_term_sweep>` when walked as substitution nodes

Compare versions using the **last identifier after `.`** for physical keys. Schema-only qualification differences are not regressions.

---

## Where CTE and query-backed row sources are registered

### `table_alias` on the enclosing `def_queryN` scope

Each WITH member and each derived row source is wired on the **parent scope** that can reference it:

```text
def_query4.table_alias = {
  latest_applications = query1,
  activity_prospect_map = query2,
  campus_visit_activity = query3,
  p = <[ECRM].[Prospects].{fulfillment}>
}
```

- **Key:** SQL alias visible in `FROM` / `JOIN` (CTE name or table alias).
- **Value:** `queryN` (walk-time) or nested scope id — after publish, open **`def_queryN`** for the child body.

This is **richer** than 5.0.0-3’s CTE name in `tableDictionary`: it documents the alias → nested query scope edge explicitly.

### Nested scope payloads

Inside each child `def_queryN`:

- **`interface`** — output columns of that CTE / subquery
- **`filters`**, **`grouped_by`**, etc. — clause archives
- **`table_dictionary`** — physical/tuple sources **inside** that CTE body
- **`query_dictionary`** — output-column token sites for that scope

---

## Consumer tracing algorithm (alias → definition)

To resolve a CTE or query-backed alias (e.g. `latest_applications`, `ChosenContact_Combined`):

1. Start at the **statement root** `symbolTable` (`def_queryN` keys only in published trees).
2. On the scope that references the alias, read **`table_alias[alias_name]`**.
3. If the value is `queryM` / `unionK` / `valuesN`, open **`def_queryM`** (published key) in the same `symbolTable` map.
4. Read the child’s **`interface`** for output columns; use **`filters`**, **`table_dictionary`**, and **`dependent_queries`** for clause-level refs per [symbol-table-bucket-reference.md](symbol-table-bucket-reference.md).
5. For physical/tuple columns, descend through child scopes until `table_dictionary` or global `tableDictionary` yields a leaf token list.

**Do not** expect the CTE name as a global `tableDictionary` key. **Do not** treat its absence as source loss when step 2–3 succeeds.

### Tuple alias example (`comb_common`)

```sql
FROM <[Partner_Data_Platform].[Contact].{combined_common_format}> AS comb_common
```

| Bucket | Entry |
|--------|--------|
| Global `tableDictionary` | `<[Partner_Data_Platform].[Contact].{combined_common_format}>` → column token sites (refs may show `comb_common` as `table_ref` in filters) |
| CTE scope `table_alias` | `comb_common` → tuple key or inner binding |
| Global `tableDictionary` | **No** key `comb_common` |

---

## Comparison / RMCP scoring (post–2.9)

| Observation vs 5.0.0-3 | Verdict |
|--------------------------|---------|
| CTE name missing from global `tableDictionary` | **Not a regression** if `table_alias` → `queryN` exists |
| Local tuple alias missing from global `tableDictionary` | **Not a regression** if tuple substitution key retains token sites |
| Physical/tuple key missing from 5.1.3 `tableDictionary` | **Regression** — investigate collection |
| Extra `def_*` / `query_dictionary` layers in 5.1.3 | **Improvement** |

---

## Live fixture index (Panto 2.9 — closed)

| Cluster | Rows | `table_alias` evidence (5.1.3) |
|---------|------|--------------------------------|
| A | 583, 2139 | `latest_applications=query1`, `activity_prospect_map=query2`, `campus_visit_activity=query3` |
| B | 3870 | `student_term_crm=query7` |
| C | 4648, 4726 | `ST_student_term_sweep=query0` |
| E | 5455 | `ChosenContact_Combined=query2`; tuple on `{combined_common_format}` |

Clusters **D** and **F** (rows 3150, 5410) are separate: set-op interface FATALs — see [set-operation-interface-duplicate-output-names-policy.md](set-operation-interface-duplicate-output-names-policy.md).

---

## Author / tool migration checklist

- [ ] Stop asserting CTE names in global `tableDictionary` for 5.1.3+.
- [ ] Resolve CTEs via `symbolTable` → `table_alias` → `def_queryN`.
- [ ] Keep asserting physical and tuple substitution keys on global `tableDictionary`.
- [ ] Update dual-parse diff tools to use this scoring table.
