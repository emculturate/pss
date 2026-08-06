# DDL structured options parsing — independent work plan

**Status:** ⏸️ Not started (spun off from consolidation Phase **13.5**, Aug 2026)  
**Prerequisite:** Phase **20** DDL walker hygiene ✅ complete (Aug 2026) — new DDL exits must use walked `subMap` / verbatim slices, not ctx scrape.
**Origin:** `symbol-table-resolution-consolidation-worklist.md` §13.5  
**Primary code:** `SqlParseEventWalker` DDL exits, `SQLSelectParser.g4` (`generic_ddl_options`, `generic_ddl_paren_content`, CREATE/ALTER/DROP/TRUNCATE)  
**Primary tests:** `SqlEventWalkerScriptsAndDDLTests`  
**Related but separate:** Phase **20** (walker hygiene — join opaque blobs from walked `subMap`; does **not** structure options)

---

## Why this is its own plan

Consolidation only needs reliable DDL **statement kind**, **object type**, and **qualified object name** for script isolation and access-object routing. Unmodeled tails are already captured as **opaque** `options` / `columns` / `parameters` blobs. Structured parsing of `IF NOT EXISTS`, `OR REPLACE`, etc. is product work (catalog rules, migration tooling, policy engines) — not symbol-table consolidation.

**Until then Phase 20 is complete** — opaque blob *collection* is done; this plan only adds structured option nodes when required.

---

## Current baseline (keep working)

| Capability | Today |
|------------|--------|
| CREATE / ALTER / DROP / TRUNCATE typing | Reliable `create={…}`, `alter={…}`, `drop={…}`, `truncate={…}` |
| Object identity | Qualified names from walked `db_object_name` (Phase 20 partial) |
| Option / paren tails | Opaque strings via `generic_ddl_options` / `generic_ddl_paren_content` |
| Script coverage | `fullScriptPrimaryCoverageTest` / mixed script DDL items |
| Generator re-emit | Opaque blobs re-emitted verbatim (`SQLStatementGenerator`) |

---

## Goals (end state)

1. High-value DDL option clauses parse into **typed AST / mumble keys** (not only opaque text).
2. Unmodeled dialect tails still fall back to `generic_ddl_options` (no silent loss).
3. Walker + goldens + (optional) generator emit structured forms without breaking opaque round-trip for leftover blobs.
4. Phase 20 hygiene remains a prerequisite or parallel constraint: structured nodes must come from **walked children**, not `ctx.getText()` re-scrape.

---

## Non-goals

- Full Snowflake / Postgres DDL dialect coverage in one pass.
- Changing symbol-table convert egress or query-dictionary publish paths.
- Replacing Phase 20 (ctx scrape retirement) — complete or respect Phase 20 patterns first.

---

## Logical execution outline

### D0 — Product trigger + scope lock

1. Record the concrete consumer (catalog metadata? policy? UI edit?).
2. Freeze a **v1 clause list** (suggested starter below).
3. Phase **20** is ✅ complete — new grammar exits must follow walked-`subMap` / verbatim-slice style (**20.1–20.8**).

**Suggested v1 clauses:**

| Clause | Typical statements |
|--------|--------------------|
| `IF NOT EXISTS` / `IF EXISTS` | CREATE / DROP |
| `OR REPLACE` | CREATE VIEW / FUNCTION / … |
| `TEMPORARY` / `TEMP` / `TRANSIENT` (if product cares) | CREATE TABLE |
| Keep-as-blob | Everything else (COPY GRANTS, CLUSTER BY, …) until demanded |

### D1 — Grammar inventory

1. Grep `generic_ddl_options`, `generic_ddl_paren_content`, and CREATE/ALTER/DROP option sites in `SQLSelectParser.g4`.
2. Document which statements already absorb options only as opaque blobs.
3. Design **additive** sub-rules (do not break existing opaque capture for unmodeled tails).

### D2 — Grammar + walker for v1 clauses

1. Add targeted grammar alternatives / optional prefixes for v1 clauses.
2. Implement `exit*` handlers that promote typed keys (e.g. `if_not_exists=true`, `or_replace=true`) onto the DDL AST map.
3. Retain fallback: remaining tokens → opaque `options=` blob.
4. **No** `ctx.getChild(i).getText()` scrape — join or consume walked children only.

### D3 — Tests (structure proof)

| Method (proposed) | Proves |
|-------------------|--------|
| `createTableIfNotExistsParsedOptionsTest` | `IF NOT EXISTS` is a typed node/flag, not only blob text |
| `createViewOrReplaceParsedOptionsTest` | `OR REPLACE` captured structurally |
| `dropTableIfExistsParsedOptionsTest` | `IF EXISTS` on DROP |
| `createTableUnmodeledOptionsRemainOpaqueTest` | Unknown tail still lands in opaque `options` |

Refresh `SqlEventWalkerScriptsAndDDLTests` goldens only where AST shape intentionally changes.

### D4 — Generator / round-trip (optional, after structure)

1. Teach `SQLStatementGenerator` to emit typed flags before opaque blobs.
2. Add round-trip tests for each v1 clause.
3. Confirm opaque-only DDL paths still round-trip.

### D5 — Expand by demand

1. Add one clause family at a time (never “all DDL dialects”).
2. Keep a living inventory table: clause → grammar rule → AST key → tests → generator status.

---

## Relationship to Phase 20

| Track | Responsibility |
|-------|----------------|
| **Phase 20** | Correct *collection path* for today’s opaque blobs (walked `subMap`) |
| **This plan** | *Semantic expansion* of selected option clauses into typed AST |

Phase 20 can finish while this plan stays idle. Starting this plan without Phase 20 hygiene risks re-introducing ctx scrape in new handlers.

---

## Exit criteria

- [ ] v1 clauses structured in AST with dedicated tests
- [ ] Opaque fallback still covers unmodeled tails
- [ ] DDL + script gates green
- [ ] Generator emits typed flags when present (if D4 in scope)
- [ ] Consolidation worklist continues to show **13.5 ❌ spun off** (this doc is source of truth)
