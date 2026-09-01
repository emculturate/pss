# Phase 2.7 — WITH final-query finalizer omits CTE sources from global `tableDictionary`

**Workplan tracker:** `parse/documents/parser-defects-enhancements-workplan.md` §2.7  
**Characterization tests:** `parse/src/test/java/sql/walker/SqlEventWalkerWithConditionlessJoinFinalizerTests.java`  
**Live `last_delivered_cte` + tuple reproduction:** `parse/src/test/java/sql/walker/SqlEventWalkerLastDeliveredCteTupleSubstitutionTests.java`  
**Subquery joined-source variant:** `parse/src/test/java/sql/walker/SqlEventWalkerWithConditionlessJoinFinalizerSubqueryTests.java` — `(SELECT … FROM orders_tbl) AS orders_tbl` instead of physical `orders_tbl`  
**Related live degradations:** [panto-tabledict-degradations-2026-08-19.md](./panto-tabledict-degradations-2026-08-19.md) (clusters A–E)  
**Handoff index:** [outstanding-issues-index.md](./outstanding-issues-index.md) · [README.md](./README.md)

---

### 2.7 — WITH final-query finalizer omits CTE sources from global `tableDictionary`

**Kind:** Defect (`tableDictionary` incomplete compared with 5.0.0-3)

**Status:** **In progress** — characterization goldens landed; fix not started

**Component:** WITH / query-finalizer walk; FROM/JOIN source collection and global dictionary merge for CTE row sources in the final primary query

#### Problem

**Primary defect (live Panto reproduction):** after a conditionless `CROSS JOIN` of a CTE into the final `WITH` query, a **tuple substitution column** referenced only in a trailing clause (e.g. `ead.<Contact Deleted Dt>` in the `ELSE` branch of a `WHERE CASE`) must be recorded on the tuple source in global `tableDictionary`. The live starter below uses `CAST(ead.<ES Partner ID> …)` / `CAST(ead.<ACS Contact ID> …)` in the select list and `ead.<Contact Deleted Dt>` only in the `WHERE CASE` after `CROSS JOIN last_delivered_cte`.

Observed consumer impact vs **5.0.0-3:** the tuple key `<[Acquia].[exp__acquia_deletions].{fulfillment}>` retains select-list substitution columns but **drops** `<Contact Deleted Dt>` from `tableDictionary` even though `filters` still reference it. That is a functional regression for tools that build source-impact sets from `tableDictionary` token sites, not merely a missing CTE name.

**Secondary comparison delta (may be intentional under 5.1.3 docs):** joined **CTE row sources** such as `last_delivered_cte` appear in `context_list`, `table_alias`, and `filters`, but often not as top-level global `tableDictionary` keys. Treat as regression only when equivalent evidence is absent everywhere in the symbol tree.

**Compact physical-table fixtures** (`SqlEventWalkerWithConditionlessJoinFinalizerTests`) isolate join-finalizer shapes with stub tables; they do not use tuple substitutions.

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

Minimal wrapper when tuple substitutions are outside the fixture's scope:

```sql
WITH last_delivered_cte AS (
    SELECT MAX(log_del.contact_deleted_dt) AS last_del
    FROM PDP_UG.log__acs_contact_deletions AS log_del
)
SELECT ead.es_partner_id, ead.acs_contact_id
FROM exp__acquia_deletions AS ead
CROSS JOIN last_delivered_cte
WHERE CASE
                WHEN last_delivered_cte.last_del IS NULL THEN 1=1
                ELSE ead.contact_deleted_dt > last_delivered_cte.last_del
            END
```

#### Expected behavior

1. Tuple substitution columns on the outer source (`ead.<…>`) referenced **only after** the conditionless join (e.g. inside `WHERE CASE`) must appear on the tuple key in global `tableDictionary` with correct token sites, alongside select-list substitution columns already present.
2. `filters` must retain the same substitution ref (`{substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}`) — necessary but not sufficient.
3. Physical-table control (`ead.contact_deleted_dt` stub) must behave the same way for post-join `WHERE CASE` references.
4. Variants (`CAST(…)`, `COALESCE(…)`) must not drop the tuple column from `tableDictionary`.
5. CTE keys in global `tableDictionary` remain a separate 5.0.0-3 parity question; adjudicate via symbol-tree evidence before forcing CTE names into the physical dictionary.

#### Characterization and regression matrix

Start with compact one-CTE fixtures and inline assertions. The first row reproduces the observed defect. The other conditionless join and trailing-clause rows probe the predicted scope; record which tests fail before changing implementation. Cover each accepted conditionless join grammar form and each trailing-clause boundary at least once, but do not build a full Cartesian product.

| Evidence | Join/source form in final query | Following clause | Required assertion |
|----------|---------------------------------|------------------|--------------------|
| **Observed reproduction** | `CROSS JOIN cte_name` as the last JOIN | `WHERE` with a CTE-only reference | Outer source, CTE key, CTE base table, and CTE reference locations are present |
| Characterized (2026-09) | Any join variant in final `WITH` query (see test class) | `WHERE` comparing CTE and joined columns | Same three-source global inventory; goldens lock current omission of CTE key |
| Predicted boundary | `CROSS JOIN cte_name` as the last JOIN | `GROUP BY` | Same source inventory; grouped CTE column resolves |
| Predicted join variant | `NATURAL JOIN cte_name` | `HAVING` | Same source inventory; finalizer does not depend on `ON` / `USING` |
| Predicted join variant | `NATURAL LEFT JOIN cte_name` | `QUALIFY` with a window expression | Same source inventory after qualify finalization |
| Predicted join variant | `NATURAL RIGHT JOIN cte_name` | `ORDER BY` | Same source inventory after ordering finalization |
| Predicted query-exit boundary | `NATURAL FULL OUTER JOIN cte_name` | no trailing predicate | Same source inventory at query-specification exit |
| Predicted grammar variant | Bare `JOIN cte_name` without a condition, where already accepted | one valid trailing clause | Same source inventory; preserve current dialect behavior |
| Predicted source-kind variant | A conditionless last JOIN introducing a physical table | `WHERE` or query exit | Both physical sources and references are retained inside the final WITH query |
| Control | Comma-style `FROM outer_source, cte_name` | `WHERE` | CTE remains present |
| Control | `INNER JOIN cte_name ON 1=1` | `WHERE` | Conditioned join remains present |
| Control | `JOIN cte_name USING (shared_col)` | `ORDER BY` | USING path remains present |

Also run a base-table-only control (`FROM t1 CROSS JOIN t2`) to prove both physical sources remain in `tableDictionary` without a `WITH` finalizer.

#### Nested-WITH duplication

Duplicate the observed reproduction and each accepted conditionless-join characterization case with the complete original `WITH` query embedded as the body of a CTE in a second, outer `WITH`. The outer final query must select only from the wrapper CTE and must **not** refer to, join, or otherwise reuse the table or CTE source introduced by the embedded query's final JOIN. This isolates dictionary propagation: the inner source and its column references must survive because the embedded query recorded and propagated them, not because the outer query encountered that source again.

Use this structural template, substituting each join and trailing-clause variant from the matrix:

```sql
WITH wrapped_result AS (
    WITH joined_cte AS (
        SELECT source_id, MAX(source_value) AS joined_value
        FROM joined_base
        GROUP BY source_id
    )
    SELECT outer_source.source_id, joined_cte.joined_value
    FROM outer_source
    CROSS JOIN joined_cte
    WHERE joined_cte.joined_value IS NOT NULL
)
SELECT wrapped_result.source_id, wrapped_result.joined_value
FROM wrapped_result
```

For every nested duplicate:

1. Assert that global `tableDictionary` still contains `outer_source`, `joined_base`, and the source introduced by the embedded final JOIN (`joined_cte` in the template).
2. Assert that the embedded source entry retains the locations for columns referenced in the embedded `SELECT`, trailing clause, or both.
3. Assert that the outer query references only `wrapped_result` output columns. Do not mention `joined_cte`, `joined_base`, or their aliases outside `wrapped_result`'s body.
4. Assert that the nested symbol tree retains both WITH/query scope levels and that no merge replaces the embedded source with only the wrapper CTE.
5. Lock all six extractor goldens for at least the nested observed `CROSS JOIN` reproduction; focused dictionary/path assertions are sufficient for the remaining nested matrix duplicates.

#### Additional live-query investigations

Use [panto-tabledict-degradations-2026-08-19.md](./panto-tabledict-degradations-2026-08-19.md) as the detailed source of truth for full SQL fixture paths, observed version differences, and cluster notes. Add its CTE-centered rows to 2.7 as live characterization tests; do not duplicate the full SQL in this workplan.

| Cluster / CSV rows | CTE-centered construction | Observed 5.1.3 difference | Required investigation |
|--------------------|---------------------------|----------------------------|------------------------|
| A — 583, 2139 | Multi-CTE WITH; outer tuple `FROM` plus conditioned `LEFT JOIN` CTEs; one CTE used only by another CTE | Missing `latest_applications`, `activity_prospect_map`, `campus_visit_activity` | Determine whether finalization loses joined CTE names, tuple-backed outer scope, or transitive CTE source inventory; compare `LEFT` and `INNER` controls |
| B — 3870 | Tuple/bound-query source `FULL OUTER JOIN student_term_crm`, then derived-table wrapper | Missing `student_term_crm` | Determine whether the loss is join-kind-specific, wrapper-specific, or tuple/CTE merge-specific |
| C — 4648, 4726 | Long WITH chain; final CTE body is query substitution `<student_term_sweep>`; outer query reads the CTE | Missing `st_student_term_sweep` | Determine whether query-typed substitution CTE definitions publish a usable interface/source identity and survive outer query finalization |
| D — 3150 | `race_data` CTE is a large UNION; outer derived query has many `UNION ALL` branches reading it | Missing `race_data` plus 20 new FATALs | First determine whether grammar/diagnostic failure aborts CTE publication or whether set-op CTE finalization independently loses the source; send any independent diagnostic root cause to 2.9 |
| E — 5455 | CTE over tuple sources; nested correlated `NOT IN`; outer query reads the CTE through another derived SELECT | Missing `chosencontact_combined` and tuple alias/source evidence for `comb_common` | Keep CTE publication/finalization in 2.7; send any independent tuple-source collection defect to 2.9 |

For each live query, dual-parse the exact SQL on both fat JARs and capture `tableDictionary`, `symbolTable`, `interface`, `sqlTree`, and `messages`. Before declaring a 5.1.3 defect, explain where each 5.0.0-3-only key is represented in 5.1.3, if anywhere: global physical `tableDictionary`, a `def_*` scope, `table_alias` / CTE context, source query dictionary, or published `interface`. The documented 5.1.3 dictionary model allows richer nested query-backed evidence and primarily treats the global table dictionary as a cross-scope physical-source collection; do not delete that richer evidence or invent physical lineage merely to reproduce a legacy key layout. If the source or its references are functionally absent, fix the loss. If 5.1.3 intentionally represents the same evidence more accurately elsewhere, document that conclusion and update comparison expectations only with approval.

#### Suggested tests

1. Exact starter with the tuple source and `CASE` predicate: assert the three-source set and all six extractor goldens (`substitutions`, `symbolTable`, `interface`, `tableDictionary`, `sqlTree`, `messages`).
2. Minimal table-stub wrapper: assert the same three logical sources (stub, CTE base table, CTE key) and six goldens.
3. CTE referenced only in a trailing clause, not the select list: the CTE remains a registered row source.
4. CTE referenced in the select list with no trailing clause: the CTE remains present at query exit.
5. `last_delivered_cte.not_a_col`: assert the query-alias/interface column diagnostic and the retained CTE key.
6. Join-form and trailing-clause matrix above, preserving the observed/predicted/control labels in test names or comments so characterization results document the actual defect boundary.
7. Nested-WITH duplicates described above: the outer query uses only the wrapper CTE interface, while the global dictionary still retains the embedded final-JOIN source and its column references.
8. Live Panto clusters A–E: add full-query tests plus one minimized characterization per distinct root cause; retain the detailed brief as the fixture index.

#### Acceptance

- The exact starter's `tableDictionary` contains the same set of three logical sources as 5.0.0-3: fulfillment tuple, `last_delivered_cte`, and the CTE's physical log table. Folded or schema-qualified spelling of the physical table may differ; `last_delivered_cte` must not be absent.
- The retained CTE entry includes its observed reference locations, and `last_delivered_cte.last_del` resolves through the CTE interface.
- Conditionless join source registration is complete before `WHERE`, `GROUP BY`, `HAVING`, `QUALIFY`, `ORDER BY`, or final query exit.
- The observed `CROSS JOIN` case is fixed. Every predicted case that reproduces the omission is fixed through the same source-registration path; predicted cases that already pass remain locked as regressions and are not evidence for unrelated rewrites.
- Conditioned JOIN, comma-style, direct-CTE-FROM, physical-table, and non-WITH controls remain correct.
- Nested-WITH duplicates retain the embedded query's complete source inventory and reference locations even though the outer query never reuses the source introduced by the embedded final JOIN.
- Every live Panto CTE case has a written 5.0.0-3 versus 5.1.3 explanation identifying the owning scope/bucket and root cause before a fix or comparison-rule change is accepted.
- Rows 583, 2139, 3870, 4648, 4726, and the CTE portion of 5455 retain all functionally required CTE source/interface evidence. Row 3150 retains `race_data` once any blocking diagnostic path is resolved or separately adjudicated under 2.9.
- Six extractor goldens are locked for the starter or minimal wrapper.
- Six extractor goldens are also locked for the nested observed reproduction; focused assertions cover the remaining nested duplicates.
- Phase 2.1–2.6 tests remain unchanged and pass.

#### Out of scope (2.7)

- Normalizing `log__acs_contact_deletions` versus `pdp_ug.log__acs_contact_deletions` key spelling or folded case.
- Evaluating the business semantics of the starter's `CASE` filter.
- Changing WITH AST numbering or treating an AST-only comparison as proof that dictionary finalization is correct.
- Making currently invalid conditionless join syntax valid; test only forms already accepted by the grammar, with ordinary `ON` / `USING` joins as controls.
- Blindly forcing every legacy CTE key into the physical table dictionary when 5.1.3 already preserves equivalent query-backed evidence through the documented `def_*` / `interface` / alias contract.
