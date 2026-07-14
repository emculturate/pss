# Phase 10 — Substitution Variable Quality Gate Inventory
**Status:** New Phase (Jul 2026) — Inserted between Phase 9 and former Phase 10 (now Phase 11)

---

## Quick Reference: Test Coverage by Variable Type

### ✅ Column Substitution Variables (type=column) — ~40+ Tests
**Status:** ⚠️ PARTIAL — CTE-wrapped tests & complex DML failing

**Passing:**
- `SqlEventWalkerCoreSelectFromAliasingTests::simpleVariableName1Test`
- `SqlEventWalkerCoreSelectFromAliasingTests::simpleVariableNameWithDotTest`
- `SqlEventWalkerCoreSelectFromAliasingTests::simpleVariableNameWithDashTest`
- `SqlEventWalkerCoreSelectFromAliasingTests::getSimpleColumnVariableTest`

**Failing (CTE-wrapped):**
- `getSubstitutionColumnVariableV9CteWrappedWhereVariantWithJoinOnSelectColumnTest` through `V16CteWrappedSelfIntersectionVariantWithJoinOnSelectColumnTest` (8 tests)
- Root cause: External CTE tokens need migration from table_dictionary → query_dictionary; def_queryN wrapper nesting

**Failing (Complex DML):**
- `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests::insertComplexSubstitutionI1WithCteGroupByHaving` through `I10` (10 tests)
- `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests::updateComplexSubstitutionU1WithCteGroupByHaving` through `U10` (10 tests)
- Root cause: Query dictionary shape regression; assignment column token position tracking broken

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
| **Column** | ~40+ | ⚠️ PARTIAL | ~50% | CTE V9-V16 wrapping; INSERT/UPDATE I/U series |
| **Predicand** | ~25+ | ✅ PASS | 100% | — |
| **Condition** | ~13+ | ✅ PASS | 100% | — |
| **Tuple** | ~20+ | ✅ PASS | 100% | — |
| **In_List** | ~10+ | ✅ PASS | 100% | — |
| **Join_Extension** | ~7+ | ✅ PASS | 100% | — |
| **TOTAL** | **~115+** | ⚠️ ~76% | ~76% | **28 exact blockers** |

---

## Phase 10 Completion Gate

### Prerequisites (before Phase 11 start)

At this point, the remaining Phase 10 blockers are exactly 28 tests: Column V9-V16, INSERT I1-I10, and UPDATE U1-U10.

1. ✅ **Predicand, Condition, Tuple, Join_Extension tests** — All passing (no changes needed)
2. ✅ **In_List tests** — All passing
3. ⚠️ **Column tests** — Fix CTE-wrapped V9-V16 and complex INSERT/UPDATE I/U series

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

- **115+ tests with substitution variables:** ALL PASSING
- **No regressions** in Predicand, Condition, Tuple, Join_Extension categories
- **Column substitution:** CTE wrapping and complex DML resolved
- **In_List substitution:** Stable

---

## Next Actions

### Immediate (This Session)

- [ ] Review Phase 10 section in `symbol-table-resolution-consolidation-worklist.md`
- [ ] Inventory confirmed: 115+ substitution variable tests catalogued by type
- [ ] Current baseline: ~76% passing, 5 types fully passing, 1 type partially passing, 28 exact blockers remaining

### Short-term (Before Phase 11)

- [ ] Review Phase 7 backlog for Column substitution failures (V9-V16, INSERT/UPDATE)
- [ ] Fix CTE def_queryN wrapping for external tuple token materialization
- [ ] Fix INSERT/UPDATE I/U series query-dict assignment token tracking
- [ ] Rerun Phase 10 quality gate → target 100% (115+/115)

### Long-term (Phase 11 start)

- Once Phase 10 reaches 100% pass rate, proceed to Phase 11 (`context_list` resolution)
- Maintain Phase 10 gate as continuous verification checkpoint during later phases

---

## Related Documentation

- **Primary Plan:** [symbol-table-resolution-consolidation-worklist.md](parse/documents/symbol-table-resolution-consolidation-worklist.md)
- **Phase 7 Backlog:** See "Phase 7 golden backlog" section in main plan
- **Query Dictionary Design:** [table-and-query-dictionary-design.md](parse/documents/table-and-query-dictionary-design.md)

---

**Last Updated:** 2026-07-13 (Phase 10 inserted; comprehensive inventory created)
