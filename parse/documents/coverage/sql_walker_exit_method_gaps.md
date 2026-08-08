# SqlParseEventWalker — `exit*` method coverage gaps

**Workplan (checklists):** [sql_walker_exit_method_coverage-workplan.md](sql_walker_exit_method_coverage-workplan.md)  
**Generated:** Aug 2026 (after full `cd parse && mvn verify` + JaCoCo `target/site/jacoco/jacoco.xml`)  
**Class:** `sql.walker.SqlParseEventWalker`  
**Scope:** ANTLR listener **`exit*`** methods only (277 total). Not every lexer token or private helper.

## Summary

| Metric | Count |
|--------|------:|
| `exit*` methods | 277 |
| **Never executed** (0 lines covered by full test suite) | **10** |
| Executed with **some** missed lines (partial) | 70 |
| Fully line-covered (0 missed lines in JaCoCo) | 197 |

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

Remaining Tier 1 gaps:

| `exit*` method | Grammar rule / path | What to exercise (sketch) |
| `exitPivot_in_any` | `pivot_in_any` | `PIVOT … IN (ANY)` or `IN (ANY ORDER BY …)` |
| `exitPivot_in_subquery` | `pivot_in_subquery` | `PIVOT … IN (SELECT …)` |
| `exitTable_argument_boolean` | `table_argument_boolean` | Table-function named arg with boolean literal (e.g. FLATTEN / similar `=> true`) |
| `exitInfer_schema_files_argument` | `infer_schema_files_argument` | `INFER_SCHEMA` / `INFER_SCHEMA` with `FILES => (…)` argument shape |
| `exitValidate_table_function` | `validate_table_function` | `VALIDATE` table function call in FROM |
| `exitRow_value_predicand_list` | `row_value_predicand_list` | Parenthesized multi-column grouping set, e.g. `GROUP BY (a, b)` or `ROLLUP ((a,b), c)` |
| `exitOther_trim_operands` | `trim_operands` alt `# other_trim_operands` | Non-MySQL `TRIM`: `TRIM(source, characters)` comma form |
| `exitOrdinary_grouping_set_list` | `ordinary_grouping_set_list` | `ROLLUP` / `CUBE` / grouping sets with multiple parenthesized lists |

These are **main-grammar** alternatives that the union parser accepts but the current walker test corpus does not drive.

## Tier 2 — Hit by tests but notable line gaps (sample)

Methods with **≥5 missed lines** and **&lt;80%** line coverage on the method (good follow-ups after Tier 1):

| `exit*` method | Grammar / area | Covered / missed lines (approx.) |
|----------------|----------------|----------------------------------|
| `exitTable_argument_literal` | Table-function args | 6 / 13 |
| `exitInfer_schema_argument` | `INFER_SCHEMA` kwargs | 13 / 10 |
| `exitSubquery` | `subquery` in FROM | 5 / 8 |
| `exitInsert_target_table_primary` | INSERT target (no table-function) | 41 / 10 |
| `exitJinja_arg` / `exitJinja_function_call` | Jinja in SQL | partial |
| `exitRelational_modifier_in_item` | PIVOT/UNPIVOT `IN` list | 22 / 6 |
| `exitUnpivot_clause` | UNPIVOT (policy branch aside) | 33 / 5 |
| `exitQuantified_comparison_predicate` | `ANY`/`ALL` subquery compares | 21 / 4 |

Full JaCoCo detail: `parse/target/site/jacoco/index.html` → `SqlParseEventWalker`; legacy aggregate CSV may be stale — regenerate from `jacoco.xml` if needed.

## How to refresh

```bash
cd parse && mvn verify
# Report: parse/target/site/jacoco/jacoco.xml
```

Re-run the exit-method vs JaCoCo comparison (same approach as Aug 2026 gap pass) after adding tests; update this file when Tier 1 list shrinks.

## Related docs

- [sql_walker_astwalkers_gap_report.md](sql_walker_astwalkers_gap_report.md) — broader walker + helper method gaps (older snapshot).
- [helper-dead-code-hygiene-workplan.md](../helper-dead-code-hygiene-workplan.md) — caller audit vs JaCoCo policy.
