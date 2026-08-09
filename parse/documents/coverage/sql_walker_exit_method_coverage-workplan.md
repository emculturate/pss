# SqlParseEventWalker `exit*` coverage — workplan

**Status:** ✅ Tier 1 complete (Aug 2026 JaCoCo); Tier 2 **19** substantial + Tier 3 **53** minor `exit*` gaps; **211** fully line-covered / **283** `exit*` total.
**Branch context:** `Spring-2026-Extensions` (or follow-on)  
**Goal:** At least one exemplar test per **main-grammar** `exit*` path so the full suite exercises every listener exit that corresponds to a user-visible SQL construct.

**Not in scope:** 100% line/branch coverage inside every `exit*` body, lexer-only tokens, or private walker helpers unless needed to reach a grammar exit.

---

## How we measure

| Step | Command / artifact |
|------|-------------------|
| Run full suite + JaCoCo | `cd parse && mvn verify` |
| Report | `parse/target/site/jacoco/jacoco.xml` (HTML under `target/site/jacoco/`) |
| Class under test | `sql.walker.SqlParseEventWalker` |
| Listener methods | `exit*` only (283 methods; ANTLR-generated names ↔ grammar rules) |

**JaCoCo snapshot (Aug 9, 2026):** `mvn verify` — **0** Tier 1, **19** Tier 2, **53** Tier 3, **211** full / **283** `exit*`. Priority queue: [gaps doc Tier 2 table](sql_walker_exit_method_gaps.md#tier-2--substantial-gaps-jacoco-aug-2026).

**Tier definitions (Aug 2026 baseline: 1748 tests run, 0 failures):**

| Tier | JaCoCo signal | Work |
|------|----------------|------|
| **1** | **0 lines covered** on the `exit*` method | Add at least one new parse/walk test that enters the grammar alternative end-to-end |
| **2** | Method executed but **≥3 missed lines** or **&lt;85%** line coverage on that method | Extend tests to hit uncovered branches (second syntax variant, error path, or DDL/DML option tail) |
| **3** | Method executed; **1–2 missed lines** and **≥85%** coverage | Optional polish — small branch or rare alternative |

**Done criterion per substep:** Re-run `mvn verify`; the named `exit*` method shows **no missed lines** (or team agrees to accept residual for Tier 3 only).

**Test placement (default):** Put exemplars in the feature test class that already owns the grammar family (pivot/unpivot, table functions, DML, joins, GROUP BY, etc.). Use `assertDiagnosticAtPosition` only when the scenario intentionally emits walker diagnostics.

---

## Tier 1 — No test hits (`exit*` never executed)

Each item is one grammar alternative with **zero** JaCoCo line hits today.

### T1.1 `exitNamed_columns_join` — `named_columns_join` (JaCoCo Aug 2026: 4/4 lines, 100%, missed=0) — **COMPLETE**

- [x] Add SELECT (or DML) using **`JOIN … USING (col1, col2)`** (named columns join, not `ON`).
- **Verify:** `exitNamed_columns_join` covered; AST/symbol smoke as needed.

**Status (Spring 2026):** Tier 1.1 is **closed**. The original bar was walker exit coverage plus light smoke; the corpus now also locks down **JOIN USING** symbol behavior (not required to mark T1.1 done).

**Where tests live:** `SqlEventWalkerJoinsAndTableResolutionTests` — section *JOIN … USING (column list) — T1.1 / named_columns_join scenarios* (V0–V12, triple-join W1–W4, extension block before `// Join USING Tests end here`). CROSS/NATURAL + invalid `USING`/`ON` fatals are in the following section of the same class.

**Beyond minimum coverage (product hardening, same tier closure):**

- Unqualified `USING` columns only; qualified names → fatal (`QUALIFIED_COLUMN_IN_JOIN_USING`).
- `filters` entries pair each `USING` column with **both** join operands (alias `table_ref`), including **flat** chained joins (`join={1…N}`): walk the join sequence, find the nearest tuple operand before/after each `USING` bucket (skip `LATERAL` / modifier slots).
- Query-like operands (subquery, VALUES, set-op, pivot, table function, tuple/Jinja) validated via finalized **`def_*`** interfaces; fatals when a column is missing on a resolvable operand (`JOIN_USING_COLUMN_NOT_FOUND`).
- Chained joins: multiple `USING` clauses, `LEFT` / `RIGHT` / `FULL OUTER`, comma + `LATERAL` + `USING`, quad chain (three `USING`), partial multi-column fatal (earlier columns may still land in `filters` before fatal on a later column).

**Caveats / out of dialect (documented; no further T1.1 tests):**

- **`table_reference_list` AST is always flat** (`1=t, 2=using, 3=…`) — not left-deep `join={1={join={…}}, 2=using, 3=…}`. Recurse-on-nested-slot-1 in the walker is defensive only; this dialect does not emit that shape for SELECT `FROM`.
- **Physical base tables** are treated as permissive for `USING` column acceptance (no catalog-backed `JOIN_USING_COLUMN_NOT_FOUND` for `third`/`fourth` alone); validation targets resolvable query/VALUES/set-op interfaces.
- Capture runs at **`exitTable_reference_list`** (SELECT-style `FROM` join chains). **DELETE … USING (**subquery**)** and other DML `USING` shapes are separate grammar paths (covered elsewhere, not part of T1.1).

**Follow-up (not in scope for T1.1 — promote to a new tier if needed):** predicand/function expressions in `USING (...)` if grammar allows; derived vs regular column operands; wildcard `USING` shapes; schema-checked `USING` on physical tables.

### T1.2 `exitUnpivot_null_policy` — `unpivot_null_policy` (JaCoCo Aug 2026: 11/11 lines, 100%, missed=0) — **COMPLETE**

- [x] Add FROM clause with **`UNPIVOT INCLUDE NULLS`** or **`EXCLUDE NULLS`** before the `(` payload.
- **Verify:** `exitUnpivot_null_policy` covered.

**Status (Spring 2026):** Tier 1.2 is **closed**. Walker emits **`nulls_policy=include|exclude`** on the `unpivot={}` map (no rule-type leakage on the AST root).

**Where tests live:** `SqlEventWalkerPivotUnpivotTests` — `unpivotIncludeNullsNullPolicyAstShapeTest`, `unpivotExcludeNullsNullPolicyAstShapeTest` (end of class).

### T1.3 `exitPivot_in_any` — `pivot_in_any` (JaCoCo Aug 2026: 13/13 lines, 100%, missed=0) — **COMPLETE**

- [x] Add **`PIVOT … IN (ANY)`** or **`IN (ANY ORDER BY …)`** on a pivot source.
- **Verify:** `exitPivot_in_any` covered.

**Status (Spring 2026):** Closed via `pivotInAnyAstShapeTest`, `pivotInAnyOrderBy*`, and lineage migration test in `SqlEventWalkerPivotUnpivotTests`.

### T1.4 `exitPivot_in_subquery` — `pivot_in_subquery` (JaCoCo Aug 2026: 3/3 lines, 100%, missed=0) — **COMPLETE**

- [x] Add **`PIVOT … IN (SELECT …)`** (subquery as pivot IN list).
- **Verify:** `exitPivot_in_subquery` covered.

**Status (Spring 2026):** `pivotInSubqueryAstShapeTest` in `SqlEventWalkerPivotUnpivotTests` (goldens via `PivotWalkerTierGoldenCaptureOnce`).

### T1.5 `exitTable_argument_boolean` — `table_argument_boolean` (JaCoCo Aug 2026: 2/2 lines, 100%, missed=0) — **COMPLETE**

- [x] Exemplar SQL: `INFER_SCHEMA(… IGNORE_CASE => FALSE)`.
- **Verify:** `exitTable_argument_boolean` covered; AST `ignore_case=FALSE` (scalar, not `{literal=…}`).

**Status (Spring 2026):** Grammar prefers `table_argument_boolean` over `additive_expression` for kwargs; `inferSchemaIgnoreCaseFalseTableArgumentBooleanT1_5Test`.

### T1.9 `exitOther_trim_operands` — **REMOVED** (no active parse path)

- Comma `TRIM(a, ' ')` from the `sql()` endpoint parses as **`routine_invocation`**, not `trim_function` / `#other_trim_operands`.
- Removed dead grammar alt and `exitOther_trim_operands` (Aug 2026).

### T1.6 `exitInfer_schema_files_argument` — `infer_schema_files_argument` (JaCoCo Aug 2026: 13/13 lines, 100%, missed=0) — **COMPLETE**

- [x] Add **`INFER_SCHEMA(… FILES => (…))`** (or equivalent `FILES` argument list shape).
- **Verify:** `exitInfer_schema_files_argument` covered.

**Status (Spring 2026):** `inferSchemaFilesArgumentT1_6Test` in `SqlEventWalkerTableFunctionTests`.

### T1.7 `exitValidate_table_function` — `validate_table_function` (JaCoCo Aug 2026: 13/13 lines, 100%, missed=0) — **COMPLETE**

- [x] Add **`VALIDATE(…)`** table function in **FROM**.
- **Verify:** `exitValidate_table_function` covered.

**Status (Spring 2026):** `validateTableFunctionT1_7Test` in `SqlEventWalkerTableFunctionTests`.

### T1.8 `exitRow_value_predicand_list` — `row_value_predicand_list` (JaCoCo Aug 2026: 8/9 lines, 89%, missed=1) — **COMPLETE** (Tier 3 polish: 1 missed line)

- [x] Add **`GROUP BY (a, b)`** or grouping set using **parenthesized predicand list**.
- **Verify:** `exitRow_value_predicand_list` covered.

**Status (Spring 2026):** `groupByRowValuePredicandListT1_8Test` — `groupby={set={1=…, 2=…}}`; `grouped_by` populated.

### T1.10 `exitOrdinary_grouping_set_list` — `ordinary_grouping_set_list` (JaCoCo Aug 2026: 6/6 lines, 100%, missed=0) — **COMPLETE**

- [x] Add **`ROLLUP`/`CUBE`** (or grouping sets) with **multiple** entries in `ordinary_grouping_set_list`.
- **Verify:** `exitOrdinary_grouping_set_list` covered.

**Status (Spring 2026):** `rollupOrdinaryGroupingSetListT1_10Test` plus **`SqlEventWalkerGroupByGroupingSetsTests`** (22 cases); grammar **`GROUPING SETS`**, lexer **`SETS`** before `Identifier`; `grouping_body` disambiguates `GROUP BY` vs positional `select_list`.

**Note:** JaCoCo target is `exitOrdinary_grouping_set_list`; most behavioral coverage is shared with **T1.8** (parenthesized `row_value_predicand_list` inside `ordinary_grouping_set`). The walker does not emit a literal `ordinary_grouping_set_list` node—operands fold into `rollup` / `cube` / `grouping_sets` / `set` under `groupby`. Snowflake **`GROUP BY ALL`** and Postgres **`GROUP BY DISTINCT`** use `groupby={option=…}` (optional `set={…}` for explicit DISTINCT columns).

### T1.11 `exitEmpty_grouping_set` — `empty_grouping_set` (JaCoCo Aug 2026: 5/5 lines, 100%, missed=0) — **COMPLETE**

- [x] Add **`GROUP BY ()`** (empty grouping element via `empty_grouping_set`).
- **Verify:** `exitEmpty_grouping_set` covered; AST `groupby={set={}}`; `grouped_by` empty.

**Status (Spring 2026):** `emptyGroupingSetT1_11Test` in `SqlEventWalkerGroupByGroupingSetsTests`. Grammar path is top-level `()` (`grouping_element` → `empty_grouping_set`), not `GROUPING SETS (())` (inner `()` requires a non-empty `row_value_predicand_list` today).

---

## Tier 2 — Partial coverage (substantial gaps)

Method runs in some test but JaCoCo still reports **≥3 missed lines** or **&lt;85%** line coverage on that method. **19** methods meet this bar today (see sorted [gaps table](sql_walker_exit_method_gaps.md#tier-2--substantial-gaps-jacoco-aug-2026)). Checklist items **T2.12, T2.14, T2.21–T2.23, T2.25** now fall under **Tier 3** thresholds but keep their IDs for traceability.

### T2.1 `exitTable_argument_literal` — `table_argument_literal` (JaCoCo Aug 2026: 14/20 lines, 70%, missed=6)

- [x] Exemplars: `inferSchemaLiteralsAndNumericT2_1Test`, `flattenModeArrayLiteralT2_1Test`, `flattenPathStringLiteralT2_1Test` in `SqlEventWalkerTableFunctionTests` (regen: `TableFunctionArgumentLiteralGoldenCaptureOnce`). Numeric/string kwargs scalarize (`max_file_count=10`, `path='a'`).
- [ ] Close remaining JaCoCo branches if any (defensive early returns).

### T2.2 `exitInsert_target_table_primary` — `insert_target_table_primary` (JaCoCo Aug 2026: 50/51 lines, 98%, missed=1) — **COMPLETE** (Tier 3 polish: 1 missed line)

- [x] Exemplars: `insertTargetSubstitutionVariableT2_2Test`, `insertTargetJinjaRefT2_2Test`, `insertTargetRelationAliasT2_2Test`, `insertTargetNoColumnListT2_2Test` in `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` (regen: `InsertTargetTablePrimaryGoldenCaptureOnce`).

### T2.3 `exitInfer_schema_argument` — `infer_schema_argument` (JaCoCo Aug 2026: 18/23 lines, 78%, missed=5)

- [ ] Add `INFER_SCHEMA` kwargs: **LOCATION**, **FILE_FORMAT**, **IGNORE_CASE**, **MAX_FILE_COUNT**, etc., beyond current smokes.

### T2.4 `exitJinja_arg` — `jinja_arg` (JaCoCo Aug 2026: 15/24 lines, 62%, missed=9)

- [ ] Extend Jinja call with arg forms not yet walked (positional vs named / typed args).

### T2.5 `exitJinja_function_call` — `jinja_function_call` (JaCoCo Aug 2026: 25/34 lines, 74%, missed=9)

- [ ] Additional `{{ … }}` function patterns in SELECT/FROM (nested calls, multiple args).

### T2.6 `exitSubquery` — `subquery` (JaCoCo Aug 2026: 2/2 lines, 100%, missed=0) — COMPLETE

- [x] Removed unreachable LOOKUP/`handleListItem` branch (parent is never `nonparenthesized_value_expression_primary`; scalar wrapping is `exitPredicand_subquery` / select-item paths).
- [x] Exemplars: `SqlEventWalkerSubqueryExitT2_6Tests` (8 scenarios); regen `SubqueryExitT2_6GoldenCaptureOnce`.

### T2.7 `exitStatic_data_type_name` — `static_data_type_name` (JaCoCo Aug 2026: 20/27 lines, 74%, missed=7)

- [ ] CAST/DDL/type parse using static type names not in current goldens.

### T2.8 `exitRelational_modifier_in_item` — `relational_modifier_in_item` (JaCoCo Aug 2026: 24/30 lines, 80%, missed=6) — **EXEMPLAR** (JaCoCo gap remains)

- [x] UNPIVOT **IN** list items: alias without `AS`, qualified column + string label, single-item list.
- **Verify:** `exitRelational_modifier_in_item` fully line-covered (`exitRelational_modifier_alias` exercised via same tests).

**Note:** This exit is **UNPIVOT IN (...)** only; PIVOT `IN` uses `pivot_in_*` rules (T1.3/T1.4/T3.25).

**Status (Spring 2026):** `unpivotInListAliasWithoutAsAstShapeTest`, `unpivotInListQualifiedColumnWithStringLabelAstShapeTest`, `unpivotInListSingleItemAstShapeTest` in `SqlEventWalkerPivotUnpivotTests`.

### T2.9 `exitWith_clause` — `with_clause` (JaCoCo Aug 2026: 14/19 lines, 74%, missed=5)

- [ ] WITH variants: **RECURSIVE**, multiple CTEs, nested WITH (see also nested `with_query` under `cte_body`).

### T2.10 `exitUnpivot_clause` — `unpivot_clause` (JaCoCo Aug 2026: 36/39 lines, 92%, missed=3) — **EXEMPLAR** (JaCoCo gap remains)

- [x] UNPIVOT shapes beyond null policy (T1.2): IN-list item aliases and clause assembly.
- **Verify:** `exitUnpivot_clause` fully line-covered.

**Status (Spring 2026):** `unpivotClauseInListItemAliasesAstShapeTest` plus existing UNPIVOT matrix in `SqlEventWalkerPivotUnpivotTests`.

### T2.11 `exitFlatten_argument` — `flatten_argument` (JaCoCo Aug 2026: 15/19 lines, 79%, missed=4)

- [ ] FLATTEN named args / modes not covered in existing FLATTEN tests.

### T2.12 `exitQuantified_comparison_predicate` — `quantified_comparison_predicate` (JaCoCo Aug 2026: 28/30 lines, 93%, missed=2) — *Tier 3 per thresholds*

- [ ] `ANY`/`ALL` quantified comparisons with additional operators or subquery shapes.

### T2.13 `exitEveryRule` — `exitEveryRule` (JaCoCo Aug 2026: 25/31 lines, 81%, missed=6)

- [ ] Scenarios that exercise **generic** `exitEveryRule` paths (dialect registry + rare rules); may be satisfied by Tier 1 grammar additions.

### T2.14 `exitScript` — `script` (JaCoCo Aug 2026: 32/34 lines, 94%, missed=2) — *Tier 3 per thresholds*

- [ ] Multi-statement **script** endpoint: mixed DML/DDL, statement boundaries, symbol isolation.

### T2.15 `exitSql_statement` — `sql_statement` (JaCoCo Aug 2026: 21/24 lines, 88%, missed=3)

- [ ] Statement types not fully closing through `exitSql_statement` (edge wrappers).

### T2.16 `exitDrop_options` — `drop_options` (JaCoCo Aug 2026: 13/16 lines, 81%, missed=3)

- [ ] DROP with **option tails** beyond current DDL goldens.

### T2.17 `exitAlter_options` — `alter_options` (JaCoCo Aug 2026: 13/16 lines, 81%, missed=3)

- [ ] ALTER with **option tails** beyond current DDL goldens.

### T2.18 `exitInsert_preamble` — `insert_preamble` (JaCoCo Aug 2026: 10/12 lines, 83%, missed=2)

- [ ] `INSERT OVERWRITE` / preamble variants if grammar supports.

### T2.19 `exitConflict_target` — `conflict_target` (JaCoCo Aug 2026: 12/15 lines, 80%, missed=3)

- [ ] `ON CONFLICT (cols)` / constraint target forms not in insert upsert tests.

### T2.20 `exitSet_operation_member` — `set_operation_member` (JaCoCo Aug 2026: 22/25 lines, 88%, missed=3)

- [ ] UNION/INTERSECT **member** ordering, parentheses, nested set ops.

### T2.21 `exitGenerator_argument` — `generator_argument` (JaCoCo Aug 2026: 12/13 lines, 92%, missed=1) — *Tier 3 per thresholds*

- [ ] GENERATOR table function argument combinations.

### T2.22 `exitJinja_name` — `jinja_name` (JaCoCo Aug 2026: 12/14 lines, 86%, missed=2) — *Tier 3 per thresholds*

- [ ] Jinja identifiers / dotted names in calls.

### T2.23 `exitOrdinary_grouping_set` — `ordinary_grouping_set` (JaCoCo Aug 2026: 13/14 lines, 93%, missed=1) — *Tier 3 per thresholds*

- [x] Single grouping set unit paths paired with T1.8/T1.10 (`SqlEventWalkerGroupByGroupingSetsTests`, T1.8/T1.10 exemplars).

### T2.24 `exitAssignment_expression_list` — `assignment_expression_list` (JaCoCo Aug 2026: 5/7 lines, 71%, missed=2)

- [ ] UPDATE **multi-column** SET list edge (many assignments, qualified LHS).

### T2.25 `exitRow_value_expression` — `row_value_expression` (JaCoCo Aug 2026: 6/7 lines, 86%, missed=1) — *Tier 3 per thresholds*

- [ ] Row value / tuple expressions in GROUP BY or VALUES contexts.

---

## Tier 3 — Partial coverage (minor gaps)

Method executed; **1–2 missed lines** and **≥85%** line coverage. **53** methods (including checklist **T2.12 / T2.14 / T2.21–T2.23 / T2.25** and unlisted `exitGroupby_clause`, `exitGroupby_distinct_body`). Optional batch after Tier 2 queue.

### T3.1 `exitTruncate_postgres_expression` — `truncate_postgres_expression` (JaCoCo Aug 2026: 19/21 lines, 90%, missed=2)

- [ ] Hit remaining truncate-postgres branch (multi-target already partially tested).

### T3.2 `exitWith_list_item` — `with_list_item` (JaCoCo Aug 2026: 41/43 lines, 95%, missed=2)

- [ ] CTE list item edge (column list on CTE, materialized hint if grammar adds).

### T3.3 `exitConflict_action` — `conflict_action` (JaCoCo Aug 2026: 33/35 lines, 94%, missed=2)

- [ ] `ON CONFLICT DO UPDATE` vs `DO NOTHING` branch not hit.

### T3.4 `exitIntersect_clause` — `intersect_clause` (JaCoCo Aug 2026: 27/29 lines, 93%, missed=2)

- [ ] INTERSECT **ALL/DISTINCT** or parenthesized member if applicable.

### T3.5 `exitUnion_clause` — `union_clause` (JaCoCo Aug 2026: 27/29 lines, 93%, missed=2)

- [ ] UNION **ALL/DISTINCT** variant not hit.

### T3.6 `exitExists_operator` — `exists_operator` (JaCoCo Aug 2026: 13/14 lines, 93%, missed=1)

- [ ] EXISTS subquery operator edge in predicand frame.

### T3.7 `exitMysql_trim_operands` — `mysql_trim_operands` (JaCoCo Aug 2026: 14/16 lines, 88%, missed=2)

- [ ] MySQL-style `TRIM(LEADING … FROM …)` branch.

### T3.8 `exitDdl` — `ddl` (JaCoCo Aug 2026: 9/10 lines, 90%, missed=1)

- [ ] DDL endpoint wrapper branch.

### T3.9 `exitCreate_table_expression` — `create_table_expression` (JaCoCo Aug 2026: 25/26 lines, 96%, missed=1)

- [ ] CREATE TABLE **column list** vs **AS SELECT** uncovered alt.

### T3.10 `exitCreate_role_expression` — `create_role_expression` (JaCoCo Aug 2026: 14/15 lines, 93%, missed=1)

- [ ] CREATE ROLE option tail.

### T3.11 `exitTruncate_snowflake_expression` — `truncate_snowflake_expression` (JaCoCo Aug 2026: 12/13 lines, 92%, missed=1)

- [ ] Remaining snowflake truncate line (dialect warning path may already run).

### T3.12 `exitWith_query` — `with_query` (JaCoCo Aug 2026: 24/25 lines, 96%, missed=1)

- [ ] Top-level WITH not nested in CTE body.

### T3.13 `exitPostgres_insert` — `postgres_insert` (JaCoCo Aug 2026: 25/26 lines, 96%, missed=1)

- [ ] INSERT path through `postgres_insert` wrapper without `ON CONFLICT`.

### T3.14 `exitUpdate_expression` — `update_expression` (JaCoCo Aug 2026: 33/34 lines, 97%, missed=1)

- [ ] UPDATE **RETURNING** or **FROM** clause branch.

### T3.15 `exitOn_conflict_clause` — `on_conflict_clause` (JaCoCo Aug 2026: 19/20 lines, 95%, missed=1)

- [ ] Upsert without conflict target vs with target.

### T3.16 `exitValues_aliases_list` — `values_aliases_list` (JaCoCo Aug 2026: 14/15 lines, 93%, missed=1)

- [ ] VALUES with column alias list.

### T3.17 `exitQuery_specification` — `query_specification` (JaCoCo Aug 2026: 48/49 lines, 98%, missed=1)

- [ ] SELECT clause combo (INTO, hint, or rare clause ordering).

### T3.18 `exitTable_source_primary` — `table_source_primary` (JaCoCo Aug 2026: 29/30 lines, 97%, missed=1)

- [ ] Table source nesting / set-op wrap anchor.

### T3.19 `exitTable_primary` — `table_primary` (JaCoCo Aug 2026: 42/43 lines, 98%, missed=1)

- [ ] DELETE/UPDATE table primary variant.

### T3.20 `exitInsert_source_primary` — `insert_source_primary` (JaCoCo Aug 2026: 26/27 lines, 96%, missed=1)

- [ ] INSERT source: VALUES vs query branch gap.

### T3.21 `exitTuple_primary` — `tuple_primary` (JaCoCo Aug 2026: 25/26 lines, 96%, missed=1)

- [ ] Tuple/jinja table primary in FROM.

### T3.22 `exitDb_object_name` — `db_object_name` (JaCoCo Aug 2026: 25/26 lines, 96%, missed=1)

- [ ] Multi-part object name (db.schema.table) edge.

### T3.23 `exitQualified_join` — `qualified_join` (JaCoCo Aug 2026: 15/16 lines, 94%, missed=1)

- [ ] Qualified join type token not hit.

### T3.24 `exitRelational_modifier_alias` — `relational_modifier_alias` (JaCoCo Aug 2026: 12/12 lines, 100%, missed=0) — **COMPLETE**

- [x] UNPIVOT IN alias forms: identifier with/without `AS`, string literal label (covered by T2.8 exemplars + existing `AS 'JAN'` matrix).

**Status (Spring 2026):** See T2.8 tests and `unpivotClauseInListItemAliasesAstShapeTest`.

### T3.25 `exitPivot_in_literal` — `pivot_in_literal` (JaCoCo Aug 2026: 18/20 lines, 90%, missed=2) — **EXEMPLAR** (JaCoCo gap remains)

- [x] PIVOT IN string literal with optional `AS` prefix label (`exitPivot_in_prefix`).
- **Verify:** `exitPivot_in_literal` / prefix exits covered.

**Status (Spring 2026):** `pivotInLiteralStringWithAsPrefixAstShapeTest` in `SqlEventWalkerPivotUnpivotTests`.

### T3.26 `exitFlatten_table_function` — `flatten_table_function` (JaCoCo Aug 2026: 12/13 lines, 92%, missed=1)

- [ ] FLATTEN table function entry (single missed line).

### T3.27 `exitGenerator_table_function` — `generator_table_function` (JaCoCo Aug 2026: 12/13 lines, 92%, missed=1)

- [ ] GENERATOR table function entry.

### T3.28 `exitInfer_schema_table_function` — `infer_schema_table_function` (JaCoCo Aug 2026: 12/13 lines, 92%, missed=1)

- [ ] INFER_SCHEMA table function entry (beyond T1.6–T2.3).

### T3.29 `exitGeneric_table_function` — `generic_table_function` (JaCoCo Aug 2026: 12/13 lines, 92%, missed=1)

- [ ] Generic table function fallback.

### T3.30 `exitColumn_primary` — `column_primary` (JaCoCo Aug 2026: 39/40 lines, 98%, missed=1)

- [ ] Column primary wrapper branch.

### T3.31 `exitPredicand_primary` — `predicand_primary` (JaCoCo Aug 2026: 26/27 lines, 96%, missed=1)

- [ ] Predicand endpoint primary branch.

### T3.32 `exitGeneral_set_function` — `general_set_function` (JaCoCo Aug 2026: 21/22 lines, 95%, missed=1)

- [ ] Set function without window.

### T3.33 `exitVariable_size_data_type` — `variable_size_data_type` (JaCoCo Aug 2026: 11/12 lines, 92%, missed=1)

- [ ] VARCHAR-length style type in CAST/DDL.

### T3.34 `exitType_length` — `type_length` (JaCoCo Aug 2026: 11/12 lines, 92%, missed=1)

- [ ] Type length clause.

### T3.35 `exitPrecision_scale_data_type` — `precision_scale_data_type` (JaCoCo Aug 2026: 11/12 lines, 92%, missed=1)

- [ ] DECIMAL(p,s) style.

### T3.36 `exitStatic_data_type` — `static_data_type` (JaCoCo Aug 2026: 9/10 lines, 90%, missed=1)

- [ ] Static type wrapper.

### T3.37 `exitSelect_direction` — `select_direction` (JaCoCo Aug 2026: 11/12 lines, 92%, missed=1)

- [ ] FETCH FIRST / OFFSET direction if grammar uses.

### T3.38 `exitNull_handling` — `null_handling` (JaCoCo Aug 2026: 11/12 lines, 92%, missed=1)

- [ ] NULLS FIRST/LAST in ORDER BY.

### T3.39 `exitJinja_arg_list` — `jinja_arg_list` (JaCoCo Aug 2026: 18/19 lines, 95%, missed=1)

- [ ] Multi-arg Jinja list.

### T3.40 `exitJinja_variable_access` — `jinja_variable_access` (JaCoCo Aug 2026: 18/19 lines, 95%, missed=1)

- [ ] Jinja variable reference form.

### T3.41 `exitIs_null_clause` — `is_null_clause` (JaCoCo Aug 2026: 14/15 lines, 93%, missed=1)

- [ ] IS NULL / IS NOT NULL branch.

### T3.42 `exitComparison_predicate` — `comparison_predicate` (JaCoCo Aug 2026: 17/18 lines, 94%, missed=1)

- [ ] Comparison predicate edge operator.

### T3.43 `exitFactor` — `factor` (JaCoCo Aug 2026: 18/19 lines, 95%, missed=1)

- [ ] Unary +/- on factor.

### T3.44 `exitValue_expression` — `value_expression` (JaCoCo Aug 2026: 48/49 lines, 98%, missed=1)

- [ ] Value expression when parent is `sql_argument_list` edge.

### T3.45 `exitVariable_identifier` — `variable_identifier` (JaCoCo Aug 2026: 7/8 lines, 88%, missed=1)

- [ ] Substitution/variable identifier in expression.

---

## Execution order (recommended)

1. **Tier 1** — complete (T1.1–T1.8, T1.10–T1.11; T1.9 removed).
2. **Tier 2** — **19** substantial gaps; priority **T2.1 → T2.2 → T2.4–T2.8 → T2.3/T2.9–T2.11 → T2.13–T2.20** (see [gaps table](sql_walker_exit_method_gaps.md#tier-2--substantial-gaps-jacoco-aug-2026)).
3. **Tier 3** — **53** minor gaps (optional polish).

## Refresh inventory

After a batch of tests, re-run the JaCoCo comparison and update tier lists in [sql_walker_exit_method_gaps.md](sql_walker_exit_method_gaps.md) or regenerate this workplan’s checkboxes.

## Related

- [sql_walker_exit_method_gaps.md](sql_walker_exit_method_gaps.md) — short gap summary
- [sql_walker_astwalkers_gap_report.md](sql_walker_astwalkers_gap_report.md) — walker + helper methods (broader than `exit*`)
- [helper-dead-code-hygiene-workplan.md](../helper-dead-code-hygiene-workplan.md) — JaCoCo vs caller-audit policy
