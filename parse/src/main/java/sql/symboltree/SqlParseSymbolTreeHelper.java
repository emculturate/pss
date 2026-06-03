package sql.symboltree;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import static mumble.MumbleConstants.*;
import static mumble.ASTWalkerHelperConstants.*;
import static mumble.SQLParserEndPoints.*;

import astwalkers.SqlASTWalkerHelper;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

@SuppressWarnings("Convert2Diamond")
public class SqlParseSymbolTreeHelper {

	private final SqlASTWalkerHelper walker;

	// Fields moved from SqlParseEventWalker
	private int tableFunctionSourceCount = 0;
	private final Set<String> suppressedAmbiguousUnqualifiedKeys = new HashSet<String>();
	private final Set<String> tableFunctionSourceRefs = new HashSet<String>();

	public SqlParseSymbolTreeHelper(SqlASTWalkerHelper walkerHelper) {
		this.walker = walkerHelper;
	}

	// --- Constants shared with SqlParseEventWalker ---
	public static final String TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY = "_tmp_insert_source_select_sequence";
	public static final String INSERT_SOURCE_REF_KEY = "insert_source_ref";
	public static final String TEMP_INSERT_TARGET_COLUMN_LIST_LOCATION_KEY = "_tmp_insert_target_column_list_location";
	public static final String TEMP_DELETE_TARGET_TABLE_REF_KEY = "_tmp_delete_target_table_ref";

	// --- Getters/setters for moved fields ---

	public int getTableFunctionSourceCount() { return tableFunctionSourceCount; }
	public void setTableFunctionSourceCount(int count) { this.tableFunctionSourceCount = count; }
	public Set<String> getSuppressedAmbiguousUnqualifiedKeys() { return suppressedAmbiguousUnqualifiedKeys; }
	public Set<String> getTableFunctionSourceRefs() { return tableFunctionSourceRefs; }

	// --- normalizeTableRef delegate (mirrors event-walker static helper) ---

	public static String normalizeTableRef(String tableRef) {
		return SqlASTWalkerHelper.normalizeTableReference(tableRef);
	}

	// =========================================================================
	// Methods moved from SqlParseEventWalker
	// =========================================================================

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getAncestorSymbolTables() {
		ArrayList<Map<String, Object>> ancestors = new ArrayList<Map<String, Object>>();
		Integer parentLevel = walker.currentStackLevel("symbolTable");
		if (parentLevel == null || walker.asTree == null) {
			return ancestors;
		}

		for (int level = parentLevel; level >= 1; level--) {
			Object ancestorObj = walker.asTree.get("symbolTable_" + level);
			if (ancestorObj instanceof Map<?, ?> ancestorMapObj) {
				ancestors.add((Map<String, Object>) ancestorMapObj);
			}
		}

		return ancestors;
	}

	public String getQualifiedTableReference(Map<String, Object> tableNode) {
		if (tableNode == null) {
			return null;
		}

		Object tableObj = tableNode.get(MUMBLE_TABLE_KEY);
		if (!(tableObj instanceof String tableName) || tableName.isBlank()) {
			return null;
		}

		// Keep tuple substitutions and already-qualified names unchanged.
		if (tableName.startsWith("<") || tableName.contains(".")) {
			return tableName;
		}

		Object dbNameObj = tableNode.get(MUMBLE_DATABASE_NAME_KEY);
		String dbName = (dbNameObj instanceof String db && !db.isBlank()) ? db : null;

		Object schemaObj = tableNode.get(MUMBLE_SCHEMA_KEY);
		String schemaName = (schemaObj instanceof String schema && !schema.isBlank()) ? schema : null;

		if (dbName != null && schemaName != null) {
			return dbName + "." + schemaName + "." + tableName;
		}
		if (schemaName != null) {
			return schemaName + "." + tableName;
		}
		if (dbName != null) {
			return dbName + "." + tableName;
		}

		return tableName;
	}

	/**
	 * Drains {@code unresolved_column} from the current symbol scope and emits statement-level
	 * diagnostics for anything still unresolved.
	 * <p>
	 * INSERT statements only drain the bucket: target-column tokens are not UPDATE-rehomed and are
	 * not validated like SELECT output. Call {@link #finalizeTopLevelUnresolvedColumnsAtInsertBoundary()}
	 * from {@code exitInsert_expression} (before the SQL tree is attached) or rely on
	 * {@link #isInsertStatementSqlTree()} at {@code exitSql}.
	 */
	public void finalizeTopLevelUnresolvedColumns() {
		finalizeTopLevelUnresolvedColumns(false);
	}

	/** INSERT boundary finalize: drain only, no UPDATE rehome or statement-level unresolved diagnostics. */
	public void finalizeTopLevelUnresolvedColumnsAtInsertBoundary() {
		finalizeTopLevelUnresolvedColumns(true);
	}

	@SuppressWarnings("unchecked")
	public void finalizeTopLevelUnresolvedColumns(boolean insertStatementBoundary) {
		Object unresolvedObject = walker.symbolTable.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
		if (!(unresolvedObject instanceof HashMap<?, ?> unresolvedMapObject)) {
			return;
		}

		HashMap<String, Object> unresolvedMap = unresolvedObject instanceof HashMap<?, ?> 
											  ? (HashMap<String, Object>) unresolvedMapObject : null;
		if (unresolvedMap == null || unresolvedMap.isEmpty()) {
			return;
		}

		boolean insertStatement = insertStatementBoundary || isInsertStatementSqlTree();
		if (!insertStatement) {
			rehomeUpdateUnqualifiedUnknownsToSingleFromTable(unresolvedMap);
		}
		if (insertStatement) {
			return;
		}

		HashMap<String, Object> qualifiedUnresolved = new HashMap<String, Object>();
		HashMap<String, Object> unqualifiedUnresolved = new HashMap<String, Object>();
		splitUnresolvedEntriesByQualification(unresolvedMap, qualifiedUnresolved, unqualifiedUnresolved);

		emitUnqualifiedUnresolvedColumnsError(unqualifiedUnresolved);
		emitQualifiedSourceNotFoundFatals(qualifiedUnresolved);
	}

	@SuppressWarnings("unchecked")
	public boolean isInsertStatementSqlTree() {
		Object sqlTreeObj = walker.asTree.get(SQLPARSER_SQL_TREE_KEY);
		if (!(sqlTreeObj instanceof Map<?, ?> sqlTree)) {
			return false;
		}

		for (Object keyObj : sqlTree.keySet()) {
			if (keyObj instanceof String key && key.startsWith(MUMBLE_INSERT_KEY)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public void rehomeUpdateUnqualifiedUnknownsToSingleFromTable(HashMap<String, Object> unresolvedMap) {
		if (unresolvedMap == null || unresolvedMap.isEmpty()) {
			return;
		}

		Object sqlTreeObj = walker.asTree.get(SQLPARSER_SQL_TREE_KEY);
		if (!(sqlTreeObj instanceof Map<?, ?> sqlTree)) {
			return;
		}

		Map<String, Object> updateAst = (Map<String, Object>) sqlTree;
		boolean isUpdateAst = false;
		for (Map.Entry<?, ?> entry : sqlTree.entrySet()) {
			if (entry.getKey() instanceof String key
					&& key.startsWith(MUMBLE_UPDATE_KEY)) {
				isUpdateAst = true;
				break;
			}
		}
		if (!isUpdateAst) {
			return;
		}

		String fromTableRef = getSingleUpdateFromTableReference(updateAst);
		if (fromTableRef == null || fromTableRef.isBlank()) {
			Map<String, Object> updateNode = getUpdateNode(updateAst);
			fromTableRef = getUpdateTargetTableReference(updateNode);
		}
		if (fromTableRef == null || fromTableRef.isBlank()) {
			return;
		}
		String normalizedFromTableRef = normalizeTableRef(fromTableRef);

		HashMap<String, Object> currentTableDictionary = walker.getCurrentTableDictionary();
		HashMap<String, Object> fromTableDictionary = ensureTableDictionaryEntry(currentTableDictionary, normalizedFromTableRef);

		HashMap<String, Object> nestedFromTableDictionary = null;
		Object nestedTableDictionaryObj = currentTableDictionary.get(MUMBLE_TABLE_DICTIONARY_KEY);
		if (nestedTableDictionaryObj instanceof HashMap<?, ?> nestedTableDictionaryMapObj) {
			nestedFromTableDictionary = ensureTableDictionaryEntry((Map<String, Object>) nestedTableDictionaryMapObj, normalizedFromTableRef);
		}

		HashMap<String, Object> walkerTableDictionary = walker.getWalkerTableDictionary();
		HashMap<String, Object> globalFromTableDictionary = ensureTableDictionaryEntry(walkerTableDictionary, normalizedFromTableRef);

		ArrayList<String> resolvedKeys = new ArrayList<String>();
		for (Map.Entry<String, Object> unresolvedEntry : unresolvedMap.entrySet()) {
			String unresolvedKey = unresolvedEntry.getKey();
			if (unresolvedKey == null || unresolvedKey.contains(".")) {
				continue;
			}
			Object unresolvedValue = normalizeUpdateColumnRefs(unresolvedEntry.getValue());
			if (unresolvedValue == null) {
				continue;
			}
			fromTableDictionary.put(unresolvedKey, unresolvedValue);
			globalFromTableDictionary.put(unresolvedKey, unresolvedValue);
			if (nestedFromTableDictionary != null) {
				nestedFromTableDictionary.put(unresolvedKey, unresolvedValue);
			}
			resolvedKeys.add(unresolvedKey);
		}

		for (String resolvedKey : resolvedKeys) {
			unresolvedMap.remove(resolvedKey);
		}
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> ensureTableDictionaryEntry(Map<String, Object> dictionary, String tableRef) {
		if (dictionary == null || tableRef == null) {
			return new HashMap<String, Object>();
		}

		Object tableEntryObj = dictionary.get(tableRef);
		if (tableEntryObj instanceof HashMap<?, ?> tableEntryMapObj) {
			return (HashMap<String, Object>) tableEntryMapObj;
		}

		HashMap<String, Object> tableEntry = new HashMap<String, Object>();
		dictionary.put(tableRef, tableEntry);
		return tableEntry;
	}

	@SuppressWarnings("unchecked")
	public Object normalizeUpdateColumnRefs(Object value) {
		if (value instanceof Map<?, ?> valueMapObj) {
			Object locations = ((Map<String, Object>) valueMapObj).get("locations");
			return locations;
		}
		return value;
	}

	public void splitUnresolvedEntriesByQualification(
			HashMap<String, Object> unresolvedMap,
			HashMap<String, Object> qualified,
			HashMap<String, Object> unqualified) {
		if (qualified != null) {
			qualified.clear();
		}
		if (unqualified != null) {
			unqualified.clear();
		}

		if (unresolvedMap == null || unresolvedMap.isEmpty()) {
			return;
		}

		for (Map.Entry<String, Object> unresolvedEntry : unresolvedMap.entrySet()) {
			String key = unresolvedEntry.getKey();
			Object value = unresolvedEntry.getValue();
			if (key == null || key.isBlank()) {
				continue;
			}
			boolean isQualified = key != null && key.contains(".");
			if (isQualified) {
				if (qualified != null) {
					qualified.put(key, value);
				}
			} else if (unqualified != null) {
				unqualified.put(key, value);
			}
		}
	}

	public void emitUnqualifiedUnresolvedColumnsError(HashMap<String, Object> unqualifiedUnresolvedMap) {
		if (unqualifiedUnresolvedMap == null || unqualifiedUnresolvedMap.isEmpty()) {
			return;
		}

		if (unqualifiedUnresolvedMap.size() == 1 && unqualifiedUnresolvedMap.containsKey("*")) {
			return;
		}

		Integer[] firstTokenLocation = walker.getFirstEntryLineAndCharacter(unqualifiedUnresolvedMap);
		String unknownColumnsWithLocations = walker.formatColumnEntriesWithLocations(unqualifiedUnresolvedMap);
		String unknownColumnsCsv = walker.formatEntryKeysAsCsv(unqualifiedUnresolvedMap);
		String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_UNRESOLVED_UNQUALIFIED_COLUMNS);
		String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_UNRESOLVED_UNQUALIFIED_COLUMNS);
		String diagMessage = (diagTemplate == null)
				? "Unresolved unqualified column reference(s): " + unknownColumnsWithLocations
				: String.format(diagTemplate, unknownColumnsWithLocations);

		walker.addWalkerDiagnostic(
				ParseDiagnostic.Severity.ERROR,
				diagCode,
				diagMessage,
				firstTokenLocation[0],
				firstTokenLocation[1],
				walker.getClass().getSimpleName(),
				null,
				unknownColumnsCsv,
				false,
				"ast-walk",
				null,
				null);
	}

	public void emitQualifiedUnresolvedColumnsFatal(HashMap<String, Object> qualifiedUnresolvedMap) {
		if (qualifiedUnresolvedMap == null || qualifiedUnresolvedMap.isEmpty()) {
			return;
		}

		Integer[] firstTokenLocation = walker.getFirstEntryLineAndCharacter(qualifiedUnresolvedMap);
		String unknownColumnsWithLocations = walker.formatColumnEntriesWithLocations(qualifiedUnresolvedMap);
		String unknownColumnsCsv = walker.formatEntryKeysAsCsv(qualifiedUnresolvedMap);
		String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_UNRESOLVED_QUALIFIED_COLUMNS);
		String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_UNRESOLVED_QUALIFIED_COLUMNS);
		String diagMessage = (diagTemplate == null)
				? "Unresolved qualified column reference(s): " + unknownColumnsWithLocations
				: String.format(diagTemplate, unknownColumnsWithLocations);

		walker.addWalkerFatal(
				diagCode,
				diagMessage,
				firstTokenLocation[0],
				firstTokenLocation[1],
				unknownColumnsCsv);
	}

	public void mergeCteListIntoQueryScope(Map<String, Object> querySymbols, Map<String, Object> withSymbols) {
		Map<String, Object> withCteList = getCteListSymbolMap(withSymbols);
		if (withCteList == null || withCteList.isEmpty()) {
			return;
		}

		Map<String, Object> queryCteList = getCteListSymbolMap(querySymbols);
		if (queryCteList == null) {
			querySymbols.put(MUMBLE_CTE_LIST_KEY, new LinkedHashMap<String, Object>(withCteList));
		} else {
			for (Map.Entry<String, Object> entry : withCteList.entrySet()) {
				queryCteList.putIfAbsent(entry.getKey(), entry.getValue());
			}
		}

		withSymbols.remove(MUMBLE_CTE_LIST_KEY);
	}

	@SuppressWarnings("unchecked")
	public boolean isTupleWithSubstitution(Map<String, Object> aliasMap) {
		if (aliasMap == null) {
			return false;
		}
		Object substitutionObject = aliasMap.get(MUMBLE_SUBSTITUTION_KEY);
		if (!(substitutionObject instanceof Map<?, ?> substitutionMap)) {
			return false;
		}
		Object typeObject = substitutionMap.get(MUMBLE_TYPE_KEY);
		return "tuple".equals(typeObject);
	}

	@SuppressWarnings("unchecked")
	public String resolveCurrentWithListItemScope(Map<String, Object> aliasMap) {
		int scopeIndex = walker.queryCount - 1;

		String[] orderedPrefixes = new String[] {
				MUMBLE_VALUES_KEY,
				MUMBLE_INTERSECT_KEY,
				MUMBLE_UNION_KEY,
				MUMBLE_INSERT_KEY,
				MUMBLE_UPDATE_KEY,
				MUMBLE_DELETE_KEY,
				MUMBLE_QUERY_KEY
		};

		for (String prefix : orderedPrefixes) {
			if (!containsKeyRecursive(aliasMap, prefix)) {
				continue;
			}
			String candidate = prefix + scopeIndex;
			if (walker.symbolTable.containsKey(candidate)) {
				return candidate;
			}
		}

		for (String prefix : orderedPrefixes) {
			String candidate = prefix + scopeIndex;
			if (walker.symbolTable.containsKey(candidate)) {
				return candidate;
			}
		}

		return MUMBLE_QUERY_KEY + nextSyntheticWithQueryAliasIndex();
	}

	@SuppressWarnings("unchecked")
	public int nextSyntheticWithQueryAliasIndex() {
		int maxIndex = -1;
		Object aliasObject = walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY);
		if (aliasObject instanceof Map<?, ?> aliasMap) {
			for (Object value : aliasMap.values()) {
				if (!(value instanceof String aliasTarget)) {
					continue;
				}
				if (!aliasTarget.startsWith(MUMBLE_QUERY_KEY)) {
					continue;
				}
				String suffix = aliasTarget.substring(MUMBLE_QUERY_KEY.length());
				int index;
				try {
					index = Integer.parseInt(suffix);
				} catch (NumberFormatException ex) {
					continue;
				}
				if (index > maxIndex) {
					maxIndex = index;
				}
			}
		}
		return maxIndex + 1;
	}

	@SuppressWarnings("unchecked")
	public boolean containsKeyRecursive(Map<String, Object> map, String expectedKey) {
		if (map == null) {
			return false;
		}
		if (map.containsKey(expectedKey)) {
			return true;
		}
		for (Object value : map.values()) {
			if (value instanceof Map<?, ?> nestedMap
					&& containsKeyRecursive((Map<String, Object>) nestedMap, expectedKey)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> ensureCteListSymbolMap() {
		Object cteListObject = walker.symbolTable.get(MUMBLE_CTE_LIST_KEY);
		if (cteListObject instanceof Map<?, ?> cteListMapObject) {
			return (Map<String, Object>) cteListMapObject;
		}

		Map<String, Object> cteListMap = new LinkedHashMap<String, Object>();
		walker.symbolTable.put(MUMBLE_CTE_LIST_KEY, cteListMap);
		return cteListMap;
	}

	@SuppressWarnings("unchecked")
	public boolean isColumnReferenceListNode(Map<String, Object> candidate) {
		if (candidate == null || candidate.isEmpty()) {
			return false;
		}

		for (Object value : candidate.values()) {
			if (value instanceof Map<?, ?> valueMapObj) {
				Map<String, Object> valueMap = (Map<String, Object>) valueMapObj;
				if (valueMap.containsKey(MUMBLE_COLUMN_KEY)) {
					return true;
				}
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public void resolveInsertUnqualifiedOrphanSourceColumnsToTargetTable(
			String insertTargetTableRef,
			Map<String, Object> insertSourceDefinition,
			Map<String, Object> insertInterface) {
		if (insertTargetTableRef == null || insertTargetTableRef.isBlank()) {
			return;
		}
		if (insertSourceDefinition == null || insertSourceDefinition.isEmpty()) {
			return;
		}
		if (insertInterface == null || insertInterface.isEmpty()) {
			return;
		}

		HashMap<String, Object> currentTableDictionary = walker.getCurrentTableDictionary();
		HashMap<String, Object> insertTargetDictionary = ensureTableDictionaryEntry(currentTableDictionary, insertTargetTableRef);

		for (Object interfaceEntryObj : insertInterface.values()) {
			if (!(interfaceEntryObj instanceof List<?> interfaceRefsObj)) {
				continue;
			}

			for (Object interfaceRefObj : interfaceRefsObj) {
				String sourceColumnName = walker.extractReferenceNameFromInterfaceEntry(interfaceRefObj);
				String sourceScopeRef = walker.extractReferenceTableRefFromInterfaceEntry(interfaceRefObj);

				if (sourceColumnName == null || sourceColumnName.isBlank() || "*".equals(sourceColumnName)) {
					continue;
				}
				if (sourceScopeRef == null || sourceScopeRef.isBlank()) {
					continue;
				}
				if (containsKeyIgnoreCase(insertTargetDictionary, sourceColumnName)) {
					continue;
				}

				Map<String, Object> sourceScopeDefinition = normalizeInsertSourceDefinition(sourceScopeRef);
				if ((sourceScopeDefinition == null || sourceScopeDefinition.isEmpty()) && sourceScopeRef != null) {
					Object queryDefObj = getQueryDefinitionSymbol(sourceScopeRef);
					if (queryDefObj instanceof Map<?, ?> queryDefMapObj) {
						sourceScopeDefinition = (Map<String, Object>) queryDefMapObj;
					}
				}
				if (sourceScopeDefinition == null || sourceScopeDefinition.isEmpty()) {
					continue;
				}

				Object sourceScopeQueryDictionaryObj = sourceScopeDefinition.get(MUMBLE_QUERY_DICTIONARY_KEY);
				if (!(sourceScopeQueryDictionaryObj instanceof Map<?, ?> sourceScopeQueryDictionaryMapObj)) {
					continue;
				}

				Map<String, Object> sourceScopeQueryDictionary = (Map<String, Object>) sourceScopeQueryDictionaryMapObj;
				Object sourceScopeInterfaceObj = sourceScopeDefinition.get(MUMBLE_INTERFACE_KEY);
				if (!(sourceScopeInterfaceObj instanceof Map<?, ?> sourceScopeInterfaceMapObj)) {
					continue;
				}
				Map<String, Object> sourceScopeInterface = (Map<String, Object>) sourceScopeInterfaceMapObj;
				Object sourceInterfaceRefsObj = sourceScopeInterface.get(sourceColumnName);
				if (!(sourceInterfaceRefsObj instanceof List<?> sourceInterfaceRefs)) {
					continue;
				}

				boolean hasNullTableRef = false;
				for (Object sourceInterfaceRefObj : sourceInterfaceRefs) {
					String sourceInterfaceTableRef = walker.extractReferenceTableRefFromInterfaceEntry(sourceInterfaceRefObj);
					if (sourceInterfaceTableRef == null || sourceInterfaceTableRef.isBlank()) {
						hasNullTableRef = true;
						break;
					}
				}
				if (!hasNullTableRef) {
					continue;
				}

				Object sourceRefsObj = sourceScopeQueryDictionary.get(sourceColumnName);
				if (!(sourceRefsObj instanceof ArrayList<?> sourceRefsListObj) || sourceRefsListObj.isEmpty()) {
					continue;
				}

				insertTargetDictionary.put(sourceColumnName, new ArrayList<Object>((ArrayList<Object>) sourceRefsListObj));
				removeUnresolvedColumnEntry(sourceColumnName);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void removeUnresolvedColumnEntry(String columnName) {
		if (columnName == null || columnName.isBlank()) {
			return;
		}

		Object unresolvedObject = walker.symbolTable.get(MUMBLE_UNRESOLVED_COLUMN_KEY);
		if (!(unresolvedObject instanceof HashMap<?, ?> unresolvedMapObj)) {
			return;
		}

		HashMap<String, Object> unresolvedMap = (HashMap<String, Object>) unresolvedMapObj;
		unresolvedMap.remove(columnName);
		if (unresolvedMap.isEmpty()) {
			walker.symbolTable.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
		}
	}

	@SuppressWarnings("unchecked")
	public void mergeInsertScopeTableDictionaryIntoGlobal(String insertScopeKey) {
		if (insertScopeKey == null || insertScopeKey.isBlank()) {
			return;
		}

		Object insertScopeObj = walker.symbolTable.get(insertScopeKey);
		if (!(insertScopeObj instanceof Map<?, ?> insertScopeMapObj)) {
			return;
		}

		Map<String, Object> insertScopeMap = (Map<String, Object>) insertScopeMapObj;
		Object localTableDictionaryObj = insertScopeMap.get(MUMBLE_TABLE_DICTIONARY_KEY);
		if (!(localTableDictionaryObj instanceof Map<?, ?> localTableDictionaryMapObj)) {
			return;
		}

		Map<String, Object> localTableDictionary = (Map<String, Object>) localTableDictionaryMapObj;
		if (!hasAnyColumnsInTableDictionary(localTableDictionary)) {
			return;
		}

		HashMap<String, Object> globalTableDictionary = walker.getWalkerTableDictionary();
		for (Map.Entry<String, Object> tableEntry : localTableDictionary.entrySet()) {
			String tableRef = tableEntry.getKey();
			if (tableRef == null
					|| MUMBLE_TABLE_ALIAS_KEY.equals(tableRef)
					|| MUMBLE_TABLE_DICTIONARY_KEY.equals(tableRef)) {
				continue;
			}
			if (!(tableEntry.getValue() instanceof Map<?, ?> sourceColumnsMapObj)) {
				continue;
			}

			String normalizedTableRef = normalizeTableRef(tableRef);
			HashMap<String, Object> targetColumns = ensureTableDictionaryEntry(globalTableDictionary, normalizedTableRef);
			Map<String, Object> sourceColumns = (Map<String, Object>) sourceColumnsMapObj;
			for (Map.Entry<String, Object> columnEntry : sourceColumns.entrySet()) {
				String columnName = columnEntry.getKey();
				if (columnName == null) {
					continue;
				}
				Object existingRefs = targetColumns.get(columnName);
				if (existingRefs == null) {
					targetColumns.put(columnName, columnEntry.getValue());
				} else {
					targetColumns.put(columnName, mergeReferenceCollections(existingRefs, columnEntry.getValue()));
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void publishInsertScopeQueryDictionary(String insertScopeKey) {
		if (insertScopeKey == null || insertScopeKey.isBlank()) {
			return;
		}

		Object insertScopeObj = walker.symbolTable.get(insertScopeKey);
		if (!(insertScopeObj instanceof Map<?, ?> insertScopeMapObj)) {
			return;
		}

		Map<String, Object> insertScopeMap = (Map<String, Object>) insertScopeMapObj;
		Object queryDictionaryObj = insertScopeMap.get(MUMBLE_QUERY_DICTIONARY_KEY);
		HashMap<String, Object> queryDictionary = (queryDictionaryObj instanceof Map<?, ?> queryDictionaryMapObj)
				? new HashMap<String, Object>((Map<String, Object>) queryDictionaryMapObj)
				: new HashMap<String, Object>();

		HashMap<String, Object> mappedQueryDictionary =
				buildInsertScopeQueryDictionaryFromMappedInterface(insertScopeMap);
		if (!mappedQueryDictionary.isEmpty()) {
			for (Map.Entry<String, Object> mappedEntry : mappedQueryDictionary.entrySet()) {
				String columnName = mappedEntry.getKey();
				if (columnName == null) {
					continue;
				}
				Object existingRefs = queryDictionary.get(columnName);
				if (existingRefs == null) {
					queryDictionary.put(columnName, mappedEntry.getValue());
				} else {
					queryDictionary.put(columnName, mergeReferenceCollections(existingRefs, mappedEntry.getValue()));
				}
			}
		}

		HashMap<String, Object> inferredQueryDictionary =
				buildInsertScopeQueryDictionaryFromTableDictionary(insertScopeMap);
		if (!inferredQueryDictionary.isEmpty()) {
			for (Map.Entry<String, Object> inferredEntry : inferredQueryDictionary.entrySet()) {
				String columnName = inferredEntry.getKey();
				if (columnName == null) {
					continue;
				}
				Object existingRefs = queryDictionary.get(columnName);
				if (existingRefs == null) {
					queryDictionary.put(columnName, inferredEntry.getValue());
				} else {
					queryDictionary.put(columnName, mergeReferenceCollections(existingRefs, inferredEntry.getValue()));
				}
			}
		}

		sanitizeQueryDictionaryForGlobalExport(queryDictionary);
		if (queryDictionary.isEmpty()) {
			return;
		}

		walker.queryColumnDictionaryMap.put(insertScopeKey, queryDictionary);
		insertScopeMap.put(MUMBLE_QUERY_DICTIONARY_KEY, queryDictionary);
	}

	@SuppressWarnings("unchecked")
	public void publishUpdateScopeQueryDictionary(String updateScopeKey) {
		if (updateScopeKey == null || updateScopeKey.isBlank()) {
			return;
		}
		Object updateScopeObj = walker.symbolTable.get(updateScopeKey);
		if (!(updateScopeObj instanceof Map<?, ?> updateScopeMapObj)) {
			return;
		}
		Map<String, Object> updateScopeMap = (Map<String, Object>) updateScopeMapObj;
		Object updateDictionaryObj = updateScopeMap.get(MUMBLE_UPDATE_DICTIONARY_KEY);
		if (!(updateDictionaryObj instanceof Map<?, ?> updateDictionaryMapObj)) {
			return;
		}
		HashMap<String, Object> queryDictionary = new HashMap<String, Object>((Map<String, Object>) updateDictionaryMapObj);
		sanitizeQueryDictionaryForGlobalExport(queryDictionary);
		if (queryDictionary.isEmpty()) {
			return;
		}
		walker.queryColumnDictionaryMap.put(updateScopeKey, queryDictionary);
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> buildInsertScopeQueryDictionaryFromMappedInterface(Map<String, Object> insertScopeMap) {
		HashMap<String, Object> queryDictionary = new HashMap<String, Object>();
		if (insertScopeMap == null || insertScopeMap.isEmpty()) {
			return queryDictionary;
		}

		Object insertInterfaceObj = insertScopeMap.get(MUMBLE_INTERFACE_KEY);
		if (!(insertInterfaceObj instanceof Map<?, ?> insertInterfaceMapObj)) {
			return queryDictionary;
		}

		Map<String, Object> insertInterface = (Map<String, Object>) insertInterfaceMapObj;
		for (Map.Entry<String, Object> interfaceEntry : insertInterface.entrySet()) {
			String targetColumnName = interfaceEntry.getKey();
			if (targetColumnName == null || targetColumnName.isBlank()) {
				continue;
			}
			if (!(interfaceEntry.getValue() instanceof List<?> interfaceRefsObj)) {
				continue;
			}

			ArrayList<Object> targetRefs = null;
			for (Object interfaceRefObj : interfaceRefsObj) {
				String sourceColumnName = walker.extractReferenceNameFromInterfaceEntry(interfaceRefObj);
				String sourceScopeRef = walker.extractReferenceTableRefFromInterfaceEntry(interfaceRefObj);
				if (sourceColumnName == null || sourceColumnName.isBlank() || "*".equals(sourceColumnName)) {
					continue;
				}
				if (sourceScopeRef == null || sourceScopeRef.isBlank()) {
					continue;
				}

				Map<String, Object> sourceScopeDefinition = normalizeInsertSourceDefinition(sourceScopeRef);
				if ((sourceScopeDefinition == null || sourceScopeDefinition.isEmpty()) && sourceScopeRef != null) {
					Object queryDefObj = getQueryDefinitionSymbol(sourceScopeRef);
					if (queryDefObj instanceof Map<?, ?> queryDefMapObj) {
						sourceScopeDefinition = (Map<String, Object>) queryDefMapObj;
					}
				}
				if (sourceScopeDefinition == null || sourceScopeDefinition.isEmpty()) {
					continue;
				}

				Object sourceRefsObj = null;
				Object sourceScopeQueryDictionaryObj = sourceScopeDefinition.get(MUMBLE_QUERY_DICTIONARY_KEY);
				if (sourceScopeQueryDictionaryObj instanceof Map<?, ?> sourceScopeQueryDictionaryMapObj) {
					Map<String, Object> sourceScopeQueryDictionary =
							(Map<String, Object>) sourceScopeQueryDictionaryMapObj;
					sourceRefsObj = sourceScopeQueryDictionary.get(sourceColumnName);
				}
				if (sourceRefsObj == null) {
					sourceRefsObj = resolveInsertSourceColumnFromScopeDefinition(
							sourceScopeDefinition,
							sourceColumnName);
				}
				if (!(sourceRefsObj instanceof ArrayList<?> sourceRefsListObj) || sourceRefsListObj.isEmpty()) {
					continue;
				}

				ArrayList<Object> copiedRefs = new ArrayList<Object>((ArrayList<Object>) sourceRefsListObj);
				if (targetRefs == null) {
					targetRefs = copiedRefs;
				} else {
					targetRefs = (ArrayList<Object>) mergeReferenceCollections(targetRefs, copiedRefs);
				}
			}

			if (targetRefs != null && !targetRefs.isEmpty()) {
				queryDictionary.put(targetColumnName, targetRefs);
			}
		}

		return queryDictionary;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> buildInsertScopeQueryDictionaryFromTableDictionary(Map<String, Object> insertScopeMap) {
		HashMap<String, Object> inferred = new HashMap<String, Object>();
		if (insertScopeMap == null || insertScopeMap.isEmpty()) {
			return inferred;
		}

		HashSet<String> allowedInsertOutputColumns = new HashSet<String>();
		Object insertInterfaceObj = insertScopeMap.get(MUMBLE_INTERFACE_KEY);
		if (insertInterfaceObj instanceof Map<?, ?> insertInterfaceMapObj) {
			Map<String, Object> insertInterfaceMap = (Map<String, Object>) insertInterfaceMapObj;
			allowedInsertOutputColumns.addAll(insertInterfaceMap.keySet());
		}

		Object localTableDictionaryObj = insertScopeMap.get(MUMBLE_TABLE_DICTIONARY_KEY);
		if (!(localTableDictionaryObj instanceof Map<?, ?> localTableDictionaryMapObj)) {
			return inferred;
		}

		Map<String, Object> localTableDictionary = (Map<String, Object>) localTableDictionaryMapObj;
		Map<String, Object> targetColumns = null;
		for (Map.Entry<String, Object> tableEntry : localTableDictionary.entrySet()) {
			String tableRef = tableEntry.getKey();
			if (tableRef == null
					|| MUMBLE_TABLE_ALIAS_KEY.equals(tableRef)
					|| MUMBLE_TABLE_DICTIONARY_KEY.equals(tableRef)) {
				continue;
			}
			if (!(tableEntry.getValue() instanceof Map<?, ?> columnMapObj)) {
				continue;
			}

			Map<String, Object> candidateColumns = (Map<String, Object>) columnMapObj;
			if (!candidateColumns.isEmpty()) {
				targetColumns = candidateColumns;
				break;
			}
		}

		if (targetColumns == null || targetColumns.isEmpty()) {
			return inferred;
		}

		for (Map.Entry<String, Object> columnEntry : targetColumns.entrySet()) {
			String columnName = columnEntry.getKey();
			if (columnName == null) {
				continue;
			}
			if (!allowedInsertOutputColumns.isEmpty()
					&& !containsIgnoreCase(allowedInsertOutputColumns, columnName)) {
				continue;
			}
			Object refsObj = columnEntry.getValue();
			if (refsObj instanceof ArrayList<?> refsListObj) {
				inferred.put(columnName, new ArrayList<Object>((ArrayList<Object>) refsListObj));
			}
		}

		return inferred;
	}

	public boolean containsIgnoreCase(Set<String> values, String candidate) {
		if (values == null || values.isEmpty() || candidate == null) {
			return false;
		}
		for (String value : values) {
			if (value != null && value.equalsIgnoreCase(candidate)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public Object mergeReferenceCollections(Object existingRefs, Object incomingRefs) {
		if (existingRefs instanceof ArrayList<?> existingListObj && incomingRefs instanceof ArrayList<?> incomingListObj) {
			ArrayList<Object> merged = new ArrayList<Object>((ArrayList<Object>) existingListObj);
			for (Object incomingRef : incomingListObj) {
				if (!merged.contains(incomingRef)) {
					merged.add(incomingRef);
				}
			}
			return merged;
		}

		if (existingRefs == null) {
			return incomingRefs;
		}

		return existingRefs;
	}

	@SuppressWarnings("unchecked")
	public boolean hasAnyColumnsInTableDictionary(Map<String, Object> tableDictionary) {
		if (tableDictionary == null || tableDictionary.isEmpty()) {
			return false;
		}

		for (Map.Entry<String, Object> tableEntry : tableDictionary.entrySet()) {
			String tableRef = tableEntry.getKey();
			if (tableRef == null
					|| MUMBLE_TABLE_ALIAS_KEY.equals(tableRef)
					|| MUMBLE_TABLE_DICTIONARY_KEY.equals(tableRef)) {
				continue;
			}
			if (!(tableEntry.getValue() instanceof Map<?, ?> columnsMapObj)) {
				continue;
			}
			if (!((Map<String, Object>) columnsMapObj).isEmpty()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * When the insert has no explicit target column list, copy source token refs onto the target table
	 * using the mapped insert interface keys (same names as the resolved source outputs).
	 */
	@SuppressWarnings("unchecked")
	public void applyImplicitInsertTargetTableDictionaryFromMappedSource(
			String insertTargetTableRef,
			Map<String, Object> insertSourceDefinition,
			Map<String, Object> insertInterface) {
		if (insertTargetTableRef == null || insertTargetTableRef.isBlank()) {
			return;
		}
		if (insertSourceDefinition == null || insertSourceDefinition.isEmpty()) {
			return;
		}
		if (insertInterface == null || insertInterface.isEmpty()) {
			return;
		}

		Object sourceQueryDictionaryObj = insertSourceDefinition.get(MUMBLE_QUERY_DICTIONARY_KEY);
		if (!(sourceQueryDictionaryObj instanceof Map<?, ?> sourceQueryDictionaryMapObj)) {
			return;
		}

		HashMap<String, Object> currentTableDictionary = walker.getCurrentTableDictionary();
		HashMap<String, Object> insertTargetDictionary = ensureTableDictionaryEntry(currentTableDictionary, insertTargetTableRef);
		Map<String, Object> sourceQueryDictionary = (Map<String, Object>) sourceQueryDictionaryMapObj;

		for (String inferredColumnName : insertInterface.keySet()) {
			Object sourceRefsObj = sourceQueryDictionary.get(inferredColumnName);
			if (!(sourceRefsObj instanceof ArrayList<?> sourceRefsListObj)) {
				continue;
			}

			ArrayList<Object> copiedRefs = new ArrayList<Object>((ArrayList<Object>) sourceRefsListObj);
			Object existingRefsObj = insertTargetDictionary.get(inferredColumnName);
			if (existingRefsObj == null) {
				insertTargetDictionary.put(inferredColumnName, copiedRefs);
			} else {
				insertTargetDictionary.put(inferredColumnName, mergeReferenceCollections(existingRefsObj, copiedRefs));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void populateImplicitInsertTargetColumnsFromSourceDictionary(
			String insertTargetTableRef,
			Map<String, Object> insertColumns,
			Map<String, Object> insertSourceDefinition,
			Map<String, Object> insertInterface) {
		if (insertColumns != null && !insertColumns.isEmpty()) {
			return;
		}
		applyImplicitInsertTargetTableDictionaryFromMappedSource(
				insertTargetTableRef,
				insertSourceDefinition,
				insertInterface);
	}

	@SuppressWarnings("unchecked")
	public String getInsertTargetTableReference(Map<String, Object> insertNode) {
		if (insertNode == null) {
			return null;
		}

		Object targetTableObj = insertNode.get(MUMBLE_TARGET_TABLE_KEY);
		if (!(targetTableObj instanceof Map<?, ?>)) {
			targetTableObj = insertNode.get(MUMBLE_TABLE_KEY);
		}
		if (!(targetTableObj instanceof Map<?, ?> targetTableMapObj)) {
			return null;
		}

		Map<String, Object> targetTableMap = (Map<String, Object>) targetTableMapObj;
		Object nestedTableObj = targetTableMap.get(MUMBLE_TABLE_KEY);
		if (nestedTableObj instanceof Map<?, ?> nestedTableMapObj) {
			String tableRef = getQualifiedTableReference((Map<String, Object>) nestedTableMapObj);
			return (tableRef == null || tableRef.isBlank()) ? null : tableRef;
		}

		String tableRef = getQualifiedTableReference(targetTableMap);
		return (tableRef == null || tableRef.isBlank()) ? null : tableRef;
	}

	public String consumeInsertSourceScopeKey() {
		Object sourceRefObj = walker.symbolTable.remove(INSERT_SOURCE_REF_KEY);
		if (!(sourceRefObj instanceof String sourceRef) || sourceRef.isBlank()) {
			return null;
		}

		if (sourceRef.startsWith("def_")) {
			return sourceRef.substring("def_".length());
		}
		return sourceRef;
	}

	public boolean isInsertSourceScopeReference(String sourceRef) {
		if (sourceRef == null || sourceRef.isBlank()) {
			return false;
		}
		return sourceRef.startsWith(MUMBLE_QUERY_KEY)
				|| sourceRef.startsWith(MUMBLE_UNION_KEY)
				|| sourceRef.startsWith(MUMBLE_INTERSECT_KEY)
				|| sourceRef.startsWith(MUMBLE_VALUES_KEY)
				|| sourceRef.startsWith(MUMBLE_INSERT_KEY)
				|| sourceRef.startsWith(MUMBLE_UPDATE_KEY)
				|| sourceRef.startsWith(MUMBLE_DELETE_KEY);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> normalizeInsertSourceDefinition(String sourceScopeKey) {
		if (sourceScopeKey == null || sourceScopeKey.isBlank()) {
			return new HashMap<String, Object>();
		}

		String definitionKey = sourceScopeKey.startsWith("def_") ? sourceScopeKey : "def_" + sourceScopeKey;
		Object sourceDefinitionObj = walker.symbolTable.get(definitionKey);

		if (!(sourceDefinitionObj instanceof Map<?, ?>)) {
			Object directScopeObj = walker.symbolTable.remove(sourceScopeKey);
			if (directScopeObj instanceof Map<?, ?>) {
				sourceDefinitionObj = directScopeObj;
				walker.symbolTable.put(definitionKey, sourceDefinitionObj);
			}
		} else if (isInsertSourceScopeReference(sourceScopeKey)) {
			// Avoid leaving a dangling empty queryN/valuesN reference alongside def_queryN/def_valuesN.
			walker.symbolTable.remove(sourceScopeKey);
		}

		if (sourceDefinitionObj instanceof Map<?, ?> sourceDefinitionMapObj) {
			return (Map<String, Object>) sourceDefinitionMapObj;
		}

		return new HashMap<String, Object>();
	}

	/**
	 * Projects a resolved insert source ({@code def_queryN} / {@code def_valuesN}) onto insert-target column names.
	 * Source scope resolution must already be complete before this runs.
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> mapInsertTargetInterfaceFromResolvedSource(
			Map<String, Object> sourceDefinition,
			Map<String, Object> insertColumns,
			String sourceScopeKey,
			String insertTargetTableRef) {
		Map<String, Object> insertInterface = new LinkedHashMap<String, Object>();
		ArrayList<String> insertColumnNames = extractInsertColumnNames(insertColumns);
		if (sourceDefinition == null || sourceDefinition.isEmpty()) {
			for (String insertColumnName : insertColumnNames) {
				insertInterface.put(insertColumnName, new ArrayList<Object>());
			}
			return insertInterface;
		}

		Object sourceInterfaceObj = sourceDefinition.get(MUMBLE_INTERFACE_KEY);
		if (!(sourceInterfaceObj instanceof Map<?, ?> sourceInterfaceObjMap)) {
			for (String insertColumnName : insertColumnNames) {
				insertInterface.put(insertColumnName, new ArrayList<Object>());
			}
			return insertInterface;
		}

		Map<String, Object> sourceInterface = (Map<String, Object>) sourceInterfaceObjMap;
		if (sourceInterface.isEmpty()) {
			for (String insertColumnName : insertColumnNames) {
				insertInterface.put(insertColumnName, new ArrayList<Object>());
			}
			return insertInterface;
		}

		if (insertColumns == null || insertColumns.isEmpty()) {
			Map<String, Object> clonedSourceInterface = new HashMap<String, Object>(sourceInterface);
			for (String childColumnName : clonedSourceInterface.keySet()) {
				insertInterface.put(childColumnName, buildSingleInsertInterfaceReference(childColumnName, sourceScopeKey));
			}
			applyImplicitInsertTargetTableDictionaryFromMappedSource(
					insertTargetTableRef,
					sourceDefinition,
					insertInterface);
			return insertInterface;
		}

		ArrayList<String> sourceColumnNames = resolveInsertSourceColumnSequence(sourceDefinition, sourceInterface);
		for (int i = 0; i < insertColumnNames.size(); i++) {
			String insertColumnName = insertColumnNames.get(i);
			String sourceColumnName = (i < sourceColumnNames.size())
					? sourceColumnNames.get(i)
					: insertColumnName;
			insertInterface.put(insertColumnName, buildSingleInsertInterfaceReference(sourceColumnName, sourceScopeKey));
		}

		return insertInterface;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> buildInsertInterfaceFromSource(
			Map<String, Object> sourceDefinition,
			Map<String, Object> insertColumns,
			String sourceScopeKey) {
		return mapInsertTargetInterfaceFromResolvedSource(
				sourceDefinition,
				insertColumns,
				sourceScopeKey,
				null);
	}

	public boolean isSetOperationParticipantKey(String key) {
		if (key == null) {
			return false;
		}

		String normalizedKey = normalizeSetOperationParticipantKey(key);
		return matchesNumberedScopeKey(normalizedKey, MUMBLE_QUERY_KEY)
				|| matchesNumberedScopeKey(normalizedKey, MUMBLE_UNION_KEY)
				|| matchesNumberedScopeKey(normalizedKey, MUMBLE_INTERSECT_KEY)
				|| matchesNumberedScopeKey(normalizedKey, MUMBLE_VALUES_KEY);
	}

	private boolean matchesNumberedScopeKey(String key, String prefix) {
		if (key == null || prefix == null || !key.startsWith(prefix)) {
			return false;
		}

		String suffix = key.substring(prefix.length());
		if (suffix.isEmpty()) {
			return true;
		}

		for (int i = 0; i < suffix.length(); i++) {
			if (!Character.isDigit(suffix.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	public String normalizeSetOperationParticipantKey(String key) {
		if (key == null) {
			return null;
		}
		if (key.startsWith("def_")) {
			return key.substring("def_".length());
		}
		return key;
	}

	public int extractSetOperationParticipantKeyIndex(String key) {
		if (key == null) {
			return Integer.MAX_VALUE;
		}

		String normalizedKey = normalizeSetOperationParticipantKey(key);
		int prefixLength = -1;
		if (normalizedKey.startsWith(MUMBLE_QUERY_KEY)) {
			prefixLength = MUMBLE_QUERY_KEY.length();
		} else if (normalizedKey.startsWith(MUMBLE_UNION_KEY)) {
			prefixLength = MUMBLE_UNION_KEY.length();
		} else if (normalizedKey.startsWith(MUMBLE_INTERSECT_KEY)) {
			prefixLength = MUMBLE_INTERSECT_KEY.length();
		} else if (normalizedKey.startsWith(MUMBLE_VALUES_KEY)) {
			prefixLength = MUMBLE_VALUES_KEY.length();
		}

		if (prefixLength < 0) {
			return Integer.MAX_VALUE;
		}

		String suffix = normalizedKey.substring(prefixLength);
		if (suffix.isEmpty()) {
			return 0;
		}

		try {
			return Integer.parseInt(suffix);
		} catch (NumberFormatException ex) {
			return Integer.MAX_VALUE;
		}
	}

	@SuppressWarnings("unchecked")
	public boolean isSetOperationDefinition(Map<String, Object> scopeDefinition) {
		if (scopeDefinition == null || scopeDefinition.isEmpty()) {
			return false;
		}

		int participantCount = 0;
		for (Map.Entry<String, Object> entry : scopeDefinition.entrySet()) {
			if (isSetOperationParticipantKey(entry.getKey()) && entry.getValue() instanceof Map<?, ?>) {
				participantCount++;
			}
		}

		return participantCount >= 2;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> findFirstSetOperationParticipant(Map<String, Object> setOperationDefinition) {
		if (setOperationDefinition == null || setOperationDefinition.isEmpty()) {
			return null;
		}

		String selectedKey = null;
		int lowestIndex = Integer.MAX_VALUE;
		for (Map.Entry<String, Object> entry : setOperationDefinition.entrySet()) {
			String key = entry.getKey();
			if (!isSetOperationParticipantKey(key) || !(entry.getValue() instanceof Map<?, ?>)) {
				continue;
			}

			int keyIndex = extractSetOperationParticipantKeyIndex(key);
			if (keyIndex < lowestIndex) {
				lowestIndex = keyIndex;
				selectedKey = key;
			} else if (keyIndex == lowestIndex && selectedKey != null && key.compareTo(selectedKey) < 0) {
				selectedKey = key;
			}
		}

		if (selectedKey == null) {
			return null;
		}

		return (Map<String, Object>) setOperationDefinition.get(selectedKey);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> findFirstSetOperationLeafDefinition(Map<String, Object> scopeDefinition) {
		if (scopeDefinition == null || scopeDefinition.isEmpty()) {
			return null;
		}

		if (!isSetOperationDefinition(scopeDefinition)) {
			return scopeDefinition;
		}

		Map<String, Object> firstParticipant = findFirstSetOperationParticipant(scopeDefinition);
		if (firstParticipant == null) {
			return null;
		}

		if (isSetOperationDefinition(firstParticipant)) {
			return findFirstSetOperationLeafDefinition(firstParticipant);
		}

		return firstParticipant;
	}

	@SuppressWarnings("unchecked")
	public Object resolveInsertSourceSelectSequenceObject(Map<String, Object> scopeDefinition) {
		if (scopeDefinition == null || scopeDefinition.isEmpty()) {
			return null;
		}

		Object sequenceObj = scopeDefinition.get(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY);
		if (sequenceObj instanceof ArrayList<?>) {
			return sequenceObj;
		}

		if (!isSetOperationDefinition(scopeDefinition)) {
			return null;
		}

		Map<String, Object> firstParticipant = findFirstSetOperationParticipant(scopeDefinition);
		if (firstParticipant != null) {
			Object participantSequenceObj = resolveInsertSourceSelectSequenceObject(firstParticipant);
			if (participantSequenceObj instanceof ArrayList<?>) {
				return participantSequenceObj;
			}
		}

		Map<String, Object> firstLeaf = findFirstSetOperationLeafDefinition(scopeDefinition);
		if (firstLeaf != null && firstLeaf != scopeDefinition && firstLeaf != firstParticipant) {
			return firstLeaf.get(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public void clearInsertSourceSequenceFromSetOperationTree(Map<String, Object> scopeDefinition) {
		if (scopeDefinition == null || scopeDefinition.isEmpty()) {
			return;
		}

		scopeDefinition.remove(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY);
		for (Object value : scopeDefinition.values()) {
			if (value instanceof Map<?, ?> valueMapObj) {
				clearInsertSourceSequenceFromSetOperationTree((Map<String, Object>) valueMapObj);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void hoistInsertSourceSequenceToSetOperationRoot(Map<String, Object> setOperationDefinition) {
		if (setOperationDefinition == null || setOperationDefinition.isEmpty()
				|| !isSetOperationDefinition(setOperationDefinition)) {
			return;
		}

		Map<String, Object> firstLeaf = findFirstSetOperationLeafDefinition(setOperationDefinition);
		Map<String, Object> firstParticipant = findFirstSetOperationParticipant(setOperationDefinition);
		Object sequenceObj = resolveInsertSourceSelectSequenceObject(setOperationDefinition);
		if (!(sequenceObj instanceof ArrayList<?>) && firstLeaf != null) {
			sequenceObj = firstLeaf.get(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY);
		}
		if (!(sequenceObj instanceof ArrayList<?>) && firstParticipant != null) {
			sequenceObj = firstParticipant.get(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY);
		}
		if (!(sequenceObj instanceof ArrayList<?>)) {
			return;
		}

		clearInsertSourceSequenceFromSetOperationTree(setOperationDefinition);
		setOperationDefinition.put(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY, sequenceObj);
	}

	/**
	 * Set-op exit: recursively publish multi-ref interfaces on nested set-ops, then merge
	 * each participant's positional column refs onto the output interface (first-branch names).
	 */
	@SuppressWarnings("unchecked")
	public void publishSetOperationInterfaceAtExit(Map<String, Object> setOperationDefinition) {
		if (setOperationDefinition == null || setOperationDefinition.isEmpty()
				|| !isSetOperationDefinition(setOperationDefinition)) {
			return;
		}

		ArrayList<Map<String, Object>> participants = collectSetOperationParticipantsInOrder(setOperationDefinition);
		if (participants.size() < 2) {
			return;
		}

		for (Map<String, Object> participant : participants) {
			if (isSetOperationDefinition(participant)) {
				publishSetOperationInterfaceAtExit(participant);
			}
		}

		ArrayList<String> outputColumnNames = extractInterfaceColumnNamesInOrder(participants.get(0));
		if (outputColumnNames.isEmpty()) {
			return;
		}

		LinkedHashMap<String, Object> mergedInterface = new LinkedHashMap<String, Object>();
		for (int columnIndex = 0; columnIndex < outputColumnNames.size(); columnIndex++) {
			String outputColumnName = outputColumnNames.get(columnIndex);
			ArrayList<Object> mergedRefs = new ArrayList<Object>();
			for (Map<String, Object> participant : participants) {
				appendInterfaceReferenceEntries(
						mergedRefs,
						extractInterfaceReferenceEntriesAtPosition(participant, columnIndex));
			}
			// Preserve output columns even when refs are empty (e.g. literal SELECT items).
			mergedInterface.put(outputColumnName, mergedRefs);
		}

		if (!mergedInterface.isEmpty()) {
			setOperationDefinition.put(MUMBLE_INTERFACE_KEY, mergedInterface);
		}
	}

	/**
	 * Set-op exit finalization: always publish multi-ref interface; hoist insert sequence when
	 * the set-op is an insert source.
	 */
	@SuppressWarnings("unchecked")
	public void finalizeSetOperationAtExit(Map<String, Object> setOperationDefinition, boolean insertSource) {
		if (setOperationDefinition == null || setOperationDefinition.isEmpty()) {
			return;
		}

		publishSetOperationInterfaceAtExit(setOperationDefinition);
		if (insertSource && isSetOperationDefinition(setOperationDefinition)) {
			hoistInsertSourceSequenceToSetOperationRoot(setOperationDefinition);
		}
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Map<String, Object>> collectSetOperationParticipantsInOrder(
			Map<String, Object> setOperationDefinition) {
		ArrayList<Map<String, Object>> participants = new ArrayList<Map<String, Object>>();
		if (setOperationDefinition == null || setOperationDefinition.isEmpty()) {
			return participants;
		}

		ArrayList<Map.Entry<String, Object>> participantEntries = new ArrayList<Map.Entry<String, Object>>();
		for (Map.Entry<String, Object> entry : setOperationDefinition.entrySet()) {
			if (isSetOperationParticipantKey(entry.getKey()) && entry.getValue() instanceof Map<?, ?>) {
				participantEntries.add((Map.Entry<String, Object>) entry);
			}
		}

		participantEntries.sort((left, right) -> {
			int leftIndex = extractSetOperationParticipantKeyIndex(left.getKey());
			int rightIndex = extractSetOperationParticipantKeyIndex(right.getKey());
			int indexCompare = Integer.compare(leftIndex, rightIndex);
			if (indexCompare != 0) {
				return indexCompare;
			}
			return left.getKey().compareTo(right.getKey());
		});

		for (Map.Entry<String, Object> participantEntry : participantEntries) {
			participants.add((Map<String, Object>) participantEntry.getValue());
		}

		return participants;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> extractInterfaceColumnNamesInOrder(Map<String, Object> scopeDefinition) {
		ArrayList<String> columnNames = new ArrayList<String>();
		if (scopeDefinition == null || scopeDefinition.isEmpty()) {
			return columnNames;
		}

		Object interfaceObj = scopeDefinition.get(MUMBLE_INTERFACE_KEY);
		if (!(interfaceObj instanceof Map<?, ?> interfaceMapObj)) {
			return columnNames;
		}

		for (Object columnNameObj : ((Map<String, Object>) interfaceMapObj).keySet()) {
			if (columnNameObj instanceof String columnName && !columnName.isBlank()) {
				columnNames.add(columnName);
			}
		}

		return columnNames;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Object> extractInterfaceReferenceEntriesAtPosition(
			Map<String, Object> scopeDefinition,
			int columnIndex) {
		ArrayList<Object> refs = new ArrayList<Object>();
		if (scopeDefinition == null || scopeDefinition.isEmpty() || columnIndex < 0) {
			return refs;
		}

		ArrayList<String> columnNames = extractInterfaceColumnNamesInOrder(scopeDefinition);
		if (columnIndex >= columnNames.size()) {
			return refs;
		}

		Object interfaceObj = scopeDefinition.get(MUMBLE_INTERFACE_KEY);
		if (!(interfaceObj instanceof Map<?, ?> interfaceMapObj)) {
			return refs;
		}

		Object refsObj = ((Map<String, Object>) interfaceMapObj).get(columnNames.get(columnIndex));
		appendInterfaceReferenceEntries(refs, refsObj);
		return refs;
	}

	@SuppressWarnings("unchecked")
	public void appendInterfaceReferenceEntries(ArrayList<Object> targetRefs, Object refsObj) {
		if (targetRefs == null || refsObj == null) {
			return;
		}

		if (refsObj instanceof List<?> refsList) {
			for (Object refObj : refsList) {
				if (refObj instanceof Map<?, ?> refMapObj) {
					targetRefs.add(new HashMap<String, Object>((Map<String, Object>) refMapObj));
				}
			}
			return;
		}

		if (refsObj instanceof Map<?, ?> refMapObj) {
			targetRefs.add(new HashMap<String, Object>((Map<String, Object>) refMapObj));
		}
	}

	@SuppressWarnings("unchecked")
	public void finalizeInsertSourceAtPrimaryExit(Map<String, Object> symbols) {
		if (symbols == null || symbols.isEmpty()) {
			return;
		}

		String scopeRefKey = getSubqueryReferenceKey(symbols);
		if (scopeRefKey == null || scopeRefKey.isBlank()) {
			return;
		}

		if (!scopeRefKey.startsWith("def_")) {
			Object scopeObj = symbols.remove(scopeRefKey);
			if (scopeObj != null) {
				symbols.put("def_" + scopeRefKey, scopeObj);
			}
		} else {
			scopeRefKey = scopeRefKey.substring("def_".length());
		}

		Object scopeDefinitionObj = symbols.get("def_" + scopeRefKey);
		if (scopeDefinitionObj instanceof Map<?, ?> scopeDefinitionMapObj) {
			Map<String, Object> scopeDefinition = (Map<String, Object>) scopeDefinitionMapObj;
			if (isSetOperationDefinition(scopeDefinition)) {
				finalizeSetOperationAtExit(scopeDefinition, true);
			}
		}

		symbols.put(INSERT_SOURCE_REF_KEY, scopeRefKey);
	}

	@SuppressWarnings("unchecked")
	public Object resolveInsertSourceColumnFromScopeDefinition(
			Map<String, Object> sourceScopeDefinition,
			String sourceColumnName) {
		if (sourceScopeDefinition == null || sourceColumnName == null || sourceColumnName.isBlank()) {
			return null;
		}

		Object sourceScopeQueryDictionaryObj = sourceScopeDefinition.get(MUMBLE_QUERY_DICTIONARY_KEY);
		if (sourceScopeQueryDictionaryObj instanceof Map<?, ?> sourceScopeQueryDictionaryMapObj) {
			Map<String, Object> sourceScopeQueryDictionary =
					(Map<String, Object>) sourceScopeQueryDictionaryMapObj;
			Object directRefs = sourceScopeQueryDictionary.get(sourceColumnName);
			if (directRefs != null) {
				return directRefs;
			}
		}

		if (isSetOperationDefinition(sourceScopeDefinition)) {
			Map<String, Object> firstLeaf = findFirstSetOperationLeafDefinition(sourceScopeDefinition);
			if (firstLeaf != null && firstLeaf != sourceScopeDefinition) {
				return resolveInsertSourceColumnFromScopeDefinition(firstLeaf, sourceColumnName);
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> buildInsertSourceColumnSequenceList(
			Map<String, Object> sourceDefinition,
			Map<String, Object> sourceInterface) {
		ArrayList<String> sourceColumnNames = new ArrayList<String>();
		if (sourceInterface == null || sourceInterface.isEmpty()) {
			return sourceColumnNames;
		}

		if (sourceDefinition != null) {
			Object sequenceObj = sourceDefinition.get(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY);
			if (sequenceObj instanceof ArrayList<?> sequenceList) {
				for (Object sequenceItem : sequenceList) {
					if (!(sequenceItem instanceof String columnName)) {
						continue;
					}
					if (!sourceInterface.containsKey(columnName) || sourceColumnNames.contains(columnName)) {
						continue;
					}
					sourceColumnNames.add(columnName);
				}
			}
		}

		for (String interfaceKey : sourceInterface.keySet()) {
			if (!sourceColumnNames.contains(interfaceKey)) {
				sourceColumnNames.add(interfaceKey);
			}
		}

		return sourceColumnNames;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> resolveInsertSourceColumnSequence(
			Map<String, Object> sourceDefinition,
			Map<String, Object> sourceInterface) {
		ArrayList<String> sourceColumnNames = buildInsertSourceColumnSequenceList(sourceDefinition, sourceInterface);
		if (sourceDefinition != null && isSetOperationDefinition(sourceDefinition)) {
			clearInsertSourceSequenceFromSetOperationTree(sourceDefinition);
		} else if (sourceDefinition != null) {
			sourceDefinition.remove(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY);
		}

		return sourceColumnNames;
	}

	public ArrayList<Object> buildSingleInsertInterfaceReference(String sourceColumnName, String sourceScopeKey) {
		ArrayList<Object> refs = new ArrayList<Object>();
		HashMap<String, Object> ref = new HashMap<String, Object>();
		ref.put(MUMBLE_NAME_KEY, sourceColumnName);
		ref.put(MUMBLE_TABLE_REF_KEY, sourceScopeKey);
		refs.add(ref);
		return refs;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> extractInsertColumnNames(Map<String, Object> insertColumns) {
		ArrayList<String> columnNames = new ArrayList<String>();
		if (insertColumns == null || insertColumns.isEmpty()) {
			return columnNames;
		}

		for (int i = 1; i <= insertColumns.size(); i++) {
			Object columnEntryObj = insertColumns.get(String.valueOf(i));
			String columnName = extractInsertColumnNameFromEntry(columnEntryObj);
			if (columnName != null && !columnName.isBlank()) {
				columnNames.add(columnName);
			}
		}

		if (!columnNames.isEmpty()) {
			return columnNames;
		}

		for (Object columnEntryObj : insertColumns.values()) {
			String columnName = extractInsertColumnNameFromEntry(columnEntryObj);
			if (columnName != null && !columnName.isBlank()) {
				columnNames.add(columnName);
			}
		}

		return columnNames;
	}

	@SuppressWarnings("unchecked")
	public String extractInsertColumnNameFromEntry(Object columnEntryObj) {
		if (!(columnEntryObj instanceof Map<?, ?> columnEntryMapObj)) {
			return null;
		}

		Map<String, Object> columnEntryMap = (Map<String, Object>) columnEntryMapObj;
		Object columnObj = columnEntryMap.get(MUMBLE_COLUMN_KEY);
		if (columnObj instanceof Map<?, ?> columnMapObj) {
			Object nameObj = ((Map<String, Object>) columnMapObj).get(MUMBLE_NAME_KEY);
			if (nameObj != null) {
				return nameObj.toString();
			}
		}

		Object nameObj = columnEntryMap.get(MUMBLE_NAME_KEY);
		if (nameObj != null) {
			return nameObj.toString();
		}

		for (Object nestedValueObj : columnEntryMap.values()) {
			if (!(nestedValueObj instanceof Map<?, ?> nestedMapObj)) {
				continue;
			}
			Object nestedNameObj = ((Map<String, Object>) nestedMapObj).get(MUMBLE_NAME_KEY);
			if (nestedNameObj != null) {
				return nestedNameObj.toString();
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public String getUpdateTargetTableReference(Map<String, Object> updateNode) {
		if (updateNode == null) {
			return null;
		}

		Object targetTableObj = updateNode.get(MUMBLE_TABLE_KEY);
		if (targetTableObj instanceof Map<?, ?> targetTableMapObj) {
			String tableRef = getQualifiedTableReference((Map<String, Object>) targetTableMapObj);
			return (tableRef == null || tableRef.isBlank()) ? null : tableRef;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public String getDeleteTargetTableReference(Map<String, Object> deleteNode) {
		if (deleteNode == null) {
			return null;
		}

		Object targetTableObj = deleteNode.get(MUMBLE_TABLE_KEY);
		if (targetTableObj instanceof Map<?, ?> targetTableMapObj) {
			String tableRef = getQualifiedTableReference((Map<String, Object>) targetTableMapObj);
			return (tableRef == null || tableRef.isBlank()) ? null : tableRef;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public void initializeUpdateTargetTableSubtree(String updateTargetTableRef) {
		if (updateTargetTableRef == null || updateTargetTableRef.isBlank()) {
			return;
		}

		Object targetTableObj = walker.symbolTable.get(MUMBLE_TARGET_TABLE_KEY);
		HashMap<String, Object> targetTableMap;
		if (targetTableObj instanceof HashMap<?, ?>) {
			targetTableMap = (HashMap<String, Object>) targetTableObj;
		} else {
			targetTableMap = new HashMap<String, Object>();
			walker.symbolTable.put(MUMBLE_TARGET_TABLE_KEY, targetTableMap);
		}

		String normalizedTargetRef = normalizeTableRef(updateTargetTableRef);
		targetTableMap.putIfAbsent(normalizedTargetRef, new HashMap<String, Object>());
	}

	@SuppressWarnings("unchecked")
	public String getSingleUpdateFromTableReference(Map<String, Object> updateAst) {
		if (updateAst == null) {
			return null;
		}

		Map<String, Object> updateNode = getUpdateNode(updateAst);
		if (updateNode == null) {
			return null;
		}

		Object fromObj = updateNode.get(MUMBLE_FROM_KEY);
		if (!(fromObj instanceof Map<?, ?> fromMapObj)) {
			return null;
		}

		Map<String, Object> fromMap = (Map<String, Object>) fromMapObj;
		Object tableObj = fromMap.get(MUMBLE_TABLE_KEY);
		if (tableObj instanceof Map<?, ?> tableMapObj) {
			String tableRef = getQualifiedTableReference((Map<String, Object>) tableMapObj);
			return (tableRef == null || tableRef.isBlank()) ? null : tableRef;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getUpdateNode(Map<String, Object> updateAst) {
		if (updateAst == null) {
			return null;
		}

		Object updateObj = updateAst.get(MUMBLE_UPDATE_KEY);
		if (updateObj instanceof Map<?, ?> updateMapObj) {
			return (Map<String, Object>) updateMapObj;
		}

		return updateAst;
	}

	@SuppressWarnings("unchecked")
	public void moveAssignmentLhsToLhsUnresolvedColumns(Map<String, Object> leftAssignment) {
		Map<String, Object> lhsColumnReference = extractAssignmentLhsColumnReference(leftAssignment);
		if (lhsColumnReference == null || lhsColumnReference.isEmpty()) {
			return;
		}

		String lhsQualifiedKey = makeQualifiedColumnReferenceKey(lhsColumnReference);
		String lhsName = (lhsColumnReference.get(MUMBLE_NAME_KEY) == null)
				? null
				: lhsColumnReference.get(MUMBLE_NAME_KEY).toString();

		Object unresolvedObject = walker.symbolTable.get(MUMBLE_UNRESOLVED_COLUMN_KEY);
		HashMap<String, Object> unresolvedMap;
		if (unresolvedObject instanceof HashMap<?, ?>) {
			unresolvedMap = (HashMap<String, Object>) unresolvedObject;
		} else {
			unresolvedMap = new HashMap<String, Object>();
			walker.symbolTable.put(MUMBLE_UNRESOLVED_COLUMN_KEY, unresolvedMap);
		}

		Object lhsEntry = null;
		if (lhsQualifiedKey != null) {
			lhsEntry = unresolvedMap.remove(lhsQualifiedKey);
		}
		if (lhsEntry == null && lhsName != null) {
			lhsEntry = unresolvedMap.remove(lhsName);
		}
		if (lhsEntry == null) {
			lhsEntry = new HashMap<String, Object>(lhsColumnReference);
		}

		Object lhsUnresolvedObject = walker.symbolTable.get(MUMBLE_LHS_UNRESOLVED_COLUMNS_KEY);
		HashMap<String, Object> lhsUnresolvedColumns;
		if (lhsUnresolvedObject instanceof HashMap<?, ?>) {
			lhsUnresolvedColumns = (HashMap<String, Object>) lhsUnresolvedObject;
		} else {
			lhsUnresolvedColumns = new HashMap<String, Object>();
			walker.symbolTable.put(MUMBLE_LHS_UNRESOLVED_COLUMNS_KEY, lhsUnresolvedColumns);
		}

		String lhsStorageKey = (lhsQualifiedKey == null || lhsQualifiedKey.isBlank()) ? lhsName : lhsQualifiedKey;
		if (lhsStorageKey != null && !lhsStorageKey.isBlank()) {
			lhsUnresolvedColumns.put(lhsStorageKey, lhsEntry);
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> extractAssignmentLhsColumnReference(Map<String, Object> leftAssignment) {
		if (leftAssignment == null) {
			return null;
		}

		Object columnObject = leftAssignment.get(MUMBLE_COLUMN_KEY);
		if (columnObject instanceof Map<?, ?> columnMapObj) {
			return new HashMap<String, Object>((Map<String, Object>) columnMapObj);
		}

		Object substitutionObject = leftAssignment.get(MUMBLE_SUBSTITUTION_KEY);
		if (substitutionObject instanceof Map<?, ?> substitutionMapObj) {
			return new HashMap<String, Object>((Map<String, Object>) substitutionMapObj);
		}

		return null;
	}

	public String makeQualifiedColumnReferenceKey(Map<String, Object> columnReference) {
		if (columnReference == null) {
			return null;
		}

		Object nameObject = columnReference.get(MUMBLE_NAME_KEY);
		if (!(nameObject instanceof String columnName) || columnName.isBlank()) {
			return null;
		}

		Object tableRefObject = columnReference.get(MUMBLE_TABLE_REF_KEY);
		if (tableRefObject instanceof String tableRef && !tableRef.isBlank()) {
			return tableRef + "." + columnName;
		}

		return columnName;
	}

	@SuppressWarnings("unchecked")
	public void addUpdateAssignmentSymbolReference(
			String assignmentKey,
			Map<String, Object> assignmentValue,
			String lhsTokenString) {
		if (assignmentKey == null || assignmentKey.isBlank()) {
			return;
		}

		Object assignmentsObject = walker.symbolTable.get(MUMBLE_ASSIGNMENTS_KEY);
		HashMap<String, Object> assignmentsMap;
		if (assignmentsObject instanceof HashMap<?, ?>) {
			assignmentsMap = (HashMap<String, Object>) assignmentsObject;
		} else {
			assignmentsMap = new HashMap<String, Object>();
			walker.symbolTable.put(MUMBLE_ASSIGNMENTS_KEY, assignmentsMap);
		}

		ArrayList<Object> rhsColumnReferences = new ArrayList<Object>();
		if (assignmentValue != null && !assignmentValue.isEmpty()) {
			HashMap<String, Object> flattenTarget = new HashMap<String, Object>();
			flattenTarget.putAll(assignmentValue);
			flattenSubTreeForInterfaceColumns(flattenTarget, rhsColumnReferences);
		}
		assignmentsMap.put(assignmentKey, rhsColumnReferences);

		Object dictionaryObject = walker.symbolTable.get(MUMBLE_UPDATE_DICTIONARY_KEY);
		HashMap<String, Object> updateDictionary;
		if (dictionaryObject instanceof HashMap<?, ?>) {
			updateDictionary = (HashMap<String, Object>) dictionaryObject;
		} else {
			updateDictionary = new HashMap<String, Object>();
			walker.symbolTable.put(MUMBLE_UPDATE_DICTIONARY_KEY, updateDictionary);
		}

		ArrayList<String> lhsTokenList = new ArrayList<String>();
		if (lhsTokenString != null && !lhsTokenString.isBlank()) {
			lhsTokenList.add(lhsTokenString);
		}
		updateDictionary.put(assignmentKey, lhsTokenList);
	}

	@SuppressWarnings("unchecked")
	public String extractAssignmentLhsName(Map<String, Object> leftAssignment) {
		if (leftAssignment == null) {
			return null;
		}

		Object columnObject = leftAssignment.get(MUMBLE_COLUMN_KEY);
		if (columnObject instanceof Map<?, ?> columnMapObj) {
			Object nameObject = ((Map<String, Object>) columnMapObj).get(MUMBLE_NAME_KEY);
			if (nameObject != null) {
				return nameObject.toString();
			}
		}

		Object substitutionObject = leftAssignment.get(MUMBLE_SUBSTITUTION_KEY);
		if (substitutionObject instanceof Map<?, ?> substitutionMapObj) {
			Object nameObject = ((Map<String, Object>) substitutionMapObj).get(MUMBLE_NAME_KEY);
			if (nameObject != null) {
				return nameObject.toString();
			}
		}

		return null;
	}

	public String resolveAssignmentLhsTokenString(SQLSelectParserParser.Assignment_expressionContext ctx) {
		if (ctx == null || ctx.getChildCount() == 0) {
			Token startToken = (ctx == null) ? null : ctx.getStart();
			return (startToken == null) ? null : startToken.toString();
		}

		if (ctx.getChild(0) instanceof ParserRuleContext leftContext) {
			Token lhsToken = leftContext.getStop();
			if (lhsToken != null) {
				return lhsToken.toString();
			}
		}

		Token startToken = ctx.getStart();
		return (startToken == null) ? null : startToken.toString();
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> resolveCurrentValuesColumns() {
			Object directValuesObj = walker.symbolTable.get(MUMBLE_VALUES_KEY);
			if (directValuesObj instanceof Map<?, ?> directValuesMap) {
				return new HashMap<String, Object>((Map<String, Object>) directValuesMap);
			}

			HashMap<String, Object> derivedValues = new HashMap<String, Object>();
			Object unresolvedObj = walker.symbolTable.get(MUMBLE_UNRESOLVED_COLUMN_KEY);
			if (unresolvedObj instanceof Map<?, ?> unresolvedMapObj) {
				Map<String, Object> unresolvedMap = (Map<String, Object>) unresolvedMapObj;
				for (Map.Entry<String, Object> unresolvedEntry : unresolvedMap.entrySet()) {
					String unresolvedKey = unresolvedEntry.getKey();
					if (unresolvedKey == null || !unresolvedKey.startsWith(MUMBLE_VALUES_KEY + ".")) {
						continue;
					}

					String valueColumnName = unresolvedKey.substring((MUMBLE_VALUES_KEY + ".").length());
					Object valueObject = unresolvedEntry.getValue();
					if (!(valueObject instanceof Map<?, ?> valueMapObj)) {
						continue;
					}

					Map<String, Object> valueMap = (Map<String, Object>) valueMapObj;
					Object locationsObj = valueMap.get("locations");
					if (locationsObj instanceof ArrayList<?>) {
						derivedValues.put(valueColumnName, locationsObj);
					}
				}
			}

			return derivedValues;
		}

	public String getPendingValuesScopeKey() {
		return MUMBLE_VALUES_KEY + walker.queryCount;
	}

	public Map<String, Object> buildValuesOutputInterface(Map<String, Object> valueColumns) {
		Map<String, Object> interfaceMap = new HashMap<String, Object>();
		if (valueColumns == null || valueColumns.isEmpty()) {
			return interfaceMap;
		}

		for (Map.Entry<String, Object> entry : valueColumns.entrySet()) {
			String columnName = entry.getKey();
			if (columnName == null || columnName.isBlank()) {
				continue;
			}
			if (!(entry.getValue() instanceof ArrayList<?>)) {
				continue;
			}
			// VALUES scope interface keys mirror query_dictionary columns; refs are filled on parent scopes.
			interfaceMap.put(columnName, new ArrayList<Object>());
		}

		return interfaceMap;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> mergeValuesColumnsIntoQueryDictionary(
			HashMap<String, Object> scopeSymbols,
			HashMap<String, Object> queryDictionary) {
		HashMap<String, Object> localQueryDictionary = (queryDictionary == null)
				? new HashMap<String, Object>()
				: queryDictionary;

		Object valuesObj = scopeSymbols.remove(MUMBLE_VALUES_KEY);
		if (valuesObj instanceof Map<?, ?> valuesMapObj) {
			Map<String, Object> valuesMap = (Map<String, Object>) valuesMapObj;
			for (Map.Entry<String, Object> valuesEntry : valuesMap.entrySet()) {
				String valuesColumn = valuesEntry.getKey();
				Object valuesRefs = valuesEntry.getValue();
				if (valuesColumn == null || valuesRefs instanceof String) {
					continue;
				}
				localQueryDictionary.putIfAbsent(valuesColumn, valuesRefs);
			}
		}

		return localQueryDictionary;
	}

	@SuppressWarnings("unchecked")
	public void resolveValuesScopeSymbolTable(HashMap<String, Object> scopeSymbols) {
		if (scopeSymbols == null || scopeSymbols.isEmpty()) {
			return;
		}

		Object interfaceObj = scopeSymbols.get(MUMBLE_INTERFACE_KEY);
		Object queryDictionaryObj = scopeSymbols.get(MUMBLE_QUERY_DICTIONARY_KEY);
		if (interfaceObj instanceof HashMap<?, ?> interfaceMapObj
				&& queryDictionaryObj instanceof HashMap<?, ?> queryDictionaryMapObj) {
			walker.validateQueryInterface(
					(HashMap<String, Object>) interfaceMapObj,
					(HashMap<String, Object>) queryDictionaryMapObj,
					new HashMap<String, Object>(),
					new HashMap<String, Object>());
		}

		// Literal-only VALUES scopes have no FROM sources; keep prior behavior of not
		// leaving tuple-local unresolved entries on the published scope payload.
		scopeSymbols.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
		scopeSymbols.remove(MUMBLE_TABLE_ALIAS_KEY);
	}

	@SuppressWarnings("unchecked")
	public void publishQueryLikeScope(String scopeKey, HashMap<String, Object> scopePayload) {
		if (scopeKey == null || scopeKey.isBlank() || scopePayload == null) {
			return;
		}

		Object queryDictionaryObj = scopePayload.get(MUMBLE_QUERY_DICTIONARY_KEY);
		if (queryDictionaryObj instanceof HashMap<?, ?> queryDictionaryMapObj) {
			walker.queryColumnDictionaryMap.put(scopeKey, (HashMap<String, Object>) queryDictionaryMapObj);
		} else {
			walker.queryColumnDictionaryMap.put(scopeKey, new HashMap<String, Object>());
		}

		scopePayload.remove(MUMBLE_SCALAR_SUBQUERY_ALIASES_KEY);
		walker.popSymbolTable(scopeKey, scopePayload);
		walker.queryCount++;
	}

	@SuppressWarnings("unchecked")
	public void finalizeValuesScopeSymbolTable() {
		HashMap<String, Object> scopeSymbols = walker.symbolTable;
		String scopeKey = getPendingValuesScopeKey();

		HashMap<String, Object> localCurrentQueryDictionary =
				(HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_QUERY_DICTIONARY_KEY);
		localCurrentQueryDictionary = mergeValuesColumnsIntoQueryDictionary(scopeSymbols, localCurrentQueryDictionary);

		scopeSymbols.put(MUMBLE_QUERY_DICTIONARY_KEY, localCurrentQueryDictionary);
		scopeSymbols.put(MUMBLE_INTERFACE_KEY, buildValuesOutputInterface(localCurrentQueryDictionary));
		scopeSymbols.put(MUMBLE_TABLE_DICTIONARY_KEY, new HashMap<String, Object>());

		resolveValuesScopeSymbolTable(scopeSymbols);
		publishQueryLikeScope(scopeKey, scopeSymbols);
	}

	/**
	 * Records the token location of the insert target column list for cardinality diagnostics.
	 */
	public void recordInsertTargetColumnListLocation(
			SQLSelectParserParser.Insert_target_table_primaryContext ctx) {
		if (ctx == null || ctx.column_reference_list() == null) {
			return;
		}

		org.antlr.v4.runtime.Token locationToken = null;
		if (ctx.LEFT_PAREN() != null) {
			locationToken = ctx.LEFT_PAREN().getSymbol();
		} else {
			locationToken = ctx.column_reference_list().getStart();
		}
		if (locationToken == null) {
			return;
		}

		HashMap<String, Object> location = new HashMap<String, Object>();
		location.put("line", locationToken.getLine());
		location.put("char", locationToken.getCharPositionInLine());
		walker.symbolTable.put(TEMP_INSERT_TARGET_COLUMN_LIST_LOCATION_KEY, location);
	}

	@SuppressWarnings("unchecked")
	public int countResolvedInsertSourceColumns(
			Map<String, Object> sourceDefinition,
			Map<String, Object> sourceInterface) {
		if (sourceInterface == null || sourceInterface.isEmpty()) {
			return 0;
		}

		return buildInsertSourceColumnSequenceList(sourceDefinition, sourceInterface).size();
	}

	@SuppressWarnings("unchecked")
	public void emitInsertTargetSourceColumnCountMismatchFatal(Map<String, Object> insertColumns,
			Map<String, Object> sourceDefinition) {
		if (insertColumns == null || insertColumns.isEmpty()) {
			return;
		}

		Object locationObj = walker.symbolTable.remove(TEMP_INSERT_TARGET_COLUMN_LIST_LOCATION_KEY);
		if (sourceDefinition == null || sourceDefinition.isEmpty()) {
			return;
		}

		Object sourceInterfaceObj = sourceDefinition.get(MUMBLE_INTERFACE_KEY);
		if (!(sourceInterfaceObj instanceof Map<?, ?> sourceInterfaceMapObj)) {
			return;
		}

		Map<String, Object> sourceInterface = (Map<String, Object>) sourceInterfaceMapObj;
		if (sourceInterface.isEmpty()) {
			return;
		}

		int targetCount = extractInsertColumnNames(insertColumns).size();
		int sourceCount = countResolvedInsertSourceColumns(sourceDefinition, sourceInterface);
		if (targetCount == 0 || sourceCount == 0 || targetCount == sourceCount) {
			return;
		}

		Integer line = null;
		Integer charPosition = null;
		if (locationObj instanceof Map<?, ?> locationMapObj) {
			Object lineObj = locationMapObj.get("line");
			Object charObj = locationMapObj.get("char");
			if (lineObj instanceof Integer lineValue) {
				line = lineValue;
			} else if (lineObj instanceof Number lineNumber) {
				line = lineNumber.intValue();
			}
			if (charObj instanceof Integer charValue) {
				charPosition = charValue;
			} else if (charObj instanceof Number charNumber) {
				charPosition = charNumber.intValue();
			}
		}

		String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_INSERT_TARGET_SOURCE_COLUMN_COUNT_MISMATCH);
		String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_INSERT_TARGET_SOURCE_COLUMN_COUNT_MISMATCH);
		String diagMessage = (diagTemplate == null)
				? String.format(
						"Insert Mismatch: Target has %d columns, Source has %d columns, (l:%d c:%d)",
						targetCount,
						sourceCount,
						line,
						charPosition)
				: String.format(diagTemplate, targetCount, sourceCount, line, charPosition);

		walker.addWalkerFatal(diagCode, diagMessage, line, charPosition, null);
	}

	/**
	 * Thin insert wrap: map target columns from an already-resolved source scope, then apply orphan promotion.
	 */
	@SuppressWarnings("unchecked")
	public void wrapInsertTargetFromResolvedSource(
			String insertTargetTableRef,
			Map<String, Object> insertColumns) {
		String insertSourceScopeKey = consumeInsertSourceScopeKey();
		Map<String, Object> insertSourceDefinition = normalizeInsertSourceDefinition(insertSourceScopeKey);
		emitInsertTargetSourceColumnCountMismatchFatal(insertColumns, insertSourceDefinition);
		Map<String, Object> insertInterface = mapInsertTargetInterfaceFromResolvedSource(
				insertSourceDefinition,
				insertColumns,
				insertSourceScopeKey,
				insertTargetTableRef);
		if (!insertInterface.isEmpty()) {
			walker.symbolTable.put(MUMBLE_INTERFACE_KEY, insertInterface);
		}

		resolveInsertUnqualifiedOrphanSourceColumnsToTargetTable(
				insertTargetTableRef,
				insertSourceDefinition,
				insertInterface);
		clearInsertSourceColumnSequence(insertSourceDefinition);
	}

	@SuppressWarnings("unchecked")
	public void clearInsertSourceColumnSequence(Map<String, Object> sourceDefinition) {
		clearInsertSourceSequenceFromScopeTree(sourceDefinition);
	}

	@SuppressWarnings("unchecked")
	public void clearInsertSourceSequenceFromScopeTree(Map<String, Object> scopeDefinition) {
		if (scopeDefinition == null || scopeDefinition.isEmpty()) {
			return;
		}

		scopeDefinition.remove(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY);
		for (Object value : scopeDefinition.values()) {
			if (value instanceof Map<?, ?> valueMapObj) {
				clearInsertSourceSequenceFromScopeTree((Map<String, Object>) valueMapObj);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void normalizeFromClauseCteAliasMappings(Map<String, Object> queryMap) {
		if (queryMap == null || queryMap.isEmpty()) {
			return;
		}

		Object fromObj = queryMap.get(MUMBLE_FROM_KEY);
		collectFromClauseCteAliasMappingsRecursive(fromObj);
	}

	@SuppressWarnings("unchecked")
	public void collectFromClauseCteAliasMappingsRecursive(Object nodeObj) {
		if (nodeObj == null) {
			return;
		}

		if (nodeObj instanceof Map<?, ?> nodeMapObj) {
			Map<String, Object> nodeMap = (Map<String, Object>) nodeMapObj;

			Object tableObj = nodeMap.get(MUMBLE_TABLE_KEY);
			if (tableObj instanceof String) {
				String tableRef = getQualifiedTableReference(nodeMap);
				registerCteBackedSourceAliasMappings(tableRef, nodeMap.get(MUMBLE_ALIAS_KEY));
			} else if (tableObj instanceof Map<?, ?> tableMapObj) {
				collectFromClauseCteAliasMappingsRecursive(tableMapObj);
			}

			for (Object valueObj : nodeMap.values()) {
				if (valueObj instanceof Map<?, ?> || valueObj instanceof List<?>) {
					collectFromClauseCteAliasMappingsRecursive(valueObj);
				}
			}
			return;
		}

		if (nodeObj instanceof List<?> nodeListObj) {
			for (Object listItemObj : nodeListObj) {
				collectFromClauseCteAliasMappingsRecursive(listItemObj);
			}
		}
	}

	public void registerCteBackedSourceAliasMappings(String tableRef, Object aliasObj) {
		if (tableRef == null || tableRef.isBlank()) {
			return;
		}

		String cteScopeRef = resolveCteOrExistingQueryScopeInVisibleScopes(tableRef);
		if (cteScopeRef == null || cteScopeRef.isBlank()) {
			return;
		}

		upsertCurrentTableAliasMapping(tableRef, cteScopeRef);
		if (aliasObj instanceof String alias && !alias.isBlank()) {
			upsertCurrentTableAliasMapping(alias, cteScopeRef);
			upsertVisibleCteAliasMapping(alias, tableRef, cteScopeRef);
		}
	}

	public void upsertVisibleCteAliasMapping(String alias, String sourceRef, String cteScopeRef) {
		if (alias == null || alias.isBlank() || cteScopeRef == null || cteScopeRef.isBlank()) {
			return;
		}

		Map<String, Object> visibleCteList = getCteListSymbolMap(walker.symbolTable);
		if (visibleCteList == null || visibleCteList.isEmpty()) {
			for (Map<String, Object> ancestorSymbols : getAncestorSymbolTables()) {
				Map<String, Object> ancestorCteList = getCteListSymbolMap(ancestorSymbols);
				if (ancestorCteList != null && !ancestorCteList.isEmpty()) {
					visibleCteList = ancestorCteList;
					break;
				}
			}
		}

		if (visibleCteList == null || visibleCteList.isEmpty()) {
			return;
		}

		Object sourceScope = visibleCteList.get(sourceRef);
		if (!(sourceScope instanceof String sourceScopeRef) || !cteScopeRef.equals(sourceScopeRef)) {
			return;
		}

		Map<String, Object> activeCteList = ensureCteListSymbolMap();
		activeCteList.put(alias, cteScopeRef);
	}

	@SuppressWarnings("unchecked")
	public void upsertCurrentTableAliasMapping(String alias, String targetRef) {
		if (alias == null || alias.isBlank() || targetRef == null || targetRef.isBlank()) {
			return;
		}

		Object aliasObj = walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY);
		Map<String, Object> aliasMap;
		if (aliasObj instanceof Map<?, ?> aliasMapObj) {
			aliasMap = (Map<String, Object>) aliasMapObj;
		} else {
			aliasMap = new LinkedHashMap<String, Object>();
			walker.symbolTable.put(MUMBLE_TABLE_ALIAS_KEY, aliasMap);
		}

		aliasMap.put(alias, targetRef);
	}

	@SuppressWarnings("unchecked")
	public void mergeUnresolvedEntriesIntoCurrentScope(Map<String, Object> unresolvedEntries) {
		if (unresolvedEntries == null || unresolvedEntries.isEmpty()) {
			return;
		}
		HashMap<String, Object> unresolvedEntryMap = new HashMap<String, Object>(unresolvedEntries);

		Object parentUnresolvedObject = walker.symbolTable.get(MUMBLE_UNRESOLVED_COLUMN_KEY);
		if (parentUnresolvedObject instanceof HashMap<?, ?>) {
			walker.mergeUnknownEntries((HashMap<String, Object>) parentUnresolvedObject, unresolvedEntryMap);
		} else {
			walker.symbolTable.put(MUMBLE_UNRESOLVED_COLUMN_KEY, unresolvedEntryMap);
		}
	}

	public boolean shouldDeferSubqueryUnresolvedDiagnosticsToStatementBoundary(
			SQLSelectParserParser.Query_specificationContext ctx) {
		if (ctx == null) {
			return false;
		}

		ParserRuleContext cursor = ctx;
		while (cursor != null) {
			int ruleIndex = cursor.getRuleIndex();
			if (ruleIndex == SQLSelectParserParser.RULE_insert_expression
					|| ruleIndex == SQLSelectParserParser.RULE_update_expression) {
				return true;
			}
			if (ruleIndex == SQLSelectParserParser.RULE_query_value
					|| ruleIndex == SQLSelectParserParser.RULE_tuple_value
					|| ruleIndex == SQLSelectParserParser.RULE_join_extension_value) {
				return false;
			}
			cursor = cursor.getParent();
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public void projectSelectIntoTargetFromInterface(
			Map<String, Object> queryAst,
			Map<String, Object> querySymbols,
			Map<String, Object> localCurrentQueryDictionary) {
		if (queryAst == null || querySymbols == null) {
			return;
		}

		Object intoObj = queryAst.get(MUMBLE_INTO_KEY);
		if (!(intoObj instanceof Map<?, ?> intoMapObj)) {
			return;
		}
		Map<String, Object> intoMap = (Map<String, Object>) intoMapObj;
		Map<String, Object> targetTableMap = intoMap;
		Object firstIntoEntry = intoMap.get("1");
		if (firstIntoEntry instanceof Map<?, ?> firstIntoEntryMapObj) {
			targetTableMap = (Map<String, Object>) firstIntoEntryMapObj;
		}

		String targetTableRef = getQualifiedTableReference(targetTableMap);
		if (targetTableRef == null || targetTableRef.isBlank()) {
			return;
		}
		String normalizedTargetRef = normalizeTableRef(targetTableRef);

		Object interfaceObj = querySymbols.get(MUMBLE_INTERFACE_KEY);
		if (!(interfaceObj instanceof Map<?, ?> interfaceMapObj)) {
			return;
		}
		Map<String, Object> interfaceMap = (Map<String, Object>) interfaceMapObj;

		Object tableDictionaryObj = querySymbols.get(MUMBLE_TABLE_DICTIONARY_KEY);
		HashMap<String, Object> tableDictionary = (tableDictionaryObj instanceof HashMap<?, ?> tableDictionaryMapObj)
				? (HashMap<String, Object>) tableDictionaryMapObj
				: new HashMap<String, Object>();
		querySymbols.put(MUMBLE_TABLE_DICTIONARY_KEY, tableDictionary);

		HashMap<String, Object> targetColumns = ensureTableDictionaryEntry(tableDictionary, normalizedTargetRef);
		for (Map.Entry<String, Object> interfaceEntry : interfaceMap.entrySet()) {
			String targetColumn = interfaceEntry.getKey();
			if (targetColumn == null || targetColumn.isBlank()) {
				continue;
			}

			Object refsObj = localCurrentQueryDictionary == null ? null : localCurrentQueryDictionary.get(targetColumn);
			Object refs = (refsObj instanceof ArrayList<?> refsListObj)
					? new ArrayList<Object>((ArrayList<Object>) refsListObj)
					: new ArrayList<Object>();
			targetColumns.put(targetColumn, refs);
		}

		HashMap<String, Object> targetTableCollection;
		Object targetTableObj = querySymbols.get(MUMBLE_TARGET_TABLE_KEY);
		if (targetTableObj instanceof HashMap<?, ?> targetTableMapObj) {
			targetTableCollection = (HashMap<String, Object>) targetTableMapObj;
		} else {
			targetTableCollection = new HashMap<String, Object>();
			querySymbols.put(MUMBLE_TARGET_TABLE_KEY, targetTableCollection);
		}
		targetTableCollection.put(normalizedTargetRef, targetColumns);

		HashMap<String, Object> globalTableDictionary = walker.getWalkerTableDictionary();
		HashMap<String, Object> globalTargetColumns = ensureTableDictionaryEntry(globalTableDictionary, normalizedTargetRef);
		globalTargetColumns.putAll(targetColumns);
	}

	public HashMap<String, Object> partitionParentResolvableQualifiedUnknownsAndEmit(
			HashMap<String, Object> qualifiedUnresolvedMap,
			HashMap<String, Object> parentTableAliasMap,
			HashMap<String, Object> parentTableCollection,
			HashMap<String, Object> parentQueryCollection) {
		HashMap<String, Object> parentResolvable = new HashMap<String, Object>();
		HashMap<String, Object> unresolvedInParent = new HashMap<String, Object>();

		if (qualifiedUnresolvedMap == null || qualifiedUnresolvedMap.isEmpty()) {
			return parentResolvable;
		}

		for (Map.Entry<String, Object> entry : qualifiedUnresolvedMap.entrySet()) {
			String unresolvedKey = entry.getKey();
			if (walker.canResolveQualifiedUnknownInScope(
					unresolvedKey,
					parentTableAliasMap,
					parentTableCollection,
					parentQueryCollection)) {
				parentResolvable.put(unresolvedKey, entry.getValue());
			} else {
				unresolvedInParent.put(unresolvedKey, entry.getValue());
			}
		}

		if (!unresolvedInParent.isEmpty()) {
			walker.mergeUnknownEntries(parentResolvable, unresolvedInParent);
		}

		return parentResolvable;
	}

	public void emitQualifiedSourceNotFoundFatals(HashMap<String, Object> qualifiedUnresolvedMap) {
		if (qualifiedUnresolvedMap == null || qualifiedUnresolvedMap.isEmpty()) {
			return;
		}

		@SuppressWarnings("unchecked")
		HashMap<String, Object> currentTableAliasMap = (walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY) instanceof HashMap<?, ?>)
				? (HashMap<String, Object>) walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY)
				: null;
		if (currentTableAliasMap == null || currentTableAliasMap.isEmpty()) {
			currentTableAliasMap = getTopLevelQueryTableAliasMap();
		}
		HashMap<String, Object> currentTableCollection = walker.peekCurrentTableDictionary();
		if (currentTableCollection == null || currentTableCollection.isEmpty()) {
			currentTableCollection = walker.getWalkerTableDictionary();
		}

		String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE);
		String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE);
		boolean hasCteList = hasCteListSymbolMap();

		for (Map.Entry<String, Object> unresolvedEntry : qualifiedUnresolvedMap.entrySet()) {
			String unresolvedKey = unresolvedEntry.getKey();
			if (unresolvedKey == null || unresolvedKey.isBlank()) {
				continue;
			}

			int dotIndex = unresolvedKey.indexOf('.');
			if (dotIndex <= 0 || dotIndex + 1 >= unresolvedKey.length()) {
				continue;
			}

			String sourceRef = unresolvedKey.substring(0, dotIndex);
			String columnName = unresolvedKey.substring(dotIndex + 1);
			String cteScopeRef = resolveCteScopeReference(sourceRef, currentTableAliasMap);

			// If WITH/CTE context is active and sourceRef maps to a CTE entry, CTE interface resolution is authoritative.
			if (hasCteList && cteScopeRef != null && !cteScopeRef.isBlank()) {
				boolean resolvedInCte = "*".equals(columnName)
						|| hasColumnInQueryOutputInterface(cteScopeRef, columnName)
						|| hasWildcardInQueryOutputInterface(cteScopeRef);
				if (resolvedInCte) {
					String resolvedSourceRef = walker.resolveAliasToTableName(sourceRef, currentTableAliasMap);
					if (resolvedSourceRef == null || resolvedSourceRef.isBlank()) {
						resolvedSourceRef = sourceRef;
					}

					HashMap<String, Object> indicatedTableDictionary = walker.getTableDictionaryForReference(
							resolvedSourceRef,
							currentTableCollection);
					if (indicatedTableDictionary == null && !sourceRef.equals(resolvedSourceRef)) {
						indicatedTableDictionary = walker.getTableDictionaryForReference(sourceRef, currentTableCollection);
					}
					if (indicatedTableDictionary != null) {
						walker.mergeResolvedColumnIntoDictionary(indicatedTableDictionary, columnName, unresolvedEntry.getValue());
					}
					continue;
				}

				Object unresolvedValue = unresolvedEntry.getValue();
				Object capturedValue = walker.getCapturedQualifiedUnresolvedLocationEntry(unresolvedKey);
				Object diagnosticValue = (capturedValue != null) ? capturedValue : unresolvedValue;

				Integer[] refLocation = walker.getLineAndCharacterFromEntry(diagnosticValue);
				if (refLocation[0] == null || refLocation[1] == null) {
					refLocation = walker.getFirstEntryLineAndCharacter(qualifiedUnresolvedMap);
				}

				String allLocationsInline = walker.formatAllLocationsForEntryInline(diagnosticValue);
				boolean hasMergedLocations = allLocationsInline != null && allLocationsInline.contains(",");

				String diagMessage;
				if (hasMergedLocations) {
					diagMessage = String.format(
							"Source Table not found for Column '%s' at %s. No alias or table called '%s'.",
							columnName,
							allLocationsInline,
							sourceRef);
				} else {
					diagMessage = String.format(
							diagTemplate,
							columnName,
							refLocation[0],
							refLocation[1],
							sourceRef);
				}

				walker.addWalkerFatal(
						diagCode,
						diagMessage,
						refLocation[0],
						refLocation[1],
						columnName);
				continue;
			}

			if (walker.canResolveQualifiedUnknownInScope(
					unresolvedKey,
					currentTableAliasMap,
					currentTableCollection,
					walker.queryColumnDictionaryMap)) {
				String resolvedSourceRef = walker.resolveAliasToTableName(sourceRef, currentTableAliasMap);
				HashMap<String, Object> indicatedTableDictionary = walker.getTableDictionaryForReference(
						resolvedSourceRef,
						currentTableCollection);
				if (indicatedTableDictionary != null) {
					walker.mergeResolvedColumnIntoDictionary(indicatedTableDictionary, columnName, unresolvedEntry.getValue());
				}
				continue;
			}

			Object unresolvedValue = unresolvedEntry.getValue();
			Object capturedValue = walker.getCapturedQualifiedUnresolvedLocationEntry(unresolvedKey);
			Object diagnosticValue = (capturedValue != null) ? capturedValue : unresolvedValue;

			Integer[] refLocation = walker.getLineAndCharacterFromEntry(diagnosticValue);
			if (refLocation[0] == null || refLocation[1] == null) {
				refLocation = walker.getFirstEntryLineAndCharacter(qualifiedUnresolvedMap);
			}

			String allLocationsInline = walker.formatAllLocationsForEntryInline(diagnosticValue);
			boolean hasMergedLocations = allLocationsInline != null && allLocationsInline.contains(",");

			String diagMessage;
			if (hasMergedLocations) {
				diagMessage = String.format(
						"Source Table not found for Column '%s' at %s. No alias or table called '%s'.",
						columnName,
						allLocationsInline,
						sourceRef);
			} else {
				diagMessage = String.format(
						diagTemplate,
						columnName,
						refLocation[0],
						refLocation[1],
						sourceRef);
			}

			walker.addWalkerFatal(
					diagCode,
					diagMessage,
					refLocation[0],
					refLocation[1],
					columnName);
		}
	}

	@SuppressWarnings("unchecked")
	public boolean hasCteListSymbolMap() {
		Map<String, Object> currentCteList = getCteListSymbolMap(walker.symbolTable);
		if (currentCteList != null && !currentCteList.isEmpty()) {
			return true;
		}

		for (Map<String, Object> ancestorSymbols : getAncestorSymbolTables()) {
			Map<String, Object> ancestorCteList = getCteListSymbolMap(ancestorSymbols);
			if (ancestorCteList != null && !ancestorCteList.isEmpty()) {
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public String resolveCteScopeReference(String sourceRef, HashMap<String, Object> tableAliasMap) {
		if (sourceRef == null || sourceRef.isBlank()) {
			return null;
		}

		String localScope = resolveCteScopeReferenceInSymbols(sourceRef, tableAliasMap, walker.symbolTable);
		if (localScope != null) {
			return localScope;
		}

		for (Map<String, Object> ancestorSymbols : getAncestorSymbolTables()) {
			HashMap<String, Object> ancestorAliasMap = getTableAliasMap(ancestorSymbols);

			if (tableAliasMap != null && !tableAliasMap.isEmpty()) {
				String inheritedScope = resolveCteScopeReferenceInSymbols(sourceRef, tableAliasMap, ancestorSymbols);
				if (inheritedScope != null) {
					return inheritedScope;
				}
			}

			String inheritedScope = resolveCteScopeReferenceInSymbols(sourceRef, ancestorAliasMap, ancestorSymbols);
			if (inheritedScope != null) {
				return inheritedScope;
			}
		}

		return null;
	}

	public void emitShadowedParentCteNameWarningIfNeeded(
			String alias,
			Map<String, Object> localCteList,
			ParserRuleContext ctx) {
		if (alias == null || alias.isBlank()) {
			return;
		}

		HashMap<String, Object> tableAliasMap = getTableAliasMap(walker.symbolTable);
		String inheritedScopeRef = null;
		if (tableAliasMap != null) {
			Object existingAliasTargetObj = tableAliasMap.get(alias);
			if (existingAliasTargetObj instanceof String existingAliasTarget
					&& !existingAliasTarget.isBlank()
					&& (existingAliasTarget.startsWith(MUMBLE_QUERY_KEY)
							|| existingAliasTarget.startsWith(MUMBLE_UNION_KEY)
							|| existingAliasTarget.startsWith(MUMBLE_INTERSECT_KEY)
							|| existingAliasTarget.startsWith(MUMBLE_VALUES_KEY)
							|| MUMBLE_VALUES_KEY.equals(existingAliasTarget))) {
				inheritedScopeRef = existingAliasTarget;
			}
		}

		if (inheritedScopeRef == null) {
			inheritedScopeRef = resolveCteScopeReference(alias, tableAliasMap);
		}

		if (inheritedScopeRef == null || inheritedScopeRef.isBlank()) {
			return;
		}

		Integer line = null;
		Integer charPosition = null;
		if (ctx != null && ctx.getStart() != null) {
			line = ctx.getStart().getLine();
			charPosition = ctx.getStart().getCharPositionInLine();
		}

		String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_SHADOWED_PARENT_CTE_NAME);
		String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_SHADOWED_PARENT_CTE_NAME);
		String diagMessage = (diagTemplate == null)
				? String.format(
						"CTE '%s' at (l:%s c:%s) shadows inherited CTE '%s' (%s).",
						alias,
						line,
						charPosition,
						alias,
						inheritedScopeRef)
				: String.format(diagTemplate,
						alias,
						line,
						charPosition,
						alias,
						inheritedScopeRef);

		walker.addWalkerDiagnostic(
				ParseDiagnostic.Severity.WARNING,
				diagCode,
				diagMessage,
				line,
				charPosition,
				walker.getClass().getSimpleName(),
				null,
				alias,
				true,
				"ast-walk",
				null,
				null);
	}

	@SuppressWarnings("unchecked")
	public void pushSymbolTableWithParentCteList() {
		Map<String, Object> inheritedCteList = getCteListSymbolMap(walker.symbolTable);
		if (inheritedCteList == null || inheritedCteList.isEmpty()) {
			for (Map<String, Object> ancestorSymbols : getAncestorSymbolTables()) {
				Map<String, Object> ancestorCteList = getCteListSymbolMap(ancestorSymbols);
				if (ancestorCteList != null && !ancestorCteList.isEmpty()) {
					inheritedCteList = ancestorCteList;
					break;
				}
			}
		}
		walker.pushSymbolTable();
		if (inheritedCteList != null && !inheritedCteList.isEmpty()) {
			walker.symbolTable.put(MUMBLE_CTE_LIST_KEY, new LinkedHashMap<String, Object>(inheritedCteList));
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getParentSymbolTable() {
		Integer parentLevel = walker.currentStackLevel("symbolTable");
		if (parentLevel == null || walker.asTree == null) {
			return null;
		}

		Object parentSymbolsObj = walker.asTree.get("symbolTable_" + parentLevel);
		if (parentSymbolsObj instanceof Map<?, ?> parentSymbolsMapObj) {
			return (Map<String, Object>) parentSymbolsMapObj;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getCteListSymbolMap(Map<String, Object> symbols) {
		if (symbols == null) {
			return null;
		}

		Object cteListObj = symbols.get(MUMBLE_CTE_LIST_KEY);
		if (cteListObj instanceof Map<?, ?> cteListMapObj) {
			return (Map<String, Object>) cteListMapObj;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getTableAliasMap(Map<String, Object> symbols) {
		if (symbols == null) {
			return null;
		}

		Object tableAliasObj = symbols.get(MUMBLE_TABLE_ALIAS_KEY);
		if (tableAliasObj instanceof HashMap<?, ?> aliasMapObj) {
			return (HashMap<String, Object>) aliasMapObj;
		}
		if (tableAliasObj instanceof Map<?, ?> aliasMapObj) {
			return new HashMap<String, Object>((Map<String, Object>) aliasMapObj);
		}
		return null;
	}

	public String resolveCteScopeReferenceInSymbols(
			String sourceRef,
			HashMap<String, Object> tableAliasMap,
			Map<String, Object> symbols) {
		Map<String, Object> cteListMap = getCteListSymbolMap(symbols);
		if (cteListMap == null || cteListMap.isEmpty() || sourceRef == null || sourceRef.isBlank()) {
			return null;
		}

		Object directScope = cteListMap.get(sourceRef);
		if (directScope instanceof String directScopeRef && !directScopeRef.isBlank()) {
			return directScopeRef;
		}

		if (tableAliasMap != null) {
			Object mappedSourceObject = tableAliasMap.get(sourceRef);
			if (mappedSourceObject instanceof String mappedSource && !mappedSource.isBlank()) {
				Object mappedScope = cteListMap.get(mappedSource);
				if (mappedScope instanceof String mappedScopeRef && !mappedScopeRef.isBlank()) {
					return mappedScopeRef;
				}

				for (Object cteScopeValue : cteListMap.values()) {
					if (cteScopeValue instanceof String cteScopeRef && cteScopeRef.equals(mappedSource)) {
						return cteScopeRef;
					}
				}
			}
		}

		for (Object cteScopeValue : cteListMap.values()) {
			if (cteScopeValue instanceof String cteScopeRef && cteScopeRef.equals(sourceRef)) {
				return cteScopeRef;
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getTopLevelQueryTableAliasMap() {
		if (walker.symbolTable == null || walker.symbolTable.isEmpty()) {
			return null;
		}

		HashMap<String, Object> selectedAliasMap = null;
		int highestQueryIndex = -1;

		for (Map.Entry<String, Object> symbolEntry : walker.symbolTable.entrySet()) {
			String symbolKey = symbolEntry.getKey();
			if (symbolKey == null || !symbolKey.startsWith(MUMBLE_QUERY_KEY)) {
				continue;
			}

			Object symbolValue = symbolEntry.getValue();
			if (!(symbolValue instanceof Map<?, ?> queryScope)) {
				continue;
			}

			String suffix = symbolKey.substring(MUMBLE_QUERY_KEY.length());
			int queryIndex;
			try {
				queryIndex = Integer.parseInt(suffix);
			} catch (NumberFormatException ex) {
				continue;
			}

			Object aliasMapObject = ((Map<String, Object>) queryScope).get(MUMBLE_TABLE_ALIAS_KEY);
			if (!(aliasMapObject instanceof HashMap<?, ?>)) {
				continue;
			}

			if (queryIndex > highestQueryIndex) {
				highestQueryIndex = queryIndex;
				selectedAliasMap = (HashMap<String, Object>) aliasMapObject;
			}
		}

		return selectedAliasMap;
	}

	@SuppressWarnings("unchecked")
	public void emitQualifiedQueryAliasUnresolvedColumnsFatalAndPrune(
			HashMap<String, Object> qualifiedUnresolvedMap,
			HashMap<String, Object> tableAliasMap) {
		if (qualifiedUnresolvedMap == null || qualifiedUnresolvedMap.isEmpty()
				|| tableAliasMap == null || tableAliasMap.isEmpty()) {
			return;
		}

		ArrayList<String> keysToRemove = new ArrayList<String>();
		for (Map.Entry<String, Object> unresolvedEntry : qualifiedUnresolvedMap.entrySet()) {
			String unresolvedKey = unresolvedEntry.getKey();
			if (unresolvedKey == null) {
				continue;
			}

			int dotIndex = unresolvedKey.indexOf('.');
			if (dotIndex <= 0 || dotIndex + 1 >= unresolvedKey.length()) {
				continue;
			}

			String tableRef = unresolvedKey.substring(0, dotIndex);
			String columnName = unresolvedKey.substring(dotIndex + 1);
			Object aliasTargetObj = tableAliasMap.get(tableRef);
			if (!(aliasTargetObj instanceof String aliasTarget)) {
				continue;
			}

			boolean aliasTargetsQuery = aliasTarget.startsWith(MUMBLE_QUERY_KEY)
					|| aliasTarget.startsWith(MUMBLE_UNION_KEY)
					|| aliasTarget.startsWith(MUMBLE_INTERSECT_KEY)
					|| aliasTarget.startsWith(MUMBLE_VALUES_KEY)
					|| MUMBLE_VALUES_KEY.equals(aliasTarget);
			if (!aliasTargetsQuery) {
				continue;
			}

			if ("*".equals(columnName)) {
				promoteQualifiedWildcardIntoQuerySource(aliasTarget, unresolvedEntry.getValue());
				continue;
			}

			Object queryDictionaryObj = walker.queryColumnDictionaryMap.get(aliasTarget);
			boolean foundInQueryInterface = queryDictionaryObj instanceof Map<?, ?> queryDictionary
					&& containsKeyIgnoreCase((Map<String, Object>) queryDictionary, columnName);
			if (!foundInQueryInterface && hasColumnInQueryOutputInterface(aliasTarget, columnName)) {
				foundInQueryInterface = true;
			}
			if (!foundInQueryInterface && hasWildcardInQueryOutputInterface(aliasTarget)) {
				foundInQueryInterface = true;
			}
			if (foundInQueryInterface) {
				continue;
			}

			Object unresolvedValue = unresolvedEntry.getValue();
			Integer[] refLocation = walker.getLineAndCharacterFromEntry(unresolvedValue);
			if (refLocation[0] == null || refLocation[1] == null) {
				refLocation = walker.getFirstEntryLineAndCharacter(qualifiedUnresolvedMap);
			}

			String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS);
			String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS);
			String diagMessage = (diagTemplate == null)
					? String.format(
							"Qualified column '%s' at (l:%s c:%s) was not found in output interface of query alias '%s'.",
							columnName,
							refLocation[0],
							refLocation[1],
							tableRef)
					: String.format(diagTemplate,
							columnName,
							refLocation[0],
							refLocation[1],
							tableRef);

			walker.addWalkerFatal(
					diagCode,
					diagMessage,
					refLocation[0],
					refLocation[1],
					unresolvedKey);

			keysToRemove.add(unresolvedKey);
		}

		for (String keyToRemove : keysToRemove) {
			qualifiedUnresolvedMap.remove(keyToRemove);
		}
	}

	@SuppressWarnings("unchecked")
	public boolean canResolveUnqualifiedFromSingleWildcardQuerySource(HashMap<String, Object> unqualifiedUnresolvedMap) {
		if (unqualifiedUnresolvedMap == null || unqualifiedUnresolvedMap.isEmpty()) {
			return false;
		}

		Object tableAliasObject = walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY);
		if (!(tableAliasObject instanceof Map<?, ?> tableAliasMap) || tableAliasMap.isEmpty()) {
			return false;
		}

		HashMap<String, Object> localTableAliasMap = (HashMap<String, Object>) tableAliasMap;
		HashMap<String, Object> localTableCollection = (HashMap<String, Object>) walker.symbolTable.get(MUMBLE_TABLE_DICTIONARY_KEY);
		if (localTableCollection == null) {
			localTableCollection = new HashMap<String, Object>();
		}
		HashMap<String, Object> visibleQuerySourceCollection = collectVisibleQuerySourceCollection(localTableAliasMap);
		if (hasUnqualifiedUnknownWithMultipleViableSources(
				unqualifiedUnresolvedMap,
				localTableCollection,
				visibleQuerySourceCollection,
				localTableAliasMap)) {
			return false;
		}

		HashSet<String> queryBackedSources = new HashSet<String>();
		for (Object mappedSourceObj : localTableAliasMap.values()) {
			if (!(mappedSourceObj instanceof String mappedSource)) {
				continue;
			}
			if (mappedSource.startsWith(MUMBLE_QUERY_KEY)
					|| mappedSource.startsWith(MUMBLE_UNION_KEY)
					|| mappedSource.startsWith(MUMBLE_INTERSECT_KEY)
					|| mappedSource.startsWith(MUMBLE_VALUES_KEY)
					|| MUMBLE_VALUES_KEY.equals(mappedSource)) {
				queryBackedSources.add(mappedSource);
			}
		}

		if (queryBackedSources.size() != 1) {
			return false;
		}

		String sourceQueryKey = queryBackedSources.iterator().next();
		Object queryDictionaryObj = walker.queryColumnDictionaryMap.get(sourceQueryKey);
		if (queryDictionaryObj instanceof Map<?, ?> queryDictionary) {
			Map<String, Object> sourceQueryDictionary = (Map<String, Object>) queryDictionary;
			if (sourceQueryDictionary.containsKey("*")) {
				unqualifiedUnresolvedMap.clear();
				return true;
			}

			ArrayList<String> resolvedKeys = new ArrayList<String>();
			for (String unresolvedKey : unqualifiedUnresolvedMap.keySet()) {
				if (sourceQueryDictionary.containsKey(unresolvedKey)
						|| hasColumnInQueryOutputInterface(sourceQueryKey, unresolvedKey)) {
					resolvedKeys.add(unresolvedKey);
				}
			}

			for (String resolvedKey : resolvedKeys) {
				unqualifiedUnresolvedMap.remove(resolvedKey);
			}
			if (unqualifiedUnresolvedMap.isEmpty()) {
				return true;
			}
		}

		ArrayList<String> interfaceResolvedKeys = new ArrayList<String>();
		for (String unresolvedKey : unqualifiedUnresolvedMap.keySet()) {
			if (hasColumnInQueryOutputInterface(sourceQueryKey, unresolvedKey)) {
				interfaceResolvedKeys.add(unresolvedKey);
			}
		}
		for (String resolvedKey : interfaceResolvedKeys) {
			unqualifiedUnresolvedMap.remove(resolvedKey);
		}
		if (unqualifiedUnresolvedMap.isEmpty()) {
			return true;
		}

		if (hasWildcardInQueryOutputInterface(sourceQueryKey)) {
			unqualifiedUnresolvedMap.clear();
			return true;
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean hasWildcardInQueryOutputInterface(String queryKey) {
		if (queryKey == null || queryKey.isBlank()) {
			return false;
		}

		Object queryDefObj = getQueryDefinitionSymbol(queryKey);
		if (!(queryDefObj instanceof Map<?, ?> queryDefMap)) {
			return false;
		}

		Object interfaceObj = ((Map<String, Object>) queryDefMap).get(MUMBLE_INTERFACE_KEY);
		if (!(interfaceObj instanceof Map<?, ?> interfaceMap)) {
			return false;
		}

		return ((Map<String, Object>) interfaceMap).containsKey("*");
	}

	public boolean isQueryBackedSelectItemReference(Map<String, Object> interfaceReference) {
		if (interfaceReference == null || interfaceReference.isEmpty()) {
			return false;
		}

		return interfaceReference.containsKey(MUMBLE_SELECT_KEY)
				|| interfaceReference.containsKey(MUMBLE_LOOKUP_KEY)
				|| interfaceReference.containsKey(MUMBLE_UNION_KEY)
				|| interfaceReference.containsKey(MUMBLE_INTERSECT_KEY)
				|| interfaceReference.containsKey(MUMBLE_VALUES_KEY);
	}

	@SuppressWarnings("unchecked")
	public void recordInsertSourceSelectItemSequence(String interfaceAlias) {
		if (interfaceAlias == null || walker.currentStackLevel(SQLSelectParserParser.RULE_insert_source_primary) == null) {
			return;
		}

		Object sequenceObject = walker.symbolTable.get(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY);
		ArrayList<String> sequence;
		if (sequenceObject instanceof ArrayList<?>) {
			sequence = (ArrayList<String>) sequenceObject;
		} else {
			sequence = new ArrayList<String>();
			walker.symbolTable.put(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY, sequence);
		}

		sequence.add(interfaceAlias);
	}

	public void addAliasTokensObject(String interfaceAlias, String aliasToken) {
		String queryDictionaryKey = MUMBLE_QUERY_DICTIONARY_KEY;
		HashMap<String, Object> queryColumnDictionary = (HashMap<String, Object>) walker.symbolTable.get(queryDictionaryKey);
		if (queryColumnDictionary == null) {
			queryColumnDictionary = new HashMap<String, Object>();
			walker.symbolTable.put(queryDictionaryKey, queryColumnDictionary);
		}

		Object aliasTokensObject = queryColumnDictionary.get(interfaceAlias);
		if (aliasTokensObject == null) {
			aliasTokensObject = new ArrayList<String>();
			queryColumnDictionary.put(interfaceAlias, aliasTokensObject);
		} 
		
		((ArrayList<String>) aliasTokensObject).add(aliasToken);
	}

	@SuppressWarnings("unchecked")
	public void emitDuplicateInterfaceColumnFatal(
			String interfaceAlias,
			Object existingInterfaceEntry,
			ArrayList<Object> incomingInterfaceEntry,
			String incomingAliasToken) {
		String existingQualifiedRef = buildInterfaceReferenceLabel(existingInterfaceEntry, interfaceAlias);
		String incomingQualifiedRef = buildInterfaceReferenceLabel(incomingInterfaceEntry, interfaceAlias);

		HashMap<String, Object> queryDictionary = (HashMap<String, Object>) walker.symbolTable.get(MUMBLE_QUERY_DICTIONARY_KEY);
		Object existingTokens = (queryDictionary == null) ? null : queryDictionary.get(interfaceAlias);

		Integer[] existingLocation = walker.getLineAndCharacterFromEntry(existingTokens);
		if (existingLocation[0] == null || existingLocation[1] == null) {
			existingLocation = walker.getLineAndCharacterFromEntry(existingInterfaceEntry);
		}

		Integer[] incomingLocation = walker.getLineAndCharacterFromEntry(incomingAliasToken);
		if (incomingLocation[0] == null || incomingLocation[1] == null) {
			incomingLocation = walker.getLineAndCharacterFromEntry(incomingInterfaceEntry);
		}

		String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_DUPLICATE_INTERFACE_COLUMNS);
		String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_DUPLICATE_INTERFACE_COLUMNS);
		String diagMessage = (diagTemplate == null)
				? String.format(
						"Duplicate interface columns defined: %s at (l:%s c:%s) and %s at (l:%s c:%s).",
						existingQualifiedRef,
						existingLocation[0],
						existingLocation[1],
						incomingQualifiedRef,
						incomingLocation[0],
						incomingLocation[1])
				: String.format(
						diagTemplate,
						existingQualifiedRef,
						existingLocation[0],
						existingLocation[1],
						incomingQualifiedRef,
						incomingLocation[0],
						incomingLocation[1]);

		walker.addWalkerFatal(
				diagCode,
				diagMessage,
				existingLocation[0],
				existingLocation[1],
				existingQualifiedRef + "," + incomingQualifiedRef);
	}

	public String buildInterfaceReferenceLabel(Object interfaceEntry, String fallbackColumnName) {
		String columnName = fallbackColumnName;
		String tableRef = null;

		if (interfaceEntry instanceof ArrayList<?> refs && !refs.isEmpty()) {
			Object firstRef = refs.get(0);
			String extractedName = walker.extractReferenceNameFromInterfaceEntry(firstRef);
			String extractedTableRef = walker.extractReferenceTableRefFromInterfaceEntry(firstRef);
			if (extractedName != null && !extractedName.isBlank()) {
				columnName = extractedName;
			}
			if (extractedTableRef != null && !extractedTableRef.isBlank() && !"*".equals(extractedTableRef)) {
				tableRef = extractedTableRef;
			}
		}

		if (tableRef != null) {
			return tableRef + "." + columnName;
		}
		return columnName;
	}

	public void addCurrentQueryScalarSubqueryAlias(String interfaceAlias) {
		if (interfaceAlias == null) {
			return;
		}

		Object scalarAliasObject = walker.symbolTable.get(MUMBLE_SCALAR_SUBQUERY_ALIASES_KEY);
		HashSet<String> scalarAliases;
		if (scalarAliasObject instanceof HashSet<?>) {
			scalarAliases = (HashSet<String>) scalarAliasObject;
		} else {
			scalarAliases = new HashSet<String>();
			walker.symbolTable.put(MUMBLE_SCALAR_SUBQUERY_ALIASES_KEY, scalarAliases);
		}

		scalarAliases.add(interfaceAlias);
	}

	@SuppressWarnings("unchecked")
	public void flattenSubTreeForInterfaceQueryReferences(HashMap<String, Object> subTree, ArrayList<Object> references) {
		if (subTree == null) {
			return;
		}

		if (isQueryBackedSelectItemReference(subTree)) {
			String queryReference = resolveQueryReferenceFromSubTree(subTree);
			if (isQuerySourceReference(queryReference)) {
				HashMap<String, Object> queryEntry = new HashMap<String, Object>();
				queryEntry.put(MUMBLE_QUERY_KEY, queryReference);
				if (!references.contains(queryEntry)) {
					references.add(queryEntry);
				}
			}
			subTree.remove(MUMBLE_QUERY_KEY);
			return;
		}

		for (Object value : subTree.values()) {
			if (value instanceof HashMap<?, ?> valueMapObj) {
				flattenSubTreeForInterfaceQueryReferences((HashMap<String, Object>) valueMapObj, references);
			} else if (value instanceof ArrayList<?> valueListObj) {
				for (Object listItem : (ArrayList<Object>) valueListObj) {
					if (listItem instanceof HashMap<?, ?> listMapObj) {
						flattenSubTreeForInterfaceQueryReferences((HashMap<String, Object>) listMapObj, references);
					}
				}
			}
		}
	}

	public void annotateSubqueryReference(Map<String, Object> subTree, String queryReference) {
		if (subTree == null || subTree.isEmpty() || !isQuerySourceReference(queryReference)) {
			return;
		}
		subTree.put(MUMBLE_QUERY_KEY, queryReference);
	}

	@SuppressWarnings("unchecked")
	public String resolveQueryReferenceFromSubTree(Map<String, Object> subTree) {
		if (subTree == null || subTree.isEmpty()) {
			return null;
		}

		Object queryObject = subTree.get(MUMBLE_QUERY_KEY);
		if (queryObject instanceof String queryReference && isQuerySourceReference(queryReference)) {
			return queryReference;
		}

		for (Object value : subTree.values()) {
			if (value instanceof Map<?, ?> valueMapObj) {
				String nestedReference = resolveQueryReferenceFromSubTree((Map<String, Object>) valueMapObj);
				if (isQuerySourceReference(nestedReference)) {
					return nestedReference;
				}
			} else if (value instanceof ArrayList<?> valueListObj) {
				for (Object listItem : (ArrayList<Object>) valueListObj) {
					if (listItem instanceof Map<?, ?> listMapObj) {
						String nestedReference = resolveQueryReferenceFromSubTree((Map<String, Object>) listMapObj);
						if (isQuerySourceReference(nestedReference)) {
							return nestedReference;
						}
					}
				}
			}
		}

		return null;
	}

	// Standardize the interface reference map into a flat map of column references and not the entire AST subtree
	// This is a recursive function that traverses the item subtree until it finds column references or substitution variables, 
	// which it adds to the column list with the alias as the key

	public void flattenSubTreeForInterfaceColumns(HashMap<String, Object> subTree, ArrayList<Object> columnList) {
		if (subTree == null) {
			return;
		}

		if (subTree.containsKey(MUMBLE_COLUMN_KEY)) {
			Object col = subTree.get(MUMBLE_COLUMN_KEY);
			if (!columnList.contains(col)) {
				columnList.add(col);
			}
		} else if (subTree.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
			Object subst = subTree.get(MUMBLE_SUBSTITUTION_KEY);
			if (subst instanceof HashMap) {
				HashMap<String, Object> substMap = (HashMap<String, Object>) subst;
				Object type = substMap.get("type");
				if (type != null && (MUMBLE_COLUMN_KEY.equals(type) || MUMBLE_PREDICAND_KEY.equals(type))) {
					if (!columnList.contains(subst)) {
						columnList.add(subst);
					}
				}
			}
		} else {
			for (Object value : subTree.values()) {
				if (value instanceof HashMap<?, ?> valueMapObj) {
					flattenSubTreeForInterfaceColumns((HashMap<String, Object>) valueMapObj, columnList);
				} else if (value instanceof ArrayList<?> valueListObj) {
					for (Object listItem : (ArrayList<Object>) valueListObj) {
						if (listItem instanceof HashMap<?, ?> listMapObj) {
							flattenSubTreeForInterfaceColumns((HashMap<String, Object>) listMapObj, columnList);
						}
					}
				}
			}
		}
	}

	/**
	 * Create Dictionary from Symbol Table
	 * Validate and assign all columns to a specific source table or query
	 * Perform quality diagnostics for any unresolved columns, and if emitFinalUnresolvedUnknownFatal is true,
	 * then add fatal diagnostics to parser resultfor any remaining unresolved columns after this process
	 * 
	 * @return
	 */
	public HashMap<String, Object> convertSymbolTableToTableDictionary(
			boolean emitFinalUnresolvedUnknownFatal,
			boolean deferCorrelatedValueSubqueryQualifiedUnknowns,
			String updateTargetTableRef) {
	
		// deconstruct current symbol table into components for analysis
 		HashMap<String, Object> localInterface = (HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_INTERFACE_KEY);
		HashMap<String, Object> localLhsUnresolvedColumnMap = (HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_LHS_UNRESOLVED_COLUMNS_KEY);
		if (localLhsUnresolvedColumnMap == null)
			localLhsUnresolvedColumnMap = new HashMap<String, Object>();
		walker.captureQualifiedUnresolvedLocations(localLhsUnresolvedColumnMap);
		HashMap<String, Object> localUnresolvedColumnMap = (HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
		if (localUnresolvedColumnMap == null)
			localUnresolvedColumnMap = new HashMap<String, Object>();
		walker.captureQualifiedUnresolvedLocations(localUnresolvedColumnMap);
		HashMap<String, Object> localTableAliasMap = (HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_TABLE_ALIAS_KEY);
		if (localTableAliasMap == null)
			localTableAliasMap = new HashMap<String, Object>();
		HashMap<String, Object> localTableCollection = (HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_TABLE_DICTIONARY_KEY);
		if (localTableCollection == null)
			localTableCollection = new HashMap<String, Object>();
		HashMap<String, Object> localTargetTableCollection = (HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_TARGET_TABLE_KEY);
		if (localTargetTableCollection == null)
			localTargetTableCollection = new HashMap<String, Object>();
		HashMap<String, Object> localCurrentQueryDictionary = (HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_QUERY_DICTIONARY_KEY);
		if (localCurrentQueryDictionary == null)
			localCurrentQueryDictionary = new HashMap<String, Object>();
		String deleteTargetTableRef = (String) walker.symbolTable.remove(TEMP_DELETE_TARGET_TABLE_REF_KEY);

		// Leave these null if they don't exist
		HashSet<String> localScalarSubqueryAliases = (HashSet<String>) walker.symbolTable.remove(MUMBLE_SCALAR_SUBQUERY_ALIASES_KEY);
        Object  filtersList = walker.symbolTable.remove(MUMBLE_FILTERS_KEY);
		Object groupedByList = walker.symbolTable.remove(MUMBLE_GROUPED_BY_KEY);
		Object orderedByList = walker.symbolTable.remove(MUMBLE_ORDERED_BY_KEY);


		// Add query/derived-source aliases into the alias collection for downstream source resolution.
		walker.mergeNonTableAliasMappingsIntoAliasCollection(localCurrentQueryDictionary, localTableAliasMap);

		// Resolve alias-backed table refs so tableCollection keys align with canonical table references.
		walker.reconcileAliasBackedTableReferences(localTableCollection, localTableAliasMap);
		if (updateTargetTableRef != null && !updateTargetTableRef.isBlank()) {
			pruneUpdateTargetFromInputTableCollection(localTableCollection, updateTargetTableRef, localTableAliasMap);
		}

		if (updateTargetTableRef != null && !updateTargetTableRef.isBlank()) {
			resolveUpdateLhsColumnsToTargetTable(
					localLhsUnresolvedColumnMap,
					localUnresolvedColumnMap,
					localTableAliasMap,
					localTargetTableCollection,
					updateTargetTableRef);
			resolveUpdateQualifiedUnresolvedColumnsToInputTables(
					localUnresolvedColumnMap,
					localTableAliasMap,
					localTableCollection);
			resolveUpdateUnqualifiedUnresolvedColumnsToTargetTableWhenNoInputSources(
					localUnresolvedColumnMap,
					localTableCollection,
					localTargetTableCollection,
					localTableAliasMap,
					updateTargetTableRef);
		}

		// // Add scalar subquery aliases into the alias collection for downstream source resolution, which allows scalar subqueries to be resolved as sources for columns in the query interface.
		// walker.mergeScalarSubqueryAliasesIntoAliasCollection(localScalarSubqueryAliases, localTableAliasMap);
			
		if (localScalarSubqueryAliases != null && !localScalarSubqueryAliases.isEmpty()) {
			HashMap<String, Object> qualifiedUnresolvedColumns = new HashMap<String, Object>();
			HashMap<String, Object> unqualifiedUnresolvedColumns = new HashMap<String, Object>();
			// Split unresolved column map into sets: ones with explicit table qualifiers, and ones without.  This allows us to apply different resolution strategies to each set, and also allows us to provide more accurate diagnostics for explicitly qualified columns that cannot be resolved to a source.
			walker.splitExplicitlyQualifiedUnknownEntriesFromUnqualified(localUnresolvedColumnMap, localInterface, 
				filtersList, qualifiedUnresolvedColumns, unqualifiedUnresolvedColumns);
		} // temp location

		// Expand wildcard unknown entries (for example *, alias.*) into concrete source-scoped unknowns.
		walker.processWildcardUnknownEntries(
				localUnresolvedColumnMap,
				localInterface,
				localTableAliasMap,
				localTableCollection,
				localCurrentQueryDictionary);

		walker.moveUnknownEntriesToSingleWildcardBackedNonTableSource(
				localUnresolvedColumnMap,
				localCurrentQueryDictionary,
				localTableAliasMap);

		HashMap<String, Object> currentTableDictionary = walker.getCurrentTableDictionary();
		propagateUnqualifiedSelectStarToScopeTables(
				localInterface,
				localCurrentQueryDictionary,
				localTableCollection);
		HashMap<String, Object> visibleQuerySourceCollection = collectVisibleQuerySourceCollection(localTableAliasMap);

		if (updateTargetTableRef != null && !updateTargetTableRef.isBlank()) {
			resolveUpdateRhsUnqualifiedAssignmentColumnsToTargetTable(
					localUnresolvedColumnMap,
					localTableCollection,
					localTargetTableCollection,
					localTableAliasMap,
					visibleQuerySourceCollection,
					updateTargetTableRef);
		}

		if (!localUnresolvedColumnMap.isEmpty()) {
			// Check for explicitly qualified columns whose table qualifiers do not exist
			HashMap<String, Object> explicitQualifiedUnknownEntries = extractExplicitQualifiedUnknownEntries(
					localUnresolvedColumnMap,
					localInterface,
					filtersList,
					groupedByList,
					orderedByList);
			if (deferCorrelatedValueSubqueryQualifiedUnknowns) {
				explicitQualifiedUnknownEntries = retainOnlyLocallyResolvableExplicitQualifiedUnknowns(
						explicitQualifiedUnknownEntries,
						localInterface,
						filtersList,
						localTableAliasMap,
						localTableCollection,
						localCurrentQueryDictionary,
						localUnresolvedColumnMap);
			}
			emitExplicitQualifiedUnknownDiagnostics(
					explicitQualifiedUnknownEntries,
					localInterface,
					filtersList,
					localTableAliasMap,
					localTableCollection,
					visibleQuerySourceCollection,
					localCurrentQueryDictionary,
					localUnresolvedColumnMap);

			// Only relocate unresolved unqualified columns to a single target when no column
			// still has multiple viable logical sources in scope.
			boolean hasMultiSourceUnqualifiedUnknown = hasUnqualifiedUnknownWithMultipleViableSources(
					localUnresolvedColumnMap,
					localTableCollection,
					visibleQuerySourceCollection,
					localTableAliasMap);
			if (!hasMultiSourceUnqualifiedUnknown) {
				// Attempt to resolve any remaining unqualified unknowns that only have one possible source table, and move them to the resolved table dictionary for downstream resolution.  This allows us to resolve queries with unqualified columns that only have one possible source.
				boolean movedToSingleTableTarget = 
					walker.moveEntriesToSingleTableIfSingleTarget(localUnresolvedColumnMap, localTableCollection);
				if (!movedToSingleTableTarget && !localUnresolvedColumnMap.isEmpty()) {
					walker.moveEntriesToSingleTableIfSingleTarget(localUnresolvedColumnMap, currentTableDictionary);
				}
			}
		}

		if (updateTargetTableRef != null && !updateTargetTableRef.isBlank()) {
			// Before merging, resolve any remaining qualified unresolved columns whose
			// qualifier matches the update target table (e.g. WHERE-clause filter columns
			// like this_table.key, or RHS predicand refs like target.col) into the target
			// table collection.  After this step, if the unresolved map is empty the normal
			// diagnostic block was never reached and the merge can proceed cleanly;
			// otherwise the remaining entries will continue through the usual diagnostics.
			resolveRemainingQualifiedUnresolvedColumnsToTargetTable(
					localUnresolvedColumnMap,
					localTableAliasMap,
					localTargetTableCollection,
					updateTargetTableRef);
			mergeUpdateTargetAndLhsIntoTableDictionary(
					localTargetTableCollection,
					localLhsUnresolvedColumnMap,
					localTableCollection,
					localTableAliasMap,
					updateTargetTableRef);
			localTargetTableCollection.clear();
			localLhsUnresolvedColumnMap.clear();
		}

		// Merge table collection into the table dictionary map, which is used for symbol resolution in the rest of the query processing.
		//   This allows all table references to be resolved against the same dictionary regardless of where they are defined in the query.
		if (localTableCollection != null && localTableCollection.size() > 0) {
			for (String tab_ref : localTableCollection.keySet()) {
				String reference = normalizeTableRef(tab_ref);
				HashMap<String, Object> currDict = (HashMap<String, Object>)  currentTableDictionary.get(reference);
				if (currDict != null)
					currDict.putAll((Map<? extends String, ? extends Object>) localTableCollection.get(tab_ref));
				else {
					HashMap<String, Object> newDict = new HashMap<String, Object>();
					newDict.putAll((Map<? extends String, ? extends Object>) localTableCollection.get(tab_ref));
					 currentTableDictionary.put(reference, newDict);
				}
			}
		}
	
		// Validation that follows checks that the query's output interface can be constructed from the available columns
		// in the query context, which includes the FROM clause tables and any query-level filters.  If the interface 
		// cannot be constructed, then we have an invalid query and any unexplained logic will add fatal errors to the diagnostics 
		// list that will help the user understand why their query is invalid.
		if (localInterface != null) {
		for (String outputCol: localInterface.keySet()) {
			if (localScalarSubqueryAliases != null && localScalarSubqueryAliases.contains(outputCol)) {
				continue;
			}
			boolean hasSpecificResolutionFatalForOutputColumn = false;
			Object refsObj = localInterface.get(outputCol);
			if (refsObj instanceof ArrayList<?>) {
				ArrayList<Object> refs = (ArrayList<Object>) refsObj;
				for (int refIndex = 0; refIndex < refs.size(); refIndex++) {
					Object refObj = refs.get(refIndex);
					String columnName = walker.extractReferenceNameFromInterfaceEntry(refObj);
					String tableRef = walker.extractReferenceTableRefFromInterfaceEntry(refObj);
					String substitutionType = walker.extractSubstitutionTypeFromInterfaceEntry(refObj);

					if (substitutionType != null
							&& (MUMBLE_COLUMN_KEY.equals(substitutionType) || MUMBLE_PREDICAND_KEY.equals(substitutionType))) {
						continue;
					}

					if (columnName == null || "*".equals(columnName)) {
						continue;
					}

					if (tableRef != null) {
						String resolvedTableRef = walker.resolveAliasToTableName(tableRef, localTableAliasMap);
						String resolvedNonTableSourceRef = walker.resolveAliasToNonTableSourceQueryKey(tableRef, visibleQuerySourceCollection);
						if (resolvedNonTableSourceRef == null) {
							resolvedNonTableSourceRef = resolveAliasToQuerySourceFromAliasMap(tableRef, localTableAliasMap);
						}
						boolean explicitQueryReference = resolvedNonTableSourceRef != null
								|| walker.isNonTableQuerySourceReference(resolvedTableRef);
						boolean resolvedInSource = false;

						if (explicitQueryReference) {
							String queryDictionaryKey = (resolvedNonTableSourceRef != null)
									? resolvedNonTableSourceRef
									: resolvedTableRef;
							Object queryDictionaryObj = walker.queryColumnDictionaryMap.get(queryDictionaryKey);
							if (queryDictionaryObj instanceof Map<?, ?> queryDictionary) {
								resolvedInSource = containsKeyIgnoreCase((Map<String, Object>) queryDictionary, columnName)
										|| ((Map<String, Object>) queryDictionary).containsKey("*");
							}
							if (!resolvedInSource) {
								resolvedInSource = hasColumnInQueryOutputInterface(queryDictionaryKey, columnName);
							}
							if (!resolvedInSource) {
								resolvedInSource = hasWildcardInQueryOutputInterface(queryDictionaryKey);
							}
							if (resolvedInSource) {
								Object qualifiedUnknownEntry = consumeQualifiedUnknownEntry(
										localUnresolvedColumnMap,
										tableRef,
										columnName,
										false);
								if (qualifiedUnknownEntry == null) {
									qualifiedUnknownEntry = consumeUnqualifiedUnknownEntry(
											localUnresolvedColumnMap,
											columnName);
								}
								if (qualifiedUnknownEntry != null) {
									mergeExplicitQualifiedUnknownIntoSourceQueryDictionary(
											queryDictionaryKey,
											columnName,
											qualifiedUnknownEntry);
								}
							} else {
								// Fatal for query-alias references is already emitted by emitExplicitQualifiedUnknownDiagnostics.
								hasSpecificResolutionFatalForOutputColumn = true;
								continue;
							}
						} else {
							HashMap<String, Object> indicatedTableDictionary = walker.getTableDictionaryForReference(
									resolvedTableRef,
									localTableCollection);
							// For explicit table references (table.column), table/alias existence is sufficient.
							// Physical table schemas are not always available at parse time.
							resolvedInSource = indicatedTableDictionary != null;
						}

						if (!resolvedInSource) {
							if (deferCorrelatedValueSubqueryQualifiedUnknowns && !explicitQueryReference) {
								// Defer unresolved explicit table refs in correlated value-subqueries so
								// parent query aliases/tables can resolve them after this scope exits.
								continue;
							}
							Integer[] refLocation = (localCurrentQueryDictionary == null)
									? new Integer[] { null, null }
									: walker.getLineAndCharacterFromEntry(localCurrentQueryDictionary.get(outputCol));
							if (refLocation[0] == null || refLocation[1] == null) {
								refLocation = walker.getFirstEntryLineAndCharacter(localCurrentQueryDictionary);
							}

							String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE);
							String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE);
							String indicatedSourceRef = (resolvedNonTableSourceRef != null)
									? resolvedNonTableSourceRef
									: resolvedTableRef;
							String diagMessage =  String.format(diagTemplate,
											columnName,
											refLocation[0],
											refLocation[1],
											indicatedSourceRef);

							walker.addWalkerFatal(
									diagCode,
									diagMessage,
									refLocation[0],
									refLocation[1],
									columnName);
							hasSpecificResolutionFatalForOutputColumn = true;
						}
					} else {
						Integer[] refLocation = resolveUnqualifiedReferenceLocation(
								columnName,
								refObj,
								localUnresolvedColumnMap,
								localCurrentQueryDictionary,
								outputCol);

						ArrayList<String> sourceRefs = collectUnqualifiedSourceReferences(
								columnName,
								localTableCollection,
								visibleQuerySourceCollection,
								localTableAliasMap);

						String preferredDeleteTargetRef = resolvePreferredDeleteTargetForUnqualified(
								deleteTargetTableRef,
								localTableAliasMap,
								localTableCollection,
								sourceRefs);
						if (preferredDeleteTargetRef != null) {
							String resolvedSourceRef = normalizeTableRef(preferredDeleteTargetRef);
							refs.set(refIndex, cloneReferenceWithResolvedTableRef(refObj, resolvedSourceRef));
							materializeResolvedUnqualifiedReference(
									localUnresolvedColumnMap,
									localTableCollection,
									localTableAliasMap,
									resolvedSourceRef,
									columnName);
							continue;
						}

						if (sourceRefs.isEmpty()) {
							// Scenario: implicit reference has no candidate source.
							if (hasOnlyQueryBackedAliasSources(localTableAliasMap)) {
								emitUnqualifiedNotFoundInQueryAliasFatal(
										columnName,
										refLocation,
										localTableAliasMap);
								hasSpecificResolutionFatalForOutputColumn = true;
							}
							continue;
						} else if (sourceRefs.size() > 1) {
								if (shouldSuppressAmbiguousUnqualifiedDiagnostic(columnName, refLocation)) {
									continue;
								}
							// Scenario: implicit reference matches multiple candidate sources.
							String possibleSources = sourceRefs.toString();
							String diagCode = walker.getDiagnosticCode(
									SqlASTWalkerHelper.DIAG_SQL_AMBIGUOUS_COLUMN_REFERENCE);
							String diagTemplate = walker.getDiagnosticMessage(
									SqlASTWalkerHelper.DIAG_SQL_AMBIGUOUS_COLUMN_REFERENCE);
							String diagMessage = (diagTemplate == null)
									? String.format(
											"Ambiguous column reference '%s' at (l:%s c:%s). Possible sources: %s",
											columnName,
											refLocation[0],
											refLocation[1],
											possibleSources)
									: String.format(diagTemplate,
											columnName,
											refLocation[0],
											refLocation[1],
											possibleSources);

							walker.addWalkerDiagnostic(
									ParseDiagnostic.Severity.SEVERE_WARNING,
									diagCode,
									diagMessage,
									refLocation[0],
									refLocation[1],
									walker.getClass().getSimpleName(),
									null,
									columnName,
									true,
									"ast-walk",
									null,
									null);
							hasSpecificResolutionFatalForOutputColumn = true;
						} else {
							// Resolve an implicit column with a single source by updating only
							// the interface entry copy (do not mutate shared AST map objects).
							String resolvedSourceRef = normalizeTableRef(sourceRefs.get(0));
							refs.set(refIndex, cloneReferenceWithResolvedTableRef(refObj, resolvedSourceRef));
							materializeResolvedUnqualifiedReference(
									localUnresolvedColumnMap,
									localTableCollection,
									localTableAliasMap,
									resolvedSourceRef,
									columnName);
						}
					}
				}
			}
		}
		}

		mergeSelectListQualifiedQueryAliasRefsIntoSourceQueryDictionary(
				localInterface,
				localCurrentQueryDictionary,
				localUnresolvedColumnMap,
				localTableAliasMap,
				visibleQuerySourceCollection);

		assignTableRefsForColumnReferenceList(
				filtersList,
				localUnresolvedColumnMap,
				localCurrentQueryDictionary,
				localTableCollection,
				visibleQuerySourceCollection,
				localTableAliasMap,
				deleteTargetTableRef);
		assignTableRefsForColumnReferenceList(
				groupedByList,
				localUnresolvedColumnMap,
				localCurrentQueryDictionary,
				localTableCollection,
				visibleQuerySourceCollection,
				localTableAliasMap,
				deleteTargetTableRef);
		assignTableRefsForColumnReferenceList(
				orderedByList,
				localUnresolvedColumnMap,
				localCurrentQueryDictionary,
				localTableCollection,
				visibleQuerySourceCollection,
				localTableAliasMap,
				deleteTargetTableRef);

		// Late unqualified-reference resolution can materialize new entries (for example,
		// DELETE target columns from RETURNING). Re-merge so global and local dictionaries stay aligned.
		if (localTableCollection != null && localTableCollection.size() > 0) {
			for (String tab_ref : localTableCollection.keySet()) {
				String reference = normalizeTableRef(tab_ref);
				HashMap<String, Object> currDict = (HashMap<String, Object>) currentTableDictionary.get(reference);
				if (currDict != null) {
					currDict.putAll((Map<? extends String, ? extends Object>) localTableCollection.get(tab_ref));
				} else {
					HashMap<String, Object> newDict = new HashMap<String, Object>();
					newDict.putAll((Map<? extends String, ? extends Object>) localTableCollection.get(tab_ref));
					currentTableDictionary.put(reference, newDict);
				}
			}
		}

		 walker.validateQueryInterface(localInterface, localCurrentQueryDictionary, localTableAliasMap, localTableCollection);
		 walker.validateFilterReferences(filtersList, localCurrentQueryDictionary, localTableAliasMap, localTableCollection);

		// Merge everything back together into the final symbol table for this level of the query, with table aliases first, 
		// then symbol table entries, then query entries, then table entries, then the interface and current query dictionary 
		// for this level, and finally any filters list for this level if it exists.
		if (localTableAliasMap != null && !localTableAliasMap.isEmpty())
			walker.symbolTable.put(MUMBLE_TABLE_ALIAS_KEY, localTableAliasMap);
		walker.symbolTable.put(MUMBLE_TABLE_DICTIONARY_KEY, localTableCollection);
		if (!localTargetTableCollection.isEmpty()) {
			walker.symbolTable.put(MUMBLE_TARGET_TABLE_KEY, localTargetTableCollection);
		}
		if (updateTargetTableRef == null || !localCurrentQueryDictionary.isEmpty()) {
			walker.symbolTable.put(MUMBLE_QUERY_DICTIONARY_KEY, localCurrentQueryDictionary);
		}
		//walker.symbolTable.putAll(localCurrentQueryDictionary);
		//walker.symbolTable.putAll(localTableCollection);
		if (updateTargetTableRef == null) {
			walker.symbolTable.put(MUMBLE_INTERFACE_KEY, localInterface);
		}
		if (!localLhsUnresolvedColumnMap.isEmpty()) {
			walker.symbolTable.put(MUMBLE_LHS_UNRESOLVED_COLUMNS_KEY, localLhsUnresolvedColumnMap);
		}
		if (!localUnresolvedColumnMap.isEmpty()) {
			walker.symbolTable.put(MUMBLE_UNRESOLVED_COLUMN_KEY, localUnresolvedColumnMap);
		}
		// Call a method here that will merge the local Table Dictionary into the walker's TableDictionary Map
		walker.mergeTableDictionaryIntoWalkerTableDictionary(currentTableDictionary);
		
		if (localScalarSubqueryAliases != null)
			walker.symbolTable.put(MUMBLE_SCALAR_SUBQUERY_ALIASES_KEY, localScalarSubqueryAliases);
		if (filtersList != null)
			walker.symbolTable.put(MUMBLE_FILTERS_KEY, filtersList);
		if (groupedByList != null)
			walker.symbolTable.put(MUMBLE_GROUPED_BY_KEY, groupedByList);
		if (orderedByList != null)
			walker.symbolTable.put(MUMBLE_ORDERED_BY_KEY, orderedByList);

		 walker.showTrace(walker.symbolTrace, "Symbol Table: " + walker.symbolTable);
		return walker.symbolTable;
	}

	public void pruneUpdateTargetFromInputTableCollection(
			HashMap<String, Object> tableCollection,
			String updateTargetTableRef,
			HashMap<String, Object> tableAliasMap) {
		if (tableCollection == null || tableCollection.isEmpty() || updateTargetTableRef == null) {
			return;
		}

		String resolvedTarget = walker.resolveAliasToTableName(updateTargetTableRef, tableAliasMap);
		if (resolvedTarget == null || resolvedTarget.isBlank()) {
			resolvedTarget = updateTargetTableRef;
		}
		if (resolvedTarget == null || resolvedTarget.isBlank()) {
			return;
		}

		String normalizedTarget = normalizeTableRef(resolvedTarget);
		tableCollection.remove(normalizedTarget);
	}

	@SuppressWarnings("unchecked")
	public void resolveUpdateLhsColumnsToTargetTable(
			HashMap<String, Object> lhsUnresolvedColumnMap,
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> tableAliasMap,
			HashMap<String, Object> targetTableCollection,
			String updateTargetTableRef) {
		if (lhsUnresolvedColumnMap == null || lhsUnresolvedColumnMap.isEmpty()) {
			return;
		}
		if (targetTableCollection == null) {
			return;
		}

		String resolvedTarget = walker.resolveAliasToTableName(updateTargetTableRef, tableAliasMap);
		if (resolvedTarget == null || resolvedTarget.isBlank()) {
			resolvedTarget = updateTargetTableRef;
		}
		if (resolvedTarget == null || resolvedTarget.isBlank()) {
			return;
		}

		String normalizedTarget = normalizeTableRef(resolvedTarget);

		Object targetTableObj = targetTableCollection.get(normalizedTarget);
		HashMap<String, Object> targetTableColumns;
		if (targetTableObj instanceof HashMap<?, ?>) {
			targetTableColumns = (HashMap<String, Object>) targetTableObj;
		} else {
			targetTableColumns = new HashMap<String, Object>();
			targetTableCollection.put(normalizedTarget, targetTableColumns);
		}

		for (Map.Entry<String, Object> lhsEntry : lhsUnresolvedColumnMap.entrySet()) {
			String lhsKey = lhsEntry.getKey();
			Object lhsValue = lhsEntry.getValue();
			String lhsColumnName = extractLhsColumnName(lhsKey, lhsValue);
			if (lhsColumnName == null || lhsColumnName.isBlank()) {
				continue;
			}

			Object normalizedRefs = normalizeUpdateColumnRefs(lhsValue);
			if (normalizedRefs != null) {
				targetTableColumns.put(lhsColumnName, normalizedRefs);
			}

			if (unresolvedColumnMap != null) {
				if (lhsKey != null) {
					unresolvedColumnMap.remove(lhsKey);
				}
				unresolvedColumnMap.remove(lhsColumnName);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void mergeSelectListQualifiedQueryAliasRefsIntoSourceQueryDictionary(
			HashMap<String, Object> localInterface,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> localUnresolvedColumnMap,
			HashMap<String, Object> localTableAliasMap,
			HashMap<String, Object> visibleQuerySourceCollection) {
		if (localInterface == null || localInterface.isEmpty()) {
			return;
		}

		for (Map.Entry<String, Object> interfaceEntry : localInterface.entrySet()) {
			String outputCol = interfaceEntry.getKey();
			Object refsObj = interfaceEntry.getValue();
			if (!(refsObj instanceof ArrayList<?> refs)) {
				continue;
			}

			for (Object refObj : refs) {
				String columnName = walker.extractReferenceNameFromInterfaceEntry(refObj);
				String tableRef = walker.extractReferenceTableRefFromInterfaceEntry(refObj);
				if (columnName == null || columnName.isBlank() || "*".equals(columnName)
						|| tableRef == null || tableRef.isBlank() || "*".equals(tableRef)) {
					continue;
				}

				String resolvedNonTableSourceRef = walker.resolveAliasToNonTableSourceQueryKey(tableRef, visibleQuerySourceCollection);
				String resolvedTableRef = walker.resolveAliasToTableName(tableRef, localTableAliasMap);
				String querySourceRef = (resolvedNonTableSourceRef != null)
						? resolvedNonTableSourceRef
						: resolvedTableRef;
				if (querySourceRef == null || !walker.isNonTableQuerySourceReference(querySourceRef)) {
					continue;
				}
				if (!aliasMapsToQuerySource(tableRef, querySourceRef, localTableAliasMap)) {
					continue;
				}

				Object sourceDictionaryObj = walker.queryColumnDictionaryMap.get(querySourceRef);
				if (!(sourceDictionaryObj instanceof Map<?, ?> sourceDictionaryMapObj)) {
					continue;
				}
				Map<String, Object> sourceDictionary = (Map<String, Object>) sourceDictionaryMapObj;
				boolean sourceProvidesColumn = containsKeyIgnoreCase(sourceDictionary, columnName)
						|| containsKeyIgnoreCase(sourceDictionary, "*")
						|| hasColumnInQueryOutputInterface(querySourceRef, columnName)
						|| hasWildcardInQueryOutputInterface(querySourceRef);
				if (!sourceProvidesColumn) {
					continue;
				}

				Object sourceRefTokens = consumeQualifiedUnknownEntry(localUnresolvedColumnMap, tableRef, columnName, false);
				if (sourceRefTokens != null) {
					mergeExplicitQualifiedUnknownIntoSourceQueryDictionary(querySourceRef, columnName, sourceRefTokens);
				}
			}
		}
	}

	public boolean aliasMapsToQuerySource(
			String aliasRef,
			String querySourceRef,
			HashMap<String, Object> tableAliasMap) {
		if (aliasRef == null || aliasRef.isBlank() || querySourceRef == null || querySourceRef.isBlank()) {
			return false;
		}
		if (tableAliasMap == null || tableAliasMap.isEmpty()) {
			return false;
		}

		String mappedSource = null;
		Object mappedObj = tableAliasMap.get(aliasRef);
		if (mappedObj instanceof String s && !s.isBlank()) {
			mappedSource = s;
		} else {
			for (Map.Entry<String, Object> entry : tableAliasMap.entrySet()) {
				if (entry.getKey() != null
						&& entry.getKey().equalsIgnoreCase(aliasRef)
						&& entry.getValue() instanceof String mappedValue
						&& !mappedValue.isBlank()) {
					mappedSource = mappedValue;
					break;
				}
			}
		}
		if (mappedSource == null || mappedSource.isBlank()) {
			return false;
		}

		String normalizedMapped = mappedSource.startsWith("def_") ? mappedSource.substring("def_".length()) : mappedSource;
		String normalizedQuerySource = querySourceRef.startsWith("def_") ? querySourceRef.substring("def_".length()) : querySourceRef;
		return normalizedMapped.equalsIgnoreCase(normalizedQuerySource);
	}

	@SuppressWarnings("unchecked")
	public void resolveUpdateQualifiedUnresolvedColumnsToInputTables(
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> tableAliasMap,
			HashMap<String, Object> tableCollection) {
		if (unresolvedColumnMap == null || unresolvedColumnMap.isEmpty()
				|| tableCollection == null || tableCollection.isEmpty()) {
			return;
		}

		ArrayList<String> resolvedKeys = new ArrayList<String>();
		for (Map.Entry<String, Object> unresolvedEntry : unresolvedColumnMap.entrySet()) {
			String unresolvedKey = unresolvedEntry.getKey();
			if (unresolvedKey == null) {
				continue;
			}

			int separatorIndex = unresolvedKey.lastIndexOf('.');
			if (separatorIndex <= 0 || separatorIndex + 1 >= unresolvedKey.length()) {
				continue;
			}

			String qualifier = unresolvedKey.substring(0, separatorIndex);
			String columnName = unresolvedKey.substring(separatorIndex + 1);
			if (columnName.isBlank()) {
				continue;
			}

			String resolvedTableRef = walker.resolveAliasToTableName(qualifier, tableAliasMap);
			if (resolvedTableRef == null || resolvedTableRef.isBlank()) {
				resolvedTableRef = qualifier;
			}
			if (resolvedTableRef == null || resolvedTableRef.isBlank()) {
				continue;
			}

			String normalizedTableRef = normalizeTableRef(resolvedTableRef);
			Object tableEntryObj = tableCollection.get(normalizedTableRef);
			if (!(tableEntryObj instanceof Map<?, ?> tableEntryMapObj)) {
				continue;
			}

			Map<String, Object> tableEntry = (Map<String, Object>) tableEntryMapObj;
			Object normalizedRefs = normalizeUpdateColumnRefs(unresolvedEntry.getValue());
			if (normalizedRefs == null) {
				continue;
			}

			tableEntry.put(columnName, normalizedRefs);
			resolvedKeys.add(unresolvedKey);
		}

		for (String resolvedKey : resolvedKeys) {
			unresolvedColumnMap.remove(resolvedKey);
		}
	}

	@SuppressWarnings("unchecked")
	public void resolveUpdateUnqualifiedUnresolvedColumnsToTargetTableWhenNoInputSources(
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> targetTableCollection,
			HashMap<String, Object> tableAliasMap,
			String updateTargetTableRef) {
		if (unresolvedColumnMap == null || unresolvedColumnMap.isEmpty()
				|| targetTableCollection == null
				|| updateTargetTableRef == null || updateTargetTableRef.isBlank()) {
			return;
		}

		if (tableCollection != null && !tableCollection.isEmpty()) {
			return;
		}

		String resolvedTarget = walker.resolveAliasToTableName(updateTargetTableRef, tableAliasMap);
		if (resolvedTarget == null || resolvedTarget.isBlank()) {
			resolvedTarget = updateTargetTableRef;
		}
		if (resolvedTarget == null || resolvedTarget.isBlank()) {
			return;
		}

		String normalizedTarget = normalizeTableRef(resolvedTarget);

		Object targetTableObj = targetTableCollection.get(normalizedTarget);
		HashMap<String, Object> targetColumns;
		if (targetTableObj instanceof HashMap<?, ?> existingMap) {
			targetColumns = (HashMap<String, Object>) existingMap;
		} else {
			targetColumns = new HashMap<String, Object>();
			targetTableCollection.put(normalizedTarget, targetColumns);
		}

		ArrayList<String> resolvedKeys = new ArrayList<String>();
		for (Map.Entry<String, Object> unresolvedEntry : unresolvedColumnMap.entrySet()) {
			String unresolvedKey = unresolvedEntry.getKey();
			if (unresolvedKey == null || unresolvedKey.contains(".")) {
				continue;
			}

			Object normalizedRefs = normalizeUpdateColumnRefs(unresolvedEntry.getValue());
			if (normalizedRefs == null) {
				continue;
			}

			targetColumns.put(unresolvedKey, normalizedRefs);
			resolvedKeys.add(unresolvedKey);
		}

		for (String resolvedKey : resolvedKeys) {
			unresolvedColumnMap.remove(resolvedKey);
		}
	}

	@SuppressWarnings("unchecked")
	public void resolveUpdateRhsUnqualifiedAssignmentColumnsToTargetTable(
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> targetTableCollection,
			HashMap<String, Object> tableAliasMap,
			HashMap<String, Object> visibleQuerySourceCollection,
			String updateTargetTableRef) {
		if (targetTableCollection == null
				|| updateTargetTableRef == null
				|| updateTargetTableRef.isBlank()) {
			return;
		}

		Object assignmentsObj = walker.symbolTable.get(MUMBLE_ASSIGNMENTS_KEY);
		if (!(assignmentsObj instanceof Map<?, ?> assignmentsMapObj) || assignmentsMapObj.isEmpty()) {
			return;
		}

		String resolvedTarget = walker.resolveAliasToTableName(updateTargetTableRef, tableAliasMap);
		if (resolvedTarget == null || resolvedTarget.isBlank()) {
			resolvedTarget = updateTargetTableRef;
		}
		if (resolvedTarget == null || resolvedTarget.isBlank()) {
			return;
		}

		String normalizedTarget = normalizeTableRef(resolvedTarget);

		Object targetTableObj = targetTableCollection.get(normalizedTarget);
		HashMap<String, Object> targetColumns;
		if (targetTableObj instanceof HashMap<?, ?> existingMapObj) {
			targetColumns = (HashMap<String, Object>) existingMapObj;
		} else {
			targetColumns = new HashMap<String, Object>();
			targetTableCollection.put(normalizedTarget, targetColumns);
		}

		for (Map.Entry<?, ?> assignmentEntry : ((Map<?, ?>) assignmentsMapObj).entrySet()) {
			Object rhsRefsObj = assignmentEntry.getValue();
			if (!(rhsRefsObj instanceof List<?> rhsRefs) || rhsRefs.isEmpty()) {
				continue;
			}

			for (Object rhsRefObj : rhsRefs) {
				if (!(rhsRefObj instanceof Map<?, ?>)) {
					continue;
				}

				String columnName = walker.extractReferenceNameFromInterfaceEntry(rhsRefObj);
				String tableRef = walker.extractReferenceTableRefFromInterfaceEntry(rhsRefObj);
				if (columnName == null || columnName.isBlank() || "*".equals(columnName)) {
					continue;
				}
				if (tableRef != null && !tableRef.isBlank() && !"*".equals(tableRef)) {
					continue;
				}

				if (containsKeyIgnoreCase(targetColumns, columnName)) {
					continue;
				}

				ArrayList<String> sourceRefs = collectUnqualifiedSourceReferences(
						columnName,
						tableCollection,
						visibleQuerySourceCollection,
						tableAliasMap);
				if (sourceRefs != null && !sourceRefs.isEmpty()) {
					continue;
				}

				// If UPDATE has exactly one FROM source table, treat unqualified RHS refs
				// as originating from that source before falling back to target table.
				if (tableCollection != null && tableCollection.size() == 1) {
					String singleFromTableRef = tableCollection.keySet().iterator().next();
					Object singleFromTableObj = tableCollection.get(singleFromTableRef);
					if (singleFromTableObj instanceof Map<?, ?> singleFromTableMapObj) {
						Map<String, Object> singleFromTableMap = (Map<String, Object>) singleFromTableMapObj;
						Object unresolvedValue = null;
						if (unresolvedColumnMap != null) {
							unresolvedValue = unresolvedColumnMap.remove(columnName);
						}
						Object normalizedRefs = normalizeUpdateColumnRefs(
								(unresolvedValue != null) ? unresolvedValue : rhsRefObj);
						if (normalizedRefs == null && rhsRefObj instanceof Map<?, ?> rhsRefMapObj) {
							ArrayList<Object> syntheticRefs = new ArrayList<Object>();
							syntheticRefs.add(new HashMap<String, Object>((Map<String, Object>) rhsRefMapObj));
							normalizedRefs = syntheticRefs;
						}
						if (normalizedRefs != null) {
							singleFromTableMap.put(columnName, normalizedRefs);
							continue;
						}
					}
				}

				Object unresolvedValue = null;
				if (unresolvedColumnMap != null) {
					unresolvedValue = unresolvedColumnMap.remove(columnName);
				}
				Object normalizedRefs = normalizeUpdateColumnRefs(
						(unresolvedValue != null) ? unresolvedValue : rhsRefObj);
				if (normalizedRefs == null && rhsRefObj instanceof Map<?, ?> rhsRefMapObj) {
					ArrayList<Object> syntheticRefs = new ArrayList<Object>();
					syntheticRefs.add(new HashMap<String, Object>((Map<String, Object>) rhsRefMapObj));
					normalizedRefs = syntheticRefs;
				}
				if (normalizedRefs == null) {
					continue;
				}

				targetColumns.put(columnName, normalizedRefs);
			}
		}
	}

	/**
	 * After the normal unresolved-column resolution pass, scan any entries that
	 * remain in {@code unresolvedColumnMap} whose qualifier resolves to the update
	 * target table (by name or alias).  Those columns — typically WHERE-clause
	 * filter columns or RHS predicand refs that explicitly reference the target —
	 * are moved directly into {@code targetTableCollection} so they appear in the
	 * final table dictionary under the target table rather than staying as
	 * unresolved.  Entries that do NOT match the target are left in place so the
	 * normal diagnostic machinery can flag them.
	 */
	@SuppressWarnings("unchecked")
	public void resolveRemainingQualifiedUnresolvedColumnsToTargetTable(
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> tableAliasMap,
			HashMap<String, Object> targetTableCollection,
			String updateTargetTableRef) {
		if (unresolvedColumnMap == null || unresolvedColumnMap.isEmpty()
				|| targetTableCollection == null
				|| updateTargetTableRef == null || updateTargetTableRef.isBlank()) {
			return;
		}

		String resolvedTarget = walker.resolveAliasToTableName(updateTargetTableRef, tableAliasMap);
		if (resolvedTarget == null || resolvedTarget.isBlank()) {
			resolvedTarget = updateTargetTableRef;
		}
		String normalizedTarget = normalizeTableRef(resolvedTarget);
		String normalizedAlias = normalizeTableRef(updateTargetTableRef);

		Object targetTableObj = targetTableCollection.get(normalizedTarget);
		HashMap<String, Object> targetColumns;
		if (targetTableObj instanceof HashMap<?, ?> existingMap) {
			targetColumns = (HashMap<String, Object>) existingMap;
		} else {
			targetColumns = new HashMap<String, Object>();
			targetTableCollection.put(normalizedTarget, targetColumns);
		}

		ArrayList<String> resolvedKeys = new ArrayList<String>();
		for (Map.Entry<String, Object> unresolvedEntry : unresolvedColumnMap.entrySet()) {
			String unresolvedKey = unresolvedEntry.getKey();
			if (unresolvedKey == null) {
				continue;
			}

			int separatorIndex = unresolvedKey.lastIndexOf('.');
			if (separatorIndex <= 0 || separatorIndex + 1 >= unresolvedKey.length()) {
				continue;
			}

			String qualifier = unresolvedKey.substring(0, separatorIndex);
			String columnName = unresolvedKey.substring(separatorIndex + 1);
			if (columnName.isBlank()) {
				continue;
			}

			String normalizedQualifier = normalizeTableRef(qualifier);
			String resolvedQualifier = walker.resolveAliasToTableName(normalizedQualifier, tableAliasMap);
			if (resolvedQualifier == null || resolvedQualifier.isBlank()) {
				resolvedQualifier = normalizedQualifier;
			}
			String normalizedResolvedQualifier = normalizeTableRef(resolvedQualifier);

			// Accept if qualifier equals the canonical target name or the alias
			if (!normalizedResolvedQualifier.equals(normalizedTarget)
					&& !normalizedQualifier.equals(normalizedAlias)
					&& !normalizedQualifier.equals(normalizedTarget)) {
				continue;
			}

			Object normalizedRefs = normalizeUpdateColumnRefs(unresolvedEntry.getValue());
			if (normalizedRefs == null) {
				continue;
			}

			targetColumns.put(columnName, normalizedRefs);
			resolvedKeys.add(unresolvedKey);
		}

		for (String resolvedKey : resolvedKeys) {
			unresolvedColumnMap.remove(resolvedKey);
		}
	}

	@SuppressWarnings("unchecked")
	public void mergeUpdateTargetAndLhsIntoTableDictionary(
			HashMap<String, Object> targetTableCollection,
			HashMap<String, Object> lhsUnresolvedColumnMap,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> tableAliasMap,
			String updateTargetTableRef) {
		if (tableCollection == null || updateTargetTableRef == null || updateTargetTableRef.isBlank()) {
			return;
		}

		String resolvedTarget = walker.resolveAliasToTableName(updateTargetTableRef, tableAliasMap);
		if (resolvedTarget == null || resolvedTarget.isBlank()) {
			resolvedTarget = updateTargetTableRef;
		}
		if (resolvedTarget == null || resolvedTarget.isBlank()) {
			return;
		}

		String normalizedTarget = normalizeTableRef(resolvedTarget);

		Object existingTargetObj = tableCollection.get(normalizedTarget);
		HashMap<String, Object> targetColumns;
		if (existingTargetObj instanceof HashMap<?, ?> existingTargetMapObj) {
			targetColumns = (HashMap<String, Object>) existingTargetMapObj;
		} else {
			targetColumns = new HashMap<String, Object>();
			tableCollection.put(normalizedTarget, targetColumns);
		}

		Object targetTableObj = targetTableCollection.get(normalizedTarget);
		if (targetTableObj instanceof Map<?, ?> targetTableMapObj) {
			targetColumns.putAll((Map<String, Object>) targetTableMapObj);
		}

		if (lhsUnresolvedColumnMap == null || lhsUnresolvedColumnMap.isEmpty()) {
			return;
		}

		for (Map.Entry<String, Object> lhsEntry : lhsUnresolvedColumnMap.entrySet()) {
			String lhsColumnName = extractLhsColumnName(lhsEntry.getKey(), lhsEntry.getValue());
			if (lhsColumnName == null || lhsColumnName.isBlank()) {
				continue;
			}

			Object normalizedRefs = normalizeUpdateColumnRefs(lhsEntry.getValue());
			if (normalizedRefs != null) {
				targetColumns.put(lhsColumnName, normalizedRefs);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public String extractLhsColumnName(String lhsKey, Object lhsValue) {
		if (lhsValue instanceof Map<?, ?> lhsValueMapObj) {
			Map<String, Object> lhsValueMap = (Map<String, Object>) lhsValueMapObj;
			Object columnObj = lhsValueMap.get(MUMBLE_COLUMN_KEY);
			if (columnObj instanceof Map<?, ?> columnMapObj) {
				Object nameObj = ((Map<String, Object>) columnMapObj).get(MUMBLE_NAME_KEY);
				if (nameObj != null && !nameObj.toString().isBlank()) {
					return nameObj.toString();
				}
			}

			Object directNameObj = lhsValueMap.get(MUMBLE_NAME_KEY);
			if (directNameObj != null && !directNameObj.toString().isBlank()) {
				return directNameObj.toString();
			}
		}

		if (lhsKey == null || lhsKey.isBlank()) {
			return null;
		}

		int separatorIndex = lhsKey.lastIndexOf('.');
		if (separatorIndex >= 0 && separatorIndex + 1 < lhsKey.length()) {
			return lhsKey.substring(separatorIndex + 1);
		}

		return lhsKey;
	}

	@SuppressWarnings("unchecked")
	public void propagateUnqualifiedSelectStarToScopeTables(
			HashMap<String, Object> localInterface,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> localTableCollection) {
		if (localInterface == null || localInterface.isEmpty()
				|| localCurrentQueryDictionary == null || localCurrentQueryDictionary.isEmpty()
				|| localTableCollection == null || localTableCollection.isEmpty()) {
			return;
		}

		Object starRefs = localCurrentQueryDictionary.get("*");
		if (starRefs == null) {
			return;
		}

		Object interfaceRefsObj = localInterface.get("*");
		if (!(interfaceRefsObj instanceof ArrayList<?> interfaceRefs) || interfaceRefs.isEmpty()) {
			return;
		}

		boolean hasUnqualifiedStarReference = false;
		for (Object interfaceRef : interfaceRefs) {
			String refName = walker.extractReferenceNameFromInterfaceEntry(interfaceRef);
			if (!"*".equals(refName)) {
				continue;
			}

			String refTable = walker.extractReferenceTableRefFromInterfaceEntry(interfaceRef);
			if (refTable == null || "*".equals(refTable)) {
				hasUnqualifiedStarReference = true;
				break;
			}
		}

		if (!hasUnqualifiedStarReference) {
			return;
		}

		for (Map.Entry<String, Object> tableEntry : localTableCollection.entrySet()) {
			String tableRef = tableEntry.getKey();
			if (tableRef == null
					|| tableRef.startsWith(MUMBLE_QUERY_KEY)
					|| tableRef.startsWith(MUMBLE_UNION_KEY)
					|| tableRef.startsWith(MUMBLE_INTERSECT_KEY)
					|| tableRef.startsWith(MUMBLE_VALUES_KEY)) {
				continue;
			}

			if (tableEntry.getValue() instanceof HashMap<?, ?> tableColumns) {
				walker.mergeResolvedColumnIntoDictionary((HashMap<String, Object>) tableColumns, "*", starRefs);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> retainOnlyLocallyResolvableExplicitQualifiedUnknowns(
			HashMap<String, Object> explicitQualifiedUnknownEntries,
			HashMap<String, Object> localInterface,
			Object filtersList,
			HashMap<String, Object> tableAliasCollection,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> scopedQueryCollection,
			HashMap<String, Object> unresolvedColumnMap) {
		HashMap<String, Object> locallyResolvableEntries = new HashMap<String, Object>();
		if (explicitQualifiedUnknownEntries == null || explicitQualifiedUnknownEntries.isEmpty()) {
			return locallyResolvableEntries;
		}

		for (Map.Entry<String, Object> unknownEntry : explicitQualifiedUnknownEntries.entrySet()) {
			String unresolvedKey = unknownEntry.getKey();
			String explicitTableRef = resolveExplicitTableRefForUnknownEntry(
					unresolvedKey,
					localInterface,
					filtersList);
			if (explicitTableRef == null) {
				locallyResolvableEntries.put(unresolvedKey, unknownEntry.getValue());
				continue;
			}

			String resolvedTableRef = walker.resolveAliasToTableName(explicitTableRef, tableAliasCollection);
			String resolvedNonTableSourceRef = walker.resolveAliasToNonTableSourceQueryKey(explicitTableRef, scopedQueryCollection);
			HashMap<String, Object> indicatedTableDictionary = walker.getTableDictionaryForReference(
					resolvedTableRef,
					tableCollection);

			boolean hasLocalSource = resolvedNonTableSourceRef != null || indicatedTableDictionary != null;
			if (hasLocalSource) {
				locallyResolvableEntries.put(unresolvedKey, unknownEntry.getValue());
			} else if (unresolvedColumnMap != null) {
				unresolvedColumnMap.put(unresolvedKey, unknownEntry.getValue());
			}
		}

		return locallyResolvableEntries;
	}

	@SuppressWarnings("unchecked")
	public String resolveExplicitTableRefForUnknownEntry(
			String unresolvedKey,
			HashMap<String, Object> localInterface,
			Object filtersList) {
		if (unresolvedKey != null && unresolvedKey.contains(".")) {
			int dotIndex = unresolvedKey.indexOf('.');
			if (dotIndex > 0) {
				return unresolvedKey.substring(0, dotIndex);
			}
		}

		String columnName = unresolvedKey;
		if (columnName == null || columnName.isBlank()) {
			return null;
		}

		if (localInterface != null) {
			for (Object refsObj : localInterface.values()) {
				if (!(refsObj instanceof ArrayList<?> refs)) {
					continue;
				}
				for (Object refObj : refs) {
					String refName = walker.extractReferenceNameFromInterfaceEntry(refObj);
					String refTable = walker.extractReferenceTableRefFromInterfaceEntry(refObj);
					if (columnName.equals(refName) && refTable != null && !"*".equals(refTable)) {
						return refTable;
					}
				}
			}
		}

		if (filtersList instanceof ArrayList<?> filters) {
			for (Object filterObj : filters) {
				if (!(filterObj instanceof Map<?, ?> filterMap)) {
					continue;
				}
				String filterName = walker.extractReferenceNameFromInterfaceEntry(filterMap);
				String filterTableRef = walker.extractReferenceTableRefFromInterfaceEntry(filterMap);
				if (filterName != null
						&& filterTableRef != null
						&& columnName.equals(filterName)
						&& !"*".equals(filterTableRef)) {
					return filterTableRef;
				}
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public void materializeResolvedUnqualifiedReference(
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> tableAliasCollection,
			String resolvedSourceRef,
			String columnName) {
		if (columnName == null || columnName.isBlank()) {
			return;
		}

		Object unresolvedEntry = consumeUnqualifiedUnknownEntry(unresolvedColumnMap, columnName);
		if (unresolvedEntry == null || resolvedSourceRef == null || resolvedSourceRef.isBlank()) {
			return;
		}

		String canonicalSourceRef = normalizeTableRef(resolvedSourceRef);
		String queryAliasSourceRef = resolveAliasToQuerySourceFromAliasMap(
				canonicalSourceRef,
				tableAliasCollection);
		if (queryAliasSourceRef != null) {
			return;
		}
		if (walker.isNonTableQuerySourceReference(canonicalSourceRef)
				|| isTableFunctionSourceReference(canonicalSourceRef)) {
			return;
		}

		if (tableCollection == null) {
			return;
		}

		HashMap<String, Object> indicatedTableDictionary = walker.getTableDictionaryForReference(
				canonicalSourceRef,
				tableCollection);
		if (indicatedTableDictionary == null) {
			Object existing = tableCollection.get(canonicalSourceRef);
			if (existing instanceof HashMap<?, ?> existingMapObj) {
				indicatedTableDictionary = (HashMap<String, Object>) existingMapObj;
			} else {
				indicatedTableDictionary = new HashMap<String, Object>();
				tableCollection.put(canonicalSourceRef, indicatedTableDictionary);
			}
		}

		walker.mergeResolvedColumnIntoDictionary(indicatedTableDictionary, columnName, unresolvedEntry);
	}

	public Object consumeUnqualifiedUnknownEntry(
			HashMap<String, Object> unresolvedColumnMap,
			String columnName) {
		if (unresolvedColumnMap == null || unresolvedColumnMap.isEmpty()
				|| columnName == null || columnName.isBlank()) {
			return null;
		}

		Object direct = unresolvedColumnMap.remove(columnName);
		if (direct != null) {
			return direct;
		}

		for (String key : new ArrayList<String>(unresolvedColumnMap.keySet())) {
			if (key == null || key.contains(".")) {
				continue;
			}
			if (key.equalsIgnoreCase(columnName)) {
				return unresolvedColumnMap.remove(key);
			}
		}

		return null;
	}

	public Object consumeQualifiedUnknownEntry(
			HashMap<String, Object> unresolvedColumnMap,
			String tableRef,
			String columnName,
			boolean includeCapturedFallback) {
		if (tableRef == null || tableRef.isBlank()
				|| columnName == null || columnName.isBlank()) {
			return null;
		}

		String directKey = tableRef + "." + columnName;
		if (unresolvedColumnMap != null && !unresolvedColumnMap.isEmpty()) {
			Object removedDirect = unresolvedColumnMap.remove(directKey);
			if (removedDirect != null) {
				return removedDirect;
			}

			for (String key : new ArrayList<String>(unresolvedColumnMap.keySet())) {
				if (key == null || !key.contains(".")) {
					continue;
				}
				int separatorIndex = key.lastIndexOf('.');
				if (separatorIndex <= 0 || separatorIndex + 1 >= key.length()) {
					continue;
				}
				String keyTableRef = key.substring(0, separatorIndex);
				String keyColumnName = key.substring(separatorIndex + 1);
				if (keyTableRef.equalsIgnoreCase(tableRef)
						&& keyColumnName.equalsIgnoreCase(columnName)) {
					return unresolvedColumnMap.remove(key);
				}
			}
		}

		if (includeCapturedFallback) {
			Object capturedEntry = walker.getCapturedQualifiedUnresolvedLocationEntry(directKey);
			if (capturedEntry != null) {
				return capturedEntry;
			}
			capturedEntry = walker.getCapturedQualifiedUnresolvedLocationEntry(tableRef.toLowerCase() + "." + columnName);
			if (capturedEntry != null) {
				return capturedEntry;
			}
			capturedEntry = walker.getCapturedQualifiedUnresolvedLocationEntry(tableRef + "." + columnName.toLowerCase());
			if (capturedEntry != null) {
				return capturedEntry;
			}
			capturedEntry = walker.getCapturedQualifiedUnresolvedLocationEntry(tableRef.toLowerCase() + "." + columnName.toLowerCase());
			if (capturedEntry != null) {
				return capturedEntry;
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public Integer[] resolveUnqualifiedReferenceLocation(
			String columnName,
			Object interfaceRefObj,
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> localCurrentQueryDictionary,
			String outputCol) {
		Integer[] refLocation = walker.getLineAndCharacterFromEntry(interfaceRefObj);
		if (refLocation[0] != null && refLocation[1] != null) {
			return refLocation;
		}

		Object unresolvedEntry = getUnqualifiedUnknownEntry(unresolvedColumnMap, columnName);
		if (unresolvedEntry != null) {
			refLocation = walker.getLineAndCharacterFromEntry(unresolvedEntry);
			if (refLocation[0] != null && refLocation[1] != null) {
				return refLocation;
			}
		}

		refLocation = (localCurrentQueryDictionary == null)
				? new Integer[] { null, null }
				: walker.getLineAndCharacterFromEntry(localCurrentQueryDictionary.get(outputCol));
		if (refLocation[0] != null && refLocation[1] != null) {
			return refLocation;
		}

		if (unresolvedEntry != null) {
			refLocation = walker.getFirstEntryLineAndCharacter((HashMap<String, Object>) unresolvedEntry);
			if (refLocation[0] != null && refLocation[1] != null) {
				return refLocation;
			}
		}

		if (localCurrentQueryDictionary != null) {
			return walker.getFirstEntryLineAndCharacter(localCurrentQueryDictionary);
		}

		return new Integer[] { null, null };
	}

	public Object getUnqualifiedUnknownEntry(
			HashMap<String, Object> unresolvedColumnMap,
			String columnName) {
		if (unresolvedColumnMap == null || unresolvedColumnMap.isEmpty()
				|| columnName == null || columnName.isBlank()) {
			return null;
		}

		Object direct = unresolvedColumnMap.get(columnName);
		if (direct != null) {
			return direct;
		}

		for (Map.Entry<String, Object> entry : unresolvedColumnMap.entrySet()) {
			String key = entry.getKey();
			if (key == null || key.contains(".")) {
				continue;
			}
			if (key.equalsIgnoreCase(columnName)) {
				return entry.getValue();
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public Object cloneReferenceWithResolvedTableRef(Object refObj, String resolvedSourceRef) {
		if (refObj == null || resolvedSourceRef == null || resolvedSourceRef.isBlank()) {
			return refObj;
		}
		if (!(refObj instanceof Map<?, ?>)) {
			return refObj;
		}

		Map<String, Object> refMap = (Map<String, Object>) refObj;
		if (refMap.get(MUMBLE_TABLE_REF_KEY) != null) {
			return refObj;
		}

		HashMap<String, Object> updatedRefMap = new HashMap<String, Object>(refMap);
		updatedRefMap.put(MUMBLE_TABLE_REF_KEY, resolvedSourceRef);
		return updatedRefMap;
	}

	@SuppressWarnings("unchecked")
	public void assignTableRefsForColumnReferenceList(
			Object columnListObj,
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> visibleQuerySourceCollection,
			HashMap<String, Object> localTableAliasMap,
			String deleteTargetTableRef) {
		if (!(columnListObj instanceof ArrayList<?>)) {
			return;
		}

		ArrayList<Object> columnRefs = (ArrayList<Object>) columnListObj;
		for (int index = 0; index < columnRefs.size(); index++) {
			Object columnRefObj = columnRefs.get(index);
			String columnName = walker.extractReferenceNameFromInterfaceEntry(columnRefObj);
			String tableRef = walker.extractReferenceTableRefFromInterfaceEntry(columnRefObj);
			String substitutionType = walker.extractSubstitutionTypeFromInterfaceEntry(columnRefObj);

			if (substitutionType != null
					&& (MUMBLE_COLUMN_KEY.equals(substitutionType) || MUMBLE_PREDICAND_KEY.equals(substitutionType))) {
				continue;
			}

			if (columnName == null || "*".equals(columnName) || tableRef != null) {
				// Skip explicit refs; explicit unresolved handling is covered by unresolved-map diagnostics.
				continue;
			}

			Integer[] refLocation = resolveUnqualifiedReferenceLocation(
					columnName,
					columnRefObj,
					unresolvedColumnMap,
					localCurrentQueryDictionary,
					columnName);

			ArrayList<String> sourceRefs = collectUnqualifiedSourceReferences(
					columnName,
					localTableCollection,
					visibleQuerySourceCollection,
					localTableAliasMap);

			String preferredDeleteTargetRef = resolvePreferredDeleteTargetForUnqualified(
					deleteTargetTableRef,
					localTableAliasMap,
					localTableCollection,
					sourceRefs);
			if (preferredDeleteTargetRef != null) {
				String resolvedSourceRef = normalizeTableRef(preferredDeleteTargetRef);
				columnRefs.set(index, cloneReferenceWithResolvedTableRef(columnRefObj, resolvedSourceRef));
				materializeResolvedUnqualifiedReference(
						unresolvedColumnMap,
						localTableCollection,
						localTableAliasMap,
						resolvedSourceRef,
						columnName);
				continue;
			}

			if (sourceRefs.isEmpty()) {
				if (hasOnlyQueryBackedAliasSources(localTableAliasMap)) {
					emitUnqualifiedNotFoundInQueryAliasFatal(
							columnName,
							refLocation,
							localTableAliasMap);
				}
				continue;
			} else if (sourceRefs.size() > 1) {
				if (shouldSuppressAmbiguousUnqualifiedDiagnostic(columnName, refLocation)) {
					continue;
				}
				String possibleSources = sourceRefs.toString();
				String diagCode = walker.getDiagnosticCode(
						SqlASTWalkerHelper.DIAG_SQL_AMBIGUOUS_COLUMN_REFERENCE);
				String diagTemplate = walker.getDiagnosticMessage(
						SqlASTWalkerHelper.DIAG_SQL_AMBIGUOUS_COLUMN_REFERENCE);
				String diagMessage = (diagTemplate == null)
						? String.format(
								"Ambiguous column reference '%s' at (l:%s c:%s). Possible sources: %s",
								columnName,
								refLocation[0],
								refLocation[1],
								possibleSources)
						: String.format(diagTemplate,
								columnName,
								refLocation[0],
								refLocation[1],
								possibleSources);

				walker.addWalkerDiagnostic(
						ParseDiagnostic.Severity.SEVERE_WARNING,
						diagCode,
						diagMessage,
						refLocation[0],
						refLocation[1],
						walker.getClass().getSimpleName(),
						null,
						columnName,
						true,
						"ast-walk",
						null,
						null);
			} else {
				String resolvedSourceRef = normalizeTableRef(sourceRefs.get(0));
				columnRefs.set(index, cloneReferenceWithResolvedTableRef(columnRefObj, resolvedSourceRef));
				materializeResolvedUnqualifiedReference(
						unresolvedColumnMap,
						localTableCollection,
						localTableAliasMap,
						resolvedSourceRef,
						columnName);
			}
		}
	}

	public String resolvePreferredDeleteTargetForUnqualified(
			String deleteTargetTableRef,
			HashMap<String, Object> tableAliasCollection,
			HashMap<String, Object> tableCollection,
			ArrayList<String> sourceRefs) {
		if (deleteTargetTableRef == null || deleteTargetTableRef.isBlank()) {
			return null;
		}

		String resolvedTarget = walker.resolveAliasToTableName(deleteTargetTableRef, tableAliasCollection);
		if (resolvedTarget == null || resolvedTarget.isBlank()) {
			resolvedTarget = deleteTargetTableRef;
		}
		if (resolvedTarget == null || resolvedTarget.isBlank()) {
			return null;
		}

		String normalizedTarget = normalizeTableRef(resolvedTarget);

		if (sourceRefs != null) {
			for (String sourceRef : sourceRefs) {
				if (sourceRef != null && normalizeTableRef(sourceRef).equals(normalizedTarget)) {
					return sourceRef;
				}
			}
		}

		if (tableCollection != null) {
			for (String tableRef : tableCollection.keySet()) {
				if (tableRef != null && normalizeTableRef(tableRef).equals(normalizedTarget)) {
					return tableRef;
				}
			}
		}

		return null;
	}

	public ArrayList<String> collectUnqualifiedSourceReferences(
			String columnName,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> queryCollection,
			HashMap<String, Object> tableAliasCollection) {
		LinkedHashSet<String> visibleTableSources = new LinkedHashSet<String>();
		LinkedHashSet<String> visibleQuerySources = new LinkedHashSet<String>();
		LinkedHashSet<String> queryExactSources = new LinkedHashSet<String>();
		LinkedHashSet<String> queryWildcardOnlySources = new LinkedHashSet<String>();

		if (tableCollection != null) {
			for (String tableRef : tableCollection.keySet()) {
				addIgnoringCase(visibleTableSources, tableRef);
			}
		}

		if (tableAliasCollection != null && !tableAliasCollection.isEmpty()) {
			for (Object mappedSourceObj : tableAliasCollection.values()) {
				if (!(mappedSourceObj instanceof String mappedSource) || mappedSource.isBlank()) {
					continue;
				}

				if (isQuerySourceReference(mappedSource) || isTableFunctionSourceReference(mappedSource)) {
					addIgnoringCase(visibleQuerySources, mappedSource);
					continue;
				}

				String resolvedTableRef = walker.resolveAliasToTableName(mappedSource, tableAliasCollection);
				if (resolvedTableRef != null && !resolvedTableRef.isBlank()) {
					addIgnoringCase(visibleTableSources, resolvedTableRef);
				} else {
					addIgnoringCase(visibleTableSources, mappedSource);
				}
			}
		}

		ArrayList<String> dictionaryMatches = walker.collectSourceReferencesForColumn(
				columnName,
				tableCollection,
				queryCollection);
		LinkedHashSet<String> matchedTableSources = new LinkedHashSet<String>();
		for (String sourceRef : dictionaryMatches) {
			if (isQuerySourceReference(sourceRef) || isTableFunctionSourceReference(sourceRef)) {
				boolean allowDirectQuerySource = isValuesSourceReference(sourceRef)
						|| isTableFunctionSourceReference(sourceRef);
				if (allowDirectQuerySource) {
					addIgnoringCase(visibleQuerySources, sourceRef);
				} else {
					boolean queryIsVisibleAliasSource = false;
					for (String visibleQueryRef : visibleQuerySources) {
						if (visibleQueryRef != null && visibleQueryRef.equalsIgnoreCase(sourceRef)) {
							queryIsVisibleAliasSource = true;
							break;
						}
					}
					if (queryIsVisibleAliasSource) {
						addIgnoringCase(visibleQuerySources, sourceRef);
					}
				}
			} else {
				addIgnoringCase(visibleTableSources, sourceRef);
				addIgnoringCase(matchedTableSources, sourceRef);
			}
		}

		for (String querySourceRef : visibleQuerySources) {
			if (isTableFunctionSourceReference(querySourceRef)) {
				// Table functions are runtime-shaped sources; treat as query wildcard providers.
				addIgnoringCase(queryWildcardOnlySources, querySourceRef);
				continue;
			}

			if (querySourceHasExactColumn(querySourceRef, columnName, queryCollection)) {
				addIgnoringCase(queryExactSources, querySourceRef);
				continue;
			}

			if (isWildcardBackedQueryCandidate(querySourceRef, queryCollection)) {
				addIgnoringCase(queryWildcardOnlySources, querySourceRef);
			}
		}

		// Table-function sources are query-like for unqualified handling; keep them out of table bucket.
		visibleTableSources.removeIf(this::isTableFunctionSourceReference);

		if (queryExactSources.size() >= 2) {
			return new ArrayList<String>(queryExactSources);
		}

		if (queryExactSources.size() == 1) {
			if (!matchedTableSources.isEmpty()) {
				ArrayList<String> mixed = new ArrayList<String>();
				mixed.addAll(matchedTableSources);
				mixed.addAll(queryExactSources);
				return mixed;
			}
			return new ArrayList<String>(queryExactSources);
		}

		if (queryWildcardOnlySources.size() >= 2) {
			return new ArrayList<String>(queryWildcardOnlySources);
		}

		if (queryWildcardOnlySources.size() == 1) {
			if (!visibleTableSources.isEmpty()) {
				ArrayList<String> mixed = new ArrayList<String>();
				mixed.addAll(queryWildcardOnlySources);
				mixed.addAll(visibleTableSources);
				return mixed;
			}

			return new ArrayList<String>(queryWildcardOnlySources);
		}

		if (visibleTableSources.size() >= 2) {
			LinkedHashSet<String> orderedTableSources = new LinkedHashSet<String>();
			for (String sourceRef : dictionaryMatches) {
				if (isQuerySourceReference(sourceRef)) {
					continue;
				}
				addIgnoringCase(orderedTableSources, sourceRef);
			}
			for (String tableRef : visibleTableSources) {
				addIgnoringCase(orderedTableSources, tableRef);
			}
			return new ArrayList<String>(orderedTableSources);
		}

		if (visibleTableSources.size() == 1) {
			return new ArrayList<String>(visibleTableSources);
		}

		return new ArrayList<String>();
	}

	public void addIgnoringCase(Set<String> bucket, String candidate) {
		if (bucket == null || candidate == null || candidate.isBlank()) {
			return;
		}

		for (String existing : bucket) {
			if (existing != null && existing.equalsIgnoreCase(candidate)) {
				return;
			}
		}

		bucket.add(candidate);
	}

	public String resolveAliasToQuerySourceFromAliasMap(
			String aliasRef,
			HashMap<String, Object> tableAliasCollection) {
		if (aliasRef == null || aliasRef.isBlank() || tableAliasCollection == null || tableAliasCollection.isEmpty()) {
			return null;
		}

		for (Map.Entry<String, Object> aliasEntry : tableAliasCollection.entrySet()) {
			String aliasKey = aliasEntry.getKey();
			if (aliasKey == null || !aliasKey.equalsIgnoreCase(aliasRef)) {
				continue;
			}

			Object mappedSourceObj = aliasEntry.getValue();
			if (!(mappedSourceObj instanceof String mappedSource) || !isQuerySourceReference(mappedSource)) {
				return null;
			}

			if (mappedSource.startsWith("def_")) {
				return mappedSource.substring("def_".length());
			}
			return mappedSource;
		}

		if (isQuerySourceReference(aliasRef)) {
			return aliasRef.startsWith("def_") ? aliasRef.substring("def_".length()) : aliasRef;
		}

		return null;
	}

	public boolean hasOnlyQueryBackedAliasSources(HashMap<String, Object> tableAliasCollection) {
		if (tableAliasCollection == null || tableAliasCollection.isEmpty()) {
			return false;
		}

		int queryAliasCount = 0;
		for (Object mappedSourceObj : tableAliasCollection.values()) {
			if (!(mappedSourceObj instanceof String mappedSource) || mappedSource.isBlank()) {
				continue;
			}
			queryAliasCount++;
			if (!isQuerySourceReference(mappedSource)) {
				return false;
			}
		}

		return queryAliasCount > 1;
	}

	public void emitUnqualifiedNotFoundInQueryAliasFatal(
			String columnName,
			Integer[] refLocation,
			HashMap<String, Object> tableAliasCollection) {
		if (columnName == null || columnName.isBlank()) {
			return;
		}

		Integer line = (refLocation != null) ? refLocation[0] : null;
		Integer character = (refLocation != null) ? refLocation[1] : null;
		if (line == null || character == null) {
			line = 1;
			character = 1;
		}

		// Prevent duplicate/semi-contradictory signaling for the same unresolved reference.
		suppressedAmbiguousUnqualifiedKeys.add(buildUnqualifiedSuppressionKey(columnName, line, character));

		ArrayList<String> queryAliases = new ArrayList<String>();
		if (tableAliasCollection != null && !tableAliasCollection.isEmpty()) {
			for (Map.Entry<String, Object> aliasEntry : tableAliasCollection.entrySet()) {
				String aliasKey = aliasEntry.getKey();
				Object mappedSourceObj = aliasEntry.getValue();
				if (aliasKey == null || !(mappedSourceObj instanceof String mappedSource)) {
					continue;
				}
				if (!isQuerySourceReference(mappedSource)) {
					continue;
				}
				queryAliases.add(aliasKey);
			}
		}

		String queryAliasList = queryAliases.isEmpty() ? "[]" : queryAliases.toString();
		String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES);
		String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES);
		String diagMessage = (diagTemplate == null)
				? String.format(
						"Unqualified column '%s' at (l:%s c:%s) was not found in output interface of any visible query alias %s.",
						columnName,
						line,
						character,
						queryAliasList)
				: String.format(diagTemplate,
						columnName,
						line,
						character,
						queryAliasList);

		walker.addWalkerFatal(
				diagCode,
				diagMessage,
				line,
				character,
				columnName);
	}

	public String buildUnqualifiedSuppressionKey(String columnName, Integer line, Integer character) {
		String normalizedColumn = (columnName == null) ? "" : columnName.trim().toLowerCase();
		String normalizedLine = String.valueOf(line == null ? -1 : line);
		String normalizedCharacter = String.valueOf(character == null ? -1 : character);
		return normalizedColumn + ":" + normalizedLine + ":" + normalizedCharacter;
	}

	public boolean shouldSuppressAmbiguousUnqualifiedDiagnostic(String columnName, Integer[] refLocation) {
		if (refLocation == null || refLocation.length < 2) {
			return false;
		}
		String suppressionKey = buildUnqualifiedSuppressionKey(columnName, refLocation[0], refLocation[1]);
		return suppressedAmbiguousUnqualifiedKeys.contains(suppressionKey);
	}

	public boolean hasUnqualifiedUnknownWithMultipleViableSources(
			HashMap<String, Object> unresolvedCollection,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> queryCollection,
			HashMap<String, Object> tableAliasCollection) {
		if (unresolvedCollection == null || unresolvedCollection.isEmpty()) {
			return false;
		}

		for (String unresolvedKey : unresolvedCollection.keySet()) {
			if (unresolvedKey == null || unresolvedKey.contains(".")) {
				continue;
			}

			ArrayList<String> sourceRefs = collectUnqualifiedSourceReferences(
					unresolvedKey,
					tableCollection,
					queryCollection,
					tableAliasCollection);
			if (sourceRefs.size() > 1) {
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> collectVisibleQuerySourceCollection(HashMap<String, Object> tableAliasCollection) {
		HashMap<String, Object> visibleQuerySources = new HashMap<String, Object>();
		if (tableAliasCollection == null || tableAliasCollection.isEmpty()) {
			return visibleQuerySources;
		}

		for (Object mappedSourceObj : tableAliasCollection.values()) {
			if (!(mappedSourceObj instanceof String mappedSource) || !isQuerySourceReference(mappedSource)) {
				continue;
			}

			Object querySourceObj = walker.symbolTable.get(mappedSource);
			if (querySourceObj instanceof HashMap<?, ?>) {
				visibleQuerySources.put(mappedSource, querySourceObj);
				continue;
			}

			Object queryDefinitionObj = findInCurrentOrAncestorSymbolTables("def_" + mappedSource);
			if (queryDefinitionObj instanceof HashMap<?, ?>) {
				visibleQuerySources.put(mappedSource, queryDefinitionObj);
				continue;
			}

			Object queryDictionaryObj = walker.queryColumnDictionaryMap.get(mappedSource);
			if (queryDictionaryObj instanceof HashMap<?, ?>) {
				visibleQuerySources.put(mappedSource, queryDictionaryObj);
			}
		}

		return visibleQuerySources;
	}

	@SuppressWarnings("unchecked")
	public boolean isWildcardBackedQueryCandidate(String queryRef, HashMap<String, Object> queryCollection) {
		if (queryRef == null || !isQuerySourceReference(queryRef)) {
			return false;
		}

		if (queryCollection != null) {
			Object queryObj = queryCollection.get(queryRef);
			if (queryObj instanceof Map<?, ?> queryMap
					&& ((Map<String, Object>) queryMap).containsKey("*")) {
				return true;
			}
		}

		Object queryDictionaryObj = walker.queryColumnDictionaryMap.get(queryRef);
		if (queryDictionaryObj instanceof Map<?, ?> queryDictionary
				&& ((Map<String, Object>) queryDictionary).containsKey("*")) {
			return true;
		}

		return hasWildcardInQueryOutputInterface(queryRef);
	}

	@SuppressWarnings("unchecked")
	public boolean querySourceCanProvideColumn(
			String queryRef,
			String columnName,
			HashMap<String, Object> queryCollection) {
		if (queryRef == null || !isQuerySourceReference(queryRef)) {
			return false;
		}

		if (isWildcardBackedQueryCandidate(queryRef, queryCollection)) {
			return true;
		}

		if (queryCollection != null) {
			Object queryObj = queryCollection.get(queryRef);
			if (queryObj instanceof Map<?, ?> queryMap
					&& containsKeyIgnoreCase((Map<String, Object>) queryMap, columnName)) {
				return true;
			}
		}

		Object queryDictionaryObj = walker.queryColumnDictionaryMap.get(queryRef);
		if (queryDictionaryObj instanceof Map<?, ?> queryDictionary
				&& containsKeyIgnoreCase((Map<String, Object>) queryDictionary, columnName)) {
			return true;
		}

		return hasColumnInQueryOutputInterface(queryRef, columnName);
	}

	@SuppressWarnings("unchecked")
	public boolean querySourceHasExactColumn(
			String queryRef,
			String columnName,
			HashMap<String, Object> queryCollection) {
		if (queryRef == null || !isQuerySourceReference(queryRef) || columnName == null || columnName.isBlank()) {
			return false;
		}

		if (isTableFunctionSourceReference(queryRef)) {
			return false;
		}

		if (queryCollection != null) {
			Object queryObj = queryCollection.get(queryRef);
			if (queryObj instanceof Map<?, ?> queryMap
					&& containsKeyIgnoreCase((Map<String, Object>) queryMap, columnName)) {
				return true;
			}
		}

		Object queryDictionaryObj = walker.queryColumnDictionaryMap.get(queryRef);
		if (queryDictionaryObj instanceof Map<?, ?> queryDictionary
				&& containsKeyIgnoreCase((Map<String, Object>) queryDictionary, columnName)) {
			return true;
		}

		return hasColumnInQueryOutputInterface(queryRef, columnName);
	}

	public boolean isQuerySourceReference(String sourceRef) {
		if (sourceRef == null || sourceRef.isBlank()) {
			return false;
		}
		String normalizedSourceRef = sourceRef;
		if (normalizedSourceRef.startsWith("def_")) {
			normalizedSourceRef = normalizedSourceRef.substring("def_".length());
		}
		return normalizedSourceRef.startsWith(MUMBLE_QUERY_KEY)
				|| normalizedSourceRef.startsWith(MUMBLE_UNION_KEY)
				|| normalizedSourceRef.startsWith(MUMBLE_INTERSECT_KEY)
				|| normalizedSourceRef.startsWith(MUMBLE_VALUES_KEY)
				|| normalizedSourceRef.startsWith(MUMBLE_DELETE_KEY)
				|| MUMBLE_VALUES_KEY.equals(normalizedSourceRef);
	}

	public boolean isValuesSourceReference(String sourceRef) {
		if (sourceRef == null || sourceRef.isBlank()) {
			return false;
		}

		String normalizedSourceRef = sourceRef;
		if (normalizedSourceRef.startsWith("def_")) {
			normalizedSourceRef = normalizedSourceRef.substring("def_".length());
		}

		return normalizedSourceRef.startsWith(MUMBLE_VALUES_KEY)
				|| MUMBLE_VALUES_KEY.equals(normalizedSourceRef);
	}

	public boolean isTableFunctionSourceReference(String sourceRef) {
		if (sourceRef == null || sourceRef.isBlank()) {
			return false;
		}

		String normalizedSourceRef = sourceRef;
		if (normalizedSourceRef.startsWith("def_")) {
			normalizedSourceRef = normalizedSourceRef.substring("def_".length());
		}

		return tableFunctionSourceRefs.contains(normalizedSourceRef.toLowerCase());
	}

	@SuppressWarnings("unchecked")
	public boolean hasColumnInQueryOutputInterface(String queryKey, String columnName) {
		if (queryKey == null || queryKey.isBlank() || columnName == null || columnName.isBlank()) {
			return false;
		}

		Object queryDefObj = getQueryDefinitionSymbol(queryKey);
		if (!(queryDefObj instanceof Map<?, ?> queryDefMap)) {
			return false;
		}

		Object interfaceObj = ((Map<String, Object>) queryDefMap).get(MUMBLE_INTERFACE_KEY);
		if (!(interfaceObj instanceof Map<?, ?> interfaceMap)) {
			return false;
		}

		return containsKeyIgnoreCase((Map<String, Object>) interfaceMap, columnName);
	}

	public boolean containsKeyIgnoreCase(Map<String, Object> map, String key) {
		if (map == null || map.isEmpty() || key == null) {
			return false;
		}

		if (map.containsKey(key)) {
			return true;
		}

		for (String existingKey : map.keySet()) {
			if (existingKey != null && existingKey.equalsIgnoreCase(key)) {
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> extractExplicitQualifiedUnknownEntries(
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> localInterface,
			Object filtersList,
			Object groupedByList,
			Object orderedByList) {
		HashMap<String, Object> explicitQualified = new HashMap<String, Object>();
		if (unresolvedColumnMap == null || unresolvedColumnMap.isEmpty()) {
			return explicitQualified;
		}

		HashSet<String> explicitQualifiedKeys = new HashSet<String>();

		if (localInterface != null) {
			for (Object refsObj : localInterface.values()) {
				if (!(refsObj instanceof ArrayList<?> refs)) {
					continue;
				}
				for (Object refObj : refs) {
					String refName = walker.extractReferenceNameFromInterfaceEntry(refObj);
					String refTable = walker.extractReferenceTableRefFromInterfaceEntry(refObj);
					if (refName != null && refTable != null && !"*".equals(refTable)) {
						String qualifiedKey = refTable + "." + refName;
						if (unresolvedColumnMap.containsKey(qualifiedKey)) {
							explicitQualifiedKeys.add(qualifiedKey);
						} else if (unresolvedColumnMap.containsKey(refName)) {
							explicitQualifiedKeys.add(refName);
						}
					}
				}
			}
		}

		collectExplicitQualifiedUnknownKeysFromRefList(explicitQualifiedKeys, unresolvedColumnMap, filtersList);
		collectExplicitQualifiedUnknownKeysFromRefList(explicitQualifiedKeys, unresolvedColumnMap, groupedByList);
		collectExplicitQualifiedUnknownKeysFromRefList(explicitQualifiedKeys, unresolvedColumnMap, orderedByList);

		for (String qualifiedKey : explicitQualifiedKeys) {
			Object removed = unresolvedColumnMap.remove(qualifiedKey);
			if (removed != null) {
				explicitQualified.put(qualifiedKey, removed);
			}
		}

		return explicitQualified;
	}

	@SuppressWarnings("unchecked")
	public void collectExplicitQualifiedUnknownKeysFromRefList(
			HashSet<String> explicitQualifiedKeys,
			HashMap<String, Object> unresolvedColumnMap,
			Object refListObj) {
		if (!(refListObj instanceof ArrayList<?> refs)) {
			return;
		}

		for (Object refObj : refs) {
			if (!(refObj instanceof Map<?, ?> refMap)) {
				continue;
			}
			String refName = walker.extractReferenceNameFromInterfaceEntry(refMap);
			String refTableRef = walker.extractReferenceTableRefFromInterfaceEntry(refMap);
			if (refName != null
					&& refTableRef != null
					&& !"*".equals(refTableRef)) {
				String qualifiedKey = refTableRef + "." + refName;
				if (unresolvedColumnMap.containsKey(qualifiedKey)) {
					explicitQualifiedKeys.add(qualifiedKey);
				} else if (unresolvedColumnMap.containsKey(refName)) {
					explicitQualifiedKeys.add(refName);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void emitExplicitQualifiedUnknownDiagnostics(
			HashMap<String, Object> explicitQualifiedUnknownEntries,
			HashMap<String, Object> localInterface,
			Object filtersList,
			HashMap<String, Object> tableAliasCollection,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> scopedQueryCollection,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> unresolvedCollector) {
		if (explicitQualifiedUnknownEntries == null || explicitQualifiedUnknownEntries.isEmpty()) {
			return;
		}

		HashMap<String, String> explicitTableRefByColumn = new HashMap<String, String>();
		if (localInterface != null) {
			for (Object refsObj : localInterface.values()) {
				if (!(refsObj instanceof ArrayList<?> refs)) {
					continue;
				}
				for (Object refObj : refs) {
					String refName = walker.extractReferenceNameFromInterfaceEntry(refObj);
					String refTable = walker.extractReferenceTableRefFromInterfaceEntry(refObj);
					if (refName != null && refTable != null && !"*".equals(refTable)) {
						explicitTableRefByColumn.putIfAbsent(refName, refTable);
					}
				}
			}
		}

		if (filtersList instanceof ArrayList<?> filters) {
			for (Object filterObj : filters) {
				if (!(filterObj instanceof Map<?, ?> filterMap)) {
					continue;
				}
				String filterName = walker.extractReferenceNameFromInterfaceEntry(filterMap);
				String filterTableRef = walker.extractReferenceTableRefFromInterfaceEntry(filterMap);
				if (filterName != null
						&& filterTableRef != null
						&& !"*".equals(filterTableRef)) {
					explicitTableRefByColumn.putIfAbsent(filterName, filterTableRef);
				}
			}
		}

		for (Map.Entry<String, Object> unknownEntry : explicitQualifiedUnknownEntries.entrySet()) {
			String unresolvedKey = unknownEntry.getKey();
			String columnName = unresolvedKey;
			String tableRef = null;
			if (unresolvedKey != null && unresolvedKey.contains(".")) {
				tableRef = unresolvedKey.substring(0, unresolvedKey.lastIndexOf('.'));
				columnName = unresolvedKey.substring(unresolvedKey.lastIndexOf('.') + 1);
			}
			if (tableRef == null) {
				tableRef = explicitTableRefByColumn.get(columnName);
			}
			if (tableRef == null) {
				continue;
			}

			String resolvedTableRef = walker.resolveAliasToTableName(tableRef, tableAliasCollection);
			String resolvedNonTableSourceRef = walker.resolveAliasToNonTableSourceQueryKey(tableRef, scopedQueryCollection);
			if (resolvedNonTableSourceRef == null) {
				resolvedNonTableSourceRef = resolveAliasToQuerySourceFromAliasMap(tableRef, tableAliasCollection);
			}
			boolean explicitQueryReference = resolvedNonTableSourceRef != null
					|| walker.isNonTableQuerySourceReference(resolvedTableRef);
			String allLocationsForEntry = walker.formatAllLocationsForEntry(unknownEntry.getValue());
			String allLocationsInline = walker.formatAllLocationsForEntryInline(unknownEntry.getValue());
			boolean hasMergedLocations = allLocationsForEntry != null
					&& allLocationsForEntry.startsWith("[")
					&& allLocationsForEntry.contains(",");

			if (explicitQueryReference) {
				String querySourceRef = (resolvedNonTableSourceRef != null)
						? resolvedNonTableSourceRef
						: resolvedTableRef;
				if ("*".equals(columnName)) {
					promoteQualifiedWildcardIntoQuerySource(querySourceRef, unknownEntry.getValue());
					continue;
				}
				Object queryDictionaryObj = walker.queryColumnDictionaryMap.get(querySourceRef);
				boolean foundInQueryInterface = queryDictionaryObj instanceof Map<?, ?>
						&& containsKeyIgnoreCase((Map<String, Object>) queryDictionaryObj, columnName);
				if (!foundInQueryInterface && hasColumnInQueryOutputInterface(querySourceRef, columnName)) {
					foundInQueryInterface = true;
				}
				if (!foundInQueryInterface && hasWildcardInQueryOutputInterface(querySourceRef)) {
					foundInQueryInterface = true;
				}
				if (foundInQueryInterface) {
					mergeExplicitQualifiedUnknownIntoSourceQueryDictionary(
							querySourceRef,
							columnName,
							unknownEntry.getValue());
					continue;
				}
				if (!foundInQueryInterface) {
					Integer[] refLocation = walker.getLineAndCharacterFromEntry(unknownEntry.getValue());
					if (refLocation[0] == null || refLocation[1] == null) {
						refLocation = walker.getFirstEntryLineAndCharacter(explicitQualifiedUnknownEntries);
					}

					String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS);
					String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS);
					String queryAliasRef = (resolvedTableRef != null && !resolvedTableRef.equals(tableRef)) ? tableRef : querySourceRef;
					String diagMessage = (diagTemplate == null)
							? String.format(
									"Qualified column '%s' at (l:%s c:%s) was not found in output interface of query alias '%s'.",
									columnName,
									refLocation[0],
									refLocation[1],
									queryAliasRef)
							: String.format(diagTemplate,
									columnName,
									refLocation[0],
									refLocation[1],
									queryAliasRef);
					if (hasMergedLocations) {
						diagMessage = diagMessage + " Locations: " + allLocationsForEntry;
					}

					walker.addWalkerFatal(
							diagCode,
							diagMessage,
							refLocation[0],
							refLocation[1],
							unresolvedKey);
				}
				continue;
			}

			String indicatedSourceRef = (resolvedNonTableSourceRef != null)
					? resolvedNonTableSourceRef
					: (resolvedTableRef != null ? resolvedTableRef : tableRef);

			HashMap<String, Object> indicatedTableDictionary = walker.getTableDictionaryForReference(
					resolvedTableRef,
					tableCollection);
			if (indicatedTableDictionary != null) {
				// Explicitly qualified table reference is valid when source table/alias exists.
				// Materialize the resolved column on that source so table dictionaries remain complete.
				walker.mergeResolvedColumnIntoDictionary(indicatedTableDictionary, columnName, unknownEntry.getValue());
				continue;
			}

			boolean isSubstitutionQualifiedReference = false;
			if (unknownEntry.getValue() instanceof Map<?, ?> unknownEntryMapObj) {
				Map<String, Object> unknownEntryMap = (Map<String, Object>) unknownEntryMapObj;
				Object columnObj = unknownEntryMap.get(MUMBLE_COLUMN_KEY);
				if (columnObj != null) {
					String substitutionType = walker.extractSubstitutionTypeFromInterfaceEntry(columnObj);
					isSubstitutionQualifiedReference = substitutionType != null
							&& (MUMBLE_COLUMN_KEY.equals(substitutionType)
									|| MUMBLE_PREDICAND_KEY.equals(substitutionType));
				}
			}

			if (!isSubstitutionQualifiedReference
					&& localCurrentQueryDictionary != null
					&& localCurrentQueryDictionary.containsKey(columnName)) {
				// Regular qualified columns selected into the interface are validated in the
				// downstream interface-resolution pass; avoid duplicate fatals here.
				continue;
			}

			Integer[] refLocation = walker.getLineAndCharacterFromEntry(unknownEntry.getValue());
			if (refLocation[0] == null || refLocation[1] == null) {
				refLocation = walker.getFirstEntryLineAndCharacter(explicitQualifiedUnknownEntries);
			}

			if (unresolvedCollector != null) {
				String qualifiedStorageKey = (unresolvedKey != null && unresolvedKey.contains("."))
						? unresolvedKey
						: tableRef + "." + columnName;
				HashMap<String, Object> singleEntry = new HashMap<String, Object>();
				singleEntry.put(qualifiedStorageKey, unknownEntry.getValue());
				walker.mergeUnknownEntries(unresolvedCollector, singleEntry);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void mergeExplicitQualifiedUnknownIntoSourceQueryDictionary(
			String querySourceRef,
			String columnName,
			Object unknownEntryValue) {
		if (querySourceRef == null || querySourceRef.isBlank()
				|| columnName == null || columnName.isBlank()) {
			return;
		}

		String sourceQueryKey = querySourceRef;
		Object sourceDictionaryObj = walker.queryColumnDictionaryMap.get(sourceQueryKey);
		if (sourceDictionaryObj == null && sourceQueryKey.startsWith("def_")) {
			sourceQueryKey = sourceQueryKey.substring("def_".length());
			sourceDictionaryObj = walker.queryColumnDictionaryMap.get(sourceQueryKey);
		}
		if (sourceDictionaryObj == null) {
			String defSourceKey = sourceQueryKey.startsWith("def_") ? sourceQueryKey : "def_" + sourceQueryKey;
			sourceDictionaryObj = walker.queryColumnDictionaryMap.get(defSourceKey);
		}
		if (!(sourceDictionaryObj instanceof Map<?, ?> sourceDictionaryMapObj)) {
			return;
		}
		Map<String, Object> sourceQueryDictionary = (Map<String, Object>) sourceDictionaryMapObj;

		Object promotedRefs = unknownEntryValue;
		if (unknownEntryValue instanceof Map<?, ?> unknownEntryMapObj) {
			Object locations = ((Map<String, Object>) unknownEntryMapObj).get("locations");
			if (locations != null) {
				promotedRefs = locations;
			}
		}

		if (promotedRefs == null) {
			return;
		}

		if (promotedRefs instanceof ArrayList<?> promotedListObj) {
			promotedRefs = new ArrayList<Object>((ArrayList<Object>) promotedListObj);
		}

		String existingColumnKey = null;
		for (String existingKey : sourceQueryDictionary.keySet()) {
			if (existingKey != null && existingKey.equalsIgnoreCase(columnName)) {
				existingColumnKey = existingKey;
				break;
			}
		}

		if (existingColumnKey != null) {
			Object existingRefs = sourceQueryDictionary.get(existingColumnKey);
			sourceQueryDictionary.put(existingColumnKey, mergeReferenceCollections(existingRefs, promotedRefs));
		} else {
			sourceQueryDictionary.put(columnName, promotedRefs);
		}
	}

	@SuppressWarnings("unchecked")
	public void promoteQualifiedWildcardIntoQuerySource(String querySourceRef, Object unknownEntryValue) {
		if (querySourceRef == null || querySourceRef.isBlank()) {
			return;
		}

		mergeExplicitQualifiedUnknownIntoSourceQueryDictionary(querySourceRef, "*", unknownEntryValue);

		Object queryDefObj = getQueryDefinitionSymbol(querySourceRef);
		if (!(queryDefObj instanceof Map<?, ?> queryDefMapObj)) {
			return;
		}

		Map<String, Object> queryDefMap = (Map<String, Object>) queryDefMapObj;
		Object interfaceObj = queryDefMap.get(MUMBLE_INTERFACE_KEY);
		if (!(interfaceObj instanceof Map<?, ?> interfaceMapObj)) {
			return;
		}

		Map<String, Object> interfaceMap = (Map<String, Object>) interfaceMapObj;
		if (!containsKeyIgnoreCase(interfaceMap, "*")) {
			interfaceMap.put("*", "wildcard");
		}
	}

	public Object getQueryDefinitionSymbol(String queryKey) {
		if (queryKey == null || queryKey.isBlank()) {
			return null;
		}

		Object directObj = walker.symbolTable.get(queryKey);
		if (directObj instanceof Map<?, ?> directMap
				&& ((Map<?, ?>) directMap).containsKey(MUMBLE_INTERFACE_KEY)) {
			return directObj;
		}

		if (queryKey.startsWith("def_")) {
			return directObj;
		}

		String cteScopeRef = resolveCteScopeReference(queryKey, null);
		if (cteScopeRef != null && !cteScopeRef.isBlank()) {
			Object cteScopeObj = findInCurrentOrAncestorSymbolTables(cteScopeRef);
			if (cteScopeObj instanceof Map<?, ?> cteScopeMap
					&& ((Map<?, ?>) cteScopeMap).containsKey(MUMBLE_INTERFACE_KEY)) {
				return cteScopeObj;
			}

			Object cteDefScopeObj = findInCurrentOrAncestorSymbolTables("def_" + cteScopeRef);
			if (cteDefScopeObj != null) {
				return cteDefScopeObj;
			}
		}

		return findInCurrentOrAncestorSymbolTables("def_" + queryKey);
	}

	/**
	 * Searches the current symbol table and all ancestor symbol tables (in order from
	 * closest to farthest) for the given key. Returns the first non-null value found,
	 * or null if not found anywhere in the visible scope chain.
	 */
	@SuppressWarnings("unchecked")
	public Object findInCurrentOrAncestorSymbolTables(String key) {
		if (key == null || key.isBlank()) {
			return null;
		}
		Object found = walker.symbolTable.get(key);
		if (found != null) {
			return found;
		}
		for (Map<String, Object> ancestor : getAncestorSymbolTables()) {
			found = ancestor.get(key);
			if (found != null) {
				return found;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getCurrentTableAliasMap() {
		Object tableAliasObj = walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY);
		if (tableAliasObj instanceof HashMap<?, ?> tableAliasMapObj) {
			return (HashMap<String, Object>) tableAliasMapObj;
		}
		return null;
	}

	public String resolveCteOrQueryScopeReference(String sourceRef, HashMap<String, Object> tableAliasMap) {
		String cteScopeReference = resolveCteScopeReference(sourceRef, tableAliasMap);
		if (cteScopeReference != null && !cteScopeReference.isBlank()) {
			return cteScopeReference;
		}

		if (tableAliasMap == null || sourceRef == null || sourceRef.isBlank()) {
			return null;
		}

		Object mappedSourceObj = tableAliasMap.get(sourceRef);
		if (!(mappedSourceObj instanceof String mappedSource) || mappedSource.isBlank()) {
			return null;
		}

		if (isQuerySourceReference(mappedSource)) {
			return mappedSource;
		}

		return null;
	}

	public String resolveCteOrQueryScopeReferenceInVisibleScopes(String sourceRef) {
		HashMap<String, Object> currentAliasMap = getCurrentTableAliasMap();
		String localScope = resolveCteOrQueryScopeReference(sourceRef, currentAliasMap);
		if (localScope != null && !localScope.isBlank()) {
			return localScope;
		}

		for (Map<String, Object> ancestorSymbols : getAncestorSymbolTables()) {
			HashMap<String, Object> ancestorAliasMap = getTableAliasMap(ancestorSymbols);
			String inheritedScope = resolveCteOrQueryScopeReference(sourceRef, ancestorAliasMap);
			if (inheritedScope != null && !inheritedScope.isBlank()) {
				return inheritedScope;
			}
		}

		return null;
	}

	public String resolveCteOrExistingQueryScopeInVisibleScopes(String sourceRef) {
		String resolvedScope = resolveCteOrQueryScopeReferenceInVisibleScopes(sourceRef);
		if (resolvedScope != null && !resolvedScope.isBlank()) {
			return resolvedScope;
		}

		HashMap<String, Object> currentAliasMap = getCurrentTableAliasMap();
		resolvedScope = resolveExistingQueryScopeFromAliasMap(sourceRef, currentAliasMap);
		if (resolvedScope != null) {
			return resolvedScope;
		}

		for (Map<String, Object> ancestorSymbols : getAncestorSymbolTables()) {
			HashMap<String, Object> ancestorAliasMap = getTableAliasMap(ancestorSymbols);
			resolvedScope = resolveExistingQueryScopeFromAliasMap(sourceRef, ancestorAliasMap);
			if (resolvedScope != null) {
				return resolvedScope;
			}
		}

		return null;
	}

	public String resolveExistingQueryScopeFromAliasMap(String sourceRef, HashMap<String, Object> aliasMap) {
		if (sourceRef == null || sourceRef.isBlank() || aliasMap == null || aliasMap.isEmpty()) {
			return null;
		}

		Object mappedObj = aliasMap.get(sourceRef);
		if (!(mappedObj instanceof String mappedRef) || mappedRef.isBlank()) {
			return null;
		}

		if (isQuerySourceReference(mappedRef)) {
			return mappedRef;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public void registerTableFunctionSourceReference(Map<String, Object> reference, String alias) {
		if (reference == null || !reference.containsKey(MUMBLE_TABLE_FUNCTION_KEY)) {
			return;
		}

		String functionRef = allocateTableFunctionSourceReference(reference);
		if (functionRef == null) {
			return;
		}

		walker.ensureTableDictionaryEntry(functionRef);
		tableFunctionSourceRefs.add(functionRef.toLowerCase());
		if (alias != null && !alias.isBlank()) {
			walker.collectTableAlias(alias, functionRef);
		}
	}

	@SuppressWarnings("unchecked")
	public String allocateTableFunctionSourceReference(Map<String, Object> reference) {
		Object tableFunctionObj = reference.get(MUMBLE_TABLE_FUNCTION_KEY);
		if (!(tableFunctionObj instanceof Map<?, ?> tableFunctionMapObj)) {
			return null;
		}

		Map<String, Object> tableFunctionMap = (Map<String, Object>) tableFunctionMapObj;
		Object functionNameObj = tableFunctionMap.get(MUMBLE_FUNCTION_NAME_KEY);
		String functionName = (functionNameObj instanceof String && !((String) functionNameObj).isBlank())
				? ((String) functionNameObj).toLowerCase()
				: "table_function";

		String normalizedFunctionName = functionName.replaceAll("[^a-zA-Z0-9_]", "_");
		if (normalizedFunctionName.isBlank()) {
			normalizedFunctionName = "table_function";
		}

		return normalizedFunctionName + tableFunctionSourceCount++;
	}

	public String findTopLevelValuesScopeKey() {
		String selectedKey = null;
		int highestIndex = -1;
		for (String symbolKey : walker.symbolTable.keySet()) {
			if (symbolKey == null) {
				continue;
			}

			String normalizedValuesKey = null;
			if (symbolKey.startsWith(MUMBLE_VALUES_KEY)) {
				normalizedValuesKey = symbolKey;
			} else if (symbolKey.startsWith("def_" + MUMBLE_VALUES_KEY)) {
				normalizedValuesKey = symbolKey.substring("def_".length());
			}

			if (normalizedValuesKey == null) {
				continue;
			}
			String suffix = normalizedValuesKey.substring(MUMBLE_VALUES_KEY.length());
			if (suffix.isEmpty()) {
				continue;
			}
			int keyIndex;
			try {
				keyIndex = Integer.parseInt(suffix);
			} catch (NumberFormatException ex) {
				continue;
			}
			if (keyIndex > highestIndex) {
				highestIndex = keyIndex;
				selectedKey = normalizedValuesKey;
			}
		}
		return selectedKey;
	}

	@SuppressWarnings("unchecked")
	public void wrapValuesScopeAsDefinition(String valuesScopeKey) {
		if (valuesScopeKey == null || valuesScopeKey.isBlank()) {
			return;
		}

		String definitionKey = "def_" + valuesScopeKey;
		if (walker.symbolTable.containsKey(definitionKey)) {
			return;
		}

		Object valuesScopeObj = walker.symbolTable.remove(valuesScopeKey);
		if (valuesScopeObj instanceof Map<?, ?>) {
			walker.symbolTable.put(definitionKey, valuesScopeObj);
		}
	}

	@SuppressWarnings("unchecked")
	public void addCurrentScopeValuesAliasMapping(String alias, String valuesScopeKey) {
		if (alias == null || alias.isBlank() || valuesScopeKey == null || valuesScopeKey.isBlank()) {
			return;
		}

		Object queryDictionaryObj = walker.symbolTable.get(MUMBLE_QUERY_DICTIONARY_KEY);
		HashMap<String, Object> queryDictionary;
		if (queryDictionaryObj instanceof HashMap<?, ?>) {
			queryDictionary = (HashMap<String, Object>) queryDictionaryObj;
		} else {
			queryDictionary = new HashMap<String, Object>();
			walker.symbolTable.put(MUMBLE_QUERY_DICTIONARY_KEY, queryDictionary);
		}

		Object sourceAliasNodeObj = queryDictionary.get(valuesScopeKey);
		HashMap<String, Object> sourceAliasNode;
		if (sourceAliasNodeObj instanceof HashMap<?, ?>) {
			sourceAliasNode = (HashMap<String, Object>) sourceAliasNodeObj;
		} else {
			sourceAliasNode = new HashMap<String, Object>();
			queryDictionary.put(valuesScopeKey, sourceAliasNode);
		}
		sourceAliasNode.put(alias, valuesScopeKey);
	}

	@SuppressWarnings("unchecked")
	public void sanitizeQueryDictionaryForGlobalExport(HashMap<String, Object> queryDictionary) {
		if (queryDictionary == null || queryDictionary.isEmpty()) {
			return;
		}

		ArrayList<String> transientKeysToRemove = new ArrayList<String>();
		for (Map.Entry<String, Object> entry : queryDictionary.entrySet()) {
			String entryKey = entry.getKey();
			Object entryValue = entry.getValue();
			if (entryKey == null || !(entryValue instanceof Map<?, ?> entryMapObj)) {
				continue;
			}

			if (!(entryKey.startsWith(MUMBLE_QUERY_KEY)
					|| entryKey.startsWith(MUMBLE_UNION_KEY)
					|| entryKey.startsWith(MUMBLE_INTERSECT_KEY)
					|| entryKey.startsWith(MUMBLE_VALUES_KEY))) {
				continue;
			}

			Map<String, Object> entryMap = (Map<String, Object>) entryMapObj;
			if (entryMap.isEmpty()) {
				transientKeysToRemove.add(entryKey);
				continue;
			}

			boolean onlyAliasStringMappings = true;
			for (Object subValue : entryMap.values()) {
				if (!(subValue instanceof String)) {
					onlyAliasStringMappings = false;
					break;
				}
			}

			if (onlyAliasStringMappings) {
				transientKeysToRemove.add(entryKey);
			}
		}

		for (String transientKey : transientKeysToRemove) {
			queryDictionary.remove(transientKey);
		}
	}

	public Boolean collectQuerySymbolTable(String hdr, String alias) {
		return collectQuerySymbolTable(hdr, alias, null);
	}

	public Boolean collectQuerySymbolTable(String hdr, String alias, Map<String, Object> definitionTarget) {
		String queryName = hdr + (walker.queryCount - 1);
		Map<String, Object> query = (Map<String, Object>)  walker.symbolTable.remove(queryName);
		if (query != null) {
			if (walker.currentStackLevel(SQLSelectParserParser.RULE_insert_source_primary) != null) {
				pruneInsertSourceSequenceFromNestedDefinitions(query);
			} else {
				query.remove(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY);
			}
			// add alias to query
			if (alias != null) {
				walker.collectTableAlias(alias, queryName);
			} else
				 walker.symbolTable.put(queryName, new HashMap<String, Object>());

			// propagate interface to outer layer of query
			Map<String, Object> hold = (Map<String, Object>)  walker.symbolTable.get(queryName);
			// Move unknowns to query
			Map<String, Object> unk = (Map<String, Object>)  walker.symbolTable.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);

			if (unk != null) {
				if (hold != null) {
					// move any other interface elements to query and empty unknowns
					Map<String, Object> interfac = (Map<String, Object>) query.get(MUMBLE_INTERFACE_KEY);
					if (interfac != null)
						for (String key : interfac.keySet()) {
							Object unkItem = unk.remove(key);
							if (unkItem != null)
								hold.put(key, unkItem);
						}
				}

				// if any unknowns left, put them back into table
				if (unk.size() > 0)
					 walker.symbolTable.put(MUMBLE_UNRESOLVED_COLUMN_KEY, unk);
			}

			// Always publish query definition in the parent symbol table.
			walker.symbolTable.put("def_" + queryName, query);

			// WITH-specific cte_list tracks alias -> query scope reference (without def_ prefix).
			if (definitionTarget != null && alias != null) {
				definitionTarget.put(alias, queryName);
			}
			return true;
		} else
			return false;
	}

	@SuppressWarnings("unchecked")
	public void pruneInsertSourceSequenceFromNestedDefinitions(Map<String, Object> scopeMap) {
		if (scopeMap == null || scopeMap.isEmpty()) {
			return;
		}

		Object currentSequence = scopeMap.get(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY);
		pruneInsertSourceSequenceRecursive(scopeMap);
		if (currentSequence != null) {
			scopeMap.put(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY, currentSequence);
		}
	}

	@SuppressWarnings("unchecked")
	public void pruneInsertSourceSequenceRecursive(Map<String, Object> map) {
		if (map == null || map.isEmpty()) {
			return;
		}

		map.remove(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY);

		for (Object value : map.values()) {
			if (value instanceof Map<?, ?> valueMapObj) {
				pruneInsertSourceSequenceRecursive((Map<String, Object>) valueMapObj);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void populateInsertTargetColumnsFromTargetSubtree(
			String insertTargetTableRef,
			Map<String, Object> insertColumns,
			SQLSelectParserParser.Insert_target_table_primaryContext ctx) {
		if (insertTargetTableRef == null || insertTargetTableRef.isBlank()) {
			return;
		}
		if (insertColumns == null || insertColumns.isEmpty()) {
			return;
		}

		HashMap<String, Object> currentTableDictionary = walker.getCurrentTableDictionary();
		HashMap<String, Object> targetTableDictionary = ensureTableDictionaryEntry(currentTableDictionary, insertTargetTableRef);

		Object queryDictionaryObj = walker.symbolTable.get(MUMBLE_QUERY_DICTIONARY_KEY);
		HashMap<String, Object> queryDictionary = (queryDictionaryObj instanceof HashMap<?, ?> queryDictionaryMapObj)
				? (HashMap<String, Object>) queryDictionaryMapObj
				: new HashMap<String, Object>();
		walker.symbolTable.put(MUMBLE_QUERY_DICTIONARY_KEY, queryDictionary);

		HashMap<String, ArrayList<Object>> tokenRefsByColumn = new HashMap<String, ArrayList<Object>>();
		if (ctx != null && ctx.column_reference_list() != null) {
			for (SQLSelectParserParser.Column_referenceContext colCtx : ctx.column_reference_list().column_reference()) {
				if (colCtx == null || colCtx.name == null || colCtx.getStart() == null) {
					continue;
				}
				String columnName = colCtx.name.getText();
				if (columnName == null || columnName.isBlank()) {
					continue;
				}
				ArrayList<Object> refs = tokenRefsByColumn.get(columnName);
				if (refs == null) {
					refs = new ArrayList<Object>();
					tokenRefsByColumn.put(columnName, refs);
				}
				String tokenRef = colCtx.getStart().toString();
				if (tokenRef != null && !refs.contains(tokenRef)) {
					refs.add(tokenRef);
				}
			}
		}

		for (String insertColumnName : extractInsertColumnNames(insertColumns)) {
			if (insertColumnName == null || insertColumnName.isBlank()) {
				continue;
			}

			// Insert target columns belong to the INSERT target table by definition,
			// so they should not remain in unresolved-column diagnostics.
			removeUnresolvedColumnEntry(insertColumnName);
			removeUnresolvedColumnEntry(insertTargetTableRef + "." + insertColumnName);

			Object incomingRefs = tokenRefsByColumn.get(insertColumnName);
			if (!(incomingRefs instanceof ArrayList<?> incomingListObj)) {
				incomingRefs = new ArrayList<Object>();
			}

			Object existingTargetRefs = targetTableDictionary.get(insertColumnName);
			if (existingTargetRefs == null) {
				targetTableDictionary.put(insertColumnName, incomingRefs);
			} else {
				targetTableDictionary.put(insertColumnName, mergeReferenceCollections(existingTargetRefs, incomingRefs));
			}

			Object existingQueryRefs = queryDictionary.get(insertColumnName);
			if (existingQueryRefs == null) {
				queryDictionary.put(insertColumnName, incomingRefs);
			} else {
				queryDictionary.put(insertColumnName, mergeReferenceCollections(existingQueryRefs, incomingRefs));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void captureClauseDependencies(Map<String, Object> clauseSubMap, String symbolTableKey) {
		Object existing = walker.symbolTable.remove(symbolTableKey);
		ArrayList<Object> flatList;
		if (existing instanceof ArrayList<?>) {
			flatList = (ArrayList<Object>) existing;
		} else {
			flatList = new ArrayList<Object>();
		}

		if (clauseSubMap instanceof HashMap<?, ?>) {
			flattenSubTreeForClauseColumns((HashMap<String, Object>) clauseSubMap, flatList);
		}

		walker.symbolTable.put(symbolTableKey, flatList);
	}

	// Standardize the filters reference map into a flat map of column references and not the entire AST subtree
	// This is a recursive function that traverses the item subtree until it finds column references or substitution variables, 
	// which it adds to the column list with the alias as the key

	public void flattenSubTreeForClauseColumns(HashMap<String, Object> subTree, ArrayList<Object> columnList) {
		if (subTree == null) {
			return;
		}

		if (subTree.containsKey(MUMBLE_COLUMN_KEY)) {
			Object col = subTree.get(MUMBLE_COLUMN_KEY);
			if (!columnList.contains(col)) {
				columnList.add(col);
			}
		} else if (subTree.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
			Object subst = subTree.get(MUMBLE_SUBSTITUTION_KEY);
			if (subst instanceof HashMap) {
				HashMap<String, Object> substMap = (HashMap<String, Object>) subst;
				Object type = substMap.get("type");
				if (type != null && (MUMBLE_COLUMN_KEY.equals(type) || MUMBLE_PREDICAND_KEY.equals(type))) {
					if (!columnList.contains(subst)) {
						columnList.add(subst);
					}
				}
			}
		} else if (isQueryBackedSelectItemReference(subTree)) {
			String queryReference = resolveQueryReferenceFromSubTree(subTree);
			if (isQuerySourceReference(queryReference)) {
				HashMap<String, Object> queryEntry = new HashMap<String, Object>();
				queryEntry.put(MUMBLE_QUERY_KEY, queryReference);
				if (!columnList.contains(queryEntry)) {
					columnList.add(queryEntry);
				}
			}
			subTree.remove(MUMBLE_QUERY_KEY);
		} else {
			for (Object value : subTree.values()) {
				if (value instanceof HashMap<?, ?> valueMapObj) {
					flattenSubTreeForClauseColumns((HashMap<String, Object>) valueMapObj, columnList);
				} else if (value instanceof ArrayList<?> valueListObj) {
					for (Object listItem : (ArrayList<Object>) valueListObj) {
						if (listItem instanceof HashMap<?, ?> listMapObj) {
							flattenSubTreeForClauseColumns((HashMap<String, Object>) listMapObj, columnList);
						}
					}
				}
			}
		}
	}

	public String getSubqueryReferenceKey(Map<String, Object> symbols) {
		if (symbols == null || symbols.isEmpty()) {
			return null;
		}

		for (String key : symbols.keySet()) {
			if (isQuerySourceReference(key) && !key.startsWith("def_")) {
				return key;
			}
		}

		return null;
	}

}
