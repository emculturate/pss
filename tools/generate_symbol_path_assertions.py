#!/usr/bin/env python3
"""Generate def_* symbol-tree path assertions from SQL using parser runtime output."""

from __future__ import annotations

import argparse
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
PARSE_DIR = ROOT / "parse"


def run(cmd: list[str]) -> int:
    proc = subprocess.run(cmd, cwd=ROOT)
    return proc.returncode


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--sql-file", required=True, help="Path to a SQL text file")
    parser.add_argument(
        "--assertion-method",
        default="assertSymbolTreePathEquals",
        help="Assertion helper method to emit",
    )
    parser.add_argument(
        "--root",
        default="symbolTable",
        help=(
            "Root object to walk: symbolTable, tableDictionary, queryDictionary, "
            "sqlTree, substitutionsMap, arrayOutputCollectors"
        ),
    )
    parser.add_argument(
        "--path-regex",
        default=".*",
        help="Regex for full dotted paths to emit",
    )
    parser.add_argument(
        "--variable-prefix",
        default="expected",
        help="Prefix for generated Java expected-string variables",
    )
    parser.add_argument(
        "--skip-build",
        action="store_true",
        help="Skip Maven test-compile before generation",
    )
    args = parser.parse_args()

    sql_file = Path(args.sql_file).resolve()
    if not sql_file.exists():
        print(f"SQL file does not exist: {sql_file}", file=sys.stderr)
        return 2

    if not args.skip_build:
        build_cmd = [
            "mvn",
            "-f",
            str(PARSE_DIR / "pom.xml"),
            "-DskipTests",
            "test-compile",
        ]
        rc = run(build_cmd)
        if rc != 0:
            return rc

    cmd = [
        "mvn",
        "-f",
        str(PARSE_DIR / "pom.xml"),
        "-q",
        "-DskipTests",
        "-Dexec.mainClass=cli.SymbolTreeAssertionGenerator",
        (
            "-Dexec.args="
            + f'--sql-file "{sql_file}" '
            + f'--assertion-method "{args.assertion_method}" '
            + f'--root "{args.root}" '
            + f'--path-regex "{args.path_regex}" '
            + f'--variable-prefix "{args.variable_prefix}"'
        ),
        "org.codehaus.mojo:exec-maven-plugin:3.5.0:java",
    ]
    return run(cmd)


if __name__ == "__main__":
    raise SystemExit(main())
