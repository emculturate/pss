# SqlParseEventWalker — `exit*` method coverage gaps

**Workplan (checklists):** [sql_walker_exit_method_coverage-workplan.md](sql_walker_exit_method_coverage-workplan.md)  
**Generated:** Aug 2026 (after full `cd parse && mvn verify` + JaCoCo `target/site/jacoco/jacoco.xml`)  
**Class:** `sql.walker.SqlParseEventWalker`  
**Scope:** ANTLR listener **`exit*`** methods only (277 total). Not every lexer token or private helper.

## Summary

| Metric | Count |
|--------|------:|
| `exit*` methods | 277 |
| **Never executed** (0 lines covered by full test suite) | **0** |
| Executed with **some** missed lines (partial) | 72 |
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
| `exitRow_value_predicand_list` | `groupByRowValuePredicandListT1_8Test` | **COMPLETE** |
| ~~`exitOther_trim_operands`~~ | — | **Removed** — no path from `sql()`; grammar alt deleted |
| `exitOrdinary_grouping_set_list` | `rollupOrdinaryGroupingSetListT1_10Test` | **COMPLETE** — plus `SqlEventWalkerGroupByGroupingSetsTests` |
| `exitEmpty_grouping_set` | `emptyGroupingSetT1_11Test` | **COMPLETE** — `GROUP BY ()` → `groupby={set={}}` |

**Tier 1 open:** none.

### Tier 2.8 complete — UNPIVOT `relational_modifier_in_item`

| `exit*` method | Grammar rule / path | Status |
|----------------|---------------------|--------|
| `exitRelational_modifier_in_item` | UNPIVOT `IN (...)` column items | **Covered** — `unpivotInList*` ast-shape tests + existing IN-list matrix. |
| `exitRelational_modifier_alias` | Optional `AS` / label on IN item | **Covered** — same tests (identifier without `AS`, string literal with `AS`). |

## Tier 2 — Hit by tests but notable line gaps (sample)

Methods with **≥5 missed lines** and **&lt;80%** line coverage on the method (good follow-ups after Tier 1):

| `exit*` method | Grammar / area | Covered / missed lines (approx.) |
|----------------|----------------|----------------------------------|
| `exitTable_argument_literal` | Table-function args | 6 / 13 |
| `exitInfer_schema_argument` | `INFER_SCHEMA` kwargs | 13 / 10 |
| `exitSubquery` | `subquery` in FROM | 5 / 8 |
| `exitInsert_target_table_primary` | INSERT target (no table-function) | 41 / 10 |
| `exitJinja_arg` / `exitJinja_function_call` | Jinja in SQL | partial |
| `exitQuantified_comparison_predicate` | `ANY`/`ALL` subquery compares | 21 / 4 |

Full JaCoCo detail: `parse/target/site/jacoco/index.html` → `SqlParseEventWalker`; legacy aggregate CSV may be stale — regenerate from `jacoco.xml` if needed.

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
