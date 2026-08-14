---
name: parse-and-explain
description: >-
  Parse SQL with pss-parse and explain or trace symbol-table lineage. Use when
  implementing explainSymbolTable, traceLineage, column tracing, source impact
  analysis, or enhancing RMCP cli-parse-and-explain for 5.1.3 (513) output.
---

# Parse and explain (PSS symbol tables)

## When to use

- Implementing or enhancing **lineage explanation** (`traceLineage`, `explainSymbolTable`)
- **Column tracing** (reference → physical source, or source → impacts)
- Debugging **513** symbol tables (`def_queryN`, `dependent_queries`, `derivation`)
- Comparing explain output across parser versions

## Step 1 — Read consumer docs

Always load `.cursor/skills/pss-parse-consumer-docs/SKILL.md` and read at least:

- `symbol-table-bucket-reference` — buckets, tracing algorithms, `dependent_queries`
- Any manifest doc that matches the feature (modifiers, windows, dictionaries)

## Step 2 — Starter algorithm (current RMCP implementation)

The shipped starter lives in RMCP `symbolTableExplainer.ts` (`traceLineage`):

1. Detect format: **`5.1.3`** if top-level keys match `def_queryN` / `def_unionN` / …
2. For each output layer, read **`interface`** (output column → list of `{name, table_ref}`)
3. Resolve **`table_ref`** via **`table_alias`** (and CTE maps: `context_list` / `CTE_LIST`)
4. If alias points at nested layer (`query0` / `def_query0`), open child scope and repeat
5. If alias points at physical table or substitution ref, emit leaf description
6. Optionally mention **`filters`** columns at each layer

This is a **presentation-oriented backward walk** from outputs. It does **not** yet fully implement the parser’s two canonical consumer algorithms below.

## Step 3 — Target algorithms (from symbol-table-bucket-reference)

Enhance the starter by aligning with these pseudocode procedures.

### A. Recursive column tracing (reference → sources)

**Direction:** Given any `{name, table_ref}` in any role bucket, walk **down** to physical leaves.

1. **Dual-source per role:** For each clause role (`filters`, `grouped_by`, `interface`, …), read the archive bucket **and** scan `dependent_queries` where `entry.type == role_bucket_name`. Open `scope["def_" + entry.query]` for subquery bodies.
2. **Alternation loop** at each hop in scope **S**:
   - Hold current `{name, table_ref}`
   - Resolve `table_ref` via `table_alias` / `context_list` in **S**
   - If physical table → **leaf** (`table_dictionary`)
   - If `queryN` → open child `def_queryN`, read child **`interface`** for next hop(s)
   - If modifier bucket → `derivation.source_columns[bucket]`
   - If set-op composite → align column across branch `interface` entries
3. **Fan-out:** Multiple `interface` deps → trace each branch separately.
4. **Never skip scopes** — grandchild columns are invisible unless re-exported through each child’s `interface`.

### B. Source column impact tracing (source → impacts)

**Direction:** Given physical `{table, column}` from the **global** table dictionary, find **every participation action** and propagate **up** through exports.

1. **Anchor** on global `table_dictionary[table][column]` token proof.
2. Find **leaf introduction sites** — scopes where local `table_dictionary[table][column]` has tokens.
3. At each site, scan **all role buckets**; record **participation role** (filter, grouping, feeds output, modifier operand, subquery participation, …).
4. When anchor appears in `interface.<out_col>` deps, enqueue derived ref `{name=out_col, alias}` and walk **up** to parent scopes via `table_alias` (parent sees `{name=out_col, table_ref=alias}`, not the physical table).
5. Scan **`dependent_queries`** children for **correlated** inner uses.
6. Emit a **fan-out tree** rooted at the physical column with labeled branches per action.

Inverse of recursive tracing: **leaf → interface exports → parent role buckets → statement root**.

## Step 4 — RMCP CLI (downstream)

```bash
# Extension dev workspace
node rmcp/tools/cli-parse-and-explain.js --file query.sql --explain-only

# Installed extension
node <extension-root>/tools/cli-parse-and-explain.js --yaml query.sql.yaml --explain-only
```

Route explain logic by **`rmcpParserMeta.versionTag`** (`5.1.2` vs `5.1.3`). Do not run 5.1.2 lineage on `def_query0` trees.

## Implementation checklist when enhancing traceLineage

- [ ] Read manifest docs for buckets you will traverse
- [ ] Handle `def_*` storage keys while `table_alias` may still say `query0`
- [ ] Implement dual-source `dependent_queries` scan per role bucket
- [ ] Support `derivation.source_columns` for UNPIVOT/PIVOT hops
- [ ] Separate **trace down** (ambiguous ref → confirm leaf) from **impact up** (audit column usage)
- [ ] Add regression tests with fixtures under `rmcp/src/test/fixtures/`

## Parser source pointers (this repo)

| Area | File |
|------|------|
| Publish / `def_*` finalize | `parse/src/main/java/sql/symboltree/SqlParseSymbolTreeHelper.java` |
| Bucket constants | `parse/src/main/java/mumble/MumbleConstants.java` |
| Golden tests | `parse/src/test/java/sql/walker/` |
