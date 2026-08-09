# SqlParseEventWalker — `exit*` method coverage gaps

**Workplan (checklists):** [sql_walker_exit_method_coverage-workplan.md](sql_walker_exit_method_coverage-workplan.md)  
**Generated:** Aug 9, 2026 (after `cd parse && mvn verify` + JaCoCo `target/site/jacoco/jacoco.xml`)  
**Class:** `sql.walker.SqlParseEventWalker`  
**Scope:** ANTLR listener **`exit*`** methods only (283 total). Not every lexer token or private helper.

## Summary

| Metric | Count |
|--------|------:|
| `exit*` methods | 283 |
| **Never executed** (0 lines covered by full test suite) | **0** |
| Executed with **some** missed lines (partial = Tier 2 + Tier 3) | 72 |
| Fully line-covered (0 missed lines in JaCoCo) | 211 |

**Interpretation:** A method with **0 covered lines** has no test that walks that grammar alternative end-to-end. **Partial** methods are hit by at least one test but still have branches or error paths uncovered — useful for a second pass, not listed exhaustively below.

## Tier 1 — No test hits these `exit*` methods (priority for new exemplars)

### Tier 1.1 complete — `exitNamed_columns_join`

| `exit*` method | Grammar rule / path | Status |
|----------------|---------------------|--------|
| `exitNamed_columns_join` | `named_columns_join` | **Covered** — see [workplan T1.1](sql_walker_exit_method_coverage-workplan.md#t11-exitnamed_columns_join--named_columns_join--complete) and `SqlEventWalkerJoinsAndTableResolutionTests` (JOIN USING section). |

### Tier 1.2 complete — `exitUnpivot_null_policy`

| `exit*` method | Grammar rule / path | Status |
|----------------|---------------------|--------|
| `exitUnpivot_null_policy` | `unpivot_null_policy` | **Covered** — see [workplan T1.2](sql_walker_exit_method_coverage-workplan.md#t12-exitunpivot_null_policy--unpivot_null_policy--complete) and `SqlEventWalkerPivotUnpivotTests` (`unpivotIncludeNullsNullPolicyAstShapeTest`, `unpivotExcludeNullsNullPolicyAstShapeTest`). |

### Tier 1.3–1.4 complete — pivot `IN (ANY)` / `IN (SELECT …)`

| `exit*` method | Grammar rule / path | Status |
|----------------|---------------------|--------|
| `exitPivot_in_any` | `pivot_in_any` | **Covered** — `pivotInAnyAstShapeTest`, `pivotInAnyOrderBy*` in `SqlEventWalkerPivotUnpivotTests`. |
| `exitPivot_in_subquery` | `pivot_in_subquery` | **Covered** — `pivotInSubqueryAstShapeTest`. |

### Tier 1.5–1.10 — exemplar tests (Aug 2026 JaCoCo refresh)

| `exit*` method | Test | Notes |
|----------------|------|--------|
| `exitTable_argument_boolean` | `inferSchemaIgnoreCaseFalseTableArgumentBooleanT1_5Test` | **COMPLETE** — `ignore_case=FALSE` scalar |
| `exitInfer_schema_files_argument` | `inferSchemaFilesArgumentT1_6Test` | AST clean |
| `exitValidate_table_function` | `validateTableFunctionT1_7Test` | AST clean |
| `exitRow_value_predicand_list` | `groupByRowValuePredicandListT1_8Test` | **COMPLETE** — Tier 3: 1 missed line |
| ~~`exitOther_trim_operands`~~ | — | **Removed** — no path from `sql()`; grammar alt deleted |
| `exitOrdinary_grouping_set_list` | `rollupOrdinaryGroupingSetListT1_10Test` | **COMPLETE** — plus `SqlEventWalkerGroupByGroupingSetsTests` |
| `exitEmpty_grouping_set` | `emptyGroupingSetT1_11Test` | **COMPLETE** — `GROUP BY ()` → `groupby={set={}}` |

**Tier 1 open:** none.

### Tier 2.8 exemplar — UNPIVOT `relational_modifier_in_item`

| `exit*` method | Grammar rule / path | Status |
|----------------|---------------------|--------|
| `exitRelational_modifier_in_item` | UNPIVOT `IN (...)` column items | **Exemplar** — tests in place; JaCoCo **6** missed lines (Aug 2026) |
| `exitRelational_modifier_alias` | Optional `AS` / label on IN item | **COMPLETE** — 0 missed (T3.24) |

## Tier 2 — Substantial gaps (JaCoCo Aug 2026)

Methods with **≥3 missed lines** or **<85%** line coverage (19 total on checklist below; **19** across all `exit*`).


| ID | `exit*` | Lines (cov/total) | % | Missed |
|----|---------|-------------------|---|--------|
| T2.2 | `exitInsert_target_table_primary` | 41/51 | 80% | 10 |
| T2.4 | `exitJinja_arg` | 15/24 | 62% | 9 |
| T2.5 | `exitJinja_function_call` | 25/34 | 74% | 9 |
| T2.6 | `exitSubquery` | 5/13 | 38% | 8 |
| T2.7 | `exitStatic_data_type_name` | 20/27 | 74% | 7 |
| T2.1 | `exitTable_argument_literal` | 14/20 | 70% | 6 |
| T2.8 | `exitRelational_modifier_in_item` | 24/30 | 80% | 6 |
| T2.13 | `exitEveryRule` | 25/31 | 81% | 6 |
| T2.9 | `exitWith_clause` | 14/19 | 74% | 5 |
| T2.3 | `exitInfer_schema_argument` | 18/23 | 78% | 5 |
| T2.11 | `exitFlatten_argument` | 15/19 | 79% | 4 |
| T2.19 | `exitConflict_target` | 12/15 | 80% | 3 |
| T2.16 | `exitDrop_options` | 13/16 | 81% | 3 |
| T2.17 | `exitAlter_options` | 13/16 | 81% | 3 |
| T2.15 | `exitSql_statement` | 21/24 | 88% | 3 |
| T2.20 | `exitSet_operation_member` | 22/25 | 88% | 3 |
| T2.10 | `exitUnpivot_clause` | 36/39 | 92% | 3 |
| T2.24 | `exitAssignment_expression_list` | 5/7 | 71% | 2 |
| T2.18 | `exitInsert_preamble` | 10/12 | 83% | 2 |

**Reclassified from Tier 2 checklist to Tier 3** (1–2 missed, ≥85%): T2.12 `exitQuantified_comparison_predicate`, T2.14 `exitScript`, T2.21 `exitGenerator_argument`, T2.22 `exitJinja_name`, T2.23 `exitOrdinary_grouping_set`, T2.25 `exitRow_value_expression`.

**Not on numbered checklist (Tier 3):** `exitGroupby_clause`, `exitGroupby_distinct_body` (GROUP BY clause exits; 1 missed line each).

## How to refresh

```bash
cd parse && mvn verify
# Report: parse/target/site/jacoco/jacoco.xml
```

Re-run the exit-method vs JaCoCo comparison (same approach as Aug 2026 gap pass) after adding tests; update this file when Tier 1 list shrinks.

## Related docs

- [relational-modifier-lineage-consolidation-migration.md](relational-modifier-lineage-consolidation-migration.md) — PIVOT/UNPIVOT convert-egress lineage migration (M0–M5).
- [sql_walker_astwalkers_gap_report.md](sql_walker_astwalkers_gap_report.md) — broader walker + helper method gaps (older snapshot).
- [helper-dead-code-hygiene-workplan.md](../helper-dead-code-hygiene-workplan.md) — caller audit vs JaCoCo policy.

## Relational-modifier lineage — operand classification

**Invariant:** *operand classification ≠ skip lineage.* When convert egress classifies a column ref as `RESOLVED_PIVOT_OPERAND`, `RESOLVED_UNPIVOT_IN_SOURCE`, or derived UNPIVOT VALUE/FOR, that classification selects **how** to materialize (`applyConvertEgress*`, `materializeInterface*DependencyLineage`, derivation buckets)—not whether to skip interface/clause lineage.

Single owner paths (post-M5):

- **Interface loop:** `classifyColumnRefAtConvertEgress` → early egress blocks materialize operands before the qualified `switch` drain.
- **Clause archive:** `archiveClauseColumnRefAtConvertEgress` handles operands on `clauseEgressResult` before unqualified `switch`.
- **Remaining unresolved drain:** pivot/unpivot operands via `applyConvertEgress*` then `continue` (no duplicate `materializePivotOperandColumnAtConvertEgress` in `applyUnqualifiedScopeResolutionResult`).

See [migration plan — design invariant](relational-modifier-lineage-consolidation-migration.md#design-invariant-target-end-state).
