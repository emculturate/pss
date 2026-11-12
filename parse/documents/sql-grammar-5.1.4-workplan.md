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
| **1** | Target-dialect date/time manipulation + Snowflake/Postgres dependency affinity | Enhancement | **In progress** — Slice A token batch generated/compiled; manual goldens and full-suite gate pending |
| **2** | Snowflake PIVOT quoted unaliased IN-list identifiers | Enhancement | **Not started** |
| **3** | Snowflake `PARSE_*` + `:` semi-structured field access | Enhancement | **Not started** |
| **4** | Snowflake `ARRAY` syntax and functions (5.1–5.12) | Enhancement | **Not started** |
| **5** | Simple Jinja / dbt substitutions (6.1–6.4) | Enhancement | **Not started** |
| **6** | Deep Jinja / SCRIPT prefix *(optional)* (7.x) | Enhancement | **Not started** |
| **7** | Panto variable types × parser endpoints — test backlog (8.1–8.2) | Test / docs | **Not started** |
| **8** | Panto cardinality & grammar-safe dynamic AST expansion (9.x) | Enhancement (exploratory) | **Not started** |
| **9** | DDL structured options (`IF NOT EXISTS`, `OR REPLACE`, …) | Enhancement | **Not started** |
| **10** | SQL statement generator — post-milestone completion | Enhancement | **Not started** (milestone ✅) |
| **11** | Walker `exit*` JaCoCo coverage — Tier 2 / 3 residuals | Test / hygiene | **Optional** (Tier 1 ✅) |
| **12** | Helper dead-code hygiene — Phases C–D | Hygiene | **Optional** (A–B ✅) |
| **13** | SELECT-list subquery alias scope (false `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE`) + operand `lookup` wrapper | Defect | **Complete** (2026-11-10) |
| **14** | Join extension present → `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE` reported as severe warning + `JOIN_EXTENSION_REQUIRED` (whole-query) | Defect (release blocker) | **Complete** (2026-11-10) |
| **15** | Scope-aware join extension deferral (per-scope diagnostic capture) | Enhancement | **Not started** (follows **14**) |
| **16** | Set operation with a query variable member loses its output interface (false `QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS`) | Defect (release blocker) | **Complete** (2026-11-11) |
| **17** | `DATEADD` / `DATEDIFF` date parts resolved as columns | Defect | **Merged into Entry 1** (2026-11-11) |
| **18** | Snowflake non-reserved words rejected (`position`, `input`, `path`, `truncate()`), array literals, and walker crash after syntax errors | Defect | **Closed** (2026-11-11) — ten bound-query regressions fixed; unused s82233 discrepancy obsolete and immaterial, accepted without remediation |
| **19** | RMCP Phase 2 acceptance-test closure (`VALUES` joined UNION branch; ordered aggregates) | Test / verification | **Partial** (implementation delivered) |

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
| `EXTRACT(part FROM value)` | Shared shell | Existing `extract_expression`; classify `part` using the part matrix below | Part-dependent or neutral | Preserve existing AST and diagnostics |
| `DATE_PART(part FROM value)` | Shared parser extension | Existing `date_part_expression`; classify `part` | Part-dependent or neutral | This repo intentionally accepts the FROM form |
| `DATE_PART('part', value)` | Shared Snowflake/Postgres name | Continue through function parsing or a shared dedicated rule; classify the literal part | Part-dependent or neutral | Existing comma-form affinity tests must remain green |
| `DATE_TRUNC('part', value)` | Shared Snowflake/Postgres name | Shared production; classify the part and any dialect-specific overload | Part-dependent or neutral | Do not flag from the function name alone |
| `DATE_BIN(stride, source, origin)` | PostgreSQL | Dedicated Postgres production | Postgres | Interval stride; three arguments |
| `AGE(…)`, `MAKE_DATE(…)`, `MAKE_TIME(…)`, `MAKE_TIMESTAMP(…)`, `MAKE_TIMESTAMPTZ(…)`, `MAKE_INTERVAL(…)` | PostgreSQL | Dedicated built-in alternatives or semantic function-shape registry | Postgres | A schema-qualified same-name UDF is neutral |
| `JUSTIFY_DAYS(…)`, `JUSTIFY_HOURS(…)`, `JUSTIFY_INTERVAL(…)` | PostgreSQL | Semantic function-shape registry | Postgres | Interval argument |
| `CLOCK_TIMESTAMP()`, `STATEMENT_TIMESTAMP()`, `TRANSACTION_TIMESTAMP()`, `TIMEOFDAY()` | PostgreSQL | Zero-argument built-in alternatives / semantic registry | Postgres | Do not confuse shared `CURRENT_TIMESTAMP` with these |

The Postgres rows are part of the dialect-symmetry requirement for Entry 1. They may be delivered after Snowflake slices A–C, but the grammar/registry design must leave explicit branches for them and must not classify their names as ordinary columns.

#### Approved parser grammar design (2026-11-11)

Dedicated date/time productions must precede the generic `value_expression_primary` / `routine_invocation` path so a level such as `year` is parsed as part metadata, never as a `column_reference`. Existing `extract_expression`, `date_part_expression`, and their AST contracts remain unchanged.

```antlr
predicand_primary
  : extract_expression
  | date_part_expression
  | datetime_manipulation_expression      // NEW: before generic functions
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
  | datetime_manipulation_expression      // NEW
  | value_expression_primary
  ;

datetime_manipulation_expression
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
  invocation=DATEADD,
  level={canonical=YEAR, text=year, form=KEYWORD},
  increment={literal=1},
  value={column={name=created_at}}
}}

DATE_TRUNC('month', created_at)

{datetime_function={
  invocation=DATE_TRUNC,
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

Generators dispatch once on `datetime_function`, then use `invocation` to select an ordered signature. Never infer SQL argument order from map insertion order.

| Invocation family | Permitted subtree keys in SQL argument order |
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
| **2 Grammar + AST** | Implement the approved dedicated productions, `datetime_part`, `datetime_function` contract, and invocation signature registry incrementally; branch only when syntax, arity, AST, or keyword role differs |
| **3 Walker + affinity** | Add every exit method; exclude parts from symbol collection; stamp predicand substitutions in expression fields; route rule/function/part affinity through the existing statement collector |
| **4 Focused tests** | Add `SqlEventWalkerSnowflakeDatetimeManipulationTests` plus PostgreSQL/shared matrices, AST signatures, rendering forms, counters/warnings/mixed fatal, SCRIPT reset, and fec/`year` CASE |
| **5 Corpus + delivery** | Rebuild jar; re-run bound-query and snippet extracts; prove q5866 and all date/time matches; compare FATALs, AST/interfaces, dictionaries, lineage, and dialect diagnostics; finish generator/docs/release notes |

### Delivery slices

| Slice | Scope |
|-------|--------|
| **A — lexer baseline** | In one batch add or confirm dedicated late lexer entries for every bare built-in needed by Slices B–E: add/diff aliases, trunc/slice/day-boundary names, `DATE_BIN`, `AGE`, `MAKE_*`, `JUSTIFY_*`, and timestamp clock functions. Do not add lexer tokens for part aliases or extensible parts. Regenerate and provide sorted token substitutions; golden token-ID edits are user-only (2026-11-11 clarification supersedes earlier bulk-update approval). Run the full suite after the user's edits. No later slice may change lexer numbering without separate review. |
| **B — add family + registries** | Shared part/signature registries; `datetime_function`; `DATEADD`, `TIMEADD`, `TIMESTAMPADD`; core AST/symbol-table/dialect tests; q5866 clean with `year` absent from dictionaries and lineage |
| **C — difference + truncation** | `DATEDIFF`, `TIMEDIFF`, `TIMESTAMPDIFF`, shared `DATE_TRUNC`, datetime `TRUNC`; preserve neutral numeric `TRUNC` |
| **D — remaining Snowflake** | `TIME_SLICE`, `LAST_DAY`, `NEXT_DAY`, `PREVIOUS_DAY` |
| **E — PostgreSQL symmetry** | `DATE_BIN`, `AGE`, `MAKE_*`, `JUSTIFY_*`, timestamp clock functions, named `MAKE_INTERVAL`, shared/target part-affinity tests |
| **F — delivery closure** | Generator support for `datetime_function`, `pss-parse-docs` / release notes, bound-query + snippet corpus comparison, full regression; generator follow-up may remain under **Entry 10** only if explicitly deferred |

**Story minimum:** Slices **A–B** + regression tests. **Full Entry 1:** A+B+C+D+E+F.

**Slice A, step 1 complete (2026-11-11):** Added 26 late date/time function
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
are verified. The last isolated full suite ran 2,199 tests: 1,680 passed, 498
failed on remaining `<393>` versus `<419>` expectations, 0 errors, 21 skipped.
The user's manual golden edits and a green full-suite gate are still required
before Slice A closes. Step 1 is complete, not all of Phase 1; date/time AST,
symbol-table, and affinity implementation have not begun.

### Acceptance

Structured AST (not `parameters.1.column` for parts); `TIMESTAMPADD` parses; fec/`year` CASE and q5866 are clean; no date/time part appears as a physical/query column or unresolved reference; EXTRACT/DATE_PART FROM unchanged; every target-only function/part/construct in the matrices raises the matching statement dialect flag; shared forms raise neither; mixed affinity remains FATAL per statement; bound-query and snippet extract re-check is documented with no unexplained regressions; 5.1.4 jar + grammar extension doc.

### Snowflake reference

[DATEADD](https://docs.snowflake.com/en/sql-reference/functions/dateadd), [TIMEADD](https://docs.snowflake.com/en/sql-reference/functions/timeadd), [TIMESTAMPADD](https://docs.snowflake.com/en/sql-reference/functions/timestampadd), [DATEDIFF](https://docs.snowflake.com/en/sql-reference/functions/datediff), [DATE_TRUNC](https://docs.snowflake.com/en/sql-reference/functions/date_trunc), [TIME_SLICE](https://docs.snowflake.com/en/sql-reference/functions/time_slice), etc.

---

## Entry 2 — Snowflake PIVOT quoted unaliased identifiers

**Status:** Not started  
**Spec (do not duplicate here):** [snowflake-pivot-quoted-identifier-parser-brief.md](./snowflake-pivot-quoted-identifier-parser-brief.md)

**Summary:** Lexer/parser must treat older unaliased PIVOT output names as delimited identifiers (`"'diq_entry_year'"`, numeric `"1"`…). Register IN-list values in `derivation.source_columns`; resolve as column refs. Newer `IN ('x' AS alias)` must keep working.

**Acceptance:** Both fixtures in the brief parse with no FATAL; derivation and location refs correct.

---

## Entry 3 — Snowflake `PARSE_*` and `:` field access

**Status:** Not started

**Problem:** `PARSE_URL(url, 1):scheme::varchar` — colon path after function call is not accepted (`NoViableAltException` on `:`). Same pattern for other semi-structured PARSE results.

**Scope minimum:** `PARSE_URL`; extend to `PARSE_JSON`, `PARSE_XML`, `PARSE_IP`, `TRY_PARSE_JSON` if grammar construction is shared.

**Work:** Grammar for post-call `:` paths (distinct from `::` cast and bind variables); walker AST; column resolution on function arguments; exemplar predicand + SELECT tests.

**Repro / detail:** git history — former `parser-defects-enhancements-workplan.md` Phase 3.

---

## Entry 4 — Snowflake ARRAY syntax and functions

**Status:** Not started — implement **5.x independently** unless syntax shares one production.

| Sub | Construction |
|-----|----------------|
| **4.1** | `ARRAY_CONTAINS` + VARIANT syntax |
| **4.2** | `ARRAY_CONSTRUCT` (compact / structured) |
| **4.3** | `ARRAY_AGG` + `WITHIN GROUP` / `OVER` |
| **4.4** | Array literals and subscripts *(overlaps delivered `expr[n]` — verify gap only)* |
| **4.5** | `FLATTEN` / `LATERAL FLATTEN` |
| **4.6** | `ARRAY_UNION_AGG` / `ARRAY_UNIQUE_AGG` |
| **4.7** | Binary / set array operations |
| **4.8** | `APPEND` / `PREPEND` / `INSERT` / `REMOVE` / `REMOVE_AT` |
| **4.9** | Transforms with extra arguments (`ARRAY_TO_STRING`, etc.) |
| **4.10** | Unary array → array/scalar (`ARRAY_DISTINCT`, …) |
| **4.11** | Conversion, predicates, split-to-array |
| **4.12** | `FILTER` / `TRANSFORM` / `REDUCE` higher-order |

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

**Status:** Phases **A–B** ✅ (Aug 2026); **C–D** optional

**Policy:** Caller audit before delete; JaCoCo is heat map only, not delete oracle. Gate: `mvn -Psmoketest-quality-gate test`.

| Phase | Work | Status |
|-------|------|--------|
| **12.A** | Tier-1 dead public API in symbol-tree helpers | ✅ |
| **12.B** | Cascading private orphans + walker orphans | ✅ |
| **12.C** | Fresh JaCoCo heat map on megaclass helpers | Optional |
| **12.D** | Wrapper hygiene (deprecated zero-caller stubs only) | Optional — mostly skip |

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
| **18.6** | Re-check q2090, q3399, q3438, q3490, q3546, q5390, q5394, q5909, q5910, q5913, s82233; full suite | Sample comparison complete: all ten bound queries fixed; s82233 difference accepted as immaterial. Green full-suite gate remains tracked under Entry 1 Slice A, not claimed by this closure |

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
audit, general walker robustness, or a green full suite. Entry 4.4's independent
array work and Entry 1 Slice A's full-suite gate are unchanged. No parser code or
golden was changed for this closure.

Frozen inputs, old/current result JSONL, captured ASTs, probe logs, and SHA-256
manifest are retained in the local archive's
`comparison/runs/entry18-keywords-20261111T1838/` (archive location under Entry 1).

---

## Entry 19 — RMCP Phase 2 acceptance-test closure

**Status:** Partial — parser implementation is present; close the residual test promises from the September 2026 RMCP plan before retiring its duplicate copy.
**Kind:** Test / verification (implement a parser fix only if these fixtures expose a real regression)

The old Phase 2 plan is otherwise accounted for: wildcard set-op handling (2.1), `VALUES` aliases (2.2), `WITHIN GROUP` grammar and `LISTAGG` coverage (2.3), nested / product `CASE` and function-result subscripts (2.4 / 2.6), CTE dictionary policy (2.7 / 2.9), and the 74-query timeout program (2.8 / 2.11) are delivered or closed. The Jinja-authored DNC rollup (2.5) remains explicitly tracked as **Entry 5.4**.

| Sub | Residual acceptance work | Evidence / boundary | Status |
|-----|--------------------------|---------------------|--------|
| **19.1** | Add the exact 2.2 shape: an explicit-column UNION branch over `FROM VALUES (…) AS default_stream (cols)` followed by a joined source, with `COALESCE(pso.sort_order, 999)` and `default_stream.*` reads | `SqlEventWalkerUnparenthesizedValuesTests` covers parser endpoints and alias columns; JOIN tests cover parenthesized `VALUES`, but not this full unparenthesized UNION-branch construction | Not started |
| **19.2** | Complete shared `WITHIN GROUP` aggregate coverage for `PERCENTILE_CONT` and `PERCENTILE_DISC`; verify multiple sort keys and null ordering | Grammar already admits both; `SqlEventWalkerWithinGroupOrderedAggregateTests` currently exercises only `LISTAGG` | Not started |
| **19.3** | Complete `ARRAY_AGG … WITHIN GROUP` / `OVER` and compound-array coverage | Owned by **Entry 4.3**, not duplicated here; close this row when 4.3 accepts | Not started (Entry 4.3) |
| **19.4** | Run the focused Phase 2 regression classes and record whether 19.1–19.3 required code changes | Include wildcard set-op controls, unresolved aliases/columns, and concrete set-op width mismatch negatives | Not started |

For **19.1**, use a physical table in place of `{{ ref('sort_orders') }}` so the Phase 2 grammar/walker behavior is independently testable. Add the Jinja-authored variant when **Entry 5.1** lands. Require no syntax recovery, no `QUALIFIED_COLUMN_NOT_FOUND` for `pso` or `default_stream`, and a seven-column set-op interface. Keep a missing-alias negative control.

For **19.2**, assert AST, interface, dictionaries, symbol table, and `within_group_ordered_by` lineage. Do not add unsupported aggregate names merely to satisfy the old prose; test the four aggregates admitted by `ordered_aggregate_function` (`LISTAGG`, `ARRAY_AGG`, `PERCENTILE_CONT`, `PERCENTILE_DISC`) and align dialect restrictions with current target documentation.

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
