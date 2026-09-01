#!/usr/bin/env python3
"""Dual-parse 5.0.0-3 vs 5.1.3 for Phase 2.9 Panto degradation fixtures."""

from __future__ import annotations

import json
import re
import subprocess
import sys
import time
from pathlib import Path
from typing import Any

ROOT = Path(__file__).resolve().parents[2]
SQL_DIR = ROOT / "parse/docs/rmcp-handoff/5.1.3-panto-outstanding/sql"
OUT_DIR = Path(__file__).resolve().parent / "results"
JAVA_SRC = Path(__file__).resolve().parent / "ParseJsonDump.java"

JAR_5003 = ROOT / ".tmp/pss-5003/parse/target/pss-parse-5.0.0-3.jar"
JAR_513 = ROOT / "parse/target/pss-parse-5.1.3-1-fat.jar"

ROWS = [583, 2139, 3150, 3870, 4648, 4726, 5410, 5455]

LEGACY_MISSING = {
    583: ["latest_applications", "activity_prospect_map", "campus_visit_activity"],
    2139: ["latest_applications", "activity_prospect_map", "campus_visit_activity"],
    3150: ["race_data"],
    3870: ["student_term_crm"],
    4648: ["st_student_term_sweep"],
    4726: ["st_student_term_sweep"],
    5410: [],
    5455: ["comb_common", "chosencontact_combined"],
}


def last_identifier(key: str) -> str:
    key = key.strip()
    if not key:
        return key
    if key.startswith("<") and key.endswith(">"):
        inner = key[1:-1]
        if "].{" in inner:
            return inner.rsplit(".", 1)[-1].strip("{}")
        return inner.rsplit(".", 1)[-1]
    return key.rsplit(".", 1)[-1]


def norm_key(key: str) -> str:
    return last_identifier(key).lower()


def table_dict_keys(table_dictionary: dict[str, Any] | None) -> list[str]:
    if not isinstance(table_dictionary, dict):
        return []
    return sorted(table_dictionary.keys(), key=str.lower)


def table_dict_norm_keys(table_dictionary: dict[str, Any] | None) -> set[str]:
    return {norm_key(k) for k in table_dict_keys(table_dictionary)}


def is_tuple_or_physical_key(key: str) -> bool:
    k = key.strip()
    if k.startswith("<"):
        return True
    return "." in k or "[" in k


def looks_like_cte_alias(key: str) -> bool:
    k = key.strip()
    if k.startswith("<"):
        return False
    if "." in k or "[" in k:
        return False
    return True


def collect_symbol_strings(node: Any, out: set[str]) -> None:
    if isinstance(node, dict):
        for k, v in node.items():
            if isinstance(k, str):
                out.add(k)
                out.add(norm_key(k))
            collect_symbol_strings(v, out)
    elif isinstance(node, list):
        for item in node:
            collect_symbol_strings(item, out)
    elif isinstance(node, str):
        out.add(node)
        out.add(norm_key(node))


def cte_table_alias_binding(symbol_table: Any, cte_name: str) -> str | None:
    """Return 'cte_name=queryN' when 5.1.3 registers the CTE on table_alias."""
    target = norm_key(cte_name)
    if not isinstance(symbol_table, dict):
        return None
    for scope_val in symbol_table.values():
        if not isinstance(scope_val, dict):
            continue
        table_alias = scope_val.get("table_alias")
        if not isinstance(table_alias, dict):
            continue
        for alias, ref in table_alias.items():
            if norm_key(str(alias)) == target:
                return f"{alias}={ref}"
    return None


def cte_evidence(symbol_table: Any, cte_name: str) -> dict[str, bool]:
    strings: set[str] = set()
    collect_symbol_strings(symbol_table, strings)
    target = norm_key(cte_name)
    evidence = {
        "name_in_symbol_tree": target in strings,
        "table_alias_binding": False,
        "context_list_entry": False,
        "def_scope_key": False,
    }
    if isinstance(symbol_table, dict):
        for key in symbol_table:
            if isinstance(key, str) and key.lower().startswith("def_") and target in key.lower():
                evidence["def_scope_key"] = True
        for scope_key, scope_val in symbol_table.items():
            if not isinstance(scope_val, dict):
                continue
            table_alias = scope_val.get("table_alias")
            if isinstance(table_alias, dict):
                for alias, ref in table_alias.items():
                    if norm_key(str(alias)) == target:
                        evidence["table_alias_binding"] = True
                    if isinstance(ref, str) and norm_key(ref) == target:
                        evidence["table_alias_binding"] = True
            context_list = scope_val.get("context_list")
            if isinstance(context_list, list):
                for entry in context_list:
                    if isinstance(entry, str) and norm_key(entry) == target:
                        evidence["context_list_entry"] = True
    return evidence


def fatal_diagnostics(payload: dict[str, Any]) -> list[dict[str, Any]]:
    out: list[dict[str, Any]] = []
    for diag in payload.get("diagnostics") or []:
        if not isinstance(diag, dict):
            continue
        sev = str(diag.get("severity", "")).upper()
        if sev == "FATAL":
            out.append(diag)
    if not out and payload.get("fatalErrorCount"):
        for msg in payload.get("fatalErrorStringList") or []:
            out.append({"severity": "FATAL", "message": msg, "source": "legacy"})
    return out


def compile_dumper(jar: Path, class_dir: Path, suffix: str) -> Path:
    out_dir = class_dir / suffix
    out_dir.mkdir(parents=True, exist_ok=True)
    subprocess.run(
        ["javac", "-cp", str(jar), "-d", str(out_dir), str(JAVA_SRC)],
        check=True,
        capture_output=True,
        text=True,
    )
    return out_dir


def parse_with_dump(jar: Path, class_dir: Path, sql_file: Path) -> tuple[dict[str, Any], int]:
    start = time.time()
    proc = subprocess.run(
        ["java", "-cp", f"{jar}:{class_dir}", "ParseJsonDump", str(sql_file)],
        capture_output=True,
        text=True,
    )
    elapsed_ms = int((time.time() - start) * 1000)
    if proc.returncode != 0:
        raise RuntimeError(
            f"Parse failed for {sql_file.name} using {jar.name}:\n{proc.stderr}\n{proc.stdout}"
        )
    return json.loads(proc.stdout), elapsed_ms


def score_row(row: int, v500: dict[str, Any], v513: dict[str, Any]) -> dict[str, Any]:
    td500 = v500.get("tableDictionary") or {}
    td513 = v513.get("tableDictionary") or {}
    keys500 = table_dict_keys(td500)
    keys513 = table_dict_keys(td513)
    norm500 = table_dict_norm_keys(td500)
    norm513 = table_dict_norm_keys(td513)

    legacy_missing = LEGACY_MISSING[row]
    fatals513 = fatal_diagnostics(v513)
    fatals500 = fatal_diagnostics(v500)

    physical_only_missing: list[str] = []
    cte_only_missing: list[str] = []
    functional_losses: list[dict[str, Any]] = []

    for miss in legacy_missing:
        miss_norm = norm_key(miss)
        in_513_td = miss_norm in norm513
        if looks_like_cte_alias(miss):
            cte_only_missing.append(miss)
            ev = cte_evidence(v513.get("symbolTable"), miss)
            if not in_513_td and not any(ev.values()):
                functional_losses.append(
                    {"name": miss, "kind": "cte", "issue": "no tableDictionary key and no symbol-tree evidence", "evidence": ev}
                )
            elif not in_513_td and any(ev.values()):
                functional_losses.append(
                    {
                        "name": miss,
                        "kind": "cte",
                        "issue": "5.1.3 enhancement: symbol-tree registration (table_alias/queryN), not tableDictionary",
                        "evidence": ev,
                    }
                )
        else:
            if not in_513_td:
                physical_only_missing.append(miss)
                functional_losses.append({"name": miss, "kind": "physical_or_tuple_alias", "issue": "missing from 5.1.3 tableDictionary"})

    extra_500 = sorted(norm500 - norm513)
    extra_513 = sorted(norm513 - norm500)

  # Keys present in 5.0.0 but absent in 5.1.3 after last-identifier normalization
    missing_norm_vs_500 = sorted(norm500 - norm513)
    new_norm_vs_500 = sorted(norm513 - norm500)

    significant_issues: list[str] = []
    enhancements_513: list[str] = []
    not_significant: list[str] = []

    if fatals513 and len(fatals513) > len(fatals500):
        significant_issues.append(
            f"5.1.3 has {len(fatals513)} FATAL diagnostics vs {len(fatals500)} in 5.0.0-3"
        )
    elif fatals513:
        not_significant.append("FATAL counts equal or 5.1.3 improved")

    for item in functional_losses:
        if item.get("issue", "").startswith("5.1.3 enhancement"):
            binding = cte_table_alias_binding(v513.get("symbolTable"), item["name"])
            if binding:
                enhancements_513.append(
                    f"5.1.3 enhancement: CTE '{item['name']}' registered in symbol table table_alias as {binding} "
                    f"(not in global tableDictionary by design; richer than 5.0.0-3 CTE promotion)"
                )
            else:
                not_significant.append(
                    f"CTE '{item['name']}' missing from tableDictionary but present elsewhere in symbol tree"
                )
        elif item["kind"] == "cte":
            significant_issues.append(f"CTE '{item['name']}' functionally absent from 5.1.3 outputs")
        else:
            significant_issues.append(f"'{item['name']}' missing from 5.1.3 tableDictionary")

    for miss_norm in missing_norm_vs_500:
        if miss_norm in {norm_key(x) for x in legacy_missing if looks_like_cte_alias(x)}:
            continue
        rep = next((k for k in keys500 if norm_key(k) == miss_norm), miss_norm)
        if looks_like_cte_alias(rep):
            ev = cte_evidence(v513.get("symbolTable"), rep)
            binding = cte_table_alias_binding(v513.get("symbolTable"), rep)
            if binding:
                enhancements_513.append(
                    f"5.1.3 enhancement: CTE '{rep}' only in 5.0.0 tableDictionary; "
                    f"canonical registration is table_alias {binding}"
                )
            elif any(ev.values()):
                not_significant.append(
                    f"CTE key '{rep}' only in 5.0.0 tableDictionary (symbol-tree evidence present)"
                )
            else:
                significant_issues.append(f"CTE key '{rep}' lost without symbol-tree replacement")
        elif is_tuple_or_physical_key(rep) or not looks_like_cte_alias(rep):
            if miss_norm not in norm513:
                significant_issues.append(f"Physical/tuple source '{rep}' in 5.0.0 but not 5.1.3 tableDictionary")

    verdict = "no_significant_issue"
    if significant_issues:
        verdict = "significant_regression" if any(
            "FATAL" in s or "functionally absent" in s or "Physical/tuple" in s or "lost without" in s
            for s in significant_issues
        ) else "needs_review"

    return {
        "legacy_missing_keys": legacy_missing,
        "fatal_count": {"5.0.0-3": len(fatals500), "5.1.3": len(fatals513)},
        "fatal_messages_513": [
            {k: d.get(k) for k in ("severity", "code", "message", "line", "charPositionInLine", "source", "phase")}
            for d in fatals513
        ],
        "fatal_messages_5003": [
            {k: d.get(k) for k in ("severity", "code", "message", "line", "charPositionInLine", "source", "phase")}
            if isinstance(d, dict)
            else {"message": str(d)}
            for d in fatals500
        ],
        "tableDictionary_keys": {"5.0.0-3": keys500, "5.1.3": keys513},
        "tableDictionary_only_in_5003_norm": missing_norm_vs_500,
        "tableDictionary_only_in_513_norm": new_norm_vs_500,
        "cte_rescoring": [
            {
                "name": miss,
                "in_513_tableDictionary": norm_key(miss) in norm513,
                "table_alias_binding_513": cte_table_alias_binding(v513.get("symbolTable"), miss),
                "symbol_tree_evidence_513": cte_evidence(v513.get("symbolTable"), miss),
            }
            for miss in legacy_missing
            if looks_like_cte_alias(miss)
        ],
        "enhancements_513": enhancements_513,
        "significant_issues": significant_issues,
        "not_significant_under_latest_policy": not_significant,
        "verdict": verdict,
    }


def main() -> int:
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    class_dir = Path(__file__).resolve().parent / "classes"

    for jar in (JAR_5003, JAR_513):
        if not jar.exists():
            print(f"Missing jar: {jar}", file=sys.stderr)
            return 1

    class_dir_5003 = compile_dumper(JAR_5003, class_dir, "5003")
    class_dir_513 = compile_dumper(JAR_513, class_dir, "513")

    summary: dict[str, Any] = {"rows": {}, "generated_at": time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime())}

    for row in ROWS:
        sql_file = SQL_DIR / f"csv-row-{row}.sql"
        print(f"Parsing row {row} ...", flush=True)
        v500, ms500 = parse_with_dump(JAR_5003, class_dir_5003, sql_file)
        v513, ms513 = parse_with_dump(JAR_513, class_dir_513, sql_file)
        scored = score_row(row, v500, v513)
        row_payload = {
            "sql_file": str(sql_file.relative_to(ROOT)),
            "parse_ms": {"5.0.0-3": ms500, "5.1.3": ms513},
            "parse_outputs": {
                "5.0.0-3": {
                    "tableDictionary": v500.get("tableDictionary"),
                    "symbolTable": v500.get("symbolTable"),
                    "queryDictionary": v500.get("queryDictionary"),
                    "fatalErrorCount": v500.get("fatalErrorCount"),
                    "fatalErrorStringList": v500.get("fatalErrorStringList"),
                    "diagnostics": v500.get("diagnostics"),
                    "parserMessageStringList": v500.get("parserMessageStringList"),
                },
                "5.1.3": {
                    "tableDictionary": v513.get("tableDictionary"),
                    "symbolTable": v513.get("symbolTable"),
                    "queryDictionary": v513.get("queryDictionary"),
                    "fatalErrorCount": v513.get("fatalErrorCount"),
                    "fatalErrorStringList": v513.get("fatalErrorStringList"),
                    "diagnostics": v513.get("diagnostics"),
                },
            },
            "scoring": scored,
        }
        out_file = OUT_DIR / f"row-{row}.json"
        out_file.write_text(json.dumps(row_payload, indent=2), encoding="utf-8")
        summary["rows"][str(row)] = {
            "parse_ms": row_payload["parse_ms"],
            "verdict": scored["verdict"],
            "significant_issues": scored["significant_issues"],
            "enhancements_513": scored.get("enhancements_513", []),
            "not_significant": scored["not_significant_under_latest_policy"],
            "fatal_count": scored["fatal_count"],
            "tableDictionary_key_counts": {
                "5.0.0-3": len(scored["tableDictionary_keys"]["5.0.0-3"]),
                "5.1.3": len(scored["tableDictionary_keys"]["5.1.3"]),
            },
        }
        print(f"  done: verdict={scored['verdict']} fatals={scored['fatal_count']}")

    (OUT_DIR / "summary.json").write_text(json.dumps(summary, indent=2), encoding="utf-8")
    print(json.dumps(summary, indent=2))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
