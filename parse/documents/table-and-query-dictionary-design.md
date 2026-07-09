# Table Dictionary and Query Dictionary — Design Contract

This document is the authoritative mental model for how **table dictionaries** and **query dictionaries** collect column-reference token strings during SQL parse walking. Use it to guard refactors: changes that violate these rules are regressions unless this document is updated deliberately.

Related implementation notes: [symbol-table-resolution-consolidation-worklist.md](symbol-table-resolution-consolidation-worklist.md) (Phase 5+ publish contract, global vs `def_queryN` snapshots).

---

## Shared purpose

Both dictionaries record the **known origins** of column references encountered while walking the query, as token-string location lists (`[@line,col:col='text',<type>,line:col]`).

Collection follows **grammar visibility**: a reference is associable to a source only when the active symbol-table frame can see that source under the rules below. External references may bind to physical tables, query-backed aliases, or remain unresolved until a parent scope provides enough context.

---

## Table dictionary

### What it indexes

**Physical table columns** (and table-function sources treated similarly): keys are **table names** (canonical physical refs); values are column names → deduplicated token lists.

### When a reference is collected

From **any clause** in the active query frame, when there is a clear visibility path:

**Predicate subqueries:** the same boundary rule as for query dictionaries applies — do not drill into scalar predicand, EXISTS, or IN-list subquery bodies from the parent frame. Correlated outer columns inside those subqueries bubble via `unresolved_column`; see [Predicate subqueries — do not drill inward](#predicate-subqueries--do-not-drill-inward-scalar-predicand-exists-in-list) under Query dictionary.

| Binding style | Rule |
|---------------|------|
| **Qualified** | `table_ref.column` or `alias.column` where the prefix resolves to a visible physical table (possibly through alias indirection). |
| **Unqualified** | When exactly one physical table source is visible in the frame, unqualified names are assumed to originate from that table. Generous leeway — wrong assumptions are expected to be corrected only at real execution time. |
| **Multi-table** | Unqualified refs without a single-table proof are not assumed; qualified prefixes are required. |

### Local vs global

| Scope | Behavior |
|-------|----------|
| **Per-query symbol table** (`def_queryN.table_dictionary`) | Columns and tokens collected only from references walked **inside that query's grammar context**. |
| **Global walker table dictionary** | Merges every valid, deduplicated token collected across **all subqueries and phrases** of the statement into a single entry per physical table. |

The global table dictionary is intentionally **cross-scope**: `tab_a.pd1` tokens from the outer query, a scalar predicand, and a nested derived table may all appear under one `tab_a` entry.

### What it is not

- Not keyed by query output column names.
- Not used for query-alias / subquery interface columns (those are query-backed).
- PIVOT/UNPIVOT **derived** columns that are not physical table columns belong in relational-modifier derived-column metadata, not invented as physical table keys without a real backing table column.

---

## Query dictionary

### What it indexes

**Output interface column names** for one individual query scope: keys are **select-list output names** (including DML target-column shapes and VALUES-row outputs); values are deduplicated token lists for every **valid appearance** of that output column name within the visibility rules below.

Each `queryN` / `def_queryN.query_dictionary` is **locked to that query only**. The global `queryColumnDictionaryMap` holds one entry per query key (`query0`, `query1`, …), not a single flattened namespace.

### Membership rule (keys)

Only names that appear in the query's **projected interface** (select list, INSERT column list, VALUES row shape, UPDATE projection, etc.) may be keys.

**Exception — relational modifiers:** PIVOT/UNPIVOT can synthesize **derived columns** tracked in local `derived_columns` / hint metadata. Those generated names must **not** widen the pure query dictionary unless they are also genuine interface outputs. Keep derived-column lineage separate so the query dictionary stays a clean record of declared outputs.

### When token strings are collected (values)

For each interface output column, record token locations whenever that output name is referenced and resolvable in:

#### A. Intra-query sibling clauses (same grammar frame)

Any clause of the query that defined the output, including but not limited to:

- SELECT list (origin / definition)
- FROM-JOIN **ON**
- WHERE
- GROUP BY
- HAVING
- QUALIFY
- ORDER BY
- Boolean/predicand formulas in those clauses (see **predicate subqueries** below for visibility limits)

**Intent:** the query dictionary is a **complete record of valid appearances** of each output column **within its owning query**, not merely a select-list index.

#### Predicate subqueries — do not drill inward (scalar predicand, EXISTS, IN-list)

Clause scanning for output-column tokens applies to references that belong to the **current query's visible namespace**. It must **not** descend into the body of:

- **Scalar predicand** subqueries (`WHERE col = (SELECT …)`)
- **EXISTS** subqueries (`WHERE EXISTS (SELECT …)`)
- **IN-list** subqueries (`WHERE col IN (SELECT …)`)

Those subqueries have a **separate symbol-table context**. Their internal aliases, table names, and column names may coincidentally match outer names but are **not** the same bindings. Drilling into them from the parent's clause-walk would merge unrelated tokens, confuse resolution/finalization, and violate grammar visibility.

| Location | Collection rule |
|----------|-----------------|
| **References in the parent clause outside the subquery** (e.g. the LHS of `oa.pd3 = (SELECT …)`, or other predicates siblings to the subquery) | Collect normally when resolvable in the current frame. |
| **Correlated references inside the subquery** that semantically belong to an outer scope | Do **not** collect by drilling from the parent. They enter the **`unresolved_column` mechanism** and **bubble up** to the enclosing scope that can resolve them. |
| **References wholly inside the subquery body** | Owned by that subquery's own finalize pass (`def_queryN` for the predicand / EXISTS / IN child), not by the parent's clause sweep. |

**Refactor rule:** treat predicate subqueries as opaque boundaries during parent intra-clause dictionary collection; use deferred/unresolved ingress and scope-exit bubble-up for correlated outer columns — consistent with `dependent_queries` / predicand handoff and the worklist rule against backpatching finalized child payloads.

#### B. Parent-scope external references (one level up)

When this query is exposed as a **FROM/JOIN source** (query alias) in an **immediate parent** query, qualified references to this query's interface columns (e.g. `ix.pd7` where `ix` → `query0`) are collected into **this query's** `query_dictionary` under the output column name (`pd7`).

**Visibility limit:** parent-only for outward collection — a reference in a grandparent does not bind through multiple hops in one step; each scope collects for its own outputs and the children it directly sources. Downward visibility into child subqueries follows the child's own frame.

This is **phase-2 external usage** materialization (qualified refs on query aliases that map to a known subordinate interface).

### Local vs global (query)

| Artifact | Role |
|----------|------|
| **`def_queryN.query_dictionary`** | Immutable snapshot at scope finalize: outputs + intra-query sibling appearances resolved at that frame's exit. |
| **Global `queryColumnDictionaryMap[queryN]`** | Live index; may also receive parent-scope qualified usages (phase 2) after child publish. May be richer than the embedded `def_queryN` snapshot — see worklist publish contract. |

Do **not** backpatch finalized child `def_queryN` payloads when a parent later resolves a correlated or deferred ref; parent attribution belongs in the parent's published payload.

### What it is not

- Not a substitute for physical table lineage (use table dictionary).
- Not keyed by physical `table.column` except when that name is also an interface output key.
- Not a dump of every column visible in the FROM clause — only **declared outputs** and their valid references.
- Not populated by copying wholesale from the table dictionary (table dict and query dict are parallel tracks driven by the same resolution events).

---

## Resolution should drive both dictionaries

Token collection must happen **at resolution / materialization time** while the unresolved entry still carries locations:

1. **Walk** archives references into `unresolved_column` (and captured qualified locations).
2. **Convert / scope exit** resolves each reference; on success:
   - physical proof → **table dictionary**
   - interface output proof → **query dictionary** (same event, not a later table-dict backfill)
3. **Archived clause probe** (`filters` / `grouped_by` / `ordered_by`) marks refs **SATISFIED** when table proof already exists; any still-unconsumed resolution tokens are mirrored onto interface outputs in the query dictionary.
4. **Phase 2** merges parent qualified usages (`alias.output_col`) into the **source query's** global query dictionary.

Avoid end-of-pass fallbacks that **read table dictionary entries to infer query dictionary tokens** — that loses per-clause provenance and blurs the two roles.

---

## Two-phase query dictionary model (implementation summary)

| Phase | When | What |
|-------|------|------|
| **1 — Origins** | `exitSelect_item` (and statement-specific seeds) | Every interface output name receives its select-list origin tokens. |
| **2 — External usage** | Scope exit, after interface resolution | Qualified refs on query aliases in parent scopes merge into the **source** query's dictionary. |
| **Intra-sibling usage** | Qualified physical materialization + SATISFIED clause probe | WHERE / GROUP BY / ORDER BY (and peers sharing the same materialization path) merge onto owning interface keys during resolution. |

---

## Refactor guardrails (quick checklist)

Before merging a dictionary change, ask:

- [ ] Does table-dict collection require visible physical proof (qualified prefix or single-table assumption)?
- [ ] Does query-dict collection use an **interface output name** as the key?
- [ ] Are PIVOT/UNPIVOT derived-only names kept out of the pure query dictionary?
- [ ] Are intra-query sibling clauses covered by resolution materialization, not table-dict copy?
- [ ] Does clause collection **stop at predicate-subquery boundaries** (scalar predicand, EXISTS, IN-list) and rely on unresolved bubble-up for correlated refs — not drill into child subquery namespaces?
- [ ] Are parent-scope refs pushed to the **source query's** dictionary, not backpatched into finalized children?
- [ ] Is global-vs-`def_queryN` variance explained by the publish contract, not treated as a bug?

---

## Code anchors

| Concern | Primary location |
|---------|------------------|
| Scope-exit convert / materialization | `SqlParseSymbolTreeHelper.convertSymbolTableToTableDictionary` |
| Physical qualified materialize + query mirror | `materializeQualifiedUnresolvedEntry`, `mergeInterfaceOutputTokensFromQualifiedPhysicalResolution` |
| Explicit qualified batch at convert | `extractExplicitQualifiedUnknownEntries`, `emitExplicitQualifiedUnknownDiagnostics` |
| Archived clause probe | `probeArchivedScopeClauseColumns`, `ArchivedClauseColumnRefDisposition.SATISFIED` |
| Phase 2 external query-alias usage | `mergeSelectListQualifiedQueryAliasRefsIntoSourceQueryDictionary` |
| Predicand / correlated unresolved bubble-up | `dependent_queries`, deferred `unresolved_column` at scope exit, `finalizeQueryScopeSymbolTable` pass-up flags |
| Phase 1 output origins | `SqlParseEventWalker.exitSelect_item` → `addAliasTokensObject` |
| Global / published sync | `mergeIntoGlobalQueryColumnDictionary`, `syncPublishedScopeQueryDictionariesFromGlobal` |

---

## Known implementation gaps (audit against this contract)

These are areas to verify or extend; they are **not** relaxations of the contract above.

1. **Archived clause keys:** `SCOPE_CLAUSE_COLUMN_LIST_KEYS` currently lists `filters`, `grouped_by`, `ordered_by` only. HAVING, QUALIFY, and JOIN-ON conditions may use separate symbol-table keys and need the same SATISFIED / materialization path if not already routed through `filters` or the explicit-qualified batch.
2. **Output token shape:** Phase 1 may record bare output-name tokens (`pd7`) while goldens sometimes expect alias tokens (`ic`) from the select-list source ref — align origin capture with interface ref tokens if goldens encode source-alias provenance.
3. **`backfillQueryDictionaryFromResolvedInterfaceSources`:** Legacy safety net when query dict is empty; must not replace resolution-driven capture for sibling clauses.
