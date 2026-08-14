# Ordered select-list output alias policy

**Status:** Locked (Aug 2026) — parser `5.1.3` / `513` symbol-table format  
**Audience:** Symbol-table consumers, lineage tracers, and parser maintainers  
**Related:** [table-and-query-dictionary-design.md](table-and-query-dictionary-design.md), [phase-17.6.9-window-query-dictionary-policy.md](phase-17.6.9-window-query-dictionary-policy.md), Phase **13.4** in [symbol-table-resolution-consolidation-worklist.md](symbol-table-resolution-consolidation-worklist.md)

---

## Goal (one sentence)

While a `SELECT` list is being built **left to right**, any **output alias** from an earlier select-list item may be referenced as a column anywhere a later item in the **same** list accepts a column input — and published lineage must record that hop as **`{name=<alias>, table_ref=queryN}`**, not as a physical table column.

---

## Scope boundary

| In scope | Out of scope |
|----------|----------------|
| Unqualified refs to **prior** output aliases inside the **same** `query_specification` select list | Refs to aliases defined **later** in the list (forward references) |
| `interface`, `window_partition_by`, `window_ordered_by`, and justified `query_dictionary` tokens for those refs | Outer queries referencing inner select-list aliases not on the child's published `interface` |
| Any select-list **expression shape** that can carry an explicit or implicit output alias | Circular alias swaps (`a AS b, b AS a`) and other **ungrounded** alias graphs |
| Query-level clauses (`WHERE`, `GROUP BY`, `ORDER BY`, `QUALIFY`, …) referencing **completed** select-list outputs | Replacing SQL engine runtime evaluation order beyond what the parser encodes |

Query-level forward-alias binding for **archived clause buckets** uses the same **grounded output alias** rules but **does not** apply the select-list **source-order** gate — by the time those clauses are finalized, the full select list exists.

---

## Three rules consumers must apply together

### 1. Groundedness — is this name a valid output alias to reference?

An output alias is **grounded** when every immediate dependency in its `interface` entry is one of:

- A substitution (`{substitution:…}` or flat `{name=<var>, type=predicand|column|condition}`)
- A physical / query-backed column with a non-null `table_ref` (after convert egress)
- A bare ANSI/dialect value expression (`CURRENT_DATE`, `SESSION_USER`, …)
- Another **grounded** output alias (transitive closure; cycle detection returns **not grounded**)

**Predicand and column substitutions** on the defining select item count as grounded origins even when `table_ref` is null on that interface entry.

### 2. Select-list source order — may this *particular* hop use `queryN`?

Inside the **`interface` bucket only**, a dependency on output alias `B` from output column `A` is stamped **`table_ref=queryN`** only when `B` appears **earlier** in select-list source order than `A`.

Source order is derived from earliest `query_dictionary` token positions per interface key (`extractInterfaceColumnNamesInSelectTokenOrder` at convert egress).

Forward references keep `table_ref=null` in `interface` and remain in `unresolved_column` until finalize emits diagnostics.

### 3. Clause context — window and archived lists

References inside a later select-list item's **`OVER (PARTITION BY … ORDER BY …)`** are archived to `window_partition_by` / `window_ordered_by` and resolved at scope exit via the same **grounded output alias** skip path as `WHERE` / `GROUP BY` / `ORDER BY`.

Those sites **do not** use the select-list order gate from rule **2** — partition/order columns may reference any **grounded** prior alias because the defining item already appeared earlier in the walk.

Published shapes:

| Bucket | Prior alias `prior_alias` |
|--------|---------------------------|
| `window_partition_by` | `{name=prior_alias, table_ref=queryN}` |
| `window_ordered_by` | `{name=prior_alias, table_ref=queryN}` |
| `interface.<consumer>` | `{name=prior_alias, table_ref=queryN}` when rule **2** passes |
| `query_dictionary` | Usage tokens merged for `prior_alias` when archived-clause or interface egress consumes the ref |

See [phase-17.6.9-window-query-dictionary-policy.md](phase-17.6.9-window-query-dictionary-policy.md) for when partition/order-only names get `query_dictionary` keys vs window archive lists only.

---

## Select-list item origins (all may define `prior_alias`)

Any grammar production that can appear as a `select_item` and receive an output alias is in policy scope. The **alias name** is what later items reference; the **origin expression kind** only affects groundedness (rule **1**).

| Origin kind | Example | Grounded via |
|-------------|---------|--------------|
| Bare column | `a AS prior_alias` | Physical `table_ref` on interface entry |
| Arithmetic | `a + b AS prior_alias` | Physical / query deps |
| Function | `LOWER(a) AS prior_alias` | Physical / query deps |
| `CASE` | `CASE … END AS prior_alias` | Deps in branches |
| `CAST` | `CAST(a AS VARCHAR) AS prior_alias` | Inner expression deps |
| Predicand substitution | `<partner> AS prior_alias` | `type=predicand` on interface entry |
| Column substitution | `<email_col> AS prior_alias` | `type=column` on interface entry |
| Comparison / boolean substitution item | `<a> >= <b> AS prior_alias` | Substitution deps on interface entry |
| Bare value | `CURRENT_DATE AS prior_alias` | Bare-value registry |
| Window output (prior item) | `ROW_NUMBER() … AS prior_alias` | Window / OVER deps per window policy |

**Not yet contract-tested:** scalar subquery select items `(SELECT …) AS prior_alias` — treat as implementation gap until a golden lands.

---

## Column-ref consumer sites (later select-list items)

Wherever the grammar accepts a **column reference** inside a **later** select-list item, a **prior grounded alias** must resolve the same way:

| Consumer site | Example | Archived / interface surface |
|---------------|---------|------------------------------|
| Arithmetic | `prior_alias + 1 AS nxt` | `interface.nxt` |
| Function argument | `TRIM(prior_alias) AS nxt` | `interface.nxt` |
| `CAST` / concat / other scalar ops | `CAST(prior_alias AS INT) AS nxt` | `interface.nxt` |
| `OVER` partition | `ROW_NUMBER() OVER (PARTITION BY prior_alias) AS rn` | `window_partition_by`, `interface.rn` |
| `OVER` order | `ROW_NUMBER() OVER (ORDER BY prior_alias) AS rn` | `window_ordered_by`, `interface.rn` |
| Window aggregate arg | `SUM(b) OVER (PARTITION BY prior_alias ORDER BY prior_alias)` | `window_*`, `interface.s` |

---

## Negative controls (must not bind)

| Case | Expected consumer-visible outcome |
|------|-------------------------------------|
| Reversed select-list order (`z` uses `y` before `y` is defined) | `UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES` + `UNRESOLVED_UNQUALIFIED_COLUMNS`; `interface` keeps `table_ref=null` for the forward hop |
| Cross-renamed circular swap (`a AS b, b AS a`) | Ungrounded; fatals / unresolved — no `queryN` stamping |
| Outer query uses inner select-list alias not on subquery `interface` | Fatal against visible `FROM` aliases only |

---

## Tracing algorithm hint (consumers)

When walking `interface` for output column `C`:

1. For each dependency `{name, table_ref}`:
2. If `table_ref` is `queryN` (same scope) → next hop is `interface.<name>` in **this** `def_queryN` (intra-query alias chain).
3. If `table_ref` is a physical alias / table → leaf in `table_dictionary`.
4. If `table_ref` is null and `name` is not a bare value → treat as unresolved / forward / in-progress ingress unless your tool also models walk-time buckets.

Do **not** treat `table_ref=null` on an intra-list alias ref as a physical column in `FROM`.

---

## Parser implementation anchors

| Concern | Location |
|---------|----------|
| Groundedness / cycles | `isGroundedInterfaceOutputAlias`, `isGroundedInterfaceDependencyRef` |
| Select-list order gate (interface only) | `isPrecedingSelectListOutputAliasInInterface` |
| Interface `queryN` stamp | `tryStampGroundedOutputAliasInterfaceDependencyToQueryScope` |
| Window / clause `queryN` stamp | `trySkipSelectListOutputAliasArchivedClauseRef` → `RESOLVED_INTRA_QUERY_OUTPUT_ALIAS` |
| Select-list registration | `SqlParseEventWalker.exitSelect_item` |
| Window harvest | `captureClauseDependencies` → `window_partition_by` / `window_ordered_by` |

---

## Contract tests (representative)

| Class | Coverage |
|-------|----------|
| `SqlEventWalkerSelectListOrderedAliasRefTests` | Matrix: origins × consumers (arithmetic, function, window partition/order, predicand, bare value, …) |
| `SqlEventWalkerCoreSelectFromAliasingTests` | Chained arithmetic (`V1`), reversed order (`V2`), predicand chain (`V3`/`V4`), outer-scope negative |
| `SqlEventWalkerFunctionsAggregatesWindowingTests` | Physical + predicand column in `PARTITION BY` |
| `SqlEventWalkerLiveSampleQueriesTests` | `donorEmailWithInvalidFatalErrorOnQualifiedColumnVariableTest` (production-shaped predicand + window) |

When extending the grammar with new `select_item` shapes, add a row to **Origins** and at least one **consumer site** test before claiming consumer support.

---

## Refactor guardrails

- [ ] Forward refs never receive `table_ref=queryN` in `interface` or archived window lists.
- [ ] Predicand/column substitution origins are **grounded** for clause and window resolution.
- [ ] `interface` self-ref hops use `queryN` only when the dependency alias is **earlier** in select-list token order.
- [ ] `query_dictionary` records usage tokens when egress consumes a prior alias ref; do not mirror physical-only lineage onto interface keys.
- [ ] Outer scopes never inherit unpublished inner select-list aliases.
