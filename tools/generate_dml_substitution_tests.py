#!/usr/bin/env python3
"""Generate DML substitution-complexity tests with captured goldens."""

from __future__ import annotations

import json
import re
import subprocess
import textwrap
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
PARSE = ROOT / "parse"
PROBE_JAVA = PARSE / "src/test/java/sql/walker/DmlSubstitutionGoldenProbe.java"
OUTPUT_JAVA = PARSE / "src/test/java/sql/walker/SqlEventWalkerDmlUpdateInsertDeleteTruncateTests.generated.java"
CASES_JSON = PARSE / "target/dml_substitution_cases.json"

# Each case: (method_suffix, sql_lines_list)
INSERT_CASES = [
    ("I1WithCteGroupByHaving",
     [
         "WITH staged AS (",
         "  SELECT a.emp_id, sum(a.<insert select col I1>) AS total_score",
         "  FROM <[HR Data].[Employee Accounts I1]> a",
         "  WHERE a.<insert where col I1> > 0",
         "  GROUP BY a.emp_id, a.<insert group col I1>",
         "  HAVING sum(a.<insert select col I1>) > 0)",
         "INSERT INTO employees (agg_score, rank_bucket)",
         "SELECT s.total_score, s.emp_id",
         "FROM staged s",
         "WHERE s.total_score > 0",
     ]),
    ("I2SubqueryUnionWhereSubstitutions",
     [
         "INSERT INTO employees (score, rank_bucket)",
         "SELECT u.emp_id, u.metric_val",
         "FROM (",
         "  SELECT a.emp_id, a.<insert select col I2> AS metric_val",
         "  FROM <[Sales Data].[Perf Feed I2]> a",
         "  WHERE a.<insert where col I2> > 0",
         "  UNION",
         "  SELECT b.dept_id, b.<insert select col I2b> AS metric_val",
         "  FROM <[Sales Data].[Quota Feed I2]> b",
         "  WHERE b.<insert where col I2b> > 0",
         ") u",
         "WHERE u.metric_val > 0",
     ]),
    ("I3WithCteIntersectOrderBySubstitution",
     [
         "WITH branch_a AS (",
         "  SELECT a.emp_id, a.<insert select col I3> AS score_val",
         "  FROM <[Ops Data].[Account Ledger I3]> a",
         "  WHERE a.<insert where col I3> > 0",
         "  ORDER BY a.<insert order col I3>",
         "), branch_b AS (",
         "  SELECT b.emp_id, b.<insert select col I3> AS score_val",
         "  FROM <[Ops Data].[Audit Ledger I3]> b",
         "  WHERE b.<insert where col I3> > 0",
         "  ORDER BY b.<insert order col I3>",
         "), base AS (",
         "  SELECT * FROM branch_a",
         "  INTERSECT",
         "  SELECT * FROM branch_b",
         ")",
         "INSERT INTO employees (top_score, rank_bucket)",
         "SELECT b.score_val, b.emp_id",
         "FROM base b",
     ]),
    ("I4NestedWithInCteBody",
     [
         "WITH outer_cte AS (",
         "  WITH inner_cte AS (",
         "    SELECT a.emp_id, a.<insert select col I4> AS metric_val",
         "    FROM <[Finance].[Revenue Feed I4]> a",
         "    WHERE a.<insert where col I4> > 0",
         "  )",
         "  SELECT i.emp_id, i.metric_val",
         "  FROM inner_cte i",
         "  WHERE i.metric_val > 0",
         ")",
         "INSERT INTO employees (score, rank_bucket)",
         "SELECT o.metric_val, o.emp_id",
         "FROM outer_cte o",
     ]),
    ("I5WithCteQualifyWindowSubstitution",
     [
         "WITH ranked AS (",
         "  SELECT a.emp_id, a.<insert select col I5> AS score_val,",
         "         row_number() OVER (PARTITION BY a.emp_id ORDER BY a.<insert order col I5> DESC) AS rn",
         "  FROM <[Metrics].[Score Feed I5]> a",
         "  WHERE a.<insert where col I5> > 0",
         "  QUALIFY rn = 1",
         ")",
         "INSERT INTO employees (top_score, rank_bucket)",
         "SELECT r.score_val, r.emp_id",
         "FROM ranked r",
     ]),
    ("I6SubqueryJoinOnColumnSubstitution",
     [
         "INSERT INTO employees (score, rank_bucket)",
         "SELECT j.metric_val, j.emp_id",
         "FROM (",
         "  SELECT a.emp_id, a.<insert select col I6> AS metric_val",
         "  FROM <[Join Data].[Left Feed I6]> a",
         "  JOIN <[Join Data].[Right Feed I6]> b",
         "    ON a.<insert join col I6> = b.<insert join col I6b>",
         "  WHERE a.<insert where col I6> > 0",
         ") j",
     ]),
    ("I7ChainedCteReferences",
     [
         "WITH step1 AS (",
         "  SELECT a.emp_id, a.<insert select col I7> AS raw_val",
         "  FROM <[Pipeline].[Stage One I7]> a",
         "  WHERE a.<insert where col I7> > 0",
         "), step2 AS (",
         "  SELECT s.emp_id, s.raw_val",
         "  FROM step1 s",
         "  WHERE s.raw_val > 0",
         ")",
         "INSERT INTO employees (score, rank_bucket)",
         "SELECT t.raw_val, t.emp_id",
         "FROM step2 t",
     ]),
    ("I8UnionIntersectNestedSubquery",
     [
         "INSERT INTO employees (score, rank_bucket)",
         "SELECT x.metric_val, x.emp_id",
         "FROM (",
         "  SELECT u.emp_id, u.metric_val",
         "  FROM (",
         "    SELECT a.emp_id, a.<insert select col I8> AS metric_val",
         "    FROM <[Blend Data].[Branch A I8]> a",
         "    WHERE a.<insert where col I8> > 0",
         "    UNION",
         "    SELECT b.emp_id, b.<insert select col I8> AS metric_val",
         "    FROM <[Blend Data].[Branch B I8]> b",
         "    WHERE b.<insert where col I8> > 0",
         "  ) u",
         "  INTERSECT",
         "  SELECT c.emp_id, c.<insert select col I8> AS metric_val",
         "  FROM <[Blend Data].[Branch C I8]> c",
         "  WHERE c.<insert where col I8> > 0",
         ") x",
     ]),
    ("I9WithCteSelfUnionBranches",
     [
         "WITH blended AS (",
         "  SELECT a.emp_id, a.<insert select col I9a> AS metric_val",
         "  FROM <[Union Data].[Feed Alpha I9]> a",
         "  WHERE a.<insert where col I9a> > 0",
         "  UNION",
         "  SELECT b.emp_id, b.<insert select col I9b> AS metric_val",
         "  FROM <[Union Data].[Feed Beta I9]> b",
         "  WHERE b.<insert where col I9b> > 0",
         ")",
         "INSERT INTO employees (score, rank_bucket)",
         "SELECT bl.metric_val, bl.emp_id",
         "FROM blended bl",
         "WHERE bl.metric_val > 0",
     ]),
    ("I10SubqueryGroupByHavingQualifyCombined",
     [
         "INSERT INTO employees (agg_score, rank_bucket)",
         "SELECT src.total_score, src.emp_id",
         "FROM (",
         "  SELECT a.emp_id, sum(a.<insert select col I10>) AS total_score,",
         "         row_number() OVER (PARTITION BY a.emp_id ORDER BY a.<insert order col I10> DESC) AS rn",
         "  FROM <[Agg Data].[Fact Table I10]> a",
         "  WHERE a.<insert where col I10> > 0",
         "  GROUP BY a.emp_id, a.<insert group col I10>",
         "  HAVING sum(a.<insert select col I10>) > 0",
         "  QUALIFY rn = 1",
         ") src",
     ]),
]

UPDATE_CASES = [
    ("U1WithCteGroupByHaving",
     [
         "WITH staged AS (",
         "  SELECT a.emp_id, sum(a.<update select col U1>) AS total_score",
         "  FROM <[HR Data].[Employee Accounts U1]> a",
         "  WHERE a.<update where col U1> > 0",
         "  GROUP BY a.emp_id, a.<update group col U1>",
         "  HAVING sum(a.<update select col U1>) > 0)",
         "UPDATE employees e",
         "SET score = s.total_score",
         "FROM staged s",
         "WHERE e.emp_id = s.emp_id AND s.total_score > 0",
     ]),
    ("U2SubqueryUnionWhereSubstitutions",
     [
         "UPDATE employees e",
         "SET score = src.metric_val, rank_bucket = src.emp_id",
         "FROM (",
         "  SELECT a.emp_id, a.<update select col U2> AS metric_val",
         "  FROM <[Sales Data].[Perf Feed U2]> a",
         "  WHERE a.<update where col U2> > 0",
         "  UNION",
         "  SELECT b.dept_id, b.<update select col U2b> AS metric_val",
         "  FROM <[Sales Data].[Quota Feed U2]> b",
         "  WHERE b.<update where col U2b> > 0",
         ") src",
         "WHERE e.emp_id = src.emp_id",
     ]),
    ("U3WithCteIntersectOrderBySubstitution",
     [
         "WITH base AS (",
         "  SELECT a.emp_id, a.<update select col U3> AS score_val",
         "  FROM <[Ops Data].[Account Ledger U3]> a",
         "  WHERE a.<update where col U3> > 0",
         "  INTERSECT",
         "  SELECT b.emp_id, b.<update select col U3> AS score_val",
         "  FROM <[Ops Data].[Audit Ledger U3]> b",
         "  WHERE b.<update where col U3> > 0",
         ")",
         "UPDATE employees e",
         "SET score = b.score_val",
         "FROM base b",
         "WHERE e.emp_id = b.emp_id",
     ]),
    ("U4NestedWithInCteBody",
     [
         "WITH outer_cte AS (",
         "  WITH inner_cte AS (",
         "    SELECT a.emp_id, a.<update select col U4> AS metric_val",
         "    FROM <[Finance].[Revenue Feed U4]> a",
         "    WHERE a.<update where col U4> > 0",
         "  )",
         "  SELECT i.emp_id, i.metric_val",
         "  FROM inner_cte i",
         ")",
         "UPDATE employees e",
         "SET score = o.metric_val",
         "FROM outer_cte o",
         "WHERE e.emp_id = o.emp_id",
     ]),
    ("U5WithCteQualifyWindowSubstitution",
     [
         "WITH ranked AS (",
         "  SELECT a.emp_id, a.<update select col U5> AS score_val,",
         "         row_number() OVER (PARTITION BY a.emp_id ORDER BY a.<update order col U5> DESC) AS rn",
         "  FROM <[Metrics].[Score Feed U5]> a",
         "  WHERE a.<update where col U5> > 0",
         "  QUALIFY rn = 1",
         ")",
         "UPDATE employees e",
         "SET score = r.score_val",
         "FROM ranked r",
         "WHERE e.emp_id = r.emp_id",
     ]),
    ("U6SubqueryJoinOnColumnSubstitution",
     [
         "UPDATE employees e",
         "SET score = j.metric_val",
         "FROM (",
         "  SELECT a.emp_id, a.<update select col U6> AS metric_val",
         "  FROM <[Join Data].[Left Feed U6]> a",
         "  JOIN <[Join Data].[Right Feed U6]> b",
         "    ON a.<update join col U6> = b.<update join col U6b>",
         "  WHERE a.<update where col U6> > 0",
         ") j",
         "WHERE e.emp_id = j.emp_id",
     ]),
    ("U7ChainedCteReferences",
     [
         "WITH step1 AS (",
         "  SELECT a.emp_id, a.<update select col U7> AS raw_val",
         "  FROM <[Pipeline].[Stage One U7]> a",
         "  WHERE a.<update where col U7> > 0",
         "), step2 AS (",
         "  SELECT s.emp_id, s.raw_val",
         "  FROM step1 s",
         ")",
         "UPDATE employees e",
         "SET score = t.raw_val",
         "FROM step2 t",
         "WHERE e.emp_id = t.emp_id",
     ]),
    ("U8UnionIntersectNestedSubquery",
     [
         "UPDATE employees e",
         "SET score = x.metric_val",
         "FROM (",
         "  SELECT u.emp_id, u.metric_val",
         "  FROM (",
         "    SELECT a.emp_id, a.<update select col U8> AS metric_val",
         "    FROM <[Blend Data].[Branch A U8]> a",
         "    WHERE a.<update where col U8> > 0",
         "    UNION",
         "    SELECT b.emp_id, b.<update select col U8> AS metric_val",
         "    FROM <[Blend Data].[Branch B U8]> b",
         "    WHERE b.<update where col U8> > 0",
         "  ) u",
         "  INTERSECT",
         "  SELECT c.emp_id, c.<update select col U8> AS metric_val",
         "  FROM <[Blend Data].[Branch C U8]> c",
         "  WHERE c.<update where col U8> > 0",
         ") x",
         "WHERE e.emp_id = x.emp_id",
     ]),
    ("U9WithCteSelfUnionBranches",
     [
         "WITH blended AS (",
         "  SELECT a.emp_id, a.<update select col U9a> AS metric_val",
         "  FROM <[Union Data].[Feed Alpha U9]> a",
         "  WHERE a.<update where col U9a> > 0",
         "  UNION",
         "  SELECT b.emp_id, b.<update select col U9b> AS metric_val",
         "  FROM <[Union Data].[Feed Beta U9]> b",
         "  WHERE b.<update where col U9b> > 0",
         ")",
         "UPDATE employees e",
         "SET score = bl.metric_val",
         "FROM blended bl",
         "WHERE e.emp_id = bl.emp_id",
     ]),
    ("U10SubqueryGroupByHavingQualifyCombined",
     [
         "UPDATE employees e",
         "SET score = src.total_score",
         "FROM (",
         "  SELECT a.emp_id, sum(a.<update select col U10>) AS total_score,",
         "         row_number() OVER (PARTITION BY a.emp_id ORDER BY a.<update order col U10> DESC) AS rn",
         "  FROM <[Agg Data].[Fact Table U10]> a",
         "  WHERE a.<update where col U10> > 0",
         "  GROUP BY a.emp_id, a.<update group col U10>",
         "  HAVING sum(a.<update select col U10>) > 0",
         "  QUALIFY rn = 1",
         ") src",
         "WHERE e.emp_id = src.emp_id",
     ]),
]

DELETE_CASES = [
    ("D1WithCteGroupByHaving",
     [
         "WITH staged AS (",
         "  SELECT a.emp_id, sum(a.<delete select col D1>) AS total_score",
         "  FROM <[HR Data].[Employee Accounts D1]> a",
         "  WHERE a.<delete where col D1> > 0",
         "  GROUP BY a.emp_id, a.<delete group col D1>",
         "  HAVING sum(a.<delete select col D1>) > 0)",
         "DELETE FROM employees e",
         "USING staged s",
         "WHERE e.emp_id = s.emp_id AND s.total_score > 0",
     ]),
    ("D2SubqueryUnionWhereSubstitutions",
     [
         "DELETE FROM employees e",
         "USING (",
         "  SELECT a.emp_id, a.<delete select col D2> AS metric_val",
         "  FROM <[Sales Data].[Perf Feed D2]> a",
         "  WHERE a.<delete where col D2> > 0",
         "  UNION",
         "  SELECT b.dept_id, b.<delete select col D2b> AS metric_val",
         "  FROM <[Sales Data].[Quota Feed D2]> b",
         "  WHERE b.<delete where col D2b> > 0",
         ") src",
         "WHERE e.emp_id = src.emp_id",
     ]),
    ("D3WithCteIntersectOrderBySubstitution",
     [
         "WITH base AS (",
         "  SELECT a.emp_id, a.<delete select col D3> AS score_val",
         "  FROM <[Ops Data].[Account Ledger D3]> a",
         "  WHERE a.<delete where col D3> > 0",
         "  INTERSECT",
         "  SELECT b.emp_id, b.<delete select col D3> AS score_val",
         "  FROM <[Ops Data].[Audit Ledger D3]> b",
         "  WHERE b.<delete where col D3> > 0",
         ")",
         "DELETE FROM employees e",
         "USING base b",
         "WHERE e.emp_id = b.emp_id",
     ]),
    ("D4NestedWithInCteBody",
     [
         "WITH outer_cte AS (",
         "  WITH inner_cte AS (",
         "    SELECT a.emp_id, a.<delete select col D4> AS metric_val",
         "    FROM <[Finance].[Revenue Feed D4]> a",
         "    WHERE a.<delete where col D4> > 0",
         "  )",
         "  SELECT i.emp_id, i.metric_val",
         "  FROM inner_cte i",
         ")",
         "DELETE FROM employees e",
         "USING outer_cte o",
         "WHERE e.emp_id = o.emp_id",
     ]),
    ("D5WithCteQualifyWindowSubstitution",
     [
         "WITH ranked AS (",
         "  SELECT a.emp_id, a.<delete select col D5> AS score_val,",
         "         row_number() OVER (PARTITION BY a.emp_id ORDER BY a.<delete order col D5> DESC) AS rn",
         "  FROM <[Metrics].[Score Feed D5]> a",
         "  WHERE a.<delete where col D5> > 0",
         "  QUALIFY rn = 1",
         ")",
         "DELETE FROM employees e",
         "USING ranked r",
         "WHERE e.emp_id = r.emp_id",
     ]),
    ("D6SubqueryJoinOnColumnSubstitution",
     [
         "DELETE FROM employees e",
         "USING (",
         "  SELECT a.emp_id, a.<delete select col D6> AS metric_val",
         "  FROM <[Join Data].[Left Feed D6]> a",
         "  JOIN <[Join Data].[Right Feed D6]> b",
         "    ON a.<delete join col D6> = b.<delete join col D6b>",
         "  WHERE a.<delete where col D6> > 0",
         ") j",
         "WHERE e.emp_id = j.emp_id",
     ]),
    ("D7ChainedCteReferences",
     [
         "WITH step1 AS (",
         "  SELECT a.emp_id, a.<delete select col D7> AS raw_val",
         "  FROM <[Pipeline].[Stage One D7]> a",
         "  WHERE a.<delete where col D7> > 0",
         "), step2 AS (",
         "  SELECT s.emp_id, s.raw_val",
         "  FROM step1 s",
         ")",
         "DELETE FROM employees e",
         "USING step2 t",
         "WHERE e.emp_id = t.emp_id",
     ]),
    ("D8UnionIntersectNestedSubquery",
     [
         "DELETE FROM employees e",
         "USING (",
         "  SELECT u.emp_id, u.metric_val",
         "  FROM (",
         "    SELECT a.emp_id, a.<delete select col D8> AS metric_val",
         "    FROM <[Blend Data].[Branch A D8]> a",
         "    WHERE a.<delete where col D8> > 0",
         "    UNION",
         "    SELECT b.emp_id, b.<delete select col D8> AS metric_val",
         "    FROM <[Blend Data].[Branch B D8]> b",
         "    WHERE b.<delete where col D8> > 0",
         "  ) u",
         "  INTERSECT",
         "  SELECT c.emp_id, c.<delete select col D8> AS metric_val",
         "  FROM <[Blend Data].[Branch C D8]> c",
         "  WHERE c.<delete where col D8> > 0",
         ") x",
         "WHERE e.emp_id = x.emp_id",
     ]),
    ("D9WithCteSelfUnionBranches",
     [
         "WITH blended AS (",
         "  SELECT a.emp_id, a.<delete select col D9a> AS metric_val",
         "  FROM <[Union Data].[Feed Alpha D9]> a",
         "  WHERE a.<delete where col D9a> > 0",
         "  UNION",
         "  SELECT b.emp_id, b.<delete select col D9b> AS metric_val",
         "  FROM <[Union Data].[Feed Beta D9]> b",
         "  WHERE b.<delete where col D9b> > 0",
         ")",
         "DELETE FROM employees e",
         "USING blended bl",
         "WHERE e.emp_id = bl.emp_id",
     ]),
    ("D10SubqueryGroupByHavingQualifyCombined",
     [
         "DELETE FROM employees e",
         "USING (",
         "  SELECT a.emp_id, sum(a.<delete select col D10>) AS total_score,",
         "         row_number() OVER (PARTITION BY a.emp_id ORDER BY a.<delete order col D10> DESC) AS rn",
         "  FROM <[Agg Data].[Fact Table D10]> a",
         "  WHERE a.<delete where col D10> > 0",
         "  GROUP BY a.emp_id, a.<delete group col D10>",
         "  HAVING sum(a.<delete select col D10>) > 0",
         "  QUALIFY rn = 1",
         ") src",
         "WHERE e.emp_id = src.emp_id",
     ]),
]


def sql_to_java_concat(lines: list[str]) -> str:
    parts = []
    for i, line in enumerate(lines):
        escaped = line.replace("\\", "\\\\").replace("\"", "\\\"")
        if i == 0:
            parts.append(f'"{escaped}"')
        else:
            parts.append(f'\n\t\t\t\t\t+ "\\n{escaped}"')
    return "".join(parts)


def java_escape(s: str) -> str:
    return s.replace("\\", "\\\\").replace("\"", "\\\"")


def write_probe(cases: list[tuple[str, str, list[str]]]) -> None:
    methods = []
    for prefix, suffix, lines in cases:
        method = f"insertComplexSubstitution{suffix}" if prefix == "insert" else (
            f"updateComplexSubstitution{suffix}" if prefix == "update" else f"deleteComplexSubstitution{suffix}")
        sql_java = sql_to_java_concat(lines)
        methods.append(textwrap.dedent(f"""
            @Test
            public void probe{method}() {{
                runProbe("{method}", {sql_java});
            }}
        """).strip())

    content = textwrap.dedent("""
        package sql.walker;

        import errorhandling.ParseDiagnostic;
        import org.junit.Test;
        import sql.SQLSelectParserParser;

        /** Temporary probe — regenerated by tools/generate_dml_substitution_tests.py */
        public class DmlSubstitutionGoldenProbe extends AbstractSqlParseEventWalkerTest {
        """) + "\n\n".join(methods) + textwrap.dedent("""

            private void runProbe(String name, String query) {
                SQLSelectParserParser parser = parse(query);
                SqlParseEventWalker extractor = runParsertest(query, parser);
                try {
                    assertNoWalkerDiagnostics(extractor);
                } catch (AssertionError e) {
                    System.out.println("DIAG_FAIL " + name);
                    System.out.println("QUERY: " + query);
                    System.out.println("FATAL: " + extractor.getSnippet().getFatalErrorStringList());
                    System.out.println("ERROR: " + extractor.getSnippet().getErrorStringList(ParseDiagnostic.Severity.ERROR));
                    System.out.println("WARN: " + extractor.getSnippet().getErrorStringList(ParseDiagnostic.Severity.SEVERE_WARNING));
                    throw e;
                }
                System.out.println("GOLDEN_START " + name);
                System.out.println("AST: " + extractor.getAsTree());
                System.out.println("Interface: " + extractor.getInterface());
                System.out.println("Substitution Variables: " + extractor.getSubstitutionsMap());
                System.out.println("Table Dictionary: " + extractor.getTableColumnDictionaryMap());
                System.out.println("Query Column Dictionary: " + extractor.getQueryColumnDictionaryMap());
                System.out.println("Symbol Tree: " + extractor.getSymbolTable());
                System.out.println("GOLDEN_END " + name);
            }
        }
        """)
    PROBE_JAVA.write_text(content)


def run_probe_and_capture() -> dict[str, dict[str, str]]:
    result = subprocess.run(
        ["mvn", "-q", "test", "-Dtest=DmlSubstitutionGoldenProbe"],
        cwd=PARSE,
        capture_output=True,
        text=True,
    )
    output = result.stdout + result.stderr
    (PARSE / "target/dml_probe_output.txt").write_text(output)

    goldens: dict[str, dict[str, str]] = {}
    current = None
    field = None
    for line in output.splitlines():
        if line.startswith("GOLDEN_START "):
            current = line[len("GOLDEN_START "):].strip()
            goldens[current] = {}
        elif line.startswith("GOLDEN_END "):
            current = None
            field = None
        elif current and line.startswith("DIAG_FAIL "):
            current = None
        elif current:
            if line.startswith("AST: "):
                goldens[current]["AST"] = line[5:]
            elif line.startswith("Interface: "):
                goldens[current]["Interface"] = line[11:]
            elif line.startswith("Substitution Variables: "):
                goldens[current]["Substitution"] = line[24:]
            elif line.startswith("Table Dictionary: "):
                goldens[current]["TableDictionary"] = line[18:]
            elif line.startswith("Query Column Dictionary: "):
                goldens[current]["QueryDictionary"] = line[25:]
            elif line.startswith("Symbol Tree: "):
                goldens[current]["SymbolTree"] = line[13:]
    return goldens


def render_test_methods(cases: list[tuple[str, str, list[str]]], goldens: dict[str, dict[str, str]]) -> str:
    chunks = []
    for prefix, suffix, lines in cases:
        method = f"insertComplexSubstitution{suffix}" if prefix == "insert" else (
            f"updateComplexSubstitution{suffix}" if prefix == "update" else f"deleteComplexSubstitution{suffix}")
        g = goldens.get(method)
        if not g:
            chunks.append(f"\n\t// SKIPPED {method} — no golden captured\n")
            continue
        sql_java = sql_to_java_concat(lines)
        chunks.append(
            "\t@Test\n"
            f"\tpublic void {method}() {{\n"
            f"\t\tfinal String query = {sql_java};\n"
            "\t\tfinal SQLSelectParserParser parser = parse(query);\n"
            "\t\tSqlParseEventWalker extractor = runParsertest(query, parser);\n"
            "\t\tassertNoWalkerDiagnostics(extractor);\n"
            "\n"
            '\t\tAssert.assertEquals("AST is wrong",\n'
            f'\t\t\t\t"{java_escape(g["AST"])}",\n'
            "\t\t\t\textractor.getAsTree().toString());\n"
            f'\t\tAssert.assertEquals("Interface is wrong", "{java_escape(g["Interface"])}",\n'
            "\t\t\t\textractor.getInterface().toString());\n"
            f'\t\tAssert.assertEquals("Substitution List is wrong", "{java_escape(g["Substitution"])}",\n'
            "\t\t\t\textractor.getSubstitutionsMap().toString());\n"
            '\t\tAssert.assertEquals("Table Dictionary is wrong",\n'
            f'\t\t\t\t"{java_escape(g["TableDictionary"])}",\n'
            "\t\t\t\textractor.getTableColumnDictionaryMap().toString());\n"
            '\t\tAssert.assertEquals("Query Column Dictionary is wrong",\n'
            f'\t\t\t\t"{java_escape(g["QueryDictionary"])}",\n'
            "\t\t\t\textractor.getQueryColumnDictionaryMap().toString());\n"
            '\t\tAssert.assertEquals("Symbol Table is wrong",\n'
            f'\t\t\t\t"{java_escape(g["SymbolTree"])}",\n'
            "\t\t\t\textractor.getSymbolTable().toString());\n"
            "\t}"
        )
    return "\n\n".join(chunks)


def main() -> None:
    all_cases: list[tuple[str, str, list[str]]] = []
    for suffix, lines in INSERT_CASES:
        all_cases.append(("insert", suffix, lines))
    for suffix, lines in UPDATE_CASES:
        all_cases.append(("update", suffix, lines))
    for suffix, lines in DELETE_CASES:
        all_cases.append(("delete", suffix, lines))

    CASES_JSON.parent.mkdir(parents=True, exist_ok=True)
    CASES_JSON.write_text(json.dumps(all_cases, indent=2))

    write_probe(all_cases)
    print(f"Wrote probe with {len(all_cases)} cases")
    goldens = run_probe_and_capture()
    print(f"Captured {len(goldens)} / {len(all_cases)} goldens")
    missing = [c[1] for c in all_cases if (
        (f"insertComplexSubstitution{c[1]}" if c[0]=='insert' else
         f"updateComplexSubstitution{c[1]}" if c[0]=='update' else f"deleteComplexSubstitution{c[1]}")
        not in goldens)]
    if missing:
        print("MISSING:", ", ".join(missing))
    methods = render_test_methods(all_cases, goldens)
    (PARSE / "target/dml_substitution_methods.generated").write_text(methods)
    dml_java = PARSE / "src/test/java/sql/walker/SqlEventWalkerDmlUpdateInsertDeleteTruncateTests.java"
    text = dml_java.read_text()
    marker = "\n// COMPLEX DML SUBSTITUTION TESTS"
    if marker in text:
        text = text[:text.index(marker)]
    text = text.rstrip()
    if text.endswith("}"):
        text = text[:-1].rstrip()
    merged = text.rstrip() + marker + " (generated — review before commit)\n\n" + methods + "\n}\n"
    dml_java.write_text(merged)
    print(f"Merged into {dml_java}")


if __name__ == "__main__":
    main()
