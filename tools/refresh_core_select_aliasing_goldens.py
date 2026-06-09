#!/usr/bin/env python3
"""Refresh symbol/table/query-dictionary goldens for SqlEventWalkerCoreSelectFromAliasingTests."""

from __future__ import annotations

import re
import subprocess
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

PARSE_DIR = Path(__file__).resolve().parent.parent / "parse"
TEST_ROOT = PARSE_DIR / "src/test/java/sql/walker"
TARGET = "SqlEventWalkerCoreSelectFromAliasingTests"
JAVA = TEST_ROOT / f"{TARGET}.java"

LABEL_TO_STDOUT = {
    "Symbol Table is wrong": "Symbol Tree:",
    "Symbol Tree is wrong": "Symbol Tree:",
    "Interface is wrong": "Interface:",
    "Table Dictionary is wrong": "Table Dictionary:",
    "Query Column Dictionary is wrong": "Query Column Dictionary:",
    "Substitution List is wrong": "Substitution Variables:",
}

SKIP_PATTERNS = [
    "Unexpected fatal diagnostic count",
    "assertFatalDiagnostic",
    "assertDiagnosticCount",
]


def load_failing() -> list[tuple[str, str]]:
    tests: list[tuple[str, str]] = []
    for path in PARSE_DIR.glob("target/surefire-reports/TEST-*.xml"):
        root = ET.parse(path).getroot()
        cls = root.get("name", "").split(".")[-1]
        if cls != TARGET:
            continue
        for case in root.findall(".//testcase"):
            fail = case.find("failure")
            if fail is not None:
                method = case.get("name", "")
                msg = (fail.get("message") or "") + (fail.text or "")
                tests.append((method, msg))
    return tests


def run_test(method: str) -> tuple[int, str]:
    proc = subprocess.run(
        ["mvn", "-q", "test", f"-Dtest={TARGET}#{method}"],
        cwd=PARSE_DIR,
        capture_output=True,
        text=True,
    )
    return proc.returncode, proc.stdout + proc.stderr


def capture(output: str) -> dict[str, str]:
    vals: dict[str, str] = {}
    for label, prefix in LABEL_TO_STDOUT.items():
        match = re.search(rf"^{re.escape(prefix)} (.+)$", output, re.M)
        if match:
            vals[label] = match.group(1).strip()
    return vals


def extract_method_body(content: str, method: str) -> str | None:
    pattern = (
        rf"(public void {re.escape(method)}\(\).*?)"
        r"(?=\n\tpublic void |\n\t@Test|\n\}\s*$|\Z)"
    )
    match = re.search(pattern, content, re.DOTALL)
    return match.group(1) if match else None


def replace_assert(content: str, method: str, label: str, new_val: str) -> tuple[str, bool]:
    body = extract_method_body(content, method)
    if not body:
        return content, False
    esc = re.escape(label)
    patterns = [
        rf'(Assert\.assertEquals\("{esc}", ")(.*?)("\s*,\s*\n\s*extractor\.[^)]+\))',
        rf'(Assert\.assertEquals\("{esc}",\s*\n\s*")(.*?)("\s*,\s*\n\s*extractor\.[^)]+\))',
    ]
    new_body = body
    for pattern in patterns:
        match = re.search(pattern, new_body, re.DOTALL)
        if match:
            new_body = new_body[: match.start(2)] + new_val + new_body[match.end(2) :]
            start = content.find(body)
            return content[:start] + new_body + content[start + len(body) :], True
    return content, False


def main() -> int:
    failing = load_failing()
    if not failing:
        print("No failures found. Run mvn test first.")
        return 1

    content = JAVA.read_text()
    updated: list[str] = []
    skipped: list[tuple[str, str]] = []

    for method, msg in failing:
        if any(p in msg for p in SKIP_PATTERNS):
            skipped.append((method, "fatal/diagnostic"))
            continue
        if "AST is wrong" in msg and not any(label in msg for label in LABEL_TO_STDOUT):
            skipped.append((method, "AST only"))
            continue

        code, output = run_test(method)
        if code == 0:
            continue

        vals = capture(output)
        labels_in_fail = list(vals.keys())
        if not labels_in_fail:
            skipped.append((method, "no stdout values"))
            continue

        changed = False
        for label in labels_in_fail:
            if label not in vals:
                continue
            content, ok = replace_assert(content, method, label, vals[label])
            if ok:
                print(f"UPDATE {method} [{label}]")
                changed = True

        if changed:
            updated.append(method)
        else:
            skipped.append((method, "no replace"))

    JAVA.write_text(content)
    print(f"\nUpdated {len(updated)} methods, skipped {len(skipped)}")

    still: list[str] = []
    for method in updated:
        code, _ = run_test(method)
        if code != 0:
            still.append(method)
    print(f"Still failing: {len(still)}")
    for method in still:
        print(f"  {method}")

    for method, reason in skipped:
        print(f"  SKIP {method}: {reason}")

    return 0


if __name__ == "__main__":
    sys.exit(main())
