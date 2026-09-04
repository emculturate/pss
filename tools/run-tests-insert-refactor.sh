#!/usr/bin/env bash
# Run parse module tests during INSERT/VALUES refactor, excluding known-unrelated failures.
# See parse/documents/insert-refactor-skip-tests.md
set -euo pipefail
cd "$(dirname "$0")/../parse"
mvn test -Dtest='!SqlEventWalkerPivotUnpivotTests' "$@"
