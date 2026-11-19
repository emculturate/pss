# Copilot Instructions

## Test Suite Selection

- "Full suite", "full test suite", and "all tests" mean the default
  **result-accuracy suite**, not opt-in slow corpus regressions or manual
  latency/benchmark diagnostics.
- Use `pss-quiet` in the Testing sidebar, or Maven without `release-tests` /
  `diagnostic-tests`, for that default suite.
- Enable `pss-release` / `-Prelease-tests` only for an explicit slow/release
  regression request. Enable `pss-diagnostics` / `-Pdiagnostic-tests` only for
  explicitly requested manual probes.
- If there is any uncertainty, ask the user which set they mean before running
  tests. Give delegated agents the same instruction.
- Report the selected set and skipped tests; accuracy-only success is not a
  claim that slow regressions passed or the JAR is release-ready.

See [test suite selection](../README.md#test-suite-selection).

## Efficient Slice D Matrix Completion

- Use the existing master workplan and reconciliation as the gap inventory.
  Reuse parser contracts already read for the unchanged baseline; reopen docs
  only for changed contracts, new semantics or a concrete failure.
- Use [the reusable matrix tooling](../tools/array-matrix/README.md), not a new
  capture program per function. Describe the signature and actual legacy
  coverage; generate missing argument-isolation/placement cases from templates.
- Up to three related functions may share an approved batch, one capture and one full
  focused-host run. Preserve independent matrices, task status and evidence;
  do not expand beyond the user's requested scope or bypass task prerequisites.
- Review new goldens and unexpected results. Keep full extractor goldens,
  exact diagnostics and one query per method. Never refresh existing goldens.
- Generate method ledgers and required reports from the case catalog. Update
  only relevant checkmarks, counts and genuine findings in existing documents;
  do not write repeated completion essays or competing plans.
- Investigate beyond the known gaps only when a new case exposes a defect.
  Defer broad gates until final Slice D sign-off. Report total elapsed time
  when asked about duration, not merely test-command runtime.

## Lexer Additions - Automatic Token-ID Worklist

Whenever a task adds lexer items, automatically follow the
[lexer token-ID policy](../.cursor/rules/lexer-token-id-golden-policy.mdc).
Do not wait for the user to request a token report:

1. Before editing, generate and snapshot
   `parse/target/generated-sources/antlr4/sql/SQLSelectParserLexer.java`.
2. Append new lexer keywords immediately before `Identifier`, preserving existing
   token order. Count the additions as the expected ID increment.
3. Regenerate and snapshot the after image, then compare the generated Java token
   constants with `tools/token_id_worklist_from_vocab.py <before.java> <after.java>`.
4. Include the **entire** worklist in the final response/summary: token name,
   before ID, after ID, and delta, ordered highest ID to lowest. Include every
   shifted token, regardless of whether tests expose it, plus the added token
   names and expected increment.
5. Leave existing golden token-ID replacements to the developer's careful manual
   IDE edits. Do not automatically refresh them.

This workflow takes precedence over the test-output report below for lexer
additions and migration mappings. Test failure output is not a complete token
inventory.

## Token ID Difference Report Workflow

When the user asks to report token id differences (for example: "report token id differences", "check token id mismatches", or similar phrasing), do the following automatically:

1. Run the wrapper command from the repository root:
   - `tools/token_id_diff_latest.sh --show-total`
2. Return the command output table directly to the user.
3. If no differences are found, explicitly state that no token ID mismatches were found.
4. If the wrapper cannot find an artifact, run tests first and then report:
   - `runTests` for the workspace
   - Then rerun `tools/token_id_diff_latest.sh --show-total`
5. Do not include analysis of non-token failures unless the user explicitly asks for it.

## Generic Path Assertion Generation Workflow

When the user asks to add or refresh path-based golden assertions:

1. Prefer generator tooling over manual copy/paste assertion authoring.
2. Place SQL text in a temporary file and run from repository root:
   - `python3 tools/generate_symbol_path_assertions.py --sql-file /tmp/query.sql`
3. Use `--root` and `--path-regex` to target the exact map and subpaths needed.
4. Use emitted `expected...` values plus `assertSymbolTreePathEquals(...)` lines as the source of truth.
5. Keep assertions generic and user-directed; do not hardcode special-case test-family assumptions unless explicitly requested.
