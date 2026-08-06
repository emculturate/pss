# Relational Modifier Resolution Policy (PIVOT / UNPIVOT)

**Status:** Signed Jul 2026 (Phase 17.1 decision).  
**Audience:** Implementers and AI agents working on `SqlParseEventWalker`, `SqlParseSymbolTreeHelper`, and convert egress.

**Related docs:**

- [symbol-table-resolution-consolidation-worklist.md](symbol-table-resolution-consolidation-worklist.md) — Phases 1–20 **complete** (historical); policy for Phases 16–18
- [table-and-query-dictionary-design.md](table-and-query-dictionary-design.md) — dictionary collection contract

---

## Core principles

1. **Decide as early as full context allows** — bind semantics during the walk at the first grammar point where the necessary facts exist. Avoid convert-time rediscovery or “repair” passes that re-derive what the walk could have recorded.

2. **One implementation, many call sites** — rewrite / consume / materialize rules live in a **single shared helper**. Multiple walk hooks may call it; convert must not maintain a parallel copy of the same logic.

3. **Walk owns semantics; convert owns publication** — convert egress (`resolveColumnRefAtConvertEgress`, `RESOLVED_*` outcomes) **projects** walk-finalized symbol-table state into `table_dictionary` / `derived_columns`. It does not infer UNPIVOT/PIVOT meaning a second time.

4. **Context-dependent handling** — the same token (e.g. `sales_amount`) is handled differently depending on **where** it appears in the grammar and **what** is already known at that moment. This is intentional, not inconsistency.

5. **Defer within the walk, not to convert** — when the grammar visits SELECT before FROM, or clause lists before the modifier tuple is complete, **defer** binding until the enclosing scope closes. That deferral is still walk-time (`exitQuery_specification`), not a convert fallback.

---

## Two-tier binding model (UNPIVOT VALUE / FOR)

Derived columns introduced by UNPIVOT (and analogously PIVOT registry keys) use **two tiers**:

| Tier | When | What is decided | Example |
|------|------|-----------------|---------|
| **1 — Identity** | Modifier tuple finalize (`exitTable_primary` / `resolveUnpivotScopeAtPrimaryExit`) | Names in VALUE/FOR positions **are derived output columns**; record in `RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY`, stamp `MUMBLE_TABLE_REF_KEY` / `RELATIONAL_MODIFIER_SOURCE_REF_KEY`, consume from `unresolved_column` where hygiene applies | `sales_amount` registered as UNPIVOT VALUE derived on source `msl` |
| **2 — Reference rewrite** | Enclosing query scope finalize (`exitQuery_specification` / `finalizeQueryScopeSymbolTable`) | Every captured ref to that derived name in **this query’s** surfaces is rewritten using the recorded hint (VALUE → IN-list physical refs; FOR unchanged as derived) | `SELECT sales_amount`, `GROUP BY sales_amount`, `WHERE sales_amount > 0` all updated consistently |

**Why two tiers:** At `exitColumn` inside SELECT, `sales_amount` cannot yet be tied to the UNPIVOT tuple — FROM has not been walked. At modifier exit (lower, inner context), identity **is** knowable and must be recorded **immediately**. Clause refs encountered later (WHERE, JOIN ON, GROUP BY, …) still batch their rewrite at query closeout because SELECT was also captured before FROM; one scope-finalize pass keeps all surfaces consistent.

**Anti-pattern:** Leaving identity unrecorded at modifier exit and relying on `probeArchivedScopeClauseColumns`, ambiguity suppressors, or duplicate convert-only interface rewrites to rediscover meaning.

---

## Grammar-order call sites (query_specification)

`query_specification` is visited **SELECT → FROM → WHERE → GROUP BY → …**. Therefore:

| Surface | Earliest moment with full context | Action |
|---------|-----------------------------------|--------|
| Modifier phrase operands | `exitUnpivot_clause` | Record hint contract (VALUE, FOR, IN list) |
| Derived identity + unresolved hygiene | `exitTable_primary` | Stamp source refs; tier-1 identity; strip VALUE/FOR/IN from `unresolved_column`; materialize IN physical cols |
| SELECT `interface` VALUE expansion | Convert exit, **after** interface egress loop | Tier-2 `applyUnpivotDerivationsToQueryScope` — VALUE must not be consumed as derived before expansion (`treatDerivedRegistryKeys=false` on interface loop) |
| WHERE / HAVING / QUALIFY / GROUP BY / ORDER BY / JOIN ON | Convert egress with `treatDerivedRegistryKeys=true` | `RESOLVED_UNPIVOT_VALUE` / `RESOLVED_UNPIVOT_FOR` consume; clause list rewrite deferred to **17.3** |
| Published artifacts | Convert egress | `RESOLVED_UNPIVOT_VALUE`, `RESOLVED_UNPIVOT_FOR`, `RESOLVED_UNPIVOT_IN_SOURCE` — mechanical only |

Optional: if a clause is walked **after** FROM and hints are complete, tier-2 may run at that clause exit — but only by calling the **same** scope helper, not duplicate logic.

---

## Consolidation target (Phase 17.1–17.5)

**Canonical helper:** evolve `rewriteReferenceListForSingleUnpivotHint` into a scope-level `applyUnpivotDerivationsToQueryScope(...)` that rewrites `interface`, `filters`, `grouped_by`, `ordered_by`, and other archived clause lists from `RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY`.

**Single walk call site (recommended):** `finalizeQueryScopeSymbolTable` / `exitQuery_specification`.

**Retire at convert (after parity):**

**Retired (17.2–17.5 Jul 2026):**

- `applyUnpivotValueInterfaceDerivations` — inlined into `applyUnpivotDerivationsToQueryScope`
- `applyUnpivotValueDerivationsToReferenceListObject` stub — deleted; clauses use `probeArchivedScopeClauseColumns` + `RESOLVED_UNPIVOT_*`
- `registerUnpivotGeneratedColumnAmbiguitySuppressions` — deleted; walk tier-1 suppress + egress outcomes suffice
- `resolveUnpivotGeneratedColumnSourceRef` — renamed `resolveUnpivotHintModifierTableRef`

**Retained convert hook (17.4):**

- `materializeSelectedUnpivotInColumnsIntoSourceDictionary` — SELECT-listed IN columns in `query_dictionary` merged into `table_dictionary` (passthrough wide columns; walk tier-1 handles `unresolved_column` only)

---

## Namespace quick reference

| UNPIVOT role | Namespace | Tier-1 (modifier exit) | Tier-2 (query exit) | Convert |
|--------------|-----------|------------------------|---------------------|---------|
| VALUE | Derived output | Register identity; consume unresolved | Rewrite refs → IN physical lineage | `RESOLVED_UNPIVOT_VALUE` consume |
| FOR | Derived output | Register identity; consume unresolved | Keep as derived ref | `RESOLVED_UNPIVOT_FOR` consume |
| IN-list | Physical source | Materialize lineage; consume unresolved | — (already physical) | `RESOLVED_UNPIVOT_IN_SOURCE` materialize |
| Phrase qualifiers | Diagnostics | `validateRelationalModifierOperandQualifiers` (17.0b) | — | — |

PIVOT uses the same principles; operand materialization is Phase **16**; derived registry consume is Phase **15**. **Phase 18a** (Snowflake bare IN-list output names as first-class resolution) is **not implemented** — use the **PIVOT naming reformulation** section below instead of selecting bare IN values / IN aliases.

---

## PIVOT naming reformulation (PSS-compatible Snowflake style) — Phase 18 closeout

**Status:** Policy-only (Aug 2026). No resolver change for Snowflake default output names (`q1` alone). Instruct users to alias aggregates and IN values, then **SELECT the registry key**.

### How PSS names PIVOT outputs

Derived registry key = `{inComponent}_{aggregateAlias}` (case preserved from aliases / function name):

| Phrase fragment | `inComponent` | Aggregate alias | Registry key |
|-----------------|---------------|-----------------|--------------|
| `SUM(amt) AS total` … `IN ('jan_sales' AS q1)` | `q1` (IN prefix) | `total` | **`q1_total`** |
| `SUM(amt) AS total` … `IN ('feb_sales')` | `feb_sales` (literal) | `total` | **`feb_sales_total`** |
| `SUM(amt)` (no AS) … `IN ('jan_sales')` | `jan_sales` | `SUM` | **`jan_sales_SUM`** |

AST carries `alias=…` on the aggregate and `pivot_prefix=…` on IN entries when `AS` is present.

### Snowflake vs PSS (do not conflate)

| Construct | Snowflake result column | PSS registry key |
|-----------|-------------------------|------------------|
| `IN ('jan_sales' AS q1)` + `SUM(amt)` | **`q1`** (alias replaces value) | **`q1_SUM`** (or `q1_<aggAlias>`) |
| Select bare `q1` | Valid | **Not** a supported output name — may synthesize wrong source lineage today; treat as unsupported for clean parses |
| Select `q1_SUM` / `q1_total` | Usually absent unless agg aliased that way | **Supported** derived column |

### Instruct users to reformulate

For a clean PSS parse / resolution, rewrite Snowflake-shaped SELECTs to **name the registry key explicitly**:

**Prefer:**

```sql
SELECT empid, q1_total, feb_sales_total
FROM monthly_sales_long
PIVOT (
  SUM(sales_amount) AS total
  FOR month_name IN ('jan_sales' AS q1, 'feb_sales')
);
```

**Avoid (unsupported as pivot outputs in PSS):**

```sql
SELECT empid, q1, feb_sales   -- bare IN alias / bare IN literal as Snowflake column names
FROM monthly_sales_long
PIVOT (
  SUM(sales_amount) AS total
  FOR month_name IN ('jan_sales' AS q1, 'feb_sales')
);
```

**Checklist for authors / agents:**

1. Give the aggregate an **`AS` alias** when you care about a stable suffix (`AS total` → `…_total`).
2. Give each IN value an **`AS` prefix** when you want a short stem (`'jan_sales' AS q1` → stem `q1`).
3. In SELECT / WHERE / JOIN / GROUP BY / …, reference **`{stem}_{aggAlias}`** only — never bare `q1` and never assume Snowflake’s “alias replaces value” naming.
4. Do not expect IN aliases or bare IN literals to appear as physical columns on the pivot **source** table.

**Agent hint:** When rewriting user SQL for this parser, always emit the registry form above; do not “fix” bare Snowflake pivot output names by inventing source `table_dictionary` entries.

---

## Multi-sibling modifiers (Phase 17.6)

When multiple PIVOT or UNPIVOT operators are **siblings** in the same `query_specification` FROM clause (join chain), each tuple exposes derived column names into the same query scope.

| Ref context | Rule |
|-------------|------|
| **Alias-qualified** (`u2.sales_amount`, `p.jan_sales_SUM`) | Resolve to the named modifier tuple's hint / derived lineage. |
| **Unqualified in WHERE / JOIN ON / GROUP BY / …** | Same ambiguity rules as other multi-source columns — multiple visible modifier aliases → diagnostic. |
| **Unqualified in SELECT list** | **Must not** silently pick one tuple when ≥2 sibling modifiers expose the same derived name. Emit `AMBIGUOUS_COLUMN_REFERENCE` (see **17.6.2**). |

Walk-time state under `RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY` may evolve from clause-local column maps to bucketed `derivation.derived_columns` at modifier finalize (`exitTable_primary` / tuple primary). Convert egress **publication dedupe** (**17.7.6**) collapses published clause/interface ref lists to unique `(name, table_ref)` in step **D** — see **Phase 17.7** in the worklist. Per-sibling operand/derived buckets are **17.7** (`derivation.source_columns` / `derivation.derived_columns` keyed by `alias|tuple_N`).

**Convert prune retirement (17.7.8):** Convert must not rely on stripping pivot/unpivot **output** names from physical `table_dictionary` entries (e.g. `jan_sales_SUM` on `monthly_sales_long`). Correct finalize prevents those names from being materialized on physical keys; any leak is a finalize bug, not something convert repairs.

**Query-backed sources (17.6.7):** When a modifier's immediate source is a subquery (`FROM (SELECT …) alias`), operand and derived-column semantics are unchanged — only the **source ref** is a `queryN` (or subquery alias) rather than a physical table. Triple-tuple tests must be duplicated with subquery-backed FROM slots to prove sibling-modifier logic does not assume physical-table-only sources.

---

## Do not

- Add a second rewrite implementation in convert that mirrors walk logic.
- Treat convert `probeArchivedScopeClauseColumns` as the primary UNPIVOT clause resolver.
- Assume every binding can happen at `exitColumn` — respect grammar visit order.
- Skip tier-1 identity at modifier exit because tier-2 will run later.
