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
