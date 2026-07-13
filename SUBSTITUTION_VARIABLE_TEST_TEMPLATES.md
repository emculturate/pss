# Substitution Variable Test Templates & Suggestions

## Test Creation Guidelines

All new tests should follow the established pattern from existing test classes:

```java
@Test
public void yourDescriptiveTestNameTest() {
    final String query = "YOUR QUERY WITH <variable>";
    
    final SQLSelectParserParser parser = parse(query);
    SqlParseEventWalker extractor = runParsertest(query, parser);
    assertNoWalkerDiagnostics(extractor);
    
    Assert.assertEquals("AST is wrong", "...", extractor.getAsTree().toString());
    Assert.assertEquals("Interface is wrong", "...", extractor.getInterface().toString());
    Assert.assertEquals("Substitution List is wrong", "...", extractor.getSubstitutionsMap().toString());
    Assert.assertEquals("Table Dictionary is wrong", "...", extractor.getTableColumnDictionaryMap().toString());
    Assert.assertEquals("Query Column Dictionary is wrong", "...", extractor.getQueryColumnDictionaryMap().toString());
    Assert.assertEquals("Symbol Table is wrong", "...", extractor.getSymbolTable().toString());
}
```

---

## Priority 1: Critical Clause Coverage (15-20 Tests)

### 1. GROUP BY with Column Variable (3-5 tests)

**Test: groupByWithColumnVariableSimpleTest**
```java
final String query = "SELECT <group_col>, count(*) "
    + "FROM tab1 "
    + "GROUP BY <group_col>";
```

**Context:** Column in GROUP BY (simplest form)
**Variable Type:** Column
**Expected Assertions:**
- AST: `groupby={group_col=<group_col>}` (as `row_value_predicand`)
- Symbol Table: `grouped_by=[{substitution={name=<group_col>, type=column}}]`
- Table Dictionary: `tab1={<group_col>=[...location...]}`

**Test: groupByWithColumnVariableQualifiedTest**
```java
final String query = "SELECT t.<group_col>, count(*) "
    + "FROM tab1 t "
    + "GROUP BY t.<group_col>";
```

**Context:** Qualified column in GROUP BY
**Variable Type:** Column
**Expected:** table_ref=t in grouped_by entry

**Test: groupByWithMultipleColumnVariablesTest**
```java
final String query = "SELECT <col1>, <col2>, count(*) "
    + "FROM tab1 "
    + "GROUP BY <col1>, <col2>";
```

**Context:** Multiple column variables in GROUP BY
**Variable Type:** Column
**Expected:** Multiple entries in grouped_by list

**Test: groupByWithColumnAndPredicandMixedTest**
```java
final String query = "SELECT <col1>, sum(<amount>) "
    + "FROM tab1 "
    + "GROUP BY <col1>, year(<date_col>)";
```

**Context:** Column and function predicand in GROUP BY
**Variable Type:** Column + Predicand
**Expected:** Mixed grouped_by with column and function expression

**Test: groupByWithColumnVariableInHavingTest**
```java
final String query = "SELECT <group_col>, count(*) cnt "
    + "FROM tab1 "
    + "GROUP BY <group_col> "
    + "HAVING count(*) > 10";
```

**Context:** Column in GROUP BY, numeric condition in HAVING
**Variable Type:** Column (GROUP BY), no variable in HAVING
**Expected:** grouped_by has column var, filters unchanged

---

### 2. ORDER BY with Column Variable (3-5 tests)

**Test: orderByWithColumnVariableSimpleTest**
```java
final String query = "SELECT name FROM tab1 "
    + "ORDER BY <sort_col>";
```

**Context:** Column in ORDER BY (simplest form)
**Variable Type:** Column
**Expected Assertions:**
- AST: `orderby={sort_specifier={key={substitution={name=<sort_col>, type=column}}}}`
- Symbol Table: `ordered_by=[{substitution={name=<sort_col>, type=column}}]`

**Test: orderByWithColumnVariableDescTest**
```java
final String query = "SELECT name FROM tab1 "
    + "ORDER BY <sort_col> DESC";
```

**Context:** Column in ORDER BY with direction
**Variable Type:** Column
**Expected:** ordered_by with DESC sort_order preserved

**Test: orderByWithQualifiedColumnVariableTest**
```java
final String query = "SELECT name FROM tab1 t "
    + "ORDER BY t.<sort_col> ASC";
```

**Context:** Qualified column in ORDER BY
**Variable Type:** Column
**Expected:** ordered_by with table_ref=t

**Test: orderByWithMultipleColumnVariablesTest**
```java
final String query = "SELECT name FROM tab1 "
    + "ORDER BY <col1> DESC, <col2> ASC";
```

**Context:** Multiple columns in ORDER BY with mixed directions
**Variable Type:** Column
**Expected:** ordered_by list with multiple entries and sort_orders

**Test: orderByWithNullOrderingTest**
```java
final String query = "SELECT name FROM tab1 "
    + "ORDER BY <sort_col> DESC NULLS FIRST";
```

**Context:** Column with NULLS ordering clause
**Variable Type:** Column
**Expected:** ordered_by with null_order=first preserved

---

### 3. HAVING with Condition Variable (2-3 tests)

**Test: havingWithConditionVariableTest**
```java
final String query = "SELECT dept, count(*) cnt "
    + "FROM employees "
    + "GROUP BY dept "
    + "HAVING <agg_filter>";
```

**Context:** Condition variable as full HAVING expression
**Variable Type:** Condition
**Expected Assertions:**
- AST: `having={substitution={name=<agg_filter>, type=condition}}`
- Symbol Table: Should NOT add to filters (HAVING uses different path)

**Test: havingWithConditionVariableAndGroupByTest**
```java
final String query = "SELECT <group_col>, count(*) cnt "
    + "FROM employees "
    + "GROUP BY <group_col> "
    + "HAVING <agg_filter>";
```

**Context:** Condition in HAVING with column variable in GROUP BY
**Variable Type:** Condition + Column
**Expected:** grouped_by has column, HAVING has condition (separate paths)

**Test: havingWithPredicandVariableTest**
```java
final String query = "SELECT dept, count(*) cnt "
    + "FROM employees "
    + "GROUP BY dept "
    + "HAVING count(*) > <threshold>";
```

**Context:** Predicand variable (numeric comparison)
**Variable Type:** Predicand
**Expected Assertions:**
- AST: `having={condition={left={...count(*)...}, right={substitution={name=<threshold>, type=predicand}}}}`
- Table Dictionary: Should NOT include <threshold> (it's not a column)
- Symbol Table: filters should capture the predicand reference

---

### 4. HAVING with Predicand Variable (2-3 tests)

**Test: havingWithPredicandComparisonTest**
```java
final String query = "SELECT dept, count(*) cnt "
    + "FROM employees "
    + "GROUP BY dept "
    + "HAVING sum(salary) < <max_budget>";
```

**Context:** Aggregate comparison with predicand variable
**Variable Type:** Predicand
**Expected:** filters entry for predicand (not table column)

**Test: havingWithPredicandBetweenTest**
```java
final String query = "SELECT dept, count(*) cnt "
    + "FROM employees "
    + "GROUP BY dept "
    + "HAVING count(*) BETWEEN <min_val> AND <max_val>";
```

**Context:** Aggregate with BETWEEN and predicand variables
**Variable Type:** Predicand
**Expected:** filters with two predicand entries

---

### 5. QUALIFY with Condition Variable (2-3 tests, Snowflake)

**Test: qualifyWithConditionVariableTest**
```java
final String query = "SELECT emp_id, salary, "
    + "  row_number() OVER (ORDER BY salary DESC) rn "
    + "FROM employees "
    + "QUALIFY <rank_filter>";
```

**Context:** Condition variable in QUALIFY (post-window filter)
**Variable Type:** Condition
**Expected Assertions:**
- AST: `qualify={substitution={name=<rank_filter>, type=condition}}`
- Symbol Table: qualify entry or special handling for QUALIFY

**Test: qualifyWithConditionVariableAndWindowTest**
```java
final String query = "SELECT emp_id, salary, "
    + "  rank() OVER (PARTITION BY dept ORDER BY salary DESC) rnk "
    + "FROM employees "
    + "QUALIFY <rank_cond>";
```

**Context:** Condition with window function context
**Variable Type:** Condition
**Expected:** QUALIFY expression properly linked to window context

**Test: qualifyWithPredicandVariableTest**
```java
final String query = "SELECT emp_id, salary, "
    + "  row_number() OVER (ORDER BY salary) rn "
    + "FROM employees "
    + "QUALIFY rn <= <top_n>";
```

**Context:** Predicand variable in QUALIFY condition
**Variable Type:** Predicand
**Expected:** filters or qualify entry for predicand

---

### 6. QUALIFY with Predicand Variable (2-3 tests, Snowflake)

**Test: qualifyWithPredicandComparisonTest**
```java
final String query = "SELECT emp_id, salary, "
    + "  rank() OVER (PARTITION BY dept ORDER BY salary DESC) rnk "
    + "FROM employees "
    + "QUALIFY rnk <= <max_rank>";
```

**Context:** Numeric comparison with predicand in QUALIFY
**Variable Type:** Predicand
**Expected:** QUALIFY properly parses predicand and assigns type

---

## Priority 2: Window Function Coverage (8-12 Tests)

### 7. Window PARTITION BY with Column Variable (2-3 tests)

**Test: windowPartitionByColumnVariableSimpleTest**
```java
final String query = "SELECT emp_id, salary, "
    + "  sum(salary) OVER (PARTITION BY <partition_col>) running_total "
    + "FROM employees";
```

**Context:** Column variable in window PARTITION BY
**Variable Type:** Column
**Expected Assertions:**
- AST: `partition_by={sql_argument={substitution={name=<partition_col>, type=column}}}`
- Symbol Table: May need special window context handling

**Test: windowPartitionByMultipleColumnVariablesTest**
```java
final String query = "SELECT emp_id, salary, "
    + "  rank() OVER (PARTITION BY <col1>, <col2> ORDER BY salary DESC) rnk "
    + "FROM employees";
```

**Context:** Multiple columns in window PARTITION BY
**Variable Type:** Column
**Expected:** Multiple entries in partition_by

**Test: windowPartitionByQualifiedColumnVariableTest**
```java
final String query = "SELECT e.emp_id, e.salary, "
    + "  sum(e.salary) OVER (PARTITION BY e.<partition_col>) total "
    + "FROM employees e";
```

**Context:** Qualified column in window PARTITION BY
**Variable Type:** Column
**Expected:** table_ref preserved in partition clause

---

### 8. Window ORDER BY with Column Variable (2-3 tests)

**Test: windowOrderByColumnVariableSimpleTest**
```java
final String query = "SELECT emp_id, salary, "
    + "  row_number() OVER (ORDER BY <order_col> DESC) rn "
    + "FROM employees";
```

**Context:** Column in window ORDER BY
**Variable Type:** Column
**Expected:** order_by within OVER clause captures column substitution

**Test: windowOrderByMultipleColumnVariablesTest**
```java
final String query = "SELECT emp_id, salary, "
    + "  rank() OVER (PARTITION BY dept ORDER BY <col1> DESC, <col2> ASC) rnk "
    + "FROM employees";
```

**Context:** Multiple columns in window ORDER BY
**Variable Type:** Column
**Expected:** Multiple sort_specifier entries

**Test: windowPartitionAndOrderByColumnVariablesTest**
```java
final String query = "SELECT emp_id, salary, "
    + "  sum(salary) OVER (PARTITION BY <partition_col> ORDER BY <order_col>) total "
    + "FROM employees";
```

**Context:** Columns in both PARTITION BY and ORDER BY
**Variable Type:** Column
**Expected:** Both partition_by and order_by clauses with substitutions

---

### 9. Window Function with Predicand Variable (2-3 tests)

**Test: windowPartitionByPredicandVariableTest**
```java
final String query = "SELECT emp_id, salary, "
    + "  sum(salary) OVER (PARTITION BY year(<hire_date>) ORDER BY <order_col>) running_total "
    + "FROM employees";
```

**Context:** Predicand (function call) in PARTITION BY
**Variable Type:** Predicand
**Expected:** partition_by captures predicand expression

**Test: windowOrderByPredicandVariableTest**
```java
final String query = "SELECT emp_id, salary, "
    + "  row_number() OVER (ORDER BY <sort_expr>) rn "
    + "FROM employees";
```

**Context:** Predicand in window ORDER BY
**Variable Type:** Predicand
**Expected:** order_by within OVER captures predicand

**Test: windowFrameWithPredicandVariablesTest**
```java
final String query = "SELECT emp_id, salary, "
    + "  sum(salary) OVER (ORDER BY hire_date ROWS BETWEEN <start_offset> PRECEDING AND CURRENT ROW) running_sum "
    + "FROM employees";
```

**Context:** Predicand in window frame clause
**Variable Type:** Predicand
**Expected:** Frame definition captures numeric predicand

---

## Priority 3: Extended Predicates (4-6 Tests)

### 10. BETWEEN...AND with Variable Bounds

**Test: betweenPredicateWithColumnVariableBoundsTest**
```java
final String query = "SELECT emp_id FROM employees "
    + "WHERE salary BETWEEN <lower_bound> AND <upper_bound>";
```

**Context:** Column variable used as BETWEEN bounds
**Variable Type:** Column (semantically); but grammar allows value_expression
**Expected:** filters entries for both bounds

**Test: betweenPredicateWithPredicandVariableBoundsTest**
```java
final String query = "SELECT emp_id FROM employees "
    + "WHERE salary BETWEEN <calc_lower> AND <calc_upper>";
```

**Context:** Predicand variables as BETWEEN bounds
**Variable Type:** Predicand
**Expected:** filters captures expression predicands

---

### 11. IN Predicate Variants

**Test: likeAnyWithInListVariableTest**
```java
final String query = "SELECT emp_id FROM employees "
    + "WHERE name LIKE ANY <pattern_list>";
```

**Context:** IN_LIST variable with LIKE ANY operator
**Variable Type:** In_List
**Expected:** like_any_predicate recognizes variable type

**Test: inListVariableInUpdateWhereTest**
```java
final String query = "UPDATE employees "
    + "SET status = 'active' "
    + "WHERE emp_id IN <emp_list>";
```

**Context:** IN_LIST variable in DML UPDATE WHERE
**Variable Type:** In_List
**Expected:** UPDATE context properly handles IN_LIST substitution

**Test: inListVariableInDeleteWhereTest**
```java
final String query = "DELETE FROM employees "
    + "WHERE emp_id IN <emp_list>";
```

**Context:** IN_LIST variable in DML DELETE WHERE
**Variable Type:** In_List
**Expected:** DELETE context properly handles IN_LIST substitution

---

## Test Grouping & Class Placement

### Existing Test Classes & Recommended Additions

| Test Class | Current Count | Recommended Additions | Effort |
|---|---|---|---|
| **SqlEventWalkerCoreSelectFromAliasingTests** | 18 | +5 (GROUP BY, ORDER BY tests) | 8–10 hrs |
| **SqlEventWalkerPredicatesOperatorsSubstitutionsTests** | 20 | +8 (HAVING, QUALIFY, BETWEEN) | 12–15 hrs |
| **SqlEventWalkerFunctionsAggregatesWindowingTests** | 7 | +8 (Window PARTITION/ORDER tests) | 12–15 hrs |
| **SqlEventWalkerDmlUpdateInsertDeleteTruncateTests** | 20+ | +3 (UPDATE/DELETE IN_LIST tests) | 5–8 hrs |
| **SqlEventWalkerJoinsAndTableResolutionTests** | 3+ | +0 (Join coverage good) | — |
| **NEW: SqlEventWalkerWindowFunctionSubstitutionTests** | — | ~12 (Dedicated window class) | 15–20 hrs |
| **Total** | ~70 | +34–42 new tests | 50–70 hrs |

---

## Implementation Strategy

### Week 1: Priority 1 (GROUP BY, ORDER BY, HAVING, QUALIFY)
1. Create test methods in existing test classes
2. Generate golden values by running tests
3. Fix assertion mismatches
4. Verify all tests pass (or document expected failures)

### Week 2: Priority 2 (Window Functions)
1. Option A: Add to existing `SqlEventWalkerFunctionsAggregatesWindowingTests`
2. Option B: Create dedicated `SqlEventWalkerWindowFunctionSubstitutionTests`
3. Generate golden values
4. Iterate on assertions

### Week 3: Priority 3 + Polish
1. Add remaining extended predicate tests
2. Review entire suite for consistency
3. Update documentation & coverage matrix
4. Final gate verification

---

## Golden Value Generation

For each new test, run in isolation to capture golden values:

```bash
# Run single test to capture output
mvn -Dtest=SqlEventWalkerCoreSelectFromAliasingTests#groupByWithColumnVariableSimpleTest -Dtest.verbose=true test 2>&1 | grep -A 10 "AssertionError"

# Use the assertion error output to populate golden values
# Copy the actual values from error messages into Assert.assertEquals calls
```

---

## Validation Checklist

For each new test:

- [ ] Test method name is descriptive and follows existing convention
- [ ] Query string is clear and uses exactly one variable type per test
- [ ] Query is grammatically valid SQL
- [ ] All 6 standard assertions present (AST, Interface, Substitutions, TableDict, QueryDict, SymbolTable)
- [ ] Golden values verified or marked as TBD
- [ ] Test passes with no diagnostics (or known regressions documented)
- [ ] Test added to appropriate test class
- [ ] Coverage matrix updated with new test location
- [ ] Comment explains variable type and context being tested

