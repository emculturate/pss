#!/usr/bin/env python3
"""Build a complete lexer token-ID migration worklist from ANTLR outputs.

This compares old/new snapshots from either:
- SQLSelectParserLexer.tokens, or
- generated SQLSelectParserLexer.java constant block

and emits all shifted token IDs with symbolic token names, sorted by highest
new ID first.
"""

from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

TOKEN_LINE_RE = re.compile(r"^([A-Za-z_][A-Za-z0-9_]*)=(\d+)$")
JAVA_CONST_RE = re.compile(r"\b([A-Za-z_][A-Za-z0-9_]*)=(\d+)\b")


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description=(
            "Compare old/new SQLSelectParserLexer.tokens files and print a "
            "comprehensive token-ID shift worklist for manual golden updates."
        )
    )
    parser.add_argument(
        "old_tokens",
        help="Path to old SQLSelectParserLexer.tokens or SQLSelectParserLexer.java",
    )
    parser.add_argument(
        "new_tokens",
        help="Path to new SQLSelectParserLexer.tokens or SQLSelectParserLexer.java",
    )
    parser.add_argument(
        "--format",
        choices=("markdown", "plain"),
        default="markdown",
        help="Output format (default: markdown)",
    )
    return parser.parse_args()


def parse_tokens_file(path: Path) -> dict[str, int]:
    mapping: dict[str, int] = {}
    for line in path.read_text(encoding="utf-8", errors="replace").splitlines():
        match = TOKEN_LINE_RE.match(line.strip())
        if not match:
            continue
        token_name, token_id = match.groups()
        mapping[token_name] = int(token_id)
    return mapping


def parse_lexer_java_file(path: Path) -> dict[str, int]:
    text = path.read_text(encoding="utf-8", errors="replace")
    marker = "public static final int"
    start = text.find(marker)
    if start == -1:
        raise ValueError(f"Could not find lexer constant block in: {path}")
    end = text.find(";", start)
    if end == -1:
        raise ValueError(f"Could not find end of lexer constant block in: {path}")

    const_block = text[start:end]
    mapping: dict[str, int] = {}
    for token_name, token_id in JAVA_CONST_RE.findall(const_block):
        mapping[token_name] = int(token_id)
    return mapping


def parse_mapping(path: Path) -> dict[str, int]:
    if path.suffix == ".tokens":
        return parse_tokens_file(path)
    if path.suffix == ".java":
        return parse_lexer_java_file(path)
    raise ValueError(
        f"Unsupported file type for {path}. Use .tokens or generated lexer .java."
    )


def render_markdown(rows: list[tuple[str, int, int, int]]) -> str:
    lines = [
        "| Token | Before | After | Delta |",
        "|---|---:|---:|---:|",
    ]
    for token_name, before_id, after_id, delta in rows:
        lines.append(f"| {token_name} | {before_id} | {after_id} | {delta:+d} |")
    return "\n".join(lines)


def render_plain(rows: list[tuple[str, int, int, int]]) -> str:
    header = f"{'Token':<40} {'Before':>8} {'After':>8} {'Delta':>8}"
    sep = "-" * len(header)
    lines = [header, sep]
    for token_name, before_id, after_id, delta in rows:
        lines.append(f"{token_name:<40} {before_id:>8} {after_id:>8} {delta:>+8}")
    return "\n".join(lines)


def main() -> int:
    args = parse_args()
    old_path = Path(args.old_tokens)
    new_path = Path(args.new_tokens)

    if not old_path.exists():
        print(f"error: old tokens file not found: {old_path}", file=sys.stderr)
        return 2
    if not new_path.exists():
        print(f"error: new tokens file not found: {new_path}", file=sys.stderr)
        return 2

    try:
        old_tokens = parse_mapping(old_path)
        new_tokens = parse_mapping(new_path)
    except ValueError as exc:
        print(f"error: {exc}", file=sys.stderr)
        return 2

    added_tokens = sorted(
        ((name, new_tokens[name]) for name in new_tokens.keys() - old_tokens.keys()),
        key=lambda item: item[1],
    )
    shifted_rows: list[tuple[str, int, int, int]] = []
    for token_name in new_tokens.keys() & old_tokens.keys():
        before_id = old_tokens[token_name]
        after_id = new_tokens[token_name]
        if before_id == after_id:
            continue
        shifted_rows.append((token_name, before_id, after_id, after_id - before_id))

    if not shifted_rows:
        print("No token ID shifts found between the provided vocab files.")
        if added_tokens:
            print(f"Added tokens (no renumbered existing tokens): {len(added_tokens)}")
        return 0

    shifted_rows.sort(key=lambda row: (-row[2], -row[1], row[0]))
    deltas = {row[3] for row in shifted_rows}

    print(f"Shifted tokens: {len(shifted_rows)}")
    print(f"Added tokens: {len(added_tokens)}")
    if len(deltas) == 1:
        delta = next(iter(deltas))
        print(f"Uniform shift increment: {delta:+d}")
    else:
        print(
            "Non-uniform shifts detected; review all rows (lexer edits may not be append-only)."
        )

    if added_tokens:
        print("\nAdded token names (new grammar):")
        for name, token_id in added_tokens:
            print(f"- {name} ({token_id})")

    print("\nToken-ID replacement worklist (sorted by highest new ID first):")
    if args.format == "markdown":
        print(render_markdown(shifted_rows))
    else:
        print(render_plain(shifted_rows))

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
