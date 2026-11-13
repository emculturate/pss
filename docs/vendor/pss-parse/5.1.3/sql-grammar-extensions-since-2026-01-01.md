# SQL grammar, AST, and validation changes since 2026-01-01

**Reviewed through:** 2026-11-12, branch `Fall-2026-Extensions`, current 5.1.4-1 development line.

**Scope:** Grammar and lexer changes since the last pre-2026 snapshot
(`f49203f`, 2025-12-31), plus consumer-visible summer/fall AST, symbol-table,
diagnostic, and defect-resolution changes carried into 5.1.4. The original
August grammar inventory is retained below and supplemented by the dated review.

**Version boundary:** This is a cumulative feature inventory, not evidence that
every listed feature was absent in 5.0.0. Changes explicitly compared with
5.0.0-3 are identified below. The 5.1.3 symbol-table contracts remain applicable
in 5.1.4. Planned work is not shipped functionality.

**Consumer actions:** [Panto 5.1.4 upgrade checks](https://github.com/emculturate/pss/blob/Fall-2026-Extensions/parse/documents/panto-template-remediation-5.1.4.md#dependent-application-upgrade-checks-delivered-behavior-through-2026-11-11)
uses the change IDs below to identify required application checks.

**Build artifacts:** Packaging the current `parse/` module produces:

| Artifact | Role |
|----------|------|
| `target/pss-parse-5.1.4-1.jar` | Main parser + runtime classes |
| `target/pss-parse-5.1.4-1-tests.jar` | Test utilities (`classifier: tests`) |
| `target/pss-parse-5.1.4-1-fat.jar` | Standalone jar-with-dependencies (Python/JS callers) |

The opt-in `isolated-build` profile writes to `target-maven/` instead of
`target/`, avoiding IDE auto-build interference. Workspace testing is configured
for JDK 21. Artifact names here describe the development build, not a publication
claim.

## Summer/fall review and consumer-impact index

**Implemented** means present on the current branch; it does not imply that
every permitted grammar combination has complete acceptance or generator
coverage. IDs U01-U27 are shared with the consumer checklist.

| ID | Delivered coverage or changed behavior | Consumer-visible effect / boundary |
|---|---|---|
| U01 | SCRIPT and fragment endpoints, including LITERAL and DML | Select the correct endpoint; scripts have per-statement AST/symbol payloads, not one SELECT root |
| U02 | DDL object coverage and verbatim option tails | Preserve opaque source text; structured DDL option semantics remain backlog |
| U03 | INSERT / UPDATE / DELETE, RETURNING, ON CONFLICT | DML roots, target/assignment buckets, returning interfaces, and nested update scopes must be understood |
| U04 | CTE bodies including DML/VALUES and substitutions; ordered visibility | Nested scopes and explicit alias bindings replace assumptions of one flat query namespace |
| U05 | Global physical/tuple dictionary policy (5.1.3+) | CTE and local alias keys formerly promoted by 5.0.0-3 are intentionally absent |
| U06 | Published `def_*`, scope interfaces, clause archives, `dependent_queries` | Trace each scope boundary; query dictionaries are not flattened physical-column inventories |
| U07 | Grounded, earlier SELECT-list aliases | Same-scope lineage can use `table_ref=queryN`; forward/cyclic aliases are not valid resolved sources |
| U08 | Window dependency isolation and archive policy | Read `window_partition_by` / `window_ordered_by`; window-only names need not be query-dictionary outputs |
| U09 | PIVOT/UNPIVOT AST and derivation consolidation | `in_any`, `nulls_policy`, and singular `derivation` carry meaning; source lineage may fan out |
| U10 | JOIN USING operand resolution and join-form diagnostics | USING contributes filter/source evidence; invalid CROSS/NATURAL ON/USING and NATURAL FULL joins are rejected |
| U11 | Set-op wildcard-aware width checks | Unknown wildcard width no longer causes the known false mismatch; concrete mismatches still fail |
| U12 | Duplicate output/interface validation | SQL accepted by 5.0.0-3 may now be FATAL; duplicate names can cause a cascading width mismatch |
| U13 | Unparenthesized `FROM VALUES (...) AS alias(cols)` | Alias columns and VALUES scopes are available without adding an outer parenthesis pair |
| U14 | WITHIN GROUP ordered aggregates | Dedicated `ordered_aggregate` AST and `within_group_ordered_by` lineage; wider acceptance matrix still pending |
| U15 | Nested/product CASE and function-result array subscripts | Structured `subscript={array, index}`; bracketed identifiers remain distinct; not general array-literal support |
| U16 | Grouping combinators and ALL/DISTINCT AST | Structured ROLLUP/CUBE/GROUPING SETS and clause-level `option` values, not just flat column lists |
| U17 | Table-function arguments and row sources | Literal keyword arguments are scalarized; named arguments, lateral sources, and generic table functions need traversal |
| U18 | Arithmetic, casts, search/scalar functions, predicates, bare values | Broader expression shapes; bare value expressions must not become physical columns; SQL UNIQUE predicate removed |
| U19 | EXTRACT and DATE_PART FROM | Dedicated `extract` contract, part form, invocation discriminator, typed literal sources; distinct from the add-family datetime AST |
| U20 | Per-statement Snowflake/Postgres affinity | Warnings/counters identify target dependence; mixed affinity is FATAL; state resets between script statements |
| U21 | SELECT-list expression subquery scope fix (November Entry 13) | Restored scalar `lookup` wrapper; private subquery columns no longer leak into outer output lineage |
| U22 | Join-extension diagnostic deferral (November Entry 14) | Specific missing-table FATALs become severe warnings plus `JOIN_EXTENSION_REQUIRED`; whole-query approximation |
| U23 | Set-op query-variable interface fix (November Entry 16) | Real branch exports survive query-variable members; variable-only composites have an open wildcard interface |
| U24 | Contextual `position`, `input`, `path`, and numeric `truncate()` (November Entry 18) | Ten observed bound-query regressions cleared; obsolete unused array snippet discrepancy waived, not fixed |
| U25 | Structured syntax/walker diagnostics and production LL policy | A returned Snippet/JSON or partial AST is not parse success; error messages include more context |
| U26 | Lexer token numbering | Token IDs are version-specific; the November batch shifts old IDs 393-413 by +26 |
| U27 | Existing substitutions/Jinja/colon-path surface versus backlog | Grammar presence is not complete template expansion, semi-structured, or generator support |

### Summer additions and corrected contracts

- **Published scopes and dictionaries:** Phases 1-20 of symbol-table resolution
  consolidation closed in August. Consume published `def_*` payloads; the
  child's `interface` is the export boundary. Global table dictionaries collect
  physical/tuple sources across scopes; each query dictionary tracks its own
  outputs and justified usage sites, not every input expression. CTE references
  belong on `table_alias` / `context_list` with nested query definitions, not
  global physical table keys. Trailing-clause-only physical and tuple
  references after joins are retained.
- **Aliases and windows:** Prior grounded SELECT aliases resolve through
  same-query interface hops, including substitution-backed and scalar-subquery
  origins. Forward and ungrounded cyclic references remain unresolved/fatal.
  Partition/order dependencies are archived separately; window-only inputs do
  not automatically widen the query dictionary. Window output lineage retains
  its own dependencies without contaminating neighboring SELECT outputs or
  later query scopes.
- **Dependent subqueries:** `dependent_queries.type` matches the owning role
  bucket, including window, assignment, and PIVOT scalar sites. Parent dictionary
  collection must not flatten child-private sources. Trace role archives and
  their dependent subqueries together.
- **Relational modifiers:** UNPIVOT records `nulls_policy`; PIVOT ANY uses
  `in_any` (with optional ordering). Derived output references and their
  `derivation.source_columns` preserve modifier-to-source lineage across
  interfaces and clauses, including windows. The supported PIVOT registry
  naming policy remains `{inComponent}_{aggregateAlias}`; do not infer physical
  source columns from bare Snowflake generated output names.
- **JOIN USING:** Matching uses each operand's exported interface, including
  VALUES and table-function sources; USING columns contribute filter lineage
  across flat join chains. CROSS/NATURAL joins with ON/USING receive diagnostics
  per join entry. `NATURAL FULL [OUTER] JOIN` is intentionally unsupported;
  ordinary FULL OUTER JOIN with ON/USING is the alternative.
- **Set operations:** Wildcard-bearing unknown widths no longer trigger the
  known false column-count FATAL. Explicit mismatches still do. Duplicate
  interface names remain FATAL even where the target engine would execute a
  positional UNION; add distinct aliases instead of disabling validation.
  Set-op scope publication/merging isolates siblings rather than importing a
  sibling's private definitions.
- **August grammar additions:** Unparenthesized VALUES with column aliases,
  WITHIN GROUP ordered aggregates, function-result subscripts and bracketed
  identifier disambiguation, and nested CASE exemplars are delivered.
  `ordered_aggregate` contains the function and `within_group`, plus optional
  OVER content; ordered sort dependencies use `within_group_ordered_by`.
  Grammar admits LISTAGG, ARRAY_AGG, PERCENTILE_CONT, and PERCENTILE_DISC,
  but Entry 19 retains acceptance gaps beyond LISTAGG, and Entry 4 owns
  broader ARRAY_AGG/compound-array coverage.
- **Other structured AST changes:** GROUP BY ALL/DISTINCT records clause-level
  `option=ALL|DISTINCT`; ROLLUP/CUBE/GROUPING SETS retain combinator structure.
  Table-function literal keyword arguments are scalarized rather than exposed
  as nested literal wrappers. EXTRACT/DATE_PART FROM uses `extract` with
  `part`, `part_form`, `source`, optional `source_type`, and DATE_PART's
  `invocation` discriminator. These contracts remain separate from the current
  add-family `datetime_function` node.
- **Dialect and bare-value handling:** Existing statement-level dialect
  diagnostics were added in August. Shared constructs remain neutral;
  dialect-specific grammar/parts use the existing collector, and mixed
  Snowflake/Postgres dependence is FATAL within a statement. Bare ANSI/dialect
  value expressions are not source columns.

### September production parsing and diagnostics

Production `SqlParserAccess` uses **LL parsing**, not SLL-first or SLL fallback.
The historical 74-row timeout gate closed; its 90-second timing criterion is
not proof that SQL is valid and is not part of the ordinary correctness suite.
SLL probing and latency reporting are opt-in diagnostics only.

Production still attempts the AST walk after parse errors. Recognized walker
stack misalignment is reported as structured `AST_WALKER_STACK_MISALIGN`; other
walker exceptions can still produce FATAL diagnostics. Earlier proposals to
skip walking after syntax errors do not describe the current production path.
Fatal syntax diagnostics gained snippet/rule context. Applications must inspect
diagnostic severities, not success-shaped wrappers, nonempty ASTs, or exact
message text. No general guarantee of exception-free walking on malformed SQL
is implied.

### November defect closure and lexer baseline

- **Entry 13:** SELECT-list nested subqueries now retain private aliases inside
  their own scopes; scalar operands regain the 5.0.0-style `lookup` AST wrapper.
  The recorded corpus run fixed 395 affected cases with no newly fatal cases
  at that checkpoint. This is historical checkpoint evidence, not a fresh
  complete-corpus result for today's branch.
- **Entry 14:** When a statement contains a join extension, only
  `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE` FATALs are downgraded to
  `SEVERE_WARNING`, with `JOIN_EXTENSION_REQUIRED` per variable. Other FATALs
  remain. The implementation is whole-query within a statement: it does not
  prove the extension can satisfy a missing reference in that particular
  scope. Precise scope-aware deferral remains Entry 15.
- **Entry 16:** A concrete set-op branch combined with a query variable now
  retains its exported interface; variable-only composites publish an open
  wildcard interface. Unknown variable shape is not a known concrete width.
  Revalidate the generated SQL after substitution.
- **Entry 18:** All ten frozen bound-query cases involving `position`, `input`,
  `path`, and `truncate(...)` now have zero FATALs. Four TRUNCATE ASTs exactly
  match 5.0.0-3; the other six match after comparison-only CTE representation
  normalization. This is not a dictionary-parity assertion. Unused snippet
  s82233's array syntax/walker discrepancy was accepted as obsolete; neither
  general array literals nor walker robustness were implemented for it.
- **Entry 1 lexer baseline:** 26 function tokens were added before Identifier;
  IDs 1-392 are unchanged and former IDs 393-413 become 419-439. Part aliases
  remain identifiers. The user's manual golden edits are complete; the latest
  VS Code testing-extension run on configured JDK 21 reports **2,643 passed,
  0 failed**. Phase 1 / Slice A is complete, not the date/time feature itself.

### DATEADD-family AST: consumer guidance (5.1.4 development)

The current development branch emits a dedicated `datetime_function` node for
unqualified built-in `DATEADD`, `TIMEADD`, and `TIMESTAMPADD` calls:

```text
DaTeAdD('yEaR', 1, a.ts)
{datetime_function={
  function_name=DaTeAdD,
  level={canonical=YEAR, text=yEaR, form=STRING},
  increment={literal=1},
  value={column={name=ts, table_ref=a}}
}}
```

`function_name` retains the authored case. Dispatch on it case-insensitively;
the add-family SQL argument order is `level`, `increment`, `value`, regardless
of map iteration order. Qualified same-name functions such as `custom.dateadd`
remain generic function calls.

The `level` fields have distinct purposes:

- `canonical` is the uppercase registry-normalized part, such as `YEAR` or
  `MONTH`, for interpreting the part's meaning. An AST alone does not prove a
  valid part: inspect diagnostics, including `INVALID_DATETIME_PART` FATAL.
- `text` retains the authored spelling and case, including aliases. For a
  `STRING`, it is the decoded content **without** the surrounding single
  quotes; doubled single quotes inside the literal are decoded to one quote.
- `form` records whether the original part was a `KEYWORD`, `IDENTIFIER`, or
  `STRING`. For example, `DaTeAdD(yEaR, ...)` also has `text=yEaR` and
  `canonical=YEAR`, but has `form=KEYWORD`, not `STRING`. Use `form`, not the
  contents of `text`, to distinguish the quoted and unquoted cases.

Consumers generating SQL should preserve `function_name` and `level.text`, use
the signature's argument order, emit `KEYWORD` and `IDENTIFIER` text without
string quotes, and emit `STRING` text as a single-quoted SQL literal with
embedded single quotes doubled. Do not replace an authored alias with its
canonical name when preserving source spelling. The current SQL generator
does **not** yet handle `datetime_function`; this is guidance for consumers,
not a claim of parser-provided round-trip generation.

`level` is metadata, not a column reference. Traverse `increment` and `value`
as ordinary expressions: their column references and substitutions retain
normal typing, resolution, dictionaries, and lineage. Do not exclude a real
column merely because its name is also a date part.

### Planned work and limitations as of 2026-11-12

The add-family grammar, AST, and part registry are present, but other new
date/time families and the approved EXTRACT/DATE_PART unification remain
unfinished. Existing EXTRACT/DATE_PART output still uses `extract`.
q5866's date-part-as-column regression remains open under Entry 1. The
[consumer date/time section](https://github.com/emculturate/pss/blob/Fall-2026-Extensions/parse/documents/panto-template-remediation-5.1.4.md#dependent-application-upgrade-checks-datetime-ast-changes-planned)
distinguishes the implemented add family from the planned wider contract.

Do not promote the following to delivered support: Entry 2 quoted unaliased
PIVOT outputs; Entry 3 full PARSE_* / colon semi-structured access; Entry 4 general
arrays; Entries 5-6 broader Jinja/dbt; Entry 8 cardinality/dynamic expansion;
Entry 9 structured DDL options; Entry 10 remaining generator coverage; Entry 15
precise join-extension deferral; Entry 19 residual acceptance matrices.
Existing grammar surface below may cover subsets of those topics without
establishing full behavior. In particular, function-result subscripting is not
array-literal construction, and parsing is not a SQL-generation parity claim.

### Evidence and verification boundaries

The review combines current grammar/walker/access source, literal contract
tests, canonical consumer policies, and git history. Representative checkpoints:
`eb499c7` (dialect tracking), `ee80e1b` / `e59cf99` (grouping AST),
`e2c9388` (VALUES), `aacc694` (ordered aggregates), `442fff5` / `dd27a16`
(subscripts), `0522ce7` (CTE/join policy), `cab17b7` / `48332e8`
(production/diagnostic parsing policy), `fab17ba` (Entries 13-14), `0675160`
(Entry 16), and `f8e1053` (lexer/contextual words).

Canonical contracts:
[scope/bucket reference](./symbol-table-bucket-reference.md),
[dictionary collection](./table-and-query-dictionary-design.md),
[CTE/global dictionary policy](./global-table-dictionary-cte-alias-policy.md),
[ordered aliases](./ordered-select-list-output-alias-policy.md),
[window policy](./phase-17.6.9-window-query-dictionary-policy.md),
[modifier policy](./relational-modifier-resolution-policy.md), and
[duplicate outputs](./set-operation-interface-duplicate-output-names-policy.md).
The [master workplan](https://github.com/emculturate/pss/blob/Fall-2026-Extensions/parse/documents/sql-grammar-5.1.4-workplan.md) remains authoritative for
open work. Counts from different runners/checkpoints are not interchangeable;
the latest green test run is not a new whole-corpus 5.0.0-versus-5.1.4 comparison.
These repository-only links use GitHub URLs so they remain usable from the
vendor/JAR mirror, which does not bundle the workplan or Panto remediation index.

---

## Script and entry points

- **`script` / `sql_statement`** — New top-level: semicolon-separated mixes of DDL, DML, `WITH …` pipelines, and bare `query_expression`.
- **Dedicated endpoints** — Formal start rules for `update_end_point`, `delete_end_point`, `truncate_end_point` (update existed loosely before; delete/truncate endpoints are new).
- **`literal_value`** — Fragment endpoint wired for numeric/unsigned literals (walker key `LITERAL`).
- **`predicand_value`** — Unchanged name; predicand/expression surface expanded (see Expressions).

---

## DDL

- **Full DDL primaries** — `ddl` / `ddl_primary` with CREATE / DROP / ALTER / TRUNCATE (not only `CREATE TABLE AS`).
- **CREATE object types** — Table (column list + options tail, or `AS query`), index, view, materialized view, function, procedure, macro, sequence, schema, database, role, user, stage, file format.
- **DROP / ALTER** — `ddl_object_type` + object name + optional tails.
- **Opaque option capture** — `generic_ddl_paren_content` (nested parens) and `generic_ddl_options` (verbatim tail to `;` or EOF); options are **not** fully decomposed in the grammar.
- **TRUNCATE dialect split** — Snowflake (`TRUNCATE TABLE name`) vs Postgres (optional `TABLE`, multiple names).

---

## `WITH` (CTE)

- **CTE body shapes** — `query` under a CTE may be `query_expression`, **INSERT**, **UPDATE**, **DELETE**, or **VALUES** (not only SELECT).
- **Substitution CTE body** — `query_alias variable_identifier` (tuple/query substitution as CTE body).
- **Nested `WITH`** — `cte_body` allows `with_query` (Snowflake-style nested WITH inside a CTE only).
- **DML + outer `WITH`** — Same `with_query` wrapper used for leading CTEs on DML statements (per comment blocks in grammar).

---

## INSERT

- **Postgres path as default** — `insert_expression` → `postgres_insert` (Snowflake insert remains `snowflake_insert` building block).
- **`ON CONFLICT`** — `conflict_target`, `DO NOTHING`, `DO UPDATE SET` + optional `WHERE` (lexer keywords `CONFLICT` / `DO` / `NOTHING` with `Identifier` fallback when tokenized as names).
- **`RETURNING`** — Shared `returning` rule on insert (Postgres-style).
- **Target table rule** — `insert_target_table_primary` excludes table-function call shape from `INSERT INTO … (cols)`.
- **Sources** — `query_expression`, substitution, `VALUES`, `DEFAULT VALUES`.
- **Targets** — `db_object_name`, substitutions, **Jinja/DBT** `jinja_identifier`.

---

## UPDATE

- **`update_end_point`** — First-class parse entry.
- **Clause options** — `SET` assignment list, optional `FROM`, `WHERE`, **`RETURNING`** (Postgres/Snowflake overlap; reuses `select_list`).
- **Table resolution** — Uses `table_primary` / left-factored aliasing (see FROM).

---

## DELETE

- **Snowflake vs Postgres** — `delete_snowflake_expression` (no `RETURNING`) vs `delete_postgres_expression` (requires `delete_returning`).
- **`USING`** — `delete_using_clause` with `table_reference_list`.
- **CTE validity** — Grammar comments distinguish which variant may appear as a CTE data source.

---

## TRUNCATE

- **Standalone** — `truncate_end_point` and `truncate_statement_primary` under DDL/script.
- **Dialect variants** — See DDL (Snowflake vs Postgres).

---

## SELECT / set operations

- **`query_primary` removed** — Set-op members use `set_operation_member`: `subquery` | `query_specification` | `variable_identifier` (avoids dead `query_primary` alternative).
- **`QUALIFY`** — Snowflake post-window filter: `qualify_clause` after `HAVING`, before `ORDER BY`.
- **Set ops** — `UNION` / `EXCEPT` / `INTERSECT` with optional `DISTINCT`/`ALL` on qualifiers (structure retained; member rules updated).

---

## FROM / joins / relation sources

- **`LATERAL`** — `lateral_modifier` on comma and join operands.
- **Relation sources** — `table_source_primary` adds **table functions**, **VALUES**, **Jinja**, substitutions; `jinja_identifier` in FROM/INSERT targets.
- **PIVOT / UNPIVOT** — Postfix `table_relational_modifier` on `table_primary` (see below).
- **Alias factoring** — Aliases on `table_primary` / modifier aliases gated so PIVOT/UNPIVOT do not admit ambiguous double-optional `AS` (left-factoring change).
- **Join extension** — `join_extension` substitution variable still supported; `join_extension_primary` for snippet endpoint.

---

## PIVOT / UNPIVOT (Snowflake)

- **`UNPIVOT`** — Value/name columns, `IN` list, `INCLUDE NULLS` / `EXCLUDE NULLS`.
- **`PIVOT`** — Aggregate clause (single aggregate or Snowflake comma list: AVG/COUNT/MAX/MIN/SUM), `FOR` column, `IN` content:
  - literal value list (with optional `AS` prefix labels),
  - `ANY` [ `ORDER BY` … ],
  - subquery.
- **`DEFAULT ON NULL (...)`** — Optional pivot tail.
- **Shared IN-list helpers** — `relational_modifier_*` rules shared between pivot and unpivot.

---

## Table functions (Snowflake-oriented)

- **`table_function_primary`** — `TABLE(…)` wrapper or bare call.
- **Named functions** — FLATTEN (incl. `LATERAL_FLATTEN` name), GENERATOR, RESULT_SCAN, INFER_SCHEMA, VALIDATE, plus **generic** `identifier(…)` fallback.
- **Named arguments** — `INPUT=>`, `PATH=>`, `ROWCOUNT=>`, `LOCATION=>`, etc., with semantic predicates on argument lists where required.
- **`table_argument_literal`** — String/numeric args for TF kwargs (T2.1 coverage).

---

## GROUP BY / HAVING

- **`GROUP BY ALL`** — `groupby_all_option`.
- **`GROUP BY DISTINCT`** — `groupby_distinct_body` wrapping grouping elements.
- **`GROUPING SETS`** — First-class `grouping_sets_list` / `grouping_element` alternative (baseline had ROLLUP/CUBE/empty/parenthesized sets but not explicit `GROUPING SETS` grouping element).
- **Legacy** — Still allows `GROUP BY select_list` alternative.

---

## WHERE / predicates

- **`quantified_comparison_predicate`** — Wired on `predicate` (`= ALL|ANY|SOME (subquery)`); rule existed pre-2026 but was not on the main predicate chain in the same way.
- **`substitution_predicate`** — Tuple/substitution in boolean context.
- **`exists_predicate`** — `EXISTS` on subquery or substitution.
- **Removed** — `unique_predicate` (SQL `UNIQUE (subquery)`—not Snowflake).
- **LIKE / ILIKE** — `like_any_predicate` (`LIKE ANY` / `ILIKE ANY`) with optional `ESCAPE`.

---

## Expressions, casts, functions

- **Unparenthesized arithmetic in predicands** — `row_value_predicand` includes `common_value_expression` (`+`, `*`, etc. without extra parens).
- **Postgres `::` casts** — Chained `(expr)::type`, including `NULL::type`; predicand and value-expression paths updated.
- **`CAST` / inline casts** — Broader NULL and chain casting support in value expressions.
- **String / search functions** — `POSITION` (incl. SQL `IN` form), `INSTR`, `CHARINDEX` (2- and 3-argument shapes).
- **Snowflake scalars** — `IFF`, `MD5`, `REVERSE` (lexer + routine names).
- **TRIM** — Dropped unreachable `trim_operands` alternative; MySQL-style and `FROM` forms retained.
- **JSON path columns** — Colon path segments on `column_reference` / `column_primary` (variant access).

---

## EXTRACT / DATE_PART

- **`extract_expression`** — Expanded `extract_field` (Postgres + **Snowflake** field tokens, case-insensitive `EXTRACT` lexer).
- **`extract_source`** — Column, datetime literal, **interval**, broader predicand sources; dedicated **predicand** endpoint coverage.
- **`date_part_expression`** — Snowflake/Postgres `DATE_PART(field FROM source)` with case-insensitive `DATE_PART` lexer (new keyword placed after PIVOT block to limit token renumbering).

---

## Substitutions, Jinja, DBT

- **Angle-bracket substitutions** — Expanded use in columns, FROM, CTE bodies, predicates, set-op members.
- **`jinja_identifier`** — `{{ ref|source|stream|var|env_var(...) }}`, `config.get(...)`, `{{ this }}` / `{{ target.schema }}` chains, keyword args in calls.
- **Insert/update symbol tables** — Grammar allows DML shapes that carry substitutions (walker work; grammar enables targets/sources).

---

## VALUES

- **Script / CTE / FROM** — `values_statement_primary` as relation source.
- **Insert** — `insert_values_statement` / matrix forms unchanged in spirit; better integrated with DML and CTE rules.

---

## Lexer / grammar hygiene

- **Many new keywords** — DDL objects, DML (`DELETE`, `TRUNCATE`, `RETURNING`, conflict words), PIVOT/UNPIVOT, table-function args, Snowflake extract parts, `DATE_PART`, `SETS`, etc.
- **Keyword placement policy** — Comment in lexer: append late-keyword block before `Identifier` to avoid mass token ID churn in goldens.
- **Parser predicates** — Jinja recognition, disallowed join/set-op aliases, implicit alias gating, Snowflake TF mode literals, DDL not applicable here but same file.

---

## Intentionally shallow or out of scope in the grammar

- **DDL interiors** — Captured as opaque text, not per-option rules (see `generic_ddl_*`).
- **Hive `INSERT OVERWRITE`** — Documented in comments; not a first-class alternate in current `insert_expression`.
- **Full Postgres INSERT** — `postgres_insert` builds on Snowflake-shaped core; some Postgres-only insert variants may still be comment-only.

---

## Related non-grammar work (same period, for context only)

The summer/fall review above now includes consumer-visible walker AST, symbol
tables, dialect diagnostics, and defect fixes. JaCoCo exit coverage, golden
consolidation, and generator work accompany them but do not by themselves prove
new language support or complete regeneration fidelity.
