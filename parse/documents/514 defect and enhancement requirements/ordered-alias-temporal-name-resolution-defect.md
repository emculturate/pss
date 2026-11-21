# Ordered Alias Temporal-Name Resolution Defect

Date: 2026-11-20

Classification: **confirmed 5.1.4 defect**

Derived from successful bound-query row 276:
`ALR/Inquiry/transformation_query_inquiries`.

## Primary reproduction

Endpoint: `SQL`

Complete input:

```sql
select right(BIRTH_YEAR, 2) BIRTH_DAY,
       substring(BIRTH_DAY, 6, 2) BIRTH_MONTH
from <[ALR].[Inquiry]> as ad
```

`BIRTH_DAY` is a grounded output alias defined before `BIRTH_MONTH`. Under the
locked ordered select-list alias policy, expected lineage is:

```text
BIRTH_DAY   -> <[ALR].[Inquiry]>.BIRTH_YEAR
BIRTH_MONTH -> query0.BIRTH_DAY
```

5.1.4 returns:

```text
BIRTH_DAY   -> <[ALR].[Inquiry]>.BIRTH_YEAR
BIRTH_MONTH -> <[ALR].[Inquiry]>.BIRTH_DAY
```

The local and global table dictionaries contain physical `BIRTH_YEAR` but not
physical `BIRTH_DAY`. The payload is therefore internally inconsistent: the
interface labels `BIRTH_DAY` as a physical dependency while the table
dictionary correctly treats it as an output alias.

No diagnostic is emitted.

## Derived-table reproduction

Endpoint: `SQL`

Complete input:

```sql
select *
from (
  select right(BIRTH_YEAR, 2) BIRTH_DAY,
         substring(BIRTH_DAY, 6, 2) BIRTH_MONTH
  from <[ALR].[Inquiry]> as ad
) C
```

The nested `def_query0.interface.BIRTH_MONTH` has the same incorrect physical
`table_ref`. Explicit outer projections, CTE wrappers, and additional nesting
preserve the incorrect child lineage; they are not the root trigger.

## Name and case sensitivity

The expression shape works with an ordinary alias:

```sql
select right(src, 2) NORMAL_ALIAS,
       substring(NORMAL_ALIAS, 1, 1) out_col
from <[D].[T]> t
```

5.1.4 correctly returns `out_col -> query0.NORMAL_ALIAS`.

Temporal-looking aliases produce inconsistent results:

- `birth_day` and `Birth_Day`: resolve to `query0`.
- `BIRTH_DAY`: incorrectly resolves to the physical table.
- `end_date`: resolves to `query0`.
- `End_Date` and `END_DATE`: incorrectly resolve to the physical table.
- `start_date`, `START_DATE`, `day`, and `DAY`: incorrectly resolve to the
  physical table.
- `BIRTH_MONTH`, `MONTH`, `YEAR`, and `DATE` resolve correctly in the tested
  alias-consumer shape.

Qualifying the defining source column happened to restore `query0` for
`BIRTH_DAY` in one control. That does not justify the unqualified result:
alias-consumer resolution should not change merely because the defining
physical input is qualified. The failure still follows the output alias
name/casing rather than an ungrounded origin.

## Passing origin-shape controls

With ordinary alias names, 5.1.4 correctly stamps `table_ref=query0` for prior
aliases originating from:

- scalar functions;
- arithmetic;
- `CAST`;
- `CASE`;
- plain columns; and
- predicand substitutions.

The defect is therefore not a general failure of ordered alias grounding.
It appears to be a temporal-name classification or precedence path overriding
the earlier grounded output alias.

## Impact

Consumers following `interface` lineage are directed to a nonexistent physical
column instead of the earlier output alias. This can terminate lineage at the
wrong source even though the table dictionary itself remains correct.

## Parser-agent guidance

Inspect output-alias resolution before temporal pseudo-column or contextual
date-name classification. Earlier grounded output aliases must take precedence
case-insensitively and be stamped `{name=<alias>, table_ref=queryN}` regardless
of names such as `BIRTH_DAY`, `END_DATE`, `START_DATE`, or `DAY`.

Add controls covering lowercase, mixed-case, and uppercase forms because the
current behavior is not case invariant.
