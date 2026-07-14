# Phase 10 — Substitution Variable Quality Gate Inventory
**Status:** Complete (Jul 2026) — Current consolidation gate is green; this inventory is now historical reference

---

## Quick Reference: Test Coverage by Variable Type

### ✅ Column Substitution Variables (type=column) — ~40+ Tests
**Status:** ✅ PASSING

**Passing:**
- `SqlEventWalkerCoreSelectFromAliasingTests::simpleVariableName1Test`
- `SqlEventWalkerCoreSelectFromAliasingTests::simpleVariableNameWithDotTest`
- `SqlEventWalkerCoreSelectFromAliasingTests::simpleVariableNameWithDashTest`
- `SqlEventWalkerCoreSelectFromAliasingTests::getSimpleColumnVariableTest`

**Historical blockers (now green):**
- `getSubstitutionColumnVariableV9CteWrappedWhereVariantWithJoinOnSelectColumnTest` through `V16CteWrappedSelfIntersectionVariantWithJoinOnSelectColumnTest` (8 tests)
- Root cause was external CTE tokens migrating from table_dictionary → query_dictionary; def_queryN wrapper nesting

**Historical blockers (now green):**
- `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests::insertComplexSubstitutionI1WithCteGroupByHaving` through `I10` (10 tests)
- `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests::updateComplexSubstitutionU1WithCteGroupByHaving` through `U10` (10 tests)
- Root cause was query dictionary shape regression; assignment column token position tracking

**Extended Variable Tests (18+ tests):**
- Extended name variants: dots, dashes, population qualifiers, entity names (all extensions)

**Action:** Review Phase 7 backlog (`updateComplexSubstitutionU*` golden expectations); fix CTE def_queryN wrapping for V9-V16

---

### ✅ Predicand Substitution Variables (type=predicand) — ~25+ Tests
**Status:** ✅ PASSING

**Coverage:**
- `SqlEventWalkerFunctionsAggregatesWindowingTests` — 4 tests (CASE expressions, aggregate contexts)
- `SqlEventWalkerNonSqlEndpointParserTests` — 13+ tests (CASE predicand positions 1-7; window functions)
- `SqlEventWalkerPredicatesOperatorsSubstitutionsTests` — 6+ tests (comparisons, NULL checks, ORDER BY)
  - now-green probes: `selectListWithSubstitutions`, `withQueryFromNavigateV2StudentSubstitution`, `whereConditionComparingPredicandVariablesTest`, `whereConditionComparingPredicandVariableToNullTest`, `whereConditionComparingPredicandVariableToNotNullTest`
- `SqlEventWalkerCastingAndTypesTests` — 2 tests (casting with variables)
- `SqlEventWalkerLiveSampleQueriesTests` — 1 test (complex predicand extraction)

**Action:** Continue baseline monitoring; no known blockers

---

### ✅ Condition Substitution Variables (type=condition) — ~13+ Tests
**Status:** ✅ PASSING

**Coverage:**
- `SqlEventWalkerPredicatesOperatorsSubstitutionsTests` — 7+ tests (WHERE, HAVING, QUALIFY conditions)
  - now-green probes: `whereConditionWithSingleConditionVariableTest`, `whereConditionWithSingleColumnVariableTest`, `withQueryFromNavigateV2StudentSubstitution`
- `SqlEventWalkerNonSqlEndpointParserTests` — 6+ tests (nested condition substitution in CASE/queries)

**Action:** Continue baseline monitoring; stable

---

### ✅ Tuple Substitution Variables (type=tuple) — ~20+ Tests
**Status:** ✅ PASSING

**Coverage:**
- `SqlEventWalkerNonSqlEndpointParserTests` — 18+ tests
  - `tupleSubstitutionVariableTestV1/V2`
  - `basicTupleSubstitutionVariableTest`
  - `complexTupleSubstitutionVariableTestExtended*` (3 tests)
  - `complexTupleSubstitutionVariableTestExtendedWithPopulation*` (2 tests)
  - `complexTupleSubstitutionVariableTestAlgorithm*` (4+ tests)
  - Name population/qualifier variants

**Action:** Continue baseline monitoring; comprehensive coverage, stable

---

### ✅ In_List Substitution Variables (type=in_list) — ~10+ Tests
**Status:** ✅ PASSING

**Passing:**
- `SqlEventWalkerPredicatesOperatorsSubstitutionsTests` — 6 tests (basic IN/NOT IN predicates)
- `SqlEventWalkerNonSqlEndpointParserTests` — 2 tests (embedded IN-list)

**Action:** Continue monitoring; stable

---

### ✅ Join_Extension Substitution Variables (type=join_extension) — ~7+ Tests
**Status:** ✅ PASSING

**Coverage:**
- `SqlEventWalkerNonSqlEndpointParserTests` — 4 tests
  - `joinExtensionFullOuterJoinWithOnOnConditionVariableTest`
  - `joinExtensionJoinWithOnConditionVariableInParenthesisTest`
  - `joinExtensionJoinWithOnTwoConditionVariablesTest`
  - `joinExtensionJoinWithConditionAndJoinExtensionVariablesTest`
- `SqlEventWalkerJoinsAndTableResolutionTests` — 3 tests
  - `basicJoinWithOnOnConditionVariableTest`
  - `basicJoinWithOnConditionVariableInParenthesisTest`
  - `basicJoinWithOnTwoConditionVariablesTest`

**Action:** Continue baseline monitoring; stable

---

## Summary Statistics

| Variable Type | Tests | Status | Pass Rate | Blocker Items |
|---------------|-------|--------|-----------|---------------|
| **Column** | ~40+ | ✅ PASS | 100% | — |
| **Predicand** | ~25+ | ✅ PASS | 100% | — |
| **Condition** | ~13+ | ✅ PASS | 100% | — |
| **Tuple** | ~20+ | ✅ PASS | 100% | — |
| **In_List** | ~10+ | ✅ PASS | 100% | — |
| **Join_Extension** | ~7+ | ✅ PASS | 100% | — |
| **TOTAL** | **~126** | ✅ 100% | 100% | 0 blockers |

---

## Phase 10 Completion Gate

### Prerequisites (before Phase 11 start)

At this point, the Phase 10 blocker set has been cleared. The list below is retained only as the historical record of what was updated during the phase.

1. ✅ **Predicand, Condition, Tuple, Join_Extension tests** — Passing
2. ✅ **In_List tests** — Passing
3. ✅ **Column tests** — Passing

### Gate Command

```bash
# Run the full quality gate including substitution variable tests
cd parse
mvn -Psymbol-table-resolution-consolidation test

# OR run specific categories:
mvn -Dtest=SqlEventWalkerCoreSelectFromAliasingTests#getSubstitutionColumnVariable* test
mvn -Dtest=SqlEventWalkerDmlUpdateInsertDeleteTruncateTests#*ComplexSubstitution* test
mvn -Dtest=SqlEventWalkerPredicatesOperatorsSubstitutionsTests#*Variable* test
```

### Success Criteria

- **126 tests with substitution variables:** ALL PASSING
- **No regressions** in Predicand, Condition, Tuple, Join_Extension categories
- **Column substitution:** CTE wrapping and complex DML resolved
- **In_List substitution:** Stable

---

## Next Actions

### Immediate (This Session)

- [ ] Keep the 126/126 consolidation gate green as follow-on work lands
- [ ] Treat this inventory as the historical record of the phase 10 blocker set

### Short-term (Before Phase 11)

- [ ] Proceed with Phase 11 work only after confirming the gate remains green

### Long-term (Phase 11 start)

- Maintain the 126/126 gate as the continuous verification checkpoint during later phases

---

## Related Documentation

- **Primary Plan:** [symbol-table-resolution-consolidation-worklist.md](parse/documents/symbol-table-resolution-consolidation-worklist.md)
- **Phase 7 Backlog:** See "Phase 7 golden backlog" section in main plan
- **Query Dictionary Design:** [table-and-query-dictionary-design.md](parse/documents/table-and-query-dictionary-design.md)

---

**Last Updated:** 2026-07-14 (Phase 10 complete; current gate 126/126 passing)
