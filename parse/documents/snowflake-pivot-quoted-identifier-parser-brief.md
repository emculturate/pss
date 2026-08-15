# Snowflake PIVOT quoted unaliased identifiers — parser support brief

## Goal

Teach the PSS SQL parser (and PIVOT column resolution) to accept the older Snowflake PIVOT output-column naming form, in addition to the newer `IN (... AS alias)` form that already works.

This is a warehouse SQL issue, not a DBT requirement. DBT only ships the compiled SQL. The quoting is a Snowflake PIVOT identifier quirk.

## The construction

Snowflake `PIVOT(... FOR <col> IN ('a', 'b', ...))` turns the IN-list **values** into **output column names**. For string pivot values, Snowflake bakes the single quotes into the identifier. The resulting column is not `diq_entry_year`; it is the delimited identifier whose name is `'diq_entry_year'` (quotes included).

The only legal way to refer to that column is a double-quoted identifier:

```sql
"'diq_entry_year'"
```

That means: column whose name is `'diq_entry_year'`, including the leading and trailing single quotes.

A normal `'diq_entry_year'` in the SELECT list would be a **string literal**, not a column reference, so the pivot results would never be selected.

These two appearances are doing different jobs:

- `WHERE question_category IN ('diq_entry_year', ...)` and `PIVOT(... IN ('diq_entry_year', ...))` use ordinary **string values**.
- `SELECT "'diq_entry_year'" AS entry_year` is a **delimited identifier** referring to the column Snowflake created from that value.

## Newer form (already supported) vs older form (needed)

Newer Snowflake style aliases inside the IN list, so the awkward identifiers never appear in the SELECT list:

```sql
PIVOT(MAX(form_response) FOR question_category IN (
  'diq_entry_year' AS entry_year,
  'diq_entry_term' AS entry_term,
  'Survey Question' AS form_response
))
```

Older style (common in existing DBT models): pivot first, then rename the quote-including identifiers in the outer SELECT:

```sql
SELECT contact_initiated_activity_key
           , "'diq_entry_year'" AS entry_year
           , "'diq_entry_term'" AS entry_term
           , "'Survey Question'" AS form_response
           , survey_question_year_term_rank
      FROM src
      PIVOT(MAX(form_response) FOR question_category IN ('diq_entry_year', 'diq_entry_term', 'Survey Question'))
      WHERE entry_year IS NOT NULL AND entry_term IS NOT NULL AND form_response is not null
      ORDER BY contact_initiated_activity_key
```

Support both. Do not “fix” this by requiring authors to migrate to the newer IN-list aliases.

## Minimal reproducing query

Use this exact text as the primary fixture (SQL endpoint, parser 5.1.3, and also 5.1.2 if the grammar change is shared):

```sql
SELECT contact_initiated_activity_key
           , "'diq_entry_year'" AS entry_year
           , "'diq_entry_term'" AS entry_term
           , "'Survey Question'" AS form_response
           , survey_question_year_term_rank
      FROM src
      PIVOT(MAX(form_response) FOR question_category IN ('diq_entry_year', 'diq_entry_term', 'Survey Question'))
      WHERE entry_year IS NOT NULL AND entry_term IS NOT NULL AND form_response is not null
      ORDER BY contact_initiated_activity_key
```

## Current parser failure (observed)

The lexer/parser does **not** treat `"'diq_entry_year'"` as one delimited-identifier token. It splits it into `"` + `'diq_entry_year'` + `"`.

From RMCP `reports/dbt_sql_scan/rmcp-dbt-sql-scan-latest.md` on
`project_simulation/src/models/analytics/pdp_alr_v2/models/maestro/v_alrinsights_potentialstudentbyentrytermsurveyresponsehistory.sql`:

- FATAL `REPORT_ERROR` @22:13 near `"` (`NoViableAltException`): unexpected input: `"`
- FATAL `REPORT_ERROR` @22:14 near `'diq_entry_year'` (`NoViableAltException`): unexpected input: `'diq_entry_year'`
- Recovery warnings at the opening `"`, the string `'diq_entry_year'`, and the closing `"`
- Follow-on `APPLICATION_ISSUE_FATAL` while walking: `Cannot invoke "java.util.Map.remove(Object)" because "subMap" is null`

Treat the walker NPE as a consequence of failed recovery unless it still happens after the construct parses cleanly. Root fix is recognizing the delimited identifier.

## Expected parse / semantic behavior

1. **Lexer:** `"'diq_entry_year'"`, `"'diq_entry_term'"`, and `"'Survey Question'"` are each a single delimited identifier. Identifier text includes the inner single quotes. Do not tokenize the inner `'...'` as a string literal.
2. **Grammar:** A delimited identifier is a valid SELECT-list item (and generally a column reference), including when its body contains `'`, spaces (`"'Survey Question'"`), etc.
3. **PIVOT resolution:** For `PIVOT(MAX(form_response) FOR question_category IN ('diq_entry_year', 'diq_entry_term', 'Survey Question'))` without IN-list aliases, the pivot output columns are named `'diq_entry_year'`, `'diq_entry_term'`, and `'Survey Question'` (quotes part of the name). Grouping columns (`contact_initiated_activity_key`, `survey_question_year_term_rank`) pass through unchanged.
4. **SELECT aliases:** `"'diq_entry_year'" AS entry_year` binds that pivot column to `entry_year`. After that, `WHERE entry_year IS NOT NULL` and `ORDER BY contact_initiated_activity_key` should resolve normally.
5. **Do not confuse with literals:** `'diq_entry_year'` in the PIVOT IN list is a value that *creates* the column; `"'diq_entry_year'"` in SELECT is a reference to that column.
6. **Newer form must keep working:** `IN ('diq_entry_year' AS entry_year, ...)` should still produce columns named `entry_year` etc., with no quote-including identifiers required.

## Derivation, location references, and column resolution

Confirm and adjust logic that recognizes the quoted, **unaliased** values in the PIVOT `IN` list as source columns in the `derivation.source_columns` entry, then treat them as normal references for location references and column resolution logic.

Concretely, for the reproducing query:

- Each unaliased IN-list value (`'diq_entry_year'`, `'diq_entry_term'`, `'Survey Question'`) must appear as a source column in `derivation.source_columns` for the corresponding pivot output column.
- After that registration, `"'diq_entry_year'"` (and the other delimited identifiers) must be treated as ordinary column references: same location-reference and column-resolution paths used for normal identifiers, not a special literal or recovery artifact.
- Aliasing in SELECT (`AS entry_year`) is a normal rename of that resolved column; it must not hide or replace the `source_columns` provenance from the unaliased IN-list value.
- The newer aliased IN-list form remains a rename at pivot-output time; do not regress `derivation.source_columns` for that form.

## Real-world sources (RMCP simulation tree)

- `project_simulation/src/models/analytics/pdp_alr_v2/models/maestro/v_alrinsights_potentialstudentbyentrytermsurveyresponsehistory.sql`
- `project_simulation/src/models/analytics/pdp_alr_v2/models/maestro/v_alrinsights_potentialstudentbyentrytermnosurvey.sql` (same `"'diq_entry_year'"` / `"'diq_entry_term'"` pattern)

## Suggested implementation approach

1. Read PSS parse-consumer docs before changing PIVOT resolution (`relational-modifier-resolution-policy`, plus identifier/lexer rules). Align with `versionTag` 5.1.3 (and 5.1.2 if the grammar is shared).
2. Fix tokenization of double-quoted identifiers whose body contains single quotes (and spaces). Verify whether `"`-delimited identifiers are missing entirely from the SELECT-item path, or only fail when `'` appears inside.
3. Teach PIVOT output-column naming for unaliased string IN-list values to match Snowflake: name = the literal including quotes.
4. Confirm `derivation.source_columns` records those unaliased IN-list values as the source columns of the pivot outputs, then run the normal location-reference and column-resolution logic against the resulting identifiers.
5. Add fixtures/tests for:
   - the exact query above (must parse with no FATAL / no recovery)
   - `derivation.source_columns` for each unaliased IN-list value
   - location references / column resolution treating `"'diq_entry_year'"` as a normal column ref
   - the newer `IN ('x' AS alias)` form (no regression)
   - mixed: some IN values aliased, some not
   - space-containing value: `"'Survey Question'"`
   - SELECT of the delimited identifier without AS alias
   - WHERE/IN using the same strings as ordinary literals (must remain literals)
6. After a clean parse, confirm symbol table / interface / lineage: `entry_year`, `entry_term`, `form_response` are columns sourced from the pivot of `form_response` over `question_category`.
7. Re-scan the two maestro models above and confirm they leave `FATAL_OR_UNRECOVERED` for this reason.

## Out of scope

- Rewriting the DBT models to the newer IN-list alias form.
- Treating this as a DBT Jinja / `{{ ref() }}` problem.
- Unrelated scan fatals (e.g. `%` in other models).
