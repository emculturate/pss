#!/usr/bin/env python3
"""Batch-refresh Assert.assertEquals goldens for INTERSECT→EXCEPT clone tests."""

from __future__ import annotations

import re
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
TEST_DIR = ROOT / "src/test/java/sql/walker"

LABELS = {
    "AST:": "AST is wrong",
    "Result:": "AST is wrong",
    "Interface:": "Interface is wrong",
    "Substitution Variables:": "Substitution List is wrong",
    "Table Dictionary:": "Table Dictionary is wrong",
    "Query Column Dictionary:": "Query Column Dictionary is wrong",
    "Symbol Tree:": "Symbol Table is wrong",
}


def find_class_for_method(method_name: str) -> str | None:
    for path in TEST_DIR.glob("*.java"):
        if f"void {method_name}(" in path.read_text():
            return path.stem
    return None


def extract_method_span(text: str, method_name: str) -> tuple[int, int] | None:
    match = re.search(rf"@Test\s+public\s+void\s+{re.escape(method_name)}\s*\(\s*\)\s*\{{", text)
    if not match:
        return None
    open_brace = text.find("{", match.end() - 1)
    depth = 0
    in_string = False
    escape = False
    i = open_brace
    while i < len(text):
        ch = text[i]
        if in_string:
            if escape:
                escape = False
            elif ch == "\\":
                escape = True
            elif ch == '"':
                in_string = False
            i += 1
            continue
        if ch == '"':
            in_string = True
            i += 1
            continue
        if ch == "{":
            depth += 1
        elif ch == "}":
            depth -= 1
            if depth == 0:
                return open_brace, i + 1
        i += 1
    return None


def run_probe(class_name: str, method_name: str) -> dict[str, str]:
    proc = subprocess.run(
        [
            "mvn",
            "-q",
            "test",
            f"-Dtest={class_name}#{method_name}",
            "-Dpss.walker.test.verbose=true",
        ],
        cwd=ROOT,
        capture_output=True,
        text=True,
    )
    output = proc.stdout + "\n" + proc.stderr
    values: dict[str, str] = {}
    for line in output.splitlines():
        for prefix, label in LABELS.items():
            if line.startswith(prefix):
                values[label] = line[len(prefix) :].strip()
    return values


def replace_assert_equals_in_span(span: str, label: str, new_value: str) -> tuple[str, bool]:
    pattern = re.compile(
        rf'Assert\.assertEquals\("{re.escape(label)}",\s*"((?:\\.|[^"\\])*)"',
        re.DOTALL,
    )
    match = pattern.search(span)
    if not match:
        return span, False
    escaped = new_value.replace("\\", "\\\\").replace('"', '\\"')
    start, end = match.span(1)
    return span[:start] + escaped + span[end:], True


def refresh_method(class_name: str, method_name: str) -> bool:
    path = TEST_DIR / f"{class_name}.java"
    values = run_probe(class_name, method_name)
    if "AST is wrong" not in values and "Symbol Table is wrong" not in values:
        print(f"SKIP {class_name}#{method_name}")
        return False

    text = path.read_text()
    span_bounds = extract_method_span(text, method_name)
    if span_bounds is None:
        print(f"NO SPAN {class_name}#{method_name}")
        return False

    start, end = span_bounds
    span = text[start:end]
    updated_span = span
    changed = False
    for label, value in values.items():
        updated_span, ok = replace_assert_equals_in_span(updated_span, label, value)
        changed = changed or ok

    if not changed:
        print(f"NOOP {class_name}#{method_name}")
        return False

    path.write_text(text[:start] + updated_span + text[end:])
    print(f"UPDATED {class_name}#{method_name}")
    return True


def main(argv: list[str]) -> int:
    if len(argv) > 1 and argv[1] == "--file":
        methods = [
            line.strip()
            for line in Path(argv[2]).read_text().splitlines()
            if line.strip()
        ]
    elif len(argv) > 1:
        methods = argv[1:]
    else:
        methods_path = ROOT / "tools/intersect_except_clone_methods.txt"
        methods = [
            line.strip()
            for line in methods_path.read_text().splitlines()
            if line.strip()
        ]

    updated = 0
    for method_name in methods:
        class_name = find_class_for_method(method_name)
        if class_name is None:
            print(f"UNKNOWN CLASS {method_name}")
            continue
        if refresh_method(class_name, method_name):
            updated += 1
    print(f"Updated {updated}/{len(methods)}")
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv))
