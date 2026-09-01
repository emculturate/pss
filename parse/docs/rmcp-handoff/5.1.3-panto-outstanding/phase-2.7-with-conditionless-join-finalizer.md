# Phase 2.7 — WITH CTE physical-source and tuple-substitution finalization

**Workplan tracker:** `parse/documents/parser-defects-enhancements-workplan.md` §2.7  
**Status:** **Complete** (2026-09-01)  
**Characterization tests:** `parse/src/test/java/sql/walker/SqlEventWalkerWithCteTupleSubstitutionTests.java` — conditionless join finalizer (`amount_cte` / `orders_tbl`), subquery row-source variant, `last_delivered_cte` tuple substitution, and window-scope isolation across CTE boundaries  
**Related live degradations:** [panto-tabledict-degradations-2026-08-19.md](./panto-tabledict-degradations-2026-08-19.md) (clusters A–E) — functional losses adjudicated under **2.9**, not missing CTE keys in global `tableDictionary`  
**Handoff index:** [outstanding-issues-index.md](./outstanding-issues-index.md) · [README.md](./README.md)

---

## Completion summary

Phase 2.7 is **closed**. Characterization goldens in `SqlEventWalkerWithCteTupleSubstitutionTests` show that:

1. **Physical tables and tuple substitution sources** referenced in the final `WITH` query — including columns introduced only in trailing clauses (`WHERE`, `GROUP BY`, `HAVING`, `QUALIFY`, `ORDER BY`, scalar subqueries) after conditionless or conditioned joins — are recorded on the correct keys in **global** `tableDictionary`.
2. **CTE aliases are not global `tableDictionary` keys** in 5.1.3. That is intentional; restoring 5.0.0-3-style CTE names in the physical dictionary is **out of scope and obsolete**.
3. CTE row sources remain available through **`query_dictionary`**, **`context_list`**, **`table_alias`**, **`interface`**, **`filters`**, and nested `def_*` scopes — as locked by the test goldens.

The original investigation opened because a live Panto query appeared to lose tuple-substitution evidence after `CROSS JOIN last_delivered_cte`. Compact fixtures confirmed the tuple column (`<Contact Deleted Dt>`) is present on the fulfillment tuple key across join and final-clause variants. The apparent “missing `last_delivered_cte` / `amount_cte`” delta vs 5.0.0-3 was a **comparison-rule mismatch**, not a functional defect under the 5.1.3 consumer contract.

---

## 5.1.3 consumer contract — CTE vs physical sources

| Bucket | What belongs there | CTE aliases |
|--------|-------------------|-------------|
| **Global `tableDictionary`** | Physical tables, schema-qualified tables, tuple substitution sources (`<[…]>`) | **Never** |
| **`query_dictionary` / query column dictionary** | Output and referenced columns per query scope, including CTE output columns | CTE column refs live here |
| **`def_*` symbol table** | `context_list`, `table_alias` (`amount_cte=query0`), `interface`, `filters`, per-scope `table_dictionary` for physical sources inside CTE bodies | CTE registration lives here |

**Policy (2026-09-01):** CTE names or aliases must **not** appear in global `tableDictionary`. The queries they represent are documented in `query_dictionary` and symbol-table substructures. 5.0.0-3 behavior that promoted CTE aliases into the physical table dictionary is **obsolete** and must not be restored.

Dual-parse reports that list “missing” CTE keys in global `tableDictionary` are **not** 2.7 defects when equivalent CTE evidence exists in the symbol tree. Re-score those rows under **2.9** only when source or column evidence is functionally absent everywhere.

---

## Problem investigated (resolved)

**Primary (functional):** After a conditionless `CROSS JOIN` of a CTE into the final `WITH` query, a **tuple substitution column** referenced only in a trailing clause (e.g. `ead.<Contact Deleted Dt>` in the `ELSE` branch of a `WHERE CASE`) must appear on the tuple source key in global `tableDictionary`, with correct token sites, alongside select-list substitution columns.

**Ruled out (not a defect):** Joined **CTE row sources** such as `last_delivered_cte` or `amount_cte` absent as top-level global `tableDictionary` keys while present in `context_list`, `table_alias`, `filters`, and `query_dictionary`.

#### Starter query

```sql
WITH
last_delivered_cte AS
(
    SELECT MAX(log_del.contact_deleted_dt) AS last_del
    FROM PDP_UG.log__acs_contact_deletions AS log_del
)
SELECT
        CAST(ead.<ES Partner ID> AS varchar(64)) AS es_partner_id,
        CAST(ead.<ACS Contact ID> AS varchar(50)) AS acs_contact_id
FROM <[Acquia].[exp__acquia_deletions].{fulfillment}> AS ead
CROSS JOIN last_delivered_cte
WHERE CASE
                WHEN last_delivered_cte.last_del IS NULL THEN 1=1
                ELSE ead.<Contact Deleted Dt> > last_delivered_cte.last_del
            END
```

#### Expected behavior (met)

1. Tuple substitution columns on the outer source (`ead.<…>`) referenced only after the join must appear on the tuple key in global `tableDictionary`.
2. `filters` retain the substitution ref (`{substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}`).
3. Physical-table control (`ead.contact_deleted_dt`) behaves the same for post-join `WHERE CASE` references.
4. Variants (`CAST(…)`, `COALESCE(…)`, `GROUP BY`, `HAVING`, `QUALIFY`, `ORDER BY`, scalar subquery) do not drop tuple or physical columns from `tableDictionary`.
5. CTE aliases are registered in symbol-tree / query-dictionary structures, **not** in global `tableDictionary`.

---

## Characterization matrix (locked in test class)

| Evidence | Join / source form | Trailing clause | Assertion under 5.1.3 |
|----------|-------------------|-----------------|------------------------|
| Observed reproduction | `CROSS JOIN cte_name` | `WHERE` / `CASE` | Tuple or physical outer source + CTE base table in global `tableDictionary`; CTE via `table_alias` / `filters` / `query_dictionary` |
| Characterized (2026-09) | All join variants in test class | `WHERE` | Same; `withFinalQuery*` goldens lock **no** CTE key in global `tableDictionary` |
| Predicted | `CROSS JOIN`, `NATURAL`, bare `JOIN`, comma, `INNER JOIN ON` | `GROUP BY`, `HAVING`, `QUALIFY`, `ORDER BY` | Trailing-clause column variables on outer physical/tuple source |
| Window isolation | CTE bodies with `QUALIFY` / `OVER` | outer `WITH` wrapper | No fatal bleed across scopes |
| **Not supported** | `NATURAL FULL [OUTER] JOIN` | any | **Intentional** — out of PSS scope (see below) |

**`NATURAL FULL OUTER JOIN` — intentionally unsupported (2026-09-01):** Although valid in ANSI SQL, Snowflake, and PostgreSQL, this join combination is **not supported** by PSS and will not be implemented. Two `@Ignore` tests in `SqlEventWalkerWithCteTupleSubstitutionTests` document the decision only. Use `FULL OUTER JOIN … ON` / `USING` or `NATURAL LEFT` / `NATURAL RIGHT` / `NATURAL JOIN` instead.

---

## Acceptance (met)

- Tuple and physical sources referenced in the final `WITH` query appear on the correct global `tableDictionary` keys, including trailing-clause-only references.
- CTE aliases do **not** appear in global `tableDictionary`; CTE evidence is present in `query_dictionary` and `def_*` symbol-table structures per goldens.
- `SqlEventWalkerWithCteTupleSubstitutionTests` locks join-form and trailing-clause coverage (52 tests); `NATURAL FULL OUTER JOIN` asserts fatal `NATURAL_FULL_OUTER_JOIN_UNSUPPORTED`.
- Phase 2.1–2.6 tests unchanged and passing.

---

## Re-routed to other phases

| Item | Owner | Notes |
|------|-------|-------|
| Live Panto clusters A–E (rows 583, 2139, 3150, 3870, 4648, 4726, 5455) | **2.9** | Adjudicate functional source loss; ignore CTE-name-only `tableDictionary` deltas |
| Nested-WITH wrapper template (optional extra characterization) | — | Not required for 2.7 closure |

---

## Out of scope (2.7)

- Promoting CTE aliases into global `tableDictionary` (obsolete 5.0.0-3 parity).
- `NATURAL FULL OUTER JOIN` / `NATURAL FULL JOIN` — intentionally unsupported (valid in other engines; use `FULL OUTER JOIN … ON` / `USING` or other natural join forms).
- Normalizing `log__acs_contact_deletions` versus `pdp_ug.log__acs_contact_deletions` key spelling.
- Evaluating business semantics of the starter `CASE` filter.
- Changing WITH AST numbering or treating AST-only comparison as dictionary proof.
