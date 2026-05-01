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
		DECADE=75, DESC=76, DOW=77, DOY=78, DROP=79, EPOCH=80, ESCAPE=81, EVERY=82, 
		EXISTS=83, EXTERNAL=84, EXTRACT=85, FILTER=86, FIRST=87, FIRST_VALUE=88, 
		FOLLOWING=89, FORMAT=90, FUSION=91, GROUPING=92, HASH=93, HOUR=94, INDEX=95, 
		INSERT=96, INTERSECTION=97, ISODOW=98, ISOYEAR=99, LAG=100, LAST=101, 
		LAST_VALUE=102, LEAD=103, LESS=104, LIST=105, LOCATION=106, MAX=107, MAXVALUE=108, 
		MICROSECONDS=109, MILLENNIUM=110, MILLISECONDS=111, MIN=112, MINUTE=113, 
		MONTH=114, NATIONAL=115, NTH_VALUE=116, NULLIF=117, OVER=118, OVERWRITE=119, 
		PARTITION=120, PARTITIONS=121, PRECEDING=122, PRECISION=123, PURGE=124, 
		QUARTER=125, RANGE=126, RANK=127, REGEXP=128, RLIKE=129, ROLLUP=130, ROW_NUMBER=131, 
		ROW=132, ROWS=133, SECOND=134, SET=135, SIMILAR=136, STDDEV_POP=137, STDDEV_SAMP=138, 
		SUBPARTITION=139, SUM=140, TABLESPACE=141, THAN=142, TIMEZONE=143, TIMEZONE_HOUR=144, 
		TIMEZONE_MINUTE=145, TRIM=146, TO=147, UPDATE=148, UNBOUNDED=149, UNKNOWN=150, 
		VALUES=151, VAR_SAMP=152, VAR_POP=153, VARYING=154, WEEK=155, YEAR=156, 
		ZONE=157, ANY_VALUE=158, CORR=159, COVAR_POP=160, COVAR_SAMP=161, LISTAGG=162, 
		MEDIAN=163, PERCENTILE_CONT=164, PERCENTILE_DISC=165, STDDEV=166, VARIANCE_POP=167, 
		VARIANCE=168, VARIANCE_SAMP=169, CUME_DIST=170, DENSE_RANK=171, NTILE=172, 
		PERCENT_RANK=173, WIDTH_BUCKET=174, BITAND_AGG=175, BITOR_AGG=176, BITXOR_AGG=177, 
		HASH_AGG=178, ARRAY_AGG=179, OBJECT_AGG=180, REGR_AVGX=181, REGR_AVGY=182, 
		REGR_COUNT=183, REGR_INTERCEPT=184, REGR_R2=185, REGR_SLOPE=186, REGR_SXX=187, 
		REGR_SXY=188, REGR_SYY=189, APPROX_COUNT_DISTINCT=190, HLL=191, HLL_ACCUMULATE=192, 
		HLL_COMBINE=193, HLL_EXPORT=194, HLL_IMPORT=195, APPROXIMATE_JACCARD_INDEX=196, 
		APPROXIMATE_SIMILARITY=197, MINHASH=198, MINHASH_COMBINE=199, APPROX_TOP_K=200, 
		APPROX_TOP_K_ACCUMULATE=201, APPROX_TOP_K_COMBINE=202, APPROX_PERCENTILE=203, 
		APPROX_PERCENTILE_ACCUMULATE=204, APPROX_PERCENTILE_COMBINE=205, ABSTIME=206, 
		ANYARRAY=207, ARRAY=208, BOOLEAN=209, BOOL=210, BIT=211, VARBIT=212, CIDR=213, 
		INET=214, INET4=215, INTERVAL=216, INT1=217, INT2=218, INT4=219, INT8=220, 
		JSON=221, JSONB=222, MACADDR=223, NAME=224, OID=225, PG_LSN=226, PG_NODE_TREE=227, 
		REGPROC=228, XID=229, UUID=230, TINYINT=231, SMALLINT=232, INT=233, INTEGER=234, 
		BIGINT=235, BIGSERIAL=236, SMALLSERIAL=237, SERIAL=238, MONEY=239, FLOAT4=240, 
		FLOAT8=241, REAL=242, FLOAT=243, DOUBLE=244, NUMERIC=245, DECIMAL=246, 
		CHAR=247, VARCHAR=248, VARCHAR2=249, NCHAR=250, NVARCHAR=251, STRING=252, 
		DATE=253, DATETIME=254, TIME=255, TIMETZ=256, TIMESTAMP=257, TIMESTAMP_LTZ=258, 
		TIMESTAMP_NTZ=259, TIMESTAMP_TZ=260, TIMESTAMPTZ=261, TEXT=262, BINARY=263, 
		VARBINARY=264, BLOB=265, BYTEA=266, OBJECT=267, STRUCT=268, VARIANT=269, 
		Similar_To=270, Not_Similar_To=271, Similar_To_Case_Insensitive=272, Not_Similar_To_Case_Insensitive=273, 
		ASSIGN=274, EQUAL=275, COLON=276, SEMI_COLON=277, COMMA=278, CONCATENATION_OPERATOR=279, 
		NOT_EQUAL=280, LTH=281, LEQ=282, GTH=283, GEQ=284, IMPLIES=285, LEFT_PAREN=286, 
		RIGHT_PAREN=287, PLUS=288, MINUS=289, MULTIPLY=290, DIVIDE=291, MODULAR=292, 
		DOT=293, UNDERLINE=294, VERTICAL_BAR=295, QUOTE=296, DOUBLE_QUOTE=297, 
		CAST_OPERATOR=298, NUMBER=299, PUML_CONSTANT_TENANT_SK=300, PUML_CONSTANT_TENANT_GUID=301, 
		PUML_CONSTANT_TENANT_MASTER_ID=302, PUML_CONSTANT_TENANT_NAME=303, PUML_CONSTANT_TENANT_ACRONYM=304, 
		PUML_CONSTANT_TENANT_WEB_DOMAIN=305, PUML_CONSTANT_ES_INSTITUTION_ID=306, 
		PUML_CONSTANT_ES_INSTITUTION_CODE=307, PUML_CONSTANT_ES_INSTITUTION_NAME=308, 
		PUML_CONSTANT_SF_COUNTER_ID=309, PUML_CONSTANT_FILE_NAME=310, PUML_CONSTANT_FILE_ID=311, 
		PUML_CONSTANT_ROW_NUMBER=312, PUML_CONSTANT_OBSERVATION_TIME=313, PUML_CONSTANT_SYSTEM_DATE=314, 
		PUML_CONSTANT_SYSTEM_TIME=315, PUML_CONSTANT_FEED_RUN_ID=316, PUML_CONSTANT_FEED_NAME=317, 
		PUML_CONSTANT_TRANSACTION_RUN_ID=318, PUML_CONSTANT_TRANSACTION_NAME=319, 
		PUML_CONSTANT_POPULATION=320, PUML_CONSTANT_TARGET_MODEL_NAME=321, PUML_CONSTANT_TENANT_SALT=322, 
		PUML_CONSTANT_PIT_START_TIME=323, PUML_CONSTANT_PIT_END_TIME=324, Bracket_Identifier=325, 
		Variable_Identifier=326, Extended_Variable_Identifier=327, Mixed_Variable_Identifier=328, 
		QUALIFY=329, POSITION=330, CHARINDEX=331, INSTR=332, IFF=333, MD5=334, 
		REVERSE=335, FLATTEN=336, SPLIT_TO_TABLE=337, STRTOK_SPLIT_TO_TABLE=338, 
		GENERATOR=339, INFER_SCHEMA=340, VALIDATE=341, RESULT_SCAN=342, QUERY_HISTORY=343, 
		QUERY_HSTORY=344, INPUT=345, PATH=346, RECURSIVE=347, MODE=348, LATERAL=349, 
		ROWCOUNT=350, TIMELIMIT=351, FILES=352, FILE_FORMAT=353, IGNORE_CASE=354, 
		MAX_FILE_COUNT=355, MAX_RECORDS_PER_FILE=356, KIND=357, JOB_ID=358, Identifier=359, 
		EXPONEN=360, Scientific_Numeric_Literal=361, Numeric_Identifier=362, Double_Quoted_Numeric_Identifier=363, 
		Dollar_Sign_Identifier=364, BlockComment=365, LineComment=366, Character_String_Literal=367, 
		Space=368, White_Space=369, JINJA_OPEN=370, JINJA_CLOSE=371, BAD=372;
	public static final int
		RULE_sql = 0, RULE_column_value = 1, RULE_predicand_value = 2, RULE_in_list_predicate_value = 3, 
		RULE_condition_value = 4, RULE_tuple_value = 5, RULE_query_value = 6, 
		RULE_join_extension_value = 7, RULE_literal_value = 8, RULE_values_statement_end = 9, 
		RULE_insert_end_point = 10, RULE_with_query = 11, RULE_with_clause = 12, 
		RULE_with_list_item = 13, RULE_cte_body = 14, RULE_query_alias = 15, RULE_query = 16, 
		RULE_insert_expression = 17, RULE_snowflake_insert = 18, RULE_insert_target_table_primary = 19, 
		RULE_postgres_insert = 20, RULE_insert_preamble = 21, RULE_insert_source_primary = 22, 
		RULE_update_expression = 23, RULE_returning = 24, RULE_assignment_expression_list = 25, 
		RULE_assignment_expression = 26, RULE_create_table_as_expression = 27, 
		RULE_query_expression = 28, RULE_intersected_query = 29, RULE_intersect_clause = 30, 
		RULE_intersect_operator = 31, RULE_unionized_query = 32, RULE_union_clause = 33, 
		RULE_union_operator = 34, RULE_query_primary = 35, RULE_subquery = 36, 
		RULE_query_specification = 37, RULE_into_list = 38, RULE_set_qualifier = 39, 
		RULE_select_list = 40, RULE_select_item = 41, RULE_as_clause = 42, RULE_select_all_columns = 43, 
		RULE_wildcard_reference = 44, RULE_from_clause = 45, RULE_join_extension = 46, 
		RULE_table_reference_list = 47, RULE_join_extension_primary = 48, RULE_lateral_modifier = 49, 
		RULE_table_primary = 50, RULE_relation_as_clause = 51, RULE_tuple_primary = 52, 
		RULE_table_or_query_name = 53, RULE_unqualified_join = 54, RULE_qualified_join = 55, 
		RULE_join_type = 56, RULE_join_specification = 57, RULE_join_condition = 58, 
		RULE_named_columns_join = 59, RULE_using_term = 60, RULE_table_function_primary = 61, 
		RULE_table_function = 62, RULE_flatten_table_function = 63, RULE_flatten_argument_list = 64, 
		RULE_flatten_argument = 65, RULE_flatten_argument_value = 66, RULE_flatten_function_name = 67, 
		RULE_table_argument_literal = 68, RULE_table_argument_boolean = 69, RULE_generator_table_function = 70, 
		RULE_generator_argument_list = 71, RULE_generator_argument = 72, RULE_generator_argument_value = 73, 
		RULE_generator_function_name = 74, RULE_result_scan_table_function = 75, 
		RULE_result_scan_function_name = 76, RULE_infer_schema_table_function = 77, 
		RULE_infer_schema_argument_list = 78, RULE_infer_schema_argument = 79, 
		RULE_infer_schema_argument_value = 80, RULE_infer_schema_function_name = 81, 
		RULE_infer_schema_files_argument = 82, RULE_validate_table_function = 83, 
		RULE_validate_function_name = 84, RULE_generic_table_function = 85, RULE_table_function_name = 86, 
		RULE_table_function_argument_list = 87, RULE_column_reference_list = 88, 
		RULE_column_reference = 89, RULE_column_primary = 90, RULE_predicand_primary = 91, 
		RULE_value_expression_primary = 92, RULE_parenthesized_value_expression = 93, 
		RULE_nonparenthesized_value_expression_primary = 94, RULE_predicand_subquery = 95, 
		RULE_aggregate_function = 96, RULE_set_function_type = 97, RULE_set_qualifier_type = 98, 
		RULE_case_expression = 99, RULE_when_clause_list = 100, RULE_searched_when_clause = 101, 
		RULE_when_value_list = 102, RULE_when_value_clause = 103, RULE_else_clause = 104, 
		RULE_case_result = 105, RULE_null_literal = 106, RULE_cast_function_expression = 107, 
		RULE_cast_function_name = 108, RULE_window_over_partition_expression = 109, 
		RULE_window_function = 110, RULE_over_clause = 111, RULE_partition_by_clause = 112, 
		RULE_bracket_frame_clause = 113, RULE_rows_or_range = 114, RULE_bracket_frame_definition = 115, 
		RULE_between_frame_definition = 116, RULE_frame_edge = 117, RULE_preceding_frame_edge = 118, 
		RULE_following_frame_edge = 119, RULE_current_row_edge = 120, RULE_bracket_constraint = 121, 
		RULE_item_select_function = 122, RULE_select_direction = 123, RULE_null_handling = 124, 
		RULE_value_expression = 125, RULE_common_value_expression = 126, RULE_additive_expression = 127, 
		RULE_multiplicative_expression = 128, RULE_factor = 129, RULE_numeric_primary = 130, 
		RULE_sign = 131, RULE_extract_expression = 132, RULE_extract_field = 133, 
		RULE_time_zone_field = 134, RULE_extract_source = 135, RULE_string_value_expression = 136, 
		RULE_character_primary = 137, RULE_trim_function = 138, RULE_trim_function_name = 139, 
		RULE_trim_operands = 140, RULE_trim_specification = 141, RULE_position_function = 142, 
		RULE_position_function_name = 143, RULE_instr_function_name = 144, RULE_charindex_name = 145, 
		RULE_boolean_value_expression = 146, RULE_or_predicate = 147, RULE_and_predicate = 148, 
		RULE_negative_predicate = 149, RULE_parenthetical_predicate = 150, RULE_boolean_primary = 151, 
		RULE_predicate = 152, RULE_substitution_predicate = 153, RULE_row_value_expression = 154, 
		RULE_row_value_predicand = 155, RULE_where_clause = 156, RULE_search_condition = 157, 
		RULE_orderby_clause = 158, RULE_sort_specifier_list = 159, RULE_sort_specifier = 160, 
		RULE_order_specification = 161, RULE_null_ordering = 162, RULE_null_first_last = 163, 
		RULE_limit_clause = 164, RULE_groupby_clause = 165, RULE_grouping_element_list = 166, 
		RULE_grouping_element = 167, RULE_ordinary_grouping_set_list = 168, RULE_ordinary_grouping_set = 169, 
		RULE_rollup_list = 170, RULE_cube_list = 171, RULE_empty_grouping_set = 172, 
		RULE_having_clause = 173, RULE_qualify_clause = 174, RULE_row_value_predicand_list = 175, 
		RULE_comparison_predicate = 176, RULE_comparison_operator = 177, RULE_relative_comp_op = 178, 
		RULE_similarity_op = 179, RULE_comp_op = 180, RULE_between_predicate = 181, 
		RULE_symmetry = 182, RULE_in_predicate = 183, RULE_like_any_predicate = 184, 
		RULE_like_any_operator = 185, RULE_in_predicate_value = 186, RULE_in_value_list = 187, 
		RULE_escape_character_clause = 188, RULE_values_statement_primary = 189, 
		RULE_fully_defined_values_statement = 190, RULE_aliased_values_statement = 191, 
		RULE_values_statement = 192, RULE_values_matrix = 193, RULE_values_row = 194, 
		RULE_values_aliases = 195, RULE_values_aliases_list = 196, RULE_insert_values_statement = 197, 
		RULE_exists_predicate = 198, RULE_exists_operator = 199, RULE_exists_predicate_value = 200, 
		RULE_null_predicate = 201, RULE_is_null_clause = 202, RULE_is_clause = 203, 
		RULE_truth_value = 204, RULE_not = 205, RULE_quantified_comparison_predicate = 206, 
		RULE_quantifier = 207, RULE_all = 208, RULE_some = 209, RULE_unique_predicate = 210, 
		RULE_primary_datetime_field = 211, RULE_non_second_primary_datetime_field = 212, 
		RULE_extended_datetime_field = 213, RULE_routine_invocation = 214, RULE_function_name = 215, 
		RULE_function_names_for_reserved_words = 216, RULE_sql_argument_list = 217, 
		RULE_identifier = 218, RULE_alias_identifier = 219, RULE_variable_identifier = 220, 
		RULE_simple_identifier = 221, RULE_logical_identifier = 222, RULE_simple_variable_identifier = 223, 
		RULE_extended_variable_identifier = 224, RULE_jinja_identifier = 225, 
		RULE_jinja_function_call = 226, RULE_jinja_arg_list = 227, RULE_jinja_arg = 228, 
		RULE_jinja_variable_access = 229, RULE_jinja_name = 230, RULE_simple_numeric_identifier = 231, 
		RULE_snowflake_quoted_numeric_identifier = 232, RULE_snowflake_dollar_function_identifier = 233, 
		RULE_nonreserved_keywords = 234, RULE_signed_numeric_literal = 235, RULE_unsigned_literal = 236, 
		RULE_unsigned_numeric_literal = 237, RULE_real_number_def = 238, RULE_exponent = 239, 
		RULE_general_literal = 240, RULE_character_literal = 241, RULE_datetime_literal = 242, 
		RULE_time_literal = 243, RULE_timestamp_literal = 244, RULE_date_literal = 245, 
		RULE_boolean_literal = 246, RULE_data_type = 247, RULE_variable_size_data_type = 248, 
		RULE_variable_data_type_name = 249, RULE_type_length = 250, RULE_precision_scale_data_type = 251, 
		RULE_precision_data_type_name = 252, RULE_precision_param = 253, RULE_static_data_type = 254, 
		RULE_static_data_type_name = 255, RULE_puml_constant_identifier = 256;
	private static String[] makeRuleNames() {
		return new String[] {
			"sql", "column_value", "predicand_value", "in_list_predicate_value", 
			"condition_value", "tuple_value", "query_value", "join_extension_value", 
			"literal_value", "values_statement_end", "insert_end_point", "with_query", 
			"with_clause", "with_list_item", "cte_body", "query_alias", "query", 
			"insert_expression", "snowflake_insert", "insert_target_table_primary", 
			"postgres_insert", "insert_preamble", "insert_source_primary", "update_expression", 
			"returning", "assignment_expression_list", "assignment_expression", "create_table_as_expression", 
			"query_expression", "intersected_query", "intersect_clause", "intersect_operator", 
			"unionized_query", "union_clause", "union_operator", "query_primary", 
			"subquery", "query_specification", "into_list", "set_qualifier", "select_list", 
			"select_item", "as_clause", "select_all_columns", "wildcard_reference", 
			"from_clause", "join_extension", "table_reference_list", "join_extension_primary", 
			"lateral_modifier", "table_primary", "relation_as_clause", "tuple_primary", 
			"table_or_query_name", "unqualified_join", "qualified_join", "join_type", 
			"join_specification", "join_condition", "named_columns_join", "using_term", 
			"table_function_primary", "table_function", "flatten_table_function", 
			"flatten_argument_list", "flatten_argument", "flatten_argument_value", 
			"flatten_function_name", "table_argument_literal", "table_argument_boolean", 
			"generator_table_function", "generator_argument_list", "generator_argument", 
			"generator_argument_value", "generator_function_name", "result_scan_table_function", 
			"result_scan_function_name", "infer_schema_table_function", "infer_schema_argument_list", 
			"infer_schema_argument", "infer_schema_argument_value", "infer_schema_function_name", 
			"infer_schema_files_argument", "validate_table_function", "validate_function_name", 
			"generic_table_function", "table_function_name", "table_function_argument_list", 
			"column_reference_list", "column_reference", "column_primary", "predicand_primary", 
			"value_expression_primary", "parenthesized_value_expression", "nonparenthesized_value_expression_primary", 
			"predicand_subquery", "aggregate_function", "set_function_type", "set_qualifier_type", 
			"case_expression", "when_clause_list", "searched_when_clause", "when_value_list", 
			"when_value_clause", "else_clause", "case_result", "null_literal", "cast_function_expression", 
			"cast_function_name", "window_over_partition_expression", "window_function", 
			"over_clause", "partition_by_clause", "bracket_frame_clause", "rows_or_range", 
			"bracket_frame_definition", "between_frame_definition", "frame_edge", 
			"preceding_frame_edge", "following_frame_edge", "current_row_edge", "bracket_constraint", 
			"item_select_function", "select_direction", "null_handling", "value_expression", 
			"common_value_expression", "additive_expression", "multiplicative_expression", 
			"factor", "numeric_primary", "sign", "extract_expression", "extract_field", 
			"time_zone_field", "extract_source", "string_value_expression", "character_primary", 
			"trim_function", "trim_function_name", "trim_operands", "trim_specification", 
			"position_function", "position_function_name", "instr_function_name", 
			"charindex_name", "boolean_value_expression", "or_predicate", "and_predicate", 
			"negative_predicate", "parenthetical_predicate", "boolean_primary", "predicate", 
			"substitution_predicate", "row_value_expression", "row_value_predicand", 
			"where_clause", "search_condition", "orderby_clause", "sort_specifier_list", 
			"sort_specifier", "order_specification", "null_ordering", "null_first_last", 
			"limit_clause", "groupby_clause", "grouping_element_list", "grouping_element", 
			"ordinary_grouping_set_list", "ordinary_grouping_set", "rollup_list", 
			"cube_list", "empty_grouping_set", "having_clause", "qualify_clause", 
			"row_value_predicand_list", "comparison_predicate", "comparison_operator", 
			"relative_comp_op", "similarity_op", "comp_op", "between_predicate", 
			"symmetry", "in_predicate", "like_any_predicate", "like_any_operator", 
			"in_predicate_value", "in_value_list", "escape_character_clause", "values_statement_primary", 
			"fully_defined_values_statement", "aliased_values_statement", "values_statement", 
			"values_matrix", "values_row", "values_aliases", "values_aliases_list", 
			"insert_values_statement", "exists_predicate", "exists_operator", "exists_predicate_value", 
			"null_predicate", "is_null_clause", "is_clause", "truth_value", "not", 
			"quantified_comparison_predicate", "quantifier", "all", "some", "unique_predicate", 
			"primary_datetime_field", "non_second_primary_datetime_field", "extended_datetime_field", 
			"routine_invocation", "function_name", "function_names_for_reserved_words", 
			"sql_argument_list", "identifier", "alias_identifier", "variable_identifier", 
			"simple_identifier", "logical_identifier", "simple_variable_identifier", 
			"extended_variable_identifier", "jinja_identifier", "jinja_function_call", 
			"jinja_arg_list", "jinja_arg", "jinja_variable_access", "jinja_name", 
			"simple_numeric_identifier", "snowflake_quoted_numeric_identifier", "snowflake_dollar_function_identifier", 
			"nonreserved_keywords", "signed_numeric_literal", "unsigned_literal", 
			"unsigned_numeric_literal", "real_number_def", "exponent", "general_literal", 
			"character_literal", "datetime_literal", "time_literal", "timestamp_literal", 
			"date_literal", "boolean_literal", "data_type", "variable_size_data_type", 
			"variable_data_type_name", "type_length", "precision_scale_data_type", 
			"precision_data_type_name", "precision_param", "static_data_type", "static_data_type_name", 
			"puml_constant_identifier"
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
			null, null, null, null, null, null, "'~'", "'!~'", "'~*'", "'!~*'", "':='", 
			"'='", "':'", "';'", "','", null, null, "'<'", "'<='", "'>'", "'>='", 
			"'=>'", "'('", "')'", "'+'", "'-'", "'*'", "'/'", "'%'", null, "'_'", 
			"'|'", "'''", "'\"'", "'::'", null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, "' '", null, "'{{'", "'}}'"
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
			"ESCAPE", "EVERY", "EXISTS", "EXTERNAL", "EXTRACT", "FILTER", "FIRST", 
			"FIRST_VALUE", "FOLLOWING", "FORMAT", "FUSION", "GROUPING", "HASH", "HOUR", 
			"INDEX", "INSERT", "INTERSECTION", "ISODOW", "ISOYEAR", "LAG", "LAST", 
			"LAST_VALUE", "LEAD", "LESS", "LIST", "LOCATION", "MAX", "MAXVALUE", 
			"MICROSECONDS", "MILLENNIUM", "MILLISECONDS", "MIN", "MINUTE", "MONTH", 
			"NATIONAL", "NTH_VALUE", "NULLIF", "OVER", "OVERWRITE", "PARTITION", 
			"PARTITIONS", "PRECEDING", "PRECISION", "PURGE", "QUARTER", "RANGE", 
			"RANK", "REGEXP", "RLIKE", "ROLLUP", "ROW_NUMBER", "ROW", "ROWS", "SECOND", 
			"SET", "SIMILAR", "STDDEV_POP", "STDDEV_SAMP", "SUBPARTITION", "SUM", 
			"TABLESPACE", "THAN", "TIMEZONE", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", 
			"TRIM", "TO", "UPDATE", "UNBOUNDED", "UNKNOWN", "VALUES", "VAR_SAMP", 
			"VAR_POP", "VARYING", "WEEK", "YEAR", "ZONE", "ANY_VALUE", "CORR", "COVAR_POP", 
			"COVAR_SAMP", "LISTAGG", "MEDIAN", "PERCENTILE_CONT", "PERCENTILE_DISC", 
			"STDDEV", "VARIANCE_POP", "VARIANCE", "VARIANCE_SAMP", "CUME_DIST", "DENSE_RANK", 
			"NTILE", "PERCENT_RANK", "WIDTH_BUCKET", "BITAND_AGG", "BITOR_AGG", "BITXOR_AGG", 
			"HASH_AGG", "ARRAY_AGG", "OBJECT_AGG", "REGR_AVGX", "REGR_AVGY", "REGR_COUNT", 
			"REGR_INTERCEPT", "REGR_R2", "REGR_SLOPE", "REGR_SXX", "REGR_SXY", "REGR_SYY", 
			"APPROX_COUNT_DISTINCT", "HLL", "HLL_ACCUMULATE", "HLL_COMBINE", "HLL_EXPORT", 
			"HLL_IMPORT", "APPROXIMATE_JACCARD_INDEX", "APPROXIMATE_SIMILARITY", 
			"MINHASH", "MINHASH_COMBINE", "APPROX_TOP_K", "APPROX_TOP_K_ACCUMULATE", 
			"APPROX_TOP_K_COMBINE", "APPROX_PERCENTILE", "APPROX_PERCENTILE_ACCUMULATE", 
			"APPROX_PERCENTILE_COMBINE", "ABSTIME", "ANYARRAY", "ARRAY", "BOOLEAN", 
			"BOOL", "BIT", "VARBIT", "CIDR", "INET", "INET4", "INTERVAL", "INT1", 
			"INT2", "INT4", "INT8", "JSON", "JSONB", "MACADDR", "NAME", "OID", "PG_LSN", 
			"PG_NODE_TREE", "REGPROC", "XID", "UUID", "TINYINT", "SMALLINT", "INT", 
			"INTEGER", "BIGINT", "BIGSERIAL", "SMALLSERIAL", "SERIAL", "MONEY", "FLOAT4", 
			"FLOAT8", "REAL", "FLOAT", "DOUBLE", "NUMERIC", "DECIMAL", "CHAR", "VARCHAR", 
			"VARCHAR2", "NCHAR", "NVARCHAR", "STRING", "DATE", "DATETIME", "TIME", 
			"TIMETZ", "TIMESTAMP", "TIMESTAMP_LTZ", "TIMESTAMP_NTZ", "TIMESTAMP_TZ", 
			"TIMESTAMPTZ", "TEXT", "BINARY", "VARBINARY", "BLOB", "BYTEA", "OBJECT", 
			"STRUCT", "VARIANT", "Similar_To", "Not_Similar_To", "Similar_To_Case_Insensitive", 
			"Not_Similar_To_Case_Insensitive", "ASSIGN", "EQUAL", "COLON", "SEMI_COLON", 
			"COMMA", "CONCATENATION_OPERATOR", "NOT_EQUAL", "LTH", "LEQ", "GTH", 
			"GEQ", "IMPLIES", "LEFT_PAREN", "RIGHT_PAREN", "PLUS", "MINUS", "MULTIPLY", 
			"DIVIDE", "MODULAR", "DOT", "UNDERLINE", "VERTICAL_BAR", "QUOTE", "DOUBLE_QUOTE", 
			"CAST_OPERATOR", "NUMBER", "PUML_CONSTANT_TENANT_SK", "PUML_CONSTANT_TENANT_GUID", 
			"PUML_CONSTANT_TENANT_MASTER_ID", "PUML_CONSTANT_TENANT_NAME", "PUML_CONSTANT_TENANT_ACRONYM", 
			"PUML_CONSTANT_TENANT_WEB_DOMAIN", "PUML_CONSTANT_ES_INSTITUTION_ID", 
			"PUML_CONSTANT_ES_INSTITUTION_CODE", "PUML_CONSTANT_ES_INSTITUTION_NAME", 
			"PUML_CONSTANT_SF_COUNTER_ID", "PUML_CONSTANT_FILE_NAME", "PUML_CONSTANT_FILE_ID", 
			"PUML_CONSTANT_ROW_NUMBER", "PUML_CONSTANT_OBSERVATION_TIME", "PUML_CONSTANT_SYSTEM_DATE", 
			"PUML_CONSTANT_SYSTEM_TIME", "PUML_CONSTANT_FEED_RUN_ID", "PUML_CONSTANT_FEED_NAME", 
			"PUML_CONSTANT_TRANSACTION_RUN_ID", "PUML_CONSTANT_TRANSACTION_NAME", 
			"PUML_CONSTANT_POPULATION", "PUML_CONSTANT_TARGET_MODEL_NAME", "PUML_CONSTANT_TENANT_SALT", 
			"PUML_CONSTANT_PIT_START_TIME", "PUML_CONSTANT_PIT_END_TIME", "Bracket_Identifier", 
			"Variable_Identifier", "Extended_Variable_Identifier", "Mixed_Variable_Identifier", 
			"QUALIFY", "POSITION", "CHARINDEX", "INSTR", "IFF", "MD5", "REVERSE", 
			"FLATTEN", "SPLIT_TO_TABLE", "STRTOK_SPLIT_TO_TABLE", "GENERATOR", "INFER_SCHEMA", 
			"VALIDATE", "RESULT_SCAN", "QUERY_HISTORY", "QUERY_HSTORY", "INPUT", 
			"PATH", "RECURSIVE", "MODE", "LATERAL", "ROWCOUNT", "TIMELIMIT", "FILES", 
			"FILE_FORMAT", "IGNORE_CASE", "MAX_FILE_COUNT", "MAX_RECORDS_PER_FILE", 
			"KIND", "JOB_ID", "Identifier", "EXPONEN", "Scientific_Numeric_Literal", 
			"Numeric_Identifier", "Double_Quoted_Numeric_Identifier", "Dollar_Sign_Identifier", 
			"BlockComment", "LineComment", "Character_String_Literal", "Space", "White_Space", 
			"JINJA_OPEN", "JINJA_CLOSE", "BAD"
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


	  private boolean isJinjaPrimaryFunction(String name) {
	    if (name == null) {
	      return false;
	    }
	    String lowered = name.toLowerCase();
	    return "ref".equals(lowered)
	        || "source".equals(lowered)
	        || "stream".equals(lowered)
	        || "var".equals(lowered)
	        || "env_var".equals(lowered);
	  }

	  private boolean isJinjaObjectMethod(String objectName, String methodName) {
	    if (objectName == null || methodName == null) {
	      return false;
	    }
	    return "config".equalsIgnoreCase(objectName) && "get".equalsIgnoreCase(methodName);
	  }

	  private boolean isDisallowedSetOperatorAlias(String name) {
	    if (name == null) {
	      return false;
	    }

	    String lowered = name.toLowerCase();
	    return "union".equals(lowered)
	        || "intersect".equals(lowered)
	        || "except".equals(lowered)
	        || "intersection".equals(lowered);
	  }

	  private boolean isDisallowedJoinKeywordAlias(String name) {
	    if (name == null) {
	      return false;
	    }

	    String lowered = name.toLowerCase();
	    return "join".equals(lowered)
	        || "cross".equals(lowered)
	        || "natural".equals(lowered)
	        || "inner".equals(lowered)
	        || "left".equals(lowered)
	        || "right".equals(lowered)
	        || "full".equals(lowered);
	  }

	  private boolean isAllowedImplicitAlias(String name) {
	    return !isDisallowedSetOperatorAlias(name) && !isDisallowedJoinKeywordAlias(name);
	  }

	  private boolean isSnowflakeTableFunctionModeLiteral(String functionName, String tokenText) {
	    if (functionName == null || tokenText == null || tokenText.length() < 2) {
	      return false;
	    }

	    if (tokenText.charAt(0) != '\'' || tokenText.charAt(tokenText.length() - 1) != '\'') {
	      return false;
	    }

	    String inner = tokenText.substring(1, tokenText.length() - 1).replace("''", "'");
	    String fn = functionName.toUpperCase();

	    switch (fn) {
	      case "FLATTEN":
	      case "LATERAL_FLATTEN":
	        return "OBJECT".equalsIgnoreCase(inner)
	            || "ARRAY".equalsIgnoreCase(inner)
	            || "BOTH".equalsIgnoreCase(inner);
	      default:
	        return false;
	    }
	  }

	  private boolean containsInferSchemaLocationArgument(String text) {
	    if (text == null) {
	      return false;
	    }

	    String normalized = text.replaceAll("\\s+", "").toUpperCase();
	    return normalized.contains("LOCATION=>");
	  }

	  private boolean containsFlattenInputArgument(String text) {
	    if (text == null) {
	      return false;
	    }

	    String normalized = text.replaceAll("\\s+", "").toUpperCase();
	    return normalized.contains("INPUT=>");
	  }

	  private boolean containsGeneratorRowcountArgument(String text) {
	    if (text == null) {
	      return false;
	    }

	    String normalized = text.replaceAll("\\s+", "").toUpperCase();
	    return normalized.contains("ROWCOUNT=>");
	  }

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
		public QueryContext query() {
			return getRuleContext(QueryContext.class,0);
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
			setState(517);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case WITH:
				{
				setState(514);
				with_query();
				}
				break;
			case CREATE:
				{
				setState(515);
				create_table_as_expression();
				}
				break;
			case SELECT:
			case INSERT:
			case UPDATE:
			case LEFT_PAREN:
			case Variable_Identifier:
			case Extended_Variable_Identifier:
			case Mixed_Variable_Identifier:
				{
				setState(516);
				query();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(520);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEMI_COLON) {
				{
				setState(519);
				match(SEMI_COLON);
				}
			}

			setState(522);
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
			setState(524);
			column_primary();
			setState(525);
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
			setState(527);
			predicand_primary();
			setState(528);
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
			setState(530);
			in_predicate_value();
			setState(531);
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
		public Value_expressionContext value_expression() {
			return getRuleContext(Value_expressionContext.class,0);
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
			setState(533);
			value_expression();
			setState(534);
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
			setState(536);
			tuple_primary();
			setState(537);
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
			setState(539);
			query();
			setState(540);
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
			setState(542);
			join_extension_primary();
			setState(543);
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
			setState(547);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				{
				setState(545);
				signed_numeric_literal();
				}
				break;
			case 2:
				{
				setState(546);
				unsigned_literal();
				}
				break;
			}
			setState(549);
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
		enterRule(_localctx, 18, RULE_values_statement_end);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(551);
			values_statement_primary();
			setState(552);
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
	public static class Insert_end_pointContext extends ParserRuleContext {
		public Insert_expressionContext insert_expression() {
			return getRuleContext(Insert_expressionContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SQLSelectParserParser.EOF, 0); }
		public Insert_end_pointContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_insert_end_point; }
	}

	public final Insert_end_pointContext insert_end_point() throws RecognitionException {
		Insert_end_pointContext _localctx = new Insert_end_pointContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_insert_end_point);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(554);
			insert_expression();
			setState(555);
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
		public With_clauseContext with_clause() {
			return getRuleContext(With_clauseContext.class,0);
		}
		public QueryContext query() {
			return getRuleContext(QueryContext.class,0);
		}
		public With_queryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_with_query; }
	}

	public final With_queryContext with_query() throws RecognitionException {
		With_queryContext _localctx = new With_queryContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_with_query);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(557);
			with_clause();
			setState(558);
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
		enterRule(_localctx, 24, RULE_with_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(560);
			match(WITH);
			setState(561);
			with_list_item();
			setState(566);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(562);
				match(COMMA);
				setState(563);
				with_list_item();
				}
				}
				setState(568);
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
		public Cte_bodyContext cte_body() {
			return getRuleContext(Cte_bodyContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Variable_identifierContext variable_identifier() {
			return getRuleContext(Variable_identifierContext.class,0);
		}
		public With_list_itemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_with_list_item; }
	}

	public final With_list_itemContext with_list_item() throws RecognitionException {
		With_list_itemContext _localctx = new With_list_itemContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_with_list_item);
		try {
			setState(577);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(569);
				query_alias();
				{
				setState(570);
				match(LEFT_PAREN);
				setState(571);
				cte_body();
				setState(572);
				match(RIGHT_PAREN);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(574);
				query_alias();
				setState(575);
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
	public static class Cte_bodyContext extends ParserRuleContext {
		public With_queryContext with_query() {
			return getRuleContext(With_queryContext.class,0);
		}
		public QueryContext query() {
			return getRuleContext(QueryContext.class,0);
		}
		public Cte_bodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cte_body; }
	}

	public final Cte_bodyContext cte_body() throws RecognitionException {
		Cte_bodyContext _localctx = new Cte_bodyContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_cte_body);
		try {
			setState(581);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case WITH:
				enterOuterAlt(_localctx, 1);
				{
				setState(579);
				with_query();
				}
				break;
			case SELECT:
			case INSERT:
			case UPDATE:
			case LEFT_PAREN:
			case Variable_Identifier:
			case Extended_Variable_Identifier:
			case Mixed_Variable_Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(580);
				query();
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
		enterRule(_localctx, 30, RULE_query_alias);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(583);
			identifier();
			setState(584);
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
		public Values_statement_primaryContext values_statement_primary() {
			return getRuleContext(Values_statement_primaryContext.class,0);
		}
		public QueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query; }
	}

	public final QueryContext query() throws RecognitionException {
		QueryContext _localctx = new QueryContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_query);
		try {
			setState(590);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(586);
				query_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(587);
				insert_expression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(588);
				update_expression();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(589);
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
	public static class Insert_expressionContext extends ParserRuleContext {
		public Snowflake_insertContext snowflake_insert() {
			return getRuleContext(Snowflake_insertContext.class,0);
		}
		public Insert_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_insert_expression; }
	}

	public final Insert_expressionContext insert_expression() throws RecognitionException {
		Insert_expressionContext _localctx = new Insert_expressionContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_insert_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(592);
			snowflake_insert();
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
	public static class Snowflake_insertContext extends ParserRuleContext {
		public Insert_preambleContext insert_preamble() {
			return getRuleContext(Insert_preambleContext.class,0);
		}
		public Insert_target_table_primaryContext insert_target_table_primary() {
			return getRuleContext(Insert_target_table_primaryContext.class,0);
		}
		public Insert_source_primaryContext insert_source_primary() {
			return getRuleContext(Insert_source_primaryContext.class,0);
		}
		public Snowflake_insertContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_snowflake_insert; }
	}

	public final Snowflake_insertContext snowflake_insert() throws RecognitionException {
		Snowflake_insertContext _localctx = new Snowflake_insertContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_snowflake_insert);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(594);
			insert_preamble();
			setState(595);
			insert_target_table_primary();
			setState(596);
			insert_source_primary();
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
	public static class Insert_target_table_primaryContext extends ParserRuleContext {
		public Table_or_query_nameContext table_or_query_name() {
			return getRuleContext(Table_or_query_nameContext.class,0);
		}
		public Variable_identifierContext variable_identifier() {
			return getRuleContext(Variable_identifierContext.class,0);
		}
		public Jinja_identifierContext jinja_identifier() {
			return getRuleContext(Jinja_identifierContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public Column_reference_listContext column_reference_list() {
			return getRuleContext(Column_reference_listContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Relation_as_clauseContext relation_as_clause() {
			return getRuleContext(Relation_as_clauseContext.class,0);
		}
		public Insert_target_table_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_insert_target_table_primary; }
	}

	public final Insert_target_table_primaryContext insert_target_table_primary() throws RecognitionException {
		Insert_target_table_primaryContext _localctx = new Insert_target_table_primaryContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_insert_target_table_primary);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(601);
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
			case ESCAPE:
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
			case Bracket_Identifier:
			case Identifier:
			case Double_Quoted_Numeric_Identifier:
			case Dollar_Sign_Identifier:
				{
				setState(598);
				table_or_query_name();
				}
				break;
			case Variable_Identifier:
			case Extended_Variable_Identifier:
			case Mixed_Variable_Identifier:
				{
				setState(599);
				variable_identifier();
				}
				break;
			case JINJA_OPEN:
				{
				setState(600);
				jinja_identifier();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(607);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				{
				setState(603);
				match(LEFT_PAREN);
				setState(604);
				column_reference_list();
				setState(605);
				match(RIGHT_PAREN);
				}
				break;
			}
			setState(610);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				setState(609);
				relation_as_clause();
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
	public static class Postgres_insertContext extends ParserRuleContext {
		public Snowflake_insertContext snowflake_insert() {
			return getRuleContext(Snowflake_insertContext.class,0);
		}
		public ReturningContext returning() {
			return getRuleContext(ReturningContext.class,0);
		}
		public Postgres_insertContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_postgres_insert; }
	}

	public final Postgres_insertContext postgres_insert() throws RecognitionException {
		Postgres_insertContext _localctx = new Postgres_insertContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_postgres_insert);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(612);
			snowflake_insert();
			setState(614);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==RETURNING) {
				{
				setState(613);
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
	public static class Insert_preambleContext extends ParserRuleContext {
		public TerminalNode INSERT() { return getToken(SQLSelectParserParser.INSERT, 0); }
		public TerminalNode INTO() { return getToken(SQLSelectParserParser.INTO, 0); }
		public TerminalNode OVERWRITE() { return getToken(SQLSelectParserParser.OVERWRITE, 0); }
		public Insert_preambleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_insert_preamble; }
	}

	public final Insert_preambleContext insert_preamble() throws RecognitionException {
		Insert_preambleContext _localctx = new Insert_preambleContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_insert_preamble);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(616);
			match(INSERT);
			setState(618);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OVERWRITE) {
				{
				setState(617);
				match(OVERWRITE);
				}
			}

			setState(620);
			match(INTO);
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
	public static class Insert_source_primaryContext extends ParserRuleContext {
		public Query_expressionContext query_expression() {
			return getRuleContext(Query_expressionContext.class,0);
		}
		public Variable_identifierContext variable_identifier() {
			return getRuleContext(Variable_identifierContext.class,0);
		}
		public Insert_values_statementContext insert_values_statement() {
			return getRuleContext(Insert_values_statementContext.class,0);
		}
		public Insert_source_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_insert_source_primary; }
	}

	public final Insert_source_primaryContext insert_source_primary() throws RecognitionException {
		Insert_source_primaryContext _localctx = new Insert_source_primaryContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_insert_source_primary);
		try {
			setState(625);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(622);
				query_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(623);
				variable_identifier();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(624);
				insert_values_statement();
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
		enterRule(_localctx, 46, RULE_update_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(627);
			match(UPDATE);
			setState(628);
			table_primary();
			setState(629);
			match(SET);
			setState(630);
			assignment_expression_list();
			setState(632);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FROM) {
				{
				setState(631);
				from_clause();
				}
			}

			setState(635);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(634);
				where_clause();
				}
			}

			setState(638);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==RETURNING) {
				{
				setState(637);
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
		enterRule(_localctx, 48, RULE_returning);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(640);
			match(RETURNING);
			setState(641);
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
		enterRule(_localctx, 50, RULE_assignment_expression_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(643);
			assignment_expression();
			setState(648);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(644);
				match(COMMA);
				setState(645);
				assignment_expression();
				}
				}
				setState(650);
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
		enterRule(_localctx, 52, RULE_assignment_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(651);
			column_reference();
			setState(652);
			match(EQUAL);
			setState(653);
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
		enterRule(_localctx, 54, RULE_create_table_as_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(655);
			match(CREATE);
			setState(656);
			match(TABLE);
			setState(657);
			match(AS);
			setState(658);
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
		enterRule(_localctx, 56, RULE_query_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(660);
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
		enterRule(_localctx, 58, RULE_intersected_query);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(662);
			unionized_query();
			setState(668);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INTERSECT) {
				{
				{
				setState(663);
				intersect_clause();
				setState(664);
				unionized_query();
				}
				}
				setState(670);
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
		enterRule(_localctx, 60, RULE_intersect_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(671);
			intersect_operator();
			setState(673);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ALL || _la==DISTINCT) {
				{
				setState(672);
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
		enterRule(_localctx, 62, RULE_intersect_operator);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(675);
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
		enterRule(_localctx, 64, RULE_unionized_query);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(677);
			query_primary();
			setState(683);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EXCEPT || _la==UNION) {
				{
				{
				setState(678);
				union_clause();
				setState(679);
				query_primary();
				}
				}
				setState(685);
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
		enterRule(_localctx, 66, RULE_union_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(686);
			union_operator();
			setState(688);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ALL || _la==DISTINCT) {
				{
				setState(687);
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
		enterRule(_localctx, 68, RULE_union_operator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(690);
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
		enterRule(_localctx, 70, RULE_query_primary);
		try {
			setState(695);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LEFT_PAREN:
				enterOuterAlt(_localctx, 1);
				{
				setState(692);
				subquery();
				}
				break;
			case SELECT:
				enterOuterAlt(_localctx, 2);
				{
				setState(693);
				query_specification();
				}
				break;
			case Variable_Identifier:
			case Extended_Variable_Identifier:
			case Mixed_Variable_Identifier:
				enterOuterAlt(_localctx, 3);
				{
				setState(694);
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
		enterRule(_localctx, 72, RULE_subquery);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(697);
			match(LEFT_PAREN);
			setState(698);
			query_expression();
			setState(699);
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
		public Qualify_clauseContext qualify_clause() {
			return getRuleContext(Qualify_clauseContext.class,0);
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
		enterRule(_localctx, 74, RULE_query_specification);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(701);
			match(SELECT);
			setState(703);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==INTO) {
				{
				setState(702);
				into_list();
				}
			}

			setState(706);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ALL || _la==DISTINCT) {
				{
				setState(705);
				set_qualifier();
				}
			}

			setState(708);
			select_list();
			setState(728);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FROM) {
				{
				setState(709);
				from_clause();
				setState(711);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WHERE) {
					{
					setState(710);
					where_clause();
					}
				}

				setState(714);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==GROUP) {
					{
					setState(713);
					groupby_clause();
					}
				}

				setState(717);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HAVING) {
					{
					setState(716);
					having_clause();
					}
				}

				setState(720);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==QUALIFY) {
					{
					setState(719);
					qualify_clause();
					}
				}

				setState(723);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ORDER) {
					{
					setState(722);
					orderby_clause();
					}
				}

				setState(726);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LIMIT) {
					{
					setState(725);
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
		enterRule(_localctx, 76, RULE_into_list);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(730);
			match(INTO);
			setState(731);
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
		enterRule(_localctx, 78, RULE_set_qualifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(733);
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
		enterRule(_localctx, 80, RULE_select_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(735);
			select_item();
			setState(740);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(736);
				match(COMMA);
				setState(737);
				select_item();
				}
				}
				setState(742);
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
		enterRule(_localctx, 82, RULE_select_item);
		try {
			setState(748);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(743);
				value_expression();
				setState(745);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
				case 1:
					{
					setState(744);
					as_clause();
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(747);
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
		enterRule(_localctx, 84, RULE_as_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(751);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS) {
				{
				setState(750);
				match(AS);
				}
			}

			setState(753);
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
		public Wildcard_referenceContext wildcard_reference() {
			return getRuleContext(Wildcard_referenceContext.class,0);
		}
		public Select_all_columnsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_all_columns; }
	}

	public final Select_all_columnsContext select_all_columns() throws RecognitionException {
		Select_all_columnsContext _localctx = new Select_all_columnsContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_select_all_columns);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(755);
			wildcard_reference();
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
	public static class Wildcard_referenceContext extends ParserRuleContext {
		public Token tb_name;
		public TerminalNode MULTIPLY() { return getToken(SQLSelectParserParser.MULTIPLY, 0); }
		public TerminalNode DOT() { return getToken(SQLSelectParserParser.DOT, 0); }
		public TerminalNode Identifier() { return getToken(SQLSelectParserParser.Identifier, 0); }
		public Wildcard_referenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_wildcard_reference; }
	}

	public final Wildcard_referenceContext wildcard_reference() throws RecognitionException {
		Wildcard_referenceContext _localctx = new Wildcard_referenceContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_wildcard_reference);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(759);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Identifier) {
				{
				setState(757);
				((Wildcard_referenceContext)_localctx).tb_name = match(Identifier);
				setState(758);
				match(DOT);
				}
			}

			setState(761);
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
		enterRule(_localctx, 90, RULE_from_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(763);
			match(FROM);
			setState(764);
			table_reference_list();
			setState(766);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 326)) & ~0x3f) == 0 && ((1L << (_la - 326)) & 7L) != 0)) {
				{
				setState(765);
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
		enterRule(_localctx, 92, RULE_join_extension);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(768);
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
		public List<Lateral_modifierContext> lateral_modifier() {
			return getRuleContexts(Lateral_modifierContext.class);
		}
		public Lateral_modifierContext lateral_modifier(int i) {
			return getRuleContext(Lateral_modifierContext.class,i);
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
		enterRule(_localctx, 94, RULE_table_reference_list);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(770);
			table_primary();
			setState(792);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,42,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(790);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case COMMA:
						{
						{
						setState(771);
						match(COMMA);
						setState(773);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==LATERAL) {
							{
							setState(772);
							lateral_modifier();
							}
						}

						setState(775);
						table_primary();
						}
						}
						break;
					case CROSS:
					case NATURAL:
					case UNION:
						{
						{
						setState(776);
						unqualified_join();
						setState(778);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==LATERAL) {
							{
							setState(777);
							lateral_modifier();
							}
						}

						setState(780);
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
						setState(782);
						qualified_join();
						setState(784);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==LATERAL) {
							{
							setState(783);
							lateral_modifier();
							}
						}

						setState(786);
						((Table_reference_listContext)_localctx).right = table_primary();
						setState(788);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==ON || _la==USING) {
							{
							setState(787);
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
				setState(794);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,42,_ctx);
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
		public List<Lateral_modifierContext> lateral_modifier() {
			return getRuleContexts(Lateral_modifierContext.class);
		}
		public Lateral_modifierContext lateral_modifier(int i) {
			return getRuleContext(Lateral_modifierContext.class,i);
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
		enterRule(_localctx, 96, RULE_join_extension_primary);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(816);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 9016000322274304L) != 0) || _la==COMMA) {
				{
				setState(814);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case COMMA:
					{
					{
					setState(795);
					match(COMMA);
					setState(797);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==LATERAL) {
						{
						setState(796);
						lateral_modifier();
						}
					}

					setState(799);
					table_primary();
					}
					}
					break;
				case CROSS:
				case NATURAL:
				case UNION:
					{
					{
					setState(800);
					unqualified_join();
					setState(802);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==LATERAL) {
						{
						setState(801);
						lateral_modifier();
						}
					}

					setState(804);
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
					setState(806);
					qualified_join();
					setState(808);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==LATERAL) {
						{
						setState(807);
						lateral_modifier();
						}
					}

					setState(810);
					((Join_extension_primaryContext)_localctx).right = table_primary();
					setState(812);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==ON || _la==USING) {
						{
						setState(811);
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
				setState(818);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(820);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 326)) & ~0x3f) == 0 && ((1L << (_la - 326)) & 7L) != 0)) {
				{
				setState(819);
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
	public static class Lateral_modifierContext extends ParserRuleContext {
		public TerminalNode LATERAL() { return getToken(SQLSelectParserParser.LATERAL, 0); }
		public Lateral_modifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lateral_modifier; }
	}

	public final Lateral_modifierContext lateral_modifier() throws RecognitionException {
		Lateral_modifierContext _localctx = new Lateral_modifierContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_lateral_modifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(822);
			match(LATERAL);
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
		public Relation_as_clauseContext relation_as_clause() {
			return getRuleContext(Relation_as_clauseContext.class,0);
		}
		public Variable_identifierContext variable_identifier() {
			return getRuleContext(Variable_identifierContext.class,0);
		}
		public As_clauseContext as_clause() {
			return getRuleContext(As_clauseContext.class,0);
		}
		public Jinja_identifierContext jinja_identifier() {
			return getRuleContext(Jinja_identifierContext.class,0);
		}
		public Table_function_primaryContext table_function_primary() {
			return getRuleContext(Table_function_primaryContext.class,0);
		}
		public Values_statement_primaryContext values_statement_primary() {
			return getRuleContext(Values_statement_primaryContext.class,0);
		}
		public SubqueryContext subquery() {
			return getRuleContext(SubqueryContext.class,0);
		}
		public Table_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_primary; }
	}

	public final Table_primaryContext table_primary() throws RecognitionException {
		Table_primaryContext _localctx = new Table_primaryContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_table_primary);
		try {
			setState(844);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,54,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(824);
				table_or_query_name();
				setState(826);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,50,_ctx) ) {
				case 1:
					{
					setState(825);
					relation_as_clause();
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(828);
				variable_identifier();
				setState(829);
				as_clause();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(831);
				jinja_identifier();
				setState(833);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
				case 1:
					{
					setState(832);
					relation_as_clause();
					}
					break;
				}
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(835);
				table_function_primary();
				setState(837);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,52,_ctx) ) {
				case 1:
					{
					setState(836);
					relation_as_clause();
					}
					break;
				}
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(839);
				values_statement_primary();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(840);
				subquery();
				setState(842);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
				case 1:
					{
					setState(841);
					relation_as_clause();
					}
					break;
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
	public static class Relation_as_clauseContext extends ParserRuleContext {
		public TerminalNode AS() { return getToken(SQLSelectParserParser.AS, 0); }
		public Alias_identifierContext alias_identifier() {
			return getRuleContext(Alias_identifierContext.class,0);
		}
		public Relation_as_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relation_as_clause; }
	}

	public final Relation_as_clauseContext relation_as_clause() throws RecognitionException {
		Relation_as_clauseContext _localctx = new Relation_as_clauseContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_relation_as_clause);
		try {
			setState(850);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,55,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(846);
				match(AS);
				setState(847);
				alias_identifier();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(848);
				if (!(isAllowedImplicitAlias(_input.LT(1).getText()))) throw new FailedPredicateException(this, "isAllowedImplicitAlias(_input.LT(1).getText())");
				setState(849);
				alias_identifier();
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
		public Variable_identifierContext variable_identifier() {
			return getRuleContext(Variable_identifierContext.class,0);
		}
		public Jinja_identifierContext jinja_identifier() {
			return getRuleContext(Jinja_identifierContext.class,0);
		}
		public Table_function_primaryContext table_function_primary() {
			return getRuleContext(Table_function_primaryContext.class,0);
		}
		public Values_statement_primaryContext values_statement_primary() {
			return getRuleContext(Values_statement_primaryContext.class,0);
		}
		public SubqueryContext subquery() {
			return getRuleContext(SubqueryContext.class,0);
		}
		public Tuple_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tuple_primary; }
	}

	public final Tuple_primaryContext tuple_primary() throws RecognitionException {
		Tuple_primaryContext _localctx = new Tuple_primaryContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_tuple_primary);
		try {
			setState(858);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,56,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(852);
				table_or_query_name();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(853);
				variable_identifier();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(854);
				jinja_identifier();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(855);
				table_function_primary();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(856);
				values_statement_primary();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(857);
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
		enterRule(_localctx, 106, RULE_table_or_query_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(860);
			identifier();
			setState(866);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,58,_ctx) ) {
			case 1:
				{
				setState(861);
				match(DOT);
				setState(864);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case NUMBER:
				case Numeric_Identifier:
					{
					setState(862);
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
				case ESCAPE:
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
				case Bracket_Identifier:
				case Identifier:
				case Double_Quoted_Numeric_Identifier:
				case Dollar_Sign_Identifier:
					{
					setState(863);
					identifier();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			}
			setState(873);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
			case 1:
				{
				setState(868);
				match(DOT);
				setState(871);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case NUMBER:
				case Numeric_Identifier:
					{
					setState(869);
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
				case ESCAPE:
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
				case Bracket_Identifier:
				case Identifier:
				case Double_Quoted_Numeric_Identifier:
				case Dollar_Sign_Identifier:
					{
					setState(870);
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
		enterRule(_localctx, 108, RULE_unqualified_join);
		int _la;
		try {
			setState(884);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CROSS:
				enterOuterAlt(_localctx, 1);
				{
				setState(875);
				match(CROSS);
				setState(876);
				match(JOIN);
				}
				break;
			case UNION:
				enterOuterAlt(_localctx, 2);
				{
				setState(877);
				match(UNION);
				setState(878);
				match(JOIN);
				}
				break;
			case NATURAL:
				enterOuterAlt(_localctx, 3);
				{
				setState(879);
				match(NATURAL);
				setState(881);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 8796638347264L) != 0)) {
					{
					setState(880);
					((Unqualified_joinContext)_localctx).t = join_type();
					}
				}

				setState(883);
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
		enterRule(_localctx, 110, RULE_qualified_join);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(887);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 8796638347264L) != 0)) {
				{
				setState(886);
				((Qualified_joinContext)_localctx).t = join_type();
				}
			}

			setState(889);
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
		enterRule(_localctx, 112, RULE_join_type);
		int _la;
		try {
			setState(896);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INNER:
				enterOuterAlt(_localctx, 1);
				{
				setState(891);
				match(INNER);
				}
				break;
			case FULL:
			case LEFT:
			case RIGHT:
				enterOuterAlt(_localctx, 2);
				{
				setState(892);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 8796629958656L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(894);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OUTER) {
					{
					setState(893);
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
		enterRule(_localctx, 114, RULE_join_specification);
		try {
			setState(900);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ON:
				enterOuterAlt(_localctx, 1);
				{
				setState(898);
				join_condition();
				}
				break;
			case USING:
				enterOuterAlt(_localctx, 2);
				{
				setState(899);
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
		enterRule(_localctx, 116, RULE_join_condition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(902);
			match(ON);
			setState(903);
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
		enterRule(_localctx, 118, RULE_named_columns_join);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(905);
			using_term();
			setState(906);
			match(LEFT_PAREN);
			setState(907);
			((Named_columns_joinContext)_localctx).f = column_reference_list();
			setState(908);
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
		enterRule(_localctx, 120, RULE_using_term);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(910);
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
	public static class Table_function_primaryContext extends ParserRuleContext {
		public TerminalNode TABLE() { return getToken(SQLSelectParserParser.TABLE, 0); }
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public Table_functionContext table_function() {
			return getRuleContext(Table_functionContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Table_function_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_function_primary; }
	}

	public final Table_function_primaryContext table_function_primary() throws RecognitionException {
		Table_function_primaryContext _localctx = new Table_function_primaryContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_table_function_primary);
		try {
			setState(918);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TABLE:
				enterOuterAlt(_localctx, 1);
				{
				setState(912);
				match(TABLE);
				setState(913);
				match(LEFT_PAREN);
				setState(914);
				table_function();
				setState(915);
				match(RIGHT_PAREN);
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
			case ESCAPE:
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
			case Bracket_Identifier:
			case FLATTEN:
			case SPLIT_TO_TABLE:
			case STRTOK_SPLIT_TO_TABLE:
			case GENERATOR:
			case INFER_SCHEMA:
			case VALIDATE:
			case RESULT_SCAN:
			case QUERY_HISTORY:
			case Identifier:
			case Double_Quoted_Numeric_Identifier:
			case Dollar_Sign_Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(917);
				table_function();
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
	public static class Table_functionContext extends ParserRuleContext {
		public Flatten_table_functionContext flatten_table_function() {
			return getRuleContext(Flatten_table_functionContext.class,0);
		}
		public Generator_table_functionContext generator_table_function() {
			return getRuleContext(Generator_table_functionContext.class,0);
		}
		public Result_scan_table_functionContext result_scan_table_function() {
			return getRuleContext(Result_scan_table_functionContext.class,0);
		}
		public Infer_schema_table_functionContext infer_schema_table_function() {
			return getRuleContext(Infer_schema_table_functionContext.class,0);
		}
		public Validate_table_functionContext validate_table_function() {
			return getRuleContext(Validate_table_functionContext.class,0);
		}
		public Generic_table_functionContext generic_table_function() {
			return getRuleContext(Generic_table_functionContext.class,0);
		}
		public Table_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_function; }
	}

	public final Table_functionContext table_function() throws RecognitionException {
		Table_functionContext _localctx = new Table_functionContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_table_function);
		try {
			setState(926);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FLATTEN:
				enterOuterAlt(_localctx, 1);
				{
				setState(920);
				flatten_table_function();
				}
				break;
			case GENERATOR:
				enterOuterAlt(_localctx, 2);
				{
				setState(921);
				generator_table_function();
				}
				break;
			case RESULT_SCAN:
				enterOuterAlt(_localctx, 3);
				{
				setState(922);
				result_scan_table_function();
				}
				break;
			case INFER_SCHEMA:
				enterOuterAlt(_localctx, 4);
				{
				setState(923);
				infer_schema_table_function();
				}
				break;
			case VALIDATE:
				enterOuterAlt(_localctx, 5);
				{
				setState(924);
				validate_table_function();
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
			case ESCAPE:
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
			case Bracket_Identifier:
			case SPLIT_TO_TABLE:
			case STRTOK_SPLIT_TO_TABLE:
			case QUERY_HISTORY:
			case Identifier:
			case Double_Quoted_Numeric_Identifier:
			case Dollar_Sign_Identifier:
				enterOuterAlt(_localctx, 6);
				{
				setState(925);
				generic_table_function();
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
	public static class Flatten_table_functionContext extends ParserRuleContext {
		public Flatten_function_nameContext flatten_function_name() {
			return getRuleContext(Flatten_function_nameContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public Flatten_argument_listContext flatten_argument_list() {
			return getRuleContext(Flatten_argument_listContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Flatten_table_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flatten_table_function; }
	}

	public final Flatten_table_functionContext flatten_table_function() throws RecognitionException {
		Flatten_table_functionContext _localctx = new Flatten_table_functionContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_flatten_table_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(928);
			flatten_function_name();
			setState(929);
			match(LEFT_PAREN);
			setState(930);
			flatten_argument_list();
			setState(931);
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
	public static class Flatten_argument_listContext extends ParserRuleContext {
		public List<Flatten_argumentContext> flatten_argument() {
			return getRuleContexts(Flatten_argumentContext.class);
		}
		public Flatten_argumentContext flatten_argument(int i) {
			return getRuleContext(Flatten_argumentContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SQLSelectParserParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SQLSelectParserParser.COMMA, i);
		}
		public Flatten_argument_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flatten_argument_list; }
	}

	public final Flatten_argument_listContext flatten_argument_list() throws RecognitionException {
		Flatten_argument_listContext _localctx = new Flatten_argument_listContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_flatten_argument_list);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(933);
			flatten_argument();
			setState(938);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,69,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(934);
					match(COMMA);
					setState(935);
					flatten_argument();
					}
					} 
				}
				setState(940);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,69,_ctx);
			}
			setState(941);
			if (!(containsFlattenInputArgument(_localctx.getText()))) throw new FailedPredicateException(this, "containsFlattenInputArgument($ctx.getText())");
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
	public static class Flatten_argumentContext extends ParserRuleContext {
		public Table_argument_literalContext m;
		public TerminalNode INPUT() { return getToken(SQLSelectParserParser.INPUT, 0); }
		public TerminalNode IMPLIES() { return getToken(SQLSelectParserParser.IMPLIES, 0); }
		public Flatten_argument_valueContext flatten_argument_value() {
			return getRuleContext(Flatten_argument_valueContext.class,0);
		}
		public TerminalNode PATH() { return getToken(SQLSelectParserParser.PATH, 0); }
		public TerminalNode OUTER() { return getToken(SQLSelectParserParser.OUTER, 0); }
		public TerminalNode RECURSIVE() { return getToken(SQLSelectParserParser.RECURSIVE, 0); }
		public TerminalNode MODE() { return getToken(SQLSelectParserParser.MODE, 0); }
		public Table_argument_literalContext table_argument_literal() {
			return getRuleContext(Table_argument_literalContext.class,0);
		}
		public Flatten_argumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flatten_argument; }
	}

	public final Flatten_argumentContext flatten_argument() throws RecognitionException {
		Flatten_argumentContext _localctx = new Flatten_argumentContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_flatten_argument);
		try {
			setState(960);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INPUT:
				enterOuterAlt(_localctx, 1);
				{
				setState(943);
				match(INPUT);
				setState(944);
				match(IMPLIES);
				setState(945);
				flatten_argument_value();
				}
				break;
			case PATH:
				enterOuterAlt(_localctx, 2);
				{
				setState(946);
				match(PATH);
				setState(947);
				match(IMPLIES);
				setState(948);
				flatten_argument_value();
				}
				break;
			case OUTER:
				enterOuterAlt(_localctx, 3);
				{
				setState(949);
				match(OUTER);
				setState(950);
				match(IMPLIES);
				setState(951);
				flatten_argument_value();
				}
				break;
			case RECURSIVE:
				enterOuterAlt(_localctx, 4);
				{
				setState(952);
				match(RECURSIVE);
				setState(953);
				match(IMPLIES);
				setState(954);
				flatten_argument_value();
				}
				break;
			case MODE:
				enterOuterAlt(_localctx, 5);
				{
				setState(955);
				match(MODE);
				setState(956);
				match(IMPLIES);
				setState(957);
				((Flatten_argumentContext)_localctx).m = table_argument_literal();
				setState(958);
				if (!(isSnowflakeTableFunctionModeLiteral("FLATTEN", (((Flatten_argumentContext)_localctx).m!=null?_input.getText(((Flatten_argumentContext)_localctx).m.start,((Flatten_argumentContext)_localctx).m.stop):null)))) throw new FailedPredicateException(this, "isSnowflakeTableFunctionModeLiteral(\"FLATTEN\", $m.text)");
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
	public static class Flatten_argument_valueContext extends ParserRuleContext {
		public Value_expressionContext value_expression() {
			return getRuleContext(Value_expressionContext.class,0);
		}
		public Table_argument_literalContext table_argument_literal() {
			return getRuleContext(Table_argument_literalContext.class,0);
		}
		public Table_argument_booleanContext table_argument_boolean() {
			return getRuleContext(Table_argument_booleanContext.class,0);
		}
		public Flatten_argument_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flatten_argument_value; }
	}

	public final Flatten_argument_valueContext flatten_argument_value() throws RecognitionException {
		Flatten_argument_valueContext _localctx = new Flatten_argument_valueContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_flatten_argument_value);
		try {
			setState(965);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,71,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(962);
				value_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(963);
				table_argument_literal();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(964);
				table_argument_boolean();
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
	public static class Flatten_function_nameContext extends ParserRuleContext {
		public TerminalNode FLATTEN() { return getToken(SQLSelectParserParser.FLATTEN, 0); }
		public Flatten_function_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flatten_function_name; }
	}

	public final Flatten_function_nameContext flatten_function_name() throws RecognitionException {
		Flatten_function_nameContext _localctx = new Flatten_function_nameContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_flatten_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(967);
			match(FLATTEN);
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
	public static class Table_argument_literalContext extends ParserRuleContext {
		public TerminalNode Character_String_Literal() { return getToken(SQLSelectParserParser.Character_String_Literal, 0); }
		public Signed_numeric_literalContext signed_numeric_literal() {
			return getRuleContext(Signed_numeric_literalContext.class,0);
		}
		public Table_argument_literalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_argument_literal; }
	}

	public final Table_argument_literalContext table_argument_literal() throws RecognitionException {
		Table_argument_literalContext _localctx = new Table_argument_literalContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_table_argument_literal);
		try {
			setState(971);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Character_String_Literal:
				enterOuterAlt(_localctx, 1);
				{
				setState(969);
				match(Character_String_Literal);
				}
				break;
			case PLUS:
			case MINUS:
			case DOT:
			case NUMBER:
			case Scientific_Numeric_Literal:
				enterOuterAlt(_localctx, 2);
				{
				setState(970);
				signed_numeric_literal();
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
	public static class Table_argument_booleanContext extends ParserRuleContext {
		public TerminalNode TRUE() { return getToken(SQLSelectParserParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(SQLSelectParserParser.FALSE, 0); }
		public Table_argument_booleanContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_argument_boolean; }
	}

	public final Table_argument_booleanContext table_argument_boolean() throws RecognitionException {
		Table_argument_booleanContext _localctx = new Table_argument_booleanContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_table_argument_boolean);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(973);
			_la = _input.LA(1);
			if ( !(_la==FALSE || _la==TRUE) ) {
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
	public static class Generator_table_functionContext extends ParserRuleContext {
		public Generator_function_nameContext generator_function_name() {
			return getRuleContext(Generator_function_nameContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public Generator_argument_listContext generator_argument_list() {
			return getRuleContext(Generator_argument_listContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Generator_table_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_generator_table_function; }
	}

	public final Generator_table_functionContext generator_table_function() throws RecognitionException {
		Generator_table_functionContext _localctx = new Generator_table_functionContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_generator_table_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(975);
			generator_function_name();
			setState(976);
			match(LEFT_PAREN);
			setState(977);
			generator_argument_list();
			setState(978);
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
	public static class Generator_argument_listContext extends ParserRuleContext {
		public List<Generator_argumentContext> generator_argument() {
			return getRuleContexts(Generator_argumentContext.class);
		}
		public Generator_argumentContext generator_argument(int i) {
			return getRuleContext(Generator_argumentContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SQLSelectParserParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SQLSelectParserParser.COMMA, i);
		}
		public Generator_argument_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_generator_argument_list; }
	}

	public final Generator_argument_listContext generator_argument_list() throws RecognitionException {
		Generator_argument_listContext _localctx = new Generator_argument_listContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_generator_argument_list);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(980);
			generator_argument();
			setState(985);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,73,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(981);
					match(COMMA);
					setState(982);
					generator_argument();
					}
					} 
				}
				setState(987);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,73,_ctx);
			}
			setState(988);
			if (!(containsGeneratorRowcountArgument(_localctx.getText()))) throw new FailedPredicateException(this, "containsGeneratorRowcountArgument($ctx.getText())");
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
	public static class Generator_argumentContext extends ParserRuleContext {
		public TerminalNode ROWCOUNT() { return getToken(SQLSelectParserParser.ROWCOUNT, 0); }
		public TerminalNode IMPLIES() { return getToken(SQLSelectParserParser.IMPLIES, 0); }
		public Generator_argument_valueContext generator_argument_value() {
			return getRuleContext(Generator_argument_valueContext.class,0);
		}
		public TerminalNode TIMELIMIT() { return getToken(SQLSelectParserParser.TIMELIMIT, 0); }
		public Generator_argumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_generator_argument; }
	}

	public final Generator_argumentContext generator_argument() throws RecognitionException {
		Generator_argumentContext _localctx = new Generator_argumentContext(_ctx, getState());
		enterRule(_localctx, 144, RULE_generator_argument);
		try {
			setState(996);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ROWCOUNT:
				enterOuterAlt(_localctx, 1);
				{
				setState(990);
				match(ROWCOUNT);
				setState(991);
				match(IMPLIES);
				setState(992);
				generator_argument_value();
				}
				break;
			case TIMELIMIT:
				enterOuterAlt(_localctx, 2);
				{
				setState(993);
				match(TIMELIMIT);
				setState(994);
				match(IMPLIES);
				setState(995);
				generator_argument_value();
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
	public static class Generator_argument_valueContext extends ParserRuleContext {
		public Additive_expressionContext additive_expression() {
			return getRuleContext(Additive_expressionContext.class,0);
		}
		public Generator_argument_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_generator_argument_value; }
	}

	public final Generator_argument_valueContext generator_argument_value() throws RecognitionException {
		Generator_argument_valueContext _localctx = new Generator_argument_valueContext(_ctx, getState());
		enterRule(_localctx, 146, RULE_generator_argument_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(998);
			additive_expression();
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
	public static class Generator_function_nameContext extends ParserRuleContext {
		public TerminalNode GENERATOR() { return getToken(SQLSelectParserParser.GENERATOR, 0); }
		public Generator_function_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_generator_function_name; }
	}

	public final Generator_function_nameContext generator_function_name() throws RecognitionException {
		Generator_function_nameContext _localctx = new Generator_function_nameContext(_ctx, getState());
		enterRule(_localctx, 148, RULE_generator_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1000);
			match(GENERATOR);
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
	public static class Result_scan_table_functionContext extends ParserRuleContext {
		public Result_scan_function_nameContext result_scan_function_name() {
			return getRuleContext(Result_scan_function_nameContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Value_expressionContext value_expression() {
			return getRuleContext(Value_expressionContext.class,0);
		}
		public Result_scan_table_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_result_scan_table_function; }
	}

	public final Result_scan_table_functionContext result_scan_table_function() throws RecognitionException {
		Result_scan_table_functionContext _localctx = new Result_scan_table_functionContext(_ctx, getState());
		enterRule(_localctx, 150, RULE_result_scan_table_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1002);
			result_scan_function_name();
			setState(1003);
			match(LEFT_PAREN);
			setState(1005);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -848827245298417264L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -288230377259008257L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -2097169L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -162133984631980033L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 8947490635775L) != 0) || ((((_la - 325)) & ~0x3f) == 0 && ((1L << (_la - 325)) & 5308579579887L) != 0)) {
				{
				setState(1004);
				value_expression();
				}
			}

			setState(1007);
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
	public static class Result_scan_function_nameContext extends ParserRuleContext {
		public TerminalNode RESULT_SCAN() { return getToken(SQLSelectParserParser.RESULT_SCAN, 0); }
		public Result_scan_function_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_result_scan_function_name; }
	}

	public final Result_scan_function_nameContext result_scan_function_name() throws RecognitionException {
		Result_scan_function_nameContext _localctx = new Result_scan_function_nameContext(_ctx, getState());
		enterRule(_localctx, 152, RULE_result_scan_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1009);
			match(RESULT_SCAN);
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
	public static class Infer_schema_table_functionContext extends ParserRuleContext {
		public Infer_schema_function_nameContext infer_schema_function_name() {
			return getRuleContext(Infer_schema_function_nameContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public Infer_schema_argument_listContext infer_schema_argument_list() {
			return getRuleContext(Infer_schema_argument_listContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Infer_schema_table_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_infer_schema_table_function; }
	}

	public final Infer_schema_table_functionContext infer_schema_table_function() throws RecognitionException {
		Infer_schema_table_functionContext _localctx = new Infer_schema_table_functionContext(_ctx, getState());
		enterRule(_localctx, 154, RULE_infer_schema_table_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1011);
			infer_schema_function_name();
			setState(1012);
			match(LEFT_PAREN);
			setState(1013);
			infer_schema_argument_list();
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
	public static class Infer_schema_argument_listContext extends ParserRuleContext {
		public List<Infer_schema_argumentContext> infer_schema_argument() {
			return getRuleContexts(Infer_schema_argumentContext.class);
		}
		public Infer_schema_argumentContext infer_schema_argument(int i) {
			return getRuleContext(Infer_schema_argumentContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SQLSelectParserParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SQLSelectParserParser.COMMA, i);
		}
		public Infer_schema_argument_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_infer_schema_argument_list; }
	}

	public final Infer_schema_argument_listContext infer_schema_argument_list() throws RecognitionException {
		Infer_schema_argument_listContext _localctx = new Infer_schema_argument_listContext(_ctx, getState());
		enterRule(_localctx, 156, RULE_infer_schema_argument_list);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1016);
			infer_schema_argument();
			setState(1021);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,76,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1017);
					match(COMMA);
					setState(1018);
					infer_schema_argument();
					}
					} 
				}
				setState(1023);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,76,_ctx);
			}
			setState(1024);
			if (!(containsInferSchemaLocationArgument(_localctx.getText()))) throw new FailedPredicateException(this, "containsInferSchemaLocationArgument($ctx.getText())");
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
	public static class Infer_schema_argumentContext extends ParserRuleContext {
		public TerminalNode LOCATION() { return getToken(SQLSelectParserParser.LOCATION, 0); }
		public TerminalNode IMPLIES() { return getToken(SQLSelectParserParser.IMPLIES, 0); }
		public Infer_schema_argument_valueContext infer_schema_argument_value() {
			return getRuleContext(Infer_schema_argument_valueContext.class,0);
		}
		public TerminalNode FILE_FORMAT() { return getToken(SQLSelectParserParser.FILE_FORMAT, 0); }
		public TerminalNode FILES() { return getToken(SQLSelectParserParser.FILES, 0); }
		public TerminalNode IGNORE_CASE() { return getToken(SQLSelectParserParser.IGNORE_CASE, 0); }
		public TerminalNode MAX_FILE_COUNT() { return getToken(SQLSelectParserParser.MAX_FILE_COUNT, 0); }
		public TerminalNode MAX_RECORDS_PER_FILE() { return getToken(SQLSelectParserParser.MAX_RECORDS_PER_FILE, 0); }
		public TerminalNode KIND() { return getToken(SQLSelectParserParser.KIND, 0); }
		public Infer_schema_argumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_infer_schema_argument; }
	}

	public final Infer_schema_argumentContext infer_schema_argument() throws RecognitionException {
		Infer_schema_argumentContext _localctx = new Infer_schema_argumentContext(_ctx, getState());
		enterRule(_localctx, 158, RULE_infer_schema_argument);
		try {
			setState(1047);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LOCATION:
				enterOuterAlt(_localctx, 1);
				{
				setState(1026);
				match(LOCATION);
				setState(1027);
				match(IMPLIES);
				setState(1028);
				infer_schema_argument_value();
				}
				break;
			case FILE_FORMAT:
				enterOuterAlt(_localctx, 2);
				{
				setState(1029);
				match(FILE_FORMAT);
				setState(1030);
				match(IMPLIES);
				setState(1031);
				infer_schema_argument_value();
				}
				break;
			case FILES:
				enterOuterAlt(_localctx, 3);
				{
				setState(1032);
				match(FILES);
				setState(1033);
				match(IMPLIES);
				setState(1034);
				infer_schema_argument_value();
				}
				break;
			case IGNORE_CASE:
				enterOuterAlt(_localctx, 4);
				{
				setState(1035);
				match(IGNORE_CASE);
				setState(1036);
				match(IMPLIES);
				setState(1037);
				infer_schema_argument_value();
				}
				break;
			case MAX_FILE_COUNT:
				enterOuterAlt(_localctx, 5);
				{
				setState(1038);
				match(MAX_FILE_COUNT);
				setState(1039);
				match(IMPLIES);
				setState(1040);
				infer_schema_argument_value();
				}
				break;
			case MAX_RECORDS_PER_FILE:
				enterOuterAlt(_localctx, 6);
				{
				setState(1041);
				match(MAX_RECORDS_PER_FILE);
				setState(1042);
				match(IMPLIES);
				setState(1043);
				infer_schema_argument_value();
				}
				break;
			case KIND:
				enterOuterAlt(_localctx, 7);
				{
				setState(1044);
				match(KIND);
				setState(1045);
				match(IMPLIES);
				setState(1046);
				infer_schema_argument_value();
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
	public static class Infer_schema_argument_valueContext extends ParserRuleContext {
		public Table_argument_literalContext table_argument_literal() {
			return getRuleContext(Table_argument_literalContext.class,0);
		}
		public Infer_schema_files_argumentContext infer_schema_files_argument() {
			return getRuleContext(Infer_schema_files_argumentContext.class,0);
		}
		public Additive_expressionContext additive_expression() {
			return getRuleContext(Additive_expressionContext.class,0);
		}
		public Table_argument_booleanContext table_argument_boolean() {
			return getRuleContext(Table_argument_booleanContext.class,0);
		}
		public Infer_schema_argument_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_infer_schema_argument_value; }
	}

	public final Infer_schema_argument_valueContext infer_schema_argument_value() throws RecognitionException {
		Infer_schema_argument_valueContext _localctx = new Infer_schema_argument_valueContext(_ctx, getState());
		enterRule(_localctx, 160, RULE_infer_schema_argument_value);
		try {
			setState(1053);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,78,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1049);
				table_argument_literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1050);
				infer_schema_files_argument();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1051);
				additive_expression();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1052);
				table_argument_boolean();
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
	public static class Infer_schema_function_nameContext extends ParserRuleContext {
		public TerminalNode INFER_SCHEMA() { return getToken(SQLSelectParserParser.INFER_SCHEMA, 0); }
		public Infer_schema_function_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_infer_schema_function_name; }
	}

	public final Infer_schema_function_nameContext infer_schema_function_name() throws RecognitionException {
		Infer_schema_function_nameContext _localctx = new Infer_schema_function_nameContext(_ctx, getState());
		enterRule(_localctx, 162, RULE_infer_schema_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1055);
			match(INFER_SCHEMA);
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
	public static class Infer_schema_files_argumentContext extends ParserRuleContext {
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public List<TerminalNode> Character_String_Literal() { return getTokens(SQLSelectParserParser.Character_String_Literal); }
		public TerminalNode Character_String_Literal(int i) {
			return getToken(SQLSelectParserParser.Character_String_Literal, i);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public List<TerminalNode> COMMA() { return getTokens(SQLSelectParserParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SQLSelectParserParser.COMMA, i);
		}
		public Infer_schema_files_argumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_infer_schema_files_argument; }
	}

	public final Infer_schema_files_argumentContext infer_schema_files_argument() throws RecognitionException {
		Infer_schema_files_argumentContext _localctx = new Infer_schema_files_argumentContext(_ctx, getState());
		enterRule(_localctx, 164, RULE_infer_schema_files_argument);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1057);
			match(LEFT_PAREN);
			setState(1058);
			match(Character_String_Literal);
			setState(1063);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1059);
				match(COMMA);
				setState(1060);
				match(Character_String_Literal);
				}
				}
				setState(1065);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1066);
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
	public static class Validate_table_functionContext extends ParserRuleContext {
		public Validate_function_nameContext validate_function_name() {
			return getRuleContext(Validate_function_nameContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public Table_or_query_nameContext table_or_query_name() {
			return getRuleContext(Table_or_query_nameContext.class,0);
		}
		public TerminalNode COMMA() { return getToken(SQLSelectParserParser.COMMA, 0); }
		public TerminalNode JOB_ID() { return getToken(SQLSelectParserParser.JOB_ID, 0); }
		public TerminalNode IMPLIES() { return getToken(SQLSelectParserParser.IMPLIES, 0); }
		public Table_argument_literalContext table_argument_literal() {
			return getRuleContext(Table_argument_literalContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Validate_table_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_validate_table_function; }
	}

	public final Validate_table_functionContext validate_table_function() throws RecognitionException {
		Validate_table_functionContext _localctx = new Validate_table_functionContext(_ctx, getState());
		enterRule(_localctx, 166, RULE_validate_table_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1068);
			validate_function_name();
			setState(1069);
			match(LEFT_PAREN);
			setState(1070);
			table_or_query_name();
			setState(1071);
			match(COMMA);
			setState(1072);
			match(JOB_ID);
			setState(1073);
			match(IMPLIES);
			setState(1074);
			table_argument_literal();
			setState(1075);
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
	public static class Validate_function_nameContext extends ParserRuleContext {
		public TerminalNode VALIDATE() { return getToken(SQLSelectParserParser.VALIDATE, 0); }
		public Validate_function_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_validate_function_name; }
	}

	public final Validate_function_nameContext validate_function_name() throws RecognitionException {
		Validate_function_nameContext _localctx = new Validate_function_nameContext(_ctx, getState());
		enterRule(_localctx, 168, RULE_validate_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1077);
			match(VALIDATE);
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
	public static class Generic_table_functionContext extends ParserRuleContext {
		public Table_function_nameContext table_function_name() {
			return getRuleContext(Table_function_nameContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Table_function_argument_listContext table_function_argument_list() {
			return getRuleContext(Table_function_argument_listContext.class,0);
		}
		public Generic_table_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_generic_table_function; }
	}

	public final Generic_table_functionContext generic_table_function() throws RecognitionException {
		Generic_table_functionContext _localctx = new Generic_table_functionContext(_ctx, getState());
		enterRule(_localctx, 170, RULE_generic_table_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1079);
			table_function_name();
			setState(1080);
			match(LEFT_PAREN);
			setState(1082);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -848827245298417264L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -288230377259008257L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -2097169L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -162133984631980033L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 8947490635775L) != 0) || ((((_la - 325)) & ~0x3f) == 0 && ((1L << (_la - 325)) & 5308579579887L) != 0)) {
				{
				setState(1081);
				table_function_argument_list();
				}
			}

			setState(1084);
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
	public static class Table_function_nameContext extends ParserRuleContext {
		public TerminalNode SPLIT_TO_TABLE() { return getToken(SQLSelectParserParser.SPLIT_TO_TABLE, 0); }
		public TerminalNode STRTOK_SPLIT_TO_TABLE() { return getToken(SQLSelectParserParser.STRTOK_SPLIT_TO_TABLE, 0); }
		public TerminalNode QUERY_HISTORY() { return getToken(SQLSelectParserParser.QUERY_HISTORY, 0); }
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public Table_function_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_function_name; }
	}

	public final Table_function_nameContext table_function_name() throws RecognitionException {
		Table_function_nameContext _localctx = new Table_function_nameContext(_ctx, getState());
		enterRule(_localctx, 172, RULE_table_function_name);
		try {
			setState(1090);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SPLIT_TO_TABLE:
				enterOuterAlt(_localctx, 1);
				{
				setState(1086);
				match(SPLIT_TO_TABLE);
				}
				break;
			case STRTOK_SPLIT_TO_TABLE:
				enterOuterAlt(_localctx, 2);
				{
				setState(1087);
				match(STRTOK_SPLIT_TO_TABLE);
				}
				break;
			case QUERY_HISTORY:
				enterOuterAlt(_localctx, 3);
				{
				setState(1088);
				match(QUERY_HISTORY);
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
			case ESCAPE:
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
			case Bracket_Identifier:
			case Identifier:
			case Double_Quoted_Numeric_Identifier:
			case Dollar_Sign_Identifier:
				enterOuterAlt(_localctx, 4);
				{
				setState(1089);
				identifier();
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
	public static class Table_function_argument_listContext extends ParserRuleContext {
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
		public Table_function_argument_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_function_argument_list; }
	}

	public final Table_function_argument_listContext table_function_argument_list() throws RecognitionException {
		Table_function_argument_listContext _localctx = new Table_function_argument_listContext(_ctx, getState());
		enterRule(_localctx, 174, RULE_table_function_argument_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1092);
			value_expression();
			setState(1097);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1093);
				match(COMMA);
				setState(1094);
				value_expression();
				}
				}
				setState(1099);
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
		enterRule(_localctx, 176, RULE_column_reference_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1100);
			column_reference();
			setState(1105);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1101);
				match(COMMA);
				setState(1102);
				column_reference();
				}
				}
				setState(1107);
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
		public IdentifierContext identifier;
		public List<IdentifierContext> path_name = new ArrayList<IdentifierContext>();
		public Variable_identifierContext substitution;
		public List<IdentifierContext> identifier() {
			return getRuleContexts(IdentifierContext.class);
		}
		public IdentifierContext identifier(int i) {
			return getRuleContext(IdentifierContext.class,i);
		}
		public TerminalNode DOT() { return getToken(SQLSelectParserParser.DOT, 0); }
		public List<TerminalNode> COLON() { return getTokens(SQLSelectParserParser.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(SQLSelectParserParser.COLON, i);
		}
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
		enterRule(_localctx, 178, RULE_column_reference);
		try {
			int _alt;
			setState(1125);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,86,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1111);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,84,_ctx) ) {
				case 1:
					{
					setState(1108);
					((Column_referenceContext)_localctx).tb_name = identifier();
					setState(1109);
					match(DOT);
					}
					break;
				}
				setState(1113);
				((Column_referenceContext)_localctx).name = identifier();
				setState(1118);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,85,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1114);
						match(COLON);
						setState(1115);
						((Column_referenceContext)_localctx).identifier = identifier();
						((Column_referenceContext)_localctx).path_name.add(((Column_referenceContext)_localctx).identifier);
						}
						} 
					}
					setState(1120);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,85,_ctx);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1121);
				((Column_referenceContext)_localctx).tb_name = identifier();
				setState(1122);
				match(DOT);
				setState(1123);
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
		public IdentifierContext identifier;
		public List<IdentifierContext> path_name = new ArrayList<IdentifierContext>();
		public Variable_identifierContext substitution;
		public List<IdentifierContext> identifier() {
			return getRuleContexts(IdentifierContext.class);
		}
		public IdentifierContext identifier(int i) {
			return getRuleContext(IdentifierContext.class,i);
		}
		public TerminalNode DOT() { return getToken(SQLSelectParserParser.DOT, 0); }
		public List<TerminalNode> COLON() { return getTokens(SQLSelectParserParser.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(SQLSelectParserParser.COLON, i);
		}
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
		enterRule(_localctx, 180, RULE_column_primary);
		int _la;
		try {
			setState(1145);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,89,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1130);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,87,_ctx) ) {
				case 1:
					{
					setState(1127);
					((Column_primaryContext)_localctx).tb_name = identifier();
					setState(1128);
					match(DOT);
					}
					break;
				}
				setState(1132);
				((Column_primaryContext)_localctx).name = identifier();
				setState(1137);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COLON) {
					{
					{
					setState(1133);
					match(COLON);
					setState(1134);
					((Column_primaryContext)_localctx).identifier = identifier();
					((Column_primaryContext)_localctx).path_name.add(((Column_primaryContext)_localctx).identifier);
					}
					}
					setState(1139);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1140);
				((Column_primaryContext)_localctx).tb_name = identifier();
				setState(1141);
				match(DOT);
				setState(1142);
				((Column_primaryContext)_localctx).substitution = variable_identifier();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1144);
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
		public String_value_expressionContext string_value_expression() {
			return getRuleContext(String_value_expressionContext.class,0);
		}
		public SignContext sign() {
			return getRuleContext(SignContext.class,0);
		}
		public Numeric_primaryContext numeric_primary() {
			return getRuleContext(Numeric_primaryContext.class,0);
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
		public Position_functionContext position_function() {
			return getRuleContext(Position_functionContext.class,0);
		}
		public Predicand_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicand_primary; }
	}

	public final Predicand_primaryContext predicand_primary() throws RecognitionException {
		Predicand_primaryContext _localctx = new Predicand_primaryContext(_ctx, getState());
		enterRule(_localctx, 182, RULE_predicand_primary);
		try {
			setState(1157);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,90,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1147);
				value_expression_primary();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1148);
				string_value_expression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1149);
				sign();
				setState(1150);
				numeric_primary();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1152);
				trim_function();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1153);
				null_literal();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1154);
				variable_identifier();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1155);
				puml_constant_identifier();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1156);
				position_function();
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
		public TerminalNode CAST_OPERATOR() { return getToken(SQLSelectParserParser.CAST_OPERATOR, 0); }
		public Data_typeContext data_type() {
			return getRuleContext(Data_typeContext.class,0);
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
		enterRule(_localctx, 184, RULE_value_expression_primary);
		try {
			setState(1169);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,93,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1159);
				parenthesized_value_expression();
				setState(1162);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,91,_ctx) ) {
				case 1:
					{
					setState(1160);
					match(CAST_OPERATOR);
					setState(1161);
					data_type();
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1164);
				nonparenthesized_value_expression_primary();
				setState(1167);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,92,_ctx) ) {
				case 1:
					{
					setState(1165);
					match(CAST_OPERATOR);
					setState(1166);
					data_type();
					}
					break;
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
		enterRule(_localctx, 186, RULE_parenthesized_value_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1171);
			match(LEFT_PAREN);
			setState(1172);
			value_expression();
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
		public Position_functionContext position_function() {
			return getRuleContext(Position_functionContext.class,0);
		}
		public Window_over_partition_expressionContext window_over_partition_expression() {
			return getRuleContext(Window_over_partition_expressionContext.class,0);
		}
		public Predicand_subqueryContext predicand_subquery() {
			return getRuleContext(Predicand_subqueryContext.class,0);
		}
		public Nonparenthesized_value_expression_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nonparenthesized_value_expression_primary; }
	}

	public final Nonparenthesized_value_expression_primaryContext nonparenthesized_value_expression_primary() throws RecognitionException {
		Nonparenthesized_value_expression_primaryContext _localctx = new Nonparenthesized_value_expression_primaryContext(_ctx, getState());
		enterRule(_localctx, 188, RULE_nonparenthesized_value_expression_primary);
		try {
			setState(1184);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,94,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1175);
				unsigned_literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1176);
				column_reference();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1177);
				aggregate_function();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1178);
				case_expression();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1179);
				cast_function_expression();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1180);
				routine_invocation();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1181);
				position_function();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1182);
				window_over_partition_expression();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(1183);
				predicand_subquery();
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
	public static class Predicand_subqueryContext extends ParserRuleContext {
		public SubqueryContext subquery() {
			return getRuleContext(SubqueryContext.class,0);
		}
		public Predicand_subqueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicand_subquery; }
	}

	public final Predicand_subqueryContext predicand_subquery() throws RecognitionException {
		Predicand_subqueryContext _localctx = new Predicand_subqueryContext(_ctx, getState());
		enterRule(_localctx, 190, RULE_predicand_subquery);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1186);
			subquery();
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
		public Wildcard_referenceContext wildcard_reference() {
			return getRuleContext(Wildcard_referenceContext.class,0);
		}
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
		enterRule(_localctx, 192, RULE_aggregate_function);
		int _la;
		try {
			setState(1204);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,97,_ctx) ) {
			case 1:
				_localctx = new Count_all_aggregateContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(1188);
				match(COUNT);
				setState(1189);
				match(LEFT_PAREN);
				setState(1190);
				wildcard_reference();
				setState(1191);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				_localctx = new General_set_functionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(1195);
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
					setState(1193);
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
					setState(1194);
					set_qualifier_type();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(1197);
				match(LEFT_PAREN);
				setState(1199);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ALL || _la==DISTINCT) {
					{
					setState(1198);
					set_qualifier();
					}
				}

				setState(1201);
				value_expression();
				setState(1202);
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
		enterRule(_localctx, 194, RULE_set_function_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1206);
			_la = _input.LA(1);
			if ( !(((((_la - 61)) & ~0x3f) == 0 && ((1L << (_la - 61)) & 38358112536625921L) != 0) || ((((_la - 127)) & ~0x3f) == 0 && ((1L << (_la - 127)) & -2046809071L) != 0) || ((((_la - 191)) & ~0x3f) == 0 && ((1L << (_la - 191)) & 32767L) != 0)) ) {
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
		enterRule(_localctx, 196, RULE_set_qualifier_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1208);
			_la = _input.LA(1);
			if ( !(_la==ANY || _la==SOME || ((((_la - 66)) & ~0x3f) == 0 && ((1L << (_la - 66)) & 2181103617L) != 0)) ) {
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
		enterRule(_localctx, 198, RULE_case_expression);
		int _la;
		try {
			setState(1225);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,100,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1210);
				match(CASE);
				setState(1211);
				value_expression();
				setState(1212);
				when_value_list();
				setState(1214);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSE) {
					{
					setState(1213);
					else_clause();
					}
				}

				setState(1216);
				match(END);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1218);
				match(CASE);
				setState(1219);
				when_clause_list();
				setState(1221);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSE) {
					{
					setState(1220);
					else_clause();
					}
				}

				setState(1223);
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
		enterRule(_localctx, 200, RULE_when_clause_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1228); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(1227);
				searched_when_clause();
				}
				}
				setState(1230); 
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
		public Searched_when_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_searched_when_clause; }
	}

	public final Searched_when_clauseContext searched_when_clause() throws RecognitionException {
		Searched_when_clauseContext _localctx = new Searched_when_clauseContext(_ctx, getState());
		enterRule(_localctx, 202, RULE_searched_when_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1232);
			match(WHEN);
			setState(1233);
			((Searched_when_clauseContext)_localctx).c = value_expression();
			setState(1234);
			match(THEN);
			setState(1235);
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
		enterRule(_localctx, 204, RULE_when_value_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1238); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(1237);
				when_value_clause();
				}
				}
				setState(1240); 
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
		enterRule(_localctx, 206, RULE_when_value_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1242);
			match(WHEN);
			setState(1243);
			((When_value_clauseContext)_localctx).c = value_expression();
			setState(1244);
			match(THEN);
			setState(1245);
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
		enterRule(_localctx, 208, RULE_else_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1247);
			match(ELSE);
			setState(1248);
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
		enterRule(_localctx, 210, RULE_case_result);
		try {
			setState(1252);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,103,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1250);
				value_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1251);
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
		enterRule(_localctx, 212, RULE_null_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1254);
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
		enterRule(_localctx, 214, RULE_cast_function_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1256);
			cast_function_name();
			setState(1257);
			match(LEFT_PAREN);
			setState(1258);
			value_expression();
			setState(1259);
			match(AS);
			setState(1260);
			data_type();
			setState(1261);
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
		enterRule(_localctx, 216, RULE_cast_function_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1263);
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
		enterRule(_localctx, 218, RULE_window_over_partition_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1265);
			window_function();
			setState(1266);
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
		enterRule(_localctx, 220, RULE_window_function);
		int _la;
		try {
			setState(1285);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,107,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1268);
				set_function_type();
				setState(1269);
				match(LEFT_PAREN);
				setState(1271);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -848827245298417264L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -288230377259008257L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -2097169L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -162133984631980033L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 8947490635775L) != 0) || ((((_la - 325)) & ~0x3f) == 0 && ((1L << (_la - 325)) & 5308579579887L) != 0)) {
					{
					setState(1270);
					sql_argument_list();
					}
				}

				setState(1273);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1275);
				item_select_function();
				setState(1276);
				match(LEFT_PAREN);
				setState(1277);
				sql_argument_list();
				setState(1278);
				match(RIGHT_PAREN);
				setState(1283);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 4398047690752L) != 0)) {
					{
					setState(1280);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==FROM) {
						{
						setState(1279);
						select_direction();
						}
					}

					setState(1282);
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
		enterRule(_localctx, 222, RULE_over_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1287);
			match(OVER);
			setState(1288);
			match(LEFT_PAREN);
			{
			setState(1290);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PARTITION) {
				{
				setState(1289);
				partition_by_clause();
				}
			}

			setState(1293);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ORDER) {
				{
				setState(1292);
				orderby_clause();
				}
			}

			setState(1296);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==RANGE || _la==ROWS) {
				{
				setState(1295);
				bracket_frame_clause();
				}
			}

			}
			setState(1298);
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
		enterRule(_localctx, 224, RULE_partition_by_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1300);
			match(PARTITION);
			setState(1301);
			match(BY);
			setState(1302);
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
		enterRule(_localctx, 226, RULE_bracket_frame_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1304);
			rows_or_range();
			setState(1305);
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
		enterRule(_localctx, 228, RULE_rows_or_range);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1307);
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
		enterRule(_localctx, 230, RULE_bracket_frame_definition);
		try {
			setState(1312);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BETWEEN:
				enterOuterAlt(_localctx, 1);
				{
				setState(1309);
				between_frame_definition();
				}
				break;
			case UNBOUNDED:
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(1310);
				preceding_frame_edge();
				}
				break;
			case CURRENT:
				enterOuterAlt(_localctx, 3);
				{
				setState(1311);
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
		enterRule(_localctx, 232, RULE_between_frame_definition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1314);
			match(BETWEEN);
			setState(1315);
			frame_edge();
			setState(1316);
			match(AND);
			setState(1317);
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
		enterRule(_localctx, 234, RULE_frame_edge);
		try {
			setState(1322);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,112,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1319);
				preceding_frame_edge();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1320);
				following_frame_edge();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1321);
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
		enterRule(_localctx, 236, RULE_preceding_frame_edge);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1324);
			bracket_constraint();
			setState(1325);
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
		enterRule(_localctx, 238, RULE_following_frame_edge);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1327);
			bracket_constraint();
			setState(1328);
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
		enterRule(_localctx, 240, RULE_current_row_edge);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1330);
			match(CURRENT);
			setState(1331);
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
		enterRule(_localctx, 242, RULE_bracket_constraint);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1333);
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
		enterRule(_localctx, 244, RULE_item_select_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1335);
			_la = _input.LA(1);
			if ( !(((((_la - 88)) & ~0x3f) == 0 && ((1L << (_la - 88)) & 268488705L) != 0)) ) {
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
		enterRule(_localctx, 246, RULE_select_direction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1337);
			match(FROM);
			setState(1338);
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
		enterRule(_localctx, 248, RULE_null_handling);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1340);
			_la = _input.LA(1);
			if ( !(_la==IGNORE || _la==RESPECT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(1341);
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
		enterRule(_localctx, 250, RULE_value_expression);
		try {
			setState(1347);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,113,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1343);
				common_value_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1344);
				row_value_expression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1345);
				variable_identifier();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1346);
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
		enterRule(_localctx, 252, RULE_common_value_expression);
		try {
			setState(1352);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,114,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1349);
				additive_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1350);
				string_value_expression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1351);
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
		enterRule(_localctx, 254, RULE_additive_expression);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(1354);
			((Additive_expressionContext)_localctx).left = multiplicative_expression();
			setState(1359);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,115,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1355);
					_la = _input.LA(1);
					if ( !(_la==PLUS || _la==MINUS) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(1356);
					((Additive_expressionContext)_localctx).right = multiplicative_expression();
					}
					} 
				}
				setState(1361);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,115,_ctx);
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
		enterRule(_localctx, 256, RULE_multiplicative_expression);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1362);
			((Multiplicative_expressionContext)_localctx).left = factor();
			setState(1367);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,116,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1363);
					_la = _input.LA(1);
					if ( !(((((_la - 290)) & ~0x3f) == 0 && ((1L << (_la - 290)) & 7L) != 0)) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(1364);
					((Multiplicative_expressionContext)_localctx).right = factor();
					}
					} 
				}
				setState(1369);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,116,_ctx);
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
		enterRule(_localctx, 258, RULE_factor);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1371);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PLUS || _la==MINUS) {
				{
				setState(1370);
				sign();
				}
			}

			setState(1373);
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
		enterRule(_localctx, 260, RULE_numeric_primary);
		try {
			setState(1377);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,118,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1375);
				value_expression_primary();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1376);
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
		enterRule(_localctx, 262, RULE_sign);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1379);
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
		enterRule(_localctx, 264, RULE_extract_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1381);
			match(EXTRACT);
			setState(1382);
			match(LEFT_PAREN);
			setState(1383);
			((Extract_expressionContext)_localctx).extract_field_string = extract_field();
			setState(1384);
			match(FROM);
			setState(1385);
			extract_source();
			setState(1386);
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
		enterRule(_localctx, 266, RULE_extract_field);
		try {
			setState(1391);
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
				setState(1388);
				primary_datetime_field();
				}
				break;
			case TIMEZONE:
			case TIMEZONE_HOUR:
			case TIMEZONE_MINUTE:
				enterOuterAlt(_localctx, 2);
				{
				setState(1389);
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
				setState(1390);
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
		enterRule(_localctx, 268, RULE_time_zone_field);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1393);
			_la = _input.LA(1);
			if ( !(((((_la - 143)) & ~0x3f) == 0 && ((1L << (_la - 143)) & 7L) != 0)) ) {
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
		enterRule(_localctx, 270, RULE_extract_source);
		try {
			setState(1397);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,120,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1395);
				column_reference();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1396);
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
		enterRule(_localctx, 272, RULE_string_value_expression);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1399);
			character_primary();
			setState(1404);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,121,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1400);
					match(CONCATENATION_OPERATOR);
					setState(1401);
					character_primary();
					}
					} 
				}
				setState(1406);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,121,_ctx);
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
		enterRule(_localctx, 274, RULE_character_primary);
		try {
			setState(1409);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,122,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1407);
				value_expression_primary();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1408);
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
		enterRule(_localctx, 276, RULE_trim_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1411);
			trim_function_name();
			setState(1412);
			match(LEFT_PAREN);
			setState(1413);
			trim_operands();
			setState(1414);
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
		enterRule(_localctx, 278, RULE_trim_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1416);
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
		enterRule(_localctx, 280, RULE_trim_operands);
		int _la;
		try {
			setState(1432);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,126,_ctx) ) {
			case 1:
				_localctx = new Mysql_trim_operandsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(1425);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,125,_ctx) ) {
				case 1:
					{
					setState(1419);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1125900175278144L) != 0)) {
						{
						setState(1418);
						trim_specification();
						}
					}

					setState(1422);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -848827271068221040L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -288230377259008257L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -2097169L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -162133984631980033L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 8934605733887L) != 0) || ((((_la - 325)) & ~0x3f) == 0 && ((1L << (_la - 325)) & 5308579579873L) != 0)) {
						{
						setState(1421);
						((Mysql_trim_operandsContext)_localctx).trim_character = string_value_expression();
						}
					}

					setState(1424);
					match(FROM);
					}
					break;
				}
				setState(1427);
				((Mysql_trim_operandsContext)_localctx).trim_source = value_expression();
				}
				break;
			case 2:
				_localctx = new Other_trim_operandsContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(1428);
				((Other_trim_operandsContext)_localctx).trim_source = value_expression();
				setState(1429);
				match(COMMA);
				setState(1430);
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
		enterRule(_localctx, 282, RULE_trim_specification);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1434);
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
	public static class Position_functionContext extends ParserRuleContext {
		public String_value_expressionContext search_string;
		public String_value_expressionContext source_string;
		public Numeric_primaryContext start_position;
		public Position_function_nameContext position_function_name() {
			return getRuleContext(Position_function_nameContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public TerminalNode IN() { return getToken(SQLSelectParserParser.IN, 0); }
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public List<String_value_expressionContext> string_value_expression() {
			return getRuleContexts(String_value_expressionContext.class);
		}
		public String_value_expressionContext string_value_expression(int i) {
			return getRuleContext(String_value_expressionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SQLSelectParserParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SQLSelectParserParser.COMMA, i);
		}
		public Instr_function_nameContext instr_function_name() {
			return getRuleContext(Instr_function_nameContext.class,0);
		}
		public Charindex_nameContext charindex_name() {
			return getRuleContext(Charindex_nameContext.class,0);
		}
		public Numeric_primaryContext numeric_primary() {
			return getRuleContext(Numeric_primaryContext.class,0);
		}
		public Position_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_position_function; }
	}

	public final Position_functionContext position_function() throws RecognitionException {
		Position_functionContext _localctx = new Position_functionContext(_ctx, getState());
		enterRule(_localctx, 284, RULE_position_function);
		int _la;
		try {
			setState(1458);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,129,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1436);
				position_function_name();
				setState(1437);
				match(LEFT_PAREN);
				setState(1438);
				((Position_functionContext)_localctx).search_string = string_value_expression();
				setState(1439);
				match(IN);
				setState(1440);
				((Position_functionContext)_localctx).source_string = string_value_expression();
				setState(1441);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1446);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case POSITION:
					{
					setState(1443);
					position_function_name();
					}
					break;
				case INSTR:
					{
					setState(1444);
					instr_function_name();
					}
					break;
				case CHARINDEX:
					{
					setState(1445);
					charindex_name();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(1448);
				match(LEFT_PAREN);
				setState(1449);
				((Position_functionContext)_localctx).search_string = string_value_expression();
				setState(1450);
				match(COMMA);
				setState(1451);
				((Position_functionContext)_localctx).source_string = string_value_expression();
				setState(1454);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(1452);
					match(COMMA);
					setState(1453);
					((Position_functionContext)_localctx).start_position = numeric_primary();
					}
				}

				setState(1456);
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
	public static class Position_function_nameContext extends ParserRuleContext {
		public TerminalNode POSITION() { return getToken(SQLSelectParserParser.POSITION, 0); }
		public Position_function_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_position_function_name; }
	}

	public final Position_function_nameContext position_function_name() throws RecognitionException {
		Position_function_nameContext _localctx = new Position_function_nameContext(_ctx, getState());
		enterRule(_localctx, 286, RULE_position_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1460);
			match(POSITION);
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
	public static class Instr_function_nameContext extends ParserRuleContext {
		public TerminalNode INSTR() { return getToken(SQLSelectParserParser.INSTR, 0); }
		public Instr_function_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_instr_function_name; }
	}

	public final Instr_function_nameContext instr_function_name() throws RecognitionException {
		Instr_function_nameContext _localctx = new Instr_function_nameContext(_ctx, getState());
		enterRule(_localctx, 288, RULE_instr_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1462);
			match(INSTR);
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
	public static class Charindex_nameContext extends ParserRuleContext {
		public TerminalNode CHARINDEX() { return getToken(SQLSelectParserParser.CHARINDEX, 0); }
		public Charindex_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_charindex_name; }
	}

	public final Charindex_nameContext charindex_name() throws RecognitionException {
		Charindex_nameContext _localctx = new Charindex_nameContext(_ctx, getState());
		enterRule(_localctx, 290, RULE_charindex_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1464);
			match(CHARINDEX);
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
		enterRule(_localctx, 292, RULE_boolean_value_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1466);
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
		enterRule(_localctx, 294, RULE_or_predicate);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1468);
			and_predicate();
			setState(1473);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,130,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1469);
					match(OR);
					setState(1470);
					and_predicate();
					}
					} 
				}
				setState(1475);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,130,_ctx);
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
		enterRule(_localctx, 296, RULE_and_predicate);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1476);
			negative_predicate();
			setState(1481);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,131,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1477);
					match(AND);
					setState(1478);
					negative_predicate();
					}
					} 
				}
				setState(1483);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,131,_ctx);
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
		enterRule(_localctx, 298, RULE_negative_predicate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1485);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(1484);
				not();
				}
			}

			setState(1487);
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
		enterRule(_localctx, 300, RULE_parenthetical_predicate);
		try {
			setState(1497);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,134,_ctx) ) {
			case 1:
				_localctx = new Paren_clauseContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(1489);
				match(LEFT_PAREN);
				setState(1490);
				boolean_value_expression();
				setState(1491);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				_localctx = new Basic_predicate_clauseContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(1493);
				boolean_primary();
				setState(1495);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,133,_ctx) ) {
				case 1:
					{
					setState(1494);
					is_clause();
					}
					break;
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
		enterRule(_localctx, 302, RULE_boolean_primary);
		try {
			setState(1501);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,135,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1499);
				predicate();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1500);
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
		enterRule(_localctx, 304, RULE_predicate);
		try {
			setState(1510);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,136,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1503);
				comparison_predicate();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1504);
				between_predicate();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1505);
				in_predicate();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1506);
				like_any_predicate();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1507);
				null_predicate();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1508);
				exists_predicate();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1509);
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
		enterRule(_localctx, 306, RULE_substitution_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1512);
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
		enterRule(_localctx, 308, RULE_row_value_expression);
		try {
			setState(1516);
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
			case ESCAPE:
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
			case POSITION:
			case CHARINDEX:
			case INSTR:
			case IFF:
			case MD5:
			case REVERSE:
			case Identifier:
			case Scientific_Numeric_Literal:
			case Double_Quoted_Numeric_Identifier:
			case Dollar_Sign_Identifier:
			case Character_String_Literal:
				enterOuterAlt(_localctx, 1);
				{
				setState(1514);
				nonparenthesized_value_expression_primary();
				}
				break;
			case NULL:
				enterOuterAlt(_localctx, 2);
				{
				setState(1515);
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
		enterRule(_localctx, 310, RULE_row_value_predicand);
		try {
			setState(1521);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,138,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1518);
				nonparenthesized_value_expression_primary();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1519);
				common_value_expression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1520);
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
		enterRule(_localctx, 312, RULE_where_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1523);
			match(WHERE);
			setState(1524);
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
		enterRule(_localctx, 314, RULE_search_condition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1526);
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
		enterRule(_localctx, 316, RULE_orderby_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1528);
			match(ORDER);
			setState(1529);
			match(BY);
			setState(1530);
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
		enterRule(_localctx, 318, RULE_sort_specifier_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1532);
			sort_specifier();
			setState(1537);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1533);
				match(COMMA);
				setState(1534);
				sort_specifier();
				}
				}
				setState(1539);
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
		enterRule(_localctx, 320, RULE_sort_specifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1540);
			((Sort_specifierContext)_localctx).key = row_value_predicand();
			setState(1542);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ASC || _la==DESC) {
				{
				setState(1541);
				((Sort_specifierContext)_localctx).order = order_specification();
				}
			}

			setState(1545);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NULLS) {
				{
				setState(1544);
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
		enterRule(_localctx, 322, RULE_order_specification);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1547);
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
		enterRule(_localctx, 324, RULE_null_ordering);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1549);
			match(NULLS);
			setState(1550);
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
		enterRule(_localctx, 326, RULE_null_first_last);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1552);
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
		enterRule(_localctx, 328, RULE_limit_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1554);
			match(LIMIT);
			setState(1555);
			((Limit_clauseContext)_localctx).e = additive_expression();
			setState(1558);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OFFSET) {
				{
				setState(1556);
				match(OFFSET);
				setState(1557);
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
		enterRule(_localctx, 330, RULE_groupby_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1560);
			match(GROUP);
			setState(1561);
			match(BY);
			setState(1564);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,143,_ctx) ) {
			case 1:
				{
				setState(1562);
				grouping_element_list();
				}
				break;
			case 2:
				{
				setState(1563);
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
		enterRule(_localctx, 332, RULE_grouping_element_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1566);
			grouping_element();
			setState(1571);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1567);
				match(COMMA);
				setState(1568);
				grouping_element();
				}
				}
				setState(1573);
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
		enterRule(_localctx, 334, RULE_grouping_element);
		try {
			setState(1578);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,145,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1574);
				rollup_list();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1575);
				cube_list();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1576);
				empty_grouping_set();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1577);
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
		enterRule(_localctx, 336, RULE_ordinary_grouping_set_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1580);
			ordinary_grouping_set();
			setState(1585);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1581);
				match(COMMA);
				setState(1582);
				ordinary_grouping_set();
				}
				}
				setState(1587);
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
		enterRule(_localctx, 338, RULE_ordinary_grouping_set);
		try {
			setState(1593);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,147,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1588);
				row_value_predicand();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1589);
				match(LEFT_PAREN);
				setState(1590);
				row_value_predicand_list();
				setState(1591);
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
		enterRule(_localctx, 340, RULE_rollup_list);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1595);
			match(ROLLUP);
			setState(1596);
			match(LEFT_PAREN);
			setState(1597);
			((Rollup_listContext)_localctx).c = ordinary_grouping_set_list();
			setState(1598);
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
		enterRule(_localctx, 342, RULE_cube_list);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1600);
			match(CUBE);
			setState(1601);
			match(LEFT_PAREN);
			setState(1602);
			((Cube_listContext)_localctx).c = ordinary_grouping_set_list();
			setState(1603);
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
		enterRule(_localctx, 344, RULE_empty_grouping_set);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1605);
			match(LEFT_PAREN);
			setState(1606);
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
		enterRule(_localctx, 346, RULE_having_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1608);
			match(HAVING);
			setState(1609);
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
	public static class Qualify_clauseContext extends ParserRuleContext {
		public TerminalNode QUALIFY() { return getToken(SQLSelectParserParser.QUALIFY, 0); }
		public Search_conditionContext search_condition() {
			return getRuleContext(Search_conditionContext.class,0);
		}
		public Qualify_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qualify_clause; }
	}

	public final Qualify_clauseContext qualify_clause() throws RecognitionException {
		Qualify_clauseContext _localctx = new Qualify_clauseContext(_ctx, getState());
		enterRule(_localctx, 348, RULE_qualify_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1611);
			match(QUALIFY);
			setState(1612);
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
		enterRule(_localctx, 350, RULE_row_value_predicand_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1614);
			row_value_predicand();
			setState(1619);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1615);
				match(COMMA);
				setState(1616);
				row_value_predicand();
				}
				}
				setState(1621);
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
		enterRule(_localctx, 352, RULE_comparison_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1622);
			((Comparison_predicateContext)_localctx).left = row_value_predicand();
			setState(1623);
			((Comparison_predicateContext)_localctx).c = comparison_operator();
			setState(1624);
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
		enterRule(_localctx, 354, RULE_comparison_operator);
		int _la;
		try {
			setState(1632);
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
				setState(1626);
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
				setState(1628);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(1627);
					not();
					}
				}

				setState(1630);
				relative_comp_op();
				}
				break;
			case Similar_To:
			case Not_Similar_To:
			case Similar_To_Case_Insensitive:
			case Not_Similar_To_Case_Insensitive:
				enterOuterAlt(_localctx, 3);
				{
				setState(1631);
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
		enterRule(_localctx, 356, RULE_relative_comp_op);
		try {
			setState(1640);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LIKE:
				enterOuterAlt(_localctx, 1);
				{
				setState(1634);
				match(LIKE);
				}
				break;
			case ILIKE:
				enterOuterAlt(_localctx, 2);
				{
				setState(1635);
				match(ILIKE);
				}
				break;
			case SIMILAR:
				enterOuterAlt(_localctx, 3);
				{
				setState(1636);
				match(SIMILAR);
				setState(1637);
				match(TO);
				}
				break;
			case REGEXP:
				enterOuterAlt(_localctx, 4);
				{
				setState(1638);
				match(REGEXP);
				}
				break;
			case RLIKE:
				enterOuterAlt(_localctx, 5);
				{
				setState(1639);
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
		enterRule(_localctx, 358, RULE_similarity_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1642);
			_la = _input.LA(1);
			if ( !(((((_la - 270)) & ~0x3f) == 0 && ((1L << (_la - 270)) & 15L) != 0)) ) {
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
		enterRule(_localctx, 360, RULE_comp_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1644);
			_la = _input.LA(1);
			if ( !(((((_la - 275)) & ~0x3f) == 0 && ((1L << (_la - 275)) & 993L) != 0)) ) {
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
		enterRule(_localctx, 362, RULE_between_predicate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1646);
			row_value_predicand();
			setState(1648);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(1647);
				not();
				}
			}

			setState(1650);
			match(BETWEEN);
			setState(1652);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ASYMMETRIC || _la==SYMMETRIC) {
				{
				setState(1651);
				symmetry();
				}
			}

			setState(1654);
			((Between_predicateContext)_localctx).begin = row_value_predicand();
			setState(1655);
			match(AND);
			setState(1656);
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
		enterRule(_localctx, 364, RULE_symmetry);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1658);
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
		enterRule(_localctx, 366, RULE_in_predicate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1660);
			row_value_predicand();
			setState(1662);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(1661);
				not();
				}
			}

			setState(1664);
			match(IN);
			setState(1665);
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
		public Escape_character_clauseContext escape_character_clause() {
			return getRuleContext(Escape_character_clauseContext.class,0);
		}
		public Like_any_predicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_like_any_predicate; }
	}

	public final Like_any_predicateContext like_any_predicate() throws RecognitionException {
		Like_any_predicateContext _localctx = new Like_any_predicateContext(_ctx, getState());
		enterRule(_localctx, 368, RULE_like_any_predicate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1667);
			row_value_predicand();
			setState(1669);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(1668);
				not();
				}
			}

			setState(1671);
			like_any_operator();
			setState(1672);
			in_predicate_value();
			setState(1674);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,156,_ctx) ) {
			case 1:
				{
				setState(1673);
				escape_character_clause();
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
		enterRule(_localctx, 370, RULE_like_any_operator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1676);
			_la = _input.LA(1);
			if ( !(_la==ILIKE || _la==LIKE) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(1677);
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
		enterRule(_localctx, 372, RULE_in_predicate_value);
		try {
			setState(1685);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,157,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1679);
				subquery();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1680);
				match(LEFT_PAREN);
				setState(1681);
				in_value_list();
				setState(1682);
				match(RIGHT_PAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1684);
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
		enterRule(_localctx, 374, RULE_in_value_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1687);
			row_value_expression();
			setState(1692);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1688);
				match(COMMA);
				setState(1689);
				row_value_expression();
				}
				}
				setState(1694);
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
	public static class Escape_character_clauseContext extends ParserRuleContext {
		public TerminalNode ESCAPE() { return getToken(SQLSelectParserParser.ESCAPE, 0); }
		public TerminalNode Character_String_Literal() { return getToken(SQLSelectParserParser.Character_String_Literal, 0); }
		public Escape_character_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_escape_character_clause; }
	}

	public final Escape_character_clauseContext escape_character_clause() throws RecognitionException {
		Escape_character_clauseContext _localctx = new Escape_character_clauseContext(_ctx, getState());
		enterRule(_localctx, 376, RULE_escape_character_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1695);
			match(ESCAPE);
			setState(1696);
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
		enterRule(_localctx, 378, RULE_values_statement_primary);
		try {
			setState(1701);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,159,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1698);
				fully_defined_values_statement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1699);
				aliased_values_statement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1700);
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
		public Values_aliasesContext values_aliases() {
			return getRuleContext(Values_aliasesContext.class,0);
		}
		public Fully_defined_values_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fully_defined_values_statement; }
	}

	public final Fully_defined_values_statementContext fully_defined_values_statement() throws RecognitionException {
		Fully_defined_values_statementContext _localctx = new Fully_defined_values_statementContext(_ctx, getState());
		enterRule(_localctx, 380, RULE_fully_defined_values_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1703);
			values_statement();
			setState(1704);
			as_clause();
			setState(1705);
			values_aliases();
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
		enterRule(_localctx, 382, RULE_aliased_values_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1707);
			values_statement();
			setState(1708);
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
		enterRule(_localctx, 384, RULE_values_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1710);
			match(LEFT_PAREN);
			setState(1711);
			match(VALUES);
			setState(1712);
			values_matrix();
			setState(1713);
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
		enterRule(_localctx, 386, RULE_values_matrix);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1715);
			values_row();
			setState(1720);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1716);
				match(COMMA);
				setState(1717);
				values_row();
				}
				}
				setState(1722);
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
		enterRule(_localctx, 388, RULE_values_row);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1723);
			match(LEFT_PAREN);
			setState(1724);
			in_value_list();
			setState(1725);
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
	public static class Values_aliasesContext extends ParserRuleContext {
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public Values_aliases_listContext values_aliases_list() {
			return getRuleContext(Values_aliases_listContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Values_aliasesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_values_aliases; }
	}

	public final Values_aliasesContext values_aliases() throws RecognitionException {
		Values_aliasesContext _localctx = new Values_aliasesContext(_ctx, getState());
		enterRule(_localctx, 390, RULE_values_aliases);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(1727);
			match(LEFT_PAREN);
			setState(1728);
			values_aliases_list();
			setState(1729);
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
	public static class Values_aliases_listContext extends ParserRuleContext {
		public List<Alias_identifierContext> alias_identifier() {
			return getRuleContexts(Alias_identifierContext.class);
		}
		public Alias_identifierContext alias_identifier(int i) {
			return getRuleContext(Alias_identifierContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SQLSelectParserParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SQLSelectParserParser.COMMA, i);
		}
		public Values_aliases_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_values_aliases_list; }
	}

	public final Values_aliases_listContext values_aliases_list() throws RecognitionException {
		Values_aliases_listContext _localctx = new Values_aliases_listContext(_ctx, getState());
		enterRule(_localctx, 392, RULE_values_aliases_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1731);
			alias_identifier();
			setState(1736);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1732);
				match(COMMA);
				setState(1733);
				alias_identifier();
				}
				}
				setState(1738);
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
	public static class Insert_values_statementContext extends ParserRuleContext {
		public TerminalNode VALUES() { return getToken(SQLSelectParserParser.VALUES, 0); }
		public Values_matrixContext values_matrix() {
			return getRuleContext(Values_matrixContext.class,0);
		}
		public Insert_values_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_insert_values_statement; }
	}

	public final Insert_values_statementContext insert_values_statement() throws RecognitionException {
		Insert_values_statementContext _localctx = new Insert_values_statementContext(_ctx, getState());
		enterRule(_localctx, 394, RULE_insert_values_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1739);
			match(VALUES);
			setState(1740);
			values_matrix();
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
		public Exists_operatorContext exists_operator() {
			return getRuleContext(Exists_operatorContext.class,0);
		}
		public Exists_predicate_valueContext exists_predicate_value() {
			return getRuleContext(Exists_predicate_valueContext.class,0);
		}
		public Exists_predicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exists_predicate; }
	}

	public final Exists_predicateContext exists_predicate() throws RecognitionException {
		Exists_predicateContext _localctx = new Exists_predicateContext(_ctx, getState());
		enterRule(_localctx, 396, RULE_exists_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1742);
			exists_operator();
			setState(1743);
			exists_predicate_value();
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
	public static class Exists_operatorContext extends ParserRuleContext {
		public TerminalNode EXISTS() { return getToken(SQLSelectParserParser.EXISTS, 0); }
		public Exists_operatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exists_operator; }
	}

	public final Exists_operatorContext exists_operator() throws RecognitionException {
		Exists_operatorContext _localctx = new Exists_operatorContext(_ctx, getState());
		enterRule(_localctx, 398, RULE_exists_operator);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1745);
			match(EXISTS);
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
	public static class Exists_predicate_valueContext extends ParserRuleContext {
		public SubqueryContext subquery() {
			return getRuleContext(SubqueryContext.class,0);
		}
		public Variable_identifierContext variable_identifier() {
			return getRuleContext(Variable_identifierContext.class,0);
		}
		public Exists_predicate_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exists_predicate_value; }
	}

	public final Exists_predicate_valueContext exists_predicate_value() throws RecognitionException {
		Exists_predicate_valueContext _localctx = new Exists_predicate_valueContext(_ctx, getState());
		enterRule(_localctx, 400, RULE_exists_predicate_value);
		try {
			setState(1749);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LEFT_PAREN:
				enterOuterAlt(_localctx, 1);
				{
				setState(1747);
				subquery();
				}
				break;
			case Variable_Identifier:
			case Extended_Variable_Identifier:
			case Mixed_Variable_Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(1748);
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
		enterRule(_localctx, 402, RULE_null_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1751);
			row_value_predicand();
			setState(1752);
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
		enterRule(_localctx, 404, RULE_is_null_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1754);
			match(IS);
			setState(1756);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(1755);
				((Is_null_clauseContext)_localctx).n = match(NOT);
				}
			}

			setState(1758);
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
		enterRule(_localctx, 406, RULE_is_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1760);
			match(IS);
			setState(1762);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(1761);
				not();
				}
			}

			setState(1764);
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
		enterRule(_localctx, 408, RULE_truth_value);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1766);
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
		enterRule(_localctx, 410, RULE_not);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1768);
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
		enterRule(_localctx, 412, RULE_quantified_comparison_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1770);
			((Quantified_comparison_predicateContext)_localctx).l = additive_expression();
			setState(1771);
			((Quantified_comparison_predicateContext)_localctx).c = comp_op();
			setState(1772);
			((Quantified_comparison_predicateContext)_localctx).q = quantifier();
			setState(1773);
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
		enterRule(_localctx, 414, RULE_quantifier);
		try {
			setState(1777);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ALL:
				enterOuterAlt(_localctx, 1);
				{
				setState(1775);
				all();
				}
				break;
			case ANY:
			case SOME:
				enterOuterAlt(_localctx, 2);
				{
				setState(1776);
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
		enterRule(_localctx, 416, RULE_all);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1779);
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
		enterRule(_localctx, 418, RULE_some);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1781);
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
		enterRule(_localctx, 420, RULE_unique_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1783);
			match(UNIQUE);
			setState(1784);
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
		enterRule(_localctx, 422, RULE_primary_datetime_field);
		try {
			setState(1788);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DAY:
			case HOUR:
			case MINUTE:
			case MONTH:
			case YEAR:
				enterOuterAlt(_localctx, 1);
				{
				setState(1786);
				non_second_primary_datetime_field();
				}
				break;
			case SECOND:
				enterOuterAlt(_localctx, 2);
				{
				setState(1787);
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
		enterRule(_localctx, 424, RULE_non_second_primary_datetime_field);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1790);
			_la = _input.LA(1);
			if ( !(((((_la - 73)) & ~0x3f) == 0 && ((1L << (_la - 73)) & 3298536980481L) != 0) || _la==YEAR) ) {
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
		enterRule(_localctx, 426, RULE_extended_datetime_field);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1792);
			_la = _input.LA(1);
			if ( !(((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 2306089351358015489L) != 0) || _la==WEEK) ) {
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
		enterRule(_localctx, 428, RULE_routine_invocation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1794);
			function_name();
			setState(1795);
			match(LEFT_PAREN);
			setState(1797);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -848827245298417264L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -288230377259008257L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -2097169L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -162133984631980033L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 8947490635775L) != 0) || ((((_la - 325)) & ~0x3f) == 0 && ((1L << (_la - 325)) & 5308579579887L) != 0)) {
				{
				setState(1796);
				sql_argument_list();
				}
			}

			setState(1799);
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
		enterRule(_localctx, 430, RULE_function_name);
		int _la;
		try {
			setState(1807);
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
			case ESCAPE:
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
			case Bracket_Identifier:
			case Identifier:
			case Double_Quoted_Numeric_Identifier:
			case Dollar_Sign_Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(1801);
				identifier();
				setState(1804);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOT) {
					{
					setState(1802);
					match(DOT);
					setState(1803);
					identifier();
					}
				}

				}
				break;
			case IN:
			case LEFT:
			case RIGHT:
			case IFF:
			case MD5:
			case REVERSE:
				enterOuterAlt(_localctx, 2);
				{
				setState(1806);
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
		public TerminalNode IFF() { return getToken(SQLSelectParserParser.IFF, 0); }
		public TerminalNode MD5() { return getToken(SQLSelectParserParser.MD5, 0); }
		public TerminalNode REVERSE() { return getToken(SQLSelectParserParser.REVERSE, 0); }
		public Function_names_for_reserved_wordsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function_names_for_reserved_words; }
	}

	public final Function_names_for_reserved_wordsContext function_names_for_reserved_words() throws RecognitionException {
		Function_names_for_reserved_wordsContext _localctx = new Function_names_for_reserved_wordsContext(_ctx, getState());
		enterRule(_localctx, 432, RULE_function_names_for_reserved_words);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1809);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 8796634087424L) != 0) || ((((_la - 333)) & ~0x3f) == 0 && ((1L << (_la - 333)) & 7L) != 0)) ) {
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
		enterRule(_localctx, 434, RULE_sql_argument_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1811);
			value_expression();
			setState(1816);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1812);
				match(COMMA);
				setState(1813);
				value_expression();
				}
				}
				setState(1818);
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
		enterRule(_localctx, 436, RULE_identifier);
		try {
			setState(1824);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(1819);
				simple_identifier();
				}
				break;
			case Bracket_Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(1820);
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
			case ESCAPE:
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
				enterOuterAlt(_localctx, 3);
				{
				setState(1821);
				nonreserved_keywords();
				}
				break;
			case Double_Quoted_Numeric_Identifier:
				enterOuterAlt(_localctx, 4);
				{
				setState(1822);
				snowflake_quoted_numeric_identifier();
				}
				break;
			case Dollar_Sign_Identifier:
				enterOuterAlt(_localctx, 5);
				{
				setState(1823);
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
		enterRule(_localctx, 438, RULE_alias_identifier);
		try {
			setState(1831);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(1826);
				simple_identifier();
				}
				break;
			case Bracket_Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(1827);
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
			case ESCAPE:
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
				enterOuterAlt(_localctx, 3);
				{
				setState(1828);
				nonreserved_keywords();
				}
				break;
			case NUMBER:
			case Numeric_Identifier:
				enterOuterAlt(_localctx, 4);
				{
				setState(1829);
				simple_numeric_identifier();
				}
				break;
			case Double_Quoted_Numeric_Identifier:
				enterOuterAlt(_localctx, 5);
				{
				setState(1830);
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
		enterRule(_localctx, 440, RULE_variable_identifier);
		try {
			setState(1835);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Variable_Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(1833);
				simple_variable_identifier();
				}
				break;
			case Extended_Variable_Identifier:
			case Mixed_Variable_Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(1834);
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
		enterRule(_localctx, 442, RULE_simple_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1837);
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
		enterRule(_localctx, 444, RULE_logical_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1839);
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
		enterRule(_localctx, 446, RULE_simple_variable_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1841);
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
		enterRule(_localctx, 448, RULE_extended_variable_identifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1843);
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
	public static class Jinja_identifierContext extends ParserRuleContext {
		public TerminalNode JINJA_OPEN() { return getToken(SQLSelectParserParser.JINJA_OPEN, 0); }
		public Jinja_function_callContext jinja_function_call() {
			return getRuleContext(Jinja_function_callContext.class,0);
		}
		public TerminalNode JINJA_CLOSE() { return getToken(SQLSelectParserParser.JINJA_CLOSE, 0); }
		public Jinja_variable_accessContext jinja_variable_access() {
			return getRuleContext(Jinja_variable_accessContext.class,0);
		}
		public Jinja_identifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jinja_identifier; }
	}

	public final Jinja_identifierContext jinja_identifier() throws RecognitionException {
		Jinja_identifierContext _localctx = new Jinja_identifierContext(_ctx, getState());
		enterRule(_localctx, 450, RULE_jinja_identifier);
		try {
			setState(1853);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,174,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1845);
				match(JINJA_OPEN);
				setState(1846);
				jinja_function_call();
				setState(1847);
				match(JINJA_CLOSE);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1849);
				match(JINJA_OPEN);
				setState(1850);
				jinja_variable_access();
				setState(1851);
				match(JINJA_CLOSE);
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
	public static class Jinja_function_callContext extends ParserRuleContext {
		public IdentifierContext func_name;
		public IdentifierContext object_name;
		public IdentifierContext method_name;
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public List<IdentifierContext> identifier() {
			return getRuleContexts(IdentifierContext.class);
		}
		public IdentifierContext identifier(int i) {
			return getRuleContext(IdentifierContext.class,i);
		}
		public Jinja_arg_listContext jinja_arg_list() {
			return getRuleContext(Jinja_arg_listContext.class,0);
		}
		public TerminalNode DOT() { return getToken(SQLSelectParserParser.DOT, 0); }
		public Jinja_function_callContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jinja_function_call; }
	}

	public final Jinja_function_callContext jinja_function_call() throws RecognitionException {
		Jinja_function_callContext _localctx = new Jinja_function_callContext(_ctx, getState());
		enterRule(_localctx, 452, RULE_jinja_function_call);
		int _la;
		try {
			setState(1873);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,177,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1855);
				((Jinja_function_callContext)_localctx).func_name = identifier();
				setState(1856);
				if (!(isJinjaPrimaryFunction((((Jinja_function_callContext)_localctx).func_name!=null?_input.getText(((Jinja_function_callContext)_localctx).func_name.start,((Jinja_function_callContext)_localctx).func_name.stop):null)))) throw new FailedPredicateException(this, "isJinjaPrimaryFunction($func_name.text)");
				setState(1857);
				match(LEFT_PAREN);
				setState(1859);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -855661835887575040L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -292734320500539713L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -2097169L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -162133984631980033L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 8796093038591L) != 0) || ((((_la - 325)) & ~0x3f) == 0 && ((1L << (_la - 325)) & 5239860101121L) != 0)) {
					{
					setState(1858);
					jinja_arg_list();
					}
				}

				setState(1861);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1863);
				((Jinja_function_callContext)_localctx).object_name = identifier();
				setState(1864);
				match(DOT);
				setState(1865);
				((Jinja_function_callContext)_localctx).method_name = identifier();
				setState(1866);
				if (!(isJinjaObjectMethod((((Jinja_function_callContext)_localctx).object_name!=null?_input.getText(((Jinja_function_callContext)_localctx).object_name.start,((Jinja_function_callContext)_localctx).object_name.stop):null), (((Jinja_function_callContext)_localctx).method_name!=null?_input.getText(((Jinja_function_callContext)_localctx).method_name.start,((Jinja_function_callContext)_localctx).method_name.stop):null)))) throw new FailedPredicateException(this, "isJinjaObjectMethod($object_name.text, $method_name.text)");
				setState(1867);
				match(LEFT_PAREN);
				setState(1869);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -855661835887575040L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -292734320500539713L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -2097169L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -162133984631980033L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 8796093038591L) != 0) || ((((_la - 325)) & ~0x3f) == 0 && ((1L << (_la - 325)) & 5239860101121L) != 0)) {
					{
					setState(1868);
					jinja_arg_list();
					}
				}

				setState(1871);
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
	public static class Jinja_arg_listContext extends ParserRuleContext {
		public List<Jinja_argContext> jinja_arg() {
			return getRuleContexts(Jinja_argContext.class);
		}
		public Jinja_argContext jinja_arg(int i) {
			return getRuleContext(Jinja_argContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SQLSelectParserParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SQLSelectParserParser.COMMA, i);
		}
		public Jinja_arg_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jinja_arg_list; }
	}

	public final Jinja_arg_listContext jinja_arg_list() throws RecognitionException {
		Jinja_arg_listContext _localctx = new Jinja_arg_listContext(_ctx, getState());
		enterRule(_localctx, 454, RULE_jinja_arg_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1875);
			jinja_arg();
			setState(1880);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1876);
				match(COMMA);
				setState(1877);
				jinja_arg();
				}
				}
				setState(1882);
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
	public static class Jinja_argContext extends ParserRuleContext {
		public IdentifierContext kw_name;
		public TerminalNode Character_String_Literal() { return getToken(SQLSelectParserParser.Character_String_Literal, 0); }
		public TerminalNode NUMBER() { return getToken(SQLSelectParserParser.NUMBER, 0); }
		public TerminalNode EQUAL() { return getToken(SQLSelectParserParser.EQUAL, 0); }
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public Jinja_argContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jinja_arg; }
	}

	public final Jinja_argContext jinja_arg() throws RecognitionException {
		Jinja_argContext _localctx = new Jinja_argContext(_ctx, getState());
		enterRule(_localctx, 456, RULE_jinja_arg);
		try {
			setState(1893);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,179,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1883);
				match(Character_String_Literal);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1884);
				match(NUMBER);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1885);
				((Jinja_argContext)_localctx).kw_name = identifier();
				setState(1886);
				match(EQUAL);
				setState(1887);
				match(Character_String_Literal);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1889);
				((Jinja_argContext)_localctx).kw_name = identifier();
				setState(1890);
				match(EQUAL);
				setState(1891);
				match(NUMBER);
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
	public static class Jinja_variable_accessContext extends ParserRuleContext {
		public List<Jinja_nameContext> jinja_name() {
			return getRuleContexts(Jinja_nameContext.class);
		}
		public Jinja_nameContext jinja_name(int i) {
			return getRuleContext(Jinja_nameContext.class,i);
		}
		public List<TerminalNode> DOT() { return getTokens(SQLSelectParserParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(SQLSelectParserParser.DOT, i);
		}
		public Jinja_variable_accessContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jinja_variable_access; }
	}

	public final Jinja_variable_accessContext jinja_variable_access() throws RecognitionException {
		Jinja_variable_accessContext _localctx = new Jinja_variable_accessContext(_ctx, getState());
		enterRule(_localctx, 458, RULE_jinja_variable_access);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1895);
			jinja_name();
			setState(1900);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DOT) {
				{
				{
				setState(1896);
				match(DOT);
				setState(1897);
				jinja_name();
				}
				}
				setState(1902);
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
	public static class Jinja_nameContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public TerminalNode NUMBER() { return getToken(SQLSelectParserParser.NUMBER, 0); }
		public Jinja_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jinja_name; }
	}

	public final Jinja_nameContext jinja_name() throws RecognitionException {
		Jinja_nameContext _localctx = new Jinja_nameContext(_ctx, getState());
		enterRule(_localctx, 460, RULE_jinja_name);
		try {
			setState(1905);
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
			case ESCAPE:
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
			case Bracket_Identifier:
			case Identifier:
			case Double_Quoted_Numeric_Identifier:
			case Dollar_Sign_Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(1903);
				identifier();
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(1904);
				match(NUMBER);
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
		enterRule(_localctx, 462, RULE_simple_numeric_identifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1907);
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
		enterRule(_localctx, 464, RULE_snowflake_quoted_numeric_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1909);
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
		enterRule(_localctx, 466, RULE_snowflake_dollar_function_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1911);
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
		public TerminalNode LEAD() { return getToken(SQLSelectParserParser.LEAD, 0); }
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
		public TerminalNode ROW_NUMBER() { return getToken(SQLSelectParserParser.ROW_NUMBER, 0); }
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
		public TerminalNode ESCAPE() { return getToken(SQLSelectParserParser.ESCAPE, 0); }
		public Nonreserved_keywordsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nonreserved_keywords; }
	}

	public final Nonreserved_keywordsContext nonreserved_keywords() throws RecognitionException {
		Nonreserved_keywordsContext _localctx = new Nonreserved_keywordsContext(_ctx, getState());
		enterRule(_localctx, 468, RULE_nonreserved_keywords);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1913);
			_la = _input.LA(1);
			if ( !(((((_la - 20)) & ~0x3f) == 0 && ((1L << (_la - 20)) & -5630315556929535L) != 0) || ((((_la - 84)) & ~0x3f) == 0 && ((1L << (_la - 84)) & -281754149913649L) != 0) || ((((_la - 148)) & ~0x3f) == 0 && ((1L << (_la - 148)) & -2305843009213693955L) != 0) || ((((_la - 212)) & ~0x3f) == 0 && ((1L << (_la - 212)) & 288230221528694783L) != 0)) ) {
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
		enterRule(_localctx, 470, RULE_signed_numeric_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1916);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PLUS || _la==MINUS) {
				{
				setState(1915);
				sign();
				}
			}

			setState(1918);
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
		enterRule(_localctx, 472, RULE_unsigned_literal);
		try {
			setState(1922);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DOT:
			case NUMBER:
			case Scientific_Numeric_Literal:
				enterOuterAlt(_localctx, 1);
				{
				setState(1920);
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
				setState(1921);
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
		enterRule(_localctx, 474, RULE_unsigned_numeric_literal);
		try {
			setState(1926);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,184,_ctx) ) {
			case 1:
				_localctx = new Ordinal_numberContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(1924);
				match(NUMBER);
				}
				break;
			case 2:
				_localctx = new Real_numberContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(1925);
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
		public TerminalNode Scientific_Numeric_Literal() { return getToken(SQLSelectParserParser.Scientific_Numeric_Literal, 0); }
		public Real_number_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_real_number_def; }
	}

	public final Real_number_defContext real_number_def() throws RecognitionException {
		Real_number_defContext _localctx = new Real_number_defContext(_ctx, getState());
		enterRule(_localctx, 476, RULE_real_number_def);
		try {
			setState(1944);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,188,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1928);
				match(NUMBER);
				setState(1929);
				match(DOT);
				setState(1931);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,185,_ctx) ) {
				case 1:
					{
					setState(1930);
					match(NUMBER);
					}
					break;
				}
				setState(1934);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,186,_ctx) ) {
				case 1:
					{
					setState(1933);
					exponent();
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1936);
				match(DOT);
				setState(1937);
				match(NUMBER);
				setState(1939);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,187,_ctx) ) {
				case 1:
					{
					setState(1938);
					exponent();
					}
					break;
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1941);
				match(NUMBER);
				setState(1942);
				exponent();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1943);
				match(Scientific_Numeric_Literal);
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
		enterRule(_localctx, 478, RULE_exponent);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1946);
			match(EXPONEN);
			setState(1947);
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
		enterRule(_localctx, 480, RULE_general_literal);
		try {
			setState(1952);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Character_String_Literal:
				enterOuterAlt(_localctx, 1);
				{
				setState(1949);
				character_literal();
				}
				break;
			case DATE:
			case TIME:
			case TIMESTAMP:
				enterOuterAlt(_localctx, 2);
				{
				setState(1950);
				datetime_literal();
				}
				break;
			case FALSE:
			case TRUE:
			case UNKNOWN:
				enterOuterAlt(_localctx, 3);
				{
				setState(1951);
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
		enterRule(_localctx, 482, RULE_character_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1954);
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
		enterRule(_localctx, 484, RULE_datetime_literal);
		try {
			setState(1959);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TIMESTAMP:
				enterOuterAlt(_localctx, 1);
				{
				setState(1956);
				timestamp_literal();
				}
				break;
			case TIME:
				enterOuterAlt(_localctx, 2);
				{
				setState(1957);
				time_literal();
				}
				break;
			case DATE:
				enterOuterAlt(_localctx, 3);
				{
				setState(1958);
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
		enterRule(_localctx, 486, RULE_time_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1961);
			match(TIME);
			setState(1962);
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
		enterRule(_localctx, 488, RULE_timestamp_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1964);
			match(TIMESTAMP);
			setState(1965);
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
		enterRule(_localctx, 490, RULE_date_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1967);
			match(DATE);
			setState(1968);
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
		enterRule(_localctx, 492, RULE_boolean_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1970);
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
		enterRule(_localctx, 494, RULE_data_type);
		try {
			setState(1975);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,191,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1972);
				variable_size_data_type();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1973);
				precision_scale_data_type();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1974);
				static_data_type();
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
		enterRule(_localctx, 496, RULE_variable_size_data_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1977);
			variable_data_type_name();
			setState(1979);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,192,_ctx) ) {
			case 1:
				{
				setState(1978);
				type_length();
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
		enterRule(_localctx, 498, RULE_variable_data_type_name);
		try {
			setState(2015);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,193,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1981);
				match(CHARACTER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1982);
				match(CHAR);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1983);
				match(CHARACTER);
				setState(1984);
				match(VARYING);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1985);
				match(CHAR);
				setState(1986);
				match(VARYING);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1987);
				match(VARCHAR);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1988);
				match(VARCHAR2);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1989);
				match(NATIONAL);
				setState(1990);
				match(CHARACTER);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1991);
				match(NATIONAL);
				setState(1992);
				match(CHAR);
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(1993);
				match(NCHAR);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(1994);
				match(NATIONAL);
				setState(1995);
				match(CHARACTER);
				setState(1996);
				match(VARYING);
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(1997);
				match(NATIONAL);
				setState(1998);
				match(CHAR);
				setState(1999);
				match(VARYING);
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(2000);
				match(NCHAR);
				setState(2001);
				match(VARYING);
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(2002);
				match(NVARCHAR);
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(2003);
				match(BLOB);
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				setState(2004);
				match(BYTEA);
				}
				break;
			case 16:
				enterOuterAlt(_localctx, 16);
				{
				setState(2005);
				match(BIT);
				}
				break;
			case 17:
				enterOuterAlt(_localctx, 17);
				{
				setState(2006);
				match(VARBIT);
				}
				break;
			case 18:
				enterOuterAlt(_localctx, 18);
				{
				setState(2007);
				match(BIT);
				setState(2008);
				match(VARYING);
				}
				break;
			case 19:
				enterOuterAlt(_localctx, 19);
				{
				setState(2009);
				match(BINARY);
				}
				break;
			case 20:
				enterOuterAlt(_localctx, 20);
				{
				setState(2010);
				match(BINARY);
				setState(2011);
				match(VARYING);
				}
				break;
			case 21:
				enterOuterAlt(_localctx, 21);
				{
				setState(2012);
				match(VARBINARY);
				}
				break;
			case 22:
				enterOuterAlt(_localctx, 22);
				{
				setState(2013);
				match(INTERVAL);
				}
				break;
			case 23:
				enterOuterAlt(_localctx, 23);
				{
				setState(2014);
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
		enterRule(_localctx, 500, RULE_type_length);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2017);
			match(LEFT_PAREN);
			setState(2018);
			match(NUMBER);
			setState(2019);
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
		enterRule(_localctx, 502, RULE_precision_scale_data_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2021);
			precision_data_type_name();
			setState(2023);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,194,_ctx) ) {
			case 1:
				{
				setState(2022);
				precision_param();
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
	public static class Precision_data_type_nameContext extends ParserRuleContext {
		public TerminalNode NUMERIC() { return getToken(SQLSelectParserParser.NUMERIC, 0); }
		public TerminalNode NUMBER() { return getToken(SQLSelectParserParser.NUMBER, 0); }
		public TerminalNode DECIMAL() { return getToken(SQLSelectParserParser.DECIMAL, 0); }
		public TerminalNode DEC() { return getToken(SQLSelectParserParser.DEC, 0); }
		public TerminalNode FLOAT() { return getToken(SQLSelectParserParser.FLOAT, 0); }
		public TerminalNode DOUBLE() { return getToken(SQLSelectParserParser.DOUBLE, 0); }
		public TerminalNode PRECISION() { return getToken(SQLSelectParserParser.PRECISION, 0); }
		public TerminalNode TIMESTAMP_LTZ() { return getToken(SQLSelectParserParser.TIMESTAMP_LTZ, 0); }
		public TerminalNode TIMESTAMP_NTZ() { return getToken(SQLSelectParserParser.TIMESTAMP_NTZ, 0); }
		public TerminalNode TIMESTAMP_TZ() { return getToken(SQLSelectParserParser.TIMESTAMP_TZ, 0); }
		public Precision_data_type_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_precision_data_type_name; }
	}

	public final Precision_data_type_nameContext precision_data_type_name() throws RecognitionException {
		Precision_data_type_nameContext _localctx = new Precision_data_type_nameContext(_ctx, getState());
		enterRule(_localctx, 504, RULE_precision_data_type_name);
		try {
			setState(2036);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,195,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2025);
				match(NUMERIC);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2026);
				match(NUMBER);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2027);
				match(DECIMAL);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2028);
				match(DEC);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2029);
				match(FLOAT);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2030);
				match(DOUBLE);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(2031);
				match(DOUBLE);
				setState(2032);
				match(PRECISION);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(2033);
				match(TIMESTAMP_LTZ);
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(2034);
				match(TIMESTAMP_NTZ);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(2035);
				match(TIMESTAMP_TZ);
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
		enterRule(_localctx, 506, RULE_precision_param);
		try {
			setState(2046);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,196,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2038);
				match(LEFT_PAREN);
				setState(2039);
				((Precision_paramContext)_localctx).precision = match(NUMBER);
				setState(2040);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2041);
				match(LEFT_PAREN);
				setState(2042);
				((Precision_paramContext)_localctx).precision = match(NUMBER);
				setState(2043);
				match(COMMA);
				setState(2044);
				((Precision_paramContext)_localctx).scale = match(NUMBER);
				setState(2045);
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
		enterRule(_localctx, 508, RULE_static_data_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2048);
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
		enterRule(_localctx, 510, RULE_static_data_type_name);
		try {
			setState(2111);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,197,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2050);
				match(TEXT);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2051);
				match(NAME);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2052);
				match(INET4);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2053);
				match(INET);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2054);
				match(CIDR);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2055);
				match(STRUCT);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(2056);
				match(UNION);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(2057);
				match(VARIANT);
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(2058);
				match(OBJECT);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(2059);
				match(JSON);
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(2060);
				match(JSONB);
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(2061);
				match(OID);
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(2062);
				match(XID);
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(2063);
				match(UUID);
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				setState(2064);
				match(PG_LSN);
				}
				break;
			case 16:
				enterOuterAlt(_localctx, 16);
				{
				setState(2065);
				match(PG_NODE_TREE);
				}
				break;
			case 17:
				enterOuterAlt(_localctx, 17);
				{
				setState(2066);
				match(REGPROC);
				}
				break;
			case 18:
				enterOuterAlt(_localctx, 18);
				{
				setState(2067);
				match(MACADDR);
				}
				break;
			case 19:
				enterOuterAlt(_localctx, 19);
				{
				setState(2068);
				match(INT1);
				}
				break;
			case 20:
				enterOuterAlt(_localctx, 20);
				{
				setState(2069);
				match(TINYINT);
				}
				break;
			case 21:
				enterOuterAlt(_localctx, 21);
				{
				setState(2070);
				match(INT2);
				}
				break;
			case 22:
				enterOuterAlt(_localctx, 22);
				{
				setState(2071);
				match(SMALLINT);
				}
				break;
			case 23:
				enterOuterAlt(_localctx, 23);
				{
				setState(2072);
				match(INT4);
				}
				break;
			case 24:
				enterOuterAlt(_localctx, 24);
				{
				setState(2073);
				match(INT);
				}
				break;
			case 25:
				enterOuterAlt(_localctx, 25);
				{
				setState(2074);
				match(INTEGER);
				}
				break;
			case 26:
				enterOuterAlt(_localctx, 26);
				{
				setState(2075);
				match(INT8);
				}
				break;
			case 27:
				enterOuterAlt(_localctx, 27);
				{
				setState(2076);
				match(BIGINT);
				}
				break;
			case 28:
				enterOuterAlt(_localctx, 28);
				{
				setState(2077);
				match(BIGSERIAL);
				}
				break;
			case 29:
				enterOuterAlt(_localctx, 29);
				{
				setState(2078);
				match(SMALLSERIAL);
				}
				break;
			case 30:
				enterOuterAlt(_localctx, 30);
				{
				setState(2079);
				match(SERIAL);
				}
				break;
			case 31:
				enterOuterAlt(_localctx, 31);
				{
				setState(2080);
				match(MONEY);
				}
				break;
			case 32:
				enterOuterAlt(_localctx, 32);
				{
				setState(2081);
				match(NUMBER_TYPE);
				}
				break;
			case 33:
				enterOuterAlt(_localctx, 33);
				{
				setState(2082);
				match(FLOAT4);
				}
				break;
			case 34:
				enterOuterAlt(_localctx, 34);
				{
				setState(2083);
				match(REAL);
				}
				break;
			case 35:
				enterOuterAlt(_localctx, 35);
				{
				setState(2084);
				match(FLOAT8);
				}
				break;
			case 36:
				enterOuterAlt(_localctx, 36);
				{
				setState(2085);
				match(BOOLEAN);
				}
				break;
			case 37:
				enterOuterAlt(_localctx, 37);
				{
				setState(2086);
				match(BOOL);
				}
				break;
			case 38:
				enterOuterAlt(_localctx, 38);
				{
				setState(2087);
				match(DATE);
				}
				break;
			case 39:
				enterOuterAlt(_localctx, 39);
				{
				setState(2088);
				match(DATETIME);
				}
				break;
			case 40:
				enterOuterAlt(_localctx, 40);
				{
				setState(2089);
				match(TIME);
				}
				break;
			case 41:
				enterOuterAlt(_localctx, 41);
				{
				setState(2090);
				match(TIME);
				setState(2091);
				match(WITH);
				setState(2092);
				match(TIME);
				setState(2093);
				match(ZONE);
				}
				break;
			case 42:
				enterOuterAlt(_localctx, 42);
				{
				setState(2094);
				match(TIMETZ);
				}
				break;
			case 43:
				enterOuterAlt(_localctx, 43);
				{
				setState(2095);
				match(TIMESTAMP_LTZ);
				}
				break;
			case 44:
				enterOuterAlt(_localctx, 44);
				{
				setState(2096);
				match(TIMESTAMP_NTZ);
				}
				break;
			case 45:
				enterOuterAlt(_localctx, 45);
				{
				setState(2097);
				match(TIMESTAMP_TZ);
				}
				break;
			case 46:
				enterOuterAlt(_localctx, 46);
				{
				setState(2098);
				match(TIMESTAMP);
				}
				break;
			case 47:
				enterOuterAlt(_localctx, 47);
				{
				setState(2099);
				match(TIMESTAMP);
				setState(2100);
				match(WITH);
				setState(2101);
				match(TIME);
				setState(2102);
				match(ZONE);
				}
				break;
			case 48:
				enterOuterAlt(_localctx, 48);
				{
				setState(2103);
				match(TIMESTAMP);
				setState(2104);
				match(WITHOUT);
				setState(2105);
				match(TIME);
				setState(2106);
				match(ZONE);
				}
				break;
			case 49:
				enterOuterAlt(_localctx, 49);
				{
				setState(2107);
				match(TIMESTAMPTZ);
				}
				break;
			case 50:
				enterOuterAlt(_localctx, 50);
				{
				setState(2108);
				match(ABSTIME);
				}
				break;
			case 51:
				enterOuterAlt(_localctx, 51);
				{
				setState(2109);
				match(ARRAY);
				}
				break;
			case 52:
				enterOuterAlt(_localctx, 52);
				{
				setState(2110);
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
		enterRule(_localctx, 512, RULE_puml_constant_identifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2113);
			_la = _input.LA(1);
			if ( !(((((_la - 300)) & ~0x3f) == 0 && ((1L << (_la - 300)) & 33554431L) != 0)) ) {
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 51:
			return relation_as_clause_sempred((Relation_as_clauseContext)_localctx, predIndex);
		case 64:
			return flatten_argument_list_sempred((Flatten_argument_listContext)_localctx, predIndex);
		case 65:
			return flatten_argument_sempred((Flatten_argumentContext)_localctx, predIndex);
		case 71:
			return generator_argument_list_sempred((Generator_argument_listContext)_localctx, predIndex);
		case 78:
			return infer_schema_argument_list_sempred((Infer_schema_argument_listContext)_localctx, predIndex);
		case 226:
			return jinja_function_call_sempred((Jinja_function_callContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean relation_as_clause_sempred(Relation_as_clauseContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return isAllowedImplicitAlias(_input.LT(1).getText());
		}
		return true;
	}
	private boolean flatten_argument_list_sempred(Flatten_argument_listContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return containsFlattenInputArgument(_localctx.getText());
		}
		return true;
	}
	private boolean flatten_argument_sempred(Flatten_argumentContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return isSnowflakeTableFunctionModeLiteral("FLATTEN", (((Flatten_argumentContext)_localctx).m!=null?_input.getText(((Flatten_argumentContext)_localctx).m.start,((Flatten_argumentContext)_localctx).m.stop):null));
		}
		return true;
	}
	private boolean generator_argument_list_sempred(Generator_argument_listContext _localctx, int predIndex) {
		switch (predIndex) {
		case 3:
			return containsGeneratorRowcountArgument(_localctx.getText());
		}
		return true;
	}
	private boolean infer_schema_argument_list_sempred(Infer_schema_argument_listContext _localctx, int predIndex) {
		switch (predIndex) {
		case 4:
			return containsInferSchemaLocationArgument(_localctx.getText());
		}
		return true;
	}
	private boolean jinja_function_call_sempred(Jinja_function_callContext _localctx, int predIndex) {
		switch (predIndex) {
		case 5:
			return isJinjaPrimaryFunction((((Jinja_function_callContext)_localctx).func_name!=null?_input.getText(((Jinja_function_callContext)_localctx).func_name.start,((Jinja_function_callContext)_localctx).func_name.stop):null));
		case 6:
			return isJinjaObjectMethod((((Jinja_function_callContext)_localctx).object_name!=null?_input.getText(((Jinja_function_callContext)_localctx).object_name.start,((Jinja_function_callContext)_localctx).object_name.stop):null), (((Jinja_function_callContext)_localctx).method_name!=null?_input.getText(((Jinja_function_callContext)_localctx).method_name.start,((Jinja_function_callContext)_localctx).method_name.stop):null));
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u0174\u0844\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
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
		"\u00c8\u0002\u00c9\u0007\u00c9\u0002\u00ca\u0007\u00ca\u0002\u00cb\u0007"+
		"\u00cb\u0002\u00cc\u0007\u00cc\u0002\u00cd\u0007\u00cd\u0002\u00ce\u0007"+
		"\u00ce\u0002\u00cf\u0007\u00cf\u0002\u00d0\u0007\u00d0\u0002\u00d1\u0007"+
		"\u00d1\u0002\u00d2\u0007\u00d2\u0002\u00d3\u0007\u00d3\u0002\u00d4\u0007"+
		"\u00d4\u0002\u00d5\u0007\u00d5\u0002\u00d6\u0007\u00d6\u0002\u00d7\u0007"+
		"\u00d7\u0002\u00d8\u0007\u00d8\u0002\u00d9\u0007\u00d9\u0002\u00da\u0007"+
		"\u00da\u0002\u00db\u0007\u00db\u0002\u00dc\u0007\u00dc\u0002\u00dd\u0007"+
		"\u00dd\u0002\u00de\u0007\u00de\u0002\u00df\u0007\u00df\u0002\u00e0\u0007"+
		"\u00e0\u0002\u00e1\u0007\u00e1\u0002\u00e2\u0007\u00e2\u0002\u00e3\u0007"+
		"\u00e3\u0002\u00e4\u0007\u00e4\u0002\u00e5\u0007\u00e5\u0002\u00e6\u0007"+
		"\u00e6\u0002\u00e7\u0007\u00e7\u0002\u00e8\u0007\u00e8\u0002\u00e9\u0007"+
		"\u00e9\u0002\u00ea\u0007\u00ea\u0002\u00eb\u0007\u00eb\u0002\u00ec\u0007"+
		"\u00ec\u0002\u00ed\u0007\u00ed\u0002\u00ee\u0007\u00ee\u0002\u00ef\u0007"+
		"\u00ef\u0002\u00f0\u0007\u00f0\u0002\u00f1\u0007\u00f1\u0002\u00f2\u0007"+
		"\u00f2\u0002\u00f3\u0007\u00f3\u0002\u00f4\u0007\u00f4\u0002\u00f5\u0007"+
		"\u00f5\u0002\u00f6\u0007\u00f6\u0002\u00f7\u0007\u00f7\u0002\u00f8\u0007"+
		"\u00f8\u0002\u00f9\u0007\u00f9\u0002\u00fa\u0007\u00fa\u0002\u00fb\u0007"+
		"\u00fb\u0002\u00fc\u0007\u00fc\u0002\u00fd\u0007\u00fd\u0002\u00fe\u0007"+
		"\u00fe\u0002\u00ff\u0007\u00ff\u0002\u0100\u0007\u0100\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0003\u0000\u0206\b\u0000\u0001\u0000\u0003\u0000\u0209"+
		"\b\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\b\u0001\b\u0003\b\u0224\b\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001"+
		"\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f"+
		"\u0001\f\u0001\f\u0005\f\u0235\b\f\n\f\f\f\u0238\t\f\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0003\r\u0242\b\r\u0001\u000e"+
		"\u0001\u000e\u0003\u000e\u0246\b\u000e\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u024f\b\u0010"+
		"\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0003\u0013\u025a\b\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0003\u0013\u0260\b\u0013\u0001\u0013"+
		"\u0003\u0013\u0263\b\u0013\u0001\u0014\u0001\u0014\u0003\u0014\u0267\b"+
		"\u0014\u0001\u0015\u0001\u0015\u0003\u0015\u026b\b\u0015\u0001\u0015\u0001"+
		"\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u0272\b\u0016\u0001"+
		"\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0003\u0017\u0279"+
		"\b\u0017\u0001\u0017\u0003\u0017\u027c\b\u0017\u0001\u0017\u0003\u0017"+
		"\u027f\b\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019"+
		"\u0001\u0019\u0005\u0019\u0287\b\u0019\n\u0019\f\u0019\u028a\t\u0019\u0001"+
		"\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001b\u0001\u001b\u0001"+
		"\u001b\u0001\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0001\u001d\u0001"+
		"\u001d\u0001\u001d\u0001\u001d\u0005\u001d\u029b\b\u001d\n\u001d\f\u001d"+
		"\u029e\t\u001d\u0001\u001e\u0001\u001e\u0003\u001e\u02a2\b\u001e\u0001"+
		"\u001f\u0001\u001f\u0001 \u0001 \u0001 \u0001 \u0005 \u02aa\b \n \f \u02ad"+
		"\t \u0001!\u0001!\u0003!\u02b1\b!\u0001\"\u0001\"\u0001#\u0001#\u0001"+
		"#\u0003#\u02b8\b#\u0001$\u0001$\u0001$\u0001$\u0001%\u0001%\u0003%\u02c0"+
		"\b%\u0001%\u0003%\u02c3\b%\u0001%\u0001%\u0001%\u0003%\u02c8\b%\u0001"+
		"%\u0003%\u02cb\b%\u0001%\u0003%\u02ce\b%\u0001%\u0003%\u02d1\b%\u0001"+
		"%\u0003%\u02d4\b%\u0001%\u0003%\u02d7\b%\u0003%\u02d9\b%\u0001&\u0001"+
		"&\u0001&\u0001\'\u0001\'\u0001(\u0001(\u0001(\u0005(\u02e3\b(\n(\f(\u02e6"+
		"\t(\u0001)\u0001)\u0003)\u02ea\b)\u0001)\u0003)\u02ed\b)\u0001*\u0003"+
		"*\u02f0\b*\u0001*\u0001*\u0001+\u0001+\u0001,\u0001,\u0003,\u02f8\b,\u0001"+
		",\u0001,\u0001-\u0001-\u0001-\u0003-\u02ff\b-\u0001.\u0001.\u0001/\u0001"+
		"/\u0001/\u0003/\u0306\b/\u0001/\u0001/\u0001/\u0003/\u030b\b/\u0001/\u0001"+
		"/\u0001/\u0001/\u0003/\u0311\b/\u0001/\u0001/\u0003/\u0315\b/\u0005/\u0317"+
		"\b/\n/\f/\u031a\t/\u00010\u00010\u00030\u031e\b0\u00010\u00010\u00010"+
		"\u00030\u0323\b0\u00010\u00010\u00010\u00010\u00030\u0329\b0\u00010\u0001"+
		"0\u00030\u032d\b0\u00050\u032f\b0\n0\f0\u0332\t0\u00010\u00030\u0335\b"+
		"0\u00011\u00011\u00012\u00012\u00032\u033b\b2\u00012\u00012\u00012\u0001"+
		"2\u00012\u00032\u0342\b2\u00012\u00012\u00032\u0346\b2\u00012\u00012\u0001"+
		"2\u00032\u034b\b2\u00032\u034d\b2\u00013\u00013\u00013\u00013\u00033\u0353"+
		"\b3\u00014\u00014\u00014\u00014\u00014\u00014\u00034\u035b\b4\u00015\u0001"+
		"5\u00015\u00015\u00035\u0361\b5\u00035\u0363\b5\u00015\u00015\u00015\u0003"+
		"5\u0368\b5\u00035\u036a\b5\u00016\u00016\u00016\u00016\u00016\u00016\u0003"+
		"6\u0372\b6\u00016\u00036\u0375\b6\u00017\u00037\u0378\b7\u00017\u0001"+
		"7\u00018\u00018\u00018\u00038\u037f\b8\u00038\u0381\b8\u00019\u00019\u0003"+
		"9\u0385\b9\u0001:\u0001:\u0001:\u0001;\u0001;\u0001;\u0001;\u0001;\u0001"+
		"<\u0001<\u0001=\u0001=\u0001=\u0001=\u0001=\u0001=\u0003=\u0397\b=\u0001"+
		">\u0001>\u0001>\u0001>\u0001>\u0001>\u0003>\u039f\b>\u0001?\u0001?\u0001"+
		"?\u0001?\u0001?\u0001@\u0001@\u0001@\u0005@\u03a9\b@\n@\f@\u03ac\t@\u0001"+
		"@\u0001@\u0001A\u0001A\u0001A\u0001A\u0001A\u0001A\u0001A\u0001A\u0001"+
		"A\u0001A\u0001A\u0001A\u0001A\u0001A\u0001A\u0001A\u0001A\u0003A\u03c1"+
		"\bA\u0001B\u0001B\u0001B\u0003B\u03c6\bB\u0001C\u0001C\u0001D\u0001D\u0003"+
		"D\u03cc\bD\u0001E\u0001E\u0001F\u0001F\u0001F\u0001F\u0001F\u0001G\u0001"+
		"G\u0001G\u0005G\u03d8\bG\nG\fG\u03db\tG\u0001G\u0001G\u0001H\u0001H\u0001"+
		"H\u0001H\u0001H\u0001H\u0003H\u03e5\bH\u0001I\u0001I\u0001J\u0001J\u0001"+
		"K\u0001K\u0001K\u0003K\u03ee\bK\u0001K\u0001K\u0001L\u0001L\u0001M\u0001"+
		"M\u0001M\u0001M\u0001M\u0001N\u0001N\u0001N\u0005N\u03fc\bN\nN\fN\u03ff"+
		"\tN\u0001N\u0001N\u0001O\u0001O\u0001O\u0001O\u0001O\u0001O\u0001O\u0001"+
		"O\u0001O\u0001O\u0001O\u0001O\u0001O\u0001O\u0001O\u0001O\u0001O\u0001"+
		"O\u0001O\u0001O\u0001O\u0003O\u0418\bO\u0001P\u0001P\u0001P\u0001P\u0003"+
		"P\u041e\bP\u0001Q\u0001Q\u0001R\u0001R\u0001R\u0001R\u0005R\u0426\bR\n"+
		"R\fR\u0429\tR\u0001R\u0001R\u0001S\u0001S\u0001S\u0001S\u0001S\u0001S"+
		"\u0001S\u0001S\u0001S\u0001T\u0001T\u0001U\u0001U\u0001U\u0003U\u043b"+
		"\bU\u0001U\u0001U\u0001V\u0001V\u0001V\u0001V\u0003V\u0443\bV\u0001W\u0001"+
		"W\u0001W\u0005W\u0448\bW\nW\fW\u044b\tW\u0001X\u0001X\u0001X\u0005X\u0450"+
		"\bX\nX\fX\u0453\tX\u0001Y\u0001Y\u0001Y\u0003Y\u0458\bY\u0001Y\u0001Y"+
		"\u0001Y\u0005Y\u045d\bY\nY\fY\u0460\tY\u0001Y\u0001Y\u0001Y\u0001Y\u0003"+
		"Y\u0466\bY\u0001Z\u0001Z\u0001Z\u0003Z\u046b\bZ\u0001Z\u0001Z\u0001Z\u0005"+
		"Z\u0470\bZ\nZ\fZ\u0473\tZ\u0001Z\u0001Z\u0001Z\u0001Z\u0001Z\u0003Z\u047a"+
		"\bZ\u0001[\u0001[\u0001[\u0001[\u0001[\u0001[\u0001[\u0001[\u0001[\u0001"+
		"[\u0003[\u0486\b[\u0001\\\u0001\\\u0001\\\u0003\\\u048b\b\\\u0001\\\u0001"+
		"\\\u0001\\\u0003\\\u0490\b\\\u0003\\\u0492\b\\\u0001]\u0001]\u0001]\u0001"+
		"]\u0001^\u0001^\u0001^\u0001^\u0001^\u0001^\u0001^\u0001^\u0001^\u0003"+
		"^\u04a1\b^\u0001_\u0001_\u0001`\u0001`\u0001`\u0001`\u0001`\u0001`\u0001"+
		"`\u0003`\u04ac\b`\u0001`\u0001`\u0003`\u04b0\b`\u0001`\u0001`\u0001`\u0003"+
		"`\u04b5\b`\u0001a\u0001a\u0001b\u0001b\u0001c\u0001c\u0001c\u0001c\u0003"+
		"c\u04bf\bc\u0001c\u0001c\u0001c\u0001c\u0001c\u0003c\u04c6\bc\u0001c\u0001"+
		"c\u0003c\u04ca\bc\u0001d\u0004d\u04cd\bd\u000bd\fd\u04ce\u0001e\u0001"+
		"e\u0001e\u0001e\u0001e\u0001f\u0004f\u04d7\bf\u000bf\ff\u04d8\u0001g\u0001"+
		"g\u0001g\u0001g\u0001g\u0001h\u0001h\u0001h\u0001i\u0001i\u0003i\u04e5"+
		"\bi\u0001j\u0001j\u0001k\u0001k\u0001k\u0001k\u0001k\u0001k\u0001k\u0001"+
		"l\u0001l\u0001m\u0001m\u0001m\u0001n\u0001n\u0001n\u0003n\u04f8\bn\u0001"+
		"n\u0001n\u0001n\u0001n\u0001n\u0001n\u0001n\u0003n\u0501\bn\u0001n\u0003"+
		"n\u0504\bn\u0003n\u0506\bn\u0001o\u0001o\u0001o\u0003o\u050b\bo\u0001"+
		"o\u0003o\u050e\bo\u0001o\u0003o\u0511\bo\u0001o\u0001o\u0001p\u0001p\u0001"+
		"p\u0001p\u0001q\u0001q\u0001q\u0001r\u0001r\u0001s\u0001s\u0001s\u0003"+
		"s\u0521\bs\u0001t\u0001t\u0001t\u0001t\u0001t\u0001u\u0001u\u0001u\u0003"+
		"u\u052b\bu\u0001v\u0001v\u0001v\u0001w\u0001w\u0001w\u0001x\u0001x\u0001"+
		"x\u0001y\u0001y\u0001z\u0001z\u0001{\u0001{\u0001{\u0001|\u0001|\u0001"+
		"|\u0001}\u0001}\u0001}\u0001}\u0003}\u0544\b}\u0001~\u0001~\u0001~\u0003"+
		"~\u0549\b~\u0001\u007f\u0001\u007f\u0001\u007f\u0005\u007f\u054e\b\u007f"+
		"\n\u007f\f\u007f\u0551\t\u007f\u0001\u0080\u0001\u0080\u0001\u0080\u0005"+
		"\u0080\u0556\b\u0080\n\u0080\f\u0080\u0559\t\u0080\u0001\u0081\u0003\u0081"+
		"\u055c\b\u0081\u0001\u0081\u0001\u0081\u0001\u0082\u0001\u0082\u0003\u0082"+
		"\u0562\b\u0082\u0001\u0083\u0001\u0083\u0001\u0084\u0001\u0084\u0001\u0084"+
		"\u0001\u0084\u0001\u0084\u0001\u0084\u0001\u0084\u0001\u0085\u0001\u0085"+
		"\u0001\u0085\u0003\u0085\u0570\b\u0085\u0001\u0086\u0001\u0086\u0001\u0087"+
		"\u0001\u0087\u0003\u0087\u0576\b\u0087\u0001\u0088\u0001\u0088\u0001\u0088"+
		"\u0005\u0088\u057b\b\u0088\n\u0088\f\u0088\u057e\t\u0088\u0001\u0089\u0001"+
		"\u0089\u0003\u0089\u0582\b\u0089\u0001\u008a\u0001\u008a\u0001\u008a\u0001"+
		"\u008a\u0001\u008a\u0001\u008b\u0001\u008b\u0001\u008c\u0003\u008c\u058c"+
		"\b\u008c\u0001\u008c\u0003\u008c\u058f\b\u008c\u0001\u008c\u0003\u008c"+
		"\u0592\b\u008c\u0001\u008c\u0001\u008c\u0001\u008c\u0001\u008c\u0001\u008c"+
		"\u0003\u008c\u0599\b\u008c\u0001\u008d\u0001\u008d\u0001\u008e\u0001\u008e"+
		"\u0001\u008e\u0001\u008e\u0001\u008e\u0001\u008e\u0001\u008e\u0001\u008e"+
		"\u0001\u008e\u0001\u008e\u0003\u008e\u05a7\b\u008e\u0001\u008e\u0001\u008e"+
		"\u0001\u008e\u0001\u008e\u0001\u008e\u0001\u008e\u0003\u008e\u05af\b\u008e"+
		"\u0001\u008e\u0001\u008e\u0003\u008e\u05b3\b\u008e\u0001\u008f\u0001\u008f"+
		"\u0001\u0090\u0001\u0090\u0001\u0091\u0001\u0091\u0001\u0092\u0001\u0092"+
		"\u0001\u0093\u0001\u0093\u0001\u0093\u0005\u0093\u05c0\b\u0093\n\u0093"+
		"\f\u0093\u05c3\t\u0093\u0001\u0094\u0001\u0094\u0001\u0094\u0005\u0094"+
		"\u05c8\b\u0094\n\u0094\f\u0094\u05cb\t\u0094\u0001\u0095\u0003\u0095\u05ce"+
		"\b\u0095\u0001\u0095\u0001\u0095\u0001\u0096\u0001\u0096\u0001\u0096\u0001"+
		"\u0096\u0001\u0096\u0001\u0096\u0003\u0096\u05d8\b\u0096\u0003\u0096\u05da"+
		"\b\u0096\u0001\u0097\u0001\u0097\u0003\u0097\u05de\b\u0097\u0001\u0098"+
		"\u0001\u0098\u0001\u0098\u0001\u0098\u0001\u0098\u0001\u0098\u0001\u0098"+
		"\u0003\u0098\u05e7\b\u0098\u0001\u0099\u0001\u0099\u0001\u009a\u0001\u009a"+
		"\u0003\u009a\u05ed\b\u009a\u0001\u009b\u0001\u009b\u0001\u009b\u0003\u009b"+
		"\u05f2\b\u009b\u0001\u009c\u0001\u009c\u0001\u009c\u0001\u009d\u0001\u009d"+
		"\u0001\u009e\u0001\u009e\u0001\u009e\u0001\u009e\u0001\u009f\u0001\u009f"+
		"\u0001\u009f\u0005\u009f\u0600\b\u009f\n\u009f\f\u009f\u0603\t\u009f\u0001"+
		"\u00a0\u0001\u00a0\u0003\u00a0\u0607\b\u00a0\u0001\u00a0\u0003\u00a0\u060a"+
		"\b\u00a0\u0001\u00a1\u0001\u00a1\u0001\u00a2\u0001\u00a2\u0001\u00a2\u0001"+
		"\u00a3\u0001\u00a3\u0001\u00a4\u0001\u00a4\u0001\u00a4\u0001\u00a4\u0003"+
		"\u00a4\u0617\b\u00a4\u0001\u00a5\u0001\u00a5\u0001\u00a5\u0001\u00a5\u0003"+
		"\u00a5\u061d\b\u00a5\u0001\u00a6\u0001\u00a6\u0001\u00a6\u0005\u00a6\u0622"+
		"\b\u00a6\n\u00a6\f\u00a6\u0625\t\u00a6\u0001\u00a7\u0001\u00a7\u0001\u00a7"+
		"\u0001\u00a7\u0003\u00a7\u062b\b\u00a7\u0001\u00a8\u0001\u00a8\u0001\u00a8"+
		"\u0005\u00a8\u0630\b\u00a8\n\u00a8\f\u00a8\u0633\t\u00a8\u0001\u00a9\u0001"+
		"\u00a9\u0001\u00a9\u0001\u00a9\u0001\u00a9\u0003\u00a9\u063a\b\u00a9\u0001"+
		"\u00aa\u0001\u00aa\u0001\u00aa\u0001\u00aa\u0001\u00aa\u0001\u00ab\u0001"+
		"\u00ab\u0001\u00ab\u0001\u00ab\u0001\u00ab\u0001\u00ac\u0001\u00ac\u0001"+
		"\u00ac\u0001\u00ad\u0001\u00ad\u0001\u00ad\u0001\u00ae\u0001\u00ae\u0001"+
		"\u00ae\u0001\u00af\u0001\u00af\u0001\u00af\u0005\u00af\u0652\b\u00af\n"+
		"\u00af\f\u00af\u0655\t\u00af\u0001\u00b0\u0001\u00b0\u0001\u00b0\u0001"+
		"\u00b0\u0001\u00b1\u0001\u00b1\u0003\u00b1\u065d\b\u00b1\u0001\u00b1\u0001"+
		"\u00b1\u0003\u00b1\u0661\b\u00b1\u0001\u00b2\u0001\u00b2\u0001\u00b2\u0001"+
		"\u00b2\u0001\u00b2\u0001\u00b2\u0003\u00b2\u0669\b\u00b2\u0001\u00b3\u0001"+
		"\u00b3\u0001\u00b4\u0001\u00b4\u0001\u00b5\u0001\u00b5\u0003\u00b5\u0671"+
		"\b\u00b5\u0001\u00b5\u0001\u00b5\u0003\u00b5\u0675\b\u00b5\u0001\u00b5"+
		"\u0001\u00b5\u0001\u00b5\u0001\u00b5\u0001\u00b6\u0001\u00b6\u0001\u00b7"+
		"\u0001\u00b7\u0003\u00b7\u067f\b\u00b7\u0001\u00b7\u0001\u00b7\u0001\u00b7"+
		"\u0001\u00b8\u0001\u00b8\u0003\u00b8\u0686\b\u00b8\u0001\u00b8\u0001\u00b8"+
		"\u0001\u00b8\u0003\u00b8\u068b\b\u00b8\u0001\u00b9\u0001\u00b9\u0001\u00b9"+
		"\u0001\u00ba\u0001\u00ba\u0001\u00ba\u0001\u00ba\u0001\u00ba\u0001\u00ba"+
		"\u0003\u00ba\u0696\b\u00ba\u0001\u00bb\u0001\u00bb\u0001\u00bb\u0005\u00bb"+
		"\u069b\b\u00bb\n\u00bb\f\u00bb\u069e\t\u00bb\u0001\u00bc\u0001\u00bc\u0001"+
		"\u00bc\u0001\u00bd\u0001\u00bd\u0001\u00bd\u0003\u00bd\u06a6\b\u00bd\u0001"+
		"\u00be\u0001\u00be\u0001\u00be\u0001\u00be\u0001\u00bf\u0001\u00bf\u0001"+
		"\u00bf\u0001\u00c0\u0001\u00c0\u0001\u00c0\u0001\u00c0\u0001\u00c0\u0001"+
		"\u00c1\u0001\u00c1\u0001\u00c1\u0005\u00c1\u06b7\b\u00c1\n\u00c1\f\u00c1"+
		"\u06ba\t\u00c1\u0001\u00c2\u0001\u00c2\u0001\u00c2\u0001\u00c2\u0001\u00c3"+
		"\u0001\u00c3\u0001\u00c3\u0001\u00c3\u0001\u00c4\u0001\u00c4\u0001\u00c4"+
		"\u0005\u00c4\u06c7\b\u00c4\n\u00c4\f\u00c4\u06ca\t\u00c4\u0001\u00c5\u0001"+
		"\u00c5\u0001\u00c5\u0001\u00c6\u0001\u00c6\u0001\u00c6\u0001\u00c7\u0001"+
		"\u00c7\u0001\u00c8\u0001\u00c8\u0003\u00c8\u06d6\b\u00c8\u0001\u00c9\u0001"+
		"\u00c9\u0001\u00c9\u0001\u00ca\u0001\u00ca\u0003\u00ca\u06dd\b\u00ca\u0001"+
		"\u00ca\u0001\u00ca\u0001\u00cb\u0001\u00cb\u0003\u00cb\u06e3\b\u00cb\u0001"+
		"\u00cb\u0001\u00cb\u0001\u00cc\u0001\u00cc\u0001\u00cd\u0001\u00cd\u0001"+
		"\u00ce\u0001\u00ce\u0001\u00ce\u0001\u00ce\u0001\u00ce\u0001\u00cf\u0001"+
		"\u00cf\u0003\u00cf\u06f2\b\u00cf\u0001\u00d0\u0001\u00d0\u0001\u00d1\u0001"+
		"\u00d1\u0001\u00d2\u0001\u00d2\u0001\u00d2\u0001\u00d3\u0001\u00d3\u0003"+
		"\u00d3\u06fd\b\u00d3\u0001\u00d4\u0001\u00d4\u0001\u00d5\u0001\u00d5\u0001"+
		"\u00d6\u0001\u00d6\u0001\u00d6\u0003\u00d6\u0706\b\u00d6\u0001\u00d6\u0001"+
		"\u00d6\u0001\u00d7\u0001\u00d7\u0001\u00d7\u0003\u00d7\u070d\b\u00d7\u0001"+
		"\u00d7\u0003\u00d7\u0710\b\u00d7\u0001\u00d8\u0001\u00d8\u0001\u00d9\u0001"+
		"\u00d9\u0001\u00d9\u0005\u00d9\u0717\b\u00d9\n\u00d9\f\u00d9\u071a\t\u00d9"+
		"\u0001\u00da\u0001\u00da\u0001\u00da\u0001\u00da\u0001\u00da\u0003\u00da"+
		"\u0721\b\u00da\u0001\u00db\u0001\u00db\u0001\u00db\u0001\u00db\u0001\u00db"+
		"\u0003\u00db\u0728\b\u00db\u0001\u00dc\u0001\u00dc\u0003\u00dc\u072c\b"+
		"\u00dc\u0001\u00dd\u0001\u00dd\u0001\u00de\u0001\u00de\u0001\u00df\u0001"+
		"\u00df\u0001\u00e0\u0001\u00e0\u0001\u00e1\u0001\u00e1\u0001\u00e1\u0001"+
		"\u00e1\u0001\u00e1\u0001\u00e1\u0001\u00e1\u0001\u00e1\u0003\u00e1\u073e"+
		"\b\u00e1\u0001\u00e2\u0001\u00e2\u0001\u00e2\u0001\u00e2\u0003\u00e2\u0744"+
		"\b\u00e2\u0001\u00e2\u0001\u00e2\u0001\u00e2\u0001\u00e2\u0001\u00e2\u0001"+
		"\u00e2\u0001\u00e2\u0001\u00e2\u0003\u00e2\u074e\b\u00e2\u0001\u00e2\u0001"+
		"\u00e2\u0003\u00e2\u0752\b\u00e2\u0001\u00e3\u0001\u00e3\u0001\u00e3\u0005"+
		"\u00e3\u0757\b\u00e3\n\u00e3\f\u00e3\u075a\t\u00e3\u0001\u00e4\u0001\u00e4"+
		"\u0001\u00e4\u0001\u00e4\u0001\u00e4\u0001\u00e4\u0001\u00e4\u0001\u00e4"+
		"\u0001\u00e4\u0001\u00e4\u0003\u00e4\u0766\b\u00e4\u0001\u00e5\u0001\u00e5"+
		"\u0001\u00e5\u0005\u00e5\u076b\b\u00e5\n\u00e5\f\u00e5\u076e\t\u00e5\u0001"+
		"\u00e6\u0001\u00e6\u0003\u00e6\u0772\b\u00e6\u0001\u00e7\u0001\u00e7\u0001"+
		"\u00e8\u0001\u00e8\u0001\u00e9\u0001\u00e9\u0001\u00ea\u0001\u00ea\u0001"+
		"\u00eb\u0003\u00eb\u077d\b\u00eb\u0001\u00eb\u0001\u00eb\u0001\u00ec\u0001"+
		"\u00ec\u0003\u00ec\u0783\b\u00ec\u0001\u00ed\u0001\u00ed\u0003\u00ed\u0787"+
		"\b\u00ed\u0001\u00ee\u0001\u00ee\u0001\u00ee\u0003\u00ee\u078c\b\u00ee"+
		"\u0001\u00ee\u0003\u00ee\u078f\b\u00ee\u0001\u00ee\u0001\u00ee\u0001\u00ee"+
		"\u0003\u00ee\u0794\b\u00ee\u0001\u00ee\u0001\u00ee\u0001\u00ee\u0003\u00ee"+
		"\u0799\b\u00ee\u0001\u00ef\u0001\u00ef\u0001\u00ef\u0001\u00f0\u0001\u00f0"+
		"\u0001\u00f0\u0003\u00f0\u07a1\b\u00f0\u0001\u00f1\u0001\u00f1\u0001\u00f2"+
		"\u0001\u00f2\u0001\u00f2\u0003\u00f2\u07a8\b\u00f2\u0001\u00f3\u0001\u00f3"+
		"\u0001\u00f3\u0001\u00f4\u0001\u00f4\u0001\u00f4\u0001\u00f5\u0001\u00f5"+
		"\u0001\u00f5\u0001\u00f6\u0001\u00f6\u0001\u00f7\u0001\u00f7\u0001\u00f7"+
		"\u0003\u00f7\u07b8\b\u00f7\u0001\u00f8\u0001\u00f8\u0003\u00f8\u07bc\b"+
		"\u00f8\u0001\u00f9\u0001\u00f9\u0001\u00f9\u0001\u00f9\u0001\u00f9\u0001"+
		"\u00f9\u0001\u00f9\u0001\u00f9\u0001\u00f9\u0001\u00f9\u0001\u00f9\u0001"+
		"\u00f9\u0001\u00f9\u0001\u00f9\u0001\u00f9\u0001\u00f9\u0001\u00f9\u0001"+
		"\u00f9\u0001\u00f9\u0001\u00f9\u0001\u00f9\u0001\u00f9\u0001\u00f9\u0001"+
		"\u00f9\u0001\u00f9\u0001\u00f9\u0001\u00f9\u0001\u00f9\u0001\u00f9\u0001"+
		"\u00f9\u0001\u00f9\u0001\u00f9\u0001\u00f9\u0001\u00f9\u0003\u00f9\u07e0"+
		"\b\u00f9\u0001\u00fa\u0001\u00fa\u0001\u00fa\u0001\u00fa\u0001\u00fb\u0001"+
		"\u00fb\u0003\u00fb\u07e8\b\u00fb\u0001\u00fc\u0001\u00fc\u0001\u00fc\u0001"+
		"\u00fc\u0001\u00fc\u0001\u00fc\u0001\u00fc\u0001\u00fc\u0001\u00fc\u0001"+
		"\u00fc\u0001\u00fc\u0003\u00fc\u07f5\b\u00fc\u0001\u00fd\u0001\u00fd\u0001"+
		"\u00fd\u0001\u00fd\u0001\u00fd\u0001\u00fd\u0001\u00fd\u0001\u00fd\u0003"+
		"\u00fd\u07ff\b\u00fd\u0001\u00fe\u0001\u00fe\u0001\u00ff\u0001\u00ff\u0001"+
		"\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001"+
		"\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001"+
		"\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001"+
		"\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001"+
		"\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001"+
		"\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001"+
		"\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001"+
		"\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001"+
		"\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001"+
		"\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0003\u00ff\u0840"+
		"\b\u00ff\u0001\u0100\u0001\u0100\u0001\u0100\u0000\u0000\u0101\u0000\u0002"+
		"\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e"+
		" \"$&(*,.02468:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086"+
		"\u0088\u008a\u008c\u008e\u0090\u0092\u0094\u0096\u0098\u009a\u009c\u009e"+
		"\u00a0\u00a2\u00a4\u00a6\u00a8\u00aa\u00ac\u00ae\u00b0\u00b2\u00b4\u00b6"+
		"\u00b8\u00ba\u00bc\u00be\u00c0\u00c2\u00c4\u00c6\u00c8\u00ca\u00cc\u00ce"+
		"\u00d0\u00d2\u00d4\u00d6\u00d8\u00da\u00dc\u00de\u00e0\u00e2\u00e4\u00e6"+
		"\u00e8\u00ea\u00ec\u00ee\u00f0\u00f2\u00f4\u00f6\u00f8\u00fa\u00fc\u00fe"+
		"\u0100\u0102\u0104\u0106\u0108\u010a\u010c\u010e\u0110\u0112\u0114\u0116"+
		"\u0118\u011a\u011c\u011e\u0120\u0122\u0124\u0126\u0128\u012a\u012c\u012e"+
		"\u0130\u0132\u0134\u0136\u0138\u013a\u013c\u013e\u0140\u0142\u0144\u0146"+
		"\u0148\u014a\u014c\u014e\u0150\u0152\u0154\u0156\u0158\u015a\u015c\u015e"+
		"\u0160\u0162\u0164\u0166\u0168\u016a\u016c\u016e\u0170\u0172\u0174\u0176"+
		"\u0178\u017a\u017c\u017e\u0180\u0182\u0184\u0186\u0188\u018a\u018c\u018e"+
		"\u0190\u0192\u0194\u0196\u0198\u019a\u019c\u019e\u01a0\u01a2\u01a4\u01a6"+
		"\u01a8\u01aa\u01ac\u01ae\u01b0\u01b2\u01b4\u01b6\u01b8\u01ba\u01bc\u01be"+
		"\u01c0\u01c2\u01c4\u01c6\u01c8\u01ca\u01cc\u01ce\u01d0\u01d2\u01d4\u01d6"+
		"\u01d8\u01da\u01dc\u01de\u01e0\u01e2\u01e4\u01e6\u01e8\u01ea\u01ec\u01ee"+
		"\u01f0\u01f2\u01f4\u01f6\u01f8\u01fa\u01fc\u01fe\u0200\u0000\u001e\u0002"+
		"\u0000\u000e\u000e55\u0002\u0000\u0002\u0002\u000b\u000b\u0003\u0000\u0010"+
		"\u0010\u001d\u001d++\u0002\u0000\u000f\u000f33\u000e\u0000==EFXXddfgk"+
		"kpptt\u007f\u007f\u0083\u0083\u0089\u008a\u008c\u008c\u0098\u0099\u009e"+
		"\u00cd\u0006\u0000\u0004\u0004..BBRR[[aa\u0002\u0000\b\b44\u0002\u0000"+
		"~~\u0085\u0085\u0002\u0000\u0095\u0095\u012b\u012b\u0004\u0000XXddfgt"+
		"t\u0002\u0000WWee\u0002\u0000\u0014\u0014**\u0001\u0000\u0120\u0121\u0001"+
		"\u0000\u0122\u0124\u0001\u0000\u008f\u0091\u0003\u0000\u0006\u0006\u001c"+
		"\u001c22\u0002\u0000<<LL\u0001\u0000\u010e\u0111\u0002\u0000\u0113\u0113"+
		"\u0118\u011c\u0002\u0000\u0005\u0005//\u0002\u0000\u0015\u0015\u001e\u001e"+
		"\u0003\u0000\u000f\u000f33\u0096\u0096\u0002\u0000\u0004\u0004..\u0004"+
		"\u0000II^^qr\u009c\u009c\b\u0000@@KKMNPPbcmo}}\u009b\u009b\u0004\u0000"+
		"\u0016\u0016\u001d\u001d++\u014d\u014f\u0001\u0000\u0147\u0148\u0002\u0000"+
		"\u012b\u012b\u016a\u016a\u0015\u0000\u0014\u0014#$**,,55::<EGGIWZ]_ce"+
		"egsuy{\u0083\u0085\u0094\u0096\u00d0\u00d2\u00e9\u00eb\u00f5\u00f7\u00f8"+
		"\u00fa\u010d\u0001\u0000\u012c\u0144\u08a9\u0000\u0205\u0001\u0000\u0000"+
		"\u0000\u0002\u020c\u0001\u0000\u0000\u0000\u0004\u020f\u0001\u0000\u0000"+
		"\u0000\u0006\u0212\u0001\u0000\u0000\u0000\b\u0215\u0001\u0000\u0000\u0000"+
		"\n\u0218\u0001\u0000\u0000\u0000\f\u021b\u0001\u0000\u0000\u0000\u000e"+
		"\u021e\u0001\u0000\u0000\u0000\u0010\u0223\u0001\u0000\u0000\u0000\u0012"+
		"\u0227\u0001\u0000\u0000\u0000\u0014\u022a\u0001\u0000\u0000\u0000\u0016"+
		"\u022d\u0001\u0000\u0000\u0000\u0018\u0230\u0001\u0000\u0000\u0000\u001a"+
		"\u0241\u0001\u0000\u0000\u0000\u001c\u0245\u0001\u0000\u0000\u0000\u001e"+
		"\u0247\u0001\u0000\u0000\u0000 \u024e\u0001\u0000\u0000\u0000\"\u0250"+
		"\u0001\u0000\u0000\u0000$\u0252\u0001\u0000\u0000\u0000&\u0259\u0001\u0000"+
		"\u0000\u0000(\u0264\u0001\u0000\u0000\u0000*\u0268\u0001\u0000\u0000\u0000"+
		",\u0271\u0001\u0000\u0000\u0000.\u0273\u0001\u0000\u0000\u00000\u0280"+
		"\u0001\u0000\u0000\u00002\u0283\u0001\u0000\u0000\u00004\u028b\u0001\u0000"+
		"\u0000\u00006\u028f\u0001\u0000\u0000\u00008\u0294\u0001\u0000\u0000\u0000"+
		":\u0296\u0001\u0000\u0000\u0000<\u029f\u0001\u0000\u0000\u0000>\u02a3"+
		"\u0001\u0000\u0000\u0000@\u02a5\u0001\u0000\u0000\u0000B\u02ae\u0001\u0000"+
		"\u0000\u0000D\u02b2\u0001\u0000\u0000\u0000F\u02b7\u0001\u0000\u0000\u0000"+
		"H\u02b9\u0001\u0000\u0000\u0000J\u02bd\u0001\u0000\u0000\u0000L\u02da"+
		"\u0001\u0000\u0000\u0000N\u02dd\u0001\u0000\u0000\u0000P\u02df\u0001\u0000"+
		"\u0000\u0000R\u02ec\u0001\u0000\u0000\u0000T\u02ef\u0001\u0000\u0000\u0000"+
		"V\u02f3\u0001\u0000\u0000\u0000X\u02f7\u0001\u0000\u0000\u0000Z\u02fb"+
		"\u0001\u0000\u0000\u0000\\\u0300\u0001\u0000\u0000\u0000^\u0302\u0001"+
		"\u0000\u0000\u0000`\u0330\u0001\u0000\u0000\u0000b\u0336\u0001\u0000\u0000"+
		"\u0000d\u034c\u0001\u0000\u0000\u0000f\u0352\u0001\u0000\u0000\u0000h"+
		"\u035a\u0001\u0000\u0000\u0000j\u035c\u0001\u0000\u0000\u0000l\u0374\u0001"+
		"\u0000\u0000\u0000n\u0377\u0001\u0000\u0000\u0000p\u0380\u0001\u0000\u0000"+
		"\u0000r\u0384\u0001\u0000\u0000\u0000t\u0386\u0001\u0000\u0000\u0000v"+
		"\u0389\u0001\u0000\u0000\u0000x\u038e\u0001\u0000\u0000\u0000z\u0396\u0001"+
		"\u0000\u0000\u0000|\u039e\u0001\u0000\u0000\u0000~\u03a0\u0001\u0000\u0000"+
		"\u0000\u0080\u03a5\u0001\u0000\u0000\u0000\u0082\u03c0\u0001\u0000\u0000"+
		"\u0000\u0084\u03c5\u0001\u0000\u0000\u0000\u0086\u03c7\u0001\u0000\u0000"+
		"\u0000\u0088\u03cb\u0001\u0000\u0000\u0000\u008a\u03cd\u0001\u0000\u0000"+
		"\u0000\u008c\u03cf\u0001\u0000\u0000\u0000\u008e\u03d4\u0001\u0000\u0000"+
		"\u0000\u0090\u03e4\u0001\u0000\u0000\u0000\u0092\u03e6\u0001\u0000\u0000"+
		"\u0000\u0094\u03e8\u0001\u0000\u0000\u0000\u0096\u03ea\u0001\u0000\u0000"+
		"\u0000\u0098\u03f1\u0001\u0000\u0000\u0000\u009a\u03f3\u0001\u0000\u0000"+
		"\u0000\u009c\u03f8\u0001\u0000\u0000\u0000\u009e\u0417\u0001\u0000\u0000"+
		"\u0000\u00a0\u041d\u0001\u0000\u0000\u0000\u00a2\u041f\u0001\u0000\u0000"+
		"\u0000\u00a4\u0421\u0001\u0000\u0000\u0000\u00a6\u042c\u0001\u0000\u0000"+
		"\u0000\u00a8\u0435\u0001\u0000\u0000\u0000\u00aa\u0437\u0001\u0000\u0000"+
		"\u0000\u00ac\u0442\u0001\u0000\u0000\u0000\u00ae\u0444\u0001\u0000\u0000"+
		"\u0000\u00b0\u044c\u0001\u0000\u0000\u0000\u00b2\u0465\u0001\u0000\u0000"+
		"\u0000\u00b4\u0479\u0001\u0000\u0000\u0000\u00b6\u0485\u0001\u0000\u0000"+
		"\u0000\u00b8\u0491\u0001\u0000\u0000\u0000\u00ba\u0493\u0001\u0000\u0000"+
		"\u0000\u00bc\u04a0\u0001\u0000\u0000\u0000\u00be\u04a2\u0001\u0000\u0000"+
		"\u0000\u00c0\u04b4\u0001\u0000\u0000\u0000\u00c2\u04b6\u0001\u0000\u0000"+
		"\u0000\u00c4\u04b8\u0001\u0000\u0000\u0000\u00c6\u04c9\u0001\u0000\u0000"+
		"\u0000\u00c8\u04cc\u0001\u0000\u0000\u0000\u00ca\u04d0\u0001\u0000\u0000"+
		"\u0000\u00cc\u04d6\u0001\u0000\u0000\u0000\u00ce\u04da\u0001\u0000\u0000"+
		"\u0000\u00d0\u04df\u0001\u0000\u0000\u0000\u00d2\u04e4\u0001\u0000\u0000"+
		"\u0000\u00d4\u04e6\u0001\u0000\u0000\u0000\u00d6\u04e8\u0001\u0000\u0000"+
		"\u0000\u00d8\u04ef\u0001\u0000\u0000\u0000\u00da\u04f1\u0001\u0000\u0000"+
		"\u0000\u00dc\u0505\u0001\u0000\u0000\u0000\u00de\u0507\u0001\u0000\u0000"+
		"\u0000\u00e0\u0514\u0001\u0000\u0000\u0000\u00e2\u0518\u0001\u0000\u0000"+
		"\u0000\u00e4\u051b\u0001\u0000\u0000\u0000\u00e6\u0520\u0001\u0000\u0000"+
		"\u0000\u00e8\u0522\u0001\u0000\u0000\u0000\u00ea\u052a\u0001\u0000\u0000"+
		"\u0000\u00ec\u052c\u0001\u0000\u0000\u0000\u00ee\u052f\u0001\u0000\u0000"+
		"\u0000\u00f0\u0532\u0001\u0000\u0000\u0000\u00f2\u0535\u0001\u0000\u0000"+
		"\u0000\u00f4\u0537\u0001\u0000\u0000\u0000\u00f6\u0539\u0001\u0000\u0000"+
		"\u0000\u00f8\u053c\u0001\u0000\u0000\u0000\u00fa\u0543\u0001\u0000\u0000"+
		"\u0000\u00fc\u0548\u0001\u0000\u0000\u0000\u00fe\u054a\u0001\u0000\u0000"+
		"\u0000\u0100\u0552\u0001\u0000\u0000\u0000\u0102\u055b\u0001\u0000\u0000"+
		"\u0000\u0104\u0561\u0001\u0000\u0000\u0000\u0106\u0563\u0001\u0000\u0000"+
		"\u0000\u0108\u0565\u0001\u0000\u0000\u0000\u010a\u056f\u0001\u0000\u0000"+
		"\u0000\u010c\u0571\u0001\u0000\u0000\u0000\u010e\u0575\u0001\u0000\u0000"+
		"\u0000\u0110\u0577\u0001\u0000\u0000\u0000\u0112\u0581\u0001\u0000\u0000"+
		"\u0000\u0114\u0583\u0001\u0000\u0000\u0000\u0116\u0588\u0001\u0000\u0000"+
		"\u0000\u0118\u0598\u0001\u0000\u0000\u0000\u011a\u059a\u0001\u0000\u0000"+
		"\u0000\u011c\u05b2\u0001\u0000\u0000\u0000\u011e\u05b4\u0001\u0000\u0000"+
		"\u0000\u0120\u05b6\u0001\u0000\u0000\u0000\u0122\u05b8\u0001\u0000\u0000"+
		"\u0000\u0124\u05ba\u0001\u0000\u0000\u0000\u0126\u05bc\u0001\u0000\u0000"+
		"\u0000\u0128\u05c4\u0001\u0000\u0000\u0000\u012a\u05cd\u0001\u0000\u0000"+
		"\u0000\u012c\u05d9\u0001\u0000\u0000\u0000\u012e\u05dd\u0001\u0000\u0000"+
		"\u0000\u0130\u05e6\u0001\u0000\u0000\u0000\u0132\u05e8\u0001\u0000\u0000"+
		"\u0000\u0134\u05ec\u0001\u0000\u0000\u0000\u0136\u05f1\u0001\u0000\u0000"+
		"\u0000\u0138\u05f3\u0001\u0000\u0000\u0000\u013a\u05f6\u0001\u0000\u0000"+
		"\u0000\u013c\u05f8\u0001\u0000\u0000\u0000\u013e\u05fc\u0001\u0000\u0000"+
		"\u0000\u0140\u0604\u0001\u0000\u0000\u0000\u0142\u060b\u0001\u0000\u0000"+
		"\u0000\u0144\u060d\u0001\u0000\u0000\u0000\u0146\u0610\u0001\u0000\u0000"+
		"\u0000\u0148\u0612\u0001\u0000\u0000\u0000\u014a\u0618\u0001\u0000\u0000"+
		"\u0000\u014c\u061e\u0001\u0000\u0000\u0000\u014e\u062a\u0001\u0000\u0000"+
		"\u0000\u0150\u062c\u0001\u0000\u0000\u0000\u0152\u0639\u0001\u0000\u0000"+
		"\u0000\u0154\u063b\u0001\u0000\u0000\u0000\u0156\u0640\u0001\u0000\u0000"+
		"\u0000\u0158\u0645\u0001\u0000\u0000\u0000\u015a\u0648\u0001\u0000\u0000"+
		"\u0000\u015c\u064b\u0001\u0000\u0000\u0000\u015e\u064e\u0001\u0000\u0000"+
		"\u0000\u0160\u0656\u0001\u0000\u0000\u0000\u0162\u0660\u0001\u0000\u0000"+
		"\u0000\u0164\u0668\u0001\u0000\u0000\u0000\u0166\u066a\u0001\u0000\u0000"+
		"\u0000\u0168\u066c\u0001\u0000\u0000\u0000\u016a\u066e\u0001\u0000\u0000"+
		"\u0000\u016c\u067a\u0001\u0000\u0000\u0000\u016e\u067c\u0001\u0000\u0000"+
		"\u0000\u0170\u0683\u0001\u0000\u0000\u0000\u0172\u068c\u0001\u0000\u0000"+
		"\u0000\u0174\u0695\u0001\u0000\u0000\u0000\u0176\u0697\u0001\u0000\u0000"+
		"\u0000\u0178\u069f\u0001\u0000\u0000\u0000\u017a\u06a5\u0001\u0000\u0000"+
		"\u0000\u017c\u06a7\u0001\u0000\u0000\u0000\u017e\u06ab\u0001\u0000\u0000"+
		"\u0000\u0180\u06ae\u0001\u0000\u0000\u0000\u0182\u06b3\u0001\u0000\u0000"+
		"\u0000\u0184\u06bb\u0001\u0000\u0000\u0000\u0186\u06bf\u0001\u0000\u0000"+
		"\u0000\u0188\u06c3\u0001\u0000\u0000\u0000\u018a\u06cb\u0001\u0000\u0000"+
		"\u0000\u018c\u06ce\u0001\u0000\u0000\u0000\u018e\u06d1\u0001\u0000\u0000"+
		"\u0000\u0190\u06d5\u0001\u0000\u0000\u0000\u0192\u06d7\u0001\u0000\u0000"+
		"\u0000\u0194\u06da\u0001\u0000\u0000\u0000\u0196\u06e0\u0001\u0000\u0000"+
		"\u0000\u0198\u06e6\u0001\u0000\u0000\u0000\u019a\u06e8\u0001\u0000\u0000"+
		"\u0000\u019c\u06ea\u0001\u0000\u0000\u0000\u019e\u06f1\u0001\u0000\u0000"+
		"\u0000\u01a0\u06f3\u0001\u0000\u0000\u0000\u01a2\u06f5\u0001\u0000\u0000"+
		"\u0000\u01a4\u06f7\u0001\u0000\u0000\u0000\u01a6\u06fc\u0001\u0000\u0000"+
		"\u0000\u01a8\u06fe\u0001\u0000\u0000\u0000\u01aa\u0700\u0001\u0000\u0000"+
		"\u0000\u01ac\u0702\u0001\u0000\u0000\u0000\u01ae\u070f\u0001\u0000\u0000"+
		"\u0000\u01b0\u0711\u0001\u0000\u0000\u0000\u01b2\u0713\u0001\u0000\u0000"+
		"\u0000\u01b4\u0720\u0001\u0000\u0000\u0000\u01b6\u0727\u0001\u0000\u0000"+
		"\u0000\u01b8\u072b\u0001\u0000\u0000\u0000\u01ba\u072d\u0001\u0000\u0000"+
		"\u0000\u01bc\u072f\u0001\u0000\u0000\u0000\u01be\u0731\u0001\u0000\u0000"+
		"\u0000\u01c0\u0733\u0001\u0000\u0000\u0000\u01c2\u073d\u0001\u0000\u0000"+
		"\u0000\u01c4\u0751\u0001\u0000\u0000\u0000\u01c6\u0753\u0001\u0000\u0000"+
		"\u0000\u01c8\u0765\u0001\u0000\u0000\u0000\u01ca\u0767\u0001\u0000\u0000"+
		"\u0000\u01cc\u0771\u0001\u0000\u0000\u0000\u01ce\u0773\u0001\u0000\u0000"+
		"\u0000\u01d0\u0775\u0001\u0000\u0000\u0000\u01d2\u0777\u0001\u0000\u0000"+
		"\u0000\u01d4\u0779\u0001\u0000\u0000\u0000\u01d6\u077c\u0001\u0000\u0000"+
		"\u0000\u01d8\u0782\u0001\u0000\u0000\u0000\u01da\u0786\u0001\u0000\u0000"+
		"\u0000\u01dc\u0798\u0001\u0000\u0000\u0000\u01de\u079a\u0001\u0000\u0000"+
		"\u0000\u01e0\u07a0\u0001\u0000\u0000\u0000\u01e2\u07a2\u0001\u0000\u0000"+
		"\u0000\u01e4\u07a7\u0001\u0000\u0000\u0000\u01e6\u07a9\u0001\u0000\u0000"+
		"\u0000\u01e8\u07ac\u0001\u0000\u0000\u0000\u01ea\u07af\u0001\u0000\u0000"+
		"\u0000\u01ec\u07b2\u0001\u0000\u0000\u0000\u01ee\u07b7\u0001\u0000\u0000"+
		"\u0000\u01f0\u07b9\u0001\u0000\u0000\u0000\u01f2\u07df\u0001\u0000\u0000"+
		"\u0000\u01f4\u07e1\u0001\u0000\u0000\u0000\u01f6\u07e5\u0001\u0000\u0000"+
		"\u0000\u01f8\u07f4\u0001\u0000\u0000\u0000\u01fa\u07fe\u0001\u0000\u0000"+
		"\u0000\u01fc\u0800\u0001\u0000\u0000\u0000\u01fe\u083f\u0001\u0000\u0000"+
		"\u0000\u0200\u0841\u0001\u0000\u0000\u0000\u0202\u0206\u0003\u0016\u000b"+
		"\u0000\u0203\u0206\u00036\u001b\u0000\u0204\u0206\u0003 \u0010\u0000\u0205"+
		"\u0202\u0001\u0000\u0000\u0000\u0205\u0203\u0001\u0000\u0000\u0000\u0205"+
		"\u0204\u0001\u0000\u0000\u0000\u0206\u0208\u0001\u0000\u0000\u0000\u0207"+
		"\u0209\u0005\u0115\u0000\u0000\u0208\u0207\u0001\u0000\u0000\u0000\u0208"+
		"\u0209\u0001\u0000\u0000\u0000\u0209\u020a\u0001\u0000\u0000\u0000\u020a"+
		"\u020b\u0005\u0000\u0000\u0001\u020b\u0001\u0001\u0000\u0000\u0000\u020c"+
		"\u020d\u0003\u00b4Z\u0000\u020d\u020e\u0005\u0000\u0000\u0001\u020e\u0003"+
		"\u0001\u0000\u0000\u0000\u020f\u0210\u0003\u00b6[\u0000\u0210\u0211\u0005"+
		"\u0000\u0000\u0001\u0211\u0005\u0001\u0000\u0000\u0000\u0212\u0213\u0003"+
		"\u0174\u00ba\u0000\u0213\u0214\u0005\u0000\u0000\u0001\u0214\u0007\u0001"+
		"\u0000\u0000\u0000\u0215\u0216\u0003\u00fa}\u0000\u0216\u0217\u0005\u0000"+
		"\u0000\u0001\u0217\t\u0001\u0000\u0000\u0000\u0218\u0219\u0003h4\u0000"+
		"\u0219\u021a\u0005\u0000\u0000\u0001\u021a\u000b\u0001\u0000\u0000\u0000"+
		"\u021b\u021c\u0003 \u0010\u0000\u021c\u021d\u0005\u0000\u0000\u0001\u021d"+
		"\r\u0001\u0000\u0000\u0000\u021e\u021f\u0003`0\u0000\u021f\u0220\u0005"+
		"\u0000\u0000\u0001\u0220\u000f\u0001\u0000\u0000\u0000\u0221\u0224\u0003"+
		"\u01d6\u00eb\u0000\u0222\u0224\u0003\u01d8\u00ec\u0000\u0223\u0221\u0001"+
		"\u0000\u0000\u0000\u0223\u0222\u0001\u0000\u0000\u0000\u0224\u0225\u0001"+
		"\u0000\u0000\u0000\u0225\u0226\u0005\u0000\u0000\u0001\u0226\u0011\u0001"+
		"\u0000\u0000\u0000\u0227\u0228\u0003\u017a\u00bd\u0000\u0228\u0229\u0005"+
		"\u0000\u0000\u0001\u0229\u0013\u0001\u0000\u0000\u0000\u022a\u022b\u0003"+
		"\"\u0011\u0000\u022b\u022c\u0005\u0000\u0000\u0001\u022c\u0015\u0001\u0000"+
		"\u0000\u0000\u022d\u022e\u0003\u0018\f\u0000\u022e\u022f\u0003 \u0010"+
		"\u0000\u022f\u0017\u0001\u0000\u0000\u0000\u0230\u0231\u0005:\u0000\u0000"+
		"\u0231\u0236\u0003\u001a\r\u0000\u0232\u0233\u0005\u0116\u0000\u0000\u0233"+
		"\u0235\u0003\u001a\r\u0000\u0234\u0232\u0001\u0000\u0000\u0000\u0235\u0238"+
		"\u0001\u0000\u0000\u0000\u0236\u0234\u0001\u0000\u0000\u0000\u0236\u0237"+
		"\u0001\u0000\u0000\u0000\u0237\u0019\u0001\u0000\u0000\u0000\u0238\u0236"+
		"\u0001\u0000\u0000\u0000\u0239\u023a\u0003\u001e\u000f\u0000\u023a\u023b"+
		"\u0005\u011e\u0000\u0000\u023b\u023c\u0003\u001c\u000e\u0000\u023c\u023d"+
		"\u0005\u011f\u0000\u0000\u023d\u0242\u0001\u0000\u0000\u0000\u023e\u023f"+
		"\u0003\u001e\u000f\u0000\u023f\u0240\u0003\u01b8\u00dc\u0000\u0240\u0242"+
		"\u0001\u0000\u0000\u0000\u0241\u0239\u0001\u0000\u0000\u0000\u0241\u023e"+
		"\u0001\u0000\u0000\u0000\u0242\u001b\u0001\u0000\u0000\u0000\u0243\u0246"+
		"\u0003\u0016\u000b\u0000\u0244\u0246\u0003 \u0010\u0000\u0245\u0243\u0001"+
		"\u0000\u0000\u0000\u0245\u0244\u0001\u0000\u0000\u0000\u0246\u001d\u0001"+
		"\u0000\u0000\u0000\u0247\u0248\u0003\u01b4\u00da\u0000\u0248\u0249\u0005"+
		"\u0001\u0000\u0000\u0249\u001f\u0001\u0000\u0000\u0000\u024a\u024f\u0003"+
		"8\u001c\u0000\u024b\u024f\u0003\"\u0011\u0000\u024c\u024f\u0003.\u0017"+
		"\u0000\u024d\u024f\u0003\u017a\u00bd\u0000\u024e\u024a\u0001\u0000\u0000"+
		"\u0000\u024e\u024b\u0001\u0000\u0000\u0000\u024e\u024c\u0001\u0000\u0000"+
		"\u0000\u024e\u024d\u0001\u0000\u0000\u0000\u024f!\u0001\u0000\u0000\u0000"+
		"\u0250\u0251\u0003$\u0012\u0000\u0251#\u0001\u0000\u0000\u0000\u0252\u0253"+
		"\u0003*\u0015\u0000\u0253\u0254\u0003&\u0013\u0000\u0254\u0255\u0003,"+
		"\u0016\u0000\u0255%\u0001\u0000\u0000\u0000\u0256\u025a\u0003j5\u0000"+
		"\u0257\u025a\u0003\u01b8\u00dc\u0000\u0258\u025a\u0003\u01c2\u00e1\u0000"+
		"\u0259\u0256\u0001\u0000\u0000\u0000\u0259\u0257\u0001\u0000\u0000\u0000"+
		"\u0259\u0258\u0001\u0000\u0000\u0000\u025a\u025f\u0001\u0000\u0000\u0000"+
		"\u025b\u025c\u0005\u011e\u0000\u0000\u025c\u025d\u0003\u00b0X\u0000\u025d"+
		"\u025e\u0005\u011f\u0000\u0000\u025e\u0260\u0001\u0000\u0000\u0000\u025f"+
		"\u025b\u0001\u0000\u0000\u0000\u025f\u0260\u0001\u0000\u0000\u0000\u0260"+
		"\u0262\u0001\u0000\u0000\u0000\u0261\u0263\u0003f3\u0000\u0262\u0261\u0001"+
		"\u0000\u0000\u0000\u0262\u0263\u0001\u0000\u0000\u0000\u0263\'\u0001\u0000"+
		"\u0000\u0000\u0264\u0266\u0003$\u0012\u0000\u0265\u0267\u00030\u0018\u0000"+
		"\u0266\u0265\u0001\u0000\u0000\u0000\u0266\u0267\u0001\u0000\u0000\u0000"+
		"\u0267)\u0001\u0000\u0000\u0000\u0268\u026a\u0005`\u0000\u0000\u0269\u026b"+
		"\u0005w\u0000\u0000\u026a\u0269\u0001\u0000\u0000\u0000\u026a\u026b\u0001"+
		"\u0000\u0000\u0000\u026b\u026c\u0001\u0000\u0000\u0000\u026c\u026d\u0005"+
		"\u0019\u0000\u0000\u026d+\u0001\u0000\u0000\u0000\u026e\u0272\u00038\u001c"+
		"\u0000\u026f\u0272\u0003\u01b8\u00dc\u0000\u0270\u0272\u0003\u018a\u00c5"+
		"\u0000\u0271\u026e\u0001\u0000\u0000\u0000\u0271\u026f\u0001\u0000\u0000"+
		"\u0000\u0271\u0270\u0001\u0000\u0000\u0000\u0272-\u0001\u0000\u0000\u0000"+
		"\u0273\u0274\u0005\u0094\u0000\u0000\u0274\u0275\u0003d2\u0000\u0275\u0276"+
		"\u0005\u0087\u0000\u0000\u0276\u0278\u00032\u0019\u0000\u0277\u0279\u0003"+
		"Z-\u0000\u0278\u0277\u0001\u0000\u0000\u0000\u0278\u0279\u0001\u0000\u0000"+
		"\u0000\u0279\u027b\u0001\u0000\u0000\u0000\u027a\u027c\u0003\u0138\u009c"+
		"\u0000\u027b\u027a\u0001\u0000\u0000\u0000\u027b\u027c\u0001\u0000\u0000"+
		"\u0000\u027c\u027e\u0001\u0000\u0000\u0000\u027d\u027f\u00030\u0018\u0000"+
		"\u027e\u027d\u0001\u0000\u0000\u0000\u027e\u027f\u0001\u0000\u0000\u0000"+
		"\u027f/\u0001\u0000\u0000\u0000\u0280\u0281\u0005,\u0000\u0000\u0281\u0282"+
		"\u0005\u0122\u0000\u0000\u02821\u0001\u0000\u0000\u0000\u0283\u0288\u0003"+
		"4\u001a\u0000\u0284\u0285\u0005\u0116\u0000\u0000\u0285\u0287\u00034\u001a"+
		"\u0000\u0286\u0284\u0001\u0000\u0000\u0000\u0287\u028a\u0001\u0000\u0000"+
		"\u0000\u0288\u0286\u0001\u0000\u0000\u0000\u0288\u0289\u0001\u0000\u0000"+
		"\u0000\u02893\u0001\u0000\u0000\u0000\u028a\u0288\u0001\u0000\u0000\u0000"+
		"\u028b\u028c\u0003\u00b2Y\u0000\u028c\u028d\u0005\u0113\u0000\u0000\u028d"+
		"\u028e\u0003\u0136\u009b\u0000\u028e5\u0001\u0000\u0000\u0000\u028f\u0290"+
		"\u0005\t\u0000\u0000\u0290\u0291\u00050\u0000\u0000\u0291\u0292\u0005"+
		"\u0001\u0000\u0000\u0292\u0293\u00038\u001c\u0000\u02937\u0001\u0000\u0000"+
		"\u0000\u0294\u0295\u0003:\u001d\u0000\u02959\u0001\u0000\u0000\u0000\u0296"+
		"\u029c\u0003@ \u0000\u0297\u0298\u0003<\u001e\u0000\u0298\u0299\u0003"+
		"@ \u0000\u0299\u029b\u0001\u0000\u0000\u0000\u029a\u0297\u0001\u0000\u0000"+
		"\u0000\u029b\u029e\u0001\u0000\u0000\u0000\u029c\u029a\u0001\u0000\u0000"+
		"\u0000\u029c\u029d\u0001\u0000\u0000\u0000\u029d;\u0001\u0000\u0000\u0000"+
		"\u029e\u029c\u0001\u0000\u0000\u0000\u029f\u02a1\u0003>\u001f\u0000\u02a0"+
		"\u02a2\u0003N\'\u0000\u02a1\u02a0\u0001\u0000\u0000\u0000\u02a1\u02a2"+
		"\u0001\u0000\u0000\u0000\u02a2=\u0001\u0000\u0000\u0000\u02a3\u02a4\u0005"+
		"\u0018\u0000\u0000\u02a4?\u0001\u0000\u0000\u0000\u02a5\u02ab\u0003F#"+
		"\u0000\u02a6\u02a7\u0003B!\u0000\u02a7\u02a8\u0003F#\u0000\u02a8\u02aa"+
		"\u0001\u0000\u0000\u0000\u02a9\u02a6\u0001\u0000\u0000\u0000\u02aa\u02ad"+
		"\u0001\u0000\u0000\u0000\u02ab\u02a9\u0001\u0000\u0000\u0000\u02ab\u02ac"+
		"\u0001\u0000\u0000\u0000\u02acA\u0001\u0000\u0000\u0000\u02ad\u02ab\u0001"+
		"\u0000\u0000\u0000\u02ae\u02b0\u0003D\"\u0000\u02af\u02b1\u0003N\'\u0000"+
		"\u02b0\u02af\u0001\u0000\u0000\u0000\u02b0\u02b1\u0001\u0000\u0000\u0000"+
		"\u02b1C\u0001\u0000\u0000\u0000\u02b2\u02b3\u0007\u0000\u0000\u0000\u02b3"+
		"E\u0001\u0000\u0000\u0000\u02b4\u02b8\u0003H$\u0000\u02b5\u02b8\u0003"+
		"J%\u0000\u02b6\u02b8\u0003\u01b8\u00dc\u0000\u02b7\u02b4\u0001\u0000\u0000"+
		"\u0000\u02b7\u02b5\u0001\u0000\u0000\u0000\u02b7\u02b6\u0001\u0000\u0000"+
		"\u0000\u02b8G\u0001\u0000\u0000\u0000\u02b9\u02ba\u0005\u011e\u0000\u0000"+
		"\u02ba\u02bb\u00038\u001c\u0000\u02bb\u02bc\u0005\u011f\u0000\u0000\u02bc"+
		"I\u0001\u0000\u0000\u0000\u02bd\u02bf\u0005-\u0000\u0000\u02be\u02c0\u0003"+
		"L&\u0000\u02bf\u02be\u0001\u0000\u0000\u0000\u02bf\u02c0\u0001\u0000\u0000"+
		"\u0000\u02c0\u02c2\u0001\u0000\u0000\u0000\u02c1\u02c3\u0003N\'\u0000"+
		"\u02c2\u02c1\u0001\u0000\u0000\u0000\u02c2\u02c3\u0001\u0000\u0000\u0000"+
		"\u02c3\u02c4\u0001\u0000\u0000\u0000\u02c4\u02d8\u0003P(\u0000\u02c5\u02c7"+
		"\u0003Z-\u0000\u02c6\u02c8\u0003\u0138\u009c\u0000\u02c7\u02c6\u0001\u0000"+
		"\u0000\u0000\u02c7\u02c8\u0001\u0000\u0000\u0000\u02c8\u02ca\u0001\u0000"+
		"\u0000\u0000\u02c9\u02cb\u0003\u014a\u00a5\u0000\u02ca\u02c9\u0001\u0000"+
		"\u0000\u0000\u02ca\u02cb\u0001\u0000\u0000\u0000\u02cb\u02cd\u0001\u0000"+
		"\u0000\u0000\u02cc\u02ce\u0003\u015a\u00ad\u0000\u02cd\u02cc\u0001\u0000"+
		"\u0000\u0000\u02cd\u02ce\u0001\u0000\u0000\u0000\u02ce\u02d0\u0001\u0000"+
		"\u0000\u0000\u02cf\u02d1\u0003\u015c\u00ae\u0000\u02d0\u02cf\u0001\u0000"+
		"\u0000\u0000\u02d0\u02d1\u0001\u0000\u0000\u0000\u02d1\u02d3\u0001\u0000"+
		"\u0000\u0000\u02d2\u02d4\u0003\u013c\u009e\u0000\u02d3\u02d2\u0001\u0000"+
		"\u0000\u0000\u02d3\u02d4\u0001\u0000\u0000\u0000\u02d4\u02d6\u0001\u0000"+
		"\u0000\u0000\u02d5\u02d7\u0003\u0148\u00a4\u0000\u02d6\u02d5\u0001\u0000"+
		"\u0000\u0000\u02d6\u02d7\u0001\u0000\u0000\u0000\u02d7\u02d9\u0001\u0000"+
		"\u0000\u0000\u02d8\u02c5\u0001\u0000\u0000\u0000\u02d8\u02d9\u0001\u0000"+
		"\u0000\u0000\u02d9K\u0001\u0000\u0000\u0000\u02da\u02db\u0005\u0019\u0000"+
		"\u0000\u02db\u02dc\u0003j5\u0000\u02dcM\u0001\u0000\u0000\u0000\u02dd"+
		"\u02de\u0007\u0001\u0000\u0000\u02deO\u0001\u0000\u0000\u0000\u02df\u02e4"+
		"\u0003R)\u0000\u02e0\u02e1\u0005\u0116\u0000\u0000\u02e1\u02e3\u0003R"+
		")\u0000\u02e2\u02e0\u0001\u0000\u0000\u0000\u02e3\u02e6\u0001\u0000\u0000"+
		"\u0000\u02e4\u02e2\u0001\u0000\u0000\u0000\u02e4\u02e5\u0001\u0000\u0000"+
		"\u0000\u02e5Q\u0001\u0000\u0000\u0000\u02e6\u02e4\u0001\u0000\u0000\u0000"+
		"\u02e7\u02e9\u0003\u00fa}\u0000\u02e8\u02ea\u0003T*\u0000\u02e9\u02e8"+
		"\u0001\u0000\u0000\u0000\u02e9\u02ea\u0001\u0000\u0000\u0000\u02ea\u02ed"+
		"\u0001\u0000\u0000\u0000\u02eb\u02ed\u0003V+\u0000\u02ec\u02e7\u0001\u0000"+
		"\u0000\u0000\u02ec\u02eb\u0001\u0000\u0000\u0000\u02edS\u0001\u0000\u0000"+
		"\u0000\u02ee\u02f0\u0005\u0001\u0000\u0000\u02ef\u02ee\u0001\u0000\u0000"+
		"\u0000\u02ef\u02f0\u0001\u0000\u0000\u0000\u02f0\u02f1\u0001\u0000\u0000"+
		"\u0000\u02f1\u02f2\u0003\u01b6\u00db\u0000\u02f2U\u0001\u0000\u0000\u0000"+
		"\u02f3\u02f4\u0003X,\u0000\u02f4W\u0001\u0000\u0000\u0000\u02f5\u02f6"+
		"\u0005\u0167\u0000\u0000\u02f6\u02f8\u0005\u0125\u0000\u0000\u02f7\u02f5"+
		"\u0001\u0000\u0000\u0000\u02f7\u02f8\u0001\u0000\u0000\u0000\u02f8\u02f9"+
		"\u0001\u0000\u0000\u0000\u02f9\u02fa\u0005\u0122\u0000\u0000\u02faY\u0001"+
		"\u0000\u0000\u0000\u02fb\u02fc\u0005\u0011\u0000\u0000\u02fc\u02fe\u0003"+
		"^/\u0000\u02fd\u02ff\u0003\\.\u0000\u02fe\u02fd\u0001\u0000\u0000\u0000"+
		"\u02fe\u02ff\u0001\u0000\u0000\u0000\u02ff[\u0001\u0000\u0000\u0000\u0300"+
		"\u0301\u0003\u01b8\u00dc\u0000\u0301]\u0001\u0000\u0000\u0000\u0302\u0318"+
		"\u0003d2\u0000\u0303\u0305\u0005\u0116\u0000\u0000\u0304\u0306\u0003b"+
		"1\u0000\u0305\u0304\u0001\u0000\u0000\u0000\u0305\u0306\u0001\u0000\u0000"+
		"\u0000\u0306\u0307\u0001\u0000\u0000\u0000\u0307\u0317\u0003d2\u0000\u0308"+
		"\u030a\u0003l6\u0000\u0309\u030b\u0003b1\u0000\u030a\u0309\u0001\u0000"+
		"\u0000\u0000\u030a\u030b\u0001\u0000\u0000\u0000\u030b\u030c\u0001\u0000"+
		"\u0000\u0000\u030c\u030d\u0003d2\u0000\u030d\u0317\u0001\u0000\u0000\u0000"+
		"\u030e\u0310\u0003n7\u0000\u030f\u0311\u0003b1\u0000\u0310\u030f\u0001"+
		"\u0000\u0000\u0000\u0310\u0311\u0001\u0000\u0000\u0000\u0311\u0312\u0001"+
		"\u0000\u0000\u0000\u0312\u0314\u0003d2\u0000\u0313\u0315\u0003r9\u0000"+
		"\u0314\u0313\u0001\u0000\u0000\u0000\u0314\u0315\u0001\u0000\u0000\u0000"+
		"\u0315\u0317\u0001\u0000\u0000\u0000\u0316\u0303\u0001\u0000\u0000\u0000"+
		"\u0316\u0308\u0001\u0000\u0000\u0000\u0316\u030e\u0001\u0000\u0000\u0000"+
		"\u0317\u031a\u0001\u0000\u0000\u0000\u0318\u0316\u0001\u0000\u0000\u0000"+
		"\u0318\u0319\u0001\u0000\u0000\u0000\u0319_\u0001\u0000\u0000\u0000\u031a"+
		"\u0318\u0001\u0000\u0000\u0000\u031b\u031d\u0005\u0116\u0000\u0000\u031c"+
		"\u031e\u0003b1\u0000\u031d\u031c\u0001\u0000\u0000\u0000\u031d\u031e\u0001"+
		"\u0000\u0000\u0000\u031e\u031f\u0001\u0000\u0000\u0000\u031f\u032f\u0003"+
		"d2\u0000\u0320\u0322\u0003l6\u0000\u0321\u0323\u0003b1\u0000\u0322\u0321"+
		"\u0001\u0000\u0000\u0000\u0322\u0323\u0001\u0000\u0000\u0000\u0323\u0324"+
		"\u0001\u0000\u0000\u0000\u0324\u0325\u0003d2\u0000\u0325\u032f\u0001\u0000"+
		"\u0000\u0000\u0326\u0328\u0003n7\u0000\u0327\u0329\u0003b1\u0000\u0328"+
		"\u0327\u0001\u0000\u0000\u0000\u0328\u0329\u0001\u0000\u0000\u0000\u0329"+
		"\u032a\u0001\u0000\u0000\u0000\u032a\u032c\u0003d2\u0000\u032b\u032d\u0003"+
		"r9\u0000\u032c\u032b\u0001\u0000\u0000\u0000\u032c\u032d\u0001\u0000\u0000"+
		"\u0000\u032d\u032f\u0001\u0000\u0000\u0000\u032e\u031b\u0001\u0000\u0000"+
		"\u0000\u032e\u0320\u0001\u0000\u0000\u0000\u032e\u0326\u0001\u0000\u0000"+
		"\u0000\u032f\u0332\u0001\u0000\u0000\u0000\u0330\u032e\u0001\u0000\u0000"+
		"\u0000\u0330\u0331\u0001\u0000\u0000\u0000\u0331\u0334\u0001\u0000\u0000"+
		"\u0000\u0332\u0330\u0001\u0000\u0000\u0000\u0333\u0335\u0003\\.\u0000"+
		"\u0334\u0333\u0001\u0000\u0000\u0000\u0334\u0335\u0001\u0000\u0000\u0000"+
		"\u0335a\u0001\u0000\u0000\u0000\u0336\u0337\u0005\u015d\u0000\u0000\u0337"+
		"c\u0001\u0000\u0000\u0000\u0338\u033a\u0003j5\u0000\u0339\u033b\u0003"+
		"f3\u0000\u033a\u0339\u0001\u0000\u0000\u0000\u033a\u033b\u0001\u0000\u0000"+
		"\u0000\u033b\u034d\u0001\u0000\u0000\u0000\u033c\u033d\u0003\u01b8\u00dc"+
		"\u0000\u033d\u033e\u0003T*\u0000\u033e\u034d\u0001\u0000\u0000\u0000\u033f"+
		"\u0341\u0003\u01c2\u00e1\u0000\u0340\u0342\u0003f3\u0000\u0341\u0340\u0001"+
		"\u0000\u0000\u0000\u0341\u0342\u0001\u0000\u0000\u0000\u0342\u034d\u0001"+
		"\u0000\u0000\u0000\u0343\u0345\u0003z=\u0000\u0344\u0346\u0003f3\u0000"+
		"\u0345\u0344\u0001\u0000\u0000\u0000\u0345\u0346\u0001\u0000\u0000\u0000"+
		"\u0346\u034d\u0001\u0000\u0000\u0000\u0347\u034d\u0003\u017a\u00bd\u0000"+
		"\u0348\u034a\u0003H$\u0000\u0349\u034b\u0003f3\u0000\u034a\u0349\u0001"+
		"\u0000\u0000\u0000\u034a\u034b\u0001\u0000\u0000\u0000\u034b\u034d\u0001"+
		"\u0000\u0000\u0000\u034c\u0338\u0001\u0000\u0000\u0000\u034c\u033c\u0001"+
		"\u0000\u0000\u0000\u034c\u033f\u0001\u0000\u0000\u0000\u034c\u0343\u0001"+
		"\u0000\u0000\u0000\u034c\u0347\u0001\u0000\u0000\u0000\u034c\u0348\u0001"+
		"\u0000\u0000\u0000\u034de\u0001\u0000\u0000\u0000\u034e\u034f\u0005\u0001"+
		"\u0000\u0000\u034f\u0353\u0003\u01b6\u00db\u0000\u0350\u0351\u00043\u0000"+
		"\u0000\u0351\u0353\u0003\u01b6\u00db\u0000\u0352\u034e\u0001\u0000\u0000"+
		"\u0000\u0352\u0350\u0001\u0000\u0000\u0000\u0353g\u0001\u0000\u0000\u0000"+
		"\u0354\u035b\u0003j5\u0000\u0355\u035b\u0003\u01b8\u00dc\u0000\u0356\u035b"+
		"\u0003\u01c2\u00e1\u0000\u0357\u035b\u0003z=\u0000\u0358\u035b\u0003\u017a"+
		"\u00bd\u0000\u0359\u035b\u0003H$\u0000\u035a\u0354\u0001\u0000\u0000\u0000"+
		"\u035a\u0355\u0001\u0000\u0000\u0000\u035a\u0356\u0001\u0000\u0000\u0000"+
		"\u035a\u0357\u0001\u0000\u0000\u0000\u035a\u0358\u0001\u0000\u0000\u0000"+
		"\u035a\u0359\u0001\u0000\u0000\u0000\u035bi\u0001\u0000\u0000\u0000\u035c"+
		"\u0362\u0003\u01b4\u00da\u0000\u035d\u0360\u0005\u0125\u0000\u0000\u035e"+
		"\u0361\u0003\u01ce\u00e7\u0000\u035f\u0361\u0003\u01b4\u00da\u0000\u0360"+
		"\u035e\u0001\u0000\u0000\u0000\u0360\u035f\u0001\u0000\u0000\u0000\u0361"+
		"\u0363\u0001\u0000\u0000\u0000\u0362\u035d\u0001\u0000\u0000\u0000\u0362"+
		"\u0363\u0001\u0000\u0000\u0000\u0363\u0369\u0001\u0000\u0000\u0000\u0364"+
		"\u0367\u0005\u0125\u0000\u0000\u0365\u0368\u0003\u01ce\u00e7\u0000\u0366"+
		"\u0368\u0003\u01b4\u00da\u0000\u0367\u0365\u0001\u0000\u0000\u0000\u0367"+
		"\u0366\u0001\u0000\u0000\u0000\u0368\u036a\u0001\u0000\u0000\u0000\u0369"+
		"\u0364\u0001\u0000\u0000\u0000\u0369\u036a\u0001\u0000\u0000\u0000\u036a"+
		"k\u0001\u0000\u0000\u0000\u036b\u036c\u0005\n\u0000\u0000\u036c\u0375"+
		"\u0005\u001b\u0000\u0000\u036d\u036e\u00055\u0000\u0000\u036e\u0375\u0005"+
		"\u001b\u0000\u0000\u036f\u0371\u0005 \u0000\u0000\u0370\u0372\u0003p8"+
		"\u0000\u0371\u0370\u0001\u0000\u0000\u0000\u0371\u0372\u0001\u0000\u0000"+
		"\u0000\u0372\u0373\u0001\u0000\u0000\u0000\u0373\u0375\u0005\u001b\u0000"+
		"\u0000\u0374\u036b\u0001\u0000\u0000\u0000\u0374\u036d\u0001\u0000\u0000"+
		"\u0000\u0374\u036f\u0001\u0000\u0000\u0000\u0375m\u0001\u0000\u0000\u0000"+
		"\u0376\u0378\u0003p8\u0000\u0377\u0376\u0001\u0000\u0000\u0000\u0377\u0378"+
		"\u0001\u0000\u0000\u0000\u0378\u0379\u0001\u0000\u0000\u0000\u0379\u037a"+
		"\u0005\u001b\u0000\u0000\u037ao\u0001\u0000\u0000\u0000\u037b\u0381\u0005"+
		"\u0017\u0000\u0000\u037c\u037e\u0007\u0002\u0000\u0000\u037d\u037f\u0005"+
		"\'\u0000\u0000\u037e\u037d\u0001\u0000\u0000\u0000\u037e\u037f\u0001\u0000"+
		"\u0000\u0000\u037f\u0381\u0001\u0000\u0000\u0000\u0380\u037b\u0001\u0000"+
		"\u0000\u0000\u0380\u037c\u0001\u0000\u0000\u0000\u0381q\u0001\u0000\u0000"+
		"\u0000\u0382\u0385\u0003t:\u0000\u0383\u0385\u0003v;\u0000\u0384\u0382"+
		"\u0001\u0000\u0000\u0000\u0384\u0383\u0001\u0000\u0000\u0000\u0385s\u0001"+
		"\u0000\u0000\u0000\u0386\u0387\u0005&\u0000\u0000\u0387\u0388\u0003\u013a"+
		"\u009d\u0000\u0388u\u0001\u0000\u0000\u0000\u0389\u038a\u0003x<\u0000"+
		"\u038a\u038b\u0005\u011e\u0000\u0000\u038b\u038c\u0003\u00b0X\u0000\u038c"+
		"\u038d\u0005\u011f\u0000\u0000\u038dw\u0001\u0000\u0000\u0000\u038e\u038f"+
		"\u00057\u0000\u0000\u038fy\u0001\u0000\u0000\u0000\u0390\u0391\u00050"+
		"\u0000\u0000\u0391\u0392\u0005\u011e\u0000\u0000\u0392\u0393\u0003|>\u0000"+
		"\u0393\u0394\u0005\u011f\u0000\u0000\u0394\u0397\u0001\u0000\u0000\u0000"+
		"\u0395\u0397\u0003|>\u0000\u0396\u0390\u0001\u0000\u0000\u0000\u0396\u0395"+
		"\u0001\u0000\u0000\u0000\u0397{\u0001\u0000\u0000\u0000\u0398\u039f\u0003"+
		"~?\u0000\u0399\u039f\u0003\u008cF\u0000\u039a\u039f\u0003\u0096K\u0000"+
		"\u039b\u039f\u0003\u009aM\u0000\u039c\u039f\u0003\u00a6S\u0000\u039d\u039f"+
		"\u0003\u00aaU\u0000\u039e\u0398\u0001\u0000\u0000\u0000\u039e\u0399\u0001"+
		"\u0000\u0000\u0000\u039e\u039a\u0001\u0000\u0000\u0000\u039e\u039b\u0001"+
		"\u0000\u0000\u0000\u039e\u039c\u0001\u0000\u0000\u0000\u039e\u039d\u0001"+
		"\u0000\u0000\u0000\u039f}\u0001\u0000\u0000\u0000\u03a0\u03a1\u0003\u0086"+
		"C\u0000\u03a1\u03a2\u0005\u011e\u0000\u0000\u03a2\u03a3\u0003\u0080@\u0000"+
		"\u03a3\u03a4\u0005\u011f\u0000\u0000\u03a4\u007f\u0001\u0000\u0000\u0000"+
		"\u03a5\u03aa\u0003\u0082A\u0000\u03a6\u03a7\u0005\u0116\u0000\u0000\u03a7"+
		"\u03a9\u0003\u0082A\u0000\u03a8\u03a6\u0001\u0000\u0000\u0000\u03a9\u03ac"+
		"\u0001\u0000\u0000\u0000\u03aa\u03a8\u0001\u0000\u0000\u0000\u03aa\u03ab"+
		"\u0001\u0000\u0000\u0000\u03ab\u03ad\u0001\u0000\u0000\u0000\u03ac\u03aa"+
		"\u0001\u0000\u0000\u0000\u03ad\u03ae\u0004@\u0001\u0001\u03ae\u0081\u0001"+
		"\u0000\u0000\u0000\u03af\u03b0\u0005\u0159\u0000\u0000\u03b0\u03b1\u0005"+
		"\u011d\u0000\u0000\u03b1\u03c1\u0003\u0084B\u0000\u03b2\u03b3\u0005\u015a"+
		"\u0000\u0000\u03b3\u03b4\u0005\u011d\u0000\u0000\u03b4\u03c1\u0003\u0084"+
		"B\u0000\u03b5\u03b6\u0005\'\u0000\u0000\u03b6\u03b7\u0005\u011d\u0000"+
		"\u0000\u03b7\u03c1\u0003\u0084B\u0000\u03b8\u03b9\u0005\u015b\u0000\u0000"+
		"\u03b9\u03ba\u0005\u011d\u0000\u0000\u03ba\u03c1\u0003\u0084B\u0000\u03bb"+
		"\u03bc\u0005\u015c\u0000\u0000\u03bc\u03bd\u0005\u011d\u0000\u0000\u03bd"+
		"\u03be\u0003\u0088D\u0000\u03be\u03bf\u0004A\u0002\u0001\u03bf\u03c1\u0001"+
		"\u0000\u0000\u0000\u03c0\u03af\u0001\u0000\u0000\u0000\u03c0\u03b2\u0001"+
		"\u0000\u0000\u0000\u03c0\u03b5\u0001\u0000\u0000\u0000\u03c0\u03b8\u0001"+
		"\u0000\u0000\u0000\u03c0\u03bb\u0001\u0000\u0000\u0000\u03c1\u0083\u0001"+
		"\u0000\u0000\u0000\u03c2\u03c6\u0003\u00fa}\u0000\u03c3\u03c6\u0003\u0088"+
		"D\u0000\u03c4\u03c6\u0003\u008aE\u0000\u03c5\u03c2\u0001\u0000\u0000\u0000"+
		"\u03c5\u03c3\u0001\u0000\u0000\u0000\u03c5\u03c4\u0001\u0000\u0000\u0000"+
		"\u03c6\u0085\u0001\u0000\u0000\u0000\u03c7\u03c8\u0005\u0150\u0000\u0000"+
		"\u03c8\u0087\u0001\u0000\u0000\u0000\u03c9\u03cc\u0005\u016f\u0000\u0000"+
		"\u03ca\u03cc\u0003\u01d6\u00eb\u0000\u03cb\u03c9\u0001\u0000\u0000\u0000"+
		"\u03cb\u03ca\u0001\u0000\u0000\u0000\u03cc\u0089\u0001\u0000\u0000\u0000"+
		"\u03cd\u03ce\u0007\u0003\u0000\u0000\u03ce\u008b\u0001\u0000\u0000\u0000"+
		"\u03cf\u03d0\u0003\u0094J\u0000\u03d0\u03d1\u0005\u011e\u0000\u0000\u03d1"+
		"\u03d2\u0003\u008eG\u0000\u03d2\u03d3\u0005\u011f\u0000\u0000\u03d3\u008d"+
		"\u0001\u0000\u0000\u0000\u03d4\u03d9\u0003\u0090H\u0000\u03d5\u03d6\u0005"+
		"\u0116\u0000\u0000\u03d6\u03d8\u0003\u0090H\u0000\u03d7\u03d5\u0001\u0000"+
		"\u0000\u0000\u03d8\u03db\u0001\u0000\u0000\u0000\u03d9\u03d7\u0001\u0000"+
		"\u0000\u0000\u03d9\u03da\u0001\u0000\u0000\u0000\u03da\u03dc\u0001\u0000"+
		"\u0000\u0000\u03db\u03d9\u0001\u0000\u0000\u0000\u03dc\u03dd\u0004G\u0003"+
		"\u0001\u03dd\u008f\u0001\u0000\u0000\u0000\u03de\u03df\u0005\u015e\u0000"+
		"\u0000\u03df\u03e0\u0005\u011d\u0000\u0000\u03e0\u03e5\u0003\u0092I\u0000"+
		"\u03e1\u03e2\u0005\u015f\u0000\u0000\u03e2\u03e3\u0005\u011d\u0000\u0000"+
		"\u03e3\u03e5\u0003\u0092I\u0000\u03e4\u03de\u0001\u0000\u0000\u0000\u03e4"+
		"\u03e1\u0001\u0000\u0000\u0000\u03e5\u0091\u0001\u0000\u0000\u0000\u03e6"+
		"\u03e7\u0003\u00fe\u007f\u0000\u03e7\u0093\u0001\u0000\u0000\u0000\u03e8"+
		"\u03e9\u0005\u0153\u0000\u0000\u03e9\u0095\u0001\u0000\u0000\u0000\u03ea"+
		"\u03eb\u0003\u0098L\u0000\u03eb\u03ed\u0005\u011e\u0000\u0000\u03ec\u03ee"+
		"\u0003\u00fa}\u0000\u03ed\u03ec\u0001\u0000\u0000\u0000\u03ed\u03ee\u0001"+
		"\u0000\u0000\u0000\u03ee\u03ef\u0001\u0000\u0000\u0000\u03ef\u03f0\u0005"+
		"\u011f\u0000\u0000\u03f0\u0097\u0001\u0000\u0000\u0000\u03f1\u03f2\u0005"+
		"\u0156\u0000\u0000\u03f2\u0099\u0001\u0000\u0000\u0000\u03f3\u03f4\u0003"+
		"\u00a2Q\u0000\u03f4\u03f5\u0005\u011e\u0000\u0000\u03f5\u03f6\u0003\u009c"+
		"N\u0000\u03f6\u03f7\u0005\u011f\u0000\u0000\u03f7\u009b\u0001\u0000\u0000"+
		"\u0000\u03f8\u03fd\u0003\u009eO\u0000\u03f9\u03fa\u0005\u0116\u0000\u0000"+
		"\u03fa\u03fc\u0003\u009eO\u0000\u03fb\u03f9\u0001\u0000\u0000\u0000\u03fc"+
		"\u03ff\u0001\u0000\u0000\u0000\u03fd\u03fb\u0001\u0000\u0000\u0000\u03fd"+
		"\u03fe\u0001\u0000\u0000\u0000\u03fe\u0400\u0001\u0000\u0000\u0000\u03ff"+
		"\u03fd\u0001\u0000\u0000\u0000\u0400\u0401\u0004N\u0004\u0001\u0401\u009d"+
		"\u0001\u0000\u0000\u0000\u0402\u0403\u0005j\u0000\u0000\u0403\u0404\u0005"+
		"\u011d\u0000\u0000\u0404\u0418\u0003\u00a0P\u0000\u0405\u0406\u0005\u0161"+
		"\u0000\u0000\u0406\u0407\u0005\u011d\u0000\u0000\u0407\u0418\u0003\u00a0"+
		"P\u0000\u0408\u0409\u0005\u0160\u0000\u0000\u0409\u040a\u0005\u011d\u0000"+
		"\u0000\u040a\u0418\u0003\u00a0P\u0000\u040b\u040c\u0005\u0162\u0000\u0000"+
		"\u040c\u040d\u0005\u011d\u0000\u0000\u040d\u0418\u0003\u00a0P\u0000\u040e"+
		"\u040f\u0005\u0163\u0000\u0000\u040f\u0410\u0005\u011d\u0000\u0000\u0410"+
		"\u0418\u0003\u00a0P\u0000\u0411\u0412\u0005\u0164\u0000\u0000\u0412\u0413"+
		"\u0005\u011d\u0000\u0000\u0413\u0418\u0003\u00a0P\u0000\u0414\u0415\u0005"+
		"\u0165\u0000\u0000\u0415\u0416\u0005\u011d\u0000\u0000\u0416\u0418\u0003"+
		"\u00a0P\u0000\u0417\u0402\u0001\u0000\u0000\u0000\u0417\u0405\u0001\u0000"+
		"\u0000\u0000\u0417\u0408\u0001\u0000\u0000\u0000\u0417\u040b\u0001\u0000"+
		"\u0000\u0000\u0417\u040e\u0001\u0000\u0000\u0000\u0417\u0411\u0001\u0000"+
		"\u0000\u0000\u0417\u0414\u0001\u0000\u0000\u0000\u0418\u009f\u0001\u0000"+
		"\u0000\u0000\u0419\u041e\u0003\u0088D\u0000\u041a\u041e\u0003\u00a4R\u0000"+
		"\u041b\u041e\u0003\u00fe\u007f\u0000\u041c\u041e\u0003\u008aE\u0000\u041d"+
		"\u0419\u0001\u0000\u0000\u0000\u041d\u041a\u0001\u0000\u0000\u0000\u041d"+
		"\u041b\u0001\u0000\u0000\u0000\u041d\u041c\u0001\u0000\u0000\u0000\u041e"+
		"\u00a1\u0001\u0000\u0000\u0000\u041f\u0420\u0005\u0154\u0000\u0000\u0420"+
		"\u00a3\u0001\u0000\u0000\u0000\u0421\u0422\u0005\u011e\u0000\u0000\u0422"+
		"\u0427\u0005\u016f\u0000\u0000\u0423\u0424\u0005\u0116\u0000\u0000\u0424"+
		"\u0426\u0005\u016f\u0000\u0000\u0425\u0423\u0001\u0000\u0000\u0000\u0426"+
		"\u0429\u0001\u0000\u0000\u0000\u0427\u0425\u0001\u0000\u0000\u0000\u0427"+
		"\u0428\u0001\u0000\u0000\u0000\u0428\u042a\u0001\u0000\u0000\u0000\u0429"+
		"\u0427\u0001\u0000\u0000\u0000\u042a\u042b\u0005\u011f\u0000\u0000\u042b"+
		"\u00a5\u0001\u0000\u0000\u0000\u042c\u042d\u0003\u00a8T\u0000\u042d\u042e"+
		"\u0005\u011e\u0000\u0000\u042e\u042f\u0003j5\u0000\u042f\u0430\u0005\u0116"+
		"\u0000\u0000\u0430\u0431\u0005\u0166\u0000\u0000\u0431\u0432\u0005\u011d"+
		"\u0000\u0000\u0432\u0433\u0003\u0088D\u0000\u0433\u0434\u0005\u011f\u0000"+
		"\u0000\u0434\u00a7\u0001\u0000\u0000\u0000\u0435\u0436\u0005\u0155\u0000"+
		"\u0000\u0436\u00a9\u0001\u0000\u0000\u0000\u0437\u0438\u0003\u00acV\u0000"+
		"\u0438\u043a\u0005\u011e\u0000\u0000\u0439\u043b\u0003\u00aeW\u0000\u043a"+
		"\u0439\u0001\u0000\u0000\u0000\u043a\u043b\u0001\u0000\u0000\u0000\u043b"+
		"\u043c\u0001\u0000\u0000\u0000\u043c\u043d\u0005\u011f\u0000\u0000\u043d"+
		"\u00ab\u0001\u0000\u0000\u0000\u043e\u0443\u0005\u0151\u0000\u0000\u043f"+
		"\u0443\u0005\u0152\u0000\u0000\u0440\u0443\u0005\u0157\u0000\u0000\u0441"+
		"\u0443\u0003\u01b4\u00da\u0000\u0442\u043e\u0001\u0000\u0000\u0000\u0442"+
		"\u043f\u0001\u0000\u0000\u0000\u0442\u0440\u0001\u0000\u0000\u0000\u0442"+
		"\u0441\u0001\u0000\u0000\u0000\u0443\u00ad\u0001\u0000\u0000\u0000\u0444"+
		"\u0449\u0003\u00fa}\u0000\u0445\u0446\u0005\u0116\u0000\u0000\u0446\u0448"+
		"\u0003\u00fa}\u0000\u0447\u0445\u0001\u0000\u0000\u0000\u0448\u044b\u0001"+
		"\u0000\u0000\u0000\u0449\u0447\u0001\u0000\u0000\u0000\u0449\u044a\u0001"+
		"\u0000\u0000\u0000\u044a\u00af\u0001\u0000\u0000\u0000\u044b\u0449\u0001"+
		"\u0000\u0000\u0000\u044c\u0451\u0003\u00b2Y\u0000\u044d\u044e\u0005\u0116"+
		"\u0000\u0000\u044e\u0450\u0003\u00b2Y\u0000\u044f\u044d\u0001\u0000\u0000"+
		"\u0000\u0450\u0453\u0001\u0000\u0000\u0000\u0451\u044f\u0001\u0000\u0000"+
		"\u0000\u0451\u0452\u0001\u0000\u0000\u0000\u0452\u00b1\u0001\u0000\u0000"+
		"\u0000\u0453\u0451\u0001\u0000\u0000\u0000\u0454\u0455\u0003\u01b4\u00da"+
		"\u0000\u0455\u0456\u0005\u0125\u0000\u0000\u0456\u0458\u0001\u0000\u0000"+
		"\u0000\u0457\u0454\u0001\u0000\u0000\u0000\u0457\u0458\u0001\u0000\u0000"+
		"\u0000\u0458\u0459\u0001\u0000\u0000\u0000\u0459\u045e\u0003\u01b4\u00da"+
		"\u0000\u045a\u045b\u0005\u0114\u0000\u0000\u045b\u045d\u0003\u01b4\u00da"+
		"\u0000\u045c\u045a\u0001\u0000\u0000\u0000\u045d\u0460\u0001\u0000\u0000"+
		"\u0000\u045e\u045c\u0001\u0000\u0000\u0000\u045e\u045f\u0001\u0000\u0000"+
		"\u0000\u045f\u0466\u0001\u0000\u0000\u0000\u0460\u045e\u0001\u0000\u0000"+
		"\u0000\u0461\u0462\u0003\u01b4\u00da\u0000\u0462\u0463\u0005\u0125\u0000"+
		"\u0000\u0463\u0464\u0003\u01b8\u00dc\u0000\u0464\u0466\u0001\u0000\u0000"+
		"\u0000\u0465\u0457\u0001\u0000\u0000\u0000\u0465\u0461\u0001\u0000\u0000"+
		"\u0000\u0466\u00b3\u0001\u0000\u0000\u0000\u0467\u0468\u0003\u01b4\u00da"+
		"\u0000\u0468\u0469\u0005\u0125\u0000\u0000\u0469\u046b\u0001\u0000\u0000"+
		"\u0000\u046a\u0467\u0001\u0000\u0000\u0000\u046a\u046b\u0001\u0000\u0000"+
		"\u0000\u046b\u046c\u0001\u0000\u0000\u0000\u046c\u0471\u0003\u01b4\u00da"+
		"\u0000\u046d\u046e\u0005\u0114\u0000\u0000\u046e\u0470\u0003\u01b4\u00da"+
		"\u0000\u046f\u046d\u0001\u0000\u0000\u0000\u0470\u0473\u0001\u0000\u0000"+
		"\u0000\u0471\u046f\u0001\u0000\u0000\u0000\u0471\u0472\u0001\u0000\u0000"+
		"\u0000\u0472\u047a\u0001\u0000\u0000\u0000\u0473\u0471\u0001\u0000\u0000"+
		"\u0000\u0474\u0475\u0003\u01b4\u00da\u0000\u0475\u0476\u0005\u0125\u0000"+
		"\u0000\u0476\u0477\u0003\u01b8\u00dc\u0000\u0477\u047a\u0001\u0000\u0000"+
		"\u0000\u0478\u047a\u0003\u01b8\u00dc\u0000\u0479\u046a\u0001\u0000\u0000"+
		"\u0000\u0479\u0474\u0001\u0000\u0000\u0000\u0479\u0478\u0001\u0000\u0000"+
		"\u0000\u047a\u00b5\u0001\u0000\u0000\u0000\u047b\u0486\u0003\u00b8\\\u0000"+
		"\u047c\u0486\u0003\u0110\u0088\u0000\u047d\u047e\u0003\u0106\u0083\u0000"+
		"\u047e\u047f\u0003\u0104\u0082\u0000\u047f\u0486\u0001\u0000\u0000\u0000"+
		"\u0480\u0486\u0003\u0114\u008a\u0000\u0481\u0486\u0003\u00d4j\u0000\u0482"+
		"\u0486\u0003\u01b8\u00dc\u0000\u0483\u0486\u0003\u0200\u0100\u0000\u0484"+
		"\u0486\u0003\u011c\u008e\u0000\u0485\u047b\u0001\u0000\u0000\u0000\u0485"+
		"\u047c\u0001\u0000\u0000\u0000\u0485\u047d\u0001\u0000\u0000\u0000\u0485"+
		"\u0480\u0001\u0000\u0000\u0000\u0485\u0481\u0001\u0000\u0000\u0000\u0485"+
		"\u0482\u0001\u0000\u0000\u0000\u0485\u0483\u0001\u0000\u0000\u0000\u0485"+
		"\u0484\u0001\u0000\u0000\u0000\u0486\u00b7\u0001\u0000\u0000\u0000\u0487"+
		"\u048a\u0003\u00ba]\u0000\u0488\u0489\u0005\u012a\u0000\u0000\u0489\u048b"+
		"\u0003\u01ee\u00f7\u0000\u048a\u0488\u0001\u0000\u0000\u0000\u048a\u048b"+
		"\u0001\u0000\u0000\u0000\u048b\u0492\u0001\u0000\u0000\u0000\u048c\u048f"+
		"\u0003\u00bc^\u0000\u048d\u048e\u0005\u012a\u0000\u0000\u048e\u0490\u0003"+
		"\u01ee\u00f7\u0000\u048f\u048d\u0001\u0000\u0000\u0000\u048f\u0490\u0001"+
		"\u0000\u0000\u0000\u0490\u0492\u0001\u0000\u0000\u0000\u0491\u0487\u0001"+
		"\u0000\u0000\u0000\u0491\u048c\u0001\u0000\u0000\u0000\u0492\u00b9\u0001"+
		"\u0000\u0000\u0000\u0493\u0494\u0005\u011e\u0000\u0000\u0494\u0495\u0003"+
		"\u00fa}\u0000\u0495\u0496\u0005\u011f\u0000\u0000\u0496\u00bb\u0001\u0000"+
		"\u0000\u0000\u0497\u04a1\u0003\u01d8\u00ec\u0000\u0498\u04a1\u0003\u00b2"+
		"Y\u0000\u0499\u04a1\u0003\u00c0`\u0000\u049a\u04a1\u0003\u00c6c\u0000"+
		"\u049b\u04a1\u0003\u00d6k\u0000\u049c\u04a1\u0003\u01ac\u00d6\u0000\u049d"+
		"\u04a1\u0003\u011c\u008e\u0000\u049e\u04a1\u0003\u00dam\u0000\u049f\u04a1"+
		"\u0003\u00be_\u0000\u04a0\u0497\u0001\u0000\u0000\u0000\u04a0\u0498\u0001"+
		"\u0000\u0000\u0000\u04a0\u0499\u0001\u0000\u0000\u0000\u04a0\u049a\u0001"+
		"\u0000\u0000\u0000\u04a0\u049b\u0001\u0000\u0000\u0000\u04a0\u049c\u0001"+
		"\u0000\u0000\u0000\u04a0\u049d\u0001\u0000\u0000\u0000\u04a0\u049e\u0001"+
		"\u0000\u0000\u0000\u04a0\u049f\u0001\u0000\u0000\u0000\u04a1\u00bd\u0001"+
		"\u0000\u0000\u0000\u04a2\u04a3\u0003H$\u0000\u04a3\u00bf\u0001\u0000\u0000"+
		"\u0000\u04a4\u04a5\u0005E\u0000\u0000\u04a5\u04a6\u0005\u011e\u0000\u0000"+
		"\u04a6\u04a7\u0003X,\u0000\u04a7\u04a8\u0005\u011f\u0000\u0000\u04a8\u04b5"+
		"\u0001\u0000\u0000\u0000\u04a9\u04ac\u0003\u00c2a\u0000\u04aa\u04ac\u0003"+
		"\u00c4b\u0000\u04ab\u04a9\u0001\u0000\u0000\u0000\u04ab\u04aa\u0001\u0000"+
		"\u0000\u0000\u04ac\u04ad\u0001\u0000\u0000\u0000\u04ad\u04af\u0005\u011e"+
		"\u0000\u0000\u04ae\u04b0\u0003N\'\u0000\u04af\u04ae\u0001\u0000\u0000"+
		"\u0000\u04af\u04b0\u0001\u0000\u0000\u0000\u04b0\u04b1\u0001\u0000\u0000"+
		"\u0000\u04b1\u04b2\u0003\u00fa}\u0000\u04b2\u04b3\u0005\u011f\u0000\u0000"+
		"\u04b3\u04b5\u0001\u0000\u0000\u0000\u04b4\u04a4\u0001\u0000\u0000\u0000"+
		"\u04b4\u04ab\u0001\u0000\u0000\u0000\u04b5\u00c1\u0001\u0000\u0000\u0000"+
		"\u04b6\u04b7\u0007\u0004\u0000\u0000\u04b7\u00c3\u0001\u0000\u0000\u0000"+
		"\u04b8\u04b9\u0007\u0005\u0000\u0000\u04b9\u00c5\u0001\u0000\u0000\u0000"+
		"\u04ba\u04bb\u0005\u0007\u0000\u0000\u04bb\u04bc\u0003\u00fa}\u0000\u04bc"+
		"\u04be\u0003\u00ccf\u0000\u04bd\u04bf\u0003\u00d0h\u0000\u04be\u04bd\u0001"+
		"\u0000\u0000\u0000\u04be\u04bf\u0001\u0000\u0000\u0000\u04bf\u04c0\u0001"+
		"\u0000\u0000\u0000\u04c0\u04c1\u0005\f\u0000\u0000\u04c1\u04ca\u0001\u0000"+
		"\u0000\u0000\u04c2\u04c3\u0005\u0007\u0000\u0000\u04c3\u04c5\u0003\u00c8"+
		"d\u0000\u04c4\u04c6\u0003\u00d0h\u0000\u04c5\u04c4\u0001\u0000\u0000\u0000"+
		"\u04c5\u04c6\u0001\u0000\u0000\u0000\u04c6\u04c7\u0001\u0000\u0000\u0000"+
		"\u04c7\u04c8\u0005\f\u0000\u0000\u04c8\u04ca\u0001\u0000\u0000\u0000\u04c9"+
		"\u04ba\u0001\u0000\u0000\u0000\u04c9\u04c2\u0001\u0000\u0000\u0000\u04ca"+
		"\u00c7\u0001\u0000\u0000\u0000\u04cb\u04cd\u0003\u00cae\u0000\u04cc\u04cb"+
		"\u0001\u0000\u0000\u0000\u04cd\u04ce\u0001\u0000\u0000\u0000\u04ce\u04cc"+
		"\u0001\u0000\u0000\u0000\u04ce\u04cf\u0001\u0000\u0000\u0000\u04cf\u00c9"+
		"\u0001\u0000\u0000\u0000\u04d0\u04d1\u00058\u0000\u0000\u04d1\u04d2\u0003"+
		"\u00fa}\u0000\u04d2\u04d3\u00051\u0000\u0000\u04d3\u04d4\u0003\u00d2i"+
		"\u0000\u04d4\u00cb\u0001\u0000\u0000\u0000\u04d5\u04d7\u0003\u00ceg\u0000"+
		"\u04d6\u04d5\u0001\u0000\u0000\u0000\u04d7\u04d8\u0001\u0000\u0000\u0000"+
		"\u04d8\u04d6\u0001\u0000\u0000\u0000\u04d8\u04d9\u0001\u0000\u0000\u0000"+
		"\u04d9\u00cd\u0001\u0000\u0000\u0000\u04da\u04db\u00058\u0000\u0000\u04db"+
		"\u04dc\u0003\u00fa}\u0000\u04dc\u04dd\u00051\u0000\u0000\u04dd\u04de\u0003"+
		"\u00d2i\u0000\u04de\u00cf\u0001\u0000\u0000\u0000\u04df\u04e0\u0005\r"+
		"\u0000\u0000\u04e0\u04e1\u0003\u00d2i\u0000\u04e1\u00d1\u0001\u0000\u0000"+
		"\u0000\u04e2\u04e5\u0003\u00fa}\u0000\u04e3\u04e5\u0003\u00d4j\u0000\u04e4"+
		"\u04e2\u0001\u0000\u0000\u0000\u04e4\u04e3\u0001\u0000\u0000\u0000\u04e5"+
		"\u00d3\u0001\u0000\u0000\u0000\u04e6\u04e7\u0005\"\u0000\u0000\u04e7\u00d5"+
		"\u0001\u0000\u0000\u0000\u04e8\u04e9\u0003\u00d8l\u0000\u04e9\u04ea\u0005"+
		"\u011e\u0000\u0000\u04ea\u04eb\u0003\u00fa}\u0000\u04eb\u04ec\u0005\u0001"+
		"\u0000\u0000\u04ec\u04ed\u0003\u01ee\u00f7\u0000\u04ed\u04ee\u0005\u011f"+
		"\u0000\u0000\u04ee\u00d7\u0001\u0000\u0000\u0000\u04ef\u04f0\u0007\u0006"+
		"\u0000\u0000\u04f0\u00d9\u0001\u0000\u0000\u0000\u04f1\u04f2\u0003\u00dc"+
		"n\u0000\u04f2\u04f3\u0003\u00deo\u0000\u04f3\u00db\u0001\u0000\u0000\u0000"+
		"\u04f4\u04f5\u0003\u00c2a\u0000\u04f5\u04f7\u0005\u011e\u0000\u0000\u04f6"+
		"\u04f8\u0003\u01b2\u00d9\u0000\u04f7\u04f6\u0001\u0000\u0000\u0000\u04f7"+
		"\u04f8\u0001\u0000\u0000\u0000\u04f8\u04f9\u0001\u0000\u0000\u0000\u04f9"+
		"\u04fa\u0005\u011f\u0000\u0000\u04fa\u0506\u0001\u0000\u0000\u0000\u04fb"+
		"\u04fc\u0003\u00f4z\u0000\u04fc\u04fd\u0005\u011e\u0000\u0000\u04fd\u04fe"+
		"\u0003\u01b2\u00d9\u0000\u04fe\u0503\u0005\u011f\u0000\u0000\u04ff\u0501"+
		"\u0003\u00f6{\u0000\u0500\u04ff\u0001\u0000\u0000\u0000\u0500\u0501\u0001"+
		"\u0000\u0000\u0000\u0501\u0502\u0001\u0000\u0000\u0000\u0502\u0504\u0003"+
		"\u00f8|\u0000\u0503\u0500\u0001\u0000\u0000\u0000\u0503\u0504\u0001\u0000"+
		"\u0000\u0000\u0504\u0506\u0001\u0000\u0000\u0000\u0505\u04f4\u0001\u0000"+
		"\u0000\u0000\u0505\u04fb\u0001\u0000\u0000\u0000\u0506\u00dd\u0001\u0000"+
		"\u0000\u0000\u0507\u0508\u0005v\u0000\u0000\u0508\u050a\u0005\u011e\u0000"+
		"\u0000\u0509\u050b\u0003\u00e0p\u0000\u050a\u0509\u0001\u0000\u0000\u0000"+
		"\u050a\u050b\u0001\u0000\u0000\u0000\u050b\u050d\u0001\u0000\u0000\u0000"+
		"\u050c\u050e\u0003\u013c\u009e\u0000\u050d\u050c\u0001\u0000\u0000\u0000"+
		"\u050d\u050e\u0001\u0000\u0000\u0000\u050e\u0510\u0001\u0000\u0000\u0000"+
		"\u050f\u0511\u0003\u00e2q\u0000\u0510\u050f\u0001\u0000\u0000\u0000\u0510"+
		"\u0511\u0001\u0000\u0000\u0000\u0511\u0512\u0001\u0000\u0000\u0000\u0512"+
		"\u0513\u0005\u011f\u0000\u0000\u0513\u00df\u0001\u0000\u0000\u0000\u0514"+
		"\u0515\u0005x\u0000\u0000\u0515\u0516\u0005?\u0000\u0000\u0516\u0517\u0003"+
		"\u01b2\u00d9\u0000\u0517\u00e1\u0001\u0000\u0000\u0000\u0518\u0519\u0003"+
		"\u00e4r\u0000\u0519\u051a\u0003\u00e6s\u0000\u051a\u00e3\u0001\u0000\u0000"+
		"\u0000\u051b\u051c\u0007\u0007\u0000\u0000\u051c\u00e5\u0001\u0000\u0000"+
		"\u0000\u051d\u0521\u0003\u00e8t\u0000\u051e\u0521\u0003\u00ecv\u0000\u051f"+
		"\u0521\u0003\u00f0x\u0000\u0520\u051d\u0001\u0000\u0000\u0000\u0520\u051e"+
		"\u0001\u0000\u0000\u0000\u0520\u051f\u0001\u0000\u0000\u0000\u0521\u00e7"+
		"\u0001\u0000\u0000\u0000\u0522\u0523\u0005>\u0000\u0000\u0523\u0524\u0003"+
		"\u00eau\u0000\u0524\u0525\u0005\u0003\u0000\u0000\u0525\u0526\u0003\u00ea"+
		"u\u0000\u0526\u00e9\u0001\u0000\u0000\u0000\u0527\u052b\u0003\u00ecv\u0000"+
		"\u0528\u052b\u0003\u00eew\u0000\u0529\u052b\u0003\u00f0x\u0000\u052a\u0527"+
		"\u0001\u0000\u0000\u0000\u052a\u0528\u0001\u0000\u0000\u0000\u052a\u0529"+
		"\u0001\u0000\u0000\u0000\u052b\u00eb\u0001\u0000\u0000\u0000\u052c\u052d"+
		"\u0003\u00f2y\u0000\u052d\u052e\u0005z\u0000\u0000\u052e\u00ed\u0001\u0000"+
		"\u0000\u0000\u052f\u0530\u0003\u00f2y\u0000\u0530\u0531\u0005Y\u0000\u0000"+
		"\u0531\u00ef\u0001\u0000\u0000\u0000\u0532\u0533\u0005H\u0000\u0000\u0533"+
		"\u0534\u0005\u0084\u0000\u0000\u0534\u00f1\u0001\u0000\u0000\u0000\u0535"+
		"\u0536\u0007\b\u0000\u0000\u0536\u00f3\u0001\u0000\u0000\u0000\u0537\u0538"+
		"\u0007\t\u0000\u0000\u0538\u00f5\u0001\u0000\u0000\u0000\u0539\u053a\u0005"+
		"\u0011\u0000\u0000\u053a\u053b\u0007\n\u0000\u0000\u053b\u00f7\u0001\u0000"+
		"\u0000\u0000\u053c\u053d\u0007\u000b\u0000\u0000\u053d\u053e\u0005#\u0000"+
		"\u0000\u053e\u00f9\u0001\u0000\u0000\u0000\u053f\u0544\u0003\u00fc~\u0000"+
		"\u0540\u0544\u0003\u0134\u009a\u0000\u0541\u0544\u0003\u01b8\u00dc\u0000"+
		"\u0542\u0544\u0003\u0124\u0092\u0000\u0543\u053f\u0001\u0000\u0000\u0000"+
		"\u0543\u0540\u0001\u0000\u0000\u0000\u0543\u0541\u0001\u0000\u0000\u0000"+
		"\u0543\u0542\u0001\u0000\u0000\u0000\u0544\u00fb\u0001\u0000\u0000\u0000"+
		"\u0545\u0549\u0003\u00fe\u007f\u0000\u0546\u0549\u0003\u0110\u0088\u0000"+
		"\u0547\u0549\u0003\u00d4j\u0000\u0548\u0545\u0001\u0000\u0000\u0000\u0548"+
		"\u0546\u0001\u0000\u0000\u0000\u0548\u0547\u0001\u0000\u0000\u0000\u0549"+
		"\u00fd\u0001\u0000\u0000\u0000\u054a\u054f\u0003\u0100\u0080\u0000\u054b"+
		"\u054c\u0007\f\u0000\u0000\u054c\u054e\u0003\u0100\u0080\u0000\u054d\u054b"+
		"\u0001\u0000\u0000\u0000\u054e\u0551\u0001\u0000\u0000\u0000\u054f\u054d"+
		"\u0001\u0000\u0000\u0000\u054f\u0550\u0001\u0000\u0000\u0000\u0550\u00ff"+
		"\u0001\u0000\u0000\u0000\u0551\u054f\u0001\u0000\u0000\u0000\u0552\u0557"+
		"\u0003\u0102\u0081\u0000\u0553\u0554\u0007\r\u0000\u0000\u0554\u0556\u0003"+
		"\u0102\u0081\u0000\u0555\u0553\u0001\u0000\u0000\u0000\u0556\u0559\u0001"+
		"\u0000\u0000\u0000\u0557\u0555\u0001\u0000\u0000\u0000\u0557\u0558\u0001"+
		"\u0000\u0000\u0000\u0558\u0101\u0001\u0000\u0000\u0000\u0559\u0557\u0001"+
		"\u0000\u0000\u0000\u055a\u055c\u0003\u0106\u0083\u0000\u055b\u055a\u0001"+
		"\u0000\u0000\u0000\u055b\u055c\u0001\u0000\u0000\u0000\u055c\u055d\u0001"+
		"\u0000\u0000\u0000\u055d\u055e\u0003\u0104\u0082\u0000\u055e\u0103\u0001"+
		"\u0000\u0000\u0000\u055f\u0562\u0003\u00b8\\\u0000\u0560\u0562\u0003\u0108"+
		"\u0084\u0000\u0561\u055f\u0001\u0000\u0000\u0000\u0561\u0560\u0001\u0000"+
		"\u0000\u0000\u0562\u0105\u0001\u0000\u0000\u0000\u0563\u0564\u0007\f\u0000"+
		"\u0000\u0564\u0107\u0001\u0000\u0000\u0000\u0565\u0566\u0005U\u0000\u0000"+
		"\u0566\u0567\u0005\u011e\u0000\u0000\u0567\u0568\u0003\u010a\u0085\u0000"+
		"\u0568\u0569\u0005\u0011\u0000\u0000\u0569\u056a\u0003\u010e\u0087\u0000"+
		"\u056a\u056b\u0005\u011f\u0000\u0000\u056b\u0109\u0001\u0000\u0000\u0000"+
		"\u056c\u0570\u0003\u01a6\u00d3\u0000\u056d\u0570\u0003\u010c\u0086\u0000"+
		"\u056e\u0570\u0003\u01aa\u00d5\u0000\u056f\u056c\u0001\u0000\u0000\u0000"+
		"\u056f\u056d\u0001\u0000\u0000\u0000\u056f\u056e\u0001\u0000\u0000\u0000"+
		"\u0570\u010b\u0001\u0000\u0000\u0000\u0571\u0572\u0007\u000e\u0000\u0000"+
		"\u0572\u010d\u0001\u0000\u0000\u0000\u0573\u0576\u0003\u00b2Y\u0000\u0574"+
		"\u0576\u0003\u01e4\u00f2\u0000\u0575\u0573\u0001\u0000\u0000\u0000\u0575"+
		"\u0574\u0001\u0000\u0000\u0000\u0576\u010f\u0001\u0000\u0000\u0000\u0577"+
		"\u057c\u0003\u0112\u0089\u0000\u0578\u0579\u0005\u0117\u0000\u0000\u0579"+
		"\u057b\u0003\u0112\u0089\u0000\u057a\u0578\u0001\u0000\u0000\u0000\u057b"+
		"\u057e\u0001\u0000\u0000\u0000\u057c\u057a\u0001\u0000\u0000\u0000\u057c"+
		"\u057d\u0001\u0000\u0000\u0000\u057d\u0111\u0001\u0000\u0000\u0000\u057e"+
		"\u057c\u0001\u0000\u0000\u0000\u057f\u0582\u0003\u00b8\\\u0000\u0580\u0582"+
		"\u0003\u0114\u008a\u0000\u0581\u057f\u0001\u0000\u0000\u0000\u0581\u0580"+
		"\u0001\u0000\u0000\u0000\u0582\u0113\u0001\u0000\u0000\u0000\u0583\u0584"+
		"\u0003\u0116\u008b\u0000\u0584\u0585\u0005\u011e\u0000\u0000\u0585\u0586"+
		"\u0003\u0118\u008c\u0000\u0586\u0587\u0005\u011f\u0000\u0000\u0587\u0115"+
		"\u0001\u0000\u0000\u0000\u0588\u0589\u0005\u0092\u0000\u0000\u0589\u0117"+
		"\u0001\u0000\u0000\u0000\u058a\u058c\u0003\u011a\u008d\u0000\u058b\u058a"+
		"\u0001\u0000\u0000\u0000\u058b\u058c\u0001\u0000\u0000\u0000\u058c\u058e"+
		"\u0001\u0000\u0000\u0000\u058d\u058f\u0003\u0110\u0088\u0000\u058e\u058d"+
		"\u0001\u0000\u0000\u0000\u058e\u058f\u0001\u0000\u0000\u0000\u058f\u0590"+
		"\u0001\u0000\u0000\u0000\u0590\u0592\u0005\u0011\u0000\u0000\u0591\u058b"+
		"\u0001\u0000\u0000\u0000\u0591\u0592\u0001\u0000\u0000\u0000\u0592\u0593"+
		"\u0001\u0000\u0000\u0000\u0593\u0599\u0003\u00fa}\u0000\u0594\u0595\u0003"+
		"\u00fa}\u0000\u0595\u0596\u0005\u0116\u0000\u0000\u0596\u0597\u0003\u0110"+
		"\u0088\u0000\u0597\u0599\u0001\u0000\u0000\u0000\u0598\u0591\u0001\u0000"+
		"\u0000\u0000\u0598\u0594\u0001\u0000\u0000\u0000\u0599\u0119\u0001\u0000"+
		"\u0000\u0000\u059a\u059b\u0007\u000f\u0000\u0000\u059b\u011b\u0001\u0000"+
		"\u0000\u0000\u059c\u059d\u0003\u011e\u008f\u0000\u059d\u059e\u0005\u011e"+
		"\u0000\u0000\u059e\u059f\u0003\u0110\u0088\u0000\u059f\u05a0\u0005\u0016"+
		"\u0000\u0000\u05a0\u05a1\u0003\u0110\u0088\u0000\u05a1\u05a2\u0005\u011f"+
		"\u0000\u0000\u05a2\u05b3\u0001\u0000\u0000\u0000\u05a3\u05a7\u0003\u011e"+
		"\u008f\u0000\u05a4\u05a7\u0003\u0120\u0090\u0000\u05a5\u05a7\u0003\u0122"+
		"\u0091\u0000\u05a6\u05a3\u0001\u0000\u0000\u0000\u05a6\u05a4\u0001\u0000"+
		"\u0000\u0000\u05a6\u05a5\u0001\u0000\u0000\u0000\u05a7\u05a8\u0001\u0000"+
		"\u0000\u0000\u05a8\u05a9\u0005\u011e\u0000\u0000\u05a9\u05aa\u0003\u0110"+
		"\u0088\u0000\u05aa\u05ab\u0005\u0116\u0000\u0000\u05ab\u05ae\u0003\u0110"+
		"\u0088\u0000\u05ac\u05ad\u0005\u0116\u0000\u0000\u05ad\u05af\u0003\u0104"+
		"\u0082\u0000\u05ae\u05ac\u0001\u0000\u0000\u0000\u05ae\u05af\u0001\u0000"+
		"\u0000\u0000\u05af\u05b0\u0001\u0000\u0000\u0000\u05b0\u05b1\u0005\u011f"+
		"\u0000\u0000\u05b1\u05b3\u0001\u0000\u0000\u0000\u05b2\u059c\u0001\u0000"+
		"\u0000\u0000\u05b2\u05a6\u0001\u0000\u0000\u0000\u05b3\u011d\u0001\u0000"+
		"\u0000\u0000\u05b4\u05b5\u0005\u014a\u0000\u0000\u05b5\u011f\u0001\u0000"+
		"\u0000\u0000\u05b6\u05b7\u0005\u014c\u0000\u0000\u05b7\u0121\u0001\u0000"+
		"\u0000\u0000\u05b8\u05b9\u0005\u014b\u0000\u0000\u05b9\u0123\u0001\u0000"+
		"\u0000\u0000\u05ba\u05bb\u0003\u0126\u0093\u0000\u05bb\u0125\u0001\u0000"+
		"\u0000\u0000\u05bc\u05c1\u0003\u0128\u0094\u0000\u05bd\u05be\u0005(\u0000"+
		"\u0000\u05be\u05c0\u0003\u0128\u0094\u0000\u05bf\u05bd\u0001\u0000\u0000"+
		"\u0000\u05c0\u05c3\u0001\u0000\u0000\u0000\u05c1\u05bf\u0001\u0000\u0000"+
		"\u0000\u05c1\u05c2\u0001\u0000\u0000\u0000\u05c2\u0127\u0001\u0000\u0000"+
		"\u0000\u05c3\u05c1\u0001\u0000\u0000\u0000\u05c4\u05c9\u0003\u012a\u0095"+
		"\u0000\u05c5\u05c6\u0005\u0003\u0000\u0000\u05c6\u05c8\u0003\u012a\u0095"+
		"\u0000\u05c7\u05c5\u0001\u0000\u0000\u0000\u05c8\u05cb\u0001\u0000\u0000"+
		"\u0000\u05c9\u05c7\u0001\u0000\u0000\u0000\u05c9\u05ca\u0001\u0000\u0000"+
		"\u0000\u05ca\u0129\u0001\u0000\u0000\u0000\u05cb\u05c9\u0001\u0000\u0000"+
		"\u0000\u05cc\u05ce\u0003\u019a\u00cd\u0000\u05cd\u05cc\u0001\u0000\u0000"+
		"\u0000\u05cd\u05ce\u0001\u0000\u0000\u0000\u05ce\u05cf\u0001\u0000\u0000"+
		"\u0000\u05cf\u05d0\u0003\u012c\u0096\u0000\u05d0\u012b\u0001\u0000\u0000"+
		"\u0000\u05d1\u05d2\u0005\u011e\u0000\u0000\u05d2\u05d3\u0003\u0124\u0092"+
		"\u0000\u05d3\u05d4\u0005\u011f\u0000\u0000\u05d4\u05da\u0001\u0000\u0000"+
		"\u0000\u05d5\u05d7\u0003\u012e\u0097\u0000\u05d6\u05d8\u0003\u0196\u00cb"+
		"\u0000\u05d7\u05d6\u0001\u0000\u0000\u0000\u05d7\u05d8\u0001\u0000\u0000"+
		"\u0000\u05d8\u05da\u0001\u0000\u0000\u0000\u05d9\u05d1\u0001\u0000\u0000"+
		"\u0000\u05d9\u05d5\u0001\u0000\u0000\u0000\u05da\u012d\u0001\u0000\u0000"+
		"\u0000\u05db\u05de\u0003\u0130\u0098\u0000\u05dc\u05de\u0003\u00bc^\u0000"+
		"\u05dd\u05db\u0001\u0000\u0000\u0000\u05dd\u05dc\u0001\u0000\u0000\u0000"+
		"\u05de\u012f\u0001\u0000\u0000\u0000\u05df\u05e7\u0003\u0160\u00b0\u0000"+
		"\u05e0\u05e7\u0003\u016a\u00b5\u0000\u05e1\u05e7\u0003\u016e\u00b7\u0000"+
		"\u05e2\u05e7\u0003\u0170\u00b8\u0000\u05e3\u05e7\u0003\u0192\u00c9\u0000"+
		"\u05e4\u05e7\u0003\u018c\u00c6\u0000\u05e5\u05e7\u0003\u0132\u0099\u0000"+
		"\u05e6\u05df\u0001\u0000\u0000\u0000\u05e6\u05e0\u0001\u0000\u0000\u0000"+
		"\u05e6\u05e1\u0001\u0000\u0000\u0000\u05e6\u05e2\u0001\u0000\u0000\u0000"+
		"\u05e6\u05e3\u0001\u0000\u0000\u0000\u05e6\u05e4\u0001\u0000\u0000\u0000"+
		"\u05e6\u05e5\u0001\u0000\u0000\u0000\u05e7\u0131\u0001\u0000\u0000\u0000"+
		"\u05e8\u05e9\u0003\u01b8\u00dc\u0000\u05e9\u0133\u0001\u0000\u0000\u0000"+
		"\u05ea\u05ed\u0003\u00bc^\u0000\u05eb\u05ed\u0003\u00d4j\u0000\u05ec\u05ea"+
		"\u0001\u0000\u0000\u0000\u05ec\u05eb\u0001\u0000\u0000\u0000\u05ed\u0135"+
		"\u0001\u0000\u0000\u0000\u05ee\u05f2\u0003\u00bc^\u0000\u05ef\u05f2\u0003"+
		"\u00fc~\u0000\u05f0\u05f2\u0003\u01b8\u00dc\u0000\u05f1\u05ee\u0001\u0000"+
		"\u0000\u0000\u05f1\u05ef\u0001\u0000\u0000\u0000\u05f1\u05f0\u0001\u0000"+
		"\u0000\u0000\u05f2\u0137\u0001\u0000\u0000\u0000\u05f3\u05f4\u00059\u0000"+
		"\u0000\u05f4\u05f5\u0003\u013a\u009d\u0000\u05f5\u0139\u0001\u0000\u0000"+
		"\u0000\u05f6\u05f7\u0003\u00fa}\u0000\u05f7\u013b\u0001\u0000\u0000\u0000"+
		"\u05f8\u05f9\u0005)\u0000\u0000\u05f9\u05fa\u0005?\u0000\u0000\u05fa\u05fb"+
		"\u0003\u013e\u009f\u0000\u05fb\u013d\u0001\u0000\u0000\u0000\u05fc\u0601"+
		"\u0003\u0140\u00a0\u0000\u05fd\u05fe\u0005\u0116\u0000\u0000\u05fe\u0600"+
		"\u0003\u0140\u00a0\u0000\u05ff\u05fd\u0001\u0000\u0000\u0000\u0600\u0603"+
		"\u0001\u0000\u0000\u0000\u0601\u05ff\u0001\u0000\u0000\u0000\u0601\u0602"+
		"\u0001\u0000\u0000\u0000\u0602\u013f\u0001\u0000\u0000\u0000\u0603\u0601"+
		"\u0001\u0000\u0000\u0000\u0604\u0606\u0003\u0136\u009b\u0000\u0605\u0607"+
		"\u0003\u0142\u00a1\u0000\u0606\u0605\u0001\u0000\u0000\u0000\u0606\u0607"+
		"\u0001\u0000\u0000\u0000\u0607\u0609\u0001\u0000\u0000\u0000\u0608\u060a"+
		"\u0003\u0144\u00a2\u0000\u0609\u0608\u0001\u0000\u0000\u0000\u0609\u060a"+
		"\u0001\u0000\u0000\u0000\u060a\u0141\u0001\u0000\u0000\u0000\u060b\u060c"+
		"\u0007\u0010\u0000\u0000\u060c\u0143\u0001\u0000\u0000\u0000\u060d\u060e"+
		"\u0005#\u0000\u0000\u060e\u060f\u0003\u0146\u00a3\u0000\u060f\u0145\u0001"+
		"\u0000\u0000\u0000\u0610\u0611\u0007\n\u0000\u0000\u0611\u0147\u0001\u0000"+
		"\u0000\u0000\u0612\u0613\u0005\u001f\u0000\u0000\u0613\u0616\u0003\u00fe"+
		"\u007f\u0000\u0614\u0615\u0005%\u0000\u0000\u0615\u0617\u0003\u00fe\u007f"+
		"\u0000\u0616\u0614\u0001\u0000\u0000\u0000\u0616\u0617\u0001\u0000\u0000"+
		"\u0000\u0617\u0149\u0001\u0000\u0000\u0000\u0618\u0619\u0005\u0012\u0000"+
		"\u0000\u0619\u061c\u0005?\u0000\u0000\u061a\u061d\u0003\u014c\u00a6\u0000"+
		"\u061b\u061d\u0003P(\u0000\u061c\u061a\u0001\u0000\u0000\u0000\u061c\u061b"+
		"\u0001\u0000\u0000\u0000\u061d\u014b\u0001\u0000\u0000\u0000\u061e\u0623"+
		"\u0003\u014e\u00a7\u0000\u061f\u0620\u0005\u0116\u0000\u0000\u0620\u0622"+
		"\u0003\u014e\u00a7\u0000\u0621\u061f\u0001\u0000\u0000\u0000\u0622\u0625"+
		"\u0001\u0000\u0000\u0000\u0623\u0621\u0001\u0000\u0000\u0000\u0623\u0624"+
		"\u0001\u0000\u0000\u0000\u0624\u014d\u0001\u0000\u0000\u0000\u0625\u0623"+
		"\u0001\u0000\u0000\u0000\u0626\u062b\u0003\u0154\u00aa\u0000\u0627\u062b"+
		"\u0003\u0156\u00ab\u0000\u0628\u062b\u0003\u0158\u00ac\u0000\u0629\u062b"+
		"\u0003\u0152\u00a9\u0000\u062a\u0626\u0001\u0000\u0000\u0000\u062a\u0627"+
		"\u0001\u0000\u0000\u0000\u062a\u0628\u0001\u0000\u0000\u0000\u062a\u0629"+
		"\u0001\u0000\u0000\u0000\u062b\u014f\u0001\u0000\u0000\u0000\u062c\u0631"+
		"\u0003\u0152\u00a9\u0000\u062d\u062e\u0005\u0116\u0000\u0000\u062e\u0630"+
		"\u0003\u0152\u00a9\u0000\u062f\u062d\u0001\u0000\u0000\u0000\u0630\u0633"+
		"\u0001\u0000\u0000\u0000\u0631\u062f\u0001\u0000\u0000\u0000\u0631\u0632"+
		"\u0001\u0000\u0000\u0000\u0632\u0151\u0001\u0000\u0000\u0000\u0633\u0631"+
		"\u0001\u0000\u0000\u0000\u0634\u063a\u0003\u0136\u009b\u0000\u0635\u0636"+
		"\u0005\u011e\u0000\u0000\u0636\u0637\u0003\u015e\u00af\u0000\u0637\u0638"+
		"\u0005\u011f\u0000\u0000\u0638\u063a\u0001\u0000\u0000\u0000\u0639\u0634"+
		"\u0001\u0000\u0000\u0000\u0639\u0635\u0001\u0000\u0000\u0000\u063a\u0153"+
		"\u0001\u0000\u0000\u0000\u063b\u063c\u0005\u0082\u0000\u0000\u063c\u063d"+
		"\u0005\u011e\u0000\u0000\u063d\u063e\u0003\u0150\u00a8\u0000\u063e\u063f"+
		"\u0005\u011f\u0000\u0000\u063f\u0155\u0001\u0000\u0000\u0000\u0640\u0641"+
		"\u0005G\u0000\u0000\u0641\u0642\u0005\u011e\u0000\u0000\u0642\u0643\u0003"+
		"\u0150\u00a8\u0000\u0643\u0644\u0005\u011f\u0000\u0000\u0644\u0157\u0001"+
		"\u0000\u0000\u0000\u0645\u0646\u0005\u011e\u0000\u0000\u0646\u0647\u0005"+
		"\u011f\u0000\u0000\u0647\u0159\u0001\u0000\u0000\u0000\u0648\u0649\u0005"+
		"\u0013\u0000\u0000\u0649\u064a\u0003\u0124\u0092\u0000\u064a\u015b\u0001"+
		"\u0000\u0000\u0000\u064b\u064c\u0005\u0149\u0000\u0000\u064c\u064d\u0003"+
		"\u013a\u009d\u0000\u064d\u015d\u0001\u0000\u0000\u0000\u064e\u0653\u0003"+
		"\u0136\u009b\u0000\u064f\u0650\u0005\u0116\u0000\u0000\u0650\u0652\u0003"+
		"\u0136\u009b\u0000\u0651\u064f\u0001\u0000\u0000\u0000\u0652\u0655\u0001"+
		"\u0000\u0000\u0000\u0653\u0651\u0001\u0000\u0000\u0000\u0653\u0654\u0001"+
		"\u0000\u0000\u0000\u0654\u015f\u0001\u0000\u0000\u0000\u0655\u0653\u0001"+
		"\u0000\u0000\u0000\u0656\u0657\u0003\u0136\u009b\u0000\u0657\u0658\u0003"+
		"\u0162\u00b1\u0000\u0658\u0659\u0003\u0136\u009b\u0000\u0659\u0161\u0001"+
		"\u0000\u0000\u0000\u065a\u0661\u0003\u0168\u00b4\u0000\u065b\u065d\u0003"+
		"\u019a\u00cd\u0000\u065c\u065b\u0001\u0000\u0000\u0000\u065c\u065d\u0001"+
		"\u0000\u0000\u0000\u065d\u065e\u0001\u0000\u0000\u0000\u065e\u0661\u0003"+
		"\u0164\u00b2\u0000\u065f\u0661\u0003\u0166\u00b3\u0000\u0660\u065a\u0001"+
		"\u0000\u0000\u0000\u0660\u065c\u0001\u0000\u0000\u0000\u0660\u065f\u0001"+
		"\u0000\u0000\u0000\u0661\u0163\u0001\u0000\u0000\u0000\u0662\u0669\u0005"+
		"\u001e\u0000\u0000\u0663\u0669\u0005\u0015\u0000\u0000\u0664\u0665\u0005"+
		"\u0088\u0000\u0000\u0665\u0669\u0005\u0093\u0000\u0000\u0666\u0669\u0005"+
		"\u0080\u0000\u0000\u0667\u0669\u0005\u0081\u0000\u0000\u0668\u0662\u0001"+
		"\u0000\u0000\u0000\u0668\u0663\u0001\u0000\u0000\u0000\u0668\u0664\u0001"+
		"\u0000\u0000\u0000\u0668\u0666\u0001\u0000\u0000\u0000\u0668\u0667\u0001"+
		"\u0000\u0000\u0000\u0669\u0165\u0001\u0000\u0000\u0000\u066a\u066b\u0007"+
		"\u0011\u0000\u0000\u066b\u0167\u0001\u0000\u0000\u0000\u066c\u066d\u0007"+
		"\u0012\u0000\u0000\u066d\u0169\u0001\u0000\u0000\u0000\u066e\u0670\u0003"+
		"\u0136\u009b\u0000\u066f\u0671\u0003\u019a\u00cd\u0000\u0670\u066f\u0001"+
		"\u0000\u0000\u0000\u0670\u0671\u0001\u0000\u0000\u0000\u0671\u0672\u0001"+
		"\u0000\u0000\u0000\u0672\u0674\u0005>\u0000\u0000\u0673\u0675\u0003\u016c"+
		"\u00b6\u0000\u0674\u0673\u0001\u0000\u0000\u0000\u0674\u0675\u0001\u0000"+
		"\u0000\u0000\u0675\u0676\u0001\u0000\u0000\u0000\u0676\u0677\u0003\u0136"+
		"\u009b\u0000\u0677\u0678\u0005\u0003\u0000\u0000\u0678\u0679\u0003\u0136"+
		"\u009b\u0000\u0679\u016b\u0001\u0000\u0000\u0000\u067a\u067b\u0007\u0013"+
		"\u0000\u0000\u067b\u016d\u0001\u0000\u0000\u0000\u067c\u067e\u0003\u0136"+
		"\u009b\u0000\u067d\u067f\u0003\u019a\u00cd\u0000\u067e\u067d\u0001\u0000"+
		"\u0000\u0000\u067e\u067f\u0001\u0000\u0000\u0000\u067f\u0680\u0001\u0000"+
		"\u0000\u0000\u0680\u0681\u0005\u0016\u0000\u0000\u0681\u0682\u0003\u0174"+
		"\u00ba\u0000\u0682\u016f\u0001\u0000\u0000\u0000\u0683\u0685\u0003\u0136"+
		"\u009b\u0000\u0684\u0686\u0003\u019a\u00cd\u0000\u0685\u0684\u0001\u0000"+
		"\u0000\u0000\u0685\u0686\u0001\u0000\u0000\u0000\u0686\u0687\u0001\u0000"+
		"\u0000\u0000\u0687\u0688\u0003\u0172\u00b9\u0000\u0688\u068a\u0003\u0174"+
		"\u00ba\u0000\u0689\u068b\u0003\u0178\u00bc\u0000\u068a\u0689\u0001\u0000"+
		"\u0000\u0000\u068a\u068b\u0001\u0000\u0000\u0000\u068b\u0171\u0001\u0000"+
		"\u0000\u0000\u068c\u068d\u0007\u0014\u0000\u0000\u068d\u068e\u0005\u0004"+
		"\u0000\u0000\u068e\u0173\u0001\u0000\u0000\u0000\u068f\u0696\u0003H$\u0000"+
		"\u0690\u0691\u0005\u011e\u0000\u0000\u0691\u0692\u0003\u0176\u00bb\u0000"+
		"\u0692\u0693\u0005\u011f\u0000\u0000\u0693\u0696\u0001\u0000\u0000\u0000"+
		"\u0694\u0696\u0003\u01b8\u00dc\u0000\u0695\u068f\u0001\u0000\u0000\u0000"+
		"\u0695\u0690\u0001\u0000\u0000\u0000\u0695\u0694\u0001\u0000\u0000\u0000"+
		"\u0696\u0175\u0001\u0000\u0000\u0000\u0697\u069c\u0003\u0134\u009a\u0000"+
		"\u0698\u0699\u0005\u0116\u0000\u0000\u0699\u069b\u0003\u0134\u009a\u0000"+
		"\u069a\u0698\u0001\u0000\u0000\u0000\u069b\u069e\u0001\u0000\u0000\u0000"+
		"\u069c\u069a\u0001\u0000\u0000\u0000\u069c\u069d\u0001\u0000\u0000\u0000"+
		"\u069d\u0177\u0001\u0000\u0000\u0000\u069e\u069c\u0001\u0000\u0000\u0000"+
		"\u069f\u06a0\u0005Q\u0000\u0000\u06a0\u06a1\u0005\u016f\u0000\u0000\u06a1"+
		"\u0179\u0001\u0000\u0000\u0000\u06a2\u06a6\u0003\u017c\u00be\u0000\u06a3"+
		"\u06a6\u0003\u017e\u00bf\u0000\u06a4\u06a6\u0003\u0180\u00c0\u0000\u06a5"+
		"\u06a2\u0001\u0000\u0000\u0000\u06a5\u06a3\u0001\u0000\u0000\u0000\u06a5"+
		"\u06a4\u0001\u0000\u0000\u0000\u06a6\u017b\u0001\u0000\u0000\u0000\u06a7"+
		"\u06a8\u0003\u0180\u00c0\u0000\u06a8\u06a9\u0003T*\u0000\u06a9\u06aa\u0003"+
		"\u0186\u00c3\u0000\u06aa\u017d\u0001\u0000\u0000\u0000\u06ab\u06ac\u0003"+
		"\u0180\u00c0\u0000\u06ac\u06ad\u0003T*\u0000\u06ad\u017f\u0001\u0000\u0000"+
		"\u0000\u06ae\u06af\u0005\u011e\u0000\u0000\u06af\u06b0\u0005\u0097\u0000"+
		"\u0000\u06b0\u06b1\u0003\u0182\u00c1\u0000\u06b1\u06b2\u0005\u011f\u0000"+
		"\u0000\u06b2\u0181\u0001\u0000\u0000\u0000\u06b3\u06b8\u0003\u0184\u00c2"+
		"\u0000\u06b4\u06b5\u0005\u0116\u0000\u0000\u06b5\u06b7\u0003\u0184\u00c2"+
		"\u0000\u06b6\u06b4\u0001\u0000\u0000\u0000\u06b7\u06ba\u0001\u0000\u0000"+
		"\u0000\u06b8\u06b6\u0001\u0000\u0000\u0000\u06b8\u06b9\u0001\u0000\u0000"+
		"\u0000\u06b9\u0183\u0001\u0000\u0000\u0000\u06ba\u06b8\u0001\u0000\u0000"+
		"\u0000\u06bb\u06bc\u0005\u011e\u0000\u0000\u06bc\u06bd\u0003\u0176\u00bb"+
		"\u0000\u06bd\u06be\u0005\u011f\u0000\u0000\u06be\u0185\u0001\u0000\u0000"+
		"\u0000\u06bf\u06c0\u0005\u011e\u0000\u0000\u06c0\u06c1\u0003\u0188\u00c4"+
		"\u0000\u06c1\u06c2\u0005\u011f\u0000\u0000\u06c2\u0187\u0001\u0000\u0000"+
		"\u0000\u06c3\u06c8\u0003\u01b6\u00db\u0000\u06c4\u06c5\u0005\u0116\u0000"+
		"\u0000\u06c5\u06c7\u0003\u01b6\u00db\u0000\u06c6\u06c4\u0001\u0000\u0000"+
		"\u0000\u06c7\u06ca\u0001\u0000\u0000\u0000\u06c8\u06c6\u0001\u0000\u0000"+
		"\u0000\u06c8\u06c9\u0001\u0000\u0000\u0000\u06c9\u0189\u0001\u0000\u0000"+
		"\u0000\u06ca\u06c8\u0001\u0000\u0000\u0000\u06cb\u06cc\u0005\u0097\u0000"+
		"\u0000\u06cc\u06cd\u0003\u0182\u00c1\u0000\u06cd\u018b\u0001\u0000\u0000"+
		"\u0000\u06ce\u06cf\u0003\u018e\u00c7\u0000\u06cf\u06d0\u0003\u0190\u00c8"+
		"\u0000\u06d0\u018d\u0001\u0000\u0000\u0000\u06d1\u06d2\u0005S\u0000\u0000"+
		"\u06d2\u018f\u0001\u0000\u0000\u0000\u06d3\u06d6\u0003H$\u0000\u06d4\u06d6"+
		"\u0003\u01b8\u00dc\u0000\u06d5\u06d3\u0001\u0000\u0000\u0000\u06d5\u06d4"+
		"\u0001\u0000\u0000\u0000\u06d6\u0191\u0001\u0000\u0000\u0000\u06d7\u06d8"+
		"\u0003\u0136\u009b\u0000\u06d8\u06d9\u0003\u0194\u00ca\u0000\u06d9\u0193"+
		"\u0001\u0000\u0000\u0000\u06da\u06dc\u0005\u001a\u0000\u0000\u06db\u06dd"+
		"\u0005!\u0000\u0000\u06dc\u06db\u0001\u0000\u0000\u0000\u06dc\u06dd\u0001"+
		"\u0000\u0000\u0000\u06dd\u06de\u0001\u0000\u0000\u0000\u06de\u06df\u0005"+
		"\"\u0000\u0000\u06df\u0195\u0001\u0000\u0000\u0000\u06e0\u06e2\u0005\u001a"+
		"\u0000\u0000\u06e1\u06e3\u0003\u019a\u00cd\u0000\u06e2\u06e1\u0001\u0000"+
		"\u0000\u0000\u06e2\u06e3\u0001\u0000\u0000\u0000\u06e3\u06e4\u0001\u0000"+
		"\u0000\u0000\u06e4\u06e5\u0003\u0198\u00cc\u0000\u06e5\u0197\u0001\u0000"+
		"\u0000\u0000\u06e6\u06e7\u0007\u0015\u0000\u0000\u06e7\u0199\u0001\u0000"+
		"\u0000\u0000\u06e8\u06e9\u0005!\u0000\u0000\u06e9\u019b\u0001\u0000\u0000"+
		"\u0000\u06ea\u06eb\u0003\u00fe\u007f\u0000\u06eb\u06ec\u0003\u0168\u00b4"+
		"\u0000\u06ec\u06ed\u0003\u019e\u00cf\u0000\u06ed\u06ee\u0003H$\u0000\u06ee"+
		"\u019d\u0001\u0000\u0000\u0000\u06ef\u06f2\u0003\u01a0\u00d0\u0000\u06f0"+
		"\u06f2\u0003\u01a2\u00d1\u0000\u06f1\u06ef\u0001\u0000\u0000\u0000\u06f1"+
		"\u06f0\u0001\u0000\u0000\u0000\u06f2\u019f\u0001\u0000\u0000\u0000\u06f3"+
		"\u06f4\u0005\u0002\u0000\u0000\u06f4\u01a1\u0001\u0000\u0000\u0000\u06f5"+
		"\u06f6\u0007\u0016\u0000\u0000\u06f6\u01a3\u0001\u0000\u0000\u0000\u06f7"+
		"\u06f8\u00056\u0000\u0000\u06f8\u06f9\u0003H$\u0000\u06f9\u01a5\u0001"+
		"\u0000\u0000\u0000\u06fa\u06fd\u0003\u01a8\u00d4\u0000\u06fb\u06fd\u0005"+
		"\u0086\u0000\u0000\u06fc\u06fa\u0001\u0000\u0000\u0000\u06fc\u06fb\u0001"+
		"\u0000\u0000\u0000\u06fd\u01a7\u0001\u0000\u0000\u0000\u06fe\u06ff\u0007"+
		"\u0017\u0000\u0000\u06ff\u01a9\u0001\u0000\u0000\u0000\u0700\u0701\u0007"+
		"\u0018\u0000\u0000\u0701\u01ab\u0001\u0000\u0000\u0000\u0702\u0703\u0003"+
		"\u01ae\u00d7\u0000\u0703\u0705\u0005\u011e\u0000\u0000\u0704\u0706\u0003"+
		"\u01b2\u00d9\u0000\u0705\u0704\u0001\u0000\u0000\u0000\u0705\u0706\u0001"+
		"\u0000\u0000\u0000\u0706\u0707\u0001\u0000\u0000\u0000\u0707\u0708\u0005"+
		"\u011f\u0000\u0000\u0708\u01ad\u0001\u0000\u0000\u0000\u0709\u070c\u0003"+
		"\u01b4\u00da\u0000\u070a\u070b\u0005\u0125\u0000\u0000\u070b\u070d\u0003"+
		"\u01b4\u00da\u0000\u070c\u070a\u0001\u0000\u0000\u0000\u070c\u070d\u0001"+
		"\u0000\u0000\u0000\u070d\u0710\u0001\u0000\u0000\u0000\u070e\u0710\u0003"+
		"\u01b0\u00d8\u0000\u070f\u0709\u0001\u0000\u0000\u0000\u070f\u070e\u0001"+
		"\u0000\u0000\u0000\u0710\u01af\u0001\u0000\u0000\u0000\u0711\u0712\u0007"+
		"\u0019\u0000\u0000\u0712\u01b1\u0001\u0000\u0000\u0000\u0713\u0718\u0003"+
		"\u00fa}\u0000\u0714\u0715\u0005\u0116\u0000\u0000\u0715\u0717\u0003\u00fa"+
		"}\u0000\u0716\u0714\u0001\u0000\u0000\u0000\u0717\u071a\u0001\u0000\u0000"+
		"\u0000\u0718\u0716\u0001\u0000\u0000\u0000\u0718\u0719\u0001\u0000\u0000"+
		"\u0000\u0719\u01b3\u0001\u0000\u0000\u0000\u071a\u0718\u0001\u0000\u0000"+
		"\u0000\u071b\u0721\u0003\u01ba\u00dd\u0000\u071c\u0721\u0003\u01bc\u00de"+
		"\u0000\u071d\u0721\u0003\u01d4\u00ea\u0000\u071e\u0721\u0003\u01d0\u00e8"+
		"\u0000\u071f\u0721\u0003\u01d2\u00e9\u0000\u0720\u071b\u0001\u0000\u0000"+
		"\u0000\u0720\u071c\u0001\u0000\u0000\u0000\u0720\u071d\u0001\u0000\u0000"+
		"\u0000\u0720\u071e\u0001\u0000\u0000\u0000\u0720\u071f\u0001\u0000\u0000"+
		"\u0000\u0721\u01b5\u0001\u0000\u0000\u0000\u0722\u0728\u0003\u01ba\u00dd"+
		"\u0000\u0723\u0728\u0003\u01bc\u00de\u0000\u0724\u0728\u0003\u01d4\u00ea"+
		"\u0000\u0725\u0728\u0003\u01ce\u00e7\u0000\u0726\u0728\u0003\u01d0\u00e8"+
		"\u0000\u0727\u0722\u0001\u0000\u0000\u0000\u0727\u0723\u0001\u0000\u0000"+
		"\u0000\u0727\u0724\u0001\u0000\u0000\u0000\u0727\u0725\u0001\u0000\u0000"+
		"\u0000\u0727\u0726\u0001\u0000\u0000\u0000\u0728\u01b7\u0001\u0000\u0000"+
		"\u0000\u0729\u072c\u0003\u01be\u00df\u0000\u072a\u072c\u0003\u01c0\u00e0"+
		"\u0000\u072b\u0729\u0001\u0000\u0000\u0000\u072b\u072a\u0001\u0000\u0000"+
		"\u0000\u072c\u01b9\u0001\u0000\u0000\u0000\u072d\u072e\u0005\u0167\u0000"+
		"\u0000\u072e\u01bb\u0001\u0000\u0000\u0000\u072f\u0730\u0005\u0145\u0000"+
		"\u0000\u0730\u01bd\u0001\u0000\u0000\u0000\u0731\u0732\u0005\u0146\u0000"+
		"\u0000\u0732\u01bf\u0001\u0000\u0000\u0000\u0733\u0734\u0007\u001a\u0000"+
		"\u0000\u0734\u01c1\u0001\u0000\u0000\u0000\u0735\u0736\u0005\u0172\u0000"+
		"\u0000\u0736\u0737\u0003\u01c4\u00e2\u0000\u0737\u0738\u0005\u0173\u0000"+
		"\u0000\u0738\u073e\u0001\u0000\u0000\u0000\u0739\u073a\u0005\u0172\u0000"+
		"\u0000\u073a\u073b\u0003\u01ca\u00e5\u0000\u073b\u073c\u0005\u0173\u0000"+
		"\u0000\u073c\u073e\u0001\u0000\u0000\u0000\u073d\u0735\u0001\u0000\u0000"+
		"\u0000\u073d\u0739\u0001\u0000\u0000\u0000\u073e\u01c3\u0001\u0000\u0000"+
		"\u0000\u073f\u0740\u0003\u01b4\u00da\u0000\u0740\u0741\u0004\u00e2\u0005"+
		"\u0001\u0741\u0743\u0005\u011e\u0000\u0000\u0742\u0744\u0003\u01c6\u00e3"+
		"\u0000\u0743\u0742\u0001\u0000\u0000\u0000\u0743\u0744\u0001\u0000\u0000"+
		"\u0000\u0744\u0745\u0001\u0000\u0000\u0000\u0745\u0746\u0005\u011f\u0000"+
		"\u0000\u0746\u0752\u0001\u0000\u0000\u0000\u0747\u0748\u0003\u01b4\u00da"+
		"\u0000\u0748\u0749\u0005\u0125\u0000\u0000\u0749\u074a\u0003\u01b4\u00da"+
		"\u0000\u074a\u074b\u0004\u00e2\u0006\u0001\u074b\u074d\u0005\u011e\u0000"+
		"\u0000\u074c\u074e\u0003\u01c6\u00e3\u0000\u074d\u074c\u0001\u0000\u0000"+
		"\u0000\u074d\u074e\u0001\u0000\u0000\u0000\u074e\u074f\u0001\u0000\u0000"+
		"\u0000\u074f\u0750\u0005\u011f\u0000\u0000\u0750\u0752\u0001\u0000\u0000"+
		"\u0000\u0751\u073f\u0001\u0000\u0000\u0000\u0751\u0747\u0001\u0000\u0000"+
		"\u0000\u0752\u01c5\u0001\u0000\u0000\u0000\u0753\u0758\u0003\u01c8\u00e4"+
		"\u0000\u0754\u0755\u0005\u0116\u0000\u0000\u0755\u0757\u0003\u01c8\u00e4"+
		"\u0000\u0756\u0754\u0001\u0000\u0000\u0000\u0757\u075a\u0001\u0000\u0000"+
		"\u0000\u0758\u0756\u0001\u0000\u0000\u0000\u0758\u0759\u0001\u0000\u0000"+
		"\u0000\u0759\u01c7\u0001\u0000\u0000\u0000\u075a\u0758\u0001\u0000\u0000"+
		"\u0000\u075b\u0766\u0005\u016f\u0000\u0000\u075c\u0766\u0005\u012b\u0000"+
		"\u0000\u075d\u075e\u0003\u01b4\u00da\u0000\u075e\u075f\u0005\u0113\u0000"+
		"\u0000\u075f\u0760\u0005\u016f\u0000\u0000\u0760\u0766\u0001\u0000\u0000"+
		"\u0000\u0761\u0762\u0003\u01b4\u00da\u0000\u0762\u0763\u0005\u0113\u0000"+
		"\u0000\u0763\u0764\u0005\u012b\u0000\u0000\u0764\u0766\u0001\u0000\u0000"+
		"\u0000\u0765\u075b\u0001\u0000\u0000\u0000\u0765\u075c\u0001\u0000\u0000"+
		"\u0000\u0765\u075d\u0001\u0000\u0000\u0000\u0765\u0761\u0001\u0000\u0000"+
		"\u0000\u0766\u01c9\u0001\u0000\u0000\u0000\u0767\u076c\u0003\u01cc\u00e6"+
		"\u0000\u0768\u0769\u0005\u0125\u0000\u0000\u0769\u076b\u0003\u01cc\u00e6"+
		"\u0000\u076a\u0768\u0001\u0000\u0000\u0000\u076b\u076e\u0001\u0000\u0000"+
		"\u0000\u076c\u076a\u0001\u0000\u0000\u0000\u076c\u076d\u0001\u0000\u0000"+
		"\u0000\u076d\u01cb\u0001\u0000\u0000\u0000\u076e\u076c\u0001\u0000\u0000"+
		"\u0000\u076f\u0772\u0003\u01b4\u00da\u0000\u0770\u0772\u0005\u012b\u0000"+
		"\u0000\u0771\u076f\u0001\u0000\u0000\u0000\u0771\u0770\u0001\u0000\u0000"+
		"\u0000\u0772\u01cd\u0001\u0000\u0000\u0000\u0773\u0774\u0007\u001b\u0000"+
		"\u0000\u0774\u01cf\u0001\u0000\u0000\u0000\u0775\u0776\u0005\u016b\u0000"+
		"\u0000\u0776\u01d1\u0001\u0000\u0000\u0000\u0777\u0778\u0005\u016c\u0000"+
		"\u0000\u0778\u01d3\u0001\u0000\u0000\u0000\u0779\u077a\u0007\u001c\u0000"+
		"\u0000\u077a\u01d5\u0001\u0000\u0000\u0000\u077b\u077d\u0003\u0106\u0083"+
		"\u0000\u077c\u077b\u0001\u0000\u0000\u0000\u077c\u077d\u0001\u0000\u0000"+
		"\u0000\u077d\u077e\u0001\u0000\u0000\u0000\u077e\u077f\u0003\u01da\u00ed"+
		"\u0000\u077f\u01d7\u0001\u0000\u0000\u0000\u0780\u0783\u0003\u01da\u00ed"+
		"\u0000\u0781\u0783\u0003\u01e0\u00f0\u0000\u0782\u0780\u0001\u0000\u0000"+
		"\u0000\u0782\u0781\u0001\u0000\u0000\u0000\u0783\u01d9\u0001\u0000\u0000"+
		"\u0000\u0784\u0787\u0005\u012b\u0000\u0000\u0785\u0787\u0003\u01dc\u00ee"+
		"\u0000\u0786\u0784\u0001\u0000\u0000\u0000\u0786\u0785\u0001\u0000\u0000"+
		"\u0000\u0787\u01db\u0001\u0000\u0000\u0000\u0788\u0789\u0005\u012b\u0000"+
		"\u0000\u0789\u078b\u0005\u0125\u0000\u0000\u078a\u078c\u0005\u012b\u0000"+
		"\u0000\u078b\u078a\u0001\u0000\u0000\u0000\u078b\u078c\u0001\u0000\u0000"+
		"\u0000\u078c\u078e\u0001\u0000\u0000\u0000\u078d\u078f\u0003\u01de\u00ef"+
		"\u0000\u078e\u078d\u0001\u0000\u0000\u0000\u078e\u078f\u0001\u0000\u0000"+
		"\u0000\u078f\u0799\u0001\u0000\u0000\u0000\u0790\u0791\u0005\u0125\u0000"+
		"\u0000\u0791\u0793\u0005\u012b\u0000\u0000\u0792\u0794\u0003\u01de\u00ef"+
		"\u0000\u0793\u0792\u0001\u0000\u0000\u0000\u0793\u0794\u0001\u0000\u0000"+
		"\u0000\u0794\u0799\u0001\u0000\u0000\u0000\u0795\u0796\u0005\u012b\u0000"+
		"\u0000\u0796\u0799\u0003\u01de\u00ef\u0000\u0797\u0799\u0005\u0169\u0000"+
		"\u0000\u0798\u0788\u0001\u0000\u0000\u0000\u0798\u0790\u0001\u0000\u0000"+
		"\u0000\u0798\u0795\u0001\u0000\u0000\u0000\u0798\u0797\u0001\u0000\u0000"+
		"\u0000\u0799\u01dd\u0001\u0000\u0000\u0000\u079a\u079b\u0005\u0168\u0000"+
		"\u0000\u079b\u079c\u0005\u012b\u0000\u0000\u079c\u01df\u0001\u0000\u0000"+
		"\u0000\u079d\u07a1\u0003\u01e2\u00f1\u0000\u079e\u07a1\u0003\u01e4\u00f2"+
		"\u0000\u079f\u07a1\u0003\u01ec\u00f6\u0000\u07a0\u079d\u0001\u0000\u0000"+
		"\u0000\u07a0\u079e\u0001\u0000\u0000\u0000\u07a0\u079f\u0001\u0000\u0000"+
		"\u0000\u07a1\u01e1\u0001\u0000\u0000\u0000\u07a2\u07a3\u0005\u016f\u0000"+
		"\u0000\u07a3\u01e3\u0001\u0000\u0000\u0000\u07a4\u07a8\u0003\u01e8\u00f4"+
		"\u0000\u07a5\u07a8\u0003\u01e6\u00f3\u0000\u07a6\u07a8\u0003\u01ea\u00f5"+
		"\u0000\u07a7\u07a4\u0001\u0000\u0000\u0000\u07a7\u07a5\u0001\u0000\u0000"+
		"\u0000\u07a7\u07a6\u0001\u0000\u0000\u0000\u07a8\u01e5\u0001\u0000\u0000"+
		"\u0000\u07a9\u07aa\u0005\u00ff\u0000\u0000\u07aa\u07ab\u0005\u016f\u0000"+
		"\u0000\u07ab\u01e7\u0001\u0000\u0000\u0000\u07ac\u07ad\u0005\u0101\u0000"+
		"\u0000\u07ad\u07ae\u0005\u016f\u0000\u0000\u07ae\u01e9\u0001\u0000\u0000"+
		"\u0000\u07af\u07b0\u0005\u00fd\u0000\u0000\u07b0\u07b1\u0005\u016f\u0000"+
		"\u0000\u07b1\u01eb\u0001\u0000\u0000\u0000\u07b2\u07b3\u0007\u0015\u0000"+
		"\u0000\u07b3\u01ed\u0001\u0000\u0000\u0000\u07b4\u07b8\u0003\u01f0\u00f8"+
		"\u0000\u07b5\u07b8\u0003\u01f6\u00fb\u0000\u07b6\u07b8\u0003\u01fc\u00fe"+
		"\u0000\u07b7\u07b4\u0001\u0000\u0000\u0000\u07b7\u07b5\u0001\u0000\u0000"+
		"\u0000\u07b7\u07b6\u0001\u0000\u0000\u0000\u07b8\u01ef\u0001\u0000\u0000"+
		"\u0000\u07b9\u07bb\u0003\u01f2\u00f9\u0000\u07ba\u07bc\u0003\u01f4\u00fa"+
		"\u0000\u07bb\u07ba\u0001\u0000\u0000\u0000\u07bb\u07bc\u0001\u0000\u0000"+
		"\u0000\u07bc\u01f1\u0001\u0000\u0000\u0000\u07bd\u07e0\u0005A\u0000\u0000"+
		"\u07be\u07e0\u0005\u00f7\u0000\u0000\u07bf\u07c0\u0005A\u0000\u0000\u07c0"+
		"\u07e0\u0005\u009a\u0000\u0000\u07c1\u07c2\u0005\u00f7\u0000\u0000\u07c2"+
		"\u07e0\u0005\u009a\u0000\u0000\u07c3\u07e0\u0005\u00f8\u0000\u0000\u07c4"+
		"\u07e0\u0005\u00f9\u0000\u0000\u07c5\u07c6\u0005s\u0000\u0000\u07c6\u07e0"+
		"\u0005A\u0000\u0000\u07c7\u07c8\u0005s\u0000\u0000\u07c8\u07e0\u0005\u00f7"+
		"\u0000\u0000\u07c9\u07e0\u0005\u00fa\u0000\u0000\u07ca\u07cb\u0005s\u0000"+
		"\u0000\u07cb\u07cc\u0005A\u0000\u0000\u07cc\u07e0\u0005\u009a\u0000\u0000"+
		"\u07cd\u07ce\u0005s\u0000\u0000\u07ce\u07cf\u0005\u00f7\u0000\u0000\u07cf"+
		"\u07e0\u0005\u009a\u0000\u0000\u07d0\u07d1\u0005\u00fa\u0000\u0000\u07d1"+
		"\u07e0\u0005\u009a\u0000\u0000\u07d2\u07e0\u0005\u00fb\u0000\u0000\u07d3"+
		"\u07e0\u0005\u0109\u0000\u0000\u07d4\u07e0\u0005\u010a\u0000\u0000\u07d5"+
		"\u07e0\u0005\u00d3\u0000\u0000\u07d6\u07e0\u0005\u00d4\u0000\u0000\u07d7"+
		"\u07d8\u0005\u00d3\u0000\u0000\u07d8\u07e0\u0005\u009a\u0000\u0000\u07d9"+
		"\u07e0\u0005\u0107\u0000\u0000\u07da\u07db\u0005\u0107\u0000\u0000\u07db"+
		"\u07e0\u0005\u009a\u0000\u0000\u07dc\u07e0\u0005\u0108\u0000\u0000\u07dd"+
		"\u07e0\u0005\u00d8\u0000\u0000\u07de\u07e0\u0005\u00fc\u0000\u0000\u07df"+
		"\u07bd\u0001\u0000\u0000\u0000\u07df\u07be\u0001\u0000\u0000\u0000\u07df"+
		"\u07bf\u0001\u0000\u0000\u0000\u07df\u07c1\u0001\u0000\u0000\u0000\u07df"+
		"\u07c3\u0001\u0000\u0000\u0000\u07df\u07c4\u0001\u0000\u0000\u0000\u07df"+
		"\u07c5\u0001\u0000\u0000\u0000\u07df\u07c7\u0001\u0000\u0000\u0000\u07df"+
		"\u07c9\u0001\u0000\u0000\u0000\u07df\u07ca\u0001\u0000\u0000\u0000\u07df"+
		"\u07cd\u0001\u0000\u0000\u0000\u07df\u07d0\u0001\u0000\u0000\u0000\u07df"+
		"\u07d2\u0001\u0000\u0000\u0000\u07df\u07d3\u0001\u0000\u0000\u0000\u07df"+
		"\u07d4\u0001\u0000\u0000\u0000\u07df\u07d5\u0001\u0000\u0000\u0000\u07df"+
		"\u07d6\u0001\u0000\u0000\u0000\u07df\u07d7\u0001\u0000\u0000\u0000\u07df"+
		"\u07d9\u0001\u0000\u0000\u0000\u07df\u07da\u0001\u0000\u0000\u0000\u07df"+
		"\u07dc\u0001\u0000\u0000\u0000\u07df\u07dd\u0001\u0000\u0000\u0000\u07df"+
		"\u07de\u0001\u0000\u0000\u0000\u07e0\u01f3\u0001\u0000\u0000\u0000\u07e1"+
		"\u07e2\u0005\u011e\u0000\u0000\u07e2\u07e3\u0005\u012b\u0000\u0000\u07e3"+
		"\u07e4\u0005\u011f\u0000\u0000\u07e4\u01f5\u0001\u0000\u0000\u0000\u07e5"+
		"\u07e7\u0003\u01f8\u00fc\u0000\u07e6\u07e8\u0003\u01fa\u00fd\u0000\u07e7"+
		"\u07e6\u0001\u0000\u0000\u0000\u07e7\u07e8\u0001\u0000\u0000\u0000\u07e8"+
		"\u01f7\u0001\u0000\u0000\u0000\u07e9\u07f5\u0005\u00f5\u0000\u0000\u07ea"+
		"\u07f5\u0005\u012b\u0000\u0000\u07eb\u07f5\u0005\u00f6\u0000\u0000\u07ec"+
		"\u07f5\u0005J\u0000\u0000\u07ed\u07f5\u0005\u00f3\u0000\u0000\u07ee\u07f5"+
		"\u0005\u00f4\u0000\u0000\u07ef\u07f0\u0005\u00f4\u0000\u0000\u07f0\u07f5"+
		"\u0005{\u0000\u0000\u07f1\u07f5\u0005\u0102\u0000\u0000\u07f2\u07f5\u0005"+
		"\u0103\u0000\u0000\u07f3\u07f5\u0005\u0104\u0000\u0000\u07f4\u07e9\u0001"+
		"\u0000\u0000\u0000\u07f4\u07ea\u0001\u0000\u0000\u0000\u07f4\u07eb\u0001"+
		"\u0000\u0000\u0000\u07f4\u07ec\u0001\u0000\u0000\u0000\u07f4\u07ed\u0001"+
		"\u0000\u0000\u0000\u07f4\u07ee\u0001\u0000\u0000\u0000\u07f4\u07ef\u0001"+
		"\u0000\u0000\u0000\u07f4\u07f1\u0001\u0000\u0000\u0000\u07f4\u07f2\u0001"+
		"\u0000\u0000\u0000\u07f4\u07f3\u0001\u0000\u0000\u0000\u07f5\u01f9\u0001"+
		"\u0000\u0000\u0000\u07f6\u07f7\u0005\u011e\u0000\u0000\u07f7\u07f8\u0005"+
		"\u012b\u0000\u0000\u07f8\u07ff\u0005\u011f\u0000\u0000\u07f9\u07fa\u0005"+
		"\u011e\u0000\u0000\u07fa\u07fb\u0005\u012b\u0000\u0000\u07fb\u07fc\u0005"+
		"\u0116\u0000\u0000\u07fc\u07fd\u0005\u012b\u0000\u0000\u07fd\u07ff\u0005"+
		"\u011f\u0000\u0000\u07fe\u07f6\u0001\u0000\u0000\u0000\u07fe\u07f9\u0001"+
		"\u0000\u0000\u0000\u07ff\u01fb\u0001\u0000\u0000\u0000\u0800\u0801\u0003"+
		"\u01fe\u00ff\u0000\u0801\u01fd\u0001\u0000\u0000\u0000\u0802\u0840\u0005"+
		"\u0106\u0000\u0000\u0803\u0840\u0005\u00e0\u0000\u0000\u0804\u0840\u0005"+
		"\u00d7\u0000\u0000\u0805\u0840\u0005\u00d6\u0000\u0000\u0806\u0840\u0005"+
		"\u00d5\u0000\u0000\u0807\u0840\u0005\u010c\u0000\u0000\u0808\u0840\u0005"+
		"5\u0000\u0000\u0809\u0840\u0005\u010d\u0000\u0000\u080a\u0840\u0005\u010b"+
		"\u0000\u0000\u080b\u0840\u0005\u00dd\u0000\u0000\u080c\u0840\u0005\u00de"+
		"\u0000\u0000\u080d\u0840\u0005\u00e1\u0000\u0000\u080e\u0840\u0005\u00e5"+
		"\u0000\u0000\u080f\u0840\u0005\u00e6\u0000\u0000\u0810\u0840\u0005\u00e2"+
		"\u0000\u0000\u0811\u0840\u0005\u00e3\u0000\u0000\u0812\u0840\u0005\u00e4"+
		"\u0000\u0000\u0813\u0840\u0005\u00df\u0000\u0000\u0814\u0840\u0005\u00d9"+
		"\u0000\u0000\u0815\u0840\u0005\u00e7\u0000\u0000\u0816\u0840\u0005\u00da"+
		"\u0000\u0000\u0817\u0840\u0005\u00e8\u0000\u0000\u0818\u0840\u0005\u00db"+
		"\u0000\u0000\u0819\u0840\u0005\u00e9\u0000\u0000\u081a\u0840\u0005\u00ea"+
		"\u0000\u0000\u081b\u0840\u0005\u00dc\u0000\u0000\u081c\u0840\u0005\u00eb"+
		"\u0000\u0000\u081d\u0840\u0005\u00ec\u0000\u0000\u081e\u0840\u0005\u00ed"+
		"\u0000\u0000\u081f\u0840\u0005\u00ee\u0000\u0000\u0820\u0840\u0005\u00ef"+
		"\u0000\u0000\u0821\u0840\u0005$\u0000\u0000\u0822\u0840\u0005\u00f0\u0000"+
		"\u0000\u0823\u0840\u0005\u00f2\u0000\u0000\u0824\u0840\u0005\u00f1\u0000"+
		"\u0000\u0825\u0840\u0005\u00d1\u0000\u0000\u0826\u0840\u0005\u00d2\u0000"+
		"\u0000\u0827\u0840\u0005\u00fd\u0000\u0000\u0828\u0840\u0005\u00fe\u0000"+
		"\u0000\u0829\u0840\u0005\u00ff\u0000\u0000\u082a\u082b\u0005\u00ff\u0000"+
		"\u0000\u082b\u082c\u0005:\u0000\u0000\u082c\u082d\u0005\u00ff\u0000\u0000"+
		"\u082d\u0840\u0005\u009d\u0000\u0000\u082e\u0840\u0005\u0100\u0000\u0000"+
		"\u082f\u0840\u0005\u0102\u0000\u0000\u0830\u0840\u0005\u0103\u0000\u0000"+
		"\u0831\u0840\u0005\u0104\u0000\u0000\u0832\u0840\u0005\u0101\u0000\u0000"+
		"\u0833\u0834\u0005\u0101\u0000\u0000\u0834\u0835\u0005:\u0000\u0000\u0835"+
		"\u0836\u0005\u00ff\u0000\u0000\u0836\u0840\u0005\u009d\u0000\u0000\u0837"+
		"\u0838\u0005\u0101\u0000\u0000\u0838\u0839\u0005;\u0000\u0000\u0839\u083a"+
		"\u0005\u00ff\u0000\u0000\u083a\u0840\u0005\u009d\u0000\u0000\u083b\u0840"+
		"\u0005\u0105\u0000\u0000\u083c\u0840\u0005\u00ce\u0000\u0000\u083d\u0840"+
		"\u0005\u00d0\u0000\u0000\u083e\u0840\u0005\u00cf\u0000\u0000\u083f\u0802"+
		"\u0001\u0000\u0000\u0000\u083f\u0803\u0001\u0000\u0000\u0000\u083f\u0804"+
		"\u0001\u0000\u0000\u0000\u083f\u0805\u0001\u0000\u0000\u0000\u083f\u0806"+
		"\u0001\u0000\u0000\u0000\u083f\u0807\u0001\u0000\u0000\u0000\u083f\u0808"+
		"\u0001\u0000\u0000\u0000\u083f\u0809\u0001\u0000\u0000\u0000\u083f\u080a"+
		"\u0001\u0000\u0000\u0000\u083f\u080b\u0001\u0000\u0000\u0000\u083f\u080c"+
		"\u0001\u0000\u0000\u0000\u083f\u080d\u0001\u0000\u0000\u0000\u083f\u080e"+
		"\u0001\u0000\u0000\u0000\u083f\u080f\u0001\u0000\u0000\u0000\u083f\u0810"+
		"\u0001\u0000\u0000\u0000\u083f\u0811\u0001\u0000\u0000\u0000\u083f\u0812"+
		"\u0001\u0000\u0000\u0000\u083f\u0813\u0001\u0000\u0000\u0000\u083f\u0814"+
		"\u0001\u0000\u0000\u0000\u083f\u0815\u0001\u0000\u0000\u0000\u083f\u0816"+
		"\u0001\u0000\u0000\u0000\u083f\u0817\u0001\u0000\u0000\u0000\u083f\u0818"+
		"\u0001\u0000\u0000\u0000\u083f\u0819\u0001\u0000\u0000\u0000\u083f\u081a"+
		"\u0001\u0000\u0000\u0000\u083f\u081b\u0001\u0000\u0000\u0000\u083f\u081c"+
		"\u0001\u0000\u0000\u0000\u083f\u081d\u0001\u0000\u0000\u0000\u083f\u081e"+
		"\u0001\u0000\u0000\u0000\u083f\u081f\u0001\u0000\u0000\u0000\u083f\u0820"+
		"\u0001\u0000\u0000\u0000\u083f\u0821\u0001\u0000\u0000\u0000\u083f\u0822"+
		"\u0001\u0000\u0000\u0000\u083f\u0823\u0001\u0000\u0000\u0000\u083f\u0824"+
		"\u0001\u0000\u0000\u0000\u083f\u0825\u0001\u0000\u0000\u0000\u083f\u0826"+
		"\u0001\u0000\u0000\u0000\u083f\u0827\u0001\u0000\u0000\u0000\u083f\u0828"+
		"\u0001\u0000\u0000\u0000\u083f\u0829\u0001\u0000\u0000\u0000\u083f\u082a"+
		"\u0001\u0000\u0000\u0000\u083f\u082e\u0001\u0000\u0000\u0000\u083f\u082f"+
		"\u0001\u0000\u0000\u0000\u083f\u0830\u0001\u0000\u0000\u0000\u083f\u0831"+
		"\u0001\u0000\u0000\u0000\u083f\u0832\u0001\u0000\u0000\u0000\u083f\u0833"+
		"\u0001\u0000\u0000\u0000\u083f\u0837\u0001\u0000\u0000\u0000\u083f\u083b"+
		"\u0001\u0000\u0000\u0000\u083f\u083c\u0001\u0000\u0000\u0000\u083f\u083d"+
		"\u0001\u0000\u0000\u0000\u083f\u083e\u0001\u0000\u0000\u0000\u0840\u01ff"+
		"\u0001\u0000\u0000\u0000\u0841\u0842\u0007\u001d\u0000\u0000\u0842\u0201"+
		"\u0001\u0000\u0000\u0000\u00c6\u0205\u0208\u0223\u0236\u0241\u0245\u024e"+
		"\u0259\u025f\u0262\u0266\u026a\u0271\u0278\u027b\u027e\u0288\u029c\u02a1"+
		"\u02ab\u02b0\u02b7\u02bf\u02c2\u02c7\u02ca\u02cd\u02d0\u02d3\u02d6\u02d8"+
		"\u02e4\u02e9\u02ec\u02ef\u02f7\u02fe\u0305\u030a\u0310\u0314\u0316\u0318"+
		"\u031d\u0322\u0328\u032c\u032e\u0330\u0334\u033a\u0341\u0345\u034a\u034c"+
		"\u0352\u035a\u0360\u0362\u0367\u0369\u0371\u0374\u0377\u037e\u0380\u0384"+
		"\u0396\u039e\u03aa\u03c0\u03c5\u03cb\u03d9\u03e4\u03ed\u03fd\u0417\u041d"+
		"\u0427\u043a\u0442\u0449\u0451\u0457\u045e\u0465\u046a\u0471\u0479\u0485"+
		"\u048a\u048f\u0491\u04a0\u04ab\u04af\u04b4\u04be\u04c5\u04c9\u04ce\u04d8"+
		"\u04e4\u04f7\u0500\u0503\u0505\u050a\u050d\u0510\u0520\u052a\u0543\u0548"+
		"\u054f\u0557\u055b\u0561\u056f\u0575\u057c\u0581\u058b\u058e\u0591\u0598"+
		"\u05a6\u05ae\u05b2\u05c1\u05c9\u05cd\u05d7\u05d9\u05dd\u05e6\u05ec\u05f1"+
		"\u0601\u0606\u0609\u0616\u061c\u0623\u062a\u0631\u0639\u0653\u065c\u0660"+
		"\u0668\u0670\u0674\u067e\u0685\u068a\u0695\u069c\u06a5\u06b8\u06c8\u06d5"+
		"\u06dc\u06e2\u06f1\u06fc\u0705\u070c\u070f\u0718\u0720\u0727\u072b\u073d"+
		"\u0743\u074d\u0751\u0758\u0765\u076c\u0771\u077c\u0782\u0786\u078b\u078e"+
		"\u0793\u0798\u07a0\u07a7\u07b7\u07bb\u07df\u07e7\u07f4\u07fe\u083f";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}