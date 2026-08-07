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
2. Parse **`DATE_PART`** with the same **field** and **source** productions (comma and `FROM` forms).
3. Widen **`extract_source`** to real datetime **value expressions** (column, literals, functions, parentheses, arithmetic).
4. Emit a **stable walker node** (`extract={part, part_form, source[, source_type][, invocation]}`).
5. **Exemplar tests** — representative pairings, not exhaustive part lists (`SqlEventWalkerExtractTests`).

---

## Dialect policy (no per-dialect parse trees)

| Layer | Approach |
|-------|----------|
| **Grammar** | Single `extract_field` = keyword union (standard + `extended_datetime_field` + `snowflake_extract_field`) **or** `character_literal`. Snowflake part lexer rules are placed **before** `Identifier`. `DATE_PART` is a dedicated keyword + `date_part_expression` rule. |
| **Walker** | `part` = field text as written; `part_form` = `KEYWORD` \| `STRING`. `invocation=DATE_PART` only for `DATE_PART(...)`; `EXTRACT(...)` omits `invocation`. |
| **Validation** | Optional later: engine profile warns on unsupported part for type (not in this batch). |

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

## Phase 4 — Follow-ons (not this batch)

- Dialect profile diagnostics (invalid part for `DATE` vs `TIMESTAMP`).
- SQL regeneration for `extract` / `DATE_PART` → [sql-statement-generator-completion-workplan.md](sql-statement-generator-completion-workplan.md) §2.7.

---

## References

- Snowflake: [EXTRACT](https://docs.snowflake.com/en/sql-reference/functions/extract), [DATE_PART](https://docs.snowflake.com/en/sql-reference/functions/date_part).
- Postgres: [§9.9.1 EXTRACT / date_part](https://www.postgresql.org/docs/current/functions-datetime.html).
