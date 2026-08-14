---
name: pss-parse-consumer-docs
description: >-
  Locate and read PSS parser consumer documentation from the pss-parse-docs JAR
  bundle or vendor mirror. Use when implementing or explaining code that calls
  pss-parse, consumes symbol tables, traces column lineage, walks dictionaries,
  handles PIVOT/UNPIVOT derivation, window buckets, or grammar coverage for 5.1.3+.
---

# PSS parse consumer documentation

## When to use

Apply this skill **before** implementing or modifying:

- Symbol-table walkers, explainers, lineage tracers, or validators
- Code that reads `parse.symbolTable`, `tableDictionary`, `queryDictionary`, or `interface`
- RMCP / MCP parse-and-explain tooling for **`5.1.3`** / **`513`** output
- Consumer tests that assert bucket shapes or tracing behavior

Do not infer bucket semantics from test strings alone — read the bundled docs.

## Artifact identity

| Field | Value (current) |
|-------|-----------------|
| Parser artifact | `pss-parse` @ `5.1.3-1` |
| Docs artifact | `pss-parse-docs` @ `5.1.3-1` (JAR name: `pss-parse-docs-5.1.3-1.jar`) |
| API / docs version | `5.1.3` |
| Published scope prefix | `def_queryN`, `def_unionN`, … (`513` format) |

## Resolution order (pick the first that exists)

1. **Vendor mirror** (fastest in pss repo): `docs/vendor/pss-parse/<api-version>/`
2. **Manifest** in that folder: `pss-parse-docs.manifest.json` — lists each `id`, `role`, and `path`
3. **Built JAR**: `pss-parse-docs/target/pss-parse-docs-<parser-version>.jar`
4. **JAR classes copy** (after Maven build): `pss-parse-docs/target/classes/META-INF/pss-parse/<api-version>/`
5. **Canonical source** (parser authors): `parse/documents/` (may be ahead of vendor until sync/build)

Read `docs/vendor/pss-parse/latest.version` for the current API version string.

## Read the manifest first

Open `pss-parse-docs.manifest.json` and choose documents by `id` and `role`:

```json
{
  "parserArtifact": "pss-parse",
  "parserVersion": "5.1.3-1",
  "parserApiVersion": "5.1.3",
  "documents": [ { "id": "...", "role": "...", "path": "..." } ]
}
```

Map tasks to docs:

| Task | Document `id` |
|------|----------------|
| Buckets, scopes, tracing algorithms, `dependent_queries` | `symbol-table-bucket-reference` |
| `table_dictionary` / `query_dictionary` collection | `table-and-query-dictionary-design` |
| PIVOT/UNPIVOT, `derivation` | `relational-modifier-resolution-policy` |
| New SQL grammar / endpoints | `sql-grammar-extensions-since-2026-01-01` |
| Window PARTITION BY / ORDER BY buckets | `window-query-dictionary-policy` |

## Extract from the JAR (no vendor tree)

```bash
API=5.1.3
JAR="pss-parse-docs/target/pss-parse-docs-5.1.3-1.jar"

# List bundled docs
jar tf "$JAR" 'META-INF/pss-parse/**'

# Manifest
unzip -p "$JAR" META-INF/pss-parse/pss-parse-docs.manifest.json

# Primary symbol-table guide
unzip -p "$JAR" "META-INF/pss-parse/${API}/symbol-table-bucket-reference.md"

# Versioned copy at JAR root (same content)
unzip -p "$JAR" "symbol-table-bucket-reference-pss-parse-${API}.md"
```

For downstream projects that depend on the Maven artifact, unpack with the dependency plugin (see `pss-parse-docs/README.md`) and point agents at `META-INF/pss-parse/<api-version>/`.

## Consumer obligations (from symbol-table guide)

When reading **published** symbol tables:

- Consume **`def_*`** payloads only — not walk-time `queryN` keys on finalized trees
- Treat **`interface`** as the public export boundary of each scope
- Pair each clause archive bucket with **`dependent_queries`** entries whose `type` matches that bucket name
- Do not skip scopes when tracing — parents see only child **`interface`** columns
- For PIVOT/UNPIVOT, use **`derivation`** (singular), not `derivations`

## Related skill

Lineage / explain implementation: `.cursor/skills/parse-and-explain/SKILL.md`
