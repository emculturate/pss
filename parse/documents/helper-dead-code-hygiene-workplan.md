# Helper dead-code hygiene — short workplan

**Status:** 🚧 Phases A–B done; C–D optional (Aug 2026)  
**Origin:** Post–Phases 1–20 cleanup assessment (JaCoCo vs caller-audit)  
**Primary code:** `SqlParseSymbolTreeHelper`, `SqlASTWalkerHelper`, `InterfaceASTWalkerHelper`  
**Related (historical):** `symbol-table-resolution-consolidation-worklist.md` Step F; `parse/documents/coverage/`

---

## Goals

1. Remove **confirmed zero-caller** public (and orphaned private) methods in the huge helpers.
2. Keep intentional thin domain facades (`toLiveScopeKey`, flatten-clause wrappers, diagnostic overloads).
3. Use **caller audit first**; JaCoCo only as a follow-on heat map (not a delete oracle).

---

## Policy

| Do | Don't |
|----|--------|
| Delete methods with **no Java call sites** outside their own definition | Delete ANTLR `exit*` / visitor hooks because grep shows one hit |
| Sync `tools/extract_symbol_tree.py` allowlist when removing listed names | Mass-inline semantic rename wrappers |
| Treat uncovered-but-called paths as **test gaps**, not dead code | Trust a stale `jacoco.xml` / gap CSV alone |
| Prefer small, gate-green batches | Wide golden churn for hygiene |

**Gate after each batch:**

```bash
cd parse
mvn -Psmoketest-quality-gate test
```

---

## Phases

### Phase A — Tier 1 dead public API ✅ DONE (Aug 2026)

Caller-audited definition-only methods removed. Gate: **239/239** (`mvn -Psmoketest-quality-gate test`).

**`SqlParseSymbolTreeHelper`** (17 methods)

- Relational-modifier leftovers: `applyRelationalModifierBucketDerivedSourceExpansions`, `applyRelationalModifierBucketDerivedSourceExpansionsToClauseColumnList`, `expandRelationalModifierDerivedColumnLineageAcrossArchivedScope`, `extractRelationalModifierInListColumnNames`
- INSERT leftovers: `populateImplicitInsertTargetColumnsFromSourceDictionary`, `buildInsertInterfaceFromSource`, `extractInterfaceReferenceEntriesAtPosition`
- UPDATE leftovers: `getUpdateNode`, `resolveUpdateQualifiedUnresolvedColumnsToInputTables`, `resolveUpdateQualifiedUnresolvedColumnsToCteSources`
- Misc: `getParentSymbolTable`, `annotateSubqueryReference`, `hasUnqualifiedUnknownWithMultipleViableSources`, `querySourceCanProvideColumn`, `isValuesSourceReference`, `addCurrentScopeValuesAliasMapping`
- Deprecated stub: `stripInheritedVisibleAliasesFromPublishedTree`

**`SqlASTWalkerHelper` (+ interface decls)** (9 methods)

- Large unused: `reconcileExplicitAliasReferencesAgainstNonTableSources`
- Scoping / VALUES leftovers: `scopeTableCollectionToCurrentLevel`, `scopeQueryCollectionForSetOperationValidation`, `addColumnTokenToColumnDict`, `consolidateValuesStatementSymbolTable`
- Orphaned private / formatting: `normalizeUnresolvedColumnItem`, `findTableAliasIgnoreCase`, `formatInterfaceValuesListWithLocations`, `formatInterfaceColumnReferences`

Also pruned matching names from `tools/extract_symbol_tree.py` `MOVE_METHODS` and dropped the two decls from `InterfaceASTWalkerHelper`.

### Phase B — Cascading private orphans ✅ DONE (Aug 2026)

Post–Phase A caller re-scan; all deleted. Gate: **239/239**.

**`SqlParseSymbolTreeHelper`:** `applyConvertEgressExpandedDerivedSourceLineageToReferenceList`, `operandQualifierMatchesRelationalModifierSource`, `normalizePivotDerivedComponent`, `hasDirectQueryParticipant`, `appendInterfaceReferenceEntries`, `mergeContextListAliasesFromScopeTree`

**`SqlASTWalkerHelper`:** `getASTWALKER_COLUMN_KEY`, `getASTWALKER_UNKNOWN_KEY`, `isTopLevelSymbolScope`, `setContainsIgnoreCase`, `mapContainsValueIgnoreCase`, `splitExplicitlyQualifiedUnknownEntriesFromUnqualified`, `validateFilterReferences`

Kept shared lineage helpers still used by live convert/egress paths (`expandRelationalModifierDerivedColumnLineageIn*`). Post-B orphan rescan: **0** remaining in either helper.

### Phase B.1 — Walker orphans ✅ DONE (Aug 2026)

Caller-audited definition-only methods removed from `SqlParseEventWalker` (not relocated — walk helpers otherwise stay in the walker):

- `buildPivotDerivedColumnNames` (superseded by `buildPivotStructuredDerivedColumns`)
- `extractPivotAggregateColumnNames`
- `extractPivotAggregateDependencyColumns`
- `getWalker()`

### Phase C — Fresh JaCoCo heat map (optional)

```bash
cd parse && mvn verify   # jacoco-report at verify
```

Focus report on the three megaclass files. Pair every 0%-covered method with a caller audit before any further deletes. Refresh `parse/documents/coverage/` only if useful.

### Phase D — Wrapper hygiene (low priority; mostly skip)

- Delete only remaining **deprecated zero-caller rename stubs**.
- Do **not** inline `toLiveScopeKey`, interface/clause flatten wrappers, or diagnostic overload chains.

---

## Out of scope

- Architectural consolidation of resolution (closed in Phases 1–20)
- Writing new tests for under-covered live branches (coverage sprint, not hygiene)
- DDL options / statement-generator plans

---

## Done when

- [x] Phase A methods gone; interface + extract script synced
- [x] Smoketest quality gate green (239/239)
- [x] Phase B orphan private scan clean (0 remaining)
- [ ] (Optional) Phase C JaCoCo refresh noted or filed under coverage docs
