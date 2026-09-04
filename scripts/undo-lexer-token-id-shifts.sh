#!/usr/bin/env bash
# Revert accidental interspersed-lexer token ID shifts in golden strings.
exec "$(cd "$(dirname "$0")" && pwd)/undo-lexer-token-id-shifts.py" "$@"
