#!/usr/bin/env python3
"""Refresh SqlEventWalker* golden expected strings from live walker output.

Uses WalkerGoldenCaptureOnce (full strings, no surefire truncation) and patches
Java sources with escape-aware string replacement — safe for very large goldens.

Examples:
  # All tests in a class (Query Column Dictionary only — typical JDK 21 map-order refresh):
  python3 parse/tools/refresh_walker_goldens.py \\
      sql/walker/SqlEventWalkerJoinsAndTableResolutionTests.java --fields QueryDictionary

  # Named methods, all assertion fields:
  python3 parse/tools/refresh_walker_goldens.py \\
      sql/walker/SqlEventWalkerSetOpScopingGateTests.java \\
      gateSetOpUnionInterfaceMismatchFatalV0Test

  # Dry run (print capture lines, do not patch):
  python3 parse/tools/refresh_walker_goldens.py path/to/Test.java --dry-run
"""
from __future__ import annotations

import argparse
import re
import subprocess
import sys
import tempfile
from pathlib import Path

REPO = Path(__file__).resolve().parents[1]
TEST_JAVA_ROOT = REPO / "src/test/java"

LABELS = {
    "AST": ("AST is wrong", "getAsTree"),
    "Interface": ("Interface is wrong", "getInterface"),
    "Substitution": ("Substitution List is wrong", "getSubstitutionsMap"),
    "TableDictionary": ("Table Dictionary is wrong", "getTableColumnDictionaryMap"),
    "QueryDictionary": ("Query Column Dictionary is wrong", "getQueryColumnDictionaryMap"),
    "SymbolTable": ("Symbol Table is wrong", "getSymbolTable"),
}


def eval_query_concat(expr: str) -> str:
    parts = re.findall(r'"(?:\\.|[^"\\])*"', expr)
    out: list[str] = []
    for p in parts:
        s = p[1:-1]
        s = s.replace("\\n", "\n").replace("\\t", "\t").replace("\\'", "'").replace('\\"', '"')
        out.append(s)
    return "".join(out)


def extract_sql(body: str) -> str | None:
    for var in ("query", "sql"):
        qm = re.search(
            rf"final String {var}\s*=\s*(.*?);\s*\n\s*(?:final SQLSelectParserParser|SQLSelectParserParser)",
            body,
            re.DOTALL,
        )
        if qm:
            return eval_query_concat(qm.group(1).strip())
    return None


def list_method_blocks(java_text: str) -> list[tuple[str, int, int]]:
    positions = [(m.start(), m.group(1)) for m in re.finditer(r"public void (\w+)\(\)", java_text)]
    blocks: list[tuple[str, int, int]] = []
    for i, (start, name) in enumerate(positions):
        end = positions[i + 1][0] if i + 1 < len(positions) else len(java_text)
        blocks.append((name, start, end))
    return blocks


def extract_tests(java_text: str, methods_filter: set[str] | None) -> dict[str, str]:
    tests: dict[str, str] = {}
    for name, start, end in list_method_blocks(java_text):
        if methods_filter is not None and name not in methods_filter:
            continue
        sql = extract_sql(java_text[start:end])
        if sql is not None:
            tests[name] = sql
    return tests


def write_properties(tests: dict[str, str], path: Path) -> None:
    lines: list[str] = []
    for name, query in sorted(tests.items()):
        escaped = query.replace("\\", "\\\\").replace("\n", "\\n").replace("\r", "")
        lines.append(f"{name}={escaped}\n")
    path.write_text("".join(lines), encoding="utf-8")


def run_capture(props_path: Path) -> dict[tuple[str, str], str]:
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
        ["java", "-cp", full_cp, "sql.walker.WalkerGoldenCaptureOnce", str(props_path)],
        cwd=REPO,
        text=True,
        capture_output=True,
    )
    combined = out.stdout + out.stderr
    if out.returncode != 0 and "GOLDEN|" not in combined:
        out.check_returncode()
    goldens: dict[tuple[str, str], str] = {}
    for line in combined.splitlines():
        if not line.startswith("GOLDEN|"):
            continue
        _, method, field, value = line.split("|", 3)
        goldens[(method, field)] = value.replace("\\n", "\n")
    return goldens


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


def patch_method_block(
    block: str, method: str, goldens: dict[tuple[str, str], str], fields: set[str]
) -> str:
    for field in fields:
        label, getter = LABELS[field]
        key = (method, field)
        if key not in goldens:
            continue
        block = replace_expected_in_block(block, label, getter, goldens[key])
    return block


def patch_java(
    java_text: str,
    goldens: dict[tuple[str, str], str],
    methods: list[str],
    fields: set[str],
) -> str:
    name_to_span = {name: (start, end) for name, start, end in list_method_blocks(java_text)}
    for method in sorted(methods, key=lambda m: name_to_span.get(m, (0, 0))[0], reverse=True):
        span = name_to_span.get(method)
        if span is None:
            continue
        start, end = span
        block = java_text[start:end]
        java_text = java_text[:start] + patch_method_block(block, method, goldens, fields) + java_text[end:]
    return java_text


def resolve_test_java(arg: str) -> Path:
    p = Path(arg)
    if p.is_file():
        return p.resolve()
    candidate = TEST_JAVA_ROOT / arg
    if candidate.is_file():
        return candidate.resolve()
    raise FileNotFoundError(f"Test source not found: {arg}")


def main() -> int:
    parser = argparse.ArgumentParser(description="Refresh walker test goldens via WalkerGoldenCaptureOnce")
    parser.add_argument("test_java", help="Path to test class, e.g. sql/walker/FooTests.java")
    parser.add_argument("methods", nargs="*", help="Optional test method names (default: all with SQL)")
    parser.add_argument(
        "--fields",
        default="QueryDictionary",
        help="Comma-separated capture fields to patch (default: QueryDictionary). "
        "Choices: " + ", ".join(LABELS),
    )
    parser.add_argument("--dry-run", action="store_true", help="Capture only; print GOLDEN lines, do not patch")
    args = parser.parse_args()

    test_java = resolve_test_java(args.test_java)
    fields = {f.strip() for f in args.fields.split(",") if f.strip()}
    unknown = fields - set(LABELS)
    if unknown:
        print(f"Unknown fields: {sorted(unknown)}", file=sys.stderr)
        return 1

    java_text = test_java.read_text(encoding="utf-8")
    methods_filter = set(args.methods) if args.methods else None
    tests = extract_tests(java_text, methods_filter)
    if not tests:
        print("No tests with extractable SQL found", file=sys.stderr)
        return 1
    if methods_filter:
        missing = methods_filter - set(tests)
        if missing:
            print(f"Could not extract SQL for: {sorted(missing)}", file=sys.stderr)
            return 1

    with tempfile.NamedTemporaryFile("w", suffix=".properties", delete=False) as tmp:
        props_path = Path(tmp.name)
    write_properties(tests, props_path)

    goldens = run_capture(props_path)
    props_path.unlink(missing_ok=True)

    if args.dry_run:
        for (method, field), value in sorted(goldens.items()):
            if field in fields and method in tests:
                print(f"GOLDEN|{method}|{field}|{value[:120]}{'...' if len(value) > 120 else ''}")
        print(f"Would patch {len(tests)} methods in {test_java.name}", file=sys.stderr)
        return 0

    patched = patch_java(java_text, goldens, list(tests.keys()), fields)
    test_java.write_text(patched, encoding="utf-8")

    compile = subprocess.run(["mvn", "-q", "test-compile", "-DskipTests"], cwd=REPO)
    if compile.returncode != 0:
        print("Patch broke test sources; revert with git checkout", file=sys.stderr)
        return 1

    print(f"Patched {len(tests)} methods in {test_java.name} fields={sorted(fields)}", file=sys.stderr)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
