# Set-operation interface: duplicate output names within a branch

**API version:** 5.1.3  
**Status:** Policy (author guidance) — canonical parser behavior  
**Related diagnostics:** `DUPLICATE_INTERFACE_COLUMNS`, `SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH`  
**Adjudication:** Phase 2.9 cluster D (Panto CSV row **3150**) — **closed** 2026-09-01

---

## Decision

**5.1.3 behavior is canonical.** When a `UNION`, `UNION ALL`, `INTERSECT`, or `EXCEPT` participant’s select list assigns the **same output column name** to more than one position, the walker emits **FATAL** diagnostics. **5.0.0-3** accepted some of these shapes silently.

**No parser change is planned** to accommodate the live pattern in row 3150. Relaxing interface construction would weaken duplicate-column detection across the entire symbol-table pipeline. The supported corrective action is for the **query author** to add **disambiguating output aliases** on each select-list item in set-operation branches — the same remedy required in any other context where two expressions would otherwise share an interface name.

---

## Problem pattern (simplified)

A wide source CTE is unpivoted with `UNION ALL`. The **first** branch exposes four distinct output names. Later branches repeat the **same identifier** in two column positions (intended as separate logical slots such as `race` and `eab_race`), which collapses the branch interface.

### Wide source (abbreviated)

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

### Outer unpivot — **problematic** (matches row 3150 outer `UNION ALL`)

```sql
SELECT primary_student_id, race, eab_race, intake_dt
FROM (
    -- Branch 1: four distinct output names (reference interface)
    SELECT
        primary_student_id,
        race,
        eab_race,
        intake_dt
    FROM race_data
    WHERE eab_race IS NOT NULL AND race <> ''

    UNION ALL

    -- Branch 2: same name twice → DUPLICATE_INTERFACE_COLUMNS;
    -- branch interface collapses to 3 names → SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH
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

**5.1.3 FATALs (representative):**

| Code | Meaning |
|------|---------|
| `DUPLICATE_INTERFACE_COLUMNS` | Two select items in the same branch resolve to the same output name (e.g. both `eab_race_amer_indian`). |
| `SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH` | A later branch’s **distinct** output-name count does not match the first branch (e.g. expected 4 names, saw 3). |

**Note:** SQL engines often align `UNION ALL` by **position**; the query may still execute. PSS validates **interface contracts** for lineage, nested scopes, and set-op composition — duplicate names within a branch break that model.

---

## Corrective action (author)

In **each** `UNION` / `UNION ALL` / `INTERSECT` / `EXCEPT` branch, give every select-list item a **unique output alias** so no two positions share the same interface name. Reuse expressions freely; only the **published output names** must differ.

### Fixed branches (same logic, disambiguated aliases)

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

After this change, each branch exposes `(primary_student_id, race, eab_race, intake_dt)` with **four distinct names**, matching the first branch. The outer `SELECT` over `agg` is unchanged.

Apply the same pattern to every race-specific branch in the live query.

---

## Live fixture reference

Full partner SQL: `parse/docs/rmcp-handoff/5.1.3-panto-outstanding/sql/csv-row-3150.sql`  
Cluster D dual-parse adjudication: outer `UNION ALL` lines **239–331**.

**Cluster F (row 5410):** `sql/csv-row-5410.sql` — `UNION ALL` at line **190**; branch 2 duplicate names at **201/215** (`interested_institutions_list`) and **224/232** (`desired_completion_timeframe`). `REGEXP` / `UUID_STRING()` at lines 34–35 do not FATAL.

---

## Consumer guidance

- Treat retained FATALs on this pattern as **expected** under 5.1.3; do not request 5.0.0-3 parity.
- For RMCP / comparison tooling: row 3150 is **not** a 5.1.3 regression; document as **improved validation**.
- Do not strip or relax `interface` duplicate detection to absorb this construction.
