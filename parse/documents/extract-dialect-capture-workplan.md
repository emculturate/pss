# EXTRACT / DATE_PART dialect capture — short workplan

**Status:** ✅ Phases 1–3 + DATE_PART + dialect grammar diagnostics (Phase 4) done (Aug 2026)  
**Branch context:** `Spring-2026-Extensions`  
**Grammar:** append new lexer keywords **immediately before `Identifier`** (see comment in `SQLSelectParser.g4`); never insert mid-alphabet (e.g. after `DATE`).  
**Walker:** `SqlParseEventWalker.exitExtract_expression`, `exitDate_part_expression` (+ field normalization)  

**DATE_PART / EXTRACT policy:** Only **`name(field FROM source)`** uses the shared **`extract`** AST (`invocation=DATE_PART` for date-part names; `EXTRACT` omits `invocation`). The **`FROM`** between field and source is the discriminator. Comma forms **`name('year', source)`** parse as **`routine_invocation`** / `function` AST. Lexer names **`EXTRACT`** and **`DATE_PART`** are **case-insensitive** (`extract`, `date_part`, etc.); comma calls preserve author spelling on `function_name`.

---

## Goals

1. Parse **Snowflake** and **Postgres** `EXTRACT` surface forms in one grammar (union of parts + string-literal fields).
2. Parse **`DATE_PART`** **`(field FROM source)`** with the same **`extract_field`** / **`extract_source`** as `EXTRACT` → shared **`extract`** AST (`invocation=DATE_PART`). Comma **`DATE_PART` / `date_part('year', source)`** stays **`routine_invocation`** (not this goal).
3. Widen **`extract_source`** to real datetime **value expressions** (column, literals, functions, parentheses, arithmetic).
4. Emit a **stable walker node** (`extract={part, part_form, source[, source_type][, invocation]}`).
5. **Exemplar tests** — representative pairings, not exhaustive part lists (`SqlEventWalkerExtractTests`).

---

## Dialect policy (no per-dialect parse trees)

| Layer | Approach |
|-------|----------|
| **Grammar** | Single `extract_field` = keyword union (standard + `extended_datetime_field` + `snowflake_extract_field`) **or** `character_literal`. Snowflake part lexer rules are placed **before** `Identifier`. `date_part_expression` is **`DATE_PART ( extract_field FROM extract_source )` only** (comma calls use `routine_invocation`). |
| **Walker** | `part` = field text as written; `part_form` = `KEYWORD` \| `STRING`. `invocation=DATE_PART` only for `DATE_PART(...)`; `EXTRACT(...)` omits `invocation`. |
| **Validation** | Per-statement dialect grammar hits (extract parts + labeled Snowflake/Postgres rules); optional engine profile later (deferred). |

**Postgres exemplar parts:** `DOW`, `MILLENNIUM`, string `'year'`.  
**Snowflake exemplar parts:** `DAYOFWEEK`, `epoch_second`, string `'month'`.  
**Shared:** `YEAR`, `TIMEZONE_HOUR`.

---

## Phase 1 — Grammar ✅

- `extract_source` → `value_expression`.
- `extract_field` → add `character_literal`; `snowflake_extract_field` keyword rules + matching lexer tokens **immediately before** `Identifier` (shifts token type IDs ≥ former `Identifier`; update test goldens via replacement matrix).
- `date_part_expression` → `DATE_PART ( extract_field FROM extract_source )` only (`DATE_PART` lexer matches `date_part` / `DATE_PART` case-insensitively). Comma form is **not** this rule — it parses as `routine_invocation`.
- Keep existing `extended_datetime_field` for Postgres (`DOW`, `DOY`, `MICROSECONDS`, …).

Regenerate parser (`mvn generate-sources` / normal `parse` build).

---

## Phase 2 — Walker ✅

- `MUMBLE_EXTRACT_*` keys in `MumbleConstants` (including `invocation` / `DATE_PART`).
- `exitExtract_expression` / `exitDate_part_expression`: shared `buildExtractMumbleItem`; typed literals promote `source_type`; predicand substitution stamping on sources.
- `exitExtract_source` / `exitExtract_field`: `handleOneChild` for lifted subtrees.

### AST contract

`extract={part, part_form, source[, source_type][, invocation]}`

| Key | Meaning |
|-----|---------|
| `part` | Field name as authored; no case folding |
| `part_form` | `KEYWORD` or `STRING` |
| `source_type` | `MUMBLE_EXTRACT_SOURCE_TYPE_*` for typed SQL literals |
| `invocation` | `DATE_PART` when parsed from `date_part_expression` (`… FROM …`); absent for `EXTRACT` |
| `source` | Semantic subtree only |

No grammar rule index keys (`Type=NNN`) under `extract` or `source`.

---

## Phase 3 — Exemplar tests ✅

Class: `SqlEventWalkerExtractTests.java` — EXTRACT + DATE_PART cases (SELECT, `predicand_value`, substitution). Gate:

```bash
cd parse && mvn -q test -Dtest=SqlEventWalkerExtractTests
```

---

## Phase 4 — Statement dialect grammar diagnostics ✅

Unified **per-statement** Snowflake vs PostgreSQL grammar linting (not separate parse trees). Implemented in `SqlParseEventWalker` + `sql/grammar/SqlGrammarDialectRuleRegistry.java` + `sql/diagnostics/ExtractDatetimeFieldAffinity.java`.

### Walker contract

| Mechanism | Behavior |
|-----------|----------|
| **`recordStatementDialectGrammarHit(dialect, line, charPos, constructLabel)`** | Increments **`snowflakeDialectGrammarCount`** or **`postgresDialectGrammarCount`** on the event walker. **First** hit of each dialect per statement → one **WARNING**; further hits of the same dialect only increment the counter. If the **other** dialect count is already **> 0** when a new hit arrives → **`STATEMENT_MIXED_DIALECT_GRAMMAR`** **FATAL** at that site (no second dialect warning). |
| **Reset** | Counters and warning/fatal flags cleared in **`enterSql_statement`** (each script statement isolated). |
| **Grammar rules** | On **`exitEveryRule`**, registered Snowflake/Postgres alternatives (TRUNCATE/DELETE variants, `qualify_clause`, `pivot_clause`, `unpivot_clause`, `on_conflict_clause`, `delete_returning`, Snowflake identifiers, nested `with_query` under `cte_body`, …) call the same recorder with the rule label. |
| **EXTRACT / DATE_PART** | **FROM** forms (`exitExtract_expression`, `exitDate_part_expression`) and **comma** forms (`exitRoutine_invocation` when name is `EXTRACT` / `DATE_PART`) pass **`constructLabel`** like `EXTRACT/DATE_PART option 'epoch_second'` into the same recorder (part sets in `ExtractDatetimeFieldAffinity`). |

### Diagnostic codes (walker → snippet)

| Code | Severity | When |
|------|----------|------|
| **`STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR`** | WARNING | First Snowflake-specific construct in the statement |
| **`STATEMENT_POSTGRES_DIALECT_GRAMMAR`** | WARNING | First PostgreSQL-specific construct in the statement |
| **`STATEMENT_MIXED_DIALECT_GRAMMAR`** | FATAL | Snowflake and PostgreSQL constructs both present in one statement |

Messages include **`constructLabel`** (rule name or extract part text) and **`(l:… c:…)`** from the triggering token.

Diagnostics are always attached to the extractor/snippet; tests may **ignore** dialect WARNING codes via `assertNoWalkerDiagnostics(extractor, ignoredCodes)` (default ignores the two WARNING codes only — **not** mixed FATAL).

### Part buckets (extract / date_part only)

| Bucket | Examples | Notes |
|--------|----------|--------|
| **Snowflake-only keywords** (`snowflake_extract_field`) | `DAYOFMONTH`, `DAYOFWEEK`, `DAYOFWEEKISO`, `DAYOFYEAR`, `WEEKISO`, `WEEKOFYEAR`, `EPOCH_SECOND`, `EPOCH_MILLISECOND`, `EPOCH_MICROSECOND` | Also string literals / comma-arg names with the same spelling (case-insensitive). |
| **Postgres extended** (`extended_datetime_field`, minus overlaps) | `DOW`, `DOY`, `ISODOW`, `ISOYEAR`, `MICROSECONDS`, `MILLENNIUM`, `DECADE`, `CENTURY`, `EPOCH`, `MILLISECONDS` | `QUARTER` / `WEEK` omitted (both engines). Postgres `EPOCH` ≠ Snowflake `EPOCH_*`. |
| **Shared / standard** | `YEAR`, `MONTH`, `DAY`, `HOUR`, `MINUTE`, `SECOND`, `TIMEZONE`, `TIMEZONE_HOUR`, `TIMEZONE_MINUTE` | No dialect hit from part name alone. |

**Not counted via grammar rule registry (avoid double count):** `snowflake_extract_field` / `extended_datetime_field` rule exits — only the extract/date_part hooks above.

### Tests

- `SqlEventWalkerExtractTests` — extract/date_part positions, comma forms, mixed extract fatals.
- `SqlEventWalkerCoreSelectFromAliasingTests` — QUALIFY + EXTRACT mix, script counter reset.
- DDL TRUNCATE, pivot/unpivot mixed UNPIVOT+RETURNING — explicit `assertDiagnosticAtPosition` / fatal assertions.

### Optional later (engine profile + source-type rules)

When caller sets target engine `SNOWFLAKE` or `POSTGRES`, add **profile mismatch** warnings on top of the same `recordStatementDialectGrammarHit` hook (e.g. “declared Postgres but used `qualify_clause`”). Default remains permissive union grammar + informational warnings/fatal above.

- SQL regeneration for `extract` / `DATE_PART` → [sql-statement-generator-completion-workplan.md](sql-statement-generator-completion-workplan.md) §2.7.

### Dialect profile diagnostics (deferred — source-type rules)

**Intent:** The grammar accepts the **union** of Snowflake and Postgres surface forms. Optional **engine profile** and **part vs `source_type`** checks are separate from today’s per-statement dialect counters.

That is related to, but not the same as, **part vs source type** rules (e.g. Snowflake docs: some parts only apply to `TIMESTAMP`, not `DATE`). Those are engine reference tables, not something to encode as dozens of walker branches.

**Simple approach (one choke point, no sprawl):**

1. **Profile is optional** on parse/walk (`PERMISSIVE` default = no extract checks). Only when profile is `SNOWFLAKE` or `POSTGRES` run validation.
2. **Single hook** after `buildExtractMumbleItem` (or one post-walk visitor over `extract` nodes only). Do **not** scatter checks across `exitExtract_field`, literals, etc.
3. **One static lookup** per profile, e.g. `Map<String, Set<SourceTypeBucket>>` where `SourceTypeBucket` is coarse: `DATE`, `TIME`, `TIMESTAMP`, `INTERVAL`, `UNKNOWN` (column/expression with no `source_type`). Part keys normalized for lookup (e.g. uppercase keyword text; string literal parts optionally skipped or looked up as-is).
4. **One diagnostic code** + message template, e.g. `EXTRACT_PART_NOT_VALID_FOR_PROFILE` / `EXTRACT_PART_UNLIKELY_FOR_SOURCE_TYPE`, severity **WARNING** (or INFO). Avoid new FATALs unless a future strict gate explicitly opts in.
5. **Scope limits:** Only nodes under **`extract={…}`** from `extract_expression` / `date_part_expression`. Do **not** validate comma `date_part(...)` `function` calls unless we add a separate, equally small hook on `routine_invocation` later.
6. **Maintenance:** Small allowlists in one file (Snowflake-only parts, Postgres-only parts, and optionally “part allowed only for TIMESTAMP” rows). Extend by editing that table, not new walker code paths.

**Explicit non-goals:** Proving runtime correctness of every part on every expression shape; validating nested `source` beyond `source_type` when present; duplicating full Snowflake/Postgres manuals in the walker.

---

## References

- Snowflake: [EXTRACT](https://docs.snowflake.com/en/sql-reference/functions/extract), [DATE_PART](https://docs.snowflake.com/en/sql-reference/functions/date_part).
- Postgres: [§9.9.1 EXTRACT / date_part](https://www.postgresql.org/docs/current/functions-datetime.html).
