#!/usr/bin/env python3
"""Refresh SqlEventWalkerPivotUnpivotTests expected strings from live walker output."""
from __future__ import annotations

import re
import subprocess
import sys
from pathlib import Path

REPO = Path(__file__).resolve().parents[1]
TEST_JAVA = REPO / "src/test/java/sql/walker/SqlEventWalkerPivotUnpivotTests.java"
PROPS = REPO / "src/test/resources/pivot_unpivot_queries.properties"


def eval_query_concat(expr: str) -> str:
    parts = re.findall(r'"(?:\\.|[^"\\])*"', expr)
    out: list[str] = []
    for p in parts:
        s = p[1:-1]
        s = s.replace("\\n", "\n").replace("\\t", "\t").replace("\\'", "'").replace('\\"', '"')
        out.append(s)
    return "".join(out)


def extract_tests(java_text: str) -> dict[str, str]:
    pattern = re.compile(
        r"@Test\s+(?://[^\n]*\n\s*)*public void (\w+)\(\)\s*\{(.*?)(?=\n\t@Test|\n\}$)",
        re.DOTALL,
    )
    tests: dict[str, str] = {}
    for match in pattern.finditer(java_text):
        name = match.group(1)
        body = match.group(2)
        if "runParsertest(query, parser)" not in body and "runSQLParsertestAllowErrors(query, parser)" not in body and "runParsertest(query, parse(query))" not in body:
            continue
        qm = re.search(
            r"final String query\s*=\s*(.*?);\s*\n\s*final SQLSelectParserParser",
            body,
            re.DOTALL,
        )
        if not qm:
            qm = re.search(
                r"final String query\s*=\s*(.*?);\s*\n\s*\n\s*final SQLSelectParserParser",
                body,
                re.DOTALL,
            )
        if not qm:
            qm = re.search(
                r"final String query\s*=\s*(.*?);\s*\n\s*SqlParseEventWalker extractor = runParsertest\(query, parse\(query\)\)",
                body,
                re.DOTALL,
            )
        if not qm:
            continue
        tests[name] = eval_query_concat(qm.group(1).strip())
    return tests


def write_properties(tests: dict[str, str], java_text: str) -> None:
    PROPS.parent.mkdir(parents=True, exist_ok=True)
    lines: list[str] = []
    for name, query in sorted(tests.items()):
        escaped = query.replace("\\", "\\\\").replace("\n", "\\n").replace("\r", "")
        lines.append(f"{name}={escaped}\n")
    for match in re.finditer(
        r"@Test\s+(?://[^\n]*\n\s*)*public void (\w+)\(\)\s*\{(.*?)(?=\n\t@Test|\n\}$)",
        java_text,
        re.DOTALL,
    ):
        name = match.group(1)
        body = match.group(2)
        if "runSQLParsertestAllowErrors(query, parser)" in body and name in tests:
            lines.append(f"{name}.allowErrors=true\n")
    PROPS.write_text("".join(lines), encoding="utf-8")


def run_capture() -> dict[tuple[str, str], str]:
    subprocess.run(["mvn", "-q", "test-compile", "-DskipTests"], cwd=REPO, check=True)
    cp = subprocess.check_output(
        [
            "mvn",
            "-q",
            "-DincludeScope=test",
            "dependency:build-classpath",
            "-Dmdep.outputFile=/dev/stdout",
        ],
        cwd=REPO,
        text=True,
    ).strip()
    full_cp = f"{REPO / 'target/test-classes'}:{REPO / 'target/classes'}:{cp}"
    out = subprocess.run(
        ["java", "-cp", full_cp, "sql.walker.WalkerGoldenCaptureOnce", "--pivot-unpivot"],
        cwd=REPO,
        text=True,
        capture_output=True,
    )
    if out.returncode != 0 and not out.stdout:
        out.check_returncode()
    combined = out.stdout + out.stderr
    goldens: dict[tuple[str, str], str] = {}
    for line in combined.splitlines():
        if not line.startswith("GOLDEN|"):
            continue
        _, method, field, value = line.split("|", 3)
        goldens[(method, field)] = value.replace("\\n", "\n")
    return goldens


LABELS = {
    "AST": ("AST is wrong", "getAsTree"),
    "Interface": ("Interface is wrong", "getInterface"),
    "Substitution": ("Substitution List is wrong", "getSubstitutionsMap"),
    "TableDictionary": ("Table Dictionary is wrong", "getTableColumnDictionaryMap"),
    "QueryDictionary": ("Query Column Dictionary is wrong", "getQueryColumnDictionaryMap"),
    "SymbolTable": ("Symbol Table is wrong", "getSymbolTable"),
}


def escape_java(s: str) -> str:
    return (
        s.replace("\\", "\\\\")
        .replace('"', '\\"')
        .replace("\n", "\\n")
        .replace("\r", "")
        .replace("\t", "\\t")
    )


def replace_expected_in_block(block: str, label: str, getter: str, expected: str) -> str:
    marker = f'Assert.assertEquals("{label}",'
    idx = block.find(marker)
    if idx < 0:
        return block
    getter_marker = f"extractor.{getter}().toString()"
    getter_idx = block.find(getter_marker, idx)
    if getter_idx < 0:
        return block
    quote_start = block.find('"', idx + len(marker))
    if quote_start < 0 or quote_start >= getter_idx:
        return block
    i = quote_start + 1
    while i < getter_idx:
        if block[i] == "\\":
            i += 2
            continue
        if block[i] == '"':
            quote_end = i
            return block[: quote_start + 1] + escape_java(expected) + block[quote_end:]
        i += 1
    return block


def patch_method_block(block: str, method: str, goldens: dict[tuple[str, str], str]) -> str:
    for field, (label, getter) in LABELS.items():
        key = (method, field)
        if key not in goldens:
            continue
        block = replace_expected_in_block(block, label, getter, goldens[key])
    return block


def patch_java(java_text: str, goldens: dict[tuple[str, str], str], methods: list[str]) -> str:
    for method in methods:
        start = java_text.find(f"public void {method}()")
        if start < 0:
            continue
        end = java_text.find("\n\t@Test", start + 1)
        if end < 0:
            end = len(java_text)
        block = java_text[start:end]
        java_text = java_text[:start] + patch_method_block(block, method, goldens) + java_text[end:]
    return java_text


def main() -> int:
    methods_filter = sys.argv[1:] if len(sys.argv) > 1 else None
    java_text = TEST_JAVA.read_text(encoding="utf-8")
    all_tests = extract_tests(java_text)
    tests = all_tests
    if methods_filter:
        missing = [m for m in methods_filter if m not in all_tests]
        if missing:
            print(f"Unknown test methods: {missing}", file=sys.stderr)
            return 1
        tests = {m: all_tests[m] for m in methods_filter}
    if not all_tests:
        print("No tests extracted", file=sys.stderr)
        return 1
    write_properties(all_tests, java_text)
    goldens = run_capture()
    patch_names = list(tests.keys())
    java_text = patch_java(java_text, goldens, patch_names)
    TEST_JAVA.write_text(java_text, encoding="utf-8")
    compile = subprocess.run(
        ["mvn", "-q", "test-compile", "-DskipTests"],
        cwd=REPO,
    )
    if compile.returncode != 0:
        print("Patch broke test sources; reverting test file", file=sys.stderr)
        subprocess.run(["git", "checkout", "--", str(TEST_JAVA.relative_to(REPO.parent))], cwd=REPO.parent, check=True)
        return 1
    print(f"Patched {len(tests)} tests", file=sys.stderr)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
