# SqlParseEventWalker `exit*` coverage — workplan

**Status:** 🚧 Not started (inventory Aug 2026)  
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
| Listener methods | `exit*` only (277 methods; ANTLR-generated names ↔ grammar rules) |

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

### T1.1 `exitNamed_columns_join` — `named_columns_join` — **COMPLETE**

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

### T1.2 `exitUnpivot_null_policy` — `unpivot_null_policy` — **COMPLETE**

- [x] Add FROM clause with **`UNPIVOT INCLUDE NULLS`** or **`EXCLUDE NULLS`** before the `(` payload.
- **Verify:** `exitUnpivot_null_policy` covered.

**Status (Spring 2026):** Tier 1.2 is **closed**. Walker emits **`nulls_policy=include|exclude`** on the `unpivot={}` map (no rule-type leakage on the AST root).

**Where tests live:** `SqlEventWalkerPivotUnpivotTests` — `unpivotIncludeNullsNullPolicyAstShapeTest`, `unpivotExcludeNullsNullPolicyAstShapeTest` (end of class).

### T1.3 `exitPivot_in_any` — `pivot_in_any`

- [ ] Add **`PIVOT … IN (ANY)`** or **`IN (ANY ORDER BY …)`** on a pivot source.
- **Verify:** `exitPivot_in_any` covered.

### T1.4 `exitPivot_in_subquery` — `pivot_in_subquery`

- [ ] Add **`PIVOT … IN (SELECT …)`** (subquery as pivot IN list).
- **Verify:** `exitPivot_in_subquery` covered.

### T1.5 `exitTable_argument_boolean` — `table_argument_boolean`

- [ ] Add table-function call with a **boolean** named argument (e.g. `=> TRUE` / `=> FALSE`) where grammar uses `table_argument_boolean`.
- **Verify:** `exitTable_argument_boolean` covered.

### T1.6 `exitInfer_schema_files_argument` — `infer_schema_files_argument`

- [ ] Add **`INFER_SCHEMA(… FILES => (…))`** (or equivalent `FILES` argument list shape).
- **Verify:** `exitInfer_schema_files_argument` covered.

### T1.7 `exitValidate_table_function` — `validate_table_function`

- [ ] Add **`VALIDATE(…)`** table function in **FROM**.
- **Verify:** `exitValidate_table_function` covered.

### T1.8 `exitRow_value_predicand_list` — `row_value_predicand_list`

- [ ] Add **`GROUP BY (a, b)`** or grouping set using **parenthesized predicand list** (`ROLLUP`/`CUBE` / ordinary grouping set).
- **Verify:** `exitRow_value_predicand_list` covered.

### T1.9 `exitOther_trim_operands` — `trim_operands` (#other_trim_operands)

- [ ] Add **`TRIM(source, trim_char)`** comma form (non-MySQL `# other_trim_operands` alternative).
- **Verify:** `exitOther_trim_operands` covered.

### T1.10 `exitOrdinary_grouping_set_list` — `ordinary_grouping_set_list`

- [ ] Add **`ROLLUP`/`CUBE`** (or grouping sets) with **multiple** entries in `ordinary_grouping_set_list`.
- **Verify:** `exitOrdinary_grouping_set_list` covered.

---

## Tier 2 — Partial coverage (substantial gaps)

Method runs in some test but JaCoCo still reports **≥3 missed lines** or **&lt;85%** line coverage on that method.

### T2.1 `exitTable_argument_literal` — `table_argument_literal` (~32% lines)

- [ ] Exercise table-function **literal** arguments (string/number) not yet hit; extend `SqlEventWalkerTableFunctionTests` or equivalent.

### T2.2 `exitInsert_target_table_primary` — `insert_target_table_primary` (~80%)

- [ ] Cover INSERT target shapes not using table-function primary (partition list, alias edge, or substitution on target).

### T2.3 `exitInfer_schema_argument` — `infer_schema_argument` (~57%)

- [ ] Add `INFER_SCHEMA` kwargs: **LOCATION**, **FILE_FORMAT**, **IGNORE_CASE**, **MAX_FILE_COUNT**, etc., beyond current smokes.

### T2.4 `exitJinja_arg` — `jinja_arg` (~63%)

- [ ] Extend Jinja call with arg forms not yet walked (positional vs named / typed args).

### T2.5 `exitJinja_function_call` — `jinja_function_call` (~73%)

- [ ] Additional `{{ … }}` function patterns in SELECT/FROM (nested calls, multiple args).

### T2.6 `exitSubquery` — `subquery` (~38%)

- [ ] FROM/subquery contexts that bypass current subquery tests (correlated, parenthesized, DML subselect).

### T2.7 `exitStatic_data_type_name` — `static_data_type_name` (~74%)

- [ ] CAST/DDL/type parse using static type names not in current goldens.

### T2.8 `exitRelational_modifier_in_item` — `relational_modifier_in_item` (~79%)

- [ ] PIVOT/UNPIVOT **IN** list items: alias, qualified column, extra variants.

### T2.9 `exitWith_clause` — `with_clause` (~74%)

- [ ] WITH variants: **RECURSIVE**, multiple CTEs, nested WITH (see also nested `with_query` under `cte_body`).

### T2.10 `exitUnpivot_clause` — `unpivot_clause` (~87% lines but 5 missed)

- [ ] UNPIVOT shapes beyond current matrix (null policy covered under T1.2).

### T2.11 `exitFlatten_argument` — `flatten_argument` (~74%)

- [ ] FLATTEN named args / modes not covered in existing FLATTEN tests.

### T2.12 `exitQuantified_comparison_predicate` — `quantified_comparison_predicate` (~84%)

- [ ] `ANY`/`ALL` quantified comparisons with additional operators or subquery shapes.

### T2.13 `exitEveryRule` — `exitEveryRule` (~89%)

- [ ] Scenarios that exercise **generic** `exitEveryRule` paths (dialect registry + rare rules); may be satisfied by Tier 1 grammar additions.

### T2.14 `exitScript` — `script` (~91%)

- [ ] Multi-statement **script** endpoint: mixed DML/DDL, statement boundaries, symbol isolation.

### T2.15 `exitSql_statement` — `sql_statement` (~85%)

- [ ] Statement types not fully closing through `exitSql_statement` (edge wrappers).

### T2.16 `exitDrop_options` — `drop_options` (~81%)

- [ ] DROP with **option tails** beyond current DDL goldens.

### T2.17 `exitAlter_options` — `alter_options` (~81%)

- [ ] ALTER with **option tails** beyond current DDL goldens.

### T2.18 `exitInsert_preamble` — `insert_preamble` (~75%)

- [ ] `INSERT OVERWRITE` / preamble variants if grammar supports.

### T2.19 `exitConflict_target` — `conflict_target` (~80%)

- [ ] `ON CONFLICT (cols)` / constraint target forms not in insert upsert tests.

### T2.20 `exitSet_operation_member` — `set_operation_member` (~88%)

- [ ] UNION/INTERSECT **member** ordering, parentheses, nested set ops.

### T2.21 `exitGenerator_argument` — `generator_argument` (~85%)

- [ ] GENERATOR table function argument combinations.

### T2.22 `exitJinja_name` — `jinja_name` (~85%)

- [ ] Jinja identifiers / dotted names in calls.

### T2.23 `exitOrdinary_grouping_set` — `ordinary_grouping_set` (~71%)

- [ ] Single grouping set unit paths paired with T1.8/T1.10.

### T2.24 `exitAssignment_expression_list` — `assignment_expression_list` (~83%)

- [ ] UPDATE **multi-column** SET list edge (many assignments, qualified LHS).

### T2.25 `exitRow_value_expression` — `row_value_expression` (~83%)

- [ ] Row value / tuple expressions in GROUP BY or VALUES contexts.

---

## Tier 3 — Partial coverage (minor gaps)

Method executed; **1–2 missed lines** and **≥85%** line coverage. Optional batch after Tier 1–2.

### T3.1 `exitTruncate_postgres_expression` — `truncate_postgres_expression`

- [ ] Hit remaining truncate-postgres branch (multi-target already partially tested).

### T3.2 `exitWith_list_item` — `with_list_item`

- [ ] CTE list item edge (column list on CTE, materialized hint if grammar adds).

### T3.3 `exitConflict_action` — `conflict_action`

- [ ] `ON CONFLICT DO UPDATE` vs `DO NOTHING` branch not hit.

### T3.4 `exitIntersect_clause` — `intersect_clause`

- [ ] INTERSECT **ALL/DISTINCT** or parenthesized member if applicable.

### T3.5 `exitUnion_clause` — `union_clause`

- [ ] UNION **ALL/DISTINCT** variant not hit.

### T3.6 `exitExists_operator` — `exists_operator`

- [ ] EXISTS subquery operator edge in predicand frame.

### T3.7 `exitMysql_trim_operands` — `mysql_trim_operands` (#mysql_trim_operands)

- [ ] MySQL-style `TRIM(LEADING … FROM …)` branch.

### T3.8 `exitDdl` — `ddl`

- [ ] DDL endpoint wrapper branch.

### T3.9 `exitCreate_table_expression` — `create_table_expression`

- [ ] CREATE TABLE **column list** vs **AS SELECT** uncovered alt.

### T3.10 `exitCreate_role_expression` — `create_role_expression`

- [ ] CREATE ROLE option tail.

### T3.11 `exitTruncate_snowflake_expression` — `truncate_snowflake_expression`

- [ ] Remaining snowflake truncate line (dialect warning path may already run).

### T3.12 `exitWith_query` — `with_query`

- [ ] Top-level WITH not nested in CTE body.

### T3.13 `exitPostgres_insert` — `postgres_insert`

- [ ] INSERT path through `postgres_insert` wrapper without `ON CONFLICT`.

### T3.14 `exitUpdate_expression` — `update_expression`

- [ ] UPDATE **RETURNING** or **FROM** clause branch.

### T3.15 `exitOn_conflict_clause` — `on_conflict_clause`

- [ ] Upsert without conflict target vs with target.

### T3.16 `exitValues_aliases_list` — `values_aliases_list`

- [ ] VALUES with column alias list.

### T3.17 `exitQuery_specification` — `query_specification`

- [ ] SELECT clause combo (INTO, hint, or rare clause ordering).

### T3.18 `exitTable_source_primary` — `table_source_primary`

- [ ] Table source nesting / set-op wrap anchor.

### T3.19 `exitTable_primary` — `table_primary`

- [ ] DELETE/UPDATE table primary variant.

### T3.20 `exitInsert_source_primary` — `insert_source_primary`

- [ ] INSERT source: VALUES vs query branch gap.

### T3.21 `exitTuple_primary` — `tuple_primary`

- [ ] Tuple/jinja table primary in FROM.

### T3.22 `exitDb_object_name` — `db_object_name`

- [ ] Multi-part object name (db.schema.table) edge.

### T3.23 `exitQualified_join` — `qualified_join`

- [ ] Qualified join type token not hit.

### T3.24 `exitRelational_modifier_alias` — `relational_modifier_alias`

- [ ] PIVOT/UNPIVOT relation `AS` alias.

### T3.25 `exitPivot_in_literal` — `pivot_in_literal`

- [ ] PIVOT IN literal list variant.

### T3.26 `exitFlatten_table_function` — `flatten_table_function`

- [ ] FLATTEN table function entry (single missed line).

### T3.27 `exitGenerator_table_function` — `generator_table_function`

- [ ] GENERATOR table function entry.

### T3.28 `exitInfer_schema_table_function` — `infer_schema_table_function`

- [ ] INFER_SCHEMA table function entry (beyond T1.6–T2.3).

### T3.29 `exitGeneric_table_function` — `generic_table_function`

- [ ] Generic table function fallback.

### T3.30 `exitColumn_primary` — `column_primary`

- [ ] Column primary wrapper branch.

### T3.31 `exitPredicand_primary` — `predicand_primary`

- [ ] Predicand endpoint primary branch.

### T3.32 `exitGeneral_set_function` — `general_set_function`

- [ ] Set function without window.

### T3.33 `exitVariable_size_data_type` — `variable_size_data_type`

- [ ] VARCHAR-length style type in CAST/DDL.

### T3.34 `exitType_length` — `type_length`

- [ ] Type length clause.

### T3.35 `exitPrecision_scale_data_type` — `precision_scale_data_type`

- [ ] DECIMAL(p,s) style.

### T3.36 `exitStatic_data_type` — `static_data_type`

- [ ] Static type wrapper.

### T3.37 `exitSelect_direction` — `select_direction`

- [ ] FETCH FIRST / OFFSET direction if grammar uses.

### T3.38 `exitNull_handling` — `null_handling`

- [ ] NULLS FIRST/LAST in ORDER BY.

### T3.39 `exitJinja_arg_list` — `jinja_arg_list`

- [ ] Multi-arg Jinja list.

### T3.40 `exitJinja_variable_access` — `jinja_variable_access`

- [ ] Jinja variable reference form.

### T3.41 `exitIs_null_clause` — `is_null_clause`

- [ ] IS NULL / IS NOT NULL branch.

### T3.42 `exitComparison_predicate` — `comparison_predicate`

- [ ] Comparison predicate edge operator.

### T3.43 `exitFactor` — `factor`

- [ ] Unary +/- on factor.

### T3.44 `exitValue_expression` — `value_expression`

- [ ] Value expression when parent is `sql_argument_list` edge.

### T3.45 `exitVariable_identifier` — `variable_identifier`

- [ ] Substitution/variable identifier in expression.

---

## Execution order (recommended)

1. **Tier 1** (10 items) — largest holes; unblocks dialect and feature families.
2. **Tier 2** table functions + pivot/unpivot + Jinja (T2.1–T2.12, T2.21–T2.22) — aligns with generator and Snowflake surface.
3. **Tier 2** DML/DDL/script (remaining T2.x).
4. **Tier 3** — as time permits or when touching nearby tests.

## Refresh inventory

After a batch of tests, re-run the JaCoCo comparison and update tier lists in [sql_walker_exit_method_gaps.md](sql_walker_exit_method_gaps.md) or regenerate this workplan’s checkboxes.

## Related

- [sql_walker_exit_method_gaps.md](sql_walker_exit_method_gaps.md) — short gap summary
- [sql_walker_astwalkers_gap_report.md](sql_walker_astwalkers_gap_report.md) — walker + helper methods (broader than `exit*`)
- [helper-dead-code-hygiene-workplan.md](../helper-dead-code-hygiene-workplan.md) — JaCoCo vs caller-audit policy
