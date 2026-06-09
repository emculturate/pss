#!/usr/bin/env python3
"""Query definitions for correlated subquery diagnostic tests (30 total).

Each query is a list of lines (~60 characters) joined with newlines at runtime.
"""

# Each entry: (method_name, [line, ...])
PREDICAND_NO_CTE = [
    (
        "correlatedScalarPredicandNestedJoinSubqueryTest",
        [
            "SELECT oa.pd1, oa.pd2 FROM tab_a AS oa",
            "WHERE oa.pd3 = (SELECT ib.pd9 FROM tab_b AS ib",
            "JOIN (SELECT ic.pd7 FROM tab_c AS ic",
            "        WHERE ic.pd7 = oa.pd1) AS ix",
            "        ON ix.pd7 = ib.pd6)",
        ],
    ),
    (
        "correlatedScalarPredicandUnionContextSubqueryTest",
        [
            "SELECT ua.pu1 FROM tab_a AS ua",
            "WHERE ua.pu2 = (SELECT max(sub.pu4) FROM (",
            "SELECT ub.pu4 FROM tab_b AS ub",
            "        WHERE ub.pu5 = ua.pu1",
            "UNION SELECT uc.pu4 FROM tab_c AS uc",
            "        WHERE uc.pu6 = ua.pu1) AS sub)",
        ],
    ),
    (
        "correlatedScalarPredicandIntersectContextSubqueryTest",
        [
            "SELECT oi.oi1,",
            "       (SELECT max(ii.px1) FROM (",
            "SELECT id.px1 FROM tab_d AS id",
            "        WHERE id.px2 = oi.oi1",
            "INTERSECT SELECT ie.px1 FROM tab_e AS ie",
            "        WHERE ie.px3 = oi.oi2) AS ii) AS px_max",
            "FROM tab_o AS oi",
        ],
    ),
    (
        "correlatedScalarPredicandWithNestedInSubqueryTest",
        [
            "SELECT sa.sv1 FROM tab_s AS sa",
            "WHERE (SELECT max(ia.iv1) FROM tab_i AS ia",
            "        WHERE ia.iv2 IN (",
            "SELECT jb.jv1 FROM tab_j AS jb",
            "        WHERE jb.jv2 = sa.sv2)) > sa.sv3",
        ],
    ),
    (
        "correlatedScalarPredicandWithNestedExistsSubqueryTest",
        [
            "SELECT ea.ev1 FROM tab_e AS ea",
            "WHERE (SELECT count(ex.ex1) FROM tab_x AS ex",
            "        WHERE ex.ex2 = ea.ev2 AND EXISTS (",
            "SELECT 1 FROM tab_y AS ey",
            "        WHERE ey.ey1 = ex.ex3",
            "          AND ey.ey2 = ea.ev1)) > 0",
        ],
    ),
]

PREDICAND_CTE = [
    (
        "correlatedScalarPredicandFirstCteStandaloneTest",
        [
            "WITH c1a AS (",
            "  SELECT ta.t1c1 FROM tab1 AS ta",
            "  WHERE ta.t1c1 = (SELECT max(tb.t2c1) FROM tab2 AS tb)",
            ")",
            "SELECT c1a.t1c1 FROM c1a",
        ],
    ),
    (
        "correlatedScalarPredicandMiddleCteReferencesFirstCteTest",
        [
            "WITH w1 AS (SELECT aa.a1, aa.a2 FROM tab_a AS aa),",
            "w2 AS (SELECT bb.b1 FROM tab_b AS bb",
            "       WHERE bb.b1 = (SELECT max(ww.a1) FROM w1 AS ww",
            "                      WHERE ww.a2 = bb.b2))",
            "SELECT w2.b1 FROM w2",
        ],
    ),
    (
        "correlatedScalarPredicandLastCteReferencesPriorCtesTest",
        [
            "WITH ca AS (SELECT xa.x1, xa.x2 FROM tab_x AS xa),",
            "cb AS (SELECT yb.y1 FROM tab_y AS yb),",
            "cc AS (SELECT zc.z1 FROM tab_z AS zc",
            "       WHERE zc.z2 = (SELECT ca.x1 FROM ca",
            "                      WHERE ca.x2 = zc.z3))",
            "SELECT cc.z1 FROM cc",
        ],
    ),
    (
        "correlatedScalarPredicandFinalQueryReferencesCteChainTest",
        [
            "WITH fa AS (SELECT pa.p1, pa.p2 FROM tab_p AS pa),",
            "fb AS (SELECT qb.q1 FROM tab_q AS qb)",
            "SELECT pa.p1,",
            "       (SELECT max(ff.p2) FROM fa AS ff",
            "        WHERE ff.p1 = pa.p1) AS p2_max",
            "FROM fa AS pa JOIN fb ON pa.p1 = fb.q1",
        ],
    ),
    (
        "correlatedScalarPredicandNestedCteWithOuterRefTest",
        [
            "WITH oa AS (SELECT ra.r1 FROM tab_r AS ra),",
            "ob AS (WITH ib AS (SELECT sb.s1 FROM tab_s AS sb)",
            "       SELECT tb.t1 FROM tab_t AS tb",
            "       WHERE tb.t2 = (SELECT max(ib.s1) FROM ib",
            "                      WHERE ib.s1 = oa.r1))",
            "SELECT ob.t1 FROM ob JOIN oa ON oa.r1 = ob.t1",
        ],
    ),
]

IN_NO_CTE = [
    (
        "correlatedInSubqueryNestedJoinSubqueryTest",
        [
            "SELECT oa.in1 FROM tab_a AS oa",
            "WHERE oa.in2 IN (SELECT ib.in9 FROM tab_b AS ib",
            "JOIN (SELECT ic.in7 FROM tab_c AS ic",
            "        WHERE ic.in8 = oa.in1) AS ix",
            "        ON ix.in7 = ib.in6)",
        ],
    ),
    (
        "correlatedInSubqueryUnionContextTest",
        [
            "SELECT ua.iu1 FROM tab_a AS ua",
            "WHERE ua.iu2 IN (SELECT sub.iu4 FROM (",
            "SELECT ub.iu4 FROM tab_b AS ub",
            "        WHERE ub.iu5 = ua.iu1",
            "UNION SELECT uc.iu4 FROM tab_c AS uc",
            "        WHERE uc.iu6 = ua.iu3) AS sub)",
        ],
    ),
    (
        "correlatedInSubqueryIntersectContextTest",
        [
            "SELECT oi.ix1 FROM tab_o AS oi",
            "WHERE oi.ix2 IN (SELECT ii.ix9 FROM (",
            "SELECT id.ix9 FROM tab_d AS id",
            "        WHERE id.ix3 = oi.ix1",
            "INTERSECT SELECT ie.ix9 FROM tab_e AS ie",
            "        WHERE ie.ix4 = oi.ix2) AS ii)",
        ],
    ),
    (
        "correlatedInSubqueryWithNestedScalarPredicandTest",
        [
            "SELECT sa.in1 FROM tab_s AS sa",
            "WHERE sa.in2 IN (SELECT ia.in9 FROM tab_i AS ia",
            "                 WHERE ia.in3 = (",
            "SELECT max(jb.jx1) FROM tab_j AS jb",
            "                 WHERE jb.jx2 = sa.in1))",
        ],
    ),
    (
        "correlatedInSubqueryWithNestedExistsTest",
        [
            "SELECT ea.in1 FROM tab_e AS ea",
            "WHERE ea.in2 IN (SELECT ex.in9 FROM tab_x AS ex",
            "                 WHERE EXISTS (",
            "SELECT 1 FROM tab_y AS ey",
            "                 WHERE ey.ey1 = ex.in8",
            "                   AND ey.ey2 = ea.in1))",
        ],
    ),
]

IN_CTE = [
    (
        "correlatedInSubqueryFirstCteStandaloneTest",
        [
            "WITH c1a AS (",
            "  SELECT ta.t1c1 FROM tab1 AS ta",
            "  WHERE ta.t1c1 IN (SELECT tb.t2c1 FROM tab2 AS tb)",
            ")",
            "SELECT c1a.t1c1 FROM c1a",
        ],
    ),
    (
        "correlatedInSubqueryMiddleCteReferencesFirstCteTest",
        [
            "WITH w1 AS (SELECT aa.a1, aa.a2 FROM tab_a AS aa),",
            "w2 AS (SELECT bb.b1 FROM tab_b AS bb",
            "       WHERE bb.b1 IN (SELECT ww.a1 FROM w1 AS ww",
            "                       WHERE ww.a2 = bb.b2))",
            "SELECT w2.b1 FROM w2",
        ],
    ),
    (
        "correlatedInSubqueryLastCteReferencesPriorCtesTest",
        [
            "WITH ca AS (SELECT xa.x1, xa.x2 FROM tab_x AS xa),",
            "cb AS (SELECT yb.y1 FROM tab_y AS yb),",
            "cc AS (SELECT zc.z1 FROM tab_z AS zc",
            "       WHERE zc.z2 IN (SELECT ca.x1 FROM ca",
            "                       WHERE ca.x2 = zc.z3))",
            "SELECT cc.z1 FROM cc",
        ],
    ),
    (
        "correlatedInSubqueryFinalQueryReferencesCteChainTest",
        [
            "WITH fa AS (SELECT pa.p1, pa.p2 FROM tab_p AS pa),",
            "fb AS (SELECT qb.q1 FROM tab_q AS qb)",
            "SELECT pa.p1 FROM fa AS pa JOIN fb ON pa.p1 = fb.q1",
            "WHERE pa.p2 IN (SELECT ff.p2 FROM fa AS ff",
            "                WHERE ff.p1 = pa.p1)",
        ],
    ),
    (
        "correlatedInSubqueryNestedCteWithOuterRefTest",
        [
            "WITH oa AS (SELECT ra.r1 FROM tab_r AS ra),",
            "ob AS (WITH ib AS (SELECT sb.s1 FROM tab_s AS sb)",
            "       SELECT tb.t1 FROM tab_t AS tb",
            "       WHERE tb.t2 IN (SELECT ib.s1 FROM ib",
            "                       WHERE ib.s1 = oa.r1))",
            "SELECT ob.t1 FROM ob JOIN oa ON oa.r1 = ob.t1",
        ],
    ),
]

EXISTS_NO_CTE = [
    (
        "correlatedExistsSubqueryNestedJoinSubqueryTest",
        [
            "SELECT oa.ex1 FROM tab_a AS oa",
            "WHERE EXISTS (SELECT 1 FROM tab_b AS ib",
            "JOIN (SELECT ic.ex7 FROM tab_c AS ic",
            "        WHERE ic.ex8 = oa.ex2) AS ix",
            "        ON ix.ex7 = ib.ex6)",
        ],
    ),
    (
        "correlatedExistsSubqueryUnionContextTest",
        [
            "SELECT ua.eu1 FROM tab_a AS ua",
            "WHERE EXISTS (SELECT 1 FROM (",
            "SELECT ub.eu4 FROM tab_b AS ub",
            "        WHERE ub.eu5 = ua.eu1",
            "UNION SELECT uc.eu4 FROM tab_c AS uc",
            "        WHERE uc.eu6 = ua.eu2) AS sub",
            "        WHERE sub.eu4 = ua.eu3)",
        ],
    ),
    (
        "correlatedExistsSubqueryIntersectContextTest",
        [
            "SELECT oi.ex1 FROM tab_o AS oi",
            "WHERE EXISTS (SELECT 1 FROM (",
            "SELECT id.ex9 FROM tab_d AS id",
            "        WHERE id.ex3 = oi.ex1",
            "INTERSECT SELECT ie.ex9 FROM tab_e AS ie",
            "        WHERE ie.ex4 = oi.ex2) AS ii",
            "        WHERE ii.ex9 = oi.ex3)",
        ],
    ),
    (
        "correlatedExistsSubqueryWithNestedScalarPredicandTest",
        [
            "SELECT sa.ex1 FROM tab_s AS sa",
            "WHERE EXISTS (SELECT 1 FROM tab_i AS ia",
            "              WHERE ia.ex9 = (",
            "SELECT max(jb.jx1) FROM tab_j AS jb",
            "              WHERE jb.jx2 = sa.ex2))",
        ],
    ),
    (
        "correlatedExistsSubqueryWithNestedInSubqueryTest",
        [
            "SELECT ea.ex1 FROM tab_e AS ea",
            "WHERE EXISTS (SELECT 1 FROM tab_x AS ex",
            "              WHERE ex.ex8 IN (",
            "SELECT ey.ey1 FROM tab_y AS ey",
            "              WHERE ey.ey2 = ea.ex1))",
        ],
    ),
]

EXISTS_CTE = [
    (
        "correlatedExistsSubqueryFirstCteStandaloneTest",
        [
            "WITH c1a AS (",
            "  SELECT ta.t1c1 FROM tab1 AS ta",
            "  WHERE EXISTS (SELECT 1 FROM tab2 AS tb",
            "                WHERE tb.t2c1 = ta.t1c1)",
            ")",
            "SELECT c1a.t1c1 FROM c1a",
        ],
    ),
    (
        "correlatedExistsSubqueryMiddleCteReferencesFirstCteTest",
        [
            "WITH w1 AS (SELECT aa.a1, aa.a2 FROM tab_a AS aa),",
            "w2 AS (SELECT bb.b1 FROM tab_b AS bb",
            "       WHERE EXISTS (SELECT 1 FROM w1 AS ww",
            "                     WHERE ww.a1 = bb.b1",
            "                       AND ww.a2 = bb.b2))",
            "SELECT w2.b1 FROM w2",
        ],
    ),
    (
        "correlatedExistsSubqueryLastCteReferencesPriorCtesTest",
        [
            "WITH ca AS (SELECT xa.x1, xa.x2 FROM tab_x AS xa),",
            "cb AS (SELECT yb.y1 FROM tab_y AS yb),",
            "cc AS (SELECT zc.z1 FROM tab_z AS zc",
            "       WHERE EXISTS (SELECT 1 FROM ca",
            "                     WHERE ca.x2 = zc.z3",
            "                       AND ca.x1 = zc.z2))",
            "SELECT cc.z1 FROM cc",
        ],
    ),
    (
        "correlatedExistsSubqueryFinalQueryReferencesCteChainTest",
        [
            "WITH fa AS (SELECT pa.p1, pa.p2 FROM tab_p AS pa),",
            "fb AS (SELECT qb.q1 FROM tab_q AS qb)",
            "SELECT pa.p1 FROM fa AS pa JOIN fb ON pa.p1 = fb.q1",
            "WHERE EXISTS (SELECT 1 FROM fa AS ff",
            "              WHERE ff.p1 = pa.p1 AND ff.p2 = pa.p2)",
        ],
    ),
    (
        "correlatedExistsSubqueryNestedCteWithOuterRefTest",
        [
            "WITH oa AS (SELECT ra.r1 FROM tab_r AS ra),",
            "ob AS (WITH ib AS (SELECT sb.s1 FROM tab_s AS sb)",
            "       SELECT tb.t1 FROM tab_t AS tb",
            "       WHERE EXISTS (SELECT 1 FROM ib",
            "                     WHERE ib.s1 = oa.r1",
            "                       AND ib.s1 = tb.t2))",
            "SELECT ob.t1 FROM ob JOIN oa ON oa.r1 = ob.t1",
        ],
    ),
]

ALL_TESTS = (
    PREDICAND_NO_CTE
    + PREDICAND_CTE
    + IN_NO_CTE
    + IN_CTE
    + EXISTS_NO_CTE
    + EXISTS_CTE
)


def query_sql(lines: list[str]) -> str:
    """Join line chunks into the SQL string the parser sees."""
    return "\n".join(lines)


def all_tests_with_sql() -> list[tuple[str, str, list[str]]]:
    return [(method, query_sql(lines), lines) for method, lines in ALL_TESTS]
