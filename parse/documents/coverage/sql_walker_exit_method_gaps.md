# SqlParseEventWalker — `exit*` method coverage gaps

**Role:** current coverage inventory, not a separate workplan. Release scope,
priorities and closure decisions belong to
[master workplan Entry 11](../sql-grammar-5.1.4-workplan.md#entry-11--walker-exit-coverage-jacoco).
Superseded measurement snapshots remain in git history.

**Generated:** Nov 19, 2026, after focused Entry 11 tests, appended to T023's matching full default execution; not a new full-suite run

**Class:** `sql.walker.SqlParseEventWalker`  
**Scope:** ANTLR listener **`exit*`** methods only (321 total). Not every lexer token or private helper. The Tier 2 release decision below is limited to the twelve approved Entry 11 targets.

## Summary

| Metric | Count |
|--------|------:|
| `exit*` methods | 321 |
| **Never executed** (0 lines covered by full test suite) | **0** |
| Executed with **some** missed lines | 69 |
| Fully line-covered (0 missed lines in JaCoCo) | 252 |

**Interpretation:** A method with **0 covered lines** has no test that walks that grammar alternative end-to-end. **Partial** methods are hit by at least one test but still have branches or error paths uncovered — useful for a second pass, not listed exhaustively below.

## Tier 1 — No test hits these `exit*` methods

Tier 1 remains closed in this refresh (**no zero-hit `exit*` methods**).

Historical Tier 1 exemplars (`exitNamed_columns_join`, `exitUnpivot_null_policy`, pivot `IN (ANY)`/`IN (SELECT …)`, and T1.5–T1.11 follow-ups) remain covered and are now maintained as regression baseline rather than open gap targets.

## Tier 2 — Approved release targets, scoped closure (Nov 19, 2026)

The ten remaining targets are closed through five focused strict tests and
the source-backed dispositions below. Their raw missed-line total fell from
**41 to 30**. Seven methods still meet the numeric Tier 2 threshold
(**≥3 missed lines** or **<85%** coverage); they are **not** fully line-covered.
Their remaining lines require collector shapes that valid grammar/producers
do not emit. Three methods now have only Tier 3 residuals.

| ID | `exit*` | Lines (cov/total) | % | Missed | Residual disposition |
|----|---------|-------------------|---|--------|----------------------|
| T2-01 | `exitPredicand_primary` | 22/27 | 81.5% | 5 | Upstream scalar-query wrapping |
| T2-02 | `exitRelational_modifier_in_item` | 25/28 | 89.3% | 3 | Non-map child guard |
| T2-03 | `exitValue_expression_primary` | 64/66 | 97.0% | 2 | Tier 3; numeric-key / map guards |
| T2-04 | `exitOrdered_aggregate_call` | 27/29 | 93.1% | 2 | Tier 3; missing mandatory child |
| T2-05 | `exitValue_expression` | 52/55 | 94.5% | 3 | Child type / cardinality guards |
| T2-06 | `exitSelect_item` | 58/62 | 93.5% | 4 | Upstream scalar-query wrapping |
| T2-07 | `exitConflict_target` | 12/15 | 80% | 3 | Removed child type marker |
| T2-09 | `exitSql_statement` | 18/21 | 85.7% | 3 | Invalid child cardinality |
| T2-10 | `exitSet_operation_member` | 24/27 | 88.9% | 3 | Missing / unexpected collector shape |
| T2-12 | `exitUnpivot_clause` | 36/38 | 94.7% | 2 | Tier 3; alias-only / non-map guards |

Already Tier 3 before this work, unchanged and outside the remaining scope:

| Original ID | Method | Lines (cov/total) | % | Missed |
|---|---|---|---|---:|
| T2-08 | `exitTable_argument_literal` | 13/15 | 86.7% | 2 |
| T2-11 | `exitJinja_function_call` | 31/33 | 93.9% | 2 |

### Focused witnesses

Each new method contains one input, all six complete extractor goldens,
zero syntax errors and an exact diagnostic list (including positions and
metadata where present). Existing tests and golden values were preserved.

| Test | Reached path |
|------|--------------|
| [unaryPlusPredicandEndpointEntry11Test](../../src/test/java/sql/walker/SqlEventWalkerNonSqlEndpointParserTests.java#L3071) | T2-01 unary-plus identity at the PREDICAND endpoint, with arithmetic and a function argument |
| [qualifiedVariableJsonSuffixEntry11Test](../../src/test/java/sql/walker/SqlEventWalkerBareValueExpressionTests.java#L673) | T2-03 column JSON suffix and non-function map collection, using a qualified column variable |
| [orderedAggregateEmptyArgumentsCompatibilityEntry11Test](../../src/test/java/sql/walker/SqlEventWalkerWithinGroupOrderedAggregateTests.java#L634) | T2-04 empty argument list with mandatory WITHIN GROUP |
| [unpivotQualifiedColumnVariableEntry11Test](../../src/test/java/sql/walker/SqlEventWalkerPivotUnpivotTests.java#L6951) | T2-02 qualified-variable token fallback; T2-12 first-item recognition by table reference |
| [unpivotLambdaReferenceCollectorEntry11Test](../../src/test/java/sql/walker/SqlEventWalkerPivotUnpivotTests.java#L6987) | T2-02 raw lambda-reference map instead of the usual column wrapper |

The empty ordered-aggregate and nested lambda/UNPIVOT cases characterize
existing parser/collector compatibility, not native database execution.
The lambda witness keeps an ordinary column first in the IN list; it does
not assert support for a lambda-only first item. The qualified JSON case
preserves the current numeric AST wrapper rather than changing output shape.

### Source-backed residual dispositions

These are narrow valid-input reachability decisions, not assertions that
error recovery can never produce incomplete collectors. Defensive guards
and legacy fallbacks remain unchanged; no fabricated maps, coverage
exclusions or production deletions were used to raise percentages.

The shared collector invariant is established by
[enterEveryRule / exitEveryRule](../../src/main/java/sql/walker/SqlParseEventWalker.java#L1884)
and [addToParent](../../src/main/java/astwalkers/AbstractASTWalkerHelper.java#L689):
nonterminal children occupy numeric slots; terminals such as semicolons
are not promoted as independent children. The generated base listener's
`visitTerminal` is a no-op.

| Target | Remaining missed lines in `SqlParseEventWalker.java` | Producer / grammar proof |
|--------|-----------------------------------------------------|--------------------------|
| T2-01 | 9110–9113, 9115 | [exitPredicand_subquery](../../src/main/java/sql/walker/SqlParseEventWalker.java#L11148) wraps query-backed references in `lookup` before promotion. [isQueryBackedSelectItemReference](../../src/main/java/sql/symboltree/SqlParseSymbolTreeHelper.java#L11032) explicitly includes `select`. Other [predicand alternatives](../../src/main/antlr4/sql/SQLSelectParser.g4#L1216) produce their own expression wrappers, not a bare `select`. Existing scalar SELECT/WHERE witnesses in [SubqueryExitT2_6Tests](../../src/test/java/sql/walker/SqlEventWalkerSubqueryExitT2_6Tests.java#L29) retain the lookup shapes. |
| T2-02 | 8140–8142 | [column_reference](../../src/main/antlr4/sql/SQLSelectParser.g4#L1199) is mandatory. [exitColumn_reference](../../src/main/java/sql/walker/SqlParseEventWalker.java#L8828) emits a column map, or [collectLambdaReference](../../src/main/java/sql/walker/SqlParseEventWalker.java#L8981) emits a raw lambda map; neither emits a scalar. The raw-map alternative is now tested, not waived. |
| T2-03 | 9151, 9207 | [Primary/suffix grammar](../../src/main/antlr4/sql/SQLSelectParser.g4#L1228), numeric collector promotion and [suffix producers](../../src/main/java/sql/walker/SqlParseEventWalker.java#L9213) leave only numeric suffix keys after the rule marker is removed. Core producers emit maps, including [literal normalization](../../src/main/java/sql/walker/SqlParseEventWalker.java#L12713), [NULL](../../src/main/java/sql/walker/SqlParseEventWalker.java#L12829), columns, substitutions and composite expressions; suffix chaining preserves or wraps those maps. The catch and primitive fallback are defensive. |
| T2-04 | 9877–9878 | [ordered_aggregate_call](../../src/main/antlr4/sql/SQLSelectParser.g4#L1666) requires WITHIN GROUP, whose [exit producer](../../src/main/java/sql/walker/SqlParseEventWalker.java#L9893) always adds a map. Child 2 therefore exists with or without arguments; both valid shapes are covered. |
| T2-05 | 12427, 12455, 12482 | [value_expression](../../src/main/antlr4/sql/SQLSelectParser.g4#L1687) has exactly one expression child; [row expressions](../../src/main/antlr4/sql/SQLSelectParser.g4#L1985) also promote map-valued primaries. Literal/NULL/substitution/composite normalization and [single-child promotion](../../src/main/java/astwalkers/AbstractASTWalkerHelper.java#L557) preserve the map shape. The residuals guard non-map or non-singleton collectors, **not** the nested substitution-name branch, which is covered. |
| T2-06 | 4655–4658 | The [select_item grammar](../../src/main/antlr4/sql/SQLSelectParser.g4#L779) supplies a value expression or all-columns item. Scalar queries pass through the same eager lookup producer as T2-01; adding an output alias cannot restore a top-level `select`. Existing aliased scalar witnesses cover this output contract. |
| T2-07 | 3837–3839 | [conflict_target](../../src/main/antlr4/sql/SQLSelectParser.g4#L575) only accepts a column-reference list. [exitColumn_reference_list](../../src/main/java/sql/walker/SqlParseEventWalker.java#L8820) unconditionally removes its rule-type marker before promotion, so typed-child flattening cannot fire. Existing ON CONFLICT tests in [DmlUpdateInsertDeleteTruncateTests](../../src/test/java/sql/walker/SqlEventWalkerDmlUpdateInsertDeleteTruncateTests.java#L4340) exercise the real shape. |
| T2-09 | 2061–2062, 2064 | [sql_statement](../../src/main/antlr4/sql/SQLSelectParser.g4#L152) has one statement child and an optional terminal semicolon. Numeric promotion and no-op terminal visitation leave exactly that one non-null child. Existing [mixedScriptStatementTypesTest](../../src/test/java/sql/walker/SqlEventWalkerScriptsAndDDLTests.java#L124) exercises all statement kinds with semicolons. |
| T2-10 | 4404, 4438–4439 | [set_operation_member](../../src/main/antlr4/sql/SQLSelectParser.g4#L721) has one source and optionally an alias. Entry creates its collector; after removing the source, only the empty or single alias-map shapes are possible. Both return before the fallback. Existing [subquerySetOpMemberT2_6Test](../../src/test/java/sql/walker/SqlEventWalkerSubqueryExitT2_6Tests.java#L101) exercises the aliased shape. The null guard is retained. |
| T2-12 | 8014, 8020 | [UNPIVOT grammar](../../src/main/antlr4/sql/SQLSelectParser.g4#L901) supplies map-valued name/value columns, optional null policy and an IN-list map. Named operands have `name`; qualified variables have `table_ref` (now tested). The raw lambda map has neither, and optional IN labels use `label`, not an alias-only operand. No valid producer supplies an alias-only first item or a scalar clause child. The non-map fallback only appends to the unused local `nameSlots` list. |

### Validation and provenance

- T023 full default baseline: **4,306 reported, 4,244 passed, 62 skipped,
  zero failures/errors**, completed 2026-11-19 09:18:53 -05:00.
- Focused Maven executions with `-Pisolated-build -Djacoco.append=true`,
  each using `test jacoco:report`: three hosts **249 passed** at 09:54:14;
  corrected suffix host plus endpoint host **166 passed** at 09:57:36.
  Both had zero failures, errors or skips.
- The final four-class cohort comprises **393 distinct current tests**:
  BareValueExpression 22, WithinGroupOrderedAggregate 22, PivotUnpivot 205,
  NonSqlEndpointParser 144. The invocations overlap; 415 is not a unique-test
  total. The initial bare-value witness was replaced after measuring its
  actual path, rather than counting a successful parse as proof of coverage.
- JaCoCo analyzed **544 classes**. Agent arguments explicitly used
  `append=true`; the saved full-run execution remains an exact byte prefix
  of the updated execution file, and the walker class SHA-256 is unchanged.
  No production, grammar or lexer changes were made.
- [Current JaCoCo XML](../../target-maven/site/jacoco/jacoco.xml) is the
  baseline-plus-focused report. This closes only the approved Entry 11
  coverage package, not the later release-tests/corpus/JAR certification.

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

