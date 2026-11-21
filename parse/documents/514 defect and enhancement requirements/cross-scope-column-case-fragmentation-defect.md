# Cross-Scope Column-Case Fragmentation Defect

Date: 2026-11-20

Classification: **confirmed 5.1.4 defect**

Resolution: **complete in source (2026-11-21)**. Cross-scope global table
dictionary merges now use the shared column merge path for case-insensitive
unquoted-key matching and token deduplication. Double-quoted identifiers remain
case-sensitive. Focused SQL/QUERY dictionary and scope regressions pass; the
local release JAR has not been rebuilt.

This is a black-box finding. No parser source was consulted.

PSS 5.1.4 can publish two case-variant keys for one unquoted physical column
when references to that column occur in different query scopes. It also copies
one occurrence into both keys. The equivalent references merge correctly when
they occur in one query scope.

Unquoted SQL identifiers are case-insensitive. Splitting one physical column
between `SRC_DONOR_ID` and `src_donor_id` makes source-impact and dictionary
consumers dependent on which spelling they request.

## Minimal reproduction

Endpoint: `SQL`

Complete input:

```sql
select a.SRC_DONOR_ID
from <[AMS].[donor].{fulfillment}> as a
where a.spouse_id in (
  select d.src_donor_id
  from <[AMS].[donor].{fulfillment}> as d
)
```

Expected global table dictionary shape:

```text
<[AMS].[donor].{fulfillment}>
  SRC_DONOR_ID (or src_donor_id)
    outer a.SRC_DONOR_ID occurrence
    inner d.src_donor_id occurrence
  spouse_id
    outer a.spouse_id occurrence
```

The casing chosen for the canonical column key is not material. There should
be exactly one case-insensitive key for the physical source column, containing
both occurrences.

Actual 5.1.4 global table dictionary:

```text
<[AMS].[donor].{fulfillment}>
  SRC_DONOR_ID
    [@1,7:7='a',<459>,1:7]
  spouse_id
    [@9,68:68='a',<459>,3:6]
  src_donor_id
    [@15,94:94='d',<459>,4:9]
    [@1,7:7='a',<459>,1:7]
```

The outer occurrence is present under both case variants, while the inner
occurrence is present only under the lowercase key. No diagnostic is emitted.

## Case-swapped reproduction

Endpoint: `SQL`

Complete input:

```sql
select a.src_donor_id
from <[AMS].[donor].{fulfillment}> as a
where a.spouse_id in (
  select d.SRC_DONOR_ID
  from <[AMS].[donor].{fulfillment}> as d
)
```

Actual 5.1.4 global table dictionary:

```text
<[AMS].[donor].{fulfillment}>
  SRC_DONOR_ID
    inner d.SRC_DONOR_ID occurrence
    outer a.src_donor_id occurrence
  spouse_id
    outer a.spouse_id occurrence
  src_donor_id
    outer a.src_donor_id occurrence
```

Swapping only the source spelling swaps which key receives the complete
cross-scope merge. The duplicate case-variant key remains.

## Passing same-scope control

Endpoint: `SQL`

Complete input:

```sql
select a.SRC_DONOR_ID
from <[AMS].[donor].{fulfillment}> as a
where a.src_donor_id is not null
```

5.1.4 correctly publishes one key:

```text
<[AMS].[donor].{fulfillment}>
  SRC_DONOR_ID
    selected occurrence
    filtered occurrence
```

The trigger is therefore cross-scope global dictionary merging, not merely
mixed-case references.

## Production reproduction

The minimal case was derived from snippet row 9912:
`AMS/AMS_BUNDLE:V1.0/query`.

Endpoint: `QUERY`

Complete input:

```sql
SELECT SRC_DONOR_ID,
case when head_of_household_primary = 'Yes'
	 and spouse_id in (select src_donor_id from <[AMS].[donor].{fulfillment}> as d) then False
	 else True end AS hh_primary
FROM <[AMS].[donor].{fulfillment}> as a
```

Its 5.1.4 global table dictionary contains separate `SRC_DONOR_ID` and
`src_donor_id` entries under the same
`<[AMS].[donor].{fulfillment}>` table key.

The same corpus scan found case-insensitive duplicate column keys in additional
successful 5.1.4 query and snippet payloads. Examples include
`primary_student_id` / `PRIMARY_STUDENT_ID`, `last_name` / `LAST_NAME`, and
`contact_key` / `CONTACT_KEY`.

## Contract conflict

The 5.1.4 table/query dictionary design says the global table dictionary:

- merges every valid, deduplicated token across all query scopes;
- publishes one entry per physical table; and
- indexes each physical column to its occurrence-token list.

Case-fragmented keys and an occurrence duplicated across both variants violate
that global merge contract.

This finding is independent of numeric token-reference IDs. Renumbering a
token ID does not change lineage identity, while placing occurrences under two
case variants does.

## Parser-agent guidance

Inspect the global table-dictionary reducer used when a finalized child query
is merged into its parent/global result. Match unquoted physical column keys
case-insensitively before merging token lists, while preserving the chosen
canonical spelling. Apply token deduplication after the keys are unified.

Do not apply this normalization blindly to delimited identifiers in dialects
where quoted identifier case is significant.

Add tests for:

1. mixed-case references in one scope;
2. mixed-case references across a predicate subquery;
3. mixed-case references across a `FROM` subquery or CTE;
4. the case-swapped reproduction above; and
5. genuinely case-sensitive delimited identifiers as a non-merging control.
