#!/usr/bin/env python3
"""Clone @Test methods that exercise SQL INTERSECT set-ops into EXCEPT variants (13.1.1b)."""

from __future__ import annotations

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
TEST_ROOT = ROOT / "src/test/java/sql/walker"
TEST_FILES = sorted(TEST_ROOT.glob("*Tests.java"))
TEST_FILES.append(ROOT / "src/test/java/sql/walker/SqlParseEventWalkerWithAccessObjectTest.java")

METHOD_RE = re.compile(r"(@Test\s+public\s+void\s+(\w+)\s*\(\s*\)\s*\{)", re.MULTILINE)
INTERSECT_SIGNAL_RE = re.compile(
    r"""
    \bintersect\s+(?:all|distinct)\b |
    \bINTERSECT\s+(?:ALL|DISTINCT)\b |
    (?<!union\s)\bintersect\b(?!\s*join) |
    (?<!union\s)\bINTERSECT\b(?!\s*JOIN) |
    operator=intersect\b |
    operator=INTERSECT\b |
    setop=INTERSECTION\b |
    INTERSECTION\ has\ different\ column\ counts
    """,
    re.IGNORECASE | re.VERBOSE,
)
def normalize_query_text(query: str) -> str:
    return query.replace("\\n", "\n").replace("\\t", "\t").replace("\\r", "\r")


def query_has_except(query: str) -> bool:
    return bool(re.search(r"\bexcept\b", normalize_query_text(query), re.IGNORECASE))


def extract_method_body(text: str, open_brace_index: int) -> tuple[str, int]:
    depth = 0
    i = open_brace_index
    in_string = False
    escape = False
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
                return text[open_brace_index : i + 1], i + 1
        i += 1
    raise ValueError("Unbalanced braces in method body")


def extract_query_literal(body: str) -> str:
    match = re.search(
        r"(?:final\s+)?String\s+(?:query|sql)\s*=\s*",
        body,
    )
    if not match:
        parts = re.findall(r'(?:sql\s*\+=\s*)?"((?:\\.|[^"\\])*)"', body)
        return "".join(parts)

    tail = body[match.end() :]
    semi = tail.find(";")
    if semi < 0:
        return ""
    expr = tail[:semi]
    parts = re.findall(r'"((?:\\.|[^"\\])*)"', expr)
    var_match = re.match(r"(?:final\s+)?String\s+(query|sql)\s*=", body[match.start() :])
    var_name = var_match.group(1) if var_match else "query"
    for add_match in re.finditer(
        rf"{var_name}\s*\+=\s*((?:(?:\"(?:\\.|[^\"\\])*\")(?:\s*\+\s*)?)+);",
        body,
        re.DOTALL,
    ):
        parts.extend(re.findall(r'"((?:\\.|[^"\\])*)"', add_match.group(1)))
    return "".join(parts)


def clone_method_name(original: str, existing: set[str]) -> str | None:
    if "IntersectExcept" in original or "IntersectionExcept" in original:
        return None

    candidates = [
        re.sub("Intersection", "Except", original),
        re.sub("Intersect", "Except", original),
        original.replace("Intersection", "IntersectionExcept", 1),
        original.replace("Intersect", "IntersectExcept", 1),
        original + "IntersectAsExcept",
    ]
    seen: set[str] = set()
    for candidate in candidates:
        if candidate in seen:
            continue
        seen.add(candidate)
        if candidate != original and candidate not in existing:
            return candidate
    return None


def transform_sql_literal(sql_fragment: str) -> str:
    def repl_intersect(match: re.Match[str]) -> str:
        token = match.group(0)
        if token.isupper():
            return "EXCEPT"
        if token[0].isupper():
            return "Except"
        return "except"

    value = re.sub(r"\bintersect\s+all\b", repl_intersect, sql_fragment, flags=re.IGNORECASE)
    value = re.sub(r"\bintersect\s+distinct\b", repl_intersect, value, flags=re.IGNORECASE)
    value = re.sub(r"(?<!union\s)\bintersect\b(?!\s*join)", repl_intersect, value, flags=re.IGNORECASE)
    return value


def transform_java_string_literals(body: str) -> str:
    def repl_string(match: re.Match[str]) -> str:
        quote = match.group(1)
        content = match.group(2)
        if not re.search(r"\bintersect\b", content, re.IGNORECASE):
            return match.group(0)
        transformed = transform_sql_literal(content)
        return quote + transformed + quote

    return re.sub(r'(")((?:\\.|[^"\\])*)(")', repl_string, body)


def clone_method_block(body: str, clone_name: str) -> str:
    cloned = transform_java_string_literals(body)
    if cloned.startswith("{"):
        return f"public void {clone_name}() {cloned}"
    return re.sub(
        r"public void \w+\s*\(\s*\)\s*\{",
        f"public void {clone_name}() {{",
        cloned,
        count=1,
    )


def process_file(path: Path) -> list[str]:
    text = path.read_text()
    existing_methods = {match.group(2) for match in METHOD_RE.finditer(text)}
    clones: list[tuple[str, str, str]] = []

    for match in list(METHOD_RE.finditer(text)):
        method_name = match.group(2)
        open_brace = text.find("{", match.end() - 1)
        body, end_index = extract_method_body(text, open_brace)
        if not INTERSECT_SIGNAL_RE.search(body):
            continue
        query = extract_query_literal(body)
        if query and query_has_except(query):
            continue
        clone_name = clone_method_name(method_name, existing_methods)
        if clone_name is None:
            print(f"SKIP naming {path.name}::{method_name}")
            continue
        if clone_name in existing_methods:
            continue
        cloned_body = clone_method_block(body, clone_name)
        clones.append((method_name, clone_name, cloned_body))
        existing_methods.add(clone_name)

    if not clones:
        return []

    updated = text
    for original_name, clone_name, cloned_body in reversed(clones):
        original_match = re.search(
            rf"@Test\s+public\s+void\s+{re.escape(original_name)}\s*\(\s*\)\s*\{{",
            updated,
        )
        if not original_match:
            continue
        open_brace = updated.find("{", original_match.end() - 1)
        _, end_index = extract_method_body(updated, open_brace)
        insertion = "\n\n\t@Test\n\t" + cloned_body.strip() + "\n"
        updated = updated[:end_index] + insertion + updated[end_index:]
        print(f"CLONED {path.name} {original_name} -> {clone_name}")

    path.write_text(updated)
    return [clone_name for _, clone_name, _ in clones]


def main() -> int:
    all_cloned: list[str] = []
    for path in TEST_FILES:
        if not path.exists():
            continue
        all_cloned.extend(process_file(path))
    print(f"Total clones added: {len(all_cloned)}")
    list_path = ROOT / "tools/intersect_except_clone_methods.txt"
    list_path.write_text("\n".join(all_cloned) + ("\n" if all_cloned else ""))
    print(f"Wrote {list_path}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
