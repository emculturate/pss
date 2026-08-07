# EXTRACT / DATE_PART dialect capture — short workplan

**Status:** ✅ Phases 1–3 + DATE_PART done (Aug 2026)  
**Branch context:** `Spring-2026-Extensions`  
**Grammar:** append new lexer keywords **immediately before `Identifier`** (see comment in `SQLSelectParser.g4`); never insert mid-alphabet (e.g. after `DATE`).  
**Walker:** `SqlParseEventWalker.exitExtract_expression`, `exitDate_part_expression` (+ field normalization)  
**Out of scope:** Delimited-identifier field (`EXTRACT("year" FROM …)`).

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
| **Validation** | Optional later: engine profile → single post-build check on `extract` nodes (see Phase 4); default permissive. |

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

## Phase 4 — Dialect affinity warnings ✅ (part names); profile checks (future)

- **`ExtractDatetimeFieldAffinity`** (`sql/diagnostics`): static sets aligned with grammar branches.
- **Walker:** `emitExtractFieldDialectAffinityWarning` on `exitExtract_expression` / `exitDate_part_expression`; anchor **`ctx.getStart()`** (`EXTRACT` / `DATE_PART` token); codes **`EXTRACT_FIELD_SNOWFLAKE_ONLY`** / **`EXTRACT_FIELD_POSTGRES_ONLY`** (WARNING). **Mixed affinities in one SQL statement** → **`EXTRACT_FIELD_MIXED_DIALECT_AFFINITY`** (FATAL) at the expression that completes the mix; per-statement scope reset in `enterSql_statement`.
- **Not warned:** shared parts (`YEAR`, `QUARTER`, `WEEK`, `TIMEZONE_*`, …). Comma **`EXTRACT(part, source)`** / **`date_part(part, source)`** (`routine_invocation`) use the same affinity + mixed-statement logic in **`exitRoutine_invocation`** (first argument literal or column name).

### Part buckets (grammar-aligned)

| Bucket | Examples | Notes |
|--------|----------|--------|
| **Snowflake-only keywords** (`snowflake_extract_field`) | `DAYOFMONTH`, `DAYOFWEEK`, `DAYOFWEEKISO`, `DAYOFYEAR`, `WEEKISO`, `WEEKOFYEAR`, `EPOCH_SECOND`, `EPOCH_MILLISECOND`, `EPOCH_MICROSECOND` | Also match string literals with the same name (case-insensitive). Generic `identifier` fields are not classified. |
| **Postgres extended** (`extended_datetime_field`, minus overlaps) | `DOW`, `DOY`, `ISODOW`, `ISOYEAR`, `MICROSECONDS`, `MILLENNIUM`, `DECADE`, `CENTURY`, `EPOCH`, `MILLISECONDS` | `QUARTER` / `WEEK` omitted (both engines). Postgres `EPOCH` ≠ Snowflake `EPOCH_*`. |
| **Shared / standard** | `YEAR`, `MONTH`, `DAY`, `HOUR`, `MINUTE`, `SECOND`, `TIMEZONE`, `TIMEZONE_HOUR`, `TIMEZONE_MINUTE` | No affinity warning. |

### Optional later (engine profile)

When caller sets target engine `SNOWFLAKE` or `POSTGRES`, add **profile mismatch** warnings (e.g. Postgres profile + `EPOCH_SECOND`) — same hook, separate codes or message suffix. Default remains permissive union grammar + informational affinity warnings above.

- SQL regeneration for `extract` / `DATE_PART` → [sql-statement-generator-completion-workplan.md](sql-statement-generator-completion-workplan.md) §2.7.

### Dialect profile diagnostics (deferred — source-type rules)

**Intent (not “stop Snowflake and Postgres from mixing in one grammar”):** The grammar deliberately accepts the **union** of Snowflake and Postgres part names so one parse tree works everywhere. Profile diagnostics are for when the **caller declares a target engine** (Snowflake vs Postgres) and wants to know “this `EXTRACT`/`DATE_PART` shape is unlikely to run there,” without rejecting SQL at parse time.

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
