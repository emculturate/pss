# Slice D array-function reconciliation

Date: 2026-11-18. Source-audit baseline: commit `5604d3c`.
Planning update: the user subsequently approved all four omitted functions and
requested lexer expansion first. The approved scope is now **21 functions**.
Execution is defined by the
[expanded master-plan sequence](sql-grammar-5.1.4-workplan.md#slice-d-expanded-remaining-implementation-sequence-approved-2026-11-18);
execution update: D0.1..D0.4 are complete. `SPLIT=456` was appended immediately
before `Identifier`, with nonreserved-name handling. Official Java lexer
snapshots verify both complete replacement maps, recorded in the master plan.
Generation/compilation passed; D0.5 remains blocked on developer manual golden
repairs. Dedicated function implementations and matrices have not advanced.

## Result and scope

**Slice D is not ready for final sign-off.**

1. All **17 originally assigned Slice D names** now have lexer tokens, explicit
   family routing, a corresponding walker handler, and Snowflake registry entries.
   There is no further absent lexer token within that fixed list.
2. `ARRAY_REPEAT` has only a routing seed, not its required strict matrix.
3. **Ten other assigned functions have incomplete per-function matrices.** Their
   earlier green tests did not establish the workplan's full closure contract.
4. **Four documented Snowflake functions were omitted from the conversion /
   predicate / split target list:** `TO_ARRAY`, `AS_ARRAY`, `IS_ARRAY`, and `SPLIT`.
   All four are now explicitly approved for D3.8; implementation and strict
   matrices remain pending. `SPLIT` was the only new lexer keyword needed in this
   D batch and is now present; the other three existing tokens are unchanged.
5. `ARRAY_CONSTRUCT_STRUCTURED` remains a separate, already planned constructor
   gap under Slice A / GC1. Its planned lexer addition must be settled before
   another supposedly final token-ID repair pass.

This is a source reconciliation, not a new successful test run. The latest full
focused result, 256/256, predates `ARRAY_REPEAT`'s lexer addition. Current existing
goldens still need the documented token-ID repairs.

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

## Approved 21-function ledger (17 explicit routes plus four pending additions)

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
| `ARRAY_TO_STRING` | D1; remediation D1.2 | 362 | T | Yes | 15 | 15 | 0 | Matrix gaps |
| `ARRAY_SLICE` | D1; remediation D1.2 | 369 | T | Yes | 12 | 12 | 0 | Matrix gaps |
| `ARRAY_REPEAT` | D1.1 / GC2 | 455 | T | Yes | 1 | 0 | 0 | Routing seed only |
| `ARRAY_DISTINCT` | D2.1; remediation D2.6 | 363 | U | Yes | 2 | 2 | 0 | SELECT + missing-alias seed only |
| `ARRAY_COMPACT` | D2.2; remediation D2.6 | 364 | U | Yes | 8 | 8 | 0 | Matrix gaps |
| `ARRAY_SIZE` | D2.2; remediation D2.6 | 368 | U | Yes | 8 | 8 | 0 | Matrix gaps |
| `ARRAY_SORT` | D2.3 | 370 | U + generic optional-argument calls | Yes | 28 | 28 | 28 | Expanded placement/resolution matrix present |
| `ARRAY_MAX` | D2.3 | 371 | U | Yes | 25 | 25 | 25 | Expanded placement/resolution matrix present |
| `ARRAY_MIN` | D2.3 | 372 | U | Yes | 25 | 25 | 25 | Expanded placement/resolution matrix present |
| `ARRAY_REVERSE` | D2.4 | 452 | U | Yes | 27 | 27 | 27 | Expanded placement/resolution matrix present |
| `ARRAY_SUM` | D2.4 | 453 | U | Yes | 27 | 27 | 27 | Expanded placement/resolution matrix present |
| `SPLIT_TO_ARRAY` | D3.5; remediation D3.9 | 374 | C | Yes | 8 | 8 | 0 | Matrix gaps; Snowflake attribution also needs resolution |
| `STRTOK_TO_ARRAY` | D3.5; remediation D3.9 | 375 | C | Yes | 8 | 8 | 0 | Matrix gaps |
| `ARRAY_GENERATE_RANGE` | D3.5; remediation D3.9 | 366 | R | Yes | 7 | 7 | 0 | Literal-argument placements; no column-argument matrix |
| `ARRAY_POSITION` | D3.5; remediation D3.9 | 367 | P | Yes | 8 | 8 | 0 | Matrix gaps |
| `ARRAYS_ZIP` | D3.5; remediation D3.9 | 373 | Z | Yes | 8 | 8 | 0 | Matrix gaps |
| `ARRAYS_TO_OBJECT` | D3.6 | 454 | C | Yes | 40 | 38 | 40 | Expanded matrix plus two parser-only syntax cases present |
| `TO_ARRAY` | D3.8.1 | 376 | Generic; explicit family pending | No | 0 | 0 | 0 | Approved implementation/matrix pending |
| `AS_ARRAY` | D3.8.2 | 377 | Generic; explicit family pending | No | 0 | 0 | 0 | Approved implementation/matrix pending |
| `IS_ARRAY` | D3.8.3 | 378 | Generic; explicit family pending | No | 0 | 0 | 0 | Approved implementation/matrix pending |
| `SPLIT` | D0 complete through D0.4; D3.8.4 pending | 456 | Generic; explicit family pending | No | 0 | 0 | 0 | Dedicated D matrix pending; composition coverage exists elsewhere |

Total: **257 methods**, of which 254 assert all six extractor outputs, two are
parser-only syntax controls, and one is the unfinished `ARRAY_REPEAT` routing
seed. Source matrix presence is not current runtime certification or a claim
that every signature-negative case has been established.

## Concrete gaps in the ten older matrices

The required placement list is SELECT, WHERE, JOIN ON, HAVING, QUALIFY, ORDER BY,
VALUES, INSERT, UPDATE, DELETE, DDL, SCRIPT. A JOIN elsewhere in a query does not
count as placing the function in its ON condition.

| Function(s) | Missing independent placements | Other verified omissions |
|---|---|---|
| `ARRAY_TO_STRING` | JOIN ON, HAVING | No independent unqualified-success, unresolved-no-FROM, hidden-alias, nested/derived/set-op controls. Predicand substitution is tested only for argument 2. Existing SCRIPT has Snowflake then ANSI, not a third Snowflake reset control. |
| `ARRAY_SLICE` | WHERE, JOIN ON, QUALIFY, DDL, SCRIPT | No independent unqualified-success, unresolved-no-FROM, hidden-alias, predicand-variable, nested/derived/set-op controls. Existing ambiguous query makes all three arguments ambiguous together, rather than isolating each eligible position. |
| `ARRAY_DISTINCT` | Every required placement except SELECT | Only the SELECT and missing-alias seed methods exist; resolution, substitution and deep controls remain incomplete. |
| `ARRAY_COMPACT`, `ARRAY_SIZE` | JOIN ON, HAVING, QUALIFY, DDL, SCRIPT | No independent unresolved, ambiguous, hidden-alias, variable-typing, nested/derived/set-op controls. |
| `SPLIT_TO_ARRAY`, `STRTOK_TO_ARRAY`, `ARRAY_POSITION`, `ARRAYS_ZIP` | JOIN ON, HAVING, QUALIFY, DDL, SCRIPT | No independent unresolved, ambiguous, hidden-alias, variable-typing or nested/derived/set-op controls; missing-alias coverage is argument 1 only. |
| `ARRAY_GENERATE_RANGE` | JOIN ON, HAVING, QUALIFY, DDL, SCRIPT | All seven tests use `(1, 5)`. Column-resolution, variable typing, eligible-argument missing aliases, three-argument step, and deep controls are not independently covered. |

These are actual source gaps, not just missing method-name labels. The queries
were inspected. Existing complete goldens must not be blindly refreshed while
adding coverage. Preserve the one-query-per-method and exact diagnostic contract.

## Four formerly omitted names now approved for D3.8

| Function | Documented role/signature | Current code recognition | Missing Slice D evidence |
|---|---|---|---|
| `TO_ARRAY(expr)` | Convert an expression to ARRAY | Token 376; nonreserved name; generic invocation path | No dedicated D family membership, Snowflake registry entry, or SQL-call tests found in parser Java tests |
| `AS_ARRAY(variant_expr)` | Cast a VARIANT to ARRAY | Token 377; nonreserved name; generic invocation path | Same omissions |
| `IS_ARRAY(variant_expr)` | ARRAY type predicate | Token 378; nonreserved name; generic invocation path | Same omissions |
| `SPLIT(string, separator)` | Split a string into ARRAY | Token 456; nonreserved name; generic invocation path | Existing subscript and FLATTEN composition tests, but no dedicated D split matrix or registry entry |

These names are **not necessarily unparseable**. Generic routine support is
different from explicit family handling, dialect policy, and strict completion
evidence. The three existing tokens do not need another lexer addition.

Snowflake documents `SPLIT`, not the inventory's `SPLIT_TO_ARRAY` spelling:
the official `split_to_array` documentation URL returned HTTP 404, and that name
is absent from the retrieved semi-structured function list. This does not prove
every dialect/version lacks that spelling. Preserve existing compatibility
until its provenance and dialect policy are resolved; do not silently rename
the parser function or delete tests.

Decision: D3.8 will explicitly cover all four names, in separate bounded
per-function steps. Preserve the existing `SPLIT_TO_ARRAY` behavior/diagnostics
for compatibility; do not treat it as a replacement for documented `SPLIT`.
The D0 lexer step added `SPLIT` before `Identifier` while reusing the three
existing conversion/predicate tokens. Its manual golden-repair gate remains open.

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

- [ ] **D0.1..D0.5 FIRST:** freeze the 21-function inventory, capture official
  current and last-repaired-baseline constants, append only `SPLIT` before
  `Identifier`, regenerate, publish incremental and cumulative worklists, and
  pause for developer manual repairs before validating the existing focused
  host. Do not duplicate `TO_ARRAY`, `AS_ARRAY`, `IS_ARRAY`, or `ARRAY_REPEAT`.
  The separate GC1 constructor addition remains an explicit future release risk.
  D0.1..D0.4 are complete: `SPLIT=456`, `Identifier=457`; generation/compilation
  and both official token maps are verified. D0.5 awaits manual repairs.
- [ ] **D1.1 / GC2:** finish `ARRAY_REPEAT`'s strict matrix; preserve existing
  goldens except developer-directed manual token-ID edits.
- [ ] **D1.2.1..D1.2.2:** independently complete `ARRAY_TO_STRING` and `ARRAY_SLICE`.
- [ ] **D2.6.1..D2.6.3:** complete independent matrices for `ARRAY_DISTINCT`,
  `ARRAY_COMPACT`, and `ARRAY_SIZE`.
- [ ] **D3.8.1..D3.8.4, approved:** implement/verify `TO_ARRAY`, `AS_ARRAY`,
  `IS_ARRAY`, and `SPLIT`, one function per step under the strict full matrix.
- [ ] **D3.9.1..D3.9.5:** complete the older `SPLIT_TO_ARRAY`, `STRTOK_TO_ARRAY`,
  `ARRAY_GENERATE_RANGE`, `ARRAY_POSITION`, and `ARRAYS_ZIP` matrices.
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
- [TO_ARRAY](https://docs.snowflake.com/en/sql-reference/functions/to_array)
- [AS_ARRAY](https://docs.snowflake.com/en/sql-reference/functions/as_array)
- [IS_ARRAY](https://docs.snowflake.com/en/sql-reference/functions/is_array)
- [SPLIT](https://docs.snowflake.com/en/sql-reference/functions/split)
- [ARRAY_REPEAT](https://docs.snowflake.com/en/sql-reference/functions/array_repeat)
