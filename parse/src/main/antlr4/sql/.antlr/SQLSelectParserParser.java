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
		MAX_FILE_COUNT=355, MAX_RECORDS_PER_FILE=356, KIND=357, JOB_ID=358, ALTER=359, 
		DATABASE=360, FILE=361, FUNCTION=362, MACRO=363, MATERIALIZED=364, PROCEDURE=365, 
		RETURNS=366, ROLE=367, SCHEMA=368, SEQUENCE=369, STAGE=370, USER=371, 
		VIEW=372, Identifier=373, EXPONEN=374, Scientific_Numeric_Literal=375, 
		Numeric_Identifier=376, Double_Quoted_Numeric_Identifier=377, Dollar_Sign_Identifier=378, 
		BlockComment=379, LineComment=380, Character_String_Literal=381, Space=382, 
		White_Space=383, JINJA_OPEN=384, JINJA_CLOSE=385, BAD=386;
	public static final int
		RULE_script = 0, RULE_sql_statement = 1, RULE_ddl = 2, RULE_ddl_primary = 3, 
		RULE_sql = 4, RULE_column_value = 5, RULE_predicand_value = 6, RULE_in_list_predicate_value = 7, 
		RULE_condition_value = 8, RULE_tuple_value = 9, RULE_query_value = 10, 
		RULE_join_extension_value = 11, RULE_literal_value = 12, RULE_values_statement_end = 13, 
		RULE_insert_end_point = 14, RULE_create_statement_primary = 15, RULE_drop_statement_primary = 16, 
		RULE_alter_statement_primary = 17, RULE_create_table_expression = 18, 
		RULE_create_index_expression = 19, RULE_create_view_expression = 20, RULE_create_materialized_view_expression = 21, 
		RULE_create_function_expression = 22, RULE_create_procedure_expression = 23, 
		RULE_create_macro_expression = 24, RULE_create_sequence_expression = 25, 
		RULE_create_schema_expression = 26, RULE_create_database_expression = 27, 
		RULE_create_role_expression = 28, RULE_create_user_expression = 29, RULE_create_stage_expression = 30, 
		RULE_create_file_format_expression = 31, RULE_ddl_object_type = 32, RULE_drop_options = 33, 
		RULE_alter_options = 34, RULE_generic_ddl_paren_content = 35, RULE_generic_ddl_options = 36, 
		RULE_with_query = 37, RULE_with_clause = 38, RULE_with_list_item = 39, 
		RULE_cte_body = 40, RULE_query_alias = 41, RULE_query = 42, RULE_insert_expression = 43, 
		RULE_snowflake_insert = 44, RULE_insert_target_table_primary = 45, RULE_postgres_insert = 46, 
		RULE_insert_preamble = 47, RULE_insert_source_primary = 48, RULE_update_expression = 49, 
		RULE_returning = 50, RULE_assignment_expression_list = 51, RULE_assignment_expression = 52, 
		RULE_query_expression = 53, RULE_intersected_query = 54, RULE_intersect_clause = 55, 
		RULE_intersect_operator = 56, RULE_unionized_query = 57, RULE_union_clause = 58, 
		RULE_union_operator = 59, RULE_query_primary = 60, RULE_subquery = 61, 
		RULE_query_specification = 62, RULE_into_list = 63, RULE_set_qualifier = 64, 
		RULE_select_list = 65, RULE_select_item = 66, RULE_as_clause = 67, RULE_select_all_columns = 68, 
		RULE_wildcard_reference = 69, RULE_from_clause = 70, RULE_join_extension = 71, 
		RULE_table_reference_list = 72, RULE_join_extension_primary = 73, RULE_lateral_modifier = 74, 
		RULE_table_primary = 75, RULE_relation_as_clause = 76, RULE_tuple_primary = 77, 
		RULE_db_object_name = 78, RULE_unqualified_join = 79, RULE_qualified_join = 80, 
		RULE_join_type = 81, RULE_join_specification = 82, RULE_join_condition = 83, 
		RULE_named_columns_join = 84, RULE_using_term = 85, RULE_table_function_primary = 86, 
		RULE_table_function = 87, RULE_flatten_table_function = 88, RULE_flatten_argument_list = 89, 
		RULE_flatten_argument = 90, RULE_flatten_argument_value = 91, RULE_flatten_function_name = 92, 
		RULE_table_argument_literal = 93, RULE_table_argument_boolean = 94, RULE_generator_table_function = 95, 
		RULE_generator_argument_list = 96, RULE_generator_argument = 97, RULE_generator_argument_value = 98, 
		RULE_generator_function_name = 99, RULE_result_scan_table_function = 100, 
		RULE_result_scan_function_name = 101, RULE_infer_schema_table_function = 102, 
		RULE_infer_schema_argument_list = 103, RULE_infer_schema_argument = 104, 
		RULE_infer_schema_argument_value = 105, RULE_infer_schema_function_name = 106, 
		RULE_infer_schema_files_argument = 107, RULE_validate_table_function = 108, 
		RULE_validate_function_name = 109, RULE_generic_table_function = 110, 
		RULE_table_function_name = 111, RULE_table_function_argument_list = 112, 
		RULE_column_reference_list = 113, RULE_column_reference = 114, RULE_column_primary = 115, 
		RULE_predicand_primary = 116, RULE_value_expression_primary = 117, RULE_parenthesized_value_expression = 118, 
		RULE_nonparenthesized_value_expression_primary = 119, RULE_predicand_subquery = 120, 
		RULE_aggregate_function = 121, RULE_set_function_type = 122, RULE_set_qualifier_type = 123, 
		RULE_case_expression = 124, RULE_when_clause_list = 125, RULE_searched_when_clause = 126, 
		RULE_when_value_list = 127, RULE_when_value_clause = 128, RULE_else_clause = 129, 
		RULE_case_result = 130, RULE_null_literal = 131, RULE_cast_function_expression = 132, 
		RULE_cast_function_name = 133, RULE_window_over_partition_expression = 134, 
		RULE_window_function = 135, RULE_over_clause = 136, RULE_partition_by_clause = 137, 
		RULE_bracket_frame_clause = 138, RULE_rows_or_range = 139, RULE_bracket_frame_definition = 140, 
		RULE_between_frame_definition = 141, RULE_frame_edge = 142, RULE_preceding_frame_edge = 143, 
		RULE_following_frame_edge = 144, RULE_current_row_edge = 145, RULE_bracket_constraint = 146, 
		RULE_item_select_function = 147, RULE_select_direction = 148, RULE_null_handling = 149, 
		RULE_value_expression = 150, RULE_common_value_expression = 151, RULE_additive_expression = 152, 
		RULE_multiplicative_expression = 153, RULE_factor = 154, RULE_numeric_primary = 155, 
		RULE_sign = 156, RULE_extract_expression = 157, RULE_extract_field = 158, 
		RULE_time_zone_field = 159, RULE_extract_source = 160, RULE_string_value_expression = 161, 
		RULE_character_primary = 162, RULE_trim_function = 163, RULE_trim_function_name = 164, 
		RULE_trim_operands = 165, RULE_trim_specification = 166, RULE_position_function = 167, 
		RULE_position_function_name = 168, RULE_instr_function_name = 169, RULE_charindex_name = 170, 
		RULE_boolean_value_expression = 171, RULE_or_predicate = 172, RULE_and_predicate = 173, 
		RULE_negative_predicate = 174, RULE_parenthetical_predicate = 175, RULE_boolean_primary = 176, 
		RULE_predicate = 177, RULE_substitution_predicate = 178, RULE_row_value_expression = 179, 
		RULE_row_value_predicand = 180, RULE_where_clause = 181, RULE_search_condition = 182, 
		RULE_orderby_clause = 183, RULE_sort_specifier_list = 184, RULE_sort_specifier = 185, 
		RULE_order_specification = 186, RULE_null_ordering = 187, RULE_null_first_last = 188, 
		RULE_limit_clause = 189, RULE_groupby_clause = 190, RULE_grouping_element_list = 191, 
		RULE_grouping_element = 192, RULE_ordinary_grouping_set_list = 193, RULE_ordinary_grouping_set = 194, 
		RULE_rollup_list = 195, RULE_cube_list = 196, RULE_empty_grouping_set = 197, 
		RULE_having_clause = 198, RULE_qualify_clause = 199, RULE_row_value_predicand_list = 200, 
		RULE_comparison_predicate = 201, RULE_comparison_operator = 202, RULE_relative_comp_op = 203, 
		RULE_similarity_op = 204, RULE_comp_op = 205, RULE_between_predicate = 206, 
		RULE_symmetry = 207, RULE_in_predicate = 208, RULE_like_any_predicate = 209, 
		RULE_like_any_operator = 210, RULE_in_predicate_value = 211, RULE_in_value_list = 212, 
		RULE_escape_character_clause = 213, RULE_values_statement_primary = 214, 
		RULE_fully_defined_values_statement = 215, RULE_aliased_values_statement = 216, 
		RULE_values_statement = 217, RULE_values_matrix = 218, RULE_values_row = 219, 
		RULE_values_aliases = 220, RULE_values_aliases_list = 221, RULE_insert_values_statement = 222, 
		RULE_exists_predicate = 223, RULE_exists_operator = 224, RULE_exists_predicate_value = 225, 
		RULE_null_predicate = 226, RULE_is_null_clause = 227, RULE_is_clause = 228, 
		RULE_truth_value = 229, RULE_not = 230, RULE_quantified_comparison_predicate = 231, 
		RULE_quantifier = 232, RULE_all = 233, RULE_some = 234, RULE_unique_predicate = 235, 
		RULE_primary_datetime_field = 236, RULE_non_second_primary_datetime_field = 237, 
		RULE_extended_datetime_field = 238, RULE_routine_invocation = 239, RULE_function_name = 240, 
		RULE_function_names_for_reserved_words = 241, RULE_sql_argument_list = 242, 
		RULE_identifier = 243, RULE_alias_identifier = 244, RULE_variable_identifier = 245, 
		RULE_simple_identifier = 246, RULE_logical_identifier = 247, RULE_simple_variable_identifier = 248, 
		RULE_extended_variable_identifier = 249, RULE_jinja_identifier = 250, 
		RULE_jinja_function_call = 251, RULE_jinja_arg_list = 252, RULE_jinja_arg = 253, 
		RULE_jinja_variable_access = 254, RULE_jinja_name = 255, RULE_simple_numeric_identifier = 256, 
		RULE_snowflake_quoted_numeric_identifier = 257, RULE_snowflake_dollar_function_identifier = 258, 
		RULE_nonreserved_keywords = 259, RULE_signed_numeric_literal = 260, RULE_unsigned_literal = 261, 
		RULE_unsigned_numeric_literal = 262, RULE_real_number_def = 263, RULE_exponent = 264, 
		RULE_general_literal = 265, RULE_character_literal = 266, RULE_datetime_literal = 267, 
		RULE_time_literal = 268, RULE_timestamp_literal = 269, RULE_date_literal = 270, 
		RULE_boolean_literal = 271, RULE_data_type = 272, RULE_variable_size_data_type = 273, 
		RULE_variable_data_type_name = 274, RULE_type_length = 275, RULE_precision_scale_data_type = 276, 
		RULE_precision_data_type_name = 277, RULE_precision_param = 278, RULE_static_data_type = 279, 
		RULE_static_data_type_name = 280, RULE_puml_constant_identifier = 281;
	private static String[] makeRuleNames() {
		return new String[] {
			"script", "sql_statement", "ddl", "ddl_primary", "sql", "column_value", 
			"predicand_value", "in_list_predicate_value", "condition_value", "tuple_value", 
			"query_value", "join_extension_value", "literal_value", "values_statement_end", 
			"insert_end_point", "create_statement_primary", "drop_statement_primary", 
			"alter_statement_primary", "create_table_expression", "create_index_expression", 
			"create_view_expression", "create_materialized_view_expression", "create_function_expression", 
			"create_procedure_expression", "create_macro_expression", "create_sequence_expression", 
			"create_schema_expression", "create_database_expression", "create_role_expression", 
			"create_user_expression", "create_stage_expression", "create_file_format_expression", 
			"ddl_object_type", "drop_options", "alter_options", "generic_ddl_paren_content", 
			"generic_ddl_options", "with_query", "with_clause", "with_list_item", 
			"cte_body", "query_alias", "query", "insert_expression", "snowflake_insert", 
			"insert_target_table_primary", "postgres_insert", "insert_preamble", 
			"insert_source_primary", "update_expression", "returning", "assignment_expression_list", 
			"assignment_expression", "query_expression", "intersected_query", "intersect_clause", 
			"intersect_operator", "unionized_query", "union_clause", "union_operator", 
			"query_primary", "subquery", "query_specification", "into_list", "set_qualifier", 
			"select_list", "select_item", "as_clause", "select_all_columns", "wildcard_reference", 
			"from_clause", "join_extension", "table_reference_list", "join_extension_primary", 
			"lateral_modifier", "table_primary", "relation_as_clause", "tuple_primary", 
			"db_object_name", "unqualified_join", "qualified_join", "join_type", 
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
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, "' '", null, "'{{'", "'}}'"
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
			"KIND", "JOB_ID", "ALTER", "DATABASE", "FILE", "FUNCTION", "MACRO", "MATERIALIZED", 
			"PROCEDURE", "RETURNS", "ROLE", "SCHEMA", "SEQUENCE", "STAGE", "USER", 
			"VIEW", "Identifier", "EXPONEN", "Scientific_Numeric_Literal", "Numeric_Identifier", 
			"Double_Quoted_Numeric_Identifier", "Dollar_Sign_Identifier", "BlockComment", 
			"LineComment", "Character_String_Literal", "Space", "White_Space", "JINJA_OPEN", 
			"JINJA_CLOSE", "BAD"
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
	public static class ScriptContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(SQLSelectParserParser.EOF, 0); }
		public List<Sql_statementContext> sql_statement() {
			return getRuleContexts(Sql_statementContext.class);
		}
		public Sql_statementContext sql_statement(int i) {
			return getRuleContext(Sql_statementContext.class,i);
		}
		public ScriptContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_script; }
	}

	public final ScriptContext script() throws RecognitionException {
		ScriptContext _localctx = new ScriptContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_script);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(567);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(564);
					sql_statement();
					}
					} 
				}
				setState(569);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			setState(571);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 288265560523801088L) != 0) || _la==DROP || _la==INSERT || _la==UPDATE || ((((_la - 286)) & ~0x3f) == 0 && ((1L << (_la - 286)) & 7696581394433L) != 0) || _la==ALTER) {
				{
				setState(570);
				sql_statement();
				}
			}

			setState(573);
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
	public static class Sql_statementContext extends ParserRuleContext {
		public Ddl_primaryContext ddl_primary() {
			return getRuleContext(Ddl_primaryContext.class,0);
		}
		public With_queryContext with_query() {
			return getRuleContext(With_queryContext.class,0);
		}
		public QueryContext query() {
			return getRuleContext(QueryContext.class,0);
		}
		public TerminalNode SEMI_COLON() { return getToken(SQLSelectParserParser.SEMI_COLON, 0); }
		public Sql_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sql_statement; }
	}

	public final Sql_statementContext sql_statement() throws RecognitionException {
		Sql_statementContext _localctx = new Sql_statementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_sql_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(578);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CREATE:
			case DROP:
			case ALTER:
				{
				setState(575);
				ddl_primary();
				}
				break;
			case WITH:
				{
				setState(576);
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
				{
				setState(577);
				query();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(581);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEMI_COLON) {
				{
				setState(580);
				match(SEMI_COLON);
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
	public static class DdlContext extends ParserRuleContext {
		public Ddl_primaryContext ddl_primary() {
			return getRuleContext(Ddl_primaryContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SQLSelectParserParser.EOF, 0); }
		public DdlContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ddl; }
	}

	public final DdlContext ddl() throws RecognitionException {
		DdlContext _localctx = new DdlContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_ddl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(583);
			ddl_primary();
			setState(584);
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
	public static class Ddl_primaryContext extends ParserRuleContext {
		public Create_statement_primaryContext create_statement_primary() {
			return getRuleContext(Create_statement_primaryContext.class,0);
		}
		public Drop_statement_primaryContext drop_statement_primary() {
			return getRuleContext(Drop_statement_primaryContext.class,0);
		}
		public Alter_statement_primaryContext alter_statement_primary() {
			return getRuleContext(Alter_statement_primaryContext.class,0);
		}
		public TerminalNode SEMI_COLON() { return getToken(SQLSelectParserParser.SEMI_COLON, 0); }
		public Ddl_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ddl_primary; }
	}

	public final Ddl_primaryContext ddl_primary() throws RecognitionException {
		Ddl_primaryContext _localctx = new Ddl_primaryContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_ddl_primary);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(589);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CREATE:
				{
				setState(586);
				create_statement_primary();
				}
				break;
			case DROP:
				{
				setState(587);
				drop_statement_primary();
				}
				break;
			case ALTER:
				{
				setState(588);
				alter_statement_primary();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(592);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(591);
				match(SEMI_COLON);
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
	public static class SqlContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(SQLSelectParserParser.EOF, 0); }
		public With_queryContext with_query() {
			return getRuleContext(With_queryContext.class,0);
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
		enterRule(_localctx, 8, RULE_sql);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(596);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case WITH:
				{
				setState(594);
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
				{
				setState(595);
				query();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(599);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEMI_COLON) {
				{
				setState(598);
				match(SEMI_COLON);
				}
			}

			setState(601);
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
		enterRule(_localctx, 10, RULE_column_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(603);
			column_primary();
			setState(604);
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
		enterRule(_localctx, 12, RULE_predicand_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(606);
			predicand_primary();
			setState(607);
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
		enterRule(_localctx, 14, RULE_in_list_predicate_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(609);
			in_predicate_value();
			setState(610);
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
		enterRule(_localctx, 16, RULE_condition_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(612);
			value_expression();
			setState(613);
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
		enterRule(_localctx, 18, RULE_tuple_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(615);
			tuple_primary();
			setState(616);
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
		enterRule(_localctx, 20, RULE_query_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(618);
			query();
			setState(619);
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
		enterRule(_localctx, 22, RULE_join_extension_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(621);
			join_extension_primary();
			setState(622);
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
		enterRule(_localctx, 24, RULE_literal_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(626);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				{
				setState(624);
				signed_numeric_literal();
				}
				break;
			case 2:
				{
				setState(625);
				unsigned_literal();
				}
				break;
			}
			setState(628);
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
		enterRule(_localctx, 26, RULE_values_statement_end);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(630);
			values_statement_primary();
			setState(631);
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
		enterRule(_localctx, 28, RULE_insert_end_point);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(633);
			insert_expression();
			setState(634);
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
	public static class Create_statement_primaryContext extends ParserRuleContext {
		public Create_table_expressionContext create_table_expression() {
			return getRuleContext(Create_table_expressionContext.class,0);
		}
		public Create_index_expressionContext create_index_expression() {
			return getRuleContext(Create_index_expressionContext.class,0);
		}
		public Create_view_expressionContext create_view_expression() {
			return getRuleContext(Create_view_expressionContext.class,0);
		}
		public Create_materialized_view_expressionContext create_materialized_view_expression() {
			return getRuleContext(Create_materialized_view_expressionContext.class,0);
		}
		public Create_function_expressionContext create_function_expression() {
			return getRuleContext(Create_function_expressionContext.class,0);
		}
		public Create_procedure_expressionContext create_procedure_expression() {
			return getRuleContext(Create_procedure_expressionContext.class,0);
		}
		public Create_macro_expressionContext create_macro_expression() {
			return getRuleContext(Create_macro_expressionContext.class,0);
		}
		public Create_sequence_expressionContext create_sequence_expression() {
			return getRuleContext(Create_sequence_expressionContext.class,0);
		}
		public Create_schema_expressionContext create_schema_expression() {
			return getRuleContext(Create_schema_expressionContext.class,0);
		}
		public Create_database_expressionContext create_database_expression() {
			return getRuleContext(Create_database_expressionContext.class,0);
		}
		public Create_role_expressionContext create_role_expression() {
			return getRuleContext(Create_role_expressionContext.class,0);
		}
		public Create_user_expressionContext create_user_expression() {
			return getRuleContext(Create_user_expressionContext.class,0);
		}
		public Create_stage_expressionContext create_stage_expression() {
			return getRuleContext(Create_stage_expressionContext.class,0);
		}
		public Create_file_format_expressionContext create_file_format_expression() {
			return getRuleContext(Create_file_format_expressionContext.class,0);
		}
		public Create_statement_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_statement_primary; }
	}

	public final Create_statement_primaryContext create_statement_primary() throws RecognitionException {
		Create_statement_primaryContext _localctx = new Create_statement_primaryContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_create_statement_primary);
		try {
			setState(650);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(636);
				create_table_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(637);
				create_index_expression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(638);
				create_view_expression();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(639);
				create_materialized_view_expression();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(640);
				create_function_expression();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(641);
				create_procedure_expression();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(642);
				create_macro_expression();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(643);
				create_sequence_expression();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(644);
				create_schema_expression();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(645);
				create_database_expression();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(646);
				create_role_expression();
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(647);
				create_user_expression();
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(648);
				create_stage_expression();
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(649);
				create_file_format_expression();
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
	public static class Drop_statement_primaryContext extends ParserRuleContext {
		public TerminalNode DROP() { return getToken(SQLSelectParserParser.DROP, 0); }
		public Ddl_object_typeContext ddl_object_type() {
			return getRuleContext(Ddl_object_typeContext.class,0);
		}
		public Db_object_nameContext db_object_name() {
			return getRuleContext(Db_object_nameContext.class,0);
		}
		public Drop_optionsContext drop_options() {
			return getRuleContext(Drop_optionsContext.class,0);
		}
		public Drop_statement_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_drop_statement_primary; }
	}

	public final Drop_statement_primaryContext drop_statement_primary() throws RecognitionException {
		Drop_statement_primaryContext _localctx = new Drop_statement_primaryContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_drop_statement_primary);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(652);
			match(DROP);
			setState(653);
			ddl_object_type();
			setState(654);
			db_object_name();
			setState(656);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				{
				setState(655);
				drop_options();
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
	public static class Alter_statement_primaryContext extends ParserRuleContext {
		public TerminalNode ALTER() { return getToken(SQLSelectParserParser.ALTER, 0); }
		public Ddl_object_typeContext ddl_object_type() {
			return getRuleContext(Ddl_object_typeContext.class,0);
		}
		public Db_object_nameContext db_object_name() {
			return getRuleContext(Db_object_nameContext.class,0);
		}
		public Alter_optionsContext alter_options() {
			return getRuleContext(Alter_optionsContext.class,0);
		}
		public Alter_statement_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alter_statement_primary; }
	}

	public final Alter_statement_primaryContext alter_statement_primary() throws RecognitionException {
		Alter_statement_primaryContext _localctx = new Alter_statement_primaryContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_alter_statement_primary);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(658);
			match(ALTER);
			setState(659);
			ddl_object_type();
			setState(660);
			db_object_name();
			setState(662);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				{
				setState(661);
				alter_options();
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
	public static class Create_table_expressionContext extends ParserRuleContext {
		public TerminalNode CREATE() { return getToken(SQLSelectParserParser.CREATE, 0); }
		public TerminalNode TABLE() { return getToken(SQLSelectParserParser.TABLE, 0); }
		public Db_object_nameContext db_object_name() {
			return getRuleContext(Db_object_nameContext.class,0);
		}
		public TerminalNode AS() { return getToken(SQLSelectParserParser.AS, 0); }
		public Query_expressionContext query_expression() {
			return getRuleContext(Query_expressionContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public Generic_ddl_paren_contentContext generic_ddl_paren_content() {
			return getRuleContext(Generic_ddl_paren_contentContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Generic_ddl_optionsContext generic_ddl_options() {
			return getRuleContext(Generic_ddl_optionsContext.class,0);
		}
		public Create_table_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_table_expression; }
	}

	public final Create_table_expressionContext create_table_expression() throws RecognitionException {
		Create_table_expressionContext _localctx = new Create_table_expressionContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_create_table_expression);
		try {
			setState(682);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(664);
				match(CREATE);
				setState(665);
				match(TABLE);
				setState(666);
				db_object_name();
				setState(667);
				match(AS);
				setState(668);
				query_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(670);
				match(CREATE);
				setState(671);
				match(TABLE);
				setState(672);
				db_object_name();
				setState(677);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
				case 1:
					{
					setState(673);
					match(LEFT_PAREN);
					setState(674);
					generic_ddl_paren_content();
					setState(675);
					match(RIGHT_PAREN);
					}
					break;
				}
				setState(680);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
				case 1:
					{
					setState(679);
					generic_ddl_options();
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
	public static class Create_index_expressionContext extends ParserRuleContext {
		public TerminalNode CREATE() { return getToken(SQLSelectParserParser.CREATE, 0); }
		public TerminalNode INDEX() { return getToken(SQLSelectParserParser.INDEX, 0); }
		public List<Db_object_nameContext> db_object_name() {
			return getRuleContexts(Db_object_nameContext.class);
		}
		public Db_object_nameContext db_object_name(int i) {
			return getRuleContext(Db_object_nameContext.class,i);
		}
		public TerminalNode ON() { return getToken(SQLSelectParserParser.ON, 0); }
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public Column_reference_listContext column_reference_list() {
			return getRuleContext(Column_reference_listContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Create_index_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_index_expression; }
	}

	public final Create_index_expressionContext create_index_expression() throws RecognitionException {
		Create_index_expressionContext _localctx = new Create_index_expressionContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_create_index_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(684);
			match(CREATE);
			setState(685);
			match(INDEX);
			setState(686);
			db_object_name();
			setState(687);
			match(ON);
			setState(688);
			db_object_name();
			setState(689);
			match(LEFT_PAREN);
			setState(690);
			column_reference_list();
			setState(691);
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
	public static class Create_view_expressionContext extends ParserRuleContext {
		public TerminalNode CREATE() { return getToken(SQLSelectParserParser.CREATE, 0); }
		public TerminalNode VIEW() { return getToken(SQLSelectParserParser.VIEW, 0); }
		public Db_object_nameContext db_object_name() {
			return getRuleContext(Db_object_nameContext.class,0);
		}
		public TerminalNode AS() { return getToken(SQLSelectParserParser.AS, 0); }
		public Query_expressionContext query_expression() {
			return getRuleContext(Query_expressionContext.class,0);
		}
		public Create_view_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_view_expression; }
	}

	public final Create_view_expressionContext create_view_expression() throws RecognitionException {
		Create_view_expressionContext _localctx = new Create_view_expressionContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_create_view_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(693);
			match(CREATE);
			setState(694);
			match(VIEW);
			setState(695);
			db_object_name();
			setState(696);
			match(AS);
			setState(697);
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
	public static class Create_materialized_view_expressionContext extends ParserRuleContext {
		public TerminalNode CREATE() { return getToken(SQLSelectParserParser.CREATE, 0); }
		public TerminalNode MATERIALIZED() { return getToken(SQLSelectParserParser.MATERIALIZED, 0); }
		public TerminalNode VIEW() { return getToken(SQLSelectParserParser.VIEW, 0); }
		public Db_object_nameContext db_object_name() {
			return getRuleContext(Db_object_nameContext.class,0);
		}
		public TerminalNode AS() { return getToken(SQLSelectParserParser.AS, 0); }
		public Query_expressionContext query_expression() {
			return getRuleContext(Query_expressionContext.class,0);
		}
		public Create_materialized_view_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_materialized_view_expression; }
	}

	public final Create_materialized_view_expressionContext create_materialized_view_expression() throws RecognitionException {
		Create_materialized_view_expressionContext _localctx = new Create_materialized_view_expressionContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_create_materialized_view_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(699);
			match(CREATE);
			setState(700);
			match(MATERIALIZED);
			setState(701);
			match(VIEW);
			setState(702);
			db_object_name();
			setState(703);
			match(AS);
			setState(704);
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
	public static class Create_function_expressionContext extends ParserRuleContext {
		public TerminalNode CREATE() { return getToken(SQLSelectParserParser.CREATE, 0); }
		public TerminalNode FUNCTION() { return getToken(SQLSelectParserParser.FUNCTION, 0); }
		public Db_object_nameContext db_object_name() {
			return getRuleContext(Db_object_nameContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public TerminalNode RETURNS() { return getToken(SQLSelectParserParser.RETURNS, 0); }
		public Data_typeContext data_type() {
			return getRuleContext(Data_typeContext.class,0);
		}
		public Generic_ddl_optionsContext generic_ddl_options() {
			return getRuleContext(Generic_ddl_optionsContext.class,0);
		}
		public Generic_ddl_paren_contentContext generic_ddl_paren_content() {
			return getRuleContext(Generic_ddl_paren_contentContext.class,0);
		}
		public Create_function_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_function_expression; }
	}

	public final Create_function_expressionContext create_function_expression() throws RecognitionException {
		Create_function_expressionContext _localctx = new Create_function_expressionContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_create_function_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(706);
			match(CREATE);
			setState(707);
			match(FUNCTION);
			setState(708);
			db_object_name();
			setState(709);
			match(LEFT_PAREN);
			setState(711);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -2L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -1L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -1L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -1L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & -2147483649L) != 0) || ((((_la - 320)) & ~0x3f) == 0 && ((1L << (_la - 320)) & -1L) != 0) || ((((_la - 384)) & ~0x3f) == 0 && ((1L << (_la - 384)) & 7L) != 0)) {
				{
				setState(710);
				generic_ddl_paren_content();
				}
			}

			setState(713);
			match(RIGHT_PAREN);
			setState(714);
			match(RETURNS);
			setState(715);
			data_type();
			setState(716);
			generic_ddl_options();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Create_procedure_expressionContext extends ParserRuleContext {
		public TerminalNode CREATE() { return getToken(SQLSelectParserParser.CREATE, 0); }
		public TerminalNode PROCEDURE() { return getToken(SQLSelectParserParser.PROCEDURE, 0); }
		public Db_object_nameContext db_object_name() {
			return getRuleContext(Db_object_nameContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Generic_ddl_optionsContext generic_ddl_options() {
			return getRuleContext(Generic_ddl_optionsContext.class,0);
		}
		public Generic_ddl_paren_contentContext generic_ddl_paren_content() {
			return getRuleContext(Generic_ddl_paren_contentContext.class,0);
		}
		public Create_procedure_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_procedure_expression; }
	}

	public final Create_procedure_expressionContext create_procedure_expression() throws RecognitionException {
		Create_procedure_expressionContext _localctx = new Create_procedure_expressionContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_create_procedure_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(718);
			match(CREATE);
			setState(719);
			match(PROCEDURE);
			setState(720);
			db_object_name();
			setState(721);
			match(LEFT_PAREN);
			setState(723);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -2L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -1L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -1L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -1L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & -2147483649L) != 0) || ((((_la - 320)) & ~0x3f) == 0 && ((1L << (_la - 320)) & -1L) != 0) || ((((_la - 384)) & ~0x3f) == 0 && ((1L << (_la - 384)) & 7L) != 0)) {
				{
				setState(722);
				generic_ddl_paren_content();
				}
			}

			setState(725);
			match(RIGHT_PAREN);
			setState(726);
			generic_ddl_options();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Create_macro_expressionContext extends ParserRuleContext {
		public TerminalNode CREATE() { return getToken(SQLSelectParserParser.CREATE, 0); }
		public TerminalNode MACRO() { return getToken(SQLSelectParserParser.MACRO, 0); }
		public Db_object_nameContext db_object_name() {
			return getRuleContext(Db_object_nameContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public TerminalNode AS() { return getToken(SQLSelectParserParser.AS, 0); }
		public Query_expressionContext query_expression() {
			return getRuleContext(Query_expressionContext.class,0);
		}
		public Generic_ddl_paren_contentContext generic_ddl_paren_content() {
			return getRuleContext(Generic_ddl_paren_contentContext.class,0);
		}
		public Create_macro_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_macro_expression; }
	}

	public final Create_macro_expressionContext create_macro_expression() throws RecognitionException {
		Create_macro_expressionContext _localctx = new Create_macro_expressionContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_create_macro_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(728);
			match(CREATE);
			setState(729);
			match(MACRO);
			setState(730);
			db_object_name();
			setState(731);
			match(LEFT_PAREN);
			setState(733);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -2L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -1L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -1L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -1L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & -2147483649L) != 0) || ((((_la - 320)) & ~0x3f) == 0 && ((1L << (_la - 320)) & -1L) != 0) || ((((_la - 384)) & ~0x3f) == 0 && ((1L << (_la - 384)) & 7L) != 0)) {
				{
				setState(732);
				generic_ddl_paren_content();
				}
			}

			setState(735);
			match(RIGHT_PAREN);
			setState(736);
			match(AS);
			setState(737);
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
	public static class Create_sequence_expressionContext extends ParserRuleContext {
		public TerminalNode CREATE() { return getToken(SQLSelectParserParser.CREATE, 0); }
		public TerminalNode SEQUENCE() { return getToken(SQLSelectParserParser.SEQUENCE, 0); }
		public Db_object_nameContext db_object_name() {
			return getRuleContext(Db_object_nameContext.class,0);
		}
		public Generic_ddl_optionsContext generic_ddl_options() {
			return getRuleContext(Generic_ddl_optionsContext.class,0);
		}
		public Create_sequence_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_sequence_expression; }
	}

	public final Create_sequence_expressionContext create_sequence_expression() throws RecognitionException {
		Create_sequence_expressionContext _localctx = new Create_sequence_expressionContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_create_sequence_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(739);
			match(CREATE);
			setState(740);
			match(SEQUENCE);
			setState(741);
			db_object_name();
			setState(742);
			generic_ddl_options();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Create_schema_expressionContext extends ParserRuleContext {
		public TerminalNode CREATE() { return getToken(SQLSelectParserParser.CREATE, 0); }
		public TerminalNode SCHEMA() { return getToken(SQLSelectParserParser.SCHEMA, 0); }
		public Db_object_nameContext db_object_name() {
			return getRuleContext(Db_object_nameContext.class,0);
		}
		public Generic_ddl_optionsContext generic_ddl_options() {
			return getRuleContext(Generic_ddl_optionsContext.class,0);
		}
		public Create_schema_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_schema_expression; }
	}

	public final Create_schema_expressionContext create_schema_expression() throws RecognitionException {
		Create_schema_expressionContext _localctx = new Create_schema_expressionContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_create_schema_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(744);
			match(CREATE);
			setState(745);
			match(SCHEMA);
			setState(746);
			db_object_name();
			setState(748);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				{
				setState(747);
				generic_ddl_options();
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
	public static class Create_database_expressionContext extends ParserRuleContext {
		public TerminalNode CREATE() { return getToken(SQLSelectParserParser.CREATE, 0); }
		public TerminalNode DATABASE() { return getToken(SQLSelectParserParser.DATABASE, 0); }
		public Db_object_nameContext db_object_name() {
			return getRuleContext(Db_object_nameContext.class,0);
		}
		public Generic_ddl_optionsContext generic_ddl_options() {
			return getRuleContext(Generic_ddl_optionsContext.class,0);
		}
		public Create_database_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_database_expression; }
	}

	public final Create_database_expressionContext create_database_expression() throws RecognitionException {
		Create_database_expressionContext _localctx = new Create_database_expressionContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_create_database_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(750);
			match(CREATE);
			setState(751);
			match(DATABASE);
			setState(752);
			db_object_name();
			setState(754);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				{
				setState(753);
				generic_ddl_options();
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
	public static class Create_role_expressionContext extends ParserRuleContext {
		public TerminalNode CREATE() { return getToken(SQLSelectParserParser.CREATE, 0); }
		public TerminalNode ROLE() { return getToken(SQLSelectParserParser.ROLE, 0); }
		public Db_object_nameContext db_object_name() {
			return getRuleContext(Db_object_nameContext.class,0);
		}
		public Generic_ddl_optionsContext generic_ddl_options() {
			return getRuleContext(Generic_ddl_optionsContext.class,0);
		}
		public Create_role_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_role_expression; }
	}

	public final Create_role_expressionContext create_role_expression() throws RecognitionException {
		Create_role_expressionContext _localctx = new Create_role_expressionContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_create_role_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(756);
			match(CREATE);
			setState(757);
			match(ROLE);
			setState(758);
			db_object_name();
			setState(760);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				{
				setState(759);
				generic_ddl_options();
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
	public static class Create_user_expressionContext extends ParserRuleContext {
		public TerminalNode CREATE() { return getToken(SQLSelectParserParser.CREATE, 0); }
		public TerminalNode USER() { return getToken(SQLSelectParserParser.USER, 0); }
		public Db_object_nameContext db_object_name() {
			return getRuleContext(Db_object_nameContext.class,0);
		}
		public Generic_ddl_optionsContext generic_ddl_options() {
			return getRuleContext(Generic_ddl_optionsContext.class,0);
		}
		public Create_user_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_user_expression; }
	}

	public final Create_user_expressionContext create_user_expression() throws RecognitionException {
		Create_user_expressionContext _localctx = new Create_user_expressionContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_create_user_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(762);
			match(CREATE);
			setState(763);
			match(USER);
			setState(764);
			db_object_name();
			setState(766);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				{
				setState(765);
				generic_ddl_options();
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
	public static class Create_stage_expressionContext extends ParserRuleContext {
		public TerminalNode CREATE() { return getToken(SQLSelectParserParser.CREATE, 0); }
		public TerminalNode STAGE() { return getToken(SQLSelectParserParser.STAGE, 0); }
		public Db_object_nameContext db_object_name() {
			return getRuleContext(Db_object_nameContext.class,0);
		}
		public Generic_ddl_optionsContext generic_ddl_options() {
			return getRuleContext(Generic_ddl_optionsContext.class,0);
		}
		public Create_stage_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_stage_expression; }
	}

	public final Create_stage_expressionContext create_stage_expression() throws RecognitionException {
		Create_stage_expressionContext _localctx = new Create_stage_expressionContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_create_stage_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(768);
			match(CREATE);
			setState(769);
			match(STAGE);
			setState(770);
			db_object_name();
			setState(772);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				{
				setState(771);
				generic_ddl_options();
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
	public static class Create_file_format_expressionContext extends ParserRuleContext {
		public TerminalNode CREATE() { return getToken(SQLSelectParserParser.CREATE, 0); }
		public TerminalNode FILE() { return getToken(SQLSelectParserParser.FILE, 0); }
		public TerminalNode FORMAT() { return getToken(SQLSelectParserParser.FORMAT, 0); }
		public Db_object_nameContext db_object_name() {
			return getRuleContext(Db_object_nameContext.class,0);
		}
		public Generic_ddl_optionsContext generic_ddl_options() {
			return getRuleContext(Generic_ddl_optionsContext.class,0);
		}
		public Create_file_format_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_file_format_expression; }
	}

	public final Create_file_format_expressionContext create_file_format_expression() throws RecognitionException {
		Create_file_format_expressionContext _localctx = new Create_file_format_expressionContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_create_file_format_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(774);
			match(CREATE);
			setState(775);
			match(FILE);
			setState(776);
			match(FORMAT);
			setState(777);
			db_object_name();
			setState(779);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				{
				setState(778);
				generic_ddl_options();
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
	public static class Ddl_object_typeContext extends ParserRuleContext {
		public TerminalNode TABLE() { return getToken(SQLSelectParserParser.TABLE, 0); }
		public TerminalNode INDEX() { return getToken(SQLSelectParserParser.INDEX, 0); }
		public TerminalNode VIEW() { return getToken(SQLSelectParserParser.VIEW, 0); }
		public TerminalNode FUNCTION() { return getToken(SQLSelectParserParser.FUNCTION, 0); }
		public TerminalNode PROCEDURE() { return getToken(SQLSelectParserParser.PROCEDURE, 0); }
		public TerminalNode MACRO() { return getToken(SQLSelectParserParser.MACRO, 0); }
		public TerminalNode SEQUENCE() { return getToken(SQLSelectParserParser.SEQUENCE, 0); }
		public TerminalNode SCHEMA() { return getToken(SQLSelectParserParser.SCHEMA, 0); }
		public TerminalNode DATABASE() { return getToken(SQLSelectParserParser.DATABASE, 0); }
		public TerminalNode ROLE() { return getToken(SQLSelectParserParser.ROLE, 0); }
		public TerminalNode USER() { return getToken(SQLSelectParserParser.USER, 0); }
		public TerminalNode STAGE() { return getToken(SQLSelectParserParser.STAGE, 0); }
		public TerminalNode FILE() { return getToken(SQLSelectParserParser.FILE, 0); }
		public TerminalNode FORMAT() { return getToken(SQLSelectParserParser.FORMAT, 0); }
		public TerminalNode MATERIALIZED() { return getToken(SQLSelectParserParser.MATERIALIZED, 0); }
		public Ddl_object_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ddl_object_type; }
	}

	public final Ddl_object_typeContext ddl_object_type() throws RecognitionException {
		Ddl_object_typeContext _localctx = new Ddl_object_typeContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_ddl_object_type);
		try {
			setState(797);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TABLE:
				enterOuterAlt(_localctx, 1);
				{
				setState(781);
				match(TABLE);
				}
				break;
			case INDEX:
				enterOuterAlt(_localctx, 2);
				{
				setState(782);
				match(INDEX);
				}
				break;
			case VIEW:
				enterOuterAlt(_localctx, 3);
				{
				setState(783);
				match(VIEW);
				}
				break;
			case FUNCTION:
				enterOuterAlt(_localctx, 4);
				{
				setState(784);
				match(FUNCTION);
				}
				break;
			case PROCEDURE:
				enterOuterAlt(_localctx, 5);
				{
				setState(785);
				match(PROCEDURE);
				}
				break;
			case MACRO:
				enterOuterAlt(_localctx, 6);
				{
				setState(786);
				match(MACRO);
				}
				break;
			case SEQUENCE:
				enterOuterAlt(_localctx, 7);
				{
				setState(787);
				match(SEQUENCE);
				}
				break;
			case SCHEMA:
				enterOuterAlt(_localctx, 8);
				{
				setState(788);
				match(SCHEMA);
				}
				break;
			case DATABASE:
				enterOuterAlt(_localctx, 9);
				{
				setState(789);
				match(DATABASE);
				}
				break;
			case ROLE:
				enterOuterAlt(_localctx, 10);
				{
				setState(790);
				match(ROLE);
				}
				break;
			case USER:
				enterOuterAlt(_localctx, 11);
				{
				setState(791);
				match(USER);
				}
				break;
			case STAGE:
				enterOuterAlt(_localctx, 12);
				{
				setState(792);
				match(STAGE);
				}
				break;
			case FILE:
				enterOuterAlt(_localctx, 13);
				{
				setState(793);
				match(FILE);
				setState(794);
				match(FORMAT);
				}
				break;
			case MATERIALIZED:
				enterOuterAlt(_localctx, 14);
				{
				setState(795);
				match(MATERIALIZED);
				setState(796);
				match(VIEW);
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
	public static class Drop_optionsContext extends ParserRuleContext {
		public Generic_ddl_optionsContext generic_ddl_options() {
			return getRuleContext(Generic_ddl_optionsContext.class,0);
		}
		public Drop_optionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_drop_options; }
	}

	public final Drop_optionsContext drop_options() throws RecognitionException {
		Drop_optionsContext _localctx = new Drop_optionsContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_drop_options);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(799);
			generic_ddl_options();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Alter_optionsContext extends ParserRuleContext {
		public Generic_ddl_optionsContext generic_ddl_options() {
			return getRuleContext(Generic_ddl_optionsContext.class,0);
		}
		public Alter_optionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alter_options; }
	}

	public final Alter_optionsContext alter_options() throws RecognitionException {
		Alter_optionsContext _localctx = new Alter_optionsContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_alter_options);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(801);
			generic_ddl_options();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Generic_ddl_paren_contentContext extends ParserRuleContext {
		public List<TerminalNode> RIGHT_PAREN() { return getTokens(SQLSelectParserParser.RIGHT_PAREN); }
		public TerminalNode RIGHT_PAREN(int i) {
			return getToken(SQLSelectParserParser.RIGHT_PAREN, i);
		}
		public Generic_ddl_paren_contentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_generic_ddl_paren_content; }
	}

	public final Generic_ddl_paren_contentContext generic_ddl_paren_content() throws RecognitionException {
		Generic_ddl_paren_contentContext _localctx = new Generic_ddl_paren_contentContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_generic_ddl_paren_content);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(804); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(803);
				_la = _input.LA(1);
				if ( _la <= 0 || (_la==RIGHT_PAREN) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				}
				setState(806); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & -2L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -1L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -1L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -1L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & -2147483649L) != 0) || ((((_la - 320)) & ~0x3f) == 0 && ((1L << (_la - 320)) & -1L) != 0) || ((((_la - 384)) & ~0x3f) == 0 && ((1L << (_la - 384)) & 7L) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Generic_ddl_optionsContext extends ParserRuleContext {
		public List<TerminalNode> SEMI_COLON() { return getTokens(SQLSelectParserParser.SEMI_COLON); }
		public TerminalNode SEMI_COLON(int i) {
			return getToken(SQLSelectParserParser.SEMI_COLON, i);
		}
		public Generic_ddl_optionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_generic_ddl_options; }
	}

	public final Generic_ddl_optionsContext generic_ddl_options() throws RecognitionException {
		Generic_ddl_optionsContext _localctx = new Generic_ddl_optionsContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_generic_ddl_options);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(809); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(808);
					_la = _input.LA(1);
					if ( _la <= 0 || (_la==SEMI_COLON) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(811); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
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
		enterRule(_localctx, 74, RULE_with_query);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(813);
			with_clause();
			setState(814);
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
		enterRule(_localctx, 76, RULE_with_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(816);
			match(WITH);
			setState(817);
			with_list_item();
			setState(822);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(818);
				match(COMMA);
				setState(819);
				with_list_item();
				}
				}
				setState(824);
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
		enterRule(_localctx, 78, RULE_with_list_item);
		try {
			setState(833);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(825);
				query_alias();
				{
				setState(826);
				match(LEFT_PAREN);
				setState(827);
				cte_body();
				setState(828);
				match(RIGHT_PAREN);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(830);
				query_alias();
				setState(831);
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
		enterRule(_localctx, 80, RULE_cte_body);
		try {
			setState(837);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case WITH:
				enterOuterAlt(_localctx, 1);
				{
				setState(835);
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
				setState(836);
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
		enterRule(_localctx, 82, RULE_query_alias);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(839);
			identifier();
			setState(840);
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
		enterRule(_localctx, 84, RULE_query);
		try {
			setState(846);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(842);
				query_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(843);
				insert_expression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(844);
				update_expression();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(845);
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
		enterRule(_localctx, 86, RULE_insert_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(848);
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
		enterRule(_localctx, 88, RULE_snowflake_insert);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(850);
			insert_preamble();
			setState(851);
			insert_target_table_primary();
			setState(852);
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
		public Db_object_nameContext db_object_name() {
			return getRuleContext(Db_object_nameContext.class,0);
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
		enterRule(_localctx, 90, RULE_insert_target_table_primary);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(857);
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
			case ALTER:
			case DATABASE:
			case FILE:
			case FUNCTION:
			case MACRO:
			case MATERIALIZED:
			case PROCEDURE:
			case RETURNS:
			case ROLE:
			case SCHEMA:
			case SEQUENCE:
			case STAGE:
			case USER:
			case VIEW:
			case Identifier:
			case Double_Quoted_Numeric_Identifier:
			case Dollar_Sign_Identifier:
				{
				setState(854);
				db_object_name();
				}
				break;
			case Variable_Identifier:
			case Extended_Variable_Identifier:
			case Mixed_Variable_Identifier:
				{
				setState(855);
				variable_identifier();
				}
				break;
			case JINJA_OPEN:
				{
				setState(856);
				jinja_identifier();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(863);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
			case 1:
				{
				setState(859);
				match(LEFT_PAREN);
				setState(860);
				column_reference_list();
				setState(861);
				match(RIGHT_PAREN);
				}
				break;
			}
			setState(866);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				{
				setState(865);
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
		enterRule(_localctx, 92, RULE_postgres_insert);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(868);
			snowflake_insert();
			setState(870);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==RETURNING) {
				{
				setState(869);
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
		enterRule(_localctx, 94, RULE_insert_preamble);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(872);
			match(INSERT);
			setState(874);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OVERWRITE) {
				{
				setState(873);
				match(OVERWRITE);
				}
			}

			setState(876);
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
		enterRule(_localctx, 96, RULE_insert_source_primary);
		try {
			setState(881);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(878);
				query_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(879);
				variable_identifier();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(880);
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
		enterRule(_localctx, 98, RULE_update_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(883);
			match(UPDATE);
			setState(884);
			table_primary();
			setState(885);
			match(SET);
			setState(886);
			assignment_expression_list();
			setState(888);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FROM) {
				{
				setState(887);
				from_clause();
				}
			}

			setState(891);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(890);
				where_clause();
				}
			}

			setState(894);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==RETURNING) {
				{
				setState(893);
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
		enterRule(_localctx, 100, RULE_returning);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(896);
			match(RETURNING);
			setState(897);
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
		enterRule(_localctx, 102, RULE_assignment_expression_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(899);
			assignment_expression();
			setState(904);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(900);
				match(COMMA);
				setState(901);
				assignment_expression();
				}
				}
				setState(906);
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
		enterRule(_localctx, 104, RULE_assignment_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(907);
			column_reference();
			setState(908);
			match(EQUAL);
			setState(909);
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
		enterRule(_localctx, 106, RULE_query_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(911);
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
		enterRule(_localctx, 108, RULE_intersected_query);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(913);
			unionized_query();
			setState(919);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INTERSECT) {
				{
				{
				setState(914);
				intersect_clause();
				setState(915);
				unionized_query();
				}
				}
				setState(921);
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
		enterRule(_localctx, 110, RULE_intersect_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(922);
			intersect_operator();
			setState(924);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ALL || _la==DISTINCT) {
				{
				setState(923);
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
		enterRule(_localctx, 112, RULE_intersect_operator);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(926);
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
		enterRule(_localctx, 114, RULE_unionized_query);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(928);
			query_primary();
			setState(934);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EXCEPT || _la==UNION) {
				{
				{
				setState(929);
				union_clause();
				setState(930);
				query_primary();
				}
				}
				setState(936);
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
		enterRule(_localctx, 116, RULE_union_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(937);
			union_operator();
			setState(939);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ALL || _la==DISTINCT) {
				{
				setState(938);
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
		enterRule(_localctx, 118, RULE_union_operator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(941);
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
		enterRule(_localctx, 120, RULE_query_primary);
		try {
			setState(946);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LEFT_PAREN:
				enterOuterAlt(_localctx, 1);
				{
				setState(943);
				subquery();
				}
				break;
			case SELECT:
				enterOuterAlt(_localctx, 2);
				{
				setState(944);
				query_specification();
				}
				break;
			case Variable_Identifier:
			case Extended_Variable_Identifier:
			case Mixed_Variable_Identifier:
				enterOuterAlt(_localctx, 3);
				{
				setState(945);
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
		enterRule(_localctx, 122, RULE_subquery);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(948);
			match(LEFT_PAREN);
			setState(949);
			query_expression();
			setState(950);
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
		enterRule(_localctx, 124, RULE_query_specification);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(952);
			match(SELECT);
			setState(954);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==INTO) {
				{
				setState(953);
				into_list();
				}
			}

			setState(957);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ALL || _la==DISTINCT) {
				{
				setState(956);
				set_qualifier();
				}
			}

			setState(959);
			select_list();
			setState(979);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FROM) {
				{
				setState(960);
				from_clause();
				setState(962);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WHERE) {
					{
					setState(961);
					where_clause();
					}
				}

				setState(965);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==GROUP) {
					{
					setState(964);
					groupby_clause();
					}
				}

				setState(968);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HAVING) {
					{
					setState(967);
					having_clause();
					}
				}

				setState(971);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==QUALIFY) {
					{
					setState(970);
					qualify_clause();
					}
				}

				setState(974);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ORDER) {
					{
					setState(973);
					orderby_clause();
					}
				}

				setState(977);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LIMIT) {
					{
					setState(976);
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
		public Db_object_nameContext db_object_name() {
			return getRuleContext(Db_object_nameContext.class,0);
		}
		public Into_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_into_list; }
	}

	public final Into_listContext into_list() throws RecognitionException {
		Into_listContext _localctx = new Into_listContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_into_list);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(981);
			match(INTO);
			setState(982);
			db_object_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
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
		enterRule(_localctx, 128, RULE_set_qualifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(984);
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
		enterRule(_localctx, 130, RULE_select_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(986);
			select_item();
			setState(991);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(987);
				match(COMMA);
				setState(988);
				select_item();
				}
				}
				setState(993);
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
		enterRule(_localctx, 132, RULE_select_item);
		try {
			setState(999);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(994);
				value_expression();
				setState(996);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,56,_ctx) ) {
				case 1:
					{
					setState(995);
					as_clause();
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(998);
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
		enterRule(_localctx, 134, RULE_as_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1002);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS) {
				{
				setState(1001);
				match(AS);
				}
			}

			setState(1004);
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
		enterRule(_localctx, 136, RULE_select_all_columns);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1006);
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
		enterRule(_localctx, 138, RULE_wildcard_reference);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1010);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Identifier) {
				{
				setState(1008);
				((Wildcard_referenceContext)_localctx).tb_name = match(Identifier);
				setState(1009);
				match(DOT);
				}
			}

			setState(1012);
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
		enterRule(_localctx, 140, RULE_from_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1014);
			match(FROM);
			setState(1015);
			table_reference_list();
			setState(1017);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
			case 1:
				{
				setState(1016);
				join_extension();
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
		enterRule(_localctx, 142, RULE_join_extension);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1019);
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
		enterRule(_localctx, 144, RULE_table_reference_list);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1021);
			table_primary();
			setState(1043);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,66,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(1041);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case COMMA:
						{
						{
						setState(1022);
						match(COMMA);
						setState(1024);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==LATERAL) {
							{
							setState(1023);
							lateral_modifier();
							}
						}

						setState(1026);
						table_primary();
						}
						}
						break;
					case CROSS:
					case NATURAL:
					case UNION:
						{
						{
						setState(1027);
						unqualified_join();
						setState(1029);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==LATERAL) {
							{
							setState(1028);
							lateral_modifier();
							}
						}

						setState(1031);
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
						setState(1033);
						qualified_join();
						setState(1035);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==LATERAL) {
							{
							setState(1034);
							lateral_modifier();
							}
						}

						setState(1037);
						((Table_reference_listContext)_localctx).right = table_primary();
						setState(1039);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==ON || _la==USING) {
							{
							setState(1038);
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
				setState(1045);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,66,_ctx);
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
		enterRule(_localctx, 146, RULE_join_extension_primary);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1067);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 9016000322274304L) != 0) || _la==COMMA) {
				{
				setState(1065);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case COMMA:
					{
					{
					setState(1046);
					match(COMMA);
					setState(1048);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==LATERAL) {
						{
						setState(1047);
						lateral_modifier();
						}
					}

					setState(1050);
					table_primary();
					}
					}
					break;
				case CROSS:
				case NATURAL:
				case UNION:
					{
					{
					setState(1051);
					unqualified_join();
					setState(1053);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==LATERAL) {
						{
						setState(1052);
						lateral_modifier();
						}
					}

					setState(1055);
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
					setState(1057);
					qualified_join();
					setState(1059);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==LATERAL) {
						{
						setState(1058);
						lateral_modifier();
						}
					}

					setState(1061);
					((Join_extension_primaryContext)_localctx).right = table_primary();
					setState(1063);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==ON || _la==USING) {
						{
						setState(1062);
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
				setState(1069);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1071);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 326)) & ~0x3f) == 0 && ((1L << (_la - 326)) & 7L) != 0)) {
				{
				setState(1070);
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
		enterRule(_localctx, 148, RULE_lateral_modifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1073);
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
		public Db_object_nameContext db_object_name() {
			return getRuleContext(Db_object_nameContext.class,0);
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
		enterRule(_localctx, 150, RULE_table_primary);
		try {
			setState(1095);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,78,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1075);
				db_object_name();
				setState(1077);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,74,_ctx) ) {
				case 1:
					{
					setState(1076);
					relation_as_clause();
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1079);
				variable_identifier();
				setState(1080);
				as_clause();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1082);
				jinja_identifier();
				setState(1084);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,75,_ctx) ) {
				case 1:
					{
					setState(1083);
					relation_as_clause();
					}
					break;
				}
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1086);
				table_function_primary();
				setState(1088);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,76,_ctx) ) {
				case 1:
					{
					setState(1087);
					relation_as_clause();
					}
					break;
				}
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1090);
				values_statement_primary();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1091);
				subquery();
				setState(1093);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,77,_ctx) ) {
				case 1:
					{
					setState(1092);
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
		enterRule(_localctx, 152, RULE_relation_as_clause);
		try {
			setState(1101);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,79,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1097);
				match(AS);
				setState(1098);
				alias_identifier();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1099);
				if (!(isAllowedImplicitAlias(_input.LT(1).getText()))) throw new FailedPredicateException(this, "isAllowedImplicitAlias(_input.LT(1).getText())");
				setState(1100);
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
		public Db_object_nameContext db_object_name() {
			return getRuleContext(Db_object_nameContext.class,0);
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
		enterRule(_localctx, 154, RULE_tuple_primary);
		try {
			setState(1109);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,80,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1103);
				db_object_name();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1104);
				variable_identifier();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1105);
				jinja_identifier();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1106);
				table_function_primary();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1107);
				values_statement_primary();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1108);
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
	public static class Db_object_nameContext extends ParserRuleContext {
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
		public Db_object_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_db_object_name; }
	}

	public final Db_object_nameContext db_object_name() throws RecognitionException {
		Db_object_nameContext _localctx = new Db_object_nameContext(_ctx, getState());
		enterRule(_localctx, 156, RULE_db_object_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1111);
			identifier();
			setState(1117);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,82,_ctx) ) {
			case 1:
				{
				setState(1112);
				match(DOT);
				setState(1115);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case NUMBER:
				case Numeric_Identifier:
					{
					setState(1113);
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
				case ALTER:
				case DATABASE:
				case FILE:
				case FUNCTION:
				case MACRO:
				case MATERIALIZED:
				case PROCEDURE:
				case RETURNS:
				case ROLE:
				case SCHEMA:
				case SEQUENCE:
				case STAGE:
				case USER:
				case VIEW:
				case Identifier:
				case Double_Quoted_Numeric_Identifier:
				case Dollar_Sign_Identifier:
					{
					setState(1114);
					identifier();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			}
			setState(1124);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,84,_ctx) ) {
			case 1:
				{
				setState(1119);
				match(DOT);
				setState(1122);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case NUMBER:
				case Numeric_Identifier:
					{
					setState(1120);
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
				case ALTER:
				case DATABASE:
				case FILE:
				case FUNCTION:
				case MACRO:
				case MATERIALIZED:
				case PROCEDURE:
				case RETURNS:
				case ROLE:
				case SCHEMA:
				case SEQUENCE:
				case STAGE:
				case USER:
				case VIEW:
				case Identifier:
				case Double_Quoted_Numeric_Identifier:
				case Dollar_Sign_Identifier:
					{
					setState(1121);
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
		enterRule(_localctx, 158, RULE_unqualified_join);
		int _la;
		try {
			setState(1135);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CROSS:
				enterOuterAlt(_localctx, 1);
				{
				setState(1126);
				match(CROSS);
				setState(1127);
				match(JOIN);
				}
				break;
			case UNION:
				enterOuterAlt(_localctx, 2);
				{
				setState(1128);
				match(UNION);
				setState(1129);
				match(JOIN);
				}
				break;
			case NATURAL:
				enterOuterAlt(_localctx, 3);
				{
				setState(1130);
				match(NATURAL);
				setState(1132);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 8796638347264L) != 0)) {
					{
					setState(1131);
					((Unqualified_joinContext)_localctx).t = join_type();
					}
				}

				setState(1134);
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
		enterRule(_localctx, 160, RULE_qualified_join);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1138);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 8796638347264L) != 0)) {
				{
				setState(1137);
				((Qualified_joinContext)_localctx).t = join_type();
				}
			}

			setState(1140);
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
		enterRule(_localctx, 162, RULE_join_type);
		int _la;
		try {
			setState(1147);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INNER:
				enterOuterAlt(_localctx, 1);
				{
				setState(1142);
				match(INNER);
				}
				break;
			case FULL:
			case LEFT:
			case RIGHT:
				enterOuterAlt(_localctx, 2);
				{
				setState(1143);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 8796629958656L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1145);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OUTER) {
					{
					setState(1144);
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
		enterRule(_localctx, 164, RULE_join_specification);
		try {
			setState(1151);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ON:
				enterOuterAlt(_localctx, 1);
				{
				setState(1149);
				join_condition();
				}
				break;
			case USING:
				enterOuterAlt(_localctx, 2);
				{
				setState(1150);
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
		enterRule(_localctx, 166, RULE_join_condition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1153);
			match(ON);
			setState(1154);
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
		enterRule(_localctx, 168, RULE_named_columns_join);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1156);
			using_term();
			setState(1157);
			match(LEFT_PAREN);
			setState(1158);
			((Named_columns_joinContext)_localctx).f = column_reference_list();
			setState(1159);
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
		enterRule(_localctx, 170, RULE_using_term);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1161);
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
		enterRule(_localctx, 172, RULE_table_function_primary);
		try {
			setState(1169);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TABLE:
				enterOuterAlt(_localctx, 1);
				{
				setState(1163);
				match(TABLE);
				setState(1164);
				match(LEFT_PAREN);
				setState(1165);
				table_function();
				setState(1166);
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
			case ALTER:
			case DATABASE:
			case FILE:
			case FUNCTION:
			case MACRO:
			case MATERIALIZED:
			case PROCEDURE:
			case RETURNS:
			case ROLE:
			case SCHEMA:
			case SEQUENCE:
			case STAGE:
			case USER:
			case VIEW:
			case Identifier:
			case Double_Quoted_Numeric_Identifier:
			case Dollar_Sign_Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(1168);
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
		enterRule(_localctx, 174, RULE_table_function);
		try {
			setState(1177);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FLATTEN:
				enterOuterAlt(_localctx, 1);
				{
				setState(1171);
				flatten_table_function();
				}
				break;
			case GENERATOR:
				enterOuterAlt(_localctx, 2);
				{
				setState(1172);
				generator_table_function();
				}
				break;
			case RESULT_SCAN:
				enterOuterAlt(_localctx, 3);
				{
				setState(1173);
				result_scan_table_function();
				}
				break;
			case INFER_SCHEMA:
				enterOuterAlt(_localctx, 4);
				{
				setState(1174);
				infer_schema_table_function();
				}
				break;
			case VALIDATE:
				enterOuterAlt(_localctx, 5);
				{
				setState(1175);
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
			case ALTER:
			case DATABASE:
			case FILE:
			case FUNCTION:
			case MACRO:
			case MATERIALIZED:
			case PROCEDURE:
			case RETURNS:
			case ROLE:
			case SCHEMA:
			case SEQUENCE:
			case STAGE:
			case USER:
			case VIEW:
			case Identifier:
			case Double_Quoted_Numeric_Identifier:
			case Dollar_Sign_Identifier:
				enterOuterAlt(_localctx, 6);
				{
				setState(1176);
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
		enterRule(_localctx, 176, RULE_flatten_table_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1179);
			flatten_function_name();
			setState(1180);
			match(LEFT_PAREN);
			setState(1181);
			flatten_argument_list();
			setState(1182);
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
		enterRule(_localctx, 178, RULE_flatten_argument_list);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1184);
			flatten_argument();
			setState(1189);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,93,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1185);
					match(COMMA);
					setState(1186);
					flatten_argument();
					}
					} 
				}
				setState(1191);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,93,_ctx);
			}
			setState(1192);
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
		enterRule(_localctx, 180, RULE_flatten_argument);
		try {
			setState(1211);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INPUT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1194);
				match(INPUT);
				setState(1195);
				match(IMPLIES);
				setState(1196);
				flatten_argument_value();
				}
				break;
			case PATH:
				enterOuterAlt(_localctx, 2);
				{
				setState(1197);
				match(PATH);
				setState(1198);
				match(IMPLIES);
				setState(1199);
				flatten_argument_value();
				}
				break;
			case OUTER:
				enterOuterAlt(_localctx, 3);
				{
				setState(1200);
				match(OUTER);
				setState(1201);
				match(IMPLIES);
				setState(1202);
				flatten_argument_value();
				}
				break;
			case RECURSIVE:
				enterOuterAlt(_localctx, 4);
				{
				setState(1203);
				match(RECURSIVE);
				setState(1204);
				match(IMPLIES);
				setState(1205);
				flatten_argument_value();
				}
				break;
			case MODE:
				enterOuterAlt(_localctx, 5);
				{
				setState(1206);
				match(MODE);
				setState(1207);
				match(IMPLIES);
				setState(1208);
				((Flatten_argumentContext)_localctx).m = table_argument_literal();
				setState(1209);
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
		enterRule(_localctx, 182, RULE_flatten_argument_value);
		try {
			setState(1216);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,95,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1213);
				value_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1214);
				table_argument_literal();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1215);
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
		enterRule(_localctx, 184, RULE_flatten_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1218);
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
		enterRule(_localctx, 186, RULE_table_argument_literal);
		try {
			setState(1222);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Character_String_Literal:
				enterOuterAlt(_localctx, 1);
				{
				setState(1220);
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
				setState(1221);
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
		enterRule(_localctx, 188, RULE_table_argument_boolean);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1224);
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
		enterRule(_localctx, 190, RULE_generator_table_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1226);
			generator_function_name();
			setState(1227);
			match(LEFT_PAREN);
			setState(1228);
			generator_argument_list();
			setState(1229);
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
		enterRule(_localctx, 192, RULE_generator_argument_list);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1231);
			generator_argument();
			setState(1236);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,97,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1232);
					match(COMMA);
					setState(1233);
					generator_argument();
					}
					} 
				}
				setState(1238);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,97,_ctx);
			}
			setState(1239);
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
		enterRule(_localctx, 194, RULE_generator_argument);
		try {
			setState(1247);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ROWCOUNT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1241);
				match(ROWCOUNT);
				setState(1242);
				match(IMPLIES);
				setState(1243);
				generator_argument_value();
				}
				break;
			case TIMELIMIT:
				enterOuterAlt(_localctx, 2);
				{
				setState(1244);
				match(TIMELIMIT);
				setState(1245);
				match(IMPLIES);
				setState(1246);
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
		enterRule(_localctx, 196, RULE_generator_argument_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1249);
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
		enterRule(_localctx, 198, RULE_generator_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1251);
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
		enterRule(_localctx, 200, RULE_result_scan_table_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1253);
			result_scan_function_name();
			setState(1254);
			match(LEFT_PAREN);
			setState(1256);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -848827245298417264L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -288230377259008257L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -2097169L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -162133984631980033L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 8947490635775L) != 0) || ((((_la - 325)) & ~0x3f) == 0 && ((1L << (_la - 325)) & 87257225600436207L) != 0)) {
				{
				setState(1255);
				value_expression();
				}
			}

			setState(1258);
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
		enterRule(_localctx, 202, RULE_result_scan_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1260);
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
		enterRule(_localctx, 204, RULE_infer_schema_table_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1262);
			infer_schema_function_name();
			setState(1263);
			match(LEFT_PAREN);
			setState(1264);
			infer_schema_argument_list();
			setState(1265);
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
		enterRule(_localctx, 206, RULE_infer_schema_argument_list);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1267);
			infer_schema_argument();
			setState(1272);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,100,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1268);
					match(COMMA);
					setState(1269);
					infer_schema_argument();
					}
					} 
				}
				setState(1274);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,100,_ctx);
			}
			setState(1275);
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
		enterRule(_localctx, 208, RULE_infer_schema_argument);
		try {
			setState(1298);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LOCATION:
				enterOuterAlt(_localctx, 1);
				{
				setState(1277);
				match(LOCATION);
				setState(1278);
				match(IMPLIES);
				setState(1279);
				infer_schema_argument_value();
				}
				break;
			case FILE_FORMAT:
				enterOuterAlt(_localctx, 2);
				{
				setState(1280);
				match(FILE_FORMAT);
				setState(1281);
				match(IMPLIES);
				setState(1282);
				infer_schema_argument_value();
				}
				break;
			case FILES:
				enterOuterAlt(_localctx, 3);
				{
				setState(1283);
				match(FILES);
				setState(1284);
				match(IMPLIES);
				setState(1285);
				infer_schema_argument_value();
				}
				break;
			case IGNORE_CASE:
				enterOuterAlt(_localctx, 4);
				{
				setState(1286);
				match(IGNORE_CASE);
				setState(1287);
				match(IMPLIES);
				setState(1288);
				infer_schema_argument_value();
				}
				break;
			case MAX_FILE_COUNT:
				enterOuterAlt(_localctx, 5);
				{
				setState(1289);
				match(MAX_FILE_COUNT);
				setState(1290);
				match(IMPLIES);
				setState(1291);
				infer_schema_argument_value();
				}
				break;
			case MAX_RECORDS_PER_FILE:
				enterOuterAlt(_localctx, 6);
				{
				setState(1292);
				match(MAX_RECORDS_PER_FILE);
				setState(1293);
				match(IMPLIES);
				setState(1294);
				infer_schema_argument_value();
				}
				break;
			case KIND:
				enterOuterAlt(_localctx, 7);
				{
				setState(1295);
				match(KIND);
				setState(1296);
				match(IMPLIES);
				setState(1297);
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
		enterRule(_localctx, 210, RULE_infer_schema_argument_value);
		try {
			setState(1304);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,102,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1300);
				table_argument_literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1301);
				infer_schema_files_argument();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1302);
				additive_expression();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1303);
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
		enterRule(_localctx, 212, RULE_infer_schema_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1306);
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
		enterRule(_localctx, 214, RULE_infer_schema_files_argument);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1308);
			match(LEFT_PAREN);
			setState(1309);
			match(Character_String_Literal);
			setState(1314);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1310);
				match(COMMA);
				setState(1311);
				match(Character_String_Literal);
				}
				}
				setState(1316);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1317);
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
		public Db_object_nameContext db_object_name() {
			return getRuleContext(Db_object_nameContext.class,0);
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
		enterRule(_localctx, 216, RULE_validate_table_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1319);
			validate_function_name();
			setState(1320);
			match(LEFT_PAREN);
			setState(1321);
			db_object_name();
			setState(1322);
			match(COMMA);
			setState(1323);
			match(JOB_ID);
			setState(1324);
			match(IMPLIES);
			setState(1325);
			table_argument_literal();
			setState(1326);
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
		enterRule(_localctx, 218, RULE_validate_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1328);
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
		enterRule(_localctx, 220, RULE_generic_table_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1330);
			table_function_name();
			setState(1331);
			match(LEFT_PAREN);
			setState(1333);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -848827245298417264L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -288230377259008257L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -2097169L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -162133984631980033L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 8947490635775L) != 0) || ((((_la - 325)) & ~0x3f) == 0 && ((1L << (_la - 325)) & 87257225600436207L) != 0)) {
				{
				setState(1332);
				table_function_argument_list();
				}
			}

			setState(1335);
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
		enterRule(_localctx, 222, RULE_table_function_name);
		try {
			setState(1341);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SPLIT_TO_TABLE:
				enterOuterAlt(_localctx, 1);
				{
				setState(1337);
				match(SPLIT_TO_TABLE);
				}
				break;
			case STRTOK_SPLIT_TO_TABLE:
				enterOuterAlt(_localctx, 2);
				{
				setState(1338);
				match(STRTOK_SPLIT_TO_TABLE);
				}
				break;
			case QUERY_HISTORY:
				enterOuterAlt(_localctx, 3);
				{
				setState(1339);
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
			case ALTER:
			case DATABASE:
			case FILE:
			case FUNCTION:
			case MACRO:
			case MATERIALIZED:
			case PROCEDURE:
			case RETURNS:
			case ROLE:
			case SCHEMA:
			case SEQUENCE:
			case STAGE:
			case USER:
			case VIEW:
			case Identifier:
			case Double_Quoted_Numeric_Identifier:
			case Dollar_Sign_Identifier:
				enterOuterAlt(_localctx, 4);
				{
				setState(1340);
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
		enterRule(_localctx, 224, RULE_table_function_argument_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1343);
			value_expression();
			setState(1348);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1344);
				match(COMMA);
				setState(1345);
				value_expression();
				}
				}
				setState(1350);
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
		enterRule(_localctx, 226, RULE_column_reference_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1351);
			column_reference();
			setState(1356);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1352);
				match(COMMA);
				setState(1353);
				column_reference();
				}
				}
				setState(1358);
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
		enterRule(_localctx, 228, RULE_column_reference);
		try {
			int _alt;
			setState(1376);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,110,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1362);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,108,_ctx) ) {
				case 1:
					{
					setState(1359);
					((Column_referenceContext)_localctx).tb_name = identifier();
					setState(1360);
					match(DOT);
					}
					break;
				}
				setState(1364);
				((Column_referenceContext)_localctx).name = identifier();
				setState(1369);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,109,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1365);
						match(COLON);
						setState(1366);
						((Column_referenceContext)_localctx).identifier = identifier();
						((Column_referenceContext)_localctx).path_name.add(((Column_referenceContext)_localctx).identifier);
						}
						} 
					}
					setState(1371);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,109,_ctx);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1372);
				((Column_referenceContext)_localctx).tb_name = identifier();
				setState(1373);
				match(DOT);
				setState(1374);
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
		enterRule(_localctx, 230, RULE_column_primary);
		int _la;
		try {
			setState(1396);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,113,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1381);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,111,_ctx) ) {
				case 1:
					{
					setState(1378);
					((Column_primaryContext)_localctx).tb_name = identifier();
					setState(1379);
					match(DOT);
					}
					break;
				}
				setState(1383);
				((Column_primaryContext)_localctx).name = identifier();
				setState(1388);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COLON) {
					{
					{
					setState(1384);
					match(COLON);
					setState(1385);
					((Column_primaryContext)_localctx).identifier = identifier();
					((Column_primaryContext)_localctx).path_name.add(((Column_primaryContext)_localctx).identifier);
					}
					}
					setState(1390);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1391);
				((Column_primaryContext)_localctx).tb_name = identifier();
				setState(1392);
				match(DOT);
				setState(1393);
				((Column_primaryContext)_localctx).substitution = variable_identifier();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1395);
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
		enterRule(_localctx, 232, RULE_predicand_primary);
		try {
			setState(1408);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,114,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1398);
				value_expression_primary();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1399);
				string_value_expression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1400);
				sign();
				setState(1401);
				numeric_primary();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1403);
				trim_function();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1404);
				null_literal();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1405);
				variable_identifier();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1406);
				puml_constant_identifier();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1407);
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
		enterRule(_localctx, 234, RULE_value_expression_primary);
		try {
			setState(1420);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,117,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1410);
				parenthesized_value_expression();
				setState(1413);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,115,_ctx) ) {
				case 1:
					{
					setState(1411);
					match(CAST_OPERATOR);
					setState(1412);
					data_type();
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1415);
				nonparenthesized_value_expression_primary();
				setState(1418);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,116,_ctx) ) {
				case 1:
					{
					setState(1416);
					match(CAST_OPERATOR);
					setState(1417);
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
		enterRule(_localctx, 236, RULE_parenthesized_value_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1422);
			match(LEFT_PAREN);
			setState(1423);
			value_expression();
			setState(1424);
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
		enterRule(_localctx, 238, RULE_nonparenthesized_value_expression_primary);
		try {
			setState(1435);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,118,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1426);
				unsigned_literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1427);
				column_reference();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1428);
				aggregate_function();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1429);
				case_expression();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1430);
				cast_function_expression();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1431);
				routine_invocation();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1432);
				position_function();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1433);
				window_over_partition_expression();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(1434);
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
		enterRule(_localctx, 240, RULE_predicand_subquery);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1437);
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
		enterRule(_localctx, 242, RULE_aggregate_function);
		int _la;
		try {
			setState(1455);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,121,_ctx) ) {
			case 1:
				_localctx = new Count_all_aggregateContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(1439);
				match(COUNT);
				setState(1440);
				match(LEFT_PAREN);
				setState(1441);
				wildcard_reference();
				setState(1442);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				_localctx = new General_set_functionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(1446);
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
					setState(1444);
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
					setState(1445);
					set_qualifier_type();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(1448);
				match(LEFT_PAREN);
				setState(1450);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ALL || _la==DISTINCT) {
					{
					setState(1449);
					set_qualifier();
					}
				}

				setState(1452);
				value_expression();
				setState(1453);
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
		enterRule(_localctx, 244, RULE_set_function_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1457);
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
		enterRule(_localctx, 246, RULE_set_qualifier_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1459);
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
		enterRule(_localctx, 248, RULE_case_expression);
		int _la;
		try {
			setState(1476);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,124,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1461);
				match(CASE);
				setState(1462);
				value_expression();
				setState(1463);
				when_value_list();
				setState(1465);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSE) {
					{
					setState(1464);
					else_clause();
					}
				}

				setState(1467);
				match(END);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1469);
				match(CASE);
				setState(1470);
				when_clause_list();
				setState(1472);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSE) {
					{
					setState(1471);
					else_clause();
					}
				}

				setState(1474);
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
		enterRule(_localctx, 250, RULE_when_clause_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1479); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(1478);
				searched_when_clause();
				}
				}
				setState(1481); 
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
		enterRule(_localctx, 252, RULE_searched_when_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1483);
			match(WHEN);
			setState(1484);
			((Searched_when_clauseContext)_localctx).c = value_expression();
			setState(1485);
			match(THEN);
			setState(1486);
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
		enterRule(_localctx, 254, RULE_when_value_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1489); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(1488);
				when_value_clause();
				}
				}
				setState(1491); 
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
		enterRule(_localctx, 256, RULE_when_value_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1493);
			match(WHEN);
			setState(1494);
			((When_value_clauseContext)_localctx).c = value_expression();
			setState(1495);
			match(THEN);
			setState(1496);
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
		enterRule(_localctx, 258, RULE_else_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1498);
			match(ELSE);
			setState(1499);
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
		enterRule(_localctx, 260, RULE_case_result);
		try {
			setState(1503);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,127,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1501);
				value_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1502);
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
		enterRule(_localctx, 262, RULE_null_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1505);
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
		enterRule(_localctx, 264, RULE_cast_function_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1507);
			cast_function_name();
			setState(1508);
			match(LEFT_PAREN);
			setState(1509);
			value_expression();
			setState(1510);
			match(AS);
			setState(1511);
			data_type();
			setState(1512);
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
		enterRule(_localctx, 266, RULE_cast_function_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1514);
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
		enterRule(_localctx, 268, RULE_window_over_partition_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1516);
			window_function();
			setState(1517);
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
		enterRule(_localctx, 270, RULE_window_function);
		int _la;
		try {
			setState(1536);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,131,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1519);
				set_function_type();
				setState(1520);
				match(LEFT_PAREN);
				setState(1522);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -848827245298417264L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -288230377259008257L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -2097169L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -162133984631980033L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 8947490635775L) != 0) || ((((_la - 325)) & ~0x3f) == 0 && ((1L << (_la - 325)) & 87257225600436207L) != 0)) {
					{
					setState(1521);
					sql_argument_list();
					}
				}

				setState(1524);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1526);
				item_select_function();
				setState(1527);
				match(LEFT_PAREN);
				setState(1528);
				sql_argument_list();
				setState(1529);
				match(RIGHT_PAREN);
				setState(1534);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 4398047690752L) != 0)) {
					{
					setState(1531);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==FROM) {
						{
						setState(1530);
						select_direction();
						}
					}

					setState(1533);
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
		enterRule(_localctx, 272, RULE_over_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1538);
			match(OVER);
			setState(1539);
			match(LEFT_PAREN);
			{
			setState(1541);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PARTITION) {
				{
				setState(1540);
				partition_by_clause();
				}
			}

			setState(1544);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ORDER) {
				{
				setState(1543);
				orderby_clause();
				}
			}

			setState(1547);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==RANGE || _la==ROWS) {
				{
				setState(1546);
				bracket_frame_clause();
				}
			}

			}
			setState(1549);
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
		enterRule(_localctx, 274, RULE_partition_by_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1551);
			match(PARTITION);
			setState(1552);
			match(BY);
			setState(1553);
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
		enterRule(_localctx, 276, RULE_bracket_frame_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1555);
			rows_or_range();
			setState(1556);
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
		enterRule(_localctx, 278, RULE_rows_or_range);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1558);
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
		enterRule(_localctx, 280, RULE_bracket_frame_definition);
		try {
			setState(1563);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BETWEEN:
				enterOuterAlt(_localctx, 1);
				{
				setState(1560);
				between_frame_definition();
				}
				break;
			case UNBOUNDED:
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(1561);
				preceding_frame_edge();
				}
				break;
			case CURRENT:
				enterOuterAlt(_localctx, 3);
				{
				setState(1562);
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
		enterRule(_localctx, 282, RULE_between_frame_definition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1565);
			match(BETWEEN);
			setState(1566);
			frame_edge();
			setState(1567);
			match(AND);
			setState(1568);
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
		enterRule(_localctx, 284, RULE_frame_edge);
		try {
			setState(1573);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,136,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1570);
				preceding_frame_edge();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1571);
				following_frame_edge();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1572);
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
		enterRule(_localctx, 286, RULE_preceding_frame_edge);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1575);
			bracket_constraint();
			setState(1576);
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
		enterRule(_localctx, 288, RULE_following_frame_edge);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1578);
			bracket_constraint();
			setState(1579);
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
		enterRule(_localctx, 290, RULE_current_row_edge);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1581);
			match(CURRENT);
			setState(1582);
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
		enterRule(_localctx, 292, RULE_bracket_constraint);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1584);
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
		enterRule(_localctx, 294, RULE_item_select_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1586);
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
		enterRule(_localctx, 296, RULE_select_direction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1588);
			match(FROM);
			setState(1589);
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
		enterRule(_localctx, 298, RULE_null_handling);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1591);
			_la = _input.LA(1);
			if ( !(_la==IGNORE || _la==RESPECT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(1592);
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
		enterRule(_localctx, 300, RULE_value_expression);
		try {
			setState(1598);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,137,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1594);
				common_value_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1595);
				row_value_expression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1596);
				variable_identifier();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1597);
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
		enterRule(_localctx, 302, RULE_common_value_expression);
		try {
			setState(1603);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,138,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1600);
				additive_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1601);
				string_value_expression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1602);
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
		enterRule(_localctx, 304, RULE_additive_expression);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(1605);
			((Additive_expressionContext)_localctx).left = multiplicative_expression();
			setState(1610);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,139,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1606);
					_la = _input.LA(1);
					if ( !(_la==PLUS || _la==MINUS) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(1607);
					((Additive_expressionContext)_localctx).right = multiplicative_expression();
					}
					} 
				}
				setState(1612);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,139,_ctx);
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
		enterRule(_localctx, 306, RULE_multiplicative_expression);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1613);
			((Multiplicative_expressionContext)_localctx).left = factor();
			setState(1618);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,140,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1614);
					_la = _input.LA(1);
					if ( !(((((_la - 290)) & ~0x3f) == 0 && ((1L << (_la - 290)) & 7L) != 0)) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(1615);
					((Multiplicative_expressionContext)_localctx).right = factor();
					}
					} 
				}
				setState(1620);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,140,_ctx);
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
		enterRule(_localctx, 308, RULE_factor);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1622);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PLUS || _la==MINUS) {
				{
				setState(1621);
				sign();
				}
			}

			setState(1624);
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
		enterRule(_localctx, 310, RULE_numeric_primary);
		try {
			setState(1628);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,142,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1626);
				value_expression_primary();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1627);
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
		enterRule(_localctx, 312, RULE_sign);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1630);
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
		enterRule(_localctx, 314, RULE_extract_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1632);
			match(EXTRACT);
			setState(1633);
			match(LEFT_PAREN);
			setState(1634);
			((Extract_expressionContext)_localctx).extract_field_string = extract_field();
			setState(1635);
			match(FROM);
			setState(1636);
			extract_source();
			setState(1637);
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
		enterRule(_localctx, 316, RULE_extract_field);
		try {
			setState(1642);
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
				setState(1639);
				primary_datetime_field();
				}
				break;
			case TIMEZONE:
			case TIMEZONE_HOUR:
			case TIMEZONE_MINUTE:
				enterOuterAlt(_localctx, 2);
				{
				setState(1640);
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
				setState(1641);
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
		enterRule(_localctx, 318, RULE_time_zone_field);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1644);
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
		enterRule(_localctx, 320, RULE_extract_source);
		try {
			setState(1648);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,144,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1646);
				column_reference();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1647);
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
		enterRule(_localctx, 322, RULE_string_value_expression);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1650);
			character_primary();
			setState(1655);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,145,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1651);
					match(CONCATENATION_OPERATOR);
					setState(1652);
					character_primary();
					}
					} 
				}
				setState(1657);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,145,_ctx);
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
		enterRule(_localctx, 324, RULE_character_primary);
		try {
			setState(1660);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,146,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1658);
				value_expression_primary();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1659);
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
		enterRule(_localctx, 326, RULE_trim_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1662);
			trim_function_name();
			setState(1663);
			match(LEFT_PAREN);
			setState(1664);
			trim_operands();
			setState(1665);
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
		enterRule(_localctx, 328, RULE_trim_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1667);
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
		enterRule(_localctx, 330, RULE_trim_operands);
		int _la;
		try {
			setState(1683);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,150,_ctx) ) {
			case 1:
				_localctx = new Mysql_trim_operandsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(1676);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,149,_ctx) ) {
				case 1:
					{
					setState(1670);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1125900175278144L) != 0)) {
						{
						setState(1669);
						trim_specification();
						}
					}

					setState(1673);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -848827271068221040L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -288230377259008257L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -2097169L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -162133984631980033L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 8934605733887L) != 0) || ((((_la - 325)) & ~0x3f) == 0 && ((1L << (_la - 325)) & 87257225600436193L) != 0)) {
						{
						setState(1672);
						((Mysql_trim_operandsContext)_localctx).trim_character = string_value_expression();
						}
					}

					setState(1675);
					match(FROM);
					}
					break;
				}
				setState(1678);
				((Mysql_trim_operandsContext)_localctx).trim_source = value_expression();
				}
				break;
			case 2:
				_localctx = new Other_trim_operandsContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(1679);
				((Other_trim_operandsContext)_localctx).trim_source = value_expression();
				setState(1680);
				match(COMMA);
				setState(1681);
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
		enterRule(_localctx, 332, RULE_trim_specification);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1685);
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
		enterRule(_localctx, 334, RULE_position_function);
		int _la;
		try {
			setState(1709);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,153,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1687);
				position_function_name();
				setState(1688);
				match(LEFT_PAREN);
				setState(1689);
				((Position_functionContext)_localctx).search_string = string_value_expression();
				setState(1690);
				match(IN);
				setState(1691);
				((Position_functionContext)_localctx).source_string = string_value_expression();
				setState(1692);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1697);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case POSITION:
					{
					setState(1694);
					position_function_name();
					}
					break;
				case INSTR:
					{
					setState(1695);
					instr_function_name();
					}
					break;
				case CHARINDEX:
					{
					setState(1696);
					charindex_name();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(1699);
				match(LEFT_PAREN);
				setState(1700);
				((Position_functionContext)_localctx).search_string = string_value_expression();
				setState(1701);
				match(COMMA);
				setState(1702);
				((Position_functionContext)_localctx).source_string = string_value_expression();
				setState(1705);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(1703);
					match(COMMA);
					setState(1704);
					((Position_functionContext)_localctx).start_position = numeric_primary();
					}
				}

				setState(1707);
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
		enterRule(_localctx, 336, RULE_position_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1711);
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
		enterRule(_localctx, 338, RULE_instr_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1713);
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
		enterRule(_localctx, 340, RULE_charindex_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1715);
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
		enterRule(_localctx, 342, RULE_boolean_value_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1717);
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
		enterRule(_localctx, 344, RULE_or_predicate);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1719);
			and_predicate();
			setState(1724);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,154,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1720);
					match(OR);
					setState(1721);
					and_predicate();
					}
					} 
				}
				setState(1726);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,154,_ctx);
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
		enterRule(_localctx, 346, RULE_and_predicate);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1727);
			negative_predicate();
			setState(1732);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,155,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1728);
					match(AND);
					setState(1729);
					negative_predicate();
					}
					} 
				}
				setState(1734);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,155,_ctx);
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
		enterRule(_localctx, 348, RULE_negative_predicate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1736);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(1735);
				not();
				}
			}

			setState(1738);
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
		enterRule(_localctx, 350, RULE_parenthetical_predicate);
		try {
			setState(1748);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,158,_ctx) ) {
			case 1:
				_localctx = new Paren_clauseContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(1740);
				match(LEFT_PAREN);
				setState(1741);
				boolean_value_expression();
				setState(1742);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				_localctx = new Basic_predicate_clauseContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(1744);
				boolean_primary();
				setState(1746);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,157,_ctx) ) {
				case 1:
					{
					setState(1745);
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
		enterRule(_localctx, 352, RULE_boolean_primary);
		try {
			setState(1752);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,159,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1750);
				predicate();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1751);
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
		enterRule(_localctx, 354, RULE_predicate);
		try {
			setState(1761);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,160,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1754);
				comparison_predicate();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1755);
				between_predicate();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1756);
				in_predicate();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1757);
				like_any_predicate();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1758);
				null_predicate();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1759);
				exists_predicate();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1760);
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
		enterRule(_localctx, 356, RULE_substitution_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1763);
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
		enterRule(_localctx, 358, RULE_row_value_expression);
		try {
			setState(1767);
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
			case ALTER:
			case DATABASE:
			case FILE:
			case FUNCTION:
			case MACRO:
			case MATERIALIZED:
			case PROCEDURE:
			case RETURNS:
			case ROLE:
			case SCHEMA:
			case SEQUENCE:
			case STAGE:
			case USER:
			case VIEW:
			case Identifier:
			case Scientific_Numeric_Literal:
			case Double_Quoted_Numeric_Identifier:
			case Dollar_Sign_Identifier:
			case Character_String_Literal:
				enterOuterAlt(_localctx, 1);
				{
				setState(1765);
				nonparenthesized_value_expression_primary();
				}
				break;
			case NULL:
				enterOuterAlt(_localctx, 2);
				{
				setState(1766);
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
		enterRule(_localctx, 360, RULE_row_value_predicand);
		try {
			setState(1772);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,162,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1769);
				nonparenthesized_value_expression_primary();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1770);
				common_value_expression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1771);
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
		enterRule(_localctx, 362, RULE_where_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1774);
			match(WHERE);
			setState(1775);
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
		enterRule(_localctx, 364, RULE_search_condition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1777);
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
		enterRule(_localctx, 366, RULE_orderby_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1779);
			match(ORDER);
			setState(1780);
			match(BY);
			setState(1781);
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
		enterRule(_localctx, 368, RULE_sort_specifier_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1783);
			sort_specifier();
			setState(1788);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1784);
				match(COMMA);
				setState(1785);
				sort_specifier();
				}
				}
				setState(1790);
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
		enterRule(_localctx, 370, RULE_sort_specifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1791);
			((Sort_specifierContext)_localctx).key = row_value_predicand();
			setState(1793);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ASC || _la==DESC) {
				{
				setState(1792);
				((Sort_specifierContext)_localctx).order = order_specification();
				}
			}

			setState(1796);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NULLS) {
				{
				setState(1795);
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
		enterRule(_localctx, 372, RULE_order_specification);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1798);
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
		enterRule(_localctx, 374, RULE_null_ordering);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1800);
			match(NULLS);
			setState(1801);
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
		enterRule(_localctx, 376, RULE_null_first_last);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1803);
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
		enterRule(_localctx, 378, RULE_limit_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1805);
			match(LIMIT);
			setState(1806);
			((Limit_clauseContext)_localctx).e = additive_expression();
			setState(1809);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OFFSET) {
				{
				setState(1807);
				match(OFFSET);
				setState(1808);
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
		enterRule(_localctx, 380, RULE_groupby_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1811);
			match(GROUP);
			setState(1812);
			match(BY);
			setState(1815);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,167,_ctx) ) {
			case 1:
				{
				setState(1813);
				grouping_element_list();
				}
				break;
			case 2:
				{
				setState(1814);
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
		enterRule(_localctx, 382, RULE_grouping_element_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1817);
			grouping_element();
			setState(1822);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1818);
				match(COMMA);
				setState(1819);
				grouping_element();
				}
				}
				setState(1824);
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
		enterRule(_localctx, 384, RULE_grouping_element);
		try {
			setState(1829);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,169,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1825);
				rollup_list();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1826);
				cube_list();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1827);
				empty_grouping_set();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1828);
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
		enterRule(_localctx, 386, RULE_ordinary_grouping_set_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1831);
			ordinary_grouping_set();
			setState(1836);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1832);
				match(COMMA);
				setState(1833);
				ordinary_grouping_set();
				}
				}
				setState(1838);
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
		enterRule(_localctx, 388, RULE_ordinary_grouping_set);
		try {
			setState(1844);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,171,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1839);
				row_value_predicand();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1840);
				match(LEFT_PAREN);
				setState(1841);
				row_value_predicand_list();
				setState(1842);
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
		enterRule(_localctx, 390, RULE_rollup_list);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1846);
			match(ROLLUP);
			setState(1847);
			match(LEFT_PAREN);
			setState(1848);
			((Rollup_listContext)_localctx).c = ordinary_grouping_set_list();
			setState(1849);
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
		enterRule(_localctx, 392, RULE_cube_list);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1851);
			match(CUBE);
			setState(1852);
			match(LEFT_PAREN);
			setState(1853);
			((Cube_listContext)_localctx).c = ordinary_grouping_set_list();
			setState(1854);
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
		enterRule(_localctx, 394, RULE_empty_grouping_set);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1856);
			match(LEFT_PAREN);
			setState(1857);
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
		enterRule(_localctx, 396, RULE_having_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1859);
			match(HAVING);
			setState(1860);
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
		enterRule(_localctx, 398, RULE_qualify_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1862);
			match(QUALIFY);
			setState(1863);
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
		enterRule(_localctx, 400, RULE_row_value_predicand_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1865);
			row_value_predicand();
			setState(1870);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1866);
				match(COMMA);
				setState(1867);
				row_value_predicand();
				}
				}
				setState(1872);
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
		enterRule(_localctx, 402, RULE_comparison_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1873);
			((Comparison_predicateContext)_localctx).left = row_value_predicand();
			setState(1874);
			((Comparison_predicateContext)_localctx).c = comparison_operator();
			setState(1875);
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
		enterRule(_localctx, 404, RULE_comparison_operator);
		int _la;
		try {
			setState(1883);
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
				setState(1877);
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
				setState(1879);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(1878);
					not();
					}
				}

				setState(1881);
				relative_comp_op();
				}
				break;
			case Similar_To:
			case Not_Similar_To:
			case Similar_To_Case_Insensitive:
			case Not_Similar_To_Case_Insensitive:
				enterOuterAlt(_localctx, 3);
				{
				setState(1882);
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
		enterRule(_localctx, 406, RULE_relative_comp_op);
		try {
			setState(1891);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LIKE:
				enterOuterAlt(_localctx, 1);
				{
				setState(1885);
				match(LIKE);
				}
				break;
			case ILIKE:
				enterOuterAlt(_localctx, 2);
				{
				setState(1886);
				match(ILIKE);
				}
				break;
			case SIMILAR:
				enterOuterAlt(_localctx, 3);
				{
				setState(1887);
				match(SIMILAR);
				setState(1888);
				match(TO);
				}
				break;
			case REGEXP:
				enterOuterAlt(_localctx, 4);
				{
				setState(1889);
				match(REGEXP);
				}
				break;
			case RLIKE:
				enterOuterAlt(_localctx, 5);
				{
				setState(1890);
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
		enterRule(_localctx, 408, RULE_similarity_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1893);
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
		enterRule(_localctx, 410, RULE_comp_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1895);
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
		enterRule(_localctx, 412, RULE_between_predicate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1897);
			row_value_predicand();
			setState(1899);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(1898);
				not();
				}
			}

			setState(1901);
			match(BETWEEN);
			setState(1903);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ASYMMETRIC || _la==SYMMETRIC) {
				{
				setState(1902);
				symmetry();
				}
			}

			setState(1905);
			((Between_predicateContext)_localctx).begin = row_value_predicand();
			setState(1906);
			match(AND);
			setState(1907);
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
		enterRule(_localctx, 414, RULE_symmetry);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1909);
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
		enterRule(_localctx, 416, RULE_in_predicate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1911);
			row_value_predicand();
			setState(1913);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(1912);
				not();
				}
			}

			setState(1915);
			match(IN);
			setState(1916);
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
		enterRule(_localctx, 418, RULE_like_any_predicate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1918);
			row_value_predicand();
			setState(1920);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(1919);
				not();
				}
			}

			setState(1922);
			like_any_operator();
			setState(1923);
			in_predicate_value();
			setState(1925);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,180,_ctx) ) {
			case 1:
				{
				setState(1924);
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
		enterRule(_localctx, 420, RULE_like_any_operator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1927);
			_la = _input.LA(1);
			if ( !(_la==ILIKE || _la==LIKE) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(1928);
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
		enterRule(_localctx, 422, RULE_in_predicate_value);
		try {
			setState(1936);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,181,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1930);
				subquery();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1931);
				match(LEFT_PAREN);
				setState(1932);
				in_value_list();
				setState(1933);
				match(RIGHT_PAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1935);
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
		enterRule(_localctx, 424, RULE_in_value_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1938);
			row_value_expression();
			setState(1943);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1939);
				match(COMMA);
				setState(1940);
				row_value_expression();
				}
				}
				setState(1945);
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
		enterRule(_localctx, 426, RULE_escape_character_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1946);
			match(ESCAPE);
			setState(1947);
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
		enterRule(_localctx, 428, RULE_values_statement_primary);
		try {
			setState(1952);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,183,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1949);
				fully_defined_values_statement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1950);
				aliased_values_statement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1951);
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
		enterRule(_localctx, 430, RULE_fully_defined_values_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1954);
			values_statement();
			setState(1955);
			as_clause();
			setState(1956);
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
		enterRule(_localctx, 432, RULE_aliased_values_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1958);
			values_statement();
			setState(1959);
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
		enterRule(_localctx, 434, RULE_values_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1961);
			match(LEFT_PAREN);
			setState(1962);
			match(VALUES);
			setState(1963);
			values_matrix();
			setState(1964);
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
		enterRule(_localctx, 436, RULE_values_matrix);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1966);
			values_row();
			setState(1971);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1967);
				match(COMMA);
				setState(1968);
				values_row();
				}
				}
				setState(1973);
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
		enterRule(_localctx, 438, RULE_values_row);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1974);
			match(LEFT_PAREN);
			setState(1975);
			in_value_list();
			setState(1976);
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
		enterRule(_localctx, 440, RULE_values_aliases);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(1978);
			match(LEFT_PAREN);
			setState(1979);
			values_aliases_list();
			setState(1980);
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
		enterRule(_localctx, 442, RULE_values_aliases_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1982);
			alias_identifier();
			setState(1987);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1983);
				match(COMMA);
				setState(1984);
				alias_identifier();
				}
				}
				setState(1989);
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
		enterRule(_localctx, 444, RULE_insert_values_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1990);
			match(VALUES);
			setState(1991);
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
		enterRule(_localctx, 446, RULE_exists_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1993);
			exists_operator();
			setState(1994);
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
		enterRule(_localctx, 448, RULE_exists_operator);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1996);
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
		enterRule(_localctx, 450, RULE_exists_predicate_value);
		try {
			setState(2000);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LEFT_PAREN:
				enterOuterAlt(_localctx, 1);
				{
				setState(1998);
				subquery();
				}
				break;
			case Variable_Identifier:
			case Extended_Variable_Identifier:
			case Mixed_Variable_Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(1999);
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
		enterRule(_localctx, 452, RULE_null_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2002);
			row_value_predicand();
			setState(2003);
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
		enterRule(_localctx, 454, RULE_is_null_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2005);
			match(IS);
			setState(2007);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(2006);
				((Is_null_clauseContext)_localctx).n = match(NOT);
				}
			}

			setState(2009);
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
		enterRule(_localctx, 456, RULE_is_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2011);
			match(IS);
			setState(2013);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(2012);
				not();
				}
			}

			setState(2015);
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
		enterRule(_localctx, 458, RULE_truth_value);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2017);
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
		enterRule(_localctx, 460, RULE_not);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2019);
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
		enterRule(_localctx, 462, RULE_quantified_comparison_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2021);
			((Quantified_comparison_predicateContext)_localctx).l = additive_expression();
			setState(2022);
			((Quantified_comparison_predicateContext)_localctx).c = comp_op();
			setState(2023);
			((Quantified_comparison_predicateContext)_localctx).q = quantifier();
			setState(2024);
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
		enterRule(_localctx, 464, RULE_quantifier);
		try {
			setState(2028);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ALL:
				enterOuterAlt(_localctx, 1);
				{
				setState(2026);
				all();
				}
				break;
			case ANY:
			case SOME:
				enterOuterAlt(_localctx, 2);
				{
				setState(2027);
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
		enterRule(_localctx, 466, RULE_all);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2030);
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
		enterRule(_localctx, 468, RULE_some);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2032);
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
		enterRule(_localctx, 470, RULE_unique_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2034);
			match(UNIQUE);
			setState(2035);
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
		enterRule(_localctx, 472, RULE_primary_datetime_field);
		try {
			setState(2039);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DAY:
			case HOUR:
			case MINUTE:
			case MONTH:
			case YEAR:
				enterOuterAlt(_localctx, 1);
				{
				setState(2037);
				non_second_primary_datetime_field();
				}
				break;
			case SECOND:
				enterOuterAlt(_localctx, 2);
				{
				setState(2038);
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
		enterRule(_localctx, 474, RULE_non_second_primary_datetime_field);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2041);
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
		enterRule(_localctx, 476, RULE_extended_datetime_field);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2043);
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
		enterRule(_localctx, 478, RULE_routine_invocation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2045);
			function_name();
			setState(2046);
			match(LEFT_PAREN);
			setState(2048);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -848827245298417264L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -288230377259008257L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -2097169L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -162133984631980033L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 8947490635775L) != 0) || ((((_la - 325)) & ~0x3f) == 0 && ((1L << (_la - 325)) & 87257225600436207L) != 0)) {
				{
				setState(2047);
				sql_argument_list();
				}
			}

			setState(2050);
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
		enterRule(_localctx, 480, RULE_function_name);
		int _la;
		try {
			setState(2058);
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
			case ALTER:
			case DATABASE:
			case FILE:
			case FUNCTION:
			case MACRO:
			case MATERIALIZED:
			case PROCEDURE:
			case RETURNS:
			case ROLE:
			case SCHEMA:
			case SEQUENCE:
			case STAGE:
			case USER:
			case VIEW:
			case Identifier:
			case Double_Quoted_Numeric_Identifier:
			case Dollar_Sign_Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(2052);
				identifier();
				setState(2055);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOT) {
					{
					setState(2053);
					match(DOT);
					setState(2054);
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
				setState(2057);
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
		enterRule(_localctx, 482, RULE_function_names_for_reserved_words);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2060);
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
		enterRule(_localctx, 484, RULE_sql_argument_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2062);
			value_expression();
			setState(2067);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(2063);
				match(COMMA);
				setState(2064);
				value_expression();
				}
				}
				setState(2069);
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
		enterRule(_localctx, 486, RULE_identifier);
		try {
			setState(2075);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(2070);
				simple_identifier();
				}
				break;
			case Bracket_Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(2071);
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
			case ALTER:
			case DATABASE:
			case FILE:
			case FUNCTION:
			case MACRO:
			case MATERIALIZED:
			case PROCEDURE:
			case RETURNS:
			case ROLE:
			case SCHEMA:
			case SEQUENCE:
			case STAGE:
			case USER:
			case VIEW:
				enterOuterAlt(_localctx, 3);
				{
				setState(2072);
				nonreserved_keywords();
				}
				break;
			case Double_Quoted_Numeric_Identifier:
				enterOuterAlt(_localctx, 4);
				{
				setState(2073);
				snowflake_quoted_numeric_identifier();
				}
				break;
			case Dollar_Sign_Identifier:
				enterOuterAlt(_localctx, 5);
				{
				setState(2074);
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
		enterRule(_localctx, 488, RULE_alias_identifier);
		try {
			setState(2082);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(2077);
				simple_identifier();
				}
				break;
			case Bracket_Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(2078);
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
			case ALTER:
			case DATABASE:
			case FILE:
			case FUNCTION:
			case MACRO:
			case MATERIALIZED:
			case PROCEDURE:
			case RETURNS:
			case ROLE:
			case SCHEMA:
			case SEQUENCE:
			case STAGE:
			case USER:
			case VIEW:
				enterOuterAlt(_localctx, 3);
				{
				setState(2079);
				nonreserved_keywords();
				}
				break;
			case NUMBER:
			case Numeric_Identifier:
				enterOuterAlt(_localctx, 4);
				{
				setState(2080);
				simple_numeric_identifier();
				}
				break;
			case Double_Quoted_Numeric_Identifier:
				enterOuterAlt(_localctx, 5);
				{
				setState(2081);
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
		enterRule(_localctx, 490, RULE_variable_identifier);
		try {
			setState(2086);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Variable_Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(2084);
				simple_variable_identifier();
				}
				break;
			case Extended_Variable_Identifier:
			case Mixed_Variable_Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(2085);
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
		enterRule(_localctx, 492, RULE_simple_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2088);
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
		enterRule(_localctx, 494, RULE_logical_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2090);
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
		enterRule(_localctx, 496, RULE_simple_variable_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2092);
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
		enterRule(_localctx, 498, RULE_extended_variable_identifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2094);
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
		enterRule(_localctx, 500, RULE_jinja_identifier);
		try {
			setState(2104);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,198,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2096);
				match(JINJA_OPEN);
				setState(2097);
				jinja_function_call();
				setState(2098);
				match(JINJA_CLOSE);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2100);
				match(JINJA_OPEN);
				setState(2101);
				jinja_variable_access();
				setState(2102);
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
		enterRule(_localctx, 502, RULE_jinja_function_call);
		int _la;
		try {
			setState(2124);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,201,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2106);
				((Jinja_function_callContext)_localctx).func_name = identifier();
				setState(2107);
				if (!(isJinjaPrimaryFunction((((Jinja_function_callContext)_localctx).func_name!=null?_input.getText(((Jinja_function_callContext)_localctx).func_name.start,((Jinja_function_callContext)_localctx).func_name.stop):null)))) throw new FailedPredicateException(this, "isJinjaPrimaryFunction($func_name.text)");
				setState(2108);
				match(LEFT_PAREN);
				setState(2110);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -855661835887575040L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -292734320500539713L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -2097169L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -162133984631980033L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 8796093038591L) != 0) || ((((_la - 325)) & ~0x3f) == 0 && ((1L << (_la - 325)) & 86131325693591553L) != 0)) {
					{
					setState(2109);
					jinja_arg_list();
					}
				}

				setState(2112);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2114);
				((Jinja_function_callContext)_localctx).object_name = identifier();
				setState(2115);
				match(DOT);
				setState(2116);
				((Jinja_function_callContext)_localctx).method_name = identifier();
				setState(2117);
				if (!(isJinjaObjectMethod((((Jinja_function_callContext)_localctx).object_name!=null?_input.getText(((Jinja_function_callContext)_localctx).object_name.start,((Jinja_function_callContext)_localctx).object_name.stop):null), (((Jinja_function_callContext)_localctx).method_name!=null?_input.getText(((Jinja_function_callContext)_localctx).method_name.start,((Jinja_function_callContext)_localctx).method_name.stop):null)))) throw new FailedPredicateException(this, "isJinjaObjectMethod($object_name.text, $method_name.text)");
				setState(2118);
				match(LEFT_PAREN);
				setState(2120);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -855661835887575040L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -292734320500539713L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -2097169L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -162133984631980033L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 8796093038591L) != 0) || ((((_la - 325)) & ~0x3f) == 0 && ((1L << (_la - 325)) & 86131325693591553L) != 0)) {
					{
					setState(2119);
					jinja_arg_list();
					}
				}

				setState(2122);
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
		enterRule(_localctx, 504, RULE_jinja_arg_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2126);
			jinja_arg();
			setState(2131);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(2127);
				match(COMMA);
				setState(2128);
				jinja_arg();
				}
				}
				setState(2133);
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
		enterRule(_localctx, 506, RULE_jinja_arg);
		try {
			setState(2144);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,203,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2134);
				match(Character_String_Literal);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2135);
				match(NUMBER);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2136);
				((Jinja_argContext)_localctx).kw_name = identifier();
				setState(2137);
				match(EQUAL);
				setState(2138);
				match(Character_String_Literal);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2140);
				((Jinja_argContext)_localctx).kw_name = identifier();
				setState(2141);
				match(EQUAL);
				setState(2142);
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
		enterRule(_localctx, 508, RULE_jinja_variable_access);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2146);
			jinja_name();
			setState(2151);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DOT) {
				{
				{
				setState(2147);
				match(DOT);
				setState(2148);
				jinja_name();
				}
				}
				setState(2153);
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
		enterRule(_localctx, 510, RULE_jinja_name);
		try {
			setState(2156);
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
			case ALTER:
			case DATABASE:
			case FILE:
			case FUNCTION:
			case MACRO:
			case MATERIALIZED:
			case PROCEDURE:
			case RETURNS:
			case ROLE:
			case SCHEMA:
			case SEQUENCE:
			case STAGE:
			case USER:
			case VIEW:
			case Identifier:
			case Double_Quoted_Numeric_Identifier:
			case Dollar_Sign_Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(2154);
				identifier();
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(2155);
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
		enterRule(_localctx, 512, RULE_simple_numeric_identifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2158);
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
		enterRule(_localctx, 514, RULE_snowflake_quoted_numeric_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2160);
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
		enterRule(_localctx, 516, RULE_snowflake_dollar_function_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2162);
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
		public TerminalNode ALTER() { return getToken(SQLSelectParserParser.ALTER, 0); }
		public TerminalNode DATABASE() { return getToken(SQLSelectParserParser.DATABASE, 0); }
		public TerminalNode FILE() { return getToken(SQLSelectParserParser.FILE, 0); }
		public TerminalNode FUNCTION() { return getToken(SQLSelectParserParser.FUNCTION, 0); }
		public TerminalNode MACRO() { return getToken(SQLSelectParserParser.MACRO, 0); }
		public TerminalNode MATERIALIZED() { return getToken(SQLSelectParserParser.MATERIALIZED, 0); }
		public TerminalNode PROCEDURE() { return getToken(SQLSelectParserParser.PROCEDURE, 0); }
		public TerminalNode RETURNS() { return getToken(SQLSelectParserParser.RETURNS, 0); }
		public TerminalNode ROLE() { return getToken(SQLSelectParserParser.ROLE, 0); }
		public TerminalNode SCHEMA() { return getToken(SQLSelectParserParser.SCHEMA, 0); }
		public TerminalNode SEQUENCE() { return getToken(SQLSelectParserParser.SEQUENCE, 0); }
		public TerminalNode STAGE() { return getToken(SQLSelectParserParser.STAGE, 0); }
		public TerminalNode USER() { return getToken(SQLSelectParserParser.USER, 0); }
		public TerminalNode VIEW() { return getToken(SQLSelectParserParser.VIEW, 0); }
		public Nonreserved_keywordsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nonreserved_keywords; }
	}

	public final Nonreserved_keywordsContext nonreserved_keywords() throws RecognitionException {
		Nonreserved_keywordsContext _localctx = new Nonreserved_keywordsContext(_ctx, getState());
		enterRule(_localctx, 518, RULE_nonreserved_keywords);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2164);
			_la = _input.LA(1);
			if ( !(((((_la - 20)) & ~0x3f) == 0 && ((1L << (_la - 20)) & -5630315556929535L) != 0) || ((((_la - 84)) & ~0x3f) == 0 && ((1L << (_la - 84)) & -281754149913649L) != 0) || ((((_la - 148)) & ~0x3f) == 0 && ((1L << (_la - 148)) & -2305843009213693955L) != 0) || ((((_la - 212)) & ~0x3f) == 0 && ((1L << (_la - 212)) & 288230221528694783L) != 0) || ((((_la - 359)) & ~0x3f) == 0 && ((1L << (_la - 359)) & 16383L) != 0)) ) {
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
		enterRule(_localctx, 520, RULE_signed_numeric_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2167);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PLUS || _la==MINUS) {
				{
				setState(2166);
				sign();
				}
			}

			setState(2169);
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
		enterRule(_localctx, 522, RULE_unsigned_literal);
		try {
			setState(2173);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DOT:
			case NUMBER:
			case Scientific_Numeric_Literal:
				enterOuterAlt(_localctx, 1);
				{
				setState(2171);
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
				setState(2172);
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
		enterRule(_localctx, 524, RULE_unsigned_numeric_literal);
		try {
			setState(2177);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,208,_ctx) ) {
			case 1:
				_localctx = new Ordinal_numberContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(2175);
				match(NUMBER);
				}
				break;
			case 2:
				_localctx = new Real_numberContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(2176);
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
		enterRule(_localctx, 526, RULE_real_number_def);
		try {
			setState(2195);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,212,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2179);
				match(NUMBER);
				setState(2180);
				match(DOT);
				setState(2182);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,209,_ctx) ) {
				case 1:
					{
					setState(2181);
					match(NUMBER);
					}
					break;
				}
				setState(2185);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,210,_ctx) ) {
				case 1:
					{
					setState(2184);
					exponent();
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2187);
				match(DOT);
				setState(2188);
				match(NUMBER);
				setState(2190);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,211,_ctx) ) {
				case 1:
					{
					setState(2189);
					exponent();
					}
					break;
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2192);
				match(NUMBER);
				setState(2193);
				exponent();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2194);
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
		enterRule(_localctx, 528, RULE_exponent);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2197);
			match(EXPONEN);
			setState(2198);
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
		enterRule(_localctx, 530, RULE_general_literal);
		try {
			setState(2203);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Character_String_Literal:
				enterOuterAlt(_localctx, 1);
				{
				setState(2200);
				character_literal();
				}
				break;
			case DATE:
			case TIME:
			case TIMESTAMP:
				enterOuterAlt(_localctx, 2);
				{
				setState(2201);
				datetime_literal();
				}
				break;
			case FALSE:
			case TRUE:
			case UNKNOWN:
				enterOuterAlt(_localctx, 3);
				{
				setState(2202);
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
		enterRule(_localctx, 532, RULE_character_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2205);
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
		enterRule(_localctx, 534, RULE_datetime_literal);
		try {
			setState(2210);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TIMESTAMP:
				enterOuterAlt(_localctx, 1);
				{
				setState(2207);
				timestamp_literal();
				}
				break;
			case TIME:
				enterOuterAlt(_localctx, 2);
				{
				setState(2208);
				time_literal();
				}
				break;
			case DATE:
				enterOuterAlt(_localctx, 3);
				{
				setState(2209);
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
		enterRule(_localctx, 536, RULE_time_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2212);
			match(TIME);
			setState(2213);
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
		enterRule(_localctx, 538, RULE_timestamp_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2215);
			match(TIMESTAMP);
			setState(2216);
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
		enterRule(_localctx, 540, RULE_date_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2218);
			match(DATE);
			setState(2219);
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
		enterRule(_localctx, 542, RULE_boolean_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2221);
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
		enterRule(_localctx, 544, RULE_data_type);
		try {
			setState(2226);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,215,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2223);
				variable_size_data_type();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2224);
				precision_scale_data_type();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2225);
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
		enterRule(_localctx, 546, RULE_variable_size_data_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2228);
			variable_data_type_name();
			setState(2230);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,216,_ctx) ) {
			case 1:
				{
				setState(2229);
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
		enterRule(_localctx, 548, RULE_variable_data_type_name);
		try {
			setState(2266);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,217,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2232);
				match(CHARACTER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2233);
				match(CHAR);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2234);
				match(CHARACTER);
				setState(2235);
				match(VARYING);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2236);
				match(CHAR);
				setState(2237);
				match(VARYING);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2238);
				match(VARCHAR);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2239);
				match(VARCHAR2);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(2240);
				match(NATIONAL);
				setState(2241);
				match(CHARACTER);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(2242);
				match(NATIONAL);
				setState(2243);
				match(CHAR);
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(2244);
				match(NCHAR);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(2245);
				match(NATIONAL);
				setState(2246);
				match(CHARACTER);
				setState(2247);
				match(VARYING);
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(2248);
				match(NATIONAL);
				setState(2249);
				match(CHAR);
				setState(2250);
				match(VARYING);
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(2251);
				match(NCHAR);
				setState(2252);
				match(VARYING);
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(2253);
				match(NVARCHAR);
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(2254);
				match(BLOB);
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				setState(2255);
				match(BYTEA);
				}
				break;
			case 16:
				enterOuterAlt(_localctx, 16);
				{
				setState(2256);
				match(BIT);
				}
				break;
			case 17:
				enterOuterAlt(_localctx, 17);
				{
				setState(2257);
				match(VARBIT);
				}
				break;
			case 18:
				enterOuterAlt(_localctx, 18);
				{
				setState(2258);
				match(BIT);
				setState(2259);
				match(VARYING);
				}
				break;
			case 19:
				enterOuterAlt(_localctx, 19);
				{
				setState(2260);
				match(BINARY);
				}
				break;
			case 20:
				enterOuterAlt(_localctx, 20);
				{
				setState(2261);
				match(BINARY);
				setState(2262);
				match(VARYING);
				}
				break;
			case 21:
				enterOuterAlt(_localctx, 21);
				{
				setState(2263);
				match(VARBINARY);
				}
				break;
			case 22:
				enterOuterAlt(_localctx, 22);
				{
				setState(2264);
				match(INTERVAL);
				}
				break;
			case 23:
				enterOuterAlt(_localctx, 23);
				{
				setState(2265);
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
		enterRule(_localctx, 550, RULE_type_length);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2268);
			match(LEFT_PAREN);
			setState(2269);
			match(NUMBER);
			setState(2270);
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
		enterRule(_localctx, 552, RULE_precision_scale_data_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2272);
			precision_data_type_name();
			setState(2274);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,218,_ctx) ) {
			case 1:
				{
				setState(2273);
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
		enterRule(_localctx, 554, RULE_precision_data_type_name);
		try {
			setState(2287);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,219,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2276);
				match(NUMERIC);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2277);
				match(NUMBER);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2278);
				match(DECIMAL);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2279);
				match(DEC);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2280);
				match(FLOAT);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2281);
				match(DOUBLE);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(2282);
				match(DOUBLE);
				setState(2283);
				match(PRECISION);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(2284);
				match(TIMESTAMP_LTZ);
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(2285);
				match(TIMESTAMP_NTZ);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(2286);
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
		enterRule(_localctx, 556, RULE_precision_param);
		try {
			setState(2297);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,220,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2289);
				match(LEFT_PAREN);
				setState(2290);
				((Precision_paramContext)_localctx).precision = match(NUMBER);
				setState(2291);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2292);
				match(LEFT_PAREN);
				setState(2293);
				((Precision_paramContext)_localctx).precision = match(NUMBER);
				setState(2294);
				match(COMMA);
				setState(2295);
				((Precision_paramContext)_localctx).scale = match(NUMBER);
				setState(2296);
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
		enterRule(_localctx, 558, RULE_static_data_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2299);
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
		enterRule(_localctx, 560, RULE_static_data_type_name);
		try {
			setState(2362);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,221,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2301);
				match(TEXT);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2302);
				match(NAME);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2303);
				match(INET4);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2304);
				match(INET);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2305);
				match(CIDR);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2306);
				match(STRUCT);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(2307);
				match(UNION);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(2308);
				match(VARIANT);
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(2309);
				match(OBJECT);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(2310);
				match(JSON);
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(2311);
				match(JSONB);
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(2312);
				match(OID);
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(2313);
				match(XID);
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(2314);
				match(UUID);
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				setState(2315);
				match(PG_LSN);
				}
				break;
			case 16:
				enterOuterAlt(_localctx, 16);
				{
				setState(2316);
				match(PG_NODE_TREE);
				}
				break;
			case 17:
				enterOuterAlt(_localctx, 17);
				{
				setState(2317);
				match(REGPROC);
				}
				break;
			case 18:
				enterOuterAlt(_localctx, 18);
				{
				setState(2318);
				match(MACADDR);
				}
				break;
			case 19:
				enterOuterAlt(_localctx, 19);
				{
				setState(2319);
				match(INT1);
				}
				break;
			case 20:
				enterOuterAlt(_localctx, 20);
				{
				setState(2320);
				match(TINYINT);
				}
				break;
			case 21:
				enterOuterAlt(_localctx, 21);
				{
				setState(2321);
				match(INT2);
				}
				break;
			case 22:
				enterOuterAlt(_localctx, 22);
				{
				setState(2322);
				match(SMALLINT);
				}
				break;
			case 23:
				enterOuterAlt(_localctx, 23);
				{
				setState(2323);
				match(INT4);
				}
				break;
			case 24:
				enterOuterAlt(_localctx, 24);
				{
				setState(2324);
				match(INT);
				}
				break;
			case 25:
				enterOuterAlt(_localctx, 25);
				{
				setState(2325);
				match(INTEGER);
				}
				break;
			case 26:
				enterOuterAlt(_localctx, 26);
				{
				setState(2326);
				match(INT8);
				}
				break;
			case 27:
				enterOuterAlt(_localctx, 27);
				{
				setState(2327);
				match(BIGINT);
				}
				break;
			case 28:
				enterOuterAlt(_localctx, 28);
				{
				setState(2328);
				match(BIGSERIAL);
				}
				break;
			case 29:
				enterOuterAlt(_localctx, 29);
				{
				setState(2329);
				match(SMALLSERIAL);
				}
				break;
			case 30:
				enterOuterAlt(_localctx, 30);
				{
				setState(2330);
				match(SERIAL);
				}
				break;
			case 31:
				enterOuterAlt(_localctx, 31);
				{
				setState(2331);
				match(MONEY);
				}
				break;
			case 32:
				enterOuterAlt(_localctx, 32);
				{
				setState(2332);
				match(NUMBER_TYPE);
				}
				break;
			case 33:
				enterOuterAlt(_localctx, 33);
				{
				setState(2333);
				match(FLOAT4);
				}
				break;
			case 34:
				enterOuterAlt(_localctx, 34);
				{
				setState(2334);
				match(REAL);
				}
				break;
			case 35:
				enterOuterAlt(_localctx, 35);
				{
				setState(2335);
				match(FLOAT8);
				}
				break;
			case 36:
				enterOuterAlt(_localctx, 36);
				{
				setState(2336);
				match(BOOLEAN);
				}
				break;
			case 37:
				enterOuterAlt(_localctx, 37);
				{
				setState(2337);
				match(BOOL);
				}
				break;
			case 38:
				enterOuterAlt(_localctx, 38);
				{
				setState(2338);
				match(DATE);
				}
				break;
			case 39:
				enterOuterAlt(_localctx, 39);
				{
				setState(2339);
				match(DATETIME);
				}
				break;
			case 40:
				enterOuterAlt(_localctx, 40);
				{
				setState(2340);
				match(TIME);
				}
				break;
			case 41:
				enterOuterAlt(_localctx, 41);
				{
				setState(2341);
				match(TIME);
				setState(2342);
				match(WITH);
				setState(2343);
				match(TIME);
				setState(2344);
				match(ZONE);
				}
				break;
			case 42:
				enterOuterAlt(_localctx, 42);
				{
				setState(2345);
				match(TIMETZ);
				}
				break;
			case 43:
				enterOuterAlt(_localctx, 43);
				{
				setState(2346);
				match(TIMESTAMP_LTZ);
				}
				break;
			case 44:
				enterOuterAlt(_localctx, 44);
				{
				setState(2347);
				match(TIMESTAMP_NTZ);
				}
				break;
			case 45:
				enterOuterAlt(_localctx, 45);
				{
				setState(2348);
				match(TIMESTAMP_TZ);
				}
				break;
			case 46:
				enterOuterAlt(_localctx, 46);
				{
				setState(2349);
				match(TIMESTAMP);
				}
				break;
			case 47:
				enterOuterAlt(_localctx, 47);
				{
				setState(2350);
				match(TIMESTAMP);
				setState(2351);
				match(WITH);
				setState(2352);
				match(TIME);
				setState(2353);
				match(ZONE);
				}
				break;
			case 48:
				enterOuterAlt(_localctx, 48);
				{
				setState(2354);
				match(TIMESTAMP);
				setState(2355);
				match(WITHOUT);
				setState(2356);
				match(TIME);
				setState(2357);
				match(ZONE);
				}
				break;
			case 49:
				enterOuterAlt(_localctx, 49);
				{
				setState(2358);
				match(TIMESTAMPTZ);
				}
				break;
			case 50:
				enterOuterAlt(_localctx, 50);
				{
				setState(2359);
				match(ABSTIME);
				}
				break;
			case 51:
				enterOuterAlt(_localctx, 51);
				{
				setState(2360);
				match(ARRAY);
				}
				break;
			case 52:
				enterOuterAlt(_localctx, 52);
				{
				setState(2361);
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
		enterRule(_localctx, 562, RULE_puml_constant_identifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2364);
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
		case 76:
			return relation_as_clause_sempred((Relation_as_clauseContext)_localctx, predIndex);
		case 89:
			return flatten_argument_list_sempred((Flatten_argument_listContext)_localctx, predIndex);
		case 90:
			return flatten_argument_sempred((Flatten_argumentContext)_localctx, predIndex);
		case 96:
			return generator_argument_list_sempred((Generator_argument_listContext)_localctx, predIndex);
		case 103:
			return infer_schema_argument_list_sempred((Infer_schema_argument_listContext)_localctx, predIndex);
		case 251:
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
		"\u0004\u0001\u0182\u093f\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
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
		"\u00fe\u0002\u00ff\u0007\u00ff\u0002\u0100\u0007\u0100\u0002\u0101\u0007"+
		"\u0101\u0002\u0102\u0007\u0102\u0002\u0103\u0007\u0103\u0002\u0104\u0007"+
		"\u0104\u0002\u0105\u0007\u0105\u0002\u0106\u0007\u0106\u0002\u0107\u0007"+
		"\u0107\u0002\u0108\u0007\u0108\u0002\u0109\u0007\u0109\u0002\u010a\u0007"+
		"\u010a\u0002\u010b\u0007\u010b\u0002\u010c\u0007\u010c\u0002\u010d\u0007"+
		"\u010d\u0002\u010e\u0007\u010e\u0002\u010f\u0007\u010f\u0002\u0110\u0007"+
		"\u0110\u0002\u0111\u0007\u0111\u0002\u0112\u0007\u0112\u0002\u0113\u0007"+
		"\u0113\u0002\u0114\u0007\u0114\u0002\u0115\u0007\u0115\u0002\u0116\u0007"+
		"\u0116\u0002\u0117\u0007\u0117\u0002\u0118\u0007\u0118\u0002\u0119\u0007"+
		"\u0119\u0001\u0000\u0005\u0000\u0236\b\u0000\n\u0000\f\u0000\u0239\t\u0000"+
		"\u0001\u0000\u0003\u0000\u023c\b\u0000\u0001\u0000\u0001\u0000\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0003\u0001\u0243\b\u0001\u0001\u0001\u0003\u0001"+
		"\u0246\b\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0003\u0003\u024e\b\u0003\u0001\u0003\u0003\u0003\u0251\b"+
		"\u0003\u0001\u0004\u0001\u0004\u0003\u0004\u0255\b\u0004\u0001\u0004\u0003"+
		"\u0004\u0258\b\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n"+
		"\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0003\f\u0273"+
		"\b\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u028b\b\u000f\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u0291\b\u0010\u0001\u0011\u0001"+
		"\u0011\u0001\u0011\u0001\u0011\u0003\u0011\u0297\b\u0011\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0003"+
		"\u0012\u02a6\b\u0012\u0001\u0012\u0003\u0012\u02a9\b\u0012\u0003\u0012"+
		"\u02ab\b\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u02c8\b\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0017"+
		"\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0003\u0017\u02d4\b\u0017"+
		"\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0018"+
		"\u0001\u0018\u0001\u0018\u0003\u0018\u02de\b\u0018\u0001\u0018\u0001\u0018"+
		"\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019"+
		"\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0003\u001a"+
		"\u02ed\b\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0003\u001b"+
		"\u02f3\b\u001b\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0003\u001c"+
		"\u02f9\b\u001c\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0003\u001d"+
		"\u02ff\b\u001d\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0003\u001e"+
		"\u0305\b\u001e\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f"+
		"\u0003\u001f\u030c\b\u001f\u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0001"+
		" \u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0003"+
		" \u031e\b \u0001!\u0001!\u0001\"\u0001\"\u0001#\u0004#\u0325\b#\u000b"+
		"#\f#\u0326\u0001$\u0004$\u032a\b$\u000b$\f$\u032b\u0001%\u0001%\u0001"+
		"%\u0001&\u0001&\u0001&\u0001&\u0005&\u0335\b&\n&\f&\u0338\t&\u0001\'\u0001"+
		"\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0003\'\u0342\b\'\u0001"+
		"(\u0001(\u0003(\u0346\b(\u0001)\u0001)\u0001)\u0001*\u0001*\u0001*\u0001"+
		"*\u0003*\u034f\b*\u0001+\u0001+\u0001,\u0001,\u0001,\u0001,\u0001-\u0001"+
		"-\u0001-\u0003-\u035a\b-\u0001-\u0001-\u0001-\u0001-\u0003-\u0360\b-\u0001"+
		"-\u0003-\u0363\b-\u0001.\u0001.\u0003.\u0367\b.\u0001/\u0001/\u0003/\u036b"+
		"\b/\u0001/\u0001/\u00010\u00010\u00010\u00030\u0372\b0\u00011\u00011\u0001"+
		"1\u00011\u00011\u00031\u0379\b1\u00011\u00031\u037c\b1\u00011\u00031\u037f"+
		"\b1\u00012\u00012\u00012\u00013\u00013\u00013\u00053\u0387\b3\n3\f3\u038a"+
		"\t3\u00014\u00014\u00014\u00014\u00015\u00015\u00016\u00016\u00016\u0001"+
		"6\u00056\u0396\b6\n6\f6\u0399\t6\u00017\u00017\u00037\u039d\b7\u00018"+
		"\u00018\u00019\u00019\u00019\u00019\u00059\u03a5\b9\n9\f9\u03a8\t9\u0001"+
		":\u0001:\u0003:\u03ac\b:\u0001;\u0001;\u0001<\u0001<\u0001<\u0003<\u03b3"+
		"\b<\u0001=\u0001=\u0001=\u0001=\u0001>\u0001>\u0003>\u03bb\b>\u0001>\u0003"+
		">\u03be\b>\u0001>\u0001>\u0001>\u0003>\u03c3\b>\u0001>\u0003>\u03c6\b"+
		">\u0001>\u0003>\u03c9\b>\u0001>\u0003>\u03cc\b>\u0001>\u0003>\u03cf\b"+
		">\u0001>\u0003>\u03d2\b>\u0003>\u03d4\b>\u0001?\u0001?\u0001?\u0001@\u0001"+
		"@\u0001A\u0001A\u0001A\u0005A\u03de\bA\nA\fA\u03e1\tA\u0001B\u0001B\u0003"+
		"B\u03e5\bB\u0001B\u0003B\u03e8\bB\u0001C\u0003C\u03eb\bC\u0001C\u0001"+
		"C\u0001D\u0001D\u0001E\u0001E\u0003E\u03f3\bE\u0001E\u0001E\u0001F\u0001"+
		"F\u0001F\u0003F\u03fa\bF\u0001G\u0001G\u0001H\u0001H\u0001H\u0003H\u0401"+
		"\bH\u0001H\u0001H\u0001H\u0003H\u0406\bH\u0001H\u0001H\u0001H\u0001H\u0003"+
		"H\u040c\bH\u0001H\u0001H\u0003H\u0410\bH\u0005H\u0412\bH\nH\fH\u0415\t"+
		"H\u0001I\u0001I\u0003I\u0419\bI\u0001I\u0001I\u0001I\u0003I\u041e\bI\u0001"+
		"I\u0001I\u0001I\u0001I\u0003I\u0424\bI\u0001I\u0001I\u0003I\u0428\bI\u0005"+
		"I\u042a\bI\nI\fI\u042d\tI\u0001I\u0003I\u0430\bI\u0001J\u0001J\u0001K"+
		"\u0001K\u0003K\u0436\bK\u0001K\u0001K\u0001K\u0001K\u0001K\u0003K\u043d"+
		"\bK\u0001K\u0001K\u0003K\u0441\bK\u0001K\u0001K\u0001K\u0003K\u0446\b"+
		"K\u0003K\u0448\bK\u0001L\u0001L\u0001L\u0001L\u0003L\u044e\bL\u0001M\u0001"+
		"M\u0001M\u0001M\u0001M\u0001M\u0003M\u0456\bM\u0001N\u0001N\u0001N\u0001"+
		"N\u0003N\u045c\bN\u0003N\u045e\bN\u0001N\u0001N\u0001N\u0003N\u0463\b"+
		"N\u0003N\u0465\bN\u0001O\u0001O\u0001O\u0001O\u0001O\u0001O\u0003O\u046d"+
		"\bO\u0001O\u0003O\u0470\bO\u0001P\u0003P\u0473\bP\u0001P\u0001P\u0001"+
		"Q\u0001Q\u0001Q\u0003Q\u047a\bQ\u0003Q\u047c\bQ\u0001R\u0001R\u0003R\u0480"+
		"\bR\u0001S\u0001S\u0001S\u0001T\u0001T\u0001T\u0001T\u0001T\u0001U\u0001"+
		"U\u0001V\u0001V\u0001V\u0001V\u0001V\u0001V\u0003V\u0492\bV\u0001W\u0001"+
		"W\u0001W\u0001W\u0001W\u0001W\u0003W\u049a\bW\u0001X\u0001X\u0001X\u0001"+
		"X\u0001X\u0001Y\u0001Y\u0001Y\u0005Y\u04a4\bY\nY\fY\u04a7\tY\u0001Y\u0001"+
		"Y\u0001Z\u0001Z\u0001Z\u0001Z\u0001Z\u0001Z\u0001Z\u0001Z\u0001Z\u0001"+
		"Z\u0001Z\u0001Z\u0001Z\u0001Z\u0001Z\u0001Z\u0001Z\u0003Z\u04bc\bZ\u0001"+
		"[\u0001[\u0001[\u0003[\u04c1\b[\u0001\\\u0001\\\u0001]\u0001]\u0003]\u04c7"+
		"\b]\u0001^\u0001^\u0001_\u0001_\u0001_\u0001_\u0001_\u0001`\u0001`\u0001"+
		"`\u0005`\u04d3\b`\n`\f`\u04d6\t`\u0001`\u0001`\u0001a\u0001a\u0001a\u0001"+
		"a\u0001a\u0001a\u0003a\u04e0\ba\u0001b\u0001b\u0001c\u0001c\u0001d\u0001"+
		"d\u0001d\u0003d\u04e9\bd\u0001d\u0001d\u0001e\u0001e\u0001f\u0001f\u0001"+
		"f\u0001f\u0001f\u0001g\u0001g\u0001g\u0005g\u04f7\bg\ng\fg\u04fa\tg\u0001"+
		"g\u0001g\u0001h\u0001h\u0001h\u0001h\u0001h\u0001h\u0001h\u0001h\u0001"+
		"h\u0001h\u0001h\u0001h\u0001h\u0001h\u0001h\u0001h\u0001h\u0001h\u0001"+
		"h\u0001h\u0001h\u0003h\u0513\bh\u0001i\u0001i\u0001i\u0001i\u0003i\u0519"+
		"\bi\u0001j\u0001j\u0001k\u0001k\u0001k\u0001k\u0005k\u0521\bk\nk\fk\u0524"+
		"\tk\u0001k\u0001k\u0001l\u0001l\u0001l\u0001l\u0001l\u0001l\u0001l\u0001"+
		"l\u0001l\u0001m\u0001m\u0001n\u0001n\u0001n\u0003n\u0536\bn\u0001n\u0001"+
		"n\u0001o\u0001o\u0001o\u0001o\u0003o\u053e\bo\u0001p\u0001p\u0001p\u0005"+
		"p\u0543\bp\np\fp\u0546\tp\u0001q\u0001q\u0001q\u0005q\u054b\bq\nq\fq\u054e"+
		"\tq\u0001r\u0001r\u0001r\u0003r\u0553\br\u0001r\u0001r\u0001r\u0005r\u0558"+
		"\br\nr\fr\u055b\tr\u0001r\u0001r\u0001r\u0001r\u0003r\u0561\br\u0001s"+
		"\u0001s\u0001s\u0003s\u0566\bs\u0001s\u0001s\u0001s\u0005s\u056b\bs\n"+
		"s\fs\u056e\ts\u0001s\u0001s\u0001s\u0001s\u0001s\u0003s\u0575\bs\u0001"+
		"t\u0001t\u0001t\u0001t\u0001t\u0001t\u0001t\u0001t\u0001t\u0001t\u0003"+
		"t\u0581\bt\u0001u\u0001u\u0001u\u0003u\u0586\bu\u0001u\u0001u\u0001u\u0003"+
		"u\u058b\bu\u0003u\u058d\bu\u0001v\u0001v\u0001v\u0001v\u0001w\u0001w\u0001"+
		"w\u0001w\u0001w\u0001w\u0001w\u0001w\u0001w\u0003w\u059c\bw\u0001x\u0001"+
		"x\u0001y\u0001y\u0001y\u0001y\u0001y\u0001y\u0001y\u0003y\u05a7\by\u0001"+
		"y\u0001y\u0003y\u05ab\by\u0001y\u0001y\u0001y\u0003y\u05b0\by\u0001z\u0001"+
		"z\u0001{\u0001{\u0001|\u0001|\u0001|\u0001|\u0003|\u05ba\b|\u0001|\u0001"+
		"|\u0001|\u0001|\u0001|\u0003|\u05c1\b|\u0001|\u0001|\u0003|\u05c5\b|\u0001"+
		"}\u0004}\u05c8\b}\u000b}\f}\u05c9\u0001~\u0001~\u0001~\u0001~\u0001~\u0001"+
		"\u007f\u0004\u007f\u05d2\b\u007f\u000b\u007f\f\u007f\u05d3\u0001\u0080"+
		"\u0001\u0080\u0001\u0080\u0001\u0080\u0001\u0080\u0001\u0081\u0001\u0081"+
		"\u0001\u0081\u0001\u0082\u0001\u0082\u0003\u0082\u05e0\b\u0082\u0001\u0083"+
		"\u0001\u0083\u0001\u0084\u0001\u0084\u0001\u0084\u0001\u0084\u0001\u0084"+
		"\u0001\u0084\u0001\u0084\u0001\u0085\u0001\u0085\u0001\u0086\u0001\u0086"+
		"\u0001\u0086\u0001\u0087\u0001\u0087\u0001\u0087\u0003\u0087\u05f3\b\u0087"+
		"\u0001\u0087\u0001\u0087\u0001\u0087\u0001\u0087\u0001\u0087\u0001\u0087"+
		"\u0001\u0087\u0003\u0087\u05fc\b\u0087\u0001\u0087\u0003\u0087\u05ff\b"+
		"\u0087\u0003\u0087\u0601\b\u0087\u0001\u0088\u0001\u0088\u0001\u0088\u0003"+
		"\u0088\u0606\b\u0088\u0001\u0088\u0003\u0088\u0609\b\u0088\u0001\u0088"+
		"\u0003\u0088\u060c\b\u0088\u0001\u0088\u0001\u0088\u0001\u0089\u0001\u0089"+
		"\u0001\u0089\u0001\u0089\u0001\u008a\u0001\u008a\u0001\u008a\u0001\u008b"+
		"\u0001\u008b\u0001\u008c\u0001\u008c\u0001\u008c\u0003\u008c\u061c\b\u008c"+
		"\u0001\u008d\u0001\u008d\u0001\u008d\u0001\u008d\u0001\u008d\u0001\u008e"+
		"\u0001\u008e\u0001\u008e\u0003\u008e\u0626\b\u008e\u0001\u008f\u0001\u008f"+
		"\u0001\u008f\u0001\u0090\u0001\u0090\u0001\u0090\u0001\u0091\u0001\u0091"+
		"\u0001\u0091\u0001\u0092\u0001\u0092\u0001\u0093\u0001\u0093\u0001\u0094"+
		"\u0001\u0094\u0001\u0094\u0001\u0095\u0001\u0095\u0001\u0095\u0001\u0096"+
		"\u0001\u0096\u0001\u0096\u0001\u0096\u0003\u0096\u063f\b\u0096\u0001\u0097"+
		"\u0001\u0097\u0001\u0097\u0003\u0097\u0644\b\u0097\u0001\u0098\u0001\u0098"+
		"\u0001\u0098\u0005\u0098\u0649\b\u0098\n\u0098\f\u0098\u064c\t\u0098\u0001"+
		"\u0099\u0001\u0099\u0001\u0099\u0005\u0099\u0651\b\u0099\n\u0099\f\u0099"+
		"\u0654\t\u0099\u0001\u009a\u0003\u009a\u0657\b\u009a\u0001\u009a\u0001"+
		"\u009a\u0001\u009b\u0001\u009b\u0003\u009b\u065d\b\u009b\u0001\u009c\u0001"+
		"\u009c\u0001\u009d\u0001\u009d\u0001\u009d\u0001\u009d\u0001\u009d\u0001"+
		"\u009d\u0001\u009d\u0001\u009e\u0001\u009e\u0001\u009e\u0003\u009e\u066b"+
		"\b\u009e\u0001\u009f\u0001\u009f\u0001\u00a0\u0001\u00a0\u0003\u00a0\u0671"+
		"\b\u00a0\u0001\u00a1\u0001\u00a1\u0001\u00a1\u0005\u00a1\u0676\b\u00a1"+
		"\n\u00a1\f\u00a1\u0679\t\u00a1\u0001\u00a2\u0001\u00a2\u0003\u00a2\u067d"+
		"\b\u00a2\u0001\u00a3\u0001\u00a3\u0001\u00a3\u0001\u00a3\u0001\u00a3\u0001"+
		"\u00a4\u0001\u00a4\u0001\u00a5\u0003\u00a5\u0687\b\u00a5\u0001\u00a5\u0003"+
		"\u00a5\u068a\b\u00a5\u0001\u00a5\u0003\u00a5\u068d\b\u00a5\u0001\u00a5"+
		"\u0001\u00a5\u0001\u00a5\u0001\u00a5\u0001\u00a5\u0003\u00a5\u0694\b\u00a5"+
		"\u0001\u00a6\u0001\u00a6\u0001\u00a7\u0001\u00a7\u0001\u00a7\u0001\u00a7"+
		"\u0001\u00a7\u0001\u00a7\u0001\u00a7\u0001\u00a7\u0001\u00a7\u0001\u00a7"+
		"\u0003\u00a7\u06a2\b\u00a7\u0001\u00a7\u0001\u00a7\u0001\u00a7\u0001\u00a7"+
		"\u0001\u00a7\u0001\u00a7\u0003\u00a7\u06aa\b\u00a7\u0001\u00a7\u0001\u00a7"+
		"\u0003\u00a7\u06ae\b\u00a7\u0001\u00a8\u0001\u00a8\u0001\u00a9\u0001\u00a9"+
		"\u0001\u00aa\u0001\u00aa\u0001\u00ab\u0001\u00ab\u0001\u00ac\u0001\u00ac"+
		"\u0001\u00ac\u0005\u00ac\u06bb\b\u00ac\n\u00ac\f\u00ac\u06be\t\u00ac\u0001"+
		"\u00ad\u0001\u00ad\u0001\u00ad\u0005\u00ad\u06c3\b\u00ad\n\u00ad\f\u00ad"+
		"\u06c6\t\u00ad\u0001\u00ae\u0003\u00ae\u06c9\b\u00ae\u0001\u00ae\u0001"+
		"\u00ae\u0001\u00af\u0001\u00af\u0001\u00af\u0001\u00af\u0001\u00af\u0001"+
		"\u00af\u0003\u00af\u06d3\b\u00af\u0003\u00af\u06d5\b\u00af\u0001\u00b0"+
		"\u0001\u00b0\u0003\u00b0\u06d9\b\u00b0\u0001\u00b1\u0001\u00b1\u0001\u00b1"+
		"\u0001\u00b1\u0001\u00b1\u0001\u00b1\u0001\u00b1\u0003\u00b1\u06e2\b\u00b1"+
		"\u0001\u00b2\u0001\u00b2\u0001\u00b3\u0001\u00b3\u0003\u00b3\u06e8\b\u00b3"+
		"\u0001\u00b4\u0001\u00b4\u0001\u00b4\u0003\u00b4\u06ed\b\u00b4\u0001\u00b5"+
		"\u0001\u00b5\u0001\u00b5\u0001\u00b6\u0001\u00b6\u0001\u00b7\u0001\u00b7"+
		"\u0001\u00b7\u0001\u00b7\u0001\u00b8\u0001\u00b8\u0001\u00b8\u0005\u00b8"+
		"\u06fb\b\u00b8\n\u00b8\f\u00b8\u06fe\t\u00b8\u0001\u00b9\u0001\u00b9\u0003"+
		"\u00b9\u0702\b\u00b9\u0001\u00b9\u0003\u00b9\u0705\b\u00b9\u0001\u00ba"+
		"\u0001\u00ba\u0001\u00bb\u0001\u00bb\u0001\u00bb\u0001\u00bc\u0001\u00bc"+
		"\u0001\u00bd\u0001\u00bd\u0001\u00bd\u0001\u00bd\u0003\u00bd\u0712\b\u00bd"+
		"\u0001\u00be\u0001\u00be\u0001\u00be\u0001\u00be\u0003\u00be\u0718\b\u00be"+
		"\u0001\u00bf\u0001\u00bf\u0001\u00bf\u0005\u00bf\u071d\b\u00bf\n\u00bf"+
		"\f\u00bf\u0720\t\u00bf\u0001\u00c0\u0001\u00c0\u0001\u00c0\u0001\u00c0"+
		"\u0003\u00c0\u0726\b\u00c0\u0001\u00c1\u0001\u00c1\u0001\u00c1\u0005\u00c1"+
		"\u072b\b\u00c1\n\u00c1\f\u00c1\u072e\t\u00c1\u0001\u00c2\u0001\u00c2\u0001"+
		"\u00c2\u0001\u00c2\u0001\u00c2\u0003\u00c2\u0735\b\u00c2\u0001\u00c3\u0001"+
		"\u00c3\u0001\u00c3\u0001\u00c3\u0001\u00c3\u0001\u00c4\u0001\u00c4\u0001"+
		"\u00c4\u0001\u00c4\u0001\u00c4\u0001\u00c5\u0001\u00c5\u0001\u00c5\u0001"+
		"\u00c6\u0001\u00c6\u0001\u00c6\u0001\u00c7\u0001\u00c7\u0001\u00c7\u0001"+
		"\u00c8\u0001\u00c8\u0001\u00c8\u0005\u00c8\u074d\b\u00c8\n\u00c8\f\u00c8"+
		"\u0750\t\u00c8\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00ca"+
		"\u0001\u00ca\u0003\u00ca\u0758\b\u00ca\u0001\u00ca\u0001\u00ca\u0003\u00ca"+
		"\u075c\b\u00ca\u0001\u00cb\u0001\u00cb\u0001\u00cb\u0001\u00cb\u0001\u00cb"+
		"\u0001\u00cb\u0003\u00cb\u0764\b\u00cb\u0001\u00cc\u0001\u00cc\u0001\u00cd"+
		"\u0001\u00cd\u0001\u00ce\u0001\u00ce\u0003\u00ce\u076c\b\u00ce\u0001\u00ce"+
		"\u0001\u00ce\u0003\u00ce\u0770\b\u00ce\u0001\u00ce\u0001\u00ce\u0001\u00ce"+
		"\u0001\u00ce\u0001\u00cf\u0001\u00cf\u0001\u00d0\u0001\u00d0\u0003\u00d0"+
		"\u077a\b\u00d0\u0001\u00d0\u0001\u00d0\u0001\u00d0\u0001\u00d1\u0001\u00d1"+
		"\u0003\u00d1\u0781\b\u00d1\u0001\u00d1\u0001\u00d1\u0001\u00d1\u0003\u00d1"+
		"\u0786\b\u00d1\u0001\u00d2\u0001\u00d2\u0001\u00d2\u0001\u00d3\u0001\u00d3"+
		"\u0001\u00d3\u0001\u00d3\u0001\u00d3\u0001\u00d3\u0003\u00d3\u0791\b\u00d3"+
		"\u0001\u00d4\u0001\u00d4\u0001\u00d4\u0005\u00d4\u0796\b\u00d4\n\u00d4"+
		"\f\u00d4\u0799\t\u00d4\u0001\u00d5\u0001\u00d5\u0001\u00d5\u0001\u00d6"+
		"\u0001\u00d6\u0001\u00d6\u0003\u00d6\u07a1\b\u00d6\u0001\u00d7\u0001\u00d7"+
		"\u0001\u00d7\u0001\u00d7\u0001\u00d8\u0001\u00d8\u0001\u00d8\u0001\u00d9"+
		"\u0001\u00d9\u0001\u00d9\u0001\u00d9\u0001\u00d9\u0001\u00da\u0001\u00da"+
		"\u0001\u00da\u0005\u00da\u07b2\b\u00da\n\u00da\f\u00da\u07b5\t\u00da\u0001"+
		"\u00db\u0001\u00db\u0001\u00db\u0001\u00db\u0001\u00dc\u0001\u00dc\u0001"+
		"\u00dc\u0001\u00dc\u0001\u00dd\u0001\u00dd\u0001\u00dd\u0005\u00dd\u07c2"+
		"\b\u00dd\n\u00dd\f\u00dd\u07c5\t\u00dd\u0001\u00de\u0001\u00de\u0001\u00de"+
		"\u0001\u00df\u0001\u00df\u0001\u00df\u0001\u00e0\u0001\u00e0\u0001\u00e1"+
		"\u0001\u00e1\u0003\u00e1\u07d1\b\u00e1\u0001\u00e2\u0001\u00e2\u0001\u00e2"+
		"\u0001\u00e3\u0001\u00e3\u0003\u00e3\u07d8\b\u00e3\u0001\u00e3\u0001\u00e3"+
		"\u0001\u00e4\u0001\u00e4\u0003\u00e4\u07de\b\u00e4\u0001\u00e4\u0001\u00e4"+
		"\u0001\u00e5\u0001\u00e5\u0001\u00e6\u0001\u00e6\u0001\u00e7\u0001\u00e7"+
		"\u0001\u00e7\u0001\u00e7\u0001\u00e7\u0001\u00e8\u0001\u00e8\u0003\u00e8"+
		"\u07ed\b\u00e8\u0001\u00e9\u0001\u00e9\u0001\u00ea\u0001\u00ea\u0001\u00eb"+
		"\u0001\u00eb\u0001\u00eb\u0001\u00ec\u0001\u00ec\u0003\u00ec\u07f8\b\u00ec"+
		"\u0001\u00ed\u0001\u00ed\u0001\u00ee\u0001\u00ee\u0001\u00ef\u0001\u00ef"+
		"\u0001\u00ef\u0003\u00ef\u0801\b\u00ef\u0001\u00ef\u0001\u00ef\u0001\u00f0"+
		"\u0001\u00f0\u0001\u00f0\u0003\u00f0\u0808\b\u00f0\u0001\u00f0\u0003\u00f0"+
		"\u080b\b\u00f0\u0001\u00f1\u0001\u00f1\u0001\u00f2\u0001\u00f2\u0001\u00f2"+
		"\u0005\u00f2\u0812\b\u00f2\n\u00f2\f\u00f2\u0815\t\u00f2\u0001\u00f3\u0001"+
		"\u00f3\u0001\u00f3\u0001\u00f3\u0001\u00f3\u0003\u00f3\u081c\b\u00f3\u0001"+
		"\u00f4\u0001\u00f4\u0001\u00f4\u0001\u00f4\u0001\u00f4\u0003\u00f4\u0823"+
		"\b\u00f4\u0001\u00f5\u0001\u00f5\u0003\u00f5\u0827\b\u00f5\u0001\u00f6"+
		"\u0001\u00f6\u0001\u00f7\u0001\u00f7\u0001\u00f8\u0001\u00f8\u0001\u00f9"+
		"\u0001\u00f9\u0001\u00fa\u0001\u00fa\u0001\u00fa\u0001\u00fa\u0001\u00fa"+
		"\u0001\u00fa\u0001\u00fa\u0001\u00fa\u0003\u00fa\u0839\b\u00fa\u0001\u00fb"+
		"\u0001\u00fb\u0001\u00fb\u0001\u00fb\u0003\u00fb\u083f\b\u00fb\u0001\u00fb"+
		"\u0001\u00fb\u0001\u00fb\u0001\u00fb\u0001\u00fb\u0001\u00fb\u0001\u00fb"+
		"\u0001\u00fb\u0003\u00fb\u0849\b\u00fb\u0001\u00fb\u0001\u00fb\u0003\u00fb"+
		"\u084d\b\u00fb\u0001\u00fc\u0001\u00fc\u0001\u00fc\u0005\u00fc\u0852\b"+
		"\u00fc\n\u00fc\f\u00fc\u0855\t\u00fc\u0001\u00fd\u0001\u00fd\u0001\u00fd"+
		"\u0001\u00fd\u0001\u00fd\u0001\u00fd\u0001\u00fd\u0001\u00fd\u0001\u00fd"+
		"\u0001\u00fd\u0003\u00fd\u0861\b\u00fd\u0001\u00fe\u0001\u00fe\u0001\u00fe"+
		"\u0005\u00fe\u0866\b\u00fe\n\u00fe\f\u00fe\u0869\t\u00fe\u0001\u00ff\u0001"+
		"\u00ff\u0003\u00ff\u086d\b\u00ff\u0001\u0100\u0001\u0100\u0001\u0101\u0001"+
		"\u0101\u0001\u0102\u0001\u0102\u0001\u0103\u0001\u0103\u0001\u0104\u0003"+
		"\u0104\u0878\b\u0104\u0001\u0104\u0001\u0104\u0001\u0105\u0001\u0105\u0003"+
		"\u0105\u087e\b\u0105\u0001\u0106\u0001\u0106\u0003\u0106\u0882\b\u0106"+
		"\u0001\u0107\u0001\u0107\u0001\u0107\u0003\u0107\u0887\b\u0107\u0001\u0107"+
		"\u0003\u0107\u088a\b\u0107\u0001\u0107\u0001\u0107\u0001\u0107\u0003\u0107"+
		"\u088f\b\u0107\u0001\u0107\u0001\u0107\u0001\u0107\u0003\u0107\u0894\b"+
		"\u0107\u0001\u0108\u0001\u0108\u0001\u0108\u0001\u0109\u0001\u0109\u0001"+
		"\u0109\u0003\u0109\u089c\b\u0109\u0001\u010a\u0001\u010a\u0001\u010b\u0001"+
		"\u010b\u0001\u010b\u0003\u010b\u08a3\b\u010b\u0001\u010c\u0001\u010c\u0001"+
		"\u010c\u0001\u010d\u0001\u010d\u0001\u010d\u0001\u010e\u0001\u010e\u0001"+
		"\u010e\u0001\u010f\u0001\u010f\u0001\u0110\u0001\u0110\u0001\u0110\u0003"+
		"\u0110\u08b3\b\u0110\u0001\u0111\u0001\u0111\u0003\u0111\u08b7\b\u0111"+
		"\u0001\u0112\u0001\u0112\u0001\u0112\u0001\u0112\u0001\u0112\u0001\u0112"+
		"\u0001\u0112\u0001\u0112\u0001\u0112\u0001\u0112\u0001\u0112\u0001\u0112"+
		"\u0001\u0112\u0001\u0112\u0001\u0112\u0001\u0112\u0001\u0112\u0001\u0112"+
		"\u0001\u0112\u0001\u0112\u0001\u0112\u0001\u0112\u0001\u0112\u0001\u0112"+
		"\u0001\u0112\u0001\u0112\u0001\u0112\u0001\u0112\u0001\u0112\u0001\u0112"+
		"\u0001\u0112\u0001\u0112\u0001\u0112\u0001\u0112\u0003\u0112\u08db\b\u0112"+
		"\u0001\u0113\u0001\u0113\u0001\u0113\u0001\u0113\u0001\u0114\u0001\u0114"+
		"\u0003\u0114\u08e3\b\u0114\u0001\u0115\u0001\u0115\u0001\u0115\u0001\u0115"+
		"\u0001\u0115\u0001\u0115\u0001\u0115\u0001\u0115\u0001\u0115\u0001\u0115"+
		"\u0001\u0115\u0003\u0115\u08f0\b\u0115\u0001\u0116\u0001\u0116\u0001\u0116"+
		"\u0001\u0116\u0001\u0116\u0001\u0116\u0001\u0116\u0001\u0116\u0003\u0116"+
		"\u08fa\b\u0116\u0001\u0117\u0001\u0117\u0001\u0118\u0001\u0118\u0001\u0118"+
		"\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118"+
		"\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118"+
		"\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118"+
		"\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118"+
		"\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118"+
		"\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118"+
		"\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118"+
		"\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118"+
		"\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118"+
		"\u0001\u0118\u0001\u0118\u0001\u0118\u0001\u0118\u0003\u0118\u093b\b\u0118"+
		"\u0001\u0119\u0001\u0119\u0001\u0119\u0000\u0000\u011a\u0000\u0002\u0004"+
		"\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \""+
		"$&(*,.02468:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086"+
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
		"\u01f0\u01f2\u01f4\u01f6\u01f8\u01fa\u01fc\u01fe\u0200\u0202\u0204\u0206"+
		"\u0208\u020a\u020c\u020e\u0210\u0212\u0214\u0216\u0218\u021a\u021c\u021e"+
		"\u0220\u0222\u0224\u0226\u0228\u022a\u022c\u022e\u0230\u0232\u0000 \u0001"+
		"\u0000\u011f\u011f\u0001\u0000\u0115\u0115\u0002\u0000\u000e\u000e55\u0002"+
		"\u0000\u0002\u0002\u000b\u000b\u0003\u0000\u0010\u0010\u001d\u001d++\u0002"+
		"\u0000\u000f\u000f33\u000e\u0000==EFXXddfgkkpptt\u007f\u007f\u0083\u0083"+
		"\u0089\u008a\u008c\u008c\u0098\u0099\u009e\u00cd\u0006\u0000\u0004\u0004"+
		"..BBRR[[aa\u0002\u0000\b\b44\u0002\u0000~~\u0085\u0085\u0002\u0000\u0095"+
		"\u0095\u012b\u012b\u0004\u0000XXddfgtt\u0002\u0000WWee\u0002\u0000\u0014"+
		"\u0014**\u0001\u0000\u0120\u0121\u0001\u0000\u0122\u0124\u0001\u0000\u008f"+
		"\u0091\u0003\u0000\u0006\u0006\u001c\u001c22\u0002\u0000<<LL\u0001\u0000"+
		"\u010e\u0111\u0002\u0000\u0113\u0113\u0118\u011c\u0002\u0000\u0005\u0005"+
		"//\u0002\u0000\u0015\u0015\u001e\u001e\u0003\u0000\u000f\u000f33\u0096"+
		"\u0096\u0002\u0000\u0004\u0004..\u0004\u0000II^^qr\u009c\u009c\b\u0000"+
		"@@KKMNPPbcmo}}\u009b\u009b\u0004\u0000\u0016\u0016\u001d\u001d++\u014d"+
		"\u014f\u0001\u0000\u0147\u0148\u0002\u0000\u012b\u012b\u0178\u0178\u0016"+
		"\u0000\u0014\u0014#$**,,55::<EGGIWZ]_ceegsuy{\u0083\u0085\u0094\u0096"+
		"\u00d0\u00d2\u00e9\u00eb\u00f5\u00f7\u00f8\u00fa\u010d\u0167\u0174\u0001"+
		"\u0000\u012c\u0144\u09bc\u0000\u0237\u0001\u0000\u0000\u0000\u0002\u0242"+
		"\u0001\u0000\u0000\u0000\u0004\u0247\u0001\u0000\u0000\u0000\u0006\u024d"+
		"\u0001\u0000\u0000\u0000\b\u0254\u0001\u0000\u0000\u0000\n\u025b\u0001"+
		"\u0000\u0000\u0000\f\u025e\u0001\u0000\u0000\u0000\u000e\u0261\u0001\u0000"+
		"\u0000\u0000\u0010\u0264\u0001\u0000\u0000\u0000\u0012\u0267\u0001\u0000"+
		"\u0000\u0000\u0014\u026a\u0001\u0000\u0000\u0000\u0016\u026d\u0001\u0000"+
		"\u0000\u0000\u0018\u0272\u0001\u0000\u0000\u0000\u001a\u0276\u0001\u0000"+
		"\u0000\u0000\u001c\u0279\u0001\u0000\u0000\u0000\u001e\u028a\u0001\u0000"+
		"\u0000\u0000 \u028c\u0001\u0000\u0000\u0000\"\u0292\u0001\u0000\u0000"+
		"\u0000$\u02aa\u0001\u0000\u0000\u0000&\u02ac\u0001\u0000\u0000\u0000("+
		"\u02b5\u0001\u0000\u0000\u0000*\u02bb\u0001\u0000\u0000\u0000,\u02c2\u0001"+
		"\u0000\u0000\u0000.\u02ce\u0001\u0000\u0000\u00000\u02d8\u0001\u0000\u0000"+
		"\u00002\u02e3\u0001\u0000\u0000\u00004\u02e8\u0001\u0000\u0000\u00006"+
		"\u02ee\u0001\u0000\u0000\u00008\u02f4\u0001\u0000\u0000\u0000:\u02fa\u0001"+
		"\u0000\u0000\u0000<\u0300\u0001\u0000\u0000\u0000>\u0306\u0001\u0000\u0000"+
		"\u0000@\u031d\u0001\u0000\u0000\u0000B\u031f\u0001\u0000\u0000\u0000D"+
		"\u0321\u0001\u0000\u0000\u0000F\u0324\u0001\u0000\u0000\u0000H\u0329\u0001"+
		"\u0000\u0000\u0000J\u032d\u0001\u0000\u0000\u0000L\u0330\u0001\u0000\u0000"+
		"\u0000N\u0341\u0001\u0000\u0000\u0000P\u0345\u0001\u0000\u0000\u0000R"+
		"\u0347\u0001\u0000\u0000\u0000T\u034e\u0001\u0000\u0000\u0000V\u0350\u0001"+
		"\u0000\u0000\u0000X\u0352\u0001\u0000\u0000\u0000Z\u0359\u0001\u0000\u0000"+
		"\u0000\\\u0364\u0001\u0000\u0000\u0000^\u0368\u0001\u0000\u0000\u0000"+
		"`\u0371\u0001\u0000\u0000\u0000b\u0373\u0001\u0000\u0000\u0000d\u0380"+
		"\u0001\u0000\u0000\u0000f\u0383\u0001\u0000\u0000\u0000h\u038b\u0001\u0000"+
		"\u0000\u0000j\u038f\u0001\u0000\u0000\u0000l\u0391\u0001\u0000\u0000\u0000"+
		"n\u039a\u0001\u0000\u0000\u0000p\u039e\u0001\u0000\u0000\u0000r\u03a0"+
		"\u0001\u0000\u0000\u0000t\u03a9\u0001\u0000\u0000\u0000v\u03ad\u0001\u0000"+
		"\u0000\u0000x\u03b2\u0001\u0000\u0000\u0000z\u03b4\u0001\u0000\u0000\u0000"+
		"|\u03b8\u0001\u0000\u0000\u0000~\u03d5\u0001\u0000\u0000\u0000\u0080\u03d8"+
		"\u0001\u0000\u0000\u0000\u0082\u03da\u0001\u0000\u0000\u0000\u0084\u03e7"+
		"\u0001\u0000\u0000\u0000\u0086\u03ea\u0001\u0000\u0000\u0000\u0088\u03ee"+
		"\u0001\u0000\u0000\u0000\u008a\u03f2\u0001\u0000\u0000\u0000\u008c\u03f6"+
		"\u0001\u0000\u0000\u0000\u008e\u03fb\u0001\u0000\u0000\u0000\u0090\u03fd"+
		"\u0001\u0000\u0000\u0000\u0092\u042b\u0001\u0000\u0000\u0000\u0094\u0431"+
		"\u0001\u0000\u0000\u0000\u0096\u0447\u0001\u0000\u0000\u0000\u0098\u044d"+
		"\u0001\u0000\u0000\u0000\u009a\u0455\u0001\u0000\u0000\u0000\u009c\u0457"+
		"\u0001\u0000\u0000\u0000\u009e\u046f\u0001\u0000\u0000\u0000\u00a0\u0472"+
		"\u0001\u0000\u0000\u0000\u00a2\u047b\u0001\u0000\u0000\u0000\u00a4\u047f"+
		"\u0001\u0000\u0000\u0000\u00a6\u0481\u0001\u0000\u0000\u0000\u00a8\u0484"+
		"\u0001\u0000\u0000\u0000\u00aa\u0489\u0001\u0000\u0000\u0000\u00ac\u0491"+
		"\u0001\u0000\u0000\u0000\u00ae\u0499\u0001\u0000\u0000\u0000\u00b0\u049b"+
		"\u0001\u0000\u0000\u0000\u00b2\u04a0\u0001\u0000\u0000\u0000\u00b4\u04bb"+
		"\u0001\u0000\u0000\u0000\u00b6\u04c0\u0001\u0000\u0000\u0000\u00b8\u04c2"+
		"\u0001\u0000\u0000\u0000\u00ba\u04c6\u0001\u0000\u0000\u0000\u00bc\u04c8"+
		"\u0001\u0000\u0000\u0000\u00be\u04ca\u0001\u0000\u0000\u0000\u00c0\u04cf"+
		"\u0001\u0000\u0000\u0000\u00c2\u04df\u0001\u0000\u0000\u0000\u00c4\u04e1"+
		"\u0001\u0000\u0000\u0000\u00c6\u04e3\u0001\u0000\u0000\u0000\u00c8\u04e5"+
		"\u0001\u0000\u0000\u0000\u00ca\u04ec\u0001\u0000\u0000\u0000\u00cc\u04ee"+
		"\u0001\u0000\u0000\u0000\u00ce\u04f3\u0001\u0000\u0000\u0000\u00d0\u0512"+
		"\u0001\u0000\u0000\u0000\u00d2\u0518\u0001\u0000\u0000\u0000\u00d4\u051a"+
		"\u0001\u0000\u0000\u0000\u00d6\u051c\u0001\u0000\u0000\u0000\u00d8\u0527"+
		"\u0001\u0000\u0000\u0000\u00da\u0530\u0001\u0000\u0000\u0000\u00dc\u0532"+
		"\u0001\u0000\u0000\u0000\u00de\u053d\u0001\u0000\u0000\u0000\u00e0\u053f"+
		"\u0001\u0000\u0000\u0000\u00e2\u0547\u0001\u0000\u0000\u0000\u00e4\u0560"+
		"\u0001\u0000\u0000\u0000\u00e6\u0574\u0001\u0000\u0000\u0000\u00e8\u0580"+
		"\u0001\u0000\u0000\u0000\u00ea\u058c\u0001\u0000\u0000\u0000\u00ec\u058e"+
		"\u0001\u0000\u0000\u0000\u00ee\u059b\u0001\u0000\u0000\u0000\u00f0\u059d"+
		"\u0001\u0000\u0000\u0000\u00f2\u05af\u0001\u0000\u0000\u0000\u00f4\u05b1"+
		"\u0001\u0000\u0000\u0000\u00f6\u05b3\u0001\u0000\u0000\u0000\u00f8\u05c4"+
		"\u0001\u0000\u0000\u0000\u00fa\u05c7\u0001\u0000\u0000\u0000\u00fc\u05cb"+
		"\u0001\u0000\u0000\u0000\u00fe\u05d1\u0001\u0000\u0000\u0000\u0100\u05d5"+
		"\u0001\u0000\u0000\u0000\u0102\u05da\u0001\u0000\u0000\u0000\u0104\u05df"+
		"\u0001\u0000\u0000\u0000\u0106\u05e1\u0001\u0000\u0000\u0000\u0108\u05e3"+
		"\u0001\u0000\u0000\u0000\u010a\u05ea\u0001\u0000\u0000\u0000\u010c\u05ec"+
		"\u0001\u0000\u0000\u0000\u010e\u0600\u0001\u0000\u0000\u0000\u0110\u0602"+
		"\u0001\u0000\u0000\u0000\u0112\u060f\u0001\u0000\u0000\u0000\u0114\u0613"+
		"\u0001\u0000\u0000\u0000\u0116\u0616\u0001\u0000\u0000\u0000\u0118\u061b"+
		"\u0001\u0000\u0000\u0000\u011a\u061d\u0001\u0000\u0000\u0000\u011c\u0625"+
		"\u0001\u0000\u0000\u0000\u011e\u0627\u0001\u0000\u0000\u0000\u0120\u062a"+
		"\u0001\u0000\u0000\u0000\u0122\u062d\u0001\u0000\u0000\u0000\u0124\u0630"+
		"\u0001\u0000\u0000\u0000\u0126\u0632\u0001\u0000\u0000\u0000\u0128\u0634"+
		"\u0001\u0000\u0000\u0000\u012a\u0637\u0001\u0000\u0000\u0000\u012c\u063e"+
		"\u0001\u0000\u0000\u0000\u012e\u0643\u0001\u0000\u0000\u0000\u0130\u0645"+
		"\u0001\u0000\u0000\u0000\u0132\u064d\u0001\u0000\u0000\u0000\u0134\u0656"+
		"\u0001\u0000\u0000\u0000\u0136\u065c\u0001\u0000\u0000\u0000\u0138\u065e"+
		"\u0001\u0000\u0000\u0000\u013a\u0660\u0001\u0000\u0000\u0000\u013c\u066a"+
		"\u0001\u0000\u0000\u0000\u013e\u066c\u0001\u0000\u0000\u0000\u0140\u0670"+
		"\u0001\u0000\u0000\u0000\u0142\u0672\u0001\u0000\u0000\u0000\u0144\u067c"+
		"\u0001\u0000\u0000\u0000\u0146\u067e\u0001\u0000\u0000\u0000\u0148\u0683"+
		"\u0001\u0000\u0000\u0000\u014a\u0693\u0001\u0000\u0000\u0000\u014c\u0695"+
		"\u0001\u0000\u0000\u0000\u014e\u06ad\u0001\u0000\u0000\u0000\u0150\u06af"+
		"\u0001\u0000\u0000\u0000\u0152\u06b1\u0001\u0000\u0000\u0000\u0154\u06b3"+
		"\u0001\u0000\u0000\u0000\u0156\u06b5\u0001\u0000\u0000\u0000\u0158\u06b7"+
		"\u0001\u0000\u0000\u0000\u015a\u06bf\u0001\u0000\u0000\u0000\u015c\u06c8"+
		"\u0001\u0000\u0000\u0000\u015e\u06d4\u0001\u0000\u0000\u0000\u0160\u06d8"+
		"\u0001\u0000\u0000\u0000\u0162\u06e1\u0001\u0000\u0000\u0000\u0164\u06e3"+
		"\u0001\u0000\u0000\u0000\u0166\u06e7\u0001\u0000\u0000\u0000\u0168\u06ec"+
		"\u0001\u0000\u0000\u0000\u016a\u06ee\u0001\u0000\u0000\u0000\u016c\u06f1"+
		"\u0001\u0000\u0000\u0000\u016e\u06f3\u0001\u0000\u0000\u0000\u0170\u06f7"+
		"\u0001\u0000\u0000\u0000\u0172\u06ff\u0001\u0000\u0000\u0000\u0174\u0706"+
		"\u0001\u0000\u0000\u0000\u0176\u0708\u0001\u0000\u0000\u0000\u0178\u070b"+
		"\u0001\u0000\u0000\u0000\u017a\u070d\u0001\u0000\u0000\u0000\u017c\u0713"+
		"\u0001\u0000\u0000\u0000\u017e\u0719\u0001\u0000\u0000\u0000\u0180\u0725"+
		"\u0001\u0000\u0000\u0000\u0182\u0727\u0001\u0000\u0000\u0000\u0184\u0734"+
		"\u0001\u0000\u0000\u0000\u0186\u0736\u0001\u0000\u0000\u0000\u0188\u073b"+
		"\u0001\u0000\u0000\u0000\u018a\u0740\u0001\u0000\u0000\u0000\u018c\u0743"+
		"\u0001\u0000\u0000\u0000\u018e\u0746\u0001\u0000\u0000\u0000\u0190\u0749"+
		"\u0001\u0000\u0000\u0000\u0192\u0751\u0001\u0000\u0000\u0000\u0194\u075b"+
		"\u0001\u0000\u0000\u0000\u0196\u0763\u0001\u0000\u0000\u0000\u0198\u0765"+
		"\u0001\u0000\u0000\u0000\u019a\u0767\u0001\u0000\u0000\u0000\u019c\u0769"+
		"\u0001\u0000\u0000\u0000\u019e\u0775\u0001\u0000\u0000\u0000\u01a0\u0777"+
		"\u0001\u0000\u0000\u0000\u01a2\u077e\u0001\u0000\u0000\u0000\u01a4\u0787"+
		"\u0001\u0000\u0000\u0000\u01a6\u0790\u0001\u0000\u0000\u0000\u01a8\u0792"+
		"\u0001\u0000\u0000\u0000\u01aa\u079a\u0001\u0000\u0000\u0000\u01ac\u07a0"+
		"\u0001\u0000\u0000\u0000\u01ae\u07a2\u0001\u0000\u0000\u0000\u01b0\u07a6"+
		"\u0001\u0000\u0000\u0000\u01b2\u07a9\u0001\u0000\u0000\u0000\u01b4\u07ae"+
		"\u0001\u0000\u0000\u0000\u01b6\u07b6\u0001\u0000\u0000\u0000\u01b8\u07ba"+
		"\u0001\u0000\u0000\u0000\u01ba\u07be\u0001\u0000\u0000\u0000\u01bc\u07c6"+
		"\u0001\u0000\u0000\u0000\u01be\u07c9\u0001\u0000\u0000\u0000\u01c0\u07cc"+
		"\u0001\u0000\u0000\u0000\u01c2\u07d0\u0001\u0000\u0000\u0000\u01c4\u07d2"+
		"\u0001\u0000\u0000\u0000\u01c6\u07d5\u0001\u0000\u0000\u0000\u01c8\u07db"+
		"\u0001\u0000\u0000\u0000\u01ca\u07e1\u0001\u0000\u0000\u0000\u01cc\u07e3"+
		"\u0001\u0000\u0000\u0000\u01ce\u07e5\u0001\u0000\u0000\u0000\u01d0\u07ec"+
		"\u0001\u0000\u0000\u0000\u01d2\u07ee\u0001\u0000\u0000\u0000\u01d4\u07f0"+
		"\u0001\u0000\u0000\u0000\u01d6\u07f2\u0001\u0000\u0000\u0000\u01d8\u07f7"+
		"\u0001\u0000\u0000\u0000\u01da\u07f9\u0001\u0000\u0000\u0000\u01dc\u07fb"+
		"\u0001\u0000\u0000\u0000\u01de\u07fd\u0001\u0000\u0000\u0000\u01e0\u080a"+
		"\u0001\u0000\u0000\u0000\u01e2\u080c\u0001\u0000\u0000\u0000\u01e4\u080e"+
		"\u0001\u0000\u0000\u0000\u01e6\u081b\u0001\u0000\u0000\u0000\u01e8\u0822"+
		"\u0001\u0000\u0000\u0000\u01ea\u0826\u0001\u0000\u0000\u0000\u01ec\u0828"+
		"\u0001\u0000\u0000\u0000\u01ee\u082a\u0001\u0000\u0000\u0000\u01f0\u082c"+
		"\u0001\u0000\u0000\u0000\u01f2\u082e\u0001\u0000\u0000\u0000\u01f4\u0838"+
		"\u0001\u0000\u0000\u0000\u01f6\u084c\u0001\u0000\u0000\u0000\u01f8\u084e"+
		"\u0001\u0000\u0000\u0000\u01fa\u0860\u0001\u0000\u0000\u0000\u01fc\u0862"+
		"\u0001\u0000\u0000\u0000\u01fe\u086c\u0001\u0000\u0000\u0000\u0200\u086e"+
		"\u0001\u0000\u0000\u0000\u0202\u0870\u0001\u0000\u0000\u0000\u0204\u0872"+
		"\u0001\u0000\u0000\u0000\u0206\u0874\u0001\u0000\u0000\u0000\u0208\u0877"+
		"\u0001\u0000\u0000\u0000\u020a\u087d\u0001\u0000\u0000\u0000\u020c\u0881"+
		"\u0001\u0000\u0000\u0000\u020e\u0893\u0001\u0000\u0000\u0000\u0210\u0895"+
		"\u0001\u0000\u0000\u0000\u0212\u089b\u0001\u0000\u0000\u0000\u0214\u089d"+
		"\u0001\u0000\u0000\u0000\u0216\u08a2\u0001\u0000\u0000\u0000\u0218\u08a4"+
		"\u0001\u0000\u0000\u0000\u021a\u08a7\u0001\u0000\u0000\u0000\u021c\u08aa"+
		"\u0001\u0000\u0000\u0000\u021e\u08ad\u0001\u0000\u0000\u0000\u0220\u08b2"+
		"\u0001\u0000\u0000\u0000\u0222\u08b4\u0001\u0000\u0000\u0000\u0224\u08da"+
		"\u0001\u0000\u0000\u0000\u0226\u08dc\u0001\u0000\u0000\u0000\u0228\u08e0"+
		"\u0001\u0000\u0000\u0000\u022a\u08ef\u0001\u0000\u0000\u0000\u022c\u08f9"+
		"\u0001\u0000\u0000\u0000\u022e\u08fb\u0001\u0000\u0000\u0000\u0230\u093a"+
		"\u0001\u0000\u0000\u0000\u0232\u093c\u0001\u0000\u0000\u0000\u0234\u0236"+
		"\u0003\u0002\u0001\u0000\u0235\u0234\u0001\u0000\u0000\u0000\u0236\u0239"+
		"\u0001\u0000\u0000\u0000\u0237\u0235\u0001\u0000\u0000\u0000\u0237\u0238"+
		"\u0001\u0000\u0000\u0000\u0238\u023b\u0001\u0000\u0000\u0000\u0239\u0237"+
		"\u0001\u0000\u0000\u0000\u023a\u023c\u0003\u0002\u0001\u0000\u023b\u023a"+
		"\u0001\u0000\u0000\u0000\u023b\u023c\u0001\u0000\u0000\u0000\u023c\u023d"+
		"\u0001\u0000\u0000\u0000\u023d\u023e\u0005\u0000\u0000\u0001\u023e\u0001"+
		"\u0001\u0000\u0000\u0000\u023f\u0243\u0003\u0006\u0003\u0000\u0240\u0243"+
		"\u0003J%\u0000\u0241\u0243\u0003T*\u0000\u0242\u023f\u0001\u0000\u0000"+
		"\u0000\u0242\u0240\u0001\u0000\u0000\u0000\u0242\u0241\u0001\u0000\u0000"+
		"\u0000\u0243\u0245\u0001\u0000\u0000\u0000\u0244\u0246\u0005\u0115\u0000"+
		"\u0000\u0245\u0244\u0001\u0000\u0000\u0000\u0245\u0246\u0001\u0000\u0000"+
		"\u0000\u0246\u0003\u0001\u0000\u0000\u0000\u0247\u0248\u0003\u0006\u0003"+
		"\u0000\u0248\u0249\u0005\u0000\u0000\u0001\u0249\u0005\u0001\u0000\u0000"+
		"\u0000\u024a\u024e\u0003\u001e\u000f\u0000\u024b\u024e\u0003 \u0010\u0000"+
		"\u024c\u024e\u0003\"\u0011\u0000\u024d\u024a\u0001\u0000\u0000\u0000\u024d"+
		"\u024b\u0001\u0000\u0000\u0000\u024d\u024c\u0001\u0000\u0000\u0000\u024e"+
		"\u0250\u0001\u0000\u0000\u0000\u024f\u0251\u0005\u0115\u0000\u0000\u0250"+
		"\u024f\u0001\u0000\u0000\u0000\u0250\u0251\u0001\u0000\u0000\u0000\u0251"+
		"\u0007\u0001\u0000\u0000\u0000\u0252\u0255\u0003J%\u0000\u0253\u0255\u0003"+
		"T*\u0000\u0254\u0252\u0001\u0000\u0000\u0000\u0254\u0253\u0001\u0000\u0000"+
		"\u0000\u0255\u0257\u0001\u0000\u0000\u0000\u0256\u0258\u0005\u0115\u0000"+
		"\u0000\u0257\u0256\u0001\u0000\u0000\u0000\u0257\u0258\u0001\u0000\u0000"+
		"\u0000\u0258\u0259\u0001\u0000\u0000\u0000\u0259\u025a\u0005\u0000\u0000"+
		"\u0001\u025a\t\u0001\u0000\u0000\u0000\u025b\u025c\u0003\u00e6s\u0000"+
		"\u025c\u025d\u0005\u0000\u0000\u0001\u025d\u000b\u0001\u0000\u0000\u0000"+
		"\u025e\u025f\u0003\u00e8t\u0000\u025f\u0260\u0005\u0000\u0000\u0001\u0260"+
		"\r\u0001\u0000\u0000\u0000\u0261\u0262\u0003\u01a6\u00d3\u0000\u0262\u0263"+
		"\u0005\u0000\u0000\u0001\u0263\u000f\u0001\u0000\u0000\u0000\u0264\u0265"+
		"\u0003\u012c\u0096\u0000\u0265\u0266\u0005\u0000\u0000\u0001\u0266\u0011"+
		"\u0001\u0000\u0000\u0000\u0267\u0268\u0003\u009aM\u0000\u0268\u0269\u0005"+
		"\u0000\u0000\u0001\u0269\u0013\u0001\u0000\u0000\u0000\u026a\u026b\u0003"+
		"T*\u0000\u026b\u026c\u0005\u0000\u0000\u0001\u026c\u0015\u0001\u0000\u0000"+
		"\u0000\u026d\u026e\u0003\u0092I\u0000\u026e\u026f\u0005\u0000\u0000\u0001"+
		"\u026f\u0017\u0001\u0000\u0000\u0000\u0270\u0273\u0003\u0208\u0104\u0000"+
		"\u0271\u0273\u0003\u020a\u0105\u0000\u0272\u0270\u0001\u0000\u0000\u0000"+
		"\u0272\u0271\u0001\u0000\u0000\u0000\u0273\u0274\u0001\u0000\u0000\u0000"+
		"\u0274\u0275\u0005\u0000\u0000\u0001\u0275\u0019\u0001\u0000\u0000\u0000"+
		"\u0276\u0277\u0003\u01ac\u00d6\u0000\u0277\u0278\u0005\u0000\u0000\u0001"+
		"\u0278\u001b\u0001\u0000\u0000\u0000\u0279\u027a\u0003V+\u0000\u027a\u027b"+
		"\u0005\u0000\u0000\u0001\u027b\u001d\u0001\u0000\u0000\u0000\u027c\u028b"+
		"\u0003$\u0012\u0000\u027d\u028b\u0003&\u0013\u0000\u027e\u028b\u0003("+
		"\u0014\u0000\u027f\u028b\u0003*\u0015\u0000\u0280\u028b\u0003,\u0016\u0000"+
		"\u0281\u028b\u0003.\u0017\u0000\u0282\u028b\u00030\u0018\u0000\u0283\u028b"+
		"\u00032\u0019\u0000\u0284\u028b\u00034\u001a\u0000\u0285\u028b\u00036"+
		"\u001b\u0000\u0286\u028b\u00038\u001c\u0000\u0287\u028b\u0003:\u001d\u0000"+
		"\u0288\u028b\u0003<\u001e\u0000\u0289\u028b\u0003>\u001f\u0000\u028a\u027c"+
		"\u0001\u0000\u0000\u0000\u028a\u027d\u0001\u0000\u0000\u0000\u028a\u027e"+
		"\u0001\u0000\u0000\u0000\u028a\u027f\u0001\u0000\u0000\u0000\u028a\u0280"+
		"\u0001\u0000\u0000\u0000\u028a\u0281\u0001\u0000\u0000\u0000\u028a\u0282"+
		"\u0001\u0000\u0000\u0000\u028a\u0283\u0001\u0000\u0000\u0000\u028a\u0284"+
		"\u0001\u0000\u0000\u0000\u028a\u0285\u0001\u0000\u0000\u0000\u028a\u0286"+
		"\u0001\u0000\u0000\u0000\u028a\u0287\u0001\u0000\u0000\u0000\u028a\u0288"+
		"\u0001\u0000\u0000\u0000\u028a\u0289\u0001\u0000\u0000\u0000\u028b\u001f"+
		"\u0001\u0000\u0000\u0000\u028c\u028d\u0005O\u0000\u0000\u028d\u028e\u0003"+
		"@ \u0000\u028e\u0290\u0003\u009cN\u0000\u028f\u0291\u0003B!\u0000\u0290"+
		"\u028f\u0001\u0000\u0000\u0000\u0290\u0291\u0001\u0000\u0000\u0000\u0291"+
		"!\u0001\u0000\u0000\u0000\u0292\u0293\u0005\u0167\u0000\u0000\u0293\u0294"+
		"\u0003@ \u0000\u0294\u0296\u0003\u009cN\u0000\u0295\u0297\u0003D\"\u0000"+
		"\u0296\u0295\u0001\u0000\u0000\u0000\u0296\u0297\u0001\u0000\u0000\u0000"+
		"\u0297#\u0001\u0000\u0000\u0000\u0298\u0299\u0005\t\u0000\u0000\u0299"+
		"\u029a\u00050\u0000\u0000\u029a\u029b\u0003\u009cN\u0000\u029b\u029c\u0005"+
		"\u0001\u0000\u0000\u029c\u029d\u0003j5\u0000\u029d\u02ab\u0001\u0000\u0000"+
		"\u0000\u029e\u029f\u0005\t\u0000\u0000\u029f\u02a0\u00050\u0000\u0000"+
		"\u02a0\u02a5\u0003\u009cN\u0000\u02a1\u02a2\u0005\u011e\u0000\u0000\u02a2"+
		"\u02a3\u0003F#\u0000\u02a3\u02a4\u0005\u011f\u0000\u0000\u02a4\u02a6\u0001"+
		"\u0000\u0000\u0000\u02a5\u02a1\u0001\u0000\u0000\u0000\u02a5\u02a6\u0001"+
		"\u0000\u0000\u0000\u02a6\u02a8\u0001\u0000\u0000\u0000\u02a7\u02a9\u0003"+
		"H$\u0000\u02a8\u02a7\u0001\u0000\u0000\u0000\u02a8\u02a9\u0001\u0000\u0000"+
		"\u0000\u02a9\u02ab\u0001\u0000\u0000\u0000\u02aa\u0298\u0001\u0000\u0000"+
		"\u0000\u02aa\u029e\u0001\u0000\u0000\u0000\u02ab%\u0001\u0000\u0000\u0000"+
		"\u02ac\u02ad\u0005\t\u0000\u0000\u02ad\u02ae\u0005_\u0000\u0000\u02ae"+
		"\u02af\u0003\u009cN\u0000\u02af\u02b0\u0005&\u0000\u0000\u02b0\u02b1\u0003"+
		"\u009cN\u0000\u02b1\u02b2\u0005\u011e\u0000\u0000\u02b2\u02b3\u0003\u00e2"+
		"q\u0000\u02b3\u02b4\u0005\u011f\u0000\u0000\u02b4\'\u0001\u0000\u0000"+
		"\u0000\u02b5\u02b6\u0005\t\u0000\u0000\u02b6\u02b7\u0005\u0174\u0000\u0000"+
		"\u02b7\u02b8\u0003\u009cN\u0000\u02b8\u02b9\u0005\u0001\u0000\u0000\u02b9"+
		"\u02ba\u0003j5\u0000\u02ba)\u0001\u0000\u0000\u0000\u02bb\u02bc\u0005"+
		"\t\u0000\u0000\u02bc\u02bd\u0005\u016c\u0000\u0000\u02bd\u02be\u0005\u0174"+
		"\u0000\u0000\u02be\u02bf\u0003\u009cN\u0000\u02bf\u02c0\u0005\u0001\u0000"+
		"\u0000\u02c0\u02c1\u0003j5\u0000\u02c1+\u0001\u0000\u0000\u0000\u02c2"+
		"\u02c3\u0005\t\u0000\u0000\u02c3\u02c4\u0005\u016a\u0000\u0000\u02c4\u02c5"+
		"\u0003\u009cN\u0000\u02c5\u02c7\u0005\u011e\u0000\u0000\u02c6\u02c8\u0003"+
		"F#\u0000\u02c7\u02c6\u0001\u0000\u0000\u0000\u02c7\u02c8\u0001\u0000\u0000"+
		"\u0000\u02c8\u02c9\u0001\u0000\u0000\u0000\u02c9\u02ca\u0005\u011f\u0000"+
		"\u0000\u02ca\u02cb\u0005\u016e\u0000\u0000\u02cb\u02cc\u0003\u0220\u0110"+
		"\u0000\u02cc\u02cd\u0003H$\u0000\u02cd-\u0001\u0000\u0000\u0000\u02ce"+
		"\u02cf\u0005\t\u0000\u0000\u02cf\u02d0\u0005\u016d\u0000\u0000\u02d0\u02d1"+
		"\u0003\u009cN\u0000\u02d1\u02d3\u0005\u011e\u0000\u0000\u02d2\u02d4\u0003"+
		"F#\u0000\u02d3\u02d2\u0001\u0000\u0000\u0000\u02d3\u02d4\u0001\u0000\u0000"+
		"\u0000\u02d4\u02d5\u0001\u0000\u0000\u0000\u02d5\u02d6\u0005\u011f\u0000"+
		"\u0000\u02d6\u02d7\u0003H$\u0000\u02d7/\u0001\u0000\u0000\u0000\u02d8"+
		"\u02d9\u0005\t\u0000\u0000\u02d9\u02da\u0005\u016b\u0000\u0000\u02da\u02db"+
		"\u0003\u009cN\u0000\u02db\u02dd\u0005\u011e\u0000\u0000\u02dc\u02de\u0003"+
		"F#\u0000\u02dd\u02dc\u0001\u0000\u0000\u0000\u02dd\u02de\u0001\u0000\u0000"+
		"\u0000\u02de\u02df\u0001\u0000\u0000\u0000\u02df\u02e0\u0005\u011f\u0000"+
		"\u0000\u02e0\u02e1\u0005\u0001\u0000\u0000\u02e1\u02e2\u0003j5\u0000\u02e2"+
		"1\u0001\u0000\u0000\u0000\u02e3\u02e4\u0005\t\u0000\u0000\u02e4\u02e5"+
		"\u0005\u0171\u0000\u0000\u02e5\u02e6\u0003\u009cN\u0000\u02e6\u02e7\u0003"+
		"H$\u0000\u02e73\u0001\u0000\u0000\u0000\u02e8\u02e9\u0005\t\u0000\u0000"+
		"\u02e9\u02ea\u0005\u0170\u0000\u0000\u02ea\u02ec\u0003\u009cN\u0000\u02eb"+
		"\u02ed\u0003H$\u0000\u02ec\u02eb\u0001\u0000\u0000\u0000\u02ec\u02ed\u0001"+
		"\u0000\u0000\u0000\u02ed5\u0001\u0000\u0000\u0000\u02ee\u02ef\u0005\t"+
		"\u0000\u0000\u02ef\u02f0\u0005\u0168\u0000\u0000\u02f0\u02f2\u0003\u009c"+
		"N\u0000\u02f1\u02f3\u0003H$\u0000\u02f2\u02f1\u0001\u0000\u0000\u0000"+
		"\u02f2\u02f3\u0001\u0000\u0000\u0000\u02f37\u0001\u0000\u0000\u0000\u02f4"+
		"\u02f5\u0005\t\u0000\u0000\u02f5\u02f6\u0005\u016f\u0000\u0000\u02f6\u02f8"+
		"\u0003\u009cN\u0000\u02f7\u02f9\u0003H$\u0000\u02f8\u02f7\u0001\u0000"+
		"\u0000\u0000\u02f8\u02f9\u0001\u0000\u0000\u0000\u02f99\u0001\u0000\u0000"+
		"\u0000\u02fa\u02fb\u0005\t\u0000\u0000\u02fb\u02fc\u0005\u0173\u0000\u0000"+
		"\u02fc\u02fe\u0003\u009cN\u0000\u02fd\u02ff\u0003H$\u0000\u02fe\u02fd"+
		"\u0001\u0000\u0000\u0000\u02fe\u02ff\u0001\u0000\u0000\u0000\u02ff;\u0001"+
		"\u0000\u0000\u0000\u0300\u0301\u0005\t\u0000\u0000\u0301\u0302\u0005\u0172"+
		"\u0000\u0000\u0302\u0304\u0003\u009cN\u0000\u0303\u0305\u0003H$\u0000"+
		"\u0304\u0303\u0001\u0000\u0000\u0000\u0304\u0305\u0001\u0000\u0000\u0000"+
		"\u0305=\u0001\u0000\u0000\u0000\u0306\u0307\u0005\t\u0000\u0000\u0307"+
		"\u0308\u0005\u0169\u0000\u0000\u0308\u0309\u0005Z\u0000\u0000\u0309\u030b"+
		"\u0003\u009cN\u0000\u030a\u030c\u0003H$\u0000\u030b\u030a\u0001\u0000"+
		"\u0000\u0000\u030b\u030c\u0001\u0000\u0000\u0000\u030c?\u0001\u0000\u0000"+
		"\u0000\u030d\u031e\u00050\u0000\u0000\u030e\u031e\u0005_\u0000\u0000\u030f"+
		"\u031e\u0005\u0174\u0000\u0000\u0310\u031e\u0005\u016a\u0000\u0000\u0311"+
		"\u031e\u0005\u016d\u0000\u0000\u0312\u031e\u0005\u016b\u0000\u0000\u0313"+
		"\u031e\u0005\u0171\u0000\u0000\u0314\u031e\u0005\u0170\u0000\u0000\u0315"+
		"\u031e\u0005\u0168\u0000\u0000\u0316\u031e\u0005\u016f\u0000\u0000\u0317"+
		"\u031e\u0005\u0173\u0000\u0000\u0318\u031e\u0005\u0172\u0000\u0000\u0319"+
		"\u031a\u0005\u0169\u0000\u0000\u031a\u031e\u0005Z\u0000\u0000\u031b\u031c"+
		"\u0005\u016c\u0000\u0000\u031c\u031e\u0005\u0174\u0000\u0000\u031d\u030d"+
		"\u0001\u0000\u0000\u0000\u031d\u030e\u0001\u0000\u0000\u0000\u031d\u030f"+
		"\u0001\u0000\u0000\u0000\u031d\u0310\u0001\u0000\u0000\u0000\u031d\u0311"+
		"\u0001\u0000\u0000\u0000\u031d\u0312\u0001\u0000\u0000\u0000\u031d\u0313"+
		"\u0001\u0000\u0000\u0000\u031d\u0314\u0001\u0000\u0000\u0000\u031d\u0315"+
		"\u0001\u0000\u0000\u0000\u031d\u0316\u0001\u0000\u0000\u0000\u031d\u0317"+
		"\u0001\u0000\u0000\u0000\u031d\u0318\u0001\u0000\u0000\u0000\u031d\u0319"+
		"\u0001\u0000\u0000\u0000\u031d\u031b\u0001\u0000\u0000\u0000\u031eA\u0001"+
		"\u0000\u0000\u0000\u031f\u0320\u0003H$\u0000\u0320C\u0001\u0000\u0000"+
		"\u0000\u0321\u0322\u0003H$\u0000\u0322E\u0001\u0000\u0000\u0000\u0323"+
		"\u0325\b\u0000\u0000\u0000\u0324\u0323\u0001\u0000\u0000\u0000\u0325\u0326"+
		"\u0001\u0000\u0000\u0000\u0326\u0324\u0001\u0000\u0000\u0000\u0326\u0327"+
		"\u0001\u0000\u0000\u0000\u0327G\u0001\u0000\u0000\u0000\u0328\u032a\b"+
		"\u0001\u0000\u0000\u0329\u0328\u0001\u0000\u0000\u0000\u032a\u032b\u0001"+
		"\u0000\u0000\u0000\u032b\u0329\u0001\u0000\u0000\u0000\u032b\u032c\u0001"+
		"\u0000\u0000\u0000\u032cI\u0001\u0000\u0000\u0000\u032d\u032e\u0003L&"+
		"\u0000\u032e\u032f\u0003T*\u0000\u032fK\u0001\u0000\u0000\u0000\u0330"+
		"\u0331\u0005:\u0000\u0000\u0331\u0336\u0003N\'\u0000\u0332\u0333\u0005"+
		"\u0116\u0000\u0000\u0333\u0335\u0003N\'\u0000\u0334\u0332\u0001\u0000"+
		"\u0000\u0000\u0335\u0338\u0001\u0000\u0000\u0000\u0336\u0334\u0001\u0000"+
		"\u0000\u0000\u0336\u0337\u0001\u0000\u0000\u0000\u0337M\u0001\u0000\u0000"+
		"\u0000\u0338\u0336\u0001\u0000\u0000\u0000\u0339\u033a\u0003R)\u0000\u033a"+
		"\u033b\u0005\u011e\u0000\u0000\u033b\u033c\u0003P(\u0000\u033c\u033d\u0005"+
		"\u011f\u0000\u0000\u033d\u0342\u0001\u0000\u0000\u0000\u033e\u033f\u0003"+
		"R)\u0000\u033f\u0340\u0003\u01ea\u00f5\u0000\u0340\u0342\u0001\u0000\u0000"+
		"\u0000\u0341\u0339\u0001\u0000\u0000\u0000\u0341\u033e\u0001\u0000\u0000"+
		"\u0000\u0342O\u0001\u0000\u0000\u0000\u0343\u0346\u0003J%\u0000\u0344"+
		"\u0346\u0003T*\u0000\u0345\u0343\u0001\u0000\u0000\u0000\u0345\u0344\u0001"+
		"\u0000\u0000\u0000\u0346Q\u0001\u0000\u0000\u0000\u0347\u0348\u0003\u01e6"+
		"\u00f3\u0000\u0348\u0349\u0005\u0001\u0000\u0000\u0349S\u0001\u0000\u0000"+
		"\u0000\u034a\u034f\u0003j5\u0000\u034b\u034f\u0003V+\u0000\u034c\u034f"+
		"\u0003b1\u0000\u034d\u034f\u0003\u01ac\u00d6\u0000\u034e\u034a\u0001\u0000"+
		"\u0000\u0000\u034e\u034b\u0001\u0000\u0000\u0000\u034e\u034c\u0001\u0000"+
		"\u0000\u0000\u034e\u034d\u0001\u0000\u0000\u0000\u034fU\u0001\u0000\u0000"+
		"\u0000\u0350\u0351\u0003X,\u0000\u0351W\u0001\u0000\u0000\u0000\u0352"+
		"\u0353\u0003^/\u0000\u0353\u0354\u0003Z-\u0000\u0354\u0355\u0003`0\u0000"+
		"\u0355Y\u0001\u0000\u0000\u0000\u0356\u035a\u0003\u009cN\u0000\u0357\u035a"+
		"\u0003\u01ea\u00f5\u0000\u0358\u035a\u0003\u01f4\u00fa\u0000\u0359\u0356"+
		"\u0001\u0000\u0000\u0000\u0359\u0357\u0001\u0000\u0000\u0000\u0359\u0358"+
		"\u0001\u0000\u0000\u0000\u035a\u035f\u0001\u0000\u0000\u0000\u035b\u035c"+
		"\u0005\u011e\u0000\u0000\u035c\u035d\u0003\u00e2q\u0000\u035d\u035e\u0005"+
		"\u011f\u0000\u0000\u035e\u0360\u0001\u0000\u0000\u0000\u035f\u035b\u0001"+
		"\u0000\u0000\u0000\u035f\u0360\u0001\u0000\u0000\u0000\u0360\u0362\u0001"+
		"\u0000\u0000\u0000\u0361\u0363\u0003\u0098L\u0000\u0362\u0361\u0001\u0000"+
		"\u0000\u0000\u0362\u0363\u0001\u0000\u0000\u0000\u0363[\u0001\u0000\u0000"+
		"\u0000\u0364\u0366\u0003X,\u0000\u0365\u0367\u0003d2\u0000\u0366\u0365"+
		"\u0001\u0000\u0000\u0000\u0366\u0367\u0001\u0000\u0000\u0000\u0367]\u0001"+
		"\u0000\u0000\u0000\u0368\u036a\u0005`\u0000\u0000\u0369\u036b\u0005w\u0000"+
		"\u0000\u036a\u0369\u0001\u0000\u0000\u0000\u036a\u036b\u0001\u0000\u0000"+
		"\u0000\u036b\u036c\u0001\u0000\u0000\u0000\u036c\u036d\u0005\u0019\u0000"+
		"\u0000\u036d_\u0001\u0000\u0000\u0000\u036e\u0372\u0003j5\u0000\u036f"+
		"\u0372\u0003\u01ea\u00f5\u0000\u0370\u0372\u0003\u01bc\u00de\u0000\u0371"+
		"\u036e\u0001\u0000\u0000\u0000\u0371\u036f\u0001\u0000\u0000\u0000\u0371"+
		"\u0370\u0001\u0000\u0000\u0000\u0372a\u0001\u0000\u0000\u0000\u0373\u0374"+
		"\u0005\u0094\u0000\u0000\u0374\u0375\u0003\u0096K\u0000\u0375\u0376\u0005"+
		"\u0087\u0000\u0000\u0376\u0378\u0003f3\u0000\u0377\u0379\u0003\u008cF"+
		"\u0000\u0378\u0377\u0001\u0000\u0000\u0000\u0378\u0379\u0001\u0000\u0000"+
		"\u0000\u0379\u037b\u0001\u0000\u0000\u0000\u037a\u037c\u0003\u016a\u00b5"+
		"\u0000\u037b\u037a\u0001\u0000\u0000\u0000\u037b\u037c\u0001\u0000\u0000"+
		"\u0000\u037c\u037e\u0001\u0000\u0000\u0000\u037d\u037f\u0003d2\u0000\u037e"+
		"\u037d\u0001\u0000\u0000\u0000\u037e\u037f\u0001\u0000\u0000\u0000\u037f"+
		"c\u0001\u0000\u0000\u0000\u0380\u0381\u0005,\u0000\u0000\u0381\u0382\u0005"+
		"\u0122\u0000\u0000\u0382e\u0001\u0000\u0000\u0000\u0383\u0388\u0003h4"+
		"\u0000\u0384\u0385\u0005\u0116\u0000\u0000\u0385\u0387\u0003h4\u0000\u0386"+
		"\u0384\u0001\u0000\u0000\u0000\u0387\u038a\u0001\u0000\u0000\u0000\u0388"+
		"\u0386\u0001\u0000\u0000\u0000\u0388\u0389\u0001\u0000\u0000\u0000\u0389"+
		"g\u0001\u0000\u0000\u0000\u038a\u0388\u0001\u0000\u0000\u0000\u038b\u038c"+
		"\u0003\u00e4r\u0000\u038c\u038d\u0005\u0113\u0000\u0000\u038d\u038e\u0003"+
		"\u0168\u00b4\u0000\u038ei\u0001\u0000\u0000\u0000\u038f\u0390\u0003l6"+
		"\u0000\u0390k\u0001\u0000\u0000\u0000\u0391\u0397\u0003r9\u0000\u0392"+
		"\u0393\u0003n7\u0000\u0393\u0394\u0003r9\u0000\u0394\u0396\u0001\u0000"+
		"\u0000\u0000\u0395\u0392\u0001\u0000\u0000\u0000\u0396\u0399\u0001\u0000"+
		"\u0000\u0000\u0397\u0395\u0001\u0000\u0000\u0000\u0397\u0398\u0001\u0000"+
		"\u0000\u0000\u0398m\u0001\u0000\u0000\u0000\u0399\u0397\u0001\u0000\u0000"+
		"\u0000\u039a\u039c\u0003p8\u0000\u039b\u039d\u0003\u0080@\u0000\u039c"+
		"\u039b\u0001\u0000\u0000\u0000\u039c\u039d\u0001\u0000\u0000\u0000\u039d"+
		"o\u0001\u0000\u0000\u0000\u039e\u039f\u0005\u0018\u0000\u0000\u039fq\u0001"+
		"\u0000\u0000\u0000\u03a0\u03a6\u0003x<\u0000\u03a1\u03a2\u0003t:\u0000"+
		"\u03a2\u03a3\u0003x<\u0000\u03a3\u03a5\u0001\u0000\u0000\u0000\u03a4\u03a1"+
		"\u0001\u0000\u0000\u0000\u03a5\u03a8\u0001\u0000\u0000\u0000\u03a6\u03a4"+
		"\u0001\u0000\u0000\u0000\u03a6\u03a7\u0001\u0000\u0000\u0000\u03a7s\u0001"+
		"\u0000\u0000\u0000\u03a8\u03a6\u0001\u0000\u0000\u0000\u03a9\u03ab\u0003"+
		"v;\u0000\u03aa\u03ac\u0003\u0080@\u0000\u03ab\u03aa\u0001\u0000\u0000"+
		"\u0000\u03ab\u03ac\u0001\u0000\u0000\u0000\u03acu\u0001\u0000\u0000\u0000"+
		"\u03ad\u03ae\u0007\u0002\u0000\u0000\u03aew\u0001\u0000\u0000\u0000\u03af"+
		"\u03b3\u0003z=\u0000\u03b0\u03b3\u0003|>\u0000\u03b1\u03b3\u0003\u01ea"+
		"\u00f5\u0000\u03b2\u03af\u0001\u0000\u0000\u0000\u03b2\u03b0\u0001\u0000"+
		"\u0000\u0000\u03b2\u03b1\u0001\u0000\u0000\u0000\u03b3y\u0001\u0000\u0000"+
		"\u0000\u03b4\u03b5\u0005\u011e\u0000\u0000\u03b5\u03b6\u0003j5\u0000\u03b6"+
		"\u03b7\u0005\u011f\u0000\u0000\u03b7{\u0001\u0000\u0000\u0000\u03b8\u03ba"+
		"\u0005-\u0000\u0000\u03b9\u03bb\u0003~?\u0000\u03ba\u03b9\u0001\u0000"+
		"\u0000\u0000\u03ba\u03bb\u0001\u0000\u0000\u0000\u03bb\u03bd\u0001\u0000"+
		"\u0000\u0000\u03bc\u03be\u0003\u0080@\u0000\u03bd\u03bc\u0001\u0000\u0000"+
		"\u0000\u03bd\u03be\u0001\u0000\u0000\u0000\u03be\u03bf\u0001\u0000\u0000"+
		"\u0000\u03bf\u03d3\u0003\u0082A\u0000\u03c0\u03c2\u0003\u008cF\u0000\u03c1"+
		"\u03c3\u0003\u016a\u00b5\u0000\u03c2\u03c1\u0001\u0000\u0000\u0000\u03c2"+
		"\u03c3\u0001\u0000\u0000\u0000\u03c3\u03c5\u0001\u0000\u0000\u0000\u03c4"+
		"\u03c6\u0003\u017c\u00be\u0000\u03c5\u03c4\u0001\u0000\u0000\u0000\u03c5"+
		"\u03c6\u0001\u0000\u0000\u0000\u03c6\u03c8\u0001\u0000\u0000\u0000\u03c7"+
		"\u03c9\u0003\u018c\u00c6\u0000\u03c8\u03c7\u0001\u0000\u0000\u0000\u03c8"+
		"\u03c9\u0001\u0000\u0000\u0000\u03c9\u03cb\u0001\u0000\u0000\u0000\u03ca"+
		"\u03cc\u0003\u018e\u00c7\u0000\u03cb\u03ca\u0001\u0000\u0000\u0000\u03cb"+
		"\u03cc\u0001\u0000\u0000\u0000\u03cc\u03ce\u0001\u0000\u0000\u0000\u03cd"+
		"\u03cf\u0003\u016e\u00b7\u0000\u03ce\u03cd\u0001\u0000\u0000\u0000\u03ce"+
		"\u03cf\u0001\u0000\u0000\u0000\u03cf\u03d1\u0001\u0000\u0000\u0000\u03d0"+
		"\u03d2\u0003\u017a\u00bd\u0000\u03d1\u03d0\u0001\u0000\u0000\u0000\u03d1"+
		"\u03d2\u0001\u0000\u0000\u0000\u03d2\u03d4\u0001\u0000\u0000\u0000\u03d3"+
		"\u03c0\u0001\u0000\u0000\u0000\u03d3\u03d4\u0001\u0000\u0000\u0000\u03d4"+
		"}\u0001\u0000\u0000\u0000\u03d5\u03d6\u0005\u0019\u0000\u0000\u03d6\u03d7"+
		"\u0003\u009cN\u0000\u03d7\u007f\u0001\u0000\u0000\u0000\u03d8\u03d9\u0007"+
		"\u0003\u0000\u0000\u03d9\u0081\u0001\u0000\u0000\u0000\u03da\u03df\u0003"+
		"\u0084B\u0000\u03db\u03dc\u0005\u0116\u0000\u0000\u03dc\u03de\u0003\u0084"+
		"B\u0000\u03dd\u03db\u0001\u0000\u0000\u0000\u03de\u03e1\u0001\u0000\u0000"+
		"\u0000\u03df\u03dd\u0001\u0000\u0000\u0000\u03df\u03e0\u0001\u0000\u0000"+
		"\u0000\u03e0\u0083\u0001\u0000\u0000\u0000\u03e1\u03df\u0001\u0000\u0000"+
		"\u0000\u03e2\u03e4\u0003\u012c\u0096\u0000\u03e3\u03e5\u0003\u0086C\u0000"+
		"\u03e4\u03e3\u0001\u0000\u0000\u0000\u03e4\u03e5\u0001\u0000\u0000\u0000"+
		"\u03e5\u03e8\u0001\u0000\u0000\u0000\u03e6\u03e8\u0003\u0088D\u0000\u03e7"+
		"\u03e2\u0001\u0000\u0000\u0000\u03e7\u03e6\u0001\u0000\u0000\u0000\u03e8"+
		"\u0085\u0001\u0000\u0000\u0000\u03e9\u03eb\u0005\u0001\u0000\u0000\u03ea"+
		"\u03e9\u0001\u0000\u0000\u0000\u03ea\u03eb\u0001\u0000\u0000\u0000\u03eb"+
		"\u03ec\u0001\u0000\u0000\u0000\u03ec\u03ed\u0003\u01e8\u00f4\u0000\u03ed"+
		"\u0087\u0001\u0000\u0000\u0000\u03ee\u03ef\u0003\u008aE\u0000\u03ef\u0089"+
		"\u0001\u0000\u0000\u0000\u03f0\u03f1\u0005\u0175\u0000\u0000\u03f1\u03f3"+
		"\u0005\u0125\u0000\u0000\u03f2\u03f0\u0001\u0000\u0000\u0000\u03f2\u03f3"+
		"\u0001\u0000\u0000\u0000\u03f3\u03f4\u0001\u0000\u0000\u0000\u03f4\u03f5"+
		"\u0005\u0122\u0000\u0000\u03f5\u008b\u0001\u0000\u0000\u0000\u03f6\u03f7"+
		"\u0005\u0011\u0000\u0000\u03f7\u03f9\u0003\u0090H\u0000\u03f8\u03fa\u0003"+
		"\u008eG\u0000\u03f9\u03f8\u0001\u0000\u0000\u0000\u03f9\u03fa\u0001\u0000"+
		"\u0000\u0000\u03fa\u008d\u0001\u0000\u0000\u0000\u03fb\u03fc\u0003\u01ea"+
		"\u00f5\u0000\u03fc\u008f\u0001\u0000\u0000\u0000\u03fd\u0413\u0003\u0096"+
		"K\u0000\u03fe\u0400\u0005\u0116\u0000\u0000\u03ff\u0401\u0003\u0094J\u0000"+
		"\u0400\u03ff\u0001\u0000\u0000\u0000\u0400\u0401\u0001\u0000\u0000\u0000"+
		"\u0401\u0402\u0001\u0000\u0000\u0000\u0402\u0412\u0003\u0096K\u0000\u0403"+
		"\u0405\u0003\u009eO\u0000\u0404\u0406\u0003\u0094J\u0000\u0405\u0404\u0001"+
		"\u0000\u0000\u0000\u0405\u0406\u0001\u0000\u0000\u0000\u0406\u0407\u0001"+
		"\u0000\u0000\u0000\u0407\u0408\u0003\u0096K\u0000\u0408\u0412\u0001\u0000"+
		"\u0000\u0000\u0409\u040b\u0003\u00a0P\u0000\u040a\u040c\u0003\u0094J\u0000"+
		"\u040b\u040a\u0001\u0000\u0000\u0000\u040b\u040c\u0001\u0000\u0000\u0000"+
		"\u040c\u040d\u0001\u0000\u0000\u0000\u040d\u040f\u0003\u0096K\u0000\u040e"+
		"\u0410\u0003\u00a4R\u0000\u040f\u040e\u0001\u0000\u0000\u0000\u040f\u0410"+
		"\u0001\u0000\u0000\u0000\u0410\u0412\u0001\u0000\u0000\u0000\u0411\u03fe"+
		"\u0001\u0000\u0000\u0000\u0411\u0403\u0001\u0000\u0000\u0000\u0411\u0409"+
		"\u0001\u0000\u0000\u0000\u0412\u0415\u0001\u0000\u0000\u0000\u0413\u0411"+
		"\u0001\u0000\u0000\u0000\u0413\u0414\u0001\u0000\u0000\u0000\u0414\u0091"+
		"\u0001\u0000\u0000\u0000\u0415\u0413\u0001\u0000\u0000\u0000\u0416\u0418"+
		"\u0005\u0116\u0000\u0000\u0417\u0419\u0003\u0094J\u0000\u0418\u0417\u0001"+
		"\u0000\u0000\u0000\u0418\u0419\u0001\u0000\u0000\u0000\u0419\u041a\u0001"+
		"\u0000\u0000\u0000\u041a\u042a\u0003\u0096K\u0000\u041b\u041d\u0003\u009e"+
		"O\u0000\u041c\u041e\u0003\u0094J\u0000\u041d\u041c\u0001\u0000\u0000\u0000"+
		"\u041d\u041e\u0001\u0000\u0000\u0000\u041e\u041f\u0001\u0000\u0000\u0000"+
		"\u041f\u0420\u0003\u0096K\u0000\u0420\u042a\u0001\u0000\u0000\u0000\u0421"+
		"\u0423\u0003\u00a0P\u0000\u0422\u0424\u0003\u0094J\u0000\u0423\u0422\u0001"+
		"\u0000\u0000\u0000\u0423\u0424\u0001\u0000\u0000\u0000\u0424\u0425\u0001"+
		"\u0000\u0000\u0000\u0425\u0427\u0003\u0096K\u0000\u0426\u0428\u0003\u00a4"+
		"R\u0000\u0427\u0426\u0001\u0000\u0000\u0000\u0427\u0428\u0001\u0000\u0000"+
		"\u0000\u0428\u042a\u0001\u0000\u0000\u0000\u0429\u0416\u0001\u0000\u0000"+
		"\u0000\u0429\u041b\u0001\u0000\u0000\u0000\u0429\u0421\u0001\u0000\u0000"+
		"\u0000\u042a\u042d\u0001\u0000\u0000\u0000\u042b\u0429\u0001\u0000\u0000"+
		"\u0000\u042b\u042c\u0001\u0000\u0000\u0000\u042c\u042f\u0001\u0000\u0000"+
		"\u0000\u042d\u042b\u0001\u0000\u0000\u0000\u042e\u0430\u0003\u008eG\u0000"+
		"\u042f\u042e\u0001\u0000\u0000\u0000\u042f\u0430\u0001\u0000\u0000\u0000"+
		"\u0430\u0093\u0001\u0000\u0000\u0000\u0431\u0432\u0005\u015d\u0000\u0000"+
		"\u0432\u0095\u0001\u0000\u0000\u0000\u0433\u0435\u0003\u009cN\u0000\u0434"+
		"\u0436\u0003\u0098L\u0000\u0435\u0434\u0001\u0000\u0000\u0000\u0435\u0436"+
		"\u0001\u0000\u0000\u0000\u0436\u0448\u0001\u0000\u0000\u0000\u0437\u0438"+
		"\u0003\u01ea\u00f5\u0000\u0438\u0439\u0003\u0086C\u0000\u0439\u0448\u0001"+
		"\u0000\u0000\u0000\u043a\u043c\u0003\u01f4\u00fa\u0000\u043b\u043d\u0003"+
		"\u0098L\u0000\u043c\u043b\u0001\u0000\u0000\u0000\u043c\u043d\u0001\u0000"+
		"\u0000\u0000\u043d\u0448\u0001\u0000\u0000\u0000\u043e\u0440\u0003\u00ac"+
		"V\u0000\u043f\u0441\u0003\u0098L\u0000\u0440\u043f\u0001\u0000\u0000\u0000"+
		"\u0440\u0441\u0001\u0000\u0000\u0000\u0441\u0448\u0001\u0000\u0000\u0000"+
		"\u0442\u0448\u0003\u01ac\u00d6\u0000\u0443\u0445\u0003z=\u0000\u0444\u0446"+
		"\u0003\u0098L\u0000\u0445\u0444\u0001\u0000\u0000\u0000\u0445\u0446\u0001"+
		"\u0000\u0000\u0000\u0446\u0448\u0001\u0000\u0000\u0000\u0447\u0433\u0001"+
		"\u0000\u0000\u0000\u0447\u0437\u0001\u0000\u0000\u0000\u0447\u043a\u0001"+
		"\u0000\u0000\u0000\u0447\u043e\u0001\u0000\u0000\u0000\u0447\u0442\u0001"+
		"\u0000\u0000\u0000\u0447\u0443\u0001\u0000\u0000\u0000\u0448\u0097\u0001"+
		"\u0000\u0000\u0000\u0449\u044a\u0005\u0001\u0000\u0000\u044a\u044e\u0003"+
		"\u01e8\u00f4\u0000\u044b\u044c\u0004L\u0000\u0000\u044c\u044e\u0003\u01e8"+
		"\u00f4\u0000\u044d\u0449\u0001\u0000\u0000\u0000\u044d\u044b\u0001\u0000"+
		"\u0000\u0000\u044e\u0099\u0001\u0000\u0000\u0000\u044f\u0456\u0003\u009c"+
		"N\u0000\u0450\u0456\u0003\u01ea\u00f5\u0000\u0451\u0456\u0003\u01f4\u00fa"+
		"\u0000\u0452\u0456\u0003\u00acV\u0000\u0453\u0456\u0003\u01ac\u00d6\u0000"+
		"\u0454\u0456\u0003z=\u0000\u0455\u044f\u0001\u0000\u0000\u0000\u0455\u0450"+
		"\u0001\u0000\u0000\u0000\u0455\u0451\u0001\u0000\u0000\u0000\u0455\u0452"+
		"\u0001\u0000\u0000\u0000\u0455\u0453\u0001\u0000\u0000\u0000\u0455\u0454"+
		"\u0001\u0000\u0000\u0000\u0456\u009b\u0001\u0000\u0000\u0000\u0457\u045d"+
		"\u0003\u01e6\u00f3\u0000\u0458\u045b\u0005\u0125\u0000\u0000\u0459\u045c"+
		"\u0003\u0200\u0100\u0000\u045a\u045c\u0003\u01e6\u00f3\u0000\u045b\u0459"+
		"\u0001\u0000\u0000\u0000\u045b\u045a\u0001\u0000\u0000\u0000\u045c\u045e"+
		"\u0001\u0000\u0000\u0000\u045d\u0458\u0001\u0000\u0000\u0000\u045d\u045e"+
		"\u0001\u0000\u0000\u0000\u045e\u0464\u0001\u0000\u0000\u0000\u045f\u0462"+
		"\u0005\u0125\u0000\u0000\u0460\u0463\u0003\u0200\u0100\u0000\u0461\u0463"+
		"\u0003\u01e6\u00f3\u0000\u0462\u0460\u0001\u0000\u0000\u0000\u0462\u0461"+
		"\u0001\u0000\u0000\u0000\u0463\u0465\u0001\u0000\u0000\u0000\u0464\u045f"+
		"\u0001\u0000\u0000\u0000\u0464\u0465\u0001\u0000\u0000\u0000\u0465\u009d"+
		"\u0001\u0000\u0000\u0000\u0466\u0467\u0005\n\u0000\u0000\u0467\u0470\u0005"+
		"\u001b\u0000\u0000\u0468\u0469\u00055\u0000\u0000\u0469\u0470\u0005\u001b"+
		"\u0000\u0000\u046a\u046c\u0005 \u0000\u0000\u046b\u046d\u0003\u00a2Q\u0000"+
		"\u046c\u046b\u0001\u0000\u0000\u0000\u046c\u046d\u0001\u0000\u0000\u0000"+
		"\u046d\u046e\u0001\u0000\u0000\u0000\u046e\u0470\u0005\u001b\u0000\u0000"+
		"\u046f\u0466\u0001\u0000\u0000\u0000\u046f\u0468\u0001\u0000\u0000\u0000"+
		"\u046f\u046a\u0001\u0000\u0000\u0000\u0470\u009f\u0001\u0000\u0000\u0000"+
		"\u0471\u0473\u0003\u00a2Q\u0000\u0472\u0471\u0001\u0000\u0000\u0000\u0472"+
		"\u0473\u0001\u0000\u0000\u0000\u0473\u0474\u0001\u0000\u0000\u0000\u0474"+
		"\u0475\u0005\u001b\u0000\u0000\u0475\u00a1\u0001\u0000\u0000\u0000\u0476"+
		"\u047c\u0005\u0017\u0000\u0000\u0477\u0479\u0007\u0004\u0000\u0000\u0478"+
		"\u047a\u0005\'\u0000\u0000\u0479\u0478\u0001\u0000\u0000\u0000\u0479\u047a"+
		"\u0001\u0000\u0000\u0000\u047a\u047c\u0001\u0000\u0000\u0000\u047b\u0476"+
		"\u0001\u0000\u0000\u0000\u047b\u0477\u0001\u0000\u0000\u0000\u047c\u00a3"+
		"\u0001\u0000\u0000\u0000\u047d\u0480\u0003\u00a6S\u0000\u047e\u0480\u0003"+
		"\u00a8T\u0000\u047f\u047d\u0001\u0000\u0000\u0000\u047f\u047e\u0001\u0000"+
		"\u0000\u0000\u0480\u00a5\u0001\u0000\u0000\u0000\u0481\u0482\u0005&\u0000"+
		"\u0000\u0482\u0483\u0003\u016c\u00b6\u0000\u0483\u00a7\u0001\u0000\u0000"+
		"\u0000\u0484\u0485\u0003\u00aaU\u0000\u0485\u0486\u0005\u011e\u0000\u0000"+
		"\u0486\u0487\u0003\u00e2q\u0000\u0487\u0488\u0005\u011f\u0000\u0000\u0488"+
		"\u00a9\u0001\u0000\u0000\u0000\u0489\u048a\u00057\u0000\u0000\u048a\u00ab"+
		"\u0001\u0000\u0000\u0000\u048b\u048c\u00050\u0000\u0000\u048c\u048d\u0005"+
		"\u011e\u0000\u0000\u048d\u048e\u0003\u00aeW\u0000\u048e\u048f\u0005\u011f"+
		"\u0000\u0000\u048f\u0492\u0001\u0000\u0000\u0000\u0490\u0492\u0003\u00ae"+
		"W\u0000\u0491\u048b\u0001\u0000\u0000\u0000\u0491\u0490\u0001\u0000\u0000"+
		"\u0000\u0492\u00ad\u0001\u0000\u0000\u0000\u0493\u049a\u0003\u00b0X\u0000"+
		"\u0494\u049a\u0003\u00be_\u0000\u0495\u049a\u0003\u00c8d\u0000\u0496\u049a"+
		"\u0003\u00ccf\u0000\u0497\u049a\u0003\u00d8l\u0000\u0498\u049a\u0003\u00dc"+
		"n\u0000\u0499\u0493\u0001\u0000\u0000\u0000\u0499\u0494\u0001\u0000\u0000"+
		"\u0000\u0499\u0495\u0001\u0000\u0000\u0000\u0499\u0496\u0001\u0000\u0000"+
		"\u0000\u0499\u0497\u0001\u0000\u0000\u0000\u0499\u0498\u0001\u0000\u0000"+
		"\u0000\u049a\u00af\u0001\u0000\u0000\u0000\u049b\u049c\u0003\u00b8\\\u0000"+
		"\u049c\u049d\u0005\u011e\u0000\u0000\u049d\u049e\u0003\u00b2Y\u0000\u049e"+
		"\u049f\u0005\u011f\u0000\u0000\u049f\u00b1\u0001\u0000\u0000\u0000\u04a0"+
		"\u04a5\u0003\u00b4Z\u0000\u04a1\u04a2\u0005\u0116\u0000\u0000\u04a2\u04a4"+
		"\u0003\u00b4Z\u0000\u04a3\u04a1\u0001\u0000\u0000\u0000\u04a4\u04a7\u0001"+
		"\u0000\u0000\u0000\u04a5\u04a3\u0001\u0000\u0000\u0000\u04a5\u04a6\u0001"+
		"\u0000\u0000\u0000\u04a6\u04a8\u0001\u0000\u0000\u0000\u04a7\u04a5\u0001"+
		"\u0000\u0000\u0000\u04a8\u04a9\u0004Y\u0001\u0001\u04a9\u00b3\u0001\u0000"+
		"\u0000\u0000\u04aa\u04ab\u0005\u0159\u0000\u0000\u04ab\u04ac\u0005\u011d"+
		"\u0000\u0000\u04ac\u04bc\u0003\u00b6[\u0000\u04ad\u04ae\u0005\u015a\u0000"+
		"\u0000\u04ae\u04af\u0005\u011d\u0000\u0000\u04af\u04bc\u0003\u00b6[\u0000"+
		"\u04b0\u04b1\u0005\'\u0000\u0000\u04b1\u04b2\u0005\u011d\u0000\u0000\u04b2"+
		"\u04bc\u0003\u00b6[\u0000\u04b3\u04b4\u0005\u015b\u0000\u0000\u04b4\u04b5"+
		"\u0005\u011d\u0000\u0000\u04b5\u04bc\u0003\u00b6[\u0000\u04b6\u04b7\u0005"+
		"\u015c\u0000\u0000\u04b7\u04b8\u0005\u011d\u0000\u0000\u04b8\u04b9\u0003"+
		"\u00ba]\u0000\u04b9\u04ba\u0004Z\u0002\u0001\u04ba\u04bc\u0001\u0000\u0000"+
		"\u0000\u04bb\u04aa\u0001\u0000\u0000\u0000\u04bb\u04ad\u0001\u0000\u0000"+
		"\u0000\u04bb\u04b0\u0001\u0000\u0000\u0000\u04bb\u04b3\u0001\u0000\u0000"+
		"\u0000\u04bb\u04b6\u0001\u0000\u0000\u0000\u04bc\u00b5\u0001\u0000\u0000"+
		"\u0000\u04bd\u04c1\u0003\u012c\u0096\u0000\u04be\u04c1\u0003\u00ba]\u0000"+
		"\u04bf\u04c1\u0003\u00bc^\u0000\u04c0\u04bd\u0001\u0000\u0000\u0000\u04c0"+
		"\u04be\u0001\u0000\u0000\u0000\u04c0\u04bf\u0001\u0000\u0000\u0000\u04c1"+
		"\u00b7\u0001\u0000\u0000\u0000\u04c2\u04c3\u0005\u0150\u0000\u0000\u04c3"+
		"\u00b9\u0001\u0000\u0000\u0000\u04c4\u04c7\u0005\u017d\u0000\u0000\u04c5"+
		"\u04c7\u0003\u0208\u0104\u0000\u04c6\u04c4\u0001\u0000\u0000\u0000\u04c6"+
		"\u04c5\u0001\u0000\u0000\u0000\u04c7\u00bb\u0001\u0000\u0000\u0000\u04c8"+
		"\u04c9\u0007\u0005\u0000\u0000\u04c9\u00bd\u0001\u0000\u0000\u0000\u04ca"+
		"\u04cb\u0003\u00c6c\u0000\u04cb\u04cc\u0005\u011e\u0000\u0000\u04cc\u04cd"+
		"\u0003\u00c0`\u0000\u04cd\u04ce\u0005\u011f\u0000\u0000\u04ce\u00bf\u0001"+
		"\u0000\u0000\u0000\u04cf\u04d4\u0003\u00c2a\u0000\u04d0\u04d1\u0005\u0116"+
		"\u0000\u0000\u04d1\u04d3\u0003\u00c2a\u0000\u04d2\u04d0\u0001\u0000\u0000"+
		"\u0000\u04d3\u04d6\u0001\u0000\u0000\u0000\u04d4\u04d2\u0001\u0000\u0000"+
		"\u0000\u04d4\u04d5\u0001\u0000\u0000\u0000\u04d5\u04d7\u0001\u0000\u0000"+
		"\u0000\u04d6\u04d4\u0001\u0000\u0000\u0000\u04d7\u04d8\u0004`\u0003\u0001"+
		"\u04d8\u00c1\u0001\u0000\u0000\u0000\u04d9\u04da\u0005\u015e\u0000\u0000"+
		"\u04da\u04db\u0005\u011d\u0000\u0000\u04db\u04e0\u0003\u00c4b\u0000\u04dc"+
		"\u04dd\u0005\u015f\u0000\u0000\u04dd\u04de\u0005\u011d\u0000\u0000\u04de"+
		"\u04e0\u0003\u00c4b\u0000\u04df\u04d9\u0001\u0000\u0000\u0000\u04df\u04dc"+
		"\u0001\u0000\u0000\u0000\u04e0\u00c3\u0001\u0000\u0000\u0000\u04e1\u04e2"+
		"\u0003\u0130\u0098\u0000\u04e2\u00c5\u0001\u0000\u0000\u0000\u04e3\u04e4"+
		"\u0005\u0153\u0000\u0000\u04e4\u00c7\u0001\u0000\u0000\u0000\u04e5\u04e6"+
		"\u0003\u00cae\u0000\u04e6\u04e8\u0005\u011e\u0000\u0000\u04e7\u04e9\u0003"+
		"\u012c\u0096\u0000\u04e8\u04e7\u0001\u0000\u0000\u0000\u04e8\u04e9\u0001"+
		"\u0000\u0000\u0000\u04e9\u04ea\u0001\u0000\u0000\u0000\u04ea\u04eb\u0005"+
		"\u011f\u0000\u0000\u04eb\u00c9\u0001\u0000\u0000\u0000\u04ec\u04ed\u0005"+
		"\u0156\u0000\u0000\u04ed\u00cb\u0001\u0000\u0000\u0000\u04ee\u04ef\u0003"+
		"\u00d4j\u0000\u04ef\u04f0\u0005\u011e\u0000\u0000\u04f0\u04f1\u0003\u00ce"+
		"g\u0000\u04f1\u04f2\u0005\u011f\u0000\u0000\u04f2\u00cd\u0001\u0000\u0000"+
		"\u0000\u04f3\u04f8\u0003\u00d0h\u0000\u04f4\u04f5\u0005\u0116\u0000\u0000"+
		"\u04f5\u04f7\u0003\u00d0h\u0000\u04f6\u04f4\u0001\u0000\u0000\u0000\u04f7"+
		"\u04fa\u0001\u0000\u0000\u0000\u04f8\u04f6\u0001\u0000\u0000\u0000\u04f8"+
		"\u04f9\u0001\u0000\u0000\u0000\u04f9\u04fb\u0001\u0000\u0000\u0000\u04fa"+
		"\u04f8\u0001\u0000\u0000\u0000\u04fb\u04fc\u0004g\u0004\u0001\u04fc\u00cf"+
		"\u0001\u0000\u0000\u0000\u04fd\u04fe\u0005j\u0000\u0000\u04fe\u04ff\u0005"+
		"\u011d\u0000\u0000\u04ff\u0513\u0003\u00d2i\u0000\u0500\u0501\u0005\u0161"+
		"\u0000\u0000\u0501\u0502\u0005\u011d\u0000\u0000\u0502\u0513\u0003\u00d2"+
		"i\u0000\u0503\u0504\u0005\u0160\u0000\u0000\u0504\u0505\u0005\u011d\u0000"+
		"\u0000\u0505\u0513\u0003\u00d2i\u0000\u0506\u0507\u0005\u0162\u0000\u0000"+
		"\u0507\u0508\u0005\u011d\u0000\u0000\u0508\u0513\u0003\u00d2i\u0000\u0509"+
		"\u050a\u0005\u0163\u0000\u0000\u050a\u050b\u0005\u011d\u0000\u0000\u050b"+
		"\u0513\u0003\u00d2i\u0000\u050c\u050d\u0005\u0164\u0000\u0000\u050d\u050e"+
		"\u0005\u011d\u0000\u0000\u050e\u0513\u0003\u00d2i\u0000\u050f\u0510\u0005"+
		"\u0165\u0000\u0000\u0510\u0511\u0005\u011d\u0000\u0000\u0511\u0513\u0003"+
		"\u00d2i\u0000\u0512\u04fd\u0001\u0000\u0000\u0000\u0512\u0500\u0001\u0000"+
		"\u0000\u0000\u0512\u0503\u0001\u0000\u0000\u0000\u0512\u0506\u0001\u0000"+
		"\u0000\u0000\u0512\u0509\u0001\u0000\u0000\u0000\u0512\u050c\u0001\u0000"+
		"\u0000\u0000\u0512\u050f\u0001\u0000\u0000\u0000\u0513\u00d1\u0001\u0000"+
		"\u0000\u0000\u0514\u0519\u0003\u00ba]\u0000\u0515\u0519\u0003\u00d6k\u0000"+
		"\u0516\u0519\u0003\u0130\u0098\u0000\u0517\u0519\u0003\u00bc^\u0000\u0518"+
		"\u0514\u0001\u0000\u0000\u0000\u0518\u0515\u0001\u0000\u0000\u0000\u0518"+
		"\u0516\u0001\u0000\u0000\u0000\u0518\u0517\u0001\u0000\u0000\u0000\u0519"+
		"\u00d3\u0001\u0000\u0000\u0000\u051a\u051b\u0005\u0154\u0000\u0000\u051b"+
		"\u00d5\u0001\u0000\u0000\u0000\u051c\u051d\u0005\u011e\u0000\u0000\u051d"+
		"\u0522\u0005\u017d\u0000\u0000\u051e\u051f\u0005\u0116\u0000\u0000\u051f"+
		"\u0521\u0005\u017d\u0000\u0000\u0520\u051e\u0001\u0000\u0000\u0000\u0521"+
		"\u0524\u0001\u0000\u0000\u0000\u0522\u0520\u0001\u0000\u0000\u0000\u0522"+
		"\u0523\u0001\u0000\u0000\u0000\u0523\u0525\u0001\u0000\u0000\u0000\u0524"+
		"\u0522\u0001\u0000\u0000\u0000\u0525\u0526\u0005\u011f\u0000\u0000\u0526"+
		"\u00d7\u0001\u0000\u0000\u0000\u0527\u0528\u0003\u00dam\u0000\u0528\u0529"+
		"\u0005\u011e\u0000\u0000\u0529\u052a\u0003\u009cN\u0000\u052a\u052b\u0005"+
		"\u0116\u0000\u0000\u052b\u052c\u0005\u0166\u0000\u0000\u052c\u052d\u0005"+
		"\u011d\u0000\u0000\u052d\u052e\u0003\u00ba]\u0000\u052e\u052f\u0005\u011f"+
		"\u0000\u0000\u052f\u00d9\u0001\u0000\u0000\u0000\u0530\u0531\u0005\u0155"+
		"\u0000\u0000\u0531\u00db\u0001\u0000\u0000\u0000\u0532\u0533\u0003\u00de"+
		"o\u0000\u0533\u0535\u0005\u011e\u0000\u0000\u0534\u0536\u0003\u00e0p\u0000"+
		"\u0535\u0534\u0001\u0000\u0000\u0000\u0535\u0536\u0001\u0000\u0000\u0000"+
		"\u0536\u0537\u0001\u0000\u0000\u0000\u0537\u0538\u0005\u011f\u0000\u0000"+
		"\u0538\u00dd\u0001\u0000\u0000\u0000\u0539\u053e\u0005\u0151\u0000\u0000"+
		"\u053a\u053e\u0005\u0152\u0000\u0000\u053b\u053e\u0005\u0157\u0000\u0000"+
		"\u053c\u053e\u0003\u01e6\u00f3\u0000\u053d\u0539\u0001\u0000\u0000\u0000"+
		"\u053d\u053a\u0001\u0000\u0000\u0000\u053d\u053b\u0001\u0000\u0000\u0000"+
		"\u053d\u053c\u0001\u0000\u0000\u0000\u053e\u00df\u0001\u0000\u0000\u0000"+
		"\u053f\u0544\u0003\u012c\u0096\u0000\u0540\u0541\u0005\u0116\u0000\u0000"+
		"\u0541\u0543\u0003\u012c\u0096\u0000\u0542\u0540\u0001\u0000\u0000\u0000"+
		"\u0543\u0546\u0001\u0000\u0000\u0000\u0544\u0542\u0001\u0000\u0000\u0000"+
		"\u0544\u0545\u0001\u0000\u0000\u0000\u0545\u00e1\u0001\u0000\u0000\u0000"+
		"\u0546\u0544\u0001\u0000\u0000\u0000\u0547\u054c\u0003\u00e4r\u0000\u0548"+
		"\u0549\u0005\u0116\u0000\u0000\u0549\u054b\u0003\u00e4r\u0000\u054a\u0548"+
		"\u0001\u0000\u0000\u0000\u054b\u054e\u0001\u0000\u0000\u0000\u054c\u054a"+
		"\u0001\u0000\u0000\u0000\u054c\u054d\u0001\u0000\u0000\u0000\u054d\u00e3"+
		"\u0001\u0000\u0000\u0000\u054e\u054c\u0001\u0000\u0000\u0000\u054f\u0550"+
		"\u0003\u01e6\u00f3\u0000\u0550\u0551\u0005\u0125\u0000\u0000\u0551\u0553"+
		"\u0001\u0000\u0000\u0000\u0552\u054f\u0001\u0000\u0000\u0000\u0552\u0553"+
		"\u0001\u0000\u0000\u0000\u0553\u0554\u0001\u0000\u0000\u0000\u0554\u0559"+
		"\u0003\u01e6\u00f3\u0000\u0555\u0556\u0005\u0114\u0000\u0000\u0556\u0558"+
		"\u0003\u01e6\u00f3\u0000\u0557\u0555\u0001\u0000\u0000\u0000\u0558\u055b"+
		"\u0001\u0000\u0000\u0000\u0559\u0557\u0001\u0000\u0000\u0000\u0559\u055a"+
		"\u0001\u0000\u0000\u0000\u055a\u0561\u0001\u0000\u0000\u0000\u055b\u0559"+
		"\u0001\u0000\u0000\u0000\u055c\u055d\u0003\u01e6\u00f3\u0000\u055d\u055e"+
		"\u0005\u0125\u0000\u0000\u055e\u055f\u0003\u01ea\u00f5\u0000\u055f\u0561"+
		"\u0001\u0000\u0000\u0000\u0560\u0552\u0001\u0000\u0000\u0000\u0560\u055c"+
		"\u0001\u0000\u0000\u0000\u0561\u00e5\u0001\u0000\u0000\u0000\u0562\u0563"+
		"\u0003\u01e6\u00f3\u0000\u0563\u0564\u0005\u0125\u0000\u0000\u0564\u0566"+
		"\u0001\u0000\u0000\u0000\u0565\u0562\u0001\u0000\u0000\u0000\u0565\u0566"+
		"\u0001\u0000\u0000\u0000\u0566\u0567\u0001\u0000\u0000\u0000\u0567\u056c"+
		"\u0003\u01e6\u00f3\u0000\u0568\u0569\u0005\u0114\u0000\u0000\u0569\u056b"+
		"\u0003\u01e6\u00f3\u0000\u056a\u0568\u0001\u0000\u0000\u0000\u056b\u056e"+
		"\u0001\u0000\u0000\u0000\u056c\u056a\u0001\u0000\u0000\u0000\u056c\u056d"+
		"\u0001\u0000\u0000\u0000\u056d\u0575\u0001\u0000\u0000\u0000\u056e\u056c"+
		"\u0001\u0000\u0000\u0000\u056f\u0570\u0003\u01e6\u00f3\u0000\u0570\u0571"+
		"\u0005\u0125\u0000\u0000\u0571\u0572\u0003\u01ea\u00f5\u0000\u0572\u0575"+
		"\u0001\u0000\u0000\u0000\u0573\u0575\u0003\u01ea\u00f5\u0000\u0574\u0565"+
		"\u0001\u0000\u0000\u0000\u0574\u056f\u0001\u0000\u0000\u0000\u0574\u0573"+
		"\u0001\u0000\u0000\u0000\u0575\u00e7\u0001\u0000\u0000\u0000\u0576\u0581"+
		"\u0003\u00eau\u0000\u0577\u0581\u0003\u0142\u00a1\u0000\u0578\u0579\u0003"+
		"\u0138\u009c\u0000\u0579\u057a\u0003\u0136\u009b\u0000\u057a\u0581\u0001"+
		"\u0000\u0000\u0000\u057b\u0581\u0003\u0146\u00a3\u0000\u057c\u0581\u0003"+
		"\u0106\u0083\u0000\u057d\u0581\u0003\u01ea\u00f5\u0000\u057e\u0581\u0003"+
		"\u0232\u0119\u0000\u057f\u0581\u0003\u014e\u00a7\u0000\u0580\u0576\u0001"+
		"\u0000\u0000\u0000\u0580\u0577\u0001\u0000\u0000\u0000\u0580\u0578\u0001"+
		"\u0000\u0000\u0000\u0580\u057b\u0001\u0000\u0000\u0000\u0580\u057c\u0001"+
		"\u0000\u0000\u0000\u0580\u057d\u0001\u0000\u0000\u0000\u0580\u057e\u0001"+
		"\u0000\u0000\u0000\u0580\u057f\u0001\u0000\u0000\u0000\u0581\u00e9\u0001"+
		"\u0000\u0000\u0000\u0582\u0585\u0003\u00ecv\u0000\u0583\u0584\u0005\u012a"+
		"\u0000\u0000\u0584\u0586\u0003\u0220\u0110\u0000\u0585\u0583\u0001\u0000"+
		"\u0000\u0000\u0585\u0586\u0001\u0000\u0000\u0000\u0586\u058d\u0001\u0000"+
		"\u0000\u0000\u0587\u058a\u0003\u00eew\u0000\u0588\u0589\u0005\u012a\u0000"+
		"\u0000\u0589\u058b\u0003\u0220\u0110\u0000\u058a\u0588\u0001\u0000\u0000"+
		"\u0000\u058a\u058b\u0001\u0000\u0000\u0000\u058b\u058d\u0001\u0000\u0000"+
		"\u0000\u058c\u0582\u0001\u0000\u0000\u0000\u058c\u0587\u0001\u0000\u0000"+
		"\u0000\u058d\u00eb\u0001\u0000\u0000\u0000\u058e\u058f\u0005\u011e\u0000"+
		"\u0000\u058f\u0590\u0003\u012c\u0096\u0000\u0590\u0591\u0005\u011f\u0000"+
		"\u0000\u0591\u00ed\u0001\u0000\u0000\u0000\u0592\u059c\u0003\u020a\u0105"+
		"\u0000\u0593\u059c\u0003\u00e4r\u0000\u0594\u059c\u0003\u00f2y\u0000\u0595"+
		"\u059c\u0003\u00f8|\u0000\u0596\u059c\u0003\u0108\u0084\u0000\u0597\u059c"+
		"\u0003\u01de\u00ef\u0000\u0598\u059c\u0003\u014e\u00a7\u0000\u0599\u059c"+
		"\u0003\u010c\u0086\u0000\u059a\u059c\u0003\u00f0x\u0000\u059b\u0592\u0001"+
		"\u0000\u0000\u0000\u059b\u0593\u0001\u0000\u0000\u0000\u059b\u0594\u0001"+
		"\u0000\u0000\u0000\u059b\u0595\u0001\u0000\u0000\u0000\u059b\u0596\u0001"+
		"\u0000\u0000\u0000\u059b\u0597\u0001\u0000\u0000\u0000\u059b\u0598\u0001"+
		"\u0000\u0000\u0000\u059b\u0599\u0001\u0000\u0000\u0000\u059b\u059a\u0001"+
		"\u0000\u0000\u0000\u059c\u00ef\u0001\u0000\u0000\u0000\u059d\u059e\u0003"+
		"z=\u0000\u059e\u00f1\u0001\u0000\u0000\u0000\u059f\u05a0\u0005E\u0000"+
		"\u0000\u05a0\u05a1\u0005\u011e\u0000\u0000\u05a1\u05a2\u0003\u008aE\u0000"+
		"\u05a2\u05a3\u0005\u011f\u0000\u0000\u05a3\u05b0\u0001\u0000\u0000\u0000"+
		"\u05a4\u05a7\u0003\u00f4z\u0000\u05a5\u05a7\u0003\u00f6{\u0000\u05a6\u05a4"+
		"\u0001\u0000\u0000\u0000\u05a6\u05a5\u0001\u0000\u0000\u0000\u05a7\u05a8"+
		"\u0001\u0000\u0000\u0000\u05a8\u05aa\u0005\u011e\u0000\u0000\u05a9\u05ab"+
		"\u0003\u0080@\u0000\u05aa\u05a9\u0001\u0000\u0000\u0000\u05aa\u05ab\u0001"+
		"\u0000\u0000\u0000\u05ab\u05ac\u0001\u0000\u0000\u0000\u05ac\u05ad\u0003"+
		"\u012c\u0096\u0000\u05ad\u05ae\u0005\u011f\u0000\u0000\u05ae\u05b0\u0001"+
		"\u0000\u0000\u0000\u05af\u059f\u0001\u0000\u0000\u0000\u05af\u05a6\u0001"+
		"\u0000\u0000\u0000\u05b0\u00f3\u0001\u0000\u0000\u0000\u05b1\u05b2\u0007"+
		"\u0006\u0000\u0000\u05b2\u00f5\u0001\u0000\u0000\u0000\u05b3\u05b4\u0007"+
		"\u0007\u0000\u0000\u05b4\u00f7\u0001\u0000\u0000\u0000\u05b5\u05b6\u0005"+
		"\u0007\u0000\u0000\u05b6\u05b7\u0003\u012c\u0096\u0000\u05b7\u05b9\u0003"+
		"\u00fe\u007f\u0000\u05b8\u05ba\u0003\u0102\u0081\u0000\u05b9\u05b8\u0001"+
		"\u0000\u0000\u0000\u05b9\u05ba\u0001\u0000\u0000\u0000\u05ba\u05bb\u0001"+
		"\u0000\u0000\u0000\u05bb\u05bc\u0005\f\u0000\u0000\u05bc\u05c5\u0001\u0000"+
		"\u0000\u0000\u05bd\u05be\u0005\u0007\u0000\u0000\u05be\u05c0\u0003\u00fa"+
		"}\u0000\u05bf\u05c1\u0003\u0102\u0081\u0000\u05c0\u05bf\u0001\u0000\u0000"+
		"\u0000\u05c0\u05c1\u0001\u0000\u0000\u0000\u05c1\u05c2\u0001\u0000\u0000"+
		"\u0000\u05c2\u05c3\u0005\f\u0000\u0000\u05c3\u05c5\u0001\u0000\u0000\u0000"+
		"\u05c4\u05b5\u0001\u0000\u0000\u0000\u05c4\u05bd\u0001\u0000\u0000\u0000"+
		"\u05c5\u00f9\u0001\u0000\u0000\u0000\u05c6\u05c8\u0003\u00fc~\u0000\u05c7"+
		"\u05c6\u0001\u0000\u0000\u0000\u05c8\u05c9\u0001\u0000\u0000\u0000\u05c9"+
		"\u05c7\u0001\u0000\u0000\u0000\u05c9\u05ca\u0001\u0000\u0000\u0000\u05ca"+
		"\u00fb\u0001\u0000\u0000\u0000\u05cb\u05cc\u00058\u0000\u0000\u05cc\u05cd"+
		"\u0003\u012c\u0096\u0000\u05cd\u05ce\u00051\u0000\u0000\u05ce\u05cf\u0003"+
		"\u0104\u0082\u0000\u05cf\u00fd\u0001\u0000\u0000\u0000\u05d0\u05d2\u0003"+
		"\u0100\u0080\u0000\u05d1\u05d0\u0001\u0000\u0000\u0000\u05d2\u05d3\u0001"+
		"\u0000\u0000\u0000\u05d3\u05d1\u0001\u0000\u0000\u0000\u05d3\u05d4\u0001"+
		"\u0000\u0000\u0000\u05d4\u00ff\u0001\u0000\u0000\u0000\u05d5\u05d6\u0005"+
		"8\u0000\u0000\u05d6\u05d7\u0003\u012c\u0096\u0000\u05d7\u05d8\u00051\u0000"+
		"\u0000\u05d8\u05d9\u0003\u0104\u0082\u0000\u05d9\u0101\u0001\u0000\u0000"+
		"\u0000\u05da\u05db\u0005\r\u0000\u0000\u05db\u05dc\u0003\u0104\u0082\u0000"+
		"\u05dc\u0103\u0001\u0000\u0000\u0000\u05dd\u05e0\u0003\u012c\u0096\u0000"+
		"\u05de\u05e0\u0003\u0106\u0083\u0000\u05df\u05dd\u0001\u0000\u0000\u0000"+
		"\u05df\u05de\u0001\u0000\u0000\u0000\u05e0\u0105\u0001\u0000\u0000\u0000"+
		"\u05e1\u05e2\u0005\"\u0000\u0000\u05e2\u0107\u0001\u0000\u0000\u0000\u05e3"+
		"\u05e4\u0003\u010a\u0085\u0000\u05e4\u05e5\u0005\u011e\u0000\u0000\u05e5"+
		"\u05e6\u0003\u012c\u0096\u0000\u05e6\u05e7\u0005\u0001\u0000\u0000\u05e7"+
		"\u05e8\u0003\u0220\u0110\u0000\u05e8\u05e9\u0005\u011f\u0000\u0000\u05e9"+
		"\u0109\u0001\u0000\u0000\u0000\u05ea\u05eb\u0007\b\u0000\u0000\u05eb\u010b"+
		"\u0001\u0000\u0000\u0000\u05ec\u05ed\u0003\u010e\u0087\u0000\u05ed\u05ee"+
		"\u0003\u0110\u0088\u0000\u05ee\u010d\u0001\u0000\u0000\u0000\u05ef\u05f0"+
		"\u0003\u00f4z\u0000\u05f0\u05f2\u0005\u011e\u0000\u0000\u05f1\u05f3\u0003"+
		"\u01e4\u00f2\u0000\u05f2\u05f1\u0001\u0000\u0000\u0000\u05f2\u05f3\u0001"+
		"\u0000\u0000\u0000\u05f3\u05f4\u0001\u0000\u0000\u0000\u05f4\u05f5\u0005"+
		"\u011f\u0000\u0000\u05f5\u0601\u0001\u0000\u0000\u0000\u05f6\u05f7\u0003"+
		"\u0126\u0093\u0000\u05f7\u05f8\u0005\u011e\u0000\u0000\u05f8\u05f9\u0003"+
		"\u01e4\u00f2\u0000\u05f9\u05fe\u0005\u011f\u0000\u0000\u05fa\u05fc\u0003"+
		"\u0128\u0094\u0000\u05fb\u05fa\u0001\u0000\u0000\u0000\u05fb\u05fc\u0001"+
		"\u0000\u0000\u0000\u05fc\u05fd\u0001\u0000\u0000\u0000\u05fd\u05ff\u0003"+
		"\u012a\u0095\u0000\u05fe\u05fb\u0001\u0000\u0000\u0000\u05fe\u05ff\u0001"+
		"\u0000\u0000\u0000\u05ff\u0601\u0001\u0000\u0000\u0000\u0600\u05ef\u0001"+
		"\u0000\u0000\u0000\u0600\u05f6\u0001\u0000\u0000\u0000\u0601\u010f\u0001"+
		"\u0000\u0000\u0000\u0602\u0603\u0005v\u0000\u0000\u0603\u0605\u0005\u011e"+
		"\u0000\u0000\u0604\u0606\u0003\u0112\u0089\u0000\u0605\u0604\u0001\u0000"+
		"\u0000\u0000\u0605\u0606\u0001\u0000\u0000\u0000\u0606\u0608\u0001\u0000"+
		"\u0000\u0000\u0607\u0609\u0003\u016e\u00b7\u0000\u0608\u0607\u0001\u0000"+
		"\u0000\u0000\u0608\u0609\u0001\u0000\u0000\u0000\u0609\u060b\u0001\u0000"+
		"\u0000\u0000\u060a\u060c\u0003\u0114\u008a\u0000\u060b\u060a\u0001\u0000"+
		"\u0000\u0000\u060b\u060c\u0001\u0000\u0000\u0000\u060c\u060d\u0001\u0000"+
		"\u0000\u0000\u060d\u060e\u0005\u011f\u0000\u0000\u060e\u0111\u0001\u0000"+
		"\u0000\u0000\u060f\u0610\u0005x\u0000\u0000\u0610\u0611\u0005?\u0000\u0000"+
		"\u0611\u0612\u0003\u01e4\u00f2\u0000\u0612\u0113\u0001\u0000\u0000\u0000"+
		"\u0613\u0614\u0003\u0116\u008b\u0000\u0614\u0615\u0003\u0118\u008c\u0000"+
		"\u0615\u0115\u0001\u0000\u0000\u0000\u0616\u0617\u0007\t\u0000\u0000\u0617"+
		"\u0117\u0001\u0000\u0000\u0000\u0618\u061c\u0003\u011a\u008d\u0000\u0619"+
		"\u061c\u0003\u011e\u008f\u0000\u061a\u061c\u0003\u0122\u0091\u0000\u061b"+
		"\u0618\u0001\u0000\u0000\u0000\u061b\u0619\u0001\u0000\u0000\u0000\u061b"+
		"\u061a\u0001\u0000\u0000\u0000\u061c\u0119\u0001\u0000\u0000\u0000\u061d"+
		"\u061e\u0005>\u0000\u0000\u061e\u061f\u0003\u011c\u008e\u0000\u061f\u0620"+
		"\u0005\u0003\u0000\u0000\u0620\u0621\u0003\u011c\u008e\u0000\u0621\u011b"+
		"\u0001\u0000\u0000\u0000\u0622\u0626\u0003\u011e\u008f\u0000\u0623\u0626"+
		"\u0003\u0120\u0090\u0000\u0624\u0626\u0003\u0122\u0091\u0000\u0625\u0622"+
		"\u0001\u0000\u0000\u0000\u0625\u0623\u0001\u0000\u0000\u0000\u0625\u0624"+
		"\u0001\u0000\u0000\u0000\u0626\u011d\u0001\u0000\u0000\u0000\u0627\u0628"+
		"\u0003\u0124\u0092\u0000\u0628\u0629\u0005z\u0000\u0000\u0629\u011f\u0001"+
		"\u0000\u0000\u0000\u062a\u062b\u0003\u0124\u0092\u0000\u062b\u062c\u0005"+
		"Y\u0000\u0000\u062c\u0121\u0001\u0000\u0000\u0000\u062d\u062e\u0005H\u0000"+
		"\u0000\u062e\u062f\u0005\u0084\u0000\u0000\u062f\u0123\u0001\u0000\u0000"+
		"\u0000\u0630\u0631\u0007\n\u0000\u0000\u0631\u0125\u0001\u0000\u0000\u0000"+
		"\u0632\u0633\u0007\u000b\u0000\u0000\u0633\u0127\u0001\u0000\u0000\u0000"+
		"\u0634\u0635\u0005\u0011\u0000\u0000\u0635\u0636\u0007\f\u0000\u0000\u0636"+
		"\u0129\u0001\u0000\u0000\u0000\u0637\u0638\u0007\r\u0000\u0000\u0638\u0639"+
		"\u0005#\u0000\u0000\u0639\u012b\u0001\u0000\u0000\u0000\u063a\u063f\u0003"+
		"\u012e\u0097\u0000\u063b\u063f\u0003\u0166\u00b3\u0000\u063c\u063f\u0003"+
		"\u01ea\u00f5\u0000\u063d\u063f\u0003\u0156\u00ab\u0000\u063e\u063a\u0001"+
		"\u0000\u0000\u0000\u063e\u063b\u0001\u0000\u0000\u0000\u063e\u063c\u0001"+
		"\u0000\u0000\u0000\u063e\u063d\u0001\u0000\u0000\u0000\u063f\u012d\u0001"+
		"\u0000\u0000\u0000\u0640\u0644\u0003\u0130\u0098\u0000\u0641\u0644\u0003"+
		"\u0142\u00a1\u0000\u0642\u0644\u0003\u0106\u0083\u0000\u0643\u0640\u0001"+
		"\u0000\u0000\u0000\u0643\u0641\u0001\u0000\u0000\u0000\u0643\u0642\u0001"+
		"\u0000\u0000\u0000\u0644\u012f\u0001\u0000\u0000\u0000\u0645\u064a\u0003"+
		"\u0132\u0099\u0000\u0646\u0647\u0007\u000e\u0000\u0000\u0647\u0649\u0003"+
		"\u0132\u0099\u0000\u0648\u0646\u0001\u0000\u0000\u0000\u0649\u064c\u0001"+
		"\u0000\u0000\u0000\u064a\u0648\u0001\u0000\u0000\u0000\u064a\u064b\u0001"+
		"\u0000\u0000\u0000\u064b\u0131\u0001\u0000\u0000\u0000\u064c\u064a\u0001"+
		"\u0000\u0000\u0000\u064d\u0652\u0003\u0134\u009a\u0000\u064e\u064f\u0007"+
		"\u000f\u0000\u0000\u064f\u0651\u0003\u0134\u009a\u0000\u0650\u064e\u0001"+
		"\u0000\u0000\u0000\u0651\u0654\u0001\u0000\u0000\u0000\u0652\u0650\u0001"+
		"\u0000\u0000\u0000\u0652\u0653\u0001\u0000\u0000\u0000\u0653\u0133\u0001"+
		"\u0000\u0000\u0000\u0654\u0652\u0001\u0000\u0000\u0000\u0655\u0657\u0003"+
		"\u0138\u009c\u0000\u0656\u0655\u0001\u0000\u0000\u0000\u0656\u0657\u0001"+
		"\u0000\u0000\u0000\u0657\u0658\u0001\u0000\u0000\u0000\u0658\u0659\u0003"+
		"\u0136\u009b\u0000\u0659\u0135\u0001\u0000\u0000\u0000\u065a\u065d\u0003"+
		"\u00eau\u0000\u065b\u065d\u0003\u013a\u009d\u0000\u065c\u065a\u0001\u0000"+
		"\u0000\u0000\u065c\u065b\u0001\u0000\u0000\u0000\u065d\u0137\u0001\u0000"+
		"\u0000\u0000\u065e\u065f\u0007\u000e\u0000\u0000\u065f\u0139\u0001\u0000"+
		"\u0000\u0000\u0660\u0661\u0005U\u0000\u0000\u0661\u0662\u0005\u011e\u0000"+
		"\u0000\u0662\u0663\u0003\u013c\u009e\u0000\u0663\u0664\u0005\u0011\u0000"+
		"\u0000\u0664\u0665\u0003\u0140\u00a0\u0000\u0665\u0666\u0005\u011f\u0000"+
		"\u0000\u0666\u013b\u0001\u0000\u0000\u0000\u0667\u066b\u0003\u01d8\u00ec"+
		"\u0000\u0668\u066b\u0003\u013e\u009f\u0000\u0669\u066b\u0003\u01dc\u00ee"+
		"\u0000\u066a\u0667\u0001\u0000\u0000\u0000\u066a\u0668\u0001\u0000\u0000"+
		"\u0000\u066a\u0669\u0001\u0000\u0000\u0000\u066b\u013d\u0001\u0000\u0000"+
		"\u0000\u066c\u066d\u0007\u0010\u0000\u0000\u066d\u013f\u0001\u0000\u0000"+
		"\u0000\u066e\u0671\u0003\u00e4r\u0000\u066f\u0671\u0003\u0216\u010b\u0000"+
		"\u0670\u066e\u0001\u0000\u0000\u0000\u0670\u066f\u0001\u0000\u0000\u0000"+
		"\u0671\u0141\u0001\u0000\u0000\u0000\u0672\u0677\u0003\u0144\u00a2\u0000"+
		"\u0673\u0674\u0005\u0117\u0000\u0000\u0674\u0676\u0003\u0144\u00a2\u0000"+
		"\u0675\u0673\u0001\u0000\u0000\u0000\u0676\u0679\u0001\u0000\u0000\u0000"+
		"\u0677\u0675\u0001\u0000\u0000\u0000\u0677\u0678\u0001\u0000\u0000\u0000"+
		"\u0678\u0143\u0001\u0000\u0000\u0000\u0679\u0677\u0001\u0000\u0000\u0000"+
		"\u067a\u067d\u0003\u00eau\u0000\u067b\u067d\u0003\u0146\u00a3\u0000\u067c"+
		"\u067a\u0001\u0000\u0000\u0000\u067c\u067b\u0001\u0000\u0000\u0000\u067d"+
		"\u0145\u0001\u0000\u0000\u0000\u067e\u067f\u0003\u0148\u00a4\u0000\u067f"+
		"\u0680\u0005\u011e\u0000\u0000\u0680\u0681\u0003\u014a\u00a5\u0000\u0681"+
		"\u0682\u0005\u011f\u0000\u0000\u0682\u0147\u0001\u0000\u0000\u0000\u0683"+
		"\u0684\u0005\u0092\u0000\u0000\u0684\u0149\u0001\u0000\u0000\u0000\u0685"+
		"\u0687\u0003\u014c\u00a6\u0000\u0686\u0685\u0001\u0000\u0000\u0000\u0686"+
		"\u0687\u0001\u0000\u0000\u0000\u0687\u0689\u0001\u0000\u0000\u0000\u0688"+
		"\u068a\u0003\u0142\u00a1\u0000\u0689\u0688\u0001\u0000\u0000\u0000\u0689"+
		"\u068a\u0001\u0000\u0000\u0000\u068a\u068b\u0001\u0000\u0000\u0000\u068b"+
		"\u068d\u0005\u0011\u0000\u0000\u068c\u0686\u0001\u0000\u0000\u0000\u068c"+
		"\u068d\u0001\u0000\u0000\u0000\u068d\u068e\u0001\u0000\u0000\u0000\u068e"+
		"\u0694\u0003\u012c\u0096\u0000\u068f\u0690\u0003\u012c\u0096\u0000\u0690"+
		"\u0691\u0005\u0116\u0000\u0000\u0691\u0692\u0003\u0142\u00a1\u0000\u0692"+
		"\u0694\u0001\u0000\u0000\u0000\u0693\u068c\u0001\u0000\u0000\u0000\u0693"+
		"\u068f\u0001\u0000\u0000\u0000\u0694\u014b\u0001\u0000\u0000\u0000\u0695"+
		"\u0696\u0007\u0011\u0000\u0000\u0696\u014d\u0001\u0000\u0000\u0000\u0697"+
		"\u0698\u0003\u0150\u00a8\u0000\u0698\u0699\u0005\u011e\u0000\u0000\u0699"+
		"\u069a\u0003\u0142\u00a1\u0000\u069a\u069b\u0005\u0016\u0000\u0000\u069b"+
		"\u069c\u0003\u0142\u00a1\u0000\u069c\u069d\u0005\u011f\u0000\u0000\u069d"+
		"\u06ae\u0001\u0000\u0000\u0000\u069e\u06a2\u0003\u0150\u00a8\u0000\u069f"+
		"\u06a2\u0003\u0152\u00a9\u0000\u06a0\u06a2\u0003\u0154\u00aa\u0000\u06a1"+
		"\u069e\u0001\u0000\u0000\u0000\u06a1\u069f\u0001\u0000\u0000\u0000\u06a1"+
		"\u06a0\u0001\u0000\u0000\u0000\u06a2\u06a3\u0001\u0000\u0000\u0000\u06a3"+
		"\u06a4\u0005\u011e\u0000\u0000\u06a4\u06a5\u0003\u0142\u00a1\u0000\u06a5"+
		"\u06a6\u0005\u0116\u0000\u0000\u06a6\u06a9\u0003\u0142\u00a1\u0000\u06a7"+
		"\u06a8\u0005\u0116\u0000\u0000\u06a8\u06aa\u0003\u0136\u009b\u0000\u06a9"+
		"\u06a7\u0001\u0000\u0000\u0000\u06a9\u06aa\u0001\u0000\u0000\u0000\u06aa"+
		"\u06ab\u0001\u0000\u0000\u0000\u06ab\u06ac\u0005\u011f\u0000\u0000\u06ac"+
		"\u06ae\u0001\u0000\u0000\u0000\u06ad\u0697\u0001\u0000\u0000\u0000\u06ad"+
		"\u06a1\u0001\u0000\u0000\u0000\u06ae\u014f\u0001\u0000\u0000\u0000\u06af"+
		"\u06b0\u0005\u014a\u0000\u0000\u06b0\u0151\u0001\u0000\u0000\u0000\u06b1"+
		"\u06b2\u0005\u014c\u0000\u0000\u06b2\u0153\u0001\u0000\u0000\u0000\u06b3"+
		"\u06b4\u0005\u014b\u0000\u0000\u06b4\u0155\u0001\u0000\u0000\u0000\u06b5"+
		"\u06b6\u0003\u0158\u00ac\u0000\u06b6\u0157\u0001\u0000\u0000\u0000\u06b7"+
		"\u06bc\u0003\u015a\u00ad\u0000\u06b8\u06b9\u0005(\u0000\u0000\u06b9\u06bb"+
		"\u0003\u015a\u00ad\u0000\u06ba\u06b8\u0001\u0000\u0000\u0000\u06bb\u06be"+
		"\u0001\u0000\u0000\u0000\u06bc\u06ba\u0001\u0000\u0000\u0000\u06bc\u06bd"+
		"\u0001\u0000\u0000\u0000\u06bd\u0159\u0001\u0000\u0000\u0000\u06be\u06bc"+
		"\u0001\u0000\u0000\u0000\u06bf\u06c4\u0003\u015c\u00ae\u0000\u06c0\u06c1"+
		"\u0005\u0003\u0000\u0000\u06c1\u06c3\u0003\u015c\u00ae\u0000\u06c2\u06c0"+
		"\u0001\u0000\u0000\u0000\u06c3\u06c6\u0001\u0000\u0000\u0000\u06c4\u06c2"+
		"\u0001\u0000\u0000\u0000\u06c4\u06c5\u0001\u0000\u0000\u0000\u06c5\u015b"+
		"\u0001\u0000\u0000\u0000\u06c6\u06c4\u0001\u0000\u0000\u0000\u06c7\u06c9"+
		"\u0003\u01cc\u00e6\u0000\u06c8\u06c7\u0001\u0000\u0000\u0000\u06c8\u06c9"+
		"\u0001\u0000\u0000\u0000\u06c9\u06ca\u0001\u0000\u0000\u0000\u06ca\u06cb"+
		"\u0003\u015e\u00af\u0000\u06cb\u015d\u0001\u0000\u0000\u0000\u06cc\u06cd"+
		"\u0005\u011e\u0000\u0000\u06cd\u06ce\u0003\u0156\u00ab\u0000\u06ce\u06cf"+
		"\u0005\u011f\u0000\u0000\u06cf\u06d5\u0001\u0000\u0000\u0000\u06d0\u06d2"+
		"\u0003\u0160\u00b0\u0000\u06d1\u06d3\u0003\u01c8\u00e4\u0000\u06d2\u06d1"+
		"\u0001\u0000\u0000\u0000\u06d2\u06d3\u0001\u0000\u0000\u0000\u06d3\u06d5"+
		"\u0001\u0000\u0000\u0000\u06d4\u06cc\u0001\u0000\u0000\u0000\u06d4\u06d0"+
		"\u0001\u0000\u0000\u0000\u06d5\u015f\u0001\u0000\u0000\u0000\u06d6\u06d9"+
		"\u0003\u0162\u00b1\u0000\u06d7\u06d9\u0003\u00eew\u0000\u06d8\u06d6\u0001"+
		"\u0000\u0000\u0000\u06d8\u06d7\u0001\u0000\u0000\u0000\u06d9\u0161\u0001"+
		"\u0000\u0000\u0000\u06da\u06e2\u0003\u0192\u00c9\u0000\u06db\u06e2\u0003"+
		"\u019c\u00ce\u0000\u06dc\u06e2\u0003\u01a0\u00d0\u0000\u06dd\u06e2\u0003"+
		"\u01a2\u00d1\u0000\u06de\u06e2\u0003\u01c4\u00e2\u0000\u06df\u06e2\u0003"+
		"\u01be\u00df\u0000\u06e0\u06e2\u0003\u0164\u00b2\u0000\u06e1\u06da\u0001"+
		"\u0000\u0000\u0000\u06e1\u06db\u0001\u0000\u0000\u0000\u06e1\u06dc\u0001"+
		"\u0000\u0000\u0000\u06e1\u06dd\u0001\u0000\u0000\u0000\u06e1\u06de\u0001"+
		"\u0000\u0000\u0000\u06e1\u06df\u0001\u0000\u0000\u0000\u06e1\u06e0\u0001"+
		"\u0000\u0000\u0000\u06e2\u0163\u0001\u0000\u0000\u0000\u06e3\u06e4\u0003"+
		"\u01ea\u00f5\u0000\u06e4\u0165\u0001\u0000\u0000\u0000\u06e5\u06e8\u0003"+
		"\u00eew\u0000\u06e6\u06e8\u0003\u0106\u0083\u0000\u06e7\u06e5\u0001\u0000"+
		"\u0000\u0000\u06e7\u06e6\u0001\u0000\u0000\u0000\u06e8\u0167\u0001\u0000"+
		"\u0000\u0000\u06e9\u06ed\u0003\u00eew\u0000\u06ea\u06ed\u0003\u012e\u0097"+
		"\u0000\u06eb\u06ed\u0003\u01ea\u00f5\u0000\u06ec\u06e9\u0001\u0000\u0000"+
		"\u0000\u06ec\u06ea\u0001\u0000\u0000\u0000\u06ec\u06eb\u0001\u0000\u0000"+
		"\u0000\u06ed\u0169\u0001\u0000\u0000\u0000\u06ee\u06ef\u00059\u0000\u0000"+
		"\u06ef\u06f0\u0003\u016c\u00b6\u0000\u06f0\u016b\u0001\u0000\u0000\u0000"+
		"\u06f1\u06f2\u0003\u012c\u0096\u0000\u06f2\u016d\u0001\u0000\u0000\u0000"+
		"\u06f3\u06f4\u0005)\u0000\u0000\u06f4\u06f5\u0005?\u0000\u0000\u06f5\u06f6"+
		"\u0003\u0170\u00b8\u0000\u06f6\u016f\u0001\u0000\u0000\u0000\u06f7\u06fc"+
		"\u0003\u0172\u00b9\u0000\u06f8\u06f9\u0005\u0116\u0000\u0000\u06f9\u06fb"+
		"\u0003\u0172\u00b9\u0000\u06fa\u06f8\u0001\u0000\u0000\u0000\u06fb\u06fe"+
		"\u0001\u0000\u0000\u0000\u06fc\u06fa\u0001\u0000\u0000\u0000\u06fc\u06fd"+
		"\u0001\u0000\u0000\u0000\u06fd\u0171\u0001\u0000\u0000\u0000\u06fe\u06fc"+
		"\u0001\u0000\u0000\u0000\u06ff\u0701\u0003\u0168\u00b4\u0000\u0700\u0702"+
		"\u0003\u0174\u00ba\u0000\u0701\u0700\u0001\u0000\u0000\u0000\u0701\u0702"+
		"\u0001\u0000\u0000\u0000\u0702\u0704\u0001\u0000\u0000\u0000\u0703\u0705"+
		"\u0003\u0176\u00bb\u0000\u0704\u0703\u0001\u0000\u0000\u0000\u0704\u0705"+
		"\u0001\u0000\u0000\u0000\u0705\u0173\u0001\u0000\u0000\u0000\u0706\u0707"+
		"\u0007\u0012\u0000\u0000\u0707\u0175\u0001\u0000\u0000\u0000\u0708\u0709"+
		"\u0005#\u0000\u0000\u0709\u070a\u0003\u0178\u00bc\u0000\u070a\u0177\u0001"+
		"\u0000\u0000\u0000\u070b\u070c\u0007\f\u0000\u0000\u070c\u0179\u0001\u0000"+
		"\u0000\u0000\u070d\u070e\u0005\u001f\u0000\u0000\u070e\u0711\u0003\u0130"+
		"\u0098\u0000\u070f\u0710\u0005%\u0000\u0000\u0710\u0712\u0003\u0130\u0098"+
		"\u0000\u0711\u070f\u0001\u0000\u0000\u0000\u0711\u0712\u0001\u0000\u0000"+
		"\u0000\u0712\u017b\u0001\u0000\u0000\u0000\u0713\u0714\u0005\u0012\u0000"+
		"\u0000\u0714\u0717\u0005?\u0000\u0000\u0715\u0718\u0003\u017e\u00bf\u0000"+
		"\u0716\u0718\u0003\u0082A\u0000\u0717\u0715\u0001\u0000\u0000\u0000\u0717"+
		"\u0716\u0001\u0000\u0000\u0000\u0718\u017d\u0001\u0000\u0000\u0000\u0719"+
		"\u071e\u0003\u0180\u00c0\u0000\u071a\u071b\u0005\u0116\u0000\u0000\u071b"+
		"\u071d\u0003\u0180\u00c0\u0000\u071c\u071a\u0001\u0000\u0000\u0000\u071d"+
		"\u0720\u0001\u0000\u0000\u0000\u071e\u071c\u0001\u0000\u0000\u0000\u071e"+
		"\u071f\u0001\u0000\u0000\u0000\u071f\u017f\u0001\u0000\u0000\u0000\u0720"+
		"\u071e\u0001\u0000\u0000\u0000\u0721\u0726\u0003\u0186\u00c3\u0000\u0722"+
		"\u0726\u0003\u0188\u00c4\u0000\u0723\u0726\u0003\u018a\u00c5\u0000\u0724"+
		"\u0726\u0003\u0184\u00c2\u0000\u0725\u0721\u0001\u0000\u0000\u0000\u0725"+
		"\u0722\u0001\u0000\u0000\u0000\u0725\u0723\u0001\u0000\u0000\u0000\u0725"+
		"\u0724\u0001\u0000\u0000\u0000\u0726\u0181\u0001\u0000\u0000\u0000\u0727"+
		"\u072c\u0003\u0184\u00c2\u0000\u0728\u0729\u0005\u0116\u0000\u0000\u0729"+
		"\u072b\u0003\u0184\u00c2\u0000\u072a\u0728\u0001\u0000\u0000\u0000\u072b"+
		"\u072e\u0001\u0000\u0000\u0000\u072c\u072a\u0001\u0000\u0000\u0000\u072c"+
		"\u072d\u0001\u0000\u0000\u0000\u072d\u0183\u0001\u0000\u0000\u0000\u072e"+
		"\u072c\u0001\u0000\u0000\u0000\u072f\u0735\u0003\u0168\u00b4\u0000\u0730"+
		"\u0731\u0005\u011e\u0000\u0000\u0731\u0732\u0003\u0190\u00c8\u0000\u0732"+
		"\u0733\u0005\u011f\u0000\u0000\u0733\u0735\u0001\u0000\u0000\u0000\u0734"+
		"\u072f\u0001\u0000\u0000\u0000\u0734\u0730\u0001\u0000\u0000\u0000\u0735"+
		"\u0185\u0001\u0000\u0000\u0000\u0736\u0737\u0005\u0082\u0000\u0000\u0737"+
		"\u0738\u0005\u011e\u0000\u0000\u0738\u0739\u0003\u0182\u00c1\u0000\u0739"+
		"\u073a\u0005\u011f\u0000\u0000\u073a\u0187\u0001\u0000\u0000\u0000\u073b"+
		"\u073c\u0005G\u0000\u0000\u073c\u073d\u0005\u011e\u0000\u0000\u073d\u073e"+
		"\u0003\u0182\u00c1\u0000\u073e\u073f\u0005\u011f\u0000\u0000\u073f\u0189"+
		"\u0001\u0000\u0000\u0000\u0740\u0741\u0005\u011e\u0000\u0000\u0741\u0742"+
		"\u0005\u011f\u0000\u0000\u0742\u018b\u0001\u0000\u0000\u0000\u0743\u0744"+
		"\u0005\u0013\u0000\u0000\u0744\u0745\u0003\u0156\u00ab\u0000\u0745\u018d"+
		"\u0001\u0000\u0000\u0000\u0746\u0747\u0005\u0149\u0000\u0000\u0747\u0748"+
		"\u0003\u016c\u00b6\u0000\u0748\u018f\u0001\u0000\u0000\u0000\u0749\u074e"+
		"\u0003\u0168\u00b4\u0000\u074a\u074b\u0005\u0116\u0000\u0000\u074b\u074d"+
		"\u0003\u0168\u00b4\u0000\u074c\u074a\u0001\u0000\u0000\u0000\u074d\u0750"+
		"\u0001\u0000\u0000\u0000\u074e\u074c\u0001\u0000\u0000\u0000\u074e\u074f"+
		"\u0001\u0000\u0000\u0000\u074f\u0191\u0001\u0000\u0000\u0000\u0750\u074e"+
		"\u0001\u0000\u0000\u0000\u0751\u0752\u0003\u0168\u00b4\u0000\u0752\u0753"+
		"\u0003\u0194\u00ca\u0000\u0753\u0754\u0003\u0168\u00b4\u0000\u0754\u0193"+
		"\u0001\u0000\u0000\u0000\u0755\u075c\u0003\u019a\u00cd\u0000\u0756\u0758"+
		"\u0003\u01cc\u00e6\u0000\u0757\u0756\u0001\u0000\u0000\u0000\u0757\u0758"+
		"\u0001\u0000\u0000\u0000\u0758\u0759\u0001\u0000\u0000\u0000\u0759\u075c"+
		"\u0003\u0196\u00cb\u0000\u075a\u075c\u0003\u0198\u00cc\u0000\u075b\u0755"+
		"\u0001\u0000\u0000\u0000\u075b\u0757\u0001\u0000\u0000\u0000\u075b\u075a"+
		"\u0001\u0000\u0000\u0000\u075c\u0195\u0001\u0000\u0000\u0000\u075d\u0764"+
		"\u0005\u001e\u0000\u0000\u075e\u0764\u0005\u0015\u0000\u0000\u075f\u0760"+
		"\u0005\u0088\u0000\u0000\u0760\u0764\u0005\u0093\u0000\u0000\u0761\u0764"+
		"\u0005\u0080\u0000\u0000\u0762\u0764\u0005\u0081\u0000\u0000\u0763\u075d"+
		"\u0001\u0000\u0000\u0000\u0763\u075e\u0001\u0000\u0000\u0000\u0763\u075f"+
		"\u0001\u0000\u0000\u0000\u0763\u0761\u0001\u0000\u0000\u0000\u0763\u0762"+
		"\u0001\u0000\u0000\u0000\u0764\u0197\u0001\u0000\u0000\u0000\u0765\u0766"+
		"\u0007\u0013\u0000\u0000\u0766\u0199\u0001\u0000\u0000\u0000\u0767\u0768"+
		"\u0007\u0014\u0000\u0000\u0768\u019b\u0001\u0000\u0000\u0000\u0769\u076b"+
		"\u0003\u0168\u00b4\u0000\u076a\u076c\u0003\u01cc\u00e6\u0000\u076b\u076a"+
		"\u0001\u0000\u0000\u0000\u076b\u076c\u0001\u0000\u0000\u0000\u076c\u076d"+
		"\u0001\u0000\u0000\u0000\u076d\u076f\u0005>\u0000\u0000\u076e\u0770\u0003"+
		"\u019e\u00cf\u0000\u076f\u076e\u0001\u0000\u0000\u0000\u076f\u0770\u0001"+
		"\u0000\u0000\u0000\u0770\u0771\u0001\u0000\u0000\u0000\u0771\u0772\u0003"+
		"\u0168\u00b4\u0000\u0772\u0773\u0005\u0003\u0000\u0000\u0773\u0774\u0003"+
		"\u0168\u00b4\u0000\u0774\u019d\u0001\u0000\u0000\u0000\u0775\u0776\u0007"+
		"\u0015\u0000\u0000\u0776\u019f\u0001\u0000\u0000\u0000\u0777\u0779\u0003"+
		"\u0168\u00b4\u0000\u0778\u077a\u0003\u01cc\u00e6\u0000\u0779\u0778\u0001"+
		"\u0000\u0000\u0000\u0779\u077a\u0001\u0000\u0000\u0000\u077a\u077b\u0001"+
		"\u0000\u0000\u0000\u077b\u077c\u0005\u0016\u0000\u0000\u077c\u077d\u0003"+
		"\u01a6\u00d3\u0000\u077d\u01a1\u0001\u0000\u0000\u0000\u077e\u0780\u0003"+
		"\u0168\u00b4\u0000\u077f\u0781\u0003\u01cc\u00e6\u0000\u0780\u077f\u0001"+
		"\u0000\u0000\u0000\u0780\u0781\u0001\u0000\u0000\u0000\u0781\u0782\u0001"+
		"\u0000\u0000\u0000\u0782\u0783\u0003\u01a4\u00d2\u0000\u0783\u0785\u0003"+
		"\u01a6\u00d3\u0000\u0784\u0786\u0003\u01aa\u00d5\u0000\u0785\u0784\u0001"+
		"\u0000\u0000\u0000\u0785\u0786\u0001\u0000\u0000\u0000\u0786\u01a3\u0001"+
		"\u0000\u0000\u0000\u0787\u0788\u0007\u0016\u0000\u0000\u0788\u0789\u0005"+
		"\u0004\u0000\u0000\u0789\u01a5\u0001\u0000\u0000\u0000\u078a\u0791\u0003"+
		"z=\u0000\u078b\u078c\u0005\u011e\u0000\u0000\u078c\u078d\u0003\u01a8\u00d4"+
		"\u0000\u078d\u078e\u0005\u011f\u0000\u0000\u078e\u0791\u0001\u0000\u0000"+
		"\u0000\u078f\u0791\u0003\u01ea\u00f5\u0000\u0790\u078a\u0001\u0000\u0000"+
		"\u0000\u0790\u078b\u0001\u0000\u0000\u0000\u0790\u078f\u0001\u0000\u0000"+
		"\u0000\u0791\u01a7\u0001\u0000\u0000\u0000\u0792\u0797\u0003\u0166\u00b3"+
		"\u0000\u0793\u0794\u0005\u0116\u0000\u0000\u0794\u0796\u0003\u0166\u00b3"+
		"\u0000\u0795\u0793\u0001\u0000\u0000\u0000\u0796\u0799\u0001\u0000\u0000"+
		"\u0000\u0797\u0795\u0001\u0000\u0000\u0000\u0797\u0798\u0001\u0000\u0000"+
		"\u0000\u0798\u01a9\u0001\u0000\u0000\u0000\u0799\u0797\u0001\u0000\u0000"+
		"\u0000\u079a\u079b\u0005Q\u0000\u0000\u079b\u079c\u0005\u017d\u0000\u0000"+
		"\u079c\u01ab\u0001\u0000\u0000\u0000\u079d\u07a1\u0003\u01ae\u00d7\u0000"+
		"\u079e\u07a1\u0003\u01b0\u00d8\u0000\u079f\u07a1\u0003\u01b2\u00d9\u0000"+
		"\u07a0\u079d\u0001\u0000\u0000\u0000\u07a0\u079e\u0001\u0000\u0000\u0000"+
		"\u07a0\u079f\u0001\u0000\u0000\u0000\u07a1\u01ad\u0001\u0000\u0000\u0000"+
		"\u07a2\u07a3\u0003\u01b2\u00d9\u0000\u07a3\u07a4\u0003\u0086C\u0000\u07a4"+
		"\u07a5\u0003\u01b8\u00dc\u0000\u07a5\u01af\u0001\u0000\u0000\u0000\u07a6"+
		"\u07a7\u0003\u01b2\u00d9\u0000\u07a7\u07a8\u0003\u0086C\u0000\u07a8\u01b1"+
		"\u0001\u0000\u0000\u0000\u07a9\u07aa\u0005\u011e\u0000\u0000\u07aa\u07ab"+
		"\u0005\u0097\u0000\u0000\u07ab\u07ac\u0003\u01b4\u00da\u0000\u07ac\u07ad"+
		"\u0005\u011f\u0000\u0000\u07ad\u01b3\u0001\u0000\u0000\u0000\u07ae\u07b3"+
		"\u0003\u01b6\u00db\u0000\u07af\u07b0\u0005\u0116\u0000\u0000\u07b0\u07b2"+
		"\u0003\u01b6\u00db\u0000\u07b1\u07af\u0001\u0000\u0000\u0000\u07b2\u07b5"+
		"\u0001\u0000\u0000\u0000\u07b3\u07b1\u0001\u0000\u0000\u0000\u07b3\u07b4"+
		"\u0001\u0000\u0000\u0000\u07b4\u01b5\u0001\u0000\u0000\u0000\u07b5\u07b3"+
		"\u0001\u0000\u0000\u0000\u07b6\u07b7\u0005\u011e\u0000\u0000\u07b7\u07b8"+
		"\u0003\u01a8\u00d4\u0000\u07b8\u07b9\u0005\u011f\u0000\u0000\u07b9\u01b7"+
		"\u0001\u0000\u0000\u0000\u07ba\u07bb\u0005\u011e\u0000\u0000\u07bb\u07bc"+
		"\u0003\u01ba\u00dd\u0000\u07bc\u07bd\u0005\u011f\u0000\u0000\u07bd\u01b9"+
		"\u0001\u0000\u0000\u0000\u07be\u07c3\u0003\u01e8\u00f4\u0000\u07bf\u07c0"+
		"\u0005\u0116\u0000\u0000\u07c0\u07c2\u0003\u01e8\u00f4\u0000\u07c1\u07bf"+
		"\u0001\u0000\u0000\u0000\u07c2\u07c5\u0001\u0000\u0000\u0000\u07c3\u07c1"+
		"\u0001\u0000\u0000\u0000\u07c3\u07c4\u0001\u0000\u0000\u0000\u07c4\u01bb"+
		"\u0001\u0000\u0000\u0000\u07c5\u07c3\u0001\u0000\u0000\u0000\u07c6\u07c7"+
		"\u0005\u0097\u0000\u0000\u07c7\u07c8\u0003\u01b4\u00da\u0000\u07c8\u01bd"+
		"\u0001\u0000\u0000\u0000\u07c9\u07ca\u0003\u01c0\u00e0\u0000\u07ca\u07cb"+
		"\u0003\u01c2\u00e1\u0000\u07cb\u01bf\u0001\u0000\u0000\u0000\u07cc\u07cd"+
		"\u0005S\u0000\u0000\u07cd\u01c1\u0001\u0000\u0000\u0000\u07ce\u07d1\u0003"+
		"z=\u0000\u07cf\u07d1\u0003\u01ea\u00f5\u0000\u07d0\u07ce\u0001\u0000\u0000"+
		"\u0000\u07d0\u07cf\u0001\u0000\u0000\u0000\u07d1\u01c3\u0001\u0000\u0000"+
		"\u0000\u07d2\u07d3\u0003\u0168\u00b4\u0000\u07d3\u07d4\u0003\u01c6\u00e3"+
		"\u0000\u07d4\u01c5\u0001\u0000\u0000\u0000\u07d5\u07d7\u0005\u001a\u0000"+
		"\u0000\u07d6\u07d8\u0005!\u0000\u0000\u07d7\u07d6\u0001\u0000\u0000\u0000"+
		"\u07d7\u07d8\u0001\u0000\u0000\u0000\u07d8\u07d9\u0001\u0000\u0000\u0000"+
		"\u07d9\u07da\u0005\"\u0000\u0000\u07da\u01c7\u0001\u0000\u0000\u0000\u07db"+
		"\u07dd\u0005\u001a\u0000\u0000\u07dc\u07de\u0003\u01cc\u00e6\u0000\u07dd"+
		"\u07dc\u0001\u0000\u0000\u0000\u07dd\u07de\u0001\u0000\u0000\u0000\u07de"+
		"\u07df\u0001\u0000\u0000\u0000\u07df\u07e0\u0003\u01ca\u00e5\u0000\u07e0"+
		"\u01c9\u0001\u0000\u0000\u0000\u07e1\u07e2\u0007\u0017\u0000\u0000\u07e2"+
		"\u01cb\u0001\u0000\u0000\u0000\u07e3\u07e4\u0005!\u0000\u0000\u07e4\u01cd"+
		"\u0001\u0000\u0000\u0000\u07e5\u07e6\u0003\u0130\u0098\u0000\u07e6\u07e7"+
		"\u0003\u019a\u00cd\u0000\u07e7\u07e8\u0003\u01d0\u00e8\u0000\u07e8\u07e9"+
		"\u0003z=\u0000\u07e9\u01cf\u0001\u0000\u0000\u0000\u07ea\u07ed\u0003\u01d2"+
		"\u00e9\u0000\u07eb\u07ed\u0003\u01d4\u00ea\u0000\u07ec\u07ea\u0001\u0000"+
		"\u0000\u0000\u07ec\u07eb\u0001\u0000\u0000\u0000\u07ed\u01d1\u0001\u0000"+
		"\u0000\u0000\u07ee\u07ef\u0005\u0002\u0000\u0000\u07ef\u01d3\u0001\u0000"+
		"\u0000\u0000\u07f0\u07f1\u0007\u0018\u0000\u0000\u07f1\u01d5\u0001\u0000"+
		"\u0000\u0000\u07f2\u07f3\u00056\u0000\u0000\u07f3\u07f4\u0003z=\u0000"+
		"\u07f4\u01d7\u0001\u0000\u0000\u0000\u07f5\u07f8\u0003\u01da\u00ed\u0000"+
		"\u07f6\u07f8\u0005\u0086\u0000\u0000\u07f7\u07f5\u0001\u0000\u0000\u0000"+
		"\u07f7\u07f6\u0001\u0000\u0000\u0000\u07f8\u01d9\u0001\u0000\u0000\u0000"+
		"\u07f9\u07fa\u0007\u0019\u0000\u0000\u07fa\u01db\u0001\u0000\u0000\u0000"+
		"\u07fb\u07fc\u0007\u001a\u0000\u0000\u07fc\u01dd\u0001\u0000\u0000\u0000"+
		"\u07fd\u07fe\u0003\u01e0\u00f0\u0000\u07fe\u0800\u0005\u011e\u0000\u0000"+
		"\u07ff\u0801\u0003\u01e4\u00f2\u0000\u0800\u07ff\u0001\u0000\u0000\u0000"+
		"\u0800\u0801\u0001\u0000\u0000\u0000\u0801\u0802\u0001\u0000\u0000\u0000"+
		"\u0802\u0803\u0005\u011f\u0000\u0000\u0803\u01df\u0001\u0000\u0000\u0000"+
		"\u0804\u0807\u0003\u01e6\u00f3\u0000\u0805\u0806\u0005\u0125\u0000\u0000"+
		"\u0806\u0808\u0003\u01e6\u00f3\u0000\u0807\u0805\u0001\u0000\u0000\u0000"+
		"\u0807\u0808\u0001\u0000\u0000\u0000\u0808\u080b\u0001\u0000\u0000\u0000"+
		"\u0809\u080b\u0003\u01e2\u00f1\u0000\u080a\u0804\u0001\u0000\u0000\u0000"+
		"\u080a\u0809\u0001\u0000\u0000\u0000\u080b\u01e1\u0001\u0000\u0000\u0000"+
		"\u080c\u080d\u0007\u001b\u0000\u0000\u080d\u01e3\u0001\u0000\u0000\u0000"+
		"\u080e\u0813\u0003\u012c\u0096\u0000\u080f\u0810\u0005\u0116\u0000\u0000"+
		"\u0810\u0812\u0003\u012c\u0096\u0000\u0811\u080f\u0001\u0000\u0000\u0000"+
		"\u0812\u0815\u0001\u0000\u0000\u0000\u0813\u0811\u0001\u0000\u0000\u0000"+
		"\u0813\u0814\u0001\u0000\u0000\u0000\u0814\u01e5\u0001\u0000\u0000\u0000"+
		"\u0815\u0813\u0001\u0000\u0000\u0000\u0816\u081c\u0003\u01ec\u00f6\u0000"+
		"\u0817\u081c\u0003\u01ee\u00f7\u0000\u0818\u081c\u0003\u0206\u0103\u0000"+
		"\u0819\u081c\u0003\u0202\u0101\u0000\u081a\u081c\u0003\u0204\u0102\u0000"+
		"\u081b\u0816\u0001\u0000\u0000\u0000\u081b\u0817\u0001\u0000\u0000\u0000"+
		"\u081b\u0818\u0001\u0000\u0000\u0000\u081b\u0819\u0001\u0000\u0000\u0000"+
		"\u081b\u081a\u0001\u0000\u0000\u0000\u081c\u01e7\u0001\u0000\u0000\u0000"+
		"\u081d\u0823\u0003\u01ec\u00f6\u0000\u081e\u0823\u0003\u01ee\u00f7\u0000"+
		"\u081f\u0823\u0003\u0206\u0103\u0000\u0820\u0823\u0003\u0200\u0100\u0000"+
		"\u0821\u0823\u0003\u0202\u0101\u0000\u0822\u081d\u0001\u0000\u0000\u0000"+
		"\u0822\u081e\u0001\u0000\u0000\u0000\u0822\u081f\u0001\u0000\u0000\u0000"+
		"\u0822\u0820\u0001\u0000\u0000\u0000\u0822\u0821\u0001\u0000\u0000\u0000"+
		"\u0823\u01e9\u0001\u0000\u0000\u0000\u0824\u0827\u0003\u01f0\u00f8\u0000"+
		"\u0825\u0827\u0003\u01f2\u00f9\u0000\u0826\u0824\u0001\u0000\u0000\u0000"+
		"\u0826\u0825\u0001\u0000\u0000\u0000\u0827\u01eb\u0001\u0000\u0000\u0000"+
		"\u0828\u0829\u0005\u0175\u0000\u0000\u0829\u01ed\u0001\u0000\u0000\u0000"+
		"\u082a\u082b\u0005\u0145\u0000\u0000\u082b\u01ef\u0001\u0000\u0000\u0000"+
		"\u082c\u082d\u0005\u0146\u0000\u0000\u082d\u01f1\u0001\u0000\u0000\u0000"+
		"\u082e\u082f\u0007\u001c\u0000\u0000\u082f\u01f3\u0001\u0000\u0000\u0000"+
		"\u0830\u0831\u0005\u0180\u0000\u0000\u0831\u0832\u0003\u01f6\u00fb\u0000"+
		"\u0832\u0833\u0005\u0181\u0000\u0000\u0833\u0839\u0001\u0000\u0000\u0000"+
		"\u0834\u0835\u0005\u0180\u0000\u0000\u0835\u0836\u0003\u01fc\u00fe\u0000"+
		"\u0836\u0837\u0005\u0181\u0000\u0000\u0837\u0839\u0001\u0000\u0000\u0000"+
		"\u0838\u0830\u0001\u0000\u0000\u0000\u0838\u0834\u0001\u0000\u0000\u0000"+
		"\u0839\u01f5\u0001\u0000\u0000\u0000\u083a\u083b\u0003\u01e6\u00f3\u0000"+
		"\u083b\u083c\u0004\u00fb\u0005\u0001\u083c\u083e\u0005\u011e\u0000\u0000"+
		"\u083d\u083f\u0003\u01f8\u00fc\u0000\u083e\u083d\u0001\u0000\u0000\u0000"+
		"\u083e\u083f\u0001\u0000\u0000\u0000\u083f\u0840\u0001\u0000\u0000\u0000"+
		"\u0840\u0841\u0005\u011f\u0000\u0000\u0841\u084d\u0001\u0000\u0000\u0000"+
		"\u0842\u0843\u0003\u01e6\u00f3\u0000\u0843\u0844\u0005\u0125\u0000\u0000"+
		"\u0844\u0845\u0003\u01e6\u00f3\u0000\u0845\u0846\u0004\u00fb\u0006\u0001"+
		"\u0846\u0848\u0005\u011e\u0000\u0000\u0847\u0849\u0003\u01f8\u00fc\u0000"+
		"\u0848\u0847\u0001\u0000\u0000\u0000\u0848\u0849\u0001\u0000\u0000\u0000"+
		"\u0849\u084a\u0001\u0000\u0000\u0000\u084a\u084b\u0005\u011f\u0000\u0000"+
		"\u084b\u084d\u0001\u0000\u0000\u0000\u084c\u083a\u0001\u0000\u0000\u0000"+
		"\u084c\u0842\u0001\u0000\u0000\u0000\u084d\u01f7\u0001\u0000\u0000\u0000"+
		"\u084e\u0853\u0003\u01fa\u00fd\u0000\u084f\u0850\u0005\u0116\u0000\u0000"+
		"\u0850\u0852\u0003\u01fa\u00fd\u0000\u0851\u084f\u0001\u0000\u0000\u0000"+
		"\u0852\u0855\u0001\u0000\u0000\u0000\u0853\u0851\u0001\u0000\u0000\u0000"+
		"\u0853\u0854\u0001\u0000\u0000\u0000\u0854\u01f9\u0001\u0000\u0000\u0000"+
		"\u0855\u0853\u0001\u0000\u0000\u0000\u0856\u0861\u0005\u017d\u0000\u0000"+
		"\u0857\u0861\u0005\u012b\u0000\u0000\u0858\u0859\u0003\u01e6\u00f3\u0000"+
		"\u0859\u085a\u0005\u0113\u0000\u0000\u085a\u085b\u0005\u017d\u0000\u0000"+
		"\u085b\u0861\u0001\u0000\u0000\u0000\u085c\u085d\u0003\u01e6\u00f3\u0000"+
		"\u085d\u085e\u0005\u0113\u0000\u0000\u085e\u085f\u0005\u012b\u0000\u0000"+
		"\u085f\u0861\u0001\u0000\u0000\u0000\u0860\u0856\u0001\u0000\u0000\u0000"+
		"\u0860\u0857\u0001\u0000\u0000\u0000\u0860\u0858\u0001\u0000\u0000\u0000"+
		"\u0860\u085c\u0001\u0000\u0000\u0000\u0861\u01fb\u0001\u0000\u0000\u0000"+
		"\u0862\u0867\u0003\u01fe\u00ff\u0000\u0863\u0864\u0005\u0125\u0000\u0000"+
		"\u0864\u0866\u0003\u01fe\u00ff\u0000\u0865\u0863\u0001\u0000\u0000\u0000"+
		"\u0866\u0869\u0001\u0000\u0000\u0000\u0867\u0865\u0001\u0000\u0000\u0000"+
		"\u0867\u0868\u0001\u0000\u0000\u0000\u0868\u01fd\u0001\u0000\u0000\u0000"+
		"\u0869\u0867\u0001\u0000\u0000\u0000\u086a\u086d\u0003\u01e6\u00f3\u0000"+
		"\u086b\u086d\u0005\u012b\u0000\u0000\u086c\u086a\u0001\u0000\u0000\u0000"+
		"\u086c\u086b\u0001\u0000\u0000\u0000\u086d\u01ff\u0001\u0000\u0000\u0000"+
		"\u086e\u086f\u0007\u001d\u0000\u0000\u086f\u0201\u0001\u0000\u0000\u0000"+
		"\u0870\u0871\u0005\u0179\u0000\u0000\u0871\u0203\u0001\u0000\u0000\u0000"+
		"\u0872\u0873\u0005\u017a\u0000\u0000\u0873\u0205\u0001\u0000\u0000\u0000"+
		"\u0874\u0875\u0007\u001e\u0000\u0000\u0875\u0207\u0001\u0000\u0000\u0000"+
		"\u0876\u0878\u0003\u0138\u009c\u0000\u0877\u0876\u0001\u0000\u0000\u0000"+
		"\u0877\u0878\u0001\u0000\u0000\u0000\u0878\u0879\u0001\u0000\u0000\u0000"+
		"\u0879\u087a\u0003\u020c\u0106\u0000\u087a\u0209\u0001\u0000\u0000\u0000"+
		"\u087b\u087e\u0003\u020c\u0106\u0000\u087c\u087e\u0003\u0212\u0109\u0000"+
		"\u087d\u087b\u0001\u0000\u0000\u0000\u087d\u087c\u0001\u0000\u0000\u0000"+
		"\u087e\u020b\u0001\u0000\u0000\u0000\u087f\u0882\u0005\u012b\u0000\u0000"+
		"\u0880\u0882\u0003\u020e\u0107\u0000\u0881\u087f\u0001\u0000\u0000\u0000"+
		"\u0881\u0880\u0001\u0000\u0000\u0000\u0882\u020d\u0001\u0000\u0000\u0000"+
		"\u0883\u0884\u0005\u012b\u0000\u0000\u0884\u0886\u0005\u0125\u0000\u0000"+
		"\u0885\u0887\u0005\u012b\u0000\u0000\u0886\u0885\u0001\u0000\u0000\u0000"+
		"\u0886\u0887\u0001\u0000\u0000\u0000\u0887\u0889\u0001\u0000\u0000\u0000"+
		"\u0888\u088a\u0003\u0210\u0108\u0000\u0889\u0888\u0001\u0000\u0000\u0000"+
		"\u0889\u088a\u0001\u0000\u0000\u0000\u088a\u0894\u0001\u0000\u0000\u0000"+
		"\u088b\u088c\u0005\u0125\u0000\u0000\u088c\u088e\u0005\u012b\u0000\u0000"+
		"\u088d\u088f\u0003\u0210\u0108\u0000\u088e\u088d\u0001\u0000\u0000\u0000"+
		"\u088e\u088f\u0001\u0000\u0000\u0000\u088f\u0894\u0001\u0000\u0000\u0000"+
		"\u0890\u0891\u0005\u012b\u0000\u0000\u0891\u0894\u0003\u0210\u0108\u0000"+
		"\u0892\u0894\u0005\u0177\u0000\u0000\u0893\u0883\u0001\u0000\u0000\u0000"+
		"\u0893\u088b\u0001\u0000\u0000\u0000\u0893\u0890\u0001\u0000\u0000\u0000"+
		"\u0893\u0892\u0001\u0000\u0000\u0000\u0894\u020f\u0001\u0000\u0000\u0000"+
		"\u0895\u0896\u0005\u0176\u0000\u0000\u0896\u0897\u0005\u012b\u0000\u0000"+
		"\u0897\u0211\u0001\u0000\u0000\u0000\u0898\u089c\u0003\u0214\u010a\u0000"+
		"\u0899\u089c\u0003\u0216\u010b\u0000\u089a\u089c\u0003\u021e\u010f\u0000"+
		"\u089b\u0898\u0001\u0000\u0000\u0000\u089b\u0899\u0001\u0000\u0000\u0000"+
		"\u089b\u089a\u0001\u0000\u0000\u0000\u089c\u0213\u0001\u0000\u0000\u0000"+
		"\u089d\u089e\u0005\u017d\u0000\u0000\u089e\u0215\u0001\u0000\u0000\u0000"+
		"\u089f\u08a3\u0003\u021a\u010d\u0000\u08a0\u08a3\u0003\u0218\u010c\u0000"+
		"\u08a1\u08a3\u0003\u021c\u010e\u0000\u08a2\u089f\u0001\u0000\u0000\u0000"+
		"\u08a2\u08a0\u0001\u0000\u0000\u0000\u08a2\u08a1\u0001\u0000\u0000\u0000"+
		"\u08a3\u0217\u0001\u0000\u0000\u0000\u08a4\u08a5\u0005\u00ff\u0000\u0000"+
		"\u08a5\u08a6\u0005\u017d\u0000\u0000\u08a6\u0219\u0001\u0000\u0000\u0000"+
		"\u08a7\u08a8\u0005\u0101\u0000\u0000\u08a8\u08a9\u0005\u017d\u0000\u0000"+
		"\u08a9\u021b\u0001\u0000\u0000\u0000\u08aa\u08ab\u0005\u00fd\u0000\u0000"+
		"\u08ab\u08ac\u0005\u017d\u0000\u0000\u08ac\u021d\u0001\u0000\u0000\u0000"+
		"\u08ad\u08ae\u0007\u0017\u0000\u0000\u08ae\u021f\u0001\u0000\u0000\u0000"+
		"\u08af\u08b3\u0003\u0222\u0111\u0000\u08b0\u08b3\u0003\u0228\u0114\u0000"+
		"\u08b1\u08b3\u0003\u022e\u0117\u0000\u08b2\u08af\u0001\u0000\u0000\u0000"+
		"\u08b2\u08b0\u0001\u0000\u0000\u0000\u08b2\u08b1\u0001\u0000\u0000\u0000"+
		"\u08b3\u0221\u0001\u0000\u0000\u0000\u08b4\u08b6\u0003\u0224\u0112\u0000"+
		"\u08b5\u08b7\u0003\u0226\u0113\u0000\u08b6\u08b5\u0001\u0000\u0000\u0000"+
		"\u08b6\u08b7\u0001\u0000\u0000\u0000\u08b7\u0223\u0001\u0000\u0000\u0000"+
		"\u08b8\u08db\u0005A\u0000\u0000\u08b9\u08db\u0005\u00f7\u0000\u0000\u08ba"+
		"\u08bb\u0005A\u0000\u0000\u08bb\u08db\u0005\u009a\u0000\u0000\u08bc\u08bd"+
		"\u0005\u00f7\u0000\u0000\u08bd\u08db\u0005\u009a\u0000\u0000\u08be\u08db"+
		"\u0005\u00f8\u0000\u0000\u08bf\u08db\u0005\u00f9\u0000\u0000\u08c0\u08c1"+
		"\u0005s\u0000\u0000\u08c1\u08db\u0005A\u0000\u0000\u08c2\u08c3\u0005s"+
		"\u0000\u0000\u08c3\u08db\u0005\u00f7\u0000\u0000\u08c4\u08db\u0005\u00fa"+
		"\u0000\u0000\u08c5\u08c6\u0005s\u0000\u0000\u08c6\u08c7\u0005A\u0000\u0000"+
		"\u08c7\u08db\u0005\u009a\u0000\u0000\u08c8\u08c9\u0005s\u0000\u0000\u08c9"+
		"\u08ca\u0005\u00f7\u0000\u0000\u08ca\u08db\u0005\u009a\u0000\u0000\u08cb"+
		"\u08cc\u0005\u00fa\u0000\u0000\u08cc\u08db\u0005\u009a\u0000\u0000\u08cd"+
		"\u08db\u0005\u00fb\u0000\u0000\u08ce\u08db\u0005\u0109\u0000\u0000\u08cf"+
		"\u08db\u0005\u010a\u0000\u0000\u08d0\u08db\u0005\u00d3\u0000\u0000\u08d1"+
		"\u08db\u0005\u00d4\u0000\u0000\u08d2\u08d3\u0005\u00d3\u0000\u0000\u08d3"+
		"\u08db\u0005\u009a\u0000\u0000\u08d4\u08db\u0005\u0107\u0000\u0000\u08d5"+
		"\u08d6\u0005\u0107\u0000\u0000\u08d6\u08db\u0005\u009a\u0000\u0000\u08d7"+
		"\u08db\u0005\u0108\u0000\u0000\u08d8\u08db\u0005\u00d8\u0000\u0000\u08d9"+
		"\u08db\u0005\u00fc\u0000\u0000\u08da\u08b8\u0001\u0000\u0000\u0000\u08da"+
		"\u08b9\u0001\u0000\u0000\u0000\u08da\u08ba\u0001\u0000\u0000\u0000\u08da"+
		"\u08bc\u0001\u0000\u0000\u0000\u08da\u08be\u0001\u0000\u0000\u0000\u08da"+
		"\u08bf\u0001\u0000\u0000\u0000\u08da\u08c0\u0001\u0000\u0000\u0000\u08da"+
		"\u08c2\u0001\u0000\u0000\u0000\u08da\u08c4\u0001\u0000\u0000\u0000\u08da"+
		"\u08c5\u0001\u0000\u0000\u0000\u08da\u08c8\u0001\u0000\u0000\u0000\u08da"+
		"\u08cb\u0001\u0000\u0000\u0000\u08da\u08cd\u0001\u0000\u0000\u0000\u08da"+
		"\u08ce\u0001\u0000\u0000\u0000\u08da\u08cf\u0001\u0000\u0000\u0000\u08da"+
		"\u08d0\u0001\u0000\u0000\u0000\u08da\u08d1\u0001\u0000\u0000\u0000\u08da"+
		"\u08d2\u0001\u0000\u0000\u0000\u08da\u08d4\u0001\u0000\u0000\u0000\u08da"+
		"\u08d5\u0001\u0000\u0000\u0000\u08da\u08d7\u0001\u0000\u0000\u0000\u08da"+
		"\u08d8\u0001\u0000\u0000\u0000\u08da\u08d9\u0001\u0000\u0000\u0000\u08db"+
		"\u0225\u0001\u0000\u0000\u0000\u08dc\u08dd\u0005\u011e\u0000\u0000\u08dd"+
		"\u08de\u0005\u012b\u0000\u0000\u08de\u08df\u0005\u011f\u0000\u0000\u08df"+
		"\u0227\u0001\u0000\u0000\u0000\u08e0\u08e2\u0003\u022a\u0115\u0000\u08e1"+
		"\u08e3\u0003\u022c\u0116\u0000\u08e2\u08e1\u0001\u0000\u0000\u0000\u08e2"+
		"\u08e3\u0001\u0000\u0000\u0000\u08e3\u0229\u0001\u0000\u0000\u0000\u08e4"+
		"\u08f0\u0005\u00f5\u0000\u0000\u08e5\u08f0\u0005\u012b\u0000\u0000\u08e6"+
		"\u08f0\u0005\u00f6\u0000\u0000\u08e7\u08f0\u0005J\u0000\u0000\u08e8\u08f0"+
		"\u0005\u00f3\u0000\u0000\u08e9\u08f0\u0005\u00f4\u0000\u0000\u08ea\u08eb"+
		"\u0005\u00f4\u0000\u0000\u08eb\u08f0\u0005{\u0000\u0000\u08ec\u08f0\u0005"+
		"\u0102\u0000\u0000\u08ed\u08f0\u0005\u0103\u0000\u0000\u08ee\u08f0\u0005"+
		"\u0104\u0000\u0000\u08ef\u08e4\u0001\u0000\u0000\u0000\u08ef\u08e5\u0001"+
		"\u0000\u0000\u0000\u08ef\u08e6\u0001\u0000\u0000\u0000\u08ef\u08e7\u0001"+
		"\u0000\u0000\u0000\u08ef\u08e8\u0001\u0000\u0000\u0000\u08ef\u08e9\u0001"+
		"\u0000\u0000\u0000\u08ef\u08ea\u0001\u0000\u0000\u0000\u08ef\u08ec\u0001"+
		"\u0000\u0000\u0000\u08ef\u08ed\u0001\u0000\u0000\u0000\u08ef\u08ee\u0001"+
		"\u0000\u0000\u0000\u08f0\u022b\u0001\u0000\u0000\u0000\u08f1\u08f2\u0005"+
		"\u011e\u0000\u0000\u08f2\u08f3\u0005\u012b\u0000\u0000\u08f3\u08fa\u0005"+
		"\u011f\u0000\u0000\u08f4\u08f5\u0005\u011e\u0000\u0000\u08f5\u08f6\u0005"+
		"\u012b\u0000\u0000\u08f6\u08f7\u0005\u0116\u0000\u0000\u08f7\u08f8\u0005"+
		"\u012b\u0000\u0000\u08f8\u08fa\u0005\u011f\u0000\u0000\u08f9\u08f1\u0001"+
		"\u0000\u0000\u0000\u08f9\u08f4\u0001\u0000\u0000\u0000\u08fa\u022d\u0001"+
		"\u0000\u0000\u0000\u08fb\u08fc\u0003\u0230\u0118\u0000\u08fc\u022f\u0001"+
		"\u0000\u0000\u0000\u08fd\u093b\u0005\u0106\u0000\u0000\u08fe\u093b\u0005"+
		"\u00e0\u0000\u0000\u08ff\u093b\u0005\u00d7\u0000\u0000\u0900\u093b\u0005"+
		"\u00d6\u0000\u0000\u0901\u093b\u0005\u00d5\u0000\u0000\u0902\u093b\u0005"+
		"\u010c\u0000\u0000\u0903\u093b\u00055\u0000\u0000\u0904\u093b\u0005\u010d"+
		"\u0000\u0000\u0905\u093b\u0005\u010b\u0000\u0000\u0906\u093b\u0005\u00dd"+
		"\u0000\u0000\u0907\u093b\u0005\u00de\u0000\u0000\u0908\u093b\u0005\u00e1"+
		"\u0000\u0000\u0909\u093b\u0005\u00e5\u0000\u0000\u090a\u093b\u0005\u00e6"+
		"\u0000\u0000\u090b\u093b\u0005\u00e2\u0000\u0000\u090c\u093b\u0005\u00e3"+
		"\u0000\u0000\u090d\u093b\u0005\u00e4\u0000\u0000\u090e\u093b\u0005\u00df"+
		"\u0000\u0000\u090f\u093b\u0005\u00d9\u0000\u0000\u0910\u093b\u0005\u00e7"+
		"\u0000\u0000\u0911\u093b\u0005\u00da\u0000\u0000\u0912\u093b\u0005\u00e8"+
		"\u0000\u0000\u0913\u093b\u0005\u00db\u0000\u0000\u0914\u093b\u0005\u00e9"+
		"\u0000\u0000\u0915\u093b\u0005\u00ea\u0000\u0000\u0916\u093b\u0005\u00dc"+
		"\u0000\u0000\u0917\u093b\u0005\u00eb\u0000\u0000\u0918\u093b\u0005\u00ec"+
		"\u0000\u0000\u0919\u093b\u0005\u00ed\u0000\u0000\u091a\u093b\u0005\u00ee"+
		"\u0000\u0000\u091b\u093b\u0005\u00ef\u0000\u0000\u091c\u093b\u0005$\u0000"+
		"\u0000\u091d\u093b\u0005\u00f0\u0000\u0000\u091e\u093b\u0005\u00f2\u0000"+
		"\u0000\u091f\u093b\u0005\u00f1\u0000\u0000\u0920\u093b\u0005\u00d1\u0000"+
		"\u0000\u0921\u093b\u0005\u00d2\u0000\u0000\u0922\u093b\u0005\u00fd\u0000"+
		"\u0000\u0923\u093b\u0005\u00fe\u0000\u0000\u0924\u093b\u0005\u00ff\u0000"+
		"\u0000\u0925\u0926\u0005\u00ff\u0000\u0000\u0926\u0927\u0005:\u0000\u0000"+
		"\u0927\u0928\u0005\u00ff\u0000\u0000\u0928\u093b\u0005\u009d\u0000\u0000"+
		"\u0929\u093b\u0005\u0100\u0000\u0000\u092a\u093b\u0005\u0102\u0000\u0000"+
		"\u092b\u093b\u0005\u0103\u0000\u0000\u092c\u093b\u0005\u0104\u0000\u0000"+
		"\u092d\u093b\u0005\u0101\u0000\u0000\u092e\u092f\u0005\u0101\u0000\u0000"+
		"\u092f\u0930\u0005:\u0000\u0000\u0930\u0931\u0005\u00ff\u0000\u0000\u0931"+
		"\u093b\u0005\u009d\u0000\u0000\u0932\u0933\u0005\u0101\u0000\u0000\u0933"+
		"\u0934\u0005;\u0000\u0000\u0934\u0935\u0005\u00ff\u0000\u0000\u0935\u093b"+
		"\u0005\u009d\u0000\u0000\u0936\u093b\u0005\u0105\u0000\u0000\u0937\u093b"+
		"\u0005\u00ce\u0000\u0000\u0938\u093b\u0005\u00d0\u0000\u0000\u0939\u093b"+
		"\u0005\u00cf\u0000\u0000\u093a\u08fd\u0001\u0000\u0000\u0000\u093a\u08fe"+
		"\u0001\u0000\u0000\u0000\u093a\u08ff\u0001\u0000\u0000\u0000\u093a\u0900"+
		"\u0001\u0000\u0000\u0000\u093a\u0901\u0001\u0000\u0000\u0000\u093a\u0902"+
		"\u0001\u0000\u0000\u0000\u093a\u0903\u0001\u0000\u0000\u0000\u093a\u0904"+
		"\u0001\u0000\u0000\u0000\u093a\u0905\u0001\u0000\u0000\u0000\u093a\u0906"+
		"\u0001\u0000\u0000\u0000\u093a\u0907\u0001\u0000\u0000\u0000\u093a\u0908"+
		"\u0001\u0000\u0000\u0000\u093a\u0909\u0001\u0000\u0000\u0000\u093a\u090a"+
		"\u0001\u0000\u0000\u0000\u093a\u090b\u0001\u0000\u0000\u0000\u093a\u090c"+
		"\u0001\u0000\u0000\u0000\u093a\u090d\u0001\u0000\u0000\u0000\u093a\u090e"+
		"\u0001\u0000\u0000\u0000\u093a\u090f\u0001\u0000\u0000\u0000\u093a\u0910"+
		"\u0001\u0000\u0000\u0000\u093a\u0911\u0001\u0000\u0000\u0000\u093a\u0912"+
		"\u0001\u0000\u0000\u0000\u093a\u0913\u0001\u0000\u0000\u0000\u093a\u0914"+
		"\u0001\u0000\u0000\u0000\u093a\u0915\u0001\u0000\u0000\u0000\u093a\u0916"+
		"\u0001\u0000\u0000\u0000\u093a\u0917\u0001\u0000\u0000\u0000\u093a\u0918"+
		"\u0001\u0000\u0000\u0000\u093a\u0919\u0001\u0000\u0000\u0000\u093a\u091a"+
		"\u0001\u0000\u0000\u0000\u093a\u091b\u0001\u0000\u0000\u0000\u093a\u091c"+
		"\u0001\u0000\u0000\u0000\u093a\u091d\u0001\u0000\u0000\u0000\u093a\u091e"+
		"\u0001\u0000\u0000\u0000\u093a\u091f\u0001\u0000\u0000\u0000\u093a\u0920"+
		"\u0001\u0000\u0000\u0000\u093a\u0921\u0001\u0000\u0000\u0000\u093a\u0922"+
		"\u0001\u0000\u0000\u0000\u093a\u0923\u0001\u0000\u0000\u0000\u093a\u0924"+
		"\u0001\u0000\u0000\u0000\u093a\u0925\u0001\u0000\u0000\u0000\u093a\u0929"+
		"\u0001\u0000\u0000\u0000\u093a\u092a\u0001\u0000\u0000\u0000\u093a\u092b"+
		"\u0001\u0000\u0000\u0000\u093a\u092c\u0001\u0000\u0000\u0000\u093a\u092d"+
		"\u0001\u0000\u0000\u0000\u093a\u092e\u0001\u0000\u0000\u0000\u093a\u0932"+
		"\u0001\u0000\u0000\u0000\u093a\u0936\u0001\u0000\u0000\u0000\u093a\u0937"+
		"\u0001\u0000\u0000\u0000\u093a\u0938\u0001\u0000\u0000\u0000\u093a\u0939"+
		"\u0001\u0000\u0000\u0000\u093b\u0231\u0001\u0000\u0000\u0000\u093c\u093d"+
		"\u0007\u001f\u0000\u0000\u093d\u0233\u0001\u0000\u0000\u0000\u00de\u0237"+
		"\u023b\u0242\u0245\u024d\u0250\u0254\u0257\u0272\u028a\u0290\u0296\u02a5"+
		"\u02a8\u02aa\u02c7\u02d3\u02dd\u02ec\u02f2\u02f8\u02fe\u0304\u030b\u031d"+
		"\u0326\u032b\u0336\u0341\u0345\u034e\u0359\u035f\u0362\u0366\u036a\u0371"+
		"\u0378\u037b\u037e\u0388\u0397\u039c\u03a6\u03ab\u03b2\u03ba\u03bd\u03c2"+
		"\u03c5\u03c8\u03cb\u03ce\u03d1\u03d3\u03df\u03e4\u03e7\u03ea\u03f2\u03f9"+
		"\u0400\u0405\u040b\u040f\u0411\u0413\u0418\u041d\u0423\u0427\u0429\u042b"+
		"\u042f\u0435\u043c\u0440\u0445\u0447\u044d\u0455\u045b\u045d\u0462\u0464"+
		"\u046c\u046f\u0472\u0479\u047b\u047f\u0491\u0499\u04a5\u04bb\u04c0\u04c6"+
		"\u04d4\u04df\u04e8\u04f8\u0512\u0518\u0522\u0535\u053d\u0544\u054c\u0552"+
		"\u0559\u0560\u0565\u056c\u0574\u0580\u0585\u058a\u058c\u059b\u05a6\u05aa"+
		"\u05af\u05b9\u05c0\u05c4\u05c9\u05d3\u05df\u05f2\u05fb\u05fe\u0600\u0605"+
		"\u0608\u060b\u061b\u0625\u063e\u0643\u064a\u0652\u0656\u065c\u066a\u0670"+
		"\u0677\u067c\u0686\u0689\u068c\u0693\u06a1\u06a9\u06ad\u06bc\u06c4\u06c8"+
		"\u06d2\u06d4\u06d8\u06e1\u06e7\u06ec\u06fc\u0701\u0704\u0711\u0717\u071e"+
		"\u0725\u072c\u0734\u074e\u0757\u075b\u0763\u076b\u076f\u0779\u0780\u0785"+
		"\u0790\u0797\u07a0\u07b3\u07c3\u07d0\u07d7\u07dd\u07ec\u07f7\u0800\u0807"+
		"\u080a\u0813\u081b\u0822\u0826\u0838\u083e\u0848\u084c\u0853\u0860\u0867"+
		"\u086c\u0877\u087d\u0881\u0886\u0889\u088e\u0893\u089b\u08a2\u08b2\u08b6"+
		"\u08da\u08e2\u08ef\u08f9\u093a";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}