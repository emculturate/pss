# Relational Modifier Resolution Policy (PIVOT / UNPIVOT)

**Status:** Signed Jul 2026 (Phase 17.1 decision).  
**Audience:** Implementers and AI agents working on `SqlParseEventWalker`, `SqlParseSymbolTreeHelper`, and convert egress.

**Related docs:**

- [symbol-table-resolution-consolidation-worklist.md](symbol-table-resolution-consolidation-worklist.md) — Phase 16–18 execution tracking
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
| **1 — Identity** | Modifier tuple finalize (`exitTable_primary` / `resolveUnpivotScopeAtPrimaryExit`) | Names in VALUE/FOR positions **are derived output columns**; record in `DERIVED_COLUMNS_HINTS_KEY`, stamp `MUMBLE_TABLE_REF_KEY` / `RELATIONAL_MODIFIER_SOURCE_REF_KEY`, consume from `unresolved_column` where hygiene applies | `sales_amount` registered as UNPIVOT VALUE derived on source `msl` |
| **2 — Reference rewrite** | Enclosing query scope finalize (`exitQuery_specification` / `finalizeQueryScopeSymbolTable`) | Every captured ref to that derived name in **this query’s** surfaces is rewritten using the recorded hint (VALUE → IN-list physical refs; FOR unchanged as derived) | `SELECT sales_amount`, `GROUP BY sales_amount`, `WHERE sales_amount > 0` all updated consistently |

**Why two tiers:** At `exitColumn` inside SELECT, `sales_amount` cannot yet be tied to the UNPIVOT tuple — FROM has not been walked. At modifier exit (lower, inner context), identity **is** knowable and must be recorded **immediately**. Clause refs encountered later (WHERE, JOIN ON, GROUP BY, …) still batch their rewrite at query closeout because SELECT was also captured before FROM; one scope-finalize pass keeps all surfaces consistent.

**Anti-pattern:** Leaving identity unrecorded at modifier exit and relying on `probeArchivedScopeClauseColumns`, ambiguity suppressors, or convert-only `applyUnpivotValueInterfaceDerivations` to rediscover meaning.

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

**Canonical helper:** evolve `rewriteReferenceListForSingleUnpivotHint` into a scope-level `applyUnpivotDerivationsToQueryScope(...)` that rewrites `interface`, `filters`, `grouped_by`, `ordered_by`, and other archived clause lists from `DERIVED_COLUMNS_HINTS_KEY`.

**Single walk call site (recommended):** `finalizeQueryScopeSymbolTable` / `exitQuery_specification`.

**Retire at convert (after parity):**

- `applyUnpivotValueInterfaceDerivations` (duplicate rewrite)
- `applyUnpivotValueDerivationsToReferenceListObject` stub → folded into walk helper
- `materializeSelectedUnpivotInColumnsIntoSourceDictionary` where walk already materialized
- `registerUnpivotGeneratedColumnAmbiguitySuppressions` (band-aid once tier-1 + tier-2 are correct)

---

## Namespace quick reference

| UNPIVOT role | Namespace | Tier-1 (modifier exit) | Tier-2 (query exit) | Convert |
|--------------|-----------|------------------------|---------------------|---------|
| VALUE | Derived output | Register identity; consume unresolved | Rewrite refs → IN physical lineage | `RESOLVED_UNPIVOT_VALUE` consume |
| FOR | Derived output | Register identity; consume unresolved | Keep as derived ref | `RESOLVED_UNPIVOT_FOR` consume |
| IN-list | Physical source | Materialize lineage; consume unresolved | — (already physical) | `RESOLVED_UNPIVOT_IN_SOURCE` materialize |
| Phrase qualifiers | Diagnostics | `validateRelationalModifierOperandQualifiers` (17.0b) | — | — |

PIVOT uses the same principles; operand materialization is Phase **16**; derived registry consume is Phase **15**; IN-list output aliases are Phase **18**.

---

## Do not

- Add a second rewrite implementation in convert that mirrors walk logic.
- Treat convert `probeArchivedScopeClauseColumns` as the primary UNPIVOT clause resolver.
- Assume every binding can happen at `exitColumn` — respect grammar visit order.
- Skip tier-1 identity at modifier exit because tier-2 will run later.
