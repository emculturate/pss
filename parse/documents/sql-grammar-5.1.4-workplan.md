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
| **13** | SELECT-list subquery alias scope (false `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE`) + operand `lookup` wrapper | Defect | **Complete** (2026-11-10) |
| **14** | Join extension present → `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE` reported as severe warning + `JOIN_EXTENSION_REQUIRED` (whole-query) | Defect (release blocker) | **Complete** (2026-11-10) |
| **15** | Scope-aware join extension deferral (per-scope diagnostic capture) | Enhancement | **Not started** (follows **14**) |

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
| **14.4** | Corpus re-check of the 113 remaining bound queries (`.tmp/alias-scope/remaining113-entry14.out`) | Complete — see results below |

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
