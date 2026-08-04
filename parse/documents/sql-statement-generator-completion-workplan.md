# SQL statement generator completion — independent work plan

**Status:** ⏸️ Milestone delivered; remaining work not started (spun off from consolidation Phase **13.6**, Aug 2026)  
**Origin:** `symbol-table-resolution-consolidation-worklist.md` §13.6  
**Primary code:** `generators/SQLStatementGenerator.java`, `generators/AbstractSQLASTGenerator.java`  
**Primary tests:** `generators/SQLStatementGeneratorTest` (~51 tests; milestone round-trips green)  
**Related:** DDL opaque blob re-emit depends on walker AST shape; structured DDL options are a separate plan ([ddl-structured-options-parsing-workplan.md](ddl-structured-options-parsing-workplan.md))

---

## Why this is its own plan

Consolidation needed a **usable** AST→SQL path for DML / SCRIPT / DDL / PIVOT smoke coverage. That milestone is done. Full rule-aligned generation (every grammar/AST surface, substitution injection, Jinja, expression families) is a multi-month product/tooling track and must not block Phase **20** or closing the consolidation worklist.

---

## Architecture snapshot

- `AbstractSQLASTGenerator.generateStatement(mumbleKey, node, sql)` dispatches on **mumble keys** and `SQLParserEndPoints` tree keys.
- `SQLStatementGenerator` overrides selected `on*` methods and adds `emit*` helpers for statements/clauses.
- Leaf / unhandled shapes often fall through `appendNode` / `handleEndPoint` (incomplete emission risk — print “Unexpected node”).
- Round-trip tests: parse SQL → walker AST → `generateFromAst` → assert regenerated SQL (and console **Generated SQL** blocks for visual review).

**Design tension (resolve in G0):** one emit method per ANTLR grammar rule vs one method per mumble AST key. Today the code is **mumble-key / statement-shaped**. Prefer keeping mumble-key dispatch unless a measured inventory proves grammar-rule 1:1 is clearer; document the decision in G0 before mass refactor.

---

## Milestone status — what is done vs started

### Statement / endpoint families

| Family | Status | Evidence / entry points |
|--------|--------|-------------------------|
| **INSERT** | ✅ Milestone complete | `emitInsertStatement` — VALUES, INSERT SELECT, DEFAULT VALUES, column lists, ON CONFLICT (DO NOTHING / DO UPDATE + WHERE), RETURNING; `roundTripInsert*` |
| **UPDATE** | ✅ Milestone complete | `emitUpdateStatement` — SET assignments, FROM, WHERE, RETURNING; `roundTripUpdate*` |
| **DELETE** | ✅ Milestone complete | `emitDeleteStatement` — USING / join, WHERE, RETURNING; `roundTripDelete*` |
| **TRUNCATE** | ✅ Milestone complete | `emitTruncateStatement` + truncate endpoint; `roundTripTruncateEndpointTest` |
| **SELECT** | ✅ Core started / used heavily | `emitSelectStatement` / `onSelect` — select list, FROM, WHERE/HAVING/QUALIFY, GROUP BY, ORDER BY; exercised via INSERT SELECT, WITH, PIVOT joins, scripts |
| **WITH / CTE** | ✅ Milestone complete for common shapes | `emitWithQuery` / `emitCteBody` — nested WITH, SELECT/VALUES/INSERT/UPDATE/DELETE/UNION CTE bodies; `roundTripScriptWithCteTest` |
| **VALUES** | ✅ Milestone complete | Standalone + script-wrapped + INSERT VALUES matrix; `roundTripScriptValuesOnlyTest` |
| **UNION / set ops** | 🟡 Started | `emitUnionQuery` present; INTERSECT/EXCEPT parity and full set-op clause coverage need dedicated round-trips |
| **SCRIPT** | ✅ Milestone complete for mixed kinds | `emitScriptStatement` — SELECT/WITH/VALUES/DML/DDL items; `roundTripScriptMixedStatementsTest` |
| **DDL CREATE** | ✅ Opaque milestone | CTAS, column-def blobs, qualified names; `roundTripDdlCreate*` / `roundTripCreateTableWithColumnsTest` |
| **DDL ALTER / DROP** | ✅ Opaque milestone | Options blob re-emit; `roundTripDdlAlterTableTest`, `roundTripDdlDropTableTest` |
| **PIVOT / UNPIVOT** | ✅ Milestone complete for exercised shapes | `emitPivotClause` / `emitUnpivotClause` + join + clause egress; `roundTripPivot*` / `roundTripUnpivot*` |
| **Table functions** | 🟡 Started | `emitTableFunction` / FROM path hooks; needs systematic round-trips |
| **Endpoint stubs** | 🔴 Thin | Predicand / join_extension / in_list / condition / column / tuple / query endpoints mostly `handleEndPoint` |

### Expression / clause surface (used inside statements)

| Surface | Status | Notes |
|---------|--------|-------|
| Columns / literals / calc (`+` `-` `*` `/`) | ✅ Basic | `onColumn`, `onLiteral`, `onCalculation` |
| Functions | ✅ Basic | `onFunction` / parameters |
| AND / OR / simple condition | ✅ Basic | Filters path |
| Parentheses | ✅ Via dispatch | |
| Concatenate | 🟡 Imported / partial | Needs dedicated round-trip matrix |
| CASE / WHEN / ELSE | 🔴 Dispatch exists in abstract; concrete emit incomplete vs walker AST | |
| IN / NOT IN / LIKE ANY | 🔴 Endpoint / mumble handlers thin | |
| EXISTS / scalar lookup | 🟡 `onLookup` present | Needs coverage |
| Window `OVER` / PARTITION BY / frame | 🔴 Not a milestone focus | |
| Substitutions `<var>` / Jinja | 🟡 `onSubstitution` exists; **no external substitution map** for round-trip injection | |
| Join extension tail | 🔴 Endpoint stub | |

---

## Logical execution outline (resume order)

### G0 — Inventory and contract (do first)

1. Run the parked **progress-tracker prompt** (below): map ANTLR rule constants ↔ existing `on*` / `emit*` methods; mark complete / in progress / missing.
2. Decide dispatch model: **mumble-key (current)** vs grammar-rule 1:1; write the decision at the top of this doc.
3. Document **non-goals**: pretty-print formatting, comment preservation, whitespace fidelity.
4. Define round-trip success: parse → AST → generate → re-parse AST equality **or** normalized SQL string equality (pick one and stick to it).
5. Add a living progress table (rule or mumble key → status → owning method → test).

### G1 — Stabilize milestone regressions

1. Keep all existing `roundTrip*` tests green as a permanent gate profile (e.g. `-Psql-generator-gate` or suite class).
2. Fix any `Unexpected node` console leaks on milestone paths before expanding.

### G2 — Complete SELECT expression families

Work family-by-family (steps under each family below). Prefer small SQL fixtures already covered by walker tests.

### G3 — Complete set-ops and query composition

UNION / INTERSECT / EXCEPT nesting, parenthesized set ops, ORDER BY/LIMIT on set-op results.

### G4 — Substitution and endpoint trees

External substitution map; predicand / condition / column / in_list / tuple / join_extension endpoints.

### G5 — Window and advanced FROM

Window functions + frames; table functions; lateral / advanced join shapes as product needs.

### G6 — DDL structured emit (optional bridge)

Only after/with [ddl-structured-options-parsing-workplan.md](ddl-structured-options-parsing-workplan.md): emit typed flags; keep opaque fallback.

---

## Step-by-step by capability / clause family

### A. INSERT (✅ milestone — extend carefully)

1. Add round-trips for INSERT…WITH CTE body if not already covered by script/WITH paths.
2. Cover multi-row VALUES edge cases and sparse DEFAULT mixtures.
3. ON CONFLICT: conflict target expressions beyond bare columns; inferred unique index form if AST supports it.
4. Keep `insert={}` wrap contract aligned with walker.

### B. UPDATE (✅ milestone — extend carefully)

1. Multi-assignment SET with calc/function RHS round-trips.
2. UPDATE FROM multi-join and correlated subquery SET RHS.
3. QUALIFY on UPDATE if grammar/AST emits it for product dialects.

### C. DELETE (✅ milestone — extend carefully)

1. Multi-source USING / join trees beyond current happy path.
2. DELETE…RETURNING expression lists (not only columns).

### D. TRUNCATE (✅ milestone)

1. Multi-table / dialect option blobs if walker gains them (stay opaque until DDL plan structures them).

### E. SELECT list and projections (🟡 core present)

1. Inventory select-item AST shapes from walker goldens (column, alias, calc, function, subquery, `*`, substitution).
2. Implement/override `onSelect` item emission per shape; add one round-trip per shape.
3. Prove alias emission order matches walker interface order.
4. Cover DISTINCT / ALL qualifiers if present on AST.

### F. FROM / JOIN (🟡 present)

1. Catalog join types emitted today (INNER/LEFT/… + ON).
2. Add round-trips: nested joins, USING (if AST), cross join, subquery-in-FROM aliases.
3. Ensure join_extension endpoint can splice opaque/join-extension substitutions without dropping tails.
4. Table-function FROM: parameter lists, alias, lateral if applicable.

### G. WHERE / HAVING / QUALIFY / filters (🟡 basic AND/OR/condition)

1. Matrix: comparison, IS NULL, BETWEEN, IN list, EXISTS, NOT, nested AND/OR.
2. Implement missing `onBetween` / `onIn` / `onExists` / `onNot` concrete emission if still falling through.
3. One round-trip per operator family in SELECT and in UPDATE/DELETE WHERE.

### H. GROUP BY / ORDER BY / LIMIT / OFFSET (🟡 partial)

1. GROUP BY expression lists including calc and ordinal if supported.
2. ORDER BY nulls first/last (`null_order`) already partially handled — add dedicated tests.
3. LIMIT/OFFSET emission if mumble keys appear on AST.

### I. WITH / CTE (✅ common shapes)

1. Recursive CTE markers if grammar emits them.
2. Column lists on CTE names `(a, b) AS (…)`.
3. Multi-CTE mutual references already partially covered — add unhappy generation diagnostics policy (fail loud vs skip).

### J. Set operations UNION / INTERSECT / EXCEPT (🟡 started)

1. Extend `emitUnionQuery` (or sibling) for INTERSECT/EXCEPT operators and ALL/DISTINCT qualifiers.
2. Round-trip three-level nests mirroring walker EXCEPT matrix canaries.
3. ORDER BY / LIMIT applied to set-op result.

### K. VALUES (✅ milestone)

1. VALUES as query expression in FROM/CTE already partially covered — add UNION of VALUES if AST supports it.

### L. PIVOT / UNPIVOT (✅ milestone shapes)

1. Multi-aggregate PIVOT and multi-column UNPIVOT if walker AST differs from current emitters.
2. Keep generation aligned with relational-modifier AST keys (`value` / `for` / `in`), not derived symbol-table names.

### M. CASE expressions (🔴)

1. Capture walker AST for simple CASE and searched CASE from existing walker tests.
2. Implement `onCase` / clauses / WHEN / THEN / ELSE emission.
3. Round-trip: SELECT CASE, CASE in WHERE, CASE in ORDER BY.

### N. Scalar functions & casts (🟡)

1. CAST / TRY_CAST / datatype nodes (`onDatatype`) round-trips.
2. Nested function args including keyword args if AST has them.
3. DISTINCT aggregate qualifier emission.

### O. Window functions (🔴)

1. Emit `window_function` / `over` / `partition_by` / window `orderby` / frame (`preceding`/`following`).
2. Round-trip minimal ROW_NUMBER and one framed aggregate.
3. Do not invent SQL for non-ANSI window sites the walker intentionally leaves untested.

### P. Predicates: IN / LIKE / ILIKE ANY (🔴)

1. Wire `onSQLParserInList` and mumble `in` / `in_list` / `like_any` emitters.
2. Round-trip IN (list), IN (subquery), NOT IN, LIKE ANY.

### Q. Concatenate & string operators (🟡)

1. Emit `concatenate` lists with correct `||` (or dialect op) and parentheses policy.
2. Round-trip against `concatenationFormulaTest`-style AST.

### R. Substitutions & Jinja (🟡 / product-critical)

1. Accept optional **external substitution map** on the generator (name → replacement SQL fragment).
2. `onSubstitution`: if map has name, emit replacement; else emit original `<name>` (or Jinja spelling).
3. Round-trip with map empty (identity) and map populated (injected SQL).
4. Cover types: predicand, condition, column, tuple, in_list, join_extension, query — one test each.
5. Document that generation does not re-type substitutions; it only prints.

### S. SQLParserEndPoints trees (🔴 stubs)

For each endpoint key in `AbstractSQLASTGenerator`:

1. Predicand tree  
2. Condition tree  
3. Column tree  
4. In-list tree  
5. Tuple tree  
6. Join-extension tree  
7. Query tree (if distinct from SQL/script)

**Per endpoint steps:**

1. Capture a minimal AST from the corresponding parser endpoint / walker test.  
2. Replace `handleEndPoint` with a dedicated emit that walks the endpoint root.  
3. Add `roundTrip*Endpoint*` test.  
4. Mark progress table row complete.

### T. SCRIPT multi-statement (✅ milestone)

1. Preserve statement separators and ordering.  
2. Add script item for any new statement kind as that kind is completed.

### U. DDL opaque emit (✅ milestone) → structured (optional)

1. Keep opaque `options` / `columns` re-emit working.  
2. When DDL structured plan delivers typed flags, emit them ahead of residual blobs (see DDL plan D4).

---

## Parked progress-tracker prompt (run at G0)

> Try to measure our SQL generator progress and map out the entire work plan for completing the generation class. In order to do that, my supposition is that the generator ought to have ONE and ONLY ONE generating method PER GRAMMAR RULE. If a rule is at a leaf node for a LEXER item like a term or identifier, that can be a shared method or an inline method to emit the text, but especially any rule that contains other rules ought to have its own method — I THINK. Comment on this supposition and if you think its incorrect let me know before you proceed with the next part of my prompt here. If you proceed, then I want you to locate the rule constants generated by ANTLR and I want you to add a comment before each generation method you've already created in the class indicating which rule (or possibly set of rules) the method is handling directly (this should not include rules that are called by this generator method, but rules whose statements are directly emitted/constructed by the generator method. If you can do that I need you to finish this exercise by creating a detailed progress tracker here in this work plan where you list out every rule by its header and rule number, and indicate whether its complete, in progress, or still to be started. Finally, present this table to me so I can see where we are.

**Note:** Current implementation is mumble-key based; G0 should explicitly accept or reject the per-grammar-rule supposition before refactoring.

---

## Exit criteria (full completion)

- [ ] G0 inventory table committed in this doc  
- [ ] Milestone `roundTrip*` suite remains green  
- [ ] Expression families M–R have round-trips  
- [ ] Endpoint trees S have non-stub handlers + tests  
- [ ] Substitution map API documented and tested  
- [ ] Non-goals documented  
- [ ] Consolidation worklist continues to show **13.6 ❌ spun off** (this doc is source of truth)

---

## Suggested first resume slice

1. G0 inventory + dispatch decision  
2. Family **M (CASE)** + family **P (IN/LIKE)** — high leverage for SELECT/WHERE fidelity  
3. Family **R (substitutions map)** — unlocks product round-trips  
4. Then set-ops (**J**) and windows (**O**) as needed  
