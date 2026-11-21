# Query-Substitution Presumed-Lineage Enhancement

Date: 2026-11-20

Classification: **5.1.4 lineage enhancement request**

This is a black-box finding. No parser source was consulted.

## Clarified interpretation

This behavior is a tightening of substitution-variable typing in 5.1.4, not a
table-dictionary defect. The parenthesized variable is a `QUERY`. Once filled,
its produced columns belong to that query's interface and query lineage rather
than to a physical table dictionary. Because the parser does not have the
substituted query text at parse time, it cannot construct the real query
dictionary or concrete interface for that variable.

Completing static lineage therefore requires an enhancement: publish a
placeholder `def_queryN` scope for the unknown query variable and record
external references such as `x` and `y` as `"PRESUMED"` columns expected in
that placeholder query interface. The outer alias should resolve to that
placeholder scope. After binding supplies the actual query, those presumed
requirements can be validated against its real interface.

## Minimal motivating reproduction

Endpoint: `SQL` or `QUERY`

Complete input:

```sql
select x
from (<source_query>) q
where y = 1
```

The parenthesized substitution is correctly classified as:

```text
<source_query>: query
```

The table dictionary is expected to remain empty because `<source_query>` is
not a tuple or physical table. Current 5.1.4 results are:

```text
tableDictionary: empty
interface.x: {name: x, table_ref: null}
filters.y:   {name: y, table_ref: null}
```

5.1.4 emits:

```text
UNRESOLVED_UNQUALIFIED_COLUMNS: x, y
```

5.0.0-3 binds both columns to `<source_query>` and emits no diagnostic.
That 5.0.0-3 behavior conflates a query variable with a table source and is not
the desired 5.1.4 model.

The proposed enhancement would preserve the strict typing while publishing a
shape conceptually equivalent to:

```text
table_alias.q -> queryN
def_queryN.query_substitution -> <source_query>
def_queryN.interface.x -> PRESUMED
def_queryN.interface.y -> PRESUMED
outer interface.x -> queryN.x
outer filters.y -> queryN.y
```

## Qualified passing control

Endpoint: `SQL` or `QUERY`

Complete input:

```sql
select q.x
from (<source_query>) q
where q.y = 1
```

Current 5.1.4 records direct qualified references under:

```text
<source_query>: x, y
```

Its interface and filter references retain alias `q`, which resolves through:

```text
table_alias.q -> <source_query>
```

No diagnostic is emitted. Under the proposed enhancement, qualified and
unqualified external requirements should both route through the same
placeholder query scope rather than requiring a table-dictionary entry.

## Tuple-substitution passing control

Endpoint: `SQL` or `QUERY`

Complete input:

```sql
select x
from <source_tuple> q
where y = 1
```

5.1.4 classifies `<source_tuple>` as `tuple`, applies the single-source rule,
and correctly records:

```text
<source_tuple>: x, y
```

This contrast demonstrates the intentional typing distinction: tuple variables
can contribute physical-style table dictionary entries, while query variables
need a placeholder query scope to expose presumed interface requirements.

## Production-shaped reproduction

Derived from bound-query row 3868.

Endpoint: `SQL` or `QUERY`

Complete input:

```sql
select partnercontact_id,
       partner_system_name,
       eab_entry_year_academic,
       sum(agg_fund_amount) as agg_fund_amount
from (
    <fao_calc.query.total_fund_amount_per_fund_type>
) fund_agg
where eab_fund_type = 'pell_grant'
  and coalesce(imputed_flag, false) = false
group by partnercontact_id,
         partner_system_name,
         eab_entry_year_academic
```

External columns presumed to be required from the query variable:

```text
partnercontact_id
partner_system_name
eab_entry_year_academic
agg_fund_amount
eab_fund_type
imputed_flag
```

5.0.0-3 records all six under
`<fao_calc.query.total_fund_amount_per_fund_type>` as though it were a table.

5.1.4 records no table-dictionary entry for the query substitution. Its
interface, grouping, and filters retain the names without `table_ref`, and it
emits `UNRESOLVED_UNQUALIFIED_COLUMNS` for all six.

The enhancement should preserve the empty table dictionary while placing the
six `"PRESUMED"` requirements on a placeholder query interface and linking
the outer references to that scope.

## Affected corpus cluster

- Bound query row 3868.
- Snippet row 35605, endpoint `JOIN_EXTENSION`.
- Snippet row 37024, endpoint `QUERY`.
- Snippet row 37025, endpoint `QUERY`.

The snippet cases reuse the same financial-aid query substitution and expose
the same missing presumed-lineage representation.

## Contract clarification

The generic single-source rule must not coerce a `QUERY` variable into a tuple
or physical table. For this shape:

- an empty table dictionary is expected;
- the absent query text prevents a concrete query dictionary and interface;
- outer references still express requirements that the eventual query must
  satisfy; and
- those requirements need an explicit presumed-lineage representation.

The current vendor wording about query substitution bodies appearing as source
keys in the global table dictionary should be clarified for parenthesized
`QUERY` variables so consumers do not infer the old 5.0.0 table model.

## Parser-agent guidance

Design a placeholder `def_queryN` representation for parenthesized query
variables whose SQL is unavailable. Preserve the variable's `query` type and
empty table dictionary. Route qualified and unqualified outer references to
the placeholder and mark the expected interface columns as `"PRESUMED"` until
the binding query is available for validation.

Add paired contract tests for:

1. unqualified requirements over `FROM (<query_variable>) q`;
2. qualified `q.column` requirements;
3. unqualified columns over `FROM <tuple_variable> q` as a distinct table
   model;
4. multiple query variables where unqualified requirements cannot be assigned
   to one placeholder; and
5. post-binding validation of presumed requirements against the real query
   interface.

Assert the empty table dictionary, placeholder query scope, presumed interface,
outer interface lineage, filters, grouping, substitutions, and diagnostics.
