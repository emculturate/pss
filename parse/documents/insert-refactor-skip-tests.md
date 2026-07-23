# Tests to skip during INSERT / VALUES refactor

Use this list when running the suite during incremental INSERT/VALUES work. Failures in these tests are **known and unrelated** to the refactor unless we are explicitly working on INSERT/VALUES semantics.

**Last verified:** full `mvn test` — **1434/1434** green (Jul 2026). No walker classes are on the skip list.

---

## Historical note — PIVOT AST (resolved Jul 2026)

`sql.walker.SqlEventWalkerPivotUnpivotTests` was previously skipped during INSERT refactor because PIVOT AST goldens were out of date. **PIVOT is green again** (67/67 as of Jul 2026). Do not exclude this class unless a new PIVOT regression is opened.

---

## Historical note — donor-email live sample (resolved in Phase 13.4)

`SqlEventWalkerLiveSampleQueriesTests#donorEmailWithInvalidFatalErrorOnQualifiedColumnVariableTest` previously failed on same-select-list forward alias resolution. **Fixed in Phase 13.4** — test is green and included in the smoketest gate.

---

## Quick Maven runs

From `parse/`:

```bash
# Full suite (expected: 0 failures)
mvn test

# Smoketest quality gate only (204/204)
mvn -Psmoketest-quality-gate test
```

**Target for INSERT/VALUES refactor:** `Failures: 0`. Any new failure requires investigation.

---

## In scope for INSERT/VALUES refactor (should stay green)

- `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` (including `insertValues*`, UPDATE RETURNING)
- `SqlEventWalkerNonSqlEndpointParserTests` (`valuesStatement*`, etc.)
- `SqlEventWalkerSubqueriesAndClauseSemanticsTests` (VALUES CTEs)
- `SqlParseEventWalkerWithAccessObjectTest` (`basicInsert*`, VALUES-from-access)
- `SqlEventWalkerPivotUnpivotTests`
- `SqlEventWalkerLiveSampleQueriesTests` (donor-email sample)
- All other walker / access / CLI test classes
