#!/usr/bin/env bash
set -euo pipefail

API_VERSION="${1:-}"
PARSER_VERSION="${2:-}"

if [[ -z "${API_VERSION}" || -z "${PARSER_VERSION}" ]]; then
  echo "Usage: $0 <api-version> <parser-version>" >&2
  echo "Example: $0 5.1.3 5.1.3-1" >&2
  exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
SRC_DIR="${REPO_ROOT}/parse/documents"
DEST_DIR="${REPO_ROOT}/docs/vendor/pss-parse/${API_VERSION}"
MANIFEST="${DEST_DIR}/pss-parse-docs.manifest.json"
LATEST_FILE="${REPO_ROOT}/docs/vendor/pss-parse/latest.version"

# id|source-filename|role|versioned-filename(optional)
DOCS=(
  "symbol-table-bucket-reference|symbol-table-bucket-reference.md|Primary consumer guide for symbol-table buckets, tracing algorithms, and scope semantics|symbol-table-bucket-reference-pss-parse-${API_VERSION}.md"
  "table-and-query-dictionary-design|table-and-query-dictionary-design.md|Design contract for global and per-scope table_dictionary and query_dictionary collection|"
  "relational-modifier-resolution-policy|relational-modifier-resolution-policy.md|PIVOT and UNPIVOT semantics, derivation buckets, and modifier column lineage|"
  "sql-grammar-extensions-since-2026-01-01|sql-grammar-extensions-since-2026-01-01.md|Parse coverage and grammar extensions available in pss-parse 5.1.3 and later|"
  "window-query-dictionary-policy|phase-17.6.9-window-query-dictionary-policy.md|Window clause policy for window_partition_by and window_ordered_by versus query_dictionary|"
  "ordered-select-list-output-alias-policy|ordered-select-list-output-alias-policy.md|Ordered intra-select-list output alias resolution, interface queryN lineage, and contract tests|"
  "global-table-dictionary-cte-alias-policy|global-table-dictionary-cte-alias-policy.md|5.1.3 global tableDictionary contract CTE and query aliases via symbol table table_alias|"
  "set-operation-interface-duplicate-output-names-policy|set-operation-interface-duplicate-output-names-policy.md|Author guidance for UNION INTERSECT EXCEPT branches with duplicate select-list output names; DUPLICATE_INTERFACE_COLUMNS and SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH|"
)

mkdir -p "${DEST_DIR}"

{
  echo '{'
  echo '  "parserArtifact": "pss-parse",'
  echo "  \"parserVersion\": \"${PARSER_VERSION}\","
  echo "  \"parserApiVersion\": \"${API_VERSION}\","
  echo '  "documents": ['

  doc_index=0
  for entry in "${DOCS[@]}"; do
    IFS='|' read -r id filename role versioned_filename <<< "${entry}"
    src="${SRC_DIR}/${filename}"
    if [[ ! -f "${src}" ]]; then
      echo "Canonical document not found: ${src}" >&2
      exit 1
    fi

    cp "${src}" "${DEST_DIR}/${filename}"
    if [[ -n "${versioned_filename}" ]]; then
      cp "${src}" "${DEST_DIR}/${versioned_filename}"
    fi

    if [[ "${doc_index}" -gt 0 ]]; then
      echo ','
    fi
    echo '    {'
    echo "      \"id\": \"${id}\","
    echo "      \"role\": \"${role}\","
    echo "      \"path\": \"${filename}\","
    if [[ -n "${versioned_filename}" ]]; then
      echo "      \"versionedFilename\": \"${versioned_filename}\","
    fi
    echo "      \"parserVersionMin\": \"${API_VERSION}\""
    echo -n '    }'
    doc_index=$((doc_index + 1))
  done

  echo ''
  echo '  ]'
  echo '}'
} > "${MANIFEST}"

printf '%s\n' "${API_VERSION}" > "${LATEST_FILE}"

echo "Synced ${#DOCS[@]} pss-parse consumer docs to ${DEST_DIR}"
