# SqlParseEventWalker — `exit*` method coverage gaps

**Role:** current coverage inventory, not a separate workplan. Release scope,
priorities and closure decisions belong to
[master workplan Entry 11](../sql-grammar-5.1.4-workplan.md#entry-11--walker-exit-coverage-jacoco).
Superseded measurement snapshots remain in git history.

**Generated:** Nov 19, 2026, from T023's successful full default execution using report-only `mvn -f parse/pom.xml -Pisolated-build jacoco:report`; no tests rerun  
**Class:** `sql.walker.SqlParseEventWalker`  
**Scope:** ANTLR listener **`exit*`** methods only (321 total). Not every lexer token or private helper. The Tier 2 release decision below is limited to the twelve approved Entry 11 targets.

## Summary

| Metric | Count |
|--------|------:|
| `exit*` methods | 321 |
| **Never executed** (0 lines covered by full test suite) | **0** |
| Executed with **some** missed lines | 70 |
| Fully line-covered (0 missed lines in JaCoCo) | 251 |

**Interpretation:** A method with **0 covered lines** has no test that walks that grammar alternative end-to-end. **Partial** methods are hit by at least one test but still have branches or error paths uncovered — useful for a second pass, not listed exhaustively below.

## Tier 1 — No test hits these `exit*` methods

Tier 1 remains closed in this refresh (**no zero-hit `exit*` methods**).

Historical Tier 1 exemplars (`exitNamed_columns_join`, `exitUnpivot_null_policy`, pivot `IN (ANY)`/`IN (SELECT …)`, and T1.5–T1.11 follow-ups) remain covered and are now maintained as regression baseline rather than open gap targets.

## Tier 2 — Approved release targets (JaCoCo Nov 19, 2026)

The approved twelve-method set was measured from the latest successful run.
**Ten** still have **≥3 missed lines** or **<85%** line coverage; two now have
only Tier 3 residuals. The rows below are the single maintained inventory
of that approved set; they do not establish parser defects.

| ID | `exit*` | Lines (cov/total) | % | Missed |
|----|---------|-------------------|---|--------|
| T2-01 | `exitPredicand_primary` | 21/27 | 77.8% | 6 |
| T2-02 | `exitRelational_modifier_in_item` | 22/28 | 78.6% | 6 |
| T2-03 | `exitValue_expression_primary` | 60/66 | 90.9% | 6 |
| T2-04 | `exitOrdered_aggregate_call` | 25/29 | 86.2% | 4 |
| T2-05 | `exitValue_expression` | 52/55 | 94.5% | 3 |
| T2-06 | `exitSelect_item` | 58/62 | 93.5% | 4 |
| T2-07 | `exitConflict_target` | 12/15 | 80% | 3 |
| T2-09 | `exitSql_statement` | 18/21 | 85.7% | 3 |
| T2-10 | `exitSet_operation_member` | 24/27 | 88.9% | 3 |
| T2-12 | `exitUnpivot_clause` | 35/38 | 92.1% | 3 |

Now Tier 3, explicitly outside the approved 5.1.4 residual scope:

| Original ID | Method | Lines (cov/total) | % | Missed |
|---|---|---|---|---:|
| T2-08 | `exitTable_argument_literal` | 13/15 | 86.7% | 2 |
| T2-11 | `exitJinja_function_call` | 31/33 | 93.9% | 2 |

Evidence: [current JaCoCo XML](../../target-maven/site/jacoco/jacoco.xml).
The report-only command loaded the execution file last modified at the end
of T023 (2026-11-19 09:18:53 -05:00), analyzed 544 classes and exited 0.
No tests, lexer/parser changes or coverage waivers were added.

## How to refresh

From the repository root, render matching existing execution data without
executing tests again:

```bash
mvn -f parse/pom.xml -Pisolated-build jacoco:report
# Report: parse/target-maven/site/jacoco/jacoco.xml
```

After test changes, execute the relevant tests before regenerating the report.
Update the measurement date, evidence and counters here, and release decisions
in the master workplan; do not append updates to the frozen Slice D record.

## Related docs

- [relational-modifier-lineage-consolidation-migration.md](relational-modifier-lineage-consolidation-migration.md) — PIVOT/UNPIVOT convert-egress lineage migration (M0–M5).
- [sql_walker_astwalkers_gap_report.md](sql_walker_astwalkers_gap_report.md) — broader walker + helper method gaps (older snapshot).
- [sql-grammar-5.1.4-workplan.md](../sql-grammar-5.1.4-workplan.md) Entry **12** — caller audit vs JaCoCo policy.

