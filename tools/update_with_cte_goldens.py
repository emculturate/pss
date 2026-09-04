#!/usr/bin/env python3
"""Update golden assertions for WITH/CTE tests (no predicand subqueries)."""

import re
import subprocess
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
PARSE = ROOT / "parse"
REPORTS = PARSE / "target" / "surefire-reports"
TEST_ROOT = PARSE / "src/test/java/sql/walker"

TEST_CLASSES = [
    "sql.walker.SqlEventWalkerCoreSelectFromAliasingTests",
    "sql.walker.SqlEventWalkerSubqueriesAndClauseSemanticsTests",
    "sql.walker.SqlEventWalkerDmlUpdateInsertDeleteTruncateTests",
    "sql.walker.SqlEventWalkerPredicatesOperatorsSubstitutionsTests",
]

EXTRA_METHODS = {
    "SqlEventWalkerCoreSelectFromAliasingTests": [
        "correlatedScalarPredicandPlainUnionBranchOuterFatalTest",
    ],
}

GOLDEN_LABELS = (
    "Symbol Table is wrong",
    "Table Dictionary is wrong",
    "Query Column Dictionary is wrong",
)

FIELD_PATTERNS = {
    "Symbol Table is wrong": re.compile(r"^Symbol Tree: (.+)$"),
    "Table Dictionary is wrong": re.compile(r"^Table Dictionary: (.+)$"),
    "Query Column Dictionary is wrong": re.compile(r"^Query Column Dictionary: (.+)$"),
}

PREDICAND_METHOD_RE = re.compile(
    r"(?i)(predicand|correlatedin|correlatedexists|siblingsubquery|scalarSubqueriesCorrelated|"
    r"nestedFormulaSubqueries|havingScalar|havingExists|ilikeAny|iLikeAnyInList|"
    r"scalarSubqueriesSymbolTable|multipleScalarAndOther|selectItemSubquery|subqueryParseTest)"
)


def class_to_path(classname: str) -> Path:
    return TEST_ROOT / f"{classname.split('.')[-1]}.java"


def discover_methods(java_path: Path) -> set[str]:
    text = java_path.read_text()
    methods = set()
    for m in re.finditer(r"public void (\w+)\(\)", text):
        name = m.group(1)
        if name in (EXTRA_METHODS.get(java_path.stem, []) or []):
            methods.add(name)
            continue
        if PREDICAND_METHOD_RE.search(name):
            continue
        start = m.start()
        nxt = text.find("\n\t@Test", start + 1)
        if nxt < 0:
            nxt = len(text)
        block = text[start:nxt]
        if "context_list" not in block:
            continue
        if re.search(r"predicand\d+\s*=\s*query", block):
            continue
        methods.add(name)
    for name in EXTRA_METHODS.get(java_path.stem, []):
        methods.add(name)
    return methods


def failing_golden_tests() -> list[tuple[str, str]]:
    allowed = set()
    for classname in TEST_CLASSES:
        allowed |= discover_methods(class_to_path(classname))

    tests = []
    seen = set()
    for classname in TEST_CLASSES:
        xml_path = REPORTS / f"TEST-{classname}.xml"
        if not xml_path.exists():
            continue
        root = ET.parse(xml_path).getroot()
        for case in root.findall("testcase"):
            method = case.attrib["name"]
            if method not in allowed:
                continue
            for tag in ("failure", "error"):
                failure = case.find(tag)
                if failure is None:
                    continue
                message = failure.attrib.get("message", "")
                if not any(label in message for label in GOLDEN_LABELS):
                    continue
                key = (classname, method)
                if key not in seen:
                    seen.add(key)
                    tests.append(key)
    return tests


def capture_fields(classname: str, method: str) -> dict[str, str]:
    cmd = ["mvn", "-q", "test", f"-Dtest={classname}#{method}"]
    proc = subprocess.run(cmd, cwd=PARSE, capture_output=True, text=True)
    output = proc.stdout + proc.stderr
    captured = {}
    for line in output.splitlines():
        stripped = line.strip()
        for label, pattern in FIELD_PATTERNS.items():
            m = pattern.match(stripped)
            if m:
                captured[label] = m.group(1)
    return captured


def update_test_file(java_path: Path, method: str, label: str, actual: str) -> bool:
    text = java_path.read_text()
    needle = f"public void {method}()"
    start = text.find(needle)
    if start < 0:
        return False
    next_test = text.find("\n\t@Test", start + 1)
    if next_test < 0:
        next_test = len(text)
    block = text[start:next_test]

    label_pattern = f'Assert.assertEquals("{label}"'
    astart = block.find(label_pattern)
    if astart < 0:
        return False

    # Support both single-line and multiline assertEquals(label, "expected", actual)
    quote_start = block.find('"', astart + len(label_pattern))
    if quote_start < 0:
        return False
    quote_start += 1
    quote_end = quote_start
    while quote_end < len(block):
        quote_end = block.find('"', quote_end)
        if quote_end < 0:
            return False
        if quote_end > quote_start and block[quote_end - 1] != "\\":
            break
        quote_end += 1
    old = block[quote_start:quote_end]
    if old == actual:
        return False
    new_block = block[:quote_start] + actual + block[quote_end:]
    text = text[:start] + new_block + text[next_test:]
    java_path.write_text(text)
    return True


def main() -> int:
    if not any((REPORTS / f"TEST-{c}.xml").exists() for c in TEST_CLASSES):
        subprocess.run(
            ["mvn", "-q", "test", "-Dtest=" + ",".join(c.split(".")[-1] for c in TEST_CLASSES)],
            cwd=PARSE,
        )

    tests = failing_golden_tests()
    if not tests:
        print("No failing WITH/CTE golden tests found.")
        return 0

    print(f"Updating {len(tests)} failing WITH/CTE golden tests...")
    updated = 0
    skipped = 0
    for classname, method in tests:
        java_path = class_to_path(classname)
        captured = capture_fields(classname, method)
        if not captured:
            print(f"  SKIP {method}: no stdout fields", file=sys.stderr)
            skipped += 1
            continue
        for label, actual in captured.items():
            if update_test_file(java_path, method, label, actual):
                print(f"  UPDATED {method} ({label})")
                updated += 1
    print(f"Updated {updated} assertions, skipped {skipped} methods.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
