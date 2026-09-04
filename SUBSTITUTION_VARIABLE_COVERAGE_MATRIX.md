# Substitution Variable Coverage Matrix: Variable Types × SQL Contexts

## Summary of SQL Contexts (Grammar-Based)

Based on `SQLSelectParser.g4`, substitution variables can grammatically appear in these contexts:

### 1. **SELECT List** (select_list)
- Grammar rule: `select_list → select_item*`
- Variable types allowed: **Column**, **Predicand**, **Tuple** (as derived source)

### 2. **FROM Clause** (from_clause, table_source)
- Grammar rule: `from_clause → FROM table_reference_list join_extension?`
- Variable types allowed: **Tuple** (as table/CTE name), **Join_Extension** (postfix)

### 3. **JOIN ON Clause** (join_condition)
- Grammar rule: `join_condition → ON search_condition`
- Variable types allowed: **Condition** (full condition), **Predicand** (comparison operands), **Column** (qualified/unqualified)

### 4. **WHERE Clause** (where_clause)
- Grammar rule: `where_clause → WHERE search_condition`
- Variable types allowed: **Condition** (full condition), **Predicand** (value expressions), **Column** (qualified)

### 5. **GROUP BY Clause** (groupby_clause)
- Grammar rule: `groupby_clause → GROUP BY grouping_element_list`
- Variable types allowed: **Predicand** (as `row_value_predicand`), **Column** (qualified/unqualified)

### 6. **HAVING Clause** (having_clause)
- Grammar rule: `having_clause → HAVING boolean_value_expression`
- Variable types allowed: **Condition** (full condition), **Predicand** (aggregate expressions)

### 7. **QUALIFY Clause** (qualify_clause, Snowflake)
- Grammar rule: `qualify_clause → QUALIFY search_condition`
- Variable types allowed: **Condition** (full condition), **Predicand**

### 8. **ORDER BY Clause** (orderby_clause)
- Grammar rule: `orderby_clause → ORDER BY sort_specifier_list`
- Sort specifier rule: `sort_specifier → row_value_predicand`
- Variable types allowed: **Predicand**, **Column**

### 9. **Window Function Contexts** (window_over_partition_expression)

#### a. PARTITION BY Clause (partition_by_clause)
- Grammar rule: `partition_by_clause → PARTITION BY sql_argument_list`
- sql_argument_list: `value_expression*`
- Variable types allowed: **Predicand**, **Column**

#### b. ORDER BY inside OVER (orderby_clause within over_clause)
- Grammar rule: Same as standard ORDER BY
- Variable types allowed: **Predicand**, **Column**

### 10. **IN Predicate List** (in_predicate_value)
- Grammar rule: `in_predicate_value → subquery | LEFT_PAREN in_value_list RIGHT_PAREN | variable_identifier`
- Variable types allowed: **In_List** (as standalone variable), **Tuple** (as VALUES statement)

### 11. **UPDATE Statement Contexts**
- Assignment targets: **Column** (via column_reference)
- Assignment values: **Predicand**, **Column**
- FROM source: **Tuple**
- WHERE condition: **Condition**, **Predicand**, **Column**

### 12. **INSERT Statement Contexts**
- Column list: **Column** (via column_reference)
- VALUES list: **Predicand**, **Column**, **Tuple**
- SELECT source: **Predicand**, **Column**, **Tuple**

### 13. **Function Arguments** (sql_argument_list)
- Grammar rule: `sql_argument_list → value_expression*`
- Variable types allowed: **Predicand**, **Column**, **Tuple** (for table functions)

---

## Actual Test Coverage Matrix

### Variable Type: COLUMN
**Definition:** Variables representing actual data columns (e.g., `<metric_col>`, `<[Schema].[Table].[Column]>`)

**Grammatically Valid Contexts:**
- ✅ SELECT list
- ✅ WHERE clause
- ✅ GROUP BY clause
- ✅ HAVING clause
- ✅ ORDER BY clause
- ✅ JOIN ON clause
- ✅ Window PARTITION BY clause
- ✅ Window ORDER BY clause
- ✅ Function arguments
- ✅ Assignment values (UPDATE SET)
- ✅ QUALIFY clause
- ❌ FROM clause (not as table source)
- ❌ IN list (not standalone)

**Test Coverage Status:**

| Context | Test Class | Test Method(s) | Status | Notes |
|---------|-----------|---|--------|-------|
| SELECT list | SqlEventWalkerCoreSelectFromAliasingTests | `simpleVariableName*Test` (6), `getSimpleColumnVariableTest` | ✅ COVERED | Basic tests passing |
| SELECT list + CTE wrap | SqlEventWalkerCoreSelectFromAliasingTests | `getSubstitutionColumnVariableV9–V16Test` (8) | ❌ **FAILING** | CTE tuple token migration issue |
| WHERE clause | SqlEventWalkerPredicatesOperatorsSubstitutionsTests | `whereConditionWithSingleColumnVariableTest` | ✅ COVERED | Qualified column working |
| WHERE clause (qualified) | SqlEventWalkerDmlUpdateInsertDeleteTruncateTests | `updateComplexSubstitutionU*` (10) | ❌ **REGRESSED** | Query dict shape issue |
| JOIN ON clause | SqlEventWalkerDmlUpdateInsertDeleteTruncateTests | `updateComplexSubstitutionU6SubqueryJoinOnColumnSubstitution` | ❌ **REGRESSED** | JOIN col assignment tracking |
| GROUP BY clause | — | — | ❌ **MISSING** | No dedicated GROUP BY column variable test |
| ORDER BY clause | — | — | ❌ **MISSING** | No dedicated ORDER BY column variable test |
| HAVING clause | — | — | ❌ **MISSING** | No dedicated HAVING column variable test |
| Window PARTITION BY | — | — | ❌ **MISSING** | No dedicated PARTITION BY column variable test |
| Window ORDER BY | — | — | ❌ **MISSING** | No dedicated OVER ORDER BY column variable test |
| Function arguments | SqlEventWalkerFunctionsAggregatesWindowingTests | `trimFunctionColumnSubstitutionsTest` | ✅ COVERED | TRIM function with column var |
| Function arguments (aggregate) | SqlEventWalkerFunctionsAggregatesWindowingTests | `basicAggregateQueryWithColumnVariableTest` | ✅ COVERED | SUM(column_var) working |
| Assignment value (UPDATE) | SqlEventWalkerDmlUpdateInsertDeleteTruncateTests | `updateComplexSubstitutionU*` series | ❌ **REGRESSED** | Complex UPDATE with column subs |
| QUALIFY clause | — | — | ❌ **MISSING** | No dedicated QUALIFY column test |

**Missing Combinations to Add:**
- [ ] Column variable in GROUP BY (non-aliased)
- [ ] Column variable in ORDER BY (non-aliased)
- [ ] Column variable in HAVING clause (non-trivial aggregate)
- [ ] Column variable in QUALIFY clause (post-window filter)
- [ ] Column variable in window PARTITION BY
- [ ] Column variable in window ORDER BY (OVER...ORDER BY)
- [ ] Column variable in function arguments (CASE, CAST, various aggregate functions)
- [ ] Column variable in BETWEEN...AND predicates

---

### Variable Type: PREDICAND
**Definition:** Variables representing complete scalar value expressions (e.g., `<calc_expr>`, `<numeric_expr>`)

**Grammatically Valid Contexts:**
- ✅ SELECT list (as complete expression)
- ✅ WHERE clause (as value to compare)
- ✅ GROUP BY clause (as expression)
- ✅ HAVING clause (as aggregate expression)
- ✅ ORDER BY clause (as sort key)
- ✅ JOIN ON clause (as comparison operand)
- ✅ Window PARTITION BY clause
- ✅ Window ORDER BY clause
- ✅ Function arguments
- ✅ QUALIFY clause
- ❌ FROM clause (not as source)

**Test Coverage Status:**

| Context | Test Class | Test Method(s) | Status | Notes |
|---------|-----------|---|--------|-------|
| SELECT list | SqlEventWalkerNonSqlEndpointParserTests | `arithmeticExpressionPredicandTest` | ✅ COVERED | Basic arithmetic predicand |
| SELECT list (CASE) | SqlEventWalkerNonSqlEndpointParserTests | `caseExpressionWithPredicandSubstitutionVariableTestV*` (7) | ✅ COVERED | CASE with predicand vars |
| WHERE clause | SqlEventWalkerPredicatesOperatorsSubstitutionsTests | `whereConditionComparingPredicandVariablesTest` | ✅ COVERED | Predicand comparison (=) |
| WHERE clause (NULL check) | SqlEventWalkerPredicatesOperatorsSubstitutionsTests | `whereConditionComparingPredicandVariableToNullTest` | ✅ COVERED | IS NULL with predicand |
| WHERE clause (NOT NULL) | SqlEventWalkerPredicatesOperatorsSubstitutionsTests | `whereConditionComparingPredicandVariableToNotNullTest` | ✅ COVERED | IS NOT NULL with predicand |
| GROUP BY clause | — | — | ❌ **MISSING** | No dedicated GROUP BY predicand test |
| HAVING clause | — | — | ❌ **MISSING** | No dedicated HAVING predicand test |
| ORDER BY clause | — | — | ❌ **MISSING** | No dedicated ORDER BY predicand test |
| Window PARTITION BY | SqlEventWalkerFunctionsAggregatesWindowingTests | Window tests (embedded) | ⚠️ PARTIAL | Window partition appears in complex tests |
| Window ORDER BY | SqlEventWalkerFunctionsAggregatesWindowingTests | Window tests (embedded) | ⚠️ PARTIAL | Window order appears in complex tests |
| Function arguments (aggregate) | SqlEventWalkerFunctionsAggregatesWindowingTests | `basicAggregateQueryWithColumnVariableTest` | ✅ COVERED | Aggregate with predicand |
| Function arguments (CAST) | SqlEventWalkerCastingAndTypesTests | `castingWithColumnVariableTest` | ✅ COVERED | CAST with predicand |
| QUALIFY clause | — | — | ❌ **MISSING** | No dedicated QUALIFY predicand test |

**Missing Combinations to Add:**
- [ ] Predicand variable in GROUP BY clause
- [ ] Predicand variable in HAVING clause (post-aggregate)
- [ ] Predicand variable in ORDER BY clause  
- [ ] Predicand variable in QUALIFY clause (Snowflake)
- [ ] Predicand in BETWEEN...AND (as range bounds)
- [ ] Predicand in complex nested window functions
- [ ] Predicand in CAST operator (as target type expression)

---

### Variable Type: CONDITION
**Definition:** Variables representing complete boolean conditions (e.g., `<where_cond>`, `<filter_logic>`)

**Grammatically Valid Contexts:**
- ✅ WHERE clause (as full condition)
- ✅ HAVING clause (as full condition)
- ✅ QUALIFY clause (as full condition)
- ✅ JOIN ON clause (as ON condition)
- ❌ SELECT list (not valid)
- ❌ GROUP BY (not valid, only GROUP BY expressions)
- ❌ ORDER BY (not valid, only ORDER BY expressions)

**Test Coverage Status:**

| Context | Test Class | Test Method(s) | Status | Notes |
|---------|-----------|---|--------|-------|
| WHERE clause (single condition var) | SqlEventWalkerPredicatesOperatorsSubstitutionsTests | `whereConditionWithSingleConditionVariableTest` | ✅ COVERED | Bare condition variable |
| WHERE clause (compound AND) | SqlEventWalkerPredicatesOperatorsSubstitutionsTests | `whereMultipleConditionComparingPredicandVariableToNullTest` | ✅ COVERED | Condition var ANDed with predicand |
| HAVING clause | — | — | ❌ **MISSING** | No dedicated HAVING condition variable test |
| QUALIFY clause | — | — | ❌ **MISSING** | No dedicated QUALIFY condition variable test |
| JOIN ON clause | SqlEventWalkerJoinsAndTableResolutionTests | `basicJoinWithOnOnConditionVariableTest`, `basicJoinWithOnTwoConditionVariablesTest` | ✅ COVERED | ON with condition variables |
| JOIN ON clause (extended) | SqlEventWalkerNonSqlEndpointParserTests | `joinExtensionJoinWithOnConditionVariableInParenthesisTest`, `joinExtensionJoinWithOnTwoConditionVariablesTest` | ✅ COVERED | Parenthesized condition vars |

**Missing Combinations to Add:**
- [ ] Condition variable in HAVING clause
- [ ] Condition variable in QUALIFY clause
- [ ] Condition variable with OR logic in WHERE
- [ ] Condition variable in complex nested predicates (AND/OR combinations)
- [ ] Condition variable in UPDATE WHERE clause
- [ ] Condition variable in DELETE WHERE clause

---

### Variable Type: TUPLE
**Definition:** Variables representing table/datasource names or VALUES statements (e.g., `<[Schema].[Table]>`)

**Grammatically Valid Contexts:**
- ✅ FROM clause (as table source)
- ✅ JOIN clause (as join target)
- ✅ Subquery alias (wrapping subquery result)
- ❌ SELECT list (not valid)
- ❌ WHERE/HAVING/ORDER BY (not valid)

**Test Coverage Status:**

| Context | Test Class | Test Method(s) | Status | Notes |
|---------|-----------|---|--------|-------|
| FROM clause (single table) | SqlEventWalkerNonSqlEndpointParserTests | `tupleSubstitutionVariableTestV1`, `tupleSubstitutionVariableTestV2` | ✅ COVERED | Simple tuple substitution |
| FROM clause (named table) | SqlEventWalkerNonSqlEndpointParserTests | `basicTupleSubstitutionVariableTest` | ✅ COVERED | Named table substitution |
| FROM clause (extended name) | SqlEventWalkerNonSqlEndpointParserTests | `complexTupleSubstitutionVariableTest*` (5+) | ✅ COVERED | Complex/extended tuple names |
| FROM clause (with population) | SqlEventWalkerNonSqlEndpointParserTests | `complexTupleSubstitutionVariableTestExtendedWithPopulation*` (2+) | ✅ COVERED | Tuple with qualifier |
| FROM clause (algorithm variants) | SqlEventWalkerNonSqlEndpointParserTests | `complexTupleSubstitutionVariableTestAlgorithm*` (4+) | ✅ COVERED | Algorithm variant tuples |
| JOIN clause | SqlEventWalkerDmlUpdateInsertDeleteTruncateTests | `updateComplexSubstitutionU6SubqueryJoinOnColumnSubstitution` | ✅ COVERED | JOIN with tuple |
| Subquery alias | — | — | ⚠️ PARTIAL | Implicit via nested query tests |
| CTE source (WITH) | — | — | ⚠️ PARTIAL | Embedded in CTE tests (V9-V16 regressed) |

**Missing Combinations to Add:**
- [ ] Tuple variable in nested subquery (FROM with correlated subquery tuple)
- [ ] Tuple variable in VALUES statement (as datasource)
- [ ] Tuple variable in UNION/INTERSECT/EXCEPT operations
- [ ] Multiple tuple variables in single FROM (comma-separated)
- [ ] Tuple variable in PIVOT/UNPIVOT context
- [ ] Tuple variable in INSERT INTO context
- [ ] Tuple variable in SET OPERATION queries

---

### Variable Type: IN_LIST
**Definition:** Variables representing IN-list expressions (e.g., `<value_list>`, `<in_expr>`)

**Grammatically Valid Contexts:**
- ✅ IN predicate (as in_predicate_value)
- ✅ NOT IN predicate
- ✅ LIKE ANY predicate (similar syntax)
- ❌ Other contexts (IN_LIST is condition-specific)

**Test Coverage Status:**

| Context | Test Class | Test Method(s) | Status | Notes |
|---------|-----------|---|--------|-------|
| IN predicate (basic) | SqlEventWalkerPredicatesOperatorsSubstitutionsTests | `notInPredicateInListVariableTest`, `inPredicateInListVariableTest` | ✅ COVERED | Basic IN/NOT IN with var |
| IN predicate (WHERE) | SqlEventWalkerCoreSelectFromAliasingTests | `correlatedInSubqueryFirstCteStandaloneTest` | ⚠️ PARTIAL | Golden update pending |
| IN predicate (nested CTE) | SqlEventWalkerCoreSelectFromAliasingTests | `correlatedInSubqueryNestedCteWithOuterRefTest` | ⚠️ PARTIAL | Golden update pending |
| IN predicate (chain CTE) | SqlEventWalkerCoreSelectFromAliasingTests | `correlatedInSubqueryFinalQueryReferencesCteChainTest` | ⚠️ PARTIAL | Golden update pending |
| NOT IN predicate | SqlEventWalkerPredicatesOperatorsSubstitutionsTests | `notInPredicateInListVariableTest` | ✅ COVERED | NOT IN variant |
| LIKE ANY predicate | — | — | ❌ **MISSING** | No dedicated LIKE ANY in_list test |
| IN predicate (subquery form) | — | — | ⚠️ PARTIAL | Appears in golden tests (implicit) |

**Missing Combinations to Add:**
- [ ] IN_LIST variable with LIKE ANY operator
- [ ] IN_LIST variable in UPDATE WHERE clause
- [ ] IN_LIST variable in DELETE WHERE clause
- [ ] IN_LIST variable in HAVING clause

---

### Variable Type: JOIN_EXTENSION
**Definition:** Variables representing optional join clauses appended to a query (Informatica-specific pattern)

**Grammatically Valid Contexts:**
- ✅ FROM clause (as postfix join_extension)
- ❌ Other contexts (join_extension is FROM-postfix specific)

**Test Coverage Status:**

| Context | Test Class | Test Method(s) | Status | Notes |
|---------|-----------|---|--------|-------|
| FROM clause (postfix) | SqlEventWalkerNonSqlEndpointParserTests | `joinExtensionFullOuterJoinWithOnOnConditionVariableTest` | ✅ COVERED | FULL OUTER with ext var |
| FROM clause (with condition) | SqlEventWalkerNonSqlEndpointParserTests | `joinExtensionJoinWithOnConditionVariableInParenthesisTest` | ✅ COVERED | Parenthesized condition |
| FROM clause (two conditions) | SqlEventWalkerNonSqlEndpointParserTests | `joinExtensionJoinWithOnTwoConditionVariablesTest` | ✅ COVERED | Multiple ON conditions |
| FROM clause (mixed vars) | SqlEventWalkerNonSqlEndpointParserTests | `joinExtensionJoinWithConditionAndJoinExtensionVariablesTest` | ✅ COVERED | Condition + extension vars |

**Missing Combinations to Add:**
- [ ] JOIN_EXTENSION with CROSS JOIN (no condition)
- [ ] JOIN_EXTENSION with NATURAL JOIN
- [ ] JOIN_EXTENSION in UPDATE statement
- [ ] JOIN_EXTENSION in DELETE statement
- [ ] JOIN_EXTENSION with LATERAL modifier

---

## Test Coverage Gaps Summary

### CRITICAL GAPS (High Priority)

| Variable Type | Missing Context | Reason | Est. Tests Needed |
|---|---|---|---|
| **Column** | GROUP BY clause | Core SQL functionality | 3–5 |
| **Column** | ORDER BY clause | Core SQL functionality | 3–5 |
| **Column** | QUALIFY clause (Snowflake) | Core SQL functionality | 2–3 |
| **Column** | Window PARTITION BY | Window function support | 2–3 |
| **Column** | Window ORDER BY | Window function support | 2–3 |
| **Condition** | HAVING clause | Core SQL functionality | 2–3 |
| **Condition** | QUALIFY clause | Core SQL functionality | 2–3 |
| **Predicand** | GROUP BY clause | Core SQL functionality | 2–3 |
| **Predicand** | HAVING clause | Core SQL functionality | 2–3 |
| **Predicand** | ORDER BY clause | Core SQL functionality | 2–3 |
| **Predicand** | QUALIFY clause | Core SQL functionality | 2–3 |

### MEDIUM GAPS (Recommended)

| Variable Type | Missing Context | Reason | Est. Tests Needed |
|---|---|---|---|
| **Column** | CAST operator (type expression) | Type handling | 1–2 |
| **Column** | BETWEEN...AND operands | Predicate operators | 1–2 |
| **Predicand** | BETWEEN...AND bounds | Predicate operators | 1–2 |
| **Predicand** | CAST target type | Type handling | 1–2 |
| **IN_LIST** | LIKE ANY predicate | String matching | 1–2 |
| **IN_LIST** | UPDATE/DELETE WHERE | DML contexts | 2–3 |
| **Tuple** | INSERT INTO context | DML contexts | 1–2 |
| **Tuple** | Set operations (UNION/INTERSECT) | Query composition | 2–3 |
| **Join_Extension** | CROSS JOIN (no condition) | Alternative join syntax | 1 |

### LOWER PRIORITY GAPS (Nice to Have)

| Variable Type | Missing Context | Reason | Est. Tests Needed |
|---|---|---|---|
| **Condition** | OR logic in WHERE | Logical operators | 1–2 |
| **Predicand** | Nested window functions | Complex expressions | 1–2 |
| **Tuple** | PIVOT/UNPIVOT context | Relational operators | 1–2 |
| **Column** | EXISTS subquery | Subquery patterns | 1 |

---

## Proposed Test Additions (Phase 10 Expansion)

### Priority 1: Core Clause Coverage (15–20 tests)

**GROUP BY with Column Variable:**
```java
@Test
public void groupByWithColumnVariableTest() {
  // SELECT count(*) FROM tab WHERE x=1 GROUP BY <group_col>
}

@Test
public void groupByWithPredicandVariableTest() {
  // SELECT count(*) FROM tab GROUP BY <expr>
}
```

**ORDER BY with Column Variable:**
```java
@Test
public void orderByWithColumnVariableTest() {
  // SELECT col FROM tab ORDER BY <order_col> DESC
}

@Test
public void orderByWithPredicandVariableTest() {
  // SELECT col FROM tab ORDER BY <sort_expr> ASC
}
```

**HAVING with Condition/Predicand Variables:**
```java
@Test
public void havingWithConditionVariableTest() {
  // SELECT col FROM tab GROUP BY col HAVING <agg_cond>
}

@Test
public void havingWithPredicandVariableTest() {
  // SELECT col FROM tab GROUP BY col HAVING count(*) > <threshold>
}
```

**QUALIFY with Variables (Snowflake):**
```java
@Test
public void qualifyWithConditionVariableTest() {
  // SELECT col FROM tab QUALIFY <window_filter>
}

@Test
public void qualifyWithPredicandVariableTest() {
  // SELECT col FROM tab QUALIFY row_number() OVER (...) = <rank_val>
}
```

### Priority 2: Window Function Coverage (8–12 tests)

**Window PARTITION BY with Column/Predicand:**
```java
@Test
public void windowPartitionByColumnVariableTest() {
  // SELECT col FROM tab ... OVER (PARTITION BY <partition_col> ...)
}

@Test
public void windowOrderByColumnVariableTest() {
  // SELECT col FROM tab ... OVER (... ORDER BY <order_col> DESC)
}
```

### Priority 3: Extended Predicate Coverage (4–6 tests)

**BETWEEN...AND with Variables:**
```java
@Test
public void betweenPredicateWithColumnVariablesTest() {
  // SELECT col FROM tab WHERE col BETWEEN <lower_col> AND <upper_col>
}
```

**IN_LIST with Additional Operators:**
```java
@Test
public void likeAnyWithInListVariableTest() {
  // SELECT col FROM tab WHERE col LIKE ANY <pattern_list>
}
```

---

## Estimated Impact

- **Total new tests to add:** 30–50 tests
- **Coverage improvement:** ~25–30% increase in variable×context combinations
- **Phase 10 gate expansion:** Current 115+ tests → 145–165 tests
- **Timeline impact:** 1–2 weeks for test creation + execution
- **Maintenance burden:** Low (existing test patterns can be parameterized)

---

## Recommendations

1. **Immediate (Phase 10):** Add Priority 1 tests (GROUP BY, ORDER BY, HAVING, QUALIFY) to close critical gaps
2. **Short-term (Phase 11):** Add Priority 2 tests (window functions) while working on context_list resolution
3. **Long-term (Phase 12):** Add Priority 3 and lower-priority tests as DML parity work progresses
4. **Architecture:** Create test templates/base classes for each variable type to reduce duplication
5. **Maintenance:** Update this matrix as new tests are added; use it as ongoing coverage dashboard

