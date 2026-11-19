# Slice D array-function reconciliation

Date: 2026-11-18. Source-audit baseline: commit `5604d3c`.
Planning update: the user subsequently approved all four omitted functions and
requested lexer expansion first. The approved scope is now **21 functions**.
Execution is defined by the
[expanded master-plan sequence](sql-grammar-5.1.4-workplan.md#slice-d-expanded-remaining-implementation-sequence-approved-2026-11-18);
execution update: D0.1..D0.4 are complete. `SPLIT=456` was appended immediately
before `Identifier`, with nonreserved-name handling. Official Java lexer
snapshots verify both complete replacement maps, recorded in the master plan.
Generation/compilation passed; D0.5's developer manual golden repairs are now
committed in `641ded6`, with all 36 affected classes validated. D1.1 completes
the ARRAY_REPEAT matrix with 48 cases. D1.2.1 completes ARRAY_TO_STRING with 45
cases; D1.2.2 completes ARRAY_SLICE with 55 cases. D2.6 completes ARRAY_DISTINCT,
ARRAY_COMPACT and ARRAY_SIZE with 31 cases each. D3.8.1-D3.8.3 complete TO_ARRAY,
AS_ARRAY and IS_ARRAY with 35, 32 and 37 cases respectively. D3.8.4/D3.9.1-D3.9.2
complete SPLIT, SPLIT_TO_ARRAY and STRTOK_TO_ARRAY with 48, 53 and 53 cases.
The focused host passes 694/694; 102 directly dependent composition tests also
pass. Three older matrix remediations remain pending.

See the [D1 checkpoint summary](slice-d-d1-checkpoint-summary.md) for the changes,
verification and reusable tooling added since the manual token-ID repair commit.

## Result and scope

**Slice D is not ready for final sign-off.**

1. All **17 originally assigned Slice D names** now have lexer tokens, explicit
   family routing, a corresponding walker handler, and Snowflake registry entries.
   There is no further absent lexer token within that fixed list.
2. `ARRAY_REPEAT` now has its required strict matrix: 48 independent cases,
   including 45 six-output goldens and three parser-only syntax controls.
3. **Three other assigned functions have incomplete per-function matrices:**
   `ARRAY_GENERATE_RANGE`, `ARRAY_POSITION`, and `ARRAYS_ZIP`.
   `ARRAY_TO_STRING` and `ARRAY_SLICE` are complete under D1.2.1/D1.2.2, with
   exact diagnostics and their original extractor goldens retained.
   `ARRAY_DISTINCT`, `ARRAY_COMPACT` and `ARRAY_SIZE` are complete under D2.6,
   each with 29 six-output cases and two parser-only syntax controls.
   `SPLIT_TO_ARRAY` and `STRTOK_TO_ARRAY` are complete under D3.9.1/D3.9.2,
   each with 50 six-output cases and three parser-only syntax controls.
   The remaining functions'
   earlier green tests did not establish the workplan's full closure contract.
4. **Four documented Snowflake functions were omitted from the conversion /
   predicate / split target list:** `TO_ARRAY`, `AS_ARRAY`, `IS_ARRAY`, and `SPLIT`.
   All four are explicitly approved for D3.8. `TO_ARRAY`, `AS_ARRAY` and
   `IS_ARRAY`, together with `SPLIT`, now have dedicated conversion-family
   routing, Snowflake affinity and independent strict matrices.
   `SPLIT` was the only new lexer keyword needed in this D batch and
   is already present; the other three existing tokens are unchanged.
5. `ARRAY_CONSTRUCT_STRUCTURED` remains a separate, already planned constructor
   gap under Slice A / GC1. Its planned lexer addition must be settled before
   another supposedly final token-ID repair pass.

The initial source audit did not establish runtime certification. Subsequent
manual token-ID repairs are committed, and the string-to-array batch passed
694 focused-host tests plus 24 subscript and 78 table-function tests, all with
zero failures/errors/skips. This does not close the remaining per-function
gaps or the final Slice D sign-off ladder.

The bounded comparison is: the workplan's 4.9/4.10/4.11 assignments, Snowflake's
documented `ARRAY_*`/`ARRAYS_*` family, explicit array conversion/type predicates,
and string-to-array split functions. It is not a promise to inventory every
SQL function that can indirectly produce or consume an array. Object/map
families, aggregate/set/mutator/constructor slices, table functions, and
higher-order functions are not silently moved into D.

## Verification requirements

- **REQ-001:** Every one of the 21 approved names has an explicit task, lexical recognition,
  grammar/walker path, dialect policy, and independently identified test evidence.
- **REQ-002:** A green family class is not accepted as per-function matrix
  completion. Check all twelve required placements, resolution controls,
  argument substitutions, nested/derived/set-op/SCRIPT controls, six full
  extractor goldens, and exact positioned diagnostics.
- **REQ-003:** Reconcile documented signatures and parser validation policy
  explicitly; do not confuse parse coverage with SQL execution or runtime
  type/value validation.
- **REQ-004:** Expand the lexer first, preserving official before/after snapshots
  and publishing both incremental and cumulative complete token-ID worklists.
  Pause for developer manual golden repairs. Complete matrix work and the final
  accuracy sign-off ladder afterward.

## Approved 21-function ledger (all 21 explicit routes present)

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

Counts below are independent methods naming the function, not credits for
incidental nested calls. All matching SQL call sites for the older ten functions
were also searched across parser Java tests; they occur in this focused host.
"Exact lists" counts complete diagnostic-list comparisons, not individual
position/count assertions. An exact-list count of zero does **not** mean there
are no diagnostic assertions. The older methods generally have individual
diagnostic assertions and complete output goldens.

| Function | Task | Lexer ID | Route | Registry | Methods | Six goldens | Exact lists | Current source assessment |
|---|---|---:|---|---|---:|---:|---:|---|
| `ARRAY_TO_STRING` | D1.2.1 complete | 362 | T | Yes | 45 | 42 | 45 | Expanded matrix and exact legacy diagnostics; parser nonreserved-name fix; evidence below |
| `ARRAY_SLICE` | D1.2.2 complete | 369 | T | Yes | 55 | 52 | 55 | All three arguments isolated; exact legacy diagnostics; evidence below |
| `ARRAY_REPEAT` | D1.1 / GC2 complete | 455 | T | Yes | 48 | 45 | 48 | Expanded matrix plus three parser-only syntax cases; evidence below |
| `ARRAY_DISTINCT` | D2.6.1 / T009 complete | 363 | U | Yes | 31 | 29 | 31 | Independent strict matrix; original goldens preserved; evidence below |
| `ARRAY_COMPACT` | D2.6.2 / T010 complete | 364 | U | Yes | 31 | 29 | 31 | Independent strict matrix; original goldens preserved; evidence below |
| `ARRAY_SIZE` | D2.6.3 / T011 complete | 368 | U | Yes | 31 | 29 | 31 | Independent strict matrix; original goldens preserved; evidence below |
| `ARRAY_SORT` | D2.3 | 370 | U + generic optional-argument calls | Yes | 28 | 28 | 28 | Expanded placement/resolution matrix present |
| `ARRAY_MAX` | D2.3 | 371 | U | Yes | 25 | 25 | 25 | Expanded placement/resolution matrix present |
| `ARRAY_MIN` | D2.3 | 372 | U | Yes | 25 | 25 | 25 | Expanded placement/resolution matrix present |
| `ARRAY_REVERSE` | D2.4 | 452 | U | Yes | 27 | 27 | 27 | Expanded placement/resolution matrix present |
| `ARRAY_SUM` | D2.4 | 453 | U | Yes | 27 | 27 | 27 | Expanded placement/resolution matrix present |
| `SPLIT_TO_ARRAY` | D3.9.1 / T016 complete | 374 | C | Yes, retained compatibility | 53 | 50 | 53 | Optional delimiter; Redshift provenance; legacy affinity/goldens preserved; evidence below |
| `STRTOK_TO_ARRAY` | D3.9.2 / T017 complete | 375 | C | Yes | 53 | 50 | 53 | Optional delimiter-set form and independent operand controls; evidence below |
| `ARRAY_GENERATE_RANGE` | D3.5; remediation D3.9 | 366 | R | Yes | 7 | 7 | 0 | Literal-argument placements; no column-argument matrix |
| `ARRAY_POSITION` | D3.5; remediation D3.9 | 367 | P | Yes | 8 | 8 | 0 | Matrix gaps |
| `ARRAYS_ZIP` | D3.5; remediation D3.9 | 373 | Z | Yes | 8 | 8 | 0 | Matrix gaps |
| `ARRAYS_TO_OBJECT` | D3.6 | 454 | C | Yes | 40 | 38 | 40 | Expanded matrix plus two parser-only syntax cases present |
| `TO_ARRAY` | D3.8.1 / T012 complete | 376 | C | Yes | 35 | 33 | 35 | Dedicated conversion route; scalar/VARIANT/NULL controls; evidence below |
| `AS_ARRAY` | D3.8.2 / T013 complete | 377 | C | Yes | 32 | 30 | 32 | Dedicated conversion route; VARIANT/NULL controls; evidence below |
| `IS_ARRAY` | D3.8.3 / T014 complete | 378 | C | Yes | 37 | 35 | 37 | Dedicated conversion route; bare predicates and projection/CASE; evidence below |
| `SPLIT` | D3.8.4 / T015 complete | 456 | C | Yes | 48 | 45 | 48 | Dedicated route; legacy AST serialization and subscript/FLATTEN composition preserved; evidence below |

Total: **694 methods**, of which 662 assert all six extractor outputs and 32
are parser-only syntax controls. The focused run passes all 694. This is not a
claim that every signature-negative case across all 21 functions is established;
D-SIG and the remaining per-function batches remain open.

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

## Older-matrix gap ledger (three functions remain)

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
| `ARRAY_POSITION`, `ARRAYS_ZIP` | JOIN ON, HAVING, QUALIFY, DDL, SCRIPT | No independent unresolved, ambiguous, hidden-alias, variable-typing or nested/derived/set-op controls; missing-alias coverage is argument 1 only. |
| `ARRAY_GENERATE_RANGE` | JOIN ON, HAVING, QUALIFY, DDL, SCRIPT | All seven tests use `(1, 5)`. Column-resolution, variable typing, eligible-argument missing aliases, three-argument step, and deep controls are not independently covered. |

The remaining rows are actual source gaps, not just missing method-name labels.
The queries were inspected. Existing complete goldens must not be blindly refreshed while
adding coverage. Preserve the one-query-per-method and exact diagnostic contract.

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

## Signature-policy gap, separate from runtime evaluation

The shared transform grammar accepts two or three arguments for each T name.
Thus source routing alone does not enforce the distinct documented signatures
of `ARRAY_TO_STRING`, `ARRAY_SLICE`, and `ARRAY_REPEAT`. The D3 C/R/P/Z families
share a general argument list. Generic routine invocation can also accept
calls outside a dedicated family's narrow signature.

The workplan allocates AR-M20..24 to signature/arity/type-shape diagnostics.
Those obligations must be reconciled explicitly: identify which invalid
syntactic signatures the parser must reject versus calls intentionally passed
through for database validation, then test that boundary. This report does not
invent a new global arity validator or treat Snowflake runtime type/length/value
checks as parser responsibilities.

## Remaining work outside D, not surprise additions to D

The original inventory has 34 names: the 17 assigned to D plus these 17 names.
Their ownership stays unchanged:

| Owner | Names | Audit disposition |
|---|---|---|
| Slice A / 4.2 / GC1 | `ARRAY_CONSTRUCT`, `ARRAY_CONSTRUCT_COMPACT`, `ARRAY_CONSTRUCT_STRUCTURED` | Constructor parity remains outside D. `ARRAY_CONSTRUCT_STRUCTURED` has no lexer token or registry entry; its planned addition is a known further token-ID risk. |
| Entry 4.1 | `ARRAY_CONTAINS` | Existing partial VARIANT/placement work; do not claim closed by D |
| Slice B / 4.3, 4.6 | `ARRAY_AGG`, `ARRAY_UNION_AGG`, `ARRAY_UNIQUE_AGG` | Existing aggregate ownership; not re-certified here |
| Slice C / 4.7 | `ARRAY_CAT`, `ARRAY_EXCEPT`, `ARRAY_INTERSECTION`, `ARRAYS_OVERLAP` | Existing binary/set ownership; not re-certified here |
| Slice C / 4.8 | `ARRAY_APPEND`, `ARRAY_PREPEND`, `ARRAY_INSERT`, `ARRAY_REMOVE`, `ARRAY_REMOVE_AT` | Existing mutator ownership; not re-certified here |
| Slice E / 4.5 | `ARRAY_FLATTEN` | Has token 365 and generic-name support, but no registry entry or SQL-call tests found; preserve its already assigned E ownership |

`FLATTEN` / `LATERAL FLATTEN` and `FILTER` / `TRANSFORM` / `REDUCE` also remain
explicitly in Slice E. They are not additional D requirements.

The comparison accounts for the 32 `ARRAY_*`/`ARRAYS_*` names in the retrieved
Snowflake semi-structured list, `STRTOK_TO_ARRAY`, the four candidates above,
and the existing `SPLIT_TO_ARRAY` compatibility spelling: **38 distinct names**.
No other name in that bounded comparison remains unassigned in this report.

## Executable work checklist

- [x] **D0.1..D0.5 FIRST:** freeze the 21-function inventory, capture official
  current and last-repaired-baseline constants, append only `SPLIT` before
  `Identifier`, regenerate, publish incremental and cumulative worklists, and
  pause for developer manual repairs before validating the existing focused
  host. Do not duplicate `TO_ARRAY`, `AS_ARRAY`, `IS_ARRAY`, or `ARRAY_REPEAT`.
  The separate GC1 constructor addition remains an explicit future release risk.
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
- [ ] **D3.9.3..D3.9.5:** complete `ARRAY_GENERATE_RANGE`, `ARRAY_POSITION`,
  and `ARRAYS_ZIP` matrices.
- [ ] **D-SIG:** resolve and test the signature-validation boundary above across
  the final approved function list.
- [ ] **D-AUDIT:** reconcile all 21 function rows to method-level evidence,
  including the six expanded matrices already present, after the D0 manual
  repair gate and all per-function batches; do not infer coverage from green totals.
- [ ] **D4 final sign-off:** focused Slice D host, Phase15 derived-column gate,
  Smoketest quality gate, and full default parser accuracy suite, excluding
  opt-in slow/manual modes. Record actual pass/fail/skip counts and exit codes.

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
