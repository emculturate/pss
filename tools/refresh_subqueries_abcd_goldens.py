#!/usr/bin/env python3
"""Refresh goldens for Categories A-D failures in SqlEventWalkerSubqueriesAndClauseSemanticsTests.

Category A: Table Dictionary drift (outer-scope alias bleed)
Category B: Table Dictionary drift (correlated subquery / HAVING materialization)
Category C: Symbol Table shape drift (dependent_queries refactor)
Category D: AST drift (query= sibling removed from lookup nodes)

Category E tests are intentionally excluded (real behavioral gaps).
"""

from __future__ import annotations

import re
import subprocess
import sys
from pathlib import Path

PARSE_DIR = Path(__file__).resolve().parent.parent / "parse"
TEST_ROOT = PARSE_DIR / "src/test/java/sql/walker"
TARGET = "SqlEventWalkerSubqueriesAndClauseSemanticsTests"
JAVA = TEST_ROOT / f"{TARGET}.java"

METHODS = [
    # A — table dictionary outer-scope bleed
    "selectWithBasicTest",
    "selectWithUnionTest",
    "unionAndQueryUnqualifiedReferencesCTEV4",
    "substitutionAndQueryUnqualifiedReferencesCTEV13",
    "intersectAndQueryUnqualifiedReferencesCTEV6",
    "intersectAndQueryQualifiedReferencesOutOfSequenceWithAliasesCTEV1",
    "sameTableDifferentSchemaUnqualifiedReferencesCTEV15",
    "sameTableDifferentSchemaQualifiedReferencesCTEV1",
    # B — correlated subquery / HAVING materialization
    "selectWhereExistsCorrelatedSubquery",
    "selectWhereScalarConditionCorrelatedSubquery",
    "selectOrderByScalarCorrelatedSubquery",
    "havingExistsCorrelatedSubqueryTest",
    # C — symbol table dependent_queries refactor
    "scalarSubqueriesCorrelatedSubquerySymbolTableTest",
    "havingScalarSubqueryComparisonTest",
    "nestedFormulaSubqueriesUseQueryRefsInInterfaceAndFiltersTest",
    "intersectWithSubqueryWithIntersectSubqueryTest",
    "intersectWithSubqueryWithUnionSubqueryTest",
    # D — AST query= sibling removed
    "multipleScalarAndOtherSubqueriesSymbolTableTest",
]

LABEL_TO_STDOUT = {
    "AST is wrong": "Result:",
    "Symbol Table is wrong": "Symbol Tree:",
    "Interface is wrong": "Interface:",
    "Table Dictionary is wrong": "Table Dictionary:",
    "Query Column Dictionary is wrong": "Query Column Dictionary:",
    "Substitution List is wrong": "Substitution Variables:",
}


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
    content = JAVA.read_text()
    updated: list[str] = []
    skipped: list[tuple[str, str]] = []

    for method in METHODS:
        code, output = run_test(method)
        if code == 0:
            print(f"PASS (already green) {method}")
            continue

        vals = capture(output)
        if not vals:
            skipped.append((method, "no stdout values captured"))
            continue

        changed = False
        for label in LABEL_TO_STDOUT:
            if label not in vals:
                continue
            content, ok = replace_assert(content, method, label, vals[label])
            if ok:
                print(f"UPDATE {method} [{label}]")
                changed = True

        if changed:
            updated.append(method)
        else:
            skipped.append((method, "no replace matched"))

    if updated:
        JAVA.write_text(content)

    print(f"\nUpdated {len(updated)} methods, skipped {len(skipped)}")

    still: list[str] = []
    for method in updated:
        code, _ = run_test(method)
        if code != 0:
            still.append(method)
    print(f"Still failing after refresh: {len(still)}")
    for method in still:
        print(f"  {method}")

    for method, reason in skipped:
        print(f"  SKIP {method}: {reason}")

    return 0 if not still else 1


if __name__ == "__main__":
    sys.exit(main())
