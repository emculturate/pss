# Agent workflow (PSS / pss-parse)

## PSS parse consumer documentation — read before implementing consumers

The **`pss-parse-docs`** Maven artifact (`pss-parse-docs-5.1.3-1.jar`) is the companion documentation bundle for **`pss-parse-5.1.3-1`** (fat jar / `513` symbol-table format). It packages markdown guides that describe what the parser emits and how to walk symbol tables, dictionaries, and modifier lineage.

**Agents must not guess symbol-table semantics.** Before writing or changing code that calls the parser, explains parse results, traces column lineage, or validates bucket payloads:

1. **Read the skill** `.cursor/skills/pss-parse-consumer-docs/SKILL.md` to locate the correct document for the task and the fastest path to open it (vendor tree, JAR, or manifest).
2. For explain / lineage / tracing work, also read `.cursor/skills/parse-and-explain/SKILL.md`.
3. Start with **`symbol-table-bucket-reference`** for scopes, buckets, `def_*` keys, `dependent_queries`, and the two consumer tracing algorithms.
4. Pull additional docs from the manifest when the task touches dictionaries, PIVOT/UNPIVOT, grammar coverage, or window buckets.

### Quick paths (API version `5.1.3`)

| Source | Path |
|--------|------|
| Vendor mirror (preferred in this repo) | `docs/vendor/pss-parse/5.1.3/` |
| Machine-readable index | `docs/vendor/pss-parse/5.1.3/pss-parse-docs.manifest.json` |
| Canonical editable source | `parse/documents/` (synced into vendor + JAR on build) |
| Built docs JAR | `pss-parse-docs/target/pss-parse-docs-5.1.3-1.jar` after `mvn -pl pss-parse-docs package` |
| JAR entry layout | `META-INF/pss-parse/5.1.3/*.md` and `META-INF/pss-parse/pss-parse-docs.manifest.json` |

### Document roles (from manifest)

| `id` | Use when |
|------|----------|
| `symbol-table-bucket-reference` | Symbol table structure, buckets, visibility, recursive tracing, source impact tracing |
| `table-and-query-dictionary-design` | Global vs per-scope `table_dictionary` / `query_dictionary` collection rules |
| `relational-modifier-resolution-policy` | PIVOT/UNPIVOT, `derivation` buckets, modifier naming |
| `sql-grammar-extensions-since-2026-01-01` | New grammar / endpoint coverage in 5.1.3+ |
| `window-query-dictionary-policy` | `window_partition_by` / `window_ordered_by` vs `query_dictionary` |
| `ordered-select-list-output-alias-policy` | Ordered intra-select-list output alias refs, `interface` `queryN` lineage |

### Version alignment

- **Maven artifact version** (`5.1.3-1`) matches **`pss-parse`** coordinates.
- **API version** (`5.1.3`) is the docs directory segment and `parserApiVersion` in the manifest.
- **`513` / `symbolTableFormat: "5.1.3"`** in downstream tools means published `def_queryN` scopes — use the **5.1.3** doc bundle, not 5.1.2 lineage assumptions.

### Extract a single doc from the JAR (when vendor tree is missing)

```bash
API=5.1.3
JAR=pss-parse-docs/target/pss-parse-docs-5.1.3-1.jar
unzip -p "$JAR" "META-INF/pss-parse/${API}/symbol-table-bucket-reference.md" | less
unzip -p "$JAR" "META-INF/pss-parse/pss-parse-docs.manifest.json"
```

Or list entries: `jar tf "$JAR" 'META-INF/pss-parse/**'`

## Parser implementation rules (this repository)

- **Relational modifiers:** `.cursor/rules/relational-modifier-resolution.mdc` → `parse/documents/relational-modifier-resolution-policy.md`
- **Do not auto-refresh pivot/unpivot goldens** without user confirmation (see rule file).
- Regenerate vendor docs after editing `parse/documents/*.md`: `./scripts/sync-pss-parse-docs-vendor.sh 5.1.3 5.1.3-1` or `mvn -pl pss-parse-docs package`.
