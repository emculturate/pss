# Tests to skip during INSERT / VALUES refactor

Use this list when running the suite during incremental INSERT/VALUES work. Failures in these tests are **known and unrelated** to the refactor unless we are explicitly working on PIVOT or donor-email alias resolution.

**Last verified:** full `mvn test` — 16 failures, all accounted for below (1013 effective pass + 10 new `insertValues*` tests).

---

## 1. PIVOT AST (incomplete walker) — skip entire class

**Class:** `sql.walker.SqlEventWalkerPivotUnpivotTests`

**Why:** Goldens expect a flat PIVOT AST; walker currently emits nested `column` / `Type=110` shapes. UNPIVOT tests in this class still pass today, but the class is treated as PIVOT work-in-progress.

**Failing methods (15)** — all `Assert.assertEquals("AST is wrong", …)`:

| Method |
|--------|
| `pivotV1Test` |
| `pivotBasicMetricColumnsV1Test` |
| `pivotTableWithInAliasesJanFebMarV2Test` |
| `pivotTableWithGroupByAndOrderByV2GroupOrderTest` |
| `pivotTableWithHavingAndOrderByV2HavingOrderTest` |
| `pivotTableJoinOnWithUnqualifiedJanSalesProbeTest` |
| `pivotTableWithQualifyJanSalesProbeTest` |
| `pivotTableWithOrderByExpressionJanFebProbeTest` |
| `pivotFromDerivedAdjustedColumnsV3Test` |
| `pivotWithTaxAndWhereV4Test` |
| `pivotJoinTargetsWithFilterV5Test` |
| `pivotKeepingForColumnV6Test` |
| `pivotBasicMonthSalesV7Test` |
| `pivotBasicMonthSalesJoinV8Test` |
| `generatorDirectFromListTupleEndpointNakedSyntaxBuildsSameAstShapeTest` |

**Passing in same class (13 UNPIVOT)** — optional to run during refactor: `unpivot*` methods.

---

## 2. Donor email live sample — skip one method

**Class:** `sql.walker.SqlEventWalkerLiveSampleQueriesTests`  
**Method:** `donorEmailWithInvalidFatalErrorOnQualifiedColumnVariableTest`

**Why:** Expects no ERROR diagnostics; walker still reports  
`Unresolved unqualified column reference(s): [source_partner_system_name]` for a SELECT-list alias used in `PARTITION BY`. Fails on clean `HEAD` as well (not an INSERT/VALUES regression). See TODO in test around alias resolution in the current select list.

---

## Quick Maven runs

From `parse/`:

```bash
# All tests except PIVOT class (expect 1 failure in LiveSample if that class runs)
mvn test -Dtest='!SqlEventWalkerPivotUnpivotTests'

# Walker tests only, excluding PIVOT class
mvn test -Dtest='sql.walker.!SqlEventWalkerPivotUnpivotTests'
```

There is no portable Surefire `-Dtest` syntax to exclude a single method. For a green refactor run without touching `@Ignore`:

1. Use the command above (skip PIVOT class), **or**
2. Run full `mvn test` and treat **exactly 16 failures** (15 PIVOT + 1 donor email) as expected.

**Target for INSERT/VALUES refactor:** `Failures: 16` (or `Failures: 1` if PIVOT class excluded), never more without investigation.

---

## In scope for INSERT/VALUES refactor (should stay green)

- `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` (including `insertValues*`)
- `SqlEventWalkerNonSqlEndpointParserTests` (`valuesStatement*`, etc.)
- `SqlEventWalkerSubqueriesAndClauseSemanticsTests` (VALUES CTEs)
- `SqlParseEventWalkerWithAccessObjectTest` (`basicInsert*`, VALUES-from-access)
- All other walker / access / CLI test classes
