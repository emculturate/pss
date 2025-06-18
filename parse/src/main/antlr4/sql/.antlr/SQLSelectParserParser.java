// Generated from /Users/ghowe/emculturate-pss/pss/parse/src/main/antlr4/sql/SQLSelectParser.g4 by ANTLR 4.13.1


import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class SQLSelectParserParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		AS=1, ALL=2, AND=3, ANY=4, ASYMMETRIC=5, BOTH=6, CASE=7, CAST=8, CREATE=9, 
		CROSS=10, DISTINCT=11, END=12, ELSE=13, EXCEPT=14, FALSE=15, FULL=16, 
		FROM=17, GROUP=18, HAVING=19, IGNORE=20, ILIKE=21, IN=22, INNER=23, INTERSECT=24, 
		INTO=25, IS=26, JOIN=27, LEADING=28, LEFT=29, LIKE=30, LIMIT=31, NATURAL=32, 
		NOT=33, NULL=34, NULLS=35, NUMBER_TYPE=36, OFFSET=37, ON=38, OUTER=39, 
		OR=40, ORDER=41, RESPECT=42, RIGHT=43, RETURNING=44, SELECT=45, SOME=46, 
		SYMMETRIC=47, TABLE=48, THEN=49, TRAILING=50, TRUE=51, TRYCAST=52, UNION=53, 
		UNIQUE=54, USING=55, WHEN=56, WHERE=57, WITH=58, WITHOUT=59, ASC=60, AVG=61, 
		BETWEEN=62, BY=63, CENTURY=64, CHARACTER=65, COLLECT=66, COALESCE=67, 
		COLUMN=68, COUNT=69, COUNT_IF=70, CUBE=71, CURRENT=72, DAY=73, DEC=74, 
		DECADE=75, DESC=76, DOW=77, DOY=78, DROP=79, EPOCH=80, EVERY=81, EXISTS=82, 
		EXTERNAL=83, EXTRACT=84, FILTER=85, FIRST=86, FIRST_VALUE=87, FOLLOWING=88, 
		FORMAT=89, FUSION=90, GROUPING=91, HASH=92, HOUR=93, INDEX=94, INSERT=95, 
		INTERSECTION=96, ISODOW=97, ISOYEAR=98, LAG=99, LAST=100, LAST_VALUE=101, 
		LEAD=102, LESS=103, LIST=104, LOCATION=105, MAX=106, MAXVALUE=107, MICROSECONDS=108, 
		MILLENNIUM=109, MILLISECONDS=110, MIN=111, MINUTE=112, MONTH=113, NATIONAL=114, 
		NTH_VALUE=115, NULLIF=116, OVER=117, OVERWRITE=118, PARTITION=119, PARTITIONS=120, 
		PRECEDING=121, PRECISION=122, PURGE=123, QUARTER=124, RANGE=125, RANK=126, 
		REGEXP=127, RLIKE=128, ROLLUP=129, ROW_NUMBER=130, ROW=131, ROWS=132, 
		SECOND=133, SET=134, SIMILAR=135, STDDEV_POP=136, STDDEV_SAMP=137, SUBPARTITION=138, 
		SUM=139, TABLESPACE=140, THAN=141, TIMEZONE=142, TIMEZONE_HOUR=143, TIMEZONE_MINUTE=144, 
		TRIM=145, TO=146, UPDATE=147, UNBOUNDED=148, UNKNOWN=149, VALUES=150, 
		VAR_SAMP=151, VAR_POP=152, VARYING=153, WEEK=154, YEAR=155, ZONE=156, 
		ANY_VALUE=157, CORR=158, COVAR_POP=159, COVAR_SAMP=160, LISTAGG=161, MEDIAN=162, 
		PERCENTILE_CONT=163, PERCENTILE_DISC=164, STDDEV=165, VARIANCE_POP=166, 
		VARIANCE=167, VARIANCE_SAMP=168, CUME_DIST=169, DENSE_RANK=170, NTILE=171, 
		PERCENT_RANK=172, WIDTH_BUCKET=173, BITAND_AGG=174, BITOR_AGG=175, BITXOR_AGG=176, 
		HASH_AGG=177, ARRAY_AGG=178, OBJECT_AGG=179, REGR_AVGX=180, REGR_AVGY=181, 
		REGR_COUNT=182, REGR_INTERCEPT=183, REGR_R2=184, REGR_SLOPE=185, REGR_SXX=186, 
		REGR_SXY=187, REGR_SYY=188, APPROX_COUNT_DISTINCT=189, HLL=190, HLL_ACCUMULATE=191, 
		HLL_COMBINE=192, HLL_EXPORT=193, HLL_IMPORT=194, APPROXIMATE_JACCARD_INDEX=195, 
		APPROXIMATE_SIMILARITY=196, MINHASH=197, MINHASH_COMBINE=198, APPROX_TOP_K=199, 
		APPROX_TOP_K_ACCUMULATE=200, APPROX_TOP_K_COMBINE=201, APPROX_PERCENTILE=202, 
		APPROX_PERCENTILE_ACCUMULATE=203, APPROX_PERCENTILE_COMBINE=204, ABSTIME=205, 
		ANYARRAY=206, ARRAY=207, BOOLEAN=208, BOOL=209, BIT=210, VARBIT=211, CIDR=212, 
		INET=213, INET4=214, INTERVAL=215, INT1=216, INT2=217, INT4=218, INT8=219, 
		JSON=220, JSONB=221, MACADDR=222, NAME=223, OID=224, PG_LSN=225, PG_NODE_TREE=226, 
		REGPROC=227, XID=228, UUID=229, TINYINT=230, SMALLINT=231, INT=232, INTEGER=233, 
		BIGINT=234, BIGSERIAL=235, SMALLSERIAL=236, SERIAL=237, MONEY=238, FLOAT4=239, 
		FLOAT8=240, REAL=241, FLOAT=242, DOUBLE=243, NUMERIC=244, DECIMAL=245, 
		CHAR=246, VARCHAR=247, VARCHAR2=248, NCHAR=249, NVARCHAR=250, STRING=251, 
		DATE=252, DATETIME=253, TIME=254, TIMETZ=255, TIMESTAMP=256, TIMESTAMP_LTZ=257, 
		TIMESTAMP_NTZ=258, TIMESTAMP_TZ=259, TIMESTAMPTZ=260, TEXT=261, BINARY=262, 
		VARBINARY=263, BLOB=264, BYTEA=265, OBJECT=266, STRUCT=267, VARIANT=268, 
		Similar_To=269, Not_Similar_To=270, Similar_To_Case_Insensitive=271, Not_Similar_To_Case_Insensitive=272, 
		ASSIGN=273, EQUAL=274, COLON=275, SEMI_COLON=276, COMMA=277, CONCATENATION_OPERATOR=278, 
		NOT_EQUAL=279, LTH=280, LEQ=281, GTH=282, GEQ=283, LEFT_PAREN=284, RIGHT_PAREN=285, 
		PLUS=286, MINUS=287, MULTIPLY=288, DIVIDE=289, MODULAR=290, DOT=291, UNDERLINE=292, 
		VERTICAL_BAR=293, QUOTE=294, DOUBLE_QUOTE=295, CAST_OPERATOR=296, NUMBER=297, 
		PUML_CONSTANT_TENANT_SK=298, PUML_CONSTANT_TENANT_GUID=299, PUML_CONSTANT_TENANT_MASTER_ID=300, 
		PUML_CONSTANT_TENANT_NAME=301, PUML_CONSTANT_TENANT_ACRONYM=302, PUML_CONSTANT_TENANT_WEB_DOMAIN=303, 
		PUML_CONSTANT_ES_INSTITUTION_ID=304, PUML_CONSTANT_ES_INSTITUTION_CODE=305, 
		PUML_CONSTANT_ES_INSTITUTION_NAME=306, PUML_CONSTANT_SF_COUNTER_ID=307, 
		PUML_CONSTANT_FILE_NAME=308, PUML_CONSTANT_FILE_ID=309, PUML_CONSTANT_ROW_NUMBER=310, 
		PUML_CONSTANT_OBSERVATION_TIME=311, PUML_CONSTANT_SYSTEM_DATE=312, PUML_CONSTANT_SYSTEM_TIME=313, 
		PUML_CONSTANT_FEED_RUN_ID=314, PUML_CONSTANT_FEED_NAME=315, PUML_CONSTANT_TRANSACTION_RUN_ID=316, 
		PUML_CONSTANT_TRANSACTION_NAME=317, PUML_CONSTANT_POPULATION=318, PUML_CONSTANT_TARGET_MODEL_NAME=319, 
		PUML_CONSTANT_TENANT_SALT=320, PUML_CONSTANT_PIT_START_TIME=321, PUML_CONSTANT_PIT_END_TIME=322, 
		Bracket_Identifier=323, Variable_Identifier=324, Extended_Variable_Identifier=325, 
		Mixed_Variable_Identifier=326, Identifier=327, EXPONEN=328, Numeric_Identifier=329, 
		Double_Quoted_Numeric_Identifier=330, Dollar_Sign_Identifier=331, BlockComment=332, 
		LineComment=333, Character_String_Literal=334, Space=335, White_Space=336, 
		BAD=337;
	public static final int
		RULE_sql = 0, RULE_column_value = 1, RULE_predicand_value = 2, RULE_in_list_predicate_value = 3, 
		RULE_condition_value = 4, RULE_tuple_value = 5, RULE_query_value = 6, 
		RULE_join_extension_value = 7, RULE_literal_value = 8, RULE_with_query = 9, 
		RULE_with_clause = 10, RULE_with_list_item = 11, RULE_query_alias = 12, 
		RULE_query = 13, RULE_insert_expression = 14, RULE_update_expression = 15, 
		RULE_returning = 16, RULE_assignment_expression_list = 17, RULE_assignment_expression = 18, 
		RULE_create_table_as_expression = 19, RULE_query_expression = 20, RULE_intersected_query = 21, 
		RULE_intersect_clause = 22, RULE_intersect_operator = 23, RULE_unionized_query = 24, 
		RULE_union_clause = 25, RULE_union_operator = 26, RULE_query_primary = 27, 
		RULE_subquery = 28, RULE_query_specification = 29, RULE_into_list = 30, 
		RULE_set_qualifier = 31, RULE_select_list = 32, RULE_select_item = 33, 
		RULE_as_clause = 34, RULE_select_all_columns = 35, RULE_from_clause = 36, 
		RULE_join_extension = 37, RULE_table_reference_list = 38, RULE_join_extension_primary = 39, 
		RULE_table_primary = 40, RULE_tuple_primary = 41, RULE_table_or_query_name = 42, 
		RULE_unqualified_join = 43, RULE_qualified_join = 44, RULE_join_type = 45, 
		RULE_join_specification = 46, RULE_join_condition = 47, RULE_named_columns_join = 48, 
		RULE_using_term = 49, RULE_column_reference_list = 50, RULE_column_reference = 51, 
		RULE_column_primary = 52, RULE_predicand_primary = 53, RULE_value_expression_primary = 54, 
		RULE_parenthesized_value_expression = 55, RULE_nonparenthesized_value_expression_primary = 56, 
		RULE_aggregate_function = 57, RULE_set_function_type = 58, RULE_set_qualifier_type = 59, 
		RULE_case_expression = 60, RULE_when_clause_list = 61, RULE_searched_when_clause = 62, 
		RULE_when_value_list = 63, RULE_when_value_clause = 64, RULE_else_clause = 65, 
		RULE_case_result = 66, RULE_null_literal = 67, RULE_cast_function_expression = 68, 
		RULE_cast_function_name = 69, RULE_window_over_partition_expression = 70, 
		RULE_window_function = 71, RULE_over_clause = 72, RULE_partition_by_clause = 73, 
		RULE_bracket_frame_clause = 74, RULE_rows_or_range = 75, RULE_bracket_frame_definition = 76, 
		RULE_between_frame_definition = 77, RULE_frame_edge = 78, RULE_preceding_frame_edge = 79, 
		RULE_following_frame_edge = 80, RULE_current_row_edge = 81, RULE_bracket_constraint = 82, 
		RULE_item_select_function = 83, RULE_select_direction = 84, RULE_null_handling = 85, 
		RULE_value_expression = 86, RULE_common_value_expression = 87, RULE_additive_expression = 88, 
		RULE_multiplicative_expression = 89, RULE_factor = 90, RULE_numeric_primary = 91, 
		RULE_sign = 92, RULE_extract_expression = 93, RULE_extract_field = 94, 
		RULE_time_zone_field = 95, RULE_extract_source = 96, RULE_string_value_expression = 97, 
		RULE_character_primary = 98, RULE_trim_function = 99, RULE_trim_function_name = 100, 
		RULE_trim_operands = 101, RULE_trim_specification = 102, RULE_boolean_value_expression = 103, 
		RULE_or_predicate = 104, RULE_and_predicate = 105, RULE_negative_predicate = 106, 
		RULE_parenthetical_predicate = 107, RULE_boolean_primary = 108, RULE_predicate = 109, 
		RULE_substitution_predicate = 110, RULE_row_value_expression = 111, RULE_row_value_predicand = 112, 
		RULE_where_clause = 113, RULE_search_condition = 114, RULE_orderby_clause = 115, 
		RULE_sort_specifier_list = 116, RULE_sort_specifier = 117, RULE_order_specification = 118, 
		RULE_null_ordering = 119, RULE_null_first_last = 120, RULE_limit_clause = 121, 
		RULE_groupby_clause = 122, RULE_grouping_element_list = 123, RULE_grouping_element = 124, 
		RULE_ordinary_grouping_set_list = 125, RULE_ordinary_grouping_set = 126, 
		RULE_rollup_list = 127, RULE_cube_list = 128, RULE_empty_grouping_set = 129, 
		RULE_having_clause = 130, RULE_row_value_predicand_list = 131, RULE_comparison_predicate = 132, 
		RULE_comparison_operator = 133, RULE_relative_comp_op = 134, RULE_similarity_op = 135, 
		RULE_comp_op = 136, RULE_between_predicate = 137, RULE_symmetry = 138, 
		RULE_in_predicate = 139, RULE_like_any_predicate = 140, RULE_like_any_operator = 141, 
		RULE_in_predicate_value = 142, RULE_in_value_list = 143, RULE_values_statement_end = 144, 
		RULE_values_statement_primary = 145, RULE_fully_defined_values_statement = 146, 
		RULE_aliased_values_statement = 147, RULE_values_statement = 148, RULE_values_matrix = 149, 
		RULE_values_row = 150, RULE_values_columns = 151, RULE_null_predicate = 152, 
		RULE_is_null_clause = 153, RULE_is_clause = 154, RULE_truth_value = 155, 
		RULE_not = 156, RULE_quantified_comparison_predicate = 157, RULE_quantifier = 158, 
		RULE_all = 159, RULE_some = 160, RULE_exists_predicate = 161, RULE_unique_predicate = 162, 
		RULE_primary_datetime_field = 163, RULE_non_second_primary_datetime_field = 164, 
		RULE_extended_datetime_field = 165, RULE_routine_invocation = 166, RULE_function_name = 167, 
		RULE_function_names_for_reserved_words = 168, RULE_sql_argument_list = 169, 
		RULE_identifier = 170, RULE_alias_identifier = 171, RULE_variable_identifier = 172, 
		RULE_simple_identifier = 173, RULE_logical_identifier = 174, RULE_simple_variable_identifier = 175, 
		RULE_extended_variable_identifier = 176, RULE_simple_numeric_identifier = 177, 
		RULE_snowflake_quoted_numeric_identifier = 178, RULE_snowflake_dollar_function_identifier = 179, 
		RULE_nonreserved_keywords = 180, RULE_signed_numeric_literal = 181, RULE_unsigned_literal = 182, 
		RULE_unsigned_numeric_literal = 183, RULE_real_number_def = 184, RULE_exponent = 185, 
		RULE_general_literal = 186, RULE_character_literal = 187, RULE_datetime_literal = 188, 
		RULE_time_literal = 189, RULE_timestamp_literal = 190, RULE_date_literal = 191, 
		RULE_boolean_literal = 192, RULE_data_type = 193, RULE_variable_size_data_type = 194, 
		RULE_variable_data_type_name = 195, RULE_type_length = 196, RULE_precision_scale_data_type = 197, 
		RULE_precision_data_type_name = 198, RULE_precision_param = 199, RULE_static_data_type = 200, 
		RULE_static_data_type_name = 201, RULE_puml_constant_identifier = 202;
	private static String[] makeRuleNames() {
		return new String[] {
			"sql", "column_value", "predicand_value", "in_list_predicate_value", 
			"condition_value", "tuple_value", "query_value", "join_extension_value", 
			"literal_value", "with_query", "with_clause", "with_list_item", "query_alias", 
			"query", "insert_expression", "update_expression", "returning", "assignment_expression_list", 
			"assignment_expression", "create_table_as_expression", "query_expression", 
			"intersected_query", "intersect_clause", "intersect_operator", "unionized_query", 
			"union_clause", "union_operator", "query_primary", "subquery", "query_specification", 
			"into_list", "set_qualifier", "select_list", "select_item", "as_clause", 
			"select_all_columns", "from_clause", "join_extension", "table_reference_list", 
			"join_extension_primary", "table_primary", "tuple_primary", "table_or_query_name", 
			"unqualified_join", "qualified_join", "join_type", "join_specification", 
			"join_condition", "named_columns_join", "using_term", "column_reference_list", 
			"column_reference", "column_primary", "predicand_primary", "value_expression_primary", 
			"parenthesized_value_expression", "nonparenthesized_value_expression_primary", 
			"aggregate_function", "set_function_type", "set_qualifier_type", "case_expression", 
			"when_clause_list", "searched_when_clause", "when_value_list", "when_value_clause", 
			"else_clause", "case_result", "null_literal", "cast_function_expression", 
			"cast_function_name", "window_over_partition_expression", "window_function", 
			"over_clause", "partition_by_clause", "bracket_frame_clause", "rows_or_range", 
			"bracket_frame_definition", "between_frame_definition", "frame_edge", 
			"preceding_frame_edge", "following_frame_edge", "current_row_edge", "bracket_constraint", 
			"item_select_function", "select_direction", "null_handling", "value_expression", 
			"common_value_expression", "additive_expression", "multiplicative_expression", 
			"factor", "numeric_primary", "sign", "extract_expression", "extract_field", 
			"time_zone_field", "extract_source", "string_value_expression", "character_primary", 
			"trim_function", "trim_function_name", "trim_operands", "trim_specification", 
			"boolean_value_expression", "or_predicate", "and_predicate", "negative_predicate", 
			"parenthetical_predicate", "boolean_primary", "predicate", "substitution_predicate", 
			"row_value_expression", "row_value_predicand", "where_clause", "search_condition", 
			"orderby_clause", "sort_specifier_list", "sort_specifier", "order_specification", 
			"null_ordering", "null_first_last", "limit_clause", "groupby_clause", 
			"grouping_element_list", "grouping_element", "ordinary_grouping_set_list", 
			"ordinary_grouping_set", "rollup_list", "cube_list", "empty_grouping_set", 
			"having_clause", "row_value_predicand_list", "comparison_predicate", 
			"comparison_operator", "relative_comp_op", "similarity_op", "comp_op", 
			"between_predicate", "symmetry", "in_predicate", "like_any_predicate", 
			"like_any_operator", "in_predicate_value", "in_value_list", "values_statement_end", 
			"values_statement_primary", "fully_defined_values_statement", "aliased_values_statement", 
			"values_statement", "values_matrix", "values_row", "values_columns", 
			"null_predicate", "is_null_clause", "is_clause", "truth_value", "not", 
			"quantified_comparison_predicate", "quantifier", "all", "some", "exists_predicate", 
			"unique_predicate", "primary_datetime_field", "non_second_primary_datetime_field", 
			"extended_datetime_field", "routine_invocation", "function_name", "function_names_for_reserved_words", 
			"sql_argument_list", "identifier", "alias_identifier", "variable_identifier", 
			"simple_identifier", "logical_identifier", "simple_variable_identifier", 
			"extended_variable_identifier", "simple_numeric_identifier", "snowflake_quoted_numeric_identifier", 
			"snowflake_dollar_function_identifier", "nonreserved_keywords", "signed_numeric_literal", 
			"unsigned_literal", "unsigned_numeric_literal", "real_number_def", "exponent", 
			"general_literal", "character_literal", "datetime_literal", "time_literal", 
			"timestamp_literal", "date_literal", "boolean_literal", "data_type", 
			"variable_size_data_type", "variable_data_type_name", "type_length", 
			"precision_scale_data_type", "precision_data_type_name", "precision_param", 
			"static_data_type", "static_data_type_name", "puml_constant_identifier"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, "'~'", "'!~'", "'~*'", "'!~*'", "':='", 
			"'='", "':'", "';'", "','", null, null, "'<'", "'<='", "'>'", "'>='", 
			"'('", "')'", "'+'", "'-'", "'*'", "'/'", "'%'", null, "'_'", "'|'", 
			"'''", "'\"'", "'::'", null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, "' '"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "AS", "ALL", "AND", "ANY", "ASYMMETRIC", "BOTH", "CASE", "CAST", 
			"CREATE", "CROSS", "DISTINCT", "END", "ELSE", "EXCEPT", "FALSE", "FULL", 
			"FROM", "GROUP", "HAVING", "IGNORE", "ILIKE", "IN", "INNER", "INTERSECT", 
			"INTO", "IS", "JOIN", "LEADING", "LEFT", "LIKE", "LIMIT", "NATURAL", 
			"NOT", "NULL", "NULLS", "NUMBER_TYPE", "OFFSET", "ON", "OUTER", "OR", 
			"ORDER", "RESPECT", "RIGHT", "RETURNING", "SELECT", "SOME", "SYMMETRIC", 
			"TABLE", "THEN", "TRAILING", "TRUE", "TRYCAST", "UNION", "UNIQUE", "USING", 
			"WHEN", "WHERE", "WITH", "WITHOUT", "ASC", "AVG", "BETWEEN", "BY", "CENTURY", 
			"CHARACTER", "COLLECT", "COALESCE", "COLUMN", "COUNT", "COUNT_IF", "CUBE", 
			"CURRENT", "DAY", "DEC", "DECADE", "DESC", "DOW", "DOY", "DROP", "EPOCH", 
			"EVERY", "EXISTS", "EXTERNAL", "EXTRACT", "FILTER", "FIRST", "FIRST_VALUE", 
			"FOLLOWING", "FORMAT", "FUSION", "GROUPING", "HASH", "HOUR", "INDEX", 
			"INSERT", "INTERSECTION", "ISODOW", "ISOYEAR", "LAG", "LAST", "LAST_VALUE", 
			"LEAD", "LESS", "LIST", "LOCATION", "MAX", "MAXVALUE", "MICROSECONDS", 
			"MILLENNIUM", "MILLISECONDS", "MIN", "MINUTE", "MONTH", "NATIONAL", "NTH_VALUE", 
			"NULLIF", "OVER", "OVERWRITE", "PARTITION", "PARTITIONS", "PRECEDING", 
			"PRECISION", "PURGE", "QUARTER", "RANGE", "RANK", "REGEXP", "RLIKE", 
			"ROLLUP", "ROW_NUMBER", "ROW", "ROWS", "SECOND", "SET", "SIMILAR", "STDDEV_POP", 
			"STDDEV_SAMP", "SUBPARTITION", "SUM", "TABLESPACE", "THAN", "TIMEZONE", 
			"TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TRIM", "TO", "UPDATE", "UNBOUNDED", 
			"UNKNOWN", "VALUES", "VAR_SAMP", "VAR_POP", "VARYING", "WEEK", "YEAR", 
			"ZONE", "ANY_VALUE", "CORR", "COVAR_POP", "COVAR_SAMP", "LISTAGG", "MEDIAN", 
			"PERCENTILE_CONT", "PERCENTILE_DISC", "STDDEV", "VARIANCE_POP", "VARIANCE", 
			"VARIANCE_SAMP", "CUME_DIST", "DENSE_RANK", "NTILE", "PERCENT_RANK", 
			"WIDTH_BUCKET", "BITAND_AGG", "BITOR_AGG", "BITXOR_AGG", "HASH_AGG", 
			"ARRAY_AGG", "OBJECT_AGG", "REGR_AVGX", "REGR_AVGY", "REGR_COUNT", "REGR_INTERCEPT", 
			"REGR_R2", "REGR_SLOPE", "REGR_SXX", "REGR_SXY", "REGR_SYY", "APPROX_COUNT_DISTINCT", 
			"HLL", "HLL_ACCUMULATE", "HLL_COMBINE", "HLL_EXPORT", "HLL_IMPORT", "APPROXIMATE_JACCARD_INDEX", 
			"APPROXIMATE_SIMILARITY", "MINHASH", "MINHASH_COMBINE", "APPROX_TOP_K", 
			"APPROX_TOP_K_ACCUMULATE", "APPROX_TOP_K_COMBINE", "APPROX_PERCENTILE", 
			"APPROX_PERCENTILE_ACCUMULATE", "APPROX_PERCENTILE_COMBINE", "ABSTIME", 
			"ANYARRAY", "ARRAY", "BOOLEAN", "BOOL", "BIT", "VARBIT", "CIDR", "INET", 
			"INET4", "INTERVAL", "INT1", "INT2", "INT4", "INT8", "JSON", "JSONB", 
			"MACADDR", "NAME", "OID", "PG_LSN", "PG_NODE_TREE", "REGPROC", "XID", 
			"UUID", "TINYINT", "SMALLINT", "INT", "INTEGER", "BIGINT", "BIGSERIAL", 
			"SMALLSERIAL", "SERIAL", "MONEY", "FLOAT4", "FLOAT8", "REAL", "FLOAT", 
			"DOUBLE", "NUMERIC", "DECIMAL", "CHAR", "VARCHAR", "VARCHAR2", "NCHAR", 
			"NVARCHAR", "STRING", "DATE", "DATETIME", "TIME", "TIMETZ", "TIMESTAMP", 
			"TIMESTAMP_LTZ", "TIMESTAMP_NTZ", "TIMESTAMP_TZ", "TIMESTAMPTZ", "TEXT", 
			"BINARY", "VARBINARY", "BLOB", "BYTEA", "OBJECT", "STRUCT", "VARIANT", 
			"Similar_To", "Not_Similar_To", "Similar_To_Case_Insensitive", "Not_Similar_To_Case_Insensitive", 
			"ASSIGN", "EQUAL", "COLON", "SEMI_COLON", "COMMA", "CONCATENATION_OPERATOR", 
			"NOT_EQUAL", "LTH", "LEQ", "GTH", "GEQ", "LEFT_PAREN", "RIGHT_PAREN", 
			"PLUS", "MINUS", "MULTIPLY", "DIVIDE", "MODULAR", "DOT", "UNDERLINE", 
			"VERTICAL_BAR", "QUOTE", "DOUBLE_QUOTE", "CAST_OPERATOR", "NUMBER", "PUML_CONSTANT_TENANT_SK", 
			"PUML_CONSTANT_TENANT_GUID", "PUML_CONSTANT_TENANT_MASTER_ID", "PUML_CONSTANT_TENANT_NAME", 
			"PUML_CONSTANT_TENANT_ACRONYM", "PUML_CONSTANT_TENANT_WEB_DOMAIN", "PUML_CONSTANT_ES_INSTITUTION_ID", 
			"PUML_CONSTANT_ES_INSTITUTION_CODE", "PUML_CONSTANT_ES_INSTITUTION_NAME", 
			"PUML_CONSTANT_SF_COUNTER_ID", "PUML_CONSTANT_FILE_NAME", "PUML_CONSTANT_FILE_ID", 
			"PUML_CONSTANT_ROW_NUMBER", "PUML_CONSTANT_OBSERVATION_TIME", "PUML_CONSTANT_SYSTEM_DATE", 
			"PUML_CONSTANT_SYSTEM_TIME", "PUML_CONSTANT_FEED_RUN_ID", "PUML_CONSTANT_FEED_NAME", 
			"PUML_CONSTANT_TRANSACTION_RUN_ID", "PUML_CONSTANT_TRANSACTION_NAME", 
			"PUML_CONSTANT_POPULATION", "PUML_CONSTANT_TARGET_MODEL_NAME", "PUML_CONSTANT_TENANT_SALT", 
			"PUML_CONSTANT_PIT_START_TIME", "PUML_CONSTANT_PIT_END_TIME", "Bracket_Identifier", 
			"Variable_Identifier", "Extended_Variable_Identifier", "Mixed_Variable_Identifier", 
			"Identifier", "EXPONEN", "Numeric_Identifier", "Double_Quoted_Numeric_Identifier", 
			"Dollar_Sign_Identifier", "BlockComment", "LineComment", "Character_String_Literal", 
			"Space", "White_Space", "BAD"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "SQLSelectParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }



	public SQLSelectParserParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SqlContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(SQLSelectParserParser.EOF, 0); }
		public With_queryContext with_query() {
			return getRuleContext(With_queryContext.class,0);
		}
		public Create_table_as_expressionContext create_table_as_expression() {
			return getRuleContext(Create_table_as_expressionContext.class,0);
		}
		public TerminalNode SEMI_COLON() { return getToken(SQLSelectParserParser.SEMI_COLON, 0); }
		public SqlContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sql; }
	}

	public final SqlContext sql() throws RecognitionException {
		SqlContext _localctx = new SqlContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_sql);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(408);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SELECT:
			case WITH:
			case INSERT:
			case UPDATE:
			case LEFT_PAREN:
			case Variable_Identifier:
			case Extended_Variable_Identifier:
			case Mixed_Variable_Identifier:
				{
				setState(406);
				with_query();
				}
				break;
			case CREATE:
				{
				setState(407);
				create_table_as_expression();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(411);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEMI_COLON) {
				{
				setState(410);
				match(SEMI_COLON);
				}
			}

			setState(413);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Column_valueContext extends ParserRuleContext {
		public Column_primaryContext column_primary() {
			return getRuleContext(Column_primaryContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SQLSelectParserParser.EOF, 0); }
		public Column_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_column_value; }
	}

	public final Column_valueContext column_value() throws RecognitionException {
		Column_valueContext _localctx = new Column_valueContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_column_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(415);
			column_primary();
			setState(416);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Predicand_valueContext extends ParserRuleContext {
		public Predicand_primaryContext predicand_primary() {
			return getRuleContext(Predicand_primaryContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SQLSelectParserParser.EOF, 0); }
		public Predicand_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicand_value; }
	}

	public final Predicand_valueContext predicand_value() throws RecognitionException {
		Predicand_valueContext _localctx = new Predicand_valueContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_predicand_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(418);
			predicand_primary();
			setState(419);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class In_list_predicate_valueContext extends ParserRuleContext {
		public In_predicate_valueContext in_predicate_value() {
			return getRuleContext(In_predicate_valueContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SQLSelectParserParser.EOF, 0); }
		public In_list_predicate_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_in_list_predicate_value; }
	}

	public final In_list_predicate_valueContext in_list_predicate_value() throws RecognitionException {
		In_list_predicate_valueContext _localctx = new In_list_predicate_valueContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_in_list_predicate_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(421);
			in_predicate_value();
			setState(422);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Condition_valueContext extends ParserRuleContext {
		public Search_conditionContext search_condition() {
			return getRuleContext(Search_conditionContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SQLSelectParserParser.EOF, 0); }
		public Condition_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition_value; }
	}

	public final Condition_valueContext condition_value() throws RecognitionException {
		Condition_valueContext _localctx = new Condition_valueContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_condition_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(424);
			search_condition();
			setState(425);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Tuple_valueContext extends ParserRuleContext {
		public Tuple_primaryContext tuple_primary() {
			return getRuleContext(Tuple_primaryContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SQLSelectParserParser.EOF, 0); }
		public Tuple_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tuple_value; }
	}

	public final Tuple_valueContext tuple_value() throws RecognitionException {
		Tuple_valueContext _localctx = new Tuple_valueContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_tuple_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(427);
			tuple_primary();
			setState(428);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Query_valueContext extends ParserRuleContext {
		public QueryContext query() {
			return getRuleContext(QueryContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SQLSelectParserParser.EOF, 0); }
		public Query_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query_value; }
	}

	public final Query_valueContext query_value() throws RecognitionException {
		Query_valueContext _localctx = new Query_valueContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_query_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(430);
			query();
			setState(431);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Join_extension_valueContext extends ParserRuleContext {
		public Join_extension_primaryContext join_extension_primary() {
			return getRuleContext(Join_extension_primaryContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SQLSelectParserParser.EOF, 0); }
		public Join_extension_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_join_extension_value; }
	}

	public final Join_extension_valueContext join_extension_value() throws RecognitionException {
		Join_extension_valueContext _localctx = new Join_extension_valueContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_join_extension_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(433);
			join_extension_primary();
			setState(434);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Literal_valueContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(SQLSelectParserParser.EOF, 0); }
		public Signed_numeric_literalContext signed_numeric_literal() {
			return getRuleContext(Signed_numeric_literalContext.class,0);
		}
		public Unsigned_literalContext unsigned_literal() {
			return getRuleContext(Unsigned_literalContext.class,0);
		}
		public Literal_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal_value; }
	}

	public final Literal_valueContext literal_value() throws RecognitionException {
		Literal_valueContext _localctx = new Literal_valueContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_literal_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(438);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				{
				setState(436);
				signed_numeric_literal();
				}
				break;
			case 2:
				{
				setState(437);
				unsigned_literal();
				}
				break;
			}
			setState(440);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class With_queryContext extends ParserRuleContext {
		public QueryContext query() {
			return getRuleContext(QueryContext.class,0);
		}
		public With_clauseContext with_clause() {
			return getRuleContext(With_clauseContext.class,0);
		}
		public With_queryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_with_query; }
	}

	public final With_queryContext with_query() throws RecognitionException {
		With_queryContext _localctx = new With_queryContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_with_query);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(443);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WITH) {
				{
				setState(442);
				with_clause();
				}
			}

			setState(445);
			query();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class With_clauseContext extends ParserRuleContext {
		public TerminalNode WITH() { return getToken(SQLSelectParserParser.WITH, 0); }
		public List<With_list_itemContext> with_list_item() {
			return getRuleContexts(With_list_itemContext.class);
		}
		public With_list_itemContext with_list_item(int i) {
			return getRuleContext(With_list_itemContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SQLSelectParserParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SQLSelectParserParser.COMMA, i);
		}
		public With_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_with_clause; }
	}

	public final With_clauseContext with_clause() throws RecognitionException {
		With_clauseContext _localctx = new With_clauseContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_with_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(447);
			match(WITH);
			setState(448);
			with_list_item();
			setState(453);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(449);
				match(COMMA);
				setState(450);
				with_list_item();
				}
				}
				setState(455);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class With_list_itemContext extends ParserRuleContext {
		public Query_aliasContext query_alias() {
			return getRuleContext(Query_aliasContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public QueryContext query() {
			return getRuleContext(QueryContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public With_list_itemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_with_list_item; }
	}

	public final With_list_itemContext with_list_item() throws RecognitionException {
		With_list_itemContext _localctx = new With_list_itemContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_with_list_item);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(456);
			query_alias();
			{
			setState(457);
			match(LEFT_PAREN);
			setState(458);
			query();
			setState(459);
			match(RIGHT_PAREN);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Query_aliasContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public TerminalNode AS() { return getToken(SQLSelectParserParser.AS, 0); }
		public Query_aliasContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query_alias; }
	}

	public final Query_aliasContext query_alias() throws RecognitionException {
		Query_aliasContext _localctx = new Query_aliasContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_query_alias);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(461);
			identifier();
			setState(462);
			match(AS);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class QueryContext extends ParserRuleContext {
		public Query_expressionContext query_expression() {
			return getRuleContext(Query_expressionContext.class,0);
		}
		public Insert_expressionContext insert_expression() {
			return getRuleContext(Insert_expressionContext.class,0);
		}
		public Update_expressionContext update_expression() {
			return getRuleContext(Update_expressionContext.class,0);
		}
		public QueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query; }
	}

	public final QueryContext query() throws RecognitionException {
		QueryContext _localctx = new QueryContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_query);
		try {
			setState(467);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SELECT:
			case LEFT_PAREN:
			case Variable_Identifier:
			case Extended_Variable_Identifier:
			case Mixed_Variable_Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(464);
				query_expression();
				}
				break;
			case INSERT:
				enterOuterAlt(_localctx, 2);
				{
				setState(465);
				insert_expression();
				}
				break;
			case UPDATE:
				enterOuterAlt(_localctx, 3);
				{
				setState(466);
				update_expression();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Insert_expressionContext extends ParserRuleContext {
		public TerminalNode INSERT() { return getToken(SQLSelectParserParser.INSERT, 0); }
		public TerminalNode INTO() { return getToken(SQLSelectParserParser.INTO, 0); }
		public Table_primaryContext table_primary() {
			return getRuleContext(Table_primaryContext.class,0);
		}
		public TerminalNode VALUES() { return getToken(SQLSelectParserParser.VALUES, 0); }
		public SubqueryContext subquery() {
			return getRuleContext(SubqueryContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public Column_reference_listContext column_reference_list() {
			return getRuleContext(Column_reference_listContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public ReturningContext returning() {
			return getRuleContext(ReturningContext.class,0);
		}
		public Insert_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_insert_expression; }
	}

	public final Insert_expressionContext insert_expression() throws RecognitionException {
		Insert_expressionContext _localctx = new Insert_expressionContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_insert_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(469);
			match(INSERT);
			setState(470);
			match(INTO);
			setState(471);
			table_primary();
			setState(476);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LEFT_PAREN) {
				{
				setState(472);
				match(LEFT_PAREN);
				setState(473);
				column_reference_list();
				setState(474);
				match(RIGHT_PAREN);
				}
			}

			setState(478);
			match(VALUES);
			setState(479);
			subquery();
			setState(481);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==RETURNING) {
				{
				setState(480);
				returning();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Update_expressionContext extends ParserRuleContext {
		public TerminalNode UPDATE() { return getToken(SQLSelectParserParser.UPDATE, 0); }
		public Table_primaryContext table_primary() {
			return getRuleContext(Table_primaryContext.class,0);
		}
		public TerminalNode SET() { return getToken(SQLSelectParserParser.SET, 0); }
		public Assignment_expression_listContext assignment_expression_list() {
			return getRuleContext(Assignment_expression_listContext.class,0);
		}
		public From_clauseContext from_clause() {
			return getRuleContext(From_clauseContext.class,0);
		}
		public Where_clauseContext where_clause() {
			return getRuleContext(Where_clauseContext.class,0);
		}
		public ReturningContext returning() {
			return getRuleContext(ReturningContext.class,0);
		}
		public Update_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_update_expression; }
	}

	public final Update_expressionContext update_expression() throws RecognitionException {
		Update_expressionContext _localctx = new Update_expressionContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_update_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(483);
			match(UPDATE);
			setState(484);
			table_primary();
			setState(485);
			match(SET);
			setState(486);
			assignment_expression_list();
			setState(488);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FROM) {
				{
				setState(487);
				from_clause();
				}
			}

			setState(491);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(490);
				where_clause();
				}
			}

			setState(494);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==RETURNING) {
				{
				setState(493);
				returning();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ReturningContext extends ParserRuleContext {
		public TerminalNode RETURNING() { return getToken(SQLSelectParserParser.RETURNING, 0); }
		public TerminalNode MULTIPLY() { return getToken(SQLSelectParserParser.MULTIPLY, 0); }
		public ReturningContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_returning; }
	}

	public final ReturningContext returning() throws RecognitionException {
		ReturningContext _localctx = new ReturningContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_returning);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(496);
			match(RETURNING);
			setState(497);
			match(MULTIPLY);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Assignment_expression_listContext extends ParserRuleContext {
		public List<Assignment_expressionContext> assignment_expression() {
			return getRuleContexts(Assignment_expressionContext.class);
		}
		public Assignment_expressionContext assignment_expression(int i) {
			return getRuleContext(Assignment_expressionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SQLSelectParserParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SQLSelectParserParser.COMMA, i);
		}
		public Assignment_expression_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignment_expression_list; }
	}

	public final Assignment_expression_listContext assignment_expression_list() throws RecognitionException {
		Assignment_expression_listContext _localctx = new Assignment_expression_listContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_assignment_expression_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(499);
			assignment_expression();
			setState(504);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(500);
				match(COMMA);
				setState(501);
				assignment_expression();
				}
				}
				setState(506);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Assignment_expressionContext extends ParserRuleContext {
		public Column_referenceContext column_reference() {
			return getRuleContext(Column_referenceContext.class,0);
		}
		public TerminalNode EQUAL() { return getToken(SQLSelectParserParser.EQUAL, 0); }
		public Row_value_predicandContext row_value_predicand() {
			return getRuleContext(Row_value_predicandContext.class,0);
		}
		public Assignment_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignment_expression; }
	}

	public final Assignment_expressionContext assignment_expression() throws RecognitionException {
		Assignment_expressionContext _localctx = new Assignment_expressionContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_assignment_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(507);
			column_reference();
			setState(508);
			match(EQUAL);
			setState(509);
			row_value_predicand();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Create_table_as_expressionContext extends ParserRuleContext {
		public TerminalNode CREATE() { return getToken(SQLSelectParserParser.CREATE, 0); }
		public TerminalNode TABLE() { return getToken(SQLSelectParserParser.TABLE, 0); }
		public TerminalNode AS() { return getToken(SQLSelectParserParser.AS, 0); }
		public Query_expressionContext query_expression() {
			return getRuleContext(Query_expressionContext.class,0);
		}
		public Create_table_as_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_table_as_expression; }
	}

	public final Create_table_as_expressionContext create_table_as_expression() throws RecognitionException {
		Create_table_as_expressionContext _localctx = new Create_table_as_expressionContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_create_table_as_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(511);
			match(CREATE);
			setState(512);
			match(TABLE);
			setState(513);
			match(AS);
			setState(514);
			query_expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Query_expressionContext extends ParserRuleContext {
		public Intersected_queryContext intersected_query() {
			return getRuleContext(Intersected_queryContext.class,0);
		}
		public Query_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query_expression; }
	}

	public final Query_expressionContext query_expression() throws RecognitionException {
		Query_expressionContext _localctx = new Query_expressionContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_query_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(516);
			intersected_query();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Intersected_queryContext extends ParserRuleContext {
		public List<Unionized_queryContext> unionized_query() {
			return getRuleContexts(Unionized_queryContext.class);
		}
		public Unionized_queryContext unionized_query(int i) {
			return getRuleContext(Unionized_queryContext.class,i);
		}
		public List<Intersect_clauseContext> intersect_clause() {
			return getRuleContexts(Intersect_clauseContext.class);
		}
		public Intersect_clauseContext intersect_clause(int i) {
			return getRuleContext(Intersect_clauseContext.class,i);
		}
		public Intersected_queryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_intersected_query; }
	}

	public final Intersected_queryContext intersected_query() throws RecognitionException {
		Intersected_queryContext _localctx = new Intersected_queryContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_intersected_query);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(518);
			unionized_query();
			setState(524);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INTERSECT) {
				{
				{
				setState(519);
				intersect_clause();
				setState(520);
				unionized_query();
				}
				}
				setState(526);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Intersect_clauseContext extends ParserRuleContext {
		public Intersect_operatorContext intersect_operator() {
			return getRuleContext(Intersect_operatorContext.class,0);
		}
		public Set_qualifierContext set_qualifier() {
			return getRuleContext(Set_qualifierContext.class,0);
		}
		public Intersect_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_intersect_clause; }
	}

	public final Intersect_clauseContext intersect_clause() throws RecognitionException {
		Intersect_clauseContext _localctx = new Intersect_clauseContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_intersect_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(527);
			intersect_operator();
			setState(529);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ALL || _la==DISTINCT) {
				{
				setState(528);
				set_qualifier();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Intersect_operatorContext extends ParserRuleContext {
		public TerminalNode INTERSECT() { return getToken(SQLSelectParserParser.INTERSECT, 0); }
		public Intersect_operatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_intersect_operator; }
	}

	public final Intersect_operatorContext intersect_operator() throws RecognitionException {
		Intersect_operatorContext _localctx = new Intersect_operatorContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_intersect_operator);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(531);
			match(INTERSECT);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Unionized_queryContext extends ParserRuleContext {
		public List<Query_primaryContext> query_primary() {
			return getRuleContexts(Query_primaryContext.class);
		}
		public Query_primaryContext query_primary(int i) {
			return getRuleContext(Query_primaryContext.class,i);
		}
		public List<Union_clauseContext> union_clause() {
			return getRuleContexts(Union_clauseContext.class);
		}
		public Union_clauseContext union_clause(int i) {
			return getRuleContext(Union_clauseContext.class,i);
		}
		public Unionized_queryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unionized_query; }
	}

	public final Unionized_queryContext unionized_query() throws RecognitionException {
		Unionized_queryContext _localctx = new Unionized_queryContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_unionized_query);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(533);
			query_primary();
			setState(539);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EXCEPT || _la==UNION) {
				{
				{
				setState(534);
				union_clause();
				setState(535);
				query_primary();
				}
				}
				setState(541);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Union_clauseContext extends ParserRuleContext {
		public Union_operatorContext union_operator() {
			return getRuleContext(Union_operatorContext.class,0);
		}
		public Set_qualifierContext set_qualifier() {
			return getRuleContext(Set_qualifierContext.class,0);
		}
		public Union_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_union_clause; }
	}

	public final Union_clauseContext union_clause() throws RecognitionException {
		Union_clauseContext _localctx = new Union_clauseContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_union_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(542);
			union_operator();
			setState(544);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ALL || _la==DISTINCT) {
				{
				setState(543);
				set_qualifier();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Union_operatorContext extends ParserRuleContext {
		public TerminalNode UNION() { return getToken(SQLSelectParserParser.UNION, 0); }
		public TerminalNode EXCEPT() { return getToken(SQLSelectParserParser.EXCEPT, 0); }
		public Union_operatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_union_operator; }
	}

	public final Union_operatorContext union_operator() throws RecognitionException {
		Union_operatorContext _localctx = new Union_operatorContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_union_operator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(546);
			_la = _input.LA(1);
			if ( !(_la==EXCEPT || _la==UNION) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Query_primaryContext extends ParserRuleContext {
		public SubqueryContext subquery() {
			return getRuleContext(SubqueryContext.class,0);
		}
		public Query_specificationContext query_specification() {
			return getRuleContext(Query_specificationContext.class,0);
		}
		public Variable_identifierContext variable_identifier() {
			return getRuleContext(Variable_identifierContext.class,0);
		}
		public Query_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query_primary; }
	}

	public final Query_primaryContext query_primary() throws RecognitionException {
		Query_primaryContext _localctx = new Query_primaryContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_query_primary);
		try {
			setState(551);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LEFT_PAREN:
				enterOuterAlt(_localctx, 1);
				{
				setState(548);
				subquery();
				}
				break;
			case SELECT:
				enterOuterAlt(_localctx, 2);
				{
				setState(549);
				query_specification();
				}
				break;
			case Variable_Identifier:
			case Extended_Variable_Identifier:
			case Mixed_Variable_Identifier:
				enterOuterAlt(_localctx, 3);
				{
				setState(550);
				variable_identifier();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SubqueryContext extends ParserRuleContext {
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public Query_expressionContext query_expression() {
			return getRuleContext(Query_expressionContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public SubqueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subquery; }
	}

	public final SubqueryContext subquery() throws RecognitionException {
		SubqueryContext _localctx = new SubqueryContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_subquery);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(553);
			match(LEFT_PAREN);
			setState(554);
			query_expression();
			setState(555);
			match(RIGHT_PAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Query_specificationContext extends ParserRuleContext {
		public TerminalNode SELECT() { return getToken(SQLSelectParserParser.SELECT, 0); }
		public Select_listContext select_list() {
			return getRuleContext(Select_listContext.class,0);
		}
		public Into_listContext into_list() {
			return getRuleContext(Into_listContext.class,0);
		}
		public Set_qualifierContext set_qualifier() {
			return getRuleContext(Set_qualifierContext.class,0);
		}
		public From_clauseContext from_clause() {
			return getRuleContext(From_clauseContext.class,0);
		}
		public Where_clauseContext where_clause() {
			return getRuleContext(Where_clauseContext.class,0);
		}
		public Groupby_clauseContext groupby_clause() {
			return getRuleContext(Groupby_clauseContext.class,0);
		}
		public Having_clauseContext having_clause() {
			return getRuleContext(Having_clauseContext.class,0);
		}
		public Orderby_clauseContext orderby_clause() {
			return getRuleContext(Orderby_clauseContext.class,0);
		}
		public Limit_clauseContext limit_clause() {
			return getRuleContext(Limit_clauseContext.class,0);
		}
		public Query_specificationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query_specification; }
	}

	public final Query_specificationContext query_specification() throws RecognitionException {
		Query_specificationContext _localctx = new Query_specificationContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_query_specification);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(557);
			match(SELECT);
			setState(559);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==INTO) {
				{
				setState(558);
				into_list();
				}
			}

			setState(562);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ALL || _la==DISTINCT) {
				{
				setState(561);
				set_qualifier();
				}
			}

			setState(564);
			select_list();
			setState(581);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FROM) {
				{
				setState(565);
				from_clause();
				setState(567);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WHERE) {
					{
					setState(566);
					where_clause();
					}
				}

				setState(570);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==GROUP) {
					{
					setState(569);
					groupby_clause();
					}
				}

				setState(573);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HAVING) {
					{
					setState(572);
					having_clause();
					}
				}

				setState(576);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ORDER) {
					{
					setState(575);
					orderby_clause();
					}
				}

				setState(579);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LIMIT) {
					{
					setState(578);
					limit_clause();
					}
				}

				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Into_listContext extends ParserRuleContext {
		public TerminalNode INTO() { return getToken(SQLSelectParserParser.INTO, 0); }
		public Table_or_query_nameContext table_or_query_name() {
			return getRuleContext(Table_or_query_nameContext.class,0);
		}
		public Into_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_into_list; }
	}

	public final Into_listContext into_list() throws RecognitionException {
		Into_listContext _localctx = new Into_listContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_into_list);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(583);
			match(INTO);
			setState(584);
			table_or_query_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Set_qualifierContext extends ParserRuleContext {
		public TerminalNode DISTINCT() { return getToken(SQLSelectParserParser.DISTINCT, 0); }
		public TerminalNode ALL() { return getToken(SQLSelectParserParser.ALL, 0); }
		public Set_qualifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_set_qualifier; }
	}

	public final Set_qualifierContext set_qualifier() throws RecognitionException {
		Set_qualifierContext _localctx = new Set_qualifierContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_set_qualifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(586);
			_la = _input.LA(1);
			if ( !(_la==ALL || _la==DISTINCT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Select_listContext extends ParserRuleContext {
		public List<Select_itemContext> select_item() {
			return getRuleContexts(Select_itemContext.class);
		}
		public Select_itemContext select_item(int i) {
			return getRuleContext(Select_itemContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SQLSelectParserParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SQLSelectParserParser.COMMA, i);
		}
		public Select_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_list; }
	}

	public final Select_listContext select_list() throws RecognitionException {
		Select_listContext _localctx = new Select_listContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_select_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(588);
			select_item();
			setState(593);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(589);
				match(COMMA);
				setState(590);
				select_item();
				}
				}
				setState(595);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Select_itemContext extends ParserRuleContext {
		public Value_expressionContext value_expression() {
			return getRuleContext(Value_expressionContext.class,0);
		}
		public As_clauseContext as_clause() {
			return getRuleContext(As_clauseContext.class,0);
		}
		public Select_all_columnsContext select_all_columns() {
			return getRuleContext(Select_all_columnsContext.class,0);
		}
		public Select_itemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_item; }
	}

	public final Select_itemContext select_item() throws RecognitionException {
		Select_itemContext _localctx = new Select_itemContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_select_item);
		try {
			setState(601);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(596);
				value_expression();
				setState(598);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
				case 1:
					{
					setState(597);
					as_clause();
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(600);
				select_all_columns();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class As_clauseContext extends ParserRuleContext {
		public Alias_identifierContext alias_identifier() {
			return getRuleContext(Alias_identifierContext.class,0);
		}
		public TerminalNode AS() { return getToken(SQLSelectParserParser.AS, 0); }
		public As_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_as_clause; }
	}

	public final As_clauseContext as_clause() throws RecognitionException {
		As_clauseContext _localctx = new As_clauseContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_as_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(604);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS) {
				{
				setState(603);
				match(AS);
				}
			}

			setState(606);
			alias_identifier();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Select_all_columnsContext extends ParserRuleContext {
		public Token tb_name;
		public TerminalNode MULTIPLY() { return getToken(SQLSelectParserParser.MULTIPLY, 0); }
		public TerminalNode DOT() { return getToken(SQLSelectParserParser.DOT, 0); }
		public TerminalNode Identifier() { return getToken(SQLSelectParserParser.Identifier, 0); }
		public Select_all_columnsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_all_columns; }
	}

	public final Select_all_columnsContext select_all_columns() throws RecognitionException {
		Select_all_columnsContext _localctx = new Select_all_columnsContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_select_all_columns);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(610);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Identifier) {
				{
				setState(608);
				((Select_all_columnsContext)_localctx).tb_name = match(Identifier);
				setState(609);
				match(DOT);
				}
			}

			setState(612);
			match(MULTIPLY);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class From_clauseContext extends ParserRuleContext {
		public TerminalNode FROM() { return getToken(SQLSelectParserParser.FROM, 0); }
		public Table_reference_listContext table_reference_list() {
			return getRuleContext(Table_reference_listContext.class,0);
		}
		public Join_extensionContext join_extension() {
			return getRuleContext(Join_extensionContext.class,0);
		}
		public From_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_from_clause; }
	}

	public final From_clauseContext from_clause() throws RecognitionException {
		From_clauseContext _localctx = new From_clauseContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_from_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(614);
			match(FROM);
			setState(615);
			table_reference_list();
			setState(617);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 324)) & ~0x3f) == 0 && ((1L << (_la - 324)) & 7L) != 0)) {
				{
				setState(616);
				join_extension();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Join_extensionContext extends ParserRuleContext {
		public Variable_identifierContext variable_identifier() {
			return getRuleContext(Variable_identifierContext.class,0);
		}
		public Join_extensionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_join_extension; }
	}

	public final Join_extensionContext join_extension() throws RecognitionException {
		Join_extensionContext _localctx = new Join_extensionContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_join_extension);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(619);
			variable_identifier();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Table_reference_listContext extends ParserRuleContext {
		public Table_primaryContext right;
		public Join_specificationContext s;
		public List<Table_primaryContext> table_primary() {
			return getRuleContexts(Table_primaryContext.class);
		}
		public Table_primaryContext table_primary(int i) {
			return getRuleContext(Table_primaryContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SQLSelectParserParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SQLSelectParserParser.COMMA, i);
		}
		public List<Unqualified_joinContext> unqualified_join() {
			return getRuleContexts(Unqualified_joinContext.class);
		}
		public Unqualified_joinContext unqualified_join(int i) {
			return getRuleContext(Unqualified_joinContext.class,i);
		}
		public List<Qualified_joinContext> qualified_join() {
			return getRuleContexts(Qualified_joinContext.class);
		}
		public Qualified_joinContext qualified_join(int i) {
			return getRuleContext(Qualified_joinContext.class,i);
		}
		public List<Join_specificationContext> join_specification() {
			return getRuleContexts(Join_specificationContext.class);
		}
		public Join_specificationContext join_specification(int i) {
			return getRuleContext(Join_specificationContext.class,i);
		}
		public Table_reference_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_reference_list; }
	}

	public final Table_reference_listContext table_reference_list() throws RecognitionException {
		Table_reference_listContext _localctx = new Table_reference_listContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_table_reference_list);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(621);
			table_primary();
			setState(634);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,33,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(632);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case COMMA:
						{
						{
						setState(622);
						match(COMMA);
						setState(623);
						table_primary();
						}
						}
						break;
					case CROSS:
					case NATURAL:
					case UNION:
						{
						{
						setState(624);
						unqualified_join();
						setState(625);
						((Table_reference_listContext)_localctx).right = table_primary();
						}
						}
						break;
					case FULL:
					case INNER:
					case JOIN:
					case LEFT:
					case RIGHT:
						{
						{
						setState(627);
						qualified_join();
						setState(628);
						((Table_reference_listContext)_localctx).right = table_primary();
						setState(630);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==ON || _la==USING) {
							{
							setState(629);
							((Table_reference_listContext)_localctx).s = join_specification();
							}
						}

						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					} 
				}
				setState(636);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,33,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Join_extension_primaryContext extends ParserRuleContext {
		public Table_primaryContext right;
		public Join_specificationContext s;
		public Join_extensionContext join_extension() {
			return getRuleContext(Join_extensionContext.class,0);
		}
		public List<TerminalNode> COMMA() { return getTokens(SQLSelectParserParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SQLSelectParserParser.COMMA, i);
		}
		public List<Table_primaryContext> table_primary() {
			return getRuleContexts(Table_primaryContext.class);
		}
		public Table_primaryContext table_primary(int i) {
			return getRuleContext(Table_primaryContext.class,i);
		}
		public List<Unqualified_joinContext> unqualified_join() {
			return getRuleContexts(Unqualified_joinContext.class);
		}
		public Unqualified_joinContext unqualified_join(int i) {
			return getRuleContext(Unqualified_joinContext.class,i);
		}
		public List<Qualified_joinContext> qualified_join() {
			return getRuleContexts(Qualified_joinContext.class);
		}
		public Qualified_joinContext qualified_join(int i) {
			return getRuleContext(Qualified_joinContext.class,i);
		}
		public List<Join_specificationContext> join_specification() {
			return getRuleContexts(Join_specificationContext.class);
		}
		public Join_specificationContext join_specification(int i) {
			return getRuleContext(Join_specificationContext.class,i);
		}
		public Join_extension_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_join_extension_primary; }
	}

	public final Join_extension_primaryContext join_extension_primary() throws RecognitionException {
		Join_extension_primaryContext _localctx = new Join_extension_primaryContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_join_extension_primary);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(649);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 9016000322274304L) != 0) || _la==COMMA) {
				{
				setState(647);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case COMMA:
					{
					{
					setState(637);
					match(COMMA);
					setState(638);
					table_primary();
					}
					}
					break;
				case CROSS:
				case NATURAL:
				case UNION:
					{
					{
					setState(639);
					unqualified_join();
					setState(640);
					((Join_extension_primaryContext)_localctx).right = table_primary();
					}
					}
					break;
				case FULL:
				case INNER:
				case JOIN:
				case LEFT:
				case RIGHT:
					{
					{
					setState(642);
					qualified_join();
					setState(643);
					((Join_extension_primaryContext)_localctx).right = table_primary();
					setState(645);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==ON || _la==USING) {
						{
						setState(644);
						((Join_extension_primaryContext)_localctx).s = join_specification();
						}
					}

					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(651);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(653);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 324)) & ~0x3f) == 0 && ((1L << (_la - 324)) & 7L) != 0)) {
				{
				setState(652);
				join_extension();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Table_primaryContext extends ParserRuleContext {
		public Table_or_query_nameContext table_or_query_name() {
			return getRuleContext(Table_or_query_nameContext.class,0);
		}
		public As_clauseContext as_clause() {
			return getRuleContext(As_clauseContext.class,0);
		}
		public SubqueryContext subquery() {
			return getRuleContext(SubqueryContext.class,0);
		}
		public Variable_identifierContext variable_identifier() {
			return getRuleContext(Variable_identifierContext.class,0);
		}
		public Values_statement_primaryContext values_statement_primary() {
			return getRuleContext(Values_statement_primaryContext.class,0);
		}
		public Table_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_primary; }
	}

	public final Table_primaryContext table_primary() throws RecognitionException {
		Table_primaryContext _localctx = new Table_primaryContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_table_primary);
		try {
			setState(667);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(655);
				table_or_query_name();
				setState(657);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
				case 1:
					{
					setState(656);
					as_clause();
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(659);
				subquery();
				setState(661);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
				case 1:
					{
					setState(660);
					as_clause();
					}
					break;
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(663);
				variable_identifier();
				setState(664);
				as_clause();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(666);
				values_statement_primary();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Tuple_primaryContext extends ParserRuleContext {
		public Table_or_query_nameContext table_or_query_name() {
			return getRuleContext(Table_or_query_nameContext.class,0);
		}
		public SubqueryContext subquery() {
			return getRuleContext(SubqueryContext.class,0);
		}
		public Variable_identifierContext variable_identifier() {
			return getRuleContext(Variable_identifierContext.class,0);
		}
		public Fully_defined_values_statementContext fully_defined_values_statement() {
			return getRuleContext(Fully_defined_values_statementContext.class,0);
		}
		public Tuple_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tuple_primary; }
	}

	public final Tuple_primaryContext tuple_primary() throws RecognitionException {
		Tuple_primaryContext _localctx = new Tuple_primaryContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_tuple_primary);
		try {
			setState(673);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,41,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(669);
				table_or_query_name();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(670);
				subquery();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(671);
				variable_identifier();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(672);
				fully_defined_values_statement();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Table_or_query_nameContext extends ParserRuleContext {
		public List<IdentifierContext> identifier() {
			return getRuleContexts(IdentifierContext.class);
		}
		public IdentifierContext identifier(int i) {
			return getRuleContext(IdentifierContext.class,i);
		}
		public List<TerminalNode> DOT() { return getTokens(SQLSelectParserParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(SQLSelectParserParser.DOT, i);
		}
		public List<Simple_numeric_identifierContext> simple_numeric_identifier() {
			return getRuleContexts(Simple_numeric_identifierContext.class);
		}
		public Simple_numeric_identifierContext simple_numeric_identifier(int i) {
			return getRuleContext(Simple_numeric_identifierContext.class,i);
		}
		public Table_or_query_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_or_query_name; }
	}

	public final Table_or_query_nameContext table_or_query_name() throws RecognitionException {
		Table_or_query_nameContext _localctx = new Table_or_query_nameContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_table_or_query_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(675);
			identifier();
			setState(681);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,43,_ctx) ) {
			case 1:
				{
				setState(676);
				match(DOT);
				setState(679);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case NUMBER:
				case Numeric_Identifier:
					{
					setState(677);
					simple_numeric_identifier();
					}
					break;
				case IGNORE:
				case NULLS:
				case NUMBER_TYPE:
				case RESPECT:
				case RETURNING:
				case UNION:
				case WITH:
				case ASC:
				case AVG:
				case BETWEEN:
				case BY:
				case CENTURY:
				case CHARACTER:
				case COLLECT:
				case COALESCE:
				case COLUMN:
				case COUNT:
				case CUBE:
				case DAY:
				case DEC:
				case DECADE:
				case DESC:
				case DOW:
				case DOY:
				case DROP:
				case EPOCH:
				case EVERY:
				case EXISTS:
				case EXTERNAL:
				case EXTRACT:
				case FILTER:
				case FIRST:
				case FORMAT:
				case FUSION:
				case GROUPING:
				case HASH:
				case INDEX:
				case INSERT:
				case INTERSECTION:
				case ISODOW:
				case ISOYEAR:
				case LAST:
				case LESS:
				case LIST:
				case LOCATION:
				case MAX:
				case MAXVALUE:
				case MICROSECONDS:
				case MILLENNIUM:
				case MILLISECONDS:
				case MIN:
				case MINUTE:
				case MONTH:
				case NATIONAL:
				case NULLIF:
				case OVER:
				case OVERWRITE:
				case PARTITION:
				case PARTITIONS:
				case PRECISION:
				case PURGE:
				case QUARTER:
				case RANGE:
				case RANK:
				case REGEXP:
				case RLIKE:
				case ROLLUP:
				case ROWS:
				case SECOND:
				case SET:
				case SIMILAR:
				case STDDEV_POP:
				case STDDEV_SAMP:
				case SUBPARTITION:
				case SUM:
				case TABLESPACE:
				case THAN:
				case TIMEZONE:
				case TIMEZONE_HOUR:
				case TIMEZONE_MINUTE:
				case TRIM:
				case TO:
				case UPDATE:
				case UNKNOWN:
				case VALUES:
				case VAR_SAMP:
				case VAR_POP:
				case VARYING:
				case WEEK:
				case YEAR:
				case ZONE:
				case ANY_VALUE:
				case CORR:
				case COVAR_POP:
				case COVAR_SAMP:
				case LISTAGG:
				case MEDIAN:
				case PERCENTILE_CONT:
				case PERCENTILE_DISC:
				case STDDEV:
				case VARIANCE_POP:
				case VARIANCE:
				case VARIANCE_SAMP:
				case CUME_DIST:
				case DENSE_RANK:
				case NTILE:
				case PERCENT_RANK:
				case WIDTH_BUCKET:
				case BITAND_AGG:
				case BITOR_AGG:
				case BITXOR_AGG:
				case HASH_AGG:
				case ARRAY_AGG:
				case OBJECT_AGG:
				case REGR_AVGX:
				case REGR_AVGY:
				case REGR_COUNT:
				case REGR_INTERCEPT:
				case REGR_R2:
				case REGR_SLOPE:
				case REGR_SXX:
				case REGR_SXY:
				case REGR_SYY:
				case APPROX_COUNT_DISTINCT:
				case HLL:
				case HLL_ACCUMULATE:
				case HLL_COMBINE:
				case HLL_EXPORT:
				case HLL_IMPORT:
				case APPROXIMATE_JACCARD_INDEX:
				case APPROXIMATE_SIMILARITY:
				case MINHASH:
				case MINHASH_COMBINE:
				case APPROX_TOP_K:
				case APPROX_TOP_K_ACCUMULATE:
				case APPROX_TOP_K_COMBINE:
				case APPROX_PERCENTILE:
				case APPROX_PERCENTILE_ACCUMULATE:
				case APPROX_PERCENTILE_COMBINE:
				case ABSTIME:
				case ANYARRAY:
				case ARRAY:
				case BOOL:
				case BIT:
				case VARBIT:
				case CIDR:
				case INET:
				case INET4:
				case INTERVAL:
				case INT1:
				case INT2:
				case INT4:
				case INT8:
				case JSON:
				case JSONB:
				case MACADDR:
				case NAME:
				case OID:
				case PG_LSN:
				case PG_NODE_TREE:
				case REGPROC:
				case XID:
				case UUID:
				case TINYINT:
				case SMALLINT:
				case INT:
				case BIGINT:
				case BIGSERIAL:
				case SMALLSERIAL:
				case SERIAL:
				case MONEY:
				case FLOAT4:
				case FLOAT8:
				case REAL:
				case FLOAT:
				case DOUBLE:
				case NUMERIC:
				case CHAR:
				case VARCHAR:
				case NCHAR:
				case NVARCHAR:
				case STRING:
				case DATE:
				case DATETIME:
				case TIME:
				case TIMETZ:
				case TIMESTAMP:
				case TIMESTAMP_LTZ:
				case TIMESTAMP_NTZ:
				case TIMESTAMP_TZ:
				case TIMESTAMPTZ:
				case TEXT:
				case BINARY:
				case VARBINARY:
				case BLOB:
				case BYTEA:
				case OBJECT:
				case STRUCT:
				case VARIANT:
				case Bracket_Identifier:
				case Identifier:
				case Double_Quoted_Numeric_Identifier:
				case Dollar_Sign_Identifier:
					{
					setState(678);
					identifier();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			}
			setState(688);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
			case 1:
				{
				setState(683);
				match(DOT);
				setState(686);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case NUMBER:
				case Numeric_Identifier:
					{
					setState(684);
					simple_numeric_identifier();
					}
					break;
				case IGNORE:
				case NULLS:
				case NUMBER_TYPE:
				case RESPECT:
				case RETURNING:
				case UNION:
				case WITH:
				case ASC:
				case AVG:
				case BETWEEN:
				case BY:
				case CENTURY:
				case CHARACTER:
				case COLLECT:
				case COALESCE:
				case COLUMN:
				case COUNT:
				case CUBE:
				case DAY:
				case DEC:
				case DECADE:
				case DESC:
				case DOW:
				case DOY:
				case DROP:
				case EPOCH:
				case EVERY:
				case EXISTS:
				case EXTERNAL:
				case EXTRACT:
				case FILTER:
				case FIRST:
				case FORMAT:
				case FUSION:
				case GROUPING:
				case HASH:
				case INDEX:
				case INSERT:
				case INTERSECTION:
				case ISODOW:
				case ISOYEAR:
				case LAST:
				case LESS:
				case LIST:
				case LOCATION:
				case MAX:
				case MAXVALUE:
				case MICROSECONDS:
				case MILLENNIUM:
				case MILLISECONDS:
				case MIN:
				case MINUTE:
				case MONTH:
				case NATIONAL:
				case NULLIF:
				case OVER:
				case OVERWRITE:
				case PARTITION:
				case PARTITIONS:
				case PRECISION:
				case PURGE:
				case QUARTER:
				case RANGE:
				case RANK:
				case REGEXP:
				case RLIKE:
				case ROLLUP:
				case ROWS:
				case SECOND:
				case SET:
				case SIMILAR:
				case STDDEV_POP:
				case STDDEV_SAMP:
				case SUBPARTITION:
				case SUM:
				case TABLESPACE:
				case THAN:
				case TIMEZONE:
				case TIMEZONE_HOUR:
				case TIMEZONE_MINUTE:
				case TRIM:
				case TO:
				case UPDATE:
				case UNKNOWN:
				case VALUES:
				case VAR_SAMP:
				case VAR_POP:
				case VARYING:
				case WEEK:
				case YEAR:
				case ZONE:
				case ANY_VALUE:
				case CORR:
				case COVAR_POP:
				case COVAR_SAMP:
				case LISTAGG:
				case MEDIAN:
				case PERCENTILE_CONT:
				case PERCENTILE_DISC:
				case STDDEV:
				case VARIANCE_POP:
				case VARIANCE:
				case VARIANCE_SAMP:
				case CUME_DIST:
				case DENSE_RANK:
				case NTILE:
				case PERCENT_RANK:
				case WIDTH_BUCKET:
				case BITAND_AGG:
				case BITOR_AGG:
				case BITXOR_AGG:
				case HASH_AGG:
				case ARRAY_AGG:
				case OBJECT_AGG:
				case REGR_AVGX:
				case REGR_AVGY:
				case REGR_COUNT:
				case REGR_INTERCEPT:
				case REGR_R2:
				case REGR_SLOPE:
				case REGR_SXX:
				case REGR_SXY:
				case REGR_SYY:
				case APPROX_COUNT_DISTINCT:
				case HLL:
				case HLL_ACCUMULATE:
				case HLL_COMBINE:
				case HLL_EXPORT:
				case HLL_IMPORT:
				case APPROXIMATE_JACCARD_INDEX:
				case APPROXIMATE_SIMILARITY:
				case MINHASH:
				case MINHASH_COMBINE:
				case APPROX_TOP_K:
				case APPROX_TOP_K_ACCUMULATE:
				case APPROX_TOP_K_COMBINE:
				case APPROX_PERCENTILE:
				case APPROX_PERCENTILE_ACCUMULATE:
				case APPROX_PERCENTILE_COMBINE:
				case ABSTIME:
				case ANYARRAY:
				case ARRAY:
				case BOOL:
				case BIT:
				case VARBIT:
				case CIDR:
				case INET:
				case INET4:
				case INTERVAL:
				case INT1:
				case INT2:
				case INT4:
				case INT8:
				case JSON:
				case JSONB:
				case MACADDR:
				case NAME:
				case OID:
				case PG_LSN:
				case PG_NODE_TREE:
				case REGPROC:
				case XID:
				case UUID:
				case TINYINT:
				case SMALLINT:
				case INT:
				case BIGINT:
				case BIGSERIAL:
				case SMALLSERIAL:
				case SERIAL:
				case MONEY:
				case FLOAT4:
				case FLOAT8:
				case REAL:
				case FLOAT:
				case DOUBLE:
				case NUMERIC:
				case CHAR:
				case VARCHAR:
				case NCHAR:
				case NVARCHAR:
				case STRING:
				case DATE:
				case DATETIME:
				case TIME:
				case TIMETZ:
				case TIMESTAMP:
				case TIMESTAMP_LTZ:
				case TIMESTAMP_NTZ:
				case TIMESTAMP_TZ:
				case TIMESTAMPTZ:
				case TEXT:
				case BINARY:
				case VARBINARY:
				case BLOB:
				case BYTEA:
				case OBJECT:
				case STRUCT:
				case VARIANT:
				case Bracket_Identifier:
				case Identifier:
				case Double_Quoted_Numeric_Identifier:
				case Dollar_Sign_Identifier:
					{
					setState(685);
					identifier();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Unqualified_joinContext extends ParserRuleContext {
		public Join_typeContext t;
		public TerminalNode CROSS() { return getToken(SQLSelectParserParser.CROSS, 0); }
		public TerminalNode JOIN() { return getToken(SQLSelectParserParser.JOIN, 0); }
		public TerminalNode UNION() { return getToken(SQLSelectParserParser.UNION, 0); }
		public TerminalNode NATURAL() { return getToken(SQLSelectParserParser.NATURAL, 0); }
		public Join_typeContext join_type() {
			return getRuleContext(Join_typeContext.class,0);
		}
		public Unqualified_joinContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unqualified_join; }
	}

	public final Unqualified_joinContext unqualified_join() throws RecognitionException {
		Unqualified_joinContext _localctx = new Unqualified_joinContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_unqualified_join);
		int _la;
		try {
			setState(699);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CROSS:
				enterOuterAlt(_localctx, 1);
				{
				setState(690);
				match(CROSS);
				setState(691);
				match(JOIN);
				}
				break;
			case UNION:
				enterOuterAlt(_localctx, 2);
				{
				setState(692);
				match(UNION);
				setState(693);
				match(JOIN);
				}
				break;
			case NATURAL:
				enterOuterAlt(_localctx, 3);
				{
				setState(694);
				match(NATURAL);
				setState(696);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 8796638347264L) != 0)) {
					{
					setState(695);
					((Unqualified_joinContext)_localctx).t = join_type();
					}
				}

				setState(698);
				match(JOIN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Qualified_joinContext extends ParserRuleContext {
		public Join_typeContext t;
		public TerminalNode JOIN() { return getToken(SQLSelectParserParser.JOIN, 0); }
		public Join_typeContext join_type() {
			return getRuleContext(Join_typeContext.class,0);
		}
		public Qualified_joinContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qualified_join; }
	}

	public final Qualified_joinContext qualified_join() throws RecognitionException {
		Qualified_joinContext _localctx = new Qualified_joinContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_qualified_join);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(702);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 8796638347264L) != 0)) {
				{
				setState(701);
				((Qualified_joinContext)_localctx).t = join_type();
				}
			}

			setState(704);
			match(JOIN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Join_typeContext extends ParserRuleContext {
		public TerminalNode INNER() { return getToken(SQLSelectParserParser.INNER, 0); }
		public TerminalNode LEFT() { return getToken(SQLSelectParserParser.LEFT, 0); }
		public TerminalNode RIGHT() { return getToken(SQLSelectParserParser.RIGHT, 0); }
		public TerminalNode FULL() { return getToken(SQLSelectParserParser.FULL, 0); }
		public TerminalNode OUTER() { return getToken(SQLSelectParserParser.OUTER, 0); }
		public Join_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_join_type; }
	}

	public final Join_typeContext join_type() throws RecognitionException {
		Join_typeContext _localctx = new Join_typeContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_join_type);
		int _la;
		try {
			setState(711);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INNER:
				enterOuterAlt(_localctx, 1);
				{
				setState(706);
				match(INNER);
				}
				break;
			case FULL:
			case LEFT:
			case RIGHT:
				enterOuterAlt(_localctx, 2);
				{
				setState(707);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 8796629958656L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(709);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OUTER) {
					{
					setState(708);
					match(OUTER);
					}
				}

				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Join_specificationContext extends ParserRuleContext {
		public Join_conditionContext join_condition() {
			return getRuleContext(Join_conditionContext.class,0);
		}
		public Named_columns_joinContext named_columns_join() {
			return getRuleContext(Named_columns_joinContext.class,0);
		}
		public Join_specificationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_join_specification; }
	}

	public final Join_specificationContext join_specification() throws RecognitionException {
		Join_specificationContext _localctx = new Join_specificationContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_join_specification);
		try {
			setState(715);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ON:
				enterOuterAlt(_localctx, 1);
				{
				setState(713);
				join_condition();
				}
				break;
			case USING:
				enterOuterAlt(_localctx, 2);
				{
				setState(714);
				named_columns_join();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Join_conditionContext extends ParserRuleContext {
		public TerminalNode ON() { return getToken(SQLSelectParserParser.ON, 0); }
		public Search_conditionContext search_condition() {
			return getRuleContext(Search_conditionContext.class,0);
		}
		public Join_conditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_join_condition; }
	}

	public final Join_conditionContext join_condition() throws RecognitionException {
		Join_conditionContext _localctx = new Join_conditionContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_join_condition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(717);
			match(ON);
			setState(718);
			search_condition();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Named_columns_joinContext extends ParserRuleContext {
		public Column_reference_listContext f;
		public Using_termContext using_term() {
			return getRuleContext(Using_termContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Column_reference_listContext column_reference_list() {
			return getRuleContext(Column_reference_listContext.class,0);
		}
		public Named_columns_joinContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_named_columns_join; }
	}

	public final Named_columns_joinContext named_columns_join() throws RecognitionException {
		Named_columns_joinContext _localctx = new Named_columns_joinContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_named_columns_join);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(720);
			using_term();
			setState(721);
			match(LEFT_PAREN);
			setState(722);
			((Named_columns_joinContext)_localctx).f = column_reference_list();
			setState(723);
			match(RIGHT_PAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Using_termContext extends ParserRuleContext {
		public TerminalNode USING() { return getToken(SQLSelectParserParser.USING, 0); }
		public Using_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_using_term; }
	}

	public final Using_termContext using_term() throws RecognitionException {
		Using_termContext _localctx = new Using_termContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_using_term);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(725);
			match(USING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Column_reference_listContext extends ParserRuleContext {
		public List<Column_referenceContext> column_reference() {
			return getRuleContexts(Column_referenceContext.class);
		}
		public Column_referenceContext column_reference(int i) {
			return getRuleContext(Column_referenceContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SQLSelectParserParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SQLSelectParserParser.COMMA, i);
		}
		public Column_reference_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_column_reference_list; }
	}

	public final Column_reference_listContext column_reference_list() throws RecognitionException {
		Column_reference_listContext _localctx = new Column_reference_listContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_column_reference_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(727);
			column_reference();
			setState(732);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(728);
				match(COMMA);
				setState(729);
				column_reference();
				}
				}
				setState(734);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Column_referenceContext extends ParserRuleContext {
		public IdentifierContext tb_name;
		public IdentifierContext name;
		public Variable_identifierContext substitution;
		public List<IdentifierContext> identifier() {
			return getRuleContexts(IdentifierContext.class);
		}
		public IdentifierContext identifier(int i) {
			return getRuleContext(IdentifierContext.class,i);
		}
		public TerminalNode DOT() { return getToken(SQLSelectParserParser.DOT, 0); }
		public Variable_identifierContext variable_identifier() {
			return getRuleContext(Variable_identifierContext.class,0);
		}
		public Column_referenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_column_reference; }
	}

	public final Column_referenceContext column_reference() throws RecognitionException {
		Column_referenceContext _localctx = new Column_referenceContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_column_reference);
		try {
			setState(745);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,54,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(738);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
				case 1:
					{
					setState(735);
					((Column_referenceContext)_localctx).tb_name = identifier();
					setState(736);
					match(DOT);
					}
					break;
				}
				setState(740);
				((Column_referenceContext)_localctx).name = identifier();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(741);
				((Column_referenceContext)_localctx).tb_name = identifier();
				setState(742);
				match(DOT);
				setState(743);
				((Column_referenceContext)_localctx).substitution = variable_identifier();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Column_primaryContext extends ParserRuleContext {
		public IdentifierContext tb_name;
		public IdentifierContext name;
		public Variable_identifierContext substitution;
		public List<IdentifierContext> identifier() {
			return getRuleContexts(IdentifierContext.class);
		}
		public IdentifierContext identifier(int i) {
			return getRuleContext(IdentifierContext.class,i);
		}
		public TerminalNode DOT() { return getToken(SQLSelectParserParser.DOT, 0); }
		public Variable_identifierContext variable_identifier() {
			return getRuleContext(Variable_identifierContext.class,0);
		}
		public Column_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_column_primary; }
	}

	public final Column_primaryContext column_primary() throws RecognitionException {
		Column_primaryContext _localctx = new Column_primaryContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_column_primary);
		try {
			setState(758);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,56,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(750);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,55,_ctx) ) {
				case 1:
					{
					setState(747);
					((Column_primaryContext)_localctx).tb_name = identifier();
					setState(748);
					match(DOT);
					}
					break;
				}
				setState(752);
				((Column_primaryContext)_localctx).name = identifier();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(753);
				((Column_primaryContext)_localctx).tb_name = identifier();
				setState(754);
				match(DOT);
				setState(755);
				((Column_primaryContext)_localctx).substitution = variable_identifier();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(757);
				((Column_primaryContext)_localctx).substitution = variable_identifier();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Predicand_primaryContext extends ParserRuleContext {
		public Value_expression_primaryContext value_expression_primary() {
			return getRuleContext(Value_expression_primaryContext.class,0);
		}
		public TerminalNode CAST_OPERATOR() { return getToken(SQLSelectParserParser.CAST_OPERATOR, 0); }
		public Data_typeContext data_type() {
			return getRuleContext(Data_typeContext.class,0);
		}
		public Trim_functionContext trim_function() {
			return getRuleContext(Trim_functionContext.class,0);
		}
		public Null_literalContext null_literal() {
			return getRuleContext(Null_literalContext.class,0);
		}
		public Variable_identifierContext variable_identifier() {
			return getRuleContext(Variable_identifierContext.class,0);
		}
		public Puml_constant_identifierContext puml_constant_identifier() {
			return getRuleContext(Puml_constant_identifierContext.class,0);
		}
		public Predicand_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicand_primary; }
	}

	public final Predicand_primaryContext predicand_primary() throws RecognitionException {
		Predicand_primaryContext _localctx = new Predicand_primaryContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_predicand_primary);
		int _la;
		try {
			setState(769);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,58,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(760);
				value_expression_primary();
				setState(763);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CAST_OPERATOR) {
					{
					setState(761);
					match(CAST_OPERATOR);
					setState(762);
					data_type();
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(765);
				trim_function();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(766);
				null_literal();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(767);
				variable_identifier();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(768);
				puml_constant_identifier();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Value_expression_primaryContext extends ParserRuleContext {
		public Parenthesized_value_expressionContext parenthesized_value_expression() {
			return getRuleContext(Parenthesized_value_expressionContext.class,0);
		}
		public Nonparenthesized_value_expression_primaryContext nonparenthesized_value_expression_primary() {
			return getRuleContext(Nonparenthesized_value_expression_primaryContext.class,0);
		}
		public Value_expression_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value_expression_primary; }
	}

	public final Value_expression_primaryContext value_expression_primary() throws RecognitionException {
		Value_expression_primaryContext _localctx = new Value_expression_primaryContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_value_expression_primary);
		try {
			setState(773);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,59,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(771);
				parenthesized_value_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(772);
				nonparenthesized_value_expression_primary();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Parenthesized_value_expressionContext extends ParserRuleContext {
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public Value_expressionContext value_expression() {
			return getRuleContext(Value_expressionContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Parenthesized_value_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parenthesized_value_expression; }
	}

	public final Parenthesized_value_expressionContext parenthesized_value_expression() throws RecognitionException {
		Parenthesized_value_expressionContext _localctx = new Parenthesized_value_expressionContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_parenthesized_value_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(775);
			match(LEFT_PAREN);
			setState(776);
			value_expression();
			setState(777);
			match(RIGHT_PAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Nonparenthesized_value_expression_primaryContext extends ParserRuleContext {
		public Unsigned_literalContext unsigned_literal() {
			return getRuleContext(Unsigned_literalContext.class,0);
		}
		public Column_referenceContext column_reference() {
			return getRuleContext(Column_referenceContext.class,0);
		}
		public Aggregate_functionContext aggregate_function() {
			return getRuleContext(Aggregate_functionContext.class,0);
		}
		public Case_expressionContext case_expression() {
			return getRuleContext(Case_expressionContext.class,0);
		}
		public Cast_function_expressionContext cast_function_expression() {
			return getRuleContext(Cast_function_expressionContext.class,0);
		}
		public Routine_invocationContext routine_invocation() {
			return getRuleContext(Routine_invocationContext.class,0);
		}
		public Window_over_partition_expressionContext window_over_partition_expression() {
			return getRuleContext(Window_over_partition_expressionContext.class,0);
		}
		public SubqueryContext subquery() {
			return getRuleContext(SubqueryContext.class,0);
		}
		public Nonparenthesized_value_expression_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nonparenthesized_value_expression_primary; }
	}

	public final Nonparenthesized_value_expression_primaryContext nonparenthesized_value_expression_primary() throws RecognitionException {
		Nonparenthesized_value_expression_primaryContext _localctx = new Nonparenthesized_value_expression_primaryContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_nonparenthesized_value_expression_primary);
		try {
			setState(787);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(779);
				unsigned_literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(780);
				column_reference();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(781);
				aggregate_function();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(782);
				case_expression();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(783);
				cast_function_expression();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(784);
				routine_invocation();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(785);
				window_over_partition_expression();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(786);
				subquery();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Aggregate_functionContext extends ParserRuleContext {
		public Aggregate_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aggregate_function; }
	 
		public Aggregate_functionContext() { }
		public void copyFrom(Aggregate_functionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Count_all_aggregateContext extends Aggregate_functionContext {
		public TerminalNode COUNT() { return getToken(SQLSelectParserParser.COUNT, 0); }
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public TerminalNode MULTIPLY() { return getToken(SQLSelectParserParser.MULTIPLY, 0); }
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Count_all_aggregateContext(Aggregate_functionContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class General_set_functionContext extends Aggregate_functionContext {
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public Value_expressionContext value_expression() {
			return getRuleContext(Value_expressionContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Set_function_typeContext set_function_type() {
			return getRuleContext(Set_function_typeContext.class,0);
		}
		public Set_qualifier_typeContext set_qualifier_type() {
			return getRuleContext(Set_qualifier_typeContext.class,0);
		}
		public Set_qualifierContext set_qualifier() {
			return getRuleContext(Set_qualifierContext.class,0);
		}
		public General_set_functionContext(Aggregate_functionContext ctx) { copyFrom(ctx); }
	}

	public final Aggregate_functionContext aggregate_function() throws RecognitionException {
		Aggregate_functionContext _localctx = new Aggregate_functionContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_aggregate_function);
		int _la;
		try {
			setState(804);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,63,_ctx) ) {
			case 1:
				_localctx = new Count_all_aggregateContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(789);
				match(COUNT);
				setState(790);
				match(LEFT_PAREN);
				setState(791);
				match(MULTIPLY);
				setState(792);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				_localctx = new General_set_functionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(795);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case AVG:
				case COUNT:
				case COUNT_IF:
				case FIRST_VALUE:
				case LAG:
				case LAST_VALUE:
				case LEAD:
				case MAX:
				case MIN:
				case NTH_VALUE:
				case RANK:
				case ROW_NUMBER:
				case STDDEV_POP:
				case STDDEV_SAMP:
				case SUM:
				case VAR_SAMP:
				case VAR_POP:
				case ANY_VALUE:
				case CORR:
				case COVAR_POP:
				case COVAR_SAMP:
				case LISTAGG:
				case MEDIAN:
				case PERCENTILE_CONT:
				case PERCENTILE_DISC:
				case STDDEV:
				case VARIANCE_POP:
				case VARIANCE:
				case VARIANCE_SAMP:
				case CUME_DIST:
				case DENSE_RANK:
				case NTILE:
				case PERCENT_RANK:
				case WIDTH_BUCKET:
				case BITAND_AGG:
				case BITOR_AGG:
				case BITXOR_AGG:
				case HASH_AGG:
				case ARRAY_AGG:
				case OBJECT_AGG:
				case REGR_AVGX:
				case REGR_AVGY:
				case REGR_COUNT:
				case REGR_INTERCEPT:
				case REGR_R2:
				case REGR_SLOPE:
				case REGR_SXX:
				case REGR_SXY:
				case REGR_SYY:
				case APPROX_COUNT_DISTINCT:
				case HLL:
				case HLL_ACCUMULATE:
				case HLL_COMBINE:
				case HLL_EXPORT:
				case HLL_IMPORT:
				case APPROXIMATE_JACCARD_INDEX:
				case APPROXIMATE_SIMILARITY:
				case MINHASH:
				case MINHASH_COMBINE:
				case APPROX_TOP_K:
				case APPROX_TOP_K_ACCUMULATE:
				case APPROX_TOP_K_COMBINE:
				case APPROX_PERCENTILE:
				case APPROX_PERCENTILE_ACCUMULATE:
				case APPROX_PERCENTILE_COMBINE:
					{
					setState(793);
					set_function_type();
					}
					break;
				case ANY:
				case SOME:
				case COLLECT:
				case EVERY:
				case FUSION:
				case INTERSECTION:
					{
					setState(794);
					set_qualifier_type();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(797);
				match(LEFT_PAREN);
				setState(799);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ALL || _la==DISTINCT) {
					{
					setState(798);
					set_qualifier();
					}
				}

				setState(801);
				value_expression();
				setState(802);
				match(RIGHT_PAREN);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Set_function_typeContext extends ParserRuleContext {
		public TerminalNode AVG() { return getToken(SQLSelectParserParser.AVG, 0); }
		public TerminalNode FIRST_VALUE() { return getToken(SQLSelectParserParser.FIRST_VALUE, 0); }
		public TerminalNode LAG() { return getToken(SQLSelectParserParser.LAG, 0); }
		public TerminalNode LAST_VALUE() { return getToken(SQLSelectParserParser.LAST_VALUE, 0); }
		public TerminalNode LEAD() { return getToken(SQLSelectParserParser.LEAD, 0); }
		public TerminalNode MAX() { return getToken(SQLSelectParserParser.MAX, 0); }
		public TerminalNode MIN() { return getToken(SQLSelectParserParser.MIN, 0); }
		public TerminalNode NTH_VALUE() { return getToken(SQLSelectParserParser.NTH_VALUE, 0); }
		public TerminalNode SUM() { return getToken(SQLSelectParserParser.SUM, 0); }
		public TerminalNode COUNT() { return getToken(SQLSelectParserParser.COUNT, 0); }
		public TerminalNode RANK() { return getToken(SQLSelectParserParser.RANK, 0); }
		public TerminalNode ROW_NUMBER() { return getToken(SQLSelectParserParser.ROW_NUMBER, 0); }
		public TerminalNode STDDEV_POP() { return getToken(SQLSelectParserParser.STDDEV_POP, 0); }
		public TerminalNode STDDEV_SAMP() { return getToken(SQLSelectParserParser.STDDEV_SAMP, 0); }
		public TerminalNode VAR_SAMP() { return getToken(SQLSelectParserParser.VAR_SAMP, 0); }
		public TerminalNode VAR_POP() { return getToken(SQLSelectParserParser.VAR_POP, 0); }
		public TerminalNode ANY_VALUE() { return getToken(SQLSelectParserParser.ANY_VALUE, 0); }
		public TerminalNode CORR() { return getToken(SQLSelectParserParser.CORR, 0); }
		public TerminalNode COUNT_IF() { return getToken(SQLSelectParserParser.COUNT_IF, 0); }
		public TerminalNode COVAR_POP() { return getToken(SQLSelectParserParser.COVAR_POP, 0); }
		public TerminalNode COVAR_SAMP() { return getToken(SQLSelectParserParser.COVAR_SAMP, 0); }
		public TerminalNode LISTAGG() { return getToken(SQLSelectParserParser.LISTAGG, 0); }
		public TerminalNode MEDIAN() { return getToken(SQLSelectParserParser.MEDIAN, 0); }
		public TerminalNode PERCENTILE_CONT() { return getToken(SQLSelectParserParser.PERCENTILE_CONT, 0); }
		public TerminalNode PERCENTILE_DISC() { return getToken(SQLSelectParserParser.PERCENTILE_DISC, 0); }
		public TerminalNode STDDEV() { return getToken(SQLSelectParserParser.STDDEV, 0); }
		public TerminalNode VARIANCE_POP() { return getToken(SQLSelectParserParser.VARIANCE_POP, 0); }
		public TerminalNode VARIANCE() { return getToken(SQLSelectParserParser.VARIANCE, 0); }
		public TerminalNode VARIANCE_SAMP() { return getToken(SQLSelectParserParser.VARIANCE_SAMP, 0); }
		public TerminalNode CUME_DIST() { return getToken(SQLSelectParserParser.CUME_DIST, 0); }
		public TerminalNode DENSE_RANK() { return getToken(SQLSelectParserParser.DENSE_RANK, 0); }
		public TerminalNode NTILE() { return getToken(SQLSelectParserParser.NTILE, 0); }
		public TerminalNode PERCENT_RANK() { return getToken(SQLSelectParserParser.PERCENT_RANK, 0); }
		public TerminalNode WIDTH_BUCKET() { return getToken(SQLSelectParserParser.WIDTH_BUCKET, 0); }
		public TerminalNode BITAND_AGG() { return getToken(SQLSelectParserParser.BITAND_AGG, 0); }
		public TerminalNode BITOR_AGG() { return getToken(SQLSelectParserParser.BITOR_AGG, 0); }
		public TerminalNode BITXOR_AGG() { return getToken(SQLSelectParserParser.BITXOR_AGG, 0); }
		public TerminalNode HASH_AGG() { return getToken(SQLSelectParserParser.HASH_AGG, 0); }
		public TerminalNode ARRAY_AGG() { return getToken(SQLSelectParserParser.ARRAY_AGG, 0); }
		public TerminalNode OBJECT_AGG() { return getToken(SQLSelectParserParser.OBJECT_AGG, 0); }
		public TerminalNode REGR_AVGX() { return getToken(SQLSelectParserParser.REGR_AVGX, 0); }
		public TerminalNode REGR_AVGY() { return getToken(SQLSelectParserParser.REGR_AVGY, 0); }
		public TerminalNode REGR_COUNT() { return getToken(SQLSelectParserParser.REGR_COUNT, 0); }
		public TerminalNode REGR_INTERCEPT() { return getToken(SQLSelectParserParser.REGR_INTERCEPT, 0); }
		public TerminalNode REGR_R2() { return getToken(SQLSelectParserParser.REGR_R2, 0); }
		public TerminalNode REGR_SLOPE() { return getToken(SQLSelectParserParser.REGR_SLOPE, 0); }
		public TerminalNode REGR_SXX() { return getToken(SQLSelectParserParser.REGR_SXX, 0); }
		public TerminalNode REGR_SXY() { return getToken(SQLSelectParserParser.REGR_SXY, 0); }
		public TerminalNode REGR_SYY() { return getToken(SQLSelectParserParser.REGR_SYY, 0); }
		public TerminalNode APPROX_COUNT_DISTINCT() { return getToken(SQLSelectParserParser.APPROX_COUNT_DISTINCT, 0); }
		public TerminalNode HLL() { return getToken(SQLSelectParserParser.HLL, 0); }
		public TerminalNode HLL_ACCUMULATE() { return getToken(SQLSelectParserParser.HLL_ACCUMULATE, 0); }
		public TerminalNode HLL_COMBINE() { return getToken(SQLSelectParserParser.HLL_COMBINE, 0); }
		public TerminalNode HLL_EXPORT() { return getToken(SQLSelectParserParser.HLL_EXPORT, 0); }
		public TerminalNode HLL_IMPORT() { return getToken(SQLSelectParserParser.HLL_IMPORT, 0); }
		public TerminalNode APPROXIMATE_JACCARD_INDEX() { return getToken(SQLSelectParserParser.APPROXIMATE_JACCARD_INDEX, 0); }
		public TerminalNode APPROXIMATE_SIMILARITY() { return getToken(SQLSelectParserParser.APPROXIMATE_SIMILARITY, 0); }
		public TerminalNode MINHASH() { return getToken(SQLSelectParserParser.MINHASH, 0); }
		public TerminalNode MINHASH_COMBINE() { return getToken(SQLSelectParserParser.MINHASH_COMBINE, 0); }
		public TerminalNode APPROX_TOP_K() { return getToken(SQLSelectParserParser.APPROX_TOP_K, 0); }
		public TerminalNode APPROX_TOP_K_ACCUMULATE() { return getToken(SQLSelectParserParser.APPROX_TOP_K_ACCUMULATE, 0); }
		public TerminalNode APPROX_TOP_K_COMBINE() { return getToken(SQLSelectParserParser.APPROX_TOP_K_COMBINE, 0); }
		public TerminalNode APPROX_PERCENTILE() { return getToken(SQLSelectParserParser.APPROX_PERCENTILE, 0); }
		public TerminalNode APPROX_PERCENTILE_ACCUMULATE() { return getToken(SQLSelectParserParser.APPROX_PERCENTILE_ACCUMULATE, 0); }
		public TerminalNode APPROX_PERCENTILE_COMBINE() { return getToken(SQLSelectParserParser.APPROX_PERCENTILE_COMBINE, 0); }
		public Set_function_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_set_function_type; }
	}

	public final Set_function_typeContext set_function_type() throws RecognitionException {
		Set_function_typeContext _localctx = new Set_function_typeContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_set_function_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(806);
			_la = _input.LA(1);
			if ( !(((((_la - 61)) & ~0x3f) == 0 && ((1L << (_la - 61)) & 19179056268313345L) != 0) || ((((_la - 126)) & ~0x3f) == 0 && ((1L << (_la - 126)) & -2046809071L) != 0) || ((((_la - 190)) & ~0x3f) == 0 && ((1L << (_la - 190)) & 32767L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Set_qualifier_typeContext extends ParserRuleContext {
		public TerminalNode EVERY() { return getToken(SQLSelectParserParser.EVERY, 0); }
		public TerminalNode ANY() { return getToken(SQLSelectParserParser.ANY, 0); }
		public TerminalNode SOME() { return getToken(SQLSelectParserParser.SOME, 0); }
		public TerminalNode COLLECT() { return getToken(SQLSelectParserParser.COLLECT, 0); }
		public TerminalNode FUSION() { return getToken(SQLSelectParserParser.FUSION, 0); }
		public TerminalNode INTERSECTION() { return getToken(SQLSelectParserParser.INTERSECTION, 0); }
		public Set_qualifier_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_set_qualifier_type; }
	}

	public final Set_qualifier_typeContext set_qualifier_type() throws RecognitionException {
		Set_qualifier_typeContext _localctx = new Set_qualifier_typeContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_set_qualifier_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(808);
			_la = _input.LA(1);
			if ( !(_la==ANY || _la==SOME || ((((_la - 66)) & ~0x3f) == 0 && ((1L << (_la - 66)) & 1090551809L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Case_expressionContext extends ParserRuleContext {
		public TerminalNode CASE() { return getToken(SQLSelectParserParser.CASE, 0); }
		public Value_expressionContext value_expression() {
			return getRuleContext(Value_expressionContext.class,0);
		}
		public When_value_listContext when_value_list() {
			return getRuleContext(When_value_listContext.class,0);
		}
		public TerminalNode END() { return getToken(SQLSelectParserParser.END, 0); }
		public Else_clauseContext else_clause() {
			return getRuleContext(Else_clauseContext.class,0);
		}
		public When_clause_listContext when_clause_list() {
			return getRuleContext(When_clause_listContext.class,0);
		}
		public Case_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_case_expression; }
	}

	public final Case_expressionContext case_expression() throws RecognitionException {
		Case_expressionContext _localctx = new Case_expressionContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_case_expression);
		int _la;
		try {
			setState(825);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,66,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(810);
				match(CASE);
				setState(811);
				value_expression();
				setState(812);
				when_value_list();
				setState(814);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSE) {
					{
					setState(813);
					else_clause();
					}
				}

				setState(816);
				match(END);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(818);
				match(CASE);
				setState(819);
				when_clause_list();
				setState(821);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSE) {
					{
					setState(820);
					else_clause();
					}
				}

				setState(823);
				match(END);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class When_clause_listContext extends ParserRuleContext {
		public List<Searched_when_clauseContext> searched_when_clause() {
			return getRuleContexts(Searched_when_clauseContext.class);
		}
		public Searched_when_clauseContext searched_when_clause(int i) {
			return getRuleContext(Searched_when_clauseContext.class,i);
		}
		public When_clause_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_when_clause_list; }
	}

	public final When_clause_listContext when_clause_list() throws RecognitionException {
		When_clause_listContext _localctx = new When_clause_listContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_when_clause_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(828); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(827);
				searched_when_clause();
				}
				}
				setState(830); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==WHEN );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Searched_when_clauseContext extends ParserRuleContext {
		public Search_conditionContext c;
		public Case_resultContext r;
		public TerminalNode WHEN() { return getToken(SQLSelectParserParser.WHEN, 0); }
		public TerminalNode THEN() { return getToken(SQLSelectParserParser.THEN, 0); }
		public Search_conditionContext search_condition() {
			return getRuleContext(Search_conditionContext.class,0);
		}
		public Case_resultContext case_result() {
			return getRuleContext(Case_resultContext.class,0);
		}
		public Searched_when_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_searched_when_clause; }
	}

	public final Searched_when_clauseContext searched_when_clause() throws RecognitionException {
		Searched_when_clauseContext _localctx = new Searched_when_clauseContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_searched_when_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(832);
			match(WHEN);
			setState(833);
			((Searched_when_clauseContext)_localctx).c = search_condition();
			setState(834);
			match(THEN);
			setState(835);
			((Searched_when_clauseContext)_localctx).r = case_result();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class When_value_listContext extends ParserRuleContext {
		public List<When_value_clauseContext> when_value_clause() {
			return getRuleContexts(When_value_clauseContext.class);
		}
		public When_value_clauseContext when_value_clause(int i) {
			return getRuleContext(When_value_clauseContext.class,i);
		}
		public When_value_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_when_value_list; }
	}

	public final When_value_listContext when_value_list() throws RecognitionException {
		When_value_listContext _localctx = new When_value_listContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_when_value_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(838); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(837);
				when_value_clause();
				}
				}
				setState(840); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==WHEN );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class When_value_clauseContext extends ParserRuleContext {
		public Value_expressionContext c;
		public Case_resultContext r;
		public TerminalNode WHEN() { return getToken(SQLSelectParserParser.WHEN, 0); }
		public TerminalNode THEN() { return getToken(SQLSelectParserParser.THEN, 0); }
		public Value_expressionContext value_expression() {
			return getRuleContext(Value_expressionContext.class,0);
		}
		public Case_resultContext case_result() {
			return getRuleContext(Case_resultContext.class,0);
		}
		public When_value_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_when_value_clause; }
	}

	public final When_value_clauseContext when_value_clause() throws RecognitionException {
		When_value_clauseContext _localctx = new When_value_clauseContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_when_value_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(842);
			match(WHEN);
			setState(843);
			((When_value_clauseContext)_localctx).c = value_expression();
			setState(844);
			match(THEN);
			setState(845);
			((When_value_clauseContext)_localctx).r = case_result();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Else_clauseContext extends ParserRuleContext {
		public Case_resultContext r;
		public TerminalNode ELSE() { return getToken(SQLSelectParserParser.ELSE, 0); }
		public Case_resultContext case_result() {
			return getRuleContext(Case_resultContext.class,0);
		}
		public Else_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_else_clause; }
	}

	public final Else_clauseContext else_clause() throws RecognitionException {
		Else_clauseContext _localctx = new Else_clauseContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_else_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(847);
			match(ELSE);
			setState(848);
			((Else_clauseContext)_localctx).r = case_result();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Case_resultContext extends ParserRuleContext {
		public Value_expressionContext value_expression() {
			return getRuleContext(Value_expressionContext.class,0);
		}
		public Null_literalContext null_literal() {
			return getRuleContext(Null_literalContext.class,0);
		}
		public Case_resultContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_case_result; }
	}

	public final Case_resultContext case_result() throws RecognitionException {
		Case_resultContext _localctx = new Case_resultContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_case_result);
		try {
			setState(852);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,69,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(850);
				value_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(851);
				null_literal();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Null_literalContext extends ParserRuleContext {
		public TerminalNode NULL() { return getToken(SQLSelectParserParser.NULL, 0); }
		public Null_literalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_null_literal; }
	}

	public final Null_literalContext null_literal() throws RecognitionException {
		Null_literalContext _localctx = new Null_literalContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_null_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(854);
			match(NULL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Cast_function_expressionContext extends ParserRuleContext {
		public Cast_function_nameContext cast_function_name() {
			return getRuleContext(Cast_function_nameContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public Value_expressionContext value_expression() {
			return getRuleContext(Value_expressionContext.class,0);
		}
		public TerminalNode AS() { return getToken(SQLSelectParserParser.AS, 0); }
		public Data_typeContext data_type() {
			return getRuleContext(Data_typeContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Cast_function_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cast_function_expression; }
	}

	public final Cast_function_expressionContext cast_function_expression() throws RecognitionException {
		Cast_function_expressionContext _localctx = new Cast_function_expressionContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_cast_function_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(856);
			cast_function_name();
			setState(857);
			match(LEFT_PAREN);
			setState(858);
			value_expression();
			setState(859);
			match(AS);
			setState(860);
			data_type();
			setState(861);
			match(RIGHT_PAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Cast_function_nameContext extends ParserRuleContext {
		public TerminalNode CAST() { return getToken(SQLSelectParserParser.CAST, 0); }
		public TerminalNode TRYCAST() { return getToken(SQLSelectParserParser.TRYCAST, 0); }
		public Cast_function_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cast_function_name; }
	}

	public final Cast_function_nameContext cast_function_name() throws RecognitionException {
		Cast_function_nameContext _localctx = new Cast_function_nameContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_cast_function_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(863);
			_la = _input.LA(1);
			if ( !(_la==CAST || _la==TRYCAST) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Window_over_partition_expressionContext extends ParserRuleContext {
		public Window_functionContext window_function() {
			return getRuleContext(Window_functionContext.class,0);
		}
		public Over_clauseContext over_clause() {
			return getRuleContext(Over_clauseContext.class,0);
		}
		public Window_over_partition_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_window_over_partition_expression; }
	}

	public final Window_over_partition_expressionContext window_over_partition_expression() throws RecognitionException {
		Window_over_partition_expressionContext _localctx = new Window_over_partition_expressionContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_window_over_partition_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(865);
			window_function();
			setState(866);
			over_clause();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Window_functionContext extends ParserRuleContext {
		public Set_function_typeContext set_function_type() {
			return getRuleContext(Set_function_typeContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Sql_argument_listContext sql_argument_list() {
			return getRuleContext(Sql_argument_listContext.class,0);
		}
		public Item_select_functionContext item_select_function() {
			return getRuleContext(Item_select_functionContext.class,0);
		}
		public Null_handlingContext null_handling() {
			return getRuleContext(Null_handlingContext.class,0);
		}
		public Select_directionContext select_direction() {
			return getRuleContext(Select_directionContext.class,0);
		}
		public Window_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_window_function; }
	}

	public final Window_functionContext window_function() throws RecognitionException {
		Window_functionContext _localctx = new Window_functionContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_window_function);
		int _la;
		try {
			setState(885);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,73,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(868);
				set_function_type();
				setState(869);
				match(LEFT_PAREN);
				setState(871);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -848827245298417264L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -144115188629504257L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -1048585L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -81066992315990017L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 2236872663039L) != 0) || ((((_la - 323)) & ~0x3f) == 0 && ((1L << (_la - 323)) & 2463L) != 0)) {
					{
					setState(870);
					sql_argument_list();
					}
				}

				setState(873);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(875);
				item_select_function();
				setState(876);
				match(LEFT_PAREN);
				setState(877);
				sql_argument_list();
				setState(878);
				match(RIGHT_PAREN);
				setState(883);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 4398047690752L) != 0)) {
					{
					setState(880);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==FROM) {
						{
						setState(879);
						select_direction();
						}
					}

					setState(882);
					null_handling();
					}
				}

				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Over_clauseContext extends ParserRuleContext {
		public TerminalNode OVER() { return getToken(SQLSelectParserParser.OVER, 0); }
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Partition_by_clauseContext partition_by_clause() {
			return getRuleContext(Partition_by_clauseContext.class,0);
		}
		public Orderby_clauseContext orderby_clause() {
			return getRuleContext(Orderby_clauseContext.class,0);
		}
		public Bracket_frame_clauseContext bracket_frame_clause() {
			return getRuleContext(Bracket_frame_clauseContext.class,0);
		}
		public Over_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_over_clause; }
	}

	public final Over_clauseContext over_clause() throws RecognitionException {
		Over_clauseContext _localctx = new Over_clauseContext(_ctx, getState());
		enterRule(_localctx, 144, RULE_over_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(887);
			match(OVER);
			setState(888);
			match(LEFT_PAREN);
			{
			setState(890);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PARTITION) {
				{
				setState(889);
				partition_by_clause();
				}
			}

			setState(893);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ORDER) {
				{
				setState(892);
				orderby_clause();
				}
			}

			setState(896);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==RANGE || _la==ROWS) {
				{
				setState(895);
				bracket_frame_clause();
				}
			}

			}
			setState(898);
			match(RIGHT_PAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Partition_by_clauseContext extends ParserRuleContext {
		public TerminalNode PARTITION() { return getToken(SQLSelectParserParser.PARTITION, 0); }
		public TerminalNode BY() { return getToken(SQLSelectParserParser.BY, 0); }
		public Sql_argument_listContext sql_argument_list() {
			return getRuleContext(Sql_argument_listContext.class,0);
		}
		public Partition_by_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_partition_by_clause; }
	}

	public final Partition_by_clauseContext partition_by_clause() throws RecognitionException {
		Partition_by_clauseContext _localctx = new Partition_by_clauseContext(_ctx, getState());
		enterRule(_localctx, 146, RULE_partition_by_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(900);
			match(PARTITION);
			setState(901);
			match(BY);
			setState(902);
			sql_argument_list();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Bracket_frame_clauseContext extends ParserRuleContext {
		public Rows_or_rangeContext rows_or_range() {
			return getRuleContext(Rows_or_rangeContext.class,0);
		}
		public Bracket_frame_definitionContext bracket_frame_definition() {
			return getRuleContext(Bracket_frame_definitionContext.class,0);
		}
		public Bracket_frame_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bracket_frame_clause; }
	}

	public final Bracket_frame_clauseContext bracket_frame_clause() throws RecognitionException {
		Bracket_frame_clauseContext _localctx = new Bracket_frame_clauseContext(_ctx, getState());
		enterRule(_localctx, 148, RULE_bracket_frame_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(904);
			rows_or_range();
			setState(905);
			bracket_frame_definition();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Rows_or_rangeContext extends ParserRuleContext {
		public TerminalNode ROWS() { return getToken(SQLSelectParserParser.ROWS, 0); }
		public TerminalNode RANGE() { return getToken(SQLSelectParserParser.RANGE, 0); }
		public Rows_or_rangeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rows_or_range; }
	}

	public final Rows_or_rangeContext rows_or_range() throws RecognitionException {
		Rows_or_rangeContext _localctx = new Rows_or_rangeContext(_ctx, getState());
		enterRule(_localctx, 150, RULE_rows_or_range);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(907);
			_la = _input.LA(1);
			if ( !(_la==RANGE || _la==ROWS) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Bracket_frame_definitionContext extends ParserRuleContext {
		public Between_frame_definitionContext between_frame_definition() {
			return getRuleContext(Between_frame_definitionContext.class,0);
		}
		public Preceding_frame_edgeContext preceding_frame_edge() {
			return getRuleContext(Preceding_frame_edgeContext.class,0);
		}
		public Current_row_edgeContext current_row_edge() {
			return getRuleContext(Current_row_edgeContext.class,0);
		}
		public Bracket_frame_definitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bracket_frame_definition; }
	}

	public final Bracket_frame_definitionContext bracket_frame_definition() throws RecognitionException {
		Bracket_frame_definitionContext _localctx = new Bracket_frame_definitionContext(_ctx, getState());
		enterRule(_localctx, 152, RULE_bracket_frame_definition);
		try {
			setState(912);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BETWEEN:
				enterOuterAlt(_localctx, 1);
				{
				setState(909);
				between_frame_definition();
				}
				break;
			case UNBOUNDED:
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(910);
				preceding_frame_edge();
				}
				break;
			case CURRENT:
				enterOuterAlt(_localctx, 3);
				{
				setState(911);
				current_row_edge();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Between_frame_definitionContext extends ParserRuleContext {
		public TerminalNode BETWEEN() { return getToken(SQLSelectParserParser.BETWEEN, 0); }
		public List<Frame_edgeContext> frame_edge() {
			return getRuleContexts(Frame_edgeContext.class);
		}
		public Frame_edgeContext frame_edge(int i) {
			return getRuleContext(Frame_edgeContext.class,i);
		}
		public TerminalNode AND() { return getToken(SQLSelectParserParser.AND, 0); }
		public Between_frame_definitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_between_frame_definition; }
	}

	public final Between_frame_definitionContext between_frame_definition() throws RecognitionException {
		Between_frame_definitionContext _localctx = new Between_frame_definitionContext(_ctx, getState());
		enterRule(_localctx, 154, RULE_between_frame_definition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(914);
			match(BETWEEN);
			setState(915);
			frame_edge();
			setState(916);
			match(AND);
			setState(917);
			frame_edge();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Frame_edgeContext extends ParserRuleContext {
		public Preceding_frame_edgeContext preceding_frame_edge() {
			return getRuleContext(Preceding_frame_edgeContext.class,0);
		}
		public Following_frame_edgeContext following_frame_edge() {
			return getRuleContext(Following_frame_edgeContext.class,0);
		}
		public Current_row_edgeContext current_row_edge() {
			return getRuleContext(Current_row_edgeContext.class,0);
		}
		public Frame_edgeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_frame_edge; }
	}

	public final Frame_edgeContext frame_edge() throws RecognitionException {
		Frame_edgeContext _localctx = new Frame_edgeContext(_ctx, getState());
		enterRule(_localctx, 156, RULE_frame_edge);
		try {
			setState(922);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,78,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(919);
				preceding_frame_edge();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(920);
				following_frame_edge();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(921);
				current_row_edge();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Preceding_frame_edgeContext extends ParserRuleContext {
		public Bracket_constraintContext bracket_constraint() {
			return getRuleContext(Bracket_constraintContext.class,0);
		}
		public TerminalNode PRECEDING() { return getToken(SQLSelectParserParser.PRECEDING, 0); }
		public Preceding_frame_edgeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_preceding_frame_edge; }
	}

	public final Preceding_frame_edgeContext preceding_frame_edge() throws RecognitionException {
		Preceding_frame_edgeContext _localctx = new Preceding_frame_edgeContext(_ctx, getState());
		enterRule(_localctx, 158, RULE_preceding_frame_edge);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(924);
			bracket_constraint();
			setState(925);
			match(PRECEDING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Following_frame_edgeContext extends ParserRuleContext {
		public Bracket_constraintContext bracket_constraint() {
			return getRuleContext(Bracket_constraintContext.class,0);
		}
		public TerminalNode FOLLOWING() { return getToken(SQLSelectParserParser.FOLLOWING, 0); }
		public Following_frame_edgeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_following_frame_edge; }
	}

	public final Following_frame_edgeContext following_frame_edge() throws RecognitionException {
		Following_frame_edgeContext _localctx = new Following_frame_edgeContext(_ctx, getState());
		enterRule(_localctx, 160, RULE_following_frame_edge);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(927);
			bracket_constraint();
			setState(928);
			match(FOLLOWING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Current_row_edgeContext extends ParserRuleContext {
		public TerminalNode CURRENT() { return getToken(SQLSelectParserParser.CURRENT, 0); }
		public TerminalNode ROW() { return getToken(SQLSelectParserParser.ROW, 0); }
		public Current_row_edgeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_current_row_edge; }
	}

	public final Current_row_edgeContext current_row_edge() throws RecognitionException {
		Current_row_edgeContext _localctx = new Current_row_edgeContext(_ctx, getState());
		enterRule(_localctx, 162, RULE_current_row_edge);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(930);
			match(CURRENT);
			setState(931);
			match(ROW);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Bracket_constraintContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(SQLSelectParserParser.NUMBER, 0); }
		public TerminalNode UNBOUNDED() { return getToken(SQLSelectParserParser.UNBOUNDED, 0); }
		public Bracket_constraintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bracket_constraint; }
	}

	public final Bracket_constraintContext bracket_constraint() throws RecognitionException {
		Bracket_constraintContext _localctx = new Bracket_constraintContext(_ctx, getState());
		enterRule(_localctx, 164, RULE_bracket_constraint);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(933);
			_la = _input.LA(1);
			if ( !(_la==UNBOUNDED || _la==NUMBER) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Item_select_functionContext extends ParserRuleContext {
		public TerminalNode FIRST_VALUE() { return getToken(SQLSelectParserParser.FIRST_VALUE, 0); }
		public TerminalNode LAST_VALUE() { return getToken(SQLSelectParserParser.LAST_VALUE, 0); }
		public TerminalNode NTH_VALUE() { return getToken(SQLSelectParserParser.NTH_VALUE, 0); }
		public TerminalNode LAG() { return getToken(SQLSelectParserParser.LAG, 0); }
		public TerminalNode LEAD() { return getToken(SQLSelectParserParser.LEAD, 0); }
		public Item_select_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_item_select_function; }
	}

	public final Item_select_functionContext item_select_function() throws RecognitionException {
		Item_select_functionContext _localctx = new Item_select_functionContext(_ctx, getState());
		enterRule(_localctx, 166, RULE_item_select_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(935);
			_la = _input.LA(1);
			if ( !(((((_la - 87)) & ~0x3f) == 0 && ((1L << (_la - 87)) & 268488705L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Select_directionContext extends ParserRuleContext {
		public TerminalNode FROM() { return getToken(SQLSelectParserParser.FROM, 0); }
		public TerminalNode FIRST() { return getToken(SQLSelectParserParser.FIRST, 0); }
		public TerminalNode LAST() { return getToken(SQLSelectParserParser.LAST, 0); }
		public Select_directionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_direction; }
	}

	public final Select_directionContext select_direction() throws RecognitionException {
		Select_directionContext _localctx = new Select_directionContext(_ctx, getState());
		enterRule(_localctx, 168, RULE_select_direction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(937);
			match(FROM);
			setState(938);
			_la = _input.LA(1);
			if ( !(_la==FIRST || _la==LAST) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Null_handlingContext extends ParserRuleContext {
		public TerminalNode NULLS() { return getToken(SQLSelectParserParser.NULLS, 0); }
		public TerminalNode IGNORE() { return getToken(SQLSelectParserParser.IGNORE, 0); }
		public TerminalNode RESPECT() { return getToken(SQLSelectParserParser.RESPECT, 0); }
		public Null_handlingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_null_handling; }
	}

	public final Null_handlingContext null_handling() throws RecognitionException {
		Null_handlingContext _localctx = new Null_handlingContext(_ctx, getState());
		enterRule(_localctx, 170, RULE_null_handling);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(940);
			_la = _input.LA(1);
			if ( !(_la==IGNORE || _la==RESPECT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(941);
			match(NULLS);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Value_expressionContext extends ParserRuleContext {
		public Common_value_expressionContext common_value_expression() {
			return getRuleContext(Common_value_expressionContext.class,0);
		}
		public Row_value_expressionContext row_value_expression() {
			return getRuleContext(Row_value_expressionContext.class,0);
		}
		public Variable_identifierContext variable_identifier() {
			return getRuleContext(Variable_identifierContext.class,0);
		}
		public Boolean_value_expressionContext boolean_value_expression() {
			return getRuleContext(Boolean_value_expressionContext.class,0);
		}
		public Value_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value_expression; }
	}

	public final Value_expressionContext value_expression() throws RecognitionException {
		Value_expressionContext _localctx = new Value_expressionContext(_ctx, getState());
		enterRule(_localctx, 172, RULE_value_expression);
		try {
			setState(947);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,79,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(943);
				common_value_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(944);
				row_value_expression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(945);
				variable_identifier();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(946);
				boolean_value_expression();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Common_value_expressionContext extends ParserRuleContext {
		public Additive_expressionContext additive_expression() {
			return getRuleContext(Additive_expressionContext.class,0);
		}
		public String_value_expressionContext string_value_expression() {
			return getRuleContext(String_value_expressionContext.class,0);
		}
		public Null_literalContext null_literal() {
			return getRuleContext(Null_literalContext.class,0);
		}
		public Common_value_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_common_value_expression; }
	}

	public final Common_value_expressionContext common_value_expression() throws RecognitionException {
		Common_value_expressionContext _localctx = new Common_value_expressionContext(_ctx, getState());
		enterRule(_localctx, 174, RULE_common_value_expression);
		try {
			setState(952);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,80,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(949);
				additive_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(950);
				string_value_expression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(951);
				null_literal();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Additive_expressionContext extends ParserRuleContext {
		public Multiplicative_expressionContext left;
		public Multiplicative_expressionContext right;
		public List<Multiplicative_expressionContext> multiplicative_expression() {
			return getRuleContexts(Multiplicative_expressionContext.class);
		}
		public Multiplicative_expressionContext multiplicative_expression(int i) {
			return getRuleContext(Multiplicative_expressionContext.class,i);
		}
		public List<TerminalNode> PLUS() { return getTokens(SQLSelectParserParser.PLUS); }
		public TerminalNode PLUS(int i) {
			return getToken(SQLSelectParserParser.PLUS, i);
		}
		public List<TerminalNode> MINUS() { return getTokens(SQLSelectParserParser.MINUS); }
		public TerminalNode MINUS(int i) {
			return getToken(SQLSelectParserParser.MINUS, i);
		}
		public Additive_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_additive_expression; }
	}

	public final Additive_expressionContext additive_expression() throws RecognitionException {
		Additive_expressionContext _localctx = new Additive_expressionContext(_ctx, getState());
		enterRule(_localctx, 176, RULE_additive_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(954);
			((Additive_expressionContext)_localctx).left = multiplicative_expression();
			setState(959);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PLUS || _la==MINUS) {
				{
				{
				setState(955);
				_la = _input.LA(1);
				if ( !(_la==PLUS || _la==MINUS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(956);
				((Additive_expressionContext)_localctx).right = multiplicative_expression();
				}
				}
				setState(961);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Multiplicative_expressionContext extends ParserRuleContext {
		public FactorContext left;
		public FactorContext right;
		public List<FactorContext> factor() {
			return getRuleContexts(FactorContext.class);
		}
		public FactorContext factor(int i) {
			return getRuleContext(FactorContext.class,i);
		}
		public List<TerminalNode> MULTIPLY() { return getTokens(SQLSelectParserParser.MULTIPLY); }
		public TerminalNode MULTIPLY(int i) {
			return getToken(SQLSelectParserParser.MULTIPLY, i);
		}
		public List<TerminalNode> DIVIDE() { return getTokens(SQLSelectParserParser.DIVIDE); }
		public TerminalNode DIVIDE(int i) {
			return getToken(SQLSelectParserParser.DIVIDE, i);
		}
		public List<TerminalNode> MODULAR() { return getTokens(SQLSelectParserParser.MODULAR); }
		public TerminalNode MODULAR(int i) {
			return getToken(SQLSelectParserParser.MODULAR, i);
		}
		public Multiplicative_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiplicative_expression; }
	}

	public final Multiplicative_expressionContext multiplicative_expression() throws RecognitionException {
		Multiplicative_expressionContext _localctx = new Multiplicative_expressionContext(_ctx, getState());
		enterRule(_localctx, 178, RULE_multiplicative_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(962);
			((Multiplicative_expressionContext)_localctx).left = factor();
			setState(967);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 288)) & ~0x3f) == 0 && ((1L << (_la - 288)) & 7L) != 0)) {
				{
				{
				setState(963);
				_la = _input.LA(1);
				if ( !(((((_la - 288)) & ~0x3f) == 0 && ((1L << (_la - 288)) & 7L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(964);
				((Multiplicative_expressionContext)_localctx).right = factor();
				}
				}
				setState(969);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FactorContext extends ParserRuleContext {
		public Numeric_primaryContext numeric_primary() {
			return getRuleContext(Numeric_primaryContext.class,0);
		}
		public SignContext sign() {
			return getRuleContext(SignContext.class,0);
		}
		public FactorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_factor; }
	}

	public final FactorContext factor() throws RecognitionException {
		FactorContext _localctx = new FactorContext(_ctx, getState());
		enterRule(_localctx, 180, RULE_factor);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(971);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PLUS || _la==MINUS) {
				{
				setState(970);
				sign();
				}
			}

			setState(973);
			numeric_primary();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Numeric_primaryContext extends ParserRuleContext {
		public Value_expression_primaryContext value_expression_primary() {
			return getRuleContext(Value_expression_primaryContext.class,0);
		}
		public Extract_expressionContext extract_expression() {
			return getRuleContext(Extract_expressionContext.class,0);
		}
		public Numeric_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numeric_primary; }
	}

	public final Numeric_primaryContext numeric_primary() throws RecognitionException {
		Numeric_primaryContext _localctx = new Numeric_primaryContext(_ctx, getState());
		enterRule(_localctx, 182, RULE_numeric_primary);
		try {
			setState(977);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,84,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(975);
				value_expression_primary();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(976);
				extract_expression();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SignContext extends ParserRuleContext {
		public TerminalNode PLUS() { return getToken(SQLSelectParserParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(SQLSelectParserParser.MINUS, 0); }
		public SignContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sign; }
	}

	public final SignContext sign() throws RecognitionException {
		SignContext _localctx = new SignContext(_ctx, getState());
		enterRule(_localctx, 184, RULE_sign);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(979);
			_la = _input.LA(1);
			if ( !(_la==PLUS || _la==MINUS) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Extract_expressionContext extends ParserRuleContext {
		public Extract_fieldContext extract_field_string;
		public TerminalNode EXTRACT() { return getToken(SQLSelectParserParser.EXTRACT, 0); }
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public TerminalNode FROM() { return getToken(SQLSelectParserParser.FROM, 0); }
		public Extract_sourceContext extract_source() {
			return getRuleContext(Extract_sourceContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Extract_fieldContext extract_field() {
			return getRuleContext(Extract_fieldContext.class,0);
		}
		public Extract_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extract_expression; }
	}

	public final Extract_expressionContext extract_expression() throws RecognitionException {
		Extract_expressionContext _localctx = new Extract_expressionContext(_ctx, getState());
		enterRule(_localctx, 186, RULE_extract_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(981);
			match(EXTRACT);
			setState(982);
			match(LEFT_PAREN);
			setState(983);
			((Extract_expressionContext)_localctx).extract_field_string = extract_field();
			setState(984);
			match(FROM);
			setState(985);
			extract_source();
			setState(986);
			match(RIGHT_PAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Extract_fieldContext extends ParserRuleContext {
		public Primary_datetime_fieldContext primary_datetime_field() {
			return getRuleContext(Primary_datetime_fieldContext.class,0);
		}
		public Time_zone_fieldContext time_zone_field() {
			return getRuleContext(Time_zone_fieldContext.class,0);
		}
		public Extended_datetime_fieldContext extended_datetime_field() {
			return getRuleContext(Extended_datetime_fieldContext.class,0);
		}
		public Extract_fieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extract_field; }
	}

	public final Extract_fieldContext extract_field() throws RecognitionException {
		Extract_fieldContext _localctx = new Extract_fieldContext(_ctx, getState());
		enterRule(_localctx, 188, RULE_extract_field);
		try {
			setState(991);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DAY:
			case HOUR:
			case MINUTE:
			case MONTH:
			case SECOND:
			case YEAR:
				enterOuterAlt(_localctx, 1);
				{
				setState(988);
				primary_datetime_field();
				}
				break;
			case TIMEZONE:
			case TIMEZONE_HOUR:
			case TIMEZONE_MINUTE:
				enterOuterAlt(_localctx, 2);
				{
				setState(989);
				time_zone_field();
				}
				break;
			case CENTURY:
			case DECADE:
			case DOW:
			case DOY:
			case EPOCH:
			case ISODOW:
			case ISOYEAR:
			case MICROSECONDS:
			case MILLENNIUM:
			case MILLISECONDS:
			case QUARTER:
			case WEEK:
				enterOuterAlt(_localctx, 3);
				{
				setState(990);
				extended_datetime_field();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Time_zone_fieldContext extends ParserRuleContext {
		public TerminalNode TIMEZONE() { return getToken(SQLSelectParserParser.TIMEZONE, 0); }
		public TerminalNode TIMEZONE_HOUR() { return getToken(SQLSelectParserParser.TIMEZONE_HOUR, 0); }
		public TerminalNode TIMEZONE_MINUTE() { return getToken(SQLSelectParserParser.TIMEZONE_MINUTE, 0); }
		public Time_zone_fieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_time_zone_field; }
	}

	public final Time_zone_fieldContext time_zone_field() throws RecognitionException {
		Time_zone_fieldContext _localctx = new Time_zone_fieldContext(_ctx, getState());
		enterRule(_localctx, 190, RULE_time_zone_field);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(993);
			_la = _input.LA(1);
			if ( !(((((_la - 142)) & ~0x3f) == 0 && ((1L << (_la - 142)) & 7L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Extract_sourceContext extends ParserRuleContext {
		public Column_referenceContext column_reference() {
			return getRuleContext(Column_referenceContext.class,0);
		}
		public Datetime_literalContext datetime_literal() {
			return getRuleContext(Datetime_literalContext.class,0);
		}
		public Extract_sourceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extract_source; }
	}

	public final Extract_sourceContext extract_source() throws RecognitionException {
		Extract_sourceContext _localctx = new Extract_sourceContext(_ctx, getState());
		enterRule(_localctx, 192, RULE_extract_source);
		try {
			setState(997);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,86,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(995);
				column_reference();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(996);
				datetime_literal();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class String_value_expressionContext extends ParserRuleContext {
		public List<Character_primaryContext> character_primary() {
			return getRuleContexts(Character_primaryContext.class);
		}
		public Character_primaryContext character_primary(int i) {
			return getRuleContext(Character_primaryContext.class,i);
		}
		public List<TerminalNode> CONCATENATION_OPERATOR() { return getTokens(SQLSelectParserParser.CONCATENATION_OPERATOR); }
		public TerminalNode CONCATENATION_OPERATOR(int i) {
			return getToken(SQLSelectParserParser.CONCATENATION_OPERATOR, i);
		}
		public String_value_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_string_value_expression; }
	}

	public final String_value_expressionContext string_value_expression() throws RecognitionException {
		String_value_expressionContext _localctx = new String_value_expressionContext(_ctx, getState());
		enterRule(_localctx, 194, RULE_string_value_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(999);
			character_primary();
			setState(1004);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CONCATENATION_OPERATOR) {
				{
				{
				setState(1000);
				match(CONCATENATION_OPERATOR);
				setState(1001);
				character_primary();
				}
				}
				setState(1006);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Character_primaryContext extends ParserRuleContext {
		public Value_expression_primaryContext value_expression_primary() {
			return getRuleContext(Value_expression_primaryContext.class,0);
		}
		public Trim_functionContext trim_function() {
			return getRuleContext(Trim_functionContext.class,0);
		}
		public Character_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_character_primary; }
	}

	public final Character_primaryContext character_primary() throws RecognitionException {
		Character_primaryContext _localctx = new Character_primaryContext(_ctx, getState());
		enterRule(_localctx, 196, RULE_character_primary);
		try {
			setState(1009);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,88,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1007);
				value_expression_primary();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1008);
				trim_function();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Trim_functionContext extends ParserRuleContext {
		public Trim_function_nameContext trim_function_name() {
			return getRuleContext(Trim_function_nameContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public Trim_operandsContext trim_operands() {
			return getRuleContext(Trim_operandsContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Trim_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_trim_function; }
	}

	public final Trim_functionContext trim_function() throws RecognitionException {
		Trim_functionContext _localctx = new Trim_functionContext(_ctx, getState());
		enterRule(_localctx, 198, RULE_trim_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1011);
			trim_function_name();
			setState(1012);
			match(LEFT_PAREN);
			setState(1013);
			trim_operands();
			setState(1014);
			match(RIGHT_PAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Trim_function_nameContext extends ParserRuleContext {
		public TerminalNode TRIM() { return getToken(SQLSelectParserParser.TRIM, 0); }
		public Trim_function_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_trim_function_name; }
	}

	public final Trim_function_nameContext trim_function_name() throws RecognitionException {
		Trim_function_nameContext _localctx = new Trim_function_nameContext(_ctx, getState());
		enterRule(_localctx, 200, RULE_trim_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1016);
			match(TRIM);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Trim_operandsContext extends ParserRuleContext {
		public Trim_operandsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_trim_operands; }
	 
		public Trim_operandsContext() { }
		public void copyFrom(Trim_operandsContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Other_trim_operandsContext extends Trim_operandsContext {
		public Value_expressionContext trim_source;
		public String_value_expressionContext trim_character;
		public TerminalNode COMMA() { return getToken(SQLSelectParserParser.COMMA, 0); }
		public Value_expressionContext value_expression() {
			return getRuleContext(Value_expressionContext.class,0);
		}
		public String_value_expressionContext string_value_expression() {
			return getRuleContext(String_value_expressionContext.class,0);
		}
		public Other_trim_operandsContext(Trim_operandsContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Mysql_trim_operandsContext extends Trim_operandsContext {
		public String_value_expressionContext trim_character;
		public Value_expressionContext trim_source;
		public Value_expressionContext value_expression() {
			return getRuleContext(Value_expressionContext.class,0);
		}
		public TerminalNode FROM() { return getToken(SQLSelectParserParser.FROM, 0); }
		public Trim_specificationContext trim_specification() {
			return getRuleContext(Trim_specificationContext.class,0);
		}
		public String_value_expressionContext string_value_expression() {
			return getRuleContext(String_value_expressionContext.class,0);
		}
		public Mysql_trim_operandsContext(Trim_operandsContext ctx) { copyFrom(ctx); }
	}

	public final Trim_operandsContext trim_operands() throws RecognitionException {
		Trim_operandsContext _localctx = new Trim_operandsContext(_ctx, getState());
		enterRule(_localctx, 202, RULE_trim_operands);
		int _la;
		try {
			setState(1032);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,92,_ctx) ) {
			case 1:
				_localctx = new Mysql_trim_operandsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(1025);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,91,_ctx) ) {
				case 1:
					{
					setState(1019);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1125900175278144L) != 0)) {
						{
						setState(1018);
						trim_specification();
						}
					}

					setState(1022);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -848827271068221040L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -144115188629504257L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -1048585L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -81066992315990017L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 2233651437567L) != 0) || ((((_la - 323)) & ~0x3f) == 0 && ((1L << (_la - 323)) & 2449L) != 0)) {
						{
						setState(1021);
						((Mysql_trim_operandsContext)_localctx).trim_character = string_value_expression();
						}
					}

					setState(1024);
					match(FROM);
					}
					break;
				}
				setState(1027);
				((Mysql_trim_operandsContext)_localctx).trim_source = value_expression();
				}
				break;
			case 2:
				_localctx = new Other_trim_operandsContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(1028);
				((Other_trim_operandsContext)_localctx).trim_source = value_expression();
				setState(1029);
				match(COMMA);
				setState(1030);
				((Other_trim_operandsContext)_localctx).trim_character = string_value_expression();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Trim_specificationContext extends ParserRuleContext {
		public TerminalNode LEADING() { return getToken(SQLSelectParserParser.LEADING, 0); }
		public TerminalNode TRAILING() { return getToken(SQLSelectParserParser.TRAILING, 0); }
		public TerminalNode BOTH() { return getToken(SQLSelectParserParser.BOTH, 0); }
		public Trim_specificationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_trim_specification; }
	}

	public final Trim_specificationContext trim_specification() throws RecognitionException {
		Trim_specificationContext _localctx = new Trim_specificationContext(_ctx, getState());
		enterRule(_localctx, 204, RULE_trim_specification);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1034);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 1125900175278144L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Boolean_value_expressionContext extends ParserRuleContext {
		public Or_predicateContext or_predicate() {
			return getRuleContext(Or_predicateContext.class,0);
		}
		public Boolean_value_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolean_value_expression; }
	}

	public final Boolean_value_expressionContext boolean_value_expression() throws RecognitionException {
		Boolean_value_expressionContext _localctx = new Boolean_value_expressionContext(_ctx, getState());
		enterRule(_localctx, 206, RULE_boolean_value_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1036);
			or_predicate();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Or_predicateContext extends ParserRuleContext {
		public List<And_predicateContext> and_predicate() {
			return getRuleContexts(And_predicateContext.class);
		}
		public And_predicateContext and_predicate(int i) {
			return getRuleContext(And_predicateContext.class,i);
		}
		public List<TerminalNode> OR() { return getTokens(SQLSelectParserParser.OR); }
		public TerminalNode OR(int i) {
			return getToken(SQLSelectParserParser.OR, i);
		}
		public Or_predicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_or_predicate; }
	}

	public final Or_predicateContext or_predicate() throws RecognitionException {
		Or_predicateContext _localctx = new Or_predicateContext(_ctx, getState());
		enterRule(_localctx, 208, RULE_or_predicate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1038);
			and_predicate();
			setState(1043);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OR) {
				{
				{
				setState(1039);
				match(OR);
				setState(1040);
				and_predicate();
				}
				}
				setState(1045);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class And_predicateContext extends ParserRuleContext {
		public List<Negative_predicateContext> negative_predicate() {
			return getRuleContexts(Negative_predicateContext.class);
		}
		public Negative_predicateContext negative_predicate(int i) {
			return getRuleContext(Negative_predicateContext.class,i);
		}
		public List<TerminalNode> AND() { return getTokens(SQLSelectParserParser.AND); }
		public TerminalNode AND(int i) {
			return getToken(SQLSelectParserParser.AND, i);
		}
		public And_predicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_and_predicate; }
	}

	public final And_predicateContext and_predicate() throws RecognitionException {
		And_predicateContext _localctx = new And_predicateContext(_ctx, getState());
		enterRule(_localctx, 210, RULE_and_predicate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1046);
			negative_predicate();
			setState(1051);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AND) {
				{
				{
				setState(1047);
				match(AND);
				setState(1048);
				negative_predicate();
				}
				}
				setState(1053);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Negative_predicateContext extends ParserRuleContext {
		public Parenthetical_predicateContext parenthetical_predicate() {
			return getRuleContext(Parenthetical_predicateContext.class,0);
		}
		public NotContext not() {
			return getRuleContext(NotContext.class,0);
		}
		public Negative_predicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_negative_predicate; }
	}

	public final Negative_predicateContext negative_predicate() throws RecognitionException {
		Negative_predicateContext _localctx = new Negative_predicateContext(_ctx, getState());
		enterRule(_localctx, 212, RULE_negative_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1055);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,95,_ctx) ) {
			case 1:
				{
				setState(1054);
				not();
				}
				break;
			}
			setState(1057);
			parenthetical_predicate();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Parenthetical_predicateContext extends ParserRuleContext {
		public Parenthetical_predicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parenthetical_predicate; }
	 
		public Parenthetical_predicateContext() { }
		public void copyFrom(Parenthetical_predicateContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Paren_clauseContext extends Parenthetical_predicateContext {
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public Boolean_value_expressionContext boolean_value_expression() {
			return getRuleContext(Boolean_value_expressionContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Paren_clauseContext(Parenthetical_predicateContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Basic_predicate_clauseContext extends Parenthetical_predicateContext {
		public Boolean_primaryContext boolean_primary() {
			return getRuleContext(Boolean_primaryContext.class,0);
		}
		public Is_clauseContext is_clause() {
			return getRuleContext(Is_clauseContext.class,0);
		}
		public Basic_predicate_clauseContext(Parenthetical_predicateContext ctx) { copyFrom(ctx); }
	}

	public final Parenthetical_predicateContext parenthetical_predicate() throws RecognitionException {
		Parenthetical_predicateContext _localctx = new Parenthetical_predicateContext(_ctx, getState());
		enterRule(_localctx, 214, RULE_parenthetical_predicate);
		int _la;
		try {
			setState(1067);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,97,_ctx) ) {
			case 1:
				_localctx = new Paren_clauseContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(1059);
				match(LEFT_PAREN);
				setState(1060);
				boolean_value_expression();
				setState(1061);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				_localctx = new Basic_predicate_clauseContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(1063);
				boolean_primary();
				setState(1065);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IS) {
					{
					setState(1064);
					is_clause();
					}
				}

				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Boolean_primaryContext extends ParserRuleContext {
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public Nonparenthesized_value_expression_primaryContext nonparenthesized_value_expression_primary() {
			return getRuleContext(Nonparenthesized_value_expression_primaryContext.class,0);
		}
		public Boolean_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolean_primary; }
	}

	public final Boolean_primaryContext boolean_primary() throws RecognitionException {
		Boolean_primaryContext _localctx = new Boolean_primaryContext(_ctx, getState());
		enterRule(_localctx, 216, RULE_boolean_primary);
		try {
			setState(1071);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,98,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1069);
				predicate();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1070);
				nonparenthesized_value_expression_primary();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PredicateContext extends ParserRuleContext {
		public Comparison_predicateContext comparison_predicate() {
			return getRuleContext(Comparison_predicateContext.class,0);
		}
		public Between_predicateContext between_predicate() {
			return getRuleContext(Between_predicateContext.class,0);
		}
		public In_predicateContext in_predicate() {
			return getRuleContext(In_predicateContext.class,0);
		}
		public Like_any_predicateContext like_any_predicate() {
			return getRuleContext(Like_any_predicateContext.class,0);
		}
		public Null_predicateContext null_predicate() {
			return getRuleContext(Null_predicateContext.class,0);
		}
		public Exists_predicateContext exists_predicate() {
			return getRuleContext(Exists_predicateContext.class,0);
		}
		public Substitution_predicateContext substitution_predicate() {
			return getRuleContext(Substitution_predicateContext.class,0);
		}
		public PredicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicate; }
	}

	public final PredicateContext predicate() throws RecognitionException {
		PredicateContext _localctx = new PredicateContext(_ctx, getState());
		enterRule(_localctx, 218, RULE_predicate);
		try {
			setState(1080);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,99,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1073);
				comparison_predicate();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1074);
				between_predicate();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1075);
				in_predicate();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1076);
				like_any_predicate();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1077);
				null_predicate();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1078);
				exists_predicate();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1079);
				substitution_predicate();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Substitution_predicateContext extends ParserRuleContext {
		public Variable_identifierContext variable_identifier() {
			return getRuleContext(Variable_identifierContext.class,0);
		}
		public Substitution_predicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_substitution_predicate; }
	}

	public final Substitution_predicateContext substitution_predicate() throws RecognitionException {
		Substitution_predicateContext _localctx = new Substitution_predicateContext(_ctx, getState());
		enterRule(_localctx, 220, RULE_substitution_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1082);
			variable_identifier();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Row_value_expressionContext extends ParserRuleContext {
		public Nonparenthesized_value_expression_primaryContext nonparenthesized_value_expression_primary() {
			return getRuleContext(Nonparenthesized_value_expression_primaryContext.class,0);
		}
		public Null_literalContext null_literal() {
			return getRuleContext(Null_literalContext.class,0);
		}
		public Row_value_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_row_value_expression; }
	}

	public final Row_value_expressionContext row_value_expression() throws RecognitionException {
		Row_value_expressionContext _localctx = new Row_value_expressionContext(_ctx, getState());
		enterRule(_localctx, 222, RULE_row_value_expression);
		try {
			setState(1086);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ANY:
			case CASE:
			case CAST:
			case FALSE:
			case IGNORE:
			case IN:
			case LEFT:
			case NULLS:
			case NUMBER_TYPE:
			case RESPECT:
			case RIGHT:
			case RETURNING:
			case SOME:
			case TRUE:
			case TRYCAST:
			case UNION:
			case WITH:
			case ASC:
			case AVG:
			case BETWEEN:
			case BY:
			case CENTURY:
			case CHARACTER:
			case COLLECT:
			case COALESCE:
			case COLUMN:
			case COUNT:
			case COUNT_IF:
			case CUBE:
			case DAY:
			case DEC:
			case DECADE:
			case DESC:
			case DOW:
			case DOY:
			case DROP:
			case EPOCH:
			case EVERY:
			case EXISTS:
			case EXTERNAL:
			case EXTRACT:
			case FILTER:
			case FIRST:
			case FIRST_VALUE:
			case FORMAT:
			case FUSION:
			case GROUPING:
			case HASH:
			case INDEX:
			case INSERT:
			case INTERSECTION:
			case ISODOW:
			case ISOYEAR:
			case LAG:
			case LAST:
			case LAST_VALUE:
			case LEAD:
			case LESS:
			case LIST:
			case LOCATION:
			case MAX:
			case MAXVALUE:
			case MICROSECONDS:
			case MILLENNIUM:
			case MILLISECONDS:
			case MIN:
			case MINUTE:
			case MONTH:
			case NATIONAL:
			case NTH_VALUE:
			case NULLIF:
			case OVER:
			case OVERWRITE:
			case PARTITION:
			case PARTITIONS:
			case PRECISION:
			case PURGE:
			case QUARTER:
			case RANGE:
			case RANK:
			case REGEXP:
			case RLIKE:
			case ROLLUP:
			case ROW_NUMBER:
			case ROWS:
			case SECOND:
			case SET:
			case SIMILAR:
			case STDDEV_POP:
			case STDDEV_SAMP:
			case SUBPARTITION:
			case SUM:
			case TABLESPACE:
			case THAN:
			case TIMEZONE:
			case TIMEZONE_HOUR:
			case TIMEZONE_MINUTE:
			case TRIM:
			case TO:
			case UPDATE:
			case UNKNOWN:
			case VALUES:
			case VAR_SAMP:
			case VAR_POP:
			case VARYING:
			case WEEK:
			case YEAR:
			case ZONE:
			case ANY_VALUE:
			case CORR:
			case COVAR_POP:
			case COVAR_SAMP:
			case LISTAGG:
			case MEDIAN:
			case PERCENTILE_CONT:
			case PERCENTILE_DISC:
			case STDDEV:
			case VARIANCE_POP:
			case VARIANCE:
			case VARIANCE_SAMP:
			case CUME_DIST:
			case DENSE_RANK:
			case NTILE:
			case PERCENT_RANK:
			case WIDTH_BUCKET:
			case BITAND_AGG:
			case BITOR_AGG:
			case BITXOR_AGG:
			case HASH_AGG:
			case ARRAY_AGG:
			case OBJECT_AGG:
			case REGR_AVGX:
			case REGR_AVGY:
			case REGR_COUNT:
			case REGR_INTERCEPT:
			case REGR_R2:
			case REGR_SLOPE:
			case REGR_SXX:
			case REGR_SXY:
			case REGR_SYY:
			case APPROX_COUNT_DISTINCT:
			case HLL:
			case HLL_ACCUMULATE:
			case HLL_COMBINE:
			case HLL_EXPORT:
			case HLL_IMPORT:
			case APPROXIMATE_JACCARD_INDEX:
			case APPROXIMATE_SIMILARITY:
			case MINHASH:
			case MINHASH_COMBINE:
			case APPROX_TOP_K:
			case APPROX_TOP_K_ACCUMULATE:
			case APPROX_TOP_K_COMBINE:
			case APPROX_PERCENTILE:
			case APPROX_PERCENTILE_ACCUMULATE:
			case APPROX_PERCENTILE_COMBINE:
			case ABSTIME:
			case ANYARRAY:
			case ARRAY:
			case BOOL:
			case BIT:
			case VARBIT:
			case CIDR:
			case INET:
			case INET4:
			case INTERVAL:
			case INT1:
			case INT2:
			case INT4:
			case INT8:
			case JSON:
			case JSONB:
			case MACADDR:
			case NAME:
			case OID:
			case PG_LSN:
			case PG_NODE_TREE:
			case REGPROC:
			case XID:
			case UUID:
			case TINYINT:
			case SMALLINT:
			case INT:
			case BIGINT:
			case BIGSERIAL:
			case SMALLSERIAL:
			case SERIAL:
			case MONEY:
			case FLOAT4:
			case FLOAT8:
			case REAL:
			case FLOAT:
			case DOUBLE:
			case NUMERIC:
			case CHAR:
			case VARCHAR:
			case NCHAR:
			case NVARCHAR:
			case STRING:
			case DATE:
			case DATETIME:
			case TIME:
			case TIMETZ:
			case TIMESTAMP:
			case TIMESTAMP_LTZ:
			case TIMESTAMP_NTZ:
			case TIMESTAMP_TZ:
			case TIMESTAMPTZ:
			case TEXT:
			case BINARY:
			case VARBINARY:
			case BLOB:
			case BYTEA:
			case OBJECT:
			case STRUCT:
			case VARIANT:
			case LEFT_PAREN:
			case DOT:
			case NUMBER:
			case Bracket_Identifier:
			case Identifier:
			case Double_Quoted_Numeric_Identifier:
			case Dollar_Sign_Identifier:
			case Character_String_Literal:
				enterOuterAlt(_localctx, 1);
				{
				setState(1084);
				nonparenthesized_value_expression_primary();
				}
				break;
			case NULL:
				enterOuterAlt(_localctx, 2);
				{
				setState(1085);
				null_literal();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Row_value_predicandContext extends ParserRuleContext {
		public Nonparenthesized_value_expression_primaryContext nonparenthesized_value_expression_primary() {
			return getRuleContext(Nonparenthesized_value_expression_primaryContext.class,0);
		}
		public Common_value_expressionContext common_value_expression() {
			return getRuleContext(Common_value_expressionContext.class,0);
		}
		public Variable_identifierContext variable_identifier() {
			return getRuleContext(Variable_identifierContext.class,0);
		}
		public Row_value_predicandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_row_value_predicand; }
	}

	public final Row_value_predicandContext row_value_predicand() throws RecognitionException {
		Row_value_predicandContext _localctx = new Row_value_predicandContext(_ctx, getState());
		enterRule(_localctx, 224, RULE_row_value_predicand);
		try {
			setState(1091);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,101,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1088);
				nonparenthesized_value_expression_primary();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1089);
				common_value_expression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1090);
				variable_identifier();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Where_clauseContext extends ParserRuleContext {
		public TerminalNode WHERE() { return getToken(SQLSelectParserParser.WHERE, 0); }
		public Search_conditionContext search_condition() {
			return getRuleContext(Search_conditionContext.class,0);
		}
		public Where_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_where_clause; }
	}

	public final Where_clauseContext where_clause() throws RecognitionException {
		Where_clauseContext _localctx = new Where_clauseContext(_ctx, getState());
		enterRule(_localctx, 226, RULE_where_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1093);
			match(WHERE);
			setState(1094);
			search_condition();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Search_conditionContext extends ParserRuleContext {
		public Value_expressionContext value_expression() {
			return getRuleContext(Value_expressionContext.class,0);
		}
		public Search_conditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_search_condition; }
	}

	public final Search_conditionContext search_condition() throws RecognitionException {
		Search_conditionContext _localctx = new Search_conditionContext(_ctx, getState());
		enterRule(_localctx, 228, RULE_search_condition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1096);
			value_expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Orderby_clauseContext extends ParserRuleContext {
		public TerminalNode ORDER() { return getToken(SQLSelectParserParser.ORDER, 0); }
		public TerminalNode BY() { return getToken(SQLSelectParserParser.BY, 0); }
		public Sort_specifier_listContext sort_specifier_list() {
			return getRuleContext(Sort_specifier_listContext.class,0);
		}
		public Orderby_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orderby_clause; }
	}

	public final Orderby_clauseContext orderby_clause() throws RecognitionException {
		Orderby_clauseContext _localctx = new Orderby_clauseContext(_ctx, getState());
		enterRule(_localctx, 230, RULE_orderby_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1098);
			match(ORDER);
			setState(1099);
			match(BY);
			setState(1100);
			sort_specifier_list();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Sort_specifier_listContext extends ParserRuleContext {
		public List<Sort_specifierContext> sort_specifier() {
			return getRuleContexts(Sort_specifierContext.class);
		}
		public Sort_specifierContext sort_specifier(int i) {
			return getRuleContext(Sort_specifierContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SQLSelectParserParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SQLSelectParserParser.COMMA, i);
		}
		public Sort_specifier_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sort_specifier_list; }
	}

	public final Sort_specifier_listContext sort_specifier_list() throws RecognitionException {
		Sort_specifier_listContext _localctx = new Sort_specifier_listContext(_ctx, getState());
		enterRule(_localctx, 232, RULE_sort_specifier_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1102);
			sort_specifier();
			setState(1107);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1103);
				match(COMMA);
				setState(1104);
				sort_specifier();
				}
				}
				setState(1109);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Sort_specifierContext extends ParserRuleContext {
		public Row_value_predicandContext key;
		public Order_specificationContext order;
		public Null_orderingContext null_order;
		public Row_value_predicandContext row_value_predicand() {
			return getRuleContext(Row_value_predicandContext.class,0);
		}
		public Order_specificationContext order_specification() {
			return getRuleContext(Order_specificationContext.class,0);
		}
		public Null_orderingContext null_ordering() {
			return getRuleContext(Null_orderingContext.class,0);
		}
		public Sort_specifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sort_specifier; }
	}

	public final Sort_specifierContext sort_specifier() throws RecognitionException {
		Sort_specifierContext _localctx = new Sort_specifierContext(_ctx, getState());
		enterRule(_localctx, 234, RULE_sort_specifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1110);
			((Sort_specifierContext)_localctx).key = row_value_predicand();
			setState(1112);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ASC || _la==DESC) {
				{
				setState(1111);
				((Sort_specifierContext)_localctx).order = order_specification();
				}
			}

			setState(1115);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NULLS) {
				{
				setState(1114);
				((Sort_specifierContext)_localctx).null_order = null_ordering();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Order_specificationContext extends ParserRuleContext {
		public TerminalNode ASC() { return getToken(SQLSelectParserParser.ASC, 0); }
		public TerminalNode DESC() { return getToken(SQLSelectParserParser.DESC, 0); }
		public Order_specificationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_order_specification; }
	}

	public final Order_specificationContext order_specification() throws RecognitionException {
		Order_specificationContext _localctx = new Order_specificationContext(_ctx, getState());
		enterRule(_localctx, 236, RULE_order_specification);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1117);
			_la = _input.LA(1);
			if ( !(_la==ASC || _la==DESC) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Null_orderingContext extends ParserRuleContext {
		public TerminalNode NULLS() { return getToken(SQLSelectParserParser.NULLS, 0); }
		public Null_first_lastContext null_first_last() {
			return getRuleContext(Null_first_lastContext.class,0);
		}
		public Null_orderingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_null_ordering; }
	}

	public final Null_orderingContext null_ordering() throws RecognitionException {
		Null_orderingContext _localctx = new Null_orderingContext(_ctx, getState());
		enterRule(_localctx, 238, RULE_null_ordering);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1119);
			match(NULLS);
			setState(1120);
			null_first_last();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Null_first_lastContext extends ParserRuleContext {
		public TerminalNode FIRST() { return getToken(SQLSelectParserParser.FIRST, 0); }
		public TerminalNode LAST() { return getToken(SQLSelectParserParser.LAST, 0); }
		public Null_first_lastContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_null_first_last; }
	}

	public final Null_first_lastContext null_first_last() throws RecognitionException {
		Null_first_lastContext _localctx = new Null_first_lastContext(_ctx, getState());
		enterRule(_localctx, 240, RULE_null_first_last);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1122);
			_la = _input.LA(1);
			if ( !(_la==FIRST || _la==LAST) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Limit_clauseContext extends ParserRuleContext {
		public Additive_expressionContext e;
		public Additive_expressionContext o;
		public TerminalNode LIMIT() { return getToken(SQLSelectParserParser.LIMIT, 0); }
		public List<Additive_expressionContext> additive_expression() {
			return getRuleContexts(Additive_expressionContext.class);
		}
		public Additive_expressionContext additive_expression(int i) {
			return getRuleContext(Additive_expressionContext.class,i);
		}
		public TerminalNode OFFSET() { return getToken(SQLSelectParserParser.OFFSET, 0); }
		public Limit_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_limit_clause; }
	}

	public final Limit_clauseContext limit_clause() throws RecognitionException {
		Limit_clauseContext _localctx = new Limit_clauseContext(_ctx, getState());
		enterRule(_localctx, 242, RULE_limit_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1124);
			match(LIMIT);
			setState(1125);
			((Limit_clauseContext)_localctx).e = additive_expression();
			setState(1128);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OFFSET) {
				{
				setState(1126);
				match(OFFSET);
				setState(1127);
				((Limit_clauseContext)_localctx).o = additive_expression();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Groupby_clauseContext extends ParserRuleContext {
		public TerminalNode GROUP() { return getToken(SQLSelectParserParser.GROUP, 0); }
		public TerminalNode BY() { return getToken(SQLSelectParserParser.BY, 0); }
		public Grouping_element_listContext grouping_element_list() {
			return getRuleContext(Grouping_element_listContext.class,0);
		}
		public Select_listContext select_list() {
			return getRuleContext(Select_listContext.class,0);
		}
		public Groupby_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_groupby_clause; }
	}

	public final Groupby_clauseContext groupby_clause() throws RecognitionException {
		Groupby_clauseContext _localctx = new Groupby_clauseContext(_ctx, getState());
		enterRule(_localctx, 244, RULE_groupby_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1130);
			match(GROUP);
			setState(1131);
			match(BY);
			setState(1134);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,106,_ctx) ) {
			case 1:
				{
				setState(1132);
				grouping_element_list();
				}
				break;
			case 2:
				{
				setState(1133);
				select_list();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Grouping_element_listContext extends ParserRuleContext {
		public List<Grouping_elementContext> grouping_element() {
			return getRuleContexts(Grouping_elementContext.class);
		}
		public Grouping_elementContext grouping_element(int i) {
			return getRuleContext(Grouping_elementContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SQLSelectParserParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SQLSelectParserParser.COMMA, i);
		}
		public Grouping_element_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_grouping_element_list; }
	}

	public final Grouping_element_listContext grouping_element_list() throws RecognitionException {
		Grouping_element_listContext _localctx = new Grouping_element_listContext(_ctx, getState());
		enterRule(_localctx, 246, RULE_grouping_element_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1136);
			grouping_element();
			setState(1141);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1137);
				match(COMMA);
				setState(1138);
				grouping_element();
				}
				}
				setState(1143);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Grouping_elementContext extends ParserRuleContext {
		public Rollup_listContext rollup_list() {
			return getRuleContext(Rollup_listContext.class,0);
		}
		public Cube_listContext cube_list() {
			return getRuleContext(Cube_listContext.class,0);
		}
		public Empty_grouping_setContext empty_grouping_set() {
			return getRuleContext(Empty_grouping_setContext.class,0);
		}
		public Ordinary_grouping_setContext ordinary_grouping_set() {
			return getRuleContext(Ordinary_grouping_setContext.class,0);
		}
		public Grouping_elementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_grouping_element; }
	}

	public final Grouping_elementContext grouping_element() throws RecognitionException {
		Grouping_elementContext _localctx = new Grouping_elementContext(_ctx, getState());
		enterRule(_localctx, 248, RULE_grouping_element);
		try {
			setState(1148);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,108,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1144);
				rollup_list();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1145);
				cube_list();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1146);
				empty_grouping_set();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1147);
				ordinary_grouping_set();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Ordinary_grouping_set_listContext extends ParserRuleContext {
		public List<Ordinary_grouping_setContext> ordinary_grouping_set() {
			return getRuleContexts(Ordinary_grouping_setContext.class);
		}
		public Ordinary_grouping_setContext ordinary_grouping_set(int i) {
			return getRuleContext(Ordinary_grouping_setContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SQLSelectParserParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SQLSelectParserParser.COMMA, i);
		}
		public Ordinary_grouping_set_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ordinary_grouping_set_list; }
	}

	public final Ordinary_grouping_set_listContext ordinary_grouping_set_list() throws RecognitionException {
		Ordinary_grouping_set_listContext _localctx = new Ordinary_grouping_set_listContext(_ctx, getState());
		enterRule(_localctx, 250, RULE_ordinary_grouping_set_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1150);
			ordinary_grouping_set();
			setState(1155);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1151);
				match(COMMA);
				setState(1152);
				ordinary_grouping_set();
				}
				}
				setState(1157);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Ordinary_grouping_setContext extends ParserRuleContext {
		public Row_value_predicandContext row_value_predicand() {
			return getRuleContext(Row_value_predicandContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public Row_value_predicand_listContext row_value_predicand_list() {
			return getRuleContext(Row_value_predicand_listContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Ordinary_grouping_setContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ordinary_grouping_set; }
	}

	public final Ordinary_grouping_setContext ordinary_grouping_set() throws RecognitionException {
		Ordinary_grouping_setContext _localctx = new Ordinary_grouping_setContext(_ctx, getState());
		enterRule(_localctx, 252, RULE_ordinary_grouping_set);
		try {
			setState(1163);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,110,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1158);
				row_value_predicand();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1159);
				match(LEFT_PAREN);
				setState(1160);
				row_value_predicand_list();
				setState(1161);
				match(RIGHT_PAREN);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Rollup_listContext extends ParserRuleContext {
		public Ordinary_grouping_set_listContext c;
		public TerminalNode ROLLUP() { return getToken(SQLSelectParserParser.ROLLUP, 0); }
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Ordinary_grouping_set_listContext ordinary_grouping_set_list() {
			return getRuleContext(Ordinary_grouping_set_listContext.class,0);
		}
		public Rollup_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rollup_list; }
	}

	public final Rollup_listContext rollup_list() throws RecognitionException {
		Rollup_listContext _localctx = new Rollup_listContext(_ctx, getState());
		enterRule(_localctx, 254, RULE_rollup_list);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1165);
			match(ROLLUP);
			setState(1166);
			match(LEFT_PAREN);
			setState(1167);
			((Rollup_listContext)_localctx).c = ordinary_grouping_set_list();
			setState(1168);
			match(RIGHT_PAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Cube_listContext extends ParserRuleContext {
		public Ordinary_grouping_set_listContext c;
		public TerminalNode CUBE() { return getToken(SQLSelectParserParser.CUBE, 0); }
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Ordinary_grouping_set_listContext ordinary_grouping_set_list() {
			return getRuleContext(Ordinary_grouping_set_listContext.class,0);
		}
		public Cube_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cube_list; }
	}

	public final Cube_listContext cube_list() throws RecognitionException {
		Cube_listContext _localctx = new Cube_listContext(_ctx, getState());
		enterRule(_localctx, 256, RULE_cube_list);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1170);
			match(CUBE);
			setState(1171);
			match(LEFT_PAREN);
			setState(1172);
			((Cube_listContext)_localctx).c = ordinary_grouping_set_list();
			setState(1173);
			match(RIGHT_PAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Empty_grouping_setContext extends ParserRuleContext {
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Empty_grouping_setContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_empty_grouping_set; }
	}

	public final Empty_grouping_setContext empty_grouping_set() throws RecognitionException {
		Empty_grouping_setContext _localctx = new Empty_grouping_setContext(_ctx, getState());
		enterRule(_localctx, 258, RULE_empty_grouping_set);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1175);
			match(LEFT_PAREN);
			setState(1176);
			match(RIGHT_PAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Having_clauseContext extends ParserRuleContext {
		public TerminalNode HAVING() { return getToken(SQLSelectParserParser.HAVING, 0); }
		public Boolean_value_expressionContext boolean_value_expression() {
			return getRuleContext(Boolean_value_expressionContext.class,0);
		}
		public Having_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_having_clause; }
	}

	public final Having_clauseContext having_clause() throws RecognitionException {
		Having_clauseContext _localctx = new Having_clauseContext(_ctx, getState());
		enterRule(_localctx, 260, RULE_having_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1178);
			match(HAVING);
			setState(1179);
			boolean_value_expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Row_value_predicand_listContext extends ParserRuleContext {
		public List<Row_value_predicandContext> row_value_predicand() {
			return getRuleContexts(Row_value_predicandContext.class);
		}
		public Row_value_predicandContext row_value_predicand(int i) {
			return getRuleContext(Row_value_predicandContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SQLSelectParserParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SQLSelectParserParser.COMMA, i);
		}
		public Row_value_predicand_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_row_value_predicand_list; }
	}

	public final Row_value_predicand_listContext row_value_predicand_list() throws RecognitionException {
		Row_value_predicand_listContext _localctx = new Row_value_predicand_listContext(_ctx, getState());
		enterRule(_localctx, 262, RULE_row_value_predicand_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1181);
			row_value_predicand();
			setState(1186);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1182);
				match(COMMA);
				setState(1183);
				row_value_predicand();
				}
				}
				setState(1188);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comparison_predicateContext extends ParserRuleContext {
		public Row_value_predicandContext left;
		public Comparison_operatorContext c;
		public Row_value_predicandContext right;
		public List<Row_value_predicandContext> row_value_predicand() {
			return getRuleContexts(Row_value_predicandContext.class);
		}
		public Row_value_predicandContext row_value_predicand(int i) {
			return getRuleContext(Row_value_predicandContext.class,i);
		}
		public Comparison_operatorContext comparison_operator() {
			return getRuleContext(Comparison_operatorContext.class,0);
		}
		public Comparison_predicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparison_predicate; }
	}

	public final Comparison_predicateContext comparison_predicate() throws RecognitionException {
		Comparison_predicateContext _localctx = new Comparison_predicateContext(_ctx, getState());
		enterRule(_localctx, 264, RULE_comparison_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1189);
			((Comparison_predicateContext)_localctx).left = row_value_predicand();
			setState(1190);
			((Comparison_predicateContext)_localctx).c = comparison_operator();
			setState(1191);
			((Comparison_predicateContext)_localctx).right = row_value_predicand();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comparison_operatorContext extends ParserRuleContext {
		public Comp_opContext comp_op() {
			return getRuleContext(Comp_opContext.class,0);
		}
		public Relative_comp_opContext relative_comp_op() {
			return getRuleContext(Relative_comp_opContext.class,0);
		}
		public NotContext not() {
			return getRuleContext(NotContext.class,0);
		}
		public Similarity_opContext similarity_op() {
			return getRuleContext(Similarity_opContext.class,0);
		}
		public Comparison_operatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparison_operator; }
	}

	public final Comparison_operatorContext comparison_operator() throws RecognitionException {
		Comparison_operatorContext _localctx = new Comparison_operatorContext(_ctx, getState());
		enterRule(_localctx, 266, RULE_comparison_operator);
		int _la;
		try {
			setState(1199);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EQUAL:
			case NOT_EQUAL:
			case LTH:
			case LEQ:
			case GTH:
			case GEQ:
				enterOuterAlt(_localctx, 1);
				{
				setState(1193);
				comp_op();
				}
				break;
			case ILIKE:
			case LIKE:
			case NOT:
			case REGEXP:
			case RLIKE:
			case SIMILAR:
				enterOuterAlt(_localctx, 2);
				{
				setState(1195);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(1194);
					not();
					}
				}

				setState(1197);
				relative_comp_op();
				}
				break;
			case Similar_To:
			case Not_Similar_To:
			case Similar_To_Case_Insensitive:
			case Not_Similar_To_Case_Insensitive:
				enterOuterAlt(_localctx, 3);
				{
				setState(1198);
				similarity_op();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Relative_comp_opContext extends ParserRuleContext {
		public TerminalNode LIKE() { return getToken(SQLSelectParserParser.LIKE, 0); }
		public TerminalNode ILIKE() { return getToken(SQLSelectParserParser.ILIKE, 0); }
		public TerminalNode SIMILAR() { return getToken(SQLSelectParserParser.SIMILAR, 0); }
		public TerminalNode TO() { return getToken(SQLSelectParserParser.TO, 0); }
		public TerminalNode REGEXP() { return getToken(SQLSelectParserParser.REGEXP, 0); }
		public TerminalNode RLIKE() { return getToken(SQLSelectParserParser.RLIKE, 0); }
		public Relative_comp_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relative_comp_op; }
	}

	public final Relative_comp_opContext relative_comp_op() throws RecognitionException {
		Relative_comp_opContext _localctx = new Relative_comp_opContext(_ctx, getState());
		enterRule(_localctx, 268, RULE_relative_comp_op);
		try {
			setState(1207);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LIKE:
				enterOuterAlt(_localctx, 1);
				{
				setState(1201);
				match(LIKE);
				}
				break;
			case ILIKE:
				enterOuterAlt(_localctx, 2);
				{
				setState(1202);
				match(ILIKE);
				}
				break;
			case SIMILAR:
				enterOuterAlt(_localctx, 3);
				{
				setState(1203);
				match(SIMILAR);
				setState(1204);
				match(TO);
				}
				break;
			case REGEXP:
				enterOuterAlt(_localctx, 4);
				{
				setState(1205);
				match(REGEXP);
				}
				break;
			case RLIKE:
				enterOuterAlt(_localctx, 5);
				{
				setState(1206);
				match(RLIKE);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Similarity_opContext extends ParserRuleContext {
		public TerminalNode Similar_To() { return getToken(SQLSelectParserParser.Similar_To, 0); }
		public TerminalNode Not_Similar_To() { return getToken(SQLSelectParserParser.Not_Similar_To, 0); }
		public TerminalNode Similar_To_Case_Insensitive() { return getToken(SQLSelectParserParser.Similar_To_Case_Insensitive, 0); }
		public TerminalNode Not_Similar_To_Case_Insensitive() { return getToken(SQLSelectParserParser.Not_Similar_To_Case_Insensitive, 0); }
		public Similarity_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_similarity_op; }
	}

	public final Similarity_opContext similarity_op() throws RecognitionException {
		Similarity_opContext _localctx = new Similarity_opContext(_ctx, getState());
		enterRule(_localctx, 270, RULE_similarity_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1209);
			_la = _input.LA(1);
			if ( !(((((_la - 269)) & ~0x3f) == 0 && ((1L << (_la - 269)) & 15L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comp_opContext extends ParserRuleContext {
		public TerminalNode EQUAL() { return getToken(SQLSelectParserParser.EQUAL, 0); }
		public TerminalNode NOT_EQUAL() { return getToken(SQLSelectParserParser.NOT_EQUAL, 0); }
		public TerminalNode LTH() { return getToken(SQLSelectParserParser.LTH, 0); }
		public TerminalNode LEQ() { return getToken(SQLSelectParserParser.LEQ, 0); }
		public TerminalNode GTH() { return getToken(SQLSelectParserParser.GTH, 0); }
		public TerminalNode GEQ() { return getToken(SQLSelectParserParser.GEQ, 0); }
		public Comp_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comp_op; }
	}

	public final Comp_opContext comp_op() throws RecognitionException {
		Comp_opContext _localctx = new Comp_opContext(_ctx, getState());
		enterRule(_localctx, 272, RULE_comp_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1211);
			_la = _input.LA(1);
			if ( !(((((_la - 274)) & ~0x3f) == 0 && ((1L << (_la - 274)) & 993L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Between_predicateContext extends ParserRuleContext {
		public Row_value_predicandContext begin;
		public Row_value_predicandContext end;
		public List<Row_value_predicandContext> row_value_predicand() {
			return getRuleContexts(Row_value_predicandContext.class);
		}
		public Row_value_predicandContext row_value_predicand(int i) {
			return getRuleContext(Row_value_predicandContext.class,i);
		}
		public TerminalNode BETWEEN() { return getToken(SQLSelectParserParser.BETWEEN, 0); }
		public TerminalNode AND() { return getToken(SQLSelectParserParser.AND, 0); }
		public NotContext not() {
			return getRuleContext(NotContext.class,0);
		}
		public SymmetryContext symmetry() {
			return getRuleContext(SymmetryContext.class,0);
		}
		public Between_predicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_between_predicate; }
	}

	public final Between_predicateContext between_predicate() throws RecognitionException {
		Between_predicateContext _localctx = new Between_predicateContext(_ctx, getState());
		enterRule(_localctx, 274, RULE_between_predicate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1213);
			row_value_predicand();
			setState(1215);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(1214);
				not();
				}
			}

			setState(1217);
			match(BETWEEN);
			setState(1219);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ASYMMETRIC || _la==SYMMETRIC) {
				{
				setState(1218);
				symmetry();
				}
			}

			setState(1221);
			((Between_predicateContext)_localctx).begin = row_value_predicand();
			setState(1222);
			match(AND);
			setState(1223);
			((Between_predicateContext)_localctx).end = row_value_predicand();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SymmetryContext extends ParserRuleContext {
		public TerminalNode ASYMMETRIC() { return getToken(SQLSelectParserParser.ASYMMETRIC, 0); }
		public TerminalNode SYMMETRIC() { return getToken(SQLSelectParserParser.SYMMETRIC, 0); }
		public SymmetryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_symmetry; }
	}

	public final SymmetryContext symmetry() throws RecognitionException {
		SymmetryContext _localctx = new SymmetryContext(_ctx, getState());
		enterRule(_localctx, 276, RULE_symmetry);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1225);
			_la = _input.LA(1);
			if ( !(_la==ASYMMETRIC || _la==SYMMETRIC) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class In_predicateContext extends ParserRuleContext {
		public Row_value_predicandContext row_value_predicand() {
			return getRuleContext(Row_value_predicandContext.class,0);
		}
		public TerminalNode IN() { return getToken(SQLSelectParserParser.IN, 0); }
		public In_predicate_valueContext in_predicate_value() {
			return getRuleContext(In_predicate_valueContext.class,0);
		}
		public NotContext not() {
			return getRuleContext(NotContext.class,0);
		}
		public In_predicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_in_predicate; }
	}

	public final In_predicateContext in_predicate() throws RecognitionException {
		In_predicateContext _localctx = new In_predicateContext(_ctx, getState());
		enterRule(_localctx, 278, RULE_in_predicate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1227);
			row_value_predicand();
			setState(1229);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(1228);
				not();
				}
			}

			setState(1231);
			match(IN);
			setState(1232);
			in_predicate_value();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Like_any_predicateContext extends ParserRuleContext {
		public Row_value_predicandContext row_value_predicand() {
			return getRuleContext(Row_value_predicandContext.class,0);
		}
		public Like_any_operatorContext like_any_operator() {
			return getRuleContext(Like_any_operatorContext.class,0);
		}
		public In_predicate_valueContext in_predicate_value() {
			return getRuleContext(In_predicate_valueContext.class,0);
		}
		public NotContext not() {
			return getRuleContext(NotContext.class,0);
		}
		public Like_any_predicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_like_any_predicate; }
	}

	public final Like_any_predicateContext like_any_predicate() throws RecognitionException {
		Like_any_predicateContext _localctx = new Like_any_predicateContext(_ctx, getState());
		enterRule(_localctx, 280, RULE_like_any_predicate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1234);
			row_value_predicand();
			setState(1236);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(1235);
				not();
				}
			}

			setState(1238);
			like_any_operator();
			setState(1239);
			in_predicate_value();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Like_any_operatorContext extends ParserRuleContext {
		public TerminalNode ANY() { return getToken(SQLSelectParserParser.ANY, 0); }
		public TerminalNode LIKE() { return getToken(SQLSelectParserParser.LIKE, 0); }
		public TerminalNode ILIKE() { return getToken(SQLSelectParserParser.ILIKE, 0); }
		public Like_any_operatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_like_any_operator; }
	}

	public final Like_any_operatorContext like_any_operator() throws RecognitionException {
		Like_any_operatorContext _localctx = new Like_any_operatorContext(_ctx, getState());
		enterRule(_localctx, 282, RULE_like_any_operator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1241);
			_la = _input.LA(1);
			if ( !(_la==ILIKE || _la==LIKE) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(1242);
			match(ANY);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class In_predicate_valueContext extends ParserRuleContext {
		public SubqueryContext subquery() {
			return getRuleContext(SubqueryContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public In_value_listContext in_value_list() {
			return getRuleContext(In_value_listContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Variable_identifierContext variable_identifier() {
			return getRuleContext(Variable_identifierContext.class,0);
		}
		public In_predicate_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_in_predicate_value; }
	}

	public final In_predicate_valueContext in_predicate_value() throws RecognitionException {
		In_predicate_valueContext _localctx = new In_predicate_valueContext(_ctx, getState());
		enterRule(_localctx, 284, RULE_in_predicate_value);
		try {
			setState(1250);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,119,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1244);
				subquery();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1245);
				match(LEFT_PAREN);
				setState(1246);
				in_value_list();
				setState(1247);
				match(RIGHT_PAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1249);
				variable_identifier();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class In_value_listContext extends ParserRuleContext {
		public List<Row_value_expressionContext> row_value_expression() {
			return getRuleContexts(Row_value_expressionContext.class);
		}
		public Row_value_expressionContext row_value_expression(int i) {
			return getRuleContext(Row_value_expressionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SQLSelectParserParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SQLSelectParserParser.COMMA, i);
		}
		public In_value_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_in_value_list; }
	}

	public final In_value_listContext in_value_list() throws RecognitionException {
		In_value_listContext _localctx = new In_value_listContext(_ctx, getState());
		enterRule(_localctx, 286, RULE_in_value_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1252);
			row_value_expression();
			setState(1257);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1253);
				match(COMMA);
				setState(1254);
				row_value_expression();
				}
				}
				setState(1259);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Values_statement_endContext extends ParserRuleContext {
		public Values_statement_primaryContext values_statement_primary() {
			return getRuleContext(Values_statement_primaryContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SQLSelectParserParser.EOF, 0); }
		public Values_statement_endContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_values_statement_end; }
	}

	public final Values_statement_endContext values_statement_end() throws RecognitionException {
		Values_statement_endContext _localctx = new Values_statement_endContext(_ctx, getState());
		enterRule(_localctx, 288, RULE_values_statement_end);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1260);
			values_statement_primary();
			setState(1261);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Values_statement_primaryContext extends ParserRuleContext {
		public Fully_defined_values_statementContext fully_defined_values_statement() {
			return getRuleContext(Fully_defined_values_statementContext.class,0);
		}
		public Aliased_values_statementContext aliased_values_statement() {
			return getRuleContext(Aliased_values_statementContext.class,0);
		}
		public Values_statementContext values_statement() {
			return getRuleContext(Values_statementContext.class,0);
		}
		public Values_statement_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_values_statement_primary; }
	}

	public final Values_statement_primaryContext values_statement_primary() throws RecognitionException {
		Values_statement_primaryContext _localctx = new Values_statement_primaryContext(_ctx, getState());
		enterRule(_localctx, 290, RULE_values_statement_primary);
		try {
			setState(1266);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,121,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1263);
				fully_defined_values_statement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1264);
				aliased_values_statement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1265);
				values_statement();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Fully_defined_values_statementContext extends ParserRuleContext {
		public Values_statementContext values_statement() {
			return getRuleContext(Values_statementContext.class,0);
		}
		public As_clauseContext as_clause() {
			return getRuleContext(As_clauseContext.class,0);
		}
		public Values_columnsContext values_columns() {
			return getRuleContext(Values_columnsContext.class,0);
		}
		public Fully_defined_values_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fully_defined_values_statement; }
	}

	public final Fully_defined_values_statementContext fully_defined_values_statement() throws RecognitionException {
		Fully_defined_values_statementContext _localctx = new Fully_defined_values_statementContext(_ctx, getState());
		enterRule(_localctx, 292, RULE_fully_defined_values_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1268);
			values_statement();
			setState(1269);
			as_clause();
			setState(1270);
			values_columns();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Aliased_values_statementContext extends ParserRuleContext {
		public Values_statementContext values_statement() {
			return getRuleContext(Values_statementContext.class,0);
		}
		public As_clauseContext as_clause() {
			return getRuleContext(As_clauseContext.class,0);
		}
		public Aliased_values_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aliased_values_statement; }
	}

	public final Aliased_values_statementContext aliased_values_statement() throws RecognitionException {
		Aliased_values_statementContext _localctx = new Aliased_values_statementContext(_ctx, getState());
		enterRule(_localctx, 294, RULE_aliased_values_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1272);
			values_statement();
			setState(1273);
			as_clause();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Values_statementContext extends ParserRuleContext {
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public TerminalNode VALUES() { return getToken(SQLSelectParserParser.VALUES, 0); }
		public Values_matrixContext values_matrix() {
			return getRuleContext(Values_matrixContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Values_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_values_statement; }
	}

	public final Values_statementContext values_statement() throws RecognitionException {
		Values_statementContext _localctx = new Values_statementContext(_ctx, getState());
		enterRule(_localctx, 296, RULE_values_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1275);
			match(LEFT_PAREN);
			setState(1276);
			match(VALUES);
			setState(1277);
			values_matrix();
			setState(1278);
			match(RIGHT_PAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Values_matrixContext extends ParserRuleContext {
		public List<Values_rowContext> values_row() {
			return getRuleContexts(Values_rowContext.class);
		}
		public Values_rowContext values_row(int i) {
			return getRuleContext(Values_rowContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SQLSelectParserParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SQLSelectParserParser.COMMA, i);
		}
		public Values_matrixContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_values_matrix; }
	}

	public final Values_matrixContext values_matrix() throws RecognitionException {
		Values_matrixContext _localctx = new Values_matrixContext(_ctx, getState());
		enterRule(_localctx, 298, RULE_values_matrix);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1280);
			values_row();
			setState(1285);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1281);
				match(COMMA);
				setState(1282);
				values_row();
				}
				}
				setState(1287);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Values_rowContext extends ParserRuleContext {
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public In_value_listContext in_value_list() {
			return getRuleContext(In_value_listContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Values_rowContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_values_row; }
	}

	public final Values_rowContext values_row() throws RecognitionException {
		Values_rowContext _localctx = new Values_rowContext(_ctx, getState());
		enterRule(_localctx, 300, RULE_values_row);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1288);
			match(LEFT_PAREN);
			setState(1289);
			in_value_list();
			setState(1290);
			match(RIGHT_PAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Values_columnsContext extends ParserRuleContext {
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public Column_reference_listContext column_reference_list() {
			return getRuleContext(Column_reference_listContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Values_columnsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_values_columns; }
	}

	public final Values_columnsContext values_columns() throws RecognitionException {
		Values_columnsContext _localctx = new Values_columnsContext(_ctx, getState());
		enterRule(_localctx, 302, RULE_values_columns);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(1292);
			match(LEFT_PAREN);
			setState(1293);
			column_reference_list();
			setState(1294);
			match(RIGHT_PAREN);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Null_predicateContext extends ParserRuleContext {
		public Row_value_predicandContext row_value_predicand() {
			return getRuleContext(Row_value_predicandContext.class,0);
		}
		public Is_null_clauseContext is_null_clause() {
			return getRuleContext(Is_null_clauseContext.class,0);
		}
		public Null_predicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_null_predicate; }
	}

	public final Null_predicateContext null_predicate() throws RecognitionException {
		Null_predicateContext _localctx = new Null_predicateContext(_ctx, getState());
		enterRule(_localctx, 304, RULE_null_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1296);
			row_value_predicand();
			setState(1297);
			is_null_clause();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Is_null_clauseContext extends ParserRuleContext {
		public Token n;
		public TerminalNode IS() { return getToken(SQLSelectParserParser.IS, 0); }
		public TerminalNode NULL() { return getToken(SQLSelectParserParser.NULL, 0); }
		public TerminalNode NOT() { return getToken(SQLSelectParserParser.NOT, 0); }
		public Is_null_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_is_null_clause; }
	}

	public final Is_null_clauseContext is_null_clause() throws RecognitionException {
		Is_null_clauseContext _localctx = new Is_null_clauseContext(_ctx, getState());
		enterRule(_localctx, 306, RULE_is_null_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1299);
			match(IS);
			setState(1301);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(1300);
				((Is_null_clauseContext)_localctx).n = match(NOT);
				}
			}

			setState(1303);
			match(NULL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Is_clauseContext extends ParserRuleContext {
		public TerminalNode IS() { return getToken(SQLSelectParserParser.IS, 0); }
		public Truth_valueContext truth_value() {
			return getRuleContext(Truth_valueContext.class,0);
		}
		public NotContext not() {
			return getRuleContext(NotContext.class,0);
		}
		public Is_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_is_clause; }
	}

	public final Is_clauseContext is_clause() throws RecognitionException {
		Is_clauseContext _localctx = new Is_clauseContext(_ctx, getState());
		enterRule(_localctx, 308, RULE_is_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1305);
			match(IS);
			setState(1307);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(1306);
				not();
				}
			}

			setState(1309);
			truth_value();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Truth_valueContext extends ParserRuleContext {
		public TerminalNode TRUE() { return getToken(SQLSelectParserParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(SQLSelectParserParser.FALSE, 0); }
		public TerminalNode UNKNOWN() { return getToken(SQLSelectParserParser.UNKNOWN, 0); }
		public Truth_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_truth_value; }
	}

	public final Truth_valueContext truth_value() throws RecognitionException {
		Truth_valueContext _localctx = new Truth_valueContext(_ctx, getState());
		enterRule(_localctx, 310, RULE_truth_value);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1311);
			_la = _input.LA(1);
			if ( !(_la==FALSE || _la==TRUE || _la==UNKNOWN) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NotContext extends ParserRuleContext {
		public TerminalNode NOT() { return getToken(SQLSelectParserParser.NOT, 0); }
		public NotContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_not; }
	}

	public final NotContext not() throws RecognitionException {
		NotContext _localctx = new NotContext(_ctx, getState());
		enterRule(_localctx, 312, RULE_not);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1313);
			match(NOT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Quantified_comparison_predicateContext extends ParserRuleContext {
		public Additive_expressionContext l;
		public Comp_opContext c;
		public QuantifierContext q;
		public SubqueryContext s;
		public Additive_expressionContext additive_expression() {
			return getRuleContext(Additive_expressionContext.class,0);
		}
		public Comp_opContext comp_op() {
			return getRuleContext(Comp_opContext.class,0);
		}
		public QuantifierContext quantifier() {
			return getRuleContext(QuantifierContext.class,0);
		}
		public SubqueryContext subquery() {
			return getRuleContext(SubqueryContext.class,0);
		}
		public Quantified_comparison_predicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_quantified_comparison_predicate; }
	}

	public final Quantified_comparison_predicateContext quantified_comparison_predicate() throws RecognitionException {
		Quantified_comparison_predicateContext _localctx = new Quantified_comparison_predicateContext(_ctx, getState());
		enterRule(_localctx, 314, RULE_quantified_comparison_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1315);
			((Quantified_comparison_predicateContext)_localctx).l = additive_expression();
			setState(1316);
			((Quantified_comparison_predicateContext)_localctx).c = comp_op();
			setState(1317);
			((Quantified_comparison_predicateContext)_localctx).q = quantifier();
			setState(1318);
			((Quantified_comparison_predicateContext)_localctx).s = subquery();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class QuantifierContext extends ParserRuleContext {
		public AllContext all() {
			return getRuleContext(AllContext.class,0);
		}
		public SomeContext some() {
			return getRuleContext(SomeContext.class,0);
		}
		public QuantifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_quantifier; }
	}

	public final QuantifierContext quantifier() throws RecognitionException {
		QuantifierContext _localctx = new QuantifierContext(_ctx, getState());
		enterRule(_localctx, 316, RULE_quantifier);
		try {
			setState(1322);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ALL:
				enterOuterAlt(_localctx, 1);
				{
				setState(1320);
				all();
				}
				break;
			case ANY:
			case SOME:
				enterOuterAlt(_localctx, 2);
				{
				setState(1321);
				some();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AllContext extends ParserRuleContext {
		public TerminalNode ALL() { return getToken(SQLSelectParserParser.ALL, 0); }
		public AllContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_all; }
	}

	public final AllContext all() throws RecognitionException {
		AllContext _localctx = new AllContext(_ctx, getState());
		enterRule(_localctx, 318, RULE_all);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1324);
			match(ALL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SomeContext extends ParserRuleContext {
		public TerminalNode SOME() { return getToken(SQLSelectParserParser.SOME, 0); }
		public TerminalNode ANY() { return getToken(SQLSelectParserParser.ANY, 0); }
		public SomeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_some; }
	}

	public final SomeContext some() throws RecognitionException {
		SomeContext _localctx = new SomeContext(_ctx, getState());
		enterRule(_localctx, 320, RULE_some);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1326);
			_la = _input.LA(1);
			if ( !(_la==ANY || _la==SOME) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Exists_predicateContext extends ParserRuleContext {
		public SubqueryContext s;
		public TerminalNode EXISTS() { return getToken(SQLSelectParserParser.EXISTS, 0); }
		public SubqueryContext subquery() {
			return getRuleContext(SubqueryContext.class,0);
		}
		public TerminalNode NOT() { return getToken(SQLSelectParserParser.NOT, 0); }
		public Exists_predicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exists_predicate; }
	}

	public final Exists_predicateContext exists_predicate() throws RecognitionException {
		Exists_predicateContext _localctx = new Exists_predicateContext(_ctx, getState());
		enterRule(_localctx, 322, RULE_exists_predicate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1329);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(1328);
				match(NOT);
				}
			}

			setState(1331);
			match(EXISTS);
			setState(1332);
			((Exists_predicateContext)_localctx).s = subquery();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Unique_predicateContext extends ParserRuleContext {
		public SubqueryContext s;
		public TerminalNode UNIQUE() { return getToken(SQLSelectParserParser.UNIQUE, 0); }
		public SubqueryContext subquery() {
			return getRuleContext(SubqueryContext.class,0);
		}
		public Unique_predicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unique_predicate; }
	}

	public final Unique_predicateContext unique_predicate() throws RecognitionException {
		Unique_predicateContext _localctx = new Unique_predicateContext(_ctx, getState());
		enterRule(_localctx, 324, RULE_unique_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1334);
			match(UNIQUE);
			setState(1335);
			((Unique_predicateContext)_localctx).s = subquery();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Primary_datetime_fieldContext extends ParserRuleContext {
		public Non_second_primary_datetime_fieldContext non_second_primary_datetime_field() {
			return getRuleContext(Non_second_primary_datetime_fieldContext.class,0);
		}
		public TerminalNode SECOND() { return getToken(SQLSelectParserParser.SECOND, 0); }
		public Primary_datetime_fieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primary_datetime_field; }
	}

	public final Primary_datetime_fieldContext primary_datetime_field() throws RecognitionException {
		Primary_datetime_fieldContext _localctx = new Primary_datetime_fieldContext(_ctx, getState());
		enterRule(_localctx, 326, RULE_primary_datetime_field);
		try {
			setState(1339);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DAY:
			case HOUR:
			case MINUTE:
			case MONTH:
			case YEAR:
				enterOuterAlt(_localctx, 1);
				{
				setState(1337);
				non_second_primary_datetime_field();
				}
				break;
			case SECOND:
				enterOuterAlt(_localctx, 2);
				{
				setState(1338);
				match(SECOND);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Non_second_primary_datetime_fieldContext extends ParserRuleContext {
		public TerminalNode YEAR() { return getToken(SQLSelectParserParser.YEAR, 0); }
		public TerminalNode MONTH() { return getToken(SQLSelectParserParser.MONTH, 0); }
		public TerminalNode DAY() { return getToken(SQLSelectParserParser.DAY, 0); }
		public TerminalNode HOUR() { return getToken(SQLSelectParserParser.HOUR, 0); }
		public TerminalNode MINUTE() { return getToken(SQLSelectParserParser.MINUTE, 0); }
		public Non_second_primary_datetime_fieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_non_second_primary_datetime_field; }
	}

	public final Non_second_primary_datetime_fieldContext non_second_primary_datetime_field() throws RecognitionException {
		Non_second_primary_datetime_fieldContext _localctx = new Non_second_primary_datetime_fieldContext(_ctx, getState());
		enterRule(_localctx, 328, RULE_non_second_primary_datetime_field);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1341);
			_la = _input.LA(1);
			if ( !(((((_la - 73)) & ~0x3f) == 0 && ((1L << (_la - 73)) & 1649268490241L) != 0) || _la==YEAR) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Extended_datetime_fieldContext extends ParserRuleContext {
		public TerminalNode CENTURY() { return getToken(SQLSelectParserParser.CENTURY, 0); }
		public TerminalNode DECADE() { return getToken(SQLSelectParserParser.DECADE, 0); }
		public TerminalNode DOW() { return getToken(SQLSelectParserParser.DOW, 0); }
		public TerminalNode DOY() { return getToken(SQLSelectParserParser.DOY, 0); }
		public TerminalNode EPOCH() { return getToken(SQLSelectParserParser.EPOCH, 0); }
		public TerminalNode ISODOW() { return getToken(SQLSelectParserParser.ISODOW, 0); }
		public TerminalNode ISOYEAR() { return getToken(SQLSelectParserParser.ISOYEAR, 0); }
		public TerminalNode MICROSECONDS() { return getToken(SQLSelectParserParser.MICROSECONDS, 0); }
		public TerminalNode MILLENNIUM() { return getToken(SQLSelectParserParser.MILLENNIUM, 0); }
		public TerminalNode MILLISECONDS() { return getToken(SQLSelectParserParser.MILLISECONDS, 0); }
		public TerminalNode QUARTER() { return getToken(SQLSelectParserParser.QUARTER, 0); }
		public TerminalNode WEEK() { return getToken(SQLSelectParserParser.WEEK, 0); }
		public Extended_datetime_fieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extended_datetime_field; }
	}

	public final Extended_datetime_fieldContext extended_datetime_field() throws RecognitionException {
		Extended_datetime_fieldContext _localctx = new Extended_datetime_fieldContext(_ctx, getState());
		enterRule(_localctx, 330, RULE_extended_datetime_field);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1343);
			_la = _input.LA(1);
			if ( !(((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 1153044675679053825L) != 0) || _la==WEEK) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Routine_invocationContext extends ParserRuleContext {
		public Function_nameContext function_name() {
			return getRuleContext(Function_nameContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Sql_argument_listContext sql_argument_list() {
			return getRuleContext(Sql_argument_listContext.class,0);
		}
		public Routine_invocationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_routine_invocation; }
	}

	public final Routine_invocationContext routine_invocation() throws RecognitionException {
		Routine_invocationContext _localctx = new Routine_invocationContext(_ctx, getState());
		enterRule(_localctx, 332, RULE_routine_invocation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1345);
			function_name();
			setState(1346);
			match(LEFT_PAREN);
			setState(1348);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -848827245298417264L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -144115188629504257L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -1048585L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -81066992315990017L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 2236872663039L) != 0) || ((((_la - 323)) & ~0x3f) == 0 && ((1L << (_la - 323)) & 2463L) != 0)) {
				{
				setState(1347);
				sql_argument_list();
				}
			}

			setState(1350);
			match(RIGHT_PAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Function_nameContext extends ParserRuleContext {
		public List<IdentifierContext> identifier() {
			return getRuleContexts(IdentifierContext.class);
		}
		public IdentifierContext identifier(int i) {
			return getRuleContext(IdentifierContext.class,i);
		}
		public TerminalNode DOT() { return getToken(SQLSelectParserParser.DOT, 0); }
		public Function_names_for_reserved_wordsContext function_names_for_reserved_words() {
			return getRuleContext(Function_names_for_reserved_wordsContext.class,0);
		}
		public Function_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function_name; }
	}

	public final Function_nameContext function_name() throws RecognitionException {
		Function_nameContext _localctx = new Function_nameContext(_ctx, getState());
		enterRule(_localctx, 334, RULE_function_name);
		int _la;
		try {
			setState(1358);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IGNORE:
			case NULLS:
			case NUMBER_TYPE:
			case RESPECT:
			case RETURNING:
			case UNION:
			case WITH:
			case ASC:
			case AVG:
			case BETWEEN:
			case BY:
			case CENTURY:
			case CHARACTER:
			case COLLECT:
			case COALESCE:
			case COLUMN:
			case COUNT:
			case CUBE:
			case DAY:
			case DEC:
			case DECADE:
			case DESC:
			case DOW:
			case DOY:
			case DROP:
			case EPOCH:
			case EVERY:
			case EXISTS:
			case EXTERNAL:
			case EXTRACT:
			case FILTER:
			case FIRST:
			case FORMAT:
			case FUSION:
			case GROUPING:
			case HASH:
			case INDEX:
			case INSERT:
			case INTERSECTION:
			case ISODOW:
			case ISOYEAR:
			case LAST:
			case LESS:
			case LIST:
			case LOCATION:
			case MAX:
			case MAXVALUE:
			case MICROSECONDS:
			case MILLENNIUM:
			case MILLISECONDS:
			case MIN:
			case MINUTE:
			case MONTH:
			case NATIONAL:
			case NULLIF:
			case OVER:
			case OVERWRITE:
			case PARTITION:
			case PARTITIONS:
			case PRECISION:
			case PURGE:
			case QUARTER:
			case RANGE:
			case RANK:
			case REGEXP:
			case RLIKE:
			case ROLLUP:
			case ROWS:
			case SECOND:
			case SET:
			case SIMILAR:
			case STDDEV_POP:
			case STDDEV_SAMP:
			case SUBPARTITION:
			case SUM:
			case TABLESPACE:
			case THAN:
			case TIMEZONE:
			case TIMEZONE_HOUR:
			case TIMEZONE_MINUTE:
			case TRIM:
			case TO:
			case UPDATE:
			case UNKNOWN:
			case VALUES:
			case VAR_SAMP:
			case VAR_POP:
			case VARYING:
			case WEEK:
			case YEAR:
			case ZONE:
			case ANY_VALUE:
			case CORR:
			case COVAR_POP:
			case COVAR_SAMP:
			case LISTAGG:
			case MEDIAN:
			case PERCENTILE_CONT:
			case PERCENTILE_DISC:
			case STDDEV:
			case VARIANCE_POP:
			case VARIANCE:
			case VARIANCE_SAMP:
			case CUME_DIST:
			case DENSE_RANK:
			case NTILE:
			case PERCENT_RANK:
			case WIDTH_BUCKET:
			case BITAND_AGG:
			case BITOR_AGG:
			case BITXOR_AGG:
			case HASH_AGG:
			case ARRAY_AGG:
			case OBJECT_AGG:
			case REGR_AVGX:
			case REGR_AVGY:
			case REGR_COUNT:
			case REGR_INTERCEPT:
			case REGR_R2:
			case REGR_SLOPE:
			case REGR_SXX:
			case REGR_SXY:
			case REGR_SYY:
			case APPROX_COUNT_DISTINCT:
			case HLL:
			case HLL_ACCUMULATE:
			case HLL_COMBINE:
			case HLL_EXPORT:
			case HLL_IMPORT:
			case APPROXIMATE_JACCARD_INDEX:
			case APPROXIMATE_SIMILARITY:
			case MINHASH:
			case MINHASH_COMBINE:
			case APPROX_TOP_K:
			case APPROX_TOP_K_ACCUMULATE:
			case APPROX_TOP_K_COMBINE:
			case APPROX_PERCENTILE:
			case APPROX_PERCENTILE_ACCUMULATE:
			case APPROX_PERCENTILE_COMBINE:
			case ABSTIME:
			case ANYARRAY:
			case ARRAY:
			case BOOL:
			case BIT:
			case VARBIT:
			case CIDR:
			case INET:
			case INET4:
			case INTERVAL:
			case INT1:
			case INT2:
			case INT4:
			case INT8:
			case JSON:
			case JSONB:
			case MACADDR:
			case NAME:
			case OID:
			case PG_LSN:
			case PG_NODE_TREE:
			case REGPROC:
			case XID:
			case UUID:
			case TINYINT:
			case SMALLINT:
			case INT:
			case BIGINT:
			case BIGSERIAL:
			case SMALLSERIAL:
			case SERIAL:
			case MONEY:
			case FLOAT4:
			case FLOAT8:
			case REAL:
			case FLOAT:
			case DOUBLE:
			case NUMERIC:
			case CHAR:
			case VARCHAR:
			case NCHAR:
			case NVARCHAR:
			case STRING:
			case DATE:
			case DATETIME:
			case TIME:
			case TIMETZ:
			case TIMESTAMP:
			case TIMESTAMP_LTZ:
			case TIMESTAMP_NTZ:
			case TIMESTAMP_TZ:
			case TIMESTAMPTZ:
			case TEXT:
			case BINARY:
			case VARBINARY:
			case BLOB:
			case BYTEA:
			case OBJECT:
			case STRUCT:
			case VARIANT:
			case Bracket_Identifier:
			case Identifier:
			case Double_Quoted_Numeric_Identifier:
			case Dollar_Sign_Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(1352);
				identifier();
				setState(1355);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOT) {
					{
					setState(1353);
					match(DOT);
					setState(1354);
					identifier();
					}
				}

				}
				break;
			case IN:
			case LEFT:
			case RIGHT:
				enterOuterAlt(_localctx, 2);
				{
				setState(1357);
				function_names_for_reserved_words();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Function_names_for_reserved_wordsContext extends ParserRuleContext {
		public TerminalNode LEFT() { return getToken(SQLSelectParserParser.LEFT, 0); }
		public TerminalNode RIGHT() { return getToken(SQLSelectParserParser.RIGHT, 0); }
		public TerminalNode IN() { return getToken(SQLSelectParserParser.IN, 0); }
		public Function_names_for_reserved_wordsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function_names_for_reserved_words; }
	}

	public final Function_names_for_reserved_wordsContext function_names_for_reserved_words() throws RecognitionException {
		Function_names_for_reserved_wordsContext _localctx = new Function_names_for_reserved_wordsContext(_ctx, getState());
		enterRule(_localctx, 336, RULE_function_names_for_reserved_words);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1360);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 8796634087424L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Sql_argument_listContext extends ParserRuleContext {
		public List<Value_expressionContext> value_expression() {
			return getRuleContexts(Value_expressionContext.class);
		}
		public Value_expressionContext value_expression(int i) {
			return getRuleContext(Value_expressionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SQLSelectParserParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SQLSelectParserParser.COMMA, i);
		}
		public Sql_argument_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sql_argument_list; }
	}

	public final Sql_argument_listContext sql_argument_list() throws RecognitionException {
		Sql_argument_listContext _localctx = new Sql_argument_listContext(_ctx, getState());
		enterRule(_localctx, 338, RULE_sql_argument_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1362);
			value_expression();
			setState(1367);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1363);
				match(COMMA);
				setState(1364);
				value_expression();
				}
				}
				setState(1369);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IdentifierContext extends ParserRuleContext {
		public Simple_identifierContext simple_identifier() {
			return getRuleContext(Simple_identifierContext.class,0);
		}
		public Logical_identifierContext logical_identifier() {
			return getRuleContext(Logical_identifierContext.class,0);
		}
		public Nonreserved_keywordsContext nonreserved_keywords() {
			return getRuleContext(Nonreserved_keywordsContext.class,0);
		}
		public Snowflake_quoted_numeric_identifierContext snowflake_quoted_numeric_identifier() {
			return getRuleContext(Snowflake_quoted_numeric_identifierContext.class,0);
		}
		public Snowflake_dollar_function_identifierContext snowflake_dollar_function_identifier() {
			return getRuleContext(Snowflake_dollar_function_identifierContext.class,0);
		}
		public IdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifier; }
	}

	public final IdentifierContext identifier() throws RecognitionException {
		IdentifierContext _localctx = new IdentifierContext(_ctx, getState());
		enterRule(_localctx, 340, RULE_identifier);
		try {
			setState(1375);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(1370);
				simple_identifier();
				}
				break;
			case Bracket_Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(1371);
				logical_identifier();
				}
				break;
			case IGNORE:
			case NULLS:
			case NUMBER_TYPE:
			case RESPECT:
			case RETURNING:
			case UNION:
			case WITH:
			case ASC:
			case AVG:
			case BETWEEN:
			case BY:
			case CENTURY:
			case CHARACTER:
			case COLLECT:
			case COALESCE:
			case COLUMN:
			case COUNT:
			case CUBE:
			case DAY:
			case DEC:
			case DECADE:
			case DESC:
			case DOW:
			case DOY:
			case DROP:
			case EPOCH:
			case EVERY:
			case EXISTS:
			case EXTERNAL:
			case EXTRACT:
			case FILTER:
			case FIRST:
			case FORMAT:
			case FUSION:
			case GROUPING:
			case HASH:
			case INDEX:
			case INSERT:
			case INTERSECTION:
			case ISODOW:
			case ISOYEAR:
			case LAST:
			case LESS:
			case LIST:
			case LOCATION:
			case MAX:
			case MAXVALUE:
			case MICROSECONDS:
			case MILLENNIUM:
			case MILLISECONDS:
			case MIN:
			case MINUTE:
			case MONTH:
			case NATIONAL:
			case NULLIF:
			case OVER:
			case OVERWRITE:
			case PARTITION:
			case PARTITIONS:
			case PRECISION:
			case PURGE:
			case QUARTER:
			case RANGE:
			case RANK:
			case REGEXP:
			case RLIKE:
			case ROLLUP:
			case ROWS:
			case SECOND:
			case SET:
			case SIMILAR:
			case STDDEV_POP:
			case STDDEV_SAMP:
			case SUBPARTITION:
			case SUM:
			case TABLESPACE:
			case THAN:
			case TIMEZONE:
			case TIMEZONE_HOUR:
			case TIMEZONE_MINUTE:
			case TRIM:
			case TO:
			case UPDATE:
			case UNKNOWN:
			case VALUES:
			case VAR_SAMP:
			case VAR_POP:
			case VARYING:
			case WEEK:
			case YEAR:
			case ZONE:
			case ANY_VALUE:
			case CORR:
			case COVAR_POP:
			case COVAR_SAMP:
			case LISTAGG:
			case MEDIAN:
			case PERCENTILE_CONT:
			case PERCENTILE_DISC:
			case STDDEV:
			case VARIANCE_POP:
			case VARIANCE:
			case VARIANCE_SAMP:
			case CUME_DIST:
			case DENSE_RANK:
			case NTILE:
			case PERCENT_RANK:
			case WIDTH_BUCKET:
			case BITAND_AGG:
			case BITOR_AGG:
			case BITXOR_AGG:
			case HASH_AGG:
			case ARRAY_AGG:
			case OBJECT_AGG:
			case REGR_AVGX:
			case REGR_AVGY:
			case REGR_COUNT:
			case REGR_INTERCEPT:
			case REGR_R2:
			case REGR_SLOPE:
			case REGR_SXX:
			case REGR_SXY:
			case REGR_SYY:
			case APPROX_COUNT_DISTINCT:
			case HLL:
			case HLL_ACCUMULATE:
			case HLL_COMBINE:
			case HLL_EXPORT:
			case HLL_IMPORT:
			case APPROXIMATE_JACCARD_INDEX:
			case APPROXIMATE_SIMILARITY:
			case MINHASH:
			case MINHASH_COMBINE:
			case APPROX_TOP_K:
			case APPROX_TOP_K_ACCUMULATE:
			case APPROX_TOP_K_COMBINE:
			case APPROX_PERCENTILE:
			case APPROX_PERCENTILE_ACCUMULATE:
			case APPROX_PERCENTILE_COMBINE:
			case ABSTIME:
			case ANYARRAY:
			case ARRAY:
			case BOOL:
			case BIT:
			case VARBIT:
			case CIDR:
			case INET:
			case INET4:
			case INTERVAL:
			case INT1:
			case INT2:
			case INT4:
			case INT8:
			case JSON:
			case JSONB:
			case MACADDR:
			case NAME:
			case OID:
			case PG_LSN:
			case PG_NODE_TREE:
			case REGPROC:
			case XID:
			case UUID:
			case TINYINT:
			case SMALLINT:
			case INT:
			case BIGINT:
			case BIGSERIAL:
			case SMALLSERIAL:
			case SERIAL:
			case MONEY:
			case FLOAT4:
			case FLOAT8:
			case REAL:
			case FLOAT:
			case DOUBLE:
			case NUMERIC:
			case CHAR:
			case VARCHAR:
			case NCHAR:
			case NVARCHAR:
			case STRING:
			case DATE:
			case DATETIME:
			case TIME:
			case TIMETZ:
			case TIMESTAMP:
			case TIMESTAMP_LTZ:
			case TIMESTAMP_NTZ:
			case TIMESTAMP_TZ:
			case TIMESTAMPTZ:
			case TEXT:
			case BINARY:
			case VARBINARY:
			case BLOB:
			case BYTEA:
			case OBJECT:
			case STRUCT:
			case VARIANT:
				enterOuterAlt(_localctx, 3);
				{
				setState(1372);
				nonreserved_keywords();
				}
				break;
			case Double_Quoted_Numeric_Identifier:
				enterOuterAlt(_localctx, 4);
				{
				setState(1373);
				snowflake_quoted_numeric_identifier();
				}
				break;
			case Dollar_Sign_Identifier:
				enterOuterAlt(_localctx, 5);
				{
				setState(1374);
				snowflake_dollar_function_identifier();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Alias_identifierContext extends ParserRuleContext {
		public Simple_identifierContext simple_identifier() {
			return getRuleContext(Simple_identifierContext.class,0);
		}
		public Logical_identifierContext logical_identifier() {
			return getRuleContext(Logical_identifierContext.class,0);
		}
		public Nonreserved_keywordsContext nonreserved_keywords() {
			return getRuleContext(Nonreserved_keywordsContext.class,0);
		}
		public Simple_numeric_identifierContext simple_numeric_identifier() {
			return getRuleContext(Simple_numeric_identifierContext.class,0);
		}
		public Snowflake_quoted_numeric_identifierContext snowflake_quoted_numeric_identifier() {
			return getRuleContext(Snowflake_quoted_numeric_identifierContext.class,0);
		}
		public Alias_identifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alias_identifier; }
	}

	public final Alias_identifierContext alias_identifier() throws RecognitionException {
		Alias_identifierContext _localctx = new Alias_identifierContext(_ctx, getState());
		enterRule(_localctx, 342, RULE_alias_identifier);
		try {
			setState(1382);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(1377);
				simple_identifier();
				}
				break;
			case Bracket_Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(1378);
				logical_identifier();
				}
				break;
			case IGNORE:
			case NULLS:
			case NUMBER_TYPE:
			case RESPECT:
			case RETURNING:
			case UNION:
			case WITH:
			case ASC:
			case AVG:
			case BETWEEN:
			case BY:
			case CENTURY:
			case CHARACTER:
			case COLLECT:
			case COALESCE:
			case COLUMN:
			case COUNT:
			case CUBE:
			case DAY:
			case DEC:
			case DECADE:
			case DESC:
			case DOW:
			case DOY:
			case DROP:
			case EPOCH:
			case EVERY:
			case EXISTS:
			case EXTERNAL:
			case EXTRACT:
			case FILTER:
			case FIRST:
			case FORMAT:
			case FUSION:
			case GROUPING:
			case HASH:
			case INDEX:
			case INSERT:
			case INTERSECTION:
			case ISODOW:
			case ISOYEAR:
			case LAST:
			case LESS:
			case LIST:
			case LOCATION:
			case MAX:
			case MAXVALUE:
			case MICROSECONDS:
			case MILLENNIUM:
			case MILLISECONDS:
			case MIN:
			case MINUTE:
			case MONTH:
			case NATIONAL:
			case NULLIF:
			case OVER:
			case OVERWRITE:
			case PARTITION:
			case PARTITIONS:
			case PRECISION:
			case PURGE:
			case QUARTER:
			case RANGE:
			case RANK:
			case REGEXP:
			case RLIKE:
			case ROLLUP:
			case ROWS:
			case SECOND:
			case SET:
			case SIMILAR:
			case STDDEV_POP:
			case STDDEV_SAMP:
			case SUBPARTITION:
			case SUM:
			case TABLESPACE:
			case THAN:
			case TIMEZONE:
			case TIMEZONE_HOUR:
			case TIMEZONE_MINUTE:
			case TRIM:
			case TO:
			case UPDATE:
			case UNKNOWN:
			case VALUES:
			case VAR_SAMP:
			case VAR_POP:
			case VARYING:
			case WEEK:
			case YEAR:
			case ZONE:
			case ANY_VALUE:
			case CORR:
			case COVAR_POP:
			case COVAR_SAMP:
			case LISTAGG:
			case MEDIAN:
			case PERCENTILE_CONT:
			case PERCENTILE_DISC:
			case STDDEV:
			case VARIANCE_POP:
			case VARIANCE:
			case VARIANCE_SAMP:
			case CUME_DIST:
			case DENSE_RANK:
			case NTILE:
			case PERCENT_RANK:
			case WIDTH_BUCKET:
			case BITAND_AGG:
			case BITOR_AGG:
			case BITXOR_AGG:
			case HASH_AGG:
			case ARRAY_AGG:
			case OBJECT_AGG:
			case REGR_AVGX:
			case REGR_AVGY:
			case REGR_COUNT:
			case REGR_INTERCEPT:
			case REGR_R2:
			case REGR_SLOPE:
			case REGR_SXX:
			case REGR_SXY:
			case REGR_SYY:
			case APPROX_COUNT_DISTINCT:
			case HLL:
			case HLL_ACCUMULATE:
			case HLL_COMBINE:
			case HLL_EXPORT:
			case HLL_IMPORT:
			case APPROXIMATE_JACCARD_INDEX:
			case APPROXIMATE_SIMILARITY:
			case MINHASH:
			case MINHASH_COMBINE:
			case APPROX_TOP_K:
			case APPROX_TOP_K_ACCUMULATE:
			case APPROX_TOP_K_COMBINE:
			case APPROX_PERCENTILE:
			case APPROX_PERCENTILE_ACCUMULATE:
			case APPROX_PERCENTILE_COMBINE:
			case ABSTIME:
			case ANYARRAY:
			case ARRAY:
			case BOOL:
			case BIT:
			case VARBIT:
			case CIDR:
			case INET:
			case INET4:
			case INTERVAL:
			case INT1:
			case INT2:
			case INT4:
			case INT8:
			case JSON:
			case JSONB:
			case MACADDR:
			case NAME:
			case OID:
			case PG_LSN:
			case PG_NODE_TREE:
			case REGPROC:
			case XID:
			case UUID:
			case TINYINT:
			case SMALLINT:
			case INT:
			case BIGINT:
			case BIGSERIAL:
			case SMALLSERIAL:
			case SERIAL:
			case MONEY:
			case FLOAT4:
			case FLOAT8:
			case REAL:
			case FLOAT:
			case DOUBLE:
			case NUMERIC:
			case CHAR:
			case VARCHAR:
			case NCHAR:
			case NVARCHAR:
			case STRING:
			case DATE:
			case DATETIME:
			case TIME:
			case TIMETZ:
			case TIMESTAMP:
			case TIMESTAMP_LTZ:
			case TIMESTAMP_NTZ:
			case TIMESTAMP_TZ:
			case TIMESTAMPTZ:
			case TEXT:
			case BINARY:
			case VARBINARY:
			case BLOB:
			case BYTEA:
			case OBJECT:
			case STRUCT:
			case VARIANT:
				enterOuterAlt(_localctx, 3);
				{
				setState(1379);
				nonreserved_keywords();
				}
				break;
			case NUMBER:
			case Numeric_Identifier:
				enterOuterAlt(_localctx, 4);
				{
				setState(1380);
				simple_numeric_identifier();
				}
				break;
			case Double_Quoted_Numeric_Identifier:
				enterOuterAlt(_localctx, 5);
				{
				setState(1381);
				snowflake_quoted_numeric_identifier();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Variable_identifierContext extends ParserRuleContext {
		public Simple_variable_identifierContext simple_variable_identifier() {
			return getRuleContext(Simple_variable_identifierContext.class,0);
		}
		public Extended_variable_identifierContext extended_variable_identifier() {
			return getRuleContext(Extended_variable_identifierContext.class,0);
		}
		public Variable_identifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable_identifier; }
	}

	public final Variable_identifierContext variable_identifier() throws RecognitionException {
		Variable_identifierContext _localctx = new Variable_identifierContext(_ctx, getState());
		enterRule(_localctx, 344, RULE_variable_identifier);
		try {
			setState(1386);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Variable_Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(1384);
				simple_variable_identifier();
				}
				break;
			case Extended_Variable_Identifier:
			case Mixed_Variable_Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(1385);
				extended_variable_identifier();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Simple_identifierContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(SQLSelectParserParser.Identifier, 0); }
		public Simple_identifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simple_identifier; }
	}

	public final Simple_identifierContext simple_identifier() throws RecognitionException {
		Simple_identifierContext _localctx = new Simple_identifierContext(_ctx, getState());
		enterRule(_localctx, 346, RULE_simple_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1388);
			match(Identifier);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Logical_identifierContext extends ParserRuleContext {
		public TerminalNode Bracket_Identifier() { return getToken(SQLSelectParserParser.Bracket_Identifier, 0); }
		public Logical_identifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logical_identifier; }
	}

	public final Logical_identifierContext logical_identifier() throws RecognitionException {
		Logical_identifierContext _localctx = new Logical_identifierContext(_ctx, getState());
		enterRule(_localctx, 348, RULE_logical_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1390);
			match(Bracket_Identifier);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Simple_variable_identifierContext extends ParserRuleContext {
		public TerminalNode Variable_Identifier() { return getToken(SQLSelectParserParser.Variable_Identifier, 0); }
		public Simple_variable_identifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simple_variable_identifier; }
	}

	public final Simple_variable_identifierContext simple_variable_identifier() throws RecognitionException {
		Simple_variable_identifierContext _localctx = new Simple_variable_identifierContext(_ctx, getState());
		enterRule(_localctx, 350, RULE_simple_variable_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1392);
			match(Variable_Identifier);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Extended_variable_identifierContext extends ParserRuleContext {
		public TerminalNode Extended_Variable_Identifier() { return getToken(SQLSelectParserParser.Extended_Variable_Identifier, 0); }
		public TerminalNode Mixed_Variable_Identifier() { return getToken(SQLSelectParserParser.Mixed_Variable_Identifier, 0); }
		public Extended_variable_identifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extended_variable_identifier; }
	}

	public final Extended_variable_identifierContext extended_variable_identifier() throws RecognitionException {
		Extended_variable_identifierContext _localctx = new Extended_variable_identifierContext(_ctx, getState());
		enterRule(_localctx, 352, RULE_extended_variable_identifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1394);
			_la = _input.LA(1);
			if ( !(_la==Extended_Variable_Identifier || _la==Mixed_Variable_Identifier) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Simple_numeric_identifierContext extends ParserRuleContext {
		public TerminalNode Numeric_Identifier() { return getToken(SQLSelectParserParser.Numeric_Identifier, 0); }
		public TerminalNode NUMBER() { return getToken(SQLSelectParserParser.NUMBER, 0); }
		public Simple_numeric_identifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simple_numeric_identifier; }
	}

	public final Simple_numeric_identifierContext simple_numeric_identifier() throws RecognitionException {
		Simple_numeric_identifierContext _localctx = new Simple_numeric_identifierContext(_ctx, getState());
		enterRule(_localctx, 354, RULE_simple_numeric_identifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1396);
			_la = _input.LA(1);
			if ( !(_la==NUMBER || _la==Numeric_Identifier) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Snowflake_quoted_numeric_identifierContext extends ParserRuleContext {
		public TerminalNode Double_Quoted_Numeric_Identifier() { return getToken(SQLSelectParserParser.Double_Quoted_Numeric_Identifier, 0); }
		public Snowflake_quoted_numeric_identifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_snowflake_quoted_numeric_identifier; }
	}

	public final Snowflake_quoted_numeric_identifierContext snowflake_quoted_numeric_identifier() throws RecognitionException {
		Snowflake_quoted_numeric_identifierContext _localctx = new Snowflake_quoted_numeric_identifierContext(_ctx, getState());
		enterRule(_localctx, 356, RULE_snowflake_quoted_numeric_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1398);
			match(Double_Quoted_Numeric_Identifier);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Snowflake_dollar_function_identifierContext extends ParserRuleContext {
		public TerminalNode Dollar_Sign_Identifier() { return getToken(SQLSelectParserParser.Dollar_Sign_Identifier, 0); }
		public Snowflake_dollar_function_identifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_snowflake_dollar_function_identifier; }
	}

	public final Snowflake_dollar_function_identifierContext snowflake_dollar_function_identifier() throws RecognitionException {
		Snowflake_dollar_function_identifierContext _localctx = new Snowflake_dollar_function_identifierContext(_ctx, getState());
		enterRule(_localctx, 358, RULE_snowflake_dollar_function_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1400);
			match(Dollar_Sign_Identifier);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Nonreserved_keywordsContext extends ParserRuleContext {
		public TerminalNode AVG() { return getToken(SQLSelectParserParser.AVG, 0); }
		public TerminalNode ABSTIME() { return getToken(SQLSelectParserParser.ABSTIME, 0); }
		public TerminalNode ANYARRAY() { return getToken(SQLSelectParserParser.ANYARRAY, 0); }
		public TerminalNode ARRAY() { return getToken(SQLSelectParserParser.ARRAY, 0); }
		public TerminalNode ASC() { return getToken(SQLSelectParserParser.ASC, 0); }
		public TerminalNode BETWEEN() { return getToken(SQLSelectParserParser.BETWEEN, 0); }
		public TerminalNode BIGINT() { return getToken(SQLSelectParserParser.BIGINT, 0); }
		public TerminalNode BIGSERIAL() { return getToken(SQLSelectParserParser.BIGSERIAL, 0); }
		public TerminalNode BINARY() { return getToken(SQLSelectParserParser.BINARY, 0); }
		public TerminalNode BIT() { return getToken(SQLSelectParserParser.BIT, 0); }
		public TerminalNode BLOB() { return getToken(SQLSelectParserParser.BLOB, 0); }
		public TerminalNode BOOL() { return getToken(SQLSelectParserParser.BOOL, 0); }
		public TerminalNode BY() { return getToken(SQLSelectParserParser.BY, 0); }
		public TerminalNode BYTEA() { return getToken(SQLSelectParserParser.BYTEA, 0); }
		public TerminalNode CENTURY() { return getToken(SQLSelectParserParser.CENTURY, 0); }
		public TerminalNode CHAR() { return getToken(SQLSelectParserParser.CHAR, 0); }
		public TerminalNode CHARACTER() { return getToken(SQLSelectParserParser.CHARACTER, 0); }
		public TerminalNode CIDR() { return getToken(SQLSelectParserParser.CIDR, 0); }
		public TerminalNode COALESCE() { return getToken(SQLSelectParserParser.COALESCE, 0); }
		public TerminalNode COLLECT() { return getToken(SQLSelectParserParser.COLLECT, 0); }
		public TerminalNode COLUMN() { return getToken(SQLSelectParserParser.COLUMN, 0); }
		public TerminalNode COUNT() { return getToken(SQLSelectParserParser.COUNT, 0); }
		public TerminalNode CUBE() { return getToken(SQLSelectParserParser.CUBE, 0); }
		public TerminalNode DATE() { return getToken(SQLSelectParserParser.DATE, 0); }
		public TerminalNode DATETIME() { return getToken(SQLSelectParserParser.DATETIME, 0); }
		public TerminalNode DAY() { return getToken(SQLSelectParserParser.DAY, 0); }
		public TerminalNode DEC() { return getToken(SQLSelectParserParser.DEC, 0); }
		public TerminalNode DECADE() { return getToken(SQLSelectParserParser.DECADE, 0); }
		public TerminalNode DESC() { return getToken(SQLSelectParserParser.DESC, 0); }
		public TerminalNode DOUBLE() { return getToken(SQLSelectParserParser.DOUBLE, 0); }
		public TerminalNode DOW() { return getToken(SQLSelectParserParser.DOW, 0); }
		public TerminalNode DOY() { return getToken(SQLSelectParserParser.DOY, 0); }
		public TerminalNode DROP() { return getToken(SQLSelectParserParser.DROP, 0); }
		public TerminalNode EPOCH() { return getToken(SQLSelectParserParser.EPOCH, 0); }
		public TerminalNode EVERY() { return getToken(SQLSelectParserParser.EVERY, 0); }
		public TerminalNode EXISTS() { return getToken(SQLSelectParserParser.EXISTS, 0); }
		public TerminalNode EXTERNAL() { return getToken(SQLSelectParserParser.EXTERNAL, 0); }
		public TerminalNode EXTRACT() { return getToken(SQLSelectParserParser.EXTRACT, 0); }
		public TerminalNode FILTER() { return getToken(SQLSelectParserParser.FILTER, 0); }
		public TerminalNode FIRST() { return getToken(SQLSelectParserParser.FIRST, 0); }
		public TerminalNode FLOAT() { return getToken(SQLSelectParserParser.FLOAT, 0); }
		public TerminalNode FLOAT4() { return getToken(SQLSelectParserParser.FLOAT4, 0); }
		public TerminalNode FLOAT8() { return getToken(SQLSelectParserParser.FLOAT8, 0); }
		public TerminalNode FORMAT() { return getToken(SQLSelectParserParser.FORMAT, 0); }
		public TerminalNode FUSION() { return getToken(SQLSelectParserParser.FUSION, 0); }
		public TerminalNode GROUPING() { return getToken(SQLSelectParserParser.GROUPING, 0); }
		public TerminalNode HASH() { return getToken(SQLSelectParserParser.HASH, 0); }
		public TerminalNode INDEX() { return getToken(SQLSelectParserParser.INDEX, 0); }
		public TerminalNode INET() { return getToken(SQLSelectParserParser.INET, 0); }
		public TerminalNode INET4() { return getToken(SQLSelectParserParser.INET4, 0); }
		public TerminalNode INSERT() { return getToken(SQLSelectParserParser.INSERT, 0); }
		public TerminalNode INT() { return getToken(SQLSelectParserParser.INT, 0); }
		public TerminalNode INT1() { return getToken(SQLSelectParserParser.INT1, 0); }
		public TerminalNode INT2() { return getToken(SQLSelectParserParser.INT2, 0); }
		public TerminalNode INT4() { return getToken(SQLSelectParserParser.INT4, 0); }
		public TerminalNode INT8() { return getToken(SQLSelectParserParser.INT8, 0); }
		public TerminalNode INTERSECTION() { return getToken(SQLSelectParserParser.INTERSECTION, 0); }
		public TerminalNode INTERVAL() { return getToken(SQLSelectParserParser.INTERVAL, 0); }
		public TerminalNode ISODOW() { return getToken(SQLSelectParserParser.ISODOW, 0); }
		public TerminalNode ISOYEAR() { return getToken(SQLSelectParserParser.ISOYEAR, 0); }
		public TerminalNode JSON() { return getToken(SQLSelectParserParser.JSON, 0); }
		public TerminalNode JSONB() { return getToken(SQLSelectParserParser.JSONB, 0); }
		public TerminalNode LAST() { return getToken(SQLSelectParserParser.LAST, 0); }
		public TerminalNode LESS() { return getToken(SQLSelectParserParser.LESS, 0); }
		public TerminalNode LIST() { return getToken(SQLSelectParserParser.LIST, 0); }
		public TerminalNode LOCATION() { return getToken(SQLSelectParserParser.LOCATION, 0); }
		public TerminalNode MACADDR() { return getToken(SQLSelectParserParser.MACADDR, 0); }
		public TerminalNode MAX() { return getToken(SQLSelectParserParser.MAX, 0); }
		public TerminalNode MAXVALUE() { return getToken(SQLSelectParserParser.MAXVALUE, 0); }
		public TerminalNode MICROSECONDS() { return getToken(SQLSelectParserParser.MICROSECONDS, 0); }
		public TerminalNode MILLENNIUM() { return getToken(SQLSelectParserParser.MILLENNIUM, 0); }
		public TerminalNode MILLISECONDS() { return getToken(SQLSelectParserParser.MILLISECONDS, 0); }
		public TerminalNode MIN() { return getToken(SQLSelectParserParser.MIN, 0); }
		public TerminalNode MINUTE() { return getToken(SQLSelectParserParser.MINUTE, 0); }
		public TerminalNode MONEY() { return getToken(SQLSelectParserParser.MONEY, 0); }
		public TerminalNode MONTH() { return getToken(SQLSelectParserParser.MONTH, 0); }
		public TerminalNode NAME() { return getToken(SQLSelectParserParser.NAME, 0); }
		public TerminalNode NATIONAL() { return getToken(SQLSelectParserParser.NATIONAL, 0); }
		public TerminalNode NCHAR() { return getToken(SQLSelectParserParser.NCHAR, 0); }
		public TerminalNode NULLIF() { return getToken(SQLSelectParserParser.NULLIF, 0); }
		public TerminalNode NUMBER_TYPE() { return getToken(SQLSelectParserParser.NUMBER_TYPE, 0); }
		public TerminalNode NUMERIC() { return getToken(SQLSelectParserParser.NUMERIC, 0); }
		public TerminalNode NVARCHAR() { return getToken(SQLSelectParserParser.NVARCHAR, 0); }
		public TerminalNode OBJECT() { return getToken(SQLSelectParserParser.OBJECT, 0); }
		public TerminalNode OID() { return getToken(SQLSelectParserParser.OID, 0); }
		public TerminalNode OVER() { return getToken(SQLSelectParserParser.OVER, 0); }
		public TerminalNode OVERWRITE() { return getToken(SQLSelectParserParser.OVERWRITE, 0); }
		public TerminalNode PARTITION() { return getToken(SQLSelectParserParser.PARTITION, 0); }
		public TerminalNode PARTITIONS() { return getToken(SQLSelectParserParser.PARTITIONS, 0); }
		public TerminalNode PG_LSN() { return getToken(SQLSelectParserParser.PG_LSN, 0); }
		public TerminalNode PG_NODE_TREE() { return getToken(SQLSelectParserParser.PG_NODE_TREE, 0); }
		public TerminalNode PRECISION() { return getToken(SQLSelectParserParser.PRECISION, 0); }
		public TerminalNode PURGE() { return getToken(SQLSelectParserParser.PURGE, 0); }
		public TerminalNode QUARTER() { return getToken(SQLSelectParserParser.QUARTER, 0); }
		public TerminalNode RANGE() { return getToken(SQLSelectParserParser.RANGE, 0); }
		public TerminalNode RANK() { return getToken(SQLSelectParserParser.RANK, 0); }
		public TerminalNode REAL() { return getToken(SQLSelectParserParser.REAL, 0); }
		public TerminalNode REGEXP() { return getToken(SQLSelectParserParser.REGEXP, 0); }
		public TerminalNode REGPROC() { return getToken(SQLSelectParserParser.REGPROC, 0); }
		public TerminalNode RETURNING() { return getToken(SQLSelectParserParser.RETURNING, 0); }
		public TerminalNode RLIKE() { return getToken(SQLSelectParserParser.RLIKE, 0); }
		public TerminalNode ROLLUP() { return getToken(SQLSelectParserParser.ROLLUP, 0); }
		public TerminalNode ROWS() { return getToken(SQLSelectParserParser.ROWS, 0); }
		public TerminalNode SECOND() { return getToken(SQLSelectParserParser.SECOND, 0); }
		public TerminalNode SERIAL() { return getToken(SQLSelectParserParser.SERIAL, 0); }
		public TerminalNode SET() { return getToken(SQLSelectParserParser.SET, 0); }
		public TerminalNode SIMILAR() { return getToken(SQLSelectParserParser.SIMILAR, 0); }
		public TerminalNode SMALLINT() { return getToken(SQLSelectParserParser.SMALLINT, 0); }
		public TerminalNode SMALLSERIAL() { return getToken(SQLSelectParserParser.SMALLSERIAL, 0); }
		public TerminalNode STDDEV_POP() { return getToken(SQLSelectParserParser.STDDEV_POP, 0); }
		public TerminalNode STDDEV_SAMP() { return getToken(SQLSelectParserParser.STDDEV_SAMP, 0); }
		public TerminalNode STRING() { return getToken(SQLSelectParserParser.STRING, 0); }
		public TerminalNode STRUCT() { return getToken(SQLSelectParserParser.STRUCT, 0); }
		public TerminalNode SUBPARTITION() { return getToken(SQLSelectParserParser.SUBPARTITION, 0); }
		public TerminalNode SUM() { return getToken(SQLSelectParserParser.SUM, 0); }
		public TerminalNode TABLESPACE() { return getToken(SQLSelectParserParser.TABLESPACE, 0); }
		public TerminalNode TEXT() { return getToken(SQLSelectParserParser.TEXT, 0); }
		public TerminalNode THAN() { return getToken(SQLSelectParserParser.THAN, 0); }
		public TerminalNode TIME() { return getToken(SQLSelectParserParser.TIME, 0); }
		public TerminalNode TIMESTAMP() { return getToken(SQLSelectParserParser.TIMESTAMP, 0); }
		public TerminalNode TIMESTAMP_LTZ() { return getToken(SQLSelectParserParser.TIMESTAMP_LTZ, 0); }
		public TerminalNode TIMESTAMP_NTZ() { return getToken(SQLSelectParserParser.TIMESTAMP_NTZ, 0); }
		public TerminalNode TIMESTAMP_TZ() { return getToken(SQLSelectParserParser.TIMESTAMP_TZ, 0); }
		public TerminalNode TIMESTAMPTZ() { return getToken(SQLSelectParserParser.TIMESTAMPTZ, 0); }
		public TerminalNode TIMETZ() { return getToken(SQLSelectParserParser.TIMETZ, 0); }
		public TerminalNode TIMEZONE() { return getToken(SQLSelectParserParser.TIMEZONE, 0); }
		public TerminalNode TIMEZONE_HOUR() { return getToken(SQLSelectParserParser.TIMEZONE_HOUR, 0); }
		public TerminalNode TIMEZONE_MINUTE() { return getToken(SQLSelectParserParser.TIMEZONE_MINUTE, 0); }
		public TerminalNode TINYINT() { return getToken(SQLSelectParserParser.TINYINT, 0); }
		public TerminalNode TO() { return getToken(SQLSelectParserParser.TO, 0); }
		public TerminalNode TRIM() { return getToken(SQLSelectParserParser.TRIM, 0); }
		public TerminalNode UNION() { return getToken(SQLSelectParserParser.UNION, 0); }
		public TerminalNode UNKNOWN() { return getToken(SQLSelectParserParser.UNKNOWN, 0); }
		public TerminalNode UPDATE() { return getToken(SQLSelectParserParser.UPDATE, 0); }
		public TerminalNode UUID() { return getToken(SQLSelectParserParser.UUID, 0); }
		public TerminalNode VALUES() { return getToken(SQLSelectParserParser.VALUES, 0); }
		public TerminalNode VAR_POP() { return getToken(SQLSelectParserParser.VAR_POP, 0); }
		public TerminalNode VAR_SAMP() { return getToken(SQLSelectParserParser.VAR_SAMP, 0); }
		public TerminalNode VARBINARY() { return getToken(SQLSelectParserParser.VARBINARY, 0); }
		public TerminalNode VARBIT() { return getToken(SQLSelectParserParser.VARBIT, 0); }
		public TerminalNode VARCHAR() { return getToken(SQLSelectParserParser.VARCHAR, 0); }
		public TerminalNode VARIANT() { return getToken(SQLSelectParserParser.VARIANT, 0); }
		public TerminalNode VARYING() { return getToken(SQLSelectParserParser.VARYING, 0); }
		public TerminalNode WEEK() { return getToken(SQLSelectParserParser.WEEK, 0); }
		public TerminalNode WITH() { return getToken(SQLSelectParserParser.WITH, 0); }
		public TerminalNode XID() { return getToken(SQLSelectParserParser.XID, 0); }
		public TerminalNode YEAR() { return getToken(SQLSelectParserParser.YEAR, 0); }
		public TerminalNode ZONE() { return getToken(SQLSelectParserParser.ZONE, 0); }
		public TerminalNode ANY_VALUE() { return getToken(SQLSelectParserParser.ANY_VALUE, 0); }
		public TerminalNode CORR() { return getToken(SQLSelectParserParser.CORR, 0); }
		public TerminalNode COVAR_POP() { return getToken(SQLSelectParserParser.COVAR_POP, 0); }
		public TerminalNode COVAR_SAMP() { return getToken(SQLSelectParserParser.COVAR_SAMP, 0); }
		public TerminalNode LISTAGG() { return getToken(SQLSelectParserParser.LISTAGG, 0); }
		public TerminalNode MEDIAN() { return getToken(SQLSelectParserParser.MEDIAN, 0); }
		public TerminalNode PERCENTILE_CONT() { return getToken(SQLSelectParserParser.PERCENTILE_CONT, 0); }
		public TerminalNode PERCENTILE_DISC() { return getToken(SQLSelectParserParser.PERCENTILE_DISC, 0); }
		public TerminalNode STDDEV() { return getToken(SQLSelectParserParser.STDDEV, 0); }
		public TerminalNode VARIANCE_POP() { return getToken(SQLSelectParserParser.VARIANCE_POP, 0); }
		public TerminalNode VARIANCE() { return getToken(SQLSelectParserParser.VARIANCE, 0); }
		public TerminalNode VARIANCE_SAMP() { return getToken(SQLSelectParserParser.VARIANCE_SAMP, 0); }
		public TerminalNode CUME_DIST() { return getToken(SQLSelectParserParser.CUME_DIST, 0); }
		public TerminalNode DENSE_RANK() { return getToken(SQLSelectParserParser.DENSE_RANK, 0); }
		public TerminalNode NTILE() { return getToken(SQLSelectParserParser.NTILE, 0); }
		public TerminalNode PERCENT_RANK() { return getToken(SQLSelectParserParser.PERCENT_RANK, 0); }
		public TerminalNode WIDTH_BUCKET() { return getToken(SQLSelectParserParser.WIDTH_BUCKET, 0); }
		public TerminalNode BITAND_AGG() { return getToken(SQLSelectParserParser.BITAND_AGG, 0); }
		public TerminalNode BITOR_AGG() { return getToken(SQLSelectParserParser.BITOR_AGG, 0); }
		public TerminalNode BITXOR_AGG() { return getToken(SQLSelectParserParser.BITXOR_AGG, 0); }
		public TerminalNode HASH_AGG() { return getToken(SQLSelectParserParser.HASH_AGG, 0); }
		public TerminalNode ARRAY_AGG() { return getToken(SQLSelectParserParser.ARRAY_AGG, 0); }
		public TerminalNode OBJECT_AGG() { return getToken(SQLSelectParserParser.OBJECT_AGG, 0); }
		public TerminalNode REGR_AVGX() { return getToken(SQLSelectParserParser.REGR_AVGX, 0); }
		public TerminalNode REGR_AVGY() { return getToken(SQLSelectParserParser.REGR_AVGY, 0); }
		public TerminalNode REGR_COUNT() { return getToken(SQLSelectParserParser.REGR_COUNT, 0); }
		public TerminalNode REGR_INTERCEPT() { return getToken(SQLSelectParserParser.REGR_INTERCEPT, 0); }
		public TerminalNode REGR_R2() { return getToken(SQLSelectParserParser.REGR_R2, 0); }
		public TerminalNode REGR_SLOPE() { return getToken(SQLSelectParserParser.REGR_SLOPE, 0); }
		public TerminalNode REGR_SXX() { return getToken(SQLSelectParserParser.REGR_SXX, 0); }
		public TerminalNode REGR_SXY() { return getToken(SQLSelectParserParser.REGR_SXY, 0); }
		public TerminalNode REGR_SYY() { return getToken(SQLSelectParserParser.REGR_SYY, 0); }
		public TerminalNode APPROX_COUNT_DISTINCT() { return getToken(SQLSelectParserParser.APPROX_COUNT_DISTINCT, 0); }
		public TerminalNode HLL() { return getToken(SQLSelectParserParser.HLL, 0); }
		public TerminalNode HLL_ACCUMULATE() { return getToken(SQLSelectParserParser.HLL_ACCUMULATE, 0); }
		public TerminalNode HLL_COMBINE() { return getToken(SQLSelectParserParser.HLL_COMBINE, 0); }
		public TerminalNode HLL_EXPORT() { return getToken(SQLSelectParserParser.HLL_EXPORT, 0); }
		public TerminalNode HLL_IMPORT() { return getToken(SQLSelectParserParser.HLL_IMPORT, 0); }
		public TerminalNode APPROXIMATE_JACCARD_INDEX() { return getToken(SQLSelectParserParser.APPROXIMATE_JACCARD_INDEX, 0); }
		public TerminalNode APPROXIMATE_SIMILARITY() { return getToken(SQLSelectParserParser.APPROXIMATE_SIMILARITY, 0); }
		public TerminalNode MINHASH() { return getToken(SQLSelectParserParser.MINHASH, 0); }
		public TerminalNode MINHASH_COMBINE() { return getToken(SQLSelectParserParser.MINHASH_COMBINE, 0); }
		public TerminalNode APPROX_TOP_K() { return getToken(SQLSelectParserParser.APPROX_TOP_K, 0); }
		public TerminalNode APPROX_TOP_K_ACCUMULATE() { return getToken(SQLSelectParserParser.APPROX_TOP_K_ACCUMULATE, 0); }
		public TerminalNode APPROX_TOP_K_COMBINE() { return getToken(SQLSelectParserParser.APPROX_TOP_K_COMBINE, 0); }
		public TerminalNode APPROX_PERCENTILE() { return getToken(SQLSelectParserParser.APPROX_PERCENTILE, 0); }
		public TerminalNode APPROX_PERCENTILE_ACCUMULATE() { return getToken(SQLSelectParserParser.APPROX_PERCENTILE_ACCUMULATE, 0); }
		public TerminalNode APPROX_PERCENTILE_COMBINE() { return getToken(SQLSelectParserParser.APPROX_PERCENTILE_COMBINE, 0); }
		public TerminalNode IGNORE() { return getToken(SQLSelectParserParser.IGNORE, 0); }
		public TerminalNode RESPECT() { return getToken(SQLSelectParserParser.RESPECT, 0); }
		public TerminalNode NULLS() { return getToken(SQLSelectParserParser.NULLS, 0); }
		public Nonreserved_keywordsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nonreserved_keywords; }
	}

	public final Nonreserved_keywordsContext nonreserved_keywords() throws RecognitionException {
		Nonreserved_keywordsContext _localctx = new Nonreserved_keywordsContext(_ctx, getState());
		enterRule(_localctx, 360, RULE_nonreserved_keywords);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1402);
			_la = _input.LA(1);
			if ( !(((((_la - 20)) & ~0x3f) == 0 && ((1L << (_la - 20)) & -5630315556929535L) != 0) || ((((_la - 84)) & ~0x3f) == 0 && ((1L << (_la - 84)) & -211245819396633L) != 0) || ((((_la - 149)) & ~0x3f) == 0 && ((1L << (_la - 149)) & -576460752303423489L) != 0) || ((((_la - 213)) & ~0x3f) == 0 && ((1L << (_la - 213)) & 72057555382173695L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Signed_numeric_literalContext extends ParserRuleContext {
		public Unsigned_numeric_literalContext unsigned_numeric_literal() {
			return getRuleContext(Unsigned_numeric_literalContext.class,0);
		}
		public SignContext sign() {
			return getRuleContext(SignContext.class,0);
		}
		public Signed_numeric_literalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_signed_numeric_literal; }
	}

	public final Signed_numeric_literalContext signed_numeric_literal() throws RecognitionException {
		Signed_numeric_literalContext _localctx = new Signed_numeric_literalContext(_ctx, getState());
		enterRule(_localctx, 362, RULE_signed_numeric_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1405);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PLUS || _la==MINUS) {
				{
				setState(1404);
				sign();
				}
			}

			setState(1407);
			unsigned_numeric_literal();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Unsigned_literalContext extends ParserRuleContext {
		public Unsigned_numeric_literalContext unsigned_numeric_literal() {
			return getRuleContext(Unsigned_numeric_literalContext.class,0);
		}
		public General_literalContext general_literal() {
			return getRuleContext(General_literalContext.class,0);
		}
		public Unsigned_literalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unsigned_literal; }
	}

	public final Unsigned_literalContext unsigned_literal() throws RecognitionException {
		Unsigned_literalContext _localctx = new Unsigned_literalContext(_ctx, getState());
		enterRule(_localctx, 364, RULE_unsigned_literal);
		try {
			setState(1411);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DOT:
			case NUMBER:
				enterOuterAlt(_localctx, 1);
				{
				setState(1409);
				unsigned_numeric_literal();
				}
				break;
			case FALSE:
			case TRUE:
			case UNKNOWN:
			case DATE:
			case TIME:
			case TIMESTAMP:
			case Character_String_Literal:
				enterOuterAlt(_localctx, 2);
				{
				setState(1410);
				general_literal();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Unsigned_numeric_literalContext extends ParserRuleContext {
		public Unsigned_numeric_literalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unsigned_numeric_literal; }
	 
		public Unsigned_numeric_literalContext() { }
		public void copyFrom(Unsigned_numeric_literalContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Real_numberContext extends Unsigned_numeric_literalContext {
		public Real_number_defContext real_number_def() {
			return getRuleContext(Real_number_defContext.class,0);
		}
		public Real_numberContext(Unsigned_numeric_literalContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Ordinal_numberContext extends Unsigned_numeric_literalContext {
		public TerminalNode NUMBER() { return getToken(SQLSelectParserParser.NUMBER, 0); }
		public Ordinal_numberContext(Unsigned_numeric_literalContext ctx) { copyFrom(ctx); }
	}

	public final Unsigned_numeric_literalContext unsigned_numeric_literal() throws RecognitionException {
		Unsigned_numeric_literalContext _localctx = new Unsigned_numeric_literalContext(_ctx, getState());
		enterRule(_localctx, 366, RULE_unsigned_numeric_literal);
		try {
			setState(1415);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,137,_ctx) ) {
			case 1:
				_localctx = new Ordinal_numberContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(1413);
				match(NUMBER);
				}
				break;
			case 2:
				_localctx = new Real_numberContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(1414);
				real_number_def();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Real_number_defContext extends ParserRuleContext {
		public List<TerminalNode> NUMBER() { return getTokens(SQLSelectParserParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(SQLSelectParserParser.NUMBER, i);
		}
		public TerminalNode DOT() { return getToken(SQLSelectParserParser.DOT, 0); }
		public ExponentContext exponent() {
			return getRuleContext(ExponentContext.class,0);
		}
		public Real_number_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_real_number_def; }
	}

	public final Real_number_defContext real_number_def() throws RecognitionException {
		Real_number_defContext _localctx = new Real_number_defContext(_ctx, getState());
		enterRule(_localctx, 368, RULE_real_number_def);
		int _la;
		try {
			setState(1432);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,141,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1417);
				match(NUMBER);
				setState(1418);
				match(DOT);
				setState(1420);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,138,_ctx) ) {
				case 1:
					{
					setState(1419);
					match(NUMBER);
					}
					break;
				}
				setState(1423);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==EXPONEN) {
					{
					setState(1422);
					exponent();
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1425);
				match(DOT);
				setState(1426);
				match(NUMBER);
				setState(1428);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==EXPONEN) {
					{
					setState(1427);
					exponent();
					}
				}

				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1430);
				match(NUMBER);
				setState(1431);
				exponent();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExponentContext extends ParserRuleContext {
		public TerminalNode EXPONEN() { return getToken(SQLSelectParserParser.EXPONEN, 0); }
		public TerminalNode NUMBER() { return getToken(SQLSelectParserParser.NUMBER, 0); }
		public ExponentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exponent; }
	}

	public final ExponentContext exponent() throws RecognitionException {
		ExponentContext _localctx = new ExponentContext(_ctx, getState());
		enterRule(_localctx, 370, RULE_exponent);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1434);
			match(EXPONEN);
			setState(1435);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class General_literalContext extends ParserRuleContext {
		public Character_literalContext character_literal() {
			return getRuleContext(Character_literalContext.class,0);
		}
		public Datetime_literalContext datetime_literal() {
			return getRuleContext(Datetime_literalContext.class,0);
		}
		public Boolean_literalContext boolean_literal() {
			return getRuleContext(Boolean_literalContext.class,0);
		}
		public General_literalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_general_literal; }
	}

	public final General_literalContext general_literal() throws RecognitionException {
		General_literalContext _localctx = new General_literalContext(_ctx, getState());
		enterRule(_localctx, 372, RULE_general_literal);
		try {
			setState(1440);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Character_String_Literal:
				enterOuterAlt(_localctx, 1);
				{
				setState(1437);
				character_literal();
				}
				break;
			case DATE:
			case TIME:
			case TIMESTAMP:
				enterOuterAlt(_localctx, 2);
				{
				setState(1438);
				datetime_literal();
				}
				break;
			case FALSE:
			case TRUE:
			case UNKNOWN:
				enterOuterAlt(_localctx, 3);
				{
				setState(1439);
				boolean_literal();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Character_literalContext extends ParserRuleContext {
		public TerminalNode Character_String_Literal() { return getToken(SQLSelectParserParser.Character_String_Literal, 0); }
		public Character_literalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_character_literal; }
	}

	public final Character_literalContext character_literal() throws RecognitionException {
		Character_literalContext _localctx = new Character_literalContext(_ctx, getState());
		enterRule(_localctx, 374, RULE_character_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1442);
			match(Character_String_Literal);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Datetime_literalContext extends ParserRuleContext {
		public Timestamp_literalContext timestamp_literal() {
			return getRuleContext(Timestamp_literalContext.class,0);
		}
		public Time_literalContext time_literal() {
			return getRuleContext(Time_literalContext.class,0);
		}
		public Date_literalContext date_literal() {
			return getRuleContext(Date_literalContext.class,0);
		}
		public Datetime_literalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_datetime_literal; }
	}

	public final Datetime_literalContext datetime_literal() throws RecognitionException {
		Datetime_literalContext _localctx = new Datetime_literalContext(_ctx, getState());
		enterRule(_localctx, 376, RULE_datetime_literal);
		try {
			setState(1447);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TIMESTAMP:
				enterOuterAlt(_localctx, 1);
				{
				setState(1444);
				timestamp_literal();
				}
				break;
			case TIME:
				enterOuterAlt(_localctx, 2);
				{
				setState(1445);
				time_literal();
				}
				break;
			case DATE:
				enterOuterAlt(_localctx, 3);
				{
				setState(1446);
				date_literal();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Time_literalContext extends ParserRuleContext {
		public Token time_string;
		public TerminalNode TIME() { return getToken(SQLSelectParserParser.TIME, 0); }
		public TerminalNode Character_String_Literal() { return getToken(SQLSelectParserParser.Character_String_Literal, 0); }
		public Time_literalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_time_literal; }
	}

	public final Time_literalContext time_literal() throws RecognitionException {
		Time_literalContext _localctx = new Time_literalContext(_ctx, getState());
		enterRule(_localctx, 378, RULE_time_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1449);
			match(TIME);
			setState(1450);
			((Time_literalContext)_localctx).time_string = match(Character_String_Literal);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Timestamp_literalContext extends ParserRuleContext {
		public Token timestamp_string;
		public TerminalNode TIMESTAMP() { return getToken(SQLSelectParserParser.TIMESTAMP, 0); }
		public TerminalNode Character_String_Literal() { return getToken(SQLSelectParserParser.Character_String_Literal, 0); }
		public Timestamp_literalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_timestamp_literal; }
	}

	public final Timestamp_literalContext timestamp_literal() throws RecognitionException {
		Timestamp_literalContext _localctx = new Timestamp_literalContext(_ctx, getState());
		enterRule(_localctx, 380, RULE_timestamp_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1452);
			match(TIMESTAMP);
			setState(1453);
			((Timestamp_literalContext)_localctx).timestamp_string = match(Character_String_Literal);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Date_literalContext extends ParserRuleContext {
		public Token date_string;
		public TerminalNode DATE() { return getToken(SQLSelectParserParser.DATE, 0); }
		public TerminalNode Character_String_Literal() { return getToken(SQLSelectParserParser.Character_String_Literal, 0); }
		public Date_literalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_date_literal; }
	}

	public final Date_literalContext date_literal() throws RecognitionException {
		Date_literalContext _localctx = new Date_literalContext(_ctx, getState());
		enterRule(_localctx, 382, RULE_date_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1455);
			match(DATE);
			setState(1456);
			((Date_literalContext)_localctx).date_string = match(Character_String_Literal);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Boolean_literalContext extends ParserRuleContext {
		public TerminalNode TRUE() { return getToken(SQLSelectParserParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(SQLSelectParserParser.FALSE, 0); }
		public TerminalNode UNKNOWN() { return getToken(SQLSelectParserParser.UNKNOWN, 0); }
		public Boolean_literalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolean_literal; }
	}

	public final Boolean_literalContext boolean_literal() throws RecognitionException {
		Boolean_literalContext _localctx = new Boolean_literalContext(_ctx, getState());
		enterRule(_localctx, 384, RULE_boolean_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1458);
			_la = _input.LA(1);
			if ( !(_la==FALSE || _la==TRUE || _la==UNKNOWN) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_typeContext extends ParserRuleContext {
		public Variable_size_data_typeContext variable_size_data_type() {
			return getRuleContext(Variable_size_data_typeContext.class,0);
		}
		public Precision_scale_data_typeContext precision_scale_data_type() {
			return getRuleContext(Precision_scale_data_typeContext.class,0);
		}
		public Static_data_typeContext static_data_type() {
			return getRuleContext(Static_data_typeContext.class,0);
		}
		public Data_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_type; }
	}

	public final Data_typeContext data_type() throws RecognitionException {
		Data_typeContext _localctx = new Data_typeContext(_ctx, getState());
		enterRule(_localctx, 386, RULE_data_type);
		try {
			setState(1463);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CHARACTER:
			case NATIONAL:
			case BIT:
			case VARBIT:
			case INTERVAL:
			case CHAR:
			case VARCHAR:
			case VARCHAR2:
			case NCHAR:
			case NVARCHAR:
			case STRING:
			case BINARY:
			case VARBINARY:
			case BLOB:
			case BYTEA:
				enterOuterAlt(_localctx, 1);
				{
				setState(1460);
				variable_size_data_type();
				}
				break;
			case DEC:
			case FLOAT:
			case DOUBLE:
			case NUMERIC:
			case DECIMAL:
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(1461);
				precision_scale_data_type();
				}
				break;
			case NUMBER_TYPE:
			case UNION:
			case ABSTIME:
			case ANYARRAY:
			case ARRAY:
			case BOOLEAN:
			case BOOL:
			case CIDR:
			case INET:
			case INET4:
			case INT1:
			case INT2:
			case INT4:
			case INT8:
			case JSON:
			case JSONB:
			case MACADDR:
			case NAME:
			case OID:
			case PG_LSN:
			case PG_NODE_TREE:
			case REGPROC:
			case XID:
			case UUID:
			case TINYINT:
			case SMALLINT:
			case INT:
			case INTEGER:
			case BIGINT:
			case BIGSERIAL:
			case SMALLSERIAL:
			case SERIAL:
			case MONEY:
			case FLOAT4:
			case FLOAT8:
			case REAL:
			case DATE:
			case DATETIME:
			case TIME:
			case TIMETZ:
			case TIMESTAMP:
			case TIMESTAMP_LTZ:
			case TIMESTAMP_NTZ:
			case TIMESTAMP_TZ:
			case TIMESTAMPTZ:
			case TEXT:
			case OBJECT:
			case STRUCT:
			case VARIANT:
				enterOuterAlt(_localctx, 3);
				{
				setState(1462);
				static_data_type();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Variable_size_data_typeContext extends ParserRuleContext {
		public Variable_data_type_nameContext variable_data_type_name() {
			return getRuleContext(Variable_data_type_nameContext.class,0);
		}
		public Type_lengthContext type_length() {
			return getRuleContext(Type_lengthContext.class,0);
		}
		public Variable_size_data_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable_size_data_type; }
	}

	public final Variable_size_data_typeContext variable_size_data_type() throws RecognitionException {
		Variable_size_data_typeContext _localctx = new Variable_size_data_typeContext(_ctx, getState());
		enterRule(_localctx, 388, RULE_variable_size_data_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1465);
			variable_data_type_name();
			setState(1467);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LEFT_PAREN) {
				{
				setState(1466);
				type_length();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Variable_data_type_nameContext extends ParserRuleContext {
		public TerminalNode CHARACTER() { return getToken(SQLSelectParserParser.CHARACTER, 0); }
		public TerminalNode CHAR() { return getToken(SQLSelectParserParser.CHAR, 0); }
		public TerminalNode VARYING() { return getToken(SQLSelectParserParser.VARYING, 0); }
		public TerminalNode VARCHAR() { return getToken(SQLSelectParserParser.VARCHAR, 0); }
		public TerminalNode VARCHAR2() { return getToken(SQLSelectParserParser.VARCHAR2, 0); }
		public TerminalNode NATIONAL() { return getToken(SQLSelectParserParser.NATIONAL, 0); }
		public TerminalNode NCHAR() { return getToken(SQLSelectParserParser.NCHAR, 0); }
		public TerminalNode NVARCHAR() { return getToken(SQLSelectParserParser.NVARCHAR, 0); }
		public TerminalNode BLOB() { return getToken(SQLSelectParserParser.BLOB, 0); }
		public TerminalNode BYTEA() { return getToken(SQLSelectParserParser.BYTEA, 0); }
		public TerminalNode BIT() { return getToken(SQLSelectParserParser.BIT, 0); }
		public TerminalNode VARBIT() { return getToken(SQLSelectParserParser.VARBIT, 0); }
		public TerminalNode BINARY() { return getToken(SQLSelectParserParser.BINARY, 0); }
		public TerminalNode VARBINARY() { return getToken(SQLSelectParserParser.VARBINARY, 0); }
		public TerminalNode INTERVAL() { return getToken(SQLSelectParserParser.INTERVAL, 0); }
		public TerminalNode STRING() { return getToken(SQLSelectParserParser.STRING, 0); }
		public Variable_data_type_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable_data_type_name; }
	}

	public final Variable_data_type_nameContext variable_data_type_name() throws RecognitionException {
		Variable_data_type_nameContext _localctx = new Variable_data_type_nameContext(_ctx, getState());
		enterRule(_localctx, 390, RULE_variable_data_type_name);
		try {
			setState(1503);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,146,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1469);
				match(CHARACTER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1470);
				match(CHAR);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1471);
				match(CHARACTER);
				setState(1472);
				match(VARYING);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1473);
				match(CHAR);
				setState(1474);
				match(VARYING);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1475);
				match(VARCHAR);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1476);
				match(VARCHAR2);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1477);
				match(NATIONAL);
				setState(1478);
				match(CHARACTER);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1479);
				match(NATIONAL);
				setState(1480);
				match(CHAR);
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(1481);
				match(NCHAR);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(1482);
				match(NATIONAL);
				setState(1483);
				match(CHARACTER);
				setState(1484);
				match(VARYING);
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(1485);
				match(NATIONAL);
				setState(1486);
				match(CHAR);
				setState(1487);
				match(VARYING);
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(1488);
				match(NCHAR);
				setState(1489);
				match(VARYING);
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(1490);
				match(NVARCHAR);
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(1491);
				match(BLOB);
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				setState(1492);
				match(BYTEA);
				}
				break;
			case 16:
				enterOuterAlt(_localctx, 16);
				{
				setState(1493);
				match(BIT);
				}
				break;
			case 17:
				enterOuterAlt(_localctx, 17);
				{
				setState(1494);
				match(VARBIT);
				}
				break;
			case 18:
				enterOuterAlt(_localctx, 18);
				{
				setState(1495);
				match(BIT);
				setState(1496);
				match(VARYING);
				}
				break;
			case 19:
				enterOuterAlt(_localctx, 19);
				{
				setState(1497);
				match(BINARY);
				}
				break;
			case 20:
				enterOuterAlt(_localctx, 20);
				{
				setState(1498);
				match(BINARY);
				setState(1499);
				match(VARYING);
				}
				break;
			case 21:
				enterOuterAlt(_localctx, 21);
				{
				setState(1500);
				match(VARBINARY);
				}
				break;
			case 22:
				enterOuterAlt(_localctx, 22);
				{
				setState(1501);
				match(INTERVAL);
				}
				break;
			case 23:
				enterOuterAlt(_localctx, 23);
				{
				setState(1502);
				match(STRING);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Type_lengthContext extends ParserRuleContext {
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public TerminalNode NUMBER() { return getToken(SQLSelectParserParser.NUMBER, 0); }
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Type_lengthContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_length; }
	}

	public final Type_lengthContext type_length() throws RecognitionException {
		Type_lengthContext _localctx = new Type_lengthContext(_ctx, getState());
		enterRule(_localctx, 392, RULE_type_length);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1505);
			match(LEFT_PAREN);
			setState(1506);
			match(NUMBER);
			setState(1507);
			match(RIGHT_PAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Precision_scale_data_typeContext extends ParserRuleContext {
		public Precision_data_type_nameContext precision_data_type_name() {
			return getRuleContext(Precision_data_type_nameContext.class,0);
		}
		public Precision_paramContext precision_param() {
			return getRuleContext(Precision_paramContext.class,0);
		}
		public Precision_scale_data_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_precision_scale_data_type; }
	}

	public final Precision_scale_data_typeContext precision_scale_data_type() throws RecognitionException {
		Precision_scale_data_typeContext _localctx = new Precision_scale_data_typeContext(_ctx, getState());
		enterRule(_localctx, 394, RULE_precision_scale_data_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1509);
			precision_data_type_name();
			setState(1511);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LEFT_PAREN) {
				{
				setState(1510);
				precision_param();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Precision_data_type_nameContext extends ParserRuleContext {
		public TerminalNode NUMERIC() { return getToken(SQLSelectParserParser.NUMERIC, 0); }
		public TerminalNode NUMBER() { return getToken(SQLSelectParserParser.NUMBER, 0); }
		public TerminalNode DECIMAL() { return getToken(SQLSelectParserParser.DECIMAL, 0); }
		public TerminalNode DEC() { return getToken(SQLSelectParserParser.DEC, 0); }
		public TerminalNode FLOAT() { return getToken(SQLSelectParserParser.FLOAT, 0); }
		public TerminalNode DOUBLE() { return getToken(SQLSelectParserParser.DOUBLE, 0); }
		public TerminalNode PRECISION() { return getToken(SQLSelectParserParser.PRECISION, 0); }
		public Precision_data_type_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_precision_data_type_name; }
	}

	public final Precision_data_type_nameContext precision_data_type_name() throws RecognitionException {
		Precision_data_type_nameContext _localctx = new Precision_data_type_nameContext(_ctx, getState());
		enterRule(_localctx, 396, RULE_precision_data_type_name);
		try {
			setState(1521);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,148,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1513);
				match(NUMERIC);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1514);
				match(NUMBER);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1515);
				match(DECIMAL);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1516);
				match(DEC);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1517);
				match(FLOAT);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1518);
				match(DOUBLE);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1519);
				match(DOUBLE);
				setState(1520);
				match(PRECISION);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Precision_paramContext extends ParserRuleContext {
		public Token precision;
		public Token scale;
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public List<TerminalNode> NUMBER() { return getTokens(SQLSelectParserParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(SQLSelectParserParser.NUMBER, i);
		}
		public TerminalNode COMMA() { return getToken(SQLSelectParserParser.COMMA, 0); }
		public Precision_paramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_precision_param; }
	}

	public final Precision_paramContext precision_param() throws RecognitionException {
		Precision_paramContext _localctx = new Precision_paramContext(_ctx, getState());
		enterRule(_localctx, 398, RULE_precision_param);
		try {
			setState(1531);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,149,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1523);
				match(LEFT_PAREN);
				setState(1524);
				((Precision_paramContext)_localctx).precision = match(NUMBER);
				setState(1525);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1526);
				match(LEFT_PAREN);
				setState(1527);
				((Precision_paramContext)_localctx).precision = match(NUMBER);
				setState(1528);
				match(COMMA);
				setState(1529);
				((Precision_paramContext)_localctx).scale = match(NUMBER);
				setState(1530);
				match(RIGHT_PAREN);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Static_data_typeContext extends ParserRuleContext {
		public Static_data_type_nameContext static_data_type_name() {
			return getRuleContext(Static_data_type_nameContext.class,0);
		}
		public Static_data_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_static_data_type; }
	}

	public final Static_data_typeContext static_data_type() throws RecognitionException {
		Static_data_typeContext _localctx = new Static_data_typeContext(_ctx, getState());
		enterRule(_localctx, 400, RULE_static_data_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1533);
			static_data_type_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Static_data_type_nameContext extends ParserRuleContext {
		public TerminalNode TEXT() { return getToken(SQLSelectParserParser.TEXT, 0); }
		public TerminalNode NAME() { return getToken(SQLSelectParserParser.NAME, 0); }
		public TerminalNode INET4() { return getToken(SQLSelectParserParser.INET4, 0); }
		public TerminalNode INET() { return getToken(SQLSelectParserParser.INET, 0); }
		public TerminalNode CIDR() { return getToken(SQLSelectParserParser.CIDR, 0); }
		public TerminalNode STRUCT() { return getToken(SQLSelectParserParser.STRUCT, 0); }
		public TerminalNode UNION() { return getToken(SQLSelectParserParser.UNION, 0); }
		public TerminalNode VARIANT() { return getToken(SQLSelectParserParser.VARIANT, 0); }
		public TerminalNode OBJECT() { return getToken(SQLSelectParserParser.OBJECT, 0); }
		public TerminalNode JSON() { return getToken(SQLSelectParserParser.JSON, 0); }
		public TerminalNode JSONB() { return getToken(SQLSelectParserParser.JSONB, 0); }
		public TerminalNode OID() { return getToken(SQLSelectParserParser.OID, 0); }
		public TerminalNode XID() { return getToken(SQLSelectParserParser.XID, 0); }
		public TerminalNode UUID() { return getToken(SQLSelectParserParser.UUID, 0); }
		public TerminalNode PG_LSN() { return getToken(SQLSelectParserParser.PG_LSN, 0); }
		public TerminalNode PG_NODE_TREE() { return getToken(SQLSelectParserParser.PG_NODE_TREE, 0); }
		public TerminalNode REGPROC() { return getToken(SQLSelectParserParser.REGPROC, 0); }
		public TerminalNode MACADDR() { return getToken(SQLSelectParserParser.MACADDR, 0); }
		public TerminalNode INT1() { return getToken(SQLSelectParserParser.INT1, 0); }
		public TerminalNode TINYINT() { return getToken(SQLSelectParserParser.TINYINT, 0); }
		public TerminalNode INT2() { return getToken(SQLSelectParserParser.INT2, 0); }
		public TerminalNode SMALLINT() { return getToken(SQLSelectParserParser.SMALLINT, 0); }
		public TerminalNode INT4() { return getToken(SQLSelectParserParser.INT4, 0); }
		public TerminalNode INT() { return getToken(SQLSelectParserParser.INT, 0); }
		public TerminalNode INTEGER() { return getToken(SQLSelectParserParser.INTEGER, 0); }
		public TerminalNode INT8() { return getToken(SQLSelectParserParser.INT8, 0); }
		public TerminalNode BIGINT() { return getToken(SQLSelectParserParser.BIGINT, 0); }
		public TerminalNode BIGSERIAL() { return getToken(SQLSelectParserParser.BIGSERIAL, 0); }
		public TerminalNode SMALLSERIAL() { return getToken(SQLSelectParserParser.SMALLSERIAL, 0); }
		public TerminalNode SERIAL() { return getToken(SQLSelectParserParser.SERIAL, 0); }
		public TerminalNode MONEY() { return getToken(SQLSelectParserParser.MONEY, 0); }
		public TerminalNode NUMBER_TYPE() { return getToken(SQLSelectParserParser.NUMBER_TYPE, 0); }
		public TerminalNode FLOAT4() { return getToken(SQLSelectParserParser.FLOAT4, 0); }
		public TerminalNode REAL() { return getToken(SQLSelectParserParser.REAL, 0); }
		public TerminalNode FLOAT8() { return getToken(SQLSelectParserParser.FLOAT8, 0); }
		public TerminalNode BOOLEAN() { return getToken(SQLSelectParserParser.BOOLEAN, 0); }
		public TerminalNode BOOL() { return getToken(SQLSelectParserParser.BOOL, 0); }
		public TerminalNode DATE() { return getToken(SQLSelectParserParser.DATE, 0); }
		public TerminalNode DATETIME() { return getToken(SQLSelectParserParser.DATETIME, 0); }
		public List<TerminalNode> TIME() { return getTokens(SQLSelectParserParser.TIME); }
		public TerminalNode TIME(int i) {
			return getToken(SQLSelectParserParser.TIME, i);
		}
		public TerminalNode WITH() { return getToken(SQLSelectParserParser.WITH, 0); }
		public TerminalNode ZONE() { return getToken(SQLSelectParserParser.ZONE, 0); }
		public TerminalNode TIMETZ() { return getToken(SQLSelectParserParser.TIMETZ, 0); }
		public TerminalNode TIMESTAMP_LTZ() { return getToken(SQLSelectParserParser.TIMESTAMP_LTZ, 0); }
		public TerminalNode TIMESTAMP_NTZ() { return getToken(SQLSelectParserParser.TIMESTAMP_NTZ, 0); }
		public TerminalNode TIMESTAMP_TZ() { return getToken(SQLSelectParserParser.TIMESTAMP_TZ, 0); }
		public TerminalNode TIMESTAMP() { return getToken(SQLSelectParserParser.TIMESTAMP, 0); }
		public TerminalNode WITHOUT() { return getToken(SQLSelectParserParser.WITHOUT, 0); }
		public TerminalNode TIMESTAMPTZ() { return getToken(SQLSelectParserParser.TIMESTAMPTZ, 0); }
		public TerminalNode ABSTIME() { return getToken(SQLSelectParserParser.ABSTIME, 0); }
		public TerminalNode ARRAY() { return getToken(SQLSelectParserParser.ARRAY, 0); }
		public TerminalNode ANYARRAY() { return getToken(SQLSelectParserParser.ANYARRAY, 0); }
		public Static_data_type_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_static_data_type_name; }
	}

	public final Static_data_type_nameContext static_data_type_name() throws RecognitionException {
		Static_data_type_nameContext _localctx = new Static_data_type_nameContext(_ctx, getState());
		enterRule(_localctx, 402, RULE_static_data_type_name);
		try {
			setState(1596);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,150,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1535);
				match(TEXT);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1536);
				match(NAME);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1537);
				match(INET4);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1538);
				match(INET);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1539);
				match(CIDR);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1540);
				match(STRUCT);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1541);
				match(UNION);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1542);
				match(VARIANT);
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(1543);
				match(OBJECT);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(1544);
				match(JSON);
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(1545);
				match(JSONB);
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(1546);
				match(OID);
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(1547);
				match(XID);
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(1548);
				match(UUID);
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				setState(1549);
				match(PG_LSN);
				}
				break;
			case 16:
				enterOuterAlt(_localctx, 16);
				{
				setState(1550);
				match(PG_NODE_TREE);
				}
				break;
			case 17:
				enterOuterAlt(_localctx, 17);
				{
				setState(1551);
				match(REGPROC);
				}
				break;
			case 18:
				enterOuterAlt(_localctx, 18);
				{
				setState(1552);
				match(MACADDR);
				}
				break;
			case 19:
				enterOuterAlt(_localctx, 19);
				{
				setState(1553);
				match(INT1);
				}
				break;
			case 20:
				enterOuterAlt(_localctx, 20);
				{
				setState(1554);
				match(TINYINT);
				}
				break;
			case 21:
				enterOuterAlt(_localctx, 21);
				{
				setState(1555);
				match(INT2);
				}
				break;
			case 22:
				enterOuterAlt(_localctx, 22);
				{
				setState(1556);
				match(SMALLINT);
				}
				break;
			case 23:
				enterOuterAlt(_localctx, 23);
				{
				setState(1557);
				match(INT4);
				}
				break;
			case 24:
				enterOuterAlt(_localctx, 24);
				{
				setState(1558);
				match(INT);
				}
				break;
			case 25:
				enterOuterAlt(_localctx, 25);
				{
				setState(1559);
				match(INTEGER);
				}
				break;
			case 26:
				enterOuterAlt(_localctx, 26);
				{
				setState(1560);
				match(INT8);
				}
				break;
			case 27:
				enterOuterAlt(_localctx, 27);
				{
				setState(1561);
				match(BIGINT);
				}
				break;
			case 28:
				enterOuterAlt(_localctx, 28);
				{
				setState(1562);
				match(BIGSERIAL);
				}
				break;
			case 29:
				enterOuterAlt(_localctx, 29);
				{
				setState(1563);
				match(SMALLSERIAL);
				}
				break;
			case 30:
				enterOuterAlt(_localctx, 30);
				{
				setState(1564);
				match(SERIAL);
				}
				break;
			case 31:
				enterOuterAlt(_localctx, 31);
				{
				setState(1565);
				match(MONEY);
				}
				break;
			case 32:
				enterOuterAlt(_localctx, 32);
				{
				setState(1566);
				match(NUMBER_TYPE);
				}
				break;
			case 33:
				enterOuterAlt(_localctx, 33);
				{
				setState(1567);
				match(FLOAT4);
				}
				break;
			case 34:
				enterOuterAlt(_localctx, 34);
				{
				setState(1568);
				match(REAL);
				}
				break;
			case 35:
				enterOuterAlt(_localctx, 35);
				{
				setState(1569);
				match(FLOAT8);
				}
				break;
			case 36:
				enterOuterAlt(_localctx, 36);
				{
				setState(1570);
				match(BOOLEAN);
				}
				break;
			case 37:
				enterOuterAlt(_localctx, 37);
				{
				setState(1571);
				match(BOOL);
				}
				break;
			case 38:
				enterOuterAlt(_localctx, 38);
				{
				setState(1572);
				match(DATE);
				}
				break;
			case 39:
				enterOuterAlt(_localctx, 39);
				{
				setState(1573);
				match(DATETIME);
				}
				break;
			case 40:
				enterOuterAlt(_localctx, 40);
				{
				setState(1574);
				match(TIME);
				}
				break;
			case 41:
				enterOuterAlt(_localctx, 41);
				{
				setState(1575);
				match(TIME);
				setState(1576);
				match(WITH);
				setState(1577);
				match(TIME);
				setState(1578);
				match(ZONE);
				}
				break;
			case 42:
				enterOuterAlt(_localctx, 42);
				{
				setState(1579);
				match(TIMETZ);
				}
				break;
			case 43:
				enterOuterAlt(_localctx, 43);
				{
				setState(1580);
				match(TIMESTAMP_LTZ);
				}
				break;
			case 44:
				enterOuterAlt(_localctx, 44);
				{
				setState(1581);
				match(TIMESTAMP_NTZ);
				}
				break;
			case 45:
				enterOuterAlt(_localctx, 45);
				{
				setState(1582);
				match(TIMESTAMP_TZ);
				}
				break;
			case 46:
				enterOuterAlt(_localctx, 46);
				{
				setState(1583);
				match(TIMESTAMP);
				}
				break;
			case 47:
				enterOuterAlt(_localctx, 47);
				{
				setState(1584);
				match(TIMESTAMP);
				setState(1585);
				match(WITH);
				setState(1586);
				match(TIME);
				setState(1587);
				match(ZONE);
				}
				break;
			case 48:
				enterOuterAlt(_localctx, 48);
				{
				setState(1588);
				match(TIMESTAMP);
				setState(1589);
				match(WITHOUT);
				setState(1590);
				match(TIME);
				setState(1591);
				match(ZONE);
				}
				break;
			case 49:
				enterOuterAlt(_localctx, 49);
				{
				setState(1592);
				match(TIMESTAMPTZ);
				}
				break;
			case 50:
				enterOuterAlt(_localctx, 50);
				{
				setState(1593);
				match(ABSTIME);
				}
				break;
			case 51:
				enterOuterAlt(_localctx, 51);
				{
				setState(1594);
				match(ARRAY);
				}
				break;
			case 52:
				enterOuterAlt(_localctx, 52);
				{
				setState(1595);
				match(ANYARRAY);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Puml_constant_identifierContext extends ParserRuleContext {
		public TerminalNode PUML_CONSTANT_TENANT_SK() { return getToken(SQLSelectParserParser.PUML_CONSTANT_TENANT_SK, 0); }
		public TerminalNode PUML_CONSTANT_TENANT_GUID() { return getToken(SQLSelectParserParser.PUML_CONSTANT_TENANT_GUID, 0); }
		public TerminalNode PUML_CONSTANT_TENANT_MASTER_ID() { return getToken(SQLSelectParserParser.PUML_CONSTANT_TENANT_MASTER_ID, 0); }
		public TerminalNode PUML_CONSTANT_TENANT_NAME() { return getToken(SQLSelectParserParser.PUML_CONSTANT_TENANT_NAME, 0); }
		public TerminalNode PUML_CONSTANT_TENANT_ACRONYM() { return getToken(SQLSelectParserParser.PUML_CONSTANT_TENANT_ACRONYM, 0); }
		public TerminalNode PUML_CONSTANT_TENANT_WEB_DOMAIN() { return getToken(SQLSelectParserParser.PUML_CONSTANT_TENANT_WEB_DOMAIN, 0); }
		public TerminalNode PUML_CONSTANT_ES_INSTITUTION_ID() { return getToken(SQLSelectParserParser.PUML_CONSTANT_ES_INSTITUTION_ID, 0); }
		public TerminalNode PUML_CONSTANT_ES_INSTITUTION_CODE() { return getToken(SQLSelectParserParser.PUML_CONSTANT_ES_INSTITUTION_CODE, 0); }
		public TerminalNode PUML_CONSTANT_ES_INSTITUTION_NAME() { return getToken(SQLSelectParserParser.PUML_CONSTANT_ES_INSTITUTION_NAME, 0); }
		public TerminalNode PUML_CONSTANT_SF_COUNTER_ID() { return getToken(SQLSelectParserParser.PUML_CONSTANT_SF_COUNTER_ID, 0); }
		public TerminalNode PUML_CONSTANT_FILE_NAME() { return getToken(SQLSelectParserParser.PUML_CONSTANT_FILE_NAME, 0); }
		public TerminalNode PUML_CONSTANT_FILE_ID() { return getToken(SQLSelectParserParser.PUML_CONSTANT_FILE_ID, 0); }
		public TerminalNode PUML_CONSTANT_ROW_NUMBER() { return getToken(SQLSelectParserParser.PUML_CONSTANT_ROW_NUMBER, 0); }
		public TerminalNode PUML_CONSTANT_OBSERVATION_TIME() { return getToken(SQLSelectParserParser.PUML_CONSTANT_OBSERVATION_TIME, 0); }
		public TerminalNode PUML_CONSTANT_SYSTEM_DATE() { return getToken(SQLSelectParserParser.PUML_CONSTANT_SYSTEM_DATE, 0); }
		public TerminalNode PUML_CONSTANT_SYSTEM_TIME() { return getToken(SQLSelectParserParser.PUML_CONSTANT_SYSTEM_TIME, 0); }
		public TerminalNode PUML_CONSTANT_FEED_RUN_ID() { return getToken(SQLSelectParserParser.PUML_CONSTANT_FEED_RUN_ID, 0); }
		public TerminalNode PUML_CONSTANT_FEED_NAME() { return getToken(SQLSelectParserParser.PUML_CONSTANT_FEED_NAME, 0); }
		public TerminalNode PUML_CONSTANT_TRANSACTION_RUN_ID() { return getToken(SQLSelectParserParser.PUML_CONSTANT_TRANSACTION_RUN_ID, 0); }
		public TerminalNode PUML_CONSTANT_TRANSACTION_NAME() { return getToken(SQLSelectParserParser.PUML_CONSTANT_TRANSACTION_NAME, 0); }
		public TerminalNode PUML_CONSTANT_POPULATION() { return getToken(SQLSelectParserParser.PUML_CONSTANT_POPULATION, 0); }
		public TerminalNode PUML_CONSTANT_TARGET_MODEL_NAME() { return getToken(SQLSelectParserParser.PUML_CONSTANT_TARGET_MODEL_NAME, 0); }
		public TerminalNode PUML_CONSTANT_TENANT_SALT() { return getToken(SQLSelectParserParser.PUML_CONSTANT_TENANT_SALT, 0); }
		public TerminalNode PUML_CONSTANT_PIT_START_TIME() { return getToken(SQLSelectParserParser.PUML_CONSTANT_PIT_START_TIME, 0); }
		public TerminalNode PUML_CONSTANT_PIT_END_TIME() { return getToken(SQLSelectParserParser.PUML_CONSTANT_PIT_END_TIME, 0); }
		public Puml_constant_identifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_puml_constant_identifier; }
	}

	public final Puml_constant_identifierContext puml_constant_identifier() throws RecognitionException {
		Puml_constant_identifierContext _localctx = new Puml_constant_identifierContext(_ctx, getState());
		enterRule(_localctx, 404, RULE_puml_constant_identifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1598);
			_la = _input.LA(1);
			if ( !(((((_la - 298)) & ~0x3f) == 0 && ((1L << (_la - 298)) & 33554431L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u0151\u0641\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007"+
		"\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007"+
		"\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007"+
		"\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007"+
		"\u001b\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007"+
		"\u001e\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007"+
		"\"\u0002#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007"+
		"\'\u0002(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0002,\u0007"+
		",\u0002-\u0007-\u0002.\u0007.\u0002/\u0007/\u00020\u00070\u00021\u0007"+
		"1\u00022\u00072\u00023\u00073\u00024\u00074\u00025\u00075\u00026\u0007"+
		"6\u00027\u00077\u00028\u00078\u00029\u00079\u0002:\u0007:\u0002;\u0007"+
		";\u0002<\u0007<\u0002=\u0007=\u0002>\u0007>\u0002?\u0007?\u0002@\u0007"+
		"@\u0002A\u0007A\u0002B\u0007B\u0002C\u0007C\u0002D\u0007D\u0002E\u0007"+
		"E\u0002F\u0007F\u0002G\u0007G\u0002H\u0007H\u0002I\u0007I\u0002J\u0007"+
		"J\u0002K\u0007K\u0002L\u0007L\u0002M\u0007M\u0002N\u0007N\u0002O\u0007"+
		"O\u0002P\u0007P\u0002Q\u0007Q\u0002R\u0007R\u0002S\u0007S\u0002T\u0007"+
		"T\u0002U\u0007U\u0002V\u0007V\u0002W\u0007W\u0002X\u0007X\u0002Y\u0007"+
		"Y\u0002Z\u0007Z\u0002[\u0007[\u0002\\\u0007\\\u0002]\u0007]\u0002^\u0007"+
		"^\u0002_\u0007_\u0002`\u0007`\u0002a\u0007a\u0002b\u0007b\u0002c\u0007"+
		"c\u0002d\u0007d\u0002e\u0007e\u0002f\u0007f\u0002g\u0007g\u0002h\u0007"+
		"h\u0002i\u0007i\u0002j\u0007j\u0002k\u0007k\u0002l\u0007l\u0002m\u0007"+
		"m\u0002n\u0007n\u0002o\u0007o\u0002p\u0007p\u0002q\u0007q\u0002r\u0007"+
		"r\u0002s\u0007s\u0002t\u0007t\u0002u\u0007u\u0002v\u0007v\u0002w\u0007"+
		"w\u0002x\u0007x\u0002y\u0007y\u0002z\u0007z\u0002{\u0007{\u0002|\u0007"+
		"|\u0002}\u0007}\u0002~\u0007~\u0002\u007f\u0007\u007f\u0002\u0080\u0007"+
		"\u0080\u0002\u0081\u0007\u0081\u0002\u0082\u0007\u0082\u0002\u0083\u0007"+
		"\u0083\u0002\u0084\u0007\u0084\u0002\u0085\u0007\u0085\u0002\u0086\u0007"+
		"\u0086\u0002\u0087\u0007\u0087\u0002\u0088\u0007\u0088\u0002\u0089\u0007"+
		"\u0089\u0002\u008a\u0007\u008a\u0002\u008b\u0007\u008b\u0002\u008c\u0007"+
		"\u008c\u0002\u008d\u0007\u008d\u0002\u008e\u0007\u008e\u0002\u008f\u0007"+
		"\u008f\u0002\u0090\u0007\u0090\u0002\u0091\u0007\u0091\u0002\u0092\u0007"+
		"\u0092\u0002\u0093\u0007\u0093\u0002\u0094\u0007\u0094\u0002\u0095\u0007"+
		"\u0095\u0002\u0096\u0007\u0096\u0002\u0097\u0007\u0097\u0002\u0098\u0007"+
		"\u0098\u0002\u0099\u0007\u0099\u0002\u009a\u0007\u009a\u0002\u009b\u0007"+
		"\u009b\u0002\u009c\u0007\u009c\u0002\u009d\u0007\u009d\u0002\u009e\u0007"+
		"\u009e\u0002\u009f\u0007\u009f\u0002\u00a0\u0007\u00a0\u0002\u00a1\u0007"+
		"\u00a1\u0002\u00a2\u0007\u00a2\u0002\u00a3\u0007\u00a3\u0002\u00a4\u0007"+
		"\u00a4\u0002\u00a5\u0007\u00a5\u0002\u00a6\u0007\u00a6\u0002\u00a7\u0007"+
		"\u00a7\u0002\u00a8\u0007\u00a8\u0002\u00a9\u0007\u00a9\u0002\u00aa\u0007"+
		"\u00aa\u0002\u00ab\u0007\u00ab\u0002\u00ac\u0007\u00ac\u0002\u00ad\u0007"+
		"\u00ad\u0002\u00ae\u0007\u00ae\u0002\u00af\u0007\u00af\u0002\u00b0\u0007"+
		"\u00b0\u0002\u00b1\u0007\u00b1\u0002\u00b2\u0007\u00b2\u0002\u00b3\u0007"+
		"\u00b3\u0002\u00b4\u0007\u00b4\u0002\u00b5\u0007\u00b5\u0002\u00b6\u0007"+
		"\u00b6\u0002\u00b7\u0007\u00b7\u0002\u00b8\u0007\u00b8\u0002\u00b9\u0007"+
		"\u00b9\u0002\u00ba\u0007\u00ba\u0002\u00bb\u0007\u00bb\u0002\u00bc\u0007"+
		"\u00bc\u0002\u00bd\u0007\u00bd\u0002\u00be\u0007\u00be\u0002\u00bf\u0007"+
		"\u00bf\u0002\u00c0\u0007\u00c0\u0002\u00c1\u0007\u00c1\u0002\u00c2\u0007"+
		"\u00c2\u0002\u00c3\u0007\u00c3\u0002\u00c4\u0007\u00c4\u0002\u00c5\u0007"+
		"\u00c5\u0002\u00c6\u0007\u00c6\u0002\u00c7\u0007\u00c7\u0002\u00c8\u0007"+
		"\u00c8\u0002\u00c9\u0007\u00c9\u0002\u00ca\u0007\u00ca\u0001\u0000\u0001"+
		"\u0000\u0003\u0000\u0199\b\u0000\u0001\u0000\u0003\u0000\u019c\b\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001"+
		"\b\u0003\b\u01b7\b\b\u0001\b\u0001\b\u0001\t\u0003\t\u01bc\b\t\u0001\t"+
		"\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0005\n\u01c4\b\n\n\n\f\n\u01c7"+
		"\t\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0003\r\u01d4\b\r\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0003\u000e\u01dd\b\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0003\u000e"+
		"\u01e2\b\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0003\u000f\u01e9\b\u000f\u0001\u000f\u0003\u000f\u01ec\b\u000f\u0001"+
		"\u000f\u0003\u000f\u01ef\b\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0011\u0001\u0011\u0001\u0011\u0005\u0011\u01f7\b\u0011\n\u0011\f\u0011"+
		"\u01fa\t\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0005\u0015\u020b\b\u0015"+
		"\n\u0015\f\u0015\u020e\t\u0015\u0001\u0016\u0001\u0016\u0003\u0016\u0212"+
		"\b\u0016\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001"+
		"\u0018\u0005\u0018\u021a\b\u0018\n\u0018\f\u0018\u021d\t\u0018\u0001\u0019"+
		"\u0001\u0019\u0003\u0019\u0221\b\u0019\u0001\u001a\u0001\u001a\u0001\u001b"+
		"\u0001\u001b\u0001\u001b\u0003\u001b\u0228\b\u001b\u0001\u001c\u0001\u001c"+
		"\u0001\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0003\u001d\u0230\b\u001d"+
		"\u0001\u001d\u0003\u001d\u0233\b\u001d\u0001\u001d\u0001\u001d\u0001\u001d"+
		"\u0003\u001d\u0238\b\u001d\u0001\u001d\u0003\u001d\u023b\b\u001d\u0001"+
		"\u001d\u0003\u001d\u023e\b\u001d\u0001\u001d\u0003\u001d\u0241\b\u001d"+
		"\u0001\u001d\u0003\u001d\u0244\b\u001d\u0003\u001d\u0246\b\u001d\u0001"+
		"\u001e\u0001\u001e\u0001\u001e\u0001\u001f\u0001\u001f\u0001 \u0001 \u0001"+
		" \u0005 \u0250\b \n \f \u0253\t \u0001!\u0001!\u0003!\u0257\b!\u0001!"+
		"\u0003!\u025a\b!\u0001\"\u0003\"\u025d\b\"\u0001\"\u0001\"\u0001#\u0001"+
		"#\u0003#\u0263\b#\u0001#\u0001#\u0001$\u0001$\u0001$\u0003$\u026a\b$\u0001"+
		"%\u0001%\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001"+
		"&\u0003&\u0277\b&\u0005&\u0279\b&\n&\f&\u027c\t&\u0001\'\u0001\'\u0001"+
		"\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0003\'\u0286\b\'\u0005\'\u0288"+
		"\b\'\n\'\f\'\u028b\t\'\u0001\'\u0003\'\u028e\b\'\u0001(\u0001(\u0003("+
		"\u0292\b(\u0001(\u0001(\u0003(\u0296\b(\u0001(\u0001(\u0001(\u0001(\u0003"+
		"(\u029c\b(\u0001)\u0001)\u0001)\u0001)\u0003)\u02a2\b)\u0001*\u0001*\u0001"+
		"*\u0001*\u0003*\u02a8\b*\u0003*\u02aa\b*\u0001*\u0001*\u0001*\u0003*\u02af"+
		"\b*\u0003*\u02b1\b*\u0001+\u0001+\u0001+\u0001+\u0001+\u0001+\u0003+\u02b9"+
		"\b+\u0001+\u0003+\u02bc\b+\u0001,\u0003,\u02bf\b,\u0001,\u0001,\u0001"+
		"-\u0001-\u0001-\u0003-\u02c6\b-\u0003-\u02c8\b-\u0001.\u0001.\u0003.\u02cc"+
		"\b.\u0001/\u0001/\u0001/\u00010\u00010\u00010\u00010\u00010\u00011\u0001"+
		"1\u00012\u00012\u00012\u00052\u02db\b2\n2\f2\u02de\t2\u00013\u00013\u0001"+
		"3\u00033\u02e3\b3\u00013\u00013\u00013\u00013\u00013\u00033\u02ea\b3\u0001"+
		"4\u00014\u00014\u00034\u02ef\b4\u00014\u00014\u00014\u00014\u00014\u0001"+
		"4\u00034\u02f7\b4\u00015\u00015\u00015\u00035\u02fc\b5\u00015\u00015\u0001"+
		"5\u00015\u00035\u0302\b5\u00016\u00016\u00036\u0306\b6\u00017\u00017\u0001"+
		"7\u00017\u00018\u00018\u00018\u00018\u00018\u00018\u00018\u00018\u0003"+
		"8\u0314\b8\u00019\u00019\u00019\u00019\u00019\u00019\u00039\u031c\b9\u0001"+
		"9\u00019\u00039\u0320\b9\u00019\u00019\u00019\u00039\u0325\b9\u0001:\u0001"+
		":\u0001;\u0001;\u0001<\u0001<\u0001<\u0001<\u0003<\u032f\b<\u0001<\u0001"+
		"<\u0001<\u0001<\u0001<\u0003<\u0336\b<\u0001<\u0001<\u0003<\u033a\b<\u0001"+
		"=\u0004=\u033d\b=\u000b=\f=\u033e\u0001>\u0001>\u0001>\u0001>\u0001>\u0001"+
		"?\u0004?\u0347\b?\u000b?\f?\u0348\u0001@\u0001@\u0001@\u0001@\u0001@\u0001"+
		"A\u0001A\u0001A\u0001B\u0001B\u0003B\u0355\bB\u0001C\u0001C\u0001D\u0001"+
		"D\u0001D\u0001D\u0001D\u0001D\u0001D\u0001E\u0001E\u0001F\u0001F\u0001"+
		"F\u0001G\u0001G\u0001G\u0003G\u0368\bG\u0001G\u0001G\u0001G\u0001G\u0001"+
		"G\u0001G\u0001G\u0003G\u0371\bG\u0001G\u0003G\u0374\bG\u0003G\u0376\b"+
		"G\u0001H\u0001H\u0001H\u0003H\u037b\bH\u0001H\u0003H\u037e\bH\u0001H\u0003"+
		"H\u0381\bH\u0001H\u0001H\u0001I\u0001I\u0001I\u0001I\u0001J\u0001J\u0001"+
		"J\u0001K\u0001K\u0001L\u0001L\u0001L\u0003L\u0391\bL\u0001M\u0001M\u0001"+
		"M\u0001M\u0001M\u0001N\u0001N\u0001N\u0003N\u039b\bN\u0001O\u0001O\u0001"+
		"O\u0001P\u0001P\u0001P\u0001Q\u0001Q\u0001Q\u0001R\u0001R\u0001S\u0001"+
		"S\u0001T\u0001T\u0001T\u0001U\u0001U\u0001U\u0001V\u0001V\u0001V\u0001"+
		"V\u0003V\u03b4\bV\u0001W\u0001W\u0001W\u0003W\u03b9\bW\u0001X\u0001X\u0001"+
		"X\u0005X\u03be\bX\nX\fX\u03c1\tX\u0001Y\u0001Y\u0001Y\u0005Y\u03c6\bY"+
		"\nY\fY\u03c9\tY\u0001Z\u0003Z\u03cc\bZ\u0001Z\u0001Z\u0001[\u0001[\u0003"+
		"[\u03d2\b[\u0001\\\u0001\\\u0001]\u0001]\u0001]\u0001]\u0001]\u0001]\u0001"+
		"]\u0001^\u0001^\u0001^\u0003^\u03e0\b^\u0001_\u0001_\u0001`\u0001`\u0003"+
		"`\u03e6\b`\u0001a\u0001a\u0001a\u0005a\u03eb\ba\na\fa\u03ee\ta\u0001b"+
		"\u0001b\u0003b\u03f2\bb\u0001c\u0001c\u0001c\u0001c\u0001c\u0001d\u0001"+
		"d\u0001e\u0003e\u03fc\be\u0001e\u0003e\u03ff\be\u0001e\u0003e\u0402\b"+
		"e\u0001e\u0001e\u0001e\u0001e\u0001e\u0003e\u0409\be\u0001f\u0001f\u0001"+
		"g\u0001g\u0001h\u0001h\u0001h\u0005h\u0412\bh\nh\fh\u0415\th\u0001i\u0001"+
		"i\u0001i\u0005i\u041a\bi\ni\fi\u041d\ti\u0001j\u0003j\u0420\bj\u0001j"+
		"\u0001j\u0001k\u0001k\u0001k\u0001k\u0001k\u0001k\u0003k\u042a\bk\u0003"+
		"k\u042c\bk\u0001l\u0001l\u0003l\u0430\bl\u0001m\u0001m\u0001m\u0001m\u0001"+
		"m\u0001m\u0001m\u0003m\u0439\bm\u0001n\u0001n\u0001o\u0001o\u0003o\u043f"+
		"\bo\u0001p\u0001p\u0001p\u0003p\u0444\bp\u0001q\u0001q\u0001q\u0001r\u0001"+
		"r\u0001s\u0001s\u0001s\u0001s\u0001t\u0001t\u0001t\u0005t\u0452\bt\nt"+
		"\ft\u0455\tt\u0001u\u0001u\u0003u\u0459\bu\u0001u\u0003u\u045c\bu\u0001"+
		"v\u0001v\u0001w\u0001w\u0001w\u0001x\u0001x\u0001y\u0001y\u0001y\u0001"+
		"y\u0003y\u0469\by\u0001z\u0001z\u0001z\u0001z\u0003z\u046f\bz\u0001{\u0001"+
		"{\u0001{\u0005{\u0474\b{\n{\f{\u0477\t{\u0001|\u0001|\u0001|\u0001|\u0003"+
		"|\u047d\b|\u0001}\u0001}\u0001}\u0005}\u0482\b}\n}\f}\u0485\t}\u0001~"+
		"\u0001~\u0001~\u0001~\u0001~\u0003~\u048c\b~\u0001\u007f\u0001\u007f\u0001"+
		"\u007f\u0001\u007f\u0001\u007f\u0001\u0080\u0001\u0080\u0001\u0080\u0001"+
		"\u0080\u0001\u0080\u0001\u0081\u0001\u0081\u0001\u0081\u0001\u0082\u0001"+
		"\u0082\u0001\u0082\u0001\u0083\u0001\u0083\u0001\u0083\u0005\u0083\u04a1"+
		"\b\u0083\n\u0083\f\u0083\u04a4\t\u0083\u0001\u0084\u0001\u0084\u0001\u0084"+
		"\u0001\u0084\u0001\u0085\u0001\u0085\u0003\u0085\u04ac\b\u0085\u0001\u0085"+
		"\u0001\u0085\u0003\u0085\u04b0\b\u0085\u0001\u0086\u0001\u0086\u0001\u0086"+
		"\u0001\u0086\u0001\u0086\u0001\u0086\u0003\u0086\u04b8\b\u0086\u0001\u0087"+
		"\u0001\u0087\u0001\u0088\u0001\u0088\u0001\u0089\u0001\u0089\u0003\u0089"+
		"\u04c0\b\u0089\u0001\u0089\u0001\u0089\u0003\u0089\u04c4\b\u0089\u0001"+
		"\u0089\u0001\u0089\u0001\u0089\u0001\u0089\u0001\u008a\u0001\u008a\u0001"+
		"\u008b\u0001\u008b\u0003\u008b\u04ce\b\u008b\u0001\u008b\u0001\u008b\u0001"+
		"\u008b\u0001\u008c\u0001\u008c\u0003\u008c\u04d5\b\u008c\u0001\u008c\u0001"+
		"\u008c\u0001\u008c\u0001\u008d\u0001\u008d\u0001\u008d\u0001\u008e\u0001"+
		"\u008e\u0001\u008e\u0001\u008e\u0001\u008e\u0001\u008e\u0003\u008e\u04e3"+
		"\b\u008e\u0001\u008f\u0001\u008f\u0001\u008f\u0005\u008f\u04e8\b\u008f"+
		"\n\u008f\f\u008f\u04eb\t\u008f\u0001\u0090\u0001\u0090\u0001\u0090\u0001"+
		"\u0091\u0001\u0091\u0001\u0091\u0003\u0091\u04f3\b\u0091\u0001\u0092\u0001"+
		"\u0092\u0001\u0092\u0001\u0092\u0001\u0093\u0001\u0093\u0001\u0093\u0001"+
		"\u0094\u0001\u0094\u0001\u0094\u0001\u0094\u0001\u0094\u0001\u0095\u0001"+
		"\u0095\u0001\u0095\u0005\u0095\u0504\b\u0095\n\u0095\f\u0095\u0507\t\u0095"+
		"\u0001\u0096\u0001\u0096\u0001\u0096\u0001\u0096\u0001\u0097\u0001\u0097"+
		"\u0001\u0097\u0001\u0097\u0001\u0098\u0001\u0098\u0001\u0098\u0001\u0099"+
		"\u0001\u0099\u0003\u0099\u0516\b\u0099\u0001\u0099\u0001\u0099\u0001\u009a"+
		"\u0001\u009a\u0003\u009a\u051c\b\u009a\u0001\u009a\u0001\u009a\u0001\u009b"+
		"\u0001\u009b\u0001\u009c\u0001\u009c\u0001\u009d\u0001\u009d\u0001\u009d"+
		"\u0001\u009d\u0001\u009d\u0001\u009e\u0001\u009e\u0003\u009e\u052b\b\u009e"+
		"\u0001\u009f\u0001\u009f\u0001\u00a0\u0001\u00a0\u0001\u00a1\u0003\u00a1"+
		"\u0532\b\u00a1\u0001\u00a1\u0001\u00a1\u0001\u00a1\u0001\u00a2\u0001\u00a2"+
		"\u0001\u00a2\u0001\u00a3\u0001\u00a3\u0003\u00a3\u053c\b\u00a3\u0001\u00a4"+
		"\u0001\u00a4\u0001\u00a5\u0001\u00a5\u0001\u00a6\u0001\u00a6\u0001\u00a6"+
		"\u0003\u00a6\u0545\b\u00a6\u0001\u00a6\u0001\u00a6\u0001\u00a7\u0001\u00a7"+
		"\u0001\u00a7\u0003\u00a7\u054c\b\u00a7\u0001\u00a7\u0003\u00a7\u054f\b"+
		"\u00a7\u0001\u00a8\u0001\u00a8\u0001\u00a9\u0001\u00a9\u0001\u00a9\u0005"+
		"\u00a9\u0556\b\u00a9\n\u00a9\f\u00a9\u0559\t\u00a9\u0001\u00aa\u0001\u00aa"+
		"\u0001\u00aa\u0001\u00aa\u0001\u00aa\u0003\u00aa\u0560\b\u00aa\u0001\u00ab"+
		"\u0001\u00ab\u0001\u00ab\u0001\u00ab\u0001\u00ab\u0003\u00ab\u0567\b\u00ab"+
		"\u0001\u00ac\u0001\u00ac\u0003\u00ac\u056b\b\u00ac\u0001\u00ad\u0001\u00ad"+
		"\u0001\u00ae\u0001\u00ae\u0001\u00af\u0001\u00af\u0001\u00b0\u0001\u00b0"+
		"\u0001\u00b1\u0001\u00b1\u0001\u00b2\u0001\u00b2\u0001\u00b3\u0001\u00b3"+
		"\u0001\u00b4\u0001\u00b4\u0001\u00b5\u0003\u00b5\u057e\b\u00b5\u0001\u00b5"+
		"\u0001\u00b5\u0001\u00b6\u0001\u00b6\u0003\u00b6\u0584\b\u00b6\u0001\u00b7"+
		"\u0001\u00b7\u0003\u00b7\u0588\b\u00b7\u0001\u00b8\u0001\u00b8\u0001\u00b8"+
		"\u0003\u00b8\u058d\b\u00b8\u0001\u00b8\u0003\u00b8\u0590\b\u00b8\u0001"+
		"\u00b8\u0001\u00b8\u0001\u00b8\u0003\u00b8\u0595\b\u00b8\u0001\u00b8\u0001"+
		"\u00b8\u0003\u00b8\u0599\b\u00b8\u0001\u00b9\u0001\u00b9\u0001\u00b9\u0001"+
		"\u00ba\u0001\u00ba\u0001\u00ba\u0003\u00ba\u05a1\b\u00ba\u0001\u00bb\u0001"+
		"\u00bb\u0001\u00bc\u0001\u00bc\u0001\u00bc\u0003\u00bc\u05a8\b\u00bc\u0001"+
		"\u00bd\u0001\u00bd\u0001\u00bd\u0001\u00be\u0001\u00be\u0001\u00be\u0001"+
		"\u00bf\u0001\u00bf\u0001\u00bf\u0001\u00c0\u0001\u00c0\u0001\u00c1\u0001"+
		"\u00c1\u0001\u00c1\u0003\u00c1\u05b8\b\u00c1\u0001\u00c2\u0001\u00c2\u0003"+
		"\u00c2\u05bc\b\u00c2\u0001\u00c3\u0001\u00c3\u0001\u00c3\u0001\u00c3\u0001"+
		"\u00c3\u0001\u00c3\u0001\u00c3\u0001\u00c3\u0001\u00c3\u0001\u00c3\u0001"+
		"\u00c3\u0001\u00c3\u0001\u00c3\u0001\u00c3\u0001\u00c3\u0001\u00c3\u0001"+
		"\u00c3\u0001\u00c3\u0001\u00c3\u0001\u00c3\u0001\u00c3\u0001\u00c3\u0001"+
		"\u00c3\u0001\u00c3\u0001\u00c3\u0001\u00c3\u0001\u00c3\u0001\u00c3\u0001"+
		"\u00c3\u0001\u00c3\u0001\u00c3\u0001\u00c3\u0001\u00c3\u0001\u00c3\u0003"+
		"\u00c3\u05e0\b\u00c3\u0001\u00c4\u0001\u00c4\u0001\u00c4\u0001\u00c4\u0001"+
		"\u00c5\u0001\u00c5\u0003\u00c5\u05e8\b\u00c5\u0001\u00c6\u0001\u00c6\u0001"+
		"\u00c6\u0001\u00c6\u0001\u00c6\u0001\u00c6\u0001\u00c6\u0001\u00c6\u0003"+
		"\u00c6\u05f2\b\u00c6\u0001\u00c7\u0001\u00c7\u0001\u00c7\u0001\u00c7\u0001"+
		"\u00c7\u0001\u00c7\u0001\u00c7\u0001\u00c7\u0003\u00c7\u05fc\b\u00c7\u0001"+
		"\u00c8\u0001\u00c8\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001"+
		"\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001"+
		"\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001"+
		"\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001"+
		"\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001"+
		"\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001"+
		"\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001"+
		"\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001"+
		"\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001"+
		"\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001"+
		"\u00c9\u0001\u00c9\u0001\u00c9\u0003\u00c9\u063d\b\u00c9\u0001\u00ca\u0001"+
		"\u00ca\u0001\u00ca\u0000\u0000\u00cb\u0000\u0002\u0004\u0006\b\n\f\u000e"+
		"\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BDF"+
		"HJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086\u0088\u008a\u008c"+
		"\u008e\u0090\u0092\u0094\u0096\u0098\u009a\u009c\u009e\u00a0\u00a2\u00a4"+
		"\u00a6\u00a8\u00aa\u00ac\u00ae\u00b0\u00b2\u00b4\u00b6\u00b8\u00ba\u00bc"+
		"\u00be\u00c0\u00c2\u00c4\u00c6\u00c8\u00ca\u00cc\u00ce\u00d0\u00d2\u00d4"+
		"\u00d6\u00d8\u00da\u00dc\u00de\u00e0\u00e2\u00e4\u00e6\u00e8\u00ea\u00ec"+
		"\u00ee\u00f0\u00f2\u00f4\u00f6\u00f8\u00fa\u00fc\u00fe\u0100\u0102\u0104"+
		"\u0106\u0108\u010a\u010c\u010e\u0110\u0112\u0114\u0116\u0118\u011a\u011c"+
		"\u011e\u0120\u0122\u0124\u0126\u0128\u012a\u012c\u012e\u0130\u0132\u0134"+
		"\u0136\u0138\u013a\u013c\u013e\u0140\u0142\u0144\u0146\u0148\u014a\u014c"+
		"\u014e\u0150\u0152\u0154\u0156\u0158\u015a\u015c\u015e\u0160\u0162\u0164"+
		"\u0166\u0168\u016a\u016c\u016e\u0170\u0172\u0174\u0176\u0178\u017a\u017c"+
		"\u017e\u0180\u0182\u0184\u0186\u0188\u018a\u018c\u018e\u0190\u0192\u0194"+
		"\u0000\u001d\u0002\u0000\u000e\u000e55\u0002\u0000\u0002\u0002\u000b\u000b"+
		"\u0003\u0000\u0010\u0010\u001d\u001d++\u000e\u0000==EFWWccefjjooss~~\u0082"+
		"\u0082\u0088\u0089\u008b\u008b\u0097\u0098\u009d\u00cc\u0006\u0000\u0004"+
		"\u0004..BBQQZZ``\u0002\u0000\b\b44\u0002\u0000}}\u0084\u0084\u0002\u0000"+
		"\u0094\u0094\u0129\u0129\u0004\u0000WWccefss\u0002\u0000VVdd\u0002\u0000"+
		"\u0014\u0014**\u0001\u0000\u011e\u011f\u0001\u0000\u0120\u0122\u0001\u0000"+
		"\u008e\u0090\u0003\u0000\u0006\u0006\u001c\u001c22\u0002\u0000<<LL\u0001"+
		"\u0000\u010d\u0110\u0002\u0000\u0112\u0112\u0117\u011b\u0002\u0000\u0005"+
		"\u0005//\u0002\u0000\u0015\u0015\u001e\u001e\u0003\u0000\u000f\u000f3"+
		"3\u0095\u0095\u0002\u0000\u0004\u0004..\u0004\u0000II]]pq\u009b\u009b"+
		"\b\u0000@@KKMNPPabln||\u009a\u009a\u0003\u0000\u0016\u0016\u001d\u001d"+
		"++\u0001\u0000\u0145\u0146\u0002\u0000\u0129\u0129\u0149\u0149\u0015\u0000"+
		"\u0014\u0014#$**,,55::<EGGIVY\\^bddgrtxz\u0081\u0084\u0093\u0095\u00cf"+
		"\u00d1\u00e8\u00ea\u00f4\u00f6\u00f7\u00f9\u010c\u0001\u0000\u012a\u0142"+
		"\u0689\u0000\u0198\u0001\u0000\u0000\u0000\u0002\u019f\u0001\u0000\u0000"+
		"\u0000\u0004\u01a2\u0001\u0000\u0000\u0000\u0006\u01a5\u0001\u0000\u0000"+
		"\u0000\b\u01a8\u0001\u0000\u0000\u0000\n\u01ab\u0001\u0000\u0000\u0000"+
		"\f\u01ae\u0001\u0000\u0000\u0000\u000e\u01b1\u0001\u0000\u0000\u0000\u0010"+
		"\u01b6\u0001\u0000\u0000\u0000\u0012\u01bb\u0001\u0000\u0000\u0000\u0014"+
		"\u01bf\u0001\u0000\u0000\u0000\u0016\u01c8\u0001\u0000\u0000\u0000\u0018"+
		"\u01cd\u0001\u0000\u0000\u0000\u001a\u01d3\u0001\u0000\u0000\u0000\u001c"+
		"\u01d5\u0001\u0000\u0000\u0000\u001e\u01e3\u0001\u0000\u0000\u0000 \u01f0"+
		"\u0001\u0000\u0000\u0000\"\u01f3\u0001\u0000\u0000\u0000$\u01fb\u0001"+
		"\u0000\u0000\u0000&\u01ff\u0001\u0000\u0000\u0000(\u0204\u0001\u0000\u0000"+
		"\u0000*\u0206\u0001\u0000\u0000\u0000,\u020f\u0001\u0000\u0000\u0000."+
		"\u0213\u0001\u0000\u0000\u00000\u0215\u0001\u0000\u0000\u00002\u021e\u0001"+
		"\u0000\u0000\u00004\u0222\u0001\u0000\u0000\u00006\u0227\u0001\u0000\u0000"+
		"\u00008\u0229\u0001\u0000\u0000\u0000:\u022d\u0001\u0000\u0000\u0000<"+
		"\u0247\u0001\u0000\u0000\u0000>\u024a\u0001\u0000\u0000\u0000@\u024c\u0001"+
		"\u0000\u0000\u0000B\u0259\u0001\u0000\u0000\u0000D\u025c\u0001\u0000\u0000"+
		"\u0000F\u0262\u0001\u0000\u0000\u0000H\u0266\u0001\u0000\u0000\u0000J"+
		"\u026b\u0001\u0000\u0000\u0000L\u026d\u0001\u0000\u0000\u0000N\u0289\u0001"+
		"\u0000\u0000\u0000P\u029b\u0001\u0000\u0000\u0000R\u02a1\u0001\u0000\u0000"+
		"\u0000T\u02a3\u0001\u0000\u0000\u0000V\u02bb\u0001\u0000\u0000\u0000X"+
		"\u02be\u0001\u0000\u0000\u0000Z\u02c7\u0001\u0000\u0000\u0000\\\u02cb"+
		"\u0001\u0000\u0000\u0000^\u02cd\u0001\u0000\u0000\u0000`\u02d0\u0001\u0000"+
		"\u0000\u0000b\u02d5\u0001\u0000\u0000\u0000d\u02d7\u0001\u0000\u0000\u0000"+
		"f\u02e9\u0001\u0000\u0000\u0000h\u02f6\u0001\u0000\u0000\u0000j\u0301"+
		"\u0001\u0000\u0000\u0000l\u0305\u0001\u0000\u0000\u0000n\u0307\u0001\u0000"+
		"\u0000\u0000p\u0313\u0001\u0000\u0000\u0000r\u0324\u0001\u0000\u0000\u0000"+
		"t\u0326\u0001\u0000\u0000\u0000v\u0328\u0001\u0000\u0000\u0000x\u0339"+
		"\u0001\u0000\u0000\u0000z\u033c\u0001\u0000\u0000\u0000|\u0340\u0001\u0000"+
		"\u0000\u0000~\u0346\u0001\u0000\u0000\u0000\u0080\u034a\u0001\u0000\u0000"+
		"\u0000\u0082\u034f\u0001\u0000\u0000\u0000\u0084\u0354\u0001\u0000\u0000"+
		"\u0000\u0086\u0356\u0001\u0000\u0000\u0000\u0088\u0358\u0001\u0000\u0000"+
		"\u0000\u008a\u035f\u0001\u0000\u0000\u0000\u008c\u0361\u0001\u0000\u0000"+
		"\u0000\u008e\u0375\u0001\u0000\u0000\u0000\u0090\u0377\u0001\u0000\u0000"+
		"\u0000\u0092\u0384\u0001\u0000\u0000\u0000\u0094\u0388\u0001\u0000\u0000"+
		"\u0000\u0096\u038b\u0001\u0000\u0000\u0000\u0098\u0390\u0001\u0000\u0000"+
		"\u0000\u009a\u0392\u0001\u0000\u0000\u0000\u009c\u039a\u0001\u0000\u0000"+
		"\u0000\u009e\u039c\u0001\u0000\u0000\u0000\u00a0\u039f\u0001\u0000\u0000"+
		"\u0000\u00a2\u03a2\u0001\u0000\u0000\u0000\u00a4\u03a5\u0001\u0000\u0000"+
		"\u0000\u00a6\u03a7\u0001\u0000\u0000\u0000\u00a8\u03a9\u0001\u0000\u0000"+
		"\u0000\u00aa\u03ac\u0001\u0000\u0000\u0000\u00ac\u03b3\u0001\u0000\u0000"+
		"\u0000\u00ae\u03b8\u0001\u0000\u0000\u0000\u00b0\u03ba\u0001\u0000\u0000"+
		"\u0000\u00b2\u03c2\u0001\u0000\u0000\u0000\u00b4\u03cb\u0001\u0000\u0000"+
		"\u0000\u00b6\u03d1\u0001\u0000\u0000\u0000\u00b8\u03d3\u0001\u0000\u0000"+
		"\u0000\u00ba\u03d5\u0001\u0000\u0000\u0000\u00bc\u03df\u0001\u0000\u0000"+
		"\u0000\u00be\u03e1\u0001\u0000\u0000\u0000\u00c0\u03e5\u0001\u0000\u0000"+
		"\u0000\u00c2\u03e7\u0001\u0000\u0000\u0000\u00c4\u03f1\u0001\u0000\u0000"+
		"\u0000\u00c6\u03f3\u0001\u0000\u0000\u0000\u00c8\u03f8\u0001\u0000\u0000"+
		"\u0000\u00ca\u0408\u0001\u0000\u0000\u0000\u00cc\u040a\u0001\u0000\u0000"+
		"\u0000\u00ce\u040c\u0001\u0000\u0000\u0000\u00d0\u040e\u0001\u0000\u0000"+
		"\u0000\u00d2\u0416\u0001\u0000\u0000\u0000\u00d4\u041f\u0001\u0000\u0000"+
		"\u0000\u00d6\u042b\u0001\u0000\u0000\u0000\u00d8\u042f\u0001\u0000\u0000"+
		"\u0000\u00da\u0438\u0001\u0000\u0000\u0000\u00dc\u043a\u0001\u0000\u0000"+
		"\u0000\u00de\u043e\u0001\u0000\u0000\u0000\u00e0\u0443\u0001\u0000\u0000"+
		"\u0000\u00e2\u0445\u0001\u0000\u0000\u0000\u00e4\u0448\u0001\u0000\u0000"+
		"\u0000\u00e6\u044a\u0001\u0000\u0000\u0000\u00e8\u044e\u0001\u0000\u0000"+
		"\u0000\u00ea\u0456\u0001\u0000\u0000\u0000\u00ec\u045d\u0001\u0000\u0000"+
		"\u0000\u00ee\u045f\u0001\u0000\u0000\u0000\u00f0\u0462\u0001\u0000\u0000"+
		"\u0000\u00f2\u0464\u0001\u0000\u0000\u0000\u00f4\u046a\u0001\u0000\u0000"+
		"\u0000\u00f6\u0470\u0001\u0000\u0000\u0000\u00f8\u047c\u0001\u0000\u0000"+
		"\u0000\u00fa\u047e\u0001\u0000\u0000\u0000\u00fc\u048b\u0001\u0000\u0000"+
		"\u0000\u00fe\u048d\u0001\u0000\u0000\u0000\u0100\u0492\u0001\u0000\u0000"+
		"\u0000\u0102\u0497\u0001\u0000\u0000\u0000\u0104\u049a\u0001\u0000\u0000"+
		"\u0000\u0106\u049d\u0001\u0000\u0000\u0000\u0108\u04a5\u0001\u0000\u0000"+
		"\u0000\u010a\u04af\u0001\u0000\u0000\u0000\u010c\u04b7\u0001\u0000\u0000"+
		"\u0000\u010e\u04b9\u0001\u0000\u0000\u0000\u0110\u04bb\u0001\u0000\u0000"+
		"\u0000\u0112\u04bd\u0001\u0000\u0000\u0000\u0114\u04c9\u0001\u0000\u0000"+
		"\u0000\u0116\u04cb\u0001\u0000\u0000\u0000\u0118\u04d2\u0001\u0000\u0000"+
		"\u0000\u011a\u04d9\u0001\u0000\u0000\u0000\u011c\u04e2\u0001\u0000\u0000"+
		"\u0000\u011e\u04e4\u0001\u0000\u0000\u0000\u0120\u04ec\u0001\u0000\u0000"+
		"\u0000\u0122\u04f2\u0001\u0000\u0000\u0000\u0124\u04f4\u0001\u0000\u0000"+
		"\u0000\u0126\u04f8\u0001\u0000\u0000\u0000\u0128\u04fb\u0001\u0000\u0000"+
		"\u0000\u012a\u0500\u0001\u0000\u0000\u0000\u012c\u0508\u0001\u0000\u0000"+
		"\u0000\u012e\u050c\u0001\u0000\u0000\u0000\u0130\u0510\u0001\u0000\u0000"+
		"\u0000\u0132\u0513\u0001\u0000\u0000\u0000\u0134\u0519\u0001\u0000\u0000"+
		"\u0000\u0136\u051f\u0001\u0000\u0000\u0000\u0138\u0521\u0001\u0000\u0000"+
		"\u0000\u013a\u0523\u0001\u0000\u0000\u0000\u013c\u052a\u0001\u0000\u0000"+
		"\u0000\u013e\u052c\u0001\u0000\u0000\u0000\u0140\u052e\u0001\u0000\u0000"+
		"\u0000\u0142\u0531\u0001\u0000\u0000\u0000\u0144\u0536\u0001\u0000\u0000"+
		"\u0000\u0146\u053b\u0001\u0000\u0000\u0000\u0148\u053d\u0001\u0000\u0000"+
		"\u0000\u014a\u053f\u0001\u0000\u0000\u0000\u014c\u0541\u0001\u0000\u0000"+
		"\u0000\u014e\u054e\u0001\u0000\u0000\u0000\u0150\u0550\u0001\u0000\u0000"+
		"\u0000\u0152\u0552\u0001\u0000\u0000\u0000\u0154\u055f\u0001\u0000\u0000"+
		"\u0000\u0156\u0566\u0001\u0000\u0000\u0000\u0158\u056a\u0001\u0000\u0000"+
		"\u0000\u015a\u056c\u0001\u0000\u0000\u0000\u015c\u056e\u0001\u0000\u0000"+
		"\u0000\u015e\u0570\u0001\u0000\u0000\u0000\u0160\u0572\u0001\u0000\u0000"+
		"\u0000\u0162\u0574\u0001\u0000\u0000\u0000\u0164\u0576\u0001\u0000\u0000"+
		"\u0000\u0166\u0578\u0001\u0000\u0000\u0000\u0168\u057a\u0001\u0000\u0000"+
		"\u0000\u016a\u057d\u0001\u0000\u0000\u0000\u016c\u0583\u0001\u0000\u0000"+
		"\u0000\u016e\u0587\u0001\u0000\u0000\u0000\u0170\u0598\u0001\u0000\u0000"+
		"\u0000\u0172\u059a\u0001\u0000\u0000\u0000\u0174\u05a0\u0001\u0000\u0000"+
		"\u0000\u0176\u05a2\u0001\u0000\u0000\u0000\u0178\u05a7\u0001\u0000\u0000"+
		"\u0000\u017a\u05a9\u0001\u0000\u0000\u0000\u017c\u05ac\u0001\u0000\u0000"+
		"\u0000\u017e\u05af\u0001\u0000\u0000\u0000\u0180\u05b2\u0001\u0000\u0000"+
		"\u0000\u0182\u05b7\u0001\u0000\u0000\u0000\u0184\u05b9\u0001\u0000\u0000"+
		"\u0000\u0186\u05df\u0001\u0000\u0000\u0000\u0188\u05e1\u0001\u0000\u0000"+
		"\u0000\u018a\u05e5\u0001\u0000\u0000\u0000\u018c\u05f1\u0001\u0000\u0000"+
		"\u0000\u018e\u05fb\u0001\u0000\u0000\u0000\u0190\u05fd\u0001\u0000\u0000"+
		"\u0000\u0192\u063c\u0001\u0000\u0000\u0000\u0194\u063e\u0001\u0000\u0000"+
		"\u0000\u0196\u0199\u0003\u0012\t\u0000\u0197\u0199\u0003&\u0013\u0000"+
		"\u0198\u0196\u0001\u0000\u0000\u0000\u0198\u0197\u0001\u0000\u0000\u0000"+
		"\u0199\u019b\u0001\u0000\u0000\u0000\u019a\u019c\u0005\u0114\u0000\u0000"+
		"\u019b\u019a\u0001\u0000\u0000\u0000\u019b\u019c\u0001\u0000\u0000\u0000"+
		"\u019c\u019d\u0001\u0000\u0000\u0000\u019d\u019e\u0005\u0000\u0000\u0001"+
		"\u019e\u0001\u0001\u0000\u0000\u0000\u019f\u01a0\u0003h4\u0000\u01a0\u01a1"+
		"\u0005\u0000\u0000\u0001\u01a1\u0003\u0001\u0000\u0000\u0000\u01a2\u01a3"+
		"\u0003j5\u0000\u01a3\u01a4\u0005\u0000\u0000\u0001\u01a4\u0005\u0001\u0000"+
		"\u0000\u0000\u01a5\u01a6\u0003\u011c\u008e\u0000\u01a6\u01a7\u0005\u0000"+
		"\u0000\u0001\u01a7\u0007\u0001\u0000\u0000\u0000\u01a8\u01a9\u0003\u00e4"+
		"r\u0000\u01a9\u01aa\u0005\u0000\u0000\u0001\u01aa\t\u0001\u0000\u0000"+
		"\u0000\u01ab\u01ac\u0003R)\u0000\u01ac\u01ad\u0005\u0000\u0000\u0001\u01ad"+
		"\u000b\u0001\u0000\u0000\u0000\u01ae\u01af\u0003\u001a\r\u0000\u01af\u01b0"+
		"\u0005\u0000\u0000\u0001\u01b0\r\u0001\u0000\u0000\u0000\u01b1\u01b2\u0003"+
		"N\'\u0000\u01b2\u01b3\u0005\u0000\u0000\u0001\u01b3\u000f\u0001\u0000"+
		"\u0000\u0000\u01b4\u01b7\u0003\u016a\u00b5\u0000\u01b5\u01b7\u0003\u016c"+
		"\u00b6\u0000\u01b6\u01b4\u0001\u0000\u0000\u0000\u01b6\u01b5\u0001\u0000"+
		"\u0000\u0000\u01b7\u01b8\u0001\u0000\u0000\u0000\u01b8\u01b9\u0005\u0000"+
		"\u0000\u0001\u01b9\u0011\u0001\u0000\u0000\u0000\u01ba\u01bc\u0003\u0014"+
		"\n\u0000\u01bb\u01ba\u0001\u0000\u0000\u0000\u01bb\u01bc\u0001\u0000\u0000"+
		"\u0000\u01bc\u01bd\u0001\u0000\u0000\u0000\u01bd\u01be\u0003\u001a\r\u0000"+
		"\u01be\u0013\u0001\u0000\u0000\u0000\u01bf\u01c0\u0005:\u0000\u0000\u01c0"+
		"\u01c5\u0003\u0016\u000b\u0000\u01c1\u01c2\u0005\u0115\u0000\u0000\u01c2"+
		"\u01c4\u0003\u0016\u000b\u0000\u01c3\u01c1\u0001\u0000\u0000\u0000\u01c4"+
		"\u01c7\u0001\u0000\u0000\u0000\u01c5\u01c3\u0001\u0000\u0000\u0000\u01c5"+
		"\u01c6\u0001\u0000\u0000\u0000\u01c6\u0015\u0001\u0000\u0000\u0000\u01c7"+
		"\u01c5\u0001\u0000\u0000\u0000\u01c8\u01c9\u0003\u0018\f\u0000\u01c9\u01ca"+
		"\u0005\u011c\u0000\u0000\u01ca\u01cb\u0003\u001a\r\u0000\u01cb\u01cc\u0005"+
		"\u011d\u0000\u0000\u01cc\u0017\u0001\u0000\u0000\u0000\u01cd\u01ce\u0003"+
		"\u0154\u00aa\u0000\u01ce\u01cf\u0005\u0001\u0000\u0000\u01cf\u0019\u0001"+
		"\u0000\u0000\u0000\u01d0\u01d4\u0003(\u0014\u0000\u01d1\u01d4\u0003\u001c"+
		"\u000e\u0000\u01d2\u01d4\u0003\u001e\u000f\u0000\u01d3\u01d0\u0001\u0000"+
		"\u0000\u0000\u01d3\u01d1\u0001\u0000\u0000\u0000\u01d3\u01d2\u0001\u0000"+
		"\u0000\u0000\u01d4\u001b\u0001\u0000\u0000\u0000\u01d5\u01d6\u0005_\u0000"+
		"\u0000\u01d6\u01d7\u0005\u0019\u0000\u0000\u01d7\u01dc\u0003P(\u0000\u01d8"+
		"\u01d9\u0005\u011c\u0000\u0000\u01d9\u01da\u0003d2\u0000\u01da\u01db\u0005"+
		"\u011d\u0000\u0000\u01db\u01dd\u0001\u0000\u0000\u0000\u01dc\u01d8\u0001"+
		"\u0000\u0000\u0000\u01dc\u01dd\u0001\u0000\u0000\u0000\u01dd\u01de\u0001"+
		"\u0000\u0000\u0000\u01de\u01df\u0005\u0096\u0000\u0000\u01df\u01e1\u0003"+
		"8\u001c\u0000\u01e0\u01e2\u0003 \u0010\u0000\u01e1\u01e0\u0001\u0000\u0000"+
		"\u0000\u01e1\u01e2\u0001\u0000\u0000\u0000\u01e2\u001d\u0001\u0000\u0000"+
		"\u0000\u01e3\u01e4\u0005\u0093\u0000\u0000\u01e4\u01e5\u0003P(\u0000\u01e5"+
		"\u01e6\u0005\u0086\u0000\u0000\u01e6\u01e8\u0003\"\u0011\u0000\u01e7\u01e9"+
		"\u0003H$\u0000\u01e8\u01e7\u0001\u0000\u0000\u0000\u01e8\u01e9\u0001\u0000"+
		"\u0000\u0000\u01e9\u01eb\u0001\u0000\u0000\u0000\u01ea\u01ec\u0003\u00e2"+
		"q\u0000\u01eb\u01ea\u0001\u0000\u0000\u0000\u01eb\u01ec\u0001\u0000\u0000"+
		"\u0000\u01ec\u01ee\u0001\u0000\u0000\u0000\u01ed\u01ef\u0003 \u0010\u0000"+
		"\u01ee\u01ed\u0001\u0000\u0000\u0000\u01ee\u01ef\u0001\u0000\u0000\u0000"+
		"\u01ef\u001f\u0001\u0000\u0000\u0000\u01f0\u01f1\u0005,\u0000\u0000\u01f1"+
		"\u01f2\u0005\u0120\u0000\u0000\u01f2!\u0001\u0000\u0000\u0000\u01f3\u01f8"+
		"\u0003$\u0012\u0000\u01f4\u01f5\u0005\u0115\u0000\u0000\u01f5\u01f7\u0003"+
		"$\u0012\u0000\u01f6\u01f4\u0001\u0000\u0000\u0000\u01f7\u01fa\u0001\u0000"+
		"\u0000\u0000\u01f8\u01f6\u0001\u0000\u0000\u0000\u01f8\u01f9\u0001\u0000"+
		"\u0000\u0000\u01f9#\u0001\u0000\u0000\u0000\u01fa\u01f8\u0001\u0000\u0000"+
		"\u0000\u01fb\u01fc\u0003f3\u0000\u01fc\u01fd\u0005\u0112\u0000\u0000\u01fd"+
		"\u01fe\u0003\u00e0p\u0000\u01fe%\u0001\u0000\u0000\u0000\u01ff\u0200\u0005"+
		"\t\u0000\u0000\u0200\u0201\u00050\u0000\u0000\u0201\u0202\u0005\u0001"+
		"\u0000\u0000\u0202\u0203\u0003(\u0014\u0000\u0203\'\u0001\u0000\u0000"+
		"\u0000\u0204\u0205\u0003*\u0015\u0000\u0205)\u0001\u0000\u0000\u0000\u0206"+
		"\u020c\u00030\u0018\u0000\u0207\u0208\u0003,\u0016\u0000\u0208\u0209\u0003"+
		"0\u0018\u0000\u0209\u020b\u0001\u0000\u0000\u0000\u020a\u0207\u0001\u0000"+
		"\u0000\u0000\u020b\u020e\u0001\u0000\u0000\u0000\u020c\u020a\u0001\u0000"+
		"\u0000\u0000\u020c\u020d\u0001\u0000\u0000\u0000\u020d+\u0001\u0000\u0000"+
		"\u0000\u020e\u020c\u0001\u0000\u0000\u0000\u020f\u0211\u0003.\u0017\u0000"+
		"\u0210\u0212\u0003>\u001f\u0000\u0211\u0210\u0001\u0000\u0000\u0000\u0211"+
		"\u0212\u0001\u0000\u0000\u0000\u0212-\u0001\u0000\u0000\u0000\u0213\u0214"+
		"\u0005\u0018\u0000\u0000\u0214/\u0001\u0000\u0000\u0000\u0215\u021b\u0003"+
		"6\u001b\u0000\u0216\u0217\u00032\u0019\u0000\u0217\u0218\u00036\u001b"+
		"\u0000\u0218\u021a\u0001\u0000\u0000\u0000\u0219\u0216\u0001\u0000\u0000"+
		"\u0000\u021a\u021d\u0001\u0000\u0000\u0000\u021b\u0219\u0001\u0000\u0000"+
		"\u0000\u021b\u021c\u0001\u0000\u0000\u0000\u021c1\u0001\u0000\u0000\u0000"+
		"\u021d\u021b\u0001\u0000\u0000\u0000\u021e\u0220\u00034\u001a\u0000\u021f"+
		"\u0221\u0003>\u001f\u0000\u0220\u021f\u0001\u0000\u0000\u0000\u0220\u0221"+
		"\u0001\u0000\u0000\u0000\u02213\u0001\u0000\u0000\u0000\u0222\u0223\u0007"+
		"\u0000\u0000\u0000\u02235\u0001\u0000\u0000\u0000\u0224\u0228\u00038\u001c"+
		"\u0000\u0225\u0228\u0003:\u001d\u0000\u0226\u0228\u0003\u0158\u00ac\u0000"+
		"\u0227\u0224\u0001\u0000\u0000\u0000\u0227\u0225\u0001\u0000\u0000\u0000"+
		"\u0227\u0226\u0001\u0000\u0000\u0000\u02287\u0001\u0000\u0000\u0000\u0229"+
		"\u022a\u0005\u011c\u0000\u0000\u022a\u022b\u0003(\u0014\u0000\u022b\u022c"+
		"\u0005\u011d\u0000\u0000\u022c9\u0001\u0000\u0000\u0000\u022d\u022f\u0005"+
		"-\u0000\u0000\u022e\u0230\u0003<\u001e\u0000\u022f\u022e\u0001\u0000\u0000"+
		"\u0000\u022f\u0230\u0001\u0000\u0000\u0000\u0230\u0232\u0001\u0000\u0000"+
		"\u0000\u0231\u0233\u0003>\u001f\u0000\u0232\u0231\u0001\u0000\u0000\u0000"+
		"\u0232\u0233\u0001\u0000\u0000\u0000\u0233\u0234\u0001\u0000\u0000\u0000"+
		"\u0234\u0245\u0003@ \u0000\u0235\u0237\u0003H$\u0000\u0236\u0238\u0003"+
		"\u00e2q\u0000\u0237\u0236\u0001\u0000\u0000\u0000\u0237\u0238\u0001\u0000"+
		"\u0000\u0000\u0238\u023a\u0001\u0000\u0000\u0000\u0239\u023b\u0003\u00f4"+
		"z\u0000\u023a\u0239\u0001\u0000\u0000\u0000\u023a\u023b\u0001\u0000\u0000"+
		"\u0000\u023b\u023d\u0001\u0000\u0000\u0000\u023c\u023e\u0003\u0104\u0082"+
		"\u0000\u023d\u023c\u0001\u0000\u0000\u0000\u023d\u023e\u0001\u0000\u0000"+
		"\u0000\u023e\u0240\u0001\u0000\u0000\u0000\u023f\u0241\u0003\u00e6s\u0000"+
		"\u0240\u023f\u0001\u0000\u0000\u0000\u0240\u0241\u0001\u0000\u0000\u0000"+
		"\u0241\u0243\u0001\u0000\u0000\u0000\u0242\u0244\u0003\u00f2y\u0000\u0243"+
		"\u0242\u0001\u0000\u0000\u0000\u0243\u0244\u0001\u0000\u0000\u0000\u0244"+
		"\u0246\u0001\u0000\u0000\u0000\u0245\u0235\u0001\u0000\u0000\u0000\u0245"+
		"\u0246\u0001\u0000\u0000\u0000\u0246;\u0001\u0000\u0000\u0000\u0247\u0248"+
		"\u0005\u0019\u0000\u0000\u0248\u0249\u0003T*\u0000\u0249=\u0001\u0000"+
		"\u0000\u0000\u024a\u024b\u0007\u0001\u0000\u0000\u024b?\u0001\u0000\u0000"+
		"\u0000\u024c\u0251\u0003B!\u0000\u024d\u024e\u0005\u0115\u0000\u0000\u024e"+
		"\u0250\u0003B!\u0000\u024f\u024d\u0001\u0000\u0000\u0000\u0250\u0253\u0001"+
		"\u0000\u0000\u0000\u0251\u024f\u0001\u0000\u0000\u0000\u0251\u0252\u0001"+
		"\u0000\u0000\u0000\u0252A\u0001\u0000\u0000\u0000\u0253\u0251\u0001\u0000"+
		"\u0000\u0000\u0254\u0256\u0003\u00acV\u0000\u0255\u0257\u0003D\"\u0000"+
		"\u0256\u0255\u0001\u0000\u0000\u0000\u0256\u0257\u0001\u0000\u0000\u0000"+
		"\u0257\u025a\u0001\u0000\u0000\u0000\u0258\u025a\u0003F#\u0000\u0259\u0254"+
		"\u0001\u0000\u0000\u0000\u0259\u0258\u0001\u0000\u0000\u0000\u025aC\u0001"+
		"\u0000\u0000\u0000\u025b\u025d\u0005\u0001\u0000\u0000\u025c\u025b\u0001"+
		"\u0000\u0000\u0000\u025c\u025d\u0001\u0000\u0000\u0000\u025d\u025e\u0001"+
		"\u0000\u0000\u0000\u025e\u025f\u0003\u0156\u00ab\u0000\u025fE\u0001\u0000"+
		"\u0000\u0000\u0260\u0261\u0005\u0147\u0000\u0000\u0261\u0263\u0005\u0123"+
		"\u0000\u0000\u0262\u0260\u0001\u0000\u0000\u0000\u0262\u0263\u0001\u0000"+
		"\u0000\u0000\u0263\u0264\u0001\u0000\u0000\u0000\u0264\u0265\u0005\u0120"+
		"\u0000\u0000\u0265G\u0001\u0000\u0000\u0000\u0266\u0267\u0005\u0011\u0000"+
		"\u0000\u0267\u0269\u0003L&\u0000\u0268\u026a\u0003J%\u0000\u0269\u0268"+
		"\u0001\u0000\u0000\u0000\u0269\u026a\u0001\u0000\u0000\u0000\u026aI\u0001"+
		"\u0000\u0000\u0000\u026b\u026c\u0003\u0158\u00ac\u0000\u026cK\u0001\u0000"+
		"\u0000\u0000\u026d\u027a\u0003P(\u0000\u026e\u026f\u0005\u0115\u0000\u0000"+
		"\u026f\u0279\u0003P(\u0000\u0270\u0271\u0003V+\u0000\u0271\u0272\u0003"+
		"P(\u0000\u0272\u0279\u0001\u0000\u0000\u0000\u0273\u0274\u0003X,\u0000"+
		"\u0274\u0276\u0003P(\u0000\u0275\u0277\u0003\\.\u0000\u0276\u0275\u0001"+
		"\u0000\u0000\u0000\u0276\u0277\u0001\u0000\u0000\u0000\u0277\u0279\u0001"+
		"\u0000\u0000\u0000\u0278\u026e\u0001\u0000\u0000\u0000\u0278\u0270\u0001"+
		"\u0000\u0000\u0000\u0278\u0273\u0001\u0000\u0000\u0000\u0279\u027c\u0001"+
		"\u0000\u0000\u0000\u027a\u0278\u0001\u0000\u0000\u0000\u027a\u027b\u0001"+
		"\u0000\u0000\u0000\u027bM\u0001\u0000\u0000\u0000\u027c\u027a\u0001\u0000"+
		"\u0000\u0000\u027d\u027e\u0005\u0115\u0000\u0000\u027e\u0288\u0003P(\u0000"+
		"\u027f\u0280\u0003V+\u0000\u0280\u0281\u0003P(\u0000\u0281\u0288\u0001"+
		"\u0000\u0000\u0000\u0282\u0283\u0003X,\u0000\u0283\u0285\u0003P(\u0000"+
		"\u0284\u0286\u0003\\.\u0000\u0285\u0284\u0001\u0000\u0000\u0000\u0285"+
		"\u0286\u0001\u0000\u0000\u0000\u0286\u0288\u0001\u0000\u0000\u0000\u0287"+
		"\u027d\u0001\u0000\u0000\u0000\u0287\u027f\u0001\u0000\u0000\u0000\u0287"+
		"\u0282\u0001\u0000\u0000\u0000\u0288\u028b\u0001\u0000\u0000\u0000\u0289"+
		"\u0287\u0001\u0000\u0000\u0000\u0289\u028a\u0001\u0000\u0000\u0000\u028a"+
		"\u028d\u0001\u0000\u0000\u0000\u028b\u0289\u0001\u0000\u0000\u0000\u028c"+
		"\u028e\u0003J%\u0000\u028d\u028c\u0001\u0000\u0000\u0000\u028d\u028e\u0001"+
		"\u0000\u0000\u0000\u028eO\u0001\u0000\u0000\u0000\u028f\u0291\u0003T*"+
		"\u0000\u0290\u0292\u0003D\"\u0000\u0291\u0290\u0001\u0000\u0000\u0000"+
		"\u0291\u0292\u0001\u0000\u0000\u0000\u0292\u029c\u0001\u0000\u0000\u0000"+
		"\u0293\u0295\u00038\u001c\u0000\u0294\u0296\u0003D\"\u0000\u0295\u0294"+
		"\u0001\u0000\u0000\u0000\u0295\u0296\u0001\u0000\u0000\u0000\u0296\u029c"+
		"\u0001\u0000\u0000\u0000\u0297\u0298\u0003\u0158\u00ac\u0000\u0298\u0299"+
		"\u0003D\"\u0000\u0299\u029c\u0001\u0000\u0000\u0000\u029a\u029c\u0003"+
		"\u0122\u0091\u0000\u029b\u028f\u0001\u0000\u0000\u0000\u029b\u0293\u0001"+
		"\u0000\u0000\u0000\u029b\u0297\u0001\u0000\u0000\u0000\u029b\u029a\u0001"+
		"\u0000\u0000\u0000\u029cQ\u0001\u0000\u0000\u0000\u029d\u02a2\u0003T*"+
		"\u0000\u029e\u02a2\u00038\u001c\u0000\u029f\u02a2\u0003\u0158\u00ac\u0000"+
		"\u02a0\u02a2\u0003\u0124\u0092\u0000\u02a1\u029d\u0001\u0000\u0000\u0000"+
		"\u02a1\u029e\u0001\u0000\u0000\u0000\u02a1\u029f\u0001\u0000\u0000\u0000"+
		"\u02a1\u02a0\u0001\u0000\u0000\u0000\u02a2S\u0001\u0000\u0000\u0000\u02a3"+
		"\u02a9\u0003\u0154\u00aa\u0000\u02a4\u02a7\u0005\u0123\u0000\u0000\u02a5"+
		"\u02a8\u0003\u0162\u00b1\u0000\u02a6\u02a8\u0003\u0154\u00aa\u0000\u02a7"+
		"\u02a5\u0001\u0000\u0000\u0000\u02a7\u02a6\u0001\u0000\u0000\u0000\u02a8"+
		"\u02aa\u0001\u0000\u0000\u0000\u02a9\u02a4\u0001\u0000\u0000\u0000\u02a9"+
		"\u02aa\u0001\u0000\u0000\u0000\u02aa\u02b0\u0001\u0000\u0000\u0000\u02ab"+
		"\u02ae\u0005\u0123\u0000\u0000\u02ac\u02af\u0003\u0162\u00b1\u0000\u02ad"+
		"\u02af\u0003\u0154\u00aa\u0000\u02ae\u02ac\u0001\u0000\u0000\u0000\u02ae"+
		"\u02ad\u0001\u0000\u0000\u0000\u02af\u02b1\u0001\u0000\u0000\u0000\u02b0"+
		"\u02ab\u0001\u0000\u0000\u0000\u02b0\u02b1\u0001\u0000\u0000\u0000\u02b1"+
		"U\u0001\u0000\u0000\u0000\u02b2\u02b3\u0005\n\u0000\u0000\u02b3\u02bc"+
		"\u0005\u001b\u0000\u0000\u02b4\u02b5\u00055\u0000\u0000\u02b5\u02bc\u0005"+
		"\u001b\u0000\u0000\u02b6\u02b8\u0005 \u0000\u0000\u02b7\u02b9\u0003Z-"+
		"\u0000\u02b8\u02b7\u0001\u0000\u0000\u0000\u02b8\u02b9\u0001\u0000\u0000"+
		"\u0000\u02b9\u02ba\u0001\u0000\u0000\u0000\u02ba\u02bc\u0005\u001b\u0000"+
		"\u0000\u02bb\u02b2\u0001\u0000\u0000\u0000\u02bb\u02b4\u0001\u0000\u0000"+
		"\u0000\u02bb\u02b6\u0001\u0000\u0000\u0000\u02bcW\u0001\u0000\u0000\u0000"+
		"\u02bd\u02bf\u0003Z-\u0000\u02be\u02bd\u0001\u0000\u0000\u0000\u02be\u02bf"+
		"\u0001\u0000\u0000\u0000\u02bf\u02c0\u0001\u0000\u0000\u0000\u02c0\u02c1"+
		"\u0005\u001b\u0000\u0000\u02c1Y\u0001\u0000\u0000\u0000\u02c2\u02c8\u0005"+
		"\u0017\u0000\u0000\u02c3\u02c5\u0007\u0002\u0000\u0000\u02c4\u02c6\u0005"+
		"\'\u0000\u0000\u02c5\u02c4\u0001\u0000\u0000\u0000\u02c5\u02c6\u0001\u0000"+
		"\u0000\u0000\u02c6\u02c8\u0001\u0000\u0000\u0000\u02c7\u02c2\u0001\u0000"+
		"\u0000\u0000\u02c7\u02c3\u0001\u0000\u0000\u0000\u02c8[\u0001\u0000\u0000"+
		"\u0000\u02c9\u02cc\u0003^/\u0000\u02ca\u02cc\u0003`0\u0000\u02cb\u02c9"+
		"\u0001\u0000\u0000\u0000\u02cb\u02ca\u0001\u0000\u0000\u0000\u02cc]\u0001"+
		"\u0000\u0000\u0000\u02cd\u02ce\u0005&\u0000\u0000\u02ce\u02cf\u0003\u00e4"+
		"r\u0000\u02cf_\u0001\u0000\u0000\u0000\u02d0\u02d1\u0003b1\u0000\u02d1"+
		"\u02d2\u0005\u011c\u0000\u0000\u02d2\u02d3\u0003d2\u0000\u02d3\u02d4\u0005"+
		"\u011d\u0000\u0000\u02d4a\u0001\u0000\u0000\u0000\u02d5\u02d6\u00057\u0000"+
		"\u0000\u02d6c\u0001\u0000\u0000\u0000\u02d7\u02dc\u0003f3\u0000\u02d8"+
		"\u02d9\u0005\u0115\u0000\u0000\u02d9\u02db\u0003f3\u0000\u02da\u02d8\u0001"+
		"\u0000\u0000\u0000\u02db\u02de\u0001\u0000\u0000\u0000\u02dc\u02da\u0001"+
		"\u0000\u0000\u0000\u02dc\u02dd\u0001\u0000\u0000\u0000\u02dde\u0001\u0000"+
		"\u0000\u0000\u02de\u02dc\u0001\u0000\u0000\u0000\u02df\u02e0\u0003\u0154"+
		"\u00aa\u0000\u02e0\u02e1\u0005\u0123\u0000\u0000\u02e1\u02e3\u0001\u0000"+
		"\u0000\u0000\u02e2\u02df\u0001\u0000\u0000\u0000\u02e2\u02e3\u0001\u0000"+
		"\u0000\u0000\u02e3\u02e4\u0001\u0000\u0000\u0000\u02e4\u02ea\u0003\u0154"+
		"\u00aa\u0000\u02e5\u02e6\u0003\u0154\u00aa\u0000\u02e6\u02e7\u0005\u0123"+
		"\u0000\u0000\u02e7\u02e8\u0003\u0158\u00ac\u0000\u02e8\u02ea\u0001\u0000"+
		"\u0000\u0000\u02e9\u02e2\u0001\u0000\u0000\u0000\u02e9\u02e5\u0001\u0000"+
		"\u0000\u0000\u02eag\u0001\u0000\u0000\u0000\u02eb\u02ec\u0003\u0154\u00aa"+
		"\u0000\u02ec\u02ed\u0005\u0123\u0000\u0000\u02ed\u02ef\u0001\u0000\u0000"+
		"\u0000\u02ee\u02eb\u0001\u0000\u0000\u0000\u02ee\u02ef\u0001\u0000\u0000"+
		"\u0000\u02ef\u02f0\u0001\u0000\u0000\u0000\u02f0\u02f7\u0003\u0154\u00aa"+
		"\u0000\u02f1\u02f2\u0003\u0154\u00aa\u0000\u02f2\u02f3\u0005\u0123\u0000"+
		"\u0000\u02f3\u02f4\u0003\u0158\u00ac\u0000\u02f4\u02f7\u0001\u0000\u0000"+
		"\u0000\u02f5\u02f7\u0003\u0158\u00ac\u0000\u02f6\u02ee\u0001\u0000\u0000"+
		"\u0000\u02f6\u02f1\u0001\u0000\u0000\u0000\u02f6\u02f5\u0001\u0000\u0000"+
		"\u0000\u02f7i\u0001\u0000\u0000\u0000\u02f8\u02fb\u0003l6\u0000\u02f9"+
		"\u02fa\u0005\u0128\u0000\u0000\u02fa\u02fc\u0003\u0182\u00c1\u0000\u02fb"+
		"\u02f9\u0001\u0000\u0000\u0000\u02fb\u02fc\u0001\u0000\u0000\u0000\u02fc"+
		"\u0302\u0001\u0000\u0000\u0000\u02fd\u0302\u0003\u00c6c\u0000\u02fe\u0302"+
		"\u0003\u0086C\u0000\u02ff\u0302\u0003\u0158\u00ac\u0000\u0300\u0302\u0003"+
		"\u0194\u00ca\u0000\u0301\u02f8\u0001\u0000\u0000\u0000\u0301\u02fd\u0001"+
		"\u0000\u0000\u0000\u0301\u02fe\u0001\u0000\u0000\u0000\u0301\u02ff\u0001"+
		"\u0000\u0000\u0000\u0301\u0300\u0001\u0000\u0000\u0000\u0302k\u0001\u0000"+
		"\u0000\u0000\u0303\u0306\u0003n7\u0000\u0304\u0306\u0003p8\u0000\u0305"+
		"\u0303\u0001\u0000\u0000\u0000\u0305\u0304\u0001\u0000\u0000\u0000\u0306"+
		"m\u0001\u0000\u0000\u0000\u0307\u0308\u0005\u011c\u0000\u0000\u0308\u0309"+
		"\u0003\u00acV\u0000\u0309\u030a\u0005\u011d\u0000\u0000\u030ao\u0001\u0000"+
		"\u0000\u0000\u030b\u0314\u0003\u016c\u00b6\u0000\u030c\u0314\u0003f3\u0000"+
		"\u030d\u0314\u0003r9\u0000\u030e\u0314\u0003x<\u0000\u030f\u0314\u0003"+
		"\u0088D\u0000\u0310\u0314\u0003\u014c\u00a6\u0000\u0311\u0314\u0003\u008c"+
		"F\u0000\u0312\u0314\u00038\u001c\u0000\u0313\u030b\u0001\u0000\u0000\u0000"+
		"\u0313\u030c\u0001\u0000\u0000\u0000\u0313\u030d\u0001\u0000\u0000\u0000"+
		"\u0313\u030e\u0001\u0000\u0000\u0000\u0313\u030f\u0001\u0000\u0000\u0000"+
		"\u0313\u0310\u0001\u0000\u0000\u0000\u0313\u0311\u0001\u0000\u0000\u0000"+
		"\u0313\u0312\u0001\u0000\u0000\u0000\u0314q\u0001\u0000\u0000\u0000\u0315"+
		"\u0316\u0005E\u0000\u0000\u0316\u0317\u0005\u011c\u0000\u0000\u0317\u0318"+
		"\u0005\u0120\u0000\u0000\u0318\u0325\u0005\u011d\u0000\u0000\u0319\u031c"+
		"\u0003t:\u0000\u031a\u031c\u0003v;\u0000\u031b\u0319\u0001\u0000\u0000"+
		"\u0000\u031b\u031a\u0001\u0000\u0000\u0000\u031c\u031d\u0001\u0000\u0000"+
		"\u0000\u031d\u031f\u0005\u011c\u0000\u0000\u031e\u0320\u0003>\u001f\u0000"+
		"\u031f\u031e\u0001\u0000\u0000\u0000\u031f\u0320\u0001\u0000\u0000\u0000"+
		"\u0320\u0321\u0001\u0000\u0000\u0000\u0321\u0322\u0003\u00acV\u0000\u0322"+
		"\u0323\u0005\u011d\u0000\u0000\u0323\u0325\u0001\u0000\u0000\u0000\u0324"+
		"\u0315\u0001\u0000\u0000\u0000\u0324\u031b\u0001\u0000\u0000\u0000\u0325"+
		"s\u0001\u0000\u0000\u0000\u0326\u0327\u0007\u0003\u0000\u0000\u0327u\u0001"+
		"\u0000\u0000\u0000\u0328\u0329\u0007\u0004\u0000\u0000\u0329w\u0001\u0000"+
		"\u0000\u0000\u032a\u032b\u0005\u0007\u0000\u0000\u032b\u032c\u0003\u00ac"+
		"V\u0000\u032c\u032e\u0003~?\u0000\u032d\u032f\u0003\u0082A\u0000\u032e"+
		"\u032d\u0001\u0000\u0000\u0000\u032e\u032f\u0001\u0000\u0000\u0000\u032f"+
		"\u0330\u0001\u0000\u0000\u0000\u0330\u0331\u0005\f\u0000\u0000\u0331\u033a"+
		"\u0001\u0000\u0000\u0000\u0332\u0333\u0005\u0007\u0000\u0000\u0333\u0335"+
		"\u0003z=\u0000\u0334\u0336\u0003\u0082A\u0000\u0335\u0334\u0001\u0000"+
		"\u0000\u0000\u0335\u0336\u0001\u0000\u0000\u0000\u0336\u0337\u0001\u0000"+
		"\u0000\u0000\u0337\u0338\u0005\f\u0000\u0000\u0338\u033a\u0001\u0000\u0000"+
		"\u0000\u0339\u032a\u0001\u0000\u0000\u0000\u0339\u0332\u0001\u0000\u0000"+
		"\u0000\u033ay\u0001\u0000\u0000\u0000\u033b\u033d\u0003|>\u0000\u033c"+
		"\u033b\u0001\u0000\u0000\u0000\u033d\u033e\u0001\u0000\u0000\u0000\u033e"+
		"\u033c\u0001\u0000\u0000\u0000\u033e\u033f\u0001\u0000\u0000\u0000\u033f"+
		"{\u0001\u0000\u0000\u0000\u0340\u0341\u00058\u0000\u0000\u0341\u0342\u0003"+
		"\u00e4r\u0000\u0342\u0343\u00051\u0000\u0000\u0343\u0344\u0003\u0084B"+
		"\u0000\u0344}\u0001\u0000\u0000\u0000\u0345\u0347\u0003\u0080@\u0000\u0346"+
		"\u0345\u0001\u0000\u0000\u0000\u0347\u0348\u0001\u0000\u0000\u0000\u0348"+
		"\u0346\u0001\u0000\u0000\u0000\u0348\u0349\u0001\u0000\u0000\u0000\u0349"+
		"\u007f\u0001\u0000\u0000\u0000\u034a\u034b\u00058\u0000\u0000\u034b\u034c"+
		"\u0003\u00acV\u0000\u034c\u034d\u00051\u0000\u0000\u034d\u034e\u0003\u0084"+
		"B\u0000\u034e\u0081\u0001\u0000\u0000\u0000\u034f\u0350\u0005\r\u0000"+
		"\u0000\u0350\u0351\u0003\u0084B\u0000\u0351\u0083\u0001\u0000\u0000\u0000"+
		"\u0352\u0355\u0003\u00acV\u0000\u0353\u0355\u0003\u0086C\u0000\u0354\u0352"+
		"\u0001\u0000\u0000\u0000\u0354\u0353\u0001\u0000\u0000\u0000\u0355\u0085"+
		"\u0001\u0000\u0000\u0000\u0356\u0357\u0005\"\u0000\u0000\u0357\u0087\u0001"+
		"\u0000\u0000\u0000\u0358\u0359\u0003\u008aE\u0000\u0359\u035a\u0005\u011c"+
		"\u0000\u0000\u035a\u035b\u0003\u00acV\u0000\u035b\u035c\u0005\u0001\u0000"+
		"\u0000\u035c\u035d\u0003\u0182\u00c1\u0000\u035d\u035e\u0005\u011d\u0000"+
		"\u0000\u035e\u0089\u0001\u0000\u0000\u0000\u035f\u0360\u0007\u0005\u0000"+
		"\u0000\u0360\u008b\u0001\u0000\u0000\u0000\u0361\u0362\u0003\u008eG\u0000"+
		"\u0362\u0363\u0003\u0090H\u0000\u0363\u008d\u0001\u0000\u0000\u0000\u0364"+
		"\u0365\u0003t:\u0000\u0365\u0367\u0005\u011c\u0000\u0000\u0366\u0368\u0003"+
		"\u0152\u00a9\u0000\u0367\u0366\u0001\u0000\u0000\u0000\u0367\u0368\u0001"+
		"\u0000\u0000\u0000\u0368\u0369\u0001\u0000\u0000\u0000\u0369\u036a\u0005"+
		"\u011d\u0000\u0000\u036a\u0376\u0001\u0000\u0000\u0000\u036b\u036c\u0003"+
		"\u00a6S\u0000\u036c\u036d\u0005\u011c\u0000\u0000\u036d\u036e\u0003\u0152"+
		"\u00a9\u0000\u036e\u0373\u0005\u011d\u0000\u0000\u036f\u0371\u0003\u00a8"+
		"T\u0000\u0370\u036f\u0001\u0000\u0000\u0000\u0370\u0371\u0001\u0000\u0000"+
		"\u0000\u0371\u0372\u0001\u0000\u0000\u0000\u0372\u0374\u0003\u00aaU\u0000"+
		"\u0373\u0370\u0001\u0000\u0000\u0000\u0373\u0374\u0001\u0000\u0000\u0000"+
		"\u0374\u0376\u0001\u0000\u0000\u0000\u0375\u0364\u0001\u0000\u0000\u0000"+
		"\u0375\u036b\u0001\u0000\u0000\u0000\u0376\u008f\u0001\u0000\u0000\u0000"+
		"\u0377\u0378\u0005u\u0000\u0000\u0378\u037a\u0005\u011c\u0000\u0000\u0379"+
		"\u037b\u0003\u0092I\u0000\u037a\u0379\u0001\u0000\u0000\u0000\u037a\u037b"+
		"\u0001\u0000\u0000\u0000\u037b\u037d\u0001\u0000\u0000\u0000\u037c\u037e"+
		"\u0003\u00e6s\u0000\u037d\u037c\u0001\u0000\u0000\u0000\u037d\u037e\u0001"+
		"\u0000\u0000\u0000\u037e\u0380\u0001\u0000\u0000\u0000\u037f\u0381\u0003"+
		"\u0094J\u0000\u0380\u037f\u0001\u0000\u0000\u0000\u0380\u0381\u0001\u0000"+
		"\u0000\u0000\u0381\u0382\u0001\u0000\u0000\u0000\u0382\u0383\u0005\u011d"+
		"\u0000\u0000\u0383\u0091\u0001\u0000\u0000\u0000\u0384\u0385\u0005w\u0000"+
		"\u0000\u0385\u0386\u0005?\u0000\u0000\u0386\u0387\u0003\u0152\u00a9\u0000"+
		"\u0387\u0093\u0001\u0000\u0000\u0000\u0388\u0389\u0003\u0096K\u0000\u0389"+
		"\u038a\u0003\u0098L\u0000\u038a\u0095\u0001\u0000\u0000\u0000\u038b\u038c"+
		"\u0007\u0006\u0000\u0000\u038c\u0097\u0001\u0000\u0000\u0000\u038d\u0391"+
		"\u0003\u009aM\u0000\u038e\u0391\u0003\u009eO\u0000\u038f\u0391\u0003\u00a2"+
		"Q\u0000\u0390\u038d\u0001\u0000\u0000\u0000\u0390\u038e\u0001\u0000\u0000"+
		"\u0000\u0390\u038f\u0001\u0000\u0000\u0000\u0391\u0099\u0001\u0000\u0000"+
		"\u0000\u0392\u0393\u0005>\u0000\u0000\u0393\u0394\u0003\u009cN\u0000\u0394"+
		"\u0395\u0005\u0003\u0000\u0000\u0395\u0396\u0003\u009cN\u0000\u0396\u009b"+
		"\u0001\u0000\u0000\u0000\u0397\u039b\u0003\u009eO\u0000\u0398\u039b\u0003"+
		"\u00a0P\u0000\u0399\u039b\u0003\u00a2Q\u0000\u039a\u0397\u0001\u0000\u0000"+
		"\u0000\u039a\u0398\u0001\u0000\u0000\u0000\u039a\u0399\u0001\u0000\u0000"+
		"\u0000\u039b\u009d\u0001\u0000\u0000\u0000\u039c\u039d\u0003\u00a4R\u0000"+
		"\u039d\u039e\u0005y\u0000\u0000\u039e\u009f\u0001\u0000\u0000\u0000\u039f"+
		"\u03a0\u0003\u00a4R\u0000\u03a0\u03a1\u0005X\u0000\u0000\u03a1\u00a1\u0001"+
		"\u0000\u0000\u0000\u03a2\u03a3\u0005H\u0000\u0000\u03a3\u03a4\u0005\u0083"+
		"\u0000\u0000\u03a4\u00a3\u0001\u0000\u0000\u0000\u03a5\u03a6\u0007\u0007"+
		"\u0000\u0000\u03a6\u00a5\u0001\u0000\u0000\u0000\u03a7\u03a8\u0007\b\u0000"+
		"\u0000\u03a8\u00a7\u0001\u0000\u0000\u0000\u03a9\u03aa\u0005\u0011\u0000"+
		"\u0000\u03aa\u03ab\u0007\t\u0000\u0000\u03ab\u00a9\u0001\u0000\u0000\u0000"+
		"\u03ac\u03ad\u0007\n\u0000\u0000\u03ad\u03ae\u0005#\u0000\u0000\u03ae"+
		"\u00ab\u0001\u0000\u0000\u0000\u03af\u03b4\u0003\u00aeW\u0000\u03b0\u03b4"+
		"\u0003\u00deo\u0000\u03b1\u03b4\u0003\u0158\u00ac\u0000\u03b2\u03b4\u0003"+
		"\u00ceg\u0000\u03b3\u03af\u0001\u0000\u0000\u0000\u03b3\u03b0\u0001\u0000"+
		"\u0000\u0000\u03b3\u03b1\u0001\u0000\u0000\u0000\u03b3\u03b2\u0001\u0000"+
		"\u0000\u0000\u03b4\u00ad\u0001\u0000\u0000\u0000\u03b5\u03b9\u0003\u00b0"+
		"X\u0000\u03b6\u03b9\u0003\u00c2a\u0000\u03b7\u03b9\u0003\u0086C\u0000"+
		"\u03b8\u03b5\u0001\u0000\u0000\u0000\u03b8\u03b6\u0001\u0000\u0000\u0000"+
		"\u03b8\u03b7\u0001\u0000\u0000\u0000\u03b9\u00af\u0001\u0000\u0000\u0000"+
		"\u03ba\u03bf\u0003\u00b2Y\u0000\u03bb\u03bc\u0007\u000b\u0000\u0000\u03bc"+
		"\u03be\u0003\u00b2Y\u0000\u03bd\u03bb\u0001\u0000\u0000\u0000\u03be\u03c1"+
		"\u0001\u0000\u0000\u0000\u03bf\u03bd\u0001\u0000\u0000\u0000\u03bf\u03c0"+
		"\u0001\u0000\u0000\u0000\u03c0\u00b1\u0001\u0000\u0000\u0000\u03c1\u03bf"+
		"\u0001\u0000\u0000\u0000\u03c2\u03c7\u0003\u00b4Z\u0000\u03c3\u03c4\u0007"+
		"\f\u0000\u0000\u03c4\u03c6\u0003\u00b4Z\u0000\u03c5\u03c3\u0001\u0000"+
		"\u0000\u0000\u03c6\u03c9\u0001\u0000\u0000\u0000\u03c7\u03c5\u0001\u0000"+
		"\u0000\u0000\u03c7\u03c8\u0001\u0000\u0000\u0000\u03c8\u00b3\u0001\u0000"+
		"\u0000\u0000\u03c9\u03c7\u0001\u0000\u0000\u0000\u03ca\u03cc\u0003\u00b8"+
		"\\\u0000\u03cb\u03ca\u0001\u0000\u0000\u0000\u03cb\u03cc\u0001\u0000\u0000"+
		"\u0000\u03cc\u03cd\u0001\u0000\u0000\u0000\u03cd\u03ce\u0003\u00b6[\u0000"+
		"\u03ce\u00b5\u0001\u0000\u0000\u0000\u03cf\u03d2\u0003l6\u0000\u03d0\u03d2"+
		"\u0003\u00ba]\u0000\u03d1\u03cf\u0001\u0000\u0000\u0000\u03d1\u03d0\u0001"+
		"\u0000\u0000\u0000\u03d2\u00b7\u0001\u0000\u0000\u0000\u03d3\u03d4\u0007"+
		"\u000b\u0000\u0000\u03d4\u00b9\u0001\u0000\u0000\u0000\u03d5\u03d6\u0005"+
		"T\u0000\u0000\u03d6\u03d7\u0005\u011c\u0000\u0000\u03d7\u03d8\u0003\u00bc"+
		"^\u0000\u03d8\u03d9\u0005\u0011\u0000\u0000\u03d9\u03da\u0003\u00c0`\u0000"+
		"\u03da\u03db\u0005\u011d\u0000\u0000\u03db\u00bb\u0001\u0000\u0000\u0000"+
		"\u03dc\u03e0\u0003\u0146\u00a3\u0000\u03dd\u03e0\u0003\u00be_\u0000\u03de"+
		"\u03e0\u0003\u014a\u00a5\u0000\u03df\u03dc\u0001\u0000\u0000\u0000\u03df"+
		"\u03dd\u0001\u0000\u0000\u0000\u03df\u03de\u0001\u0000\u0000\u0000\u03e0"+
		"\u00bd\u0001\u0000\u0000\u0000\u03e1\u03e2\u0007\r\u0000\u0000\u03e2\u00bf"+
		"\u0001\u0000\u0000\u0000\u03e3\u03e6\u0003f3\u0000\u03e4\u03e6\u0003\u0178"+
		"\u00bc\u0000\u03e5\u03e3\u0001\u0000\u0000\u0000\u03e5\u03e4\u0001\u0000"+
		"\u0000\u0000\u03e6\u00c1\u0001\u0000\u0000\u0000\u03e7\u03ec\u0003\u00c4"+
		"b\u0000\u03e8\u03e9\u0005\u0116\u0000\u0000\u03e9\u03eb\u0003\u00c4b\u0000"+
		"\u03ea\u03e8\u0001\u0000\u0000\u0000\u03eb\u03ee\u0001\u0000\u0000\u0000"+
		"\u03ec\u03ea\u0001\u0000\u0000\u0000\u03ec\u03ed\u0001\u0000\u0000\u0000"+
		"\u03ed\u00c3\u0001\u0000\u0000\u0000\u03ee\u03ec\u0001\u0000\u0000\u0000"+
		"\u03ef\u03f2\u0003l6\u0000\u03f0\u03f2\u0003\u00c6c\u0000\u03f1\u03ef"+
		"\u0001\u0000\u0000\u0000\u03f1\u03f0\u0001\u0000\u0000\u0000\u03f2\u00c5"+
		"\u0001\u0000\u0000\u0000\u03f3\u03f4\u0003\u00c8d\u0000\u03f4\u03f5\u0005"+
		"\u011c\u0000\u0000\u03f5\u03f6\u0003\u00cae\u0000\u03f6\u03f7\u0005\u011d"+
		"\u0000\u0000\u03f7\u00c7\u0001\u0000\u0000\u0000\u03f8\u03f9\u0005\u0091"+
		"\u0000\u0000\u03f9\u00c9\u0001\u0000\u0000\u0000\u03fa\u03fc\u0003\u00cc"+
		"f\u0000\u03fb\u03fa\u0001\u0000\u0000\u0000\u03fb\u03fc\u0001\u0000\u0000"+
		"\u0000\u03fc\u03fe\u0001\u0000\u0000\u0000\u03fd\u03ff\u0003\u00c2a\u0000"+
		"\u03fe\u03fd\u0001\u0000\u0000\u0000\u03fe\u03ff\u0001\u0000\u0000\u0000"+
		"\u03ff\u0400\u0001\u0000\u0000\u0000\u0400\u0402\u0005\u0011\u0000\u0000"+
		"\u0401\u03fb\u0001\u0000\u0000\u0000\u0401\u0402\u0001\u0000\u0000\u0000"+
		"\u0402\u0403\u0001\u0000\u0000\u0000\u0403\u0409\u0003\u00acV\u0000\u0404"+
		"\u0405\u0003\u00acV\u0000\u0405\u0406\u0005\u0115\u0000\u0000\u0406\u0407"+
		"\u0003\u00c2a\u0000\u0407\u0409\u0001\u0000\u0000\u0000\u0408\u0401\u0001"+
		"\u0000\u0000\u0000\u0408\u0404\u0001\u0000\u0000\u0000\u0409\u00cb\u0001"+
		"\u0000\u0000\u0000\u040a\u040b\u0007\u000e\u0000\u0000\u040b\u00cd\u0001"+
		"\u0000\u0000\u0000\u040c\u040d\u0003\u00d0h\u0000\u040d\u00cf\u0001\u0000"+
		"\u0000\u0000\u040e\u0413\u0003\u00d2i\u0000\u040f\u0410\u0005(\u0000\u0000"+
		"\u0410\u0412\u0003\u00d2i\u0000\u0411\u040f\u0001\u0000\u0000\u0000\u0412"+
		"\u0415\u0001\u0000\u0000\u0000\u0413\u0411\u0001\u0000\u0000\u0000\u0413"+
		"\u0414\u0001\u0000\u0000\u0000\u0414\u00d1\u0001\u0000\u0000\u0000\u0415"+
		"\u0413\u0001\u0000\u0000\u0000\u0416\u041b\u0003\u00d4j\u0000\u0417\u0418"+
		"\u0005\u0003\u0000\u0000\u0418\u041a\u0003\u00d4j\u0000\u0419\u0417\u0001"+
		"\u0000\u0000\u0000\u041a\u041d\u0001\u0000\u0000\u0000\u041b\u0419\u0001"+
		"\u0000\u0000\u0000\u041b\u041c\u0001\u0000\u0000\u0000\u041c\u00d3\u0001"+
		"\u0000\u0000\u0000\u041d\u041b\u0001\u0000\u0000\u0000\u041e\u0420\u0003"+
		"\u0138\u009c\u0000\u041f\u041e\u0001\u0000\u0000\u0000\u041f\u0420\u0001"+
		"\u0000\u0000\u0000\u0420\u0421\u0001\u0000\u0000\u0000\u0421\u0422\u0003"+
		"\u00d6k\u0000\u0422\u00d5\u0001\u0000\u0000\u0000\u0423\u0424\u0005\u011c"+
		"\u0000\u0000\u0424\u0425\u0003\u00ceg\u0000\u0425\u0426\u0005\u011d\u0000"+
		"\u0000\u0426\u042c\u0001\u0000\u0000\u0000\u0427\u0429\u0003\u00d8l\u0000"+
		"\u0428\u042a\u0003\u0134\u009a\u0000\u0429\u0428\u0001\u0000\u0000\u0000"+
		"\u0429\u042a\u0001\u0000\u0000\u0000\u042a\u042c\u0001\u0000\u0000\u0000"+
		"\u042b\u0423\u0001\u0000\u0000\u0000\u042b\u0427\u0001\u0000\u0000\u0000"+
		"\u042c\u00d7\u0001\u0000\u0000\u0000\u042d\u0430\u0003\u00dam\u0000\u042e"+
		"\u0430\u0003p8\u0000\u042f\u042d\u0001\u0000\u0000\u0000\u042f\u042e\u0001"+
		"\u0000\u0000\u0000\u0430\u00d9\u0001\u0000\u0000\u0000\u0431\u0439\u0003"+
		"\u0108\u0084\u0000\u0432\u0439\u0003\u0112\u0089\u0000\u0433\u0439\u0003"+
		"\u0116\u008b\u0000\u0434\u0439\u0003\u0118\u008c\u0000\u0435\u0439\u0003"+
		"\u0130\u0098\u0000\u0436\u0439\u0003\u0142\u00a1\u0000\u0437\u0439\u0003"+
		"\u00dcn\u0000\u0438\u0431\u0001\u0000\u0000\u0000\u0438\u0432\u0001\u0000"+
		"\u0000\u0000\u0438\u0433\u0001\u0000\u0000\u0000\u0438\u0434\u0001\u0000"+
		"\u0000\u0000\u0438\u0435\u0001\u0000\u0000\u0000\u0438\u0436\u0001\u0000"+
		"\u0000\u0000\u0438\u0437\u0001\u0000\u0000\u0000\u0439\u00db\u0001\u0000"+
		"\u0000\u0000\u043a\u043b\u0003\u0158\u00ac\u0000\u043b\u00dd\u0001\u0000"+
		"\u0000\u0000\u043c\u043f\u0003p8\u0000\u043d\u043f\u0003\u0086C\u0000"+
		"\u043e\u043c\u0001\u0000\u0000\u0000\u043e\u043d\u0001\u0000\u0000\u0000"+
		"\u043f\u00df\u0001\u0000\u0000\u0000\u0440\u0444\u0003p8\u0000\u0441\u0444"+
		"\u0003\u00aeW\u0000\u0442\u0444\u0003\u0158\u00ac\u0000\u0443\u0440\u0001"+
		"\u0000\u0000\u0000\u0443\u0441\u0001\u0000\u0000\u0000\u0443\u0442\u0001"+
		"\u0000\u0000\u0000\u0444\u00e1\u0001\u0000\u0000\u0000\u0445\u0446\u0005"+
		"9\u0000\u0000\u0446\u0447\u0003\u00e4r\u0000\u0447\u00e3\u0001\u0000\u0000"+
		"\u0000\u0448\u0449\u0003\u00acV\u0000\u0449\u00e5\u0001\u0000\u0000\u0000"+
		"\u044a\u044b\u0005)\u0000\u0000\u044b\u044c\u0005?\u0000\u0000\u044c\u044d"+
		"\u0003\u00e8t\u0000\u044d\u00e7\u0001\u0000\u0000\u0000\u044e\u0453\u0003"+
		"\u00eau\u0000\u044f\u0450\u0005\u0115\u0000\u0000\u0450\u0452\u0003\u00ea"+
		"u\u0000\u0451\u044f\u0001\u0000\u0000\u0000\u0452\u0455\u0001\u0000\u0000"+
		"\u0000\u0453\u0451\u0001\u0000\u0000\u0000\u0453\u0454\u0001\u0000\u0000"+
		"\u0000\u0454\u00e9\u0001\u0000\u0000\u0000\u0455\u0453\u0001\u0000\u0000"+
		"\u0000\u0456\u0458\u0003\u00e0p\u0000\u0457\u0459\u0003\u00ecv\u0000\u0458"+
		"\u0457\u0001\u0000\u0000\u0000\u0458\u0459\u0001\u0000\u0000\u0000\u0459"+
		"\u045b\u0001\u0000\u0000\u0000\u045a\u045c\u0003\u00eew\u0000\u045b\u045a"+
		"\u0001\u0000\u0000\u0000\u045b\u045c\u0001\u0000\u0000\u0000\u045c\u00eb"+
		"\u0001\u0000\u0000\u0000\u045d\u045e\u0007\u000f\u0000\u0000\u045e\u00ed"+
		"\u0001\u0000\u0000\u0000\u045f\u0460\u0005#\u0000\u0000\u0460\u0461\u0003"+
		"\u00f0x\u0000\u0461\u00ef\u0001\u0000\u0000\u0000\u0462\u0463\u0007\t"+
		"\u0000\u0000\u0463\u00f1\u0001\u0000\u0000\u0000\u0464\u0465\u0005\u001f"+
		"\u0000\u0000\u0465\u0468\u0003\u00b0X\u0000\u0466\u0467\u0005%\u0000\u0000"+
		"\u0467\u0469\u0003\u00b0X\u0000\u0468\u0466\u0001\u0000\u0000\u0000\u0468"+
		"\u0469\u0001\u0000\u0000\u0000\u0469\u00f3\u0001\u0000\u0000\u0000\u046a"+
		"\u046b\u0005\u0012\u0000\u0000\u046b\u046e\u0005?\u0000\u0000\u046c\u046f"+
		"\u0003\u00f6{\u0000\u046d\u046f\u0003@ \u0000\u046e\u046c\u0001\u0000"+
		"\u0000\u0000\u046e\u046d\u0001\u0000\u0000\u0000\u046f\u00f5\u0001\u0000"+
		"\u0000\u0000\u0470\u0475\u0003\u00f8|\u0000\u0471\u0472\u0005\u0115\u0000"+
		"\u0000\u0472\u0474\u0003\u00f8|\u0000\u0473\u0471\u0001\u0000\u0000\u0000"+
		"\u0474\u0477\u0001\u0000\u0000\u0000\u0475\u0473\u0001\u0000\u0000\u0000"+
		"\u0475\u0476\u0001\u0000\u0000\u0000\u0476\u00f7\u0001\u0000\u0000\u0000"+
		"\u0477\u0475\u0001\u0000\u0000\u0000\u0478\u047d\u0003\u00fe\u007f\u0000"+
		"\u0479\u047d\u0003\u0100\u0080\u0000\u047a\u047d\u0003\u0102\u0081\u0000"+
		"\u047b\u047d\u0003\u00fc~\u0000\u047c\u0478\u0001\u0000\u0000\u0000\u047c"+
		"\u0479\u0001\u0000\u0000\u0000\u047c\u047a\u0001\u0000\u0000\u0000\u047c"+
		"\u047b\u0001\u0000\u0000\u0000\u047d\u00f9\u0001\u0000\u0000\u0000\u047e"+
		"\u0483\u0003\u00fc~\u0000\u047f\u0480\u0005\u0115\u0000\u0000\u0480\u0482"+
		"\u0003\u00fc~\u0000\u0481\u047f\u0001\u0000\u0000\u0000\u0482\u0485\u0001"+
		"\u0000\u0000\u0000\u0483\u0481\u0001\u0000\u0000\u0000\u0483\u0484\u0001"+
		"\u0000\u0000\u0000\u0484\u00fb\u0001\u0000\u0000\u0000\u0485\u0483\u0001"+
		"\u0000\u0000\u0000\u0486\u048c\u0003\u00e0p\u0000\u0487\u0488\u0005\u011c"+
		"\u0000\u0000\u0488\u0489\u0003\u0106\u0083\u0000\u0489\u048a\u0005\u011d"+
		"\u0000\u0000\u048a\u048c\u0001\u0000\u0000\u0000\u048b\u0486\u0001\u0000"+
		"\u0000\u0000\u048b\u0487\u0001\u0000\u0000\u0000\u048c\u00fd\u0001\u0000"+
		"\u0000\u0000\u048d\u048e\u0005\u0081\u0000\u0000\u048e\u048f\u0005\u011c"+
		"\u0000\u0000\u048f\u0490\u0003\u00fa}\u0000\u0490\u0491\u0005\u011d\u0000"+
		"\u0000\u0491\u00ff\u0001\u0000\u0000\u0000\u0492\u0493\u0005G\u0000\u0000"+
		"\u0493\u0494\u0005\u011c\u0000\u0000\u0494\u0495\u0003\u00fa}\u0000\u0495"+
		"\u0496\u0005\u011d\u0000\u0000\u0496\u0101\u0001\u0000\u0000\u0000\u0497"+
		"\u0498\u0005\u011c\u0000\u0000\u0498\u0499\u0005\u011d\u0000\u0000\u0499"+
		"\u0103\u0001\u0000\u0000\u0000\u049a\u049b\u0005\u0013\u0000\u0000\u049b"+
		"\u049c\u0003\u00ceg\u0000\u049c\u0105\u0001\u0000\u0000\u0000\u049d\u04a2"+
		"\u0003\u00e0p\u0000\u049e\u049f\u0005\u0115\u0000\u0000\u049f\u04a1\u0003"+
		"\u00e0p\u0000\u04a0\u049e\u0001\u0000\u0000\u0000\u04a1\u04a4\u0001\u0000"+
		"\u0000\u0000\u04a2\u04a0\u0001\u0000\u0000\u0000\u04a2\u04a3\u0001\u0000"+
		"\u0000\u0000\u04a3\u0107\u0001\u0000\u0000\u0000\u04a4\u04a2\u0001\u0000"+
		"\u0000\u0000\u04a5\u04a6\u0003\u00e0p\u0000\u04a6\u04a7\u0003\u010a\u0085"+
		"\u0000\u04a7\u04a8\u0003\u00e0p\u0000\u04a8\u0109\u0001\u0000\u0000\u0000"+
		"\u04a9\u04b0\u0003\u0110\u0088\u0000\u04aa\u04ac\u0003\u0138\u009c\u0000"+
		"\u04ab\u04aa\u0001\u0000\u0000\u0000\u04ab\u04ac\u0001\u0000\u0000\u0000"+
		"\u04ac\u04ad\u0001\u0000\u0000\u0000\u04ad\u04b0\u0003\u010c\u0086\u0000"+
		"\u04ae\u04b0\u0003\u010e\u0087\u0000\u04af\u04a9\u0001\u0000\u0000\u0000"+
		"\u04af\u04ab\u0001\u0000\u0000\u0000\u04af\u04ae\u0001\u0000\u0000\u0000"+
		"\u04b0\u010b\u0001\u0000\u0000\u0000\u04b1\u04b8\u0005\u001e\u0000\u0000"+
		"\u04b2\u04b8\u0005\u0015\u0000\u0000\u04b3\u04b4\u0005\u0087\u0000\u0000"+
		"\u04b4\u04b8\u0005\u0092\u0000\u0000\u04b5\u04b8\u0005\u007f\u0000\u0000"+
		"\u04b6\u04b8\u0005\u0080\u0000\u0000\u04b7\u04b1\u0001\u0000\u0000\u0000"+
		"\u04b7\u04b2\u0001\u0000\u0000\u0000\u04b7\u04b3\u0001\u0000\u0000\u0000"+
		"\u04b7\u04b5\u0001\u0000\u0000\u0000\u04b7\u04b6\u0001\u0000\u0000\u0000"+
		"\u04b8\u010d\u0001\u0000\u0000\u0000\u04b9\u04ba\u0007\u0010\u0000\u0000"+
		"\u04ba\u010f\u0001\u0000\u0000\u0000\u04bb\u04bc\u0007\u0011\u0000\u0000"+
		"\u04bc\u0111\u0001\u0000\u0000\u0000\u04bd\u04bf\u0003\u00e0p\u0000\u04be"+
		"\u04c0\u0003\u0138\u009c\u0000\u04bf\u04be\u0001\u0000\u0000\u0000\u04bf"+
		"\u04c0\u0001\u0000\u0000\u0000\u04c0\u04c1\u0001\u0000\u0000\u0000\u04c1"+
		"\u04c3\u0005>\u0000\u0000\u04c2\u04c4\u0003\u0114\u008a\u0000\u04c3\u04c2"+
		"\u0001\u0000\u0000\u0000\u04c3\u04c4\u0001\u0000\u0000\u0000\u04c4\u04c5"+
		"\u0001\u0000\u0000\u0000\u04c5\u04c6\u0003\u00e0p\u0000\u04c6\u04c7\u0005"+
		"\u0003\u0000\u0000\u04c7\u04c8\u0003\u00e0p\u0000\u04c8\u0113\u0001\u0000"+
		"\u0000\u0000\u04c9\u04ca\u0007\u0012\u0000\u0000\u04ca\u0115\u0001\u0000"+
		"\u0000\u0000\u04cb\u04cd\u0003\u00e0p\u0000\u04cc\u04ce\u0003\u0138\u009c"+
		"\u0000\u04cd\u04cc\u0001\u0000\u0000\u0000\u04cd\u04ce\u0001\u0000\u0000"+
		"\u0000\u04ce\u04cf\u0001\u0000\u0000\u0000\u04cf\u04d0\u0005\u0016\u0000"+
		"\u0000\u04d0\u04d1\u0003\u011c\u008e\u0000\u04d1\u0117\u0001\u0000\u0000"+
		"\u0000\u04d2\u04d4\u0003\u00e0p\u0000\u04d3\u04d5\u0003\u0138\u009c\u0000"+
		"\u04d4\u04d3\u0001\u0000\u0000\u0000\u04d4\u04d5\u0001\u0000\u0000\u0000"+
		"\u04d5\u04d6\u0001\u0000\u0000\u0000\u04d6\u04d7\u0003\u011a\u008d\u0000"+
		"\u04d7\u04d8\u0003\u011c\u008e\u0000\u04d8\u0119\u0001\u0000\u0000\u0000"+
		"\u04d9\u04da\u0007\u0013\u0000\u0000\u04da\u04db\u0005\u0004\u0000\u0000"+
		"\u04db\u011b\u0001\u0000\u0000\u0000\u04dc\u04e3\u00038\u001c\u0000\u04dd"+
		"\u04de\u0005\u011c\u0000\u0000\u04de\u04df\u0003\u011e\u008f\u0000\u04df"+
		"\u04e0\u0005\u011d\u0000\u0000\u04e0\u04e3\u0001\u0000\u0000\u0000\u04e1"+
		"\u04e3\u0003\u0158\u00ac\u0000\u04e2\u04dc\u0001\u0000\u0000\u0000\u04e2"+
		"\u04dd\u0001\u0000\u0000\u0000\u04e2\u04e1\u0001\u0000\u0000\u0000\u04e3"+
		"\u011d\u0001\u0000\u0000\u0000\u04e4\u04e9\u0003\u00deo\u0000\u04e5\u04e6"+
		"\u0005\u0115\u0000\u0000\u04e6\u04e8\u0003\u00deo\u0000\u04e7\u04e5\u0001"+
		"\u0000\u0000\u0000\u04e8\u04eb\u0001\u0000\u0000\u0000\u04e9\u04e7\u0001"+
		"\u0000\u0000\u0000\u04e9\u04ea\u0001\u0000\u0000\u0000\u04ea\u011f\u0001"+
		"\u0000\u0000\u0000\u04eb\u04e9\u0001\u0000\u0000\u0000\u04ec\u04ed\u0003"+
		"\u0122\u0091\u0000\u04ed\u04ee\u0005\u0000\u0000\u0001\u04ee\u0121\u0001"+
		"\u0000\u0000\u0000\u04ef\u04f3\u0003\u0124\u0092\u0000\u04f0\u04f3\u0003"+
		"\u0126\u0093\u0000\u04f1\u04f3\u0003\u0128\u0094\u0000\u04f2\u04ef\u0001"+
		"\u0000\u0000\u0000\u04f2\u04f0\u0001\u0000\u0000\u0000\u04f2\u04f1\u0001"+
		"\u0000\u0000\u0000\u04f3\u0123\u0001\u0000\u0000\u0000\u04f4\u04f5\u0003"+
		"\u0128\u0094\u0000\u04f5\u04f6\u0003D\"\u0000\u04f6\u04f7\u0003\u012e"+
		"\u0097\u0000\u04f7\u0125\u0001\u0000\u0000\u0000\u04f8\u04f9\u0003\u0128"+
		"\u0094\u0000\u04f9\u04fa\u0003D\"\u0000\u04fa\u0127\u0001\u0000\u0000"+
		"\u0000\u04fb\u04fc\u0005\u011c\u0000\u0000\u04fc\u04fd\u0005\u0096\u0000"+
		"\u0000\u04fd\u04fe\u0003\u012a\u0095\u0000\u04fe\u04ff\u0005\u011d\u0000"+
		"\u0000\u04ff\u0129\u0001\u0000\u0000\u0000\u0500\u0505\u0003\u012c\u0096"+
		"\u0000\u0501\u0502\u0005\u0115\u0000\u0000\u0502\u0504\u0003\u012c\u0096"+
		"\u0000\u0503\u0501\u0001\u0000\u0000\u0000\u0504\u0507\u0001\u0000\u0000"+
		"\u0000\u0505\u0503\u0001\u0000\u0000\u0000\u0505\u0506\u0001\u0000\u0000"+
		"\u0000\u0506\u012b\u0001\u0000\u0000\u0000\u0507\u0505\u0001\u0000\u0000"+
		"\u0000\u0508\u0509\u0005\u011c\u0000\u0000\u0509\u050a\u0003\u011e\u008f"+
		"\u0000\u050a\u050b\u0005\u011d\u0000\u0000\u050b\u012d\u0001\u0000\u0000"+
		"\u0000\u050c\u050d\u0005\u011c\u0000\u0000\u050d\u050e\u0003d2\u0000\u050e"+
		"\u050f\u0005\u011d\u0000\u0000\u050f\u012f\u0001\u0000\u0000\u0000\u0510"+
		"\u0511\u0003\u00e0p\u0000\u0511\u0512\u0003\u0132\u0099\u0000\u0512\u0131"+
		"\u0001\u0000\u0000\u0000\u0513\u0515\u0005\u001a\u0000\u0000\u0514\u0516"+
		"\u0005!\u0000\u0000\u0515\u0514\u0001\u0000\u0000\u0000\u0515\u0516\u0001"+
		"\u0000\u0000\u0000\u0516\u0517\u0001\u0000\u0000\u0000\u0517\u0518\u0005"+
		"\"\u0000\u0000\u0518\u0133\u0001\u0000\u0000\u0000\u0519\u051b\u0005\u001a"+
		"\u0000\u0000\u051a\u051c\u0003\u0138\u009c\u0000\u051b\u051a\u0001\u0000"+
		"\u0000\u0000\u051b\u051c\u0001\u0000\u0000\u0000\u051c\u051d\u0001\u0000"+
		"\u0000\u0000\u051d\u051e\u0003\u0136\u009b\u0000\u051e\u0135\u0001\u0000"+
		"\u0000\u0000\u051f\u0520\u0007\u0014\u0000\u0000\u0520\u0137\u0001\u0000"+
		"\u0000\u0000\u0521\u0522\u0005!\u0000\u0000\u0522\u0139\u0001\u0000\u0000"+
		"\u0000\u0523\u0524\u0003\u00b0X\u0000\u0524\u0525\u0003\u0110\u0088\u0000"+
		"\u0525\u0526\u0003\u013c\u009e\u0000\u0526\u0527\u00038\u001c\u0000\u0527"+
		"\u013b\u0001\u0000\u0000\u0000\u0528\u052b\u0003\u013e\u009f\u0000\u0529"+
		"\u052b\u0003\u0140\u00a0\u0000\u052a\u0528\u0001\u0000\u0000\u0000\u052a"+
		"\u0529\u0001\u0000\u0000\u0000\u052b\u013d\u0001\u0000\u0000\u0000\u052c"+
		"\u052d\u0005\u0002\u0000\u0000\u052d\u013f\u0001\u0000\u0000\u0000\u052e"+
		"\u052f\u0007\u0015\u0000\u0000\u052f\u0141\u0001\u0000\u0000\u0000\u0530"+
		"\u0532\u0005!\u0000\u0000\u0531\u0530\u0001\u0000\u0000\u0000\u0531\u0532"+
		"\u0001\u0000\u0000\u0000\u0532\u0533\u0001\u0000\u0000\u0000\u0533\u0534"+
		"\u0005R\u0000\u0000\u0534\u0535\u00038\u001c\u0000\u0535\u0143\u0001\u0000"+
		"\u0000\u0000\u0536\u0537\u00056\u0000\u0000\u0537\u0538\u00038\u001c\u0000"+
		"\u0538\u0145\u0001\u0000\u0000\u0000\u0539\u053c\u0003\u0148\u00a4\u0000"+
		"\u053a\u053c\u0005\u0085\u0000\u0000\u053b\u0539\u0001\u0000\u0000\u0000"+
		"\u053b\u053a\u0001\u0000\u0000\u0000\u053c\u0147\u0001\u0000\u0000\u0000"+
		"\u053d\u053e\u0007\u0016\u0000\u0000\u053e\u0149\u0001\u0000\u0000\u0000"+
		"\u053f\u0540\u0007\u0017\u0000\u0000\u0540\u014b\u0001\u0000\u0000\u0000"+
		"\u0541\u0542\u0003\u014e\u00a7\u0000\u0542\u0544\u0005\u011c\u0000\u0000"+
		"\u0543\u0545\u0003\u0152\u00a9\u0000\u0544\u0543\u0001\u0000\u0000\u0000"+
		"\u0544\u0545\u0001\u0000\u0000\u0000\u0545\u0546\u0001\u0000\u0000\u0000"+
		"\u0546\u0547\u0005\u011d\u0000\u0000\u0547\u014d\u0001\u0000\u0000\u0000"+
		"\u0548\u054b\u0003\u0154\u00aa\u0000\u0549\u054a\u0005\u0123\u0000\u0000"+
		"\u054a\u054c\u0003\u0154\u00aa\u0000\u054b\u0549\u0001\u0000\u0000\u0000"+
		"\u054b\u054c\u0001\u0000\u0000\u0000\u054c\u054f\u0001\u0000\u0000\u0000"+
		"\u054d\u054f\u0003\u0150\u00a8\u0000\u054e\u0548\u0001\u0000\u0000\u0000"+
		"\u054e\u054d\u0001\u0000\u0000\u0000\u054f\u014f\u0001\u0000\u0000\u0000"+
		"\u0550\u0551\u0007\u0018\u0000\u0000\u0551\u0151\u0001\u0000\u0000\u0000"+
		"\u0552\u0557\u0003\u00acV\u0000\u0553\u0554\u0005\u0115\u0000\u0000\u0554"+
		"\u0556\u0003\u00acV\u0000\u0555\u0553\u0001\u0000\u0000\u0000\u0556\u0559"+
		"\u0001\u0000\u0000\u0000\u0557\u0555\u0001\u0000\u0000\u0000\u0557\u0558"+
		"\u0001\u0000\u0000\u0000\u0558\u0153\u0001\u0000\u0000\u0000\u0559\u0557"+
		"\u0001\u0000\u0000\u0000\u055a\u0560\u0003\u015a\u00ad\u0000\u055b\u0560"+
		"\u0003\u015c\u00ae\u0000\u055c\u0560\u0003\u0168\u00b4\u0000\u055d\u0560"+
		"\u0003\u0164\u00b2\u0000\u055e\u0560\u0003\u0166\u00b3\u0000\u055f\u055a"+
		"\u0001\u0000\u0000\u0000\u055f\u055b\u0001\u0000\u0000\u0000\u055f\u055c"+
		"\u0001\u0000\u0000\u0000\u055f\u055d\u0001\u0000\u0000\u0000\u055f\u055e"+
		"\u0001\u0000\u0000\u0000\u0560\u0155\u0001\u0000\u0000\u0000\u0561\u0567"+
		"\u0003\u015a\u00ad\u0000\u0562\u0567\u0003\u015c\u00ae\u0000\u0563\u0567"+
		"\u0003\u0168\u00b4\u0000\u0564\u0567\u0003\u0162\u00b1\u0000\u0565\u0567"+
		"\u0003\u0164\u00b2\u0000\u0566\u0561\u0001\u0000\u0000\u0000\u0566\u0562"+
		"\u0001\u0000\u0000\u0000\u0566\u0563\u0001\u0000\u0000\u0000\u0566\u0564"+
		"\u0001\u0000\u0000\u0000\u0566\u0565\u0001\u0000\u0000\u0000\u0567\u0157"+
		"\u0001\u0000\u0000\u0000\u0568\u056b\u0003\u015e\u00af\u0000\u0569\u056b"+
		"\u0003\u0160\u00b0\u0000\u056a\u0568\u0001\u0000\u0000\u0000\u056a\u0569"+
		"\u0001\u0000\u0000\u0000\u056b\u0159\u0001\u0000\u0000\u0000\u056c\u056d"+
		"\u0005\u0147\u0000\u0000\u056d\u015b\u0001\u0000\u0000\u0000\u056e\u056f"+
		"\u0005\u0143\u0000\u0000\u056f\u015d\u0001\u0000\u0000\u0000\u0570\u0571"+
		"\u0005\u0144\u0000\u0000\u0571\u015f\u0001\u0000\u0000\u0000\u0572\u0573"+
		"\u0007\u0019\u0000\u0000\u0573\u0161\u0001\u0000\u0000\u0000\u0574\u0575"+
		"\u0007\u001a\u0000\u0000\u0575\u0163\u0001\u0000\u0000\u0000\u0576\u0577"+
		"\u0005\u014a\u0000\u0000\u0577\u0165\u0001\u0000\u0000\u0000\u0578\u0579"+
		"\u0005\u014b\u0000\u0000\u0579\u0167\u0001\u0000\u0000\u0000\u057a\u057b"+
		"\u0007\u001b\u0000\u0000\u057b\u0169\u0001\u0000\u0000\u0000\u057c\u057e"+
		"\u0003\u00b8\\\u0000\u057d\u057c\u0001\u0000\u0000\u0000\u057d\u057e\u0001"+
		"\u0000\u0000\u0000\u057e\u057f\u0001\u0000\u0000\u0000\u057f\u0580\u0003"+
		"\u016e\u00b7\u0000\u0580\u016b\u0001\u0000\u0000\u0000\u0581\u0584\u0003"+
		"\u016e\u00b7\u0000\u0582\u0584\u0003\u0174\u00ba\u0000\u0583\u0581\u0001"+
		"\u0000\u0000\u0000\u0583\u0582\u0001\u0000\u0000\u0000\u0584\u016d\u0001"+
		"\u0000\u0000\u0000\u0585\u0588\u0005\u0129\u0000\u0000\u0586\u0588\u0003"+
		"\u0170\u00b8\u0000\u0587\u0585\u0001\u0000\u0000\u0000\u0587\u0586\u0001"+
		"\u0000\u0000\u0000\u0588\u016f\u0001\u0000\u0000\u0000\u0589\u058a\u0005"+
		"\u0129\u0000\u0000\u058a\u058c\u0005\u0123\u0000\u0000\u058b\u058d\u0005"+
		"\u0129\u0000\u0000\u058c\u058b\u0001\u0000\u0000\u0000\u058c\u058d\u0001"+
		"\u0000\u0000\u0000\u058d\u058f\u0001\u0000\u0000\u0000\u058e\u0590\u0003"+
		"\u0172\u00b9\u0000\u058f\u058e\u0001\u0000\u0000\u0000\u058f\u0590\u0001"+
		"\u0000\u0000\u0000\u0590\u0599\u0001\u0000\u0000\u0000\u0591\u0592\u0005"+
		"\u0123\u0000\u0000\u0592\u0594\u0005\u0129\u0000\u0000\u0593\u0595\u0003"+
		"\u0172\u00b9\u0000\u0594\u0593\u0001\u0000\u0000\u0000\u0594\u0595\u0001"+
		"\u0000\u0000\u0000\u0595\u0599\u0001\u0000\u0000\u0000\u0596\u0597\u0005"+
		"\u0129\u0000\u0000\u0597\u0599\u0003\u0172\u00b9\u0000\u0598\u0589\u0001"+
		"\u0000\u0000\u0000\u0598\u0591\u0001\u0000\u0000\u0000\u0598\u0596\u0001"+
		"\u0000\u0000\u0000\u0599\u0171\u0001\u0000\u0000\u0000\u059a\u059b\u0005"+
		"\u0148\u0000\u0000\u059b\u059c\u0005\u0129\u0000\u0000\u059c\u0173\u0001"+
		"\u0000\u0000\u0000\u059d\u05a1\u0003\u0176\u00bb\u0000\u059e\u05a1\u0003"+
		"\u0178\u00bc\u0000\u059f\u05a1\u0003\u0180\u00c0\u0000\u05a0\u059d\u0001"+
		"\u0000\u0000\u0000\u05a0\u059e\u0001\u0000\u0000\u0000\u05a0\u059f\u0001"+
		"\u0000\u0000\u0000\u05a1\u0175\u0001\u0000\u0000\u0000\u05a2\u05a3\u0005"+
		"\u014e\u0000\u0000\u05a3\u0177\u0001\u0000\u0000\u0000\u05a4\u05a8\u0003"+
		"\u017c\u00be\u0000\u05a5\u05a8\u0003\u017a\u00bd\u0000\u05a6\u05a8\u0003"+
		"\u017e\u00bf\u0000\u05a7\u05a4\u0001\u0000\u0000\u0000\u05a7\u05a5\u0001"+
		"\u0000\u0000\u0000\u05a7\u05a6\u0001\u0000\u0000\u0000\u05a8\u0179\u0001"+
		"\u0000\u0000\u0000\u05a9\u05aa\u0005\u00fe\u0000\u0000\u05aa\u05ab\u0005"+
		"\u014e\u0000\u0000\u05ab\u017b\u0001\u0000\u0000\u0000\u05ac\u05ad\u0005"+
		"\u0100\u0000\u0000\u05ad\u05ae\u0005\u014e\u0000\u0000\u05ae\u017d\u0001"+
		"\u0000\u0000\u0000\u05af\u05b0\u0005\u00fc\u0000\u0000\u05b0\u05b1\u0005"+
		"\u014e\u0000\u0000\u05b1\u017f\u0001\u0000\u0000\u0000\u05b2\u05b3\u0007"+
		"\u0014\u0000\u0000\u05b3\u0181\u0001\u0000\u0000\u0000\u05b4\u05b8\u0003"+
		"\u0184\u00c2\u0000\u05b5\u05b8\u0003\u018a\u00c5\u0000\u05b6\u05b8\u0003"+
		"\u0190\u00c8\u0000\u05b7\u05b4\u0001\u0000\u0000\u0000\u05b7\u05b5\u0001"+
		"\u0000\u0000\u0000\u05b7\u05b6\u0001\u0000\u0000\u0000\u05b8\u0183\u0001"+
		"\u0000\u0000\u0000\u05b9\u05bb\u0003\u0186\u00c3\u0000\u05ba\u05bc\u0003"+
		"\u0188\u00c4\u0000\u05bb\u05ba\u0001\u0000\u0000\u0000\u05bb\u05bc\u0001"+
		"\u0000\u0000\u0000\u05bc\u0185\u0001\u0000\u0000\u0000\u05bd\u05e0\u0005"+
		"A\u0000\u0000\u05be\u05e0\u0005\u00f6\u0000\u0000\u05bf\u05c0\u0005A\u0000"+
		"\u0000\u05c0\u05e0\u0005\u0099\u0000\u0000\u05c1\u05c2\u0005\u00f6\u0000"+
		"\u0000\u05c2\u05e0\u0005\u0099\u0000\u0000\u05c3\u05e0\u0005\u00f7\u0000"+
		"\u0000\u05c4\u05e0\u0005\u00f8\u0000\u0000\u05c5\u05c6\u0005r\u0000\u0000"+
		"\u05c6\u05e0\u0005A\u0000\u0000\u05c7\u05c8\u0005r\u0000\u0000\u05c8\u05e0"+
		"\u0005\u00f6\u0000\u0000\u05c9\u05e0\u0005\u00f9\u0000\u0000\u05ca\u05cb"+
		"\u0005r\u0000\u0000\u05cb\u05cc\u0005A\u0000\u0000\u05cc\u05e0\u0005\u0099"+
		"\u0000\u0000\u05cd\u05ce\u0005r\u0000\u0000\u05ce\u05cf\u0005\u00f6\u0000"+
		"\u0000\u05cf\u05e0\u0005\u0099\u0000\u0000\u05d0\u05d1\u0005\u00f9\u0000"+
		"\u0000\u05d1\u05e0\u0005\u0099\u0000\u0000\u05d2\u05e0\u0005\u00fa\u0000"+
		"\u0000\u05d3\u05e0\u0005\u0108\u0000\u0000\u05d4\u05e0\u0005\u0109\u0000"+
		"\u0000\u05d5\u05e0\u0005\u00d2\u0000\u0000\u05d6\u05e0\u0005\u00d3\u0000"+
		"\u0000\u05d7\u05d8\u0005\u00d2\u0000\u0000\u05d8\u05e0\u0005\u0099\u0000"+
		"\u0000\u05d9\u05e0\u0005\u0106\u0000\u0000\u05da\u05db\u0005\u0106\u0000"+
		"\u0000\u05db\u05e0\u0005\u0099\u0000\u0000\u05dc\u05e0\u0005\u0107\u0000"+
		"\u0000\u05dd\u05e0\u0005\u00d7\u0000\u0000\u05de\u05e0\u0005\u00fb\u0000"+
		"\u0000\u05df\u05bd\u0001\u0000\u0000\u0000\u05df\u05be\u0001\u0000\u0000"+
		"\u0000\u05df\u05bf\u0001\u0000\u0000\u0000\u05df\u05c1\u0001\u0000\u0000"+
		"\u0000\u05df\u05c3\u0001\u0000\u0000\u0000\u05df\u05c4\u0001\u0000\u0000"+
		"\u0000\u05df\u05c5\u0001\u0000\u0000\u0000\u05df\u05c7\u0001\u0000\u0000"+
		"\u0000\u05df\u05c9\u0001\u0000\u0000\u0000\u05df\u05ca\u0001\u0000\u0000"+
		"\u0000\u05df\u05cd\u0001\u0000\u0000\u0000\u05df\u05d0\u0001\u0000\u0000"+
		"\u0000\u05df\u05d2\u0001\u0000\u0000\u0000\u05df\u05d3\u0001\u0000\u0000"+
		"\u0000\u05df\u05d4\u0001\u0000\u0000\u0000\u05df\u05d5\u0001\u0000\u0000"+
		"\u0000\u05df\u05d6\u0001\u0000\u0000\u0000\u05df\u05d7\u0001\u0000\u0000"+
		"\u0000\u05df\u05d9\u0001\u0000\u0000\u0000\u05df\u05da\u0001\u0000\u0000"+
		"\u0000\u05df\u05dc\u0001\u0000\u0000\u0000\u05df\u05dd\u0001\u0000\u0000"+
		"\u0000\u05df\u05de\u0001\u0000\u0000\u0000\u05e0\u0187\u0001\u0000\u0000"+
		"\u0000\u05e1\u05e2\u0005\u011c\u0000\u0000\u05e2\u05e3\u0005\u0129\u0000"+
		"\u0000\u05e3\u05e4\u0005\u011d\u0000\u0000\u05e4\u0189\u0001\u0000\u0000"+
		"\u0000\u05e5\u05e7\u0003\u018c\u00c6\u0000\u05e6\u05e8\u0003\u018e\u00c7"+
		"\u0000\u05e7\u05e6\u0001\u0000\u0000\u0000\u05e7\u05e8\u0001\u0000\u0000"+
		"\u0000\u05e8\u018b\u0001\u0000\u0000\u0000\u05e9\u05f2\u0005\u00f4\u0000"+
		"\u0000\u05ea\u05f2\u0005\u0129\u0000\u0000\u05eb\u05f2\u0005\u00f5\u0000"+
		"\u0000\u05ec\u05f2\u0005J\u0000\u0000\u05ed\u05f2\u0005\u00f2\u0000\u0000"+
		"\u05ee\u05f2\u0005\u00f3\u0000\u0000\u05ef\u05f0\u0005\u00f3\u0000\u0000"+
		"\u05f0\u05f2\u0005z\u0000\u0000\u05f1\u05e9\u0001\u0000\u0000\u0000\u05f1"+
		"\u05ea\u0001\u0000\u0000\u0000\u05f1\u05eb\u0001\u0000\u0000\u0000\u05f1"+
		"\u05ec\u0001\u0000\u0000\u0000\u05f1\u05ed\u0001\u0000\u0000\u0000\u05f1"+
		"\u05ee\u0001\u0000\u0000\u0000\u05f1\u05ef\u0001\u0000\u0000\u0000\u05f2"+
		"\u018d\u0001\u0000\u0000\u0000\u05f3\u05f4\u0005\u011c\u0000\u0000\u05f4"+
		"\u05f5\u0005\u0129\u0000\u0000\u05f5\u05fc\u0005\u011d\u0000\u0000\u05f6"+
		"\u05f7\u0005\u011c\u0000\u0000\u05f7\u05f8\u0005\u0129\u0000\u0000\u05f8"+
		"\u05f9\u0005\u0115\u0000\u0000\u05f9\u05fa\u0005\u0129\u0000\u0000\u05fa"+
		"\u05fc\u0005\u011d\u0000\u0000\u05fb\u05f3\u0001\u0000\u0000\u0000\u05fb"+
		"\u05f6\u0001\u0000\u0000\u0000\u05fc\u018f\u0001\u0000\u0000\u0000\u05fd"+
		"\u05fe\u0003\u0192\u00c9\u0000\u05fe\u0191\u0001\u0000\u0000\u0000\u05ff"+
		"\u063d\u0005\u0105\u0000\u0000\u0600\u063d\u0005\u00df\u0000\u0000\u0601"+
		"\u063d\u0005\u00d6\u0000\u0000\u0602\u063d\u0005\u00d5\u0000\u0000\u0603"+
		"\u063d\u0005\u00d4\u0000\u0000\u0604\u063d\u0005\u010b\u0000\u0000\u0605"+
		"\u063d\u00055\u0000\u0000\u0606\u063d\u0005\u010c\u0000\u0000\u0607\u063d"+
		"\u0005\u010a\u0000\u0000\u0608\u063d\u0005\u00dc\u0000\u0000\u0609\u063d"+
		"\u0005\u00dd\u0000\u0000\u060a\u063d\u0005\u00e0\u0000\u0000\u060b\u063d"+
		"\u0005\u00e4\u0000\u0000\u060c\u063d\u0005\u00e5\u0000\u0000\u060d\u063d"+
		"\u0005\u00e1\u0000\u0000\u060e\u063d\u0005\u00e2\u0000\u0000\u060f\u063d"+
		"\u0005\u00e3\u0000\u0000\u0610\u063d\u0005\u00de\u0000\u0000\u0611\u063d"+
		"\u0005\u00d8\u0000\u0000\u0612\u063d\u0005\u00e6\u0000\u0000\u0613\u063d"+
		"\u0005\u00d9\u0000\u0000\u0614\u063d\u0005\u00e7\u0000\u0000\u0615\u063d"+
		"\u0005\u00da\u0000\u0000\u0616\u063d\u0005\u00e8\u0000\u0000\u0617\u063d"+
		"\u0005\u00e9\u0000\u0000\u0618\u063d\u0005\u00db\u0000\u0000\u0619\u063d"+
		"\u0005\u00ea\u0000\u0000\u061a\u063d\u0005\u00eb\u0000\u0000\u061b\u063d"+
		"\u0005\u00ec\u0000\u0000\u061c\u063d\u0005\u00ed\u0000\u0000\u061d\u063d"+
		"\u0005\u00ee\u0000\u0000\u061e\u063d\u0005$\u0000\u0000\u061f\u063d\u0005"+
		"\u00ef\u0000\u0000\u0620\u063d\u0005\u00f1\u0000\u0000\u0621\u063d\u0005"+
		"\u00f0\u0000\u0000\u0622\u063d\u0005\u00d0\u0000\u0000\u0623\u063d\u0005"+
		"\u00d1\u0000\u0000\u0624\u063d\u0005\u00fc\u0000\u0000\u0625\u063d\u0005"+
		"\u00fd\u0000\u0000\u0626\u063d\u0005\u00fe\u0000\u0000\u0627\u0628\u0005"+
		"\u00fe\u0000\u0000\u0628\u0629\u0005:\u0000\u0000\u0629\u062a\u0005\u00fe"+
		"\u0000\u0000\u062a\u063d\u0005\u009c\u0000\u0000\u062b\u063d\u0005\u00ff"+
		"\u0000\u0000\u062c\u063d\u0005\u0101\u0000\u0000\u062d\u063d\u0005\u0102"+
		"\u0000\u0000\u062e\u063d\u0005\u0103\u0000\u0000\u062f\u063d\u0005\u0100"+
		"\u0000\u0000\u0630\u0631\u0005\u0100\u0000\u0000\u0631\u0632\u0005:\u0000"+
		"\u0000\u0632\u0633\u0005\u00fe\u0000\u0000\u0633\u063d\u0005\u009c\u0000"+
		"\u0000\u0634\u0635\u0005\u0100\u0000\u0000\u0635\u0636\u0005;\u0000\u0000"+
		"\u0636\u0637\u0005\u00fe\u0000\u0000\u0637\u063d\u0005\u009c\u0000\u0000"+
		"\u0638\u063d\u0005\u0104\u0000\u0000\u0639\u063d\u0005\u00cd\u0000\u0000"+
		"\u063a\u063d\u0005\u00cf\u0000\u0000\u063b\u063d\u0005\u00ce\u0000\u0000"+
		"\u063c\u05ff\u0001\u0000\u0000\u0000\u063c\u0600\u0001\u0000\u0000\u0000"+
		"\u063c\u0601\u0001\u0000\u0000\u0000\u063c\u0602\u0001\u0000\u0000\u0000"+
		"\u063c\u0603\u0001\u0000\u0000\u0000\u063c\u0604\u0001\u0000\u0000\u0000"+
		"\u063c\u0605\u0001\u0000\u0000\u0000\u063c\u0606\u0001\u0000\u0000\u0000"+
		"\u063c\u0607\u0001\u0000\u0000\u0000\u063c\u0608\u0001\u0000\u0000\u0000"+
		"\u063c\u0609\u0001\u0000\u0000\u0000\u063c\u060a\u0001\u0000\u0000\u0000"+
		"\u063c\u060b\u0001\u0000\u0000\u0000\u063c\u060c\u0001\u0000\u0000\u0000"+
		"\u063c\u060d\u0001\u0000\u0000\u0000\u063c\u060e\u0001\u0000\u0000\u0000"+
		"\u063c\u060f\u0001\u0000\u0000\u0000\u063c\u0610\u0001\u0000\u0000\u0000"+
		"\u063c\u0611\u0001\u0000\u0000\u0000\u063c\u0612\u0001\u0000\u0000\u0000"+
		"\u063c\u0613\u0001\u0000\u0000\u0000\u063c\u0614\u0001\u0000\u0000\u0000"+
		"\u063c\u0615\u0001\u0000\u0000\u0000\u063c\u0616\u0001\u0000\u0000\u0000"+
		"\u063c\u0617\u0001\u0000\u0000\u0000\u063c\u0618\u0001\u0000\u0000\u0000"+
		"\u063c\u0619\u0001\u0000\u0000\u0000\u063c\u061a\u0001\u0000\u0000\u0000"+
		"\u063c\u061b\u0001\u0000\u0000\u0000\u063c\u061c\u0001\u0000\u0000\u0000"+
		"\u063c\u061d\u0001\u0000\u0000\u0000\u063c\u061e\u0001\u0000\u0000\u0000"+
		"\u063c\u061f\u0001\u0000\u0000\u0000\u063c\u0620\u0001\u0000\u0000\u0000"+
		"\u063c\u0621\u0001\u0000\u0000\u0000\u063c\u0622\u0001\u0000\u0000\u0000"+
		"\u063c\u0623\u0001\u0000\u0000\u0000\u063c\u0624\u0001\u0000\u0000\u0000"+
		"\u063c\u0625\u0001\u0000\u0000\u0000\u063c\u0626\u0001\u0000\u0000\u0000"+
		"\u063c\u0627\u0001\u0000\u0000\u0000\u063c\u062b\u0001\u0000\u0000\u0000"+
		"\u063c\u062c\u0001\u0000\u0000\u0000\u063c\u062d\u0001\u0000\u0000\u0000"+
		"\u063c\u062e\u0001\u0000\u0000\u0000\u063c\u062f\u0001\u0000\u0000\u0000"+
		"\u063c\u0630\u0001\u0000\u0000\u0000\u063c\u0634\u0001\u0000\u0000\u0000"+
		"\u063c\u0638\u0001\u0000\u0000\u0000\u063c\u0639\u0001\u0000\u0000\u0000"+
		"\u063c\u063a\u0001\u0000\u0000\u0000\u063c\u063b\u0001\u0000\u0000\u0000"+
		"\u063d\u0193\u0001\u0000\u0000\u0000\u063e\u063f\u0007\u001c\u0000\u0000"+
		"\u063f\u0195\u0001\u0000\u0000\u0000\u0097\u0198\u019b\u01b6\u01bb\u01c5"+
		"\u01d3\u01dc\u01e1\u01e8\u01eb\u01ee\u01f8\u020c\u0211\u021b\u0220\u0227"+
		"\u022f\u0232\u0237\u023a\u023d\u0240\u0243\u0245\u0251\u0256\u0259\u025c"+
		"\u0262\u0269\u0276\u0278\u027a\u0285\u0287\u0289\u028d\u0291\u0295\u029b"+
		"\u02a1\u02a7\u02a9\u02ae\u02b0\u02b8\u02bb\u02be\u02c5\u02c7\u02cb\u02dc"+
		"\u02e2\u02e9\u02ee\u02f6\u02fb\u0301\u0305\u0313\u031b\u031f\u0324\u032e"+
		"\u0335\u0339\u033e\u0348\u0354\u0367\u0370\u0373\u0375\u037a\u037d\u0380"+
		"\u0390\u039a\u03b3\u03b8\u03bf\u03c7\u03cb\u03d1\u03df\u03e5\u03ec\u03f1"+
		"\u03fb\u03fe\u0401\u0408\u0413\u041b\u041f\u0429\u042b\u042f\u0438\u043e"+
		"\u0443\u0453\u0458\u045b\u0468\u046e\u0475\u047c\u0483\u048b\u04a2\u04ab"+
		"\u04af\u04b7\u04bf\u04c3\u04cd\u04d4\u04e2\u04e9\u04f2\u0505\u0515\u051b"+
		"\u052a\u0531\u053b\u0544\u054b\u054e\u0557\u055f\u0566\u056a\u057d\u0583"+
		"\u0587\u058c\u058f\u0594\u0598\u05a0\u05a7\u05b7\u05bb\u05df\u05e7\u05f1"+
		"\u05fb\u063c";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}