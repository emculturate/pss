# Agent workflow (PSS / pss-parse)

## PSS parse consumer documentation — read before implementing consumers

The **`pss-parse-docs`** Maven artifact (`pss-parse-docs-5.1.4-1.jar`) is the companion documentation bundle for **`pss-parse-5.1.4-1`** (fat jar / `513` symbol-table format). It packages markdown guides that describe what the parser emits and how to walk symbol tables, dictionaries, and modifier lineage.

**Agents must not guess symbol-table semantics.** Before writing or changing code that calls the parser, explains parse results, traces column lineage, or validates bucket payloads:

1. **Read the skill** `.cursor/skills/pss-parse-consumer-docs/SKILL.md` to locate the correct document for the task and the fastest path to open it (vendor tree, JAR, or manifest).
2. For explain / lineage / tracing work, also read `.cursor/skills/parse-and-explain/SKILL.md`.
3. Start with **`symbol-table-bucket-reference`** for scopes, buckets, `def_*` keys, `dependent_queries`, and the two consumer tracing algorithms.
4. Pull additional docs from the manifest when the task touches dictionaries, PIVOT/UNPIVOT, grammar coverage, or window buckets.

### Quick paths (API version `5.1.4`)

| Source | Path |
|--------|------|
| Vendor mirror (preferred in this repo) | `docs/vendor/pss-parse/5.1.4/` |
| Machine-readable index | `docs/vendor/pss-parse/5.1.4/pss-parse-docs.manifest.json` |
| Canonical editable source | `parse/documents/` (synced into vendor + JAR on build) |
| Built docs JAR | `pss-parse-docs/target/pss-parse-docs-5.1.4-1.jar` after `mvn -pl pss-parse-docs package` |
| JAR entry layout | `META-INF/pss-parse/5.1.4/*.md` and `META-INF/pss-parse/pss-parse-docs.manifest.json` |

### Document roles (from manifest)

| `id` | Use when |
|------|----------|
| `symbol-table-bucket-reference` | Symbol table structure, buckets, visibility, recursive tracing, source impact tracing |
| `table-and-query-dictionary-design` | Global vs per-scope `table_dictionary` / `query_dictionary` collection rules |
| `relational-modifier-resolution-policy` | PIVOT/UNPIVOT, `derivation` buckets, modifier naming |
| `sql-grammar-extensions-since-2026-01-01` | New grammar / endpoint coverage in 5.1.4+ |
| `window-query-dictionary-policy` | `window_partition_by` / `window_ordered_by` vs `query_dictionary` |
| `ordered-select-list-output-alias-policy` | Ordered intra-select-list output alias refs, `interface` `queryN` lineage |
| `global-table-dictionary-cte-alias-policy` | 5.1.4 global `tableDictionary`: physical/tuple only; CTE via `table_alias` |
| `set-operation-interface-duplicate-output-names-policy` | UNION / INTERSECT / EXCEPT branch interface distinctness; duplicate output names and author remediation |

### Panto platform design (repo-only — not in `pss-parse-docs` JAR)

These documents live under `parse/documents/` and describe **Query Tool / Panto product semantics** (templates, bundles, resolution, generation). They are **not** synced into the parser docs manifest. Agents must read them before **redesigning or refactoring** substitution variables, snippet endpoints, `substitutionsMap` / resolution-set behavior, or SQL generation from template ASTs — the parser and walker must continue to enforce the design justifications they describe.

| Document | Use when |
|----------|----------|
| [`panto-variable-inheritance-using-bundles.md`](parse/documents/panto-variable-inheritance-using-bundles.md) | How Panto types variables from grammar location, parses snippets per endpoint, builds substitution maps from sequenced bundle imports, forms resolution/unresolved sets, and generates final SQL via depth-first AST traversal |
| [`panto-language-enhancements-grammar-safe-dynamic-expansion-2.md`](parse/documents/panto-language-enhancements-grammar-safe-dynamic-expansion-2.md) | Proposed cardinality / sequence substitution extensions ([5.1.4 workplan](parse/documents/sql-grammar-5.1.4-workplan.md) Entry **8**); read **after** the bundles concept-of-operations doc |

### Version alignment

- **Maven artifact version** (`5.1.4-1`) matches **`pss-parse`** coordinates.
- **API version** (`5.1.4`) is the docs directory segment and `parserApiVersion` in the manifest.
- **`513` / `symbolTableFormat: "5.1.4"`** in downstream tools means published `def_queryN` scopes — use the **5.1.4** doc bundle, not 5.1.2 lineage assumptions.

### Extract a single doc from the JAR (when vendor tree is missing)

```bash
API=5.1.4
JAR=pss-parse-docs/target/pss-parse-docs-5.1.4-1.jar
unzip -p "$JAR" "META-INF/pss-parse/${API}/symbol-table-bucket-reference.md" | less
unzip -p "$JAR" "META-INF/pss-parse/pss-parse-docs.manifest.json"
```

Or list entries: `jar tf "$JAR" 'META-INF/pss-parse/**'`

## Parser implementation rules (this repository)

- **Test suite terminology:** A request for the "full suite", "full test suite", or "all tests" means the default **result-accuracy suite**, excluding opt-in slow corpus regressions and manual latency/benchmark diagnostics. Use the Testing sidebar's `pss-quiet` configuration or Maven without `release-tests` / `diagnostic-tests`. Only enable `pss-release` / `-Prelease-tests` when slow/release regressions are explicitly requested; only enable `pss-diagnostics` / `-Pdiagnostic-tests` for explicitly requested diagnostic probes. If there is any uncertainty about the intended set, ask the user which set they mean before running it. Apply this rule to delegated agents too. Report the executed set and skips accurately; never imply that accuracy-only success validates slow regressions or release readiness. See [test suite selection](README.md#test-suite-selection).
- **Lexer additions - automatic required workflow:** Whenever a task adds lexer items, follow [the lexer token-ID policy](.cursor/rules/lexer-token-id-golden-policy.mdc) without waiting for a separate request. Before editing, generate and snapshot `parse/target/generated-sources/antlr4/sql/SQLSelectParserLexer.java`; append new keywords immediately before `Identifier`; regenerate and snapshot the after image. Compare the generated Java token constants with `tools/token_id_worklist_from_vocab.py`. Include the entire named before/after token-ID table, sorted highest ID first, in the final response/summary, together with the additions and expected increment. Include every shifted token, even those absent from test failures. Existing golden corrections are manual IDE work for the developer, not automatic agent edits.
- **Open parser / grammar / walker work (5.1.4 line):** [`parse/documents/sql-grammar-5.1.4-workplan.md`](parse/documents/sql-grammar-5.1.4-workplan.md) — single master plan (Entries 1–12). Older `*-workplan.md` files in `parse/documents/` are retired stubs.
- **Set-op convert egress (Phase 2.8 + 15.6):** `.cursor/rules/set-op-convert-egress-scoping.mdc` — bundle build vs egress reads, S4 sibling isolation, no post-bundle fallback; canonical test `setOpSiblingIsolationInvariantV0Test`
- **Relational modifiers:** `.cursor/rules/relational-modifier-resolution.mdc` → `parse/documents/relational-modifier-resolution-policy.md`
- **Panto variables & snippets:** `parse/documents/panto-variable-inheritance-using-bundles.md` — platform semantics to preserve when changing variable types, endpoints, or substitution/resolution behavior (see **Panto platform design** above).
- **Do not auto-refresh pivot/unpivot goldens** without user confirmation (see rule file).
- Regenerate vendor docs after editing `parse/documents/*.md`: `./scripts/sync-pss-parse-docs-vendor.sh 5.1.4 5.1.4-1` or `mvn -pl pss-parse-docs package`.
