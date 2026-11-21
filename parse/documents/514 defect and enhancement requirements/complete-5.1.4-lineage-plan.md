---
name: Complete 5.1.4 Lineage
overview: Prepare a parser-agent handoff for implementing authoritative structured lineage and explanation inside PSS 5.1.4. RMCP contributes reusable ideas, corpus evidence, and acceptance cases, then becomes a thin consumer of the parser-produced explanation.
todos:
  - id: audit-lineage-corpus
    content: Assess the live bound-query corpus and select complete runnable examples for every lineage construct and known failure.
    status: pending
  - id: inventory-rmcp-lineage
    content: Summarize reusable RMCP lineage code and annotate which ideas are safe to port, incomplete, or unsafe.
    status: pending
  - id: specify-parser-lineage
    content: Define the parser-owned lineage graph, explanation payload, scope resolution, and special-construct behavior.
    status: pending
  - id: specify-query-placeholders
    content: Define query-substitution placeholder scopes and PRESUMED interface lineage in the parser payload.
    status: pending
  - id: define-parser-acceptance
    content: Provide parser-side graph invariants, golden queries, and corpus-level completeness and accuracy thresholds.
    status: pending
  - id: define-rmcp-consumer
    content: Specify the thin RMCP integration that displays parser-produced structured and narrative lineage without reimplementing semantics.
    status: pending
isProject: false
---

# Complete 5.1.4 Lineage

## Ownership decision
- Implement the authoritative lineage program in the PSS parser, where grammar contexts, scope ownership, substitutions, token locations, and unresolved-reference state are available.
- The parser should publish both a structured lineage document and a human-readable explanation derived from that document.
- RMCP should stop being the semantic authority. It should request, display, validate, and audit parser-produced lineage, with only compatibility handling for older parser versions.
- Use [rmcp/docs/vendor/pss-parse/5.1.4/symbol-table-bucket-reference.md](rmcp/docs/vendor/pss-parse/5.1.4/symbol-table-bucket-reference.md) and the other manifest-selected 5.1.4 guides as the normative contract.

## Current evidence
- Audit the 6,549-row capture at [rmcp/tmp/strict_raw/live_queries_11-19.raw.jsonl.gz](rmcp/tmp/strict_raw/live_queries_11-19.raw.jsonl.gz). Of 6,549 rows, 6,330 parse cleanly and RMCP currently emits a non-empty explanation containing every output name.
- Treat that 100% rate only as presentation coverage. RMCP currently follows outer `interface` entries and limited `filters`; it does not prove complete or accurate lineage through every scope and role.
- Stratify assessment examples across tuple/query substitutions, scalar and correlated subqueries, CTEs, derived tables, UNION/INTERSECT, windows, VALUES, joins, ordered output aliases, and depth-6+ scope chains.
- Every parser-agent finding must include its complete runnable SQL, endpoint, expected graph, actual graph, and diagnostics.

## RMCP code worth mining
- [rmcp/src/utils/symbolTableExplainer.ts](rmcp/src/utils/symbolTableExplainer.ts)
  - **Useful:** population/lookup narration, substitution-reference display text, recursive interface presentation, UNION branch narration, and existing CLI/UI wording.
  - **Incomplete:** `traceLineage514` delegates to the 5.1.3 implementation; only output `interface` plus limited `filters` are handled.
  - **Do not port as-is:** `findLayerByRefDeep` searches the whole tree and can cross sibling-scope boundaries; unresolved refs are flattened to generic ambiguous prose; empty interface lineage is assumed to be a literal or scalar function.
- [rmcp/tools/symbolTableFormat.js](rmcp/tools/symbolTableFormat.js)
  - **Useful:** `def_*` naming, version metadata plumbing, outer-layer selection, and subordinate-layer key normalization.
  - **Needs parser-native replacement:** scope resolution must use the active grammar/symbol-table parent and must not rely on global deep search.
- [rmcp/tools/columnSources.js](rmcp/tools/columnSources.js)
  - **Useful:** cycle guard, concise output-column-to-source projection, and branch phrasing.
  - **Incomplete:** follows interface hops only, does not pair role buckets with `dependent_queries`, and does not fully model `def_*` set-operation children.
- [rmcp/python/testing/test_symbol_table_lineage_514.py](rmcp/python/testing/test_symbol_table_lineage_514.py)
  - **Useful:** live-JAR invocation patterns and existing bound-query samples.
  - **Insufficient oracle:** primarily checks prose markers rather than graph topology, leaf identity, participation roles, and token proof.
- Existing reports under [reports/panto_strict_rerun/live_2026-11-19](reports/panto_strict_rerun/live_2026-11-19) provide complete reproductions for ordered aliases, compound `IN`, cross-scope casing, query substitutions, and interface-loss adjudication.

## Parser-owned structured lineage
- Add a parser result component that records:
  - parser artifact/API version and statement root;
  - published scope tree (`def_queryN`, `def_unionN`, `def_intersectN`, `def_valuesN`, DML roots, and `SCRIPT` statements);
  - per-output lineage graphs with ordered hops and terminal leaves;
  - role participation for filters, grouping, ordering, windows, assignments, joins, and relational modifiers;
  - optional source-to-impact trees anchored on physical table columns;
  - token/location proof for every resolved occurrence;
  - explicit `physical`, `query`, `values`, `tuple_substitution`, `query_substitution`, `scalar_substitution`, `PRESUMED`, and `unresolved` leaf kinds.
- Generate human-readable explanation text from this structure inside the parser so prose and machine lineage cannot disagree.

## Scope and recursion rules
- Resolve each reference through the current scope's `table_alias`, `context_list`, or `dependent_queries`; never locate a same-named `def_queryN` through unrestricted global search.
- Alternate through every intermediate scope: reference → source mapping → child `def_*` → child `interface` → next reference. Never skip a child interface to jump directly to a grandchild.
- Pair every clause/role bucket with `dependent_queries` entries whose `type` matches that role. This is required for unnamed scalar subqueries and nested `EXISTS`/`IN` constructs that may have no ordinary archive entry.
- Handle ordered select-list alias hops (`table_ref=queryN`), set-operation `query_column` alignment, fan-out lineage, correlated outer references, modifier `derivation`, and cycle detection.
- Preserve scope topology in the result so scalar subqueries are shown as contributing or constraining branches rather than flattened into parent filters.

## Substitution behavior
- Distinguish tuple substitutions from query substitutions using the parser's substitution type.
- A tuple substitution is a row-source leaf and may appear in the physical/tuple dictionary contract.
- A query substitution is a query-backed source, not a physical table. When its SQL body is unavailable, create a placeholder `def_queryN` scope instead of inventing a table-dictionary entry.
- Populate that placeholder interface from external column references made against the query variable and label those interface leaves `PRESUMED`.
- Keep `PRESUMED` distinct from resolved and unresolved lineage:
  - resolved means the supplied query body proves the output;
  - `PRESUMED` means the surrounding SQL requires that output from an unavailable query body;
  - unresolved means no valid source binding can be established.

## Parser acceptance criteria
- Add graph assertions rather than prose-only snapshots:
  - every outer interface output has zero or more explicit lineage branches;
  - every resolved branch terminates in a typed leaf;
  - every hop remains within a legal parent/child/context visibility path;
  - all relevant `dependent_queries` are attached to their participation role;
  - physical leaves agree with table dictionaries;
  - query-backed leaves pass through each intermediate query interface;
  - case-insensitive unquoted identifiers merge without losing or duplicating token occurrences;
  - cycles terminate with an explicit cycle/unresolved result.
- Seed parser tests with complete SQL from the existing defect reports and add focused cases for scalar SELECT expressions, correlated scalar predicates, CTE chains, nested set operations, windows, VALUES, PIVOT/UNPIVOT, scripts, and DML.
- Run the parser-produced lineage over the live corpus and report resolvable-leaf rate, `PRESUMED` rate, unresolved rate, maximum hop depth, dependent-subquery coverage, clause-role coverage, and source-impact reachability by SQL class.

## Thin RMCP integration after parser delivery
- Extend the parser client and endpoint payload types to receive the structured lineage document and parser-produced narrative.
- Update `cli-parse-and-explain`, extension UI, and SQL parse-summary skill to render that payload directly.
- Retain the existing RMCP explainer only as a version-keyed compatibility fallback for parser versions that do not publish lineage.
- Add contract checks that the parser narrative and structured graph identify the same outputs and terminal sources; do not duplicate parser traversal rules in RMCP.

## Handoff deliverable
- Produce one self-contained parser-agent markdown package containing:
  - the ownership and output contract above;
  - annotated RMCP code inventory;
  - live-corpus measurements and prioritized query classes;
  - complete runnable SQL for every demonstrated gap;
  - expected structured lineage for each case;
  - parser-side implementation checklist and acceptance matrix;
  - the eventual thin RMCP consumer contract.
