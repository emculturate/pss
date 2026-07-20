#!/usr/bin/env python3
"""Refresh Option E goldens after bulk/prepend removal (ordering + diagnostic anchors)."""

from __future__ import annotations

import re
import subprocess
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

PARSE_DIR = Path(__file__).resolve().parent.parent / "parse"
TEST_ROOT = PARSE_DIR / "src/test/java/sql/walker"

LABEL_TO_STDOUT = {
    "Symbol Table is wrong": "Symbol Tree:",
    "Symbol Tree is wrong": "Symbol Tree:",
    "Interface is wrong": "Interface:",
    "Table Dictionary is wrong": "Table Dictionary:",
    "Table dictionary is wrong": "Table Dictionary:",
    "Query Column Dictionary is wrong": "Query Column Dictionary:",
    "Substitution List is wrong": "Substitution Variables:",
}

SKIP_PATTERNS = [
    "Unexpected fatal diagnostic count",
    "assertFatalDiagnostic",
    "assertDiagnosticCount",
]


def load_failing() -> list[tuple[str, str, str]]:
    tests: list[tuple[str, str, str]] = []
    for path in PARSE_DIR.glob("target/surefire-reports/TEST-*.xml"):
        root = ET.parse(path).getroot()
        cls = root.get("name", "").split(".")[-1]
        if not cls.startswith("SqlEventWalker"):
            continue
        for case in root.findall(".//testcase"):
            fail = case.find("failure")
            if fail is None:
                continue
            method = case.get("name", "")
            msg = (fail.get("message") or "") + (fail.text or "")
            tests.append((cls, method, msg))
    return tests


def run_test(cls: str, method: str) -> tuple[int, str]:
    proc = subprocess.run(
        ["mvn", "-q", "test", f"-Dtest={cls}#{method}"],
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
        rf'(Assert\.assertEquals\("{esc}", ")(.*?)("\s*,\s*\n\s*extractor\.[^)]+\.toString\(\)\))',
        rf'(Assert\.assertEquals\("{esc}", ")(.*?)("\s*,\s*extractor\.[^)]+\))',
    ]
    new_body = body
    for pattern in patterns:
        match = re.search(pattern, new_body, re.DOTALL)
        if match:
            new_body = new_body[: match.start(2)] + new_val + new_body[match.end(2) :]
            start = content.find(body)
            return content[:start] + new_body + content[start + len(body) :], True
    return content, False


def replace_diagnostic_position(content: str, method: str, actual_line: int, actual_char: int) -> tuple[str, bool]:
    body = extract_method_body(content, method)
    if not body:
        return content, False
    pattern = (
        rf"(public void {re.escape(method)}\(\).*?"
        r"assertDiagnosticAtPosition\(\s*"
        r"(?:snippet,\s*)?"
        r"[^,]+,\s*[^,]+,\s*[^,]+,\s*"
        r'"[^"]*",\s*'
        r'"[^"]*",\s*'
        r")(\d+)(\s*,\s*)(\d+)(\s*\))"
    )
    match = re.search(pattern, body, re.DOTALL)
    if not match:
        return content, False
    new_body = (
        body[: match.start(2)]
        + str(actual_line)
        + match.group(3)
        + str(actual_char)
        + match.group(5)
    )
    start = content.find(body)
    return content[:start] + new_body + content[start + len(body) :], True


def replace_diagnostic_list_summary(content: str, method: str, new_summary: str) -> tuple[str, bool]:
    body = extract_method_body(content, method)
    if not body:
        return content, False
    pattern = (
        r"(assertDiagnosticListByCodeAndSeverity\(\s*snippet,\s*"
        r'"UNRESOLVED_UNQUALIFIED_COLUMNS",\s*'
        r"ParseDiagnostic\.Severity\.ERROR,\s*)"
        r'((?:"[^"]*"\s*(?:\n\s*\+\s*)?)+)'
        r"(\s*\))"
    )
    match = re.search(pattern, body, re.DOTALL)
    if not match:
        return content, False
    escaped = new_summary.replace("\\", "\\\\").replace('"', '\\"')
    parts = escaped.split("\n")
    if len(parts) == 1:
        replacement = f'"{parts[0]}"'
    else:
        replacement = '"' + parts[0] + '"\n\t\t\t\t+ "' + '"\n\t\t\t\t+ "'.join(parts[1:]) + '"'
    new_body = body[: match.start(2)] + replacement + body[match.end(2) :]
    start = content.find(body)
    return content[:start] + new_body + content[start + len(body) :], True


def parse_diagnostic_position_failure(msg: str) -> tuple[int, int] | None:
    match = re.search(
        r"Unexpected diagnostic character position expected:<(\d+)> but was:<(\d+)>",
        msg,
    )
    if not match:
        match = re.search(
            r"Unexpected diagnostic line expected:<(\d+)> but was:<(\d+)>",
            msg,
        )
        if match:
            return int(match.group(2)), -1
        return None
    return -1, int(match.group(2))


def capture_diagnostic_summary(output: str, code: str) -> str | None:
    in_diag = False
    lines: list[str] = []
    for line in output.splitlines():
        if line.startswith("errorhandling.ParseErrorListener found Diagnostics:"):
            in_diag = True
            continue
        if in_diag and line.strip() == "":
            break
        if in_diag and code in line and "severity=ERROR" in line:
            lines.append(line.strip())
    if not lines:
        return None
    return "\n".join(lines)


def main() -> int:
    failing = load_failing()
    if not failing:
        print("No failures found. Run mvn test first.")
        return 1

    by_class: dict[str, list[tuple[str, str]]] = {}
    for cls, method, msg in failing:
        by_class.setdefault(cls, []).append((method, msg))

    updated: list[str] = []
    skipped: list[tuple[str, str]] = []

    for cls, cases in sorted(by_class.items()):
        java = TEST_ROOT / f"{cls}.java"
        if not java.exists():
            for method, msg in cases:
                skipped.append((f"{cls}#{method}", "missing java"))
            continue

        content = java.read_text()
        class_changed = False

        for method, msg in cases:
            if any(p in msg for p in SKIP_PATTERNS):
                skipped.append((f"{cls}#{method}", "fatal/diagnostic guard"))
                continue

            if "assertDiagnosticAtPosition" in msg or "Unexpected diagnostic" in msg:
                pos = parse_diagnostic_position_failure(msg)
                if pos is not None:
                    _, actual_char = pos
                    if actual_char > 0:
                        content, ok = replace_diagnostic_position(content, method, -1, actual_char)
                        if ok:
                            print(f"UPDATE {cls}#{method} [diagnostic char position -> {actual_char}]")
                            class_changed = True
                            updated.append(f"{cls}#{method}")
                            continue
                skipped.append((f"{cls}#{method}", "diagnostic position manual"))
                continue

            if "assertDiagnosticListByCodeAndSeverity" in msg:
                code, output = run_test(cls, method)
                if code == 0:
                    continue
                summary = capture_diagnostic_summary(output, "UNRESOLVED_UNQUALIFIED_COLUMNS")
                if summary:
                    content, ok = replace_diagnostic_list_summary(content, method, summary)
                    if ok:
                        print(f"UPDATE {cls}#{method} [diagnostic list summary]")
                        class_changed = True
                        updated.append(f"{cls}#{method}")
                        continue
                skipped.append((f"{cls}#{method}", "diagnostic list manual"))
                continue

            code, output = run_test(cls, method)
            if code == 0:
                continue

            vals = capture(output)
            if not vals:
                skipped.append((f"{cls}#{method}", "no stdout values"))
                continue

            changed = False
            for label, value in vals.items():
                content, ok = replace_assert(content, method, label, value)
                if ok:
                    print(f"UPDATE {cls}#{method} [{label}]")
                    changed = True

            if changed:
                class_changed = True
                updated.append(f"{cls}#{method}")
            else:
                skipped.append((f"{cls}#{method}", "no replace"))

        if class_changed:
            java.write_text(content)

    print(f"\nUpdated {len(updated)} methods, skipped {len(skipped)}")

    still: list[str] = []
    for entry in updated:
        cls, method = entry.split("#", 1)
        code, _ = run_test(cls, method)
        if code != 0:
            still.append(entry)
    print(f"Still failing: {len(still)}")
    for entry in still:
        print(f"  {entry}")

    for entry, reason in skipped:
        print(f"  SKIP {entry}: {reason}")

    return 0 if not still else 1


if __name__ == "__main__":
    sys.exit(main())
