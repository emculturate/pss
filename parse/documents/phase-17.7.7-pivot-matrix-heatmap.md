# Phase 17.7.7 — Pivot/unpivot matrix heatmap (living)

**Class:** `SqlEventWalkerPivotUnpivotTests`  
**Spec:** `symbol-table-resolution-consolidation-worklist.md` §17.7.7-matrix  
**Tag convention** (on new / gap-fill tests):

```text
Matrix: subset=E | topo=S3 | bucket=GROUP_BY,HAVING | kind=derived | outcome=happy
```

## Topology × clause bucket (derived column refs)

Legend: **●** covered (happy and/or unhappy in class), **◐** partial (other topology or diagnostic only), **○** empty / weak.

| Topo \\ Bucket | SELECT | WHERE | GROUP BY | HAVING | ORDER BY | JOIN ON |
|----------------|--------|-------|----------|--------|----------|---------|
| **S1-P** | ● | ● | ● | ● | ● | ● |
| **S1-U** | ● | ● | ● | ● | ● | ● |
| **S2-PP** | ● | ◐ | ◐ | ◐ | ◐ | ● |
| **S2-UU** | ● | ◐ | ◐ | ◐ | ◐ | ● |
| **S2-PU** | ● | ● | ● | ● | ● | ● |
| **S3** | ● | ● | ● | ● | ● | ● |

**Notes**

- **S1** clause coverage is mostly subset **B** (`*GroupOrder*`, `*HavingOrder*`, `*JoinOn*`, …).
- **S3** SELECT/WHERE/ORDER BY/JOIN ON: gate tests `triplePivotUnpivotPivotJoinDerivedColumnsV1Test`, `tripleUnpivotPivotUnpivotJoinDerivedColumnsV1Test`.
- **S3** GROUP BY / HAVING (qualified happy + GROUP BY derived fatal): `gapFill17_7_7_S3PivotUnpivotPivotGroupByHavingQualifiedDerivedV1Test`, `gapFill17_7_7_S3UnpivotPivotUnpivotGroupByAmbiguousDerivedFatalV1Test` (Aug 2026).
- **S2-PU** systematic clause egress: `gapFill17_7_7_S2PuPivotUnpivotJoinClauseEgressDerivedV1Test` (Aug 2026); complements `pivotDerivedAmbiguousConvertEgressPhaseParityOneVsTwoSelectRefsTest` (S2-PP SELECT parity).

## Outcome × kind (multi-modifier only)

| | Derived happy (qualified) | Derived unhappy (unqualified) | Source ambiguity |
|--|---------------------------|-------------------------------|------------------|
| **S2-PU** | ● clause egress test | ○ | ◐ (triple gate) |
| **S3** | ● GROUP BY/HAVING | ● GROUP BY `sales_amount` | ● gate tests |

## Incremental tagging

Full per-method tags for all **109** tests are **not** required for gap-fill; subset **A–E** in the worklist remains the catalog. New tests and gate tests carry explicit `Matrix:` headers; extend this heatmap when filling another cell.
