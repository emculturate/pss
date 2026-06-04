#!/usr/bin/env python3
"""
Remove private helper methods from SqlParseEventWalker that already exist in
SqlParseSymbolTreeHelper, redirecting remaining call sites to symbolTreeHelper.

Also syncs helper copies when the walker version differs (walker is canonical).
"""

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
WALKER = ROOT / "parse/src/main/java/sql/walker/SqlParseEventWalker.java"
HELPER = ROOT / "parse/src/main/java/sql/symboltree/SqlParseSymbolTreeHelper.java"

KEEP_IN_WALKER = {
    "extractUnpivotInListColumnNames",
    "extractUnpivotValueColumnName",
    "buildUnpivotInterfaceHints",
    "normalizeTableRef",
}

METHOD_START = re.compile(
    r"^(\t)(?:private|public|protected)\s+(?:static\s+)?(?:final\s+)?(?:synchronized\s+)?"
    r"(?:<[^(]+>\s+)?"
    r"[\w$]+(?:\s*<[^(>]+>)?(?:\[\])*\s+"
    r"([\w$]+)\s*\("
)


def count_braces(code: str) -> int:
    delta = 0
    in_str = in_char = escape = False
    for ch in code:
        if escape:
            escape = False
            continue
        if ch == "\\" and (in_str or in_char):
            escape = True
            continue
        if ch == '"' and not in_char:
            in_str = not in_str
        elif ch == "'" and not in_str:
            in_char = not in_char
        elif not in_str and not in_char:
            if ch == "{":
                delta += 1
            elif ch == "}":
                delta -= 1
    return delta


def code_part(line: str) -> str:
    return line.split("//", 1)[0] if "//" in line else line


def find_annotation_start(lines, method_line_idx):
    j = method_line_idx - 1
    start = method_line_idx
    found_annot = False
    while j >= 0:
        prev = lines[j].strip()
        if prev == "":
            j -= 1
            continue
        if prev.startswith("@") or prev.startswith("/**") or prev.startswith("/*") or prev.startswith("*"):
            start = j
            found_annot = True
            j -= 1
        else:
            if found_annot:
                break
            if prev.endswith(",") or prev.endswith("("):
                start = j
                j -= 1
            else:
                break
    j2 = start - 1
    while j2 >= 0:
        p = lines[j2].strip()
        if p == "":
            j2 -= 1
            continue
        if p.startswith("/**") or p.startswith("/*") or p.startswith("*"):
            start = j2
            j2 -= 1
        else:
            break
    return start


def extract_tab_indented_methods(lines):
    """Extract one-tab-indented methods (class members, not nested types)."""
    results = {}
    n = len(lines)
    i = 0
    while i < n:
        line = lines[i]
        m = METHOD_START.match(line)
        if not m:
            i += 1
            continue
        name = m.group(2)
        sig_start = i
        # multi-line signature until '{'
        j = i
        depth = 0
        found_brace = False
        while j < n:
            cp = code_part(lines[j])
            if "{" in cp:
                found_brace = True
                depth += count_braces(cp)
                break
            j += 1
        if not found_brace:
            i += 1
            continue
        while j + 1 < n and depth > 0:
            j += 1
            depth += count_braces(code_part(lines[j]))
        annot_start = find_annotation_start(lines, sig_start)
        results[name] = (annot_start, j, lines[annot_start : j + 1])
        i = j + 1
    return results


def normalize_body(body_lines):
    text = "".join(body_lines)
    text = re.sub(r"^\t(?:private|public|protected)\s+", "\t", text, count=1)
    return text


def to_public_method(body_lines):
    out = []
    replaced = False
    for line in body_lines:
        if not replaced:
            m = re.match(r"(\t)(private|protected)\s+", line)
            if m:
                out.append(f"{m.group(1)}public {line[m.end():]}")
                replaced = True
                continue
        out.append(line)
    return out


def sync_differing_methods(walker_methods, helper_methods, helper_lines):
    to_sync = []
    for name in sorted(set(walker_methods) & set(helper_methods)):
        if name in KEEP_IN_WALKER:
            continue
        w_body = normalize_body(walker_methods[name][2])
        h_body = normalize_body(helper_methods[name][2])
        if w_body != h_body:
            to_sync.append(name)
    if not to_sync:
        return helper_lines, []
    print(f"Syncing {len(to_sync)} differing methods from walker -> helper:")
    remove_idxs = set()
    insertions = []
    for name in to_sync:
        hs, he, _ = helper_methods[name]
        for idx in range(hs, he + 1):
            remove_idxs.add(idx)
        new_body = to_public_method(walker_methods[name][2])
        insertions.append((hs, new_body))
        print(f"  {name} (helper lines {hs+1}-{he+1})")
    remaining = [ln for idx, ln in enumerate(helper_lines) if idx not in remove_idxs]
    for hs, new_body in sorted(insertions, key=lambda x: x[0], reverse=True):
        remaining[hs:hs] = new_body
    return remaining, to_sync


def replace_calls(lines, method_names):
    alt = "|".join(re.escape(m) for m in sorted(method_names, key=len, reverse=True))
    call_re = re.compile(r"(?<![.\w])(" + alt + r")\s*\(")
    out = []
    for line in lines:
        stripped = line.lstrip()
        if stripped.startswith("//") or stripped.startswith("*") or stripped.startswith("/*"):
            out.append(line)
            continue
        if METHOD_START.match(line):
            out.append(line)
            continue
        out.append(call_re.sub(r"symbolTreeHelper.\1(", line))
    return out


def remove_walker_methods(walker_lines, remove_names, walker_methods):
    remove_idxs = set()
    for name in remove_names:
        if name not in walker_methods:
            continue
        hs, he, _ = walker_methods[name]
        for idx in range(hs, he + 1):
            remove_idxs.add(idx)
    remaining = [ln for idx, ln in enumerate(walker_lines) if idx not in remove_idxs]
    blank_run = 0
    collapsed = []
    for ln in remaining:
        if ln.strip() == "":
            blank_run += 1
            if blank_run <= 2:
                collapsed.append(ln)
        else:
            blank_run = 0
            collapsed.append(ln)
    return collapsed


def main():
    walker_text = WALKER.read_text(encoding="utf-8")
    helper_text = HELPER.read_text(encoding="utf-8")
    walker_lines = walker_text.splitlines(keepends=True)
    helper_lines = helper_text.splitlines(keepends=True)

    walker_methods = extract_tab_indented_methods(walker_lines)
    helper_methods = extract_tab_indented_methods(helper_lines)

    walker_private_dupes = {
        name
        for name, (_, _, body) in walker_methods.items()
        if name in helper_methods
        and name not in KEEP_IN_WALKER
        and body[0].lstrip().startswith("private")
    }

    print(f"Walker methods: {len(walker_methods)}, helper methods: {len(helper_methods)}")
    print(f"Walker private methods duplicated in helper: {len(walker_private_dupes)}")

    helper_lines, synced = sync_differing_methods(walker_methods, helper_methods, helper_lines)
    if synced:
        HELPER.write_text("".join(helper_lines), encoding="utf-8")
        helper_methods = extract_tab_indented_methods(helper_lines)

    walker_lines = replace_calls(walker_lines, walker_private_dupes)
    walker_lines = remove_walker_methods(walker_lines, walker_private_dupes, walker_methods)
    WALKER.write_text("".join(walker_lines), encoding="utf-8")

    print(f"Removed {len(walker_private_dupes)} duplicate methods from walker")
    print(f"Walker lines: {len(walker_text.splitlines())} -> {len(walker_lines)}")
    print(f"Helper lines: {len(helper_text.splitlines())} -> {len(helper_lines)}")


if __name__ == "__main__":
    main()
