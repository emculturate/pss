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
| **Walker** | `part` = field text as written (keyword/identifier token spelling, or string literal body without SQL quotes); `part_form` = `KEYWORD` \| `STRING`. |
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
- `exitExtract_expression`: build `extract={part, part_form, source[, source_type]}`; typed `DATE`/`TIME`/`TIMESTAMP` literals promote `source_type` on the extract node and `source={literal=…}`.
- `exitExtract_source`: `handleOneChild` so `source` is the lifted value subtree (not `{1=…, Type=…}`).
- `exitExtract_field`: `handleOneChild` for part tokens / string literal.
- `exitDate_literal` / `exitTime_literal` / `exitTimestamp_literal`: `{literal, source_type}` before `unsigned_literal` merge.
- `attachExtractSource`: promote `source_type` onto `extract` (no post-hoc `Type` stripping — rely on dedicated exits / `handleOneChild`).

### AST contract (generator / round-trip)

`extract={part, part_form, source[, source_type]}`

| Key | Meaning |
|-----|---------|
| `part` | Field name as authored (lexer token text or unquoted string literal); no case folding |
| `part_form` | `KEYWORD` or `STRING` |
| `source_type` | Present for typed SQL literals: `MUMBLE_EXTRACT_SOURCE_TYPE_*` (`DATE`, `TIME`, `TIMESTAMP`, `INTERVAL`) |
| `source` | Semantic subtree only — e.g. `{column=…}`, `{literal=…}`, `{parentheses=…}`, `{calc=…}`, `{function=…}`, nested `{extract=…}` |

No grammar rule index keys (`Type=NNN`) anywhere under `extract` or `source`.

---

## Phase 3 — Exemplar tests ✅ (expanded)

Class: `SqlEventWalkerExtractTests.java` — **56** cases (25 full `SELECT`, 25 `predicand_value`, 6 predicand-substitution exemplars). Each test: `assertNoFatalErrors`, `assertNoWalkerDiagnostics`, golden `getAsTree()`, and **rejects** any `Type=\d+` in the AST string. Substitution exemplars also assert substitution map and symbol table goldens.

| Area | Examples covered |
|------|------------------|
| Field keyword / string | `YEAR`, `'month'`, `'dow'` |
| Postgres extended | `DOW`, `CENTURY`, `MICROSECONDS` |
| Snowflake parts | `DAYOFWEEK`, `WEEKISO`, `EPOCH_SECOND`, `EPOCH_MICROSECOND` |
| Timezone parts | `TIMEZONE`, `TIMEZONE_HOUR` |
| Typed literals | `DATE '…'`, `TIMESTAMP '…'`, `TIME '…'` → `source_type` + `source.literal` |
| Column / qual / paren | `d`, `o.d`, `(d)` |
| Expression source | `d + 1` (`calc`), `CAST(… AS TIMESTAMP)`, `'…'::timestamp`, nested `EXTRACT` |

One test also asserts symbol table goldens (`extractKeywordYearFromColumn`).

**Gate:**

```bash
cd parse && mvn -q test -Dtest=SqlEventWalkerExtractTests
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
