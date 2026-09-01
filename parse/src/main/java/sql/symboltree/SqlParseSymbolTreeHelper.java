package sql.symboltree;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.IdentityHashMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import static mumble.MumbleConstants.*;
import static mumble.ASTWalkerHelperConstants.*;
import static mumble.SQLParserEndPoints.*;

import astwalkers.SqlASTWalkerHelper;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;
import sql.grammar.SqlBareValueExpressionRegistry;
import sql.grammar.SqlBareValueExpressionRegistry.Affinity;
import sql.grammar.SqlGrammarContextClassifier;
import sql.grammar.SqlGrammarDialect;

@SuppressWarnings("Convert2Diamond")
public class SqlParseSymbolTreeHelper {

	private final SqlASTWalkerHelper walker;

	/**
	 * Incremental cache of {@code def_*} entries visible to branches being built within a
	 * set-operation (UNION/INTERSECT) frame.  Avoids O(n²) re-scan of the growing parent
	 * symbol table in {@link #buildConvertEgressScopeBundle} for each successive branch.
	 * <p>
	 * Lifecycle: pushed at {@code enterUnionized_query} / {@code enterIntersected_query},
	 * updated after each {@link #publishQueryLikeScope} call (incrementally adds one entry),
	 * popped at {@code exitUnionized_query} / {@code exitIntersected_query} before finalization.
	 */
	private final ArrayDeque<HashMap<String, Object>> setOpDefinitionPayloadCacheStack =
			new ArrayDeque<>();

	/**
	 * Parallel to {@link #setOpDefinitionPayloadCacheStack}: tracks only the live query-source
	 * keys (those where {@link #isQuerySourceReference} is true) from each cached def_* entry.
	 * Eliminates the O(n) scan of {@code visibleDefinitionPayloads.keySet()} in
	 * {@link #buildConvertEgressScopeBundle}, making it O(1) per branch instead.
	 */
	private final ArrayDeque<LinkedHashSet<String>> setOpLiveQueryRefCacheStack =
			new ArrayDeque<>();

	/**
	 * Convert-egress contract for bare ANSI/dialect value expressions ({@code CURRENT_TIMESTAMP},
	 * {@code SESSION_USER}, …) captured as unqualified column refs during the walk.
	 * <p>
	 * <b>Site 1</b> ({@link #pruneBareValueExpressionsFromUnresolvedMap} at convert egress start)
	 * removes them from {@code localUnresolvedColumnMap}. <b>Site 5</b>
	 * ({@link #consumeLocallyResolvedUnqualifiedBeforeScopePassUp}) prunes again before nested-scope
	 * pass-up. Nothing in between must {@code put} or {@code mergeUnknownEntries} unqualified bare-value
	 * keys back into that map — ref-site handlers (site 2 / clause probe) consume or skip only.
	 * <b>Site 6</b> ({@link #finalizeTopLevelUnresolvedColumns},
	 * {@link #emitUnqualifiedUnresolvedColumnsError}) is the statement-boundary safety net.
	 */

	// Fields moved from SqlParseEventWalker
	private int tableFunctionSourceCount = 0;
	private final Set<String> suppressedAmbiguousUnqualifiedKeys = new HashSet<String>();
	private final Set<String> tableFunctionSourceRefs = new HashSet<String>();
	private final ArrayDeque<String> dependentQueryContextStack = new ArrayDeque<>();
	private final ArrayDeque<String> pendingUnionSetOperatorsForNextParticipants = new ArrayDeque<>();
	private final ArrayDeque<String> pendingIntersectSetOperatorsForNextParticipants = new ArrayDeque<>();
	/** Parse token for a walked {@code column} subtree (clause egress only; never SELECT-list). */
	private final IdentityHashMap<Object, String> clauseColumnSiteTokenBySubTree = new IdentityHashMap<>();
	/**
	 * Per-{@code OVER} partition/order column refs for the next SELECT-list window output (walk order:
	 * partition push, then order push; consumed LIFO on {@code exitSelect_item}).
	 */
	private final ArrayDeque<ArrayList<Object>> pendingWindowSelectInterfacePartitionDeps =
			new ArrayDeque<>();
	private final ArrayDeque<ArrayList<Object>> pendingWithinGroupOrderByDeps =
			new ArrayDeque<>();
	/** One partition+order pair per completed {@code OVER}; consumed on {@code exitSelect_item}. */
	private final ArrayDeque<WindowSelectInterfaceClauseDeps> pendingWindowSelectInterfaceOverDeps =
			new ArrayDeque<>();
	/**
	 * Set when an in-{@code OVER} {@code ORDER BY} finishes; latched on
	 * {@code exitWindow_over_partition_expression} for the following {@code exitSelect_item}.
	 */
	private WindowSelectInterfaceClauseDeps latchedWindowOverClauseDepsForNextSelectItem;
	private String lastWindowSelectListOutputInterfaceAlias;
	private String lastSelectListOutputInterfaceAlias;
	/** M4: unresolved map snapshot at prior {@code exitSelect_item} for per-item site attach. */
	private HashMap<String, Object> unresolvedColumnSnapshotBeforeSelectItem = new HashMap<>();
	private final HashMap<String, WindowSelectInterfaceClauseDeps> windowOutputInterfaceClauseDepsByAlias =
			new LinkedHashMap<>();

	private static final class WindowSelectInterfaceClauseDeps {
		private final ArrayList<Object> partitionByRefs;
		private final ArrayList<Object> orderByRefs;
		private final ArrayList<Object> withinGroupOrderByRefs;

		private WindowSelectInterfaceClauseDeps(
				ArrayList<Object> partitionByRefs,
				ArrayList<Object> orderByRefs,
				ArrayList<Object> withinGroupOrderByRefs) {
			this.partitionByRefs = partitionByRefs != null ? partitionByRefs : new ArrayList<Object>();
			this.orderByRefs = orderByRefs != null ? orderByRefs : new ArrayList<Object>();
			this.withinGroupOrderByRefs =
					withinGroupOrderByRefs != null ? withinGroupOrderByRefs : new ArrayList<Object>();
		}
	}

	/** Phase 15.6: active only during {@link #convertSymbolTableToTableDictionary}. */
	private ConvertEgressScopeBundle activeConvertEgressScopeBundle;
	private HashMap<String, Object> activeConvertEgressDerivedColumns;
	/** Live {@code queryN} scope key for the query_specification being finalized at convert egress. */
	private String activeConvertEgressCurrentQueryScopeKey;
	private RelationalModifierConvertEgressContext activeConvertEgressRelationalModifierContext;
	private HashMap<String, Object> activeConvertEgressRelationalModifierSourceColumns;
	private HashMap<String, Object> activeConvertEgressPivotDerivedSourceBindingsByBucket;
	/** Per convert-egress frame only: column → structured derived bucket keys (PIVOT/UNPIVOT). */
	private HashMap<String, ArrayList<String>> activeConvertEgressStructuredDerivedColumnCandidates;
	/** Select-list output aliases backed by scalar / query-backed subtrees (grounded without physical egress). */
	private HashSet<String> activeConvertEgressScalarSubqueryAliases;
	private ArrayList<String> activeConvertEgressSelectListOutputAliasSourceOrder;

	public SqlParseSymbolTreeHelper(SqlASTWalkerHelper walkerHelper) {
		this.walker = walkerHelper;
	}

	// --- Constants shared with SqlParseEventWalker ---
	public static final String TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY = "_tmp_insert_source_select_sequence";
	public static final String INSERT_SOURCE_REF_KEY = "insert_source_ref";
	public static final String TEMP_INSERT_TARGET_COLUMN_LIST_LOCATION_KEY = "_tmp_insert_target_column_list_location";
	public static final String TEMP_INSERT_TARGET_INTERFACE_KEY = "_tmp_insert_target_interface";
	public static final String TEMP_INSERT_TARGET_TABLE_REF_KEY = "_tmp_insert_target_table_ref";
	public static final String TEMP_DELETE_TARGET_TABLE_REF_KEY = "_tmp_delete_target_table_ref";
	public static final String TEMP_DELETE_TARGET_ALIAS_KEY = "_tmp_delete_target_alias";
	public static final String TEMP_UPDATE_NODEFROM_TARGET_KEY = "_tmp_update_nodefrom_target";
	public static final String TEMP_UPDATE_NODEFROM_TARGET_TABLE_COLLECTION_KEY = "_tmp_update_nodefrom_target_table_collection";
	public static final String TEMP_UPDATE_ASSIGNMENT_RHS_TOKENS_KEY = "_tmp_update_assignment_rhs_tokens";
	public static final String UPDATE_ASSIGNMENT_RHS_CLAUSE_PROBE_KEY = "_update_assignment_rhs_clause_probe";
	public static final String TEMP_SELECT_LIST_OUTPUT_ALIAS_SOURCE_ORDER_KEY =
			"_tmp_select_list_output_alias_source_order";
	private static final String TEMP_SET_OPERATION_INTERFACE_SUMMARY_MAP_KEY =
			SqlASTWalkerHelper.TEMP_SET_OPERATION_INTERFACE_SUMMARY_MAP_KEY;
	private static final String TEMP_QUERY_SET_OPERATION_SUMMARY_KEYS_MAP_KEY =
			SqlASTWalkerHelper.TEMP_QUERY_SET_OPERATION_SUMMARY_KEYS_MAP_KEY;
	private static final String TEMP_SET_OPERATION_OPERATOR_ANCHOR_LINE_KEY =
			SqlASTWalkerHelper.TEMP_SET_OPERATION_OPERATOR_ANCHOR_LINE_KEY;
	private static final String TEMP_SET_OPERATION_OPERATOR_ANCHOR_CHAR_KEY =
			SqlASTWalkerHelper.TEMP_SET_OPERATION_OPERATOR_ANCHOR_CHAR_KEY;
	private static final String TEMP_PENDING_UNION_SETOP_FOR_NEXT_PARTICIPANT_KEY =
			SqlASTWalkerHelper.TEMP_PENDING_UNION_SETOP_FOR_NEXT_PARTICIPANT_KEY;
	private static final String TEMP_PENDING_INTERSECT_SETOP_FOR_NEXT_PARTICIPANT_KEY =
			SqlASTWalkerHelper.TEMP_PENDING_INTERSECT_SETOP_FOR_NEXT_PARTICIPANT_KEY;
	public static final String RELATIONAL_MODIFIER_SOURCE_COLUMNS_KEY = "source_columns";
	public static final String RELATIONAL_MODIFIER_OPERAND_TOKEN_REFS_KEY = "modifier_operand_token_refs";
	/** Depth counter while walking {@code JOIN … USING (column_reference_list)}. */
	public static final String JOIN_USING_COLUMN_LIST_DEPTH_KEY = "join_using_column_list_depth";
	/** Unqualified USING column name → token ref string for resolution/materialization. */
	public static final String JOIN_USING_OPERAND_TOKEN_REFS_KEY = "join_using_operand_token_refs";
	/** Last {@link org.antlr.v4.runtime.Token} per USING column name for qualified unresolved handoff at join exit. */
	public static final String JOIN_USING_OPERAND_TOKEN_BY_NAME_KEY = "join_using_operand_token_by_name";
	/** USING list column names rejected as qualified (must not resolve or appear in filters). */
	public static final String JOIN_USING_DISQUALIFIED_COLUMN_NAMES_KEY = "join_using_disqualified_column_names";
	public static final String RELATIONAL_MODIFIER_OPERAND_REFERENCES_KEY = "relational_modifier_operand_references";
	public static final String RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY = "derived_columns";
	public static final String RELATIONAL_MODIFIER_SOURCE_REF_KEY = "source_ref";
	/** Interface alias (or query scope ref) for the relational modifier's immediate source at finalize. */
	public static final String RELATIONAL_MODIFIER_INTERFACE_SOURCE_REF_KEY = "interface_source_ref";
	/** Parent-scope bucketed PIVOT/UNPIVOT lineage: {@code {derived_columns={...}, source_columns={...}}}. */
	public static final String RELATIONAL_MODIFIER_DERIVATION_KEY = "derivation";
	/** Per-bucket map of PIVOT derived output name → aggregate operand source column (convert interface lineage). */
	public static final String RELATIONAL_MODIFIER_PIVOT_DERIVED_SOURCE_BINDINGS_KEY = "pivot_derived_source_bindings";
	public static final String RELATIONAL_MODIFIER_DERIVED_COLUMN_TOKENS_KEY = "tokens";
	public static final String RELATIONAL_MODIFIER_PIVOT_AGGREGATE_OPERAND_KEY = "pivot_aggregate_operand";

	/**
	 * Archived scope containers holding flat column-ref lists (WHERE/HAVING/QUALIFY/JOIN ON,
	 * GROUP BY, ORDER BY, etc.) validated at scope exit via {@link #probeArchivedScopeClauseColumns}
	 * and included in {@link #forEachConvertEgressUnqualifiedColumnRefSite}.
	 */
	private static final String[] ARCHIVED_SCOPE_COLUMN_REFERENCE_CONTAINER_KEYS = {
			MUMBLE_FILTERS_KEY,
			MUMBLE_GROUPED_BY_KEY,
			MUMBLE_ORDERED_BY_KEY,
			MUMBLE_WINDOW_PARTITION_BY_KEY,
			MUMBLE_WINDOW_ORDERED_BY_KEY,
			MUMBLE_WITHIN_GROUP_ORDERED_BY_KEY,
	};

	/**
	 * True when convert egress for the current query frame has structured PIVOT/UNPIVOT
	 * {@code derivation} state (bucketed derived/source columns). Plain SELECTs without a
	 * relational modifier keep normal clause probe and unqualified resolution on archived
	 * clause lists.
	 */
	private boolean convertEgressScopeHasRelationalModifierStructuredDerivation() {
		if (hasRelationalModifierBucketDerivedColumns(activeConvertEgressDerivedColumns)) {
			return true;
		}
		if (activeConvertEgressRelationalModifierSourceColumns != null
				&& !activeConvertEgressRelationalModifierSourceColumns.isEmpty()) {
			return true;
		}
		RelationalModifierConvertEgressContext modifierContext =
				activeConvertEgressRelationalModifierContext;
		return modifierContext != null && !modifierContext.isEmpty();
	}

	/**
	 * Cross-namespace derived-vs-regular ambiguity applies only when this convert-egress frame
	 * finalized at least one PIVOT/UNPIVOT structured {@code derived_columns} bucket.
	 */
	private boolean isDerivedVersusRegularColumnNamespaceDiagnosticScope() {
		if (!convertEgressScopeHasRelationalModifierStructuredDerivation()) {
			return false;
		}
		return hasRelationalModifierBucketDerivedColumns(activeConvertEgressDerivedColumns);
	}

	/**
	 * Clause / assignment RHS lists that keep walk-captured tuple-qualified modifier refs through
	 * convert egress until structured clause harvest ({@linkplain #ARCHIVED_SCOPE_COLUMN_REFERENCE_CONTAINER_KEYS}
	 * plus UPDATE assignment RHS probe bucket).
	 */
	private boolean defersRelationalModifierClauseHarvestColumnRefList(String containerKey) {
		if (!convertEgressScopeHasRelationalModifierStructuredDerivation()) {
			return false;
		}
		if (containerKey == null || containerKey.isBlank()) {
			return false;
		}
		if (UPDATE_ASSIGNMENT_RHS_CLAUSE_PROBE_KEY.equals(containerKey)) {
			return true;
		}
		for (String archivedKey : ARCHIVED_SCOPE_COLUMN_REFERENCE_CONTAINER_KEYS) {
			if (archivedKey.equals(containerKey)) {
				return true;
			}
		}
		return false;
	}

	/** @deprecated use {@link #ARCHIVED_SCOPE_COLUMN_REFERENCE_CONTAINER_KEYS} */
	private static final String[] SCOPE_CLAUSE_COLUMN_LIST_KEYS = ARCHIVED_SCOPE_COLUMN_REFERENCE_CONTAINER_KEYS;

	// --- normalizeTableRef delegate (mirrors event-walker static helper) ---

	public static String normalizeTableRef(String tableRef) {
		return SqlASTWalkerHelper.normalizeTableReference(tableRef);
	}

	/**
	 * Canonical physical table name for table-dictionary keys (full alias-chain walk).
	 * Returns null for query-backed sources.
	 */
	public String resolveCanonicalPhysicalTableRef(
			String tableRef,
			HashMap<String, Object> tableAliasCollection) {
		return walker.resolveCanonicalPhysicalTableRef(tableRef, tableAliasCollection);
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
		RESOLVED_DERIVED_COLUMN,
		RESOLVED_PIVOT_OPERAND,
		RESOLVED_UNPIVOT_VALUE,
		RESOLVED_UNPIVOT_FOR,
		RESOLVED_UNPIVOT_IN_SOURCE,
		DEFERRED,
		AMBIGUOUS,
		AMBIGUOUS_DERIVED_COLUMN,
		AMBIGUOUS_DERIVED_AND_REGULAR_COLUMN,
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

		static UnqualifiedScopeResolutionResult resolvedDerivedColumn() {
			return new UnqualifiedScopeResolutionResult(
					UnqualifiedScopeResolutionStatus.RESOLVED_DERIVED_COLUMN,
					null,
					null);
		}

		static UnqualifiedScopeResolutionResult resolvedPivotOperand(String materializeTableRef) {
			return new UnqualifiedScopeResolutionResult(
					UnqualifiedScopeResolutionStatus.RESOLVED_PIVOT_OPERAND,
					materializeTableRef,
					null);
		}

		static UnqualifiedScopeResolutionResult resolvedUnpivotValue() {
			return new UnqualifiedScopeResolutionResult(
					UnqualifiedScopeResolutionStatus.RESOLVED_UNPIVOT_VALUE,
					null,
					null);
		}

		static UnqualifiedScopeResolutionResult resolvedUnpivotFor() {
			return new UnqualifiedScopeResolutionResult(
					UnqualifiedScopeResolutionStatus.RESOLVED_UNPIVOT_FOR,
					null,
					null);
		}

		static UnqualifiedScopeResolutionResult resolvedUnpivotInSource(String materializeTableRef) {
			return new UnqualifiedScopeResolutionResult(
					UnqualifiedScopeResolutionStatus.RESOLVED_UNPIVOT_IN_SOURCE,
					materializeTableRef,
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

		static UnqualifiedScopeResolutionResult ambiguousDerivedColumn(String ambiguousSourcesLabel) {
			return new UnqualifiedScopeResolutionResult(
					UnqualifiedScopeResolutionStatus.AMBIGUOUS_DERIVED_COLUMN,
					null,
					ambiguousSourcesLabel);
		}

		static UnqualifiedScopeResolutionResult ambiguousDerivedAndRegularColumn(String ambiguousSourcesLabel) {
			return new UnqualifiedScopeResolutionResult(
					UnqualifiedScopeResolutionStatus.AMBIGUOUS_DERIVED_AND_REGULAR_COLUMN,
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
		RESOLVED_PIVOT_OPERAND,
		RESOLVED_UNPIVOT_VALUE,
		RESOLVED_UNPIVOT_FOR,
		RESOLVED_UNPIVOT_IN_SOURCE,
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

		static QualifiedScopeResolutionResult resolvedPivotOperand(
				String materializeTableRef,
				String sourceTableRef) {
			return new QualifiedScopeResolutionResult(
					QualifiedScopeResolutionStatus.RESOLVED_PIVOT_OPERAND,
					null,
					materializeTableRef,
					sourceTableRef);
		}

		static QualifiedScopeResolutionResult resolvedUnpivotValue(String sourceTableRef) {
			return new QualifiedScopeResolutionResult(
					QualifiedScopeResolutionStatus.RESOLVED_UNPIVOT_VALUE,
					null,
					null,
					sourceTableRef);
		}

		static QualifiedScopeResolutionResult resolvedUnpivotFor(String sourceTableRef) {
			return new QualifiedScopeResolutionResult(
					QualifiedScopeResolutionStatus.RESOLVED_UNPIVOT_FOR,
					null,
					null,
					sourceTableRef);
		}

		static QualifiedScopeResolutionResult resolvedUnpivotInSource(
				String materializeTableRef,
				String sourceTableRef) {
			return new QualifiedScopeResolutionResult(
					QualifiedScopeResolutionStatus.RESOLVED_UNPIVOT_IN_SOURCE,
					null,
					materializeTableRef,
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

	/**
	 * Phase 15.4: bundled scope inputs for {@link #classifyColumnRefAtConvertEgress}.
	 */
	private static final class ConvertEgressResolutionContext {
		final HashMap<String, Object> localDerivedColumns;
		final HashMap<String, Object> localSourceColumnsByBucket;
		final RelationalModifierConvertEgressContext relationalModifierContext;
		final HashMap<String, Object> localPhysicalTableCollection;
		final HashMap<String, Object> localTableCollection;
		final HashMap<String, Object> visibleQuerySourceCollection;
		final HashMap<String, Object> localTableAliasMap;
		final HashMap<String, Object> effectiveAliasMap;
		final HashMap<String, Object> effectiveTableCollection;
		final String deleteTargetTableRef;
		final String clauseProbeKey;
		final boolean treatDerivedRegistryKeysAsDerivedColumn;
		final boolean allowQuerySourceFallback;
		final boolean deferWhenQueryAliasOnlyWithoutParentFatal;
		final boolean deferUnresolvedQualifiedPhysicalSources;
		final boolean resolveQualifiedWhenTableRefPresent;
		/** When set, structured pivot/unpivot derived names used inside another output's expression are not expanded to operand lineage. */
		final String interfaceOutputColumnName;

		private ConvertEgressResolutionContext(
				HashMap<String, Object> localDerivedColumns,
				HashMap<String, Object> localSourceColumnsByBucket,
				RelationalModifierConvertEgressContext relationalModifierContext,
				HashMap<String, Object> localPhysicalTableCollection,
				HashMap<String, Object> localTableCollection,
				HashMap<String, Object> visibleQuerySourceCollection,
				HashMap<String, Object> localTableAliasMap,
				HashMap<String, Object> effectiveAliasMap,
				HashMap<String, Object> effectiveTableCollection,
				String deleteTargetTableRef,
				String clauseProbeKey,
				boolean treatDerivedRegistryKeysAsDerivedColumn,
				boolean allowQuerySourceFallback,
				boolean deferWhenQueryAliasOnlyWithoutParentFatal,
				boolean deferUnresolvedQualifiedPhysicalSources,
				boolean resolveQualifiedWhenTableRefPresent,
				String interfaceOutputColumnName) {
			this.localDerivedColumns = localDerivedColumns;
			this.localSourceColumnsByBucket = localSourceColumnsByBucket;
			this.relationalModifierContext = relationalModifierContext;
			this.localPhysicalTableCollection = localPhysicalTableCollection;
			this.localTableCollection = localTableCollection;
			this.visibleQuerySourceCollection = visibleQuerySourceCollection;
			this.localTableAliasMap = localTableAliasMap;
			this.effectiveAliasMap = effectiveAliasMap;
			this.effectiveTableCollection = effectiveTableCollection;
			this.deleteTargetTableRef = deleteTargetTableRef;
			this.clauseProbeKey = clauseProbeKey;
			this.treatDerivedRegistryKeysAsDerivedColumn = treatDerivedRegistryKeysAsDerivedColumn;
			this.allowQuerySourceFallback = allowQuerySourceFallback;
			this.deferWhenQueryAliasOnlyWithoutParentFatal = deferWhenQueryAliasOnlyWithoutParentFatal;
			this.deferUnresolvedQualifiedPhysicalSources = deferUnresolvedQualifiedPhysicalSources;
			this.resolveQualifiedWhenTableRefPresent = resolveQualifiedWhenTableRefPresent;
			this.interfaceOutputColumnName = interfaceOutputColumnName;
		}
	}

	/**
	 * Phase 15.6: frozen scope visibility built once at convert exit so egress readers do not
	 * repeat {@link #resolveDefinitionSymbolInScopeChain} walks.
	 */
	private static final class ConvertEgressScopeBundle {
		final HashMap<String, Object> visibleDefinitionPayloads;
		final HashMap<String, Object> visibleQuerySourceRefs;
		final HashMap<String, Object> localQueryDictionary;
		final HashMap<String, Object> globalQueryDictionaryRefs;

		private ConvertEgressScopeBundle(
				HashMap<String, Object> visibleDefinitionPayloads,
				HashMap<String, Object> visibleQuerySourceRefs,
				HashMap<String, Object> localQueryDictionary,
				HashMap<String, Object> globalQueryDictionaryRefs) {
			this.visibleDefinitionPayloads = visibleDefinitionPayloads;
			this.visibleQuerySourceRefs = visibleQuerySourceRefs;
			this.localQueryDictionary = localQueryDictionary;
			this.globalQueryDictionaryRefs = globalQueryDictionaryRefs;
		}

		Object getDefinitionPayload(String definitionKey) {
			if (definitionKey == null || definitionKey.isBlank()) {
				return null;
			}
			return visibleDefinitionPayloads.get(definitionKey);
		}

		Object getGlobalQueryDictionary(String liveQueryRef) {
			if (liveQueryRef == null || liveQueryRef.isBlank()) {
				return null;
			}
			return globalQueryDictionaryRefs.get(liveQueryRef);
		}
	}

	/** Phase 15.4: unified convert-egress resolution outcome (derived, qualified, or unqualified). */
	private static final class ConvertEgressColumnResolutionResult {
		private final boolean derivedColumn;
		private final QualifiedScopeResolutionResult qualified;
		private final UnqualifiedScopeResolutionResult unqualified;
		private final ArrayList<Object> expandedDerivedSourceLineage;

		private ConvertEgressColumnResolutionResult(
				boolean derivedColumn,
				QualifiedScopeResolutionResult qualified,
				UnqualifiedScopeResolutionResult unqualified,
				ArrayList<Object> expandedDerivedSourceLineage) {
			this.derivedColumn = derivedColumn;
			this.qualified = qualified;
			this.unqualified = unqualified;
			this.expandedDerivedSourceLineage = expandedDerivedSourceLineage;
		}

		static ConvertEgressColumnResolutionResult derivedColumn() {
			return new ConvertEgressColumnResolutionResult(true, null, null, null);
		}

		static ConvertEgressColumnResolutionResult fromQualified(
				QualifiedScopeResolutionResult qualified) {
			return new ConvertEgressColumnResolutionResult(false, qualified, null, null);
		}

		static ConvertEgressColumnResolutionResult fromUnqualified(
				UnqualifiedScopeResolutionResult unqualified) {
			return new ConvertEgressColumnResolutionResult(false, null, unqualified, null);
		}

		static ConvertEgressColumnResolutionResult fromExpandedDerivedSourceLineage(
				ArrayList<Object> expandedDerivedSourceLineage) {
			return new ConvertEgressColumnResolutionResult(false, null, null, expandedDerivedSourceLineage);
		}

		boolean hasExpandedDerivedSourceLineage() {
			return expandedDerivedSourceLineage != null && !expandedDerivedSourceLineage.isEmpty();
		}

		ArrayList<Object> expandedDerivedSourceLineage() {
			return expandedDerivedSourceLineage;
		}

		boolean isDerivedColumn() {
			if (derivedColumn) {
				return true;
			}
			if (qualified != null
					&& (qualified.status == QualifiedScopeResolutionStatus.RESOLVED_DERIVED_COLUMN
							|| qualified.status == QualifiedScopeResolutionStatus.RESOLVED_UNPIVOT_VALUE
							|| qualified.status == QualifiedScopeResolutionStatus.RESOLVED_UNPIVOT_FOR)) {
				return true;
			}
			return unqualified != null
					&& (unqualified.status == UnqualifiedScopeResolutionStatus.RESOLVED_DERIVED_COLUMN
							|| unqualified.status == UnqualifiedScopeResolutionStatus.RESOLVED_UNPIVOT_VALUE
							|| unqualified.status == UnqualifiedScopeResolutionStatus.RESOLVED_UNPIVOT_FOR);
		}

		boolean isPivotOperandColumn() {
			if (qualified != null
					&& qualified.status == QualifiedScopeResolutionStatus.RESOLVED_PIVOT_OPERAND) {
				return true;
			}
			return unqualified != null
					&& unqualified.status == UnqualifiedScopeResolutionStatus.RESOLVED_PIVOT_OPERAND;
		}

		boolean isUnpivotInSourceColumn() {
			if (qualified != null
					&& qualified.status == QualifiedScopeResolutionStatus.RESOLVED_UNPIVOT_IN_SOURCE) {
				return true;
			}
			return unqualified != null
					&& unqualified.status == UnqualifiedScopeResolutionStatus.RESOLVED_UNPIVOT_IN_SOURCE;
		}

		String pivotOperandMaterializeTableRef() {
			if (qualified != null
					&& (qualified.status == QualifiedScopeResolutionStatus.RESOLVED_PIVOT_OPERAND
							|| qualified.status == QualifiedScopeResolutionStatus.RESOLVED_UNPIVOT_IN_SOURCE)) {
				return qualified.resolvedPhysicalTableRef;
			}
			if (unqualified != null
					&& (unqualified.status == UnqualifiedScopeResolutionStatus.RESOLVED_PIVOT_OPERAND
							|| unqualified.status == UnqualifiedScopeResolutionStatus.RESOLVED_UNPIVOT_IN_SOURCE)) {
				return unqualified.resolvedSourceRef;
			}
			return null;
		}

		QualifiedScopeResolutionResult qualified() {
			return qualified;
		}

		UnqualifiedScopeResolutionResult unqualified() {
			return unqualified;
		}
	}

	// =========================================================================
	// Methods moved from SqlParseEventWalker

	// --- PIVOT/UNPIVOT derivation scope (bucketed derived_columns + source_columns) ---

	static final class RelationalModifierDerivationScopeState {
		final HashMap<String, Object> derivedColumnsByBucket = new HashMap<String, Object>();
		final HashMap<String, Object> sourceColumnsByBucket = new HashMap<String, Object>();
		/** Convert egress only; never published on {@code def_query*}. */
		final HashMap<String, Object> pivotDerivedSourceBindingsByBucket = new HashMap<String, Object>();
		String interfaceSourceRef;
		String dictionaryPhysicalSourceRef;

		boolean hasStructuredBucketDerivation() {
			if (!sourceColumnsByBucket.isEmpty()) {
				return true;
			}
			for (Object bucketObj : derivedColumnsByBucket.values()) {
				if (bucketObj instanceof Map<?, ?>) {
					return true;
				}
			}
			return false;
		}

		boolean isEmpty() {
			return derivedColumnsByBucket.isEmpty()
					&& sourceColumnsByBucket.isEmpty()
					&& pivotDerivedSourceBindingsByBucket.isEmpty()
					&& (interfaceSourceRef == null || interfaceSourceRef.isBlank())
					&& (dictionaryPhysicalSourceRef == null || dictionaryPhysicalSourceRef.isBlank());
		}
	}

	/** UNPIVOT VALUE/FOR names inferred from walk-time {@code derived_columns} map key order (VALUE then FOR). */
	public static final class InferredUnpivotDerivedOutputs {
		public final String valueColumn;
		public final String forColumn;

		InferredUnpivotDerivedOutputs(String valueColumn, String forColumn) {
			this.valueColumn = valueColumn;
			this.forColumn = forColumn;
		}
	}

	/** Structured PIVOT/UNPIVOT state for convert egress (replaces flat {@code ArrayList} hint lists). */
	static final class RelationalModifierConvertEgressContext {
		final String interfaceSourceRef;
		final String dictionaryPhysicalSourceRef;
		final HashMap<String, Object> derivedColumnsByBucket;
		final HashMap<String, Object> sourceColumnsByBucket;

		private RelationalModifierConvertEgressContext(
				String interfaceSourceRef,
				String dictionaryPhysicalSourceRef,
				HashMap<String, Object> derivedColumnsByBucket,
				HashMap<String, Object> sourceColumnsByBucket) {
			this.interfaceSourceRef = interfaceSourceRef;
			this.dictionaryPhysicalSourceRef = dictionaryPhysicalSourceRef;
			this.derivedColumnsByBucket = derivedColumnsByBucket;
			this.sourceColumnsByBucket = sourceColumnsByBucket;
		}

		static RelationalModifierConvertEgressContext from(RelationalModifierDerivationScopeState state) {
			if (state == null) {
				return null;
			}
			return new RelationalModifierConvertEgressContext(
					state.interfaceSourceRef,
					state.dictionaryPhysicalSourceRef,
					new HashMap<String, Object>(state.derivedColumnsByBucket),
					new HashMap<String, Object>(state.sourceColumnsByBucket));
		}

		boolean isEmpty() {
			return (interfaceSourceRef == null || interfaceSourceRef.isBlank())
					&& derivedColumnsByBucket.isEmpty()
					&& sourceColumnsByBucket.isEmpty();
		}

		@SuppressWarnings("unchecked")
		boolean isUnpivot() {
			if (derivedColumnsByBucket.isEmpty() || sourceColumnsByBucket.isEmpty()) {
				return false;
			}
			for (Object bucketObj : derivedColumnsByBucket.values()) {
				if (!(bucketObj instanceof Map<?, ?> bucketMap) || bucketMap.size() != 2) {
					return false;
				}
				if (relationalModifierDerivedBucketLooksLikePivot((Map<String, Object>) bucketMap)) {
					return false;
				}
			}
			return true;
		}

		boolean isPivot() {
			if (derivedColumnsByBucket.isEmpty() || sourceColumnsByBucket.isEmpty()) {
				return false;
			}
			return !isUnpivot();
		}
	}

	private static boolean relationalModifierDerivedBucketLooksLikePivot(Map<String, Object> bucketMap) {
		if (bucketMap == null || bucketMap.isEmpty()) {
			return false;
		}
		if (bucketMap.size() != 2) {
			return bucketMap.size() > 2;
		}
		String firstKey = null;
		String secondKey = null;
		for (String key : bucketMap.keySet()) {
			if (firstKey == null) {
				firstKey = key;
			} else {
				secondKey = key;
				break;
			}
		}
		if (firstKey == null || secondKey == null) {
			return false;
		}
		return shareRelationalModifierPivotAggregateSuffix(firstKey, secondKey);
	}

	private static boolean shareRelationalModifierPivotAggregateSuffix(String left, String right) {
		String leftSuffix = pivotAggregateSuffixFromDerivedColumnName(left);
		String rightSuffix = pivotAggregateSuffixFromDerivedColumnName(right);
		return leftSuffix != null
				&& rightSuffix != null
				&& leftSuffix.equalsIgnoreCase(rightSuffix);
	}

	private static String pivotAggregateSuffixFromDerivedColumnName(String derivedColumnName) {
		if (derivedColumnName == null || derivedColumnName.isBlank()) {
			return null;
		}
		int separatorIndex = derivedColumnName.lastIndexOf('_');
		if (separatorIndex < 0 || separatorIndex >= derivedColumnName.length() - 1) {
			return null;
		}
		return derivedColumnName.substring(separatorIndex + 1);
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> collectUnpivotInColumnNamesFromContext(
			RelationalModifierConvertEgressContext ctx) {
		ArrayList<String> inColumns = new ArrayList<String>();
		if (ctx == null) {
			return inColumns;
		}
		for (Object bucketObj : ctx.sourceColumnsByBucket.values()) {
			if (bucketObj instanceof Map<?, ?> bucketMap) {
				for (Object keyObj : bucketMap.keySet()) {
					if (keyObj instanceof String columnName && !columnName.isBlank()) {
						inColumns.add(columnName);
					}
				}
			} else if (bucketObj instanceof ArrayList<?> sourceRefs) {
				for (Object refObj : sourceRefs) {
					String columnName = walker.extractReferenceNameFromInterfaceEntry(refObj);
					if (columnName != null && !columnName.isBlank()) {
						inColumns.add(columnName);
					}
				}
			}
		}
		return inColumns;
	}

	@SuppressWarnings("unchecked")
	public InferredUnpivotDerivedOutputs inferUnpivotDerivedOutputColumns(Map<String, Object> walkTimeDerivedColumnMap) {
		if (walkTimeDerivedColumnMap == null || walkTimeDerivedColumnMap.isEmpty()) {
			return null;
		}
		ArrayList<String> names = new ArrayList<String>();
		for (Object keyObj : walkTimeDerivedColumnMap.keySet()) {
			if (!(keyObj instanceof String columnName) || columnName.isBlank()) {
				continue;
			}
			names.add(columnName);
		}
		if (names.isEmpty()) {
			return null;
		}
		return new InferredUnpivotDerivedOutputs(
				names.get(0),
				names.size() > 1 ? names.get(1) : null);
	}

	InferredUnpivotDerivedOutputs inferUnpivotDerivedOutputColumnsFromContext(
			RelationalModifierConvertEgressContext ctx) {
		if (ctx == null) {
			return null;
		}
		for (Object bucketObj : ctx.derivedColumnsByBucket.values()) {
			if (bucketObj instanceof Map<?, ?> bucketMap) {
				InferredUnpivotDerivedOutputs inferred = inferUnpivotDerivedOutputColumns(
						(Map<String, Object>) bucketMap);
				if (inferred != null) {
					return inferred;
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> collectPivotOperandColumnNamesFromContext(
			RelationalModifierConvertEgressContext ctx) {
		return collectUnpivotInColumnNamesFromContext(ctx);
	}

	private boolean structuredContextDefinesPivotDerivedOutputColumn(
			RelationalModifierConvertEgressContext ctx,
			String columnName) {
		if (ctx == null || !ctx.isPivot() || columnName == null || columnName.isBlank()) {
			return false;
		}
		for (Object bucketObj : ctx.derivedColumnsByBucket.values()) {
			if (bucketObj instanceof Map<?, ?> bucketMap
					&& containsKeyIgnoreCase((Map<String, Object>) bucketMap, columnName)) {
				return true;
			}
		}
		return false;
	}

	private boolean structuredContextSourceRefMatches(
			RelationalModifierConvertEgressContext ctx,
			String tableRef,
			HashMap<String, Object> visibleAliasMap) {
		if (ctx == null || tableRef == null || tableRef.isBlank()) {
			return false;
		}
		if (ctx.interfaceSourceRef != null && !ctx.interfaceSourceRef.isBlank()) {
			if (derivedColumnSourceRefMatchesTableRef(tableRef, ctx.interfaceSourceRef, visibleAliasMap)) {
				return true;
			}
		}
		if (ctx.dictionaryPhysicalSourceRef != null && !ctx.dictionaryPhysicalSourceRef.isBlank()) {
			return derivedColumnSourceRefMatchesTableRef(
					tableRef,
					ctx.dictionaryPhysicalSourceRef,
					visibleAliasMap);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public RelationalModifierConvertEgressContext peekRelationalModifierConvertContextFromScope(
			HashMap<String, Object> scopeSymbols) {
		if (scopeSymbols == null || scopeSymbols.isEmpty()) {
			return null;
		}
		RelationalModifierDerivationScopeState state = new RelationalModifierDerivationScopeState();
		Object derivationObj = scopeSymbols.get(RELATIONAL_MODIFIER_DERIVATION_KEY);
		if (derivationObj instanceof Map<?, ?> derivationMapObj) {
			Map<String, Object> derivationMap = (Map<String, Object>) derivationMapObj;
			mergeDerivationSubMapIntoState(
					state,
					derivationMap.get(RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY),
					true);
			mergeDerivationSubMapIntoState(
					state,
					derivationMap.get(RELATIONAL_MODIFIER_SOURCE_COLUMNS_KEY),
					false);
			mergeDerivationSubMapIntoState(
					state,
					derivationMap.get(RELATIONAL_MODIFIER_PIVOT_DERIVED_SOURCE_BINDINGS_KEY),
					state.pivotDerivedSourceBindingsByBucket);
			Object interfaceSourceRefObj = derivationMap.get(RELATIONAL_MODIFIER_INTERFACE_SOURCE_REF_KEY);
			if (interfaceSourceRefObj instanceof String interfaceSourceRef
					&& !interfaceSourceRef.isBlank()) {
				state.interfaceSourceRef = interfaceSourceRef;
			}
			Object dictionarySourceRefObj = derivationMap.get(RELATIONAL_MODIFIER_SOURCE_REF_KEY);
			if (dictionarySourceRefObj instanceof String dictionarySourceRef
					&& !dictionarySourceRef.isBlank()) {
				state.dictionaryPhysicalSourceRef = dictionarySourceRef;
			}
		}
		if (state.isEmpty()) {
			Object derivedObj = scopeSymbols.get(RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY);
			if (derivedObj instanceof Map<?, ?> derivedMapObj && !derivedMapObj.isEmpty()) {
				mergeDerivationSubMapIntoState(state, derivedObj, true);
			}
			Object sourceObj = scopeSymbols.get(RELATIONAL_MODIFIER_SOURCE_COLUMNS_KEY);
			if (sourceObj instanceof Map<?, ?> sourceMapObj && !sourceMapObj.isEmpty()) {
				mergeDerivationSubMapIntoState(state, sourceObj, false);
			}
		}
		return RelationalModifierConvertEgressContext.from(state);
	}

	@SuppressWarnings("unchecked")
	public void mergeRelationalModifierDerivationBucketOnParentScope(
			String bucketKey,
			Object derivedBucketValue,
			ArrayList<Object> sourceColumnBucketRefs) {
		mergeRelationalModifierDerivationBucketOnParentScope(
				bucketKey,
				derivedBucketValue,
				sourceColumnBucketRefs,
				null,
				null,
				null);
	}

	@SuppressWarnings("unchecked")
	public void mergeRelationalModifierDerivationBucketOnParentScope(
			String bucketKey,
			Object derivedBucketValue,
			ArrayList<Object> sourceColumnBucketRefs,
			HashMap<String, String> pivotDerivedSourceColumnBindings,
			String interfaceSourceRef,
			String dictionaryPhysicalSourceRef) {
		if (bucketKey == null || bucketKey.isBlank()) {
			return;
		}
		if (derivedBucketValue == null
				&& (sourceColumnBucketRefs == null || sourceColumnBucketRefs.isEmpty())) {
			return;
		}

		HashMap<String, Object> derivationMap = ensureRelationalModifierDerivationMapOnScope(walker.symbolTable);
		if (derivedBucketValue != null) {
			HashMap<String, Object> derivedColumnsMap = ensureRelationalModifierDerivationSubMap(
					derivationMap,
					RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY);
			derivedColumnsMap.put(bucketKey, derivedBucketValue);
		}
		if (sourceColumnBucketRefs != null && !sourceColumnBucketRefs.isEmpty()) {
			HashMap<String, Object> sourceColumnsMap = ensureRelationalModifierDerivationSubMap(
					derivationMap,
					RELATIONAL_MODIFIER_SOURCE_COLUMNS_KEY);
			sourceColumnsMap.put(bucketKey, copyInterfaceReferenceList(sourceColumnBucketRefs));
		}
		if (pivotDerivedSourceColumnBindings != null && !pivotDerivedSourceColumnBindings.isEmpty()) {
			HashMap<String, Object> bindingsMap = ensureRelationalModifierDerivationSubMap(
					derivationMap,
					RELATIONAL_MODIFIER_PIVOT_DERIVED_SOURCE_BINDINGS_KEY);
			bindingsMap.put(bucketKey, new HashMap<String, String>(pivotDerivedSourceColumnBindings));
		}
		if (interfaceSourceRef != null && !interfaceSourceRef.isBlank()) {
			derivationMap.put(RELATIONAL_MODIFIER_INTERFACE_SOURCE_REF_KEY, interfaceSourceRef);
		}
		if (dictionaryPhysicalSourceRef != null && !dictionaryPhysicalSourceRef.isBlank()) {
			derivationMap.put(RELATIONAL_MODIFIER_SOURCE_REF_KEY, dictionaryPhysicalSourceRef);
		}

		walker.symbolTable.remove(RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY);
		walker.symbolTable.remove(RELATIONAL_MODIFIER_SOURCE_COLUMNS_KEY);
		walker.symbolTable.remove(RELATIONAL_MODIFIER_PIVOT_DERIVED_SOURCE_BINDINGS_KEY);
	}

	@SuppressWarnings("unchecked")
	public RelationalModifierDerivationScopeState detachRelationalModifierDerivationFromScope(
			HashMap<String, Object> scopeSymbols) {
		RelationalModifierDerivationScopeState state = new RelationalModifierDerivationScopeState();
		if (scopeSymbols == null || scopeSymbols.isEmpty()) {
			return state;
		}

		Object derivationObj = scopeSymbols.remove(RELATIONAL_MODIFIER_DERIVATION_KEY);
		if (derivationObj instanceof Map<?, ?> derivationMapObj) {
			Map<String, Object> derivationMap = (Map<String, Object>) derivationMapObj;
			mergeDerivationSubMapIntoState(
					state,
					derivationMap.get(RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY),
					true);
			mergeDerivationSubMapIntoState(
					state,
					derivationMap.get(RELATIONAL_MODIFIER_SOURCE_COLUMNS_KEY),
					false);
			mergeDerivationSubMapIntoState(
					state,
					derivationMap.get(RELATIONAL_MODIFIER_PIVOT_DERIVED_SOURCE_BINDINGS_KEY),
					state.pivotDerivedSourceBindingsByBucket);
			Object interfaceSourceRefObj = derivationMap.get(RELATIONAL_MODIFIER_INTERFACE_SOURCE_REF_KEY);
			if (interfaceSourceRefObj instanceof String interfaceSourceRef
					&& !interfaceSourceRef.isBlank()) {
				state.interfaceSourceRef = interfaceSourceRef;
			}
			Object dictionarySourceRefObj = derivationMap.get(RELATIONAL_MODIFIER_SOURCE_REF_KEY);
			if (dictionarySourceRefObj instanceof String dictionarySourceRef
					&& !dictionarySourceRef.isBlank()) {
				state.dictionaryPhysicalSourceRef = dictionarySourceRef;
			}
		}

		mergeDerivationSubMapIntoState(state, scopeSymbols.remove(RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY), true);
		mergeDerivationSubMapIntoState(state, scopeSymbols.remove(RELATIONAL_MODIFIER_SOURCE_COLUMNS_KEY), false);
		mergeDerivationSubMapIntoState(
				state,
				scopeSymbols.remove(RELATIONAL_MODIFIER_PIVOT_DERIVED_SOURCE_BINDINGS_KEY),
				state.pivotDerivedSourceBindingsByBucket);
		return state;
	}

	@SuppressWarnings("unchecked")
	public void publishRelationalModifierDerivationToScope(
			HashMap<String, Object> scopeSymbols,
			RelationalModifierDerivationScopeState state,
			boolean retainWalkTimeHintsForContinuedFrom) {
		if (scopeSymbols == null || state == null || state.isEmpty()) {
			return;
		}

		if (retainWalkTimeHintsForContinuedFrom
				&& state.hasStructuredBucketDerivation()) {
			republishStructuredRelationalModifierDerivationForContinuedFrom(scopeSymbols, state);
			return;
		}

		if (state.hasStructuredBucketDerivation()) {
			HashMap<String, Object> derivationMap = new HashMap<String, Object>();
			if (!state.derivedColumnsByBucket.isEmpty()) {
				derivationMap.put(RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY, state.derivedColumnsByBucket);
			}
			if (!state.sourceColumnsByBucket.isEmpty()) {
				derivationMap.put(RELATIONAL_MODIFIER_SOURCE_COLUMNS_KEY, state.sourceColumnsByBucket);
			}
			// Published derivation on def_query* retains only bucketed derived_columns and
			// source_columns; pivot bindings and source refs stay in convert egress state only.
			if (!derivationMap.isEmpty()) {
				scopeSymbols.put(RELATIONAL_MODIFIER_DERIVATION_KEY, derivationMap);
			}
			return;
		}

		if (!state.derivedColumnsByBucket.isEmpty()) {
			scopeSymbols.put(RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY, state.derivedColumnsByBucket);
		}
		if (!state.sourceColumnsByBucket.isEmpty()) {
			scopeSymbols.put(RELATIONAL_MODIFIER_SOURCE_COLUMNS_KEY, state.sourceColumnsByBucket);
		}
	}

	@SuppressWarnings("unchecked")
	private void republishStructuredRelationalModifierDerivationForContinuedFrom(
			HashMap<String, Object> scopeSymbols,
			RelationalModifierDerivationScopeState state) {
		LinkedHashMap<String, Object> derivedColumnMap = new LinkedHashMap<String, Object>();
		LinkedHashMap<String, Object> sourceColumnMap = new LinkedHashMap<String, Object>();
		for (Object bucketObj : state.derivedColumnsByBucket.values()) {
			if (bucketObj instanceof Map<?, ?> bucketMapObj) {
				derivedColumnMap.putAll((Map<String, Object>) bucketMapObj);
			}
		}
		for (Object bucketObj : state.sourceColumnsByBucket.values()) {
			if (bucketObj instanceof Map<?, ?> bucketMapObj) {
				sourceColumnMap.putAll((Map<String, Object>) bucketMapObj);
			} else if (bucketObj instanceof ArrayList<?> sourceRefs) {
				for (Object refObj : sourceRefs) {
					String columnName = walker.extractReferenceNameFromInterfaceEntry(refObj);
					if (columnName == null || columnName.isBlank()) {
						continue;
					}
					Object tokenPayload = refObj;
					walker.mergeResolvedColumnIntoDictionary(sourceColumnMap, columnName, tokenPayload);
				}
			}
		}
		if (!derivedColumnMap.isEmpty()) {
			scopeSymbols.put(RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY, derivedColumnMap);
		}
		if (!sourceColumnMap.isEmpty()) {
			scopeSymbols.put(RELATIONAL_MODIFIER_SOURCE_COLUMNS_KEY, sourceColumnMap);
		}
		if (state.interfaceSourceRef != null && !state.interfaceSourceRef.isBlank()) {
			scopeSymbols.put(RELATIONAL_MODIFIER_INTERFACE_SOURCE_REF_KEY, state.interfaceSourceRef);
		}
		if (state.dictionaryPhysicalSourceRef != null && !state.dictionaryPhysicalSourceRef.isBlank()) {
			scopeSymbols.put(RELATIONAL_MODIFIER_SOURCE_REF_KEY, state.dictionaryPhysicalSourceRef);
		}
	}

	@SuppressWarnings("unchecked")
	private void mergeDerivationSubMapIntoState(
			RelationalModifierDerivationScopeState state,
			Object subMapObj,
			boolean derivedColumnsTarget) {
		if (state == null || subMapObj == null) {
			return;
		}
		if (!(subMapObj instanceof Map<?, ?> subMapObjCast)) {
			return;
		}
		HashMap<String, Object> targetMap = derivedColumnsTarget
				? state.derivedColumnsByBucket
				: state.sourceColumnsByBucket;
		for (Map.Entry<?, ?> entry : subMapObjCast.entrySet()) {
			if (entry.getKey() instanceof String bucketKey && entry.getValue() != null) {
				targetMap.put(bucketKey, entry.getValue());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void mergeDerivationSubMapIntoState(
			RelationalModifierDerivationScopeState state,
			Object subMapObj,
			HashMap<String, Object> targetBucketMap) {
		if (state == null || subMapObj == null || targetBucketMap == null) {
			return;
		}
		if (!(subMapObj instanceof Map<?, ?> subMapObjCast)) {
			return;
		}
		for (Map.Entry<?, ?> entry : subMapObjCast.entrySet()) {
			if (entry.getKey() instanceof String bucketKey && entry.getValue() != null) {
				targetBucketMap.put(bucketKey, entry.getValue());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> ensureRelationalModifierDerivationMapOnScope(
			HashMap<String, Object> scopeSymbols) {
		Object derivationObj = scopeSymbols.get(RELATIONAL_MODIFIER_DERIVATION_KEY);
		if (derivationObj instanceof HashMap<?, ?> derivationMapObj) {
			return (HashMap<String, Object>) derivationMapObj;
		}
		HashMap<String, Object> derivationMap = new HashMap<String, Object>();
		scopeSymbols.put(RELATIONAL_MODIFIER_DERIVATION_KEY, derivationMap);
		return derivationMap;
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> ensureRelationalModifierDerivationSubMap(
			HashMap<String, Object> derivationMap,
			String subMapKey) {
		Object subMapObj = derivationMap.get(subMapKey);
		if (subMapObj instanceof HashMap<?, ?> subMapObjCast) {
			return (HashMap<String, Object>) subMapObjCast;
		}
		HashMap<String, Object> subMap = new HashMap<String, Object>();
		derivationMap.put(subMapKey, subMap);
		return subMap;
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> getRelationalModifierDerivationSubMapFromScope(
			HashMap<String, Object> scopeSymbols,
			String subMapKey) {
		if (scopeSymbols == null || scopeSymbols.isEmpty() || subMapKey == null) {
			return null;
		}
		Object derivationObj = scopeSymbols.get(RELATIONAL_MODIFIER_DERIVATION_KEY);
		if (derivationObj instanceof Map<?, ?> derivationMapObj) {
			Object subMapObj = derivationMapObj.get(subMapKey);
			if (subMapObj instanceof Map<?, ?> subMapObjCast && !subMapObjCast.isEmpty()) {
				return (HashMap<String, Object>) subMapObjCast;
			}
		}
		Object legacyObj = scopeSymbols.get(subMapKey);
		if (legacyObj instanceof Map<?, ?> legacyMapObj && !legacyMapObj.isEmpty()) {
			return (HashMap<String, Object>) legacyMapObj;
		}
		return null;
	}

	/**
	 * Expands every convert-egress reference that names a structured PIVOT/UNPIVOT derived column
	 * in a modifier bucket to {@code derived@tuple_N} plus that bucket's {@code source_columns} list.
	 * Invoked from {@link #runConvertEgressRelationalModifierDerivedLineagePhaseB} at convert exit.
	 */
	@SuppressWarnings("unchecked")
	public void finalizeRelationalModifierDerivedColumnLineageInClauseLists(
			HashMap<String, Object> localInterface,
			HashMap<String, Object> archivedScopeColumnReferenceContainers,
			Object assignmentsObj,
			HashMap<String, Object> localDerivedColumns,
			HashMap<String, Object> localSourceColumnsByBucket,
			HashMap<String, Object> localTableAliasMap) {
		if (localDerivedColumns == null
				|| localDerivedColumns.isEmpty()
				|| localSourceColumnsByBucket == null
				|| localSourceColumnsByBucket.isEmpty()) {
			return;
		}
		applyWalkCapturedWindowSelectInterfaceClauseDeps(localInterface);
		expandRelationalModifierDerivedColumnLineageInInterfaceMap(
				localInterface,
				localDerivedColumns,
				localSourceColumnsByBucket,
				localTableAliasMap);
		if (archivedScopeColumnReferenceContainers != null) {
			for (String containerKey : ARCHIVED_SCOPE_COLUMN_REFERENCE_CONTAINER_KEYS) {
				expandRelationalModifierDerivedColumnLineageInColumnRefList(
						archivedScopeColumnReferenceContainers.get(containerKey),
						localDerivedColumns,
						localSourceColumnsByBucket,
						localTableAliasMap);
			}
		}
		if (assignmentsObj instanceof Map<?, ?> assignmentsMapObj) {
			for (Object rhsRefsObj : ((Map<String, Object>) assignmentsMapObj).values()) {
				expandRelationalModifierDerivedColumnLineageInColumnRefList(
						rhsRefsObj,
						localDerivedColumns,
						localSourceColumnsByBucket,
						localTableAliasMap);
			}
		}
	}

	/**
	 * Phase 17.7.5b.3: derived-phase lineage expansion on interface, archived clause lists, and
	 * UPDATE assignment RHS — after ambiguous diagnostics, clause probe, and deferred harvest
	 * merge into {@code query_dictionary}, immediately before strip/consolidate.
	 */
	private void runConvertEgressRelationalModifierDerivedLineagePhaseB(
			HashMap<String, Object> localInterface,
			HashMap<String, Object> archivedScopeColumnReferenceContainers,
			Object assignmentsObj,
			HashMap<String, Object> localDerivedColumns,
			HashMap<String, Object> localSourceColumnsByBucket,
			HashMap<String, Object> localTableAliasMap,
			HashMap<String, Object> localUnresolvedColumnMap,
			HashMap<String, Object> localFromTableCollection,
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> visibleQuerySourceCollection,
			HashMap<String, Object> effectiveAliasMap,
			HashMap<String, Object> effectiveTableCollection,
			String deleteTargetTableRef,
			RelationalModifierConvertEgressContext relationalModifierContext) {
		finalizeRelationalModifierDerivedColumnLineageInClauseLists(
				localInterface,
				archivedScopeColumnReferenceContainers,
				assignmentsObj,
				localDerivedColumns,
				localSourceColumnsByBucket,
				localTableAliasMap);
		consumeStructuredRelationalModifierDerivedColumnUnknownsAtConvertEgressPhaseB(
				localInterface,
				archivedScopeColumnReferenceContainers,
				assignmentsObj,
				localUnresolvedColumnMap,
				localDerivedColumns,
				localSourceColumnsByBucket,
				localFromTableCollection,
				localTableCollection,
				visibleQuerySourceCollection,
				localTableAliasMap,
				effectiveAliasMap,
				effectiveTableCollection,
				deleteTargetTableRef,
				relationalModifierContext);
	}

	/**
	 * Phase 17.7.5b.5: single batch consume for structured PIVOT/UNPIVOT derived unknowns after
	 * lineage expansion — ambiguous sites are diagnosed earlier; consume runs only here.
	 */
	@SuppressWarnings("unchecked")
	private void consumeStructuredRelationalModifierDerivedColumnUnknownsAtConvertEgressPhaseB(
			HashMap<String, Object> localInterface,
			HashMap<String, Object> archivedScopeColumnReferenceContainers,
			Object assignmentsObj,
			HashMap<String, Object> localUnresolvedColumnMap,
			HashMap<String, Object> localDerivedColumns,
			HashMap<String, Object> localSourceColumnsByBucket,
			HashMap<String, Object> localFromTableCollection,
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> visibleQuerySourceCollection,
			HashMap<String, Object> localTableAliasMap,
			HashMap<String, Object> effectiveAliasMap,
			HashMap<String, Object> effectiveTableCollection,
			String deleteTargetTableRef,
			RelationalModifierConvertEgressContext relationalModifierContext) {
		if (localUnresolvedColumnMap == null
				|| localUnresolvedColumnMap.isEmpty()
				|| localDerivedColumns == null
				|| localDerivedColumns.isEmpty()) {
			return;
		}

		ConvertEgressResolutionContext phaseBCtx = new ConvertEgressResolutionContext(
				localDerivedColumns,
				localSourceColumnsByBucket,
				relationalModifierContext,
				localFromTableCollection,
				localTableCollection,
				visibleQuerySourceCollection,
				localTableAliasMap,
				effectiveAliasMap,
				effectiveTableCollection,
				deleteTargetTableRef,
				null,
				true,
				true,
				false,
				false,
				false,
				null);

		forEachConvertEgressColumnRefSite(
				localInterface,
				archivedScopeColumnReferenceContainers,
				assignmentsObj,
				(siteKey, refObj, columnName, tableRef) -> {
					if (shouldConsumeStructuredDerivedUnknownAtConvertEgressPhaseB(
							classifyColumnRefAtConvertEgress(columnName, tableRef, phaseBCtx))) {
						consumeDerivedColumnUnknownEntry(
								localUnresolvedColumnMap,
								tableRef,
								columnName);
					}
				});

		for (String unresolvedKey : new ArrayList<String>(localUnresolvedColumnMap.keySet())) {
			if (unresolvedKey == null || unresolvedKey.isBlank()) {
				continue;
			}
			String columnName;
			String tableRef = null;
			if (unresolvedKey.contains(".")) {
				tableRef = unresolvedKey.substring(0, unresolvedKey.lastIndexOf('.'));
				columnName = unresolvedKey.substring(unresolvedKey.lastIndexOf('.') + 1);
			} else {
				columnName = unresolvedKey;
			}
			if (columnName == null || columnName.isBlank()) {
				continue;
			}
			if (shouldConsumeStructuredDerivedUnknownAtConvertEgressPhaseB(
					classifyColumnRefAtConvertEgress(columnName, tableRef, phaseBCtx))) {
				consumeDerivedColumnUnknownEntry(
						localUnresolvedColumnMap,
						tableRef,
						columnName);
			}
		}
	}

	private boolean shouldConsumeStructuredDerivedUnknownAtConvertEgressPhaseB(
			ConvertEgressColumnResolutionResult result) {
		if (result == null) {
			return false;
		}
		if (result.hasExpandedDerivedSourceLineage() || result.isDerivedColumn()) {
			return true;
		}
		return result.unqualified() != null
				&& (result.unqualified().status == UnqualifiedScopeResolutionStatus.AMBIGUOUS_DERIVED_COLUMN
						|| result.unqualified().status
								== UnqualifiedScopeResolutionStatus.AMBIGUOUS_DERIVED_AND_REGULAR_COLUMN);
	}

	/**
	 * Phase 17.7.8: FATAL when an unqualified name is both a structured relational-modifier derived
	 * output and a regular physical / query-backed column in visible scope.
	 */
	@SuppressWarnings("unchecked")
	private void diagnoseDerivedVersusRegularUnqualifiedColumnRefSites(
			HashMap<String, Object> localInterface,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> localUnresolvedColumnMap,
			HashMap<String, Object> localDerivedColumns,
			HashMap<String, Object> archivedScopeColumnReferenceContainers,
			Object updateAssignmentsObj,
			HashMap<String, Object> localPhysicalTableCollection,
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> visibleQuerySourceCollection,
			HashMap<String, Object> localTableAliasMap) {
		if (!isDerivedVersusRegularColumnNamespaceDiagnosticScope()) {
			return;
		}

		HashSet<String> emittedDiagnosticLocations = new HashSet<String>();
		LinkedHashSet<String> diagnosedAmbiguousColumnNames = new LinkedHashSet<String>();

		forEachConvertEgressUnqualifiedColumnRefSite(
				localInterface,
				archivedScopeColumnReferenceContainers,
				updateAssignmentsObj,
				(siteKey, refObj, columnName) -> {
					UnqualifiedScopeResolutionResult cross = tryResolveDerivedVersusRegularColumnNamespaceAmbiguity(
							columnName,
							null,
							localPhysicalTableCollection,
							localTableCollection,
							visibleQuerySourceCollection,
							localTableAliasMap,
							true,
							localDerivedColumns);
					if (cross == null) {
						return;
					}
					diagnosedAmbiguousColumnNames.add(columnName);
					String interfaceDictionaryKey = MUMBLE_INTERFACE_KEY.equals(siteKey) && localInterface != null
							? findKeyIgnoreCase(localInterface, columnName)
							: null;
					Integer[] refLocation;
					if (MUMBLE_INTERFACE_KEY.equals(siteKey)) {
						refLocation = resolveAmbiguousDerivedColumnRefSiteLocation(
								refObj,
								columnName,
								localUnresolvedColumnMap,
								localCurrentQueryDictionary,
								interfaceDictionaryKey,
								emittedDiagnosticLocations);
					} else {
						refLocation = resolveAmbiguousDerivedColumnRefSiteLocation(
								refObj,
								columnName,
								localUnresolvedColumnMap,
								null,
								null,
								emittedDiagnosticLocations);
					}
					emitAmbiguousDerivedAndRegularColumnReferenceFatalIfNew(
							columnName,
							refLocation,
							cross.ambiguousSourcesLabel,
							emittedDiagnosticLocations);
				});

		for (String columnName : diagnosedAmbiguousColumnNames) {
			UnqualifiedScopeResolutionResult cross = tryResolveDerivedVersusRegularColumnNamespaceAmbiguity(
					columnName,
					null,
					localPhysicalTableCollection,
					localTableCollection,
					visibleQuerySourceCollection,
					localTableAliasMap,
					true,
					localDerivedColumns);
			if (cross == null) {
				continue;
			}
			emitAmbiguousDerivedAndRegularColumnReferenceFatalsFromUnresolvedLocations(
					columnName,
					localUnresolvedColumnMap,
					cross.ambiguousSourcesLabel,
					null,
					emittedDiagnosticLocations);
		}
	}

	private void emitAmbiguousDerivedAndRegularColumnReferenceFatalsFromUnresolvedLocations(
			String columnName,
			HashMap<String, Object> localUnresolvedColumnMap,
			String combinedAmbiguityLabel,
			Integer[] excludedLocation,
			HashSet<String> emittedDiagnosticLocations) {
		Object unresolvedEntry = getUnqualifiedUnknownEntry(localUnresolvedColumnMap, columnName);
		if (!(unresolvedEntry instanceof Map<?, ?> unresolvedMap)) {
			return;
		}
		Object locationsObj = unresolvedMap.get("locations");
		if (!(locationsObj instanceof ArrayList<?> locations) || locations.isEmpty()) {
			return;
		}
		for (Object locationObj : locations) {
			if (locationObj == null) {
				continue;
			}
			Integer[] location = walker.parseLineAndCharacterFromToken(locationObj.toString());
			if (location == null || location.length < 2 || location[0] == null) {
				continue;
			}
			if (excludedLocation != null
					&& excludedLocation.length >= 2
					&& excludedLocation[0] != null
					&& excludedLocation[0].equals(location[0])
					&& Objects.equals(excludedLocation[1], location[1])) {
				continue;
			}
			emitAmbiguousDerivedAndRegularColumnReferenceFatalIfNew(
					columnName,
					location,
					combinedAmbiguityLabel,
					emittedDiagnosticLocations);
		}
	}

	/**
	 * Merges UNPIVOT/PIVOT FOR/VALUE definition tokens from structured {@code derived_columns}
	 * buckets onto {@code query_dictionary} for names that are query interface outputs.
	 * SELECT-list origins are phase-1 ({@code exitSelect_item}); this is the modifier-definition
	 * counterpart to {@link #recordInterfaceOutputClauseRefOnQueryDictionary} for clause usages.
	 */
	/**
	 * Emits {@link SqlASTWalkerHelper#DIAG_SQL_AMBIGUOUS_DERIVED_COLUMN_REFERENCE} for every
	 * unqualified convert-egress column ref site (SELECT interface, filters, GROUP BY, ORDER BY,
	 * UPDATE assignment RHS, etc.) whose name is an ambiguous structured derived column.
	 */
	@SuppressWarnings("unchecked")
	private void diagnoseAmbiguousUnqualifiedRelationalModifierDerivedColumnRefSites(
			HashMap<String, Object> localInterface,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> localUnresolvedColumnMap,
			HashMap<String, Object> localDerivedColumns,
			HashMap<String, Object> archivedScopeColumnReferenceContainers,
			Object updateAssignmentsObj,
			HashMap<String, Object> localPhysicalTableCollection,
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> visibleQuerySourceCollection,
			HashMap<String, Object> localTableAliasMap) {
		if (localDerivedColumns == null || localDerivedColumns.isEmpty()) {
			return;
		}

		HashSet<String> emittedDiagnosticLocations = new HashSet<String>();
		LinkedHashSet<String> diagnosedAmbiguousColumnNames = new LinkedHashSet<String>();

		forEachConvertEgressUnqualifiedColumnRefSite(
				localInterface,
				archivedScopeColumnReferenceContainers,
				updateAssignmentsObj,
				(siteKey, refObj, columnName) -> {
					if (!isAmbiguousUnqualifiedStructuredDerivedColumn(
							columnName,
							null,
							localDerivedColumns)) {
						return;
					}
					if (tryResolveDerivedVersusRegularColumnNamespaceAmbiguity(
							columnName,
							null,
							localPhysicalTableCollection,
							localTableCollection,
							visibleQuerySourceCollection,
							localTableAliasMap,
							true,
							localDerivedColumns) != null) {
						return;
					}
					if (MUMBLE_INTERFACE_KEY.equals(siteKey)
							&& localInterface != null
							&& countUnqualifiedInterfaceRefsForColumnName(localInterface, columnName)
									< countUnresolvedColumnRefSiteLocations(
											localUnresolvedColumnMap,
											columnName)) {
						diagnosedAmbiguousColumnNames.add(columnName);
						return;
					}
					diagnosedAmbiguousColumnNames.add(columnName);
					ArrayList<String> derivedBuckets = collectRelationalModifierStructuredDerivedColumnBucketKeys(
							columnName,
							localDerivedColumns);
					String ambiguitySources = formatRelationalModifierDerivedColumnAmbiguitySources(
							derivedBuckets);
					String interfaceDictionaryKey = MUMBLE_INTERFACE_KEY.equals(siteKey) && localInterface != null
							? findKeyIgnoreCase(localInterface, columnName)
							: null;
					Integer[] refLocation;
					if (MUMBLE_INTERFACE_KEY.equals(siteKey)) {
						refLocation = resolveAmbiguousDerivedColumnRefSiteLocation(
								refObj,
								columnName,
								localUnresolvedColumnMap,
								localCurrentQueryDictionary,
								interfaceDictionaryKey,
								emittedDiagnosticLocations);
					} else {
						refLocation = resolveAmbiguousDerivedColumnRefSiteLocation(
								refObj,
								columnName,
								localUnresolvedColumnMap,
								null,
								null,
								emittedDiagnosticLocations);
					}
					emitAmbiguousDerivedColumnReferenceFatalIfNew(
							columnName,
							refLocation,
							ambiguitySources,
							emittedDiagnosticLocations);
				});

		for (String columnName : diagnosedAmbiguousColumnNames) {
			ArrayList<String> derivedBuckets = collectRelationalModifierStructuredDerivedColumnBucketKeys(
					columnName,
					localDerivedColumns);
			String ambiguitySources = formatRelationalModifierDerivedColumnAmbiguitySources(
					derivedBuckets);
			emitAmbiguousDerivedColumnReferenceFatalsFromUnresolvedLocations(
					columnName,
					localUnresolvedColumnMap,
					ambiguitySources,
					null,
					emittedDiagnosticLocations);
		}
	}

	/**
	 * Phase 17.7.10: outside the modifier phrase, warn when a structured derived output is qualified
	 * with the source-primary alias ({@code p_src}) instead of the modifier alias ({@code p}).
	 */
	@SuppressWarnings("unchecked")
	private void diagnoseRelationalModifierDerivedReferenceWithSourcePrimaryAlias(
			HashMap<String, Object> localInterface,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> archivedScopeColumnReferenceContainers,
			Object updateAssignmentsObj,
			HashMap<String, Object> localUnresolvedColumnMap,
			HashMap<String, Object> localDerivedColumns,
			HashMap<String, Object> localRelationalModifierSourceColumns,
			HashMap<String, Object> localTableAliasMap) {
		if (localDerivedColumns == null
				|| localDerivedColumns.isEmpty()
				|| localTableAliasMap == null
				|| localTableAliasMap.isEmpty()) {
			return;
		}

		HashSet<String> emittedDiagnosticLocations = new HashSet<String>();
		forEachConvertEgressColumnRefSite(
				localInterface,
				archivedScopeColumnReferenceContainers,
				updateAssignmentsObj,
				(siteKey, refObj, columnName, tableRef) -> {
					if (MUMBLE_INTERFACE_KEY.equals(siteKey)) {
						return;
					}
					if (columnName == null
							|| columnName.isBlank()
							|| isUnqualifiedColumnRef(tableRef)) {
						return;
					}
					String matchedBucketKey = findStructuredDerivedColumnBucketForSourcePrimaryQualifier(
							columnName,
							tableRef,
							localDerivedColumns,
							localRelationalModifierSourceColumns,
							localTableAliasMap);
					if (matchedBucketKey == null) {
						return;
					}
					String interfaceDictionaryKey = MUMBLE_INTERFACE_KEY.equals(siteKey) && localInterface != null
							? findKeyIgnoreCase(localInterface, columnName)
							: null;
					Integer[] refLocation = resolveRelationalModifierDerivedSourcePrimaryAliasRefSiteLocation(
							refObj,
							columnName,
							tableRef,
							localUnresolvedColumnMap,
							localCurrentQueryDictionary,
							interfaceDictionaryKey,
							emittedDiagnosticLocations);
					emitRelationalModifierDerivedReferenceUseModifierAliasWarningIfNew(
							columnName,
							tableRef,
							matchedBucketKey,
							refLocation,
							emittedDiagnosticLocations);
				});
	}

	private String findStructuredDerivedColumnBucketForSourcePrimaryQualifier(
			String columnName,
			String tableRef,
			HashMap<String, Object> localDerivedColumns,
			HashMap<String, Object> localSourceColumnsByBucket,
			HashMap<String, Object> localTableAliasMap) {
		if (columnName == null
				|| columnName.isBlank()
				|| tableRef == null
				|| tableRef.isBlank()
				|| localDerivedColumns == null
				|| localDerivedColumns.isEmpty()) {
			return null;
		}

		String matchedBucketKey = null;
		for (Map.Entry<String, Object> bucketEntry : localDerivedColumns.entrySet()) {
			String bucketKey = bucketEntry.getKey();
			if (bucketKey == null || bucketKey.isBlank()) {
				continue;
			}
			Object bucketObj = bucketEntry.getValue();
			if (!(bucketObj instanceof Map<?, ?> bucketMap)
					|| !isRelationalModifierStructuredDerivedColumnBucket((Map<String, Object>) bucketMap)
					|| !containsKeyIgnoreCase((Map<String, Object>) bucketMap, columnName)) {
				continue;
			}
			String sourcePrimaryAlias = resolveRelationalModifierSourcePrimaryAliasForBucket(
					bucketKey,
					localSourceColumnsByBucket,
					localTableAliasMap);
			if (sourcePrimaryAlias == null
					|| !sourcePrimaryAlias.equalsIgnoreCase(tableRef)
					|| bucketKey.equalsIgnoreCase(tableRef)) {
				continue;
			}
			if (matchedBucketKey != null && !matchedBucketKey.equalsIgnoreCase(bucketKey)) {
				return null;
			}
			matchedBucketKey = bucketKey;
		}
		return matchedBucketKey;
	}

	private String resolveRelationalModifierSourcePrimaryAliasForBucket(
			String modifierBucketKey,
			HashMap<String, Object> localSourceColumnsByBucket,
			HashMap<String, Object> localTableAliasMap) {
		if (modifierBucketKey == null || modifierBucketKey.isBlank()) {
			return null;
		}
		if (localTableAliasMap != null && !localTableAliasMap.isEmpty()) {
			String mapKey = findKeyIgnoreCase(localTableAliasMap, modifierBucketKey);
			if (mapKey != null) {
				Object aliasTargetObj = localTableAliasMap.get(mapKey);
				if (aliasTargetObj instanceof String aliasTarget
						&& !aliasTarget.isBlank()
						&& !aliasTarget.equalsIgnoreCase(modifierBucketKey)) {
					return aliasTarget;
				}
			}
		}
		if (localSourceColumnsByBucket == null || localSourceColumnsByBucket.isEmpty()) {
			return null;
		}
		String bucketMapKey = findKeyIgnoreCase(localSourceColumnsByBucket, modifierBucketKey);
		if (bucketMapKey == null) {
			return null;
		}
		Object refsObj = localSourceColumnsByBucket.get(bucketMapKey);
		if (!(refsObj instanceof ArrayList<?> refs) || refs.isEmpty()) {
			return null;
		}
		String sourceTableRef = walker.extractReferenceTableRefFromInterfaceEntry(refs.get(0));
		if (sourceTableRef == null
				|| sourceTableRef.isBlank()
				|| sourceTableRef.equalsIgnoreCase(modifierBucketKey)) {
			return null;
		}
		return sourceTableRef;
	}

	private Integer[] resolveRelationalModifierDerivedSourcePrimaryAliasRefSiteLocation(
			Object refObj,
			String columnName,
			String tableRef,
			HashMap<String, Object> localUnresolvedColumnMap,
			HashMap<String, Object> localCurrentQueryDictionary,
			String interfaceDictionaryKey,
			HashSet<String> emittedDiagnosticLocations) {
		if (!isUnqualifiedColumnRef(tableRef)) {
			Integer[] qualifiedLocation = firstLocationFromUnresolvedUnknownEntry(
					peekQualifiedUnknownEntry(localUnresolvedColumnMap, tableRef, columnName));
			if (qualifiedLocation != null && qualifiedLocation.length >= 2 && qualifiedLocation[0] != null) {
				return qualifiedLocation;
			}
			Object globalQualifiedEntry = walker.getCapturedQualifiedUnresolvedLocationEntry(
					tableRef + "." + columnName);
			qualifiedLocation = firstLocationFromUnresolvedUnknownEntry(globalQualifiedEntry);
			if (qualifiedLocation != null && qualifiedLocation.length >= 2 && qualifiedLocation[0] != null) {
				return qualifiedLocation;
			}
		}

		Integer[] refLocation = resolveAmbiguousDerivedColumnRefSiteLocation(
				refObj,
				columnName,
				localUnresolvedColumnMap,
				localCurrentQueryDictionary,
				interfaceDictionaryKey,
				emittedDiagnosticLocations);
		if (refLocation != null && refLocation.length >= 2 && refLocation[0] != null) {
			return refLocation;
		}
		return new Integer[] { null, null };
	}

	private Object peekQualifiedUnknownEntry(
			HashMap<String, Object> unresolvedColumnMap,
			String tableRef,
			String columnName) {
		if (unresolvedColumnMap == null
				|| unresolvedColumnMap.isEmpty()
				|| tableRef == null
				|| tableRef.isBlank()
				|| columnName == null
				|| columnName.isBlank()) {
			return null;
		}

		String directKey = tableRef + "." + columnName;
		if (unresolvedColumnMap.containsKey(directKey)) {
			return unresolvedColumnMap.get(directKey);
		}
		for (Map.Entry<String, Object> entry : unresolvedColumnMap.entrySet()) {
			String key = entry.getKey();
			if (key == null || !key.contains(".")) {
				continue;
			}
			int separatorIndex = key.lastIndexOf('.');
			if (separatorIndex <= 0 || separatorIndex + 1 >= key.length()) {
				continue;
			}
			String keyTableRef = key.substring(0, separatorIndex);
			String keyColumnName = key.substring(separatorIndex + 1);
			if (keyTableRef.equalsIgnoreCase(tableRef) && keyColumnName.equalsIgnoreCase(columnName)) {
				return entry.getValue();
			}
		}
		return null;
	}

	private Integer[] firstLocationFromUnresolvedUnknownEntry(Object unresolvedEntry) {
		if (!(unresolvedEntry instanceof Map<?, ?> unresolvedMap)) {
			return new Integer[] { null, null };
		}
		Object locationsObj = unresolvedMap.get("locations");
		if (!(locationsObj instanceof ArrayList<?> locations) || locations.isEmpty()) {
			return new Integer[] { null, null };
		}
		for (Object locationObj : locations) {
			if (locationObj == null) {
				continue;
			}
			Integer[] location = walker.parseLineAndCharacterFromToken(locationObj.toString());
			if (location != null && location.length >= 2 && location[0] != null) {
				return location;
			}
		}
		return new Integer[] { null, null };
	}

	private void emitRelationalModifierDerivedReferenceUseModifierAliasWarningIfNew(
			String columnName,
			String sourcePrimaryAlias,
			String modifierAlias,
			Integer[] refLocation,
			HashSet<String> emittedDiagnosticLocations) {
		String locationKey = formatAmbiguousDerivedDiagnosticLocationKey(refLocation);
		if (locationKey == null) {
			return;
		}
		if (emittedDiagnosticLocations != null && !emittedDiagnosticLocations.add(locationKey)) {
			return;
		}

		String diagCode = walker.getDiagnosticCode(
				SqlASTWalkerHelper.DIAG_SQL_RELATIONAL_MODIFIER_DERIVED_REFERENCE_USE_MODIFIER_ALIAS);
		String diagTemplate = walker.getDiagnosticMessage(
				SqlASTWalkerHelper.DIAG_SQL_RELATIONAL_MODIFIER_DERIVED_REFERENCE_USE_MODIFIER_ALIAS);
		String diagMessage = (diagTemplate == null)
				? String.format(
						"Derived column '%s' qualified with source alias '%s' at (l:%s c:%s); use relational modifier alias '%s' instead.",
						columnName,
						sourcePrimaryAlias,
						refLocation[0],
						refLocation[1],
						modifierAlias)
				: String.format(
						diagTemplate,
						columnName,
						sourcePrimaryAlias,
						refLocation[0],
						refLocation[1],
						modifierAlias);

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

	private void diagnoseAmbiguousUnqualifiedRelationalModifierSourceOperandRefSites(
			HashMap<String, Object> localInterface,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> localUnresolvedColumnMap,
			HashMap<String, Object> localSourceColumnsByBucket,
			HashMap<String, Object> archivedScopeColumnReferenceContainers,
			Object updateAssignmentsObj) {
		if (localSourceColumnsByBucket == null || localSourceColumnsByBucket.isEmpty()) {
			return;
		}

		HashSet<String> emittedDiagnosticLocations = new HashSet<String>();
		LinkedHashSet<String> diagnosedAmbiguousColumnNames = new LinkedHashSet<String>();

		if (localInterface != null && !localInterface.isEmpty()) {
			for (String interfaceOutputName : localInterface.keySet()) {
				if (interfaceOutputName == null || interfaceOutputName.isBlank()) {
					continue;
				}
				if (!isAmbiguousUnqualifiedRelationalModifierSourceOperandColumn(
						interfaceOutputName,
						null,
						localSourceColumnsByBucket)) {
					continue;
				}
				diagnosedAmbiguousColumnNames.add(interfaceOutputName);
				ArrayList<String> sourceBuckets = collectRelationalModifierSourceOperandBucketKeys(
						interfaceOutputName,
						localSourceColumnsByBucket);
				String ambiguitySources = formatRelationalModifierDerivedColumnAmbiguitySources(
						sourceBuckets);
				String interfaceDictionaryKey = findKeyIgnoreCase(localInterface, interfaceOutputName);
				Integer[] refLocation = resolveAmbiguousDerivedColumnRefSiteLocation(
						null,
						interfaceOutputName,
						localUnresolvedColumnMap,
						localCurrentQueryDictionary,
						interfaceDictionaryKey,
						emittedDiagnosticLocations);
				emitAmbiguousUnqualifiedColumnDiagnosticIfNew(
						interfaceOutputName,
						refLocation,
						ambiguitySources,
						emittedDiagnosticLocations);
			}
		}

		forEachConvertEgressUnqualifiedColumnRefSite(
				localInterface,
				archivedScopeColumnReferenceContainers,
				updateAssignmentsObj,
				(siteKey, refObj, columnName) -> {
					if (MUMBLE_INTERFACE_KEY.equals(siteKey)) {
						return;
					}
					if (!isAmbiguousUnqualifiedRelationalModifierSourceOperandColumn(
							columnName,
							null,
							localSourceColumnsByBucket)) {
						return;
					}
					diagnosedAmbiguousColumnNames.add(columnName);
					ArrayList<String> sourceBuckets = collectRelationalModifierSourceOperandBucketKeys(
							columnName,
							localSourceColumnsByBucket);
					String ambiguitySources = formatRelationalModifierDerivedColumnAmbiguitySources(
							sourceBuckets);
					String interfaceDictionaryKey = MUMBLE_INTERFACE_KEY.equals(siteKey) && localInterface != null
							? findKeyIgnoreCase(localInterface, columnName)
							: null;
					Integer[] refLocation;
					if (MUMBLE_INTERFACE_KEY.equals(siteKey)) {
						refLocation = resolveAmbiguousDerivedColumnRefSiteLocation(
								refObj,
								columnName,
								localUnresolvedColumnMap,
								localCurrentQueryDictionary,
								interfaceDictionaryKey,
								emittedDiagnosticLocations);
					} else {
						refLocation = resolveAmbiguousDerivedColumnRefSiteLocation(
								refObj,
								columnName,
								localUnresolvedColumnMap,
								null,
								null,
								emittedDiagnosticLocations);
					}
					emitAmbiguousUnqualifiedColumnDiagnosticIfNew(
							columnName,
							refLocation,
							ambiguitySources,
							emittedDiagnosticLocations);
				});

		for (String columnName : diagnosedAmbiguousColumnNames) {
			ArrayList<String> sourceBuckets = collectRelationalModifierSourceOperandBucketKeys(
					columnName,
					localSourceColumnsByBucket);
			String ambiguitySources = formatRelationalModifierDerivedColumnAmbiguitySources(
					sourceBuckets);
			emitAmbiguousUnqualifiedColumnDiagnosticsFromUnresolvedLocations(
					columnName,
					localUnresolvedColumnMap,
					ambiguitySources,
					null,
					emittedDiagnosticLocations);
		}
	}

	@FunctionalInterface
	private interface ConvertEgressUnqualifiedColumnRefSiteVisitor {
		void visit(String siteKey, Object refObj, String columnName);
	}

	@SuppressWarnings("unchecked")
	private void forEachConvertEgressUnqualifiedColumnRefSite(
			HashMap<String, Object> localInterface,
			HashMap<String, Object> archivedScopeColumnReferenceContainers,
			Object updateAssignmentsObj,
			ConvertEgressUnqualifiedColumnRefSiteVisitor visitor) {
		if (visitor == null) {
			return;
		}

		if (localInterface != null && !localInterface.isEmpty()) {
			for (Map.Entry<String, Object> interfaceEntry : localInterface.entrySet()) {
				String interfaceKey = interfaceEntry.getKey();
				if (interfaceKey == null || interfaceKey.isBlank()) {
					continue;
				}
				Object refsObj = interfaceEntry.getValue();
				if (!(refsObj instanceof ArrayList<?> refs)) {
					continue;
				}
				for (Object refObj : (ArrayList<Object>) refs) {
					visitConvertEgressUnqualifiedColumnRefSite(
							visitor,
							MUMBLE_INTERFACE_KEY,
							refObj);
				}
			}
		}

		if (archivedScopeColumnReferenceContainers != null) {
			for (String containerKey : ARCHIVED_SCOPE_COLUMN_REFERENCE_CONTAINER_KEYS) {
				visitConvertEgressUnqualifiedColumnRefList(
						visitor,
						containerKey,
						archivedScopeColumnReferenceContainers.get(containerKey));
			}
		}

		if (updateAssignmentsObj instanceof Map<?, ?> assignmentsMapObj) {
			for (Object rhsRefsObj : ((Map<String, Object>) assignmentsMapObj).values()) {
				visitConvertEgressUnqualifiedColumnRefList(
						visitor,
						UPDATE_ASSIGNMENT_RHS_CLAUSE_PROBE_KEY,
						rhsRefsObj);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void visitConvertEgressUnqualifiedColumnRefList(
			ConvertEgressUnqualifiedColumnRefSiteVisitor visitor,
			String siteKey,
			Object columnListObj) {
		if (!(columnListObj instanceof ArrayList<?> columnRefsObj)) {
			return;
		}
		for (Object refObj : (ArrayList<Object>) columnRefsObj) {
			visitConvertEgressUnqualifiedColumnRefSite(visitor, siteKey, refObj);
		}
	}

	private void visitConvertEgressUnqualifiedColumnRefSite(
			ConvertEgressUnqualifiedColumnRefSiteVisitor visitor,
			String siteKey,
			Object refObj) {
		if (visitor == null || refObj == null) {
			return;
		}
		String columnName = walker.extractReferenceNameFromInterfaceEntry(refObj);
		String tableRef = walker.extractReferenceTableRefFromInterfaceEntry(refObj);
		if (columnName == null
				|| columnName.isBlank()
				|| !isUnqualifiedColumnRef(tableRef)) {
			return;
		}
		visitor.visit(siteKey, refObj, columnName);
	}

	@FunctionalInterface
	private interface ConvertEgressColumnRefSiteVisitor {
		void visit(String siteKey, Object refObj, String columnName, String tableRef);
	}

	@SuppressWarnings("unchecked")
	private void forEachConvertEgressColumnRefSite(
			HashMap<String, Object> localInterface,
			HashMap<String, Object> archivedScopeColumnReferenceContainers,
			Object updateAssignmentsObj,
			ConvertEgressColumnRefSiteVisitor visitor) {
		if (visitor == null) {
			return;
		}

		if (localInterface != null && !localInterface.isEmpty()) {
			for (Map.Entry<String, Object> interfaceEntry : localInterface.entrySet()) {
				String interfaceKey = interfaceEntry.getKey();
				if (interfaceKey == null || interfaceKey.isBlank()) {
					continue;
				}
				Object refsObj = interfaceEntry.getValue();
				if (!(refsObj instanceof ArrayList<?> refs)) {
					continue;
				}
				for (Object refObj : (ArrayList<Object>) refs) {
					visitConvertEgressColumnRefSite(visitor, MUMBLE_INTERFACE_KEY, refObj);
				}
			}
		}

		if (archivedScopeColumnReferenceContainers != null) {
			for (String containerKey : ARCHIVED_SCOPE_COLUMN_REFERENCE_CONTAINER_KEYS) {
				visitConvertEgressColumnRefList(
						visitor,
						containerKey,
						archivedScopeColumnReferenceContainers.get(containerKey));
			}
		}

		if (updateAssignmentsObj instanceof Map<?, ?> assignmentsMapObj) {
			for (Object rhsRefsObj : ((Map<String, Object>) assignmentsMapObj).values()) {
				visitConvertEgressColumnRefList(
						visitor,
						UPDATE_ASSIGNMENT_RHS_CLAUSE_PROBE_KEY,
						rhsRefsObj);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void visitConvertEgressColumnRefList(
			ConvertEgressColumnRefSiteVisitor visitor,
			String siteKey,
			Object columnListObj) {
		if (!(columnListObj instanceof ArrayList<?> columnRefsObj)) {
			return;
		}
		for (Object refObj : (ArrayList<Object>) columnRefsObj) {
			visitConvertEgressColumnRefSite(visitor, siteKey, refObj);
		}
	}

	private void visitConvertEgressColumnRefSite(
			ConvertEgressColumnRefSiteVisitor visitor,
			String siteKey,
			Object refObj) {
		if (visitor == null || refObj == null) {
			return;
		}
		String columnName = walker.extractReferenceNameFromInterfaceEntry(refObj);
		String tableRef = walker.extractReferenceTableRefFromInterfaceEntry(refObj);
		if (columnName == null || columnName.isBlank() || "*".equals(columnName)) {
			return;
		}
		visitor.visit(siteKey, refObj, columnName, tableRef);
	}

	private boolean isAmbiguousUnqualifiedStructuredDerivedColumn(
			String columnName,
			String tableRef,
			HashMap<String, Object> localDerivedColumns) {
		if (columnName == null
				|| columnName.isBlank()
				|| localDerivedColumns == null
				|| localDerivedColumns.isEmpty()) {
			return false;
		}
		if (!isUnqualifiedColumnRef(tableRef)) {
			return false;
		}
		return collectRelationalModifierStructuredDerivedColumnBucketKeys(
				columnName,
				localDerivedColumns).size() >= 2;
	}

	@SuppressWarnings("unchecked")
	private void mergeRelationalModifierDerivedColumnDefinitionTokensIntoQueryDictionary(
			HashMap<String, Object> localInterface,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> localDerivedColumns) {
		if (localInterface == null
				|| localInterface.isEmpty()
				|| localCurrentQueryDictionary == null
				|| localDerivedColumns == null
				|| localDerivedColumns.isEmpty()) {
			return;
		}

		for (Object bucketObj : localDerivedColumns.values()) {
			if (!(bucketObj instanceof Map<?, ?>)) {
				continue;
			}
			Map<String, Object> bucketMap = (Map<String, Object>) bucketObj;
			for (Map.Entry<String, Object> columnEntry : bucketMap.entrySet()) {
				String derivedColumnName = columnEntry.getKey();
				if (derivedColumnName == null || derivedColumnName.isBlank()) {
					continue;
				}
				String interfaceKey = findKeyIgnoreCase(localInterface, derivedColumnName);
				if (interfaceKey == null) {
					continue;
				}
				ArrayList<String> derivedBuckets = collectRelationalModifierStructuredDerivedColumnBucketKeys(
						derivedColumnName,
						localDerivedColumns);
				if (derivedBuckets.size() >= 2) {
					continue;
				}
				Object definitionTokens = columnEntry.getValue();
				if (definitionTokens == null) {
					continue;
				}
				walker.mergeResolvedColumnIntoDictionary(
						localCurrentQueryDictionary,
						interfaceKey,
						definitionTokens);
			}
		}
	}

	private void recordRelationalModifierDerivedColumnClauseRefOnQueryDictionary(
			Object columnRefObj,
			String columnName,
			String clauseKey,
			HashMap<String, Object> localInterface,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> localUnresolvedColumnMap,
			HashMap<String, Object> effectiveAliasMap,
			HashMap<String, Object> visibleQuerySourceCollection,
			HashMap<String, Object> localTableCollection) {
		String tableRef = walker.extractReferenceTableRefFromInterfaceEntry(columnRefObj);
		if (isAmbiguousUnqualifiedStructuredDerivedColumn(
				columnName,
				tableRef,
				activeConvertEgressDerivedColumns)) {
			return;
		}
		if (!isInterfaceOutputColumnName(localInterface, columnName)) {
			return;
		}
		ArchivedClauseProbeContext clauseProbeContext = new ArchivedClauseProbeContext(
				null,
				localInterface,
				localCurrentQueryDictionary,
				localUnresolvedColumnMap,
				null,
				localTableCollection,
				visibleQuerySourceCollection,
				effectiveAliasMap,
				null,
				null,
				null,
				null,
				null,
				false,
				activeConvertEgressCurrentQueryScopeKey);
		recordInterfaceOutputClauseRefOnQueryDictionary(
				columnRefObj,
				columnName,
				clauseKey,
				clauseProbeContext);
	}

	// --- UNPIVOT / convertSymbolTable resolution (canonical from event walker) ---

	/**
	 * Phase 17.1: tier-2 UNPIVOT interface rewrite on the active walker symbol-table frame.
	 * Prefer {@link #applyUnpivotDerivationsToQueryScope(HashMap, ArrayList)} at convert exit
	 * after the interface egress loop (VALUE expansion must run after derived consume is skipped).
	 */
	@SuppressWarnings("unchecked")
	public void applyUnpivotDerivationsToQueryScope() {
		RelationalModifierConvertEgressContext ctx =
				peekRelationalModifierConvertContextFromScope(walker.symbolTable);
		if (ctx == null || !ctx.isUnpivot()) {
			return;
		}
		Object interfaceObj = walker.symbolTable.get(MUMBLE_INTERFACE_KEY);
		if (interfaceObj instanceof HashMap<?, ?> interfaceMapObj) {
			applyUnpivotDerivationsToQueryScope(
					(HashMap<String, Object>) interfaceMapObj,
					ctx,
					activeConvertEgressDerivedColumns);
		}
	}

	public void applyUnpivotDerivationsToQueryScope(
			HashMap<String, Object> localInterface,
			RelationalModifierConvertEgressContext relationalModifierContext,
			HashMap<String, Object> localDerivedColumns) {
		if (localInterface == null || localInterface.isEmpty()
				|| relationalModifierContext == null
				|| relationalModifierContext.isEmpty()
				|| !relationalModifierContext.isUnpivot()) {
			return;
		}

		InferredUnpivotDerivedOutputs unpivotOutputs =
				inferUnpivotDerivedOutputColumnsFromContext(relationalModifierContext);
		if (unpivotOutputs == null) {
			return;
		}

		String valueColumn = unpivotOutputs.valueColumn;
		String sourceRef = relationalModifierContext.interfaceSourceRef;
		String forColumn = unpivotOutputs.forColumn;
		ArrayList<String> inColumns = collectUnpivotInColumnNamesFromContext(relationalModifierContext);
		if (valueColumn == null
				|| valueColumn.isBlank()
				|| sourceRef == null
				|| sourceRef.isBlank()
				|| inColumns.isEmpty()) {
			return;
		}

		// Legacy single-unpivot VALUE→IN-list rewrite must not run when multiple sibling
		// modifiers expose the same structured derived name (17.6.2 / 17.7.5).
		if (isAmbiguousUnqualifiedStructuredDerivedColumn(
				valueColumn,
				null,
				localDerivedColumns)) {
			return;
		}

		ArrayList<Object> inColumnsObj = new ArrayList<Object>(inColumns);
		for (Map.Entry<String, Object> interfaceEntry : localInterface.entrySet()) {
			Object refsObj = interfaceEntry.getValue();
			if (!(refsObj instanceof ArrayList<?> refs) || refs.isEmpty()) {
				continue;
			}

			ArrayList<Object> rewrittenRefs = rewriteReferenceListForSingleUnpivotHint(
					(ArrayList<Object>) refs,
					valueColumn,
					forColumn,
					sourceRef,
					inColumnsObj,
					interfaceEntry.getKey(),
					localDerivedColumns);

			if (!rewrittenRefs.equals(refs)) {
				interfaceEntry.setValue(rewrittenRefs);
			}
		}
	}

	public ArrayList<Object> rewriteReferenceListForSingleUnpivotHint(
			ArrayList<Object> refs,
			String valueColumn,
			String forColumn,
			String sourceRef,
			ArrayList<Object> inColumnsObj) {
		return rewriteReferenceListForSingleUnpivotHint(
				refs,
				valueColumn,
				forColumn,
				sourceRef,
				inColumnsObj,
				null,
				null);
	}

	public ArrayList<Object> rewriteReferenceListForSingleUnpivotHint(
			ArrayList<Object> refs,
			String valueColumn,
			String forColumn,
			String sourceRef,
			ArrayList<Object> inColumnsObj,
			String interfaceOutputColumn,
			HashMap<String, Object> localDerivedColumns) {
		ArrayList<Object> rewrittenRefs = new ArrayList<Object>();
		boolean valueColumnReferenceFound = false;
		boolean retainDerivedValueHop = interfaceOutputColumn != null
				&& !interfaceOutputColumn.isBlank()
				&& valueColumn != null
				&& !interfaceOutputColumn.equalsIgnoreCase(valueColumn);

		boolean suppressInColumnRefsAfterValueExpand = false;

		for (Object refObj : refs) {
			String refName = walker.extractReferenceNameFromInterfaceEntry(refObj);
			if (refName != null && refName.equalsIgnoreCase(valueColumn)) {
				if (retainDerivedValueHop
						&& !isUnpivotValueDerivedColumnInterfaceRef(
								refObj,
								sourceRef,
								localDerivedColumns)) {
					appendInterfaceReferenceIfMissing(rewrittenRefs, refObj);
					continue;
				}
				valueColumnReferenceFound = true;
				String inListTableRef = sourceRef;
				if (retainDerivedValueHop && refObj instanceof Map<?, ?> valueRefMap) {
					Object valueTableRefObj = valueRefMap.get(MUMBLE_TABLE_REF_KEY);
					if (valueTableRefObj instanceof String valueTableRef
							&& !valueTableRef.isBlank()) {
						inListTableRef = valueTableRef;
					}
				}
				if (retainDerivedValueHop) {
					appendInterfaceReferenceIfMissing(rewrittenRefs, refObj);
				}
				for (Object inColumnObj : inColumnsObj) {
					if (!(inColumnObj instanceof String inColumn) || inColumn.isBlank()) {
						continue;
					}

					HashMap<String, Object> derivedRef = new HashMap<String, Object>();
					derivedRef.put(MUMBLE_NAME_KEY, inColumn);
					derivedRef.put(MUMBLE_TABLE_REF_KEY, inListTableRef);
					appendInterfaceReferenceIfMissing(rewrittenRefs, derivedRef);
				}
				suppressInColumnRefsAfterValueExpand = retainDerivedValueHop;
			} else if (forColumn != null
					&& refName != null
					&& refName.equalsIgnoreCase(forColumn)) {
				appendInterfaceReferenceIfMissing(rewrittenRefs, refObj);
			} else if (suppressInColumnRefsAfterValueExpand
					&& refName != null
					&& unpivotInListContainsColumnName(inColumnsObj, refName)) {
				continue;
			} else {
				appendInterfaceReferenceIfMissing(rewrittenRefs, refObj);
			}
		}

		if (!valueColumnReferenceFound) {
			return new ArrayList<Object>(refs);
		}

		return rewrittenRefs;
	}

	private boolean unpivotInListContainsColumnName(ArrayList<Object> inColumnsObj, String columnName) {
		if (inColumnsObj == null || inColumnsObj.isEmpty() || columnName == null || columnName.isBlank()) {
			return false;
		}
		for (Object inColumnObj : inColumnsObj) {
			if (inColumnObj instanceof String inColumn
					&& inColumn.equalsIgnoreCase(columnName)) {
				return true;
			}
		}
		return false;
	}

	private boolean isUnpivotValueDerivedColumnInterfaceRef(
			Object refObj,
			String modifierInterfaceSourceRef,
			HashMap<String, Object> localDerivedColumns) {
		String tableRef = walker.extractReferenceTableRefFromInterfaceEntry(refObj);
		if (tableRef == null || tableRef.isBlank()) {
			return true;
		}
		if (modifierInterfaceSourceRef != null
				&& tableRef.equalsIgnoreCase(modifierInterfaceSourceRef)) {
			return false;
		}
		if (localDerivedColumns == null || localDerivedColumns.isEmpty()) {
			return false;
		}
		for (String bucketKey : localDerivedColumns.keySet()) {
			if (bucketKey != null && bucketKey.equalsIgnoreCase(tableRef)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Object> buildRelationalModifierSourceColumnInterfaceRefs(
			Map<String, Object> sourceColumnsMap,
			String physicalTableRef) {
		ArrayList<Object> sourceRefs = new ArrayList<Object>();
		if (sourceColumnsMap == null
				|| sourceColumnsMap.isEmpty()
				|| physicalTableRef == null
				|| physicalTableRef.isBlank()) {
			return sourceRefs;
		}

		for (String columnName : sourceColumnsMap.keySet()) {
			if (columnName == null || columnName.isBlank()) {
				continue;
			}
			HashMap<String, Object> sourceRef = new HashMap<String, Object>();
			sourceRef.put(MUMBLE_NAME_KEY, columnName);
			sourceRef.put(MUMBLE_TABLE_REF_KEY, physicalTableRef);
			sourceRefs.add(sourceRef);
		}
		return sourceRefs;
	}

	/**
	 * True when {@code columnName} is recorded as a relational-modifier source operand for
	 * {@code physicalTableRef} in structured {@code derivation.source_columns} buckets.
	 */
	@SuppressWarnings("unchecked")
	public boolean isRelationalModifierSourceColumnForPhysicalTable(
			String columnName,
			String physicalTableRef,
			HashMap<String, Object> sourceColumnsByBucket,
			HashMap<String, Object> localTableAliasMap) {
		if (columnName == null
				|| columnName.isBlank()
				|| physicalTableRef == null
				|| physicalTableRef.isBlank()
				|| sourceColumnsByBucket == null
				|| sourceColumnsByBucket.isEmpty()) {
			return false;
		}

		String canonicalPhysical = resolveCanonicalPhysicalTableRef(physicalTableRef, localTableAliasMap);
		if (canonicalPhysical == null || canonicalPhysical.isBlank()) {
			canonicalPhysical = normalizeTableRef(physicalTableRef);
		}
		if (canonicalPhysical == null || canonicalPhysical.isBlank()) {
			return false;
		}

		for (Object bucketObj : sourceColumnsByBucket.values()) {
			if (bucketObj instanceof ArrayList<?> sourceRefList) {
				for (Object refObj : sourceRefList) {
					if (!(refObj instanceof Map<?, ?> refMap)) {
						continue;
					}
					Object nameObj = refMap.get(MUMBLE_NAME_KEY);
					if (!(nameObj instanceof String sourceName) || sourceName.isBlank()) {
						continue;
					}
					if (!sourceName.equalsIgnoreCase(columnName)) {
						continue;
					}
					Object tableRefObj = refMap.get(MUMBLE_TABLE_REF_KEY);
					String sourceTableRef = tableRefObj instanceof String tableRefValue ? tableRefValue : null;
					String sourcePhysical = resolveCanonicalPhysicalTableRef(sourceTableRef, localTableAliasMap);
					if (sourcePhysical == null || sourcePhysical.isBlank()) {
						sourcePhysical = normalizeTableRef(sourceTableRef);
					}
					if (sourcePhysical != null && sourcePhysical.equalsIgnoreCase(canonicalPhysical)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean hasRelationalModifierBucketDerivedColumns(HashMap<String, Object> localDerivedColumns) {
		if (localDerivedColumns == null || localDerivedColumns.isEmpty()) {
			return false;
		}
		for (Object bucketObj : localDerivedColumns.values()) {
			if (bucketObj instanceof Map<?, ?> bucketMap
					&& isRelationalModifierStructuredDerivedColumnBucket((Map<String, Object>) bucketMap)) {
				return true;
			}
		}
		return false;
	}

	private boolean isRelationalModifierStructuredDerivedColumnBucket(Map<String, Object> bucketMap) {
		if (bucketMap == null || bucketMap.isEmpty()) {
			return false;
		}
		for (Object valueObj : bucketMap.values()) {
			ArrayList<?> valueList = relationalModifierDerivedColumnEntryTokenRefs(valueObj);
			if (valueList == null || valueList.isEmpty()) {
				continue;
			}
			Object firstItem = valueList.get(0);
			if (firstItem instanceof Map<?, ?> firstMap
					&& ((Map<?, ?>) firstMap).containsKey(MUMBLE_NAME_KEY)
					&& ((Map<?, ?>) firstMap).containsKey(MUMBLE_TABLE_REF_KEY)) {
				continue;
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	ArrayList<Object> relationalModifierDerivedColumnEntryTokenRefs(Object derivedEntry) {
		if (derivedEntry instanceof Map<?, ?> entryMap) {
			Object tokensObj = entryMap.get(RELATIONAL_MODIFIER_DERIVED_COLUMN_TOKENS_KEY);
			if (tokensObj instanceof ArrayList<?> tokenList) {
				return (ArrayList<Object>) tokenList;
			}
			return null;
		}
		if (derivedEntry instanceof ArrayList<?> tokenList) {
			return (ArrayList<Object>) tokenList;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Object> expandReferenceListForRelationalModifierDerivedColumn(
			ArrayList<Object> refs,
			HashMap<String, Object> localDerivedColumns,
			HashMap<String, Object> localSourceColumnsByBucket,
			HashMap<String, Object> localTableAliasMap) {
		if (refs == null
				|| refs.size() != 1
				|| localDerivedColumns == null
				|| localDerivedColumns.isEmpty()
				|| localSourceColumnsByBucket == null
				|| localSourceColumnsByBucket.isEmpty()) {
			return null;
		}
		return expandRelationalModifierDerivedColumnReference(
				refs.get(0),
				localDerivedColumns,
				localSourceColumnsByBucket,
				localTableAliasMap);
	}

	@SuppressWarnings("unchecked")
	private ArrayList<Object> expandRelationalModifierDerivedColumnReference(
			Object refObj,
			HashMap<String, Object> localDerivedColumns,
			HashMap<String, Object> localSourceColumnsByBucket,
			HashMap<String, Object> localTableAliasMap) {
		if (refObj == null) {
			return null;
		}
		if (isEgressRelationalModifierDerivedBucketColumnRef(refObj, localDerivedColumns)) {
			return null;
		}

		String columnName = walker.extractReferenceNameFromInterfaceEntry(refObj);
		String qualifier = walker.extractReferenceTableRefFromInterfaceEntry(refObj);
		if (columnName == null || columnName.isBlank()) {
			return null;
		}
		if (isAmbiguousUnqualifiedStructuredDerivedColumn(
				columnName,
				qualifier,
				localDerivedColumns)) {
			return null;
		}

		String bucketKey = resolveRelationalModifierDerivedColumnBucketKey(
				columnName,
				qualifier,
				localDerivedColumns,
				localTableAliasMap);
		if (bucketKey == null || bucketKey.isBlank()) {
			return null;
		}

		Object sourceBucketObj = localSourceColumnsByBucket.get(bucketKey);
		if (sourceBucketObj == null) {
			String resolvedSourceBucketKey = findKeyIgnoreCase(localSourceColumnsByBucket, bucketKey);
			if (resolvedSourceBucketKey != null) {
				sourceBucketObj = localSourceColumnsByBucket.get(resolvedSourceBucketKey);
			}
		}
		if (!(sourceBucketObj instanceof ArrayList<?> sourceRefs) || sourceRefs.isEmpty()) {
			return null;
		}

		ArrayList<Object> sourceLineage = copyInterfaceReferenceList((ArrayList<Object>) sourceRefs);
		if (sourceLineage == null || sourceLineage.isEmpty()) {
			return null;
		}

		String resolvedBucketKey = bucketKey;
		Object derivedBucketObj = localDerivedColumns.get(bucketKey);
		if (derivedBucketObj == null) {
			String matchedBucketKey = findKeyIgnoreCase(localDerivedColumns, bucketKey);
			if (matchedBucketKey != null) {
				resolvedBucketKey = matchedBucketKey;
				derivedBucketObj = localDerivedColumns.get(resolvedBucketKey);
			}
		}
		String canonicalDerivedName = columnName;
		if (derivedBucketObj instanceof Map<?, ?> derivedBucketMap) {
			String matchedDerivedName = findKeyIgnoreCase(
					(Map<String, Object>) derivedBucketMap,
					columnName);
			if (matchedDerivedName != null && !matchedDerivedName.isBlank()) {
				canonicalDerivedName = matchedDerivedName;
			}
		}

		ArrayList<Object> lineage = new ArrayList<Object>();
		HashMap<String, Object> derivedRef = new HashMap<String, Object>();
		derivedRef.put(MUMBLE_NAME_KEY, canonicalDerivedName);
		derivedRef.put(MUMBLE_TABLE_REF_KEY, resolvedBucketKey);
		lineage.add(derivedRef);
		for (Object sourceRef : sourceLineage) {
			appendInterfaceReferenceIfMissing(lineage, sourceRef);
		}
		return lineage;
	}

	private boolean isEgressRelationalModifierDerivedBucketColumnRef(
			Object refObj,
			HashMap<String, Object> localDerivedColumns) {
		if (localDerivedColumns == null || localDerivedColumns.isEmpty()) {
			return false;
		}
		String columnName = walker.extractReferenceNameFromInterfaceEntry(refObj);
		String tableRef = walker.extractReferenceTableRefFromInterfaceEntry(refObj);
		if (columnName == null
				|| columnName.isBlank()
				|| tableRef == null
				|| tableRef.isBlank()) {
			return false;
		}
		if (!isRelationalModifierDerivedColumnBucketKey(tableRef, localDerivedColumns)) {
			return false;
		}
		String resolvedBucketKey = findKeyIgnoreCase(localDerivedColumns, tableRef);
		if (resolvedBucketKey == null) {
			return false;
		}
		Object bucketObj = localDerivedColumns.get(resolvedBucketKey);
		if (!(bucketObj instanceof Map<?, ?> bucketMap)) {
			return false;
		}
		return containsKeyIgnoreCase((Map<String, Object>) bucketMap, columnName);
	}

	@SuppressWarnings("unchecked")
	private String resolveRelationalModifierDerivedColumnBucketKey(
			String columnName,
			String qualifier,
			HashMap<String, Object> localDerivedColumns,
			HashMap<String, Object> localTableAliasMap) {
		if (columnName == null || columnName.isBlank() || localDerivedColumns == null) {
			return null;
		}

		if (qualifier != null && !qualifier.isBlank()) {
			String bucketKey = findKeyIgnoreCase(localDerivedColumns, qualifier);
			if (bucketKey != null) {
				Object bucketObj = localDerivedColumns.get(bucketKey);
				if (bucketObj instanceof Map<?, ?> bucketMap
						&& isRelationalModifierStructuredDerivedColumnBucket((Map<String, Object>) bucketMap)
						&& containsKeyIgnoreCase((Map<String, Object>) bucketMap, columnName)) {
					return bucketKey;
				}
			}
			if (isRelationalModifierDerivedColumnBucketKey(qualifier, localDerivedColumns)) {
				return null;
			}
		}

		String matchedBucketKey = null;
		for (Map.Entry<String, Object> bucketEntry : localDerivedColumns.entrySet()) {
			Object bucketObj = bucketEntry.getValue();
			if (!(bucketObj instanceof Map<?, ?> bucketMap)) {
				continue;
			}
			if (!isRelationalModifierStructuredDerivedColumnBucket((Map<String, Object>) bucketMap)) {
				continue;
			}
			if (!containsKeyIgnoreCase((Map<String, Object>) bucketMap, columnName)) {
				continue;
			}
			if (matchedBucketKey != null) {
				return null;
			}
			matchedBucketKey = bucketEntry.getKey();
		}
		return matchedBucketKey;
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> collectRelationalModifierStructuredDerivedColumnBucketKeys(
			String columnName,
			HashMap<String, Object> localDerivedColumns) {
		ArrayList<String> bucketKeys = new ArrayList<String>();
		if (columnName == null
				|| columnName.isBlank()
				|| localDerivedColumns == null
				|| localDerivedColumns.isEmpty()) {
			return bucketKeys;
		}

		for (Map.Entry<String, Object> bucketEntry : localDerivedColumns.entrySet()) {
			String bucketKey = bucketEntry.getKey();
			if (bucketKey == null || bucketKey.isBlank()) {
				continue;
			}
			Object bucketObj = bucketEntry.getValue();
			if (!(bucketObj instanceof Map<?, ?> bucketMap)) {
				continue;
			}
			if (!isRelationalModifierStructuredDerivedColumnBucket((Map<String, Object>) bucketMap)) {
				continue;
			}
			if (containsKeyIgnoreCase((Map<String, Object>) bucketMap, columnName)) {
				bucketKeys.add(bucketKey);
			}
		}
		return bucketKeys;
	}

	private String formatRelationalModifierDerivedColumnAmbiguitySources(ArrayList<String> bucketKeys) {
		if (bucketKeys == null || bucketKeys.isEmpty()) {
			return "[]";
		}
		return bucketKeys.toString();
	}

	private String formatDerivedVersusRegularColumnAmbiguitySources(
			ArrayList<String> derivedBucketKeys,
			LinkedHashSet<String> regularSourceLabels) {
		String derivedLabel = formatRelationalModifierDerivedColumnAmbiguitySources(derivedBucketKeys);
		String regularLabel = regularSourceLabels == null || regularSourceLabels.isEmpty()
				? "[]"
				: regularSourceLabels.toString();
		return derivedLabel + "|" + regularLabel;
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, ArrayList<String>> buildStructuredDerivedColumnCandidateMap(
			HashMap<String, Object> localDerivedColumns) {
		HashMap<String, ArrayList<String>> byColumn = new HashMap<String, ArrayList<String>>();
		if (localDerivedColumns == null || localDerivedColumns.isEmpty()) {
			return byColumn;
		}
		for (Map.Entry<String, Object> bucketEntry : localDerivedColumns.entrySet()) {
			String bucketKey = bucketEntry.getKey();
			if (bucketKey == null || bucketKey.isBlank()) {
				continue;
			}
			Object bucketObj = bucketEntry.getValue();
			if (!(bucketObj instanceof Map<?, ?> bucketMap)) {
				continue;
			}
			if (!isRelationalModifierStructuredDerivedColumnBucket((Map<String, Object>) bucketMap)) {
				continue;
			}
			for (String derivedColumnName : ((Map<String, Object>) bucketMap).keySet()) {
				if (derivedColumnName == null || derivedColumnName.isBlank()) {
					continue;
				}
				String mapKey = derivedColumnName;
				for (String existingKey : byColumn.keySet()) {
					if (existingKey != null && existingKey.equalsIgnoreCase(derivedColumnName)) {
						mapKey = existingKey;
						break;
					}
				}
				ArrayList<String> buckets = byColumn.computeIfAbsent(mapKey, key -> new ArrayList<String>());
				if (!bucketKeysContainIgnoreCase(buckets, bucketKey)) {
					buckets.add(bucketKey);
				}
			}
		}
		return byColumn;
	}

	private static boolean bucketKeysContainIgnoreCase(ArrayList<String> bucketKeys, String bucketKey) {
		if (bucketKeys == null || bucketKey == null) {
			return false;
		}
		for (String existing : bucketKeys) {
			if (existing != null && existing.equalsIgnoreCase(bucketKey)) {
				return true;
			}
		}
		return false;
	}

	private void stashStructuredDerivedColumnCandidatesForConvertEgress(
			HashMap<String, Object> localDerivedColumns) {
		if (!isDerivedVersusRegularColumnNamespaceDiagnosticScope()) {
			activeConvertEgressStructuredDerivedColumnCandidates = null;
			return;
		}
		activeConvertEgressStructuredDerivedColumnCandidates =
				buildStructuredDerivedColumnCandidateMap(localDerivedColumns);
	}

	private ArrayList<String> getStructuredDerivedColumnCandidateBuckets(String columnName) {
		if (columnName == null || columnName.isBlank()) {
			return new ArrayList<String>();
		}
		HashMap<String, ArrayList<String>> candidateMap =
				activeConvertEgressStructuredDerivedColumnCandidates;
		if (candidateMap == null || candidateMap.isEmpty()) {
			return new ArrayList<String>();
		}
		for (Map.Entry<String, ArrayList<String>> entry : candidateMap.entrySet()) {
			if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(columnName)) {
				ArrayList<String> buckets = entry.getValue();
				return buckets == null ? new ArrayList<String>() : new ArrayList<String>(buckets);
			}
		}
		return new ArrayList<String>();
	}

	private LinkedHashSet<String> collectUnqualifiedRegularColumnResolutionSourceLabels(
			String columnName,
			HashMap<String, Object> localPhysicalTableCollection,
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> visibleQuerySourceCollection,
			HashMap<String, Object> localTableAliasMap,
			boolean allowQuerySourceFallback) {
		LinkedHashSet<String> labels = new LinkedHashSet<String>();
		if (columnName == null || columnName.isBlank()) {
			return labels;
		}

		HashMap<String, Object> localPhysical = localPhysicalTableCollection != null
				? localPhysicalTableCollection
				: buildLocalPhysicalFromTableCollection(localTableCollection);
		if (localPhysical != null) {
			for (String tableRef : localPhysical.keySet()) {
				if (tableRef == null || tableRef.isBlank()) {
					continue;
				}
				HashMap<String, Object> sourceDictionary = walker.getTableDictionaryForReference(
						tableRef,
						localTableCollection);
				if (sourceDictionary != null && containsKeyIgnoreCase(sourceDictionary, columnName)) {
					labels.add(normalizeTableRef(tableRef));
				}
			}
		}

		if (allowQuerySourceFallback) {
			LinkedHashSet<String> querySources = collectQuerySourcesWithColumn(
					columnName,
					visibleQuerySourceCollection,
					localTableAliasMap);
			labels.addAll(querySources);
		}

		return filterRegularSourcesExcludedFromDerivedVersusRegularCrossCheck(
				columnName,
				labels,
				localTableCollection,
				localTableAliasMap,
				activeConvertEgressRelationalModifierSourceColumns);
	}

	/**
	 * Physical columns that exist only as PIVOT/UNPIVOT <em>source operands</em> on a modifier's
	 * source table are not independent "regular" resolution targets for structured derived output
	 * names (e.g. UNPIVOT VALUE/FOR vs {@code monthly_sales_long.month_name} operand).
	 */
	@SuppressWarnings("unchecked")
	private LinkedHashSet<String> filterRegularSourcesExcludedFromDerivedVersusRegularCrossCheck(
			String columnName,
			LinkedHashSet<String> regularSources,
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> localTableAliasMap,
			HashMap<String, Object> localRelationalModifierSourceColumns) {
		if (regularSources == null || regularSources.isEmpty()) {
			return regularSources == null ? new LinkedHashSet<String>() : new LinkedHashSet<String>(regularSources);
		}
		LinkedHashSet<String> filtered = new LinkedHashSet<String>();
		for (String sourceLabel : regularSources) {
			if (sourceLabel == null || sourceLabel.isBlank()) {
				continue;
			}
			if (isQuerySourceReference(sourceLabel)
					|| isTableFunctionSourceReference(sourceLabel)
					|| walker.isNonTableQuerySourceReference(sourceLabel)) {
				if (!isQuerySourceRegisteredAsModifierSourceOperandHost(
						columnName,
						sourceLabel,
						localTableAliasMap,
						localRelationalModifierSourceColumns)) {
					filtered.add(sourceLabel);
				}
				continue;
			}
			String physicalTableRef = walker.resolveAliasToTableName(sourceLabel, localTableAliasMap);
			if (physicalTableRef == null || physicalTableRef.isBlank()) {
				physicalTableRef = normalizeTableRef(sourceLabel);
			}
			if (!isPhysicalColumnRegisteredAsRelationalModifierSourceOperand(
					columnName,
					physicalTableRef,
					localRelationalModifierSourceColumns,
					localTableAliasMap)) {
				filtered.add(sourceLabel);
			}
		}
		return filtered;
	}

	@SuppressWarnings("unchecked")
	private boolean isPhysicalColumnRegisteredAsRelationalModifierSourceOperand(
			String columnName,
			String physicalTableRef,
			HashMap<String, Object> localRelationalModifierSourceColumns,
			HashMap<String, Object> localTableAliasMap) {
		if (columnName == null
				|| columnName.isBlank()
				|| physicalTableRef == null
				|| physicalTableRef.isBlank()
				|| localRelationalModifierSourceColumns == null
				|| localRelationalModifierSourceColumns.isEmpty()) {
			return false;
		}
		String normalizedPhysical = normalizeTableRef(physicalTableRef);
		for (Object bucketObj : localRelationalModifierSourceColumns.values()) {
			if (!(bucketObj instanceof ArrayList<?> operandList)) {
				continue;
			}
			for (Object operandObj : operandList) {
				if (!(operandObj instanceof Map<?, ?> operandMap)) {
					continue;
				}
				Object nameObj = operandMap.get(MUMBLE_NAME_KEY);
				if (nameObj == null || !columnName.equalsIgnoreCase(nameObj.toString())) {
					continue;
				}
				Object tableRefObj = operandMap.get(MUMBLE_TABLE_REF_KEY);
				if (tableRefObj == null) {
					continue;
				}
				String operandTableRef = tableRefObj.toString();
				String resolvedOperandTable = walker.resolveAliasToTableName(
						operandTableRef,
						localTableAliasMap);
				if (resolvedOperandTable == null || resolvedOperandTable.isBlank()) {
					resolvedOperandTable = normalizeTableRef(operandTableRef);
				}
				if (normalizedPhysical.equalsIgnoreCase(resolvedOperandTable)) {
					return true;
				}
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean isQuerySourceRegisteredAsModifierSourceOperandHost(
			String columnName,
			String queryRef,
			HashMap<String, Object> localTableAliasMap,
			HashMap<String, Object> localRelationalModifierSourceColumns) {
		if (columnName == null
				|| columnName.isBlank()
				|| queryRef == null
				|| queryRef.isBlank()
				|| localRelationalModifierSourceColumns == null
				|| localRelationalModifierSourceColumns.isEmpty()
				|| localTableAliasMap == null
				|| localTableAliasMap.isEmpty()) {
			return false;
		}
		String canonicalQuery = normalizeQuerySourceReference(queryRef);
		if (canonicalQuery == null || canonicalQuery.isBlank()) {
			return false;
		}
		for (Object bucketObj : localRelationalModifierSourceColumns.values()) {
			if (!(bucketObj instanceof ArrayList<?> operandList)) {
				continue;
			}
			for (Object operandObj : operandList) {
				if (!(operandObj instanceof Map<?, ?> operandMap)) {
					continue;
				}
				Object nameObj = operandMap.get(MUMBLE_NAME_KEY);
				if (nameObj == null || !columnName.equalsIgnoreCase(nameObj.toString())) {
					continue;
				}
				Object tableRefObj = operandMap.get(MUMBLE_TABLE_REF_KEY);
				if (tableRefObj == null) {
					continue;
				}
				String sourcePrimaryAlias = tableRefObj.toString();
				Object mappedSource = localTableAliasMap.get(sourcePrimaryAlias);
				if (!(mappedSource instanceof String mappedQuery)) {
					continue;
				}
				String canonicalMapped = normalizeQuerySourceReference(mappedQuery);
				if (canonicalQuery.equalsIgnoreCase(canonicalMapped)) {
					return true;
				}
			}
		}
		return false;
	}

	private UnqualifiedScopeResolutionResult tryResolveDerivedVersusRegularColumnNamespaceAmbiguity(
			String columnName,
			String tableRef,
			HashMap<String, Object> localPhysicalTableCollection,
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> visibleQuerySourceCollection,
			HashMap<String, Object> localTableAliasMap,
			boolean allowQuerySourceFallback,
			HashMap<String, Object> localDerivedColumns) {
		if (!isDerivedVersusRegularColumnNamespaceDiagnosticScope()) {
			return null;
		}
		if (!isUnqualifiedColumnRef(tableRef)) {
			return null;
		}
		ArrayList<String> derivedBuckets = getStructuredDerivedColumnCandidateBuckets(columnName);
		if (derivedBuckets.isEmpty() && localDerivedColumns != null) {
			derivedBuckets = collectRelationalModifierStructuredDerivedColumnBucketKeys(
					columnName,
					localDerivedColumns);
		}
		if (derivedBuckets.isEmpty()) {
			return null;
		}
		LinkedHashSet<String> regularSources = collectUnqualifiedRegularColumnResolutionSourceLabels(
				columnName,
				localPhysicalTableCollection,
				localTableCollection,
				visibleQuerySourceCollection,
				localTableAliasMap,
				allowQuerySourceFallback);
		if (regularSources.isEmpty()) {
			return null;
		}
		return UnqualifiedScopeResolutionResult.ambiguousDerivedAndRegularColumn(
				formatDerivedVersusRegularColumnAmbiguitySources(derivedBuckets, regularSources));
	}

	private boolean isDerivedVersusRegularColumnNamespaceAmbiguity(
			String columnName,
			String tableRef,
			HashMap<String, Object> localPhysicalTableCollection,
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> visibleQuerySourceCollection,
			HashMap<String, Object> localTableAliasMap,
			boolean allowQuerySourceFallback,
			HashMap<String, Object> localDerivedColumns) {
		return tryResolveDerivedVersusRegularColumnNamespaceAmbiguity(
				columnName,
				tableRef,
				localPhysicalTableCollection,
				localTableCollection,
				visibleQuerySourceCollection,
				localTableAliasMap,
				allowQuerySourceFallback,
				localDerivedColumns) != null;
	}

	private String parseDerivedVersusRegularAmbiguityDerivedSources(String combinedLabel) {
		if (combinedLabel == null || combinedLabel.isBlank()) {
			return "[]";
		}
		int separator = combinedLabel.indexOf('|');
		if (separator < 0) {
			return combinedLabel;
		}
		return combinedLabel.substring(0, separator);
	}

	private String parseDerivedVersusRegularAmbiguityRegularSources(String combinedLabel) {
		if (combinedLabel == null || combinedLabel.isBlank()) {
			return "[]";
		}
		int separator = combinedLabel.indexOf('|');
		if (separator < 0) {
			return "[]";
		}
		return combinedLabel.substring(separator + 1);
	}

	private void emitAmbiguousDerivedAndRegularColumnReferenceFatal(
			String columnName,
			Integer[] refLocation,
			String derivedSourcesLabel,
			String regularSourcesLabel) {
		String diagCode = walker.getDiagnosticCode(
				SqlASTWalkerHelper.DIAG_SQL_AMBIGUOUS_DERIVED_AND_REGULAR_COLUMN_REFERENCE);
		String diagTemplate = walker.getDiagnosticMessage(
				SqlASTWalkerHelper.DIAG_SQL_AMBIGUOUS_DERIVED_AND_REGULAR_COLUMN_REFERENCE);
		String diagMessage = (diagTemplate == null)
				? String.format(
						"Ambiguous column reference '%s' at (l:%s c:%s). Possible derived sources: %s. Possible regular sources: %s",
						columnName,
						refLocation[0],
						refLocation[1],
						derivedSourcesLabel,
						regularSourcesLabel)
				: String.format(
						diagTemplate,
						columnName,
						refLocation[0],
						refLocation[1],
						derivedSourcesLabel,
						regularSourcesLabel);

		walker.addWalkerFatal(
				diagCode,
				diagMessage,
				refLocation[0],
				refLocation[1],
				columnName);
	}

	private void emitAmbiguousDerivedAndRegularColumnReferenceFatalIfNew(
			String columnName,
			Integer[] refLocation,
			String combinedAmbiguityLabel,
			HashSet<String> emittedDiagnosticLocations) {
		String locationKey = formatAmbiguousDerivedDiagnosticLocationKey(refLocation);
		if (locationKey == null) {
			return;
		}
		if (emittedDiagnosticLocations != null && !emittedDiagnosticLocations.add(locationKey)) {
			return;
		}
		emitAmbiguousDerivedAndRegularColumnReferenceFatal(
				columnName,
				refLocation,
				parseDerivedVersusRegularAmbiguityDerivedSources(combinedAmbiguityLabel),
				parseDerivedVersusRegularAmbiguityRegularSources(combinedAmbiguityLabel));
	}

	/**
	 * Modifier bucket keys whose structured {@code derivation.source_columns} list records
	 * {@code columnName} as a pivot/unpivot operand (e.g. {@code p} and {@code q} both expose
	 * {@code sales_amount} from their respective source aliases).
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<String> collectRelationalModifierSourceOperandBucketKeys(
			String columnName,
			HashMap<String, Object> sourceColumnsByBucket) {
		ArrayList<String> bucketKeys = new ArrayList<String>();
		if (columnName == null
				|| columnName.isBlank()
				|| sourceColumnsByBucket == null
				|| sourceColumnsByBucket.isEmpty()) {
			return bucketKeys;
		}

		for (Map.Entry<String, Object> bucketEntry : sourceColumnsByBucket.entrySet()) {
			String bucketKey = bucketEntry.getKey();
			if (bucketKey == null || bucketKey.isBlank()) {
				continue;
			}
			Object refsObj = bucketEntry.getValue();
			if (!(refsObj instanceof ArrayList<?> refs)) {
				continue;
			}
			for (Object refObj : refs) {
				if (!(refObj instanceof Map<?, ?> refMap)) {
					continue;
				}
				Object nameObj = refMap.get(MUMBLE_NAME_KEY);
				if (!(nameObj instanceof String sourceName) || sourceName.isBlank()) {
					continue;
				}
				if (sourceName.equalsIgnoreCase(columnName)) {
					bucketKeys.add(bucketKey);
					break;
				}
			}
		}
		return bucketKeys;
	}

	private boolean isAmbiguousUnqualifiedRelationalModifierSourceOperandColumn(
			String columnName,
			String tableRef,
			HashMap<String, Object> sourceColumnsByBucket) {
		if (columnName == null
				|| columnName.isBlank()
				|| sourceColumnsByBucket == null
				|| sourceColumnsByBucket.isEmpty()) {
			return false;
		}
		if (!isUnqualifiedColumnRef(tableRef)) {
			return false;
		}
		return collectRelationalModifierSourceOperandBucketKeys(
				columnName,
				sourceColumnsByBucket).size() >= 2;
	}

	@SuppressWarnings("unchecked")
	private ArrayList<Object> copyInterfaceReferenceList(ArrayList<Object> sourceRefs) {
		ArrayList<Object> copy = new ArrayList<Object>(sourceRefs.size());
		for (Object refObj : sourceRefs) {
			copy.add(copyClauseColumnReferenceForEgress(refObj));
		}
		return copy;
	}

	private boolean isRelationalModifierDerivedColumnBucketKey(
			String bucketKeyCandidate,
			HashMap<String, Object> localDerivedColumns) {
		if (bucketKeyCandidate == null
				|| bucketKeyCandidate.isBlank()
				|| localDerivedColumns == null
				|| localDerivedColumns.isEmpty()) {
			return false;
		}
		String resolvedKey = findKeyIgnoreCase(localDerivedColumns, bucketKeyCandidate);
		if (resolvedKey == null) {
			return false;
		}
		Object bucketObj = localDerivedColumns.get(resolvedKey);
		return bucketObj instanceof Map<?, ?> bucketMap
				&& isRelationalModifierStructuredDerivedColumnBucket((Map<String, Object>) bucketMap);
	}

	private ArrayList<Object> tryExpandRelationalModifierDerivedColumnSourceLineageAtConvertEgress(
			String columnName,
			String tableRef,
			ConvertEgressResolutionContext ctx) {
		// Bucket lineage is applied in runConvertEgressRelationalModifierDerivedLineagePhaseB (17.7.5b.3).
		return null;
	}

	@SuppressWarnings("unchecked")
	private void expandRelationalModifierDerivedColumnLineageInInterfaceMap(
			HashMap<String, Object> localInterface,
			HashMap<String, Object> localDerivedColumns,
			HashMap<String, Object> localSourceColumnsByBucket,
			HashMap<String, Object> localTableAliasMap) {
		if (localInterface == null || localInterface.isEmpty()) {
			return;
		}
		for (Map.Entry<String, Object> interfaceEntry : localInterface.entrySet()) {
			Object refsObj = interfaceEntry.getValue();
			if (!(refsObj instanceof ArrayList<?> refs) || refs.isEmpty()) {
				continue;
			}
			ArrayList<Object> mutableRefs = (ArrayList<Object>) refs;
			expandRelationalModifierDerivedColumnLineageInMutableReferenceList(
					mutableRefs,
					localDerivedColumns,
					localSourceColumnsByBucket,
					localTableAliasMap);
		}
	}

	/**
	 * Phase 17.7.6 / publication step D: after phase B and {@link #stripEphemeralLocationsFromConvertEgressColumnReferences},
	 * collapse egress ref lists ({@code interface}, archived {@code filters} / {@code grouped_by} / {@code ordered_by},
	 * UPDATE assignment RHS) to one entry per {@code (name, table_ref)}. Does not prune unqualified vs qualified
	 * duplicates — the same name can be physical in one modifier bucket and derived in another.
	 */
	@SuppressWarnings("unchecked")
	private void consolidateConvertEgressColumnReferenceLists(
			HashMap<String, Object> localInterface,
			HashMap<String, Object> archivedScopeColumnReferenceContainers,
			Object updateAssignmentsObj) {
		if (localInterface != null) {
			for (Object refsObj : localInterface.values()) {
				consolidateClauseColumnReferenceListInPlace(refsObj);
			}
		}
		if (archivedScopeColumnReferenceContainers != null) {
			for (String containerKey : ARCHIVED_SCOPE_COLUMN_REFERENCE_CONTAINER_KEYS) {
				consolidateClauseColumnReferenceListInPlace(
						archivedScopeColumnReferenceContainers.get(containerKey));
			}
		}
		if (updateAssignmentsObj instanceof Map<?, ?> assignmentsMapObj) {
			for (Object rhsRefsObj : ((Map<String, Object>) assignmentsMapObj).values()) {
				consolidateClauseColumnReferenceListInPlace(rhsRefsObj);
			}
		}
	}

	private void consolidateClauseColumnReferenceListInPlace(Object columnListObj) {
		if (!(columnListObj instanceof ArrayList<?> columnRefsObj)) {
			return;
		}
		ArrayList<Object> columnRefs = (ArrayList<Object>) columnRefsObj;
		if (columnRefs.isEmpty()) {
			return;
		}
		ArrayList<Object> consolidated = new ArrayList<Object>(columnRefs.size());
		for (Object refObj : columnRefs) {
			stripEphemeralLocationsFromColumnReferenceInPlace(refObj);
			appendInterfaceReferenceIfMissing(consolidated, refObj);
		}
		columnRefs.clear();
		columnRefs.addAll(consolidated);
	}

	@SuppressWarnings("unchecked")
	private void expandRelationalModifierDerivedColumnLineageInColumnRefList(
			Object columnListObj,
			HashMap<String, Object> localDerivedColumns,
			HashMap<String, Object> localSourceColumnsByBucket,
			HashMap<String, Object> localTableAliasMap) {
		if (!(columnListObj instanceof ArrayList<?> columnRefsObj)) {
			return;
		}
		expandRelationalModifierDerivedColumnLineageInMutableReferenceList(
				(ArrayList<Object>) columnRefsObj,
				localDerivedColumns,
				localSourceColumnsByBucket,
				localTableAliasMap);
	}

	@SuppressWarnings("unchecked")
	private void expandRelationalModifierDerivedColumnLineageInMutableReferenceList(
			ArrayList<Object> refs,
			HashMap<String, Object> localDerivedColumns,
			HashMap<String, Object> localSourceColumnsByBucket,
			HashMap<String, Object> localTableAliasMap) {
		for (int index = 0; index < refs.size(); index++) {
			Object refObj = refs.get(index);
			ArrayList<Object> expandedRefs = expandRelationalModifierDerivedColumnReference(
					refObj,
					localDerivedColumns,
					localSourceColumnsByBucket,
					localTableAliasMap);
			if (expandedRefs == null || expandedRefs.isEmpty()) {
				continue;
			}
			refs.remove(index);
			refs.addAll(index, expandedRefs);
			index += expandedRefs.size() - 1;
		}
	}

	public void appendInterfaceReferenceIfMissing(ArrayList<Object> targetRefs, Object candidateRef) {
		if (candidateRef == null) {
			return;
		}

		Object egressRef = copyClauseColumnReferenceForEgress(candidateRef);
		String candidateName = walker.extractReferenceNameFromInterfaceEntry(egressRef);
		String candidateTableRef = walker.extractReferenceTableRefFromInterfaceEntry(egressRef);

		for (Object existingRef : targetRefs) {
			String existingName = walker.extractReferenceNameFromInterfaceEntry(existingRef);
			String existingTableRef = walker.extractReferenceTableRefFromInterfaceEntry(existingRef);
			if (equalsIgnoreCaseNullable(existingName, candidateName)
					&& equalsNullable(existingTableRef, candidateTableRef)) {
				return;
			}
		}

		targetRefs.add(egressRef);
	}

	/** Appends a detached egress copy for each convert-egress site (duplicates allowed until consolidate). */
	public void appendClauseColumnReferenceForConvertEgress(
			ArrayList<Object> targetRefs,
			Object candidateRef) {
		if (candidateRef == null) {
			return;
		}
		Object egressRef = copyClauseColumnReferenceForEgress(candidateRef);
		attachClauseColumnSiteTokenFromSourceSubTree(egressRef, candidateRef);
		targetRefs.add(egressRef);
	}

	public void registerClauseColumnSiteTokenForColumnSubTree(Object columnSubTree, String tokenText) {
		if (columnSubTree == null || tokenText == null || tokenText.isBlank()) {
			return;
		}
		clauseColumnSiteTokenBySubTree.put(columnSubTree, tokenText);
	}

	public void clearClauseColumnSiteTokens() {
		clauseColumnSiteTokenBySubTree.clear();
	}

	@SuppressWarnings("unchecked")
	private void attachClauseColumnSiteTokenFromSourceSubTree(Object egressRef, Object sourceColumnSubTree) {
		if (egressRef == null || sourceColumnSubTree == null) {
			return;
		}
		Object tokenSource = sourceColumnSubTree;
		if (sourceColumnSubTree instanceof Map<?, ?> sourceMap
				&& sourceMap.containsKey(MUMBLE_COLUMN_KEY)) {
			tokenSource = sourceMap.get(MUMBLE_COLUMN_KEY);
		}
		String tokenText = clauseColumnSiteTokenBySubTree.remove(tokenSource);
		if (tokenText == null || tokenText.isBlank()) {
			return;
		}
		if (!(egressRef instanceof Map<?, ?> egressMapObj)) {
			return;
		}
		ArrayList<String> locations = new ArrayList<String>(1);
		locations.add(tokenText);
		((Map<String, Object>) egressMapObj).put("locations", locations);
	}

	public void resetSelectItemUnresolvedColumnSnapshot() {
		unresolvedColumnSnapshotBeforeSelectItem = new HashMap<>();
	}

	public void rotateSelectItemUnresolvedColumnSnapshot() {
		unresolvedColumnSnapshotBeforeSelectItem = snapshotUnresolvedColumnMap();
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> snapshotUnresolvedColumnMap() {
		HashMap<String, Object> snapshot = new HashMap<>();
		Object unresolvedObj = walker.symbolTable.get(MUMBLE_UNRESOLVED_COLUMN_KEY);
		if (!(unresolvedObj instanceof Map<?, ?> unresolvedMapObj) || unresolvedMapObj.isEmpty()) {
			return snapshot;
		}
		for (Map.Entry<?, ?> entry : unresolvedMapObj.entrySet()) {
			if (entry.getKey() instanceof String key && entry.getValue() != null) {
				snapshot.put(key, cloneMaterializationTokenEntry(entry.getValue()));
			}
		}
		return snapshot;
	}

	@SuppressWarnings("unchecked")
	private Object cloneMaterializationTokenEntry(Object entry) {
		if (entry == null) {
			return null;
		}
		if (entry instanceof List<?> tokenList) {
			return new ArrayList<>(tokenList);
		}
		if (entry instanceof Map<?, ?> entryMap) {
			HashMap<String, Object> copy = new HashMap<>((Map<String, Object>) entryMap);
			Object locationsObj = copy.get("locations");
			if (locationsObj instanceof List<?> locationList) {
				copy.put("locations", new ArrayList<>(locationList));
			}
			return copy;
		}
		return entry;
	}

	@SuppressWarnings("unchecked")
	private Object materializationRefTokensAddedSinceSnapshot(
			Object currentEntry,
			Object snapshotEntry) {
		ArrayList<Object> added = new ArrayList<>();
		appendMaterializationRefTokens(added, currentEntry);
		if (snapshotEntry != null) {
			ArrayList<Object> prior = new ArrayList<>();
			appendMaterializationRefTokens(prior, snapshotEntry);
			added.removeIf(prior::contains);
		}
		return added.isEmpty() ? null : added;
	}

	/**
	 * M3/M4 bridge: ephemeral {@code locations} on interface dependency refs from per-SELECT-item
	 * unresolved deltas (convert egress only — not written to {@code query_dictionary}).
	 */
	@SuppressWarnings("unchecked")
	public void attachWalkCapturedSiteTokensToSelectItemDependencyRefs(ArrayList<Object> columnList) {
		attachWalkCapturedSiteTokensToSelectItemDependencyRefs(
				columnList,
				unresolvedColumnSnapshotBeforeSelectItem);
	}

	/**
	 * M4: attach only unresolved tokens recorded while the current SELECT item was walking (delta
	 * since the prior {@code exitSelect_item} snapshot), avoiding cross-item column-name bleed.
	 */
	@SuppressWarnings("unchecked")
	public void attachWalkCapturedSiteTokensToSelectItemDependencyRefs(
			ArrayList<Object> columnList,
			HashMap<String, Object> unresolvedSnapshotBeforeSelectItem) {
		if (columnList == null || columnList.isEmpty()) {
			return;
		}
		Object unresolvedObj = walker.symbolTable.get(MUMBLE_UNRESOLVED_COLUMN_KEY);
		if (!(unresolvedObj instanceof Map<?, ?> unresolvedMapObj) || unresolvedMapObj.isEmpty()) {
			return;
		}
		HashMap<String, Object> unresolvedMap = (HashMap<String, Object>) unresolvedMapObj;
		HashMap<String, Object> snapshot = unresolvedSnapshotBeforeSelectItem != null
				? unresolvedSnapshotBeforeSelectItem
				: new HashMap<>();
		for (Object refObj : columnList) {
			if (!(refObj instanceof Map<?, ?> refMapObj)) {
				continue;
			}
			String columnName = walker.extractReferenceNameFromInterfaceEntry(refObj);
			if (columnName == null || columnName.isBlank()) {
				continue;
			}
			Object unresolvedEntry = getUnqualifiedUnknownEntry(unresolvedMap, columnName);
			if (unresolvedEntry == null) {
				continue;
			}
			Object snapshotEntry = getUnqualifiedUnknownEntry(snapshot, columnName);
			Object deltaTokens = materializationRefTokensAddedSinceSnapshot(
					unresolvedEntry,
					snapshotEntry);
			if (deltaTokens == null && snapshotEntry == null) {
				ArrayList<Object> initialSites = new ArrayList<>();
				appendMaterializationRefTokens(initialSites, unresolvedEntry);
				deltaTokens = initialSites.isEmpty() ? null : initialSites;
			}
			if (deltaTokens == null) {
				continue;
			}
			ArrayList<Object> siteTokens = new ArrayList<Object>();
			Object existingLocationsObj = ((Map<String, Object>) refMapObj).get("locations");
			if (existingLocationsObj instanceof List<?> existingLocations) {
				for (Object existingLocation : existingLocations) {
					if (existingLocation != null && !siteTokens.contains(existingLocation)) {
						siteTokens.add(existingLocation);
					}
				}
			}
			appendMaterializationRefTokens(siteTokens, deltaTokens);
			if (!siteTokens.isEmpty()) {
				((Map<String, Object>) refObj).put("locations", siteTokens);
			}
		}
	}

	/**
	 * Detached egress copy of a clause/interface column ref (never the AST {@code column} subtree).
	 * Omits ephemeral {@code locations}; token payloads live on {@code unresolved_column_map} until materialized.
	 */
	@SuppressWarnings("unchecked")
	public Object copyClauseColumnReferenceForEgress(Object columnRefObj) {
		if (columnRefObj == null) {
			return null;
		}
		if (!(columnRefObj instanceof Map<?, ?> sourceMapObj)) {
			return columnRefObj;
		}

		Map<String, Object> sourceMap = (Map<String, Object>) sourceMapObj;
		if (sourceMap.containsKey(MUMBLE_COLUMN_KEY)
				&& sourceMap.get(MUMBLE_COLUMN_KEY) instanceof Map<?, ?>) {
			return copyClauseColumnReferenceForEgress(sourceMap.get(MUMBLE_COLUMN_KEY));
		}

		HashMap<String, Object> egressMap = new HashMap<String, Object>();
		for (Map.Entry<String, Object> entry : sourceMap.entrySet()) {
			if ("locations".equals(entry.getKey())) {
				continue;
			}
			egressMap.put(entry.getKey(), entry.getValue());
		}
		return egressMap.isEmpty() ? columnRefObj : egressMap;
	}

	@SuppressWarnings("unchecked")
	public void stripEphemeralLocationsFromColumnReferenceInPlace(Object refObj) {
		if (!(refObj instanceof Map<?, ?> refMapObj)) {
			return;
		}
		((Map<String, Object>) refMapObj).remove("locations");
		((Map<String, Object>) refMapObj).remove(MUMBLE_UNRESOLVED_INGRESS_SITE_KEY);
		Object columnObj = ((Map<String, Object>) refMapObj).get(MUMBLE_COLUMN_KEY);
		if (columnObj instanceof Map<?, ?> columnMapObj) {
			((Map<String, Object>) columnMapObj).remove("locations");
		}
	}

	@SuppressWarnings("unchecked")
	public void stripEphemeralLocationsFromColumnReferenceListInPlace(Object columnListObj) {
		if (!(columnListObj instanceof ArrayList<?> columnRefs)) {
			return;
		}
		for (Object refObj : (ArrayList<Object>) columnRefs) {
			stripEphemeralLocationsFromColumnReferenceInPlace(refObj);
		}
	}

	@SuppressWarnings("unchecked")
	public void stripEphemeralLocationsFromConvertEgressColumnReferences(
			HashMap<String, Object> localInterface,
			HashMap<String, Object> archivedScopeColumnReferenceContainers,
			Object updateAssignmentsObj) {
		if (localInterface != null) {
			for (Object refsObj : localInterface.values()) {
				stripEphemeralLocationsFromColumnReferenceListInPlace(refsObj);
			}
		}
		if (archivedScopeColumnReferenceContainers != null) {
			for (String containerKey : ARCHIVED_SCOPE_COLUMN_REFERENCE_CONTAINER_KEYS) {
				stripEphemeralLocationsFromColumnReferenceListInPlace(
						archivedScopeColumnReferenceContainers.get(containerKey));
			}
		}
		if (updateAssignmentsObj instanceof Map<?, ?> assignmentsMapObj) {
			for (Object rhsRefsObj : ((Map<String, Object>) assignmentsMapObj).values()) {
				stripEphemeralLocationsFromColumnReferenceListInPlace(rhsRefsObj);
			}
		}
	}

	/**
	 * When clause harvest is deferred ({@link #defersRelationalModifierClauseHarvestColumnRefList}),
	 * archived clause lists skip probe-based token merge. Mirror interface-output site tokens from
	 * ephemeral {@code locations} (and unresolved fallbacks) onto {@code query_dictionary} before
	 * locations are stripped.
	 */
	@SuppressWarnings("unchecked")
	private void mergeDeferredClauseHarvestSiteTokensIntoQueryDictionary(
			HashMap<String, Object> localInterface,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> archivedScopeColumnReferenceContainers,
			Object updateAssignmentsObj) {
		if (localInterface == null || localCurrentQueryDictionary == null) {
			return;
		}
		if (archivedScopeColumnReferenceContainers != null) {
			for (String containerKey : ARCHIVED_SCOPE_COLUMN_REFERENCE_CONTAINER_KEYS) {
				if (!defersRelationalModifierClauseHarvestColumnRefList(containerKey)) {
					continue;
				}
				mergeClauseColumnListSiteTokensIntoQueryDictionary(
						archivedScopeColumnReferenceContainers.get(containerKey),
						localInterface,
						localCurrentQueryDictionary,
						containerKey);
			}
		}
		if (defersRelationalModifierClauseHarvestColumnRefList(UPDATE_ASSIGNMENT_RHS_CLAUSE_PROBE_KEY)
				&& updateAssignmentsObj instanceof Map<?, ?> assignmentsMapObj) {
			for (Object rhsRefsObj : ((Map<String, Object>) assignmentsMapObj).values()) {
				mergeClauseColumnListSiteTokensIntoQueryDictionary(
						rhsRefsObj,
						localInterface,
						localCurrentQueryDictionary,
						UPDATE_ASSIGNMENT_RHS_CLAUSE_PROBE_KEY);
			}
		}
	}

	/** Window lists are merged after phase B so derived/source lineage and site {@code locations} exist. */
	@SuppressWarnings("unchecked")
	private void mergeDeferredWindowClauseHarvestSiteTokensIntoQueryDictionary(
			HashMap<String, Object> localInterface,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> archivedScopeColumnReferenceContainers) {
		if (localCurrentQueryDictionary == null || archivedScopeColumnReferenceContainers == null) {
			return;
		}
		if (!convertEgressScopeHasRelationalModifierStructuredDerivation()) {
			return;
		}
		mergeClauseColumnListSiteTokensIntoQueryDictionary(
				archivedScopeColumnReferenceContainers.get(MUMBLE_WINDOW_PARTITION_BY_KEY),
				localInterface,
				localCurrentQueryDictionary,
				MUMBLE_WINDOW_PARTITION_BY_KEY);
		mergeClauseColumnListSiteTokensIntoQueryDictionary(
				archivedScopeColumnReferenceContainers.get(MUMBLE_WINDOW_ORDERED_BY_KEY),
				localInterface,
				localCurrentQueryDictionary,
				MUMBLE_WINDOW_ORDERED_BY_KEY);
	}

	private static boolean mergesQueryDictionaryTokensForAllColumnNamesInClauseList(String containerKey) {
		// 17.6.9: window_partition_by / window_ordered_by use the same rule as other clause lists —
		// query_dictionary tokens only for names that are SELECT-list interface outputs.
		return false;
	}

	@SuppressWarnings("unchecked")
	private void mergeClauseColumnListSiteTokensIntoQueryDictionary(
			Object columnListObj,
			HashMap<String, Object> localInterface,
			HashMap<String, Object> localCurrentQueryDictionary,
			String containerKey) {
		if (!(columnListObj instanceof ArrayList<?> columnRefsObj)) {
			return;
		}
		boolean mergeAllNamedColumns = mergesQueryDictionaryTokensForAllColumnNamesInClauseList(containerKey);
		for (Object refObj : (ArrayList<Object>) columnRefsObj) {
			String columnName = walker.extractReferenceNameFromInterfaceEntry(refObj);
			if (columnName == null || columnName.isBlank()) {
				continue;
			}
			if (!mergeAllNamedColumns
					&& !isInterfaceOutputColumnName(localInterface, columnName)) {
				continue;
			}
			String dictionaryKey = findKeyIgnoreCase(localInterface, columnName);
			if (dictionaryKey == null) {
				dictionaryKey = columnName;
			}
			Object tokenPayload = extractClauseColumnReferenceSiteTokenPayload(refObj);
			if (tokenPayload == null) {
				continue;
			}
			walker.mergeResolvedColumnIntoDictionary(
					localCurrentQueryDictionary,
					dictionaryKey,
					tokenPayload);
		}
	}

	private Object extractClauseColumnReferenceSiteTokenPayload(Object refObj) {
		ArrayList<Object> tokens = new ArrayList<Object>();
		appendMaterializationRefTokens(tokens, refObj);
		return tokens.isEmpty() ? null : tokens;
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

	public boolean equalsNullable(String left, String right) {
		if (left == null && right == null) {
			return true;
		}
		if (left == null || right == null) {
			return false;
		}
		return left.equals(right);
	}

	/**
	 * After structured UNPIVOT {@code derived_columns} / {@code source_columns} are populated,
	 * removes columns definitively resolved by the operator from {@code unresolved_column}.
	 */
	public void resolveUnpivotGeneratedColumnsAtWalkScope(
			String interfaceSourceRef,
			String dictionarySourceRef,
			String valueColumn,
			String forColumn,
			ArrayList<String> inColumns,
			HashMap<String, Object> unresolvedColumnMap) {
		if (interfaceSourceRef == null
				|| interfaceSourceRef.isBlank()
				|| unresolvedColumnMap == null
				|| unresolvedColumnMap.isEmpty()) {
			return;
		}

		String dictionaryPhysicalSourceRef = dictionarySourceRef;
		if (dictionaryPhysicalSourceRef == null || dictionaryPhysicalSourceRef.isBlank()) {
			dictionaryPhysicalSourceRef = interfaceSourceRef;
		}

		if (valueColumn != null && !valueColumn.isBlank()) {
			removeUnpivotGeneratedColumnReference(
					unresolvedColumnMap,
					interfaceSourceRef,
					valueColumn,
					true);
		}
		if (forColumn != null && !forColumn.isBlank()) {
			removeUnpivotGeneratedColumnReference(
					unresolvedColumnMap,
					interfaceSourceRef,
					forColumn,
					true);
		}

		if (inColumns != null && !inColumns.isEmpty()) {
			String canonicalSourceRef = resolveCanonicalPhysicalTableRef(
					dictionaryPhysicalSourceRef,
					getTableAliasMapFromSymbolTable());
			if (canonicalSourceRef == null || canonicalSourceRef.isBlank()) {
				canonicalSourceRef = dictionaryPhysicalSourceRef;
			}
			for (String inColumn : inColumns) {
				if (inColumn == null || inColumn.isBlank()) {
					continue;
				}
				materializeUnpivotInColumnFromStructuredSourceDictionary(canonicalSourceRef, inColumn);
				materializeUnpivotInColumnFromOperandTokenRefs(canonicalSourceRef, inColumn);
				removeAndMaterializeUnpivotResolvedColumn(
						unresolvedColumnMap,
						dictionaryPhysicalSourceRef,
						inColumn,
						false);
			}
		}

		if (!walker.isNonTableQuerySourceReference(interfaceSourceRef)) {
			return;
		}
		Object sourceQueryScopeObj = walker.symbolTable.get(interfaceSourceRef);
		if (!(sourceQueryScopeObj instanceof Map<?, ?> sourceQueryScopeMapObj)) {
			return;
		}
		Map<String, Object> sourceQueryScope = (Map<String, Object>) sourceQueryScopeMapObj;
		Object interfaceObj = sourceQueryScope.get(MUMBLE_INTERFACE_KEY);
		if (!(interfaceObj instanceof Map<?, ?> interfaceMapObj)) {
			return;
		}
		Map<String, Object> sourceInterface = (Map<String, Object>) interfaceMapObj;
		for (String interfaceColName : new ArrayList<>(sourceInterface.keySet())) {
			removeFromUnresolvedMapCaseInsensitive(unresolvedColumnMap, interfaceColName);
		}
	}

	@SuppressWarnings("unchecked")
	private void materializeUnpivotInColumnFromOperandTokenRefs(
			String canonicalSourceRef,
			String inColumn) {
		if (canonicalSourceRef == null || canonicalSourceRef.isBlank()
				|| inColumn == null || inColumn.isBlank()) {
			return;
		}
		Object operandRefsObj = walker.symbolTable.get(RELATIONAL_MODIFIER_OPERAND_TOKEN_REFS_KEY);
		if (!(operandRefsObj instanceof Map<?, ?> operandRefsMapObj) || operandRefsMapObj.isEmpty()) {
			return;
		}
		Map<String, Object> operandRefsMap = (Map<String, Object>) operandRefsMapObj;
		Object tokenPayload = operandRefsMap.get(inColumn);
		if (tokenPayload == null) {
			for (Map.Entry<String, Object> entry : operandRefsMap.entrySet()) {
				String key = entry.getKey();
				if (key == null || !key.contains(".")) {
					continue;
				}
				String keyColumn = key.substring(key.lastIndexOf('.') + 1);
				if (keyColumn.equalsIgnoreCase(inColumn)) {
					tokenPayload = entry.getValue();
					break;
				}
			}
		}
		if (tokenPayload == null) {
			return;
		}
		HashMap<String, Object> localTableDictionary = walker.getCurrentTableDictionary();
		mergeSourceLineageIntoPhysicalTableDictionary(
				localTableDictionary,
				canonicalSourceRef,
				inColumn,
				tokenPayload);
	}

	@SuppressWarnings("unchecked")
	private void materializeUnpivotInColumnFromStructuredSourceDictionary(
			String canonicalSourceRef,
			String inColumn) {
		if (canonicalSourceRef == null || canonicalSourceRef.isBlank()
				|| inColumn == null || inColumn.isBlank()) {
			return;
		}
		Object sourceColumnsObj = walker.symbolTable.get(RELATIONAL_MODIFIER_SOURCE_COLUMNS_KEY);
		if (!(sourceColumnsObj instanceof Map<?, ?> sourceColumnsMapObj)) {
			return;
		}
		Object tokenPayload = ((Map<String, Object>) sourceColumnsMapObj).get(inColumn);
		if (tokenPayload == null) {
			for (Map.Entry<String, Object> entry : ((Map<String, Object>) sourceColumnsMapObj).entrySet()) {
				if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(inColumn)) {
					tokenPayload = entry.getValue();
					break;
				}
			}
		}
		if (tokenPayload == null) {
			return;
		}
		HashMap<String, Object> localTableDictionary = walker.getCurrentTableDictionary();
		mergeSourceLineageIntoPhysicalTableDictionary(
				localTableDictionary,
				canonicalSourceRef,
				inColumn,
				tokenPayload);
	}

	@SuppressWarnings("unchecked")
	private void materializePivotOperandStructuredBucketsAtConvertEgress(
			RelationalModifierConvertEgressContext relationalModifierContext,
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> localPhysicalTableCollection,
			HashMap<String, Object> localTableAliasMap) {
		if (relationalModifierContext == null
				|| relationalModifierContext.isEmpty()
				|| !relationalModifierContext.isPivot()
				|| localTableCollection == null
				|| localPhysicalTableCollection == null
				|| localPhysicalTableCollection.isEmpty()) {
			return;
		}

		String materializeTableRef = resolvePivotOperandMaterializationTableRefFromContext(
				relationalModifierContext,
				localPhysicalTableCollection,
				localTableAliasMap);
		if (materializeTableRef == null || materializeTableRef.isBlank()) {
			return;
		}

		materializePivotOperandColumnsFromStructuredSourceBuckets(
				relationalModifierContext,
				materializeTableRef,
				localTableCollection);
	}

	@SuppressWarnings("unchecked")
	private void drainPivotOperandColumnsFromUnresolvedMap(
			RelationalModifierConvertEgressContext relationalModifierContext,
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> localPhysicalTableCollection,
			HashMap<String, Object> localTableAliasMap) {
		if (relationalModifierContext == null
				|| relationalModifierContext.isEmpty()
				|| !relationalModifierContext.isPivot()
				|| localTableCollection == null
				|| localPhysicalTableCollection == null
				|| localPhysicalTableCollection.isEmpty()) {
			return;
		}

		if (unresolvedColumnMap == null) {
			unresolvedColumnMap = new HashMap<String, Object>();
		}

		String materializeTableRef = resolvePivotOperandMaterializationTableRefFromContext(
				relationalModifierContext,
				localPhysicalTableCollection,
				localTableAliasMap);
		if (materializeTableRef == null || materializeTableRef.isBlank()) {
			return;
		}

		String pivotSourceRef = relationalModifierContext.interfaceSourceRef;
		if (pivotSourceRef != null && !pivotSourceRef.isBlank()) {
			pivotSourceRef = resolveCanonicalPhysicalTableRef(pivotSourceRef, localTableAliasMap);
		}
		for (String operandColumnName : collectPivotOperandColumnNamesFromContext(relationalModifierContext)) {
			materializePivotOperandColumnAtConvertEgress(
					operandColumnName,
					null,
					materializeTableRef,
					pivotSourceRef,
					unresolvedColumnMap,
					localTableCollection,
					localTableAliasMap);
		}
	}

	/**
	 * Materialize PIVOT aggregate/FOR operand columns captured as unqualified refs and
	 * clear them from {@code unresolved_column} so scope exit does not emit false
	 * unresolved diagnostics. Operand tokens bind to the local physical table that is
	 * not the pivot source's underlying physical lineage (typically the joined target
	 * table in PIVOT ... JOIN patterns).
	 */
	@SuppressWarnings("unchecked")
	private void resolvePivotOperandColumnsFromUnresolvedMap(
			RelationalModifierConvertEgressContext relationalModifierContext,
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> localPhysicalTableCollection,
			HashMap<String, Object> localTableAliasMap) {
		materializePivotOperandStructuredBucketsAtConvertEgress(
				relationalModifierContext,
				localTableCollection,
				localPhysicalTableCollection,
				localTableAliasMap);
		drainPivotOperandColumnsFromUnresolvedMap(
				relationalModifierContext,
				unresolvedColumnMap,
				localTableCollection,
				localPhysicalTableCollection,
				localTableAliasMap);
	}

	private String resolvePivotOperandMaterializationTableRefFromContext(
			RelationalModifierConvertEgressContext ctx,
			HashMap<String, Object> localPhysicalTableCollection,
			HashMap<String, Object> localTableAliasMap) {
		if (ctx == null || localPhysicalTableCollection == null || localPhysicalTableCollection.isEmpty()) {
			return null;
		}
		if (ctx.dictionaryPhysicalSourceRef != null && !ctx.dictionaryPhysicalSourceRef.isBlank()) {
			String canonical = resolveCanonicalPhysicalTableRef(
					ctx.dictionaryPhysicalSourceRef,
					localTableAliasMap);
			if (canonical != null && !canonical.isBlank()) {
				for (String physicalRef : localPhysicalTableCollection.keySet()) {
					if (!physicalRef.equalsIgnoreCase(canonical)) {
						return physicalRef;
					}
				}
			}
		}
		if (localPhysicalTableCollection.size() == 1) {
			return localPhysicalTableCollection.keySet().iterator().next();
		}
		for (String physicalRef : localPhysicalTableCollection.keySet()) {
			if (ctx.interfaceSourceRef == null
					|| !physicalRef.equalsIgnoreCase(ctx.interfaceSourceRef)) {
				return physicalRef;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private void materializePivotOperandColumnsFromStructuredSourceBuckets(
			RelationalModifierConvertEgressContext ctx,
			String materializeTableRef,
			HashMap<String, Object> localTableCollection) {
		if (ctx == null || materializeTableRef == null || materializeTableRef.isBlank()) {
			return;
		}
		HashMap<String, Object> targetDictionary = ensureTableDictionaryEntry(localTableCollection, materializeTableRef);
		for (Object bucketObj : ctx.sourceColumnsByBucket.values()) {
			if (!(bucketObj instanceof Map<?, ?> sourceMap)) {
				continue;
			}
			for (Map.Entry<?, ?> entry : sourceMap.entrySet()) {
				if (!(entry.getKey() instanceof String columnName) || columnName.isBlank()) {
					continue;
				}
				Object tokenPayload = entry.getValue();
				if (tokenPayload != null) {
					walker.mergeResolvedColumnIntoDictionary(targetDictionary, columnName, tokenPayload);
				}
			}
		}
	}

	private HashMap<String, Object> ensureTableDictionaryEntry(
			HashMap<String, Object> localTableCollection,
			String tableRef) {
		Object dictionaryObj = localTableCollection.get(tableRef);
		if (dictionaryObj instanceof HashMap<?, ?>) {
			return (HashMap<String, Object>) dictionaryObj;
		}
		HashMap<String, Object> dictionary = new HashMap<String, Object>();
		localTableCollection.put(tableRef, dictionary);
		return dictionary;
	}

	/** Phase 16.1: pivot operand bind target for convert egress. */
	private static final class PivotOperandBinding {
		final String materializeTableRef;
		final String pivotSourceRef;

		private PivotOperandBinding(String materializeTableRef, String pivotSourceRef) {
			this.materializeTableRef = materializeTableRef;
			this.pivotSourceRef = pivotSourceRef;
		}
	}

	/** Phase 17.1: UNPIVOT namespace bind target for convert egress. */
	private enum UnpivotBindingKind {
		VALUE,
		FOR,
		IN_SOURCE
	}

	private static final class UnpivotBinding {
		final UnpivotBindingKind kind;
		final String materializeTableRef;
		final String unpivotSourceRef;

		private UnpivotBinding(
				UnpivotBindingKind kind,
				String materializeTableRef,
				String unpivotSourceRef) {
			this.kind = kind;
			this.materializeTableRef = materializeTableRef;
			this.unpivotSourceRef = unpivotSourceRef;
		}
	}

	@SuppressWarnings("unchecked")
	private UnpivotBinding resolveUnpivotBindingAtConvertEgress(
			String columnName,
			String tableRef,
			RelationalModifierConvertEgressContext relationalModifierContext,
			HashMap<String, Object> localPhysicalTableCollection,
			HashMap<String, Object> localTableAliasMap,
			HashMap<String, Object> aliasMapForHintMatch) {
		if (columnName == null
				|| columnName.isBlank()
				|| relationalModifierContext == null
				|| relationalModifierContext.isEmpty()) {
			return null;
		}

		HashMap<String, Object> hintAliasMap = aliasMapForHintMatch != null
				? aliasMapForHintMatch
				: localTableAliasMap;
		boolean qualifiedShape = tableRef != null && !tableRef.isBlank() && !"*".equals(tableRef);

		if (!relationalModifierContext.isUnpivot()) {
			return null;
		}

		String unpivotSourceRef = relationalModifierContext.interfaceSourceRef;
		if (unpivotSourceRef != null && !unpivotSourceRef.isBlank()) {
			unpivotSourceRef = resolveCanonicalPhysicalTableRef(unpivotSourceRef, localTableAliasMap);
		}

		InferredUnpivotDerivedOutputs unpivotOutputs =
				inferUnpivotDerivedOutputColumnsFromContext(relationalModifierContext);
		if (unpivotOutputs == null) {
			return null;
		}

		String valueColumn = unpivotOutputs.valueColumn;
		if (valueColumn != null
				&& !valueColumn.isBlank()
				&& valueColumn.equalsIgnoreCase(columnName)) {
			if (qualifiedShape
					&& !structuredContextSourceRefMatches(relationalModifierContext, tableRef, hintAliasMap)) {
				return null;
			}
			return new UnpivotBinding(UnpivotBindingKind.VALUE, null, unpivotSourceRef);
		}

		String forColumn = unpivotOutputs.forColumn;
		if (forColumn != null
				&& !forColumn.isBlank()
				&& forColumn.equalsIgnoreCase(columnName)) {
			if (qualifiedShape
					&& !structuredContextSourceRefMatches(relationalModifierContext, tableRef, hintAliasMap)) {
				return null;
			}
			return new UnpivotBinding(UnpivotBindingKind.FOR, null, unpivotSourceRef);
		}

		for (String inColumn : collectUnpivotInColumnNamesFromContext(relationalModifierContext)) {
			if (!inColumn.equalsIgnoreCase(columnName)) {
				continue;
			}
			if (qualifiedShape
					&& !structuredContextSourceRefMatches(relationalModifierContext, tableRef, hintAliasMap)) {
				continue;
			}
			String materializeTableRef = resolveUnpivotInSourceMaterializationTableRefFromContext(
					relationalModifierContext,
					localPhysicalTableCollection,
					localTableAliasMap);
			if (materializeTableRef == null || materializeTableRef.isBlank()) {
				continue;
			}
			return new UnpivotBinding(
					UnpivotBindingKind.IN_SOURCE,
					materializeTableRef,
					unpivotSourceRef);
		}
		return null;
	}

	private String resolveUnpivotInSourceMaterializationTableRefFromContext(
			RelationalModifierConvertEgressContext ctx,
			HashMap<String, Object> localPhysicalTableCollection,
			HashMap<String, Object> localTableAliasMap) {
		if (ctx == null) {
			return null;
		}
		if (ctx.dictionaryPhysicalSourceRef != null && !ctx.dictionaryPhysicalSourceRef.isBlank()) {
			String canonicalPhysicalRef = resolveCanonicalPhysicalTableRef(
					ctx.dictionaryPhysicalSourceRef,
					localTableAliasMap);
			if (canonicalPhysicalRef != null && !canonicalPhysicalRef.isBlank()) {
				return canonicalPhysicalRef;
			}
		}
		return resolveCanonicalPhysicalTableRef(ctx.interfaceSourceRef, localTableAliasMap);
	}

	@SuppressWarnings("unchecked")
	private PivotOperandBinding resolvePivotOperandBindingAtConvertEgress(
			String columnName,
			String tableRef,
			RelationalModifierConvertEgressContext relationalModifierContext,
			HashMap<String, Object> localPhysicalTableCollection,
			HashMap<String, Object> localTableAliasMap,
			HashMap<String, Object> aliasMapForHintMatch) {
		if (columnName == null
				|| columnName.isBlank()
				|| relationalModifierContext == null
				|| relationalModifierContext.isEmpty()
				|| localPhysicalTableCollection == null
				|| localPhysicalTableCollection.isEmpty()) {
			return null;
		}

		HashMap<String, Object> hintAliasMap = aliasMapForHintMatch != null
				? aliasMapForHintMatch
				: localTableAliasMap;
		boolean qualifiedShape = tableRef != null && !tableRef.isBlank() && !"*".equals(tableRef);

		if (!relationalModifierContext.isPivot()
				|| !containsStringIgnoreCase(
						collectPivotOperandColumnNamesFromContext(relationalModifierContext),
						columnName)) {
			return null;
		}
		if (qualifiedShape
				&& !structuredContextSourceRefMatches(relationalModifierContext, tableRef, hintAliasMap)) {
			return null;
		}

		String materializeTableRef = resolvePivotOperandMaterializationTableRefFromContext(
				relationalModifierContext,
				localPhysicalTableCollection,
				localTableAliasMap);
		if (materializeTableRef == null || materializeTableRef.isBlank()) {
			return null;
		}
		String pivotSourceRef = relationalModifierContext.interfaceSourceRef;
		return new PivotOperandBinding(materializeTableRef, pivotSourceRef);
	}

	private void materializePivotOperandColumnAtConvertEgress(
			String columnName,
			String tableRef,
			String materializeTableRef,
			String pivotSourceRef,
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> localTableAliasMap) {
		if (materializeTableRef == null
				|| materializeTableRef.isBlank()
				|| unresolvedColumnMap == null
				|| localTableCollection == null) {
			return;
		}

		Object unresolvedEntry;
		if (tableRef != null && !tableRef.isBlank() && !"*".equals(tableRef)) {
			unresolvedEntry = consumeQualifiedUnknownEntry(unresolvedColumnMap, tableRef, columnName);
			if (unresolvedEntry == null) {
				String resolvedSourceRef = walker.resolveAliasToTableName(tableRef, localTableAliasMap);
				if (resolvedSourceRef != null
						&& !resolvedSourceRef.isBlank()
						&& !resolvedSourceRef.equalsIgnoreCase(tableRef)) {
					unresolvedEntry = consumeQualifiedUnknownEntry(
							unresolvedColumnMap,
							resolvedSourceRef,
							columnName);
				}
			}
		} else {
			unresolvedEntry = consumePivotOperandUnresolvedEntry(
					unresolvedColumnMap,
					columnName,
					pivotSourceRef,
					localTableAliasMap);
		}
		if (unresolvedEntry == null) {
			return;
		}
		mergeSourceLineageIntoPhysicalTableDictionary(
				localTableCollection,
				materializeTableRef,
				columnName,
				unresolvedEntry);
	}

	private void applyConvertEgressPivotOperandMaterialization(
			ConvertEgressColumnResolutionResult egressResult,
			String columnName,
			String tableRef,
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> localTableCollection,
			RelationalModifierConvertEgressContext relationalModifierContext,
			HashMap<String, Object> localTableAliasMap) {
		if (!egressResult.isPivotOperandColumn()) {
			return;
		}
		String pivotSourceRef = null;
		if (relationalModifierContext != null) {
			PivotOperandBinding binding = resolvePivotOperandBindingAtConvertEgress(
					columnName,
					tableRef,
					relationalModifierContext,
					buildLocalPhysicalFromTableCollection(localTableCollection),
					localTableAliasMap,
					localTableAliasMap);
			if (binding != null) {
				pivotSourceRef = binding.pivotSourceRef;
			}
		}
		materializePivotOperandColumnAtConvertEgress(
				columnName,
				tableRef,
				egressResult.pivotOperandMaterializeTableRef(),
				pivotSourceRef,
				unresolvedColumnMap,
				localTableCollection,
				localTableAliasMap);
	}

	private void applyConvertEgressUnpivotInSourceMaterialization(
			ConvertEgressColumnResolutionResult egressResult,
			String columnName,
			String tableRef,
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> localTableCollection,
			RelationalModifierConvertEgressContext relationalModifierContext,
			HashMap<String, Object> localTableAliasMap) {
		if (!egressResult.isUnpivotInSourceColumn()) {
			return;
		}
		String unpivotSourceRef = null;
		if (relationalModifierContext != null) {
			UnpivotBinding binding = resolveUnpivotBindingAtConvertEgress(
					columnName,
					tableRef,
					relationalModifierContext,
					buildLocalPhysicalFromTableCollection(localTableCollection),
					localTableAliasMap,
					localTableAliasMap);
			if (binding != null) {
				unpivotSourceRef = binding.unpivotSourceRef;
			}
		}
		materializePivotOperandColumnAtConvertEgress(
				columnName,
				tableRef,
				egressResult.pivotOperandMaterializeTableRef(),
				unpivotSourceRef,
				unresolvedColumnMap,
				localTableCollection,
				localTableAliasMap);
	}

	private Object consumePivotOperandUnresolvedEntry(
			HashMap<String, Object> unresolvedColumnMap,
			String operandColumnName,
			String pivotSourceRef,
			HashMap<String, Object> localTableAliasMap) {
		Object unresolvedEntry = consumeUnqualifiedUnknownEntry(unresolvedColumnMap, operandColumnName);
		if (unresolvedEntry != null || pivotSourceRef == null || pivotSourceRef.isBlank()) {
			return unresolvedEntry;
		}

		unresolvedEntry = consumeQualifiedUnknownEntry(unresolvedColumnMap, pivotSourceRef, operandColumnName);
		if (unresolvedEntry != null) {
			return unresolvedEntry;
		}

		String resolvedSourceRef = walker.resolveAliasToTableName(pivotSourceRef, localTableAliasMap);
		if (resolvedSourceRef != null
				&& !resolvedSourceRef.isBlank()
				&& !resolvedSourceRef.equalsIgnoreCase(pivotSourceRef)) {
			unresolvedEntry = consumeQualifiedUnknownEntry(
					unresolvedColumnMap,
					resolvedSourceRef,
					operandColumnName);
		}
		return unresolvedEntry;
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> getTableAliasMapFromSymbolTable() {
		Object tableAliasObj = walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY);
		if (tableAliasObj instanceof Map<?, ?> tableAliasMapObj && !tableAliasMapObj.isEmpty()) {
			return new HashMap<String, Object>((Map<String, Object>) tableAliasMapObj);
		}
		return new HashMap<String, Object>();
	}

	private boolean containsStringIgnoreCase(ArrayList<String> values, String candidate) {
		if (values == null || candidate == null) {
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
		String canonicalTargetRef = resolveCanonicalPhysicalTableRef(sourceRef, tableAliasMap);
		if (canonicalTargetRef != null && !canonicalTargetRef.isBlank()) {
			dictionaryTargetRef = canonicalTargetRef;
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

	@SuppressWarnings("unchecked")
	public void materializeSelectedUnpivotInColumnsIntoSourceDictionary(
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> localTableAliasMap,
			RelationalModifierConvertEgressContext relationalModifierContext) {
		if (localCurrentQueryDictionary == null || localCurrentQueryDictionary.isEmpty()
				|| localTableCollection == null
				|| relationalModifierContext == null
				|| relationalModifierContext.isEmpty()
				|| !relationalModifierContext.isUnpivot()) {
			return;
		}

		ArrayList<String> inColumns = collectUnpivotInColumnNamesFromContext(relationalModifierContext);
		if (inColumns.isEmpty()) {
			return;
		}

		String dictionaryTargetRef = relationalModifierContext.dictionaryPhysicalSourceRef;
		if (dictionaryTargetRef == null || dictionaryTargetRef.isBlank()) {
			dictionaryTargetRef = relationalModifierContext.interfaceSourceRef;
		}
		if (dictionaryTargetRef == null || dictionaryTargetRef.isBlank()) {
			return;
		}

		String canonicalTargetRef = resolveCanonicalPhysicalTableRef(
				dictionaryTargetRef,
				localTableAliasMap);
		if (canonicalTargetRef != null && !canonicalTargetRef.isBlank()) {
			dictionaryTargetRef = canonicalTargetRef;
		}

		HashMap<String, Object> sourceDictionary =
				ensureTableDictionaryEntry(localTableCollection, dictionaryTargetRef);

		for (String inColumn : inColumns) {
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

	/**
	 * Publishes PIVOT derived registry keys onto matching {@code localInterface} entries.
	 *
	 * <p>Walks structured {@code derivation.derived_columns} buckets
	 * ({@code RelationalModifierConvertEgressContext#derivedColumnsByBucket}) and, for each
	 * registry name that already appears in the interface (case-insensitive), appends a derived
	 * ref. Skips names that are ambiguous across sibling modifier buckets (Phase 17.6.3).
	 *
	 * <p>Does not rebuild names from aggregate × IN-list hints — those keys are walk-finalized
	 * into the structured registry before convert egress. Legacy
	 * {@code pivot_aggregate_columns} / {@code pivot_in_columns} fallbacks are retired.
	 */
	@SuppressWarnings("unchecked")
	public void applyPivotValueInterfaceDerivations(
			HashMap<String, Object> localInterface,
			RelationalModifierConvertEgressContext relationalModifierContext,
			HashMap<String, Object> localDerivedColumns) {
		if (localInterface == null || localInterface.isEmpty()
				|| relationalModifierContext == null
				|| relationalModifierContext.isEmpty()
				|| !relationalModifierContext.isPivot()) {
			return;
		}

		for (Object bucketObj : relationalModifierContext.derivedColumnsByBucket.values()) {
			if (!(bucketObj instanceof Map<?, ?> bucketMap)) {
				continue;
			}
			for (String derivedColumnName : ((Map<String, Object>) bucketMap).keySet()) {
				if (derivedColumnName == null || derivedColumnName.isBlank()) {
					continue;
				}
				// Do not publish pivot derived interface lineage for names that are
				// ambiguous across sibling modifiers (17.6.3 parity with 17.6.2 UNPIVOT).
				if (isAmbiguousUnqualifiedStructuredDerivedColumn(
						derivedColumnName,
						null,
						localDerivedColumns)) {
					continue;
				}
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
	private String resolveUnpivotHintModifierTableRef(
			String columnName,
			RelationalModifierConvertEgressContext ctx) {
		if (columnName == null || columnName.isBlank() || ctx == null || !ctx.isUnpivot()) {
			return null;
		}
		String sourceRef = ctx.interfaceSourceRef;
		if (sourceRef == null || sourceRef.isBlank()) {
			return null;
		}
		InferredUnpivotDerivedOutputs unpivotOutputs = inferUnpivotDerivedOutputColumnsFromContext(ctx);
		if (unpivotOutputs == null) {
			return null;
		}
		if (unpivotOutputs.valueColumn != null && unpivotOutputs.valueColumn.equalsIgnoreCase(columnName)) {
			return sourceRef;
		}
		if (unpivotOutputs.forColumn != null && unpivotOutputs.forColumn.equalsIgnoreCase(columnName)) {
			return sourceRef;
		}
		return null;
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
				false,
				false);
	}

	public HashMap<String, Object> convertSymbolTableToTableDictionary(
			boolean emitFinalUnresolvedUnknownFatal,
			boolean deferCorrelatedValueSubqueryQualifiedUnknowns,
			String updateTargetTableRef,
			boolean updateHasFromClause) {
		return convertSymbolTableToTableDictionary(
				emitFinalUnresolvedUnknownFatal,
				deferCorrelatedValueSubqueryQualifiedUnknowns,
				updateTargetTableRef,
				updateHasFromClause,
				false);
	}

	public HashMap<String, Object> convertSymbolTableToTableDictionary(
			boolean emitFinalUnresolvedUnknownFatal,
			boolean deferCorrelatedValueSubqueryQualifiedUnknowns,
			String updateTargetTableRef,
			boolean updateHasFromClause,
			boolean retainRelationalModifierHintsForContinuedFrom) {
		return convertSymbolTableToTableDictionary(
				emitFinalUnresolvedUnknownFatal,
				deferCorrelatedValueSubqueryQualifiedUnknowns,
				updateTargetTableRef,
				updateHasFromClause,
				retainRelationalModifierHintsForContinuedFrom,
				null);
	}

	public HashMap<String, Object> convertSymbolTableToTableDictionary(
			boolean emitFinalUnresolvedUnknownFatal,
			boolean deferCorrelatedValueSubqueryQualifiedUnknowns,
			String updateTargetTableRef,
			boolean updateHasFromClause,
			boolean retainRelationalModifierHintsForContinuedFrom,
			String currentQueryScopeKey) {
	
		clearClauseColumnSiteTokens();

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
		RelationalModifierDerivationScopeState relationalModifierDerivationScope =
				detachRelationalModifierDerivationFromScope(walker.symbolTable);
		activeConvertEgressRelationalModifierContext = RelationalModifierConvertEgressContext.from(
				relationalModifierDerivationScope);
		HashMap<String, Object> localDerivedColumns;
		if (!relationalModifierDerivationScope.derivedColumnsByBucket.isEmpty()) {
			localDerivedColumns = new HashMap<String, Object>(
					relationalModifierDerivationScope.derivedColumnsByBucket);
		} else {
			localDerivedColumns = new HashMap<String, Object>();
		}
		HashMap<String, Object> localRelationalModifierSourceColumns =
				new HashMap<String, Object>(relationalModifierDerivationScope.sourceColumnsByBucket);
		activeConvertEgressDerivedColumns = localDerivedColumns;
		activeConvertEgressRelationalModifierSourceColumns = localRelationalModifierSourceColumns;
		activeConvertEgressPivotDerivedSourceBindingsByBucket =
				relationalModifierDerivationScope.pivotDerivedSourceBindingsByBucket.isEmpty()
						? null
						: new HashMap<String, Object>(
								relationalModifierDerivationScope.pivotDerivedSourceBindingsByBucket);
		String deleteTargetTableRef = (String) walker.symbolTable.remove(TEMP_DELETE_TARGET_TABLE_REF_KEY);
		String deleteTargetAlias = (String) walker.symbolTable.remove(TEMP_DELETE_TARGET_ALIAS_KEY);

		// Leave these null if they don't exist
		HashSet<String> localScalarSubqueryAliases = (HashSet<String>) walker.symbolTable.remove(MUMBLE_SCALAR_SUBQUERY_ALIASES_KEY);
		activeConvertEgressScalarSubqueryAliases = localScalarSubqueryAliases;
		activeConvertEgressSelectListOutputAliasSourceOrder =
				(ArrayList<String>) walker.symbolTable.remove(TEMP_SELECT_LIST_OUTPUT_ALIAS_SOURCE_ORDER_KEY);
        Object  filtersList = walker.symbolTable.remove(MUMBLE_FILTERS_KEY);
		Object groupedByList = walker.symbolTable.remove(MUMBLE_GROUPED_BY_KEY);
		Object orderedByList = walker.symbolTable.remove(MUMBLE_ORDERED_BY_KEY);
		Object windowPartitionByList = walker.symbolTable.remove(MUMBLE_WINDOW_PARTITION_BY_KEY);
		Object windowOrderedByList = walker.symbolTable.remove(MUMBLE_WINDOW_ORDERED_BY_KEY);
		Object withinGroupOrderedByList = walker.symbolTable.remove(MUMBLE_WITHIN_GROUP_ORDERED_BY_KEY);
		HashMap<String, Object> archivedScopeColumnReferenceContainers = new HashMap<String, Object>();
		if (filtersList != null) {
			archivedScopeColumnReferenceContainers.put(MUMBLE_FILTERS_KEY, filtersList);
		}
		if (groupedByList != null) {
			archivedScopeColumnReferenceContainers.put(MUMBLE_GROUPED_BY_KEY, groupedByList);
		}
		if (orderedByList != null) {
			archivedScopeColumnReferenceContainers.put(MUMBLE_ORDERED_BY_KEY, orderedByList);
		}
		if (windowPartitionByList != null) {
			archivedScopeColumnReferenceContainers.put(MUMBLE_WINDOW_PARTITION_BY_KEY, windowPartitionByList);
		}
		if (windowOrderedByList != null) {
			archivedScopeColumnReferenceContainers.put(MUMBLE_WINDOW_ORDERED_BY_KEY, windowOrderedByList);
		}
		if (withinGroupOrderedByList != null) {
			archivedScopeColumnReferenceContainers.put(
					MUMBLE_WITHIN_GROUP_ORDERED_BY_KEY, withinGroupOrderedByList);
		}
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

		// Physical FROM refs for convert egress (M2: operand drain runs after interface loop).
		HashMap<String, Object> localFromTableCollection =
				buildLocalPhysicalFromTableCollection(localTableCollection);
		materializePivotOperandStructuredBucketsAtConvertEgress(
				activeConvertEgressRelationalModifierContext,
				localTableCollection,
				localFromTableCollection,
				localTableAliasMap);

		HashMap<String, Object> currentTableDictionary = walker.getCurrentTableDictionary();
		propagateUnqualifiedSelectStarToScopeTables(
				localInterface,
				localCurrentQueryDictionary,
				localTableCollection);
		activeConvertEgressScopeBundle = buildConvertEgressScopeBundle(
				localTableAliasMap,
				localCurrentQueryDictionary);
		activeConvertEgressCurrentQueryScopeKey = normalizeQuerySourceReference(currentQueryScopeKey);
		try {
		HashMap<String, Object> visibleQuerySourceCollection =
				activeConvertEgressScopeBundle.visibleQuerySourceRefs;
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
					updateTargetTableRef,
					localDerivedColumns,
					activeConvertEgressRelationalModifierContext);
			walker.symbolTable.remove(TEMP_UPDATE_ASSIGNMENT_RHS_TOKENS_KEY);
		}
		HashMap<String, Object> effectiveAliasMap = buildEffectiveVisibleAliasMap(localTableAliasMap);
		HashMap<String, Object> effectiveTableCollection = buildEffectiveVisibleTableCollection(localTableCollection);

		// Bare-value egress site 1: batch prune before interface / clause column recognition.
		// CONTRACT (class javadoc above): no unqualified bare-value re-inserts into
		// localUnresolvedColumnMap until site 5 pass-up.
		pruneBareValueExpressionsFromUnresolvedMap(localUnresolvedColumnMap);

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
					activeConvertEgressRelationalModifierContext);
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
							ConvertEgressResolutionContext substitutionCtx =
									new ConvertEgressResolutionContext(
											localDerivedColumns,
											localRelationalModifierSourceColumns,
											activeConvertEgressRelationalModifierContext,
											localFromTableCollection,
											localFromTableCollection,
											visibleQuerySourceCollection,
											localTableAliasMap,
											effectiveAliasMap,
											effectiveTableCollection,
											deleteTargetTableRef,
											null,
											true,
											true,
											deferCorrelatedValueSubqueryQualifiedUnknowns,
											deferCorrelatedValueSubqueryQualifiedUnknowns,
											true,
											null);
							ConvertEgressColumnResolutionResult substitutionEgressResult =
									classifyColumnRefAtConvertEgress(
											columnName,
											tableRef,
											substitutionCtx);
							if (substitutionEgressResult.isDerivedColumn()) {
								continue;
							}
							if (substitutionEgressResult.qualified() != null
									&& (substitutionEgressResult.qualified().status
											== QualifiedScopeResolutionStatus.RESOLVED_QUERY_SOURCE
											|| substitutionEgressResult.qualified().status
													== QualifiedScopeResolutionStatus.RESOLVED_PHYSICAL_SOURCE)) {
								Object qualifiedTokens = consumeQualifiedUnknownEntry(
										localUnresolvedColumnMap,
										tableRef,
										columnName);
								QualifiedScopeResolutionResult substitutionResolutionResult =
										substitutionEgressResult.qualified();
								if (substitutionResolutionResult != null
										&& substitutionResolutionResult.status
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
						ConvertEgressResolutionContext interfaceQualifiedCtx =
								new ConvertEgressResolutionContext(
										localDerivedColumns,
										localRelationalModifierSourceColumns,
										activeConvertEgressRelationalModifierContext,
										localFromTableCollection,
										localFromTableCollection,
										visibleQuerySourceCollection,
										localTableAliasMap,
										effectiveAliasMap,
										effectiveTableCollection,
										deleteTargetTableRef,
										null,
										false,
										true,
										!emitFinalUnresolvedUnknownFatal,
										deferCorrelatedValueSubqueryQualifiedUnknowns,
										true,
										null);
						ConvertEgressColumnResolutionResult egressResult =
								classifyColumnRefAtConvertEgress(
										columnName,
										tableRef,
										interfaceQualifiedCtx);
						if (egressResult.hasExpandedDerivedSourceLineage()) {
							materializeUnpivotValueOperandFromInterfaceIfNeeded(
									activeConvertEgressRelationalModifierContext,
									outputCol,
									columnName,
									refObj,
									refIndex,
									localCurrentQueryDictionary,
									localDerivedColumns,
									localUnresolvedColumnMap);
							continue;
						}
						if (egressResult.isDerivedColumn()) {
							materializeUnpivotValueOperandFromInterfaceIfNeeded(
									activeConvertEgressRelationalModifierContext,
									outputCol,
									columnName,
									refObj,
									refIndex,
									localCurrentQueryDictionary,
									localDerivedColumns,
									localUnresolvedColumnMap);
							continue;
						}
						if (egressResult.isPivotOperandColumn()) {
							applyConvertEgressPivotOperandMaterialization(
									egressResult,
									columnName,
									tableRef,
									localUnresolvedColumnMap,
									localTableCollection,
									activeConvertEgressRelationalModifierContext,
									localTableAliasMap);
							String materializeTableRef = egressResult.pivotOperandMaterializeTableRef();
							if (materializeTableRef != null && !materializeTableRef.isBlank()) {
								refs.set(refIndex, cloneReferenceWithResolvedTableRef(
										refObj,
										materializeTableRef));
								materializeInterfacePivotOperandDependencyLineage(
										materializeTableRef,
										columnName,
										refObj,
										refIndex,
										outputCol,
										localCurrentQueryDictionary,
										localUnresolvedColumnMap,
										localFromTableCollection,
										localTableAliasMap,
										visibleQuerySourceCollection);
							}
							continue;
						}
						if (egressResult.isUnpivotInSourceColumn()) {
							applyConvertEgressUnpivotInSourceMaterialization(
									egressResult,
									columnName,
									tableRef,
									localUnresolvedColumnMap,
									localTableCollection,
									activeConvertEgressRelationalModifierContext,
									localTableAliasMap);
							String materializeTableRef = egressResult.pivotOperandMaterializeTableRef();
							if (materializeTableRef != null && !materializeTableRef.isBlank()) {
								refs.set(refIndex, cloneReferenceWithResolvedTableRef(
										refObj,
										materializeTableRef));
								materializeInterfaceUnpivotInSourceDependencyLineage(
										materializeTableRef,
										columnName,
										refObj,
										refIndex,
										outputCol,
										localCurrentQueryDictionary,
										localUnresolvedColumnMap,
										localFromTableCollection,
										localTableAliasMap,
										visibleQuerySourceCollection);
							}
							continue;
						}

						QualifiedScopeResolutionResult resolutionResult = egressResult.qualified();
						if (resolutionResult == null) {
							continue;
						}

						switch (resolutionResult.status) {
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
								String physicalRef = normalizeTableRef(
										(resolutionResult.resolvedPhysicalTableRef != null
												&& !resolutionResult.resolvedPhysicalTableRef.isBlank())
												? resolutionResult.resolvedPhysicalTableRef
												: walker.resolveAliasToTableName(tableRef, effectiveAliasMap));
								if (physicalRef == null || physicalRef.isBlank()) {
									physicalRef = normalizeTableRef(tableRef);
								}
								Object qualifiedTokens = consumeQualifiedUnknownEntry(
										localUnresolvedColumnMap,
										tableRef,
										columnName);
								Object tokenPayload = coalesceMaterializationRefTokens(
										qualifiedTokens,
										refObj);
								if (tokenPayload != null && physicalRef != null && !physicalRef.isBlank()) {
									mergeSourceLineageIntoPhysicalTableDictionary(
											localFromTableCollection,
											physicalRef,
											columnName,
											tokenPayload);
								}
							}
							case RESOLVED_DERIVED_COLUMN, RESOLVED_UNPIVOT_FOR, RESOLVED_UNPIVOT_VALUE,
									RESOLVED_PIVOT_OPERAND, RESOLVED_UNPIVOT_IN_SOURCE -> {
								// Handled above via egressResult early materialization (M5).
								break;
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
						// Bare-value egress site 2: ref-site skip before derived / scope resolution.
						if (skipBareValueExpressionAtConvertEgressRefSite(
								localUnresolvedColumnMap,
								null,
								columnName,
								refObj)) {
							continue;
						}

						if (tryStampGroundedOutputAliasInterfaceDependencyToQueryScope(
								refs,
								refIndex,
								refObj,
								columnName,
								outputCol,
								localInterface,
								localTableCollection,
								localUnresolvedColumnMap,
								localCurrentQueryDictionary,
								effectiveAliasMap,
								visibleQuerySourceCollection)) {
							continue;
						}

						// Pivot registry derived outputs (jan_sales_SUM / q1_total) — skip physical
						// interface bind; shared egress treats them as RESOLVED_DERIVED_COLUMN.
						if (outputCol.equalsIgnoreCase(columnName)
								&& structuredContextDefinesPivotDerivedOutputColumn(
										activeConvertEgressRelationalModifierContext,
										columnName)) {
							continue;
						}

						Integer[] refLocation = resolveUnqualifiedReferenceLocation(
								columnName,
								refObj,
								localUnresolvedColumnMap,
								localCurrentQueryDictionary,
								outputCol);

						ConvertEgressResolutionContext interfaceUnqualifiedCtx =
								new ConvertEgressResolutionContext(
										localDerivedColumns,
										localRelationalModifierSourceColumns,
										activeConvertEgressRelationalModifierContext,
										localFromTableCollection,
										localFromTableCollection,
										visibleQuerySourceCollection,
										localTableAliasMap,
										null,
										null,
										deleteTargetTableRef,
										null,
										false,
										true,
										!emitFinalUnresolvedUnknownFatal,
										false,
										false,
										outputCol);
						ConvertEgressColumnResolutionResult egressResult =
								classifyColumnRefAtConvertEgress(
										columnName,
										null,
										interfaceUnqualifiedCtx);
						if (egressResult.hasExpandedDerivedSourceLineage()) {
							materializeUnpivotValueOperandFromInterfaceIfNeeded(
									activeConvertEgressRelationalModifierContext,
									outputCol,
									columnName,
									refObj,
									refIndex,
									localCurrentQueryDictionary,
									localDerivedColumns,
									localUnresolvedColumnMap);
							continue;
						}
						if (egressResult.isDerivedColumn()) {
							materializeUnpivotValueOperandFromInterfaceIfNeeded(
									activeConvertEgressRelationalModifierContext,
									outputCol,
									columnName,
									refObj,
									refIndex,
									localCurrentQueryDictionary,
									localDerivedColumns,
									localUnresolvedColumnMap);
							continue;
						}
						if (egressResult.isPivotOperandColumn()) {
							applyConvertEgressPivotOperandMaterialization(
									egressResult,
									columnName,
									null,
									localUnresolvedColumnMap,
									localTableCollection,
									activeConvertEgressRelationalModifierContext,
									localTableAliasMap);
							String materializeTableRef = egressResult.pivotOperandMaterializeTableRef();
							if (materializeTableRef != null && !materializeTableRef.isBlank()) {
								refs.set(refIndex, cloneReferenceWithResolvedTableRef(
										refObj,
										materializeTableRef));
								materializeInterfacePivotOperandDependencyLineage(
										materializeTableRef,
										columnName,
										refObj,
										refIndex,
										outputCol,
										localCurrentQueryDictionary,
										localUnresolvedColumnMap,
										localFromTableCollection,
										localTableAliasMap,
										visibleQuerySourceCollection);
							}
							continue;
						}
						if (egressResult.isUnpivotInSourceColumn()) {
							applyConvertEgressUnpivotInSourceMaterialization(
									egressResult,
									columnName,
									null,
									localUnresolvedColumnMap,
									localTableCollection,
									activeConvertEgressRelationalModifierContext,
									localTableAliasMap);
							String materializeTableRef = egressResult.pivotOperandMaterializeTableRef();
							if (materializeTableRef != null && !materializeTableRef.isBlank()) {
								refs.set(refIndex, cloneReferenceWithResolvedTableRef(
										refObj,
										materializeTableRef));
								materializeInterfaceUnpivotInSourceDependencyLineage(
										materializeTableRef,
										columnName,
										refObj,
										refIndex,
										outputCol,
										localCurrentQueryDictionary,
										localUnresolvedColumnMap,
										localFromTableCollection,
										localTableAliasMap,
										visibleQuerySourceCollection);
							}
							continue;
						}

						UnqualifiedScopeResolutionResult resolutionResult = egressResult.unqualified();
						if (resolutionResult == null) {
							continue;
						}
						if (resolutionResult.status == UnqualifiedScopeResolutionStatus.RESOLVED) {
							if (tryStampGroundedOutputAliasInterfaceDependencyToQueryScope(
									refs,
									refIndex,
									refObj,
									columnName,
									outputCol,
									localInterface,
									localTableCollection,
									localUnresolvedColumnMap,
									localCurrentQueryDictionary,
									effectiveAliasMap,
									visibleQuerySourceCollection)) {
								continue;
							}
							refs.set(refIndex, cloneReferenceWithResolvedTableRef(
									refObj,
									resolutionResult.resolvedSourceRef));
							if (!isRelationalModifierDerivedColumnReference(
									localDerivedColumns,
									activeConvertEgressRelationalModifierContext,
									null,
									columnName)) {
								materializeInterfaceOutputSourceLineage(
										resolutionResult.resolvedSourceRef,
										columnName,
										refObj,
										localUnresolvedColumnMap,
										localFromTableCollection,
										localTableAliasMap,
										visibleQuerySourceCollection);
							}
						} else {
							if (resolutionResult.status == UnqualifiedScopeResolutionStatus.AMBIGUOUS_DERIVED_COLUMN
									|| resolutionResult.status
											== UnqualifiedScopeResolutionStatus.AMBIGUOUS_DERIVED_AND_REGULAR_COLUMN) {
								hasSpecificResolutionFatalForOutputColumn = true;
								continue;
							}
							if (resolutionResult.status == UnqualifiedScopeResolutionStatus.AMBIGUOUS) {
								if (isAmbiguousUnqualifiedRelationalModifierSourceOperandColumn(
										columnName,
										null,
										localRelationalModifierSourceColumns)) {
									hasSpecificResolutionFatalForOutputColumn = true;
									continue;
								}
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
		}

		// Phase 16.2 (M2): drain operand unresolved after interface lineage pass (buckets pre-interface).
		drainPivotOperandColumnsFromUnresolvedMap(
				activeConvertEgressRelationalModifierContext,
				localUnresolvedColumnMap,
				localTableCollection,
				localFromTableCollection,
				localTableAliasMap);

		materializeUnpivotValueOperandSelectExpressionSitesBeforeDerivationRewrite(
				localInterface,
				activeConvertEgressRelationalModifierContext,
				localDerivedColumns,
				localUnresolvedColumnMap,
				localCurrentQueryDictionary);

		applyUnpivotDerivationsToQueryScope(
				localInterface,
				activeConvertEgressRelationalModifierContext,
				localDerivedColumns);

		materializeSelectedUnpivotInColumnsIntoSourceDictionary(
				localCurrentQueryDictionary,
				localTableCollection,
				localTableAliasMap,
				activeConvertEgressRelationalModifierContext);

		applyPivotValueInterfaceDerivations(
				localInterface,
				activeConvertEgressRelationalModifierContext,
				localDerivedColumns);

		stashStructuredDerivedColumnCandidatesForConvertEgress(localDerivedColumns);
		HashMap<String, Object> localPhysicalFromTableCollection =
				buildLocalPhysicalFromTableCollection(localFromTableCollection);

		if (isDerivedVersusRegularColumnNamespaceDiagnosticScope()) {
			diagnoseDerivedVersusRegularUnqualifiedColumnRefSites(
					localInterface,
					localCurrentQueryDictionary,
					localUnresolvedColumnMap,
					localDerivedColumns,
					archivedScopeColumnReferenceContainers,
					walker.symbolTable.get(MUMBLE_ASSIGNMENTS_KEY),
					localPhysicalFromTableCollection,
					localFromTableCollection,
					visibleQuerySourceCollection,
					localTableAliasMap);
		}

		diagnoseAmbiguousUnqualifiedRelationalModifierDerivedColumnRefSites(
				localInterface,
				localCurrentQueryDictionary,
				localUnresolvedColumnMap,
				localDerivedColumns,
				archivedScopeColumnReferenceContainers,
				walker.symbolTable.get(MUMBLE_ASSIGNMENTS_KEY),
				localPhysicalFromTableCollection,
				localFromTableCollection,
				visibleQuerySourceCollection,
				localTableAliasMap);

		diagnoseAmbiguousUnqualifiedRelationalModifierSourceOperandRefSites(
				localInterface,
				localCurrentQueryDictionary,
				localUnresolvedColumnMap,
				localRelationalModifierSourceColumns,
				archivedScopeColumnReferenceContainers,
				walker.symbolTable.get(MUMBLE_ASSIGNMENTS_KEY));

		diagnoseRelationalModifierDerivedReferenceWithSourcePrimaryAlias(
				localInterface,
				localCurrentQueryDictionary,
				archivedScopeColumnReferenceContainers,
				walker.symbolTable.get(MUMBLE_ASSIGNMENTS_KEY),
				localUnresolvedColumnMap,
				localDerivedColumns,
				localRelationalModifierSourceColumns,
				localTableAliasMap);

		// Resolve ingress-captured unqualified entries
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
					deleteTargetTableRef,
					localDerivedColumns,
					activeConvertEgressRelationalModifierContext);
		}

		probeArchivedScopeClauseColumns(
				archivedScopeColumnReferenceContainers,
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
				activeConvertEgressRelationalModifierContext,
				deleteTargetTableRef,
				deferCorrelatedValueSubqueryQualifiedUnknowns);

		mergeDeferredClauseHarvestSiteTokensIntoQueryDictionary(
				localInterface,
				localCurrentQueryDictionary,
				archivedScopeColumnReferenceContainers,
				walker.symbolTable.get(MUMBLE_ASSIGNMENTS_KEY));

		runConvertEgressRelationalModifierDerivedLineagePhaseB(
				localInterface,
				archivedScopeColumnReferenceContainers,
				walker.symbolTable.get(MUMBLE_ASSIGNMENTS_KEY),
				localDerivedColumns,
				localRelationalModifierSourceColumns,
				localTableAliasMap,
				localUnresolvedColumnMap,
				localFromTableCollection,
				localTableCollection,
				visibleQuerySourceCollection,
				effectiveAliasMap,
				effectiveTableCollection,
				deleteTargetTableRef,
				activeConvertEgressRelationalModifierContext);

		mergeDeferredWindowClauseHarvestSiteTokensIntoQueryDictionary(
				localInterface,
				localCurrentQueryDictionary,
				archivedScopeColumnReferenceContainers);

		stripEphemeralLocationsFromConvertEgressColumnReferences(
				localInterface,
				archivedScopeColumnReferenceContainers,
				walker.symbolTable.get(MUMBLE_ASSIGNMENTS_KEY));

		consolidateConvertEgressColumnReferenceLists(
				localInterface,
				archivedScopeColumnReferenceContainers,
				walker.symbolTable.get(MUMBLE_ASSIGNMENTS_KEY));

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

		// Late unqualified-reference resolution can materialize new entries (for example,
		// DELETE target columns from RETURNING). Re-merge so global and local dictionaries stay aligned.
		canonicalizeLocalTableCollection(
				localTableCollection,
				localTableAliasMap);

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

		materializeUnqualifiedLineageForSingleSourceScopeAtConvertExit(
				localUnresolvedColumnMap,
				localTableCollection,
				localDerivedColumns,
				activeConvertEgressRelationalModifierContext);

		canonicalizeLocalTableCollection(
				localTableCollection,
				localTableAliasMap);
		canonicalizeLocalTableCollection(
				currentTableDictionary,
				localTableAliasMap);

		mergeRelationalModifierDerivedColumnDefinitionTokensIntoQueryDictionary(
				localInterface,
				localCurrentQueryDictionary,
				localDerivedColumns);

		patchInterfaceTableRefsForSinglePhysicalTableScope(localInterface, localTableCollection);

		 walker.validateQueryInterface(localInterface, localCurrentQueryDictionary, effectiveAliasMap, effectiveTableCollection);

		enrichTableAliasMapWithRelationalModifierBucketAliases(
				localTableAliasMap,
				localRelationalModifierSourceColumns);

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
		relationalModifierDerivationScope.derivedColumnsByBucket.clear();
		if (!localDerivedColumns.isEmpty()) {
			relationalModifierDerivationScope.derivedColumnsByBucket.putAll(localDerivedColumns);
		}
		relationalModifierDerivationScope.sourceColumnsByBucket.clear();
		if (!localRelationalModifierSourceColumns.isEmpty()) {
			relationalModifierDerivationScope.sourceColumnsByBucket.putAll(localRelationalModifierSourceColumns);
		}
		publishRelationalModifierDerivationToScope(
				walker.symbolTable,
				relationalModifierDerivationScope,
				retainRelationalModifierHintsForContinuedFrom);
		// Call a method here that will merge the local Table Dictionary into the walker's TableDictionary Map
		walker.mergeTableDictionaryIntoWalkerTableDictionary(currentTableDictionary);
		
		if (localScalarSubqueryAliases != null)
			walker.symbolTable.put(MUMBLE_SCALAR_SUBQUERY_ALIASES_KEY, localScalarSubqueryAliases);
		if (filtersList != null) {
			walker.symbolTable.put(MUMBLE_FILTERS_KEY, filtersList);
		}
		if (groupedByList != null) {
			walker.symbolTable.put(MUMBLE_GROUPED_BY_KEY, groupedByList);
		}
		if (orderedByList != null) {
			walker.symbolTable.put(MUMBLE_ORDERED_BY_KEY, orderedByList);
		}
		if (windowPartitionByList != null) {
			walker.symbolTable.put(MUMBLE_WINDOW_PARTITION_BY_KEY, windowPartitionByList);
		}
		if (windowOrderedByList != null) {
			walker.symbolTable.put(MUMBLE_WINDOW_ORDERED_BY_KEY, windowOrderedByList);
		}
		if (withinGroupOrderedByList != null) {
			walker.symbolTable.put(MUMBLE_WITHIN_GROUP_ORDERED_BY_KEY, withinGroupOrderedByList);
		}
		if (preservedInsertSourceSelectSequence != null) {
			walker.symbolTable.put(TEMP_INSERT_SOURCE_SELECT_SEQUENCE_KEY, preservedInsertSourceSelectSequence);
		}

		if (localTableCollection != null && !localTableCollection.isEmpty()) {
			walker.mergeTableDictionaryIntoWalkerTableDictionary(localTableCollection);
		}

		HashMap<String, Object> globalTableDictionary = walker.getWalkerTableDictionary();
		if (globalTableDictionary != null && !globalTableDictionary.isEmpty()) {
			canonicalizeLocalTableCollection(
					globalTableDictionary,
					localTableAliasMap);
		}

		return walker.symbolTable;
		} finally {
			activeConvertEgressScopeBundle = null;
			activeConvertEgressStructuredDerivedColumnCandidates = null;
			activeConvertEgressDerivedColumns = null;
			activeConvertEgressCurrentQueryScopeKey = null;
			activeConvertEgressRelationalModifierContext = null;
			activeConvertEgressRelationalModifierSourceColumns = null;
			activeConvertEgressPivotDerivedSourceBindingsByBucket = null;
			activeConvertEgressScalarSubqueryAliases = null;
			activeConvertEgressSelectListOutputAliasSourceOrder = null;
		}
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
		convertSymbolTableToTableDictionary(
				false,
				false,
				null,
				false,
				true,
				MUMBLE_QUERY_KEY + walker.queryCount);
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

	/**
	 * Phase 15.6: one-shot scope visibility for convert egress. Closest {@code def_*} payload wins
	 * (current symbol table, then ancestors nearest-to-farthest).
	 */
	@SuppressWarnings("unchecked")
	private ConvertEgressScopeBundle buildConvertEgressScopeBundle(
			HashMap<String, Object> localTableAliasMap,
			HashMap<String, Object> localCurrentQueryDictionary) {
		HashMap<String, Object> visibleDefinitionPayloads = new HashMap<String, Object>();
		if (!setOpDefinitionPayloadCacheStack.isEmpty()) {
			// O(1): use the incrementally-maintained cache instead of the O(n) ancestor scan.
			visibleDefinitionPayloads.putAll(setOpDefinitionPayloadCacheStack.peek());
		} else {
			List<Map<String, Object>> ancestors = getAncestorSymbolTables();
			for (int ancestorIndex = ancestors.size() - 1; ancestorIndex >= 0; ancestorIndex--) {
				mergeDefinitionPayloadsFromSymbolTable(ancestors.get(ancestorIndex), visibleDefinitionPayloads);
			}
		}
		mergeDefinitionPayloadsFromSymbolTable(walker.symbolTable, visibleDefinitionPayloads);

		HashMap<String, Object> localQueryDictionary = localCurrentQueryDictionary != null
				? new HashMap<String, Object>(localCurrentQueryDictionary)
				: new HashMap<String, Object>();

		LinkedHashSet<String> liveQueryRefs = new LinkedHashSet<String>();
		if (localTableAliasMap != null && !localTableAliasMap.isEmpty()) {
			for (Object mappedSourceObj : localTableAliasMap.values()) {
				if (mappedSourceObj instanceof String mappedSource
						&& isQuerySourceReference(mappedSource)) {
					liveQueryRefs.add(mappedSource);
				}
			}
		}
		// O(1): if the live-query-ref cache is active, use it directly instead of scanning
		// all visibleDefinitionPayloads entries (which was O(n) per branch = O(n²) total).
		if (!setOpLiveQueryRefCacheStack.isEmpty()) {
			liveQueryRefs.addAll(setOpLiveQueryRefCacheStack.peek());
			// Check only the entries added by the current branch's symbolTable (already merged above).
			if (walker.symbolTable != null) {
				for (Map.Entry<String, Object> entry : walker.symbolTable.entrySet()) {
					if (entry.getKey() != null && isDefinitionScopeKey(entry.getKey())) {
						String liveRef = toLiveScopeKey(entry.getKey());
						if (liveRef != null && isQuerySourceReference(liveRef)) {
							liveQueryRefs.add(liveRef);
						}
					}
				}
			}
		} else {
			for (String definitionKey : visibleDefinitionPayloads.keySet()) {
				String liveRef = toLiveScopeKey(definitionKey);
				if (liveRef != null && isQuerySourceReference(liveRef)) {
					liveQueryRefs.add(liveRef);
				}
			}
		}

		HashMap<String, Object> globalQueryDictionaryRefs = new HashMap<String, Object>();
		for (String liveQueryRef : liveQueryRefs) {
			// Phase 19.5: live index is owned by publishQueryDictionary / phase-2 enrichers;
			// the bundle holds the same map refs (not copies) for convert-egress reads.
			Object queryDictionaryObj = walker.queryColumnDictionaryMap.get(liveQueryRef);
			if (queryDictionaryObj != null) {
				globalQueryDictionaryRefs.put(liveQueryRef, queryDictionaryObj);
			}
		}

		HashMap<String, Object> visibleQuerySourceRefs = new HashMap<String, Object>();
		if (localTableAliasMap != null && !localTableAliasMap.isEmpty()) {
			for (Object mappedSourceObj : localTableAliasMap.values()) {
				if (!(mappedSourceObj instanceof String mappedSource) || !isQuerySourceReference(mappedSource)) {
					continue;
				}

				String defQueryRef = toDefinitionScopeKey(mappedSource);
				Object queryDefinitionObj = visibleDefinitionPayloads.get(defQueryRef);
				if (queryDefinitionObj instanceof Map<?, ?>) {
					visibleQuerySourceRefs.put(mappedSource, queryDefinitionObj);
					continue;
				}

				Object queryDictionaryObj = globalQueryDictionaryRefs.get(mappedSource);
				if (queryDictionaryObj instanceof Map<?, ?>) {
					visibleQuerySourceRefs.put(mappedSource, queryDictionaryObj);
				}
			}
		}

		return new ConvertEgressScopeBundle(
				visibleDefinitionPayloads,
				visibleQuerySourceRefs,
				localQueryDictionary,
				globalQueryDictionaryRefs);
	}

	private void mergeDefinitionPayloadsFromSymbolTable(
			Map<String, Object> symbolTable,
			HashMap<String, Object> visibleDefinitionPayloads) {
		if (symbolTable == null || symbolTable.isEmpty()) {
			return;
		}
		for (Map.Entry<String, Object> entry : symbolTable.entrySet()) {
			String key = entry.getKey();
			if (key != null && isDefinitionScopeKey(key)) {
				visibleDefinitionPayloads.put(key, entry.getValue());
			}
		}
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

		// Bare-value egress site 6: statement-boundary safety net.
		pruneBareValueExpressionsFromUnresolvedMap(unresolvedMap);

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

		// Bare-value egress site 6: prune before UNRESOLVED_UNQUALIFIED_COLUMNS emit.
		pruneBareValueExpressionsFromUnresolvedMap(unqualifiedUnresolvedMap);
		if (unqualifiedUnresolvedMap.isEmpty()) {
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

		// Already sanitized above (empty-dict early return must not rewrite embedded).
		publishQueryDictionary(new QueryDictionaryPublishContext(
				toLiveScopeKey(insertDefinitionScopeKey),
				queryDictionary,
				insertScopeMap,
				false,
				true));
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
		// Global-only export from update_dictionary (no embedded query_dictionary rewrite).
		publishQueryDictionary(new QueryDictionaryPublishContext(
				toLiveScopeKey(updateDefinitionScopeKey),
				queryDictionary,
				null,
				true,
				true));
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

	/**
	 * Opens a nested UPDATE scope for {@code ON CONFLICT DO UPDATE}: seeds the insert target
	 * table so assignment/WHERE resolution matches a no-FROM UPDATE against that table.
	 */
	@SuppressWarnings("unchecked")
	public void beginInsertOnConflictUpdateScope() {
		String insertTargetTableRef = (String) walker.symbolTable.get(TEMP_INSERT_TARGET_TABLE_REF_KEY);
		HashMap<String, Object> seededTableDictionary = copyInsertTargetTableDictionary(insertTargetTableRef);
		walker.pushSymbolTable();
		if (insertTargetTableRef != null && !insertTargetTableRef.isBlank()) {
			walker.symbolTable.put(TEMP_INSERT_TARGET_TABLE_REF_KEY, insertTargetTableRef);
			initializeUpdateTargetTableSubtree(insertTargetTableRef);
		}
		if (seededTableDictionary != null && !seededTableDictionary.isEmpty()) {
			walker.symbolTable.put(MUMBLE_TABLE_DICTIONARY_KEY, seededTableDictionary);
		}
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> copyInsertTargetTableDictionary(String insertTargetTableRef) {
		if (insertTargetTableRef == null || insertTargetTableRef.isBlank()) {
			return null;
		}
		Object parentTableDictionaryObj = walker.symbolTable.get(MUMBLE_TABLE_DICTIONARY_KEY);
		if (!(parentTableDictionaryObj instanceof HashMap<?, ?> parentTableDictionaryMapObj)) {
			return null;
		}
		Object targetEntryObj = ((HashMap<String, Object>) parentTableDictionaryMapObj).get(insertTargetTableRef);
		if (!(targetEntryObj instanceof Map<?, ?>)) {
			return null;
		}
		HashMap<String, Object> seededTableDictionary = new HashMap<String, Object>();
		seededTableDictionary.put(insertTargetTableRef, targetEntryObj);
		return seededTableDictionary;
	}

	public Map<String, Object> buildInsertOnConflictUpdateNode(
			String insertTargetTableRef,
			Map<String, Object> actionNode) {
		Map<String, Object> updateNode = new LinkedHashMap<String, Object>();
		if (insertTargetTableRef != null && !insertTargetTableRef.isBlank()) {
			Map<String, Object> tableNode = new LinkedHashMap<String, Object>();
			tableNode.put("table", insertTargetTableRef);
			tableNode.put("alias", null);
			updateNode.put(MUMBLE_TABLE_KEY, tableNode);
		}
		Object assignmentsObj = actionNode == null ? null : actionNode.get(MUMBLE_ASSIGNMENTS_KEY);
		if (assignmentsObj != null) {
			updateNode.put(MUMBLE_ASSIGNMENTS_KEY, assignmentsObj);
		}
		Object whereObj = actionNode == null ? null : actionNode.get(MUMBLE_WHERE_KEY);
		if (whereObj != null) {
			updateNode.put(MUMBLE_WHERE_KEY, whereObj);
		}
		return updateNode;
	}

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
		HashMap<String, Object> scopeDerivedColumns = getScopeDerivedColumnsFromSymbolTable();
		RelationalModifierConvertEgressContext scopeRelationalModifierContext =
				getScopeRelationalModifierContextFromSymbolTable();

		HashMap<String, Object> scopeSymbols = convertSymbolTableToTableDictionary(
				emitFinalUnresolvedUnknownFatal,
				deferCorrelatedValueSubqueryQualifiedUnknowns,
				null,
				false,
				false,
				scopeKey);

		HashMap<String, Object> localCurrentQueryDictionary =
				(HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_QUERY_DICTIONARY_KEY);
		if (localCurrentQueryDictionary == null) {
			localCurrentQueryDictionary = new HashMap<String, Object>();
		}

		// Phase 19.2: do not sanitize/merge here — {@link #publishQueryLikeScope} owns a single
		// publish via {@link #publishQueryDictionary} (sanitize + embed + global merge).
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
				emitUnqualifiedUnresolvedColumnsError(unqualifiedUnresolvedForLocal);
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

		mergeScopeTableDictionaryIntoGlobalWalkerDictionary(scopeSymbols);

		applyParentFramePendingSetOperatorToPublishingParticipant(scopeSymbols);
		publishQueryLikeScope(scopeKey, scopeSymbols);

		if (deferSubqueryUnresolvedDiagnosticsToStatementBoundary) {
			consumeLocallyResolvedUnqualifiedBeforeScopePassUp(
					unqualifiedUnresolvedForLocal,
					scopeSymbols,
					scopeDerivedColumns,
					scopeRelationalModifierContext);
			HashMap<String, Object> deferredUnresolvedForParent = new HashMap<String, Object>();
			deferredUnresolvedForParent.putAll(unqualifiedUnresolvedForLocal);
			deferredUnresolvedForParent.putAll(qualifiedUnresolvedForParent);
			mergeUnresolvedEntriesIntoCurrentScope(deferredUnresolvedForParent);
			return;
		}

		// For correlated subqueries (passUpQualifiedUnresolvedFromThisSubquery=true), pass up ALL unresolved
		// columns (both qualified and unqualified) to the parent query for resolution.
		if (passUpQualifiedUnresolvedFromThisSubquery) {
			consumeLocallyResolvedUnqualifiedBeforeScopePassUp(
					unqualifiedUnresolvedForLocal,
					scopeSymbols,
					scopeDerivedColumns,
					scopeRelationalModifierContext);
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
		boolean updateHasReturning = updateNode != null && updateNode.get(MUMBLE_RETURNING_KEY) != null;
		HashMap<String, Object> returningInterface = null;
		HashMap<String, Object> returningQueryDictionary = null;
		if (updateHasReturning) {
			returningInterface = copyHashMapValueIfPresent(walker.symbolTable, MUMBLE_INTERFACE_KEY);
			returningQueryDictionary = copyHashMapValueIfPresent(walker.symbolTable, MUMBLE_QUERY_DICTIONARY_KEY);
		}
		initializeUpdateTargetTableSubtree(updateTargetTableRef);
		convertSymbolTableToTableDictionary(false, false, updateTargetTableRef, updateHasFromClause);
		finalizeUpdateScopeUnresolvedColumnsAtExit(updateHasFromClause, updateNode);

		String updateScopeKey = MUMBLE_UPDATE_KEY + walker.queryCount;
		stripUnresolvedFromScopePayload(walker.symbolTable);
		publishQueryLikeScope(updateScopeKey, walker.symbolTable);
		if (updateHasReturning) {
			publishUpdateReturningScopeArtifacts(updateScopeKey, returningInterface, returningQueryDictionary);
		}
		publishUpdateScopeQueryDictionary(updateScopeKey);
	}

	@SuppressWarnings("unchecked")
	private void publishUpdateReturningScopeArtifacts(
			String updateScopeKey,
			HashMap<String, Object> returningInterface,
			HashMap<String, Object> returningQueryDictionary) {
		if (updateScopeKey == null || updateScopeKey.isBlank()) {
			return;
		}
		String updateDefinitionScopeKey = toDefinitionScopeKey(updateScopeKey);
		Object updateScopeObj = walker.symbolTable.get(updateDefinitionScopeKey);
		if (!(updateScopeObj instanceof HashMap<?, ?> updateScopeMapObj)) {
			return;
		}
		HashMap<String, Object> updateScopeMap = (HashMap<String, Object>) updateScopeMapObj;
		if (returningQueryDictionary != null) {
			publishQueryDictionary(new QueryDictionaryPublishContext(
					toLiveScopeKey(updateDefinitionScopeKey),
					returningQueryDictionary,
					updateScopeMap,
					true,
					true));
		}
		HashMap<String, Object> mergedInterface = buildUpdateScopeInterfaceFromAssignments(updateScopeMap);
		mergeMissingInterfaceEntries(mergedInterface, returningInterface);
		if (!mergedInterface.isEmpty()) {
			updateScopeMap.put(MUMBLE_INTERFACE_KEY, mergedInterface);
		}
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> buildUpdateScopeInterfaceFromAssignments(Map<String, Object> updateScopeMap) {
		HashMap<String, Object> interfaceMap = new HashMap<String, Object>();
		if (updateScopeMap == null) {
			return interfaceMap;
		}
		Object assignmentsObj = updateScopeMap.get(MUMBLE_ASSIGNMENTS_KEY);
		if (assignmentsObj instanceof Map<?, ?> assignmentsMap) {
			for (Map.Entry<String, Object> entry : ((Map<String, Object>) assignmentsMap).entrySet()) {
				String assignmentKey = entry.getKey();
				if (assignmentKey != null && !assignmentKey.isBlank()) {
					interfaceMap.put(assignmentKey, entry.getValue());
				}
			}
		}
		return interfaceMap;
	}

	@SuppressWarnings("unchecked")
	private void mergeMissingInterfaceEntries(
			HashMap<String, Object> targetInterface,
			HashMap<String, Object> returningInterface) {
		if (targetInterface == null || returningInterface == null || returningInterface.isEmpty()) {
			return;
		}
		for (Map.Entry<String, Object> entry : returningInterface.entrySet()) {
			String interfaceKey = entry.getKey();
			if (interfaceKey != null && !interfaceKey.isBlank()) {
				targetInterface.putIfAbsent(interfaceKey, entry.getValue());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static HashMap<String, Object> copyHashMapValueIfPresent(
			HashMap<String, Object> source,
			String key) {
		if (source == null || key == null) {
			return null;
		}
		Object value = source.get(key);
		if (!(value instanceof HashMap<?, ?> valueMap)) {
			return null;
		}
		return new LinkedHashMap<String, Object>((Map<String, Object>) valueMap);
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
		HashMap<String, Object> scopeSymbols = walker.symbolTable;
		if (publishReturningQueryDictionary) {
			HashMap<String, Object> localCurrentQueryDictionary =
					(HashMap<String, Object>) walker.symbolTable.remove(MUMBLE_QUERY_DICTIONARY_KEY);
			if (localCurrentQueryDictionary == null) {
				localCurrentQueryDictionary = new HashMap<String, Object>();
			}
			// Phase 19.3: single publish via publishQueryLikeScope → publishQueryDictionary
			// (same SELECT 19.2 pattern — no pre-publish global merge).
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
	public void finalizeInsertScopeSymbolTable(Map<String, Object> insertNode) {
		finalizeTopLevelUnresolvedColumnsAtInsertBoundary();

		boolean insertHasReturning = insertNode != null && insertNode.get(MUMBLE_RETURNING_KEY) != null;
		HashMap<String, Object> targetInterface = copyHashMapValueIfPresent(
				walker.symbolTable,
				TEMP_INSERT_TARGET_INTERFACE_KEY);
		walker.symbolTable.remove(TEMP_INSERT_TARGET_INTERFACE_KEY);
		walker.symbolTable.remove(TEMP_INSERT_TARGET_TABLE_REF_KEY);
		HashMap<String, Object> returningInterface = null;
		HashMap<String, Object> returningQueryDictionary = null;
		if (insertHasReturning) {
			returningInterface = copyHashMapValueIfPresent(walker.symbolTable, MUMBLE_INTERFACE_KEY);
			returningQueryDictionary = copyHashMapValueIfPresent(walker.symbolTable, MUMBLE_QUERY_DICTIONARY_KEY);
		} else if (targetInterface != null && !targetInterface.isEmpty()) {
			walker.symbolTable.put(MUMBLE_INTERFACE_KEY, targetInterface);
		}

		String insertScopeKey = MUMBLE_INSERT_KEY + walker.queryCount;
		publishQueryLikeScope(insertScopeKey, walker.symbolTable);
		mergeInsertScopeTableDictionaryIntoGlobal(insertScopeKey);
		if (insertHasReturning) {
			publishInsertReturningScopeArtifacts(
					insertScopeKey,
					targetInterface,
					returningInterface,
					returningQueryDictionary);
		}
		publishInsertScopeQueryDictionary(insertScopeKey);
	}

	@SuppressWarnings("unchecked")
	private void publishInsertReturningScopeArtifacts(
			String insertScopeKey,
			HashMap<String, Object> targetInterface,
			HashMap<String, Object> returningInterface,
			HashMap<String, Object> returningQueryDictionary) {
		if (insertScopeKey == null || insertScopeKey.isBlank()) {
			return;
		}
		String insertDefinitionScopeKey = toDefinitionScopeKey(insertScopeKey);
		Object insertScopeObj = walker.symbolTable.get(insertDefinitionScopeKey);
		if (!(insertScopeObj instanceof HashMap<?, ?> insertScopeMapObj)) {
			return;
		}
		HashMap<String, Object> insertScopeMap = (HashMap<String, Object>) insertScopeMapObj;
		if (returningQueryDictionary != null) {
			// Preserve prior contract: sanitize+merge RETURNING into global first, then fold into
			// any embedded dict left by publishQueryLikeScope (without a second global write).
			publishQueryDictionary(new QueryDictionaryPublishContext(
					toLiveScopeKey(insertDefinitionScopeKey),
					returningQueryDictionary,
					null,
					true,
					true));
			Object existingQueryDictionaryObj = insertScopeMap.get(MUMBLE_QUERY_DICTIONARY_KEY);
			if (existingQueryDictionaryObj instanceof HashMap<?, ?> existingQueryDictionaryMapObj) {
				HashMap<String, Object> mergedQueryDictionary =
						new HashMap<String, Object>((Map<String, Object>) existingQueryDictionaryMapObj);
				for (Map.Entry<String, Object> entry : returningQueryDictionary.entrySet()) {
					String columnName = entry.getKey();
					if (columnName == null) {
						continue;
					}
					Object existingRefs = mergedQueryDictionary.get(columnName);
					if (existingRefs == null) {
						mergedQueryDictionary.put(columnName, entry.getValue());
					} else {
						mergedQueryDictionary.put(columnName, mergeReferenceCollections(existingRefs, entry.getValue()));
					}
				}
				insertScopeMap.put(MUMBLE_QUERY_DICTIONARY_KEY, mergedQueryDictionary);
			} else {
				insertScopeMap.put(MUMBLE_QUERY_DICTIONARY_KEY, returningQueryDictionary);
			}
		}
		HashMap<String, Object> mergedInterface = buildInsertScopeInterfaceFromTargetStaging(targetInterface);
		mergeMissingInterfaceEntries(mergedInterface, returningInterface);
		if (!mergedInterface.isEmpty()) {
			insertScopeMap.put(MUMBLE_INTERFACE_KEY, mergedInterface);
		}
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> buildInsertScopeInterfaceFromTargetStaging(
			HashMap<String, Object> targetInterface) {
		HashMap<String, Object> interfaceMap = new HashMap<String, Object>();
		if (targetInterface == null || targetInterface.isEmpty()) {
			return interfaceMap;
		}
		for (Map.Entry<String, Object> entry : targetInterface.entrySet()) {
			String interfaceKey = entry.getKey();
			if (interfaceKey != null && !interfaceKey.isBlank()) {
				interfaceMap.put(interfaceKey, entry.getValue());
			}
		}
		return interfaceMap;
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

		// Composite set-op scopes stamp setop when published as a participant of an outer set-op.
		if (!isLeafQueryScopeKey(scopeKey)) {
			applyParentFramePendingSetOperatorToCompositePublishingParticipant(scopePayload);
		}

		// Canonical contract: payload-bearing symbol-table keys are always def_*;
		// references (table_alias/table_ref/context/dependencies) remain live keys.
		String liveScopeKey = toLiveScopeKey(scopeKey);
		String definitionKey = toDefinitionScopeKey(scopeKey);
		if (liveScopeKey == null || definitionKey == null) {
			return;
		}

		// Incrementally update the set-op definition cache so that branches processed
		// after this one can see the just-published def_* entry in O(1) — without
		// re-scanning the entire parent symbol table on each call.
		if (!setOpDefinitionPayloadCacheStack.isEmpty()) {
			setOpDefinitionPayloadCacheStack.peek().put(definitionKey, scopePayload);
			// Also update the parallel live-query-ref cache to avoid O(n) scan in buildConvertEgressScopeBundle.
			if (!setOpLiveQueryRefCacheStack.isEmpty()) {
				String liveRef = toLiveScopeKey(definitionKey);
				if (liveRef != null && isQuerySourceReference(liveRef)) {
					setOpLiveQueryRefCacheStack.peek().add(liveRef);
				}
			}
		}

		HashMap<String, Object> scopeSummaryMap = removeScopedSetOperationSummaryMap(scopePayload);
		HashMap<String, Object> scopedQuerySummaryKeysMap = removeScopedQuerySummaryKeysMap(scopePayload);

		Object queryDictionaryObj = scopePayload.get(MUMBLE_QUERY_DICTIONARY_KEY);
		if (queryDictionaryObj instanceof HashMap<?, ?> queryDictionaryMapObj) {
			publishQueryDictionary(new QueryDictionaryPublishContext(
					liveScopeKey,
					(HashMap<String, Object>) queryDictionaryMapObj,
					scopePayload,
					true,
					true));
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

	/**
	 * Records the set operator that will introduce the next participant published into this set-op frame.
	 */
	public void stagePendingSetOperatorForNextParticipant(Object rawOperator) {
		String normalized = normalizeSetOperatorForParticipantStamp(rawOperator);
		if (normalized == null) {
			return;
		}
		if ("INTERSECTION".equals(normalized)) {
			pendingIntersectSetOperatorsForNextParticipants.push(normalized);
		} else {
			pendingUnionSetOperatorsForNextParticipants.push(normalized);
		}
	}

	/**
	 * When a unionized_query has no UNION clause, its lone leaf is a direct intersect participant.
	 */
	@SuppressWarnings("unchecked")
	public void applyIntersectSetopToPassthroughUnionizedQueryFrame(HashMap<String, Object> frameSymbols) {
		if (frameSymbols == null || frameSymbols.isEmpty()) {
			return;
		}

		String intersectSetop = consumePendingIntersectSetOperatorForNextParticipant();
		if (intersectSetop == null) {
			return;
		}

		for (Map.Entry<String, Object> entry : frameSymbols.entrySet()) {
			String key = entry.getKey();
			if (!isSetOperationParticipantKey(key) || !(entry.getValue() instanceof HashMap<?, ?>)) {
				continue;
			}
			if (!key.startsWith("def_query")) {
				continue;
			}
			((HashMap<String, Object>) entry.getValue()).put(MUMBLE_SETOP_KEY, intersectSetop);
		}
	}

	private String normalizeSetOperatorForParticipantStamp(Object rawOperator) {
		if (rawOperator == null) {
			return null;
		}
		String operatorText = rawOperator.toString().trim();
		if (operatorText.isEmpty()) {
			return null;
		}
		String normalized = operatorText.toUpperCase(java.util.Locale.ROOT);
		if ("UNION".equals(normalized)) {
			return "UNION";
		}
		if ("EXCEPT".equals(normalized)) {
			return "EXCEPT";
		}
		if ("INTERSECT".equals(normalized)) {
			return "INTERSECTION";
		}
		return null;
	}

	private void applyParentFramePendingSetOperatorToPublishingParticipant(HashMap<String, Object> scopePayload) {
		String unionSetop = consumePendingUnionSetOperatorForNextParticipant();
		if (unionSetop != null) {
			scopePayload.put(MUMBLE_SETOP_KEY, unionSetop);
		}
	}

	private void applyParentFramePendingSetOperatorToCompositePublishingParticipant(HashMap<String, Object> scopePayload) {
		String intersectSetop = consumePendingIntersectSetOperatorForNextParticipant();
		if (intersectSetop != null) {
			scopePayload.put(MUMBLE_SETOP_KEY, intersectSetop);
			return;
		}
		String unionSetop = consumePendingUnionSetOperatorForNextParticipant();
		if (unionSetop != null) {
			scopePayload.put(MUMBLE_SETOP_KEY, unionSetop);
		}
	}

	private String consumePendingUnionSetOperatorForNextParticipant() {
		if (pendingUnionSetOperatorsForNextParticipants.isEmpty()) {
			return null;
		}
		return pendingUnionSetOperatorsForNextParticipants.pop();
	}

	private String consumePendingIntersectSetOperatorForNextParticipant() {
		if (pendingIntersectSetOperatorsForNextParticipants.isEmpty()) {
			return null;
		}
		return pendingIntersectSetOperatorsForNextParticipants.pop();
	}

	private boolean isLeafQueryScopeKey(String scopeKey) {
		return scopeKey != null && scopeKey.startsWith(MUMBLE_QUERY_KEY);
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

	public void wrapInsertTargetFromDefaultValues(
			String insertTargetTableRef,
			Map<String, Object> insertColumns) {
		Map<String, Object> insertInterface = mapInsertTargetInterfaceFromResolvedSource(
				new HashMap<String, Object>(),
				insertColumns,
				null,
				insertTargetTableRef);
		if (!insertInterface.isEmpty()) {
			walker.symbolTable.put(TEMP_INSERT_TARGET_INTERFACE_KEY, insertInterface);
		}
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
			walker.symbolTable.put(TEMP_INSERT_TARGET_INTERFACE_KEY, insertInterface);
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
	private void enrichTableAliasMapWithRelationalModifierBucketAliases(
			HashMap<String, Object> localTableAliasMap,
			HashMap<String, Object> localSourceColumnsByBucket) {
		if (localTableAliasMap == null
				|| localSourceColumnsByBucket == null
				|| localSourceColumnsByBucket.isEmpty()) {
			return;
		}
		for (Map.Entry<String, Object> bucketEntry : localSourceColumnsByBucket.entrySet()) {
			String bucketKey = bucketEntry.getKey();
			if (bucketKey == null || bucketKey.isBlank()) {
				continue;
			}
			Object refsObj = bucketEntry.getValue();
			if (!(refsObj instanceof ArrayList<?> refs) || refs.isEmpty()) {
				continue;
			}
			String sourceTableRef = walker.extractReferenceTableRefFromInterfaceEntry(refs.get(0));
			if (sourceTableRef == null || sourceTableRef.isBlank()) {
				continue;
			}
			localTableAliasMap.putIfAbsent(bucketKey, sourceTableRef);
		}
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
		HashMap<String, Object> scopeDerivedColumns = getScopeDerivedColumnsFromSymbolTable();
		RelationalModifierConvertEgressContext scopeRelationalModifierContext =
				getScopeRelationalModifierContextFromSymbolTable();
		if (isRelationalModifierDerivedColumnReference(
				scopeDerivedColumns,
				scopeRelationalModifierContext,
				sourceRef,
				columnName,
				visibleAliasMap)) {
			return false;
		}
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
		String canonicalTableRef = resolveCanonicalPhysicalTableRef(resolvedTableRef, visibleAliasMap);
		if (canonicalTableRef == null || canonicalTableRef.isBlank()) {
			canonicalTableRef = normalizeTableRef(resolvedTableRef);
		}
		if (canonicalTableRef == null || canonicalTableRef.isBlank()) {
			return false;
		}
		if (walker.isNonTableQuerySourceReference(canonicalTableRef)
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

		String normalizedTableRef = canonicalTableRef;
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
		RelationalModifierConvertEgressContext scopeRelationalModifierContext =
				getScopeRelationalModifierContextFromSymbolTable();

		QualifiedScopeResolutionResult resolutionResult =
				resolveQualifiedColumnAgainstVisibleScope(
						tableRef,
						columnName,
						visibleAliasMap,
						visibleTableCollection,
						visibleQuerySourceCollection,
						false,
						scopeDerivedColumns,
						scopeRelationalModifierContext);

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
			case RESOLVED_DERIVED_COLUMN, RESOLVED_UNPIVOT_VALUE, RESOLVED_UNPIVOT_FOR -> {
				return true;
			}
			case RESOLVED_PIVOT_OPERAND, RESOLVED_UNPIVOT_IN_SOURCE -> {
				HashMap<String, Object> targetCollection = localTableCollectionOverride != null
						? localTableCollectionOverride
						: visibleTableCollection;
				if (resolutionResult.resolvedPhysicalTableRef != null
						&& !resolutionResult.resolvedPhysicalTableRef.isBlank()) {
					mergeSourceLineageIntoPhysicalTableDictionary(
							targetCollection,
							resolutionResult.resolvedPhysicalTableRef,
							columnName,
							entryValue);
				}
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
	/**
	 * Called at {@code enterUnionized_query} after the new child scope is pushed.
	 * Snapshots all {@code def_*} entries currently visible from ancestor scopes so that
	 * subsequent branches can use {@link #buildConvertEgressScopeBundle} in O(1) instead of
	 * rescanning the growing parent symbol table on every branch (O(n²) → O(n)).
	 */
	public void startSetOpDefinitionCache() {
		HashMap<String, Object> snapshot = new HashMap<>();
		for (Map<String, Object> ancestor : getAncestorSymbolTables()) {
			mergeDefinitionPayloadsFromSymbolTable(ancestor, snapshot);
		}
		setOpDefinitionPayloadCacheStack.push(snapshot);

		// Build the parallel live-query-ref set from the initial snapshot.
		LinkedHashSet<String> liveRefs = new LinkedHashSet<>();
		for (String defKey : snapshot.keySet()) {
			String liveRef = toLiveScopeKey(defKey);
			if (liveRef != null && isQuerySourceReference(liveRef)) {
				liveRefs.add(liveRef);
			}
		}
		setOpLiveQueryRefCacheStack.push(liveRefs);
	}

	/**
	 * Called at {@code exitUnionized_query} / {@code exitIntersected_query} BEFORE
	 * {@link #finalizeSetOperationScopeSymbolTable} so the union scope's own publish is not
	 * added to the cache of its parent.
	 */
	public void stopSetOpDefinitionCache() {
		if (!setOpDefinitionPayloadCacheStack.isEmpty()) {
			setOpDefinitionPayloadCacheStack.pop();
		}
		if (!setOpLiveQueryRefCacheStack.isEmpty()) {
			setOpLiveQueryRefCacheStack.pop();
		}
	}

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
			appendClauseColumnReferenceForConvertEgress(columnList, col);
			return;
		}
		if (subTree.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
			Object subst = subTree.get(MUMBLE_SUBSTITUTION_KEY);
			if (subst instanceof HashMap<?, ?> substMapObj) {
				Object type = substMapObj.get("type");
				if (type != null && (MUMBLE_COLUMN_KEY.equals(type) || MUMBLE_PREDICAND_KEY.equals(type))) {
					appendClauseColumnReferenceForConvertEgress(columnList, subst);
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
			String updateTargetTableRef,
			HashMap<String, Object> localDerivedColumns,
			RelationalModifierConvertEgressContext relationalModifierContext) {
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

				ConvertEgressResolutionContext updateRhsCtx = new ConvertEgressResolutionContext(
						localDerivedColumns,
						activeConvertEgressRelationalModifierSourceColumns,
						relationalModifierContext,
						buildLocalPhysicalFromTableCollection(tableCollection),
						tableCollection,
						visibleQuerySourceCollection,
						tableAliasMap,
						tableAliasMap,
						tableCollection,
						updateTargetTableRef,
						UPDATE_ASSIGNMENT_RHS_CLAUSE_PROBE_KEY,
						true,
						true,
						false,
						false,
						false,
						null);
				ConvertEgressColumnResolutionResult updateRhsEgressResult =
						resolveColumnRefAtConvertEgress(columnName, tableRef, updateRhsCtx);
				if (updateRhsEgressResult.isDerivedColumn()) {
					continue;
				}
				if (updateRhsEgressResult.isPivotOperandColumn()) {
					applyConvertEgressPivotOperandMaterialization(
							updateRhsEgressResult,
							columnName,
							tableRef,
							unresolvedColumnMap,
							tableCollection,
							relationalModifierContext,
							tableAliasMap);
					continue;
				}
				if (updateRhsEgressResult.isUnpivotInSourceColumn()) {
					applyConvertEgressUnpivotInSourceMaterialization(
							updateRhsEgressResult,
							columnName,
							tableRef,
							unresolvedColumnMap,
							tableCollection,
							relationalModifierContext,
							tableAliasMap);
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
				columnName,
				null);
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
		materializeResolvedUnqualifiedReference(
				unresolvedColumnMap,
				tableCollection,
				tableAliasCollection,
				localCurrentQueryDictionary,
				localInterface,
				visibleQuerySourceCollection,
				clauseKey,
				resolvedSourceRef,
				columnName,
				null,
				false);
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
			String columnName,
			Object archivedRefTokens) {
		materializeResolvedUnqualifiedReference(
				unresolvedColumnMap,
				tableCollection,
				tableAliasCollection,
				localCurrentQueryDictionary,
				localInterface,
				visibleQuerySourceCollection,
				clauseKey,
				resolvedSourceRef,
				columnName,
				archivedRefTokens,
				false);
	}

	/** Tokens in an output {@code query_dictionary} entry that name {@code operandColumnName} (SELECT expression deps). */
	private Object queryDictionaryOperandDependencyTokens(
			Object queryColumnRefs,
			String operandColumnName) {
		if (queryColumnRefs == null || operandColumnName == null || operandColumnName.isBlank()) {
			return null;
		}
		String needle = "='" + operandColumnName + "'";
		ArrayList<Object> matches = new ArrayList<Object>();
		if (queryColumnRefs instanceof List<?> tokenList) {
			for (Object tokenObj : tokenList) {
				if (tokenObj != null && tokenObj.toString().contains(needle)) {
					matches.add(tokenObj);
				}
			}
		} else if (queryColumnRefs.toString().contains(needle)) {
			matches.add(queryColumnRefs);
		}
		return matches.isEmpty() ? null : matches;
	}

	/**
	 * M4 convert egress: dual lookup for UNPIVOT VALUE operand sites — (1) ephemeral {@code locations}
	 * on the interface dependency ref (SELECT attach), (2) operand-named tokens already on
	 * {@code query_dictionary[output]} if present. Does not merge the full {@code unresolved_column}
	 * bucket for the VALUE name (that would leak clause/unknown sites into {@code derived_columns}).
	 */
	private Object coalesceUnpivotValueOperandDependencySiteTokensAtConvertEgress(
			Object interfaceRefObj,
			int interfaceRefIndex,
			String valueColumnName,
			String interfaceOutputColumn,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> unresolvedColumnMap) {
		Object siteTokens = coalesceMaterializationRefTokens(null, interfaceRefObj);
		Object queryColumnRefs = localCurrentQueryDictionary == null
				? null
				: localCurrentQueryDictionary.get(interfaceOutputColumn);
		siteTokens = coalesceMaterializationRefTokens(
				siteTokens,
				queryDictionaryOperandDependencyTokens(queryColumnRefs, valueColumnName));
		return siteTokens;
	}

	/**
	 * Before {@link #applyUnpivotDerivationsToQueryScope} rewrites VALUE refs to IN-list physical
	 * columns, materialize SELECT expression sites onto structured {@code derived_columns} via dual
	 * lookup (interface ref sites + {@code query_dictionary} operand tokens only).
	 */
	@SuppressWarnings("unchecked")
	private void materializeUnpivotValueOperandSelectExpressionSitesBeforeDerivationRewrite(
			HashMap<String, Object> localInterface,
			RelationalModifierConvertEgressContext relationalModifierContext,
			HashMap<String, Object> localDerivedColumns,
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> localCurrentQueryDictionary) {
		if (localInterface == null
				|| localInterface.isEmpty()
				|| relationalModifierContext == null
				|| !relationalModifierContext.isUnpivot()
				|| localDerivedColumns == null) {
			return;
		}
		InferredUnpivotDerivedOutputs unpivotOutputs =
				inferUnpivotDerivedOutputColumnsFromContext(relationalModifierContext);
		if (unpivotOutputs == null
				|| unpivotOutputs.valueColumn == null
				|| unpivotOutputs.valueColumn.isBlank()) {
			return;
		}
		String valueColumn = unpivotOutputs.valueColumn;
		for (Map.Entry<String, Object> interfaceEntry : localInterface.entrySet()) {
			String interfaceOutputColumn = interfaceEntry.getKey();
			if (interfaceOutputColumn == null
					|| interfaceOutputColumn.isBlank()
					|| interfaceOutputColumn.equalsIgnoreCase(valueColumn)) {
				continue;
			}
			Object refsObj = interfaceEntry.getValue();
			if (!(refsObj instanceof ArrayList<?> refs)) {
				continue;
			}
			ArrayList<Object> mutableRefs = (ArrayList<Object>) refs;
			for (int refIndex = 0; refIndex < mutableRefs.size(); refIndex++) {
				Object refObj = mutableRefs.get(refIndex);
				String refColumnName = walker.extractReferenceNameFromInterfaceEntry(refObj);
				if (refColumnName == null || !refColumnName.equalsIgnoreCase(valueColumn)) {
					continue;
				}
				materializeInterfaceUnpivotValueOperandDependencyLineage(
						relationalModifierContext,
						valueColumn,
						refObj,
						refIndex,
						interfaceOutputColumn,
						localCurrentQueryDictionary,
						localDerivedColumns,
						unresolvedColumnMap);
			}
		}
	}

	private Object coalesceInterfaceOperandDependencySiteTokens(
			Object interfaceRefObj,
			int interfaceRefIndex,
			String operandColumnName,
			String interfaceOutputColumn,
			HashMap<String, Object> localCurrentQueryDictionary) {
		Object queryColumnRefs = localCurrentQueryDictionary == null
				? null
				: localCurrentQueryDictionary.get(interfaceOutputColumn);
		Object queryDictionarySites;
		if (interfaceOutputColumn != null && interfaceOutputColumn.equalsIgnoreCase(operandColumnName)) {
			queryDictionarySites = queryDictionaryRefTokenAtSiteIndex(queryColumnRefs, interfaceRefIndex);
		} else {
			queryDictionarySites = queryDictionaryOperandDependencyTokens(queryColumnRefs, operandColumnName);
		}
		return coalesceMaterializationRefTokens(interfaceRefObj, queryDictionarySites);
	}

	/**
	 * M3: bind interface dependency sites for pivot operands onto the operand physical table.
	 * Coalesces walk/interface ref tokens with SELECT expression operand sites from
	 * {@code query_dictionary} (by column name, not output alias).
	 */
	private void materializeInterfacePivotOperandDependencyLineage(
			String materializeTableRef,
			String operandColumnName,
			Object interfaceRefObj,
			int interfaceRefIndex,
			String interfaceOutputColumn,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> tableAliasCollection,
			HashMap<String, Object> visibleQuerySourceCollection) {
		if (materializeTableRef == null || materializeTableRef.isBlank()) {
			return;
		}
		Object dependencySiteTokens = coalesceInterfaceOperandDependencySiteTokens(
				interfaceRefObj,
				interfaceRefIndex,
				operandColumnName,
				interfaceOutputColumn,
				localCurrentQueryDictionary);
		materializeInterfaceOutputSourceLineage(
				materializeTableRef,
				operandColumnName,
				dependencySiteTokens,
				unresolvedColumnMap,
				tableCollection,
				tableAliasCollection,
				visibleQuerySourceCollection);
	}

	/**
	 * M3: unpivot IN-list source columns referenced from the interface — physical lineage only.
	 */
	private void materializeInterfaceUnpivotInSourceDependencyLineage(
			String materializeTableRef,
			String inSourceColumnName,
			Object interfaceRefObj,
			int interfaceRefIndex,
			String interfaceOutputColumn,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> tableAliasCollection,
			HashMap<String, Object> visibleQuerySourceCollection) {
		materializeInterfacePivotOperandDependencyLineage(
				materializeTableRef,
				inSourceColumnName,
				interfaceRefObj,
				interfaceRefIndex,
				interfaceOutputColumn,
				localCurrentQueryDictionary,
				unresolvedColumnMap,
				tableCollection,
				tableAliasCollection,
				visibleQuerySourceCollection);
	}

	/**
	 * M4: UNPIVOT VALUE operand sites from SELECT expressions → structured {@code derived_columns} bucket.
	 */
	@SuppressWarnings("unchecked")
	private void materializeUnpivotValueOperandFromInterfaceIfNeeded(
			RelationalModifierConvertEgressContext relationalModifierContext,
			String interfaceOutputColumn,
			String operandColumnName,
			Object interfaceRefObj,
			int interfaceRefIndex,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> localDerivedColumns,
			HashMap<String, Object> unresolvedColumnMap) {
		if (interfaceOutputColumn != null
				&& operandColumnName != null
				&& interfaceOutputColumn.equalsIgnoreCase(operandColumnName)) {
			return;
		}
		materializeInterfaceUnpivotValueOperandDependencyLineage(
				relationalModifierContext,
				operandColumnName,
				interfaceRefObj,
				interfaceRefIndex,
				interfaceOutputColumn,
				localCurrentQueryDictionary,
				localDerivedColumns,
				unresolvedColumnMap);
	}

	@SuppressWarnings("unchecked")
	private void materializeInterfaceUnpivotValueOperandDependencyLineage(
			RelationalModifierConvertEgressContext relationalModifierContext,
			String valueColumnName,
			Object interfaceRefObj,
			int interfaceRefIndex,
			String interfaceOutputColumn,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> localDerivedColumns,
			HashMap<String, Object> unresolvedColumnMap) {
		if (relationalModifierContext == null
				|| !relationalModifierContext.isUnpivot()
				|| valueColumnName == null
				|| valueColumnName.isBlank()
				|| localDerivedColumns == null) {
			return;
		}
		InferredUnpivotDerivedOutputs unpivotOutputs =
				inferUnpivotDerivedOutputColumnsFromContext(relationalModifierContext);
		if (unpivotOutputs == null
				|| unpivotOutputs.valueColumn == null
				|| !unpivotOutputs.valueColumn.equalsIgnoreCase(valueColumnName)) {
			return;
		}
		Object dependencySiteTokens = coalesceUnpivotValueOperandDependencySiteTokensAtConvertEgress(
				interfaceRefObj,
				interfaceRefIndex,
				valueColumnName,
				interfaceOutputColumn,
				localCurrentQueryDictionary,
				unresolvedColumnMap);
		if (dependencySiteTokens != null) {
			mergeUnpivotValueOperandSitesIntoStructuredDerivedColumns(
					localDerivedColumns,
					valueColumnName,
					dependencySiteTokens);
		}
	}

	@SuppressWarnings("unchecked")
	private void mergeUnpivotValueOperandSitesIntoStructuredDerivedColumns(
			HashMap<String, Object> localDerivedColumns,
			String valueColumnName,
			Object dependencySiteTokens) {
		if (localDerivedColumns == null || valueColumnName == null || dependencySiteTokens == null) {
			return;
		}
		for (Object bucketObj : localDerivedColumns.values()) {
			if (bucketObj instanceof Map<?, ?> bucketMapObj) {
				HashMap<String, Object> bucketMap = (HashMap<String, Object>) bucketMapObj;
				boolean bucketHasValueColumn = false;
				for (String bucketColumnKey : bucketMap.keySet()) {
					if (bucketColumnKey != null
							&& bucketColumnKey.equalsIgnoreCase(valueColumnName)) {
						bucketHasValueColumn = true;
						break;
					}
				}
				if (bucketHasValueColumn) {
					walker.mergeResolvedColumnIntoDictionary(
							bucketMap,
							valueColumnName,
							dependencySiteTokens);
					return;
				}
			}
		}
		walker.mergeResolvedColumnIntoDictionary(
				localDerivedColumns,
				valueColumnName,
				dependencySiteTokens);
	}

	/**
	 * Interface-loop lineage pass: bind SELECT-list source tokens to {@code table_dictionary}
	 * only. Output identity tokens remain on {@code query_dictionary} from phase-1 walk capture.
	 */
	private void materializeInterfaceOutputSourceLineage(
			String resolvedSourceRef,
			String sourceColumnName,
			Object interfaceRefObj,
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> tableAliasCollection,
			HashMap<String, Object> visibleQuerySourceCollection) {
		if (sourceColumnName == null || sourceColumnName.isBlank()) {
			return;
		}

		Object unresolvedEntry = getUnqualifiedUnknownEntry(unresolvedColumnMap, sourceColumnName);
		Object tokenPayload = coalesceMaterializationRefTokens(unresolvedEntry, interfaceRefObj);
		consumeUnqualifiedUnknownEntry(unresolvedColumnMap, sourceColumnName);

		if (tokenPayload == null || resolvedSourceRef == null || resolvedSourceRef.isBlank()) {
			return;
		}

		String canonicalSourceRef = normalizeTableRef(resolvedSourceRef);
		String queryAliasSourceRef = resolveAliasToQuerySourceFromAliasMap(
				canonicalSourceRef,
				tableAliasCollection);
		boolean queryBackedSource = queryAliasSourceRef != null
				|| walker.isNonTableQuerySourceReference(canonicalSourceRef)
				|| isTableFunctionSourceReference(canonicalSourceRef);
		if (!queryBackedSource && canonicalSourceRef != null && canonicalSourceRef.startsWith("<")) {
			Object substitutionType = walker.substitutionsMap == null
					? null
					: walker.substitutionsMap.get(canonicalSourceRef);
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
					sourceColumnName,
					tokenPayload);
			return;
		}

		mergeSourceLineageIntoPhysicalTableDictionary(
				tableCollection,
				canonicalSourceRef,
				sourceColumnName,
				tokenPayload);
	}

	@SuppressWarnings("unchecked")
	private void mergeSourceLineageIntoPhysicalTableDictionary(
			HashMap<String, Object> tableCollection,
			String canonicalSourceRef,
			String columnName,
			Object tokenPayload) {
		if (tableCollection == null || tokenPayload == null) {
			return;
		}

		HashMap<String, Object> tableAliasMap = null;
		Object tableAliasObj = walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY);
		if (tableAliasObj instanceof Map<?, ?> tableAliasMapObj && !tableAliasMapObj.isEmpty()) {
			tableAliasMap = new HashMap<String, Object>((Map<String, Object>) tableAliasMapObj);
		}
		String dictionaryKey = resolveCanonicalPhysicalTableRef(canonicalSourceRef, tableAliasMap);
		if (dictionaryKey == null || dictionaryKey.isBlank()) {
			dictionaryKey = normalizeTableRef(canonicalSourceRef);
		}
		if (dictionaryKey == null || dictionaryKey.isBlank()) {
			return;
		}

		// 17.7.3 / 17.7.8: block structured derived outputs on physical keys; allow per-bucket
		// source_columns operands (sibling modifiers may share a column name as derived vs IN-list).
		if (columnName != null
				&& activeConvertEgressDerivedColumns != null
				&& containsStructuredDerivedColumnName(activeConvertEgressDerivedColumns, columnName)
				&& !isRelationalModifierSourceColumnForPhysicalTable(
						columnName,
						dictionaryKey,
						activeConvertEgressRelationalModifierSourceColumns,
						tableAliasMap)) {
			return;
		}

		HashMap<String, Object> indicatedTableDictionary = walker.getTableDictionaryForReference(
				dictionaryKey,
				tableCollection);
		if (indicatedTableDictionary == null) {
			Object existing = tableCollection.get(dictionaryKey);
			if (existing instanceof HashMap<?, ?> existingMapObj) {
				indicatedTableDictionary = (HashMap<String, Object>) existingMapObj;
			} else {
				indicatedTableDictionary = new HashMap<String, Object>();
				tableCollection.put(dictionaryKey, indicatedTableDictionary);
			}
		}

		walker.mergeResolvedColumnIntoDictionary(indicatedTableDictionary, columnName, tokenPayload);
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
			String columnName,
			Object archivedRefTokens,
			boolean sourceLineageOnly) {
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
		Object tokenPayload = coalesceMaterializationRefTokens(unresolvedEntry, archivedRefTokens);
		if (tokenPayload == null || resolvedSourceRef == null || resolvedSourceRef.isBlank()) {
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
					tokenPayload);
			return;
		}

		if (!sourceLineageOnly) {
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
						tokenPayload);
				if (isIntraQueryOutputAliasUsage(columnName, null, localInterface, tableCollection)) {
					return;
				}
			}
		}

		mergeSourceLineageIntoPhysicalTableDictionary(
				tableCollection,
				canonicalSourceRef,
				columnName,
				tokenPayload);
	}

	private Object coalesceMaterializationRefTokens(Object primaryEntry, Object supplementalEntry) {
		ArrayList<Object> combined = new ArrayList<Object>();
		appendMaterializationRefTokens(combined, primaryEntry);
		appendMaterializationRefTokens(combined, supplementalEntry);
		return combined.isEmpty() ? null : combined;
	}

	/** One output-interface site token from {@code query_dictionary} (excludes later clause refs). */
	private Object queryDictionaryRefTokenAtSiteIndex(Object queryColumnRefs, int siteIndex) {
		if (queryColumnRefs == null || siteIndex < 0) {
			return null;
		}
		if (queryColumnRefs instanceof List<?> tokenList) {
			if (siteIndex >= tokenList.size()) {
				return null;
			}
			Object token = tokenList.get(siteIndex);
			if (token == null) {
				return null;
			}
			ArrayList<Object> singleSite = new ArrayList<Object>();
			singleSite.add(token);
			return singleSite;
		}
		return siteIndex == 0 ? queryColumnRefs : null;
	}

	@SuppressWarnings("unchecked")
	private void appendMaterializationRefTokens(ArrayList<Object> target, Object entry) {
		if (target == null || entry == null) {
			return;
		}

		if (entry instanceof Map<?, ?> entryMap) {
			Object locationsObj = ((Map<String, Object>) entryMap).get("locations");
			if (locationsObj instanceof List<?> locationList) {
				for (Object locationObj : locationList) {
					if (locationObj != null && !target.contains(locationObj)) {
						target.add(locationObj);
					}
				}
				return;
			}
		}

		if (entry instanceof List<?> tokenList) {
			for (Object tokenObj : tokenList) {
				if (tokenObj != null && !target.contains(tokenObj)) {
					target.add(tokenObj);
				}
			}
			return;
		}

		if (entry instanceof String tokenString && !target.contains(tokenString)) {
			target.add(tokenString);
		}
	}

	/**
	 * Walk-time (M1): relocate matching {@code unresolved_column} payloads into modifier registries
	 * and/or the scope {@code table_dictionary} instead of discarding them when UNPIVOT/PIVOT claims
	 * an operand name.
	 */
	@SuppressWarnings("unchecked")
	public void relocateUnresolvedModifierScopeColumnReferences(
			String operandColumnName,
			String derivedColumnsRegistryKey,
			String physicalSourceTableRef,
			String sourceColumnsRegistryKey) {
		relocateUnresolvedModifierScopeColumnReferences(
				operandColumnName,
				derivedColumnsRegistryKey,
				physicalSourceTableRef,
				sourceColumnsRegistryKey,
				physicalSourceTableRef != null && !physicalSourceTableRef.isBlank());
	}

	@SuppressWarnings("unchecked")
	public void relocateUnresolvedModifierScopeColumnReferences(
			String operandColumnName,
			String derivedColumnsRegistryKey,
			String physicalSourceTableRef,
			String sourceColumnsRegistryKey,
			boolean includeQualifiedUnresolvedKeys) {
		if (operandColumnName == null || operandColumnName.isBlank()) {
			return;
		}

		Object unresolvedObj = walker.symbolTable.get(MUMBLE_UNRESOLVED_COLUMN_KEY);
		if (!(unresolvedObj instanceof HashMap<?, ?> unresolvedMapObj) || unresolvedMapObj.isEmpty()) {
			return;
		}

		HashMap<String, Object> unresolvedMap = (HashMap<String, Object>) unresolvedMapObj;
		ArrayList<String> keysToRelocate = collectModifierScopeUnresolvedKeysForOperand(
				unresolvedMap,
				operandColumnName,
				includeQualifiedUnresolvedKeys);
		for (String keyToRelocate : keysToRelocate) {
			Object unresolvedEntry = unresolvedMap.remove(keyToRelocate);
			if (unresolvedEntry == null) {
				continue;
			}
			relocateModifierScopeUnresolvedEntry(
					operandColumnName,
					keyToRelocate,
					unresolvedEntry,
					derivedColumnsRegistryKey,
					physicalSourceTableRef,
					sourceColumnsRegistryKey);
		}

		if (unresolvedMap.isEmpty()) {
			walker.symbolTable.remove(MUMBLE_UNRESOLVED_COLUMN_KEY);
		}
	}

	public void relocateUnresolvedModifierScopeColumnReferencesForDerivedOperand(String operandColumnName) {
		relocateUnresolvedModifierScopeColumnReferences(
				operandColumnName,
				RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY,
				null,
				null);
	}

	private ArrayList<String> collectModifierScopeUnresolvedKeysForOperand(
			HashMap<String, Object> unresolvedMap,
			String operandColumnName,
			boolean includeQualifiedUnresolvedKeys) {
		ArrayList<String> keysToRelocate = new ArrayList<String>();
		String operandLower = operandColumnName.toLowerCase(java.util.Locale.ROOT);
		for (String unresolvedKey : unresolvedMap.keySet()) {
			if (unresolvedKey == null) {
				continue;
			}
			if (unresolvedKey.equalsIgnoreCase(operandColumnName)) {
				keysToRelocate.add(unresolvedKey);
				continue;
			}
			if (!includeQualifiedUnresolvedKeys || !unresolvedKey.contains(".")) {
				continue;
			}
			if (unresolvedKey.toLowerCase(java.util.Locale.ROOT).endsWith("." + operandLower)) {
				keysToRelocate.add(unresolvedKey);
			}
		}
		return keysToRelocate;
	}

	@SuppressWarnings("unchecked")
	private void relocateModifierScopeUnresolvedEntry(
			String operandColumnName,
			String unresolvedKey,
			Object unresolvedEntry,
			String derivedColumnsRegistryKey,
			String physicalSourceTableRef,
			String sourceColumnsRegistryKey) {
		if (derivedColumnsRegistryKey != null && !derivedColumnsRegistryKey.isBlank()) {
			Object registryObj = walker.symbolTable.get(derivedColumnsRegistryKey);
			if (registryObj instanceof HashMap<?, ?> registryMapObj) {
				walker.mergeResolvedColumnIntoDictionary(
						(HashMap<String, Object>) registryMapObj,
						operandColumnName,
						unresolvedEntry);
			}
		}

		if (sourceColumnsRegistryKey != null && !sourceColumnsRegistryKey.isBlank()) {
			Object registryObj = walker.symbolTable.get(sourceColumnsRegistryKey);
			if (registryObj instanceof HashMap<?, ?> registryMapObj) {
				walker.mergeResolvedColumnIntoDictionary(
						(HashMap<String, Object>) registryMapObj,
						operandColumnName,
						unresolvedEntry);
			}
		}

		if (physicalSourceTableRef == null || physicalSourceTableRef.isBlank()) {
			return;
		}

		String tableRefForLineage = physicalSourceTableRef;
		String columnNameForLineage = operandColumnName;
		int separatorIndex = unresolvedKey == null ? -1 : unresolvedKey.lastIndexOf('.');
		if (separatorIndex > 0 && separatorIndex + 1 < unresolvedKey.length()) {
			String qualifier = unresolvedKey.substring(0, separatorIndex);
			String qualifiedColumn = unresolvedKey.substring(separatorIndex + 1);
			if (!qualifiedColumn.isBlank()) {
				columnNameForLineage = qualifiedColumn;
			}
			if (!qualifier.isBlank()) {
				tableRefForLineage = qualifier;
			}
		}

		HashMap<String, Object> tableAliasMap = null;
		Object tableAliasObj = walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY);
		if (tableAliasObj instanceof Map<?, ?> tableAliasMapObj && !tableAliasMapObj.isEmpty()) {
			tableAliasMap = new HashMap<String, Object>((Map<String, Object>) tableAliasMapObj);
		}
		String canonicalPhysicalRef = resolveCanonicalPhysicalTableRef(tableRefForLineage, tableAliasMap);
		if (canonicalPhysicalRef == null || canonicalPhysicalRef.isBlank()) {
			canonicalPhysicalRef = normalizeTableRef(tableRefForLineage);
		}
		String canonicalOperandSource = resolveCanonicalPhysicalTableRef(physicalSourceTableRef, tableAliasMap);
		if (canonicalOperandSource == null || canonicalOperandSource.isBlank()) {
			canonicalOperandSource = normalizeTableRef(physicalSourceTableRef);
		}
		if (canonicalPhysicalRef == null
				|| canonicalOperandSource == null
				|| !canonicalPhysicalRef.equalsIgnoreCase(canonicalOperandSource)) {
			return;
		}

		Object tableDictionaryObj = walker.symbolTable.get(MUMBLE_TABLE_DICTIONARY_KEY);
		HashMap<String, Object> tableDictionary;
		if (tableDictionaryObj instanceof HashMap<?, ?> tableDictionaryMapObj) {
			tableDictionary = (HashMap<String, Object>) tableDictionaryMapObj;
		} else {
			tableDictionary = new HashMap<String, Object>();
			walker.symbolTable.put(MUMBLE_TABLE_DICTIONARY_KEY, tableDictionary);
		}
		HashMap<String, Object> physicalDictionary = walker.getTableDictionaryForReference(
				canonicalPhysicalRef,
				tableDictionary);
		if (physicalDictionary == null) {
			physicalDictionary = new HashMap<String, Object>();
			tableDictionary.put(canonicalPhysicalRef, physicalDictionary);
		}
		walker.mergeResolvedColumnIntoDictionary(
				physicalDictionary,
				columnNameForLineage,
				unresolvedEntry);
	}

	private boolean isBareValueExpressionUnqualifiedReference(String tableRef, String columnName) {
		if (columnName == null || columnName.isBlank()) {
			return false;
		}
		if (tableRef != null && !tableRef.isBlank() && !"*".equals(tableRef)) {
			return false;
		}
		return SqlBareValueExpressionRegistry.classify(columnName) != null;
	}

	@SuppressWarnings("unchecked")
	private Integer[] resolveBareValueExpressionDiagnosticLocation(Object unresolvedEntry) {
		Integer[] refLocation = walker.getLineAndCharacterFromEntry(unresolvedEntry);
		if (refLocation[0] != null && refLocation[1] != null) {
			return refLocation;
		}
		if (unresolvedEntry instanceof Map<?, ?> entryMap) {
			refLocation = walker.getFirstEntryLineAndCharacter((HashMap<String, Object>) entryMap);
			if (refLocation[0] != null && refLocation[1] != null) {
				return refLocation;
			}
		}
		return new Integer[] { null, null };
	}

	private void recordBareValueExpressionDialectHitIfNeeded(String columnName, Object unresolvedEntry) {
		Affinity affinity = SqlBareValueExpressionRegistry.classify(columnName);
		if (affinity == null || affinity == Affinity.COMMON) {
			return;
		}
		Integer[] refLocation = resolveBareValueExpressionDiagnosticLocation(unresolvedEntry);
		if (refLocation[0] == null || refLocation[1] == null) {
			return;
		}
		SqlGrammarDialect dialect = affinity == Affinity.SNOWFLAKE_ONLY
				? SqlGrammarDialect.SNOWFLAKE
				: SqlGrammarDialect.POSTGRES;
		walker.notifyStatementDialectGrammarHit(
				dialect,
				refLocation[0],
				refLocation[1],
				SqlBareValueExpressionRegistry.dialectConstructLabel(columnName));
	}

	/**
	 * Returns {@code true} when {@code columnName} is an unqualified bare value expression and was
	 * consumed (or was already absent) from {@code unresolvedColumnMap}.
	 */
	public boolean tryConsumeBareValueExpressionUnknown(
			HashMap<String, Object> unresolvedColumnMap,
			String tableRef,
			String columnName,
			Object unresolvedEntryHint) {
		if (!isBareValueExpressionUnqualifiedReference(tableRef, columnName)) {
			return false;
		}
		Object entryForDialect = unresolvedEntryHint;
		if (entryForDialect == null && unresolvedColumnMap != null) {
			entryForDialect = getUnqualifiedUnknownEntry(unresolvedColumnMap, columnName);
		}
		recordBareValueExpressionDialectHitIfNeeded(columnName, entryForDialect);
		if (unresolvedColumnMap != null && !unresolvedColumnMap.isEmpty()) {
			consumeUnqualifiedUnknownEntry(unresolvedColumnMap, columnName);
		}
		return true;
	}

	/** Convert-egress site 1 / site 6: O(n) batch prune of unqualified bare value keys. */
	public void pruneBareValueExpressionsFromUnresolvedMap(HashMap<String, Object> unresolvedColumnMap) {
		if (unresolvedColumnMap == null || unresolvedColumnMap.isEmpty()) {
			return;
		}
		for (String unresolvedKey : new ArrayList<String>(unresolvedColumnMap.keySet())) {
			if (unresolvedKey == null || unresolvedKey.isBlank() || unresolvedKey.contains(".")) {
				continue;
			}
			Object unresolvedEntry = unresolvedColumnMap.get(unresolvedKey);
			String columnName = extractColumnNameFromUnresolvedEntry(unresolvedKey, unresolvedEntry);
			if (columnName == null || columnName.isBlank()) {
				columnName = unresolvedKey;
			}
			tryConsumeBareValueExpressionUnknown(unresolvedColumnMap, null, columnName, unresolvedEntry);
		}
	}

	private boolean skipBareValueExpressionAtConvertEgressRefSite(
			HashMap<String, Object> unresolvedColumnMap,
			String tableRef,
			String columnName,
			Object refObj) {
		return tryConsumeBareValueExpressionUnknown(unresolvedColumnMap, tableRef, columnName, refObj);
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
			RelationalModifierConvertEgressContext relationalModifierContext,
			String tableRef,
			String columnName) {
		return isRelationalModifierDerivedColumnReference(
				localDerivedColumns,
				relationalModifierContext,
				tableRef,
				columnName,
				null);
	}

	@SuppressWarnings("unchecked")
	private boolean isRelationalModifierDerivedColumnReference(
			HashMap<String, Object> localDerivedColumns,
			RelationalModifierConvertEgressContext relationalModifierContext,
			String tableRef,
			String columnName,
			HashMap<String, Object> visibleAliasMap) {
		if (!containsDerivedColumnName(localDerivedColumns, columnName)) {
			return false;
		}

		String structuredBucketKey = resolveRelationalModifierDerivedColumnBucketKey(
				columnName,
				null,
				localDerivedColumns,
				visibleAliasMap);
		if (structuredBucketKey != null) {
			if (tableRef == null || tableRef.isBlank()) {
				return true;
			}
			if (isRelationalModifierDerivedColumnBucketKey(tableRef, localDerivedColumns)) {
				return resolveRelationalModifierDerivedColumnBucketKey(
						columnName,
						tableRef,
						localDerivedColumns,
						visibleAliasMap) != null;
			}
			return true;
		}

		if (tableRef == null || tableRef.isBlank()) {
			ArrayList<String> structuredBuckets = collectRelationalModifierStructuredDerivedColumnBucketKeys(
					columnName,
					localDerivedColumns);
			if (structuredBuckets.size() >= 2) {
				return false;
			}
			if (structuredBuckets.size() == 1) {
				return true;
			}
			return true;
		}
		if (relationalModifierContext != null
				&& !relationalModifierContext.isEmpty()
				&& containsDerivedColumnNameInContext(relationalModifierContext, columnName)
				&& structuredContextSourceRefMatches(relationalModifierContext, tableRef, visibleAliasMap)) {
			return true;
		}

		return derivedColumnMapSourceRefMatches(localDerivedColumns, columnName, tableRef, visibleAliasMap);
	}

	private boolean containsDerivedColumnNameInContext(
			RelationalModifierConvertEgressContext ctx,
			String columnName) {
		if (ctx == null || columnName == null || columnName.isBlank()) {
			return false;
		}
		for (Object bucketObj : ctx.derivedColumnsByBucket.values()) {
			if (bucketObj instanceof Map<?, ?> bucketMap
					&& containsKeyIgnoreCase((Map<String, Object>) bucketMap, columnName)) {
				return true;
			}
		}
		return structuredContextDefinesPivotDerivedOutputColumn(ctx, columnName);
	}

	@SuppressWarnings("unchecked")
	private boolean containsDerivedColumnName(HashMap<String, Object> localDerivedColumns, String columnName) {
		if (localDerivedColumns == null || localDerivedColumns.isEmpty()
				|| columnName == null || columnName.isBlank()) {
			return false;
		}

		if (containsKeyIgnoreCase(localDerivedColumns, columnName)) {
			return true;
		}

		return containsStructuredDerivedColumnName(localDerivedColumns, columnName);
	}

	/**
	 * Phase 17.7.3: structured {@code derivation.derived_columns} buckets only (per sibling modifier),
	 * not legacy flat keys on the derivation map root.
	 */
	@SuppressWarnings("unchecked")
	private boolean containsStructuredDerivedColumnName(
			HashMap<String, Object> localDerivedColumns,
			String columnName) {
		if (localDerivedColumns == null || localDerivedColumns.isEmpty()
				|| columnName == null || columnName.isBlank()) {
			return false;
		}

		for (Object bucketObj : localDerivedColumns.values()) {
			if (bucketObj instanceof Map<?, ?> bucketMap
					&& isRelationalModifierStructuredDerivedColumnBucket((Map<String, Object>) bucketMap)
					&& containsKeyIgnoreCase((Map<String, Object>) bucketMap, columnName)) {
				return true;
			}
		}

		return false;
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

		String qualifierBucketKey = findKeyIgnoreCase(localDerivedColumns, tableRef);
		if (qualifierBucketKey != null) {
			Object qualifierBucketObj = localDerivedColumns.get(qualifierBucketKey);
			if (qualifierBucketObj instanceof Map<?, ?> qualifierBucketMap
					&& isRelationalModifierStructuredDerivedColumnBucket(
							(Map<String, Object>) qualifierBucketMap)
					&& containsKeyIgnoreCase((Map<String, Object>) qualifierBucketMap, columnName)) {
				return true;
			}
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

	/**
	 * True when a qualified derived ref uses a physical {@code FROM} table name directly
	 * (not a PIVOT/UNPIVOT/subquery alias). Allows qualifier-token lineage for cases like
	 * {@code tab1.A_sum} while suppressing modifier-alias refs such as {@code pvt.A_sum}.
	 */
	private boolean isPhysicalTableQualifierForDerivedLineage(
			String tableRef,
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> tableAliasCollection) {
		if (tableRef == null || tableRef.isBlank() || localTableCollection == null) {
			return false;
		}
		if (tableAliasCollection != null
				&& (tableAliasCollection.containsKey(tableRef)
						|| containsKeyIgnoreCase(tableAliasCollection, tableRef))) {
			return false;
		}

		String normalizedRef = normalizeTableRef(tableRef);
		return localTableCollection.containsKey(normalizedRef)
				|| containsKeyIgnoreCase(localTableCollection, normalizedRef);
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
	public void promoteRelationalModifierDerivedColumnsBucketKeyIfNeeded(String relationAlias) {
		if (relationAlias == null || relationAlias.isBlank()) {
			return;
		}

		HashMap<String, Object> derivedColumnsMap = getRelationalModifierDerivationSubMapFromScope(
				walker.symbolTable,
				RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY);
		if (derivedColumnsMap == null) {
			Object derivedColumnsObj = walker.symbolTable.get(RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY);
			if (!(derivedColumnsObj instanceof Map<?, ?> derivedColumnsMapObj)) {
				return;
			}
			derivedColumnsMap = (HashMap<String, Object>) derivedColumnsMapObj;
		}
		if (derivedColumnsMap.containsKey(relationAlias)) {
			return;
		}

		String bucketKeyToRename = null;
		Object bucketValue = null;
		for (Map.Entry<String, Object> entry : derivedColumnsMap.entrySet()) {
			String bucketKey = entry.getKey();
			Object value = entry.getValue();
			if (!(value instanceof Map<?, ?> bucketMapObj)) {
				continue;
			}
			if (!isRelationalModifierStructuredDerivedColumnBucket((Map<String, Object>) bucketMapObj)) {
				continue;
			}
			bucketKeyToRename = bucketKey;
			bucketValue = value;
			break;
		}

		if (bucketKeyToRename == null
				|| bucketValue == null
				|| relationAlias.equalsIgnoreCase(bucketKeyToRename)) {
			return;
		}

		derivedColumnsMap.remove(bucketKeyToRename);
		derivedColumnsMap.put(relationAlias, bucketValue);

		HashMap<String, Object> sourceColumnsMap = getRelationalModifierDerivationSubMapFromScope(
				walker.symbolTable,
				RELATIONAL_MODIFIER_SOURCE_COLUMNS_KEY);
		if (sourceColumnsMap != null && sourceColumnsMap.containsKey(bucketKeyToRename)) {
			Object sourceBucket = sourceColumnsMap.remove(bucketKeyToRename);
			sourceColumnsMap.put(relationAlias, sourceBucket);
		}
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> getScopeDerivedColumnsFromSymbolTable() {
		if (activeConvertEgressDerivedColumns != null && !activeConvertEgressDerivedColumns.isEmpty()) {
			return activeConvertEgressDerivedColumns;
		}
		HashMap<String, Object> derivedFromDerivation = getRelationalModifierDerivationSubMapFromScope(
				walker.symbolTable,
				RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY);
		if (derivedFromDerivation != null && !derivedFromDerivation.isEmpty()) {
			return derivedFromDerivation;
		}
		Object derivedObj = walker.symbolTable.get(RELATIONAL_MODIFIER_DERIVED_COLUMNS_KEY);
		if (derivedObj instanceof HashMap<?, ?> derivedMap && !derivedMap.isEmpty()) {
			return (HashMap<String, Object>) derivedMap;
		}
		return null;
	}

	private RelationalModifierConvertEgressContext getScopeRelationalModifierContextFromSymbolTable() {
		if (activeConvertEgressRelationalModifierContext != null
				&& !activeConvertEgressRelationalModifierContext.isEmpty()) {
			return activeConvertEgressRelationalModifierContext;
		}
		return peekRelationalModifierConvertContextFromScope(walker.symbolTable);
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

	/**
	 * Per-column lineage capture for scopes with exactly one {@code FROM} source (physical table,
	 * table function, or tuple substitution). Replaces late scope-exit bulk relocation (Phase 14 C2a):
	 * merges unresolved unqualified tokens into the sole source dictionary without consuming the
	 * unresolved map so finalize can still emit diagnostics for entries that remain locally ambiguous
	 * or unresolved. {@link #consumeLocallyResolvedUnqualifiedBeforeScopePassUp} removes entries that
	 * resolve unambiguously in this scope before parent pass-up.
	 */
	private void materializeUnqualifiedLineageForSingleSourceScopeAtConvertExit(
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> tableCollection,
			HashMap<String, Object> localDerivedColumns,
			RelationalModifierConvertEgressContext relationalModifierContext) {
		if (unresolvedColumnMap == null || unresolvedColumnMap.isEmpty()
				|| tableCollection == null || tableCollection.size() != 1) {
			return;
		}

		String onlySourceRef = normalizeTableRef(tableCollection.keySet().iterator().next());
		if (onlySourceRef == null || onlySourceRef.isBlank()) {
			return;
		}

		for (String columnKey : new ArrayList<String>(unresolvedColumnMap.keySet())) {
			if (columnKey == null || columnKey.isBlank() || columnKey.contains(".")) {
				continue;
			}

			if (isAmbiguousUnqualifiedStructuredDerivedColumn(
					columnKey,
					null,
					localDerivedColumns)) {
				continue;
			}

			if (isRelationalModifierDerivedColumnReference(
					localDerivedColumns,
					relationalModifierContext,
					null,
					columnKey)
					|| structuredContextDefinesPivotDerivedOutputColumn(
							relationalModifierContext,
							columnKey)) {
				consumeUnqualifiedUnknownEntry(unresolvedColumnMap, columnKey);
				continue;
			}

			Object unresolvedEntry = unresolvedColumnMap.get(columnKey);
			Object tokenPayload = coalesceMaterializationRefTokens(unresolvedEntry, null);
			if (tokenPayload == null) {
				continue;
			}

			mergeSourceLineageIntoPhysicalTableDictionary(
					tableCollection,
					onlySourceRef,
					columnKey,
					tokenPayload);
		}
	}

	/**
	 * Before unresolved unqualified columns are passed to a parent scope, bind and consume any that
	 * {@link #resolveUnqualifiedColumnAgainstVisibleScope} can resolve unambiguously against this
	 * scope's own {@code FROM} sources. Used only on pass-up / statement-defer egress paths so
	 * top-level scopes can still emit {@code UNRESOLVED_UNQUALIFIED_COLUMNS} when interface binding
	 * remains open. Avoids parent-scope false ambiguities without blind single-table bulk relocation.
	 */
	@SuppressWarnings("unchecked")
	private void consumeLocallyResolvedUnqualifiedBeforeScopePassUp(
			HashMap<String, Object> unqualifiedUnresolvedForLocal,
			Map<String, Object> scopeSymbols,
			HashMap<String, Object> scopeDerivedColumns,
			RelationalModifierConvertEgressContext scopeRelationalModifierContext) {
		if (unqualifiedUnresolvedForLocal == null || unqualifiedUnresolvedForLocal.isEmpty()
				|| scopeSymbols == null) {
			return;
		}

		Object tableCollectionObj = scopeSymbols.get(MUMBLE_TABLE_DICTIONARY_KEY);
		if (!(tableCollectionObj instanceof HashMap<?, ?>)) {
			return;
		}
		HashMap<String, Object> localTableCollection = (HashMap<String, Object>) tableCollectionObj;

		HashMap<String, Object> localTableAliasMap =
				(scopeSymbols.get(MUMBLE_TABLE_ALIAS_KEY) instanceof HashMap<?, ?>)
						? (HashMap<String, Object>) scopeSymbols.get(MUMBLE_TABLE_ALIAS_KEY)
						: new HashMap<String, Object>();
		HashMap<String, Object> localCurrentQueryDictionary =
				(scopeSymbols.get(MUMBLE_QUERY_DICTIONARY_KEY) instanceof HashMap<?, ?>)
						? (HashMap<String, Object>) scopeSymbols.get(MUMBLE_QUERY_DICTIONARY_KEY)
						: new HashMap<String, Object>();
		HashMap<String, Object> localInterface =
				(scopeSymbols.get(MUMBLE_INTERFACE_KEY) instanceof HashMap<?, ?>)
						? (HashMap<String, Object>) scopeSymbols.get(MUMBLE_INTERFACE_KEY)
						: new HashMap<String, Object>();

		HashMap<String, Object> localPhysicalFromTables =
				buildLocalPhysicalFromTableCollection(localTableCollection);
		HashMap<String, Object> visibleQuerySourceCollection =
				collectVisibleQuerySourceCollection(localTableAliasMap);

		for (String unresolvedKey : new ArrayList<String>(unqualifiedUnresolvedForLocal.keySet())) {
			Object unresolvedEntry = unqualifiedUnresolvedForLocal.get(unresolvedKey);
			String columnName = extractColumnNameFromUnresolvedEntry(unresolvedKey, unresolvedEntry);
			if (columnName == null || columnName.isBlank()) {
				continue;
			}
			if (extractTableRefFromUnresolvedEntry(unresolvedKey, unresolvedEntry) != null) {
				continue;
			}

			// Bare-value egress site 5: prune before parent pass-up (see class javadoc contract).
			if (tryConsumeBareValueExpressionUnknown(
					unqualifiedUnresolvedForLocal,
					null,
					columnName,
					unresolvedEntry)) {
				continue;
			}

			UnqualifiedScopeResolutionResult resolutionResult =
					resolveUnqualifiedColumnAgainstVisibleScope(
							columnName,
							localPhysicalFromTables,
							localTableCollection,
							visibleQuerySourceCollection,
							localTableAliasMap,
							null,
							true,
							false,
							scopeRelationalModifierContext,
							scopeDerivedColumns,
							null,
							true);
			if (resolutionResult.status == UnqualifiedScopeResolutionStatus.RESOLVED_DERIVED_COLUMN
					|| resolutionResult.status == UnqualifiedScopeResolutionStatus.RESOLVED_UNPIVOT_VALUE
					|| resolutionResult.status == UnqualifiedScopeResolutionStatus.RESOLVED_UNPIVOT_FOR) {
				continue;
			}
			if (resolutionResult.status != UnqualifiedScopeResolutionStatus.RESOLVED) {
				continue;
			}

			materializeResolvedUnqualifiedReference(
					unqualifiedUnresolvedForLocal,
					localTableCollection,
					localTableAliasMap,
					localCurrentQueryDictionary,
					localInterface,
					visibleQuerySourceCollection,
					null,
					resolutionResult.resolvedSourceRef,
					columnName,
					unresolvedEntry,
					true);
		}
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
		updatedRefMap.remove("locations");
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
			RelationalModifierConvertEgressContext relationalModifierContext) {
		if (tableRef == null || tableRef.isBlank()
				|| columnName == null || columnName.isBlank()) {
			return QualifiedScopeResolutionResult.unresolvedPhysicalSource(null, tableRef);
		}

		if (isNonTupleSubstitutionReference(tableRef)) {
			return QualifiedScopeResolutionResult.unresolvedPhysicalSource(null, tableRef);
		}

		UnpivotBinding unpivotBinding = resolveUnpivotBindingAtConvertEgress(
				columnName,
				tableRef,
				relationalModifierContext,
				buildLocalPhysicalFromTableCollection(visibleTableCollection),
				visibleAliasMap,
				visibleAliasMap);
		if (unpivotBinding != null && unpivotBinding.kind == UnpivotBindingKind.IN_SOURCE) {
			return QualifiedScopeResolutionResult.resolvedUnpivotInSource(
					unpivotBinding.materializeTableRef,
					tableRef);
		}
		if (localDerivedColumns != null && unpivotBinding != null) {
			return switch (unpivotBinding.kind) {
				case VALUE -> QualifiedScopeResolutionResult.resolvedUnpivotValue(tableRef);
				case FOR -> QualifiedScopeResolutionResult.resolvedUnpivotFor(tableRef);
				case IN_SOURCE -> QualifiedScopeResolutionResult.resolvedUnpivotInSource(
						unpivotBinding.materializeTableRef,
						tableRef);
			};
		}

		if (isRelationalModifierDerivedColumnReference(
				localDerivedColumns,
				relationalModifierContext,
				tableRef,
				columnName,
				visibleAliasMap)) {
			return QualifiedScopeResolutionResult.resolvedDerivedColumn(tableRef);
		}

		HashMap<String, Object> localPhysicalTableCollection =
				buildLocalPhysicalFromTableCollection(visibleTableCollection);
		PivotOperandBinding pivotOperandBinding = resolvePivotOperandBindingAtConvertEgress(
				columnName,
				tableRef,
				relationalModifierContext,
				localPhysicalTableCollection,
				visibleAliasMap,
				visibleAliasMap);
		if (pivotOperandBinding != null) {
			return QualifiedScopeResolutionResult.resolvedPivotOperand(
					pivotOperandBinding.materializeTableRef,
					tableRef);
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
						getScopeRelationalModifierContextFromSymbolTable());
		if (resolutionResult.status == QualifiedScopeResolutionStatus.RESOLVED_DERIVED_COLUMN
				|| resolutionResult.status == QualifiedScopeResolutionStatus.RESOLVED_UNPIVOT_VALUE
				|| resolutionResult.status == QualifiedScopeResolutionStatus.RESOLVED_UNPIVOT_FOR) {
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
	 * Phase 15.4: shared derived-column proof for convert egress (clause probe, interface loop,
	 * {@code resolveRemaining…}). UPDATE RHS also treats structured PIVOT registry keys as derived
	 * via {@link #structuredContextDefinesPivotDerivedOutputColumn}.
	 */
	private boolean isConvertEgressDerivedColumnReference(
			String columnName,
			String tableRef,
			ConvertEgressResolutionContext ctx) {
		if (ctx.treatDerivedRegistryKeysAsDerivedColumn
				&& isRelationalModifierDerivedColumnReference(
						ctx.localDerivedColumns,
						ctx.relationalModifierContext,
						tableRef,
						columnName)) {
			return true;
		}
		return ctx.clauseProbeKey != null
				&& UPDATE_ASSIGNMENT_RHS_CLAUSE_PROBE_KEY.equals(ctx.clauseProbeKey)
				&& structuredContextDefinesPivotDerivedOutputColumn(
						ctx.relationalModifierContext,
						columnName);
	}

	/**
	 * Phase 17.7.5b.2: read-only convert-egress classification — derived, expanded lineage,
	 * qualified, or unqualified. No map mutation; callers consume / materialize / diagnose.
	 */
	private ConvertEgressColumnResolutionResult classifyColumnRefAtConvertEgress(
			String columnName,
			String tableRef,
			ConvertEgressResolutionContext ctx) {
		boolean qualifiedShape = tableRef != null && !tableRef.isBlank() && !"*".equals(tableRef);
		if (!qualifiedShape
				&& ctx.localDerivedColumns != null
				&& !ctx.localDerivedColumns.isEmpty()) {
			UnqualifiedScopeResolutionResult crossNamespace =
					tryResolveDerivedVersusRegularColumnNamespaceAmbiguity(
							columnName,
							tableRef,
							ctx.localPhysicalTableCollection,
							ctx.localTableCollection,
							ctx.visibleQuerySourceCollection,
							ctx.localTableAliasMap,
							ctx.allowQuerySourceFallback,
							ctx.localDerivedColumns);
			if (crossNamespace != null) {
				return ConvertEgressColumnResolutionResult.fromUnqualified(crossNamespace);
			}
			ArrayList<String> ambiguousDerivedBuckets = collectRelationalModifierStructuredDerivedColumnBucketKeys(
					columnName,
					ctx.localDerivedColumns);
			if (ambiguousDerivedBuckets.size() >= 2) {
				return ConvertEgressColumnResolutionResult.fromUnqualified(
						UnqualifiedScopeResolutionResult.ambiguousDerivedColumn(
								formatRelationalModifierDerivedColumnAmbiguitySources(
										ambiguousDerivedBuckets)));
			}
		}

		if (!qualifiedShape
				&& ctx.localSourceColumnsByBucket != null
				&& !ctx.localSourceColumnsByBucket.isEmpty()) {
			ArrayList<String> ambiguousSourceOperandBuckets =
					collectRelationalModifierSourceOperandBucketKeys(
							columnName,
							ctx.localSourceColumnsByBucket);
			if (ambiguousSourceOperandBuckets.size() >= 2) {
				return ConvertEgressColumnResolutionResult.fromUnqualified(
						UnqualifiedScopeResolutionResult.ambiguous(
								formatRelationalModifierDerivedColumnAmbiguitySources(
										ambiguousSourceOperandBuckets)));
			}
		}

		ArrayList<Object> expandedDerivedSourceLineage =
				tryExpandRelationalModifierDerivedColumnSourceLineageAtConvertEgress(columnName, tableRef, ctx);
		if (expandedDerivedSourceLineage != null) {
			return ConvertEgressColumnResolutionResult.fromExpandedDerivedSourceLineage(
					expandedDerivedSourceLineage);
		}

		if (isConvertEgressDerivedColumnReference(columnName, tableRef, ctx)) {
			return ConvertEgressColumnResolutionResult.derivedColumn();
		}

		if (qualifiedShape && ctx.resolveQualifiedWhenTableRefPresent) {
			HashMap<String, Object> aliasMap = ctx.effectiveAliasMap != null
					? ctx.effectiveAliasMap
					: ctx.localTableAliasMap;
			HashMap<String, Object> tableCollection = ctx.effectiveTableCollection != null
					? ctx.effectiveTableCollection
					: ctx.localTableCollection;
			return ConvertEgressColumnResolutionResult.fromQualified(
					resolveQualifiedColumnAgainstVisibleScope(
							tableRef,
							columnName,
							aliasMap,
							tableCollection,
							ctx.visibleQuerySourceCollection,
							ctx.deferUnresolvedQualifiedPhysicalSources,
							ctx.localDerivedColumns,
							ctx.relationalModifierContext));
		}

		return ConvertEgressColumnResolutionResult.fromUnqualified(
				resolveUnqualifiedColumnAgainstVisibleScope(
						columnName,
						ctx.localPhysicalTableCollection,
						ctx.localTableCollection,
						ctx.visibleQuerySourceCollection,
						ctx.localTableAliasMap,
						ctx.deleteTargetTableRef,
						ctx.allowQuerySourceFallback,
						ctx.deferWhenQueryAliasOnlyWithoutParentFatal,
						ctx.relationalModifierContext,
						ctx.localDerivedColumns,
						ctx.localSourceColumnsByBucket,
						ctx.treatDerivedRegistryKeysAsDerivedColumn));
	}

	/** @see #classifyColumnRefAtConvertEgress */
	private ConvertEgressColumnResolutionResult resolveColumnRefAtConvertEgress(
			String columnName,
			String tableRef,
			ConvertEgressResolutionContext ctx) {
		return classifyColumnRefAtConvertEgress(columnName, tableRef, ctx);
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
			RelationalModifierConvertEgressContext relationalModifierContext,
			HashMap<String, Object> localDerivedColumns,
			HashMap<String, Object> localSourceColumnsByBucket,
			boolean treatDerivedRegistryKeysAsDerivedColumn) {
		UnqualifiedScopeResolutionResult crossNamespace = tryResolveDerivedVersusRegularColumnNamespaceAmbiguity(
				columnName,
				null,
				localPhysicalTableCollection,
				localTableCollection,
				visibleQuerySourceCollection,
				localTableAliasMap,
				allowQuerySourceFallback,
				localDerivedColumns);
		if (crossNamespace != null) {
			return crossNamespace;
		}

		ArrayList<String> ambiguousDerivedBuckets = collectRelationalModifierStructuredDerivedColumnBucketKeys(
				columnName,
				localDerivedColumns);
		if (ambiguousDerivedBuckets.size() >= 2) {
			return UnqualifiedScopeResolutionResult.ambiguousDerivedColumn(
					formatRelationalModifierDerivedColumnAmbiguitySources(ambiguousDerivedBuckets));
		}

		if (localSourceColumnsByBucket != null && !localSourceColumnsByBucket.isEmpty()) {
			ArrayList<String> ambiguousSourceOperandBuckets =
					collectRelationalModifierSourceOperandBucketKeys(
							columnName,
							localSourceColumnsByBucket);
			if (ambiguousSourceOperandBuckets.size() >= 2) {
				return UnqualifiedScopeResolutionResult.ambiguous(
						formatRelationalModifierDerivedColumnAmbiguitySources(
								ambiguousSourceOperandBuckets));
			}
		}

		if (structuredContextDefinesPivotDerivedOutputColumn(
				relationalModifierContext,
				columnName)) {
			return UnqualifiedScopeResolutionResult.resolvedDerivedColumn();
		}

		UnpivotBinding unpivotBinding = resolveUnpivotBindingAtConvertEgress(
				columnName,
				null,
				relationalModifierContext,
				localPhysicalTableCollection,
				localTableAliasMap,
				localTableAliasMap);
		if (unpivotBinding != null && unpivotBinding.kind == UnpivotBindingKind.IN_SOURCE) {
			return UnqualifiedScopeResolutionResult.resolvedUnpivotInSource(
					unpivotBinding.materializeTableRef);
		}

		if (treatDerivedRegistryKeysAsDerivedColumn) {
			if (unpivotBinding != null) {
				return switch (unpivotBinding.kind) {
					case VALUE -> UnqualifiedScopeResolutionResult.resolvedUnpivotValue();
					case FOR -> UnqualifiedScopeResolutionResult.resolvedUnpivotFor();
					case IN_SOURCE -> UnqualifiedScopeResolutionResult.resolvedUnpivotInSource(
							unpivotBinding.materializeTableRef);
				};
			}
			if (containsKeyIgnoreCase(localDerivedColumns, columnName)) {
				return UnqualifiedScopeResolutionResult.resolvedDerivedColumn();
			}
		}

		PivotOperandBinding pivotOperandBinding = resolvePivotOperandBindingAtConvertEgress(
				columnName,
				null,
				relationalModifierContext,
				localPhysicalTableCollection,
				localTableAliasMap,
				localTableAliasMap);
		if (pivotOperandBinding != null) {
			return UnqualifiedScopeResolutionResult.resolvedPivotOperand(
					pivotOperandBinding.materializeTableRef);
		}

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
			String unpivotModifierTableRef = resolveUnpivotHintModifierTableRef(
					columnName,
					relationalModifierContext);
			if (unpivotModifierTableRef != null && !unpivotModifierTableRef.isBlank()) {
				return UnqualifiedScopeResolutionResult.resolved(normalizeTableRef(unpivotModifierTableRef));
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
				&& !hasOnlyQueryBackedAliasSources(localTableAliasMap)) {
			Iterable<String> fallbackQueryRefs;
			if (activeConvertEgressScopeBundle != null) {
				// Phase 19.5: prefer the convert-egress publish snapshot over a raw global-map scan.
				fallbackQueryRefs = activeConvertEgressScopeBundle.globalQueryDictionaryRefs.keySet();
			} else if (walker.queryColumnDictionaryMap != null) {
				fallbackQueryRefs = walker.queryColumnDictionaryMap.keySet();
			} else {
				fallbackQueryRefs = null;
			}
			if (fallbackQueryRefs != null) {
				for (String queryRef : fallbackQueryRefs) {
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

	private void emitAmbiguousUnqualifiedColumnDiagnosticIfNew(
			String columnName,
			Integer[] refLocation,
			String possibleSources,
			HashSet<String> emittedDiagnosticLocations) {
		String locationKey = formatAmbiguousDerivedDiagnosticLocationKey(refLocation);
		if (locationKey == null) {
			return;
		}
		if (emittedDiagnosticLocations != null && !emittedDiagnosticLocations.add(locationKey)) {
			return;
		}
		emitAmbiguousUnqualifiedColumnDiagnostic(columnName, refLocation, possibleSources);
	}

	private void emitAmbiguousUnqualifiedColumnDiagnosticsFromUnresolvedLocations(
			String columnName,
			HashMap<String, Object> localUnresolvedColumnMap,
			String possibleSources,
			Integer[] excludedLocation,
			HashSet<String> emittedDiagnosticLocations) {
		Object unresolvedEntry = getUnqualifiedUnknownEntry(localUnresolvedColumnMap, columnName);
		if (!(unresolvedEntry instanceof Map<?, ?> unresolvedMap)) {
			return;
		}
		Object locationsObj = unresolvedMap.get("locations");
		if (!(locationsObj instanceof ArrayList<?> locations) || locations.isEmpty()) {
			return;
		}
		for (Object locationObj : locations) {
			if (locationObj == null) {
				continue;
			}
			Integer[] location = walker.parseLineAndCharacterFromToken(locationObj.toString());
			if (location == null || location.length < 2 || location[0] == null) {
				continue;
			}
			if (excludedLocation != null
					&& excludedLocation.length >= 2
					&& excludedLocation[0] != null
					&& excludedLocation[0].equals(location[0])
					&& Objects.equals(excludedLocation[1], location[1])) {
				continue;
			}
			emitAmbiguousUnqualifiedColumnDiagnosticIfNew(
					columnName,
					location,
					possibleSources,
					emittedDiagnosticLocations);
		}
	}

	private void emitAmbiguousDerivedColumnReferenceFatal(
			String columnName,
			Integer[] refLocation,
			String possibleModifierSources) {
		String diagCode = walker.getDiagnosticCode(
				SqlASTWalkerHelper.DIAG_SQL_AMBIGUOUS_DERIVED_COLUMN_REFERENCE);
		String diagTemplate = walker.getDiagnosticMessage(
				SqlASTWalkerHelper.DIAG_SQL_AMBIGUOUS_DERIVED_COLUMN_REFERENCE);
		String diagMessage = (diagTemplate == null)
				? String.format(
						"Ambiguous derived column reference '%s' at (l:%s c:%s). Possible sources: %s",
						columnName,
						refLocation[0],
						refLocation[1],
						possibleModifierSources)
				: String.format(diagTemplate,
						columnName,
						refLocation[0],
						refLocation[1],
						possibleModifierSources);

		walker.addWalkerFatal(
				diagCode,
				diagMessage,
				refLocation[0],
				refLocation[1],
				columnName);
	}

	private static String formatAmbiguousDerivedDiagnosticLocationKey(Integer[] refLocation) {
		if (refLocation == null || refLocation.length < 2 || refLocation[0] == null) {
			return null;
		}
		return refLocation[0] + ":" + refLocation[1];
	}

	private void emitAmbiguousDerivedColumnReferenceFatalIfNew(
			String columnName,
			Integer[] refLocation,
			String possibleModifierSources,
			HashSet<String> emittedDiagnosticLocations) {
		String locationKey = formatAmbiguousDerivedDiagnosticLocationKey(refLocation);
		if (locationKey == null) {
			return;
		}
		if (emittedDiagnosticLocations != null && !emittedDiagnosticLocations.add(locationKey)) {
			return;
		}
		emitAmbiguousDerivedColumnReferenceFatal(columnName, refLocation, possibleModifierSources);
	}

	private void emitAmbiguousDerivedColumnReferenceFatalsFromUnresolvedLocations(
			String columnName,
			HashMap<String, Object> localUnresolvedColumnMap,
			String possibleModifierSources,
			Integer[] excludedLocation,
			HashSet<String> emittedDiagnosticLocations) {
		Object unresolvedEntry = getUnqualifiedUnknownEntry(localUnresolvedColumnMap, columnName);
		if (!(unresolvedEntry instanceof Map<?, ?> unresolvedMap)) {
			return;
		}
		Object locationsObj = unresolvedMap.get("locations");
		if (!(locationsObj instanceof ArrayList<?> locations) || locations.isEmpty()) {
			return;
		}
		for (Object locationObj : locations) {
			if (locationObj == null) {
				continue;
			}
			Integer[] location = walker.parseLineAndCharacterFromToken(locationObj.toString());
			if (location == null || location.length < 2 || location[0] == null) {
				continue;
			}
			if (excludedLocation != null
					&& excludedLocation.length >= 2
					&& excludedLocation[0] != null
					&& excludedLocation[0].equals(location[0])
					&& Objects.equals(excludedLocation[1], location[1])) {
				continue;
			}
			emitAmbiguousDerivedColumnReferenceFatalIfNew(
					columnName,
					location,
					possibleModifierSources,
					emittedDiagnosticLocations);
		}
	}

	private int countUnqualifiedInterfaceRefsForColumnName(
			HashMap<String, Object> localInterface,
			String columnName) {
		if (localInterface == null || localInterface.isEmpty() || columnName == null || columnName.isBlank()) {
			return 0;
		}
		int count = 0;
		for (Object refsObj : localInterface.values()) {
			if (!(refsObj instanceof ArrayList<?> refs)) {
				continue;
			}
			for (Object refObj : refs) {
				String refColumnName = walker.extractReferenceNameFromInterfaceEntry(refObj);
				String refTableRef = walker.extractReferenceTableRefFromInterfaceEntry(refObj);
				if (columnName.equalsIgnoreCase(refColumnName) && isUnqualifiedColumnRef(refTableRef)) {
					count++;
				}
			}
		}
		return count;
	}

	private int countUnresolvedColumnRefSiteLocations(
			HashMap<String, Object> localUnresolvedColumnMap,
			String columnName) {
		Object unresolvedEntry = getUnqualifiedUnknownEntry(localUnresolvedColumnMap, columnName);
		if (!(unresolvedEntry instanceof Map<?, ?> unresolvedMap)) {
			return 0;
		}
		Object locationsObj = unresolvedMap.get("locations");
		if (!(locationsObj instanceof ArrayList<?> locations)) {
			return 0;
		}
		int count = 0;
		for (Object locationObj : locations) {
			if (locationObj != null) {
				count++;
			}
		}
		return count;
	}

	private Integer[] resolveAmbiguousDerivedColumnRefSiteLocation(
			Object refObj,
			String columnName,
			HashMap<String, Object> localUnresolvedColumnMap,
			HashMap<String, Object> localCurrentQueryDictionary,
			String interfaceDictionaryKey,
			HashSet<String> emittedDiagnosticLocations) {
		Integer[] refLocation = walker.getLineAndCharacterFromEntry(refObj);
		if (refLocation != null && refLocation.length >= 2 && refLocation[0] != null) {
			return refLocation;
		}
		if (refObj instanceof Map<?, ?> refMap) {
			Object columnObj = refMap.get(MUMBLE_COLUMN_KEY);
			refLocation = walker.getLineAndCharacterFromEntry(columnObj);
			if (refLocation != null && refLocation.length >= 2 && refLocation[0] != null) {
				return refLocation;
			}
		}
		if (interfaceDictionaryKey != null
				&& localCurrentQueryDictionary != null
				&& localCurrentQueryDictionary.containsKey(interfaceDictionaryKey)) {
			refLocation = walker.getLineAndCharacterFromEntry(
					localCurrentQueryDictionary.get(interfaceDictionaryKey));
			if (refLocation != null && refLocation.length >= 2 && refLocation[0] != null) {
				return refLocation;
			}
		}
		Object unresolvedEntry = getUnqualifiedUnknownEntry(localUnresolvedColumnMap, columnName);
		if (!(unresolvedEntry instanceof Map<?, ?> unresolvedMap)) {
			return new Integer[] { null, null };
		}
		Object locationsObj = unresolvedMap.get("locations");
		if (!(locationsObj instanceof ArrayList<?> locations)) {
			return new Integer[] { null, null };
		}
		for (Object locationObj : locations) {
			if (locationObj == null) {
				continue;
			}
			Integer[] location = walker.parseLineAndCharacterFromToken(locationObj.toString());
			String locationKey = formatAmbiguousDerivedDiagnosticLocationKey(location);
			if (locationKey == null) {
				continue;
			}
			if (emittedDiagnosticLocations != null && emittedDiagnosticLocations.contains(locationKey)) {
				continue;
			}
			return location;
		}
		return new Integer[] { null, null };
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
			case RESOLVED_DERIVED_COLUMN, RESOLVED_UNPIVOT_VALUE, RESOLVED_UNPIVOT_FOR,
					RESOLVED_PIVOT_OPERAND, RESOLVED_UNPIVOT_IN_SOURCE -> {
				// Operand / derived statuses materialized at convert egress before this helper (M5).
			}
			case RESOLVED -> {
				if (consumeSelectListOutputAliasUnresolvedEntry(
						columnName,
						unresolvedColumnMap,
						localCurrentQueryDictionary,
						localInterface,
						localTableCollection,
						unresolvedEntry,
						extractTableRefFromUnresolvedEntry(null, unresolvedEntry),
						clauseKey)) {
					if (clauseLocations != null
							&& !clauseLocations.isEmpty()
							&& activeConvertEgressCurrentQueryScopeKey != null
							&& !activeConvertEgressCurrentQueryScopeKey.isBlank()) {
						updateTrackedClauseLocationsWithResolvedTableRef(
								clauseLocations,
								activeConvertEgressCurrentQueryScopeKey);
					}
					return;
				}
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
							columnName,
							unresolvedEntry);
				}
			}
			case AMBIGUOUS -> {
				if (consumeSelectListOutputAliasUnresolvedEntry(
						columnName,
						unresolvedColumnMap,
						localCurrentQueryDictionary,
						localInterface,
						localTableCollection,
						unresolvedEntry,
						extractTableRefFromUnresolvedEntry(null, unresolvedEntry),
						clauseKey)) {
					if (clauseLocations != null
							&& !clauseLocations.isEmpty()
							&& activeConvertEgressCurrentQueryScopeKey != null
							&& !activeConvertEgressCurrentQueryScopeKey.isBlank()) {
						updateTrackedClauseLocationsWithResolvedTableRef(
								clauseLocations,
								activeConvertEgressCurrentQueryScopeKey);
					}
					return;
				}
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
			case AMBIGUOUS_DERIVED_COLUMN -> {
				Integer[] diagnosticLocation = refLocation;
				if (diagnosticLocation == null || diagnosticLocation.length < 2 || diagnosticLocation[0] == null) {
					diagnosticLocation = walker.getLineAndCharacterFromEntry(unresolvedEntry);
				}
				if (diagnosticLocation == null || diagnosticLocation.length < 2 || diagnosticLocation[0] == null) {
					diagnosticLocation = new Integer[] { null, null };
				}
				emitAmbiguousDerivedColumnReferenceFatal(
						columnName,
						diagnosticLocation,
						result.ambiguousSourcesLabel);
			}
			case AMBIGUOUS_DERIVED_AND_REGULAR_COLUMN -> {
				Integer[] diagnosticLocation = refLocation;
				if (diagnosticLocation == null || diagnosticLocation.length < 2 || diagnosticLocation[0] == null) {
					diagnosticLocation = walker.getLineAndCharacterFromEntry(unresolvedEntry);
				}
				if (diagnosticLocation == null || diagnosticLocation.length < 2 || diagnosticLocation[0] == null) {
					diagnosticLocation = new Integer[] { null, null };
				}
				emitAmbiguousDerivedAndRegularColumnReferenceFatal(
						columnName,
						diagnosticLocation,
						parseDerivedVersusRegularAmbiguityDerivedSources(result.ambiguousSourcesLabel),
						parseDerivedVersusRegularAmbiguityRegularSources(result.ambiguousSourcesLabel));
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
			String deleteTargetTableRef,
			HashMap<String, Object> localDerivedColumns,
			RelationalModifierConvertEgressContext relationalModifierContext) {
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

			if (isAmbiguousUnqualifiedStructuredDerivedColumn(columnName, null, localDerivedColumns)) {
				continue;
			}

			// Later clauses (WHERE, HAVING, QUALIFY, GROUP BY, ORDER BY, …) may reference current-query
			// SELECT-list output aliases (arithmetic, functions, aggregates, window). Consume against
			// local interface before FROM-source resolution — especially when FROM is query-backed only.
			if (consumeSelectListOutputAliasUnresolvedEntry(
					columnName,
					unresolvedColumnMap,
					localCurrentQueryDictionary,
					localInterface,
					localTableCollection,
					unresolvedEntry,
					tableRef,
					null)) {
				continue;
			}

			HashMap<String, Object> localPhysicalTableCollection =
					buildLocalPhysicalFromTableCollection(localTableCollection);
			Set<ClauseRefLocation> clauseLocations = (unresolvedColumnLocations != null)
					? unresolvedColumnLocations.get(columnName)
					: null;

			ConvertEgressResolutionContext remainingIngressCtx = new ConvertEgressResolutionContext(
					localDerivedColumns,
					activeConvertEgressRelationalModifierSourceColumns,
					relationalModifierContext,
					localPhysicalTableCollection,
					localTableCollection,
					visibleQuerySourceCollection,
					localTableAliasMap,
					null,
					null,
					deleteTargetTableRef,
					null,
					true,
					true,
					false,
					false,
					false,
					null);
			ConvertEgressColumnResolutionResult egressResult =
					resolveColumnRefAtConvertEgress(columnName, null, remainingIngressCtx);
			if (egressResult.isDerivedColumn()) {
				continue;
			}
			if (egressResult.isPivotOperandColumn()) {
				applyConvertEgressPivotOperandMaterialization(
						egressResult,
						columnName,
						null,
						unresolvedColumnMap,
						localTableCollection,
						relationalModifierContext,
						localTableAliasMap);
				continue;
			}
			if (egressResult.isUnpivotInSourceColumn()) {
				applyConvertEgressUnpivotInSourceMaterialization(
						egressResult,
						columnName,
						null,
						unresolvedColumnMap,
						localTableCollection,
						relationalModifierContext,
						localTableAliasMap);
				continue;
			}

			UnqualifiedScopeResolutionResult resolutionResult = egressResult.unqualified();
			if (resolutionResult == null) {
				continue;
			}

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
		RESOLVED_INTRA_QUERY_OUTPUT_ALIAS,
		DEFERRED,
		AMBIGUOUS,
		AMBIGUOUS_DERIVED_COLUMN,
		UNRESOLVED,
		EXPANDED_DERIVED_SOURCE_LINEAGE
	}

	private static final class ArchivedClauseColumnRefResult {
		final ArchivedClauseColumnRefDisposition disposition;
		final String resolvedSourceRef;
		final String ambiguousSourcesLabel;
		final ArrayList<Object> expandedDerivedSourceLineage;

		private ArchivedClauseColumnRefResult(
				ArchivedClauseColumnRefDisposition disposition,
				String resolvedSourceRef,
				String ambiguousSourcesLabel,
				ArrayList<Object> expandedDerivedSourceLineage) {
			this.disposition = disposition;
			this.resolvedSourceRef = resolvedSourceRef;
			this.ambiguousSourcesLabel = ambiguousSourcesLabel;
			this.expandedDerivedSourceLineage = expandedDerivedSourceLineage;
		}

		static ArchivedClauseColumnRefResult skip() {
			return new ArchivedClauseColumnRefResult(
					ArchivedClauseColumnRefDisposition.SKIP, null, null, null);
		}

		static ArchivedClauseColumnRefResult resolved(String resolvedSourceRef) {
			return new ArchivedClauseColumnRefResult(
					ArchivedClauseColumnRefDisposition.RESOLVED,
					resolvedSourceRef,
					null,
					null);
		}

		static ArchivedClauseColumnRefResult resolvedIntraQueryOutputAlias(String queryScopeKey) {
			return new ArchivedClauseColumnRefResult(
					ArchivedClauseColumnRefDisposition.RESOLVED_INTRA_QUERY_OUTPUT_ALIAS,
					queryScopeKey,
					null,
					null);
		}

		static ArchivedClauseColumnRefResult deferred() {
			return new ArchivedClauseColumnRefResult(
					ArchivedClauseColumnRefDisposition.DEFERRED, null, null, null);
		}

		static ArchivedClauseColumnRefResult ambiguous(String ambiguousSourcesLabel) {
			return new ArchivedClauseColumnRefResult(
					ArchivedClauseColumnRefDisposition.AMBIGUOUS,
					null,
					ambiguousSourcesLabel,
					null);
		}

		static ArchivedClauseColumnRefResult ambiguousDerivedColumn(String ambiguousSourcesLabel) {
			return new ArchivedClauseColumnRefResult(
					ArchivedClauseColumnRefDisposition.AMBIGUOUS_DERIVED_COLUMN,
					null,
					ambiguousSourcesLabel,
					null);
		}

		static ArchivedClauseColumnRefResult unresolved() {
			return new ArchivedClauseColumnRefResult(
					ArchivedClauseColumnRefDisposition.UNRESOLVED, null, null, null);
		}

		static ArchivedClauseColumnRefResult expandedDerivedSourceLineage(
				ArrayList<Object> expandedDerivedSourceLineage) {
			return new ArchivedClauseColumnRefResult(
					ArchivedClauseColumnRefDisposition.EXPANDED_DERIVED_SOURCE_LINEAGE,
					null,
					null,
					expandedDerivedSourceLineage);
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
		final RelationalModifierConvertEgressContext relationalModifierContext;
		final String deleteTargetTableRef;
		final boolean deferCorrelatedSubqueries;
		final String currentQueryScopeKey;

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
				RelationalModifierConvertEgressContext relationalModifierContext,
				String deleteTargetTableRef,
				boolean deferCorrelatedSubqueries,
				String currentQueryScopeKey) {
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
			this.relationalModifierContext = relationalModifierContext;
			this.deleteTargetTableRef = deleteTargetTableRef;
			this.deferCorrelatedSubqueries = deferCorrelatedSubqueries;
			this.currentQueryScopeKey = currentQueryScopeKey;
		}
	}

	private static boolean isGroupOrOrderClauseKey(String clauseKey) {
		return MUMBLE_GROUPED_BY_KEY.equals(clauseKey)
				|| MUMBLE_ORDERED_BY_KEY.equals(clauseKey)
				|| MUMBLE_WINDOW_PARTITION_BY_KEY.equals(clauseKey)
				|| MUMBLE_WINDOW_ORDERED_BY_KEY.equals(clauseKey)
				|| MUMBLE_WITHIN_GROUP_ORDERED_BY_KEY.equals(clauseKey);
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

	private boolean isGroundedIntraQueryOutputAliasUsage(
			String columnName,
			String tableRef,
			HashMap<String, Object> localInterface,
			HashMap<String, Object> tableCollection) {
		if (!isIntraQueryOutputAliasUsage(columnName, tableRef, localInterface, tableCollection)) {
			return false;
		}
		return isGroundedInterfaceOutputAlias(localInterface, columnName, new HashSet<String>());
	}

	private boolean isGroundedInterfaceOutputAlias(
			HashMap<String, Object> localInterface,
			String outputColumnName,
			Set<String> activeOutputColumns) {
		if (!isInterfaceOutputAliasOnly(localInterface, outputColumnName)) {
			return false;
		}
		String matchedKey = findKeyIgnoreCase(localInterface, outputColumnName);
		if (matchedKey == null) {
			return true;
		}
		if (activeConvertEgressScalarSubqueryAliases != null
				&& activeConvertEgressScalarSubqueryAliases.stream()
						.anyMatch(alias -> alias != null && alias.equalsIgnoreCase(matchedKey))) {
			return true;
		}
		if (activeOutputColumns != null
				&& activeOutputColumns.stream().anyMatch(key -> key != null && key.equalsIgnoreCase(matchedKey))) {
			return false;
		}
		Set<String> nextActive = new HashSet<String>();
		if (activeOutputColumns != null) {
			nextActive.addAll(activeOutputColumns);
		}
		nextActive.add(matchedKey);

		Object refsObj = localInterface.get(matchedKey);
		if (!(refsObj instanceof ArrayList<?> refs) || refs.isEmpty()) {
			return true;
		}
		for (Object refObj : refs) {
			if (!isGroundedInterfaceDependencyRef(localInterface, refObj, nextActive)) {
				return false;
			}
		}
		return true;
	}

	private boolean isGroundedInterfaceDependencyRef(
			HashMap<String, Object> localInterface,
			Object refObj,
			Set<String> activeOutputColumns) {
		if (refObj instanceof Map<?, ?> refMap
				&& refMap.containsKey(MUMBLE_SUBSTITUTION_KEY)) {
			return true;
		}
		String substitutionType = walker.extractSubstitutionTypeFromInterfaceEntry(refObj);
		if (MUMBLE_COLUMN_KEY.equals(substitutionType)
				|| MUMBLE_PREDICAND_KEY.equals(substitutionType)) {
			return true;
		}
		String tableRef = walker.extractReferenceTableRefFromInterfaceEntry(refObj);
		if (tableRef != null && !tableRef.isBlank() && !MUMBLE_UNKNOWN_KEY.equals(tableRef)) {
			return true;
		}
		String dependencyName = walker.extractReferenceNameFromInterfaceEntry(refObj);
		if (dependencyName == null || dependencyName.isBlank()) {
			return false;
		}
		if (isInterfaceOutputAliasOnly(localInterface, dependencyName)) {
			return isGroundedInterfaceOutputAlias(localInterface, dependencyName, activeOutputColumns);
		}
		return SqlBareValueExpressionRegistry.classify(dependencyName) != null;
	}

	/**
	 * True when {@code dependencyOutputAliasName} is a SELECT-list output alias defined before
	 * {@code consumingInterfaceOutputColumnName} in source order.
	 */
	private boolean isPrecedingSelectListOutputAliasInInterface(
			String dependencyOutputAliasName,
			String consumingInterfaceOutputColumnName,
			HashMap<String, Object> localInterface,
			HashMap<String, Object> localCurrentQueryDictionary) {
		if (dependencyOutputAliasName == null
				|| dependencyOutputAliasName.isBlank()
				|| consumingInterfaceOutputColumnName == null
				|| consumingInterfaceOutputColumnName.isBlank()
				|| localInterface == null
				|| localInterface.isEmpty()
				|| !isInterfaceOutputAliasOnly(localInterface, dependencyOutputAliasName)) {
			return false;
		}
		if (activeConvertEgressSelectListOutputAliasSourceOrder != null
				&& !activeConvertEgressSelectListOutputAliasSourceOrder.isEmpty()) {
			int dependencyIndex = indexOfIgnoreCase(
					activeConvertEgressSelectListOutputAliasSourceOrder,
					dependencyOutputAliasName);
			int consumingIndex = indexOfIgnoreCase(
					activeConvertEgressSelectListOutputAliasSourceOrder,
					consumingInterfaceOutputColumnName);
			return dependencyIndex >= 0
					&& consumingIndex >= 0
					&& dependencyIndex < consumingIndex;
		}
		HashMap<String, Object> scopeStub = new HashMap<String, Object>();
		if (localCurrentQueryDictionary != null) {
			scopeStub.put(MUMBLE_QUERY_DICTIONARY_KEY, localCurrentQueryDictionary);
		}
		ArrayList<String> selectOrder = extractInterfaceColumnNamesInSelectTokenOrder(scopeStub, localInterface);
		if (selectOrder.isEmpty()) {
			return false;
		}
		int dependencyIndex = -1;
		int consumingIndex = -1;
		for (int index = 0; index < selectOrder.size(); index++) {
			String outputColumnName = selectOrder.get(index);
			if (dependencyIndex < 0
					&& outputColumnName != null
					&& outputColumnName.equalsIgnoreCase(dependencyOutputAliasName)) {
				dependencyIndex = index;
			}
			if (consumingIndex < 0
					&& outputColumnName != null
					&& outputColumnName.equalsIgnoreCase(consumingInterfaceOutputColumnName)) {
				consumingIndex = index;
			}
		}
		return dependencyIndex >= 0 && consumingIndex >= 0 && dependencyIndex < consumingIndex;
	}

	/**
	 * {@code interface} lineage for refs to preceding grounded SELECT-list output aliases:
	 * stamp {@code queryN}, not a physical {@code table_dictionary} source.
	 */
	private boolean tryStampGroundedOutputAliasInterfaceDependencyToQueryScope(
			ArrayList<Object> refs,
			int refIndex,
			Object refObj,
			String columnName,
			String consumingInterfaceOutputColumnName,
			HashMap<String, Object> localInterface,
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> localUnresolvedColumnMap,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> effectiveAliasMap,
			HashMap<String, Object> visibleQuerySourceCollection) {
		if (refs == null
				|| refIndex < 0
				|| refIndex >= refs.size()
				|| columnName == null
				|| columnName.isBlank()
				|| !isPrecedingSelectListOutputAliasInInterface(
						columnName,
						consumingInterfaceOutputColumnName,
						localInterface,
						localCurrentQueryDictionary)
				|| !isGroundedIntraQueryOutputAliasUsage(
						columnName,
						null,
						localInterface,
						localTableCollection)) {
			return false;
		}
		String queryScopeKey = activeConvertEgressCurrentQueryScopeKey;
		if (queryScopeKey == null || queryScopeKey.isBlank()) {
			return false;
		}
		refs.set(refIndex, cloneReferenceWithResolvedTableRef(refObj, queryScopeKey));
		ArchivedClauseProbeContext clauseProbeContext = new ArchivedClauseProbeContext(
				null,
				localInterface,
				localCurrentQueryDictionary,
				localUnresolvedColumnMap,
				null,
				localTableCollection,
				visibleQuerySourceCollection,
				effectiveAliasMap,
				null,
				null,
				null,
				null,
				null,
				false,
				queryScopeKey);
		recordInterfaceOutputClauseRefOnQueryDictionary(
				refObj,
				columnName,
				MUMBLE_INTERFACE_KEY,
				clauseProbeContext);
		consumeUnqualifiedUnknownEntry(localUnresolvedColumnMap, columnName);
		return true;
	}

	@SuppressWarnings("unchecked")
	private String extractUnresolvedIngressSite(Object unresolvedEntry) {
		if (!(unresolvedEntry instanceof Map<?, ?> entryMap)) {
			return null;
		}
		Object siteObj = ((Map<String, Object>) entryMap).get(MUMBLE_UNRESOLVED_INGRESS_SITE_KEY);
		return siteObj == null ? null : siteObj.toString();
	}

	private boolean isSelectListIngressUnresolvedEntry(Object unresolvedEntry) {
		return MUMBLE_UNRESOLVED_INGRESS_SITE_SELECT_LIST.equals(
				extractUnresolvedIngressSite(unresolvedEntry));
	}

	private boolean isClauseIngressUnresolvedEntry(Object unresolvedEntry) {
		return MUMBLE_UNRESOLVED_INGRESS_SITE_CLAUSE.equals(
				extractUnresolvedIngressSite(unresolvedEntry));
	}

	private boolean mayConsumeUnresolvedAsGroundedOutputAliasForLaterClause(
			String columnName,
			String tableRef,
			Object unresolvedEntry,
			HashMap<String, Object> localInterface,
			HashMap<String, Object> localTableCollection,
			String clauseKey) {
		if (isSelectListIngressUnresolvedEntry(unresolvedEntry)) {
			return false;
		}
		if (clauseKey == null && !isClauseIngressUnresolvedEntry(unresolvedEntry)) {
			return false;
		}
		return isGroundedIntraQueryOutputAliasUsage(
				columnName,
				tableRef,
				localInterface,
				localTableCollection);
	}

	/**
	 * SELECT-list output alias referenced from a later clause: record on {@code query_dictionary},
	 * consume unresolved ingress, and skip physical binding / ambiguity handling.
	 */
	private ArchivedClauseColumnRefResult trySkipSelectListOutputAliasArchivedClauseRef(
			Object refObj,
			String columnName,
			String tableRef,
			String clauseKey,
			ArchivedClauseProbeContext probeContext) {
		if (probeContext == null || columnName == null || columnName.isBlank()) {
			return null;
		}
		if ((MUMBLE_WINDOW_PARTITION_BY_KEY.equals(clauseKey)
						|| MUMBLE_WINDOW_ORDERED_BY_KEY.equals(clauseKey)
						|| MUMBLE_WITHIN_GROUP_ORDERED_BY_KEY.equals(clauseKey))
				&& isForwardWindowSelectListOutputAliasRef(
						columnName, clauseKey, probeContext)) {
			return null;
		}
		if (!isGroundedIntraQueryOutputAliasUsage(
				columnName,
				tableRef,
				probeContext.localInterface,
				probeContext.localTableCollection)) {
			return null;
		}
		recordInterfaceOutputClauseRefOnQueryDictionary(refObj, columnName, clauseKey, probeContext);
		consumeUnqualifiedUnknownEntry(probeContext.localUnresolvedColumnMap, columnName);
		String queryScopeKey = probeContext.currentQueryScopeKey;
		if (queryScopeKey != null && !queryScopeKey.isBlank()) {
			return ArchivedClauseColumnRefResult.resolvedIntraQueryOutputAlias(queryScopeKey);
		}
		return ArchivedClauseColumnRefResult.skip();
	}

	private boolean isForwardWindowSelectListOutputAliasRef(
			String columnName,
			String clauseKey,
			ArchivedClauseProbeContext probeContext) {
		if (probeContext == null || columnName == null || columnName.isBlank()) {
			return false;
		}
		if (!MUMBLE_WINDOW_PARTITION_BY_KEY.equals(clauseKey)
				&& !MUMBLE_WINDOW_ORDERED_BY_KEY.equals(clauseKey)
				&& !MUMBLE_WITHIN_GROUP_ORDERED_BY_KEY.equals(clauseKey)) {
			return false;
		}
		if (!isInterfaceOutputAliasOnly(probeContext.localInterface, columnName)
				|| windowOutputInterfaceClauseDepsByAlias.isEmpty()) {
			return false;
		}
		for (Map.Entry<String, WindowSelectInterfaceClauseDeps> entry :
				windowOutputInterfaceClauseDepsByAlias.entrySet()) {
			WindowSelectInterfaceClauseDeps overDeps = entry.getValue();
			if (overDeps == null
					|| !windowClauseDepsReferenceColumn(overDeps, columnName)) {
				continue;
			}
			String consumingInterfaceOutputColumnName = entry.getKey();
			if (consumingInterfaceOutputColumnName == null
					|| consumingInterfaceOutputColumnName.isBlank()) {
				continue;
			}
			return !isPrecedingSelectListOutputAliasInInterface(
					columnName,
					consumingInterfaceOutputColumnName,
					probeContext.localInterface,
					probeContext.localCurrentQueryDictionary);
		}
		return false;
	}

	private boolean windowClauseDepsReferenceColumn(
			WindowSelectInterfaceClauseDeps overDeps,
			String columnName) {
		return clauseRefListContainsColumnName(overDeps.partitionByRefs, columnName)
				|| clauseRefListContainsColumnName(overDeps.orderByRefs, columnName)
				|| clauseRefListContainsColumnName(overDeps.withinGroupOrderByRefs, columnName);
	}

	private boolean clauseRefListContainsColumnName(ArrayList<Object> refs, String columnName) {
		if (refs == null || columnName == null || columnName.isBlank()) {
			return false;
		}
		for (Object refObj : refs) {
			String refName = walker.extractReferenceNameFromInterfaceEntry(refObj);
			if (refName != null && refName.equalsIgnoreCase(columnName)) {
				return true;
			}
		}
		return false;
	}

	private boolean consumeSelectListOutputAliasUnresolvedEntry(
			String columnName,
			HashMap<String, Object> unresolvedColumnMap,
			HashMap<String, Object> localCurrentQueryDictionary,
			HashMap<String, Object> localInterface,
			HashMap<String, Object> localTableCollection,
			Object unresolvedEntry,
			String tableRef,
			String clauseKey) {
		if (!mayConsumeUnresolvedAsGroundedOutputAliasForLaterClause(
				columnName,
				tableRef,
				unresolvedEntry,
				localInterface,
				localTableCollection,
				clauseKey)) {
			return false;
		}
		Object tokenPayload = unresolvedEntry;
		if (tokenPayload == null && unresolvedColumnMap != null) {
			tokenPayload = getUnqualifiedUnknownEntry(unresolvedColumnMap, columnName);
		}
		if (unresolvedColumnMap != null) {
			consumeUnqualifiedUnknownEntry(unresolvedColumnMap, columnName);
		}
		if (localCurrentQueryDictionary != null && tokenPayload != null) {
			walker.mergeResolvedColumnIntoDictionary(
					localCurrentQueryDictionary,
					columnName,
					tokenPayload);
		}
		return true;
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
		if (isGroundedIntraQueryOutputAliasUsage(columnName, tableRef, localInterface, tableCollection)) {
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
			ArchivedClauseProbeContext probeContext,
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
			RelationalModifierConvertEgressContext relationalModifierContext,
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

		// Bare-value egress site 2: archived clause lists (WHERE, GROUP BY, ORDER BY, window buckets).
		if (skipBareValueExpressionAtConvertEgressRefSite(
				localUnresolvedColumnMap,
				tableRef,
				columnName,
				refObj)) {
			return ArchivedClauseColumnRefResult.skip();
		}

		ArchivedClauseColumnRefResult selectListAliasSkip = trySkipSelectListOutputAliasArchivedClauseRef(
				refObj,
				columnName,
				tableRef,
				clauseKey,
				probeContext);
		if (selectListAliasSkip != null) {
			return selectListAliasSkip;
		}

		if (isAmbiguousUnqualifiedStructuredDerivedColumn(
				columnName,
				tableRef,
				localDerivedColumns)) {
			return ArchivedClauseColumnRefResult.skip();
		}

		if (isDerivedVersusRegularColumnNamespaceAmbiguity(
				columnName,
				tableRef,
				buildLocalPhysicalFromTableCollection(localFromTableCollection),
				localTableCollection,
				visibleQuerySourceCollection,
				localTableAliasMap,
				true,
				localDerivedColumns)) {
			return ArchivedClauseColumnRefResult.skip();
		}

		if (isAmbiguousUnqualifiedRelationalModifierSourceOperandColumn(
				columnName,
				tableRef,
				activeConvertEgressRelationalModifierSourceColumns)) {
			return ArchivedClauseColumnRefResult.skip();
		}

		ConvertEgressResolutionContext clauseProbeCtx = new ConvertEgressResolutionContext(
				localDerivedColumns,
				activeConvertEgressRelationalModifierSourceColumns,
				relationalModifierContext,
				localFromTableCollection,
				localTableCollection,
				visibleQuerySourceCollection,
				localTableAliasMap,
				effectiveAliasMap,
				effectiveTableCollection,
				deleteTargetTableRef,
				clauseKey,
				true,
				true,
				false,
				deferCorrelatedValueSubqueryQualifiedUnknowns,
				false,
				null);
		ConvertEgressColumnResolutionResult clauseEgressResult =
				resolveColumnRefAtConvertEgress(columnName, tableRef, clauseProbeCtx);
		if (clauseEgressResult.hasExpandedDerivedSourceLineage()) {
			recordRelationalModifierDerivedColumnClauseRefOnQueryDictionary(
					refObj,
					columnName,
					clauseKey,
					localInterface,
					localCurrentQueryDictionary,
					localUnresolvedColumnMap,
					effectiveAliasMap,
					visibleQuerySourceCollection,
					localTableCollection);
			return ArchivedClauseColumnRefResult.expandedDerivedSourceLineage(
					clauseEgressResult.expandedDerivedSourceLineage());
		}
		if (clauseEgressResult.isPivotOperandColumn()) {
			applyConvertEgressPivotOperandMaterialization(
					clauseEgressResult,
					columnName,
					tableRef,
					localUnresolvedColumnMap,
					localTableCollection,
					relationalModifierContext,
					localTableAliasMap);
			return ArchivedClauseColumnRefResult.skip();
		}
		if (clauseEgressResult.isUnpivotInSourceColumn()) {
			applyConvertEgressUnpivotInSourceMaterialization(
					clauseEgressResult,
					columnName,
					tableRef,
					localUnresolvedColumnMap,
					localTableCollection,
					relationalModifierContext,
					localTableAliasMap);
			return ArchivedClauseColumnRefResult.skip();
		}
		if (clauseEgressResult.isDerivedColumn()) {
			recordRelationalModifierDerivedColumnClauseRefOnQueryDictionary(
					refObj,
					columnName,
					clauseKey,
					localInterface,
					localCurrentQueryDictionary,
					localUnresolvedColumnMap,
					effectiveAliasMap,
					visibleQuerySourceCollection,
					localTableCollection);
			String bucketKey = resolveRelationalModifierDerivedColumnBucketKey(
					columnName,
					tableRef,
					localDerivedColumns,
					localTableAliasMap);
			if (bucketKey != null && !bucketKey.isBlank()) {
				HashMap<String, Object> probeRef = new HashMap<String, Object>();
				probeRef.put(MUMBLE_NAME_KEY, columnName);
				if (tableRef != null && !tableRef.isBlank()) {
					probeRef.put(MUMBLE_TABLE_REF_KEY, tableRef);
				}
				ArrayList<Object> probeRefs = new ArrayList<Object>(1);
				probeRefs.add(probeRef);
				ArrayList<Object> dependencyLineage = expandReferenceListForRelationalModifierDerivedColumn(
						probeRefs,
						localDerivedColumns,
						activeConvertEgressRelationalModifierSourceColumns,
						localTableAliasMap);
				if (dependencyLineage != null && !dependencyLineage.isEmpty()) {
					return ArchivedClauseColumnRefResult.expandedDerivedSourceLineage(
							dependencyLineage);
				}
			}
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

		UnqualifiedScopeResolutionResult resolutionResult = clauseEgressResult.unqualified();
		if (resolutionResult == null) {
			return ArchivedClauseColumnRefResult.unresolved();
		}

		switch (resolutionResult.status) {
			case RESOLVED_DERIVED_COLUMN, RESOLVED_UNPIVOT_VALUE, RESOLVED_UNPIVOT_FOR -> {
				recordRelationalModifierDerivedColumnClauseRefOnQueryDictionary(
						refObj,
						columnName,
						clauseKey,
						localInterface,
						localCurrentQueryDictionary,
						localUnresolvedColumnMap,
						effectiveAliasMap,
						visibleQuerySourceCollection,
						localTableCollection);
				return ArchivedClauseColumnRefResult.skip();
			}
			case RESOLVED -> {
				if (isGroundedIntraQueryOutputAliasUsage(
						columnName,
						tableRef,
						localInterface,
						localTableCollection)) {
					if (isForwardWindowSelectListOutputAliasRef(columnName, clauseKey, probeContext)) {
						return ArchivedClauseColumnRefResult.unresolved();
					}
					return ArchivedClauseColumnRefResult.skip();
				}
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
				if (isGroundedIntraQueryOutputAliasUsage(
						columnName,
						tableRef,
						localInterface,
						localTableCollection)) {
					if (isForwardWindowSelectListOutputAliasRef(columnName, clauseKey, probeContext)) {
						return ArchivedClauseColumnRefResult.unresolved();
					}
					return ArchivedClauseColumnRefResult.skip();
				}
				return ArchivedClauseColumnRefResult.ambiguous(resolutionResult.ambiguousSourcesLabel);
			}
			case AMBIGUOUS_DERIVED_COLUMN -> {
				return ArchivedClauseColumnRefResult.skip();
			}
			case DEFERRED -> {
				return ArchivedClauseColumnRefResult.deferred();
			}
			default -> {
				// Phase 13.4: ingress already proved true SELECT-list output aliases and skipped
				// unresolved collection. Convert clause probe (esp. window OVER inside the select
				// list) must not re-fatal those names when FROM sources cannot bind them.
				if (isGroundedIntraQueryOutputAliasUsage(
						columnName,
						tableRef,
						localInterface,
						localTableCollection)) {
					if (isForwardWindowSelectListOutputAliasRef(columnName, clauseKey, probeContext)) {
						return ArchivedClauseColumnRefResult.unresolved();
					}
					return ArchivedClauseColumnRefResult.skip();
				}
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
			HashMap<String, Object> archivedScopeColumnReferenceContainers,
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
			RelationalModifierConvertEgressContext relationalModifierContext,
			String deleteTargetTableRef,
			boolean deferCorrelatedValueSubqueryQualifiedUnknowns) {
		HashMap<String, Object> scopeSymbols = archivedScopeColumnReferenceContainers != null
				? archivedScopeColumnReferenceContainers
				: new HashMap<String, Object>();
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
				relationalModifierContext,
				deleteTargetTableRef,
				deferCorrelatedValueSubqueryQualifiedUnknowns,
				activeConvertEgressCurrentQueryScopeKey);
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
		if (defersRelationalModifierClauseHarvestColumnRefList(UPDATE_ASSIGNMENT_RHS_CLAUSE_PROBE_KEY)) {
			return;
		}
		for (Object rhsRefsObj : assignmentsMapObj.values()) {
			probeArchivedScopeClauseColumnList(
					rhsRefsObj,
					UPDATE_ASSIGNMENT_RHS_CLAUSE_PROBE_KEY,
					probeContext);
		}
	}

	@SuppressWarnings("unchecked")
	private void probeArchivedScopeClauseColumns(ArchivedClauseProbeContext probeContext) {
		if (probeContext == null || probeContext.scopeSymbols == null) {
			return;
		}
		for (String clauseKey : ARCHIVED_SCOPE_COLUMN_REFERENCE_CONTAINER_KEYS) {
			if (defersRelationalModifierClauseHarvestColumnRefList(clauseKey)) {
				continue;
			}
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
					probeContext,
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
					probeContext.relationalModifierContext,
					probeContext.deleteTargetTableRef,
					probeContext.deferCorrelatedSubqueries);
			applyArchivedClauseColumnRefProbeResult(
					columnRefs,
					index,
					columnRefObj,
					result,
					clauseKey,
					probeContext);
			if (result != null
					&& result.disposition == ArchivedClauseColumnRefDisposition.EXPANDED_DERIVED_SOURCE_LINEAGE
					&& result.expandedDerivedSourceLineage != null
					&& !result.expandedDerivedSourceLineage.isEmpty()) {
				index += result.expandedDerivedSourceLineage.size() - 1;
			}
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
			case EXPANDED_DERIVED_SOURCE_LINEAGE -> {
				if (defersRelationalModifierClauseHarvestColumnRefList(clauseKey)) {
					return;
				}
				if (result.expandedDerivedSourceLineage == null
						|| result.expandedDerivedSourceLineage.isEmpty()) {
					return;
				}
				columnRefs.remove(index);
				columnRefs.addAll(
						index,
						copyInterfaceReferenceList(result.expandedDerivedSourceLineage));
			}
			case RESOLVED_INTRA_QUERY_OUTPUT_ALIAS -> {
				if (result.resolvedSourceRef != null && !result.resolvedSourceRef.isBlank()) {
					columnRefs.set(
							index,
							cloneReferenceWithResolvedTableRef(columnRefObj, result.resolvedSourceRef));
				}
			}
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
						|| hasLocalPhysicalFromTables(
								buildLocalPhysicalFromTableCollection(
										probeContext.localFromTableCollection))) {
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

	/**
	 * Fold alias-keyed table-dictionary buckets into canonical physical table keys.
	 * Phase 17.7.8: derived PIVOT/UNPIVOT outputs must not be stripped here — if they appear on
	 * physical keys, fix modifier finalize upstream.
	 */
	@SuppressWarnings("unchecked")
	private void canonicalizeLocalTableCollection(
			HashMap<String, Object> localTableCollection,
			HashMap<String, Object> localTableAliasMap) {
		if (localTableCollection == null || localTableCollection.isEmpty()) {
			return;
		}

		for (String tableKey : new ArrayList<String>(localTableCollection.keySet())) {
			if (tableKey == null || tableKey.isBlank()) {
				continue;
			}
			if (isQuerySourceReference(tableKey)
					|| isTableFunctionSourceReference(tableKey)
					|| walker.isNonTableQuerySourceReference(tableKey)) {
				continue;
			}

			String canonicalKey = resolveCanonicalPhysicalTableRef(tableKey, localTableAliasMap);
			if (canonicalKey == null || canonicalKey.isBlank()) {
				canonicalKey = normalizeTableRef(tableKey);
			}
			if (canonicalKey == null || canonicalKey.isBlank()) {
				continue;
			}

			Object tableColumnsObj = localTableCollection.get(tableKey);
			if (!(tableColumnsObj instanceof HashMap<?, ?> tableColumnsMapObj)) {
				continue;
			}
			HashMap<String, Object> tableColumns = (HashMap<String, Object>) tableColumnsMapObj;

			if (tableKey.equalsIgnoreCase(canonicalKey)) {
				continue;
			}

			HashMap<String, Object> canonicalColumns = walker.getTableDictionaryForReference(
					canonicalKey,
					localTableCollection);
			if (canonicalColumns == null) {
				canonicalColumns = new HashMap<String, Object>();
				localTableCollection.put(canonicalKey, canonicalColumns);
			}
			for (Map.Entry<String, Object> columnEntry : tableColumns.entrySet()) {
				walker.mergeResolvedColumnIntoDictionary(
						canonicalColumns,
						columnEntry.getKey(),
						columnEntry.getValue());
			}
			localTableCollection.remove(tableKey);
		}
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

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> collectVisibleQuerySourceCollection(HashMap<String, Object> tableAliasCollection) {
		if (activeConvertEgressScopeBundle != null) {
			return new HashMap<String, Object>(activeConvertEgressScopeBundle.visibleQuerySourceRefs);
		}

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

	/**
	 * Phase 19.1: inputs for the single query-dictionary publish policy
	 * ({@link #publishQueryDictionary}). Scope finalizers and {@link #publishQueryLikeScope}
	 * should route through this instead of calling {@link #mergeIntoGlobalQueryColumnDictionary}
	 * directly.
	 */
	private static final class QueryDictionaryPublishContext {
		final String liveScopeKey;
		final HashMap<String, Object> queryDictionary;
		/** When non-null, the sanitized dictionary is written under {@code query_dictionary}. */
		final Map<String, Object> embeddedTarget;
		final boolean sanitize;
		final boolean mergeIntoGlobal;

		private QueryDictionaryPublishContext(
				String liveScopeKey,
				HashMap<String, Object> queryDictionary,
				Map<String, Object> embeddedTarget,
				boolean sanitize,
				boolean mergeIntoGlobal) {
			this.liveScopeKey = liveScopeKey;
			this.queryDictionary = queryDictionary;
			this.embeddedTarget = embeddedTarget;
			this.sanitize = sanitize;
			this.mergeIntoGlobal = mergeIntoGlobal;
		}
	}

	/**
	 * Phase 19.1: sole intended ingress for scope-close query-dictionary publish —
	 * optional sanitize, optional embed onto a scope payload / {@code def_*} map, then
	 * optional merge into the global live index. Does not replace phase-2 usage enrichment
	 * ({@link #mergeExplicitQualifiedUnknownIntoSourceQueryDictionary}).
	 */
	private void publishQueryDictionary(QueryDictionaryPublishContext context) {
		if (context == null || context.queryDictionary == null) {
			return;
		}
		if (context.sanitize) {
			sanitizeQueryDictionaryForGlobalExport(context.queryDictionary);
		}
		if (context.embeddedTarget != null) {
			context.embeddedTarget.put(MUMBLE_QUERY_DICTIONARY_KEY, context.queryDictionary);
		}
		if (context.mergeIntoGlobal) {
			mergeIntoGlobalQueryColumnDictionary(context.liveScopeKey, context.queryDictionary);
		}
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
			HashMap<String, Object> published = new HashMap<String, Object>(queryDictionary);
			walker.queryColumnDictionaryMap.put(normalizedScopeKey, published);
			notifyActiveConvertEgressBundleGlobalQueryDictionary(normalizedScopeKey, published);
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
		notifyActiveConvertEgressBundleGlobalQueryDictionary(normalizedScopeKey, existingMap);
	}

	/**
	 * Phase 19.5: keep {@link ConvertEgressScopeBundle#globalQueryDictionaryRefs} aligned when the
	 * live index gains a new scope key during convert (same map instance for existing keys is
	 * already shared; new keys must be registered explicitly).
	 */
	private void notifyActiveConvertEgressBundleGlobalQueryDictionary(
			String liveQueryRef,
			Object queryDictionaryObj) {
		if (activeConvertEgressScopeBundle == null
				|| liveQueryRef == null
				|| liveQueryRef.isBlank()
				|| queryDictionaryObj == null) {
			return;
		}
		activeConvertEgressScopeBundle.globalQueryDictionaryRefs.put(liveQueryRef, queryDictionaryObj);
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

		if (activeConvertEgressScopeBundle != null) {
			Object fromVisibleRefs =
					activeConvertEgressScopeBundle.visibleQuerySourceRefs.get(normalizedQueryRef);
			if (fromVisibleRefs instanceof Map<?, ?>) {
				return fromVisibleRefs;
			}
			return activeConvertEgressScopeBundle.getDefinitionPayload(defQueryRef);
		}

		Object queryObj = resolveDefinitionSymbolInScopeChain(defQueryRef);
		if (queryObj instanceof Map<?, ?>) {
			return queryObj;
		}

		return null;
	}

	/**
	 * Reads a scope's column-token map from the global live index
	 * ({@link SqlASTWalkerHelper#queryColumnDictionaryMap}), preferring
	 * {@link ConvertEgressScopeBundle#globalQueryDictionaryRefs} while convert egress is active
	 * (Phase 15.6 / 19.5). That index is keyed by live source refs ({@code queryN}, {@code unionN},
	 * etc.), not {@code def_*}. Nested scope payloads live under {@code def_queryN} in the symbol
	 * table; use {@link #getQueryDefinitionSymbol} for those.
	 */
	public Object getQuerySourceDictionaryPreferDefinition(String queryRef) {
		String liveQueryRef = normalizeQuerySourceReference(queryRef);
		if (liveQueryRef == null || !isQuerySourceReference(liveQueryRef)) {
			return null;
		}

		if (activeConvertEgressScopeBundle != null) {
			Object queryDictionaryObj =
					activeConvertEgressScopeBundle.getGlobalQueryDictionary(liveQueryRef);
			if (queryDictionaryObj instanceof Map<?, ?>) {
				return queryDictionaryObj;
			}
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
				|| normalizedSourceRef.startsWith(MUMBLE_INSERT_KEY)
				|| normalizedSourceRef.startsWith(MUMBLE_UPDATE_KEY)
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
			RelationalModifierConvertEgressContext relationalModifierContext) {
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
							relationalModifierContext);

			switch (resolutionResult.status) {
				case RESOLVED_DERIVED_COLUMN, RESOLVED_UNPIVOT_VALUE, RESOLVED_UNPIVOT_FOR -> {
					if (isPhysicalTableQualifierForDerivedLineage(
							tableRef,
							localTableCollection,
							tableAliasCollection)) {
						materializeQualifiedUnresolvedEntry(
								unresolvedKey,
								unknownEntry.getValue(),
								tableAliasCollection,
								localTableCollection,
								localCurrentQueryDictionary,
								localInterface);
					}
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
			notifyActiveConvertEgressBundleGlobalQueryDictionary(liveQueryRef, sourceQueryDictionary);
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

	public Object getQueryDefinitionSymbol(String queryKey) {
		if (queryKey == null || queryKey.isBlank()) {
			return null;
		}

		String definitionKey = normalizeQueryScopeDefinitionKey(queryKey);
		if (definitionKey != null) {
			Object normalizedDefObj = resolveDefinitionSymbolInScopeChain(definitionKey);
			if (normalizedDefObj != null) {
				return normalizedDefObj;
			}
		}

		String cteScopeRef = resolveCteScopeReference(queryKey, null);
		if (cteScopeRef != null && !cteScopeRef.isBlank()) {
			String cteDefinitionKey = toDefinitionScopeKey(cteScopeRef);
			Object cteDefScopeObj = resolveDefinitionSymbolInScopeChain(cteDefinitionKey);
			if (cteDefScopeObj != null) {
				return cteDefScopeObj;
			}
		}

		return null;
	}

	/**
	 * Definition scope chain resolution: searches the current symbol table and all
	 * ancestor symbol tables (closest to farthest) for a published {@code def_*} payload.
	 * Returns the first non-null value found, or null if not visible on the scope chain.
	 * <p>
	 * Convert-egress readers should migrate to {@code ConvertEgressScopeBundle} (Phase 15.6)
	 * so this walk is not repeated per lookup during scope exit.
	 */
	@SuppressWarnings("unchecked")
	private Object resolveDefinitionSymbolInScopeChain(String key) {
		if (key == null || key.isBlank()) {
			return null;
		}
		if (activeConvertEgressScopeBundle != null) {
			return activeConvertEgressScopeBundle.getDefinitionPayload(key);
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

	public boolean isWindowFunctionSelectItemSubtree(HashMap<String, Object> selectItemSubtree) {
		return containsWindowFunctionSelectItemSubtree(selectItemSubtree);
	}

	@SuppressWarnings("unchecked")
	private boolean containsWindowFunctionSelectItemSubtree(HashMap<String, Object> selectItemSubtree) {
		if (selectItemSubtree == null || selectItemSubtree.isEmpty()) {
			return false;
		}
		if (selectItemSubtree.containsKey(MUMBLE_WINDOW_FUNCTION_KEY)) {
			return true;
		}
		for (Object value : selectItemSubtree.values()) {
			if (value instanceof HashMap<?, ?> childMapObj) {
				if (containsWindowFunctionSelectItemSubtree((HashMap<String, Object>) childMapObj)) {
					return true;
				}
			}
		}
		return false;
	}

	public void pushPendingWindowSelectInterfacePartitionDeps(ArrayList<Object> partitionDeps) {
		if (partitionDeps == null || partitionDeps.isEmpty()) {
			pendingWindowSelectInterfacePartitionDeps.addLast(new ArrayList<Object>());
			return;
		}
		pendingWindowSelectInterfacePartitionDeps.addLast(partitionDeps);
	}

	private void pushPendingWithinGroupOrderByDeps(ArrayList<Object> withinGroupOrderByDeps) {
		if (withinGroupOrderByDeps == null || withinGroupOrderByDeps.isEmpty()) {
			pendingWithinGroupOrderByDeps.addLast(new ArrayList<Object>());
			return;
		}
		pendingWithinGroupOrderByDeps.addLast(withinGroupOrderByDeps);
	}

	private ArrayList<Object> pollPendingWithinGroupOrderByDeps() {
		if (pendingWithinGroupOrderByDeps.isEmpty()) {
			return new ArrayList<Object>();
		}
		return pendingWithinGroupOrderByDeps.pollLast();
	}

	private WindowSelectInterfaceClauseDeps newWindowSelectInterfaceClauseDeps(
			ArrayList<Object> partitionByRefs,
			ArrayList<Object> orderByRefs) {
		return new WindowSelectInterfaceClauseDeps(
				partitionByRefs,
				orderByRefs,
				pollPendingWithinGroupOrderByDeps());
	}

	public boolean hasLatchedWindowOverClauseDepsForNextSelectItem() {
		return latchedWindowOverClauseDepsForNextSelectItem != null;
	}

	public boolean hasPendingWindowSelectInterfaceOverDeps() {
		return !pendingWindowSelectInterfaceOverDeps.isEmpty();
	}

	public void latchCompletedWindowOverClauseDepsForNextSelectItem() {
		if (latchedWindowOverClauseDepsForNextSelectItem == null
				&& !pendingWindowSelectInterfaceOverDeps.isEmpty()) {
			latchedWindowOverClauseDepsForNextSelectItem =
					pendingWindowSelectInterfaceOverDeps.peekLast();
		}
	}

	/**
	 * After {@code exitSelect_item} flatten for a window output, append PARTITION BY and ORDER BY
	 * clause column refs captured at {@code exitPartition_by_clause} / in-{@code OVER} {@code exitOrderby_clause}
	 * (same harvest as {@code window_partition_by} / {@code window_ordered_by}, scoped per {@code OVER}).
	 */
	public void mergePendingWindowSelectInterfaceClauseDepsIntoInterfaceColumnList(
			ArrayList<Object> interfaceColumnList,
			String interfaceAlias) {
		if (interfaceColumnList == null) {
			return;
		}
		WindowSelectInterfaceClauseDeps overDeps = latchedWindowOverClauseDepsForNextSelectItem;
		latchedWindowOverClauseDepsForNextSelectItem = null;
		if (overDeps == null && !pendingWindowSelectInterfaceOverDeps.isEmpty()) {
			overDeps = pendingWindowSelectInterfaceOverDeps.pollLast();
		} else if (overDeps != null) {
			pendingWindowSelectInterfaceOverDeps.pollLast();
		}
		if (overDeps == null && !pendingWindowSelectInterfacePartitionDeps.isEmpty()) {
			ArrayList<Object> partitionByRefs = pendingWindowSelectInterfacePartitionDeps.pollLast();
			overDeps = newWindowSelectInterfaceClauseDeps(partitionByRefs, new ArrayList<Object>());
		}
		if (overDeps == null) {
			return;
		}
		for (Object refObj : overDeps.partitionByRefs) {
			appendClauseColumnReferenceForConvertEgress(interfaceColumnList, refObj);
		}
		for (Object refObj : overDeps.orderByRefs) {
			appendClauseColumnReferenceForConvertEgress(interfaceColumnList, refObj);
		}
		rememberWindowOutputInterfaceClauseDepsForAlias(
				interfaceAlias,
				copyWindowSelectInterfaceClauseDeps(overDeps));
	}

	private WindowSelectInterfaceClauseDeps copyWindowSelectInterfaceClauseDeps(
			WindowSelectInterfaceClauseDeps source) {
		if (source == null) {
			return null;
		}
		return new WindowSelectInterfaceClauseDeps(
				copyClauseColumnReferenceSublist(source.partitionByRefs, 0),
				copyClauseColumnReferenceSublist(source.orderByRefs, 0),
				copyClauseColumnReferenceSublist(source.withinGroupOrderByRefs, 0));
	}

	private ArrayList<Object> copyClauseColumnReferenceSublist(
			ArrayList<Object> sourceList,
			int startIndexInclusive) {
		ArrayList<Object> slice = new ArrayList<Object>();
		if (sourceList == null || startIndexInclusive >= sourceList.size()) {
			return slice;
		}
		for (int index = startIndexInclusive; index < sourceList.size(); index++) {
			Object refObj = sourceList.get(index);
			Object egressCopy = copyClauseColumnReferenceForEgress(refObj);
			if (egressCopy != null) {
				slice.add(egressCopy);
			}
		}
		return slice;
	}

	/** Windows in query {@code ORDER BY} do not consume pending deps; drop after the SELECT list. */
	public void clearPendingWindowSelectInterfaceClauseDeps() {
		pendingWindowSelectInterfacePartitionDeps.clear();
		pendingWithinGroupOrderByDeps.clear();
		pendingWindowSelectInterfaceOverDeps.clear();
		latchedWindowOverClauseDepsForNextSelectItem = null;
		lastWindowSelectListOutputInterfaceAlias = null;
		lastSelectListOutputInterfaceAlias = null;
	}

	@SuppressWarnings("unchecked")
	private void applyWalkCapturedWindowSelectInterfaceClauseDeps(
			HashMap<String, Object> localInterface) {
		if (localInterface == null || windowOutputInterfaceClauseDepsByAlias.isEmpty()) {
			return;
		}
		for (Map.Entry<String, WindowSelectInterfaceClauseDeps> entry :
				windowOutputInterfaceClauseDepsByAlias.entrySet()) {
			Object refsObj = localInterface.get(entry.getKey());
			if (!(refsObj instanceof ArrayList<?>)) {
				continue;
			}
			ArrayList<Object> interfaceColumnList = (ArrayList<Object>) refsObj;
			WindowSelectInterfaceClauseDeps overDeps = entry.getValue();
			if (overDeps == null) {
				continue;
			}
			for (Object refObj : overDeps.partitionByRefs) {
				appendClauseColumnReferenceForConvertEgress(interfaceColumnList, refObj);
			}
			for (Object refObj : overDeps.orderByRefs) {
				appendClauseColumnReferenceForConvertEgress(interfaceColumnList, refObj);
			}
		}
		windowOutputInterfaceClauseDepsByAlias.clear();
	}

	private void rememberWindowOutputInterfaceClauseDepsForAlias(
			String interfaceAlias,
			WindowSelectInterfaceClauseDeps overDeps) {
		if (interfaceAlias == null
				|| interfaceAlias.isBlank()
				|| overDeps == null) {
			return;
		}
		windowOutputInterfaceClauseDepsByAlias.put(interfaceAlias, overDeps);
	}

	public void recordSelectListOutputInterfaceAlias(String interfaceAlias) {
		if (interfaceAlias != null && !interfaceAlias.isBlank()) {
			lastSelectListOutputInterfaceAlias = interfaceAlias;
			Object orderObj = walker.symbolTable.get(TEMP_SELECT_LIST_OUTPUT_ALIAS_SOURCE_ORDER_KEY);
			ArrayList<String> sourceOrder;
			if (orderObj instanceof ArrayList<?>) {
				sourceOrder = (ArrayList<String>) orderObj;
			} else {
				sourceOrder = new ArrayList<String>();
				walker.symbolTable.put(TEMP_SELECT_LIST_OUTPUT_ALIAS_SOURCE_ORDER_KEY, sourceOrder);
			}
			if (indexOfIgnoreCase(sourceOrder, interfaceAlias) < 0) {
				sourceOrder.add(interfaceAlias);
			}
		}
	}

	private static int indexOfIgnoreCase(ArrayList<String> values, String candidate) {
		if (values == null || candidate == null || candidate.isBlank()) {
			return -1;
		}
		for (int index = 0; index < values.size(); index++) {
			String value = values.get(index);
			if (value != null && value.equalsIgnoreCase(candidate)) {
				return index;
			}
		}
		return -1;
	}

	public void recordWindowSelectListOutputInterfaceAlias(String interfaceAlias) {
		if (interfaceAlias != null && !interfaceAlias.isBlank()) {
			lastWindowSelectListOutputInterfaceAlias = interfaceAlias;
		}
	}

	/**
	 * When in-{@code OVER} {@code ORDER BY} is harvested after {@code exitSelect_item}, merge
	 * latched PARTITION BY / ORDER BY refs into the last window SELECT-list output on this list.
	 */
	@SuppressWarnings("unchecked")
	public void mergeOrphanedLatchedWindowOverClauseDepsIntoSelectInterface(
			HashMap<String, Object> selectInterface) {
		if (latchedWindowOverClauseDepsForNextSelectItem == null
				|| selectInterface == null) {
			return;
		}
		String targetAlias = lastWindowSelectListOutputInterfaceAlias;
		if (targetAlias == null || targetAlias.isBlank()) {
			targetAlias = lastSelectListOutputInterfaceAlias;
		}
		if (targetAlias == null || targetAlias.isBlank()) {
			return;
		}
		Object refsObj = selectInterface.get(targetAlias);
		if (!(refsObj instanceof ArrayList<?>)) {
			return;
		}
		ArrayList<Object> interfaceColumnList = (ArrayList<Object>) refsObj;
		WindowSelectInterfaceClauseDeps overDeps = latchedWindowOverClauseDepsForNextSelectItem;
		latchedWindowOverClauseDepsForNextSelectItem = null;
		if (!pendingWindowSelectInterfaceOverDeps.isEmpty()) {
			pendingWindowSelectInterfaceOverDeps.pollLast();
		}
		for (Object refObj : overDeps.partitionByRefs) {
			appendClauseColumnReferenceForConvertEgress(interfaceColumnList, refObj);
		}
		for (Object refObj : overDeps.orderByRefs) {
			appendClauseColumnReferenceForConvertEgress(interfaceColumnList, refObj);
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

		int flatListSizeBeforeClauseHarvest = flatList.size();
		if (clauseSubMap instanceof HashMap<?, ?>) {
			flattenSubTreeForClauseColumns((HashMap<String, Object>) clauseSubMap, flatList);
		}

		if (MUMBLE_WITHIN_GROUP_ORDERED_BY_KEY.equals(symbolTableKey)) {
			pushPendingWithinGroupOrderByDeps(
					copyClauseColumnReferenceSublistFromFlatList(flatList, flatListSizeBeforeClauseHarvest));
		} else if (MUMBLE_WINDOW_PARTITION_BY_KEY.equals(symbolTableKey)) {
			pushPendingWindowSelectInterfacePartitionDeps(
					copyClauseColumnReferenceSublistFromFlatList(flatList, flatListSizeBeforeClauseHarvest));
		} else if (MUMBLE_WINDOW_ORDERED_BY_KEY.equals(symbolTableKey)) {
			ArrayList<Object> orderByRefs =
					copyClauseColumnReferenceSublistFromFlatList(flatList, flatListSizeBeforeClauseHarvest);
			ArrayList<Object> partitionByRefs = pendingWindowSelectInterfacePartitionDeps.pollLast();
			WindowSelectInterfaceClauseDeps overDeps =
					newWindowSelectInterfaceClauseDeps(partitionByRefs, orderByRefs);
			pendingWindowSelectInterfaceOverDeps.addLast(overDeps);
			latchedWindowOverClauseDepsForNextSelectItem = overDeps;
		}

		walker.symbolTable.put(symbolTableKey, flatList);
	}

	@SuppressWarnings("unchecked")
	private ArrayList<Object> copyClauseColumnReferenceSublistFromFlatList(
			ArrayList<Object> flatList,
			int startIndexInclusive) {
		ArrayList<Object> slice = new ArrayList<Object>();
		if (flatList == null || startIndexInclusive >= flatList.size()) {
			return slice;
		}
		for (int index = startIndexInclusive; index < flatList.size(); index++) {
			Object refObj = flatList.get(index);
			Object egressCopy = copyClauseColumnReferenceForEgress(refObj);
			if (egressCopy != null) {
				slice.add(egressCopy);
			}
		}
		return slice;
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

	public void beginJoinUsingColumnListScope() {
		Object depthObj = walker.symbolTable.get(JOIN_USING_COLUMN_LIST_DEPTH_KEY);
		int depth = (depthObj instanceof Number number) ? number.intValue() : 0;
		walker.symbolTable.put(JOIN_USING_COLUMN_LIST_DEPTH_KEY, depth + 1);
	}

	public void endJoinUsingColumnListScope() {
		Object depthObj = walker.symbolTable.get(JOIN_USING_COLUMN_LIST_DEPTH_KEY);
		int depth = (depthObj instanceof Number number) ? number.intValue() : 0;
		if (depth <= 1) {
			walker.symbolTable.remove(JOIN_USING_COLUMN_LIST_DEPTH_KEY);
		} else {
			walker.symbolTable.put(JOIN_USING_COLUMN_LIST_DEPTH_KEY, depth - 1);
		}
	}

	public boolean isInJoinUsingColumnListScope() {
		Object depthObj = walker.symbolTable.get(JOIN_USING_COLUMN_LIST_DEPTH_KEY);
		return depthObj instanceof Number number && number.intValue() > 0;
	}

	@SuppressWarnings("unchecked")
	public void recordJoinUsingOperandTokenRef(String columnName, Token token) {
		if (columnName == null || columnName.isBlank() || token == null) {
			return;
		}
		Object refsObj = walker.symbolTable.get(JOIN_USING_OPERAND_TOKEN_REFS_KEY);
		HashMap<String, Object> refsMap;
		if (refsObj instanceof HashMap<?, ?> existing) {
			refsMap = (HashMap<String, Object>) existing;
		} else {
			refsMap = new HashMap<String, Object>();
			walker.symbolTable.put(JOIN_USING_OPERAND_TOKEN_REFS_KEY, refsMap);
		}
		ArrayList<String> tokenRefs = new ArrayList<String>();
		tokenRefs.add(token.toString());
		walker.mergeResolvedColumnIntoDictionary(refsMap, columnName, tokenRefs);
		Object tokenByNameObj = walker.symbolTable.get(JOIN_USING_OPERAND_TOKEN_BY_NAME_KEY);
		HashMap<String, Token> tokenByName;
		if (tokenByNameObj instanceof HashMap<?, ?> existing) {
			tokenByName = (HashMap<String, Token>) existing;
		} else {
			tokenByName = new HashMap<String, Token>();
			walker.symbolTable.put(JOIN_USING_OPERAND_TOKEN_BY_NAME_KEY, tokenByName);
		}
		tokenByName.put(columnName, token);
	}

	public void emitJoinUsingQualifiedColumnFatal(
			String operandTableRef,
			String columnName,
			Integer line,
			Integer charPos) {
		String qualifiedColumnLabel = (operandTableRef != null && !operandTableRef.isBlank())
				? operandTableRef + "." + columnName
				: columnName;
		String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_IN_JOIN_USING);
		String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_QUALIFIED_COLUMN_IN_JOIN_USING);
		String diagMessage = (diagTemplate == null)
				? String.format(
						"Join Using column '%s' at (l:%s c:%s) must not be qualified.",
						qualifiedColumnLabel,
						String.valueOf(line),
						String.valueOf(charPos))
				: String.format(
						diagTemplate,
						qualifiedColumnLabel,
						String.valueOf(line),
						String.valueOf(charPos));
		walker.addWalkerFatal(
				diagCode,
				diagMessage,
				line,
				charPos,
				qualifiedColumnLabel);
		markJoinUsingColumnDisqualified(columnName);
	}

	public void emitCrossNaturalJoinInvalidConditionFatal(
			String joinKeyword,
			Integer joinLine,
			Integer joinCharPos,
			String conditionKeyword,
			Integer conditionLine,
			Integer conditionCharPos) {
		String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_CROSS_NATURAL_JOIN_INVALID_CONDITION);
		String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_CROSS_NATURAL_JOIN_INVALID_CONDITION);
		String diagMessage = (diagTemplate == null)
				? String.format(
						"%s JOIN at (l:%s c:%s) has invalid %s condition (l:%s c:%s).",
						joinKeyword,
						String.valueOf(joinLine),
						String.valueOf(joinCharPos),
						conditionKeyword,
						String.valueOf(conditionLine),
						String.valueOf(conditionCharPos))
				: String.format(
						diagTemplate,
						joinKeyword,
						String.valueOf(joinLine),
						String.valueOf(joinCharPos),
						conditionKeyword,
						String.valueOf(conditionLine),
						String.valueOf(conditionCharPos));
		walker.addWalkerFatal(
				diagCode,
				diagMessage,
				joinLine,
				joinCharPos,
				joinKeyword + "/" + conditionKeyword);
	}

	public void emitNaturalFullOuterJoinUnsupportedFatal(Integer joinLine, Integer joinCharPos) {
		String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_NATURAL_FULL_OUTER_JOIN_UNSUPPORTED);
		String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_NATURAL_FULL_OUTER_JOIN_UNSUPPORTED);
		String diagMessage = (diagTemplate == null)
				? String.format(
						"NATURAL FULL OUTER JOIN at (l:%s c:%s) is not supported.",
						String.valueOf(joinLine),
						String.valueOf(joinCharPos))
				: String.format(diagTemplate, String.valueOf(joinLine), String.valueOf(joinCharPos));
		walker.addWalkerFatal(
				diagCode,
				diagMessage,
				joinLine,
				joinCharPos,
				"NATURAL FULL OUTER JOIN");
	}

	@SuppressWarnings("unchecked")
	private void markJoinUsingColumnDisqualified(String columnName) {
		if (columnName == null || columnName.isBlank()) {
			return;
		}
		Object existing = walker.symbolTable.get(JOIN_USING_DISQUALIFIED_COLUMN_NAMES_KEY);
		HashSet<String> disqualified;
		if (existing instanceof HashSet<?> set) {
			disqualified = (HashSet<String>) set;
		} else {
			disqualified = new HashSet<String>();
			walker.symbolTable.put(JOIN_USING_DISQUALIFIED_COLUMN_NAMES_KEY, disqualified);
		}
		disqualified.add(columnName);
	}

	public boolean isJoinUsingColumnDisqualified(String columnName) {
		if (columnName == null || columnName.isBlank()) {
			return false;
		}
		Object existing = walker.symbolTable.get(JOIN_USING_DISQUALIFIED_COLUMN_NAMES_KEY);
		if (!(existing instanceof HashSet<?> disqualified)) {
			return false;
		}
		for (Object entry : disqualified) {
			if (entry instanceof String name && name.equalsIgnoreCase(columnName)) {
				return true;
			}
		}
		return false;
	}

	private void emitJoinUsingColumnNotFoundFatal(
			String columnName,
			Integer line,
			Integer charPos,
			String joinSourcesList) {
		if (joinSourcesList == null || joinSourcesList.isBlank()) {
			joinSourcesList = "?";
		}
		String diagCode = walker.getDiagnosticCode(SqlASTWalkerHelper.DIAG_SQL_JOIN_USING_COLUMN_NOT_FOUND);
		String diagTemplate = walker.getDiagnosticMessage(SqlASTWalkerHelper.DIAG_SQL_JOIN_USING_COLUMN_NOT_FOUND);
		String diagMessage = (diagTemplate == null)
				? String.format(
						"Join Using column '%s' at (l:%s c:%s) not found in Join Sources (%s). ",
						columnName,
						String.valueOf(line),
						String.valueOf(charPos),
						joinSourcesList)
				: String.format(
						diagTemplate,
						columnName,
						String.valueOf(line),
						String.valueOf(charPos),
						joinSourcesList);
		walker.addWalkerFatal(diagCode, diagMessage, line, charPos, columnName);
	}

	private String formatJoinUsingMissingSourcesList(
			Map<String, Object> leftSource,
			Map<String, Object> rightSource,
			boolean leftAccepts,
			boolean rightAccepts) {
		ArrayList<String> missing = new ArrayList<String>();
		if (!leftAccepts) {
			missing.add(resolveJoinUsingDisplaySourceLabel(leftSource));
		}
		if (!rightAccepts) {
			missing.add(resolveJoinUsingDisplaySourceLabel(rightSource));
		}
		return String.join(", ", missing);
	}

	@SuppressWarnings("unchecked")
	private String resolveJoinUsingDisplaySourceLabel(Map<String, Object> sourceResult) {
		if (sourceResult == null) {
			return "?";
		}
		String sourceRef = resolveJoinUsingSourceReference(sourceResult);
		if (sourceRef != null && !sourceRef.isBlank()) {
			Object aliasMapObj = walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY);
			if (aliasMapObj instanceof Map<?, ?> aliasMap && aliasMap.containsKey(sourceRef)) {
				return sourceRef;
			}
			if (aliasMapObj instanceof Map<?, ?> aliasMap) {
				for (Map.Entry<String, Object> entry : ((Map<String, Object>) aliasMap).entrySet()) {
					if (entry.getValue() instanceof String target
							&& target.equalsIgnoreCase(sourceRef)) {
						return entry.getKey();
					}
				}
			}
			return sourceRef;
		}
		return "?";
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> coerceJoinUsingColumnListAst(Object usingColumnsObj) {
		if (!(usingColumnsObj instanceof Map<?, ?> rawMap)) {
			return new LinkedHashMap<String, Object>();
		}
		Map<String, Object> raw = (Map<String, Object>) rawMap;
		if (raw.containsKey(MUMBLE_COLUMN_KEY)) {
			LinkedHashMap<String, Object> single = new LinkedHashMap<String, Object>();
			single.put("1", raw);
			return normalizeJoinUsingColumnListAst(single);
		}
		boolean hasNumericColumnEntry = false;
		for (String key : raw.keySet()) {
			if (key != null && key.matches("\\d+")) {
				hasNumericColumnEntry = true;
				break;
			}
		}
		if (hasNumericColumnEntry) {
			return normalizeJoinUsingColumnListAst(raw);
		}
		return new LinkedHashMap<String, Object>();
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> normalizeJoinUsingColumnListAst(Object usingColumnsObj) {
		LinkedHashMap<String, Object> normalized = new LinkedHashMap<String, Object>();
		if (!(usingColumnsObj instanceof Map<?, ?> rawMap)) {
			return normalized;
		}
		int index = 1;
		for (Map.Entry<?, ?> entry : ((Map<String, Object>) rawMap).entrySet()) {
			if (entry.getKey() == null || !entry.getKey().toString().matches("\\d+")) {
				continue;
			}
			Object columnEntry = entry.getValue();
			Map<String, Object> columnSubTree = extractColumnSubTreeFromAstNode(columnEntry);
			if (columnSubTree == null) {
				continue;
			}
			String columnName = (String) columnSubTree.get(MUMBLE_NAME_KEY);
			if (columnName == null || columnName.isBlank()) {
				continue;
			}
			HashMap<String, Object> usingColumn = new HashMap<String, Object>();
			usingColumn.put(MUMBLE_NAME_KEY, columnName);
			usingColumn.put(MUMBLE_TABLE_REF_KEY, null);
			HashMap<String, Object> wrapper = new HashMap<String, Object>();
			wrapper.put(MUMBLE_COLUMN_KEY, usingColumn);
			normalized.put(String.valueOf(index++), wrapper);
		}
		return normalized;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> extractColumnSubTreeFromAstNode(Object columnEntry) {
		if (columnEntry instanceof Map<?, ?> columnEntryMap) {
			if (columnEntryMap.containsKey(MUMBLE_COLUMN_KEY)) {
				Object inner = columnEntryMap.get(MUMBLE_COLUMN_KEY);
				if (inner instanceof Map<?, ?> innerMap) {
					return (Map<String, Object>) innerMap;
				}
			}
			if (columnEntryMap.containsKey(MUMBLE_NAME_KEY)) {
				return (Map<String, Object>) columnEntryMap;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public void captureJoinUsingClauseDependencies(
			Object usingColumnsObj,
			Map<String, Object> leftSource,
			Map<String, Object> rightSource) {
		try {
			Map<String, Object> normalizedUsing = normalizeJoinUsingColumnListAst(usingColumnsObj);
			if (leftSource == null || rightSource == null) {
				Map<String, Object>[] operands = resolveJoinUsingOperandsFromTableAliasMap();
				if (operands != null) {
					if (leftSource == null) {
						leftSource = operands[0];
					}
					if (rightSource == null) {
						rightSource = operands[1];
					}
				}
			}
			if (leftSource == null || rightSource == null) {
				processJoinUsingColumnsWhenOperandsUnresolved(normalizedUsing);
				return;
			}
			Object existingFilters = walker.symbolTable.remove(MUMBLE_FILTERS_KEY);
			ArrayList<Object> filtersList;
			if (existingFilters instanceof ArrayList<?>) {
				filtersList = (ArrayList<Object>) existingFilters;
			} else {
				filtersList = new ArrayList<Object>();
			}
			processJoinUsingColumnsAtJoinSpecification(
					normalizedUsing, leftSource, rightSource, filtersList);
			if (!filtersList.isEmpty()) {
				walker.symbolTable.put(MUMBLE_FILTERS_KEY, filtersList);
			}
		} finally {
			clearJoinUsingOperandScratchState();
		}
	}

	private void clearJoinUsingOperandScratchState() {
		walker.symbolTable.remove(JOIN_USING_OPERAND_TOKEN_REFS_KEY);
		walker.symbolTable.remove(JOIN_USING_OPERAND_TOKEN_BY_NAME_KEY);
		walker.symbolTable.remove(JOIN_USING_DISQUALIFIED_COLUMN_NAMES_KEY);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object>[] resolveJoinUsingOperandsFromTableAliasMap() {
		Object aliasMapObj = walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY);
		if (!(aliasMapObj instanceof LinkedHashMap<?, ?> aliasMap) || aliasMap.size() < 2) {
			return null;
		}
		ArrayList<String> aliases = new ArrayList<String>();
		for (Object aliasObj : aliasMap.keySet()) {
			if (aliasObj instanceof String alias && !alias.isBlank()) {
				aliases.add(alias);
			}
		}
		if (aliases.size() < 2) {
			return null;
		}
		String leftAlias = aliases.get(aliases.size() - 2);
		String rightAlias = aliases.get(aliases.size() - 1);
		return new Map[] {
				buildMinimalJoinUsingSourceForAlias(leftAlias),
				buildMinimalJoinUsingSourceForAlias(rightAlias)
		};
	}

	private Map<String, Object> buildMinimalJoinUsingSourceForAlias(String alias) {
		HashMap<String, Object> tablePayload = new HashMap<String, Object>();
		tablePayload.put(MUMBLE_ALIAS_KEY, alias);
		HashMap<String, Object> source = new HashMap<String, Object>();
		source.put(MUMBLE_TABLE_KEY, tablePayload);
		return source;
	}

	@SuppressWarnings("unchecked")
	private void processJoinUsingColumnsWhenOperandsUnresolved(Map<String, Object> normalizedUsing) {
		if (normalizedUsing == null || normalizedUsing.isEmpty()) {
			return;
		}
		for (Object columnWrapperObj : normalizedUsing.values()) {
			Map<String, Object> columnSubTree = extractColumnSubTreeFromAstNode(columnWrapperObj);
			if (columnSubTree == null) {
				continue;
			}
			String columnName = (String) columnSubTree.get(MUMBLE_NAME_KEY);
			if (columnName == null || columnName.isBlank() || isJoinUsingColumnDisqualified(columnName)) {
				continue;
			}
			Object tableRefObj = columnSubTree.get(MUMBLE_TABLE_REF_KEY);
			if (tableRefObj instanceof String tableRef && tableRef != null && !tableRef.isBlank()) {
				Integer[] tokenPosition = lookupJoinUsingOperandTokenPosition(columnName);
				emitJoinUsingQualifiedColumnFatal(
						tableRef,
						columnName,
						tokenPosition[0],
						tokenPosition[1]);
				continue;
			}
			Integer[] tokenPosition = lookupJoinUsingOperandTokenPosition(columnName);
			emitJoinUsingColumnNotFoundFatal(
					columnName,
					tokenPosition[0],
					tokenPosition[1],
					"?, ?");
		}
	}

	@SuppressWarnings("unchecked")
	private void processJoinUsingColumnsAtJoinSpecification(
			Map<String, Object> normalizedUsing,
			Map<String, Object> leftSource,
			Map<String, Object> rightSource,
			ArrayList<Object> filtersList) {
		if (normalizedUsing == null || normalizedUsing.isEmpty()) {
			return;
		}
		for (Object columnWrapperObj : normalizedUsing.values()) {
			Map<String, Object> columnSubTree = extractColumnSubTreeFromAstNode(columnWrapperObj);
			if (columnSubTree == null) {
				continue;
			}
			String columnName = (String) columnSubTree.get(MUMBLE_NAME_KEY);
			if (columnName == null || columnName.isBlank()) {
				continue;
			}
			if (isJoinUsingColumnDisqualified(columnName)) {
				continue;
			}
			Object tableRefObj = columnSubTree.get(MUMBLE_TABLE_REF_KEY);
			if (tableRefObj instanceof String tableRef && tableRef != null && !tableRef.isBlank()) {
				Integer[] tokenPosition = lookupJoinUsingOperandTokenPosition(columnName);
				emitJoinUsingQualifiedColumnFatal(
						tableRef,
						columnName,
						tokenPosition[0],
						tokenPosition[1]);
				continue;
			}

			Token token = lookupJoinUsingOperandToken(columnName);
			Integer[] tokenPosition = lookupJoinUsingOperandTokenPosition(columnName);

			boolean leftAccepts = joinUsingSourceAcceptsColumn(leftSource, columnName);
			boolean rightAccepts = joinUsingSourceAcceptsColumn(rightSource, columnName);
			if (!leftAccepts || !rightAccepts) {
				emitJoinUsingColumnNotFoundFatal(
						columnName,
						tokenPosition[0],
						tokenPosition[1],
						formatJoinUsingMissingSourcesList(
								leftSource, rightSource, leftAccepts, rightAccepts));
			}

			if (leftAccepts) {
				enqueueQualifiedJoinUsingColumnForStandardResolution(leftSource, columnName, token);
				appendJoinUsingQualifiedColumnToFilters(filtersList, leftSource, columnName);
			}
			if (rightAccepts) {
				enqueueQualifiedJoinUsingColumnForStandardResolution(rightSource, columnName, token);
				appendJoinUsingQualifiedColumnToFilters(filtersList, rightSource, columnName);
			}
		}
	}

	private void appendJoinUsingQualifiedColumnToFilters(
			ArrayList<Object> filtersList,
			Map<String, Object> sourceResult,
			String columnName) {
		if (filtersList == null || columnName == null || columnName.isBlank()) {
			return;
		}
		String qualifiedSourceRef = resolveJoinUsingSourceReference(sourceResult);
		if (qualifiedSourceRef == null || qualifiedSourceRef.isBlank()) {
			return;
		}
		HashMap<String, Object> columnRef = new HashMap<String, Object>();
		columnRef.put(MUMBLE_NAME_KEY, columnName);
		columnRef.put(MUMBLE_TABLE_REF_KEY, qualifiedSourceRef);
		appendClauseColumnReferenceForConvertEgress(filtersList, columnRef);
	}

	private void enqueueQualifiedJoinUsingColumnForStandardResolution(
			Map<String, Object> sourceResult,
			String columnName,
			Token token) {
		String qualifiedSourceRef = resolveJoinUsingSourceReference(sourceResult);
		if (qualifiedSourceRef == null || qualifiedSourceRef.isBlank()) {
			return;
		}
		HashMap<String, Object> columnMap = new HashMap<String, Object>();
		columnMap.put(MUMBLE_NAME_KEY, columnName);
		columnMap.put(MUMBLE_TABLE_REF_KEY, qualifiedSourceRef);
		walker.collectUnresolvedColumnReference(qualifiedSourceRef, columnMap, token);
		mergeJoinUsingOperandColumnIntoSubstitutionOrJinjaTableDictionary(
				sourceResult, columnName, token);
	}

	@SuppressWarnings("unchecked")
	private void mergeJoinUsingOperandColumnIntoSubstitutionOrJinjaTableDictionary(
			Map<String, Object> sourceResult,
			String columnName,
			Token token) {
		if (token == null || columnName == null || columnName.isBlank()) {
			return;
		}
		if (!isJoinUsingTupleSubstitutionJoinSource(sourceResult)
				&& !isJoinUsingJinjaTableJoinSource(sourceResult)) {
			return;
		}
		String aliasRef = resolveJoinUsingSourceReference(sourceResult);
		String dictionaryKey = resolveJoinUsingPhysicalDictionaryKey(sourceResult, aliasRef);
		if (dictionaryKey == null || dictionaryKey.isBlank()) {
			return;
		}
		walker.ensureTableDictionaryEntry(dictionaryKey);
		Object tableDictionaryObj = walker.symbolTable.get(MUMBLE_TABLE_DICTIONARY_KEY);
		if (!(tableDictionaryObj instanceof Map<?, ?> tableDictionaryObjMap)) {
			return;
		}
		Map<String, Object> tableDictionary = (Map<String, Object>) tableDictionaryObjMap;
		Object tableBucketObj = tableDictionary.get(dictionaryKey);
		if (!(tableBucketObj instanceof Map<?, ?> tableBucketMapObj)) {
			return;
		}
		HashMap<String, Object> tableBucket = new HashMap<String, Object>((Map<String, Object>) tableBucketMapObj);
		ArrayList<String> tokenRefs = new ArrayList<String>();
		tokenRefs.add(token.toString());
		walker.mergeResolvedColumnIntoDictionary(tableBucket, columnName, tokenRefs);
		tableDictionary.put(dictionaryKey, tableBucket);
	}

	private Token lookupJoinUsingOperandToken(String columnName) {
		Object tokenByNameObj = walker.symbolTable.get(JOIN_USING_OPERAND_TOKEN_BY_NAME_KEY);
		if (!(tokenByNameObj instanceof Map<?, ?> tokenByName)) {
			return null;
		}
		Object token = ((Map<String, Object>) tokenByName).get(columnName);
		if (token instanceof Token direct) {
			return direct;
		}
		for (Map.Entry<String, Object> entry : ((Map<String, Object>) tokenByName).entrySet()) {
			if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(columnName)
					&& entry.getValue() instanceof Token matched) {
				return matched;
			}
		}
		return null;
	}

	private Integer[] lookupJoinUsingOperandTokenPosition(String columnName) {
		Integer[] result = new Integer[] { null, null };
		Token token = lookupJoinUsingOperandToken(columnName);
		if (token != null) {
			result[0] = token.getLine();
			result[1] = token.getCharPositionInLine();
			return result;
		}
		Object refsObj = walker.symbolTable.get(JOIN_USING_OPERAND_TOKEN_REFS_KEY);
		if (!(refsObj instanceof Map<?, ?> refsMap)) {
			return result;
		}
		Object tokenListObj = ((Map<String, Object>) refsMap).get(columnName);
		if (tokenListObj == null) {
			for (Map.Entry<String, Object> entry : ((Map<String, Object>) refsMap).entrySet()) {
				if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(columnName)) {
					tokenListObj = entry.getValue();
					break;
				}
			}
		}
		if (tokenListObj instanceof List<?> tokenList && !tokenList.isEmpty()) {
			String tokenString = tokenList.get(0) == null ? null : tokenList.get(0).toString();
			int lastComma = tokenString == null ? -1 : tokenString.lastIndexOf(',');
			if (lastComma >= 0 && lastComma + 1 < tokenString.length()) {
				String tail = tokenString.substring(lastComma + 1).replace("]", "").trim();
				int colon = tail.indexOf(':');
				if (colon > 0) {
					try {
						result[0] = Integer.parseInt(tail.substring(0, colon).trim());
						result[1] = Integer.parseInt(tail.substring(colon + 1).trim());
					} catch (NumberFormatException ignored) {
						// leave nulls
					}
				}
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private boolean joinUsingSourceAcceptsColumn(Map<String, Object> sourceResult, String columnName) {
		if (sourceResult == null || columnName == null || columnName.isBlank()) {
			return false;
		}
		if (isJoinUsingCompositeOperand(sourceResult)) {
			return joinUsingCompositeOperandAcceptsColumn(sourceResult, columnName);
		}
		if (isJoinUsingPermissiveTableLikeSource(sourceResult)) {
			return true;
		}
		if (isDirectTableJoinSource(sourceResult)) {
			return true;
		}
		String canonicalRef = resolveJoinUsingCanonicalSourceRef(sourceResult);
		if (canonicalRef == null || canonicalRef.isBlank()) {
			return false;
		}
		if (isQuerySourceReference(canonicalRef)) {
			return joinUsingQueryScopeDeclaresColumn(canonicalRef, columnName);
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean joinUsingQueryScopeDeclaresColumn(String scopeRef, String columnName) {
		if (hasColumnInQueryOutputInterface(scopeRef, columnName)) {
			return true;
		}
		Object queryDefObj = getQueryDefinitionSymbol(scopeRef);
		if (!(queryDefObj instanceof Map<?, ?> queryDefMap)) {
			return false;
		}
		Object interfaceObj = ((Map<String, Object>) queryDefMap).get(MUMBLE_INTERFACE_KEY);
		if (!(interfaceObj instanceof Map<?, ?> interfaceMap)) {
			return false;
		}
		return containsKeyIgnoreCase((Map<String, Object>) interfaceMap, "*");
	}

	private boolean isJoinUsingTableFunctionSource(Map<String, Object> sourceResult) {
		if (sourceResult.containsKey(MUMBLE_TABLE_FUNCTION_KEY)) {
			return true;
		}
		Object tableObj = sourceResult.get(MUMBLE_TABLE_KEY);
		if (tableObj instanceof Map<?, ?> tableMapObj
				&& ((Map<String, Object>) tableMapObj).containsKey(MUMBLE_TABLE_FUNCTION_KEY)) {
			return true;
		}
		String canonicalRef = resolveJoinUsingCanonicalSourceRef(sourceResult);
		return canonicalRef != null && isTableFunctionSourceReference(canonicalRef);
	}

	/**
	 * Tuple substitutions, Jinja table refs, and table functions cannot be schema-checked like
	 * physical tables; treat them like direct tables for JOIN USING acceptance.
	 */
	private boolean isJoinUsingPermissiveTableLikeSource(Map<String, Object> sourceResult) {
		return isJoinUsingTableFunctionSource(sourceResult)
				|| isJoinUsingTupleSubstitutionJoinSource(sourceResult)
				|| isJoinUsingJinjaTableJoinSource(sourceResult);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> extractJoinUsingSubstitutionPayload(Map<String, Object> sourceResult) {
		if (sourceResult == null) {
			return null;
		}
		Object tableObj = sourceResult.get(MUMBLE_TABLE_KEY);
		if (tableObj instanceof Map<?, ?> tableMapObj) {
			Object substitutionObj = ((Map<String, Object>) tableMapObj).get(MUMBLE_SUBSTITUTION_KEY);
			if (substitutionObj instanceof Map<?, ?> substitutionMap) {
				return (Map<String, Object>) substitutionMap;
			}
		}
		Object directSubstitution = sourceResult.get(MUMBLE_SUBSTITUTION_KEY);
		if (directSubstitution instanceof Map<?, ?> substitutionMap) {
			return (Map<String, Object>) substitutionMap;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private boolean isJoinUsingTupleSubstitutionJoinSource(Map<String, Object> sourceResult) {
		Map<String, Object> substitution = extractJoinUsingSubstitutionPayload(sourceResult);
		if (substitution == null) {
			return false;
		}
		Object typeObj = substitution.get(MUMBLE_TYPE_KEY);
		return typeObj != null && MUMBLE_TUPLE_KEY.equals(typeObj.toString());
	}

	@SuppressWarnings("unchecked")
	private boolean isJoinUsingJinjaTableJoinSource(Map<String, Object> sourceResult) {
		Map<String, Object> substitution = extractJoinUsingSubstitutionPayload(sourceResult);
		if (substitution == null) {
			return false;
		}
		Object partsObj = substitution.get(MUMBLE_PARTS_KEY);
		if (partsObj instanceof Map<?, ?> partsMap
				&& partsMap.containsKey(MUMBLE_JINJA_TABLE_KEY)) {
			return true;
		}
		Object nameObj = substitution.get(MUMBLE_NAME_KEY);
		return nameObj instanceof String name && name.contains("{{");
	}

	public String resolveJoinUsingPhysicalDictionaryKey(
			Map<String, Object> sourceResult,
			String qualifiedSourceRef) {
		if (qualifiedSourceRef == null || qualifiedSourceRef.isBlank()) {
			return qualifiedSourceRef;
		}
		Map<String, Object> substitution = extractJoinUsingSubstitutionPayload(sourceResult);
		if (substitution != null) {
			Object nameObj = substitution.get(MUMBLE_NAME_KEY);
			if (nameObj instanceof String substitutionName && !substitutionName.isBlank()) {
				return substitutionName;
			}
		}
		Object aliasMapObj = walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY);
		if (aliasMapObj instanceof Map<?, ?> aliasMap && aliasMap.containsKey(qualifiedSourceRef)) {
			Object mapped = aliasMap.get(qualifiedSourceRef);
			if (mapped instanceof String mappedRef && mappedRef.startsWith("<")) {
				return mappedRef;
			}
		}
		return qualifiedSourceRef;
	}

	@SuppressWarnings("unchecked")
	private boolean isJoinUsingCompositeOperand(Map<String, Object> sourceResult) {
		Object joinObj = sourceResult.get(MUMBLE_JOIN_KEY);
		return joinObj instanceof Map<?, ?>;
	}

	@SuppressWarnings("unchecked")
	private boolean joinUsingCompositeOperandAcceptsColumn(
			Map<String, Object> sourceResult,
			String columnName) {
		Object joinObj = sourceResult.get(MUMBLE_JOIN_KEY);
		if (!(joinObj instanceof Map<?, ?> joinMapObj)) {
			return false;
		}
		Map<String, Object> joinMap = (Map<String, Object>) joinMapObj;
		Object leftObj = joinMap.get("1");
		Object rightObj = joinMap.get("3");
		Map<String, Object> left = coerceJoinUsingOperandMapForAcceptance(leftObj);
		Map<String, Object> right = coerceJoinUsingOperandMapForAcceptance(rightObj);
		boolean leftAccepts = left != null && joinUsingSourceAcceptsColumn(left, columnName);
		boolean rightAccepts = right != null && joinUsingSourceAcceptsColumn(right, columnName);
		return leftAccepts || rightAccepts;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> coerceJoinUsingOperandMapForAcceptance(Object operandObj) {
		if (!(operandObj instanceof Map<?, ?> operandMapObj)) {
			return null;
		}
		Map<String, Object> operandMap = (Map<String, Object>) operandMapObj;
		if (operandMap.containsKey(MUMBLE_TABLE_KEY)
				|| operandMap.containsKey(MUMBLE_VALUES_KEY)) {
			return operandMap;
		}
		Object joinObj = operandMap.get(MUMBLE_JOIN_KEY);
		if (joinObj instanceof Map<?, ?>) {
			return operandMap;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private String resolveJoinUsingCanonicalSourceRef(Map<String, Object> sourceResult) {
		String sourceRef = resolveJoinUsingSourceReference(sourceResult);
		if (sourceRef == null || sourceRef.isBlank()) {
			return null;
		}
		Object aliasMapObj = walker.symbolTable.get(MUMBLE_TABLE_ALIAS_KEY);
		if (aliasMapObj instanceof Map<?, ?> aliasMap && aliasMap.containsKey(sourceRef)) {
			Object aliasTarget = aliasMap.get(sourceRef);
			if (aliasTarget instanceof String aliasTargetRef && !aliasTargetRef.isBlank()) {
				return aliasTargetRef;
			}
		}
		return sourceRef;
	}

	@SuppressWarnings("unchecked")
	private boolean isDirectTableJoinSource(Map<String, Object> sourceResult) {
		Object tableObj = sourceResult.get(MUMBLE_TABLE_KEY);
		if (!(tableObj instanceof Map<?, ?> tableMapObj)) {
			return false;
		}
		Object tableNameObj = ((Map<String, Object>) tableMapObj).get(MUMBLE_TABLE_KEY);
		return tableNameObj instanceof String tableName && !tableName.isBlank();
	}

	@SuppressWarnings("unchecked")
	private String resolveJoinUsingSourceReference(Map<String, Object> sourceResult) {
		Object tableObj = sourceResult.get(MUMBLE_TABLE_KEY);
		if (tableObj instanceof Map<?, ?> tableMapObj) {
			Object aliasObj = ((Map<String, Object>) tableMapObj).get(MUMBLE_ALIAS_KEY);
			if (aliasObj instanceof String alias && !alias.isBlank()) {
				return alias;
			}
			String tableRef = getQualifiedTableReference((Map<String, Object>) tableMapObj);
			if (tableRef != null && !tableRef.isBlank()) {
				return tableRef;
			}
			if (((Map<String, Object>) tableMapObj).containsKey(MUMBLE_QUERY_KEY)) {
				return getSubqueryReferenceKey(walker.symbolTable);
			}
		}
		Object valuesObj = sourceResult.get(MUMBLE_VALUES_KEY);
		if (valuesObj instanceof Map<?, ?> valuesMapObj) {
			Object aliasObj = ((Map<String, Object>) valuesMapObj).get(MUMBLE_ALIAS_KEY);
			if (aliasObj instanceof String alias && !alias.isBlank()) {
				return alias;
			}
		}
		return null;
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
	 * query scope into that scope's local {@code query_dictionary} (handoff sync).
	 * <p>
	 * <b>Two-store contract:</b> phase-1 publish leaves an immutable-ish snapshot on
	 * {@code def_queryN.query_dictionary}; phase-2 external usage (parent qualified refs on query
	 * aliases) enriches the <em>global</em> live index afterward. This method copies those
	 * phase-2 tokens onto nested {@code def_*} payloads at client handoff so symbol-table goldens
	 * and consumers that only read the tree still see them.
	 * <p>
	 * Phase <b>19.4</b> goal is to retire this repair (global may legitimately be richer than the
	 * embedded snapshot). A no-sync probe failed <b>125/239</b> smoketest gate methods — all
	 * {@code Symbol Table is wrong} golden diffs (missing phase-2 tokens on nested
	 * {@code query_dictionary} entries). Do <b>not</b> remove call sites without human acceptance
	 * of that golden churn (Phase 19 stability rule).
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
		PREDICAND,
		QUANTIFIED
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
			case QUANTIFIED -> recordDependentQueryReference(
					predicateFrameSymbols, MUMBLE_QUANTIFIED_KEY, liveQueryRefKey);
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
		dependentQueryContextStack.push(SqlGrammarContextClassifier.inferDependentQueryContext(ctx));
	}

	public void popDependentQueryContextForFrame() {
		if (!dependentQueryContextStack.isEmpty()) {
			dependentQueryContextStack.pop();
		}
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
		scopePayload.remove(TEMP_PENDING_UNION_SETOP_FOR_NEXT_PARTICIPANT_KEY);
		scopePayload.remove(TEMP_PENDING_INTERSECT_SETOP_FOR_NEXT_PARTICIPANT_KEY);
		scopePayload.remove(TEMP_SELECT_LIST_OUTPUT_ALIAS_SOURCE_ORDER_KEY);
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
		scopePayload.remove(TEMP_INSERT_TARGET_INTERFACE_KEY);
		scopePayload.remove(TEMP_INSERT_TARGET_TABLE_REF_KEY);
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
		stripIngressSiteFromPublishedScopePayload(scopePayload);
		for (Object nestedValue : scopePayload.values()) {
			if (nestedValue instanceof HashMap<?, ?> nestedScopeObj) {
				stripWalkTimeKeysFromPublishedScope((HashMap<String, Object>) nestedScopeObj);
			}
		}
	}

	/** Strip walk-time {@code ingress_site} from published {@code unresolved_column} bucket entries. */
	@SuppressWarnings("unchecked")
	private void stripIngressSiteFromPublishedScopePayload(HashMap<String, Object> scopePayload) {
		if (scopePayload == null || scopePayload.isEmpty()) {
			return;
		}
		stripIngressSiteFromUnresolvedColumnMap(scopePayload.get(MUMBLE_UNRESOLVED_COLUMN_KEY));
		stripIngressSiteFromUnresolvedColumnMap(scopePayload.get(MUMBLE_LHS_UNRESOLVED_COLUMNS_KEY));
		Object tableDictionaryObj = scopePayload.get(MUMBLE_TABLE_DICTIONARY_KEY);
		if (tableDictionaryObj instanceof Map<?, ?> tableDictionaryMap) {
			stripIngressSiteFromUnresolvedColumnMap(
					tableDictionaryMap.get(MUMBLE_UNRESOLVED_COLUMN_KEY));
		}
	}

	@SuppressWarnings("unchecked")
	private void stripIngressSiteFromUnresolvedColumnMap(Object unresolvedColumnMapObj) {
		if (!(unresolvedColumnMapObj instanceof Map<?, ?> unresolvedColumnMap)) {
			return;
		}
		for (Object entryValue : ((Map<String, Object>) unresolvedColumnMap).values()) {
			stripIngressSiteFromUnresolvedEntryInPlace(entryValue);
		}
	}

	@SuppressWarnings("unchecked")
	private void stripIngressSiteFromUnresolvedEntryInPlace(Object unresolvedEntry) {
		if (!(unresolvedEntry instanceof Map<?, ?> entryMap)) {
			return;
		}
		((Map<String, Object>) entryMap).remove(MUMBLE_UNRESOLVED_INGRESS_SITE_KEY);
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
