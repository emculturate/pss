# Ordered Alias Iteration-Order Defect

Date: 2026-11-20

Classification: **confirmed 5.1.4 defect**

This is a black-box finding. No parser source was consulted.

The lineage assigned to a prior select-list output alias changes when only the
consumer output alias name changes. SQL source order and dependency semantics
remain identical.

## Passing control

Endpoint: `SQL`

Complete input:

```sql
select case when a = 1 then 'x' else 'y' end as x,
       case when x = 'x' then 'y' else 'n' end as y
from <[D].[T]> t
```

Expected and actual 5.1.4 lineage:

```text
interface.x -> <[D].[T]>.a
interface.y -> query0.x
```

The local and global table dictionaries contain physical `a` and do not
contain physical `x`.

## Failing reproduction

Endpoint: `SQL`

Complete input:

```sql
select case when a = 1 then 'x' else 'y' end as x,
       case when x = 'x' then 'y' else 'n' end as out_col
from <[D].[T]> t
```

Only the second output alias changed from `y` to `out_col`.

Expected lineage:

```text
interface.x       -> <[D].[T]>.a
interface.out_col -> query0.x
```

Actual 5.1.4 lineage:

```text
interface.x       -> <[D].[T]>.a
interface.out_col -> <[D].[T]>.x
```

The table dictionary still contains physical `a` and does not contain physical
`x`. The interface therefore points to a nonexistent physical dependency.

No diagnostic is emitted.

## Alias-name sensitivity

Using the same defining item and consumer expression, these tested consumer
aliases resolved correctly to `query0.x`:

```text
y
z
derived_value
m
```

These tested consumer aliases incorrectly resolved to physical `<[D].[T]>.x`:

```text
aa
out_col
result
flag
BIRTH_MONTH
```

The consumer output alias has no semantic role in resolving its dependency on
the earlier `x` output. These differing results cannot be justified by SQL
scope or the ordered select-list alias policy.

## Surrounding-select sensitivity

Adding unrelated select-list outputs can also flip whether a later dependency
is stamped `query0` or a physical table. This originally appeared to be a
select-width threshold, but reductions showed that width alone is insufficient:
simple alias chains can remain correct with at least 60 preceding outputs.

The observed behavior is consistent with resolution depending on interface-map
key composition or iteration order instead of only source token order. That is
a black-box inference, not a parser implementation claim.

## Production impact

The finding explains the previously context-dependent cluster:

- bound row 254:
  `ALR/DMP_Partner_Data/transformation_query_comprehensive_test`
- bound rows 1141 and 1142:
  `ALR/Universal/transformation_query_applicants`
- bound row 1189:
  `ALR/Universal/transformation_query_comprehensive`

In those queries, `ethnicity_rollup` consumes earlier outputs `race_2` through
`race_6`. 5.1.4 incorrectly publishes those dependencies as physical source
columns even though its table dictionary correctly omits them as physical
columns.

Small extracts sometimes pass because changing the set of surrounding output
aliases changes the faulty ordering condition. The two-query reproduction
above avoids requiring the parser agent to reproduce the full production
select lists.

## Contract conflict

The locked ordered select-list alias policy requires an earlier grounded output
alias used by a later select item to be published as:

```text
{name=<alias>, table_ref=queryN}
```

Resolution must follow select-list source order. The later output's own alias
name must not affect dependency resolution.

## Parser-agent guidance

Inspect the final interface pass that stamps prior output-alias dependencies.
Use token-derived select-list order consistently and avoid relying on map/set
iteration order when determining whether a dependency precedes its consumer.

Add a contract test that parses the passing and failing queries above and
asserts identical lineage for `x`. Repeat with reordered or additional
unrelated select outputs to ensure the result is invariant under interface-key
composition.
