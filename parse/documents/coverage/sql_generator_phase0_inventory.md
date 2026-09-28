# SQL generator Phase 0 inventory (auto-generated)

Regenerate: `python3 parse/tools/sql_generator_phase0_inventory.py` (after `mvn -q generate-sources` in `parse/`).

## Summary

| Metric | Value |
|--------|------:|
| Parser rules (`RULE_*`) | 341 |
| Walker `exit*` methods (parsed blocks) | 291 |
| Mumble AST string keys (constants) | 183 |
| Generator switch routes (`on*`) | 124 |
| `SQLStatementGenerator` `on*` overrides | 35 |
| Keys referenced in generator emit paths | 71 |

### Per-rule emit status (grammar rule grain)

| Status | Rules | % of grammar |
|--------|------:|-------------:|
| `passthrough` | 112 | 32.8% |
| `partial` | 108 | 31.7% |
| `no_walker_exit` | 59 | 17.3% |
| `stub` | 27 | 7.9% |
| `complete` | 18 | 5.3% |
| `missing` | 14 | 4.1% |
| `n/a_internal` | 2 | 0.6% |
| `gap` | 1 | 0.3% |

**Generation gap (rules without full emit):** `missing` + `gap` + `stub` + `no_walker_exit` = **101** of **341** rules (29.6%).

**Regeneration-ready (complete + partial + passthrough):** **238** rules (69.8%).

### Per mumble key (AST grain)

| Key emit status | Keys |
|-----------------|-----:|
| `missing` | 48 |
| `stub` | 47 |
| `partial_helper` | 36 |
| `complete` | 20 |
| `complete_helper` | 19 |
| `n/a_internal` | 15 |

## Status legend

| Rule status | Meaning |
|-------------|---------|
| `complete` | All regen-relevant AST keys for this rule have implemented generator coverage |
| `partial` | Mix of complete and stub/missing keys, or helper-only emit |
| `stub` | Keys route to abstract `on*` stubs only (`handleStructured` → `Unexpected node`) |
| `missing` | AST keys not in generator switch / emit helpers |
| `gap` | Mixed missing + stub |
| `passthrough` | Walker exit aggregates children only (no direct AST keys) |
| `n/a_internal` | Symbol-table / internal keys only |
| `no_walker_exit` | No `exit*` in `SqlParseEventWalker` for this grammar rule |

Full table: [`sql_generator_phase0_inventory.csv`](sql_generator_phase0_inventory.csv)
