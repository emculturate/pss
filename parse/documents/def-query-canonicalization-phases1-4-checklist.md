# Def Query Canonicalization: Phases 1-4 Checklist

> **Full roadmap (Phases 1–11):** see [`symbol-table-resolution-consolidation-worklist.md`](symbol-table-resolution-consolidation-worklist.md).

## Scope
- Target file: parse/src/main/java/sql/symboltree/SqlParseSymbolTreeHelper.java
- Constraint: no DELETE-logic refactor in this pass.
- Goal: canonical read path via def_* resolution first, with minimal write-path disruption.
- Note: Most tests with golden values in their symbol tble entries will now fail because we've changed the default format of the symbol table keys in the map to start with def_. Don't try to make your next changes conform to tests outside those mentioned in this plan for now because you will move in th wrong direction.
- Rule: query references (and similar object references like union, intersect, values) should all take the form of def_* when used as the key value in the symbol tree's hashmap, but should retain their unprefixed form (e.g., queryN, valuesN...) whenever referenced as a value, alias, or in other places in the symbol table construction. Code must translate between these representations whenever you try to use the one format to locate the other.

## Phase Checklist
- [x] Phase 1: Add query-key normalization helper for def_* lookup.
- [x] Phase 2: Update getQueryDefinitionSymbol to prefer normalized def_* lookup across current and ancestor scopes.
- [x] Phase 3: Keep unaliased query handle registration stable (queryN map + alias self-mapping).
- [x] Phase 4: Preserve unresolved transfer compatibility on queryN while still publishing canonical def_queryN payload.

## Exact Patch Chunks Applied

### Chunk A: Add normalized def-key helper and route lookups through it
```diff
@@
+	private String normalizeQueryScopeDefinitionKey(String queryKey) {
+		if (queryKey == null || queryKey.isBlank()) {
+			return null;
+		}
+		if (queryKey.startsWith("def_")) {
+			return queryKey;
+		}
+		if (!isQuerySourceReference(queryKey)) {
+			return null;
+		}
+		return "def_" + queryKey;
+	}
@@
 	public Object getQueryDefinitionSymbol(String queryKey) {
 		if (queryKey == null || queryKey.isBlank()) {
 			return null;
 		}
+
+		String definitionKey = normalizeQueryScopeDefinitionKey(queryKey);
+		if (definitionKey != null) {
+			Object normalizedDefObj = findInCurrentOrAncestorSymbolTables(definitionKey);
+			if (normalizedDefObj == null) {
+				normalizedDefObj = findInCurrentOrAncestorSymbolTablesRecursive(definitionKey);
+			}
+			if (normalizedDefObj != null) {
+				return normalizedDefObj;
+			}
+		}
@@
-		if (queryKey.startsWith("def_")) {
-			return directObj;
+		if (queryKey.startsWith("def_")) {
+			if (directObj != null) {
+				return directObj;
+			}
+			Object scopedDefObj = findInCurrentOrAncestorSymbolTables(queryKey);
+			if (scopedDefObj != null) {
+				return scopedDefObj;
+			}
+			return findInCurrentOrAncestorSymbolTablesRecursive(queryKey);
 		}
```

### Chunk B: Keep unaliased query handle stable and preserve unresolved compatibility move
```diff
@@
 			if (alias != null) {
 				walker.collectTableAlias(alias, queryName);
 				recordLocalFromRegisteredAlias(alias);
 			} else {
-				walker.symbolTable.put(queryName, queryName);
+				walker.symbolTable.put(queryName, new HashMap<String, Object>());
 				upsertCurrentTableAliasMapping(queryName, queryName);
 				recordLocalFromRegisteredAlias(queryName);
 			}
@@
-			Map<String, Object> unk = (Map<String, Object>)  walker.symbolTable.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
+			Map<String, Object> hold = (Map<String, Object>)  walker.symbolTable.get(queryName);
+			Map<String, Object> unk = (Map<String, Object>)  walker.symbolTable.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
@@
-				if (interfac != null) {
-					...
-				}
+				if (hold != null && interfac != null) {
+					for (String key : interfac.keySet()) {
+						Object unkItem = unk.remove(key);
+						if (unkItem != null) {
+							hold.put(key, unkItem);
+						}
+					}
+				}
```

## Verification Steps Run
1. Narrow test run for two methods in SqlEventWalkerSubqueriesAndClauseSemanticsTests:
   - unaliasedDerivedSimpleAllOuterClausesV1Test
   - unaliasedDerivedUnionAllOuterClausesV7Test

2. Result summary:
   - Diagnostics regression from first attempt was resolved.
   - Current state: V7 passes; V1 has a symbol snapshot delta only.

3. Current known delta (V1):
   - In def_query3 filters, expected table_ref=query0 but actual table_ref=null for col1 reference.

## Next Steps (post Phases 1-4)
1. Decide whether the V1 table_ref delta is intended canonical behavior.
2. If intended, update snapshot assertion in the V1 test.
3. If not intended, patch source-binding path that assigns filter table_ref for correlated outer ref in def_query3.
4. Re-run the same narrow pair, then expand to nearby subquery/set-op methods.

## Consolidation Status (As Of 2026-07-03)

## Phase 5 Direction Lock (2026-07-04)

- Definition payload lookup is now strict `def_*` only.
- No fallback path should read live keys (`queryN`/`unionN`/`intersectN`/`valuesN`) when resolving published scope payload maps.
- Alias/reference flow remains live-key based, but must translate to `def_*` before payload retrieval.
- Any drift from this change should be reviewed as intentional contract tightening first, then either accepted (golden update) or corrected in logic.

This section summarizes where we are after the def-key canonicalization follow-on work and what remains to complete the consolidation.

### Done Since Phases 1-4
- Canonical publish contract is active for query-like scopes:
	- payload-bearing symbol table entries are published under `def_*` keys,
	- live keys (`queryN`, `unionN`, `intersectN`, `valuesN`) are retained for reference wiring.
- Upstream candidate-source normalization is in place for unqualified resolution:
	- ambiguous-source collection dedupes canonical live keys,
	- `def_queryN` no longer appears as a duplicate candidate surface in diagnostics.
- Internal resolver read-paths now prefer definition-backed sources where needed:
	- helper methods normalize and resolve live vs definition keys consistently.
- Predicate dependent-query wiring was corrected to keep dependency references live (`queryN`) while preserving definition payloads.
- Public API surface for query dictionary output was aligned:
	- `getQueryColumnDictionaryMap()` exposes live keys only,
	- internal `def_*` mirror entries remain available for resolver internals.
- Top-level interface extraction now supports def-published top scopes.
- Regression checks run and passing in the unaliased-derived block:
	- `unaliasedDerivedSimpleAllOuterClausesV1Test`
	- `unaliasedDerivedFlattenInnerSelectAllOuterClausesV16Test`
	- `unaliasedDerivedSimpleFilteredInnerV2Test`

### Current Contract Snapshot (What Is Now True)
- `def_*` is the canonical storage key for published scope payloads.
- Live keys are canonical reference keys for:
	- table alias mappings,
	- dependent query references,
	- external/public dictionary and interface surfaces.
- Internal resolution may consult def-backed entries first, but outward-facing collections should not leak def-key duplicates.

### Remaining Work To Finish Consolidation

1. Consolidate query dictionary publication to avoid dual-write spread.
- Keep one explicit policy point for when `def_*` mirrors are written.
- Ensure all outward-facing accessors consistently filter to live keys.

2. Complete read-path unification on one canonical helper family.
- Replace remaining ad-hoc `queryN`/`def_queryN` checks with `normalize/toLive/toDefinition` helpers.
- Remove any residual string-prefix branching duplicated at call sites.

3. Finish source-reference collection simplification.
- Ensure all ambiguity and unqualified-source collectors take canonical live refs from the start.
- Confirm no late-stage normalization hacks remain in diagnostic emitters.

4. Close outstanding phase-5 verification breadth for unaliased-derived variants.
- Re-run the V1-V16 unaliased-derived family after any additional cleanup.
- Keep `query0=query0` self-alias presentation stable across the set.

5. Fold this phase checklist into the master consolidation roadmap.
- Treat this file as def-key completion notes and move ongoing multi-phase execution tracking to:
	- `parse/documents/symbol-table-resolution-consolidation-worklist.md`.

### Suggested Immediate Completion Slice
- ~~Perform a focused cleanup pass…~~ **Superseded** — Phase 5 closed Jul 2026 in master worklist (strict lookup audit + dead recursive lookup removal).

## Phase 5 Closeout (Jul 2026)

- **Strict payload lookup** enforced in `getQueryDefinitionSymbol` — `def_*` via **`resolveDefinitionSymbolInScopeChain`** (definition scope chain resolution); no recursive nested-map fallback.
- **Renamed (Jul 2026):** `findInCurrentOrAncestorSymbolTables` → private `resolveDefinitionSymbolInScopeChain`; egress bundle migration → Phase **15.6**.
- **Dead code removed:** `findInCurrentOrAncestorSymbolTablesRecursive`, `findInScopeTreeByKeyRecursive` (zero call sites).
- **Verification:** unaliased-derived V1–V16 + gate **195/195** + full suite **1209/1209**.
- **Query-dict dual-write consolidation** → Phase **19** (backfill repair closed in Step D; two-store model remains intentional).
