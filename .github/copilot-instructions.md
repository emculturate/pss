# Copilot Instructions

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
