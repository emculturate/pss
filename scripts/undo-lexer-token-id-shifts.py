#!/usr/bin/env python3
"""Revert accidental interspersed-lexer token ID shifts in golden strings.

Only replaces bracketed token IDs (e.g. <385>) so bare numbers elsewhere are untouched.
Apply highest source ID first to avoid double-replacement.

The 287/291/295 chain is handled with context for '(' so '*' token ids are preserved.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path

# Global find -> replace (highest source ID first)
GLOBAL_REPLACEMENTS: list[tuple[str, str]] = [
    ("<389>", "<385>"),
    ("<388>", "<384>"),
    ("<385>", "<381>"),
    ("<295>", "<291>"),
    ("<292>", "<288>"),
    ("<304>", "<300>"),
    ("<331>", "<327>"),
    ("<132>", "<128>"),
    ("<92>", "<88>"),
    ("<81>", "<77>"),
]

# Undo <287> -> <291> for LEFT_PAREN only (do not touch MULTIPLY at <291>)
PAREN_UNDO_PATTERN = re.compile(r"(='\(',)<291>(,)")

EXTENSIONS = {".java", ".json", ".xml"}


def undo_token_shifts(text: str) -> str:
    updated = text
    for find, replace in GLOBAL_REPLACEMENTS:
        updated = updated.replace(find, replace)
    updated = PAREN_UNDO_PATTERN.sub(r"\g<1><287>\g<2>", updated)
    return updated


def main() -> int:
    root = Path(__file__).resolve().parent.parent
    target = Path(sys.argv[1]) if len(sys.argv) > 1 else root / "parse" / "src" / "test"

    if not target.is_dir():
        print(f"Target directory not found: {target}", file=sys.stderr)
        return 1

    print(f"Undoing lexer token ID shifts under: {target}")
    changed_files = 0

    for path in sorted(target.rglob("*")):
        if not path.is_file() or path.suffix not in EXTENSIONS:
            continue

        text = path.read_text(encoding="utf-8")
        updated = undo_token_shifts(text)

        if updated != text:
            path.write_text(updated, encoding="utf-8")
            changed_files += 1

    print("Global replacements:")
    for find, replace in GLOBAL_REPLACEMENTS:
        print(f"  {find} -> {replace}")
    print("Context replacement:")
    print("  ='(',<291>, -> ='(',<287>,")
    print(f"Done. Updated {changed_files} file(s).")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
