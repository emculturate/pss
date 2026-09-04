# Quick Reference: Substitution Variable Coverage Matrix

## Heat Map: Coverage by Variable Type × Context

```
                    SELECT  FROM   WHERE  GROUP  HAVING QUALIFY ORDERBY  JOIN  WINDOW  FUNC   IN     CAST   UPDATE
                    ------  ----   -----  -----  ------ ------- -------  ----  ------  ----   --     ----   ------

COLUMN              ✅      ❌     ✅     ❌     ❌     ❌      ❌       ✅    ❌      ✅     ❌     ⚠️     ✅
  Test Count       (12)    (0)    (1)    (0)    (0)    (0)     (0)      (1)   (0)     (2)    (0)    (1)    (8)
  Status           PASS    NONE   PASS   MISS   MISS   MISS    MISS     PART  MISS    PASS   MISS   PART   REGR

PREDICAND          ✅      ❌     ✅     ❌     ❌     ❌      ❌       ⚠️    ⚠️      ✅     ❌     ⚠️     ✅
  Test Count       (7)     (0)    (1)    (0)    (0)    (0)     (0)      (1)   (2)     (2)    (0)    (1)    (0)
  Status           PASS    NONE   PASS   MISS   MISS   MISS    MISS     PART  PART    PASS   MISS   PART   NONE

CONDITION          ❌      ❌     ✅     ❌     ❌     ❌      ❌       ✅    ❌      ❌     ❌     ❌     ❌
  Test Count       (0)     (0)    (2)    (0)    (0)    (0)     (0)      (8)   (0)     (0)    (0)    (0)    (0)
  Status           MISS    NONE   PASS   MISS   MISS   MISS    MISS     PASS  MISS    MISS   MISS   MISS   MISS

TUPLE               ⚠️      ✅     ❌     ❌     ❌     ❌      ❌       ✅    ❌      ❌     ❌     ❌     ❌
  Test Count       (3)     (9)    (0)    (0)    (0)    (0)     (0)      (1)   (0)     (0)    (0)    (0)    (0)
  Status           PART    PASS   NONE   MISS   MISS   MISS    MISS     PASS  MISS    MISS   MISS   MISS   MISS

IN_LIST            ❌      ❌     ✅     ❌     ❌     ❌      ❌       ❌    ❌      ❌     ✅     ❌     ❌
  Test Count       (0)     (0)    (3)    (0)    (0)    (0)     (0)      (0)   (0)     (0)    (3)    (0)    (0)
  Status           NONE    NONE   PART   MISS   MISS   MISS    MISS     NONE  MISS    MISS   PART   MISS   MISS

JOIN_EXT           ❌      ❌     ❌     ❌     ❌     ❌      ❌       ✅    ❌      ❌     ❌     ❌     ❌
  Test Count       (0)     (0)    (0)    (0)    (0)    (0)     (0)      (7)   (0)     (0)    (0)    (0)    (0)
  Status           NONE    NONE   NONE   MISS   MISS   MISS    MISS     PASS  MISS    MISS   MISS   MISS   MISS

─────────────────────────────────────────────────────────────────────────────────────────────────────────────────
Legend:
  ✅ = Covered & Passing
  ⚠️  = Partially Covered / Mixed Status
  ❌  = Missing or Failing
  (N) = Number of tests in that category
```

## Status Summary by Variable Type

| Type | Contexts Covered | Pass Rate | Critical Issues | Recommendation |
|------|--|--|--|--|
| **COLUMN** | 4/12 | ~50% | V9-V16 CTE wrapping; INSERT/UPDATE I/U series | Fix regressions; add GROUP/ORDER/QUALIFY/WINDOW |
| **PREDICAND** | 2/11 | ~75% | Minimal failures | Add GROUP/ORDER/HAVING/QUALIFY/WINDOW tests |
| **CONDITION** | 2/4 | ~80% | Missing HAVING/QUALIFY | Add HAVING & QUALIFY tests (2-3 ea) |
| **TUPLE** | 2/4 | ~90% | Minor gaps in DML contexts | Add INSERT/SET-OP tests |
| **IN_LIST** | 1/2 | ~67% | Goldens pending; missing LIKE ANY | Apply 3 goldens; add LIKE ANY (1-2) |
| **JOIN_EXT** | 1/1 | ~100% | Well covered | Add CROSS JOIN variant (1) |

---

## Test Addition Priority & Effort Matrix

### Phase 10 Priority 1: Immediate (Quick wins, 15-20 tests)

```
EASY (1-2 hrs each):
  □ Group By with Column Variable (✅ grammar rules in place)
  □ Order By with Column Variable (✅ grammar rules in place)
  □ Group By with Predicand Variable (✅ grammar rules in place)
  □ Order By with Predicand Variable (✅ grammar rules in place)

MEDIUM (2-3 hrs each):
  □ Having with Condition Variable (⚠️  aggregate context)
  □ Having with Predicand Variable (⚠️  aggregate context)
  □ Qualify with Condition Variable (⚠️  Snowflake-specific)
  □ Qualify with Predicand Variable (⚠️  Snowflake-specific)
  □ Having with Column Variable (✅ grammar rules in place)

Total: 9 tests, ~15-20 hours
Status: **READY TO IMPLEMENT**
```

### Phase 10 Priority 2: Window Functions (8-12 tests)

```
MEDIUM (2-3 hrs each):
  □ Window PARTITION BY with Column Variable
  □ Window PARTITION BY with Predicand Variable
  □ Window ORDER BY with Column Variable
  □ Window ORDER BY with Predicand Variable
  □ Multiple Window Functions with Variables

Total: 5+ tests, ~12-15 hours
Status: **READY TO IMPLEMENT** (can run in parallel with Priority 1)
```

### Phase 10 Priority 3: Extended Predicates (4-6 tests)

```
MEDIUM (2-3 hrs each):
  □ Between...And with Column Variables
  □ Between...And with Predicand Variables
  □ Like Any with In_List Variable
  □ In_List in Update WHERE Clause
  □ In_List in Delete WHERE Clause

Total: 5 tests, ~10-15 hours
Status: **LOWER PRIORITY** (can defer to Phase 11)
```

---

## Coverage Roadmap

### Current State (Phase 10 Start)
- **Tested combinations:** ~50-60 variable-type×context pairs
- **Untested combinations:** ~40-50 variable-type×context pairs
- **Coverage %:** ~55% of grammatically valid combinations
- **Passing tests:** ~70 tests (from 131 total)

### Phase 10 Target (with Priority 1+2)
- **Tested combinations:** ~90-100 variable-type×context pairs
- **Untested combinations:** ~10-20 variable-type×context pairs
- **Coverage %:** ~80-85% of grammatically valid combinations
- **Passing tests:** ~150+ tests (115 current + 30-40 new)

### Phase 10 Stretch Goal (Priority 1+2+3)
- **Tested combinations:** ~100-110 variable-type×context pairs
- **Untested combinations:** ~5-10 variable-type×context pairs
- **Coverage %:** ~90-95% of grammatically valid combinations
- **Passing tests:** ~160+ tests (115 current + 45-50 new)

---

## Quick Links & Commands

### View Coverage Matrix
```bash
cat /Users/ghowe/emculturate-pss/pss/SUBSTITUTION_VARIABLE_COVERAGE_MATRIX.md
```

### Run Substitution Variable Tests (Current)
```bash
cd /Users/ghowe/emculturate-pss/pss/parse
mvn -Psymbol-table-resolution-consolidation test
# OR specific classes:
mvn -Dtest=SqlEventWalkerCoreSelectFromAliasingTests test
mvn -Dtest=SqlEventWalkerPredicatesOperatorsSubstitutionsTests test
```

### Add New Test Template
1. Copy existing test method (e.g., `groupByWithColumnVariableTest`)
2. Modify query: add `<variable>` to new context
3. Update golden assertions (use `mvn test -X` output as baseline)
4. Verify with `mvn test -Dtest=YourTestClass#yourTestMethod`

---

## Key Statistics

| Metric | Value | Note |
|--------|-------|------|
| **Total variable types** | 6 | Column, Predicand, Condition, Tuple, In_List, Join_Extension |
| **Grammatically valid contexts** | 110-120 | ~12 contexts × 6 types (with overlaps) |
| **Currently tested combinations** | ~50-60 | ~45-50% coverage |
| **Currently missing combinations** | ~50-60 | ~45-50% gaps |
| **Passing tests** | ~70/131 | ~53% of tests passing |
| **Failing tests** | ~25/131 | ~19% failures (mostly regressions) |
| **Unknown/unanalyzed** | ~36/131 | ~27% not yet categorized |
| **Priority 1 recommendations** | 9-15 tests | Easy/quick to add |
| **Priority 2 recommendations** | 8-12 tests | Window function coverage |
| **Estimated completion time (all)** | 30-50 hours | Phase 10 gate expansion |

---

## Next Steps

1. ✅ **DONE:** Create comprehensive coverage matrix
2. ⏭️ **NOW:** 
   - [ ] Review matrix with team
   - [ ] Prioritize test additions (Priority 1 vs defer?)
   - [ ] Assign test creation tasks
3. **Phase 10 (1-2 weeks):**
   - [ ] Implement Priority 1 tests (GROUP BY, ORDER BY, HAVING, QUALIFY)
   - [ ] Optionally add Priority 2 tests (windows)
   - [ ] Fix column/in_list regressions
   - [ ] Achieve ~150+/165 tests PASSING
4. **Phase 11:**
   - [ ] Maintain matrix as living document
   - [ ] Add Priority 3 tests if time permits
   - [ ] Integrate context_list resolution work

