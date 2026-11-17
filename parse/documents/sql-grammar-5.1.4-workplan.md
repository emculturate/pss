# SQL grammar and walker — 5.1.4 master workplan

**API / docs version target:** `5.1.4` (coordinate with parent `pss-parent` / `pss-parse` artifact when shipping)  
**Branch context:** `Fall-2026-Extensions` (Spring-2026-Extensions closed with 5.1.3; open Spring items roll over here)  
**Primary code:** `parse/src/main/antlr4/sql/SQLSelectParser.g4`, `SqlParseEventWalker.java`, `SqlParseSymbolTreeHelper.java`, `generators/SQLStatementGenerator.java`  
**Status:** Active — open entries below  
**Audience:** PSS parser maintainers, Panto / Query Tool consumers

**This file is the single planning source** for open parser/grammar/walker/generator/hygiene work on the 5.1.4 line. Older standalone workplans in `parse/documents/` are **retired** (stub headers only); detail for completed programs lives in git history or RMCP handoff docs where noted.

**Grammar changelog reference:** [sql-grammar-extensions-since-2026-01-01.md](./sql-grammar-extensions-since-2026-01-01.md)  
**Lexer golden policy:** `.cursor/rules/lexer-token-id-golden-policy.mdc`

---

## How to add an entry

1. Add a row to the progress tracker.
2. Add `## Entry N — …` with goals, phases, acceptance, out of scope, and links to **briefs** (keep long specs in sibling `*.md` files, not duplicate megabytes here).
3. Retire any ad-hoc plan by replacing it with a stub pointing to the entry number.

---

## Progress tracker (open work)

| Entry | Feature | Kind | Status |
|-------|---------|------|--------|
| **1** | Target-dialect date/time manipulation + Snowflake/Postgres dependency affinity | Enhancement | **In progress** — Slices A-D complete with explicit matrix traceability; Slice E deferred until after 5.1.4 release; Slice F complete except explicitly deferred generator work (Entry 10) |
| **2** | Snowflake PIVOT quoted unaliased IN-list identifiers | Enhancement | **Complete** (closed 2026-11-17) — Stage A/B/C delivered and live-query fixture closeout re-scan verified |
| **3** | Snowflake `PARSE_*` + `:` semi-structured field access | Enhancement | **Complete** (closed 2026-11-17) — Stage A/B/C/D delivered with cohort regression bundle green |
| **4** | Snowflake `ARRAY` syntax and functions (5.1–5.12) | Enhancement | **Partial** — baseline subscript/`ARRAY_AGG`/`FLATTEN` support exists; remaining array-function backlog open |
| **5** | Simple Jinja / dbt substitutions (6.1–6.4) | Enhancement | **Not started** |
| **6** | Deep Jinja / SCRIPT prefix *(optional)* (7.x) | Enhancement | **Not started** |
| **7** | Panto variable types × parser endpoints — test backlog (8.1–8.2) | Test / docs | **Not started** |
| **8** | Panto cardinality & grammar-safe dynamic AST expansion (9.x) | Enhancement (exploratory) | **Not started** |
| **9** | DDL structured options (`IF NOT EXISTS`, `OR REPLACE`, …) | Enhancement | **Not started** |
| **10** | SQL statement generator — post-milestone completion | Enhancement | **Not started** (milestone ✅) |
| **11** | Walker `exit*` JaCoCo coverage — Tier 2 / 3 residuals | Test / hygiene | **Optional** (Tier 1 ✅) |
| **12** | Helper dead-code hygiene — Phases C–D | Hygiene | **Complete** (closed 2026-11-16) |
| **13** | SELECT-list subquery alias scope (false `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE`) + operand `lookup` wrapper | Defect | **Complete** (2026-11-10) |
| **14** | Join extension present → `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE` reported as severe warning + `JOIN_EXTENSION_REQUIRED` (whole-query) | Defect (release blocker) | **Complete** (2026-11-10) |
| **15** | Scope-aware join extension deferral (per-scope diagnostic capture) | Enhancement | **Not started** (follows **14**) |
| **16** | Set operation with a query variable member loses its output interface (false `QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS`) | Defect (release blocker) | **Complete** (2026-11-11) |
| **17** | `DATEADD` / `DATEDIFF` date parts resolved as columns | Defect | **Merged into Entry 1** (2026-11-11) |
| **18** | Snowflake non-reserved words rejected (`position`, `input`, `path`, `truncate()`), array literals, and walker crash after syntax errors | Defect | **Closed** (2026-11-11) — ten bound-query regressions fixed; unused s82233 discrepancy obsolete and immaterial, accepted without remediation |
| **19** | RMCP Phase 2 acceptance-test closure (`VALUES` joined UNION branch; ordered aggregates) | Test / verification | **Complete** (closed 2026-11-16) |
| **20** | Qualified column substitutions on visible query aliases rejected as missing interface columns | Defect (5.0.0 parity blocker) | **Complete** (closed 2026-11-13) — targeted fix + warning policy + parity re-evaluation delivered; see Entry 20 section |

**5.0.0 → 5.1.4 parity gate update:** Entry **20** is closed (2026-11-13) and
no longer blocks parity. Remaining parity/release checks continue to be the
corpus, consumer, generator, and release verification gates documented in this
workplan.

---

## Completed baselines (no open entry — do not reopen)

| Program | Shipped / closed | Where detail lives |
|---------|------------------|-------------------|
| **EXTRACT / DATE_PART `FROM`** | ✅ Aug 2026 | `SqlEventWalkerExtractTests`; git: `extract-dialect-capture-workplan.md` |
| **Symbol table resolution consolidation** Phases 1–20 | ✅ Aug 2026 | [symbol-table-resolution-consolidation-worklist.md](./symbol-table-resolution-consolidation-worklist.md) (historical) |
| **RMCP parser-defects Phase 2 implementation** (set-op, `VALUES`, `CASE`, parse perf 2.8–2.11, etc.) | ✅ 2026-08–09; residual acceptance matrix → **Entry 19** | [parse/docs/rmcp-handoff/5.1.3-panto-outstanding/](./docs/rmcp-handoff/5.1.3-panto-outstanding/); set-op egress: `.cursor/rules/set-op-convert-egress-scoping.mdc` |
| **Java 21 / parent 5.1.4-1 toolchain** | ✅ Sep 2026 | git: `java-21-upgrade-workplan.md` |
| **2.5 Jinja DNC rollup closure** | ⏳ | **Entry 5** subtask **5.4** (depends on **5.1**) |

---

## Entry 1 — Target-dialect date/time manipulation and dependency affinity

### Product story (authoritative)

Update and release the parser jar to recognize and construct proper AST and symbol table entries for the **DATEADD** and **TIMEADD** functions in Snowflake's SQL dialect. If this assumption is incorrect, defer this story until the capability is available in a newer parser.

The Snowflake product story remains the delivery driver. The implementation must also classify equivalent PostgreSQL-only function, part, and grammar forms through the same statement dialect mechanism, so adding Snowflake support does not create a one-sided dialect detector.

Subtree will be structured with three mandatory parts: Tree should have been structured with **"level"** for the part of the date to be modified, **"increment"** for the integer factor, and **"date part"** for the expression generating the date or timestamp.

Investigate if integer value and date or time expressions are just predicands. If so include tests for complex functions in each place. Confirm that substitution variables placed in these spots get classified correctly as predicand variables.

Add all standard Snowflake date or time part constants recognition. Add these terms as non-keyword enumerated types by adding grammar validation logic to the rule. Fix tests that might expect those entries to be column references at the moment. Extend predicated family of rules to include these functions.

While we're at this, add support for all Snowflake date and time functions, including:

- **EXTRACT** *(FROM form ✅; comma form stays `routine_invocation`)*
- **DATE_PART** *(FROM form ✅)*
- **DATEDIFF**, **DATE_TRUNC**, **TRUNC** (datetime; not DDL **TRUNCATE**)
- **TIMESTAMPADD**, **TIMESTAMPDIFF**, **TIMEADD**, **TIMEDIFF**
- **TIME_SLICE**, **LAST_DAY**, **NEXT_DAY**, **PREVIOUS_DAY**

Build rules for subsets of the date and time parts, and add all exit methods for rules.

### Dialect dependency marking (required)

Recognizing a construct is not sufficient: when correct execution depends on a target dialect, the statement must also be marked with that dialect affinity. Reuse the existing statement-level mechanism in `SqlParseEventWalker`:

- `recordStatementDialectGrammarHit(SqlGrammarDialect.SNOWFLAKE|POSTGRES, …)` increments the appropriate statement counter and emits one `STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR` or `STATEMENT_POSTGRES_DIALECT_GRAMMAR` warning;
- seeing both affinities in one statement emits `STATEMENT_MIXED_DIALECT_GRAMMAR` FATAL;
- scripts reset the counters and flags for every statement;
- `SqlGrammarDialectRuleRegistry` is the registry for distinct grammar rules; semantic registries/callbacks are appropriate when shared grammar is classified by a function name, argument form, or level/part value (as already done by `ExtractDatetimeFieldAffinity` and `SqlBareValueExpressionRegistry`).

This warning/counter pair is the current “query depends on Snowflake/Postgres” flag; do not add a competing symbol-table key. If consumers later require a serialized boolean or enum in `Snippet`, derive it from the same per-statement hit collector rather than adding a second classifier.

Branch the grammar when the dialects have different syntax, arity, AST, or keyword roles. Do **not** fork an otherwise identical rule merely to raise a flag: use a semantic affinity registry and call the same collector. Shared ANSI/Snowflake/Postgres forms remain neutral. A schema-qualified function call or an explicitly modeled user-defined function with the same name as a built-in must remain neutral unless it uses dialect-specific syntax.

#### Agent implementation matrix — date/time functions

“Flag” means call the statement dialect collector at the function token. “Shared” means accept without either flag; a dialect-specific part or argument form may still flag the statement separately.

| Construct accepted by the grammar | Target affinity | Detection / grammar action | Flag | Notes / required tests |
|-----------------------------------|-----------------|----------------------------|------|------------------------|
| `DATEADD(part, increment, value)`, `TIMEADD(…)`, `TIMESTAMPADD(…)` | Snowflake | Dedicated add-family production; distinguish `TIMESTAMPADD` from `TIMESTAMP` lexer split | Snowflake | All three aliases; canonical parts + documented abbreviations; substitutions in slots 2–3 |
| `DATEDIFF(part, left, right)`, `TIMEDIFF(…)`, `TIMESTAMPDIFF(…)` | Snowflake | Dedicated diff-family production | Snowflake | Preserve argument order; part is never a column |
| `TIME_SLICE(…)` | Snowflake | Dedicated production because arity and optional alignment argument are dialect-specific | Snowflake | Validate supported parts and optional arguments |
| `LAST_DAY(…)`, `NEXT_DAY(…)`, `PREVIOUS_DAY(…)` | Snowflake | Dedicated productions (including optional part where Snowflake permits it) | Snowflake | Do not infer affinity from an unqualified UDF call until the call matches the built-in shape |
| datetime `TRUNC(value, part)` / `TRUNC(value)` | Snowflake | Dedicated datetime alternative, separate from numeric `TRUNC` and DDL `TRUNCATE` | Snowflake | Numeric `TRUNC(number[, scale])` remains neutral unless a target-specific form proves otherwise |
| `EXTRACT(part FROM value)` | Shared shell | Fold extraction beneath `datetime_function`; classify `part` using the part matrix below | Part-dependent or neutral | Migrate AST under the Phase 1 extraction-unification action; preserve accepted source semantics |
| `DATE_PART(part FROM value)` | Shared parser extension | Unify dedicated FROM/comma alternatives beneath `datetime_function`; classify `part` | Part-dependent or neutral | Preserve accepted FROM syntax; AST refactoring is permitted by the Phase 1 follow-up below |
| `DATE_PART(part, value)` | Shared Snowflake/Postgres name | Dedicated DATE_PART alternative, accepting supported bare and quoted parts as metadata | Part-dependent or neutral | Remove built-in comma calls from generic argument collection; retain qualified UDF behavior and correct affinity |
| `DATE_TRUNC('part', value)` | Shared Snowflake/Postgres name | Shared production; classify the part and any dialect-specific overload | Part-dependent or neutral | Do not flag from the function name alone |
| `DATE_BIN(stride, source, origin)` | PostgreSQL | Dedicated Postgres production | Postgres | Interval stride; three arguments |
| `AGE(…)`, `MAKE_DATE(…)`, `MAKE_TIME(…)`, `MAKE_TIMESTAMP(…)`, `MAKE_TIMESTAMPTZ(…)`, `MAKE_INTERVAL(…)` | PostgreSQL | Dedicated built-in alternatives or semantic function-shape registry | Postgres | A schema-qualified same-name UDF is neutral |
| `JUSTIFY_DAYS(…)`, `JUSTIFY_HOURS(…)`, `JUSTIFY_INTERVAL(…)` | PostgreSQL | Semantic function-shape registry | Postgres | Interval argument |
| `CLOCK_TIMESTAMP()`, `STATEMENT_TIMESTAMP()`, `TRANSACTION_TIMESTAMP()`, `TIMEOFDAY()` | PostgreSQL | Zero-argument built-in alternatives / semantic registry | Postgres | Do not confuse shared `CURRENT_TIMESTAMP` with these |

The Postgres rows are part of the dialect-symmetry requirement for Entry 1. They may be delivered after Snowflake slices A–C, but the grammar/registry design must leave explicit branches for them and must not classify their names as ordinary columns.

#### Approved parser grammar design (2026-11-11)

Dedicated date/time productions must precede the generic `value_expression_primary` / `routine_invocation` path so a level such as `year` is parsed as part metadata, never as a `column_reference`. The original EXTRACT/DATE_PART AST preservation requirements are superseded by the 2026-11-12 Phase 1 extraction-unification action below; grammar examples here describe the initial design until that action is implemented.

The umbrella grammar rule and the common AST key are both named
`datetime_function`. Family-specific grammar rules specialize this parent;
they do not introduce separate top-level AST keys.

```antlr
predicand_primary
  : extract_expression
  | date_part_expression
  | datetime_function                     // NEW: before generic functions
  | value_expression_primary
  | string_value_expression
  | sign numeric_primary
  | trim_function
  | null_literal
  | variable_identifier
  | puml_constant_identifier
  | position_function
  ;

numeric_primary
  : extract_expression
  | date_part_expression
  | datetime_function                     // NEW
  | value_expression_primary
  ;

datetime_function
  : snowflake_datetime_add_expression
  | snowflake_datetime_diff_expression
  | datetime_trunc_expression
  | snowflake_time_slice_expression
  | snowflake_day_boundary_expression
  | postgres_datetime_expression
  ;
```

Use one non-column part production across the new functions. Keep `extract_field` as a compatibility wrapper. The final `identifier` alternative accepts documented aliases and extensible engine parts without collecting them as columns; the shared Java registry performs canonicalization and affinity classification.

```antlr
extract_field
  : datetime_part
  ;

datetime_part
  : primary_datetime_field
  | time_zone_field
  | extended_datetime_field
  | snowflake_datetime_part
  | character_literal
  | identifier
  ;

snowflake_datetime_part
  : DAYOFMONTH | DAYOFWEEK | DAYOFWEEKISO | DAYOFYEAR
  | WEEKISO | WEEKOFYEAR
  | EPOCH_SECOND | EPOCH_MILLISECOND | EPOCH_MICROSECOND
  ;
```

The dedicated rules must label arguments semantically. Representative shapes:

```antlr
snowflake_datetime_add_expression
  : function=snowflake_datetime_add_function
    LEFT_PAREN level=datetime_part
    COMMA increment=value_expression
    COMMA datetime_value=value_expression RIGHT_PAREN
  ;

snowflake_datetime_diff_expression
  : function=snowflake_datetime_diff_function
    LEFT_PAREN level=datetime_part
    COMMA left_value=value_expression
    COMMA right_value=value_expression RIGHT_PAREN
  ;

datetime_trunc_expression
  : DATE_TRUNC LEFT_PAREN level=datetime_part
    COMMA datetime_value=value_expression RIGHT_PAREN
  | TRUNC LEFT_PAREN datetime_value=value_expression
    COMMA level=datetime_part RIGHT_PAREN
  ;
```

Apply the same pattern to the remaining matrix rows. Bare built-ins that match these shapes use the dedicated rules and dialect classifier. Schema-qualified same-name calls remain generic, dialect-neutral UDF calls. One-argument `TRUNC(value)` remains on the generic path unless semantic type evidence can distinguish datetime truncation from neutral numeric truncation.

#### Approved `datetime_function` AST contract (2026-11-11)

All new dedicated Snowflake and PostgreSQL constructions use a common top-level `datetime_function` key. Do not place them under generic `function`, and do not encode their arguments as positional `parameters`: that would allow a generic generator to render a date/time part as an ordinary expression.

```text
DATEADD(year, 1, created_at)

{datetime_function={
  function_name=DATEADD,
  level={canonical=YEAR, text=year, form=KEYWORD},
  increment={literal=1},
  value={column={name=created_at}}
}}

DATE_TRUNC('month', created_at)

{datetime_function={
  function_name=DATE_TRUNC,
  level={canonical=MONTH, text=month, form=STRING},
  value={column={name=created_at}}
}}
```

Part-like values use `{canonical, text, form}`:

- `canonical` is the registry-normalized meaning used for validation and dialect affinity;
- `text` preserves the authored spelling, including aliases such as `mm`;
- `form` is `KEYWORD`, `IDENTIFIER`, or `STRING`;
- generators write `KEYWORD` / `IDENTIFIER` text without quotes and emit `STRING` text as an escaped SQL character literal;
- canonicalization must never silently replace authored text during ordinary regeneration.

`function_name` preserves the authored case, as ordinary routine calls do.
`function_name` is the approved discriminator; there is no `invocation` AST
key. Generators dispatch once on `datetime_function`, then compare `function_name`
case-insensitively to select an ordered signature. Preserve the authored name
when generating SQL; never infer argument order from map insertion order.

| `function_name` value(s) | Permitted subtree keys in SQL argument order |
|-------------------|-----------------------------------------------|
| `DATEADD`, `TIMEADD`, `TIMESTAMPADD` | `level`, `increment`, `value` |
| `DATEDIFF`, `TIMEDIFF`, `TIMESTAMPDIFF` | `level`, `left`, `right` |
| `DATE_TRUNC` | `level`, `value` |
| datetime `TRUNC` | `value`, optional `level` |
| `TIME_SLICE` | `value`, `slice_length`, `level`, optional `boundary` |
| `LAST_DAY` | `value`, optional `level` |
| `NEXT_DAY`, `PREVIOUS_DAY` | `value`, `weekday` |
| `DATE_BIN` | `stride`, `source`, `origin` |
| one-argument `AGE` | `value` |
| two-argument `AGE` | `left`, `right` |
| `MAKE_DATE` | `year`, `month`, `day` |
| `MAKE_TIME` | `hour`, `minute`, `second` |
| `MAKE_TIMESTAMP` | `year`, `month`, `day`, `hour`, `minute`, `second` |
| `MAKE_TIMESTAMPTZ` | timestamp fields plus optional `timezone` |
| `MAKE_INTERVAL` | `components`, keyed by `years`, `months`, `weeks`, `days`, `hours`, `mins`, `secs`; retain positional vs named source form where required for regeneration |
| `JUSTIFY_DAYS`, `JUSTIFY_HOURS`, `JUSTIFY_INTERVAL` | `value` |
| `CLOCK_TIMESTAMP`, `STATEMENT_TIMESTAMP`, `TRANSACTION_TIMESTAMP`, `TIMEOFDAY` | no argument fields |

`level` and `weekday` use the part-like metadata contract. Ordinary expression fields retain their normal AST nodes, including predicand-typed substitutions. The AST does not store a second dialect flag: affinity continues to use the existing per-statement diagnostic collector.

#### Agent implementation matrix — level / part terms

Apply this matrix in every dedicated part slot (`DATEADD`, diff/trunc family, `EXTRACT`, `DATE_PART`, `TIME_SLICE`, and Postgres date/time productions where applicable). Quoted part literals and unquoted level tokens must converge on the same normalized classifier.

| Accepted canonical part / level | Target affinity | Flag behavior |
|---------------------------------|-----------------|---------------|
| `YEAR`, `QUARTER`, `MONTH`, `WEEK`, `DAY`, `HOUR`, `MINUTE`, `SECOND` | Shared | No dialect flag |
| `DAYOFMONTH`, `DAYOFWEEK`, `DAYOFWEEKISO`, `DAYOFYEAR`, `WEEKISO`, `WEEKOFYEAR`, `EPOCH_SECOND`, `EPOCH_MILLISECOND`, `EPOCH_MICROSECOND` | Snowflake | Snowflake flag |
| `CENTURY`, `DECADE`, `DOW`, `DOY`, `EPOCH`, `ISODOW`, `ISOYEAR`, `MICROSECONDS`, `MILLENNIUM`, `MILLISECONDS` | PostgreSQL | Postgres flag |
| `TIMEZONE`, `TIMEZONE_HOUR`, `TIMEZONE_MINUTE` | PostgreSQL | Postgres flag; add to `ExtractDatetimeFieldAffinity` unless Snowflake documentation proves a shared form |
| Documented aliases/abbreviations (`yy`, `yyyy`, `qtr`, `mon`, `mm`, `wk`, `woy`, `dd`, `dw`, `hh`, `mi`, `sec`, `ms`, `us`, `ns`, etc.) | Inherit the owning canonical part | Normalize first, then flag; verify each alias against current Snowflake/Postgres documentation—do not guess from spelling |
| Character literal or identifier not in a target registry | Unknown / extensible | Accept where the function permits extensible text, but do not set a dialect flag and do not collect it as a column |

Keep a single machine-readable part registry (canonical name, aliases, allowed function families, affinity). Do not duplicate sets independently in grammar predicates, `ExtractDatetimeFieldAffinity`, and walker code.

#### Agent implementation matrix — non-date constructs that establish dialect affinity

Entry 1 must preserve and test these existing markers when composing or nesting date/time expressions. This is also the model for future dialect branches. The rows describe the current registry; audit constructs that overlap across engines before expanding the catalog.

| Target | Already recognized constructs / keywords | Implementation source |
|--------|------------------------------------------|-----------------------|
| Snowflake | `QUALIFY`; `PIVOT` / `UNPIVOT`; Snowflake multi-aggregate PIVOT; nested `WITH` inside a CTE body; Snowflake `TRUNCATE TABLE`; Snowflake DELETE form; quoted numeric identifiers; dollar function identifiers | `SqlGrammarDialectRuleRegistry` plus `nestedWithInCteBodyRegistration` |
| PostgreSQL | `ON CONFLICT`; `DELETE … RETURNING` / `delete_returning`; Postgres DELETE form; Postgres TRUNCATE form (optional `TABLE`, multiple targets) | `SqlGrammarDialectRuleRegistry` |
| Snowflake bare value | `CURRENT_ORGANIZATION_USER` | `SqlBareValueExpressionRegistry` |
| PostgreSQL bare values | `CURRENT_CATALOG`, `CURRENT_ROLE`, `CURRENT_SCHEMA`, `SESSION_USER`, `USER` | `SqlBareValueExpressionRegistry` |
| Shared / neutral bare values | `CURRENT_DATE`, `CURRENT_TIME`, `CURRENT_TIMESTAMP`, `CURRENT_USER`, `LOCALTIME`, `LOCALTIMESTAMP` | `SqlBareValueExpressionRegistry`; no dialect flag |

**Dialect acceptance tests:** one warning and the correct counter for each target-only row; no warning for every shared row; nested target-specific function/part combinations count every hit but emit one target warning; Snowflake + Postgres in either order emits one mixed-dialect FATAL at the second affinity; SCRIPT statements reset independently; part text is absent from table/query dictionaries; grammar-rule and semantic-registry paths produce the same diagnostic contract.

**Testing:** (1) Basic dateadd/timeadd; (2) rule templates for `DATEADD` / `TIMEADD` / `TIMESTAMPADD`; (3) symbol tables resolve increment and date/time expr, **ignore** parts; predicand substitutions in slots 2–3; nested complexity; fec/`year` regression (formerly “parser-defects Phase 4”).

### Baseline (2026-09-28)

Most names parse as generic `function` via `routine_invocation`; unquoted parts become **`column`** refs. **`TIMESTAMPADD` fails parse** (`TIMESTAMP` lexer split). No dedicated AST or walker tests.

### Absorbed corpus defect (former Entry 17)

Entry 1 owns the complete fix and regression evidence for the date-part-as-column defect found by the 5.0.0-3 vs 5.1.4-1 comparison of the 11-10-2026 extract.

**Problem:** The first argument of `DATEADD` / `DATEDIFF` (and the other date-part functions in this entry) is parsed as an ordinary column reference. When the only visible sources are derived tables or CTEs, the part (`year`, `day`, `month`, …) is looked up in their output interfaces and reported FATAL `UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES`. With a physical table in scope it may appear clean while silently recording the part as a phantom source column, which corrupts lineage.

**Known affected bound query:**

| Case ID | Extract record | Domain / entity / query name | FATAL / expression |
|---------|----------------|------------------------------|--------------------|
| q5866 | `bound_queries_11-10-2026.csv` record 5866 (header = record 1) | PDP_AMS / mkt__segmentation / pdp_ams_mkt__segmentation_intake | `Unqualified column 'year' at (l:29 c:15) was not found in output interface of any visible query alias [cte_mkt_segmentation]` — `dateadd(year,2000,effective_end_date)` |

**q5866 resolved (2026-11-13):** Replayed the original frozen SQL record with
the current JDK 21 isolated-build fat JAR. The historical 5.1.4 run had one
FATAL; the replay had zero syntax errors and zero diagnostics. Both DATEADD
calls retain YEAR metadata and `effective_end_date` as their value; `year` is
absent as a column in global dictionaries and published symbol-table lineage.
This closes the q5866 case only, not the broader DATEDIFF/date-time work or
the required full-corpus re-test.

**Minimal reproductions (2026-11-11):**

- `select dateadd(year, 1, c.d) as y from (select a.d from t a) c` → FATAL on `year`;
- the same occurs with `dateadd(day, …)` and `datediff(month, …)`;
- over a physical source (`from t a`) the calls may be clean but incorrectly record `year` / `day` as columns.

The fix is not merely to suppress q5866's diagnostic. Every recognized date/time part and documented abbreviation must be parsed as a part constant, excluded from table/query dictionaries and unresolved-column handling, and classified for dialect affinity using the matrices above.

### Extract/corpus re-test required before Entry 1 closes

Use a rebuilt 5.1.4 fat jar and re-run both 11-10-2026 extracts through their correct endpoints:

1. Parse every record in `bound_queries_11-10-2026.csv` through `SQL`.
2. Parse every record in `snippets-11-10-2026.csv` through the endpoint implied by its type (`COLUMN`, `PREDICAND`, `CONDITION`, `TUPLE`, `QUERY`, `JOIN_EXTENSION`, `IN_LIST`, etc.), using the same endpoint-input normalization as the prior 5.0.0-3 vs 5.1.4-1 comparison.
3. Re-test every bound-query or snippet record that previously failed on 5.1.4 but succeeded on 5.0.0-3; identify date/time cases separately from unrelated author errors and Entries 16/18.
4. q5866 must lose its FATAL, and `year` must be absent from its table dictionary, query dictionary, unresolved-reference diagnostics, and lineage.
5. Search both extracts for every function and part/alias listed in the Entry 1 matrices. For each match, verify that the part is not collected as a column and that target-only constructs produce the expected Snowflake/Postgres statement flag.
6. Compare the new run with the saved 5.0.0-3 and pre-fix 5.1.4 results. Investigate every new FATAL and every changed AST/interface outside the intended date/time nodes; do not refresh unrelated goldens or accept corpus drift wholesale.
7. Record the affected case IDs, template/bundle names, before/after FATAL counts, dialect diagnostics, and any remaining failures in this entry. When extracts with stable Panto IDs become available, add those IDs rather than relying only on `qN` / `sN` record numbers.

There was no known snippet-specific date-part regression in the original 15 parser-defect cases, but the snippet extract must still be re-run: generic function parsing can hide phantom part columns without producing a FATAL.

**Interim snippet replay (2026-11-13):** Replayed all 87,897 recognized
snippet records from `corpus-final.jsonl` with the current isolated-build
5.1.4-1 fat JAR and their frozen endpoint normalization. Eight source records
have unrecognized extract types and were excluded as in the original comparison.
Against the saved `final514` run, all cases have the same clean/FATAL status:
87,378 clean, 519 with FATALs, no batch crashes or timeouts; no cases newly
passed or failed. Against `base5003` with the `inlist5003` overlay, 26 cases
are FATAL only on current 5.1.4, nine are FATAL only on 5.0.0-3, and 493
are FATAL on both. The 26 are the same historical cases: 25 previously
classified for author remediation and the owner-waived unused s82233.
The two parsers' totals are 87,378 vs 87,395 clean respectively.

The current replay changed canonical AST hashes for 121 cases clean in both
5.1.4 runs; all 121 contain textual add-family function matches. Two already
failing cases have changed walker-exception messages but unchanged FATAL
counts. Results and case IDs are in the local-only archive under
`comparison/runs/snippet514-20261113-0909/`.

**Fresh bound-query replay (2026-11-13):** Replayed all 6,551 original
bound-query SQL inputs on the current isolated-build 5.1.4-1 fat JAR; each
input was verified against the frozen historical corpus. There were no batch
crashes or timeouts. Against the saved 5.0.0-3 results:

| 5.0.0-3 | Current 5.1.4 | Records |
|---|---|---:|
| Zero FATALs | Zero FATALs | 6,304 |
| Zero FATALs | FATAL | 147 |
| FATAL | FATAL | 73 |
| FATAL | Zero FATALs | 27 |

The 147 5.1.4-only FATALs are all in the historical classification:
110 duplicate outputs in a set-operation branch with a width diagnostic;
five duplicate outputs in one SELECT; one set-operation width mismatch;
16 missing or out-of-scope aliases; five concrete columns absent from a
derived interface; one syntax case (q5128); seven variable-outside cases
covered by Entry 20; and two mirror-image variable-inside cases (q5314 and
q5335) approved by the owner for release with author guidance to add fixed
output aliases or use concrete columns. The original two queries remain
FATAL until revised; no parser change is implied. The owner also accepts the
other 138 author-classified bound-query differences for release, with
corrective guidance in
[Panto template and snippet remediation](./panto-template-remediation-5.1.4.md).
The same acceptance applies to the remaining author-classified snippet issues
in Patterns 1–5 and 7. The owner also accepts mirror-image snippets s8491 and
s8492 for release with fixed-alias or concrete-column guidance. These
dispositions do not mean the SQL has already been corrected or that unchanged
records will pass. Entry 20 is now closed (2026-11-13) with the
qualified-column-variable FATAL replaced by the approved warning behavior.

Compared with the prior 5.1.4 run, 14 previously failing bound queries now
have zero FATALs, including q5866; no clean query became FATAL. One query
that still fails, q3718, retains its syntax FATAL while its walker-exception
message changes. Canonical AST hashes changed for 36 queries with zero FATALs
in both 5.1.4 runs; their separate audit is recorded below. The 73 failures
shared with 5.0.0 and the 27
5.0.0-only failures are status comparisons, not individual semantic
approvals. Local-only results and per-case IDs are under
`comparison/runs/bound514-20261113-fresh/`.

**Bound-query DATEADD AST audit (2026-11-13):** All 36 changed ASTs were
captured from the historical and current 5.1.4 JARs, with hashes matching the
saved corpus results. All 100 DATEADD calls migrate from generic `function`
nodes to the expected `datetime_function` representation. Applying only that
transformation to each historical AST reproduces the entire current AST.
Function names and authored part text match the SQL, and all 200 increment
and value expressions match independent parenthesized PREDICAND parses.
All 36 original queries and all 200 probes have zero syntax errors, reach EOF,
and report zero FATALs on the current JAR.

Zero FATALs does not mean zero diagnostics: 12 queries still emit 18
`AMBIGUOUS_COLUMN_REFERENCE` SEVERE_WARNINGs and 15
`UNRESOLVED_UNQUALIFIED_COLUMNS` ERROR diagnostics. These concern
`acs_contact_id`, `ST_DT_APP_STU_GEN_ACSL`, and `ST_DT_APP_PAR_GEN_ACSL`,
not DATEADD part keywords; the same unresolved references existed on the
historical 5.1.4 JAR. Three queries lose historical phantom DATEADD-part
diagnostics (`year` in q3673, `day` in q4155 and q5499). The audit validates
the AST migration, not the unresolved references, dictionaries, lineage,
generation, or release approval. Local-only per-case evidence and captured
diagnostics are under `comparison/runs/datetime-bound-ast-audit-20261113/`.

**5.0.0 diagnostic comparison:** All 12 of these queries also have zero
FATALs and zero syntax errors on 5.0.0-3. Its exposed parser message lists
are empty for each; unlike 5.1.4, that version does not expose the same
structured diagnostic API. Thus FATAL status matches, but the available
outputs do not establish matching diagnostic behavior. The owner approves
all 12 queries (q4139–q4143, q4149–q4151, q4155, q4157, q4159, and q4160)
for the 5.0.0-to-5.1.4 switch as-is. Their recorded direct-API diagnostics include both
`AMBIGUOUS_COLUMN_REFERENCE` SEVERE_WARNING and
`UNRESOLVED_UNQUALIFIED_COLUMNS` ERROR for `acs_contact_id`; q4157, q4159,
and q4160 also
report these diagnostics for `ST_DT_APP_STU_GEN_ACSL` and
`ST_DT_APP_PAR_GEN_ACSL`. Approval does not change their direct-API severities.
All 12 have now received the owner's release disposition.

**RMCP presentation check (q4151):** Direct `SqlParserAccess` reports the
unresolved-column diagnostic as ERROR on both the RMCP-bundled 5.1.3 JAR and
the current 5.1.4 JAR. Through RMCP's MCP service, both JARs instead return
that diagnostic as WARNING along with the ambiguity SEVERE_WARNING; both
report zero FATALs. The MCP adapter normalizes AST-walker ERRORs to WARNING,
so the user's warning-only RMCP result and the direct-parser audit are
consistent with their different delivery paths. This check is for q4151;
do not infer that every diagnostic from every query has been compared through
both paths.

**Bare TUPLE review (2026-11-13):** The owner accepts the 484 cases that
FATAL on both parsers as bare SELECT inputs and accepts the 471 that were
FATAL-free on both historical parsers when wrapped. This is acceptance of those
comparison outcomes, not a claim that the 471 were freshly re-run or checked
for every warning, AST, or consumer behavior.

Of the other 13, the current JAR reproduced the historical 5.1.4 FATAL counts:

- Eleven (s37279, s87311, s87868–s87875, s87905) include a trailing semicolon.
  The historical wrapper placed it inside the closing parenthesis. The legacy
  parser reports two FATALs; current 5.1.4 reports no FATALs but emits two
  `RECOVER_INLINE` warnings and leaves the semicolon as its current token.
  **This is a comparison-input artifact, not an established difference for the
  original snippets.** The owner reports successful unwrapped parses in RMCP
  with 5.1.3 while selecting the TUPLE endpoint, and confirms that applications
  will not wrap these statements without removing the semicolon. **Owner
  disposition: all eleven are accepted as successfully matched for the snippet
  comparison**; the artificial wrapper differences require no remediation.
  Replaying all eleven original inputs at the SQL endpoint produced zero
  FATALs on each of 5.0.0-3, RMCP's bundled 5.1.3, and current 5.1.4.
  At the TUPLE endpoint, the same unwrapped inputs produced FATALs on all
  three versions in the standalone replay. This differs from the owner's RMCP
  observation and is retained as a test-path distinction, not a rejected
  snippet-comparison case. In the inspected RMCP extension, highlighted-editor
  validation defaults to SQL even if a different endpoint is selected; the
  owner's exact validation path has not been independently established.
  Removing that terminal semicolon *for a separate comparison input* before
  wrapping produced zero FATALs and matching AST hashes on both parsers in all
  eleven cases; current 5.1.4 reached EOF with no diagnostics.
- s84947 projects `extended_prop_min_value` twice: current 5.1.4 emits a
  duplicate-interface FATAL, whereas 5.0.0-3 did not. **Accepted by the
  owner:** the 5.1.4 FATAL prevents a duplicate-column runtime failure.
- s85047 references `sub.<Source Contact ID>` in a join while `sub` projects
  `sourcecontact_id AS source_contact_id`: current 5.1.4 emits a
  qualified-column-not-found FATAL and a separate ambiguous-column
  SEVERE_WARNING; 5.0.0-3 reported no FATAL. The owner confirmed the qualified
  column-variable FATAL as a parser defect; fix and parity gate: **Entry 20**.

**Snippet-comparison disposition:** The owner considers the reviewed snippet
comparison satisfied; the qualified column-variable defect tracked in
**Entry 20** is now resolved and no longer blocks parity. This disposition
includes the eight records with unrecognized types (s37280–s37286 and s77528):
the owner considers them invalid extract records and excludes them as parity
blockers for either version. Neither parser was tested on these records, so
this is an accepted data disposition, not a measured parser outcome. The
disposition does not claim that the broader Entry 1 corpus, dialect, lineage,
and consumer checks are complete.
The original inputs remain unchanged; local-only replay evidence is under
`comparison/runs/tuplewrap-differences-20261113/`,
`comparison/runs/tuple-semicolon-514-20261113/`, and
`comparison/runs/tuple-semicolon-5003-20261113/`.

**DATEADD AST audit (2026-11-13):** All 121 changed snippets are individually
approved as DATEADD AST migration fixtures for dependent-application testing
(117 PREDICAND, three CONDITION, one TUPLE; 128 DATEADD calls). Captured AST
hashes matched both saved runs; each whole-tree difference matched the expected
generic-function-to-`datetime_function` transformation. Authored function
names and levels matched their AST metadata; 256 independently parsed,
parenthesized increment/value operands matched the embedded ASTs. All 121
original snippets had zero syntax errors and FATALs. The per-case disposition,
original endpoint inputs, and historical/current AST fixtures are in the
local-only archive under `comparison/runs/datetime-ast-audit-20261113/`.
Approval is limited to this AST migration; it does not certify dictionary,
lineage, dialect, generator behavior, other pre-existing AST semantics, or the
full corpus gate above.

**Separate pre-existing PREDICAND finding:** Standalone `-1 * 30` silently
produces the AST for `-1` with zero syntax errors and FATALs on both the
historical postfix and current JAR; `(-1 * 30)` preserves the multiplication.
The audited embedded DATEADD expressions retain their full operands, and no
extract PREDICAND is exactly the standalone example. The owner does not regard
this synthetic example as a blocker for certifying the snippet extract. The
general standalone endpoint behavior remains outside the AST audit.

### Local corpus evidence and replay tools (archived 2026-11-11)

The former repository `.tmp/` assessment artifacts and the two untracked live CSV
extracts were moved to a **local-only sibling archive**, outside this Git repository:
[`../../../pss-parser-evidence/2026-11-11/`](../../../pss-parser-evidence/2026-11-11/).
On the current workstation this is
`/Users/ghowe/emculturate-pss/pss-parser-evidence/2026-11-11`.
The archive contains authored SQL and diagnostic payloads; do not commit, publish,
or upload it. It is not a shared or remote backup.

| Archive-relative location | Purpose |
|---------------------------|---------|
| `live_queries/` | Original bound-query and snippet CSV extracts; preserve record order for stable `qN` / `sN` IDs |
| `comparison/corpus*.jsonl` | Frozen endpoint-normalized inputs, including final, IN_LIST, tuple-wrapper, and join-extension variants |
| `comparison/runs/*/results.jsonl` | Historical 5.0.0-3 and pre/post-fix 5.1.4 results; keep all tags, especially `base5003`, `inlist5003`, `pre514`, `final514`, and `entry16-*` |
| `comparison/runs/jext514/` | Unmerged historical run; shard outputs retained because no merged result exists |
| `comparison/` | Batch/probe Java sources, comparison/classification scripts, remediation indexes, affected IDs, summaries, and detailed probe results |
| `alias-scope/`, `capture/` | Entry 13–16 scope/lineage probes, including the 113-query Entry 14 evidence and captured golden failures |
| `legacy-5003/` | Frozen legacy parser source/build snapshot, including the 5.0.0-3 reference JAR; not an active module |
| `jars/` | Historical prefix/postfix 5.1.4 JARs, not current release artifacts |
| `logs/` | Historical compile/test evidence, including `full-parse-regression-entry16-final.log`: 2,194 tests, 0 failures, 0 errors, 21 skipped; this is a recorded result, not a new verification |
| `manifests/` | Source-to-archive paths, sizes, SHA-256 hashes, discarded-file reasons, initial Git status, and hashes of unrelated modified files |
| `original-tools/` | Byte-preserved originals of the four replay scripts whose paths were adapted after relocation |

Cleanup removed only untracked rebuildable classes/Python caches, empty outputs,
the machine-specific classpath, Finder metadata, shard inputs verified against
retained corpus records, and shard outputs whose concatenation was byte-identical
to a retained merged result. Other evidence, including historical failing logs,
was retained. Previously tracked temporary artifacts now appear as Git deletions;
the existing build/IDE edits were left unchanged.

**Replay from the repository root** (requires the rebuilt fat JAR and a suitable JDK):

```bash
REPO="$PWD"
EVIDENCE="$(cd .. && pwd)/pss-parser-evidence/2026-11-11"
python3 "$EVIDENCE/comparison/run_corpus.py" run datetime514 \
  "$REPO/parse/target/pss-parse-5.1.4-1-fat.jar" \
  --corpus "$EVIDENCE/comparison/corpus-final.jsonl" --shards 1
```

Use a **new run tag** each time; do not overwrite historical baseline tags.
`--only "$EVIDENCE/comparison/entry16-affected-ids.txt"` selects the saved Entry 16
cases; another ID file can select q5866 or the date/time matrix.
`run_corpus.py build` reconstructs `corpus.jsonl` from the archived CSVs, but the
frozen `corpus-final.jsonl` is the comparison reference: keep its historical
endpoint normalization rather than assuming a fresh build is identical.

`compare_live.py` compares its fixed `base5003` / `pre514` / `post514` tags.
`new_fatals.py` compares `final514` against `base5003` overlaid with `inlist5003`;
`remediation_index.py` regenerates the local remediation index from those saved
results and classifications. The older `compare_panto_corpus.py` and
`compare_panto_29.py` now require `PSS_REPO_ROOT="$REPO"` to locate repository
fixtures/current JARs and use the archived legacy JAR; the latter still targets
5.1.3 and is historical, not the Entry 1 runner.

The batch summaries retain AST hashes and selected diagnostics, **not** complete
dictionaries, lineage, or dialect hit counts. Entry 1 closure still requires the
detailed AST/symbol-table/dialect assertions specified above.

### Work phases

| Phase | Work |
|-------|------|
| **1 Lexer baseline** | Inventory and add every lexer entry required by delivery Slices B–E in one late-keyword batch immediately before `Identifier`; regenerate; publish the complete old→new token-ID substitutions in safe replacement order; the user updates affected goldens manually; require a green full suite before grammar work |
| **1 follow-up: datetime regression suite** | Implement the invalid DATEADD level FATAL prerequisite, then develop the numbered DATEADD suite and reduced cross-function suite below, incrementally with the relevant delivery slices; the lexer baseline remains complete |
| **1 follow-up: extraction unification** | Refactor EXTRACT and DATE_PART into datetime-family subtrees, with metadata-only parts, normal source expressions, explicit syntax-form retention, and the AST details described below |
| **1 follow-up: per-function build and test** | Execute actions 66-97 below: deep context suites for distinct signature primaries, compact construction/resolution/validation suites for every sibling invocation |
| **2 Grammar + AST** | Implement the approved dedicated productions, `datetime_part`, `datetime_function` contract, and invocation signature registry incrementally; branch only when syntax, arity, AST, or keyword role differs |
| **3 Walker + affinity** | Add every exit method; exclude parts from symbol collection; stamp predicand substitutions in expression fields; route rule/function/part affinity through the existing statement collector |
| **4 Focused tests** | Extend `SqlEventWalkerDatetimeFunctionPlacementTests` plus PostgreSQL/shared matrices, AST signatures, rendering forms, counters/warnings/mixed fatal, SCRIPT reset, and fec/`year` CASE |
| **5 Corpus + delivery** | Rebuild jar; re-run bound-query and snippet extracts; prove q5866 and all date/time matches; compare FATALs, AST/interfaces, dictionaries, lineage, and dialect diagnostics; finish generator/docs/release notes |

#### Phase 1 follow-up substep: invalid DATEADD level diagnostic

**Implemented 2026-11-12; DATEADD family milestone complete through scenario 65.**
This is a prerequisite to the expanded DATEADD test suite, not a reopening of
the completed lexer baseline. No new lexer tokens or token-ID changes are required.
DATEADD now reports `INVALID_DATETIME_PART` FATAL at the authored level token
for an unknown part. Focused tests cover registered spellings, invalid bare and
quoted levels, empty text, and an invalid nested level; the remaining canonical,
alias, and placement matrices below are still pending. TIMEADD and TIMESTAMPADD
subsequently gained the same registered-part validation; other families remain
unvalidated by this add-family change.

- Add a dedicated **FATAL** diagnostic for an invalid DATEADD level code
  (proposed code: `INVALID_DATETIME_PART`), using the existing diagnostic
  reporting path. Identify the invocation, authored level text, and source
  location.
- Validate the decoded level against the accepted DATEADD canonical names and
  aliases. An unknown spelling must not become accepted merely because
  `canonicalize` uppercases it. Reject parts unsupported by DATEADD even if
  another date/time function accepts them; do not broaden EXTRACT or other
  function validation as part of this substep.
- Keep invalid level text out of column collection and resolution. Report the
  level diagnostic rather than a phantom-column diagnostic.
- Cover invalid identifier and string forms, empty quoted text, misspellings,
  and invalid levels in nested calls. Keep these separate from malformed SQL
  syntax tests. Replace the new DATEADD AST test's permissive unknown-part
  expectation with the explicit FATAL expectation.
- Positive level coverage must exercise all registered canonical names and
  their accepted aliases, preserving authored text/form while asserting the
  expected canonical value. Use explicit expected mappings, not registry output
  as its own test oracle; a multi-expression SELECT can cover all canonical
  names, with separate alias/form cases.
- Extend the planned dedicated placement suite with unprefixed `predicand`
  substitutions and table-prefixed `column` substitutions in INCREMENT, VALUE,
  and formulas within either slot. Include both same-kind and mixed-kind slot
  combinations, normal dictionaries/lineage, and scope qualification checks.
- Include DATEADD as an argument to other functions and DATEADD nested in the
  VALUE slot with a different date part and increment. Assert each call's
  independent metadata and all expression-column dependencies.

#### Phase 1 follow-up action: unify DATE_PART and EXTRACT grammar and AST

**Implemented; closure coverage and consumer/generator updates remain.** Built-in
`DATE_PART(part, expression)`, `DATE_PART(part FROM expression)`, and
`EXTRACT(part FROM expression)` as specialized alternatives beneath
`datetime_function`. Include the parser's already accepted EXTRACT comma form
in the extraction refactor, without claiming it is standard engine syntax.
This supersedes this entry's earlier requirements to preserve the EXTRACT and
DATE_PART AST shapes. Both invocations now use datetime-family subtrees.

- History: commit `eb0566e` (2026-08-06, "Add DATE_PART FROM parsing and
  case-insensitive EXTRACT/DATE_PART lexers.") introduced the dedicated grammar,
  walker support, and tests. The completed-baselines table also dates this
  functionality to August 2026. This establishes introduction after January
  2026, not an independently verified artifact publication date; no release
  tags are available in the current repository. Earlier generic same-name
  calls are distinct from this dedicated feature.
- Per the user's January-2026 compatibility cutoff, the newly introduced
  dedicated DATE_PART AST need not be frozen for legacy use cases. The user
  explicitly approved changing DATE_PART and EXTRACT to `datetime_function`
  subtrees:
  `datetime_function={function_name=DATE_PART|EXTRACT, level={canonical,text,form},
  value=..., <syntax discriminator>}`. Choose and document the discriminator
  key/values explicitly; retain comma versus FROM so generators never guess
  the authored syntax from map order or part form.
- Route both built-in forms through dedicated recognition before generic
  invocation. Bare/quoted part spellings are metadata, not column arguments.
  Reuse common recognition/registry mechanisms, but validate/classify against
  extraction-specific supported parts, not DATEADD's part subset.
- Explicitly recognize the comma-delimited variation
  `DATE_PART(year, expression)` as well as `DATE_PART('year', expression)`.
  Validate the part in comma and FROM forms alike, including canonical names,
  accepted aliases, and invalid bare/quoted text. Do not let comma syntax
  bypass part validation or collect its first argument as a column. Define
  extraction-specific invalid-part diagnostics during this development step;
  DATEADD's allowed-part set must not become the extraction validity test.
- Apply the same metadata recognition, extraction-specific part validation,
  and normal source-expression traversal to EXTRACT. Preserve accepted interval
  and typed datetime literal sources, timezone/extended parts, and source type
  information in the new VALUE subtree. Do not restrict EXTRACT to DATEADD
  units or lose existing extraction semantics during the AST migration.
- Source expressions must retain ordinary columns, formulas, nested functions,
  unprefixed predicand substitutions, and table-qualified column substitutions,
  with normal resolution, dictionaries, and lineage. Keep schema-qualified
  `custom.date_part(...)` and `custom.extract(...)` generic; do not apply
  built-in part validation to UDFs.
- Update directly dependent walker exits and AST consumers/generation paths.
  Inventory and update all affected DATE_PART and EXTRACT tests, including
  those introduced in August: AST, diagnostics, dictionaries, lineage, typed
  sources, and endpoint coverage. Preserve intended behavior except for the
  approved AST/part-handling changes. Existing token-ID goldens remain
  user-edited only; review behavioral golden changes explicitly.
- Update consumer instructions to describe the new extraction subtrees and
  adoption requirements directly: dispatch on `datetime_function` and
  `function_name=DATE_PART|EXTRACT` case-insensitively, render level metadata with its preserved spelling and
  quoting form, recursively generate the VALUE expression, and use the explicit
  syntax discriminator to emit comma or FROM syntax. Include examples for both
  forms and explain part validation and normal source-expression traversal.
  Do not include a historical explanation that DATE_PART previously followed
  generic function rules; that migration narrative is not needed.
- Extend scenario 58 below with both syntax forms, quoted/bare canonical and
  alias levels, no phantom-part columns, source-expression/substitution
  coverage, nested DATEADD, qualified UDF controls, syntax preservation, and
  extraction-specific dialect/diagnostic cases. Assert each invocation
  independently so DATE_PART coverage cannot mask an EXTRACT regression.

#### Phase 1 follow-up test suite: formulation and assertion instructions

**Consolidated 2026-11-12; partially implemented by family.** The single numbered inventory
below combines the agreed argument, placement, substitution, level, and nesting
coverage. Numbers identify coverage scenarios, not a promised count of JUnit
methods: a scenario may require several concrete cases. The DATEADD inventory
comes first; the reduced cross-function inventory follows with continuous
numbering. Other functions are tested as their delivery slices become available,
not treated as implemented merely because their names have lexer tokens.

**Test organization and execution**

- Use the dedicated JUnit 4 class,
  `parse/src/test/java/sql/walker/SqlEventWalkerDatetimeFunctionPlacementTests.java`,
  extending `AbstractSqlParseEventWalkerTest`, for the DATEADD suite. Keep new
  datetime placement tests out of `SqlEventWalkerCoreSelectFromAliasingTests`.
  Put reduced cross-function tests in a second dedicated class,
  `SqlEventWalkerDatetimeFunctionFamilyTests`, in the same directory. The
  original `SqlEventWalkerSnowflakeDatetimeManipulationTests` was renamed to
  the placement class; its existing tests seed, rather than complete, the suite.
- Keep endpoint permutations together in
  `SqlEventWalkerDatetimeFunctionSnippetTests`; add a separate class only if a
  future endpoint genuinely needs substantially different setup. DATEADD
  currently has one case each for PREDICAND, CONDITION, QUERY, TUPLE, IN_LIST,
  JOIN_EXTENSION, VALUES, INSERT, UPDATE, DELETE, DDL, and SCRIPT. These tests
  use the real endpoint and six complete literal output goldens; the DELETE case
  also asserts its expected Snowflake warning at its source position.
- For **every** planned datetime invocation, add one test for each of those
  12 endpoints as that invocation's implementation becomes available:
  TIMEADD, TIMESTAMPADD, DATEDIFF, TIMEDIFF, TIMESTAMPDIFF, DATE_TRUNC,
  datetime TRUNC, TIME_SLICE, LAST_DAY, NEXT_DAY, PREVIOUS_DAY, DATE_PART,
  EXTRACT, DATE_BIN, AGE, MAKE_DATE, MAKE_TIME, MAKE_TIMESTAMP, MAKE_TIMESTAMPTZ,
  MAKE_INTERVAL, JUSTIFY_DAYS, JUSTIFY_HOURS, JUSTIFY_INTERVAL, CLOCK_TIMESTAMP,
  STATEMENT_TIMESTAMP, TRANSACTION_TIMESTAMP, and TIMEOFDAY. DATEADD's
  permutations are already covered; SQL statement placement is covered
  separately in the placement suite. Each case must assert the function's own
  invocation/semantic AST, not the current generic fallback. Use appropriate
  argument and result types for each endpoint and its surrounding comparison,
  and assert any expected dialect diagnostic rather than suppressing it.
  Complete DATE_PART/EXTRACT form-specific checks under their own actions;
  one endpoint case per function is the minimum, not a substitute for
  required syntax-form tests. Do not mark the remaining permutations complete
  until the relevant function implementation and runnable goldens exist.
- The COLUMN endpoint accepts only a column reference/substitution, LITERAL
  accepts literal values, and TRUNCATE accepts a truncate statement: none
  accepts a datetime function as an expression. Do not manufacture positive
  datetime-function cases for those endpoints. DDL is applicable through a
  query-containing form such as CREATE VIEW AS SELECT; TUPLE is applicable
  through a subquery without a relation alias at the endpoint root.
- DATEADD previously followed generic function parsing in CONDITION, IN_LIST,
  JOIN_EXTENSION, VALUES, UPDATE, and DELETE expression positions. The shared
  nonparenthesized expression grammar now selects `datetime_function` before
  generic invocation, so the part is metadata rather than a phantom column.
  Preserve qualified same-name UDF handling and existing token numbers.
- Follow [SqlEventWalkerCoreSelectFromAliasingTests.java](../src/test/java/sql/walker/SqlEventWalkerCoreSelectFromAliasingTests.java)
  specifically `basicSelectListQuotedNumericPrefixColumnTest` as the structural
  model for every diagnostic-free test case, and
  [AbstractSqlParseEventWalkerTest.java](../src/test/java/sql/walker/AbstractSqlParseEventWalkerTest.java)
  for parsing, walking, diagnostic, and path-assertion helpers. Use production
  SQL parsing/walking, not private symbol-helper method harnesses.
- Build and review the standalone cases first, then scopes/clauses, DML, set
  operations, and cross-function samples. Run only the current new class or
  selected methods on JDK 21 using the established isolated Maven build when
  needed. Do not run broad cohorts or the full workspace suite during each
  construction batch. Report actual executed cases and failures separately
  from remaining planned coverage; do not mark unimplemented families green.
- Existing token-ID goldens are user-edited only. The new suite may introduce
  new literal expectations, but never bulk-refresh existing expectations.
  Update the newly added permissive unknown-DATEADD-part test specifically when
  the FATAL contract is implemented; preserve unrelated EXTRACT behavior.

**Reusable query ingredients**

- Keep the three original one-line queries exactly in scenarios 1-3 so their
  token locations remain reproducible. Use distinct output aliases in larger
  SELECT lists, including canonical/alias coverage lists.
- For the argument matrix, use `L`, `CQ`, `CU`, `FQ`, and `FU` for literal,
  qualified column, unqualified column, qualified multi-column formula, and
  unqualified multi-column formula. INCREMENT examples are `2`, `a.offset_days`,
  `offset_days`, `a.offset_days + a.extra_days * 2`, and
  `offset_days + extra_days * 2`. VALUE examples are `DATE '2026-01-01'`,
  `a.created_at`, `created_at`, `a.created_at + (a.base_days + a.adjust_days)`,
  and `created_at + (base_days + adjust_days)`. These use date-plus-numeric
  arithmetic, not addition of two date columns.
- Run all unqualified positive physical-column cases against one table.
  Physical-table ambiguity is tested separately with two visible sources.
  In cross-table qualified formulas deliberately split references between
  `events a` and `accounts b`; do not accidentally turn a positive case into
  an ambiguous one by dropping a qualifier.
- Derived tables and CTEs must explicitly export every intended input under
  known column names. Give overlapping names to both exports for ambiguity
  tests, and distinct names for uniquely resolvable two-source tests. Do not
  use `SELECT *` to hide interface assumptions.
- Use `<increment>` / `<date value>` for unprefixed predicand substitutions and
  `a.<increment column>` / `a.<date column>` for qualified column substitutions.
  Use distinct variable names for different types; do not introduce a
  same-name/different-type conflict into a positive test.
- Query wrappers must remain valid in their clause: DATEADD is an expression,
  not a condition. For WHERE/ON/HAVING/QUALIFY use an explicit comparison;
  for grouped queries group the necessary inputs or use valid aggregates;
  for scalar queries select one output and formulate a single-row result.
  Keep set-op branch output counts aligned and use explicit parentheses for
  intended nesting. Start DML from an existing accepted repository syntax,
  rather than inventing UPDATE/DELETE alias or FROM/USING forms.

**Required assertion contract**

- For each diagnostic-free concrete SQL case, use `parse(query)`,
  `runParsertest(query, parser)`, and `assertNoWalkerDiagnostics(extractor)`
  as in `basicSelectListQuotedNumericPrefixColumnTest`. Provide independent,
  explicit full golden expectations for all six outputs: AST
  (`getAsTree()`), output interface (`getInterface()`), substitutions
  (`getSubstitutionsMap()`), global table dictionary
  (`getTableColumnDictionaryMap()`), global query dictionary
  (`getQueryColumnDictionaryMap()`), and full symbol table
  (`getSymbolTable()`). Include exact token IDs/positions in the dictionary and
  symbol goldens, and assert empty objects explicitly. This applies to every
  SQL case, including cases grouped in loops; a few subtree or presence checks
  cannot substitute for these six goldens. Path assertions may supplement, not
  replace, full-object goldens, even for large or deeply nested queries.
- For expected diagnostics use the standard position-based methods on
  `AbstractSqlParseEventWalkerTest`, preferably
  `assertFatalDiagnosticAtPositionWithFullMessage` for FATAL cases. Supply the
  exact full message, diagnostic code, token text, line, and character position
  so a casual reader can understand the reported failure; assert severity and
  the exact expected diagnostic set/count as well. Use corresponding existing
  severity/position helpers for non-FATAL diagnostics. Assert relevant outputs
  for diagnostic cases as far as the error-processing contract guarantees them;
  never treat a FATAL's partial output as a successful parse.
- For every concrete positive case assert zero syntax errors and no unexpected
  diagnostics. Once affinity is implemented, a Snowflake DATEADD warning is
  expected, not a reason to require an empty diagnostic list. Assert the exact
  expected warning/error/FATAL set and the relevant dialect counters; keep
  implementation-stage gaps visible rather than weakening final expectations.
- Assert the complete AST for every query.
  Every DATEADD must have `datetime_function`, an authored-case `function_name`,
  `level={canonical,text,form}`, `increment`, and `value`, with normal expression
  nodes, correct precedence, and no raw `Type=`/numbered grammar wrappers.
- Assert output names/order, substitutions map, global table/query
  dictionaries with all occurrence tokens, and the complete relevant published
  `def_*` payloads: `table_alias`, `context_list`, per-scope dictionaries,
  `interface`, and clause/DML buckets as applicable. Assert genuinely empty
  artifacts as empty, not by omitting them.
- INCREMENT and VALUE columns must both enter normal qualification, validation,
  dictionaries, and lineage. Clause-only columns belong in the appropriate
  filters/group/order/window/assignment bucket, not automatically in the output
  interface. A parent sees child exports through the child `interface`, never
  by flattening hidden grandchild columns into the parent.
- Assert that metadata-only level/weekday strings are absent from physical
  columns, query-column references, lineage, and unresolved-reference errors.
  Do not globally ban names such as `year`: a real expression column named
  `year` must still be collected.
- Negative tests must assert exact diagnostic code, severity, full expected
  message, token text, source line/character position, and diagnostic count,
  using existing helpers where available. Assert remaining AST/symbol payloads
  only to the extent guaranteed by the existing error-processing contract;
  do not invent successful resolution after a FATAL.
- Prefer [generate_symbol_path_assertions.py](../../tools/generate_symbol_path_assertions.py)
  over manually copying large path goldens: put the exact SQL in a temporary
  file and run `python3 tools/generate_symbol_path_assertions.py --sql-file /tmp/query.sql`
  from the repository root, selecting `--root` and `--path-regex` for the
  relevant maps/subpaths. Respect the current isolated-build/output setup
  before using the generator, whose default build targets ordinary Maven
  output. Use emitted `expected...` values and `assertSymbolTreePathEquals(...)`
  lines, then check them against the documented scope/collection rules.
  Generated current output is not proof of correct behavior. Fix the new
  implementation if it violates those rules; do not bless a defect as a golden.
  Remove temporary SQL/probe files after use.
- For nested/set-op cases also follow
  [symbol-table-bucket-reference.md](../../docs/vendor/pss-parse/5.1.3/symbol-table-bucket-reference.md)
  and [set-op-convert-egress-scoping.mdc](../../.cursor/rules/set-op-convert-egress-scoping.mdc):
  assert branch-local provenance and sibling isolation through observable
  published scopes, with no post-bundle fallback assumptions.

**Comprehensive DATEADD scenarios**

1. **Original qualified VALUE.** Parse exactly
   `select dateadd(year, 1, a.created_at) as shifted from events a`.
   Golden the entire AST, dictionaries, interface, aliases, and symbol table;
   YEAR is metadata and only `created_at` contributes output lineage.
2. **Original unqualified VALUE.** Parse exactly
   `select dateadd(year, 1, created_at) as shifted from events a`.
   Preserve the unqualified AST reference and assert normal symbol resolution
   to `events`, including the authored `created_at` occurrence location.
3. **Original joined formula INCREMENT / literal VALUE.** Parse exactly
   `select dateadd(day, a.offset_days + b.extra_days * 2, DATE '2026-01-01') as shifted from events a join accounts b on a.account_id = b.id`.
   Assert multiplication precedence, typed date literal, both increment
   dependencies, and join-key filter dependencies separately.
4. **Original ambiguous VALUE.** Parse exactly
   `select dateadd(year, 1, created_at) as shifted from events a join accounts b on a.account_id = b.id`.
   Assert `AMBIGUOUS_COLUMN_REFERENCE` SEVERE_WARNING and
   `UNRESOLVED_UNQUALIFIED_COLUMNS` ERROR at the expected occurrences, no
   level-as-column diagnostics, and normal collection of qualified join keys.
5. **Complete standalone argument-shape matrix.** Cross each INCREMENT shape
   `L/CQ/CU/FQ/FU` with each VALUE shape `L/CQ/CU/FQ/FU`, omitting only `L/L`:
   24 explicit cases. Include all four simple column qualification pairs
   `CQ/CQ`, `CQ/CU`, `CU/CQ`, `CU/CU`, formulas in either slot alone, formulas
   against a column in the other slot, and formulas in both slots. Cases
   already covered by scenarios 1-3 need not be duplicated. Assert every input
   occurrence and the union of both slots' output dependencies.
6. **All-literal control.** Add `dateadd(day, 2, DATE '2026-01-01')` without a
   FROM clause; assert a typed literal AST, no source-column dependencies, and
   the normal constant-output interface. This is the optional control from
   the earlier proposal, now explicitly included in this development inventory.
7. **Joined qualified source split.** Use two joined tables with INCREMENT
   columns from one and VALUE columns from the other, then qualified formulas
   spanning both tables in each slot. Assert precise per-table provenance.
8. **Ambiguous INCREMENT only.** Leave the increment column unqualified with
   two physical sources, while VALUE is qualified or literal; assert only
   the increment ambiguity/unresolved reference and correct VALUE resolution.
9. **Both slots ambiguous.** Use distinct unqualified increment/value names
   with two physical sources; assert both names and all offending locations
   in the repository's normal per-occurrence/aggregate diagnostic structure.
10. **Mixed qualification within formulas.** Put qualified and unqualified
    terms in INCREMENT, VALUE, and both; use separate positive one-source and
    negative two-source cases. Only genuinely ambiguous terms should fail.
11. **Unique versus overlapping derived-source exports.** Join two explicit
    derived tables, then repeat with two CTE sources. Unqualified unique output
    names must resolve; names exported by both sources must be ambiguous.
12. **Hidden or nonexistent source columns.** Reference a non-exported
    underlying column, a missing derived/CTE output, and an unknown table alias
    in separate cases. Assert standard existing diagnostics; DATEADD must not
    bypass interface visibility or qualification checks.
13. **Unprefixed predicand substitutions.** Put a variable in INCREMENT only,
    VALUE only, and both. Assert `type=predicand` in each AST and substitutions
    map; do not treat these as fixed source-column names.
14. **Qualified COLUMN substitutions.** Repeat those three arrangements with
    `a.<increment column>` and `a.<date column>`, including different joined
    source aliases. Assert `type=column`, qualifier preservation, dictionary
    occurrences, and normal column-substitution lineage.
15. **Mixed substitution kinds across slots.** Cover predicand INCREMENT /
    column VALUE and column INCREMENT / predicand VALUE, with independent
    names and no type contamination between slots.
16. **Substitutions inside formulas.** In either slot and both slots combine
    ordinary columns, unprefixed variables, and qualified column variables.
    Include same-kind and mixed-kind formulas; preserve each variable's own
    type and collect only the appropriate column-bearing dependencies.
17. **All canonical levels in one SELECT.** Use 11 uniquely aliased DATEADD
    outputs for YEAR, QUARTER, MONTH, WEEK, DAY, HOUR, MINUTE, SECOND,
    MILLISECOND, MICROSECOND, and NANOSECOND. Use a timestamp VALUE suitable
    for subday units. Explicitly assert all 11 canonical/text/form triples
    and independent output interfaces, not merely the SELECT item count.
18. **Every documented registered alias.** Maintain explicit alias-to-canonical
    expectations verified against the Snowflake reference. Group manageable
    batches of uniquely aliased DATEADD SELECT items; cover every registered
    spelling, not just one per canonical name. Compare registry membership
    against the independently expected set as well as parser AST results so
    an omitted or wrongly mapped alias cannot make the coverage self-fulfilling.
19. **Case and level source forms.** Use representative canonical and alias
    spellings in mixed case and single-quoted form, plus unquoted keyword and
    identifier forms. Assert exact authored text and actual grammar form.
    Do not assume every alias lexes as IDENTIFIER or every canonical as KEYWORD.
20. **Level-like real columns.** Use `a.year` and `a.day` in argument formulas
    alongside YEAR/DAY level metadata. Assert real column collection and no
    phantom occurrence for the first argument.
21. **Invalid level forms.** After the diagnostic prerequisite, test an unknown
    identifier, unknown quoted text, misspelling, empty quoted text, and a
    verified part unsupported by DATEADD but supported by another family.
    Expect the dedicated FATAL at the level token, not an unresolved column.
22. **Invalid nested level location.** Test an invalid inner level with a valid
    outer call, then invalid outer with valid inner. Assert the diagnostic
    identifies the correct invocation/location; keep valid and invalid calls
    together once to detect cross-call state contamination.
23. **Numeric edge expression shapes.** Use zero, negative literal, unary minus
    on a column, and parenthesized multi-column INCREMENT expressions; assert
    sign/parentheses/precedence and unchanged normal dependency collection.
24. **Repeated references and independent sibling calls.** Repeat the same
    column within/between slots and use several DATEADD outputs from different
    sources. Assert all dictionary occurrence tokens, existing lineage
    deduplication semantics, and no dependencies leaking between outputs.
25. **DATEADD inside ordinary functions.** Put DATEADD in different legal
    parameter positions of COALESCE and another existing supported function,
    plus multiple sibling DATEADD arguments with distinct parts/sources.
    Preserve the enclosing ordinary-function AST and each datetime subtree.
26. **Ordinary functions inside DATEADD.** Use numeric functions in INCREMENT
    and date-valued functions in VALUE, separately and together. Include
    columns and substitutions inside those inner functions.
27. **DATEADD VALUE contains DATEADD.** Use
    `dateadd(month, 2, dateadd(day, 3, a.created_at))`, then a version whose
    inner/outer increments use distinct column formulas and variables. Assert
    different inner/outer part and increment metadata and complete dependencies.
28. **CASE embedding.** Put DATEADD in THEN/ELSE expressions, then put CASE
    expressions in its argument slots. Assert condition/filter and resulting
    expression dependencies according to existing CASE collection behavior.
29. **Derived-table producer.** Put each of scenarios 1-3 inside a FROM
    subquery and consume its `shifted` output in an outer SELECT. Assert the
    parent reference is to the child output, with traceable inner dependencies.
30. **Derived-table consumer and multiple layers.** Export raw argument columns
    from a derived table, call DATEADD outside it, then repeat through two
    nested layers with renamed exports. Assert every intermediate interface
    and reject direct access to hidden leaf names.
31. **Join placements.** Put the anchor calls in the SELECT of a joined query,
    inside left/right joined derived tables, and in an ON comparison. Use
    INNER and representative LEFT JOIN coverage; verify output versus join
    filter buckets rather than varying every join spelling.
32. **Scalar-query placements.** Put an anchor DATEADD inside a single-output,
    single-row scalar subquery in SELECT; separately let a scalar query supply
    INCREMENT or VALUE to an outer DATEADD. Use aggregates or constrained
    sources to make the scalar formulation sensible, and assert dependent
    query linkage and scope-local inputs.
32a. **Correlated scalar arguments.** Supply INCREMENT and VALUE independently
    with single-output, single-row scalar subqueries, including both uncorrelated
    controls and subqueries that reference columns from the enclosing query.
    Also put separate correlated scalar subqueries in both slots of one DATEADD.
    Assert each argument's scalar AST and dependent-query linkage, the inner
    local and correlated references in their proper scopes and roles, and the
    outer output lineage without flattening or leaking sibling-query inputs.
    Use aggregates or otherwise constrain each subquery to a single row.
33. **CTE producer, consumer, and chain.** Put each anchor in a CTE body and
    consume its output; call DATEADD over raw CTE exports; then carry its result
    through chained CTEs with renamed columns. Assert ordered context visibility,
    published interfaces, and physical versus query dictionary separation.
34. **Correlated subquery and shadowing.** Mix a local INCREMENT with outer
    VALUE and reverse them, then use an inner alias that shadows an outer
    alias. Assert the repository's normal nearest-visible-scope resolution,
    not flattened or sibling visibility.
35. **WHERE predicates.** Use anchor calls in explicit comparisons and BETWEEN;
    also use DATEADD within an IN or EXISTS predicate subquery. Assert filter
    dependencies and `dependent_queries`, including inputs not projected.
36. **GROUP BY and HAVING.** Group by a DATEADD expression with matching SELECT
    output; separately compare an aggregate-based DATEADD in HAVING. Assert
    `grouped_by`, filters, and output dependencies independently.
37. **ORDER BY expressions and output aliases.** Order directly by DATEADD,
    then by its SELECT alias. Assert direct source references versus the
    existing output-alias resolution contract in `ordered_by`.
38. **Window PARTITION BY / ORDER BY and QUALIFY.** Place DATEADD in window
    partition/order expressions and in a valid QUALIFY comparison alongside
    a window expression. Assert `window_partition_by`, `window_ordered_by`,
    filters, and expected Snowflake affinity without spurious output columns.
39. **INSERT source.** Place each anchor in INSERT ... SELECT with explicit
    target columns; include representative CTE/derived sources. Assert target
    mapping, source scope, and lineage from both expression slots.
40. **UPDATE assignment and predicate.** Place DATEADD in SET, then in WHERE,
    then both. Include qualified column and substitution inputs using an
    existing accepted UPDATE form. Assert target/assignment versus filter
    dependencies and the complete statement's expected dialect diagnostics.
41. **DELETE predicate and subquery.** Use DATEADD in a direct WHERE comparison
    and in an EXISTS/scalar predicate subquery, based on an existing accepted
    DELETE form. Assert source/filter/correlation scopes; DELETE does not gain
    an invented SELECT output interface.
42. **UNION and UNION ALL branches.** Put the anchor in first branch, later
    branch, and both branches, with compatible outputs. Include a three-branch
    case where branches 1/3 use a CTE and branch 2 uses a different physical
    table. Assert branch-local dictionaries and no sibling provenance leakage.
43. **EXCEPT branches.** Put DATEADD on left, right, and both sides; keep
    outputs aligned. Assert operand order, composite interface, and branch
    isolation rather than treating EXCEPT as interchangeable with UNION.
44. **INTERSECT branches.** Repeat first/later/both branch placements and assert
    its own composite scope/interface and source provenance.
45. **Nested set operations.** Cover same-operator nesting and mixed nesting:
    `(A UNION B) EXCEPT C`, `A UNION (B INTERSECT C)`, and
    `(A EXCEPT B) INTERSECT (C UNION ALL D)`, formulated with the grammar's
    accepted parenthesized query syntax. Distribute different anchor calls
    across leaves and inspect every intermediate set-op interface.
46. **Set-op consumers and DML composition.** Consume a set-op result in a
    derived table, CTE, and INSERT source; call DATEADD on exported result
    columns as well as inside leaf branches. Assert all intervening exports,
    target mappings where applicable, and absence of hidden/sibling inputs.
47. **Qualified same-name UDF control.** Keep
    `custom.dateadd(a.part, 1, a.created_at)` generic and neutral unless another
    construct establishes affinity. Its `a.part` is an ordinary column, not
    level metadata; do not apply built-in level validation to it.
48. **Statement affinity and reset.** Once affinity is implemented, verify
    repeated/nested DATEADD counts with one Snowflake warning, composition
    with a PostgreSQL-only construct in either order yielding the existing
    mixed-dialect FATAL, and SCRIPT statements resetting independently.
    Assert exact counts from the defined collector behavior, not a guessed
    one-hit-per-query rule.

**Reduced replication for other datetime functions**

Do not repeat the 24-shape DATEADD matrix or every query wrapper for each
sibling function. The per-function actions 66-97 below supersede the earlier
reduced-only scope of scenarios 49-64: distinct-signature primaries now receive
deep context suites; sibling names receive compact suites without repeated
placement matrices. Retain the signature-specific cases below, moving wrapper
coverage to the appropriate primary instead of adding it to every sibling.
Preserve actual signatures:
diff has two datetime expressions, not an increment; day-boundary metadata is
not a column; PostgreSQL constructors have expression fields, not part slots.
`TIMEADD` is the add alias; `TIMEDIFF` is the difference alias (not `TIMEDD`).
For each listed invocation at least one test must check its own name/signature,
named AST fields, relevant dictionaries/interfaces, and affinity. Verify exact
supported forms/parts against the linked engine reference before implementation.
Reduced alias sampling does not authorize an incomplete production registry.

49. **TIMEADD representative add subset.** Cover qualified column INCREMENT /
    VALUE, unqualified one-source inputs, formula INCREMENT, and both
    substitution kinds across its two expression slots. Use HOUR/MINUTE/SECOND
    and TIME-valued examples, not DAY on a TIME literal. Add one joined ON
    comparison and one nested ordinary-function case. Include one valid alias
    spelling and invalid-level FATAL coverage once family validation exists.
50. **TIMESTAMPADD representative add subset.** Cover formulas in both slots,
    mixed predicand/column substitutions, DATEADD nested in VALUE with a
    different part, a CTE consumer, and an INSERT source containing UNION ALL.
    Assert it lexes/parses as TIMESTAMPADD, preserves that invocation, and
    uses the add-family `level/increment/value` signature. Include a canonical,
    an alias, and invalid-level coverage.
51. **DATEDIFF representative diff subset.** Cover literal/column and
    column/column `left/right`, a multi-column date-valued expression, and
    predicand versus column substitutions in either expression slot. Add one
    ambiguous unqualified operand and one joined query. Assert authored
    left/right order with distinct operands, part metadata exclusion, and
    supported/unsupported part handling; never reuse increment assertions.
52. **TIMEDIFF and TIMESTAMPDIFF alias samples.** Give each a distinct-operand
    baseline and substitution case. Use TIMEDIFF in HAVING with appropriate
    time expressions, and TIMESTAMPDIFF in a CTE over an INTERSECT source.
    Preserve invocation spelling and left/right order; include one part alias
    and one invalid part per invocation, with suitable time/timestamp values.
53. **DATE_TRUNC and datetime TRUNC argument-order samples.** For each, use a
    qualified VALUE, a value formula, and both variable kinds in the expression
    slot. Contrast `DATE_TRUNC('month', value)` with `TRUNC(value, 'month')`.
    Put one in GROUP BY and the other in ORDER BY over a derived table.
    Add family-specific invalid part coverage; verify shared versus
    target-specific affinity from the existing matrix, not function name alone.
54. **TRUNC overload and DDL controls.** Keep numeric `TRUNC(number[, scale])`
    neutral and preserve its established generic AST. One-argument ambiguous
    `TRUNC(value)` stays generic without distinguishing semantic evidence.
    Keep DDL TRUNCATE separate. These are compatibility tests, not permission
    to infer datetime types from a column name.
55. **TIME_SLICE signature samples.** Cover required arguments and omitted/
    explicit supported boundary choices, formula/column slice length, a VALUE
    column substitution, and a predicand variable. Use one window-order
    placement and one CTE projection; assert `value/slice_length/level/boundary`
    in SQL signature order, metadata exclusion, and documented invalid
    level/boundary handling rather than a guessed DATEADD part set.
56. **LAST_DAY optional-level samples.** Test omitted level and explicit
    supported level, a qualified VALUE, and both variable kinds. Place one
    case in UPDATE SET and another in a scalar subquery. Assert optional-field
    representation according to the agreed AST and reject unsupported levels
    through the family-specific validation contract.
57. **NEXT_DAY and PREVIOUS_DAY weekday samples.** Give each literal/column
    VALUE and documented weekday spelling variants, then one predicand and
    one column-substitution VALUE. Put one in DELETE WHERE and one in CASE.
    Assert `weekday` metadata instead of a column, correct invocation, and
    invalid-weekday diagnostics under their own validation contract.
58. **EXTRACT and DATE_PART unification samples.** For both invocations,
    implement the approved Phase 1 action above: accepted comma/FROM forms use the
    agreed datetime-family AST with explicit syntax retention. Sample shared
    and dialect-specific parts, quoted/bare canonical and alias spellings,
    normal source columns/formulas, both substitution kinds, one nested DATEADD
    VALUE, and qualified generic UDF controls. Assert no phantom-part columns,
    correct extraction-specific validation/affinity, and intentional reviewed
    EXTRACT/DATE_PART behavioral golden changes, not DATEADD's allowed-part set.
    Include typed datetime/interval sources, extended/timezone parts, and
    existing extraction endpoint coverage to verify no source semantics are lost.
59. **PostgreSQL DATE_BIN samples.** Use interval stride with distinct source
    and origin expressions, qualified/unqualified columns, and substitution
    examples in expression slots permitted by its grammar. Put one in a CTE
    and one in a set-op branch. Assert `stride/source/origin`, interval literal
    representation, source order, lineage, and PostgreSQL affinity.
60. **PostgreSQL AGE overload samples.** Cover one-argument `value` and
    two-argument `left/right`, distinct qualified operands, unprefixed and
    qualified substitutions, and an ordinary-function wrapper. Use a
    correlated scalar-query case to exercise outer/local resolution without
    re-running the entire DATEADD placement matrix.
61. **PostgreSQL MAKE_* constructor samples.** Give MAKE_DATE, MAKE_TIME,
    MAKE_TIMESTAMP, and MAKE_TIMESTAMPTZ one explicit signature baseline each.
    Across these cases distribute formula fields, both substitution kinds,
    joined sources, a derived-table consumer, and     optional timezone coverage.
    Assert named numeric/timezone expression fields and normal source
    collection; `year`/`month` field names here are not level metadata.
62. **PostgreSQL MAKE_INTERVAL source-form samples.** Cover supported positional
    and named forms, omitted components, formulas, and both substitution kinds
    in component expressions. Preserve component mapping and source form for
    regeneration; use one chained-CTE case. Do not silently rearrange positional
    arguments or invent defaults in the AST.
63. **PostgreSQL JUSTIFY_* interval samples.** Give JUSTIFY_DAYS, JUSTIFY_HOURS,
    and JUSTIFY_INTERVAL a baseline each; distribute an interval-valued column,
    interval expression, predicand variable, qualified column variable, and
    nested invocation across those tests. Put one in an EXCEPT branch and
    assert normal interval-expression lineage and PostgreSQL affinity.
64. **PostgreSQL zero-argument clock samples.** Cover CLOCK_TIMESTAMP,
    STATEMENT_TIMESTAMP, TRANSACTION_TIMESTAMP, and TIMEOFDAY in one uniquely
    aliased SELECT. Assert each invocation has no invented argument fields or
    source columns. Include an ORDER BY alias and a neutral CURRENT_TIMESTAMP
    control; add only arity diagnostics defined by the implemented contract.
65. **Cross-family isolation, UDFs, and dialect composition.** Sample a
    Snowflake add/diff nesting, a shared truncation with dialect-specific part,
    and a PostgreSQL-only nested expression. Add schema-qualified same-name
    UDF controls for one function from each new family. Assert normal UDF
    column arguments, family-specific allowed-part boundaries, one warning per
    target, accurate hit counts, mixed-dialect FATAL in both orders, and SCRIPT
    reset. Do not treat cross-dialect composition as a positive clean statement.

#### Phase 1 additional steps: per-function build and test actions

**Approved 2026-11-12; partially implemented.** This section defines the
execution scope for each function individually. Scenarios 1-65 remain the
case-formulation reference; the profiles below govern which functions repeat
deep context coverage. Do not interpret the earlier reduced-family examples as
permission to omit a primary function's deep suite. A build action includes
dedicated recognition, named AST construction, signature-specific metadata
validation, normal expression collection/substitution typing, and the existing
dialect collector. Complete and run its tests before claiming that invocation
implemented. Deliver incrementally with Slices B-E, not as one large rewrite.

66. **Define the compact sibling suite (S).** For every parameterized sibling
    invocation, write separate simple SELECT cases for: literal construction;
    qualified columns; unqualified columns with one source; ambiguous
    unqualified columns with two physical sources; formulas; unprefixed
    predicand substitutions; qualified COLUMN substitutions; and mixed
    substitution kinds where at least two expression slots exist. Exercise each
    expression slot at least once, with distinct values to detect reordering.
    For multiple slots, ambiguity must be tested in each slot separately; one
    representative both-slots ambiguity case is sufficient. Include a
    canonical part, representative accepted alias and quoted/bare forms where
    supported, and the invalid-date-part FATAL for every invocation with a
    date-part slot. Include invalid weekday/boundary tests for those metadata
    slots under their own validation contracts. Do not manufacture a date-part
    error for functions without a part parameter. Add same-name qualified UDF
    control, exact affinity expectations, and signature-specific optional/arity
    cases. All cases use the full assertion contract above: exact named AST,
    dictionaries/occurrences, interface, substitutions, and diagnostics.
    No sibling repeats the placement matrix merely because its name differs.
    Zero-argument siblings instead use profile Z in actions 92-95.
67. **Define the deep primary suite (D).** Every primary gets S plus the
    scope/clause/DML/set-operation coverage in scenarios 25-46, adapted to its
    signature, not copied by textual replacement of DATEADD. For each primary
    formulate three anchors: qualified column expressions; one-source
    unqualified expressions; multi-column formulas with inputs split across
    qualified joined sources. Substitute these into scenarios 29-46, covering
    derived producer/consumer and multiple layers, joins/ON, scalar queries,
    CTE producer/consumer/chains, correlation/shadowing, WHERE/BETWEEN/IN/EXISTS,
    grouping/HAVING, ordering/aliases, window clauses, INSERT, UPDATE, DELETE,
    UNION/UNION ALL, EXCEPT, INTERSECT, nested mixed set operations, and set-op
    consumers. Retain the distinct first/later/both branch placements.
    Also include scenario 12 visibility failures, 13-16 substitution varieties,
    21-24 applicable validation/state/occurrence checks, and 47-48 UDF/affinity
    controls. Do not repeat DATEADD's 24-shape standalone Cartesian matrix:
    use the primary's own argument shapes, covering every expression field
    with a column, formula, and both substitution kinds. For metadata, cover
    its documented allowed values independently of DATEADD.
    Use type-sensible wrappers: compare extracted/diff results numerically,
    date outputs with dates, interval outputs with intervals, and text outputs
    with text. Nested same-family calls require compatible argument/result
    types; otherwise use a compatible enclosing function rather than inventing
    invalid SQL. For PostgreSQL primaries, choose PostgreSQL-compatible DML
    forms; a Snowflake-only QUALIFY wrapper is an explicit mixed-dialect
    negative, not a clean positive. Record any genuinely inapplicable case and
    its reason rather than silently dropping a category. Add a dedicated
    primary-family test class extending the existing abstract test base when
    the suite is developed; keep sibling cases beside their primary, except
    the already planned DATEADD/add-shape classes. Do not create a new test
    framework or generic query-generation abstraction.
68. **Build/test DATEADD (primary, D plus full scenarios 1-48).** Finish the
    invalid-part FATAL prerequisite, then the agreed comprehensive standalone
    and placement suite. This is the reference for normal collection in two
    expression slots after metadata.
69. **Build/test TIMEADD (sibling, S).** Reuse the add-family implementation and
    mini-suite with `level/increment/value`; preserve its authored `function_name`.
    Use time-appropriate examples. Required negatives include invalid part and
    separately ambiguous INCREMENT and VALUE. Do not duplicate context tests.
70. **Build/test TIMESTAMPADD (sibling, S).** Reuse the add-family implementation
    with timestamp inputs; assert its own token/name and invocation rather than
    DATEADD. Include invalid part and separate INCREMENT/VALUE ambiguity.
    Move scenario 50's placement wrappers to primary coverage, not this suite.
71. **Build/test DATEDIFF (add-shaped sibling, S).** Its layout is metadata plus
    two expressions, so reuse the compact two-expression coverage, not a new
    deep placement suite. Build `level/left/right`; both expression operands
    are datetime-valued. Test distinct operands/order, invalid part, one-source
    unqualified success, and separate left/right ambiguity.
72. **Build/test TIMEDIFF (add-shaped sibling, S).** Reuse diff construction with
    its own invocation and suitable time operands. Include its own invalid
    part, unqualified success, left/right ambiguity, and both variable types.
    No dedicated HAVING/CTE context matrix is required for this alias.
73. **Build/test TIMESTAMPDIFF (add-shaped sibling, S).** Reuse diff construction
    with timestamp operands and distinct left/right dependencies. Include its
    own invalid part, unqualified success, and ambiguity per operand; preserve
    invocation and do not duplicate INTERSECT/context suites.

#### Entry 1 execution checkpoint (2026-11-14): diff-family implementation plan (actions 71-73)

This checkpoint defines the concrete implementation and test inventory for the
next milestone: `DATEDIFF` / `TIMEDIFF` / `TIMESTAMPDIFF` with
`level/left/right` shaping and full left-right visibility validation.

**Status at checkpoint**
- DATEADD family milestone is complete through the scenario-65 boundary and
  validated in `SqlEventWalkerDatetimeFunctionPlacementTests`.
- Diff-family grammar, walker shaping, validation, and affinity are now present,
  with initial focused tests. The DF-01..DF-96 closure catalog below remains
  largely open and is the next verification target; truncation implementation
  no longer depends on waiting for that closure.

**Implementation plan (code changes)**
1. **Grammar routing and parser productions**
   - Ensure each diff invocation binds to dedicated datetime-function rules and
     preserves invocation name text/case.
2. **Walker shaping**
   - Build `datetime_function={function_name, level, left, right}` in SQL
     argument order.
   - Stamp predicand substitutions in `left` and `right` exactly as in add-family
     expression slots.
3. **Part/level validation**
   - Reuse canonical registry + alias normalization for `level`.
   - Emit family-specific invalid-part diagnostics at the level token location.
4. **Resolution/collection contracts**
   - Preserve existing table/query dictionary collection behavior for both
     expression slots; never collect `level` as a source column.
   - Preserve existing ambiguity/unresolved/hidden-interface diagnostics.
5. **Dialect affinity**
   - Register diff-family hits through the existing statement dialect collector;
     preserve mixed-dialect FATAL behavior and SCRIPT statement reset semantics.
6. **Regression guard**
   - Keep schema-qualified same-name UDF invocations generic (not datetime-family)
     unless the grammar specifically routes them as built-ins.

**Realistic-context test rule (applies to all scenarios below)**
- Every scenario should include lightweight natural SQL context around the
  target function: add 1-3 nearby non-target expressions/columns and at least
  one benign surrounding clause pattern (for example extra SELECT projections,
  JOIN filters, COALESCE/CASE wrapper, WHERE conjunct, ORDER BY alias use).
- Keep this deterministic (stable SQL text), not random at runtime, so goldens
  remain reproducible while still surfacing side effects.

**Diff-family scenario catalog (96 cases; IDs DF-01..DF-96)**

**A. Signature and level metadata (DF-01..DF-12)**
- DF-01 DATEDIFF baseline with qualified `left/right`.
- DF-02 DATEDIFF unqualified one-source operands.
- DF-03 TIMEDIFF baseline.
- DF-04 TIMESTAMPDIFF baseline.
- DF-05 Canonical shared levels captured in metadata.
- DF-06 Alias level spellings normalize but preserve authored text.
- DF-07 Quoted level (`STRING` form) preserved.
- DF-08 Identifier-form level preserved.
- DF-09 Invalid identifier level FATAL at level token.
- DF-10 Invalid quoted level FATAL at level token.
- DF-11 Mixed-case invocation name preserved.
- DF-12 Distinct `left/right` order preserved in AST and lineage.

**B. Left/right argument-shape matrix (DF-13..DF-37)**
- 25 explicit combinations using shapes `L`, `CQ`, `CU`, `FQ`, `FU`
  crossed for left x right.
- Include all four simple qualification pairs (`CQ/CQ`, `CQ/CU`, `CU/CQ`,
  `CU/CU`) and both formula-on-one-side / formula-on-both-sides variants.
- DF-13..DF-17 left `L` x right `L/CQ/CU/FQ/FU`
- DF-18..DF-22 left `CQ` x right `L/CQ/CU/FQ/FU`
- DF-23..DF-27 left `CU` x right `L/CQ/CU/FQ/FU`
- DF-28..DF-32 left `FQ` x right `L/CQ/CU/FQ/FU`
- DF-33..DF-37 left `FU` x right `L/CQ/CU/FQ/FU`

**C. Ambiguity and visibility failures (DF-38..DF-49)**
- DF-38 Ambiguous unqualified `left`.
- DF-39 Ambiguous unqualified `right`.
- DF-40 Both sides ambiguous (distinct names).
- DF-41 Unresolved unqualified `left`.
- DF-42 Unresolved unqualified `right`.
- DF-43 Unknown alias on `left`.
- DF-44 Unknown alias on `right`.
- DF-45 Hidden non-exported derived column in `left`.
- DF-46 Hidden non-exported derived column in `right`.
- DF-47 Missing derived export name in one slot.
- DF-48 Missing CTE export name in one slot.
- DF-49 Overlapping derived/CTE exports trigger ambiguity.

**D. Substitutions in left/right trees (DF-50..DF-67)**
- DF-50 Predicand substitution in `left` only.
- DF-51 Predicand substitution in `right` only.
- DF-52 Predicand substitutions in both slots.
- DF-53 Qualified column substitution in `left` only.
- DF-54 Qualified column substitution in `right` only.
- DF-55 Column substitutions in both slots.
- DF-56 Mixed kinds: predicand-left / column-right.
- DF-57 Mixed kinds: column-left / predicand-right.
- DF-58 Predicand substitutions inside `left` formula.
- DF-59 Predicand substitutions inside `right` formula.
- DF-60 Column substitutions inside `left` formula.
- DF-61 Column substitutions inside `right` formula.
- DF-62 Mixed substitutions + ordinary columns in `left` formula.
- DF-63 Mixed substitutions + ordinary columns in `right` formula.
- DF-64 Mixed substitutions in both formulas.
- DF-65 Joined-source substitution typing stays isolated.
- DF-66 Missing qualified column substitution remains fatal without join extension.
- DF-67 Same reference with join extension follows existing defer/warning policy.

**E. Nested and sibling isolation (DF-68..DF-76)**
- DF-68 DATEDIFF nested in ordinary function argument #1.
- DF-69 DATEDIFF nested in ordinary function argument #2.
- DF-70 Ordinary function nested in `left`.
- DF-71 Ordinary function nested in `right`.
- DF-72 Nested diff in `left` with valid outer diff.
- DF-73 Nested diff in `right` with valid outer diff.
- DF-74 Invalid inner level does not contaminate sibling diff expression.
- DF-75 Repeated references preserve existing occurrence semantics.
- DF-76 Distinct sibling diff outputs retain independent lineage.

**F. Producer/consumer and scope propagation (DF-77..DF-86)**
- DF-77 Derived-table producer exports diff result lineage.
- DF-78 Derived-table consumer computes diff from exports.
- DF-79 Two-layer derived rename chain.
- DF-80 CTE producer lineage.
- DF-81 CTE consumer lineage.
- DF-82 Chained CTE rename path.
- DF-83 Scalar subquery supplies `left`.
- DF-84 Scalar subquery supplies `right`.
- DF-85 Correlated scalar in `left`.
- DF-86 Correlated scalar in `right`.

**G. Clause/DML/set-op placement (DF-87..DF-94)**
- DF-87 WHERE placement.
- DF-88 HAVING placement.
- DF-89 ORDER BY expression + output alias placement.
- DF-90 Window PARTITION/ORDER placement.
- DF-91 INSERT ... SELECT placement.
- DF-92 UPDATE (SET and WHERE) placement.
- DF-93 DELETE (WHERE and EXISTS) placement.
- DF-94 UNION/EXCEPT/INTERSECT + nested set-op branch isolation.

**H. UDF controls and statement reset (DF-95..DF-96)**
- DF-95 Schema-qualified same-name UDF remains generic.
- DF-96 SCRIPT multi-statement reset and per-statement affinity isolation.

#### Diff-family verification update (2026-11-16)

Added focused verification coverage for the remaining highest-risk diff behavior
(independent `left`/`right` operand resolution) in
`SqlEventWalkerDatetimeFunctionPlacementTests`:

- [x] TIMEDIFF alias sample in HAVING with predicand substitution in `right`.
- [x] TIMESTAMPDIFF alias sample in CTE-over-INTERSECT with predicand substitution in `right`.
- [x] TIMEDIFF invalid part fatal with exact token/position/message assertions.
- [x] TIMESTAMPDIFF invalid part fatal with exact token/position/message assertions.
- [x] Completed Section A closure for diff family: quoted-level metadata and mixed-case invocation preservation for TIMEDIFF/TIMESTAMPDIFF, plus quoted invalid-part fatal coverage per invocation.
- [x] Completed Section B (`DF-13..DF-37`) 25-case left/right shape matrix with strict goldens (`L/CQ/CU/FQ/FU` × `L/CQ/CU/FQ/FU`).
- [x] Completed Section C (`DF-38..DF-49`) ambiguity/visibility failures, including unknown-alias operand failures, missing derived export handling, and overlapping CTE export ambiguity.
- [x] Completed Section D (`DF-50..DF-67`) substitution matrix for `left`/`right` trees (predicand/column/mixed/formula/joined-source) including join-extension fatal vs deferred behavior.
- [x] Completed Section E (`DF-68..DF-76`) nested/sibling isolation, including nested-diff-in-wrapper positions, wrapper-in-operand shapes, inner-invalid-part isolation against sibling diffs, repeated-reference occurrence semantics, and independent sibling output lineage.
- [x] Completed Section F (`DF-77..DF-86`) producer/consumer and scope propagation through derived producers/consumers, two-layer rename chains, CTE producer/consumer chains, and scalar/correlated scalar subqueries in both `left` and `right` operands.
- [x] Completed Section G (`DF-87..DF-94`) clause/DML/set-op placement for WHERE, HAVING, ORDER BY expression+alias, window PARTITION/ORDER, INSERT…SELECT, UPDATE SET+WHERE, DELETE WHERE+EXISTS, and set-op branch isolation.
- [x] Completed Section H (`DF-95..DF-96`) schema-qualified same-name UDF generic control for DATEDIFF and SCRIPT statement-level dialect-affinity reset/isolation verification.
- [x] Added DIFF-family snippet production: full DATEDIFF endpoint matrix (`PREDICAND`, `CONDITION`, `QUERY`, `TUPLE`, `IN_LIST`, `JOIN_EXTENSION`, `VALUES`, `INSERT`, `UPDATE`, `DELETE`, `DDL`, `SCRIPT`) plus TIMEDIFF/TIMESTAMPDIFF predicand substitution controls with strict AccessObject goldens.
- [x] Added missing prior-family snippet coverage with substitution-slot assertions for EXTRACT, DATE_PART, DATE_TRUNC, TIME_SLICE, LAST_DAY, NEXT_DAY, and PREVIOUS_DAY.
- [x] Added Postgres-family snippet endpoint matrices at the same depth for DATE_BIN, AGE, MAKE_TIMESTAMP, JUSTIFY_INTERVAL, and CLOCK_TIMESTAMP (`PREDICAND`, `CONDITION`, `QUERY`, `TUPLE`, `IN_LIST`, `JOIN_EXTENSION`, `VALUES`, `INSERT`, `UPDATE`, `DELETE`, `DDL`, `SCRIPT`) plus sibling predicand controls for MAKE_DATE/MAKE_TIME/MAKE_TIMESTAMPTZ/MAKE_INTERVAL, JUSTIFY_DAYS/JUSTIFY_HOURS, and STATEMENT_TIMESTAMP/TRANSACTION_TIMESTAMP/TIMEOFDAY.

These close the representative Entry 1 item-52 gaps for alias+substitution and
per-invocation invalid-part verification, and close Sections A-H of the
DF-01..DF-96 catalog.

**Remaining datetime-family scenario catalogs (planning checkpoint, 2026-11-14)**

> **Test-hosting recommendation (applies to all blocks below):**
> split the current large datetime placement class into family-focused classes.
> Suggested classes:
> - `SqlEventWalkerDatetimeTruncExtractTests`
> - `SqlEventWalkerDatetimeBoundarySliceTests`
> - `SqlEventWalkerDatetimePostgresDiffBinAgeTests`
> - `SqlEventWalkerDatetimeMakeConstructorTests`
> - `SqlEventWalkerDatetimeJustifyTests`
> - `SqlEventWalkerDatetimeClockTimestampTests`
> Keep only shared helpers in the existing placement base/test utilities.

### TRUNC/EXTRACT family plan (DATE_TRUNC, datetime TRUNC, DATE_PART, EXTRACT)
- **Core functionality scenarios (TE-C01..TE-C28)**
  - Invocation-shape ASTs for DATE_TRUNC/ TRUNC (`value/level`) and DATE_PART/EXTRACT (`level/value`)
  - Comma and FROM forms for DATE_PART/EXTRACT with syntax discriminator assertions
  - Canonical/text/form metadata coverage for keyword/identifier/string part forms
  - Column lineage for single-source, qualified multi-source, formula-wrapped value expressions
  - Placement core: SELECT/WHERE/HAVING/ORDER BY + derived/CTE producer-consumer
- **Malformed/failure scenarios (TE-M01..TE-M24)**
  - Invalid part diagnostics per invocation (no cross-function collapse)
  - Ambiguous/unqualified value diagnostics and unresolved behavior checks
  - Hidden/missing export failures through derived/CTE boundaries
  - TRUNC one-arg/numeric controls (must remain generic or numeric path as applicable)
- **Section D — mandatory snippet substitution coverage (fold into TE-M/TE-R execution)**
  - Add explicit snippet-endpoint tests for this family in `SqlEventWalkerDatetimeFunctionSnippetTests` (or family-specific snippet class if split) using endpoint forms where datetime functions are valid (`PREDICAND`, `CONDITION`, `QUERY`, `TUPLE`, `JOIN_EXTENSION`, `IN_LIST`, `VALUES`, `INSERT`, `UPDATE`, `DELETE`, `DDL`, `SCRIPT`).
  - Exercise substitution variables in every argument slot that accepts predicand/column trees (`value` and family-specific expression slots); verify substitution typing, table/query dictionary collection, and symbol-table payloads with full literal goldens.
  - Keep DATEADD-only snippet presence checks as insufficient for closure; each family must prove its own snippet-path behavior.
- **Complete regression scenarios (TE-R01..TE-R44)**
  - Nested/sibling isolation, substitution typing, set-op branch isolation
  - DML placements (INSERT/UPDATE/DELETE), window wrappers where legal
  - UDF same-name controls, multi-statement reset, mixed-dialect affinity checks
  - **Total planned: 96 (TE-01..TE-96)**
- **Build/test plan note:** implement in `SqlEventWalkerDatetimeTruncExtractTests`; keep parser/walker regressions targeted to this class while iterating.

**TRUNC/EXTRACT coverage reconciliation (2026-11-14, based on currently passing pre-existing suites)**

Validated suites:
- `SqlEventWalkerExtractTests`
- `SqlEventWalkerDatetimeFunctionPlacementTests`
- `SqlEventWalkerDatetimeFunctionSnippetTests`
- `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests`

Covered now (**mark complete**):
- [x] **TE-C01** Invocation-shape AST for EXTRACT/DATE_PART (FROM and comma forms) under `datetime_function`.
- [x] **TE-C02** Basic DATE_TRUNC and datetime TRUNC happy-path AST shape (`level/value` and `value/level` order normalization).
- [x] **TE-C03** EXTRACT/DATE_PART part-form coverage (`KEYWORD` + `STRING`) and broad part vocabulary (Snowflake/Postgres/timezone/epoch families).
- [x] **TE-C04** EXTRACT/DATE_PART source-shape coverage (column, qualified column, typed literal, parentheses, arithmetic, CAST, nested extract).
- [x] **TE-C05** Predicand substitution coverage for EXTRACT/DATE_PART source trees.
- [x] **TE-C06** Dialect-affinity warning coverage for EXTRACT/DATE_PART (including comma-form and nested combinations).
- [x] **TE-C07** Snippet endpoint presence for datetime-function family behavior (datetime snippet suite now includes DATEADD + DIFF family endpoint matrix and substitution-focused coverage for EXTRACT/DATE_PART/DATE_TRUNC and boundary/slice functions).

Still to add for this family (**not covered yet**):
- [ ] **TE-C08..TE-C12 (core placement expansion):** explicit WHERE/HAVING/ORDER BY/window/derived+CTE producer-consumer coverage for DATE_TRUNC and datetime TRUNC (not just EXTRACT/DATE_PART).
- [ ] **TE-M01..TE-M08:** invalid-part fatal diagnostics per invocation for DATE_TRUNC and datetime TRUNC, including token/position/message goldens.
- [ ] **TE-M09..TE-M14:** ambiguous/unqualified VALUE diagnostics for DATE_TRUNC/TRUNC across one-source/two-source/derived contexts.
- [ ] **TE-M15..TE-M20:** hidden-export and missing-output visibility failures for DATE_TRUNC/TRUNC through derived/CTE chains.
- [ ] **TE-M21..TE-M24:** TRUNC controls proving numeric/one-arg TRUNC remains generic (non-datetime) while datetime signature promotes.
- [ ] **TE-R01..TE-R18:** nested/sibling isolation for mixed EXTRACT/DATE_PART/DATE_TRUNC/TRUNC compositions.
- [ ] **TE-R19..TE-R30:** DML placement coverage (INSERT/UPDATE/DELETE) for TRUNC/EXTRACT signatures.
- [ ] **TE-R31..TE-R38:** set-op branch isolation and multi-layer derived/CTE rename-chain regression.
- [ ] **TE-R39..TE-R44:** schema-qualified same-name UDF controls + SCRIPT statement-reset/mixed-affinity regression.

Minimum parity implementation update (2026-11-14):
- [x] Implemented a dedicated parity batch in `SqlEventWalkerDatetimeTruncExtractTests` covering:
  - **TE-C08..TE-C12** core DATE_TRUNC/datetime TRUNC placement (WHERE/HAVING/ORDER BY/window + derived/CTE producer-consumer).
  - **TE-M01..TE-M24** malformed/diagnostic guards for invalid part fatal positions/messages, ambiguous/unqualified value behavior, hidden/missing derived outputs, and TRUNC generic-vs-datetime controls.
  - **TE-R19..TE-R30** DML placement guards (INSERT/UPDATE/DELETE) for TRUNC/EXTRACT signatures.
  - **TE-R39..TE-R44** schema-qualified same-name UDF controls, SCRIPT statement-reset, and mixed Snowflake/Postgres affinity fatal coverage.

Current effective status after the minimum-parity batch:
- **Completed:** the 12 core scenarios, all 24 malformed/diagnostic scenarios,
  DML scenarios TE-R19..TE-R30, and UDF/reset/affinity scenarios TE-R39..TE-R44.
- **Remaining:** none for the TRUNC/EXTRACT Slice C deep-regression block.
  TE-R01..TE-R18 nested/sibling isolation and TE-R31..TE-R38 set-operation
  plus multi-layer rename-chain scenarios are now implemented in
  `SqlEventWalkerDatetimeTruncExtractTests` and passing.

### Datetime-family golden assertion rule (applies to remaining slices)
- Family test classes must assert **both**:
  1) `SqlParserAccess` snippet snapshots (`getSnippet()` trees/dictionaries/symbol table), and
  2) direct walker extractor snapshots (`runParsertest` / `runScriptParsertest` outputs).
- Exception: `SCRIPT` `arrayOutputCollectors` may differ because `SqlParserAccess` enriches collector payloads with
  statement-level warning context; assert collector equality on the access-layer snapshot and assert extractor collectors for non-`SCRIPT` endpoints.
- This dual assertion pattern is now applied in:
  - `SqlEventWalkerDatetimeTruncExtractTests` (Slice C),
  - `SqlEventWalkerDatetimeBoundarySliceTests` (Slice D),
  and should be used for remaining Entry 1 family test classes (Slices E/F work).

### Snowflake boundary/slice family plan (TIME_SLICE, LAST_DAY, NEXT_DAY, PREVIOUS_DAY)
- **Core functionality scenarios (BS-C01..BS-C24)**
  - TIME_SLICE signature (`value/slice_length/level/[boundary]`) with omitted/explicit boundary
  - LAST_DAY optional level behavior with omitted-level semantics preserved
  - NEXT_DAY/PREVIOUS_DAY weekday handling with dedicated weekday metadata
  - Normal column lineage/visibility in each expression slot
  - Core placement and two-level derived/CTE propagation
- **Malformed/failure scenarios (BS-M01..BS-M20)**
  - Invalid level diagnostics for TIME_SLICE/LAST_DAY only
  - Invalid boundary diagnostics for TIME_SLICE
  - Invalid weekday diagnostics for NEXT_DAY/PREVIOUS_DAY
  - Ambiguous/unqualified value and export-visibility failures
- **Section D — mandatory snippet substitution coverage (fold into BS-M/BS-R execution)**
  - Add snippet-endpoint tests for this family across the supported snippet endpoints, including `SCRIPT` multi-statement reset behavior.
  - Validate substitutions in all predicand-capable slots (`value`, `slice_length`, and optional boundary/weekday positions where grammar permits expression/substitution forms) with exact golden assertions for AST/interface/dictionaries/symbol tables.
  - Include negative snippet cases for invalid level/boundary/weekday diagnostics with exact position/message assertions.
- **Complete regression scenarios (BS-R01..BS-R36)**
  - Substitutions in value/length/weekday slots, nested function wrappers
  - Clause/DML/set-op placement and sibling isolation
  - UDF controls and statement reset
  - **Total planned: 80 (BS-01..BS-80)**
- **Build/test plan note:** implement in `SqlEventWalkerDatetimeBoundarySliceTests`; keep family-specific negative diagnostics goldened in this class.
- **Default dialect hook note:** register each Snowflake-only grammar path in `SqlGrammarDialectRuleRegistry`, assert the Snowflake warning at its source position in every diagnostic test, and include mixed-dialect fatal checks in regression coverage.

Minimum parity implementation update (2026-11-14):
- [x] Added dedicated grammar + walker coverage for `TIME_SLICE`, `LAST_DAY`,
  `NEXT_DAY`, and `PREVIOUS_DAY` under `datetime_function`.
- [x] Registered Snowflake-only grammar paths for this family in
  `SqlGrammarDialectRuleRegistry` so statement-dialect warnings/fatals follow
  the standard counter/affinity mechanism.
- [x] Implemented minimum parity suite in
  `SqlEventWalkerDatetimeBoundarySliceTests` using one query per test method
  and full literal goldens for AST/interface/substitutions/table dictionary/
  query dictionary/symbol table/array collectors.
- [x] Implemented core placement parity set for boundary/slice family:
  WHERE/HAVING/ORDER BY/window + derived/CTE producer-consumer.
- [x] Implemented malformed/diagnostic parity set for boundary/slice family:
  invalid level, invalid boundary, invalid weekday, ambiguous unqualified value,
  and missing derived output diagnostics (with position/message assertions).
- [x] Implemented minimum regression parity set for boundary/slice family:
  INSERT/UPDATE/DELETE placements, schema-qualified same-name UDF generic-path
  control, SCRIPT statement reset behavior, and mixed-affinity fatal checks.
- [x] Implemented first deep-regression closure batch for boundary/slice family:
  nested/sibling isolation compositions plus explicit valid-slot substitution
  lineage checks (no part-slot leakage) with full access-object and direct
  extractor golden assertions.
- [x] Implemented set-operation branch-isolation coverage for boundary/slice
  functions (`UNION ALL`, `INTERSECT`, `EXCEPT`) with per-branch lineage
  verification and interface/set-op symbol-table assertions.
- [x] Implemented remaining multi-layer scope/rename propagation coverage for
  boundary/slice functions, including chained CTE rename propagation and
  derived set-op consumer lineage checks.
- [x] Moved boundary/weekday value sets into shared grammar registry
  (`SnowflakeDatetimeValueRegistry`) so external consumers can access these
  validity lists independently of the event walker implementation.

#### BS matrix accounting (strict traceability, 2026-11-16)

Canonical BS-ID mapping used for closure (every BS ID is bound to one or more
implemented tests; no unassigned IDs remain):

| BS IDs | Coverage theme | Evidence tests |
|---|---|---|
| BS-C01..BS-C05 | TIME_SLICE core signature + value/slice_length lineage in clause placement | `coreTimeSliceInWhereCollectsFilters` |
| BS-C06..BS-C09 | LAST_DAY placement + grouped/filter lineage | `coreLastDayInHavingCollectsGroupedAndFilterDependencies` |
| BS-C10..BS-C12 | NEXT_DAY ordered-by lineage | `coreNextDayInOrderByTracksOrderedBy` |
| BS-C13..BS-C15 | PREVIOUS_DAY window partition/order lineage | `corePreviousDayInWindowTracksWindowBuckets` |
| BS-C16..BS-C20 | Two-level producer/consumer propagation (CTE + derived) | `coreTimeSliceAcrossCteAndDerivedProducerConsumer` |
| BS-C21..BS-C24 | Nested boundary/slice compositions and sibling-isolation core checks | `regressionNestedTimeSliceInsideLastDayCollectsOnlyValueAndLengthLineage`, `regressionNestedNextDayAroundTimeSlicePreservesNestedLineageIsolation`, `regressionSiblingBoundaryFunctionsShareScopeWithoutPartSlotLeakage` |
| BS-M01..BS-M04 | Invalid level diagnostics (TIME_SLICE, LAST_DAY) | `malformedTimeSliceInvalidLevelFatal`, `malformedLastDayInvalidLevelFatal` |
| BS-M05..BS-M07 | Invalid boundary diagnostics (TIME_SLICE) | `malformedTimeSliceInvalidBoundaryFatal` |
| BS-M08..BS-M11 | Invalid weekday diagnostics (NEXT_DAY, PREVIOUS_DAY) | `malformedNextDayInvalidWeekdayFatal`, `malformedPreviousDayInvalidWeekdayFatal` |
| BS-M12..BS-M16 | Ambiguous/unqualified value diagnostics | `malformedTimeSliceAmbiguousValueDiagnostics` |
| BS-M17..BS-M20 | Export-visibility/missing-derived-output failures | `malformedTimeSliceMissingDerivedOutputFatal` |
| BS-R01..BS-R03 | DML placement (INSERT/UPDATE/DELETE) | `regressionDmlInsertPlacementForTimeSlice`, `regressionDmlUpdatePlacementForLastDay`, `regressionDmlDeletePlacementForNextDay` |
| BS-R04..BS-R06 | UDF same-name generic-path controls | `regressionSchemaQualifiedSameNameUdfStaysGenericForTimeSlice` |
| BS-R07..BS-R10 | Nested wrappers and sibling isolation regression | `regressionNestedTimeSliceInsideLastDayCollectsOnlyValueAndLengthLineage`, `regressionNestedNextDayAroundTimeSlicePreservesNestedLineageIsolation`, `regressionSiblingBoundaryFunctionsShareScopeWithoutPartSlotLeakage` |
| BS-R11..BS-R14 | Valid-slot substitution typing/lineage (no keyword-slot leakage) | `regressionSiblingValidSubstitutionSlotsCollectWithoutKeywordLeakage`, snippet coverage: `timeSlicePredicandSnippet`, `lastDayPredicandSnippet`, `nextDayPredicandSnippet`, `previousDayPredicandSnippet` |
| BS-R15..BS-R20 | Set-op branch isolation (`UNION ALL`, `INTERSECT`, `EXCEPT`) | `regressionSetOpUnionAllTimeSliceBranchIsolation`, `regressionSetOpIntersectLastDayBranchIsolation`, `regressionSetOpExceptNextPreviousDayBranchIsolation` |
| BS-R21..BS-R28 | Multi-layer scope/rename propagation and derived set-op consumer behavior | `regressionMultiLayerRenameChainForTimeSlice`, `regressionMultiLayerSetOpDerivedForBoundaryFamily` |
| BS-R29..BS-R32 | SCRIPT statement reset / script-scope collector behavior | `regressionScriptResetAndAffinityIsolationForBoundaryFamily` |
| BS-R33..BS-R36 | Mixed-dialect fatal controls with boundary/slice members | `regressionMixedAffinityFatalForBoundaryFamily` |

Matrix result: **BS-C01..BS-C24, BS-M01..BS-M20, BS-R01..BS-R36 are all assigned
and implemented**.

The grammar, walker, registries, core placements, malformed diagnostics, DML,
UDF controls, statement reset, and mixed-affinity minimum parity are delivered.

### Postgres difference/bin/age family plan (DATE_BIN, AGE)
- **Core functionality scenarios (PG-C01..PG-C18)**
  - DATE_BIN stride/source/origin slot ordering and lineage
  - AGE one-arg and two-arg forms with preserved arity semantics
  - Qualified/unqualified/formula inputs across all slots
  - Core placement and producer-consumer propagation
- **Malformed/failure scenarios (PG-M01..PG-M14)**
  - Ambiguous source/origin for DATE_BIN
  - AGE left/right ambiguity and hidden-export failures
  - Invalid-shape controls (arity/signature mismatch paths)
- **Section D — mandatory snippet substitution coverage (fold into PG-M/PG-R execution)**
  - Add snippet-endpoint tests for DATE_BIN and AGE with substitutions in each expression slot (`stride/source/origin` and AGE one/two-argument forms as applicable).
  - Verify substitution typing and normal column resolution/collection through snippet parsing with strict golden outputs.
  - Add snippet negative controls for signature misuse and ambiguity/visibility failures.
- **Complete regression scenarios (PG-R01..PG-R24)**
  - Substitution typing per slot, nested/sibling isolation
  - DML/set-op placement and mixed-source visibility proofs
  - UDF controls + statement reset
  - **Total planned: 56 (PG-01..PG-56)**
- **Build/test plan note:** implement in `SqlEventWalkerDatetimePostgresDiffBinAgeTests`; run targeted selectors for AGE vs DATE_BIN sub-batches.
- **Default dialect hook note:** register each Postgres-only grammar path in `SqlGrammarDialectRuleRegistry`, assert the Postgres warning at its source position in every diagnostic test, and include mixed-dialect fatal checks in regression coverage.

### MAKE constructor family plan (MAKE_DATE, MAKE_TIME, MAKE_TIMESTAMP, MAKE_TIMESTAMPTZ, MAKE_INTERVAL)
- **Core functionality scenarios (MK-C01..MK-C30)**
  - Ordered-field AST/lineage for 3-field, 6-field, 6+timezone variants
  - MAKE_INTERVAL positional and named forms with source-form retention
  - Field-by-field column/formula/substitution collection
  - Core placement, derived/CTE propagation, and basic DML wrappers
- **Malformed/failure scenarios (MK-M01..MK-M24)**
  - Field-local ambiguity/unqualified failures for each constructor shape
  - MAKE_INTERVAL component misuse/missing-field shape controls
  - Hidden-export visibility failures through derived/CTE chains
- **Section D — mandatory snippet substitution coverage (fold into MK-M/MK-R execution)**
  - Add snippet-endpoint tests for all MAKE* constructors, including named/positional MAKE_INTERVAL forms.
  - Require substitution-variable coverage for every field position that accepts predicand expressions; assert per-field typing and dictionary/symbol-table placement with exact goldens.
  - Include malformed snippet controls for component misuse and missing/invalid field shapes.
- **Complete regression scenarios (MK-R01..MK-R42)**
  - Full field matrix permutations, nested wrappers, set-op isolation
  - Multi-layer rename chains and substitution typing in each field family
  - UDF controls + statement reset
  - **Total planned: 96 (MK-01..MK-96)**
- **Build/test plan note:** implement in `SqlEventWalkerDatetimeMakeConstructorTests`; keep helper builders for repetitive field-matrix assertions local to this class.
- **Default dialect hook note:** wire constructor-specific dialect affinity through the default statement-dialect collector (rule/semantic registry as applicable), assert by-position warnings where emitted, and cover mixed-dialect fatal behavior.

### JUSTIFY family plan (JUSTIFY_INTERVAL, JUSTIFY_DAYS, JUSTIFY_HOURS)
- **Core functionality scenarios (JY-C01..JY-C14)**
  - One-argument interval-expression AST/lineage across all three invocations
  - Qualified/unqualified/formula/substitution value coverage
  - Core placement and producer-consumer propagation
- **Malformed/failure scenarios (JY-M01..JY-M10)**
  - Ambiguous/unqualified value failures
  - Hidden-export and missing-output visibility failures
  - Invocation routing controls (no part-diagnostic mislabeling)
- **Section D — mandatory snippet substitution coverage (fold into JY-M/JY-R execution)**
  - Add snippet-endpoint tests for JUSTIFY* invocations with substitutions in the interval-expression input.
  - Verify substitution typing and ordinary column resolution/collection behaviors in snippet contexts with strict goldens.
  - Include snippet negatives for ambiguity/visibility and routing-control diagnostics.
- **Complete regression scenarios (JY-R01..JY-R20)**
  - Nested/sibling isolation, DML/set-op placement, statement reset
  - UDF same-name controls and dialect-affinity checks
  - **Total planned: 44 (JY-01..JY-44)**
- **Build/test plan note:** implement in `SqlEventWalkerDatetimeJustifyTests`; use compact sibling coverage for JUSTIFY_DAYS/HOURS after full JUSTIFY_INTERVAL primaries.
- **Default dialect hook note:** treat JUSTIFY* as Postgres-only for default affinity registration, assert by-position Postgres warnings in parser-access tests, and include mixed-dialect fatal regression scenarios.

### Clock timestamp family plan (CLOCK_TIMESTAMP, STATEMENT_TIMESTAMP, TRANSACTION_TIMESTAMP, TIMEOFDAY)
- **Core functionality scenarios (CK-C01..CK-C14)**
  - Zero-argument AST construction and invocation identity per function
  - Independent alias outputs and no-input lineage invariants
  - Core placement in SELECT/ORDER/GROUP-compatible wrappers
- **Malformed/failure scenarios (CK-M01..CK-M10)**
  - Signature misuse / argument-introduction rejection paths
  - UDF same-name controls and function-routing protections
  - Mixed-affinity fatal checks where statement rules require
- **Section D — mandatory snippet substitution coverage (fold into CK-M/CK-R execution)**
  - Add snippet-endpoint tests proving zero-argument family behavior across snippet endpoints where invocation is legal, including SCRIPT reset behavior.
  - Assert that no argument-level substitution/column collection is introduced for these zero-arg functions, while surrounding query substitutions still type and resolve normally.
  - Include snippet negatives for illegal argument introduction and same-name UDF routing controls.
- **Complete regression scenarios (CK-R01..CK-R20)**
  - Derived/CTE propagation, DML/set-op placement where valid
  - Multi-statement reset and sibling isolation
  - CURRENT_TIMESTAMP neutral controls to prevent false family extension
  - **Total planned: 44 (CK-01..CK-44)**
- **Build/test plan note:** implement in `SqlEventWalkerDatetimeClockTimestampTests`; enforce explicit “no argument columns/substitutions” assertions in every core case.
- **Default dialect hook note:** keep zero-arg timestamp family on the standard statement-dialect hooks (Postgres warning by position for these forms; neutral controls for shared `CURRENT_TIMESTAMP`), plus mixed-dialect fatal checks in script/statement reset coverage.

**Per-scenario assertion contract**
- Use exactly one SQL/query string per JUnit test method. Do not bundle multiple
  independent queries in one test method; split them so each failure maps to a
  single query and a single golden set.
- Full literal `assertEquals` goldens for AST/interface/substitutions/table
  dictionary/query dictionary/symbol table.
- Exact diagnostic assertions (code, severity, message, token text, line/char,
  diagnostic count) for all negative/edge cases.
- Do not use permissive assertions (`assertTrue`/`assertFalse` with `contains`,
  partial substring checks) as a substitute for full-object goldens or exact
  diagnostic assertions.
- For every query that intentionally exercises Snowflake-only or PostgreSQL-only
  grammar, assert the dialect warning by source position (code/severity/message
  fragment/token/line/char) with the standard diagnostic helpers.
- Symbol-path checks for key left/right scope buckets and set-op interfaces.
74. **Build/test DATE_TRUNC (primary, D).** Primary for leading part metadata
    plus one expression: `level/value`. Use valid parts and date-valued formula
    anchors; include invalid part, unqualified VALUE, and ambiguous VALUE.
    Deep grouping/order/window cases must assert the corresponding buckets.
75. **Build/test DATE_PART (syntax-variant sibling, S).** Share leading-part/
    expression collection coverage with DATE_TRUNC, but implement extraction
    semantics and both comma/FROM forms under the approved new datetime AST.
    Run S for both syntactic alternatives, including invalid bare/quoted parts,
    unqualified/ambiguous VALUE, and syntax-discriminator goldens. Update all
    affected August tests; do not repeat DATE_TRUNC's placement matrix.
76. **Build/test EXTRACT (syntax-variant sibling, S).** Fold into the datetime
    AST with its own invocation and extraction registry. Apply S to FROM and
    the parser's accepted comma form, including invalid parts and unqualified/
    ambiguous source expressions. Preserve interval/typed literal source
    semantics, extended/timezone part handling, and endpoint coverage. Update
    affected old tests and consumer instructions; do not repeat deep wrappers.
77. **Build/test LAST_DAY (primary, D).** Primary for expression followed by
    optional part: `value/[level]`. Cover omitted/explicit level and its own
    smaller allowed-part set, invalid part, unqualified/ambiguous VALUE, both
    variable kinds, and full deep contexts. Do not normalize an omitted level
    into an invented authored AST argument.
78. **Build/test datetime TRUNC (sibling, S).** Reuse trailing-part expression
    placement coverage from LAST_DAY, not its allowed-part set or semantics.
    Assert `value/level` order, invalid part, unqualified/ambiguous VALUE, and
    both variable kinds. Retain scenario 54 numeric/one-argument/DDL controls;
    one-argument TRUNC stays generic without distinguishing semantic evidence.
79. **Build/test TIME_SLICE (primary, D).** Unique
    `value/slice_length/level/[boundary]` signature. Cover both expression
    fields with qualified/unqualified/formula/substitution inputs, ambiguity
    separately in each, valid/invalid part, and omitted/explicit boundary
    choices and invalid boundary. Replicate deep contexts with timestamp/date
    anchors and appropriate length expressions; do not parse boundary as a
    normal source column or borrow DATEADD's validity set.
80. **Build/test NEXT_DAY (primary, D).** Primary for `value/weekday` metadata.
    Use documented weekday spellings and its dedicated invalid-weekday
    contract; there is no date-part parameter. Include unqualified/ambiguous
    VALUE and both variable kinds, then full deep contexts with date outputs.
81. **Build/test PREVIOUS_DAY (sibling, S).** Reuse weekday-family construction
    but preserve its own invocation and direction. Include valid/invalid
    weekday, unqualified/ambiguous VALUE, both variable types, and no context
    matrix duplication. Do not label its weekday failure a DATEADD part error.
82. **Build/test DATE_BIN (primary, D).** Unique three-expression
    `stride/source/origin`, with no metadata part. Use interval, timestamp, and
    origin anchors; test each expression slot's collection and substitutions.
    Include separate source/origin ambiguity and a column-backed stride where
    accepted. Replicate deep contexts and preserve stride/source/origin order;
    no invalid-date-part test is applicable.
83. **Build/test AGE (primary, D).** Unique optional expression arity: one
    `value` or two `left/right` operands. Run simple construction/resolution
    cases for both overloads, and adapt each deep context category to both
    forms without an unnecessary Cartesian product. Assert interval-valued
    results, left/right order, omitted versus present fields, and ambiguity
    in each operand. No part metadata/part diagnostic applies.
84. **Build/test MAKE_DATE (primary, D).** Primary for three ordered numeric
    expression fields `year/month/day`. All are ordinary expressions, not
    date-part codes. Distribute qualified/unqualified/formula inputs and both
    substitution kinds across every field, including field-local ambiguity,
    then deep date-output context tests.
85. **Build/test MAKE_TIME (sibling, S).** Reuse three-numeric-field collection
    coverage with `hour/minute/second`, preserving different field names and
    supported second precision. Include unqualified/ambiguous input cases and
    both substitution kinds for each field. No invalid-part test or replicated
    deep placement matrix applies.
86. **Build/test MAKE_TIMESTAMP (primary, D).** Primary for six numeric
    expression fields `year/month/day/hour/minute/second`. Exercise every field
    with normal collection/substitutions and field-local ambiguity, then deep
    timestamp-output contexts. Do not assume three-field constructor tests
    prove the six-field AST and argument order.
87. **Build/test MAKE_TIMESTAMPTZ (sibling with unique optional tail, S).** Reuse
    six-field context coverage, preserving its own invocation. Add explicit
    simple cases for omitted/explicit timezone expression, both substitution
    kinds in that field, its qualified/unqualified columns, and ambiguity.
    This tail is an expression, not a date-part code; test it directly rather
    than duplicating every placement wrapper.
88. **Build/test MAKE_INTERVAL (primary, D).** Unique positional/named component
    signature. Cover full and partial supported forms, source-form retention,
    expression formulas, both variable types, and ambiguity in each populated
    component. Deep contexts must exercise both positional and named paths
    across the suite, with at least one of each in derived/CTE, DML, and set-op
    categories. Do not invent omitted components or confuse component names
    with metadata-valued date parts.
89. **Build/test JUSTIFY_INTERVAL (primary, D).** Primary for one ordinary
    interval expression `value`, without part metadata. Cover literals,
    columns, formulas, both substitutions, unqualified success and ambiguity,
    then interval-appropriate deep context wrappers.
90. **Build/test JUSTIFY_DAYS (sibling, S).** Reuse the one-interval-expression
    implementation/coverage with its own invocation; include unqualified/
    ambiguous VALUE and both variable types. No part diagnostic or placement
    replication is applicable.
91. **Build/test JUSTIFY_HOURS (sibling, S).** Same compact obligations as
    JUSTIFY_DAYS, with its own invocation and normal input dependencies.
92. **Build/test CLOCK_TIMESTAMP (zero-argument primary, D adapted as Z).**
    Build a no-argument datetime node with PostgreSQL affinity. Replicate deep
    contexts using a constant-like anchor, multiple independently aliased
    occurrences, and calls mixed with ordinary source columns. There are no
    argument columns, substitutions, parts, or ambiguous function inputs:
    explicitly mark those S/anchor cases inapplicable. Verify wrapper columns
    still resolve normally, but never claim they test clock-input ambiguity.
    Do not nest the function as its own argument. Group only where SQL permits;
    do not introduce a window OVER directly on a non-window clock function.
93. **Build/test STATEMENT_TIMESTAMP (zero-argument sibling, Z compact).**
    Assert invocation, no argument fields/source dependencies, independent
    output aliases, PostgreSQL affinity, and qualified same-name UDF behavior.
    Reuse CLOCK_TIMESTAMP placement proof without repeating its context suite.
94. **Build/test TRANSACTION_TIMESTAMP (zero-argument sibling, Z compact).**
    Apply the same simple no-argument construction/affinity controls with its
    own invocation; no invented invalid-part/unqualified-input cases.
95. **Build/test TIMEOFDAY (zero-argument sibling, Z compact).** Apply compact
    clock construction controls with its own invocation; its result is text,
    so any enclosing expression must be text-compatible. Retain a neutral
    CURRENT_TIMESTAMP comparison control without making it a new family member.
96. **Build shared signature/validation plumbing without sibling shortcuts.**
    Reuse the single registry and ordinary expression helpers, but keep
    per-invocation AST names, supported parts, optional fields, and affinity
    explicit. Run invalid-date-part FATAL cases for DATEADD, TIMEADD,
    TIMESTAMPADD, DATEDIFF, TIMEDIFF, TIMESTAMPDIFF, DATE_TRUNC, DATE_PART,
    EXTRACT, datetime TRUNC, TIME_SLICE, and LAST_DAY individually. Canonical
    existence alone is not family validity. A compact sibling test must fail
    if only that invocation is misrouted, swapped, or skips validation.
97. **Close each build/test action with observable evidence.** Record the
    function name, implemented signature/form variants, profile S/D/Z, actual
    tests run and pass/fail counts, and remaining gaps. Only run the current
    family class/methods during development. Review new AST/symbol/diagnostic
    goldens against the assertion contract, not just a successful test exit.
    Update consumers/generation instructions for each newly available signature
    and retain syntax forms; the extraction documentation describes the new
    AST directly without a generic-function migration narrative. Do not mark
    the whole datetime family complete because DATEADD passes.

### Delivery slices

| Slice | Scope | Current status |
|-------|-------|----------------|
| **A — lexer baseline** | In one batch add or confirm dedicated late lexer entries for every bare built-in needed by Slices B–E: add/diff aliases, trunc/slice/day-boundary names, `DATE_BIN`, `AGE`, `MAKE_*`, `JUSTIFY_*`, and timestamp clock functions. Do not add lexer tokens for part aliases or extensible parts. Regenerate and provide sorted token substitutions; golden token-ID edits are user-only (2026-11-11 clarification supersedes earlier bulk-update approval). Run the full suite after the user's edits. No later slice may change lexer numbering without separate review. | **Complete** — 26 late tokens; user-applied golden migration; configured JDK 21 suite 2,643 passed |
| **B — add family + registries** | Shared part/signature registries; `datetime_function`; `DATEADD`, `TIMEADD`, `TIMESTAMPADD`; core AST/symbol-table/dialect tests; q5866 clean with `year` absent from dictionaries and lineage | **Complete (Slice B scope)** — scenario-65 boundary delivered with explicit matrix accounting below; retain cross-family endpoint/generator closure under F |
| **C — difference + truncation** | `DATEDIFF`, `TIMEDIFF`, `TIMESTAMPDIFF`, shared `DATE_TRUNC`, datetime `TRUNC`; preserve neutral numeric `TRUNC` | **Complete** — DF catalog closed (`DF-01..DF-96`) and TE deep-regression closure delivered/passing |
| **D — remaining Snowflake** | `TIME_SLICE`, `LAST_DAY`, `NEXT_DAY`, `PREVIOUS_DAY` | **Complete** — BS-C/BS-M/BS-R matrix fully mapped (`BS-C01..BS-C24`, `BS-M01..BS-M20`, `BS-R01..BS-R36`) with passing coverage and explicit traceability |
| **E — PostgreSQL symmetry** | `DATE_BIN`, `AGE`, `MAKE_*`, `JUSTIFY_*`, timestamp clock functions, named `MAKE_INTERVAL`, shared/target part-affinity tests | **Deferred (post-5.1.4 release)** — parked intentionally; lexer baselines and planning catalogs retained |
| **F — delivery closure** | Generator support for `datetime_function`, `pss-parse-docs` / release notes, bound-query + snippet corpus comparison, full regression; generator follow-up may remain under **Entry 10** only if explicitly deferred | **Complete except deferred parts** — consumer docs updated for delivered datetime families; datetime generator support deferred to **Entry 10**; Slice E handoff deferred post-release |

**Story minimum:** Slices **A–B** + regression tests. **Full Entry 1:** A+B+C+D+E+F.

#### Slice A matrix accounting (strict traceability, 2026-11-16)

| Slice A IDs | Coverage theme | Evidence |
|---|---|---|
| A-L01..A-L26 | Late lexer token additions for datetime families used by Slices B–E (add/diff/trunc/slice/day-boundary, `DATE_BIN`/`AGE`, `MAKE_*`, `JUSTIFY_*`, clock timestamp names) inserted before `Identifier` without reordering prior tokens | Lexer checkpoint + verification block below; focused suite `SqlDatetimeLexerBaselineTest`; full-suite gate recorded as 2,643/0 after user-applied token-ID golden edits |
| A-V01..A-V03 | Token-ID migration protocol and non-agent golden edits | Slice A checkpoint notes: descending old-ID replacement list, user-only edits, no automatic agent rewrite |
| A-V04..A-V06 | Build/regeneration and full-suite verification for the lexer batch | Slice A checkpoint verification paragraphs (ANTLR regeneration, focused tests, then full-suite pass after manual edits) |

Matrix result: **Slice A obligations are fully assigned and completed**.

#### Slice B matrix accounting (strict traceability, 2026-11-16)

Canonical Slice-B scenario boundary is **1..65** (the add-family closure scope).

| Slice B IDs | Coverage theme | Evidence tests / artifacts |
|---|---|---|
| B-01..B-04 | Add-family grammar routing and named datetime AST shaping for `DATEADD`/`TIMEADD`/`TIMESTAMPADD` | `SqlEventWalkerDatetimeFunctionPlacementTests`: `dateaddHasNamedArgumentsAndPartMetadata`, `timeaddAndTimestampaddRetainTheirOwnInvocationsAndExpressionArguments` |
| B-05 | 24-shape argument matrix for increment/value source forms | `SqlEventWalkerDatetimeFunctionPlacementTests`: `dateaddMatrix*` method series |
| B-06..B-15 | Canonical/alias level normalization and authored spelling/form retention | `SqlEventWalkerDatetimeFunctionPlacementTests`: registry and spelling tests (`dateaddRegistryMatchesSnowflakeDocumentedPartsAndAliases`, `dateaddAcceptsEvery*Spelling`, quoted/case controls) |
| B-16..B-33 | Ambiguity, visibility, derived/CTE/scalar/correlated propagation and join contexts | `SqlEventWalkerDatetimeFunctionPlacementTests`: `dateaddReportsAmbiguous*`, `dateaddResolvesUnique*`, `dateaddConsumes*`, `dateadd*Scalar*`, `dateadd*Cte*` |
| B-34..B-47 | Nested/sibling behavior, substitution typing, hidden-source and join-extension deferral controls | `SqlEventWalkerDatetimeFunctionPlacementTests`: nested DATEADD controls + substitution matrix + join-extension fatal/defer tests |
| B-48 | Add-family dialect-affinity and mixed-affinity controls | `SqlEventWalkerDatetimeFunctionPlacementTests` add-family affinity/script diagnostics; q5866 checkpoint notes |
| B-49..B-65 | Clause/DML/set-op/script placement and statement reset isolation | `SqlEventWalkerDatetimeFunctionPlacementTests`: WHERE/HAVING/ORDER/WINDOW, INSERT/UPDATE/DELETE, set-op, SCRIPT reset tests |
| B-SNIP-01..B-SNIP-12 | Add-family snippet endpoint and substitution coverage | `SqlEventWalkerDatetimeFunctionSnippetTests` (DATEADD/TIMEADD/TIMESTAMPADD coverage documented in checkpoint text) |

Matrix result: **B-01..B-65 are fully assigned and implemented for Slice B scope**.

#### Slice C matrix accounting (strict traceability, 2026-11-16)

Slice C consists of the diff catalog plus trunc/extract catalog closure.

| Slice C IDs | Coverage theme | Evidence tests |
|---|---|---|
| DF-01..DF-12 | Diff signature and level metadata controls | `SqlEventWalkerDatetimeFunctionPlacementTests` (Section A closure) |
| DF-13..DF-37 | 25-case left/right operand-shape matrix | `SqlEventWalkerDatetimeFunctionPlacementTests` (Section B closure) |
| DF-38..DF-49 | Ambiguity and visibility failure behavior | `SqlEventWalkerDatetimeFunctionPlacementTests` (Section C closure) |
| DF-50..DF-67 | Substitution matrix in left/right trees and join-extension behavior | `SqlEventWalkerDatetimeFunctionPlacementTests` (Section D closure) |
| DF-68..DF-76 | Nested/sibling isolation for diff family | `SqlEventWalkerDatetimeFunctionPlacementTests` (Section E closure) |
| DF-77..DF-86 | Producer/consumer and scope propagation | `SqlEventWalkerDatetimeFunctionPlacementTests` (Section F closure) |
| DF-87..DF-94 | Clause/DML/set-op placement | `SqlEventWalkerDatetimeFunctionPlacementTests` (Section G closure) |
| DF-95..DF-96 | UDF controls and SCRIPT reset/isolation | `SqlEventWalkerDatetimeFunctionPlacementTests` (Section H closure) |
| TE-C01..TE-C28 | TRUNC/EXTRACT core functionality matrix | `SqlEventWalkerDatetimeTruncExtractTests` core coverage block |
| TE-M01..TE-M24 | TRUNC/EXTRACT malformed and diagnostic matrix | `SqlEventWalkerDatetimeTruncExtractTests` malformed block |
| TE-R01..TE-R44 | TRUNC/EXTRACT deep regression (nested/sibling, set-op, rename propagation, DML, UDF/reset/mixed-affinity) | `SqlEventWalkerDatetimeTruncExtractTests` deep-regression additions and pass runs |
| C-SNIP-01..C-SNIP-12 | TRUNC/EXTRACT and diff snippet endpoint/substitution coverage | `SqlEventWalkerDatetimeFunctionSnippetTests` + diff snippet checkpoint entries |

Matrix result: **DF-01..DF-96 and TE coverage obligations are fully assigned and implemented**.

**Phase 1 / Slice A complete (2026-11-11):** Added 26 late date/time function
tokens, `DATEADD` through `TIMEOFDAY`, immediately before `Identifier`. Existing
token IDs 1–392 remain unchanged; former IDs 393–413 shift by +26. Supply the
replacement list in descending old-ID order, matching exact `<nnn>` strings.
No existing golden was edited. Part aliases remain identifiers, and the new
function tokens remain eligible for generic/qualified UDF calls until dedicated
date/time productions are implemented.

With user approval, the same checkpoint included the narrow Entry 18 parser-rule
fixes: existing `POSITION`, `INPUT`, and `PATH` tokens are non-reserved identifiers;
existing `TRUNCATE` is allowed as a generic function name. Their lexer declarations
were not moved or duplicated, so they introduce no additional token-ID changes.
`position_function` precedes `routine_invocation` to preserve the existing
POSITION comma/IN AST path.

Verification: ANTLR regeneration, main/test compilation, and the five tests in
`SqlDatetimeLexerBaselineTest` pass via
`mvn -pl parse -Dmaven.compiler.fork=true -Dtest=SqlDatetimeLexerBaselineTest test`.
Initial builds encountered IDE-written unresolved-ANTLR error classes that Maven
considered current; invalidating only
`parse/target/maven-status/maven-compiler-plugin/compile/default-compile/inputFiles.lst`
forced a successful javac rebuild. Java auto-build was temporarily disabled with
permission and restored. Subsequent IDE output interference is avoided with the
opt-in `isolated-build` Maven profile (`target-maven/` output); Java auto-build
remains enabled. Regeneration, compilation, and the five focused baseline tests
are verified. The earlier isolated full suite ran 2,199 tests: 1,680 passed, 498
failed on remaining `<393>` versus `<433>` expectations, 0 errors, 21 skipped.
After the user's manual golden edits, the full workspace suite was run through
the VS Code testing extension using its configured JDK 21 execution settings:
**2,643 passed, 0 failed**. This satisfies the Phase 1 / Slice A full-suite gate;
the counts come from a different runner than the earlier Maven run. No golden
edits were made by the agent. This paragraph records the Slice A checkpoint;
subsequent checkpoints below supersede its then-current implementation state:
add, extraction, diff/trunc, and boundary/slice grammar/walker work has since
been delivered as described in the slice-status table.

### Acceptance

**Slice B grammar-only inspection checkpoint (2026-11-11):** Added
`datetime_function`, the shared add-family expression/name rules, and the shared
`datetime_part` / `snowflake_datetime_part` rules. `extract_field` remains a
compatibility wrapper. All five new walker exit overrides are intentionally
empty for raw-AST inspection; no signature/part registry, AST shaping, or new
affinity handling is implemented yet. JDK 21 isolated-build verification passed
91 focused grammar/EXTRACT tests, with lexer declaration order unchanged.
An ordinary JDK 21 run of
`select dateadd(year, 1, a.created_at) as shifted from events a`
reported zero syntax errors/FATALs and retained numbered rule maps and `Type`
markers, as expected before shaping. This does not complete Slice B or establish
its final symbol-table/substitution/affinity contract.

**DATEADD AST checkpoint (2026-11-12):** DATEADD now emits
`datetime_function={function_name=DATEADD, level={canonical,text,form},
increment=..., value=...}` without numbered rule wrappers or `Type` markers.
`DatetimePartRegistry` canonicalizes documented Snowflake DATEADD aliases while
preserving authored spelling and part form; unknown/extensible text retains its
uppercased canonical text, with family-validity checks still deferred. Nested
DATEADD, ordinary expression operands, predicand substitutions, and qualified
generic UDFs have focused AST tests. At that checkpoint TIMEADD/TIMESTAMPADD
shaping, signature validation, and new dialect-affinity handling remained unfinished. Verification
on JDK 21: 96 focused tests passed; isolated full parser suite ran 2,207 tests
with 0 failures, 0 errors, 21 skipped. No existing goldens were edited.

**Add-family AST follow-up (2026-11-12):** TIMEADD and TIMESTAMPADD now use
the same named argument shaping and registered-part validation as DATEADD,
while retaining their own invocation names. One focused query tests both
aliases, qualified expression arguments, and their full output goldens; a
second focused test covers each alias's invalid-part FATAL. Type-dependent
unit restrictions, dialect affinity, complete sibling mini-suites, and
snippet permutations remain pending.

**DATEADD scenario progress (2026-11-12):** Placement tests now include
scenarios 1-33, including 32a, plus portions of 47. Scenario 5 has all 24 argument-shape combinations except literal/literal,
each with six literal output goldens. The remaining substitution permutations,
scope/clause/DML/set-operation matrix, and affinity/reset coverage are not yet
complete. Scenario 18 compares the registry against the Snowflake DATEADD
parts table and tests all 62 canonical and alias spellings in 11 full-golden
parser batches. Scenario 48 depends on DATEADD dialect affinity, which is not yet
implemented. Positive cases use six literal output goldens; ambiguity cases
assert the existing source-position diagnostics.
Scenario 16 includes mixed variable kinds alongside ordinary columns in each
argument independently and in both together. Scenario 19 includes quoted
canonical and alias spellings as well as unquoted forms.
Scenarios 20-28 cover level-like columns, invalid and nested levels, numeric
expressions, repeated inputs, ordinary-function nesting, nested DATEADD, and
CASE. The `epoch_second` control is accepted by EXTRACT with the existing
Snowflake dialect warning and rejected as a DATEADD part.
Scenario 29 covers all three DATEADD anchors as derived-table producers,
including child-to-parent output lineage and the joined anchor's filter inputs.
Scenario 30 covers DATEADD consuming arguments through one derived table and
two renamed layers, with fatal diagnostics for hidden leaf names in either slot.
Scenario 31 covers the three anchor shapes in joined SELECT and ON expressions,
and in derived tables on both sides of INNER and LEFT JOINs; output and filter
dependencies are asserted separately.
Scenario 32 covers DATEADD inside a single-row scalar SELECT and uncorrelated
scalar aggregates supplying INCREMENT and VALUE independently. These two
argument tests are also the uncorrelated controls for scenario 32a, which adds
correlated scalar aggregates independently in each argument and in both together.
Their local inputs, outer references, filters, and dependent-query links are
asserted in their respective scopes.
Scenario 33 covers the three anchor shapes as CTE producers, DATEADD over raw
CTE exports, and a renamed CTE chain with ordered visibility and lineage.
Nested qualified column substitutions in both DATEADD formulas are also
covered with missing source aliases: they remain fatal without a join extension
and become severe warnings with a required-extension warning when one is present.
The unknown-table-alias control currently reports the SELECT output alias
position rather than the offending qualifier; an ordinary column reference
shows the same existing diagnostic behavior.

Structured AST (not `parameters.1.column` for parts); `TIMESTAMPADD` parses; fec/`year` CASE and q5866 are clean; no date/time part appears as a physical/query column or unresolved reference; EXTRACT/DATE_PART unified under the approved Phase 1 action with syntax-preserving datetime-family ASTs, retained source semantics, and documented consumer changes; every target-only function/part/construct in the matrices raises the matching statement dialect flag; shared forms raise neither; mixed affinity remains FATAL per statement; bound-query and snippet extract re-check is documented with no unexplained regressions; 5.1.4 jar + grammar extension doc.

### Snowflake reference

[DATEADD](https://docs.snowflake.com/en/sql-reference/functions/dateadd), [TIMEADD](https://docs.snowflake.com/en/sql-reference/functions/timeadd), [TIMESTAMPADD](https://docs.snowflake.com/en/sql-reference/functions/timestampadd), [DATEDIFF](https://docs.snowflake.com/en/sql-reference/functions/datediff), [DATE_TRUNC](https://docs.snowflake.com/en/sql-reference/functions/date_trunc), [TIME_SLICE](https://docs.snowflake.com/en/sql-reference/functions/time_slice), etc.

---

## Entry 2 — Snowflake PIVOT quoted unaliased identifiers

**Status:** Complete (closed 2026-11-17)  
**Spec (do not duplicate here):** [snowflake-pivot-quoted-identifier-parser-brief.md](./snowflake-pivot-quoted-identifier-parser-brief.md)

**Summary:** Lexer/parser must treat older unaliased PIVOT output names as delimited identifiers (`"'diq_entry_year'"`, numeric `"1"`…). Register IN-list values in `derivation.source_columns`; resolve as column refs. Newer `IN ('x' AS alias)` must keep working.

**Acceptance:** Both fixtures in the brief parse with no FATAL; derivation and location refs correct.

### Stage progress

- **Stage A (complete):** Added targeted capture/guard tests in
  `SqlEventWalkerPivotUnpivotTests` to pin legacy quoted-selector behavior and
  protect modern `IN (... AS alias)` form.
- **Stage B (complete):** Expanded lexer support for double-quoted identifiers
  so legacy selector spellings such as `"'diq_entry_year'"` parse as normal
  identifier references in SELECT/WHERE.
- **Stage C (complete):** Added mixed alias/unaliased PIVOT IN acceptance
  coverage to confirm side-by-side resolution (`entry_year` + legacy quoted
  selector) without regressions.
- **Stage D (complete):** Re-scanned copied live-query fixtures under
  `parse/testdata/live_queries/` and confirmed the historical quoted-selector
  parser-recovery fatal pattern is no longer present.

### Validation evidence

- `mvn -pl parse clean -Dtest=sql.walker.SqlEventWalkerPivotUnpivotTests#pivotQuotedUnaliasedSelectorWithoutAsResolvedStageBTest test`
- `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerPivotUnpivotTests#pivotQuotedLegacyIdentifierWithOuterAliasResolvedStageBTest,sql.walker.SqlEventWalkerPivotUnpivotTests#pivotInListAliasedFormNoRegressionGuardStageATest,sql.walker.SqlEventWalkerPivotUnpivotTests#pivotQuotedUnaliasedSelectorWithoutAsResolvedStageBTest,sql.walker.SqlEventWalkerPivotUnpivotTests#pivotMixedInAliasAndLegacyQuotedSelectorResolutionStageCTest test`
- `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerPivotUnpivotTests test`
- `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerSetOpScopingGateTests,sql.walker.SqlEventWalkerSetOpQueryVariableTests test`
- `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerLiveSampleQueriesTests#liveQueryPotentialStudentByEntryTermSurveyResponseHistoryNoLegacyPivotFatalRecoveryTest,sql.walker.SqlEventWalkerLiveSampleQueriesTests#liveQueryPotentialStudentByEntryTermNoSurveyNoLegacyPivotFatalRecoveryTest test`

---

## Entry 3 — Snowflake `PARSE_*` and `:` field access

**Status:** Complete (closed 2026-11-17)

**Problem:** `PARSE_URL(url, 1):scheme::varchar` — colon path after function call is not accepted (`NoViableAltException` on `:`). Same pattern for other semi-structured PARSE results.

**Scope minimum:** `PARSE_URL`; extend to `PARSE_JSON`, `PARSE_XML`, `PARSE_IP`, `TRY_PARSE_JSON` if grammar construction is shared.

**Work:** Grammar for post-call `:` paths (distinct from `::` cast and bind variables); walker AST; column resolution on function arguments; exemplar predicand + SELECT tests.

**Repro / detail:** git history — former `parser-defects-enhancements-workplan.md` Phase 3.

### Entry 3 staged execution plan (A-D)

| Stage | Scope | Exit gate |
|------|-------|-----------|
| **A — repro + guards** | Add failing repro tests for `PARSE_URL(url, 1):scheme::varchar` plus controls for existing `::` cast behavior and colon-adjacent non-target forms. | Repro fails for the right reason; controls remain stable. |
| **B — grammar acceptance** | Minimal parser production changes to allow post-call `:field` access while preserving `::` cast precedence. Avoid new lexer keywords unless evidence forces it. | Repro parse succeeds with no parser/listener fatal diagnostics; cast controls still pass. |
| **C — walker/AST + resolution** | Wire AST/walker handling for post-call field access and confirm function-argument column collection/resolution/diagnostics stay on normal paths. | No walker fatal diagnostics; unresolved/ambiguous controls produce expected diagnostics. |
| **D — family expansion + closeout** | Expand shared coverage across `PARSE_JSON`, `PARSE_XML`, `PARSE_IP`, `TRY_PARSE_JSON` where grammar path is shared; add placement coverage and non-regression bundle. | Focused suite + guard bundle green; Entry 3 acceptance evidence recorded for closure. |

### Stage progress

- **Stage A (complete):** Added a failing repro capture for
  `PARSE_URL(t.url, 1):scheme::varchar` plus a cast-only control guard
  (`PARSE_URL(t.url, 1)::varchar`) in `SqlEventWalkerTableFunctionTests`.
- **Stage B (complete):** Added parser support for post-call `:field` suffixes
  (`value_expression_primary_suffix`) and promoted the Stage A repro to a
  passing acceptance case while preserving cast guard behavior.
- **Stage C (complete):** Added explicit walker handling for post-call JSON-path
  suffixes so `PARSE_URL(...):scheme::varchar` produces a stable AST shape
  (`json_path=scheme` on the function output) while preserving downstream cast
  semantics and existing colon-notation table-function behavior.
- **Stage D (complete):** Expanded shared `PARSE_*` family coverage for
  `PARSE_JSON`, `PARSE_XML`, `PARSE_IP`, and `TRY_PARSE_JSON` across
  representative placements (SELECT, WHERE, JOIN ON), then added placement
  parity expansion coverage (`GROUP BY`, `HAVING`, `ORDER BY`, `UPDATE SET`)
  with strict full-golden extractor assertions and positional diagnostics for
  ambiguous/unresolved argument resolution.

### Stage validation evidence

- `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerTableFunctionTests#parseUrlPostCallFieldAccessResolvedStageBTest,sql.walker.SqlEventWalkerTableFunctionTests#parseUrlCastWithoutPostCallFieldAccessGuardStageATest test`
- `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerTableFunctionTests#parseUrlPostCallFieldAccessResolvedStageBTest,sql.walker.SqlEventWalkerTableFunctionTests#parseUrlCastWithoutPostCallFieldAccessGuardStageATest test`
- `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerTableFunctionTests#parseUrlPostCallFieldAccessResolvedStageBTest,sql.walker.SqlEventWalkerTableFunctionTests#parseUrlCastWithoutPostCallFieldAccessGuardStageATest,sql.walker.SqlEventWalkerTableFunctionTests#snowflakeColonNotationSelectPathV1Test,sql.walker.SqlEventWalkerTableFunctionTests#snowflakeColonNotationNestedFlattenInputV2Test test`
- `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerTableFunctionTests,sql.walker.SqlEventWalkerCastingAndTypesTests test`
- `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerTableFunctionTests#parseJsonPostCallFieldAccessSelectStageDTest,sql.walker.SqlEventWalkerTableFunctionTests#parseXmlPostCallFieldAccessSelectStageDTest,sql.walker.SqlEventWalkerTableFunctionTests#parseIpPostCallFieldAccessWhereStageDTest,sql.walker.SqlEventWalkerTableFunctionTests#tryParseJsonPostCallFieldAccessJoinOnStageDTest,sql.walker.SqlEventWalkerTableFunctionTests#parseUrlPostCallFieldAccessResolvedStageBTest test`
- `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerTableFunctionTests#parseUrlPostCallFieldAccessResolvedStageBTest,sql.walker.SqlEventWalkerTableFunctionTests#parseUrlCastWithoutPostCallFieldAccessGuardStageATest,sql.walker.SqlEventWalkerTableFunctionTests#parseJsonPostCallFieldAccessSelectStageDTest,sql.walker.SqlEventWalkerTableFunctionTests#parseXmlPostCallFieldAccessSelectStageDTest,sql.walker.SqlEventWalkerTableFunctionTests#parseIpPostCallFieldAccessWhereStageDTest,sql.walker.SqlEventWalkerTableFunctionTests#tryParseJsonPostCallFieldAccessJoinOnStageDTest,sql.walker.SqlEventWalkerTableFunctionTests#parseUrlPostCallFieldAccessAmbiguousArgumentColumnDiagnosticByPositionTest,sql.walker.SqlEventWalkerTableFunctionTests#parseUrlPostCallFieldAccessHasNoSnowflakeDialectGrammarWarningTest,sql.walker.SqlEventWalkerTableFunctionTests#parseUrlPostCallFieldAccessOrderByPlacementParityExpansionTest,sql.walker.SqlEventWalkerTableFunctionTests#parseUrlPostCallFieldAccessGroupByPlacementParityExpansionTest,sql.walker.SqlEventWalkerTableFunctionTests#parseUrlPostCallFieldAccessHavingPlacementParityExpansionTest,sql.walker.SqlEventWalkerTableFunctionTests#parseUrlPostCallFieldAccessHavingSubstitutionParityExpansionTest,sql.walker.SqlEventWalkerTableFunctionTests#parseUrlPostCallFieldAccessUpdateSetPlacementParityExpansionTest test`

---

## Entry 4 — Snowflake ARRAY syntax and functions

**Status:** Partial — implement remaining **4.x** independently unless syntax shares one production.

### Slice A implementation decision (2026-11-17)

- Implement Slice A with **parser/walker-first changes and no new lexer tokens**
  unless an unavoidable ambiguity is proven.
- Treat array literal support (`[expr, ...]`) as expression-grammar work with
  strict diagnostic controls and golden extractor assertions, while preserving
  subscript behavior (`expr[index]`) as a non-regression invariant.

### Entry 4 staged execution slices (A-F)

| Slice | Scope | Target subtasks | Exit gate |
|------|-------|------------------|-----------|
| **A — constructors and literal forms** | Close `ARRAY_CONSTRUCT` parity + array literal syntax completion | **4.2**, **4.4** | Placement + endpoint parity matrix passes; malformed/syntax controls green; no regressions in subscript behavior |
| **B — ordered/union aggregations** | Consolidate aggregate family behavior and lineage | **4.3** (complete), **4.6** | `WITHIN GROUP`/`OVER` lineage matrix complete; aggregate family diagnostics + set-op integration controls pass |
| **C — mutation/set operations** | Binary/set operators + mutator family | **4.7**, **4.8** | Function/operator signature matrix + column resolution + dialect diagnostics complete |
| **D — transforms/conversion/predicates** | Multi-arg transforms, unary transforms, conversion/predicate split-to-array | **4.9**, **4.10**, **4.11** | Signature-specific AST + symbol-table lineage + unresolved/ambiguous controls complete |
| **E — table/higher-order closure** | Advanced `FLATTEN` parity and higher-order array functions | **4.5**, **4.12** | Table-function scope/alias/lineage controls and higher-order argument-shape diagnostics complete |
| **F — delivery closure** | Corpus + snippet replay, docs, and release-gate verification | Entry 4 closeout + Entry 10/consumer-doc touchpoints where needed | Focused + smoketest/full parser gates pass; workplan matrix accounting complete |

**Story minimum for Entry 4:** Slices **A–C**.  
**Full Entry 4 closure:** A+B+C+D+E+F.

### Entry 4 test framework (matrix contract)

Use the same strict matrix discipline used in Entry 1 date/time work:

1. **Matrix IDs by concern**
   - **AR-Cxx**: core happy-path + placement matrix (SQL + DML + required clause sites)
   - **AR-Mxx**: malformed/syntax/dialect diagnostics (exact code, severity, position)
   - **AR-Rxx**: deep regressions (nested scopes, alias propagation, set-op branch isolation, multi-statement SCRIPT reset)
2. **Assertion contract**
   - Exact extractor goldens: AST, interface, substitutions, table dictionary, query dictionary, symbol table.
   - For snippet endpoints, keep AccessObject parity checks aligned with extractor outputs (same policy used in datetime suites).
3. **Column-resolution contract**
   - Every slice must include qualified, unqualified, unresolved, ambiguous, and hidden-alias controls.
   - Substitution-variable arguments in ARRAY functions must be typed/recorded like equivalent predicand/column contexts.
4. **Placement coverage contract**
   - Minimum sites per function family: SELECT, WHERE, JOIN ON, HAVING, QUALIFY, ORDER BY, VALUES, INSERT, UPDATE, DELETE, DDL, SCRIPT.
5. **Validation gates**
   - Focused class-level runs per slice while implementing.
   - Slice closeout requires `mvn -Psmoketest-quality-gate test`.
   - Entry 4 closeout requires focused bundle + parser baseline/full gate per current release protocol.

### Entry 4 first-pass AR ID allocation (implementation starter)

Use these IDs as the execution backbone; keep one-query-per-method and map each
completed test method back to its AR ID in comments or method names.

#### AR-C (core/placement) — AR-C01..AR-C40

| ID range | Scenario block | Primary host test class |
|---------|-----------------|--------------------------|
| **AR-C01..AR-C12** | `ARRAY_CONSTRUCT` full placement matrix (SELECT/WHERE/JOIN/HAVING/QUALIFY/ORDER BY/VALUES/INSERT/UPDATE/DELETE/DDL/SCRIPT) | `SqlEventWalkerSnowflakeArrayConstructTests` |
| **AR-C13..AR-C18** | `ARRAY_CONSTRUCT` core expression shapes (mixed args, repeated refs, CASE nesting, derived-table consumer, substitution args) | `SqlEventWalkerSnowflakeArrayConstructTests` |
| **AR-C19..AR-C24** | Array literal value expressions `[expr,...]` across core SQL/DML placements (paired with existing subscript controls) | `SqlEventWalkerArraySubscriptTests` (or dedicated `ArrayLiteral` class if split) |
| **AR-C25..AR-C29** | `ARRAY_CONTAINS` + VARIANT argument-shape happy paths + placement representatives | `SqlEventWalkerFunctionsAggregatesWindowingTests` (or dedicated array function class) |
| **AR-C30..AR-C34** | `ARRAY_UNION_AGG` / `ARRAY_UNIQUE_AGG` aggregate placement + lineage paths | `SqlEventWalkerWithinGroupOrderedAggregateTests` / dedicated aggregate array class |
| **AR-C35..AR-C37** | Mutator family (`APPEND`/`PREPEND`/`INSERT`/`REMOVE`/`REMOVE_AT`) representative placements | dedicated array function class |
| **AR-C38..AR-C40** | Transform/unary/conversion/predicate representatives (`ARRAY_TO_STRING`, `ARRAY_DISTINCT`, split/convert/predicate) | dedicated array function class |

#### AR-M (malformed/diagnostics) — AR-M01..AR-M24

| ID range | Scenario block | Primary host test class |
|---------|-----------------|--------------------------|
| **AR-M01..AR-M08** | Syntax controls for malformed `ARRAY_CONSTRUCT` / array literal forms (missing paren/comma, trailing comma, malformed literal list) with full syntax diagnostics | `SqlEventWalkerSnowflakeArrayConstructTests` + array literal class |
| **AR-M09..AR-M14** | Column resolution diagnostics (unresolved, ambiguous, hidden alias) for constructor/aggregate/mutator families | family-specific array test classes |
| **AR-M15..AR-M19** | Dialect diagnostics (Snowflake warnings, mixed-dialect fatal pairings, SCRIPT statement-scoped warning reset) | `SqlEventWalkerSnowflakeArrayConstructTests` + function-family classes |
| **AR-M20..AR-M24** | Signature/arity/type-shape diagnostics for transform/unary/conversion/predicate families | dedicated array function class |

#### AR-R (regression/deep integration) — AR-R01..AR-R20

| ID range | Scenario block | Primary host test class |
|---------|-----------------|--------------------------|
| **AR-R01..AR-R06** | Nested/derived/CTE alias propagation for constructor + aggregate outputs | `SqlEventWalkerSnowflakeArrayConstructTests` / `SqlEventWalkerWithinGroupOrderedAggregateTests` |
| **AR-R07..AR-R10** | Set-op branch isolation and output-interface propagation for array-family outputs | `SqlEventWalkerSetOpScopingGateTests` + family classes |
| **AR-R11..AR-R14** | Multi-statement SCRIPT reset semantics for array warnings/diagnostics | family-specific array classes |
| **AR-R15..AR-R17** | `FLATTEN`/`LATERAL FLATTEN` advanced parity (scope, alias visibility, lineage) | `SqlEventWalkerTableFunctionTests` |
| **AR-R18..AR-R20** | Compound integration controls (constructor+aggregate+transform compositions) | `SqlEventWalkerWithinGroupOrderedAggregateTests` + dedicated array function class |

#### Slice-to-ID mapping (first pass)

| Slice | ID allocation |
|------|---------------|
| **A (4.2 + 4.4)** | AR-C01..AR-C24, AR-M01..AR-M12, AR-R01..AR-R04 |
| **B (4.3 + 4.6)** | AR-C30..AR-C34 (+ existing 4.3 coverage), AR-M09..AR-M14, AR-R05..AR-R10 |
| **C (4.7 + 4.8)** | AR-C35..AR-C37, AR-M20..AR-M22, AR-R11..AR-R12 |
| **D (4.9 + 4.10 + 4.11)** | AR-C38..AR-C40, AR-M20..AR-M24, AR-R18..AR-R20 |
| **E (4.5 + 4.12)** | AR-R15..AR-R17 (+ additional AR-C/AR-M IDs if higher-order syntax expands) |
| **F (closeout)** | Full re-run + matrix accounting over all AR-C/AR-M/AR-R IDs |

#### Tentative test method naming prefixes (first PR convention)

Method-name template:

- `arC##_shortScenario_V#Test`
- `arM##_shortScenario_V#Test`
- `arR##_shortScenario_V#Test`

Where:

- `##` is zero-padded matrix ID number (e.g., `01`, `07`, `20`)
- `shortScenario` is stable, human-readable scenario text
- `V#` is the local variant counter in that class (`V0`, `V1`, ...)

Suggested host/class prefix mapping:

| Host class | Prefix family |
|-----------|----------------|
| `SqlEventWalkerSnowflakeArrayConstructTests` | `arC01..arC18`, `arM01..arM08`, `arM15..arM19`, `arR01..arR04` |
| `SqlEventWalkerArraySubscriptTests` *(or split literal class)* | `arC19..arC24`, `arM01..arM08` (literal-specific rows) |
| `SqlEventWalkerWithinGroupOrderedAggregateTests` | `arC30..arC34`, `arR05..arR10`, `arR18..arR20` |
| `SqlEventWalkerTableFunctionTests` | `arR15..arR17` |
| dedicated array-family classes (4.7–4.11) | `arC35..arC40`, `arM09..arM14`, `arM20..arM24`, `arR11..arR14` |

Examples:

- `arC01_arrayConstructSelectPlacement_V0Test`
- `arM07_arrayLiteralTrailingCommaSyntax_V0Test`
- `arR09_arrayAggSetOpBranchIsolation_V0Test`

### Entry 4 checkpoint (2026-11-15)

The codebase already contains ARRAY-adjacent capabilities that were not reflected
in the earlier "Not started" status:

- **Delivered baseline A:** postfix subscript `expr[index]` parsing + AST/walker
  support (see `value_expression_primary_suffix` and
  `SqlEventWalkerArraySubscriptTests`).
- **Delivered baseline B:** `ARRAY_AGG` function parsing and ordered-aggregate
  grammar path (`WITHIN GROUP`) with test coverage in aggregate suites.
- **Delivered baseline C:** `FLATTEN` / `LATERAL FLATTEN` table-function support
  with parser-access and symbol-table coverage.

These baselines do **not** close the full Snowflake ARRAY family scope below.

| Sub | Construction | Status (2026-11-16) | Notes / remaining decision |
|-----|--------------|---------------------|-----------------------------|
| **4.1** | `ARRAY_CONTAINS` + VARIANT syntax | Not started | Add dedicated grammar/AST tests for VARIANT argument forms and column lineage rules. |
| **4.2** | `ARRAY_CONSTRUCT` (compact / structured) | Partial | Added routine-level Snowflake dialect affinity + expanded SQL endpoint parity coverage (core happy paths + placement + column/reference + dialect controls). Remaining closure work is snippet-endpoint parity, SCRIPT/DDL coverage, and malformed-call syntax controls. |
| **4.3** | `ARRAY_AGG` + `WITHIN GROUP` / `OVER` | Complete | `SqlEventWalkerWithinGroupOrderedAggregateTests` now includes full location/resolution matrix coverage plus set-op + compound-array integration controls (Entry 19.3 closure). |
| **4.4** | Array literals and subscripts | Partial | `expr[n]` subscript is delivered; array literal value-expression syntax `[expr, ...]` remains open. |
| **4.5** | `FLATTEN` / `LATERAL FLATTEN` | Partial | Baseline table-function coverage exists; keep advanced parity scenarios in this entry until accepted as complete. |
| **4.6** | `ARRAY_UNION_AGG` / `ARRAY_UNIQUE_AGG` | Not started | No dedicated grammar/semantic parity coverage yet. |
| **4.7** | Binary / set array operations | Not started | Define supported operator/function set and add grammar + walker + diagnostics coverage. |
| **4.8** | `APPEND` / `PREPEND` / `INSERT` / `REMOVE` / `REMOVE_AT` | Not started | Function-family parity not yet implemented. |
| **4.9** | Transforms with extra arguments (`ARRAY_TO_STRING`, etc.) | Not started | Add signature-specific AST/lineage coverage for multi-argument transform family. |
| **4.10** | Unary array → array/scalar (`ARRAY_DISTINCT`, …) | Not started | Add unary family routing and output-shape coverage. |
| **4.11** | Conversion, predicates, split-to-array | Not started | Identify exact Snowflake target set; add function-shape and diagnostic contracts. |
| **4.12** | `FILTER` / `TRANSFORM` / `REDUCE` higher-order | Not started | Higher-order syntax and lambda-style argument handling still open. |

### 4.2 completion gate — ARRAY_CONSTRUCT parity test plan

**Current state:** partial. SQL endpoint parity was expanded across core
happy-path, placement (including DML), and reference/dialect controls.
Remaining closure items are snippet endpoint parity plus SCRIPT/DDL and
malformed syntax-control coverage.

**Definition of done for 4.2:** complete this checklist with one-query-per-method
tests, exact `assertEquals` goldens for all extractor objects, and explicit
diagnostic-by-position assertions.

#### 4.2.A Core happy-path matrix (SQL endpoint)

1. `ARRAY_CONSTRUCT` in SELECT projection with mixed literal + qualified column
   + predicand substitution arguments (existing baseline; keep as control).
2. `ARRAY_CONSTRUCT` nested inside `TO_VARCHAR`/`MD5` in JOIN `ON`
   comparison (existing baseline; keep as control).
3. `ARRAY_CONSTRUCT` with unqualified columns from a single source table.
4. `ARRAY_CONSTRUCT` with repeated column references in argument list.
5. `ARRAY_CONSTRUCT` nested inside CASE arm(s) and returned as expression.
6. `ARRAY_CONSTRUCT` nested in derived-table SELECT and consumed by parent
   SELECT alias reference.

#### 4.2.B Placement matrix (required SQL/DML/snippet sites)

7. SELECT list expression.
8. WHERE clause predicate operand.
9. JOIN `ON` clause operand (existing baseline; keep).
10. HAVING clause operand.
11. QUALIFY clause operand (Snowflake site).
12. ORDER BY expression.
13. GROUP BY expression (if accepted by grammar path).
14. VALUES row/cell expression.
15. INSERT…SELECT projection expression.
16. UPDATE assignment RHS expression.
17. DELETE WHERE expression.
18. DDL query body expression (`CREATE VIEW … AS SELECT …`).
19. SCRIPT with two statements: Snowflake warning should be statement-scoped
    (collector reset semantics).
20. Snippet endpoint parity set (`PREDICAND`, `CONDITION`, `QUERY`, `TUPLE`,
    `IN_LIST`, `JOIN_EXTENSION`, `VALUES`, `INSERT`, `UPDATE`, `DELETE`,
    `DDL`, `SCRIPT`) following existing datetime snippet contracts.

#### 4.2.C Column/reference verification matrix

21. Qualified column args resolve and land in table dictionary/filter/interface
    per site.
22. Unqualified column args resolve against visible source when unambiguous.
23. Unresolved unqualified column in args emits expected unresolved diagnostic
    and source position.
24. Ambiguous unqualified column in args emits expected ambiguous diagnostic.
25. Hidden/invalid table alias in qualified arg emits expected fatal or severe
    warning behavior (join-extension exception path where applicable).
26. Substitution variable arguments are typed/recorded like existing predicand
    variables in other function arguments.

#### 4.2.D Dialect diagnostics matrix

27. Snowflake statement warning emitted once per statement at first
    `array_construct` site with exact position.
28. Mixed-dialect fatal when `ARRAY_CONSTRUCT` is combined with PostgreSQL-only
    feature in same statement (existing baseline; keep and extend at least one
    additional placement site).
29. Multi-statement SCRIPT: warning appears on each statement that actually
    contains `ARRAY_CONSTRUCT`; no spillover.

#### 4.2.E Negative/malformed controls

30. Parser syntax error controls for malformed calls (missing right paren,
    missing comma between args, trailing comma) with full syntax-diagnostic
    assertions.
31. Regression control proving ordinary non-ARRAY routine invocation remains
    unchanged by 4.2 warning hook.

### Known happy-path syntax targets still not supported (sample authoring set)

These are representative positive-shape examples to use when implementing the
open subentries:

```sql
-- 4.4 array literal value expression (not subscript)
select ['a', 'b', 'c'] as arr from events e;

-- 4.1 array contains with variant-ish construction
select array_contains('a', array_construct('a','b')) as has_a from events e;

-- 4.6 union/unique aggregate family
select array_union_agg(e.tags) as merged_tags from events e;
```

**Test contract (each subtask):** Happy path without FATAL; six extractor goldens where applicable; column ref matrix (qualified, unqualified, unresolved, ambiguous); required SQL/DML sites per construction; compound nest test once **4.3**, **4.9**, **4.10** exist (see former Phase 5.6 compound SELECT in git history).

**Detail:** git — former `parser-defects-enhancements-workplan.md` Phase 5.

---

## Entry 5 — Simple Jinja / dbt substitutions

**Status:** Not started — **not** a Jinja interpreter (**Entry 6**).

| Sub | Work | Status |
|-----|------|--------|
| **5.1** | Tuple/table `{{ source() }}` / `{{ ref() }}` quote and spacing variants | Not started |
| **5.2** | Predicand/column Jinja in non-table positions | Not started |
| **5.3** | File-prefix Jinja: skip/collect as unexamined comments (bracket-matched) | Not started |
| **5.4** | **2.5 closure** — Jinja-authored DNC rollup (§2.5 starter in git) without `PARSE_TIMEOUT` | Not started (needs **5.1**) |

**Platform constraints:** [panto-variable-inheritance-using-bundles.md](./panto-variable-inheritance-using-bundles.md)

**Detail:** git — former `parser-defects-enhancements-workplan.md` Phase 6.

---

## Entry 6 — Deep Jinja / SCRIPT prefix *(optional)*

**Status:** Not started — **do not start until Entry 5 accepted** unless product requires compile-time shape change.

| Sub | Work |
|-----|------|
| **6.1** | Parse Entry **5.3** prefix bucket under SCRIPT endpoint (`config`, `set`, `snapshot`, …) |

Further 7.x (`{% if %}`, macros, filters, dbt_utils) — add when phase activated.

**Spec:** [panto-language-enhancements-grammar-safe-dynamic-expansion-2.md](./panto-language-enhancements-grammar-safe-dynamic-expansion-2.md) (overlaps strategic intent with **Entry 8**).

**Detail:** git — former `parser-defects-enhancements-workplan.md` Phase 7.

---

## Entry 7 — Panto variable types × parser endpoints (tests)

**Status:** Not started — engineering backlog, not new grammar.

**Theme:** Seven substitution `type=` values × sixteen parse endpoints — fill golden gaps.

**8.1 endpoint gaps (summary):**

| Endpoint | Gap |
|----------|-----|
| `INSERT` | No INSERT endpoint parity test vs `SQL` |
| `DELETE` | Smoke only — extend golden parity |
| `SCRIPT` | No angle-bracket subs in SCRIPT-scoped outputs |
| `DDL` | Subs in DDL templates — only if product needs |
| `VALUES` | No matrix/cell `<var>` endpoint test |
| `LITERAL` | N/A (document only) |

**8.2 type × grammar gaps (summary):** `EXISTS <var>`; minimal `INSERT … <queryVar>`; VALUES matrix `<var>`; defer Jinja predicand to **Entry 5.2**.

**References:** `SQLParserEndPoints.java`, `SqlEventWalkerNonSqlEndpointParserTests`, `SqlASTWalkerHelper.resolveSubstitutionValueTypeFromContext`.

**Detail:** git — former `parser-defects-enhancements-workplan.md` Phase 8.

---

## Entry 8 — Panto cardinality & grammar-safe dynamic AST expansion

**Status:** Not started — exploratory design

**Do not duplicate spec.** Authoritative design: [panto-language-enhancements-grammar-safe-dynamic-expansion-2.md](./panto-language-enhancements-grammar-safe-dynamic-expansion-2.md) (delivery sequence 9.1–9.5 in that doc).

**Platform:** [panto-variable-inheritance-using-bundles.md](./panto-variable-inheritance-using-bundles.md) — parser/walker changes must preserve bundle/substitution semantics.

**Detail:** git — former `parser-defects-enhancements-workplan.md` Phase 9.

---

## Entry 9 — DDL structured options parsing

**Status:** Not started (product-triggered)  
**Prerequisite:** Phase 20 DDL walker hygiene ✅ (walked `subMap`, not ctx scrape)

**Goal:** Promote high-value clauses from opaque `generic_ddl_options` blobs to typed AST flags (`if_not_exists`, `or_replace`, …); keep unmodeled tails opaque.

| Phase | Work |
|-------|------|
| **9.D0** | Product trigger + v1 clause list (`IF NOT EXISTS`, `IF EXISTS`, `OR REPLACE`, …) |
| **9.D1** | Grammar inventory |
| **9.D2** | Grammar + walker for v1 |
| **9.D3** | `SqlEventWalkerScriptsAndDDLTests` structure proofs |
| **9.D4** | Generator round-trip *(optional)* → **Entry 10** |
| **9.D5** | Expand by demand |

**Tests (proposed):** `createTableIfNotExistsParsedOptionsTest`, `createViewOrReplaceParsedOptionsTest`, `dropTableIfExistsParsedOptionsTest`, `createTableUnmodeledOptionsRemainOpaqueTest`.

**Detail:** git — former `ddl-structured-options-parsing-workplan.md`.

---

## Entry 10 — SQL statement generator completion

**Status:** Milestone ✅ (~51 round-trips); **expansion not started**

**Code:** `generators/SQLStatementGenerator.java`, `AbstractSQLASTGenerator.java`  
**Tests:** `generators/SQLStatementGeneratorTest`

**Architecture:** Walker mumble-key AST → generator `emit*` / `on*`; unhandled shapes → `Unexpected node`.

| Area | Milestone status |
|------|------------------|
| INSERT / UPDATE / DELETE / TRUNCATE | ✅ |
| SELECT core + WITH / VALUES / SCRIPT | ✅ / 🟡 |
| Opaque DDL CREATE/ALTER/DROP | ✅ |
| PIVOT / UNPIVOT exercised shapes | ✅ |
| Set ops INTERSECT/EXCEPT depth | 🟡 |
| Table functions matrix | 🟡 |
| CASE / IN / LIKE ANY / windows / Jinja map | 🔴 |
| Endpoint trees (predicand, condition, …) | 🔴 stubs |

**Phases (summary):** **0** inventory + round-trip contract + gate profile; **1** leaves/literals; **2** expressions; **3** predicates; **4** functions; **5** FROM/joins; **6** modifiers; **7** windows; **8–11** statements/DML/DDL/script. Work bottom-up when extending.

**Related:** Entry **9.D4** for typed DDL flags; Entry **1.D** for new datetime AST nodes.

**Datetime deferral note (2026-11-16):** `datetime_function` generator support
for the newly delivered Entry 1 Snowflake datetime families is intentionally
deferred here (Entry 10) per release-scoping decision.

**Inventory (auto):** `parse/tools/sql_generator_phase0_inventory.py` → `parse/documents/coverage/sql_generator_phase0_inventory.md` (regenerate after grammar/generator changes).

---

## Entry 11 — Walker `exit*` coverage (JaCoCo)

**Status:** Tier **1** ✅ (Aug 2026); **Tier 2** ~19 methods + **Tier 3** ~53 with minor gaps optional

**Measure:** `cd parse && mvn verify` → `target/site/jacoco/jacoco.xml` on `SqlParseEventWalker`.

**Priority queue:** [coverage/sql_walker_exit_method_gaps.md](./coverage/sql_walker_exit_method_gaps.md#tier-2--substantial-gaps-jacoco-aug-2026)

**Open Tier 2 examples (unchecked in former plan):** `exitTable_argument_literal`, `exitInfer_schema_argument`, `exitJinja_arg`, `exitJinja_function_call`, `exitStatic_data_type_name`, `exitWith_clause`, `exitFlatten_argument`, `exitEveryRule`, `exitSql_statement`, `exitDrop_options`, `exitAlter_options`, `exitInsert_preamble`, … — see gaps doc for full sorted list.

**Done criterion:** Re-run `mvn verify`; target `exit*` shows no missed lines (or team accepts Tier 3 residual).

**Detail:** git — former `coverage/sql_walker_exit_method_coverage-workplan.md`.

---

## Entry 12 — Helper dead-code hygiene

**Status:** Complete (closed 2026-11-16): Phases **A–D** complete.

**Policy:** Caller audit before delete; JaCoCo is heat map only, not delete oracle. Gate: `mvn -Psmoketest-quality-gate test`.

| Phase | Work | Status |
|-------|------|--------|
| **12.A** | Tier-1 dead public API in symbol-tree helpers | ✅ |
| **12.B** | Cascading private orphans + walker orphans | ✅ |
| **12.C** | Fresh JaCoCo heat map on megaclass helpers | ✅ (2026-11-16) |
| **12.D** | Wrapper hygiene (deprecated zero-caller stubs only) | ✅ (2026-11-16) |

**12.C/12.D closeout record (2026-11-16):**

- Gate run (post-change): `mvn -Psmoketest-quality-gate test`
  - `SmoketestQualityGateTestSuite`: 263 tests, 0 failures, 0 errors, 0 skipped (`BUILD SUCCESS`)
- Wrapper hygiene audit:
  - Removed zero-caller deprecated private wrapper constant
    `SCOPE_CLAUSE_COLUMN_LIST_KEYS` from
    `SqlParseSymbolTreeHelper` (alias of `ARCHIVED_SCOPE_COLUMN_REFERENCE_CONTAINER_KEYS`).

**Detail:** git — former `helper-dead-code-hygiene-workplan.md`.

---

## Entry 13 — SELECT-list subquery alias scope

**Status:** Complete (opened and closed 2026-11-10)  
**Kind:** Defect — blocking regression vs **5.0.0-3** (valid SQL rejected with FATAL)  
**Brief:** [select-list-subquery-alias-scope-defect.md](./select-list-subquery-alias-scope-defect.md)

**Symptom:** A subquery nested inside a SELECT-list *expression* (arithmetic operand, function argument, `CASE`, `IN (subquery)`, `NOT IN`, `EXISTS`) gets `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE` for every qualified reference to an alias declared inside the subquery. The diagnostic is reported at the outer select item's output alias. The same subquery is accepted in `WHERE` / `ON` / `HAVING` / `ORDER BY` and as a bare select item. Reproduced unchanged on 5.1.4-1.

**Root cause:**

1. `exitSelect_item` builds `interface[alias]` with `flattenSubTreeForDependencyColumns(..., skipQueryBackedSubtrees=false)`, so the flattening descends into nested subqueries and lifts inner-scope refs (`g.y`, `pp.x`) into the outer interface. Interface validation at scope exit then resolves them against the outer `table_alias` and FATALs. Clause flattening (`filters`, `grouped_by`, `ordered_by`) already stops at subquery boundaries. Bare scalar-subquery items avoid the FATAL only because the whole output column is skipped (`MUMBLE_SCALAR_SUBQUERY_ALIASES_KEY`).
2. Separate AST regression: the `predicand_subquery` rule (new since 5.0.0-3) never re-applies the `lookup` wrapper that 5.0.0-3 put on every scalar subquery, so operands are emitted as `calc:{left:{select…}}`. This is **not** the cause of the FATAL (`IN (subquery)` has an identical AST on both versions and still FATALs), but it makes the AST diverge from 5.0.0-3 and from the bare select-item shape.

| Sub | Work | Status |
|-----|------|--------|
| **13.1** | Select-item interface flattening stops at subquery boundaries (`select` / `lookup` / `exists` / set-op); subquery-local refs stay in `def_queryN`, linked via `dependent_queries`; outer correlations still validated at predicate-frame exit | Done — `SqlParseSymbolTreeHelper.flattenSelectItemDependencyColumns`, called from `exitSelect_item` |
| **13.2** | `exitPredicand_subquery` restores the `lookup` wrapper so operand scalar subqueries match 5.0.0-3 AST shape | Done |
| **13.3** | Regression tests: every Fails/Passes case in the brief, snippets 9530 / 8520, `zz` negative control, AST `lookup` shape | Done — `SqlEventWalkerSelectListSubqueryAliasScopeTests` (6 tests) |
| **13.4** | Corpus re-check: 11-10-2026 bound-query and snippet extracts, 5.0.0-3 vs 5.1.4 before/after | Done — results below |

**Golden updates:** 68 AST goldens changed only by the restored `lookup` wrapper (verified structurally). 5 symbol-table goldens had recorded the leak and were corrected: `scalarSubqueryWindowPartitionBySymbolTableTest` / `scalarSubqueryWindowOrderedBySymbolTableTest` (`rn` listed `k`/`ts` as `fact` columns), `nestedFormulaSubqueriesUseQueryRefsInInterfaceAndFiltersTest` (`dd` listed `D` as a `tab1` column), `nestedQueryDemoTest` / `nestedQueryDemoWithCteTest` (`mxd` listed the inner `tt` subquery's columns). No token-ID or pivot goldens changed. Full suite: 2181 run, 0 failures, 21 skipped.

**Corpus results (94,448 cases: 6,551 bound queries + 87,897 snippets, 11-10-2026 extract):**

| Measure | 5.0.0-3 | 5.1.4 before | 5.1.4 after |
|---------|---------|--------------|-------------|
| Bound queries with FATAL | 100 | 337 | 333 |
| Scope-signature FATALs (SQL / QUERY / TUPLE) | 0 / 0 / 0 | 128 / 10 / 385 | 124 / 0 / 4 |
| AST byte-identical to 5.0.0-3 | — | 57,805 | 59,188 |
| `lookup` node count equal to 5.0.0-3 | — | 93,039 | 94,440 |

395 cases fixed, all with the scope signature; 0 newly fatal.

**Remaining 113 bound queries with a scope FATAL where 5.0.0-3 is clean** (all classified; 5.0.0-3 never validates alias scope, and all 113 FATAL identically before this fix):

| Group | Cases | Finding |
|-------|-------|---------|
| No `join_extension` in the template | 14 | Genuine author errors: alias never declared (q508, q746, q1476, q1669, q4154, q5112, q6290, q6304, q33), typo (q3870 `stu_term_crmstu_term_crm`), or alias used outside the derived table that declares it (q1280, q2910, q3676, q5137). |
| Template contains a `join_extension` variable | 99 | The unresolved alias is never declared in the template text but is consistent with being declared by the substituted join snippet: in 98 of 99 cases every missing alias is declared by at least one JOIN_EXTENSION snippet in the same extract (matched by alias name; the extract carries no variable binding). Minimal repro: `select a.id, ev.x from t a <extension>` — 0 FATAL on 5.0.0-3, FATAL on 5.1.4. |

The `join_extension` group is a separate 5.1.x behaviour, not part of this defect: alias-scope validation cannot see aliases that a trailing join extension introduces. **Open decision** (not scheduled): whether unresolved qualifiers in a scope containing a `join_extension` substitution should stay FATAL, downgrade to a warning, or be deferred to post-substitution validation.

**IN_LIST snippets:** 33,307 of 33,810 extract values are stored without the parentheses the `IN_LIST` endpoint (`in_list_predicate_value`) requires; templates always write `IN <var>`. Parsed raw they FATAL identically on both versions. Wrapped in `( … )`, 33,809 parse clean on both with byte-identical ASTs; the one exception (s6989) has a stray `]` that 5.1.4 rejects and 5.0.0-3 silently accepts.

**Acceptance:** Every Fails case and both snippets reach 0 FATALs; every Passes case stays at 0; genuine outer-scope errors (e.g. `zz.y` inside the subquery) still FATAL; no new FATALs elsewhere in the live corpus. Outer `interface` entries no longer list subquery filter columns as sources of the output column (answers the brief's lineage question).

**Out of scope:** Correlated `EXISTS` / `IN` in the SELECT list beyond what Snowflake documents (WHERE-only). The parser accepts them; Snowflake may not.

---

## Entry 14 — Join extension deferral of missing-table fatals (whole-query)

**Status:** Complete (opened and closed 2026-11-10)  
**Kind:** Defect — release blocker for bound-query templates

**Problem:** Panto templates end a `FROM` list with a join extension variable (`FROM … <join_extension.Student>`). The snippet substituted there routinely declares aliases that the template's SELECT list, filters, and correlated subqueries reference (e.g. q2157 reads `event_agg.event_ct`; snippet s35160 supplies `LEFT OUTER JOIN (…) event_agg`). 5.0.0-3 never validated alias scope; 5.1.x reports every such reference as FATAL `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE` because the template alone cannot declare them. In the 11-10-2026 extract this affects 99 Enroll360/ALR bound queries (588 fatals); 92% of the alias-introducing snippet joins are `LEFT [OUTER] JOIN`, i.e. an established optional-enrichment convention, not author error. The default substitution for a join extension is *no* substitution (the variable is removed at generation), so a template is only valid when paired with a snippet that declares the missing aliases.

**Rule (applied once, at the end of the walk, over the complete diagnostic list):**

1. Collect all diagnostics exactly as today (unresolved qualified references are still raised as FATAL during the walk).
2. During the walk, record each join extension variable's name and source position (simple list on the event walker; no symbol-table entry, no assumptions about snippet content).
3. At handoff (`SqlParseEventWalker.getSnippet()`), if the query has at least one join extension variable **and** at least one `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE` fatal:
   - convert only those `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE` fatals to `SEVERE_WARNING` (same code, message, position); every other fatal stays fatal;
   - add one `JOIN_EXTENSION_REQUIRED` severe warning per join extension variable, at the variable's position, stating that the listed table/column references must be satisfied by the substituted snippet and the join extension is therefore not optional.
4. Join extension variables with no `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE` fatal produce no warning.
5. Multi-statement scripts: each statement is evaluated on its own when it finishes. Only the fatals that statement generated are considered, together with only that statement's join extension variables. The join extension list is then emptied, so a join extension never affects a sibling statement's fatals, and a later statement with missing-table fatals and no join extension keeps them as fatal.

**Known imprecision (accepted for release):** the check is whole-query. A missing alias in a scope the join extension cannot reach (another CTE, a non-lateral derived table, a sibling set-operation branch, an outer query) is also downgraded, and every `JOIN_EXTENSION_REQUIRED` warning lists all deferred references rather than those the specific variable could satisfy. **Entry 15** removes this.

| Sub | Work | Status |
|-----|------|--------|
| **14.1** | Record join extension variable locations in `exitJoin_extension` | Complete |
| **14.2** | Conversion + `JOIN_EXTENSION_REQUIRED` diagnostic (catalog entry in `SqlASTWalkerHelper`); runs at `exitSql_statement` for scripts and at `getSnippet()` for single-query endpoints | Complete |
| **14.3** | Tests (`SqlEventWalkerJoinExtensionDeferralTests`, 6): one / two join extensions; no warning without missing refs; no conversion without a join extension; unrelated fatals (duplicate interface, set-op column count) stay fatal; script statements evaluated independently | Complete — full `parse` suite 2187 run, 0 failures, 21 skipped; no existing golden changed |
| **14.4** | Corpus re-check of the 113 remaining bound queries (`alias-scope/remaining113-entry14.out` in the local archive documented under Entry 1) | Complete — see results below |

**14.4 results (5.1.4-1 jar with Entry 13 + 14):**

| Group | Queries | Still FATAL | `JOIN_EXTENSION_REQUIRED` | Remaining fatals |
|-------|---------|-------------|---------------------------|------------------|
| Join extension templates | 99 | 1 (q2155) | 99 | q2155: `DUPLICATE_INTERFACE_COLUMNS` + `SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH` (genuine, unrelated to scope) |
| No join extension (genuine errors) | 14 | 14 | 0 | 31 `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE` + 7 `QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS` — unchanged |

---

## Entry 15 — Scope-aware join extension deferral (enhancement)

**Status:** Not started — follows **Entry 14**; requires a rework of walker diagnostic capture  
**Kind:** Enhancement

**Goal:** Defer a missing-table/column diagnostic to a join extension only when that join extension could actually make the reference valid, and attribute each deferred reference to the specific join extension variable(s) that could satisfy it.

**Visibility rules to implement.** A join extension attached to query scope *Q* (`FROM … <ext>` of *Q*) adds table aliases, and through them columns, that are visible to:

- *Q*'s own SELECT list, `WHERE`, `GROUP BY`, `HAVING`, `QUALIFY`, `ORDER BY`, window clauses, and `ON` conditions of joins inside the extension;
- correlated references inside subqueries nested in those clauses (scalar, `EXISTS`, `IN`, quantified, lateral), at any depth, unless an intermediate scope shadows the alias.

It is **not** visible to:

- non-lateral derived tables in *Q*'s `FROM` list, or CTE bodies defined outside *Q* (including a CTE whose body references its own name, as in q3852);
- sibling set-operation branches;
- enclosing (outer) queries. Aliases never flow outward; only *Q*'s output interface does.

**Output propagation (the "queries that reference their outputs" case).** If *Q* projects a wildcard (`*` or `alias.*`) over a source set that includes a join extension, *Q*'s output interface is *open*: the snippet may add output columns. A consumer of *Q*, such as an outer query reading `agg.col`, a CTE reference, or a set-operation peer, should then also defer `QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS` / `UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES` against that alias to the originating join extension, instead of treating them as fatal. Explicit (non-wildcard) projections close the interface, and those diagnostics stay fatal.

**Why it needs a diagnostic-subsystem rework.** Today diagnostics are appended to one flat walker list as they are discovered. Some are merged across scopes (`globalQualifiedUnresolvedLocations`, merged-location messages) and de-duplicated by key (`emittedQualifiedSourceNotFoundKeys`), so after the walk a diagnostic can no longer be tied reliably to the scope that raised it or to the scopes that could satisfy it. The precise variant needs:

1. **Scope-attached diagnostics.** Each unresolved reference is recorded against the `def_queryN` scope that owns it (scope path plus reference position), not only in the flat list.
2. **Pending, not emitted.** Unresolved qualified references are held as pending on their scope. At each scope exit they are either resolved, propagated to the enclosing scope that is visible to them (for correlation), or finalised.
3. **Join extension ownership.** Each scope records the join extension variables attached to its own `FROM`. A pending reference reaching a scope that owns a join extension, without being shadowed on the way, is deferred to that variable. A reference that leaves every visible scope without meeting one becomes fatal.
4. **Open-interface taint.** A scope that projects a wildcard over a join-extension source marks its published interface as open, tagged with the originating variable(s). Consumers check the taint before raising `QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS`-type diagnostics.
5. **Per-variable warnings.** `JOIN_EXTENSION_REQUIRED` lists only the references deferred to that variable. A variable with none produces no warning.
6. **De-duplication and merged locations** move to finalisation time, so per-scope attribution survives.

**Acceptance (beyond Entry 14):**

- q3201 shape (alias used inside a derived table whose own `FROM` carries the join extension) is still deferred.
- A missing alias in a CTE or derived table with no join extension stays fatal, even when another scope in the same query has one.
- Two join extensions in different scopes produce disjoint reference lists.
- An outer reference to `agg.col` is deferred only when `agg` projects a wildcard over a join-extension source.
- Entry 14 corpus results: no previously deferred valid template becomes fatal.

**Out of scope:** Inspecting candidate snippet text to decide whether it actually declares the alias. The bound query and the snippet are parsed independently, and the binding is resolved by Panto bundles at generation time.

---

## Entry 16 — Set operation with a query variable member loses its output interface

**Status:** Partial — known identifier/function grammar fixes delivered with Entry 1 Slice A; remaining work below (opened 2026-11-11)  
**Kind:** Defect — release blocker for bound-query templates  
**Found by:** 5.0.0-3 vs 5.1.4-1 comparison of the 11-10-2026 extract ([panto-template-remediation-5.1.4.md](./panto-template-remediation-5.1.4.md), "Not your problem")

**Problem:** When a derived table or CTE body is a set operation and one of its members is a query variable (`set_operation_member : … | variable_identifier`, for example `<union_add.Partner ID Xwalk>`), the published output interface of that derived table / CTE is empty. Every `alias.column` read by the consumer is then reported FATAL `QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS`, although the real `SELECT` member defines all the columns. A set operation takes its output names from its first query member (positional matching for the rest), so the interface should come from the query member(s), and the variable member should only be checked for arity when its shape is known (it is not, at template parse time).

**Affected queries (11-10-2026 extract, `bound_queries_11-10-2026.csv` record numbers, header = record 1):**

| Case ID | Domain / entity / query name | FATALs |
|---------|------------------------------|--------|
| q2084 | Enroll360 / Partner ID Xwalk / Enroll360.Partner ID Xwalk.final | 7 (alias `all_ids`) |
| q2087 | Enroll360 / Partner ID Xwalk / Enroll360.Partner ID Xwalk.final_simple | 7 (alias `agg`) |
| q2088 | Enroll360 / Partner ID Xwalk / Enroll360.Partner ID Xwalk.final_simple | 12 (alias `all_ids`) |

Removing only the `union all <union_add.…>` member clears every FATAL in q2087. Ten other templates in the extract use `union [all] <union_add…>` without failing because the union is top-level, has two or more concrete query members, or no consumer reads the derived alias's columns. All thirteen templates were rechecked after the fix.

**Observed behaviour (2026-11-11 probes, `alias-scope/ua*.tsv` in the local archive documented under Entry 1):**

| Shape | Result |
|-------|--------|
| `(select t.a, t.b from tbl t <op> <union_add.X>) agg` for `<op>` = `UNION ALL`, `UNION`, `UNION DISTINCT`, `INTERSECT`, `EXCEPT` | FATAL on `agg.a`, `agg.b` |
| `(<union_add.X> union all select t.a, t.b from tbl t) agg` (variable first) | FATAL |
| CTE body `with agg as (select … union all <union_add.X>) select agg.a …` | FATAL |
| `(select t.a from tbl t union all <union_add.X> union all <union_add.Y>) agg` (one query, two variables) | FATAL |
| `((select t.a from tbl t union <q.X>) intersect (select u.a from u u)) agg` (nested, parenthesised) | FATAL |
| `(<union_add.X> union all <union_add.Y>) agg` (variables only) | FATAL — expected behaviour needs a decision (see below) |
| `(select t.a from tbl t union all select u.a from u u union all <union_add.X>) agg` (two queries, one variable) | Clean |
| `(select t.a from tbl t intersect <q.X> except <q.Y>) agg` | Clean |
| Top-level `select t.a from tbl t union all <union_add.X>` | Clean |

**Confirmed root cause (2026-11-11):** set-operation identity was inferred from at least two published `def_queryN` / query-like participant scopes. A query variable is correctly typed and retained in the AST / substitution map but intentionally has no `def_queryN`; one concrete query plus variable members therefore failed the participant-count test and the composite `def_unionN` / `def_intersectN` never received the concrete query's interface. The operator staged for a variable was also left pending and could be stamped onto the composite scope or a later participant.

**Fix rule:**

1. The interface of a set operation comes from its first **query** member, wherever variables appear in the member list (first, middle, last, or several).
2. Variable members contribute no columns and do not trigger `SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH` or `DUPLICATE_INTERFACE_COLUMNS`.
3. **Variable-only set operation (decision confirmed 2026-11-11):** the interface is unknown at template parse time. Publish an open wildcard interface so consumers do not falsely reject `alias.column`; final generated SQL remains responsible for concrete interface validation after substitution.

| Sub | Work | Status |
|-----|------|--------|
| **16.1** | Root cause in set-op interface construction for query + variable members | Complete (2026-11-11) |
| **16.2** | Fix per the rule above; decide and implement the variable-only case | Complete (2026-11-11) |
| **16.3** | Tests (below) | Complete — focused class + existing set-op gate pass |
| **16.4** | Re-check q2084, q2087, q2088 and the ten passing `union_add` templates; full suite; no change to set-op egress goldens unless explained | Complete — all 13 templates have zero FATALs under both 5.0.0-3 and 5.1.4-1; full suite passes |

**Test requirements (16.3).** The tests must prove that **every set (tuple-conjunction) operator the grammar supports** works with a query snippet variable as a member: `UNION`, `UNION ALL`, `UNION DISTINCT`, `EXCEPT`, `EXCEPT ALL`, `EXCEPT DISTINCT`, `INTERSECT`, `INTERSECT ALL`, `INTERSECT DISTINCT` (`union_clause` / `intersect_clause` with optional `set_qualifier`). For each operator cover:

- the variable as the last, first, and a middle member;
- **more than one variable** in the same operation (for example `select … union all <union_add.X> union all <union_add.Y>`);
- **more than one query** in the same operation together with one or more variables (for example `select … from a union all select … from b union all <union_add.X> union all select … from c`);
- mixed operators with precedence (`INTERSECT` binds tighter than `UNION` / `EXCEPT`) and parenthesised sub-operations that contain variables;
- each placement as a derived table read by `alias.column`, as a CTE body read by `cte.column`, as a scalar / `IN` subquery, and at top level;
- negative controls: a genuinely missing column (`agg.not_there`) stays FATAL, and two **query** members with different column counts still raise `SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH`;
- the variable-only case, with whatever behaviour 16.2 decides.

Assert both the diagnostics and the published interface (`def_queryN` output columns) of the set operation.

**Acceptance result (2026-11-11):** q1789, q1840, q1841, q1842, q1853, q1854, q1855, q1872, q1996, q2084, q2087, q2088, and q2089 all parse with zero FATALs under both 5.0.0-3 and the fixed 5.1.4-1 build. No snippet record in the extract exhibited this defect. The full `parse` regression passes: 2,194 tests, 0 failures, 0 errors, 21 skipped. Seven pre-existing symbol-table expectations were updated because they encoded the retired pending-operator leakage and absence of the now-required composite interface.

---

## Entry 17 — Merged into Entry 1

**Status:** Merged into **Entry 1** (2026-11-11)
**Former scope:** `DATEADD` / `DATEDIFF` date parts resolved as columns (q5866; false `UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES`, plus silent phantom-column lineage over physical sources).

All problem evidence, reproductions, implementation requirements, tests, and bound-query/snippet extract re-check instructions now live under **Entry 1 — Target-dialect date/time manipulation and dependency affinity**. Keep this numbered tombstone so Entries 18+ do not need renumbering; do not implement or track Entry 17 separately.

---

## Entry 18 — Snowflake non-reserved words, array literals, and walker crash after syntax errors

**Status:** Closed by owner decision (2026-11-11): known bound-query regressions fixed; unused s82233 discrepancy obsolete and immaterial, no remediation planned under this entry

**Kind:** Defect  
**Found by:** 5.0.0-3 vs 5.1.4-1 comparison of the 11-10-2026 extract

**Problem:** Words that Snowflake allows as identifiers or function names are rejected by the 5.1.4 grammar, and an array literal in a SELECT list does not parse. After the resulting syntax error the semantic walk often crashes (`ClassCastException: String cannot be cast to Map`, or `null`) or aborts with `AST_WALKER_STACK_MISALIGN`, adding application-issue FATALs on top of the syntax error.

**Affected queries and snippets (11-10-2026 extract; `q` = bound query record, `s` = snippet record):**

| Sub-problem | Case IDs | Domain / entity / query name (or bundle) | Example |
|-------------|----------|------------------------------------------|---------|
| `position` as alias / column | q5909, q5910, q5913 | PDP_UG / acs__contacts / del_src_intake_acs_contacts_PDP_UG_asof_20260910 (q5909), src_intake_acs_contacts_PDP_UG_asof_20260910 (q5910), src_intake_acs_contacts_PDP_UG_v0.2 (q5913) | `CAST(acq_contacts.<Position> AS varchar) AS position`, `acs_contacts.position` |
| `input` as CTE name | q5390, q5394 | Partner_Data_Platform / Contact Attributes Combined / con_contact_attributes_combined_full_load (q5390), pre_swap_con_contact_attributes_combined_full_load (q5394) | `with input as ( … )` |
| `path` as alias / column | q2090 | Enroll360 / Partner ID Xwalk / Enroll360.Partner ID Xwalk.ID_Array | `ARRAY_CONSTRUCT(SYSTEM_NAME\|\|'::'\|\|ID) AS PATH`, `ARRAY_APPEND(ap.PATH, se.B)` |
| `truncate(…)` function | q3399, q3438, q3490, q3546 | Enroll360 / Student Term Application / Enroll360.Student Term Application.final (q3399), .final_v2 (q3438), INACTIVE_….final (q3490), INACTIVE_….final_v2 (q3546) | `dateadd(d, truncate(try_to_decimal(…)), …)` |
| Array literal | s82233 (tuple) | Foundation / bundle `m_STUDENT_COURSE_WORK_8026_OSF:v.5001` | `(select ['STUDENT_ID', 'ACADEMIC_PERIOD', …]` |

**One-line repros (2026-11-11, all FATAL on 5.1.4-1):** `select cast(a.x as varchar) as position from t a`; `select a.position from t a`; `with input as (select 1 as x) select input.x from input`; `select array_construct(a.x) as path from t a`; `select truncate(a.x) as y from t a` (`trunc(a.x)` is clean); `select ['a','b'] as arr from t a`.

**Fix:**

- Allow `POSITION`, `INPUT`, `PATH` wherever a non-reserved identifier is allowed (column, alias, CTE / table name, qualified column part), keeping their keyword roles (`POSITION(x IN y)` / `POSITION(x, y)`, `FLATTEN(input => …, path => …)`). Audit the other keywords added since 5.0.0-3 against Snowflake's [reserved keyword list](https://docs.snowflake.com/en/sql-reference/reserved-keywords) and allow every non-reserved one as an identifier.
- Accept `TRUNCATE(expr [, scale])` as a numeric function (Snowflake synonym of `TRUNC`), distinct from DDL `TRUNCATE TABLE` (Entry 1 notes TRUNC vs TRUNCATE).
- Accept array literals `[expr, …]` as value expressions (overlaps **Entry 4.4**; verify against the delivered `expr[n]` subscript work).
- Walker robustness: after a syntax error the walker must not throw. Report the syntax error(s) only, and skip or guard the semantic step instead of raising `ClassCastException` / `null` application FATALs.

| Sub | Work | Status |
|-----|------|--------|
| **18.1** | Non-reserved identifier audit; allow `position`, `input`, `path` (+ any others found) as identifiers | Known three names verified in all six affected bound queries; broader audit not performed and not a closure requirement under the owner's accepted scope |
| **18.2** | `TRUNCATE(expr[, scale])` function | Complete for known regression: grammar forms tested and all four affected bound queries parse/walk with zero FATALs |
| **18.3** | Array literal value expression (with Entry 4.4) | Obsolete for this discrepancy: s82233 is unused; no compatibility fix planned. General array work remains independently scoped under Entry 4.4 |
| **18.4** | Walker guard: no exceptions after syntax errors (repro: any 18.1–18.3 case) | Not implemented; follow-on diagnostic for unused s82233 accepted without remediation under this entry, not a claim of general walker robustness |
| **18.5** | Tests per word in each identifier position plus its keyword role; token-ID golden impact handled per `.cursor/rules/lexer-token-id-golden-policy.mdc` (substitution list, no bulk update without approval) | Known-word syntax/keyword-role coverage passes in `SqlDatetimeLexerBaselineTest`; broader coverage not required for accepted closure scope |
| **18.6** | Re-check q2090, q3399, q3438, q3490, q3546, q5390, q5394, q5909, q5910, q5913, s82233; full suite | Sample comparison complete: all ten bound queries fixed; s82233 difference accepted as immaterial. Subsequent testing-extension full suite passed: 2,643 passed, 0 failed (Entry 1 Slice A) |

### Entry 18 sample comparison (2026-11-11)

Rebuilt the current 5.1.4-1 fat JAR using
`mvn -Pisolated-build -pl parse -Dmaven.test.skip=true package`. Replayed the same
frozen inputs through both that JAR and the archived 5.0.0-3 JAR with
`BatchParse.java`: bound queries use `SQL`, s82233 uses `TUPLE`. Both batch
processes exited 0 and returned all 11 records; process success alone is not
parse success, so FATAL counts were checked explicitly.

| Construction | Cases | 5.0.0-3 FATALs per case | Pre-fix 5.1.4 FATALs per case | Current FATALs per case |
|--------------|-------|------------------------|-----------------------------|------------------------|
| `position` alias/column | q5909, q5910, q5913 | 0 | 3 | 0 |
| `input` CTE | q5390, q5394 | 0 | 3 | 0 |
| `path` alias/column | q2090 | 0 | 4 | 0 |
| `truncate(...)` | q3399, q3438, q3490, q3546 | 0 | 2 | 0 |
| Array literal | s82233 | 0 | 2 | 2 |

All ten bound-query keyword/function regressions are resolved in this sample.
The four TRUNCATE query ASTs match 5.0.0-3 exactly. The other six match after
comparison-only normalization of 5.1.x numbered CTE storage
(`with.N={alias, cte}`) to legacy alias-keyed CTE storage; no other AST differences
remain. This does not claim dictionary/interface parity across parser versions.
The five focused tests also pass (0 failures/errors), covering identifier
positions, POSITION IN/comma forms, FLATTEN argument keywords, numeric TRUNCATE
forms, and DDL TRUNCATE.

**Closure decision (2026-11-11):** The owner checked usage and confirmed that the
snippets in Foundation bundle `m_STUDENT_COURSE_WORK_8026_OSF:v.5001`, containing
s82233, are never used. The s82233 discrepancy is **obsolete and immaterial**;
the 5.0.0-3 versus 5.1.4-1 behavior difference will **not be fixed** under Entry 18.
Legacy acceptance was not correct array support: its captured AST represented
the 29 strings as separate SELECT literals rather than one array.

Entry 18 is therefore **closed**: all ten relevant bound-query regressions are
fixed, and the unused snippet's syntax failure and follow-on walker
`String`-to-`Map` diagnostic are accepted without remediation. This is a scoped
owner acceptance, not proof of general array support, a completed broader keyword
audit, or general walker robustness. Entry 4.4's independent array work is
unchanged. Entry 1 Slice A's full-suite gate was subsequently satisfied as
recorded above. No parser code or golden was changed for this closure.

Frozen inputs, old/current result JSONL, captured ASTs, probe logs, and SHA-256
manifest are retained in the local archive's
`comparison/runs/entry18-keywords-20261111T1838/` (archive location under Entry 1).

---

## Entry 19 — RMCP Phase 2 acceptance-test closure

**Status:** Complete (closed 2026-11-16) — residual acceptance promises from the September 2026 RMCP plan are now closed without new parser/grammar/walker defects.
**Kind:** Test / verification (implement a parser fix only if these fixtures expose a real regression)

The old Phase 2 plan is otherwise accounted for: wildcard set-op handling (2.1), `VALUES` aliases (2.2), `WITHIN GROUP` grammar and `LISTAGG` coverage (2.3), nested / product `CASE` and function-result subscripts (2.4 / 2.6), CTE dictionary policy (2.7 / 2.9), and the 74-query timeout program (2.8 / 2.11) are delivered or closed. The Jinja-authored DNC rollup (2.5) remains explicitly tracked as **Entry 5.4**.

| Sub | Residual acceptance work | Evidence / boundary | Status |
|-----|--------------------------|---------------------|--------|
| **19.1** | Retire the invalid 2.2 fixture variant (mismatched alias usage) and keep the acceptance shape as a valid alias-consistent unparenthesized `FROM VALUES` UNION branch with joined source and `COALESCE(pso.sort_order, 999)` | Existing `VALUES` alias coverage plus manual replay confirm parser behavior is correct when aliasing is valid; unresolved-alias form remains a negative control | Complete (no parser change) |
| **19.2** | Complete shared `WITHIN GROUP` aggregate coverage for `PERCENTILE_CONT` and `PERCENTILE_DISC`; verify multiple sort keys and null ordering | `SqlEventWalkerWithinGroupOrderedAggregateTests` now includes location-matrix coverage for both percentile functions (SELECT-clause matrix, JOIN ON, UPDATE SET/WHERE, INSERT SELECT, DELETE WHERE, prior-select-list alias lineage), with explicit multi-key ordering and `NULLS FIRST/LAST` assertions | Complete |
| **19.3** | Complete `ARRAY_AGG … WITHIN GROUP` / `OVER` and compound-array coverage | Implemented in `SqlEventWalkerWithinGroupOrderedAggregateTests`: full location matrix, column-resolution diagnostics, set-op integration, and compound-array (`ARRAY_AGG(ARRAY_CONSTRUCT(...))`) control | Complete (Entry 4.3 accepted) |
| **19.4** | Run the focused Phase 2 regression classes and record whether 19.1–19.3 required code changes | Focused bundle passed: `SqlEventWalkerUnparenthesizedValuesTests` (19.1 shape), `SqlEventWalkerWithinGroupOrderedAggregateTests` (19.2 matrix), `SqlEventWalkerSetOpScopingGateTests` (wildcard/set-op controls + width-mismatch negatives), and `SqlEventWalkerSetOpQueryVariableTests` (unresolved alias/column fatal controls + width mismatch with variable members) | Complete |

For **19.1**, use a physical table in place of `{{ ref('sort_orders') }}` so the Phase 2 grammar/walker behavior is independently testable. Add the Jinja-authored variant when **Entry 5.1** lands. The previously discussed failing variant was invalid SQL (alias mismatch) and is retired as a positive fixture. Keep it only as a negative control. The valid alias-consistent variant parses correctly and requires no parser/grammar/walker change.

Candidate SQL skeleton for **19.1** (physical-table variant):

```sql
SELECT
    default_stream.partner_id,
    default_stream.partner_name,
    default_stream.segment,
    default_stream.region,
    default_stream.status,
    COALESCE(pso.sort_order, 999) AS sort_order,
    default_stream.is_default
FROM VALUES
    (1001, 'Unknown Partner', 'GEN', 'NA', 'active', 1),
    (1002, 'Other Partner',   'GEN', 'NA', 'active', 1)
AS default_stream (partner_id, partner_name, segment, region, status, is_default)
LEFT JOIN partner_sort_orders pso
    ON pso.partner_id = default_stream.partner_id

UNION ALL

SELECT
    live.partner_id,
    live.partner_name,
    live.segment,
    live.region,
    live.status,
    COALESCE(pso.sort_order, 999) AS sort_order,
    0 AS is_default
FROM live_partner_stream live
LEFT JOIN partner_sort_orders pso
    ON pso.partner_id = live.partner_id
```

For **19.2**, assert AST, interface, dictionaries, symbol table, and `within_group_ordered_by` lineage. Do not add unsupported aggregate names merely to satisfy the old prose; test the four aggregates admitted by `ordered_aggregate_function` (`LISTAGG`, `ARRAY_AGG`, `PERCENTILE_CONT`, `PERCENTILE_DISC`) and align dialect restrictions with current target documentation.

**19.4 execution record (2026-11-16):**

- Command:
  - `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerUnparenthesizedValuesTests,sql.walker.SqlEventWalkerWithinGroupOrderedAggregateTests,sql.walker.SqlEventWalkerSetOpScopingGateTests,sql.walker.SqlEventWalkerSetOpQueryVariableTests test`
- Result:
  - `SqlEventWalkerWithinGroupOrderedAggregateTests`: 21/21 pass
  - `SqlEventWalkerSetOpScopingGateTests`: 17/17 pass
  - `SqlEventWalkerSetOpQueryVariableTests`: 7/7 pass
  - `SqlEventWalkerUnparenthesizedValuesTests`: 4/4 pass
  - Bundle total: 49 tests, 0 failures, 0 errors, 0 skipped
- Code-change conclusion for 19.1–19.3:
  - **19.1:** invalid-alias fixture retired; no parser/grammar/walker change required.
  - **19.2:** test expansion only (location/resolution matrix for `PERCENTILE_CONT`/`PERCENTILE_DISC`); no parser/grammar/walker change required.
  - **19.3:** completed via Entry 4.3 test expansion (`ARRAY_AGG` location/resolution matrix + set-op/compound-array integration); no parser/grammar/walker change required.

---

## Entry 20 — Qualified column variables on visible query aliases

**Status:** Complete (closed 2026-11-13): core parser fix implemented,
clause-position golden coverage added, targeted + full parser suites pass, and
the last open bound-query parity issue for the 5.0.0 → 5.1.4 upgrade is
resolved.  
**Kind:** Defect / resolution and diagnostic enhancement.

**Evidence:** The original s85047 TUPLE snippet uses
`sub.<Source Contact ID>` in a join against a visible derived-table alias.
5.0.0-3 reports no FATAL; current 5.1.4 reports
`QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS` because it looks up the
column-variable placeholder as a literal output name. The separate
ambiguous-column warning in that snippet is not automatically resolved by
this entry. The original input and comparison are in the local-only archive
under `comparison/runs/tuplewrap-differences-20261113/`.

**Reduced reproduction (verified against both JARs):**

```sql
SELECT sub.id
FROM (SELECT id FROM t) sub
JOIN b ON sub.<Contact ID> = b.<Contact ID>
```

Through `SQL` this has zero FATALs on 5.0.0-3 and one on current 5.1.4:
`Qualified column '<Contact ID>' ... was not found in output interface of query
alias 'sub'.` The AST identifies `sub.<Contact ID>` as a `column` substitution,
not a literal column. Parenthesizing the query and using the `TUPLE` endpoint
reproduces the same difference. An even smaller SELECT-list variant is
`SELECT sub.<Contact ID> FROM (SELECT id FROM t) sub`. The synthetic inputs,
both JAR results, and the typed-substitution AST are retained locally under
`comparison/runs/s85047-qualified-variable-20261113/`.

**Goal and resolution policy:**

- Recognize the typed, qualified column substitution in column resolution.
  Once the qualifier is proven visible under the existing scope rules, do not
  FATAL merely because the placeholder name is absent from that query alias's
  interface. Its eventual binding must name an actual exported column.
- Emit a **warning** for this deferred check,
  identify the variable, query alias, and source location, and explain the
  binding requirement. Do not present an unresolved variable as a validated
  concrete column. Whether and how binding is validated after substitution is
  a separate consumer/generation concern; do not claim it already happens.
- Preserve typed substitution collection, AST, dictionaries, symbol-table
  lineage, scope visibility, and normal handling of actual column names.
  Continue to FATAL for a missing or invisible qualifier and for a concrete
  column absent from a closed query interface. Do not suppress unrelated
  diagnostics, including s85047's ambiguity warning.
- This entry covers the **variable-outside** form. A concrete column read
  outside a query that exports a variable without a literal alias is a
  distinct case; do not automatically relax its validation.

**Phases and acceptance:**

1. Trace how typed substitutions and source qualifiers reach the resolution
   and diagnostic paths; reuse existing scope and diagnostic machinery.
2. Implement the targeted deferral and the warning described above. Verify
   `SQL` and `TUPLE`, SELECT and JOIN/ON placements, and visible
   derived-table and CTE aliases; exercise physical sources and nested scopes
   as controls.
3. Keep negative controls: `sub.missing` against
   `(SELECT id FROM t) sub` still FATALs; `ghost.<Contact ID>` with no visible
   `ghost` still FATALs; an inaccessible sibling or child alias remains
   invisible. Check substitutions and real columns in interfaces,
   dictionaries, and lineage without inventing a resolved column name.
4. Re-run the original s85047 and compare AST, substitutions, diagnostics,
   and resolution artifacts. Re-evaluate the seven historically classified
   variable-outside bound queries q5201 and q5286–q5291. The owner approved
   mirror-image bound queries q5314 and q5335 and snippets s8491 and s8492 for
   release with fixed-alias or concrete-column author remediation; their
   original FATALs are not parser fixes. Record results
   individually; do not presume all Pattern 6 cases have the same disposition.
5. Re-run the relevant bound-query and snippet comparisons and document any
   remaining regressions. Update directly affected consumer guidance. Close
   this entry only when these checks pass; no 5.0.0 → 5.1.4 extract parity
   sign-off before closure.

**Out of scope:** implementing broader variable binding, weakening concrete
column or alias validation, automatically accepting the other Pattern 6
cases, or changing the DATEADD test goldens.

---

## Retired documents (2026-09-28)

| File | Superseded by |
|------|----------------|
| `parser-defects-enhancements-workplan.md` | Entries **1–8** + **Completed baselines** |
| `extract-dialect-capture-workplan.md` | **Completed baselines** |
| `java-21-upgrade-workplan.md` | **Completed baselines** |
| `ddl-structured-options-parsing-workplan.md` | **Entry 9** |
| `sql-statement-generator-completion-workplan.md` | **Entry 10** |
| `coverage/sql_walker_exit_method_coverage-workplan.md` | **Entry 11** (+ gaps doc) |
| `helper-dead-code-hygiene-workplan.md` | **Entry 12** |

Historical prose: `git log --follow -- <path>`
