# Slice D D1 completion and reusable matrix tooling checkpoint

Date: 2026-11-18

Baseline: `641ded6` (`Checkpoint manual lexer token-ID golden repairs`).
This document summarizes the subsequent changes; the
[master workplan](sql-grammar-5.1.4-workplan.md) remains authoritative.

## Completed parser coverage

| Task | Function | Independent cases | Six-output goldens | Parser-only syntax controls |
|---|---|---:|---:|---:|
| T006 / D1.1 | ARRAY_REPEAT | 48 | 45 | 3 |
| T007 / D1.2.1 | ARRAY_TO_STRING | 45 | 42 | 3 |
| T008 / D1.2.2 | ARRAY_SLICE | 55 | 52 | 3 |
| Total | | 148 | 139 | 9 |

The [focused test host](../src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java)
grew by 120 net test methods, from 257 to 377. It now contains 366 six-output
cases and 11 parser-only syntax controls across all functions in that class.

- Replaced the ARRAY_REPEAT route-only seed with its strict independent matrix.
- Added 30 ARRAY_TO_STRING cases and exact diagnostic lists to its 15 existing
  cases, retaining their original extractor goldens.
- Added 43 ARRAY_SLICE cases and exact diagnostic lists to its 12 existing
  cases, retaining their original extractor goldens.
- Covered placements, individual argument resolution/substitution controls,
  nested and derived expressions, set-operation sibling isolation, SCRIPT reset,
  qualified-UDF and same-named-column neutrality, dialect behavior and syntax
  rejection. ARRAY_SLICE isolates all three argument positions.
- Valid cases assert full AST, interface, substitutions, table dictionary, query
  dictionary and symbol-table strings, plus complete positioned diagnostic lists.
  Malformed cases assert parser diagnostics without walking rejected trees.
- Preserved unrelated test methods and their golden values.

The [reconciliation](slice-d-array-function-reconciliation.md) records the
per-function named-method evidence and current counts.

## Production fix and token stability

Qualified-UDF coverage exposed a parser defect: ARRAY_TO_STRING was missing from
`nonreserved_keywords`. Added that single alternative in the
[grammar](../src/main/antlr4/sql/SQLSelectParser.g4), allowing qualified calls and
same-named columns to remain neutral.

No lexer declarations or walker implementation changed. The generated Java
lexer was verified unchanged after the parser fix. This checkpoint introduces
no token-ID migration and does not repeat the manual repairs in `641ded6`.

## Reusable workflow improvements

Added [repository-owned matrix tooling](../../tools/array-matrix/README.md):

- Signature-derived templates generate only cells missing from explicit legacy
  and supplemental coverage.
- One function or an approved related pair shares capture setup and a focused
  run, while retaining independent function/task ownership and evidence.
- Capture handles SQL, DDL, SCRIPT, exact diagnostics and parser-only controls;
  reusable Java capture classes are cached between batches.
- Emission requires explicit review, preserves existing extractor goldens and
  unrelated methods, and rejects a changed host.
- Accounting and checkpoint/report generation use the case catalog and verified
  Surefire evidence, rejecting stale, incomplete or failing focused results.
- Reports require actual production-file paths when a production fix is declared.

Updated [persistent agent instructions](../../.github/copilot-instructions.md)
and the master workplan to reuse unchanged contracts and the known gap inventory,
avoid repeated discovery, and run broad gates only at final Slice D sign-off.

## Verification

- Focused parser host: **377 passed; 0 failures, errors or skips**.
- Tooling regression tests: **3 passed; 0 failures**. These exercise argument
  isolation, covered-cell filtering, separate ownership/accounting, golden
  preservation, explicit review and stale/failed-result rejection.
- Compiled capture-helper smoke check: **58 cases captured and checked** across
  ARRAY_COMPACT and ARRAY_SIZE templates, without emitting tests or modifying the
  real test host. This validates tooling, not completion of those matrices.
- Documentation vendor sync completed; whitespace validation passed.

Commands used for focused regression validation:

```sh
mvn -f parse/pom.xml -Pisolated-build \
  -Dtest=SqlEventWalkerSnowflakeArrayTransformTests \
  -Dpss.walker.test.verbose=false \
  -Dpss.test.slow=false -Dpss.test.diagnostics=false test
node --test tools/array-matrix/matrix.test.cjs
git -c core.whitespace=cr-at-eol diff --check
```

The parser run used JDK 21 with the project's configured Java release target.

## Remaining release work

D1 is complete, not all of Slice D. Next is T009 / D2.6.1 ARRAY_DISTINCT.
Eight older matrices and four D3.8 implementations remain, followed by signature
policy reconciliation, audit, final validation and accounting (T021-T024).
This checkpoint does not certify SQL runtime behavior, close the broad release
gates, or publish the 5.1.4 JAR.
