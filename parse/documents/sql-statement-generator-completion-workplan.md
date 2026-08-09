# SQL statement generator completion — independent work plan

**Status:** ⏸️ Milestone delivered; remaining work not started (spun off from consolidation Phase **13.6**, Aug 2026)  
**Origin:** `symbol-table-resolution-consolidation-worklist.md` §13.6  
**Primary code:** `generators/SQLStatementGenerator.java`, `generators/AbstractSQLASTGenerator.java`  
**Primary grammar:** `SQLSelectParser.g4` (~327 parser rules)  
**Primary walker guide:** `SqlParseEventWalker` `exit*` handlers (AST shape source of truth)  
**Primary tests:** `generators/SQLStatementGeneratorTest` (~51 milestone round-trips)  
**Related:** Structured DDL options → [ddl-structured-options-parsing-workplan.md](ddl-structured-options-parsing-workplan.md)

---

## Why this is its own plan

Consolidation milestone is done (worklist closed Aug 2026). Full grammar-surface generation is a multi-month track on this independent plan.

---

## Architecture snapshot

- Walker builds a nested **mumble-key AST** (`select`, `from`, `join`, `pivot`, `column`, `calc`, …). Generator walks that AST and emits SQL text.
- `AbstractSQLASTGenerator.generateStatement(mumbleKey, node, sql)` dispatches on mumble keys and `SQLParserEndPoints` roots.
- `SQLStatementGenerator` overrides selected `on*` methods and adds statement-shaped `emit*` helpers.
- Unhandled shapes fall through `appendNode` / `handleEndPoint` (risk: `Unexpected node` console noise / incomplete SQL).

**Dispatch policy (decide in Phase 0):** Prefer **one emit path per mumble AST shape produced by a walker `exit*`**, annotated with the grammar rule(s) that produce that shape. Do **not** require a Java method per ANTLR rule if several rules collapse to one AST key — but every grammar alternative that yields a distinct AST shape must have an explicit emit step below.

**Round-trip contract (decide in Phase 0):** parse → walker AST → generate → (re-parse AST equality **or** normalized SQL equality). Pick one; use it for every phase gate.

**Non-goals:** pretty-print / comment preservation / exact whitespace / dialect pretty forms beyond what the AST encodes.

**Grammar / walker hygiene (Aug 2026, `Spring-2026-Extensions`):** Quantified comparisons (`= ALL/ANY/SOME (subquery)`) are wired on `predicate` with walker `exitQuantified_comparison_predicate` / `exitQuantifier`. The `literal_value` fragment endpoint is registered as `SQLPARSER_LITERAL_TREE_KEY` (`LITERAL`) with `exitLiteral_value`. Removed as dead grammar: `query_primary` (+ `exitQuery_primary`), `unique_predicate` (not Snowflake). Set-op and related parents use `subquery` \| `query_specification` \| `variable_identifier` directly.

---

## Milestone status (high level)

| Family | Status |
|--------|--------|
| INSERT / UPDATE / DELETE / TRUNCATE | ✅ Milestone |
| SELECT core + WITH / VALUES / SCRIPT | ✅ / 🟡 |
| Opaque DDL CREATE/ALTER/DROP | ✅ Milestone |
| PIVOT / UNPIVOT exercised shapes | ✅ Milestone |
| Set ops INTERSECT/EXCEPT depth | 🟡 |
| Table functions matrix | 🟡 |
| CASE / IN / LIKE ANY / windows / Jinja map | 🔴 |
| Endpoint trees (predicand, condition, …) | 🔴 stubs |

---

## How to use this roadmap

1. Work **bottom-up where possible**: leaves (Phase 1) before predicates (Phase 3) before FROM (Phase 5) before statements (Phases 8–11). Milestone already inverted that order for smoke coverage — when extending, prefer filling lower phases before adding exotic statement variants.
2. Each substep lists: **grammar rules**, **walker exits to mirror**, **AST keys to emit**, **actions**, **verify**.
3. Status legend in checkboxes: leave unchecked until round-trip exists; mark milestone-covered items with notes.
4. After Phase 0, maintain a living appendix table: `grammar rule → walker exit → mumble key → emit method → test → status`.

---

# Grammar-aligned implementation phases

## Phase 0 — Inventory, contract, milestone gate

**Goal:** Make expansion safe and measurable.

### 0.1 Contract

1. Freeze round-trip success metric (AST reparse vs normalized SQL).
2. Document non-goals (formatting/comments).
3. Decide mumble-key vs per-grammar-rule method policy (see Architecture).
4. Add `-Psql-generator-gate` (or suite) locking current `roundTrip*` tests.

### 0.2 Inventory (run parked prompt)

1. List ANTLR `RULE_*` constants from generated `SQLSelectParserParser`.
2. Map each `SQLStatementGenerator` / abstract `on*` / `emit*` to grammar rule set.
3. Diff against the 327 parser rules; mark **complete / partial / missing**.
4. Commit the progress table as an appendix in this doc (or sibling CSV).

### 0.3 Stabilize

1. Eliminate `Unexpected node` on all milestone round-trips.
2. No golden/diagnostic churn in walker tests when only touching generator.

**Walker guide:** n/a (process phase).

---

## Phase 1 — Lexical / identifier / literal leaves

**Goal:** Every leaf AST node the walker emits can be printed.

### 1.1 Identifiers

| Grammar | Walker exits | AST / actions |
|---------|--------------|---------------|
| `identifier`, `simple_identifier`, `logical_identifier`, `alias_identifier` | `exitIdentifier`, `exitAlias_identifier` | Emit quoted vs bare spelling exactly as AST stored |
| `simple_numeric_identifier`, `snowflake_quoted_numeric_identifier` | (via identifier path) | Preserve numeric/quoted forms |
| `snowflake_dollar_function_identifier` | (function name path) | Emit `$…` names |

**Actions:**

1. Capture AST samples for bare, `"Quoted"`, and numeric identifiers from walker tests.
2. Ensure `onName` / `onAlias` / column name emission never lowercases or strips quotes incorrectly.
3. Round-trip: `SELECT "Weird Col" AS x FROM t`.

### 1.2 Substitution variables

| Grammar | Walker | Actions |
|---------|--------|---------|
| `variable_identifier`, `simple_variable_identifier`, `extended_variable_identifier` | `exitVariable_identifier`, `exitSimple_variable_identifier`, `exitExtended_variable_identifier` | Emit `<name>` / extended forms from `substitution={name, type}` |
| Endpoint uses | `exitColumn_value`, `exitPredicand_value`, … | Same emitter; type is metadata only |

**Actions:**

1. Implement optional **external substitution map** API: `name → replacement SQL`.
2. Default: emit original variable spelling when map miss.
3. Round-trip identity (empty map) + injection (populated map) for types: predicand, condition, column, tuple, in_list, join_extension, query.

### 1.3 Jinja

| Grammar | Walker | Actions |
|---------|--------|---------|
| `jinja_identifier`, `jinja_function_call`, `jinja_variable_access`, `jinja_arg_list`, `jinja_arg`, `jinja_name` | matching `exitJinja_*` | Reconstruct `{{ … }}` / function-call Jinja from AST parts |

**Actions:**

1. Inventory Jinja AST shapes from walker / endpoint tests.
2. Emit function-call vs variable-access forms.
3. Round-trip one of each; document unsupported Jinja surface if any.

### 1.4 Literals & PUML constants

| Grammar | Walker | Actions |
|---------|--------|---------|
| `general_literal`, `character_literal`, `unsigned_literal`, `signed_numeric_literal`, `real_number_def`, `exponent` | `exitGeneral_literal`, `exitUnsigned_literal`, `exitReal_number*`, `exitExponent` | Emit numbers/strings with original lexical form |
| `datetime_literal`, `date_literal`, `time_literal`, `timestamp_literal`, `boolean_literal`, `null_literal` | `exitDatetime_literal`, `exitNull_literal`, … | Emit typed literals / NULL |
| `puml_constant_identifier` | `exitPuml_constant_identifier` | Emit `#CONSTANT` tokens |

### 1.5 Data types (for CAST / DDL)

| Grammar | Walker | Actions |
|---------|--------|---------|
| `data_type`, `variable_size_data_type`, `precision_scale_data_type`, `static_data_type`, length/precision params | `exitData_type`, `exitVariable_*`, `exitPrecision_*`, `exitStatic_*` | Emit `VARCHAR(n)`, `NUMBER(p,s)`, static type names |

**Verify:** CAST round-trips + CREATE column blob paths still green.

---

## Phase 2 — Columns, value expressions, and scalar functions

**Goal:** Anything that can appear as a select-item / assignment RHS / function arg (except full CASE/window detail covered in Phase 2.5–2.6).

### 2.1 Column references

| Grammar | Walker | Actions |
|---------|--------|---------|
| `column_reference`, `column_reference_list`, `column_primary` | `exitColumn_reference`, `exitColumn_reference_list`, `exitColumn_primary` | Emit `table_ref.column` / bare column / `*` / qualified `*` from AST `column={name, table_ref}` |

**Substeps:**

1. Unqualified column.
2. Alias-qualified column (`t.col`).
3. Substitution-as-column (`t.<col>`).
4. Wildcard / `select_all_columns` / `wildcard_reference` (`*`, `t.*`).

### 2.2 Arithmetic & common value expression

| Grammar | Walker | Actions |
|---------|--------|---------|
| `value_expression`, `common_value_expression`, `additive_expression`, `multiplicative_expression`, `factor`, `numeric_primary`, `sign` | `exitValue_expression`, `exitAdditive_expression`, `exitMultiplicative_expression`, `exitFactor`, … | Emit `calc={left, operator, right}` trees; unary sign |

**Substeps:**

1. Binary `+ - * / %` chains with correct associativity/parentheses (`parentheses` node).
2. Unary minus factors.
3. Nested calcs in SELECT and WHERE.

### 2.3 String / concatenate / trim / position

| Grammar | Walker | Actions |
|---------|--------|---------|
| `string_value_expression`, `character_primary` | `exitString_value_expression`, `exitCharacter_primary` | Route to concat / trim / position / primary |
| concatenate (AST `concatenate`) | (built in value expression exits) | Emit `\|\|` lists with paren policy |
| `trim_function`, trim operands | `exitTrim_function`, `exitMysql_trim_operands` | Emit TRIM / BOTH-LEADING-TRAILING forms |
| `position_function` (+ INSTR / CHARINDEX names) | `exitPosition_function` | Emit POSITION / CHARINDEX / INSTR per AST function name |

### 2.4 Routine invocation / aggregates

| Grammar | Walker | Actions |
|---------|--------|---------|
| `routine_invocation`, `function_name`, `sql_argument_list` | `exitRoutine_invocation`, `exitFunction_name`, `exitSql_argument_list` | Emit `fn(args…)` |
| `aggregate_function`, `set_function_type`, `set_qualifier_type`, count-all / general set function | `exitCount_all_aggregate`, `exitGeneral_set_function` | Emit `COUNT(*)`, `SUM(DISTINCT x)`, named aggregates |

### 2.5 CAST / TRY_CAST

| Grammar | Walker | Actions |
|---------|--------|---------|
| `cast_function_expression`, `cast_function_name` | `exitCast_function_expression` | Emit CAST/TRY_CAST with Phase 1.5 types |
| `::` cast operator if present in AST | (value expression path) | Emit `expr::type` when AST uses cast-operator shape |

### 2.6 CASE expressions

| Grammar | Walker | Actions |
|---------|--------|---------|
| `case_expression`, `when_clause_list`, `searched_when_clause`, `when_value_list`, `when_value_clause`, `else_clause`, `case_result` | `exitCase_expression`, `exitSearched_when_clause`, `exitWhen_value_clause`, `exitElse_clause`, … | Emit searched CASE and simple CASE |

**Substeps:**

1. Searched: `CASE WHEN cond THEN … ELSE … END`.
2. Simple: `CASE expr WHEN v THEN … END`.
3. Nested CASE; CASE in SELECT / WHERE / ORDER BY.
4. IFF shorthand if walker emits distinct AST (grammar has `IFF` token usage via functions — treat as routine unless dedicated node).

### 2.7 EXTRACT, DATE_PART, and datetime fields

Walker shape (capture plan closed Aug 2026): `extract={part, part_form, source[, source_type][, invocation]}`.

| Key | Emit |
|-----|------|
| `part` / `part_form` | Keyword as authored, or quoted string when `part_form=STRING` |
| `source` | Recursive emit for column, literal, calc, cast/function, nested `extract`, parentheses |
| `source_type` | Prefix typed literal when present (`DATE '…'`, `TIMESTAMP '…'`, etc.) |
| `invocation` | Absent or default → `EXTRACT(part FROM source)`; `DATE_PART` → `DATE_PART(part FROM source)` only (`FROM` differentiates extract AST from comma `function` calls) |

| Grammar | Walker | Actions |
|---------|--------|---------|
| `extract_expression`, `date_part_expression`, `extract_field`, `extract_source` | `exitExtract_expression`, `exitDate_part_expression`, `exitExtract_field`, `exitExtract_source` | Emit per table above |

**Verify:** Round-trip SELECT items mirroring `SqlEventWalkerExtractTests` exemplars (EXTRACT + DATE_PART comma/FROM, string/keyword parts, typed sources).

**Status:** 🔴 Not started (walker AST ready).

### 2.8 Window functions (value-expression family)

| Grammar | Walker | Actions |
|---------|--------|---------|
| `window_over_partition_expression`, `window_function`, `over_clause` | `exitWindow_over_partition_expression`, `exitWindow_function`, `exitOver_clause` | Emit `fn(...) OVER (...)` |
| `partition_by_clause` | `exitPartition_by_clause` | Emit `PARTITION BY` list via Phase 2 predicands |
| window order (`orderby` inside OVER) | (over clause children) | Emit `ORDER BY` + `select_direction` + `null_handling` |
| `bracket_frame_clause`, `rows_or_range`, `bracket_frame_definition`, `between_frame_definition`, `frame_edge`, `preceding_frame_edge`, `following_frame_edge`, `current_row_edge`, `bracket_constraint` | matching `exit*` | Emit `ROWS|RANGE BETWEEN … AND …` / single-edge frames |
| `item_select_function` (FIRST/LAST VALUE etc. if separate) | — | Cover via window_function AST |

**Substeps:**

1. `ROW_NUMBER() OVER ()`.
2. PARTITION BY only; ORDER BY only; both.
3. NULLS FIRST/LAST inside OVER ORDER BY.
4. Framed aggregate (one PRECEDING/FOLLOWING + one BETWEEN).
5. RESPECT/IGNORE NULLS if AST carries `null_handling`.

**Note:** Do not invent generation for non-ANSI window sites the walker leaves untested (WHERE/HAVING OVER) unless product requires it.

### 2.9 Predicand / row value wrappers

| Grammar | Walker | Actions |
|---------|--------|---------|
| `predicand_primary`, `value_expression_primary`, `parenthesized_value_expression`, `nonparenthesized_value_expression_primary` | matching exits | Ensure parentheses nodes emit `( … )` |
| `row_value_expression`, `row_value_predicand`, `row_value_predicand_list` | matching exits | Emit row constructors / lists for IN/BETWEEN RHS |
| `predicand_subquery` | `exitPredicand_subquery` | Emit scalar subquery `(SELECT …)` |

### 2.10 Arrays and array functions (planned grammar — not yet in `SQLSelectParser.g4`)

**Status:** ⏸️ **Placeholder.** Array constructors, subscripting, and array functions will be added to the PSS grammar soon. When those rules land, treat them as first-class Phase 2 value-expression surface (same emit + round-trip discipline as CASE/CAST/windows). Do **not** invent AST keys ahead of the walker; mirror whatever `exit*` / mumble shapes the new grammar produces.

**Expected rule families (names TBD when grammar lands — adjust to actual ANTLR rules):**

| Likely surface | Generator actions (when available) |
|----------------|--------------------------------------|
| Array constructor / literal (e.g. `[a, b]`, `ARRAY[...]`) | Emit constructor from AST list of Phase 2 value expressions |
| Array element / slice access (e.g. `arr[i]`, `arr[i:j]`) | Emit subscript / slice operators from AST index expressions |
| Array-typed CAST / data type | Extend Phase 1.5 `data_type` emit for `ARRAY` / `ARRAY<…>` (or dialect equivalent) |
| Array functions (e.g. `ARRAY_AGG`, `ARRAY_APPEND`, `ARRAY_CAT`, `ARRAY_CONTAINS`, `ARRAY_SIZE`, `FLATTEN` overlap) | Prefer Phase 2.4 `routine_invocation` when AST is a normal function call; add dedicated emit only if walker introduces distinct array-function nodes |
| Nested arrays / arrays of structs | Recursive emit through constructor + element expressions; round-trip nested fixtures |

**Substeps (execute after grammar + walker exits exist):**

1. Inventory new parser rules and `SqlParseEventWalker` `exit*` handlers; map each to mumble AST keys.
2. Add emit paths (or extend `onFunction` / new `onArray*` handlers) for constructor, subscript, and any non-routine array nodes.
3. Round-trip matrix: literal array in SELECT; subscript in WHERE; array function args; CAST to array type; array inside CASE/calc.
4. Confirm interaction with table-function `FLATTEN` (Phase 5.7) — relation-valued FLATTEN stays in FROM; scalar array functions stay in Phase 2.10.
5. Update Appendix A and the Phase 0 inventory table with the real rule names once merged.

---

## Phase 3 — Predicates and search conditions

**Goal:** Full WHERE/HAVING/QUALIFY/ON boolean layer.

### 3.1 Boolean composition

| Grammar | Walker | Actions |
|---------|--------|---------|
| `boolean_value_expression`, `or_predicate`, `and_predicate`, `negative_predicate`, `parenthetical_predicate`, `boolean_primary` | `exitOr_predicate`, `exitAnd_predicate`, `exitNegative_predicate`, `exitParen_clause`, … | Emit `AND` / `OR` / `NOT` / parentheses from AST `and`/`or`/`not` |

### 3.2 Comparison

| Grammar | Walker | Actions |
|---------|--------|---------|
| `comparison_predicate`, `comparison_operator`, `comp_op`, `relative_comp_op`, `similarity_op` | `exitComparison_predicate`, `exitComparison_operator` | Emit `= <> < <= > >=` and similarity ops from AST `condition={left, right, operator}` |
| `quantified_comparison_predicate`, `quantifier`, `all`, `some` | `exitQuantified_comparison_predicate`, `exitQuantifier` | Emit `comp_op quantifier (subquery)` from AST `condition={left, right, operator, quantifier}` (subquery on `right`) |

### 3.3 BETWEEN / NULL / IS

| Grammar | Walker | Actions |
|---------|--------|---------|
| `between_predicate`, `symmetry` | `exitBetween_predicate` | Emit `[NOT] BETWEEN [ASYMMETRIC\|SYMMETRIC] a AND b` |
| `null_predicate`, `is_null_clause`, `is_clause`, `truth_value` | `exitNull_predicate`, `exitIs_null_clause`, `exitIs_clause` | Emit `IS [NOT] NULL` / `IS [NOT] TRUE/FALSE/UNKNOWN` |

### 3.4 IN / LIKE ANY

| Grammar | Walker | Actions |
|---------|--------|---------|
| `in_predicate`, `in_predicate_value`, `in_value_list` | `exitIn_predicate`, `exitIn_predicate_value`, `exitIn_value_list` | Emit `[NOT] IN (list\|subquery)` |
| `like_any_predicate`, `like_any_operator`, `escape_character_clause` | `exitLike_any_predicate`, `exitLike_any_operator`, `exitEscape_character_clause` | Emit LIKE/ILIKE/RLIKE/REGEXP ANY + ESCAPE |

### 3.5 EXISTS

| Grammar | Walker | Actions |
|---------|--------|---------|
| `exists_predicate`, `exists_operator`, `exists_predicate_value` | `exitExists_predicate`, … | Emit `[NOT] EXISTS (subquery)` |

*(Removed: SQL-standard `unique_predicate` — not supported on Snowflake; grammar rule deleted.)*

### 3.6 Substitution as condition

| Grammar | Walker | Actions |
|---------|--------|---------|
| `substitution_predicate` | `exitSubstitution_predicate` | Emit condition-typed `<var>` (Phase 1.2 map applies) |
| `search_condition` | `exitSearch_condition` | Top-level router for WHERE/ON/HAVING/QUALIFY |

### 3.7 Clause wrappers

| Grammar | Walker | Actions |
|---------|--------|---------|
| `where_clause`, `having_clause`, `qualify_clause` | `exitWhere_clause`, `exitHaving_clause`, `exitQualify_clause` | Emit keyword + Phase 3 search_condition |

**Verify:** one round-trip per predicate family in SELECT WHERE and at least one in JOIN ON / HAVING / QUALIFY.

---

## Phase 4 — SELECT list and query-specification clauses (non-FROM)

**Goal:** Emit `query_specification` bodies assuming FROM emission exists (Phase 5) or is stubbed.

### 4.1 Query specification spine

| Grammar | Walker | Actions |
|---------|--------|---------|
| `query_specification` | `exitQuery_specification` | Emit `SELECT` + optional set_qualifier + select_list + optional INTO + from + where + groupby + having + qualify + orderby + limit |
| `set_qualifier` | `exitSet_qualifier` | Emit `DISTINCT` / `ALL` |
| `into_list` | `exitInto_list` | Emit `INTO …` if product uses it |

### 4.2 Select list

| Grammar | Walker | Actions |
|---------|--------|---------|
| `select_list`, `select_item`, `as_clause` | `exitSelect_list`, `exitSelect_item`, `exitAs_clause` | Emit comma-separated items + aliases |
| `select_all_columns`, `wildcard_reference` | matching exits | Emit `*` / `t.*` |

**Select-item substeps (each needs emit + round-trip):**

1. Bare column / qualified column.
2. Calc / function / CAST / CASE / window (reuse Phase 2).
3. Substitution predicand.
4. Scalar subquery item.
5. Explicit `AS alias` vs implicit name.
6. `unnamed_N` only if AST still carries it (prefer regenerating without inventing names — emit expression only if no alias key).

### 4.3 GROUP BY

| Grammar | Walker | Actions |
|---------|--------|---------|
| `groupby_clause`, `grouping_element_list`, `grouping_element` | `exitGroupby_clause`, `exitGrouping_element*` | Emit `GROUP BY` |
| `ordinary_grouping_set`, `ordinary_grouping_set_list` | matching exits | Expression lists |
| `rollup_list`, `cube_list`, `empty_grouping_set` | — | Emit ROLLUP/CUBE/`()` if AST present |

### 4.4 ORDER BY / LIMIT

| Grammar | Walker | Actions |
|---------|--------|---------|
| `orderby_clause`, `sort_specifier_list`, `sort_specifier`, `order_specification`, `null_ordering`, `null_first_last` | `exitOrderby_clause`, `exitSort_specifier`, `exitNull_ordering` | Emit `ORDER BY expr [ASC\|DESC] [NULLS FIRST\|LAST]` |
| `limit_clause` | `exitLimit_clause` | Emit `LIMIT` / `OFFSET` per AST |

---

## Phase 5 — FROM / JOIN / table sources / relational modifiers

**Goal:** Complete relation emission. This is the largest structural phase; follow the grammar nesting exactly.

### 5.1 `from_clause`

| Grammar | Walker | Actions |
|---------|--------|---------|
| `from_clause` | `exitFrom_clause` | Emit `FROM` + `table_reference_list` + optional `join_extension` |

**Substeps:**

1. Single-table FROM.
2. FROM + join_extension variable tail (`<join_ext>`).
3. Ensure generator does not drop join_extension when present on AST.

### 5.2 `table_reference_list` and join chains

| Grammar | Walker | Actions |
|---------|--------|---------|
| `table_reference_list` | `exitTable_reference_list` | Emit left-to-right chain of `table_primary` with commas / joins |
| `lateral_modifier` | (flag on join/table) | Emit `LATERAL` before right source when set |
| `unqualified_join` | (join node type) | Emit `CROSS JOIN` / `UNION JOIN` / `NATURAL [join_type] JOIN` |
| `qualified_join` + `join_type` | (join node) | Emit `[INNER\|LEFT\|RIGHT\|FULL] [OUTER] JOIN` |
| `join_specification` | — | Dispatch ON vs USING |
| `join_condition` | — | Emit `ON` + Phase 3 `search_condition` |
| `named_columns_join`, `using_term` | — | Emit `USING (col, …)` |

**Substeps:**

1. Comma cross-product (`FROM a, b`).
2. INNER / LEFT / RIGHT / FULL OUTER JOIN + ON.
3. CROSS / NATURAL JOIN.
4. USING join.
5. LATERAL subquery/table function join.
6. Nested join trees (AST `join={1,2,3,…}` ordering — match walker numeric keys).

### 5.3 `join_extension` / `join_extension_primary`

| Grammar | Walker | Actions |
|---------|--------|---------|
| `join_extension` | `exitJoin_extension` | Emit substitution variable as opaque join tail |
| `join_extension_primary` | `exitJoin_extension_primary` | Emit partial join chain used by join-extension endpoint |
| Endpoint `join_extension_value` | `exitJoin_extension_value` | Dedicated endpoint emit (Phase 12) |

### 5.4 `table_primary` (relation unit)

Grammar: `table_source_primary table_relational_modifier? relation_as_clause?`  
Walker: `exitTable_primary` merges source + optional pivot/unpivot + outer alias.

**Generator actions for `table_primary`:**

1. Emit **source** via §5.5.
2. If AST wraps `pivot` / `unpivot` around source (walker promotes modifier onto table node), emit modifier via §5.6 **after** source (postfix operator).
3. Emit outer `relation_as_clause` alias (`AS alias` / bare alias per AST).
4. Preserve parentheses around subquery sources when AST has `query` under `table`.

### 5.5 `table_source_primary` alternatives

Grammar alternatives → emit substeps:

#### 5.5.1 Physical / named object — `db_object_name`

| Walker | Actions |
|--------|---------|
| `exitTable_source_primary` (table branch), `exitDb_object_name` if present | Emit `db.schema.obj` / `schema.obj` / `obj` from `table={database_name?, schema?, table}` |

1. Unqualified table.
2. 2-part and 3-part names.
3. Optional inner `relation_as_clause` on source (grammar allows alias on `db_object_name` branch) **plus** outer alias on `table_primary` — emit only aliases present in AST without duplicating.

#### 5.5.2 Tuple / table substitution — `variable_identifier as_clause`

1. Emit `<tuple_var> AS alias` (grammar requires `as_clause` here).
2. Round-trip with substitution map replacing tuple var.

#### 5.5.3 Jinja relation — `jinja_identifier relation_as_clause?`

1. Emit Jinja identifier (Phase 1.3) + optional alias.

#### 5.5.4 Table function — `table_function_primary`

See §5.7 (full subtree).

#### 5.5.5 VALUES-in-FROM — `values_statement_primary`

1. Delegate to Phase 7 VALUES emitters.
2. Include aliases list forms (`fully_defined` / `aliased` VALUES).

#### 5.5.6 Subquery — `subquery relation_as_clause?`

| Grammar / walker | Actions |
|------------------|---------|
| `subquery`, `exitSubquery` | Emit `( query_expression )` + alias |

1. Simple derived table.
2. Set-op subquery in FROM.
3. WITH-bearing subquery only if grammar allows at that site (usually via query_expression).

### 5.6 `table_relational_modifier` — PIVOT / UNPIVOT

Grammar: `table_relational_modifier : unpivot_clause | pivot_clause`  
Walker: modifier exits + `exitTable_primary` attachment (`pivot` / `unpivot` keys).

#### 5.6.1 Shared operand / IN-list pieces

| Grammar | Walker | Actions |
|---------|--------|---------|
| `relational_modifier_operand_column` | (column ref) | Emit FOR/VALUE column |
| `relational_modifier_value_column`, `relational_modifier_name_column` | — | UNPIVOT value/name columns |
| `relational_modifier_list`, `relational_modifier_in_item`, `relational_modifier_alias` | pivot/unpivot IN item exits | Emit `(col [AS alias], …)` |

#### 5.6.2 UNPIVOT (`unpivot_clause`)

| Grammar | Walker | Actions |
|---------|--------|---------|
| `unpivot_clause`, `unpivot_null_policy` | unpivot exits (via table_primary / modifier path) | Emit `UNPIVOT [INCLUDE\|EXCLUDE NULLS] (value FOR name IN (…))` |

**Detailed actions:**

1. Emit keyword `UNPIVOT`.
2. Optional null policy (`INCLUDE NULLS` / `EXCLUDE NULLS`).
3. Emit `(`.
4. Emit value column (Phase 2 column ref).
5. Emit `FOR` + name column.
6. Emit `IN` + parenthesized IN list (columns with optional aliases / string labels).
7. Emit `)`.
8. Confirm milestone `emitUnpivotClause` covers labels variant; add round-trips for null policy if AST supports it.
9. Compose with §5.5 sources (physical, subquery, values) + outer alias.

#### 5.6.3 PIVOT (`pivot_clause`)

| Grammar | Walker | Actions |
|---------|--------|---------|
| `pivot_clause` | pivot exits + structured derived registration (walker-only; generator ignores symbol table) | Emit `PIVOT ( aggregates FOR col IN (…) ) [DEFAULT ON NULL …]` |
| `pivot_aggregate_clause`, `pivot_aggregate` | `exitPivot_aggregate*` | Single aggregate `fn(col) [AS a]` |
| `snowflake_pivot_aggregate_list`, `snowflake_pivot_aggregate`, `snowflake_pivot_aggregate_function` | matching exits | Multi-aggregate list AVG/COUNT/MAX/MIN/SUM |
| `pivot_in_clause`, `pivot_in_content` | matching exits | Router for IN forms |
| `pivot_in_value_list`, `pivot_in_value`, `pivot_in_literal` | matching exits | Literal IN list (+ optional aliases) |
| `pivot_in_prefix` | `exitPivot_in_prefix` | Prefix IN form if AST present |
| `pivot_in_any` | `exitPivot_in_any` | `ANY` IN form |
| `pivot_in_subquery` | `exitPivot_in_subquery` | Subquery IN form |
| `pivot_default_on_null_clause` | — | Emit DEFAULT ON NULL clause when present |

**Detailed actions:**

1. Emit `PIVOT (`.
2. Emit aggregate clause (single vs Snowflake list) — reuse Phase 2.4 function emission for `fn(col)`.
3. Emit `FOR` + operand column.
4. Emit `IN` content:
   - literal value list;
   - ANY;
   - prefix form;
   - subquery.
5. Emit `)`.
6. Optional DEFAULT ON NULL.
7. Round-trip matrix: basic metric pivot; multi-agg; IN subquery; IN ANY; default-on-null; pivot on subquery source; pivot+join+WHERE (milestone already has join/clause samples — extend gaps).

### 5.7 Table functions (`table_function_primary`)

| Grammar | Walker | Actions |
|---------|--------|---------|
| `table_function_primary`, `table_function` | `exitTable_function_primary`, `exitTable_function` | Dispatch to specific TF |
| `flatten_table_function` + args | `exitFlatten_*` | Emit `FLATTEN(...)` named args |
| `generator_table_function` + args | `exitGenerator_*` | Emit `GENERATOR(...)` |
| `result_scan_table_function` | `exitResult_scan_*` | Emit `RESULT_SCAN(...)` |
| `infer_schema_table_function` + files args | `exitInfer_schema_*` | Emit `INFER_SCHEMA(...)` |
| `validate_table_function` | `exitValidate_*` | Emit `VALIDATE(...)` |
| `generic_table_function`, `table_function_name`, `table_function_argument_list` | matching exits | Emit `TABLE(name(args))` / generic form per AST |
| `table_argument_literal`, `table_argument_boolean` | matching exits | Emit TF argument literals/booleans |

**Substeps per TF family:** minimal call → all documented args → alias → LATERAL usage in join.

### 5.8 `tuple_primary` / `tuple_source_primary` (tuple endpoint)

Used by **tuple substitution endpoint**, not always full FROM:

| Grammar | Walker | Actions |
|---------|--------|---------|
| `tuple_primary`, `tuple_source_primary` | (tuple endpoint path `exitTuple_value`) | Same source alternatives as table_source_primary **minus** some alias rules; allow `table_relational_modifier` on tuple_primary |

**Actions:** mirror §5.5–5.6 emit helpers; wire through Phase 12 tuple endpoint.

### 5.9 `relation_as_clause`

| Grammar | Walker | Actions |
|---------|--------|---------|
| `relation_as_clause` | `exitRelation_as_clause` | Emit `AS alias` / alias token consistently with walker |

---

## Phase 6 — Query expression & set operations

**Goal:** Full `query_expression` tree.

| Grammar | Walker | Actions |
|---------|--------|---------|
| `query_expression` | `exitQuery_expression` | Top dispatch |
| `intersected_query`, `intersect_clause`, `intersect_operator` | `exitIntersected_query`, `exitIntersect_clause` | Emit INTERSECT [ALL\|DISTINCT] chains |
| `unionized_query`, `union_clause`, `union_operator` | `exitUnionized_query`, `exitUnion_clause` | Emit UNION / EXCEPT [ALL\|DISTINCT] |
| `set_operation_member` | `exitSet_operation_member` | Emit parenthesized members / substitutions |
| `subquery`, `query_specification`, `variable_identifier` | (via `set_operation_member` / `query` — no `query_primary`) | Member is parenthesized `query_expression`, bare `SELECT`, or substitution |
| `query` | `exitQuery` | Wrapper used in WITH/script |

**Substeps:**

1. UNION / UNION ALL.
2. EXCEPT / EXCEPT ALL (mirror walker EXCEPT matrix).
3. INTERSECT.
4. Mixed precedence nests (INTERSECT binds tighter than UNION/EXCEPT — emit parentheses when AST encodes them).
5. Set-op of VALUES and SELECT.
6. ORDER BY/LIMIT on set-op result when attached to outer query_specification / wrapper.

---

## Phase 7 — VALUES statements

| Grammar | Walker | Actions |
|---------|--------|---------|
| `values_statement_primary`, `fully_defined_values_statement`, `aliased_values_statement`, `values_statement` | matching exits | Emit VALUES forms for FROM/script/CTE |
| `values_matrix`, `values_row` | matching exits | Emit row lists |
| `values_aliases`, `values_aliases_list` | matching exits | Emit column alias lists |
| `insert_values_statement` | matching exit | INSERT-specific VALUES shape |
| Endpoint `values_statement_end` | `exitValues_statement_end` | Phase 12 |

**Substeps:** bare VALUES; aliased; fully defined; multi-row; NULL cells; use as CTE body / FROM source / INSERT source (milestone covers many — close gaps).

---

## Phase 8 — WITH / CTE

| Grammar | Walker | Actions |
|---------|--------|---------|
| `with_query`, `with_clause`, `with_list_item`, `query_alias`, `cte_body` | `exitWith_query`, `exitWith_clause`, `exitWith_list_item`, `exitCte_body`, `exitQuery_alias` | Emit `WITH cte AS (body), …` + outer query |

**Substeps:**

1. Single CTE SELECT.
2. Multiple CTEs.
3. Nested WITH inside `cte_body` (Snowflake).
4. CTE body = VALUES / INSERT / UPDATE / DELETE / set-op (per grammar `cte_body` / query alternatives actually allowed).
5. RECURSIVE marker if/when grammar+AST support it.
6. Column lists on CTE names if AST has them.

---

## Phase 9 — DML statements

### 9.1 INSERT

| Grammar | Walker | Actions |
|---------|--------|---------|
| `insert_expression`, `snowflake_insert`, `postgres_insert` | `exitInsert_expression`, `exitSnowflake_insert`, `exitPostgres_insert` | Emit `insert={…}` wrapper contents |
| `insert_preamble` | `exitInsert_preamble` | `INSERT INTO` / `INSERT OVERWRITE INTO` etc. |
| `insert_target_table_primary` | `exitInsert_target_table_primary` | Target name + optional column list + alias |
| `insert_source_primary` / VALUES / SELECT / DEFAULT | `exitInsert_default_values_statement`, values/select paths | Emit source |
| `on_conflict_clause`, `conflict_target`, `conflict_action` | matching exits | `ON CONFLICT … DO NOTHING\|DO UPDATE SET … [WHERE]` |
| `returning` | `exitReturning` | `RETURNING select_list` (Phase 4.2) |

**Substeps beyond milestone:** CTE-bodied INSERT; overwrite preamble; conflict target inference forms; RETURNING expressions.

### 9.2 UPDATE

| Grammar | Walker | Actions |
|---------|--------|---------|
| `update_expression` | `exitUpdate_expression` | `UPDATE target SET … [FROM] [WHERE] [RETURNING]` |
| `assignment_expression_list`, `assignment_expression` | matching exits | `col = expr` lists |
| `from_clause` on UPDATE | Phase 5 | |

**Substeps:** multi-assign; FROM join; RETURNING; substitution LHS/RHS.

### 9.3 DELETE

| Grammar | Walker | Actions |
|---------|--------|---------|
| `delete_expression`, `delete_snowflake_expression`, `delete_postgres_expression` | matching exits | `DELETE FROM …` |
| `delete_using_clause` | `exitDelete_using_clause` | `USING table_reference_list` (Phase 5.2) |
| `delete_returning` / `returning` | matching exits | RETURNING list |

### 9.4 TRUNCATE

| Grammar | Walker | Actions |
|---------|--------|---------|
| `truncate_statement_primary`, `truncate_snowflake_expression`, `truncate_postgres_expression` | matching exits | Emit dialect truncate forms + opaque options |

---

## Phase 10 — DDL statements

**Opaque milestone first; structured options deferred** to [ddl-structured-options-parsing-workplan.md](ddl-structured-options-parsing-workplan.md).

| Grammar | Walker | Actions |
|---------|--------|---------|
| `ddl`, `ddl_primary`, `create_statement_primary`, each `create_*_expression` | matching `exitCreate_*` | Emit `CREATE type name …` with opaque `columns`/`options`/`parameters`/`query` |
| `drop_statement_primary`, `ddl_object_type`, `drop_options` | matching exits | `DROP …` + opaque options |
| `alter_statement_primary`, `alter_options` | matching exits | `ALTER …` + opaque options |
| `generic_ddl_options`, `generic_ddl_paren_content` | matching exits | Re-emit blobs verbatim |

**Substeps:**

1. Keep CTAS / column-blob / ALTER / DROP milestone green.
2. Per CREATE kind (view, mview, function, procedure, macro, sequence, schema, database, role, user, stage, file_format, index): ensure emit doesn’t drop `type` or name.
3. When structured-options plan delivers flags, prepend typed clauses then residual blob.

---

## Phase 11 — Script and sql_statement packaging

| Grammar | Walker | Actions |
|---------|--------|---------|
| `script`, `sql_statement`, `sql`, `dml_primary`, `ddl` | `exitScript`, `exitSql_statement`, `exitSql`, … | Emit multi-statement script with separators |
| Statement ordering | — | Preserve numeric statement keys |

**Substeps:** mixed DML+DDL+SELECT; WITH-as-statement; VALUES-only statement; ensure each Phase 9–10 kind can appear as a script item.

---

## Phase 12 — Parser endpoints (`SQLParserEndPoints`)

Each endpoint is a **root** that reuses Phases 1–8 emitters.

| Endpoint grammar | Walker | Generator action |
|------------------|--------|------------------|
| `column_value` | `exitColumn_value` | Emit column tree only |
| `predicand_value` | `exitPredicand_value` | Emit predicand/value expression tree |
| `condition_value` | `exitCondition_value` | Emit search_condition tree |
| `in_list_predicate_value` | `exitIn_list_predicate_value` | Emit IN-list tree |
| `tuple_value` | `exitTuple_value` | Emit tuple_primary tree (§5.8) |
| `query_value` | `exitQuery_value` | Emit query_expression |
| `join_extension_value` | `exitJoin_extension_value` | Emit join_extension_primary |
| `literal_value` | `exitLiteral_value` | Emit `LITERAL` root / `{literal=…}` (endpoint key `SQLPARSER_LITERAL_TREE_KEY`) |
| `values_statement_end` | `exitValues_statement_end` | Emit VALUES statement |
| `insert_end_point` / `update_end_point` / `delete_end_point` / `truncate_end_point` | matching exits | Emit full DML statement |

**Per endpoint substeps:**

1. Capture minimal AST from endpoint parse tests.
2. Replace `handleEndPoint` stub with real emit.
3. Round-trip test.
4. Mark inventory row complete.

---

## Recommended execution order (when resuming)

| Order | Phase | Why |
|------:|-------|-----|
| 1 | **0** | Contract + inventory + gate |
| 2 | **1** | Leaves + substitution map unlock everything |
| 3 | **2** + **3** | Expressions/predicates improve SELECT/WHERE fidelity fastest (include §2.10 when array grammar lands) |
| 4 | **4** | Select list / GROUP/ORDER/LIMIT polish |
| 5 | **5** | FROM/JOIN/TF/PIVOT-UNPIVOT completeness (largest) |
| 6 | **6**–**8** | Set ops, VALUES gaps, WITH edges |
| 7 | **9**–**11** | DML/DDL/script edge cases |
| 8 | **12** | Endpoints last (reuse all emitters) |
| ∥ | DDL structured options plan | Parallel product track; generator Phase 10.3 depends on it |

---

## Parked progress-tracker prompt (Phase 0.2)

> Try to measure our SQL generator progress and map out the entire work plan for completing the generation class. In order to do that, my supposition is that the generator ought to have ONE and ONLY ONE generating method PER GRAMMAR RULE. If a rule is at a leaf node for a LEXER item like a term or identifier, that can be a shared method or an inline method to emit the text, but especially any rule that contains other rules ought to have its own method — I THINK. Comment on this supposition and if you think its incorrect let me know before you proceed with the next part of my prompt here. If you proceed, then I want you to locate the rule constants generated by ANTLR and I want you to add a comment before each generation method you've already created in the class indicating which rule (or possibly set of rules) the method is handling directly (this should not include rules that are called by this generator method, but rules whose statements are directly emitted/constructed by the generator method. If you can do that I need you to finish this exercise by creating a detailed progress tracker here in this work plan where you list out every rule by its header and rule number, and indicate whether its complete, in progress, or still to be started. Finally, present this table to me so I can see where we are.

**Guidance for that prompt:** This roadmap already organizes by **major grammar rule / walker exit / AST shape**. Prefer annotating emit methods with rule sets over forcing a 1:1 Java method per rule when the walker collapses alternatives into one mumble key.

---

## Exit criteria (full completion)

- [ ] Phase 0 inventory appendix committed (all ~327 parser rules classified)
- [ ] Milestone `roundTrip*` suite remains green
- [ ] Phases 1–4 green for expression/predicate/select-list matrices
- [ ] Phase 5 complete for all `table_source_primary` alternatives + PIVOT/UNPIVOT IN forms + TF families
- [ ] Phases 6–8 set-op / VALUES / WITH edge cases green
- [ ] Phases 9–11 statement packaging green
- [ ] Phase 12 endpoints non-stub with tests
- [ ] Substitution map API documented and tested
- [ ] Consolidation worklist continues to show **13.6 ❌ spun off** (this doc is source of truth)

---

## Appendix A — Grammar rule index by phase (quick lookup)

| Phase | Grammar rules (primary) |
|------:|-------------------------|
| 1 | `identifier*`, `variable_identifier*`, `jinja_*`, `*_literal`, `puml_constant_identifier`, `data_type*` |
| 2 | `column_*`, `value_expression*`, `additive_*`, `multiplicative_*`, `routine_invocation`, `aggregate_*`, `case_*`, `cast_*`, `trim_*`, `position_*`, `window_*`, `over_*`, `partition_by_clause`, `bracket_frame_*`, `extract_*`, **arrays / array functions (planned — §2.10)** |
| 3 | `search_condition`, `*_predicate`, `comparison_*`, `quantified_*`, `between_*`, `in_*`, `like_any_*`, `exists_*`, `null_predicate`, `is_*`, `where/having/qualify_clause` |
| 4 | `query_specification`, `set_qualifier`, `select_*`, `groupby_*`, `orderby_*`, `limit_clause`, `into_list` |
| 5 | `from_clause`, `table_reference_list`, `*_join*`, `table_primary`, `table_source_primary`, `tuple_*`, `table_relational_modifier`, `pivot_*`, `unpivot_*`, `relational_modifier_*`, `table_function_*`, `flatten_*`, `generator_*`, `result_scan_*`, `infer_schema_*`, `validate_*`, `join_extension*` |
| 6 | `query_expression`, `intersected_query`, `unionized_query`, `set_operation_member`, `subquery`, `query_specification`, `query` |
| 7 | `values_*` |
| 8 | `with_*`, `cte_body`, `query_alias` |
| 9 | `insert_*`, `postgres_insert`, `on_conflict_*`, `update_*`, `delete_*`, `assignment_*`, `returning`, `truncate_*` |
| 10 | `ddl*`, `create_*`, `drop_*`, `alter_*`, `generic_ddl_*` |
| 11 | `script`, `sql_statement`, `sql`, `dml_primary` |
| 12 | `*_value`, `*_end_point`, `values_statement_end` |

---

## Appendix B — Suggested first resume slice

1. Phase **0** (gate + inventory start).
2. Phase **1.2** substitution map + Phase **2.6** CASE + Phase **3.4** IN/LIKE ANY.
3. Phase **5.5–5.7** table_source gaps (TF matrix) while PIVOT/UNPIVOT milestone stays green.
4. Phase **6** EXCEPT/INTERSECT round-trips.
5. Phase **12** endpoints once Phases 1–5 emitters are solid.
