# SQL grammar and walker — 5.1.4 master workplan

**API / docs version target:** `5.1.4` (coordinate with parent `pss-parent` / `pss-parse` artifact when shipping)  
**Branch context:** `Fall-2026-Extensions` (Spring-2026-Extensions closed with 5.1.4; open Spring items roll over here)  
**Primary code:** `parse/src/main/antlr4/sql/SQLSelectParser.g4`, `SqlParseEventWalker.java`, `SqlParseSymbolTreeHelper.java`, `generators/SQLStatementGenerator.java`  
**Status:** Active — open entries below  
**Audience:** PSS parser maintainers, Panto / Query Tool consumers

**This file is the single planning source** for open parser/grammar/walker/generator/hygiene work on the 5.1.4 line. Older standalone workplans in `parse/documents/` are **retired** (stub headers only); detail for completed programs lives in git history or RMCP handoff docs where noted.

**Document ownership:** maintain release scope, decisions and current status
here. The [coverage inventory](coverage/sql_walker_exit_method_gaps.md) owns
current Entry 11 measurements and target rows. The
[Slice D evidence record](slice-d-array-function-reconciliation.md) is frozen
at its T023/T024 milestone; do not append later release-status updates there.
Link to those records rather than copying their inventories or measurements.

**Grammar changelog reference:** [sql-grammar-extensions-since-2026-01-01.md](./sql-grammar-extensions-since-2026-01-01.md)  
**Lexer golden policy:** `.cursor/rules/lexer-token-id-golden-policy.mdc`

---

## How to add an entry

1. Add a row to the progress tracker.
2. Add `## Entry N — …` with goals, phases, acceptance, out of scope, and links to **briefs** (keep long specs in sibling `*.md` files, not duplicate megabytes here).
3. Retire any ad-hoc plan by replacing it with a stub pointing to the entry number.

---

## Progress tracker (open work)

| Entry | Feature | Kind | Status |
|-------|---------|------|--------|
| **1** | Target-dialect date/time manipulation + Snowflake/Postgres dependency affinity | Enhancement | **In progress** — Slices A-D complete with explicit matrix traceability; Slice E deferred until after 5.1.4 release; Slice F complete except explicitly deferred generator work (Entry 10) |
| **2** | Snowflake PIVOT quoted unaliased IN-list identifiers | Enhancement | **Complete** (closed 2026-11-17) — Stage A/B/C delivered and live-query fixture closeout re-scan verified |
| **3** | Snowflake `PARSE_*` + `:` semi-structured field access | Enhancement | **Complete** (closed 2026-11-17) — Stage A/B/C/D delivered with cohort regression bundle green |
| **4** | Snowflake `ARRAY` syntax and functions (5.1–5.12) | Enhancement | **Complete** (2026-11-19) — Slices A–F closed within the release scope below |
| **5** | Simple Jinja / dbt substitutions (6.1–6.4) | Enhancement | **Not started** |
| **6** | Deep Jinja / SCRIPT prefix *(optional)* (7.x) | Enhancement | **Not started** |
| **7** | Panto variable types × parser endpoints — test backlog (8.1–8.2) | Test / docs | **Not started** |
| **8** | Panto cardinality & grammar-safe dynamic AST expansion (9.x) | Enhancement (exploratory) | **Not started** |
| **9** | DDL structured options (`IF NOT EXISTS`, `OR REPLACE`, …) | Enhancement | **Not started** |
| **10** | SQL statement generator — post-milestone completion | Enhancement | **Not started** (milestone ✅) |
| **11** | Walker `exit*` JaCoCo coverage — Tier 2 / 3 residuals | Test / hygiene | **Complete** (2026-11-19) — focused tests and source-backed residual dispositions; Tier 3 excluded |
| **12** | Helper dead-code hygiene — Phases C–D | Hygiene | **Complete** (closed 2026-11-16) |
| **13** | SELECT-list subquery alias scope (false `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE`) + operand `lookup` wrapper | Defect | **Complete** (2026-11-10) |
| **14** | Join extension present → `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE` reported as severe warning + `JOIN_EXTENSION_REQUIRED` (whole-query) | Defect (release blocker) | **Complete** (2026-11-10) |
| **15** | Scope-aware join extension deferral (per-scope diagnostic capture) | Enhancement | **Not started** (follows **14**) |
| **16** | Set operation with a query variable member loses its output interface (false `QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS`) | Defect (release blocker) | **Complete** (2026-11-11) |
| **17** | `DATEADD` / `DATEDIFF` date parts resolved as columns | Defect | **Merged into Entry 1** (2026-11-11) |
| **18** | Snowflake non-reserved words rejected (`position`, `input`, `path`, `truncate()`), array literals, and walker crash after syntax errors | Defect | **Closed** (2026-11-11) — ten bound-query regressions fixed; unused s82233 discrepancy obsolete and immaterial, accepted without remediation |
| **19** | RMCP Phase 2 acceptance-test closure (`VALUES` joined UNION branch; ordered aggregates) | Test / verification | **Complete** (closed 2026-11-16) |
| **20** | Qualified column substitutions on visible query aliases rejected as missing interface columns | Defect (5.0.0 parity blocker) | **Complete** (closed 2026-11-13) — targeted fix + warning policy + parity re-evaluation delivered; see Entry 20 section |
| **21** | QUERY variables in FROM/JOIN: author guidance and tuple-versus-query distinction | Diagnostic enhancement | **Proposed for 5.1.4 remediation** (2026-11-19) — policy discussion pending; not implemented |
| **22** | Conflicting types for repeated occurrences of one substitution name | Diagnostic / consistency enhancement | **Proposed for 5.1.4 remediation** (2026-11-19) — severity and compatibility policy pending; not implemented |
| **23** | Opaque QUERY-variable row sources: assumed column binding and symbol-table representation | Resolution / metadata enhancement | **Proposed for 5.1.4 remediation** (2026-11-19) — representation and diagnostic policy pending; not implemented |
| **24** | Successive IN lists in SELECT CASE silently overwrite pending column/token collection | Defect | **Confirmed; fourth 5.1.4 remediation** (2026-11-19) — root cause traced; ready for targeted implementation; not implemented |
| **25** | Datetime story gaps: TRUNCATE alias, complete part coverage, function-specific subsets, and shared-alias affinity | Defect / story completion | **Fifth 5.1.4 remediation** (2026-11-20) — deficiencies verified on the existing local JAR; correction substeps approved for planning; not implemented |
| **26** | Unicode string-literal preservation in MCP and bulk preprocessing | Defect | **Complete in source** (2026-11-20) — focused tests pass; local release JAR not rebuilt |
| **27** | Cross-scope case fragmentation in the global table dictionary | Defect | **Reported; not yet source-verified or fixed** |
| **28** | Ordered output-alias lineage depends on consumer name or temporal-looking alias | Defect | **Reported; root cause and relationship between reproductions unverified; not fixed** |
| **29** | Parser-owned structured lineage and explanation | Enhancement / design | **Proposed; contract, scope, and acceptance thresholds pending** |

**5.0.0 → 5.1.4 parity gate update:** Entry **20** is closed (2026-11-13) and
no longer blocks parity. Remaining parity/release checks continue to be the
corpus, consumer, generator, and release verification gates documented in this
workplan.

### 5.1.4 release completion checklist (scoped subset)

This checklist is the release-scope subset selected for the 5.1.4 JAR closeout.
Items not listed here are out of scope for this release.

**Syntax implementation milestone — 2026-11-19:** the planned array-function
syntax implementation is complete through T031, including table `FLATTEN` and
the higher-order `FILTER`, `TRANSFORM` and `REDUCE` forms. Underscored
`LATERAL_FLATTEN` compatibility remains deferred; existing `LATERAL` modifier
support is unchanged. The final implementation batch added 224 independent
cases, with 1,688 focused tests passing and no lexer-ID changes. This milestone
does not waive the remaining coverage, audit, corpus, documentation or release
gates listed below, and is not whole-release certification.

- **Entry 4 — required for 5.1.4**
  - **Slice D — COMPLETE (2026-11-19):** all 42 canonical names, Entry 4.1–4.12,
    GC1..GC4 and former E work are accounted for. The
    [frozen final sign-off](slice-d-array-function-reconciliation.md#t023t024-final-slice-d-sign-off-2026-11-19)
    retains the test results, matrices and accepted family evidence.
    `LATERAL_FLATTEN` compatibility stays deferred beyond 5.1.4.
  - **Former Slice E function work — complete in D:** scalar/table FLATTEN and FILTER/TRANSFORM/REDUCE are delivered under T027..T031 and included in T023/T024. There is no second deferred array-function backlog.
  - **Slice F (non-generator only) — COMPLETE (2026-11-19):** existing
    T023/T024 evidence accepted; see the delivery closeout under
    [Entry 4](#entry-4--snowflake-array-syntax-and-functions).
    - Exclude any generator-expansion work from 5.1.4 (deferred to Entry 10).

- **Entry 11 — scoped coverage COMPLETE (2026-11-19)**
  - Focused strict tests and source-backed residual dispositions close the
    approved targets recorded in the
    [current coverage inventory](coverage/sql_walker_exit_method_gaps.md).
    Scope is limited to that approved twelve-method set, not a new broad audit.
  - **Tier 3 is explicitly out of 5.1.4 scope.**

- **Entries 21–23 — newly recorded 5.1.4 remediation proposals (2026-11-19)**
  - Discuss FROM/JOIN query-variable guidance, repeated-name type conflicts,
    and opaque query-source column binding before implementation.
  - These are open assessment/design items, not approved diagnostic severities,
    symbol-table formats, or completed fixes. Additional integration findings
    may extend this remediation list.

- **Entry 24 — fourth 5.1.4 remediation: confirmed IN-list collection defect**
  - Preserve all source columns and occurrence tokens across successive
    ordinary IN lists in SELECT CASE expressions.
  - Root cause, reproduction, runtime trace, and completed characterization
    are recorded in [Entry 24](#entry-24--successive-in-lists-silently-overwrite-column-collection).
    Implementation can start from that evidence without repeating discovery;
    post-fix regression verification is still required.

- **Entry 25 — fifth 5.1.4 remediation: datetime story deficiencies**
  - Correct datetime TRUNCATE handling, missing supported parts/aliases,
    per-function part subsets, and false dialect conflicts for shared aliases.
  - Implement multiple named date/time-part allowlists for functions with
    different supported subsets, backed by shared part/alias definitions.
  - [Entry 25](#entry-25--datetime-story-completion-and-function-specific-part-lists)
    records verified baseline examples and correction/acceptance substeps.
    Historical Entry 1 slice completion does not waive these later findings.

- **Entry 26 — Unicode literal preservation: complete in source**
  - MCP JSON, stream, and bulk preprocessing preserves literal data while
    retaining existing syntax normalization. Focused tests passed; the local
    release JAR still needs the ordinary candidate build and owner validation.

- **Entries 27–29 — additional 5.1.4 handoffs**
  - Resolve cross-scope dictionary casing and ordered-alias lineage defects;
    assess and specify parser-owned structured lineage. The reports contain
    black-box evidence, not source-verified root causes or approved schemas.

**Latest scope reduction — deferred beyond 5.1.4, not release prerequisites:**
- **Entry 7, entirely:** endpoint parity, type×grammar coverage and their
  traceability/accounting, including the formerly conditional DDL work.
- **Entry 15, entirely:** C1/C2/C3 characterization as well as the broader
  scope-aware diagnostic rework.

Their detailed backlogs remain below for a later release. This reduction does
not remove Entry 4's array-specific matrix requirements, the approved Entry 11
Tier 2 work, or final corpus/documentation/release validation.

### Wrap-up sequence — local candidate first; Archiva publication on hold

**Owner direction (2026-11-19):** generate the parser and companion
documentation JARs locally for manual integration and testing in the RMCP
extension project. **Do not publish to Archiva until the owner explicitly
authorizes that separate step.** A successful local build or RMCP test result
does not itself authorize publication.

1. [ ] **Prepare and verify the local candidate.** Finish consumer/release
   documentation and the remaining frozen-corpus comparison using the existing
   accepted dispositions. Build the parser and docs together from the repository
   root with `mvn -Prelease-tests clean verify`; inspect the
   packaged artifacts and documentation manifest. This is local packaging and
   verification, not publication. Pause `java.autobuild.enabled` before the
   build and restore its prior value afterward, even on failure or cancellation;
   see [local release instructions](../../README.md#local-release-jars).
   Standard `target/` directories are authoritative for release JARs;
   `target-maven/` remains for isolated testing. Principal handoff artifacts:
   - `parse/target/pss-parse-5.1.4-1-fat.jar`
   - `pss-parse-docs/target/pss-parse-docs-5.1.4-1.jar`
   - `pss-parse-docs/target/pss-parse-docs-5.1.4-1-agent-kit.zip`
     (optional workspace-installable consumer documentation; generated by the
     docs module's `package` phase alongside the documentation JAR)
   Record the source revision and artifact SHA-256 checksums for the candidate.

   **Local build complete — 2026-11-19 10:42:30 -05:00:** the initial reactor
   build used `mvn -Pisolated-build,release-tests clean verify` and succeeded
   with **4,311 reported, 4,271 passed, 40 skipped, zero
   failures/errors**; the release profile enabled the slow regressions.
   Both artifact versions are `5.1.4-1`; the docs API version is `5.1.4`.
   All eight bundled guides match their canonical sources, and the parser
   includes its runtime dependencies and Java 17-compatible walker bytecode.
   Candidate source matches `cfcb948` (the only commit during the build
   changed this rollout plan, not parser or bundled-doc sources).
   The verified JARs were subsequently relocated byte-for-byte to the standard
   `target/` handoff paths above, removing their isolated-output copies without
   another build or any checksum change.

   **Documentation-only refresh — 2026-11-19 11:01:48 -05:00:**
   `mvn -pl pss-parse-docs package` regenerated the documentation JAR and
   added the optional agent-kit ZIP. All eight ZIP guides were verified
   byte-for-byte against both canonical sources and the documentation JAR;
   manifest identity, filtered bootstrap links, and ZIP integrity passed.
   The parser JAR and its release-test evidence above are unchanged.
   The table below records the current handoff checksums, including the
   refreshed documentation JAR. A subsequent docs-only rebuild at
   **11:58:54 -05:00** renamed the kit bootstrap to
   `PSS-PARSER-AGENT-KIT.md` to avoid collisions with other libraries' kits;
   the updated instruction pointer, all eight guides, and ZIP integrity
   were verified again without changing the parser candidate.

   | Handoff artifact | SHA-256 |
   |------------------|---------|
   | `pss-parse-5.1.4-1-fat.jar` | `4b7cde697d03cc5fc80189ab3797ff87ff13a46cd91e3890a09b0da0fa6ce3cf` |
   | `pss-parse-docs-5.1.4-1.jar` | `0bce15cc616f71d2bc469a420effbf071af5867921c693cd43e3a5ef990197d1` |
   | `pss-parse-docs-5.1.4-1-agent-kit.zip` | `5012eadf8bc8290d916312efacd9fe817cd6638049527df6f91e7836680b48f0` |

   The standard parser and test JARs were also generated locally. No Maven
   install, deploy or repository upload was performed. This completes the
   local packaging/test portion only; the separate frozen-corpus comparison
   and final release sign-off remain open.

2. [ ] **Owner-managed RMCP integration and acceptance.** The owner manually
   integrates the local JARs into the RMCP extension project, runs additional
   tests and reports the results. Await that feedback before final sign-off.
3. [ ] **Resolve any RMCP findings locally.** If changes are needed, fix and
   validate them, regenerate the local parser/docs candidate and repeat RMCP
   acceptance. Keep the existing unpublished `5.1.4-1` Maven coordinates
   (API/docs version `5.1.4`) during these iterations; do not increment the
   artifact version merely because another local candidate was built.
   Record new checksums after every rebuild.

   **RMCP datetime findings (2026-11-19):** DATE_PART comma and EXTRACT
   comma now use dedicated metadata rules, preventing bare date parts from
   becoming source columns. TIME_SLICE also accepts the existing END token
   as a bare boundary, consistent with START and quoted boundaries.
   [SqlEventWalkerDatetimeMetadataTests](../src/test/java/sql/walker/SqlEventWalkerDatetimeMetadataTests.java)
   adds 578 independent six-golden cases, exact diagnostics, and direct-walker/
   production-access parity across 14 function names. The seven-suite datetime
   selection passed **1,090 tests, zero failures/errors/skips**. Running the new
   suite against the unchanged local release JAR produced **141 failures out
   of 578**, confined to the extraction-comma cases and bare END. All **479**
   official lexer constant declarations match that JAR. The release JARs above
   have **not** been rebuilt with these fixes. Contextual-keyword findings
   are covered by the follow-up below. The owner confirmed that the
   malformed-SQL findings require RMCP expectation changes, not parser
   changes; the earlier recovery edits and corresponding test edits were
   reverted, preserving existing 5.1.4 recovery behavior.

   **RMCP contextual-name follow-up (2026-11-19):** Audited all **128** late
   word keywords. Added the **31** previously omitted words to the shared
   name allowlist, including MODE/TRUNCATE, string-function names, table-
   function argument names, extraction fields, QUALIFY/LATERAL, SETS/WITHIN,
   and the legacy QUERY_HSTORY spelling. Qualified wildcards now use the same
   identifier rule, with no change to their AST shape. Fixed internal query-
   scope prefix collisions exposed by physical/alias names such as
   `query_hstory` and `delete`.
   [SqlEventWalkerContextualKeywordTests](../src/test/java/sql/walker/SqlEventWalkerContextualKeywordTests.java)
   adds **517** independent six-golden cases, exact diagnostics, production-
   access parity, and the four original MODE/TRUNCATE RMCP queries. The
   ten-suite focused selection passed **1,845 tests, zero failures/errors/
   skips**. The first full run caught an implicit-alias ambiguity in the
   existing `QUALIFY <condition>` template form. Corrected clause precedence
   and added guards for all three variable-token forms; the subsequent
   keyword/predicate/QUALIFY selection passed **642 tests, zero failures/
   errors/skips**. All **479** lexer constant declarations remain unchanged.
   After reverting those recovery changes, the full parser release selection
   (`mvn -f parse/pom.xml -Pisolated-build,release-tests test`) passed:
   **5,406 reported; 5,366 passed, 40 skipped, zero failures/errors**
   (exit 0). This includes both new regression suites.
   No release JAR rebuild or publication was performed.

4. [ ] **Separate publication gate — BLOCKED pending explicit authorization.**
   After RMCP acceptance, obtain the owner's explicit instruction to publish
   to Archiva. Until then, do not run Maven `deploy`, direct repository uploads
   or any publishing automation. Publish the exact approved candidate; if
   publication requires a rebuild, revalidate that candidate before upload.

---

## Completed baselines (no open entry — do not reopen)

| Program | Shipped / closed | Where detail lives |
|---------|------------------|-------------------|
| **EXTRACT / DATE_PART `FROM`** | ✅ Aug 2026 | `SqlEventWalkerExtractTests`; git: `extract-dialect-capture-workplan.md` |
| **Symbol table resolution consolidation** Phases 1–20 | ✅ Aug 2026 | [symbol-table-resolution-consolidation-worklist.md](./symbol-table-resolution-consolidation-worklist.md) (historical) |
| **RMCP parser-defects Phase 2 implementation** (set-op, `VALUES`, `CASE`, parse perf 2.8–2.11, etc.) | ✅ 2026-08–09; residual acceptance matrix → **Entry 19** | [parse/docs/rmcp-handoff/5.1.4-panto-outstanding/](./docs/rmcp-handoff/5.1.4-panto-outstanding/); set-op egress: `.cursor/rules/set-op-convert-egress-scoping.mdc` |
| **Java 21 / parent 5.1.4-1 toolchain** | ✅ Sep 2026 | git: `java-21-upgrade-workplan.md` |
| **2.5 Jinja DNC rollup closure** | ⏳ | **Entry 5** subtask **5.4** (depends on **5.1**) |

---

## Entry 1 — Target-dialect date/time manipulation and dependency affinity

### Product story (authoritative)

Update and release the parser jar to recognize and construct proper AST and symbol table entries for the **DATEADD** and **TIMEADD** functions in Snowflake's SQL dialect. If this assumption is incorrect, defer this story until the capability is available in a newer parser.

The Snowflake product story remains the delivery driver. The implementation must also classify equivalent PostgreSQL-only function, part, and grammar forms through the same statement dialect mechanism, so adding Snowflake support does not create a one-sided dialect detector.

Subtree will be structured with three mandatory parts: Tree should have been structured with **"level"** for the part of the date to be modified, **"increment"** for the integer factor, and **"date part"** for the expression generating the date or timestamp.

Investigate if integer value and date or time expressions are just predicands. If so include tests for complex functions in each place. Confirm that substitution variables placed in these spots get classified correctly as predicand variables.

Add all standard Snowflake date or time part constants recognition. Add these terms as non-keyword enumerated types by adding grammar validation logic to the rule. Fix tests that might expect those entries to be column references at the moment. Extend predicated family of rules to include these functions.

While we're at this, add support for all Snowflake date and time functions, including:

- **EXTRACT** *(FROM form ✅; comma form stays `routine_invocation`)*
- **DATE_PART** *(FROM form ✅)*
- **DATEDIFF**, **DATE_TRUNC**, **TRUNC** (datetime; not DDL **TRUNCATE**)
- **TIMESTAMPADD**, **TIMESTAMPDIFF**, **TIMEADD**, **TIMEDIFF**
- **TIME_SLICE**, **LAST_DAY**, **NEXT_DAY**, **PREVIOUS_DAY**

Build rules for subsets of the date and time parts, and add all exit methods for rules.

### Dialect dependency marking (required)

Recognizing a construct is not sufficient: when correct execution depends on a target dialect, the statement must also be marked with that dialect affinity. Reuse the existing statement-level mechanism in `SqlParseEventWalker`:

- `recordStatementDialectGrammarHit(SqlGrammarDialect.SNOWFLAKE|POSTGRES, …)` increments the appropriate statement counter and emits one `STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR` or `STATEMENT_POSTGRES_DIALECT_GRAMMAR` warning;
- seeing both affinities in one statement emits `STATEMENT_MIXED_DIALECT_GRAMMAR` FATAL;
- scripts reset the counters and flags for every statement;
- `SqlGrammarDialectRuleRegistry` is the registry for distinct grammar rules; semantic registries/callbacks are appropriate when shared grammar is classified by a function name, argument form, or level/part value (as already done by `ExtractDatetimeFieldAffinity` and `SqlBareValueExpressionRegistry`).

This warning/counter pair is the current “query depends on Snowflake/Postgres” flag; do not add a competing symbol-table key. If consumers later require a serialized boolean or enum in `Snippet`, derive it from the same per-statement hit collector rather than adding a second classifier.

Branch the grammar when the dialects have different syntax, arity, AST, or keyword roles. Do **not** fork an otherwise identical rule merely to raise a flag: use a semantic affinity registry and call the same collector. Shared ANSI/Snowflake/Postgres forms remain neutral. A schema-qualified function call or an explicitly modeled user-defined function with the same name as a built-in must remain neutral unless it uses dialect-specific syntax.

#### Agent implementation matrix — date/time functions

“Flag” means call the statement dialect collector at the function token. “Shared” means accept without either flag; a dialect-specific part or argument form may still flag the statement separately.

| Construct accepted by the grammar | Target affinity | Detection / grammar action | Flag | Notes / required tests |
|-----------------------------------|-----------------|----------------------------|------|------------------------|
| `DATEADD(part, increment, value)`, `TIMEADD(…)`, `TIMESTAMPADD(…)` | Snowflake | Dedicated add-family production; distinguish `TIMESTAMPADD` from `TIMESTAMP` lexer split | Snowflake | All three aliases; canonical parts + documented abbreviations; substitutions in slots 2–3 |
| `DATEDIFF(part, left, right)`, `TIMEDIFF(…)`, `TIMESTAMPDIFF(…)` | Snowflake | Dedicated diff-family production | Snowflake | Preserve argument order; part is never a column |
| `TIME_SLICE(…)` | Snowflake | Dedicated production because arity and optional alignment argument are dialect-specific | Snowflake | Validate supported parts and optional arguments |
| `LAST_DAY(…)`, `NEXT_DAY(…)`, `PREVIOUS_DAY(…)` | Snowflake | Dedicated productions (including optional part where Snowflake permits it) | Snowflake | Do not infer affinity from an unqualified UDF call until the call matches the built-in shape |
| datetime `TRUNC(value, part)` / `TRUNC(value)` | Snowflake | Dedicated datetime alternative, separate from numeric `TRUNC` and DDL `TRUNCATE` | Snowflake | Numeric `TRUNC(number[, scale])` remains neutral unless a target-specific form proves otherwise |
| `EXTRACT(part FROM value)` | Shared shell | Fold extraction beneath `datetime_function`; classify `part` using the part matrix below | Part-dependent or neutral | Migrate AST under the Phase 1 extraction-unification action; preserve accepted source semantics |
| `DATE_PART(part FROM value)` | Shared parser extension | Unify dedicated FROM/comma alternatives beneath `datetime_function`; classify `part` | Part-dependent or neutral | Preserve accepted FROM syntax; AST refactoring is permitted by the Phase 1 follow-up below |
| `DATE_PART(part, value)` | Shared Snowflake/Postgres name | Dedicated DATE_PART alternative, accepting supported bare and quoted parts as metadata | Part-dependent or neutral | Remove built-in comma calls from generic argument collection; retain qualified UDF behavior and correct affinity |
| `DATE_TRUNC('part', value)` | Shared Snowflake/Postgres name | Shared production; classify the part and any dialect-specific overload | Part-dependent or neutral | Do not flag from the function name alone |
| `DATE_BIN(stride, source, origin)` | PostgreSQL | Dedicated Postgres production | Postgres | Interval stride; three arguments |
| `AGE(…)`, `MAKE_DATE(…)`, `MAKE_TIME(…)`, `MAKE_TIMESTAMP(…)`, `MAKE_TIMESTAMPTZ(…)`, `MAKE_INTERVAL(…)` | PostgreSQL | Dedicated built-in alternatives or semantic function-shape registry | Postgres | A schema-qualified same-name UDF is neutral |
| `JUSTIFY_DAYS(…)`, `JUSTIFY_HOURS(…)`, `JUSTIFY_INTERVAL(…)` | PostgreSQL | Semantic function-shape registry | Postgres | Interval argument |
| `CLOCK_TIMESTAMP()`, `STATEMENT_TIMESTAMP()`, `TRANSACTION_TIMESTAMP()`, `TIMEOFDAY()` | PostgreSQL | Zero-argument built-in alternatives / semantic registry | Postgres | Do not confuse shared `CURRENT_TIMESTAMP` with these |

The Postgres rows are part of the dialect-symmetry requirement for Entry 1. They may be delivered after Snowflake slices A–C, but the grammar/registry design must leave explicit branches for them and must not classify their names as ordinary columns.

#### Approved parser grammar design (2026-11-11)

Dedicated date/time productions must precede the generic `value_expression_primary` / `routine_invocation` path so a level such as `year` is parsed as part metadata, never as a `column_reference`. The original EXTRACT/DATE_PART AST preservation requirements are superseded by the 2026-11-12 Phase 1 extraction-unification action below; grammar examples here describe the initial design until that action is implemented.

The umbrella grammar rule and the common AST key are both named
`datetime_function`. Family-specific grammar rules specialize this parent;
they do not introduce separate top-level AST keys.

```antlr
predicand_primary
  : extract_expression
  | date_part_expression
  | datetime_function                     // NEW: before generic functions
  | value_expression_primary
  | string_value_expression
  | sign numeric_primary
  | trim_function
  | null_literal
  | variable_identifier
  | puml_constant_identifier
  | position_function
  ;

numeric_primary
  : extract_expression
  | date_part_expression
  | datetime_function                     // NEW
  | value_expression_primary
  ;

datetime_function
  : snowflake_datetime_add_expression
  | snowflake_datetime_diff_expression
  | datetime_trunc_expression
  | snowflake_time_slice_expression
  | snowflake_day_boundary_expression
  | postgres_datetime_expression
  ;
```

Use one non-column part production across the new functions. Keep `extract_field` as a compatibility wrapper. The final `identifier` alternative accepts documented aliases and extensible engine parts without collecting them as columns; the shared Java registry performs canonicalization and affinity classification.

```antlr
extract_field
  : datetime_part
  ;

datetime_part
  : primary_datetime_field
  | time_zone_field
  | extended_datetime_field
  | snowflake_datetime_part
  | character_literal
  | identifier
  ;

snowflake_datetime_part
  : DAYOFMONTH | DAYOFWEEK | DAYOFWEEKISO | DAYOFYEAR
  | WEEKISO | WEEKOFYEAR
  | EPOCH_SECOND | EPOCH_MILLISECOND | EPOCH_MICROSECOND
  ;
```

The dedicated rules must label arguments semantically. Representative shapes:

```antlr
snowflake_datetime_add_expression
  : function=snowflake_datetime_add_function
    LEFT_PAREN level=datetime_part
    COMMA increment=value_expression
    COMMA datetime_value=value_expression RIGHT_PAREN
  ;

snowflake_datetime_diff_expression
  : function=snowflake_datetime_diff_function
    LEFT_PAREN level=datetime_part
    COMMA left_value=value_expression
    COMMA right_value=value_expression RIGHT_PAREN
  ;

datetime_trunc_expression
  : DATE_TRUNC LEFT_PAREN level=datetime_part
    COMMA datetime_value=value_expression RIGHT_PAREN
  | TRUNC LEFT_PAREN datetime_value=value_expression
    COMMA level=datetime_part RIGHT_PAREN
  ;
```

Apply the same pattern to the remaining matrix rows. Bare built-ins that match these shapes use the dedicated rules and dialect classifier. Schema-qualified same-name calls remain generic, dialect-neutral UDF calls. One-argument `TRUNC(value)` remains on the generic path unless semantic type evidence can distinguish datetime truncation from neutral numeric truncation.

#### Approved `datetime_function` AST contract (2026-11-11)

All new dedicated Snowflake and PostgreSQL constructions use a common top-level `datetime_function` key. Do not place them under generic `function`, and do not encode their arguments as positional `parameters`: that would allow a generic generator to render a date/time part as an ordinary expression.

```text
DATEADD(year, 1, created_at)

{datetime_function={
  function_name=DATEADD,
  level={canonical=YEAR, text=year, form=KEYWORD},
  increment={literal=1},
  value={column={name=created_at}}
}}

DATE_TRUNC('month', created_at)

{datetime_function={
  function_name=DATE_TRUNC,
  level={canonical=MONTH, text=month, form=STRING},
  value={column={name=created_at}}
}}
```

Part-like values use `{canonical, text, form}`:

- `canonical` is the registry-normalized meaning used for validation and dialect affinity;
- `text` preserves the authored spelling, including aliases such as `mm`;
- `form` is `KEYWORD`, `IDENTIFIER`, or `STRING`;
- generators write `KEYWORD` / `IDENTIFIER` text without quotes and emit `STRING` text as an escaped SQL character literal;
- canonicalization must never silently replace authored text during ordinary regeneration.

`function_name` preserves the authored case, as ordinary routine calls do.
`function_name` is the approved discriminator; there is no `invocation` AST
key. Generators dispatch once on `datetime_function`, then compare `function_name`
case-insensitively to select an ordered signature. Preserve the authored name
when generating SQL; never infer argument order from map insertion order.

| `function_name` value(s) | Permitted subtree keys in SQL argument order |
|-------------------|-----------------------------------------------|
| `DATEADD`, `TIMEADD`, `TIMESTAMPADD` | `level`, `increment`, `value` |
| `DATEDIFF`, `TIMEDIFF`, `TIMESTAMPDIFF` | `level`, `left`, `right` |
| `DATE_TRUNC` | `level`, `value` |
| datetime `TRUNC` | `value`, optional `level` |
| `TIME_SLICE` | `value`, `slice_length`, `level`, optional `boundary` |
| `LAST_DAY` | `value`, optional `level` |
| `NEXT_DAY`, `PREVIOUS_DAY` | `value`, `weekday` |
| `DATE_BIN` | `stride`, `source`, `origin` |
| one-argument `AGE` | `value` |
| two-argument `AGE` | `left`, `right` |
| `MAKE_DATE` | `year`, `month`, `day` |
| `MAKE_TIME` | `hour`, `minute`, `second` |
| `MAKE_TIMESTAMP` | `year`, `month`, `day`, `hour`, `minute`, `second` |
| `MAKE_TIMESTAMPTZ` | timestamp fields plus optional `timezone` |
| `MAKE_INTERVAL` | `components`, keyed by `years`, `months`, `weeks`, `days`, `hours`, `mins`, `secs`; retain positional vs named source form where required for regeneration |
| `JUSTIFY_DAYS`, `JUSTIFY_HOURS`, `JUSTIFY_INTERVAL` | `value` |
| `CLOCK_TIMESTAMP`, `STATEMENT_TIMESTAMP`, `TRANSACTION_TIMESTAMP`, `TIMEOFDAY` | no argument fields |

`level` and `weekday` use the part-like metadata contract. Ordinary expression fields retain their normal AST nodes, including predicand-typed substitutions. The AST does not store a second dialect flag: affinity continues to use the existing per-statement diagnostic collector.

#### Agent implementation matrix — level / part terms

Apply this matrix in every dedicated part slot (`DATEADD`, diff/trunc family, `EXTRACT`, `DATE_PART`, `TIME_SLICE`, and Postgres date/time productions where applicable). Quoted part literals and unquoted level tokens must converge on the same normalized classifier.

| Accepted canonical part / level | Target affinity | Flag behavior |
|---------------------------------|-----------------|---------------|
| `YEAR`, `QUARTER`, `MONTH`, `WEEK`, `DAY`, `HOUR`, `MINUTE`, `SECOND` | Shared | No dialect flag |
| `DAYOFMONTH`, `DAYOFWEEK`, `DAYOFWEEKISO`, `DAYOFYEAR`, `WEEKISO`, `WEEKOFYEAR`, `EPOCH_SECOND`, `EPOCH_MILLISECOND`, `EPOCH_MICROSECOND` | Snowflake | Snowflake flag |
| `CENTURY`, `DECADE`, `DOW`, `DOY`, `EPOCH`, `ISODOW`, `ISOYEAR`, `MICROSECONDS`, `MILLENNIUM`, `MILLISECONDS` | PostgreSQL | Postgres flag |
| `TIMEZONE`, `TIMEZONE_HOUR`, `TIMEZONE_MINUTE` | PostgreSQL | Postgres flag; add to `ExtractDatetimeFieldAffinity` unless Snowflake documentation proves a shared form |
| Documented aliases/abbreviations (`yy`, `yyyy`, `qtr`, `mon`, `mm`, `wk`, `woy`, `dd`, `dw`, `hh`, `mi`, `sec`, `ms`, `us`, `ns`, etc.) | Inherit the owning canonical part | Normalize first, then flag; verify each alias against current Snowflake/Postgres documentation—do not guess from spelling |
| Character literal or identifier not in a target registry | Unknown / extensible | Accept where the function permits extensible text, but do not set a dialect flag and do not collect it as a column |

Keep a single machine-readable part registry (canonical name, aliases, allowed function families, affinity). Do not duplicate sets independently in grammar predicates, `ExtractDatetimeFieldAffinity`, and walker code.

#### Agent implementation matrix — non-date constructs that establish dialect affinity

Entry 1 must preserve and test these existing markers when composing or nesting date/time expressions. This is also the model for future dialect branches. The rows describe the current registry; audit constructs that overlap across engines before expanding the catalog.

| Target | Already recognized constructs / keywords | Implementation source |
|--------|------------------------------------------|-----------------------|
| Snowflake | `QUALIFY`; `PIVOT` / `UNPIVOT`; Snowflake multi-aggregate PIVOT; nested `WITH` inside a CTE body; Snowflake `TRUNCATE TABLE`; Snowflake DELETE form; quoted numeric identifiers; dollar function identifiers | `SqlGrammarDialectRuleRegistry` plus `nestedWithInCteBodyRegistration` |
| PostgreSQL | `ON CONFLICT`; `DELETE … RETURNING` / `delete_returning`; Postgres DELETE form; Postgres TRUNCATE form (optional `TABLE`, multiple targets) | `SqlGrammarDialectRuleRegistry` |
| Snowflake bare value | `CURRENT_ORGANIZATION_USER` | `SqlBareValueExpressionRegistry` |
| PostgreSQL bare values | `CURRENT_CATALOG`, `CURRENT_ROLE`, `CURRENT_SCHEMA`, `SESSION_USER`, `USER` | `SqlBareValueExpressionRegistry` |
| Shared / neutral bare values | `CURRENT_DATE`, `CURRENT_TIME`, `CURRENT_TIMESTAMP`, `CURRENT_USER`, `LOCALTIME`, `LOCALTIMESTAMP` | `SqlBareValueExpressionRegistry`; no dialect flag |

**Dialect acceptance tests:** one warning and the correct counter for each target-only row; no warning for every shared row; nested target-specific function/part combinations count every hit but emit one target warning; Snowflake + Postgres in either order emits one mixed-dialect FATAL at the second affinity; SCRIPT statements reset independently; part text is absent from table/query dictionaries; grammar-rule and semantic-registry paths produce the same diagnostic contract.

**Testing:** (1) Basic dateadd/timeadd; (2) rule templates for `DATEADD` / `TIMEADD` / `TIMESTAMPADD`; (3) symbol tables resolve increment and date/time expr, **ignore** parts; predicand substitutions in slots 2–3; nested complexity; fec/`year` regression (formerly “parser-defects Phase 4”).

### Baseline (2026-09-28)

Most names parse as generic `function` via `routine_invocation`; unquoted parts become **`column`** refs. **`TIMESTAMPADD` fails parse** (`TIMESTAMP` lexer split). No dedicated AST or walker tests.

### Absorbed corpus defect (former Entry 17)

Entry 1 owns the complete fix and regression evidence for the date-part-as-column defect found by the 5.0.0-3 vs 5.1.4-1 comparison of the 11-10-2026 extract.

**Problem:** The first argument of `DATEADD` / `DATEDIFF` (and the other date-part functions in this entry) is parsed as an ordinary column reference. When the only visible sources are derived tables or CTEs, the part (`year`, `day`, `month`, …) is looked up in their output interfaces and reported FATAL `UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES`. With a physical table in scope it may appear clean while silently recording the part as a phantom source column, which corrupts lineage.

**Known affected bound query:**

| Case ID | Extract record | Domain / entity / query name | FATAL / expression |
|---------|----------------|------------------------------|--------------------|
| q5866 | `bound_queries_11-10-2026.csv` record 5866 (header = record 1) | PDP_AMS / mkt__segmentation / pdp_ams_mkt__segmentation_intake | `Unqualified column 'year' at (l:29 c:15) was not found in output interface of any visible query alias [cte_mkt_segmentation]` — `dateadd(year,2000,effective_end_date)` |

**q5866 resolved (2026-11-13):** Replayed the original frozen SQL record with
the current JDK 21 isolated-build fat JAR. The historical 5.1.4 run had one
FATAL; the replay had zero syntax errors and zero diagnostics. Both DATEADD
calls retain YEAR metadata and `effective_end_date` as their value; `year` is
absent as a column in global dictionaries and published symbol-table lineage.
This closes the q5866 case only, not the broader DATEDIFF/date-time work or
the required full-corpus re-test.

**Minimal reproductions (2026-11-11):**

- `select dateadd(year, 1, c.d) as y from (select a.d from t a) c` → FATAL on `year`;
- the same occurs with `dateadd(day, …)` and `datediff(month, …)`;
- over a physical source (`from t a`) the calls may be clean but incorrectly record `year` / `day` as columns.

The fix is not merely to suppress q5866's diagnostic. Every recognized date/time part and documented abbreviation must be parsed as a part constant, excluded from table/query dictionaries and unresolved-column handling, and classified for dialect affinity using the matrices above.

### Extract/corpus re-test required before Entry 1 closes

Use a rebuilt 5.1.4 fat jar and re-run both 11-10-2026 extracts through their correct endpoints:

**Additional RMCP evidence (2026-11-19):** use the strict
[query assessment](<./514 defect and enhancement requirements/queries/rmcp-strict-queries-latest.md>)
and [snippet assessment](<./514 defect and enhancement requirements/snippets/rmcp-strict-snippets-latest.md>).
The detailed dated query and snippet comparisons in that directory's
`queries/` and `snippets/` subdirectories are local-only corpus artifacts and
are not included in the repository checkpoint; their corresponding
`*-latest.md` copies were byte-identical when reviewed. These are historical inventories, not a
current-candidate validation or proof that every tagged difference is a defect.
Adjudicate findings against the current contract and existing dispositions;
the [interface-loss audit](<./514 defect and enhancement requirements/interface-loss-audit.md>)
classifies snippet 35573 and bound rows 1535/1539 as false positives or
improvements, not parser defects. Track any remaining unadjudicated material
findings rather than accepting or rejecting all differences by tag.

1. Parse every record in `bound_queries_11-10-2026.csv` through `SQL`.
2. Parse every record in `snippets-11-10-2026.csv` through the endpoint implied by its type (`COLUMN`, `PREDICAND`, `CONDITION`, `TUPLE`, `QUERY`, `JOIN_EXTENSION`, `IN_LIST`, etc.), using the same endpoint-input normalization as the prior 5.0.0-3 vs 5.1.4-1 comparison.
3. Re-test every bound-query or snippet record that previously failed on 5.1.4 but succeeded on 5.0.0-3; identify date/time cases separately from unrelated author errors and Entries 16/18.
4. q5866 must lose its FATAL, and `year` must be absent from its table dictionary, query dictionary, unresolved-reference diagnostics, and lineage.
5. Search both extracts for every function and part/alias listed in the Entry 1 matrices. For each match, verify that the part is not collected as a column and that target-only constructs produce the expected Snowflake/Postgres statement flag.
6. Compare the new run with the saved 5.0.0-3 and pre-fix 5.1.4 results. Investigate every new FATAL and every changed AST/interface outside the intended date/time nodes; do not refresh unrelated goldens or accept corpus drift wholesale.
7. Record the affected case IDs, template/bundle names, before/after FATAL counts, dialect diagnostics, and any remaining failures in this entry. When extracts with stable Panto IDs become available, add those IDs rather than relying only on `qN` / `sN` record numbers.

There was no known snippet-specific date-part regression in the original 15 parser-defect cases, but the snippet extract must still be re-run: generic function parsing can hide phantom part columns without producing a FATAL.

**Interim snippet replay (2026-11-13):** Replayed all 87,897 recognized
snippet records from `corpus-final.jsonl` with the current isolated-build
5.1.4-1 fat JAR and their frozen endpoint normalization. Eight source records
have unrecognized extract types and were excluded as in the original comparison.
Against the saved `final514` run, all cases have the same clean/FATAL status:
87,378 clean, 519 with FATALs, no batch crashes or timeouts; no cases newly
passed or failed. Against `base5003` with the `inlist5003` overlay, 26 cases
are FATAL only on current 5.1.4, nine are FATAL only on 5.0.0-3, and 493
are FATAL on both. The 26 are the same historical cases: 25 previously
classified for author remediation and the owner-waived unused s82233.
The two parsers' totals are 87,378 vs 87,395 clean respectively.

The current replay changed canonical AST hashes for 121 cases clean in both
5.1.4 runs; all 121 contain textual add-family function matches. Two already
failing cases have changed walker-exception messages but unchanged FATAL
counts. Results and case IDs are in the local-only archive under
`comparison/runs/snippet514-20261113-0909/`.

**Fresh bound-query replay (2026-11-13):** Replayed all 6,551 original
bound-query SQL inputs on the current isolated-build 5.1.4-1 fat JAR; each
input was verified against the frozen historical corpus. There were no batch
crashes or timeouts. Against the saved 5.0.0-3 results:

| 5.0.0-3 | Current 5.1.4 | Records |
|---|---|---:|
| Zero FATALs | Zero FATALs | 6,304 |
| Zero FATALs | FATAL | 147 |
| FATAL | FATAL | 73 |
| FATAL | Zero FATALs | 27 |

The 147 5.1.4-only FATALs are all in the historical classification:
110 duplicate outputs in a set-operation branch with a width diagnostic;
five duplicate outputs in one SELECT; one set-operation width mismatch;
16 missing or out-of-scope aliases; five concrete columns absent from a
derived interface; one syntax case (q5128); seven variable-outside cases
covered by Entry 20; and two mirror-image variable-inside cases (q5314 and
q5335) approved by the owner for release with author guidance to add fixed
output aliases or use concrete columns. The original two queries remain
FATAL until revised; no parser change is implied. The owner also accepts the
other 138 author-classified bound-query differences for release, with
corrective guidance in
[Panto template and snippet remediation](./panto-template-remediation-5.1.4.md).
The same acceptance applies to the remaining author-classified snippet issues
in Patterns 1–5 and 7. The owner also accepts mirror-image snippets s8491 and
s8492 for release with fixed-alias or concrete-column guidance. These
dispositions do not mean the SQL has already been corrected or that unchanged
records will pass. Entry 20 is now closed (2026-11-13) with the
qualified-column-variable FATAL replaced by the approved warning behavior.

Compared with the prior 5.1.4 run, 14 previously failing bound queries now
have zero FATALs, including q5866; no clean query became FATAL. One query
that still fails, q3718, retains its syntax FATAL while its walker-exception
message changes. Canonical AST hashes changed for 36 queries with zero FATALs
in both 5.1.4 runs; their separate audit is recorded below. The 73 failures
shared with 5.0.0 and the 27
5.0.0-only failures are status comparisons, not individual semantic
approvals. Local-only results and per-case IDs are under
`comparison/runs/bound514-20261113-fresh/`.

**Bound-query DATEADD AST audit (2026-11-13):** All 36 changed ASTs were
captured from the historical and current 5.1.4 JARs, with hashes matching the
saved corpus results. All 100 DATEADD calls migrate from generic `function`
nodes to the expected `datetime_function` representation. Applying only that
transformation to each historical AST reproduces the entire current AST.
Function names and authored part text match the SQL, and all 200 increment
and value expressions match independent parenthesized PREDICAND parses.
All 36 original queries and all 200 probes have zero syntax errors, reach EOF,
and report zero FATALs on the current JAR.

Zero FATALs does not mean zero diagnostics: 12 queries still emit 18
`AMBIGUOUS_COLUMN_REFERENCE` SEVERE_WARNINGs and 15
`UNRESOLVED_UNQUALIFIED_COLUMNS` ERROR diagnostics. These concern
`acs_contact_id`, `ST_DT_APP_STU_GEN_ACSL`, and `ST_DT_APP_PAR_GEN_ACSL`,
not DATEADD part keywords; the same unresolved references existed on the
historical 5.1.4 JAR. Three queries lose historical phantom DATEADD-part
diagnostics (`year` in q3673, `day` in q4155 and q5499). The audit validates
the AST migration, not the unresolved references, dictionaries, lineage,
generation, or release approval. Local-only per-case evidence and captured
diagnostics are under `comparison/runs/datetime-bound-ast-audit-20261113/`.

**5.0.0 diagnostic comparison:** All 12 of these queries also have zero
FATALs and zero syntax errors on 5.0.0-3. Its exposed parser message lists
are empty for each; unlike 5.1.4, that version does not expose the same
structured diagnostic API. Thus FATAL status matches, but the available
outputs do not establish matching diagnostic behavior. The owner approves
all 12 queries (q4139–q4143, q4149–q4151, q4155, q4157, q4159, and q4160)
for the 5.0.0-to-5.1.4 switch as-is. Their recorded direct-API diagnostics include both
`AMBIGUOUS_COLUMN_REFERENCE` SEVERE_WARNING and
`UNRESOLVED_UNQUALIFIED_COLUMNS` ERROR for `acs_contact_id`; q4157, q4159,
and q4160 also
report these diagnostics for `ST_DT_APP_STU_GEN_ACSL` and
`ST_DT_APP_PAR_GEN_ACSL`. Approval does not change their direct-API severities.
All 12 have now received the owner's release disposition.

**RMCP presentation check (q4151):** Direct `SqlParserAccess` reports the
unresolved-column diagnostic as ERROR on both the RMCP-bundled 5.1.4 JAR and
the current 5.1.4 JAR. Through RMCP's MCP service, both JARs instead return
that diagnostic as WARNING along with the ambiguity SEVERE_WARNING; both
report zero FATALs. The MCP adapter normalizes AST-walker ERRORs to WARNING,
so the user's warning-only RMCP result and the direct-parser audit are
consistent with their different delivery paths. This check is for q4151;
do not infer that every diagnostic from every query has been compared through
both paths.

**Bare TUPLE review (2026-11-13):** The owner accepts the 484 cases that
FATAL on both parsers as bare SELECT inputs and accepts the 471 that were
FATAL-free on both historical parsers when wrapped. This is acceptance of those
comparison outcomes, not a claim that the 471 were freshly re-run or checked
for every warning, AST, or consumer behavior.

Of the other 13, the current JAR reproduced the historical 5.1.4 FATAL counts:

- Eleven (s37279, s87311, s87868–s87875, s87905) include a trailing semicolon.
  The historical wrapper placed it inside the closing parenthesis. The legacy
  parser reports two FATALs; current 5.1.4 reports no FATALs but emits two
  `RECOVER_INLINE` warnings and leaves the semicolon as its current token.
  **This is a comparison-input artifact, not an established difference for the
  original snippets.** The owner reports successful unwrapped parses in RMCP
  with 5.1.4 while selecting the TUPLE endpoint, and confirms that applications
  will not wrap these statements without removing the semicolon. **Owner
  disposition: all eleven are accepted as successfully matched for the snippet
  comparison**; the artificial wrapper differences require no remediation.
  Replaying all eleven original inputs at the SQL endpoint produced zero
  FATALs on each of 5.0.0-3, RMCP's bundled 5.1.4, and current 5.1.4.
  At the TUPLE endpoint, the same unwrapped inputs produced FATALs on all
  three versions in the standalone replay. This differs from the owner's RMCP
  observation and is retained as a test-path distinction, not a rejected
  snippet-comparison case. In the inspected RMCP extension, highlighted-editor
  validation defaults to SQL even if a different endpoint is selected; the
  owner's exact validation path has not been independently established.
  Removing that terminal semicolon *for a separate comparison input* before
  wrapping produced zero FATALs and matching AST hashes on both parsers in all
  eleven cases; current 5.1.4 reached EOF with no diagnostics.
- s84947 projects `extended_prop_min_value` twice: current 5.1.4 emits a
  duplicate-interface FATAL, whereas 5.0.0-3 did not. **Accepted by the
  owner:** the 5.1.4 FATAL prevents a duplicate-column runtime failure.
- s85047 references `sub.<Source Contact ID>` in a join while `sub` projects
  `sourcecontact_id AS source_contact_id`: current 5.1.4 emits a
  qualified-column-not-found FATAL and a separate ambiguous-column
  SEVERE_WARNING; 5.0.0-3 reported no FATAL. The owner confirmed the qualified
  column-variable FATAL as a parser defect; fix and parity gate: **Entry 20**.

**Snippet-comparison disposition:** The owner considers the reviewed snippet
comparison satisfied; the qualified column-variable defect tracked in
**Entry 20** is now resolved and no longer blocks parity. This disposition
includes the eight records with unrecognized types (s37280–s37286 and s77528):
the owner considers them invalid extract records and excludes them as parity
blockers for either version. Neither parser was tested on these records, so
this is an accepted data disposition, not a measured parser outcome. The
disposition does not claim that the broader Entry 1 corpus, dialect, lineage,
and consumer checks are complete.
The original inputs remain unchanged; local-only replay evidence is under
`comparison/runs/tuplewrap-differences-20261113/`,
`comparison/runs/tuple-semicolon-514-20261113/`, and
`comparison/runs/tuple-semicolon-5003-20261113/`.

**DATEADD AST audit (2026-11-13):** All 121 changed snippets are individually
approved as DATEADD AST migration fixtures for dependent-application testing
(117 PREDICAND, three CONDITION, one TUPLE; 128 DATEADD calls). Captured AST
hashes matched both saved runs; each whole-tree difference matched the expected
generic-function-to-`datetime_function` transformation. Authored function
names and levels matched their AST metadata; 256 independently parsed,
parenthesized increment/value operands matched the embedded ASTs. All 121
original snippets had zero syntax errors and FATALs. The per-case disposition,
original endpoint inputs, and historical/current AST fixtures are in the
local-only archive under `comparison/runs/datetime-ast-audit-20261113/`.
Approval is limited to this AST migration; it does not certify dictionary,
lineage, dialect, generator behavior, other pre-existing AST semantics, or the
full corpus gate above.

**Separate pre-existing PREDICAND finding:** Standalone `-1 * 30` silently
produces the AST for `-1` with zero syntax errors and FATALs on both the
historical postfix and current JAR; `(-1 * 30)` preserves the multiplication.
The audited embedded DATEADD expressions retain their full operands, and no
extract PREDICAND is exactly the standalone example. The owner does not regard
this synthetic example as a blocker for certifying the snippet extract. The
general standalone endpoint behavior remains outside the AST audit.

### Local corpus evidence and replay tools (archived 2026-11-11)

The former repository `.tmp/` assessment artifacts and the two untracked live CSV
extracts were moved to a **local-only sibling archive**, outside this Git repository:
[`../../../pss-parser-evidence/2026-11-11/`](../../../pss-parser-evidence/2026-11-11/).
On the current workstation this is
`/Users/ghowe/emculturate-pss/pss-parser-evidence/2026-11-11`.
The archive contains authored SQL and diagnostic payloads; do not commit, publish,
or upload it. It is not a shared or remote backup.

| Archive-relative location | Purpose |
|---------------------------|---------|
| `live_queries/` | Original bound-query and snippet CSV extracts; preserve record order for stable `qN` / `sN` IDs |
| `comparison/corpus*.jsonl` | Frozen endpoint-normalized inputs, including final, IN_LIST, tuple-wrapper, and join-extension variants |
| `comparison/runs/*/results.jsonl` | Historical 5.0.0-3 and pre/post-fix 5.1.4 results; keep all tags, especially `base5003`, `inlist5003`, `pre514`, `final514`, and `entry16-*` |
| `comparison/runs/jext514/` | Unmerged historical run; shard outputs retained because no merged result exists |
| `comparison/` | Batch/probe Java sources, comparison/classification scripts, remediation indexes, affected IDs, summaries, and detailed probe results |
| `alias-scope/`, `capture/` | Entry 13–16 scope/lineage probes, including the 113-query Entry 14 evidence and captured golden failures |
| `legacy-5003/` | Frozen legacy parser source/build snapshot, including the 5.0.0-3 reference JAR; not an active module |
| `jars/` | Historical prefix/postfix 5.1.4 JARs, not current release artifacts |
| `logs/` | Historical compile/test evidence, including `full-parse-regression-entry16-final.log`: 2,194 tests, 0 failures, 0 errors, 21 skipped; this is a recorded result, not a new verification |
| `manifests/` | Source-to-archive paths, sizes, SHA-256 hashes, discarded-file reasons, initial Git status, and hashes of unrelated modified files |
| `original-tools/` | Byte-preserved originals of the four replay scripts whose paths were adapted after relocation |

Cleanup removed only untracked rebuildable classes/Python caches, empty outputs,
the machine-specific classpath, Finder metadata, shard inputs verified against
retained corpus records, and shard outputs whose concatenation was byte-identical
to a retained merged result. Other evidence, including historical failing logs,
was retained. Previously tracked temporary artifacts now appear as Git deletions;
the existing build/IDE edits were left unchanged.

**Replay from the repository root** (requires the rebuilt fat JAR and a suitable JDK):

```bash
REPO="$PWD"
EVIDENCE="$(cd .. && pwd)/pss-parser-evidence/2026-11-11"
python3 "$EVIDENCE/comparison/run_corpus.py" run datetime514 \
  "$REPO/parse/target/pss-parse-5.1.4-1-fat.jar" \
  --corpus "$EVIDENCE/comparison/corpus-final.jsonl" --shards 1
```

Use a **new run tag** each time; do not overwrite historical baseline tags.
`--only "$EVIDENCE/comparison/entry16-affected-ids.txt"` selects the saved Entry 16
cases; another ID file can select q5866 or the date/time matrix.
`run_corpus.py build` reconstructs `corpus.jsonl` from the archived CSVs, but the
frozen `corpus-final.jsonl` is the comparison reference: keep its historical
endpoint normalization rather than assuming a fresh build is identical.

`compare_live.py` compares its fixed `base5003` / `pre514` / `post514` tags.
`new_fatals.py` compares `final514` against `base5003` overlaid with `inlist5003`;
`remediation_index.py` regenerates the local remediation index from those saved
results and classifications. The older `compare_panto_corpus.py` and
`compare_panto_29.py` now require `PSS_REPO_ROOT="$REPO"` to locate repository
fixtures/current JARs and use the archived legacy JAR; the latter still targets
5.1.4 and is historical, not the Entry 1 runner.

The batch summaries retain AST hashes and selected diagnostics, **not** complete
dictionaries, lineage, or dialect hit counts. Entry 1 closure still requires the
detailed AST/symbol-table/dialect assertions specified above.

### Work phases

| Phase | Work |
|-------|------|
| **1 Lexer baseline** | Inventory and add every lexer entry required by delivery Slices B–E in one late-keyword batch immediately before `Identifier`; regenerate; publish the complete old→new token-ID substitutions in safe replacement order; the user updates affected goldens manually; require a green full suite before grammar work |
| **1 follow-up: datetime regression suite** | Implement the invalid DATEADD level FATAL prerequisite, then develop the numbered DATEADD suite and reduced cross-function suite below, incrementally with the relevant delivery slices; the lexer baseline remains complete |
| **1 follow-up: extraction unification** | Refactor EXTRACT and DATE_PART into datetime-family subtrees, with metadata-only parts, normal source expressions, explicit syntax-form retention, and the AST details described below |
| **1 follow-up: per-function build and test** | Execute actions 66-97 below: deep context suites for distinct signature primaries, compact construction/resolution/validation suites for every sibling invocation |
| **2 Grammar + AST** | Implement the approved dedicated productions, `datetime_part`, `datetime_function` contract, and invocation signature registry incrementally; branch only when syntax, arity, AST, or keyword role differs |
| **3 Walker + affinity** | Add every exit method; exclude parts from symbol collection; stamp predicand substitutions in expression fields; route rule/function/part affinity through the existing statement collector |
| **4 Focused tests** | Extend `SqlEventWalkerDatetimeFunctionPlacementTests` plus PostgreSQL/shared matrices, AST signatures, rendering forms, counters/warnings/mixed fatal, SCRIPT reset, and fec/`year` CASE |
| **5 Corpus + delivery** | Rebuild jar; re-run bound-query and snippet extracts; prove q5866 and all date/time matches; compare FATALs, AST/interfaces, dictionaries, lineage, and dialect diagnostics; finish generator/docs/release notes |

#### Phase 1 follow-up substep: invalid DATEADD level diagnostic

**Implemented 2026-11-12; DATEADD family milestone complete through scenario 65.**
This is a prerequisite to the expanded DATEADD test suite, not a reopening of
the completed lexer baseline. No new lexer tokens or token-ID changes are required.
DATEADD now reports `INVALID_DATETIME_PART` FATAL at the authored level token
for an unknown part. Focused tests cover registered spellings, invalid bare and
quoted levels, empty text, and an invalid nested level; the remaining canonical,
alias, and placement matrices below are still pending. TIMEADD and TIMESTAMPADD
subsequently gained the same registered-part validation; other families remain
unvalidated by this add-family change.

- Add a dedicated **FATAL** diagnostic for an invalid DATEADD level code
  (proposed code: `INVALID_DATETIME_PART`), using the existing diagnostic
  reporting path. Identify the invocation, authored level text, and source
  location.
- Validate the decoded level against the accepted DATEADD canonical names and
  aliases. An unknown spelling must not become accepted merely because
  `canonicalize` uppercases it. Reject parts unsupported by DATEADD even if
  another date/time function accepts them; do not broaden EXTRACT or other
  function validation as part of this substep.
- Keep invalid level text out of column collection and resolution. Report the
  level diagnostic rather than a phantom-column diagnostic.
- Cover invalid identifier and string forms, empty quoted text, misspellings,
  and invalid levels in nested calls. Keep these separate from malformed SQL
  syntax tests. Replace the new DATEADD AST test's permissive unknown-part
  expectation with the explicit FATAL expectation.
- Positive level coverage must exercise all registered canonical names and
  their accepted aliases, preserving authored text/form while asserting the
  expected canonical value. Use explicit expected mappings, not registry output
  as its own test oracle; a multi-expression SELECT can cover all canonical
  names, with separate alias/form cases.
- Extend the planned dedicated placement suite with unprefixed `predicand`
  substitutions and table-prefixed `column` substitutions in INCREMENT, VALUE,
  and formulas within either slot. Include both same-kind and mixed-kind slot
  combinations, normal dictionaries/lineage, and scope qualification checks.
- Include DATEADD as an argument to other functions and DATEADD nested in the
  VALUE slot with a different date part and increment. Assert each call's
  independent metadata and all expression-column dependencies.

#### Phase 1 follow-up action: unify DATE_PART and EXTRACT grammar and AST

**Implemented; closure coverage and consumer/generator updates remain.** Built-in
`DATE_PART(part, expression)`, `DATE_PART(part FROM expression)`, and
`EXTRACT(part FROM expression)` as specialized alternatives beneath
`datetime_function`. Include the parser's already accepted EXTRACT comma form
in the extraction refactor, without claiming it is standard engine syntax.
This supersedes this entry's earlier requirements to preserve the EXTRACT and
DATE_PART AST shapes. Both invocations now use datetime-family subtrees.

- History: commit `eb0566e` (2026-08-06, "Add DATE_PART FROM parsing and
  case-insensitive EXTRACT/DATE_PART lexers.") introduced the dedicated grammar,
  walker support, and tests. The completed-baselines table also dates this
  functionality to August 2026. This establishes introduction after January
  2026, not an independently verified artifact publication date; no release
  tags are available in the current repository. Earlier generic same-name
  calls are distinct from this dedicated feature.
- Per the user's January-2026 compatibility cutoff, the newly introduced
  dedicated DATE_PART AST need not be frozen for legacy use cases. The user
  explicitly approved changing DATE_PART and EXTRACT to `datetime_function`
  subtrees:
  `datetime_function={function_name=DATE_PART|EXTRACT, level={canonical,text,form},
  value=..., <syntax discriminator>}`. Choose and document the discriminator
  key/values explicitly; retain comma versus FROM so generators never guess
  the authored syntax from map order or part form.
- Route both built-in forms through dedicated recognition before generic
  invocation. Bare/quoted part spellings are metadata, not column arguments.
  Reuse common recognition/registry mechanisms, but validate/classify against
  extraction-specific supported parts, not DATEADD's part subset.
- Explicitly recognize the comma-delimited variation
  `DATE_PART(year, expression)` as well as `DATE_PART('year', expression)`.
  Validate the part in comma and FROM forms alike, including canonical names,
  accepted aliases, and invalid bare/quoted text. Do not let comma syntax
  bypass part validation or collect its first argument as a column. Define
  extraction-specific invalid-part diagnostics during this development step;
  DATEADD's allowed-part set must not become the extraction validity test.
- Apply the same metadata recognition, extraction-specific part validation,
  and normal source-expression traversal to EXTRACT. Preserve accepted interval
  and typed datetime literal sources, timezone/extended parts, and source type
  information in the new VALUE subtree. Do not restrict EXTRACT to DATEADD
  units or lose existing extraction semantics during the AST migration.
- Source expressions must retain ordinary columns, formulas, nested functions,
  unprefixed predicand substitutions, and table-qualified column substitutions,
  with normal resolution, dictionaries, and lineage. Keep schema-qualified
  `custom.date_part(...)` and `custom.extract(...)` generic; do not apply
  built-in part validation to UDFs.
- Update directly dependent walker exits and AST consumers/generation paths.
  Inventory and update all affected DATE_PART and EXTRACT tests, including
  those introduced in August: AST, diagnostics, dictionaries, lineage, typed
  sources, and endpoint coverage. Preserve intended behavior except for the
  approved AST/part-handling changes. Existing token-ID goldens remain
  user-edited only; review behavioral golden changes explicitly.
- Update consumer instructions to describe the new extraction subtrees and
  adoption requirements directly: dispatch on `datetime_function` and
  `function_name=DATE_PART|EXTRACT` case-insensitively, render level metadata with its preserved spelling and
  quoting form, recursively generate the VALUE expression, and use the explicit
  syntax discriminator to emit comma or FROM syntax. Include examples for both
  forms and explain part validation and normal source-expression traversal.
  Do not include a historical explanation that DATE_PART previously followed
  generic function rules; that migration narrative is not needed.
- Extend scenario 58 below with both syntax forms, quoted/bare canonical and
  alias levels, no phantom-part columns, source-expression/substitution
  coverage, nested DATEADD, qualified UDF controls, syntax preservation, and
  extraction-specific dialect/diagnostic cases. Assert each invocation
  independently so DATE_PART coverage cannot mask an EXTRACT regression.

#### Phase 1 follow-up test suite: formulation and assertion instructions

**Consolidated 2026-11-12; partially implemented by family.** The single numbered inventory
below combines the agreed argument, placement, substitution, level, and nesting
coverage. Numbers identify coverage scenarios, not a promised count of JUnit
methods: a scenario may require several concrete cases. The DATEADD inventory
comes first; the reduced cross-function inventory follows with continuous
numbering. Other functions are tested as their delivery slices become available,
not treated as implemented merely because their names have lexer tokens.

**Test organization and execution**

- Use the dedicated JUnit 4 class,
  `parse/src/test/java/sql/walker/SqlEventWalkerDatetimeFunctionPlacementTests.java`,
  extending `AbstractSqlParseEventWalkerTest`, for the DATEADD suite. Keep new
  datetime placement tests out of `SqlEventWalkerCoreSelectFromAliasingTests`.
  Put reduced cross-function tests in a second dedicated class,
  `SqlEventWalkerDatetimeFunctionFamilyTests`, in the same directory. The
  original `SqlEventWalkerSnowflakeDatetimeManipulationTests` was renamed to
  the placement class; its existing tests seed, rather than complete, the suite.
- Keep endpoint permutations together in
  `SqlEventWalkerDatetimeFunctionSnippetTests`; add a separate class only if a
  future endpoint genuinely needs substantially different setup. DATEADD
  currently has one case each for PREDICAND, CONDITION, QUERY, TUPLE, IN_LIST,
  JOIN_EXTENSION, VALUES, INSERT, UPDATE, DELETE, DDL, and SCRIPT. These tests
  use the real endpoint and six complete literal output goldens; the DELETE case
  also asserts its expected Snowflake warning at its source position.
- For **every** planned datetime invocation, add one test for each of those
  12 endpoints as that invocation's implementation becomes available:
  TIMEADD, TIMESTAMPADD, DATEDIFF, TIMEDIFF, TIMESTAMPDIFF, DATE_TRUNC,
  datetime TRUNC, TIME_SLICE, LAST_DAY, NEXT_DAY, PREVIOUS_DAY, DATE_PART,
  EXTRACT, DATE_BIN, AGE, MAKE_DATE, MAKE_TIME, MAKE_TIMESTAMP, MAKE_TIMESTAMPTZ,
  MAKE_INTERVAL, JUSTIFY_DAYS, JUSTIFY_HOURS, JUSTIFY_INTERVAL, CLOCK_TIMESTAMP,
  STATEMENT_TIMESTAMP, TRANSACTION_TIMESTAMP, and TIMEOFDAY. DATEADD's
  permutations are already covered; SQL statement placement is covered
  separately in the placement suite. Each case must assert the function's own
  invocation/semantic AST, not the current generic fallback. Use appropriate
  argument and result types for each endpoint and its surrounding comparison,
  and assert any expected dialect diagnostic rather than suppressing it.
  Complete DATE_PART/EXTRACT form-specific checks under their own actions;
  one endpoint case per function is the minimum, not a substitute for
  required syntax-form tests. Do not mark the remaining permutations complete
  until the relevant function implementation and runnable goldens exist.
- The COLUMN endpoint accepts only a column reference/substitution, LITERAL
  accepts literal values, and TRUNCATE accepts a truncate statement: none
  accepts a datetime function as an expression. Do not manufacture positive
  datetime-function cases for those endpoints. DDL is applicable through a
  query-containing form such as CREATE VIEW AS SELECT; TUPLE is applicable
  through a subquery without a relation alias at the endpoint root.
- DATEADD previously followed generic function parsing in CONDITION, IN_LIST,
  JOIN_EXTENSION, VALUES, UPDATE, and DELETE expression positions. The shared
  nonparenthesized expression grammar now selects `datetime_function` before
  generic invocation, so the part is metadata rather than a phantom column.
  Preserve qualified same-name UDF handling and existing token numbers.
- Follow [SqlEventWalkerCoreSelectFromAliasingTests.java](../src/test/java/sql/walker/SqlEventWalkerCoreSelectFromAliasingTests.java)
  specifically `basicSelectListQuotedNumericPrefixColumnTest` as the structural
  model for every diagnostic-free test case, and
  [AbstractSqlParseEventWalkerTest.java](../src/test/java/sql/walker/AbstractSqlParseEventWalkerTest.java)
  for parsing, walking, diagnostic, and path-assertion helpers. Use production
  SQL parsing/walking, not private symbol-helper method harnesses.
- Build and review the standalone cases first, then scopes/clauses, DML, set
  operations, and cross-function samples. Run only the current new class or
  selected methods on JDK 21 using the established isolated Maven build when
  needed. Do not run broad cohorts or the full workspace suite during each
  construction batch. Report actual executed cases and failures separately
  from remaining planned coverage; do not mark unimplemented families green.
- Existing token-ID goldens are user-edited only. The new suite may introduce
  new literal expectations, but never bulk-refresh existing expectations.
  Update the newly added permissive unknown-DATEADD-part test specifically when
  the FATAL contract is implemented; preserve unrelated EXTRACT behavior.

**Reusable query ingredients**

- Keep the three original one-line queries exactly in scenarios 1-3 so their
  token locations remain reproducible. Use distinct output aliases in larger
  SELECT lists, including canonical/alias coverage lists.
- For the argument matrix, use `L`, `CQ`, `CU`, `FQ`, and `FU` for literal,
  qualified column, unqualified column, qualified multi-column formula, and
  unqualified multi-column formula. INCREMENT examples are `2`, `a.offset_days`,
  `offset_days`, `a.offset_days + a.extra_days * 2`, and
  `offset_days + extra_days * 2`. VALUE examples are `DATE '2026-01-01'`,
  `a.created_at`, `created_at`, `a.created_at + (a.base_days + a.adjust_days)`,
  and `created_at + (base_days + adjust_days)`. These use date-plus-numeric
  arithmetic, not addition of two date columns.
- Run all unqualified positive physical-column cases against one table.
  Physical-table ambiguity is tested separately with two visible sources.
  In cross-table qualified formulas deliberately split references between
  `events a` and `accounts b`; do not accidentally turn a positive case into
  an ambiguous one by dropping a qualifier.
- Derived tables and CTEs must explicitly export every intended input under
  known column names. Give overlapping names to both exports for ambiguity
  tests, and distinct names for uniquely resolvable two-source tests. Do not
  use `SELECT *` to hide interface assumptions.
- Use `<increment>` / `<date value>` for unprefixed predicand substitutions and
  `a.<increment column>` / `a.<date column>` for qualified column substitutions.
  Use distinct variable names for different types; do not introduce a
  same-name/different-type conflict into a positive test.
- Query wrappers must remain valid in their clause: DATEADD is an expression,
  not a condition. For WHERE/ON/HAVING/QUALIFY use an explicit comparison;
  for grouped queries group the necessary inputs or use valid aggregates;
  for scalar queries select one output and formulate a single-row result.
  Keep set-op branch output counts aligned and use explicit parentheses for
  intended nesting. Start DML from an existing accepted repository syntax,
  rather than inventing UPDATE/DELETE alias or FROM/USING forms.

**Required assertion contract**

- For each diagnostic-free concrete SQL case, use `parse(query)`,
  `runParsertest(query, parser)`, and `assertNoWalkerDiagnostics(extractor)`
  as in `basicSelectListQuotedNumericPrefixColumnTest`. Provide independent,
  explicit full golden expectations for all six outputs: AST
  (`getAsTree()`), output interface (`getInterface()`), substitutions
  (`getSubstitutionsMap()`), global table dictionary
  (`getTableColumnDictionaryMap()`), global query dictionary
  (`getQueryColumnDictionaryMap()`), and full symbol table
  (`getSymbolTable()`). Include exact token IDs/positions in the dictionary and
  symbol goldens, and assert empty objects explicitly. This applies to every
  SQL case, including cases grouped in loops; a few subtree or presence checks
  cannot substitute for these six goldens. Path assertions may supplement, not
  replace, full-object goldens, even for large or deeply nested queries.
- For expected diagnostics use the standard position-based methods on
  `AbstractSqlParseEventWalkerTest`, preferably
  `assertFatalDiagnosticAtPositionWithFullMessage` for FATAL cases. Supply the
  exact full message, diagnostic code, token text, line, and character position
  so a casual reader can understand the reported failure; assert severity and
  the exact expected diagnostic set/count as well. Use corresponding existing
  severity/position helpers for non-FATAL diagnostics. Assert relevant outputs
  for diagnostic cases as far as the error-processing contract guarantees them;
  never treat a FATAL's partial output as a successful parse.
- For every concrete positive case assert zero syntax errors and no unexpected
  diagnostics. Once affinity is implemented, a Snowflake DATEADD warning is
  expected, not a reason to require an empty diagnostic list. Assert the exact
  expected warning/error/FATAL set and the relevant dialect counters; keep
  implementation-stage gaps visible rather than weakening final expectations.
- Assert the complete AST for every query.
  Every DATEADD must have `datetime_function`, an authored-case `function_name`,
  `level={canonical,text,form}`, `increment`, and `value`, with normal expression
  nodes, correct precedence, and no raw `Type=`/numbered grammar wrappers.
- Assert output names/order, substitutions map, global table/query
  dictionaries with all occurrence tokens, and the complete relevant published
  `def_*` payloads: `table_alias`, `context_list`, per-scope dictionaries,
  `interface`, and clause/DML buckets as applicable. Assert genuinely empty
  artifacts as empty, not by omitting them.
- INCREMENT and VALUE columns must both enter normal qualification, validation,
  dictionaries, and lineage. Clause-only columns belong in the appropriate
  filters/group/order/window/assignment bucket, not automatically in the output
  interface. A parent sees child exports through the child `interface`, never
  by flattening hidden grandchild columns into the parent.
- Assert that metadata-only level/weekday strings are absent from physical
  columns, query-column references, lineage, and unresolved-reference errors.
  Do not globally ban names such as `year`: a real expression column named
  `year` must still be collected.
- Negative tests must assert exact diagnostic code, severity, full expected
  message, token text, source line/character position, and diagnostic count,
  using existing helpers where available. Assert remaining AST/symbol payloads
  only to the extent guaranteed by the existing error-processing contract;
  do not invent successful resolution after a FATAL.
- Prefer [generate_symbol_path_assertions.py](../../tools/generate_symbol_path_assertions.py)
  over manually copying large path goldens: put the exact SQL in a temporary
  file and run `python3 tools/generate_symbol_path_assertions.py --sql-file /tmp/query.sql`
  from the repository root, selecting `--root` and `--path-regex` for the
  relevant maps/subpaths. Respect the current isolated-build/output setup
  before using the generator, whose default build targets ordinary Maven
  output. Use emitted `expected...` values and `assertSymbolTreePathEquals(...)`
  lines, then check them against the documented scope/collection rules.
  Generated current output is not proof of correct behavior. Fix the new
  implementation if it violates those rules; do not bless a defect as a golden.
  Remove temporary SQL/probe files after use.
- For nested/set-op cases also follow
  [symbol-table-bucket-reference.md](../../docs/vendor/pss-parse/5.1.4/symbol-table-bucket-reference.md)
  and [set-op-convert-egress-scoping.mdc](../../.cursor/rules/set-op-convert-egress-scoping.mdc):
  assert branch-local provenance and sibling isolation through observable
  published scopes, with no post-bundle fallback assumptions.

**Comprehensive DATEADD scenarios**

1. **Original qualified VALUE.** Parse exactly
   `select dateadd(year, 1, a.created_at) as shifted from events a`.
   Golden the entire AST, dictionaries, interface, aliases, and symbol table;
   YEAR is metadata and only `created_at` contributes output lineage.
2. **Original unqualified VALUE.** Parse exactly
   `select dateadd(year, 1, created_at) as shifted from events a`.
   Preserve the unqualified AST reference and assert normal symbol resolution
   to `events`, including the authored `created_at` occurrence location.
3. **Original joined formula INCREMENT / literal VALUE.** Parse exactly
   `select dateadd(day, a.offset_days + b.extra_days * 2, DATE '2026-01-01') as shifted from events a join accounts b on a.account_id = b.id`.
   Assert multiplication precedence, typed date literal, both increment
   dependencies, and join-key filter dependencies separately.
4. **Original ambiguous VALUE.** Parse exactly
   `select dateadd(year, 1, created_at) as shifted from events a join accounts b on a.account_id = b.id`.
   Assert `AMBIGUOUS_COLUMN_REFERENCE` SEVERE_WARNING and
   `UNRESOLVED_UNQUALIFIED_COLUMNS` ERROR at the expected occurrences, no
   level-as-column diagnostics, and normal collection of qualified join keys.
5. **Complete standalone argument-shape matrix.** Cross each INCREMENT shape
   `L/CQ/CU/FQ/FU` with each VALUE shape `L/CQ/CU/FQ/FU`, omitting only `L/L`:
   24 explicit cases. Include all four simple column qualification pairs
   `CQ/CQ`, `CQ/CU`, `CU/CQ`, `CU/CU`, formulas in either slot alone, formulas
   against a column in the other slot, and formulas in both slots. Cases
   already covered by scenarios 1-3 need not be duplicated. Assert every input
   occurrence and the union of both slots' output dependencies.
6. **All-literal control.** Add `dateadd(day, 2, DATE '2026-01-01')` without a
   FROM clause; assert a typed literal AST, no source-column dependencies, and
   the normal constant-output interface. This is the optional control from
   the earlier proposal, now explicitly included in this development inventory.
7. **Joined qualified source split.** Use two joined tables with INCREMENT
   columns from one and VALUE columns from the other, then qualified formulas
   spanning both tables in each slot. Assert precise per-table provenance.
8. **Ambiguous INCREMENT only.** Leave the increment column unqualified with
   two physical sources, while VALUE is qualified or literal; assert only
   the increment ambiguity/unresolved reference and correct VALUE resolution.
9. **Both slots ambiguous.** Use distinct unqualified increment/value names
   with two physical sources; assert both names and all offending locations
   in the repository's normal per-occurrence/aggregate diagnostic structure.
10. **Mixed qualification within formulas.** Put qualified and unqualified
    terms in INCREMENT, VALUE, and both; use separate positive one-source and
    negative two-source cases. Only genuinely ambiguous terms should fail.
11. **Unique versus overlapping derived-source exports.** Join two explicit
    derived tables, then repeat with two CTE sources. Unqualified unique output
    names must resolve; names exported by both sources must be ambiguous.
12. **Hidden or nonexistent source columns.** Reference a non-exported
    underlying column, a missing derived/CTE output, and an unknown table alias
    in separate cases. Assert standard existing diagnostics; DATEADD must not
    bypass interface visibility or qualification checks.
13. **Unprefixed predicand substitutions.** Put a variable in INCREMENT only,
    VALUE only, and both. Assert `type=predicand` in each AST and substitutions
    map; do not treat these as fixed source-column names.
14. **Qualified COLUMN substitutions.** Repeat those three arrangements with
    `a.<increment column>` and `a.<date column>`, including different joined
    source aliases. Assert `type=column`, qualifier preservation, dictionary
    occurrences, and normal column-substitution lineage.
15. **Mixed substitution kinds across slots.** Cover predicand INCREMENT /
    column VALUE and column INCREMENT / predicand VALUE, with independent
    names and no type contamination between slots.
16. **Substitutions inside formulas.** In either slot and both slots combine
    ordinary columns, unprefixed variables, and qualified column variables.
    Include same-kind and mixed-kind formulas; preserve each variable's own
    type and collect only the appropriate column-bearing dependencies.
17. **All canonical levels in one SELECT.** Use 11 uniquely aliased DATEADD
    outputs for YEAR, QUARTER, MONTH, WEEK, DAY, HOUR, MINUTE, SECOND,
    MILLISECOND, MICROSECOND, and NANOSECOND. Use a timestamp VALUE suitable
    for subday units. Explicitly assert all 11 canonical/text/form triples
    and independent output interfaces, not merely the SELECT item count.
18. **Every documented registered alias.** Maintain explicit alias-to-canonical
    expectations verified against the Snowflake reference. Group manageable
    batches of uniquely aliased DATEADD SELECT items; cover every registered
    spelling, not just one per canonical name. Compare registry membership
    against the independently expected set as well as parser AST results so
    an omitted or wrongly mapped alias cannot make the coverage self-fulfilling.
19. **Case and level source forms.** Use representative canonical and alias
    spellings in mixed case and single-quoted form, plus unquoted keyword and
    identifier forms. Assert exact authored text and actual grammar form.
    Do not assume every alias lexes as IDENTIFIER or every canonical as KEYWORD.
20. **Level-like real columns.** Use `a.year` and `a.day` in argument formulas
    alongside YEAR/DAY level metadata. Assert real column collection and no
    phantom occurrence for the first argument.
21. **Invalid level forms.** After the diagnostic prerequisite, test an unknown
    identifier, unknown quoted text, misspelling, empty quoted text, and a
    verified part unsupported by DATEADD but supported by another family.
    Expect the dedicated FATAL at the level token, not an unresolved column.
22. **Invalid nested level location.** Test an invalid inner level with a valid
    outer call, then invalid outer with valid inner. Assert the diagnostic
    identifies the correct invocation/location; keep valid and invalid calls
    together once to detect cross-call state contamination.
23. **Numeric edge expression shapes.** Use zero, negative literal, unary minus
    on a column, and parenthesized multi-column INCREMENT expressions; assert
    sign/parentheses/precedence and unchanged normal dependency collection.
24. **Repeated references and independent sibling calls.** Repeat the same
    column within/between slots and use several DATEADD outputs from different
    sources. Assert all dictionary occurrence tokens, existing lineage
    deduplication semantics, and no dependencies leaking between outputs.
25. **DATEADD inside ordinary functions.** Put DATEADD in different legal
    parameter positions of COALESCE and another existing supported function,
    plus multiple sibling DATEADD arguments with distinct parts/sources.
    Preserve the enclosing ordinary-function AST and each datetime subtree.
26. **Ordinary functions inside DATEADD.** Use numeric functions in INCREMENT
    and date-valued functions in VALUE, separately and together. Include
    columns and substitutions inside those inner functions.
27. **DATEADD VALUE contains DATEADD.** Use
    `dateadd(month, 2, dateadd(day, 3, a.created_at))`, then a version whose
    inner/outer increments use distinct column formulas and variables. Assert
    different inner/outer part and increment metadata and complete dependencies.
28. **CASE embedding.** Put DATEADD in THEN/ELSE expressions, then put CASE
    expressions in its argument slots. Assert condition/filter and resulting
    expression dependencies according to existing CASE collection behavior.
29. **Derived-table producer.** Put each of scenarios 1-3 inside a FROM
    subquery and consume its `shifted` output in an outer SELECT. Assert the
    parent reference is to the child output, with traceable inner dependencies.
30. **Derived-table consumer and multiple layers.** Export raw argument columns
    from a derived table, call DATEADD outside it, then repeat through two
    nested layers with renamed exports. Assert every intermediate interface
    and reject direct access to hidden leaf names.
31. **Join placements.** Put the anchor calls in the SELECT of a joined query,
    inside left/right joined derived tables, and in an ON comparison. Use
    INNER and representative LEFT JOIN coverage; verify output versus join
    filter buckets rather than varying every join spelling.
32. **Scalar-query placements.** Put an anchor DATEADD inside a single-output,
    single-row scalar subquery in SELECT; separately let a scalar query supply
    INCREMENT or VALUE to an outer DATEADD. Use aggregates or constrained
    sources to make the scalar formulation sensible, and assert dependent
    query linkage and scope-local inputs.
32a. **Correlated scalar arguments.** Supply INCREMENT and VALUE independently
    with single-output, single-row scalar subqueries, including both uncorrelated
    controls and subqueries that reference columns from the enclosing query.
    Also put separate correlated scalar subqueries in both slots of one DATEADD.
    Assert each argument's scalar AST and dependent-query linkage, the inner
    local and correlated references in their proper scopes and roles, and the
    outer output lineage without flattening or leaking sibling-query inputs.
    Use aggregates or otherwise constrain each subquery to a single row.
33. **CTE producer, consumer, and chain.** Put each anchor in a CTE body and
    consume its output; call DATEADD over raw CTE exports; then carry its result
    through chained CTEs with renamed columns. Assert ordered context visibility,
    published interfaces, and physical versus query dictionary separation.
34. **Correlated subquery and shadowing.** Mix a local INCREMENT with outer
    VALUE and reverse them, then use an inner alias that shadows an outer
    alias. Assert the repository's normal nearest-visible-scope resolution,
    not flattened or sibling visibility.
35. **WHERE predicates.** Use anchor calls in explicit comparisons and BETWEEN;
    also use DATEADD within an IN or EXISTS predicate subquery. Assert filter
    dependencies and `dependent_queries`, including inputs not projected.
36. **GROUP BY and HAVING.** Group by a DATEADD expression with matching SELECT
    output; separately compare an aggregate-based DATEADD in HAVING. Assert
    `grouped_by`, filters, and output dependencies independently.
37. **ORDER BY expressions and output aliases.** Order directly by DATEADD,
    then by its SELECT alias. Assert direct source references versus the
    existing output-alias resolution contract in `ordered_by`.
38. **Window PARTITION BY / ORDER BY and QUALIFY.** Place DATEADD in window
    partition/order expressions and in a valid QUALIFY comparison alongside
    a window expression. Assert `window_partition_by`, `window_ordered_by`,
    filters, and expected Snowflake affinity without spurious output columns.
39. **INSERT source.** Place each anchor in INSERT ... SELECT with explicit
    target columns; include representative CTE/derived sources. Assert target
    mapping, source scope, and lineage from both expression slots.
40. **UPDATE assignment and predicate.** Place DATEADD in SET, then in WHERE,
    then both. Include qualified column and substitution inputs using an
    existing accepted UPDATE form. Assert target/assignment versus filter
    dependencies and the complete statement's expected dialect diagnostics.
41. **DELETE predicate and subquery.** Use DATEADD in a direct WHERE comparison
    and in an EXISTS/scalar predicate subquery, based on an existing accepted
    DELETE form. Assert source/filter/correlation scopes; DELETE does not gain
    an invented SELECT output interface.
42. **UNION and UNION ALL branches.** Put the anchor in first branch, later
    branch, and both branches, with compatible outputs. Include a three-branch
    case where branches 1/3 use a CTE and branch 2 uses a different physical
    table. Assert branch-local dictionaries and no sibling provenance leakage.
43. **EXCEPT branches.** Put DATEADD on left, right, and both sides; keep
    outputs aligned. Assert operand order, composite interface, and branch
    isolation rather than treating EXCEPT as interchangeable with UNION.
44. **INTERSECT branches.** Repeat first/later/both branch placements and assert
    its own composite scope/interface and source provenance.
45. **Nested set operations.** Cover same-operator nesting and mixed nesting:
    `(A UNION B) EXCEPT C`, `A UNION (B INTERSECT C)`, and
    `(A EXCEPT B) INTERSECT (C UNION ALL D)`, formulated with the grammar's
    accepted parenthesized query syntax. Distribute different anchor calls
    across leaves and inspect every intermediate set-op interface.
46. **Set-op consumers and DML composition.** Consume a set-op result in a
    derived table, CTE, and INSERT source; call DATEADD on exported result
    columns as well as inside leaf branches. Assert all intervening exports,
    target mappings where applicable, and absence of hidden/sibling inputs.
47. **Qualified same-name UDF control.** Keep
    `custom.dateadd(a.part, 1, a.created_at)` generic and neutral unless another
    construct establishes affinity. Its `a.part` is an ordinary column, not
    level metadata; do not apply built-in level validation to it.
48. **Statement affinity and reset.** Once affinity is implemented, verify
    repeated/nested DATEADD counts with one Snowflake warning, composition
    with a PostgreSQL-only construct in either order yielding the existing
    mixed-dialect FATAL, and SCRIPT statements resetting independently.
    Assert exact counts from the defined collector behavior, not a guessed
    one-hit-per-query rule.

**Reduced replication for other datetime functions**

Do not repeat the 24-shape DATEADD matrix or every query wrapper for each
sibling function. The per-function actions 66-97 below supersede the earlier
reduced-only scope of scenarios 49-64: distinct-signature primaries now receive
deep context suites; sibling names receive compact suites without repeated
placement matrices. Retain the signature-specific cases below, moving wrapper
coverage to the appropriate primary instead of adding it to every sibling.
Preserve actual signatures:
diff has two datetime expressions, not an increment; day-boundary metadata is
not a column; PostgreSQL constructors have expression fields, not part slots.
`TIMEADD` is the add alias; `TIMEDIFF` is the difference alias (not `TIMEDD`).
For each listed invocation at least one test must check its own name/signature,
named AST fields, relevant dictionaries/interfaces, and affinity. Verify exact
supported forms/parts against the linked engine reference before implementation.
Reduced alias sampling does not authorize an incomplete production registry.

49. **TIMEADD representative add subset.** Cover qualified column INCREMENT /
    VALUE, unqualified one-source inputs, formula INCREMENT, and both
    substitution kinds across its two expression slots. Use HOUR/MINUTE/SECOND
    and TIME-valued examples, not DAY on a TIME literal. Add one joined ON
    comparison and one nested ordinary-function case. Include one valid alias
    spelling and invalid-level FATAL coverage once family validation exists.
50. **TIMESTAMPADD representative add subset.** Cover formulas in both slots,
    mixed predicand/column substitutions, DATEADD nested in VALUE with a
    different part, a CTE consumer, and an INSERT source containing UNION ALL.
    Assert it lexes/parses as TIMESTAMPADD, preserves that invocation, and
    uses the add-family `level/increment/value` signature. Include a canonical,
    an alias, and invalid-level coverage.
51. **DATEDIFF representative diff subset.** Cover literal/column and
    column/column `left/right`, a multi-column date-valued expression, and
    predicand versus column substitutions in either expression slot. Add one
    ambiguous unqualified operand and one joined query. Assert authored
    left/right order with distinct operands, part metadata exclusion, and
    supported/unsupported part handling; never reuse increment assertions.
52. **TIMEDIFF and TIMESTAMPDIFF alias samples.** Give each a distinct-operand
    baseline and substitution case. Use TIMEDIFF in HAVING with appropriate
    time expressions, and TIMESTAMPDIFF in a CTE over an INTERSECT source.
    Preserve invocation spelling and left/right order; include one part alias
    and one invalid part per invocation, with suitable time/timestamp values.
53. **DATE_TRUNC and datetime TRUNC argument-order samples.** For each, use a
    qualified VALUE, a value formula, and both variable kinds in the expression
    slot. Contrast `DATE_TRUNC('month', value)` with `TRUNC(value, 'month')`.
    Put one in GROUP BY and the other in ORDER BY over a derived table.
    Add family-specific invalid part coverage; verify shared versus
    target-specific affinity from the existing matrix, not function name alone.
54. **TRUNC overload and DDL controls.** Keep numeric `TRUNC(number[, scale])`
    neutral and preserve its established generic AST. One-argument ambiguous
    `TRUNC(value)` stays generic without distinguishing semantic evidence.
    Keep DDL TRUNCATE separate. These are compatibility tests, not permission
    to infer datetime types from a column name.
55. **TIME_SLICE signature samples.** Cover required arguments and omitted/
    explicit supported boundary choices, formula/column slice length, a VALUE
    column substitution, and a predicand variable. Use one window-order
    placement and one CTE projection; assert `value/slice_length/level/boundary`
    in SQL signature order, metadata exclusion, and documented invalid
    level/boundary handling rather than a guessed DATEADD part set.
56. **LAST_DAY optional-level samples.** Test omitted level and explicit
    supported level, a qualified VALUE, and both variable kinds. Place one
    case in UPDATE SET and another in a scalar subquery. Assert optional-field
    representation according to the agreed AST and reject unsupported levels
    through the family-specific validation contract.
57. **NEXT_DAY and PREVIOUS_DAY weekday samples.** Give each literal/column
    VALUE and documented weekday spelling variants, then one predicand and
    one column-substitution VALUE. Put one in DELETE WHERE and one in CASE.
    Assert `weekday` metadata instead of a column, correct invocation, and
    invalid-weekday diagnostics under their own validation contract.
58. **EXTRACT and DATE_PART unification samples.** For both invocations,
    implement the approved Phase 1 action above: accepted comma/FROM forms use the
    agreed datetime-family AST with explicit syntax retention. Sample shared
    and dialect-specific parts, quoted/bare canonical and alias spellings,
    normal source columns/formulas, both substitution kinds, one nested DATEADD
    VALUE, and qualified generic UDF controls. Assert no phantom-part columns,
    correct extraction-specific validation/affinity, and intentional reviewed
    EXTRACT/DATE_PART behavioral golden changes, not DATEADD's allowed-part set.
    Include typed datetime/interval sources, extended/timezone parts, and
    existing extraction endpoint coverage to verify no source semantics are lost.
59. **PostgreSQL DATE_BIN samples.** Use interval stride with distinct source
    and origin expressions, qualified/unqualified columns, and substitution
    examples in expression slots permitted by its grammar. Put one in a CTE
    and one in a set-op branch. Assert `stride/source/origin`, interval literal
    representation, source order, lineage, and PostgreSQL affinity.
60. **PostgreSQL AGE overload samples.** Cover one-argument `value` and
    two-argument `left/right`, distinct qualified operands, unprefixed and
    qualified substitutions, and an ordinary-function wrapper. Use a
    correlated scalar-query case to exercise outer/local resolution without
    re-running the entire DATEADD placement matrix.
61. **PostgreSQL MAKE_* constructor samples.** Give MAKE_DATE, MAKE_TIME,
    MAKE_TIMESTAMP, and MAKE_TIMESTAMPTZ one explicit signature baseline each.
    Across these cases distribute formula fields, both substitution kinds,
    joined sources, a derived-table consumer, and     optional timezone coverage.
    Assert named numeric/timezone expression fields and normal source
    collection; `year`/`month` field names here are not level metadata.
62. **PostgreSQL MAKE_INTERVAL source-form samples.** Cover supported positional
    and named forms, omitted components, formulas, and both substitution kinds
    in component expressions. Preserve component mapping and source form for
    regeneration; use one chained-CTE case. Do not silently rearrange positional
    arguments or invent defaults in the AST.
63. **PostgreSQL JUSTIFY_* interval samples.** Give JUSTIFY_DAYS, JUSTIFY_HOURS,
    and JUSTIFY_INTERVAL a baseline each; distribute an interval-valued column,
    interval expression, predicand variable, qualified column variable, and
    nested invocation across those tests. Put one in an EXCEPT branch and
    assert normal interval-expression lineage and PostgreSQL affinity.
64. **PostgreSQL zero-argument clock samples.** Cover CLOCK_TIMESTAMP,
    STATEMENT_TIMESTAMP, TRANSACTION_TIMESTAMP, and TIMEOFDAY in one uniquely
    aliased SELECT. Assert each invocation has no invented argument fields or
    source columns. Include an ORDER BY alias and a neutral CURRENT_TIMESTAMP
    control; add only arity diagnostics defined by the implemented contract.
65. **Cross-family isolation, UDFs, and dialect composition.** Sample a
    Snowflake add/diff nesting, a shared truncation with dialect-specific part,
    and a PostgreSQL-only nested expression. Add schema-qualified same-name
    UDF controls for one function from each new family. Assert normal UDF
    column arguments, family-specific allowed-part boundaries, one warning per
    target, accurate hit counts, mixed-dialect FATAL in both orders, and SCRIPT
    reset. Do not treat cross-dialect composition as a positive clean statement.

#### Phase 1 additional steps: per-function build and test actions

**Approved 2026-11-12; partially implemented.** This section defines the
execution scope for each function individually. Scenarios 1-65 remain the
case-formulation reference; the profiles below govern which functions repeat
deep context coverage. Do not interpret the earlier reduced-family examples as
permission to omit a primary function's deep suite. A build action includes
dedicated recognition, named AST construction, signature-specific metadata
validation, normal expression collection/substitution typing, and the existing
dialect collector. Complete and run its tests before claiming that invocation
implemented. Deliver incrementally with Slices B-E, not as one large rewrite.

66. **Define the compact sibling suite (S).** For every parameterized sibling
    invocation, write separate simple SELECT cases for: literal construction;
    qualified columns; unqualified columns with one source; ambiguous
    unqualified columns with two physical sources; formulas; unprefixed
    predicand substitutions; qualified COLUMN substitutions; and mixed
    substitution kinds where at least two expression slots exist. Exercise each
    expression slot at least once, with distinct values to detect reordering.
    For multiple slots, ambiguity must be tested in each slot separately; one
    representative both-slots ambiguity case is sufficient. Include a
    canonical part, representative accepted alias and quoted/bare forms where
    supported, and the invalid-date-part FATAL for every invocation with a
    date-part slot. Include invalid weekday/boundary tests for those metadata
    slots under their own validation contracts. Do not manufacture a date-part
    error for functions without a part parameter. Add same-name qualified UDF
    control, exact affinity expectations, and signature-specific optional/arity
    cases. All cases use the full assertion contract above: exact named AST,
    dictionaries/occurrences, interface, substitutions, and diagnostics.
    No sibling repeats the placement matrix merely because its name differs.
    Zero-argument siblings instead use profile Z in actions 92-95.
67. **Define the deep primary suite (D).** Every primary gets S plus the
    scope/clause/DML/set-operation coverage in scenarios 25-46, adapted to its
    signature, not copied by textual replacement of DATEADD. For each primary
    formulate three anchors: qualified column expressions; one-source
    unqualified expressions; multi-column formulas with inputs split across
    qualified joined sources. Substitute these into scenarios 29-46, covering
    derived producer/consumer and multiple layers, joins/ON, scalar queries,
    CTE producer/consumer/chains, correlation/shadowing, WHERE/BETWEEN/IN/EXISTS,
    grouping/HAVING, ordering/aliases, window clauses, INSERT, UPDATE, DELETE,
    UNION/UNION ALL, EXCEPT, INTERSECT, nested mixed set operations, and set-op
    consumers. Retain the distinct first/later/both branch placements.
    Also include scenario 12 visibility failures, 13-16 substitution varieties,
    21-24 applicable validation/state/occurrence checks, and 47-48 UDF/affinity
    controls. Do not repeat DATEADD's 24-shape standalone Cartesian matrix:
    use the primary's own argument shapes, covering every expression field
    with a column, formula, and both substitution kinds. For metadata, cover
    its documented allowed values independently of DATEADD.
    Use type-sensible wrappers: compare extracted/diff results numerically,
    date outputs with dates, interval outputs with intervals, and text outputs
    with text. Nested same-family calls require compatible argument/result
    types; otherwise use a compatible enclosing function rather than inventing
    invalid SQL. For PostgreSQL primaries, choose PostgreSQL-compatible DML
    forms; a Snowflake-only QUALIFY wrapper is an explicit mixed-dialect
    negative, not a clean positive. Record any genuinely inapplicable case and
    its reason rather than silently dropping a category. Add a dedicated
    primary-family test class extending the existing abstract test base when
    the suite is developed; keep sibling cases beside their primary, except
    the already planned DATEADD/add-shape classes. Do not create a new test
    framework or generic query-generation abstraction.
68. **Build/test DATEADD (primary, D plus full scenarios 1-48).** Finish the
    invalid-part FATAL prerequisite, then the agreed comprehensive standalone
    and placement suite. This is the reference for normal collection in two
    expression slots after metadata.
69. **Build/test TIMEADD (sibling, S).** Reuse the add-family implementation and
    mini-suite with `level/increment/value`; preserve its authored `function_name`.
    Use time-appropriate examples. Required negatives include invalid part and
    separately ambiguous INCREMENT and VALUE. Do not duplicate context tests.
70. **Build/test TIMESTAMPADD (sibling, S).** Reuse the add-family implementation
    with timestamp inputs; assert its own token/name and invocation rather than
    DATEADD. Include invalid part and separate INCREMENT/VALUE ambiguity.
    Move scenario 50's placement wrappers to primary coverage, not this suite.
71. **Build/test DATEDIFF (add-shaped sibling, S).** Its layout is metadata plus
    two expressions, so reuse the compact two-expression coverage, not a new
    deep placement suite. Build `level/left/right`; both expression operands
    are datetime-valued. Test distinct operands/order, invalid part, one-source
    unqualified success, and separate left/right ambiguity.
72. **Build/test TIMEDIFF (add-shaped sibling, S).** Reuse diff construction with
    its own invocation and suitable time operands. Include its own invalid
    part, unqualified success, left/right ambiguity, and both variable types.
    No dedicated HAVING/CTE context matrix is required for this alias.
73. **Build/test TIMESTAMPDIFF (add-shaped sibling, S).** Reuse diff construction
    with timestamp operands and distinct left/right dependencies. Include its
    own invalid part, unqualified success, and ambiguity per operand; preserve
    invocation and do not duplicate INTERSECT/context suites.

#### Entry 1 execution checkpoint (2026-11-14): diff-family implementation plan (actions 71-73)

This checkpoint defines the concrete implementation and test inventory for the
next milestone: `DATEDIFF` / `TIMEDIFF` / `TIMESTAMPDIFF` with
`level/left/right` shaping and full left-right visibility validation.

**Status at checkpoint**
- DATEADD family milestone is complete through the scenario-65 boundary and
  validated in `SqlEventWalkerDatetimeFunctionPlacementTests`.
- Diff-family grammar, walker shaping, validation, and affinity are now present,
  with initial focused tests. The DF-01..DF-96 closure catalog below remains
  largely open and is the next verification target; truncation implementation
  no longer depends on waiting for that closure.

**Implementation plan (code changes)**
1. **Grammar routing and parser productions**
   - Ensure each diff invocation binds to dedicated datetime-function rules and
     preserves invocation name text/case.
2. **Walker shaping**
   - Build `datetime_function={function_name, level, left, right}` in SQL
     argument order.
   - Stamp predicand substitutions in `left` and `right` exactly as in add-family
     expression slots.
3. **Part/level validation**
   - Reuse canonical registry + alias normalization for `level`.
   - Emit family-specific invalid-part diagnostics at the level token location.
4. **Resolution/collection contracts**
   - Preserve existing table/query dictionary collection behavior for both
     expression slots; never collect `level` as a source column.
   - Preserve existing ambiguity/unresolved/hidden-interface diagnostics.
5. **Dialect affinity**
   - Register diff-family hits through the existing statement dialect collector;
     preserve mixed-dialect FATAL behavior and SCRIPT statement reset semantics.
6. **Regression guard**
   - Keep schema-qualified same-name UDF invocations generic (not datetime-family)
     unless the grammar specifically routes them as built-ins.

**Realistic-context test rule (applies to all scenarios below)**
- Every scenario should include lightweight natural SQL context around the
  target function: add 1-3 nearby non-target expressions/columns and at least
  one benign surrounding clause pattern (for example extra SELECT projections,
  JOIN filters, COALESCE/CASE wrapper, WHERE conjunct, ORDER BY alias use).
- Keep this deterministic (stable SQL text), not random at runtime, so goldens
  remain reproducible while still surfacing side effects.

**Diff-family scenario catalog (96 cases; IDs DF-01..DF-96)**

**A. Signature and level metadata (DF-01..DF-12)**
- DF-01 DATEDIFF baseline with qualified `left/right`.
- DF-02 DATEDIFF unqualified one-source operands.
- DF-03 TIMEDIFF baseline.
- DF-04 TIMESTAMPDIFF baseline.
- DF-05 Canonical shared levels captured in metadata.
- DF-06 Alias level spellings normalize but preserve authored text.
- DF-07 Quoted level (`STRING` form) preserved.
- DF-08 Identifier-form level preserved.
- DF-09 Invalid identifier level FATAL at level token.
- DF-10 Invalid quoted level FATAL at level token.
- DF-11 Mixed-case invocation name preserved.
- DF-12 Distinct `left/right` order preserved in AST and lineage.

**B. Left/right argument-shape matrix (DF-13..DF-37)**
- 25 explicit combinations using shapes `L`, `CQ`, `CU`, `FQ`, `FU`
  crossed for left x right.
- Include all four simple qualification pairs (`CQ/CQ`, `CQ/CU`, `CU/CQ`,
  `CU/CU`) and both formula-on-one-side / formula-on-both-sides variants.
- DF-13..DF-17 left `L` x right `L/CQ/CU/FQ/FU`
- DF-18..DF-22 left `CQ` x right `L/CQ/CU/FQ/FU`
- DF-23..DF-27 left `CU` x right `L/CQ/CU/FQ/FU`
- DF-28..DF-32 left `FQ` x right `L/CQ/CU/FQ/FU`
- DF-33..DF-37 left `FU` x right `L/CQ/CU/FQ/FU`

**C. Ambiguity and visibility failures (DF-38..DF-49)**
- DF-38 Ambiguous unqualified `left`.
- DF-39 Ambiguous unqualified `right`.
- DF-40 Both sides ambiguous (distinct names).
- DF-41 Unresolved unqualified `left`.
- DF-42 Unresolved unqualified `right`.
- DF-43 Unknown alias on `left`.
- DF-44 Unknown alias on `right`.
- DF-45 Hidden non-exported derived column in `left`.
- DF-46 Hidden non-exported derived column in `right`.
- DF-47 Missing derived export name in one slot.
- DF-48 Missing CTE export name in one slot.
- DF-49 Overlapping derived/CTE exports trigger ambiguity.

**D. Substitutions in left/right trees (DF-50..DF-67)**
- DF-50 Predicand substitution in `left` only.
- DF-51 Predicand substitution in `right` only.
- DF-52 Predicand substitutions in both slots.
- DF-53 Qualified column substitution in `left` only.
- DF-54 Qualified column substitution in `right` only.
- DF-55 Column substitutions in both slots.
- DF-56 Mixed kinds: predicand-left / column-right.
- DF-57 Mixed kinds: column-left / predicand-right.
- DF-58 Predicand substitutions inside `left` formula.
- DF-59 Predicand substitutions inside `right` formula.
- DF-60 Column substitutions inside `left` formula.
- DF-61 Column substitutions inside `right` formula.
- DF-62 Mixed substitutions + ordinary columns in `left` formula.
- DF-63 Mixed substitutions + ordinary columns in `right` formula.
- DF-64 Mixed substitutions in both formulas.
- DF-65 Joined-source substitution typing stays isolated.
- DF-66 Missing qualified column substitution remains fatal without join extension.
- DF-67 Same reference with join extension follows existing defer/warning policy.

**E. Nested and sibling isolation (DF-68..DF-76)**
- DF-68 DATEDIFF nested in ordinary function argument #1.
- DF-69 DATEDIFF nested in ordinary function argument #2.
- DF-70 Ordinary function nested in `left`.
- DF-71 Ordinary function nested in `right`.
- DF-72 Nested diff in `left` with valid outer diff.
- DF-73 Nested diff in `right` with valid outer diff.
- DF-74 Invalid inner level does not contaminate sibling diff expression.
- DF-75 Repeated references preserve existing occurrence semantics.
- DF-76 Distinct sibling diff outputs retain independent lineage.

**F. Producer/consumer and scope propagation (DF-77..DF-86)**
- DF-77 Derived-table producer exports diff result lineage.
- DF-78 Derived-table consumer computes diff from exports.
- DF-79 Two-layer derived rename chain.
- DF-80 CTE producer lineage.
- DF-81 CTE consumer lineage.
- DF-82 Chained CTE rename path.
- DF-83 Scalar subquery supplies `left`.
- DF-84 Scalar subquery supplies `right`.
- DF-85 Correlated scalar in `left`.
- DF-86 Correlated scalar in `right`.

**G. Clause/DML/set-op placement (DF-87..DF-94)**
- DF-87 WHERE placement.
- DF-88 HAVING placement.
- DF-89 ORDER BY expression + output alias placement.
- DF-90 Window PARTITION/ORDER placement.
- DF-91 INSERT ... SELECT placement.
- DF-92 UPDATE (SET and WHERE) placement.
- DF-93 DELETE (WHERE and EXISTS) placement.
- DF-94 UNION/EXCEPT/INTERSECT + nested set-op branch isolation.

**H. UDF controls and statement reset (DF-95..DF-96)**
- DF-95 Schema-qualified same-name UDF remains generic.
- DF-96 SCRIPT multi-statement reset and per-statement affinity isolation.

#### Diff-family verification update (2026-11-16)

Added focused verification coverage for the remaining highest-risk diff behavior
(independent `left`/`right` operand resolution) in
`SqlEventWalkerDatetimeFunctionPlacementTests`:

- [x] TIMEDIFF alias sample in HAVING with predicand substitution in `right`.
- [x] TIMESTAMPDIFF alias sample in CTE-over-INTERSECT with predicand substitution in `right`.
- [x] TIMEDIFF invalid part fatal with exact token/position/message assertions.
- [x] TIMESTAMPDIFF invalid part fatal with exact token/position/message assertions.
- [x] Completed Section A closure for diff family: quoted-level metadata and mixed-case invocation preservation for TIMEDIFF/TIMESTAMPDIFF, plus quoted invalid-part fatal coverage per invocation.
- [x] Completed Section B (`DF-13..DF-37`) 25-case left/right shape matrix with strict goldens (`L/CQ/CU/FQ/FU` × `L/CQ/CU/FQ/FU`).
- [x] Completed Section C (`DF-38..DF-49`) ambiguity/visibility failures, including unknown-alias operand failures, missing derived export handling, and overlapping CTE export ambiguity.
- [x] Completed Section D (`DF-50..DF-67`) substitution matrix for `left`/`right` trees (predicand/column/mixed/formula/joined-source) including join-extension fatal vs deferred behavior.
- [x] Completed Section E (`DF-68..DF-76`) nested/sibling isolation, including nested-diff-in-wrapper positions, wrapper-in-operand shapes, inner-invalid-part isolation against sibling diffs, repeated-reference occurrence semantics, and independent sibling output lineage.
- [x] Completed Section F (`DF-77..DF-86`) producer/consumer and scope propagation through derived producers/consumers, two-layer rename chains, CTE producer/consumer chains, and scalar/correlated scalar subqueries in both `left` and `right` operands.
- [x] Completed Section G (`DF-87..DF-94`) clause/DML/set-op placement for WHERE, HAVING, ORDER BY expression+alias, window PARTITION/ORDER, INSERT…SELECT, UPDATE SET+WHERE, DELETE WHERE+EXISTS, and set-op branch isolation.
- [x] Completed Section H (`DF-95..DF-96`) schema-qualified same-name UDF generic control for DATEDIFF and SCRIPT statement-level dialect-affinity reset/isolation verification.
- [x] Added DIFF-family snippet production: full DATEDIFF endpoint matrix (`PREDICAND`, `CONDITION`, `QUERY`, `TUPLE`, `IN_LIST`, `JOIN_EXTENSION`, `VALUES`, `INSERT`, `UPDATE`, `DELETE`, `DDL`, `SCRIPT`) plus TIMEDIFF/TIMESTAMPDIFF predicand substitution controls with strict AccessObject goldens.
- [x] Added missing prior-family snippet coverage with substitution-slot assertions for EXTRACT, DATE_PART, DATE_TRUNC, TIME_SLICE, LAST_DAY, NEXT_DAY, and PREVIOUS_DAY.
- [x] Added Postgres-family snippet endpoint matrices at the same depth for DATE_BIN, AGE, MAKE_TIMESTAMP, JUSTIFY_INTERVAL, and CLOCK_TIMESTAMP (`PREDICAND`, `CONDITION`, `QUERY`, `TUPLE`, `IN_LIST`, `JOIN_EXTENSION`, `VALUES`, `INSERT`, `UPDATE`, `DELETE`, `DDL`, `SCRIPT`) plus sibling predicand controls for MAKE_DATE/MAKE_TIME/MAKE_TIMESTAMPTZ/MAKE_INTERVAL, JUSTIFY_DAYS/JUSTIFY_HOURS, and STATEMENT_TIMESTAMP/TRANSACTION_TIMESTAMP/TIMEOFDAY.

These close the representative Entry 1 item-52 gaps for alias+substitution and
per-invocation invalid-part verification, and close Sections A-H of the
DF-01..DF-96 catalog.

**Remaining datetime-family scenario catalogs (planning checkpoint, 2026-11-14)**

> **Test-hosting recommendation (applies to all blocks below):**
> split the current large datetime placement class into family-focused classes.
> Suggested classes:
> - `SqlEventWalkerDatetimeTruncExtractTests`
> - `SqlEventWalkerDatetimeBoundarySliceTests`
> - `SqlEventWalkerDatetimePostgresDiffBinAgeTests`
> - `SqlEventWalkerDatetimeMakeConstructorTests`
> - `SqlEventWalkerDatetimeJustifyTests`
> - `SqlEventWalkerDatetimeClockTimestampTests`
> Keep only shared helpers in the existing placement base/test utilities.

### TRUNC/EXTRACT family plan (DATE_TRUNC, datetime TRUNC, DATE_PART, EXTRACT)
- **Core functionality scenarios (TE-C01..TE-C28)**
  - Invocation-shape ASTs for DATE_TRUNC/ TRUNC (`value/level`) and DATE_PART/EXTRACT (`level/value`)
  - Comma and FROM forms for DATE_PART/EXTRACT with syntax discriminator assertions
  - Canonical/text/form metadata coverage for keyword/identifier/string part forms
  - Column lineage for single-source, qualified multi-source, formula-wrapped value expressions
  - Placement core: SELECT/WHERE/HAVING/ORDER BY + derived/CTE producer-consumer
- **Malformed/failure scenarios (TE-M01..TE-M24)**
  - Invalid part diagnostics per invocation (no cross-function collapse)
  - Ambiguous/unqualified value diagnostics and unresolved behavior checks
  - Hidden/missing export failures through derived/CTE boundaries
  - TRUNC one-arg/numeric controls (must remain generic or numeric path as applicable)
- **Section D — mandatory snippet substitution coverage (fold into TE-M/TE-R execution)**
  - Add explicit snippet-endpoint tests for this family in `SqlEventWalkerDatetimeFunctionSnippetTests` (or family-specific snippet class if split) using endpoint forms where datetime functions are valid (`PREDICAND`, `CONDITION`, `QUERY`, `TUPLE`, `JOIN_EXTENSION`, `IN_LIST`, `VALUES`, `INSERT`, `UPDATE`, `DELETE`, `DDL`, `SCRIPT`).
  - Exercise substitution variables in every argument slot that accepts predicand/column trees (`value` and family-specific expression slots); verify substitution typing, table/query dictionary collection, and symbol-table payloads with full literal goldens.
  - Keep DATEADD-only snippet presence checks as insufficient for closure; each family must prove its own snippet-path behavior.
- **Complete regression scenarios (TE-R01..TE-R44)**
  - Nested/sibling isolation, substitution typing, set-op branch isolation
  - DML placements (INSERT/UPDATE/DELETE), window wrappers where legal
  - UDF same-name controls, multi-statement reset, mixed-dialect affinity checks
  - **Total planned: 96 (TE-01..TE-96)**
- **Build/test plan note:** implement in `SqlEventWalkerDatetimeTruncExtractTests`; keep parser/walker regressions targeted to this class while iterating.

**TRUNC/EXTRACT coverage reconciliation (2026-11-14, based on currently passing pre-existing suites)**

Validated suites:
- `SqlEventWalkerExtractTests`
- `SqlEventWalkerDatetimeFunctionPlacementTests`
- `SqlEventWalkerDatetimeFunctionSnippetTests`
- `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests`

Covered now (**mark complete**):
- [x] **TE-C01** Invocation-shape AST for EXTRACT/DATE_PART (FROM and comma forms) under `datetime_function`.
- [x] **TE-C02** Basic DATE_TRUNC and datetime TRUNC happy-path AST shape (`level/value` and `value/level` order normalization).
- [x] **TE-C03** EXTRACT/DATE_PART part-form coverage (`KEYWORD` + `STRING`) and broad part vocabulary (Snowflake/Postgres/timezone/epoch families).
- [x] **TE-C04** EXTRACT/DATE_PART source-shape coverage (column, qualified column, typed literal, parentheses, arithmetic, CAST, nested extract).
- [x] **TE-C05** Predicand substitution coverage for EXTRACT/DATE_PART source trees.
- [x] **TE-C06** Dialect-affinity warning coverage for EXTRACT/DATE_PART (including comma-form and nested combinations).
- [x] **TE-C07** Snippet endpoint presence for datetime-function family behavior (datetime snippet suite now includes DATEADD + DIFF family endpoint matrix and substitution-focused coverage for EXTRACT/DATE_PART/DATE_TRUNC and boundary/slice functions).

Still to add for this family (**not covered yet**):
- [ ] **TE-C08..TE-C12 (core placement expansion):** explicit WHERE/HAVING/ORDER BY/window/derived+CTE producer-consumer coverage for DATE_TRUNC and datetime TRUNC (not just EXTRACT/DATE_PART).
- [ ] **TE-M01..TE-M08:** invalid-part fatal diagnostics per invocation for DATE_TRUNC and datetime TRUNC, including token/position/message goldens.
- [ ] **TE-M09..TE-M14:** ambiguous/unqualified VALUE diagnostics for DATE_TRUNC/TRUNC across one-source/two-source/derived contexts.
- [ ] **TE-M15..TE-M20:** hidden-export and missing-output visibility failures for DATE_TRUNC/TRUNC through derived/CTE chains.
- [ ] **TE-M21..TE-M24:** TRUNC controls proving numeric/one-arg TRUNC remains generic (non-datetime) while datetime signature promotes.
- [ ] **TE-R01..TE-R18:** nested/sibling isolation for mixed EXTRACT/DATE_PART/DATE_TRUNC/TRUNC compositions.
- [ ] **TE-R19..TE-R30:** DML placement coverage (INSERT/UPDATE/DELETE) for TRUNC/EXTRACT signatures.
- [ ] **TE-R31..TE-R38:** set-op branch isolation and multi-layer derived/CTE rename-chain regression.
- [ ] **TE-R39..TE-R44:** schema-qualified same-name UDF controls + SCRIPT statement-reset/mixed-affinity regression.

Minimum parity implementation update (2026-11-14):
- [x] Implemented a dedicated parity batch in `SqlEventWalkerDatetimeTruncExtractTests` covering:
  - **TE-C08..TE-C12** core DATE_TRUNC/datetime TRUNC placement (WHERE/HAVING/ORDER BY/window + derived/CTE producer-consumer).
  - **TE-M01..TE-M24** malformed/diagnostic guards for invalid part fatal positions/messages, ambiguous/unqualified value behavior, hidden/missing derived outputs, and TRUNC generic-vs-datetime controls.
  - **TE-R19..TE-R30** DML placement guards (INSERT/UPDATE/DELETE) for TRUNC/EXTRACT signatures.
  - **TE-R39..TE-R44** schema-qualified same-name UDF controls, SCRIPT statement-reset, and mixed Snowflake/Postgres affinity fatal coverage.

Current effective status after the minimum-parity batch:
- **Completed:** the 12 core scenarios, all 24 malformed/diagnostic scenarios,
  DML scenarios TE-R19..TE-R30, and UDF/reset/affinity scenarios TE-R39..TE-R44.
- **Remaining:** none for the TRUNC/EXTRACT Slice C deep-regression block.
  TE-R01..TE-R18 nested/sibling isolation and TE-R31..TE-R38 set-operation
  plus multi-layer rename-chain scenarios are now implemented in
  `SqlEventWalkerDatetimeTruncExtractTests` and passing.

### Datetime-family golden assertion rule (applies to remaining slices)
- Family test classes must assert **both**:
  1) `SqlParserAccess` snippet snapshots (`getSnippet()` trees/dictionaries/symbol table), and
  2) direct walker extractor snapshots (`runParsertest` / `runScriptParsertest` outputs).
- Exception: `SCRIPT` `arrayOutputCollectors` may differ because `SqlParserAccess` enriches collector payloads with
  statement-level warning context; assert collector equality on the access-layer snapshot and assert extractor collectors for non-`SCRIPT` endpoints.
- This dual assertion pattern is now applied in:
  - `SqlEventWalkerDatetimeTruncExtractTests` (Slice C),
  - `SqlEventWalkerDatetimeBoundarySliceTests` (Slice D),
  and should be used for remaining Entry 1 family test classes (Slices E/F work).

### Snowflake boundary/slice family plan (TIME_SLICE, LAST_DAY, NEXT_DAY, PREVIOUS_DAY)
- **Core functionality scenarios (BS-C01..BS-C24)**
  - TIME_SLICE signature (`value/slice_length/level/[boundary]`) with omitted/explicit boundary
  - LAST_DAY optional level behavior with omitted-level semantics preserved
  - NEXT_DAY/PREVIOUS_DAY weekday handling with dedicated weekday metadata
  - Normal column lineage/visibility in each expression slot
  - Core placement and two-level derived/CTE propagation
- **Malformed/failure scenarios (BS-M01..BS-M20)**
  - Invalid level diagnostics for TIME_SLICE/LAST_DAY only
  - Invalid boundary diagnostics for TIME_SLICE
  - Invalid weekday diagnostics for NEXT_DAY/PREVIOUS_DAY
  - Ambiguous/unqualified value and export-visibility failures
- **Section D — mandatory snippet substitution coverage (fold into BS-M/BS-R execution)**
  - Add snippet-endpoint tests for this family across the supported snippet endpoints, including `SCRIPT` multi-statement reset behavior.
  - Validate substitutions in all predicand-capable slots (`value`, `slice_length`, and optional boundary/weekday positions where grammar permits expression/substitution forms) with exact golden assertions for AST/interface/dictionaries/symbol tables.
  - Include negative snippet cases for invalid level/boundary/weekday diagnostics with exact position/message assertions.
- **Complete regression scenarios (BS-R01..BS-R36)**
  - Substitutions in value/length/weekday slots, nested function wrappers
  - Clause/DML/set-op placement and sibling isolation
  - UDF controls and statement reset
  - **Total planned: 80 (BS-01..BS-80)**
- **Build/test plan note:** implement in `SqlEventWalkerDatetimeBoundarySliceTests`; keep family-specific negative diagnostics goldened in this class.
- **Default dialect hook note:** register each Snowflake-only grammar path in `SqlGrammarDialectRuleRegistry`, assert the Snowflake warning at its source position in every diagnostic test, and include mixed-dialect fatal checks in regression coverage.

Minimum parity implementation update (2026-11-14):
- [x] Added dedicated grammar + walker coverage for `TIME_SLICE`, `LAST_DAY`,
  `NEXT_DAY`, and `PREVIOUS_DAY` under `datetime_function`.
- [x] Registered Snowflake-only grammar paths for this family in
  `SqlGrammarDialectRuleRegistry` so statement-dialect warnings/fatals follow
  the standard counter/affinity mechanism.
- [x] Implemented minimum parity suite in
  `SqlEventWalkerDatetimeBoundarySliceTests` using one query per test method
  and full literal goldens for AST/interface/substitutions/table dictionary/
  query dictionary/symbol table/array collectors.
- [x] Implemented core placement parity set for boundary/slice family:
  WHERE/HAVING/ORDER BY/window + derived/CTE producer-consumer.
- [x] Implemented malformed/diagnostic parity set for boundary/slice family:
  invalid level, invalid boundary, invalid weekday, ambiguous unqualified value,
  and missing derived output diagnostics (with position/message assertions).
- [x] Implemented minimum regression parity set for boundary/slice family:
  INSERT/UPDATE/DELETE placements, schema-qualified same-name UDF generic-path
  control, SCRIPT statement reset behavior, and mixed-affinity fatal checks.
- [x] Implemented first deep-regression closure batch for boundary/slice family:
  nested/sibling isolation compositions plus explicit valid-slot substitution
  lineage checks (no part-slot leakage) with full access-object and direct
  extractor golden assertions.
- [x] Implemented set-operation branch-isolation coverage for boundary/slice
  functions (`UNION ALL`, `INTERSECT`, `EXCEPT`) with per-branch lineage
  verification and interface/set-op symbol-table assertions.
- [x] Implemented remaining multi-layer scope/rename propagation coverage for
  boundary/slice functions, including chained CTE rename propagation and
  derived set-op consumer lineage checks.
- [x] Moved boundary/weekday value sets into shared grammar registry
  (`SnowflakeDatetimeValueRegistry`) so external consumers can access these
  validity lists independently of the event walker implementation.

#### BS matrix accounting (strict traceability, 2026-11-16)

Canonical BS-ID mapping used for closure (every BS ID is bound to one or more
implemented tests; no unassigned IDs remain):

| BS IDs | Coverage theme | Evidence tests |
|---|---|---|
| BS-C01..BS-C05 | TIME_SLICE core signature + value/slice_length lineage in clause placement | `coreTimeSliceInWhereCollectsFilters` |
| BS-C06..BS-C09 | LAST_DAY placement + grouped/filter lineage | `coreLastDayInHavingCollectsGroupedAndFilterDependencies` |
| BS-C10..BS-C12 | NEXT_DAY ordered-by lineage | `coreNextDayInOrderByTracksOrderedBy` |
| BS-C13..BS-C15 | PREVIOUS_DAY window partition/order lineage | `corePreviousDayInWindowTracksWindowBuckets` |
| BS-C16..BS-C20 | Two-level producer/consumer propagation (CTE + derived) | `coreTimeSliceAcrossCteAndDerivedProducerConsumer` |
| BS-C21..BS-C24 | Nested boundary/slice compositions and sibling-isolation core checks | `regressionNestedTimeSliceInsideLastDayCollectsOnlyValueAndLengthLineage`, `regressionNestedNextDayAroundTimeSlicePreservesNestedLineageIsolation`, `regressionSiblingBoundaryFunctionsShareScopeWithoutPartSlotLeakage` |
| BS-M01..BS-M04 | Invalid level diagnostics (TIME_SLICE, LAST_DAY) | `malformedTimeSliceInvalidLevelFatal`, `malformedLastDayInvalidLevelFatal` |
| BS-M05..BS-M07 | Invalid boundary diagnostics (TIME_SLICE) | `malformedTimeSliceInvalidBoundaryFatal` |
| BS-M08..BS-M11 | Invalid weekday diagnostics (NEXT_DAY, PREVIOUS_DAY) | `malformedNextDayInvalidWeekdayFatal`, `malformedPreviousDayInvalidWeekdayFatal` |
| BS-M12..BS-M16 | Ambiguous/unqualified value diagnostics | `malformedTimeSliceAmbiguousValueDiagnostics` |
| BS-M17..BS-M20 | Export-visibility/missing-derived-output failures | `malformedTimeSliceMissingDerivedOutputFatal` |
| BS-R01..BS-R03 | DML placement (INSERT/UPDATE/DELETE) | `regressionDmlInsertPlacementForTimeSlice`, `regressionDmlUpdatePlacementForLastDay`, `regressionDmlDeletePlacementForNextDay` |
| BS-R04..BS-R06 | UDF same-name generic-path controls | `regressionSchemaQualifiedSameNameUdfStaysGenericForTimeSlice` |
| BS-R07..BS-R10 | Nested wrappers and sibling isolation regression | `regressionNestedTimeSliceInsideLastDayCollectsOnlyValueAndLengthLineage`, `regressionNestedNextDayAroundTimeSlicePreservesNestedLineageIsolation`, `regressionSiblingBoundaryFunctionsShareScopeWithoutPartSlotLeakage` |
| BS-R11..BS-R14 | Valid-slot substitution typing/lineage (no keyword-slot leakage) | `regressionSiblingValidSubstitutionSlotsCollectWithoutKeywordLeakage`, snippet coverage: `timeSlicePredicandSnippet`, `lastDayPredicandSnippet`, `nextDayPredicandSnippet`, `previousDayPredicandSnippet` |
| BS-R15..BS-R20 | Set-op branch isolation (`UNION ALL`, `INTERSECT`, `EXCEPT`) | `regressionSetOpUnionAllTimeSliceBranchIsolation`, `regressionSetOpIntersectLastDayBranchIsolation`, `regressionSetOpExceptNextPreviousDayBranchIsolation` |
| BS-R21..BS-R28 | Multi-layer scope/rename propagation and derived set-op consumer behavior | `regressionMultiLayerRenameChainForTimeSlice`, `regressionMultiLayerSetOpDerivedForBoundaryFamily` |
| BS-R29..BS-R32 | SCRIPT statement reset / script-scope collector behavior | `regressionScriptResetAndAffinityIsolationForBoundaryFamily` |
| BS-R33..BS-R36 | Mixed-dialect fatal controls with boundary/slice members | `regressionMixedAffinityFatalForBoundaryFamily` |

Matrix result: **BS-C01..BS-C24, BS-M01..BS-M20, BS-R01..BS-R36 are all assigned
and implemented**.

The grammar, walker, registries, core placements, malformed diagnostics, DML,
UDF controls, statement reset, and mixed-affinity minimum parity are delivered.

### Postgres difference/bin/age family plan (DATE_BIN, AGE)
- **Core functionality scenarios (PG-C01..PG-C18)**
  - DATE_BIN stride/source/origin slot ordering and lineage
  - AGE one-arg and two-arg forms with preserved arity semantics
  - Qualified/unqualified/formula inputs across all slots
  - Core placement and producer-consumer propagation
- **Malformed/failure scenarios (PG-M01..PG-M14)**
  - Ambiguous source/origin for DATE_BIN
  - AGE left/right ambiguity and hidden-export failures
  - Invalid-shape controls (arity/signature mismatch paths)
- **Section D — mandatory snippet substitution coverage (fold into PG-M/PG-R execution)**
  - Add snippet-endpoint tests for DATE_BIN and AGE with substitutions in each expression slot (`stride/source/origin` and AGE one/two-argument forms as applicable).
  - Verify substitution typing and normal column resolution/collection through snippet parsing with strict golden outputs.
  - Add snippet negative controls for signature misuse and ambiguity/visibility failures.
- **Complete regression scenarios (PG-R01..PG-R24)**
  - Substitution typing per slot, nested/sibling isolation
  - DML/set-op placement and mixed-source visibility proofs
  - UDF controls + statement reset
  - **Total planned: 56 (PG-01..PG-56)**
- **Build/test plan note:** implement in `SqlEventWalkerDatetimePostgresDiffBinAgeTests`; run targeted selectors for AGE vs DATE_BIN sub-batches.
- **Default dialect hook note:** register each Postgres-only grammar path in `SqlGrammarDialectRuleRegistry`, assert the Postgres warning at its source position in every diagnostic test, and include mixed-dialect fatal checks in regression coverage.

### MAKE constructor family plan (MAKE_DATE, MAKE_TIME, MAKE_TIMESTAMP, MAKE_TIMESTAMPTZ, MAKE_INTERVAL)
- **Core functionality scenarios (MK-C01..MK-C30)**
  - Ordered-field AST/lineage for 3-field, 6-field, 6+timezone variants
  - MAKE_INTERVAL positional and named forms with source-form retention
  - Field-by-field column/formula/substitution collection
  - Core placement, derived/CTE propagation, and basic DML wrappers
- **Malformed/failure scenarios (MK-M01..MK-M24)**
  - Field-local ambiguity/unqualified failures for each constructor shape
  - MAKE_INTERVAL component misuse/missing-field shape controls
  - Hidden-export visibility failures through derived/CTE chains
- **Section D — mandatory snippet substitution coverage (fold into MK-M/MK-R execution)**
  - Add snippet-endpoint tests for all MAKE* constructors, including named/positional MAKE_INTERVAL forms.
  - Require substitution-variable coverage for every field position that accepts predicand expressions; assert per-field typing and dictionary/symbol-table placement with exact goldens.
  - Include malformed snippet controls for component misuse and missing/invalid field shapes.
- **Complete regression scenarios (MK-R01..MK-R42)**
  - Full field matrix permutations, nested wrappers, set-op isolation
  - Multi-layer rename chains and substitution typing in each field family
  - UDF controls + statement reset
  - **Total planned: 96 (MK-01..MK-96)**
- **Build/test plan note:** implement in `SqlEventWalkerDatetimeMakeConstructorTests`; keep helper builders for repetitive field-matrix assertions local to this class.
- **Default dialect hook note:** wire constructor-specific dialect affinity through the default statement-dialect collector (rule/semantic registry as applicable), assert by-position warnings where emitted, and cover mixed-dialect fatal behavior.

### JUSTIFY family plan (JUSTIFY_INTERVAL, JUSTIFY_DAYS, JUSTIFY_HOURS)
- **Core functionality scenarios (JY-C01..JY-C14)**
  - One-argument interval-expression AST/lineage across all three invocations
  - Qualified/unqualified/formula/substitution value coverage
  - Core placement and producer-consumer propagation
- **Malformed/failure scenarios (JY-M01..JY-M10)**
  - Ambiguous/unqualified value failures
  - Hidden-export and missing-output visibility failures
  - Invocation routing controls (no part-diagnostic mislabeling)
- **Section D — mandatory snippet substitution coverage (fold into JY-M/JY-R execution)**
  - Add snippet-endpoint tests for JUSTIFY* invocations with substitutions in the interval-expression input.
  - Verify substitution typing and ordinary column resolution/collection behaviors in snippet contexts with strict goldens.
  - Include snippet negatives for ambiguity/visibility and routing-control diagnostics.
- **Complete regression scenarios (JY-R01..JY-R20)**
  - Nested/sibling isolation, DML/set-op placement, statement reset
  - UDF same-name controls and dialect-affinity checks
  - **Total planned: 44 (JY-01..JY-44)**
- **Build/test plan note:** implement in `SqlEventWalkerDatetimeJustifyTests`; use compact sibling coverage for JUSTIFY_DAYS/HOURS after full JUSTIFY_INTERVAL primaries.
- **Default dialect hook note:** treat JUSTIFY* as Postgres-only for default affinity registration, assert by-position Postgres warnings in parser-access tests, and include mixed-dialect fatal regression scenarios.

### Clock timestamp family plan (CLOCK_TIMESTAMP, STATEMENT_TIMESTAMP, TRANSACTION_TIMESTAMP, TIMEOFDAY)
- **Core functionality scenarios (CK-C01..CK-C14)**
  - Zero-argument AST construction and invocation identity per function
  - Independent alias outputs and no-input lineage invariants
  - Core placement in SELECT/ORDER/GROUP-compatible wrappers
- **Malformed/failure scenarios (CK-M01..CK-M10)**
  - Signature misuse / argument-introduction rejection paths
  - UDF same-name controls and function-routing protections
  - Mixed-affinity fatal checks where statement rules require
- **Section D — mandatory snippet substitution coverage (fold into CK-M/CK-R execution)**
  - Add snippet-endpoint tests proving zero-argument family behavior across snippet endpoints where invocation is legal, including SCRIPT reset behavior.
  - Assert that no argument-level substitution/column collection is introduced for these zero-arg functions, while surrounding query substitutions still type and resolve normally.
  - Include snippet negatives for illegal argument introduction and same-name UDF routing controls.
- **Complete regression scenarios (CK-R01..CK-R20)**
  - Derived/CTE propagation, DML/set-op placement where valid
  - Multi-statement reset and sibling isolation
  - CURRENT_TIMESTAMP neutral controls to prevent false family extension
  - **Total planned: 44 (CK-01..CK-44)**
- **Build/test plan note:** implement in `SqlEventWalkerDatetimeClockTimestampTests`; enforce explicit “no argument columns/substitutions” assertions in every core case.
- **Default dialect hook note:** keep zero-arg timestamp family on the standard statement-dialect hooks (Postgres warning by position for these forms; neutral controls for shared `CURRENT_TIMESTAMP`), plus mixed-dialect fatal checks in script/statement reset coverage.

**Per-scenario assertion contract**
- Use exactly one SQL/query string per JUnit test method. Do not bundle multiple
  independent queries in one test method; split them so each failure maps to a
  single query and a single golden set.
- Full literal `assertEquals` goldens for AST/interface/substitutions/table
  dictionary/query dictionary/symbol table.
- Exact diagnostic assertions (code, severity, message, token text, line/char,
  diagnostic count) for all negative/edge cases.
- Do not use permissive assertions (`assertTrue`/`assertFalse` with `contains`,
  partial substring checks) as a substitute for full-object goldens or exact
  diagnostic assertions.
- For every query that intentionally exercises Snowflake-only or PostgreSQL-only
  grammar, assert the dialect warning by source position (code/severity/message
  fragment/token/line/char) with the standard diagnostic helpers.
- Symbol-path checks for key left/right scope buckets and set-op interfaces.
74. **Build/test DATE_TRUNC (primary, D).** Primary for leading part metadata
    plus one expression: `level/value`. Use valid parts and date-valued formula
    anchors; include invalid part, unqualified VALUE, and ambiguous VALUE.
    Deep grouping/order/window cases must assert the corresponding buckets.
75. **Build/test DATE_PART (syntax-variant sibling, S).** Share leading-part/
    expression collection coverage with DATE_TRUNC, but implement extraction
    semantics and both comma/FROM forms under the approved new datetime AST.
    Run S for both syntactic alternatives, including invalid bare/quoted parts,
    unqualified/ambiguous VALUE, and syntax-discriminator goldens. Update all
    affected August tests; do not repeat DATE_TRUNC's placement matrix.
76. **Build/test EXTRACT (syntax-variant sibling, S).** Fold into the datetime
    AST with its own invocation and extraction registry. Apply S to FROM and
    the parser's accepted comma form, including invalid parts and unqualified/
    ambiguous source expressions. Preserve interval/typed literal source
    semantics, extended/timezone part handling, and endpoint coverage. Update
    affected old tests and consumer instructions; do not repeat deep wrappers.
77. **Build/test LAST_DAY (primary, D).** Primary for expression followed by
    optional part: `value/[level]`. Cover omitted/explicit level and its own
    smaller allowed-part set, invalid part, unqualified/ambiguous VALUE, both
    variable kinds, and full deep contexts. Do not normalize an omitted level
    into an invented authored AST argument.
78. **Build/test datetime TRUNC (sibling, S).** Reuse trailing-part expression
    placement coverage from LAST_DAY, not its allowed-part set or semantics.
    Assert `value/level` order, invalid part, unqualified/ambiguous VALUE, and
    both variable kinds. Retain scenario 54 numeric/one-argument/DDL controls;
    one-argument TRUNC stays generic without distinguishing semantic evidence.
79. **Build/test TIME_SLICE (primary, D).** Unique
    `value/slice_length/level/[boundary]` signature. Cover both expression
    fields with qualified/unqualified/formula/substitution inputs, ambiguity
    separately in each, valid/invalid part, and omitted/explicit boundary
    choices and invalid boundary. Replicate deep contexts with timestamp/date
    anchors and appropriate length expressions; do not parse boundary as a
    normal source column or borrow DATEADD's validity set.
80. **Build/test NEXT_DAY (primary, D).** Primary for `value/weekday` metadata.
    Use documented weekday spellings and its dedicated invalid-weekday
    contract; there is no date-part parameter. Include unqualified/ambiguous
    VALUE and both variable kinds, then full deep contexts with date outputs.
81. **Build/test PREVIOUS_DAY (sibling, S).** Reuse weekday-family construction
    but preserve its own invocation and direction. Include valid/invalid
    weekday, unqualified/ambiguous VALUE, both variable types, and no context
    matrix duplication. Do not label its weekday failure a DATEADD part error.
82. **Build/test DATE_BIN (primary, D).** Unique three-expression
    `stride/source/origin`, with no metadata part. Use interval, timestamp, and
    origin anchors; test each expression slot's collection and substitutions.
    Include separate source/origin ambiguity and a column-backed stride where
    accepted. Replicate deep contexts and preserve stride/source/origin order;
    no invalid-date-part test is applicable.
83. **Build/test AGE (primary, D).** Unique optional expression arity: one
    `value` or two `left/right` operands. Run simple construction/resolution
    cases for both overloads, and adapt each deep context category to both
    forms without an unnecessary Cartesian product. Assert interval-valued
    results, left/right order, omitted versus present fields, and ambiguity
    in each operand. No part metadata/part diagnostic applies.
84. **Build/test MAKE_DATE (primary, D).** Primary for three ordered numeric
    expression fields `year/month/day`. All are ordinary expressions, not
    date-part codes. Distribute qualified/unqualified/formula inputs and both
    substitution kinds across every field, including field-local ambiguity,
    then deep date-output context tests.
85. **Build/test MAKE_TIME (sibling, S).** Reuse three-numeric-field collection
    coverage with `hour/minute/second`, preserving different field names and
    supported second precision. Include unqualified/ambiguous input cases and
    both substitution kinds for each field. No invalid-part test or replicated
    deep placement matrix applies.
86. **Build/test MAKE_TIMESTAMP (primary, D).** Primary for six numeric
    expression fields `year/month/day/hour/minute/second`. Exercise every field
    with normal collection/substitutions and field-local ambiguity, then deep
    timestamp-output contexts. Do not assume three-field constructor tests
    prove the six-field AST and argument order.
87. **Build/test MAKE_TIMESTAMPTZ (sibling with unique optional tail, S).** Reuse
    six-field context coverage, preserving its own invocation. Add explicit
    simple cases for omitted/explicit timezone expression, both substitution
    kinds in that field, its qualified/unqualified columns, and ambiguity.
    This tail is an expression, not a date-part code; test it directly rather
    than duplicating every placement wrapper.
88. **Build/test MAKE_INTERVAL (primary, D).** Unique positional/named component
    signature. Cover full and partial supported forms, source-form retention,
    expression formulas, both variable types, and ambiguity in each populated
    component. Deep contexts must exercise both positional and named paths
    across the suite, with at least one of each in derived/CTE, DML, and set-op
    categories. Do not invent omitted components or confuse component names
    with metadata-valued date parts.
89. **Build/test JUSTIFY_INTERVAL (primary, D).** Primary for one ordinary
    interval expression `value`, without part metadata. Cover literals,
    columns, formulas, both substitutions, unqualified success and ambiguity,
    then interval-appropriate deep context wrappers.
90. **Build/test JUSTIFY_DAYS (sibling, S).** Reuse the one-interval-expression
    implementation/coverage with its own invocation; include unqualified/
    ambiguous VALUE and both variable types. No part diagnostic or placement
    replication is applicable.
91. **Build/test JUSTIFY_HOURS (sibling, S).** Same compact obligations as
    JUSTIFY_DAYS, with its own invocation and normal input dependencies.
92. **Build/test CLOCK_TIMESTAMP (zero-argument primary, D adapted as Z).**
    Build a no-argument datetime node with PostgreSQL affinity. Replicate deep
    contexts using a constant-like anchor, multiple independently aliased
    occurrences, and calls mixed with ordinary source columns. There are no
    argument columns, substitutions, parts, or ambiguous function inputs:
    explicitly mark those S/anchor cases inapplicable. Verify wrapper columns
    still resolve normally, but never claim they test clock-input ambiguity.
    Do not nest the function as its own argument. Group only where SQL permits;
    do not introduce a window OVER directly on a non-window clock function.
93. **Build/test STATEMENT_TIMESTAMP (zero-argument sibling, Z compact).**
    Assert invocation, no argument fields/source dependencies, independent
    output aliases, PostgreSQL affinity, and qualified same-name UDF behavior.
    Reuse CLOCK_TIMESTAMP placement proof without repeating its context suite.
94. **Build/test TRANSACTION_TIMESTAMP (zero-argument sibling, Z compact).**
    Apply the same simple no-argument construction/affinity controls with its
    own invocation; no invented invalid-part/unqualified-input cases.
95. **Build/test TIMEOFDAY (zero-argument sibling, Z compact).** Apply compact
    clock construction controls with its own invocation; its result is text,
    so any enclosing expression must be text-compatible. Retain a neutral
    CURRENT_TIMESTAMP comparison control without making it a new family member.
96. **Build shared signature/validation plumbing without sibling shortcuts.**
    Reuse the single registry and ordinary expression helpers, but keep
    per-invocation AST names, supported parts, optional fields, and affinity
    explicit. Run invalid-date-part FATAL cases for DATEADD, TIMEADD,
    TIMESTAMPADD, DATEDIFF, TIMEDIFF, TIMESTAMPDIFF, DATE_TRUNC, DATE_PART,
    EXTRACT, datetime TRUNC, TIME_SLICE, and LAST_DAY individually. Canonical
    existence alone is not family validity. A compact sibling test must fail
    if only that invocation is misrouted, swapped, or skips validation.
97. **Close each build/test action with observable evidence.** Record the
    function name, implemented signature/form variants, profile S/D/Z, actual
    tests run and pass/fail counts, and remaining gaps. Only run the current
    family class/methods during development. Review new AST/symbol/diagnostic
    goldens against the assertion contract, not just a successful test exit.
    Update consumers/generation instructions for each newly available signature
    and retain syntax forms; the extraction documentation describes the new
    AST directly without a generic-function migration narrative. Do not mark
    the whole datetime family complete because DATEADD passes.

### Delivery slices

| Slice | Scope | Current status |
|-------|-------|----------------|
| **A — lexer baseline** | In one batch add or confirm dedicated late lexer entries for every bare built-in needed by Slices B–E: add/diff aliases, trunc/slice/day-boundary names, `DATE_BIN`, `AGE`, `MAKE_*`, `JUSTIFY_*`, and timestamp clock functions. Do not add lexer tokens for part aliases or extensible parts. Regenerate and provide sorted token substitutions; golden token-ID edits are user-only (2026-11-11 clarification supersedes earlier bulk-update approval). Run the full suite after the user's edits. No later slice may change lexer numbering without separate review. | **Complete** — 26 late tokens; user-applied golden migration; configured JDK 21 suite 2,643 passed |
| **B — add family + registries** | Shared part/signature registries; `datetime_function`; `DATEADD`, `TIMEADD`, `TIMESTAMPADD`; core AST/symbol-table/dialect tests; q5866 clean with `year` absent from dictionaries and lineage | **Complete (Slice B scope)** — scenario-65 boundary delivered with explicit matrix accounting below; retain cross-family endpoint/generator closure under F |
| **C — difference + truncation** | `DATEDIFF`, `TIMEDIFF`, `TIMESTAMPDIFF`, shared `DATE_TRUNC`, datetime `TRUNC`; preserve neutral numeric `TRUNC` | **Complete** — DF catalog closed (`DF-01..DF-96`) and TE deep-regression closure delivered/passing |
| **D — remaining Snowflake** | `TIME_SLICE`, `LAST_DAY`, `NEXT_DAY`, `PREVIOUS_DAY` | **Complete** — BS-C/BS-M/BS-R matrix fully mapped (`BS-C01..BS-C24`, `BS-M01..BS-M20`, `BS-R01..BS-R36`) with passing coverage and explicit traceability |
| **E — PostgreSQL symmetry** | `DATE_BIN`, `AGE`, `MAKE_*`, `JUSTIFY_*`, timestamp clock functions, named `MAKE_INTERVAL`, shared/target part-affinity tests | **Deferred (post-5.1.4 release)** — parked intentionally; lexer baselines and planning catalogs retained |
| **F — delivery closure** | Generator support for `datetime_function`, `pss-parse-docs` / release notes, bound-query + snippet corpus comparison, full regression; generator follow-up may remain under **Entry 10** only if explicitly deferred | **Complete except deferred parts** — consumer docs updated for delivered datetime families; datetime generator support deferred to **Entry 10**; Slice E handoff deferred post-release |

**Story minimum:** Slices **A–B** + regression tests. **Full Entry 1:** A+B+C+D+E+F.

**Post-completion story comparison (2026-11-20):** the delivered slice
milestones remain historical evidence, not proof of complete Snowflake
part/signature coverage. [Entry 25](#entry-25--datetime-story-completion-and-function-specific-part-lists)
owns the subsequently verified TRUNCATE alias, supported-part, function-subset,
and shared-alias dialect-classification deficiencies. These are 5.1.4
remediations, separate from the intentionally deferred PostgreSQL expansion
and SQL-generator work.

#### Slice A matrix accounting (strict traceability, 2026-11-16)

| Slice A IDs | Coverage theme | Evidence |
|---|---|---|
| A-L01..A-L26 | Late lexer token additions for datetime families used by Slices B–E (add/diff/trunc/slice/day-boundary, `DATE_BIN`/`AGE`, `MAKE_*`, `JUSTIFY_*`, clock timestamp names) inserted before `Identifier` without reordering prior tokens | Lexer checkpoint + verification block below; focused suite `SqlDatetimeLexerBaselineTest`; full-suite gate recorded as 2,643/0 after user-applied token-ID golden edits |
| A-V01..A-V03 | Token-ID migration protocol and non-agent golden edits | Slice A checkpoint notes: descending old-ID replacement list, user-only edits, no automatic agent rewrite |
| A-V04..A-V06 | Build/regeneration and full-suite verification for the lexer batch | Slice A checkpoint verification paragraphs (ANTLR regeneration, focused tests, then full-suite pass after manual edits) |

Matrix result: **Slice A obligations are fully assigned and completed**.

#### Slice B matrix accounting (strict traceability, 2026-11-16)

Canonical Slice-B scenario boundary is **1..65** (the add-family closure scope).

| Slice B IDs | Coverage theme | Evidence tests / artifacts |
|---|---|---|
| B-01..B-04 | Add-family grammar routing and named datetime AST shaping for `DATEADD`/`TIMEADD`/`TIMESTAMPADD` | `SqlEventWalkerDatetimeFunctionPlacementTests`: `dateaddHasNamedArgumentsAndPartMetadata`, `timeaddAndTimestampaddRetainTheirOwnInvocationsAndExpressionArguments` |
| B-05 | 24-shape argument matrix for increment/value source forms | `SqlEventWalkerDatetimeFunctionPlacementTests`: `dateaddMatrix*` method series |
| B-06..B-15 | Canonical/alias level normalization and authored spelling/form retention | `SqlEventWalkerDatetimeFunctionPlacementTests`: registry and spelling tests (`dateaddRegistryMatchesSnowflakeDocumentedPartsAndAliases`, `dateaddAcceptsEvery*Spelling`, quoted/case controls) |
| B-16..B-33 | Ambiguity, visibility, derived/CTE/scalar/correlated propagation and join contexts | `SqlEventWalkerDatetimeFunctionPlacementTests`: `dateaddReportsAmbiguous*`, `dateaddResolvesUnique*`, `dateaddConsumes*`, `dateadd*Scalar*`, `dateadd*Cte*` |
| B-34..B-47 | Nested/sibling behavior, substitution typing, hidden-source and join-extension deferral controls | `SqlEventWalkerDatetimeFunctionPlacementTests`: nested DATEADD controls + substitution matrix + join-extension fatal/defer tests |
| B-48 | Add-family dialect-affinity and mixed-affinity controls | `SqlEventWalkerDatetimeFunctionPlacementTests` add-family affinity/script diagnostics; q5866 checkpoint notes |
| B-49..B-65 | Clause/DML/set-op/script placement and statement reset isolation | `SqlEventWalkerDatetimeFunctionPlacementTests`: WHERE/HAVING/ORDER/WINDOW, INSERT/UPDATE/DELETE, set-op, SCRIPT reset tests |
| B-SNIP-01..B-SNIP-12 | Add-family snippet endpoint and substitution coverage | `SqlEventWalkerDatetimeFunctionSnippetTests` (DATEADD/TIMEADD/TIMESTAMPADD coverage documented in checkpoint text) |

Matrix result: **B-01..B-65 are fully assigned and implemented for Slice B scope**.

#### Slice C matrix accounting (strict traceability, 2026-11-16)

Slice C consists of the diff catalog plus trunc/extract catalog closure.

| Slice C IDs | Coverage theme | Evidence tests |
|---|---|---|
| DF-01..DF-12 | Diff signature and level metadata controls | `SqlEventWalkerDatetimeFunctionPlacementTests` (Section A closure) |
| DF-13..DF-37 | 25-case left/right operand-shape matrix | `SqlEventWalkerDatetimeFunctionPlacementTests` (Section B closure) |
| DF-38..DF-49 | Ambiguity and visibility failure behavior | `SqlEventWalkerDatetimeFunctionPlacementTests` (Section C closure) |
| DF-50..DF-67 | Substitution matrix in left/right trees and join-extension behavior | `SqlEventWalkerDatetimeFunctionPlacementTests` (Section D closure) |
| DF-68..DF-76 | Nested/sibling isolation for diff family | `SqlEventWalkerDatetimeFunctionPlacementTests` (Section E closure) |
| DF-77..DF-86 | Producer/consumer and scope propagation | `SqlEventWalkerDatetimeFunctionPlacementTests` (Section F closure) |
| DF-87..DF-94 | Clause/DML/set-op placement | `SqlEventWalkerDatetimeFunctionPlacementTests` (Section G closure) |
| DF-95..DF-96 | UDF controls and SCRIPT reset/isolation | `SqlEventWalkerDatetimeFunctionPlacementTests` (Section H closure) |
| TE-C01..TE-C28 | TRUNC/EXTRACT core functionality matrix | `SqlEventWalkerDatetimeTruncExtractTests` core coverage block |
| TE-M01..TE-M24 | TRUNC/EXTRACT malformed and diagnostic matrix | `SqlEventWalkerDatetimeTruncExtractTests` malformed block |
| TE-R01..TE-R44 | TRUNC/EXTRACT deep regression (nested/sibling, set-op, rename propagation, DML, UDF/reset/mixed-affinity) | `SqlEventWalkerDatetimeTruncExtractTests` deep-regression additions and pass runs |
| C-SNIP-01..C-SNIP-12 | TRUNC/EXTRACT and diff snippet endpoint/substitution coverage | `SqlEventWalkerDatetimeFunctionSnippetTests` + diff snippet checkpoint entries |

Matrix result: **DF-01..DF-96 and TE coverage obligations are fully assigned and implemented**.

**Phase 1 / Slice A complete (2026-11-11):** Added 26 late date/time function
tokens, `DATEADD` through `TIMEOFDAY`, immediately before `Identifier`. Existing
token IDs 1–392 remain unchanged; former IDs 393–413 shift by +26. Supply the
replacement list in descending old-ID order, matching exact `<nnn>` strings.
Use generated lexer constant snapshots (before/after
`parse/target/generated-sources/antlr4/sql/SQLSelectParserLexer.java`) rather
than runTests mismatch output to build the full replacement set:
`python3 tools/token_id_worklist_from_vocab.py <old.java> <new.java>`.
No existing golden was edited. Part aliases remain identifiers, and the new
function tokens remain eligible for generic/qualified UDF calls until dedicated
date/time productions are implemented.

With user approval, the same checkpoint included the narrow Entry 18 parser-rule
fixes: existing `POSITION`, `INPUT`, and `PATH` tokens are non-reserved identifiers;
existing `TRUNCATE` is allowed as a generic function name. Their lexer declarations
were not moved or duplicated, so they introduce no additional token-ID changes.
`position_function` precedes `routine_invocation` to preserve the existing
POSITION comma/IN AST path.

Verification: ANTLR regeneration, main/test compilation, and the five tests in
`SqlDatetimeLexerBaselineTest` pass via
`mvn -pl parse -Dmaven.compiler.fork=true -Dtest=SqlDatetimeLexerBaselineTest test`.
Initial builds encountered IDE-written unresolved-ANTLR error classes that Maven
considered current; invalidating only
`parse/target/maven-status/maven-compiler-plugin/compile/default-compile/inputFiles.lst`
forced a successful javac rebuild. Java auto-build was temporarily disabled with
permission and restored. Subsequent IDE output interference is avoided with the
opt-in `isolated-build` Maven profile (`target-maven/` output); Java auto-build
remains enabled. Regeneration, compilation, and the five focused baseline tests
are verified. The earlier isolated full suite ran 2,199 tests: 1,680 passed, 498
failed on remaining `<393>` versus `<452>` expectations, 0 errors, 21 skipped.
After the user's manual golden edits, the full workspace suite was run through
the VS Code testing extension using its configured JDK 21 execution settings:
**2,643 passed, 0 failed**. This satisfies the Phase 1 / Slice A full-suite gate;
the counts come from a different runner than the earlier Maven run. No golden
edits were made by the agent. This paragraph records the Slice A checkpoint;
subsequent checkpoints below supersede its then-current implementation state:
add, extraction, diff/trunc, and boundary/slice grammar/walker work has since
been delivered as described in the slice-status table.

### Acceptance

**Slice B grammar-only inspection checkpoint (2026-11-11):** Added
`datetime_function`, the shared add-family expression/name rules, and the shared
`datetime_part` / `snowflake_datetime_part` rules. `extract_field` remains a
compatibility wrapper. All five new walker exit overrides are intentionally
empty for raw-AST inspection; no signature/part registry, AST shaping, or new
affinity handling is implemented yet. JDK 21 isolated-build verification passed
91 focused grammar/EXTRACT tests, with lexer declaration order unchanged.
An ordinary JDK 21 run of
`select dateadd(year, 1, a.created_at) as shifted from events a`
reported zero syntax errors/FATALs and retained numbered rule maps and `Type`
markers, as expected before shaping. This does not complete Slice B or establish
its final symbol-table/substitution/affinity contract.

**DATEADD AST checkpoint (2026-11-12):** DATEADD now emits
`datetime_function={function_name=DATEADD, level={canonical,text,form},
increment=..., value=...}` without numbered rule wrappers or `Type` markers.
`DatetimePartRegistry` canonicalizes documented Snowflake DATEADD aliases while
preserving authored spelling and part form; unknown/extensible text retains its
uppercased canonical text, with family-validity checks still deferred. Nested
DATEADD, ordinary expression operands, predicand substitutions, and qualified
generic UDFs have focused AST tests. At that checkpoint TIMEADD/TIMESTAMPADD
shaping, signature validation, and new dialect-affinity handling remained unfinished. Verification
on JDK 21: 96 focused tests passed; isolated full parser suite ran 2,207 tests
with 0 failures, 0 errors, 21 skipped. No existing goldens were edited.

**Add-family AST follow-up (2026-11-12):** TIMEADD and TIMESTAMPADD now use
the same named argument shaping and registered-part validation as DATEADD,
while retaining their own invocation names. One focused query tests both
aliases, qualified expression arguments, and their full output goldens; a
second focused test covers each alias's invalid-part FATAL. Type-dependent
unit restrictions, dialect affinity, complete sibling mini-suites, and
snippet permutations remain pending.

**DATEADD scenario progress (2026-11-12):** Placement tests now include
scenarios 1-33, including 32a, plus portions of 47. Scenario 5 has all 24 argument-shape combinations except literal/literal,
each with six literal output goldens. The remaining substitution permutations,
scope/clause/DML/set-operation matrix, and affinity/reset coverage are not yet
complete. Scenario 18 compares the registry against the Snowflake DATEADD
parts table and tests all 62 canonical and alias spellings in 11 full-golden
parser batches. Scenario 48 depends on DATEADD dialect affinity, which is not yet
implemented. Positive cases use six literal output goldens; ambiguity cases
assert the existing source-position diagnostics.
Scenario 16 includes mixed variable kinds alongside ordinary columns in each
argument independently and in both together. Scenario 19 includes quoted
canonical and alias spellings as well as unquoted forms.
Scenarios 20-28 cover level-like columns, invalid and nested levels, numeric
expressions, repeated inputs, ordinary-function nesting, nested DATEADD, and
CASE. The `epoch_second` control is accepted by EXTRACT with the existing
Snowflake dialect warning and rejected as a DATEADD part.
Scenario 29 covers all three DATEADD anchors as derived-table producers,
including child-to-parent output lineage and the joined anchor's filter inputs.
Scenario 30 covers DATEADD consuming arguments through one derived table and
two renamed layers, with fatal diagnostics for hidden leaf names in either slot.
Scenario 31 covers the three anchor shapes in joined SELECT and ON expressions,
and in derived tables on both sides of INNER and LEFT JOINs; output and filter
dependencies are asserted separately.
Scenario 32 covers DATEADD inside a single-row scalar SELECT and uncorrelated
scalar aggregates supplying INCREMENT and VALUE independently. These two
argument tests are also the uncorrelated controls for scenario 32a, which adds
correlated scalar aggregates independently in each argument and in both together.
Their local inputs, outer references, filters, and dependent-query links are
asserted in their respective scopes.
Scenario 33 covers the three anchor shapes as CTE producers, DATEADD over raw
CTE exports, and a renamed CTE chain with ordered visibility and lineage.
Nested qualified column substitutions in both DATEADD formulas are also
covered with missing source aliases: they remain fatal without a join extension
and become severe warnings with a required-extension warning when one is present.
The unknown-table-alias control currently reports the SELECT output alias
position rather than the offending qualifier; an ordinary column reference
shows the same existing diagnostic behavior.

Structured AST (not `parameters.1.column` for parts); `TIMESTAMPADD` parses; fec/`year` CASE and q5866 are clean; no date/time part appears as a physical/query column or unresolved reference; EXTRACT/DATE_PART unified under the approved Phase 1 action with syntax-preserving datetime-family ASTs, retained source semantics, and documented consumer changes; every target-only function/part/construct in the matrices raises the matching statement dialect flag; shared forms raise neither; mixed affinity remains FATAL per statement; bound-query and snippet extract re-check is documented with no unexplained regressions; 5.1.4 jar + grammar extension doc.

### Snowflake reference

[DATEADD](https://docs.snowflake.com/en/sql-reference/functions/dateadd), [TIMEADD](https://docs.snowflake.com/en/sql-reference/functions/timeadd), [TIMESTAMPADD](https://docs.snowflake.com/en/sql-reference/functions/timestampadd), [DATEDIFF](https://docs.snowflake.com/en/sql-reference/functions/datediff), [DATE_TRUNC](https://docs.snowflake.com/en/sql-reference/functions/date_trunc), [TIME_SLICE](https://docs.snowflake.com/en/sql-reference/functions/time_slice), etc.

---

## Entry 2 — Snowflake PIVOT quoted unaliased identifiers

**Status:** Complete (closed 2026-11-17)  
**Spec (do not duplicate here):** [snowflake-pivot-quoted-identifier-parser-brief.md](./snowflake-pivot-quoted-identifier-parser-brief.md)

**Summary:** Lexer/parser must treat older unaliased PIVOT output names as delimited identifiers (`"'diq_entry_year'"`, numeric `"1"`…). Register IN-list values in `derivation.source_columns`; resolve as column refs. Newer `IN ('x' AS alias)` must keep working.

**Acceptance:** Both fixtures in the brief parse with no FATAL; derivation and location refs correct.

### Stage progress

- **Stage A (complete):** Added targeted capture/guard tests in
  `SqlEventWalkerPivotUnpivotTests` to pin legacy quoted-selector behavior and
  protect modern `IN (... AS alias)` form.
- **Stage B (complete):** Expanded lexer support for double-quoted identifiers
  so legacy selector spellings such as `"'diq_entry_year'"` parse as normal
  identifier references in SELECT/WHERE.
- **Stage C (complete):** Added mixed alias/unaliased PIVOT IN acceptance
  coverage to confirm side-by-side resolution (`entry_year` + legacy quoted
  selector) without regressions.
- **Stage D (complete):** Re-scanned copied live-query fixtures under
  `parse/testdata/live_queries/` and confirmed the historical quoted-selector
  parser-recovery fatal pattern is no longer present.

### Validation evidence

- `mvn -pl parse clean -Dtest=sql.walker.SqlEventWalkerPivotUnpivotTests#pivotQuotedUnaliasedSelectorWithoutAsResolvedStageBTest test`
- `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerPivotUnpivotTests#pivotQuotedLegacyIdentifierWithOuterAliasResolvedStageBTest,sql.walker.SqlEventWalkerPivotUnpivotTests#pivotInListAliasedFormNoRegressionGuardStageATest,sql.walker.SqlEventWalkerPivotUnpivotTests#pivotQuotedUnaliasedSelectorWithoutAsResolvedStageBTest,sql.walker.SqlEventWalkerPivotUnpivotTests#pivotMixedInAliasAndLegacyQuotedSelectorResolutionStageCTest test`
- `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerPivotUnpivotTests test`
- `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerSetOpScopingGateTests,sql.walker.SqlEventWalkerSetOpQueryVariableTests test`
- `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerLiveSampleQueriesTests#liveQueryPotentialStudentByEntryTermSurveyResponseHistoryNoLegacyPivotFatalRecoveryTest,sql.walker.SqlEventWalkerLiveSampleQueriesTests#liveQueryPotentialStudentByEntryTermNoSurveyNoLegacyPivotFatalRecoveryTest test`

---

## Entry 3 — Snowflake `PARSE_*` and `:` field access

**Status:** Complete (closed 2026-11-17)

**Problem:** `PARSE_URL(url, 1):scheme::varchar` — colon path after function call is not accepted (`NoViableAltException` on `:`). Same pattern for other semi-structured PARSE results.

**Scope minimum:** `PARSE_URL`; extend to `PARSE_JSON`, `PARSE_XML`, `PARSE_IP`, `TRY_PARSE_JSON` if grammar construction is shared.

**Work:** Grammar for post-call `:` paths (distinct from `::` cast and bind variables); walker AST; column resolution on function arguments; exemplar predicand + SELECT tests.

**Repro / detail:** git history — former `parser-defects-enhancements-workplan.md` Phase 3.

### Entry 3 staged execution plan (A-D)

| Stage | Scope | Exit gate |
|------|-------|-----------|
| **A — repro + guards** | Add failing repro tests for `PARSE_URL(url, 1):scheme::varchar` plus controls for existing `::` cast behavior and colon-adjacent non-target forms. | Repro fails for the right reason; controls remain stable. |
| **B — grammar acceptance** | Minimal parser production changes to allow post-call `:field` access while preserving `::` cast precedence. Avoid new lexer keywords unless evidence forces it. | Repro parse succeeds with no parser/listener fatal diagnostics; cast controls still pass. |
| **C — walker/AST + resolution** | Wire AST/walker handling for post-call field access and confirm function-argument column collection/resolution/diagnostics stay on normal paths. | No walker fatal diagnostics; unresolved/ambiguous controls produce expected diagnostics. |
| **D — family expansion + closeout** | Expand shared coverage across `PARSE_JSON`, `PARSE_XML`, `PARSE_IP`, `TRY_PARSE_JSON` where grammar path is shared; add placement coverage and non-regression bundle. | Focused suite + guard bundle green; Entry 3 acceptance evidence recorded for closure. |

### Stage progress

- **Stage A (complete):** Added a failing repro capture for
  `PARSE_URL(t.url, 1):scheme::varchar` plus a cast-only control guard
  (`PARSE_URL(t.url, 1)::varchar`) in `SqlEventWalkerTableFunctionTests`.
- **Stage B (complete):** Added parser support for post-call `:field` suffixes
  (`value_expression_primary_suffix`) and promoted the Stage A repro to a
  passing acceptance case while preserving cast guard behavior.
- **Stage C (complete):** Added explicit walker handling for post-call JSON-path
  suffixes so `PARSE_URL(...):scheme::varchar` produces a stable AST shape
  (`json_path=scheme` on the function output) while preserving downstream cast
  semantics and existing colon-notation table-function behavior.
- **Stage D (complete):** Expanded shared `PARSE_*` family coverage for
  `PARSE_JSON`, `PARSE_XML`, `PARSE_IP`, and `TRY_PARSE_JSON` across
  representative placements (SELECT, WHERE, JOIN ON), then added placement
  parity expansion coverage (`GROUP BY`, `HAVING`, `ORDER BY`, `UPDATE SET`)
  with strict full-golden extractor assertions and positional diagnostics for
  ambiguous/unresolved argument resolution.

### Stage validation evidence

- `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerTableFunctionTests#parseUrlPostCallFieldAccessResolvedStageBTest,sql.walker.SqlEventWalkerTableFunctionTests#parseUrlCastWithoutPostCallFieldAccessGuardStageATest test`
- `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerTableFunctionTests#parseUrlPostCallFieldAccessResolvedStageBTest,sql.walker.SqlEventWalkerTableFunctionTests#parseUrlCastWithoutPostCallFieldAccessGuardStageATest test`
- `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerTableFunctionTests#parseUrlPostCallFieldAccessResolvedStageBTest,sql.walker.SqlEventWalkerTableFunctionTests#parseUrlCastWithoutPostCallFieldAccessGuardStageATest,sql.walker.SqlEventWalkerTableFunctionTests#snowflakeColonNotationSelectPathV1Test,sql.walker.SqlEventWalkerTableFunctionTests#snowflakeColonNotationNestedFlattenInputV2Test test`
- `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerTableFunctionTests,sql.walker.SqlEventWalkerCastingAndTypesTests test`
- `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerTableFunctionTests#parseJsonPostCallFieldAccessSelectStageDTest,sql.walker.SqlEventWalkerTableFunctionTests#parseXmlPostCallFieldAccessSelectStageDTest,sql.walker.SqlEventWalkerTableFunctionTests#parseIpPostCallFieldAccessWhereStageDTest,sql.walker.SqlEventWalkerTableFunctionTests#tryParseJsonPostCallFieldAccessJoinOnStageDTest,sql.walker.SqlEventWalkerTableFunctionTests#parseUrlPostCallFieldAccessResolvedStageBTest test`
- `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerTableFunctionTests#parseUrlPostCallFieldAccessResolvedStageBTest,sql.walker.SqlEventWalkerTableFunctionTests#parseUrlCastWithoutPostCallFieldAccessGuardStageATest,sql.walker.SqlEventWalkerTableFunctionTests#parseJsonPostCallFieldAccessSelectStageDTest,sql.walker.SqlEventWalkerTableFunctionTests#parseXmlPostCallFieldAccessSelectStageDTest,sql.walker.SqlEventWalkerTableFunctionTests#parseIpPostCallFieldAccessWhereStageDTest,sql.walker.SqlEventWalkerTableFunctionTests#tryParseJsonPostCallFieldAccessJoinOnStageDTest,sql.walker.SqlEventWalkerTableFunctionTests#parseUrlPostCallFieldAccessAmbiguousArgumentColumnDiagnosticByPositionTest,sql.walker.SqlEventWalkerTableFunctionTests#parseUrlPostCallFieldAccessHasNoSnowflakeDialectGrammarWarningTest,sql.walker.SqlEventWalkerTableFunctionTests#parseUrlPostCallFieldAccessOrderByPlacementParityExpansionTest,sql.walker.SqlEventWalkerTableFunctionTests#parseUrlPostCallFieldAccessGroupByPlacementParityExpansionTest,sql.walker.SqlEventWalkerTableFunctionTests#parseUrlPostCallFieldAccessHavingPlacementParityExpansionTest,sql.walker.SqlEventWalkerTableFunctionTests#parseUrlPostCallFieldAccessHavingSubstitutionParityExpansionTest,sql.walker.SqlEventWalkerTableFunctionTests#parseUrlPostCallFieldAccessUpdateSetPlacementParityExpansionTest test`

---

## Entry 4 — Snowflake ARRAY syntax and functions

**Status:** Complete (2026-11-19) — all 42 in-scope names and Slices A–F
closed. `LATERAL_FLATTEN` compatibility and generator expansion remain
explicitly deferred.

### Slice A implementation decision (2026-11-17)

- Implement Slice A with **lexer-constrained array-family names** plus parser/walker
  updates: dedicated ARRAY-family lexer tokens are retained, and grammar paths are
  wired through those tokens rather than broad identifier-only assumptions.
- Treat array literal support (`[expr, ...]`) as expression-grammar work with
  strict diagnostic controls and golden extractor assertions, while preserving
  subscript behavior (`expr[index]`) as a non-regression invariant.

### Entry 4 staged execution slices (A-F)

| Slice | Scope | Target subtasks | Exit gate |
|------|-------|------------------|-----------|
| **A — constructors and literal forms** | Close `ARRAY_CONSTRUCT` parity + array literal syntax completion | **4.2**, **4.4** | Placement + endpoint parity matrix passes; malformed/syntax controls green; no regressions in subscript behavior |
| **B — ordered/union aggregations** | Consolidate aggregate family behavior and lineage | **4.3** (complete), **4.6** | `WITHIN GROUP`/`OVER` lineage matrix complete; aggregate family diagnostics + set-op integration controls pass |
| **C — mutation/set operations** | Binary/set operators + mutator family | **4.7**, **4.8** | Function/operator signature matrix + column resolution + dialect diagnostics complete |
| **D — consolidated all-array function closure — COMPLETE** | Original transforms/conversions plus every remaining array function; completed A/B/C evidence carried forward | **4.1–4.12**, GC1..GC4, T023/T024 and T025..T034 complete | All 42 names accounted for; accepted family coverage retained; final default run: 4,244 passed, 62 skipped, zero failures/errors, exit 0 |
| **E — former table/higher-order owner** | Function implementation and acceptance transferred into D, not deferred or duplicated | **4.5** -> T027/T028; **4.12** -> T029/T030/T031 | Satisfied by the corresponding D tasks and expanded audit, not by the ownership transfer itself |
| **F — delivery closure — COMPLETE** | Corpus + snippet replay, docs and regression-gate verification | Entry 4 delivery closeout below; generator expansion stays in Entry 10 | Existing T023/T024 evidence accepted; no duplicate execution |

**Story minimum for Entry 4:** Slices **A–C**.  
**Full Entry 4 closure:** A+B+C+D+E+F.
Under the post-T022 amendment, D includes E's function requirements and verifies
the A/B/C carry-forward evidence.

**Slice F delivery closeout (2026-11-19):** the
[frozen T023/T024 sign-off](slice-d-array-function-reconciliation.md#t023t024-final-slice-d-sign-off-2026-11-19)
also supplies the non-generator delivery gate. That execution passed 42
live/sample-query cases (three previously parked samples skipped), all 143
non-SQL endpoint cases and the array/snippet/aggregate/pivot source suites.
T032–T034 and T024 supply completed matrix/accounting documentation.
No duplicate replay or full-suite run was required. This accepts the existing
default regression/sample corpus; it does not claim that separate opt-in
slow bound-query regressions or an external frozen CSV corpus were rerun.
Whole-release opt-in checks and artifact packaging remain separate.

### Slice D safe implementation plan (execution order)

Use bounded batches and close each before moving forward. The original D1/D2/D3
batches below are historical implementation records; the **expanded remaining
implementation sequence** below them governs all next steps.

**Current completeness correction (2026-11-18):** use the
[function reconciliation and explicit remaining-task checklist](slice-d-array-function-reconciliation.md).
The historical D2.5/D3.7 green class runs did not establish complete independent
matrices for all earlier functions. D1 and D2.6 are now complete; D3.9 remains
required remediation. The user approved D3.8's four additions on 2026-11-18 and requested
another lexer expansion first. The approved D0 batch adds `SPLIT`; `TO_ARRAY`,
`AS_ARRAY`, and `IS_ARRAY` already have tokens and must not be duplicated.

1. **D1 (4.9 transforms, first):**
   - Multi-arg transform set for 4.9 is explicitly: `ARRAY_TO_STRING`, `ARRAY_SLICE`, `ARRAY_REPEAT`.
   - **D1.1 / GC2 (`ARRAY_REPEAT`) — complete (2026-11-18):** 48 strict cases, including 45 six-output goldens and three parser-only syntax controls; exact diagnostics in every case. Focused host: 304/304, zero failures/errors/skips. No production or lexer changes were needed.
   - **D1.2 — complete:** `ARRAY_TO_STRING` has 45 independent cases; `ARRAY_SLICE` has 55. All retained methods now compare complete exact diagnostics without changing their extractor goldens. Focused host: 377/377. Only `ARRAY_TO_STRING` needed a parser nonreserved-name fix; neither batch changed lexer IDs.
   - Add dedicated grammar + walker routing using existing value-expression argument resolution.
   - Add strict full-golden tests with positional diagnostics, unresolved/missing-alias by eligible argument position, and substitution-variable typing checks (including qualified column-variable typing).
2. **D2 (4.10 unary transforms):**
   - Add unary array transform family routing and full matrix tests.
   - Include unresolved/ambiguous and substitution typing controls for each eligible argument position.
   - Safe bounded sequence (execute in order; run focused validation at each step, defer full ladder to Slice D final sign-off):
     1. **D2.1 seed route + seed tests (`ARRAY_DISTINCT`)**
        - Add dedicated unary grammar/walker route for one-arg transform shape.
        - Add strict seed tests: SELECT happy path + missing-alias fatal + full extractor goldens.
        - Run focused transform suite and patch exact literal/token-position goldens.
     2. **D2.2 route expansion (`ARRAY_COMPACT`, `ARRAY_SIZE`)**
        - Extend unary name set in grammar route.
        - Add strict per-function core matrix (SELECT/WHERE/ORDER BY/VALUES/INSERT/UPDATE/DELETE as applicable).
     3. **D2.3 route expansion (`ARRAY_SORT`, `ARRAY_MAX`, `ARRAY_MIN`) — complete (2026-11-18)**
        - Extend unary name set and complete per-function strict matrix.
        - Add unresolved/ambiguous/missing-alias controls and substitution typing checks.
        - Completed with 78 new strict tests (28 sort, 25 max, 25 min); focused transform suite: 162 passed, zero failures/errors/skips. See the D2.3 completion record below.
     4. **D2.4 GC2 unary closure (`ARRAY_REVERSE`, `ARRAY_SUM`) — complete (2026-11-18)**
        - After lexer/token readiness, extend unary route and complete strict per-function matrix.
        - Record explicit GC2 completion evidence in Entry 4 accounting.
        - Completed with 54 new strict tests (27 per function); focused transform suite: 216 passed, zero failures/errors/skips. This closes the GC2 unary portion only; `ARRAY_REPEAT` remains open under 4.9.
     5. **D2.5 D2 focused closeout — complete (2026-11-18)**
        - Focused unary transform suite and strict-golden reconciliation only.
        - Do not run Phase15/Smoketest/full parser ladder here; defer to Slice D final sign-off.
        - Fresh Maven and VS Code focused runs both passed 216/216; no golden reconciliation or additional production changes were needed.
3. **D3 (4.11 conversion/predicate/split-to-array):**
   - Lock exact target function set, then implement signature-specific routing and tests.
   - Safe bounded sequence (execute in order; run focused validation at each step, defer full ladder to Slice D final sign-off):
     1. **D3.1 target-set lock + signature map (`SPLIT_TO_ARRAY`, `STRTOK_TO_ARRAY`, `ARRAY_GENERATE_RANGE`, `ARRAY_POSITION`, `ARRAYS_ZIP`, `ARRAYS_TO_OBJECT`) — complete (2026-11-17)**
        - Exact 4.11 target list is frozen in this workplan/accounting with no placeholder terms.
        - Signature-family map locked before code changes:
          - conversion: `SPLIT_TO_ARRAY`, `STRTOK_TO_ARRAY`
          - range: `ARRAY_GENERATE_RANGE`
          - position/predicate: `ARRAY_POSITION`
          - zip: `ARRAYS_ZIP`
          - object conversion (GC3): `ARRAYS_TO_OBJECT`
     2. **D3.2 seed grammar routing by signature family — complete (2026-11-17)**
        - Added minimal dedicated grammar routes for representative functions by family:
          - conversion: `SPLIT_TO_ARRAY`
          - range: `ARRAY_GENERATE_RANGE`
          - position/predicate: `ARRAY_POSITION`
          - zip: `ARRAYS_ZIP`
        - Kept routing explicit with narrow seed rules and no broad grammar refactor; object conversion (`ARRAYS_TO_OBJECT`) was initially deferred to GC3 and is now completed under D3.6 below.
     3. **D3.3 seed walker handling by signature family — complete (2026-11-17)**
        - Added signature-specific walker handling for seeded conversion/range/position/zip routes.
        - Reused existing function-map + sql-argument handling shape and Snowflake dialect affinity recording path.
     4. **D3.4 strict seed tests per family — complete (2026-11-17)**
        - Added strict seed tests for representative conversion/range/position/zip routes (`SPLIT_TO_ARRAY`, `ARRAY_GENERATE_RANGE`, `ARRAY_POSITION`, `ARRAYS_ZIP`).
        - Added missing-alias fatal controls for eligible argument positions and reconciled full extractor goldens + positional diagnostics.
     5. **D3.5 per-function matrix expansion — complete (2026-11-17)**
        - Expand to full per-function placement matrix (`SELECT`/`WHERE`/`ORDER BY`/`VALUES`/`INSERT`/`UPDATE`/`DELETE` as applicable).
        - Add unresolved/ambiguous/missing-alias and substitution-variable typing controls for eligible argument positions.
        - Completed strict placement matrix for `SPLIT_TO_ARRAY`, `ARRAY_POSITION`, `ARRAY_GENERATE_RANGE`, `ARRAYS_ZIP`, and `STRTOK_TO_ARRAY` (`WHERE`/`ORDER BY`/`VALUES`/`INSERT`/`UPDATE`/`DELETE`) with full literal goldens and positional diagnostics.
        - Focused validation green: `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerSnowflakeArrayTransformTests test`.
     6. **D3.6 GC3 explicit closure (`ARRAYS_TO_OBJECT`) — complete (2026-11-18)**
        - Complete `ARRAYS_TO_OBJECT` routing/tests explicitly (no implicit carryover).
        - Record explicit GC3 completion evidence in Entry 4 accounting.
        - Routed explicitly through the existing conversion-family grammar/walker and added 40 cases: 38 full-golden extractor tests and two parser-only malformed-argument syntax controls. Focused Maven and VS Code runs pass 256/256. D3.7 focused closeout is recorded below.
     7. **D3.7 D3 focused closeout — complete (2026-11-18)**
        - Run focused D3 family suites and reconcile strict literal/token-position goldens.
        - Do not run Phase15/Smoketest/full parser ladder here; defer to Slice D final sign-off.
        - Fresh Maven and VS Code runs of `SqlEventWalkerSnowflakeArrayTransformTests`, covering all six D3 functions, pass 256/256. Existing full literal goldens and positioned diagnostics pass unchanged; no reconciliation or production-code changes were required.

#### Slice D expanded remaining implementation sequence (approved 2026-11-18)

This is a plan, not an assertion that the following work has executed. Keep the
master plan here; do not create a competing modernization plan or change the
historical completion records into current sign-off claims.

**Scope amendment after T022:** the user requires every remaining planned
array function to close under D, with `ARRAY_CONSTRUCT_STRUCTURED` next.
The [42-name inventory](slice-d-array-function-reconciliation.md#expanded-all-array-closure-inventory)
is the exhaustive planning-document ledger. T025..T034 below are inserted
**before** T023/T024 without renumbering completed task history. Historical
"next" pointers and the original 21-name scope below do not override this order.

**Original audited scope (21 names, retained completion evidence):**

| Area | Exact functions | Remaining execution owner |
|---|---|---|
| 4.9 transforms (3) | `ARRAY_TO_STRING`, `ARRAY_SLICE`, `ARRAY_REPEAT` | D1, D-SIG, D-AUDIT and T023/T024 complete |
| 4.10 unary (8) | `ARRAY_DISTINCT`, `ARRAY_COMPACT`, `ARRAY_SIZE`, `ARRAY_SORT`, `ARRAY_MAX`, `ARRAY_MIN`, `ARRAY_REVERSE`, `ARRAY_SUM` | All eight matrices, including SORT flags, and T023/T024 complete |
| 4.11 conversion/predicate/split/range/position/zip (10) | `SPLIT_TO_ARRAY`, `STRTOK_TO_ARRAY`, `ARRAY_GENERATE_RANGE`, `ARRAY_POSITION`, `ARRAYS_ZIP`, `ARRAYS_TO_OBJECT`, `TO_ARRAY`, `AS_ARRAY`, `IS_ARRAY`, `SPLIT` | All ten matrices, original audits and T023/T024 complete; ARRAYS_TO_OBJECT operand controls and ZIP's fourth operand retained |

**Implementation conventions and source anchors:**
- Module: `parse`; ANTLR grammar: `parse/src/main/antlr4/sql/SQLSelectParser.g4`;
  shared walker: `parse/src/main/java/sql/walker/SqlParseEventWalker.java`;
  affinity registry: `parse/src/main/java/sql/grammar/SnowflakeRoutineDialectRegistry.java`;
  focused host: `parse/src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java`.
- Reuse existing argument stamping, function-map output and dialect helpers.
  Extend the appropriate conversion/predicate/split route explicitly rather
  than introducing duplicate walkers or relying solely on generic parsing.
- Read the parser consumer docs before changing bucket/lineage expectations.
  No Panto substitution redesign, dependency upgrade, new test framework, or
  migration transformation is part of this plan.
- Preserve `SPLIT_TO_ARRAY` and its current behavior/diagnostics for compatibility.
  Add documented Snowflake `SPLIT` separately; do not rename, delete, or silently
  reclassify the existing spelling. Document that compatibility choice.
- `ARRAY_CONSTRUCT_STRUCTURED` is D5.1 / T025 / GC1, not an excluded Slice A
  follow-up. Its expanded lexical/manual-repair gate and independent scalar
  matrix are complete. Old D0 snapshots describe the previous SPLIT addition;
  the later official D5 lexer images remain unchanged by scalar closure.

**Dependencies:** D0.1 -> D0.2 -> D0.3 -> D0.4 -> D0.5 is a hard prerequisite
chain. D0.5 blocks every matrix/implementation task below. After that gate,
execute the per-function rows in the displayed order, validating each before
moving to the next. All rows edit the same focused host; do not parallel-edit it.
The original D-SIG and D-AUDIT retain their 21-name scope. **T025–T034 are
complete**, including the single shared consolidation and user-approved
carry-forward family coverage. **T023/T024 are also complete:** one full default
accuracy run passed and its final status/accounting is recorded below.
Task IDs preserve traceability; they do not require separate passes.
Reuse existing named-method ledgers and passing batch results; inspect only
missing or contradictory evidence, without reopening completed matrices.
T025 includes a hard developer
manual-token-repair gate before new matrices can be completed; that gate is
developer-confirmed complete as of 2026-11-19. T032/T033/T034 closed together
from the shared evidence; T023 follows that consolidation, and T024 records
its successful result once.

##### Sequential prompt checklist for completing Slice D

This is the prompt-friendly index of T006..T034 below, not additional scope.
Keep each checkbox aligned with its corresponding detailed task and completion
ledger. Complete and validate one function before proceeding to the next.

**Completed setup:** the SPLIT lexer addition and replacement maps were committed
and pushed in `d99e214`. The developer's manual token-ID repairs were committed
and pushed in `641ded6`. Validation covered all 36 affected test classes,
including the focused array-transform host: 2,778 tests run, zero failures/errors,
three skipped. This clears the D0.5 repair/baseline gate, not Slice D sign-off.

**Standing instruction for every function:** follow the strict per-function
matrix below; preserve existing extractor goldens; generate only new expectations;
assert all six complete outputs and exact positioned diagnostics; run the entire
focused host; and update the function's completion ledger. Change production code
only for a proved defect. Do not commit unless requested.

**Finish the remaining transform**

1. [x] **D1.1 / T006:** `Complete D1.1: ARRAY_REPEAT.`

**Complete the first five older matrices**

2. [x] **D1.2.1 / T007:** `Complete D1.2.1: ARRAY_TO_STRING.`
3. [x] **D1.2.2 / T008:** `Complete D1.2.2: ARRAY_SLICE.`
4. [x] **D2.6.1 / T009:** `Complete D2.6.1: ARRAY_DISTINCT.`
5. [x] **D2.6.2 / T010:** `Complete D2.6.2: ARRAY_COMPACT.`
6. [x] **D2.6.3 / T011:** `Complete D2.6.3: ARRAY_SIZE.`

**Implement the four approved additions**

7. [x] **D3.8.1 / T012:** `Complete D3.8.1: TO_ARRAY routing, diagnostics and strict matrix.`
8. [x] **D3.8.2 / T013:** `Complete D3.8.2: AS_ARRAY routing, diagnostics and strict matrix.`
9. [x] **D3.8.3 / T014:** `Complete D3.8.3: IS_ARRAY routing, diagnostics and strict matrix.`
10. [x] **D3.8.4 / T015:** `Complete D3.8.4: SPLIT routing, diagnostics and strict matrix.`

**Complete the remaining five older matrices**

11. [x] **D3.9.1 / T016:** `Complete D3.9.1: SPLIT_TO_ARRAY.`
12. [x] **D3.9.2 / T017:** `Complete D3.9.2: STRTOK_TO_ARRAY.`
13. [x] **D3.9.3 / T018:** `Complete D3.9.3: ARRAY_GENERATE_RANGE.`
14. [x] **D3.9.4 / T019:** `Complete D3.9.4: ARRAY_POSITION.`
15. [x] **D3.9.5 / T020:** `Complete D3.9.5: ARRAYS_ZIP.`

**Completed original 21-function audits**

16. [x] **D-SIG / T021:** `Complete D-SIG: verify signatures, optional arguments and parser-versus-runtime validation boundaries across all 21 functions.`
17. [x] **D-AUDIT / T022:** `Complete D-AUDIT: reconcile all 21 functions against every required matrix cell; report any remaining gaps.`

**Complete every outstanding array function, starting here**

18. [x] **D5.1 / T025 / GC1:** structured constructor routing/affinity/signature and independent 83-case matrix complete.
19. [x] **D5.2 / T026:** contains VARIANT/operand and independent 74-case matrix complete; postfix-cast substitution loss repaired.
20. [x] **D5.3 / T027:** scalar flatten routing/affinity and independent 54-case matrix complete; table FLATTEN remains T028.
21. [x] **D5.4 / T028:** advanced FLATTEN complete with 56 independent cases and unchanged existing goldens. Only `FLATTEN(...)` examples; `LATERAL_FLATTEN` compatibility deferred beyond 5.1.4, no lexer changes or new explicit LATERAL examples.
22. [x] **D5.5 / T029:** FILTER complete: 71 independent lambda/binding/lineage cases.
23. [x] **D5.6 / T030:** TRANSFORM complete: 71 independent higher-order cases.
24. [x] **D5.7 / T031:** REDUCE complete: 82 independent array/initial-value/lambda cases.
25. [x] **D5.8 / T032:** `Fourteen A/B/C names covered by named tests and accepted sibling-family evidence; no duplicate matrix expansion.`
26. [x] **D5-SIG / T033:** `All 42 signature/routing/runtime-boundary dispositions consolidated; LATERAL_FLATTEN remains excluded.`
27. [x] **D5-AUDIT / T034:** `Coverage closed from the same evidence under the user-approved family policy; no second matrix audit.`

**Close the expanded Slice D**

28. [x] **D4.1 / T023:** `Single full default run complete: 4,306 reported, 4,244 passed, 62 skipped, zero failures/errors, exit 0; source-class gate coverage, no wrapper reruns.`
29. [x] **D4.2 / T024:** `Final all-42-name, Entry 4.1–4.12, GC1..GC4 and former E accounting complete; whole-release readiness remains separate.`

At any suitable checkpoint, separately prompt:
`Commit and push the completed work with a clear summary.`

##### D0 — lexer expansion and developer repair gate FIRST

1. **D0.1 — freeze the approved lexer inventory [REQ-001, REQ-004].**
   Confirm the 21-name list against the current grammar/generated constants.
   `TO_ARRAY=376`, `AS_ARRAY=377`, `IS_ARRAY=378`, and the already added
   `ARRAY_REPEAT=455` are existing tokens, not additions. `SPLIT` is the only
   new keyword in this D batch. Record the separate GC1 constructor risk above.
   - [x] T001 [Plan:D0.1] Reconcile the 21-name table and lexer decisions in `parse/documents/sql-grammar-5.1.4-workplan.md` against `parse/src/main/antlr4/sql/SQLSelectParser.g4`; do not change grammar yet.
2. **D0.2 — capture official before images [REQ-004].**
   Generate/capture the current Java lexer constants before editing. Also
   preserve or regenerate the last repaired golden baseline (`afa2196`, before
   `ARRAY_REPEAT`) in an isolated worktree if its official snapshot is unavailable.
   Never overwrite a before image or derive IDs from failing assertions.
   - [x] T002 [Plan:D0.2] Generate `SQLSelectParserLexer.java` from `parse/src/main/antlr4/sql/SQLSelectParser.g4` and preserve current-step plus last-repaired-baseline snapshots in session artifacts.
3. **D0.3 — append the new keyword [REQ-001, REQ-004].**
   Append `SPLIT` after `ARRAY_REPEAT`, immediately before `Identifier`; add it
   to `nonreserved_keywords` so existing column/qualified-UDF use remains legal.
   Do not insert keyword rules elsewhere or add duplicate conversion tokens.
   - [x] T003 [Plan:D0.3] Add only the `SPLIT` lexer entry and nonreserved-name handling in `parse/src/main/antlr4/sql/SQLSelectParser.g4`.
4. **D0.4 — regenerate and publish both complete worklists [REQ-004].**
   Use `tools/token_id_worklist_from_vocab.py` on official generated Java
   snapshots. Publish every changed name/before/after/delta in descending order:
   current-step mapping and cumulative mapping from the last repaired baseline.
   Expected, subject to verification: current-step `Identifier` 456 -> 457
   (+1), cumulative 455 -> 457 (+2). `ARRAY_REPEAT=455` stays fixed;
   the new `SPLIT` is expected at 456. Report all following tokens through `BAD`,
   including comments/whitespace/skip tokens; verify earlier IDs are unchanged.
   - [x] T004 [Plan:D0.4] Capture the regenerated lexer and run `tools/token_id_worklist_from_vocab.py` for both baselines; record the complete human-readable worklists in `parse/documents/sql-grammar-5.1.4-workplan.md`.
   - Completed 2026-11-18: official constants verify `SPLIT=456`, unchanged
     `ARRAY_REPEAT=455`, `Identifier=457`, and `BAD=477`. Generation/compilation
     passed. Both complete replacement maps are recorded in the SPLIT lexer
     checkpoint below. D0.5 remains blocked on developer manual repairs;
     no existing golden strings were changed.
5. **D0.5 — STOP for developer manual golden repairs [REQ-002, REQ-004].**
   The developer makes descending, surgical IDE replacements. No agent bulk
   token rewrite or golden refresh. Resume only after developer confirmation;
   verify the existing focused host on the repaired baseline before adding new
   matrices. If that run fails, diagnose rather than changing golden content
   automatically. Do not run the broad sign-off ladder here.
   - [x] T005 [Plan:D0.5] Obtain manual-repair confirmation for existing parser goldens, then run `SqlEventWalkerSnowflakeArrayTransformTests` using the focused command below; retain actual results.
   - Completed in checkpoint `641ded6`: user-confirmed manual repairs were
     validated across all 36 affected classes, including the focused host.
     Isolated Maven test result: 2,778 run, zero failures/errors, three skipped,
     exit code 0; full default accuracy and Slice D sign-off remain pending.

##### Per-function completion contract [REQ-002, REQ-003]

Every function row below independently follows the established regression /
coverage case -> route / walker / registry -> strict matrix -> focused green ->
accounting sequence:

**Superseding user-approved carry-forward policy (2026-11-19):** T032–T034
accept named tests plus sibling-family evidence where the parser/walker path
is shared; missing duplicate full suites are not context blockers.
`ARRAY_CONSTRUCT_COMPACT` uses three name-specific cases plus its constructor
family. The reconciliation identifies shared mechanisms and witnesses for
all 14 carried-forward names without claiming new full per-name matrices or
upgrading the actual strength of older assertions. Completed matrices for
the other 28 names remain unchanged.

1. Start with a new case for the actual missing behavior or assertion coverage.
   Prove red -> green for behavior changes. Coverage-only additions may already
   pass; record that fact rather than manufacturing a routing failure or changing
   correct production code. Retain existing passing controls.
2. Check official signature forms and family routing. Fix only defects proved
   by the new cases; do not refactor working handlers merely to fill a matrix.
   `TO_ARRAY`, `AS_ARRAY`, and `IS_ARRAY` have one operand; `SPLIT` has two.
3. One realistic SQL query per method, with surrounding columns/conditions.
   Independently cover SELECT, WHERE, JOIN ON, HAVING, QUALIFY, ORDER BY, VALUES,
   INSERT, UPDATE, DELETE, DDL, SCRIPT. Do not substitute family-level coverage.
4. Cover qualified/unqualified success; unresolved, ambiguous, missing-alias,
   and hidden-alias failures; raw predicand versus qualified-column variables;
   independently isolate each eligible argument position, including optional
   arguments. Add combined-variable controls where relevant.
5. Cover nested expression, derived-output consumption, set-op sibling isolation,
   Snowflake/ANSI/Snowflake SCRIPT reset, schema-qualified UDF and same-named
   column neutrality, uppercase spelling, and mixed-dialect diagnostics.
6. Assert all six complete extractor goldens and the complete diagnostic list
   with exact messages, severity, code, positions and count. Strengthen directly
   related older diagnostic assertions without recapturing their existing
   extractor goldens. Generate only new golden expectations with tooling,
   inspect them against the documented scope/lineage rules, then freeze literals.
7. Malformed comma/parenthesis controls must assert exact parser diagnostics and
   error counts. Do not walk rejected syntax trees just to obtain extractor
   outputs. Keep parser responsibilities separate from database execution:
   runtime ARRAY types, element values, lengths and output values are not evaluated.
8. Use `tools/array-matrix/` for signature-driven missing cases, preservation
   checks and generated evidence/accounting. Reuse unchanged contracts and the
   known gap inventory rather than repeating discovery. Related functions may
   share an approved batch while retaining independent matrices and task IDs.
   Run the entire focused host once per batch, not the broad gates.
   Record function name, matrix cells, exact command, pass/fail/skip counts and
   exit code. A family green count alone cannot mark the function complete.

##### D1 — multi-argument transform completion

- [x] T006 [Plan:D1.1] Complete `ARRAY_REPEAT` in `parse/src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java` under the per-function contract, reusing current transform handling; include element/count resolution and variable typing, scalar/array/NULL elements, NULL/zero/negative counts as parse inputs, not executed results. Keep grammar/registry/walker call sites consistent if a proved defect requires a change.
  - Completed 2026-11-18: the route-only seed was strengthened into a 48-case
    matrix, with 47 net new methods. All 256 pre-existing strict methods and
    extractor goldens remain unchanged. See the method-level ARRAY_REPEAT
    evidence ledger in the reconciliation and the D1.1 completion record below.
- [x] T007 [Plan:D1.2.1] Complete `ARRAY_TO_STRING` in the focused host: JOIN ON/HAVING, independent resolution/hidden-alias and both-argument variable controls, nested/derived/set-op coverage and three-statement reset; strengthen exact diagnostics and retain existing goldens.
  - Completed 2026-11-18: 30 new methods plus exact diagnostic lists in all 15
    older methods; all existing extractor goldens and the other 289 methods are
    unchanged. Fixed the missing parser nonreserved-name alternative exposed by
    the neutrality controls; no lexer declaration or token ID changed. Focused
    host: 334 passed, zero failures/errors/skips. See the D1.2.1 completion record
    and method-level reconciliation ledger.
- [x] T008 [Plan:D1.2.2] Complete `ARRAY_SLICE` in the focused host: WHERE/JOIN ON/QUALIFY/DDL/SCRIPT, independently isolated controls for all three arguments, raw-variable and deep coverage, and exact diagnostics; retain existing goldens.
  - Completed 2026-11-18: 43 new cases and 12 strengthened diagnostic lists;
    55 independent cases total. Existing extractor goldens and all 322 other
    methods preserved. Focused host: 377 passed, zero failures/errors/skips.
    No production change was needed.

##### D2.6 — older unary matrix completion

- [x] T009 [Plan:D2.6.1] Complete `ARRAY_DISTINCT` in the focused host beyond its SELECT/missing-alias seeds, satisfying every per-function contract cell.
- [x] T010 [Plan:D2.6.2] Complete `ARRAY_COMPACT` in the focused host: JOIN ON/HAVING/QUALIFY/DDL/SCRIPT, independent resolution and variable typing, nested/derived/set-op controls and exact diagnostics.
- [x] T011 [Plan:D2.6.3] Complete `ARRAY_SIZE` in the focused host under the same independent contract; do not credit `ARRAY_COMPACT` tests as its evidence.
  - Completed 2026-11-18 as one approved batch: 31 cases per function (29
    six-output goldens and two parser-only controls), 75 added methods and all
    18 older diagnostic lists strengthened. Existing extractor goldens and all
    359 unrelated methods preserved. One focused run passed 452/452, zero
    failures/errors/skips. Coverage-only work; no production or lexer changes.

##### D3.8 — four approved conversion/predicate/split additions

For each row, use the focused host plus the existing grammar, walker and
Snowflake registry paths named above. Add explicit family membership and
Snowflake affinity, reuse compatible walker behavior, and prove generic
qualified UDFs/same-named columns remain unaffected. A generic-call success
without the intended route and diagnostics does not close the function.

- [x] T012 [Plan:D3.8.1] Implement `TO_ARRAY` recognition/affinity and its complete independent matrix using existing token 376; verify its single operand's lineage and substitutions, without evaluating conversion results.
- [x] T013 [Plan:D3.8.2] Implement `AS_ARRAY` recognition/affinity and its complete independent matrix using existing token 377; preserve generic qualified-UDF behavior and do not invent runtime VARIANT validation.
- [x] T014 [Plan:D3.8.3] Implement `IS_ARRAY` recognition/affinity and its complete independent predicate matrix using existing token 378; test both predicate and projected-expression placements.
  - Completed 2026-11-18: 35/32/37 independent cases respectively; 104 new
    methods with full outputs/exact diagnostics, including six parser-only
    syntax controls. Added explicit conversion-family names and registry
    entries, reusing the existing compatible walker. All 452 prior methods
    preserved byte-for-byte; one shared focused run passed 556/556 with zero
    failures/errors/skips. Official generated lexer unchanged.
- [x] T015 [Plan:D3.8.4] Implement `SPLIT` using the D0 keyword, explicit two-operand split handling, Snowflake affinity and a complete independent matrix; preserve existing subscript/FLATTEN composition parsing and `SPLIT_TO_ARRAY` compatibility.
  - Completed 2026-11-18: 48 independent cases, including 45 six-output
    goldens and three parser-only controls. Added the explicit C route and
    affinity; preserved legacy generic-routine AST map construction for SPLIT.
    Existing subscript and FLATTEN goldens are unchanged.

##### D3.9 — older conversion/range/position/zip matrix completion

- [x] T016 [Plan:D3.9.1] Complete `SPLIT_TO_ARRAY` in the focused host: missing JOIN ON/HAVING/QUALIFY/DDL/SCRIPT, independent controls for both arguments, variable typing and deep cases; preserve its existing public behavior/diagnostics and document compatibility provenance separately from Snowflake `SPLIT`.
- [x] T017 [Plan:D3.9.2] Complete `STRTOK_TO_ARRAY` in the focused host: missing placements, resolution/variables/deep cases and documented optional-delimiter forms; test each eligible argument position independently.
  - Completed 2026-11-18 in the approved T015-T017 batch: 53 cases per
    older function (50 six-output, three syntax), including both optional
    delimiter forms. Added 138 methods across the batch and strengthened 16
    legacy diagnostic lists; all old extractor goldens and 540 unrelated
    host methods preserved. Final shared run: host 694/694, subscript 24/24,
    table-function 78/78; total 796, zero failures/errors/skips. Lexer unchanged.
- [x] T018 [Plan:D3.9.3] Complete `ARRAY_GENERATE_RANGE` in the focused host: replace literal-only evidence with additional column/variable cases for start/stop/optional step, independent resolution failures, both two-/three-argument forms, missing placements and deep cases; preserve existing literal controls.
- [x] T019 [Plan:D3.9.4] Complete `ARRAY_POSITION` in the focused host: missing placements, independent needle/array controls, variable typing, nested/derived/set-op cases and exact diagnostics.
- [x] T020 [Plan:D3.9.5] Complete `ARRAYS_ZIP` in the focused host: missing placements, one/multiple-array forms, independently isolated eligible argument controls, variable typing and deep cases, without executing zip results.
  - Completed 2026-11-18 as one approved batch: 79/50/90 cases respectively,
    including 76/47/87 six-output goldens and three parser-only controls each.
    Added 196 methods and strengthened 23 legacy diagnostic lists, preserving
    all existing extractor goldens and 671 unrelated host methods. One focused
    run passed 890/890, zero failures/errors/skips. Coverage-only additions;
    no production, lexer or toolkit changes. Next: T021 / D-SIG.

##### Cross-cutting closure and the single final sign-off

- [x] T021 [Plan:D-SIG] Reconcile documented signatures across all 21 names against `parse/src/main/antlr4/sql/SQLSelectParser.g4`, generic fallback behavior and focused tests. Cover valid optional-argument forms and exact malformed-syntax diagnostics; document which arity/type checks intentionally remain database responsibilities. Preserve existing generic/schema-qualified compatibility; do not introduce a global arity/type validator or silently treat fallback routing as dedicated coverage.
  - Completed 2026-11-18: 134 new methods (117 six-output, 17 parser-only),
    preserving all 890 existing methods byte-for-byte. Explicit positive and
    zero rule counts distinguish all six dedicated families from generic
    fallback. Maven and VS Code each passed 1,024/1,024; Maven exit 0, no
    failures/errors/skips. The [21-signature policy ledger](slice-d-array-function-reconciliation.md#t021--d-sig-completed-signature-validation-policy)
    records optional forms, permissive compatibility, native/runtime limits,
    exact malformed controls and the AR-M20..24 reconciliation. No production
    or lexer changes. Next: T022; T023/T024 remain open.
- [x] T022 [Plan:D-AUDIT] Update the 21-row completion ledger in `parse/documents/slice-d-array-function-reconciliation.md` and this master plan, with method-level evidence for every required cell. Recheck the six expanded matrices already present (`ARRAY_SORT`, `ARRAY_MAX`, `ARRAY_MIN`, `ARRAY_REVERSE`, `ARRAY_SUM`, `ARRAYS_TO_OBJECT`) under the final lexer IDs. No unassigned in-scope function, pending required matrix cell, or unexpected keyword addition may pass this gate.
  - Completed 2026-11-19: 21/21 unique token/rule/handler/registry owners,
    854/854 source-audited required cells and 277 additional source-matched
    catalog labels. Nine proved gaps repaired with eight six-output cases
    and one exact parser-only control; all 1,024 previous methods unchanged.
    Maven and VS Code each passed 1,033/1,033, zero failures/errors/skips;
    Maven exit 0. No production changes or lexer additions; the complete
    generated lexer hash is unchanged. The
    [T022 ledger](slice-d-array-function-reconciliation.md#t022--d-audit-completed-cell-level-reconciliation)
    records every required cell's named witness, final counts and limits.
    At that checkpoint, next was T023/T024; the scope amendment inserts
    T025..T034 first. This is scoped matrix-audit completion, not final
    Slice D, whole-project build/smoke or release sign-off.

##### D5 — exhaustive array closure amendment (T025..T034 BEFORE T023/T024)

**Acceptance for every new function:** apply REQ-001..REQ-004 and the
[matrix contract](#entry-4-test-framework-matrix-contract). Use one realistic
query per method, all six complete extractor goldens and the complete exact
positioned diagnostic list. Rejected syntax is parser-only. Isolate every
eligible argument, optional argument and variadic tail independently. Cover
resolution, substitutions, supported SQL/DML/snippet sites, nested/derived/CTE/
set-op boundaries, neutrality and statement warning reset. For table functions
and lambdas, adapt cells to their actual grammar role; record a specific
reason for each inapplicable scalar-placement cell, never omit it silently.
Use `tools/array-matrix/` where its templates fit; extend it only for proved
new shapes, not by pretending scalar templates prove table/lambda semantics.
Preserve old expectations except developer-controlled token-ID repairs.

- [x] T025 [Plan:D5.1] **Complete: `ARRAY_CONSTRUCT_STRUCTURED` / GC1** in `parse/src/main/antlr4/sql/SQLSelectParser.g4`, `parse/src/main/java/sql/grammar/SnowflakeRoutineDialectRegistry.java` and the existing generic constructor walker path; independent 83-case constructor matrix in `parse/src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayConstructTests.java`.
  - **Lexer step complete, including developer-confirmed manual repairs and
    a successful sidebar test run (2026-11-19).** See the
    [official token worklist and sign-off](#slice-d-d5-lexer-preparation-checkpoint-complete).
    This does not complete the function's routing, affinity, signature or matrix.
  - Before editing the lexer, freeze recognition/routing needs for **all 42
    names**, existing `LATERAL_FLATTEN` compatibility, and any required lambda
    punctuation. Existing FILTER/TRANSFORM/REDUCE/ARRAY_FLATTEN/FLATTEN tokens
    are not new additions; `LATERAL_FLATTEN` retains its existing identifier
    spelling. Do not invent duplicate keywords. Record genuinely missing
    entries and resolve the intended constructor signature against documentation.
  - Generate and retain the official
    `parse/target-maven/generated-sources/antlr4/sql/SQLSelectParserLexer.java`
    before image and the last developer-repaired baseline. Append approved
    missing keywords at the **end of the keyword list immediately before
    `Identifier`**, with nonreserved handling as appropriate; retain an official
    regenerated after image. Use `tools/token_id_worklist_from_vocab.py` to
    publish the **entire** named old/new/delta map, highest token ID first,
    including every following token, both incremental and cumulative if needed.
    Explicitly account for any unavoidable punctuation-token changes too.
  - **Pause for the developer's careful manual IDE repairs and confirmation.**
    Do not automatically rewrite existing goldens, infer shifts from failure
    samples, or claim matrix completion while that gate is pending. Validate
    affected existing classes after confirmation, then finish the structured
    constructor before implementing another outstanding function.
  - Verify argument order, empty/variadic and NULL/nested forms under the
    documented/parser-compatibility policy, independent operand lineage,
    substitution typing, name/UDF neutrality, malformed syntax, endpoint
    parity, dialect warnings and SCRIPT reset. Do not evaluate array contents.

- [x] T026 [Plan:D5.2] Complete `ARRAY_CONTAINS` / 4.1 in `parse/src/test/java/sql/walker/SqlEventWalkerFunctionsAggregatesWindowingTests.java`: 74 independent cases using existing generic routing and affinity. Repair the proved postfix-cast substitution loss in `exitValue_expression_primary` using the existing context-typing helper before suffix wrapping. Independently cover value and array operands, VARIANT casts/shapes, NULL/empty inputs, predicate and projection uses, all applicable placement/endpoint cells and negative resolution/variable controls. Runtime type/value boundaries remain database responsibilities, not a global validator.

- [x] T027 [Plan:D5.3] Complete scalar `ARRAY_FLATTEN` / 4.5: reuse `array_unary_transform_function` and its walker, register Snowflake affinity, and add 54 independent cases to `parse/src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java`. No keyword or token-ID changes. Nested arrays, empty/NULL shapes, independently resolved/substituted operands, composition, generic/UDF compatibility and malformed calls are covered. Table FLATTEN remains T028; flattening results and runtime element validity are not evaluated.

- [x] T028 [Plan:D5.4] Advanced table `FLATTEN` / 4.5 complete in `parse/src/test/java/sql/walker/SqlEventWalkerTableFunctionTests.java`: 56 independent cases (51 six-output goldens, five positioned syntax controls), including six AccessObject/direct-walker endpoint comparisons. Covers named options, six output columns and aliases, correlated input, chained calls, joins, derived/CTE/set-op consumers, unresolved/ambiguous/hidden/self/later references, substitutions and SCRIPT reset. Fixed input-source visibility, INSERT source resolution and bare argument substitution typing; retained UPDATE target-aware egress and existing goldens. Use only `FLATTEN(...)` examples; `LATERAL_FLATTEN` compatibility is deferred beyond 5.1.4 and explicit `LATERAL` examples are excluded from this batch. Existing modifier support and lexer IDs are unchanged. Focused regression selection: 304 passed, zero failures/errors/skips.

- [x] T029 [Plan:D5.5] Implement and complete higher-order `FILTER` / 4.12 in `parse/src/main/antlr4/sql/SQLSelectParser.g4` and `parse/src/main/java/sql/walker/SqlParseEventWalker.java`, registering affinity in `parse/src/main/java/sql/grammar/SnowflakeRoutineDialectRegistry.java` as required; add its matrix in the planned `parse/src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayHigherOrderTests.java`. Verify documented lambda syntax, parameter bindings (including typed forms where documented), array/body argument handling, outer references, shadowing/nested-lambda isolation, substitution contexts and malformed syntax. Lambda parameters must not become fictitious physical columns. Preserve unrelated SQL FILTER syntax and generic/schema-qualified compatibility; no runtime lambda evaluation or Panto substitution redesign.

- [x] T030 [Plan:D5.6] Complete `TRANSFORM` independently in `parse/src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayHigherOrderTests.java`, extending T029's shared grammar/walker/registry handling only where required. Verify its own input/body lineage, local parameter binding, nested composition, applicable placements/endpoints, diagnostic positions, substitution typing and syntax controls; FILTER's passing cases cannot substitute for TRANSFORM evidence.

- [x] T031 [Plan:D5.7] Complete `REDUCE` independently in `parse/src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayHigherOrderTests.java` with documented array/initial-value/lambda forms, ordered arguments and separately isolated accumulator/element bindings and external references. Cover shadowing, nested calls, scope leakage, substitutions, supported placements/endpoints and exact malformed-call/lambda diagnostics. Reuse shared grammar/walker helpers without inventing result evaluation or type inference beyond the parser contract.

  Completed 2026-11-19: T029/T030/T031 contribute 71/71/82 methods, respectively:
  **224 new cases**, comprising 215 six-output goldens and nine exact positioned
  syntax controls. Each function includes all twelve placements and twelve
  direct-walker/AccessObject snippet endpoints. Focused Maven selection:
  **1,688 passed, zero failures/errors/skips, exit 0**. No lexer IDs or existing
  extractor goldens changed. The transient VS Code generated-rule diagnostic
  has cleared; the higher-order test host reports no editor errors.

- [x] T032 [Plan:D5.8] As part of the **single shared T032/T033/T034 evidence consolidation**, carry forward the A/B/C evidence for these **14** names in `parse/documents/slice-d-array-function-reconciliation.md`: `ARRAY_CONSTRUCT`, `ARRAY_CONSTRUCT_COMPACT`, `ARRAY_AGG`, `ARRAY_UNION_AGG`, `ARRAY_UNIQUE_AGG`, `ARRAY_CAT`, `ARRAY_EXCEPT`, `ARRAY_INTERSECTION`, `ARRAYS_OVERLAP`, `ARRAY_APPEND`, `ARRAY_PREPEND`, `ARRAY_INSERT`, `ARRAY_REMOVE`, `ARRAY_REMOVE_AT`. Named witnesses and shared constructor/aggregate/binary/mutator mechanisms are recorded under the user-approved family policy. Existing literal/subscript controls and T025's twelve shared constructor snippet endpoints are retained. No duplicate matrices or family reruns.

  **Compact evidence resolved (2026-11-19):** per user approval, retain its
  existing SELECT case and add only empty-call and mixed NULL/value cases,
  relying on constructor-family tests for shared behavior. All three have
  six full output goldens; the new cases assert exact diagnostic lists.
  One focused constructor-host run passed **114 tests, zero failures/errors/
  skips, exit 0**; no production changes. This satisfies compact's approved
  minimum, not a duplicated matrix. The same family-based acceptance now
  closes the remaining carry-forward evidence. T032/T033/T034 are complete;
  see the [shared consolidation](slice-d-array-function-reconciliation.md#shared-consolidation-complete-t032t033t034).

- [x] T033 [Plan:D5-SIG] Completed in the **same consolidation** for all **42** names: reuse T021 and T025–T031 signature/routing/runtime notes; add the missing 14 native forms and generic/aggregate/binary/mutator dispositions. Preserve permissive arity compatibility, standard ARRAY_AGG versus Snowflake-affinity policy, empty-call behavior and database-only runtime semantics. `LATERAL_FLATTEN` remains excluded. No new tests or unchanged-entry re-audit.

- [x] T034 [Plan:D5-AUDIT] Closed coverage from the **same consolidated evidence** for all 42 names. Preserve T022's 854-cell/1,033-method original scope and the seven later ledgers; accept the 14 carried-forward names' documented sibling-family evidence. Literal/subscript/endpoint controls, lexical/manual-repair evidence, registry/routing dispositions and deferred spelling are explicitly accounted for. No invented expanded cell count or duplicate matrix pass. T023/T024 final sign-off is recorded below.

- [x] T023 [Plan:D4.1] Executed the **single full default parser accuracy run** after consolidation, completed 2026-11-19 09:18:53 -05:00 in 1 min 29 sec. **4,306 reported; 4,244 passed; zero failures/errors; 62 existing/configured skips; exit 0 / BUILD SUCCESS.** Original source-class results supply affected-family and Phase15/Smoketest coverage. No focused/wrapper rerun, repair loop or golden rewrite.
- [x] T024 [Plan:D4.2] Recorded final status/accounting once here and in the [reconciliation sign-off](slice-d-array-function-reconciliation.md#t023t024-final-slice-d-sign-off-2026-11-19): all 42 names, 4.1–4.12, GC1/GC2/GC3/GC4 and former E ownership are closed. `LATERAL_FLATTEN` compatibility stays excluded. Consumer bundle contents did not change, so no vendor/JAR docs sync was needed. Slice F, unrelated entries, the opt-in release suite, packaging/publishing and whole-release certification remain separate.

**Single executed final accuracy command (T023, JDK 21):**

```bash
mvn -f parse/pom.xml -Pisolated-build \
  -Dpss.walker.test.verbose=false -Dpss.test.slow=false -Dpss.test.diagnostics=false test
```

Use the configured JDK for this module. No second VS Code test run is required.
`Phase15DerivedColumnGateTestSuite` only inherits `SqlEventWalkerPivotUnpivotTests`;
all 263 `SmoketestQualityGateTestSuite` test methods delegate to existing source
tests. Use the original classes' results from the full run as gate-coverage
evidence; do not claim the wrapper classes themselves ran or add their counts.
"Full suite" means default accuracy tests, not opt-in release/slow or diagnostic
modes. Preserve configured exclusions and report skips; do not exclude additional
bound-query/snippet accuracy classes. This policy supersedes earlier references
to a sequential focused/Phase15/Smoketest/full-suite ladder for Slice D.

##### Requirement mapping for the expanded sequence

Requirements are defined in the [reconciliation](slice-d-array-function-reconciliation.md#verification-requirements).

| Requirement | Plan items / tasks | Required implementation evidence |
|---|---|---|
| REQ-001: complete named ownership and implementation paths | Original T001/T003/T006..T022; D5.1..D5.8 (T025..T032); D5-AUDIT (T034); D4.2 (T024) | All 42 canonical names plus FLATTEN spellings explicitly accounted for in the reconciliation; lexical/routing/handler/affinity dispositions and named source evidence, with no duplicate tokens or hidden omissions |
| REQ-002: completed matrices plus user-approved carry-forward family coverage | Original T005..T022; D5.1..D5.8 (T025..T032); D5-AUDIT (T034); D4.1/D4.2 (T023/T024) | Existing strict ledgers retained; 14 carried-forward names mapped to named tests and shared family mechanisms with actual assertion strength preserved; focused results reused and final default accuracy passed |
| REQ-003: explicit signature/parser-runtime boundary | Original T006..T021; D5.1..D5.7 (T025..T031); D5-SIG (T033) | All 42 documented/parser-compatible forms, optional/variadic/table/lambda boundaries, exact syntax diagnostics and preserved generic/runtime compatibility |
| REQ-004: frozen lexer decisions, official worklists, manual repair and final gate | Original T001..T005; D5.1 (T025); D5-AUDIT (T034); D4.1/D4.2 (T023/T024) | Expanded lexical inventory, official before/after and last-repaired-baseline snapshots, complete descending named maps, developer repair confirmation and successful final sign-off |

**Validation policy for Stage D (reordered sign-off):**
- **Per substep/batch (including the expanded sequence):** run focused family suites only after the D0 manual-repair gate; no automatic existing-golden rewrites.
- The new T025 lexical/manual-repair gate supersedes the earlier D0 clearance
  for subsequent lexer changes; it cannot be skipped because D0 once passed.
  The current two-token addition and manual repairs are developer-confirmed
  complete as of 2026-11-19; any later lexer addition requires a fresh gate.
- **Single Slice D final sign-off:** consolidate existing evidence once, run the
  full default parser accuracy suite once, and record closure once. Its original
  source-class results supply family and Phase15/Smoketest coverage without
  duplicate wrapper execution. Stop and report a failed run.

#### Slice D final sign-off checklist — complete (2026-11-19)

- [x] **T025..T034 complete for all 42 in-scope names**
  - [Shared evidence consolidation](slice-d-array-function-reconciliation.md#shared-consolidation-complete-t032t033t034) and [manual lexer-repair evidence](#slice-d-d5-lexer-preparation-checkpoint-complete).
  - Deferred `LATERAL_FLATTEN` compatibility remains excluded.
- [x] **One full default accuracy run**
  - Command: `mvn -f parse/pom.xml -Pisolated-build -Dpss.walker.test.verbose=false -Dpss.test.slow=false -Dpss.test.diagnostics=false test`
  - Exit code **0**; **4,306 reported / 4,244 passed / 0 failures / 0 errors / 62 skipped**, across 70 source-class reports.
  - [Same-run Surefire reports](../target-maven/surefire-reports/): constructor 114, transform 1,087, higher-order 224, table-function 134, binary/set 14, mutator 22, set-aggregate 7, pivot/unpivot 203; all zero failures/errors/skips. Wrapper classes were not rerun or double-counted.
  - Skips: 22 slow-release, 26 diagnostic, 11 manual profiling/timing and three parked live-query tests. Existing `PUML3ParserTest.java` / `DumpNestedCteSymbolGoldensTest.java` exclusions unchanged.
  - Persistent session log: `slice-d-final-accuracy.log`; completed 09:18:53 -05:00, elapsed 1 min 29 sec.
- [x] **Final status/accounting update recorded once**
  - Updated sections: current Slice D summary, task/index checkboxes, this final checklist, Entry 4 status table, GC1..GC4 and reconciliation final sign-off.
  - T023/T024 and T025..T034 complete; all 42 canonical functions are explicitly recorded in the [closed inventory](slice-d-array-function-reconciliation.md#expanded-all-array-closure-inventory). Entry 4.1–4.12 and former E function work are closed.
  - `LATERAL_FLATTEN` compatibility remains deferred. This checklist records
    the Slice D milestone only; later release decisions are maintained in
    the release checklist and their owning entries above/below.

### Entry 4 array-function inventory and historical gap list (no ellipsis)

**Current audit:** [Slice D array-function reconciliation](slice-d-array-function-reconciliation.md).
The following inventory/gap snapshot predates gap-closure lexer additions and
omits `TO_ARRAY`, `AS_ARRAY`, `IS_ARRAY`, and Snowflake's documented `SPLIT`
(all four now approved for D3.8).
It must not be used as the current exhaustive Snowflake inventory or as proof of
per-function completion. `SPLIT_TO_ARRAY` compatibility/dialect attribution
requires an explicit decision rather than silent replacement.

Source-of-truth inputs used for this inventory:
- Lexer/grammar tokens in `SQLSelectParser.g4` (`ARRAY_*`, `ARRAYS_*`, `SPLIT_TO_ARRAY`, `STRTOK_TO_ARRAY`)
- Snowflake documentation list: `functions-semistructured` (retrieved 2026-11-17)

#### A) Historical pre-gap-closure lexer inventory (29)

`ARRAY_AGG`, `ARRAY_CONSTRUCT`, `ARRAY_CONSTRUCT_COMPACT`, `ARRAY_CONTAINS`, `ARRAY_UNION_AGG`, `ARRAY_UNIQUE_AGG`, `ARRAY_APPEND`, `ARRAY_PREPEND`, `ARRAY_INSERT`, `ARRAY_REMOVE`, `ARRAY_REMOVE_AT`, `ARRAY_CAT`, `ARRAY_EXCEPT`, `ARRAY_INTERSECTION`, `ARRAYS_OVERLAP`, `ARRAY_TO_STRING`, `ARRAY_DISTINCT`, `ARRAY_COMPACT`, `ARRAY_FLATTEN`, `ARRAY_GENERATE_RANGE`, `ARRAY_POSITION`, `ARRAY_SIZE`, `ARRAY_SLICE`, `ARRAY_SORT`, `ARRAY_MAX`, `ARRAY_MIN`, `ARRAYS_ZIP`, `SPLIT_TO_ARRAY`, `STRTOK_TO_ARRAY`

#### B) Historical missing-lexer inventory before GC additions (explicit gaps)

- `ARRAY_CONSTRUCT_STRUCTURED`
- `ARRAY_REPEAT`
- `ARRAY_REVERSE`
- `ARRAY_SUM`
- `ARRAYS_TO_OBJECT`

All five are in scope. GC2/GC3 implementation and original audits are complete;
GC1 is complete under T025 and T023/T024, not a release-scope exclusion.

### Entry 4 gap-closure subplan (GC) — explicit sequencing for doc/lexer gaps

All five gap functions are in-scope for Entry 4 closure. Sequence them in bounded GC batches:

1. **GC1 — COMPLETE: Slice D5.1 / T025 (constructor parity, originally Slice A)**
   - Function: `ARRAY_CONSTRUCT_STRUCTURED`
   - Attach to: **4.2**, owned by consolidated Slice D.
   - Deliverables: T025's expanded lexical inventory, official before/after
     snapshots and complete descending named token-ID maps; developer manual
     repair gate; grammar/walker/affinity handling and independent strict
     constructor matrix. Completed before T026; T023/T024 final sign-off passed.

2. **GC2 — COMPLETE: Slice D extensions (transform/unary parity)**
   - Functions: `ARRAY_REPEAT`, `ARRAY_REVERSE`, `ARRAY_SUM`
   - Attach to:
     - **4.9**: `ARRAY_REPEAT` (multi-arg transform)
     - **4.10**: `ARRAY_REVERSE`, `ARRAY_SUM` (single-array-argument parser family; ARRAY_SUM is a database aggregate)
   - Deliverables: dedicated function routing by signature family, strict per-function matrix, focused validations during implementation; include gate/full-suite evidence at Slice D final sign-off.
   - D2.4 unary portion complete (2026-11-18): `ARRAY_REVERSE` and `ARRAY_SUM` had 27 strict tests each; that historical focused run passed 216/216. Subsequent D1/D2.6/D-SIG and T022 work established 55 `ARRAY_REPEAT` and 33 each `ARRAY_REVERSE`/`ARRAY_SUM` cases, with the historical 1,033-test host green in Maven and VS Code. The current expanded host passes 1,087 in T023's full default run; T024 closes GC2 accounting.

3. **GC3 — COMPLETE: Slice D conversion extension**
   - Function: `ARRAYS_TO_OBJECT`
   - Attach to: **4.11**
   - Deliverables: conversion-family routing, argument-shape diagnostics, strict per-function matrix, focused validations during implementation; include gate/full-suite evidence at Slice D final sign-off.
   - D3.6 implementation and D3.7 focused closeout complete (2026-11-18); that historical host run passed 256/256. T022 added isolated unqualified controls for both arguments and a missing-right-parenthesis control. `ARRAYS_TO_OBJECT` retains 48 cases (45 six-output, three parser-only). The current expanded host passes 1,087 in T023's full default run; T024 closes GC3 accounting.

4. **GC4 — COMPLETE: matrix/accounting closeout**
   - T034/T024 close all five gaps and all 42 names using the original 21-name
     audit, seven added ledgers and accepted fourteen-name family evidence.
   - Final default run: 4,244 passed, 62 skipped, zero failures/errors, exit 0.
     Source-class results cover the gates without duplicate wrapper execution.

**Gate rule for GC batches:** no “etc” placeholders in status rows, plans, or closeout notes; each function must be named explicitly.

#### C) Slice assignment map (explicit)

The following are stable **subsection labels**, not separate future owners:
all outstanding function work closes in D5; completed A/B/C functions carry
forward through T032. No listed array function is deferred outside D closure.

- **4.1**: `ARRAY_CONTAINS`
- **4.2**: `ARRAY_CONSTRUCT`, `ARRAY_CONSTRUCT_COMPACT`, `ARRAY_CONSTRUCT_STRUCTURED` (GC1)
- **4.3**: `ARRAY_AGG`
- **4.5**: `ARRAY_FLATTEN` (T027 complete); `FLATTEN` (T028 complete).
  Existing LATERAL modifier support is unchanged; `LATERAL_FLATTEN`
  compatibility remains deferred beyond 5.1.4.
- **4.6**: `ARRAY_UNION_AGG`, `ARRAY_UNIQUE_AGG`
- **4.7**: `ARRAY_CAT`, `ARRAY_EXCEPT`, `ARRAY_INTERSECTION`, `ARRAYS_OVERLAP`
- **4.8**: `ARRAY_APPEND`, `ARRAY_PREPEND`, `ARRAY_INSERT`, `ARRAY_REMOVE`, `ARRAY_REMOVE_AT`
- **4.9 (transforms with extra args)**: `ARRAY_TO_STRING`, `ARRAY_SLICE`, `ARRAY_REPEAT` (GC2)
- **4.10 (single-array-argument parser family)**: `ARRAY_DISTINCT`, `ARRAY_COMPACT`, `ARRAY_SIZE`, `ARRAY_SORT`, `ARRAY_MAX`, `ARRAY_MIN`, `ARRAY_REVERSE`, `ARRAY_SUM` (GC2; ARRAY_SUM is a structured-array aggregate across rows)
- **4.11 (conversion/predicate/split/range/position/zip)**: `SPLIT_TO_ARRAY`, `STRTOK_TO_ARRAY`, `ARRAY_GENERATE_RANGE`, `ARRAY_POSITION`, `ARRAYS_ZIP`, `ARRAYS_TO_OBJECT` (GC3), `TO_ARRAY`, `AS_ARRAY`, `IS_ARRAY`, `SPLIT` (approved D3.8 expansion)
- **4.12 (higher-order)**: `FILTER`, `TRANSFORM`, `REDUCE`

### Entry 4 test framework (matrix contract)

Use the same strict matrix discipline used in Entry 1 date/time work:

1. **Matrix IDs by concern**
   - **AR-Cxx**: core happy-path + placement matrix (SQL + DML + required clause sites)
   - **AR-Mxx**: malformed/syntax/dialect diagnostics (exact code, severity, position)
   - **AR-Rxx**: deep regressions (nested scopes, alias propagation, set-op branch isolation, multi-statement SCRIPT reset)
2. **Assertion contract**
   - Exact extractor goldens: AST, interface, substitutions, table dictionary, query dictionary, symbol table.
   - For snippet endpoints, keep AccessObject parity checks aligned with extractor outputs (same policy used in datetime suites).
3. **Column-resolution contract**
   - Every slice must include qualified, unqualified, unresolved, ambiguous, and hidden-alias controls.
   - Substitution-variable arguments in ARRAY functions must be typed/recorded like equivalent predicand/column contexts.
   - For mutator families, include unhappy unresolved/missing-alias controls for each eligible argument position that accepts predicand/column expressions.
4. **Placement coverage contract**
   - Minimum sites per function family: SELECT, WHERE, JOIN ON, HAVING, QUALIFY, ORDER BY, VALUES, INSERT, UPDATE, DELETE, DDL, SCRIPT.
5. **Validation gates**
   - Focused class-level runs per slice while implementing.
   - Slice closeout requires `mvn -Psmoketest-quality-gate test`.
   - Entry 4 closeout requires focused bundle + parser baseline/full gate per current release protocol.
6. **Per-function full matrix mandate (Entry 4 array families)**
   - Do not accept family-level spot checks as closure evidence.
   - Each targeted array function in the active slice must independently satisfy the full matrix:
     clause placement (SELECT/WHERE/JOIN ON/HAVING/QUALIFY/ORDER BY/VALUES/INSERT/UPDATE/DELETE/DDL/SCRIPT),
     nested placement controls, column-resolution matrix (qualified/unqualified/unresolved/ambiguous/missing-alias),
     substitution-variable coverage (including qualified column-variable typing checks where table prefixes are present), and positional diagnostics.
   - For Slice C 4.7 specifically, this mandate applies function-by-function to:
     `ARRAY_CAT`, `ARRAY_EXCEPT`, `ARRAY_INTERSECTION`, and `ARRAYS_OVERLAP`.
   - For Slice C 4.8 mutators, this mandate applies function-by-function to:
     `ARRAY_APPEND`, `ARRAY_PREPEND`, `ARRAY_INSERT`, `ARRAY_REMOVE`, and `ARRAY_REMOVE_AT`.

### Entry 4 first-pass AR ID allocation (implementation starter)

Use these IDs as the execution backbone; keep one-query-per-method and map each
completed test method back to its AR ID in comments or method names.

#### AR-C (core/placement) — AR-C01..AR-C40

| ID range | Scenario block | Primary host test class |
|---------|-----------------|--------------------------|
| **AR-C01..AR-C12** | `ARRAY_CONSTRUCT` full placement matrix (SELECT/WHERE/JOIN/HAVING/QUALIFY/ORDER BY/VALUES/INSERT/UPDATE/DELETE/DDL/SCRIPT) | `SqlEventWalkerSnowflakeArrayConstructTests` |
| **AR-C13..AR-C18** | `ARRAY_CONSTRUCT` core expression shapes (mixed args, repeated refs, CASE nesting, derived-table consumer, substitution args) | `SqlEventWalkerSnowflakeArrayConstructTests` |
| **AR-C19..AR-C24** | Array literal value expressions `[expr,...]` across core SQL/DML placements (paired with existing subscript controls) | `SqlEventWalkerArraySubscriptTests` (or dedicated `ArrayLiteral` class if split) |
| **AR-C25..AR-C29** | `ARRAY_CONTAINS` + VARIANT argument-shape happy paths + placement representatives | `SqlEventWalkerFunctionsAggregatesWindowingTests` (or dedicated array function class) |
| **AR-C30..AR-C34** | `ARRAY_UNION_AGG` / `ARRAY_UNIQUE_AGG` aggregate placement + lineage paths | `SqlEventWalkerWithinGroupOrderedAggregateTests` / dedicated aggregate array class |
| **AR-C35..AR-C37** | Mutator family (`APPEND`/`PREPEND`/`INSERT`/`REMOVE`/`REMOVE_AT`) representative placements | dedicated array function class |
| **AR-C38..AR-C40** | Transform/unary/conversion/predicate representatives (`ARRAY_TO_STRING`, `ARRAY_DISTINCT`, split/convert/predicate) | dedicated array function class |

#### AR-M (malformed/diagnostics) — AR-M01..AR-M24

| ID range | Scenario block | Primary host test class |
|---------|-----------------|--------------------------|
| **AR-M01..AR-M08** | Syntax controls for malformed `ARRAY_CONSTRUCT` / array literal forms (missing paren/comma, trailing comma, malformed literal list) with full syntax diagnostics | `SqlEventWalkerSnowflakeArrayConstructTests` + array literal class |
| **AR-M09..AR-M14** | Column resolution diagnostics (unresolved, ambiguous, hidden alias) for constructor/aggregate/mutator families | family-specific array test classes |
| **AR-M15..AR-M19** | Dialect diagnostics (Snowflake warnings, mixed-dialect fatal pairings, SCRIPT statement-scoped warning reset) | `SqlEventWalkerSnowflakeArrayConstructTests` + function-family classes |
| **AR-M20..AR-M24** | Signature/arity/type-shape diagnostics for transform/unary/conversion/predicate families | dedicated array function class |

For Slice D, T021's [explicit AR-M reconciliation](slice-d-array-function-reconciliation.md#ar-m20ar-m24-reconciliation)
supplies the signature-policy evidence. Historical D2.3/D2.4 use of these IDs
for resolution/dialect controls remains historical, not proof of native
signature validation. T021 preserves generic/schema-qualified compatibility
and leaves runtime arity/type/value certification to the database.

#### AR-R (regression/deep integration) — AR-R01..AR-R20

| ID range | Scenario block | Primary host test class |
|---------|-----------------|--------------------------|
| **AR-R01..AR-R06** | Nested/derived/CTE alias propagation for constructor + aggregate outputs | `SqlEventWalkerSnowflakeArrayConstructTests` / `SqlEventWalkerWithinGroupOrderedAggregateTests` |
| **AR-R07..AR-R10** | Set-op branch isolation and output-interface propagation for array-family outputs | `SqlEventWalkerSetOpScopingGateTests` + family classes |
| **AR-R11..AR-R14** | Multi-statement SCRIPT reset semantics for array warnings/diagnostics | family-specific array classes |
| **AR-R15..AR-R17** | `FLATTEN`/`LATERAL FLATTEN` advanced parity (scope, alias visibility, lineage) | `SqlEventWalkerTableFunctionTests` |
| **AR-R18..AR-R20** | Compound integration controls (constructor+aggregate+transform compositions) | `SqlEventWalkerWithinGroupOrderedAggregateTests` + dedicated array function class |

#### Slice-to-ID mapping (first pass)

| Slice | ID allocation |
|------|---------------|
| **A (4.2 + 4.4)** | AR-C01..AR-C24, AR-M01..AR-M12, AR-R01..AR-R04 |
| **B (4.3 + 4.6)** | AR-C30..AR-C34 (+ existing 4.3 coverage), AR-M09..AR-M14, AR-R05..AR-R10 |
| **C (4.7 + 4.8)** | AR-C35..AR-C37, AR-M20..AR-M22, AR-R11..AR-R12 |
| **D (consolidated 4.1–4.12 function closure)** | Original AR-C38..AR-C40, AR-M20..AR-M24, AR-R18..AR-R20; transferred AR-R15..AR-R17; constructor/contains/higher-order and A/B/C evidence mapped per function by T032..T034 |
| **E (former 4.5 + 4.12 owner)** | Requirements transferred to T027..T031; no separate deferred function queue and no duplicate completion credit |
| **F (closeout)** | Full re-run + matrix accounting over all AR-C/AR-M/AR-R IDs |

#### Tentative test method naming prefixes (first PR convention)

Method-name template:

- `arC##_shortScenario_V#Test`
- `arM##_shortScenario_V#Test`
- `arR##_shortScenario_V#Test`

Where:

- `##` is zero-padded matrix ID number (e.g., `01`, `07`, `20`)
- `shortScenario` is stable, human-readable scenario text
- `V#` is the local variant counter in that class (`V0`, `V1`, ...)

Suggested host/class prefix mapping:

| Host class | Prefix family |
|-----------|----------------|
| `SqlEventWalkerSnowflakeArrayConstructTests` | `arC01..arC18`, `arM01..arM08`, `arM15..arM19`, `arR01..arR04` |
| `SqlEventWalkerArraySubscriptTests` *(or split literal class)* | `arC19..arC24`, `arM01..arM08` (literal-specific rows) |
| `SqlEventWalkerWithinGroupOrderedAggregateTests` | `arC30..arC34`, `arR05..arR10`, `arR18..arR20` |
| `SqlEventWalkerTableFunctionTests` | `arR15..arR17` |
| dedicated array-family classes (4.7–4.11) | `arC35..arC40`, `arM09..arM14`, `arM20..arM24`, `arR11..arR14` |

Examples:

- `arC01_arrayConstructSelectPlacement_V0Test`
- `arM07_arrayLiteralTrailingCommaSyntax_V0Test`
- `arR09_arrayAggSetOpBranchIsolation_V0Test`

### Entry 4 checkpoint (2026-11-15)

The codebase already contains ARRAY-adjacent capabilities that were not reflected
in the earlier "Not started" status:

- **Delivered baseline A:** postfix subscript `expr[index]` parsing + AST/walker
  support (see `value_expression_primary_suffix` and
  `SqlEventWalkerArraySubscriptTests`).
- **Delivered baseline B:** `ARRAY_AGG` function parsing and ordered-aggregate
  grammar path (`WITHIN GROUP`) with test coverage in aggregate suites.
- **Delivered baseline C:** `FLATTEN` / `LATERAL FLATTEN` table-function support
  with parser-access and symbol-table coverage.

These baselines do **not** close the full Snowflake ARRAY family scope below.

| Sub | Construction | Current status / closure owner | Notes / remaining decision |
|-----|--------------|---------------------|-----------------------------|
| **4.1** | `ARRAY_CONTAINS` + VARIANT syntax | Complete, including T023/T024 | 74 independent cases, including both operands, VARIANT casts and substitutions, all twelve placements and twelve dual-snapshot snippet endpoints. Postfix cast typing loss repaired at suffix construction. |
| **4.2** | `ARRAY_CONSTRUCT` (compact / structured) | Complete: GC1 / T025, T032–T034 and T023/T024 | Structured constructor retains its independent 83-case matrix and lexical/manual gate. Plain/compact named cases and shared constructor/snippet coverage are accepted under the user-approved family policy. Final constructor host: 114 passed. |
| **4.3** | `ARRAY_AGG` + `WITHIN GROUP` / `OVER` | Complete | `SqlEventWalkerWithinGroupOrderedAggregateTests` now includes full location/resolution matrix coverage plus set-op + compound-array integration controls (Entry 19.3 closure). |
| **4.4** | Array literals and subscripts | Complete (Slice A) | `expr[n]` subscript is delivered. Array literal value-expression baseline is delivered (`[expr, ...]` parses as `array_construct`) with warning/resolution and malformed trailing-comma controls, and no regressions in subscript behavior under focused/full gates. |
| **4.5** | Scalar `ARRAY_FLATTEN`; table `FLATTEN` | T027/T028 complete | Scalar 54-case and table 56-case matrices complete. `LATERAL_FLATTEN` compatibility is deferred beyond 5.1.4; no new explicit LATERAL examples or lexer changes. |
| **4.6** | `ARRAY_UNION_AGG` / `ARRAY_UNIQUE_AGG` | Complete (Slice B) | Lexer tokens + aggregate grammar hook are active, walker records Snowflake statement-dialect affinity for set-aggregate usage, and strict placement/diagnostic/set-op/script controls are covered in `SqlEventWalkerSnowflakeArraySetAggregateTests`. |
| **4.7** | Binary / set array operations | Complete: Slice C, accepted family consolidation and T023/T024 | Named/sibling-family witnesses cover `ARRAY_CAT` / `ARRAY_EXCEPT` / `ARRAY_INTERSECTION` / `ARRAYS_OVERLAP`, shared routing, operand resolution and substitution typing. Final binary/set host: 14 passed; not four invented full matrices. |
| **4.8** | `APPEND` / `PREPEND` / `INSERT` / `REMOVE` / `REMOVE_AT` | Complete: Slice C, accepted family consolidation and T023/T024 | Named/sibling-family witnesses cover shared mutator routing, each eligible missing-alias operand and substitution typing, including INSERT's third operand. Final mutator host: 22 passed. |
| **4.9** | Transforms with extra arguments (`ARRAY_TO_STRING`, `ARRAY_SLICE`, `ARRAY_REPEAT`) | Complete: D1, original audits and T023/T024 | Audited counts: 50/60/55; native arity and permissive T/generic controls distinguished. Historical T022 host: 1,033; current expanded host: 1,087 passed in the full default run. |
| **4.10** | Unary array argument routes (`ARRAY_DISTINCT`, `ARRAY_COMPACT`, `ARRAY_SIZE`, `ARRAY_SORT`, `ARRAY_MAX`, `ARRAY_MIN`, `ARRAY_REVERSE`, `ARRAY_SUM`) | Complete: matrices, original audits and T023/T024 | Counts: 35/35/37/55/33/33/33/33. T022's operand/NULL/name-neutrality and optional SORT flag evidence retained. ARRAY_SUM remains a structured-array aggregate; U describes parser shape only. Current expanded host: 1,087 passed. |
| **4.11** | Conversion/predicate/split/range/position/zip (`SPLIT_TO_ARRAY`, `STRTOK_TO_ARRAY`, `ARRAY_GENERATE_RANGE`, `ARRAY_POSITION`, `ARRAYS_ZIP`, `ARRAYS_TO_OBJECT`, `TO_ARRAY`, `AS_ARRAY`, `IS_ARRAY`, `SPLIT`) | Complete: matrices, original audits and T023/T024 | Counts: 58/58/87/57/95/48/39/36/41/55. Eligible operand, ZIP variadic, ARRAYS_TO_OBJECT, optional/generic and runtime-boundary evidence retained. Current expanded host: 1,087 passed. |
| **4.12** | `FILTER` / `TRANSFORM` / `REDUCE` higher-order | Complete; D5.5..D5.7 / T029..T031 | Independent 71/71/82-case matrices; typed/local/nested bindings, external lineage, substitutions, twelve placements and twelve snippet endpoints per function. Focused selection: 1,688 passed. Runtime evaluation/type coercion remains outside the parser contract. |

T032–T034 now consolidate all function rows: retain the original 21-name
audit and seven added matrices; accept the 14 carried-forward names' named
tests plus identified sibling-family coverage. T023's full default accuracy
run passed and T024 accounting is complete. No Slice D closeout work remains.

**Slice C closeout record (2026-11-17):**
- Focused family suites green: `SqlEventWalkerSnowflakeArraySetBinaryTests`, `SqlEventWalkerSnowflakeArrayMutatorTests`, and `SqlEventWalkerSnowflakeArraySetAggregateTests`.
- Regression gates green: `sql.walker.Phase15DerivedColumnGateTestSuite` and `sql.walker.SmoketestQualityGateTestSuite`.
- Parser Java suite rerun (excluding bound-query/snippet assessment classes by operator request) green with zero failures.

**Slice D kickoff record (2026-11-17):**
- D1 (`4.9`) initiated with dedicated grammar/walker route for `ARRAY_TO_STRING` and new focused suite scaffold: `SqlEventWalkerSnowflakeArrayTransformTests`.
- Initial focused validation green: `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerSnowflakeArrayTransformTests test` (3/3 passing).

**Slice D D2 sequence opened (2026-11-17):**
- Added explicit D2.1→D2.5 safe bounded implementation sequence for `4.10` unary transforms in this workplan.
- D2.1 started: seed unary route and strict `ARRAY_DISTINCT` seed tests in `SqlEventWalkerSnowflakeArrayTransformTests`.
- D2.2 complete: unary route expanded to `ARRAY_COMPACT` + `ARRAY_SIZE` with strict per-function matrix closure across `SELECT`/`WHERE`/`ORDER BY`/`VALUES`/`INSERT`/`UPDATE`/`DELETE`, including unresolved/missing-alias controls by eligible argument position.
- D2.2 validation ladder complete and green:
  - `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerSnowflakeArrayTransformTests test`
  - `mvn -pl parse -Dtest=sql.walker.Phase15DerivedColumnGateTestSuite test`
  - `mvn -pl parse -Dtest=sql.walker.SmoketestQualityGateTestSuite test`
  - `mvn -pl parse test`

**Slice D D2.3 completion record (2026-11-18):**
- Added the existing `ARRAY_SORT`, `ARRAY_MAX`, and `ARRAY_MIN` tokens to the unary transform name rule; reused the existing unary walker and registered all three names for Snowflake statement affinity. No lexer entries or token IDs changed.
- Added 78 one-query-per-method tests in `SqlEventWalkerSnowflakeArrayTransformTests`: 28 for sort and 25 each for max/min. All 84 pre-existing tests and their goldens are unchanged.
- Each function independently covers SELECT, WHERE, JOIN ON, HAVING, QUALIFY, ORDER BY, VALUES, INSERT, UPDATE, DELETE, DDL, and SCRIPT. Each also covers nested expressions, derived-output consumption, set-op sibling isolation, qualified/unqualified/unresolved/ambiguous/missing/hidden aliases, and predicand versus qualified-column substitutions.
- All new tests assert six complete extractor goldens and the exact diagnostic list (including full messages, severity, code, line/column positions, and count). SCRIPT controls assert Snowflake/ANSI/Snowflake statement reset; schema-qualified UDF controls remain dialect-neutral; mixed PostgreSQL/Snowflake controls assert the fatal diagnostic.
- `ARRAY_SORT` additionally preserves its existing generic two-/three-argument forms, including descending/null-ordering flags and substitution typing for optional arguments.
- Traceability: AR-C38/39/40 identify sort/max/min core cases; AR-M20/21/22/23/24 cover unresolved/ambiguous/missing-or-hidden/schema-qualified/mixed-dialect controls; AR-R18/19/20 cover nesting-and-derived/set-op/SCRIPT controls.
- Validation: the three initial routing tests failed before implementation and passed afterward. `mvn -f parse/pom.xml -Pisolated-build -Dtest=SqlEventWalkerSnowflakeArrayTransformTests -Dpss.walker.test.verbose=false test` passed **162/162**, with zero failures, errors, or skips. VS Code's test runner independently passed the same **162** tests.
- Scope remains D2.3 only. D2.4 (`ARRAY_REVERSE`/`ARRAY_SUM`), D2.5, and the Slice D sign-off ladder are not claimed complete.

**Slice D D2.4 completion record (2026-11-18):**
- Added the already-declared `ARRAY_REVERSE` and `ARRAY_SUM` tokens to the unary transform name rule and registered their Snowflake affinity, reusing the existing unary walker. Added both names to the nonreserved-keyword rule, consistent with the other array functions, so schema-qualified UDFs and ordinary column names remain supported. Lexer declarations and token IDs are unchanged.
- Added 54 one-query-per-method tests in `SqlEventWalkerSnowflakeArrayTransformTests`, 27 per function. All 162 pre-existing tests and their goldens, including the D2.3 additions, are unchanged.
- Each function independently covers SELECT, WHERE, JOIN ON, HAVING, QUALIFY, ORDER BY, VALUES, INSERT, UPDATE, DELETE, DDL, and SCRIPT; nested expressions, derived-output consumption, and set-op sibling isolation; qualified/unqualified/unresolved/ambiguous/missing/hidden aliases; and predicand versus qualified-column substitutions.
- Every new test asserts six complete extractor goldens and the exact diagnostic list, including full messages, severity, code, line/column positions, and count. Additional controls cover uppercase calls, NULL operands, dialect-neutral schema-qualified UDFs and same-named columns, mixed PostgreSQL/Snowflake diagnostics, and Snowflake/ANSI/Snowflake SCRIPT reset.
- Traceability: AR-C39/40 identify the reverse/sum core cases; AR-M20..24 identify resolution and dialect controls; AR-R18..20 identify nesting/derived, set-op, and SCRIPT controls. Function-name suffixes distinguish these cases from D2.3.
- Validation: both new seed tests failed before implementation and passed afterward. `mvn -f parse/pom.xml -Pisolated-build -Dtest=SqlEventWalkerSnowflakeArrayTransformTests -Dpss.walker.test.verbose=false test` passed **216/216**, with zero failures, errors, or skips. VS Code's test runner independently passed the same **216** tests. The complete lexer-rule section matches the committed baseline; generated lexer constants remain `ARRAY_REVERSE=452`, `ARRAY_SUM=453`, `ARRAYS_TO_OBJECT=454`, and `Identifier=455`.
- This completes D2.4 and the GC2 unary portion, not all GC2 work. `ARRAY_REPEAT` remains open in 4.9; D2.5 and the broad Slice D sign-off ladder are not claimed complete.

**Slice D D2.5 focused closeout record (2026-11-18):**
- Confirmed all eight 4.10 unary function names are present in the dedicated unary grammar route and shared Snowflake affinity registry.
- Reran `mvn -f parse/pom.xml -Pisolated-build -Dtest=SqlEventWalkerSnowflakeArrayTransformTests -Dpss.walker.test.verbose=false test`: **216 passed**, zero failures, errors, or skips.
- Independently reran the same focused class through VS Code's test runner: **216 passed**, zero failures.
- Existing strict extractor goldens and diagnostic assertions pass unchanged; no golden reconciliation, lexer/token-ID changes, or additional production-code changes were needed for D2.5.
- D2 focused closeout is complete. This is not a full parser/release sign-off: Phase15, Smoketest, and the full parser ladder were deliberately not run, as required by the bounded sequence.
- Remaining Slice D work includes `ARRAY_REPEAT` under 4.9, D3.6/D3.7 for `ARRAYS_TO_OBJECT`, and the final Slice D sign-off ladder.

**Slice D D3 kickoff record (2026-11-17):**
- D3.1 complete: locked explicit 4.11 target set (`SPLIT_TO_ARRAY`, `STRTOK_TO_ARRAY`, `ARRAY_GENERATE_RANGE`, `ARRAY_POSITION`, `ARRAYS_ZIP`, `ARRAYS_TO_OBJECT`) and fixed signature-family map (conversion/range/position/zip/object conversion) before code-routing work.
- D3.2 complete: seeded dedicated grammar routes for representative conversion/range/position/zip functions (`SPLIT_TO_ARRAY`, `ARRAY_GENERATE_RANGE`, `ARRAY_POSITION`, `ARRAYS_ZIP`) with explicit narrow routing and no broad grammar refactor.
- D3.3 complete: added seed walker handling for the same representative conversion/range/position/zip routes, preserving routine-style argument mapping and Snowflake dialect affinity warning behavior.
- D3.4 complete: added strict seed tests (happy + eligible missing-alias fatal controls) for `SPLIT_TO_ARRAY`, `ARRAY_GENERATE_RANGE`, `ARRAY_POSITION`, and `ARRAYS_ZIP` in `SqlEventWalkerSnowflakeArrayTransformTests`; focused transform suite green.
- D3.5 started: completed strict `SPLIT_TO_ARRAY` placement matrix (`WHERE`/`ORDER BY`/`VALUES`/`INSERT`/`UPDATE`/`DELETE`) with full goldens + diagnostics in `SqlEventWalkerSnowflakeArrayTransformTests`; focused transform suite green.
- D3.5 advanced: completed strict `ARRAY_POSITION` placement matrix (`WHERE`/`ORDER BY`/`VALUES`/`INSERT`/`UPDATE`/`DELETE`) with full goldens + diagnostics in `SqlEventWalkerSnowflakeArrayTransformTests`; focused transform suite green.
- D3.5 complete: completed strict placement matrix closure for `ARRAY_GENERATE_RANGE`, `ARRAYS_ZIP`, and routed `STRTOK_TO_ARRAY` (including seed + missing-alias fatal + placement matrix), with focused transform suite green (`mvn -pl parse -Dtest=sql.walker.SqlEventWalkerSnowflakeArrayTransformTests test`).

**Slice D D3.6 completion record (2026-11-18):**
- Added the already-declared `ARRAYS_TO_OBJECT` token to the explicit conversion-family name rule and nonreserved-keyword list. Reused the existing conversion walker, ordered argument-list handling, and existing Snowflake registry entry; no duplicate walker or dialect logic was introduced.
- Added 40 one-query-per-method cases in `SqlEventWalkerSnowflakeArrayTransformTests`: 38 tests assert all six complete extractor goldens and the exact diagnostic list; two parser-only syntax tests assert exact error counts and full positioned diagnostics for missing/trailing commas without walking rejected syntax trees.
- Independently covered SELECT, WHERE, JOIN ON, HAVING, QUALIFY, ORDER BY, VALUES, INSERT, UPDATE, DELETE, DDL, and SCRIPT; nested expressions, derived-output consumption, and set-op sibling isolation; and qualified/unqualified operands.
- Both key-array and value-array positions independently cover unresolved, ambiguous, missing/hidden aliases, predicand substitutions, qualified-column substitutions, and NULL operands. Combined column-variable, array-literal, NULL key/value element, uppercase, schema-qualified UDF, same-named column, mixed-dialect, and SCRIPT-reset controls are also included.
- Traceability: AR-C40 core/placement and argument-shape cases; AR-M20..24 resolution/dialect/syntax controls; AR-R18..20 nesting/derived, set-op, and SCRIPT controls.
- The parser records argument expressions; it does not execute the function or validate runtime key types, array lengths, or resulting object contents.
- Validation: the initial routing test failed before implementation and passed afterward. `mvn -f parse/pom.xml -Pisolated-build -Dtest=SqlEventWalkerSnowflakeArrayTransformTests -Dpss.walker.test.verbose=false test` passed **256/256**, zero failures/errors/skips; VS Code independently passed **256** tests. All 216 existing test methods/goldens are unchanged.
- Lexer declarations match the committed baseline; generated constants remain `ARRAYS_TO_OBJECT=454` and `Identifier=455`. D3.7 is deliberately still open, per the requested D3.6-only scope; no Phase15/Smoketest/full parser ladder was run.

**Slice D D3.7 focused closeout record (2026-11-18):**
- Confirmed `SqlEventWalkerSnowflakeArrayTransformTests` is the focused family host for all six D3 functions: `SPLIT_TO_ARRAY`, `STRTOK_TO_ARRAY`, `ARRAY_GENERATE_RANGE`, `ARRAY_POSITION`, `ARRAYS_ZIP`, and `ARRAYS_TO_OBJECT`.
- Fresh `mvn -f parse/pom.xml -Pisolated-build -Dtest=SqlEventWalkerSnowflakeArrayTransformTests -Dpss.walker.test.verbose=false test` completed successfully (exit code 0): **256 passed**, zero failures, errors, or skips.
- Independently reran the same focused class through VS Code's test runner: **256 passed**, zero failures.
- Full literal extractor goldens and positioned diagnostic assertions passed unchanged. D3.7 required no test/golden reconciliation, new tests, production-code changes, or lexer/token-ID changes.
- D3 focused closeout and GC3 focused accounting are complete, not full parser/release sign-off. Phase15, Smoketest, and the full parser ladder were deliberately not run.
- Remaining Slice D work is `ARRAY_REPEAT` under 4.9/GC2, followed by the single final Slice D sign-off ladder.

**Slice D ARRAY_REPEAT implementation checkpoint (2026-11-18; incomplete):**
- Historical single-addition checkpoint: its +1 map below predates `SPLIT`.
  For current manual repairs, use the subsequent SPLIT checkpoint instead;
  do not apply this historical map as well as the cumulative map.
- Added `ARRAY_REPEAT` immediately before `Identifier`, included it in the existing multi-argument transform route and nonreserved-keyword list, and registered Snowflake affinity. The existing transform walker is reused unchanged.
- The new routing seed failed before implementation (expected one dedicated transform node, observed zero) and passed afterward.
- The strict per-function matrix is not yet generated. No existing golden strings were rewritten. Final Slice D sign-off has not run and is blocked on matrix completion and the developer's manual token-ID repairs.
- The preceding D2/D3 passing counts describe runs before this lexer addition; they are not a claim that the current full suite is green.
- Official generated Java lexer snapshots confirm one new token, `ARRAY_REPEAT=455`, and a uniform **+1** shift for all 21 following tokens. All earlier token IDs are unchanged. Apply this complete manual worklist from highest ID downward:

| Token | Before | After | Delta |
|---|---:|---:|---:|
| BAD | 475 | 476 | +1 |
| RIGHT_BRACKET | 474 | 475 | +1 |
| Bracket_Identifier | 473 | 474 | +1 |
| JINJA_CLOSE | 472 | 473 | +1 |
| JINJA_OPEN | 471 | 472 | +1 |
| NOTHING | 470 | 471 | +1 |
| DO | 469 | 470 | +1 |
| CONSTRAINT | 468 | 469 | +1 |
| CONFLICT | 467 | 468 | +1 |
| White_Space | 466 | 467 | +1 |
| Space | 465 | 466 | +1 |
| Pivot_Identifier | 464 | 465 | +1 |
| Character_String_Literal | 463 | 464 | +1 |
| LineComment | 462 | 463 | +1 |
| BlockComment | 461 | 462 | +1 |
| Dollar_Sign_Identifier | 460 | 461 | +1 |
| Double_Quoted_Numeric_Identifier | 459 | 460 | +1 |
| Numeric_Identifier | 458 | 459 | +1 |
| Scientific_Numeric_Literal | 457 | 458 | +1 |
| EXPONEN | 456 | 457 | +1 |
| Identifier | 455 | 456 | +1 |

**Slice D SPLIT lexer checkpoint (2026-11-18; D0.1..D0.4 complete):**
- Added only `SPLIT`, after `ARRAY_REPEAT` and immediately before `Identifier`,
  plus nonreserved-name handling to retain generic routine/identifier use.
  Dedicated routing, diagnostics and matrix work were pending at this checkpoint
  and are now complete under D3.8.4 / T015.
- Captured official generated Java constants before/after this addition.
  The before constants exactly match the retained ARRAY_REPEAT after image.
  Reused the retained official pre-ARRAY_REPEAT image for the last repaired
  baseline (`afa2196`). Ran `tools/token_id_worklist_from_vocab.py` against
  both baselines and verified every existing constant, not just emitted rows.
- New token: `SPLIT=456`. `ARRAY_REPEAT=455` and every earlier ID are unchanged
  by this step. Exactly 21 existing tokens shift: +1 incrementally, or +2
  cumulatively since the last repaired baseline.
- Both complete maps appear below, descending by final ID. Choose the source
  column matching each golden's baseline and replace directly with the final ID;
  **do not apply both maps**. The first source column is the appropriate one
  for goldens still awaiting both ARRAY_REPEAT and SPLIT repairs.
  Calls/names spelled `SPLIT` previously lexed as `Identifier`; those occurrences
  now use `SPLIT=456`, not the shifted ordinary `Identifier=457`. Replacements
  must remain token-aware, not blanket numeric substitutions.

| Token | Last repaired (`afa2196`) | Before SPLIT (`5604d3c`) | Final after SPLIT |
|---|---:|---:|---:|
| BAD | 475 | 476 | 477 |
| RIGHT_BRACKET | 474 | 475 | 476 |
| Bracket_Identifier | 473 | 474 | 475 |
| JINJA_CLOSE | 472 | 473 | 474 |
| JINJA_OPEN | 471 | 472 | 473 |
| NOTHING | 470 | 471 | 472 |
| DO | 469 | 470 | 471 |
| CONSTRAINT | 468 | 469 | 470 |
| CONFLICT | 467 | 468 | 469 |
| White_Space | 466 | 467 | 468 |
| Space | 465 | 466 | 467 |
| Pivot_Identifier | 464 | 465 | 466 |
| Character_String_Literal | 463 | 464 | 465 |
| LineComment | 462 | 463 | 464 |
| BlockComment | 461 | 462 | 463 |
| Dollar_Sign_Identifier | 460 | 461 | 462 |
| Double_Quoted_Numeric_Identifier | 459 | 460 | 461 |
| Numeric_Identifier | 458 | 459 | 460 |
| Scientific_Numeric_Literal | 457 | 458 | 459 |
| EXPONEN | 456 | 457 | 458 |
| Identifier | 455 | 456 | 457 |

- Validation: JDK 21 isolated-build `mvn -f parse/pom.xml -Pisolated-build
  -DskipTests compile` passed; the editor reports no grammar errors.
  No existing tests/goldens were edited and no parser test suite was run.
  Stop at D0.5 for developer manual repairs; compilation is not accuracy sign-off.

**Slice D D1.1 ARRAY_REPEAT completion record (2026-11-18):**
- Replaced the route-only seed with 48 independent cases: 45 assert all six
  complete extractor outputs and exact walker diagnostics; three assert exact
  parser error counts and full parser diagnostic lists without walking rejected
  syntax trees. Net addition: 47 methods; focused host total: 304.
- Covered all twelve placements, both arguments' isolated unqualified-success,
  unresolved/ambiguous/missing-alias/hidden-alias and variable-typing controls,
  combined variables, nested expressions, derived exports, set-op sibling
  isolation, Snowflake/ANSI/Snowflake SCRIPT reset, qualified-UDF/column
  neutrality, uppercase and mixed-dialect controls.
- Included scalar/array/NULL element forms and NULL/zero/negative count inputs.
  These assert parsing/lineage, not evaluated array values or runtime types.
  The shared transform rule and generic-call signature boundary are unchanged;
  D-SIG remains pending.
- Generated only new expectations against the repaired token baseline, audited
  them using the consumer bucket/dictionary contracts, and preserved all 256
  other test methods/goldens exactly. No grammar, lexer, registry or walker edit
  was required: this was coverage-only work, passing on existing production code.
- Validation command: `JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home/bin:$PATH mvn -f parse/pom.xml -Pisolated-build -Dtest=SqlEventWalkerSnowflakeArrayTransformTests -Dpss.walker.test.verbose=false -Dpss.test.slow=false -Dpss.test.diagnostics=false test`.
  Result: **304 passed**, zero failures/errors/skips, exit code 0.
  The editor reports only the three previously known unused `snippet` locals;
  no new compilation problem was introduced.
- Next: D1.2.1 `ARRAY_TO_STRING`. The ten older remediations, four D3.8
  implementations, D-SIG/D-AUDIT and broad final gates remain open.

**Slice D D1.2.1 ARRAY_TO_STRING completion record (2026-11-18):**
- Added 30 independent cases and strengthened the 15 existing methods to compare
  their complete diagnostic lists. The function now has 45 cases: 42 six-output
  goldens and three parser-only syntax controls. Preserved all 15 original
  extractor golden blocks and all 289 unrelated methods, including the complete
  D1.1 `ARRAY_REPEAT` matrix, byte-for-byte.
- Filled JOIN ON/HAVING, isolated argument resolution and variable typing,
  nested/derived/set-op lineage, and Snowflake/ANSI/Snowflake SCRIPT reset.
  Added uppercase/mixed-dialect, qualified-UDF/column neutrality, NULL operands,
  array-with-NULL/empty-array and empty-separator inputs. All twelve placements
  have named independent evidence in the reconciliation.
- The new qualified-UDF control initially failed with three parser errors:
  `ARRAY_TO_STRING` was missing from `nonreserved_keywords`. Added only that
  parser alternative, consistent with the other array names. Both the
  schema-qualified UDF and same-named column now parse without Snowflake
  diagnostics. The generated Java lexer is byte-for-byte unchanged; no token
  replacement or existing golden refresh was needed.
- Generated only new extractor expectations and exact diagnostic literals,
  audited derived exports, physical/query dictionaries, sibling isolation and
  predicand/column typing against the consumer contracts, then froze them.
  The documented signature is `ARRAY_TO_STRING(array, separator_string)`;
  runtime input types/results and shared-rule/generic-call arity policy remain
  outside this batch under D-SIG.
- Validation command: `JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home/bin:$PATH mvn -f parse/pom.xml -Pisolated-build -Dtest=SqlEventWalkerSnowflakeArrayTransformTests -Dpss.walker.test.verbose=false -Dpss.test.slow=false -Dpss.test.diagnostics=false test`.
  Result: **334 passed**, zero failures/errors/skips, exit code 0. The host now
  has 326 six-output cases and eight parser-only syntax controls. The editor
  reports only the same three pre-existing unused `snippet` locals.
- Next: D1.2.2 `ARRAY_SLICE`. Nine older matrices, four D3.8 implementations,
  D-SIG/D-AUDIT and the broad final Slice D gates remain open.

**Slice D D1.2.2 ARRAY_SLICE completion record (2026-11-18):**
- Added 43 cases and strengthened all 12 older diagnostic lists: 55 independent
  cases, comprising 52 six-output goldens and three parser-only syntax controls.
  Existing golden blocks and all 322 unrelated methods, including both prior
  completed transform matrices, are unchanged.
- Closed all twelve placements and isolated all three arguments' resolution,
  raw/column-variable and NULL controls; included nested/derived/set-op/SCRIPT
  reset, neutrality, dialect/case, empty-array and index-bound inputs.
  The reconciliation maps every method. Bounds/results are not evaluated;
  shared-rule/generic-call arity policy remains under D-SIG.
- No production or lexer changes. Generated expectations passed on the current
  implementation; no artificial red phase. One full focused run:
  `JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home/bin:$PATH mvn -f parse/pom.xml -Pisolated-build -Dtest=SqlEventWalkerSnowflakeArrayTransformTests -Dpss.walker.test.verbose=false -Dpss.test.slow=false -Dpss.test.diagnostics=false test`
  passed **377/377**, zero failures/errors/skips, exit 0.
- Next at that checkpoint: D2.6.1 `ARRAY_DISTINCT`; eight older matrices, four D3.8 implementations,
  D-SIG/D-AUDIT and broad final gates remain open.

**Slice D D2.6 unary completion record (2026-11-18):**
- T009/T010/T011 complete: `ARRAY_DISTINCT`, `ARRAY_COMPACT` and `ARRAY_SIZE`
  each have 31 independent cases, including 29 six-output cases and two exact
  parser-only syntax controls. Generated missing cells from one signature
  catalog, reviewed new scope/lineage/diagnostic expectations, and preserved all
  original extractor goldens plus 359 unrelated methods.
- All twelve placements, operand resolution/variables, nested/derived/set-op,
  SCRIPT reset, neutral UDF/column, case/mixed-dialect and NULL/empty/literal
  controls are mapped by generated method-level evidence in the reconciliation.
  Arrays are parsed, not executed; global arity/type policy stays under D-SIG.
- Coverage passed against existing production code; no artificial red phase,
  production changes, lexer changes or token-ID migration. One shared focused
  run passed **452/452**, zero failures/errors/skips, exit 0. Host totals:
  435 six-output cases and 17 parser-only controls.
- Extended the existing toolkit to the approved three-function batch; its
  three regression tests pass. Generated task/checkpoint reports from the case
  catalog and actual fresh Surefire results.
- Next at that checkpoint: T012 / D3.8.1 `TO_ARRAY`. Five older matrices, four D3.8 implementations,
  D-SIG/D-AUDIT and final gates remain open; this is not whole-release sign-off.

**Slice D D3.8.1-D3.8.3 conversion/type-inspection completion (2026-11-18):**
- T012/T013/T014 complete: `TO_ARRAY` 35 cases (33 six-output, two syntax),
  `AS_ARRAY` 32 (30/two), `IS_ARRAY` 37 (35/two). All 104 methods assert exact
  diagnostics; all 452 existing methods/goldens are byte-for-byte preserved.
- Red regression: `TO_ARRAY` SELECT required one dedicated conversion route
  but found zero. Added the three existing token names to
  `array_conversion_seed_function_name` and the Snowflake routine registry;
  reused `exitArray_conversion_seed_function` without changing the walker.
  New scope/lineage, raw-versus-column variables, neutrality, SCRIPT reset and
  diagnostic goldens were reviewed before emission. One shared focused run
  passed **556/556**, zero failures/errors/skips, exit 0.
- Checked Snowflake's documented single-operand signatures:
  [TO_ARRAY](https://docs.snowflake.com/en/sql-reference/functions/to_array)
  accepts an expression of any type;
  [AS_ARRAY](https://docs.snowflake.com/en/sql-reference/functions/as_array) and
  [IS_ARRAY](https://docs.snowflake.com/en/sql-reference/functions/is_array)
  take VARIANT expressions. Added scalar/VARIANT-array/object, SQL NULL and
  JSON-null syntax controls as appropriate. These tests do not execute SQL or
  enforce database runtime types/results. The shared argument-list route and
  generic-call arity policy remain subject to D-SIG, not a new global validator.
- `IS_ARRAY` additionally covers bare WHERE/JOIN/HAVING/QUALIFY predicates and
  CASE projection. Schema-qualified UDFs and same-named columns remain neutral.
- Official generated Java lexer verified byte-for-byte unchanged: no lexer
  additions, token-ID migration or existing-golden refresh.
- Generated independent per-function evidence and task/checkpoint reports from
  the catalog and fresh Surefire results. Next at that checkpoint: T015 / D3.8.4 `SPLIT`; five
  older matrices, D-SIG/D-AUDIT and final gates remain open.

**Slice D T015-T017 string-to-array completion (2026-11-18):**
- `SPLIT`: 48 cases (45 six-output, three syntax); `SPLIT_TO_ARRAY` and
  `STRTOK_TO_ARRAY`: 53 each (50/three). Added 138 methods, strengthened all
  16 legacy diagnostic lists, and preserved old extractor goldens plus all
  540 unrelated host methods. Current host: 694 methods, 662 six-output,
  32 parser-only controls. Generated per-function evidence is in the reconciliation.
- Red route regression established that SPLIT lacked dedicated C routing.
  Added its existing token to the conversion family and its name to the
  Snowflake registry. The first composition run then exposed 11 AST
  serialization regressions: the generic route's bulk map insertion and the
  dedicated route's individual insertion yield different HashMap iteration
  order. Retained the generic construction for SPLIT only; other conversion
  functions and every existing composition golden are unchanged.
- Reviewed the corrected new SPLIT ASTs before emission; all other captured
  fields stayed identical. Final shared Maven selection passed the focused
  host **694/694**, array-subscript suite **24/24**, and table-function suite
  **78/78**: **796 total**, zero failures/errors/skips, exit 0.
- Documented signatures: Snowflake SPLIT has a required substring separator;
  STRTOK_TO_ARRAY has an optional delimiter character set, default single
  space. Amazon Redshift SPLIT_TO_ARRAY has an optional substring delimiter,
  default comma, and returns SUPER. Its existing Snowflake-affinity diagnostic
  remains compatibility behavior, not native Snowflake certification.
  Omitted delimiters stay absent from AST parameter lists; input controls
  do not execute SQL or evaluate runtime defaults/types/results.
- Both operand positions have independent resolution and variable controls;
  all twelve placements, nested/derived/set-op/SCRIPT reset, neutral UDF/name,
  NULL/empty/repeated delimiter and exact malformed-syntax controls are recorded.
  Added explicit SPLIT subscript and aliased FLATTEN cases.
- Official generated lexer is byte-for-byte unchanged. No token-ID edits or
  existing-golden refresh. Toolkit records the actual related-suite selection
  while keeping host counts separate; all three Node regression tests pass.
- Next at that checkpoint: T018 / D3.9.3 `ARRAY_GENERATE_RANGE`, then `ARRAY_POSITION` and
  `ARRAYS_ZIP`. D-SIG/D-AUDIT and final accuracy gates remain open; this
  checkpoint is not final Slice D or release sign-off.

**Slice D T018-T020 final individual-function completion (2026-11-18):**
- `ARRAY_GENERATE_RANGE`: 79 cases (76 six-output, three syntax);
  `ARRAY_POSITION`: 50 (47/three); `ARRAYS_ZIP`: 90 (87/three).
  Generated missing cells from the existing toolkit, reviewed new expectations,
  added 196 methods and strengthened all 23 original diagnostic lists.
  All existing extractor goldens and 671 unrelated methods are unchanged.
- Existing dedicated routes/walkers/affinity already satisfy the cases:
  coverage-only additions passed without artificial red tests or production
  changes. One shared focused run passed **890/890**, zero failures/errors/skips,
  exit 0. Host totals: 849 six-output cases and 41 parser-only controls.
- Range coverage independently isolates start, stop and optional step;
  adds column/variable/deep two-argument forms while retaining all seven
  original literal controls. Omitted step remains absent from the AST.
  Signed/arithmetic steps, equal/reversed bounds and NULLs are parse controls.
  Zero-step runtime rejection remains Snowflake's responsibility, not a new
  PSS value validator.
- Position coverage isolates needle and array in every resolution/variable
  mode, with VARIANT-cast literals, NULL needles/elements, empty arrays,
  first/duplicate/absent values and CASE projection. No index evaluation or
  database runtime comparability/type certification is claimed.
- Zip coverage includes one/two/three/four inputs. The main three-input
  matrix and isolated fourth-operand controls cover variadic propagation;
  both variable types are checked independently and in combined forms.
  Unequal lengths, empty arrays and NULL elements are not executed.
- Each function independently covers all twelve placements, nested/derived/
  set-op/SCRIPT reset, neutral UDF/name, dialect/case and exact malformed
  parser diagnostics. Scope/dictionary and optional-form expectations were
  checked against the established consumer contracts before emission.
- Generated independent named-method accounting and task/checkpoint reports
  from the case catalog and fresh Surefire results. No lexer/token-ID changes,
  old-golden refresh or new tooling. Next: T021 / D-SIG; T022-T024 remain open.
  This is individual-function completion, not final Slice D or release sign-off.

#### D-SIG / T021 completion record (2026-11-18)

- Reconciled all 21 official signatures, dedicated arities and generic
  fallback without changing production grammar, walkers, affinity or lexer IDs.
  Parser acceptance is not native signature/runtime certification.
- Added **134** one-query methods: **117** full six-output controls and **17**
  exact parser-only malformed cases. All **890** previous methods, including
  their extractor goldens, remain byte-for-byte unchanged.
- Each valid audit query checks one primary route and zero occurrences of
  each other family/generic route. SORT's documented two-/three-argument
  forms are explicitly generic; both optional flags receive eight independent
  resolution/substitution/NULL controls each. Empty calls and qualified UDFs
  retain their existing generic behavior.
- Closed missing exact syntax controls for SORT/MAX/MIN/REVERSE/SUM and
  added qualified malformed-call probes for all six families. Runtime
  type/value/default/length/comparability checks remain database responsibilities.
  ARRAY_SUM is documented as structured-array aggregation across rows;
  parser U routing is not a scalar-semantics claim.
- Reconciled historical AR-M20..24 resolution/dialect labels with the actual
  signature obligations. The [21-function signature ledger](slice-d-array-function-reconciliation.md#t021--d-sig-completed-signature-validation-policy)
  and generated named-method tables provide evidence without erasing history.
- Extended the existing capture/emission toolkit with per-case primary rules
  and additional exact route counts. All three toolkit regression tests pass.
  Maven passed **1,024/1,024**, zero failures/errors/skips, exit **0**, in
  23.437 seconds; VS Code independently passed **1,024/1,024**.
  Host totals: **966** six-output cases, **58** parser-only controls.
- Next: **T022 / D-AUDIT**. T023's sequential final accuracy ladder and
  T024's final accounting remain open; this is not final Slice D/release sign-off.

#### D-AUDIT / T022 completion record (2026-11-19)

- Independently mapped all **21** functions to **854/854** required cells using
  actual SQL, six complete output goldens, exact diagnostics and source-matched
  case catalogs. The [persistent cell ledger](slice-d-array-function-reconciliation.md#t022--d-audit-completed-cell-level-reconciliation)
  names a test for every cell; 277 additional catalog labels are retained as
  supporting evidence, not counted as extra independent contract cells.
- Rechecked all six older expanded matrices. Nine real gaps were repaired:
  NULL and same-named-column neutrality for SORT/MAX/MIN (six cases), plus
  independently unqualified arguments 1/2 and missing-right-parenthesis syntax
  for ARRAYS_TO_OBJECT (three). REVERSE and SUM needed no new tests.
  SORT optional operands 2/3 and ZIP's fourth operand are explicitly covered.
- Added **nine** methods: **eight** six-output and **one** exact parser-only
  case. All **1,024** previous methods, including their goldens, are unchanged.
  Totals: **1,033** methods, **974** six-output, **59** parser-only.
  Maven passed **1,033/1,033**, zero failures/errors/skips, exit **0**, in
  **27.741 seconds**; VS Code independently passed **1,033/1,033**.
- Verified exact dedicated-name ownership, all matching walker handlers and
  registry entries, unique official lexer IDs, column/UDF neutrality, unchanged
  grammar/walker/registry source and an unchanged complete generated lexer hash.
  No production changes, keyword additions, old-golden refreshes or global
  arity/type/value validator.
- Next: **T023 / D4.1**, then **T024 / D4.2**. The scoped matrix audit passes;
  final Slice D, GC2/GC3, other Entry 4 slices, whole-project build/smoke and
  release sign-off are not asserted by T022.

### Slice D D5 lexer preparation checkpoint (complete)

**2026-11-18 preparation; 2026-11-19 manual-repair sign-off. T025 lexical
portion complete.** The user requested the needed lexer
changes now, before the remaining function implementations. All **42** canonical
names were checked against official generated Java constants: only the planned
`ARRAY_CONSTRUCT_STRUCTURED` spelling lacked a function-name token.

| Outstanding function | Existing name token before this batch | Required lexer addition |
|---|---:|---|
| `ARRAY_CONSTRUCT_STRUCTURED` | None | New keyword, now 457; also added to `nonreserved_keywords` |
| `ARRAY_CONTAINS` | 350 | None |
| `ARRAY_FLATTEN` | 365 | None |
| `FLATTEN` / `LATERAL FLATTEN` | FLATTEN 339; LATERAL 385 | None; retain `LATERAL_FLATTEN` identifier compatibility |
| `FILTER` | 87 | Shared `LAMBDA_ARROW` for contiguous `->`, now 458 |
| `TRANSFORM` | 379 | Same shared `LAMBDA_ARROW`, not another token |
| `REDUCE` | 380 | Same shared `LAMBDA_ARROW`, not another token |

The official [FILTER](https://docs.snowflake.com/en/sql-reference/functions/filter),
[TRANSFORM](https://docs.snowflake.com/en/sql-reference/functions/transform) and
[REDUCE](https://docs.snowflake.com/en/sql-reference/functions/reduce) syntax
requires `->`. It previously lexed as separate MINUS/GTH tokens. Both new
entries were appended after SPLIT, immediately before Identifier; no existing
lexer entry moved. LAMBDA_ARROW deliberately stays in this append-only block,
not the earlier operator block, to avoid renumbering hundreds of earlier tokens.

Official images and generated incremental/cumulative maps are retained under
session `97c6686f-0dcd-4f49-8c59-51885ff75f2f`, `files/d5-lexer/`:
`SQLSelectParserLexer.before.java`, `SQLSelectParserLexer.after.java`,
`SQLSelectParserLexer.last-repaired.java`, `incremental-token-map.txt` and
`cumulative-token-map.txt`. Before and last-repaired images both have SHA-256
`55bce4bc7ec702db4c7c2a9c9cc0cec1fad8c86ced39a848b9b93bd5070cdf5c`;
after is `0ad82dbaf1ef656cca37b8f43f488e9139b15fa113348d2d9112fc1705ea958c`.
The repaired baseline is the retained post-SPLIT image associated with developer
repairs in `641ded6`. It matches the fresh before image byte-for-byte, so the
incremental and cumulative replacement maps are **identical**.

**Two additions => expected +2.** All **456 earlier IDs** are unchanged; all
**21 following IDs**, including skipped/comment tokens, shift exactly +2.
`tools/token_id_worklist_from_vocab.py` produced this complete manual IDE
worklist from both official Java baselines, highest new ID first:

| Token | Before | After | Delta |
|---|---:|---:|---:|
| BAD | 477 | 479 | +2 |
| RIGHT_BRACKET | 476 | 478 | +2 |
| Bracket_Identifier | 475 | 477 | +2 |
| JINJA_CLOSE | 474 | 476 | +2 |
| JINJA_OPEN | 473 | 475 | +2 |
| NOTHING | 472 | 474 | +2 |
| DO | 471 | 473 | +2 |
| CONSTRAINT | 470 | 472 | +2 |
| CONFLICT | 469 | 471 | +2 |
| White_Space | 468 | 470 | +2 |
| Space | 467 | 469 | +2 |
| Pivot_Identifier | 466 | 468 | +2 |
| Character_String_Literal | 465 | 467 | +2 |
| LineComment | 464 | 466 | +2 |
| BlockComment | 463 | 465 | +2 |
| Dollar_Sign_Identifier | 462 | 464 | +2 |
| Double_Quoted_Numeric_Identifier | 461 | 463 | +2 |
| Numeric_Identifier | 460 | 462 | +2 |
| Scientific_Numeric_Literal | 459 | 461 | +2 |
| EXPONEN | 458 | 460 | +2 |
| Identifier | 457 | 459 | +2 |

Validation: six new lexical regression cases failed on the before image and
all pass after the additions. The focused run of `SqlArrayLexerBaselineTest`
and the existing `SqlDatetimeLexerBaselineTest` passes **14/14**, zero
failures/errors/skips; generation, main compilation and test compilation pass.
The tests compare complete symbolic token streams, exercise case-insensitive
constructor recognition and nonreserved column/qualified-UDF compatibility,
and distinguish `->` from subtraction, comparisons and named-argument `=>`.
The higher-order cases are **lexer-only**, not evidence of lambda parser/walker
support. That initial lexical-only batch did not change existing tests/goldens,
walker, registry or consumer payloads.

**Lexer/manual-repair gate COMPLETE (2026-11-19).** The developer manually
repaired the affected test goldens, ran tests from the VS Code sidebar, and
confirmed that everything passed. This is developer-reported validation; no
exact sidebar test count was supplied, and no new full Maven run is claimed.
The checkpoint inspection verifies +2 token-marker-only repairs in 36 existing
test classes and the three canonical/vendor bucket-guide copies, except for
removal of one unused `access.Snippet` import in the array-transform host.
The official token maps remain unchanged.

This historical lexer checkpoint unblocked the scalar batch below. Higher-order
T029..T031 remain unchecked; lexer readiness is not lambda support or release PASS.

### Slice D scalar closure checkpoint — T025/T026/T027 complete

**2026-11-19:** implemented the approved scalar batch with **211 new methods:
203 six-output walker goldens and eight parser-only malformed-syntax controls**.
Each function independently covers all twelve placements, operand resolution,
substitutions, derived/CTE/correlated/set-op boundaries and warning reset.
The 36 snippet cases additionally compare six complete AccessObject outputs
and exact diagnostics against their direct-walker snapshots.

- **T025:** 83 cases (80 goldens, three syntax controls). Structured constructor
  reuses generic constructor routing/walking, with new Snowflake affinity.
  One-, three- and four-argument forms include independent variadic-tail controls.
- **T026:** 74 cases (71 goldens, three syntax controls). Existing generic
  routing/affinity retained. VARIANT postfix casts exposed lost substitution
  typing; stamp the original operand with the existing context helper before
  suffix wrapping. Qualified column substitutions retain `column` typing.
- **T027:** 54 cases (52 goldens, two syntax controls). Add the existing token
  to the unary family and affinity registry; reuse its walker.

Native signatures and runtime boundaries are recorded with generated named-cell
evidence in the [reconciliation](slice-d-array-function-reconciliation.md#scalar-batch-completion-t025t026t027).
Existing goldens are unchanged. The generated Java lexer's SHA-256 remains
`0ad82dbaf1ef656cca37b8f43f488e9139b15fa113348d2d9112fc1705ea958c`.
Maven passes **1,641/1,641**, zero failures/errors/skips across the three family
hosts plus literal/subscript, casting, column-variable, non-SQL endpoint and
lexer regressions. VS Code's selected-host run also passes (tool summary:
1,727 passed, zero failed across registered controllers; not a disjoint count
to add to Maven). Generator tooling checks pass **4/4**.

**Slice D complete:** T025..T034 and T023/T024 are closed. The single full
default run passed 4,244 tests with 62 existing/configured skips and zero
failures/errors (exit 0). Whole-release certification remains separate.

### 4.2 completion gate — ARRAY_CONSTRUCT parity test plan

**Historical checklist, reconciled by completed T032:** plain/compact named
tests and shared constructor-family coverage satisfy the approved carry-forward
scope, including T025's twelve shared snippet endpoints. Per user direction,
do not reopen this checklist as duplicate matrices for each constructor name.
`ARRAY_CONSTRUCT_STRUCTURED` is complete under T025.

**Definition of done for 4.2:** complete this checklist with one-query-per-method
tests, exact `assertEquals` goldens for all extractor objects, and explicit
diagnostic-by-position assertions.

#### 4.2.A Core happy-path matrix (SQL endpoint)

1. `ARRAY_CONSTRUCT` in SELECT projection with mixed literal + qualified column
   + predicand substitution arguments (existing baseline; keep as control).
2. `ARRAY_CONSTRUCT` nested inside `TO_VARCHAR`/`MD5` in JOIN `ON`
   comparison (existing baseline; keep as control).
3. `ARRAY_CONSTRUCT` with unqualified columns from a single source table.
4. `ARRAY_CONSTRUCT` with repeated column references in argument list.
5. `ARRAY_CONSTRUCT` nested inside CASE arm(s) and returned as expression.
6. `ARRAY_CONSTRUCT` nested in derived-table SELECT and consumed by parent
   SELECT alias reference.

#### 4.2.B Placement matrix (required SQL/DML/snippet sites)

7. SELECT list expression.
8. WHERE clause predicate operand.
9. JOIN `ON` clause operand (existing baseline; keep).
10. HAVING clause operand.
11. QUALIFY clause operand (Snowflake site).
12. ORDER BY expression.
13. GROUP BY expression (if accepted by grammar path).
14. VALUES row/cell expression.
15. INSERT…SELECT projection expression.
16. UPDATE assignment RHS expression.
17. DELETE WHERE expression.
18. DDL query body expression (`CREATE VIEW … AS SELECT …`).
19. SCRIPT with two statements: Snowflake warning should be statement-scoped
    (collector reset semantics).
20. Snippet endpoint parity set (`PREDICAND`, `CONDITION`, `QUERY`, `TUPLE`,
    `IN_LIST`, `JOIN_EXTENSION`, `VALUES`, `INSERT`, `UPDATE`, `DELETE`,
    `DDL`, `SCRIPT`) following existing datetime snippet contracts.

#### 4.2.C Column/reference verification matrix

21. Qualified column args resolve and land in table dictionary/filter/interface
    per site.
22. Unqualified column args resolve against visible source when unambiguous.
23. Unresolved unqualified column in args emits expected unresolved diagnostic
    and source position.
24. Ambiguous unqualified column in args emits expected ambiguous diagnostic.
25. Hidden/invalid table alias in qualified arg emits expected fatal or severe
    warning behavior (join-extension exception path where applicable).
26. Substitution variable arguments are typed/recorded like existing predicand
    variables in other function arguments.

#### 4.2.D Dialect diagnostics matrix

27. Snowflake statement warning emitted once per statement at first
    `array_construct` site with exact position.
28. Mixed-dialect fatal when `ARRAY_CONSTRUCT` is combined with PostgreSQL-only
    feature in same statement (existing baseline; keep and extend at least one
    additional placement site).
29. Multi-statement SCRIPT: warning appears on each statement that actually
    contains `ARRAY_CONSTRUCT`; no spillover.

#### 4.2.E Negative/malformed controls

30. Parser syntax error controls for malformed calls (missing right paren,
    missing comma between args, trailing comma) with full syntax-diagnostic
    assertions.
31. Regression control proving ordinary non-ARRAY routine invocation remains
    unchanged by 4.2 warning hook.

### Historical happy-path syntax targets (sample authoring set)

These examples predate A/B completion and are not a current unsupported-feature
list: literals and union aggregates have delivered evidence, while contains
still needs T026. T032/T034 reconcile the actual tests.

```sql
-- 4.4 array literal value expression (not subscript)
select ['a', 'b', 'c'] as arr from events e;

-- 4.1 array contains with variant-ish construction
select array_contains('a', array_construct('a','b')) as has_a from events e;

-- 4.6 union/unique aggregate family
select array_union_agg(e.tags) as merged_tags from events e;
```

**Test contract (each subtask):** Happy path without FATAL; six extractor goldens where applicable; column ref matrix (qualified, unqualified, unresolved, ambiguous); required SQL/DML sites per construction; compound nest test once **4.3**, **4.9**, **4.10** exist (see former Phase 5.6 compound SELECT in git history).

### Entry 4 Slice A closure checkpoint (2026-11-17)

Slice A closure criteria were validated in two gates:

1. **Focused array/placement suites**
   - `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerSnowflakeArrayConstructTests,sql.walker.SqlEventWalkerArraySubscriptTests,sql.walker.SqlEventWalkerWithinGroupOrderedAggregateTests,sql.walker.SqlEventWalkerFunctionsAggregatesWindowingTests test`
   - Result: **207 run, 0 failures, 0 errors**
2. **Full parse cohort**
   - `mvn -pl parse test`
   - Result: **2710 run, 0 failures, 0 errors** (21 skipped)

Additional Slice A completion deltas included:
- Snowflake statement-dialect warning coverage for set-aggregate array routines (`array_union_agg`, `array_unique_agg`) in walker + strict tests.
- DDL/SCRIPT statement-scoped warning controls for `array_construct`.

### Entry 4 Slice B closure checkpoint (2026-11-17)

Slice B closure criteria were validated in two gates:

1. **Focused Slice B suites**
   - `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerWithinGroupOrderedAggregateTests,sql.walker.SqlEventWalkerSnowflakeArraySetAggregateTests,sql.walker.SqlEventWalkerSnowflakeArrayConstructTests test`
   - Result: **57 run, 0 failures, 0 errors**
2. **Full parse cohort**
   - `mvn -pl parse test`
   - Result: **2717 run, 0 failures, 0 errors** (21 skipped)

Slice B completion deltas:
- Added strict set-aggregate parity tests in `SqlEventWalkerSnowflakeArraySetAggregateTests` covering `ARRAY_UNION_AGG` / `ARRAY_UNIQUE_AGG` placement, Snowflake warning behavior, ambiguous-column diagnostics, set-op interface propagation, and script-scoped warning controls.
- Confirmed existing `ARRAY_AGG ... WITHIN GROUP ... OVER` lineage matrix remains green.

**Detail:** git — former `parser-defects-enhancements-workplan.md` Phase 5.

---

## Entry 5 — Simple Jinja / dbt substitutions

**Status:** Not started — **not** a Jinja interpreter (**Entry 6**).

| Sub | Work | Status |
|-----|------|--------|
| **5.1** | Tuple/table `{{ source() }}` / `{{ ref() }}` quote and spacing variants | Not started |
| **5.2** | Predicand/column Jinja in non-table positions | Not started |
| **5.3** | File-prefix Jinja: skip/collect as unexamined comments (bracket-matched) | Not started |
| **5.4** | **2.5 closure** — Jinja-authored DNC rollup (§2.5 starter in git) without `PARSE_TIMEOUT` | Not started (needs **5.1**) |

**Platform constraints:** [panto-variable-inheritance-using-bundles.md](./panto-variable-inheritance-using-bundles.md)

**Detail:** git — former `parser-defects-enhancements-workplan.md` Phase 6.

---

## Entry 6 — Deep Jinja / SCRIPT prefix *(optional)*

**Status:** Not started — **do not start until Entry 5 accepted** unless product requires compile-time shape change.

| Sub | Work |
|-----|------|
| **6.1** | Parse Entry **5.3** prefix bucket under SCRIPT endpoint (`config`, `set`, `snapshot`, …) |

Further 7.x (`{% if %}`, macros, filters, dbt_utils) — add when phase activated.

**Spec:** [panto-language-enhancements-grammar-safe-dynamic-expansion-2.md](./panto-language-enhancements-grammar-safe-dynamic-expansion-2.md) (overlaps strategic intent with **Entry 8**).

**Detail:** git — former `parser-defects-enhancements-workplan.md` Phase 7.

---

## Entry 7 — Panto variable types × parser endpoints (tests)

**Status:** Not started — **entirely deferred beyond 5.1.4** by the latest
release-scope reduction; engineering backlog, not new grammar. Neither endpoint
parity nor type×grammar coverage/accounting is a 5.1.4 release prerequisite.

**Theme:** Seven substitution `type=` values × sixteen parse endpoints — fill golden gaps.

**8.1 endpoint gaps (summary):**

| Endpoint | Gap |
|----------|-----|
| `INSERT` | No INSERT endpoint parity test vs `SQL` |
| `DELETE` | Smoke only — extend golden parity |
| `SCRIPT` | No angle-bracket subs in SCRIPT-scoped outputs |
| `DDL` | Subs in DDL templates — only if product needs |
| `VALUES` | No matrix/cell `<var>` endpoint test |
| `LITERAL` | N/A (document only) |

**8.2 type × grammar gaps (summary):** `EXISTS <var>`; minimal `INSERT … <queryVar>`; VALUES matrix `<var>`; defer Jinja predicand to **Entry 5.2**.

**References:** `SQLParserEndPoints.java`, `SqlEventWalkerNonSqlEndpointParserTests`, `SqlASTWalkerHelper.resolveSubstitutionValueTypeFromContext`.

**Detail:** git — former `parser-defects-enhancements-workplan.md` Phase 8.

---

## Entry 8 — Panto cardinality & grammar-safe dynamic AST expansion

**Status:** Not started — exploratory design

**Do not duplicate spec.** Authoritative design: [panto-language-enhancements-grammar-safe-dynamic-expansion-2.md](./panto-language-enhancements-grammar-safe-dynamic-expansion-2.md) (delivery sequence 9.1–9.5 in that doc).

**Platform:** [panto-variable-inheritance-using-bundles.md](./panto-variable-inheritance-using-bundles.md) — parser/walker changes must preserve bundle/substitution semantics.

**Detail:** git — former `parser-defects-enhancements-workplan.md` Phase 9.

---

## Entry 9 — DDL structured options parsing

**Status:** Not started (product-triggered)  
**Prerequisite:** Phase 20 DDL walker hygiene ✅ (walked `subMap`, not ctx scrape)

**Goal:** Promote high-value clauses from opaque `generic_ddl_options` blobs to typed AST flags (`if_not_exists`, `or_replace`, …); keep unmodeled tails opaque.

| Phase | Work |
|-------|------|
| **9.D0** | Product trigger + v1 clause list (`IF NOT EXISTS`, `IF EXISTS`, `OR REPLACE`, …) |
| **9.D1** | Grammar inventory |
| **9.D2** | Grammar + walker for v1 |
| **9.D3** | `SqlEventWalkerScriptsAndDDLTests` structure proofs |
| **9.D4** | Generator round-trip *(optional)* → **Entry 10** |
| **9.D5** | Expand by demand |

**Tests (proposed):** `createTableIfNotExistsParsedOptionsTest`, `createViewOrReplaceParsedOptionsTest`, `dropTableIfExistsParsedOptionsTest`, `createTableUnmodeledOptionsRemainOpaqueTest`.

**Detail:** git — former `ddl-structured-options-parsing-workplan.md`.

---

## Entry 10 — SQL statement generator completion

**Status:** Milestone ✅ (~51 round-trips); **expansion not started**

**Code:** `generators/SQLStatementGenerator.java`, `AbstractSQLASTGenerator.java`  
**Tests:** `generators/SQLStatementGeneratorTest`

**Architecture:** Walker mumble-key AST → generator `emit*` / `on*`; unhandled shapes → `Unexpected node`.

| Area | Milestone status |
|------|------------------|
| INSERT / UPDATE / DELETE / TRUNCATE | ✅ |
| SELECT core + WITH / VALUES / SCRIPT | ✅ / 🟡 |
| Opaque DDL CREATE/ALTER/DROP | ✅ |
| PIVOT / UNPIVOT exercised shapes | ✅ |
| Set ops INTERSECT/EXCEPT depth | 🟡 |
| Table functions matrix | 🟡 |
| CASE / IN / LIKE ANY / windows / Jinja map | 🔴 |
| Endpoint trees (predicand, condition, …) | 🔴 stubs |

**Phases (summary):** **0** inventory + round-trip contract + gate profile; **1** leaves/literals; **2** expressions; **3** predicates; **4** functions; **5** FROM/joins; **6** modifiers; **7** windows; **8–11** statements/DML/DDL/script. Work bottom-up when extending.

**Related:** Entry **9.D4** for typed DDL flags; Entry **1.D** for new datetime AST nodes.

**Datetime deferral note (2026-11-16):** `datetime_function` generator support
for the newly delivered Entry 1 Snowflake datetime families is intentionally
deferred here (Entry 10) per release-scoping decision.

**Inventory (auto):** `parse/tools/sql_generator_phase0_inventory.py` → `parse/documents/coverage/sql_generator_phase0_inventory.md` (regenerate after grammar/generator changes).

---

## Entry 11 — Walker `exit*` coverage (JaCoCo)

**Status:** Complete (2026-11-19) within the approved release scope. Tier 1
remains complete; reachable missed paths were covered by focused strict
tests, and retained defensive/legacy paths received source-backed
dispositions. This is not a claim of 100% line coverage. Tier 3 residuals
remain outside 5.1.4 scope.

**Scope and evidence:** the
[current coverage inventory](./coverage/sql_walker_exit_method_gaps.md)
owns the approved twelve-method set, its latest measurements, evidence source
and refresh command. Do not copy its method rows or counters into this workplan.

**Closeout:** five one-input tests preserve full extractor goldens and exact
diagnostics. Matching full-default execution data was retained and augmented
by successful affected-host tests; no new full suite or release certification
was run. No production changes, coverage exclusions or synthetic collector
maps were needed. The inventory records the measured residuals and their
individual grammar/producer proofs.

**Done criterion (met):** approved valid-input gaps are covered or have an
explicit source-backed unreachable-path disposition; only excluded Tier 3
work and the documented defensive/legacy residuals remain. Reuse valid
existing execution data for accounting; test changes require new execution
evidence, not a duplicate run solely for sign-off.

**Detail:** git — former `coverage/sql_walker_exit_method_coverage-workplan.md`.

---

## Entry 12 — Helper dead-code hygiene

**Status:** Complete (closed 2026-11-16): Phases **A–D** complete.

**Policy:** Caller audit before delete; JaCoCo is heat map only, not delete oracle. Gate: `mvn -Psmoketest-quality-gate test`.

| Phase | Work | Status |
|-------|------|--------|
| **12.A** | Tier-1 dead public API in symbol-tree helpers | ✅ |
| **12.B** | Cascading private orphans + walker orphans | ✅ |
| **12.C** | Fresh JaCoCo heat map on megaclass helpers | ✅ (2026-11-16) |
| **12.D** | Wrapper hygiene (deprecated zero-caller stubs only) | ✅ (2026-11-16) |

**12.C/12.D closeout record (2026-11-16):**

- Gate run (post-change): `mvn -Psmoketest-quality-gate test`
  - `SmoketestQualityGateTestSuite`: 263 tests, 0 failures, 0 errors, 0 skipped (`BUILD SUCCESS`)
- Wrapper hygiene audit:
  - Removed zero-caller deprecated private wrapper constant
    `SCOPE_CLAUSE_COLUMN_LIST_KEYS` from
    `SqlParseSymbolTreeHelper` (alias of `ARCHIVED_SCOPE_COLUMN_REFERENCE_CONTAINER_KEYS`).

**Detail:** git — former `helper-dead-code-hygiene-workplan.md`.

---

## Entry 13 — SELECT-list subquery alias scope

**Status:** Complete (opened and closed 2026-11-10)  
**Kind:** Defect — blocking regression vs **5.0.0-3** (valid SQL rejected with FATAL)  
**Brief:** [select-list-subquery-alias-scope-defect.md](./select-list-subquery-alias-scope-defect.md)

**Symptom:** A subquery nested inside a SELECT-list *expression* (arithmetic operand, function argument, `CASE`, `IN (subquery)`, `NOT IN`, `EXISTS`) gets `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE` for every qualified reference to an alias declared inside the subquery. The diagnostic is reported at the outer select item's output alias. The same subquery is accepted in `WHERE` / `ON` / `HAVING` / `ORDER BY` and as a bare select item. Reproduced unchanged on 5.1.4-1.

**Root cause:**

1. `exitSelect_item` builds `interface[alias]` with `flattenSubTreeForDependencyColumns(..., skipQueryBackedSubtrees=false)`, so the flattening descends into nested subqueries and lifts inner-scope refs (`g.y`, `pp.x`) into the outer interface. Interface validation at scope exit then resolves them against the outer `table_alias` and FATALs. Clause flattening (`filters`, `grouped_by`, `ordered_by`) already stops at subquery boundaries. Bare scalar-subquery items avoid the FATAL only because the whole output column is skipped (`MUMBLE_SCALAR_SUBQUERY_ALIASES_KEY`).
2. Separate AST regression: the `predicand_subquery` rule (new since 5.0.0-3) never re-applies the `lookup` wrapper that 5.0.0-3 put on every scalar subquery, so operands are emitted as `calc:{left:{select…}}`. This is **not** the cause of the FATAL (`IN (subquery)` has an identical AST on both versions and still FATALs), but it makes the AST diverge from 5.0.0-3 and from the bare select-item shape.

| Sub | Work | Status |
|-----|------|--------|
| **13.1** | Select-item interface flattening stops at subquery boundaries (`select` / `lookup` / `exists` / set-op); subquery-local refs stay in `def_queryN`, linked via `dependent_queries`; outer correlations still validated at predicate-frame exit | Done — `SqlParseSymbolTreeHelper.flattenSelectItemDependencyColumns`, called from `exitSelect_item` |
| **13.2** | `exitPredicand_subquery` restores the `lookup` wrapper so operand scalar subqueries match 5.0.0-3 AST shape | Done |
| **13.3** | Regression tests: every Fails/Passes case in the brief, snippets 9530 / 8520, `zz` negative control, AST `lookup` shape | Done — `SqlEventWalkerSelectListSubqueryAliasScopeTests` (6 tests) |
| **13.4** | Corpus re-check: 11-10-2026 bound-query and snippet extracts, 5.0.0-3 vs 5.1.4 before/after | Done — results below |

**Golden updates:** 68 AST goldens changed only by the restored `lookup` wrapper (verified structurally). 5 symbol-table goldens had recorded the leak and were corrected: `scalarSubqueryWindowPartitionBySymbolTableTest` / `scalarSubqueryWindowOrderedBySymbolTableTest` (`rn` listed `k`/`ts` as `fact` columns), `nestedFormulaSubqueriesUseQueryRefsInInterfaceAndFiltersTest` (`dd` listed `D` as a `tab1` column), `nestedQueryDemoTest` / `nestedQueryDemoWithCteTest` (`mxd` listed the inner `tt` subquery's columns). No token-ID or pivot goldens changed. Full suite: 2181 run, 0 failures, 21 skipped.

**Corpus results (94,448 cases: 6,551 bound queries + 87,897 snippets, 11-10-2026 extract):**

| Measure | 5.0.0-3 | 5.1.4 before | 5.1.4 after |
|---------|---------|--------------|-------------|
| Bound queries with FATAL | 100 | 337 | 333 |
| Scope-signature FATALs (SQL / QUERY / TUPLE) | 0 / 0 / 0 | 128 / 10 / 385 | 124 / 0 / 4 |
| AST byte-identical to 5.0.0-3 | — | 57,805 | 59,188 |
| `lookup` node count equal to 5.0.0-3 | — | 93,039 | 94,440 |

395 cases fixed, all with the scope signature; 0 newly fatal.

**Remaining 113 bound queries with a scope FATAL where 5.0.0-3 is clean** (all classified; 5.0.0-3 never validates alias scope, and all 113 FATAL identically before this fix):

| Group | Cases | Finding |
|-------|-------|---------|
| No `join_extension` in the template | 14 | Genuine author errors: alias never declared (q508, q746, q1476, q1669, q4154, q5112, q6290, q6304, q33), typo (q3870 `stu_term_crmstu_term_crm`), or alias used outside the derived table that declares it (q1280, q2910, q3676, q5137). |
| Template contains a `join_extension` variable | 99 | The unresolved alias is never declared in the template text but is consistent with being declared by the substituted join snippet: in 98 of 99 cases every missing alias is declared by at least one JOIN_EXTENSION snippet in the same extract (matched by alias name; the extract carries no variable binding). Minimal repro: `select a.id, ev.x from t a <extension>` — 0 FATAL on 5.0.0-3, FATAL on 5.1.4. |

The `join_extension` group is a separate 5.1.x behaviour, not part of this defect: alias-scope validation cannot see aliases that a trailing join extension introduces. **Open decision** (not scheduled): whether unresolved qualifiers in a scope containing a `join_extension` substitution should stay FATAL, downgrade to a warning, or be deferred to post-substitution validation.

**IN_LIST snippets:** 33,307 of 33,810 extract values are stored without the parentheses the `IN_LIST` endpoint (`in_list_predicate_value`) requires; templates always write `IN <var>`. Parsed raw they FATAL identically on both versions. Wrapped in `( … )`, 33,809 parse clean on both with byte-identical ASTs; the one exception (s6989) has a stray `]` that 5.1.4 rejects and 5.0.0-3 silently accepts.

**Acceptance:** Every Fails case and both snippets reach 0 FATALs; every Passes case stays at 0; genuine outer-scope errors (e.g. `zz.y` inside the subquery) still FATAL; no new FATALs elsewhere in the live corpus. Outer `interface` entries no longer list subquery filter columns as sources of the output column (answers the brief's lineage question).

**Out of scope:** Correlated `EXISTS` / `IN` in the SELECT list beyond what Snowflake documents (WHERE-only). The parser accepts them; Snowflake may not.

---

## Entry 14 — Join extension deferral of missing-table fatals (whole-query)

**Status:** Complete (opened and closed 2026-11-10)  
**Kind:** Defect — release blocker for bound-query templates

**Problem:** Panto templates end a `FROM` list with a join extension variable (`FROM … <join_extension.Student>`). The snippet substituted there routinely declares aliases that the template's SELECT list, filters, and correlated subqueries reference (e.g. q2157 reads `event_agg.event_ct`; snippet s35160 supplies `LEFT OUTER JOIN (…) event_agg`). 5.0.0-3 never validated alias scope; 5.1.x reports every such reference as FATAL `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE` because the template alone cannot declare them. In the 11-10-2026 extract this affects 99 Enroll360/ALR bound queries (588 fatals); 92% of the alias-introducing snippet joins are `LEFT [OUTER] JOIN`, i.e. an established optional-enrichment convention, not author error. The default substitution for a join extension is *no* substitution (the variable is removed at generation), so a template is only valid when paired with a snippet that declares the missing aliases.

**Rule (applied once, at the end of the walk, over the complete diagnostic list):**

1. Collect all diagnostics exactly as today (unresolved qualified references are still raised as FATAL during the walk).
2. During the walk, record each join extension variable's name and source position (simple list on the event walker; no symbol-table entry, no assumptions about snippet content).
3. At handoff (`SqlParseEventWalker.getSnippet()`), if the query has at least one join extension variable **and** at least one `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE` fatal:
   - convert only those `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE` fatals to `SEVERE_WARNING` (same code, message, position); every other fatal stays fatal;
   - add one `JOIN_EXTENSION_REQUIRED` severe warning per join extension variable, at the variable's position, stating that the listed table/column references must be satisfied by the substituted snippet and the join extension is therefore not optional.
4. Join extension variables with no `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE` fatal produce no warning.
5. Multi-statement scripts: each statement is evaluated on its own when it finishes. Only the fatals that statement generated are considered, together with only that statement's join extension variables. The join extension list is then emptied, so a join extension never affects a sibling statement's fatals, and a later statement with missing-table fatals and no join extension keeps them as fatal.

**Known imprecision (accepted for release):** the check is whole-query. A missing alias in a scope the join extension cannot reach (another CTE, a non-lateral derived table, a sibling set-operation branch, an outer query) is also downgraded, and every `JOIN_EXTENSION_REQUIRED` warning lists all deferred references rather than those the specific variable could satisfy. **Entry 15** removes this.

| Sub | Work | Status |
|-----|------|--------|
| **14.1** | Record join extension variable locations in `exitJoin_extension` | Complete |
| **14.2** | Conversion + `JOIN_EXTENSION_REQUIRED` diagnostic (catalog entry in `SqlASTWalkerHelper`); runs at `exitSql_statement` for scripts and at `getSnippet()` for single-query endpoints | Complete |
| **14.3** | Tests (`SqlEventWalkerJoinExtensionDeferralTests`, 6): one / two join extensions; no warning without missing refs; no conversion without a join extension; unrelated fatals (duplicate interface, set-op column count) stay fatal; script statements evaluated independently | Complete — full `parse` suite 2187 run, 0 failures, 21 skipped; no existing golden changed |
| **14.4** | Corpus re-check of the 113 remaining bound queries (`alias-scope/remaining113-entry14.out` in the local archive documented under Entry 1) | Complete — see results below |

**14.4 results (5.1.4-1 jar with Entry 13 + 14):**

| Group | Queries | Still FATAL | `JOIN_EXTENSION_REQUIRED` | Remaining fatals |
|-------|---------|-------------|---------------------------|------------------|
| Join extension templates | 99 | 1 (q2155) | 99 | q2155: `DUPLICATE_INTERFACE_COLUMNS` + `SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH` (genuine, unrelated to scope) |
| No join extension (genuine errors) | 14 | 14 | 0 | 31 `QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE` + 7 `QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS` — unchanged |

---

## Entry 15 — Scope-aware join extension deferral (enhancement)

**Status:** Not started — follows **Entry 14**; requires a rework of walker diagnostic capture  
**Kind:** Enhancement

**Release scope:** **Entirely deferred beyond 5.1.4**, including the previously
selected C1 same-scope join-extension visibility characterization, C2
non-lateral derived/out-of-scope CTE non-visibility characterization, and C3
sibling set-operation isolation characterization. Neither those tests nor the
broader diagnostic-subsystem rework is a 5.1.4 release prerequisite. Entry 14's
documented accepted release behavior remains unchanged.

**Goal:** Defer a missing-table/column diagnostic to a join extension only when that join extension could actually make the reference valid, and attribute each deferred reference to the specific join extension variable(s) that could satisfy it.

**Visibility rules to implement.** A join extension attached to query scope *Q* (`FROM … <ext>` of *Q*) adds table aliases, and through them columns, that are visible to:

- *Q*'s own SELECT list, `WHERE`, `GROUP BY`, `HAVING`, `QUALIFY`, `ORDER BY`, window clauses, and `ON` conditions of joins inside the extension;
- correlated references inside subqueries nested in those clauses (scalar, `EXISTS`, `IN`, quantified, lateral), at any depth, unless an intermediate scope shadows the alias.

It is **not** visible to:

- non-lateral derived tables in *Q*'s `FROM` list, or CTE bodies defined outside *Q* (including a CTE whose body references its own name, as in q3852);
- sibling set-operation branches;
- enclosing (outer) queries. Aliases never flow outward; only *Q*'s output interface does.

**Output propagation (the "queries that reference their outputs" case).** If *Q* projects a wildcard (`*` or `alias.*`) over a source set that includes a join extension, *Q*'s output interface is *open*: the snippet may add output columns. A consumer of *Q*, such as an outer query reading `agg.col`, a CTE reference, or a set-operation peer, should then also defer `QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS` / `UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES` against that alias to the originating join extension, instead of treating them as fatal. Explicit (non-wildcard) projections close the interface, and those diagnostics stay fatal.

**Why it needs a diagnostic-subsystem rework.** Today diagnostics are appended to one flat walker list as they are discovered. Some are merged across scopes (`globalQualifiedUnresolvedLocations`, merged-location messages) and de-duplicated by key (`emittedQualifiedSourceNotFoundKeys`), so after the walk a diagnostic can no longer be tied reliably to the scope that raised it or to the scopes that could satisfy it. The precise variant needs:

1. **Scope-attached diagnostics.** Each unresolved reference is recorded against the `def_queryN` scope that owns it (scope path plus reference position), not only in the flat list.
2. **Pending, not emitted.** Unresolved qualified references are held as pending on their scope. At each scope exit they are either resolved, propagated to the enclosing scope that is visible to them (for correlation), or finalised.
3. **Join extension ownership.** Each scope records the join extension variables attached to its own `FROM`. A pending reference reaching a scope that owns a join extension, without being shadowed on the way, is deferred to that variable. A reference that leaves every visible scope without meeting one becomes fatal.
4. **Open-interface taint.** A scope that projects a wildcard over a join-extension source marks its published interface as open, tagged with the originating variable(s). Consumers check the taint before raising `QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS`-type diagnostics.
5. **Per-variable warnings.** `JOIN_EXTENSION_REQUIRED` lists only the references deferred to that variable. A variable with none produces no warning.
6. **De-duplication and merged locations** move to finalisation time, so per-scope attribution survives.

**Acceptance (beyond Entry 14):**

- q3201 shape (alias used inside a derived table whose own `FROM` carries the join extension) is still deferred.
- A missing alias in a CTE or derived table with no join extension stays fatal, even when another scope in the same query has one.
- Two join extensions in different scopes produce disjoint reference lists.
- An outer reference to `agg.col` is deferred only when `agg` projects a wildcard over a join-extension source.
- Entry 14 corpus results: no previously deferred valid template becomes fatal.

**Out of scope:** Inspecting candidate snippet text to decide whether it actually declares the alias. The bound query and the snippet are parsed independently, and the binding is resolved by Panto bundles at generation time.

---

## Entry 16 — Set operation with a query variable member loses its output interface

**Status:** Partial — known identifier/function grammar fixes delivered with Entry 1 Slice A; remaining work below (opened 2026-11-11)  
**Kind:** Defect — release blocker for bound-query templates  
**Found by:** 5.0.0-3 vs 5.1.4-1 comparison of the 11-10-2026 extract ([panto-template-remediation-5.1.4.md](./panto-template-remediation-5.1.4.md), "Not your problem")

**Problem:** When a derived table or CTE body is a set operation and one of its members is a query variable (`set_operation_member : … | variable_identifier`, for example `<union_add.Partner ID Xwalk>`), the published output interface of that derived table / CTE is empty. Every `alias.column` read by the consumer is then reported FATAL `QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS`, although the real `SELECT` member defines all the columns. A set operation takes its output names from its first query member (positional matching for the rest), so the interface should come from the query member(s), and the variable member should only be checked for arity when its shape is known (it is not, at template parse time).

**Affected queries (11-10-2026 extract, `bound_queries_11-10-2026.csv` record numbers, header = record 1):**

| Case ID | Domain / entity / query name | FATALs |
|---------|------------------------------|--------|
| q2084 | Enroll360 / Partner ID Xwalk / Enroll360.Partner ID Xwalk.final | 7 (alias `all_ids`) |
| q2087 | Enroll360 / Partner ID Xwalk / Enroll360.Partner ID Xwalk.final_simple | 7 (alias `agg`) |
| q2088 | Enroll360 / Partner ID Xwalk / Enroll360.Partner ID Xwalk.final_simple | 12 (alias `all_ids`) |

Removing only the `union all <union_add.…>` member clears every FATAL in q2087. Ten other templates in the extract use `union [all] <union_add…>` without failing because the union is top-level, has two or more concrete query members, or no consumer reads the derived alias's columns. All thirteen templates were rechecked after the fix.

**Observed behaviour (2026-11-11 probes, `alias-scope/ua*.tsv` in the local archive documented under Entry 1):**

| Shape | Result |
|-------|--------|
| `(select t.a, t.b from tbl t <op> <union_add.X>) agg` for `<op>` = `UNION ALL`, `UNION`, `UNION DISTINCT`, `INTERSECT`, `EXCEPT` | FATAL on `agg.a`, `agg.b` |
| `(<union_add.X> union all select t.a, t.b from tbl t) agg` (variable first) | FATAL |
| CTE body `with agg as (select … union all <union_add.X>) select agg.a …` | FATAL |
| `(select t.a from tbl t union all <union_add.X> union all <union_add.Y>) agg` (one query, two variables) | FATAL |
| `((select t.a from tbl t union <q.X>) intersect (select u.a from u u)) agg` (nested, parenthesised) | FATAL |
| `(<union_add.X> union all <union_add.Y>) agg` (variables only) | FATAL — expected behaviour needs a decision (see below) |
| `(select t.a from tbl t union all select u.a from u u union all <union_add.X>) agg` (two queries, one variable) | Clean |
| `(select t.a from tbl t intersect <q.X> except <q.Y>) agg` | Clean |
| Top-level `select t.a from tbl t union all <union_add.X>` | Clean |

**Confirmed root cause (2026-11-11):** set-operation identity was inferred from at least two published `def_queryN` / query-like participant scopes. A query variable is correctly typed and retained in the AST / substitution map but intentionally has no `def_queryN`; one concrete query plus variable members therefore failed the participant-count test and the composite `def_unionN` / `def_intersectN` never received the concrete query's interface. The operator staged for a variable was also left pending and could be stamped onto the composite scope or a later participant.

**Fix rule:**

1. The interface of a set operation comes from its first **query** member, wherever variables appear in the member list (first, middle, last, or several).
2. Variable members contribute no columns and do not trigger `SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH` or `DUPLICATE_INTERFACE_COLUMNS`.
3. **Variable-only set operation (decision confirmed 2026-11-11):** the interface is unknown at template parse time. Publish an open wildcard interface so consumers do not falsely reject `alias.column`; final generated SQL remains responsible for concrete interface validation after substitution.

| Sub | Work | Status |
|-----|------|--------|
| **16.1** | Root cause in set-op interface construction for query + variable members | Complete (2026-11-11) |
| **16.2** | Fix per the rule above; decide and implement the variable-only case | Complete (2026-11-11) |
| **16.3** | Tests (below) | Complete — focused class + existing set-op gate pass |
| **16.4** | Re-check q2084, q2087, q2088 and the ten passing `union_add` templates; full suite; no change to set-op egress goldens unless explained | Complete — all 13 templates have zero FATALs under both 5.0.0-3 and 5.1.4-1; full suite passes |

**Test requirements (16.3).** The tests must prove that **every set (tuple-conjunction) operator the grammar supports** works with a query snippet variable as a member: `UNION`, `UNION ALL`, `UNION DISTINCT`, `EXCEPT`, `EXCEPT ALL`, `EXCEPT DISTINCT`, `INTERSECT`, `INTERSECT ALL`, `INTERSECT DISTINCT` (`union_clause` / `intersect_clause` with optional `set_qualifier`). For each operator cover:

- the variable as the last, first, and a middle member;
- **more than one variable** in the same operation (for example `select … union all <union_add.X> union all <union_add.Y>`);
- **more than one query** in the same operation together with one or more variables (for example `select … from a union all select … from b union all <union_add.X> union all select … from c`);
- mixed operators with precedence (`INTERSECT` binds tighter than `UNION` / `EXCEPT`) and parenthesised sub-operations that contain variables;
- each placement as a derived table read by `alias.column`, as a CTE body read by `cte.column`, as a scalar / `IN` subquery, and at top level;
- negative controls: a genuinely missing column (`agg.not_there`) stays FATAL, and two **query** members with different column counts still raise `SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH`;
- the variable-only case, with whatever behaviour 16.2 decides.

Assert both the diagnostics and the published interface (`def_queryN` output columns) of the set operation.

**Acceptance result (2026-11-11):** q1789, q1840, q1841, q1842, q1853, q1854, q1855, q1872, q1996, q2084, q2087, q2088, and q2089 all parse with zero FATALs under both 5.0.0-3 and the fixed 5.1.4-1 build. No snippet record in the extract exhibited this defect. The full `parse` regression passes: 2,194 tests, 0 failures, 0 errors, 21 skipped. Seven pre-existing symbol-table expectations were updated because they encoded the retired pending-operator leakage and absence of the now-required composite interface.

---

## Entry 17 — Merged into Entry 1

**Status:** Merged into **Entry 1** (2026-11-11)
**Former scope:** `DATEADD` / `DATEDIFF` date parts resolved as columns (q5866; false `UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES`, plus silent phantom-column lineage over physical sources).

All problem evidence, reproductions, implementation requirements, tests, and bound-query/snippet extract re-check instructions now live under **Entry 1 — Target-dialect date/time manipulation and dependency affinity**. Keep this numbered tombstone so Entries 18+ do not need renumbering; do not implement or track Entry 17 separately.

---

## Entry 18 — Snowflake non-reserved words, array literals, and walker crash after syntax errors

**Status:** Closed by owner decision (2026-11-11): known bound-query regressions fixed; unused s82233 discrepancy obsolete and immaterial, no remediation planned under this entry

**Kind:** Defect  
**Found by:** 5.0.0-3 vs 5.1.4-1 comparison of the 11-10-2026 extract

**Problem:** Words that Snowflake allows as identifiers or function names are rejected by the 5.1.4 grammar, and an array literal in a SELECT list does not parse. After the resulting syntax error the semantic walk often crashes (`ClassCastException: String cannot be cast to Map`, or `null`) or aborts with `AST_WALKER_STACK_MISALIGN`, adding application-issue FATALs on top of the syntax error.

**Affected queries and snippets (11-10-2026 extract; `q` = bound query record, `s` = snippet record):**

| Sub-problem | Case IDs | Domain / entity / query name (or bundle) | Example |
|-------------|----------|------------------------------------------|---------|
| `position` as alias / column | q5909, q5910, q5913 | PDP_UG / acs__contacts / del_src_intake_acs_contacts_PDP_UG_asof_20260910 (q5909), src_intake_acs_contacts_PDP_UG_asof_20260910 (q5910), src_intake_acs_contacts_PDP_UG_v0.2 (q5913) | `CAST(acq_contacts.<Position> AS varchar) AS position`, `acs_contacts.position` |
| `input` as CTE name | q5390, q5394 | Partner_Data_Platform / Contact Attributes Combined / con_contact_attributes_combined_full_load (q5390), pre_swap_con_contact_attributes_combined_full_load (q5394) | `with input as ( … )` |
| `path` as alias / column | q2090 | Enroll360 / Partner ID Xwalk / Enroll360.Partner ID Xwalk.ID_Array | `ARRAY_CONSTRUCT(SYSTEM_NAME\|\|'::'\|\|ID) AS PATH`, `ARRAY_APPEND(ap.PATH, se.B)` |
| `truncate(…)` function | q3399, q3438, q3490, q3546 | Enroll360 / Student Term Application / Enroll360.Student Term Application.final (q3399), .final_v2 (q3438), INACTIVE_….final (q3490), INACTIVE_….final_v2 (q3546) | `dateadd(d, truncate(try_to_decimal(…)), …)` |
| Array literal | s82233 (tuple) | Foundation / bundle `m_STUDENT_COURSE_WORK_8026_OSF:v.5001` | `(select ['STUDENT_ID', 'ACADEMIC_PERIOD', …]` |

**One-line repros (2026-11-11, all FATAL on 5.1.4-1):** `select cast(a.x as varchar) as position from t a`; `select a.position from t a`; `with input as (select 1 as x) select input.x from input`; `select array_construct(a.x) as path from t a`; `select truncate(a.x) as y from t a` (`trunc(a.x)` is clean); `select ['a','b'] as arr from t a`.

**Fix:**

- Allow `POSITION`, `INPUT`, `PATH` wherever a non-reserved identifier is allowed (column, alias, CTE / table name, qualified column part), keeping their keyword roles (`POSITION(x IN y)` / `POSITION(x, y)`, `FLATTEN(input => …, path => …)`). Audit the other keywords added since 5.0.0-3 against Snowflake's [reserved keyword list](https://docs.snowflake.com/en/sql-reference/reserved-keywords) and allow every non-reserved one as an identifier.
- Accept `TRUNCATE(expr [, scale])` as a numeric function (Snowflake synonym of `TRUNC`), distinct from DDL `TRUNCATE TABLE` (Entry 1 notes TRUNC vs TRUNCATE).
- Accept array literals `[expr, …]` as value expressions (overlaps **Entry 4.4**; verify against the delivered `expr[n]` subscript work).
- Walker robustness: after a syntax error the walker must not throw. Report the syntax error(s) only, and skip or guard the semantic step instead of raising `ClassCastException` / `null` application FATALs.

| Sub | Work | Status |
|-----|------|--------|
| **18.1** | Non-reserved identifier audit; allow `position`, `input`, `path` (+ any others found) as identifiers | Known three names verified in all six affected bound queries; broader audit not performed and not a closure requirement under the owner's accepted scope |
| **18.2** | `TRUNCATE(expr[, scale])` function | Complete for known regression: grammar forms tested and all four affected bound queries parse/walk with zero FATALs |
| **18.3** | Array literal value expression (with Entry 4.4) | Obsolete for this discrepancy: s82233 is unused; no compatibility fix planned. General array work remains independently scoped under Entry 4.4 |
| **18.4** | Walker guard: no exceptions after syntax errors (repro: any 18.1–18.3 case) | Not implemented; follow-on diagnostic for unused s82233 accepted without remediation under this entry, not a claim of general walker robustness |
| **18.5** | Tests per word in each identifier position plus its keyword role; token-ID golden impact handled per `.cursor/rules/lexer-token-id-golden-policy.mdc` (substitution list, no bulk update without approval) | Known-word syntax/keyword-role coverage passes in `SqlDatetimeLexerBaselineTest`; broader coverage not required for accepted closure scope |
| **18.6** | Re-check q2090, q3399, q3438, q3490, q3546, q5390, q5394, q5909, q5910, q5913, s82233; full suite | Sample comparison complete: all ten bound queries fixed; s82233 difference accepted as immaterial. Subsequent testing-extension full suite passed: 2,643 passed, 0 failed (Entry 1 Slice A) |

### Entry 18 sample comparison (2026-11-11)

Rebuilt the current 5.1.4-1 fat JAR using
`mvn -Pisolated-build -pl parse -Dmaven.test.skip=true package`. Replayed the same
frozen inputs through both that JAR and the archived 5.0.0-3 JAR with
`BatchParse.java`: bound queries use `SQL`, s82233 uses `TUPLE`. Both batch
processes exited 0 and returned all 11 records; process success alone is not
parse success, so FATAL counts were checked explicitly.

| Construction | Cases | 5.0.0-3 FATALs per case | Pre-fix 5.1.4 FATALs per case | Current FATALs per case |
|--------------|-------|------------------------|-----------------------------|------------------------|
| `position` alias/column | q5909, q5910, q5913 | 0 | 3 | 0 |
| `input` CTE | q5390, q5394 | 0 | 3 | 0 |
| `path` alias/column | q2090 | 0 | 4 | 0 |
| `truncate(...)` | q3399, q3438, q3490, q3546 | 0 | 2 | 0 |
| Array literal | s82233 | 0 | 2 | 2 |

All ten bound-query keyword/function regressions are resolved in this sample.
The four TRUNCATE query ASTs match 5.0.0-3 exactly. The other six match after
comparison-only normalization of 5.1.x numbered CTE storage
(`with.N={alias, cte}`) to legacy alias-keyed CTE storage; no other AST differences
remain. This does not claim dictionary/interface parity across parser versions.
The five focused tests also pass (0 failures/errors), covering identifier
positions, POSITION IN/comma forms, FLATTEN argument keywords, numeric TRUNCATE
forms, and DDL TRUNCATE.

**Closure decision (2026-11-11):** The owner checked usage and confirmed that the
snippets in Foundation bundle `m_STUDENT_COURSE_WORK_8026_OSF:v.5001`, containing
s82233, are never used. The s82233 discrepancy is **obsolete and immaterial**;
the 5.0.0-3 versus 5.1.4-1 behavior difference will **not be fixed** under Entry 18.
Legacy acceptance was not correct array support: its captured AST represented
the 29 strings as separate SELECT literals rather than one array.

Entry 18 is therefore **closed**: all ten relevant bound-query regressions are
fixed, and the unused snippet's syntax failure and follow-on walker
`String`-to-`Map` diagnostic are accepted without remediation. This is a scoped
owner acceptance, not proof of general array support, a completed broader keyword
audit, or general walker robustness. Entry 4.4's independent array work is
unchanged. Entry 1 Slice A's full-suite gate was subsequently satisfied as
recorded above. No parser code or golden was changed for this closure.

Frozen inputs, old/current result JSONL, captured ASTs, probe logs, and SHA-256
manifest are retained in the local archive's
`comparison/runs/entry18-keywords-20261111T1838/` (archive location under Entry 1).

---

## Entry 19 — RMCP Phase 2 acceptance-test closure

**Status:** Complete (closed 2026-11-16) — residual acceptance promises from the September 2026 RMCP plan are now closed without new parser/grammar/walker defects.
**Kind:** Test / verification (implement a parser fix only if these fixtures expose a real regression)

The old Phase 2 plan is otherwise accounted for: wildcard set-op handling (2.1), `VALUES` aliases (2.2), `WITHIN GROUP` grammar and `LISTAGG` coverage (2.3), nested / product `CASE` and function-result subscripts (2.4 / 2.6), CTE dictionary policy (2.7 / 2.9), and the 74-query timeout program (2.8 / 2.11) are delivered or closed. The Jinja-authored DNC rollup (2.5) remains explicitly tracked as **Entry 5.4**.

| Sub | Residual acceptance work | Evidence / boundary | Status |
|-----|--------------------------|---------------------|--------|
| **19.1** | Retire the invalid 2.2 fixture variant (mismatched alias usage) and keep the acceptance shape as a valid alias-consistent unparenthesized `FROM VALUES` UNION branch with joined source and `COALESCE(pso.sort_order, 999)` | Existing `VALUES` alias coverage plus manual replay confirm parser behavior is correct when aliasing is valid; unresolved-alias form remains a negative control | Complete (no parser change) |
| **19.2** | Complete shared `WITHIN GROUP` aggregate coverage for `PERCENTILE_CONT` and `PERCENTILE_DISC`; verify multiple sort keys and null ordering | `SqlEventWalkerWithinGroupOrderedAggregateTests` now includes location-matrix coverage for both percentile functions (SELECT-clause matrix, JOIN ON, UPDATE SET/WHERE, INSERT SELECT, DELETE WHERE, prior-select-list alias lineage), with explicit multi-key ordering and `NULLS FIRST/LAST` assertions | Complete |
| **19.3** | Complete `ARRAY_AGG … WITHIN GROUP` / `OVER` and compound-array coverage | Implemented in `SqlEventWalkerWithinGroupOrderedAggregateTests`: full location matrix, column-resolution diagnostics, set-op integration, and compound-array (`ARRAY_AGG(ARRAY_CONSTRUCT(...))`) control | Complete (Entry 4.3 accepted) |
| **19.4** | Run the focused Phase 2 regression classes and record whether 19.1–19.3 required code changes | Focused bundle passed: `SqlEventWalkerUnparenthesizedValuesTests` (19.1 shape), `SqlEventWalkerWithinGroupOrderedAggregateTests` (19.2 matrix), `SqlEventWalkerSetOpScopingGateTests` (wildcard/set-op controls + width-mismatch negatives), and `SqlEventWalkerSetOpQueryVariableTests` (unresolved alias/column fatal controls + width mismatch with variable members) | Complete |

For **19.1**, use a physical table in place of `{{ ref('sort_orders') }}` so the Phase 2 grammar/walker behavior is independently testable. Add the Jinja-authored variant when **Entry 5.1** lands. The previously discussed failing variant was invalid SQL (alias mismatch) and is retired as a positive fixture. Keep it only as a negative control. The valid alias-consistent variant parses correctly and requires no parser/grammar/walker change.

Candidate SQL skeleton for **19.1** (physical-table variant):

```sql
SELECT
    default_stream.partner_id,
    default_stream.partner_name,
    default_stream.segment,
    default_stream.region,
    default_stream.status,
    COALESCE(pso.sort_order, 999) AS sort_order,
    default_stream.is_default
FROM VALUES
    (1001, 'Unknown Partner', 'GEN', 'NA', 'active', 1),
    (1002, 'Other Partner',   'GEN', 'NA', 'active', 1)
AS default_stream (partner_id, partner_name, segment, region, status, is_default)
LEFT JOIN partner_sort_orders pso
    ON pso.partner_id = default_stream.partner_id

UNION ALL

SELECT
    live.partner_id,
    live.partner_name,
    live.segment,
    live.region,
    live.status,
    COALESCE(pso.sort_order, 999) AS sort_order,
    0 AS is_default
FROM live_partner_stream live
LEFT JOIN partner_sort_orders pso
    ON pso.partner_id = live.partner_id
```

For **19.2**, assert AST, interface, dictionaries, symbol table, and `within_group_ordered_by` lineage. Do not add unsupported aggregate names merely to satisfy the old prose; test the four aggregates admitted by `ordered_aggregate_function` (`LISTAGG`, `ARRAY_AGG`, `PERCENTILE_CONT`, `PERCENTILE_DISC`) and align dialect restrictions with current target documentation.

**19.4 execution record (2026-11-16):**

- Command:
  - `mvn -pl parse -Dtest=sql.walker.SqlEventWalkerUnparenthesizedValuesTests,sql.walker.SqlEventWalkerWithinGroupOrderedAggregateTests,sql.walker.SqlEventWalkerSetOpScopingGateTests,sql.walker.SqlEventWalkerSetOpQueryVariableTests test`
- Result:
  - `SqlEventWalkerWithinGroupOrderedAggregateTests`: 21/21 pass
  - `SqlEventWalkerSetOpScopingGateTests`: 17/17 pass
  - `SqlEventWalkerSetOpQueryVariableTests`: 7/7 pass
  - `SqlEventWalkerUnparenthesizedValuesTests`: 4/4 pass
  - Bundle total: 49 tests, 0 failures, 0 errors, 0 skipped
- Code-change conclusion for 19.1–19.3:
  - **19.1:** invalid-alias fixture retired; no parser/grammar/walker change required.
  - **19.2:** test expansion only (location/resolution matrix for `PERCENTILE_CONT`/`PERCENTILE_DISC`); no parser/grammar/walker change required.
  - **19.3:** completed via Entry 4.3 test expansion (`ARRAY_AGG` location/resolution matrix + set-op/compound-array integration); no parser/grammar/walker change required.

---

## Entry 20 — Qualified column variables on visible query aliases

**Status:** Complete (closed 2026-11-13): core parser fix implemented,
clause-position golden coverage added, targeted + full parser suites pass, and
the last open bound-query parity issue for the 5.0.0 → 5.1.4 upgrade is
resolved.  
**Kind:** Defect / resolution and diagnostic enhancement.

**Evidence:** The original s85047 TUPLE snippet uses
`sub.<Source Contact ID>` in a join against a visible derived-table alias.
5.0.0-3 reports no FATAL; current 5.1.4 reports
`QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS` because it looks up the
column-variable placeholder as a literal output name. The separate
ambiguous-column warning in that snippet is not automatically resolved by
this entry. The original input and comparison are in the local-only archive
under `comparison/runs/tuplewrap-differences-20261113/`.

**Reduced reproduction (verified against both JARs):**

```sql
SELECT sub.id
FROM (SELECT id FROM t) sub
JOIN b ON sub.<Contact ID> = b.<Contact ID>
```

Through `SQL` this has zero FATALs on 5.0.0-3 and one on current 5.1.4:
`Qualified column '<Contact ID>' ... was not found in output interface of query
alias 'sub'.` The AST identifies `sub.<Contact ID>` as a `column` substitution,
not a literal column. Parenthesizing the query and using the `TUPLE` endpoint
reproduces the same difference. An even smaller SELECT-list variant is
`SELECT sub.<Contact ID> FROM (SELECT id FROM t) sub`. The synthetic inputs,
both JAR results, and the typed-substitution AST are retained locally under
`comparison/runs/s85047-qualified-variable-20261113/`.

**Goal and resolution policy:**

- Recognize the typed, qualified column substitution in column resolution.
  Once the qualifier is proven visible under the existing scope rules, do not
  FATAL merely because the placeholder name is absent from that query alias's
  interface. Its eventual binding must name an actual exported column.
- Emit a **warning** for this deferred check,
  identify the variable, query alias, and source location, and explain the
  binding requirement. Do not present an unresolved variable as a validated
  concrete column. Whether and how binding is validated after substitution is
  a separate consumer/generation concern; do not claim it already happens.
- Preserve typed substitution collection, AST, dictionaries, symbol-table
  lineage, scope visibility, and normal handling of actual column names.
  Continue to FATAL for a missing or invisible qualifier and for a concrete
  column absent from a closed query interface. Do not suppress unrelated
  diagnostics, including s85047's ambiguity warning.
- This entry covers the **variable-outside** form. A concrete column read
  outside a query that exports a variable without a literal alias is a
  distinct case; do not automatically relax its validation.

**Phases and acceptance:**

1. Trace how typed substitutions and source qualifiers reach the resolution
   and diagnostic paths; reuse existing scope and diagnostic machinery.
2. Implement the targeted deferral and the warning described above. Verify
   `SQL` and `TUPLE`, SELECT and JOIN/ON placements, and visible
   derived-table and CTE aliases; exercise physical sources and nested scopes
   as controls.
3. Keep negative controls: `sub.missing` against
   `(SELECT id FROM t) sub` still FATALs; `ghost.<Contact ID>` with no visible
   `ghost` still FATALs; an inaccessible sibling or child alias remains
   invisible. Check substitutions and real columns in interfaces,
   dictionaries, and lineage without inventing a resolved column name.
4. Re-run the original s85047 and compare AST, substitutions, diagnostics,
   and resolution artifacts. Re-evaluate the seven historically classified
   variable-outside bound queries q5201 and q5286–q5291. The owner approved
   mirror-image bound queries q5314 and q5335 and snippets s8491 and s8492 for
   release with fixed-alias or concrete-column author remediation; their
   original FATALs are not parser fixes. Record results
   individually; do not presume all Pattern 6 cases have the same disposition.
5. Re-run the relevant bound-query and snippet comparisons and document any
   remaining regressions. Update directly affected consumer guidance. Close
   this entry only when these checks pass; no 5.0.0 → 5.1.4 extract parity
   sign-off before closure.

**Out of scope:** implementing broader variable binding, weakening concrete
column or alias validation, automatically accepting the other Pattern 6
cases, or changing the DATEADD test goldens.

---

## Entry 21 — QUERY variables in FROM/JOIN: author guidance

**Status:** Proposed for 5.1.4 remediation (2026-11-19); discussion pending,
not implemented.
**Kind:** Diagnostic enhancement.

### Shared evidence and sample for Entries 21–23

Keep this subquery as the discussion and regression fixture:

```sql
select partnercontact_id, partner_system_name, eab_entry_year_academic,
       sum(agg_fund_amount) as agg_fund_amount
from (<fao_calc.query.total_fund_amount_per_fund_type>) fund_agg
where eab_fund_type = 'pell_grant'
  and coalesce(imputed_flag, false) = false
group by partnercontact_id, partner_system_name, eab_entry_year_academic
```

In isolation, both 5.0.0 and 5.1.4 type the parenthesized variable as `query`;
removing its surrounding parentheses changes the occurrence to `tuple`.
5.0.0 collects all six input-column names under the variable in the table
dictionary in either form. Current 5.1.4 excludes the `query` form and reports
`UNRESOLVED_UNQUALIFIED_COLUMNS` for all six distinct names in the standalone
replay. Its enclosing query dictionary contains the four SELECT output names
and their definition tokens, not the WHERE or GROUP BY occurrence tokens.
The placeholder itself has no parsed child query body or query dictionary.

**Larger-query observation (owner-reported):** the same variable also occurs
without parentheses in a second subquery and is typed `tuple` there. The global
table dictionary contains SELECT-related columns but lacks the expected WHERE/
GROUP BY coverage from the affected subquery. Preserve this distinction from
the isolated replay; the complete larger statement is still needed to reproduce
its exact scope, token, and traversal-order effects.

**Owner direction:** 5.1.4 is correct not to turn a QUERY variable into a table
dictionary entry. Do not restore 5.0.0's indiscriminate table-dictionary behavior.
Typing, row-source participation, inferred columns, and binding validation are
separate concerns.

### Goal and proposal

Consider a warning when a FROM/JOIN row source is a QUERY variable, explaining
when a TUPLE variable would be preferable. Keep the QUERY form legal and retain
its contextual type. Diagnostic wording, applicability, and severity remain
open; this is not approval for a blanket warning.

### Reasonableness and decisions

- **Benefit:** explain the parentheses-driven distinction and help authors
  choose the intended snippet endpoint instead of encountering opaque column
  diagnostics.
- **Legitimate QUERY use:** `FROM (<q>) q` can intentionally require a query-body
  snippet such as `SELECT id FROM t`. A TUPLE occurrence can instead accept
  `schema.table` or a parenthesized query fragment such as `(SELECT id FROM t)`.
  The choice affects the snippet's grammar and who supplies its parentheses;
  switching the template alone is not necessarily a compatible correction.
- **Risk:** warning on every legal QUERY row source could create noise or
  incorrectly imply that a deliberate whole-query binding is invalid. Decide
  whether guidance should be unconditional, conditional, or documentation-only.
- Coordinate with Entry 23 so advice about authoring style is not confused with
  source-column validity or duplicated by unresolved-column diagnostics.

### Phases and acceptance

1. Characterize parenthesized QUERY and bare TUPLE occurrences in FROM and JOIN,
   with concrete snippet bindings, before selecting a guidance policy.
2. Agree warning conditions and wording. If implemented, identify the variable,
   occurrence location, contextual type, and endpoint/parenthesis implications.
3. Keep legitimate QUERY row sources accepted, preserve their AST type, and
   create no table-dictionary entry for them.
4. Add focused, one-query-per-method full-golden and position-based diagnostic
   tests for the agreed positive and negative cases; update consumer guidance
   only after the policy is approved.

**Out of scope:** rejecting all QUERY row sources, automatically retagging them
as TUPLE, changing lexer tokens, or implementing variable substitution itself.

**Design reference:** [typed AST substitution and endpoint distinctions](panto-variable-inheritance-using-bundles.md#typed-ast-substitution).

---

## Entry 22 — Conflicting types for one substitution name

**Status:** Proposed for 5.1.4 remediation (2026-11-19); discussion pending,
not implemented.
**Kind:** Diagnostic / substitution-map consistency enhancement.

**Evidence:** use the [Entry 21 sample](#shared-evidence-and-sample-for-entries-2123)
inside a larger statement that also uses the same variable as a bare TUPLE in
another subquery. The owner observed this mixed-type use; exact larger-query
reproduction and traversal-order characterization remain pending.

### Goal and proposal

At substitution registration, compare every second and subsequent occurrence's
contextual type with the type already registered for that name. Report a type
conflict rather than silently replacing the registry value. The owner's
preferred starting policy is to preserve the first registration and issue at
least a severe warning, potentially a fatal diagnostic. Final severity is not
yet decided.

### Reasonableness and decisions

- **Benefit:** one named binding should not silently acquire incompatible
  endpoint requirements or change source eligibility as traversal proceeds.
- **First-entry retention:** preserves the original evidence and prevents
  last-write-wins behavior. It does not by itself make a conflicting statement
  valid or eliminate order-dependent behavior; diagnostics and downstream
  handling must make that explicit.
- **Severity trade-off:** a fatal prevents potentially incompatible snippet
  composition; a severe warning preserves authoring/inspection workflows but
  must not be presented as proof that one snippet satisfies every occurrence.
- **Compatibility question:** decide whether all unequal contextual types
  conflict or whether explicitly justified compatible pairs are allowed.
  For example, a column-reference fragment may also be a valid scalar
  predicand. Do not silently coerce types or assume endpoint overlap proves
  equivalent composition behavior.
- Define identity using the existing variable-name normalization rules, and
  decide the registry lifetime across nested scopes, SCRIPT statements, and
  separate parser invocations. Do not accidentally introduce cross-parse state.
- Preserve each AST occurrence's grammar-derived type even if the global map
  retains the first type. Evaluate effects on dictionaries, binding, generation,
  serialization, and consumers that currently expect one type per name.

### Phases and acceptance

1. Inventory all substitution-registration paths and characterize current
   overwrite behavior, including the QUERY/TUPLE example in both source orders.
2. Agree conflict/compatibility, severity, registry lifetime, first-entry
   retention, and repeated-diagnostic policies before changing registration.
3. Diagnostics must identify the name, original/new types, and both occurrence
   locations/scopes. Detect third and fourth conflicting occurrences as well;
   decide whether they produce individual or aggregated diagnostics.
4. Test same-name/same-type reuse, each approved conflicting or compatible pair,
   nested/sibling scopes, reversed occurrence order, and parser reuse/reset.
   Assert full AST/maps/dictionaries and exact positioned diagnostics, with one
   SQL query per test method.

**Out of scope:** silently renaming variables, rewriting occurrence types,
selecting a usable binding for the author, or silently changing the public
substitution-map value shape.

**Design reference:** [typed AST substitution and bundles](panto-variable-inheritance-using-bundles.md#typed-ast-substitution).

---

## Entry 23 — Opaque QUERY-variable row-source column binding

**Status:** Proposed for 5.1.4 remediation (2026-11-19); discussion pending,
not implemented.
**Kind:** Resolution / symbol-table metadata enhancement.

**Evidence:** the [Entry 21 sample](#shared-evidence-and-sample-for-entries-2123)
has a visible FROM alias but no parsed body behind its QUERY placeholder.
Names in the enclosing SELECT's query dictionary establish output identity,
not successful resolution of the WHERE, GROUP BY, or SELECT input references.
The [query-substitution presumed-lineage report](<./514 defect and enhancement requirements/query-substitution-single-source-binding-defect.md>)
adds qualified and tuple controls, corpus cases, and a proposed placeholder
shape. Treat that shape as design input, not an approved public schema; preserve
the owner-specified diagnostic and design decisions below.

### Goal and proposal

Consider associating otherwise unresolved columns with a visible opaque
QUERY-variable row source, rather than reporting them as having no possible
source. Preserve `type=query` and the owner's rule that such a source does
**not** become a table-dictionary entry. Distinguish an assumed/deferred source
binding from validation against an actual exported query interface.

**Owner-specified warning (2026-11-19):** if columns are credited to the QUERY
variable as presumed outputs, still emit a **warning**, replacing the generic
unresolved-column diagnostic for those credited references. Use this message:

```text
Columns presumed output of Query Variable <variable name> include: <list of column names>
```

Include the actual substitution name and a deduplicated list of the column
names presumed to come from that occurrence's row source, across the relevant
clauses. Anchor the diagnostic's source location/hyperlink at the **QUERY
variable occurrence in the SQL text**, not at the first presumed column.
Do not suppress unrelated unresolved, ambiguous, or conflicting-type
diagnostics. This warning policy is specified; the source representation and
eligibility rules remain proposals pending discussion and implementation.

### Representation options and trade-offs

- **Owner's candidate:** represent the placeholder using an alias and a
  synthetic `def_queryN` scope containing the minimum interface/query-dictionary
  metadata needed to record columns required by surrounding clauses.
- **Benefit:** existing alias-to-query traversal could expose these dependencies
  without pretending the placeholder is a physical table.
- **Risk:** ordinary `def_queryN` payloads describe parsed scopes and exported
  outputs. A synthetic payload could falsely imply a real SELECT body or proven
  exports. An explicit opaque/inferred marker and substitution link may be
  necessary; their schema and consumer compatibility are not yet approved.
- **Alternative for discussion:** explicit opaque-row-source metadata linked
  from the existing alias, rather than fabricating a normal query scope.
  Compare consumer work and compatibility before choosing either format.
- Preserve an explicit author alias such as `fund_agg`. Decide whether the
  variable name is a source identity, a fallback alias, or both; do not overwrite
  aliases or derive colliding scope keys directly from variable text.
- Record all required SELECT/aggregate-argument, WHERE, GROUP BY, JOIN/ON, and
  other clause occurrences with their token evidence and roles. Do not add
  WHERE-only names to the enclosing SELECT's output dictionary or interface.
- Keep inferred requirements occurrence/scope-aware when one name appears in
  several subqueries. Do not let another occurrence's table dictionary or
  inferred columns leak into this scope as proof of resolution.
- Define ambiguity behavior for multiple visible opaque sources, or opaque
  sources mixed with physical/known query sources. A single opaque source does
  not justify choosing arbitrarily when several sources could supply a name.
- Replace generic unresolved reporting only for references actually credited
  under the approved policy, retaining the owner-specified presumed-output
  warning above. Decide how Entry 21 guidance relates without redundant advice.
  Real existence/export validation requires an available bound snippet; do not
  claim the parser already performs that later validation.

### Phases and acceptance

1. Characterize the standalone sample, a nested wrapper, and mixed QUERY/TUPLE
   reuse. Coordinate with Entry 22 so registry conflicts cannot silently change
   source eligibility.
2. Approve the opaque-source representation, assumed-versus-validated status,
   token ownership, scope visibility, ambiguity, and diagnostic policies.
3. Test qualified and unqualified references in SELECT/aggregate arguments,
   WHERE, GROUP BY, and JOIN/ON; include multiple-source ambiguity, repeated
   names, and inaccessible sibling/ancestor-scope controls.
4. Keep missing concrete columns on closed, parsed query interfaces invalid.
   Do not admit condition, column, predicand, in-list, or join-extension
   substitutions as row sources merely because they are variables.
5. Assert no QUERY-variable table-dictionary entry; correct clause lineage and
   token coverage; unchanged surrounding output boundaries; and explicit
   assumed/deferred status in the approved representation. Use independent
   full-golden tests and positioned diagnostics. Assert the exact presumed-output
   warning, its deduplicated column list, and its location/hyperlink target at
   the applicable QUERY variable occurrence. Repeated column uses must not
   duplicate names in the message; unrelated diagnostics must remain intact.
6. Document any published schema/consumer impact and update the symbol-table
   and dictionary guides before implementation is accepted.

**Out of scope:** restoring type-agnostic 5.0.0 table collection, inventing
verified exports or a real SQL body, bypassing normal concrete-subquery
validation, or implementing bundle resolution/execution.

**Design references:** [symbol-table scope boundaries](symbol-table-bucket-reference.md#sql-visibility-conventions),
[dictionary ownership](table-and-query-dictionary-design.md),
and [typed AST substitution](panto-variable-inheritance-using-bundles.md#typed-ast-substitution).

---

## Entry 24 — Successive IN lists silently overwrite column collection

**Status:** Confirmed defect; fourth 5.1.4 remediation (2026-11-19), following
Entries 21–23. Root cause traced; not implemented.
**Kind:** Dictionary / reference-token collection defect.
**Implementation starting point:** the evidence below is sufficient to begin
the targeted fix. Do not make repeating the exploratory probes a prerequisite.
This does not waive regression tests or post-fix verification.
The [compound IN report](<./514 defect and enhancement requirements/compound-in-table-dictionary-defect.md>)
adds SQL/QUERY, OR, mixed-predicate, direct boolean-output, and JOIN ON
reproductions, plus WHERE/HAVING controls. Its JOIN ON case reports losing even
the selected left-table `id`; verify the responsible path and include that
case, rather than assuming every reported manifestation has the same root cause.

### Report and evidence provenance

RMCP row **9922** repeatedly tests two column families inside CASE WHEN:

```sql
when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,
              end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)
  AND ('Future Donor') IN (constituent_code,constituent_code_1,
                          constituent_code_2,constituent_code_3,
                          constituent_code_4,constituent_code_5,
                          constituent_code_6,constituent_code_7,
                          constituent_code_8,constituent_code_9))
then 'Future Donor'
```

The next reported WHEN repeats the two families with `'Student'` in the
second predicate. The single source is `<[AMS].[donor_source]>`, and the
SELECT also includes `src_donor_id`.

- **Owner-reported full-query evidence:** 5.0.0 collects all twenty family
  names; 5.1.4 collects only `constituent_code*`. Both SQL and QUERY endpoints
  succeed without diagnostics. The AST contains all ten `end_date*` names,
  with forty occurrences of each in the complete query. The full original
  statement was not supplied; do not reconstruct it and call it row-9922
  replay evidence.
- **Locally verified current-parser evidence:** reduced complete queries
  reproduce the loss, and a live walker trace proves replacement of the
  pending-reference map at each ordinary IN-list frame exit. AST and output
  interface dependencies retain both families, including resolved source
  references, while the table dictionary omits the first family.
- **Baseline:** source HEAD `718fffd49fa97d79ae373f42deef85404ff9d3d7`;
  locally rebuilt `parse/target/pss-parse-5.1.4-1-fat.jar`, SHA256
  `91ca73c8b39268df6c4b5c63b6bdfcb84694e107686224cb5802aabfbcc9fecf`.
  This is an existing defect in that candidate, not an error introduced by
  new remediation code.
- **Old-version limitation:** the local cached 5.0.0 JAR threw an unrelated
  `NullPointerException` in `AbstractASTWalkerHelper.handleOneChild` via
  `exitAlias_identifier` on the reduced CASE probe. No successful local
  old-JAR CASE comparison is claimed. The owner's full-query comparison and
  the independently proven current-code overwrite must remain distinguished.

### Complete minimal reproduction and exact captured trace

This complete reduced query was traced successfully against current code:

```sql
select src_donor_id,
       case when '' in (end_date)
                 and 'Future Donor' in (constituent_code)
            then 'Future Donor'
            else 'Other'
       end as donor_type
from donor_source
```

**Expected:** table dictionary `donor_source` contains `src_donor_id`,
`end_date`, and `constituent_code`, each with its SQL occurrence token;
diagnostics remain empty.
**Actual:** only `src_donor_id` and `constituent_code` are present;
diagnostics are empty. The AST and CASE output dependency list retain both
predicate columns.

Live trace, reading the internal pending map rather than the published
symbol-table accessor:

```text
BEFORE PUSH (end_date) unresolved=[src_donor_id]
CHILD BEFORE POP (end_date) unresolved=[end_date]
PARENT AFTER POP (end_date) unresolved=[end_date]
BEFORE PUSH (constituent_code) unresolved=[end_date]
CHILD BEFORE POP (constituent_code) unresolved=[constituent_code]
PARENT AFTER POP (constituent_code) unresolved=[constituent_code]
FINAL DICTIONARY names: donor_source -> [constituent_code, src_donor_id]
```

The trace also shows that references pending before the first list can be
overwritten. An earlier SELECT item already has separately captured interface
site metadata, explaining why final `src_donor_id` retention must not be used
as evidence that the pending-map merge was safe.

### Completed characterization — do not repeat for discovery

These are observed reduced-query controls, not proposed results:

| Change from the affected CASE shape | Observed 5.1.4 result |
|------------------------------------|-----------------------|
| Two successive IN predicates, end-date family first | First family absent; second family retained |
| Reverse the two predicates | `end_date*` retained; `constituent_code*` absent |
| Rename the first column family | First family still absent |
| Replace the empty-string literal with a nonempty string | Same loss |
| Replace AND with OR | Same loss |
| Remove parentheses around the literal operands | Same loss |
| Replace the tuple-variable source with a physical table | Same loss |
| Keep only one IN predicate | That list's columns retained |
| Put the compound condition in WHERE instead of SELECT CASE | Both families retained |
| Reduce each list to one column | First column absent; second retained |

Repeated WHEN branches also lose earlier occurrence tokens: in the reduced
two-WHEN capture, the surviving second-family dictionary entries retain only
the final processed list's tokens, not both WHEN occurrences. Fixing just
distinct column-name membership is therefore insufficient.

### Confirmed code path and reason for silence

Source line anchors below describe the investigated baseline; use the method
names if later edits move lines.

1. [SqlParseEventWalker.enterIn_predicate_value](../src/main/java/sql/walker/SqlParseEventWalker.java#L11045)
   opens a frame using `pushDependentQueryContextForFrame(ctx)` and
   `pushSymbolTableWithParentVisibleScope()`. This happens for ordinary
   expression lists as well as SELECT subqueries. During SELECT traversal,
   FROM has not yet been processed, so these list-column tokens accumulate
   in the child's `unresolved_column` map.
2. `exitIn_predicate_value` calls
   [SqlParseSymbolTreeHelper.exitPredicateSubqueryFrame](../src/main/java/sql/symboltree/SqlParseSymbolTreeHelper.java#L19982)
   with merge kind `IN`. Its `isSubquery` test checks for the SELECT AST key.
   The ordinary-list branch calls `popPredicateFrameSymbolTableMerge` and
   returns without separately consuming/merging unresolved entries.
3. [popPredicateFrameSymbolTableMerge](../src/main/java/sql/symboltree/SqlParseSymbolTreeHelper.java#L20068)
   separately handles `dependent_queries`, strips frame-local keys, and calls
   [SqlASTWalkerHelper.popSymbolTablePutAll](../src/main/java/astwalkers/SqlASTWalkerHelper.java#L719).
   That method restores the parent and executes `symbolTable.putAll(symbols)`.
   It specially merges `table_alias`, but not `unresolved_column`. The child's
   entire unresolved map replaces the parent's map. Each later list repeats
   that destructive replacement.
4. AST construction is independent of this pending map, so it preserves the
   SQL references. SELECT-item dependency flattening also retains their names.
   However,
   [attachWalkCapturedSiteTokensToSelectItemDependencyRefs](../src/main/java/sql/symboltree/SqlParseSymbolTreeHelper.java#L3702)
   gets occurrence evidence from the surviving unresolved map; missing entries
   are skipped. The lost tokens are unavailable for dictionary materialization,
   and erased pending entries cannot generate unresolved-column diagnostics.
   Successful interface source binding is not proof of complete dictionary
   token collection.

This explains positional asymmetry, not an `end_date` keyword classification
issue. WHERE's already-visible source context avoids the observed failure in
the tested control; do not generalize that every use of IN loses columns.

**Historical corroboration:** the earlier walker at `3e83b60` handled
`exitIn_predicate_value` with substitution checking and `handleOneChild`, without
the current list-frame merge path. Current frame entry dates to `6d54818c`;
the shared predicate-frame helper path is attributed to `6d2adbea`. This is
source-history evidence, not a successful local 5.0.0 binary replay.

### Targeted implementation approach and invariants

- Prefer a central, bucket-specific accumulation contract in
  `SqlASTWalkerHelper.popSymbolTablePutAll`, subject to the bounded caller audit
  below, rather than an IN-only workaround. For a temporary frame explicitly
  being merged into its parent, `unresolved_column` must not be replaced:
  - Parent has no map: retain the child's eligible entries.
  - Parent has a map: merge child column keys and all distinct occurrence
    tokens into it, preserving parent-only and child-only references.
  - Both contain the same column: retain both sets of occurrence tokens,
    without duplication, mutable self-merges, or loss of token payload metadata.
  Detach the child's accumulation bucket before the shallow `putAll` and use
  the restored parent's existing bucket when merging. Check aliasing and
  mutation order; merging only after replacement cannot recover discarded
  parent entries. The existing special handling of `table_alias` demonstrates
  why accumulation buckets need explicit contracts rather than raw replacement.
- Reuse
  [mergeUnresolvedEntriesIntoCurrentScope](../src/main/java/sql/symboltree/SqlParseSymbolTreeHelper.java#L9828)
  and the existing `SqlASTWalkerHelper.mergeUnknownEntries` machinery where
  appropriate. Preserve names **and every distinct occurrence token**,
  including repeated names shared by parent and child lists. Avoid duplicate
  token insertion or mutable self-merges.
- The real SELECT-subquery branch already consumes unresolved entries,
  partitions inner-local versus outer-correlated references, publishes the
  query scope, and merges only deferred parent references. Preserve that
  behavior, `def_queryN` boundaries, dependency registrations, alias precedence,
  flag restoration, and balanced frame push/pop.
- This contract applies to **merge operations**, not automatically to every
  stack pop. Publishing a named scope retains its local references within that
  scope; discarding a frame must not publish them. A real subquery must first
  partition/filter references for parent eligibility. Preserve those caller
  responsibilities even if the accumulation merge itself is centralized.
  Do not indiscriminately deep-merge every symbol-table bucket or route publish/
  discard operations through the merge helper. If a caller cannot meet the
  eligibility contract, fix its boundary handling or retain a narrower
  non-subquery merge; do not flatten child scopes to force a generic solution.
- Do not compensate by scanning AST text for columns, using query output
  dictionaries as input dictionaries, or adding a warning in place of collecting
  valid references. This valid single-source SQL should remain diagnostic-free.
- No lexer/grammar changes or token-ID remapping are needed. This is separate
  from Entries 21–23: the tuple-variable example is a valid physical/tuple
  source, not a request to admit opaque QUERY variables to the table dictionary.

### Bounded caller audit and related regression guards

**Yes, a targeted audit is worthwhile.** The defect is in walker symbol-table
frame handling, not grammar recognition. Start from merge/pop implementations
and their callers, then map those callers to grammar productions for realistic
SQL regressions. Do not search every grammar rule or rerun the completed
IN-list investigation before implementing.

The source search performed while updating this proposal found two direct
implementation callers of `popSymbolTablePutAll`:

| Merge path | Callers / contexts to assess | Classification |
|------------|-----------------------------|----------------|
| `popPredicateFrameSymbolTableMerge` | IN values, EXISTS values, predicand subqueries, quantified comparisons | IN ordinary-list overwrite confirmed; other contexts are audit candidates or isolation controls, not confirmed defects |
| `popFrameAndMergeIntoParent` | Passthrough `exitIntersected_query`, passthrough `exitUnionized_query`, and insert-source primary exit after source finalization | Audit candidates; no additional defect or runtime failure established |

Relevant productions in
[SQLSelectParser.g4](../src/main/antlr4/sql/SQLSelectParser.g4) include
`in_predicate_value`, `exists_predicate_value`, `predicand_subquery`,
`quantified_comparison_predicate`, `intersected_query`, and `unionized_query`.
Passthrough union/intersection grammar frames can occur even without an actual
set operator. Inspect their existing finalization and scope ownership rather
than assuming every such frame contains transferable unresolved references.

For each distinct path, record:

1. Whether the exit merges, publishes, or discards the frame, and where
   unresolved entries are consumed, resolved, partitioned, or retained.
2. Whether both parent and child can contain eligible unresolved entries at
   merge time; whether overlapping column names refer to distinct SQL sites.
3. Whether the shared contract protects the path, or the caller intentionally
   preserves local entries under a named scope instead.
4. The existing observable regression that covers it, or a small new SQL
   regression needed to demonstrate accumulation or isolation.

Inspect any other raw symbol-table `putAll`/`put` near these frame boundaries
only to establish whether it can replace an accumulation bucket. Do not treat
AST assembly `putAll`, intentional scope publication, or resolved-bucket
replacement as this defect solely because the operation has the same name.
Keep the audit limited to reference loss and scope safety coupled to Entry 24;
record unrelated findings separately instead of silently expanding the fix.

Add a few representative regressions for distinct uncovered behaviors, not
a matrix of every grammar production. In particular, require preservation of
parent-only references, child-only references, and repeated-name occurrence
tokens. Include an IN in a later WHEN with no AND/OR between the lists, ordinary
comparison references before an IN, and nested expression/frame controls.
These are proposed regression cases; their exact SQL shapes have not yet been
replayed. Actual IN/EXISTS/scalar/quantified subqueries and set-operation sibling
boundaries are negative isolation controls, not permission to merge their
local references into the enclosing query.

### Future-agent prevention guard — deliver with the fix

Document the invariant in three complementary places:

- Add a mandatory frame-edit review rule under parser implementation rules in
  [AGENTS.md](../../AGENTS.md), and reference the same rule from
  [.github/copilot-instructions.md](../../.github/copilot-instructions.md).
  If a dedicated rule is useful, place it in `.cursor/rules/` and link it from
  both entry points; do not rely only on editor-specific automatic loading.
- Put the precise merge contract in the Javadoc beside
  `SqlASTWalkerHelper.popSymbolTablePutAll`, with caller obligations for
  filtering child-scope references and preserving token payloads.
- Enforce the contract with persistent SQL/extractor regression tests.
  Documentation alone cannot prevent a future implementation from silently
  reintroducing replacement.

Proposed mandatory review wording:

> Before changing symbol-table frame push/pop or merging, classify the exit as
> merge, publish, or discard. Merge operations must preserve accumulated
> unresolved references and all distinct occurrence tokens. Never let a shallow
> `putAll` replace an existing accumulation bucket. Establish parent eligibility
> before merging child references; preserve query and sibling-scope boundaries.
> Do not apply blanket deep merging to unrelated buckets. Validate both reference
> accumulation and scope isolation through observable SQL regression outputs.

These agent instructions and helper documentation are planned deliverables,
not changes already made by this workplan update.

### Regression work and acceptance

Start implementation from the confirmed merge defect above. Convert the
captured minimal SQL and characterized cases into persistent regression tests;
do not rerun the exploratory harness merely to rediscover the cause.

1. Prefer the existing
   [SqlEventWalkerPredicatesOperatorsSubstitutionsTests](../src/test/java/sql/walker/SqlEventWalkerPredicatesOperatorsSubstitutionsTests.java)
   or the nearest established CASE regression class. Use one query per test
   method, full extractor golden strings, and exact positioned diagnostics
   according to existing test conventions.
2. Cover the minimal CASE, reversed predicate order, AND/OR, repeated WHEN
   branches, both ten-column families, and surrounding SELECT items before
   and after CASE. Assert all expected names and exact occurrence tokens,
   not merely nonempty dictionaries or successful parsing.
3. For a complete two-WHEN fixture using each ten-column family once per WHEN
   and `src_donor_id` once, require **21 distinct input names** and **two
   occurrence tokens per family member**, plus the one `src_donor_id` token.
   This is a new regression fixture's acceptance shape, not a claim that the
   complete owner query was locally replayed. Keep `donor_type` as an output
   alias, not a source-table input.
4. Cover physical and tuple-variable sources, qualified references, NOT IN,
   the working single-list and WHERE controls, and SQL/QUERY endpoint parity.
   Cases not listed as observed above are required post-fix coverage, not
   already verified investigative results.
5. Run existing real IN-subquery and correlation tests, including closed
   interfaces and sibling scopes, to prove the fix does not flatten inner
   references into the parent. Keep legitimate unresolved diagnostics intact.
6. Complete the bounded caller audit above. Reuse existing passing regressions
   for covered paths and add focused observable SQL tests for uncovered
   accumulation/isolation behavior. Include no-AND WHEN traversal, repeated
   tokens across parent/child frames, and representative passthrough or
   insert-source paths only where the audit establishes relevant coverage gaps.
   Mark source-derived risks, reproduced defects, and passing controls distinctly.
7. Deliver the future-agent guard and merge-helper contract documentation with
   the fix. Verify that tests enforce the invariant rather than merely asserting
   that the documentation or helper exists.
8. Run the focused regression selection first, then the release suite for
   acceptance. Distinguish baseline reproduction failures from new failures
   caused by the patch. Verify current source before rebuilding the local
   parser/docs candidate; do not publish to Archiva without separate approval.
9. Add the report's JOIN ON case and its OR variant, asserting both tables'
   complete dictionaries including the selected `l.id`; retain WHERE/HAVING
   and separate single-IN select-item controls.

**Out of scope:** implementing Entries 21–23, changing substitution types or
dictionary eligibility, weakening true subquery encapsulation, altering lexer
identities, repairing the unrelated old-JAR CASE crash, or adding generator
features.

---

## Entry 25 — Datetime story completion and function-specific part lists

**Status:** Fifth 5.1.4 remediation (2026-11-20); deficiencies verified on the
existing local candidate. Not implemented.
**Kind:** Defect correction / completion of the Entry 1 Snowflake story.
**Owner direction:** correct the deficiencies below, including creating
multiple date/time-part lists where functions support different subsets.

### Scope and verified baseline

The [Entry 1 product story](#product-story-authoritative) requires structured
datetime ASTs, normal expression/substitution handling, part metadata excluded
from column resolution, and support for the named Snowflake datetime families,
including both TRUNC and TRUNCATE. Most of that implementation is present.
The add-family fields `level`, `increment`, and `value` are the approved
contract; `value` fulfills the story's date/timestamp-expression role. Do not
rename it to the literal key `"date part"`.

The following complete SQL inputs were replayed on 2026-11-20 through
`SqlParserAccess.executeTheParse(..., "SQL")` using the existing local
`parse/target/pss-parse-5.1.4-1-fat.jar` built on 2026-11-19
(SHA256 `91ca73c8b39268df6c4b5c63b6bdfcb84694e107686224cb5802aabfbcc9fecf`).
All had zero syntax errors; the deficiencies are AST/collection/semantic
classification results, not grammar-recovery failures. No correction code
was introduced by these probes.

| Complete baseline SQL | Observed deficiency |
|-----------------------|---------------------|
| `select truncate(a.ts, day) as bucket from events a` | Generic `function` AST; `day` incorrectly added to the `events` table dictionary; no diagnostics |
| `select truncate(a.ts, 'day') as bucket from events a` | Generic `function` AST instead of `datetime_function`; quoted part avoids phantom column but does not fix the missing datetime semantics |
| `select date_trunc('week_iso', a.ts) as bucket from events a` | `INVALID_DATETIME_PART` FATAL at the quoted part, despite documented Snowflake support |
| `select trunc(a.ts, 'week_iso') as bucket from events a` | Same false invalid-part FATAL |
| `select last_day(a.ts, 'hour') as bucket from events a` | No invalid-part diagnostic although HOUR is outside LAST_DAY's supported subset |
| `select time_slice(a.ts, 1, 'millisecond') as bucket from events a` | No invalid-part diagnostic although MILLISECOND is outside TIME_SLICE's supported subset |
| `select dateadd(day, date_part(dow, a.ts), a.ts) as shifted from events a` | `dow` classified as PostgreSQL-only; valid Snowflake composition receives a false `STATEMENT_MIXED_DIALECT_GRAMMAR` FATAL |

Passing controls were also replayed:

```sql
select dateadd(day, abs(a.n), coalesce(a.ts, a.other_ts)) as shifted
from events a
```

This builds named datetime arguments, collects `n`, `ts`, and `other_ts`, and
does not collect `day`. It emits only the expected Snowflake dialect warning.

```sql
select dateadd(day, <increment>, <date value>) as shifted
```

Both substitutions are `predicand`, the table dictionary is empty, and only
the expected Snowflake warning is emitted. `select trunc(a.ts, day) as bucket
from events a` also produces the correct datetime AST without a phantom `day`.
Retain these controls; the correction must not undo already delivered behavior.

### Substep 25.1 — Complete the datetime TRUNCATE alias

- Route unqualified datetime `TRUNCATE(value, part)` through the same structured
  contract as datetime TRUNC, retaining the authored `function_name`, expression
  subtree, part text/form, and valid part normalization.
- Correct both dedicated grammar routing and generic-routine promotion where
  applicable. Current `snowflake_datetime_trunc_expression` accepts only TRUNC;
  `maybePromoteRoutineInvocationToDatetimeFunction` also recognizes only
  `"trunc"` for this family.
- Bare parts must be metadata, not collected as source columns before or after
  promotion. Quoting the part is not an acceptable workaround for missing
  structured handling.
- Preserve neutral numeric TRUNC/TRUNCATE calls, schema-qualified same-name
  UDFs, and DDL `TRUNCATE TABLE`. Snowflake's datetime TRUNC/TRUNCATE takes two
  required arguments; do not infer a missing one-argument datetime overload
  from the numeric overload's optional scale.

### Substep 25.2 — Complete supported part and alias coverage

- Reconcile the supported canonical parts and documented variations for every
  delivered datetime family against Snowflake documentation. Include ISO-week
  spellings such as `week_iso` and `weekiso`, and other supported ISO/calendar/
  extraction/epoch forms in the families that actually accept them.
- Correct the verified false rejection of `week_iso` in DATE_TRUNC and TRUNC,
  and apply the resulting truncation-family rules to datetime TRUNCATE.
- Preserve authored spelling/case, quoted-versus-bare form, and expression
  structure while normalizing semantic identity. Test documented aliases, not
  just canonical names; do not invent abbreviations.
- Keep parts out of dictionaries and unresolved-reference handling without
  suppressing real columns named `day`, `year`, or other part names in ordinary
  expression positions.

### Substep 25.3 — Implement function-specific date/time-part lists

The current `DatetimePartRegistry` is explicitly an add-family registry but is
also used to validate diff, truncation, TIME_SLICE, and LAST_DAY. Membership in
one broad set is not proof that a part is valid for a particular function.

- Provide multiple named, immutable supported-part lists/sets for differing
  function families, backed by shared canonical-part and alias definitions.
  At minimum distinguish add/diff, extraction, truncation, TIME_SLICE, and
  LAST_DAY. Families with genuinely identical supported sets may share a set
  explicitly; do not force differing families to use the union of all parts.
- Associate each invocation and alias with its appropriate subset:
  DATEADD/TIMEADD/TIMESTAMPADD, DATEDIFF/TIMEDIFF/TIMESTAMPDIFF,
  EXTRACT/DATE_PART, DATE_TRUNC/TRUNC/TRUNCATE, TIME_SLICE, and LAST_DAY.
  Preserve PostgreSQL/shared extraction forms already accepted; a Snowflake
  allowlist must not globally reject legitimate other-dialect syntax.
- Canonicalize aliases before family-membership checks, with dialect/context
  accounted for where meanings differ. Keep validity and dialect affinity
  separate; a valid alias must not become invalid merely because it overlaps
  with another dialect.
- LAST_DAY's function-specific documentation lists YEAR, QUARTER, MONTH, WEEK
  and supported variations. Reject HOUR and other parts outside its verified
  set. TIME_SLICE permits YEAR through DAY for DATE input and additionally
  HOUR/MINUTE/SECOND for TIMESTAMP_NTZ input; reject MILLISECOND/MICROSECOND/
  NANOSECOND. Do not invent input types for untyped columns to enforce the
  DATE-versus-TIMESTAMP restriction.
- Check supported ISO variants for each relevant family explicitly. Where a
  general reference table and a function page differ, resolve and document the
  discrepancy before finalizing that family list rather than copying all
  extraction aliases into every family.
- Reuse these definitions for validation and consumer-facing discovery APIs;
  do not scatter independent hardcoded sets across grammar and walker exits.
  Existing weekday and START/END boundary sets remain distinct metadata
  domains, not date/time parts.
- Emit the existing positioned `INVALID_DATETIME_PART` diagnostic for a known
  part used in the wrong family, not only for unknown strings like `bogus`.

### Substep 25.4 — Correct shared-alias dialect classification

- Reconcile `ExtractDatetimeFieldAffinity` with the supported-part/alias data.
  Snowflake accepts `dow`; treating it as PostgreSQL-only causes the verified
  false mixed-dialect FATAL. Review related overlaps such as `doy` and `epoch`
  against documentation rather than assuming a Postgres-oriented spelling is
  exclusive to that engine.
- Preserve shared/neutral forms and genuinely target-specific affinity,
  statement hit counting, single-warning aggregation, mixed-dialect FATALs
  when genuinely warranted, and SCRIPT statement reset.
- Do not remove all dialect diagnostics or globally relabel PostgreSQL
  constructs as Snowflake to make the failing example pass.

### Substep 25.5 — Regression and acceptance

1. Add focused regressions in the existing datetime placement, trunc/extract,
   boundary/slice, metadata, and snippet test classes as appropriate. Use one
   query per method, complete extractor goldens, applicable access-layer
   snapshots, and exact positioned diagnostics. Existing tests that encode
   incorrect subset or affinity assumptions must be corrected deliberately,
   not blindly refreshed.
2. Convert every deficient baseline example above into a regression with the
   corrected result. Add numeric/DDL/UDF controls for TRUNCATE and bare/quoted,
   mixed-case, canonical/alias controls for changed part sets.
3. Test each family list's accepted names/aliases and representative
   cross-family negatives: a part accepted by one family must be rejected by
   a family that excludes it. Assert token/column collection, not merely parse
   success or list membership.
4. Verify complex expression operands and predicand substitutions still work
   for DATEADD, TIMEADD, and TIMESTAMPADD. Check genuine column references
   named like part constants alongside metadata slots.
5. Test the `dow` nested Snowflake example, shared-alias standalone cases,
   genuine Postgres-only/mixed-dialect controls, and SCRIPT isolation. Exercise
   SQL and relevant snippet endpoints without conflating syntax success with
   absence of semantic FATALs.
6. Run focused tests first and the required release verification before
   acceptance. Update the published datetime guidance and supported-part
   discovery contract; sync the vendor docs. Rebuild local parser/docs
   artifacts only as part of the authorized packaging workflow. Archiva
   publication remains prohibited without separate owner approval.

**Implementation references:** [datetime grammar](../src/main/antlr4/sql/SQLSelectParser.g4),
[datetime walker and generic-routine promotion](../src/main/java/sql/walker/SqlParseEventWalker.java),
[current add-family part registry](../src/main/java/sql/grammar/DatetimePartRegistry.java),
[extraction affinity classifier](../src/main/java/sql/diagnostics/ExtractDatetimeFieldAffinity.java),
and [weekday/boundary registry](../src/main/java/sql/grammar/SnowflakeDatetimeValueRegistry.java).

**External specifications checked for the baseline comparison:**
[Snowflake parts and aliases](https://docs.snowflake.com/en/sql-reference/functions-date-time#supported-date-and-time-parts),
[datetime TRUNCATE/TRUNC](https://docs.snowflake.com/en/sql-reference/functions/trunc2),
[TIME_SLICE](https://docs.snowflake.com/en/sql-reference/functions/time_slice),
and [LAST_DAY](https://docs.snowflake.com/en/sql-reference/functions/last_day).

**Out of scope:** PostgreSQL Slice E expansion, SQL-generator Entry 10,
implementing Entries 21–24, full SQL type inference or date arithmetic
execution, unrelated Snowflake function catalog expansion, automatic lexer-ID
golden replacement, and publication. Reuse existing lexer tokens and contextual
identifiers for part aliases; any unavoidable lexer addition must follow the
existing token-ID workflow rather than making part names newly reserved.

---

## Entry 26 — Preserve Unicode string-literal contents

**Status:** Complete in source (2026-11-20); local release JAR not rebuilt and
downstream integration not yet verified.
**Kind:** MCP / bulk preprocessing defect.
**Report:** [Unicode string-literal normalization defect](<./514 defect and enhancement requirements/unicode-string-literal-normalization-defect.md>).

The shared MCP/bulk normalizer converted Unicode dashes and spaces and removed
variation selectors inside string literals. Direct parser calls bypass that
normalizer; do not attribute every possible upstream mutation to the parser.
The fix in [SqlParseMcpSupport](../src/main/java/cli/SqlParseMcpSupport.java)
preserves quoted literal contents, including doubled and backslash-escaped
quotes, while retaining syntax normalization outside literals. It recognizes
comments and double-quoted identifiers so their apostrophes do not begin a
literal. No lexer tokens were changed.

**Verification:** [SqlParseUnicodeLiteralTest](../src/test/java/cli/SqlParseUnicodeLiteralTest.java)
checks exact ASTs for U+2013, U+2014, U+2212, U+00A0, U+202F, U+FE0F and
unchanged Unicode controls through SQL, QUERY, PREDICAND, and IN_LIST, using
direct access, JSON-RPC, stream, and bulk paths; it also covers base64, escaped
quotes, comments, and syntax normalization. The outdated normalization
expectation in [SqlParseMCPTest](../src/test/java/cli/SqlParseMCPTest.java)
was corrected. Focused Maven run: **27 tests, 0 failures, 0 errors, 1
skipped**. Ordinary release build and RMCP integration remain separate gates;
upstream text mutation before these transports is not addressed by this fix.

---

## Entry 27 — Cross-scope physical-column case fragmentation

**Status:** Reported black-box defect; source verification and fix pending.
**Report:** [cross-scope column-case fragmentation](<./514 defect and enhancement requirements/cross-scope-column-case-fragmentation-defect.md>).

### Goal and evidence

Merge occurrences of the same unquoted physical column across query scopes
under one case-insensitive global table-dictionary key, without duplicating
tokens. The report's predicate-subquery example publishes both `SRC_DONOR_ID`
and `src_donor_id` and duplicates an outer occurrence; same-scope references
merge correctly. Its case-swapped and production-shaped examples are complete.
This is not a token-ID renumbering issue.

### Phases and acceptance

1. Trace child-to-global dictionary merging and establish the actual cause;
   preserve local scope boundaries and the existing chosen canonical spelling.
2. Merge unquoted keys case-insensitively and deduplicate occurrence tokens
   without dropping any valid distinct occurrence. Respect dialect-specific
   semantics for delimited identifiers; do not blindly fold quoted names.
3. Add independent full-golden tests for same-scope, predicate-subquery,
   FROM-subquery or CTE, swapped casing, and case-sensitive delimited-identifier
   controls. Assert one appropriate key, exact token positions, and diagnostics.
4. Verify SQL/QUERY behavior and existing dictionary/scope regressions; update
   consumer dictionary guidance if the published contract requires clarification.

**Out of scope:** changing identifier spelling globally, merging distinct
quoted identifiers, or relaxing query-scope visibility.

---

## Entry 28 — Ordered select-alias lineage invariance

**Status:** Reported black-box defects; source verification and fix pending.
**Reports:** [consumer-alias iteration-order finding](<./514 defect and enhancement requirements/ordered-alias-iteration-order-defect.md>)
and [temporal-name finding](<./514 defect and enhancement requirements/ordered-alias-temporal-name-resolution-defect.md>).

### Goal and evidence

An earlier grounded select-list alias referenced by a later item must be
represented by the appropriate `queryN` reference under the existing ordered
alias policy. The first report changes only the consumer alias (`y` versus
`out_col`) and observes an incorrect physical-table reference in one case.
The second reports name/case-sensitive failures such as `BIRTH_DAY` and
`END_DATE`, including nested derived-table forms. Both show interface lineage
pointing to a physical column absent from the table dictionary.

The proposed map-iteration and temporal-classification explanations are
black-box hypotheses. Determine whether these are one or multiple code paths;
do not assume either cause or alter precedence before tracing it.

### Phases and acceptance

1. Reproduce both reports and trace alias grounding, source-order handling,
   final interface stamping, and any temporal-name handling.
2. Correct each demonstrated path so valid prior grounded aliases resolve
   according to source order, independent of consumer name, unrelated output
   keys, case, nesting, and qualification of the defining source.
3. Add one-query-per-method full-golden tests for both minimal reproductions,
   their passing controls, consumer-name permutations, added/reordered
   unrelated outputs, and temporal-looking alias case variants. Assert the
   `queryN` hop, underlying physical lineage, dictionaries, and diagnostics.
4. Retain forward-reference, physical-column, ungrounded-alias, and sibling
   scope controls; run existing ordered-alias and scope regressions and update
   the consumer policy guide if the contract changes.

**Out of scope:** making forward aliases visible or bypassing existing
scope/ambiguity rules merely to force a `queryN` reference.

---

## Entry 29 — Parser-owned structured lineage and explanation

**Status:** Proposed enhancement; design, release scope, and acceptance
thresholds pending.
**Handoff:** [complete 5.1.4 lineage plan](<./514 defect and enhancement requirements/complete-5.1.4-lineage-plan.md>).

### Goal and dependencies

Evaluate the handoff's proposal for the parser to publish a structured lineage
graph and a human-readable explanation derived from it, allowing RMCP to be a
thin consumer. The report describes desired scope topology, role participation,
token proof, typed leaves, recursive visibility, set operations, substitutions,
and optional source-impact tracing. Its proposed API and `PRESUMED` placeholder
shape are not yet approved. Coordinate query-variable representation and the
owner-specified warning with Entry 23, dictionary casing with Entry 27, and
ordered aliases with Entry 28; do not silently settle their open policies here.

### Phases and acceptance

1. Review the handoff, current parser consumer guides, available RMCP code and
   corpus evidence; verify external report paths and data availability rather
   than assuming they exist in this repository.
2. Define and approve the versioned payload, explanation contract, backward
   compatibility, scope visibility, token evidence, leaf types, unresolved and
   presumed states, and explicit release scope. Agree measurable corpus
   thresholds before claiming completion.
3. Build source-anchored golden cases for each specified construct, including
   dependent queries, correlated references, set operations, ordered aliases,
   windows, VALUES, modifiers, scripts, and DML. Verify legal parent/child
   traversal, no sibling leakage, cycle termination, and graph/prose agreement.
4. Implement and verify the agreed parser contract; assess the live corpus and
   report resolvable, presumed and unresolved rates, hop depth, role coverage,
   dependent-subquery coverage, and source-impact reachability where in scope.
5. Document the consumer contract and coordinate RMCP adoption separately;
   retain existing compatibility behavior until a versioned transition is agreed.

**Out of scope without further approval:** assuming a concrete placeholder
schema, claiming presumed outputs are verified, unrestricted deep scope search,
or changing RMCP in this repository.

---

## Retired documents (2026-09-28)

| File | Superseded by |
|------|----------------|
| `parser-defects-enhancements-workplan.md` | Entries **1–8** + **Completed baselines** |
| `extract-dialect-capture-workplan.md` | **Completed baselines** |
| `java-21-upgrade-workplan.md` | **Completed baselines** |
| `ddl-structured-options-parsing-workplan.md` | **Entry 9** |
| `sql-statement-generator-completion-workplan.md` | **Entry 10** |
| `coverage/sql_walker_exit_method_coverage-workplan.md` | **Entry 11** (+ gaps doc) |
| `helper-dead-code-hygiene-workplan.md` | **Entry 12** |

Historical prose: `git log --follow -- <path>`
