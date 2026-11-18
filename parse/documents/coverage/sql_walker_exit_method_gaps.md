# SqlParseEventWalker — `exit*` method coverage gaps

**Workplan:** [sql-grammar-5.1.4-workplan.md](../sql-grammar-5.1.4-workplan.md) **Entry 11** (retired checklist: `sql_walker_exit_method_coverage-workplan.md`)  
**Generated:** Nov 18, 2026 (refresh after `cd parse && mvn verify`)  
**Class:** `sql.walker.SqlParseEventWalker`  
**Scope:** ANTLR listener **`exit*`** methods only (317 total). Not every lexer token or private helper.

## Summary

| Metric | Count |
|--------|------:|
| `exit*` methods | 317 |
| **Never executed** (0 lines covered by full test suite) | **0** |
| Executed with **some** missed lines (partial = Tier 2 + Tier 3) | 72 |
| Fully line-covered (0 missed lines in JaCoCo) | 245 |

**Interpretation:** A method with **0 covered lines** has no test that walks that grammar alternative end-to-end. **Partial** methods are hit by at least one test but still have branches or error paths uncovered — useful for a second pass, not listed exhaustively below.

## Tier 1 — No test hits these `exit*` methods

Tier 1 remains closed in this refresh (**no zero-hit `exit*` methods**).

Historical Tier 1 exemplars (`exitNamed_columns_join`, `exitUnpivot_null_policy`, pivot `IN (ANY)`/`IN (SELECT …)`, and T1.5–T1.11 follow-ups) remain covered and are now maintained as regression baseline rather than open gap targets.

## Tier 2 — Substantial gaps (JaCoCo Nov 18, 2026)

Methods with **≥3 missed lines** or **<85%** line coverage (**12 methods**).


| ID | `exit*` | Lines (cov/total) | % | Missed |
|----|---------|-------------------|---|--------|
| T2-01 | `exitPredicand_primary` | 21/27 | 78% | 6 |
| T2-02 | `exitRelational_modifier_in_item` | 24/30 | 80% | 6 |
| T2-03 | `exitValue_expression_primary` | 57/63 | 90% | 6 |
| T2-04 | `exitOrdered_aggregate_call` | 24/28 | 86% | 4 |
| T2-05 | `exitValue_expression` | 52/56 | 93% | 4 |
| T2-06 | `exitSelect_item` | 60/64 | 94% | 4 |
| T2-07 | `exitConflict_target` | 12/15 | 80% | 3 |
| T2-08 | `exitTable_argument_literal` | 13/16 | 81% | 3 |
| T2-09 | `exitSql_statement` | 22/25 | 88% | 3 |
| T2-10 | `exitSet_operation_member` | 27/30 | 90% | 3 |
| T2-11 | `exitJinja_function_call` | 31/34 | 91% | 3 |
| T2-12 | `exitUnpivot_clause` | 36/39 | 92% | 3 |

### Delta vs Aug 2026 list

- **Completed (now full line coverage):**
  - `exitJinja_arg`, `exitStatic_data_type_name`, `exitWith_clause`, `exitAssignment_expression_list`, `exitInsert_preamble`, `exitSubquery`.
- **Still partial but no longer Tier 2 (now Tier 3 residual):**
  - `exitEveryRule`, `exitInfer_schema_argument`, `exitFlatten_argument`, `exitDrop_options`, `exitAlter_options`, `exitInsert_target_table_primary`, `exitQuantified_comparison_predicate`, `exitScript`, `exitGenerator_argument`, `exitJinja_name`, `exitOrdinary_grouping_set`, `exitRow_value_expression`.
- **New Tier 2 additions in this refresh (data-backed from current parser):**
  - `exitPredicand_primary`, `exitValue_expression_primary`, `exitOrdered_aggregate_call`, `exitValue_expression`, `exitSelect_item`.

### 5.1.4 feature-area spot check (newly introduced/enhanced paths)

- **Array/date-time feature exits are mostly full coverage** (`exitArray_conversion_seed_function`, `exitArray_range_seed_function`, `exitArray_position_seed_function`, `exitArray_zip_seed_function`, `exitArray_unary_transform_function`, `exitArray_transform_function`, `exitSnowflake_datetime_*` all at 100% line coverage in this refresh).
- **Residual partials in nearby feature paths are Tier 3**, not Tier 2 (`exitArray_literal_elements` 15/17, `exitArray_literal_expression` 21/22, `exitPivot_in_literal` 18/20, `exitJinja_name` 12/14).

## How to refresh

```bash
cd parse && mvn verify
# Report: parse/target/site/jacoco/jacoco.xml
```

Re-run the exit-method vs JaCoCo comparison after adding tests; update this file whenever Tier 2/Tier 3 membership changes.

## Related docs

- [relational-modifier-lineage-consolidation-migration.md](relational-modifier-lineage-consolidation-migration.md) — PIVOT/UNPIVOT convert-egress lineage migration (M0–M5).
- [sql_walker_astwalkers_gap_report.md](sql_walker_astwalkers_gap_report.md) — broader walker + helper method gaps (older snapshot).
- [sql-grammar-5.1.4-workplan.md](../sql-grammar-5.1.4-workplan.md) Entry **12** — caller audit vs JaCoCo policy.

## Relational-modifier lineage — operand classification

**Invariant:** *operand classification ≠ skip lineage.* When convert egress classifies a column ref as `RESOLVED_PIVOT_OPERAND`, `RESOLVED_UNPIVOT_IN_SOURCE`, or derived UNPIVOT VALUE/FOR, that classification selects **how** to materialize (`applyConvertEgress*`, `materializeInterface*DependencyLineage`, derivation buckets)—not whether to skip interface/clause lineage.

Single owner paths (post-M5):

- **Interface loop:** `classifyColumnRefAtConvertEgress` → early egress blocks materialize operands before the qualified `switch` drain.
- **Clause archive:** `archiveClauseColumnRefAtConvertEgress` handles operands on `clauseEgressResult` before unqualified `switch`.
- **Remaining unresolved drain:** pivot/unpivot operands via `applyConvertEgress*` then `continue` (no duplicate `materializePivotOperandColumnAtConvertEgress` in `applyUnqualifiedScopeResolutionResult`).

See [migration plan — design invariant](relational-modifier-lineage-consolidation-migration.md#design-invariant-target-end-state).
