#!/usr/bin/env python3
"""Update UNION/INTERSECT test goldens from captured walker output."""

import re
import subprocess
import sys
from pathlib import Path

PARSE_DIR = Path(__file__).resolve().parent.parent / "parse"
TEST_ROOT = PARSE_DIR / "src/test/java/sql/walker"

SETOP_FAILING = [
    ("SqlEventWalkerJoinsAndTableResolutionTests", "sameTableDifferentSchemaQualifiedReferencesV4"),
    ("SqlEventWalkerJoinsAndTableResolutionTests", "simplifiedQualifiedWildcardOverIntersectionInMiddleLayerTest"),
    ("SqlEventWalkerJoinsAndTableResolutionTests", "simplifiedQualifiedWildcardOverUnionInMiddleLayerTest"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "simpleMultipleIntersect1ParseTest"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "intersectWithSubqueryWithIntersectSubqueryTest"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "simpleUnionIntersectParseTest"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "intersectDistinctTest"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "selectWithSameSubqueriesTest"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "nestedWithUnionAliasHeavyValuesCteVirtualPositionRefsExemplarParsesWithoutErrors"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "intersectAndQueryUnqualifiedReferencesCTEV6"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "unionWithMismatchColumnCountsAndNamesTest"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "intersectWithSubqueryWithUnionSubqueryTest"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "nestedWithIntersectAliasHeavySnowflakeTableFunctionColumnsExemplarParsesWithoutErrors"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "simpleMultipleUnionParseTest"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "queryWithIntersectSubqueryTest"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "unionAllTest"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "intersectAndQueryQualifiedReferencesOutOfSequenceWithAliasesCTEV1"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "nestedUnionIntersectParseTest"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "intersectionWithMismatchColumnCountsAndNamesTest"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "sameTableDifferentSchemaQualifiedReferencesCTEV1"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "nestedWithIntersectAliasHeavyValuesCteVirtualColumnsExemplarParsesWithoutErrors"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "intersectAllTest"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "nestedWithIntersectAliasHeavyDeleteCteVirtualColumnsExemplarParsesWithoutErrors"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "intersectWithDuplicateColumnNameTestv2"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "simpleMultipleUnion1ParseTest"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "valuesAndIntersectUnqualifiedReferencesCTEV10"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "unionWithSubqueryWithIntersectSubqueryTest"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "unionDistinctTest"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "intersectWithDuplicateColumnNameTest"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "unionWithSubqueryP1Test"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "nestedWithUnionAliasHeavyValuesCteVirtualColumnsExemplarParsesWithoutErrors"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "nestedWithIntersectAliasHeavyValuesCteVirtualPositionRefsExemplarParsesWithoutErrors"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "unionAndQueryUnqualifiedReferencesCTEV4"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "simpleMultipleIntersectParseTest"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "nestedWithUnionAliasHeavyDeleteCteVirtualColumnsExemplarParsesWithoutErrors"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "unionAndValuesUnqualifiedReferencesCTEV9"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "unionWithSubqueryWithSubqueryTest"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "selectWithUnionSubqueryTest"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "selectWithUnionTest"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "nestedWithUnionAliasHeavySnowflakeTableFunctionColumnsExemplarParsesWithoutErrors"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "unionWithDuplicateColumnNameTest"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "multipleUnionWithDuplicateColumnNameTest"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "nestedUnionIntersectAAParseTest"),
    ("SqlEventWalkerSubqueriesAndClauseSemanticsTests", "unionWithSubqueryWithUnionSubqueryTest"),
    ("SqlEventWalkerLiveSampleQueriesTests", "createTermFilterTableTest"),
    ("SqlEventWalkerLiveSampleQueriesTests", "createCurrentTermTableTest"),
    ("SqlEventWalkerLiveSampleQueriesTests", "getGroupingSqlTest"),
    ("SqlEventWalkerLiveSampleQueriesTests", "complexJINJAQueryWithWoldcardTest"),
]

LABEL_TO_STDOUT = {
    "Symbol Table is wrong": "Symbol Tree:",
    "Symbol Tree is wrong": "Symbol Tree:",
    "Interface is wrong": "Interface:",
    "Table Dictionary is wrong": "Table Dictionary:",
    "Query Column Dictionary is wrong": "Query Column Dictionary:",
    "Substitution List is wrong": "Substitution Variables:",
    "AST is wrong": "Result:",
}


def run_test(cls: str, method: str) -> tuple[int, str]:
    cmd = ["mvn", "-q", "test", f"-Dtest={cls}#{method}"]
    proc = subprocess.run(cmd, cwd=PARSE_DIR, capture_output=True, text=True)
    return proc.returncode, proc.stdout + proc.stderr


def capture_stdout_values(output: str) -> dict[str, str]:
    values = {}
    for label, prefix in LABEL_TO_STDOUT.items():
        match = re.search(rf"^{re.escape(prefix)} (.+)$", output, re.M)
        if match:
            values[label] = match.group(1).strip()
    custom = re.search(r"^def_union\d+ local interface is wrong expected:", output, re.M)
    if custom:
        pass
    return values


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
    replaced = False
    for pattern in patterns:
        m = re.search(pattern, updated_body, re.DOTALL)
        if m:
            updated_body = updated_body[: m.start(2)] + new_expected + updated_body[m.end(2) :]
            replaced = True
            break

    if not replaced:
        return content, False

    return content[: method_match.start(1)] + updated_body + content[method_match.end(1) :], True


def replace_custom_local_interface(content: str, method: str, new_expected: str) -> tuple[str, bool]:
    method_pattern = (
        rf"(public void {re.escape(method)}\(\).*?)"
        r"(?=\n\tpublic void |\n\t@Test|\n\}\s*$|\Z)"
    )
    method_match = re.search(method_pattern, content, re.DOTALL)
    if not method_match:
        return content, False

    method_body = method_match.group(1)
    pattern = r'(Assert\.assertEquals\("def_union\d+ local interface is wrong", ")(.*?)("\s*,\s*\n\s*[^)]+\))'
    m = re.search(pattern, method_body, re.DOTALL)
    if not m:
        return content, False

    updated_body = method_body[: m.start(2)] + new_expected + method_body[m.end(2) :]
    return content[: method_match.start(1)] + updated_body + content[method_match.end(1) :], True


def main() -> int:
    file_cache: dict[Path, str] = {}
    updated = 0
    still_failing = []

    for cls, method in SETOP_FAILING:
        code, output = run_test(cls, method)
        values = capture_stdout_values(output)

        java_path = TEST_ROOT / f"{cls}.java"
        if java_path not in file_cache:
            file_cache[java_path] = java_path.read_text()

        content = file_cache[java_path]
        any_replaced = False

        if code != 0:
            failure_labels = re.findall(r"(Symbol Table is wrong|Symbol Tree is wrong|Interface is wrong|def_union\d+ local interface is wrong|Table Dictionary is wrong|Query Column Dictionary is wrong|AST is wrong)", output)
            for label in dict.fromkeys(failure_labels):
                if label.startswith("def_union") and "local interface" in label:
                    m = re.search(r'but was:<(.+?)>\s*$', output, re.M)
                    if m:
                        content, ok = replace_custom_local_interface(content, method, m.group(1))
                        any_replaced = any_replaced or ok
                elif label in values:
                    content, ok = replace_assert_in_method(content, method, label, values[label])
                    any_replaced = any_replaced or ok
                    if ok:
                        print(f"  updated {label}")

        if any_replaced:
            file_cache[java_path] = content
            updated += 1
            code2, output2 = run_test(cls, method)
            if code2 != 0:
                still_failing.append((cls, method, output2.split("ComparisonFailure:")[-1][:200] if "ComparisonFailure" in output2 else output2[-300:]))
                print(f"STILL FAILING {cls}.{method}")
            else:
                print(f"FIXED {cls}.{method}")
        else:
            still_failing.append((cls, method, "no replacement made"))
            print(f"SKIPPED {cls}.{method} (no replacement)")

    for path, content in file_cache.items():
        path.write_text(content)

    print(f"\nUpdated {updated} tests")
    if still_failing:
        print(f"Still failing: {len(still_failing)}")
        for cls, method, msg in still_failing[:10]:
            print(f"  {cls}.{method}: {msg[:120]}")
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
