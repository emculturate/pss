# Phase 17.7.7 — Pivot/unpivot matrix heatmap

**Status:** ✅ **Signed off Aug 2026** (gap-fill batch complete; optional cells deferred — see worklist §17.7.7-gap-fill).  
**Class:** `SqlEventWalkerPivotUnpivotTests` (**142** methods, **142/142** green).  
**Catalog:** `phase-17.7.7-pivot-test-catalog.md` (per-method subset **A–E**).  
**Spec:** `symbol-table-resolution-consolidation-worklist.md` §17.7.7-matrix  

**Tag convention** (gap-fill / gate tests):

```text
Matrix: subset=E | topo=S3 | bucket=GROUP_BY,HAVING | kind=derived | outcome=happy
```

## Topology × clause bucket (derived / source column refs)

Legend: **●** covered in class (happy and/or unhappy), **◐** gate / adjacent topology only, **○** deferred (not required for 17.7.7 sign-off).

| Topo \\ Bucket | SELECT | WHERE | GROUP BY | HAVING | ORDER BY | JOIN ON | QUALIFY |
|----------------|--------|-------|----------|--------|----------|---------|---------|
| **S1-P** | ● | ● | ● | ● | ● | ● | ● |
| **S1-U** | ● | ● | ● | ● | ● | ● | ● |
| **S2-PP** | ● | ◐ | ● | ● | ● | ◐ | ○ |
| **S2-UU** | ● | ◐ | ◐ | ◐ | ◐ | ● | ○ |
| **S2-PU** | ● | ● | ● | ● | ● | ● | ● |
| **S3** | ● | ● | ● | ● | ● | ● | ○ |

## Gap-fill inventory (`gapFill17_7_7_*` — 11 tests)

| Test | Topo | Bucket | Outcome |
|------|------|--------|---------|
| `gapFill17_7_7_S3PivotUnpivotPivotGroupByHavingQualifiedDerivedV1Test` | S3 | GROUP BY, HAVING, ORDER BY | happy (+ SELECT severe warnings) |
| `gapFill17_7_7_S3UnpivotPivotUnpivotGroupByAmbiguousDerivedFatalV1Test` | S3 | GROUP BY | derived fatal |
| `gapFill17_7_7_S3UnpivotPivotUnpivotOrderByAmbiguousDerivedMonthNameFatalV1Test` | S3 | ORDER BY | derived fatal |
| `gapFill17_7_7_S3PivotUnpivotPivotHavingAmbiguousDerivedFebSalesSumFatalV1Test` | S3 | HAVING | derived fatal |
| `gapFill17_7_7_S3PivotUnpivotPivotOrderByAmbiguousSourceSalesAmountSevereV1Test` | S3 | ORDER BY | source SEVERE |
| `gapFill17_7_7_S3PivotUnpivotPivotJoinOnQualifiedDerivedHappyV1Test` | S3 | JOIN ON | happy |
| `gapFill17_7_7_S2PuPivotUnpivotJoinClauseEgressDerivedV1Test` | S2-PU | WHERE, GROUP BY, HAVING, ORDER BY | happy |
| `gapFill17_7_7_S2PuQualifyDerivedQualifiedHappyV1Test` | S2-PU | QUALIFY | happy |
| `gapFill17_7_7_S2PpDualPivotGroupByAmbiguousDerivedJanSalesSumFatalV1Test` | S2-PP | GROUP BY | derived fatal |
| `gapFill17_7_7_S2PpDualPivotGroupByHavingQualifiedDerivedHappyV1Test` | S2-PP | GROUP BY, HAVING | happy |
| `gapFill17_7_7_S2PpDualPivotOrderByAmbiguousSourceMonthNameSevereV1Test` | S2-PP | ORDER BY | source SEVERE |

All gap-fill tests use **full golden** asserts (AST, interface, substitutions, table/query dictionaries, symbol tree) plus **position-based diagnostics** where applicable. Queries: `pivot_unpivot_queries.properties`; refresh: `parse/tools/refresh_pivot_unpivot_goldens.py`.

## Deferred (post–17.7.7)

| Gap | Reason |
|-----|--------|
| **§17.7.7-gap-fill table** ⏸️ rows | Optional only — UPDATE RHS + derived-vs-same-named-physical unhappy pairs; explained in worklist §17.7.7-gap-fill |
| **§17.7.7-deferred-large-sample-goldens** | `largeStudentgeneralQueryParse*` still `@Ignore` |
| S3 × QUALIFY, S2-UU systematic clause matrix | Low priority vs gate + gap-fill |

## Related gate tests

- `triplePivotUnpivotPivotJoinDerivedColumnsV1Test`, `tripleUnpivotPivotUnpivotJoinDerivedColumnsV1Test`
- `pivotDerivedAmbiguousConvertEgressPhaseParityOneVsTwoSelectRefsTest` (S2-PP SELECT)
- `closeout17_7_8_*` (physical/subquery × PIVOT/UNPIVOT — full goldens on tests 1–4)
