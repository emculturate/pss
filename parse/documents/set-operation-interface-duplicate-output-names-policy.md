# Set-operation branch interface distinctness (duplicate output names)

**API version:** 5.1.3  
**Status:** Policy (author guidance) — canonical parser behavior  
**Related diagnostics:** `DUPLICATE_INTERFACE_COLUMNS`, `SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH`, `DUPLICATE_INTERFACE_COLUMN_FIRST`, `DUPLICATE_INTERFACE_COLUMN`  
**Adjudication:** Phase 2.9 clusters D and F (Panto CSV rows **3150**, **5410**) — **closed** 2026-09-01

---

## Purpose

PSS **5.1.3** validates **set-operation** queries (`UNION`, `UNION ALL`, `INTERSECT`, `EXCEPT`) by aligning the **output interface** of each branch. The interface is keyed by **output column name** — one key per distinct name in the branch select list.

This document explains:

- How branch **interface width** is computed (distinct names, not select-list positions).
- A common author mistake: repeating the same output name twice in one branch.
- The **diagnostic cascade** (`DUPLICATE_INTERFACE_COLUMNS` → collapsed interface → `SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH`).
- Concrete SQL fixes and a branch-wide checklist for authors and tooling agents.

---

## Decision (canonical behavior)

**5.1.3 behavior is canonical.** When a set-operation participant’s select list assigns the **same output column name** to more than one position, the walker emits **FATAL** diagnostics. **5.0.0-3** accepted some of these shapes silently.

**No parser change is planned** to accommodate live patterns in rows 3150 and 5410. Relaxing interface construction would weaken duplicate-column detection across the entire symbol-table pipeline.

The supported corrective action is for the **query author** to add **disambiguating output aliases** on select-list items in set-operation branches — the same remedy required anywhere two expressions would otherwise share an interface name.

This is **author-fixable SQL hygiene**, not a signal to disable set-op validation.

---

## Background: interface vs select-list position

For each `queryN` / `def_queryN` scope, the parser builds an **`interface`** map:

- Keys are **output column names** (from explicit `AS alias`, or inferred from unqualified/qualified column references and expressions).
- Values are deduplicated token reference lists for lineage.

The **number of interface keys** in a branch is therefore the number of **distinct output names**, not necessarily the number of comma-separated select items.

When finalize aligns set-operation branches, it compares **interface width** (and column-name compatibility) across branches. A branch with 87 select items but only 85 distinct interface names is treated as an **85-column** branch for set-op alignment.

**Note:** SQL engines often align `UNION ALL` by **position**; the query may still execute. PSS validates **interface contracts** for lineage, nested scopes, and set-op composition — duplicate names within a branch break that model.

---

## How errors appear

### Symptom sequence

1. **`DUPLICATE_INTERFACE_COLUMNS` (FATAL)** — two or more select items in the **same branch** resolve to the **same interface column name** (often the same qualified column reference repeated, e.g. `cc_pdp.interested_institutions_list` at two positions).

2. **`SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH` (FATAL)** — set-op finalize reports mismatched branch widths, e.g. “UNION has different column counts. Expected **87** columns (…) but there were **85** (…)”.

The miscount is frequently a **cascade** of step 1: each duplicate name collapses to one interface key, shrinking the effective branch width even though the raw `SELECT` list still lists more expressions.

Treat `SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH` that accompanies duplicate-interface fatals on the **same query** as a **cascade miscount**, not an independent regression.

### Related diagnostics

| Code | Meaning |
|------|---------|
| `DUPLICATE_INTERFACE_COLUMNS` | Same output interface name from two select positions in one scope |
| `SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH` | Branch interfaces differ in width or cannot be aligned |
| `DUPLICATE_INTERFACE_COLUMN_FIRST` / `DUPLICATE_INTERFACE_COLUMN` | RMCP-normalized splits of the duplicate-interface message for tooling |

**Separate issue:** `SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH` when one side uses wildcard `SELECT *` is a different known defect (parser workplan Phase 2.1). The patterns in this document apply when **both** sides use explicit column lists (or inferred names from explicit references).

### What 5.1.3 is telling you

The SQL may execute on some engines, but the **symbol table cannot assign a stable, distinct output name** to every select position without explicit aliases. The parser is rejecting ambiguous interface shape before lineage and dictionary consumers run.

---

## Problem patterns

### Pattern A — repeated inferred column name (minimal)

```sql
SELECT
  1 AS rn,
  s.col_a,
  s.col_b
UNION ALL
SELECT
  1 AS rn,
  t.col_a,
  t.col_b,
  t.col_a          -- duplicate interface name `col_a`
FROM ...
```

Branch 2 has four select items but three distinct interface names. If branch 1 has three interface columns, finalize may emit `SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH` even though both `SELECT` lists “look” the same width to the author.

### Pattern B — unpivot with duplicate source identifiers (row 3150)

A wide source CTE is unpivoted with `UNION ALL`. The **first** branch exposes four distinct output names. Later branches repeat the **same identifier** in two column positions (intended as separate logical slots such as `race` and `eab_race`), which collapses the branch interface.

**Wide source (abbreviated):**

```sql
WITH race_data AS (
    SELECT
        s.student_id   AS primary_student_id,
        s.race_label   AS race,
        s.eab_race     AS eab_race,
        s.amer_indian  AS eab_race_amer_indian,
        s.asian        AS eab_race_asian,
        s.intake_dt    AS intake_dt
    FROM source_table AS s
)
```

**Outer unpivot — problematic (matches row 3150 outer `UNION ALL`):**

```sql
SELECT primary_student_id, race, eab_race, intake_dt
FROM (
  SELECT primary_student_id, race, eab_race, intake_dt
  FROM race_data
  WHERE eab_race IS NOT NULL AND race <> ''

  UNION ALL

  -- Branch 2: same name twice → DUPLICATE_INTERFACE_COLUMNS;
  -- interface collapses to 3 names → SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH
  SELECT
      primary_student_id,
      eab_race_amer_indian,
      eab_race_amer_indian,
      intake_dt
  FROM race_data
  WHERE eab_race_amer_indian IS NOT NULL

  UNION ALL

  -- Branch 3: duplicate name on CASE output (Asian branch in live query)
  SELECT
      primary_student_id,
      eab_race_asian,
      CASE
          WHEN eab_race_asian IN ('Asian/Asian American', 'Asian') THEN 'Asian'
          ELSE NULL
      END AS eab_race_asian,
      intake_dt
  FROM race_data
  WHERE eab_race_asian IS NOT NULL
) AS agg;
```

### Pattern C — repeated qualified columns in a wide branch (row 5410)

In a wide `UNION ALL`, the same qualified column may appear at multiple positions without aliases, e.g. `t.interested_institutions_list` at two select-list positions and `t.desired_completion_timeframe` at two others. Each pair triggers `DUPLICATE_INTERFACE_COLUMNS` and can shrink the branch interface relative to sibling branches.

```sql
-- Before (branch 2)
t.interested_institutions_list,
...
t.interested_institutions_list,

-- After
t.interested_institutions_list,
...
t.interested_institutions_list AS interested_institutions_list_2,
```

---

## Author guidance: make every output name distinct

### Rule

In **every** branch of a set-operation query, ensure **each select-list item has a unique output interface name** within that branch.

> **Alias every select-list item** in set-op branches with an explicit `AS output_name` that is unique within the branch.

Reuse expressions freely; only the **published output names** must differ. When columns are meant to align positionally across branches, use the **same alias on the corresponding item in every branch** (e.g. both branches expose `race` and `eab_race` at the same positions).

### Fix pattern

For each duplicate reported by `DUPLICATE_INTERFACE_COLUMNS`:

1. Keep the **first** occurrence as-is (or with the canonical alias used across branches).
2. On **later** occurrences of the same inferred name, add a distinct alias — or, when unpivoting, map source columns to the **branch’s target interface names** explicitly.

**Unpivot fix (row 3150 — same logic, disambiguated aliases):**

```sql
    UNION ALL

    SELECT
        primary_student_id,
        eab_race_amer_indian AS race,
        eab_race_amer_indian AS eab_race,
        intake_dt
    FROM race_data
    WHERE eab_race_amer_indian IS NOT NULL

    UNION ALL

    SELECT
        primary_student_id,
        eab_race_asian AS race,
        CASE
            WHEN eab_race_asian IN ('Asian/Asian American', 'Asian') THEN 'Asian'
            ELSE NULL
        END AS eab_race,
        intake_dt
    FROM race_data
    WHERE eab_race_asian IS NOT NULL
```

After this change, each branch exposes `(primary_student_id, race, eab_race, intake_dt)` with **four distinct names**, matching the first branch. The outer `SELECT` over `agg` is unchanged. Apply the same pattern to every race-specific branch in the live query.

**Alias naming:**

- Stay **stable across branches** when columns align positionally in the set-op.
- Stay **unique within each branch** (never reuse the same output name twice in one branch).
- Prefer lowercase snake_case unless the project standard dictates otherwise.

### Branch-wide checklist

Before submitting set-op SQL to PSS 5.1.3:

1. List each branch’s select items.
2. Confirm **no output name repeats** within a branch.
3. Confirm **branch interfaces have the same width** and compatible names positionally (or use explicit column lists on the set-op if your dialect requires it).
4. Prefer `expression AS alias` on **every** item in wide `UNION` branches — not only duplicates — to make reviews and diffs easier.

### When an agent assists the author

1. Run parse with endpoint `SQL` (or `QUERY` for a single branch fragment) on profile **5.1.3**.
2. Collect `DUPLICATE_INTERFACE_COLUMNS` and any `SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH` diagnostics.
3. Explain the cascade (duplicate names → collapsed interface → miscount).
4. **Propose concrete `AS alias` edits** on every duplicate occurrence after the first, preserving commas and branch structure.
5. Re-parse after edits until duplicate-interface and miscount fatals clear.

---

## Live fixture reference

| Cluster | Row | Pattern | Fixture |
|---------|-----|---------|---------|
| D | 3150 | Outer `UNION ALL` unpivot; duplicate inferred names in branches 2–3 | `parse/docs/rmcp-handoff/5.1.3-panto-outstanding/sql/csv-row-3150.sql` (outer `UNION ALL` lines **239–331**) |
| F | 5410 | Wide `UNION ALL`; branch 2 duplicates at **201/215** (`interested_institutions_list`) and **224/232** (`desired_completion_timeframe`) | `parse/docs/rmcp-handoff/5.1.3-panto-outstanding/sql/csv-row-5410.sql` (`UNION ALL` at line **190**) |

`REGEXP` / `UUID_STRING()` at lines 34–35 of row 5410 do not FATAL; the set-op interface issues are independent.

---

## Consumer guidance (RMCP / migration compare)

When comparing **5.0.0-3** (silent) to **5.1.3**:

- New `DUPLICATE_INTERFACE_COLUMNS` fatals indicate **latent SQL ambiguity** now surfaced — score as **author action**, not parser regression.
- Rows **3150** and **5410** are **not** 5.1.3 regressions; document as **improved validation**.
- Do not strip or relax `interface` duplicate detection to absorb these constructions.
- Do not request 5.0.0-3 parity for retained FATALs on this pattern.

---

## See also

- `table-and-query-dictionary-design.md` — interface key semantics
- `symbol-table-bucket-reference.md` — `def_unionN` / `query_column` alignment
- `ordered-select-list-output-alias-policy.md` — intra-select-list alias resolution order
