# Handoff prompt: qualified column capture, carry-up, and table dictionary materialization

Use this as the opening message in a **fresh agent conversation** after the working-point commit.

---

## Goal

Fix SQL parse/walker behavior for **nested SELECT lists, predicands, and subqueries** where outer table references (e.g. `tab1.t`, `tab1.<y_col>`) appear **before** the parent query's `FROM` clause is walked.

Two outcomes must hold for the canary test `nestedQueryDemoTest` in  
`parse/src/test/java/sql/walker/SqlEventWalkerCoreSelectFromAliasingTests.java` (~line 348):

1. **Diagnostics:** exactly **2 fatals** — `tab2.e3` (unknown table) and `gg.y` (unknown table in IN subquery select list). No premature fatals for outer-correlated `tab1.*` refs seen before outer `FROM` completes.

2. **Table dictionary:** global `tab1` entries must include token locations for:  
   `t`, `<y_col>`, `a`, `x`, `<z_col>`, `<w_col>`  
   (from refs scattered across nested subqueries, FROM subquery, and IN subquery).

**Do not update golden expected strings** until parse/validation outcome is reviewed by the user.

---

## Canary SQL (current test query)

```sql
select
  (select max((select avg(t) at from tt where tt.b > tab1.t and tt.e = ee.x2)) mxd
   from ee where ee.x = (select tab1.<y_col>)) max_D,
  tab1.a aa,
  (select min(D) mnd from ee where ee.x = tab1.x) min_D,  kk.w
 from (select w from jj where jj.y = tab1.<z_col> and jj.m > tab2.e3) kk join tab1
where c in (select c, gg.y gg_y from ff where ff.z = tab1.<w_col>)
```

---

## Design principles (do not violate)

1. **Ingress:** capture at parse event only — `collectUnresolvedColumnReference` in `SqlParseEventWalker` on `exitColumn_reference` / `exitColumn_primary`. No retrospective scans of `filters`, `groupby`, `orderby`, or `def_queryN` interface maps.

2. **Egress:** use the **existing** `unresolved_column` map and scope-exit carry-up. At scope exit: **resolve** if visible, **defer** if an ancestor query_spec `FROM` is still pending, **fatal** otherwise. No separate pending bucket.

3. **No second resolution system.** Do not add large abstractions (`egressQualifiedUnresolvedMap`, parse-time materialize wrappers, duplicate key builders). If adding code, prefer **one small helper** called from **two existing hooks**.

4. **Cannot target owner query at discovery time.** When `max_D` is walked first, there is no hint that `tab1` will exist later. Carry-up scope-by-scope; materialize when an ancestor can resolve.

5. **Minimize diff size.** Match existing naming and patterns in `SqlParseSymbolTreeHelper`, `SqlASTWalkerHelper`, `SqlParseEventWalker`.

---

## What is already implemented (working point after commit)

### A. FROM-stack diagnostic deferral (working)

**Files:** `SqlASTWalkerHelper.java`, `SqlParseEventWalker.java`, `SqlParseSymbolTreeHelper.java`

- `queryFromClauseComplete` on flag stack (`null` / `false` / `true` per query_spec frame).
- `beginQuerySpecificationFromClause()` on `enterQuery_specification`.
- `markCurrentQueryFromClauseComplete()` on `exitFrom_clause` for SELECT query_specs (`isQuerySpecificationFromClause`).
- `anyIncompleteQuerySpecificationOnStack()` — true while any ancestor query_spec `FROM` is pending.
- `shouldDeferUnresolvedUntilQueryFromComplete()` in symbol tree helper wraps the above.
- `resolveVisibleOuterDeferredUnresolved` (predicate subquery exit): materialize or defer; **fatal only when `!shouldDeferUnresolvedUntilQueryFromComplete()`**.

**Result:** 2 fatals restored on `nestedQueryDemoTest`. Test failure is **stale golden line numbers only** (e3 at line 6 not 4; do not fix goldens without user approval).

### B. Minimal FROM-complete retry helper (partial — tab1 dict still incomplete)

**File:** `SqlParseSymbolTreeHelper.java`

- `retryResolvableQualifiedUnresolvedInCurrentScope()` — reads live `unresolved_column`, splits qualified, calls `retryResolvableQualifiedUnresolvedEntries`, writes back remainder.
- `retryResolvableQualifiedUnresolvedEntries(qualifiedMap, visibleAliasMap, visibleTableDict)` — for each `qualifier.col` where `canResolveQualifiedUnknownInScope`, merge into global + local `table_dictionary` via `mergeResolvedColumnIntoDictionary`.

**Hooks (only these two):**
1. `exitFrom_clause` after `markCurrentQueryFromClauseComplete()` — e.g. resolves `ee.x2` when middle query FROM completes.
2. `publishQueryLikeScope` after `mergeUnresolvedEntriesIntoCurrentScope(hoistedUnresolved)` — retry when subquery unresolved is hoisted to parent.

**Reverted (do not reintroduce):**
- `recordColumnReference`, `egressQualifiedUnresolvedMap`, `materializeResolvableQualifiedReferencesInCurrentScope`, ingress try-materialize at parse time.

### C. Broader branch context (already in tree, not the immediate task)

This branch also includes walk-time visible scope / `context_list` migration, predicate subquery frame handling (`exitPredicateSubqueryFrame`), UPDATE CTE fixes, and related test golden updates elsewhere. **Do not expand scope into those areas** unless a regression requires it.
The intent of the context list is so that when we DO have known context carrying table resolutions from parent contexts, we can use those to help resolve these correlated column references. How this works should be rationalized against the changes we're making for resolving columns and collecting references for the ultimate table dictionaries. Not to keep things separated and parallel, but to consider if the the two approaches are complementary and can be merged in a smart way.
---

## Current gap (next task)

After the working-point commit, `nestedQueryDemoTest` **table dictionary for `tab1`** is still incomplete.

**Observed (approximate):** `tab1` has `<z_col>`, `c`, `<w_col>` but is **missing** `t`, `<y_col>`, `a`, `x`.

**Likely causes to investigate (in order):**

1. **Retry not running at the right time for outer `tab1` join** — when query11 `FROM` completes (`join tab1`), carried `tab1.*` entries in `unresolved_column` should become resolvable; confirm `retryResolvableQualifiedUnresolvedInCurrentScope` sees them and that `buildEffectiveVisibleAliasMap` includes `tab1`.

2. **Entries dropped during `convertSymbolTableToTableDictionary`** before they reach carry-up — check whether `deferCorrelatedValueSubqueryQualifiedUnknowns` / `retainOnlyLocallyResolvableExplicitQualifiedUnknowns` removes entries that should stay in `unresolved_column` for parent resolution.

3. **Select-list refs (`tab1.a aa`)** may materialize through a different path (interface validation) than predicand/subquery refs — ensure direct select-item refs also land in global table dict, not only via `unresolved_column`.

4. **Predicate-frame partition** — `partitionPredicateUnresolvedByScope` sends non-local qualifiers to `outerCorrelated`; `gg.y` correctly fatals, but `tab1.*` in IN subquery should resolve after deferral, not be lost.

**Do not fix by:** scanning `filters`/`interface` after the fact, or adding a third resolution pipeline. Collecting unresolved columns and column substitution variables should happen when we're exiting column reference subtrees in the event walker, no matter where these occur or what kind of statement or phrase is being handled by the parse event walker. This will eliminate the need for post-collection scans or fallbacks and keep logic tied to the event tree shape. 

2) Another good test case would be the donorEmailWithInvalidFatalErrorOnQualifiedColumnVariableTest in the SqlEventWalkerLiveSampleQueriesTest class. In this case, we have the problem where in the select list we introduce a column and alias entry in the select list, and then immediately follow that up with a reference to the alias we just defined in the select list. The current validation doesn't notice that the column substitution reference "source_partner_system_name" that appears in the partition function 2 columns later is just the alis for the predicand substitution variable: <source_partner_system_name_donor_email> We need to adjust the select list logic so that it can "resolve" unqualified column references to entries showing ealier in sequence in its own select list. This unique scenario needs to be supported as well.
---

## Key symbols and files

| Symbol / method | Role |
|-----------------|------|
| `MUMBLE_UNRESOLVED_COLUMN_KEY` | Live carry-up map (`unresolved_column`) |
| `collectUnresolvedColumnReference` | Parse-event ingress (`SqlASTWalkerHelper`) |
| `finalizeQueryScopeSymbolTable` | Scope exit: split qualified/unqualified, pass-up, top-level fatals |
| `publishQueryLikeScope` | Pop scope, hoist archived unresolved, merge to parent |
| `exitPredicateSubqueryFrame` | IN/predicand/EXISTS merge; `resolveInnerLocalPredicateUnresolved` + `resolveVisibleOuterDeferredUnresolved` |
| `retryResolvableQualifiedUnresolvedInCurrentScope` | FROM-complete / post-hoist materialization |
| `canResolveQualifiedUnknownInScope` | Qualifier visible (alias exists or table in dict) |
| `mergeResolvedColumnIntoDictionary` | Write token locations into table dict |
| `buildEffectiveVisibleAliasMap` / `buildEffectiveVisibleTableCollection` | Local + inherited outer scope |

| File | Role |
|------|------|
| `parse/src/main/java/astwalkers/SqlASTWalkerHelper.java` | FROM stack, collect unresolved |
| `parse/src/main/java/sql/walker/SqlParseEventWalker.java` | Parse hooks |
| `parse/src/main/java/sql/symboltree/SqlParseSymbolTreeHelper.java` | Scope finalize, predicate exit, retry helper |

---

## Test commands

```bash
cd parse
mvn test -Dtest=SqlEventWalkerCoreSelectFromAliasingTests#nestedQueryDemoTest
```

**Pass criteria:**
- `assertFatalDiagnosticCount(..., 2)` with messages for `e3`/tab2 and `y`/gg.
- `getTableColumnDictionaryMap()` tab1 contains `t`, `<y_col>`, `a`, `x`, `<z_col>`, `<w_col>`.

**Regression spot-check after changes:**
- UPDATE CTE tests (U3/U4/U5/U7/U9) if touching carry-up.
- Union-branch correlated tests.
- Broader `SqlEventWalkerCoreSelectFromAliasingTests` if time permits.

---

## Implementation constraints for the next agent

1. **Smallest correct diff** — extend `retryResolvableQualifiedUnresolvedEntries` or add one call site; do not add 200+ lines.
2. **Read surrounding code** before editing; match conventions.
3. **No golden updates** until user reviews outcome.
4. **No git commit** unless user asks.
5. If stuck, **instrument** `nestedQueryDemoTest` (print `unresolved_column` at FROM complete and scope exit) rather than adding abstractions.

---

## Prompt to paste into new conversation

```
We are continuing work on SQL walker qualified column resolution for nested queries.

Read parse/docs/qualified-column-table-dict-handoff-prompt.md for full context.

Summary: nestedQueryDemoTest must have exactly 2 fatals (tab2.e3, gg.y) and tab1 table dict entries for t, <y_col>, a, x, <z_col>, <w_col>. Diagnostics deferral via FROM-stack is done. A minimal retryResolvableQualifiedUnresolvedInCurrentScope helper exists (exitFrom_clause + publishQueryLikeScope). The egress layer was reverted — do not rebuild it.

Next: make tab1 table dictionary complete using existing unresolved_column carry-up and the retry helper only. No new abstractions. Do not update test goldens.

Use SqlEventWalkerSubqueriesAndClauseSemanticsTests as the primary development driver for cross-subclause resolution unification, starting at the block:
"UNALIASED DERIVED TABLE IN FROM/JOIN TESTS".
Follow and preserve the V1-V16 sequence in that block as the canonical progression for SELECT/JOIN/WHERE/EXISTS/GROUP BY/HAVING/QUALIFY/ORDER BY behavior.
When changing resolution logic, validate against V1-V16 first before widening to other suites.
```
