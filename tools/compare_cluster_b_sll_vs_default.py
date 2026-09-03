#!/usr/bin/env python3
"""Compare SLL vs default prediction fatals for Cluster B rows."""

from __future__ import annotations

import json
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
PARSE = ROOT / "parse"
ROWS = [605, 606, 623, 635, 636, 5453, 5454, 5592, 5593, 5594]


def run_row(row: int) -> dict:
    proc = subprocess.run(
        [
            "mvn",
            "-q",
            "exec:java",
            "-Dexec.classpathScope=test",
            "-Dexec.mainClass=sql.latency.SllVsDefaultClusterBProbe",
            f"-Dexec.args={row}",
        ],
        cwd=PARSE,
        capture_output=True,
        text=True,
    )
    if proc.returncode != 0 and not proc.stdout.strip():
        print(proc.stderr, file=sys.stderr)
        proc.check_returncode()

    data: dict[str, str] = {}
    for line in proc.stdout.splitlines():
        if "=" in line:
            key, value = line.split("=", 1)
            data[key] = value

    def fatals(key: str) -> int:
        raw = data.get(key, "-1")
        return int(raw) if raw.isdigit() or (raw.startswith("-") and raw[1:].isdigit()) else -1

    default_f = fatals("DEFAULT_FATALS")
    sll_f = fatals("SLL_FATALS")
    default_err = data.get("DEFAULT_ERROR", "")
    sll_err = data.get("SLL_ERROR", "")

    # SLL regression: SLL emits more fatals than default, or only SLL path completes with fatals.
    sll_related = sll_f > max(default_f, 0)

    return {
        "csv_row": row,
        "default_fatals": default_f,
        "sll_fatals": sll_f,
        "default_error": default_err or None,
        "sll_error": sll_err or None,
        "sll_regression": sll_related,
        "default_top_scope": data.get("DEFAULT_TOP"),
        "sll_top_scope": data.get("SLL_TOP"),
        "default_table_alias": data.get("DEFAULT_ALIAS"),
        "sll_table_alias": data.get("SLL_ALIAS"),
        "fixture": f"sql/csv-row-{row}.sql",
    }


def main() -> int:
    subprocess.run(["mvn", "-q", "test-compile"], cwd=PARSE, check=True)

    results = [run_row(row) for row in ROWS]
    for item in results:
        print(json.dumps(item))

    sll_rows = [r["csv_row"] for r in results if r["sll_regression"]]
    out = PARSE / "docs/rmcp-handoff/5.1.3-panto-outstanding/cluster-b-sll-regression-rows.json"
    payload = {
        "description": "Cluster B E3 fast-FATAL rows where SLL-only parse emits more fatals than default (LL) prediction.",
        "source": "2.11.2 adjudication routed to 2.10 SLL→LL policy",
        "csv_rows": sll_rows,
        "rows": results,
    }
    out.write_text(json.dumps(payload, indent=2) + "\n", encoding="utf-8")
    print(f"\nWrote {out}")
    print(f"SLL-regression rows ({len(sll_rows)}): {sll_rows}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
