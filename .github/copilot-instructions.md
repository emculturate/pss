# Copilot Instructions

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
