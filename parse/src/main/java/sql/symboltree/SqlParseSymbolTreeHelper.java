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
	private final ArrayDeque<String> dependentQueryContextStack = new ArrayDeque<>();

	public SqlParseSymbolTreeHelper(SqlASTWalkerHelper walkerHelper) {
		this.walker = walkerHelper;
	}

	// --- Constants shared with SqlParseEventWalker ---
	public static final String TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY = "_tmp_insert_source_select_sequence";
	public static final String INSERT_SOURCE_REF_KEY = "insert_source_ref";
	public static final String TEMP_INSERT_TARGET_COLUMN_LIST_LOCATION_KEY = "_tmp_insert_target_column_list_location";
	public static final String TEMP_DELETE_TARGET_TABLE_REF_KEY = "_tmp_delete_target_table_ref";
	public static final String TEMP_DELETE_TARGET_ALIAS_KEY = "_tmp_delete_target_alias";
	public static final String TEMP_UPDATE_NODEFROM_TARGET_KEY = "_tmp_update_nodefrom_target";
	public static final String TEMP_UPDATE_NODEFROM_TARGET_TABLE_COLLECTION_KEY = "_tmp_update_nodefrom_target_table_collection";
	public static final String TEMP_UPDATE_ASSIGNMENT_RHS_TOKENS_KEY = "_tmp_update_assignment_rhs_tokens";
	public static final String DERIVED_COLUMNS_HINTS_KEY = "derived_columns";
	private static final String TEMP_SET_OPERATION_INTERFACE_SUMMARY_MAP_KEY =
			SqlASTWalkerHelper.TEMP_SET_OPERATION_INTERFACE_SUMMARY_MAP_KEY;
	private static final String TEMP_QUERY_SET_OPERATION_SUMMARY_KEYS_MAP_KEY =
			SqlASTWalkerHelper.TEMP_QUERY_SET_OPERATION_SUMMARY_KEYS_MAP_KEY;
	private static final String TEMP_SET_OPERATION_OPERATOR_ANCHOR_LINE_KEY =
			SqlASTWalkerHelper.TEMP_SET_OPERATION_OPERATOR_ANCHOR_LINE_KEY;
	private static final String TEMP_SET_OPERATION_OPERATOR_ANCHOR_CHAR_KEY =
			SqlASTWalkerHelper.TEMP_SET_OPERATION_OPERATOR_ANCHOR_CHAR_KEY;
	public static final String RELATIONAL_MODIFIER_OPERATOR_KEY = "operator";
	public static final String RELATIONAL_MODIFIER_SOURCE_COLUMNS_KEY = "source_columns";
	public static final String RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY = "derived_columns";
	public static final String RELATIONAL_MODIFIER_SOURCE_REF_KEY = "source_ref";

	/** Archived scope keys whose flat column-ref lists are validated at scope exit. */
	private static final String[] SCOPE_CLAUSE_COLUMN_LIST_KEYS = {
			MUMBLE_FILTERS_KEY,
			MUMBLE_GROUPED_BY_KEY,
			MUMBLE_ORDERED_BY_KEY,
	};

	// --- Getters/setters for moved fields ---

	public int getTableFunctionSourceCount() { return tableFunctionSourceCount; }
	public void setTableFunctionSourceCount(int count) { this.tableFunctionSourceCount = count; }
	public Set<String> getSuppressedAmbiguousUnqualifiedKeys() { return suppressedAmbiguousUnqualifiedKeys; }
	public Set<String> getTableFunctionSourceRefs() { return tableFunctionSourceRefs; }

	// --- normalizeTableRef delegate (mirrors event-walker static helper) ---

	public static String normalizeTableRef(String tableRef) {
		return SqlASTWalkerHelper.normalizeTableReference(tableRef);
	}

	// --- Wrapper classes for tracking unresolved column locations ---

	/**
	 * Tracks a single occurrence of an unqualified column reference in a clause list.
	 * Used during Option 2 deferred resolution to update clause references after resolution.
	 */
	public static class ClauseRefLocation {
		public final String clauseName;  // "filters", "groupedByList", "orderedByList", etc.
		public final ArrayList<Object> clauseList;  // The actual ArrayList holding the references
		public final int index;  // Index within that list

		public ClauseRefLocation(String clauseName, ArrayList<Object> clauseList, int index) {
			this.clauseName = clauseName;
			this.clauseList = clauseList;
			this.index = index;
		}
	}

	/**
	 * Tracks all occurrences of a single unqualified column across multiple clause lists.
	 * Stores the original columnRefObj and all locations where it appears.
	 */
	public static class UnresolvedColumnTracking {
		public final Object columnRefObj;  // Original column reference object
		public final Set<ClauseRefLocation> locations = new HashSet<>();  // All occurrences

		public UnresolvedColumnTracking(Object columnRefObj) {
			this.columnRefObj = columnRefObj;
		}
	}

	/** Outcome of a single unqualified column bind attempt at scope exit (Phase 8 egress). */
	private enum UnqualifiedScopeResolutionStatus {
		RESOLVED,
		DEFERRED,
		AMBIGUOUS,
		UNRESOLVED
	}

	private static final class UnqualifiedScopeResolutionResult {
		private final UnqualifiedScopeResolutionStatus status;
		private final String resolvedSourceRef;
		private final String ambiguousSourcesLabel;

		private UnqualifiedScopeResolutionResult(
				UnqualifiedScopeResolutionStatus status,
				String resolvedSourceRef,
				String ambiguousSourcesLabel) {
			this.status = status;
			this.resolvedSourceRef = resolvedSourceRef;
			this.ambiguousSourcesLabel = ambiguousSourcesLabel;
		}

		static UnqualifiedScopeResolutionResult resolved(String resolvedSourceRef) {
			return new UnqualifiedScopeResolutionResult(
					UnqualifiedScopeResolutionStatus.RESOLVED,
					resolvedSourceRef,
					null);
		}

		static UnqualifiedScopeResolutionResult deferred() {
			return new UnqualifiedScopeResolutionResult(
					UnqualifiedScopeResolutionStatus.DEFERRED,
					null,
					null);
		}

		static UnqualifiedScopeResolutionResult ambiguous(String ambiguousSourcesLabel) {
			return new UnqualifiedScopeResolutionResult(
					UnqualifiedScopeResolutionStatus.AMBIGUOUS,
					null,
					ambiguousSourcesLabel);
		}

		static UnqualifiedScopeResolutionResult unresolved() {
			return new UnqualifiedScopeResolutionResult(
					UnqualifiedScopeResolutionStatus.UNRESOLVED,
					null,
					null);
		}
	}

	/** Outcome of a single qualified column bind attempt at scope egress (Phase 8). */
	private enum QualifiedScopeResolutionStatus {
		RESOLVED_QUERY_SOURCE,
		RESOLVED_WILDCARD_QUERY_SOURCE,
		RESOLVED_PHYSICAL_SOURCE,
		RESOLVED_DERIVED_COLUMN,
		DEFERRED,
		UNRESOLVED_QUERY_SOURCE,
		UNRESOLVED_PHYSICAL_SOURCE
	}

	private static final class QualifiedScopeResolutionResult {
		private final QualifiedScopeResolutionStatus status;
		private final String querySourceRef;
		private final String resolvedPhysicalTableRef;
		private final String sourceTableRef;

		private QualifiedScopeResolutionResult(
				QualifiedScopeResolutionStatus status,
				String querySourceRef,
				String resolvedPhysicalTableRef,
				String sourceTableRef) {
			this.status = status;
			this.querySourceRef = querySourceRef;
			this.resolvedPhysicalTableRef = resolvedPhysicalTableRef;
			this.sourceTableRef = sourceTableRef;
		}

		static QualifiedScopeResolutionResult resolvedQuerySource(
				String querySourceRef,
				String sourceTableRef) {
			return new QualifiedScopeResolutionResult(
					QualifiedScopeResolutionStatus.RESOLVED_QUERY_SOURCE,
					querySourceRef,
					null,
					sourceTableRef);
		}

		static QualifiedScopeResolutionResult resolvedWildcardQuerySource(
				String querySourceRef,
				String sourceTableRef) {
			return new QualifiedScopeResolutionResult(
					QualifiedScopeResolutionStatus.RESOLVED_WILDCARD_QUERY_SOURCE,
					querySourceRef,
					null,
					sourceTableRef);
		}

		static QualifiedScopeResolutionResult resolvedPhysicalSource(
				String resolvedPhysicalTableRef,
				String sourceTableRef) {
			return new QualifiedScopeResolutionResult(
					QualifiedScopeResolutionStatus.RESOLVED_PHYSICAL_SOURCE,
					null,
					resolvedPhysicalTableRef,
					sourceTableRef);
		}

		static QualifiedScopeResolutionResult resolvedDerivedColumn(String sourceTableRef) {
			return new QualifiedScopeResolutionResult(
					QualifiedScopeResolutionStatus.RESOLVED_DERIVED_COLUMN,
					null,
					null,
					sourceTableRef);
		}

		static QualifiedScopeResolutionResult deferred(String sourceTableRef) {
			return new QualifiedScopeResolutionResult(
					QualifiedScopeResolutionStatus.DEFERRED,
					null,
					null,
					sourceTableRef);
		}

		static QualifiedScopeResolutionResult unresolvedQuerySource(String sourceTableRef) {
			return new QualifiedScopeResolutionResult(
					QualifiedScopeResolutionStatus.UNRESOLVED_QUERY_SOURCE,
					null,
					null,
					sourceTableRef);
		}

		static QualifiedScopeResolutionResult unresolvedPhysicalSource(
				String resolvedPhysicalTableRef,
				String sourceTableRef) {
			return new QualifiedScopeResolutionResult(
					QualifiedScopeResolutionStatus.UNRESOLVED_PHYSICAL_SOURCE,
					null,
					resolvedPhysicalTableRef,
					sourceTableRef);
		}
	}

	// =========================================================================
	// Methods moved from SqlParseEventWalker

	// --- UNPIVOT / convertSymbolTable resolution (canonical from event walker) ---

	public void applyUnpivotValueInterfaceDerivations(
			HashMap<String, Object> localInterface,
			ArrayList<Object> relationalModifierInterfaceHints) {
		if (localInterface == null || localInterface.isEmpty()
				|| relationalModifierInterfaceHints == null || relationalModifierInterfaceHints.isEmpty()) {
			return;
		}

		for (Object hintObj : relationalModifierInterfaceHints) {
			if (!(hintObj instanceof Map<?, ?> hintMapObj)) {
				continue;
			}

			Map<String, Object> hintMap = (Map<String, Object>) hintMapObj;
			Object valueObj = hintMap.get(MUMBLE_VALUE_KEY);
			Object forObj = hintMap.get(MUMBLE_FOR_KEY);
			Object tableRefObj = hintMap.get(MUMBLE_TABLE_REF_KEY);
			Object inListObj = hintMap.get(MUMBLE_IN_KEY);

			if (!(valueObj instanceof String valueColumn)
					|| valueColumn.isBlank()
					|| !(tableRefObj instanceof String sourceRef)
					|| sourceRef.isBlank()
					|| !(inListObj instanceof ArrayList<?> inColumnsObj)
					|| inColumnsObj.isEmpty()) {
				continue;
			}

			for (Map.Entry<String, Object> interfaceEntry : localInterface.entrySet()) {
				Object refsObj = interfaceEntry.getValue();
				if (!(refsObj instanceof ArrayList<?> refs) || refs.isEmpty()) {
					continue;
				}

				ArrayList<Object> rewrittenRefs = rewriteReferenceListForSingleUnpivotHint(
						(ArrayList<Object>) refs,
						valueColumn,
						(forObj instanceof String) ? (String) forObj : null,
						sourceRef,
						(ArrayList<Object>) inColumnsObj);

				if (!rewrittenRefs.equals(refs)) {
					interfaceEntry.setValue(rewrittenRefs);
				}
			}
		}
	}

	public Object applyUnpivotValueDerivationsToReferenceListObject(
			Object referenceListObject,
			ArrayList<Object> relationalModifierInterfaceHints) {
		return referenceListObject;
	}

	public ArrayList<Object> rewriteReferenceListForSingleUnpivotHint(
			ArrayList<Object> refs,
			String valueColumn,
			String forColumn,
			String sourceRef,
			ArrayList<Object> inColumnsObj) {
		ArrayList<Object> rewrittenRefs = new ArrayList<Object>();
		boolean valueColumnReferenceFound = false;

		for (Object refObj : refs) {
			String refName = walker.extractReferenceNameFromInterfaceEntry(refObj);
			if (refName != null && refName.equalsIgnoreCase(valueColumn)) {
				valueColumnReferenceFound = true;
				for (Object inColumnObj : inColumnsObj) {
					if (!(inColumnObj instanceof String inColumn) || inColumn.isBlank()) {
						continue;
					}

					HashMap<String, Object> derivedRef = new HashMap<String, Object>();
					derivedRef.put(MUMBLE_NAME_KEY, inColumn);
					derivedRef.put(MUMBLE_TABLE_REF_KEY, sourceRef);
					appendInterfaceReferenceIfMissing(rewrittenRefs, derivedRef);
				}
			} else if (forColumn != null
					&& refName != null
					&& refName.equalsIgnoreCase(forColumn)) {
				appendInterfaceReferenceIfMissing(rewrittenRefs, refObj);
			} else {
				appendInterfaceReferenceIfMissing(rewrittenRefs, refObj);
			}
		}

		if (!valueColumnReferenceFound) {
			return new ArrayList<Object>(refs);
		}

		return rewrittenRefs;
	}

	public void appendInterfaceReferenceIfMissing(ArrayList<Object> targetRefs, Object candidateRef) {
		if (candidateRef == null) {
			return;
		}

		String candidateName = walker.extractReferenceNameFromInterfaceEntry(candidateRef);
		String candidateTableRef = walker.extractReferenceTableRefFromInterfaceEntry(candidateRef);

		for (Object existingRef : targetRefs) {
			String existingName = walker.extractReferenceNameFromInterfaceEntry(existingRef);
			String existingTableRef = walker.extractReferenceTableRefFromInterfaceEntry(existingRef);
			if (equalsIgnoreCaseNullable(existingName, candidateName)
					&& equalsIgnoreCaseNullable(existingTableRef, candidateTableRef)) {
				return;
			}
		}

		targetRefs.add(candidateRef);
	}

	public boolean equalsIgnoreCaseNullable(String left, String right) {
		if (left == null && right == null) {
			return true;
		}
		if (left == null || right == null) {
			return false;
		}
		return left.equalsIgnoreCase(right);
	}

	/**
	 * After UNPIVOT derivation hints have been applied, removes columns that are
	 * definitively resolved by the UNPIVOT operator from the unresolved-column map
	 * so they do not generate spurious diagnostic errors.
	 *
	 * <ul>
	 *   <li>The VALUE column (e.g. {@code sales_amount}) is synthetic — generated by
	 *       the UNPIVOT operator itself.</li>
	 *   <li>The FOR column (e.g. {@code month_name}) is also synthetic.</li>
	 *   <li>When the UNPIVOT source is an un-aliased subquery (sourceRef is a query
	 *       scope key like {@code query0}), every column that appears in that
	 *       subquery's output interface (including the IN-list columns and passthrough
	 *       columns like {@code empid}) is also resolved.</li>
	 * </ul>
	 */
	public void resolveUnpivotGeneratedColumnsFromUnresolvedMap(
			ArrayList<Object> hints,
			HashMap<String, Object> unresolvedColumnMap) {
		if (hints == null || hints.isEmpty() || unresolvedColumnMap == null || unresolvedColumnMap.isEmpty()) {
			return;
		}
		for (Object hintObj : hints) {
			if (!(hintObj instanceof Map<?, ?> hintMapObj)) {
				continue;
			}
			Map<String, Object> hint = (Map<String, Object>) hintMapObj;
			Object sourceRefObj = hint.get(MUMBLE_TABLE_REF_KEY);
			if (!(sourceRefObj instanceof String sourceRef) || sourceRef.isBlank()) {
				continue;
			}

			String dictionarySourceRef = sourceRef;
			Object dictionarySourceRefObj = hint.get(RELATIONAL_MODIFIER_SOURCE_REF_KEY);
			if (dictionarySourceRefObj instanceof String dictionarySource
					&& !dictionarySource.isBlank()) {
				dictionarySourceRef = dictionarySource;
			}

			// The UNPIVOT VALUE column is synthetically generated by the UNPIVOT operator
			Object valueColumnObj = hint.get(MUMBLE_VALUE_KEY);
			if (valueColumnObj instanceof String valueColumn && !valueColumn.isBlank()) {
				removeUnpivotGeneratedColumnReference(unresolvedColumnMap, sourceRef, valueColumn, true);
			}

			// The UNPIVOT FOR column is also synthetically generated by the UNPIVOT operator
			Object forColumnObj = hint.get(MUMBLE_FOR_KEY);
			if (forColumnObj instanceof String forColumn && !forColumn.isBlank()) {
				removeUnpivotGeneratedColumnReference(unresolvedColumnMap, sourceRef, forColumn, true);
			}

			Object inColumnsObj = hint.get(MUMBLE_IN_KEY);
			if (inColumnsObj instanceof ArrayList<?> inColumns) {
				for (Object inColumnObj : inColumns) {
					if (inColumnObj instanceof String inColumn && !inColumn.isBlank()) {
						removeAndMaterializeUnpivotResolvedColumn(unresolvedColumnMap, dictionarySourceRef, inColumn, false);
					}
				}
			}

			// When the UNPIVOT source is a query reference (e.g., un-aliased subquery),
			// every column in the source query's output interface is resolved by that scope.
			if (!walker.isNonTableQuerySourceReference(sourceRef)) {
				continue;
			}
			Object sourceQueryScopeObj = walker.symbolTable.get(sourceRef);
			if (!(sourceQueryScopeObj instanceof Map<?, ?> sourceQueryScopeMapObj)) {
				continue;
			}
			Map<String, Object> sourceQueryScope = (Map<String, Object>) sourceQueryScopeMapObj;
			Object interfaceObj = sourceQueryScope.get(MUMBLE_INTERFACE_KEY);
			if (!(interfaceObj instanceof Map<?, ?> interfaceMapObj)) {
				continue;
			}
			Map<String, Object> sourceInterface = (Map<String, Object>) interfaceMapObj;
			for (String interfaceColName : new ArrayList<>(sourceInterface.keySet())) {
				removeFromUnresolvedMapCaseInsensitive(unresolvedColumnMap, interfaceColName);
			}
		}
	}

	public void resolveRelationalModifierDerivedColumnsFromUnresolvedMap(
			ArrayList<Object> relationalModifierInterfaceHints,
			HashMap<String, Object> derivedColumns,
			HashMap<String, Object> unresolvedColumnMap) {
		if (derivedColumns == null || derivedColumns.isEmpty()
				|| unresolvedColumnMap == null || unresolvedColumnMap.isEmpty()) {
			return;
		}

		ArrayList<String> resolvedKeys = new ArrayList<String>();
		for (String unresolvedKey : new ArrayList<String>(unresolvedColumnMap.keySet())) {
			if (unresolvedKey == null || unresolvedKey.isBlank()) {
				continue;
			}

			String tableRef = null;
			String columnName = unresolvedKey;
			int separatorIndex = unresolvedKey.lastIndexOf('.');
			if (separatorIndex >= 0 && separatorIndex + 1 < unresolvedKey.length()) {
				tableRef = unresolvedKey.substring(0, separatorIndex);
				columnName = unresolvedKey.substring(separatorIndex + 1);
			}

			if (columnName == null || columnName.isBlank()) {
				continue;
			}

			boolean matchesDerived = (tableRef == null || tableRef.isBlank())
					? containsKeyIgnoreCase(derivedColumns, columnName)
					: isRelationalModifierDerivedColumnReference(
							derivedColumns,
							relationalModifierInterfaceHints,
							tableRef,
							columnName);

			if (matchesDerived) {
				resolvedKeys.add(unresolvedKey);
			}
		}

		for (String resolvedKey : resolvedKeys) {
			unresolvedColumnMap.remove(resolvedKey);
			releaseResolvedQualifiedGlobalLocationIfQualified(resolvedKey);
		}
	}

	@SuppressWarnings("unchecked")
	public void materializeSelectedUnpivotInColumnsIntoSourceDictionary(
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> localTableAliasMap,
			ArrayList<Object> relationalModifierInterfaceHints) {
		if (localCurrentQueryDictionary == null || localCurrentQueryDictionary.isEmpty()
				|| localTableCollection == null
				|| relationalModifierInterfaceHints == null || relationalModifierInterfaceHints.isEmpty()) {
			return;
		}

		for (Object hintObj : relationalModifierInterfaceHints) {
			if (!(hintObj instanceof Map<?, ?> hintMapObj)) {
				continue;
			}

			Map<String, Object> hintMap = (Map<String, Object>) hintMapObj;
			Object operatorObj = hintMap.get(RELATIONAL_MODIFIER_OPERATOR_KEY);
			if (!(operatorObj instanceof String operator)
					|| !MUMBLE_UNPIVOT_KEY.equalsIgnoreCase(operator)) {
				continue;
			}

			Object inColumnsObj = hintMap.get(MUMBLE_IN_KEY);
			if (!(inColumnsObj instanceof ArrayList<?> inColumns) || inColumns.isEmpty()) {
				continue;
			}

			String dictionaryTargetRef = null;
			Object dictionarySourceRefObj = hintMap.get(RELATIONAL_MODIFIER_SOURCE_REF_KEY);
			if (dictionarySourceRefObj instanceof String dictionarySourceRef
					&& !dictionarySourceRef.isBlank()) {
				dictionaryTargetRef = dictionarySourceRef;
			} else {
				Object sourceRefObj = hintMap.get(MUMBLE_TABLE_REF_KEY);
				if (sourceRefObj instanceof String sourceRef && !sourceRef.isBlank()) {
					dictionaryTargetRef = sourceRef;
				}
			}

			if (dictionaryTargetRef == null || dictionaryTargetRef.isBlank()) {
				continue;
			}

			String resolvedTargetRef = walker.resolveAliasToTableName(dictionaryTargetRef, localTableAliasMap);
			if (resolvedTargetRef != null
					&& !resolvedTargetRef.isBlank()
					&& !walker.isNonTableQuerySourceReference(resolvedTargetRef)) {
				dictionaryTargetRef = resolvedTargetRef;
			}

			Object sourceDictionaryObj = localTableCollection.get(dictionaryTargetRef);
			if (!(sourceDictionaryObj instanceof HashMap<?, ?>)) {
				String normalizedSourceRef = normalizeTableRef(dictionaryTargetRef);
				sourceDictionaryObj = localTableCollection.get(normalizedSourceRef);
				if (!(sourceDictionaryObj instanceof HashMap<?, ?>)) {
					sourceDictionaryObj = new HashMap<String, Object>();
					localTableCollection.put(normalizedSourceRef, sourceDictionaryObj);
				}
			}
			HashMap<String, Object> sourceDictionary = (HashMap<String, Object>) sourceDictionaryObj;

			for (Object inColumnObj : inColumns) {
				if (!(inColumnObj instanceof String inColumn) || inColumn.isBlank()) {
					continue;
				}

				String queryColumnKey = findKeyIgnoreCase(localCurrentQueryDictionary, inColumn);
				if (queryColumnKey == null) {
					continue;
				}

				Object queryColumnRefs = localCurrentQueryDictionary.get(queryColumnKey);
				if (queryColumnRefs == null) {
					continue;
				}

				walker.mergeResolvedColumnIntoDictionary(sourceDictionary, inColumn, queryColumnRefs);
			}
		}
	}

	private void removeUnpivotGeneratedColumnReference(
			HashMap<String, Object> unresolvedColumnMap,
			String sourceRef,
			String columnName,
			boolean suppressAmbiguousDiagnostic) {
		if (unresolvedColumnMap == null || columnName == null || columnName.isBlank()) {
			return;
		}

		Object matchedEntry = (sourceRef == null || sourceRef.isBlank())
				? null
				: consumeQualifiedUnknownEntry(unresolvedColumnMap, sourceRef, columnName);
		if (matchedEntry == null) {
			matchedEntry = removeUnresolvedMapEntry(unresolvedColumnMap, columnName);
		}
		if (matchedEntry == null) {
			for (String key : new ArrayList<String>(unresolvedColumnMap.keySet())) {
				if (key != null && key.equalsIgnoreCase(columnName)) {
					matchedEntry = removeUnresolvedMapEntry(unresolvedColumnMap, key);
					break;
				}
			}
		}

		if (matchedEntry != null && suppressAmbiguousDiagnostic) {
			Integer[] refLocation = walker.getLineAndCharacterFromEntry(matchedEntry);
			if (refLocation != null && refLocation.length >= 2) {
				suppressedAmbiguousUnqualifiedKeys.add(
						buildUnqualifiedSuppressionKey(columnName, refLocation[0], refLocation[1]));
			}
		}

	}

	@SuppressWarnings("unchecked")
	private void removeAndMaterializeUnpivotResolvedColumn(
			HashMap<String, Object> unresolvedColumnMap,
			String sourceRef,
			String columnName,
			boolean suppressAmbiguousDiagnostic) {
		if (unresolvedColumnMap == null || sourceRef == null || sourceRef.isBlank()
				|| columnName == null || columnName.isBlank()) {
			return;
		}

		String dictionaryTargetRef = sourceRef;
		HashMap<String, Object> tableAliasMap = null;
		Object tableAliasObj = walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY);
		if (tableAliasObj instanceof Map<?, ?> tableAliasMapObj && !tableAliasMapObj.isEmpty()) {
			tableAliasMap = new HashMap<String, Object>((Map<String, Object>) tableAliasMapObj);
		}
		String resolvedSourceRef = walker.resolveAliasToTableName(sourceRef, tableAliasMap);
		if (resolvedSourceRef != null
				&& !resolvedSourceRef.isBlank()
				&& !walker.isNonTableQuerySourceReference(resolvedSourceRef)) {
			dictionaryTargetRef = resolvedSourceRef;
		}

		Object matchedEntry = consumeQualifiedUnknownEntry(unresolvedColumnMap, sourceRef, columnName);
		if (matchedEntry == null
				&& dictionaryTargetRef != null
				&& !dictionaryTargetRef.isBlank()
				&& !dictionaryTargetRef.equalsIgnoreCase(sourceRef)) {
			matchedEntry = consumeQualifiedUnknownEntry(unresolvedColumnMap, dictionaryTargetRef, columnName);
		}
		if (matchedEntry == null) {
			matchedEntry = removeUnresolvedMapEntry(unresolvedColumnMap, columnName);
		}
		if (matchedEntry == null) {
			for (String key : new ArrayList<String>(unresolvedColumnMap.keySet())) {
				if (key != null && key.equalsIgnoreCase(columnName)) {
					matchedEntry = removeUnresolvedMapEntry(unresolvedColumnMap, key);
					break;
				}
			}
		}

		HashMap<String, Object> localTableDictionary = walker.getCurrentTableDictionary();
		HashMap<String, Object> sourceDictionary = walker.getTableDictionaryForReference(dictionaryTargetRef, localTableDictionary);
		if (sourceDictionary == null) {
			String normalizedSourceRef = normalizeTableRef(dictionaryTargetRef);
			sourceDictionary = walker.getTableDictionaryForReference(normalizedSourceRef, localTableDictionary);
			if (sourceDictionary == null) {
				sourceDictionary = new HashMap<String, Object>();
				localTableDictionary.put(normalizedSourceRef, sourceDictionary);
			}
		}

		if (matchedEntry != null) {
			walker.mergeResolvedColumnIntoDictionary(sourceDictionary, columnName, matchedEntry);
			if (suppressAmbiguousDiagnostic) {
				Integer[] refLocation = walker.getLineAndCharacterFromEntry(matchedEntry);
				if (refLocation != null && refLocation.length >= 2) {
					suppressedAmbiguousUnqualifiedKeys.add(
							buildUnqualifiedSuppressionKey(columnName, refLocation[0], refLocation[1]));
				}
			}
		} else {
			sourceDictionary.putIfAbsent(columnName, new ArrayList<Object>());
		}

		if (!dictionaryTargetRef.equalsIgnoreCase(sourceRef)) {
			String normalizedSourceRef = normalizeTableRef(sourceRef);
			localTableDictionary.remove(sourceRef);
			if (!normalizedSourceRef.equalsIgnoreCase(sourceRef)) {
				localTableDictionary.remove(normalizedSourceRef);
			}

			HashMap<String, Object> globalTableDictionary = walker.getWalkerTableDictionary();
			if (globalTableDictionary != null) {
				globalTableDictionary.remove(sourceRef);
				if (!normalizedSourceRef.equalsIgnoreCase(sourceRef)) {
					globalTableDictionary.remove(normalizedSourceRef);
				}
			}
		}

	}

	/**
	 * Applies PIVOT derivation hints to the local interface, adding new interface entries
	 * for derived pivot columns created by combining aggregates and IN-list values.
	 * 
	 * For each pivot hint:
	 * - Extracts aggregate column names (function names or explicit aliases)
	 * - Extracts IN-list column names (pivot value columns)
	 * - For each combination of aggregate and IN value, creates a derived column name
	 * - Adds new interface entries for these derived columns
	 */
	@SuppressWarnings("unchecked")
	public void applyPivotValueInterfaceDerivations(
			HashMap<String, Object> localInterface,
			ArrayList<Object> relationalModifierInterfaceHints) {
		if (localInterface == null || localInterface.isEmpty()
				|| relationalModifierInterfaceHints == null || relationalModifierInterfaceHints.isEmpty()) {
			return;
		}

		for (Object hintObj : relationalModifierInterfaceHints) {
			if (!(hintObj instanceof Map<?, ?> hintMapObj)) {
				continue;
			}

			Map<String, Object> hintMap = (Map<String, Object>) hintMapObj;
			Object operatorObj = hintMap.get(RELATIONAL_MODIFIER_OPERATOR_KEY);
			boolean isPivotHint = operatorObj instanceof String operator && MUMBLE_PIVOT_KEY.equals(operator);
			Object derivedColumnsObj = isPivotHint ? hintMap.get(RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY) : null;
			if (derivedColumnsObj instanceof ArrayList<?> derivedColumnsList && !derivedColumnsList.isEmpty()) {
				for (Object derivedColumnObj : derivedColumnsList) {
					if (!(derivedColumnObj instanceof String derivedColumnName) || derivedColumnName.isBlank()) {
						continue;
					}

					String interfaceKey = findKeyIgnoreCase(localInterface, derivedColumnName);
					if (interfaceKey == null) {
						// Keep full PIVOT derivation metadata in derived_columns hints, but do not
						// widen the projected interface with non-selected derived columns.
						continue;
					}

					HashMap<String, Object> derivedRef = new HashMap<String, Object>();
					derivedRef.put(MUMBLE_NAME_KEY, derivedColumnName);
					derivedRef.put(MUMBLE_TABLE_REF_KEY, null);

					Object interfaceRefsObj = localInterface.get(interfaceKey);
					ArrayList<Object> interfaceRefs;
					if (interfaceRefsObj instanceof ArrayList<?>) {
						interfaceRefs = (ArrayList<Object>) interfaceRefsObj;
					} else {
						interfaceRefs = new ArrayList<Object>();
						localInterface.put(interfaceKey, interfaceRefs);
					}

					appendInterfaceReferenceIfMissing(interfaceRefs, derivedRef);
				}
				continue;
			}

			Object aggregateColumnsObj = hintMap.get("pivot_aggregate_columns");
			Object inColumnsObj = hintMap.get("pivot_in_columns");

			if (!(aggregateColumnsObj instanceof ArrayList<?> aggregateColumnsList)
					|| aggregateColumnsList.isEmpty()
					|| !(inColumnsObj instanceof ArrayList<?> inColumnsList)
					|| inColumnsList.isEmpty()) {
				continue;
			}

			// Create derived column names for each aggregate-IN value combination
			// Naming convention: <in_value>_<aggregate_name>
			for (Object aggregateObj : aggregateColumnsList) {
				if (!(aggregateObj instanceof String aggregate) || aggregate.isBlank()) {
					continue;
				}

				for (Object inObj : inColumnsList) {
					if (!(inObj instanceof String inValue) || inValue.isBlank()) {
						continue;
					}

					String derivedColumnName = inValue + "_" + aggregate;
					String interfaceKey = findKeyIgnoreCase(localInterface, derivedColumnName);
					if (interfaceKey == null) {
						continue;
					}
					HashMap<String, Object> derivedRef = new HashMap<String, Object>();
					derivedRef.put(MUMBLE_NAME_KEY, derivedColumnName);
					derivedRef.put(MUMBLE_TABLE_REF_KEY, null);

					Object interfaceRefsObj = localInterface.get(interfaceKey);
					ArrayList<Object> interfaceRefs;
					if (interfaceRefsObj instanceof ArrayList<?>) {
						interfaceRefs = (ArrayList<Object>) interfaceRefsObj;
					} else {
						interfaceRefs = new ArrayList<Object>();
						localInterface.put(interfaceKey, interfaceRefs);
					}

					appendInterfaceReferenceIfMissing(interfaceRefs, derivedRef);
				}
			}
		}
	}

	private String findKeyIgnoreCase(Map<String, Object> map, String key) {
		if (map == null || map.isEmpty() || key == null) {
			return null;
		}
		if (map.containsKey(key)) {
			return key;
		}
		for (String existingKey : map.keySet()) {
			if (existingKey != null && existingKey.equalsIgnoreCase(key)) {
				return existingKey;
			}
		}
		return null;
	}

	/**
	 * Removes one entry from a scope-local unresolved map and, when the key is qualified,
	 * drops the matching statement-level position tracker entry.
	 */
	private Object removeUnresolvedMapEntry(
			HashMap<String, Object> unresolvedColumnMap,
			String key) {
		if (unresolvedColumnMap == null || key == null || key.isBlank()) {
			return null;
		}

		Object removed = unresolvedColumnMap.remove(key);
		if (removed != null) {
			releaseResolvedQualifiedGlobalLocationIfQualified(key);
		}
		return removed;
	}

	public void removeFromUnresolvedMapCaseInsensitive(HashMap<String, Object> unresolvedColumnMap, String columnName) {
		if (columnName == null || columnName.isBlank()) {
			return;
		}
		if (removeUnresolvedMapEntry(unresolvedColumnMap, columnName) != null) {
			return;
		}
		String matchingKey = null;
		for (String key : unresolvedColumnMap.keySet()) {
			if (key != null && key.equalsIgnoreCase(columnName)) {
				matchingKey = key;
				break;
			}
		}
		if (matchingKey != null) {
			removeUnresolvedMapEntry(unresolvedColumnMap, matchingKey);
		}
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> extractRelationalModifierInListColumnNames(Object inListObj) {
		ArrayList<String> inColumns = new ArrayList<String>();
		if (!(inListObj instanceof Map<?, ?> inListMapObj)) {
			return inColumns;
		}

		Map<String, Object> inListMap = (Map<String, Object>) inListMapObj;
		for (int index = 1; inListMap.containsKey(String.valueOf(index)); index++) {
			Object inItemObj = inListMap.get(String.valueOf(index));
			if (!(inItemObj instanceof Map<?, ?> inItemMapObj)) {
				continue;
			}

			Object inNameObj = ((Map<String, Object>) inItemMapObj).get(MUMBLE_NAME_KEY);
			if (!(inNameObj instanceof String inName) || inName.isBlank()) {
				continue;
			}

			inColumns.add(inName);
		}

		return inColumns;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> buildDerivedColumnsMapFromHints(ArrayList<Object> hints) {
		HashMap<String, Object> derivedColumns = new HashMap<String, Object>();
		if (hints == null || hints.isEmpty()) {
			return derivedColumns;
		}

		for (Object hintObj : hints) {
			if (!(hintObj instanceof Map<?, ?> hintMapObj)) {
				continue;
			}

			Map<String, Object> hintMap = (Map<String, Object>) hintMapObj;
			Object operatorObj = hintMap.get(RELATIONAL_MODIFIER_OPERATOR_KEY);
			boolean isPivotHint = operatorObj instanceof String operator && MUMBLE_PIVOT_KEY.equals(operator);

			Object derivedColumnsObj = hintMap.get(RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY);
			if (derivedColumnsObj instanceof ArrayList<?> derivedColumnsList) {
				for (Object derivedColumnObj : derivedColumnsList) {
					if (derivedColumnObj instanceof String derivedColumnName && !derivedColumnName.isBlank()) {
						addDerivedColumnNameIfMissing(derivedColumns, derivedColumnName);
						mergeUnpivotDerivedRefsIfPresent(derivedColumns, hintMap, derivedColumnName);
						if (isPivotHint) {
							mergePivotAggregateDependencyRefsFallbackIfPresent(
									derivedColumns,
									hintMap,
									derivedColumnName);
						}
					}
				}
			}

			Object valueObj = hintMap.get(MUMBLE_VALUE_KEY);
			if (valueObj instanceof String valueColumn && !valueColumn.isBlank()) {
				addDerivedColumnNameIfMissing(derivedColumns, valueColumn);
				mergeUnpivotDerivedRefsIfPresent(derivedColumns, hintMap, valueColumn);
			}

			Object forObj = hintMap.get(MUMBLE_FOR_KEY);
			if (forObj instanceof String forColumn && !forColumn.isBlank()) {
				addDerivedColumnNameIfMissing(derivedColumns, forColumn);
				mergeUnpivotDerivedRefsIfPresent(derivedColumns, hintMap, forColumn);
			}

			Object aggregateColumnsObj = hintMap.get("pivot_aggregate_columns");
			Object inColumnsObj = hintMap.get("pivot_in_columns");
			if (aggregateColumnsObj instanceof ArrayList<?> aggregateColumns
					&& !aggregateColumns.isEmpty()
					&& inColumnsObj instanceof ArrayList<?> inColumns
					&& !inColumns.isEmpty()) {
				for (Object inObj : inColumns) {
					if (!(inObj instanceof String inValue) || inValue.isBlank()) {
						continue;
					}
					String normalizedInValue = normalizePivotDerivedComponent(inValue);
					for (Object aggObj : aggregateColumns) {
						if (!(aggObj instanceof String aggName) || aggName.isBlank()) {
							continue;
						}
						String derivedColumnName = inValue + "_" + aggName;
						addDerivedColumnNameIfMissing(derivedColumns, derivedColumnName);
						mergePivotAggregateResolvedRefsIfPresent(
								derivedColumns,
								hintMap,
								aggName,
								derivedColumnName);
						mergePivotAggregateDependencyRefsFallbackIfPresent(
								derivedColumns,
								hintMap,
								aggName,
								derivedColumnName);
						if (!normalizedInValue.equals(inValue)) {
							String normalizedDerivedColumnName = normalizedInValue + "_" + aggName;
							addDerivedColumnNameIfMissing(derivedColumns, normalizedDerivedColumnName);
							mergePivotAggregateResolvedRefsIfPresent(
									derivedColumns,
									hintMap,
									aggName,
									normalizedDerivedColumnName);
							mergePivotAggregateDependencyRefsFallbackIfPresent(
									derivedColumns,
									hintMap,
									aggName,
									normalizedDerivedColumnName);
						}
					}
				}
			}
		}

		return derivedColumns;
	}

	private void addDerivedColumnNameIfMissing(HashMap<String, Object> derivedColumns, String columnName) {
		if (columnName == null || columnName.isBlank()) {
			return;
		}
		if (containsKeyIgnoreCase(derivedColumns, columnName)) {
			return;
		}
		derivedColumns.put(columnName, new ArrayList<Object>());
	}

	private void mergePivotAggregateDependencyRefsFallbackIfPresent(
			HashMap<String, Object> derivedColumns,
			Map<String, Object> hintMap,
			String derivedColumnName) {
		if (derivedColumns == null
				|| derivedColumns.isEmpty()
				|| hintMap == null
				|| hintMap.isEmpty()
				|| derivedColumnName == null
				|| derivedColumnName.isBlank()) {
			return;
		}

		String aggregateName = null;
		int separatorIndex = derivedColumnName.lastIndexOf('_');
		if (separatorIndex > 0 && separatorIndex + 1 < derivedColumnName.length()) {
			aggregateName = derivedColumnName.substring(separatorIndex + 1);
		}

		if (aggregateName == null || aggregateName.isBlank()) {
			return;
		}

		mergePivotAggregateDependencyRefsFallbackIfPresent(
				derivedColumns,
				hintMap,
				aggregateName,
				derivedColumnName);
	}

	@SuppressWarnings("unchecked")
	private void mergePivotAggregateDependencyRefsFallbackIfPresent(
			HashMap<String, Object> derivedColumns,
			Map<String, Object> hintMap,
			String aggregateName,
			String derivedColumnName) {
		if (derivedColumns == null
				|| derivedColumns.isEmpty()
				|| hintMap == null
				|| aggregateName == null
				|| aggregateName.isBlank()
				|| derivedColumnName == null
				|| derivedColumnName.isBlank()) {
			return;
		}

		String resolvedKey = findKeyIgnoreCase(derivedColumns, derivedColumnName);
		String targetKey = resolvedKey != null ? resolvedKey : derivedColumnName;
		Object existingRefsObj = derivedColumns.get(targetKey);
		if (existingRefsObj instanceof ArrayList<?> existingRefs && !existingRefs.isEmpty()) {
			return;
		}

		Object dependencyObj = hintMap.get("pivot_aggregate_dependency_columns");
		if (!(dependencyObj instanceof Map<?, ?> dependencyMapObj)) {
			return;
		}

		Object dependencyListObj = ((Map<String, Object>) dependencyMapObj).get(aggregateName);
		if (!(dependencyListObj instanceof ArrayList<?> dependencyList) || dependencyList.isEmpty()) {
			return;
		}

		Object sourceRefObj = hintMap.get(RELATIONAL_MODIFIER_SOURCE_REF_KEY);
		if (!(sourceRefObj instanceof String sourceRef) || sourceRef.isBlank()) {
			sourceRefObj = hintMap.get(MUMBLE_TABLE_REF_KEY);
		}
		if (!(sourceRefObj instanceof String sourceRef) || sourceRef.isBlank()) {
			return;
		}

		ArrayList<Object> fallbackRefs = new ArrayList<Object>();
		for (Object dependencyObjItem : dependencyList) {
			if (!(dependencyObjItem instanceof String dependencyName) || dependencyName.isBlank()) {
				continue;
			}
			HashMap<String, Object> dependencyRef = new HashMap<String, Object>();
			dependencyRef.put(MUMBLE_NAME_KEY, dependencyName);
			dependencyRef.put(MUMBLE_TABLE_REF_KEY, sourceRef);
			appendInterfaceReferenceIfMissing(fallbackRefs, dependencyRef);
		}

		if (!fallbackRefs.isEmpty()) {
			derivedColumns.put(targetKey, fallbackRefs);
		}
	}

	@SuppressWarnings("unchecked")
	private void mergeUnpivotDerivedRefsIfPresent(
			HashMap<String, Object> derivedColumns,
			Map<String, Object> hintMap,
			String derivedColumnName) {
		if (derivedColumns == null
				|| hintMap == null
				|| derivedColumnName == null
				|| derivedColumnName.isBlank()) {
			return;
		}

		Object sourceRefObj = hintMap.get(MUMBLE_TABLE_REF_KEY);
		if (!(sourceRefObj instanceof String sourceRef) || sourceRef.isBlank()) {
			return;
		}

		Object inColumnsObj = hintMap.get(MUMBLE_IN_KEY);
		if (!(inColumnsObj instanceof ArrayList<?> inColumns) || inColumns.isEmpty()) {
			return;
		}

		Object existingRefsObj = derivedColumns.get(derivedColumnName);
		ArrayList<Object> existingRefs;
		if (existingRefsObj instanceof ArrayList<?>) {
			existingRefs = (ArrayList<Object>) existingRefsObj;
		} else {
			existingRefs = new ArrayList<Object>();
			derivedColumns.put(derivedColumnName, existingRefs);
		}

		for (Object inObj : inColumns) {
			if (!(inObj instanceof String inColumn) || inColumn.isBlank()) {
				continue;
			}
			HashMap<String, Object> ref = new HashMap<String, Object>();
			ref.put(MUMBLE_NAME_KEY, inColumn);
			ref.put(MUMBLE_TABLE_REF_KEY, sourceRef);
			appendInterfaceReferenceIfMissing(existingRefs, ref);
		}
	}

	@SuppressWarnings("unchecked")
	private void mergePivotAggregateResolvedRefsIfPresent(
			HashMap<String, Object> derivedColumns,
			Map<String, Object> hintMap,
			String aggregateName,
			String derivedColumnName) {
		if (derivedColumns == null
				|| hintMap == null
				|| aggregateName == null
				|| aggregateName.isBlank()
				|| derivedColumnName == null
				|| derivedColumnName.isBlank()) {
			return;
		}

		Object resolvedRefsObj = hintMap.get("pivot_aggregate_resolved_refs");
		if (!(resolvedRefsObj instanceof Map<?, ?> resolvedRefsMapObj)) {
			return;
		}

		Object aggregateRefsObj = ((Map<String, Object>) resolvedRefsMapObj).get(aggregateName);
		if (!(aggregateRefsObj instanceof ArrayList<?> aggregateRefs) || aggregateRefs.isEmpty()) {
			return;
		}

		Object existingRefsObj = derivedColumns.get(derivedColumnName);
		ArrayList<Object> existingRefs;
		if (existingRefsObj instanceof ArrayList<?>) {
			existingRefs = (ArrayList<Object>) existingRefsObj;
		} else {
			existingRefs = new ArrayList<Object>();
			derivedColumns.put(derivedColumnName, existingRefs);
		}

		for (Object refObj : aggregateRefs) {
			if (refObj instanceof Map<?, ?> refMapObj) {
				HashMap<String, Object> clonedRef = new HashMap<String, Object>((Map<String, Object>) refMapObj);
				appendInterfaceReferenceIfMissing(existingRefs, clonedRef);
			} else {
				appendInterfaceReferenceIfMissing(existingRefs, refObj);
			}
		}
	}

	private String normalizePivotDerivedComponent(String component) {
		if (component == null || component.length() < 2) {
			return component;
		}
		char first = component.charAt(0);
		char last = component.charAt(component.length() - 1);
		if ((first == '\'' && last == '\'') || (first == '"' && last == '"')) {
			return component.substring(1, component.length() - 1);
		}
		return component;
	}

	@SuppressWarnings("unchecked")
	public void registerUnpivotGeneratedColumnAmbiguitySuppressions(
			ArrayList<Object> hints,
			HashMap<String, Object> localCurrentQueryDictionary) {
		if (hints == null || hints.isEmpty()
				|| localCurrentQueryDictionary == null || localCurrentQueryDictionary.isEmpty()) {
			return;
		}

		for (Object hintObj : hints) {
			if (!(hintObj instanceof Map<?, ?> hintMapObj)) {
				continue;
			}
			Map<String, Object> hint = (Map<String, Object>) hintMapObj;
			Object valueObj = hint.get(MUMBLE_VALUE_KEY);
			if (valueObj instanceof String valueColumn && !valueColumn.isBlank()) {
				registerUnqualifiedSuppressionFromQueryDictionary(localCurrentQueryDictionary, valueColumn);
			}
			Object forObj = hint.get(MUMBLE_FOR_KEY);
			if (forObj instanceof String forColumn && !forColumn.isBlank()) {
				registerUnqualifiedSuppressionFromQueryDictionary(localCurrentQueryDictionary, forColumn);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private String resolveUnpivotGeneratedColumnSourceRef(
			String columnName,
			ArrayList<Object> hints) {
		if (columnName == null || columnName.isBlank() || hints == null || hints.isEmpty()) {
			return null;
		}
		for (Object hintObj : hints) {
			if (!(hintObj instanceof Map<?, ?> hintMapObj)) {
				continue;
			}
			Map<String, Object> hint = (Map<String, Object>) hintMapObj;
			Object sourceRefObj = hint.get(MUMBLE_TABLE_REF_KEY);
			if (!(sourceRefObj instanceof String sourceRef) || sourceRef.isBlank()) {
				continue;
			}
			Object valueObj = hint.get(MUMBLE_VALUE_KEY);
			if (valueObj instanceof String valueColumn
					&& !valueColumn.isBlank()
					&& valueColumn.equalsIgnoreCase(columnName)) {
				return sourceRef;
			}
			Object forObj = hint.get(MUMBLE_FOR_KEY);
			if (forObj instanceof String forColumn
					&& !forColumn.isBlank()
					&& forColumn.equalsIgnoreCase(columnName)) {
				return sourceRef;
			}
		}
		return null;
	}

	private void registerUnqualifiedSuppressionFromQueryDictionary(
			HashMap<String, Object> localCurrentQueryDictionary,
			String columnName) {
		Object refEntry = localCurrentQueryDictionary.get(columnName);
		Integer[] refLocation = walker.getLineAndCharacterFromEntry(refEntry);
		if (refLocation == null || refLocation.length < 2 || refLocation[0] == null || refLocation[1] == null) {
			return;
		}
		suppressedAmbiguousUnqualifiedKeys.add(
				buildUnqualifiedSuppressionKey(columnName, refLocation[0], refLocation[1]));
	}

	/**
	 * Canonical scope-exit column resolution for the active symbol-table frame.
	 * <p>
	 * <b>Intentional call sites</b> (grep {@code convertSymbolTableToTableDictionary} to audit):
	 * <ul>
	 *   <li>{@link #finalizeQueryScopeSymbolTable} — SELECT / CTE-body / insert-source scope publish</li>
	 *   <li>{@link #finalizeUpdateScopeSymbolTable} — UPDATE scope publish</li>
	 *   <li>{@link #finalizeDeleteScopeSymbolTable} — DELETE scope publish</li>
	 *   <li>{@link #reconcileJoinExtensionSymbolTable} — mid-FROM partial reconcile only (not publish)</li>
	 * </ul>
	 * There is no walker-owned duplicate; all paths delegate here.
	 */
	public HashMap<String, Object> convertSymbolTableToTableDictionary(
			boolean emitFinalUnresolvedUnknownFatal,
			boolean deferCorrelatedValueSubqueryQualifiedUnknowns,
			String updateTargetTableRef) {
		return convertSymbolTableToTableDictionary(
				emitFinalUnresolvedUnknownFatal,
				deferCorrelatedValueSubqueryQualifiedUnknowns,
				updateTargetTableRef,
				false);
	}

	public HashMap<String, Object> convertSymbolTableToTableDictionary(
			boolean emitFinalUnresolvedUnknownFatal,
			boolean deferCorrelatedValueSubqueryQualifiedUnknowns,
			String updateTargetTableRef,
			boolean updateHasFromClause) {
	
		// deconstruct current symbol table into components for analysis
		Object preservedInsertSourceSelectSequence = null;
		if (walker.currentStackLevel(SQLSelectParserParser.RULE_insert_source_primary) != null) {
			preservedInsertSourceSelectSequence = walker.symbolTable.remove(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY);
		}

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
		ArrayList<Object> localRelationalModifierInterfaceHints =
				(ArrayList<Object>) walker.symbolTable.remove(DERIVED_COLUMNS_HINTS_KEY);
		HashMap<String, Object> localDerivedColumns =
				buildDerivedColumnsMapFromHints(localRelationalModifierInterfaceHints);
		String deleteTargetTableRef = (String) walker.symbolTable.remove(TEMP_DELETE_TARGET_TABLE_REF_KEY);
		String deleteTargetAlias = (String) walker.symbolTable.remove(TEMP_DELETE_TARGET_ALIAS_KEY);

		// Leave these null if they don't exist
		HashSet<String> localScalarSubqueryAliases = (HashSet<String>) walker.symbolTable.remove(MUMBLE_SCALAR_SUBQUERY_ALIASES_KEY);
        Object  filtersList = walker.symbolTable.remove(MUMBLE_FILTERS_KEY);
		Object groupedByList = walker.symbolTable.remove(MUMBLE_GROUPED_BY_KEY);
		Object orderedByList = walker.symbolTable.remove(MUMBLE_ORDERED_BY_KEY);
		walker.mergeNonTableAliasMappingsIntoAliasCollection(localCurrentQueryDictionary, localTableAliasMap);

		// Resolve alias-backed table refs so tableCollection keys align with canonical table references.
		walker.reconcileAliasBackedTableReferences(localTableCollection, localTableAliasMap);

		boolean isUpdateScope = updateTargetTableRef != null && !updateTargetTableRef.isBlank();
		if (isUpdateScope) {
			resolveUpdateLhsColumnsToTargetTable(
					localLhsUnresolvedColumnMap,
					localUnresolvedColumnMap,
					localTableAliasMap,
					localTargetTableCollection,
					updateTargetTableRef);
			// DEFER UPDATE no-FROM target resolution to unified resolver at query exit
			// This allows UPDATE target columns to be resolved alongside other clause columns
			// in a single unified pass, rather than multiple early resolution steps.
			if (!updateHasFromClause) {
				// Store target table ref for unified resolver to use later
				walker.symbolTable.put(TEMP_UPDATE_NODEFROM_TARGET_KEY, updateTargetTableRef);
				walker.symbolTable.put(TEMP_UPDATE_NODEFROM_TARGET_TABLE_COLLECTION_KEY, localTargetTableCollection);
			} else {
				mergeUpdateTargetAndLhsIntoTableDictionary(
						localTargetTableCollection,
						null,
						localTableCollection,
						localTableAliasMap,
						updateTargetTableRef);
				localTargetTableCollection.clear();
			}
		}

		// // Add scalar subquery aliases into the alias collection for downstream source resolution, which allows scalar subqueries to be resolved as sources for columns in the query interface.
		// walker.mergeScalarSubqueryAliasesIntoAliasCollection(localScalarSubqueryAliases, localTableAliasMap);

		// Expand wildcard unknown entries (for example *, alias.*) into concrete source-scoped unknowns.
		walker.processWildcardUnknownEntries(
				localUnresolvedColumnMap,
				localInterface,
				localTableAliasMap,
				localTableCollection,
				localCurrentQueryDictionary);

		// Resolve relational-modifier derived columns (PIVOT/UNPIVOT) before
		// wildcard fallback can materialize them into physical table dictionaries.
		resolveRelationalModifierDerivedColumnsFromUnresolvedMap(
				localRelationalModifierInterfaceHints,
				localDerivedColumns,
				localUnresolvedColumnMap);

		walker.moveUnknownEntriesToSingleWildcardBackedNonTableSource(
				localUnresolvedColumnMap,
				localCurrentQueryDictionary,
				localTableAliasMap);

		HashMap<String, Object> currentTableDictionary = walker.getCurrentTableDictionary();
		propagateUnqualifiedSelectStarToScopeTables(
				localInterface,
				localCurrentQueryDictionary,
				localTableCollection);
		HashMap<String, Object> localFromTableCollection =
				buildLocalPhysicalFromTableCollection(localTableCollection);
		HashMap<String, Object> visibleQuerySourceCollection = collectVisibleQuerySourceCollection(localTableAliasMap);
		if (isUpdateScope && updateHasFromClause) {
			HashMap<String, Object> fromInputTableCollection = new HashMap<String, Object>(localTableCollection);
			pruneUpdateTargetFromInputTableCollection(
					fromInputTableCollection,
					updateTargetTableRef,
					localTableAliasMap);
			resolveUpdateRhsUnqualifiedAssignmentColumnsToTargetTable(
					localUnresolvedColumnMap,
					fromInputTableCollection,
					localTableCollection,
					localTableAliasMap,
					visibleQuerySourceCollection,
					updateTargetTableRef);
			walker.symbolTable.remove(TEMP_UPDATE_ASSIGNMENT_RHS_TOKENS_KEY);
		}
		resolveRelationalModifierDerivedColumnsFromUnresolvedMap(
				localRelationalModifierInterfaceHints,
				localDerivedColumns,
				localUnresolvedColumnMap);
		if (!localUnresolvedColumnMap.isEmpty()) {
			// Bind unqualified refs to the sole local physical FROM table before ambiguity checks.
			relocateUnqualifiedToSingleTableExcludingOutputAliases(
					localUnresolvedColumnMap,
					localFromTableCollection,
					localInterface);
		}
		HashMap<String, Object> effectiveAliasMap = buildEffectiveVisibleAliasMap(localTableAliasMap);
		HashMap<String, Object> effectiveTableCollection = buildEffectiveVisibleTableCollection(localTableCollection);

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
						groupedByList,
						orderedByList,
						localTableAliasMap,
						localTableCollection,
						localCurrentQueryDictionary,
						localUnresolvedColumnMap);
			}
			emitExplicitQualifiedUnknownDiagnostics(
					explicitQualifiedUnknownEntries,
					effectiveAliasMap,
					effectiveTableCollection,
					visibleQuerySourceCollection,
					localUnresolvedColumnMap,
					localTableCollection,
					localInterface,
					localCurrentQueryDictionary,
					deleteTargetTableRef,
					deleteTargetAlias,
					localDerivedColumns,
					localRelationalModifierInterfaceHints);

			// Only relocate unresolved unqualified columns to a single target when no column
			// still has multiple viable logical sources in scope.
			boolean hasMultiSourceUnqualifiedUnknown = hasUnqualifiedUnknownWithMultipleViableSources(
					localUnresolvedColumnMap,
					localFromTableCollection,
					null,
					localTableAliasMap);
			if (!hasMultiSourceUnqualifiedUnknown) {
				relocateUnqualifiedToSingleTableExcludingOutputAliases(
						localUnresolvedColumnMap,
						localFromTableCollection,
						localInterface);
			}
		}

		if (isUpdateScope && updateHasFromClause) {
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
							&& (MUMBLE_COLUMN_KEY.equals(substitutionType)
									|| MUMBLE_PREDICAND_KEY.equals(substitutionType))) {
						if (tableRef != null && !tableRef.isBlank() && !"*".equals(tableRef)
								&& columnName != null && !columnName.isBlank()) {
							QualifiedScopeResolutionResult substitutionResolutionResult =
									resolveQualifiedColumnAgainstVisibleScope(
											tableRef,
											columnName,
											effectiveAliasMap,
											effectiveTableCollection,
											visibleQuerySourceCollection,
											deferCorrelatedValueSubqueryQualifiedUnknowns,
											localDerivedColumns,
											localRelationalModifierInterfaceHints);
							if (substitutionResolutionResult.status
									== QualifiedScopeResolutionStatus.RESOLVED_QUERY_SOURCE
									|| substitutionResolutionResult.status
											== QualifiedScopeResolutionStatus.RESOLVED_PHYSICAL_SOURCE
									|| substitutionResolutionResult.status
											== QualifiedScopeResolutionStatus.RESOLVED_DERIVED_COLUMN) {
								Object qualifiedTokens = consumeQualifiedUnknownEntry(
										localUnresolvedColumnMap,
										tableRef,
										columnName);
								if (substitutionResolutionResult.status
										== QualifiedScopeResolutionStatus.RESOLVED_QUERY_SOURCE) {
									materializeResolvedQualifiedQuerySourceReference(
											tableRef,
											columnName,
											substitutionResolutionResult.querySourceRef,
											qualifiedTokens != null ? qualifiedTokens : refObj,
											localUnresolvedColumnMap,
											effectiveAliasMap,
											false);
								}
							}
						}
						continue;
					}

					if (columnName == null || "*".equals(columnName)) {
						continue;
					}

					if (tableRef != null) {
						QualifiedScopeResolutionResult resolutionResult =
								resolveQualifiedColumnAgainstVisibleScope(
										tableRef,
										columnName,
										effectiveAliasMap,
										effectiveTableCollection,
										visibleQuerySourceCollection,
										deferCorrelatedValueSubqueryQualifiedUnknowns,
										localDerivedColumns,
										localRelationalModifierInterfaceHints);

						switch (resolutionResult.status) {
							case RESOLVED_DERIVED_COLUMN -> {
								consumeDerivedColumnUnknownEntry(
										localUnresolvedColumnMap,
										tableRef,
										columnName);
							}
							case RESOLVED_QUERY_SOURCE -> {
								materializeResolvedQualifiedQuerySourceReference(
										tableRef,
										columnName,
										resolutionResult.querySourceRef,
										refObj,
										localUnresolvedColumnMap,
										effectiveAliasMap,
										true);
							}
							case RESOLVED_WILDCARD_QUERY_SOURCE -> {
								Object wildcardEntry = consumeQualifiedUnknownEntry(
										localUnresolvedColumnMap,
										tableRef,
										columnName);
								if (wildcardEntry != null) {
									promoteQualifiedWildcardIntoQuerySource(
											resolutionResult.querySourceRef,
											wildcardEntry);
								}
							}
							case RESOLVED_PHYSICAL_SOURCE -> {
								// Physical lineage belongs in table_dictionary only.
							}
							case DEFERRED -> {
								continue;
							}
							case UNRESOLVED_QUERY_SOURCE -> {
								// Fatal for query-alias references is already emitted by
								// emitExplicitQualifiedUnknownDiagnostics.
								hasSpecificResolutionFatalForOutputColumn = true;
								continue;
							}
							case UNRESOLVED_PHYSICAL_SOURCE -> {
								String unresolvedQualifiedKey = tableRef + "." + columnName;
								if (walker.hasEmittedQualifiedSourceNotFoundFatal(unresolvedQualifiedKey)) {
									hasSpecificResolutionFatalForOutputColumn = true;
									continue;
								}
								Integer[] refLocation = (localCurrentQueryDictionary == null)
										? new Integer[] { null, null }
										: walker.getLineAndCharacterFromEntry(
												localCurrentQueryDictionary.get(outputCol));
								if (refLocation[0] == null || refLocation[1] == null) {
									refLocation = walker.getFirstEntryLineAndCharacter(
											localCurrentQueryDictionary);
								}
								emitQualifiedPhysicalColumnNotFoundFatal(
										columnName,
										tableRef,
										resolutionResult.resolvedPhysicalTableRef,
										refLocation);
								hasSpecificResolutionFatalForOutputColumn = true;
							}
							default -> {
							}
						}
					} else {
						Integer[] refLocation = resolveUnqualifiedReferenceLocation(
								columnName,
								refObj,
								localUnresolvedColumnMap,
								localCurrentQueryDictionary,
								outputCol);

						UnqualifiedScopeResolutionResult resolutionResult =
								resolveUnqualifiedColumnAgainstVisibleScope(
										columnName,
										localFromTableCollection,
										localFromTableCollection,
										null,
										localTableAliasMap,
										deleteTargetTableRef,
										false,
										!emitFinalUnresolvedUnknownFatal,
										localRelationalModifierInterfaceHints);
						if (resolutionResult.status == UnqualifiedScopeResolutionStatus.RESOLVED) {
							refs.set(refIndex, cloneReferenceWithResolvedTableRef(
									refObj,
									resolutionResult.resolvedSourceRef));
						} else if (resolutionResult.status == UnqualifiedScopeResolutionStatus.AMBIGUOUS) {
							if (shouldSuppressAmbiguousUnqualifiedDiagnostic(columnName, refLocation)) {
								continue;
							}
							hasSpecificResolutionFatalForOutputColumn = true;
						} else if (resolutionResult.status == UnqualifiedScopeResolutionStatus.UNRESOLVED) {
							hasSpecificResolutionFatalForOutputColumn = true;
						}

						applyUnqualifiedScopeResolutionResult(
								resolutionResult,
								columnName,
								refObj,
								refLocation,
								localUnresolvedColumnMap,
								localFromTableCollection,
								localTableAliasMap,
								localCurrentQueryDictionary,
								localInterface,
								visibleQuerySourceCollection,
								null,
								null,
								deferCorrelatedValueSubqueryQualifiedUnknowns,
								false,
								shouldSuppressAmbiguousUnqualifiedDiagnostic(columnName, refLocation));
					}
				}
			}
		}
		}

		applyUnpivotValueInterfaceDerivations(
				localInterface,
				localRelationalModifierInterfaceHints);

		materializeSelectedUnpivotInColumnsIntoSourceDictionary(
				localCurrentQueryDictionary,
				localTableCollection,
				localTableAliasMap,
				localRelationalModifierInterfaceHints);

		applyPivotValueInterfaceDerivations(
				localInterface,
				localRelationalModifierInterfaceHints);

		filtersList = applyUnpivotValueDerivationsToReferenceListObject(
				filtersList,
				localRelationalModifierInterfaceHints);
		groupedByList = applyUnpivotValueDerivationsToReferenceListObject(
				groupedByList,
				localRelationalModifierInterfaceHints);
		orderedByList = applyUnpivotValueDerivationsToReferenceListObject(
				orderedByList,
				localRelationalModifierInterfaceHints);

		// Resolve ingress-captured unqualified entries (nested-scope merges, UPDATE no-FROM, etc.).
		// Archived clause lists are validated separately via probeArchivedScopeClauseColumns.
		if (!deferCorrelatedValueSubqueryQualifiedUnknowns) {
			resolveRemainingUnresolvedAgainstQuerySources(
					localUnresolvedColumnMap,
					null,
					localFromTableCollection,
					visibleQuerySourceCollection,
					localTableAliasMap,
					localCurrentQueryDictionary,
					localInterface,
					deleteTargetTableRef);
		}

		probeArchivedScopeClauseColumns(
				filtersList,
				groupedByList,
				orderedByList,
				localInterface,
				localCurrentQueryDictionary,
				localUnresolvedColumnMap,
				localFromTableCollection,
				localTableCollection,
				visibleQuerySourceCollection,
				effectiveAliasMap,
				effectiveTableCollection,
				localTableAliasMap,
				localDerivedColumns,
				localRelationalModifierInterfaceHints,
				deleteTargetTableRef,
				deferCorrelatedValueSubqueryQualifiedUnknowns);

		// If UPDATE with no FROM was deferred, merge resolved target columns back into localTableCollection
		if (isUpdateScope && !updateHasFromClause) {
			HashMap<String, Object> updateTargetTableCollection = (HashMap<String, Object>) walker.symbolTable.remove(TEMP_UPDATE_NODEFROM_TARGET_TABLE_COLLECTION_KEY);
			if (updateTargetTableCollection != null && !updateTargetTableCollection.isEmpty()) {
				mergeUpdateTargetAndLhsIntoTableDictionary(
						updateTargetTableCollection,
						localLhsUnresolvedColumnMap,
						localTableCollection,
						localTableAliasMap,
						updateTargetTableRef);
				localLhsUnresolvedColumnMap.clear();
			}
		}

		registerUnpivotGeneratedColumnAmbiguitySuppressions(
				localRelationalModifierInterfaceHints,
				localCurrentQueryDictionary);

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

		patchInterfaceTableRefsForSinglePhysicalTableScope(localInterface, localTableCollection);

		 walker.validateQueryInterface(localInterface, localCurrentQueryDictionary, effectiveAliasMap, effectiveTableCollection);

		// Merge everything back together into the final symbol table for this level of the query, with table aliases first, 
		// then symbol table entries, then query entries, then table entries, then the interface and current query dictionary 
		// for this level, and finally any filters list for this level if it exists.
		if (localTableAliasMap != null && !localTableAliasMap.isEmpty())
			walker.symbolTable.put(MUMBLE_TABLE_ALIAS_KEY, localTableAliasMap);
		HashMap<String, Object> tableDictionaryForSymbolTable = new HashMap<String, Object>();
		for (Map.Entry<String, Object> tableEntry : localTableCollection.entrySet()) {
			String tableRef = tableEntry.getKey();
			if (tableRef != null && tableRef.startsWith("<")) {
				Object substitutionType = walker.substitutionsMap == null ? null : walker.substitutionsMap.get(tableRef);
				if (substitutionType != null && !MUMBLE_TUPLE_KEY.equals(substitutionType.toString())) {
					continue;
				}
			}
			if (tableRef == null
					|| tableRef.isBlank()
					|| isQuerySourceReference(tableRef)
					|| walker.isNonTableQuerySourceReference(tableRef)) {
				continue;
			}
			tableDictionaryForSymbolTable.put(tableRef, tableEntry.getValue());
		}
		if (tableDictionaryForSymbolTable.isEmpty()) {
			walker.symbolTable.remove(MUMBLE_TABLE_DICTIONARY_KEY);
		} else {
			walker.symbolTable.put(MUMBLE_TABLE_DICTIONARY_KEY, tableDictionaryForSymbolTable);
		}
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
		if (!localDerivedColumns.isEmpty()) {
			walker.symbolTable.put(DERIVED_COLUMNS_HINTS_KEY, localDerivedColumns);
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
		if (preservedInsertSourceSelectSequence != null) {
			walker.symbolTable.put(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY, preservedInsertSourceSelectSequence);
		}

		if (localTableCollection != null && !localTableCollection.isEmpty()) {
			walker.mergeTableDictionaryIntoWalkerTableDictionary(localTableCollection);
		}

		return walker.symbolTable;
	}

	/**
	 * Mid-FROM reconcile after {@code join_extension_primary} (PIVOT / UNPIVOT / lateral extensions).
	 * <p>
	 * This is <b>not</b> a scope publish: it runs while the query_specification is still open so
	 * relational-modifier aliases and partial {@code table_dictionary} state stay aligned before
	 * the next FROM/JOIN fragment. Scope finalization still happens in
	 * {@link #finalizeQueryScopeSymbolTable} at {@code exitQuery_specification}.
	 * <p>
	 * Delegates to {@link #convertSymbolTableToTableDictionary} with no UPDATE target and no
	 * statement-boundary fatals ({@code emitFinalUnresolvedUnknownFatal=false}).
	 */
	public void reconcileJoinExtensionSymbolTable() {
		convertSymbolTableToTableDictionary(false, false, null);
	}

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
		String tableName = null;
		if (tableObj instanceof String tableString && !tableString.isBlank()) {
			tableName = tableString;
		} else {
			@SuppressWarnings("unchecked")
			Object substitutionObj = tableNode.get(MUMBLE_SUBSTITUTION_KEY);
			if (substitutionObj instanceof Map<?, ?> substitutionMapObj) {
				Object substitutionNameObj = ((Map<String, Object>) substitutionMapObj).get(MUMBLE_NAME_KEY);
				if (substitutionNameObj instanceof String substitutionName && !substitutionName.isBlank()) {
					tableName = substitutionName;
				}
			}
		}
		if (tableName == null || tableName.isBlank()) {
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
	 * INSERT statements skip UPDATE-style rehome but still emit unresolved-column diagnostics at the
	 * insert boundary. Call {@link #finalizeTopLevelUnresolvedColumnsAtInsertBoundary()} from
	 * {@code exitInsert_expression} (before the SQL tree is attached) or rely on
	 * {@link #isInsertStatementSqlTree()} at {@code exitSql}.
	 */
	public void finalizeTopLevelUnresolvedColumns() {
		finalizeTopLevelUnresolvedColumns(false);
	}

	/** INSERT boundary finalize: emit deferred SELECT unresolved diagnostics; skip UPDATE rehome. */
	public void finalizeTopLevelUnresolvedColumnsAtInsertBoundary() {
		finalizeTopLevelUnresolvedColumns(true);
	}

	@SuppressWarnings("unchecked")
	public void finalizeTopLevelUnresolvedColumns(boolean insertStatementBoundary) {
		HashMap<String, Object> unresolvedMap = collectStatementBoundaryUnresolvedColumns();
		if (unresolvedMap == null || unresolvedMap.isEmpty()) {
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
		return MUMBLE_TUPLE_KEY.equals(typeObject);
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
			if (isLiveOrDefinitionScopeKeyPresent(candidate)) {
				return candidate;
			}
		}

		for (String prefix : orderedPrefixes) {
			String candidate = prefix + scopeIndex;
			if (isLiveOrDefinitionScopeKeyPresent(candidate)) {
				return candidate;
			}
		}

		return MUMBLE_QUERY_KEY + nextSyntheticWithQueryAliasIndex();
	}

	private boolean isLiveOrDefinitionScopeKeyPresent(String scopeKey) {
		if (scopeKey == null || scopeKey.isBlank()) {
			return false;
		}
		return walker.symbolTable.containsKey(scopeKey)
				|| walker.symbolTable.containsKey(toDefinitionScopeKey(scopeKey));
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
	public Map<String, Object> ensureContextListSymbolMap() {
		Object contextListObject = walker.symbolTable.get(MUMBLE_CONTEXT_LIST_KEY);
		if (contextListObject instanceof Map<?, ?> contextListMapObject) {
			return (Map<String, Object>) contextListMapObject;
		}

		Map<String, Object> contextListMap = new LinkedHashMap<String, Object>();
		walker.symbolTable.put(MUMBLE_CONTEXT_LIST_KEY, contextListMap);
		return contextListMap;
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
	public void removeUnresolvedColumnEntry(String columnName) {
		if (columnName == null || columnName.isBlank()) {
			return;
		}

		Object unresolvedObject = walker.symbolTable.get(MUMBLE_UNRESOLVED_COLUMN_KEY);
		if (!(unresolvedObject instanceof HashMap<?, ?> unresolvedMapObj)) {
			return;
		}

		HashMap<String, Object> unresolvedMap = (HashMap<String, Object>) unresolvedMapObj;
		removeUnresolvedMapEntry(unresolvedMap, columnName);
		if (unresolvedMap.isEmpty()) {
			walker.symbolTable.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
		}
	}

	@SuppressWarnings("unchecked")
	public void mergeInsertScopeTableDictionaryIntoGlobal(String insertScopeKey) {
		if (insertScopeKey == null || insertScopeKey.isBlank()) {
			return;
		}

		// publishQueryLikeScope stores the payload under def_<key> and removes the live key,
		// so we must look up the definition key here.
		String insertDefinitionScopeKey = toDefinitionScopeKey(insertScopeKey);
		Object insertScopeObj = walker.symbolTable.get(insertDefinitionScopeKey);
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
		String insertDefinitionScopeKey = toDefinitionScopeKey(insertScopeKey);
		Object insertScopeObj = walker.symbolTable.get(insertDefinitionScopeKey);
		if (!(insertScopeObj instanceof Map<?, ?> insertScopeMapObj)) {
			return;
		}

		Map<String, Object> insertScopeMap = (Map<String, Object>) insertScopeMapObj;
		Object queryDictionaryObj = insertScopeMap.get(MUMBLE_QUERY_DICTIONARY_KEY);
		HashMap<String, Object> queryDictionary = (queryDictionaryObj instanceof Map<?, ?> queryDictionaryMapObj)
				? new HashMap<String, Object>((Map<String, Object>) queryDictionaryMapObj)
				: new HashMap<String, Object>();

		// Explicit target lists populate query_dictionary during populateInsertTargetColumnsFromTargetSubtree.
		// Implicit inserts seed from the insert-scope target table_dictionary (same refs, explicit path).
		HashMap<String, Object> seededQueryDictionary =
				seedInsertScopeQueryDictionaryFromTargetTableDictionary(insertScopeMap);
		if (!seededQueryDictionary.isEmpty()) {
			for (Map.Entry<String, Object> seededEntry : seededQueryDictionary.entrySet()) {
				String columnName = seededEntry.getKey();
				if (columnName == null) {
					continue;
				}
				Object existingRefs = queryDictionary.get(columnName);
				if (existingRefs == null) {
					queryDictionary.put(columnName, seededEntry.getValue());
				} else {
					queryDictionary.put(columnName, mergeReferenceCollections(existingRefs, seededEntry.getValue()));
				}
			}
		}

		sanitizeQueryDictionaryForGlobalExport(queryDictionary);
		if (queryDictionary.isEmpty()) {
			return;
		}

		mergeIntoGlobalQueryColumnDictionary(toLiveScopeKey(insertDefinitionScopeKey), queryDictionary);
		insertScopeMap.put(MUMBLE_QUERY_DICTIONARY_KEY, queryDictionary);
	}

	@SuppressWarnings("unchecked")
	public void publishUpdateScopeQueryDictionary(String updateScopeKey) {
		if (updateScopeKey == null || updateScopeKey.isBlank()) {
			return;
		}
		String updateDefinitionScopeKey = toDefinitionScopeKey(updateScopeKey);
		Object updateScopeObj = walker.symbolTable.get(updateDefinitionScopeKey);
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
		mergeIntoGlobalQueryColumnDictionary(toLiveScopeKey(updateDefinitionScopeKey), queryDictionary);
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> seedInsertScopeQueryDictionaryFromTargetTableDictionary(
			Map<String, Object> insertScopeMap) {
		HashMap<String, Object> seeded = new HashMap<String, Object>();
		if (insertScopeMap == null || insertScopeMap.isEmpty()) {
			return seeded;
		}

		HashSet<String> allowedInsertOutputColumns = new HashSet<String>();
		Object insertInterfaceObj = insertScopeMap.get(MUMBLE_INTERFACE_KEY);
		if (insertInterfaceObj instanceof Map<?, ?> insertInterfaceMapObj) {
			Map<String, Object> insertInterfaceMap = (Map<String, Object>) insertInterfaceMapObj;
			allowedInsertOutputColumns.addAll(insertInterfaceMap.keySet());
		}

		Object localTableDictionaryObj = insertScopeMap.get(MUMBLE_TABLE_DICTIONARY_KEY);
		if (!(localTableDictionaryObj instanceof Map<?, ?> localTableDictionaryMapObj)) {
			return seeded;
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
			return seeded;
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
				seeded.put(columnName, new ArrayList<Object>((ArrayList<Object>) refsListObj));
			}
		}

		return seeded;
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
		return normalizeQuerySourceReference(sourceRef);
	}

	public boolean isInsertSourceScopeReference(String sourceRef) {
		String normalizedSourceRef = normalizeQuerySourceReference(sourceRef);
		if (normalizedSourceRef == null || normalizedSourceRef.isBlank()) {
			return false;
		}
		return normalizedSourceRef.startsWith(MUMBLE_QUERY_KEY)
				|| normalizedSourceRef.startsWith(MUMBLE_UNION_KEY)
				|| normalizedSourceRef.startsWith(MUMBLE_INTERSECT_KEY)
				|| normalizedSourceRef.startsWith(MUMBLE_VALUES_KEY)
				|| normalizedSourceRef.startsWith(MUMBLE_INSERT_KEY)
				|| normalizedSourceRef.startsWith(MUMBLE_UPDATE_KEY)
				|| normalizedSourceRef.startsWith(MUMBLE_DELETE_KEY);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> normalizeInsertSourceDefinition(String sourceScopeKey) {
		if (sourceScopeKey == null || sourceScopeKey.isBlank()) {
			return new HashMap<String, Object>();
		}

		String definitionKey = toDefinitionScopeKey(sourceScopeKey);
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
		return normalizeQuerySourceReference(key);
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
	public void publishSetOperationInterfaceAtExit(String setOperationKey, Map<String, Object> setOperationDefinition) {
		if (setOperationDefinition == null || setOperationDefinition.isEmpty()
				|| !isSetOperationDefinition(setOperationDefinition)) {
			return;
		}

		ArrayList<Map<String, Object>> participants = collectSetOperationParticipantsInOrder(setOperationDefinition);
		if (participants.isEmpty()) {
			return;
		}

		ArrayList<String> outputColumnNames = extractInterfaceColumnNamesInOrder(participants.get(0));
		if (outputColumnNames.isEmpty()) {
			outputColumnNames = extractScopeOutputColumnNamesInSelectOrder(participants.get(0));
		}
		if (outputColumnNames.isEmpty()) {
			return;
		}

		LinkedHashMap<String, Object> mergedInterface = new LinkedHashMap<String, Object>();
		Object sentinelValue = "query_column";
		for (String outputColumnName : outputColumnNames) {
			mergedInterface.put(outputColumnName, sentinelValue);
		}

		if (!mergedInterface.isEmpty()) {
			setOperationDefinition.put(MUMBLE_INTERFACE_KEY, mergedInterface);
		}
	}

	private boolean hasDirectQueryParticipant(Map<String, Object> setOperationDefinition) {
		if (setOperationDefinition == null || setOperationDefinition.isEmpty()) {
			return false;
		}

		for (String key : setOperationDefinition.keySet()) {
			if (key == null) {
				continue;
			}
			String normalizedKey = key.startsWith("def_") ? key.substring("def_".length()) : key;
			if (normalizedKey.startsWith(MUMBLE_QUERY_KEY) || normalizedKey.startsWith(MUMBLE_VALUES_KEY)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Set-op exit finalization: always publish multi-ref interface; hoist insert sequence when
	 * the set-op is an insert source.
	 */
	@SuppressWarnings("unchecked")
	public void finalizeSetOperationAtExit(
			String setOperationKey,
			Map<String, Object> setOperationDefinition,
			boolean insertSource) {
		if (setOperationDefinition == null || setOperationDefinition.isEmpty()) {
			return;
		}

		publishSetOperationInterfaceAtExit(setOperationKey, setOperationDefinition);
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
	public Map<String, Object> extractScopeInterfaceMap(Map<String, Object> scopeDefinition) {
		if (scopeDefinition == null || scopeDefinition.isEmpty()) {
			return null;
		}

		Object interfaceObj = scopeDefinition.get(MUMBLE_INTERFACE_KEY);
		if (!(interfaceObj instanceof Map<?, ?> interfaceMapObj)) {
			return null;
		}

		return (Map<String, Object>) interfaceMapObj;
	}

	/**
	 * Resolve output-column order by SELECT-list position. Prefer recorded insert-source
	 * sequence when present; otherwise order interface keys by earliest query_dictionary token.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> extractScopeOutputColumnNamesInSelectOrder(Map<String, Object> scopeDefinition) {
		Map<String, Object> interfaceMap = extractScopeInterfaceMap(scopeDefinition);
		if (interfaceMap == null || interfaceMap.isEmpty()) {
			Object queryDictionaryObj = scopeDefinition == null ? null : scopeDefinition.get(MUMBLE_QUERY_DICTIONARY_KEY);
			if (!(queryDictionaryObj instanceof Map<?, ?> queryDictionaryMapObj) || queryDictionaryMapObj.isEmpty()) {
				return new ArrayList<String>();
			}

			ArrayList<String> derivedColumnNames = new ArrayList<String>();
			for (Object columnNameObj : queryDictionaryMapObj.keySet()) {
				if (columnNameObj instanceof String columnName && !columnName.isBlank()) {
					derivedColumnNames.add(columnName);
				}
			}
			if (derivedColumnNames.isEmpty()) {
				return derivedColumnNames;
			}

			Map<String, Object> queryDictionary = (Map<String, Object>) queryDictionaryMapObj;
			derivedColumnNames.sort((left, right) -> {
				long leftPosition = getEarliestQueryDictionaryTokenSortKey(queryDictionary.get(left));
				long rightPosition = getEarliestQueryDictionaryTokenSortKey(queryDictionary.get(right));
				int positionCompare = Long.compare(leftPosition, rightPosition);
				if (positionCompare != 0) {
					return positionCompare;
				}
				return left.compareTo(right);
			});
			return derivedColumnNames;
		}

		if (isSetOperationDefinition(scopeDefinition)) {
			Map<String, Object> firstParticipant = findFirstSetOperationParticipant(scopeDefinition);
			if (firstParticipant != null && firstParticipant != scopeDefinition) {
				ArrayList<String> fromFirstParticipant = extractScopeOutputColumnNamesInSelectOrder(firstParticipant);
				if (fromFirstParticipant.size() == interfaceMap.size()) {
					return fromFirstParticipant;
				}
			}
		}

		ArrayList<String> fromSequence = buildInsertSourceColumnSequenceList(scopeDefinition, interfaceMap);
		ArrayList<String> fromTokenOrder = extractInterfaceColumnNamesInSelectTokenOrder(scopeDefinition, interfaceMap);
		if (fromTokenOrder.size() == interfaceMap.size()) {
			return fromTokenOrder;
		}
		if (fromSequence.size() == interfaceMap.size()) {
			return fromSequence;
		}

		return extractInterfaceColumnNamesInOrder(scopeDefinition);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> extractInterfaceColumnNamesInSelectTokenOrder(
			Map<String, Object> scopeDefinition,
			Map<String, Object> interfaceMap) {
		ArrayList<String> columnNames = new ArrayList<String>();
		if (interfaceMap == null || interfaceMap.isEmpty()) {
			return columnNames;
		}

		for (Object columnNameObj : interfaceMap.keySet()) {
			if (columnNameObj instanceof String columnName && !columnName.isBlank()) {
				columnNames.add(columnName);
			}
		}
		if (columnNames.isEmpty()) {
			return columnNames;
		}

		Object queryDictionaryObj = scopeDefinition.get(MUMBLE_QUERY_DICTIONARY_KEY);
		if (!(queryDictionaryObj instanceof Map<?, ?> queryDictionaryMapObj)) {
			return new ArrayList<String>();
		}

		Map<String, Object> queryDictionary = (Map<String, Object>) queryDictionaryMapObj;
		ArrayList<String> orderedColumnNames = new ArrayList<String>(columnNames);
		orderedColumnNames.sort((left, right) -> {
			long leftPosition = getEarliestQueryDictionaryTokenSortKey(queryDictionary.get(left));
			long rightPosition = getEarliestQueryDictionaryTokenSortKey(queryDictionary.get(right));
			int positionCompare = Long.compare(leftPosition, rightPosition);
			if (positionCompare != 0) {
				return positionCompare;
			}
			return left.compareTo(right);
		});

		return orderedColumnNames;
	}

	public long getEarliestQueryDictionaryTokenSortKey(Object queryDictionaryEntry) {
		Integer[] location = walker.getLineAndCharacterFromEntry(queryDictionaryEntry);
		if (location[0] == null || location[1] == null) {
			return Long.MAX_VALUE;
		}

		return location[0].longValue() * 100000L + location[1].longValue();
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> extractInterfaceColumnNamesInOrder(Map<String, Object> scopeDefinition) {
		ArrayList<String> columnNames = new ArrayList<String>();
		Map<String, Object> interfaceMap = extractScopeInterfaceMap(scopeDefinition);
		if (interfaceMap == null || interfaceMap.isEmpty()) {
			return columnNames;
		}

		for (Object columnNameObj : interfaceMap.keySet()) {
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

		Map<String, Object> interfaceMap = extractScopeInterfaceMap(scopeDefinition);
		if (interfaceMap == null || interfaceMap.isEmpty()) {
			return refs;
		}

		ArrayList<String> columnNames = extractScopeOutputColumnNamesInSelectOrder(scopeDefinition);
		if (columnIndex >= columnNames.size()) {
			return refs;
		}

		Object refsObj = interfaceMap.get(columnNames.get(columnIndex));
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

		String subqueryRef = getSubqueryReferenceKey(symbols);
		if (subqueryRef == null || subqueryRef.isBlank()) {
			return;
		}
		String scopeRefKey = normalizeQuerySourceReference(subqueryRef);
		String definitionKey = toDefinitionScopeKey(scopeRefKey);

		if (!isDefinitionScopeKey(subqueryRef)) {
			Object scopeObj = symbols.remove(subqueryRef);
			if (scopeObj != null) {
				symbols.put(definitionKey, scopeObj);
			}
		}

		Object scopeDefinitionObj = symbols.get(definitionKey);
		if (scopeDefinitionObj instanceof Map<?, ?> scopeDefinitionMapObj) {
			Map<String, Object> scopeDefinition = (Map<String, Object>) scopeDefinitionMapObj;
			if (isSetOperationDefinition(scopeDefinition)) {
				finalizeSetOperationAtExit(definitionKey, scopeDefinition, true);
			}
		}

		symbols.put(INSERT_SOURCE_REF_KEY, scopeRefKey);
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
	public String getDeleteTargetAlias(Map<String, Object> deleteNode) {
		if (deleteNode == null) {
			return null;
		}

		Object targetTableObj = deleteNode.get(MUMBLE_TABLE_KEY);
		if (!(targetTableObj instanceof Map<?, ?> targetTableMapObj)) {
			return null;
		}

		Object aliasObj = ((Map<String, Object>) targetTableMapObj).get(MUMBLE_ALIAS_KEY);
		if (aliasObj instanceof String alias && !alias.isBlank()) {
			return alias;
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
		String lhsName = extractColumnNameFromColumnReferenceMap(lhsColumnReference);

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

	@SuppressWarnings("unchecked")
	public String extractColumnNameFromColumnReferenceMap(Map<String, Object> columnReference) {
		if (columnReference == null) {
			return null;
		}

		Object nameObject = columnReference.get(MUMBLE_NAME_KEY);
		if (nameObject instanceof String columnName && !columnName.isBlank()) {
			return columnName;
		}

		Object substitutionObject = columnReference.get(MUMBLE_SUBSTITUTION_KEY);
		if (substitutionObject instanceof Map<?, ?> substitutionMapObj) {
			Object substitutionNameObject = ((Map<String, Object>) substitutionMapObj).get(MUMBLE_NAME_KEY);
			if (substitutionNameObject != null && !substitutionNameObject.toString().isBlank()) {
				return substitutionNameObject.toString();
			}
		}

		return null;
	}

	public String makeQualifiedColumnReferenceKey(Map<String, Object> columnReference) {
		if (columnReference == null) {
			return null;
		}

		String columnName = extractColumnNameFromColumnReferenceMap(columnReference);
		if (columnName == null || columnName.isBlank()) {
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
			String lhsTokenString,
			Token rhsToken) {
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
			ArrayList<Object> astColumnReferences = new ArrayList<Object>();
			HashMap<String, Object> flattenTarget = new HashMap<String, Object>();
			flattenTarget.putAll(assignmentValue);
			flattenSubTreeForInterfaceColumns(flattenTarget, astColumnReferences);
			for (Object astColumnReference : astColumnReferences) {
				rhsColumnReferences.add(cloneColumnReferenceEntry(astColumnReference));
			}
		}
		assignmentsMap.put(assignmentKey, rhsColumnReferences);
		registerUpdateAssignmentRhsUnresolvedReferences(rhsColumnReferences, rhsToken);

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

	public Token resolveAssignmentRhsToken(SQLSelectParserParser.Assignment_expressionContext ctx) {
		if (ctx == null || ctx.getChildCount() == 0) {
			return null;
		}

		if (ctx.getChildCount() >= 3 && ctx.getChild(2) instanceof ParserRuleContext rightContext) {
			Token rhsToken = rightContext.getStop();
			if (rhsToken != null) {
				return rhsToken;
			}
		}

		return ctx.getStop();
	}

	public void registerUpdateAssignmentRhsUnresolvedReferences(
			ArrayList<Object> rhsColumnReferences,
			Token rhsToken) {
		if (rhsColumnReferences == null || rhsColumnReferences.isEmpty() || rhsToken == null) {
			return;
		}

		for (Object rhsRefObj : rhsColumnReferences) {
			String columnName = walker.extractReferenceNameFromInterfaceEntry(rhsRefObj);
			String tableRef = walker.extractReferenceTableRefFromInterfaceEntry(rhsRefObj);
			if (columnName == null || columnName.isBlank() || "*".equals(columnName)) {
				continue;
			}
			if (tableRef != null && !tableRef.isBlank() && !"*".equals(tableRef)) {
				continue;
			}

			walker.collectUnresolvedColumnReference(MUMBLE_UNKNOWN_KEY, rhsRefObj, rhsToken);
			recordUpdateAssignmentRhsToken(columnName, rhsToken);
		}
	}

	@SuppressWarnings("unchecked")
	private void recordUpdateAssignmentRhsToken(String columnName, Token rhsToken) {
		if (columnName == null || columnName.isBlank() || rhsToken == null) {
			return;
		}

		Object tokensObj = walker.symbolTable.get(TEMP_UPDATE_ASSIGNMENT_RHS_TOKENS_KEY);
		HashMap<String, Object> assignmentRhsTokens;
		if (tokensObj instanceof HashMap<?, ?>) {
			assignmentRhsTokens = (HashMap<String, Object>) tokensObj;
		} else {
			assignmentRhsTokens = new HashMap<String, Object>();
			walker.symbolTable.put(TEMP_UPDATE_ASSIGNMENT_RHS_TOKENS_KEY, assignmentRhsTokens);
		}

		assignmentRhsTokens.putIfAbsent(columnName, rhsToken.toString());
	}

	@SuppressWarnings("unchecked")
	private ArrayList<Object> resolveUpdateAssignmentRhsTokenRefs(String columnName) {
		Object tokensObj = walker.symbolTable.get(TEMP_UPDATE_ASSIGNMENT_RHS_TOKENS_KEY);
		if (!(tokensObj instanceof HashMap<?, ?> assignmentRhsTokensObj)) {
			return null;
		}

		Object tokenRefObj = ((HashMap<String, Object>) assignmentRhsTokensObj).get(columnName);
		if (tokenRefObj == null) {
			for (Map.Entry<String, Object> entry : ((HashMap<String, Object>) assignmentRhsTokensObj).entrySet()) {
				if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(columnName)) {
					tokenRefObj = entry.getValue();
					break;
				}
			}
		}
		if (!(tokenRefObj instanceof String tokenRef) || tokenRef.isBlank()) {
			return null;
		}

		ArrayList<Object> tokenRefs = new ArrayList<Object>();
		tokenRefs.add(tokenRef);
		return tokenRefs;
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

	/**
	 * Computes parent/subquery unresolved flags for a query_specification exit and
	 * finalizes the scope under {@code query<N>}.
	 */
	public void finalizeQueryScopeSymbolTable(
			SQLSelectParserParser.Query_specificationContext ctx,
			Map<String, Object> querySpecificationSubMap,
			boolean projectSelectIntoTarget) {
		Integer symbolScopeLevel = walker.stackSymbols.get("symbolTable");
		boolean hasParentQueryScope = symbolScopeLevel != null && symbolScopeLevel > 2;
		Integer subqueryParentRuleIndex = walker.findNearestSubqueryParentRuleIndex(ctx);
		boolean passUpQualifiedUnresolvedFromThisSubquery =
				walker.shouldPassUpQualifiedUnresolvedForSubqueryParent(subqueryParentRuleIndex);
		boolean emitQualifiedUnresolvedFromThisSubquery =
				walker.shouldEmitQualifiedUnresolvedForSubqueryParent(subqueryParentRuleIndex);
		boolean deferSubqueryUnresolvedDiagnosticsToStatementBoundary =
				shouldDeferSubqueryUnresolvedDiagnosticsToStatementBoundary(ctx);

		boolean isStatementTopLevelQueryScope = isMainStatementQuerySpecification(ctx);
		finalizeQueryScopeSymbolTable(
				"query" + walker.queryCount,
				querySpecificationSubMap,
				projectSelectIntoTarget,
				hasParentQueryScope,
				passUpQualifiedUnresolvedFromThisSubquery,
				emitQualifiedUnresolvedFromThisSubquery,
				deferSubqueryUnresolvedDiagnosticsToStatementBoundary,
				isStatementTopLevelQueryScope);
	}

	/** True for the outermost {@code query_specification} not nested under a {@code Subquery} node. */
	private boolean isMainStatementQuerySpecification(ParserRuleContext ctx) {
		if (ctx == null) {
			return false;
		}
		ParserRuleContext ancestor = ctx.getParent();
		while (ancestor != null) {
			if (ancestor instanceof SQLSelectParserParser.SubqueryContext) {
				return false;
			}
			ancestor = ancestor.getParent();
		}
		return true;
	}

	/**
	 * Finalizes a leaf SELECT / query_specification scope the same way VALUES scopes do:
	 * convert symbol table, export query dictionary, publish scope payload, then bubble
	 * or emit deferred unresolved columns according to parent/subquery flags.
	 */
	@SuppressWarnings("unchecked")
	public void finalizeQueryScopeSymbolTable(
			String scopeKey,
			Map<String, Object> querySpecificationSubMap,
			boolean projectSelectIntoTarget,
			boolean hasParentQueryScope,
			boolean passUpQualifiedUnresolvedFromThisSubquery,
			boolean emitQualifiedUnresolvedFromThisSubquery,
			boolean deferSubqueryUnresolvedDiagnosticsToStatementBoundary,
			boolean isStatementTopLevelQueryScope) {
		boolean emitFinalUnresolvedUnknownFatal = !hasParentQueryScope;
		boolean deferCorrelatedValueSubqueryQualifiedUnknowns = hasParentQueryScope
				&& passUpQualifiedUnresolvedFromThisSubquery;

		HashMap<String, Object> scopeSymbols = convertSymbolTableToTableDictionary(
				emitFinalUnresolvedUnknownFatal,
				deferCorrelatedValueSubqueryQualifiedUnknowns,
				null);

		HashMap<String, Object> localCurrentQueryDictionary =
				(HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_QUERY_DICTIONARY_KEY);
		if (localCurrentQueryDictionary == null) {
			localCurrentQueryDictionary = new HashMap<String, Object>();
		}

		sanitizeQueryDictionaryForGlobalExport(localCurrentQueryDictionary);
		mergeIntoGlobalQueryColumnDictionary(scopeKey, localCurrentQueryDictionary);
		scopeSymbols.put(MUMBLE_QUERY_DICTIONARY_KEY, localCurrentQueryDictionary);
		if (projectSelectIntoTarget && querySpecificationSubMap != null) {
			projectSelectIntoTargetFromInterface(querySpecificationSubMap, scopeSymbols, localCurrentQueryDictionary);
		}
		walker.symbolTable.remove(MUMBLE_SCALAR_SUBQUERY_ALIASES_KEY);

		HashMap<String, Object> unresolvedMap =
				(HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
		HashMap<String, Object> scopeTreeUnresolved = new HashMap<String, Object>();
		collectAndStripUnresolvedFromScopeTree(scopeSymbols, scopeTreeUnresolved);
		if (!scopeTreeUnresolved.isEmpty()) {
			if (unresolvedMap == null) {
				unresolvedMap = new HashMap<String, Object>();
			}
			walker.mergeUnknownEntries(unresolvedMap, scopeTreeUnresolved);
		}
		HashMap<String, Object> qualifiedUnresolvedForParent = new HashMap<String, Object>();
		HashMap<String, Object> unqualifiedUnresolvedForLocal = new HashMap<String, Object>();
		if (unresolvedMap != null && !unresolvedMap.isEmpty()) {
			splitUnresolvedEntriesByQualification(
					unresolvedMap, qualifiedUnresolvedForParent, unqualifiedUnresolvedForLocal);
			// For deferred correlated subqueries (EXISTS, etc.), pass up ALL unresolved columns to the parent.
			// The parent query will resolve them after its own assembly is complete.
			if (!deferSubqueryUnresolvedDiagnosticsToStatementBoundary && !passUpQualifiedUnresolvedFromThisSubquery) {
				if (!canResolveUnqualifiedFromSingleWildcardQuerySource(unqualifiedUnresolvedForLocal)) {
					emitUnqualifiedUnresolvedColumnsError(unqualifiedUnresolvedForLocal);
				}
				HashMap<String, Object> tableAliasMap =
						(HashMap<String, Object>) walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY);
				emitQualifiedQueryAliasUnresolvedColumnsFatalAndPrune(
						qualifiedUnresolvedForParent,
						tableAliasMap);
			}
		}

		if (!deferSubqueryUnresolvedDiagnosticsToStatementBoundary && !qualifiedUnresolvedForParent.isEmpty()) {
			if (emitQualifiedUnresolvedFromThisSubquery && !isStatementTopLevelQueryScope) {
				HashMap<String, Object> localScopeFatals = new HashMap<String, Object>();
				HashMap<String, Object> deferToStatementTop = new HashMap<String, Object>();
				@SuppressWarnings("unchecked")
				HashMap<String, Object> tableAliasMap =
						(walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY) instanceof HashMap<?, ?>)
								? (HashMap<String, Object>) walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY)
								: new HashMap<String, Object>();
				@SuppressWarnings("unchecked")
				HashMap<String, Object> tableCollection =
						(walker.symbolTable.get(MUMBLE_TABLE_DICTIONARY_KEY) instanceof HashMap<?, ?>)
								? (HashMap<String, Object>) walker.symbolTable.get(MUMBLE_TABLE_DICTIONARY_KEY)
								: new HashMap<String, Object>();
				HashMap<String, Object> visibleAliasMap = buildEffectiveVisibleAliasMap(tableAliasMap);
				HashMap<String, Object> visibleTableCollection = buildEffectiveVisibleTableCollection(tableCollection);
				for (Map.Entry<String, Object> entry : qualifiedUnresolvedForParent.entrySet()) {
					if (shouldDeferQualifiedUntilStatementTop(
							entry.getKey(),
							visibleAliasMap,
							visibleTableCollection)) {
						deferToStatementTop.put(entry.getKey(), entry.getValue());
					} else {
						localScopeFatals.put(entry.getKey(), entry.getValue());
					}
				}
				boolean emitLocalScopeFatals = !deferSubqueryUnresolvedDiagnosticsToStatementBoundary;
				resolveQualifiedUnresolvedEntries(
						localScopeFatals,
						copyLocalScopeAliasMap(tableAliasMap),
						copyLocalScopeTableCollection(tableCollection),
						emitLocalScopeFatals);
				qualifiedUnresolvedForParent.clear();
				qualifiedUnresolvedForParent.putAll(localScopeFatals);
				qualifiedUnresolvedForParent.putAll(deferToStatementTop);
			}
			boolean emitRemainingScopeFatals = !deferSubqueryUnresolvedDiagnosticsToStatementBoundary
					&& isStatementTopLevelQueryScope;
			resolveQualifiedUnresolvedAtQueryScopeExit(
					qualifiedUnresolvedForParent,
					emitRemainingScopeFatals);
			if (isStatementTopLevelQueryScope) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> tableAliasMap =
						(walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY) instanceof HashMap<?, ?>)
								? (HashMap<String, Object>) walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY)
								: new HashMap<String, Object>();
				@SuppressWarnings("unchecked")
				HashMap<String, Object> tableCollection =
						(walker.symbolTable.get(MUMBLE_TABLE_DICTIONARY_KEY) instanceof HashMap<?, ?>)
								? (HashMap<String, Object>) walker.symbolTable.get(MUMBLE_TABLE_DICTIONARY_KEY)
								: new HashMap<String, Object>();
				HashMap<String, Object> visibleAliasMap = buildEffectiveVisibleAliasMap(tableAliasMap);
				mergePublishedScopeContextListIntoAliasMap(visibleAliasMap);
				resolveQualifiedUnresolvedEntries(
						walker.globalQualifiedUnresolvedLocations,
						visibleAliasMap,
						buildEffectiveVisibleTableCollection(tableCollection),
						false);
			}
		}

		materializeRemainingSingleTableUnqualifiedAtScopeExit(unqualifiedUnresolvedForLocal, scopeSymbols);
		mergeScopeTableDictionaryIntoGlobalWalkerDictionary(scopeSymbols);

		publishQueryLikeScope(scopeKey, scopeSymbols);

		if (deferSubqueryUnresolvedDiagnosticsToStatementBoundary) {
			HashMap<String, Object> deferredUnresolvedForParent = new HashMap<String, Object>();
			deferredUnresolvedForParent.putAll(unqualifiedUnresolvedForLocal);
			deferredUnresolvedForParent.putAll(qualifiedUnresolvedForParent);
			mergeUnresolvedEntriesIntoCurrentScope(deferredUnresolvedForParent);
			return;
		}

		// For correlated subqueries (passUpQualifiedUnresolvedFromThisSubquery=true), pass up ALL unresolved
		// columns (both qualified and unqualified) to the parent query for resolution.
		if (passUpQualifiedUnresolvedFromThisSubquery) {
			HashMap<String, Object> allUnresolvedForParent = new HashMap<String, Object>();
			allUnresolvedForParent.putAll(qualifiedUnresolvedForParent);
			allUnresolvedForParent.putAll(unqualifiedUnresolvedForLocal);
			if (!allUnresolvedForParent.isEmpty()) {
				Object parentUnresolvedObject = walker.symbolTable.get(MUMBLE_UNRESOLVED_COLUMN_KEY);
				if (parentUnresolvedObject instanceof HashMap<?, ?>) {
					walker.mergeUnknownEntries(
							(HashMap<String, Object>) parentUnresolvedObject, allUnresolvedForParent);
				} else {
					walker.symbolTable.put(MUMBLE_UNRESOLVED_COLUMN_KEY, allUnresolvedForParent);
				}
			}
			return;
		}

		if (!passUpQualifiedUnresolvedFromThisSubquery && !qualifiedUnresolvedForParent.isEmpty()) {
			Object archivedUnresolved = scopeSymbols.get(MUMBLE_UNRESOLVED_COLUMN_KEY);
			if (archivedUnresolved instanceof HashMap<?, ?>) {
				walker.mergeUnknownEntries(
						(HashMap<String, Object>) archivedUnresolved,
						qualifiedUnresolvedForParent);
			} else {
				scopeSymbols.put(MUMBLE_UNRESOLVED_COLUMN_KEY, new HashMap<String, Object>(qualifiedUnresolvedForParent));
			}
		}

		if (passUpQualifiedUnresolvedFromThisSubquery && !qualifiedUnresolvedForParent.isEmpty()) {
			Object parentUnresolvedObject = walker.symbolTable.get(MUMBLE_UNRESOLVED_COLUMN_KEY);
			if (parentUnresolvedObject instanceof HashMap<?, ?>) {
				walker.mergeUnknownEntries(
						(HashMap<String, Object>) parentUnresolvedObject, qualifiedUnresolvedForParent);
			} else {
				walker.symbolTable.put(MUMBLE_UNRESOLVED_COLUMN_KEY, qualifiedUnresolvedForParent);
			}
		}
	}

	/**
	 * Finalizes a UNION / INTERSECT scope: publish merged set-op interface, then pop the
	 * scope payload the same way VALUES and leaf SELECT scopes do.
	 */
	@SuppressWarnings("unchecked")
	public void finalizeSetOperationScopeSymbolTable(
			String scopeKey,
			Map<String, Object> scopeSymbols,
			boolean insertSource) {
		finalizeSetOperationAtExit(scopeKey, scopeSymbols, insertSource);
		HashMap<String, Object> scopePayload = (HashMap<String, Object>) scopeSymbols;
		HashMap<String, Object> scopedSummaryMap = removeScopedSetOperationSummaryMap(scopePayload);
		HashMap<String, Object> scopedQuerySummaryKeysMap = removeScopedQuerySummaryKeysMap(scopePayload);
		HashMap<String, Object> scopeSummary = walker.buildSetOperationInterfaceSummary(scopeKey, scopePayload);
		scopePayload.remove(TEMP_SET_OPERATION_OPERATOR_ANCHOR_LINE_KEY);
		scopePayload.remove(TEMP_SET_OPERATION_OPERATOR_ANCHOR_CHAR_KEY);
		if (scopeSummary != null && !scopeSummary.isEmpty()) {
			ArrayList<String> lineageSummaryKeys = buildSetOperationParticipantLineageSummaryKeys(
					scopePayload,
					scopedSummaryMap,
					scopedQuerySummaryKeysMap);
			walker.putSetOperationParticipantLineageSummaryKeys(scopeSummary, lineageSummaryKeys);
			scopedSummaryMap.put(scopeKey, scopeSummary);
			scopePayload.put(TEMP_SET_OPERATION_INTERFACE_SUMMARY_MAP_KEY, scopedSummaryMap);
			if (!scopedQuerySummaryKeysMap.isEmpty()) {
				scopePayload.put(TEMP_QUERY_SET_OPERATION_SUMMARY_KEYS_MAP_KEY, scopedQuerySummaryKeysMap);
			}
		} else {
			if (!scopedSummaryMap.isEmpty()) {
				scopePayload.put(TEMP_SET_OPERATION_INTERFACE_SUMMARY_MAP_KEY, scopedSummaryMap);
			}
			if (!scopedQuerySummaryKeysMap.isEmpty()) {
				scopePayload.put(TEMP_QUERY_SET_OPERATION_SUMMARY_KEYS_MAP_KEY, scopedQuerySummaryKeysMap);
			}
		}

		HashMap<String, Object> liveDeferred = new HashMap<String, Object>();
		Object liveUnresolved = walker.symbolTable.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
		if (liveUnresolved instanceof HashMap<?, ?>) {
			walker.mergeUnknownEntries(liveDeferred, (HashMap<String, Object>) liveUnresolved);
		}

		finalizeScopeDeferredUnresolved(scopePayload, liveDeferred);
		publishQueryLikeScope(scopeKey, scopePayload);
	}

	/**
	 * Finalizes an UPDATE scope: resolve columns against the target table, publish the
	 * scope payload, then export {@code update_dictionary} as the global query dictionary.
	 */
	public void finalizeUpdateScopeSymbolTable(Map<String, Object> updateNode) {
		String updateTargetTableRef = getUpdateTargetTableReference(updateNode);
		boolean updateHasFromClause = updateNode != null && updateNode.get(MUMBLE_FROM_KEY) != null;
		initializeUpdateTargetTableSubtree(updateTargetTableRef);
		convertSymbolTableToTableDictionary(false, false, updateTargetTableRef, updateHasFromClause);
		finalizeUpdateScopeUnresolvedColumnsAtExit(updateHasFromClause, updateNode);

		String updateScopeKey = MUMBLE_UPDATE_KEY + walker.queryCount;
		stripUnresolvedFromScopePayload(walker.symbolTable);
		publishQueryLikeScope(updateScopeKey, walker.symbolTable);
		publishUpdateScopeQueryDictionary(updateScopeKey);
	}

	/**
	 * Applies the same qualified/unqualified scope-exit resolution SELECT uses when an UPDATE
	 * statement includes a FROM clause.
	 */
	@SuppressWarnings("unchecked")
	private void finalizeUpdateScopeUnresolvedColumnsAtExit(
			boolean updateHasFromClause,
			Map<String, Object> updateNode) {
		if (!updateHasFromClause) {
			return;
		}

		Object unresolvedObj = walker.symbolTable.get(MUMBLE_UNRESOLVED_COLUMN_KEY);
		if (!(unresolvedObj instanceof HashMap<?, ?> unresolvedMapObj) || unresolvedMapObj.isEmpty()) {
			mergeScopeTableDictionaryIntoGlobalWalkerDictionary(walker.symbolTable);
			return;
		}

		HashMap<String, Object> unresolvedMap = (HashMap<String, Object>) unresolvedMapObj;
		HashMap<String, Object> qualifiedUnresolved = new HashMap<String, Object>();
		HashMap<String, Object> unqualifiedUnresolved = new HashMap<String, Object>();
		splitUnresolvedEntriesByQualification(unresolvedMap, qualifiedUnresolved, unqualifiedUnresolved);

		resolveUpdateDeferredQualifiedUnresolvedAtBoundary(qualifiedUnresolved, updateNode);
		pruneMaterializedQualifiedUnresolved(qualifiedUnresolved);
		resolveQualifiedUnresolvedAtQueryScopeExit(qualifiedUnresolved, true);
		pruneMaterializedQualifiedUnresolved(qualifiedUnresolved);
		materializeRemainingSingleTableUnqualifiedAtScopeExit(unqualifiedUnresolved, walker.symbolTable);
		mergeScopeTableDictionaryIntoGlobalWalkerDictionary(walker.symbolTable);

		unresolvedMap.clear();
		unresolvedMap.putAll(qualifiedUnresolved);
		unresolvedMap.putAll(unqualifiedUnresolved);
		if (unresolvedMap.isEmpty()) {
			walker.symbolTable.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
		}
	}

	@SuppressWarnings("unchecked")
	public String getUpdateTargetAlias(Map<String, Object> updateNode) {
		if (updateNode == null) {
			return null;
		}

		Object targetTableObj = updateNode.get(MUMBLE_TABLE_KEY);
		if (!(targetTableObj instanceof Map<?, ?> targetTableMapObj)) {
			return null;
		}

		Object aliasObj = ((Map<String, Object>) targetTableMapObj).get(MUMBLE_ALIAS_KEY);
		if (aliasObj instanceof String alias && !alias.isBlank()) {
			return alias;
		}

		return null;
	}

	/** Resolve correlated target-table qualified refs hoisted from UPDATE FROM subqueries. */
	@SuppressWarnings("unchecked")
	private void resolveUpdateDeferredQualifiedUnresolvedAtBoundary(
			HashMap<String, Object> qualifiedUnresolved,
			Map<String, Object> updateNode) {
		if (qualifiedUnresolved == null || qualifiedUnresolved.isEmpty()) {
			return;
		}

		HashMap<String, Object> tableAliasMap =
				(walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY) instanceof HashMap<?, ?>)
						? (HashMap<String, Object>) walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY)
						: new HashMap<String, Object>();
		HashMap<String, Object> tableCollection =
				(walker.symbolTable.get(MUMBLE_TABLE_DICTIONARY_KEY) instanceof HashMap<?, ?>)
						? (HashMap<String, Object>) walker.symbolTable.get(MUMBLE_TABLE_DICTIONARY_KEY)
						: new HashMap<String, Object>();

		HashMap<String, Object> visibleAliasMap = buildEffectiveVisibleAliasMap(tableAliasMap);
		mergePublishedScopeContextListIntoAliasMap(visibleAliasMap);

		String updateTargetTableRef = getUpdateTargetTableReference(updateNode);
		String updateTargetAlias = getUpdateTargetAlias(updateNode);
		if (updateTargetTableRef != null && !updateTargetTableRef.isBlank()) {
			String normalizedTargetRef = normalizeTableRef(updateTargetTableRef);
			if (updateTargetAlias != null && !updateTargetAlias.isBlank()) {
				visibleAliasMap.putIfAbsent(updateTargetAlias, normalizedTargetRef);
			}
			ensureTableDictionaryEntry(tableCollection, normalizedTargetRef);
		}

		HashMap<String, Object> visibleTableCollection = buildEffectiveVisibleTableCollection(tableCollection);
		HashMap<String, Object> globalTableDictionary = walker.getWalkerTableDictionary();
		if (globalTableDictionary != null && !globalTableDictionary.isEmpty()) {
			for (Map.Entry<String, Object> tableEntry : globalTableDictionary.entrySet()) {
				visibleTableCollection.putIfAbsent(tableEntry.getKey(), tableEntry.getValue());
			}
		}

		resolveQualifiedUnresolvedEntries(
				qualifiedUnresolved,
				visibleAliasMap,
				visibleTableCollection,
				false);
	}

	private void pruneMaterializedQualifiedUnresolved(HashMap<String, Object> qualifiedUnresolved) {
		if (qualifiedUnresolved == null || qualifiedUnresolved.isEmpty()) {
			return;
		}

		HashMap<String, Object> tableAliasMap =
				(walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY) instanceof HashMap<?, ?>)
						? (HashMap<String, Object>) walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY)
						: new HashMap<String, Object>();
		HashMap<String, Object> visibleAliasMap = buildEffectiveVisibleAliasMap(tableAliasMap);
		mergePublishedScopeContextListIntoAliasMap(visibleAliasMap);

		qualifiedUnresolved.entrySet().removeIf(entry ->
				isQualifiedEntryAlreadyMaterialized(entry.getKey(), visibleAliasMap));
	}

	/**
	 * Finalizes a DELETE scope: optionally seeds {@code query_dictionary} for RETURNING,
	 * then publishes the scope payload under {@code delete<N>}.
	 */
	@SuppressWarnings("unchecked")
	public void finalizeDeleteScopeSymbolTable(
			Map<String, Object> deleteNode,
			boolean publishReturningQueryDictionary) {
		String deleteTargetTableRef = getDeleteTargetTableReference(deleteNode);
		String deleteTargetAlias = getDeleteTargetAlias(deleteNode);
		if (deleteTargetTableRef != null && !deleteTargetTableRef.isBlank()) {
			walker.symbolTable.put(TEMP_DELETE_TARGET_TABLE_REF_KEY, deleteTargetTableRef);
		}
		if (deleteTargetAlias != null && !deleteTargetAlias.isBlank()) {
			walker.symbolTable.put(TEMP_DELETE_TARGET_ALIAS_KEY, deleteTargetAlias);
		}

		convertSymbolTableToTableDictionary(false, false, null);

		String deleteScopeKey = MUMBLE_DELETE_KEY + walker.queryCount;
		String deleteDefinitionScopeKey = toDefinitionScopeKey(deleteScopeKey);
		HashMap<String, Object> scopeSymbols = walker.symbolTable;
		if (publishReturningQueryDictionary) {
			HashMap<String, Object> localCurrentQueryDictionary =
					(HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_QUERY_DICTIONARY_KEY);
			if (localCurrentQueryDictionary == null) {
				localCurrentQueryDictionary = new HashMap<String, Object>();
			}
			sanitizeQueryDictionaryForGlobalExport(localCurrentQueryDictionary);
			mergeIntoGlobalQueryColumnDictionary(deleteScopeKey, localCurrentQueryDictionary);
			scopeSymbols.put(MUMBLE_QUERY_DICTIONARY_KEY, localCurrentQueryDictionary);
		}

		emitDeleteUsingSiblingCorrelationFatals(
				scopeSymbols,
				deleteTargetTableRef,
				deleteTargetAlias);

		publishQueryLikeScope(deleteScopeKey, scopeSymbols);
	}

	@SuppressWarnings("unchecked")
	private void emitDeleteUsingSiblingCorrelationFatals(
			HashMap<String, Object> scopeSymbols,
			String deleteTargetTableRef,
			String deleteTargetAlias) {
		if (scopeSymbols == null || scopeSymbols.isEmpty()) {
			return;
		}

		HashSet<String> deleteFrameAliases = new HashSet<String>();
		Object deleteAliasObj = scopeSymbols.get(MUMBLE_TABLE_ALIAS_KEY);
		if (deleteAliasObj instanceof HashMap<?, ?> deleteAliasMapObj) {
			for (Object aliasKeyObj : ((HashMap<String, Object>) deleteAliasMapObj).keySet()) {
				if (aliasKeyObj instanceof String aliasKey && !aliasKey.isBlank()) {
					deleteFrameAliases.add(aliasKey.toLowerCase());
				}
			}
		}

		for (Map.Entry<String, Object> entry : scopeSymbols.entrySet()) {
			String key = entry.getKey();
			if (key == null || !key.startsWith("def_query")) {
				continue;
			}
			if (!(entry.getValue() instanceof HashMap<?, ?> scopeObj)) {
				continue;
			}
			emitDeleteUsingSiblingCorrelationFatalsInScope(
					(HashMap<String, Object>) scopeObj,
					deleteTargetTableRef,
					deleteTargetAlias,
					deleteFrameAliases);
		}
	}

	@SuppressWarnings("unchecked")
	private void emitDeleteUsingSiblingCorrelationFatalsInScope(
			HashMap<String, Object> queryScope,
			String deleteTargetTableRef,
			String deleteTargetAlias,
			HashSet<String> deleteFrameAliases) {
		if (queryScope == null || queryScope.isEmpty()) {
			return;
		}

		HashSet<String> localAliases = new HashSet<String>();
		Object aliasObj = queryScope.get(MUMBLE_TABLE_ALIAS_KEY);
		if (aliasObj instanceof HashMap<?, ?> aliasMapObj) {
			for (Object aliasKeyObj : ((HashMap<String, Object>) aliasMapObj).keySet()) {
				if (aliasKeyObj instanceof String aliasKey && !aliasKey.isBlank()) {
					localAliases.add(aliasKey.toLowerCase());
				}
			}
		}

		HashSet<String> localTables = new HashSet<String>();
		Object tableDictObj = queryScope.get(MUMBLE_TABLE_DICTIONARY_KEY);
		if (tableDictObj instanceof HashMap<?, ?> tableDictMapObj) {
			for (Object tableKeyObj : ((HashMap<String, Object>) tableDictMapObj).keySet()) {
				if (tableKeyObj instanceof String tableKey && !tableKey.isBlank()) {
					localTables.add(tableKey.toLowerCase());
					localTables.add(normalizeTableRef(tableKey).toLowerCase());
				}
			}
		}

		Object filtersObj = queryScope.get(MUMBLE_FILTERS_KEY);
		if (filtersObj instanceof ArrayList<?> filters) {
			for (Object filterObj : filters) {
				String tableRef = walker.extractReferenceTableRefFromInterfaceEntry(filterObj);
				String columnName = walker.extractReferenceNameFromInterfaceEntry(filterObj);
				if (tableRef == null || tableRef.isBlank() || "*".equals(tableRef)) {
					continue;
				}
				String tableRefLower = tableRef.toLowerCase();
				String normalizedTableRefLower = normalizeTableRef(tableRef).toLowerCase();
				if (deleteTargetAlias != null && !deleteTargetAlias.isBlank()
						&& tableRef.equalsIgnoreCase(deleteTargetAlias)) {
					continue;
				}
				if (deleteFrameAliases != null
						&& deleteFrameAliases.contains(tableRefLower)
						&& !localAliases.contains(tableRefLower)) {
					// Refers to a sibling USING alias from the outer DELETE frame.
					// These are not visible inside a non-lateral USING subquery.
					Integer[] refLocation = walker.getLineAndCharacterFromEntry(filterObj);
					if (refLocation[0] == null || refLocation[1] == null) {
						refLocation = resolveDeleteScopeRefLocation(queryScope, tableRef, columnName);
					}
					String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE);
					String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE);
					String col = (columnName == null || columnName.isBlank()) ? "?" : columnName;
					String diagMessage = (diagTemplate == null)
							? String.format(
									"Source Table not found for Column '%s' at (l:%s c:%s). No alias or table called '%s'.",
									col,
									refLocation[0],
									refLocation[1],
									tableRef)
							: String.format(diagTemplate, col, refLocation[0], refLocation[1], tableRef);
					walker.addWalkerFatal(diagCode, diagMessage, refLocation[0], refLocation[1], col);
					continue;
				}
				if (localAliases.contains(tableRefLower)
						|| localTables.contains(tableRefLower)
						|| localTables.contains(normalizedTableRefLower)) {
					continue;
				}
				if (deleteTargetTableRef != null && !deleteTargetTableRef.isBlank()
						&& normalizeTableRef(tableRef).equalsIgnoreCase(normalizeTableRef(deleteTargetTableRef))) {
					continue;
				}
				if (walker.isNonTableQuerySourceReference(tableRef)) {
					continue;
				}

				Integer[] refLocation = walker.getLineAndCharacterFromEntry(filterObj);
				if (refLocation[0] == null || refLocation[1] == null) {
					refLocation = resolveDeleteScopeRefLocation(queryScope, tableRef, columnName);
				}
				String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE);
				String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE);
				String col = (columnName == null || columnName.isBlank()) ? "?" : columnName;
				String diagMessage = (diagTemplate == null)
						? String.format(
								"Source Table not found for Column '%s' at (l:%s c:%s). No alias or table called '%s'.",
								col,
								refLocation[0],
								refLocation[1],
								tableRef)
						: String.format(
								diagTemplate,
								col,
								refLocation[0],
								refLocation[1],
								tableRef);
				walker.addWalkerFatal(diagCode, diagMessage, refLocation[0], refLocation[1], col);
			}
		}

		for (Map.Entry<String, Object> nestedEntry : queryScope.entrySet()) {
			if (nestedEntry.getKey() != null
					&& nestedEntry.getKey().startsWith("def_query")
					&& nestedEntry.getValue() instanceof HashMap<?, ?> nestedScopeObj) {
				emitDeleteUsingSiblingCorrelationFatalsInScope(
						(HashMap<String, Object>) nestedScopeObj,
						deleteTargetTableRef,
						deleteTargetAlias,
						deleteFrameAliases);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Integer[] resolveDeleteScopeRefLocation(
			HashMap<String, Object> queryScope,
			String tableRef,
			String columnName) {
		if (queryScope == null || tableRef == null || tableRef.isBlank()) {
			return new Integer[] { null, null };
		}

		Object tableDictObj = queryScope.get(MUMBLE_TABLE_DICTIONARY_KEY);
		if (tableDictObj instanceof HashMap<?, ?> tableDictMapObj) {
			HashMap<String, Object> tableDict = (HashMap<String, Object>) tableDictMapObj;
			HashMap<String, Object> targetDict = walker.getTableDictionaryForReference(tableRef, tableDict);
			if (targetDict != null) {
				if (columnName != null && !columnName.isBlank()) {
					Object columnEntry = targetDict.get(columnName);
					if (columnEntry == null) {
						for (Map.Entry<String, Object> entry : targetDict.entrySet()) {
							if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(columnName)) {
								columnEntry = entry.getValue();
								break;
							}
						}
					}
					if (columnEntry != null) {
						Integer[] loc = walker.getLineAndCharacterFromEntry(columnEntry);
						if (loc[0] != null && loc[1] != null) {
							return loc;
						}
					}
				}
				Integer[] fallback = walker.getFirstEntryLineAndCharacter(targetDict);
				if (fallback[0] != null && fallback[1] != null) {
					return fallback;
				}
			}
		}

		Object queryDictObj = queryScope.get(MUMBLE_QUERY_DICTIONARY_KEY);
		if (queryDictObj instanceof HashMap<?, ?> queryDictMapObj) {
			Integer[] fallback = walker.getFirstEntryLineAndCharacter((HashMap<String, Object>) queryDictMapObj);
			if (fallback[0] != null && fallback[1] != null) {
				return fallback;
			}
		}

		return new Integer[] { null, null };
	}

	/**
	 * Finalizes an INSERT scope: emit deferred unresolved diagnostics at the insert boundary,
	 * publish the scope payload, merge local table dictionary into global, then export
	 * {@code query_dictionary} for the insert scope.
	 */
	public void finalizeInsertScopeSymbolTable() {
		finalizeTopLevelUnresolvedColumnsAtInsertBoundary();

		String insertScopeKey = MUMBLE_INSERT_KEY + walker.queryCount;
		publishQueryLikeScope(insertScopeKey, walker.symbolTable);
		mergeInsertScopeTableDictionaryIntoGlobal(insertScopeKey);
		publishInsertScopeQueryDictionary(insertScopeKey);
	}

	/**
	 * Accidental archive key produced when {@code unresolved_column} was renamed via {@code def_}
	 * promotion. Hoist entries back into the active {@link #MUMBLE_UNRESOLVED_COLUMN_KEY} pipeline.
	 */
	private static final String DEF_ARCHIVED_UNRESOLVED_COLUMN_KEY = "def_" + MUMBLE_UNRESOLVED_COLUMN_KEY;

	@SuppressWarnings("unchecked")
	public void publishQueryLikeScope(String scopeKey, HashMap<String, Object> scopePayload) {
		if (scopeKey == null || scopeKey.isBlank() || scopePayload == null) {
			return;
		}

		// Canonical contract: payload-bearing symbol-table keys are always def_*;
		// references (table_alias/table_ref/context/dependencies) remain live keys.
		String liveScopeKey = toLiveScopeKey(scopeKey);
		String definitionKey = toDefinitionScopeKey(scopeKey);
		if (liveScopeKey == null || definitionKey == null) {
			return;
		}

		HashMap<String, Object> scopeSummaryMap = removeScopedSetOperationSummaryMap(scopePayload);
		HashMap<String, Object> scopedQuerySummaryKeysMap = removeScopedQuerySummaryKeysMap(scopePayload);

		Object queryDictionaryObj = scopePayload.get(MUMBLE_QUERY_DICTIONARY_KEY);
		if (queryDictionaryObj instanceof HashMap<?, ?> queryDictionaryMapObj) {
			mergeIntoGlobalQueryColumnDictionary(liveScopeKey, (HashMap<String, Object>) queryDictionaryMapObj);
		}

		scopePayload.remove(MUMBLE_SCALAR_SUBQUERY_ALIASES_KEY);
		HashMap<String, Object> hoistedUnresolved = new HashMap<String, Object>();
		collectAndStripUnresolvedFromScopeTree(scopePayload, hoistedUnresolved);
		stripUnresolvedFromScopePayload(scopePayload);
		stripFrameLocalWalkTimeKeys(scopePayload);
		walker.popSymbolTable(definitionKey, scopePayload);
		walker.symbolTable.remove(liveScopeKey);
		walker.mergeSetOperationInterfaceSummariesIntoCurrentScope(scopeSummaryMap);

		if (liveScopeKey.startsWith(MUMBLE_QUERY_KEY) && scopeSummaryMap != null && !scopeSummaryMap.isEmpty()) {
			HashMap<String, Object> queryToSummaryMap = new HashMap<String, Object>();
			queryToSummaryMap.put(liveScopeKey, walker.extractSetOperationSummaryKeys(scopeSummaryMap));
			walker.mergeQuerySetOperationSummaryKeysIntoCurrentScope(queryToSummaryMap);
		}

		walker.mergeQuerySetOperationSummaryKeysIntoCurrentScope(scopedQuerySummaryKeysMap);
		mergeUnresolvedEntriesIntoCurrentScope(hoistedUnresolved);
		walker.queryCount++;
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> buildSetOperationParticipantLineageSummaryKeys(
			Map<String, Object> scopePayload,
			Map<String, Object> scopedSummaryMap,
			Map<String, Object> scopedQuerySummaryKeysMap) {
		ArrayList<String> lineageSummaryKeys = new ArrayList<String>();
		if (scopePayload == null || scopePayload.isEmpty() || scopedSummaryMap == null || scopedSummaryMap.isEmpty()) {
			return lineageSummaryKeys;
		}

		ArrayList<Map.Entry<String, Object>> participantEntries = new ArrayList<Map.Entry<String, Object>>();
		for (Map.Entry<String, Object> entry : scopePayload.entrySet()) {
			if (!isSetOperationParticipantKey(entry.getKey()) || !(entry.getValue() instanceof Map<?, ?>)) {
				continue;
			}
			participantEntries.add(entry);
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
			String participantKey = participantEntry.getKey();
			if (participantKey == null) {
				continue;
			}

			String liveParticipantKey = normalizeSetOperationParticipantKey(participantKey);

			if (isSetOperationSourceReference(liveParticipantKey)
					&& scopedSummaryMap.containsKey(liveParticipantKey)) {
				appendUniqueSummaryKey(lineageSummaryKeys, liveParticipantKey);
				continue;
			}

			if (!liveParticipantKey.startsWith(MUMBLE_QUERY_KEY)
					|| scopedQuerySummaryKeysMap == null
					|| scopedQuerySummaryKeysMap.isEmpty()) {
				if (participantEntry.getValue() instanceof Map<?, ?> participantMapObj) {
					collectSetOperationDefinitionSummaryKeysFromParticipantMap(
							lineageSummaryKeys,
							(Map<String, Object>) participantMapObj,
							scopedSummaryMap);
				}
				continue;
			}

			Object summaryKeysObj = scopedQuerySummaryKeysMap.get(liveParticipantKey);
			if (summaryKeysObj instanceof List<?> summaryKeysList) {
				boolean appendedFromQuerySummaryMap = false;
				for (Object summaryKeyObj : summaryKeysList) {
					if (!(summaryKeyObj instanceof String summaryKey)
							|| !scopedSummaryMap.containsKey(summaryKey)) {
						continue;
					}
					appendUniqueSummaryKey(lineageSummaryKeys, summaryKey);
					appendedFromQuerySummaryMap = true;
				}
				if (appendedFromQuerySummaryMap) {
					continue;
				}
			}

			if (participantEntry.getValue() instanceof Map<?, ?> participantMapObj) {
				collectSetOperationDefinitionSummaryKeysFromParticipantMap(
						lineageSummaryKeys,
						(Map<String, Object>) participantMapObj,
						scopedSummaryMap);
			}
		}

		return lineageSummaryKeys;
	}

	private void collectSetOperationDefinitionSummaryKeysFromParticipantMap(
			ArrayList<String> lineageSummaryKeys,
			Map<String, Object> participantMap,
			Map<String, Object> scopedSummaryMap) {
		if (lineageSummaryKeys == null
				|| participantMap == null
				|| participantMap.isEmpty()
				|| scopedSummaryMap == null
				|| scopedSummaryMap.isEmpty()) {
			return;
		}

		for (String nestedKey : participantMap.keySet()) {
			if (nestedKey == null || nestedKey.isBlank()) {
				continue;
			}

			String normalizedKey = nestedKey.startsWith("def_")
					? nestedKey.substring("def_".length())
					: nestedKey;
			if (!(normalizedKey.startsWith(MUMBLE_UNION_KEY) || normalizedKey.startsWith(MUMBLE_INTERSECT_KEY))) {
				continue;
			}

			if (!scopedSummaryMap.containsKey(normalizedKey)
					&& participantMap.get(nestedKey) instanceof Map<?, ?> nestedSetMapObj) {
				HashMap<String, Object> derivedSummary = walker.buildSetOperationInterfaceSummary(
						normalizedKey,
						(Map<String, Object>) nestedSetMapObj);
				if (derivedSummary != null && !derivedSummary.isEmpty()) {
					scopedSummaryMap.put(normalizedKey, derivedSummary);
				}
			}

			if (!scopedSummaryMap.containsKey(normalizedKey)) {
				continue;
			}

			appendUniqueSummaryKey(lineageSummaryKeys, normalizedKey);
		}
	}

	private void appendUniqueSummaryKey(ArrayList<String> keys, String candidate) {
		if (keys == null || candidate == null || candidate.isBlank() || keys.contains(candidate)) {
			return;
		}
		keys.add(candidate);
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> removeScopedSetOperationSummaryMap(HashMap<String, Object> scopePayload) {
		if (scopePayload == null || scopePayload.isEmpty()) {
			return new HashMap<String, Object>();
		}

		Object scopedSummaryObj = scopePayload.remove(TEMP_SET_OPERATION_INTERFACE_SUMMARY_MAP_KEY);
		if (scopedSummaryObj instanceof HashMap<?, ?> scopedSummaryMapObj) {
			return (HashMap<String, Object>) scopedSummaryMapObj;
		}

		return new HashMap<String, Object>();
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> removeScopedQuerySummaryKeysMap(HashMap<String, Object> scopePayload) {
		if (scopePayload == null || scopePayload.isEmpty()) {
			return new HashMap<String, Object>();
		}

		Object scopedSummaryKeysObj = scopePayload.remove(TEMP_QUERY_SET_OPERATION_SUMMARY_KEYS_MAP_KEY);
		if (scopedSummaryKeysObj instanceof HashMap<?, ?> scopedSummaryKeysMapObj) {
			return (HashMap<String, Object>) scopedSummaryKeysMapObj;
		}

		return new HashMap<String, Object>();
	}

	/**
	 * Pops the current symbol-table frame and merges its entries into the restored parent frame
	 * after removing walk-time-only keys from the frame root (not from nested published scopes).
	 */
	public void popFrameAndMergeIntoParent(HashMap<String, Object> frameSymbols) {
		if (frameSymbols != null) {
			stripFrameLocalWalkTimeKeys(frameSymbols);
		}
		walker.popSymbolTablePutAll(frameSymbols);
	}

	/**
	 * Publishes a finalized scope payload under {@code scopeKey} and pops back to the parent frame
	 * after stripping walk-time-only keys from the payload.
	 */
	public void publishNamedScopeAndPop(String scopeKey, HashMap<String, Object> scopePayload) {
		if (scopeKey == null || scopeKey.isBlank()) {
			return;
		}
		HashMap<String, Object> payload = scopePayload;
		if (payload == null) {
			payload = new HashMap<String, Object>();
		}
		stripFrameLocalWalkTimeKeys(payload);
		String definitionKey = toDefinitionScopeKey(scopeKey);
		walker.popSymbolTable(definitionKey != null ? definitionKey : scopeKey, payload);
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
	 * Thin insert wrap: map target columns from an already-resolved source scope.
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
			if (nodeMap.containsKey(MUMBLE_SELECT_KEY)) {
				return;
			}

			String tableRef = getQualifiedTableReference(nodeMap);
			Object aliasObj = nodeMap.get(MUMBLE_ALIAS_KEY);
			if (tableRef != null && !tableRef.isBlank()) {
				registerCteBackedSourceAliasMappings(tableRef, aliasObj);
				registerPhysicalTableAliasMappings(tableRef, aliasObj);
			}

			Object tableObj = nodeMap.get(MUMBLE_TABLE_KEY);
			if (tableObj instanceof Map<?, ?> tableMapObj) {
				Map<String, Object> tableMap = (Map<String, Object>) tableMapObj;
				if (!tableMap.containsKey(MUMBLE_QUERY_KEY)) {
					collectFromClauseCteAliasMappingsRecursive(tableMapObj);
				}
			}

			for (Object valueObj : nodeMap.values()) {
				if (valueObj instanceof Map<?, ?> valueMapObj) {
					Map<String, Object> valueMap = (Map<String, Object>) valueMapObj;
					if (valueMap.containsKey(MUMBLE_SELECT_KEY) || valueMap.containsKey(MUMBLE_QUERY_KEY)) {
						continue;
					}
					collectFromClauseCteAliasMappingsRecursive(valueObj);
				} else if (valueObj instanceof List<?>) {
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
		recordLocalFromRegisteredAlias(tableRef);
		if (aliasObj instanceof String alias && !alias.isBlank()) {
			upsertCurrentTableAliasMapping(alias, cteScopeRef);
			recordLocalFromRegisteredAlias(alias);
			upsertVisibleCteAliasMapping(alias, tableRef, cteScopeRef);
		}
	}

	/** Re-register physical-table aliases from the FROM AST before scope finalization. */
	public void registerPhysicalTableAliasMappings(String tableRef, Object aliasObj) {
		if (tableRef == null || tableRef.isBlank()) {
			return;
		}
		if (resolveCteOrExistingQueryScopeInVisibleScopes(tableRef) != null) {
			return;
		}
		if (aliasObj instanceof String alias && !alias.isBlank()) {
			upsertCurrentTableAliasMapping(alias, tableRef);
			recordLocalFromRegisteredAlias(alias);
		}
		recordLocalFromRegisteredAlias(tableRef);
		walker.ensureTableDictionaryEntry(tableRef);
	}

	@SuppressWarnings("unchecked")
	public void recordLocalFromRegisteredAlias(String alias) {
		if (alias == null || alias.isBlank()) {
			return;
		}

		Object registeredAliasesObj = walker.symbolTable.get(MUMBLE_LOCAL_FROM_REGISTERED_ALIASES_KEY);
		LinkedHashSet<String> registeredAliases;
		if (registeredAliasesObj instanceof LinkedHashSet<?>) {
			registeredAliases = (LinkedHashSet<String>) registeredAliasesObj;
		} else {
			registeredAliases = new LinkedHashSet<String>();
			walker.symbolTable.put(MUMBLE_LOCAL_FROM_REGISTERED_ALIASES_KEY, registeredAliases);
		}

		for (String existingAlias : registeredAliases) {
			if (existingAlias != null && existingAlias.equalsIgnoreCase(alias)) {
				return;
			}
		}
		registeredAliases.add(alias);
	}

	private boolean isLocalFromRegisteredAlias(String aliasKey) {
		if (aliasKey == null || aliasKey.isBlank()) {
			return false;
		}

		Object registeredAliasesObj = walker.symbolTable.get(MUMBLE_LOCAL_FROM_REGISTERED_ALIASES_KEY);
		if (!(registeredAliasesObj instanceof Set<?> registeredAliases) || registeredAliases.isEmpty()) {
			return false;
		}

		for (Object registeredAliasObj : registeredAliases) {
			if (registeredAliasObj instanceof String registeredAlias
					&& registeredAlias.equalsIgnoreCase(aliasKey)) {
				return true;
			}
		}
		return false;
	}

	public void upsertVisibleCteAliasMapping(String alias, String sourceRef, String cteScopeRef) {
		if (alias == null || alias.isBlank() || cteScopeRef == null || cteScopeRef.isBlank()) {
			return;
		}

		Map<String, Object> visibleCteList = getContextListSymbolMap(walker.symbolTable);
		if (visibleCteList == null || visibleCteList.isEmpty()) {
			for (Map<String, Object> ancestorSymbols : getAncestorSymbolTables()) {
				Map<String, Object> ancestorCteList = getContextListSymbolMap(ancestorSymbols);
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

		Map<String, Object> activeCteList = ensureContextListSymbolMap();
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

	/** True when this FROM clause belongs to a SELECT {@code query_specification}, not DML. */
	public boolean isQuerySpecificationFromClause(ParserRuleContext fromClauseContext) {
		if (fromClauseContext == null) {
			return false;
		}
		ParserRuleContext cursor = fromClauseContext.getParent();
		while (cursor != null) {
			int ruleIndex = cursor.getRuleIndex();
			if (ruleIndex == SQLSelectParserParser.RULE_query_specification) {
				return true;
			}
			if (ruleIndex == SQLSelectParserParser.RULE_insert_expression
					|| ruleIndex == SQLSelectParserParser.RULE_update_expression
					|| ruleIndex == SQLSelectParserParser.RULE_delete_expression) {
				return false;
			}
			cursor = cursor.getParent();
		}
		return false;
	}

	/** Defer outer-correlated refs while the statement's main query_spec is still being assembled. */
	private boolean shouldDeferQualifiedUntilStatementTop(
			String unresolvedKey,
			HashMap<String, Object> visibleAliasMap,
			HashMap<String, Object> visibleTableDictionary) {
		if (unresolvedKey == null || !unresolvedKey.contains(".")) {
			return false;
		}
		if (!walker.anyIncompleteQuerySpecificationOnStack()) {
			return false;
		}
		HashMap<String, Object> tableDictionaryView =
				buildQualifiedResolutionTableDictionaryView(visibleTableDictionary);
		if (walker.canResolveQualifiedUnknownInScope(
				unresolvedKey,
				visibleAliasMap,
				tableDictionaryView,
				walker.queryColumnDictionaryMap)) {
			return false;
		}
		int dotIndex = unresolvedKey.indexOf('.');
		String columnName = unresolvedKey.substring(dotIndex + 1);
		return columnName.startsWith("<") && columnName.endsWith(">");
	}

	/** Resolve or fatal qualified unknowns once at query_spec exit using stack-visible scope only. */
	@SuppressWarnings("unchecked")
	private void resolveQualifiedUnresolvedAtQueryScopeExit(
			HashMap<String, Object> qualifiedUnresolved,
			boolean emitRemainingFatals) {
		if (qualifiedUnresolved == null || qualifiedUnresolved.isEmpty()) {
			return;
		}

		HashMap<String, Object> tableAliasMap =
				(walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY) instanceof HashMap<?, ?>)
						? (HashMap<String, Object>) walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY)
						: new HashMap<String, Object>();
		HashMap<String, Object> tableCollection =
				(walker.symbolTable.get(MUMBLE_TABLE_DICTIONARY_KEY) instanceof HashMap<?, ?>)
						? (HashMap<String, Object>) walker.symbolTable.get(MUMBLE_TABLE_DICTIONARY_KEY)
						: new HashMap<String, Object>();
		HashMap<String, Object> visibleAliasMap;
		HashMap<String, Object> visibleTableCollection;
		if (emitRemainingFatals) {
			visibleAliasMap = buildEffectiveVisibleAliasMap(tableAliasMap);
			mergePublishedScopeContextListIntoAliasMap(visibleAliasMap);
			visibleTableCollection = buildEffectiveVisibleTableCollection(tableCollection);
		} else {
			visibleAliasMap = copyLocalScopeAliasMap(tableAliasMap);
			mergePublishedScopeContextListIntoAliasMap(visibleAliasMap);
			visibleTableCollection = copyLocalScopeTableCollection(tableCollection);
		}
		resolveQualifiedUnresolvedEntries(
				qualifiedUnresolved,
				visibleAliasMap,
				visibleTableCollection,
				emitRemainingFatals);
	}

	private HashMap<String, Object> copyLocalScopeAliasMap(HashMap<String, Object> localAliasMap) {
		HashMap<String, Object> localOnly = new HashMap<String, Object>();
		if (localAliasMap != null && !localAliasMap.isEmpty()) {
			localOnly.putAll(localAliasMap);
		}
		return localOnly;
	}

	private HashMap<String, Object> copyLocalScopeTableCollection(HashMap<String, Object> localTableCollection) {
		HashMap<String, Object> localOnly = new HashMap<String, Object>();
		if (localTableCollection != null && !localTableCollection.isEmpty()) {
			localOnly.putAll(localTableCollection);
		}
		return localOnly;
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> buildQualifiedResolutionTableDictionaryView(
			HashMap<String, Object> visibleTableDictionary) {
		HashMap<String, Object> tableDictionaryView = new HashMap<String, Object>();
		if (visibleTableDictionary != null) {
			tableDictionaryView.putAll(visibleTableDictionary);
		}
		return tableDictionaryView;
	}

	/**
	 * Writes one resolvable qualified entry into the global walker table dictionary and the
	 * current scope local table dictionary.
	 */
	@SuppressWarnings("unchecked")
	private boolean materializeQualifiedUnresolvedEntry(
			String unresolvedKey,
			Object entryValue,
			HashMap<String, Object> visibleAliasMap) {
		return materializeQualifiedUnresolvedEntry(
				unresolvedKey,
				entryValue,
				visibleAliasMap,
				null,
				null,
				null);
	}

	@SuppressWarnings("unchecked")
	private boolean materializeQualifiedUnresolvedEntry(
			String unresolvedKey,
			Object entryValue,
			HashMap<String, Object> visibleAliasMap,
			HashMap<String, Object> localTableCollectionOverride) {
		return materializeQualifiedUnresolvedEntry(
				unresolvedKey,
				entryValue,
				visibleAliasMap,
				localTableCollectionOverride,
				null,
				null);
	}

	@SuppressWarnings("unchecked")
	private boolean materializeQualifiedUnresolvedEntry(
			String unresolvedKey,
			Object entryValue,
			HashMap<String, Object> visibleAliasMap,
			HashMap<String, Object> localTableCollectionOverride,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> localInterface) {
		if (unresolvedKey == null || unresolvedKey.isBlank() || !unresolvedKey.contains(".") || entryValue == null) {
			return false;
		}

		int dotIndex = unresolvedKey.indexOf('.');
		String sourceRef = unresolvedKey.substring(0, dotIndex);
		String columnName = unresolvedKey.substring(dotIndex + 1);
		if (hasCteListSymbolMap()) {
			String cteScopeRef = resolveCteScopeReference(sourceRef, visibleAliasMap);
			if (cteScopeRef != null && !cteScopeRef.isBlank()) {
				boolean resolvedInCte = "*".equals(columnName)
						|| hasColumnInQueryOutputInterface(cteScopeRef, columnName)
						|| hasWildcardInQueryOutputInterface(cteScopeRef);
				if (resolvedInCte) {
					return materializeCteContextQualifiedUnresolvedEntry(
							columnName,
							entryValue,
							cteScopeRef);
				}
			}
		}
		String resolvedTableRef = walker.resolveAliasToTableName(sourceRef, visibleAliasMap);
		if (resolvedTableRef == null || resolvedTableRef.isBlank()) {
			resolvedTableRef = sourceRef;
		}
		if (walker.isNonTableQuerySourceReference(resolvedTableRef)
				|| walker.isNonTableQuerySourceReference(sourceRef)) {
			return false;
		}
		if (visibleAliasMap != null && !visibleAliasMap.isEmpty()) {
			String queryAliasTarget = resolveAliasToQuerySourceFromAliasMap(sourceRef, visibleAliasMap);
			if (queryAliasTarget != null && walker.isNonTableQuerySourceReference(queryAliasTarget)) {
				return false;
			}
			String aliasResolvedTarget = walker.resolveAliasToTableName(sourceRef, visibleAliasMap);
			if (aliasResolvedTarget != null
					&& walker.isNonTableQuerySourceReference(aliasResolvedTarget)) {
				return false;
			}
		}

		HashMap<String, Object> localTableCollection = localTableCollectionOverride;
		if (localTableCollection == null) {
			Object localTableDictObj = walker.symbolTable.get(MUMBLE_TABLE_DICTIONARY_KEY);
			if (localTableDictObj instanceof HashMap<?, ?>) {
				localTableCollection = (HashMap<String, Object>) localTableDictObj;
			}
		}
		HashMap<String, Object> visibleTableCollection =
				buildEffectiveVisibleTableCollection(localTableCollection);
		if (!isPhysicalTableRefVisibleInScope(
				sourceRef,
				visibleAliasMap,
				localTableCollection,
				visibleTableCollection)) {
			return false;
		}

		String normalizedTableRef = normalizeTableRef(resolvedTableRef);
		if (localTableCollection == null) {
			return false;
		}

		HashMap<String, Object> localTarget = ensureTableDictionaryEntry(
				localTableCollection,
				normalizedTableRef);
		walker.mergeResolvedColumnIntoDictionary(localTarget, columnName, entryValue);

		HashMap<String, Object> globalTableDictionary = walker.getWalkerTableDictionary();
		if (globalTableDictionary != null) {
			HashMap<String, Object> globalTarget = ensureTableDictionaryEntry(
					globalTableDictionary,
					normalizedTableRef);
			walker.mergeResolvedColumnIntoDictionary(globalTarget, columnName, entryValue);
		}
		releaseResolvedQualifiedGlobalLocationIfQualified(unresolvedKey);
		return true;
	}

	/** Publishes a finalized scope local table dictionary into the global walker dictionary. */
	@SuppressWarnings("unchecked")
	private void mergeScopeTableDictionaryIntoGlobalWalkerDictionary(Map<String, Object> scopeSymbols) {
		if (scopeSymbols == null) {
			return;
		}
		Object tableDictionaryObj = scopeSymbols.get(MUMBLE_TABLE_DICTIONARY_KEY);
		if (tableDictionaryObj instanceof HashMap<?, ?> tableDictionaryMap
				&& !tableDictionaryMap.isEmpty()) {
			walker.mergeTableDictionaryIntoWalkerTableDictionary(
					(HashMap<String, Object>) tableDictionaryMap);
		}
	}

	/** Resolves deferred unqualified columns when the scope has exactly one physical table source. */
	@SuppressWarnings("unchecked")
	private void materializeRemainingSingleTableUnqualifiedAtScopeExit(
			HashMap<String, Object> unqualifiedUnresolved,
			Map<String, Object> scopeSymbols) {
		if (unqualifiedUnresolved == null || unqualifiedUnresolved.isEmpty() || scopeSymbols == null) {
			return;
		}

		HashMap<String, Object> tableCollection =
				(scopeSymbols.get(MUMBLE_TABLE_DICTIONARY_KEY) instanceof HashMap<?, ?>)
						? (HashMap<String, Object>) scopeSymbols.get(MUMBLE_TABLE_DICTIONARY_KEY)
						: new HashMap<String, Object>();
		if (tableCollection.isEmpty()) {
			return;
		}

		if (walker.moveEntriesToSingleTableIfSingleTarget(unqualifiedUnresolved, tableCollection)) {
			scopeSymbols.put(MUMBLE_TABLE_DICTIONARY_KEY, tableCollection);
			walker.mergeTableDictionaryIntoWalkerTableDictionary(tableCollection);
			Object interfaceObj = scopeSymbols.get(MUMBLE_INTERFACE_KEY);
			if (interfaceObj instanceof HashMap<?, ?>) {
				patchInterfaceTableRefsForSinglePhysicalTableScope(
						(HashMap<String, Object>) interfaceObj,
						tableCollection);
			}
		}
	}

	/**
	 * Apply unified qualified scope resolution for one batch-exit entry (scope-exit egress).
	 * Returns true when the entry was resolved and should be removed from the working map.
	 */
	private boolean applyQualifiedScopeResolutionAtBatchExit(
			String unresolvedKey,
			Object entryValue,
			HashMap<String, Object> visibleAliasMap,
			HashMap<String, Object> visibleTableCollection,
			HashMap<String, Object> visibleQuerySourceCollection,
			HashMap<String, Object> tableDictionaryView,
			HashMap<String, Object> localTableCollectionOverride) {
		if (unresolvedKey == null || unresolvedKey.isBlank() || !unresolvedKey.contains(".")) {
			return false;
		}

		int dotIndex = unresolvedKey.indexOf('.');
		String tableRef = unresolvedKey.substring(0, dotIndex);
		String columnName = unresolvedKey.substring(dotIndex + 1);
		if (tableRef.isBlank() || columnName.isBlank()) {
			return false;
		}

		if (tryResolveQualifiedEntryViaCteContext(unresolvedKey, entryValue, visibleAliasMap)) {
			return true;
		}

		HashMap<String, Object> scopeDerivedColumns = getScopeDerivedColumnsFromSymbolTable();
		ArrayList<Object> scopeRelationalModifierHints = getScopeRelationalModifierHintsFromSymbolTable();

		QualifiedScopeResolutionResult resolutionResult =
				resolveQualifiedColumnAgainstVisibleScope(
						tableRef,
						columnName,
						visibleAliasMap,
						visibleTableCollection,
						visibleQuerySourceCollection,
						false,
						scopeDerivedColumns,
						scopeRelationalModifierHints);

		switch (resolutionResult.status) {
			case RESOLVED_WILDCARD_QUERY_SOURCE -> {
				promoteQualifiedWildcardIntoQuerySource(resolutionResult.querySourceRef, entryValue);
				return true;
			}
			case RESOLVED_QUERY_SOURCE -> {
				materializeResolvedQualifiedQuerySourceReference(
						tableRef,
						columnName,
						resolutionResult.querySourceRef,
						entryValue,
						null,
						visibleAliasMap,
						false);
				return true;
			}
			case RESOLVED_PHYSICAL_SOURCE -> {
				return materializeQualifiedUnresolvedEntry(
						unresolvedKey,
						entryValue,
						visibleAliasMap,
						localTableCollectionOverride);
			}
			case RESOLVED_DERIVED_COLUMN -> {
				return true;
			}
			default -> {
			}
		}

		// Broader scope-exit materialization gate for physical / late-bound refs that the
		// primary resolver defers to canResolveQualifiedUnknownInScope.
		if (!walker.canResolveQualifiedUnknownInScope(
				unresolvedKey,
				visibleAliasMap,
				tableDictionaryView,
				walker.queryColumnDictionaryMap)) {
			return false;
		}
		if (materializeQualifiedUnresolvedEntry(
				unresolvedKey,
				entryValue,
				visibleAliasMap,
				localTableCollectionOverride)) {
			return true;
		}
		return resolutionResult.status == QualifiedScopeResolutionStatus.RESOLVED_QUERY_SOURCE
				|| resolutionResult.status == QualifiedScopeResolutionStatus.RESOLVED_WILDCARD_QUERY_SOURCE;
	}

	/**
	 * Unified qualified-column egress at query exit: resolve via CTE context or stack-visible
	 * scope, materialize into the global table dictionary, optionally fatal the remainder.
	 */
	@SuppressWarnings("unchecked")
	private void resolveQualifiedUnresolvedEntries(
			HashMap<String, Object> qualifiedUnresolved,
			HashMap<String, Object> visibleAliasMap,
			HashMap<String, Object> visibleTableDictionary,
			boolean emitUnresolvedFatals) {
		if (qualifiedUnresolved == null || qualifiedUnresolved.isEmpty()) {
			return;
		}

		HashMap<String, Object> visibleQuerySourceCollection =
				collectVisibleQuerySourceCollection(visibleAliasMap);
		HashMap<String, Object> tableDictionaryView =
				buildQualifiedResolutionTableDictionaryView(visibleTableDictionary);
		ArrayList<String> resolvedKeys = new ArrayList<String>();
		for (Map.Entry<String, Object> entry : new ArrayList<>(qualifiedUnresolved.entrySet())) {
			String unresolvedKey = entry.getKey();
			if (applyQualifiedScopeResolutionAtBatchExit(
					unresolvedKey,
					entry.getValue(),
					visibleAliasMap,
					visibleTableDictionary,
					visibleQuerySourceCollection,
					tableDictionaryView,
					null)) {
				resolvedKeys.add(unresolvedKey);
			}
		}
		for (String resolvedKey : resolvedKeys) {
			qualifiedUnresolved.remove(resolvedKey);
			releaseResolvedQualifiedGlobalLocationIfQualified(resolvedKey);
		}
		if (qualifiedUnresolved.isEmpty() || !emitUnresolvedFatals) {
			return;
		}

		ArrayList<String> diagnosedKeys = new ArrayList<String>();
		for (Map.Entry<String, Object> unresolvedEntry : new ArrayList<>(qualifiedUnresolved.entrySet())) {
			String unresolvedKey = unresolvedEntry.getKey();
			int dotIndex = unresolvedKey == null ? -1 : unresolvedKey.indexOf('.');
			String columnName = (dotIndex > 0 && dotIndex + 1 < unresolvedKey.length())
					? unresolvedKey.substring(dotIndex + 1)
					: unresolvedKey;
			if (columnName != null
					&& columnName.startsWith("<")
					&& columnName.endsWith(">")
					&& isQualifiedEntryAlreadyMaterialized(unresolvedKey, visibleAliasMap)) {
				diagnosedKeys.add(unresolvedKey);
				continue;
			}
			if (isQualifiedEntryAlreadyMaterialized(unresolvedKey, visibleAliasMap)) {
				diagnosedKeys.add(unresolvedKey);
				continue;
			}
			emitQualifiedScopeExitFatalForUnresolvedKey(
					unresolvedKey,
					unresolvedEntry.getValue(),
					visibleAliasMap,
					visibleTableDictionary,
					visibleQuerySourceCollection,
					qualifiedUnresolved);
			diagnosedKeys.add(unresolvedKey);
		}
		for (String diagnosedKey : diagnosedKeys) {
			qualifiedUnresolved.remove(diagnosedKey);
		}
	}

	/** Resolve a qualified ref against a prior WITH/CTE entry in {@code context_list}. */
	@SuppressWarnings("unchecked")
	private boolean tryResolveQualifiedEntryViaCteContext(
			String unresolvedKey,
			Object entryValue,
			HashMap<String, Object> visibleAliasMap) {
		if (unresolvedKey == null || !unresolvedKey.contains(".") || !hasCteListSymbolMap()) {
			return false;
		}

		int dotIndex = unresolvedKey.indexOf('.');
		String sourceRef = unresolvedKey.substring(0, dotIndex);
		String columnName = unresolvedKey.substring(dotIndex + 1);
		String cteScopeRef = resolveCteScopeReference(sourceRef, visibleAliasMap);
		if (cteScopeRef == null || cteScopeRef.isBlank()) {
			return false;
		}

		boolean resolvedInCte = "*".equals(columnName)
				|| hasColumnInQueryOutputInterface(cteScopeRef, columnName)
				|| hasWildcardInQueryOutputInterface(cteScopeRef);
		if (!resolvedInCte) {
			return false;
		}
		return materializeCteContextQualifiedUnresolvedEntry(
				columnName,
				entryValue,
				cteScopeRef);
	}

	/**
	 * CTE-qualified refs from outside the CTE body materialize onto the CTE query scope
	 * ({@code queryN.col}) only — same contract as derived subquery aliases such as {@code ix.col}.
	 */
	private boolean materializeCteContextQualifiedUnresolvedEntry(
			String columnName,
			Object entryValue,
			String cteScopeRef) {
		if (cteScopeRef == null || cteScopeRef.isBlank()) {
			return false;
		}

		mergeExplicitQualifiedUnknownIntoSourceQueryDictionary(
				cteScopeRef,
				columnName,
				entryValue);
		return true;
	}

	private boolean isQualifiedEntryAlreadyMaterialized(
			String unresolvedKey,
			HashMap<String, Object> visibleAliasMap) {
		if (unresolvedKey == null || !unresolvedKey.contains(".")) {
			return false;
		}
		int dotIndex = unresolvedKey.indexOf('.');
		String sourceRef = unresolvedKey.substring(0, dotIndex);
		String columnName = unresolvedKey.substring(dotIndex + 1);
		if (hasCteListSymbolMap()) {
			String cteScopeRef = resolveCteScopeReference(sourceRef, visibleAliasMap);
			if (cteScopeRef != null && !cteScopeRef.isBlank()) {
				Object queryDictionaryObj = getQuerySourceDictionaryPreferDefinition(cteScopeRef);
				if (queryDictionaryObj instanceof Map<?, ?> queryDictionary
						&& (queryDictionary.containsKey(columnName)
								|| containsKeyIgnoreCase((Map<String, Object>) queryDictionary, columnName))) {
					return true;
				}
			}
		}
		if (visibleAliasMap != null && !visibleAliasMap.isEmpty()) {
			String querySourceRef = resolveAliasToQuerySourceFromAliasMap(sourceRef, visibleAliasMap);
			if (querySourceRef != null && !querySourceRef.isBlank()
					&& walker.isNonTableQuerySourceReference(querySourceRef)) {
				Object queryDictionaryObj = getQuerySourceDictionaryPreferDefinition(querySourceRef);
				if (queryDictionaryObj instanceof Map<?, ?> queryDictionary
						&& (queryDictionary.containsKey(columnName)
								|| containsKeyIgnoreCase((Map<String, Object>) queryDictionary, columnName))) {
					return true;
				}
			}
		}
		String resolvedTableRef = walker.resolveAliasToTableName(sourceRef, visibleAliasMap);
		if (resolvedTableRef == null || resolvedTableRef.isBlank()) {
			resolvedTableRef = sourceRef;
		}
		HashMap<String, Object> globalTableDictionary = walker.getCurrentTableDictionary();
		if (globalTableDictionary == null || globalTableDictionary.isEmpty()) {
			globalTableDictionary = walker.getWalkerTableDictionary();
		}
		HashMap<String, Object> targetDictionary = walker.getTableDictionaryForReference(
				resolvedTableRef,
				globalTableDictionary);
		if (targetDictionary == null) {
			return false;
		}
		return targetDictionary.containsKey(columnName)
				|| containsKeyIgnoreCase(targetDictionary, columnName);
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

	public void emitQualifiedSourceNotFoundFatals(HashMap<String, Object> qualifiedUnresolvedMap) {
		if (qualifiedUnresolvedMap == null || qualifiedUnresolvedMap.isEmpty()) {
			return;
		}

		@SuppressWarnings("unchecked")
		HashMap<String, Object> currentTableAliasMap = (walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY) instanceof HashMap<?, ?>)
				? (HashMap<String, Object>) walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY)
				: new HashMap<String, Object>();
		HashMap<String, Object> visibleAliasMap =
				collectOuterVisibleScope(null, true).aliases;
		for (Map.Entry<String, Object> aliasEntry : visibleAliasMap.entrySet()) {
			currentTableAliasMap.putIfAbsent(aliasEntry.getKey(), aliasEntry.getValue());
		}
		mergeContextListAliasesIntoMap(currentTableAliasMap, collectPublishedScopeContextList());
		if (currentTableAliasMap.isEmpty()) {
			HashMap<String, Object> topLevelAliasMap = getTopLevelQueryTableAliasMap();
			if (topLevelAliasMap != null) {
				currentTableAliasMap.putAll(topLevelAliasMap);
			}
		}
		HashMap<String, Object> currentTableCollection = walker.peekCurrentTableDictionary();
		if (currentTableCollection == null || currentTableCollection.isEmpty()) {
			currentTableCollection = new HashMap<String, Object>();
		}
		HashMap<String, Object> visibleTableCollection =
				collectOuterVisibleScope(null, true).tableDictionary;
		for (Map.Entry<String, Object> tableEntry : visibleTableCollection.entrySet()) {
			currentTableCollection.putIfAbsent(tableEntry.getKey(), tableEntry.getValue());
		}

		resolveQualifiedUnresolvedEntries(
				qualifiedUnresolvedMap,
				currentTableAliasMap,
				currentTableCollection,
				true);
	}

	@SuppressWarnings("unchecked")
	public boolean hasCteListSymbolMap() {
		if (!collectPublishedScopeContextList().isEmpty()) {
			return true;
		}

		Map<String, Object> currentCteList = getContextListSymbolMap(walker.symbolTable);
		if (currentCteList != null && !currentCteList.isEmpty()) {
			return true;
		}

		for (Map<String, Object> ancestorSymbols : getAncestorSymbolTables()) {
			Map<String, Object> ancestorCteList = getContextListSymbolMap(ancestorSymbols);
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

		Map<String, Object> publishedContextList = collectPublishedScopeContextList();
		if (!publishedContextList.isEmpty()) {
			String publishedScope = resolveCteScopeReferenceInContextList(
					sourceRef, tableAliasMap, publishedContextList);
			if (publishedScope != null) {
				return publishedScope;
			}
		}

		String localScope = resolveCteScopeReferenceInSymbols(sourceRef, tableAliasMap, walker.symbolTable);
		if (localScope != null) {
			return localScope;
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
					&& isQueryOrSetOrValuesSourceReference(existingAliasTarget)) {
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

	/**
	 * Opens a nested symbol-table frame inheriting the nearest visible named scope registry
	 * ({@code context_list}), table aliases, and table dictionary from ancestor frames.
	 * Used for WITH bodies, correlated subqueries (predicand/IN/EXISTS), and nested FROM scopes.
	 */
	@SuppressWarnings("unchecked")
	public void pushSymbolTableWithParentVisibleScope() {
		OuterVisibleScope outerVisibleScope = collectOuterVisibleScope(null, true);

		walker.pushSymbolTable();
		walker.symbolTable.put(MUMBLE_LOCAL_FROM_REGISTERED_ALIASES_KEY, new LinkedHashSet<String>());
		if (!outerVisibleScope.contextList.isEmpty()) {
			walker.symbolTable.put(
					MUMBLE_CONTEXT_LIST_KEY,
					new LinkedHashMap<String, Object>(outerVisibleScope.contextList));
		}
		if (!outerVisibleScope.aliases.isEmpty()) {
			walker.symbolTable.put(
					MUMBLE_INHERITED_VISIBLE_ALIASES_KEY,
					new HashMap<String, Object>(outerVisibleScope.aliases));
			HashMap<String, Object> contextBackedAliases = new HashMap<String, Object>();
			mergeContextListAliasesIntoMap(contextBackedAliases, outerVisibleScope.contextList);
			if (!contextBackedAliases.isEmpty()) {
				walker.symbolTable.put(MUMBLE_TABLE_ALIAS_KEY, contextBackedAliases);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static final class OuterVisibleScope {
		private final LinkedHashMap<String, Object> contextList = new LinkedHashMap<String, Object>();
		private final HashMap<String, Object> aliases = new HashMap<String, Object>();
		private final HashMap<String, Object> tableDictionary = new HashMap<String, Object>();
	}

	/**
	 * Ancestor-only visible scope: direct {@code table_alias}, {@code table_dictionary}, and
	 * {@code context_list} per frame, plus nested {@code context_list} from published child scopes.
	 * Never merges sibling branch {@code table_alias} or {@code table_dictionary} maps from nested
	 * {@code queryN}/{@code def_*} payloads.
	 *
	 * @param ancestorStopFrame when non-null, stop after merging this ancestor (exclusive outward)
	 */
	@SuppressWarnings("unchecked")
	private OuterVisibleScope collectOuterVisibleScope(
			Map<String, Object> ancestorStopFrame,
			boolean includeActiveFrame) {
		OuterVisibleScope result = new OuterVisibleScope();
		for (Map<String, Object> ancestorSymbols : getAncestorSymbolTables()) {
			mergeOuterVisibleScopeFromFrame(result, ancestorSymbols);
			if (ancestorStopFrame != null && ancestorStopFrame == ancestorSymbols) {
				break;
			}
		}
		if (includeActiveFrame) {
			mergeOuterVisibleScopeFromFrame(result, walker.symbolTable);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private void mergeOuterVisibleScopeFromFrame(
			OuterVisibleScope result,
			Map<String, Object> scopeSymbols) {
		if (scopeSymbols == null || scopeSymbols.isEmpty()) {
			return;
		}
		Object inheritedAliasesObj = scopeSymbols.get(MUMBLE_INHERITED_VISIBLE_ALIASES_KEY);
		if (inheritedAliasesObj instanceof HashMap<?, ?> inheritedAliasMapObj) {
			result.aliases.putAll((HashMap<String, Object>) inheritedAliasMapObj);
		}
		mergeContextListFromScopeSymbols(result.contextList, scopeSymbols);
		Object aliasObj = scopeSymbols.get(MUMBLE_TABLE_ALIAS_KEY);
		if (aliasObj instanceof HashMap<?, ?> aliasMapObj) {
			result.aliases.putAll((HashMap<String, Object>) aliasMapObj);
		}
		mergeContextListFromScopeSymbols(result.aliases, scopeSymbols);
		Object tableDictObj = scopeSymbols.get(MUMBLE_TABLE_DICTIONARY_KEY);
		if (tableDictObj instanceof HashMap<?, ?> tableDictMapObj) {
			result.tableDictionary.putAll((HashMap<String, Object>) tableDictMapObj);
		}
		Object targetTableObj = scopeSymbols.get(MUMBLE_TARGET_TABLE_KEY);
		if (targetTableObj instanceof HashMap<?, ?> targetTableMapObj) {
			result.tableDictionary.putAll((HashMap<String, Object>) targetTableMapObj);
		}
	}

	@SuppressWarnings("unchecked")
	private void mergeContextListAliasesFromScopeTree(
			Map<String, Object> targetAliasMap,
			Map<String, Object> scopeSymbols,
			boolean directPutIfAbsent,
			boolean nestedPutIfAbsent) {
		if (targetAliasMap == null || scopeSymbols == null || scopeSymbols.isEmpty()) {
			return;
		}

		mergeContextListAliasesIntoMap(
				targetAliasMap,
				getContextListSymbolMap(scopeSymbols),
				directPutIfAbsent);
		for (Object valueObj : scopeSymbols.values()) {
			if (valueObj instanceof Map<?, ?> nestedScopeObj) {
				mergeContextListAliasesIntoMap(
						targetAliasMap,
						getContextListSymbolMap((Map<String, Object>) nestedScopeObj),
						nestedPutIfAbsent);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void mergeContextListFromScopeSymbols(
			Map<String, Object> merged,
			Map<String, Object> scopeSymbols) {
		if (scopeSymbols == null || scopeSymbols.isEmpty()) {
			return;
		}
		mergeContextListAliasesIntoMap(merged, getContextListSymbolMap(scopeSymbols), true);
	}

	private void mergeContextListAliasesIntoMap(
			Map<String, Object> targetAliasMap,
			Map<String, Object> contextList,
			boolean putIfAbsent) {
		if (targetAliasMap == null || contextList == null || contextList.isEmpty()) {
			return;
		}
		for (Map.Entry<String, Object> entry : contextList.entrySet()) {
			String alias = entry.getKey();
			if (alias == null || alias.isBlank()) {
				continue;
			}
			if (entry.getValue() instanceof String scopeRef && !scopeRef.isBlank()) {
				if (putIfAbsent) {
					targetAliasMap.putIfAbsent(alias, scopeRef);
				} else {
					targetAliasMap.put(alias, scopeRef);
				}
			}
		}
	}

	private void mergeContextListAliasesIntoMap(
			Map<String, Object> targetAliasMap,
			Map<String, Object> contextList) {
		mergeContextListAliasesIntoMap(targetAliasMap, contextList, true);
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
	public Map<String, Object> getContextListSymbolMap(Map<String, Object> symbols) {
		if (symbols == null) {
			return null;
		}

		Object contextListObj = symbols.get(MUMBLE_CONTEXT_LIST_KEY);
		if (contextListObj instanceof Map<?, ?> contextListMapObj) {
			return (Map<String, Object>) contextListMapObj;
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
		Map<String, Object> cteListMap = getContextListSymbolMap(symbols);
		return resolveCteScopeReferenceInContextList(sourceRef, tableAliasMap, cteListMap);
	}

	public String resolveCteScopeReferenceInContextList(
			String sourceRef,
			HashMap<String, Object> tableAliasMap,
			Map<String, Object> cteListMap) {
		if (cteListMap == null || cteListMap.isEmpty() || sourceRef == null || sourceRef.isBlank()) {
			return null;
		}

		Object directScope = cteListMap.get(sourceRef);
		if (directScope instanceof String directScopeRef && !directScopeRef.isBlank()) {
			return directScopeRef;
		}

		for (Object cteScopeValue : cteListMap.values()) {
			if (cteScopeValue instanceof String cteScopeRef && cteScopeRef.equals(sourceRef)) {
				return cteScopeRef;
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> collectPublishedScopeContextList() {
		LinkedHashMap<String, Object> merged = new LinkedHashMap<String, Object>();
		mergeContextListFromScopeSymbols(merged, walker.symbolTable);
		for (Map<String, Object> ancestorSymbols : getAncestorSymbolTables()) {
			mergeContextListFromScopeSymbols(merged, ancestorSymbols);
		}
		return merged;
	}

	@SuppressWarnings("unchecked")
	private void mergePublishedScopeContextListIntoAliasMap(HashMap<String, Object> aliasMap) {
		if (aliasMap == null) {
			return;
		}

		mergeContextListAliasesIntoMap(aliasMap, collectPublishedScopeContextList());
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

	/**
	 * Collects deferred unresolved columns at statement exit. Only the root live map is
	 * drained here; archived scopes finalize at {@link #finalizeScopeDeferredUnresolved}
	 * (WITH CTE registration, UNION/INTERSECT exit, and DML scope publish).
	 * and {@code exitWith_query} hoists main-body deferred refs back to the root.
	 */
	@SuppressWarnings("unchecked")
	private HashMap<String, Object> collectStatementBoundaryUnresolvedColumns() {
		HashMap<String, Object> collected = new HashMap<String, Object>();

		Object rootUnresolvedObject = walker.symbolTable.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
		if (rootUnresolvedObject instanceof HashMap<?, ?>) {
			walker.mergeUnknownEntries(collected, (HashMap<String, Object>) rootUnresolvedObject);
		}

		return collected;
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

			boolean aliasTargetsQuery = isQueryOrSetOrValuesSourceReference(aliasTarget);
			if (!aliasTargetsQuery) {
				continue;
			}

			if ("*".equals(columnName)) {
				promoteQualifiedWildcardIntoQuerySource(aliasTarget, unresolvedEntry.getValue());
				continue;
			}

			Object queryDictionaryObj = getQuerySourceDictionaryPreferDefinition(aliasTarget);
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
			if (isQueryOrSetOrValuesSourceReference(mappedSource)) {
				String normalizedSourceKey = normalizeQuerySourceReference(mappedSource);
				if (normalizedSourceKey != null && !normalizedSourceKey.isBlank()) {
					queryBackedSources.add(normalizedSourceKey);
				}
			}
		}

		if (queryBackedSources.size() != 1) {
			return false;
		}

		String sourceQueryKey = queryBackedSources.iterator().next();
		Object queryDictionaryObj = getQuerySourceDictionaryPreferDefinition(sourceQueryKey);
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
		flattenSubTreeForDependencyColumns(subTree, columnList, false);
	}

	/**
	 * Collects column and column/predicand substitution references from an AST subtree.
	 * Interface flattening recurses into query-backed subtrees so output-column dependencies
	 * (e.g. scalar subquery {@code max(D)}) are recorded on the interface entry.
	 */
	public void flattenSubTreeForDependencyColumns(HashMap<String, Object> subTree, ArrayList<Object> columnList) {
		flattenSubTreeForDependencyColumns(subTree, columnList, false);
	}

	/**
	 * Clause flattening ({@code filters}, {@code grouped_by}, {@code ordered_by}) collects only
	 * references in the enclosing query scope. The {@code filters} list aggregates column refs from
	 * WHERE, HAVING, QUALIFY, and JOIN ON (not separate symbol-table keys). Subquery-local columns
	 * are collected into that subquery's {@code unresolved_column} map, resolved at subquery exit,
	 * and linked via {@link #MUMBLE_DEPENDENT_QUERIES_KEY}; they must not be re-hoisted here.
	 */
	private boolean isPredicateSubqueryBoundarySubtree(HashMap<String, Object> subTree) {
		if (subTree == null || subTree.isEmpty()) {
			return false;
		}
		// Note: MUMBLE_IN_LIST_KEY / MUMBLE_NOT_IN_LIST_KEY are intentionally NOT checked here.
		// The IN predicate container {item: {column:...}, in_list: {select:...}} contains
		// MUMBLE_IN_LIST_KEY as a peer of item — treating that container as a boundary would
		// silently drop the item column reference from the filters list.  The actual subquery
		// value (in_list: {select:...}) is still caught by MUMBLE_SELECT_KEY one level deeper.
		return subTree.containsKey(MUMBLE_EXISTS_KEY)
				|| subTree.containsKey(MUMBLE_SELECT_KEY)
				|| isQueryBackedSelectItemReference(subTree);
	}

	private void flattenSubTreeForDependencyColumns(
			HashMap<String, Object> subTree,
			ArrayList<Object> columnList,
			boolean skipQueryBackedSubtrees) {
		if (subTree == null) {
			return;
		}

		if (skipQueryBackedSubtrees && isPredicateSubqueryBoundarySubtree(subTree)) {
			return;
		}

		if (skipQueryBackedSubtrees && isQueryBackedSelectItemReference(subTree)) {
			return;
		}

		if (subTree.containsKey(MUMBLE_COLUMN_KEY)) {
			Object col = subTree.get(MUMBLE_COLUMN_KEY);
			if (!columnList.contains(col)) {
				columnList.add(col);
			}
			return;
		}
		if (subTree.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
			Object subst = subTree.get(MUMBLE_SUBSTITUTION_KEY);
			if (subst instanceof HashMap<?, ?> substMapObj) {
				Object type = substMapObj.get("type");
				if (type != null && (MUMBLE_COLUMN_KEY.equals(type) || MUMBLE_PREDICAND_KEY.equals(type))) {
					if (!columnList.contains(subst)) {
						columnList.add(subst);
					}
				}
			}
			return;
		}

		for (Object value : subTree.values()) {
			if (value instanceof HashMap<?, ?> valueMapObj) {
				HashMap<String, Object> valueMap = (HashMap<String, Object>) valueMapObj;
				if (skipQueryBackedSubtrees && isPredicateSubqueryBoundarySubtree(valueMap)) {
					continue;
				}
				if (skipQueryBackedSubtrees && isQueryBackedSelectItemReference(valueMap)) {
					continue;
				}
				flattenSubTreeForDependencyColumns(valueMap, columnList, skipQueryBackedSubtrees);
			} else if (value instanceof ArrayList<?> valueListObj) {
				for (Object listItem : (ArrayList<Object>) valueListObj) {
					if (listItem instanceof HashMap<?, ?> listMapObj) {
						HashMap<String, Object> listMap = (HashMap<String, Object>) listMapObj;
						if (skipQueryBackedSubtrees && isPredicateSubqueryBoundarySubtree(listMap)) {
							continue;
						}
						if (skipQueryBackedSubtrees && isQueryBackedSelectItemReference(listMap)) {
							continue;
						}
						flattenSubTreeForDependencyColumns(listMap, columnList, skipQueryBackedSubtrees);
					}
				}
			}
		}
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
			releaseResolvedQualifiedGlobalLocationIfQualified(resolvedKey);
		}
	}

	@SuppressWarnings("unchecked")
	public void resolveUpdateQualifiedUnresolvedColumnsToCteSources(
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> tableAliasMap) {
		if (unresolvedColumnMap == null || unresolvedColumnMap.isEmpty()) {
			return;
		}

		HashMap<String, Object> effectiveAliasMap = tableAliasMap != null
				? new HashMap<String, Object>(tableAliasMap)
				: new HashMap<String, Object>();
		mergeContextListAliasesIntoMap(effectiveAliasMap, collectPublishedScopeContextList());

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

			String cteScopeRef = resolveCteScopeReference(qualifier, effectiveAliasMap);
			if (cteScopeRef == null || cteScopeRef.isBlank()) {
				continue;
			}

			boolean resolvedInCte = "*".equals(columnName)
					|| hasColumnInQueryOutputInterface(cteScopeRef, columnName)
					|| hasWildcardInQueryOutputInterface(cteScopeRef);
			if (!resolvedInCte) {
				continue;
			}

			mergeExplicitQualifiedUnknownIntoSourceQueryDictionary(
					cteScopeRef,
					columnName,
					unresolvedEntry.getValue());
			resolvedKeys.add(unresolvedKey);
		}

		for (String resolvedKey : resolvedKeys) {
			unresolvedColumnMap.remove(resolvedKey);
			releaseResolvedQualifiedGlobalLocationIfQualified(resolvedKey);
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
			if (!(rhsRefsObj instanceof List<?> rhsRefsList) || rhsRefsList.isEmpty()) {
				continue;
			}

			@SuppressWarnings("unchecked")
			List<Object> rhsRefs = (List<Object>) rhsRefsList;
			for (int refIndex = 0; refIndex < rhsRefs.size(); refIndex++) {
				Object rhsRefObj = rhsRefs.get(refIndex);
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

				if (updateRhsColumnExistsInFromSources(
						columnName,
						tableCollection,
						visibleQuerySourceCollection,
						tableAliasMap)) {
					continue;
				}

				// If UPDATE has exactly one FROM source table, treat unqualified RHS refs
				// as originating from that source before falling back to target table.
				if (tableCollection != null && tableCollection.size() == 1) {
					String singleFromTableRef = tableCollection.keySet().iterator().next();
					Object singleFromTableObj = tableCollection.get(singleFromTableRef);
					if (singleFromTableObj instanceof Map<?, ?> singleFromTableMapObj) {
						Map<String, Object> singleFromTableMap = (Map<String, Object>) singleFromTableMapObj;
						Object normalizedRefs = resolveUpdateRhsColumnRefs(
								unresolvedColumnMap,
								columnName,
								rhsRefObj);
						if (normalizedRefs != null) {
							walker.mergeResolvedColumnIntoDictionary(
									(HashMap<String, Object>) singleFromTableMap,
									columnName,
									normalizedRefs);
							replaceAssignmentRhsReferenceWithResolved(
									rhsRefs,
									refIndex,
									rhsRefObj,
									singleFromTableRef);
							continue;
						}
					}
				}

				Object normalizedRefs = resolveUpdateRhsColumnRefs(
						unresolvedColumnMap,
						columnName,
						rhsRefObj);
				if (normalizedRefs == null) {
					continue;
				}

				walker.mergeResolvedColumnIntoDictionary(targetColumns, columnName, normalizedRefs);
				replaceAssignmentRhsReferenceWithResolved(
						rhsRefs,
						refIndex,
						rhsRefObj,
						normalizedTarget);
			}
		}
	}

	private boolean updateRhsColumnExistsInFromSources(
			String columnName,
			HashMap<String, Object> fromTableCollection,
			HashMap<String, Object> visibleQuerySourceCollection,
			HashMap<String, Object> tableAliasMap) {
		if (columnName == null || columnName.isBlank()) {
			return false;
		}

		if (walker.tableCollectionContainsColumn(columnName, fromTableCollection)) {
			return true;
		}

		LinkedHashSet<String> querySourcesWithColumn = collectQuerySourcesWithColumn(
				columnName,
				visibleQuerySourceCollection,
				tableAliasMap);
		return !querySourcesWithColumn.isEmpty();
	}

	@SuppressWarnings("unchecked")
	private Object resolveUpdateRhsColumnRefs(
			HashMap<String, Object> unresolvedColumnMap,
			String columnName,
			Object rhsRefObj) {
		Object unresolvedValue = findUnqualifiedUnknownEntry(unresolvedColumnMap, columnName);

		Object normalizedRefs = normalizeUpdateColumnRefs(unresolvedValue);
		if (normalizedRefs != null) {
			consumeUnqualifiedUnknownEntry(unresolvedColumnMap, columnName);
			return normalizedRefs;
		}

		normalizedRefs = normalizeUpdateColumnRefs(rhsRefObj);
		if (normalizedRefs != null) {
			consumeUnqualifiedUnknownEntry(unresolvedColumnMap, columnName);
			return normalizedRefs;
		}

		ArrayList<Object> assignmentRhsTokenRefs = resolveUpdateAssignmentRhsTokenRefs(columnName);
		if (assignmentRhsTokenRefs != null && !assignmentRhsTokenRefs.isEmpty()) {
			consumeUnqualifiedUnknownEntry(unresolvedColumnMap, columnName);
			return assignmentRhsTokenRefs;
		}

		if (rhsRefObj instanceof Map<?, ?> rhsRefMapObj) {
			consumeUnqualifiedUnknownEntry(unresolvedColumnMap, columnName);
			ArrayList<Object> syntheticRefs = new ArrayList<Object>();
			syntheticRefs.add(new HashMap<String, Object>((Map<String, Object>) rhsRefMapObj));
			return syntheticRefs;
		}

		return null;
	}

	private Object findUnqualifiedUnknownEntry(
			HashMap<String, Object> unresolvedColumnMap,
			String columnName) {
		Object unresolvedValue = getUnqualifiedUnknownEntry(unresolvedColumnMap, columnName);
		if (unresolvedValue != null || unresolvedColumnMap == null || unresolvedColumnMap.isEmpty()) {
			return unresolvedValue;
		}

		for (Map.Entry<String, Object> entry : unresolvedColumnMap.entrySet()) {
			String unresolvedKey = entry.getKey();
			if (unresolvedKey == null || unresolvedKey.contains(".")) {
				continue;
			}
			if (!unresolvedKey.equalsIgnoreCase(columnName)) {
				continue;
			}
			return entry.getValue();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private void replaceAssignmentRhsReferenceWithResolved(
			List<Object> rhsRefs,
			int refIndex,
			Object rhsRefObj,
			String resolvedSourceRef) {
		Object resolvedRef = cloneReferenceWithResolvedTableRef(rhsRefObj, resolvedSourceRef);
		if (resolvedRef != rhsRefObj) {
			rhsRefs.set(refIndex, resolvedRef);
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
			releaseResolvedQualifiedGlobalLocationIfQualified(resolvedKey);
		}
	}

	private void releaseResolvedQualifiedGlobalLocationIfQualified(String unresolvedKey) {
		if (unresolvedKey != null && unresolvedKey.contains(".")) {
			walker.releaseResolvedQualifiedGlobalLocation(unresolvedKey);
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
				String columnName = extractColumnNameFromColumnReferenceMap(
						(Map<String, Object>) columnMapObj);
				if (columnName != null && !columnName.isBlank()) {
					return columnName;
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
			Object groupedByList,
			Object orderedByList,
			HashMap<String, Object> tableAliasCollection,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> scopedQueryCollection,
			HashMap<String, Object> unresolvedColumnMap) {
		HashMap<String, Object> locallyResolvableEntries = new HashMap<String, Object>();
		if (explicitQualifiedUnknownEntries == null || explicitQualifiedUnknownEntries.isEmpty()) {
			return locallyResolvableEntries;
		}

		HashMap<String, Object> visibleAliasMap = new HashMap<String, Object>();
		if (tableAliasCollection != null && !tableAliasCollection.isEmpty()) {
			visibleAliasMap.putAll(tableAliasCollection);
		}
		HashMap<String, Object> visibleTableCollection = new HashMap<String, Object>();
		if (tableCollection != null && !tableCollection.isEmpty()) {
			visibleTableCollection.putAll(tableCollection);
		}

		for (Map.Entry<String, Object> unknownEntry : explicitQualifiedUnknownEntries.entrySet()) {
			String unresolvedKey = unknownEntry.getKey();
			String explicitTableRef = resolveExplicitTableRefForUnknownEntry(
					unresolvedKey,
					localInterface,
					filtersList,
					groupedByList,
					orderedByList);
			if (explicitTableRef == null) {
				locallyResolvableEntries.put(unresolvedKey, unknownEntry.getValue());
				continue;
			}

			String resolvedTableRef = walker.resolveAliasToTableName(explicitTableRef, visibleAliasMap);
			String resolvedNonTableSourceRef = resolveAliasToQuerySourceRefPreferDefinition(
					explicitTableRef,
					visibleAliasMap,
					scopedQueryCollection);
			HashMap<String, Object> indicatedTableDictionary = walker.getTableDictionaryForReference(
					resolvedTableRef,
					visibleTableCollection);

			boolean hasVisibleSource = resolvedNonTableSourceRef != null
					|| indicatedTableDictionary != null
					|| containsKeyIgnoreCase(visibleAliasMap, explicitTableRef);
			if (hasVisibleSource) {
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
			Object... clauseRefLists) {
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

		if (clauseRefLists != null) {
			for (Object clauseRefListObj : clauseRefLists) {
				if (!(clauseRefListObj instanceof ArrayList<?> clauseRefs)) {
					continue;
				}
				for (Object refObj : clauseRefs) {
					if (!(refObj instanceof Map<?, ?> refMap)) {
						continue;
					}
					String refName = walker.extractReferenceNameFromInterfaceEntry(refMap);
					String refTableRef = walker.extractReferenceTableRefFromInterfaceEntry(refMap);
					if (refName != null
							&& refTableRef != null
							&& columnName.equals(refName)
							&& !"*".equals(refTableRef)) {
						return refTableRef;
					}
				}
			}
		}

		return null;
	}

	private void materializeInterfaceUnqualifiedReferenceIfDeferredScope(
			boolean deferCorrelatedValueSubqueryQualifiedUnknowns,
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> tableAliasCollection,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> localInterface,
			HashMap<String, Object> visibleQuerySourceCollection,
			String clauseKey,
			String resolvedSourceRef,
			String columnName) {
		if (!deferCorrelatedValueSubqueryQualifiedUnknowns) {
			return;
		}
		materializeResolvedUnqualifiedReference(
				unresolvedColumnMap,
				tableCollection,
				tableAliasCollection,
				localCurrentQueryDictionary,
				localInterface,
				visibleQuerySourceCollection,
				clauseKey,
				resolvedSourceRef,
				columnName);
	}

	@SuppressWarnings("unchecked")
	public void materializeResolvedUnqualifiedReference(
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> tableAliasCollection,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> localInterface,
			HashMap<String, Object> visibleQuerySourceCollection,
			String clauseKey,
			String resolvedSourceRef,
			String columnName) {
		if (columnName == null || columnName.isBlank()) {
			return;
		}

		String canonicalSourceRef = normalizeTableRef(resolvedSourceRef);
		Object unresolvedEntry = consumeQualifiedUnknownEntry(
				unresolvedColumnMap,
				canonicalSourceRef,
				columnName);
		if (unresolvedEntry == null) {
			unresolvedEntry = consumeUnqualifiedUnknownEntry(unresolvedColumnMap, columnName);
		} else {
			// Clear any map-shaped fallback entry inserted by deferred clause collection
			// so unresolved diagnostics are not polluted by duplicates.
			consumeUnqualifiedUnknownEntry(unresolvedColumnMap, columnName);
		}
		if (unresolvedEntry == null || resolvedSourceRef == null || resolvedSourceRef.isBlank()) {
			return;
		}

		String queryAliasSourceRef = resolveAliasToQuerySourceFromAliasMap(
				canonicalSourceRef,
				tableAliasCollection);
		boolean queryBackedSource = queryAliasSourceRef != null
				|| walker.isNonTableQuerySourceReference(canonicalSourceRef)
				|| isTableFunctionSourceReference(canonicalSourceRef);
		if (!queryBackedSource && canonicalSourceRef != null && canonicalSourceRef.startsWith("<")) {
			Object substitutionType = walker.substitutionsMap == null ? null : walker.substitutionsMap.get(canonicalSourceRef);
			if (substitutionType == null || !MUMBLE_TUPLE_KEY.equals(substitutionType.toString())) {
				return;
			}
		}

		if (queryBackedSource) {
			String querySourceRef = (queryAliasSourceRef != null && !queryAliasSourceRef.isBlank())
					? queryAliasSourceRef
					: canonicalSourceRef;
			mergeExplicitQualifiedUnknownIntoSourceQueryDictionary(
					querySourceRef,
					columnName,
					unresolvedEntry);
			return;
		}

		boolean outputUsage = isIntraQueryOutputClauseUsage(
				clauseKey,
				columnName,
				null,
				localInterface,
				tableAliasCollection,
				visibleQuerySourceCollection,
				tableCollection);
		if (outputUsage && localCurrentQueryDictionary != null) {
			walker.mergeResolvedColumnIntoDictionary(
					localCurrentQueryDictionary,
					columnName,
					unresolvedEntry);
			if (isIntraQueryOutputAliasUsage(columnName, null, localInterface, tableCollection)) {
				return;
			}
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

	/**
	 * Removes and returns one qualified unknown entry from a scope-local map.
	 * Global qualified location capture is for diagnostics only — not re-read here.
	 */
	public Object consumeQualifiedUnknownEntry(
			HashMap<String, Object> unresolvedColumnMap,
			String tableRef,
			String columnName) {
		if (tableRef == null || tableRef.isBlank()
				|| columnName == null || columnName.isBlank()) {
			return null;
		}

		String directKey = tableRef + "." + columnName;
		if (unresolvedColumnMap != null && !unresolvedColumnMap.isEmpty()) {
			Object removedDirect = unresolvedColumnMap.remove(directKey);
			if (removedDirect != null) {
				releaseResolvedQualifiedGlobalLocationIfQualified(directKey);
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
					Object removed = unresolvedColumnMap.remove(key);
					releaseResolvedQualifiedGlobalLocationIfQualified(key);
					return removed;
				}
			}
		}

		return null;
	}

	private boolean isRelationalModifierDerivedColumnReference(
			HashMap<String, Object> localDerivedColumns,
			ArrayList<Object> relationalModifierInterfaceHints,
			String tableRef,
			String columnName) {
		return isRelationalModifierDerivedColumnReference(
				localDerivedColumns,
				relationalModifierInterfaceHints,
				tableRef,
				columnName,
				null);
	}

	@SuppressWarnings("unchecked")
	private boolean isRelationalModifierDerivedColumnReference(
			HashMap<String, Object> localDerivedColumns,
			ArrayList<Object> relationalModifierInterfaceHints,
			String tableRef,
			String columnName,
			HashMap<String, Object> visibleAliasMap) {
		if (!containsKeyIgnoreCase(localDerivedColumns, columnName)) {
			return false;
		}
		if (tableRef == null || tableRef.isBlank()) {
			return true;
		}
		if (relationalModifierInterfaceHints != null && !relationalModifierInterfaceHints.isEmpty()) {
			for (Object hintObj : relationalModifierInterfaceHints) {
				if (!(hintObj instanceof Map<?, ?> hintMapObj)) {
					continue;
				}
				Map<String, Object> hintMap = (Map<String, Object>) hintMapObj;
				if (hintDefinesDerivedColumn(hintMap, columnName)
						&& hintSourceRefMatches(hintMap, tableRef, visibleAliasMap)) {
					return true;
				}
			}
		}

		return derivedColumnMapSourceRefMatches(localDerivedColumns, columnName, tableRef, visibleAliasMap);
	}

	@SuppressWarnings("unchecked")
	private boolean derivedColumnMapSourceRefMatches(
			HashMap<String, Object> localDerivedColumns,
			String columnName,
			String tableRef,
			HashMap<String, Object> visibleAliasMap) {
		if (localDerivedColumns == null || localDerivedColumns.isEmpty()
				|| tableRef == null || tableRef.isBlank()) {
			return false;
		}

		String resolvedKey = findKeyIgnoreCase(localDerivedColumns, columnName);
		if (resolvedKey == null) {
			return false;
		}

		Object refsObj = localDerivedColumns.get(resolvedKey);
		if (!(refsObj instanceof ArrayList<?> refs) || refs.isEmpty()) {
			return false;
		}

		for (Object refObj : refs) {
			String refTable = walker.extractReferenceTableRefFromInterfaceEntry(refObj);
			if (derivedColumnSourceRefMatchesTableRef(tableRef, refTable, visibleAliasMap)) {
				return true;
			}
		}
		return false;
	}

	private boolean derivedColumnSourceRefMatchesTableRef(
			String tableRef,
			String derivedSourceRef,
			HashMap<String, Object> visibleAliasMap) {
		if (derivedSourceRef == null || derivedSourceRef.isBlank()) {
			return false;
		}
		if (derivedSourceRef.equalsIgnoreCase(tableRef)
				|| normalizeTableRef(derivedSourceRef).equalsIgnoreCase(normalizeTableRef(tableRef))) {
			return true;
		}
		if (visibleAliasMap == null || visibleAliasMap.isEmpty()) {
			return false;
		}

		String resolvedTableRef = walker.resolveAliasToTableName(tableRef, visibleAliasMap);
		if (resolvedTableRef != null
				&& (derivedSourceRef.equalsIgnoreCase(resolvedTableRef)
						|| normalizeTableRef(derivedSourceRef).equalsIgnoreCase(normalizeTableRef(resolvedTableRef)))) {
			return true;
		}

		String resolvedDerivedSourceRef = walker.resolveAliasToTableName(derivedSourceRef, visibleAliasMap);
		return resolvedDerivedSourceRef != null
				&& (resolvedDerivedSourceRef.equalsIgnoreCase(tableRef)
						|| normalizeTableRef(resolvedDerivedSourceRef).equalsIgnoreCase(normalizeTableRef(tableRef)));
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> getScopeDerivedColumnsFromSymbolTable() {
		Object derivedObj = walker.symbolTable.get(DERIVED_COLUMNS_HINTS_KEY);
		if (derivedObj instanceof HashMap<?, ?> derivedMap && !derivedMap.isEmpty()) {
			return (HashMap<String, Object>) derivedMap;
		}
		if (derivedObj instanceof ArrayList<?> hintsList && !hintsList.isEmpty()) {
			return buildDerivedColumnsMapFromHints((ArrayList<Object>) hintsList);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private ArrayList<Object> getScopeRelationalModifierHintsFromSymbolTable() {
		Object derivedObj = walker.symbolTable.get(DERIVED_COLUMNS_HINTS_KEY);
		if (derivedObj instanceof ArrayList<?> hintsList && !hintsList.isEmpty()) {
			return (ArrayList<Object>) hintsList;
		}
		return null;
	}

	private boolean hintSourceRefMatches(Map<String, Object> hintMap, String tableRef) {
		return hintSourceRefMatches(hintMap, tableRef, null);
	}

	private boolean hintSourceRefMatches(
			Map<String, Object> hintMap,
			String tableRef,
			HashMap<String, Object> visibleAliasMap) {
		Object sourceRefObj = hintMap.get(MUMBLE_TABLE_REF_KEY);
		if (!(sourceRefObj instanceof String sourceRef) || sourceRef.isBlank()) {
			sourceRefObj = hintMap.get(RELATIONAL_MODIFIER_SOURCE_REF_KEY);
		}
		if (!(sourceRefObj instanceof String sourceRef) || sourceRef.isBlank()) {
			return false;
		}
		return derivedColumnSourceRefMatchesTableRef(tableRef, sourceRef, visibleAliasMap);
	}

	private boolean hintDefinesDerivedColumn(Map<String, Object> hintMap, String columnName) {
		Object derivedColumnsObj = hintMap.get(RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY);
		if (derivedColumnsObj instanceof ArrayList<?> derivedColumns) {
			for (Object derivedColumnObj : derivedColumns) {
				if (derivedColumnObj instanceof String derivedColumn
						&& derivedColumn.equalsIgnoreCase(columnName)) {
					return true;
				}
			}
		}

		Object valueObj = hintMap.get(MUMBLE_VALUE_KEY);
		if (valueObj instanceof String valueColumn && valueColumn.equalsIgnoreCase(columnName)) {
			return true;
		}

		Object forObj = hintMap.get(MUMBLE_FOR_KEY);
		return forObj instanceof String forColumn && forColumn.equalsIgnoreCase(columnName);
	}

	private void consumeDerivedColumnUnknownEntry(
			HashMap<String, Object> unresolvedColumnMap,
			String tableRef,
			String columnName) {
		if (tableRef == null || tableRef.isBlank()) {
			consumeUnqualifiedUnknownEntry(unresolvedColumnMap, columnName);
			return;
		}

		Object qualifiedEntry = consumeQualifiedUnknownEntry(
				unresolvedColumnMap,
				tableRef,
				columnName);
		if (qualifiedEntry == null) {
			consumeUnqualifiedUnknownEntry(unresolvedColumnMap, columnName);
		}
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
	private void patchInterfaceTableRefsForSinglePhysicalTableScope(
			HashMap<String, Object> localInterface,
			HashMap<String, Object> localTableCollection) {
		if (localInterface == null || localInterface.isEmpty()
				|| localTableCollection == null || localTableCollection.size() != 1) {
			return;
		}

		String onlyTableRef = normalizeTableRef(localTableCollection.keySet().iterator().next());
		if (onlyTableRef == null || onlyTableRef.isBlank()
				|| walker.isNonTableQuerySourceReference(onlyTableRef)
				|| isTableFunctionSourceReference(onlyTableRef)) {
			return;
		}

		for (Object refsObj : localInterface.values()) {
			if (!(refsObj instanceof ArrayList<?> refs)) {
				continue;
			}
			ArrayList<Object> mutableRefs = (ArrayList<Object>) refs;
			for (int refIndex = 0; refIndex < mutableRefs.size(); refIndex++) {
				Object refObj = mutableRefs.get(refIndex);
				String tableRef = walker.extractReferenceTableRefFromInterfaceEntry(refObj);
				if (tableRef != null && !tableRef.isBlank()) {
					continue;
				}
				if (!(refObj instanceof Map<?, ?> refMapObj)) {
					continue;
				}
				Map<String, Object> refMap = (Map<String, Object>) refMapObj;
				if (refMap.containsKey(MUMBLE_QUERY_KEY)) {
					continue;
				}
				String columnName = walker.extractReferenceNameFromInterfaceEntry(refObj);
				if (columnName != null && !columnName.isBlank() && !"*".equals(columnName)) {
					continue;
				}
				mutableRefs.set(refIndex, cloneReferenceWithResolvedTableRef(refObj, onlyTableRef));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Object cloneColumnReferenceEntry(Object refObj) {
		if (!(refObj instanceof Map<?, ?> refMapObj)) {
			return refObj;
		}

		return new HashMap<String, Object>((Map<String, Object>) refMapObj);
	}

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

	/**
	 * Materialized references with resolved table references. Called after column resolution
	 * to update the actual clause list arrays.
	 */
	private void updateTrackedClauseLocationsWithResolvedTableRef(
			Set<ClauseRefLocation> locations,
			String resolvedSourceRef) {
		if (locations == null || locations.isEmpty() || resolvedSourceRef == null || resolvedSourceRef.isBlank()) {
			return;
		}

		String canonicalSourceRef = normalizeTableRef(resolvedSourceRef);
		for (ClauseRefLocation location : locations) {
			if (location == null || location.clauseList == null || location.index < 0 || location.index >= location.clauseList.size()) {
				continue;
			}

			Object currentRefObj = location.clauseList.get(location.index);
			Object updatedRefObj = cloneReferenceWithResolvedTableRef(currentRefObj, canonicalSourceRef);
			location.clauseList.set(location.index, updatedRefObj);
		}
	}

	/**
	 * Phase 8: single decision tree for binding one qualified column reference against visible
	 * scope (query-backed aliases, physical tables, CTE/query interface proof).
	 */
	@SuppressWarnings("unchecked")
	private QualifiedScopeResolutionResult resolveQualifiedColumnAgainstVisibleScope(
			String tableRef,
			String columnName,
			HashMap<String, Object> visibleAliasMap,
			HashMap<String, Object> visibleTableCollection,
			HashMap<String, Object> visibleQuerySourceCollection,
			boolean deferUnresolvedPhysicalSources) {
		return resolveQualifiedColumnAgainstVisibleScope(
				tableRef,
				columnName,
				visibleAliasMap,
				visibleTableCollection,
				visibleQuerySourceCollection,
				deferUnresolvedPhysicalSources,
				null,
				null);
	}

	private QualifiedScopeResolutionResult resolveQualifiedColumnAgainstVisibleScope(
			String tableRef,
			String columnName,
			HashMap<String, Object> visibleAliasMap,
			HashMap<String, Object> visibleTableCollection,
			HashMap<String, Object> visibleQuerySourceCollection,
			boolean deferUnresolvedPhysicalSources,
			HashMap<String, Object> localDerivedColumns,
			ArrayList<Object> relationalModifierInterfaceHints) {
		if (tableRef == null || tableRef.isBlank()
				|| columnName == null || columnName.isBlank()) {
			return QualifiedScopeResolutionResult.unresolvedPhysicalSource(null, tableRef);
		}

		if (isNonTupleSubstitutionReference(tableRef)) {
			return QualifiedScopeResolutionResult.unresolvedPhysicalSource(null, tableRef);
		}

		if (isRelationalModifierDerivedColumnReference(
				localDerivedColumns,
				relationalModifierInterfaceHints,
				tableRef,
				columnName,
				visibleAliasMap)) {
			return QualifiedScopeResolutionResult.resolvedDerivedColumn(tableRef);
		}

		String resolvedTableRef = walker.resolveAliasToTableName(tableRef, visibleAliasMap);
		String resolvedNonTableSourceRef = resolveAliasToQuerySourceRefPreferDefinition(
				tableRef,
				visibleAliasMap,
				visibleQuerySourceCollection);
		boolean explicitQueryReference = resolvedNonTableSourceRef != null
				|| walker.isNonTableQuerySourceReference(resolvedTableRef);

		if (explicitQueryReference) {
			String querySourceRef = (resolvedNonTableSourceRef != null)
					? resolvedNonTableSourceRef
					: resolvedTableRef;
			if ("*".equals(columnName)) {
				return QualifiedScopeResolutionResult.resolvedWildcardQuerySource(
						querySourceRef,
						tableRef);
			}
			if (querySourceExportsColumn(querySourceRef, columnName)) {
				return QualifiedScopeResolutionResult.resolvedQuerySource(
						querySourceRef,
						tableRef);
			}
			return QualifiedScopeResolutionResult.unresolvedQuerySource(tableRef);
		}

		String normalizedPhysicalRef = (resolvedTableRef == null || resolvedTableRef.isBlank())
				? normalizeTableRef(tableRef)
				: normalizeTableRef(resolvedTableRef);
		HashMap<String, Object> indicatedTableDictionary = walker.getTableDictionaryForReference(
				normalizedPhysicalRef,
				visibleTableCollection);
		if (indicatedTableDictionary != null) {
			return QualifiedScopeResolutionResult.resolvedPhysicalSource(
					normalizedPhysicalRef,
					tableRef);
		}

		if (deferUnresolvedPhysicalSources) {
			return QualifiedScopeResolutionResult.deferred(tableRef);
		}
		return QualifiedScopeResolutionResult.unresolvedPhysicalSource(
				normalizedPhysicalRef,
				tableRef);
	}

	@SuppressWarnings("unchecked")
	private boolean querySourceExportsColumn(String querySourceRef, String columnName) {
		if (querySourceRef == null || querySourceRef.isBlank()
				|| columnName == null || columnName.isBlank()) {
			return false;
		}

		Object queryDictionaryObj = getQuerySourceDictionaryPreferDefinition(querySourceRef);
		if (queryDictionaryObj instanceof Map<?, ?> queryDictionary) {
			if (containsKeyIgnoreCase((Map<String, Object>) queryDictionary, columnName)
					|| ((Map<String, Object>) queryDictionary).containsKey("*")) {
				return true;
			}
		}
		if (hasColumnInQueryOutputInterface(querySourceRef, columnName)) {
			return true;
		}
		return hasWildcardInQueryOutputInterface(querySourceRef);
	}

	/**
	 * Materialize a resolved query-backed qualified reference into the global source query
	 * dictionary (and CTE physical tables when applicable).
	 */
	private void materializeResolvedQualifiedQuerySourceReference(
			String tableRef,
			String columnName,
			String querySourceRef,
			Object fallbackEntryValue,
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> visibleAliasMap,
			boolean consumeFromUnresolvedMap) {
		if (querySourceRef == null || querySourceRef.isBlank()
				|| columnName == null || columnName.isBlank()) {
			return;
		}

		Object qualifiedUnknownEntry = null;
		if (consumeFromUnresolvedMap && unresolvedColumnMap != null) {
			qualifiedUnknownEntry = consumeQualifiedUnknownEntry(
					unresolvedColumnMap,
					tableRef,
					columnName);
			if (qualifiedUnknownEntry == null) {
				qualifiedUnknownEntry = consumeUnqualifiedUnknownEntry(unresolvedColumnMap, columnName);
			} else {
				// Clear map-shaped fallback entry inserted by deferred clause collection.
				consumeUnqualifiedUnknownEntry(unresolvedColumnMap, columnName);
			}
		}
		if (qualifiedUnknownEntry == null) {
			qualifiedUnknownEntry = fallbackEntryValue;
		}
		if (qualifiedUnknownEntry == null) {
			return;
		}

		mergeExplicitQualifiedUnknownIntoSourceQueryDictionary(
				querySourceRef,
				columnName,
				qualifiedUnknownEntry);
	}

	private void emitQualifiedPhysicalColumnNotFoundFatal(
			String columnName,
			String tableRef,
			String resolvedPhysicalTableRef,
			Integer[] refLocation) {
		String unresolvedQualifiedKey = (tableRef == null || tableRef.isBlank())
				? null
				: tableRef + "." + columnName;
		emitQualifiedSourceNotFoundFatalForUnresolvedKey(
				unresolvedQualifiedKey,
				null,
				tableRef,
				columnName,
				resolvedPhysicalTableRef,
				refLocation,
				null,
				null);
	}

	/**
	 * Emit a qualified not-found fatal at scope exit, choosing query-alias vs physical-table
	 * diagnostic shape from the unified resolver outcome.
	 */
	private void emitQualifiedScopeExitFatalForUnresolvedKey(
			String unresolvedKey,
			Object unresolvedValue,
			HashMap<String, Object> visibleAliasMap,
			HashMap<String, Object> visibleTableCollection,
			HashMap<String, Object> visibleQuerySourceCollection,
			HashMap<String, Object> locationFallbackMap) {
		if (unresolvedKey == null || unresolvedKey.isBlank() || !unresolvedKey.contains(".")) {
			return;
		}
		if (walker.hasEmittedQualifiedSourceNotFoundFatal(unresolvedKey)) {
			return;
		}

		int dotIndex = unresolvedKey.indexOf('.');
		String tableRef = unresolvedKey.substring(0, dotIndex);
		String columnName = unresolvedKey.substring(dotIndex + 1);

		QualifiedScopeResolutionResult resolutionResult =
				resolveQualifiedColumnAgainstVisibleScope(
						tableRef,
						columnName,
						visibleAliasMap,
						visibleTableCollection,
						visibleQuerySourceCollection,
						false,
						getScopeDerivedColumnsFromSymbolTable(),
						getScopeRelationalModifierHintsFromSymbolTable());
		if (resolutionResult.status == QualifiedScopeResolutionStatus.RESOLVED_DERIVED_COLUMN) {
			return;
		}
		if (resolutionResult.status == QualifiedScopeResolutionStatus.UNRESOLVED_QUERY_SOURCE) {
			String querySourceRef = resolutionResult.querySourceRef;
			if (querySourceRef == null || querySourceRef.isBlank()) {
				querySourceRef = resolveAliasToQuerySourceRefPreferDefinition(
						tableRef,
						visibleAliasMap,
						visibleQuerySourceCollection);
			}
			String resolvedTableRef = walker.resolveAliasToTableName(tableRef, visibleAliasMap);
			String allLocationsForEntry = walker.formatAllLocationsForEntry(unresolvedValue);
			boolean hasMergedLocations = allLocationsForEntry != null
					&& allLocationsForEntry.startsWith("[")
					&& allLocationsForEntry.contains(",");
			String locationsSuffix = hasMergedLocations
					? " Locations: " + allLocationsForEntry
					: null;
			emitQualifiedQueryAliasColumnNotFoundFatal(
					columnName,
					tableRef,
					querySourceRef,
					resolvedTableRef,
					unresolvedValue,
					locationsSuffix);
			walker.markEmittedQualifiedSourceNotFoundFatal(unresolvedKey);
			return;
		}

		Object capturedValue = walker.getCapturedQualifiedUnresolvedLocationEntry(unresolvedKey);
		Object diagnosticValue = (capturedValue != null) ? capturedValue : unresolvedValue;
		Integer[] refLocation = walker.getLineAndCharacterFromEntry(diagnosticValue);
		if (refLocation == null || refLocation.length < 2 || refLocation[0] == null) {
			refLocation = walker.getFirstEntryLineAndCharacter(locationFallbackMap);
		}
		emitQualifiedSourceNotFoundFatalForUnresolvedKey(
				unresolvedKey,
				diagnosticValue,
				tableRef,
				columnName,
				resolutionResult.resolvedPhysicalTableRef,
				refLocation,
				locationFallbackMap,
				walker.formatAllLocationsForEntryInline(diagnosticValue));
	}

	private void emitQualifiedSourceNotFoundFatalForUnresolvedKey(
			String unresolvedKey,
			Object diagnosticValue,
			String tableRef,
			String columnName,
			String resolvedPhysicalTableRef,
			Integer[] refLocation,
			HashMap<String, Object> locationFallbackMap,
			String allLocationsInline) {
		if (unresolvedKey != null && walker.hasEmittedQualifiedSourceNotFoundFatal(unresolvedKey)) {
			return;
		}
		if (columnName == null || columnName.isBlank()) {
			return;
		}

		if (refLocation == null || refLocation.length < 2 || refLocation[0] == null) {
			refLocation = (locationFallbackMap != null)
					? walker.getFirstEntryLineAndCharacter(locationFallbackMap)
					: new Integer[] { null, null };
		}

		String diagCode = walker.getDiagnosticCode(
				SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE);
		String diagTemplate = walker.getDiagnosticMessage(
				SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE);
		String indicatedSourceRef = (tableRef != null && !tableRef.isBlank())
				? tableRef
				: resolvedPhysicalTableRef;
		boolean hasMergedLocations = allLocationsInline != null && allLocationsInline.contains(",");
		String diagMessage;
		if (hasMergedLocations) {
			diagMessage = String.format(
					"Source Table not found for Column '%s' at %s. No alias or table called '%s'.",
					columnName,
					allLocationsInline,
					indicatedSourceRef);
		} else {
			diagMessage = (diagTemplate == null)
					? String.format(
							"Source Table not found for Column '%s' at (l:%s c:%s). No alias or table called '%s'.",
							columnName,
							refLocation[0],
							refLocation[1],
							indicatedSourceRef)
					: String.format(
							diagTemplate,
							columnName,
							refLocation[0],
							refLocation[1],
							indicatedSourceRef);
		}

		walker.addWalkerFatal(
				diagCode,
				diagMessage,
				refLocation[0],
				refLocation[1],
				columnName);
		if (unresolvedKey != null && !unresolvedKey.isBlank()) {
			walker.markEmittedQualifiedSourceNotFoundFatal(unresolvedKey);
		}
	}

	private void emitQualifiedQueryAliasColumnNotFoundFatal(
			String columnName,
			String tableRef,
			String querySourceRef,
			String resolvedTableRef,
			Object entryValue,
			String allLocationsSuffix) {
		Integer[] refLocation = walker.getLineAndCharacterFromEntry(entryValue);
		if (refLocation == null || refLocation.length < 2 || refLocation[0] == null) {
			refLocation = new Integer[] { null, null };
		}

		String diagCode = walker.getDiagnosticCode(
				SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS);
		String diagTemplate = walker.getDiagnosticMessage(
				SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS);
		String queryAliasRef = (resolvedTableRef != null && !resolvedTableRef.equals(tableRef))
				? tableRef
				: querySourceRef;
		String diagMessage = (diagTemplate == null)
				? String.format(
						"Qualified column '%s' at (l:%s c:%s) was not found in output interface of query alias '%s'.",
						columnName,
						refLocation[0],
						refLocation[1],
						queryAliasRef)
				: String.format(
						diagTemplate,
						columnName,
						refLocation[0],
						refLocation[1],
						queryAliasRef);
		if (allLocationsSuffix != null && !allLocationsSuffix.isBlank()) {
			diagMessage = diagMessage + allLocationsSuffix;
		}

		String unresolvedKey = tableRef + "." + columnName;
		walker.addWalkerFatal(
				diagCode,
				diagMessage,
				refLocation[0],
				refLocation[1],
				unresolvedKey);
	}

	/**
	 * Phase 8: single decision tree for binding one unqualified column name against visible
	 * scope sources (physical tables, query-backed aliases, optional query-dictionary fallback).
	 */
	private UnqualifiedScopeResolutionResult resolveUnqualifiedColumnAgainstVisibleScope(
			String columnName,
			HashMap<String, Object> localPhysicalTableCollection,
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> visibleQuerySourceCollection,
			HashMap<String, Object> localTableAliasMap,
			String deleteTargetTableRef,
			boolean allowQuerySourceFallback,
			boolean deferWhenQueryAliasOnlyWithoutParentFatal,
			ArrayList<Object> relationalModifierInterfaceHints) {
		ArrayList<String> sourceRefs = collectUnqualifiedSourceReferences(
				columnName,
				localPhysicalTableCollection,
				allowQuerySourceFallback ? visibleQuerySourceCollection : null,
				localTableAliasMap);

		String preferredDeleteTargetRef = resolvePreferredDeleteTargetForUnqualified(
				deleteTargetTableRef,
				localTableAliasMap,
				localTableCollection,
				sourceRefs);
		if (preferredDeleteTargetRef != null) {
			return UnqualifiedScopeResolutionResult.resolved(normalizeTableRef(preferredDeleteTargetRef));
		}

		if (sourceRefs.isEmpty()) {
			if (allowQuerySourceFallback) {
				LinkedHashSet<String> querySourcesWithColumn = collectQuerySourcesWithColumn(
						columnName,
						visibleQuerySourceCollection,
						localTableAliasMap);
				if (querySourcesWithColumn.size() == 1) {
					return UnqualifiedScopeResolutionResult.resolved(
							normalizeTableRef(querySourcesWithColumn.iterator().next()));
				}
				if (querySourcesWithColumn.size() > 1) {
					return UnqualifiedScopeResolutionResult.ambiguous(querySourcesWithColumn.toString());
				}
			}
			if (hasOnlyQueryBackedAliasSources(localTableAliasMap)
					&& !hasLocalPhysicalFromTables(localPhysicalTableCollection)) {
				if (deferWhenQueryAliasOnlyWithoutParentFatal) {
					return UnqualifiedScopeResolutionResult.deferred();
				}
				return UnqualifiedScopeResolutionResult.unresolved();
			}
			return UnqualifiedScopeResolutionResult.unresolved();
		}

		if (sourceRefs.size() > 1) {
			String unpivotGeneratedSourceRef = resolveUnpivotGeneratedColumnSourceRef(
					columnName,
					relationalModifierInterfaceHints);
			if (unpivotGeneratedSourceRef != null && !unpivotGeneratedSourceRef.isBlank()) {
				return UnqualifiedScopeResolutionResult.resolved(normalizeTableRef(unpivotGeneratedSourceRef));
			}
			return UnqualifiedScopeResolutionResult.ambiguous(sourceRefs.toString());
		}

		return UnqualifiedScopeResolutionResult.resolved(normalizeTableRef(sourceRefs.get(0)));
	}

	private LinkedHashSet<String> collectQuerySourcesWithColumn(
			String columnName,
			HashMap<String, Object> visibleQuerySourceCollection,
			HashMap<String, Object> localTableAliasMap) {
		LinkedHashSet<String> querySourcesWithColumn = new LinkedHashSet<String>();
		LinkedHashSet<String> checkedQuerySources = new LinkedHashSet<String>();

		if (visibleQuerySourceCollection != null && !visibleQuerySourceCollection.isEmpty()) {
			for (String queryRef : visibleQuerySourceCollection.keySet()) {
				String canonicalQueryRef = normalizeQuerySourceReference(queryRef);
				if (canonicalQueryRef == null || canonicalQueryRef.isBlank()) {
					continue;
				}
				checkedQuerySources.add(canonicalQueryRef);
				if (querySourceHasExactColumn(queryRef, columnName, null)) {
					addIgnoringCase(querySourcesWithColumn, canonicalQueryRef);
				}
			}
		}

		if (querySourcesWithColumn.isEmpty()
				&& walker.queryColumnDictionaryMap != null
				&& !hasOnlyQueryBackedAliasSources(localTableAliasMap)) {
			for (String queryRef : walker.queryColumnDictionaryMap.keySet()) {
				String canonicalQueryRef = normalizeQuerySourceReference(queryRef);
				if (canonicalQueryRef == null || canonicalQueryRef.isBlank()) {
					continue;
				}
				if (checkedQuerySources.contains(canonicalQueryRef)) {
					continue;
				}
				checkedQuerySources.add(canonicalQueryRef);
				if (querySourceHasExactColumn(queryRef, columnName, null)) {
					addIgnoringCase(querySourcesWithColumn, canonicalQueryRef);
				}
			}
		}

		return querySourcesWithColumn;
	}

	private void emitAmbiguousUnqualifiedColumnDiagnostic(
			String columnName,
			Integer[] refLocation,
			String possibleSources) {
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
	}

	/**
	 * Apply a scope-exit unqualified resolution result: update tracked clause refs and
	 * materialize tokens according to deferred vs immediate scope policy.
	 */
	private void applyUnqualifiedScopeResolutionResult(
			UnqualifiedScopeResolutionResult result,
			String columnName,
			Object unresolvedEntry,
			Integer[] refLocation,
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> localTableAliasMap,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> localInterface,
			HashMap<String, Object> visibleQuerySourceCollection,
			String clauseKey,
			Set<ClauseRefLocation> clauseLocations,
			boolean deferCorrelatedValueSubqueryQualifiedUnknowns,
			boolean materializeWhenImmediateScope,
			boolean suppressAmbiguousDiagnostic) {
		if (result == null) {
			return;
		}

		switch (result.status) {
			case RESOLVED -> {
				if (clauseLocations != null && !clauseLocations.isEmpty()) {
					updateTrackedClauseLocationsWithResolvedTableRef(
							clauseLocations,
							result.resolvedSourceRef);
				}
				if (deferCorrelatedValueSubqueryQualifiedUnknowns) {
					materializeInterfaceUnqualifiedReferenceIfDeferredScope(
							true,
							unresolvedColumnMap,
							localTableCollection,
							localTableAliasMap,
							localCurrentQueryDictionary,
							localInterface,
							visibleQuerySourceCollection,
							clauseKey,
							result.resolvedSourceRef,
							columnName);
				} else if (materializeWhenImmediateScope) {
					materializeResolvedUnqualifiedReference(
							unresolvedColumnMap,
							localTableCollection,
							localTableAliasMap,
							localCurrentQueryDictionary,
							localInterface,
							visibleQuerySourceCollection,
							clauseKey,
							result.resolvedSourceRef,
							columnName);
				}
			}
			case AMBIGUOUS -> {
				if (suppressAmbiguousDiagnostic) {
					return;
				}
				Integer[] diagnosticLocation = refLocation;
				if (diagnosticLocation == null || diagnosticLocation.length < 2 || diagnosticLocation[0] == null) {
					diagnosticLocation = walker.getLineAndCharacterFromEntry(unresolvedEntry);
				}
				if (diagnosticLocation == null || diagnosticLocation.length < 2 || diagnosticLocation[0] == null) {
					diagnosticLocation = new Integer[] { null, null };
				}
				emitAmbiguousUnqualifiedColumnDiagnostic(
						columnName,
						diagnosticLocation,
						result.ambiguousSourcesLabel);
			}
			case UNRESOLVED -> {
				if (!hasOnlyQueryBackedAliasSources(localTableAliasMap)
						|| hasLocalPhysicalFromTables(
								buildLocalPhysicalFromTableCollection(localTableCollection))) {
					return;
				}
				Integer[] diagnosticLocation = refLocation;
				if (diagnosticLocation == null || diagnosticLocation.length < 2 || diagnosticLocation[0] == null) {
					diagnosticLocation = walker.getLineAndCharacterFromEntry(unresolvedEntry);
				}
				if (diagnosticLocation == null || diagnosticLocation.length < 2 || diagnosticLocation[0] == null) {
					diagnosticLocation = new Integer[] { null, null };
				}
				emitUnqualifiedNotFoundInQueryAliasFatal(
						columnName,
						diagnosticLocation,
						localTableAliasMap);
			}
			default -> {
			}
		}
	}

	/**
	 * Resolve remaining unqualified, unresolved entries against visible query sources.
	 * Handles columns deferred from nested scopes (e.g., EXISTS predicates) that are merged
	 * back after clause processing. Applies the same resolution strategy as clause columns:
	 * if exactly one query source has the column, bind it to that source.
	 */
	@SuppressWarnings("unchecked")
	private void resolveRemainingUnresolvedAgainstQuerySources(
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Set<ClauseRefLocation>> unresolvedColumnLocations,
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> visibleQuerySourceCollection,
			HashMap<String, Object> localTableAliasMap,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> localInterface,
			String deleteTargetTableRef) {
		// Check if we're in UPDATE with no FROM clause - if so, resolve against UPDATE target first
		String updateTargetTableRef = (String) walker.symbolTable.get(TEMP_UPDATE_NODEFROM_TARGET_KEY);
		HashMap<String, Object> updateTargetTableCollection = (HashMap<String, Object>) walker.symbolTable.get(TEMP_UPDATE_NODEFROM_TARGET_TABLE_COLLECTION_KEY);
		
		if (updateTargetTableRef != null && !updateTargetTableRef.isBlank() && updateTargetTableCollection != null) {
			// Resolve UPDATE no-FROM unqualified columns against target table first
			resolveUpdateUnqualifiedUnresolvedColumnsToTargetTableWhenNoInputSources(
					unresolvedColumnMap,
					localTableCollection,
					updateTargetTableCollection,
					localTableAliasMap,
					updateTargetTableRef);
			resolveRemainingQualifiedUnresolvedColumnsToTargetTable(
					unresolvedColumnMap,
					localTableAliasMap,
					updateTargetTableCollection,
					updateTargetTableRef);
			// Clean up temporary storage
			walker.symbolTable.remove(TEMP_UPDATE_NODEFROM_TARGET_KEY);
			walker.symbolTable.remove(TEMP_UPDATE_NODEFROM_TARGET_TABLE_COLLECTION_KEY);
		}
		if (unresolvedColumnMap == null || unresolvedColumnMap.isEmpty()) {
			return;
		}

		// Process each unresolved entry to see if it can be resolved against local or query sources
		for (String unresolvedKey : new ArrayList<String>(unresolvedColumnMap.keySet())) {
			Object unresolvedEntry = unresolvedColumnMap.get(unresolvedKey);
			
			// Extract the column name and table ref from the unresolved entry
			String columnName = extractColumnNameFromUnresolvedEntry(unresolvedKey, unresolvedEntry);
			String tableRef = extractTableRefFromUnresolvedEntry(unresolvedKey, unresolvedEntry);
			
			if (columnName == null || columnName.isBlank() || tableRef != null) {
				// Skip if qualified (has explicit table ref) or if column name couldn't be extracted
				continue;
			}

			HashMap<String, Object> localPhysicalTableCollection =
					buildLocalPhysicalFromTableCollection(localTableCollection);
			Set<ClauseRefLocation> clauseLocations = (unresolvedColumnLocations != null)
					? unresolvedColumnLocations.get(columnName)
					: null;

			UnqualifiedScopeResolutionResult resolutionResult =
					resolveUnqualifiedColumnAgainstVisibleScope(
							columnName,
							localPhysicalTableCollection,
							localTableCollection,
							visibleQuerySourceCollection,
							localTableAliasMap,
							deleteTargetTableRef,
							true,
							false,
							null);

			Integer[] refLocation = resolveUnqualifiedReferenceLocation(
					columnName,
					unresolvedEntry,
					unresolvedColumnMap,
					localCurrentQueryDictionary,
					columnName);

			applyUnqualifiedScopeResolutionResult(
					resolutionResult,
					columnName,
					unresolvedEntry,
					refLocation,
					unresolvedColumnMap,
					localTableCollection,
					localTableAliasMap,
					localCurrentQueryDictionary,
					localInterface,
					visibleQuerySourceCollection,
					null,
					clauseLocations,
					false,
					true,
					false);
		}
	}

	/**
	 * Phase 9: single decision tree for one archived clause-list column reference at scope exit.
	 * Filters allow physical-table dictionary proof; GROUP BY / ORDER BY require query-output proof.
	 * Qualified query-alias refs are skipped (unified qualified egress owns them).
	 */
	private enum ArchivedClauseColumnRefDisposition {
		SKIP,
		RESOLVED,
		DEFERRED,
		AMBIGUOUS,
		UNRESOLVED
	}

	private static final class ArchivedClauseColumnRefResult {
		final ArchivedClauseColumnRefDisposition disposition;
		final String resolvedSourceRef;
		final String ambiguousSourcesLabel;

		private ArchivedClauseColumnRefResult(
				ArchivedClauseColumnRefDisposition disposition,
				String resolvedSourceRef,
				String ambiguousSourcesLabel) {
			this.disposition = disposition;
			this.resolvedSourceRef = resolvedSourceRef;
			this.ambiguousSourcesLabel = ambiguousSourcesLabel;
		}

		static ArchivedClauseColumnRefResult skip() {
			return new ArchivedClauseColumnRefResult(ArchivedClauseColumnRefDisposition.SKIP, null, null);
		}

		static ArchivedClauseColumnRefResult resolved(String resolvedSourceRef) {
			return new ArchivedClauseColumnRefResult(
					ArchivedClauseColumnRefDisposition.RESOLVED,
					resolvedSourceRef,
					null);
		}

		static ArchivedClauseColumnRefResult deferred() {
			return new ArchivedClauseColumnRefResult(ArchivedClauseColumnRefDisposition.DEFERRED, null, null);
		}

		static ArchivedClauseColumnRefResult ambiguous(String ambiguousSourcesLabel) {
			return new ArchivedClauseColumnRefResult(
					ArchivedClauseColumnRefDisposition.AMBIGUOUS,
					null,
					ambiguousSourcesLabel);
		}

		static ArchivedClauseColumnRefResult unresolved() {
			return new ArchivedClauseColumnRefResult(ArchivedClauseColumnRefDisposition.UNRESOLVED, null, null);
		}
	}

	/** Carries visible-scope inputs for {@link #probeArchivedScopeClauseColumns}. */
	@SuppressWarnings("unchecked")
	private static final class ArchivedClauseProbeContext {
		final HashMap<String, Object> scopeSymbols;
		final HashMap<String, Object> localInterface;
		final HashMap<String, Object> localCurrentQueryDictionary;
		final HashMap<String, Object> localUnresolvedColumnMap;
		final HashMap<String, Object> localFromTableCollection;
		final HashMap<String, Object> localTableCollection;
		final HashMap<String, Object> visibleQuerySourceCollection;
		final HashMap<String, Object> effectiveAliasMap;
		final HashMap<String, Object> effectiveTableCollection;
		final HashMap<String, Object> localTableAliasMap;
		final HashMap<String, Object> localDerivedColumns;
		final ArrayList<Object> relationalModifierInterfaceHints;
		final String deleteTargetTableRef;
		final boolean deferCorrelatedSubqueries;

		ArchivedClauseProbeContext(
				HashMap<String, Object> scopeSymbols,
				HashMap<String, Object> localInterface,
				HashMap<String, Object> localCurrentQueryDictionary,
				HashMap<String, Object> localUnresolvedColumnMap,
				HashMap<String, Object> localFromTableCollection,
				HashMap<String, Object> localTableCollection,
				HashMap<String, Object> visibleQuerySourceCollection,
				HashMap<String, Object> effectiveAliasMap,
				HashMap<String, Object> effectiveTableCollection,
				HashMap<String, Object> localTableAliasMap,
				HashMap<String, Object> localDerivedColumns,
				ArrayList<Object> relationalModifierInterfaceHints,
				String deleteTargetTableRef,
				boolean deferCorrelatedSubqueries) {
			this.scopeSymbols = scopeSymbols;
			this.localInterface = localInterface;
			this.localCurrentQueryDictionary = localCurrentQueryDictionary;
			this.localUnresolvedColumnMap = localUnresolvedColumnMap;
			this.localFromTableCollection = localFromTableCollection;
			this.localTableCollection = localTableCollection;
			this.visibleQuerySourceCollection = visibleQuerySourceCollection;
			this.effectiveAliasMap = effectiveAliasMap;
			this.effectiveTableCollection = effectiveTableCollection;
			this.localTableAliasMap = localTableAliasMap;
			this.localDerivedColumns = localDerivedColumns;
			this.relationalModifierInterfaceHints = relationalModifierInterfaceHints;
			this.deleteTargetTableRef = deleteTargetTableRef;
			this.deferCorrelatedSubqueries = deferCorrelatedSubqueries;
		}
	}

	private static boolean isGroupOrOrderClauseKey(String clauseKey) {
		return MUMBLE_GROUPED_BY_KEY.equals(clauseKey) || MUMBLE_ORDERED_BY_KEY.equals(clauseKey);
	}

	/**
	 * Relocate unqualified physical refs to a sole FROM table, but keep interface output-only
	 * alias names in {@code unresolved_column} for clause probe / query-dictionary capture.
	 */
	@SuppressWarnings("unchecked")
	private void relocateUnqualifiedToSingleTableExcludingOutputAliases(
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> localInterface) {
		if (unresolvedColumnMap == null || unresolvedColumnMap.isEmpty()) {
			return;
		}
		HashMap<String, Object> deferredOutputAliasUnknowns =
				deferInterfaceOutputAliasOnlyUnqualifiedEntries(unresolvedColumnMap, localInterface);
		walker.moveEntriesToSingleTableIfSingleTarget(unresolvedColumnMap, tableCollection);
		if (!deferredOutputAliasUnknowns.isEmpty()) {
			walker.mergeUnknownEntries(unresolvedColumnMap, deferredOutputAliasUnknowns);
		}
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> deferInterfaceOutputAliasOnlyUnqualifiedEntries(
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> localInterface) {
		HashMap<String, Object> deferred = new HashMap<String, Object>();
		if (unresolvedColumnMap == null
				|| unresolvedColumnMap.isEmpty()
				|| localInterface == null
				|| localInterface.isEmpty()) {
			return deferred;
		}
		for (String key : new ArrayList<String>(unresolvedColumnMap.keySet())) {
			if (key != null && key.contains(".")) {
				continue;
			}
			Object entry = unresolvedColumnMap.get(key);
			String columnName = extractColumnNameFromUnresolvedEntry(key, entry);
			if (isInterfaceOutputAliasOnly(localInterface, columnName)) {
				deferred.put(key, unresolvedColumnMap.remove(key));
			}
		}
		return deferred;
	}

	private static boolean isUnqualifiedColumnRef(String tableRef) {
		return tableRef == null || tableRef.isBlank() || MUMBLE_UNKNOWN_KEY.equals(tableRef);
	}

	private boolean isInterfaceOutputColumnName(
			HashMap<String, Object> localInterface,
			String columnName) {
		return columnName != null
				&& !columnName.isBlank()
				&& containsKeyIgnoreCase(localInterface, columnName);
	}

	private boolean isQualifiedPhysicalSourceRef(
			String tableRef,
			HashMap<String, Object> effectiveAliasMap,
			HashMap<String, Object> visibleQuerySourceCollection) {
		if (isUnqualifiedColumnRef(tableRef)) {
			return false;
		}
		String resolvedQuerySourceRef = resolveAliasToQuerySourceRefPreferDefinition(
				tableRef,
				effectiveAliasMap,
				visibleQuerySourceCollection);
		if (resolvedQuerySourceRef != null && !resolvedQuerySourceRef.isBlank()) {
			return false;
		}
		String resolvedTableRef = walker.resolveAliasToTableName(tableRef, effectiveAliasMap);
		if (resolvedTableRef == null || resolvedTableRef.isBlank()) {
			resolvedTableRef = tableRef;
		}
		return !isQueryBackedSourceRef(resolvedTableRef);
	}

	private boolean isInterfaceOutputAliasOnly(
			HashMap<String, Object> localInterface,
			String outputColumnName) {
		if (!isInterfaceOutputColumnName(localInterface, outputColumnName)) {
			return false;
		}
		String matchedKey = findKeyIgnoreCase(localInterface, outputColumnName);
		if (matchedKey == null) {
			return true;
		}
		Object refsObj = localInterface.get(matchedKey);
		if (!(refsObj instanceof ArrayList<?> refs)) {
			return true;
		}
		for (Object refObj : refs) {
			String sourceColumnName = walker.extractReferenceNameFromInterfaceEntry(refObj);
			if (sourceColumnName != null
					&& sourceColumnName.equalsIgnoreCase(outputColumnName)) {
				return false;
			}
		}
		return true;
	}

	private boolean isIntraQueryOutputAliasUsage(
			String columnName,
			String tableRef,
			HashMap<String, Object> localInterface,
			HashMap<String, Object> tableCollection) {
		if (!isUnqualifiedColumnRef(tableRef)) {
			return false;
		}
		return isInterfaceOutputAliasOnly(localInterface, columnName);
	}

	private boolean isIntraQueryOutputClauseUsage(
			String clauseKey,
			String columnName,
			String tableRef,
			HashMap<String, Object> localInterface,
			HashMap<String, Object> effectiveAliasMap,
			HashMap<String, Object> visibleQuerySourceCollection,
			HashMap<String, Object> tableCollection) {
		if (isQualifiedPhysicalSourceRef(tableRef, effectiveAliasMap, visibleQuerySourceCollection)) {
			return false;
		}
		if (isIntraQueryOutputAliasUsage(columnName, tableRef, localInterface, tableCollection)) {
			return true;
		}
		return isGroupOrOrderClauseKey(clauseKey)
				&& isUnqualifiedColumnRef(tableRef)
				&& isInterfaceOutputColumnName(localInterface, columnName);
	}

	private boolean isQueryOutputColumnProofForClause(
			String columnName,
			String tableRef,
			String resolvedSourceRef,
			String clauseKey,
			HashMap<String, Object> localInterface,
			HashMap<String, Object> effectiveAliasMap,
			HashMap<String, Object> visibleQuerySourceCollection,
			HashMap<String, Object> tableCollection) {
		if (columnName == null || columnName.isBlank()) {
			return false;
		}
		if (!isUnqualifiedColumnRef(tableRef)
				&& isQualifiedPhysicalSourceRef(tableRef, effectiveAliasMap, visibleQuerySourceCollection)) {
			return false;
		}
		if (isIntraQueryOutputClauseUsage(
				clauseKey,
				columnName,
				tableRef,
				localInterface,
				effectiveAliasMap,
				visibleQuerySourceCollection,
				tableCollection)) {
			return true;
		}
		String sourceRef = (resolvedSourceRef != null && !resolvedSourceRef.isBlank())
				? resolvedSourceRef
				: tableRef;
		return isQueryBackedSourceRef(sourceRef)
				&& querySourceExportsColumn(sourceRef, columnName);
	}

	private boolean isQueryBackedSourceRef(String sourceRef) {
		return sourceRef != null
				&& (isQuerySourceReference(sourceRef)
						|| walker.isNonTableQuerySourceReference(sourceRef)
						|| isTableFunctionSourceReference(sourceRef));
	}

	@SuppressWarnings("unchecked")
	private ArchivedClauseColumnRefResult validateArchivedClauseColumnRef(
			Object refObj,
			String clauseKey,
			HashMap<String, Object> localInterface,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> localUnresolvedColumnMap,
			HashMap<String, Object> localFromTableCollection,
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> visibleQuerySourceCollection,
			HashMap<String, Object> effectiveAliasMap,
			HashMap<String, Object> effectiveTableCollection,
			HashMap<String, Object> localTableAliasMap,
			HashMap<String, Object> localDerivedColumns,
			ArrayList<Object> relationalModifierInterfaceHints,
			String deleteTargetTableRef,
			boolean deferCorrelatedValueSubqueryQualifiedUnknowns) {
		String columnName = walker.extractReferenceNameFromInterfaceEntry(refObj);
		String tableRef = walker.extractReferenceTableRefFromInterfaceEntry(refObj);
		String substitutionType = walker.extractSubstitutionTypeFromInterfaceEntry(refObj);

		if (substitutionType != null
				&& (MUMBLE_COLUMN_KEY.equals(substitutionType) || MUMBLE_PREDICAND_KEY.equals(substitutionType))) {
			return ArchivedClauseColumnRefResult.skip();
		}
		if (columnName == null || columnName.isBlank() || "*".equals(columnName)) {
			return ArchivedClauseColumnRefResult.skip();
		}
		if (isRelationalModifierDerivedColumnReference(
				localDerivedColumns,
				relationalModifierInterfaceHints,
				tableRef,
				columnName)) {
			return ArchivedClauseColumnRefResult.skip();
		}

		boolean requiresOutputColumnProof = isGroupOrOrderClauseKey(clauseKey);

		if (tableRef != null && !tableRef.isBlank() && !"*".equals(tableRef)) {
			String resolvedTableRef = walker.resolveAliasToTableName(tableRef, effectiveAliasMap);
			String resolvedNonTableSourceRef = resolveAliasToQuerySourceRefPreferDefinition(
					tableRef,
					effectiveAliasMap,
					visibleQuerySourceCollection);
			boolean explicitQueryReference = resolvedNonTableSourceRef != null
					|| walker.isNonTableQuerySourceReference(resolvedTableRef);

			if (explicitQueryReference) {
				return ArchivedClauseColumnRefResult.skip();
			}

			HashMap<String, Object> indicatedTableDictionary = walker.getTableDictionaryForReference(
					resolvedTableRef,
					effectiveTableCollection);
			if (indicatedTableDictionary == null
					|| !containsKeyIgnoreCase(indicatedTableDictionary, columnName)) {
				return ArchivedClauseColumnRefResult.unresolved();
			}
			String normalizedPhysicalRef = normalizeTableRef(
					(resolvedTableRef == null || resolvedTableRef.isBlank()) ? tableRef : resolvedTableRef);
			if (requiresOutputColumnProof
					&& !isQueryOutputColumnProofForClause(
							columnName,
							tableRef,
							normalizedPhysicalRef,
							clauseKey,
							localInterface,
							effectiveAliasMap,
							visibleQuerySourceCollection,
							effectiveTableCollection)) {
				return ArchivedClauseColumnRefResult.unresolved();
			}
			return ArchivedClauseColumnRefResult.resolved(normalizedPhysicalRef);
		}

		if (deferCorrelatedValueSubqueryQualifiedUnknowns) {
			return ArchivedClauseColumnRefResult.deferred();
		}

		UnqualifiedScopeResolutionResult resolutionResult =
				resolveUnqualifiedColumnAgainstVisibleScope(
						columnName,
						localFromTableCollection,
						localTableCollection,
						visibleQuerySourceCollection,
						localTableAliasMap,
						deleteTargetTableRef,
						true,
						false,
						relationalModifierInterfaceHints);

		switch (resolutionResult.status) {
			case RESOLVED -> {
				if (requiresOutputColumnProof
						&& !isQueryOutputColumnProofForClause(
								columnName,
								tableRef,
								resolutionResult.resolvedSourceRef,
								clauseKey,
								localInterface,
								localTableAliasMap,
								visibleQuerySourceCollection,
								localTableCollection)) {
					return ArchivedClauseColumnRefResult.unresolved();
				}
				return ArchivedClauseColumnRefResult.resolved(resolutionResult.resolvedSourceRef);
			}
			case AMBIGUOUS -> {
				return ArchivedClauseColumnRefResult.ambiguous(resolutionResult.ambiguousSourcesLabel);
			}
			case DEFERRED -> {
				return ArchivedClauseColumnRefResult.deferred();
			}
			default -> {
				return ArchivedClauseColumnRefResult.unresolved();
			}
		}
	}

	/**
	 * Phase 9: validate and bind archived {@code filters} / {@code grouped_by} / {@code ordered_by}
	 * column refs at scope exit. Replaces the retired per-clause
	 * {@code assignTableRefsForColumnReferenceList} pass and separate {@code validateFilterReferences}
	 * pipeline.
	 */
	@SuppressWarnings("unchecked")
	private void probeArchivedScopeClauseColumns(
			Object filtersList,
			Object groupedByList,
			Object orderedByList,
			HashMap<String, Object> localInterface,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> localUnresolvedColumnMap,
			HashMap<String, Object> localFromTableCollection,
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> visibleQuerySourceCollection,
			HashMap<String, Object> effectiveAliasMap,
			HashMap<String, Object> effectiveTableCollection,
			HashMap<String, Object> localTableAliasMap,
			HashMap<String, Object> localDerivedColumns,
			ArrayList<Object> relationalModifierInterfaceHints,
			String deleteTargetTableRef,
			boolean deferCorrelatedValueSubqueryQualifiedUnknowns) {
		HashMap<String, Object> scopeSymbols = new HashMap<String, Object>();
		if (filtersList != null) {
			scopeSymbols.put(MUMBLE_FILTERS_KEY, filtersList);
		}
		if (groupedByList != null) {
			scopeSymbols.put(MUMBLE_GROUPED_BY_KEY, groupedByList);
		}
		if (orderedByList != null) {
			scopeSymbols.put(MUMBLE_ORDERED_BY_KEY, orderedByList);
		}
		ArchivedClauseProbeContext probeContext = new ArchivedClauseProbeContext(
				scopeSymbols,
				localInterface,
				localCurrentQueryDictionary,
				localUnresolvedColumnMap,
				localFromTableCollection,
				localTableCollection,
				visibleQuerySourceCollection,
				effectiveAliasMap,
				effectiveTableCollection,
				localTableAliasMap,
				localDerivedColumns,
				relationalModifierInterfaceHints,
				deleteTargetTableRef,
				deferCorrelatedValueSubqueryQualifiedUnknowns);
		probeArchivedScopeClauseColumns(probeContext);
		probeUpdateAssignmentRhsClauseColumns(probeContext);
	}

	@SuppressWarnings("unchecked")
	private void probeUpdateAssignmentRhsClauseColumns(ArchivedClauseProbeContext probeContext) {
		if (probeContext == null) {
			return;
		}
		Object assignmentsObj = null;
		if (probeContext.scopeSymbols != null) {
			assignmentsObj = probeContext.scopeSymbols.get(MUMBLE_ASSIGNMENTS_KEY);
		}
		if (assignmentsObj == null) {
			assignmentsObj = walker.symbolTable.get(MUMBLE_ASSIGNMENTS_KEY);
		}
		if (!(assignmentsObj instanceof Map<?, ?> assignmentsMapObj)) {
			return;
		}
		for (Object rhsRefsObj : assignmentsMapObj.values()) {
			probeArchivedScopeClauseColumnList(rhsRefsObj, MUMBLE_FILTERS_KEY, probeContext);
		}
	}

	@SuppressWarnings("unchecked")
	private void probeArchivedScopeClauseColumns(ArchivedClauseProbeContext probeContext) {
		if (probeContext == null || probeContext.scopeSymbols == null) {
			return;
		}
		for (String clauseKey : SCOPE_CLAUSE_COLUMN_LIST_KEYS) {
			probeArchivedScopeClauseColumnList(
					probeContext.scopeSymbols.get(clauseKey),
					clauseKey,
					probeContext);
		}
	}

	@SuppressWarnings("unchecked")
	private void probeArchivedScopeClauseColumnList(
			Object columnListObj,
			String clauseKey,
			ArchivedClauseProbeContext probeContext) {
		if (!(columnListObj instanceof ArrayList<?>) || probeContext == null) {
			return;
		}

		ArrayList<Object> columnRefs = (ArrayList<Object>) columnListObj;
		for (int index = 0; index < columnRefs.size(); index++) {
			Object columnRefObj = columnRefs.get(index);
			ArchivedClauseColumnRefResult result = validateArchivedClauseColumnRef(
					columnRefObj,
					clauseKey,
					probeContext.localInterface,
					probeContext.localCurrentQueryDictionary,
					probeContext.localUnresolvedColumnMap,
					probeContext.localFromTableCollection,
					probeContext.localTableCollection,
					probeContext.visibleQuerySourceCollection,
					probeContext.effectiveAliasMap,
					probeContext.effectiveTableCollection,
					probeContext.localTableAliasMap,
					probeContext.localDerivedColumns,
					probeContext.relationalModifierInterfaceHints,
					probeContext.deleteTargetTableRef,
					probeContext.deferCorrelatedSubqueries);
			applyArchivedClauseColumnRefProbeResult(
					columnRefs,
					index,
					columnRefObj,
					result,
					clauseKey,
					probeContext);
		}
	}

	/**
	 * Mirror intra-query output-column usage tokens onto the local query dictionary.
	 * Physical source refs and query-backed source refs are routed elsewhere.
	 */
	private void recordInterfaceOutputClauseRefOnQueryDictionary(
			Object columnRefObj,
			String columnName,
			String clauseKey,
			ArchivedClauseProbeContext probeContext) {
		if (columnRefObj == null || probeContext == null
				|| columnName == null || columnName.isBlank()
				|| probeContext.localCurrentQueryDictionary == null
				|| probeContext.localInterface == null) {
			return;
		}

		String tableRef = walker.extractReferenceTableRefFromInterfaceEntry(columnRefObj);
		if (!isIntraQueryOutputClauseUsage(
				clauseKey,
				columnName,
				tableRef,
				probeContext.localInterface,
				probeContext.effectiveAliasMap,
				probeContext.visibleQuerySourceCollection,
				probeContext.localTableCollection)) {
			return;
		}

		Object refTokens = null;
		if (!isUnqualifiedColumnRef(tableRef)) {
			refTokens = consumeQualifiedUnknownEntry(
					probeContext.localUnresolvedColumnMap,
					tableRef,
					columnName);
		}
		if (refTokens == null) {
			refTokens = getUnqualifiedUnknownEntry(probeContext.localUnresolvedColumnMap, columnName);
		}
		if (refTokens != null) {
			walker.mergeResolvedColumnIntoDictionary(
					probeContext.localCurrentQueryDictionary,
					columnName,
					refTokens);
			if (!isUnqualifiedColumnRef(tableRef)) {
				releaseResolvedQualifiedGlobalLocationIfQualified(tableRef + "." + columnName);
			}
		}
	}

	private void applyArchivedClauseColumnRefProbeResult(
			ArrayList<Object> columnRefs,
			int index,
			Object columnRefObj,
			ArchivedClauseColumnRefResult result,
			String clauseKey,
			ArchivedClauseProbeContext probeContext) {
		if (result == null || probeContext == null) {
			return;
		}

		String columnName = walker.extractReferenceNameFromInterfaceEntry(columnRefObj);
		Integer[] refLocation = resolveUnqualifiedReferenceLocation(
				columnName,
				columnRefObj,
				probeContext.localUnresolvedColumnMap,
				probeContext.localCurrentQueryDictionary,
				columnName);

		switch (result.disposition) {
			case RESOLVED -> {
				if (result.resolvedSourceRef == null || result.resolvedSourceRef.isBlank()) {
					return;
				}
				columnRefs.set(
						index,
						cloneReferenceWithResolvedTableRef(columnRefObj, result.resolvedSourceRef));
				String tableRef = walker.extractReferenceTableRefFromInterfaceEntry(columnRefObj);
				if (isQualifiedPhysicalSourceRef(
						tableRef,
						probeContext.effectiveAliasMap,
						probeContext.visibleQuerySourceCollection)) {
					return;
				}
				if (isArchivedClauseColumnAlreadyMaterialized(
						columnName,
						result.resolvedSourceRef,
						probeContext.localTableCollection)) {
					recordInterfaceOutputClauseRefOnQueryDictionary(
							columnRefObj,
							columnName,
							clauseKey,
							probeContext);
					return;
				}
				applyUnqualifiedScopeResolutionResult(
						UnqualifiedScopeResolutionResult.resolved(result.resolvedSourceRef),
						columnName,
						columnRefObj,
						refLocation,
						probeContext.localUnresolvedColumnMap,
						probeContext.localTableCollection,
						probeContext.localTableAliasMap,
						probeContext.localCurrentQueryDictionary,
						probeContext.localInterface,
						probeContext.visibleQuerySourceCollection,
						clauseKey,
						null,
						probeContext.deferCorrelatedSubqueries,
						!probeContext.deferCorrelatedSubqueries,
						shouldSuppressAmbiguousUnqualifiedDiagnostic(columnName, refLocation));
			}
			case AMBIGUOUS -> {
				if (shouldSuppressAmbiguousUnqualifiedDiagnostic(columnName, refLocation)) {
					return;
				}
				emitAmbiguousUnqualifiedColumnDiagnostic(
						columnName,
						refLocation,
						result.ambiguousSourcesLabel);
			}
			case UNRESOLVED -> {
				String tableRef = walker.extractReferenceTableRefFromInterfaceEntry(columnRefObj);
				if (tableRef != null && !tableRef.isBlank() && !"*".equals(tableRef)) {
					// Qualified refs unresolved in this nested scope are resolved at the
					// enclosing query/CTE finalization — not as unqualified columns here.
					return;
				}
				if (!hasOnlyQueryBackedAliasSources(probeContext.localTableAliasMap)
						|| hasLocalPhysicalFromTables(probeContext.localFromTableCollection)) {
					return;
				}
				emitUnqualifiedNotFoundInQueryAliasFatal(
						columnName,
						refLocation,
						probeContext.localTableAliasMap);
			}
			default -> {
			}
		}
	}

	private boolean isArchivedClauseColumnAlreadyMaterialized(
			String columnName,
			String resolvedSourceRef,
			HashMap<String, Object> localTableCollection) {
		if (columnName == null || columnName.isBlank() || resolvedSourceRef == null || resolvedSourceRef.isBlank()) {
			return false;
		}

		String normalizedSourceRef = normalizeTableRef(resolvedSourceRef);
		HashMap<String, Object> sourceDictionary = walker.getTableDictionaryForReference(
				normalizedSourceRef,
				localTableCollection);
		return sourceDictionary != null && containsKeyIgnoreCase(sourceDictionary, columnName);
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

	/**
	 * Collects every local unqualified-resolution candidate for {@code columnName} in this query
	 * frame. All local physical {@code FROM} tables are included regardless of column dictionary
	 * state. Local query-backed {@code FROM} aliases are added when the subquery has a wildcard
	 * output interface or lists {@code columnName} in its output interface. Inherited
	 * {@code context_list} CTE aliases (e.g. {@code aaa} inside {@code bbb}'s body) are excluded
	 * unless re-registered by this frame's {@code FROM} clause. Queries without {@code WITH} have
	 * no {@code context_list} and never apply this filter. Callers resolve diagnostics from the
	 * returned count: 0 = not found, 1 = bind, {@code >1} = ambiguous.
	 */
	public ArrayList<String> collectUnqualifiedSourceReferences(
			String columnName,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> queryCollection,
			HashMap<String, Object> tableAliasCollection) {
		LinkedHashSet<String> candidates = new LinkedHashSet<String>();

		HashMap<String, Object> localPhysicalTables = buildLocalPhysicalFromTableCollection(tableCollection);
		for (String tableRef : localPhysicalTables.keySet()) {
			addIgnoringCase(candidates, tableRef);
		}

		boolean queryAliasOnlyScope = hasOnlyQueryBackedAliasSources(tableAliasCollection)
				&& !hasLocalPhysicalFromTables(localPhysicalTables);

		if (tableAliasCollection != null && !tableAliasCollection.isEmpty()) {
			for (Map.Entry<String, Object> aliasEntry : tableAliasCollection.entrySet()) {
				String aliasKey = aliasEntry.getKey();
				if (isInheritedCteContextListAlias(aliasKey)
						&& !isLocalFromRegisteredAlias(aliasKey)) {
					continue;
				}
				Object mappedSourceObj = aliasEntry.getValue();
				if (!(mappedSourceObj instanceof String mappedSource) || mappedSource.isBlank()) {
					continue;
				}
				if (!isQuerySourceReference(mappedSource) && !isTableFunctionSourceReference(mappedSource)) {
					continue;
				}
				if (isTableFunctionSourceReference(mappedSource)) {
					addIgnoringCase(candidates, mappedSource);
					continue;
				}
				if (isWildcardBackedQueryCandidate(mappedSource, queryCollection)
						|| querySourceHasExactColumn(mappedSource, columnName, queryCollection)) {
					addIgnoringCase(candidates, mappedSource);
				}
			}
		}

		return new ArrayList<String>(candidates);
	}

	/**
	 * Substitution variables that are not tuple/table row sources (join_extension, column,
	 * predicand, condition, etc.) must never participate in column resolution.
	 */
	private boolean isNonTupleSubstitutionReference(String referenceName) {
		if (referenceName == null || !referenceName.startsWith("<")) {
			return false;
		}
		Object substitutionType = walker.substitutionsMap == null ? null : walker.substitutionsMap.get(referenceName);
		return substitutionType != null && !MUMBLE_TUPLE_KEY.equals(substitutionType.toString());
	}

	/**
	 * Physical tables registered in this query frame's {@code table_dictionary} only.
	 * Inherited CTE {@code context_list} / query aliases are excluded.
	 */
	public HashMap<String, Object> buildLocalPhysicalFromTableCollection(
			HashMap<String, Object> tableCollection) {
		HashMap<String, Object> localPhysical = new HashMap<String, Object>();
		if (tableCollection == null || tableCollection.isEmpty()) {
			return localPhysical;
		}
		for (Map.Entry<String, Object> entry : tableCollection.entrySet()) {
			String tableRef = entry.getKey();
			if (tableRef == null || tableRef.isBlank()) {
				continue;
			}
			if (isNonTupleSubstitutionReference(tableRef)) {
				continue;
			}
			if (isQuerySourceReference(tableRef)
					|| isTableFunctionSourceReference(tableRef)
					|| walker.isNonTableQuerySourceReference(tableRef)) {
				continue;
			}
			localPhysical.put(tableRef, entry.getValue());
		}
		return localPhysical;
	}

	public boolean hasLocalPhysicalFromTables(HashMap<String, Object> localPhysicalTables) {
		return localPhysicalTables != null && !localPhysicalTables.isEmpty();
	}

	/**
	 * True when {@code aliasKey} names a CTE entry in this frame's inherited {@code context_list}
	 * from {@code WITH} scope propagation. Absent or empty {@code context_list} always yields false.
	 */
	private boolean isInheritedCteContextListAlias(String aliasKey) {
		if (aliasKey == null || aliasKey.isBlank()) {
			return false;
		}

		Map<String, Object> contextList = getContextListSymbolMap(walker.symbolTable);
		if (contextList == null || contextList.isEmpty()) {
			return false;
		}

		for (String contextAlias : contextList.keySet()) {
			if (contextAlias != null && contextAlias.equalsIgnoreCase(aliasKey)) {
				return true;
			}
		}
		return false;
	}

	public void addIgnoringCase(Set<String> bucket, String candidate) {
		if (bucket == null || candidate == null || candidate.isBlank()) {
			return;
		}

		String normalizedCandidate = normalizeQuerySourceReference(candidate);
		if (normalizedCandidate == null || normalizedCandidate.isBlank()) {
			normalizedCandidate = candidate;
		}

		for (String existing : bucket) {
			if (existing == null) {
				continue;
			}
			String normalizedExisting = normalizeQuerySourceReference(existing);
			if (normalizedExisting == null || normalizedExisting.isBlank()) {
				normalizedExisting = existing;
			}
			if (normalizedExisting.equalsIgnoreCase(normalizedCandidate)) {
				return;
			}
		}

		bucket.add(normalizedCandidate);
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

			return normalizeQuerySourceReference(mappedSource);
		}

		if (isQuerySourceReference(aliasRef)) {
			return normalizeQuerySourceReference(aliasRef);
		}

		return null;
	}

	public boolean hasOnlyQueryBackedAliasSources(HashMap<String, Object> tableAliasCollection) {
		if (tableAliasCollection == null || tableAliasCollection.isEmpty()) {
			return false;
		}

		int queryAliasCount = 0;
		int localAliasCount = 0;
		for (Map.Entry<String, Object> aliasEntry : tableAliasCollection.entrySet()) {
			String aliasKey = aliasEntry.getKey();
			if (aliasKey == null || !isLocalFromRegisteredAlias(aliasKey)) {
				continue;
			}
			localAliasCount++;
			Object mappedSourceObj = aliasEntry.getValue();
			if (!(mappedSourceObj instanceof String mappedSource) || mappedSource.isBlank()) {
				continue;
			}
			queryAliasCount++;
			if (!isQuerySourceReference(mappedSource)) {
				return false;
			}
		}

		if (localAliasCount > 0) {
			return queryAliasCount >= 1;
		}

		// Fallback: some frames may carry query-backed aliases in table_alias without
		// local FROM registration markers. Treat these as query-alias-only if every
		// alias maps to a query-backed source.
		int fallbackQueryAliasCount = 0;
		for (Map.Entry<String, Object> aliasEntry : tableAliasCollection.entrySet()) {
			String aliasKey = aliasEntry.getKey();
			if (aliasKey == null || aliasKey.isBlank()) {
				continue;
			}
			Object mappedSourceObj = aliasEntry.getValue();
			if (!(mappedSourceObj instanceof String mappedSource) || mappedSource.isBlank()) {
				continue;
			}
			fallbackQueryAliasCount++;
			if (!isQuerySourceReference(mappedSource)) {
				return false;
			}
		}

		return fallbackQueryAliasCount >= 1;
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
		
		// ONLY add sources from the current query's local table alias collection
		// Do NOT look into nested or ancestor symbol tables - those have their own resolution pass
		// This ensures each query only resolves against its direct sources (FROM/JOIN)
		if (tableAliasCollection != null && !tableAliasCollection.isEmpty()) {
			for (Object mappedSourceObj : tableAliasCollection.values()) {
				if (!(mappedSourceObj instanceof String mappedSource) || !isQuerySourceReference(mappedSource)) {
					continue;
				}

				Object queryDefinitionObj = getQuerySourcePayloadPreferDefinition(mappedSource, null);
				if (queryDefinitionObj instanceof HashMap<?, ?>) {
					visibleQuerySources.put(mappedSource, queryDefinitionObj);
					continue;
				}

				Object queryDictionaryObj = getQuerySourceDictionaryPreferDefinition(mappedSource);
				if (queryDictionaryObj instanceof HashMap<?, ?>) {
					visibleQuerySources.put(mappedSource, queryDictionaryObj);
				}
			}
		}

		return visibleQuerySources;
	}

	@SuppressWarnings("unchecked")
	public boolean isWildcardBackedQueryCandidate(String queryRef, HashMap<String, Object> queryCollection) {
		if (queryRef == null || !isQuerySourceReference(queryRef)) {
			return false;
		}

		if (isSetOperationSourceReference(queryRef)) {
			return hasWildcardInQueryOutputInterface(queryRef);
		}

		if (queryCollection != null) {
			Object queryObj = getQuerySourcePayloadPreferDefinition(queryRef, queryCollection);
			if (queryObj instanceof Map<?, ?> queryMap
					&& ((Map<String, Object>) queryMap).containsKey("*")) {
				return true;
			}
		}

		Object queryDictionaryObj = getQuerySourceDictionaryPreferDefinition(queryRef);
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

		if (isSetOperationSourceReference(queryRef)) {
			// Set-operation sources expose only their published output interface.
			return hasColumnInQueryOutputInterface(queryRef, columnName)
					|| hasWildcardInQueryOutputInterface(queryRef);
		}

		if (isWildcardBackedQueryCandidate(queryRef, queryCollection)) {
			return true;
		}

		if (queryCollection != null) {
			Object queryObj = getQuerySourcePayloadPreferDefinition(queryRef, queryCollection);
			if (queryObj instanceof Map<?, ?> queryMap
					&& containsKeyIgnoreCase((Map<String, Object>) queryMap, columnName)) {
				return true;
			}
		}

		Object queryDictionaryObj = getQuerySourceDictionaryPreferDefinition(queryRef);
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

		if (isSetOperationSourceReference(queryRef)) {
			// Set-operation sources must resolve against set-operation output interface only.
			return hasColumnInQueryOutputInterface(queryRef, columnName);
		}

		if (isTableFunctionSourceReference(queryRef)) {
			return false;
		}

		if (queryCollection != null) {
			Object queryObj = getQuerySourcePayloadPreferDefinition(queryRef, queryCollection);
			if (queryObj instanceof Map<?, ?> queryMap
					&& containsKeyIgnoreCase((Map<String, Object>) queryMap, columnName)) {
				return true;
			}
		}

		Object queryDictionaryObj = getQuerySourceDictionaryPreferDefinition(queryRef);
		if (queryDictionaryObj instanceof Map<?, ?> queryDictionary
				&& containsKeyIgnoreCase((Map<String, Object>) queryDictionary, columnName)) {
			return true;
		}

		return hasColumnInQueryOutputInterface(queryRef, columnName);
	}

	public String resolveAliasToQuerySourceRefPreferDefinition(
			String aliasRef,
			HashMap<String, Object> tableAliasCollection,
			HashMap<String, Object> scopedQueryCollection) {
		String resolvedNonTableSourceRef = walker.resolveAliasToNonTableSourceQueryKey(aliasRef, scopedQueryCollection);
		if (resolvedNonTableSourceRef == null) {
			resolvedNonTableSourceRef = resolveAliasToQuerySourceFromAliasMap(aliasRef, tableAliasCollection);
		}
		return normalizeQuerySourceReference(resolvedNonTableSourceRef);
	}

	public String normalizeQuerySourceReference(String sourceRef) {
		if (sourceRef == null || sourceRef.isBlank()) {
			return null;
		}
		String normalizedSourceRef = sourceRef;
		if (normalizedSourceRef.startsWith("def_")) {
			normalizedSourceRef = normalizedSourceRef.substring("def_".length());
		}
		return normalizedSourceRef;
	}

	@SuppressWarnings("unchecked")
	private void mergeIntoGlobalQueryColumnDictionary(String queryScopeKey, HashMap<String, Object> queryDictionary) {
		if (queryScopeKey == null || queryScopeKey.isBlank() || queryDictionary == null || queryDictionary.isEmpty()) {
			return;
		}

		// Global query-column map uses live source refs only (queryN, unionN, …).
		String normalizedScopeKey = normalizeQuerySourceReference(queryScopeKey);
		if (normalizedScopeKey == null || normalizedScopeKey.isBlank()) {
			return;
		}

		Object existingObj = walker.queryColumnDictionaryMap.get(normalizedScopeKey);
		if (!(existingObj instanceof HashMap<?, ?> existingMapObj)) {
			walker.queryColumnDictionaryMap.put(normalizedScopeKey, new HashMap<String, Object>(queryDictionary));
			return;
		}

		HashMap<String, Object> existingMap = (HashMap<String, Object>) existingMapObj;
		for (Map.Entry<String, Object> entry : queryDictionary.entrySet()) {
			String columnName = entry.getKey();
			if (columnName == null) {
				continue;
			}
			Object existingRefs = existingMap.get(columnName);
			if (existingRefs == null) {
				existingMap.put(columnName, entry.getValue());
				continue;
			}
			existingMap.put(columnName, mergeReferenceCollections(existingRefs, entry.getValue()));
		}
	}

	public boolean isDefinitionScopeKey(String scopeKey) {
		return scopeKey != null && scopeKey.startsWith("def_");
	}

	public String toLiveScopeKey(String scopeKey) {
		return normalizeQuerySourceReference(scopeKey);
	}

	public String toDefinitionScopeKey(String scopeKey) {
		String liveScopeKey = toLiveScopeKey(scopeKey);
		if (liveScopeKey == null || liveScopeKey.isBlank()) {
			return null;
		}
		return "def_" + liveScopeKey;
	}

	public boolean isSetOperationSourceReference(String sourceRef) {
		String normalizedSourceRef = normalizeQuerySourceReference(sourceRef);
		if (normalizedSourceRef == null || normalizedSourceRef.isBlank()) {
			return false;
		}
		return normalizedSourceRef.startsWith(MUMBLE_UNION_KEY)
				|| normalizedSourceRef.startsWith(MUMBLE_INTERSECT_KEY);
	}

	public boolean isQueryOrSetOrValuesSourceReference(String sourceRef) {
		String normalizedSourceRef = normalizeQuerySourceReference(sourceRef);
		if (normalizedSourceRef == null || normalizedSourceRef.isBlank()) {
			return false;
		}
		return normalizedSourceRef.startsWith(MUMBLE_QUERY_KEY)
				|| normalizedSourceRef.startsWith(MUMBLE_UNION_KEY)
				|| normalizedSourceRef.startsWith(MUMBLE_INTERSECT_KEY)
				|| normalizedSourceRef.startsWith(MUMBLE_VALUES_KEY)
				|| MUMBLE_VALUES_KEY.equals(normalizedSourceRef);
	}

	public Object getQuerySourcePayloadPreferDefinition(String queryRef, HashMap<String, Object> queryCollection) {
		String normalizedQueryRef = normalizeQuerySourceReference(queryRef);
		if (normalizedQueryRef == null || !isQuerySourceReference(normalizedQueryRef)) {
			return null;
		}

		String defQueryRef = toDefinitionScopeKey(normalizedQueryRef);

		if (queryCollection != null && !queryCollection.isEmpty()) {
			Object queryObj = queryCollection.get(defQueryRef);
			if (queryObj instanceof Map<?, ?>) {
				return queryObj;
			}
		}

		Object queryObj = findDefinitionSymbolInVisibleScopes(defQueryRef);
		if (queryObj instanceof Map<?, ?>) {
			return queryObj;
		}

		return null;
	}

	/**
	 * Reads a scope's column-token map from {@link SqlASTWalkerHelper#queryColumnDictionaryMap}.
	 * That global map is keyed by live source refs ({@code queryN}, {@code unionN}, etc.), not
	 * {@code def_*}. Nested scope payloads live under {@code def_queryN} in the symbol table;
	 * use {@link #getQueryDefinitionSymbol} for those.
	 */
	public Object getQuerySourceDictionaryPreferDefinition(String queryRef) {
		String liveQueryRef = normalizeQuerySourceReference(queryRef);
		if (liveQueryRef == null || !isQuerySourceReference(liveQueryRef)) {
			return null;
		}

		Object queryDictionaryObj = walker.queryColumnDictionaryMap.get(liveQueryRef);
		if (queryDictionaryObj instanceof Map<?, ?>) {
			return queryDictionaryObj;
		}

		return null;
	}

	public boolean isQuerySourceReference(String sourceRef) {
		if (sourceRef == null || sourceRef.isBlank()) {
			return false;
		}
		String normalizedSourceRef = normalizeQuerySourceReference(sourceRef);
		return normalizedSourceRef.startsWith(MUMBLE_QUERY_KEY)
				|| normalizedSourceRef.startsWith(MUMBLE_UNION_KEY)
				|| normalizedSourceRef.startsWith(MUMBLE_INTERSECT_KEY)
				|| normalizedSourceRef.startsWith(MUMBLE_VALUES_KEY)
				|| normalizedSourceRef.startsWith(MUMBLE_DELETE_KEY)
				|| MUMBLE_VALUES_KEY.equals(normalizedSourceRef);
	}

	public boolean isValuesSourceReference(String sourceRef) {
		String normalizedSourceRef = normalizeQuerySourceReference(sourceRef);
		if (normalizedSourceRef == null || normalizedSourceRef.isBlank()) {
			return false;
		}

		return normalizedSourceRef.startsWith(MUMBLE_VALUES_KEY)
				|| MUMBLE_VALUES_KEY.equals(normalizedSourceRef);
	}

	public boolean isTableFunctionSourceReference(String sourceRef) {
		String normalizedSourceRef = normalizeQuerySourceReference(sourceRef);
		if (normalizedSourceRef == null || normalizedSourceRef.isBlank()) {
			return false;
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
					if (refName != null && refTable != null
							&& !"*".equals(refTable) && !"*".equals(refName)) {
						String qualifiedKey = refTable + "." + refName;
						if (unresolvedColumnMap.containsKey(qualifiedKey)) {
							explicitQualifiedKeys.add(qualifiedKey);
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
					&& !"*".equals(refTableRef)
					&& !"*".equals(refName)) {
				String qualifiedKey = refTableRef + "." + refName;
				if (unresolvedColumnMap.containsKey(qualifiedKey)) {
					explicitQualifiedKeys.add(qualifiedKey);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void emitExplicitQualifiedUnknownDiagnostics(
			HashMap<String, Object> explicitQualifiedUnknownEntries,
			HashMap<String, Object> tableAliasCollection,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> scopedQueryCollection,
			HashMap<String, Object> unresolvedCollector,
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> localInterface,
			HashMap<String, Object> localCurrentQueryDictionary,
			String deleteTargetTableRef,
			String deleteTargetAlias,
			HashMap<String, Object> localDerivedColumns,
			ArrayList<Object> relationalModifierInterfaceHints) {
		if (explicitQualifiedUnknownEntries == null || explicitQualifiedUnknownEntries.isEmpty()) {
			return;
		}

		for (Map.Entry<String, Object> unknownEntry : explicitQualifiedUnknownEntries.entrySet()) {
			String unresolvedKey = unknownEntry.getKey();
			if (unresolvedKey == null || !unresolvedKey.contains(".")) {
				// Explicit-qualified diagnostics are only for truly qualified keys.
				continue;
			}
			String columnName = unresolvedKey;
			String tableRef = null;
			tableRef = unresolvedKey.substring(0, unresolvedKey.lastIndexOf('.'));
			columnName = unresolvedKey.substring(unresolvedKey.lastIndexOf('.') + 1);
			if (tableRef == null) {
				continue;
			}

			String allLocationsForEntry = walker.formatAllLocationsForEntry(unknownEntry.getValue());
			boolean hasMergedLocations = allLocationsForEntry != null
					&& allLocationsForEntry.startsWith("[")
					&& allLocationsForEntry.contains(",");

			QualifiedScopeResolutionResult resolutionResult =
					resolveQualifiedColumnAgainstVisibleScope(
							tableRef,
							columnName,
							tableAliasCollection,
							tableCollection,
							scopedQueryCollection,
							false,
							localDerivedColumns,
							relationalModifierInterfaceHints);

			switch (resolutionResult.status) {
				case RESOLVED_DERIVED_COLUMN -> {
					materializeQualifiedUnresolvedEntry(
							unresolvedKey,
							unknownEntry.getValue(),
							tableAliasCollection,
							localTableCollection,
							localCurrentQueryDictionary,
							localInterface);
					continue;
				}
				case RESOLVED_WILDCARD_QUERY_SOURCE -> {
					promoteQualifiedWildcardIntoQuerySource(
							resolutionResult.querySourceRef,
							unknownEntry.getValue());
					continue;
				}
				case RESOLVED_QUERY_SOURCE -> {
					materializeResolvedQualifiedQuerySourceReference(
							tableRef,
							columnName,
							resolutionResult.querySourceRef,
							unknownEntry.getValue(),
							null,
							tableAliasCollection,
							false);
					continue;
				}
				case UNRESOLVED_QUERY_SOURCE -> {
					String querySourceRef = resolveAliasToQuerySourceRefPreferDefinition(
							tableRef,
							tableAliasCollection,
							scopedQueryCollection);
					if (querySourceRef == null) {
						querySourceRef = walker.resolveAliasToTableName(tableRef, tableAliasCollection);
					}
					String resolvedTableRef = walker.resolveAliasToTableName(tableRef, tableAliasCollection);
					String locationsSuffix = hasMergedLocations
							? " Locations: " + allLocationsForEntry
							: null;
					emitQualifiedQueryAliasColumnNotFoundFatal(
							columnName,
							tableRef,
							querySourceRef,
							resolvedTableRef,
							unknownEntry.getValue(),
							locationsSuffix);
					continue;
				}
				case RESOLVED_PHYSICAL_SOURCE -> {
					if (materializeQualifiedUnresolvedEntry(
							unresolvedKey,
							unknownEntry.getValue(),
							tableAliasCollection,
							localTableCollection,
							localCurrentQueryDictionary,
							localInterface)) {
						continue;
					}
				}
				default -> {
				}
			}

			String qualifiedStorageKey = (unresolvedKey != null && unresolvedKey.contains("."))
					? unresolvedKey
					: tableRef + "." + columnName;

			if (isDeleteSiblingCorrelationReference(
					tableRef,
					tableAliasCollection,
					localTableCollection,
					deleteTargetTableRef,
					deleteTargetAlias)) {
				// Keep unresolved so qualified-source fatal is emitted later.
			} else if ((walker.canResolveQualifiedUnknownInScope(
					unresolvedKey,
					tableAliasCollection,
					buildQualifiedResolutionTableDictionaryView(tableCollection),
					walker.queryColumnDictionaryMap)
					|| canMaterializeQualifiedToKnownPhysicalSource(
							unresolvedKey,
							tableAliasCollection,
							localTableCollection,
							tableCollection))
					&& materializeQualifiedUnresolvedEntry(
							unresolvedKey,
							unknownEntry.getValue(),
							tableAliasCollection,
							localTableCollection,
							localCurrentQueryDictionary,
							localInterface)) {
				continue;
			}

			Integer[] refLocation = walker.getLineAndCharacterFromEntry(unknownEntry.getValue());
			if (refLocation[0] == null || refLocation[1] == null) {
				refLocation = walker.getFirstEntryLineAndCharacter(explicitQualifiedUnknownEntries);
			}

			if (unresolvedCollector != null) {
				HashMap<String, Object> singleEntry = new HashMap<String, Object>();
				singleEntry.put(qualifiedStorageKey, unknownEntry.getValue());
				walker.mergeUnknownEntries(unresolvedCollector, singleEntry);
			}
		}
	}

	/**
	 * True when a physical table qualifier is visible in the current grammar scope: local FROM,
	 * inherited ancestor {@code table_dictionary}, or a registered physical alias in the effective
	 * alias map (correlated outer refs). Does not consult the statement-global table dictionary.
	 */
	private boolean isPhysicalTableRefVisibleInScope(
			String sourceRef,
			HashMap<String, Object> tableAliasCollection,
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> visibleTableCollection) {
		if (sourceRef == null || sourceRef.isBlank()) {
			return false;
		}

		String resolvedTableRef = walker.resolveAliasToTableName(sourceRef, tableAliasCollection);
		if (resolvedTableRef == null || resolvedTableRef.isBlank()) {
			resolvedTableRef = sourceRef;
		}
		if (walker.isNonTableQuerySourceReference(resolvedTableRef)
				|| walker.isNonTableQuerySourceReference(sourceRef)
				|| isQuerySourceReference(resolvedTableRef)) {
			return false;
		}

		String normalizedRef = normalizeTableRef(resolvedTableRef);

		if (localTableCollection != null && !localTableCollection.isEmpty()) {
			if (walker.getTableDictionaryForReference(normalizedRef, localTableCollection) != null) {
				return true;
			}
			for (String tableKey : localTableCollection.keySet()) {
				if (tableKey != null && tableKey.equalsIgnoreCase(sourceRef)) {
					return true;
				}
			}
		}

		if (visibleTableCollection != null && !visibleTableCollection.isEmpty()) {
			if (walker.getTableDictionaryForReference(normalizedRef, visibleTableCollection) != null) {
				return true;
			}
			for (String tableKey : visibleTableCollection.keySet()) {
				if (tableKey != null && tableKey.equalsIgnoreCase(sourceRef)) {
					return true;
				}
			}
		}

		if (tableAliasCollection == null || tableAliasCollection.isEmpty()) {
			return false;
		}

		Object mappedAlias = tableAliasCollection.get(sourceRef);
		if (!(mappedAlias instanceof String)) {
			for (Map.Entry<String, Object> aliasEntry : tableAliasCollection.entrySet()) {
				if (aliasEntry.getKey() != null
						&& aliasEntry.getKey().equalsIgnoreCase(sourceRef)
						&& aliasEntry.getValue() instanceof String aliasTarget) {
					mappedAlias = aliasTarget;
					break;
				}
			}
		}
		if (!(mappedAlias instanceof String aliasTarget) || aliasTarget.isBlank()) {
			return false;
		}
		return !walker.isNonTableQuerySourceReference(aliasTarget) && !isQuerySourceReference(aliasTarget);
	}

	private boolean canMaterializeQualifiedToKnownPhysicalSource(
			String unresolvedKey,
			HashMap<String, Object> tableAliasCollection,
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> visibleTableCollection) {
		if (unresolvedKey == null || unresolvedKey.isBlank() || !unresolvedKey.contains(".")) {
			return false;
		}

		int dotIndex = unresolvedKey.indexOf('.');
		String sourceRef = unresolvedKey.substring(0, dotIndex);
		if (sourceRef.isBlank()) {
			return false;
		}

		return isPhysicalTableRefVisibleInScope(
				sourceRef,
				tableAliasCollection,
				localTableCollection,
				visibleTableCollection);
	}

	private boolean isDeleteSiblingCorrelationReference(
			String sourceRef,
			HashMap<String, Object> tableAliasCollection,
			HashMap<String, Object> localTableCollection,
			String deleteTargetTableRef,
			String deleteTargetAlias) {
		if (sourceRef == null || sourceRef.isBlank()
				|| deleteTargetTableRef == null || deleteTargetTableRef.isBlank()) {
			return false;
		}

		// DELETE target alias/table are allowed correlations.
		if (deleteTargetAlias != null && sourceRef.equalsIgnoreCase(deleteTargetAlias)) {
			return false;
		}
		if (normalizeTableRef(sourceRef).equals(normalizeTableRef(deleteTargetTableRef))) {
			return false;
		}

		String localAliasTarget = null;
		if (tableAliasCollection != null && !tableAliasCollection.isEmpty()) {
			for (Map.Entry<String, Object> aliasEntry : tableAliasCollection.entrySet()) {
				if (aliasEntry.getKey() != null
						&& aliasEntry.getKey().equalsIgnoreCase(sourceRef)
						&& aliasEntry.getValue() instanceof String aliasTarget) {
					localAliasTarget = aliasTarget;
					break;
				}
			}
		}

		// Local direct table refs are local scope sources.
		if (localTableCollection != null && !localTableCollection.isEmpty()) {
			for (String tableRef : localTableCollection.keySet()) {
				if (tableRef != null && tableRef.equalsIgnoreCase(sourceRef)) {
					return false;
				}
			}
			if (localAliasTarget != null) {
				for (String tableRef : localTableCollection.keySet()) {
					if (tableRef != null && tableRef.equalsIgnoreCase(localAliasTarget)) {
						return false;
					}
				}
			}
		}

		// Non-local source ref inside DELETE USING subquery: should not bind to sibling USING sources.
		return true;
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

		String liveQueryRef = normalizeQuerySourceReference(querySourceRef);
		if (liveQueryRef == null || liveQueryRef.isBlank()) {
			return;
		}

		Object sourceDictionaryObj = walker.queryColumnDictionaryMap.get(liveQueryRef);
		HashMap<String, Object> sourceQueryDictionary;
		if (sourceDictionaryObj instanceof Map<?, ?> sourceDictionaryMapObj) {
			sourceQueryDictionary = (HashMap<String, Object>) sourceDictionaryMapObj;
		} else {
			sourceQueryDictionary = new HashMap<String, Object>();
			walker.queryColumnDictionaryMap.put(liveQueryRef, sourceQueryDictionary);
		}

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

		releaseResolvedQualifiedGlobalLocationFromUnknownEntry(unknownEntryValue);
	}

	@SuppressWarnings("unchecked")
	private void releaseResolvedQualifiedGlobalLocationFromUnknownEntry(Object unknownEntryValue) {
		if (!(unknownEntryValue instanceof Map<?, ?> entryMapObj)) {
			return;
		}

		Object columnObj = ((Map<String, Object>) entryMapObj).get(MUMBLE_COLUMN_KEY);
		if (!(columnObj instanceof Map<?, ?>)) {
			return;
		}

		String tableRef = walker.extractReferenceTableRefFromInterfaceEntry(columnObj);
		String resolvedColumnName = walker.extractReferenceNameFromInterfaceEntry(columnObj);
		if (tableRef != null && !tableRef.isBlank() && !"*".equals(tableRef)
				&& resolvedColumnName != null && !resolvedColumnName.isBlank()) {
			releaseResolvedQualifiedGlobalLocationIfQualified(tableRef + "." + resolvedColumnName);
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

	private String normalizeQueryScopeDefinitionKey(String queryKey) {
		if (queryKey == null || queryKey.isBlank()) {
			return null;
		}
		if (isDefinitionScopeKey(queryKey)) {
			return queryKey;
		}
		if (!isQuerySourceReference(queryKey)) {
			return null;
		}
		return toDefinitionScopeKey(queryKey);
	}

	private Object findDefinitionSymbolInVisibleScopes(String definitionKey) {
		if (definitionKey == null || definitionKey.isBlank()) {
			return null;
		}
		return findInCurrentOrAncestorSymbolTables(definitionKey);
	}

	public Object getQueryDefinitionSymbol(String queryKey) {
		if (queryKey == null || queryKey.isBlank()) {
			return null;
		}

		String definitionKey = normalizeQueryScopeDefinitionKey(queryKey);
		if (definitionKey != null) {
			Object normalizedDefObj = findDefinitionSymbolInVisibleScopes(definitionKey);
			if (normalizedDefObj != null) {
				return normalizedDefObj;
			}
		}

		String cteScopeRef = resolveCteScopeReference(queryKey, null);
		if (cteScopeRef != null && !cteScopeRef.isBlank()) {
			String cteDefinitionKey = toDefinitionScopeKey(cteScopeRef);
			Object cteDefScopeObj = findDefinitionSymbolInVisibleScopes(cteDefinitionKey);
			if (cteDefScopeObj != null) {
				return cteDefScopeObj;
			}
		}

		return null;
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
	public Object findInCurrentOrAncestorSymbolTablesRecursive(String key) {
		if (key == null || key.isBlank()) {
			return null;
		}
		Object found = findInScopeTreeByKeyRecursive(walker.symbolTable, key);
		if (found != null) {
			return found;
		}
		for (Map<String, Object> ancestor : getAncestorSymbolTables()) {
			found = findInScopeTreeByKeyRecursive(ancestor, key);
			if (found != null) {
				return found;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Object findInScopeTreeByKeyRecursive(Map<String, Object> scopeNode, String key) {
		if (scopeNode == null || scopeNode.isEmpty() || key == null || key.isBlank()) {
			return null;
		}
		if (scopeNode.containsKey(key)) {
			return scopeNode.get(key);
		}
		for (Object valueObj : scopeNode.values()) {
			if (valueObj instanceof Map<?, ?> valueMapObj) {
				Object found = findInScopeTreeByKeyRecursive((Map<String, Object>) valueMapObj, key);
				if (found != null) {
					return found;
				}
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
			recordLocalFromRegisteredAlias(alias);
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

	public String findTopLevelScopeKey(String hdr) {
		if (hdr == null || hdr.isBlank()) {
			return null;
		}

		String selectedKey = null;
		int highestIndex = -1;
		for (String symbolKey : walker.symbolTable.keySet()) {
			if (symbolKey == null) {
				continue;
			}

			String normalizedKey = null;
			if (symbolKey.startsWith(hdr)) {
				normalizedKey = symbolKey;
			} else if (symbolKey.startsWith("def_" + hdr)) {
				normalizedKey = symbolKey.substring("def_".length());
			}

			if (normalizedKey == null) {
				continue;
			}
			String suffix = normalizedKey.substring(hdr.length());
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
				selectedKey = normalizedKey;
			}
		}
		return selectedKey;
	}

	public String findTopLevelValuesScopeKey() {
		return findTopLevelScopeKey(MUMBLE_VALUES_KEY);
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

	public String resolveQueryBackedSourceHeaderFromItem(Map<String, Object> item) {
		if (item == null || item.isEmpty()) {
			return null;
		}
		if (item.containsKey(MUMBLE_UNION_KEY)) {
			return MUMBLE_UNION_KEY;
		}
		if (item.containsKey(MUMBLE_INTERSECT_KEY)) {
			return MUMBLE_INTERSECT_KEY;
		}
		if (item.containsKey(MUMBLE_VALUES_KEY)) {
			return MUMBLE_VALUES_KEY;
		}
		if (item.containsKey(MUMBLE_SELECT_KEY)) {
			return MUMBLE_QUERY_KEY;
		}
		return null;
	}

	public void registerUnaliasedScopeSelfAlias(String scopeKey) {
		if (scopeKey == null || scopeKey.isBlank()) {
			return;
		}
		upsertCurrentTableAliasMapping(scopeKey, scopeKey);
		recordLocalFromRegisteredAlias(scopeKey);
	}

	public Boolean collectUnaliasedFromSourceSymbolTable(Map<String, Object> item) {
		String hdr = resolveQueryBackedSourceHeaderFromItem(item);
		if (hdr != null) {
			return collectQuerySymbolTable(hdr, null);
		}

		Boolean done = collectQuerySymbolTable(MUMBLE_QUERY_KEY, null);
		if (!done) {
			done = collectQuerySymbolTable(MUMBLE_INSERT_KEY, null);
		}
		if (!done) {
			done = collectQuerySymbolTable(MUMBLE_UPDATE_KEY, null);
		}
		if (!done) {
			done = collectQuerySymbolTable(MUMBLE_DELETE_KEY, null);
		}
		return done;
	}

	/**
	 * Registers an unaliased FROM source so unqualified columns resolve against the inner scope.
	 * Set operations only receive a self-alias mapping; full collection would disturb nested set-op scopes.
	 */
	public void registerUnaliasedFromSource(Map<String, Object> item) {
		String hdr = resolveQueryBackedSourceHeaderFromItem(item);
		if (MUMBLE_UNION_KEY.equals(hdr) || MUMBLE_INTERSECT_KEY.equals(hdr)) {
			registerUnaliasedScopeSelfAlias(findTopLevelScopeKey(hdr));
			return;
		}
		if (MUMBLE_VALUES_KEY.equals(hdr)) {
			if (!collectQuerySymbolTable(MUMBLE_VALUES_KEY, null)) {
				registerUnaliasedScopeSelfAlias(findTopLevelScopeKey(MUMBLE_VALUES_KEY));
			}
			return;
		}
		collectUnaliasedFromSourceSymbolTable(item);
	}

	public Boolean collectQuerySymbolTable(String hdr, String alias) {
		return collectQuerySymbolTable(hdr, alias, null);
	}

	public Boolean collectQuerySymbolTable(String hdr, String alias, Map<String, Object> definitionTarget) {
		String queryName = hdr + (walker.queryCount - 1);
		Map<String, Object> query = (Map<String, Object>) walker.symbolTable.remove(queryName);
		if (query == null) {
			String definitionQueryName = toDefinitionScopeKey(queryName);
			query = (Map<String, Object>) walker.symbolTable.remove(definitionQueryName);
		}
		if (query != null) {
			boolean insertSourceFlow = walker.currentStackLevel(SQLSelectParserParser.RULE_insert_source_primary) != null;
			if (insertSourceFlow) {
				pruneInsertSourceSequenceFromNestedDefinitions(query);
			} else {
				query.remove(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY);
			}
			// add alias to query
			if (alias != null) {
				upsertCurrentTableAliasMapping(alias, queryName);
				recordLocalFromRegisteredAlias(alias);
			} else {
				if (insertSourceFlow) {
					walker.symbolTable.put(queryName, new HashMap<String, Object>());
				}
				upsertCurrentTableAliasMapping(queryName, queryName);
				recordLocalFromRegisteredAlias(queryName);
			}

			// propagate interface to outer layer of query
			Map<String, Object> hold = insertSourceFlow
					? (Map<String, Object>) walker.symbolTable.get(queryName)
					: null;
			// Move unknowns to query
			Map<String, Object> unk = (Map<String, Object>)  walker.symbolTable.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
			HashMap<String, Object> liveDeferred = new HashMap<String, Object>();

			if (unk != null) {
				Map<String, Object> interfac = (Map<String, Object>) query.get(MUMBLE_INTERFACE_KEY);
				if (hold != null && interfac != null) {
					for (String key : interfac.keySet()) {
						Object unkItem = unk.remove(key);
						if (unkItem != null) {
							hold.put(key, unkItem);
						}
					}
				}

				if (!unk.isEmpty()) {
					walker.mergeUnknownEntries(liveDeferred, (HashMap<String, Object>) unk);
				}
			}

			// Always publish query definition in the parent symbol table.
			walker.symbolTable.put("def_" + queryName, query);

			if (definitionTarget != null && alias != null) {
				finalizeCteScopeDeferredUnresolved(
						alias,
						queryName,
						(HashMap<String, Object>) query,
						liveDeferred);
				definitionTarget.put(alias, queryName);
			} else {
				if (!liveDeferred.isEmpty()) {
					walker.symbolTable.put(MUMBLE_UNRESOLVED_COLUMN_KEY, liveDeferred);
				}
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

	@SuppressWarnings("unchecked")
	public void captureJoinOnClauseDependencies(Object onCondition) {
		Object existing = walker.symbolTable.remove(MUMBLE_FILTERS_KEY);
		ArrayList<Object> flatList;
		if (existing instanceof ArrayList<?>) {
			flatList = (ArrayList<Object>) existing;
		} else {
			flatList = new ArrayList<Object>();
		}

		if (onCondition instanceof HashMap<?, ?> onConditionMapObj) {
			flattenSubTreeForClauseColumns((HashMap<String, Object>) onConditionMapObj, flatList);
		}

		walker.symbolTable.put(MUMBLE_FILTERS_KEY, flatList);
	}

	// Standardize the filters reference map into a flat map of column references and not the entire AST subtree
	// This is a recursive function that traverses the item subtree until it finds column references or substitution variables, 
	// which it adds to the column list with the alias as the key

	public void flattenSubTreeForClauseColumns(HashMap<String, Object> subTree, ArrayList<Object> columnList) {
		flattenSubTreeForDependencyColumns(subTree, columnList, true);
	}

	public String getSubqueryReferenceKey(Map<String, Object> symbols) {
		if (symbols == null || symbols.isEmpty()) {
			return null;
		}

		for (String key : symbols.keySet()) {
			if (isQuerySourceReference(key) && !isDefinitionScopeKey(key)) {
				return key;
			}
		}

		for (String key : symbols.keySet()) {
			if (!isDefinitionScopeKey(key)) {
				continue;
			}
			String bareKey = toLiveScopeKey(key);
			if (isQuerySourceReference(bareKey)) {
				return key;
			}
		}

		return null;
	}

	/**
	 * Shared exit finalizer for archived query-like scopes (WITH CTE bodies, UNION/INTERSECT).
	 * Resolves scope-local deferred unknowns in-place; merges outer-correlated refs into the
	 * global table dictionary when the alias is visible — no upward bubble.
	 */
	@SuppressWarnings("unchecked")
	public void finalizeScopeDeferredUnresolved(
			HashMap<String, Object> scopePayload,
			HashMap<String, Object> liveDeferred) {
		if (scopePayload == null) {
			return;
		}

		HashMap<String, Object> pending = new HashMap<String, Object>();
		if (liveDeferred != null && !liveDeferred.isEmpty()) {
			walker.mergeUnknownEntries(pending, liveDeferred);
		}
		collectAndStripUnresolvedFromScopeTree(scopePayload, pending);

		if (pending.isEmpty()) {
			return;
		}

		resolveNestedScopeDeferredUnresolved(scopePayload, pending);

		HashMap<String, Object> scopeLocal = new HashMap<String, Object>();
		HashMap<String, Object> outerCorrelated = new HashMap<String, Object>();

		for (Map.Entry<String, Object> entry : pending.entrySet()) {
			String tableRef = extractTableRefFromUnresolvedEntry(entry.getKey(), entry.getValue());
			if (isAliasLocalToScopeTree(scopePayload, tableRef)
					|| isPriorCteAlias(tableRef, getContextListSymbolMap(scopePayload))) {
				scopeLocal.put(entry.getKey(), entry.getValue());
			} else {
				outerCorrelated.put(entry.getKey(), entry.getValue());
			}
		}

		resolveScopeBodyDeferredUnresolved(scopeLocal, scopePayload);
		mergeUnresolvedEntriesIntoCurrentScope(outerCorrelated);
	}

	/**
	 * Finalizes deferred unresolved columns when a WITH CTE is registered in {@code context_list}.
	 */
	public void finalizeCteScopeDeferredUnresolved(
			String cteAlias,
			String queryName,
			HashMap<String, Object> cteScopePayload,
			HashMap<String, Object> liveDeferred) {
		finalizeScopeDeferredUnresolved(cteScopePayload, liveDeferred);
	}

	private static final String[] WITH_MAIN_BODY_SCOPE_PREFIXES = new String[] {
			MUMBLE_VALUES_KEY,
			MUMBLE_INTERSECT_KEY,
			MUMBLE_UNION_KEY,
			MUMBLE_INSERT_KEY,
			MUMBLE_UPDATE_KEY,
			MUMBLE_DELETE_KEY,
			MUMBLE_QUERY_KEY
	};

	/**
	 * Resolves the live scope key for the main body of a {@code with_query} at {@code scopeIndex}.
	 * Checks both live keys and already-published {@code def_*} payloads (post-{@link #publishQueryLikeScope}).
	 */
	public String resolveWithMainBodyLiveScopeKey(int scopeIndex) {
		for (String prefix : WITH_MAIN_BODY_SCOPE_PREFIXES) {
			String liveKey = prefix + scopeIndex;
			if (walker.symbolTable.containsKey(liveKey)) {
				return liveKey;
			}
			String definitionKey = toDefinitionScopeKey(liveKey);
			if (definitionKey != null && walker.symbolTable.containsKey(definitionKey)) {
				return liveKey;
			}
		}
		return MUMBLE_QUERY_KEY + scopeIndex;
	}

	/**
	 * Promotes the WITH main body's already-published scope instead of wrapping it in a duplicate
	 * {@code def_*} shell. CTE sibling {@code def_*} payloads from the WITH frame are nested as children.
	 * Applies to every {@code with_query} that has a WITH list (nested or statement-top).
	 *
	 * @return the promoted scope payload (also installed on {@code walker.symbolTable} under {@code def_<liveKey>})
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> promoteWithQueryMainBodyScope(int scopeIndex) {
		String liveScopeKey = resolveWithMainBodyLiveScopeKey(scopeIndex);
		String definitionScopeKey = toDefinitionScopeKey(liveScopeKey);

		HashMap<String, Object> promotedScope = (HashMap<String, Object>) walker.symbolTable.remove(liveScopeKey);
		if (promotedScope == null && definitionScopeKey != null) {
			promotedScope = (HashMap<String, Object>) walker.symbolTable.remove(definitionScopeKey);
		}
		if (promotedScope == null) {
			promotedScope = new HashMap<String, Object>();
		}

		HashMap<String, Object> withFrameSymbols = walker.symbolTable;
		Map<String, Object> withContextList = getContextListSymbolMap(withFrameSymbols);
		if (withContextList != null && !withContextList.isEmpty()) {
			Map<String, Object> queryContextList = getContextListSymbolMap(promotedScope);
			if (queryContextList == null) {
				promotedScope.put(MUMBLE_CONTEXT_LIST_KEY, new LinkedHashMap<String, Object>(withContextList));
			} else {
				for (Map.Entry<String, Object> entry : withContextList.entrySet()) {
					queryContextList.putIfAbsent(entry.getKey(), entry.getValue());
				}
			}
			withFrameSymbols.remove(MUMBLE_CONTEXT_LIST_KEY);
		}
		nestCteSiblingDefinitionsFromWithFrame(promotedScope, definitionScopeKey, withFrameSymbols);
		mergeTableAliasMapsFromWithFrame(promotedScope, withFrameSymbols);
		mergeUnresolvedColumnMapsFromWithFrame(promotedScope, withFrameSymbols);
		absorbWalkTimeBackupKey(withFrameSymbols, promotedScope, MUMBLE_OUTER_TABLE_ALIAS_KEY);
		absorbWalkTimeBackupKey(withFrameSymbols, promotedScope, MUMBLE_OUTER_CONTEXT_LIST_KEY);
		absorbWalkTimeBackupKey(withFrameSymbols, promotedScope, MUMBLE_OUTER_DEF_ENTRIES_KEY);

		walker.symbolTable = new HashMap<String, Object>();
		if (definitionScopeKey != null) {
			walker.symbolTable.put(definitionScopeKey, promotedScope);
		}

		return promotedScope;
	}

	@SuppressWarnings("unchecked")
	private void nestCteSiblingDefinitionsFromWithFrame(
			HashMap<String, Object> promotedScope,
			String mainBodyDefinitionKey,
			HashMap<String, Object> withFrameSymbols) {
		if (withFrameSymbols == null || withFrameSymbols.isEmpty()) {
			return;
		}

		ArrayList<String> siblingDefinitionKeys = new ArrayList<String>();
		for (String frameKey : withFrameSymbols.keySet()) {
			if (frameKey == null || !frameKey.startsWith("def_")) {
				continue;
			}
			if (frameKey.equals(mainBodyDefinitionKey)) {
				continue;
			}
			if (!(withFrameSymbols.get(frameKey) instanceof Map<?, ?>)) {
				continue;
			}
			siblingDefinitionKeys.add(frameKey);
		}

		for (String siblingKey : siblingDefinitionKeys) {
			Object siblingScopeObj = withFrameSymbols.remove(siblingKey);
			if (siblingScopeObj == null) {
				continue;
			}
			promotedScope.putIfAbsent(siblingKey, siblingScopeObj);
		}
	}

	@SuppressWarnings("unchecked")
	private void mergeTableAliasMapsFromWithFrame(
			HashMap<String, Object> promotedScope,
			HashMap<String, Object> withFrameSymbols) {
		if (withFrameSymbols == null || withFrameSymbols.isEmpty()) {
			return;
		}

		Object frameAliasObj = withFrameSymbols.remove(MUMBLE_TABLE_ALIAS_KEY);
		if (!(frameAliasObj instanceof Map<?, ?> frameAliasMapObj)) {
			return;
		}

		HashMap<String, Object> frameAliasMap = (HashMap<String, Object>) frameAliasMapObj;
		if (frameAliasMap.isEmpty()) {
			return;
		}

		Object promotedAliasObj = promotedScope.get(MUMBLE_TABLE_ALIAS_KEY);
		if (!(promotedAliasObj instanceof Map<?, ?> promotedAliasMapObj)) {
			promotedScope.put(MUMBLE_TABLE_ALIAS_KEY, new LinkedHashMap<String, Object>(frameAliasMap));
			return;
		}

		HashMap<String, Object> promotedAliasMap = (HashMap<String, Object>) promotedAliasMapObj;
		for (Map.Entry<String, Object> entry : frameAliasMap.entrySet()) {
			promotedAliasMap.putIfAbsent(entry.getKey(), entry.getValue());
		}
	}

	@SuppressWarnings("unchecked")
	private void mergeUnresolvedColumnMapsFromWithFrame(
			HashMap<String, Object> promotedScope,
			HashMap<String, Object> withFrameSymbols) {
		if (withFrameSymbols == null || withFrameSymbols.isEmpty()) {
			return;
		}

		Object frameUnresolvedObj = withFrameSymbols.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
		if (!(frameUnresolvedObj instanceof Map<?, ?> frameUnresolvedMapObj)) {
			return;
		}

		HashMap<String, Object> frameUnresolvedMap = (HashMap<String, Object>) frameUnresolvedMapObj;
		if (frameUnresolvedMap.isEmpty()) {
			return;
		}

		Object promotedUnresolvedObj = promotedScope.get(MUMBLE_UNRESOLVED_COLUMN_KEY);
		if (promotedUnresolvedObj instanceof Map<?, ?> promotedUnresolvedMapObj) {
			walker.mergeUnknownEntries(
					(HashMap<String, Object>) promotedUnresolvedMapObj,
					frameUnresolvedMap);
			return;
		}

		promotedScope.put(MUMBLE_UNRESOLVED_COLUMN_KEY, new HashMap<String, Object>(frameUnresolvedMap));
	}

	private void absorbWalkTimeBackupKey(
			HashMap<String, Object> from,
			HashMap<String, Object> to,
			String key) {
		Object value = from.remove(key);
		if (value != null) {
			to.put(key, value);
		}
	}

	/**
	 * Merges the global {@link SqlASTWalkerHelper#queryColumnDictionaryMap} entry for a published
	 * query scope into that scope's local {@code query_dictionary} (handoff sync). Downstream
	 * references (e.g. UPDATE {@code o.col}) land on the global map after the scope is published;
	 * this keeps nested {@code def_queryN} payloads aligned with the global two-store model.
	 */
	@SuppressWarnings("unchecked")
	public void syncPublishedScopeQueryDictionariesFromGlobal(HashMap<String, Object> scopeRoot) {
		if (scopeRoot == null || scopeRoot.isEmpty()) {
			return;
		}

		for (Map.Entry<String, Object> entry : scopeRoot.entrySet()) {
			String scopeKey = entry.getKey();
			Object scopeObj = entry.getValue();
			if (!(scopeObj instanceof HashMap<?, ?> scopeMapObj)) {
				continue;
			}

			HashMap<String, Object> scopeMap = (HashMap<String, Object>) scopeMapObj;
			if (scopeKey != null && scopeKey.startsWith("def_")) {
				String liveScopeKey = normalizeQuerySourceReference(scopeKey);
				if (liveScopeKey != null && isPublishedQueryDictionaryScopeKey(liveScopeKey)) {
					mergeGlobalQueryDictionaryIntoScopeQueryDictionary(scopeMap, liveScopeKey);
				}
			}
			syncPublishedScopeQueryDictionariesFromGlobal(scopeMap);
		}
	}

	private boolean isPublishedQueryDictionaryScopeKey(String liveScopeKey) {
		if (liveScopeKey == null || liveScopeKey.isBlank()) {
			return false;
		}
		return liveScopeKey.startsWith(MUMBLE_QUERY_KEY)
				|| liveScopeKey.startsWith(MUMBLE_UNION_KEY)
				|| liveScopeKey.startsWith(MUMBLE_INTERSECT_KEY)
				|| liveScopeKey.startsWith(MUMBLE_VALUES_KEY);
	}

	@SuppressWarnings("unchecked")
	private void mergeGlobalQueryDictionaryIntoScopeQueryDictionary(
			HashMap<String, Object> scopeMap,
			String liveScopeKey) {
		if (scopeMap == null || liveScopeKey == null || liveScopeKey.isBlank()
				|| walker.queryColumnDictionaryMap == null) {
			return;
		}

		Object globalDictionaryObj = walker.queryColumnDictionaryMap.get(liveScopeKey);
		if (!(globalDictionaryObj instanceof Map<?, ?> globalDictionaryMapObj)
				|| globalDictionaryMapObj.isEmpty()) {
			return;
		}

		HashMap<String, Object> globalDictionary = (HashMap<String, Object>) globalDictionaryMapObj;
		Object scopeDictionaryObj = scopeMap.get(MUMBLE_QUERY_DICTIONARY_KEY);
		HashMap<String, Object> scopeDictionary;
		if (scopeDictionaryObj instanceof Map<?, ?> scopeDictionaryMapObj) {
			scopeDictionary = (HashMap<String, Object>) scopeDictionaryMapObj;
		} else {
			scopeDictionary = new HashMap<String, Object>();
			scopeMap.put(MUMBLE_QUERY_DICTIONARY_KEY, scopeDictionary);
		}

		for (Map.Entry<String, Object> columnEntry : globalDictionary.entrySet()) {
			String columnName = columnEntry.getKey();
			if (columnName == null) {
				continue;
			}
			Object existingRefs = scopeDictionary.get(columnName);
			if (existingRefs == null) {
				scopeDictionary.put(columnName, columnEntry.getValue());
				continue;
			}
			scopeDictionary.put(columnName, mergeReferenceCollections(existingRefs, columnEntry.getValue()));
		}
	}

	/**
	 * After {@code exitWith_query} promotes the main body, resolves any remaining
	 * archived deferred refs against visible outer scope (no bubble into the parent frame).
	 */
	@SuppressWarnings("unchecked")
	public void hoistMainBodyDeferredUnresolvedFromWithQueryScope(HashMap<String, Object> queryScopePayload) {
		if (queryScopePayload == null || queryScopePayload.isEmpty()) {
			return;
		}

		HashMap<String, Object> hoisted = new HashMap<String, Object>();
		walker.mergeUnknownEntries(hoisted, extractDefArchivedUnresolved(queryScopePayload));
		walker.mergeUnknownEntries(hoisted, extractUnresolvedColumnMapFromScopePayload(queryScopePayload));
		mergeUnresolvedEntriesIntoCurrentScope(hoisted);
	}

	@SuppressWarnings("unchecked")
	private void collectAndStripUnresolvedFromScopeTree(
			HashMap<String, Object> scopeNode,
			HashMap<String, Object> collected) {
		if (scopeNode == null || scopeNode.isEmpty()) {
			return;
		}

		walker.mergeUnknownEntries(collected, extractDefArchivedUnresolved(scopeNode));
		walker.mergeUnknownEntries(collected, extractUnresolvedColumnMapFromScopePayload(scopeNode));

		for (Object value : scopeNode.values()) {
			if (value instanceof HashMap<?, ?>) {
				collectAndStripUnresolvedFromScopeTree((HashMap<String, Object>) value, collected);
			}
		}
	}

	private boolean isPriorCteAlias(String tableRef, Map<String, Object> priorCteList) {
		if (tableRef == null || tableRef.isBlank() || priorCteList == null || priorCteList.isEmpty()) {
			return false;
		}
		if (priorCteList.containsKey(tableRef)) {
			return true;
		}
		for (String alias : priorCteList.keySet()) {
			if (alias != null && alias.equalsIgnoreCase(tableRef)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private void resolveNestedScopeDeferredUnresolved(
			HashMap<String, Object> cteScopePayload,
			HashMap<String, Object> pending) {
		if (pending == null || pending.isEmpty()) {
			return;
		}

		ArrayList<String> consumed = new ArrayList<String>();
		for (Map.Entry<String, Object> entry : pending.entrySet()) {
			String tableRef = extractTableRefFromUnresolvedEntry(entry.getKey(), entry.getValue());
			HashMap<String, Object> nestedScope = findInnermostNestedScopeForTableRef(cteScopePayload, tableRef);
			if (nestedScope == null) {
				continue;
			}

			HashMap<String, Object> singlePending = new HashMap<String, Object>();
			singlePending.put(entry.getKey(), entry.getValue());
			HashMap<String, Object> outerCorrelated = new HashMap<String, Object>();
			HashMap<String, Object> innerLocal = new HashMap<String, Object>();
			partitionPredicateUnresolvedByScope(
					singlePending,
					nestedScope,
					outerCorrelated,
					innerLocal);
			if (!innerLocal.isEmpty()) {
				resolveInnerLocalPredicateUnresolved(
						innerLocal,
						nestedScope);
				consumed.add(entry.getKey());
			}
		}

		for (String key : consumed) {
			pending.remove(key);
		}
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> findInnermostNestedScopeForTableRef(
			HashMap<String, Object> cteScopePayload,
			String tableRef) {
		if (tableRef == null || tableRef.isBlank() || cteScopePayload == null) {
			return null;
		}

		HashMap<String, Object> topAliasMap = getScopeTableAliasMap(cteScopePayload);
		if (isAliasLocalToScope(tableRef, topAliasMap)) {
			return null;
		}

		return findInnermostNestedScopeForTableRefRecursive(cteScopePayload, tableRef, false);
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> findInnermostNestedScopeForTableRefRecursive(
			HashMap<String, Object> node,
			String tableRef,
			boolean belowTop) {
		HashMap<String, Object> bestMatch = null;

		if (belowTop) {
			HashMap<String, Object> aliasMap = getScopeTableAliasMap(node);
			if (isAliasLocalToScope(tableRef, aliasMap)) {
				bestMatch = node;
			}
		}

		for (Object value : node.values()) {
			if (value instanceof HashMap<?, ?>) {
				HashMap<String, Object> nestedMatch = findInnermostNestedScopeForTableRefRecursive(
						(HashMap<String, Object>) value,
						tableRef,
						true);
				if (nestedMatch != null) {
					bestMatch = nestedMatch;
				}
			}
		}

		return bestMatch;
	}

	@SuppressWarnings("unchecked")
	private void resolveScopeBodyDeferredUnresolved(
			HashMap<String, Object> scopeLocal,
			HashMap<String, Object> scopePayload) {
		if (scopeLocal == null || scopeLocal.isEmpty()) {
			return;
		}

		HashMap<String, Object> qualifiedUnresolved = new HashMap<String, Object>();
		HashMap<String, Object> unqualifiedUnresolved = new HashMap<String, Object>();
		splitUnresolvedEntriesByQualification(scopeLocal, qualifiedUnresolved, unqualifiedUnresolved);

		if (!unqualifiedUnresolved.isEmpty()) {
			emitUnqualifiedUnresolvedColumnsError(unqualifiedUnresolved);
		}
		if (qualifiedUnresolved.isEmpty()) {
			return;
		}

		HashMap<String, Object> scopeAliasMap = collectScopeTreeTableAliasMap(scopePayload);
		HashMap<String, Object> scopeTableDictionary = getEffectiveScopeTableDictionary(scopePayload);

		HashMap<String, Object> priorNamedRefs = new HashMap<String, Object>();
		HashMap<String, Object> scopeBodyRefs = new HashMap<String, Object>();
		Map<String, Object> priorNamedScopeRefs = getContextListSymbolMap(scopePayload);
		for (Map.Entry<String, Object> entry : qualifiedUnresolved.entrySet()) {
			String tableRef = extractTableRefFromUnresolvedEntry(entry.getKey(), entry.getValue());
			if (isPriorCteAlias(tableRef, priorNamedScopeRefs)) {
				priorNamedRefs.put(entry.getKey(), entry.getValue());
			} else {
				scopeBodyRefs.put(entry.getKey(), entry.getValue());
			}
		}

		final HashMap<String, Object> scopeAliasMapFinal = scopeAliasMap;
		if (!scopeBodyRefs.isEmpty()) {
			emitQualifiedQueryAliasUnresolvedColumnsFatalAndPrune(scopeBodyRefs, scopeAliasMapFinal);
			scopeBodyRefs.entrySet().removeIf(entry -> walker.canFullyResolveQualifiedUnknownInScope(
					entry.getKey(),
					scopeAliasMapFinal,
					scopeTableDictionary,
					walker.queryColumnDictionaryMap));
			resolveQualifiedUnresolvedEntries(scopeBodyRefs, scopeAliasMapFinal, scopeTableDictionary, true);
		}
		if (!priorNamedRefs.isEmpty()) {
			emitQualifiedSourceNotFoundFatals(priorNamedRefs);
		}
	}


	@SuppressWarnings("unchecked")
	private HashMap<String, Object> collectScopeTreeTableAliasMap(HashMap<String, Object> scopeRoot) {
		HashMap<String, Object> merged = new HashMap<String, Object>();
		if (scopeRoot == null) {
			return merged;
		}
		mergeScopeTreeTableAliasMapRecursive(scopeRoot, merged);
		return merged;
	}

	@SuppressWarnings("unchecked")
	private void mergeScopeTreeTableAliasMapRecursive(
			HashMap<String, Object> scopeNode,
			HashMap<String, Object> merged) {
		if (scopeNode == null || scopeNode.isEmpty()) {
			return;
		}

		HashMap<String, Object> aliasMap = getScopeTableAliasMap(scopeNode);
		if (aliasMap != null && !aliasMap.isEmpty()) {
			merged.putAll(aliasMap);
		}

		for (Map.Entry<String, Object> entry : scopeNode.entrySet()) {
			String key = entry.getKey();
			if (key == null
					|| !(isDefinitionScopeKey(key)
							|| key.startsWith(MUMBLE_QUERY_KEY)
							|| key.startsWith(MUMBLE_UNION_KEY)
							|| key.startsWith(MUMBLE_INTERSECT_KEY))) {
				continue;
			}
			if (entry.getValue() instanceof HashMap<?, ?> nestedScopeObj) {
				mergeScopeTreeTableAliasMapRecursive((HashMap<String, Object>) nestedScopeObj, merged);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private boolean isAliasLocalToScopeTree(HashMap<String, Object> scopeRoot, String tableRef) {
		return findNestedScopeOwningAlias(scopeRoot, tableRef) != null;
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> findNestedScopeOwningAlias(
			HashMap<String, Object> scopeRoot,
			String tableRef) {
		if (tableRef == null || tableRef.isBlank() || scopeRoot == null) {
			return null;
		}

		if (isAliasLocalToScope(tableRef, getScopeTableAliasMap(scopeRoot))) {
			return scopeRoot;
		}

		return findInnermostNestedScopeForTableRefRecursive(scopeRoot, tableRef, false);
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> getEffectiveScopeTableDictionary(HashMap<String, Object> scopePayload) {
		HashMap<String, Object> merged = new HashMap<String, Object>();
		if (scopePayload == null) {
			return merged;
		}
		Object tableDictObj = scopePayload.get(MUMBLE_TABLE_DICTIONARY_KEY);
		if (tableDictObj instanceof HashMap<?, ?> tableDictMapObj) {
			merged.putAll((HashMap<String, Object>) tableDictMapObj);
		}
		mergeTargetTableDictionaryFromScopeSymbols(merged, scopePayload);
		return merged;
	}

	@SuppressWarnings("unchecked")
	private void mergeTargetTableDictionaryFromScopeSymbols(
			HashMap<String, Object> merged,
			Map<String, Object> scopeSymbols) {
		if (merged == null || scopeSymbols == null || scopeSymbols.isEmpty()) {
			return;
		}
		Object targetTableObj = scopeSymbols.get(MUMBLE_TARGET_TABLE_KEY);
		if (targetTableObj instanceof HashMap<?, ?> targetTableMapObj) {
			merged.putAll((HashMap<String, Object>) targetTableMapObj);
		}
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> buildEffectiveVisibleAliasMap(HashMap<String, Object> localAliasMap) {
		HashMap<String, Object> effective =
				collectOuterVisibleScope(null, true).aliases;
		if (localAliasMap != null && !localAliasMap.isEmpty()) {
			effective.putAll(localAliasMap);
		}
		return effective;
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> buildEffectiveVisibleTableCollection(HashMap<String, Object> localTableCollection) {
		HashMap<String, Object> effective =
				collectOuterVisibleScope(null, true).tableDictionary;
		if (localTableCollection != null && !localTableCollection.isEmpty()) {
			effective.putAll(localTableCollection);
		}
		return effective;
	}

	/**
	 * Predicate contexts that wrap a parenthesized SELECT (IN list, EXISTS test, scalar predicand).
	 */
	public enum PredicateSubqueryMergeKind {
		IN,
		EXISTS,
		PREDICAND
	}

	/**
	 * Closes a predicate-local symbol-table frame after {@code exitQuery_specification} has already
	 * published the inner SELECT as {@code queryN}.
	 * <p>
	 * Two-store model (do not re-export {@code query_dictionary} here):
	 * <ul>
	 *   <li>Global map: {@code queryColumnDictionaryMap["queryN"]} was written at leaf SELECT finalization.</li>
	 *   <li>Parent symbol table: inner scope is published as {@code def_queryN}; predicate kind stores a
	 *       lightweight pointer ({@code existsN}, {@code predicandN}, or {@code in_listN}) under
	 *       {@link MumbleConstants#MUMBLE_DEPENDENT_QUERIES_KEY} pointing at {@code queryN}.</li>
	 * </ul>
	 * Unresolved entries in the predicate frame are partitioned into outer-correlated vs
	 * subquery-local refs. Outer-correlated entries are resolved or diagnosed against inherited
	 * visible scope (not bubbled to the parent). Subquery-local entries are resolved against the
	 * {@code def_queryN} alias map or discarded (filters already capture them).
	 * This is a merge ({@link SqlASTWalkerHelper#popSymbolTablePutAll}), not a scope publish —
	 * do not call {@link #finalizeQueryScopeSymbolTable} from predicate exits.
	 */
	@SuppressWarnings("unchecked")
	public void exitPredicateSubqueryFrame(
			Map<String, Object> astReference,
			HashMap<String, Object> predicateFrameSymbols,
			PredicateSubqueryMergeKind kind) {
		boolean isSubquery = astReference != null && astReference.containsKey(MUMBLE_SELECT_KEY);
		if (!isSubquery) {
			popPredicateFrameSymbolTableMerge(predicateFrameSymbols);
			return;
		}

		HashMap<String, Object> pendingUnresolved =
				consumePredicateFrameUnresolvedEntries(predicateFrameSymbols);
		String queryRefKey = resolveSubqueryReferenceKeyFromPredicateFrame(predicateFrameSymbols, kind);
		String liveQueryRefKey = normalizeQuerySourceReference(queryRefKey);
		switch (kind) {
			case PREDICAND -> recordDependentQueryReference(
					predicateFrameSymbols, MUMBLE_PREDICAND_KEY, liveQueryRefKey);
			case EXISTS -> recordDependentQueryReference(
					predicateFrameSymbols, MUMBLE_EXISTS_KEY, liveQueryRefKey);
			case IN -> recordDependentQueryReference(
					predicateFrameSymbols, MUMBLE_IN_LIST_KEY, liveQueryRefKey);
		}

		promotePublishedQueryScopeToDefPrefix(predicateFrameSymbols, liveQueryRefKey);
		HashMap<String, Object> defScopePayload = getDefQueryScopePayload(predicateFrameSymbols, liveQueryRefKey);
		HashMap<String, Object> outerCorrelated = new HashMap<String, Object>();
		HashMap<String, Object> innerLocal = new HashMap<String, Object>();
		partitionPredicateUnresolvedByScope(
				pendingUnresolved,
				defScopePayload,
				outerCorrelated,
				innerLocal);
		HashMap<String, Object> deferredForParent = resolveInnerLocalPredicateUnresolved(
				innerLocal,
				defScopePayload);
		walker.mergeUnknownEntries(deferredForParent, outerCorrelated);
		stripUnresolvedFromScopePayloads(predicateFrameSymbols);
		// Predicate frames inherit context-backed aliases for inner resolution only; merging
		// that snapshot onto the parent would overwrite locally registered FROM aliases (e.g. kk).
		predicateFrameSymbols.remove(MUMBLE_TABLE_ALIAS_KEY);
		popPredicateFrameSymbolTableMerge(predicateFrameSymbols);
		mergeUnresolvedEntriesIntoCurrentScope(deferredForParent);
		walker.queryCount++;
	}

	public void pushDependentQueryContextForFrame(ParserRuleContext ctx) {
		dependentQueryContextStack.push(inferDependentQueryContext(ctx));
	}

	public void popDependentQueryContextForFrame() {
		if (!dependentQueryContextStack.isEmpty()) {
			dependentQueryContextStack.pop();
		}
	}

	private String inferDependentQueryContext(ParserRuleContext ctx) {
		for (ParserRuleContext walk = ctx; walk != null; walk = walk.getParent()) {
			int ruleIndex = walk.getRuleIndex();
			if (ruleIndex == SQLSelectParserParser.RULE_select_list
					|| ruleIndex == SQLSelectParserParser.RULE_select_item) {
				return MUMBLE_DEPENDENT_QUERY_CONTEXT_INTERFACE;
			}
			if (ruleIndex == SQLSelectParserParser.RULE_groupby_clause) {
				return MUMBLE_DEPENDENT_QUERY_CONTEXT_GROUP_BY;
			}
			if (ruleIndex == SQLSelectParserParser.RULE_orderby_clause) {
				return MUMBLE_DEPENDENT_QUERY_CONTEXT_ORDER_BY;
			}
			if (ruleIndex == SQLSelectParserParser.RULE_where_clause
					|| ruleIndex == SQLSelectParserParser.RULE_having_clause
					|| ruleIndex == SQLSelectParserParser.RULE_qualify_clause
					|| ruleIndex == SQLSelectParserParser.RULE_search_condition) {
				return MUMBLE_DEPENDENT_QUERY_CONTEXT_FILTERS;
			}
		}
		return MUMBLE_DEPENDENT_QUERY_CONTEXT_FILTERS;
	}

	@SuppressWarnings("unchecked")
	private void recordDependentQueryReference(
			HashMap<String, Object> scopeSymbols,
			String referenceKindKey,
			String queryRefKey) {
		if (scopeSymbols == null || queryRefKey == null || !isQuerySourceReference(queryRefKey)) {
			return;
		}
		String entryKey = referenceKindKey + walker.queryCount;
		HashMap<String, Object> entryPayload = new HashMap<>();
		entryPayload.put(MUMBLE_QUERY_KEY, queryRefKey);
		if (!dependentQueryContextStack.isEmpty()) {
			entryPayload.put(MUMBLE_DEPENDENT_QUERY_TYPE_KEY, dependentQueryContextStack.peek());
		}
		getOrCreateDependentQueriesMap(scopeSymbols).put(entryKey, entryPayload);
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> getOrCreateDependentQueriesMap(HashMap<String, Object> scopeSymbols) {
		Object existing = scopeSymbols.get(MUMBLE_DEPENDENT_QUERIES_KEY);
		if (existing instanceof HashMap<?, ?> map) {
			return (HashMap<String, Object>) map;
		}
		HashMap<String, Object> created = new HashMap<>();
		scopeSymbols.put(MUMBLE_DEPENDENT_QUERIES_KEY, created);
		return created;
	}

	@SuppressWarnings("unchecked")
	private void popPredicateFrameSymbolTableMerge(HashMap<String, Object> predicateFrameSymbols) {
		Object incomingDependent = predicateFrameSymbols.remove(MUMBLE_DEPENDENT_QUERIES_KEY);
		stripFrameLocalWalkTimeKeys(predicateFrameSymbols);
		walker.popSymbolTablePutAll(predicateFrameSymbols);
		if (incomingDependent instanceof Map<?, ?> incoming && !incoming.isEmpty()) {
			getOrCreateDependentQueriesMap(walker.symbolTable).putAll((Map<String, Object>) incoming);
		}
	}

	/**
	 * Locates the {@code queryN} key left in the predicate frame by {@link #finalizeQueryScopeSymbolTable}.
	 * Uses {@link #getSubqueryReferenceKey} with a first-key fallback for all predicate kinds.
	 */
	private String resolveSubqueryReferenceKeyFromPredicateFrame(
			HashMap<String, Object> predicateFrameSymbols,
			PredicateSubqueryMergeKind kind) {
		String queryRefKey = getSubqueryReferenceKey(predicateFrameSymbols);
		if (queryRefKey != null || kind == PredicateSubqueryMergeKind.EXISTS) {
			return queryRefKey;
		}
		if (predicateFrameSymbols != null && !predicateFrameSymbols.isEmpty()) {
			return predicateFrameSymbols.keySet().iterator().next();
		}
		return null;
	}

	/** Renames {@code queryN} to {@code def_queryN} in the predicate frame before merging into the parent. */
	private void promotePublishedQueryScopeToDefPrefix(
			HashMap<String, Object> predicateFrameSymbols,
			String queryRefKey) {
		if (queryRefKey == null || isDefinitionScopeKey(queryRefKey) || !isQuerySourceReference(queryRefKey)) {
			return;
		}
		if (predicateFrameSymbols == null || !predicateFrameSymbols.containsKey(queryRefKey)) {
			return;
		}
		predicateFrameSymbols.put(toDefinitionScopeKey(queryRefKey), predicateFrameSymbols.remove(queryRefKey));
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> getDefQueryScopePayload(
			HashMap<String, Object> predicateFrameSymbols,
			String queryRefKey) {
		if (queryRefKey == null || predicateFrameSymbols == null) {
			return null;
		}
		String defKey = toDefinitionScopeKey(queryRefKey);
		Object payloadObj = predicateFrameSymbols.get(defKey);
		if (payloadObj instanceof HashMap<?, ?>) {
			return (HashMap<String, Object>) payloadObj;
		}
		return null;
	}

	/**
	 * Splits predicate-frame unresolved entries into outer-correlated (bubble to parent) vs
	 * subquery-local (alias exists only inside the {@code def_queryN} body).
	 */
	@SuppressWarnings("unchecked")
	private void partitionPredicateUnresolvedByScope(
			HashMap<String, Object> pendingUnresolved,
			HashMap<String, Object> defScopePayload,
			HashMap<String, Object> outerCorrelated,
			HashMap<String, Object> innerLocal) {
		if (pendingUnresolved == null || pendingUnresolved.isEmpty()) {
			return;
		}
		HashMap<String, Object> innerAliasMap = collectScopeTreeTableAliasMap(defScopePayload);
		for (Map.Entry<String, Object> entry : pendingUnresolved.entrySet()) {
			String unresolvedKey = entry.getKey();
			if (unresolvedKey == null || unresolvedKey.isBlank()) {
				continue;
			}
			String tableRef = extractTableRefFromUnresolvedEntry(unresolvedKey, entry.getValue());
			if (tableRef != null
					&& (isAliasLocalToScope(tableRef, innerAliasMap)
						|| isInheritedCteContextListAlias(tableRef))) {
				innerLocal.put(unresolvedKey, entry.getValue());
			} else {
				outerCorrelated.put(unresolvedKey, entry.getValue());
			}
		}
	}

	private boolean isAliasLocalToScope(String tableRef, HashMap<String, Object> scopeAliasMap) {
		if (tableRef == null || tableRef.isBlank() || scopeAliasMap == null || scopeAliasMap.isEmpty()) {
			return false;
		}
		if (scopeAliasMap.containsKey(tableRef)) {
			return true;
		}
		for (String alias : scopeAliasMap.keySet()) {
			if (alias != null && alias.equalsIgnoreCase(tableRef)) {
				return true;
			}
		}
		for (Object aliasTargetObj : scopeAliasMap.values()) {
			if (aliasTargetObj instanceof String aliasTarget
					&& (aliasTarget.equals(tableRef) || aliasTarget.equalsIgnoreCase(tableRef))) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> getScopeTableAliasMap(HashMap<String, Object> scopePayload) {
		if (scopePayload == null) {
			return null;
		}
		Object aliasObj = scopePayload.get(MUMBLE_TABLE_ALIAS_KEY);
		if (aliasObj instanceof HashMap<?, ?>) {
			return (HashMap<String, Object>) aliasObj;
		}
		return null;
	}

	private String extractTableRefFromUnresolvedEntry(String unresolvedKey, Object unresolvedValue) {
		if (unresolvedKey != null) {
			int dotIndex = unresolvedKey.indexOf('.');
			if (dotIndex > 0) {
				return unresolvedKey.substring(0, dotIndex);
			}
		}
		if (!(unresolvedValue instanceof Map<?, ?> valueMap)) {
			return null;
		}
		Object columnObj = valueMap.get("column");
		if (!(columnObj instanceof Map<?, ?> columnMap)) {
			return null;
		}
		Object tableRefObj = columnMap.get("table_ref");
		return tableRefObj instanceof String tableRef ? tableRef : null;
	}

	private String extractColumnNameFromUnresolvedEntry(String unresolvedKey, Object unresolvedValue) {
		if (unresolvedKey != null) {
			int dotIndex = unresolvedKey.indexOf('.');
			if (dotIndex > 0) {
				return unresolvedKey.substring(dotIndex + 1);
			}
			// No dot means the key is the column name
			return unresolvedKey;
		}
		return null;
	}

	/**
	 * Hoists predicate-local unresolved refs for parent query exit resolution.
	 */
	@SuppressWarnings("unchecked")
	private HashMap<String, Object> resolveInnerLocalPredicateUnresolved(
			HashMap<String, Object> innerLocal,
			HashMap<String, Object> defScopePayload) {
		HashMap<String, Object> deferredForParent = new HashMap<String, Object>();
		if (innerLocal == null || innerLocal.isEmpty()) {
			return deferredForParent;
		}

		HashMap<String, Object> qualifiedUnresolved = new HashMap<String, Object>();
		HashMap<String, Object> unqualifiedUnresolved = new HashMap<String, Object>();
		splitUnresolvedEntriesByQualification(innerLocal, qualifiedUnresolved, unqualifiedUnresolved);

		HashMap<String, Object> innerAliasMap = buildEffectiveVisibleAliasMap(getScopeTableAliasMap(defScopePayload));
		if (!qualifiedUnresolved.isEmpty()) {
			emitQualifiedQueryAliasUnresolvedColumnsFatalAndPrune(qualifiedUnresolved, innerAliasMap);
			deferredForParent.putAll(qualifiedUnresolved);
		}
		if (!unqualifiedUnresolved.isEmpty()) {
			walker.mergeUnknownEntries(deferredForParent, unqualifiedUnresolved);
		}
		return deferredForParent;
	}

	/** Hoists outer-correlated predicate unresolved refs to the parent query exit pipeline. */
	private void stripUnresolvedFromScopePayload(HashMap<String, Object> scopePayload) {
		if (scopePayload == null || scopePayload.isEmpty()) {
			return;
		}
		scopePayload.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
		scopePayload.remove(DEF_ARCHIVED_UNRESOLVED_COLUMN_KEY);
		scopePayload.remove(MUMBLE_INHERITED_VISIBLE_ALIASES_KEY);
		for (Object nestedValue : scopePayload.values()) {
			if (nestedValue instanceof HashMap<?, ?> nestedScopeObj) {
				stripUnresolvedFromScopePayload((HashMap<String, Object>) nestedScopeObj);
			}
		}
	}

	private void stripFrameLocalWalkTimeKeys(HashMap<String, Object> scopePayload) {
		if (scopePayload == null || scopePayload.isEmpty()) {
			return;
		}
		scopePayload.remove(MUMBLE_INHERITED_VISIBLE_ALIASES_KEY);
		scopePayload.remove(MUMBLE_LOCAL_FROM_REGISTERED_ALIASES_KEY);
		scopePayload.remove(TEMP_SET_OPERATION_OPERATOR_ANCHOR_LINE_KEY);
		scopePayload.remove(TEMP_SET_OPERATION_OPERATOR_ANCHOR_CHAR_KEY);
	}

	private void stripWalkTimeKeysFromScopePayload(HashMap<String, Object> scopePayload) {
		if (scopePayload == null || scopePayload.isEmpty()) {
			return;
		}
		stripFrameLocalWalkTimeKeys(scopePayload);
		scopePayload.remove(MUMBLE_OUTER_CONTEXT_LIST_KEY);
		scopePayload.remove(MUMBLE_OUTER_DEF_ENTRIES_KEY);
		scopePayload.remove(MUMBLE_OUTER_TABLE_ALIAS_KEY);
		scopePayload.remove(TEMP_SET_OPERATION_INTERFACE_SUMMARY_MAP_KEY);
		scopePayload.remove(TEMP_QUERY_SET_OPERATION_SUMMARY_KEYS_MAP_KEY);
		scopePayload.remove(TEMP_SET_OPERATION_OPERATOR_ANCHOR_LINE_KEY);
		scopePayload.remove(TEMP_SET_OPERATION_OPERATOR_ANCHOR_CHAR_KEY);
		scopePayload.remove(TEMP_DELETE_TARGET_TABLE_REF_KEY);
		scopePayload.remove(TEMP_DELETE_TARGET_ALIAS_KEY);
		scopePayload.remove(TEMP_INSERT_TARGET_COLUMN_LIST_LOCATION_KEY);
		scopePayload.remove(TEMP_UPDATE_NODEFROM_TARGET_KEY);
		scopePayload.remove(TEMP_UPDATE_NODEFROM_TARGET_TABLE_COLLECTION_KEY);
		scopePayload.remove(TEMP_UPDATE_ASSIGNMENT_RHS_TOKENS_KEY);
	}

	/** Removes walk-time-only symbol-table entries from a published scope tree. */
	@SuppressWarnings("unchecked")
	public void stripWalkTimeKeysFromPublishedScope(HashMap<String, Object> scopePayload) {
		if (scopePayload == null || scopePayload.isEmpty()) {
			return;
		}
		stripWalkTimeKeysFromScopePayload(scopePayload);
		for (Object nestedValue : scopePayload.values()) {
			if (nestedValue instanceof HashMap<?, ?> nestedScopeObj) {
				stripWalkTimeKeysFromPublishedScope((HashMap<String, Object>) nestedScopeObj);
			}
		}
	}

	/** @deprecated use {@link #stripWalkTimeKeysFromPublishedScope(HashMap)} */
	@SuppressWarnings("unchecked")
	public void stripInheritedVisibleAliasesFromPublishedTree(HashMap<String, Object> scopePayload) {
		stripWalkTimeKeysFromPublishedScope(scopePayload);
	}

	@SuppressWarnings("unchecked")
	private void stripUnresolvedFromScopePayloads(HashMap<String, Object> predicateFrameSymbols) {
		if (predicateFrameSymbols == null || predicateFrameSymbols.isEmpty()) {
			return;
		}
		for (Object value : predicateFrameSymbols.values()) {
			if (value instanceof HashMap<?, ?>) {
				stripUnresolvedFromScopePayload((HashMap<String, Object>) value);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> consumePredicateFrameUnresolvedEntries(
			HashMap<String, Object> predicateFrameSymbols) {
		HashMap<String, Object> collected = new HashMap<String, Object>();
		if (predicateFrameSymbols == null || predicateFrameSymbols.isEmpty()) {
			return collected;
		}
		Object active = predicateFrameSymbols.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
		if (active instanceof HashMap<?, ?>) {
			walker.mergeUnknownEntries(collected, (HashMap<String, Object>) active);
		}
		Object archived = predicateFrameSymbols.remove(DEF_ARCHIVED_UNRESOLVED_COLUMN_KEY);
		if (archived instanceof HashMap<?, ?>) {
			walker.mergeUnknownEntries(collected, (HashMap<String, Object>) archived);
		}
		for (Object value : predicateFrameSymbols.values()) {
			if (!(value instanceof HashMap<?, ?> scopePayload)) {
				continue;
			}
			walker.mergeUnknownEntries(collected, extractUnresolvedColumnMapFromScopePayload(
					(HashMap<String, Object>) scopePayload));
			walker.mergeUnknownEntries(collected, extractDefArchivedUnresolved(
					(HashMap<String, Object>) scopePayload));
		}
		return collected;
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> extractUnresolvedColumnMapFromScopePayload(
			HashMap<String, Object> scopeSymbols) {
		if (scopeSymbols == null || scopeSymbols.isEmpty()) {
			return new HashMap<String, Object>();
		}
		Object unresolved = scopeSymbols.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
		if (unresolved instanceof HashMap<?, ?>) {
			return new HashMap<String, Object>((HashMap<String, Object>) unresolved);
		}
		return new HashMap<String, Object>();
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> extractDefArchivedUnresolved(HashMap<String, Object> scopeSymbols) {
		if (scopeSymbols == null || scopeSymbols.isEmpty()) {
			return new HashMap<String, Object>();
		}
		Object archived = scopeSymbols.remove(DEF_ARCHIVED_UNRESOLVED_COLUMN_KEY);
		if (archived instanceof HashMap<?, ?>) {
			return new HashMap<String, Object>((HashMap<String, Object>) archived);
		}
		return new HashMap<String, Object>();
	}

}
