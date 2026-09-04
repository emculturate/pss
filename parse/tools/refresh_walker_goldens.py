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
import os
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

SNIPPET_LABELS = {
    "AST": ("AST is wrong", "getSqlAbstractTree"),
    "Interface": ("Interface is wrong", "getQueryInterface"),
    "Substitution": ("Substitution List is wrong", "getSubstitutionsMap"),
    "TableDictionary": ("Table Dictionary is wrong", "getTableDictionary"),
    "QueryDictionary": ("Query Column Dictionary is wrong", "getQueryColumnDictionaryMap"),
    "SymbolTable": ("Symbol Table is wrong", "getSymbolTable"),
}

ENDPOINT_CONSTANTS = {
    "SQLPARSER_PREDICAND_TREE_KEY": "PREDICAND",
    "SQLPARSER_JOIN_EXTENSION_TREE_KEY": "JOIN_EXTENSION",
    "SQLPARSER_INSERT_TREE_KEY": "INSERT",
    "SQLPARSER_UPDATE_TREE_KEY": "UPDATE",
    "SQLPARSER_DELETE_TREE_KEY": "DELETE",
    "SQLPARSER_TRUNCATE_TREE_KEY": "TRUNCATE",
    "SQLPARSER_IN_LIST_TREE_KEY": "IN_LIST",
    "SQLPARSER_CONDITION_TREE_KEY": "CONDITION",
    "SQLPARSER_COLUMN_TREE_KEY": "COLUMN",
    "SQLPARSER_VALUES_TREE_KEY": "VALUES",
    "SQLPARSER_TUPLE_TREE_KEY": "TUPLE",
    "SQLPARSER_DDL_TREE_KEY": "DDL",
    "SQLPARSER_SCRIPT_TREE_KEY": "SCRIPT",
    "SQLPARSER_SQL_TREE_KEY": "SQL",
    "SQLPARSER_QUERY_TREE_KEY": "QUERY",
    "SQLPARSER_LITERAL_TREE_KEY": "LITERAL",
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


def extract_access_object_sql(body: str) -> str | None:
    for var in ("query", "sql"):
        qm = re.search(
            rf"final String {var}\s*=\s*(.*?);\s*\n.*?run(?:Successful|FailedSyntax)SQLParserTest",
            body,
            re.DOTALL,
        )
        if qm:
            return eval_query_concat(qm.group(1).strip())
    return None


def extract_access_object_run(body: str) -> tuple[str, int | None] | None:
    failed = re.search(
        r"runFailedSyntaxSQLParserTest\([^,]+,\s*(SQLPARSER_\w+),\s*(\d+)\)",
        body,
    )
    if failed:
        const = failed.group(1)
        endpoint = ENDPOINT_CONSTANTS.get(const)
        if endpoint is None:
            return None
        return endpoint, int(failed.group(2))
    success = re.search(r"runSuccessfulSQLParserTest\([^,]+,\s*(SQLPARSER_\w+)\)", body)
    if success:
        const = success.group(1)
        endpoint = ENDPOINT_CONSTANTS.get(const)
        if endpoint is None:
            return None
        return endpoint, None
    return None


def extract_access_object_tests(
    java_text: str, methods_filter: set[str] | None
) -> dict[str, tuple[str, str, int | None]]:
    tests: dict[str, tuple[str, str, int | None]] = {}
    for name, start, end in list_method_blocks(java_text):
        if methods_filter is not None and name not in methods_filter:
            continue
        block = java_text[start:end]
        sql = extract_access_object_sql(block)
        run = extract_access_object_run(block)
        if sql is not None and run is not None:
            endpoint, fatal_errors = run
            tests[name] = (sql, endpoint, fatal_errors)
    return tests


def escape_properties_value(query: str) -> str:
    """Java Properties-safe value; preserve leading/trailing spaces via \\u0020."""
    escaped = query.replace("\\", "\\\\").replace("\n", "\\n").replace("\r", "")
    if escaped.startswith(" "):
        escaped = "\\u0020" + escaped[1:]
    if escaped.endswith(" "):
        escaped = escaped[:-1] + "\\u0020"
    return escaped


def write_properties(tests: dict[str, str], path: Path) -> None:
    lines: list[str] = []
    for name, query in sorted(tests.items()):
        lines.append(f"{name}={escape_properties_value(query)}\n")
    path.write_text("".join(lines), encoding="utf-8")


def write_access_object_properties(
    tests: dict[str, tuple[str, str, int | None]], path: Path
) -> None:
    lines: list[str] = []
    for name, (query, endpoint, fatal_errors) in sorted(tests.items()):
        lines.append(f"{name}={escape_properties_value(query)}\n")
        lines.append(f"{name}.endpoint={endpoint}\n")
        if fatal_errors is not None:
            lines.append(f"{name}.fatalErrors={fatal_errors}\n")
    path.write_text("".join(lines), encoding="utf-8")


def run_capture(props_path: Path, access_object: bool = False) -> dict[tuple[str, str], str]:
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
    java_cmd = ["java"]
    java_home = os.environ.get("JAVA_HOME")
    if java_home:
        java_cmd[0] = str(Path(java_home) / "bin" / "java")
    main_class = (
        "sql.walker.AccessObjectGoldenCaptureOnce"
        if access_object
        else "sql.walker.WalkerGoldenCaptureOnce"
    )
    out = subprocess.run(
        java_cmd + ["-cp", full_cp, main_class, str(props_path)],
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


def replace_expected_in_block(
    block: str, label: str, getter: str, expected: str, object_var: str = "extractor"
) -> str:
    marker = f'Assert.assertEquals("{label}",'
    idx = block.find(marker)
    if idx < 0:
        return block
    getter_marker = f"{object_var}.{getter}().toString()"
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
    block: str,
    method: str,
    goldens: dict[tuple[str, str], str],
    fields: set[str],
    labels: dict[str, tuple[str, str]],
    object_var: str = "extractor",
) -> str:
    for field in fields:
        label, getter = labels[field]
        key = (method, field)
        if key not in goldens:
            continue
        block = replace_expected_in_block(block, label, getter, goldens[key], object_var)
    return block


def patch_java(
    java_text: str,
    goldens: dict[tuple[str, str], str],
    methods: list[str],
    fields: set[str],
    labels: dict[str, tuple[str, str]] = LABELS,
    object_var: str = "extractor",
) -> str:
    name_to_span = {name: (start, end) for name, start, end in list_method_blocks(java_text)}
    for method in sorted(methods, key=lambda m: name_to_span.get(m, (0, 0))[0], reverse=True):
        span = name_to_span.get(method)
        if span is None:
            continue
        start, end = span
        block = java_text[start:end]
        java_text = (
            java_text[:start]
            + patch_method_block(block, method, goldens, fields, labels, object_var)
            + java_text[end:]
        )
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
    parser.add_argument(
        "--access-object",
        action="store_true",
        help="SqlParseEventWalkerWithAccessObjectTest style (snippet.get* goldens)",
    )
    args = parser.parse_args()

    test_java = resolve_test_java(args.test_java)
    labels = SNIPPET_LABELS if args.access_object else LABELS
    fields = {f.strip() for f in args.fields.split(",") if f.strip()}
    unknown = fields - set(labels)
    if unknown:
        print(f"Unknown fields: {sorted(unknown)}", file=sys.stderr)
        return 1

    java_text = test_java.read_text(encoding="utf-8")
    methods_filter = set(args.methods) if args.methods else None
    if args.access_object:
        access_tests = extract_access_object_tests(java_text, methods_filter)
        if not access_tests:
            print("No AccessObject tests with extractable SQL found", file=sys.stderr)
            return 1
        if methods_filter:
            missing = methods_filter - set(access_tests)
            if missing:
                print(f"Could not extract SQL for: {sorted(missing)}", file=sys.stderr)
                return 1
        test_names = list(access_tests.keys())
    else:
        walker_tests = extract_tests(java_text, methods_filter)
        if not walker_tests:
            print("No tests with extractable SQL found", file=sys.stderr)
            return 1
        if methods_filter:
            missing = methods_filter - set(walker_tests)
            if missing:
                print(f"Could not extract SQL for: {sorted(missing)}", file=sys.stderr)
                return 1
        test_names = list(walker_tests.keys())

    with tempfile.NamedTemporaryFile("w", suffix=".properties", delete=False) as tmp:
        props_path = Path(tmp.name)
    if args.access_object:
        write_access_object_properties(access_tests, props_path)
    else:
        write_properties(walker_tests, props_path)

    goldens = run_capture(props_path, access_object=args.access_object)
    props_path.unlink(missing_ok=True)

    if args.dry_run:
        for (method, field), value in sorted(goldens.items()):
            if field in fields and method in test_names:
                print(f"GOLDEN|{method}|{field}|{value[:120]}{'...' if len(value) > 120 else ''}")
        print(f"Would patch {len(test_names)} methods in {test_java.name}", file=sys.stderr)
        return 0

    object_var = "snippet" if args.access_object else "extractor"
    patched = patch_java(java_text, goldens, test_names, fields, labels, object_var)
    test_java.write_text(patched, encoding="utf-8")

    compile = subprocess.run(["mvn", "-q", "test-compile", "-DskipTests"], cwd=REPO)
    if compile.returncode != 0:
        print("Patch broke test sources; revert with git checkout", file=sys.stderr)
        return 1

    print(f"Patched {len(test_names)} methods in {test_java.name} fields={sorted(fields)}", file=sys.stderr)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
