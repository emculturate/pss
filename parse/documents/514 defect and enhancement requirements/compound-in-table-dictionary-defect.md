# Compound IN Table-Dictionary Defect

Date: 2026-11-20

Classification: **confirmed 5.1.4 defect**

This is a black-box reproduction derived from Panto snippet row 9922. The
parser recognizes the complete expression and produces complete output
interface lineage, but its local and global table dictionaries omit operands.
No diagnostic reports the omission.

## Minimal primary reproduction

Endpoint: `SQL`

Complete input:

```sql
select case
         when ('x' IN (a1, a2) and 'y' IN (b1, b2))
         then 1
         else 0
       end as flag
from <[D].[T]> t
```

Expected table dictionary:

```text
<[D].[T]>: a1, a2, b1, b2
```

5.0.0-3 table dictionary:

```text
<[D].[T]>: a1, a2, b1, b2
```

5.1.4 local and global table dictionaries:

```text
<[D].[T]>: b1, b2
```

The 5.1.4 `def_query0.interface.flag` lineage is nevertheless complete:

```text
a1 -> <[D].[T]>
a2 -> <[D].[T]>
b1 -> <[D].[T]>
b2 -> <[D].[T]>
```

This internal disagreement demonstrates that parsing and interface lineage
succeed, but table-dictionary collection drops the left operand.

## Operand-order reproduction

Endpoint: `SQL`

Complete input:

```sql
select case
         when ('y' IN (b1, b2) and 'x' IN (a1, a2))
         then 1
         else 0
       end as flag
from <[D].[T]> t
```

5.1.4 now records only `a1, a2`. The retained columns therefore follow the
right-hand `IN` operand rather than column identity.

## OR reproduction

Endpoint: `SQL`

Complete input:

```sql
select case
         when ('x' IN (a1, a2) or 'y' IN (b1, b2))
         then 1
         else 0
       end as flag
from <[D].[T]> t
```

5.1.4 again records only `b1, b2`. Swapping the operands causes it to record
only `a1, a2`. The defect therefore affects both `AND` and `OR`.

## Mixed-predicate reproduction

Endpoint: `SQL`

Complete input:

```sql
select case
         when (b1 = 2 and 'x' IN (a1, a2))
         then 1
         else 0
       end as flag
from <[D].[T]> t
```

Expected and 5.0.0-3 table dictionary:

```text
<[D].[T]>: a1, a2, b1
```

5.1.4 table dictionary:

```text
<[D].[T]>: a1, a2
```

The equality column on the left is lost when the right operand is `IN`.

Reversing the operands is a passing control:

```sql
select case
         when ('x' IN (a1, a2) and b1 = 2)
         then 1
         else 0
       end as flag
from <[D].[T]> t
```

5.1.4 records `a1, a2, b1` for this control. The trigger is therefore not
compound conditions generally; it is a right-hand `IN` predicate replacing
rather than merging prior table-dictionary contributions.

## Direct boolean output

`CASE` is not required. A compound boolean used directly as a selected output
reproduces the loss.

Endpoint: `SQL`

Complete input:

```sql
select id,
       ('x' IN (a1, a2) and 'y' IN (b1, b2)) as both_match,
       name
from <[D].[T]> t
```

Expected and 5.0.0-3 table dictionary:

```text
<[D].[T]>: id, name, a1, a2, b1, b2
```

5.1.4 table dictionary:

```text
<[D].[T]>: id, name, b1, b2
```

The `both_match` interface lineage still contains all four predicate columns.
`OR` reproduces the same loss.

Multiple independent boolean outputs, each containing only one `IN`, are a
passing control:

```sql
select id,
       'x' IN (a1, a2) as bool_a,
       name,
       'y' IN (b1, b2) as bool_b
from <[D].[T]> t
```

5.1.4 correctly records `id, name, a1, a2, b1, b2`. Therefore, multiple
selected boolean columns are safe when each expression has one `IN`; the defect
occurs within an individual compound boolean expression.

Multiple compound boolean outputs reproduce independently:

```sql
select id,
       ('x' IN (a1, a2) and 'y' IN (b1, b2)) as ab,
       name,
       ('y' IN (b1, b2) and 'z' IN (c1, c2)) as bc
from <[D].[T]> t
```

5.1.4 records `id, name, b1, b2, c1, c2` and omits `a1, a2`. Each output's
right-hand `IN` survives; columns retained by another select item remain in the
combined table dictionary.

## JOIN ON reproduction

The defect also affects compound `JOIN ON` conditions and can remove columns
outside the condition.

Endpoint: `SQL`

Complete input:

```sql
select l.id
from <[D].[L]> l
join <[D].[R]> r
  on ('x' IN (l.a1, l.a2) and 'y' IN (r.b1, r.b2))
```

Expected and 5.0.0-3 table dictionaries:

```text
<[D].[L]>: id, a1, a2
<[D].[R]>: b1, b2
```

5.1.4 table dictionaries:

```text
<[D].[L]>: empty
<[D].[R]>: b1, b2
```

There are no diagnostics, and replacing `AND` with `OR` produces the same
result. In this context the right-hand `IN` contribution appears to replace
the accumulated left-table dictionary, including selected `l.id`.

## WHERE control

Endpoint: `SQL`

Complete input:

```sql
select id
from <[D].[T]> t
where ('x' IN (a1, a2) and 'y' IN (b1, b2))
```

5.1.4 correctly records `id, a1, a2, b1, b2`, and its `filters` bucket contains
all four predicate columns. The defect is isolated to compound predicates
inside select-expression lineage such as `CASE WHEN`, not ordinary `WHERE`
filter collection.

## Additional scope

- `QUERY` reproduces the same loss as `SQL`.
- Explicitly qualifying all columns does not prevent the loss.
- Three nested `IN` predicates retain only the final predicate's columns.
- Both left- and right-nested parenthesization reproduce the loss.
- Direct selected boolean expressions and `JOIN ON` conditions reproduce.
- Separate select items containing one `IN` each are handled correctly.
- `WHERE` and `HAVING` compound conditions are handled correctly.
- The AST and output interface lineage contain all operands.
- No FATAL, warning, or informational diagnostic is emitted.

## Full corpus reproduction

The complete unminimized row 9922 input is preserved at
`rmcp/tmp/snippet-row-9922.sql`. In its original ordering, 5.1.4 omits every
`end_date*` column and retains the following `constituent_code*` columns.
The complete operand-swapped input is preserved at
`rmcp/tmp/snippet-row-9922-swapped.sql`; it retains the `end_date*` columns and
loses `constituent_code_1` through `constituent_code_9`. Base
`constituent_code` remains only because it is independently referenced by the
final `ELSE constituent_code`.

## Parser-agent guidance

Inspect table-dictionary accumulation for compound conditions. The evidence
suggests that when the right child is an `IN` predicate, its table-dictionary
contribution replaces the accumulated left-child contribution in selected
expressions and `JOIN ON` processing. Compare those reducers with:

- the complete interface-lineage reducer for the same `CASE` expression; and
- the working reducers used for equivalent `WHERE` and `HAVING` conditions.
