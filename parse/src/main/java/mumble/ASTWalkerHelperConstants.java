package mumble;

/*
 * These constants are teh standards used by the AST Walker Helper when needed to track certain
 * elements of the AST tree. Each Grammar can substitute their own constants for these
 * to provide a consistent set of keys for the AST Walker Helper to use when traversing the trees
 * generated from that Grammar.
 */
public final class ASTWalkerHelperConstants {

	public static final String ASTWALKER_QUERY_KEY = "query";
	public static final String ASTWALKER_INTERSECT_KEY = "intersect";
	public static final String ASTWALKER_UNION_KEY = "union";
	public static final String ASTWALKER_SUBSTITUTION_KEY = "substitution";
	public static final String ASTWALKER_TYPE_KEY = "type";
	public static final String ASTWALKER_COLUMN_KEY = "column";
	public static final String ASTWALKER_UNKNOWN_KEY = "unknown";
	public static final String ASTWALKER_VALUES_KEY = "values";
	public static final String ASTWALKER_INSERT_KEY = "insert";
	public static final String ASTWALKER_UPDATE_KEY = "update";

	public static final String ASTWALKER_RULE_TYPE_KEY = "Type";
	
}
