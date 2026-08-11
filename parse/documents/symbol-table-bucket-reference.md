# Symbol Table Bucket Reference

**Audience and version**

- Consumer guide for services that read and interpret PSS symbol tables.
- Applies to symbol tables produced by the PSS SQL Parser JAR **version 5.1.3 or greater**.

**What it is**

- A symbol table is a **nested map** produced while walking parsed SQL.
- It attempts to represent the logical elements of a portion of a SQL statement.
- The model is analogous to symbol tables in modern programming languages: maintaining **visibility**, **coherence**, and **uniqueness** of variable references within the run stack of a method or function.

**What it tracks**

- Where variables (columns) are **created**.
- Where they **can and cannot be seen**.
- How **accessibility propagates** up and down the execution stack of a running SQL statement.

**What it deliberately omits**

- The symbol table does **not** retain the full knowledge and logic applied to the data held in these variable objects.
- It **does** provide **traceability** from sources, through logical layers, to the outputs of any SQL statement.

**The core tracing problem**

- One of the hardest things to follow in SQL is how a value in one source variable moves through layers of logic to become the values under other variable names and organizations that emerge from the statement's output interface.

**Questions a symbol table can answer**

A symbol table of this kind can help answer questions such as:

- **Why do I need this source column?**
- **Where is it used?**
- **What role or roles does it play in the computation?**

A single source column may support **multiple roles** at once, or appear for **only one purpose**. Common computation roles include:

1. **Input to further logic** — the column feeds expressions, functions, or derived values that produce still other variables. Traced primarily through **`interface`** lineage entries (each output maps back to proven source columns) and, for PIVOT/UNPIVOT, through **`derivation`**.
2. **Pass-through** — the value is carried unchanged (or under a new alias) through a series of temporary variable names across symbol-table layers until it appears in the **output `interface`** of the main query. Follow **`interface`** and nested **`def_*`** scopes from inner sources outward.
3. **Filtering** — the column participates in logical conditions that restrict which rows survive: `WHERE`, `HAVING`, `QUALIFY`, `JOIN ON`, predicate subqueries, and similar. Archived in **`filters`**; predicate subqueries additionally indexed under **`dependent_queries`**.
4. **Grouping, ordering, and windowing** — the column is required for set-aggregation, sort order, or window partitioning/analysis. Archived in **`grouped_by`**, **`ordered_by`**, **`window_partition_by`**, and **`window_ordered_by`**.
5. **Set and relational operations** — the column aligns branches in **`UNION`** / **`INTERSECT`** / **`EXCEPT`**, participates in join keys, or supports DML target mapping via **`assignments`** and related DML buckets.
6. **Other purposes** — including substitution placeholders, modifier operands, EXISTS/IN correlation, and clause references that never reach the output interface but still constrain or shape the result.

When interpreting a source column, walk **all relevant buckets** in the scopes where it appears — not only **`interface`** — because a column may be essential to the statement even when it never surfaces in the final output.

**Scopes, buckets, and nesting**

- Each **scope** (query, CTE body, set-op branch, DML statement, predicate subquery, etc.) accumulates **buckets** on a stack.
- At scope exit, the finalized payload is published under a `def_*` key.
- Inside the outermost `def_*` bucket, other `def_*` symbol tables often appear, each with its own buckets of variables.

**The `interface` bucket as the public boundary**

- Only column variables defined in a symbol table's **`interface`** bucket are visible **outside** that symbol table.
- The `interface` bucket is the window into an otherwise temporary namespace.

**SQL visibility conventions**

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

Predicate subquery bodies are published as nested **`def_queryN`** scopes indexed on the parent via **`dependent_queries`**. The parent does not merge the child's internal dictionaries; correlated outer columns inside the subquery resolve against inherited outer visibility, not by flattening the child's private namespace into the parent.

**SQL as a functional model**

- PSS treats SQL statements as a **functional language** over a multi-dimensional set of domain objects.
- The output is a sequence of **rows** (tuples) of related **columns** of information.
- PSS presumes the author knows what these relationships logically symbolize and makes **no statement** on the logical correctness of the semantics produced.

**Companion artifacts and validation**

- Parser validation processes use symbol tables to provide **syntactic maps** and **accuracy checks** for subtle mistakes an author may make.
- These validations appear as **Diagnostic** messages of varying severity, alongside:
  - An **AST** representation of the original statement
  - **Table** and **query** dictionaries
  - A collection of **substitution variables** (author-permitted variable logic for customization scenarios)
  - A formal array of **interface columns** produced by the SQL statement — i.e., the SQL "function" output

**Related implementation docs:** [table-and-query-dictionary-design.md](table-and-query-dictionary-design.md), [relational-modifier-resolution-policy.md](relational-modifier-resolution-policy.md).

---

## How SQL maps to the symbol table

The walker treats SQL as a tree of nested scopes. A `SELECT` with a `FROM` subquery, a `WITH` clause, or a `UNION` does not flatten into one flat list of columns — each grammatical unit gets its own scope map, and parent scopes hold **live references** (`query0`, `values1`) that point at child **published payloads** (`def_query0`, `def_values1`).

At the top level of a single statement, the root symbol table typically contains one `def_queryN`, `def_insertN`, `def_updateN`, etc. That payload is itself a map containing dictionaries, clause archives, alias registries, and further nested `def_*` children.

**Live keys vs published keys**

| Kind | Pattern | Role |
|------|---------|------|
| Live reference | `query0`, `values1`, `union2`, `insert0` | Ephemeral pointer used during the walk; removed when the scope is published |
| Published payload | `def_query0`, `def_values0`, `def_union2`, `def_insert0` | Immutable snapshot at scope finalize — this is what consumers should read |
| Cross-statement globals | `tableDictionaryMap`, `queryColumnDictionaryMap` | Outside the symbol tree; merge tokens across the whole statement or script |

**Common value shapes**

| Shape | Example | Used for |
|-------|---------|----------|
| Token string | `[@1,7:11='apple',<392>,1:7]` | Dictionary values — line/char positions in source text |
| Column ref | `{name: "col", table_ref: "alias"}` | Clause archives, `interface` lineage entries |
| Substitution ref | `{substitution: {name: "<var>", type: "column"}, table_ref: "tab1"}` | Unresolved or templated column references |
| Lineage list | `[{name: "a", table_ref: "tab1"}]` | `interface` values — proven sources for an output column |
| Simple string | `"UNION"`, `"query0"`, `"tab1"` | `setop`, alias targets, `dependent_queries` query pointers |

---

## Scope shells (`def_*` keys)

These are the top-level entries in the symbol table (or nested children inside a parent scope). Each value is a full scope map.

> **Full catalog:** See [Row sources and `def_*` scope catalog](#row-sources-and-def_-scope-catalog) for the complete list of `def_*` key patterns, row-source taxonomy, and implementation notes.

| Key pattern | Appears when | Contains |
|-------------|--------------|----------|
| `def_queryN` | `SELECT`, subquery, CTE body, CTAS / INSERT / UPDATE sources | Full query scope: dictionaries, interface, clauses, nested children |
| `def_valuesN` | `VALUES` row source (`FROM` or `INSERT`) | Same shape as a query scope, keyed on row output names |
| `def_unionN` | `UNION` or **`EXCEPT`** composite | Nested `def_query*` children + merged composite `interface` |
| `def_intersectN` | `INTERSECT` composite | Same nesting pattern; branches may carry `setop=INTERSECTION` |
| `def_insertN` | `INSERT` statement | Target metadata + nested source `def_queryN` / `def_valuesN` |
| `def_updateN` | `UPDATE` statement | `assignments`, nested source query, `filters`, target dictionaries |
| `def_deleteN` | `DELETE` statement | Target `table_dictionary`, `filters` |
| `def_createN` | `CREATE` (table, view, etc.) | Often minimal; CTAS nests `def_queryN` |
| `def_alterN` | `ALTER` | Typically minimal payload |
| `def_dropN` | `DROP` | Typically minimal payload |
| `def_truncateN` | `TRUNCATE` | Typically minimal payload |

**Example — FROM subquery**

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
    table_dictionary={tab1={a=[[@...]]}}
  }
}
```

---

## Bucket dictionary

### Core lineage and dictionaries

| Bucket | Type | When present | Purpose |
|--------|------|--------------|---------|
| **`interface`** | `Map<outputName, List<{name, table_ref}>>` | Every query/values/set-op scope | **What this scope exports.** Maps each select-list output name to one or more proven source columns. This is the primary lineage map for downstream consumers. |
| **`query_dictionary`** | `Map<outputName, List<tokenString>>` | Every query scope | Output column names → source token positions. Keys are interface output names only; values are `[@line,start:end='text',<type>,line:col]` strings. |
| **`table_dictionary`** | `Map<tableName, Map<colName, List<tokenString>>>` | Scopes referencing physical tables | Physical table columns → token positions. Keys are canonical table names (may be qualified, e.g. `sch1.aaa`). |
| **`table_alias`** | `Map<alias, refString>` | Scopes with FROM/JOIN | Alias → source: physical table name, `queryN`, `valuesN`, or CTE name. Values are simple strings, not token lists. |

**Relationship between `query_dictionary` and `interface`**

Within the same symbol-table scope, `query_dictionary` and `interface` share the **same list of column keys** — one entry per output column the scope exposes.

| Bucket | Same keys | Different values |
|--------|-----------|-------------------|
| **`query_dictionary`** | Output column names | A reference map back into the original SQL text: line and character positions (`[@line,start:end='text',…]`) for every appearance of that column name within the query, wherever it occurs. |
| **`interface`** | Output column names | For each column, a list of **source columns** used to produce that column's direct value — i.e., its immediate dependencies. |

**Singular vs multiple `interface` entries**

- A **single** source entry most often means the output is a column pulled up from a row source with little or no transformation.
- **Multiple** source entries mean a SQL formula or logical operator was applied across that set of columns to produce the output value.
- The symbol table records **dependencies only** — not the actual expression, function, or operator logic.

**Where source columns can come from**

> **Full catalog:** See [Row sources and `def_*` scope catalog](#row-sources-and-def_-scope-catalog) for authoring context, the complete `def_*` table, and consumer notes.

Row sources from which `interface` entries may draw columns fall into two broad categories: **externally defined** sources the parser cannot fully validate, and **internally defined** sources whose column structure is established within the SQL text itself.

**Physical tables (externally defined, no catalog)**

- Table sources represent **fixed structures in matrix format** for which the PSS Parser has **no corroborating knowledge or catalog**.
- The parser therefore **assumes the author has been perfect** in choosing column names from tables — it has no way of verifying that a referenced column actually exists on the named table.
- Column references from tables are recorded in **`table_dictionary`** (table name → column → token positions). Lineage through a table alias appears in **`interface`** with `table_ref` pointing at the alias.

**Internally defined row sources (published as `def_*` scopes)**

Unlike physical tables, the following sources are **defined by the SQL statement itself**. Each kind is finalized as its own **`def_*` symbol-table scope** (or nests inside one). These scopes carry their own `interface`, `query_dictionary`, and related buckets — the same shape as a query scope where applicable.

| `def_*` key | SQL construct | Notes |
|-------------|---------------|-------|
| **`def_queryN`** | `SELECT`, subquery, CTE body, `CREATE TABLE AS SELECT` source, `INSERT … SELECT` source, `UPDATE … FROM` source | Most common scope type; may nest other `def_*` children |
| **`def_valuesN`** | `VALUES` row constructor (in `FROM` or as `INSERT` source) | Column names derived from row position or explicit aliases |
| **`def_unionN`** | `UNION` and **`EXCEPT`** composite | Branches are nested `def_query*` children; non-anchor branches carry `setop=UNION` or `setop=EXCEPT` |
| **`def_intersectN`** | `INTERSECT` composite | Same nesting pattern as union; branches may carry `setop=INTERSECTION` |
| **`def_insertN`** | `INSERT` statement | Contains nested `def_queryN` / `def_valuesN` for the source plus target-column mapping |
| **`def_updateN`** | `UPDATE` statement | Contains `assignments`, optional nested `def_queryN` source, target dictionaries |
| **`def_deleteN`** | `DELETE` statement | Target `table_dictionary` and `filters` |
| **`def_createN`** | `CREATE` (table, view, etc.) | Often empty or minimal; `CREATE TABLE AS SELECT` nests a `def_queryN` for the source |
| **`def_alterN`** | `ALTER` | Typically minimal payload |
| **`def_dropN`** | `DROP` | Typically minimal payload |
| **`def_truncateN`** | `TRUNCATE` | Typically minimal payload |

**Other row sources (within a `def_queryN` scope, not separate `def_*` types)**

- **Table functions** — appear as aliased row sources in `table_alias` and `table_dictionary` (e.g. `flatten0`, `generator0`) within a parent query scope.
- **Relational operators** (PIVOT, UNPIVOT) — modifier-derived columns tracked in **`derivation`**; source columns referenced via modifier bucket keys (`tuple_0`, `outer_up`, `unpvt`, etc.).
- **DML statements as row sources** — in rare dialect-specific situations, an `UPDATE` or similar may appear as a `FROM` source; treated as a nested scope within the consuming query.

**Script-level wrapper (not a `def_*` type)**

Multi-statement scripts collect per-statement symbol tables under a **`SCRIPT`** key keyed by statement number (`"1"`, `"2"`, …), each value being the statement's root `def_*` payload.

**`interface` example**

```
interface={
  da=[{name=a, table_ref=b}],
  ca=[{name=a, table_ref=a}]
}
```

For a set-op composite scope, `interface` values may use the sentinel `"query_column"` meaning "column aligned across branches" — resolved from branch interfaces at finalize.

---

### Clause column-reference archives

These buckets list columns referenced in specific SQL clauses. Values are **lists of column refs** (`{name, table_ref}` or substitution objects), not token strings.

| Bucket | SQL clause | When present |
|--------|------------|--------------|
| **`filters`** | WHERE, HAVING, QUALIFY, JOIN ON | Any of those clauses reference columns |
| **`grouped_by`** | GROUP BY | GROUP BY present |
| **`ordered_by`** | ORDER BY (query-level) | ORDER BY present |
| **`window_partition_by`** | `OVER (PARTITION BY …)` | Window function with PARTITION BY |
| **`window_ordered_by`** | `OVER (… ORDER BY …)` | Window function with in-OVER ORDER BY |

> **Note:** There is no bucket named `window_over_partition`. Window PARTITION BY and in-OVER ORDER BY are archived separately as `window_partition_by` and `window_ordered_by`. Partition/order-only column names are kept out of `query_dictionary`; they live in these buckets. SELECT-list window outputs merge OVER dependencies onto `interface`.

**Example**

```sql
SELECT ROW_NUMBER() OVER (PARTITION BY k_stfd, kppi ORDER BY OBSERVATION_TM) AS key_rank
FROM tab1
```

```
window_partition_by=[{name=k_stfd, table_ref=null}, {name=kppi, table_ref=null}]
window_ordered_by=[{name=OBSERVATION_TM, table_ref=null}]
interface={
  key_rank=[{name=k_stfd, table_ref=tab1}, {name=kppi, table_ref=tab1},
            {name=OBSERVATION_TM, table_ref=tab1}, {name=row_num, table_ref=tab1}]
}
```

---

### CTE / WITH visibility

| Bucket | Type | When present | Purpose |
|--------|------|--------------|---------|
| **`context_list`** | `Map<cteAlias, queryRef>` | `WITH` clause or inherited from outer scope | Named CTE aliases visible to nested queries. Values are live refs like `query0` (resolve to sibling `def_queryN`). |
| **`parent_cte`** | String or map | CTE shadowing chains | Optional inherited CTE chain metadata |

**Example**

```sql
WITH left_cte AS (SELECT a, b FROM third),
     right_cte AS (SELECT a, b FROM fourth)
SELECT l.a AS da, r.a AS ca FROM left_cte l JOIN right_cte r ON ...
```

```
context_list={left_cte=query0, right_cte=query1, l=query0, r=query1}
table_alias={l=query0, r=query1, left_cte=query0}
```

Walk-time keys (`inherited_visible_aliases`, `outer_context_list_backup`, etc.) are stripped before publish and should not appear in finalized `def_*` payloads.

---

### Predicate and dependent subqueries

| Bucket | Type | When present | Purpose |
|--------|------|--------------|---------|
| **`dependent_queries`** | `Map<kindN, {query, type?}>` | Parent of scalar/EXISTS/IN/quantified subquery | Index of predicate subqueries on this scope |
| **`predicandN`** | Entry under `dependent_queries` | `= (SELECT …)`, select-list scalar subquery | Scalar subquery |
| **`existsN`** | Entry under `dependent_queries` | `EXISTS (SELECT …)` | EXISTS subquery |
| **`in_listN`** | Entry under `dependent_queries` | `IN (SELECT …)` | IN-list subquery |
| **`quantifiedN`** | Entry under `dependent_queries` | `ANY` / `ALL` subquery | Quantified comparison |
| **`type`** (on entry) | String | Always on dependent entry | Clause context: `interface`, `filters`, `group_by`, `order_by` |

**Example**

```sql
SELECT * FROM tab1 WHERE subj_cd IN (SELECT fld FROM orange)
```

```
dependent_queries={in_list1={query=query0, type=filters}}
def_query0={... inner SELECT scope ...}
filters=[{name=subj_cd, table_ref=tab1}]
```

**Important boundary:** Parent scopes do **not** drill into predicate subquery bodies for dictionary collection. Inner references belong to the child's `def_queryN`. Correlated outer columns inside a subquery bubble via `unresolved_column` during the walk and resolve in the enclosing scope.

---

### Relational modifiers (PIVOT / UNPIVOT)

| Bucket | Type | When present | Purpose |
|--------|------|--------------|---------|
| **`derivation`** | Map with sub-buckets | PIVOT or UNPIVOT in FROM | Parent bucket for modifier lineage |
| **`derivation.derived_columns`** | `Map<bucketKey, Map<colName, List<tokenString>>>` | PIVOT/UNPIVOT | Modifier-generated output columns and their token positions |
| **`derivation.source_columns`** | `Map<bucketKey, List<{name, table_ref}>>` | PIVOT/UNPIVOT | Source columns feeding the modifier |
| **`derivation.pivot_derived_source_bindings`** | `Map<bucketKey, Map<derivedCol, sourceCol>>` | PIVOT | Derived aggregate name → operand source column |
| **`derivation.interface_source_ref`** | String | At finalize | Interface alias of the modifier source |
| **`derivation.source_ref`** | String | At finalize | Physical/dictionary source ref |

Bucket keys inside `derivation` are typically `tuple_0`, `tuple_1`, or the modifier alias (`outer_up`, `unpvt`).

> **Note:** The bucket is `derivation` (singular), not `derivations`.

**Example — UNPIVOT**

```sql
SELECT sales_amount, feb_sales
FROM monthly_sales
UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales, mar_sales)) AS outer_up
```

```
derivation={
  source_columns={outer_up=[{name=jan_sales, table_ref=monthly_sales}, ...]},
  derived_columns={outer_up={sales_amount=[[@...]], month_name=[[@...]]}}
}
interface={
  sales_amount=[{name=jan_sales, table_ref=outer_up}, {name=feb_sales, table_ref=outer_up}],
  feb_sales=[{name=feb_sales, table_ref=outer_up}]
}
```

---

### DML-specific buckets

| Bucket | Type | When present | Purpose |
|--------|------|--------------|---------|
| **`assignments`** | `Map<lhsCol, List<{name, table_ref}>>` | UPDATE | SET clause: target column → RHS lineage |
| **`update_dictionary`** | `Map<col, List<tokenString>>` | UPDATE | UPDATE target column → token positions |
| **`target_table`** | Mini `table_dictionary` shape | INSERT/UPDATE/SELECT INTO | Explicit DML target columns |
| **`lhs_unresolved_columns`** | `Map<col, {column, locations}>` | UPDATE (unresolved LHS) | LHS refs pending resolution |
| **`insert_source_ref`** | String | INSERT with SELECT/VALUES | Pointer to nested `def_queryN` / `def_valuesN` |

**Example — UPDATE with subquery source**

```sql
UPDATE employees e
SET score = src.acct_sales_count
FROM (SELECT emp_id, acct_sales_count, ROW_NUMBER() OVER (...) AS rn FROM accounts) src
WHERE e.emp_id = src.emp_id
```

```
def_update1={
  assignments={score=[{name=acct_sales_count, table_ref=src}]},
  update_dictionary={score=[[@4,24:28='score',<392>,1:24]]},
  table_alias={e=employees, src=query0},
  def_query0={... source SELECT with window_partition_by, interface, etc. ...}
}
```

---

### Set operations

| Bucket | Type | When present | Purpose |
|--------|------|--------------|---------|
| **`setop`** | String: `UNION`, `EXCEPT`, `INTERSECTION` | Non-anchor set-op branch only | Which operator introduced this branch |
| **`def_unionN`** / **`def_intersectN`** | Nested scope map | `UNION` / `EXCEPT` use `def_unionN`; `INTERSECT` uses `def_intersectN` | Composite scope containing branch `def_query*` children |

Anchor (first) branch has no `setop` key. Later branches carry `setop`. Composite `interface` uses `query_column` sentinel until branches are aligned.

**Example**

```sql
SELECT col1 FROM sch1.aaa AS aaa
UNION
SELECT col1 FROM sch2.bbb AS bbb
```

```
def_union2={
  def_query0={table_alias={aaa=sch1.aaa}, interface={col1=[{name=col1, table_ref=aaa}]}},
  def_query1={setop=UNION, table_alias={bbb=sch2.bbb}, interface={col1=[{name=col1, table_ref=bbb}]}},
  interface={col1=query_column}
}
```

Set-op scopes can nest: `def_query4` may contain `def_union2` containing `def_query0` + `def_query1`.

---

### Script-level nesting

Multi-statement scripts wrap per-statement symbol tables:

```
symbolTable → {
  SCRIPT: {
    "1": def_query0_payload,
    "2": def_insert0_payload,
    ...
  }
}
```

Each `sql_statement` is walked in isolation; statement numbers are 1-based string keys.

---

## Nesting patterns

### WITH / CTE

1. Each CTE body is walked as its own query scope → `def_queryN`.
2. CTE names register in `context_list` (alias → `queryN`).
3. The main query body inherits `context_list` and may reference CTEs by alias.
4. Sibling `def_query*` from the WITH frame are nested as children of the promoted main-body scope.

Nested `WITH` saves and restores outer `context_list`, `table_alias`, and `def_*` entries before absorbing inner CTEs.

### FROM / JOIN subquery

1. Enter subquery → `pushSymbolTableWithParentVisibleScope()` (inherits outer `context_list` and aliases).
2. Exit subquery → finalize as `def_queryN`, parent gets `table_alias={alias=queryN}`.
3. Parent `interface` entries for `alias.col` resolve through the child's `interface`.

### Set operations

1. Each branch is a separate `def_queryN` inside a `def_unionN` / `def_intersectN` shell.
2. Non-anchor branches carry `setop`.
3. Composite `interface` aligns column names across branches.

### Predicate subqueries (scalar, EXISTS, IN, quantified)

1. Inner SELECT finalizes as `queryN` inside a predicate frame.
2. On exit: record `dependent_queries.{predicand|exists|in_list|quantified}N → {query: queryN, type: clause}`.
3. Rename `queryN` → `def_queryN` nested in parent (or sibling under parent's `dependent_queries` tree).
4. Merge predicate frame into parent via `popSymbolTablePutAll` — no extra `def_` wrapper around the predicate frame itself.

Parent `filters` (or other clause bucket per `type`) holds the outer-side references; the inner body is under `def_queryN`.

### Scalar subqueries in SELECT list

- Tracked during walk via `scalar_subquery_aliases` (stripped at publish).
- Published on parent as `dependent_queries.predicandN` with `type=interface`.
- Parent `interface` maps the output alias to the subquery result column lineage.
- Inner body is a nested `def_queryN`.

### DML with subqueries

- **INSERT … SELECT:** `def_insertN` contains nested `def_queryN` (or `def_valuesN`) as the source; target column mapping appears in `interface` / `target_table`.
- **UPDATE … FROM (subquery):** `def_updateN` contains `def_query0` for the source SELECT plus `assignments` mapping SET targets to source columns.
- **DELETE:** `def_deleteN` with `filters` and target `table_dictionary`; subqueries in WHERE follow the predicate pattern.

---

## Buckets you should not expect in published scopes

These are walk-time only and are stripped before `def_*` publish:

- `unresolved_column` — pending refs (hoisted or resolved at finalize)
- `scalar_subquery_aliases`
- `inherited_visible_aliases`, `local_from_registered_aliases`
- `outer_context_list_backup`, `outer_def_entries_backup`, `mumble_outer_table_alias`
- `_tmp_*` set-operation and DML staging keys
- `modifier_operand_token_refs`, `join_using_operand_token_refs`, etc.

If your consumer sees these keys, it is likely reading a live in-progress frame rather than a finalized `def_*` payload.

---

## Quick traversal recipe

1. Start at the statement root (`def_query0`, `def_insert0`, etc., or `SCRIPT["N"]` for scripts).
2. Read `interface` to learn what the scope exports and where each output column comes from.
3. Use `table_dictionary` for physical lineage; `query_dictionary` for output-name token positions.
4. Check clause buckets (`filters`, `grouped_by`, `ordered_by`, `window_partition_by`, `window_ordered_by`) for columns used in those clauses.
5. Follow `table_alias` values of `queryN` form to nested `def_queryN` children.
6. Follow `dependent_queries` entries to predicate subquery bodies.
7. Check `derivation` when PIVOT/UNPIVOT is present.
8. Use `context_list` to resolve CTE alias references.

---

## Row sources and `def_*` scope catalog

This section preserves the authoring intent behind this document and consolidates the **row-source taxonomy**, the **complete `def_*` key catalog**, and **implementation notes** for consumers building services on top of symbol tables. It is derived from PSS parser behavior (JAR 5.1.3+), `SqlParseSymbolTreeHelper`, and golden tests under `parse/src/test/java/sql/walker/`.

### Authoring context

Services that read symbol tables need to understand not only bucket keys but **how SQL row sources differ in what the parser can know about them**:

- **Physical tables** are treated as fixed matrix-format structures. The PSS Parser has **no corroborating catalog** for them and **cannot verify** that referenced column names actually exist on the named table. It assumes the author has chosen column names correctly.
- **`VALUES`** statements and several other SQL constructs are **defined entirely within the statement text**. Unlike tables, these appear as their own **`def_*` symbol-table scopes**, each carrying `interface`, `query_dictionary`, and related buckets where applicable.
- Within a scope, **`query_dictionary`** and **`interface`** share the **same column key list** but serve different purposes:
  - **`query_dictionary`** — maps each output column name to **token positions** in the original SQL (line and character references for every appearance of that name in the query).
  - **`interface`** — maps each output column name to the **source columns** that directly produce its value (immediate dependencies only; not the full expression logic).
- A **singular** `interface` entry usually means a column pulled up from a row source. **Multiple** entries mean a formula or operator combined several source columns. Sources include tables, subqueries, `VALUES`, table functions, relational operators (PIVOT/UNPIVOT), and in rare dialect cases DML statements used as row sources.

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

### Complete `def_*` key catalog

| `def_*` key | SQL construct | Typical contents | Notes |
|-------------|---------------|------------------|-------|
| **`def_queryN`** | `SELECT`, subquery, CTE body, `CREATE TABLE AS SELECT` source, `INSERT … SELECT` source, `UPDATE … FROM` source | `interface`, `query_dictionary`, `table_dictionary`, `table_alias`, clause buckets, nested `def_*` children | Most common scope type |
| **`def_valuesN`** | `VALUES` row constructor (`FROM` or `INSERT` source) | `interface`, `query_dictionary` (positional or aliased column names) | Appears as its own scope, not inside `table_dictionary` |
| **`def_unionN`** | **`UNION`** and **`EXCEPT`** composites | Nested `def_query*` branches, composite `interface` | **There is no `def_exceptN` key.** `EXCEPT` uses `def_unionN`; non-anchor branches carry `setop=EXCEPT` |
| **`def_intersectN`** | **`INTERSECT`** composite | Nested `def_query*` branches, composite `interface` | Non-anchor branches may carry `setop=INTERSECTION` |
| **`def_insertN`** | `INSERT` statement | `interface`, `target_table`, nested `def_queryN` / `def_valuesN` source | Source scope nested as child |
| **`def_updateN`** | `UPDATE` statement | `assignments`, `update_dictionary`, `filters`, optional nested `def_queryN` | `UPDATE … FROM (subquery)` nests source SELECT |
| **`def_deleteN`** | `DELETE` statement | `table_dictionary`, `filters` | Predicate subqueries indexed via parent's `dependent_queries` |
| **`def_createN`** | `CREATE` (table, view, etc.) | Often `{}` or minimal; CTAS nests **`def_queryN`** | See `SqlEventWalkerScriptsAndDDLTests`, `SqlEventWalkerDdlTests` |
| **`def_alterN`** | `ALTER` | Typically `{}` | Symbol table records scope; few column buckets |
| **`def_dropN`** | `DROP` | Typically `{}` | Symbol table records scope; few column buckets |
| **`def_truncateN`** | `TRUNCATE` | Typically `{}` | Symbol table records scope; few column buckets |

### Implementation notes for consumers

1. **Numeric suffix (`N`)** — Counters are allocated per walk (e.g. `query0`, `query1`, … → `def_query0`, `def_query1`). Numbers are **not globally meaningful** across statements; use structure and nesting, not the integer alone.
2. **Live vs published keys** — During the walk, ephemeral keys (`query0`, `values1`, `union2`) point at in-progress frames. Consumers should read **`def_*` payloads** after finalize, not live keys.
3. **`EXCEPT` is not `def_intersectN`** — `EXCEPT` branches publish under **`def_unionN`** with `setop=EXCEPT` on the non-anchor branch. Only **`INTERSECT`** uses **`def_intersectN`**.
4. **Set-op anchor branch** — The first branch in a set operation has **no `setop` key**; later branches carry `setop=UNION`, `setop=EXCEPT`, or `setop=INTERSECTION`.
5. **Composite `interface` sentinel** — Set-op parent scopes may use `query_column` as an `interface` value placeholder until branch columns are aligned at finalize.
6. **Table functions** — Sources such as `TABLE(FLATTEN(…))` appear as generated aliases (`flatten0`, `generator0`) in `table_alias` and `table_dictionary` **within** a `def_queryN` scope; they do not get a separate top-level `def_*` type.
7. **Nested `def_*` trees** — An outer `def_queryN` routinely contains inner `def_queryM`, `def_unionK`, `def_valuesJ`, etc. Only **`interface`** columns are visible outside a scope; inner buckets require drilling into nested children.
8. **Multi-statement scripts** — The root symbol table may contain `SCRIPT: {"1": <def_* payload>, "2": <def_* payload>, …}` rather than a bare `def_query0`.
9. **Catalog verification boundary** — Do not expect the parser to validate physical table columns. Validation diagnostics check **syntactic consistency and visibility** within the symbol table, not warehouse catalog truth.

### Golden test pointers

| Topic | Test class |
|-------|------------|
| `def_valuesN`, `def_insertN` with VALUES source | `SqlParseEventWalkerWithAccessObjectTest` |
| `def_unionN` / `def_intersectN`, nested set-ops | `SqlEventWalkerJoinsAndTableResolutionTests` |
| `def_createN`, `def_dropN`, `def_alterN`, `def_truncateN` | `SqlEventWalkerDdlTests`, `SqlEventWalkerScriptsAndDDLTests` |
| `def_updateN` with nested `def_queryN` | `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` |
| CTE `context_list` + nested `def_queryN` | `SqlEventWalkerJoinsAndTableResolutionTests` |
| Table functions inside `def_queryN` | `SqlEventWalkerTableFunctionTests` |

---

## Version / source pointers

| Area | Primary source |
|------|----------------|
| Scope finalize and publish | `parse/src/main/java/sql/symboltree/SqlParseSymbolTreeHelper.java` |
| Stack push/pop | `parse/src/main/java/astwalkers/SqlASTWalkerHelper.java` |
| Bucket key constants | `parse/src/main/java/mumble/MumbleConstants.java` |
| Golden examples | `parse/src/test/java/sql/walker/*Tests.java` |
