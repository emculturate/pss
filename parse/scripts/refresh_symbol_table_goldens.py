#!/usr/bin/env python3
"""Refresh Assert.assertEquals Symbol Table goldens from walker test stdout."""
from __future__ import annotations

import re
import subprocess
import sys
from pathlib import Path

PARSE_ROOT = Path(__file__).resolve().parents[1]
TEST_JAVA = {
    "SqlEventWalkerFunctionsAggregatesWindowingTests": PARSE_ROOT
    / "src/test/java/sql/walker/SqlEventWalkerFunctionsAggregatesWindowingTests.java",
    "SqlEventWalkerPivotUnpivotTests": PARSE_ROOT
    / "src/test/java/sql/walker/SqlEventWalkerPivotUnpivotTests.java",
}

SYMBOL_TABLE_ASSERT = re.compile(
    r'Assert\.assertEquals\("Symbol Table is wrong",\s*'
    r'(?:"([^"]*)"|(?:\n\s*"([^"]*)"))\s*,\s*'
    r'(?:\n\s*)?extractor\.getSymbolTable\(\)\.toString\(\)\);',
    re.MULTILINE,
)


def discover_failures(surefire_txt: Path) -> list[tuple[str, str]]:
    class_name = surefire_txt.name.replace("sql.walker.", "").replace(".txt", "")
    if not surefire_txt.exists():
        return []
    pairs: list[tuple[str, str]] = []
    for line in surefire_txt.read_text(encoding="utf-8").splitlines():
        if "<<< FAILURE!" not in line:
            continue
        m = re.search(r"\.(\w+) -- Time", line)
        if m:
            pairs.append((class_name, m.group(1)))
    return pairs


def run_symbol_tree(class_name: str, method: str) -> str:
    cmd = [
        "mvn",
        "-q",
        "-Dpss.walker.test.verbose=true",
        f"-Dtest={class_name}#{method}",
        "test",
    ]
    proc = subprocess.run(cmd, cwd=PARSE_ROOT, capture_output=True, text=True)
    combined = proc.stdout + proc.stderr
    for line in combined.splitlines():
        if line.startswith("Symbol Tree: "):
            return line[len("Symbol Tree: ") :]
    raise RuntimeError(f"No Symbol Tree for {class_name}#{method}")


def method_body_slice(content: str, method: str) -> tuple[int, int]:
    needle = f"public void {method}("
    start = content.find(needle)
    if start < 0:
        raise RuntimeError(f"Method not found: {method}")
    next_markers = [
        content.find("\n\t@Test", start + len(needle)),
        content.find("\n\tpublic void ", start + len(needle)),
    ]
    ends = [m for m in next_markers if m >= 0]
    end = min(ends) if ends else len(content)
    return start, end


def patch_method(content: str, method: str, new_golden: str) -> str:
    start, end = method_body_slice(content, method)
    body = content[start:end]
    match = SYMBOL_TABLE_ASSERT.search(body)
    if not match:
        raise RuntimeError(f"No Symbol Table assert in {method}")
    replacement = (
        'Assert.assertEquals("Symbol Table is wrong", "'
        + new_golden.replace("\\", "\\\\").replace('"', '\\"')
        + '", extractor.getSymbolTable().toString());'
    )
    new_body = body[: match.start()] + replacement + body[match.end() :]
    return content[:start] + new_body + content[end:]


def main() -> int:
    targets: list[tuple[str, str]] = []
    win_txt = (
        PARSE_ROOT
        / "target/surefire-reports/sql.walker.SqlEventWalkerFunctionsAggregatesWindowingTests.txt"
    )
    targets.extend(discover_failures(win_txt))
    # Pivot window 17.6.8 (b) goldens updated separately when needed.

    if not targets:
        print("No failure targets found; run failing tests first.", file=sys.stderr)
        return 1

    by_file: dict[Path, str] = {}
    for class_name, method in targets:
        path = TEST_JAVA[class_name]
        if path not in by_file:
            by_file[path] = path.read_text(encoding="utf-8")
        print(f"Refreshing {class_name}#{method} ...")
        golden = run_symbol_tree(class_name, method)
        by_file[path] = patch_method(by_file[path], method, golden)

    for path, content in by_file.items():
        path.write_text(content, encoding="utf-8")
        print(f"Wrote {path}")
    print(f"Updated {len(targets)} methods.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
