#!/usr/bin/env python3
"""Extract Panto outstanding rows from CSV into a JSON manifest for Java batch timing."""

from __future__ import annotations

import argparse
import csv
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
DEFAULT_CSV = (
    ROOT
    / "parse/docs/rmcp-handoff/5.1.3-panto-outstanding/panto_513_outstanding_issues.csv"
)
DEFAULT_OUT = ROOT / "parse/target/panto-timeout-batch-manifest.json"
DEFAULT_SKIP_LIST = (
    ROOT
    / "parse/docs/rmcp-handoff/5.1.3-panto-outstanding/panto-submap-walker-skip-list.json"
)


def load_skip_rows(skip_list_path: Path) -> set[int]:
    payload = json.loads(skip_list_path.read_text(encoding="utf-8"))
    return {int(row["csv_row"]) for row in payload.get("rows", [])}


def load_rows(
    csv_path: Path,
    issue_kind: str | None,
    *,
    exclude_skip_list: bool,
    skip_list_path: Path,
) -> list[dict]:
    with csv_path.open(newline="", encoding="utf-8") as handle:
        records = list(csv.DictReader(handle))
    if issue_kind:
        records = [r for r in records if issue_kind in r.get("issue_kinds", "")]
    skip_rows: set[int] = set()
    if exclude_skip_list:
        skip_rows = load_skip_rows(skip_list_path)
    rows = []
    for record in records:
        csv_row = int(record["csv_row"])
        if csv_row in skip_rows:
            continue
        rows.append(
            {
                "csv_row": csv_row,
                "query_key": record.get("query_key", ""),
                "query_sql": record.get("query_sql", ""),
                "parse_ms_5_0_0_3": int(record.get("parse_ms_5_0_0_3") or 0),
                "parse_ms_5_1_3": int(record.get("parse_ms_5_1_3") or 0),
                "issue_kinds": record.get("issue_kinds", ""),
            }
        )
    rows.sort(key=lambda r: r["csv_row"])
    return rows


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--csv", type=Path, default=DEFAULT_CSV)
    parser.add_argument("--out", type=Path, default=DEFAULT_OUT)
    parser.add_argument(
        "--issue",
        default="timeout_513",
        help="Filter issue_kinds substring (default: timeout_513). Use '' for all rows.",
    )
    parser.add_argument(
        "--skip-list",
        type=Path,
        default=DEFAULT_SKIP_LIST,
        help="JSON skip list (default: panto-submap-walker-skip-list.json).",
    )
    parser.add_argument(
        "--include-skip-list",
        action="store_true",
        help="Include subMap walker-skip rows in the manifest (default: exclude).",
    )
    args = parser.parse_args()

    issue = args.issue or None
    rows = load_rows(
        args.csv,
        issue,
        exclude_skip_list=not args.include_skip_list,
        skip_list_path=args.skip_list,
    )
    args.out.parent.mkdir(parents=True, exist_ok=True)
    payload = {
        "source_csv": str(args.csv),
        "issue_filter": args.issue,
        "skip_list": str(args.skip_list) if not args.include_skip_list else None,
        "row_count": len(rows),
        "rows": rows,
    }
    args.out.write_text(json.dumps(payload, ensure_ascii=False), encoding="utf-8")
    print(f"Wrote {len(rows)} rows to {args.out}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
