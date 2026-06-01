#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PY_TOOL="$SCRIPT_DIR/token_id_diff_table.py"

if [[ ! -f "$PY_TOOL" ]]; then
  echo "error: missing parser tool at $PY_TOOL" >&2
  exit 2
fi

find_latest_result() {
  local search_root="$1"
  local latest_file=""
  local latest_mtime=0

  while IFS= read -r -d '' candidate; do
    local mtime
    mtime="$(stat -f %m "$candidate" 2>/dev/null || echo 0)"
    if [[ "$mtime" -gt "$latest_mtime" ]]; then
      latest_mtime="$mtime"
      latest_file="$candidate"
    fi
  done < <(find "$search_root" -type f -path "*/GitHub.copilot-chat/chat-session-resources/*/*/content.txt" -print0 2>/dev/null)

  printf '%s' "$latest_file"
}

if [[ $# -gt 0 && -f "$1" ]]; then
  input_file="$1"
  shift
  exec python3 "$PY_TOOL" "$input_file" "$@"
fi

workspace_storage="${COPILOT_WORKSPACE_STORAGE:-$HOME/Library/Application Support/Code/User/workspaceStorage}"

if [[ ! -d "$workspace_storage" ]]; then
  echo "error: workspace storage directory not found: $workspace_storage" >&2
  exit 2
fi

latest="$(find_latest_result "$workspace_storage")"

if [[ -z "$latest" ]]; then
  echo "error: no Copilot runTests result files found under $workspace_storage" >&2
  exit 1
fi

echo "Using latest runTests result: $latest" >&2
exec python3 "$PY_TOOL" "$latest" "$@"
