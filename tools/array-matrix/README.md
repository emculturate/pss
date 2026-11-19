# Array matrix workflow

Run from the repository root with Node.js. Uses the existing compiled parser,
JUnit/Gson dependencies and Surefire classpath; no npm install. Capture uses
`JAVA_HOME`, or the JDK recorded in the existing focused-test report.

1. Create a session-local batch directory with `batch.cjs` exporting one function
   configuration, or `{ functions: [first, second], next_task: "..." }`.
2. `node tools/array-matrix/matrix.cjs <batch-dir> capture`
3. Review new expectations in `captured.json` against the established contracts;
   investigate unexpected diagnostics/outputs rather than rereading unchanged docs.
4. `node tools/array-matrix/matrix.cjs <batch-dir> emit --reviewed`
5. Run the entire focused host **once for the batch**, using the established
   isolated Maven command. No broad suites until final Slice D gates.
6. `node tools/array-matrix/matrix.cjs <batch-dir> finish`
7. Insert the generated `accounting.md` sections into the existing reconciliation;
   update only relevant checkmarks, counts and genuine findings in the master
   workplan. Do not create another plan or hand-copy method inventories.

If a case proves a production defect, declare `production_changes: true` and
list its actual paths in `production_files`. Reports cite the verified Surefire
XML; they do not invent a Maven exit code or claim a log file was saved.

Example function configuration (illustrative; fill the complete legacy inventory):

```js
module.exports = {
  function: 'ARRAY_COMPACT',
  task: 'T010', plan: 'D2.6.2', dependencies: ['T009'],
  rule: 'array_unary_transform_function',
  legacyPrefix: 'snowflakeArrayCompact', legacyCount: 8,
  prefix: 'arC45_arrayCompact',
  signature: {
    name: 'array_compact',
    args: [{ column: 'tags', literal: "['x', NULL, 'y']" }]
  },
  legacyCoverage: {
    snowflakeArrayCompactSelectPlacementV0Test: ['placement:Select']
    // Assign cells to EVERY existing method; this example is not a full inventory.
  },
  cases: [], // Function-specific forms/edge cases, each with suffix/query/cells/codes.
  requiredCells: [], // Additional function-specific cells beyond the shared matrix.
  production_changes: false,
  next_task: '...'
};
```

`signature.args` describes a documented call form, not a claim about runtime types
or global arity enforcement. Optional-argument omissions, variadic forms and
function-specific semantics need explicit extra cases. Legacy coverage labels
must reflect inspected SQL, not merely method names. An all-arguments-ambiguous
case must not be credited as three independent argument controls.

Templates cover all twelve placements, isolated argument controls, combined
variables, nested/derived/set-op/SCRIPT reset, neutrality, dialect/case and
malformed syntax. Explicit legacy/extra-case cells suppress only corresponding
templates. Each function retains its own catalog, ledger, counts and task ID.

The emitter preserves old extractor goldens and unrelated methods, and stops if
the host changed after capture. Finish rejects stale, incomplete or failing
focused reports and missing coverage metadata; it writes accounting plus the
batch report/checkpoint, without marking repository tasks complete itself.
Capture classes are cached alongside session batches and rebuilt when their
source, classpath or JDK changes. Keep this tooling across batches; clean only
disposable batch snapshots after verification.

Tooling checks: `node --test tools/array-matrix/matrix.test.cjs`.
