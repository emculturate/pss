#!/usr/bin/env python3
"""Build a token-ID mismatch table from VS Code runTests output.

This script scans <expectedOutput> / <actualOutput> pairs and counts token ID
shifts of the form <NNN> seen in expected versus actual values.

Usage:
    python3 tools/token_id_diff_table.py /path/to/content.txt
    python3 tools/token_id_diff_table.py /path/to/content.txt --format markdown
"""

from __future__ import annotations

import argparse
import re
import sys
from collections import defaultdict
from pathlib import Path


EXPECTED_BLOCK_RE = re.compile(
    r"<expectedOutput>\s*(.*?)\s*</expectedOutput>", re.DOTALL
)
ACTUAL_BLOCK_RE = re.compile(r"<actualOutput>\s*(.*?)\s*</actualOutput>", re.DOTALL)
TOKEN_ID_RE = re.compile(r"<(\d+)>")
TOKEN_ENTRY_RE = re.compile(
    r"\[@\d+,(\d+:\d+)='([^']*)',<(\d+)>,(\d+:\d+)\]"
)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description=(
            "Collect token ID mismatches from runTests output and print a table "
            "with Current, Should be, and Count columns."
        )
    )
    parser.add_argument(
        "input_file",
        help="Path to the runTests output file (typically named content.txt)",
    )
    parser.add_argument(
        "--format",
        choices=("markdown", "plain"),
        default="markdown",
        help="Output table format (default: markdown)",
    )
    parser.add_argument(
        "--show-total",
        action="store_true",
        help="Show a final total row",
    )
    return parser.parse_args()


def _count_by_token_identity(
    expected_text: str,
    actual_text: str,
) -> tuple[dict[tuple[str, str], int], int]:
    """Count ID shifts by matching token entries on stable identity.

    Identity key uses token source span, text, and token location. This avoids
    false positives when entire structures differ and token lists become offset.
    """
    expected_entries = TOKEN_ENTRY_RE.findall(expected_text)
    actual_entries = TOKEN_ENTRY_RE.findall(actual_text)

    if not expected_entries or not actual_entries:
        return {}, 0

    expected_by_key: dict[tuple[str, str, str], list[str]] = defaultdict(list)
    actual_by_key: dict[tuple[str, str, str], list[str]] = defaultdict(list)

    for span, text, token_id, location in expected_entries:
        expected_by_key[(span, text, location)].append(token_id)
    for span, text, token_id, location in actual_entries:
        actual_by_key[(span, text, location)].append(token_id)

    mismatches: dict[tuple[str, str], int] = defaultdict(int)
    total = 0

    for key, expected_ids in expected_by_key.items():
        actual_ids = actual_by_key.get(key)
        if not actual_ids:
            continue
        for expected_id, actual_id in zip(expected_ids, actual_ids):
            if expected_id == actual_id:
                continue
            mismatches[(expected_id, actual_id)] += 1
            total += 1

    return mismatches, total


def _count_by_fallback_zip(
    expected_text: str,
    actual_text: str,
) -> tuple[dict[tuple[str, str], int], int]:
    """Fallback for blocks without full token-entry records."""
    expected_tokens = TOKEN_ID_RE.findall(expected_text)
    actual_tokens = TOKEN_ID_RE.findall(actual_text)

    # If token list lengths differ, position-based zip comparison is unreliable.
    # In that case, skip fallback counting to avoid misaligned false positives.
    if len(expected_tokens) != len(actual_tokens):
        return {}, 0

    mismatches: dict[tuple[str, str], int] = defaultdict(int)
    total = 0
    for current_id, should_be_id in zip(expected_tokens, actual_tokens):
        if current_id == should_be_id:
            continue
        mismatches[(current_id, should_be_id)] += 1
        total += 1

    return mismatches, total


def extract_mismatch_counts(content: str) -> tuple[dict[tuple[str, str], int], int, int]:
    expected_blocks = EXPECTED_BLOCK_RE.findall(content)
    actual_blocks = ACTUAL_BLOCK_RE.findall(content)

    pair_count = min(len(expected_blocks), len(actual_blocks))
    mismatch_counts: dict[tuple[str, str], int] = defaultdict(int)
    mismatch_instances = 0

    for expected_text, actual_text in zip(expected_blocks, actual_blocks):
        if expected_text.strip() == actual_text.strip():
            continue

        by_identity, by_identity_total = _count_by_token_identity(expected_text, actual_text)
        if by_identity_total > 0:
            for pair, count in by_identity.items():
                mismatch_counts[pair] += count
            mismatch_instances += by_identity_total
            continue

        by_zip, by_zip_total = _count_by_fallback_zip(expected_text, actual_text)
        for pair, count in by_zip.items():
            mismatch_counts[pair] += count
        mismatch_instances += by_zip_total

    return mismatch_counts, mismatch_instances, pair_count


def render_markdown_table(
    rows: list[tuple[str, str, int]],
    total: int,
    show_total: bool,
) -> str:
    lines = [
        "| Current | Should be | Count |",
        "|---|---|---:|",
    ]
    for current_id, should_be_id, count in rows:
        lines.append(f"| <{current_id}> | <{should_be_id}> | {count} |")
    if show_total:
        lines.append(f"| Total |  | {total} |")
    return "\n".join(lines)


def render_plain_table(
    rows: list[tuple[str, str, int]],
    total: int,
    show_total: bool,
) -> str:
    header = f"{'Current':>10}  {'Should be':>10}  {'Count':>8}"
    sep = "-" * len(header)
    lines = [header, sep]
    for current_id, should_be_id, count in rows:
        lines.append(f"<{current_id}>     <{should_be_id}>     {count:>8}")
    if show_total:
        lines.append(sep)
        lines.append(f"{'Total':>10}  {'':>10}  {total:>8}")
    return "\n".join(lines)


def main() -> int:
    args = parse_args()
    input_path = Path(args.input_file)

    if not input_path.exists():
        print(f"error: input file does not exist: {input_path}", file=sys.stderr)
        return 2

    content = input_path.read_text(encoding="utf-8", errors="replace")
    mismatch_counts, mismatch_instances, pair_count = extract_mismatch_counts(content)

    if not mismatch_counts:
        print("No token ID mismatches found.")
        print(f"Scanned {pair_count} expected/actual output pairs.")
        return 0

    rows = [
        (current_id, should_be_id, count)
        for (current_id, should_be_id), count in mismatch_counts.items()
    ]
    rows.sort(key=lambda row: (-row[2], int(row[0]), int(row[1])))

    if args.format == "markdown":
        table = render_markdown_table(rows, mismatch_instances, args.show_total)
    else:
        table = render_plain_table(rows, mismatch_instances, args.show_total)

    print(table)
    print(f"\nScanned {pair_count} expected/actual output pairs.")
    print(f"Total mismatch instances: {mismatch_instances}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
