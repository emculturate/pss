#!/usr/bin/env python3
"""Generate correlated subquery diagnostic tests with captured golden output."""

import re
import subprocess
import sys
from pathlib import Path

from correlated_subquery_test_queries import all_tests_with_sql

PARSE_DIR = Path(__file__).resolve().parent.parent / "parse"
CAPTURE_JAVA = (
    PARSE_DIR / "src/test/java/sql/walker/CorrelatedSubqueryGoldenCapture.java"
)
TARGET_JAVA = (
    PARSE_DIR / "src/test/java/sql/walker/SqlEventWalkerCoreSelectFromAliasingTests.java"
)

SECTION_MARKER = "\t// Correlated subquery diagnostic tests (predicand / IN / EXISTS)"

LABELS = [
    ("AST is wrong", "Result:"),
    ("Interface is wrong", "Interface:"),
    ("Substitution List is wrong", "Substitution Variables:"),
    ("Table Dictionary is wrong", "Table Dictionary:"),
    ("Query Column Dictionary is wrong", "Query Column Dictionary:"),
    ("Symbol Table is wrong", "Symbol Tree:"),
]


def java_escape(s: str) -> str:
    return s.replace("\\", "\\\\").replace('"', '\\"')


def format_java_query(lines: list[str], indent: str = "\t\t") -> str:
    """Emit a multi-line Java string concatenation for the SQL query."""
    if not lines:
        return f'{indent}final String query = "";'

    parts = [f'"{java_escape(lines[0])}"']
    for line in lines[1:]:
        parts.append(f'"\\n{java_escape(line)}"')

    if len(parts) == 1:
        return f"{indent}final String query = {parts[0]};"

    out = [f"{indent}final String query = {parts[0]}"]
    for part in parts[1:-1]:
        out.append(f"{indent}    + {part}")
    out.append(f"{indent}    + {parts[-1]};")
    return "\n".join(out)


def java_string_literal(s: str) -> str:
    return '"' + java_escape(s).replace("\n", "\\n") + '"'


def build_capture_class() -> str:
    lines = [
        "package sql.walker;",
        "",
        "import org.junit.Test;",
        "import sql.SQLSelectParserParser;",
        "",
        "public class CorrelatedSubqueryGoldenCapture extends AbstractSqlParseEventWalkerTest {",
        "",
    ]
    for method, sql, _line_chunks in all_tests_with_sql():
        lines.extend(
            [
                "\t@Test",
                f"\tpublic void {method}() {{",
                f'\t\tfinal String query = {java_string_literal(sql)};',
                "\t\tfinal SQLSelectParserParser parser = parse(query);",
                "\t\tSqlParseEventWalker extractor = runParsertest(query, parser);",
                f'\t\tSystem.out.println("===METHOD:{method}===");',
                '\t\tSystem.out.println("Result: " + extractor.getAsTree());',
                '\t\tSystem.out.println("Interface: " + extractor.getInterface());',
                '\t\tSystem.out.println("Substitution Variables: " + extractor.getSubstitutionsMap());',
                '\t\tSystem.out.println("Table Dictionary: " + extractor.getTableColumnDictionaryMap());',
                '\t\tSystem.out.println("Query Column Dictionary: " + extractor.getQueryColumnDictionaryMap());',
                '\t\tSystem.out.println("Symbol Tree: " + extractor.getSymbolTable());',
                "\t}",
                "",
            ]
        )
    lines.append("}")
    lines.append("")
    return "\n".join(lines)


def run_capture() -> str:
    CAPTURE_JAVA.write_text(build_capture_class())
    proc = subprocess.run(
        ["mvn", "-q", "test", "-Dtest=CorrelatedSubqueryGoldenCapture"],
        cwd=PARSE_DIR,
        capture_output=True,
        text=True,
    )
    output = proc.stdout + proc.stderr
    if proc.returncode != 0 and "===METHOD:" not in output:
        print(output[-4000:], file=sys.stderr)
        raise SystemExit(f"Capture run failed with code {proc.returncode}")
    return output


def parse_capture_output(output: str) -> dict[str, dict[str, str]]:
    blocks: dict[str, dict[str, str]] = {}
    for chunk in output.split("===METHOD:")[1:]:
        method, _, rest = chunk.partition("===")
        method = method.strip()
        values: dict[str, str] = {}
        for label, prefix in LABELS:
            match = re.search(rf"^{re.escape(prefix)} (.+)$", rest, re.M)
            if match:
                values[label] = match.group(1).strip()
        blocks[method] = values
    return blocks


def build_test_method(
    method: str, line_chunks: list[str], goldens: dict[str, str]
) -> str:
    lines = [
        "",
        "\t@Test",
        f"\tpublic void {method}() {{",
        format_java_query(line_chunks),
        "",
        "\t\tfinal SQLSelectParserParser parser = parse(query);",
        "\t\tSqlParseEventWalker extractor = runParsertest(query, parser);",
        "\t\tassertNoWalkerDiagnostics(extractor);",
        "\t\tassertNoFatalErrors(extractor);",
        "",
    ]
    for label, _ in LABELS:
        expected = goldens.get(label, "")
        lines.append(f'\t\tAssert.assertEquals("{label}", "{java_escape(expected)}",')
        if label == "AST is wrong":
            lines.append("\t\t\t\textractor.getAsTree().toString());")
        elif label == "Interface is wrong":
            lines.append("\t\t\t\textractor.getInterface().toString());")
        elif label == "Substitution List is wrong":
            lines.append("\t\t\t\textractor.getSubstitutionsMap().toString());")
        elif label == "Table Dictionary is wrong":
            lines.append("\t\t\t\textractor.getTableColumnDictionaryMap().toString());")
        elif label == "Query Column Dictionary is wrong":
            lines.append("\t\t\t\textractor.getQueryColumnDictionaryMap().toString());")
        elif label == "Symbol Table is wrong":
            lines.append("\t\t\t\textractor.getSymbolTable().toString());")
    lines.append("\t}")
    return "\n".join(lines)


def replace_correlated_section(blocks: dict[str, dict[str, str]]) -> None:
    content = TARGET_JAVA.read_text()
    marker_pos = content.find(SECTION_MARKER)
    if marker_pos == -1:
        raise SystemExit("Correlated subquery section marker not found")

    prefix = content[: marker_pos].rstrip() + "\n\n"
    section_text = (
        "\t// -------------------------------------------------------------------------\n"
        "\t// Correlated subquery diagnostic tests (predicand / IN / EXISTS)\n"
        "\t// Compare correlation handling consistency across subquery kinds.\n"
        "\t// -------------------------------------------------------------------------"
    )
    parts = [prefix, section_text]
    for method, _sql, line_chunks in all_tests_with_sql():
        goldens = blocks.get(method)
        if not goldens or len(goldens) < len(LABELS):
            missing = [
                m
                for m, _, _ in all_tests_with_sql()
                if m not in blocks or len(blocks[m]) < len(LABELS)
            ]
            raise SystemExit(f"Missing golden output for: {missing[:5]}")
        parts.append(build_test_method(method, line_chunks, goldens))
    parts.append("\n}\n")
    TARGET_JAVA.write_text("".join(parts))


def main() -> int:
    print(f"Capturing goldens for {len(all_tests_with_sql())} queries...")
    output = run_capture()
    blocks = parse_capture_output(output)
    print(f"Captured {len(blocks)} method blocks")
    replace_correlated_section(blocks)
    print(f"Updated correlated section in {TARGET_JAVA.name}")
    CAPTURE_JAVA.unlink(missing_ok=True)
    return 0


if __name__ == "__main__":
    sys.exit(main())
