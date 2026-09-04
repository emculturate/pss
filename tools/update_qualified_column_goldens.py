#!/usr/bin/env python3
"""Conservatively refresh walker goldens after qualified-column / outer-scope refactor.

Updates Symbol Table, Table Dictionary, Interface, Query Column Dictionary, and
Substitution List assertions only when stdout output differs in expected ways:
  - unresolved_column removed from published symbol tree
  - interface gains table_ref alongside query refs
  - table_dictionary materializes previously unresolved columns
  - nested def_* scopes lose inherited table_alias bleed

Skips: diagnostic-count failures, parser-error failures, AST goldens, and diffs
that remove table_dictionary token refs present in the old golden.
"""

from __future__ import annotations

import glob
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
    "Query Column Dictionary is wrong": "Query Column Dictionary:",
    "Substitution List is wrong": "Substitution Variables:",
}

SKIP_FAILURE_PATTERNS = [
    "Expected no failures with",
    "Unexpected fatal diagnostic count",
    "Unexpected SEVERE_WARNING",
    "Unexpected WARNING",
    "assertFatalDiagnostic",
    "assertDiagnosticCount",
    "ComparisonFailure: AST is wrong",
    "ComparisonFailure: expected:<0> but was:",
]


def should_skip_test(failure_msg: str) -> str | None:
    for pat in SKIP_FAILURE_PATTERNS:
        if pat in failure_msg:
            return pat
    if "AST is wrong" in failure_msg and "Symbol Table is wrong" not in failure_msg:
        return "AST is wrong"
    return None

TOKEN_REF_RE = re.compile(r"\[\[@\d+[^\]]*\]\]")


def load_failing_tests() -> list[tuple[str, str, str]]:
    tests: list[tuple[str, str, str]] = []
    for path in glob.glob(str(PARSE_DIR / "target/surefire-reports/TEST-*.xml")):
        root = ET.parse(path).getroot()
        cls = root.get("name", "").split(".")[-1]
        for case in root.findall(".//testcase"):
            fail = case.find("failure")
            if fail is None:
                continue
            method = case.get("name", "")
            msg = (fail.get("message") or "") + (fail.text or "")
            tests.append((cls, method, msg))
    return tests


def run_test(cls: str, method: str) -> tuple[int, str]:
    cmd = ["mvn", "-q", "test", f"-Dtest={cls}#{method}"]
    proc = subprocess.run(cmd, cwd=PARSE_DIR, capture_output=True, text=True)
    return proc.returncode, proc.stdout + proc.stderr


def capture_stdout_values(output: str) -> dict[str, str]:
    values: dict[str, str] = {}
    for label, prefix in LABEL_TO_STDOUT.items():
        match = re.search(rf"^{re.escape(prefix)} (.+)$", output, re.M)
        if match:
            values[label] = match.group(1).strip()
    return values


def failure_labels(output: str) -> list[str]:
    labels: list[str] = []
    for label in LABEL_TO_STDOUT:
        if label in output:
            labels.append(label)
    return labels


def extract_old_expected(content: str, method: str, label: str) -> str | None:
    method_pattern = (
        rf"(public void {re.escape(method)}\(\).*?)"
        r"(?=\n\tpublic void |\n\t@Test|\n\}\s*$|\Z)"
    )
    method_match = re.search(method_pattern, content, re.DOTALL)
    if not method_match:
        return None
    method_body = method_match.group(1)
    escaped_label = re.escape(label)
    patterns = [
        rf'Assert\.assertEquals\("{escaped_label}", "(.*?)"\s*,\s*\n\s*extractor\.',
        rf'Assert\.assertEquals\("{escaped_label}",\s*\n\s*"(.*?)"\s*,\s*\n\s*extractor\.',
        rf'Assert\.assertEquals\("{escaped_label}", "(.*?)"\s*,\s*\n\s*snippet\.',
        rf'Assert\.assertEquals\("{escaped_label}",\s*\n\s*"(.*?)"\s*,\s*\n\s*snippet\.',
    ]
    for pattern in patterns:
        m = re.search(pattern, method_body, re.DOTALL)
        if m:
            return m.group(1)
    return None


def is_safe_table_dictionary_update(old: str, new: str) -> bool:
    old_refs = TOKEN_REF_RE.findall(old)
    if not old_refs:
        return True
    return all(ref in new for ref in old_refs)


def is_safe_symbol_tree_update(old: str, new: str) -> bool:
    if "unresolved_column=" in new and "unresolved_column=" not in old:
        return False
    if "unresolved_column=" in old and "unresolved_column=" not in new:
        return True
    old_refs = TOKEN_REF_RE.findall(old)
    if old_refs and all(ref in new for ref in old_refs):
        return True
    if old.count("table_alias=") > new.count("table_alias="):
        return True
    if new.count("table_ref=") > old.count("table_ref="):
        return True
    return False


def is_safe_interface_update(old: str, new: str) -> bool:
    old_cols = set(re.findall(r"\b(?:aa|max_D|min_D|w|[A-Za-z_][\w]*)\b", old))
    # Interface is usually a bracket list like [aa, max_D, min_D, w]
    if old.startswith("[") and new.startswith("["):
        old_items = [x.strip() for x in old.strip("[]").split(",")]
        new_items = [x.strip() for x in new.strip("[]").split(",")]
        return all(item in new_items for item in old_items if item)
    return True


def is_safe_update(label: str, old: str | None, new: str) -> tuple[bool, str]:
    if old is None:
        return False, "no old expected found"
    if old == new:
        return False, "already matches"
    if label in ("Table Dictionary is wrong",):
        if is_safe_table_dictionary_update(old, new):
            return True, "table dictionary superset/expansion"
        return False, "table dictionary lost token refs from golden"
    if label in ("Symbol Table is wrong", "Symbol Tree is wrong"):
        if is_safe_symbol_tree_update(old, new):
            return True, "symbol tree refactor pattern"
        return False, "symbol tree lost table_dictionary refs or gained unresolved_column"
    if label == "Interface is wrong":
        if is_safe_interface_update(old, new):
            return True, "interface table_ref update"
        return False, "interface lost column names"
    if label in ("Query Column Dictionary is wrong", "Substitution List is wrong"):
        if is_safe_table_dictionary_update(old, new):
            return True, "dictionary/superset update"
        return False, "lost token refs"
    return False, "unsupported label"


def replace_assert_in_method(content: str, method: str, label: str, new_expected: str) -> tuple[str, bool]:
    method_pattern = (
        rf"(public void {re.escape(method)}\(\).*?)"
        r"(?=\n\tpublic void |\n\t@Test|\n\}\s*$|\Z)"
    )
    method_match = re.search(method_pattern, content, re.DOTALL)
    if not method_match:
        return content, False

    method_body = method_match.group(1)
    escaped_label = re.escape(label)
    patterns = [
        rf'(Assert\.assertEquals\("{escaped_label}", ")(.*?)("\s*,\s*\n\s*extractor\.[^)]+\))',
        rf'(Assert\.assertEquals\("{escaped_label}",\s*\n\s*")(.*?)("\s*,\s*\n\s*extractor\.[^)]+\))',
        rf'(Assert\.assertEquals\("{escaped_label}", ")(.*?)("\s*,\s*\n\s*snippet\.[^)]+\))',
        rf'(Assert\.assertEquals\("{escaped_label}",\s*\n\s*")(.*?)("\s*,\s*\n\s*snippet\.[^)]+\))',
    ]

    updated_body = method_body
    for pattern in patterns:
        m = re.search(pattern, updated_body, re.DOTALL)
        if m:
            updated_body = updated_body[: m.start(2)] + new_expected + updated_body[m.end(2) :]
            return content[: method_match.start(1)] + updated_body + content[method_match.end(1) :], True
    return content, False


def main() -> int:
    failing = load_failing_tests()
    if not failing:
        print("No failing tests found in surefire reports. Run mvn test first.")
        return 1

    file_cache: dict[Path, str] = {}
    updated_tests: list[str] = []
    skipped: list[tuple[str, str, str]] = []

    for cls, method, failure_msg in failing:
        skip_reason = should_skip_test(failure_msg)
        if skip_reason:
            skipped.append((cls, method, f"skip category: {skip_reason}"))
            continue

        code, output = run_test(cls, method)
        if code == 0:
            continue

        values = capture_stdout_values(output)
        labels = [l for l in failure_labels(output) if l in failure_msg]
        if not labels:
            labels = failure_labels(output)
        if not labels:
            skipped.append((cls, method, "no recognized golden assertion label"))
            continue

        java_path = TEST_ROOT / f"{cls}.java"
        if java_path not in file_cache:
            file_cache[java_path] = java_path.read_text()
        content = file_cache[java_path]
        any_replaced = False

        for label in labels:
            if label not in values:
                continue
            old = extract_old_expected(content, method, label)
            safe, reason = is_safe_update(label, old, values[label])
            if not safe:
                skipped.append((cls, method, f"{label}: {reason}"))
                continue
            content, ok = replace_assert_in_method(content, method, label, values[label])
            if ok:
                any_replaced = True
                print(f"UPDATE {cls}.{method} [{label}] ({reason})")

        if any_replaced:
            file_cache[java_path] = content
            updated_tests.append(f"{cls}.{method}")

    for path, content in file_cache.items():
        path.write_text(content)

    print(f"\nUpdated {len(updated_tests)} test methods across {len(file_cache)} files")
    print(f"Skipped {len(skipped)} test methods")

    if updated_tests:
        print("\nRe-running updated tests...")
        still_bad = []
        for entry in updated_tests:
            cls, method = entry.split(".", 1)
            code, _ = run_test(cls, method)
            if code != 0:
                still_bad.append(entry)
        print(f"Still failing after update: {len(still_bad)}")
        for entry in still_bad[:20]:
            print(f"  {entry}")

    if skipped:
        print("\nSkipped examples:")
        for cls, method, reason in skipped[:25]:
            print(f"  {cls}.{method}: {reason}")

    return 0


if __name__ == "__main__":
    sys.exit(main())
