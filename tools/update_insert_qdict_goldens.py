#!/usr/bin/env python3
"""Update insert qdict golden assertions from test stdout for failing tests."""

import re
import subprocess
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
PARSE = ROOT / "parse"
REPORTS = PARSE / "target" / "surefire-reports"

FIELD_PATTERNS = {
    "Symbol Tree is wrong": re.compile(r"^Symbol Tree: (.+)$"),
    "Symbol Table is wrong": re.compile(r"^Symbol Tree: (.+)$"),
    "Query Column Dictionary is wrong": re.compile(r"^Query Column Dictionary: (.+)$"),
}


def failing_tests():
    tests = []
    seen = set()
    for xml_path in REPORTS.glob("TEST-*.xml"):
        if "DmlUpdateInsertDelete" not in xml_path.name and "AccessObject" not in xml_path.name:
            continue
        root = ET.parse(xml_path).getroot()
        classname = root.attrib.get("name", "")
        for case in root.findall("testcase"):
            failure = case.find("failure")
            if failure is None:
                failure = case.find("error")
            if failure is None:
                continue
            message = failure.attrib.get("message", "")
            m = re.search(r"^(Symbol Tree is wrong|Symbol Table is wrong|Query Column Dictionary is wrong)", message)
            if not m:
                continue
            key = (classname, case.attrib["name"])
            if key in seen:
                continue
            seen.add(key)
            tests.append(key)
    return tests


def capture_fields(classname, method):
    cmd = [
        "mvn",
        "-q",
        "test",
        f"-Dtest={classname}#{method}",
    ]
    proc = subprocess.run(cmd, cwd=PARSE, capture_output=True, text=True)
    output = proc.stdout + proc.stderr
    captured = {}
    for line in output.splitlines():
        for label, pattern in FIELD_PATTERNS.items():
            m = pattern.match(line.strip())
            if m:
                captured[label] = m.group(1)
    return captured


def update_test_file(java_path: Path, method: str, label: str, actual: str) -> bool:
    text = java_path.read_text()
    needle = f'public void {method}()'
    start = text.find(needle)
    if start < 0:
        return False
    next_test = text.find("\n\t@Test", start + 1)
    if next_test < 0:
        next_test = len(text)
    block = text[start:next_test]
    assert_prefix = f'Assert.assertEquals("{label}", "'
    astart = block.find(assert_prefix)
    if astart < 0:
        return False
    astart += len(assert_prefix)
    aend = block.find('",', astart)
    if aend < 0:
        return False
    old = block[astart:aend]
    if old == actual:
        return False
    new_block = block[:astart] + actual + block[aend:]
    text = text[:start] + new_block + text[next_test:]
    java_path.write_text(text)
    return True


def main():
    if not (REPORTS / "TEST-sql.walker.SqlEventWalkerDmlUpdateInsertDeleteTruncateTests.xml").exists():
        subprocess.run(
            [
                "mvn",
                "-q",
                "test",
                "-Dtest=SqlEventWalkerDmlUpdateInsertDeleteTruncateTests,SqlParseEventWalkerWithAccessObjectTest",
            ],
            cwd=PARSE,
        )

    tests = failing_tests()
    if not tests:
        print("No failing insert golden tests found in surefire reports.")
        return 0

    class_files = {
        "sql.walker.SqlEventWalkerDmlUpdateInsertDeleteTruncateTests": PARSE
        / "src/test/java/sql/walker/SqlEventWalkerDmlUpdateInsertDeleteTruncateTests.java",
        "sql.walker.SqlParseEventWalkerWithAccessObjectTest": PARSE
        / "src/test/java/sql/walker/SqlParseEventWalkerWithAccessObjectTest.java",
    }

    updated = 0
    for classname, method in tests:
        java_path = class_files.get(classname)
        if java_path is None:
            continue
        captured = capture_fields(classname, method)
        if not captured:
            print(f"SKIP {method}: no captured output", file=sys.stderr)
            continue
        for label, actual in captured.items():
            if update_test_file(java_path, method, label, actual):
                print(f"UPDATED {method} ({label})")
                updated += 1

    print(f"Updated {updated} assertions.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
