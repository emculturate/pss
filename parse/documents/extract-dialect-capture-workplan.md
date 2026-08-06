# EXTRACT dialect capture — short workplan

**Status:** ✅ Phases 1–3 done (Aug 2026)  
**Branch context:** `Spring-2026-Extensions`  
**Grammar:** `SQLSelectParser.g4` — `extract_expression`, `extract_field`, `extract_source`  
**Walker:** `SqlParseEventWalker.exitExtract_expression` (+ field normalization)  
**Out of scope:** Delimited-identifier field (`EXTRACT("year" FROM …)`).

---

## Goals

1. Parse **Snowflake** and **Postgres** `EXTRACT` surface forms in one grammar (union of parts + string-literal fields).
2. Widen **`extract_source`** to real datetime **value expressions** (column, literals, functions, parentheses, arithmetic).
3. Emit a **stable walker node** (`extract={part, part_form, source}`) for generator work (Phase 2.7).
4. **Exemplar tests** — representative pairings, not exhaustive part lists.

---

## Dialect policy (no per-dialect parse trees)

| Layer | Approach |
|-------|----------|
| **Grammar** | Single `extract_field` = keyword union (standard + `extended_datetime_field` + `snowflake_extract_field`) **or** `character_literal`. Snowflake part lexer rules are placed **before** `Identifier`. |
| **Walker** | Canonical `part` (normalized name); `part_form` = `KEYWORD` \| `STRING`. |
| **Validation** | Optional later: engine profile warns on unsupported part for type (not in this batch). |

**Postgres exemplar parts:** `DOW`, `MILLENNIUM`, string `'year'`.  
**Snowflake exemplar parts:** `DAYOFWEEK`, `epoch_second`, string `'month'`.  
**Shared:** `YEAR`, `TIMEZONE_HOUR`.

---

## Phase 1 — Grammar ✅

- `extract_source` → `value_expression`.
- `extract_field` → add `character_literal`; `snowflake_extract_field` keyword rules + matching lexer tokens **immediately before** `Identifier` (shifts token type IDs ≥ former `Identifier`; update test goldens via replacement matrix).
- Keep existing `extended_datetime_field` for Postgres (`DOW`, `DOY`, `MICROSECONDS`, …).

Regenerate parser (`mvn generate-sources` / normal `parse` build).

---

## Phase 2 — Walker ✅

- `MUMBLE_EXTRACT_KEY` and related keys in `MumbleConstants`.
- `exitExtract_expression`: build `extract={part, part_form, source}` from labeled children.
- `exitExtract_source`: `handleOneChild` so `source` is the lifted value subtree (not `{1=…, Type=…}`).
- `exitExtract_field` / `exitCharacter_literal` path: normalize string literals (strip quotes, uppercase `part`).

---

## Phase 3 — Exemplar tests ✅

Class: `SqlEventWalkerExtractTests.java` — each test: `assertNoFatalErrors`, `assertNoWalkerDiagnostics`, golden `getAsTree()` (and symbol table where column refs matter).

| # | Dialect hint | SQL sketch | Exercises |
|---|--------------|------------|-----------|
| 1 | shared | `EXTRACT(YEAR FROM d)` | keyword field + column source |
| 2 | both | `EXTRACT('month' FROM d)` | string field + column |
| 3 | Postgres | `EXTRACT(DOW FROM d)` | PG extended keyword |
| 4 | Snowflake | `EXTRACT(DAYOFWEEK FROM d)` | SF-only keyword |
| 5 | Snowflake | `EXTRACT(EPOCH_SECOND FROM ts)` | SF epoch part (keyword; lowercase `epoch_second` lexes as `Identifier` unless quoted) |
| 6 | shared | `EXTRACT(YEAR FROM DATE '2020-01-01')` | datetime literal source |
| 7 | both | `EXTRACT(YEAR FROM (order_date))` | parenthesized `value_expression` source *(interval arithmetic is a separate grammar gap)* |
| 8 | both | `EXTRACT(HOUR FROM CAST(order_date AS TIMESTAMP))` | cast / function-shaped source |

**Gate:**

```bash
cd parse && mvn -Psmoketest-quality-gate test
```

---

## Phase 4 — Follow-ons (not this batch)

- `DATE_PART` / `date_part` as shared field + source productions (comma and `FROM` forms).
- Dialect profile diagnostics (invalid part for `DATE` vs `TIMESTAMP`).
- `SQLStatementGenerator` emit for `extract` node (workplan §2.7).

---

## References

- Snowflake: [EXTRACT](https://docs.snowflake.com/en/sql-reference/functions/extract) — quoted or unquoted `date_or_time_part`.
- Postgres: [§9.9.1 EXTRACT](https://www.postgresql.org/docs/current/functions-datetime.html) — field as identifier **or** string.
