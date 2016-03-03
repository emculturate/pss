package sql.walker;

import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import sql.SQLSelectParserParser;
import sql.SQLSelectParserBaseListener;

public class SqlParseEventWalker extends SQLSelectParserBaseListener {

	/**
	 * Collect components of parse tree
	 */
	HashMap<String, Object> collector = new HashMap<String, Object>();
	/**
	 * Depth of token stack
	 */
	HashMap<Integer, Integer> stackLevel = new HashMap<Integer, Integer>();

	// Extra-Grammar Identifiers

	// Constructors
	public SqlParseEventWalker() {
		super();
	}

	// Getters and Setters

	public HashMap<String, Object> getCollector() {
		return collector;
	}

	public void setCollector(HashMap<String, Object> collector) {
		this.collector = collector;
	}

	// Other Methods

	private Integer pushStack(Integer ruleIndex) {
		Integer context = stackLevel.get(ruleIndex);
		Integer newLevel;
		if (context == null) {
			newLevel = 1;
		} else {
			newLevel = context + 1;
		}
		stackLevel.put(ruleIndex, newLevel);
		System.out.println("PUSH - " + makeMapIndex(ruleIndex, newLevel) + ": " + stackLevel);
		return newLevel;
	}

	private Integer popStack(Integer ruleIndex) {
		Integer level = stackLevel.get(ruleIndex) - 1;
		if (level == 0) {
			stackLevel.remove(ruleIndex);
		}
		stackLevel.put(ruleIndex, level);
		System.out.println("POP - " + makeMapIndex(ruleIndex, level) + ": " + stackLevel);
		return level;
	}

	/**
	 * Add level map to collection by ruleIndex and stackLevel
	 * 
	 * @param ruleIndex
	 * @param stackLevel
	 * @param hashMap
	 */
	private void collect(int ruleIndex, Integer stackLevel, Object item) {
		collector.put(makeMapIndex(ruleIndex, stackLevel), item);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getNodeMap(int ruleIndex, Integer stackLevel) {
		String mapIdx = makeMapIndex(ruleIndex, stackLevel);
		Map<String, Object> idMap = (Map<String, Object>) collector.get(mapIdx);
		return idMap;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> removeNodeMap(int ruleIndex, Integer stackLevel) {
		String mapIdx = makeMapIndex(ruleIndex, stackLevel);
		Map<String, Object> idMap = (Map<String, Object>) collector.get(mapIdx);
		collector.remove(mapIdx);
		return idMap;
	}

	private String makeMapIndex(int ruleIndex, Integer stackIndex) {
		return ruleIndex + "_" + stackIndex;
	}

	// Listener overrides

	@Override
	public void enterParenthesized_value_expression(
			@NotNull SQLSelectParserParser.Parenthesized_value_expressionContext ctx) {
	}

	@Override
	public void exitParenthesized_value_expression(
			@NotNull SQLSelectParserParser.Parenthesized_value_expressionContext ctx) {
	}

	@Override
	public void enterNamed_columns_join(@NotNull SQLSelectParserParser.Named_columns_joinContext ctx) {
	}

	@Override
	public void exitNamed_columns_join(@NotNull SQLSelectParserParser.Named_columns_joinContext ctx) {
	}

	@Override
	public void enterColumn_reference_list(@NotNull SQLSelectParserParser.Column_reference_listContext ctx) {
	}

	@Override
	public void exitColumn_reference_list(@NotNull SQLSelectParserParser.Column_reference_listContext ctx) {
	}

	@Override
	public void enterPredicate(@NotNull SQLSelectParserParser.PredicateContext ctx) {
	}

	@Override
	public void exitPredicate(@NotNull SQLSelectParserParser.PredicateContext ctx) {
	}

	@Override
	public void enterTrim_function(@NotNull SQLSelectParserParser.Trim_functionContext ctx) {
	}

	@Override
	public void exitTrim_function(@NotNull SQLSelectParserParser.Trim_functionContext ctx) {
	}

	@Override
	public void enterIn_predicate(@NotNull SQLSelectParserParser.In_predicateContext ctx) {
	}

	@Override
	public void exitIn_predicate(@NotNull SQLSelectParserParser.In_predicateContext ctx) {
	}

	@Override
	public void enterRegex_matcher(@NotNull SQLSelectParserParser.Regex_matcherContext ctx) {
	}

	@Override
	public void exitRegex_matcher(@NotNull SQLSelectParserParser.Regex_matcherContext ctx) {
	}

	@Override
	public void enterTime_zone_field(@NotNull SQLSelectParserParser.Time_zone_fieldContext ctx) {
	}

	@Override
	public void exitTime_zone_field(@NotNull SQLSelectParserParser.Time_zone_fieldContext ctx) {
	}

	@Override
	public void enterType_length(@NotNull SQLSelectParserParser.Type_lengthContext ctx) {
	}

	@Override
	public void exitType_length(@NotNull SQLSelectParserParser.Type_lengthContext ctx) {
	}

	@Override
	public void enterExtract_expression(@NotNull SQLSelectParserParser.Extract_expressionContext ctx) {
	}

	@Override
	public void exitExtract_expression(@NotNull SQLSelectParserParser.Extract_expressionContext ctx) {
	}

	@Override
	public void enterCube_list(@NotNull SQLSelectParserParser.Cube_listContext ctx) {
	}

	@Override
	public void exitCube_list(@NotNull SQLSelectParserParser.Cube_listContext ctx) {
	}

	@Override
	public void enterExtract_field(@NotNull SQLSelectParserParser.Extract_fieldContext ctx) {
	}

	@Override
	public void exitExtract_field(@NotNull SQLSelectParserParser.Extract_fieldContext ctx) {
	}

	@Override
	public void enterSort_specifier(@NotNull SQLSelectParserParser.Sort_specifierContext ctx) {
	}

	@Override
	public void exitSort_specifier(@NotNull SQLSelectParserParser.Sort_specifierContext ctx) {
	}

	@Override
	public void enterString_value_function(@NotNull SQLSelectParserParser.String_value_functionContext ctx) {
	}

	@Override
	public void exitString_value_function(@NotNull SQLSelectParserParser.String_value_functionContext ctx) {
	}

	@Override
	public void enterUnsigned_value_specification(
			@NotNull SQLSelectParserParser.Unsigned_value_specificationContext ctx) {
	}

	@Override
	public void exitUnsigned_value_specification(
			@NotNull SQLSelectParserParser.Unsigned_value_specificationContext ctx) {
	}

	@Override
	public void enterGeneral_set_function(@NotNull SQLSelectParserParser.General_set_functionContext ctx) {
	}

	@Override
	public void exitGeneral_set_function(@NotNull SQLSelectParserParser.General_set_functionContext ctx) {
	}

	@Override
	public void enterTerm(@NotNull SQLSelectParserParser.TermContext ctx) {
	}

	@Override
	public void exitTerm(@NotNull SQLSelectParserParser.TermContext ctx) {
	}

	@Override
	public void enterIn_predicate_value(@NotNull SQLSelectParserParser.In_predicate_valueContext ctx) {
	}

	@Override
	public void exitIn_predicate_value(@NotNull SQLSelectParserParser.In_predicate_valueContext ctx) {
	}

	@Override
	public void enterHaving_clause(@NotNull SQLSelectParserParser.Having_clauseContext ctx) {
	}

	@Override
	public void exitHaving_clause(@NotNull SQLSelectParserParser.Having_clauseContext ctx) {
	}

	@Override
	public void enterRow_value_expression(@NotNull SQLSelectParserParser.Row_value_expressionContext ctx) {
	}

	@Override
	public void exitRow_value_expression(@NotNull SQLSelectParserParser.Row_value_expressionContext ctx) {
	}

	@Override
	public void enterTable_or_query_name(@NotNull SQLSelectParserParser.Table_or_query_nameContext ctx) {
	}

	@Override
	public void exitTable_or_query_name(@NotNull SQLSelectParserParser.Table_or_query_nameContext ctx) {
	}

	@Override
	public void enterCast_operand(@NotNull SQLSelectParserParser.Cast_operandContext ctx) {
	}

	@Override
	public void exitCast_operand(@NotNull SQLSelectParserParser.Cast_operandContext ctx) {
	}

	@Override
	public void enterPattern_matcher(@NotNull SQLSelectParserParser.Pattern_matcherContext ctx) {
	}

	@Override
	public void exitPattern_matcher(@NotNull SQLSelectParserParser.Pattern_matcherContext ctx) {
	}

	@Override
	public void enterNon_second_primary_datetime_field(
			@NotNull SQLSelectParserParser.Non_second_primary_datetime_fieldContext ctx) {
	}

	@Override
	public void exitNon_second_primary_datetime_field(
			@NotNull SQLSelectParserParser.Non_second_primary_datetime_fieldContext ctx) {
	}

	@Override
	public void enterGrouping_operation(@NotNull SQLSelectParserParser.Grouping_operationContext ctx) {
	}

	@Override
	public void exitGrouping_operation(@NotNull SQLSelectParserParser.Grouping_operationContext ctx) {
	}

	@Override
	public void enterSearched_when_clause(@NotNull SQLSelectParserParser.Searched_when_clauseContext ctx) {
	}

	@Override
	public void exitSearched_when_clause(@NotNull SQLSelectParserParser.Searched_when_clauseContext ctx) {
	}

	@Override
	public void enterOrder_specification(@NotNull SQLSelectParserParser.Order_specificationContext ctx) {
	}

	@Override
	public void exitOrder_specification(@NotNull SQLSelectParserParser.Order_specificationContext ctx) {
	}

	@Override
	public void enterNetwork_type(@NotNull SQLSelectParserParser.Network_typeContext ctx) {
	}

	@Override
	public void exitNetwork_type(@NotNull SQLSelectParserParser.Network_typeContext ctx) {
	}

	@Override
	public void enterAggregate_function(@NotNull SQLSelectParserParser.Aggregate_functionContext ctx) {
	}

	@Override
	public void exitAggregate_function(@NotNull SQLSelectParserParser.Aggregate_functionContext ctx) {
	}

	@Override
	public void enterColumn_name_list(@NotNull SQLSelectParserParser.Column_name_listContext ctx) {
	}

	@Override
	public void exitColumn_name_list(@NotNull SQLSelectParserParser.Column_name_listContext ctx) {
	}

	@Override
	public void enterAs_clause(@NotNull SQLSelectParserParser.As_clauseContext ctx) {
		System.out.println(ctx.getText());
	}

	@Override
	public void exitAs_clause(@NotNull SQLSelectParserParser.As_clauseContext ctx) {
	}

	@Override
	public void enterPrecision_param(@NotNull SQLSelectParserParser.Precision_paramContext ctx) {
	}

	@Override
	public void exitPrecision_param(@NotNull SQLSelectParserParser.Precision_paramContext ctx) {
	}

	@Override
	public void enterQuery_primary(@NotNull SQLSelectParserParser.Query_primaryContext ctx) {
	}

	@Override
	public void exitQuery_primary(@NotNull SQLSelectParserParser.Query_primaryContext ctx) {
	}

	@Override
	public void enterOrdinary_grouping_set_list(@NotNull SQLSelectParserParser.Ordinary_grouping_set_listContext ctx) {
	}

	@Override
	public void exitOrdinary_grouping_set_list(@NotNull SQLSelectParserParser.Ordinary_grouping_set_listContext ctx) {
	}

	@Override
	public void enterIs_clause(@NotNull SQLSelectParserParser.Is_clauseContext ctx) {
	}

	@Override
	public void exitIs_clause(@NotNull SQLSelectParserParser.Is_clauseContext ctx) {
	}

	@Override
	public void enterOr_predicate(@NotNull SQLSelectParserParser.Or_predicateContext ctx) {
	}

	@Override
	public void exitOr_predicate(@NotNull SQLSelectParserParser.Or_predicateContext ctx) {
	}

	@Override
	public void enterNonreserved_keywords(@NotNull SQLSelectParserParser.Nonreserved_keywordsContext ctx) {
	}

	@Override
	public void exitNonreserved_keywords(@NotNull SQLSelectParserParser.Nonreserved_keywordsContext ctx) {
	}

	@Override
	public void enterDatetime_literal(@NotNull SQLSelectParserParser.Datetime_literalContext ctx) {
	}

	@Override
	public void exitDatetime_literal(@NotNull SQLSelectParserParser.Datetime_literalContext ctx) {
	}

	@Override
	public void enterUnsigned_numeric_literal(@NotNull SQLSelectParserParser.Unsigned_numeric_literalContext ctx) {
	}

	@Override
	public void exitUnsigned_numeric_literal(@NotNull SQLSelectParserParser.Unsigned_numeric_literalContext ctx) {
	}

	@Override
	public void enterRow_value_predicand(@NotNull SQLSelectParserParser.Row_value_predicandContext ctx) {
	}

	@Override
	public void exitRow_value_predicand(@NotNull SQLSelectParserParser.Row_value_predicandContext ctx) {
	}

	@Override
	public void enterSet_function_specification(@NotNull SQLSelectParserParser.Set_function_specificationContext ctx) {
	}

	@Override
	public void exitSet_function_specification(@NotNull SQLSelectParserParser.Set_function_specificationContext ctx) {
	}

	@Override
	public void enterSelect_sublist(@NotNull SQLSelectParserParser.Select_sublistContext ctx) {
	}

	@Override
	public void exitSelect_sublist(@NotNull SQLSelectParserParser.Select_sublistContext ctx) {
	}

	@Override
	public void enterSort_specifier_list(@NotNull SQLSelectParserParser.Sort_specifier_listContext ctx) {
	}

	@Override
	public void exitSort_specifier_list(@NotNull SQLSelectParserParser.Sort_specifier_listContext ctx) {
	}

	@Override
	public void enterSearch_condition(@NotNull SQLSelectParserParser.Search_conditionContext ctx) {
	}

	@Override
	public void exitSearch_condition(@NotNull SQLSelectParserParser.Search_conditionContext ctx) {
	}

	@Override
	public void enterIn_value_list(@NotNull SQLSelectParserParser.In_value_listContext ctx) {
	}

	@Override
	public void exitIn_value_list(@NotNull SQLSelectParserParser.In_value_listContext ctx) {
	}

	@Override
	public void enterNonparenthesized_value_expression_primary(
			@NotNull SQLSelectParserParser.Nonparenthesized_value_expression_primaryContext ctx) {
	}

	@Override
	public void exitNonparenthesized_value_expression_primary(
			@NotNull SQLSelectParserParser.Nonparenthesized_value_expression_primaryContext ctx) {
	}

	@Override
	public void enterSome(@NotNull SQLSelectParserParser.SomeContext ctx) {
	}

	@Override
	public void exitSome(@NotNull SQLSelectParserParser.SomeContext ctx) {
	}

	@Override
	public void enterBoolean_primary(@NotNull SQLSelectParserParser.Boolean_primaryContext ctx) {
	}

	@Override
	public void exitBoolean_primary(@NotNull SQLSelectParserParser.Boolean_primaryContext ctx) {
	}

	@Override
	public void enterJoin_specification(@NotNull SQLSelectParserParser.Join_specificationContext ctx) {
	}

	@Override
	public void exitJoin_specification(@NotNull SQLSelectParserParser.Join_specificationContext ctx) {
	}

	@Override
	public void enterExact_numeric_type(@NotNull SQLSelectParserParser.Exact_numeric_typeContext ctx) {
	}

	@Override
	public void exitExact_numeric_type(@NotNull SQLSelectParserParser.Exact_numeric_typeContext ctx) {
	}

	@Override
	public void enterNegativable_matcher(@NotNull SQLSelectParserParser.Negativable_matcherContext ctx) {
	}

	@Override
	public void exitNegativable_matcher(@NotNull SQLSelectParserParser.Negativable_matcherContext ctx) {
	}

	@Override
	public void enterCharacter_string_type(@NotNull SQLSelectParserParser.Character_string_typeContext ctx) {
	}

	@Override
	public void exitCharacter_string_type(@NotNull SQLSelectParserParser.Character_string_typeContext ctx) {
	}

	@Override
	public void enterSelect_list(@NotNull SQLSelectParserParser.Select_listContext ctx) {
	}

	@Override
	public void exitSelect_list(@NotNull SQLSelectParserParser.Select_listContext ctx) {
	}

	@Override
	public void enterFactor(@NotNull SQLSelectParserParser.FactorContext ctx) {
	}

	@Override
	public void exitFactor(@NotNull SQLSelectParserParser.FactorContext ctx) {
	}

	@Override
	public void enterDate_literal(@NotNull SQLSelectParserParser.Date_literalContext ctx) {
	}

	@Override
	public void exitDate_literal(@NotNull SQLSelectParserParser.Date_literalContext ctx) {
	}

	@Override
	public void enterBoolean_type(@NotNull SQLSelectParserParser.Boolean_typeContext ctx) {
	}

	@Override
	public void exitBoolean_type(@NotNull SQLSelectParserParser.Boolean_typeContext ctx) {
	}

	@Override
	public void enterLimit_clause(@NotNull SQLSelectParserParser.Limit_clauseContext ctx) {
	}

	@Override
	public void exitLimit_clause(@NotNull SQLSelectParserParser.Limit_clauseContext ctx) {
	}

	@Override
	public void enterTable_expression(@NotNull SQLSelectParserParser.Table_expressionContext ctx) {
	}

	@Override
	public void exitTable_expression(@NotNull SQLSelectParserParser.Table_expressionContext ctx) {
	}

	@Override
	public void enterJoin_condition(@NotNull SQLSelectParserParser.Join_conditionContext ctx) {
	}

	@Override
	public void exitJoin_condition(@NotNull SQLSelectParserParser.Join_conditionContext ctx) {
	}

	@Override
	public void enterRow_value_predicand_list(@NotNull SQLSelectParserParser.Row_value_predicand_listContext ctx) {
	}

	@Override
	public void exitRow_value_predicand_list(@NotNull SQLSelectParserParser.Row_value_predicand_listContext ctx) {
	}

	@Override
	public void enterFunction_name(@NotNull SQLSelectParserParser.Function_nameContext ctx) {
	}

	@Override
	public void exitFunction_name(@NotNull SQLSelectParserParser.Function_nameContext ctx) {
	}

	@Override
	public void enterData_type(@NotNull SQLSelectParserParser.Data_typeContext ctx) {
	}

	@Override
	public void exitData_type(@NotNull SQLSelectParserParser.Data_typeContext ctx) {
	}

	@Override
	public void enterGrouping_element_list(@NotNull SQLSelectParserParser.Grouping_element_listContext ctx) {
	}

	@Override
	public void exitGrouping_element_list(@NotNull SQLSelectParserParser.Grouping_element_listContext ctx) {
	}

	@Override
	public void enterCharacter_primary(@NotNull SQLSelectParserParser.Character_primaryContext ctx) {
	}

	@Override
	public void exitCharacter_primary(@NotNull SQLSelectParserParser.Character_primaryContext ctx) {
	}

	@Override
	public void enterSql_argument_list(@NotNull SQLSelectParserParser.Sql_argument_listContext ctx) {
	}

	@Override
	public void exitSql_argument_list(@NotNull SQLSelectParserParser.Sql_argument_listContext ctx) {
	}

	@Override
	public void enterExtract_source(@NotNull SQLSelectParserParser.Extract_sourceContext ctx) {
	}

	@Override
	public void exitExtract_source(@NotNull SQLSelectParserParser.Extract_sourceContext ctx) {
	}

	@Override
	public void enterNumeric_type(@NotNull SQLSelectParserParser.Numeric_typeContext ctx) {
	}

	@Override
	public void exitNumeric_type(@NotNull SQLSelectParserParser.Numeric_typeContext ctx) {
	}

	@Override
	public void enterQualified_asterisk(@NotNull SQLSelectParserParser.Qualified_asteriskContext ctx) {
	}

	@Override
	public void exitQualified_asterisk(@NotNull SQLSelectParserParser.Qualified_asteriskContext ctx) {
	}

	@Override
	public void enterIdentifier(@NotNull SQLSelectParserParser.IdentifierContext ctx) {
	}

	@Override
	public void exitIdentifier(@NotNull SQLSelectParserParser.IdentifierContext ctx) {
	}

	@Override
	public void enterBit_type(@NotNull SQLSelectParserParser.Bit_typeContext ctx) {
	}

	@Override
	public void exitBit_type(@NotNull SQLSelectParserParser.Bit_typeContext ctx) {
	}

	@Override
	public void enterDatetime_type(@NotNull SQLSelectParserParser.Datetime_typeContext ctx) {
	}

	@Override
	public void exitDatetime_type(@NotNull SQLSelectParserParser.Datetime_typeContext ctx) {
	}

	@Override
	public void enterBetween_predicate_part_2(@NotNull SQLSelectParserParser.Between_predicate_part_2Context ctx) {
	}

	@Override
	public void exitBetween_predicate_part_2(@NotNull SQLSelectParserParser.Between_predicate_part_2Context ctx) {
	}

	@Override
	public void enterTime_literal(@NotNull SQLSelectParserParser.Time_literalContext ctx) {
	}

	@Override
	public void exitTime_literal(@NotNull SQLSelectParserParser.Time_literalContext ctx) {
	}

	@Override
	public void enterOrderby_clause(@NotNull SQLSelectParserParser.Orderby_clauseContext ctx) {
	}

	@Override
	public void exitOrderby_clause(@NotNull SQLSelectParserParser.Orderby_clauseContext ctx) {
	}

	@Override
	public void enterGrouping_element(@NotNull SQLSelectParserParser.Grouping_elementContext ctx) {
	}

	@Override
	public void exitGrouping_element(@NotNull SQLSelectParserParser.Grouping_elementContext ctx) {
	}

	@Override
	public void enterOrdinary_grouping_set(@NotNull SQLSelectParserParser.Ordinary_grouping_setContext ctx) {
	}

	@Override
	public void exitOrdinary_grouping_set(@NotNull SQLSelectParserParser.Ordinary_grouping_setContext ctx) {
	}

	@Override
	public void enterQuantified_comparison_predicate(
			@NotNull SQLSelectParserParser.Quantified_comparison_predicateContext ctx) {
	}

	@Override
	public void exitQuantified_comparison_predicate(
			@NotNull SQLSelectParserParser.Quantified_comparison_predicateContext ctx) {
	}

	@Override
	public void enterBetween_predicate(@NotNull SQLSelectParserParser.Between_predicateContext ctx) {
	}

	@Override
	public void exitBetween_predicate(@NotNull SQLSelectParserParser.Between_predicateContext ctx) {
	}

	@Override
	public void enterUnsigned_literal(@NotNull SQLSelectParserParser.Unsigned_literalContext ctx) {
	}

	@Override
	public void exitUnsigned_literal(@NotNull SQLSelectParserParser.Unsigned_literalContext ctx) {
	}

	@Override
	public void enterApproximate_numeric_type(@NotNull SQLSelectParserParser.Approximate_numeric_typeContext ctx) {
	}

	@Override
	public void exitApproximate_numeric_type(@NotNull SQLSelectParserParser.Approximate_numeric_typeContext ctx) {
	}

	@Override
	public void enterEmpty_grouping_set(@NotNull SQLSelectParserParser.Empty_grouping_setContext ctx) {
	}

	@Override
	public void exitEmpty_grouping_set(@NotNull SQLSelectParserParser.Empty_grouping_setContext ctx) {
	}


	@Override
	public void enterTrim_operands(@NotNull SQLSelectParserParser.Trim_operandsContext ctx) {
	}

	@Override
	public void exitTrim_operands(@NotNull SQLSelectParserParser.Trim_operandsContext ctx) {
	}

	@Override
	public void enterCharacter_factor(@NotNull SQLSelectParserParser.Character_factorContext ctx) {
	}

	@Override
	public void exitCharacter_factor(@NotNull SQLSelectParserParser.Character_factorContext ctx) {
	}

	@Override
	public void enterNull_ordering(@NotNull SQLSelectParserParser.Null_orderingContext ctx) {
	}

	@Override
	public void exitNull_ordering(@NotNull SQLSelectParserParser.Null_orderingContext ctx) {
	}

	@Override
	public void enterCast_target(@NotNull SQLSelectParserParser.Cast_targetContext ctx) {
	}

	@Override
	public void exitCast_target(@NotNull SQLSelectParserParser.Cast_targetContext ctx) {
	}

	@Override
	public void enterTruth_value(@NotNull SQLSelectParserParser.Truth_valueContext ctx) {
	}

	@Override
	public void exitTruth_value(@NotNull SQLSelectParserParser.Truth_valueContext ctx) {
	}

	@Override
	public void enterBinary_type(@NotNull SQLSelectParserParser.Binary_typeContext ctx) {
	}

	@Override
	public void exitBinary_type(@NotNull SQLSelectParserParser.Binary_typeContext ctx) {
	}

	@Override
	public void enterNumeric_primary(@NotNull SQLSelectParserParser.Numeric_primaryContext ctx) {
	}

	@Override
	public void exitNumeric_primary(@NotNull SQLSelectParserParser.Numeric_primaryContext ctx) {
	}

	@Override
	public void enterCommon_value_expression(@NotNull SQLSelectParserParser.Common_value_expressionContext ctx) {
	}

	@Override
	public void exitCommon_value_expression(@NotNull SQLSelectParserParser.Common_value_expressionContext ctx) {
	}

	@Override
	public void enterAnd_predicate(@NotNull SQLSelectParserParser.And_predicateContext ctx) {
		System.out.println("ENTER AND PREDICATE: " + collector);
		System.out.println();
	}

	@Override
	public void exitAnd_predicate(@NotNull SQLSelectParserParser.And_predicateContext ctx) {
		System.out.println("EXIT AND PREDICATE: " + collector);
		System.out.println();
	}

	@Override
	public void enterFrom_clause(@NotNull SQLSelectParserParser.From_clauseContext ctx) {
	}

	@Override
	public void exitFrom_clause(@NotNull SQLSelectParserParser.From_clauseContext ctx) {
	}

	@Override
	public void enterRoutine_invocation(@NotNull SQLSelectParserParser.Routine_invocationContext ctx) {
	}

	@Override
	public void exitRoutine_invocation(@NotNull SQLSelectParserParser.Routine_invocationContext ctx) {
	}

	@Override
	public void enterNull_predicate(@NotNull SQLSelectParserParser.Null_predicateContext ctx) {
	}

	@Override
	public void exitNull_predicate(@NotNull SQLSelectParserParser.Null_predicateContext ctx) {
	}

	@Override
	public void enterGeneral_literal(@NotNull SQLSelectParserParser.General_literalContext ctx) {
	}

	@Override
	public void exitGeneral_literal(@NotNull SQLSelectParserParser.General_literalContext ctx) {
	}

	@Override
	public void enterSigned_numerical_literal(@NotNull SQLSelectParserParser.Signed_numerical_literalContext ctx) {
	}

	@Override
	public void exitSigned_numerical_literal(@NotNull SQLSelectParserParser.Signed_numerical_literalContext ctx) {
	}

	@Override
	public void enterElse_clause(@NotNull SQLSelectParserParser.Else_clauseContext ctx) {
	}

	@Override
	public void exitElse_clause(@NotNull SQLSelectParserParser.Else_clauseContext ctx) {
	}

	@Override
	public void enterSet_qualifier(@NotNull SQLSelectParserParser.Set_qualifierContext ctx) {
	}

	@Override
	public void exitSet_qualifier(@NotNull SQLSelectParserParser.Set_qualifierContext ctx) {
	}

	@Override
	public void enterWhere_clause(@NotNull SQLSelectParserParser.Where_clauseContext ctx) {
	}

	@Override
	public void exitWhere_clause(@NotNull SQLSelectParserParser.Where_clauseContext ctx) {
	}

	@Override
	public void enterSubquery(@NotNull SQLSelectParserParser.SubqueryContext ctx) {
	}

	@Override
	public void exitSubquery(@NotNull SQLSelectParserParser.SubqueryContext ctx) {
	}

	@Override
	public void enterString_value_expression(@NotNull SQLSelectParserParser.String_value_expressionContext ctx) {
	}

	@Override
	public void exitString_value_expression(@NotNull SQLSelectParserParser.String_value_expressionContext ctx) {
	}

	@Override
	public void enterSign(@NotNull SQLSelectParserParser.SignContext ctx) {
	}

	@Override
	public void exitSign(@NotNull SQLSelectParserParser.SignContext ctx) {
	}

	@Override
	public void enterFunction_names_for_reserved_words(
			@NotNull SQLSelectParserParser.Function_names_for_reserved_wordsContext ctx) {
	}

	@Override
	public void exitFunction_names_for_reserved_words(
			@NotNull SQLSelectParserParser.Function_names_for_reserved_wordsContext ctx) {
	}

	@Override
	public void enterFilter_clause(@NotNull SQLSelectParserParser.Filter_clauseContext ctx) {
	}

	@Override
	public void exitFilter_clause(@NotNull SQLSelectParserParser.Filter_clauseContext ctx) {
	}

	@Override
	public void enterTrim_specification(@NotNull SQLSelectParserParser.Trim_specificationContext ctx) {
	}

	@Override
	public void exitTrim_specification(@NotNull SQLSelectParserParser.Trim_specificationContext ctx) {
	}

	@Override
	public void enterTable_primary(@NotNull SQLSelectParserParser.Table_primaryContext ctx) {
	}

	@Override
	public void exitTable_primary(@NotNull SQLSelectParserParser.Table_primaryContext ctx) {
	}

	@Override
	public void enterAll(@NotNull SQLSelectParserParser.AllContext ctx) {
	}

	@Override
	public void exitAll(@NotNull SQLSelectParserParser.AllContext ctx) {
	}

	@Override
	public void enterColumn_reference(@NotNull SQLSelectParserParser.Column_referenceContext ctx) {
	}

	@Override
	public void exitColumn_reference(@NotNull SQLSelectParserParser.Column_referenceContext ctx) {
	}

	@Override
	public void enterJoin_type(@NotNull SQLSelectParserParser.Join_typeContext ctx) {
	}

	@Override
	public void exitJoin_type(@NotNull SQLSelectParserParser.Join_typeContext ctx) {
	}

	@Override
	public void enterCase_expression(@NotNull SQLSelectParserParser.Case_expressionContext ctx) {
	}

	@Override
	public void exitCase_expression(@NotNull SQLSelectParserParser.Case_expressionContext ctx) {
	}

	@Override
	public void enterComp_op(@NotNull SQLSelectParserParser.Comp_opContext ctx) {
	}

	@Override
	public void exitComp_op(@NotNull SQLSelectParserParser.Comp_opContext ctx) {
	}

	@Override
	public void enterBinary_large_object_string_type(
			@NotNull SQLSelectParserParser.Binary_large_object_string_typeContext ctx) {
	}

	@Override
	public void exitBinary_large_object_string_type(
			@NotNull SQLSelectParserParser.Binary_large_object_string_typeContext ctx) {
	}

	@Override
	public void enterPattern_matching_predicate(@NotNull SQLSelectParserParser.Pattern_matching_predicateContext ctx) {
	}

	@Override
	public void exitPattern_matching_predicate(@NotNull SQLSelectParserParser.Pattern_matching_predicateContext ctx) {
	}

	@Override
	public void enterTable_reference_list(@NotNull SQLSelectParserParser.Table_reference_listContext ctx) {
	}

	@Override
	public void exitTable_reference_list(@NotNull SQLSelectParserParser.Table_reference_listContext ctx) {
	}

	@Override
	public void enterPrimary_datetime_field(@NotNull SQLSelectParserParser.Primary_datetime_fieldContext ctx) {
	}

	@Override
	public void exitPrimary_datetime_field(@NotNull SQLSelectParserParser.Primary_datetime_fieldContext ctx) {
	}

	@Override
	public void enterExists_predicate(@NotNull SQLSelectParserParser.Exists_predicateContext ctx) {
	}

	@Override
	public void exitExists_predicate(@NotNull SQLSelectParserParser.Exists_predicateContext ctx) {
	}

	@Override
	public void enterSql(@NotNull SQLSelectParserParser.SqlContext ctx) {
	}

	@Override
	public void exitSql(@NotNull SQLSelectParserParser.SqlContext ctx) {
	}

	@Override
	public void enterNumeric_value_expression(@NotNull SQLSelectParserParser.Numeric_value_expressionContext ctx) {
	}

	@Override
	public void exitNumeric_value_expression(@NotNull SQLSelectParserParser.Numeric_value_expressionContext ctx) {
	}

	@Override
	public void enterSet_function_type(@NotNull SQLSelectParserParser.Set_function_typeContext ctx) {
	}

	@Override
	public void exitSet_function_type(@NotNull SQLSelectParserParser.Set_function_typeContext ctx) {
	}

	@Override
	public void enterGroupby_clause(@NotNull SQLSelectParserParser.Groupby_clauseContext ctx) {
	}

	@Override
	public void exitGroupby_clause(@NotNull SQLSelectParserParser.Groupby_clauseContext ctx) {
	}

	@Override
	public void enterUnique_predicate(@NotNull SQLSelectParserParser.Unique_predicateContext ctx) {
	}

	@Override
	public void exitUnique_predicate(@NotNull SQLSelectParserParser.Unique_predicateContext ctx) {
	}

	@Override
	public void enterBoolean_value_expression(@NotNull SQLSelectParserParser.Boolean_value_expressionContext ctx) {
	}

	@Override
	public void exitBoolean_value_expression(@NotNull SQLSelectParserParser.Boolean_value_expressionContext ctx) {
	}

	@Override
	public void enterSimple_when_clause(@NotNull SQLSelectParserParser.Simple_when_clauseContext ctx) {
	}

	@Override
	public void exitSimple_when_clause(@NotNull SQLSelectParserParser.Simple_when_clauseContext ctx) {
	}

	@Override
	public void enterValue_expression(@NotNull SQLSelectParserParser.Value_expressionContext ctx) {
	}

	@Override
	public void exitValue_expression(@NotNull SQLSelectParserParser.Value_expressionContext ctx) {
	}

	@Override
	public void enterComparison_predicate(@NotNull SQLSelectParserParser.Comparison_predicateContext ctx) {
	}

	@Override
	public void exitComparison_predicate(@NotNull SQLSelectParserParser.Comparison_predicateContext ctx) {
	}

	@Override
	public void enterTimestamp_literal(@NotNull SQLSelectParserParser.Timestamp_literalContext ctx) {
	}

	@Override
	public void exitTimestamp_literal(@NotNull SQLSelectParserParser.Timestamp_literalContext ctx) {
	}

	@Override
	public void enterQuantifier(@NotNull SQLSelectParserParser.QuantifierContext ctx) {
	}

	@Override
	public void exitQuantifier(@NotNull SQLSelectParserParser.QuantifierContext ctx) {
	}

	@Override
	public void enterCast_specification(@NotNull SQLSelectParserParser.Cast_specificationContext ctx) {
	}

	@Override
	public void exitCast_specification(@NotNull SQLSelectParserParser.Cast_specificationContext ctx) {
	}

	@Override
	public void enterRollup_list(@NotNull SQLSelectParserParser.Rollup_listContext ctx) {
	}

	@Override
	public void exitRollup_list(@NotNull SQLSelectParserParser.Rollup_listContext ctx) {
	}

	@Override
	public void enterExtended_datetime_field(@NotNull SQLSelectParserParser.Extended_datetime_fieldContext ctx) {
	}

	@Override
	public void exitExtended_datetime_field(@NotNull SQLSelectParserParser.Extended_datetime_fieldContext ctx) {
	}

	
	
	
	@Override
	public void enterEveryRule(@NotNull ParserRuleContext ctx) {
		Integer stackLvl = pushStack(ctx.getRuleIndex());

		if (ctx.getChildCount() == 1)
			if (ctx.getChild(0) instanceof TerminalNodeImpl) {
				// I'm a leaf
			} else
				collect(ctx.getRuleIndex(), stackLvl, new HashMap<String, Object>());
		else
			collect(ctx.getRuleIndex(), stackLvl, new HashMap<String, Object>());

		System.out.println("");
		System.out.println("Enter " + makeMapIndex(ctx.getRuleIndex(), stackLvl) + ": "
				+ SQLSelectParserParser.ruleNames[ctx.getRuleIndex()] + ": " + collector);
	}

	@Override
	public void exitEveryRule(@NotNull ParserRuleContext ctx) {
		int ruleIndex = ctx.getRuleIndex();
		Integer stackLevel = currentStackLevel(ruleIndex);
		Object item = null;

		if (ctx.getChildCount() == 1)
			if (ctx.getChild(0) instanceof TerminalNodeImpl) {
				// I'm a leaf
				item = ctx.getText();
			} else
				item = removeNodeMap(ruleIndex, stackLevel);
		else
			item = removeNodeMap(ruleIndex, stackLevel);

		// Add item to parent map
		if (ctx.getParent() != null) {
			int parentNodeIndex = ctx.getParent().getRuleIndex();
			Integer parentStackIndex = currentStackLevel(parentNodeIndex);
			if (ruleIndex == parentNodeIndex && stackLevel == parentStackIndex) {
				// oddity - in case it appears my parent is myself
				collect(ruleIndex, stackLevel, item);
			} else {
				Map<String, Object> idMap = getNodeMap(parentNodeIndex, parentStackIndex);
				if (idMap == null) {
					System.out.println("EXIT " + makeMapIndex(ruleIndex, stackLevel) + ": "
							+ SQLSelectParserParser.ruleNames[ruleIndex] + ": Missing pMap");
					System.out.println("");
				} else
					idMap.put(makeMapIndex(ruleIndex, idMap.size() + 1), item);
			}
		}

		popStack(ruleIndex);
		System.out.println("EXIT " + makeMapIndex(ruleIndex, stackLevel) + ": "
				+ SQLSelectParserParser.ruleNames[ruleIndex] + ": " + collector);
		System.out.println("");
	}

	private Integer currentStackLevel(int ruleIndex) {
		return stackLevel.get(ruleIndex);
	}

	@Override
	public void visitTerminal(@NotNull TerminalNode node) {
	}

	@Override
	public void visitErrorNode(@NotNull ErrorNode node) {
	}

}
