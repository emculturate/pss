# SQL grammar and walker — 5.1.4 master workplan

**API / docs version target:** `5.1.4` (coordinate with parent `pss-parent` / `pss-parse` artifact when shipping)  
**Branch context:** `Spring-2026-Extensions`  
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
| **1** | Snowflake date/time manipulation (`DATEADD`, `TIMEADD`, `TIMESTAMPADD`, …) | Enhancement | **Not started** |
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

---

## Completed baselines (no open entry — do not reopen)

| Program | Shipped / closed | Where detail lives |
|---------|------------------|-------------------|
| **EXTRACT / DATE_PART `FROM`** | ✅ Aug 2026 | `SqlEventWalkerExtractTests`; git: `extract-dialect-capture-workplan.md` |
| **Symbol table resolution consolidation** Phases 1–20 | ✅ Aug 2026 | [symbol-table-resolution-consolidation-worklist.md](./symbol-table-resolution-consolidation-worklist.md) (historical) |
| **RMCP parser-defects Phase 2** (set-op, `VALUES`, `CASE`, parse perf 2.8–2.11, etc.) | ✅ 2026-08–09 | [parse/docs/rmcp-handoff/5.1.3-panto-outstanding/](./docs/rmcp-handoff/5.1.3-panto-outstanding/); set-op egress: `.cursor/rules/set-op-convert-egress-scoping.mdc` |
| **Java 21 / parent 5.1.4-1 toolchain** | ✅ Sep 2026 | git: `java-21-upgrade-workplan.md` |
| **2.5 Jinja DNC rollup closure** | ⏳ | **Entry 5** subtask **5.4** (depends on **5.1**) |

---

## Entry 1 — Snowflake date/time manipulation functions

### Product story (authoritative)

Update and release the parser jar to recognize and construct proper AST and symbol table entries for the **DATEADD** and **TIMEADD** functions in Snowflake's SQL dialect. If this assumption is incorrect, defer this story until the capability is available in a newer parser.

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

**Testing:** (1) Basic dateadd/timeadd; (2) rule templates for `DATEADD` / `TIMEADD` / `TIMESTAMPADD`; (3) symbol tables resolve increment and date/time expr, **ignore** parts; predicand substitutions in slots 2–3; nested complexity; fec/`year` regression (formerly “parser-defects Phase 4”).

### Baseline (2026-09-28)

Most names parse as generic `function` via `routine_invocation`; unquoted parts become **`column`** refs. **`TIMESTAMPADD` fails parse** (`TIMESTAMP` lexer split). No dedicated AST or walker tests.

### Work phases

| Phase | Work |
|-------|------|
| **1 Design** | AST keys (`level` / `increment` / expr vs align with `extract`); part subsets per Snowflake; `TIMESTAMPADD` lexer; TRUNC vs TRUNCATE |
| **2 Grammar** | Dedicated `*_expression` rules on `predicand_primary` + value primaries; validated part nonterminals |
| **3 Walker** | All `exit*`; exclude parts from symbol collection; predicand stamping on increment/expr |
| **4 Tests** | `SqlEventWalkerSnowflakeDatetimeManipulationTests` — story matrix + fec/`year` CASE |

### Delivery slices

| Slice | Scope |
|-------|--------|
| **A** | `DATEADD`, `TIMEADD`, `TIMESTAMPADD` + core symbol-table tests |
| **B** | Diff/trunc family: `DATEDIFF`, `TIMEDIFF`, `TIMESTAMPDIFF`, `DATE_TRUNC`, `TRUNC` |
| **C** | `TIME_SLICE`, `LAST_DAY`, `NEXT_DAY`, `PREVIOUS_DAY` |
| **D** | `pss-parse-docs` / release notes; generator follow-up → **Entry 10** if needed |

**Story minimum:** slice **A** + regression tests. **Full Entry 1:** A+B+C+D.

### Acceptance

Structured AST (not `parameters.1.column` for parts); `TIMESTAMPADD` parses; fec/`year` CASE clean; EXTRACT/DATE_PART FROM unchanged; 5.1.4 jar + grammar extension doc.

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
