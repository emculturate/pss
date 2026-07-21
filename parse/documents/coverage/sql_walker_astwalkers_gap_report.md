# Coverage Gap Report: sql.walker and astwalkers

- Source data: parse/target/site/jacoco/jacoco.xml
- Detailed method gap CSV: parse/documents/coverage/sql_walker_astwalkers_method_gaps.csv
- **Hygiene (Jul 2026):** Removed rows for retired dead code — `rehomeUpdateUnqualifiedUnknownsToSingleFromTable`, `getSingleUpdateFromTableReference` (Step A, `36f7aa0`).

## astwalkers :: astwalkers/AbstractASTWalkerHelper
- Source: parse/src/main/java/astwalkers/AbstractASTWalkerHelper.java
- Method-level total lines: missed=41, covered=68
- Method-level total branches: missed=32, covered=34

| Method | Start line | Missed lines(counter) | Missed branches(counter) | Uncovered lines (by line nr) |
|---|---:|---:|---:|---|
| sameDiagnostic | 267 | 5 | 6 | 267-268, 273-274, 279-280, 282-283, 285-286 |
| showTrace | 322 | 4 | 6 | 322-329 |
| overrideDiagnosticCode | 148 | 6 | 6 | 148-149, 151-152, 154-155 |
| overrideDiagnosticMessage | 158 | 6 | 6 | 158-159, 161-162, 164-165 |
| addWalkerWarning | 251 | 3 | 0 | 251, 257, 264 |
| registerDiagnostic | 140 | 1 | 3 | 140-141 |
| addWalkerDiagnostic | 184 | 1 | 1 | 184-185 |
| handleOneChild | 535 | 1 | 1 | 541, 546 |
| handleListItem | 576 | 1 | 1 | 582, 594 |
| handleOperandList | 606 | 1 | 1 | 615, 622 |
| clearWalkerDiagnostics | 180 | 2 | 0 | 180-181 |
| addWalkerFatal | 222 | 2 | 0 | 222-223 |
| addWalkerFatal | 226 | 2 | 0 | 226-227 |
| addWalkerWarning | 247 | 2 | 0 | 247-248 |
| safeEquals | 292 | 0 | 1 | 293 |
| getShowparse | 299 | 1 | 0 | 299 |
| getShowsymbols | 303 | 1 | 0 | 303 |
| getShowother | 307 | 1 | 0 | 307 |
| getShowresults | 311 | 1 | 0 | 311 |

## astwalkers :: astwalkers/SqlASTWalkerHelper
- Source: parse/src/main/java/astwalkers/SqlASTWalkerHelper.java
- Method-level total lines: missed=717, covered=1031
- Method-level total branches: missed=869, covered=759

| Method | Start line | Missed lines(counter) | Missed branches(counter) | Uncovered lines (by line nr) |
|---|---:|---:|---:|---|
| reconcileExplicitAliasReferencesAgainstNonTableSources | 2955 | 78 | 92 | 2955-2956, 2958-2959, 2962-2965, 2968-2972, 2978-2979, 2984-2985, 2987-2989, 2993-2996, 3000-3004, 3006-3007, 3010-3016, 3021-3026, 3032-3036, 3041-3042, 3047-3050, 3053-3054, 3058-3062, 3066-3068, 3071-3077, 3080, 3084-3085, 3089-3090, 3093 |
| lambda$1 | 1174 | 6 | 2 | 1174-1178, 1180, 1183-1185, 1188-1191, 1193-1195, 1198-1202, 1204-1205, 1207-1210, 1213-1214, 1218-1220, 1223-1226, 1228-1235, 1237-1258, 1260-1265, 1268-1269 |
| validateTopLevelSetOperationSiblings | 1112 | 103 | 64 | 1112, 1116-1119, 1122-1125, 1127, 1131-1137, 1139, 1143-1144, 1148-1152, 1154-1157, 1162-1165, 1169-1170, 1173 |
| validateTopLevelVsNestedSetOperations | 1010 | 13 | 15 | 1010-1011, 1016-1017, 1032, 1035, 1042-1043, 1047, 1053, 1059-1060, 1071-1074, 1076-1086 |
| processWildcardUnknownEntries | 2704 | 18 | 19 | 2705, 2710-2711, 2715, 2717, 2726-2727, 2734-2740, 2743-2745, 2748-2750, 2753-2754, 2757, 2763-2764 |
| formatInterfaceColumnReferences | 3478 | 25 | 18 | 3478-3479, 3482-3495, 3497-3502, 3504-3505, 3509 |
| lambda$2 | 1312 | 1 | 1 | 1315, 1318, 1323-1324, 1340-1341, 1360-1361, 1369-1372, 1374-1385 |
| containsColumnName | 3410 | 22 | 24 | 3410-3411, 3414-3415, 3418-3419, 3421-3427, 3432-3434, 3437, 3440-3443, 3448 |
| addColumnTokenToColumnDict | 790 | 22 | 10 | 790-791, 793-802, 804, 806-807, 809-810, 812-813, 817-818, 821 |
| resolveAliasToNonTableSourceQueryKey | 2773 | 11 | 18 | 2773-2774, 2777, 2787-2791, 2801-2802, 2805, 2810, 2812, 2814-2817, 2822-2825 |
| splitExplicitlyQualifiedUnknownEntriesFromUnqualified | 2558 | 4 | 18 | 2558, 2561, 2565-2566, 2570, 2572, 2576-2577, 2581-2582, 2599, 2601, 2612, 2616, 2627-2628, 2632, 2649, 2651, 2661, 2665 |
| reconcileAliasBackedTableReferences | 2884 | 17 | 25 | 2884, 2892, 2897, 2901-2902, 2905-2911, 2914-2917, 2920-2921, 2924, 2927 |
| formatAllLocationsForEntry | 2156 | 14 | 23 | 2158, 2160, 2162-2163, 2166-2167, 2172-2175, 2177-2179, 2181, 2183-2186, 2190-2191 |
| extractUnresolvedColumnMetadata | 596 | 15 | 9 | 596-597, 600-605, 608-609, 612-618, 620, 637-638 |
| collectUnknownEntryLocations | 2514 | 18 | 26 | 2514-2515, 2518-2523, 2527, 2530-2533, 2536, 2539-2541, 2544 |
| mergeColumnReferenceIntoQueryDictionary | 3100 | 18 | 16 | 3100-3101, 3104, 3106-3110, 3113-3121, 3123 |
| scopeTableCollectionToCurrentLevel | 1943 | 17 | 20 | 1943-1944, 1947-1950, 1954-1955, 1958-1963, 1965-1966, 1970 |
| normalizeUnresolvedColumnItem | 714 | 16 | 14 | 714-715, 718-719, 722-723, 726-727, 730-733, 735-737, 741 |
| mergeUnknownEntryValues | 2487 | 16 | 12 | 2487-2489, 2491-2492, 2495-2499, 2502-2505, 2508-2509 |
| collectSourceReferencesForColumn | 3516 | 4 | 14 | 3517-3518, 3522, 3526, 3529, 3536, 3544-3546, 3548, 3552, 3554, 3561, 3563, 3567 |

## sql/walker :: sql/walker/SqlParseEventWalker
- Source: parse/src/main/java/sql/walker/SqlParseEventWalker.java
- Method-level total lines: missed=649, covered=3956
- Method-level total branches: missed=989, covered=1695

| Method | Start line | Missed lines(counter) | Missed branches(counter) | Uncovered lines (by line nr) |
|---|---:|---:|---:|---|
| emitQualifiedQueryAliasUnresolvedColumnsFatalAndPrune | 3392 | 30 | 25 | 3392-3393, 3400-3401, 3405-3406, 3417-3420, 3426-3429, 3431-3432, 3434, 3438-3441, 3444-3457, 3459-3464, 3466, 3469-3470 |
| emitExplicitQualifiedUnknownDiagnostics | 5338 | 14 | 30 | 5338, 5345, 5351, 5360, 5367, 5377, 5381-5382, 5384-5385, 5394-5395, 5403-5405, 5413-5414, 5419-5426, 5432-5433, 5446-5448, 5460, 5466-5467, 5470-5471, 5473 |
| convertSymbolTableToTableDictionary | 4002 | 15 | 29 | 4036, 4040, 4056, 4127, 4151, 4160-4161, 4181, 4190, 4194, 4210-4212, 4214-4215, 4229, 4232, 4234-4235, 4237-4238, 4243-4244, 4284-4290, 4351, 4354-4355, 4357, 4365-4366 |
| getInterface | 369 | 12 | 17 | 370, 381-382, 388-389, 391, 403-404, 410-411, 413, 422, 426-427, 433-434, 436, 445, 449-450, 456-457, 459, 468-469, 475-476, 484-485, 492-493, 496, 512, 524 |
| exitTable_primary | 5502 | 19 | 31 | 5511-5512, 5529-5530, 5542, 5554, 5556-5564, 5566, 5572, 5579-5586, 5588, 5591, 5596, 5605, 5646 |
| resolveRemainingQualifiedUnresolvedColumnsToTargetTable | 4539 | 15 | 23 | 4539-4541, 4546-4547, 4549-4550, 4552-4553, 4558, 4561-4562, 4568-4569, 4573-4574, 4579-4580, 4583-4584, 4587-4588, 4590-4591, 4595-4598, 4602-4603 |
| getTableColumnDictionaryMap | 263 | 25 | 27 | 269, 273-276, 278-281, 284-286, 289-290, 292-296, 299-304, 311 |
| resolveExplicitTableRefForUnknownEntry | 4801 | 22 | 35 | 4801, 4803, 4808-4810, 4813-4815, 4818-4822, 4828-4830, 4833-4839, 4844 |
| emitInvalidVariableDiagnosticAndSynthesizeIfNeeded | 156 | 13 | 17 | 156-157, 161-162, 166, 170-173, 175, 179-182, 185-187, 189-191, 194-195 |
| exitQuery_specification | 3091 | 8 | 17 | 3106, 3137, 3140, 3147, 3153-3154, 3167-3168, 3179, 3192-3193, 3198, 3207, 3209-3211, 3215, 3217-3219, 3222-3223 |
| resolveUpdateLhsColumnsToTargetTable | 4414 | 8 | 14 | 4414-4415, 4417-4418, 4422-4423, 4425-4426, 4429-4430, 4435, 4438-4439, 4446-4447, 4451, 4455-4456 |
| mergeUpdateTargetAndLhsIntoTableDictionary | 4622 | 8 | 15 | 4622-4623, 4627-4628, 4630-4631, 4634-4635, 4640-4642, 4648, 4652-4653, 4658-4659, 4663 |
| resolveUpdateQualifiedUnresolvedColumnsToInputTables | 4469 | 8 | 13 | 4469-4471, 4477-4478, 4482-4483, 4488-4489, 4493-4494, 4496-4497, 4500-4501, 4510-4511 |
| mergeInsertScopeTableDictionaryIntoGlobal | 1573 | 6 | 11 | 1573-1574, 1578-1579, 1584-1585, 1596-1599, 1601, 1605, 1610-1611, 1614, 1617 |
| emitQualifiedSourceNotFoundFatals | 3261 | 5 | 17 | 3261-3262, 3266-3267, 3269, 3273, 3282-3283, 3287-3288, 3303, 3311, 3314-3315, 3319 |
| propagateUnqualifiedSelectStarToScopeTables | 4704 | 3 | 14 | 4704-4706, 4716-4717, 4723-4724, 4728, 4740-4745, 4748 |
| moveAssignmentLhsToLhsUnresolvedColumns | 2297 | 6 | 13 | 2298-2299, 2303-2304, 2309, 2312-2313, 2317, 2320-2321, 2323-2324, 2336-2337 |
| sanitizeQueryDictionaryForGlobalExport | 5733 | 5 | 9 | 5733, 5741, 5745-5749, 5753-5755, 5760-5762, 5766 |

## Branch Gaps Mapped To Scenario Categories

### sql.walker (SqlParseEventWalker)

- Query alias unresolved diagnostics and pruning:
	- Methods: emitQualifiedQueryAliasUnresolvedColumnsFatalAndPrune, emitExplicitQualifiedUnknownDiagnostics, emitQualifiedSourceNotFoundFatals
	- Gap signal: high branch-missed counts around alias-target query/value source checks and wildcard fallbacks
	- Missing scenarios: alias points to query/union/values with missing column vs wildcard column vs present column; mixed-case key lookups

- UPDATE unresolved-column resolution pipeline:
	- Methods: resolveUpdateLhsColumnsToTargetTable, resolveUpdateQualifiedUnresolvedColumnsToInputTables, resolveRemainingQualifiedUnresolvedColumnsToTargetTable, mergeUpdateTargetAndLhsIntoTableDictionary
	- Retired (not in gap inventory): ~~rehomeUpdateUnqualifiedUnknownsToSingleFromTable~~, ~~getSingleUpdateFromTableReference~~ — deleted Step A Jul 2026
	- Gap signal: substantial branch misses across UPDATE-specific table dictionary logic
	- Missing scenarios: UPDATE with target-only qualification, UPDATE with conflicting target/input column names, UPDATE with nested source aliases

- Table-primary and dictionary merge behavior:
	- Methods: exitTable_primary, convertSymbolTableToTableDictionary, getTableColumnDictionaryMap
	- Gap signal: uncovered branches where source classification differs (table vs query alias vs substitution tuple vs values)
	- Missing scenarios: FROM source as values alias, tuple substitution as table source, chained alias projection into top-level table dictionary

- INSERT source and mapping edge behavior:
	- Methods: mergeInsertScopeTableDictionaryIntoGlobal, buildInsertScopeQueryDictionaryFromTableDictionary, findLatestInsertSourceScopeKey
	- Gap signal: mixed branch misses in insert-source normalization and implicit target-column assignment
	- Missing scenarios: INSERT INTO t(c,d) SELECT with derived source aliases; INSERT INTO t SELECT from UNION/INTERSECT with reordered columns

### astwalkers (SqlASTWalkerHelper and AbstractASTWalkerHelper)

- Top-level vs nested SET operation validation:
	- Methods: validateTopLevelSetOperationSiblings, validateTopLevelVsNestedSetOperations, validateSingleSetOperationInterface
	- Gap signal: very high uncovered lines/branches in sibling and nested mismatch detection
	- Missing scenarios: multiple top-level unions with mismatched column counts; nested union inside query with differing interface size

- Alias-to-non-table-source reconciliation:
	- Methods: reconcileExplicitAliasReferencesAgainstNonTableSources, splitExplicitlyQualifiedUnknownEntriesFromUnqualified, resolveAliasToNonTableSourceQueryKey, reconcileAliasBackedTableReferences
	- Gap signal: highest branch misses in non-table alias routing and explicit-ref demotion
	- Missing scenarios: alias references backed by values/query definitions where some explicit qualified refs are valid and others should become unknowns

- Diagnostic catalog and dedup helper behavior:
	- Methods: overrideDiagnosticCode, overrideDiagnosticMessage, sameDiagnostic, showTrace
	- Gap signal: mostly untested guard/exception branches
	- Missing scenarios: null-key override rejection, unknown-key override rejection, duplicate diagnostic suppression

## Concrete New Test Proposals (Existing Style)

The existing style in parse/src/test/java/sql/walker/SqlParseEventWalkerWithAccessObjectTest.java is:
- runSuccessfulSQLParserTest / runFailedSyntaxSQLParserTest
- Assert on AST, symbol tree, dictionaries, substitutions, and merged diagnostics
- Helper assertions for specific diagnostic code/severity/count

Recommended additions:

1. testName: qualifiedAliasMissingColumnEmitsQueryAliasFatalAndPrunes
- Intent: cover emitQualifiedQueryAliasUnresolvedColumnsFatalAndPrune miss path with alias backed by query.
- Query shape: SELECT q.missing FROM (SELECT a FROM tab1) q
- Assertions: QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS fatal emitted once, unresolved key removed from qualified map.

2. testName: qualifiedAliasWildcardOutputDoesNotEmitQueryAliasFatal
- Intent: branch where wildcard in query output suppresses alias fatal.
- Query shape: SELECT q.anycol FROM (SELECT * FROM tab1) q
- Assertions: no QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS fatal for q.anycol.

3. testName: explicitQualifiedUnknownAgainstValuesAliasBecomesForcedUnknown
- Intent: exercise non-table alias reconciliation through values-backed alias.
- Query shape: SELECT v.missing FROM (VALUES (1)) v(a)
- Assertions: explicit ref tracked as forced unknown; unresolved diagnostic produced with expected token.

4. testName: topLevelSiblingSetOperationColumnCountMismatchFatal
- Intent: drive validateTopLevelSetOperationSiblings mismatch branch.
- Query shape: (SELECT a FROM t1 UNION SELECT b FROM t2) INTERSECT (SELECT a,b FROM t3)
- Assertions: SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH fatal with expected/actual counts in message.

5. testName: nestedSetOperationColumnCountMismatchAgainstTopLevelFatal
- Intent: drive validateTopLevelVsNestedSetOperations branch.
- Query shape: SELECT * FROM ((SELECT a FROM t1 UNION SELECT b FROM t2) u JOIN (SELECT a,b FROM t3) x ON ...)
- Assertions: mismatch fatal includes both nested and top-level location anchors.

6. testName: insertDerivedSourceColumnSequenceMismatchFallback
- Intent: cover insert source sequence inference and fallback logic.
- Query shape: INSERT INTO tab1(c,d) SELECT x,y FROM (SELECT a AS x, b AS y FROM t2 UNION SELECT c AS x, d AS y FROM t3) s
- Assertions: insert interface maps c->x and d->y consistently, no spurious unresolved errors.

7. testName: abstractWalkerOverrideDiagnosticKeyValidation
- Intent: cover AbstractASTWalkerHelper override rejection branches.
- Approach: new focused unit test class under parse/src/test/java/astwalkers for helper-only behavior.
- Assertions: IllegalArgumentException for null key/code/message and unknown key override; duplicate diagnostics deduped by sameDiagnostic.

## Prioritized Next Coverage Sprint

1. Start with proposals 1, 2, 4, 5 (highest missed-branch hotspots).
2. Add proposal 6 to improve INSERT transformation paths.
3. Add proposal 7 to close low-level helper guard branches in astwalkers.
