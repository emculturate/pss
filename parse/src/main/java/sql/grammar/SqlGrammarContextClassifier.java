package sql.grammar;

import org.antlr.v4.runtime.ParserRuleContext;

import static mumble.MumbleConstants.MUMBLE_DEPENDENT_QUERY_CONTEXT_FILTERS;
import static mumble.MumbleConstants.MUMBLE_DEPENDENT_QUERY_CONTEXT_GROUP_BY;
import static mumble.MumbleConstants.MUMBLE_DEPENDENT_QUERY_CONTEXT_INTERFACE;
import static mumble.MumbleConstants.MUMBLE_DEPENDENT_QUERY_CONTEXT_ORDER_BY;

import sql.SQLSelectParserParser;

/**
 * Shared ANTLR ancestor-walk rules for SQL grammatical clause context.
 * Consumed by substitution-variable typing ({@link astwalkers.SqlASTWalkerHelper})
 * and dependent-query context recording ({@link sql.symboltree.SqlParseSymbolTreeHelper}).
 */
public final class SqlGrammarContextClassifier {

	private SqlGrammarContextClassifier() {
	}

	public static boolean hasAncestorRule(ParserRuleContext ctx, int ruleIndex) {
		for (ParserRuleContext walk = ctx.getParent(); walk != null; walk = walk.getParent()) {
			if (walk.getRuleIndex() == ruleIndex) {
				return true;
			}
		}
		return false;
	}

	/** Filter boolean context: WHERE / HAVING / QUALIFY / nested {@code search_condition}. */
	public static boolean isUnderFilterBooleanContext(ParserRuleContext ctx) {
		return hasAncestorRule(ctx, SQLSelectParserParser.RULE_search_condition)
				|| hasAncestorRule(ctx, SQLSelectParserParser.RULE_having_clause)
				|| hasAncestorRule(ctx, SQLSelectParserParser.RULE_qualify_clause);
	}

	public static boolean isUnderPredicandSubqueryFrame(ParserRuleContext ctx) {
		return hasAncestorRule(ctx, SQLSelectParserParser.RULE_predicand_subquery);
	}

	/** Dependent-query recording context for a subquery reference inside a predicate frame. */
	public static String inferDependentQueryContext(ParserRuleContext ctx) {
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

	/** Arithmetic (+, -, *, /) operands are predicands in every clause. */
	public static boolean isArithmeticOperatorRule(int ruleIndex) {
		return ruleIndex == SQLSelectParserParser.RULE_additive_expression
				|| ruleIndex == SQLSelectParserParser.RULE_multiplicative_expression
				|| ruleIndex == SQLSelectParserParser.RULE_common_value_expression;
	}

	/** Definite value-position operators (comparison operands, etc.). */
	public static boolean isStrongSubstitutionPredicandOperatorRule(int ruleIndex) {
		return ruleIndex == SQLSelectParserParser.RULE_comparison_predicate
				|| ruleIndex == SQLSelectParserParser.RULE_between_predicate
				|| ruleIndex == SQLSelectParserParser.RULE_like_any_predicate
				|| ruleIndex == SQLSelectParserParser.RULE_null_predicate
				|| ruleIndex == SQLSelectParserParser.RULE_in_predicate
				|| ruleIndex == SQLSelectParserParser.RULE_case_result
				|| ruleIndex == SQLSelectParserParser.RULE_when_value_clause
				|| ruleIndex == SQLSelectParserParser.RULE_aggregate_function
				|| ruleIndex == SQLSelectParserParser.RULE_trim_operands
				|| ruleIndex == SQLSelectParserParser.RULE_sql_argument_list
				|| ruleIndex == SQLSelectParserParser.RULE_row_value_predicand;
	}

	/** Boolean-composition contexts: AND/OR/NOT, bare filter substitution, standalone condition subs. */
	public static boolean isSubstitutionConditionOperatorRule(int ruleIndex) {
		return ruleIndex == SQLSelectParserParser.RULE_or_predicate
				|| ruleIndex == SQLSelectParserParser.RULE_and_predicate
				|| ruleIndex == SQLSelectParserParser.RULE_negative_predicate
				|| ruleIndex == SQLSelectParserParser.RULE_exists_predicate
				|| ruleIndex == SQLSelectParserParser.RULE_searched_when_clause
				|| ruleIndex == SQLSelectParserParser.RULE_search_condition
				|| ruleIndex == SQLSelectParserParser.RULE_condition_value;
	}
}
