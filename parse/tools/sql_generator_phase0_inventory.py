#!/usr/bin/env python3
"""Regenerate Phase 0 SQL generator inventory: grammar rule → walker AST → emit.

Reads:
  - Generated ANTLR parser (RULE_* constants)
  - SqlParseEventWalker exit* bodies (primary mumble / map keys)
  - AbstractSQLASTGenerator switch + SQLStatementGenerator overrides / emit* helpers

Writes (under parse/documents/coverage/):
  - sql_generator_phase0_inventory.csv
  - sql_generator_phase0_inventory.md

Does not run Maven; expects parser sources already generated:
  parse/target/generated-sources/antlr4/sql/SQLSelectParserParser.java

Usage:
  cd parse && mvn -q generate-sources
  python3 tools/sql_generator_phase0_inventory.py
"""
from __future__ import annotations

import csv
import re
import subprocess
import sys
from collections import Counter
from dataclasses import dataclass, field
from pathlib import Path

PARSE_ROOT = Path(__file__).resolve().parents[1]
REPO_ROOT = PARSE_ROOT.parent
OUT_DIR = PARSE_ROOT / "documents" / "coverage"
PARSER_JAVA = (
    PARSE_ROOT / "target/generated-sources/antlr4/sql/SQLSelectParserParser.java"
)
WALKER_JAVA = PARSE_ROOT / "src/main/java/sql/walker/SqlParseEventWalker.java"
MUMBLE_JAVA = PARSE_ROOT / "src/main/java/mumble/MumbleConstants.java"
ABSTRACT_GEN = PARSE_ROOT / "src/main/java/generators/AbstractSQLASTGenerator.java"
STATEMENT_GEN = PARSE_ROOT / "src/main/java/generators/SQLStatementGenerator.java"

# Walker keys that are not SQL text regeneration targets.
INTERNAL_AST_KEYS = frozenset(
    {
        "interface",
        "table_dictionary",
        "query_dictionary",
        "context_list",
        "dependent_queries",
        "derivation",
        "lhs_unresolved_columns",
        "outer_context_list_backup",
        "outer_def_entries_backup",
        "mumble_outer_table_alias",
        "inherited_visible_aliases",
        "local_from_registered_aliases",
        "scalar_subquery_aliases",
        "unresolved_column",
        "unknown",
    }
)

PASSTHROUGH_MARKERS = (
    "handleOneChild",
    "handlePushDown",
    "handlePopUp",
    "removeNodeMap",
    "getNodeMap",
    "addToParent",
    "walker.handleOneChild",
)


def ensure_parser_generated() -> None:
    if PARSER_JAVA.is_file():
        return
    print("Generated parser missing; running mvn generate-sources...", file=sys.stderr)
    subprocess.run(
        ["mvn", "-q", "generate-sources", "-DskipTests"],
        cwd=PARSE_ROOT,
        check=True,
    )


def parse_parser_rules(text: str) -> list[str]:
    pairs = re.findall(r"RULE_(\w+)\s*=\s*(\d+)", text)
    by_name: dict[str, int] = {}
    for name, num in pairs:
        by_name[name] = int(num)
    return [name for name, _ in sorted(by_name.items(), key=lambda x: x[1])]


def exit_method_for_rule(rule: str) -> str:
    if not rule:
        return "exit"
    return "exit" + rule[0].upper() + rule[1:]


def rule_for_exit_method(method: str) -> str | None:
    if not method.startswith("exit") or len(method) <= 4:
        return None
    tail = method[4:]
    return tail[0].lower() + tail[1:]


def parse_mumble_constants(text: str) -> dict[str, str]:
    return dict(re.findall(r"public static final String (MUMBLE_\w+_KEY) = \"([^\"]+)\"", text))


def split_exit_methods(walker_text: str) -> dict[str, str]:
    """Map exitMethodName -> method body (best effort)."""
    pattern = re.compile(
        r"(public void (exit\w+)\([^)]*\)\s*\{)",
        re.MULTILINE,
    )
    matches = list(pattern.finditer(walker_text))
    bodies: dict[str, str] = {}
    for i, m in enumerate(matches):
        start = m.end()
        end = matches[i + 1].start() if i + 1 < len(matches) else len(walker_text)
        bodies[m.group(2)] = walker_text[start:end]
    return bodies


def ast_keys_from_exit_body(body: str, const_to_value: dict[str, str]) -> set[str]:
    keys: set[str] = set()
    for const in re.findall(r"MUMBLE_\w+_KEY", body):
        if const in const_to_value:
            keys.add(const_to_value[const])
    for literal in re.findall(r"put\(\s*\"([a-z][a-z0-9_]*)\"", body):
        keys.add(literal)
    for literal in re.findall(r"subMap\.put\(\s*MUMBLE_\w+_KEY", body):
        pass  # already covered by const scan
    for m in re.findall(r"subMap\.put\(\s*\"([a-z][a-z0-9_]*)\"", body):
        keys.add(m)
    return keys


def parse_switch_handlers(
    abstract_text: str, const_map: dict[str, str]
) -> dict[str, str]:
    """Map mumble string key -> onHandler method name."""
    handlers: dict[str, str] = {}
    for line in abstract_text.splitlines():
        handler_m = re.search(r"-> (on\w+)", line)
        if not handler_m:
            continue
        handler = handler_m.group(1)
        ep = re.search(r"case mumble\.SQLParserEndPoints\.(\w+)", line)
        if ep:
            handlers[ep.group(1)] = handler
            continue
        const_m = re.search(r"case (MUMBLE_\w+_KEY)", line)
        if const_m:
            val = const_map.get(const_m.group(1))
            if val:
                handlers[val] = handler
    return handlers


def overridden_on_methods(statement_text: str) -> set[str]:
    return set(re.findall(r"protected void (on\w+)\(", statement_text))


def stub_on_methods(abstract_text: str) -> set[str]:
    stubs: set[str] = set()
    for m in re.finditer(
        r"protected void (on\w+)\([^)]*\)\s*\{\s*(handleStructured|handleTerminalOrRecursive|handleKeywordWithNode|appendCommaSeparated|appendParenthesized)\(",
        abstract_text,
    ):
        stubs.add(m.group(1))
    return stubs


def emit_helper_keys(statement_text: str, const_to_value: dict[str, str]) -> set[str]:
    """Mumble keys referenced from SQLStatementGenerator emit* / dedicated paths."""
    keys: set[str] = set()
    for const in re.findall(r"MUMBLE_\w+_KEY", statement_text):
        if const in const_to_value:
            keys.add(const_to_value[const])
    for s in re.findall(r"\.get\(\"([a-z][a-z0-9_]*)\"\)", statement_text):
        keys.add(s)
    return keys


def classify_key_emit(
    key: str,
    switch_handlers: dict[str, str],
    overridden: set[str],
    stub_handlers: set[str],
    emit_helpers: set[str],
) -> str:
    if key in INTERNAL_AST_KEYS:
        return "n/a_internal"
    if key in emit_helpers:
        handler = switch_handlers.get(key)
        if handler and handler in overridden:
            return "complete"
        if handler and handler in stub_handlers and handler not in overridden:
            return "partial_helper"
        return "complete_helper"
    handler = switch_handlers.get(key)
    if handler is None:
        return "missing"
    if handler in overridden:
        return "complete"
    if handler in stub_handlers:
        return "stub"
    return "partial"


def aggregate_rule_emit(
    ast_keys: set[str],
    key_status: dict[str, str],
    passthrough: bool,
) -> str:
    if passthrough and not ast_keys:
        return "passthrough"
    regen_keys = [k for k in ast_keys if key_status.get(k, "missing") != "n/a_internal"]
    if not regen_keys:
        return "n/a_internal" if ast_keys else "passthrough"
    statuses = [key_status.get(k, "missing") for k in regen_keys]
    if all(s in ("complete", "complete_helper", "partial_helper") for s in statuses):
        if all(s in ("complete", "complete_helper") for s in statuses):
            return "complete"
        return "partial"
    if any(s in ("complete", "complete_helper", "partial_helper") for s in statuses):
        return "partial"
    if all(s == "stub" for s in statuses):
        return "stub"
    if all(s == "missing" for s in statuses):
        return "missing"
    return "gap"


@dataclass
class RuleRow:
    rule: str
    rule_index: int
    exit_method: str
    has_exit: bool
    ast_keys: str
    emit_status: str
    emit_handlers: str = ""


def main() -> int:
    ensure_parser_generated()
    if not PARSER_JAVA.is_file():
        print(f"Missing {PARSER_JAVA}", file=sys.stderr)
        return 1

    parser_text = PARSER_JAVA.read_text(encoding="utf-8")
    walker_text = WALKER_JAVA.read_text(encoding="utf-8")
    mumble_text = MUMBLE_JAVA.read_text(encoding="utf-8")
    abstract_text = ABSTRACT_GEN.read_text(encoding="utf-8")
    statement_text = STATEMENT_GEN.read_text(encoding="utf-8")

    rules = parse_parser_rules(parser_text)
    rule_index = {name: i for i, name in enumerate(rules)}
    const_to_value = parse_mumble_constants(mumble_text)
    exit_bodies = split_exit_methods(walker_text)
    switch_handlers = parse_switch_handlers(abstract_text, const_to_value)
    overridden = overridden_on_methods(statement_text)
    stub_handlers = stub_on_methods(abstract_text)
    emit_helpers = emit_helper_keys(statement_text, const_to_value)

    key_status: dict[str, str] = {}
    for val in set(const_to_value.values()) | emit_helpers:
        key_status[val] = classify_key_emit(
            val, switch_handlers, overridden, stub_handlers, emit_helpers
        )

    rows: list[RuleRow] = []
    for rule in rules:
        exit_m = exit_method_for_rule(rule)
        has_exit = exit_m in exit_bodies
        body = exit_bodies.get(exit_m, "")
        ast_keys = ast_keys_from_exit_body(body, const_to_value) if has_exit else set()
        passthrough = has_exit and any(m in body for m in PASSTHROUGH_MARKERS)
        status = aggregate_rule_emit(ast_keys, key_status, passthrough)
        if not has_exit:
            status = "no_walker_exit"
        handlers = []
        for k in sorted(ast_keys):
            handlers.append(f"{k}:{key_status.get(k, 'missing')}")
        rows.append(
            RuleRow(
                rule=rule,
                rule_index=rule_index[rule],
                exit_method=exit_m,
                has_exit=has_exit,
                ast_keys="|".join(sorted(ast_keys)) if ast_keys else "",
                emit_status=status,
                emit_handlers=";".join(handlers),
            )
        )

    OUT_DIR.mkdir(parents=True, exist_ok=True)
    csv_path = OUT_DIR / "sql_generator_phase0_inventory.csv"
    md_path = OUT_DIR / "sql_generator_phase0_inventory.md"

    with csv_path.open("w", newline="", encoding="utf-8") as f:
        w = csv.writer(f)
        w.writerow(
            [
                "rule_index",
                "rule",
                "exit_method",
                "has_walker_exit",
                "ast_keys",
                "emit_status",
                "key_emit_detail",
            ]
        )
        for r in rows:
            w.writerow(
                [
                    r.rule_index,
                    r.rule,
                    r.exit_method,
                    "yes" if r.has_exit else "no",
                    r.ast_keys,
                    r.emit_status,
                    r.emit_handlers,
                ]
            )

    status_counts = Counter(r.emit_status for r in rows)
    key_counts = Counter(key_status.values())

    regen_gap = sum(
        1
        for r in rows
        if r.emit_status in ("missing", "gap", "stub", "no_walker_exit")
    )
    regen_ok = sum(1 for r in rows if r.emit_status in ("complete", "partial", "passthrough"))
    regen_na = sum(
        1 for r in rows if r.emit_status in ("n/a_internal",)
    )

    md_lines = [
        "# SQL generator Phase 0 inventory (auto-generated)",
        "",
        "Regenerate: `python3 parse/tools/sql_generator_phase0_inventory.py` "
        "(after `mvn -q generate-sources` in `parse/`).",
        "",
        "## Summary",
        "",
        f"| Metric | Value |",
        f"|--------|------:|",
        f"| Parser rules (`RULE_*`) | {len(rules)} |",
        f"| Walker `exit*` methods (parsed blocks) | {len(exit_bodies)} |",
        f"| Mumble AST string keys (constants) | {len(const_to_value)} |",
        f"| Generator switch routes (`on*`) | {len(switch_handlers)} |",
        f"| `SQLStatementGenerator` `on*` overrides | {len(overridden)} |",
        f"| Keys referenced in generator emit paths | {len(emit_helpers)} |",
        "",
        "### Per-rule emit status (grammar rule grain)",
        "",
        "| Status | Rules | % of grammar |",
        "|--------|------:|-------------:|",
    ]
    for status, count in sorted(status_counts.items(), key=lambda x: (-x[1], x[0])):
        pct = 100.0 * count / len(rules) if rules else 0
        md_lines.append(f"| `{status}` | {count} | {pct:.1f}% |")

    md_lines.extend(
        [
            "",
            "**Generation gap (rules without full emit):** "
            f"`missing` + `gap` + `stub` + `no_walker_exit` = "
            f"**{status_counts.get('missing', 0) + status_counts.get('gap', 0) + status_counts.get('stub', 0) + status_counts.get('no_walker_exit', 0)}** "
            f"of **{len(rules)}** rules "
            f"({100.0 * regen_gap / len(rules):.1f}%).",
            "",
            "**Regeneration-ready (complete + partial + passthrough):** "
            f"**{regen_ok}** rules ({100.0 * regen_ok / len(rules):.1f}%).",
            "",
            "### Per mumble key (AST grain)",
            "",
            "| Key emit status | Keys |",
            "|-----------------|-----:|",
        ]
    )
    for status, count in sorted(key_counts.items(), key=lambda x: (-x[1], x[0])):
        md_lines.append(f"| `{status}` | {count} |")

    md_lines.extend(
        [
            "",
            "## Status legend",
            "",
            "| Rule status | Meaning |",
            "|-------------|---------|",
            "| `complete` | All regen-relevant AST keys for this rule have implemented generator coverage |",
            "| `partial` | Mix of complete and stub/missing keys, or helper-only emit |",
            "| `stub` | Keys route to abstract `on*` stubs only (`handleStructured` → `Unexpected node`) |",
            "| `missing` | AST keys not in generator switch / emit helpers |",
            "| `gap` | Mixed missing + stub |",
            "| `passthrough` | Walker exit aggregates children only (no direct AST keys) |",
            "| `n/a_internal` | Symbol-table / internal keys only |",
            "| `no_walker_exit` | No `exit*` in `SqlParseEventWalker` for this grammar rule |",
            "",
            f"Full table: [`sql_generator_phase0_inventory.csv`](sql_generator_phase0_inventory.csv)",
            "",
        ]
    )

    md_path.write_text("\n".join(md_lines), encoding="utf-8")

    print(f"Wrote {csv_path}")
    print(f"Wrote {md_path}")
    print()
    print("=== Generation gap (rule-level) ===")
    for status in (
        "complete",
        "partial",
        "passthrough",
        "stub",
        "missing",
        "gap",
        "n/a_internal",
        "no_walker_exit",
    ):
        if status_counts.get(status):
            print(f"  {status:16} {status_counts[status]:4}  ({100*status_counts[status]/len(rules):5.1f}%)")
    gap_total = (
        status_counts.get("missing", 0)
        + status_counts.get("gap", 0)
        + status_counts.get("stub", 0)
        + status_counts.get("no_walker_exit", 0)
    )
    print(f"  GAP TOTAL        {gap_total:4}  ({100*gap_total/len(rules):5.1f}%)  (missing+gap+stub+no_walker_exit)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
