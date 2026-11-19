# Slice D array-function reconciliation

Date: 2026-11-18. Source-audit baseline: commit `5604d3c`.
Original planning update: the user approved four omitted functions and
requested lexer expansion first, establishing the **21-function audit baseline**.
**Post-T022 scope amendment:** all remaining planned array functions must now
close in Slice D, with **`ARRAY_CONSTRUCT_STRUCTURED` / T025 next**.
The [expanded inventory](#expanded-all-array-closure-inventory) accounts for
**42 canonical names**, including previously completed A/B/C work and all
former E function work. Execute T025..T034 before the existing T023/T024 gates.
Execution is defined by the
[expanded master-plan sequence](sql-grammar-5.1.4-workplan.md#slice-d-expanded-remaining-implementation-sequence-approved-2026-11-18);
original 21-name execution update: D0.1..D0.4 are complete. `SPLIT=456` was appended immediately
before `Identifier`, with nonreserved-name handling. Official Java lexer
snapshots verify both complete replacement maps, recorded in the master plan.
Generation/compilation passed; D0.5's developer manual golden repairs are now
committed in `641ded6`, with all 36 affected classes validated. D1.1 completes
the ARRAY_REPEAT matrix with 48 cases. D1.2.1 completes ARRAY_TO_STRING with 45
cases; D1.2.2 completes ARRAY_SLICE with 55 cases. D2.6 completes ARRAY_DISTINCT,
ARRAY_COMPACT and ARRAY_SIZE with 31 cases each. D3.8.1-D3.8.3 complete TO_ARRAY,
AS_ARRAY and IS_ARRAY with 35, 32 and 37 cases respectively. D3.8.4/D3.9.1-D3.9.2
complete SPLIT, SPLIT_TO_ARRAY and STRTOK_TO_ARRAY with 48, 53 and 53 cases.
D3.9.3-D3.9.5 complete ARRAY_GENERATE_RANGE, ARRAY_POSITION and ARRAYS_ZIP
with 79, 50 and 90 cases. T021 / D-SIG added 134 signature-policy controls.
T022 / D-AUDIT is complete (2026-11-19): all 21 functions cover all 854 required
matrix cells, with nine genuine gaps repaired. Maven and VS Code each pass all
1,033 focused tests. The original 21-name individual batches, D-SIG and D-AUDIT
are complete; they do not certify the expanded 42-name scope. T025..T034 and
T023's sequential accuracy ladder / T024's final accounting remain open.

See the [D1 checkpoint summary](slice-d-d1-checkpoint-summary.md) for the changes,
verification and reusable tooling added since the manual token-ID repair commit.

## Result and scope

**Slice D is not ready for final sign-off.**

The following implementation findings describe the **original 21-name baseline**:

1. All **17 originally assigned Slice D names** now have lexer tokens, explicit
   family routing, a corresponding walker handler, and Snowflake registry entries.
   There is no further absent lexer token within that fixed list.
2. `ARRAY_REPEAT` now has its required strict matrix: 48 independent cases,
   including 45 six-output goldens and three parser-only syntax controls.
3. **All ten originally identified older-matrix remediations are complete.**
   `ARRAY_TO_STRING` and `ARRAY_SLICE` are complete under D1.2.1/D1.2.2, with
   exact diagnostics and their original extractor goldens retained.
   `ARRAY_DISTINCT`, `ARRAY_COMPACT` and `ARRAY_SIZE` are complete under D2.6,
   each with 29 six-output cases and two parser-only syntax controls.
   `SPLIT_TO_ARRAY` and `STRTOK_TO_ARRAY` are complete under D3.9.1/D3.9.2,
   each with 50 six-output cases and three parser-only syntax controls.
   `ARRAY_GENERATE_RANGE`, `ARRAY_POSITION` and `ARRAYS_ZIP` are complete under
   D3.9.3-D3.9.5, with 79, 50 and 90 independently identified cases respectively.
   T022 subsequently audited all 21 matrices and closed nine remaining cells,
   including gaps in four of the six older expanded matrices. The final
   accuracy sign-off ladder remains open.
4. **Four documented Snowflake functions were omitted from the conversion /
   predicate / split target list:** `TO_ARRAY`, `AS_ARRAY`, `IS_ARRAY`, and `SPLIT`.
   All four are explicitly approved for D3.8. `TO_ARRAY`, `AS_ARRAY` and
   `IS_ARRAY`, together with `SPLIT`, now have dedicated conversion-family
   routing, Snowflake affinity and independent strict matrices.
   `SPLIT` was the only new lexer keyword needed in this D batch and
   is already present; the other three existing tokens are unchanged.
5. `ARRAY_CONSTRUCT_STRUCTURED` was incorrectly left under Slice A / GC1 rather
   than D closure. It is now **T025, the very next implementation**, with an
   expanded lexical-readiness inventory, official before/after constants and
   developer manual token-ID repair gate before matrix completion.

The initial source audit did not establish runtime certification. Subsequent
manual token-ID repairs are committed, and the latest focused run passed
1,033 tests with zero failures/errors/skips. The earlier string-to-array batch
also passed 24 subscript and 78 table-function tests; no production code changed
in the final three-function batch, T021 or T022. The matrix audit and
signature-policy boundary are complete for the original 21 names only;
expanded implementation/audits and the final Slice D sign-off ladder remain open.

The new comparison covers **every Entry 4 subsection and all array-related
mentions throughout the master workplan and companion planning/consumer
documents**, including Entry 18's array-literal history and Entry 19.3's
completed aggregate work. Constructor/aggregate/set/mutator names are explicitly
carried forward; table and higher-order function requirements are explicitly
transferred into D. This is an exhaustive reconciliation of those documents,
not a claim to have surveyed every function in Snowflake's evolving catalog.

## Verification requirements

- **REQ-001:** Every one of the **42 canonical names**, plus existing FLATTEN
  spellings, has an explicit status/task, lexical-recognition and grammar/walker
  disposition, dialect policy, and independently identified test evidence.
  Existing A/B/C work is reused, not omitted or automatically reimplemented.
- **REQ-002:** A green family class is not accepted as per-function matrix
  completion. Check all twelve required placements, resolution controls,
  argument substitutions, nested/derived/set-op/SCRIPT controls, six full
  extractor goldens, and exact positioned diagnostics. Table/lambda/aggregate
  cells must respect their actual grammar role; justify each inapplicable
  scalar cell explicitly. Verify the old constructor snippet checklist too.
- **REQ-003:** Reconcile documented signatures and parser validation policy
  explicitly; do not confuse parse coverage with SQL execution or runtime
  type/value validation.
- **REQ-004:** Expand the lexer first, preserving official before/after snapshots
  and publishing both incremental and cumulative complete token-ID worklists.
  Pause for developer manual golden repairs. Complete matrix work and the final
  accuracy sign-off ladder afterward. T025 reopens this gate for new lexer work;
  passing the earlier SPLIT/D0 gate does not clear a later token-ID change.

## Expanded all-array closure inventory

**Current execution order:** T025 -> T026 -> T027 -> T028 -> T029 -> T030 ->
T031 -> T032 -> T033 -> T034 -> T023 -> T024. Task details and exact source
paths live in the
[master plan](sql-grammar-5.1.4-workplan.md#d5--exhaustive-array-closure-amendment-t025t034-before-t023t024).
Task numbers preserve history rather than numeric execution order.

Inventory totals: **seven outstanding names + 21 original audited names +
14 previously completed names = 42**. Every row below is subject to T033's
expanded signature reconciliation, T034's independent cell audit and the
T023/T024 final gates; a prior completion is reusable evidence, not automatic
expanded-scope sign-off.

| Canonical function | Original subsection / slice | Current disposition | Slice D implementation/evidence owner |
|---|---|---|---|
| `ARRAY_CONSTRUCT_STRUCTURED` | 4.2 / A / GC1 | **NEXT; implementation and lexical/manual-repair gate pending** | T025 |
| `ARRAY_CONTAINS` | 4.1 | Partial; VARIANT/operand and broader matrix closure pending | T026 |
| `ARRAY_FLATTEN` | 4.5 / E | Existing keyword; scalar routing/affinity/matrix closure pending | T027 |
| `FLATTEN` | 4.5 / E | Baseline delivered; advanced table/lateral scope and lineage parity pending | T028 |
| `FILTER` | 4.12 / E | Higher-order syntax, binding/lineage and independent matrix pending | T029 |
| `TRANSFORM` | 4.12 / E | Higher-order syntax, binding/lineage and independent matrix pending | T030 |
| `REDUCE` | 4.12 / E | Higher-order syntax, binding/lineage and independent matrix pending | T031 |
| `ARRAY_TO_STRING` | 4.9 / D | Original T021/T022 audit complete | T007 evidence; T033/T034 recheck |
| `ARRAY_SLICE` | 4.9 / D | Original T021/T022 audit complete | T008 evidence; T033/T034 recheck |
| `ARRAY_REPEAT` | 4.9 / D / GC2 | Original T021/T022 audit complete | T006 evidence; T033/T034 recheck |
| `ARRAY_DISTINCT` | 4.10 / D | Original T021/T022 audit complete | T009 evidence; T033/T034 recheck |
| `ARRAY_COMPACT` | 4.10 / D | Original T021/T022 audit complete | T010 evidence; T033/T034 recheck |
| `ARRAY_SIZE` | 4.10 / D | Original T021/T022 audit complete | T011 evidence; T033/T034 recheck |
| `ARRAY_SORT` | 4.10 / D | Original T021/T022 audit complete, including optional flags | D2.3 evidence; T033/T034 recheck |
| `ARRAY_MAX` | 4.10 / D | Original T021/T022 audit complete | D2.3 evidence; T033/T034 recheck |
| `ARRAY_MIN` | 4.10 / D | Original T021/T022 audit complete | D2.3 evidence; T033/T034 recheck |
| `ARRAY_REVERSE` | 4.10 / D / GC2 | Original T021/T022 audit complete | D2.4 evidence; T033/T034 recheck |
| `ARRAY_SUM` | 4.10 / D / GC2 | Original T021/T022 audit complete; aggregate/runtime distinction retained | D2.4 evidence; T033/T034 recheck |
| `SPLIT_TO_ARRAY` | 4.11 / D | Original T021/T022 audit complete; retained compatibility provenance | T016 evidence; T033/T034 recheck |
| `STRTOK_TO_ARRAY` | 4.11 / D | Original T021/T022 audit complete | T017 evidence; T033/T034 recheck |
| `ARRAY_GENERATE_RANGE` | 4.11 / D | Original T021/T022 audit complete | T018 evidence; T033/T034 recheck |
| `ARRAY_POSITION` | 4.11 / D | Original T021/T022 audit complete | T019 evidence; T033/T034 recheck |
| `ARRAYS_ZIP` | 4.11 / D | Original T021/T022 audit complete, including variadic tail | T020 evidence; T033/T034 recheck |
| `ARRAYS_TO_OBJECT` | 4.11 / D / GC3 | Original T021/T022 audit complete | D3.6 evidence; T033/T034 recheck |
| `TO_ARRAY` | 4.11 / D | Original T021/T022 audit complete | T012 evidence; T033/T034 recheck |
| `AS_ARRAY` | 4.11 / D | Original T021/T022 audit complete | T013 evidence; T033/T034 recheck |
| `IS_ARRAY` | 4.11 / D | Original T021/T022 audit complete | T014 evidence; T033/T034 recheck |
| `SPLIT` | 4.11 / D | Original T021/T022 audit complete | T015 evidence; T033/T034 recheck |
| `ARRAY_CONSTRUCT` | 4.2 / A | Prior completion recorded; reconcile older snippet/DDL/SCRIPT checklist | T032 carry-forward verification |
| `ARRAY_CONSTRUCT_COMPACT` | 4.2 / A | Prior completion recorded; verify independent method witnesses | T032 carry-forward verification |
| `ARRAY_AGG` | 4.3 / B; Entry 19.3 | Prior ordered/window aggregate and compound-array completion recorded | T032 carry-forward verification |
| `ARRAY_UNION_AGG` | 4.6 / B | Prior completion recorded | T032 carry-forward verification |
| `ARRAY_UNIQUE_AGG` | 4.6 / B | Prior completion recorded | T032 carry-forward verification |
| `ARRAY_CAT` | 4.7 / C | Prior completion recorded | T032 carry-forward verification |
| `ARRAY_EXCEPT` | 4.7 / C | Prior completion recorded | T032 carry-forward verification |
| `ARRAY_INTERSECTION` | 4.7 / C | Prior completion recorded | T032 carry-forward verification |
| `ARRAYS_OVERLAP` | 4.7 / C | Prior completion recorded | T032 carry-forward verification |
| `ARRAY_APPEND` | 4.8 / C | Prior completion recorded | T032 carry-forward verification |
| `ARRAY_PREPEND` | 4.8 / C | Prior completion recorded | T032 carry-forward verification |
| `ARRAY_INSERT` | 4.8 / C | Prior completion recorded | T032 carry-forward verification |
| `ARRAY_REMOVE` | 4.8 / C | Prior completion recorded | T032 carry-forward verification |
| `ARRAY_REMOVE_AT` | 4.8 / C | Prior completion recorded | T032 carry-forward verification |

### Inventory sources and non-function obligations

- The master workplan's complete 4.1–4.12 assignment map yields all 42 names.
  Its GC list supplies the structured constructor; 4.1 supplies contains;
  4.5 supplies both scalar and table flattening; 4.12 supplies all three
  higher-order functions. None may remain an "outside D" surprise.
- The [grammar/consumer overview](sql-grammar-extensions-since-2026-01-01.md)
  also names **`LATERAL_FLATTEN`** as an existing function-name spelling.
  The grammar explicitly handles that spelling's MODE literals.
  T028/T033/T034 must preserve and verify its compatibility policy separately
  from Snowflake `LATERAL FLATTEN` syntax; it is recorded under canonical
  FLATTEN, not counted as another native Snowflake function.
- The [author handoff](panto-template-remediation-5.1.4.md),
  [resolution worklist](symbol-table-resolution-consolidation-worklist.md),
  [D1 checkpoint](slice-d-d1-checkpoint-summary.md), retired workplan pointers,
  and [coverage inventory](coverage/sql_walker_exit_method_gaps.md) introduce
  no additional canonical array-function names. Coverage percentages and
  accepted author-remediation cases do not prove an independent matrix.
- Array literals `[expr, ...]`, result subscripts `expr[index]`, constructor
  snippet parity, aggregate `WITHIN GROUP` / `OVER`, and lateral table scope
  are required syntax/lineage controls, not extra function counts. Reuse
  delivered 4.4 and Entry 19.3 evidence in T032 and verify it in T034.
- LISTAGG/PERCENTILE_CONT/PERCENTILE_DISC, PARSE_URL, GENERATOR, RESULT_SCAN,
  INFER_SCHEMA, VALIDATE, TO_VARCHAR and MD5 occur as neighboring features or
  composition helpers, not unassigned Entry 4 array-function implementations.
  Existing composition controls stay in scope; their unrelated feature work,
  SQL generation, and non-array release entries are not pulled into D.

## Approved 21-function ledger (all 21 explicit routes present)

**Historical baseline through T022, not the current full closure inventory.**
The lexical IDs, counts and PASS evidence below are from that checkpoint;
future developer-confirmed lexer repairs must be recorded under T025.

Shared sources:

- [Grammar names and routes](../src/main/antlr4/sql/SQLSelectParser.g4#L1320)
- [Walker handlers](../src/main/java/sql/walker/SqlParseEventWalker.java#L12006)
- [Snowflake registry](../src/main/java/sql/grammar/SnowflakeRoutineDialectRegistry.java#L11)
- [Focused test host](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13)
- [Per-function matrix contract](sql-grammar-5.1.4-workplan.md#entry-4-test-framework-matrix-contract)

Route/handler keys:

| Key | Grammar rule | Walker handler |
|---|---|---|
| T | `array_transform_function` | `exitArray_transform_function` |
| U | `array_unary_transform_function` | `exitArray_unary_transform_function` |
| C | `array_conversion_seed_function` | `exitArray_conversion_seed_function` |
| R | `array_range_seed_function` | `exitArray_range_seed_function` |
| P | `array_position_seed_function` | `exitArray_position_seed_function` |
| Z | `array_zip_seed_function` | `exitArray_zip_seed_function` |

The table below gives current audited counts through T022, including T021's
cross-cutting signature controls and T022's nine gap repairs. The
[T022 cell ledger](#t022--d-audit-completed-cell-level-reconciliation) identifies
a named source witness for every required cell of every function.
Counts are independently owned methods, not credits for incidental nested
calls. All matching SQL call sites for the older ten functions
were also searched across parser Java tests; they occur in this focused host.
"Exact lists" counts complete diagnostic-list comparisons, not individual
position/count assertions. Retained older methods now have complete exact
diagnostic lists alongside their original complete output goldens.

| Function | Task | Lexer ID | Route | Registry | Methods | Six goldens | Exact lists | Current source assessment |
|---|---|---:|---|---|---:|---:|---:|---|
| `ARRAY_TO_STRING` | D1.2.1 complete | 362 | T | Yes | 50 | 47 | 50 | Expanded matrix and exact legacy diagnostics; parser nonreserved-name fix; evidence below; T022: 44/44 cells |
| `ARRAY_SLICE` | D1.2.2 complete | 369 | T | Yes | 60 | 57 | 60 | All three arguments isolated; exact legacy diagnostics; evidence below; T022: 53/53 cells |
| `ARRAY_REPEAT` | D1.1 / GC2 complete | 455 | T | Yes | 55 | 51 | 55 | Expanded matrix plus three parser-only syntax cases; evidence below; T022: 44/44 cells |
| `ARRAY_DISTINCT` | D2.6.1 / T009 complete | 363 | U | Yes | 35 | 33 | 35 | Independent strict matrix; original goldens preserved; evidence below; T022: 32/32 cells |
| `ARRAY_COMPACT` | D2.6.2 / T010 complete | 364 | U | Yes | 35 | 33 | 35 | Independent strict matrix; original goldens preserved; evidence below; T022: 32/32 cells |
| `ARRAY_SIZE` | D2.6.3 / T011 complete | 368 | U | Yes | 37 | 34 | 37 | Independent strict matrix; original goldens preserved; evidence below; T022: 32/32 cells |
| `ARRAY_SORT` | D2.3 complete | 370 | U + generic optional-argument calls | Yes | 55 | 52 | 55 | Optional flags and main operand audited; NULL/name-neutrality gaps closed; T022: 52/52 cells |
| `ARRAY_MAX` | D2.3 complete | 371 | U | Yes | 33 | 31 | 33 | Full matrix audited; NULL/name-neutrality gaps closed; T022: 32/32 cells |
| `ARRAY_MIN` | D2.3 complete | 372 | U | Yes | 33 | 31 | 33 | Full matrix audited; NULL/name-neutrality gaps closed; T022: 32/32 cells |
| `ARRAY_REVERSE` | D2.4 complete | 452 | U | Yes | 33 | 31 | 33 | Full older matrix independently reverified; no T022 gap; T022: 32/32 cells |
| `ARRAY_SUM` | D2.4 complete | 453 | U | Yes | 33 | 31 | 33 | Full older matrix independently reverified; structured-array aggregate semantics retained; T022: 32/32 cells |
| `SPLIT_TO_ARRAY` | D3.9.1 / T016 complete | 374 | C | Yes, retained compatibility | 58 | 55 | 58 | Optional delimiter; Redshift provenance; legacy affinity/goldens preserved; evidence below; T022: 45/45 cells |
| `STRTOK_TO_ARRAY` | D3.9.2 / T017 complete | 375 | C | Yes | 58 | 55 | 58 | Optional delimiter-set form and independent operand controls; evidence below; T022: 45/45 cells |
| `ARRAY_GENERATE_RANGE` | D3.9.3 / T018 complete | 366 | R | Yes | 87 | 83 | 87 | Two-/three-argument forms; independently isolated start/stop/step; original literal controls retained; evidence below; T022: 54/54 cells |
| `ARRAY_POSITION` | D3.9.4 / T019 complete | 367 | P | Yes | 57 | 53 | 57 | Independent needle/array controls; NULL/empty/duplicate/absent inputs and CASE projection; evidence below; T022: 44/44 cells |
| `ARRAYS_ZIP` | D3.9.5 / T020 complete | 373 | Z | Yes | 95 | 91 | 95 | One/two/three/four inputs; isolated variadic tail controls, unequal lengths and NULL/empty arrays; evidence below; T022: 65/65 cells |
| `ARRAYS_TO_OBJECT` | D3.6 complete | 454 | C | Yes | 48 | 45 | 48 | Both independent unqualified operands and third syntax control added; T022: 44/44 cells |
| `TO_ARRAY` | D3.8.1 / T012 complete | 376 | C | Yes | 39 | 37 | 39 | Dedicated conversion route; scalar/VARIANT/NULL controls; evidence below; T022: 32/32 cells |
| `AS_ARRAY` | D3.8.2 / T013 complete | 377 | C | Yes | 36 | 34 | 36 | Dedicated conversion route; VARIANT/NULL controls; evidence below; T022: 32/32 cells |
| `IS_ARRAY` | D3.8.3 / T014 complete | 378 | C | Yes | 41 | 39 | 41 | Dedicated conversion route; bare predicates and projection/CASE; evidence below; T022: 32/32 cells |
| `SPLIT` | D3.8.4 / T015 complete | 456 | C | Yes | 55 | 51 | 55 | Dedicated route; legacy AST serialization and subscript/FLATTEN composition preserved; evidence below; T022: 44/44 cells |

Historical T020 baseline: **890 methods**, with 849 six-output and 41 parser-only
cases. T021 added 134 (117 six-output, 17 parser-only); T022 added nine
(eight six-output, one parser-only). Current total: **1,033 methods**,
**974** six-output cases, **59** parser-only controls and **1,033** exact
diagnostic lists. Both runners pass all 1,033. D-SIG and D-AUDIT are complete;
T023/T024 accuracy/accounting gates remain open.

## Final individual-function batch: T018-T020

The existing range, position and zip routes, walkers and registry entries
already satisfy these new cases. Coverage-only work added 196 methods and
strengthened all 23 retained legacy diagnostic lists, without changing old
extractor goldens or any of the 671 unrelated host methods. One focused Maven
run passed **890/890**, zero failures/errors/skips, exit 0. No production,
lexer or toolkit changes were needed.

- `ARRAY_GENERATE_RANGE(start, stop [, step])`: the main three-argument matrix
  isolates every operand. Additional two-argument column/variable/NULL and
  nested/derived/set-op cases preserve omitted step as two AST parameters.
  Original literal-only controls remain intact. Positive/negative steps,
  equal/reversed bounds and arithmetic expressions are parsed, not executed.
  A zero-step control explicitly preserves the parser/runtime boundary:
  Snowflake prohibits zero at execution, but PSS does not evaluate that value.
- `ARRAY_POSITION(value_expr, array)`: both operands have independent
  resolution and substitution controls. VARIANT-cast literal needles,
  first/duplicate/absent values, empty arrays and SQL NULL/NULL-element inputs
  have complete goldens. CASE projection preserves needle and array lineage.
  Index values and database comparability/type rules are not evaluated.
- `ARRAYS_ZIP(array [, array ...])`: the three-input matrix independently
  exercises each operand; single-array controls and retained/two-array cases
  cover the smaller forms. Four-array controls independently isolate the
  fourth variadic operand and both substitution types. Unequal-length,
  empty-array and NULL-element cases retain syntax and lineage without
  materializing zipped objects or validating database runtime types.

Generated method-level tables below identify every required placement,
operand control, deep case, neutral UDF/name, SCRIPT reset, dialect case and
exact parser-only malformed-syntax control. Cross-function D-SIG and D-AUDIT
are still separate tasks; this is not final Slice D or release sign-off.

## D1.1 ARRAY_REPEAT method-level completion evidence

All methods below are in the [focused host](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java).
The table gives method-name suffixes: prepend `arC41_arrayRepeat` and append
`V0Test`. Together these identify all 48 independent cases. Each valid-query
case asserts all six full extractor goldens and the exact diagnostic list.
The three syntax cases assert exact error counts and full parser diagnostics,
without walking rejected syntax trees.

| Required cells | Method suffixes |
|---|---|
| SELECT, WHERE, JOIN ON, HAVING | `SelectPlacement`, `WherePlacement`, `JoinPlacement`, `HavingPlacement` |
| QUALIFY, ORDER BY, VALUES, INSERT | `QualifyPlacement`, `OrderByPlacement`, `ValuesPlacement`, `InsertPlacement` |
| UPDATE, DELETE, DDL, SCRIPT | `UpdatePlacement`, `DeletePlacement`, `DdlPlacement`, `ScriptPlacement` |
| Qualified success | `SelectPlacement` |
| Combined and isolated unqualified success | `UnqualifiedOperands`, `Arg1UnqualifiedOperand`, `Arg2UnqualifiedOperand` |
| Unresolved, independently by argument | `Arg1Unresolved`, `Arg2Unresolved` |
| Ambiguous, independently by argument | `Arg1Ambiguous`, `Arg2Ambiguous` |
| Missing alias, independently by argument | `Arg1MissingAlias`, `Arg2MissingAlias` |
| Hidden inner alias, independently by argument | `Arg1HiddenAlias`, `Arg2HiddenAlias` |
| Raw predicand and qualified-column variables, independently by argument | `Arg1PredicandVariable`, `Arg2PredicandVariable`, `Arg1ColumnVariable`, `Arg2ColumnVariable` |
| Combined raw/column variables | `ValuesPlacement`, `BothColumnVariables` |
| Nested expression, derived export, set-op sibling isolation | `NestedExpression`, `DerivedOutput`, `SetOpSiblingIsolation` |
| Snowflake/ANSI/Snowflake reset | `ScriptPlacement` |
| Qualified UDF and same-named column neutrality | `QualifiedUdfNeutral`, `SameNamedColumnNeutral` |
| Uppercase and mixed-dialect diagnostics | `Uppercase`, `MixedDialect` |
| Numeric, string, Boolean and array elements | `NumericElement`, `StringElement`, `BooleanElement`, `ArrayElement` |
| NULL independently in either operand, with column and literal counterparts | `Arg1Null`, `Arg2Null`, `NullLiteralElement`, `NullLiteralCount` |
| Zero and negative count inputs | `ZeroCount`, `NegativeCount` |
| Malformed comma/parenthesis syntax | `TrailingCommaSyntax`, `MissingCommaSyntax`, `MissingRightParenthesisSyntax` |

This was coverage-only work: the existing transform route/walker/registry passed
without modification. The prior route-only seed was strengthened; the other 256
methods and their golden expectations are unchanged. No lexer IDs changed.
The documented two-operand form is covered; the shared rule's optional third
operand and generic-call arity policy are not silently redefined here. D-SIG
owns that cross-function policy review. Runtime element/count types and array
results are deliberately not evaluated.

## D1.2.1 ARRAY_TO_STRING method-level completion evidence

All methods are in the [focused host](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java).
The table identifies all 45 methods using prefixes: `S:` means prepend
`snowflakeArrayToString`; `N:` means prepend `arC42_arrayToString`. Append
`V0Test` to each suffix. The 15 `S:` methods retain their original extractor
goldens and now compare complete exact diagnostic lists. The 30 new `N:` methods
contain 27 full six-output cases and three parser-only syntax controls.

| Required cells | Prefixed method suffixes |
|---|---|
| SELECT, WHERE, JOIN ON, HAVING | `S:SelectPlacement`, `S:WherePlacement`, `N:JoinPlacement`, `N:HavingPlacement` |
| QUALIFY, ORDER BY, VALUES, INSERT | `S:QualifyPlacement`, `S:OrderByPlacement`, `S:ValuesPlacement`, `S:InsertPlacement` |
| UPDATE, DELETE, DDL, SCRIPT | `S:UpdatePlacement`, `S:DeletePlacement`, `S:CreateViewDdlPlacement`, `S:ScriptStatementScopedWarning` |
| Qualified success | `S:SelectPlacement` |
| Combined and isolated unqualified success | `N:UnqualifiedOperands`, `N:Arg1UnqualifiedOperand`, `N:Arg2UnqualifiedOperand` |
| Unresolved, independently by argument | `N:Arg1Unresolved`, `N:Arg2Unresolved` |
| Ambiguous, independently by argument | `S:AmbiguousUnqualifiedDiagnostic`, `N:Arg2Ambiguous` |
| Missing alias, independently by argument | `S:FirstOperandMissingAliasFatal`, `S:SecondOperandMissingAliasFatal` |
| Hidden inner alias, independently by argument | `N:Arg1HiddenAlias`, `N:Arg2HiddenAlias` |
| Raw predicand variables, independently by argument | `N:Arg1PredicandVariable`, `S:WithPredicandSubstitution` |
| Qualified-column variables, independently by argument | `N:Arg1ColumnVariable`, `N:Arg2ColumnVariable` |
| Combined raw/column variables | `N:BothPredicandVariables`, `S:QualifiedColumnVariables` |
| Nested expression, derived export, set-op sibling isolation | `N:NestedExpression`, `N:DerivedOutput`, `N:SetOpSiblingIsolation` |
| Snowflake/ANSI/Snowflake reset | `N:ScriptReset` |
| Qualified UDF and same-named column neutrality | `N:QualifiedUdfNeutral`, `N:SameNamedColumnNeutral` |
| Uppercase and mixed-dialect diagnostics | `N:Uppercase`, `N:MixedDialect` |
| Array containing NULL, empty array, empty separator | `N:ArrayWithNullElement`, `N:EmptyArray`, `N:EmptySeparator` |
| NULL independently in either operand | `N:Arg1Null`, `N:Arg2Null` |
| Malformed comma/parenthesis syntax | `N:TrailingCommaSyntax`, `N:MissingCommaSyntax`, `N:MissingRightParenthesisSyntax` |

The qualified-UDF control exposed a missing `nonreserved_keywords` alternative:
the initial capture failed with three parser errors. Adding `ARRAY_TO_STRING`
to that parser alternative fixed both neutrality controls. The lexer
declarations and generated Java lexer are unchanged, so no token-ID migration
or existing golden recapture was performed. All 289 unrelated methods,
including all 48 ARRAY_REPEAT methods, remain unchanged.

The official two-operand signature, NULL inputs and empty array/separator forms
are parse/lineage controls, not evaluated string results or runtime type checks.
The shared transform rule's optional third operand and generic-call arity
boundary remain assigned to D-SIG. Runtime validation: **334 passed**, zero
failures/errors/skips, exit code 0, using the exact Maven command recorded in
the master-plan D1.2.1 completion record.

## D1.2.2 ARRAY_SLICE method-level completion evidence

In the [focused host](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java),
`S:` means prepend `snowflakeArraySlice`; `N:` means prepend `arC43_arraySlice`.
Append `V0Test` to each suffix below. These identify all 55 independent methods:
12 retained methods with strengthened diagnostics and 43 new cases. All 52
valid-syntax cases compare six complete outputs and exact walker diagnostics;
three syntax cases compare exact parser error counts and full diagnostics.

| Required cells | Prefixed method suffixes |
|---|---|
| SELECT, WHERE, JOIN ON, HAVING | `S:SelectPlacement`, `N:WherePlacement`, `N:JoinPlacement`, `S:HavingPlacement` |
| QUALIFY, ORDER BY, VALUES, INSERT | `N:QualifyPlacement`, `S:OrderByPlacement`, `S:ValuesPlacement`, `S:InsertPlacement` |
| UPDATE, DELETE, DDL, SCRIPT/reset | `S:UpdatePlacement`, `S:DeletePlacement`, `N:DdlPlacement`, `N:ScriptReset` |
| Combined and isolated unqualified success | `N:UnqualifiedOperands`, `N:Arg1UnqualifiedOperand`, `N:Arg2UnqualifiedOperand`, `N:Arg3UnqualifiedOperand` |
| Unresolved, independently by argument | `N:Arg1Unresolved`, `N:Arg2Unresolved`, `N:Arg3Unresolved` |
| Ambiguous, independently by argument | `N:Arg1Ambiguous`, `N:Arg2Ambiguous`, `N:Arg3Ambiguous` |
| Retained combined ambiguity control | `S:AmbiguousUnqualifiedDiagnostic` |
| Missing alias, independently by argument | `S:FirstOperandMissingAliasFatal`, `S:SecondOperandMissingAliasFatal`, `S:ThirdOperandMissingAliasFatal` |
| Hidden inner alias, independently by argument | `N:Arg1HiddenAlias`, `N:Arg2HiddenAlias`, `N:Arg3HiddenAlias` |
| Raw predicand variables, independently by argument | `N:Arg1PredicandVariable`, `N:Arg2PredicandVariable`, `N:Arg3PredicandVariable` |
| Qualified-column variables, independently by argument | `N:Arg1ColumnVariable`, `N:Arg2ColumnVariable`, `N:Arg3ColumnVariable` |
| Combined raw/column variables | `N:AllPredicandVariables`, `S:QualifiedColumnVariables` |
| Nested expression, derived export, set-op sibling isolation | `N:NestedExpression`, `N:DerivedOutput`, `N:SetOpSiblingIsolation` |
| Qualified UDF and same-named column neutrality | `N:QualifiedUdfNeutral`, `N:SameNamedColumnNeutral` |
| Uppercase and mixed-dialect diagnostics | `N:Uppercase`, `N:MixedDialect` |
| NULL independently in all three operands | `N:Arg1Null`, `N:Arg2Null`, `N:Arg3Null` |
| Empty array, negative start/end, equal/reversed bounds | `N:EmptyArray`, `N:NegativeStart`, `N:NegativeEnd`, `N:EqualBounds`, `N:ReversedBounds` |
| Malformed comma/parenthesis syntax | `N:TrailingCommaSyntax`, `N:MissingCommaSyntax`, `N:MissingRightParenthesisSyntax` |

No production changes were required. All 12 original extractor golden blocks
and all 322 unrelated methods are unchanged. Generated captures were checked
for intended diagnostic codes, argument-variable types and dedicated routes;
derived/hidden-alias and sibling dictionary payloads were audited against the
established consumer contracts. Index-bound cases assert parsing/lineage, not
evaluated slices. Signature policy remains for D-SIG. One full focused run
passed **377/377**, zero failures/errors/skips, exit 0.

## Older-matrix gap ledger (all ten remediations complete)

The required placement list is SELECT, WHERE, JOIN ON, HAVING, QUALIFY, ORDER BY,
VALUES, INSERT, UPDATE, DELETE, DDL, SCRIPT. A JOIN elsewhere in a query does not
count as placing the function in its ON condition. The original audit found ten
incomplete matrices; completed rows below retain their closure evidence.

| Function(s) | Missing independent placements | Other verified omissions |
|---|---|---|
| `ARRAY_TO_STRING` | Closed by D1.2.1 | All original gaps are covered by the 45-method evidence ledger above; existing extractor goldens retained. |
| `ARRAY_SLICE` | Closed by D1.2.2 | All original gaps are covered by the 55-method evidence ledger above; existing extractor goldens retained. |
| `ARRAY_DISTINCT`, `ARRAY_COMPACT`, `ARRAY_SIZE` | Closed by D2.6 | Independent 31-case matrices and exact legacy diagnostics; evidence below. |
| `SPLIT_TO_ARRAY`, `STRTOK_TO_ARRAY` | Closed by D3.9.1/D3.9.2 | Independent 53-case matrices, optional delimiter forms and both operands' resolution/variable controls; evidence below. |
| `ARRAY_POSITION` | Closed by D3.9.4 | Independent 50-case matrix, both operands' resolution/variable controls and exact legacy diagnostics; evidence below. |
| `ARRAYS_ZIP` | Closed by D3.9.5 | Independent 90-case matrix, single/multiple inputs and isolated third/fourth operands; evidence below. |
| `ARRAY_GENERATE_RANGE` | Closed by D3.9.3 | Independent 79-case matrix, start/stop/step controls and both signatures; all seven original literal cases preserved; evidence below. |

These closures are supported by inspected SQL and generated named-method
evidence, not just a green family total. Existing complete goldens were not
refreshed. The one-query-per-method and exact diagnostic contract is retained.

## Four formerly omitted names now approved for D3.8

| Function | Documented role/signature | Current code recognition | Completion evidence |
|---|---|---|---|
| `TO_ARRAY(expr)` | Convert an expression to ARRAY | Token 376; dedicated C route and registry entry | D3.8.1: 35 independent cases |
| `AS_ARRAY(variant_expr)` | Cast a VARIANT to ARRAY | Token 377; dedicated C route and registry entry | D3.8.2: 32 independent cases |
| `IS_ARRAY(variant_expr)` | ARRAY type predicate | Token 378; dedicated C route and registry entry | D3.8.3: 37 independent cases |
| `SPLIT(string, separator)` | Split a string into ARRAY | Token 456; dedicated C route and registry entry | D3.8.4: 48 independent cases; existing subscript/FLATTEN goldens preserved |

These names are **not necessarily unparseable**. Generic routine support is
different from explicit family handling, dialect policy, and strict completion
evidence. The three existing tokens do not need another lexer addition.

Snowflake documents `SPLIT`, not the inventory's `SPLIT_TO_ARRAY` spelling:
the official `split_to_array` documentation URL returned HTTP 404, and that name
is absent from the retrieved semi-structured function list. This does not prove
every dialect/version lacks that spelling. Amazon Redshift documents
`SPLIT_TO_ARRAY(string [, delimiter])`, with an optional delimiter defaulting to
comma and a SUPER return type. PSS retains its existing public route and
Snowflake-affinity diagnostic for compatibility, not native Snowflake
certification; do not silently rename the function or delete tests.

Snowflake `SPLIT(string, separator)` uses a required substring separator;
`STRTOK_TO_ARRAY(string [, delimiter])` instead uses a delimiter character set
defaulting to a single space. The matrices cover delimiter omission for both
optional signatures, empty/repeated delimiters and NULL inputs. Omitted
delimiters remain omitted in the AST; database defaults/results are not evaluated.

Decision: D3.8 will explicitly cover all four names, in separate bounded
per-function steps. Preserve the existing `SPLIT_TO_ARRAY` behavior/diagnostics
for compatibility; do not treat it as a replacement for documented `SPLIT`.
The D0 lexer step added `SPLIT` before `Identifier` while reusing the three
existing conversion/predicate tokens. Its manual golden-repair gate is complete
in `641ded6`; this batch makes no further lexer or token-ID changes.

## T021 / D-SIG: completed signature-validation policy

Completed 2026-11-18 against the official signatures linked below, the
[grammar](../src/main/antlr4/sql/SQLSelectParser.g4#L1320), its
[generic routine fallback](../src/main/antlr4/sql/SQLSelectParser.g4#L2348),
and exact tests in the
[focused host](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java).
The 134 new methods preserve all 890 pre-existing methods byte-for-byte.
No production code, lexer rule, token identifier or existing golden changed.
Maven passed **1,024/1,024**, zero failures/errors/skips, exit 0; VS Code
independently passed **1,024/1,024**.

### Recognized syntax is not database signature certification

- T accepts exactly two or three expressions for every T name. Consequently
  `ARRAY_SLICE(array, start)` and three-argument `ARRAY_REPEAT` /
  `ARRAY_TO_STRING` remain recognized T syntax, not native-valid signatures.
  Other nonempty arities use generic routine invocation.
- U accepts exactly one expression. All other nonempty arities remain generic,
  including the **documented, valid two-/three-argument `ARRAY_SORT` forms**.
  Those forms are not credited as dedicated U coverage. Sixteen new controls
  independently isolate both optional flags for resolution, ambiguity,
  missing/hidden aliases, predicand/column substitutions and NULL.
- C/R/P/Z accept a nonempty expression list, even outside a function's
  documented arity. This is existing compatibility, not a new arity validator.
  `ARRAYS_ZIP` has no documented finite maximum; its one-/multiple-input
  matrices, including the fourth variadic operand, remain the evidence.
- Empty argument lists for all 21 names use the existing generic route.
  They retain the legacy raw name-node AST shape and emit no Snowflake-affinity
  warning. This explicitly preserves observable behavior; it does not certify
  a native zero-argument overload or introduce canonical AST normalization.
- Schema-qualified names remain generic UDFs, without Snowflake affinity.
  Existing per-function UDF controls are supplemented by six five-argument
  UDF probes, one per dedicated family; SQL syntax is still checked.
- Missing/trailing commas and missing closing parentheses are parser errors.
  Exact controls compare error counts and every diagnostic field in order,
  without walking rejected input. All 21 names now have at least two such
  controls. Eleven new methods close the prior SORT/MAX/MIN/REVERSE/SUM gaps;
  six more cover malformed schema-qualified calls.
- Nonempty, unqualified recognized calls retain existing statement-scoped
  affinity diagnostics. Wrong native arity/type alone adds no new diagnostic.
  Complete lists preserve the actual first Snowflake feature's warning
  position, including an array literal when it precedes the function warning.
  Existing syntax diagnostic classification strings are likewise preserved.

Every new non-syntax method asserts its primary grammar-rule count is one
and each of the other six family/generic counts is zero, then compares all
six complete extractor outputs and the complete diagnostic list. No fallback
route can pass as dedicated coverage. The reusable
[matrix toolkit](../../tools/array-matrix/README.md) now supports per-case
primary rules plus additional exact rule counts, including zero.

### All 21 documented forms and deferred runtime obligations

Bracketed arguments below are optional database arguments, not arguments
inserted into the AST. T/U/C/R/P/Z refer to the route table above; G means
`routine_invocation`. Zero arguments and schema-qualified calls use G for
every row. The listed runtime requirements/defaults are documentation only:
PSS does not execute calls, infer database column types or certify results.

| Function and official signature | Dedicated arities | Optional nonempty forms | Database responsibility / semantic boundary |
|---|---|---|---|
| [ARRAY_TO_STRING(array, separator)](https://docs.snowflake.com/en/sql-reference/functions/array_to_string) | T: 2, 3 | None documented | ARRAY input and string separator; the third argument is compatibility syntax only |
| [ARRAY_SLICE(array, from, to)](https://docs.snowflake.com/en/sql-reference/functions/array_slice) | T: 2, 3 | None documented | ARRAY input, integer bounds and evaluated slice; two arguments are compatibility syntax only |
| [ARRAY_REPEAT(element, count)](https://docs.snowflake.com/en/sql-reference/functions/array_repeat) | T: 2, 3 | None documented | Integer count, supported element types and nonpositive-count result |
| [ARRAY_DISTINCT(array)](https://docs.snowflake.com/en/sql-reference/functions/array_distinct) | U: 1 | None | Input type, equality and duplicate/null behavior |
| [ARRAY_COMPACT(array)](https://docs.snowflake.com/en/sql-reference/functions/array_compact) | U: 1 | None | Input type and removal of missing/null elements |
| [ARRAY_SIZE(array_or_variant)](https://docs.snowflake.com/en/sql-reference/functions/array_size) | U: 1 | None | ARRAY/VARIANT contents, sparse size and non-array VARIANT result |
| [ARRAY_SORT(array [, ascending [, nulls_first]])](https://docs.snowflake.com/en/sql-reference/functions/array_sort) | U: 1 | 2, 3: **G** | Boolean flags; ascending defaults TRUE, nulls_first defaults FALSE ascending / TRUE descending; SQL/JSON null ordering |
| [ARRAY_MAX(array)](https://docs.snowflake.com/en/sql-reference/functions/array_max) | U: 1 | None | Element comparability and null/result semantics |
| [ARRAY_MIN(array)](https://docs.snowflake.com/en/sql-reference/functions/array_min) | U: 1 | None | Element comparability and null/result semantics |
| [ARRAY_REVERSE(array)](https://docs.snowflake.com/en/sql-reference/functions/array_reverse) | U: 1 | None | Input ARRAY type and reversed result |
| [ARRAY_SUM(array)](https://docs.snowflake.com/en/sql-reference/functions/array_sum) | U: 1 | None | **Structured-array aggregate across rows**, not scalar element summation; FLOAT/DOUBLE/REAL structured element types and aggregate execution |
| [SPLIT_TO_ARRAY(string [, delimiter])](https://docs.aws.amazon.com/redshift/latest/dg/split_to_array.html) | C: any nonempty list | 2: C | Redshift provenance, delimiter defaults to comma, SUPER result; existing PSS Snowflake affinity is compatibility, not Snowflake certification |
| [STRTOK_TO_ARRAY(string [, delimiter])](https://docs.snowflake.com/en/sql-reference/functions/strtok_to_array) | C: any nonempty list | 2: C | Delimiter character set defaults to a single space; string/null/result semantics |
| [ARRAY_GENERATE_RANGE(start, stop [, step])](https://docs.snowflake.com/en/sql-reference/functions/array_generate_range) | R: any nonempty list | 3: R | Integer expressions; default step 1, step cannot be zero; range evaluation |
| [ARRAY_POSITION(value, array)](https://docs.snowflake.com/en/sql-reference/functions/array_position) | P: any nonempty list | None | Comparability/VARIANT rules and zero-based index/absent-value result |
| [ARRAYS_ZIP(array [, array ...])](https://docs.snowflake.com/en/sql-reference/functions/arrays_zip) | Z: any nonempty list | 2 or more: Z | Structured-type consistency, unequal lengths, null fill and materialized objects |
| [ARRAYS_TO_OBJECT(keys, values)](https://docs.snowflake.com/en/sql-reference/functions/arrays_to_object) | C: any nonempty list | None | String keys, equal lengths, supported input types and resulting object |
| [TO_ARRAY(expr)](https://docs.snowflake.com/en/sql-reference/functions/to_array) | C: any nonempty list | None | Any expression type; existing arrays retained, NULL/JSON null -> NULL, other values -> singleton |
| [AS_ARRAY(variant_expr)](https://docs.snowflake.com/en/sql-reference/functions/as_array) | C: any nonempty list | None | VARIANT contents and NULL result for non-array contents; structured inputs unsupported |
| [IS_ARRAY(variant_expr)](https://docs.snowflake.com/en/sql-reference/functions/is_array) | C: any nonempty list | None | VARIANT contents, boolean/null result and unsupported structured inputs |
| [SPLIT(string, separator)](https://docs.snowflake.com/en/sql-reference/functions/split) | C: any nonempty list | None; both required | Required substring separator, unlike STRTOK's delimiter character set |

`ARRAY_SUM`'s U classification describes its **one-argument parser route**,
not scalar execution semantics. Earlier "unary/scalar" grouping language must
not be read as a claim that Snowflake sums the elements inside one array.
Existing generic expression/placement behavior is preserved; this task neither
adds database aggregate/type analysis nor re-certifies the separate aggregate
slice.

For each row, new methods named
`arC56_signaturePolicy{CamelCaseFunction}{Case}V0Test` include `RequiredForm`,
`EmptyArgumentsCompatibility`, and `RuntimeShapeBoundary`, plus
`BelowDocumentedMinimum` for minima above one and `AboveDocumentedMaximum`
for finite maxima. Optional forms have `OptionalArity2` / `OptionalArity3`
controls as applicable. A `RuntimeShapeBoundary` query is a parse-policy
probe, not necessarily a native-invalid input: notably `TO_ARRAY(42)` is a
documented valid conversion. Full names for every new case and matrix cell
are recorded in the generated D-SIG method ledger below. Previously existing
per-function malformed cases are also indexed in the exact-syntax table below.
The generated ledger's `SLICE_D_SIGNATURE_POLICY` is a cross-cutting audit
owner, not a twenty-second SQL function.

### AR-M20..AR-M24 reconciliation

The starter allocation called these IDs signature/arity/type-shape diagnostics,
but historical D2.3/D2.4 records reused them for unresolved, ambiguous,
missing/hidden-alias, schema-qualified and mixed-dialect cases. Those records
remain historical resolution/dialect evidence; they did **not** already prove
the signature policy. T021 supplies the following explicitly scoped Slice D
evidence without renaming historical tests or reallocating Slice C's IDs:

| Slice D obligation | Named T021 evidence |
|---|---|
| AR-M20: documented required/optional forms and actual routing | All 21 `RequiredForm` cases; `OptionalArity2` / `OptionalArity3`; the 16 `ArraySortOptionalArg2...` / `ArraySortOptionalArg3...` controls |
| AR-M21: below-minimum and zero-argument boundary | All 21 `EmptyArgumentsCompatibility` cases and each applicable `BelowDocumentedMinimum`; accepted syntax is not native-valid arity |
| AR-M22: above-maximum and generic/schema-qualified compatibility | Every finite-maximum `AboveDocumentedMaximum`, six `QualifiedArityCompatibility` cases, and retained per-function UDF/variadic ZIP controls |
| AR-M23: exact malformed-syntax rejection | Existing per-function controls plus the 11 new five-function controls and six `QualifiedTrailingCommaSyntax` cases; no walker on rejected syntax |
| AR-M24: type/value/runtime boundary, not database evaluation | All 21 `RuntimeShapeBoundary` cases, optional-flag substitution typing and retained zero-step/unequal-length/null controls |

This closes REQ-003 / D-SIG. The subsequent
[T022 audit](#t022--d-audit-completed-cell-level-reconciliation) separately
closes the required matrix cells; T023's accuracy ladder and T024's final
accounting remain open.

## Former outside-D assignments, now consolidated into D

The original inventory has 34 names: the 17 assigned to D plus these 17 names.
These historical groupings explain where the omissions came from; they no
longer define exclusions from Slice D closure:

| Owner | Names | Audit disposition |
|---|---|---|
| Slice A / 4.2 / GC1 | `ARRAY_CONSTRUCT`, `ARRAY_CONSTRUCT_COMPACT`, `ARRAY_CONSTRUCT_STRUCTURED` | T025 completes the structured constructor FIRST, including its lexical gate. T032 verifies/reuses the other two matrices. |
| Entry 4.1 | `ARRAY_CONTAINS` | T026 completes the partial VARIANT/placement/operand work. |
| Slice B / 4.3, 4.6 | `ARRAY_AGG`, `ARRAY_UNION_AGG`, `ARRAY_UNIQUE_AGG` | T032 carries forward independently identified aggregate evidence. |
| Slice C / 4.7 | `ARRAY_CAT`, `ARRAY_EXCEPT`, `ARRAY_INTERSECTION`, `ARRAYS_OVERLAP` | T032 carries forward independently identified binary/set evidence. |
| Slice C / 4.8 | `ARRAY_APPEND`, `ARRAY_PREPEND`, `ARRAY_INSERT`, `ARRAY_REMOVE`, `ARRAY_REMOVE_AT` | T032 carries forward independently identified mutator evidence. |
| Slice E / 4.5 | `ARRAY_FLATTEN` | T027 closes the separate scalar-function requirement; the earlier audit found token/generic-name support but no registry entry or SQL-call tests. |

`FLATTEN` / `LATERAL FLATTEN` (including `LATERAL_FLATTEN` compatibility) and
`FILTER` / `TRANSFORM` / `REDUCE` transfer from E to **T028..T031 in D**.

The comparison accounts for the 32 `ARRAY_*`/`ARRAYS_*` names in the retrieved
Snowflake semi-structured list, `STRTOK_TO_ARRAY`, the four candidates above,
and the existing `SPLIT_TO_ARRAY` compatibility spelling: **38 distinct names**.
Adding canonical FLATTEN and the three higher-order names gives the current
**42-name** inventory above. No named array-function requirement found in
the audited planning/companion documents remains excluded from D closure.

## Executable work checklist

- [x] **D0.1..D0.5 FIRST:** freeze the 21-function inventory, capture official
  current and last-repaired-baseline constants, append only `SPLIT` before
  `Identifier`, regenerate, publish incremental and cumulative worklists, and
  pause for developer manual repairs before validating the existing focused
  host. Do not duplicate `TO_ARRAY`, `AS_ARRAY`, `IS_ARRAY`, or `ARRAY_REPEAT`.
  This completed historical gate does not clear T025's new GC1 lexical work.
  D0.1..D0.4 are complete: `SPLIT=456`, `Identifier=457`; generation/compilation
  and both official token maps are verified. D0.5 is complete in `641ded6`.
- [x] **D1.1 / GC2:** finish `ARRAY_REPEAT`'s strict matrix; preserve existing
  goldens except developer-directed manual token-ID edits. Completed with 48
  independent cases and a 304/304 focused-host pass; no production changes.
- [x] **D1.2.1:** complete `ARRAY_TO_STRING`: 45 independent cases, including
  exact diagnostics in all 15 retained methods; focused host 334/334. Fixed the
  missing parser nonreserved-name alternative without changing lexer IDs.
- [x] **D1.2.2:** complete `ARRAY_SLICE`: 55 independent cases, including exact
  diagnostics in all 12 retained methods; focused host 377/377. No production changes.
- [x] **D2.6.1..D2.6.3:** complete independent matrices for `ARRAY_DISTINCT`,
  `ARRAY_COMPACT`, and `ARRAY_SIZE`.
- [x] **D3.8.1..D3.8.4, approved:** implement/verify `TO_ARRAY`, `AS_ARRAY`,
  `IS_ARRAY`, and `SPLIT`, one function per step under the strict full matrix.
- [x] **D3.9.1..D3.9.2:** complete the older `SPLIT_TO_ARRAY` and
  `STRTOK_TO_ARRAY` matrices, including their optional delimiter forms.
- [x] **D3.9.3..D3.9.5:** complete `ARRAY_GENERATE_RANGE`, `ARRAY_POSITION`,
  and `ARRAYS_ZIP` matrices.
- [x] **D-SIG / T021:** reconciled all 21 signatures, optional forms, exact
  malformed syntax and parser/runtime boundaries above. Added 134 controls;
  all 1,024 focused tests pass independently in Maven and VS Code.
- [x] **D-AUDIT / T022:** independently reconciled all 21 function rows to
  named source evidence for 854/854 required cells, including the six older
  expanded matrices, SORT optional flags and ZIP's fourth operand. Nine real
  gaps repaired; all 1,024 previous methods preserved byte-for-byte; no
  production or lexer changes. Maven and VS Code each pass 1,033/1,033.
- [ ] **D5.1 / T025 / GC1 NEXT:** structured constructor with complete lexical
  inventory, official named token-ID maps, developer manual repairs and matrix.
- [ ] **D5.2 / T026:** ARRAY_CONTAINS independent VARIANT/operand/full matrix.
- [ ] **D5.3 / T027:** scalar ARRAY_FLATTEN routing/affinity/full matrix.
- [ ] **D5.4 / T028:** advanced FLATTEN/lateral forms and compatibility spelling.
- [ ] **D5.5 / T029:** FILTER syntax/bindings/lineage/full higher-order matrix.
- [ ] **D5.6 / T030:** TRANSFORM independent higher-order matrix.
- [ ] **D5.7 / T031:** REDUCE independent array/initial-value/lambda matrix.
- [ ] **D5.8 / T032:** verify/reuse all 14 A/B/C completions plus literal,
  subscript and constructor snippet controls; fill only demonstrated gaps.
- [ ] **D5-SIG / T033:** all 42 signatures, routing and runtime boundaries.
- [ ] **D5-AUDIT / T034:** all 42 per-function matrices with named witnesses
  and justified family-specific inapplicability; zero unassigned required cells.
- [ ] **D4 / T023 final sign-off AFTER T034:** all affected family hosts, Phase15 derived-column gate,
  Smoketest quality gate, and full default parser accuracy suite, excluding
  opt-in slow/manual modes. Record actual pass/fail/skip counts and exit codes.
- [ ] **D4 / T024 AFTER successful T023:** update 4.1–4.12, GC1..GC4 and
  former E accounting for every named function. Slice F and whole-release
  certification remain separate.

Prior D2.5/D3.7 focused runs remain valid historical evidence, but are not proof
that the missing older per-function matrices were complete. This reconciliation
supersedes any broader completion inference from those records.

## External sources

Retrieved 2026-11-18:

- [Snowflake semi-structured and structured function list](https://docs.snowflake.com/en/sql-reference/functions-semistructured)
- [Snowflake ARRAY_TO_STRING signature and NULL/empty-input notes](https://docs.snowflake.com/en/sql-reference/functions/array_to_string)
- [TO_ARRAY](https://docs.snowflake.com/en/sql-reference/functions/to_array)
- [AS_ARRAY](https://docs.snowflake.com/en/sql-reference/functions/as_array)
- [IS_ARRAY](https://docs.snowflake.com/en/sql-reference/functions/is_array)
- [SPLIT](https://docs.snowflake.com/en/sql-reference/functions/split)
- [STRTOK_TO_ARRAY](https://docs.snowflake.com/en/sql-reference/functions/strtok_to_array)
- [Redshift SPLIT_TO_ARRAY](https://docs.aws.amazon.com/redshift/latest/dg/split_to_array.html)
- [ARRAY_REPEAT](https://docs.snowflake.com/en/sql-reference/functions/array_repeat)
- [ARRAY_GENERATE_RANGE](https://docs.snowflake.com/en/sql-reference/functions/array_generate_range)
- [ARRAY_POSITION](https://docs.snowflake.com/en/sql-reference/functions/array_position)
- [ARRAYS_ZIP](https://docs.snowflake.com/en/sql-reference/functions/arrays_zip)

## D2.6.1 ARRAY_DISTINCT method-level completion evidence

31 methods; 29 six-output goldens; 2 parser-only controls. Every method compares complete exact diagnostics. Existing extractor goldens are preserved.

| Matrix cell | Methods |
|---|---|
| placement:Select | `snowflakeArrayDistinctSelectPlacementV0Test` |
| arg1:Qualified | `snowflakeArrayDistinctSelectPlacementV0Test` |
| arg1:MissingAlias | `snowflakeArrayDistinctMissingAliasFatalV0Test` |
| placement:Where | `arC44_arrayDistinctWherePlacementV0Test` |
| placement:Join | `arC44_arrayDistinctJoinPlacementV0Test` |
| placement:Having | `arC44_arrayDistinctHavingPlacementV0Test` |
| placement:Qualify | `arC44_arrayDistinctQualifyPlacementV0Test` |
| placement:OrderBy | `arC44_arrayDistinctOrderByPlacementV0Test` |
| placement:Values | `arC44_arrayDistinctValuesPlacementV0Test` |
| placement:Insert | `arC44_arrayDistinctInsertPlacementV0Test` |
| placement:Update | `arC44_arrayDistinctUpdatePlacementV0Test` |
| placement:Delete | `arC44_arrayDistinctDeletePlacementV0Test` |
| placement:Ddl | `arC44_arrayDistinctDdlPlacementV0Test` |
| placement:Script | `arC44_arrayDistinctScriptPlacementV0Test` |
| arg1:Unqualified | `arC44_arrayDistinctArg1UnqualifiedV0Test` |
| arg1:Unresolved | `arC44_arrayDistinctArg1UnresolvedV0Test` |
| arg1:Ambiguous | `arC44_arrayDistinctArg1AmbiguousV0Test` |
| arg1:HiddenAlias | `arC44_arrayDistinctArg1HiddenAliasV0Test` |
| arg1:PredicandVariable | `arC44_arrayDistinctArg1PredicandVariableV0Test` |
| arg1:ColumnVariable | `arC44_arrayDistinctArg1ColumnVariableV0Test` |
| arg1:Null | `arC44_arrayDistinctArg1NullV0Test` |
| deep:nested | `arC44_arrayDistinctNestedExpressionV0Test` |
| deep:derived | `arC44_arrayDistinctDerivedOutputV0Test` |
| deep:setop | `arC44_arrayDistinctSetOpSiblingIsolationV0Test` |
| neutral:udf | `arC44_arrayDistinctQualifiedUdfNeutralV0Test` |
| neutral:column | `arC44_arrayDistinctSameNamedColumnNeutralV0Test` |
| dialect:uppercase | `arC44_arrayDistinctUppercaseV0Test` |
| dialect:mixed | `arC44_arrayDistinctMixedDialectV0Test` |
| syntax:TrailingComma | `arC44_arrayDistinctTrailingCommaSyntaxV0Test` |
| syntax:MissingRightParenthesis | `arC44_arrayDistinctMissingRightParenthesisSyntaxV0Test` |
| literal:empty-array | `arC44_arrayDistinctEmptyArrayLiteralV0Test` |
| literal:null-and-duplicates | `arC44_arrayDistinctLiteralArrayWithNullAndDuplicatesV0Test` |

## D2.6.2 ARRAY_COMPACT method-level completion evidence

31 methods; 29 six-output goldens; 2 parser-only controls. Every method compares complete exact diagnostics. Existing extractor goldens are preserved.

| Matrix cell | Methods |
|---|---|
| placement:Select | `snowflakeArrayCompactSelectPlacementV0Test` |
| arg1:Qualified | `snowflakeArrayCompactSelectPlacementV0Test` |
| arg1:MissingAlias | `snowflakeArrayCompactMissingAliasFatalV0Test` |
| placement:Where | `snowflakeArrayCompactWherePlacementV0Test` |
| placement:OrderBy | `snowflakeArrayCompactOrderByPlacementV0Test` |
| placement:Values | `snowflakeArrayCompactValuesPlacementV0Test` |
| placement:Insert | `snowflakeArrayCompactInsertPlacementV0Test` |
| placement:Update | `snowflakeArrayCompactUpdatePlacementV0Test` |
| placement:Delete | `snowflakeArrayCompactDeletePlacementV0Test` |
| placement:Join | `arC45_arrayCompactJoinPlacementV0Test` |
| placement:Having | `arC45_arrayCompactHavingPlacementV0Test` |
| placement:Qualify | `arC45_arrayCompactQualifyPlacementV0Test` |
| placement:Ddl | `arC45_arrayCompactDdlPlacementV0Test` |
| placement:Script | `arC45_arrayCompactScriptPlacementV0Test` |
| arg1:Unqualified | `arC45_arrayCompactArg1UnqualifiedV0Test` |
| arg1:Unresolved | `arC45_arrayCompactArg1UnresolvedV0Test` |
| arg1:Ambiguous | `arC45_arrayCompactArg1AmbiguousV0Test` |
| arg1:HiddenAlias | `arC45_arrayCompactArg1HiddenAliasV0Test` |
| arg1:PredicandVariable | `arC45_arrayCompactArg1PredicandVariableV0Test` |
| arg1:ColumnVariable | `arC45_arrayCompactArg1ColumnVariableV0Test` |
| arg1:Null | `arC45_arrayCompactArg1NullV0Test` |
| deep:nested | `arC45_arrayCompactNestedExpressionV0Test` |
| deep:derived | `arC45_arrayCompactDerivedOutputV0Test` |
| deep:setop | `arC45_arrayCompactSetOpSiblingIsolationV0Test` |
| neutral:udf | `arC45_arrayCompactQualifiedUdfNeutralV0Test` |
| neutral:column | `arC45_arrayCompactSameNamedColumnNeutralV0Test` |
| dialect:uppercase | `arC45_arrayCompactUppercaseV0Test` |
| dialect:mixed | `arC45_arrayCompactMixedDialectV0Test` |
| syntax:TrailingComma | `arC45_arrayCompactTrailingCommaSyntaxV0Test` |
| syntax:MissingRightParenthesis | `arC45_arrayCompactMissingRightParenthesisSyntaxV0Test` |
| literal:empty-array | `arC45_arrayCompactEmptyArrayLiteralV0Test` |
| literal:null-and-duplicates | `arC45_arrayCompactLiteralArrayWithNullAndDuplicatesV0Test` |

## D2.6.3 ARRAY_SIZE method-level completion evidence

31 methods; 29 six-output goldens; 2 parser-only controls. Every method compares complete exact diagnostics. Existing extractor goldens are preserved.

| Matrix cell | Methods |
|---|---|
| placement:Select | `snowflakeArraySizeSelectPlacementV0Test` |
| arg1:Qualified | `snowflakeArraySizeSelectPlacementV0Test` |
| arg1:MissingAlias | `snowflakeArraySizeMissingAliasFatalV0Test` |
| placement:Where | `snowflakeArraySizeWherePlacementV0Test` |
| placement:OrderBy | `snowflakeArraySizeOrderByPlacementV0Test` |
| placement:Values | `snowflakeArraySizeValuesPlacementV0Test` |
| placement:Insert | `snowflakeArraySizeInsertPlacementV0Test` |
| placement:Update | `snowflakeArraySizeUpdatePlacementV0Test` |
| placement:Delete | `snowflakeArraySizeDeletePlacementV0Test` |
| placement:Join | `arC46_arraySizeJoinPlacementV0Test` |
| placement:Having | `arC46_arraySizeHavingPlacementV0Test` |
| placement:Qualify | `arC46_arraySizeQualifyPlacementV0Test` |
| placement:Ddl | `arC46_arraySizeDdlPlacementV0Test` |
| placement:Script | `arC46_arraySizeScriptPlacementV0Test` |
| arg1:Unqualified | `arC46_arraySizeArg1UnqualifiedV0Test` |
| arg1:Unresolved | `arC46_arraySizeArg1UnresolvedV0Test` |
| arg1:Ambiguous | `arC46_arraySizeArg1AmbiguousV0Test` |
| arg1:HiddenAlias | `arC46_arraySizeArg1HiddenAliasV0Test` |
| arg1:PredicandVariable | `arC46_arraySizeArg1PredicandVariableV0Test` |
| arg1:ColumnVariable | `arC46_arraySizeArg1ColumnVariableV0Test` |
| arg1:Null | `arC46_arraySizeArg1NullV0Test` |
| deep:nested | `arC46_arraySizeNestedExpressionV0Test` |
| deep:derived | `arC46_arraySizeDerivedOutputV0Test` |
| deep:setop | `arC46_arraySizeSetOpSiblingIsolationV0Test` |
| neutral:udf | `arC46_arraySizeQualifiedUdfNeutralV0Test` |
| neutral:column | `arC46_arraySizeSameNamedColumnNeutralV0Test` |
| dialect:uppercase | `arC46_arraySizeUppercaseV0Test` |
| dialect:mixed | `arC46_arraySizeMixedDialectV0Test` |
| syntax:TrailingComma | `arC46_arraySizeTrailingCommaSyntaxV0Test` |
| syntax:MissingRightParenthesis | `arC46_arraySizeMissingRightParenthesisSyntaxV0Test` |
| literal:empty-array | `arC46_arraySizeEmptyArrayLiteralV0Test` |
| literal:null-and-duplicates | `arC46_arraySizeLiteralArrayWithNullAndDuplicatesV0Test` |

Focused host: 452 passed, zero failures/errors/skips; 435 six-output cases and 17 syntax controls.

## D3.8.1 TO_ARRAY method-level completion evidence

35 methods; 33 six-output goldens; 2 parser-only controls. Every method compares complete exact diagnostics. Existing extractor goldens are preserved.

| Matrix cell | Methods |
|---|---|
| placement:Select | `arC47_toArraySelectPlacementV0Test` |
| placement:Where | `arC47_toArrayWherePlacementV0Test` |
| placement:Join | `arC47_toArrayJoinPlacementV0Test` |
| placement:Having | `arC47_toArrayHavingPlacementV0Test` |
| placement:Qualify | `arC47_toArrayQualifyPlacementV0Test` |
| placement:OrderBy | `arC47_toArrayOrderByPlacementV0Test` |
| placement:Values | `arC47_toArrayValuesPlacementV0Test` |
| placement:Insert | `arC47_toArrayInsertPlacementV0Test` |
| placement:Update | `arC47_toArrayUpdatePlacementV0Test` |
| placement:Delete | `arC47_toArrayDeletePlacementV0Test` |
| placement:Ddl | `arC47_toArrayDdlPlacementV0Test` |
| placement:Script | `arC47_toArrayScriptPlacementV0Test` |
| arg1:Unqualified | `arC47_toArrayArg1UnqualifiedV0Test` |
| arg1:Unresolved | `arC47_toArrayArg1UnresolvedV0Test` |
| arg1:Ambiguous | `arC47_toArrayArg1AmbiguousV0Test` |
| arg1:MissingAlias | `arC47_toArrayArg1MissingAliasV0Test` |
| arg1:HiddenAlias | `arC47_toArrayArg1HiddenAliasV0Test` |
| arg1:PredicandVariable | `arC47_toArrayArg1PredicandVariableV0Test` |
| arg1:ColumnVariable | `arC47_toArrayArg1ColumnVariableV0Test` |
| arg1:Null | `arC47_toArrayArg1NullV0Test` |
| deep:nested | `arC47_toArrayNestedExpressionV0Test` |
| deep:derived | `arC47_toArrayDerivedOutputV0Test` |
| deep:setop | `arC47_toArraySetOpSiblingIsolationV0Test` |
| neutral:udf | `arC47_toArrayQualifiedUdfNeutralV0Test` |
| neutral:column | `arC47_toArraySameNamedColumnNeutralV0Test` |
| dialect:uppercase | `arC47_toArrayUppercaseV0Test` |
| dialect:mixed | `arC47_toArrayMixedDialectV0Test` |
| syntax:TrailingComma | `arC47_toArrayTrailingCommaSyntaxV0Test` |
| syntax:MissingRightParenthesis | `arC47_toArrayMissingRightParenthesisSyntaxV0Test` |
| input:variant-object | `arC47_toArrayVariantObjectInputV0Test` |
| input:empty-variant-array | `arC47_toArrayEmptyVariantArrayV0Test` |
| input:json-null | `arC47_toArrayJsonNullInputV0Test` |
| input:numeric-scalar | `arC47_toArrayNumericScalarInputV0Test` |
| input:delimited-string | `arC47_toArrayDelimitedStringInputV0Test` |
| input:variant-array | `arC47_toArrayVariantArrayInputV0Test` |

## D3.8.2 AS_ARRAY method-level completion evidence

32 methods; 30 six-output goldens; 2 parser-only controls. Every method compares complete exact diagnostics. Existing extractor goldens are preserved.

| Matrix cell | Methods |
|---|---|
| placement:Select | `arC48_asArraySelectPlacementV0Test` |
| placement:Where | `arC48_asArrayWherePlacementV0Test` |
| placement:Join | `arC48_asArrayJoinPlacementV0Test` |
| placement:Having | `arC48_asArrayHavingPlacementV0Test` |
| placement:Qualify | `arC48_asArrayQualifyPlacementV0Test` |
| placement:OrderBy | `arC48_asArrayOrderByPlacementV0Test` |
| placement:Values | `arC48_asArrayValuesPlacementV0Test` |
| placement:Insert | `arC48_asArrayInsertPlacementV0Test` |
| placement:Update | `arC48_asArrayUpdatePlacementV0Test` |
| placement:Delete | `arC48_asArrayDeletePlacementV0Test` |
| placement:Ddl | `arC48_asArrayDdlPlacementV0Test` |
| placement:Script | `arC48_asArrayScriptPlacementV0Test` |
| arg1:Unqualified | `arC48_asArrayArg1UnqualifiedV0Test` |
| arg1:Unresolved | `arC48_asArrayArg1UnresolvedV0Test` |
| arg1:Ambiguous | `arC48_asArrayArg1AmbiguousV0Test` |
| arg1:MissingAlias | `arC48_asArrayArg1MissingAliasV0Test` |
| arg1:HiddenAlias | `arC48_asArrayArg1HiddenAliasV0Test` |
| arg1:PredicandVariable | `arC48_asArrayArg1PredicandVariableV0Test` |
| arg1:ColumnVariable | `arC48_asArrayArg1ColumnVariableV0Test` |
| arg1:Null | `arC48_asArrayArg1NullV0Test` |
| deep:nested | `arC48_asArrayNestedExpressionV0Test` |
| deep:derived | `arC48_asArrayDerivedOutputV0Test` |
| deep:setop | `arC48_asArraySetOpSiblingIsolationV0Test` |
| neutral:udf | `arC48_asArrayQualifiedUdfNeutralV0Test` |
| neutral:column | `arC48_asArraySameNamedColumnNeutralV0Test` |
| dialect:uppercase | `arC48_asArrayUppercaseV0Test` |
| dialect:mixed | `arC48_asArrayMixedDialectV0Test` |
| syntax:TrailingComma | `arC48_asArrayTrailingCommaSyntaxV0Test` |
| syntax:MissingRightParenthesis | `arC48_asArrayMissingRightParenthesisSyntaxV0Test` |
| input:variant-object | `arC48_asArrayVariantObjectInputV0Test` |
| input:empty-variant-array | `arC48_asArrayEmptyVariantArrayV0Test` |
| input:json-null | `arC48_asArrayJsonNullInputV0Test` |

## D3.8.3 IS_ARRAY method-level completion evidence

37 methods; 35 six-output goldens; 2 parser-only controls. Every method compares complete exact diagnostics. Existing extractor goldens are preserved.

| Matrix cell | Methods |
|---|---|
| placement:Select | `arC49_isArraySelectPlacementV0Test` |
| placement:Where | `arC49_isArrayWherePlacementV0Test` |
| placement:Join | `arC49_isArrayJoinPlacementV0Test` |
| placement:Having | `arC49_isArrayHavingPlacementV0Test` |
| placement:Qualify | `arC49_isArrayQualifyPlacementV0Test` |
| placement:OrderBy | `arC49_isArrayOrderByPlacementV0Test` |
| placement:Values | `arC49_isArrayValuesPlacementV0Test` |
| placement:Insert | `arC49_isArrayInsertPlacementV0Test` |
| placement:Update | `arC49_isArrayUpdatePlacementV0Test` |
| placement:Delete | `arC49_isArrayDeletePlacementV0Test` |
| placement:Ddl | `arC49_isArrayDdlPlacementV0Test` |
| placement:Script | `arC49_isArrayScriptPlacementV0Test` |
| arg1:Unqualified | `arC49_isArrayArg1UnqualifiedV0Test` |
| arg1:Unresolved | `arC49_isArrayArg1UnresolvedV0Test` |
| arg1:Ambiguous | `arC49_isArrayArg1AmbiguousV0Test` |
| arg1:MissingAlias | `arC49_isArrayArg1MissingAliasV0Test` |
| arg1:HiddenAlias | `arC49_isArrayArg1HiddenAliasV0Test` |
| arg1:PredicandVariable | `arC49_isArrayArg1PredicandVariableV0Test` |
| arg1:ColumnVariable | `arC49_isArrayArg1ColumnVariableV0Test` |
| arg1:Null | `arC49_isArrayArg1NullV0Test` |
| deep:nested | `arC49_isArrayNestedExpressionV0Test` |
| deep:derived | `arC49_isArrayDerivedOutputV0Test` |
| deep:setop | `arC49_isArraySetOpSiblingIsolationV0Test` |
| neutral:udf | `arC49_isArrayQualifiedUdfNeutralV0Test` |
| neutral:column | `arC49_isArraySameNamedColumnNeutralV0Test` |
| dialect:uppercase | `arC49_isArrayUppercaseV0Test` |
| dialect:mixed | `arC49_isArrayMixedDialectV0Test` |
| syntax:TrailingComma | `arC49_isArrayTrailingCommaSyntaxV0Test` |
| syntax:MissingRightParenthesis | `arC49_isArrayMissingRightParenthesisSyntaxV0Test` |
| input:variant-object | `arC49_isArrayVariantObjectInputV0Test` |
| input:empty-variant-array | `arC49_isArrayEmptyVariantArrayV0Test` |
| input:json-null | `arC49_isArrayJsonNullInputV0Test` |
| predicate:where | `arC49_isArrayBooleanWhereV0Test` |
| predicate:join | `arC49_isArrayBooleanJoinV0Test` |
| predicate:having | `arC49_isArrayBooleanHavingV0Test` |
| predicate:qualify | `arC49_isArrayBooleanQualifyV0Test` |
| predicate:case | `arC49_isArrayBooleanCaseProjectionV0Test` |

Focused host: 556 passed, zero failures/errors/skips; 533 six-output cases and 23 syntax controls.

## D3.8.4 SPLIT method-level completion evidence

48 methods; 45 six-output goldens; 3 parser-only controls. Every method compares complete exact diagnostics. Existing extractor goldens are preserved.

| Matrix cell | Methods |
|---|---|
| placement:Select | `arC50_splitSelectPlacementV0Test` |
| placement:Where | `arC50_splitWherePlacementV0Test` |
| placement:Join | `arC50_splitJoinPlacementV0Test` |
| placement:Having | `arC50_splitHavingPlacementV0Test` |
| placement:Qualify | `arC50_splitQualifyPlacementV0Test` |
| placement:OrderBy | `arC50_splitOrderByPlacementV0Test` |
| placement:Values | `arC50_splitValuesPlacementV0Test` |
| placement:Insert | `arC50_splitInsertPlacementV0Test` |
| placement:Update | `arC50_splitUpdatePlacementV0Test` |
| placement:Delete | `arC50_splitDeletePlacementV0Test` |
| placement:Ddl | `arC50_splitDdlPlacementV0Test` |
| placement:Script | `arC50_splitScriptPlacementV0Test` |
| arg1:Unqualified | `arC50_splitArg1UnqualifiedV0Test` |
| arg1:Unresolved | `arC50_splitArg1UnresolvedV0Test` |
| arg1:Ambiguous | `arC50_splitArg1AmbiguousV0Test` |
| arg1:MissingAlias | `arC50_splitArg1MissingAliasV0Test` |
| arg1:HiddenAlias | `arC50_splitArg1HiddenAliasV0Test` |
| arg1:PredicandVariable | `arC50_splitArg1PredicandVariableV0Test` |
| arg1:ColumnVariable | `arC50_splitArg1ColumnVariableV0Test` |
| arg1:Null | `arC50_splitArg1NullV0Test` |
| arg2:Unqualified | `arC50_splitArg2UnqualifiedV0Test` |
| arg2:Unresolved | `arC50_splitArg2UnresolvedV0Test` |
| arg2:Ambiguous | `arC50_splitArg2AmbiguousV0Test` |
| arg2:MissingAlias | `arC50_splitArg2MissingAliasV0Test` |
| arg2:HiddenAlias | `arC50_splitArg2HiddenAliasV0Test` |
| arg2:PredicandVariable | `arC50_splitArg2PredicandVariableV0Test` |
| arg2:ColumnVariable | `arC50_splitArg2ColumnVariableV0Test` |
| arg2:Null | `arC50_splitArg2NullV0Test` |
| combined:unqualified | `arC50_splitUnqualifiedOperandsV0Test` |
| combined:predicand | `arC50_splitAllPredicandVariablesV0Test` |
| combined:column | `arC50_splitAllColumnVariablesV0Test` |
| deep:nested | `arC50_splitNestedExpressionV0Test` |
| deep:derived | `arC50_splitDerivedOutputV0Test` |
| deep:setop | `arC50_splitSetOpSiblingIsolationV0Test` |
| neutral:udf | `arC50_splitQualifiedUdfNeutralV0Test` |
| neutral:column | `arC50_splitSameNamedColumnNeutralV0Test` |
| dialect:uppercase | `arC50_splitUppercaseV0Test` |
| dialect:mixed | `arC50_splitMixedDialectV0Test` |
| syntax:TrailingComma | `arC50_splitTrailingCommaSyntaxV0Test` |
| syntax:MissingRightParenthesis | `arC50_splitMissingRightParenthesisSyntaxV0Test` |
| syntax:MissingComma | `arC50_splitMissingCommaSyntaxV0Test` |
| input:empty-string | `arC50_splitEmptyStringV0Test` |
| input:empty-delimiter | `arC50_splitEmptyDelimiterV0Test` |
| input:repeated-delimiter | `arC50_splitRepeatedDelimiterV0Test` |
| input:delimiter-substring | `arC50_splitDelimiterSequenceV0Test` |
| input:both-null | `arC50_splitBothNullV0Test` |
| composition:subscript | `arC50_splitResultSubscriptV0Test` |
| composition:flatten | `arC50_splitFlattenResultV0Test` |

## D3.9.1 SPLIT_TO_ARRAY method-level completion evidence

53 methods; 50 six-output goldens; 3 parser-only controls. Every method compares complete exact diagnostics. Existing extractor goldens are preserved.

| Matrix cell | Methods |
|---|---|
| placement:Select | `snowflakeSplitToArraySelectSeedV0Test` |
| arg1:Qualified | `snowflakeSplitToArraySelectSeedV0Test` |
| arg2:Qualified | `snowflakeSplitToArraySelectSeedV0Test` |
| arg1:MissingAlias | `snowflakeSplitToArrayMissingAliasFatalV0Test` |
| placement:Where | `snowflakeSplitToArrayWherePlacementV0Test` |
| placement:OrderBy | `snowflakeSplitToArrayOrderByPlacementV0Test` |
| legacy:values-column-control | `snowflakeSplitToArrayValuesPlacementV0Test` |
| placement:Insert | `snowflakeSplitToArrayInsertPlacementV0Test` |
| placement:Update | `snowflakeSplitToArrayUpdatePlacementV0Test` |
| placement:Delete | `snowflakeSplitToArrayDeletePlacementV0Test` |
| placement:Join | `arC51_splitToArrayJoinPlacementV0Test` |
| placement:Having | `arC51_splitToArrayHavingPlacementV0Test` |
| placement:Qualify | `arC51_splitToArrayQualifyPlacementV0Test` |
| placement:Values | `arC51_splitToArrayValuesPlacementV0Test` |
| placement:Ddl | `arC51_splitToArrayDdlPlacementV0Test` |
| placement:Script | `arC51_splitToArrayScriptPlacementV0Test` |
| arg1:Unqualified | `arC51_splitToArrayArg1UnqualifiedV0Test` |
| arg1:Unresolved | `arC51_splitToArrayArg1UnresolvedV0Test` |
| arg1:Ambiguous | `arC51_splitToArrayArg1AmbiguousV0Test` |
| arg1:HiddenAlias | `arC51_splitToArrayArg1HiddenAliasV0Test` |
| arg1:PredicandVariable | `arC51_splitToArrayArg1PredicandVariableV0Test` |
| arg1:ColumnVariable | `arC51_splitToArrayArg1ColumnVariableV0Test` |
| arg1:Null | `arC51_splitToArrayArg1NullV0Test` |
| arg2:Unqualified | `arC51_splitToArrayArg2UnqualifiedV0Test` |
| arg2:Unresolved | `arC51_splitToArrayArg2UnresolvedV0Test` |
| arg2:Ambiguous | `arC51_splitToArrayArg2AmbiguousV0Test` |
| arg2:MissingAlias | `arC51_splitToArrayArg2MissingAliasV0Test` |
| arg2:HiddenAlias | `arC51_splitToArrayArg2HiddenAliasV0Test` |
| arg2:PredicandVariable | `arC51_splitToArrayArg2PredicandVariableV0Test` |
| arg2:ColumnVariable | `arC51_splitToArrayArg2ColumnVariableV0Test` |
| arg2:Null | `arC51_splitToArrayArg2NullV0Test` |
| combined:unqualified | `arC51_splitToArrayUnqualifiedOperandsV0Test` |
| combined:predicand | `arC51_splitToArrayAllPredicandVariablesV0Test` |
| combined:column | `arC51_splitToArrayAllColumnVariablesV0Test` |
| deep:nested | `arC51_splitToArrayNestedExpressionV0Test` |
| deep:derived | `arC51_splitToArrayDerivedOutputV0Test` |
| deep:setop | `arC51_splitToArraySetOpSiblingIsolationV0Test` |
| neutral:udf | `arC51_splitToArrayQualifiedUdfNeutralV0Test` |
| neutral:column | `arC51_splitToArraySameNamedColumnNeutralV0Test` |
| dialect:uppercase | `arC51_splitToArrayUppercaseV0Test` |
| dialect:mixed | `arC51_splitToArrayMixedDialectV0Test` |
| syntax:TrailingComma | `arC51_splitToArrayTrailingCommaSyntaxV0Test` |
| syntax:MissingRightParenthesis | `arC51_splitToArrayMissingRightParenthesisSyntaxV0Test` |
| syntax:MissingComma | `arC51_splitToArrayMissingCommaSyntaxV0Test` |
| input:empty-string | `arC51_splitToArrayEmptyStringV0Test` |
| input:empty-delimiter | `arC51_splitToArrayEmptyDelimiterV0Test` |
| input:repeated-delimiter | `arC51_splitToArrayRepeatedDelimiterV0Test` |
| input:delimiter-substring | `arC51_splitToArrayDelimiterSequenceV0Test` |
| input:both-null | `arC51_splitToArrayBothNullV0Test` |
| optional:qualified | `arC51_splitToArrayDefaultDelimiterQualifiedV0Test` |
| optional:unqualified | `arC51_splitToArrayDefaultDelimiterUnqualifiedV0Test` |
| optional:predicand | `arC51_splitToArrayDefaultDelimiterPredicandVariableV0Test` |
| optional:column | `arC51_splitToArrayDefaultDelimiterColumnVariableV0Test` |
| optional:null | `arC51_splitToArrayDefaultDelimiterNullV0Test` |
| optional:literal | `arC51_splitToArrayDefaultDelimiterLiteralV0Test` |

## D3.9.2 STRTOK_TO_ARRAY method-level completion evidence

53 methods; 50 six-output goldens; 3 parser-only controls. Every method compares complete exact diagnostics. Existing extractor goldens are preserved.

| Matrix cell | Methods |
|---|---|
| placement:Select | `snowflakeStrtokToArraySelectSeedV0Test` |
| arg1:Qualified | `snowflakeStrtokToArraySelectSeedV0Test` |
| arg2:Qualified | `snowflakeStrtokToArraySelectSeedV0Test` |
| arg1:MissingAlias | `snowflakeStrtokToArrayMissingAliasFatalV0Test` |
| placement:Where | `snowflakeStrtokToArrayWherePlacementV0Test` |
| placement:OrderBy | `snowflakeStrtokToArrayOrderByPlacementV0Test` |
| legacy:values-column-control | `snowflakeStrtokToArrayValuesPlacementV0Test` |
| placement:Insert | `snowflakeStrtokToArrayInsertPlacementV0Test` |
| placement:Update | `snowflakeStrtokToArrayUpdatePlacementV0Test` |
| placement:Delete | `snowflakeStrtokToArrayDeletePlacementV0Test` |
| placement:Join | `arC52_strtokToArrayJoinPlacementV0Test` |
| placement:Having | `arC52_strtokToArrayHavingPlacementV0Test` |
| placement:Qualify | `arC52_strtokToArrayQualifyPlacementV0Test` |
| placement:Values | `arC52_strtokToArrayValuesPlacementV0Test` |
| placement:Ddl | `arC52_strtokToArrayDdlPlacementV0Test` |
| placement:Script | `arC52_strtokToArrayScriptPlacementV0Test` |
| arg1:Unqualified | `arC52_strtokToArrayArg1UnqualifiedV0Test` |
| arg1:Unresolved | `arC52_strtokToArrayArg1UnresolvedV0Test` |
| arg1:Ambiguous | `arC52_strtokToArrayArg1AmbiguousV0Test` |
| arg1:HiddenAlias | `arC52_strtokToArrayArg1HiddenAliasV0Test` |
| arg1:PredicandVariable | `arC52_strtokToArrayArg1PredicandVariableV0Test` |
| arg1:ColumnVariable | `arC52_strtokToArrayArg1ColumnVariableV0Test` |
| arg1:Null | `arC52_strtokToArrayArg1NullV0Test` |
| arg2:Unqualified | `arC52_strtokToArrayArg2UnqualifiedV0Test` |
| arg2:Unresolved | `arC52_strtokToArrayArg2UnresolvedV0Test` |
| arg2:Ambiguous | `arC52_strtokToArrayArg2AmbiguousV0Test` |
| arg2:MissingAlias | `arC52_strtokToArrayArg2MissingAliasV0Test` |
| arg2:HiddenAlias | `arC52_strtokToArrayArg2HiddenAliasV0Test` |
| arg2:PredicandVariable | `arC52_strtokToArrayArg2PredicandVariableV0Test` |
| arg2:ColumnVariable | `arC52_strtokToArrayArg2ColumnVariableV0Test` |
| arg2:Null | `arC52_strtokToArrayArg2NullV0Test` |
| combined:unqualified | `arC52_strtokToArrayUnqualifiedOperandsV0Test` |
| combined:predicand | `arC52_strtokToArrayAllPredicandVariablesV0Test` |
| combined:column | `arC52_strtokToArrayAllColumnVariablesV0Test` |
| deep:nested | `arC52_strtokToArrayNestedExpressionV0Test` |
| deep:derived | `arC52_strtokToArrayDerivedOutputV0Test` |
| deep:setop | `arC52_strtokToArraySetOpSiblingIsolationV0Test` |
| neutral:udf | `arC52_strtokToArrayQualifiedUdfNeutralV0Test` |
| neutral:column | `arC52_strtokToArraySameNamedColumnNeutralV0Test` |
| dialect:uppercase | `arC52_strtokToArrayUppercaseV0Test` |
| dialect:mixed | `arC52_strtokToArrayMixedDialectV0Test` |
| syntax:TrailingComma | `arC52_strtokToArrayTrailingCommaSyntaxV0Test` |
| syntax:MissingRightParenthesis | `arC52_strtokToArrayMissingRightParenthesisSyntaxV0Test` |
| syntax:MissingComma | `arC52_strtokToArrayMissingCommaSyntaxV0Test` |
| input:empty-string | `arC52_strtokToArrayEmptyStringV0Test` |
| input:empty-delimiter | `arC52_strtokToArrayEmptyDelimiterV0Test` |
| input:repeated-delimiter | `arC52_strtokToArrayRepeatedDelimiterV0Test` |
| input:delimiter-set | `arC52_strtokToArrayDelimiterSequenceV0Test` |
| input:both-null | `arC52_strtokToArrayBothNullV0Test` |
| optional:qualified | `arC52_strtokToArrayDefaultDelimiterQualifiedV0Test` |
| optional:unqualified | `arC52_strtokToArrayDefaultDelimiterUnqualifiedV0Test` |
| optional:predicand | `arC52_strtokToArrayDefaultDelimiterPredicandVariableV0Test` |
| optional:column | `arC52_strtokToArrayDefaultDelimiterColumnVariableV0Test` |
| optional:null | `arC52_strtokToArrayDefaultDelimiterNullV0Test` |
| optional:literal | `arC52_strtokToArrayDefaultDelimiterLiteralV0Test` |

Focused host: 694 passed, zero failures/errors/skips; 662 six-output cases and 32 syntax controls.

## D3.9.3 ARRAY_GENERATE_RANGE method-level completion evidence

79 methods; 76 six-output goldens; 3 parser-only controls. Every method compares complete exact diagnostics. Existing extractor goldens are preserved.

| Matrix cell | Methods |
|---|---|
| form:DefaultStepLiteral:Select | `snowflakeArrayGenerateRangeSelectSeedV0Test` |
| form:DefaultStepLiteral:Where | `snowflakeArrayGenerateRangeWherePlacementV0Test` |
| form:DefaultStepLiteral:OrderBy | `snowflakeArrayGenerateRangeOrderByPlacementV0Test` |
| form:DefaultStepLiteral:Values | `snowflakeArrayGenerateRangeValuesPlacementV0Test` |
| form:DefaultStepLiteral:Insert | `snowflakeArrayGenerateRangeInsertPlacementV0Test` |
| form:DefaultStepLiteral:Update | `snowflakeArrayGenerateRangeUpdatePlacementV0Test` |
| form:DefaultStepLiteral:Delete | `snowflakeArrayGenerateRangeDeletePlacementV0Test` |
| placement:Select | `arC53_arrayGenerateRangeSelectPlacementV0Test` |
| placement:Where | `arC53_arrayGenerateRangeWherePlacementV0Test` |
| placement:Join | `arC53_arrayGenerateRangeJoinPlacementV0Test` |
| placement:Having | `arC53_arrayGenerateRangeHavingPlacementV0Test` |
| placement:Qualify | `arC53_arrayGenerateRangeQualifyPlacementV0Test` |
| placement:OrderBy | `arC53_arrayGenerateRangeOrderByPlacementV0Test` |
| placement:Values | `arC53_arrayGenerateRangeValuesPlacementV0Test` |
| placement:Insert | `arC53_arrayGenerateRangeInsertPlacementV0Test` |
| placement:Update | `arC53_arrayGenerateRangeUpdatePlacementV0Test` |
| placement:Delete | `arC53_arrayGenerateRangeDeletePlacementV0Test` |
| placement:Ddl | `arC53_arrayGenerateRangeDdlPlacementV0Test` |
| placement:Script | `arC53_arrayGenerateRangeScriptPlacementV0Test` |
| arg1:Unqualified | `arC53_arrayGenerateRangeArg1UnqualifiedV0Test` |
| arg1:Unresolved | `arC53_arrayGenerateRangeArg1UnresolvedV0Test` |
| arg1:Ambiguous | `arC53_arrayGenerateRangeArg1AmbiguousV0Test` |
| arg1:MissingAlias | `arC53_arrayGenerateRangeArg1MissingAliasV0Test` |
| arg1:HiddenAlias | `arC53_arrayGenerateRangeArg1HiddenAliasV0Test` |
| arg1:PredicandVariable | `arC53_arrayGenerateRangeArg1PredicandVariableV0Test` |
| arg1:ColumnVariable | `arC53_arrayGenerateRangeArg1ColumnVariableV0Test` |
| arg1:Null | `arC53_arrayGenerateRangeArg1NullV0Test` |
| arg2:Unqualified | `arC53_arrayGenerateRangeArg2UnqualifiedV0Test` |
| arg2:Unresolved | `arC53_arrayGenerateRangeArg2UnresolvedV0Test` |
| arg2:Ambiguous | `arC53_arrayGenerateRangeArg2AmbiguousV0Test` |
| arg2:MissingAlias | `arC53_arrayGenerateRangeArg2MissingAliasV0Test` |
| arg2:HiddenAlias | `arC53_arrayGenerateRangeArg2HiddenAliasV0Test` |
| arg2:PredicandVariable | `arC53_arrayGenerateRangeArg2PredicandVariableV0Test` |
| arg2:ColumnVariable | `arC53_arrayGenerateRangeArg2ColumnVariableV0Test` |
| arg2:Null | `arC53_arrayGenerateRangeArg2NullV0Test` |
| arg3:Unqualified | `arC53_arrayGenerateRangeArg3UnqualifiedV0Test` |
| arg3:Unresolved | `arC53_arrayGenerateRangeArg3UnresolvedV0Test` |
| arg3:Ambiguous | `arC53_arrayGenerateRangeArg3AmbiguousV0Test` |
| arg3:MissingAlias | `arC53_arrayGenerateRangeArg3MissingAliasV0Test` |
| arg3:HiddenAlias | `arC53_arrayGenerateRangeArg3HiddenAliasV0Test` |
| arg3:PredicandVariable | `arC53_arrayGenerateRangeArg3PredicandVariableV0Test` |
| arg3:ColumnVariable | `arC53_arrayGenerateRangeArg3ColumnVariableV0Test` |
| arg3:Null | `arC53_arrayGenerateRangeArg3NullV0Test` |
| combined:unqualified | `arC53_arrayGenerateRangeUnqualifiedOperandsV0Test` |
| combined:predicand | `arC53_arrayGenerateRangeAllPredicandVariablesV0Test` |
| combined:column | `arC53_arrayGenerateRangeAllColumnVariablesV0Test` |
| deep:nested | `arC53_arrayGenerateRangeNestedExpressionV0Test` |
| deep:derived | `arC53_arrayGenerateRangeDerivedOutputV0Test` |
| deep:setop | `arC53_arrayGenerateRangeSetOpSiblingIsolationV0Test` |
| neutral:udf | `arC53_arrayGenerateRangeQualifiedUdfNeutralV0Test` |
| neutral:column | `arC53_arrayGenerateRangeSameNamedColumnNeutralV0Test` |
| dialect:uppercase | `arC53_arrayGenerateRangeUppercaseV0Test` |
| dialect:mixed | `arC53_arrayGenerateRangeMixedDialectV0Test` |
| syntax:TrailingComma | `arC53_arrayGenerateRangeTrailingCommaSyntaxV0Test` |
| syntax:MissingRightParenthesis | `arC53_arrayGenerateRangeMissingRightParenthesisSyntaxV0Test` |
| syntax:MissingComma | `arC53_arrayGenerateRangeMissingCommaSyntaxV0Test` |
| form:DefaultStep:placement:Select | `arC53_arrayGenerateRangeDefaultStepSelectPlacementV0Test` |
| form:DefaultStep:arg1:Unqualified | `arC53_arrayGenerateRangeDefaultStepArg1UnqualifiedV0Test` |
| form:DefaultStep:arg1:PredicandVariable | `arC53_arrayGenerateRangeDefaultStepArg1PredicandVariableV0Test` |
| form:DefaultStep:arg1:ColumnVariable | `arC53_arrayGenerateRangeDefaultStepArg1ColumnVariableV0Test` |
| form:DefaultStep:arg1:Null | `arC53_arrayGenerateRangeDefaultStepArg1NullV0Test` |
| form:DefaultStep:arg2:Unqualified | `arC53_arrayGenerateRangeDefaultStepArg2UnqualifiedV0Test` |
| form:DefaultStep:arg2:PredicandVariable | `arC53_arrayGenerateRangeDefaultStepArg2PredicandVariableV0Test` |
| form:DefaultStep:arg2:ColumnVariable | `arC53_arrayGenerateRangeDefaultStepArg2ColumnVariableV0Test` |
| form:DefaultStep:arg2:Null | `arC53_arrayGenerateRangeDefaultStepArg2NullV0Test` |
| form:DefaultStep:combined:predicand | `arC53_arrayGenerateRangeDefaultStepAllPredicandVariablesV0Test` |
| form:DefaultStep:combined:column | `arC53_arrayGenerateRangeDefaultStepAllColumnVariablesV0Test` |
| form:DefaultStep:deep:nested | `arC53_arrayGenerateRangeDefaultStepNestedExpressionV0Test` |
| form:DefaultStep:deep:derived | `arC53_arrayGenerateRangeDefaultStepDerivedOutputV0Test` |
| form:DefaultStep:deep:setop | `arC53_arrayGenerateRangeDefaultStepSetOpSiblingIsolationV0Test` |
| input:positive-step | `arC53_arrayGenerateRangePositiveStepV0Test` |
| input:negative-step | `arC53_arrayGenerateRangeNegativeStepV0Test` |
| input:equal-bounds | `arC53_arrayGenerateRangeEqualBoundsV0Test` |
| input:positive-step-descending-bounds | `arC53_arrayGenerateRangePositiveStepDescendingBoundsV0Test` |
| input:negative-step-ascending-bounds | `arC53_arrayGenerateRangeNegativeStepAscendingBoundsV0Test` |
| boundary:zero-step-runtime-validation | `arC53_arrayGenerateRangeZeroStepRuntimeBoundaryV0Test` |
| input:integer-expressions | `arC53_arrayGenerateRangeArithmeticOperandsV0Test` |
| input:all-null | `arC53_arrayGenerateRangeAllNullV0Test` |
| form:DefaultStep:all-null | `arC53_arrayGenerateRangeDefaultStepAllNullV0Test` |

## D3.9.4 ARRAY_POSITION method-level completion evidence

50 methods; 47 six-output goldens; 3 parser-only controls. Every method compares complete exact diagnostics. Existing extractor goldens are preserved.

| Matrix cell | Methods |
|---|---|
| placement:Select | `snowflakeArrayPositionSelectSeedV0Test` |
| arg1:Qualified | `snowflakeArrayPositionSelectSeedV0Test` |
| arg2:Qualified | `snowflakeArrayPositionSelectSeedV0Test` |
| arg1:MissingAlias | `snowflakeArrayPositionMissingAliasFatalV0Test` |
| placement:Where | `snowflakeArrayPositionWherePlacementV0Test` |
| placement:OrderBy | `snowflakeArrayPositionOrderByPlacementV0Test` |
| form:LegacyValues:Values | `snowflakeArrayPositionValuesPlacementV0Test` |
| placement:Insert | `snowflakeArrayPositionInsertPlacementV0Test` |
| placement:Update | `snowflakeArrayPositionUpdatePlacementV0Test` |
| placement:Delete | `snowflakeArrayPositionDeletePlacementV0Test` |
| placement:Join | `arC54_arrayPositionJoinPlacementV0Test` |
| placement:Having | `arC54_arrayPositionHavingPlacementV0Test` |
| placement:Qualify | `arC54_arrayPositionQualifyPlacementV0Test` |
| placement:Values | `arC54_arrayPositionValuesPlacementV0Test` |
| placement:Ddl | `arC54_arrayPositionDdlPlacementV0Test` |
| placement:Script | `arC54_arrayPositionScriptPlacementV0Test` |
| arg1:Unqualified | `arC54_arrayPositionArg1UnqualifiedV0Test` |
| arg1:Unresolved | `arC54_arrayPositionArg1UnresolvedV0Test` |
| arg1:Ambiguous | `arC54_arrayPositionArg1AmbiguousV0Test` |
| arg1:HiddenAlias | `arC54_arrayPositionArg1HiddenAliasV0Test` |
| arg1:PredicandVariable | `arC54_arrayPositionArg1PredicandVariableV0Test` |
| arg1:ColumnVariable | `arC54_arrayPositionArg1ColumnVariableV0Test` |
| arg1:Null | `arC54_arrayPositionArg1NullV0Test` |
| arg2:Unqualified | `arC54_arrayPositionArg2UnqualifiedV0Test` |
| arg2:Unresolved | `arC54_arrayPositionArg2UnresolvedV0Test` |
| arg2:Ambiguous | `arC54_arrayPositionArg2AmbiguousV0Test` |
| arg2:MissingAlias | `arC54_arrayPositionArg2MissingAliasV0Test` |
| arg2:HiddenAlias | `arC54_arrayPositionArg2HiddenAliasV0Test` |
| arg2:PredicandVariable | `arC54_arrayPositionArg2PredicandVariableV0Test` |
| arg2:ColumnVariable | `arC54_arrayPositionArg2ColumnVariableV0Test` |
| arg2:Null | `arC54_arrayPositionArg2NullV0Test` |
| combined:unqualified | `arC54_arrayPositionUnqualifiedOperandsV0Test` |
| combined:predicand | `arC54_arrayPositionAllPredicandVariablesV0Test` |
| combined:column | `arC54_arrayPositionAllColumnVariablesV0Test` |
| deep:nested | `arC54_arrayPositionNestedExpressionV0Test` |
| deep:derived | `arC54_arrayPositionDerivedOutputV0Test` |
| deep:setop | `arC54_arrayPositionSetOpSiblingIsolationV0Test` |
| neutral:udf | `arC54_arrayPositionQualifiedUdfNeutralV0Test` |
| neutral:column | `arC54_arrayPositionSameNamedColumnNeutralV0Test` |
| dialect:uppercase | `arC54_arrayPositionUppercaseV0Test` |
| dialect:mixed | `arC54_arrayPositionMixedDialectV0Test` |
| syntax:TrailingComma | `arC54_arrayPositionTrailingCommaSyntaxV0Test` |
| syntax:MissingRightParenthesis | `arC54_arrayPositionMissingRightParenthesisSyntaxV0Test` |
| syntax:MissingComma | `arC54_arrayPositionMissingCommaSyntaxV0Test` |
| input:empty-array | `arC54_arrayPositionEmptyArrayV0Test` |
| input:first-element | `arC54_arrayPositionFirstElementV0Test` |
| input:duplicate-needle | `arC54_arrayPositionDuplicateNeedleV0Test` |
| input:absent-needle | `arC54_arrayPositionAbsentNeedleV0Test` |
| input:null-element-needle | `arC54_arrayPositionNullElementNeedleV0Test` |
| input:both-null | `arC54_arrayPositionBothNullV0Test` |
| input:needle-expression | `arC54_arrayPositionExpressionNeedleV0Test` |
| composition:case-projection | `arC54_arrayPositionCaseProjectionV0Test` |

## D3.9.5 ARRAYS_ZIP method-level completion evidence

90 methods; 87 six-output goldens; 3 parser-only controls. Every method compares complete exact diagnostics. Existing extractor goldens are preserved.

| Matrix cell | Methods |
|---|---|
| form:TwoArrays:Select | `snowflakeArraysZipSelectSeedV0Test` |
| form:TwoArrays:arg1:MissingAlias | `snowflakeArraysZipMissingAliasFatalV0Test` |
| form:TwoArrays:Where | `snowflakeArraysZipWherePlacementV0Test` |
| form:TwoArrays:OrderBy | `snowflakeArraysZipOrderByPlacementV0Test` |
| form:TwoArrays:Values | `snowflakeArraysZipValuesPlacementV0Test` |
| form:TwoArrays:Insert | `snowflakeArraysZipInsertPlacementV0Test` |
| form:TwoArrays:Update | `snowflakeArraysZipUpdatePlacementV0Test` |
| form:TwoArrays:Delete | `snowflakeArraysZipDeletePlacementV0Test` |
| placement:Select | `arC55_arraysZipSelectPlacementV0Test` |
| placement:Where | `arC55_arraysZipWherePlacementV0Test` |
| placement:Join | `arC55_arraysZipJoinPlacementV0Test` |
| placement:Having | `arC55_arraysZipHavingPlacementV0Test` |
| placement:Qualify | `arC55_arraysZipQualifyPlacementV0Test` |
| placement:OrderBy | `arC55_arraysZipOrderByPlacementV0Test` |
| placement:Values | `arC55_arraysZipValuesPlacementV0Test` |
| placement:Insert | `arC55_arraysZipInsertPlacementV0Test` |
| placement:Update | `arC55_arraysZipUpdatePlacementV0Test` |
| placement:Delete | `arC55_arraysZipDeletePlacementV0Test` |
| placement:Ddl | `arC55_arraysZipDdlPlacementV0Test` |
| placement:Script | `arC55_arraysZipScriptPlacementV0Test` |
| arg1:Unqualified | `arC55_arraysZipArg1UnqualifiedV0Test` |
| arg1:Unresolved | `arC55_arraysZipArg1UnresolvedV0Test` |
| arg1:Ambiguous | `arC55_arraysZipArg1AmbiguousV0Test` |
| arg1:MissingAlias | `arC55_arraysZipArg1MissingAliasV0Test` |
| arg1:HiddenAlias | `arC55_arraysZipArg1HiddenAliasV0Test` |
| arg1:PredicandVariable | `arC55_arraysZipArg1PredicandVariableV0Test` |
| arg1:ColumnVariable | `arC55_arraysZipArg1ColumnVariableV0Test` |
| arg1:Null | `arC55_arraysZipArg1NullV0Test` |
| arg2:Unqualified | `arC55_arraysZipArg2UnqualifiedV0Test` |
| arg2:Unresolved | `arC55_arraysZipArg2UnresolvedV0Test` |
| arg2:Ambiguous | `arC55_arraysZipArg2AmbiguousV0Test` |
| arg2:MissingAlias | `arC55_arraysZipArg2MissingAliasV0Test` |
| arg2:HiddenAlias | `arC55_arraysZipArg2HiddenAliasV0Test` |
| arg2:PredicandVariable | `arC55_arraysZipArg2PredicandVariableV0Test` |
| arg2:ColumnVariable | `arC55_arraysZipArg2ColumnVariableV0Test` |
| arg2:Null | `arC55_arraysZipArg2NullV0Test` |
| arg3:Unqualified | `arC55_arraysZipArg3UnqualifiedV0Test` |
| arg3:Unresolved | `arC55_arraysZipArg3UnresolvedV0Test` |
| arg3:Ambiguous | `arC55_arraysZipArg3AmbiguousV0Test` |
| arg3:MissingAlias | `arC55_arraysZipArg3MissingAliasV0Test` |
| arg3:HiddenAlias | `arC55_arraysZipArg3HiddenAliasV0Test` |
| arg3:PredicandVariable | `arC55_arraysZipArg3PredicandVariableV0Test` |
| arg3:ColumnVariable | `arC55_arraysZipArg3ColumnVariableV0Test` |
| arg3:Null | `arC55_arraysZipArg3NullV0Test` |
| combined:unqualified | `arC55_arraysZipUnqualifiedOperandsV0Test` |
| combined:predicand | `arC55_arraysZipAllPredicandVariablesV0Test` |
| combined:column | `arC55_arraysZipAllColumnVariablesV0Test` |
| deep:nested | `arC55_arraysZipNestedExpressionV0Test` |
| deep:derived | `arC55_arraysZipDerivedOutputV0Test` |
| deep:setop | `arC55_arraysZipSetOpSiblingIsolationV0Test` |
| neutral:udf | `arC55_arraysZipQualifiedUdfNeutralV0Test` |
| neutral:column | `arC55_arraysZipSameNamedColumnNeutralV0Test` |
| dialect:uppercase | `arC55_arraysZipUppercaseV0Test` |
| dialect:mixed | `arC55_arraysZipMixedDialectV0Test` |
| syntax:TrailingComma | `arC55_arraysZipTrailingCommaSyntaxV0Test` |
| syntax:MissingRightParenthesis | `arC55_arraysZipMissingRightParenthesisSyntaxV0Test` |
| syntax:MissingComma | `arC55_arraysZipMissingCommaSyntaxV0Test` |
| form:SingleArray:placement:Select | `arC55_arraysZipSingleArraySelectPlacementV0Test` |
| form:SingleArray:arg1:Unqualified | `arC55_arraysZipSingleArrayArg1UnqualifiedV0Test` |
| form:SingleArray:arg1:Unresolved | `arC55_arraysZipSingleArrayArg1UnresolvedV0Test` |
| form:SingleArray:arg1:Ambiguous | `arC55_arraysZipSingleArrayArg1AmbiguousV0Test` |
| form:SingleArray:arg1:MissingAlias | `arC55_arraysZipSingleArrayArg1MissingAliasV0Test` |
| form:SingleArray:arg1:HiddenAlias | `arC55_arraysZipSingleArrayArg1HiddenAliasV0Test` |
| form:SingleArray:arg1:PredicandVariable | `arC55_arraysZipSingleArrayArg1PredicandVariableV0Test` |
| form:SingleArray:arg1:ColumnVariable | `arC55_arraysZipSingleArrayArg1ColumnVariableV0Test` |
| form:SingleArray:arg1:Null | `arC55_arraysZipSingleArrayArg1NullV0Test` |
| form:SingleArray:deep:nested | `arC55_arraysZipSingleArrayNestedExpressionV0Test` |
| form:SingleArray:deep:derived | `arC55_arraysZipSingleArrayDerivedOutputV0Test` |
| form:SingleArray:deep:setop | `arC55_arraysZipSingleArraySetOpSiblingIsolationV0Test` |
| form:FourArrays:placement:Select | `arC55_arraysZipFourArraysSelectPlacementV0Test` |
| form:FourArrays:arg4:Unqualified | `arC55_arraysZipFourArraysArg4UnqualifiedV0Test` |
| form:FourArrays:arg4:Unresolved | `arC55_arraysZipFourArraysArg4UnresolvedV0Test` |
| form:FourArrays:arg4:Ambiguous | `arC55_arraysZipFourArraysArg4AmbiguousV0Test` |
| form:FourArrays:arg4:MissingAlias | `arC55_arraysZipFourArraysArg4MissingAliasV0Test` |
| form:FourArrays:arg4:HiddenAlias | `arC55_arraysZipFourArraysArg4HiddenAliasV0Test` |
| form:FourArrays:arg4:PredicandVariable | `arC55_arraysZipFourArraysArg4PredicandVariableV0Test` |
| form:FourArrays:arg4:ColumnVariable | `arC55_arraysZipFourArraysArg4ColumnVariableV0Test` |
| form:FourArrays:arg4:Null | `arC55_arraysZipFourArraysArg4NullV0Test` |
| form:FourArrays:combined:predicand | `arC55_arraysZipFourArraysAllPredicandVariablesV0Test` |
| form:FourArrays:combined:column | `arC55_arraysZipFourArraysAllColumnVariablesV0Test` |
| form:TwoArrays:arg2:MissingAlias | `arC55_arraysZipTwoArraysArg2MissingAliasV0Test` |
| form:TwoArrays:arg2:PredicandVariable | `arC55_arraysZipTwoArraysArg2PredicandVariableV0Test` |
| form:TwoArrays:arg2:ColumnVariable | `arC55_arraysZipTwoArraysArg2ColumnVariableV0Test` |
| input:unequal-lengths | `arC55_arraysZipUnequalLengthsV0Test` |
| input:all-empty | `arC55_arraysZipAllEmptyArraysV0Test` |
| input:first-empty | `arC55_arraysZipFirstArrayEmptyV0Test` |
| input:second-empty | `arC55_arraysZipSecondArrayEmptyV0Test` |
| input:third-empty | `arC55_arraysZipThirdArrayEmptyV0Test` |
| input:null-elements | `arC55_arraysZipNullElementsV0Test` |
| input:all-null | `arC55_arraysZipAllNullArraysV0Test` |

Focused host: 890 passed, zero failures/errors/skips; 849 six-output cases and 41 syntax controls.

## D-SIG SLICE_D_SIGNATURE_POLICY method-level completion evidence

134 methods; 117 six-output goldens; 17 parser-only controls. Every method compares complete exact diagnostics. Existing extractor goldens are preserved.

| Matrix cell | Methods |
|---|---|
| array_to_string:RequiredForm | `arC56_signaturePolicyArrayToStringRequiredFormV0Test` |
| array_to_string:EmptyArgumentsCompatibility | `arC56_signaturePolicyArrayToStringEmptyArgumentsCompatibilityV0Test` |
| array_to_string:BelowDocumentedMinimum | `arC56_signaturePolicyArrayToStringBelowDocumentedMinimumV0Test` |
| array_to_string:AboveDocumentedMaximum | `arC56_signaturePolicyArrayToStringAboveDocumentedMaximumV0Test` |
| array_to_string:RuntimeShapeBoundary | `arC56_signaturePolicyArrayToStringRuntimeShapeBoundaryV0Test` |
| array_slice:RequiredForm | `arC56_signaturePolicyArraySliceRequiredFormV0Test` |
| array_slice:EmptyArgumentsCompatibility | `arC56_signaturePolicyArraySliceEmptyArgumentsCompatibilityV0Test` |
| array_slice:BelowDocumentedMinimum | `arC56_signaturePolicyArraySliceBelowDocumentedMinimumV0Test` |
| array_slice:AboveDocumentedMaximum | `arC56_signaturePolicyArraySliceAboveDocumentedMaximumV0Test` |
| array_slice:RuntimeShapeBoundary | `arC56_signaturePolicyArraySliceRuntimeShapeBoundaryV0Test` |
| array_repeat:RequiredForm | `arC56_signaturePolicyArrayRepeatRequiredFormV0Test` |
| array_repeat:EmptyArgumentsCompatibility | `arC56_signaturePolicyArrayRepeatEmptyArgumentsCompatibilityV0Test` |
| array_repeat:BelowDocumentedMinimum | `arC56_signaturePolicyArrayRepeatBelowDocumentedMinimumV0Test` |
| array_repeat:AboveDocumentedMaximum | `arC56_signaturePolicyArrayRepeatAboveDocumentedMaximumV0Test` |
| array_repeat:RuntimeShapeBoundary | `arC56_signaturePolicyArrayRepeatRuntimeShapeBoundaryV0Test` |
| array_distinct:RequiredForm | `arC56_signaturePolicyArrayDistinctRequiredFormV0Test` |
| array_distinct:EmptyArgumentsCompatibility | `arC56_signaturePolicyArrayDistinctEmptyArgumentsCompatibilityV0Test` |
| array_distinct:AboveDocumentedMaximum | `arC56_signaturePolicyArrayDistinctAboveDocumentedMaximumV0Test` |
| array_distinct:RuntimeShapeBoundary | `arC56_signaturePolicyArrayDistinctRuntimeShapeBoundaryV0Test` |
| array_compact:RequiredForm | `arC56_signaturePolicyArrayCompactRequiredFormV0Test` |
| array_compact:EmptyArgumentsCompatibility | `arC56_signaturePolicyArrayCompactEmptyArgumentsCompatibilityV0Test` |
| array_compact:AboveDocumentedMaximum | `arC56_signaturePolicyArrayCompactAboveDocumentedMaximumV0Test` |
| array_compact:RuntimeShapeBoundary | `arC56_signaturePolicyArrayCompactRuntimeShapeBoundaryV0Test` |
| array_size:RequiredForm | `arC56_signaturePolicyArraySizeRequiredFormV0Test` |
| array_size:EmptyArgumentsCompatibility | `arC56_signaturePolicyArraySizeEmptyArgumentsCompatibilityV0Test` |
| array_size:AboveDocumentedMaximum | `arC56_signaturePolicyArraySizeAboveDocumentedMaximumV0Test` |
| array_size:RuntimeShapeBoundary | `arC56_signaturePolicyArraySizeRuntimeShapeBoundaryV0Test` |
| array_sort:RequiredForm | `arC56_signaturePolicyArraySortRequiredFormV0Test` |
| array_sort:EmptyArgumentsCompatibility | `arC56_signaturePolicyArraySortEmptyArgumentsCompatibilityV0Test` |
| array_sort:AboveDocumentedMaximum | `arC56_signaturePolicyArraySortAboveDocumentedMaximumV0Test` |
| array_sort:RuntimeShapeBoundary | `arC56_signaturePolicyArraySortRuntimeShapeBoundaryV0Test` |
| array_max:RequiredForm | `arC56_signaturePolicyArrayMaxRequiredFormV0Test` |
| array_max:EmptyArgumentsCompatibility | `arC56_signaturePolicyArrayMaxEmptyArgumentsCompatibilityV0Test` |
| array_max:AboveDocumentedMaximum | `arC56_signaturePolicyArrayMaxAboveDocumentedMaximumV0Test` |
| array_max:RuntimeShapeBoundary | `arC56_signaturePolicyArrayMaxRuntimeShapeBoundaryV0Test` |
| array_min:RequiredForm | `arC56_signaturePolicyArrayMinRequiredFormV0Test` |
| array_min:EmptyArgumentsCompatibility | `arC56_signaturePolicyArrayMinEmptyArgumentsCompatibilityV0Test` |
| array_min:AboveDocumentedMaximum | `arC56_signaturePolicyArrayMinAboveDocumentedMaximumV0Test` |
| array_min:RuntimeShapeBoundary | `arC56_signaturePolicyArrayMinRuntimeShapeBoundaryV0Test` |
| array_reverse:RequiredForm | `arC56_signaturePolicyArrayReverseRequiredFormV0Test` |
| array_reverse:EmptyArgumentsCompatibility | `arC56_signaturePolicyArrayReverseEmptyArgumentsCompatibilityV0Test` |
| array_reverse:AboveDocumentedMaximum | `arC56_signaturePolicyArrayReverseAboveDocumentedMaximumV0Test` |
| array_reverse:RuntimeShapeBoundary | `arC56_signaturePolicyArrayReverseRuntimeShapeBoundaryV0Test` |
| array_sum:RequiredForm | `arC56_signaturePolicyArraySumRequiredFormV0Test` |
| array_sum:EmptyArgumentsCompatibility | `arC56_signaturePolicyArraySumEmptyArgumentsCompatibilityV0Test` |
| array_sum:AboveDocumentedMaximum | `arC56_signaturePolicyArraySumAboveDocumentedMaximumV0Test` |
| array_sum:RuntimeShapeBoundary | `arC56_signaturePolicyArraySumRuntimeShapeBoundaryV0Test` |
| split_to_array:RequiredForm | `arC56_signaturePolicySplitToArrayRequiredFormV0Test` |
| split_to_array:EmptyArgumentsCompatibility | `arC56_signaturePolicySplitToArrayEmptyArgumentsCompatibilityV0Test` |
| split_to_array:AboveDocumentedMaximum | `arC56_signaturePolicySplitToArrayAboveDocumentedMaximumV0Test` |
| split_to_array:RuntimeShapeBoundary | `arC56_signaturePolicySplitToArrayRuntimeShapeBoundaryV0Test` |
| strtok_to_array:RequiredForm | `arC56_signaturePolicyStrtokToArrayRequiredFormV0Test` |
| strtok_to_array:EmptyArgumentsCompatibility | `arC56_signaturePolicyStrtokToArrayEmptyArgumentsCompatibilityV0Test` |
| strtok_to_array:AboveDocumentedMaximum | `arC56_signaturePolicyStrtokToArrayAboveDocumentedMaximumV0Test` |
| strtok_to_array:RuntimeShapeBoundary | `arC56_signaturePolicyStrtokToArrayRuntimeShapeBoundaryV0Test` |
| array_generate_range:RequiredForm | `arC56_signaturePolicyArrayGenerateRangeRequiredFormV0Test` |
| array_generate_range:EmptyArgumentsCompatibility | `arC56_signaturePolicyArrayGenerateRangeEmptyArgumentsCompatibilityV0Test` |
| array_generate_range:BelowDocumentedMinimum | `arC56_signaturePolicyArrayGenerateRangeBelowDocumentedMinimumV0Test` |
| array_generate_range:AboveDocumentedMaximum | `arC56_signaturePolicyArrayGenerateRangeAboveDocumentedMaximumV0Test` |
| array_generate_range:RuntimeShapeBoundary | `arC56_signaturePolicyArrayGenerateRangeRuntimeShapeBoundaryV0Test` |
| array_position:RequiredForm | `arC56_signaturePolicyArrayPositionRequiredFormV0Test` |
| array_position:EmptyArgumentsCompatibility | `arC56_signaturePolicyArrayPositionEmptyArgumentsCompatibilityV0Test` |
| array_position:BelowDocumentedMinimum | `arC56_signaturePolicyArrayPositionBelowDocumentedMinimumV0Test` |
| array_position:AboveDocumentedMaximum | `arC56_signaturePolicyArrayPositionAboveDocumentedMaximumV0Test` |
| array_position:RuntimeShapeBoundary | `arC56_signaturePolicyArrayPositionRuntimeShapeBoundaryV0Test` |
| arrays_zip:RequiredForm | `arC56_signaturePolicyArraysZipRequiredFormV0Test` |
| arrays_zip:EmptyArgumentsCompatibility | `arC56_signaturePolicyArraysZipEmptyArgumentsCompatibilityV0Test` |
| arrays_zip:RuntimeShapeBoundary | `arC56_signaturePolicyArraysZipRuntimeShapeBoundaryV0Test` |
| arrays_to_object:RequiredForm | `arC56_signaturePolicyArraysToObjectRequiredFormV0Test` |
| arrays_to_object:EmptyArgumentsCompatibility | `arC56_signaturePolicyArraysToObjectEmptyArgumentsCompatibilityV0Test` |
| arrays_to_object:BelowDocumentedMinimum | `arC56_signaturePolicyArraysToObjectBelowDocumentedMinimumV0Test` |
| arrays_to_object:AboveDocumentedMaximum | `arC56_signaturePolicyArraysToObjectAboveDocumentedMaximumV0Test` |
| arrays_to_object:RuntimeShapeBoundary | `arC56_signaturePolicyArraysToObjectRuntimeShapeBoundaryV0Test` |
| to_array:RequiredForm | `arC56_signaturePolicyToArrayRequiredFormV0Test` |
| to_array:EmptyArgumentsCompatibility | `arC56_signaturePolicyToArrayEmptyArgumentsCompatibilityV0Test` |
| to_array:AboveDocumentedMaximum | `arC56_signaturePolicyToArrayAboveDocumentedMaximumV0Test` |
| to_array:RuntimeShapeBoundary | `arC56_signaturePolicyToArrayRuntimeShapeBoundaryV0Test` |
| as_array:RequiredForm | `arC56_signaturePolicyAsArrayRequiredFormV0Test` |
| as_array:EmptyArgumentsCompatibility | `arC56_signaturePolicyAsArrayEmptyArgumentsCompatibilityV0Test` |
| as_array:AboveDocumentedMaximum | `arC56_signaturePolicyAsArrayAboveDocumentedMaximumV0Test` |
| as_array:RuntimeShapeBoundary | `arC56_signaturePolicyAsArrayRuntimeShapeBoundaryV0Test` |
| is_array:RequiredForm | `arC56_signaturePolicyIsArrayRequiredFormV0Test` |
| is_array:EmptyArgumentsCompatibility | `arC56_signaturePolicyIsArrayEmptyArgumentsCompatibilityV0Test` |
| is_array:AboveDocumentedMaximum | `arC56_signaturePolicyIsArrayAboveDocumentedMaximumV0Test` |
| is_array:RuntimeShapeBoundary | `arC56_signaturePolicyIsArrayRuntimeShapeBoundaryV0Test` |
| split:RequiredForm | `arC56_signaturePolicySplitRequiredFormV0Test` |
| split:EmptyArgumentsCompatibility | `arC56_signaturePolicySplitEmptyArgumentsCompatibilityV0Test` |
| split:BelowDocumentedMinimum | `arC56_signaturePolicySplitBelowDocumentedMinimumV0Test` |
| split:AboveDocumentedMaximum | `arC56_signaturePolicySplitAboveDocumentedMaximumV0Test` |
| split:RuntimeShapeBoundary | `arC56_signaturePolicySplitRuntimeShapeBoundaryV0Test` |
| array_sort:OptionalArity2 | `arC56_signaturePolicyArraySortOptionalArity2V0Test` |
| array_sort:OptionalArity3 | `arC56_signaturePolicyArraySortOptionalArity3V0Test` |
| split_to_array:OptionalArity2 | `arC56_signaturePolicySplitToArrayOptionalArity2V0Test` |
| strtok_to_array:OptionalArity2 | `arC56_signaturePolicyStrtokToArrayOptionalArity2V0Test` |
| array_generate_range:OptionalArity3 | `arC56_signaturePolicyArrayGenerateRangeOptionalArity3V0Test` |
| array_sort:optional:arg2:Unqualified | `arC56_signaturePolicyArraySortOptionalArg2UnqualifiedV0Test` |
| array_sort:optional:arg2:Unresolved | `arC56_signaturePolicyArraySortOptionalArg2UnresolvedV0Test` |
| array_sort:optional:arg2:Ambiguous | `arC56_signaturePolicyArraySortOptionalArg2AmbiguousV0Test` |
| array_sort:optional:arg2:MissingAlias | `arC56_signaturePolicyArraySortOptionalArg2MissingAliasV0Test` |
| array_sort:optional:arg2:HiddenAlias | `arC56_signaturePolicyArraySortOptionalArg2HiddenAliasV0Test` |
| array_sort:optional:arg2:PredicandVariable | `arC56_signaturePolicyArraySortOptionalArg2PredicandVariableV0Test` |
| array_sort:optional:arg2:ColumnVariable | `arC56_signaturePolicyArraySortOptionalArg2ColumnVariableV0Test` |
| array_sort:optional:arg2:Null | `arC56_signaturePolicyArraySortOptionalArg2NullV0Test` |
| array_sort:optional:arg3:Unqualified | `arC56_signaturePolicyArraySortOptionalArg3UnqualifiedV0Test` |
| array_sort:optional:arg3:Unresolved | `arC56_signaturePolicyArraySortOptionalArg3UnresolvedV0Test` |
| array_sort:optional:arg3:Ambiguous | `arC56_signaturePolicyArraySortOptionalArg3AmbiguousV0Test` |
| array_sort:optional:arg3:MissingAlias | `arC56_signaturePolicyArraySortOptionalArg3MissingAliasV0Test` |
| array_sort:optional:arg3:HiddenAlias | `arC56_signaturePolicyArraySortOptionalArg3HiddenAliasV0Test` |
| array_sort:optional:arg3:PredicandVariable | `arC56_signaturePolicyArraySortOptionalArg3PredicandVariableV0Test` |
| array_sort:optional:arg3:ColumnVariable | `arC56_signaturePolicyArraySortOptionalArg3ColumnVariableV0Test` |
| array_sort:optional:arg3:Null | `arC56_signaturePolicyArraySortOptionalArg3NullV0Test` |
| array_sort:syntax:TrailingComma | `arC56_signaturePolicyArraySortTrailingCommaSyntaxV0Test` |
| array_sort:syntax:MissingRightParenthesis | `arC56_signaturePolicyArraySortMissingRightParenthesisSyntaxV0Test` |
| array_sort:syntax:MissingComma | `arC56_signaturePolicyArraySortMissingCommaSyntaxV0Test` |
| array_max:syntax:TrailingComma | `arC56_signaturePolicyArrayMaxTrailingCommaSyntaxV0Test` |
| array_max:syntax:MissingRightParenthesis | `arC56_signaturePolicyArrayMaxMissingRightParenthesisSyntaxV0Test` |
| array_min:syntax:TrailingComma | `arC56_signaturePolicyArrayMinTrailingCommaSyntaxV0Test` |
| array_min:syntax:MissingRightParenthesis | `arC56_signaturePolicyArrayMinMissingRightParenthesisSyntaxV0Test` |
| array_reverse:syntax:TrailingComma | `arC56_signaturePolicyArrayReverseTrailingCommaSyntaxV0Test` |
| array_reverse:syntax:MissingRightParenthesis | `arC56_signaturePolicyArrayReverseMissingRightParenthesisSyntaxV0Test` |
| array_sum:syntax:TrailingComma | `arC56_signaturePolicyArraySumTrailingCommaSyntaxV0Test` |
| array_sum:syntax:MissingRightParenthesis | `arC56_signaturePolicyArraySumMissingRightParenthesisSyntaxV0Test` |
| array_repeat:QualifiedArityCompatibility | `arC56_signaturePolicyArrayRepeatQualifiedArityCompatibilityV0Test` |
| array_repeat:qualified-malformed-comma | `arC56_signaturePolicyArrayRepeatQualifiedTrailingCommaSyntaxV0Test` |
| array_size:QualifiedArityCompatibility | `arC56_signaturePolicyArraySizeQualifiedArityCompatibilityV0Test` |
| array_size:qualified-malformed-comma | `arC56_signaturePolicyArraySizeQualifiedTrailingCommaSyntaxV0Test` |
| split:QualifiedArityCompatibility | `arC56_signaturePolicySplitQualifiedArityCompatibilityV0Test` |
| split:qualified-malformed-comma | `arC56_signaturePolicySplitQualifiedTrailingCommaSyntaxV0Test` |
| array_generate_range:QualifiedArityCompatibility | `arC56_signaturePolicyArrayGenerateRangeQualifiedArityCompatibilityV0Test` |
| array_generate_range:qualified-malformed-comma | `arC56_signaturePolicyArrayGenerateRangeQualifiedTrailingCommaSyntaxV0Test` |
| array_position:QualifiedArityCompatibility | `arC56_signaturePolicyArrayPositionQualifiedArityCompatibilityV0Test` |
| array_position:qualified-malformed-comma | `arC56_signaturePolicyArrayPositionQualifiedTrailingCommaSyntaxV0Test` |
| arrays_zip:QualifiedArityCompatibility | `arC56_signaturePolicyArraysZipQualifiedArityCompatibilityV0Test` |
| arrays_zip:qualified-malformed-comma | `arC56_signaturePolicyArraysZipQualifiedTrailingCommaSyntaxV0Test` |

Focused host: 1024 passed, zero failures/errors/skips; 966 six-output cases and 58 syntax controls.

## D-SIG exact-syntax evidence across all 21 names

All methods below are in the [focused host](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java). Each compares the exact parser error count and complete positioned diagnostic list; rejected syntax is not walked.

| Function | Retained exact-syntax methods | Additional T021 exact-syntax methods |
|---|---|---|
| `ARRAY_TO_STRING` | `arC42_arrayToStringTrailingCommaSyntaxV0Test`, `arC42_arrayToStringMissingCommaSyntaxV0Test`, `arC42_arrayToStringMissingRightParenthesisSyntaxV0Test` | Retained controls suffice |
| `ARRAY_SLICE` | `arC43_arraySliceTrailingCommaSyntaxV0Test`, `arC43_arraySliceMissingCommaSyntaxV0Test`, `arC43_arraySliceMissingRightParenthesisSyntaxV0Test` | Retained controls suffice |
| `ARRAY_REPEAT` | `arC41_arrayRepeatTrailingCommaSyntaxV0Test`, `arC41_arrayRepeatMissingCommaSyntaxV0Test`, `arC41_arrayRepeatMissingRightParenthesisSyntaxV0Test` | `arC56_signaturePolicyArrayRepeatQualifiedTrailingCommaSyntaxV0Test` |
| `ARRAY_DISTINCT` | `arC44_arrayDistinctTrailingCommaSyntaxV0Test`, `arC44_arrayDistinctMissingRightParenthesisSyntaxV0Test` | Retained controls suffice |
| `ARRAY_COMPACT` | `arC45_arrayCompactTrailingCommaSyntaxV0Test`, `arC45_arrayCompactMissingRightParenthesisSyntaxV0Test` | Retained controls suffice |
| `ARRAY_SIZE` | `arC46_arraySizeTrailingCommaSyntaxV0Test`, `arC46_arraySizeMissingRightParenthesisSyntaxV0Test` | `arC56_signaturePolicyArraySizeQualifiedTrailingCommaSyntaxV0Test` |
| `ARRAY_SORT` | None before T021 | `arC56_signaturePolicyArraySortTrailingCommaSyntaxV0Test`, `arC56_signaturePolicyArraySortMissingRightParenthesisSyntaxV0Test`, `arC56_signaturePolicyArraySortMissingCommaSyntaxV0Test` |
| `ARRAY_MAX` | None before T021 | `arC56_signaturePolicyArrayMaxTrailingCommaSyntaxV0Test`, `arC56_signaturePolicyArrayMaxMissingRightParenthesisSyntaxV0Test` |
| `ARRAY_MIN` | None before T021 | `arC56_signaturePolicyArrayMinTrailingCommaSyntaxV0Test`, `arC56_signaturePolicyArrayMinMissingRightParenthesisSyntaxV0Test` |
| `ARRAY_REVERSE` | None before T021 | `arC56_signaturePolicyArrayReverseTrailingCommaSyntaxV0Test`, `arC56_signaturePolicyArrayReverseMissingRightParenthesisSyntaxV0Test` |
| `ARRAY_SUM` | None before T021 | `arC56_signaturePolicyArraySumTrailingCommaSyntaxV0Test`, `arC56_signaturePolicyArraySumMissingRightParenthesisSyntaxV0Test` |
| `SPLIT_TO_ARRAY` | `arC51_splitToArrayTrailingCommaSyntaxV0Test`, `arC51_splitToArrayMissingRightParenthesisSyntaxV0Test`, `arC51_splitToArrayMissingCommaSyntaxV0Test` | Retained controls suffice |
| `STRTOK_TO_ARRAY` | `arC52_strtokToArrayTrailingCommaSyntaxV0Test`, `arC52_strtokToArrayMissingRightParenthesisSyntaxV0Test`, `arC52_strtokToArrayMissingCommaSyntaxV0Test` | Retained controls suffice |
| `ARRAY_GENERATE_RANGE` | `arC53_arrayGenerateRangeTrailingCommaSyntaxV0Test`, `arC53_arrayGenerateRangeMissingRightParenthesisSyntaxV0Test`, `arC53_arrayGenerateRangeMissingCommaSyntaxV0Test` | `arC56_signaturePolicyArrayGenerateRangeQualifiedTrailingCommaSyntaxV0Test` |
| `ARRAY_POSITION` | `arC54_arrayPositionTrailingCommaSyntaxV0Test`, `arC54_arrayPositionMissingRightParenthesisSyntaxV0Test`, `arC54_arrayPositionMissingCommaSyntaxV0Test` | `arC56_signaturePolicyArrayPositionQualifiedTrailingCommaSyntaxV0Test` |
| `ARRAYS_ZIP` | `arC55_arraysZipTrailingCommaSyntaxV0Test`, `arC55_arraysZipMissingRightParenthesisSyntaxV0Test`, `arC55_arraysZipMissingCommaSyntaxV0Test` | `arC56_signaturePolicyArraysZipQualifiedTrailingCommaSyntaxV0Test` |
| `ARRAYS_TO_OBJECT` | `arM24_arraysToObjectTrailingCommaSyntaxV0Test`, `arM24_arraysToObjectMissingCommaSyntaxV0Test` | Retained controls suffice |
| `TO_ARRAY` | `arC47_toArrayTrailingCommaSyntaxV0Test`, `arC47_toArrayMissingRightParenthesisSyntaxV0Test` | Retained controls suffice |
| `AS_ARRAY` | `arC48_asArrayTrailingCommaSyntaxV0Test`, `arC48_asArrayMissingRightParenthesisSyntaxV0Test` | Retained controls suffice |
| `IS_ARRAY` | `arC49_isArrayTrailingCommaSyntaxV0Test`, `arC49_isArrayMissingRightParenthesisSyntaxV0Test` | Retained controls suffice |
| `SPLIT` | `arC50_splitTrailingCommaSyntaxV0Test`, `arC50_splitMissingRightParenthesisSyntaxV0Test`, `arC50_splitMissingCommaSyntaxV0Test` | `arC56_signaturePolicySplitQualifiedTrailingCommaSyntaxV0Test` |

## T022 / D-AUDIT completed cell-level reconciliation

Completed 2026-11-19. **Scoped T022 PASS:** 21/21 assigned functions, **854/854**
required cells, **zero** remaining gaps. This is not T023/T024 or full-project
build/smoke, Slice D, GC2/GC3, other-slice or release sign-off.

### Method and audit boundary

- Enumerated the actual test methods, literal SQL, endpoint, six complete output
  assertions and exact diagnostic lists; did not infer cells from names or a
  green host total. Matched historical catalog SQL/endpoints/codes to source.
- Independently checked twelve placements; each eligible operand in qualified,
  unqualified, unresolved, ambiguous, missing/hidden-alias, predicand-variable,
  column-variable and NULL forms; combined substitutions where applicable;
  nested/derived/set-op/SCRIPT reset; neutral columns/UDFs; dialect/case;
  documented arities; and exact malformed syntax. Repeated witnesses below
  cover multiple explicit cells, not multiple independent test methods.
- Explicitly included SORT optional flags 2/3 (generic routing, not unary
  dedicated coverage) and ZIP argument 4. ZIP arities 1..4 are representative
  variadic coverage, not a finite maximum. Signature compatibility/runtime
  boundaries retain the separate T021 policy and full named-method ledger.
- Rechecked all six older expanded matrices. Corrected audit assumptions
  about published set-op scope numbering, derived aliases, literal counterparts
  in isolated ambiguity controls and neutral intervening SCRIPT statements
  before classifying a missing cell as a real test gap.
- The pre-tool ARRAY_REPEAT catalog has stale Snowflake-affinity code metadata
  on three uppercase-runner `SYNTAX` records. Source parser-error counts and
  complete positioned REPORT_ERROR/RECOVER/RECOVER_INLINE lists are authoritative
  and passing. Only that stale catalog code comparison was excluded; the actual
  strict syntax assertions were checked. This is a catalog limitation, not a
  parser failure or a golden refresh.
- Supporting catalog labels below are source-matched supplementary evidence,
  not 277 additional independently derived required contract cells.

### Nine repaired cells

| Function | Previously absent required cell | Added named method |
|---|---|---|
| `ARRAY_SORT` | `arg1:Null` | [arC57_matrixAuditArraySortArg1NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23113) |
| `ARRAY_SORT` | `neutral:column` | [arC57_matrixAuditArraySortSameNamedColumnNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23149) |
| `ARRAY_MAX` | `arg1:Null` | [arC57_matrixAuditArrayMaxArg1NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23181) |
| `ARRAY_MAX` | `neutral:column` | [arC57_matrixAuditArrayMaxSameNamedColumnNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23217) |
| `ARRAY_MIN` | `arg1:Null` | [arC57_matrixAuditArrayMinArg1NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23249) |
| `ARRAY_MIN` | `neutral:column` | [arC57_matrixAuditArrayMinSameNamedColumnNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23285) |
| `ARRAYS_TO_OBJECT` | `arg1:Unqualified` | [arC57_matrixAuditArraysToObjectArg1UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23317) |
| `ARRAYS_TO_OBJECT` | `arg2:Unqualified` | [arC57_matrixAuditArraysToObjectArg2UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23353) |
| `ARRAYS_TO_OBJECT` | `syntax:MissingRightParenthesis` | [arC57_matrixAuditArraysToObjectMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23389) |

Eight cases assert all six complete outputs and exact diagnostics. The ninth
asserts the exact syntax-error count and full positioned parser diagnostics
without walking rejected syntax. REVERSE and SUM needed no new T022 cases.

### Ownership, lexer and preservation proof

- Every approved token has exactly one lexer definition before `Identifier`,
  exactly one dedicated `_name` family owner, the corresponding walker handler
  and registry membership. All 21 official generated Java token IDs are unique
  and match the current ledger. Every function has neutral column/UDF controls.
- Grammar, walker and affinity registry are unchanged from `fbb0cff`.
  No new keyword, production change or runtime arity/type/value validator.
- Complete generated lexer SHA-256, before and after:
  `55bce4bc7ec702db4c7c2a9c9cc0cec1fad8c86ced39a848b9b93bd5070cdf5c`.
- All **1,024** pre-T022 test methods are byte-for-byte unchanged. No old
  extractor golden was rewritten. New total: **1,033** methods, **974**
  six-output cases, **59** parser-only controls, **1,033** exact diagnostic lists.
- Focused Maven lifecycle: **1,033 passed**, zero failures/errors/skips,
  exit **0**, **27.741 s**. VS Code independently: **1,033 passed, zero failed**.
  Existing host unused-code warnings and the loose capture helper's pre-existing
  editor Java-8 analysis mismatch are unchanged; actual capture/build uses JDK 21.
- T023 must still run focused -> Phase15 -> Smoketest -> full default accuracy
  sequentially. T024 must still perform final accounting/sign-off. T022 does not
  substitute its focused pass for those pending gates.

### Per-function required-cell witnesses

Generated from the audited source inventory. Each required cell occurs exactly
once in its function table; one named witness may satisfy several cells.
Earlier detailed batch ledgers retain additional witnesses and historical counts.

#### T022 ARRAY_TO_STRING

**44/44 cells; 50 methods; 47 six-output; 3 parser-only.**

| Required cells | Named source witness |
|---|---|
| `placement:Select`<br>`arg1:Qualified`<br>`arg2:Qualified`<br>`form:documented-arity:2` | [snowflakeArrayToStringSelectPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L68) |
| `placement:Where` | [snowflakeArrayToStringWherePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L154) |
| `placement:Join` | [arC42_arrayToStringJoinPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6629) |
| `placement:Having` | [arC42_arrayToStringHavingPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6648) |
| `placement:Qualify` | [snowflakeArrayToStringQualifyPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L449) |
| `placement:OrderBy` | [snowflakeArrayToStringOrderByPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L471) |
| `placement:Values` | [snowflakeArrayToStringValuesPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L491) |
| `placement:Insert` | [snowflakeArrayToStringInsertPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L531) |
| `placement:Update` | [snowflakeArrayToStringUpdatePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L572) |
| `placement:Delete` | [snowflakeArrayToStringDeletePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L612) |
| `placement:Ddl` | [snowflakeArrayToStringCreateViewDdlPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L247) |
| `placement:Script` | [snowflakeArrayToStringScriptStatementScopedWarningV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L224) |
| `arg1:Unqualified` | [arC42_arrayToStringArg1UnqualifiedOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6771) |
| `arg1:Unresolved`<br>`arg1:Ambiguous` | [snowflakeArrayToStringAmbiguousUnqualifiedDiagnosticV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L174) |
| `arg1:MissingAlias` | [snowflakeArrayToStringFirstOperandMissingAliasFatalV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L131) |
| `arg1:HiddenAlias` | [arC42_arrayToStringArg1HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6882) |
| `arg1:PredicandVariable` | [arC42_arrayToStringArg1PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6926) |
| `arg1:ColumnVariable` | [arC42_arrayToStringArg1ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6945) |
| `arg1:Null` | [arC42_arrayToStringArg1NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7130) |
| `arg2:Unqualified` | [arC42_arrayToStringArg2UnqualifiedOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6790) |
| `arg2:Unresolved` | [arC42_arrayToStringArg2UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6832) |
| `arg2:Ambiguous` | [arC42_arrayToStringArg2AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6855) |
| `arg2:MissingAlias` | [snowflakeArrayToStringSecondOperandMissingAliasFatalV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L88) |
| `arg2:HiddenAlias` | [arC42_arrayToStringArg2HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6904) |
| `arg2:PredicandVariable` | [snowflakeArrayToStringWithPredicandSubstitutionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L204) |
| `arg2:ColumnVariable` | [arC42_arrayToStringArg2ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6964) |
| `arg2:Null` | [arC42_arrayToStringArg2NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7149) |
| `combined:predicand` | [arC42_arrayToStringBothPredicandVariablesV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6983) |
| `combined:column` | [snowflakeArrayToStringQualifiedColumnVariablesV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L111) |
| `deep:nested` | [arC42_arrayToStringNestedExpressionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6695) |
| `deep:derived` | [arC42_arrayToStringDerivedOutputV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6714) |
| `deep:setop` | [arC42_arrayToStringSetOpSiblingIsolationV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6733) |
| `deep:script-reset` | [arC42_arrayToStringScriptResetV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6667) |
| `neutral:udf` | [arC42_arrayToStringQualifiedUdfNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7002) |
| `neutral:column` | [arC42_arrayToStringSameNamedColumnNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7017) |
| `dialect:uppercase` | [arC42_arrayToStringUppercaseV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7054) |
| `dialect:mixed` | [arC42_arrayToStringMixedDialectV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7032) |
| `syntax:TrailingComma` | [arC42_arrayToStringTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7168) |
| `syntax:MissingRightParenthesis` | [arC42_arrayToStringMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7218) |
| `syntax:MissingComma` | [arC42_arrayToStringMissingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7193) |

| Supporting catalog labels (not extra required cells) | Named source witness |
|---|---|
| `array_to_string:RequiredForm` | [arC56_signaturePolicyArrayToStringRequiredFormV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18548) |
| `array_to_string:EmptyArgumentsCompatibility` | [arC56_signaturePolicyArrayToStringEmptyArgumentsCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18584) |
| `array_to_string:BelowDocumentedMinimum` | [arC56_signaturePolicyArrayToStringBelowDocumentedMinimumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18616) |
| `array_to_string:AboveDocumentedMaximum` | [arC56_signaturePolicyArrayToStringAboveDocumentedMaximumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18652) |
| `array_to_string:RuntimeShapeBoundary` | [arC56_signaturePolicyArrayToStringRuntimeShapeBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18688) |

#### T022 ARRAY_SLICE

**53/53 cells; 60 methods; 57 six-output; 3 parser-only.**

| Required cells | Named source witness |
|---|---|
| `placement:Select`<br>`arg1:Qualified`<br>`arg2:Qualified`<br>`arg3:Qualified`<br>`form:documented-arity:3` | [snowflakeArraySliceSelectPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L268) |
| `placement:Where` | [arC43_arraySliceWherePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7243) |
| `placement:Join` | [arC43_arraySliceJoinPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7262) |
| `placement:Having` | [snowflakeArraySliceHavingPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L407) |
| `placement:Qualify` | [arC43_arraySliceQualifyPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7281) |
| `placement:OrderBy` | [snowflakeArraySliceOrderByPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L429) |
| `placement:Values` | [snowflakeArraySliceValuesPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L511) |
| `placement:Insert` | [snowflakeArraySliceInsertPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L551) |
| `placement:Update` | [snowflakeArraySliceUpdatePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L592) |
| `placement:Delete` | [snowflakeArraySliceDeletePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L632) |
| `placement:Ddl` | [arC43_arraySliceDdlPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7300) |
| `placement:Script`<br>`deep:script-reset` | [arC43_arraySliceScriptResetV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7324) |
| `arg1:Unqualified` | [arC43_arraySliceArg1UnqualifiedOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7371) |
| `arg1:Unresolved` | [arC43_arraySliceArg1UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7390) |
| `arg1:Ambiguous` | [arC43_arraySliceArg1AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7413) |
| `arg1:MissingAlias` | [snowflakeArraySliceFirstOperandMissingAliasFatalV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L331) |
| `arg1:HiddenAlias` | [arC43_arraySliceArg1HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7440) |
| `arg1:PredicandVariable` | [arC43_arraySliceArg1PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7462) |
| `arg1:ColumnVariable` | [arC43_arraySliceArg1ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7481) |
| `arg1:Null` | [arC43_arraySliceArg1NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7500) |
| `arg2:Unqualified` | [arC43_arraySliceArg2UnqualifiedOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7519) |
| `arg2:Unresolved` | [arC43_arraySliceArg2UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7538) |
| `arg2:Ambiguous` | [arC43_arraySliceArg2AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7561) |
| `arg2:MissingAlias` | [snowflakeArraySliceSecondOperandMissingAliasFatalV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L354) |
| `arg2:HiddenAlias` | [arC43_arraySliceArg2HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7588) |
| `arg2:PredicandVariable` | [arC43_arraySliceArg2PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7610) |
| `arg2:ColumnVariable` | [arC43_arraySliceArg2ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7629) |
| `arg2:Null` | [arC43_arraySliceArg2NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7648) |
| `arg3:Unqualified` | [arC43_arraySliceArg3UnqualifiedOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7667) |
| `arg3:Unresolved` | [arC43_arraySliceArg3UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7686) |
| `arg3:Ambiguous` | [arC43_arraySliceArg3AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7709) |
| `arg3:MissingAlias` | [snowflakeArraySliceThirdOperandMissingAliasFatalV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L288) |
| `arg3:HiddenAlias` | [arC43_arraySliceArg3HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7736) |
| `arg3:PredicandVariable` | [arC43_arraySliceArg3PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7758) |
| `arg3:ColumnVariable` | [arC43_arraySliceArg3ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7777) |
| `arg3:Null` | [arC43_arraySliceArg3NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7796) |
| `combined:predicand` | [arC43_arraySliceAllPredicandVariablesV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7815) |
| `combined:column` | [snowflakeArraySliceQualifiedColumnVariablesV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L311) |
| `deep:nested` | [arC43_arraySliceNestedExpressionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7834) |
| `deep:derived` | [arC43_arraySliceDerivedOutputV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7853) |
| `deep:setop` | [arC43_arraySliceSetOpSiblingIsolationV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7872) |
| `neutral:udf` | [arC43_arraySliceQualifiedUdfNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7891) |
| `neutral:column` | [arC43_arraySliceSameNamedColumnNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7906) |
| `dialect:uppercase` | [arC43_arraySliceUppercaseV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7943) |
| `dialect:mixed` | [arC43_arraySliceMixedDialectV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L7921) |
| `syntax:TrailingComma` | [arC43_arraySliceTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8057) |
| `syntax:MissingRightParenthesis` | [arC43_arraySliceMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8107) |
| `syntax:MissingComma` | [arC43_arraySliceMissingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8082) |

| Supporting catalog labels (not extra required cells) | Named source witness |
|---|---|
| `array_slice:RequiredForm` | [arC56_signaturePolicyArraySliceRequiredFormV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18724) |
| `array_slice:EmptyArgumentsCompatibility` | [arC56_signaturePolicyArraySliceEmptyArgumentsCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18760) |
| `array_slice:BelowDocumentedMinimum` | [arC56_signaturePolicyArraySliceBelowDocumentedMinimumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18792) |
| `array_slice:AboveDocumentedMaximum` | [arC56_signaturePolicyArraySliceAboveDocumentedMaximumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18828) |
| `array_slice:RuntimeShapeBoundary` | [arC56_signaturePolicyArraySliceRuntimeShapeBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18864) |

#### T022 ARRAY_REPEAT

**44/44 cells; 55 methods; 51 six-output; 4 parser-only.**

| Required cells | Named source witness |
|---|---|
| `placement:Select`<br>`arg1:Qualified`<br>`arg2:Qualified`<br>`form:documented-arity:2` | [arC41_arrayRepeatSelectPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5621) |
| `placement:Where` | [arC41_arrayRepeatWherePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5641) |
| `placement:Join` | [arC41_arrayRepeatJoinPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5661) |
| `placement:Having` | [arC41_arrayRepeatHavingPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5681) |
| `placement:Qualify` | [arC41_arrayRepeatQualifyPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5701) |
| `placement:OrderBy` | [arC41_arrayRepeatOrderByPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5721) |
| `placement:Values`<br>`combined:predicand` | [arC41_arrayRepeatValuesPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5741) |
| `placement:Insert` | [arC41_arrayRepeatInsertPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5761) |
| `placement:Update` | [arC41_arrayRepeatUpdatePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5781) |
| `placement:Delete` | [arC41_arrayRepeatDeletePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5801) |
| `placement:Ddl` | [arC41_arrayRepeatDdlPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5821) |
| `placement:Script`<br>`deep:script-reset` | [arC41_arrayRepeatScriptPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5841) |
| `arg1:Unqualified` | [arC41_arrayRepeatArg1UnqualifiedOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5945) |
| `arg1:Unresolved` | [arC41_arrayRepeatArg1UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5965) |
| `arg1:Ambiguous` | [arC41_arrayRepeatArg1AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5989) |
| `arg1:MissingAlias` | [arC41_arrayRepeatArg1MissingAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6017) |
| `arg1:HiddenAlias` | [arC41_arrayRepeatArg1HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6040) |
| `arg1:PredicandVariable` | [arC41_arrayRepeatArg1PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6063) |
| `arg1:ColumnVariable` | [arC41_arrayRepeatArg1ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6083) |
| `arg1:Null` | [arC41_arrayRepeatArg1NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6103) |
| `arg2:Unqualified` | [arC41_arrayRepeatArg2UnqualifiedOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6123) |
| `arg2:Unresolved` | [arC41_arrayRepeatArg2UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6143) |
| `arg2:Ambiguous` | [arC41_arrayRepeatArg2AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6167) |
| `arg2:MissingAlias` | [arC41_arrayRepeatArg2MissingAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6195) |
| `arg2:HiddenAlias` | [arC41_arrayRepeatArg2HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6218) |
| `arg2:PredicandVariable` | [arC41_arrayRepeatArg2PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6241) |
| `arg2:ColumnVariable` | [arC41_arrayRepeatArg2ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6261) |
| `arg2:Null` | [arC41_arrayRepeatArg2NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6281) |
| `combined:column` | [arC41_arrayRepeatBothColumnVariablesV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6301) |
| `deep:nested` | [arC41_arrayRepeatNestedExpressionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5865) |
| `deep:derived` | [arC41_arrayRepeatDerivedOutputV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5885) |
| `deep:setop` | [arC41_arrayRepeatSetOpSiblingIsolationV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5905) |
| `neutral:udf` | [arC41_arrayRepeatQualifiedUdfNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6321) |
| `neutral:column` | [arC41_arrayRepeatSameNamedColumnNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6336) |
| `dialect:uppercase` | [arC41_arrayRepeatUppercaseV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6374) |
| `dialect:mixed` | [arC41_arrayRepeatMixedDialectV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6351) |
| `syntax:TrailingComma` | [arC41_arrayRepeatTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6554) |
| `syntax:MissingRightParenthesis` | [arC41_arrayRepeatMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6604) |
| `syntax:MissingComma` | [arC41_arrayRepeatMissingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L6579) |

| Supporting catalog labels (not extra required cells) | Named source witness |
|---|---|
| `array_repeat:RequiredForm` | [arC56_signaturePolicyArrayRepeatRequiredFormV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18900) |
| `array_repeat:EmptyArgumentsCompatibility` | [arC56_signaturePolicyArrayRepeatEmptyArgumentsCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18936) |
| `array_repeat:BelowDocumentedMinimum` | [arC56_signaturePolicyArrayRepeatBelowDocumentedMinimumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18968) |
| `array_repeat:AboveDocumentedMaximum` | [arC56_signaturePolicyArrayRepeatAboveDocumentedMaximumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19004) |
| `array_repeat:RuntimeShapeBoundary` | [arC56_signaturePolicyArrayRepeatRuntimeShapeBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19040) |
| `array_repeat:QualifiedArityCompatibility` | [arC56_signaturePolicyArrayRepeatQualifiedArityCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22771) |
| `array_repeat:qualified-malformed-comma` | [arC56_signaturePolicyArrayRepeatQualifiedTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22803) |

#### T022 ARRAY_DISTINCT

**32/32 cells; 35 methods; 33 six-output; 2 parser-only.**

| Required cells | Named source witness |
|---|---|
| `placement:Select`<br>`arg1:Qualified`<br>`form:documented-arity:1` | [snowflakeArrayDistinctSelectPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L652) |
| `placement:Where` | [arC44_arrayDistinctWherePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8132) |
| `placement:Join` | [arC44_arrayDistinctJoinPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8151) |
| `placement:Having` | [arC44_arrayDistinctHavingPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8170) |
| `placement:Qualify` | [arC44_arrayDistinctQualifyPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8189) |
| `placement:OrderBy` | [arC44_arrayDistinctOrderByPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8208) |
| `placement:Values` | [arC44_arrayDistinctValuesPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8227) |
| `placement:Insert` | [arC44_arrayDistinctInsertPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8246) |
| `placement:Update`<br>`arg1:Unqualified` | [arC44_arrayDistinctUpdatePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8265) |
| `placement:Delete` | [arC44_arrayDistinctDeletePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8284) |
| `placement:Ddl` | [arC44_arrayDistinctDdlPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8303) |
| `placement:Script`<br>`deep:script-reset` | [arC44_arrayDistinctScriptPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8327) |
| `arg1:Unresolved` | [arC44_arrayDistinctArg1UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8374) |
| `arg1:Ambiguous` | [arC44_arrayDistinctArg1AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8397) |
| `arg1:MissingAlias` | [snowflakeArrayDistinctMissingAliasFatalV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L672) |
| `arg1:HiddenAlias` | [arC44_arrayDistinctArg1HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8424) |
| `arg1:PredicandVariable` | [arC44_arrayDistinctArg1PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8446) |
| `arg1:ColumnVariable` | [arC44_arrayDistinctArg1ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8465) |
| `arg1:Null` | [arC44_arrayDistinctArg1NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8484) |
| `deep:nested` | [arC44_arrayDistinctNestedExpressionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8503) |
| `deep:derived` | [arC44_arrayDistinctDerivedOutputV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8522) |
| `deep:setop` | [arC44_arrayDistinctSetOpSiblingIsolationV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8541) |
| `neutral:udf` | [arC44_arrayDistinctQualifiedUdfNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8560) |
| `neutral:column` | [arC44_arrayDistinctSameNamedColumnNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8575) |
| `dialect:uppercase` | [arC44_arrayDistinctUppercaseV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8590) |
| `dialect:mixed` | [arC44_arrayDistinctMixedDialectV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8609) |
| `syntax:TrailingComma` | [arC44_arrayDistinctTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8631) |
| `syntax:MissingRightParenthesis` | [arC44_arrayDistinctMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8656) |

| Supporting catalog labels (not extra required cells) | Named source witness |
|---|---|
| `literal:empty-array` | [arC44_arrayDistinctEmptyArrayLiteralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8681) |
| `literal:null-and-duplicates` | [arC44_arrayDistinctLiteralArrayWithNullAndDuplicatesV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8700) |
| `array_distinct:RequiredForm` | [arC56_signaturePolicyArrayDistinctRequiredFormV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19076) |
| `array_distinct:EmptyArgumentsCompatibility` | [arC56_signaturePolicyArrayDistinctEmptyArgumentsCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19112) |
| `array_distinct:AboveDocumentedMaximum` | [arC56_signaturePolicyArrayDistinctAboveDocumentedMaximumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19144) |
| `array_distinct:RuntimeShapeBoundary` | [arC56_signaturePolicyArrayDistinctRuntimeShapeBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19180) |

#### T022 ARRAY_COMPACT

**32/32 cells; 35 methods; 33 six-output; 2 parser-only.**

| Required cells | Named source witness |
|---|---|
| `placement:Select`<br>`arg1:Qualified`<br>`form:documented-arity:1` | [snowflakeArrayCompactSelectPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L695) |
| `placement:Where` | [snowflakeArrayCompactWherePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L781) |
| `placement:Join` | [arC45_arrayCompactJoinPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8719) |
| `placement:Having` | [arC45_arrayCompactHavingPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8738) |
| `placement:Qualify` | [arC45_arrayCompactQualifyPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8757) |
| `placement:OrderBy` | [snowflakeArrayCompactOrderByPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L801) |
| `placement:Values` | [snowflakeArrayCompactValuesPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L821) |
| `placement:Insert` | [snowflakeArrayCompactInsertPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L841) |
| `placement:Update`<br>`arg1:Unqualified` | [snowflakeArrayCompactUpdatePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L861) |
| `placement:Delete` | [snowflakeArrayCompactDeletePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L881) |
| `placement:Ddl` | [arC45_arrayCompactDdlPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8776) |
| `placement:Script`<br>`deep:script-reset` | [arC45_arrayCompactScriptPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8800) |
| `arg1:Unresolved` | [arC45_arrayCompactArg1UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8847) |
| `arg1:Ambiguous` | [arC45_arrayCompactArg1AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8870) |
| `arg1:MissingAlias` | [snowflakeArrayCompactMissingAliasFatalV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L715) |
| `arg1:HiddenAlias` | [arC45_arrayCompactArg1HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8897) |
| `arg1:PredicandVariable` | [arC45_arrayCompactArg1PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8919) |
| `arg1:ColumnVariable` | [arC45_arrayCompactArg1ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8938) |
| `arg1:Null` | [arC45_arrayCompactArg1NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8957) |
| `deep:nested` | [arC45_arrayCompactNestedExpressionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8976) |
| `deep:derived` | [arC45_arrayCompactDerivedOutputV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L8995) |
| `deep:setop` | [arC45_arrayCompactSetOpSiblingIsolationV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9014) |
| `neutral:udf` | [arC45_arrayCompactQualifiedUdfNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9033) |
| `neutral:column` | [arC45_arrayCompactSameNamedColumnNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9048) |
| `dialect:uppercase` | [arC45_arrayCompactUppercaseV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9063) |
| `dialect:mixed` | [arC45_arrayCompactMixedDialectV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9082) |
| `syntax:TrailingComma` | [arC45_arrayCompactTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9104) |
| `syntax:MissingRightParenthesis` | [arC45_arrayCompactMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9129) |

| Supporting catalog labels (not extra required cells) | Named source witness |
|---|---|
| `literal:empty-array` | [arC45_arrayCompactEmptyArrayLiteralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9154) |
| `literal:null-and-duplicates` | [arC45_arrayCompactLiteralArrayWithNullAndDuplicatesV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9173) |
| `array_compact:RequiredForm` | [arC56_signaturePolicyArrayCompactRequiredFormV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19216) |
| `array_compact:EmptyArgumentsCompatibility` | [arC56_signaturePolicyArrayCompactEmptyArgumentsCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19252) |
| `array_compact:AboveDocumentedMaximum` | [arC56_signaturePolicyArrayCompactAboveDocumentedMaximumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19284) |
| `array_compact:RuntimeShapeBoundary` | [arC56_signaturePolicyArrayCompactRuntimeShapeBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19320) |

#### T022 ARRAY_SIZE

**32/32 cells; 37 methods; 34 six-output; 3 parser-only.**

| Required cells | Named source witness |
|---|---|
| `placement:Select`<br>`arg1:Qualified`<br>`form:documented-arity:1` | [snowflakeArraySizeSelectPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L738) |
| `placement:Where` | [snowflakeArraySizeWherePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L901) |
| `placement:Join` | [arC46_arraySizeJoinPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9192) |
| `placement:Having` | [arC46_arraySizeHavingPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9211) |
| `placement:Qualify` | [arC46_arraySizeQualifyPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9230) |
| `placement:OrderBy` | [snowflakeArraySizeOrderByPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L921) |
| `placement:Values` | [snowflakeArraySizeValuesPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L941) |
| `placement:Insert` | [snowflakeArraySizeInsertPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L961) |
| `placement:Update`<br>`arg1:Unqualified` | [snowflakeArraySizeUpdatePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L981) |
| `placement:Delete` | [snowflakeArraySizeDeletePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1001) |
| `placement:Ddl` | [arC46_arraySizeDdlPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9249) |
| `placement:Script`<br>`deep:script-reset` | [arC46_arraySizeScriptPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9273) |
| `arg1:Unresolved` | [arC46_arraySizeArg1UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9320) |
| `arg1:Ambiguous` | [arC46_arraySizeArg1AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9343) |
| `arg1:MissingAlias` | [snowflakeArraySizeMissingAliasFatalV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L758) |
| `arg1:HiddenAlias` | [arC46_arraySizeArg1HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9370) |
| `arg1:PredicandVariable` | [arC46_arraySizeArg1PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9392) |
| `arg1:ColumnVariable` | [arC46_arraySizeArg1ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9411) |
| `arg1:Null` | [arC46_arraySizeArg1NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9430) |
| `deep:nested` | [arC46_arraySizeNestedExpressionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9449) |
| `deep:derived` | [arC46_arraySizeDerivedOutputV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9468) |
| `deep:setop` | [arC46_arraySizeSetOpSiblingIsolationV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9487) |
| `neutral:udf` | [arC46_arraySizeQualifiedUdfNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9506) |
| `neutral:column` | [arC46_arraySizeSameNamedColumnNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9521) |
| `dialect:uppercase` | [arC46_arraySizeUppercaseV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9536) |
| `dialect:mixed` | [arC46_arraySizeMixedDialectV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9555) |
| `syntax:TrailingComma` | [arC46_arraySizeTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9577) |
| `syntax:MissingRightParenthesis` | [arC46_arraySizeMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9602) |

| Supporting catalog labels (not extra required cells) | Named source witness |
|---|---|
| `literal:empty-array` | [arC46_arraySizeEmptyArrayLiteralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9627) |
| `literal:null-and-duplicates` | [arC46_arraySizeLiteralArrayWithNullAndDuplicatesV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9646) |
| `array_size:RequiredForm` | [arC56_signaturePolicyArraySizeRequiredFormV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19356) |
| `array_size:EmptyArgumentsCompatibility` | [arC56_signaturePolicyArraySizeEmptyArgumentsCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19392) |
| `array_size:AboveDocumentedMaximum` | [arC56_signaturePolicyArraySizeAboveDocumentedMaximumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19424) |
| `array_size:RuntimeShapeBoundary` | [arC56_signaturePolicyArraySizeRuntimeShapeBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19460) |
| `array_size:QualifiedArityCompatibility` | [arC56_signaturePolicyArraySizeQualifiedArityCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22828) |
| `array_size:qualified-malformed-comma` | [arC56_signaturePolicyArraySizeQualifiedTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22860) |

#### T022 ARRAY_SORT

**52/52 cells; 55 methods; 52 six-output; 3 parser-only.**

| Required cells | Named source witness |
|---|---|
| `placement:Select`<br>`arg1:Qualified`<br>`form:documented-arity:1` | [arC38_arraySortSelectPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1813) |
| `placement:Where` | [arC38_arraySortWherePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1834) |
| `placement:Join` | [arC38_arraySortJoinOnPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1855) |
| `placement:Having` | [arC38_arraySortHavingPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1876) |
| `placement:Qualify` | [arC38_arraySortQualifyPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1897) |
| `placement:OrderBy` | [arC38_arraySortOrderByPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1918) |
| `placement:Values`<br>`arg1:PredicandVariable` | [arC38_arraySortValuesPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1939) |
| `placement:Insert` | [arC38_arraySortInsertPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1960) |
| `placement:Update`<br>`arg1:Unqualified` | [arC38_arraySortUpdatePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1981) |
| `placement:Delete` | [arC38_arraySortDeletePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2002) |
| `placement:Ddl` | [arC38_arraySortDdlPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2023) |
| `placement:Script`<br>`deep:script-reset` | [arR20_arraySortScriptStatementResetV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2045) |
| `arg1:Unresolved` | [arM20_arraySortUnresolvedOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2156) |
| `arg1:Ambiguous` | [arM21_arraySortAmbiguousOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2182) |
| `arg1:MissingAlias` | [arM22_arraySortMissingAliasOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2213) |
| `arg1:HiddenAlias` | [arM22_arraySortHiddenAliasOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2238) |
| `arg1:ColumnVariable` | [arC38_arraySortQualifiedColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2284) |
| `arg1:Null` | [arC57_matrixAuditArraySortArg1NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23113) |
| `arg2:Qualified`<br>`arg3:Qualified` | [arC56_signaturePolicyArraySortOptionalArity3V0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21740) |
| `arg2:Unqualified` | [arC56_signaturePolicyArraySortOptionalArg2UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21884) |
| `arg2:Unresolved` | [arC56_signaturePolicyArraySortOptionalArg2UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21920) |
| `arg2:Ambiguous` | [arC56_signaturePolicyArraySortOptionalArg2AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21960) |
| `arg2:MissingAlias` | [arC56_signaturePolicyArraySortOptionalArg2MissingAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22004) |
| `arg2:HiddenAlias` | [arC56_signaturePolicyArraySortOptionalArg2HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22043) |
| `arg2:PredicandVariable` | [arC56_signaturePolicyArraySortOptionalArg2PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22082) |
| `arg2:ColumnVariable` | [arC56_signaturePolicyArraySortOptionalArg2ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22118) |
| `arg2:Null` | [arC56_signaturePolicyArraySortOptionalArg2NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22154) |
| `arg3:Unqualified` | [arC56_signaturePolicyArraySortOptionalArg3UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22190) |
| `arg3:Unresolved` | [arC56_signaturePolicyArraySortOptionalArg3UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22226) |
| `arg3:Ambiguous` | [arC56_signaturePolicyArraySortOptionalArg3AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22266) |
| `arg3:MissingAlias` | [arC56_signaturePolicyArraySortOptionalArg3MissingAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22310) |
| `arg3:HiddenAlias` | [arC56_signaturePolicyArraySortOptionalArg3HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22349) |
| `arg3:PredicandVariable` | [arC56_signaturePolicyArraySortOptionalArg3PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22388) |
| `arg3:ColumnVariable` | [arC56_signaturePolicyArraySortOptionalArg3ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22424) |
| `arg3:Null` | [arC56_signaturePolicyArraySortOptionalArg3NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22460) |
| `deep:nested` | [arR18_arraySortNestedExpressionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2072) |
| `deep:derived` | [arR18_arraySortDerivedOutputBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2093) |
| `deep:setop` | [arR19_arraySortSetOpSiblingIsolationV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2114) |
| `neutral:udf` | [arM23_arraySortSchemaQualifiedRoutineControlV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2305) |
| `neutral:column` | [arC57_matrixAuditArraySortSameNamedColumnNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23149) |
| `dialect:uppercase` | [arC38_arraySortUppercaseNameV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2346) |
| `dialect:mixed` | [arM24_arraySortMixedDialectControlV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2321) |
| `syntax:TrailingComma` | [arC56_signaturePolicyArraySortTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22496) |
| `syntax:MissingRightParenthesis` | [arC56_signaturePolicyArraySortMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22521) |
| `form:documented-arity:2` | [arC38_arraySortDescendingOptionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2367) |
| `form:documented-arity:3` | [arC38_arraySortNullOrderingOptionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2388) |

| Supporting catalog labels (not extra required cells) | Named source witness |
|---|---|
| `array_sort:RequiredForm` | [arC56_signaturePolicyArraySortRequiredFormV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19496) |
| `array_sort:EmptyArgumentsCompatibility` | [arC56_signaturePolicyArraySortEmptyArgumentsCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19532) |
| `array_sort:AboveDocumentedMaximum` | [arC56_signaturePolicyArraySortAboveDocumentedMaximumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19564) |
| `array_sort:RuntimeShapeBoundary` | [arC56_signaturePolicyArraySortRuntimeShapeBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19600) |
| `array_sort:OptionalArity2` | [arC56_signaturePolicyArraySortOptionalArity2V0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21704) |
| `array_sort:OptionalArity3` | [arC56_signaturePolicyArraySortOptionalArity3V0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21740) |
| `array_sort:optional:arg2:Unqualified` | [arC56_signaturePolicyArraySortOptionalArg2UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21884) |
| `array_sort:optional:arg2:Unresolved` | [arC56_signaturePolicyArraySortOptionalArg2UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21920) |
| `array_sort:optional:arg2:Ambiguous` | [arC56_signaturePolicyArraySortOptionalArg2AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21960) |
| `array_sort:optional:arg2:MissingAlias` | [arC56_signaturePolicyArraySortOptionalArg2MissingAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22004) |
| `array_sort:optional:arg2:HiddenAlias` | [arC56_signaturePolicyArraySortOptionalArg2HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22043) |
| `array_sort:optional:arg2:PredicandVariable` | [arC56_signaturePolicyArraySortOptionalArg2PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22082) |
| `array_sort:optional:arg2:ColumnVariable` | [arC56_signaturePolicyArraySortOptionalArg2ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22118) |
| `array_sort:optional:arg2:Null` | [arC56_signaturePolicyArraySortOptionalArg2NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22154) |
| `array_sort:optional:arg3:Unqualified` | [arC56_signaturePolicyArraySortOptionalArg3UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22190) |
| `array_sort:optional:arg3:Unresolved` | [arC56_signaturePolicyArraySortOptionalArg3UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22226) |
| `array_sort:optional:arg3:Ambiguous` | [arC56_signaturePolicyArraySortOptionalArg3AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22266) |
| `array_sort:optional:arg3:MissingAlias` | [arC56_signaturePolicyArraySortOptionalArg3MissingAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22310) |
| `array_sort:optional:arg3:HiddenAlias` | [arC56_signaturePolicyArraySortOptionalArg3HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22349) |
| `array_sort:optional:arg3:PredicandVariable` | [arC56_signaturePolicyArraySortOptionalArg3PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22388) |
| `array_sort:optional:arg3:ColumnVariable` | [arC56_signaturePolicyArraySortOptionalArg3ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22424) |
| `array_sort:optional:arg3:Null` | [arC56_signaturePolicyArraySortOptionalArg3NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22460) |
| `array_sort:syntax:TrailingComma` | [arC56_signaturePolicyArraySortTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22496) |
| `array_sort:syntax:MissingRightParenthesis` | [arC56_signaturePolicyArraySortMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22521) |
| `array_sort:syntax:MissingComma` | [arC56_signaturePolicyArraySortMissingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22546) |
| `array_sort:arg1:Null` | [arC57_matrixAuditArraySortArg1NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23113) |
| `array_sort:neutral:column` | [arC57_matrixAuditArraySortSameNamedColumnNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23149) |

#### T022 ARRAY_MAX

**32/32 cells; 33 methods; 31 six-output; 2 parser-only.**

| Required cells | Named source witness |
|---|---|
| `placement:Select`<br>`arg1:Qualified`<br>`form:documented-arity:1` | [arC39_arrayMaxSelectPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2430) |
| `placement:Where` | [arC39_arrayMaxWherePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2451) |
| `placement:Join` | [arC39_arrayMaxJoinOnPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2472) |
| `placement:Having` | [arC39_arrayMaxHavingPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2493) |
| `placement:Qualify` | [arC39_arrayMaxQualifyPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2514) |
| `placement:OrderBy` | [arC39_arrayMaxOrderByPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2535) |
| `placement:Values`<br>`arg1:PredicandVariable` | [arC39_arrayMaxValuesPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2556) |
| `placement:Insert` | [arC39_arrayMaxInsertPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2577) |
| `placement:Update`<br>`arg1:Unqualified` | [arC39_arrayMaxUpdatePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2598) |
| `placement:Delete` | [arC39_arrayMaxDeletePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2619) |
| `placement:Ddl` | [arC39_arrayMaxDdlPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2640) |
| `placement:Script`<br>`deep:script-reset` | [arR20_arrayMaxScriptStatementResetV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2662) |
| `arg1:Unresolved` | [arM20_arrayMaxUnresolvedOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2773) |
| `arg1:Ambiguous` | [arM21_arrayMaxAmbiguousOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2799) |
| `arg1:MissingAlias` | [arM22_arrayMaxMissingAliasOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2830) |
| `arg1:HiddenAlias` | [arM22_arrayMaxHiddenAliasOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2855) |
| `arg1:ColumnVariable` | [arC39_arrayMaxQualifiedColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2901) |
| `arg1:Null` | [arC57_matrixAuditArrayMaxArg1NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23181) |
| `deep:nested` | [arR18_arrayMaxNestedExpressionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2689) |
| `deep:derived` | [arR18_arrayMaxDerivedOutputBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2710) |
| `deep:setop` | [arR19_arrayMaxSetOpSiblingIsolationV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2731) |
| `neutral:udf` | [arM23_arrayMaxSchemaQualifiedRoutineControlV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2922) |
| `neutral:column` | [arC57_matrixAuditArrayMaxSameNamedColumnNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23217) |
| `dialect:uppercase` | [arC39_arrayMaxUppercaseNameV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2963) |
| `dialect:mixed` | [arM24_arrayMaxMixedDialectControlV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2938) |
| `syntax:TrailingComma` | [arC56_signaturePolicyArrayMaxTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22571) |
| `syntax:MissingRightParenthesis` | [arC56_signaturePolicyArrayMaxMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22596) |

| Supporting catalog labels (not extra required cells) | Named source witness |
|---|---|
| `array_max:RequiredForm` | [arC56_signaturePolicyArrayMaxRequiredFormV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19636) |
| `array_max:EmptyArgumentsCompatibility` | [arC56_signaturePolicyArrayMaxEmptyArgumentsCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19672) |
| `array_max:AboveDocumentedMaximum` | [arC56_signaturePolicyArrayMaxAboveDocumentedMaximumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19704) |
| `array_max:RuntimeShapeBoundary` | [arC56_signaturePolicyArrayMaxRuntimeShapeBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19740) |
| `array_max:syntax:TrailingComma` | [arC56_signaturePolicyArrayMaxTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22571) |
| `array_max:syntax:MissingRightParenthesis` | [arC56_signaturePolicyArrayMaxMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22596) |
| `array_max:arg1:Null` | [arC57_matrixAuditArrayMaxArg1NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23181) |
| `array_max:neutral:column` | [arC57_matrixAuditArrayMaxSameNamedColumnNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23217) |

#### T022 ARRAY_MIN

**32/32 cells; 33 methods; 31 six-output; 2 parser-only.**

| Required cells | Named source witness |
|---|---|
| `placement:Select`<br>`arg1:Qualified`<br>`form:documented-arity:1` | [arC40_arrayMinSelectPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L2984) |
| `placement:Where` | [arC40_arrayMinWherePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3005) |
| `placement:Join` | [arC40_arrayMinJoinOnPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3026) |
| `placement:Having` | [arC40_arrayMinHavingPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3047) |
| `placement:Qualify` | [arC40_arrayMinQualifyPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3068) |
| `placement:OrderBy` | [arC40_arrayMinOrderByPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3089) |
| `placement:Values`<br>`arg1:PredicandVariable` | [arC40_arrayMinValuesPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3110) |
| `placement:Insert` | [arC40_arrayMinInsertPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3131) |
| `placement:Update`<br>`arg1:Unqualified` | [arC40_arrayMinUpdatePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3152) |
| `placement:Delete` | [arC40_arrayMinDeletePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3173) |
| `placement:Ddl` | [arC40_arrayMinDdlPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3194) |
| `placement:Script`<br>`deep:script-reset` | [arR20_arrayMinScriptStatementResetV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3216) |
| `arg1:Unresolved` | [arM20_arrayMinUnresolvedOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3327) |
| `arg1:Ambiguous` | [arM21_arrayMinAmbiguousOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3353) |
| `arg1:MissingAlias` | [arM22_arrayMinMissingAliasOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3384) |
| `arg1:HiddenAlias` | [arM22_arrayMinHiddenAliasOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3409) |
| `arg1:ColumnVariable` | [arC40_arrayMinQualifiedColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3455) |
| `arg1:Null` | [arC57_matrixAuditArrayMinArg1NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23249) |
| `deep:nested` | [arR18_arrayMinNestedExpressionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3243) |
| `deep:derived` | [arR18_arrayMinDerivedOutputBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3264) |
| `deep:setop` | [arR19_arrayMinSetOpSiblingIsolationV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3285) |
| `neutral:udf` | [arM23_arrayMinSchemaQualifiedRoutineControlV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3476) |
| `neutral:column` | [arC57_matrixAuditArrayMinSameNamedColumnNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23285) |
| `dialect:uppercase` | [arC40_arrayMinUppercaseNameV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3517) |
| `dialect:mixed` | [arM24_arrayMinMixedDialectControlV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3492) |
| `syntax:TrailingComma` | [arC56_signaturePolicyArrayMinTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22621) |
| `syntax:MissingRightParenthesis` | [arC56_signaturePolicyArrayMinMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22646) |

| Supporting catalog labels (not extra required cells) | Named source witness |
|---|---|
| `array_min:RequiredForm` | [arC56_signaturePolicyArrayMinRequiredFormV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19776) |
| `array_min:EmptyArgumentsCompatibility` | [arC56_signaturePolicyArrayMinEmptyArgumentsCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19812) |
| `array_min:AboveDocumentedMaximum` | [arC56_signaturePolicyArrayMinAboveDocumentedMaximumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19844) |
| `array_min:RuntimeShapeBoundary` | [arC56_signaturePolicyArrayMinRuntimeShapeBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19880) |
| `array_min:syntax:TrailingComma` | [arC56_signaturePolicyArrayMinTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22621) |
| `array_min:syntax:MissingRightParenthesis` | [arC56_signaturePolicyArrayMinMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22646) |
| `array_min:arg1:Null` | [arC57_matrixAuditArrayMinArg1NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23249) |
| `array_min:neutral:column` | [arC57_matrixAuditArrayMinSameNamedColumnNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23285) |

#### T022 ARRAY_REVERSE

**32/32 cells; 33 methods; 31 six-output; 2 parser-only.**

| Required cells | Named source witness |
|---|---|
| `placement:Select`<br>`arg1:Qualified`<br>`form:documented-arity:1` | [arC39_arrayReverseSelectPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3538) |
| `placement:Where` | [arC39_arrayReverseWherePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3559) |
| `placement:Join` | [arC39_arrayReverseJoinOnPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3580) |
| `placement:Having` | [arC39_arrayReverseHavingPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3601) |
| `placement:Qualify` | [arC39_arrayReverseQualifyPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3622) |
| `placement:OrderBy` | [arC39_arrayReverseOrderByPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3643) |
| `placement:Values`<br>`arg1:PredicandVariable` | [arC39_arrayReverseValuesPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3664) |
| `placement:Insert` | [arC39_arrayReverseInsertPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3685) |
| `placement:Update`<br>`arg1:Unqualified` | [arC39_arrayReverseUpdatePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3706) |
| `placement:Delete` | [arC39_arrayReverseDeletePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3727) |
| `placement:Ddl` | [arC39_arrayReverseDdlPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3748) |
| `placement:Script`<br>`deep:script-reset` | [arR20_arrayReverseScriptStatementResetV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3770) |
| `arg1:Unresolved` | [arM20_arrayReverseUnresolvedOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3881) |
| `arg1:Ambiguous` | [arM21_arrayReverseAmbiguousOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3907) |
| `arg1:MissingAlias` | [arM22_arrayReverseMissingAliasOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3938) |
| `arg1:HiddenAlias` | [arM22_arrayReverseHiddenAliasOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3963) |
| `arg1:ColumnVariable` | [arC39_arrayReverseQualifiedColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4009) |
| `arg1:Null` | [arC39_arrayReverseNullOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4092) |
| `deep:nested` | [arR18_arrayReverseNestedExpressionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3797) |
| `deep:derived` | [arR18_arrayReverseDerivedOutputBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3818) |
| `deep:setop` | [arR19_arrayReverseSetOpSiblingIsolationV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L3839) |
| `neutral:udf` | [arM23_arrayReverseSchemaQualifiedRoutineControlV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4030) |
| `neutral:column` | [arM23_arrayReverseNonreservedColumnNameV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4113) |
| `dialect:uppercase` | [arC39_arrayReverseUppercaseNameV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4071) |
| `dialect:mixed` | [arM24_arrayReverseMixedDialectControlV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4046) |
| `syntax:TrailingComma` | [arC56_signaturePolicyArrayReverseTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22671) |
| `syntax:MissingRightParenthesis` | [arC56_signaturePolicyArrayReverseMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22696) |

| Supporting catalog labels (not extra required cells) | Named source witness |
|---|---|
| `array_reverse:RequiredForm` | [arC56_signaturePolicyArrayReverseRequiredFormV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19916) |
| `array_reverse:EmptyArgumentsCompatibility` | [arC56_signaturePolicyArrayReverseEmptyArgumentsCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19952) |
| `array_reverse:AboveDocumentedMaximum` | [arC56_signaturePolicyArrayReverseAboveDocumentedMaximumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L19984) |
| `array_reverse:RuntimeShapeBoundary` | [arC56_signaturePolicyArrayReverseRuntimeShapeBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20020) |
| `array_reverse:syntax:TrailingComma` | [arC56_signaturePolicyArrayReverseTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22671) |
| `array_reverse:syntax:MissingRightParenthesis` | [arC56_signaturePolicyArrayReverseMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22696) |

#### T022 ARRAY_SUM

**32/32 cells; 33 methods; 31 six-output; 2 parser-only.**

| Required cells | Named source witness |
|---|---|
| `placement:Select`<br>`arg1:Qualified`<br>`form:documented-arity:1` | [arC40_arraySumSelectPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4129) |
| `placement:Where` | [arC40_arraySumWherePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4150) |
| `placement:Join` | [arC40_arraySumJoinOnPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4171) |
| `placement:Having` | [arC40_arraySumHavingPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4192) |
| `placement:Qualify` | [arC40_arraySumQualifyPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4213) |
| `placement:OrderBy` | [arC40_arraySumOrderByPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4234) |
| `placement:Values`<br>`arg1:PredicandVariable` | [arC40_arraySumValuesPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4255) |
| `placement:Insert` | [arC40_arraySumInsertPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4276) |
| `placement:Update`<br>`arg1:Unqualified` | [arC40_arraySumUpdatePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4297) |
| `placement:Delete` | [arC40_arraySumDeletePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4318) |
| `placement:Ddl` | [arC40_arraySumDdlPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4339) |
| `placement:Script`<br>`deep:script-reset` | [arR20_arraySumScriptStatementResetV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4361) |
| `arg1:Unresolved` | [arM20_arraySumUnresolvedOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4472) |
| `arg1:Ambiguous` | [arM21_arraySumAmbiguousOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4498) |
| `arg1:MissingAlias` | [arM22_arraySumMissingAliasOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4529) |
| `arg1:HiddenAlias` | [arM22_arraySumHiddenAliasOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4554) |
| `arg1:ColumnVariable` | [arC40_arraySumQualifiedColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4600) |
| `arg1:Null` | [arC40_arraySumNullOperandV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4683) |
| `deep:nested` | [arR18_arraySumNestedExpressionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4388) |
| `deep:derived` | [arR18_arraySumDerivedOutputBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4409) |
| `deep:setop` | [arR19_arraySumSetOpSiblingIsolationV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4430) |
| `neutral:udf` | [arM23_arraySumSchemaQualifiedRoutineControlV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4621) |
| `neutral:column` | [arM23_arraySumNonreservedColumnNameV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4704) |
| `dialect:uppercase` | [arC40_arraySumUppercaseNameV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4662) |
| `dialect:mixed` | [arM24_arraySumMixedDialectControlV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4637) |
| `syntax:TrailingComma` | [arC56_signaturePolicyArraySumTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22721) |
| `syntax:MissingRightParenthesis` | [arC56_signaturePolicyArraySumMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22746) |

| Supporting catalog labels (not extra required cells) | Named source witness |
|---|---|
| `array_sum:RequiredForm` | [arC56_signaturePolicyArraySumRequiredFormV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20056) |
| `array_sum:EmptyArgumentsCompatibility` | [arC56_signaturePolicyArraySumEmptyArgumentsCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20092) |
| `array_sum:AboveDocumentedMaximum` | [arC56_signaturePolicyArraySumAboveDocumentedMaximumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20124) |
| `array_sum:RuntimeShapeBoundary` | [arC56_signaturePolicyArraySumRuntimeShapeBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20160) |
| `array_sum:syntax:TrailingComma` | [arC56_signaturePolicyArraySumTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22721) |
| `array_sum:syntax:MissingRightParenthesis` | [arC56_signaturePolicyArraySumMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22746) |

#### T022 SPLIT_TO_ARRAY

**45/45 cells; 58 methods; 55 six-output; 3 parser-only.**

| Required cells | Named source witness |
|---|---|
| `placement:Select`<br>`arg1:Qualified`<br>`arg2:Qualified`<br>`form:documented-arity:2` | [snowflakeSplitToArraySelectSeedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1021) |
| `placement:Where` | [snowflakeSplitToArrayWherePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1290) |
| `placement:Join` | [arC51_splitToArrayJoinPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12733) |
| `placement:Having` | [arC51_splitToArrayHavingPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12752) |
| `placement:Qualify` | [arC51_splitToArrayQualifyPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12771) |
| `placement:OrderBy` | [snowflakeSplitToArrayOrderByPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1310) |
| `placement:Values` | [snowflakeSplitToArrayValuesPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1330) |
| `placement:Insert` | [snowflakeSplitToArrayInsertPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1350) |
| `placement:Update` | [snowflakeSplitToArrayUpdatePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1370) |
| `placement:Delete` | [snowflakeSplitToArrayDeletePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1390) |
| `placement:Ddl` | [arC51_splitToArrayDdlPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12809) |
| `placement:Script`<br>`deep:script-reset` | [arC51_splitToArrayScriptPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12833) |
| `arg1:Unqualified` | [arC51_splitToArrayArg1UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12861) |
| `arg1:Unresolved` | [arC51_splitToArrayArg1UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12880) |
| `arg1:Ambiguous` | [arC51_splitToArrayArg1AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12903) |
| `arg1:MissingAlias` | [snowflakeSplitToArrayMissingAliasFatalV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1041) |
| `arg1:HiddenAlias` | [arC51_splitToArrayArg1HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12930) |
| `arg1:PredicandVariable` | [arC51_splitToArrayArg1PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12952) |
| `arg1:ColumnVariable` | [arC51_splitToArrayArg1ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12971) |
| `arg1:Null` | [arC51_splitToArrayArg1NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12990) |
| `arg2:Unqualified` | [arC51_splitToArrayArg2UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13009) |
| `arg2:Unresolved` | [arC51_splitToArrayArg2UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13028) |
| `arg2:Ambiguous` | [arC51_splitToArrayArg2AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13051) |
| `arg2:MissingAlias` | [arC51_splitToArrayArg2MissingAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13078) |
| `arg2:HiddenAlias` | [arC51_splitToArrayArg2HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13100) |
| `arg2:PredicandVariable` | [arC51_splitToArrayArg2PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13122) |
| `arg2:ColumnVariable` | [arC51_splitToArrayArg2ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13141) |
| `arg2:Null` | [arC51_splitToArrayArg2NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13160) |
| `combined:predicand` | [arC51_splitToArrayAllPredicandVariablesV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13198) |
| `combined:column` | [arC51_splitToArrayAllColumnVariablesV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13217) |
| `deep:nested` | [arC51_splitToArrayNestedExpressionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13236) |
| `deep:derived` | [arC51_splitToArrayDerivedOutputV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13255) |
| `deep:setop` | [arC51_splitToArraySetOpSiblingIsolationV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13274) |
| `neutral:udf` | [arC51_splitToArrayQualifiedUdfNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13293) |
| `neutral:column` | [arC51_splitToArraySameNamedColumnNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13308) |
| `dialect:uppercase` | [arC51_splitToArrayUppercaseV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13323) |
| `dialect:mixed` | [arC51_splitToArrayMixedDialectV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13342) |
| `syntax:TrailingComma` | [arC51_splitToArrayTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13364) |
| `syntax:MissingRightParenthesis` | [arC51_splitToArrayMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13389) |
| `syntax:MissingComma` | [arC51_splitToArrayMissingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13414) |
| `form:documented-arity:1` | [arC51_splitToArrayDefaultDelimiterQualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13534) |

| Supporting catalog labels (not extra required cells) | Named source witness |
|---|---|
| `legacy:values-column-control` | [snowflakeSplitToArrayValuesPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1330) |
| `input:empty-string` | [arC51_splitToArrayEmptyStringV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13439) |
| `input:empty-delimiter` | [arC51_splitToArrayEmptyDelimiterV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13458) |
| `input:repeated-delimiter` | [arC51_splitToArrayRepeatedDelimiterV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13477) |
| `input:delimiter-substring` | [arC51_splitToArrayDelimiterSequenceV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13496) |
| `input:both-null` | [arC51_splitToArrayBothNullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13515) |
| `optional:qualified` | [arC51_splitToArrayDefaultDelimiterQualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13534) |
| `optional:unqualified` | [arC51_splitToArrayDefaultDelimiterUnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13553) |
| `optional:predicand` | [arC51_splitToArrayDefaultDelimiterPredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13572) |
| `optional:column` | [arC51_splitToArrayDefaultDelimiterColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13591) |
| `optional:null` | [arC51_splitToArrayDefaultDelimiterNullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13610) |
| `optional:literal` | [arC51_splitToArrayDefaultDelimiterLiteralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13629) |
| `split_to_array:RequiredForm` | [arC56_signaturePolicySplitToArrayRequiredFormV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20196) |
| `split_to_array:EmptyArgumentsCompatibility` | [arC56_signaturePolicySplitToArrayEmptyArgumentsCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20232) |
| `split_to_array:AboveDocumentedMaximum` | [arC56_signaturePolicySplitToArrayAboveDocumentedMaximumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20264) |
| `split_to_array:RuntimeShapeBoundary` | [arC56_signaturePolicySplitToArrayRuntimeShapeBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20300) |
| `split_to_array:OptionalArity2` | [arC56_signaturePolicySplitToArrayOptionalArity2V0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21776) |

#### T022 STRTOK_TO_ARRAY

**45/45 cells; 58 methods; 55 six-output; 3 parser-only.**

| Required cells | Named source witness |
|---|---|
| `placement:Select`<br>`arg1:Qualified`<br>`arg2:Qualified`<br>`form:documented-arity:2` | [snowflakeStrtokToArraySelectSeedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1650) |
| `placement:Where` | [snowflakeStrtokToArrayWherePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1693) |
| `placement:Join` | [arC52_strtokToArrayJoinPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13648) |
| `placement:Having` | [arC52_strtokToArrayHavingPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13667) |
| `placement:Qualify` | [arC52_strtokToArrayQualifyPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13686) |
| `placement:OrderBy` | [snowflakeStrtokToArrayOrderByPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1713) |
| `placement:Values` | [snowflakeStrtokToArrayValuesPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1733) |
| `placement:Insert` | [snowflakeStrtokToArrayInsertPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1753) |
| `placement:Update` | [snowflakeStrtokToArrayUpdatePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1773) |
| `placement:Delete` | [snowflakeStrtokToArrayDeletePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1793) |
| `placement:Ddl` | [arC52_strtokToArrayDdlPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13724) |
| `placement:Script`<br>`deep:script-reset` | [arC52_strtokToArrayScriptPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13748) |
| `arg1:Unqualified` | [arC52_strtokToArrayArg1UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13776) |
| `arg1:Unresolved` | [arC52_strtokToArrayArg1UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13795) |
| `arg1:Ambiguous` | [arC52_strtokToArrayArg1AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13818) |
| `arg1:MissingAlias` | [snowflakeStrtokToArrayMissingAliasFatalV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1670) |
| `arg1:HiddenAlias` | [arC52_strtokToArrayArg1HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13845) |
| `arg1:PredicandVariable` | [arC52_strtokToArrayArg1PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13867) |
| `arg1:ColumnVariable` | [arC52_strtokToArrayArg1ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13886) |
| `arg1:Null` | [arC52_strtokToArrayArg1NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13905) |
| `arg2:Unqualified` | [arC52_strtokToArrayArg2UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13924) |
| `arg2:Unresolved` | [arC52_strtokToArrayArg2UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13943) |
| `arg2:Ambiguous` | [arC52_strtokToArrayArg2AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13966) |
| `arg2:MissingAlias` | [arC52_strtokToArrayArg2MissingAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L13993) |
| `arg2:HiddenAlias` | [arC52_strtokToArrayArg2HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14015) |
| `arg2:PredicandVariable` | [arC52_strtokToArrayArg2PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14037) |
| `arg2:ColumnVariable` | [arC52_strtokToArrayArg2ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14056) |
| `arg2:Null` | [arC52_strtokToArrayArg2NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14075) |
| `combined:predicand` | [arC52_strtokToArrayAllPredicandVariablesV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14113) |
| `combined:column` | [arC52_strtokToArrayAllColumnVariablesV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14132) |
| `deep:nested` | [arC52_strtokToArrayNestedExpressionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14151) |
| `deep:derived` | [arC52_strtokToArrayDerivedOutputV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14170) |
| `deep:setop` | [arC52_strtokToArraySetOpSiblingIsolationV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14189) |
| `neutral:udf` | [arC52_strtokToArrayQualifiedUdfNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14208) |
| `neutral:column` | [arC52_strtokToArraySameNamedColumnNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14223) |
| `dialect:uppercase` | [arC52_strtokToArrayUppercaseV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14238) |
| `dialect:mixed` | [arC52_strtokToArrayMixedDialectV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14257) |
| `syntax:TrailingComma` | [arC52_strtokToArrayTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14279) |
| `syntax:MissingRightParenthesis` | [arC52_strtokToArrayMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14304) |
| `syntax:MissingComma` | [arC52_strtokToArrayMissingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14329) |
| `form:documented-arity:1` | [arC52_strtokToArrayDefaultDelimiterQualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14449) |

| Supporting catalog labels (not extra required cells) | Named source witness |
|---|---|
| `legacy:values-column-control` | [snowflakeStrtokToArrayValuesPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1733) |
| `input:empty-string` | [arC52_strtokToArrayEmptyStringV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14354) |
| `input:empty-delimiter` | [arC52_strtokToArrayEmptyDelimiterV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14373) |
| `input:repeated-delimiter` | [arC52_strtokToArrayRepeatedDelimiterV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14392) |
| `input:delimiter-set` | [arC52_strtokToArrayDelimiterSequenceV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14411) |
| `input:both-null` | [arC52_strtokToArrayBothNullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14430) |
| `optional:qualified` | [arC52_strtokToArrayDefaultDelimiterQualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14449) |
| `optional:unqualified` | [arC52_strtokToArrayDefaultDelimiterUnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14468) |
| `optional:predicand` | [arC52_strtokToArrayDefaultDelimiterPredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14487) |
| `optional:column` | [arC52_strtokToArrayDefaultDelimiterColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14506) |
| `optional:null` | [arC52_strtokToArrayDefaultDelimiterNullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14525) |
| `optional:literal` | [arC52_strtokToArrayDefaultDelimiterLiteralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14544) |
| `strtok_to_array:RequiredForm` | [arC56_signaturePolicyStrtokToArrayRequiredFormV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20336) |
| `strtok_to_array:EmptyArgumentsCompatibility` | [arC56_signaturePolicyStrtokToArrayEmptyArgumentsCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20372) |
| `strtok_to_array:AboveDocumentedMaximum` | [arC56_signaturePolicyStrtokToArrayAboveDocumentedMaximumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20404) |
| `strtok_to_array:RuntimeShapeBoundary` | [arC56_signaturePolicyStrtokToArrayRuntimeShapeBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20440) |
| `strtok_to_array:OptionalArity2` | [arC56_signaturePolicyStrtokToArrayOptionalArity2V0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21812) |

#### T022 ARRAY_GENERATE_RANGE

**54/54 cells; 87 methods; 83 six-output; 4 parser-only.**

| Required cells | Named source witness |
|---|---|
| `placement:Select`<br>`form:documented-arity:2` | [snowflakeArrayGenerateRangeSelectSeedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1064) |
| `placement:Where` | [snowflakeArrayGenerateRangeWherePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1410) |
| `placement:Join` | [arC53_arrayGenerateRangeJoinPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14601) |
| `placement:Having` | [arC53_arrayGenerateRangeHavingPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14620) |
| `placement:Qualify` | [arC53_arrayGenerateRangeQualifyPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14639) |
| `placement:OrderBy` | [snowflakeArrayGenerateRangeOrderByPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1430) |
| `placement:Values` | [snowflakeArrayGenerateRangeValuesPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1450) |
| `placement:Insert` | [snowflakeArrayGenerateRangeInsertPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1470) |
| `placement:Update` | [snowflakeArrayGenerateRangeUpdatePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1490) |
| `placement:Delete` | [snowflakeArrayGenerateRangeDeletePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1510) |
| `placement:Ddl` | [arC53_arrayGenerateRangeDdlPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14753) |
| `placement:Script`<br>`deep:script-reset` | [arC53_arrayGenerateRangeScriptPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14777) |
| `arg1:Qualified`<br>`arg2:Qualified`<br>`arg3:Qualified`<br>`form:documented-arity:3` | [arC53_arrayGenerateRangeSelectPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14563) |
| `arg1:Unqualified` | [arC53_arrayGenerateRangeArg1UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14805) |
| `arg1:Unresolved` | [arC53_arrayGenerateRangeArg1UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14824) |
| `arg1:Ambiguous` | [arC53_arrayGenerateRangeArg1AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14847) |
| `arg1:MissingAlias` | [arC53_arrayGenerateRangeArg1MissingAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14874) |
| `arg1:HiddenAlias` | [arC53_arrayGenerateRangeArg1HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14896) |
| `arg1:PredicandVariable` | [arC53_arrayGenerateRangeArg1PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14918) |
| `arg1:ColumnVariable` | [arC53_arrayGenerateRangeArg1ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14937) |
| `arg1:Null` | [arC53_arrayGenerateRangeArg1NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14956) |
| `arg2:Unqualified` | [arC53_arrayGenerateRangeArg2UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14975) |
| `arg2:Unresolved` | [arC53_arrayGenerateRangeArg2UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L14994) |
| `arg2:Ambiguous` | [arC53_arrayGenerateRangeArg2AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15017) |
| `arg2:MissingAlias` | [arC53_arrayGenerateRangeArg2MissingAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15044) |
| `arg2:HiddenAlias` | [arC53_arrayGenerateRangeArg2HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15066) |
| `arg2:PredicandVariable` | [arC53_arrayGenerateRangeArg2PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15088) |
| `arg2:ColumnVariable` | [arC53_arrayGenerateRangeArg2ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15107) |
| `arg2:Null` | [arC53_arrayGenerateRangeArg2NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15126) |
| `arg3:Unqualified` | [arC53_arrayGenerateRangeArg3UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15145) |
| `arg3:Unresolved` | [arC53_arrayGenerateRangeArg3UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15164) |
| `arg3:Ambiguous` | [arC53_arrayGenerateRangeArg3AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15187) |
| `arg3:MissingAlias` | [arC53_arrayGenerateRangeArg3MissingAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15214) |
| `arg3:HiddenAlias` | [arC53_arrayGenerateRangeArg3HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15236) |
| `arg3:PredicandVariable` | [arC53_arrayGenerateRangeArg3PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15258) |
| `arg3:ColumnVariable` | [arC53_arrayGenerateRangeArg3ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15277) |
| `arg3:Null` | [arC53_arrayGenerateRangeArg3NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15296) |
| `combined:predicand` | [arC53_arrayGenerateRangeAllPredicandVariablesV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15334) |
| `combined:column` | [arC53_arrayGenerateRangeAllColumnVariablesV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15353) |
| `deep:nested` | [arC53_arrayGenerateRangeNestedExpressionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15372) |
| `deep:derived` | [arC53_arrayGenerateRangeDerivedOutputV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15391) |
| `deep:setop` | [arC53_arrayGenerateRangeSetOpSiblingIsolationV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15410) |
| `neutral:udf` | [arC53_arrayGenerateRangeQualifiedUdfNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15429) |
| `neutral:column` | [arC53_arrayGenerateRangeSameNamedColumnNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15444) |
| `dialect:uppercase` | [arC53_arrayGenerateRangeUppercaseV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15459) |
| `dialect:mixed` | [arC53_arrayGenerateRangeMixedDialectV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15478) |
| `syntax:TrailingComma` | [arC53_arrayGenerateRangeTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15500) |
| `syntax:MissingRightParenthesis` | [arC53_arrayGenerateRangeMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15525) |
| `syntax:MissingComma` | [arC53_arrayGenerateRangeMissingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15550) |

| Supporting catalog labels (not extra required cells) | Named source witness |
|---|---|
| `form:DefaultStepLiteral:Select` | [snowflakeArrayGenerateRangeSelectSeedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1064) |
| `form:DefaultStepLiteral:Where` | [snowflakeArrayGenerateRangeWherePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1410) |
| `form:DefaultStepLiteral:OrderBy` | [snowflakeArrayGenerateRangeOrderByPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1430) |
| `form:DefaultStepLiteral:Values` | [snowflakeArrayGenerateRangeValuesPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1450) |
| `form:DefaultStepLiteral:Insert` | [snowflakeArrayGenerateRangeInsertPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1470) |
| `form:DefaultStepLiteral:Update` | [snowflakeArrayGenerateRangeUpdatePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1490) |
| `form:DefaultStepLiteral:Delete` | [snowflakeArrayGenerateRangeDeletePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1510) |
| `form:DefaultStep:placement:Select` | [arC53_arrayGenerateRangeDefaultStepSelectPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15575) |
| `form:DefaultStep:arg1:Unqualified` | [arC53_arrayGenerateRangeDefaultStepArg1UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15594) |
| `form:DefaultStep:arg1:PredicandVariable` | [arC53_arrayGenerateRangeDefaultStepArg1PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15613) |
| `form:DefaultStep:arg1:ColumnVariable` | [arC53_arrayGenerateRangeDefaultStepArg1ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15632) |
| `form:DefaultStep:arg1:Null` | [arC53_arrayGenerateRangeDefaultStepArg1NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15651) |
| `form:DefaultStep:arg2:Unqualified` | [arC53_arrayGenerateRangeDefaultStepArg2UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15670) |
| `form:DefaultStep:arg2:PredicandVariable` | [arC53_arrayGenerateRangeDefaultStepArg2PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15689) |
| `form:DefaultStep:arg2:ColumnVariable` | [arC53_arrayGenerateRangeDefaultStepArg2ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15708) |
| `form:DefaultStep:arg2:Null` | [arC53_arrayGenerateRangeDefaultStepArg2NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15727) |
| `form:DefaultStep:combined:predicand` | [arC53_arrayGenerateRangeDefaultStepAllPredicandVariablesV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15746) |
| `form:DefaultStep:combined:column` | [arC53_arrayGenerateRangeDefaultStepAllColumnVariablesV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15765) |
| `form:DefaultStep:deep:nested` | [arC53_arrayGenerateRangeDefaultStepNestedExpressionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15784) |
| `form:DefaultStep:deep:derived` | [arC53_arrayGenerateRangeDefaultStepDerivedOutputV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15803) |
| `form:DefaultStep:deep:setop` | [arC53_arrayGenerateRangeDefaultStepSetOpSiblingIsolationV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15822) |
| `input:positive-step` | [arC53_arrayGenerateRangePositiveStepV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15841) |
| `input:negative-step` | [arC53_arrayGenerateRangeNegativeStepV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15860) |
| `input:equal-bounds` | [arC53_arrayGenerateRangeEqualBoundsV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15879) |
| `input:positive-step-descending-bounds` | [arC53_arrayGenerateRangePositiveStepDescendingBoundsV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15898) |
| `input:negative-step-ascending-bounds` | [arC53_arrayGenerateRangeNegativeStepAscendingBoundsV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15917) |
| `boundary:zero-step-runtime-validation` | [arC53_arrayGenerateRangeZeroStepRuntimeBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15936) |
| `input:integer-expressions` | [arC53_arrayGenerateRangeArithmeticOperandsV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15955) |
| `input:all-null` | [arC53_arrayGenerateRangeAllNullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15974) |
| `form:DefaultStep:all-null` | [arC53_arrayGenerateRangeDefaultStepAllNullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L15993) |
| `array_generate_range:RequiredForm` | [arC56_signaturePolicyArrayGenerateRangeRequiredFormV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20476) |
| `array_generate_range:EmptyArgumentsCompatibility` | [arC56_signaturePolicyArrayGenerateRangeEmptyArgumentsCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20512) |
| `array_generate_range:BelowDocumentedMinimum` | [arC56_signaturePolicyArrayGenerateRangeBelowDocumentedMinimumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20544) |
| `array_generate_range:AboveDocumentedMaximum` | [arC56_signaturePolicyArrayGenerateRangeAboveDocumentedMaximumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20580) |
| `array_generate_range:RuntimeShapeBoundary` | [arC56_signaturePolicyArrayGenerateRangeRuntimeShapeBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20616) |
| `array_generate_range:OptionalArity3` | [arC56_signaturePolicyArrayGenerateRangeOptionalArity3V0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21848) |
| `array_generate_range:QualifiedArityCompatibility` | [arC56_signaturePolicyArrayGenerateRangeQualifiedArityCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22942) |
| `array_generate_range:qualified-malformed-comma` | [arC56_signaturePolicyArrayGenerateRangeQualifiedTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22974) |

#### T022 ARRAY_POSITION

**44/44 cells; 57 methods; 53 six-output; 4 parser-only.**

| Required cells | Named source witness |
|---|---|
| `placement:Select`<br>`arg1:Qualified`<br>`arg2:Qualified`<br>`form:documented-arity:2` | [snowflakeArrayPositionSelectSeedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1084) |
| `placement:Where` | [snowflakeArrayPositionWherePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1127) |
| `placement:Join` | [arC54_arrayPositionJoinPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16012) |
| `placement:Having` | [arC54_arrayPositionHavingPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16031) |
| `placement:Qualify` | [arC54_arrayPositionQualifyPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16050) |
| `placement:OrderBy` | [snowflakeArrayPositionOrderByPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1147) |
| `placement:Values` | [snowflakeArrayPositionValuesPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1167) |
| `placement:Insert` | [snowflakeArrayPositionInsertPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1187) |
| `placement:Update` | [snowflakeArrayPositionUpdatePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1207) |
| `placement:Delete` | [snowflakeArrayPositionDeletePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1227) |
| `placement:Ddl` | [arC54_arrayPositionDdlPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16088) |
| `placement:Script`<br>`deep:script-reset` | [arC54_arrayPositionScriptPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16112) |
| `arg1:Unqualified` | [arC54_arrayPositionArg1UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16140) |
| `arg1:Unresolved` | [arC54_arrayPositionArg1UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16159) |
| `arg1:Ambiguous` | [arC54_arrayPositionArg1AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16182) |
| `arg1:MissingAlias` | [snowflakeArrayPositionMissingAliasFatalV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1104) |
| `arg1:HiddenAlias` | [arC54_arrayPositionArg1HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16209) |
| `arg1:PredicandVariable` | [arC54_arrayPositionArg1PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16231) |
| `arg1:ColumnVariable` | [arC54_arrayPositionArg1ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16250) |
| `arg1:Null` | [arC54_arrayPositionArg1NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16269) |
| `arg2:Unqualified` | [arC54_arrayPositionArg2UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16288) |
| `arg2:Unresolved` | [arC54_arrayPositionArg2UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16307) |
| `arg2:Ambiguous` | [arC54_arrayPositionArg2AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16330) |
| `arg2:MissingAlias` | [arC54_arrayPositionArg2MissingAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16357) |
| `arg2:HiddenAlias` | [arC54_arrayPositionArg2HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16379) |
| `arg2:PredicandVariable` | [arC54_arrayPositionArg2PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16401) |
| `arg2:ColumnVariable` | [arC54_arrayPositionArg2ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16420) |
| `arg2:Null` | [arC54_arrayPositionArg2NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16439) |
| `combined:predicand` | [arC54_arrayPositionAllPredicandVariablesV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16477) |
| `combined:column` | [arC54_arrayPositionAllColumnVariablesV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16496) |
| `deep:nested` | [arC54_arrayPositionNestedExpressionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16515) |
| `deep:derived` | [arC54_arrayPositionDerivedOutputV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16534) |
| `deep:setop` | [arC54_arrayPositionSetOpSiblingIsolationV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16553) |
| `neutral:udf` | [arC54_arrayPositionQualifiedUdfNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16572) |
| `neutral:column` | [arC54_arrayPositionSameNamedColumnNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16587) |
| `dialect:uppercase` | [arC54_arrayPositionUppercaseV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16602) |
| `dialect:mixed` | [arC54_arrayPositionMixedDialectV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16621) |
| `syntax:TrailingComma` | [arC54_arrayPositionTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16643) |
| `syntax:MissingRightParenthesis` | [arC54_arrayPositionMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16668) |
| `syntax:MissingComma` | [arC54_arrayPositionMissingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16693) |

| Supporting catalog labels (not extra required cells) | Named source witness |
|---|---|
| `form:LegacyValues:Values` | [snowflakeArrayPositionValuesPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1167) |
| `input:empty-array` | [arC54_arrayPositionEmptyArrayV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16718) |
| `input:first-element` | [arC54_arrayPositionFirstElementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16737) |
| `input:duplicate-needle` | [arC54_arrayPositionDuplicateNeedleV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16756) |
| `input:absent-needle` | [arC54_arrayPositionAbsentNeedleV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16775) |
| `input:null-element-needle` | [arC54_arrayPositionNullElementNeedleV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16794) |
| `input:both-null` | [arC54_arrayPositionBothNullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16813) |
| `input:needle-expression` | [arC54_arrayPositionExpressionNeedleV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16832) |
| `composition:case-projection` | [arC54_arrayPositionCaseProjectionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16851) |
| `array_position:RequiredForm` | [arC56_signaturePolicyArrayPositionRequiredFormV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20652) |
| `array_position:EmptyArgumentsCompatibility` | [arC56_signaturePolicyArrayPositionEmptyArgumentsCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20688) |
| `array_position:BelowDocumentedMinimum` | [arC56_signaturePolicyArrayPositionBelowDocumentedMinimumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20720) |
| `array_position:AboveDocumentedMaximum` | [arC56_signaturePolicyArrayPositionAboveDocumentedMaximumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20756) |
| `array_position:RuntimeShapeBoundary` | [arC56_signaturePolicyArrayPositionRuntimeShapeBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20792) |
| `array_position:QualifiedArityCompatibility` | [arC56_signaturePolicyArrayPositionQualifiedArityCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22999) |
| `array_position:qualified-malformed-comma` | [arC56_signaturePolicyArrayPositionQualifiedTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23031) |

#### T022 ARRAYS_ZIP

**65/65 cells; 95 methods; 91 six-output; 4 parser-only.**

| Required cells | Named source witness |
|---|---|
| `placement:Select`<br>`form:documented-arity:2` | [snowflakeArraysZipSelectSeedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1247) |
| `placement:Where` | [snowflakeArraysZipWherePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1530) |
| `placement:Join` | [arC55_arraysZipJoinPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16908) |
| `placement:Having` | [arC55_arraysZipHavingPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16927) |
| `placement:Qualify` | [arC55_arraysZipQualifyPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16946) |
| `placement:OrderBy` | [snowflakeArraysZipOrderByPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1550) |
| `placement:Values` | [snowflakeArraysZipValuesPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1570) |
| `placement:Insert` | [snowflakeArraysZipInsertPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1590) |
| `placement:Update` | [snowflakeArraysZipUpdatePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1610) |
| `placement:Delete` | [snowflakeArraysZipDeletePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1630) |
| `placement:Ddl` | [arC55_arraysZipDdlPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17060) |
| `placement:Script`<br>`deep:script-reset` | [arC55_arraysZipScriptPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17084) |
| `arg1:Qualified`<br>`arg2:Qualified`<br>`arg3:Qualified`<br>`form:documented-arity:3` | [arC55_arraysZipSelectPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L16870) |
| `arg1:Unqualified` | [arC55_arraysZipArg1UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17112) |
| `arg1:Unresolved` | [arC55_arraysZipArg1UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17131) |
| `arg1:Ambiguous` | [arC55_arraysZipArg1AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17154) |
| `arg1:MissingAlias` | [arC55_arraysZipArg1MissingAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17181) |
| `arg1:HiddenAlias` | [arC55_arraysZipArg1HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17203) |
| `arg1:PredicandVariable` | [arC55_arraysZipArg1PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17225) |
| `arg1:ColumnVariable` | [arC55_arraysZipArg1ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17244) |
| `arg1:Null` | [arC55_arraysZipArg1NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17263) |
| `arg2:Unqualified` | [arC55_arraysZipArg2UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17282) |
| `arg2:Unresolved` | [arC55_arraysZipArg2UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17301) |
| `arg2:Ambiguous` | [arC55_arraysZipArg2AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17324) |
| `arg2:MissingAlias` | [arC55_arraysZipArg2MissingAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17351) |
| `arg2:HiddenAlias` | [arC55_arraysZipArg2HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17373) |
| `arg2:PredicandVariable` | [arC55_arraysZipArg2PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17395) |
| `arg2:ColumnVariable` | [arC55_arraysZipArg2ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17414) |
| `arg2:Null` | [arC55_arraysZipArg2NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17433) |
| `arg3:Unqualified` | [arC55_arraysZipArg3UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17452) |
| `arg3:Unresolved` | [arC55_arraysZipArg3UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17471) |
| `arg3:Ambiguous` | [arC55_arraysZipArg3AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17494) |
| `arg3:MissingAlias` | [arC55_arraysZipArg3MissingAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17521) |
| `arg3:HiddenAlias` | [arC55_arraysZipArg3HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17543) |
| `arg3:PredicandVariable` | [arC55_arraysZipArg3PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17565) |
| `arg3:ColumnVariable` | [arC55_arraysZipArg3ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17584) |
| `arg3:Null` | [arC55_arraysZipArg3NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17603) |
| `arg4:Qualified`<br>`form:documented-arity:4` | [arC55_arraysZipFourArraysSelectPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18128) |
| `arg4:Unqualified` | [arC55_arraysZipFourArraysArg4UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18147) |
| `arg4:Unresolved` | [arC55_arraysZipFourArraysArg4UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18166) |
| `arg4:Ambiguous` | [arC55_arraysZipFourArraysArg4AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18189) |
| `arg4:MissingAlias` | [arC55_arraysZipFourArraysArg4MissingAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18216) |
| `arg4:HiddenAlias` | [arC55_arraysZipFourArraysArg4HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18238) |
| `arg4:PredicandVariable` | [arC55_arraysZipFourArraysArg4PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18260) |
| `arg4:ColumnVariable` | [arC55_arraysZipFourArraysArg4ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18279) |
| `arg4:Null` | [arC55_arraysZipFourArraysArg4NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18298) |
| `combined:predicand` | [arC55_arraysZipAllPredicandVariablesV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17641) |
| `combined:column` | [arC55_arraysZipAllColumnVariablesV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17660) |
| `deep:nested` | [arC55_arraysZipNestedExpressionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17679) |
| `deep:derived` | [arC55_arraysZipDerivedOutputV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17698) |
| `deep:setop` | [arC55_arraysZipSetOpSiblingIsolationV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17717) |
| `neutral:udf` | [arC55_arraysZipQualifiedUdfNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17736) |
| `neutral:column` | [arC55_arraysZipSameNamedColumnNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17751) |
| `dialect:uppercase` | [arC55_arraysZipUppercaseV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17766) |
| `dialect:mixed` | [arC55_arraysZipMixedDialectV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17785) |
| `syntax:TrailingComma` | [arC55_arraysZipTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17807) |
| `syntax:MissingRightParenthesis` | [arC55_arraysZipMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17832) |
| `syntax:MissingComma` | [arC55_arraysZipMissingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17857) |
| `form:documented-arity:1` | [arC55_arraysZipSingleArraySelectPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17882) |

| Supporting catalog labels (not extra required cells) | Named source witness |
|---|---|
| `form:TwoArrays:Select` | [snowflakeArraysZipSelectSeedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1247) |
| `form:TwoArrays:arg1:MissingAlias` | [snowflakeArraysZipMissingAliasFatalV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1267) |
| `form:TwoArrays:Where` | [snowflakeArraysZipWherePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1530) |
| `form:TwoArrays:OrderBy` | [snowflakeArraysZipOrderByPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1550) |
| `form:TwoArrays:Values` | [snowflakeArraysZipValuesPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1570) |
| `form:TwoArrays:Insert` | [snowflakeArraysZipInsertPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1590) |
| `form:TwoArrays:Update` | [snowflakeArraysZipUpdatePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1610) |
| `form:TwoArrays:Delete` | [snowflakeArraysZipDeletePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L1630) |
| `form:SingleArray:placement:Select` | [arC55_arraysZipSingleArraySelectPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17882) |
| `form:SingleArray:arg1:Unqualified` | [arC55_arraysZipSingleArrayArg1UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17901) |
| `form:SingleArray:arg1:Unresolved` | [arC55_arraysZipSingleArrayArg1UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17920) |
| `form:SingleArray:arg1:Ambiguous` | [arC55_arraysZipSingleArrayArg1AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17943) |
| `form:SingleArray:arg1:MissingAlias` | [arC55_arraysZipSingleArrayArg1MissingAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17970) |
| `form:SingleArray:arg1:HiddenAlias` | [arC55_arraysZipSingleArrayArg1HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L17992) |
| `form:SingleArray:arg1:PredicandVariable` | [arC55_arraysZipSingleArrayArg1PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18014) |
| `form:SingleArray:arg1:ColumnVariable` | [arC55_arraysZipSingleArrayArg1ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18033) |
| `form:SingleArray:arg1:Null` | [arC55_arraysZipSingleArrayArg1NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18052) |
| `form:SingleArray:deep:nested` | [arC55_arraysZipSingleArrayNestedExpressionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18071) |
| `form:SingleArray:deep:derived` | [arC55_arraysZipSingleArrayDerivedOutputV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18090) |
| `form:SingleArray:deep:setop` | [arC55_arraysZipSingleArraySetOpSiblingIsolationV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18109) |
| `form:FourArrays:placement:Select` | [arC55_arraysZipFourArraysSelectPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18128) |
| `form:FourArrays:arg4:Unqualified` | [arC55_arraysZipFourArraysArg4UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18147) |
| `form:FourArrays:arg4:Unresolved` | [arC55_arraysZipFourArraysArg4UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18166) |
| `form:FourArrays:arg4:Ambiguous` | [arC55_arraysZipFourArraysArg4AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18189) |
| `form:FourArrays:arg4:MissingAlias` | [arC55_arraysZipFourArraysArg4MissingAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18216) |
| `form:FourArrays:arg4:HiddenAlias` | [arC55_arraysZipFourArraysArg4HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18238) |
| `form:FourArrays:arg4:PredicandVariable` | [arC55_arraysZipFourArraysArg4PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18260) |
| `form:FourArrays:arg4:ColumnVariable` | [arC55_arraysZipFourArraysArg4ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18279) |
| `form:FourArrays:arg4:Null` | [arC55_arraysZipFourArraysArg4NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18298) |
| `form:FourArrays:combined:predicand` | [arC55_arraysZipFourArraysAllPredicandVariablesV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18317) |
| `form:FourArrays:combined:column` | [arC55_arraysZipFourArraysAllColumnVariablesV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18336) |
| `form:TwoArrays:arg2:MissingAlias` | [arC55_arraysZipTwoArraysArg2MissingAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18355) |
| `form:TwoArrays:arg2:PredicandVariable` | [arC55_arraysZipTwoArraysArg2PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18377) |
| `form:TwoArrays:arg2:ColumnVariable` | [arC55_arraysZipTwoArraysArg2ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18396) |
| `input:unequal-lengths` | [arC55_arraysZipUnequalLengthsV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18415) |
| `input:all-empty` | [arC55_arraysZipAllEmptyArraysV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18434) |
| `input:first-empty` | [arC55_arraysZipFirstArrayEmptyV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18453) |
| `input:second-empty` | [arC55_arraysZipSecondArrayEmptyV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18472) |
| `input:third-empty` | [arC55_arraysZipThirdArrayEmptyV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18491) |
| `input:null-elements` | [arC55_arraysZipNullElementsV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18510) |
| `input:all-null` | [arC55_arraysZipAllNullArraysV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L18529) |
| `arrays_zip:RequiredForm` | [arC56_signaturePolicyArraysZipRequiredFormV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20828) |
| `arrays_zip:EmptyArgumentsCompatibility` | [arC56_signaturePolicyArraysZipEmptyArgumentsCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20864) |
| `arrays_zip:RuntimeShapeBoundary` | [arC56_signaturePolicyArraysZipRuntimeShapeBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20896) |
| `arrays_zip:QualifiedArityCompatibility` | [arC56_signaturePolicyArraysZipQualifiedArityCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23056) |
| `arrays_zip:qualified-malformed-comma` | [arC56_signaturePolicyArraysZipQualifiedTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23088) |

#### T022 ARRAYS_TO_OBJECT

**44/44 cells; 48 methods; 45 six-output; 3 parser-only.**

| Required cells | Named source witness |
|---|---|
| `placement:Select`<br>`arg1:Qualified`<br>`arg2:Qualified`<br>`form:documented-arity:2` | [arC40_arraysToObjectSelectPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4720) |
| `placement:Where` | [arC40_arraysToObjectWherePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4741) |
| `placement:Join` | [arC40_arraysToObjectJoinOnPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4762) |
| `placement:Having` | [arC40_arraysToObjectHavingPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4783) |
| `placement:Qualify` | [arC40_arraysToObjectQualifyPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4804) |
| `placement:OrderBy` | [arC40_arraysToObjectOrderByPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4825) |
| `placement:Values`<br>`combined:predicand` | [arC40_arraysToObjectValuesPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4846) |
| `placement:Insert` | [arC40_arraysToObjectInsertPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4867) |
| `placement:Update` | [arC40_arraysToObjectUpdatePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4888) |
| `placement:Delete` | [arC40_arraysToObjectDeletePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4909) |
| `placement:Ddl` | [arC40_arraysToObjectDdlPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4930) |
| `placement:Script`<br>`deep:script-reset` | [arR20_arraysToObjectScriptStatementResetV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4952) |
| `arg1:Unqualified` | [arC57_matrixAuditArraysToObjectArg1UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23317) |
| `arg1:Unresolved` | [arM20_arraysToObjectUnresolvedArgument1V0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5063) |
| `arg1:Ambiguous` | [arM21_arraysToObjectAmbiguousArgument1V0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5115) |
| `arg1:MissingAlias` | [arM22_arraysToObjectMissingAliasArgument1V0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5177) |
| `arg1:HiddenAlias` | [arM22_arraysToObjectHiddenAliasArgument1V0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5227) |
| `arg1:PredicandVariable` | [arC40_arraysToObjectPredicandVariableArgument1V0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5277) |
| `arg1:ColumnVariable` | [arC40_arraysToObjectQualifiedColumnVariableArgument1V0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5319) |
| `arg1:Null` | [arC40_arraysToObjectNullArgument1V0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5361) |
| `arg2:Unqualified` | [arC57_matrixAuditArraysToObjectArg2UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23353) |
| `arg2:Unresolved` | [arM20_arraysToObjectUnresolvedArgument2V0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5089) |
| `arg2:Ambiguous` | [arM21_arraysToObjectAmbiguousArgument2V0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5146) |
| `arg2:MissingAlias` | [arM22_arraysToObjectMissingAliasArgument2V0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5202) |
| `arg2:HiddenAlias` | [arM22_arraysToObjectHiddenAliasArgument2V0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5252) |
| `arg2:PredicandVariable` | [arC40_arraysToObjectPredicandVariableArgument2V0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5298) |
| `arg2:ColumnVariable` | [arC40_arraysToObjectQualifiedColumnVariableArgument2V0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5340) |
| `arg2:Null` | [arC40_arraysToObjectNullArgument2V0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5382) |
| `combined:column` | [arC40_arraysToObjectCombinedColumnVariablesV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5403) |
| `deep:nested` | [arR18_arraysToObjectNestedExpressionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L4979) |
| `deep:derived` | [arR18_arraysToObjectDerivedOutputBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5000) |
| `deep:setop` | [arR19_arraysToObjectSetOpSiblingIsolationV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5021) |
| `neutral:udf` | [arM23_arraysToObjectSchemaQualifiedRoutineControlV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5424) |
| `neutral:column` | [arM23_arraysToObjectNonreservedColumnNameV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5440) |
| `dialect:uppercase` | [arC40_arraysToObjectUppercaseNameV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5481) |
| `dialect:mixed` | [arM24_arraysToObjectMixedDialectControlV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5456) |
| `syntax:TrailingComma` | [arM24_arraysToObjectTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5565) |
| `syntax:MissingRightParenthesis` | [arC57_matrixAuditArraysToObjectMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23389) |
| `syntax:MissingComma` | [arM24_arraysToObjectMissingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L5593) |

| Supporting catalog labels (not extra required cells) | Named source witness |
|---|---|
| `arrays_to_object:RequiredForm` | [arC56_signaturePolicyArraysToObjectRequiredFormV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20932) |
| `arrays_to_object:EmptyArgumentsCompatibility` | [arC56_signaturePolicyArraysToObjectEmptyArgumentsCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L20968) |
| `arrays_to_object:BelowDocumentedMinimum` | [arC56_signaturePolicyArraysToObjectBelowDocumentedMinimumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21000) |
| `arrays_to_object:AboveDocumentedMaximum` | [arC56_signaturePolicyArraysToObjectAboveDocumentedMaximumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21036) |
| `arrays_to_object:RuntimeShapeBoundary` | [arC56_signaturePolicyArraysToObjectRuntimeShapeBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21072) |
| `arrays_to_object:arg1:Unqualified` | [arC57_matrixAuditArraysToObjectArg1UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23317) |
| `arrays_to_object:arg2:Unqualified` | [arC57_matrixAuditArraysToObjectArg2UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23353) |
| `arrays_to_object:syntax:MissingRightParenthesis` | [arC57_matrixAuditArraysToObjectMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L23389) |

#### T022 TO_ARRAY

**32/32 cells; 39 methods; 37 six-output; 2 parser-only.**

| Required cells | Named source witness |
|---|---|
| `placement:Select`<br>`arg1:Qualified`<br>`form:documented-arity:1` | [arC47_toArraySelectPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9665) |
| `placement:Where` | [arC47_toArrayWherePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9684) |
| `placement:Join` | [arC47_toArrayJoinPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9703) |
| `placement:Having` | [arC47_toArrayHavingPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9722) |
| `placement:Qualify` | [arC47_toArrayQualifyPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9741) |
| `placement:OrderBy` | [arC47_toArrayOrderByPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9760) |
| `placement:Values` | [arC47_toArrayValuesPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9779) |
| `placement:Insert` | [arC47_toArrayInsertPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9798) |
| `placement:Update`<br>`arg1:Unqualified` | [arC47_toArrayUpdatePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9817) |
| `placement:Delete` | [arC47_toArrayDeletePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9836) |
| `placement:Ddl` | [arC47_toArrayDdlPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9855) |
| `placement:Script`<br>`deep:script-reset` | [arC47_toArrayScriptPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9879) |
| `arg1:Unresolved` | [arC47_toArrayArg1UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9926) |
| `arg1:Ambiguous` | [arC47_toArrayArg1AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9949) |
| `arg1:MissingAlias` | [arC47_toArrayArg1MissingAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9976) |
| `arg1:HiddenAlias` | [arC47_toArrayArg1HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L9998) |
| `arg1:PredicandVariable` | [arC47_toArrayArg1PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10020) |
| `arg1:ColumnVariable` | [arC47_toArrayArg1ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10039) |
| `arg1:Null` | [arC47_toArrayArg1NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10058) |
| `deep:nested` | [arC47_toArrayNestedExpressionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10077) |
| `deep:derived` | [arC47_toArrayDerivedOutputV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10096) |
| `deep:setop` | [arC47_toArraySetOpSiblingIsolationV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10115) |
| `neutral:udf` | [arC47_toArrayQualifiedUdfNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10134) |
| `neutral:column` | [arC47_toArraySameNamedColumnNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10149) |
| `dialect:uppercase` | [arC47_toArrayUppercaseV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10164) |
| `dialect:mixed` | [arC47_toArrayMixedDialectV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10183) |
| `syntax:TrailingComma` | [arC47_toArrayTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10205) |
| `syntax:MissingRightParenthesis` | [arC47_toArrayMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10230) |

| Supporting catalog labels (not extra required cells) | Named source witness |
|---|---|
| `input:variant-object` | [arC47_toArrayVariantObjectInputV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10255) |
| `input:empty-variant-array` | [arC47_toArrayEmptyVariantArrayV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10274) |
| `input:json-null` | [arC47_toArrayJsonNullInputV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10293) |
| `input:numeric-scalar` | [arC47_toArrayNumericScalarInputV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10312) |
| `input:delimited-string` | [arC47_toArrayDelimitedStringInputV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10331) |
| `input:variant-array` | [arC47_toArrayVariantArrayInputV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10350) |
| `to_array:RequiredForm` | [arC56_signaturePolicyToArrayRequiredFormV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21108) |
| `to_array:EmptyArgumentsCompatibility` | [arC56_signaturePolicyToArrayEmptyArgumentsCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21144) |
| `to_array:AboveDocumentedMaximum` | [arC56_signaturePolicyToArrayAboveDocumentedMaximumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21176) |
| `to_array:RuntimeShapeBoundary` | [arC56_signaturePolicyToArrayRuntimeShapeBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21212) |

#### T022 AS_ARRAY

**32/32 cells; 36 methods; 34 six-output; 2 parser-only.**

| Required cells | Named source witness |
|---|---|
| `placement:Select`<br>`arg1:Qualified`<br>`form:documented-arity:1` | [arC48_asArraySelectPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10369) |
| `placement:Where` | [arC48_asArrayWherePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10388) |
| `placement:Join` | [arC48_asArrayJoinPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10407) |
| `placement:Having` | [arC48_asArrayHavingPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10426) |
| `placement:Qualify` | [arC48_asArrayQualifyPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10445) |
| `placement:OrderBy` | [arC48_asArrayOrderByPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10464) |
| `placement:Values` | [arC48_asArrayValuesPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10483) |
| `placement:Insert` | [arC48_asArrayInsertPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10502) |
| `placement:Update`<br>`arg1:Unqualified` | [arC48_asArrayUpdatePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10521) |
| `placement:Delete` | [arC48_asArrayDeletePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10540) |
| `placement:Ddl` | [arC48_asArrayDdlPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10559) |
| `placement:Script`<br>`deep:script-reset` | [arC48_asArrayScriptPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10583) |
| `arg1:Unresolved` | [arC48_asArrayArg1UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10630) |
| `arg1:Ambiguous` | [arC48_asArrayArg1AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10653) |
| `arg1:MissingAlias` | [arC48_asArrayArg1MissingAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10680) |
| `arg1:HiddenAlias` | [arC48_asArrayArg1HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10702) |
| `arg1:PredicandVariable` | [arC48_asArrayArg1PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10724) |
| `arg1:ColumnVariable` | [arC48_asArrayArg1ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10743) |
| `arg1:Null` | [arC48_asArrayArg1NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10762) |
| `deep:nested` | [arC48_asArrayNestedExpressionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10781) |
| `deep:derived` | [arC48_asArrayDerivedOutputV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10800) |
| `deep:setop` | [arC48_asArraySetOpSiblingIsolationV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10819) |
| `neutral:udf` | [arC48_asArrayQualifiedUdfNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10838) |
| `neutral:column` | [arC48_asArraySameNamedColumnNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10853) |
| `dialect:uppercase` | [arC48_asArrayUppercaseV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10868) |
| `dialect:mixed` | [arC48_asArrayMixedDialectV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10887) |
| `syntax:TrailingComma` | [arC48_asArrayTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10909) |
| `syntax:MissingRightParenthesis` | [arC48_asArrayMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10934) |

| Supporting catalog labels (not extra required cells) | Named source witness |
|---|---|
| `input:variant-object` | [arC48_asArrayVariantObjectInputV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10959) |
| `input:empty-variant-array` | [arC48_asArrayEmptyVariantArrayV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10978) |
| `input:json-null` | [arC48_asArrayJsonNullInputV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L10997) |
| `as_array:RequiredForm` | [arC56_signaturePolicyAsArrayRequiredFormV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21248) |
| `as_array:EmptyArgumentsCompatibility` | [arC56_signaturePolicyAsArrayEmptyArgumentsCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21284) |
| `as_array:AboveDocumentedMaximum` | [arC56_signaturePolicyAsArrayAboveDocumentedMaximumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21316) |
| `as_array:RuntimeShapeBoundary` | [arC56_signaturePolicyAsArrayRuntimeShapeBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21352) |

#### T022 IS_ARRAY

**32/32 cells; 41 methods; 39 six-output; 2 parser-only.**

| Required cells | Named source witness |
|---|---|
| `placement:Select`<br>`arg1:Qualified`<br>`form:documented-arity:1` | [arC49_isArraySelectPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11016) |
| `placement:Where` | [arC49_isArrayWherePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11035) |
| `placement:Join` | [arC49_isArrayJoinPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11054) |
| `placement:Having` | [arC49_isArrayHavingPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11073) |
| `placement:Qualify` | [arC49_isArrayQualifyPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11092) |
| `placement:OrderBy` | [arC49_isArrayOrderByPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11111) |
| `placement:Values` | [arC49_isArrayValuesPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11130) |
| `placement:Insert` | [arC49_isArrayInsertPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11149) |
| `placement:Update`<br>`arg1:Unqualified` | [arC49_isArrayUpdatePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11168) |
| `placement:Delete` | [arC49_isArrayDeletePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11187) |
| `placement:Ddl` | [arC49_isArrayDdlPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11206) |
| `placement:Script`<br>`deep:script-reset` | [arC49_isArrayScriptPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11230) |
| `arg1:Unresolved` | [arC49_isArrayArg1UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11277) |
| `arg1:Ambiguous` | [arC49_isArrayArg1AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11300) |
| `arg1:MissingAlias` | [arC49_isArrayArg1MissingAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11327) |
| `arg1:HiddenAlias` | [arC49_isArrayArg1HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11349) |
| `arg1:PredicandVariable` | [arC49_isArrayArg1PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11371) |
| `arg1:ColumnVariable` | [arC49_isArrayArg1ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11390) |
| `arg1:Null` | [arC49_isArrayArg1NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11409) |
| `deep:nested` | [arC49_isArrayNestedExpressionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11428) |
| `deep:derived` | [arC49_isArrayDerivedOutputV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11447) |
| `deep:setop` | [arC49_isArraySetOpSiblingIsolationV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11466) |
| `neutral:udf` | [arC49_isArrayQualifiedUdfNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11485) |
| `neutral:column` | [arC49_isArraySameNamedColumnNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11500) |
| `dialect:uppercase` | [arC49_isArrayUppercaseV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11515) |
| `dialect:mixed` | [arC49_isArrayMixedDialectV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11534) |
| `syntax:TrailingComma` | [arC49_isArrayTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11556) |
| `syntax:MissingRightParenthesis` | [arC49_isArrayMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11581) |

| Supporting catalog labels (not extra required cells) | Named source witness |
|---|---|
| `input:variant-object` | [arC49_isArrayVariantObjectInputV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11606) |
| `input:empty-variant-array` | [arC49_isArrayEmptyVariantArrayV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11625) |
| `input:json-null` | [arC49_isArrayJsonNullInputV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11644) |
| `predicate:where` | [arC49_isArrayBooleanWhereV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11663) |
| `predicate:join` | [arC49_isArrayBooleanJoinV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11682) |
| `predicate:having` | [arC49_isArrayBooleanHavingV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11701) |
| `predicate:qualify` | [arC49_isArrayBooleanQualifyV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11720) |
| `predicate:case` | [arC49_isArrayBooleanCaseProjectionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11739) |
| `is_array:RequiredForm` | [arC56_signaturePolicyIsArrayRequiredFormV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21388) |
| `is_array:EmptyArgumentsCompatibility` | [arC56_signaturePolicyIsArrayEmptyArgumentsCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21424) |
| `is_array:AboveDocumentedMaximum` | [arC56_signaturePolicyIsArrayAboveDocumentedMaximumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21456) |
| `is_array:RuntimeShapeBoundary` | [arC56_signaturePolicyIsArrayRuntimeShapeBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21492) |

#### T022 SPLIT

**44/44 cells; 55 methods; 51 six-output; 4 parser-only.**

| Required cells | Named source witness |
|---|---|
| `placement:Select`<br>`arg1:Qualified`<br>`arg2:Qualified`<br>`form:documented-arity:2` | [arC50_splitSelectPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11758) |
| `placement:Where` | [arC50_splitWherePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11777) |
| `placement:Join` | [arC50_splitJoinPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11796) |
| `placement:Having` | [arC50_splitHavingPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11815) |
| `placement:Qualify` | [arC50_splitQualifyPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11834) |
| `placement:OrderBy` | [arC50_splitOrderByPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11853) |
| `placement:Values` | [arC50_splitValuesPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11872) |
| `placement:Insert` | [arC50_splitInsertPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11891) |
| `placement:Update` | [arC50_splitUpdatePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11910) |
| `placement:Delete` | [arC50_splitDeletePlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11929) |
| `placement:Ddl` | [arC50_splitDdlPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11948) |
| `placement:Script`<br>`deep:script-reset` | [arC50_splitScriptPlacementV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L11972) |
| `arg1:Unqualified` | [arC50_splitArg1UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12000) |
| `arg1:Unresolved` | [arC50_splitArg1UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12019) |
| `arg1:Ambiguous` | [arC50_splitArg1AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12042) |
| `arg1:MissingAlias` | [arC50_splitArg1MissingAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12069) |
| `arg1:HiddenAlias` | [arC50_splitArg1HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12091) |
| `arg1:PredicandVariable` | [arC50_splitArg1PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12113) |
| `arg1:ColumnVariable` | [arC50_splitArg1ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12132) |
| `arg1:Null` | [arC50_splitArg1NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12151) |
| `arg2:Unqualified` | [arC50_splitArg2UnqualifiedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12170) |
| `arg2:Unresolved` | [arC50_splitArg2UnresolvedV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12189) |
| `arg2:Ambiguous` | [arC50_splitArg2AmbiguousV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12212) |
| `arg2:MissingAlias` | [arC50_splitArg2MissingAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12239) |
| `arg2:HiddenAlias` | [arC50_splitArg2HiddenAliasV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12261) |
| `arg2:PredicandVariable` | [arC50_splitArg2PredicandVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12283) |
| `arg2:ColumnVariable` | [arC50_splitArg2ColumnVariableV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12302) |
| `arg2:Null` | [arC50_splitArg2NullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12321) |
| `combined:predicand` | [arC50_splitAllPredicandVariablesV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12359) |
| `combined:column` | [arC50_splitAllColumnVariablesV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12378) |
| `deep:nested` | [arC50_splitNestedExpressionV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12397) |
| `deep:derived` | [arC50_splitDerivedOutputV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12416) |
| `deep:setop` | [arC50_splitSetOpSiblingIsolationV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12435) |
| `neutral:udf` | [arC50_splitQualifiedUdfNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12454) |
| `neutral:column` | [arC50_splitSameNamedColumnNeutralV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12469) |
| `dialect:uppercase` | [arC50_splitUppercaseV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12484) |
| `dialect:mixed` | [arC50_splitMixedDialectV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12503) |
| `syntax:TrailingComma` | [arC50_splitTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12525) |
| `syntax:MissingRightParenthesis` | [arC50_splitMissingRightParenthesisSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12550) |
| `syntax:MissingComma` | [arC50_splitMissingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12575) |

| Supporting catalog labels (not extra required cells) | Named source witness |
|---|---|
| `input:empty-string` | [arC50_splitEmptyStringV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12600) |
| `input:empty-delimiter` | [arC50_splitEmptyDelimiterV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12619) |
| `input:repeated-delimiter` | [arC50_splitRepeatedDelimiterV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12638) |
| `input:delimiter-substring` | [arC50_splitDelimiterSequenceV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12657) |
| `input:both-null` | [arC50_splitBothNullV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12676) |
| `composition:subscript` | [arC50_splitResultSubscriptV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12695) |
| `composition:flatten` | [arC50_splitFlattenResultV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L12714) |
| `split:RequiredForm` | [arC56_signaturePolicySplitRequiredFormV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21528) |
| `split:EmptyArgumentsCompatibility` | [arC56_signaturePolicySplitEmptyArgumentsCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21564) |
| `split:BelowDocumentedMinimum` | [arC56_signaturePolicySplitBelowDocumentedMinimumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21596) |
| `split:AboveDocumentedMaximum` | [arC56_signaturePolicySplitAboveDocumentedMaximumV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21632) |
| `split:RuntimeShapeBoundary` | [arC56_signaturePolicySplitRuntimeShapeBoundaryV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L21668) |
| `split:QualifiedArityCompatibility` | [arC56_signaturePolicySplitQualifiedArityCompatibilityV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22885) |
| `split:qualified-malformed-comma` | [arC56_signaturePolicySplitQualifiedTrailingCommaSyntaxV0Test](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java#L22917) |
