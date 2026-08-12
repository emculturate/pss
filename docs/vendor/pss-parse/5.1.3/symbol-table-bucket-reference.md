# Symbol Table Bucket Reference

## Introduction

**Audience and version**

- Consumer guide for services that read and interpret PSS symbol tables.
- Applies to symbol tables produced by the PSS SQL Parser JAR **version 5.1.3 or greater**.

**What it is**

- A symbol table is a **nested map** (`Map<String, Object>`) built while the SQL parse tree is walked.
- It represents the logical elements of a portion of a SQL statement, analogous to symbol tables in programming languages: **visibility**, **coherence**, and **uniqueness** of column references within a scope stack.

**What it tracks**

- Where variables (columns) are **created**.
- Where they **can and cannot be seen**.
- How **accessibility propagates** up and down the execution stack of a running SQL statement.

**What it deliberately omits**

- The symbol table does **not** retain the full knowledge and logic applied to the data held in these variable objects.
- It **does** provide **traceability** from sources, through logical layers, to the outputs of any SQL statement.

**The core tracing problem**

One of the hardest things to follow in SQL is how a value in one source variable moves through layers of logic to become the values under other variable names that emerge from the statement's output interface. PSS supports two complementary consumer procedures:

- [Recursive column tracing](#recursive-column-tracing-consumer-algorithm) — **reference → sources** (given a `{name, table_ref}` in a bucket, find leaf origins).
- [Source column impact tracing](#source-column-impact-tracing-consumer-algorithm) — **source → impacts** (given a physical table column from the global table dictionary, find every scope and bucket where it is used or re-exported).

**Scopes, buckets, and nesting**

- Each **scope** (query, CTE body, set-op branch, DML statement, predicate subquery, etc.) accumulates **buckets** on a stack.
- At scope exit, the finalized payload is published under a **`def_*`** key.
- Inside the outermost `def_*` bucket, other `def_*` symbol tables often appear, each with its own buckets.

**The `interface` bucket as the public boundary**

- Only column variables in a scope's **`interface`** bucket are visible **outside** that symbol table.
- The `interface` bucket is the window into an otherwise temporary namespace.

**SQL as a functional model**

- PSS treats SQL statements as a **functional language** over a multi-dimensional set of domain objects.
- The output is a sequence of **rows** (tuples) of related **columns**.
- PSS presumes the author knows what these relationships symbolize and makes **no statement** on logical correctness of semantics.

**Companion artifacts and validation**

Parser validation processes use symbol tables to provide **syntactic maps** and **accuracy checks** for subtle mistakes an author may make. These validations appear as **Diagnostic** messages of varying severity, alongside:

- An **AST** representation of the original statement
- Global **table** and **query** dictionaries
- A collection of **substitution variables** (author-permitted variable logic for customization scenarios)
- A formal array of **interface columns** produced by the SQL statement — i.e., the SQL "function" output

**Related docs:** [table-and-query-dictionary-design.md](table-and-query-dictionary-design.md), [relational-modifier-resolution-policy.md](relational-modifier-resolution-policy.md).

---

## What symbol tables help you answer

- **Why** is this source column needed?
- **Where** is it used?
- **What role or roles** does it play in the computation?

A single source column may support **multiple roles** at once, or appear for **only one purpose**:

1. **Input to further logic** — the column feeds expressions, functions, or derived values that produce still other variables. Traced primarily through **`interface`** lineage entries and, for PIVOT/UNPIVOT, through **`derivation`**.
2. **Pass-through** — the value is carried unchanged (or under a new alias) through temporary variable names across symbol-table layers until it appears in the **output `interface`** of the main query. Follow **`interface`** and nested **`def_*`** scopes from inner sources outward.
3. **Filtering** — the column participates in logical conditions that restrict which rows survive: `WHERE`, `HAVING`, `QUALIFY`, `JOIN ON`, predicate subqueries, and similar. Archived in **`filters`**; predicate subqueries additionally indexed under **`dependent_queries`**.
4. **Grouping, ordering, and windowing** — the column is required for set-aggregation, sort order, or window partitioning/analysis. Archived in **`grouped_by`**, **`ordered_by`**, **`window_partition_by`**, and **`window_ordered_by`**.
5. **Set and relational operations** — the column aligns branches in **`UNION`** / **`INTERSECT`** / **`EXCEPT`**, participates in join keys, or supports DML target mapping via **`assignments`** and related DML buckets.
6. **Other purposes** — including substitution placeholders, modifier operands, EXISTS/IN correlation, and clause references that never reach the output interface but still constrain or shape the result.

When interpreting a source column, walk **all relevant buckets** in each scope — not only **`interface`**, because a column may be essential even when it never surfaces in the final output.

To trace any reference to its origin, follow [Recursive column tracing](#recursive-column-tracing-consumer-algorithm). To audit how a physical source column is used across the statement — what actions it participates in and how derived names propagate outward — follow [Source column impact tracing](#source-column-impact-tracing-consumer-algorithm).

| Role (summary) | Primary buckets |
|----------------|-----------------|
| Input to further logic | `interface`, `derivation` |
| Pass-through to output | `interface`, nested `def_*` |
| Filtering | `filters`, `dependent_queries` |
| Grouping / ordering / windowing | `grouped_by`, `ordered_by`, `window_partition_by`, `window_ordered_by` |
| Set ops / joins / DML mapping | `setop`, `assignments`, `target_table` |
| Other | `filters`, `dependent_queries`, substitution objects |

---

## SQL visibility conventions

The PSS Parser follows standard SQL column-visibility rules. The symbol table encodes these rules through nested scopes, **`table_alias`**, **`interface`**, **`context_list`**, and correlated-reference handling. Understanding visibility is prerequisite to reading any symbol-table payload.

**How columns enter a statement**

- Columns must enter a SQL statement as a value from some **source**.
- The most common original sources are:
  - **Tables** (physical row sources)
  - **Table functions** (e.g. `TABLE(FLATTEN(…))`)
  - **`VALUES`** statements (literal row constructors)
- Additional sources (subqueries, set operations, relational modifiers) are built on top of these originals through nested query logic.

**Nesting and transformation**

- Through the SQL language, queries can be **nested** to provide transformation logic on top of sources.
- A nested query may transform:
  - **Original sources** (tables, `VALUES`, table functions) directly, or
  - The **output `interface`** of another query that sits in the nested query's visibility space.

**What a query can see**

A query may reference columns from three visibility lanes:

| Lane | Rule | Symbol-table expression |
|------|------|-------------------------|
| **Local `FROM` / `JOIN` sources** | A query can see the **output columns** of every table, subquery, `VALUES` source, table function, or other row source listed in **its own `FROM` / `JOIN` clause** | `table_alias` maps alias → source; columns resolved through that source's **`interface`** |
| **Outer (correlated) context** | A query nested inside another query may also see columns from **any enclosing query** whose sources are in scope above it | **Correlated reference** — inner scopes inherit visible outer aliases via `pushSymbolTableWithParentVisibleScope`; unresolved outer refs may appear in `unresolved_column` during the walk |
| **`WITH` (CTE) names** | CTE bodies and the main query body share a **`context_list`** of named query references visible to nested scopes. In a `WITH` clause, **later CTEs can reference earlier CTEs** in the same `WITH` list (**ordered forward visibility**). | `context_list={cte_alias=queryN, …}` — accumulated as each CTE is finalized left-to-right |

**How the symbol table enforces sibling encapsulation**

The symbol table enforces visibility by **encapsulating sibling queries** — whether they appear in a **`FROM` / `JOIN` sequence** or as **CTEs in the main query's `WITH` statement** — as their own sibling **`def_*` symbol tables**. Each sibling is a separate published scope with its own buckets; nothing from one sibling's internal namespace leaks into another unless it is exported through that sibling's **`interface`**.

From the symbol table's point of view, the **only structural difference** between these two sibling patterns is how cross-sibling visibility is signaled:

| Sibling pattern | Encapsulation | Cross-sibling visibility |
|-----------------|---------------|--------------------------|
| **`FROM` / `JOIN` subqueries** | Each derived table → its own **`def_queryN`** (sibling scopes under the parent query) | **None** — siblings are independent; a subquery sees only its own `FROM` sources plus correlated outer context |
| **`WITH` CTEs** | Each CTE body → its own **`def_queryN`** (sibling scopes under the `WITH` frame) | **`context_list`** — registers each finalized CTE name and carries **ordered forward visibility** into sequentially encountered CTE bodies and into the main query body |

In both cases, siblings are **`def_*` peers**. `WITH` simply adds **`context_list`** so that each subsequent CTE (and eventually the main query) can resolve names published by earlier siblings — matching SQL's left-to-right CTE chaining rule.

**Correlated reference**

A **correlated reference** means the inner context of a subquery can access the value of a source column defined by a query **it is nested inside** (an ancestor scope), even when that column is not in the inner query's own `FROM` clause. PSS records these references when an inner scope resolves a column against an outer alias or table source.

**What a query cannot see**

| Restriction | Rule |
|-------------|------|
| **Sibling subqueries** | A query **cannot** see columns defined **inside a sibling** subquery. Siblings in `FROM` / `JOIN` are fully independent. Siblings in `WITH` are also encapsulated as separate **`def_queryN`** scopes, but **earlier** CTEs are visible to **later** ones via **`context_list`** (ordered forward visibility only — not backward). |
| **Grandchild columns** | A query **cannot** see columns defined inside its **grandchild** queries (or deeper descendants) unless those columns have been **passed up** through each intermediate scope and appear in the **`interface`** of the query's **direct child** subquery. |
| **Inner scope internals** | An outer query **cannot** drill into the internal aliases or private buckets of a child scope — only the child's published **`interface`** columns are visible outward. |

**Scalar subqueries and `FROM` subqueries**

These visibility rules apply equally to:

- **Subqueries in `FROM` / `JOIN`** (derived tables), and
- **Scalar and predicate subqueries** (`WHERE col = (SELECT …)`, `EXISTS`, `IN`, quantified comparisons).

Predicate subquery bodies are published as nested **`def_queryN`** scopes indexed on the parent via **`dependent_queries`**. The parent does **not** merge the child's internal dictionaries; correlated outer columns inside the subquery resolve against inherited outer visibility, not by flattening the child's private namespace into the parent.

**Important boundary:** Parent scopes do **not** drill into predicate subquery bodies for dictionary collection. Inner references belong to the child's **`def_queryN`**. Correlated outer columns inside a subquery bubble via `unresolved_column` during the walk and resolve in the enclosing scope.

---

## Master reference: scopes, keys, and row sources

Single catalog for **published scope keys**, **live walk-time keys**, **row-source leaves**, and **wrappers**. Consumers should read **`def_*`** payloads, not live keys.

| Key / source | Category | SQL construct | Primary buckets | Visibility / trace notes | Example |
|--------------|----------|---------------|-----------------|--------------------------|---------|
| **`def_queryN`** | Published scope | `SELECT`, subquery, CTE body, CTAS / INSERT / UPDATE source | `interface`, `query_dictionary`, `table_dictionary`, `table_alias`, clause buckets, nested `def_*` | Public export = `interface` only; siblings encapsulated as peer `def_*` | `SELECT x.a FROM (SELECT a FROM tab1) x` → outer `def_query1` with `table_alias={x=query0}`, child `def_query0` |
| **`def_valuesN`** | Published scope | `VALUES` in `FROM` or `INSERT` source | `interface`, `query_dictionary` | Own scope; column names from position/alias | `FROM (VALUES (1,'a')) AS dd` → `def_values0` |
| **`def_unionN`** | Published scope | **`UNION`** and **`EXCEPT`** | Nested `def_query*`, composite `interface`, `setop` on non-anchor branches | **No `def_exceptN`** — `EXCEPT` uses `def_unionN` + `setop=EXCEPT` | `SELECT … UNION SELECT …` → `def_union2` with `interface={col1=query_column}` |
| **`def_intersectN`** | Published scope | **`INTERSECT`** | Same as union shell | Non-anchor branches may carry `setop=INTERSECTION` | `… INTERSECT …` → `def_intersect2` |
| **`def_insertN`** | Published scope | `INSERT` | `interface`, `target_table`, nested `def_queryN` / `def_valuesN` | Source nested as child scope | `INSERT INTO t SELECT …` |
| **`def_updateN`** | Published scope | `UPDATE` | `assignments`, `update_dictionary`, `filters`, optional nested `def_queryN` | `UPDATE … FROM (subquery)` nests source | `SET score = src.col` → `assignments={score=[{name=col, table_ref=src}]}` |
| **`def_deleteN`** | Published scope | `DELETE` | `table_dictionary`, `filters` | WHERE subqueries via parent `dependent_queries` | — |
| **`def_createN`** | Published scope | `CREATE` (table, view, …) | Often `{}`; CTAS nests `def_queryN` | — | `CREATE TABLE AS SELECT …` |
| **`def_alterN`** | Published scope | `ALTER` | Typically `{}` | — | — |
| **`def_dropN`** | Published scope | `DROP` | Typically `{}` | — | — |
| **`def_truncateN`** | Published scope | `TRUNCATE` | Typically `{}` | — | — |
| **`queryN`**, **`valuesN`**, **`unionN`**, **`insert0`**, … | Live key (walk-time) | Same as matching `def_*` | Ephemeral pointer during walk | **Do not consume** — removed at publish | `table_alias={sub=query0}` before finalize |
| **`SCRIPT["N"]`** | Script wrapper | Multi-statement script | Per-statement root `def_*` payload | Not a `def_*` type itself | `SCRIPT: {"1": def_query0, "2": def_insert0}` |
| **Physical table** | Row-source leaf | `FROM tab1`, `JOIN sch.tbl` | `table_dictionary`, `table_alias` | **No catalog verification** — parser assumes column names are correct | `table_dictionary={tab1={a=[[@…]]}}` |
| **Table function** | Row-source leaf (in query) | `TABLE(FLATTEN(…))`, generators | `table_alias`, `table_dictionary` under generated alias (`flatten0`) | Lives inside parent `def_queryN`, not separate `def_*` | `table_alias={f=flatten0}` |
| **PIVOT / UNPIVOT** | Modifier (in query) | Relational modifier in `FROM` | **`derivation`** (`source_columns`, `derived_columns`, …) | Bucket keys: `tuple_0`, `outer_up`, `unpvt` | UNPIVOT → `derivation.source_columns={outer_up=[…]}` |

**Implementation notes (scopes):**

- Numeric suffix **`N`** is per-walk, not globally meaningful across statements.
- Set-op **anchor** branch has no `setop`; later branches carry `UNION`, `EXCEPT`, or `INTERSECTION`.
- Composite set-op `interface` may use sentinel **`query_column`** until branches align.
- Nested trees are normal: `def_query4` → `def_union2` → `def_query0` + `def_query1`.

### Authoring context

Services that read symbol tables need to understand not only bucket keys but **how SQL row sources differ in what the parser can know about them**:

- **Physical tables** are treated as fixed matrix-format structures. The PSS Parser has **no corroborating catalog** for them and **cannot verify** that referenced column names actually exist on the named table. It assumes the author has chosen column names correctly.
- **`VALUES`** statements and several other SQL constructs are **defined entirely within the statement text**. Unlike tables, these appear as their own **`def_*` symbol-table scopes**, each carrying `interface`, `query_dictionary`, and related buckets where applicable.
- Within a scope, **`query_dictionary`** and **`interface`** share the **same column key list** but serve different purposes:
  - **`query_dictionary`** — maps each output column name to **token positions** in the original SQL (line and character references for every appearance of that name in the query).
  - **`interface`** — maps each output column name to the **source columns** that directly produce its value (immediate dependencies only; not the full expression logic).
- A **singular** `interface` entry usually means a column pulled up from a row source. **Multiple** entries mean a formula or operator combined several source columns. Sources include tables, subqueries, `VALUES`, table functions, relational operators (PIVOT/UNPIVOT), and in rare dialect cases DML statements used as row sources.

### External vs internal row sources

Row sources from which `interface` entries may draw columns fall into two broad categories: **externally defined** sources the parser cannot fully validate, and **internally defined** sources whose column structure is established within the SQL text itself.

**Physical tables (externally defined — no catalog)**

- Table sources represent **fixed structures in matrix format** for which the PSS Parser has **no corroborating knowledge or catalog**.
- The parser therefore **assumes the author has been perfect** in choosing column names from tables — it has no way of verifying that a referenced column actually exists on the named table.
- Column references from tables are recorded in **`table_dictionary`** (table name → column → token positions). Lineage through a table alias appears in **`interface`** with `table_ref` pointing at the alias.

**Internally defined row sources (published as `def_*` scopes)**

Unlike physical tables, constructs defined entirely within the SQL text (`SELECT`, `VALUES`, set operations, DML shells, etc.) finalize as **`def_*` scopes** carrying their own `interface`, `query_dictionary`, and related buckets. See the [master scope table](#master-reference-scopes-keys-and-row-sources) above for the complete key list and examples.

**Other row sources (within a `def_queryN` scope, not separate `def_*` types)**

- **Table functions** — appear as aliased row sources in `table_alias` and `table_dictionary` (e.g. `flatten0`, `generator0`) within a parent query scope.
- **Relational operators** (PIVOT, UNPIVOT) — modifier-derived columns tracked in **`derivation`**; source columns referenced via modifier bucket keys (`tuple_0`, `outer_up`, `unpvt`, etc.).
- **DML statements as row sources** — in rare dialect-specific situations, an `UPDATE` or similar may appear as a `FROM` source; treated as a nested scope within the consuming query.

**Script-level wrapper (not a `def_*` type)**

Multi-statement scripts collect per-statement symbol tables under a **`SCRIPT`** key keyed by statement number (`"1"`, `"2"`, …), each value being the statement's root `def_*` payload.

### Row-source taxonomy

| Category | Examples | Published as `def_*`? | Parser knowledge |
|----------|----------|----------------------|----------------|
| **Externally defined** | Physical tables (`tab1`, `sch.tbl`) | No — referenced via `table_dictionary` / `table_alias` | **None** — column names taken on faith from the SQL text |
| **Internally defined — query-like** | `SELECT`, subqueries, CTE bodies | **`def_queryN`** | Full — columns established by the statement |
| **Internally defined — literal rows** | `VALUES (…), (…)` | **`def_valuesN`** | Full — column names from position or alias |
| **Internally defined — set operations** | `UNION`, `EXCEPT`, `INTERSECT` | **`def_unionN`** / **`def_intersectN`** | Full — composite `interface` aligned across branches |
| **Internally defined — DML** | `INSERT`, `UPDATE`, `DELETE` | **`def_insertN`**, **`def_updateN`**, **`def_deleteN`** | Full for statement structure; may nest `def_queryN` / `def_valuesN` |
| **Internally defined — DDL** | `CREATE`, `ALTER`, `DROP`, `TRUNCATE` | **`def_createN`**, **`def_alterN`**, **`def_dropN`**, **`def_truncateN`** | Minimal payloads; CTAS nests `def_queryN` |
| **Within a query scope (not separate `def_*`)** | Table functions, PIVOT/UNPIVOT | No — live inside parent **`def_queryN`** | Full within the query; modifiers use **`derivation`** |
| **Script container** | Multi-statement scripts | **`SCRIPT["N"]`** wrapper (not `def_*`) | One root `def_*` payload per statement |

---

## Master reference: bucket keys

All bucket keys in one place. **Column-ref buckets** use `{name, table_ref}` lists (or substitution objects). **Dictionary buckets** use token strings `[@line,start:end='text',<type>,line:col]`.

| Bucket | Type | When present | Purpose | Example |
|--------|------|--------------|---------|---------|
| **`interface`** | `Map<outCol, List<{name, table_ref}>>` | Every query/values/set-op scope | **Public export** — immediate source dependencies per output column; only outward-visible columns | `{da=[{name=a, table_ref=b}], ca=[{name=a, table_ref=a}]}` |
| **`query_dictionary`** | `Map<outCol, List<token>>` | Every query scope | Same keys as `interface`; values = **SQL text positions** for that output name | Shares keys with `interface`; singular entry = pass-through; multiple = formula deps (logic not stored) |
| **`table_dictionary`** | `Map<table, Map<col, List<token>>>` | Scopes with physical/function sources | Physical column → token positions | `{tab1={a=[[@1,7:11='apple',…]]}}` |
| **`table_alias`** | `Map<alias, refString>` | `FROM` / `JOIN` present | Alias → table name, `queryN`, `valuesN`, or CTE name | `{x=query0, l=query0, tab1}` |
| **`context_list`** | `Map<cteAlias, queryRef>` | `WITH` or inherited outer context | CTE forward visibility registry | `{left_cte=query0, right_cte=query1}` |
| **`parent_cte`** | string / map | CTE shadowing | Optional CTE chain metadata | — |
| **`filters`** | `List<{name, table_ref}>` | WHERE, HAVING, QUALIFY, JOIN ON | Clause column refs | `WHERE subj_cd IN (…)` → `filters=[{name=subj_cd, table_ref=tab1}]` |
| **`grouped_by`** | `List<{name, table_ref}>` | GROUP BY | GROUP BY column refs | — |
| **`ordered_by`** | `List<{name, table_ref}>` | ORDER BY (query-level) | ORDER BY column refs | — |
| **`window_partition_by`** | `List<{name, table_ref}>` | `OVER (PARTITION BY …)` | Partition columns (**not** `window_over_partition`) | `PARTITION BY k_stfd, kppi` |
| **`window_ordered_by`** | `List<{name, table_ref}>` | `OVER (… ORDER BY …)` | In-OVER order columns | `ORDER BY OBSERVATION_TM` |
| **`dependent_queries`** | `Map<kindN, {query, type?}>` | Parent of predicate/scalar subquery | Index to nested subquery body | `{in_list1={query=query0, type=filters}}` |
| **`predicandN`** | entry under above | `= (SELECT …)`, scalar in SELECT | Scalar subquery | — |
| **`existsN`** | entry under above | `EXISTS (SELECT …)` | EXISTS subquery | — |
| **`in_listN`** | entry under above | `IN (SELECT …)` | IN-list subquery | — |
| **`quantifiedN`** | entry under above | `ANY` / `ALL` subquery | Quantified comparison | — |
| **`type`** (on dependent entry) | string | Always on dependent entry | Clause context — same string as a parent-scope archive bucket key: `interface`, `filters`, `grouped_by`, `ordered_by`, `window_partition_by`, `window_ordered_by`, `assignments`, `derivation` | `type=window_partition_by` |
| **`derivation`** | map | PIVOT / UNPIVOT | Modifier lineage parent (**singular**, not `derivations`) | See sub-rows below |
| **`derivation.derived_columns`** | `Map<bucket, Map<col, List<token>>>` | PIVOT/UNPIVOT | Generated output columns | `{outer_up={sales_amount=[[@…]]}}` |
| **`derivation.source_columns`** | `Map<bucket, List<{name, table_ref}>>` | PIVOT/UNPIVOT | Operand source columns | `{outer_up=[{name=jan_sales, table_ref=my_table}]}` |
| **`derivation.pivot_derived_source_bindings`** | map | PIVOT | Derived aggregate → operand source | — |
| **`derivation.interface_source_ref`** | string | finalize | Modifier interface alias | — |
| **`derivation.source_ref`** | string | finalize | Physical source ref | — |
| **`assignments`** | `Map<lhsCol, List<{name, table_ref}>>` | UPDATE | SET target ← RHS lineage | `{score=[{name=acct_sales_count, table_ref=src}]}` |
| **`update_dictionary`** | `Map<col, List<token>>` | UPDATE | LHS column token positions | — |
| **`target_table`** | mini `table_dictionary` | INSERT/UPDATE/SELECT INTO | DML target columns | — |
| **`lhs_unresolved_columns`** | map | UPDATE (unresolved LHS) | Pending LHS resolution | — |
| **`insert_source_ref`** | string | INSERT with SELECT/VALUES | Pointer to nested source `def_*` | — |
| **`setop`** | `UNION` \| `EXCEPT` \| `INTERSECTION` | Non-anchor set-op branch | Operator that introduced branch | `setop=UNION` on `def_query1` inside `def_union2` |

### Bucket groups — how to read them

The master table above is the canonical key list. The subsections below explain **what each group of buckets means** and how consumers should interpret them — without repeating the key catalog.

**Core lineage (`interface`, `query_dictionary`, `table_dictionary`, `table_alias`)**

- **`interface`** is the **primary lineage map** — what the scope exports and where each output column comes from.
- **`query_dictionary`** shares the same output-column keys but maps each to **SQL text positions** (`[@line,start:end='text',…]`).
- **`table_dictionary`** holds physical (or table-function) column → token positions; keys are canonical table names (may be qualified, e.g. `sch1.aaa`).
- **`table_alias`** maps alias → source string (`tab1`, `queryN`, `valuesN`, or CTE name). Values are simple strings, not token lists.

**Clause column-reference archives (`filters`, `grouped_by`, `ordered_by`, `window_partition_by`, `window_ordered_by`)**

These buckets list columns referenced in specific SQL clauses. Values are **lists of column refs** (`{name, table_ref}` or substitution objects), not token strings.

| Bucket | SQL clause |
|--------|------------|
| **`filters`** | WHERE, HAVING, QUALIFY, JOIN ON |
| **`grouped_by`** | GROUP BY |
| **`ordered_by`** | ORDER BY (query-level) |
| **`window_partition_by`** | `OVER (PARTITION BY …)` |
| **`window_ordered_by`** | `OVER (… ORDER BY …)` |

> **Note:** There is no bucket named `window_over_partition`. Window PARTITION BY and in-OVER ORDER BY are archived separately. Partition/order-only column names are kept out of `query_dictionary`; SELECT-list window outputs merge OVER dependencies onto `interface`.

**CTE / WITH visibility (`context_list`, `parent_cte`)**

- **`context_list`** registers CTE aliases visible to nested queries; values are live refs like `query0` (resolve to sibling `def_queryN`).
- **`parent_cte`** carries optional inherited CTE chain metadata when shadowing occurs.
- Walk-time keys (`inherited_visible_aliases`, `outer_context_list_backup`, etc.) are stripped before publish.

**Predicate and dependent subqueries (`dependent_queries`, `predicandN`, `existsN`, `in_listN`, `quantifiedN`)**

- **`dependent_queries`** is the parent-scope index of nested subquery bodies reached through predicate frames (scalar/`predicand_subquery`, `EXISTS`, `IN (SELECT …)`, quantified comparisons).
- Each entry carries `{query: queryN, type: …}` where **`type` is always the same string as the parent-scope clause archive bucket** that encloses the subquery site in SQL. Consumers can treat `type` as the bucket name — there is no parallel vocabulary.
- **Entry kinds** (map keys under `dependent_queries`):
  - **`predicandN`** — scalar subquery `(SELECT …)` in a value position
  - **`existsN`** — `EXISTS (SELECT …)`
  - **`in_listN`** — `IN (SELECT …)` / `NOT IN (SELECT …)`
  - **`quantifiedN`** — `= ANY (SELECT …)` / `ALL` / `SOME` comparisons
- **`type` values** (complete set today — each matches a `MumbleConstants` bucket key):

| `type` value | SQL site | Parent archive bucket (may be empty) |
|--------------|----------|--------------------------------------|
| `interface` | SELECT list / expression tree | `interface` |
| `filters` | WHERE, HAVING, QUALIFY, JOIN ON, boolean predicates | `filters` |
| `grouped_by` | GROUP BY expression | `grouped_by` |
| `ordered_by` | Query-level ORDER BY sort key | `ordered_by` |
| `window_partition_by` | `OVER (PARTITION BY …)` argument | `window_partition_by` |
| `window_ordered_by` | `OVER (… ORDER BY …)` sort key | `window_ordered_by` |
| `assignments` | UPDATE (or MERGE) `SET col = (SELECT …)` RHS | `assignments` |
| `derivation` | PIVOT / UNPIVOT value positions (e.g. `DEFAULT ON NULL ((SELECT …))`, `IN (ANY ORDER BY (SELECT …))`) | `derivation` (operand lineage under `derivation.source_columns`) |

- **Unnamed scalars:** A `dependent_queries` entry is recorded whenever a predicate frame closes, even when the scalar subquery has **no output name** and the corresponding clause archive list is **empty** (e.g. `ORDER BY (SELECT …)` with `ordered_by=[]`, or `PARTITION BY (SELECT …)` with `window_partition_by=[]`). The index exists to reach the nested **`def_queryN`** body; the clause bucket is optional column-ref metadata.
- **How to consume:**
  1. Read `dependent_queries.<kind>N` → `{query: queryN, type: <bucket>}` on the parent `def_*` scope.
  2. Open nested **`def_queryN`** on that same parent payload (sibling of `dependent_queries`, not merged into parent dictionaries).
  3. Use **`type`** to know which clause archive bucket describes the outer SQL site; use the child scope for everything inside the subquery.
  4. For correlated references, trace **up** from the child via inherited outer `table_alias` / `context_list` — parents do not flatten inner aliases.
- **Important boundary:** Parent scopes do **not** drill into predicate subquery bodies for dictionary collection during the walk. Inner references belong to the child's **`def_queryN`**. Correlated outer columns bubble via `unresolved_column` during the walk.
- **Out of scope for `dependent_queries`:** `FROM` / `JOIN` derived tables, CTE bodies, `INSERT … SELECT` source queries, and PIVOT `IN (SELECT …)` list subqueries use nested **`def_queryN`** / `table_alias` / `insert_source_ref` instead — they are row sources, not predicate-frame dependents.

**Relational modifiers — PIVOT / UNPIVOT (`derivation` and sub-buckets)**

- The bucket is **`derivation`** (singular), not `derivations`.
- Bucket keys inside `derivation` are typically `tuple_0`, `tuple_1`, or the modifier alias (`outer_up`, `unpvt`).
- Use **`derivation.source_columns`** for operand lineage and **`derivation.derived_columns`** for generated output token positions.
- **`derivation.pivot_derived_source_bindings`** maps derived aggregate names back to operand source columns (PIVOT only).

**DML-specific buckets (`assignments`, `update_dictionary`, `target_table`, `insert_source_ref`, …)**

- **`assignments`** maps UPDATE SET target columns to RHS lineage (`{name, table_ref}` lists).
- **`target_table`** is a mini `table_dictionary` for INSERT/UPDATE/SELECT INTO targets.
- **`insert_source_ref`** points at the nested `def_queryN` / `def_valuesN` source for INSERT.

**Set operations (`setop`, composite `def_unionN` / `def_intersectN`)**

- Anchor (first) branch has no `setop` key; later branches carry `setop=UNION`, `setop=EXCEPT`, or `setop=INTERSECTION`.
- Composite parent `interface` may use sentinel **`query_column`** until branch columns align at finalize.
- Set-op scopes can nest: `def_query4` may contain `def_union2` containing `def_query0` + `def_query1`.

**Script-level nesting (`SCRIPT`)**

Multi-statement scripts wrap per-statement symbol tables under `SCRIPT: {"1": <def_*>, "2": <def_*>, …}`. Each `sql_statement` is walked in isolation; statement numbers are 1-based string keys.

### `query_dictionary` and `interface`

Within the same scope, **`query_dictionary`** and **`interface`** share the **same list of output-column keys**.

| Bucket | Same keys | Different values |
|--------|-----------|-------------------|
| **`query_dictionary`** | Output column names | **SQL text positions** — line/character references for every appearance of that column name in the query |
| **`interface`** | Output column names | **Immediate source dependencies** — list of `{name, table_ref}` used to produce each output's direct value |

- A **singular** `interface` entry usually means a column pulled up from a row source with little transformation.
- **Multiple** entries mean a formula or operator combined several source columns.
- The symbol table records **dependencies only** — not the expression, function, or operator logic.
- Row sources for `interface` entries include tables, subqueries, `VALUES`, table functions, relational operators (PIVOT/UNPIVOT), and in rare dialect cases DML statements used as row sources.

For set-op composite scopes, `interface` values may use the sentinel **`query_column`** meaning "column aligned across branches," resolved at finalize.

### Reading bucket payloads — illustrative examples

The master bucket table above is the canonical key list. The examples below show how common SQL patterns appear in published payloads.

**`interface` — join of two aliased sources**

```
interface={
  da=[{name=a, table_ref=b}],
  ca=[{name=a, table_ref=a}]
}
```

**`FROM` subquery nesting**

```sql
SELECT x.a FROM (SELECT a FROM tab1) AS x
```

```
def_query1={
  table_alias={x=query0},
  interface={a=[{name=a, table_ref=x}]},
  def_query0={
    table_alias={tab1},
    interface={a=[{name=a, table_ref=tab1}]},
    table_dictionary={tab1={a=[[@…]]}}
  }
}
```

**Window functions — `window_partition_by` / `window_ordered_by`**

> There is no bucket named `window_over_partition`. PARTITION BY and in-OVER ORDER BY are archived separately. Partition/order-only names stay out of `query_dictionary`; SELECT-list window outputs merge OVER dependencies onto `interface`.

```sql
SELECT ROW_NUMBER() OVER (PARTITION BY k_stfd, kppi ORDER BY OBSERVATION_TM) AS key_rank FROM tab1
```

```
window_partition_by=[{name=k_stfd, table_ref=null}, {name=kppi, table_ref=null}]
window_ordered_by=[{name=OBSERVATION_TM, table_ref=null}]
interface={key_rank=[{name=k_stfd, table_ref=tab1}, {name=kppi, table_ref=tab1},
                     {name=OBSERVATION_TM, table_ref=tab1}, {name=row_num, table_ref=tab1}]}
```

**`WITH` / `context_list`**

```sql
WITH left_cte AS (SELECT a, b FROM third),
     right_cte AS (SELECT a, b FROM fourth)
SELECT l.a AS da, r.a AS ca FROM left_cte l JOIN right_cte r ON …
```

```
context_list={left_cte=query0, right_cte=query1, l=query0, r=query1}
table_alias={l=query0, r=query1, left_cte=query0}
```

Walk-time keys (`inherited_visible_aliases`, `outer_context_list_backup`, etc.) are stripped before publish.

**Predicate subquery — `dependent_queries`**

```sql
SELECT * FROM tab1 WHERE subj_cd IN (SELECT fld FROM orange)
```

```
dependent_queries={in_list1={query=query0, type=filters}}
def_query0={… inner SELECT scope …}
filters=[{name=subj_cd, table_ref=tab1}]
```

**PIVOT / UNPIVOT — `derivation`**

```sql
SELECT sales_amount, feb_sales
FROM monthly_sales
UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales, mar_sales)) AS outer_up
```

```
derivation={
  source_columns={outer_up=[{name=jan_sales, table_ref=monthly_sales}, …]},
  derived_columns={outer_up={sales_amount=[[@…]], month_name=[[@…]]}}
}
interface={
  sales_amount=[{name=jan_sales, table_ref=outer_up}, {name=feb_sales, table_ref=outer_up}],
  feb_sales=[{name=feb_sales, table_ref=outer_up}]
}
```

**UPDATE with subquery source**

```sql
UPDATE employees e SET score = src.acct_sales_count
FROM (SELECT emp_id, acct_sales_count, ROW_NUMBER() OVER (…) AS rn FROM accounts) src
WHERE e.emp_id = src.emp_id
```

```
def_update1={
  assignments={score=[{name=acct_sales_count, table_ref=src}]},
  update_dictionary={score=[[@4,24:28='score',<392>,1:24]]},
  table_alias={e=employees, src=query0},
  def_query0={… source SELECT with window_partition_by, interface, etc. …}
}
```

**Set operation — `def_unionN`**

```sql
SELECT col1 FROM sch1.aaa AS aaa UNION SELECT col1 FROM sch2.bbb AS bbb
```

```
def_union2={
  def_query0={table_alias={aaa=sch1.aaa}, interface={col1=[{name=col1, table_ref=aaa}]}},
  def_query1={setop=UNION, table_alias={bbb=sch2.bbb}, interface={col1=[{name=col1, table_ref=bbb}]}},
  interface={col1=query_column}
}
```

**Multi-statement scripts**

```
symbolTable → { SCRIPT: { "1": def_query0_payload, "2": def_insert0_payload, … } }
```

Each `sql_statement` is walked in isolation; statement numbers are 1-based string keys.

---

## Buckets you should not expect in published scopes

These are walk-time only and are stripped before `def_*` publish:

- `unresolved_column` — pending refs (hoisted or resolved at finalize)
- `scalar_subquery_aliases`
- `inherited_visible_aliases`, `local_from_registered_aliases`
- `outer_context_list_backup`, `outer_def_entries_backup`, `mumble_outer_table_alias`
- `_tmp_*` set-operation and DML staging keys
- `modifier_operand_token_refs`, `join_using_operand_token_refs`, etc.

If your consumer sees these keys, it is likely reading a live in-progress frame rather than a finalized **`def_*`** payload.

---

## How SQL maps to the symbol table

The walker treats SQL as a **tree of nested scopes**. A `SELECT` with a `FROM` subquery, a `WITH` clause, or a `UNION` does not flatten into one list of columns — each grammatical unit gets its own scope map. Parent scopes hold **live references** (`query0`, `values1`) pointing at child **published payloads** (`def_query0`, `def_values1`).

At the top level of a single statement, the root symbol table typically contains one `def_queryN`, `def_insertN`, `def_updateN`, etc. That payload is a map containing dictionaries, clause archives, alias registries, and nested `def_*` children.

**Live keys vs published keys**

| Kind | Pattern | Role |
|------|---------|------|
| Live reference | `query0`, `values1`, `union2`, `insert0` | Ephemeral pointer during the walk; removed at publish |
| Published payload | `def_query0`, `def_values0`, `def_union2`, `def_insert0` | Immutable snapshot at scope finalize — **what consumers should read** |
| Cross-statement globals | `tableDictionaryMap`, `queryColumnDictionaryMap` | Outside the symbol tree; merge tokens across the statement or script |

**Common value shapes**

| Shape | Example | Used for |
|-------|---------|----------|
| Token string | `[@1,7:11='apple',<392>,1:7]` | Dictionary values |
| Column ref | `{name: "col", table_ref: "alias"}` | Lineage / clause buckets |
| Substitution ref | `{substitution: {name: "<var>", type: "column"}, table_ref: "tab1"}` | Templated refs |
| Lineage list | `[{name: "a", table_ref: "tab1"}]` | `interface` values |
| Simple string | `"UNION"`, `"query0"`, `"tab1"` | `setop`, alias targets, `dependent_queries.query` |

**Cross-statement globals (outside symbol tree):** `tableDictionaryMap`, `queryColumnDictionaryMap`.

---

## Nesting patterns

How common SQL constructs produce nested `def_*` trees. Scope keys are defined in the [master scope table](#master-reference-scopes-keys-and-row-sources).

### WITH / CTE

1. Each CTE body → `def_queryN`.
2. CTE names register in **`context_list`** (alias → `queryN`).
3. Main query body inherits **`context_list`** and may reference CTEs by alias.
4. Sibling `def_query*` from the WITH frame nest as children of the promoted main-body scope.
5. Nested `WITH` saves/restores outer **`context_list`**, **`table_alias`**, and `def_*` entries before absorbing inner CTEs.

### FROM / JOIN subquery

1. Enter subquery → `pushSymbolTableWithParentVisibleScope()` (inherits outer `context_list` and visible aliases).
2. Exit subquery → finalize as **`def_queryN`**; parent gets `table_alias={alias=queryN}`.
3. Parent `interface` entries for `alias.col` resolve through the child's **`interface`**.

### Set operations

1. Each branch → separate **`def_queryN`** inside **`def_unionN`** or **`def_intersectN`**.
2. Non-anchor branches carry **`setop`**.
3. Composite **`interface`** aligns column names across branches (`query_column` sentinel until aligned).

### Predicate and scalar subqueries (scalar, EXISTS, IN, quantified)

1. Inner SELECT finalizes as `queryN` inside a predicate frame.
2. On exit: `dependent_queries.{predicand|exists|in_list|quantified}N → {query: queryN, type: <clause-bucket>}` where **`type` matches the parent archive bucket** (`interface`, `filters`, `grouped_by`, `ordered_by`, `window_partition_by`, `window_ordered_by`, `assignments`, or `derivation`).
3. Rename `queryN` → **`def_queryN`** nested in parent; merge via `popSymbolTablePutAll` (no extra `def_` wrapper around the predicate frame).
4. Parent clause bucket (named by `type`) may list outer-side column refs; it may also be **empty** when the subquery is an unnamed scalar. The **`dependent_queries` entry is still published** so consumers can open the child body.
5. PIVOT/UNPIVOT predicate-frame dependents are hoisted from the short-lived modifier scope together with nested **`def_queryN`** children.

### Scalar subqueries in SELECT list

- Tracked during walk via `scalar_subquery_aliases` (stripped at publish).
- Published on parent as **`dependent_queries.predicandN`** with `type=interface`.
- Parent **`interface`** maps the output alias to subquery result lineage.
- Inner body is nested **`def_queryN`**.

### Scalar subqueries in window specs, UPDATE SET, and PIVOT tails

- **`OVER (PARTITION BY (SELECT …))`** → `dependent_queries.predicandN` with `type=window_partition_by`; archive list may be `window_partition_by=[]`.
- **`OVER (ORDER BY (SELECT …))`** → `type=window_ordered_by`; archive list may be `window_ordered_by=[]`.
- **`UPDATE … SET col = (SELECT …)`** → `type=assignments` on **`def_updateN`**; RHS lineage also appears under **`assignments`** when column refs resolve.
- **PIVOT/UNPIVOT value positions** (e.g. `DEFAULT ON NULL ((SELECT …))`) → `type=derivation` on the enclosing **`def_queryN`**.

### DML with subqueries

- **INSERT … SELECT:** **`def_insertN`** contains nested **`def_queryN`** / **`def_valuesN`**; target mapping in **`interface`** / **`target_table`**.
- **UPDATE … FROM (subquery):** **`def_updateN`** contains **`def_query0`** plus **`assignments`**.
- **DELETE:** **`def_deleteN`** with **`filters`** and target **`table_dictionary`**; WHERE subqueries follow the predicate pattern.

---

## Recursive column tracing (consumer algorithm)

**Direction: reference → sources.** Given a `{name, table_ref}` appearing in any role bucket, walk **down** to physical leaves.

This is the **explicit traceability procedure** every consumer should implement. Any `{name, table_ref}` appearing in **any role bucket** can be traced to one or more **leaf sources** by the same recursive walk. This alternation is the **basis of column-reference traceability** in the symbol table structure family.

### 1. Quick traversal recipe

Scope-entry orientation before the detailed steps in the subsections below:

1. Start at the statement root (`def_query0`, `def_insert0`, etc., or `SCRIPT["N"]` for scripts).
2. Read **`interface`** to learn what the scope exports and where each output column comes from.
3. Use **`table_dictionary`** for physical lineage; **`query_dictionary`** for output-name token positions.
4. For each clause role you care about, trace **both** the archive bucket **and** matching **`dependent_queries`** entries (see [§4 Clause-bucket tracing](#4-clause-bucket-tracing-column-refs--dependent-subqueries)). Do not stop at `filters=[…]` alone — scalar and predicate subqueries in that clause may only appear under `dependent_queries`.
5. Follow `table_alias` values of `queryN` form to nested **`def_queryN`** children.
6. Follow **`dependent_queries`** entries to predicate subquery bodies.
7. Check **`derivation`** when PIVOT/UNPIVOT is present.
8. Use **`context_list`** to resolve CTE alias references.

### 2. Column reference shape

```
{name: "<column_name>", table_ref: "<alias_or_source>"}
```

Substitution objects use `{substitution: {name: "<var>", type: "column"}, table_ref: "…"}` — treat `substitution.name` as the logical column identity for tracing purposes; resolution may stop at the substitution if no physical binding exists.

### 3. Valid starting buckets (any role)

Do not limit tracing to `interface`. A reference may originate in:

| Role bucket | Typical starting context |
|-------------|--------------------------|
| **`interface`** | Output-column dependency |
| **`filters`** | WHERE, HAVING, QUALIFY, JOIN ON |
| **`grouped_by`** | GROUP BY |
| **`ordered_by`** | ORDER BY (query-level) |
| **`window_partition_by`** | `OVER (PARTITION BY …)` |
| **`window_ordered_by`** | `OVER (… ORDER BY …)` |
| **`assignments`** | UPDATE SET (RHS lineage per LHS key) |
| **`derivation.source_columns`** | PIVOT/UNPIVOT operand |
| **`dependent_queries`** + nested child | Predicate/scalar subquery body (open child `def_queryN` first) |
| DML **`interface`** / **`query_dictionary`** | `INSERT` / `UPDATE` / `DELETE` **`RETURNING`** outputs |

### 4. Clause-bucket tracing: column refs + dependent subqueries

When your algorithm is collecting or tracing columns for a **specific SQL role** (filtering, grouping, ordering, windowing, UPDATE SET, PIVOT operands, SELECT output, etc.), treat each role as **two parallel sources** on the same `def_*` scope:

1. **Archive bucket** — the flat column-ref list or map already named for that role (`filters`, `grouped_by`, `ordered_by`, `window_partition_by`, `window_ordered_by`, `assignments`, `derivation.source_columns`, or `interface`).
2. **`dependent_queries`** — subquery bodies whose **`type`** field equals that same bucket name.

**Rule:** For every role bucket you read, **also** scan `dependent_queries` and process every entry where `entry.type == <that bucket name>`. The archive list may be empty while `dependent_queries` still has work to do (unnamed scalar subqueries).

| If you are tracing… | Read this archive bucket | Also scan `dependent_queries` where `type` is… |
|---------------------|--------------------------|-----------------------------------------------|
| Filtering columns | `filters` | `filters` |
| GROUP BY columns | `grouped_by` | `grouped_by` |
| ORDER BY columns (query-level) | `ordered_by` | `ordered_by` |
| Window partition columns | `window_partition_by` | `window_partition_by` |
| Window order columns | `window_ordered_by` | `window_ordered_by` |
| UPDATE SET RHS lineage | `assignments` (per-LHS map values) | `assignments` |
| PIVOT/UNPIVOT operand scalars | `derivation.source_columns` | `derivation` |
| SELECT output / expressions | `interface` | `interface` |

**Example — “find all filtering columns” on `def_query4`:**

1. Recursively trace every `{name, table_ref}` in `def_query4.filters`.
2. Iterate `def_query4.dependent_queries`. For each entry with `type == "filters"` (regardless of whether the key is `predicand1`, `exists2`, `in_list3`, or `quantified1`), open the nested subquery scope and trace columns inside it (see below).
3. Union the leaf sources from both passes.

**How to open a subquery scope from `dependent_queries` (three steps):**

```
entry   = scope.dependent_queries["predicand1"]   // or existsN, in_listN, quantifiedN
queryRef = entry.query                            // live ref, e.g. "query0"
child    = scope["def_" + queryRef]               // published sibling, e.g. scope.def_query0
```

In plain terms: **`dependent_queries` and `def_queryN` are siblings on the same parent scope.** The `query` pointer tells you which `def_queryN` key to open. Do not search the global tree — stay on the current `def_*` payload.

Once `child` (`def_query0`, etc.) is open, run the full tracing procedure inside it: start from `child.interface`, `child.filters`, and any other buckets as needed. Correlated outer columns referenced inside the subquery resolve **up** to the parent scope via inherited visibility — they are not copied into the parent's archive buckets.

**Entry kind hints for agents:**

| `dependent_queries` key prefix | SQL form | Typical `type` when tracing… |
|--------------------------------|----------|------------------------------|
| `predicandN` | `(SELECT …)` scalar | matches enclosing clause bucket |
| `existsN` | `EXISTS (SELECT …)` | usually `filters` |
| `in_listN` | `IN (SELECT …)` | usually `filters` |
| `quantifiedN` | `= ANY (SELECT …)` etc. | usually `filters` |

For **`interface`** tracing, also walk `dependent_queries` entries with `type=interface` (scalar subqueries in the SELECT list). For **`assignments`**, walk map values under `assignments` **and** `dependent_queries` with `type=assignments`.

### 5. The alternation pattern

At each hop, alternate lookups **within the current `def_*` scope** and then **descend one child scope**:

```
column reference  →  table_alias  →  child def_*  →  interface  →  column reference  →  …
```

| Step | Where | What to do |
|------|-------|------------|
| **A** | Any lineage bucket in scope **S** | Hold the current `{name, table_ref}` (or substitution object). |
| **B** | **`table_alias`** and **`context_list`** in the **same scope S** | Resolve `table_ref` to a backing source string (see resolution table below). If already a physical leaf, go to **Leaf termination**. |
| **C** | Nested **`def_*`** child of **S** | Open the published child scope (`def_queryN`, `def_valuesN`, `def_unionN`, `def_intersectN`, …). For `dependent_queries`, resolve `query` → `def_queryN` on the parent payload. |
| **D** | **`interface`** in the child scope | Find the **next hop(s)**: locate the interface entry whose dependency list contains `{name: <current.name>, …}` matching the column you are tracing, **or** whose output key equals `current.name` when the ref is already an interface output. Collect all `{name, table_ref}` dependencies listed for that match. |
| **E** | Child scope becomes new **S** | For each next hop, repeat from step **A**. Stop each branch at a leaf. |

**Critical rule:** Do **not** skip scopes or jump to grandchild `def_*` payloads. Each hop must pass through the **direct child's `interface`** (or `derivation.source_columns` for modifier buckets) — matching SQL visibility: parents see only what children export.

When step **B** resolves to a source that needs no child scope, stop at **Leaf termination**.

### 6. Resolving `table_ref` (step B)

| Resolved value | Next action |
|----------------|-------------|
| **Physical table name** (e.g. `tab1`, `sch.tbl`) | **Leaf** — confirm `name` under `table_dictionary[table]` |
| **`queryN`** (live) or **`def_queryN`** (published) | Open **`def_queryN`** → step **D** |
| **`valuesN`** / **`def_valuesN`** | Open **`def_valuesN`** → step **D** (often leaf after one hop) |
| **CTE name** in **`context_list`** | `context_list[cte]` → `queryN` → **`def_queryN`** → step **D** |
| **Modifier bucket** (`tuple_0`, `outer_up`, `unpvt`, …) | Read **`derivation.source_columns[bucket]`**; each entry is the next `{name, table_ref}` hop (may be physical or need further descent) |
| **`unionN`** / **`def_unionN`** | Open composite scope; if parent `interface` uses sentinel **`query_column`**, align column across branch interfaces; descend into matching **`def_query*`** branch → step **D** |
| **`intersectN`** / **`def_intersectN`** | Same as union composite handling |
| **`table_ref` is `null`** | Unqualified ref in single-table scope — search visible **`table_dictionary`** for unique table proof; if exactly one physical table is visible, treat as leaf candidate |

### 7. Predicate and scalar subqueries

The [§4 Clause-bucket tracing](#4-clause-bucket-tracing-column-refs--dependent-subqueries) rule above is the primary guide. This subsection restates the navigation steps when your starting point is explicitly a **`dependent_queries`** entry rather than an archive-bucket column ref.

When the starting point is a subquery body (via **`dependent_queries`**):

1. Read `dependent_queries.{predicand|exists|in_list|quantified}N` → `{query: queryN, type: <clause-bucket>}`.
2. Use **`type`** to know which archive bucket on the parent describes the outer SQL site (`filters`, `ordered_by`, `assignments`, `derivation`, etc.). That bucket may be **empty** — the `dependent_queries` entry is still authoritative.
3. Open **`def_queryN`** on the **same parent scope** (`scope["def_" + entry.query]`). The child is a sibling of `dependent_queries`, not nested inside it.
4. Begin step **A** inside that child: trace `child.interface`, `child.filters`, and other buckets as your task requires.
5. Correlated outer columns inside the subquery trace **up** via inherited outer `table_alias` / `context_list` — the parent does not flatten inner aliases into its own buckets.

### 8. Leaf termination

Stop a branch when the reference resolves to a **source leaf** — no further query `interface` rewrite applies.

| Leaf type | How to recognize | Where proof / tokens live |
|-----------|------------------|---------------------------|
| **Physical table column** | `table_alias` resolves to table name; column under that table in **`table_dictionary`** | `table_dictionary[<table>][<column>]` → token list |
| **`VALUES` row source** | `table_alias` → `valuesN`; child **`def_valuesN`** scope | Child **`interface`** / **`query_dictionary`**; no deeper query hop |
| **Table function column** | `table_alias` → generated alias (`flatten0`, …); column in **`table_dictionary`** under that alias | `table_dictionary[<function_alias>][<column>]` |
| **PIVOT/UNPIVOT physical operand** | Hop ends in **`derivation.source_columns`** with `table_ref` = physical table or resolvable alias | `derivation.source_columns[<bucket>]` + **`table_dictionary`** |
| **DML `RETURNING` output** | Column is declared returning output on **`def_insertN`**, **`def_updateN`**, or **`def_deleteN`** | That scope's **`interface`** / **`query_dictionary`** |
| **No `queryN` indirection** | `table_ref` is physical table, `VALUES`/function alias, with no intervening nested `def_query*` | Current scope **`table_dictionary`** |

### 9. Fan-out (multiple leaves)

One starting reference may produce **multiple terminal leaves** when step **D** returns more than one `{name, table_ref}` (combined expression, window output merging `OVER` dependencies, or multi-source `interface` entry). Trace **each** dependency as a separate branch; aggregate all leaves for the full lineage set.

### 10. Worked example

```sql
SELECT * FROM (SELECT a FROM tab1) AS sub WHERE sub.a = 1
```

```
def_query1={
  filters=[{name=a, table_ref=sub}],
  table_alias={sub=query0},
  def_query0={
    interface={a=[{name=a, table_ref=tab1}]},
    table_alias={tab1},
    table_dictionary={tab1={a=[[@…]]}}
  }
}
```

Trace `filters[0]` = `{name=a, table_ref=sub}`:

1. Scope **def_query1**, step **A** — start `{name=a, table_ref=sub}` in `filters`.
2. Step **B** — `table_alias[sub]` → `query0` → open **def_query0**.
3. Step **D** — in **def_query0**, `interface[a]` = `[{name=a, table_ref=tab1}]` → next hop `{name=a, table_ref=tab1}`.
4. Step **B** — `table_alias[tab1]` → physical `tab1` → **leaf** at `table_dictionary[tab1][a]`.

### 11. Implementer checklist (consumer obligations)

1. Read **published `def_*`** payloads only — never ephemeral `queryN` keys on finalized trees.
2. At every scope, consult **`table_alias`** and **`context_list`** before assuming `table_ref` is physical.
3. For modifier-derived names, consult **`derivation.source_columns`** (and **`pivot_derived_source_bindings`** for PIVOT aggregates) before treating a name as unresolvable.
4. Never skip a scope — grandchild columns are invisible unless re-exported through each intervening child's **`interface`**.
5. When tracing a clause role, always pair the archive bucket with a **`dependent_queries` scan** where `type` matches that bucket (see [§4 Clause-bucket tracing](#4-clause-bucket-tracing-column-refs--dependent-subqueries)).
6. Record each hop `(scope_id, bucket, {name, table_ref})` when building an audit trail; use **`query_dictionary`** at each scope for SQL text-position evidence of output-column names.

### 12. Procedure summary (pseudocode)

```
function trace_clause_role(scope, role_bucket_name):
  leaves = []
  // Pass 1: archive bucket column refs
  for each ref in scope[role_bucket_name]:          // list or map values
    leaves += trace(ref, scope, role_bucket_name)
  // Pass 2: dependent subqueries in the same role
  for each (kind_key, entry) in scope.dependent_queries:
    if entry.type == role_bucket_name:
      child = scope["def_" + entry.query]         // sibling def_queryN
      leaves += trace_subquery_body(child)
  return leaves

function trace_subquery_body(child_scope):
  // Start inside child; trace interface, filters, etc. as needed
  ...

function trace(ref, scope, bucket_name):
  record hop(scope, bucket_name, ref)
  if ref is substitution and unbound: return [substitution leaf]

  source = resolve(ref.table_ref, scope.table_alias, scope.context_list)
  if source is physical table:
    return [leaf: scope.table_dictionary[source][ref.name]]
  if source is valuesN:
    child = scope.def_valuesN
    return trace_from_interface(child, ref.name)  // may terminate in child
  if source is queryN / def_queryN:
    child = scope.def_queryN
    next_refs = find_interface_deps(child.interface, ref.name)
    leaves = []
    for each next in next_refs:
      leaves += trace(next, child, "interface")
    return leaves
  if source is modifier bucket:
    next_refs = scope.derivation.source_columns[source]
  if source is unionN / intersectN:
    child = scope.def_unionN or def_intersectN
    next_refs = resolve_setop_column(child, ref.name)
  for each next in next_refs:
    leaves += trace(next, child, ...)
  return leaves
```

---

## Source column impact tracing (consumer algorithm)

**Direction: source → impacts (leaf-first, then upward).** Given a physical table column from the **global table dictionary**, start at the **leaf introduction sites** where that column enters the symbol tree, collect every **participation action** it takes in each scope, then walk **outward** through **`interface`** re-exports and **`table_alias`** / **`context_list`** bindings until you reach every consuming scope — including statement outputs, filters, grouping keys, window clauses, DML assignments, and correlated subqueries.

This procedure answers: *“How was column `a` from table `tab1` used in this query — what kinds of actions did it participate in, and how did its value propagate through aliasing and nested queries?”* It is the **inverse** of [Recursive column tracing](#recursive-column-tracing-consumer-algorithm): recursive tracing holds a `{name, table_ref}` and walks **down** to physical leaves; impact tracing holds a physical leaf and walks **up** through derived references and scope boundaries.

A single physical column may participate in **many actions at once** in the same scope (for example, exported in `interface`, used in `filters`, and listed in `grouped_by`). Record each action separately — do not collapse them into one “usage” label. See [§2 Participation roles](#2-participation-roles-what-actions-to-record).

### 1. Quick recipe

1. **Anchor** on `{physical_table, column}` using the global **table dictionary** token proof (e.g. `table_dictionary["tab1"]["a"]`).
2. **Find leaf introduction sites** — every `def_*` scope whose local **`table_dictionary[anchor.table][anchor.column]`** carries tokens (the innermost scopes that read the physical column directly). See [§5 Leaf introduction sites](#5-leaf-introduction-sites).
3. At each introduction scope, **scan all role buckets** for references matching the anchor (see [§6 Matching references](#6-matching-bucket-references-to-the-anchor)). Each match is an **impact site** with a **participation role** (see [§2](#2-participation-roles-what-actions-to-record)).
4. When the anchor appears in **`interface.<out_col>`** dependency lists, record the action *“feeds output `<out_col>`”* and enqueue **`<out_col>` as a derived reference** at this scope.
5. **Walk upward** through the [reverse alternation pattern](#4-reverse-alternation-pattern-leaf--derived-reference--parent-scope): for each derived reference `{name=<out_col>, table_ref=<alias>}`, open the **parent** scope where `table_alias[<alias>]` points at the child scope; scan parent buckets for uses of that derived name; repeat until the statement root.
6. **Walk downward** into child `def_queryN` scopes visible through `table_alias` / `context_list` when the parent consumes only the child's output — the same physical column may be read and acted on at multiple nesting depths.
7. On every scope visited, scan **`dependent_queries`**: open nested `def_queryN` children and collect **correlated** inner uses of the anchor (outer physical table visible inside the subquery body).
8. Emit a **fan-out report** rooted at the physical origin, with sibling branches per action and per propagation path (see [§10 Report shape](#10-report-shape-fan-out-from-origin)).

### 2. Participation roles (what actions to record)

When a bucket reference matches the anchor, attach a **participation role** — a plain-language label for the SQL action that column performed at that site. Use the bucket as the primary classifier; refine with context (LHS key on `assignments`, output column name on `interface`, `dependent_queries.type`, modifier bucket on `derivation`).

| Bucket | Participation role (action) | What to record |
|--------|----------------------------|----------------|
| **`table_dictionary`** | **Physical read** | Direct touch — column token collected while walking this scope (proof of introduction) |
| **`interface`** (as dependency) | **Input to output expression** | Anchor feeds output column `<out_col>` — record `{action: "feeds_output", output: "<out_col>"}` |
| **`interface`** (output key equals anchor column, pass-through) | **Pass-through export** | Column exported under same or aliased name without intermediate rewrite |
| **`filters`** | **Row restriction** | Predicate operand — WHERE, HAVING, QUALIFY, JOIN ON, or EXISTS/IN correlation site |
| **`grouped_by`** | **Grouping key** | Column participates in GROUP BY / aggregation partition |
| **`ordered_by`** | **Sort key** | Column participates in query-level ORDER BY |
| **`window_partition_by`** | **Window partition** | Column partitions an analytic window (`OVER (PARTITION BY …)`) |
| **`window_ordered_by`** | **Window order** | Column orders rows within a window frame |
| **`assignments`** | **DML target update** | Anchor appears in RHS lineage for `SET <lhs> = …` — record `{action: "update_rhs", target_column: "<lhs>"}` |
| **`derivation.source_columns`** | **Relational modifier operand** | PIVOT/UNPIVOT operand — record modifier bucket (`tuple_0`, `unpvt`, …) |
| **`derivation.derived_columns`** | **Modifier-derived output** | Anchor operand fans out to derived column name — record derived name |
| **`dependent_queries`** + inner bucket | **Subquery participation** | Anchor used inside subquery body — record `{subquery: "<kind>N", outer_type: "<type>", inner_bucket: "…"}` |
| Substitution object | **Substitution placeholder** | Bound or unbound `substitution.name` matching anchor |

**Derived references:** Whenever `interface.<out_col>` lists a dependency matching the anchor, treat `<out_col>` as a **new name** for the same logical value at that scope. Parent scopes never see the physical `{name=a, table_ref=tab1}` directly — they see `{name=a, table_ref=sub}` where `sub` aliases the child. Upward propagation always follows these **renamed hops** through `table_alias` / `context_list`, not raw physical refs.

**Multiple roles:** The same match may satisfy more than one row in the table above when a column is archived in several buckets (uncommon but valid after modifier fan-out). Emit one impact record per bucket.

### 3. Anchor: global table dictionary entry

The anchor is always a **physical source column**, not a query output name:

```
anchor = { physical_table: "tab1", column: "a" }
proof  = global_table_dictionary["tab1"]["a"]   // List<token> — SQL text positions
```

- Use the **global** table dictionary (walker artifact alongside the symbol table), not a single scope's `table_dictionary`, to select the column under study.
- Qualified table names in the dictionary key (`sch.tab1`) must match the anchor string exactly.
- If the column appears under multiple physical tables in one statement, run **one trace per anchor** — do not merge unrelated homonyms.

### 4. Reverse alternation pattern (leaf → derived reference → parent scope)

[Recursive column tracing §5](#5-the-alternation-pattern) walks **down**:

```
column reference  →  table_alias  →  child def_*  →  interface  →  column reference  →  …  →  physical leaf
```

Impact tracing walks **up** from the leaf:

```
physical leaf  →  interface (export)  →  derived reference in parent  →  parent role buckets  →  parent interface  →  …  →  statement root
```

| Step | Where | What to do |
|------|-------|------------|
| **A** | **Leaf scope** `S` | Confirm `S.table_dictionary[anchor.table][anchor.column]` has tokens — this scope **introduces** the physical read. |
| **B** | **All role buckets** in `S` | Scan every bucket ([§7](#7-impact-buckets-to-scan-at-each-scope)); record each matching ref with its [participation role](#2-participation-roles-what-actions-to-record). |
| **C** | **`interface`** in `S` | For each `interface.<out_col>` whose dependency list matches the anchor, record *feeds output* and enqueue derived ref `{name=<out_col>, scope=S}`. |
| **D** | **Parent scope** `P` | Find aliases where `P.table_alias[<alias>]` → `queryN` / `def_queryN` identifying child `S`. Search **all buckets in `P`** for refs `{name=<out_col>, table_ref=<alias>}` (or `context_list` CTE names). Each hit is a new impact site. |
| **E** | **`P` becomes current scope** | Repeat **B–D** for each derived reference until no parent scope remains. Also descend into child scopes ([§1 step 6](#1-quick-recipe)) when the anchor is consumed only through nested reads. |

**Block references (`dependent_queries`):** Subquery bodies are **siblings** of `def_queryN` on the parent payload, not nested inside archive buckets. When scanning upward, a parent may list `dependent_queries.predicand1` with `type=filters` while `filters` itself is empty — open `def_query0` via `scope["def_" + entry.query]` and scan the **child** buckets for correlated anchor matches. Record the action on both the inner bucket and the parent's `dependent_queries.<kind>N` index.

**Set operations:** When child branch `def_queryN` exports an affected column, the composite parent's `interface` may use sentinel **`query_column`** until branches align — propagate impact to the composite output name at finalize.

**PIVOT/UNPIVOT:** Operand impact on `derivation.source_columns` fans out to **derived** names in `derivation.derived_columns` and `interface` entries whose `table_ref` is a modifier bucket. Record the operand action and each derived output that lists the bucket as `table_ref`.

### 5. Leaf introduction sites

Before walking upward, locate **where the physical column enters** the symbol tree:

1. Enumerate every `def_*` scope in the statement (depth-first from root is fine).
2. At each scope `S`, if `S.table_dictionary[anchor.physical_table][anchor.column]` is non-empty, `S` is a **leaf introduction site** — the walk touched that physical column while resolving references inside `S`.
3. Prefer **deepest** introduction sites first when building the report (inner subqueries before outers), but visit **all** sites — the same physical column may be read independently in sibling branches (`UNION` arms) or in both a child and a correlated subquery.

Token positions in the global table dictionary are the authoritative **SQL location list** for “where was this column introduced in the text”; per-scope `table_dictionary` entries tie those tokens to a specific `def_*` scope.

### 6. Matching bucket references to the anchor

A bucket entry `{name, table_ref}` (or substitution object) **matches** the anchor when:

| Condition | Example |
|-----------|---------|
| `name == anchor.column` and `table_ref == anchor.physical_table` | `{name=a, table_ref=tab1}` |
| `name == anchor.column` and `table_ref` is a **local alias** mapping to the physical table | `table_alias={t=tab1}`, ref `{name=a, table_ref=t}` |
| `name == anchor.column` and `table_ref` is a **chain alias** resolving to the physical table | `table_alias={s=query0}`, child exports `a`, parent ref `{name=a, table_ref=s}` after one interface hop |
| Substitution with `substitution.name == anchor.column` and resolvable `table_ref` | Treat like a column ref after substitution binding |

**Matching derived references (upward walk):** After step **C**, a ref `{name=<out_col>, table_ref=<alias>}` matches the **propagated anchor** when `table_alias[<alias>]` resolves to the child scope that exported `<out_col>` from the anchor. Do not require `table_ref == anchor.physical_table` on parent hops — only on the leaf introduction sites.

**Resolution helper** (same `resolve_table_ref` logic as recursive tracing, used in the forward direction):

```
function resolves_to_physical(ref, scope):
  if ref.name != anchor.column: return false
  source = resolve_table_ref(ref.table_ref, scope.table_alias, scope.context_list)
  if source == anchor.physical_table: return true
  if source is def_queryN / queryN:
    return child_exports_column(source, anchor.column)   // one interface hop
  return false

function matches_propagated(ref, derived_name, child_alias, parent_scope):
  return ref.name == derived_name and ref.table_ref == child_alias
    and parent_scope.table_alias[child_alias] maps to child that exported derived_name
```

Do **not** match on column name alone at leaf sites — `table_ref` must resolve to the anchored physical table (or to a child scope that re-exports that column).

### 7. Impact buckets to scan at each scope

At every `def_*` scope visited (introduction site, parent on upward walk, or child on downward walk), scan **all** of the following. A single scope may yield **multiple** impact sites across buckets.

| Bucket | Participation role | Also scan `dependent_queries` where `type` equals… |
|--------|-------------------|-----------------------------------------------------|
| **`filters`** | Row restriction | `filters` |
| **`grouped_by`** | Grouping key | `grouped_by` |
| **`ordered_by`** | Sort key | `ordered_by` |
| **`window_partition_by`** | Window partition | `window_partition_by` |
| **`window_ordered_by`** | Window order | `window_ordered_by` |
| **`assignments`** | DML update RHS (per-LHS map values) | `assignments` |
| **`derivation.source_columns`** | Modifier operand | `derivation` |
| **`interface`** | Output expression input / pass-through export | `interface` |
| **`table_dictionary`** | Physical read | — |
| **`dependent_queries`** | Subquery index (open child — see [§9](#9-dependent-subqueries-and-correlated-inner-use)) | — |

Apply the [dual-source rule](#4-clause-bucket-tracing-column-refs--dependent-subqueries) from recursive tracing: for each role bucket, also process `dependent_queries` entries whose `type` matches that bucket name.

### 8. Propagation through interface, aliases, and derived references

The upward trace **crosses scope boundaries** only through published exports and alias maps:

1. **Upward (child → parent):** When `interface[OUT]` depends on the anchor at child scope `S`, record *feeds output `OUT`*, then in parent `P` find every ref `{name=OUT, table_ref=<alias>}` where `table_alias[<alias>]` → `queryN` identifying `S`. Each parent bucket hit is a new impact with the appropriate [participation role](#2-participation-roles-what-actions-to-record). Repeat recursively to the statement root.
2. **Downward (parent → child):** If `P.table_alias[alias]` → `def_queryN`, open the child and run the full bucket scan. The anchor may be read and acted on inside the child even when the parent only references output names.
3. **CTE (`context_list`):** Upward and downward hops use `context_list[cte_name]` → `queryN` the same way as `table_alias` for `FROM` subqueries.
4. **Fan-out:** One physical column may produce **many** parallel branches (multiple outputs, multiple clause roles, multiple subqueries, multiple `UNION` arms). Preserve each path as a sibling branch — do not merge.

### 9. Dependent subqueries and correlated inner use

Predicate-frame subqueries are indexed on the parent but executed in a **child** scope:

1. On parent scope `S`, iterate `S.dependent_queries`.
2. Open `child = S["def_" + entry.query]` (sibling of `dependent_queries`, not nested inside it).
3. Scan `child` buckets for refs matching the anchor **via outer correlation** (`table_ref` resolves to a table visible in `S`, not declared in `child`'s own `FROM`).
4. Record: `{action: "subquery_participation", parent=S, index=dependent_queries.<kind>N, entry_type=<type>, child_scope=def_queryM, inner_bucket=…}`.

The parent's archive bucket for `entry.type` may be **empty** — the `dependent_queries` entry and child body are still authoritative.

### 10. Report shape: fan-out from origin

Present results as a **tree** (or nested list) rooted at the physical source. Each node should carry:

- **Scope** — `def_queryN` path from statement root (e.g. `def_query2 → def_query0`).
- **Bucket** — role bucket or `dependent_queries.<kind>N`.
- **Reference** — the matching `{name, table_ref}` (or substitution).
- **Participation role** — action label from [§2](#2-participation-roles-what-actions-to-record) (filter, feeds output, grouping, window partition, …).
- **Derived reference** — when applicable, the output name propagated to the parent (`OUT` via alias `sub`).
- **Tokens** — optional `query_dictionary` / `table_dictionary` positions for audit.

**Example report skeleton:**

```
tab1.a  (origin — global table dictionary)
│
├─ def_query0.table_dictionary
│     └─ [physical read] direct touch [@1,8:8='a']
│
├─ def_query0.interface.a
│     └─ [pass-through export] exported as output column `a`
│         derived_ref: {name=a, scope=def_query0} → propagates as sub.a
│
├─ def_query1.filters
│     └─ [row restriction] {name=a, table_ref=sub}  — WHERE restricts on propagated alias
│
├─ def_query1.interface.*
│     └─ [feeds output] statement output depends on `sub`, which re-exports tab1.a
│
└─ def_query1.dependent_queries.predicand1 → def_query2
      └─ def_query2.filters
            └─ [row restriction] {name=a, table_ref=tab1}  — correlated scalar subquery in WHERE
```

Agents may serialize this as JSON, indented text, or a graph — the **topology** (origin at root, participation actions as labeled branches, derived-reference propagation as nested paths) matters more than the encoding.

### 11. Worked example

```sql
SELECT * FROM (SELECT a FROM tab1) AS sub WHERE sub.a = 1
```

**Anchor:** `{physical_table: tab1, column: a}`

**Impact trace (leaf-first, then upward):**

| Step | Scope | Bucket | Match | Participation role |
|------|-------|--------|-------|-------------------|
| 1 | `def_query0` | `table_dictionary` | `tab1.a` tokens | Physical read |
| 2 | `def_query0` | `interface.a` | `{name=a, table_ref=tab1}` | Pass-through export → derived ref `a` |
| 3 | `def_query1` | `table_alias` | `sub=query0` | (binding — enables upward match) |
| 4 | `def_query1` | `filters` | `{name=a, table_ref=sub}` | Row restriction |
| 5 | `def_query1` | `interface.*` | deps include `{name=a, table_ref=sub}` | Feeds output `*` |

Fan-out tree:

```
tab1.a
├── def_query0 — introduces and exports
│     ├── table_dictionary[tab1][a]     [physical read]
│     └── interface.a                 [pass-through export] → derived ref `a`
└── def_query1 — consumes via alias `sub`
      ├── filters                     [row restriction] ← {name=a, table_ref=sub}
      └── interface.*                 [feeds output]
```

### 12. Implementer checklist

1. Anchor on **one** physical `{table, column}` from the global table dictionary — not a query output name.
2. Start at **leaf introduction sites** (`table_dictionary` tokens per scope), then walk **upward** through `interface` / `table_alias` derived references.
3. Match on **name + resolved table_ref** at leaf sites; match on **propagated derived names** on parent hops.
4. Scan **all** role buckets plus **`dependent_queries`** children (correlated inner use); apply the dual-source rule per bucket.
5. Record a **participation role** for every impact — not just the bucket name.
6. When `interface.<out>` depends on the anchor, **propagate `<out>` upward** and continue scanning parent buckets.
7. Preserve **fan-out** — multiple outputs and clause roles produce sibling branches, not a single merged list.
8. Pair each impact with optional **token** proof from scope dictionaries.
9. For audit trails, run [Recursive column tracing](#recursive-column-tracing-consumer-algorithm) on any ambiguous ref to confirm it resolves back to the same anchor.

### 13. Procedure summary (pseudocode)

```
function trace_source_impacts(anchor, statement_root_scope):
  report = TreeNode(anchor, role="origin")
  intro_sites = find_leaf_introduction_sites(statement_root_scope, anchor)
  for each site in intro_sites sorted deepest_first:
    collect_impacts_at_scope(site, anchor, report)
    for each derived_ref exported from site.interface matching anchor:
      propagate_upward(site, derived_ref, anchor, report)
  // Also descend from root for scopes that only consume child outputs
  visit_scope_downward(statement_root_scope, anchor, report)
  return report

function collect_impacts_at_scope(scope, anchor, parent_node):
  for each (bucket_name, entries) in all_role_buckets(scope):
    for each ref in entries:
      if matches_anchor(ref, anchor, scope):
        parent_node.add_child(ImpactSite(scope, bucket_name, ref,
          participation_role(bucket_name, ref, scope)))
    for each entry in scope.dependent_queries where entry.type == bucket_name:
      child = scope["def_" + entry.query]
      collect_impacts_at_scope(child, anchor, parent_node.child(entry.key))

function propagate_upward(child_scope, derived_name, anchor, report_node):
  parent = find_parent_scope(child_scope)
  alias = find_alias_in_parent(parent, child_scope)
  for each (bucket_name, ref) in all_bucket_refs(parent):
    if matches_propagated(ref, derived_name, alias, parent):
      site = report_node.add_child(ImpactSite(parent, bucket_name, ref,
        participation_role(bucket_name, ref, parent)))
      if bucket_name == "interface" and ref is output dependency:
        propagate_upward(parent, <next_out_col>, anchor, site)
  if parent: propagate_upward(parent, derived_name, anchor, report_node)

function participation_role(bucket, ref, scope):
  return ROLE_TABLE[bucket]   // see §2 — refine for assignments LHS, interface output name, etc.
```

---

## Consumer implementation notes

1. **Numeric suffix (`N`)** — Counters are per-walk (`query0` → `def_query0`). Not globally meaningful across statements.
2. **Live vs published** — Consume **`def_*`** after finalize; never ephemeral `queryN` on published trees.
3. **`EXCEPT` uses `def_unionN`** — there is no `def_exceptN`; non-anchor branches carry `setop=EXCEPT`.
4. **Set-op anchor** — First branch has no `setop`; later branches carry `UNION`, `EXCEPT`, or `INTERSECTION`.
5. **`query_column` sentinel** — Set-op parent `interface` may use this until branches align at finalize.
6. **Table functions** — Generated aliases (`flatten0`, `generator0`) live inside parent **`def_queryN`**, not as separate `def_*` types.
7. **Nested trees** — Outer `def_queryN` routinely contains inner `def_queryM`, `def_unionK`, `def_valuesJ`, etc.
8. **Scripts** — Root may be `SCRIPT: {"1": <def_*>, "2": <def_*>, …}` rather than a bare `def_query0`.
9. **Catalog boundary** — Do not expect physical table column validation; Diagnostics check syntactic consistency and visibility within the symbol table, not warehouse catalog truth.

---

## Golden tests and source code

| Topic | Test class |
|-------|------------|
| `def_valuesN`, `def_insertN` + VALUES | `SqlParseEventWalkerWithAccessObjectTest` |
| `def_unionN` / `def_intersectN`, CTE `context_list` | `SqlEventWalkerJoinsAndTableResolutionTests` |
| DDL `def_createN` / `def_dropN` / `def_alterN` / `def_truncateN` | `SqlEventWalkerDdlTests`, `SqlEventWalkerScriptsAndDDLTests` |
| `def_updateN` + nested source | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` |
| Table functions | `SqlEventWalkerTableFunctionTests` |
| PIVOT/UNPIVOT `derivation` | `SqlEventWalkerPivotUnpivotTests` |
| Predicate `dependent_queries` | `SqlEventWalkerCoreSelectFromAliasingTests` |

| Area | Source file |
|------|-------------|
| Scope finalize / publish | `parse/src/main/java/sql/symboltree/SqlParseSymbolTreeHelper.java` |
| Stack push/pop | `parse/src/main/java/astwalkers/SqlASTWalkerHelper.java` |
| Bucket key constants | `parse/src/main/java/mumble/MumbleConstants.java` |
