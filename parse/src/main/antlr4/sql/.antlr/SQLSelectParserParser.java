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
		QUALIFY=329, POSITION=330, CHARINDEX=331, INSTR=332, DELETE=333, TRUNCATE=334, 
		IFF=335, MD5=336, REVERSE=337, FLATTEN=338, SPLIT_TO_TABLE=339, STRTOK_SPLIT_TO_TABLE=340, 
		GENERATOR=341, INFER_SCHEMA=342, VALIDATE=343, RESULT_SCAN=344, QUERY_HISTORY=345, 
		QUERY_HSTORY=346, INPUT=347, PATH=348, RECURSIVE=349, MODE=350, LATERAL=351, 
		ROWCOUNT=352, TIMELIMIT=353, FILES=354, FILE_FORMAT=355, IGNORE_CASE=356, 
		MAX_FILE_COUNT=357, MAX_RECORDS_PER_FILE=358, KIND=359, JOB_ID=360, ALTER=361, 
		DATABASE=362, FILE=363, FUNCTION=364, MACRO=365, MATERIALIZED=366, PROCEDURE=367, 
		RETURNS=368, ROLE=369, SCHEMA=370, SEQUENCE=371, STAGE=372, USER=373, 
		VIEW=374, FOR=375, INCLUDE=376, EXCLUDE=377, UNPIVOT=378, PIVOT=379, Identifier=380, 
		EXPONEN=381, Scientific_Numeric_Literal=382, Numeric_Identifier=383, Double_Quoted_Numeric_Identifier=384, 
		Dollar_Sign_Identifier=385, BlockComment=386, LineComment=387, Character_String_Literal=388, 
		Space=389, White_Space=390, JINJA_OPEN=391, JINJA_CLOSE=392, BAD=393;
	public static final int
		RULE_script = 0, RULE_sql_statement = 1, RULE_ddl = 2, RULE_ddl_primary = 3, 
		RULE_sql = 4, RULE_column_value = 5, RULE_predicand_value = 6, RULE_in_list_predicate_value = 7, 
		RULE_condition_value = 8, RULE_tuple_value = 9, RULE_query_value = 10, 
		RULE_join_extension_value = 11, RULE_literal_value = 12, RULE_values_statement_end = 13, 
		RULE_insert_end_point = 14, RULE_update_end_point = 15, RULE_delete_end_point = 16, 
		RULE_truncate_end_point = 17, RULE_create_statement_primary = 18, RULE_drop_statement_primary = 19, 
		RULE_alter_statement_primary = 20, RULE_truncate_statement_primary = 21, 
		RULE_truncate_snowflake_expression = 22, RULE_truncate_postgres_expression = 23, 
		RULE_create_table_expression = 24, RULE_create_index_expression = 25, 
		RULE_create_view_expression = 26, RULE_create_materialized_view_expression = 27, 
		RULE_create_function_expression = 28, RULE_create_procedure_expression = 29, 
		RULE_create_macro_expression = 30, RULE_create_sequence_expression = 31, 
		RULE_create_schema_expression = 32, RULE_create_database_expression = 33, 
		RULE_create_role_expression = 34, RULE_create_user_expression = 35, RULE_create_stage_expression = 36, 
		RULE_create_file_format_expression = 37, RULE_ddl_object_type = 38, RULE_drop_options = 39, 
		RULE_alter_options = 40, RULE_generic_ddl_paren_content = 41, RULE_generic_ddl_options = 42, 
		RULE_with_query = 43, RULE_with_clause = 44, RULE_with_list_item = 45, 
		RULE_cte_body = 46, RULE_query_alias = 47, RULE_query = 48, RULE_insert_expression = 49, 
		RULE_snowflake_insert = 50, RULE_insert_target_table_primary = 51, RULE_postgres_insert = 52, 
		RULE_insert_preamble = 53, RULE_insert_source_primary = 54, RULE_update_expression = 55, 
		RULE_delete_expression = 56, RULE_delete_snowflake_expression = 57, RULE_delete_postgres_expression = 58, 
		RULE_delete_returning = 59, RULE_delete_using_clause = 60, RULE_returning = 61, 
		RULE_assignment_expression_list = 62, RULE_assignment_expression = 63, 
		RULE_query_expression = 64, RULE_intersected_query = 65, RULE_intersect_clause = 66, 
		RULE_intersect_operator = 67, RULE_unionized_query = 68, RULE_union_clause = 69, 
		RULE_union_operator = 70, RULE_query_primary = 71, RULE_subquery = 72, 
		RULE_query_specification = 73, RULE_into_list = 74, RULE_set_qualifier = 75, 
		RULE_select_list = 76, RULE_select_item = 77, RULE_as_clause = 78, RULE_select_all_columns = 79, 
		RULE_wildcard_reference = 80, RULE_from_clause = 81, RULE_join_extension = 82, 
		RULE_table_reference_list = 83, RULE_join_extension_primary = 84, RULE_lateral_modifier = 85, 
		RULE_table_primary = 86, RULE_table_source_primary = 87, RULE_relation_as_clause = 88, 
		RULE_tuple_primary = 89, RULE_tuple_source_primary = 90, RULE_db_object_name = 91, 
		RULE_unqualified_join = 92, RULE_qualified_join = 93, RULE_join_type = 94, 
		RULE_join_specification = 95, RULE_join_condition = 96, RULE_named_columns_join = 97, 
		RULE_using_term = 98, RULE_table_relational_modifier = 99, RULE_unpivot_clause = 100, 
		RULE_relational_modifier_list = 101, RULE_unpivot_null_policy = 102, RULE_relational_modifier_value_column = 103, 
		RULE_relational_modifier_name_column = 104, RULE_relational_modifier_in_item = 105, 
		RULE_relational_modifier_alias = 106, RULE_pivot_clause = 107, RULE_pivot_aggregate = 108, 
		RULE_table_function_primary = 109, RULE_table_function = 110, RULE_flatten_table_function = 111, 
		RULE_flatten_argument_list = 112, RULE_flatten_argument = 113, RULE_flatten_argument_value = 114, 
		RULE_flatten_function_name = 115, RULE_table_argument_literal = 116, RULE_table_argument_boolean = 117, 
		RULE_generator_table_function = 118, RULE_generator_argument_list = 119, 
		RULE_generator_argument = 120, RULE_generator_argument_value = 121, RULE_generator_function_name = 122, 
		RULE_result_scan_table_function = 123, RULE_result_scan_function_name = 124, 
		RULE_infer_schema_table_function = 125, RULE_infer_schema_argument_list = 126, 
		RULE_infer_schema_argument = 127, RULE_infer_schema_argument_value = 128, 
		RULE_infer_schema_function_name = 129, RULE_infer_schema_files_argument = 130, 
		RULE_validate_table_function = 131, RULE_validate_function_name = 132, 
		RULE_generic_table_function = 133, RULE_table_function_name = 134, RULE_table_function_argument_list = 135, 
		RULE_column_reference_list = 136, RULE_column_reference = 137, RULE_column_primary = 138, 
		RULE_predicand_primary = 139, RULE_value_expression_primary = 140, RULE_parenthesized_value_expression = 141, 
		RULE_nonparenthesized_value_expression_primary = 142, RULE_predicand_subquery = 143, 
		RULE_aggregate_function = 144, RULE_set_function_type = 145, RULE_set_qualifier_type = 146, 
		RULE_case_expression = 147, RULE_when_clause_list = 148, RULE_searched_when_clause = 149, 
		RULE_when_value_list = 150, RULE_when_value_clause = 151, RULE_else_clause = 152, 
		RULE_case_result = 153, RULE_null_literal = 154, RULE_cast_function_expression = 155, 
		RULE_cast_function_name = 156, RULE_window_over_partition_expression = 157, 
		RULE_window_function = 158, RULE_over_clause = 159, RULE_partition_by_clause = 160, 
		RULE_bracket_frame_clause = 161, RULE_rows_or_range = 162, RULE_bracket_frame_definition = 163, 
		RULE_between_frame_definition = 164, RULE_frame_edge = 165, RULE_preceding_frame_edge = 166, 
		RULE_following_frame_edge = 167, RULE_current_row_edge = 168, RULE_bracket_constraint = 169, 
		RULE_item_select_function = 170, RULE_select_direction = 171, RULE_null_handling = 172, 
		RULE_value_expression = 173, RULE_common_value_expression = 174, RULE_additive_expression = 175, 
		RULE_multiplicative_expression = 176, RULE_factor = 177, RULE_numeric_primary = 178, 
		RULE_sign = 179, RULE_extract_expression = 180, RULE_extract_field = 181, 
		RULE_time_zone_field = 182, RULE_extract_source = 183, RULE_string_value_expression = 184, 
		RULE_character_primary = 185, RULE_trim_function = 186, RULE_trim_function_name = 187, 
		RULE_trim_operands = 188, RULE_trim_specification = 189, RULE_position_function = 190, 
		RULE_position_function_name = 191, RULE_instr_function_name = 192, RULE_charindex_name = 193, 
		RULE_boolean_value_expression = 194, RULE_or_predicate = 195, RULE_and_predicate = 196, 
		RULE_negative_predicate = 197, RULE_parenthetical_predicate = 198, RULE_boolean_primary = 199, 
		RULE_predicate = 200, RULE_substitution_predicate = 201, RULE_row_value_expression = 202, 
		RULE_row_value_predicand = 203, RULE_where_clause = 204, RULE_search_condition = 205, 
		RULE_orderby_clause = 206, RULE_sort_specifier_list = 207, RULE_sort_specifier = 208, 
		RULE_order_specification = 209, RULE_null_ordering = 210, RULE_null_first_last = 211, 
		RULE_limit_clause = 212, RULE_groupby_clause = 213, RULE_grouping_element_list = 214, 
		RULE_grouping_element = 215, RULE_ordinary_grouping_set_list = 216, RULE_ordinary_grouping_set = 217, 
		RULE_rollup_list = 218, RULE_cube_list = 219, RULE_empty_grouping_set = 220, 
		RULE_having_clause = 221, RULE_qualify_clause = 222, RULE_row_value_predicand_list = 223, 
		RULE_comparison_predicate = 224, RULE_comparison_operator = 225, RULE_relative_comp_op = 226, 
		RULE_similarity_op = 227, RULE_comp_op = 228, RULE_between_predicate = 229, 
		RULE_symmetry = 230, RULE_in_predicate = 231, RULE_like_any_predicate = 232, 
		RULE_like_any_operator = 233, RULE_in_predicate_value = 234, RULE_in_value_list = 235, 
		RULE_escape_character_clause = 236, RULE_values_statement_primary = 237, 
		RULE_fully_defined_values_statement = 238, RULE_aliased_values_statement = 239, 
		RULE_values_statement = 240, RULE_values_matrix = 241, RULE_values_row = 242, 
		RULE_values_aliases = 243, RULE_values_aliases_list = 244, RULE_insert_values_statement = 245, 
		RULE_exists_predicate = 246, RULE_exists_operator = 247, RULE_exists_predicate_value = 248, 
		RULE_null_predicate = 249, RULE_is_null_clause = 250, RULE_is_clause = 251, 
		RULE_truth_value = 252, RULE_not = 253, RULE_quantified_comparison_predicate = 254, 
		RULE_quantifier = 255, RULE_all = 256, RULE_some = 257, RULE_unique_predicate = 258, 
		RULE_primary_datetime_field = 259, RULE_non_second_primary_datetime_field = 260, 
		RULE_extended_datetime_field = 261, RULE_routine_invocation = 262, RULE_function_name = 263, 
		RULE_function_names_for_reserved_words = 264, RULE_sql_argument_list = 265, 
		RULE_identifier = 266, RULE_alias_identifier = 267, RULE_variable_identifier = 268, 
		RULE_simple_identifier = 269, RULE_logical_identifier = 270, RULE_simple_variable_identifier = 271, 
		RULE_extended_variable_identifier = 272, RULE_jinja_identifier = 273, 
		RULE_jinja_function_call = 274, RULE_jinja_arg_list = 275, RULE_jinja_arg = 276, 
		RULE_jinja_variable_access = 277, RULE_jinja_name = 278, RULE_simple_numeric_identifier = 279, 
		RULE_snowflake_quoted_numeric_identifier = 280, RULE_snowflake_dollar_function_identifier = 281, 
		RULE_nonreserved_keywords = 282, RULE_signed_numeric_literal = 283, RULE_unsigned_literal = 284, 
		RULE_unsigned_numeric_literal = 285, RULE_real_number_def = 286, RULE_exponent = 287, 
		RULE_general_literal = 288, RULE_character_literal = 289, RULE_datetime_literal = 290, 
		RULE_time_literal = 291, RULE_timestamp_literal = 292, RULE_date_literal = 293, 
		RULE_boolean_literal = 294, RULE_data_type = 295, RULE_variable_size_data_type = 296, 
		RULE_variable_data_type_name = 297, RULE_type_length = 298, RULE_precision_scale_data_type = 299, 
		RULE_precision_data_type_name = 300, RULE_precision_param = 301, RULE_static_data_type = 302, 
		RULE_static_data_type_name = 303, RULE_puml_constant_identifier = 304;
	private static String[] makeRuleNames() {
		return new String[] {
			"script", "sql_statement", "ddl", "ddl_primary", "sql", "column_value", 
			"predicand_value", "in_list_predicate_value", "condition_value", "tuple_value", 
			"query_value", "join_extension_value", "literal_value", "values_statement_end", 
			"insert_end_point", "update_end_point", "delete_end_point", "truncate_end_point", 
			"create_statement_primary", "drop_statement_primary", "alter_statement_primary", 
			"truncate_statement_primary", "truncate_snowflake_expression", "truncate_postgres_expression", 
			"create_table_expression", "create_index_expression", "create_view_expression", 
			"create_materialized_view_expression", "create_function_expression", 
			"create_procedure_expression", "create_macro_expression", "create_sequence_expression", 
			"create_schema_expression", "create_database_expression", "create_role_expression", 
			"create_user_expression", "create_stage_expression", "create_file_format_expression", 
			"ddl_object_type", "drop_options", "alter_options", "generic_ddl_paren_content", 
			"generic_ddl_options", "with_query", "with_clause", "with_list_item", 
			"cte_body", "query_alias", "query", "insert_expression", "snowflake_insert", 
			"insert_target_table_primary", "postgres_insert", "insert_preamble", 
			"insert_source_primary", "update_expression", "delete_expression", "delete_snowflake_expression", 
			"delete_postgres_expression", "delete_returning", "delete_using_clause", 
			"returning", "assignment_expression_list", "assignment_expression", "query_expression", 
			"intersected_query", "intersect_clause", "intersect_operator", "unionized_query", 
			"union_clause", "union_operator", "query_primary", "subquery", "query_specification", 
			"into_list", "set_qualifier", "select_list", "select_item", "as_clause", 
			"select_all_columns", "wildcard_reference", "from_clause", "join_extension", 
			"table_reference_list", "join_extension_primary", "lateral_modifier", 
			"table_primary", "table_source_primary", "relation_as_clause", "tuple_primary", 
			"tuple_source_primary", "db_object_name", "unqualified_join", "qualified_join", 
			"join_type", "join_specification", "join_condition", "named_columns_join", 
			"using_term", "table_relational_modifier", "unpivot_clause", "relational_modifier_list", 
			"unpivot_null_policy", "relational_modifier_value_column", "relational_modifier_name_column", 
			"relational_modifier_in_item", "relational_modifier_alias", "pivot_clause", 
			"pivot_aggregate", "table_function_primary", "table_function", "flatten_table_function", 
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
			null, null, null, null, null, null, null, null, null, null, null, "' '", 
			null, "'{{'", "'}}'"
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
			"QUALIFY", "POSITION", "CHARINDEX", "INSTR", "DELETE", "TRUNCATE", "IFF", 
			"MD5", "REVERSE", "FLATTEN", "SPLIT_TO_TABLE", "STRTOK_SPLIT_TO_TABLE", 
			"GENERATOR", "INFER_SCHEMA", "VALIDATE", "RESULT_SCAN", "QUERY_HISTORY", 
			"QUERY_HSTORY", "INPUT", "PATH", "RECURSIVE", "MODE", "LATERAL", "ROWCOUNT", 
			"TIMELIMIT", "FILES", "FILE_FORMAT", "IGNORE_CASE", "MAX_FILE_COUNT", 
			"MAX_RECORDS_PER_FILE", "KIND", "JOB_ID", "ALTER", "DATABASE", "FILE", 
			"FUNCTION", "MACRO", "MATERIALIZED", "PROCEDURE", "RETURNS", "ROLE", 
			"SCHEMA", "SEQUENCE", "STAGE", "USER", "VIEW", "FOR", "INCLUDE", "EXCLUDE", 
			"UNPIVOT", "PIVOT", "Identifier", "EXPONEN", "Scientific_Numeric_Literal", 
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
			setState(613);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(610);
					sql_statement();
					}
					} 
				}
				setState(615);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			setState(617);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 288265560523801088L) != 0) || _la==DROP || _la==INSERT || _la==UPDATE || ((((_la - 286)) & ~0x3f) == 0 && ((1L << (_la - 286)) & 429909046460417L) != 0) || _la==ALTER) {
				{
				setState(616);
				sql_statement();
				}
			}

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
	public static class Sql_statementContext extends ParserRuleContext {
		public Ddl_primaryContext ddl_primary() {
			return getRuleContext(Ddl_primaryContext.class,0);
		}
		public Delete_snowflake_expressionContext delete_snowflake_expression() {
			return getRuleContext(Delete_snowflake_expressionContext.class,0);
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
			setState(625);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				{
				setState(621);
				ddl_primary();
				}
				break;
			case 2:
				{
				setState(622);
				delete_snowflake_expression();
				}
				break;
			case 3:
				{
				setState(623);
				with_query();
				}
				break;
			case 4:
				{
				setState(624);
				query();
				}
				break;
			}
			setState(628);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEMI_COLON) {
				{
				setState(627);
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
			setState(630);
			ddl_primary();
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
		public Truncate_statement_primaryContext truncate_statement_primary() {
			return getRuleContext(Truncate_statement_primaryContext.class,0);
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
			setState(637);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CREATE:
				{
				setState(633);
				create_statement_primary();
				}
				break;
			case DROP:
				{
				setState(634);
				drop_statement_primary();
				}
				break;
			case ALTER:
				{
				setState(635);
				alter_statement_primary();
				}
				break;
			case TRUNCATE:
				{
				setState(636);
				truncate_statement_primary();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(640);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(639);
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
			setState(644);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case WITH:
				{
				setState(642);
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
			case DELETE:
				{
				setState(643);
				query();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(647);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEMI_COLON) {
				{
				setState(646);
				match(SEMI_COLON);
				}
			}

			setState(649);
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
			setState(651);
			column_primary();
			setState(652);
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
			setState(654);
			predicand_primary();
			setState(655);
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
			setState(657);
			in_predicate_value();
			setState(658);
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
			setState(660);
			value_expression();
			setState(661);
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
			setState(663);
			tuple_primary();
			setState(664);
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
			setState(666);
			query();
			setState(667);
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
			setState(669);
			join_extension_primary();
			setState(670);
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
			setState(674);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				{
				setState(672);
				signed_numeric_literal();
				}
				break;
			case 2:
				{
				setState(673);
				unsigned_literal();
				}
				break;
			}
			setState(676);
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
			setState(678);
			values_statement_primary();
			setState(679);
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
			setState(681);
			insert_expression();
			setState(682);
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
	public static class Update_end_pointContext extends ParserRuleContext {
		public Update_expressionContext update_expression() {
			return getRuleContext(Update_expressionContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SQLSelectParserParser.EOF, 0); }
		public Update_end_pointContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_update_end_point; }
	}

	public final Update_end_pointContext update_end_point() throws RecognitionException {
		Update_end_pointContext _localctx = new Update_end_pointContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_update_end_point);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(684);
			update_expression();
			setState(685);
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
	public static class Delete_end_pointContext extends ParserRuleContext {
		public Delete_expressionContext delete_expression() {
			return getRuleContext(Delete_expressionContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SQLSelectParserParser.EOF, 0); }
		public Delete_end_pointContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_delete_end_point; }
	}

	public final Delete_end_pointContext delete_end_point() throws RecognitionException {
		Delete_end_pointContext _localctx = new Delete_end_pointContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_delete_end_point);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(687);
			delete_expression();
			setState(688);
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
	public static class Truncate_end_pointContext extends ParserRuleContext {
		public Truncate_statement_primaryContext truncate_statement_primary() {
			return getRuleContext(Truncate_statement_primaryContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SQLSelectParserParser.EOF, 0); }
		public Truncate_end_pointContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_truncate_end_point; }
	}

	public final Truncate_end_pointContext truncate_end_point() throws RecognitionException {
		Truncate_end_pointContext _localctx = new Truncate_end_pointContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_truncate_end_point);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(690);
			truncate_statement_primary();
			setState(691);
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
		enterRule(_localctx, 36, RULE_create_statement_primary);
		try {
			setState(707);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(693);
				create_table_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(694);
				create_index_expression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(695);
				create_view_expression();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(696);
				create_materialized_view_expression();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(697);
				create_function_expression();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(698);
				create_procedure_expression();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(699);
				create_macro_expression();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(700);
				create_sequence_expression();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(701);
				create_schema_expression();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(702);
				create_database_expression();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(703);
				create_role_expression();
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(704);
				create_user_expression();
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(705);
				create_stage_expression();
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(706);
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
		enterRule(_localctx, 38, RULE_drop_statement_primary);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(709);
			match(DROP);
			setState(710);
			ddl_object_type();
			setState(711);
			db_object_name();
			setState(713);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				{
				setState(712);
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
		enterRule(_localctx, 40, RULE_alter_statement_primary);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(715);
			match(ALTER);
			setState(716);
			ddl_object_type();
			setState(717);
			db_object_name();
			setState(719);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				{
				setState(718);
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
	public static class Truncate_statement_primaryContext extends ParserRuleContext {
		public Truncate_snowflake_expressionContext truncate_snowflake_expression() {
			return getRuleContext(Truncate_snowflake_expressionContext.class,0);
		}
		public Truncate_postgres_expressionContext truncate_postgres_expression() {
			return getRuleContext(Truncate_postgres_expressionContext.class,0);
		}
		public Truncate_statement_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_truncate_statement_primary; }
	}

	public final Truncate_statement_primaryContext truncate_statement_primary() throws RecognitionException {
		Truncate_statement_primaryContext _localctx = new Truncate_statement_primaryContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_truncate_statement_primary);
		try {
			setState(723);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(721);
				truncate_snowflake_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(722);
				truncate_postgres_expression();
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
	public static class Truncate_snowflake_expressionContext extends ParserRuleContext {
		public TerminalNode TRUNCATE() { return getToken(SQLSelectParserParser.TRUNCATE, 0); }
		public TerminalNode TABLE() { return getToken(SQLSelectParserParser.TABLE, 0); }
		public Db_object_nameContext db_object_name() {
			return getRuleContext(Db_object_nameContext.class,0);
		}
		public Truncate_snowflake_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_truncate_snowflake_expression; }
	}

	public final Truncate_snowflake_expressionContext truncate_snowflake_expression() throws RecognitionException {
		Truncate_snowflake_expressionContext _localctx = new Truncate_snowflake_expressionContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_truncate_snowflake_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(725);
			match(TRUNCATE);
			setState(726);
			match(TABLE);
			setState(727);
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
	public static class Truncate_postgres_expressionContext extends ParserRuleContext {
		public TerminalNode TRUNCATE() { return getToken(SQLSelectParserParser.TRUNCATE, 0); }
		public List<Db_object_nameContext> db_object_name() {
			return getRuleContexts(Db_object_nameContext.class);
		}
		public Db_object_nameContext db_object_name(int i) {
			return getRuleContext(Db_object_nameContext.class,i);
		}
		public TerminalNode TABLE() { return getToken(SQLSelectParserParser.TABLE, 0); }
		public List<TerminalNode> COMMA() { return getTokens(SQLSelectParserParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SQLSelectParserParser.COMMA, i);
		}
		public Truncate_postgres_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_truncate_postgres_expression; }
	}

	public final Truncate_postgres_expressionContext truncate_postgres_expression() throws RecognitionException {
		Truncate_postgres_expressionContext _localctx = new Truncate_postgres_expressionContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_truncate_postgres_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(729);
			match(TRUNCATE);
			setState(731);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==TABLE) {
				{
				setState(730);
				match(TABLE);
				}
			}

			setState(733);
			db_object_name();
			setState(738);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(734);
				match(COMMA);
				setState(735);
				db_object_name();
				}
				}
				setState(740);
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
		enterRule(_localctx, 48, RULE_create_table_expression);
		try {
			setState(759);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(741);
				match(CREATE);
				setState(742);
				match(TABLE);
				setState(743);
				db_object_name();
				setState(744);
				match(AS);
				setState(745);
				query_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(747);
				match(CREATE);
				setState(748);
				match(TABLE);
				setState(749);
				db_object_name();
				setState(754);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
				case 1:
					{
					setState(750);
					match(LEFT_PAREN);
					setState(751);
					generic_ddl_paren_content();
					setState(752);
					match(RIGHT_PAREN);
					}
					break;
				}
				setState(757);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
				case 1:
					{
					setState(756);
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
		enterRule(_localctx, 50, RULE_create_index_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(761);
			match(CREATE);
			setState(762);
			match(INDEX);
			setState(763);
			db_object_name();
			setState(764);
			match(ON);
			setState(765);
			db_object_name();
			setState(766);
			match(LEFT_PAREN);
			setState(767);
			column_reference_list();
			setState(768);
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
		enterRule(_localctx, 52, RULE_create_view_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(770);
			match(CREATE);
			setState(771);
			match(VIEW);
			setState(772);
			db_object_name();
			setState(773);
			match(AS);
			setState(774);
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
		enterRule(_localctx, 54, RULE_create_materialized_view_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(776);
			match(CREATE);
			setState(777);
			match(MATERIALIZED);
			setState(778);
			match(VIEW);
			setState(779);
			db_object_name();
			setState(780);
			match(AS);
			setState(781);
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
		enterRule(_localctx, 56, RULE_create_function_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(783);
			match(CREATE);
			setState(784);
			match(FUNCTION);
			setState(785);
			db_object_name();
			setState(786);
			match(LEFT_PAREN);
			setState(788);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -2L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -1L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -1L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -1L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & -2147483649L) != 0) || ((((_la - 320)) & ~0x3f) == 0 && ((1L << (_la - 320)) & -1L) != 0) || ((((_la - 384)) & ~0x3f) == 0 && ((1L << (_la - 384)) & 1023L) != 0)) {
				{
				setState(787);
				generic_ddl_paren_content();
				}
			}

			setState(790);
			match(RIGHT_PAREN);
			setState(791);
			match(RETURNS);
			setState(792);
			data_type();
			setState(793);
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
		enterRule(_localctx, 58, RULE_create_procedure_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(795);
			match(CREATE);
			setState(796);
			match(PROCEDURE);
			setState(797);
			db_object_name();
			setState(798);
			match(LEFT_PAREN);
			setState(800);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -2L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -1L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -1L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -1L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & -2147483649L) != 0) || ((((_la - 320)) & ~0x3f) == 0 && ((1L << (_la - 320)) & -1L) != 0) || ((((_la - 384)) & ~0x3f) == 0 && ((1L << (_la - 384)) & 1023L) != 0)) {
				{
				setState(799);
				generic_ddl_paren_content();
				}
			}

			setState(802);
			match(RIGHT_PAREN);
			setState(803);
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
		enterRule(_localctx, 60, RULE_create_macro_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(805);
			match(CREATE);
			setState(806);
			match(MACRO);
			setState(807);
			db_object_name();
			setState(808);
			match(LEFT_PAREN);
			setState(810);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -2L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -1L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -1L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -1L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & -2147483649L) != 0) || ((((_la - 320)) & ~0x3f) == 0 && ((1L << (_la - 320)) & -1L) != 0) || ((((_la - 384)) & ~0x3f) == 0 && ((1L << (_la - 384)) & 1023L) != 0)) {
				{
				setState(809);
				generic_ddl_paren_content();
				}
			}

			setState(812);
			match(RIGHT_PAREN);
			setState(813);
			match(AS);
			setState(814);
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
		enterRule(_localctx, 62, RULE_create_sequence_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(816);
			match(CREATE);
			setState(817);
			match(SEQUENCE);
			setState(818);
			db_object_name();
			setState(819);
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
		enterRule(_localctx, 64, RULE_create_schema_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(821);
			match(CREATE);
			setState(822);
			match(SCHEMA);
			setState(823);
			db_object_name();
			setState(825);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				{
				setState(824);
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
		enterRule(_localctx, 66, RULE_create_database_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(827);
			match(CREATE);
			setState(828);
			match(DATABASE);
			setState(829);
			db_object_name();
			setState(831);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				{
				setState(830);
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
		enterRule(_localctx, 68, RULE_create_role_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(833);
			match(CREATE);
			setState(834);
			match(ROLE);
			setState(835);
			db_object_name();
			setState(837);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				{
				setState(836);
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
		enterRule(_localctx, 70, RULE_create_user_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(839);
			match(CREATE);
			setState(840);
			match(USER);
			setState(841);
			db_object_name();
			setState(843);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				{
				setState(842);
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
		enterRule(_localctx, 72, RULE_create_stage_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(845);
			match(CREATE);
			setState(846);
			match(STAGE);
			setState(847);
			db_object_name();
			setState(849);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
			case 1:
				{
				setState(848);
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
		enterRule(_localctx, 74, RULE_create_file_format_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(851);
			match(CREATE);
			setState(852);
			match(FILE);
			setState(853);
			match(FORMAT);
			setState(854);
			db_object_name();
			setState(856);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				{
				setState(855);
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
		enterRule(_localctx, 76, RULE_ddl_object_type);
		try {
			setState(874);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TABLE:
				enterOuterAlt(_localctx, 1);
				{
				setState(858);
				match(TABLE);
				}
				break;
			case INDEX:
				enterOuterAlt(_localctx, 2);
				{
				setState(859);
				match(INDEX);
				}
				break;
			case VIEW:
				enterOuterAlt(_localctx, 3);
				{
				setState(860);
				match(VIEW);
				}
				break;
			case FUNCTION:
				enterOuterAlt(_localctx, 4);
				{
				setState(861);
				match(FUNCTION);
				}
				break;
			case PROCEDURE:
				enterOuterAlt(_localctx, 5);
				{
				setState(862);
				match(PROCEDURE);
				}
				break;
			case MACRO:
				enterOuterAlt(_localctx, 6);
				{
				setState(863);
				match(MACRO);
				}
				break;
			case SEQUENCE:
				enterOuterAlt(_localctx, 7);
				{
				setState(864);
				match(SEQUENCE);
				}
				break;
			case SCHEMA:
				enterOuterAlt(_localctx, 8);
				{
				setState(865);
				match(SCHEMA);
				}
				break;
			case DATABASE:
				enterOuterAlt(_localctx, 9);
				{
				setState(866);
				match(DATABASE);
				}
				break;
			case ROLE:
				enterOuterAlt(_localctx, 10);
				{
				setState(867);
				match(ROLE);
				}
				break;
			case USER:
				enterOuterAlt(_localctx, 11);
				{
				setState(868);
				match(USER);
				}
				break;
			case STAGE:
				enterOuterAlt(_localctx, 12);
				{
				setState(869);
				match(STAGE);
				}
				break;
			case FILE:
				enterOuterAlt(_localctx, 13);
				{
				setState(870);
				match(FILE);
				setState(871);
				match(FORMAT);
				}
				break;
			case MATERIALIZED:
				enterOuterAlt(_localctx, 14);
				{
				setState(872);
				match(MATERIALIZED);
				setState(873);
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
		enterRule(_localctx, 78, RULE_drop_options);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(876);
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
		enterRule(_localctx, 80, RULE_alter_options);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(878);
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
		enterRule(_localctx, 82, RULE_generic_ddl_paren_content);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(881); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(880);
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
				setState(883); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & -2L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -1L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -1L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -1L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & -2147483649L) != 0) || ((((_la - 320)) & ~0x3f) == 0 && ((1L << (_la - 320)) & -1L) != 0) || ((((_la - 384)) & ~0x3f) == 0 && ((1L << (_la - 384)) & 1023L) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
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
		enterRule(_localctx, 84, RULE_generic_ddl_options);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(886); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(885);
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
				setState(888); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,29,_ctx);
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
		enterRule(_localctx, 86, RULE_with_query);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(890);
			with_clause();
			setState(891);
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
		enterRule(_localctx, 88, RULE_with_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(893);
			match(WITH);
			setState(894);
			with_list_item();
			setState(899);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(895);
				match(COMMA);
				setState(896);
				with_list_item();
				}
				}
				setState(901);
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
		enterRule(_localctx, 90, RULE_with_list_item);
		try {
			setState(910);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(902);
				query_alias();
				{
				setState(903);
				match(LEFT_PAREN);
				setState(904);
				cte_body();
				setState(905);
				match(RIGHT_PAREN);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(907);
				query_alias();
				setState(908);
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
		enterRule(_localctx, 92, RULE_cte_body);
		try {
			setState(914);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case WITH:
				enterOuterAlt(_localctx, 1);
				{
				setState(912);
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
			case DELETE:
				enterOuterAlt(_localctx, 2);
				{
				setState(913);
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
		enterRule(_localctx, 94, RULE_query_alias);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(916);
			identifier();
			setState(917);
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
		public Delete_expressionContext delete_expression() {
			return getRuleContext(Delete_expressionContext.class,0);
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
		enterRule(_localctx, 96, RULE_query);
		try {
			setState(924);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(919);
				query_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(920);
				insert_expression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(921);
				update_expression();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(922);
				delete_expression();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(923);
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
		enterRule(_localctx, 98, RULE_insert_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(926);
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
		enterRule(_localctx, 100, RULE_snowflake_insert);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(928);
			insert_preamble();
			setState(929);
			insert_target_table_primary();
			setState(930);
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
		enterRule(_localctx, 102, RULE_insert_target_table_primary);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(935);
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
			case DELETE:
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
			case FOR:
			case INCLUDE:
			case EXCLUDE:
			case UNPIVOT:
			case PIVOT:
			case Identifier:
			case Double_Quoted_Numeric_Identifier:
			case Dollar_Sign_Identifier:
				{
				setState(932);
				db_object_name();
				}
				break;
			case Variable_Identifier:
			case Extended_Variable_Identifier:
			case Mixed_Variable_Identifier:
				{
				setState(933);
				variable_identifier();
				}
				break;
			case JINJA_OPEN:
				{
				setState(934);
				jinja_identifier();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(941);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,35,_ctx) ) {
			case 1:
				{
				setState(937);
				match(LEFT_PAREN);
				setState(938);
				column_reference_list();
				setState(939);
				match(RIGHT_PAREN);
				}
				break;
			}
			setState(944);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				{
				setState(943);
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
		enterRule(_localctx, 104, RULE_postgres_insert);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(946);
			snowflake_insert();
			setState(948);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==RETURNING) {
				{
				setState(947);
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
		enterRule(_localctx, 106, RULE_insert_preamble);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(950);
			match(INSERT);
			setState(952);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OVERWRITE) {
				{
				setState(951);
				match(OVERWRITE);
				}
			}

			setState(954);
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
		enterRule(_localctx, 108, RULE_insert_source_primary);
		try {
			setState(959);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(956);
				query_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(957);
				variable_identifier();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(958);
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
		enterRule(_localctx, 110, RULE_update_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(961);
			match(UPDATE);
			setState(962);
			table_primary();
			setState(963);
			match(SET);
			setState(964);
			assignment_expression_list();
			setState(966);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FROM) {
				{
				setState(965);
				from_clause();
				}
			}

			setState(969);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(968);
				where_clause();
				}
			}

			setState(972);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==RETURNING) {
				{
				setState(971);
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
	public static class Delete_expressionContext extends ParserRuleContext {
		public Delete_snowflake_expressionContext delete_snowflake_expression() {
			return getRuleContext(Delete_snowflake_expressionContext.class,0);
		}
		public Delete_postgres_expressionContext delete_postgres_expression() {
			return getRuleContext(Delete_postgres_expressionContext.class,0);
		}
		public Delete_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_delete_expression; }
	}

	public final Delete_expressionContext delete_expression() throws RecognitionException {
		Delete_expressionContext _localctx = new Delete_expressionContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_delete_expression);
		try {
			setState(976);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,43,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(974);
				delete_snowflake_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(975);
				delete_postgres_expression();
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
	public static class Delete_snowflake_expressionContext extends ParserRuleContext {
		public TerminalNode DELETE() { return getToken(SQLSelectParserParser.DELETE, 0); }
		public TerminalNode FROM() { return getToken(SQLSelectParserParser.FROM, 0); }
		public Table_primaryContext table_primary() {
			return getRuleContext(Table_primaryContext.class,0);
		}
		public Delete_using_clauseContext delete_using_clause() {
			return getRuleContext(Delete_using_clauseContext.class,0);
		}
		public Where_clauseContext where_clause() {
			return getRuleContext(Where_clauseContext.class,0);
		}
		public Delete_snowflake_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_delete_snowflake_expression; }
	}

	public final Delete_snowflake_expressionContext delete_snowflake_expression() throws RecognitionException {
		Delete_snowflake_expressionContext _localctx = new Delete_snowflake_expressionContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_delete_snowflake_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(978);
			match(DELETE);
			setState(979);
			match(FROM);
			setState(980);
			table_primary();
			setState(982);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==USING) {
				{
				setState(981);
				delete_using_clause();
				}
			}

			setState(985);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(984);
				where_clause();
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
	public static class Delete_postgres_expressionContext extends ParserRuleContext {
		public TerminalNode DELETE() { return getToken(SQLSelectParserParser.DELETE, 0); }
		public TerminalNode FROM() { return getToken(SQLSelectParserParser.FROM, 0); }
		public Table_primaryContext table_primary() {
			return getRuleContext(Table_primaryContext.class,0);
		}
		public Delete_returningContext delete_returning() {
			return getRuleContext(Delete_returningContext.class,0);
		}
		public Delete_using_clauseContext delete_using_clause() {
			return getRuleContext(Delete_using_clauseContext.class,0);
		}
		public Where_clauseContext where_clause() {
			return getRuleContext(Where_clauseContext.class,0);
		}
		public Delete_postgres_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_delete_postgres_expression; }
	}

	public final Delete_postgres_expressionContext delete_postgres_expression() throws RecognitionException {
		Delete_postgres_expressionContext _localctx = new Delete_postgres_expressionContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_delete_postgres_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(987);
			match(DELETE);
			setState(988);
			match(FROM);
			setState(989);
			table_primary();
			setState(991);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==USING) {
				{
				setState(990);
				delete_using_clause();
				}
			}

			setState(994);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(993);
				where_clause();
				}
			}

			setState(996);
			delete_returning();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Delete_returningContext extends ParserRuleContext {
		public TerminalNode RETURNING() { return getToken(SQLSelectParserParser.RETURNING, 0); }
		public Select_listContext select_list() {
			return getRuleContext(Select_listContext.class,0);
		}
		public Delete_returningContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_delete_returning; }
	}

	public final Delete_returningContext delete_returning() throws RecognitionException {
		Delete_returningContext _localctx = new Delete_returningContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_delete_returning);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(998);
			match(RETURNING);
			setState(999);
			select_list();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Delete_using_clauseContext extends ParserRuleContext {
		public TerminalNode USING() { return getToken(SQLSelectParserParser.USING, 0); }
		public Table_reference_listContext table_reference_list() {
			return getRuleContext(Table_reference_listContext.class,0);
		}
		public Delete_using_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_delete_using_clause; }
	}

	public final Delete_using_clauseContext delete_using_clause() throws RecognitionException {
		Delete_using_clauseContext _localctx = new Delete_using_clauseContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_delete_using_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1001);
			match(USING);
			setState(1002);
			table_reference_list();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
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
		enterRule(_localctx, 122, RULE_returning);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1004);
			match(RETURNING);
			setState(1005);
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
		enterRule(_localctx, 124, RULE_assignment_expression_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1007);
			assignment_expression();
			setState(1012);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1008);
				match(COMMA);
				setState(1009);
				assignment_expression();
				}
				}
				setState(1014);
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
		enterRule(_localctx, 126, RULE_assignment_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1015);
			column_reference();
			setState(1016);
			match(EQUAL);
			setState(1017);
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
		enterRule(_localctx, 128, RULE_query_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1019);
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
		enterRule(_localctx, 130, RULE_intersected_query);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1021);
			unionized_query();
			setState(1027);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INTERSECT) {
				{
				{
				setState(1022);
				intersect_clause();
				setState(1023);
				unionized_query();
				}
				}
				setState(1029);
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
		enterRule(_localctx, 132, RULE_intersect_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1030);
			intersect_operator();
			setState(1032);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ALL || _la==DISTINCT) {
				{
				setState(1031);
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
		enterRule(_localctx, 134, RULE_intersect_operator);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(1034);
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
		enterRule(_localctx, 136, RULE_unionized_query);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1036);
			query_primary();
			setState(1042);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EXCEPT || _la==UNION) {
				{
				{
				setState(1037);
				union_clause();
				setState(1038);
				query_primary();
				}
				}
				setState(1044);
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
		enterRule(_localctx, 138, RULE_union_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1045);
			union_operator();
			setState(1047);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ALL || _la==DISTINCT) {
				{
				setState(1046);
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
		enterRule(_localctx, 140, RULE_union_operator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1049);
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
		enterRule(_localctx, 142, RULE_query_primary);
		try {
			setState(1054);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LEFT_PAREN:
				enterOuterAlt(_localctx, 1);
				{
				setState(1051);
				subquery();
				}
				break;
			case SELECT:
				enterOuterAlt(_localctx, 2);
				{
				setState(1052);
				query_specification();
				}
				break;
			case Variable_Identifier:
			case Extended_Variable_Identifier:
			case Mixed_Variable_Identifier:
				enterOuterAlt(_localctx, 3);
				{
				setState(1053);
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
		enterRule(_localctx, 144, RULE_subquery);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1056);
			match(LEFT_PAREN);
			setState(1057);
			query_expression();
			setState(1058);
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
		enterRule(_localctx, 146, RULE_query_specification);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1060);
			match(SELECT);
			setState(1062);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==INTO) {
				{
				setState(1061);
				into_list();
				}
			}

			setState(1065);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ALL || _la==DISTINCT) {
				{
				setState(1064);
				set_qualifier();
				}
			}

			setState(1067);
			select_list();
			setState(1087);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FROM) {
				{
				setState(1068);
				from_clause();
				setState(1070);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WHERE) {
					{
					setState(1069);
					where_clause();
					}
				}

				setState(1073);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==GROUP) {
					{
					setState(1072);
					groupby_clause();
					}
				}

				setState(1076);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HAVING) {
					{
					setState(1075);
					having_clause();
					}
				}

				setState(1079);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==QUALIFY) {
					{
					setState(1078);
					qualify_clause();
					}
				}

				setState(1082);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ORDER) {
					{
					setState(1081);
					orderby_clause();
					}
				}

				setState(1085);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LIMIT) {
					{
					setState(1084);
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
		enterRule(_localctx, 148, RULE_into_list);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1089);
			match(INTO);
			setState(1090);
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
		enterRule(_localctx, 150, RULE_set_qualifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1092);
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
		enterRule(_localctx, 152, RULE_select_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1094);
			select_item();
			setState(1099);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1095);
				match(COMMA);
				setState(1096);
				select_item();
				}
				}
				setState(1101);
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
		enterRule(_localctx, 154, RULE_select_item);
		try {
			setState(1107);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,65,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1102);
				value_expression();
				setState(1104);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,64,_ctx) ) {
				case 1:
					{
					setState(1103);
					as_clause();
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1106);
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
		enterRule(_localctx, 156, RULE_as_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1110);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS) {
				{
				setState(1109);
				match(AS);
				}
			}

			setState(1112);
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
		enterRule(_localctx, 158, RULE_select_all_columns);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1114);
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
		enterRule(_localctx, 160, RULE_wildcard_reference);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1118);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Identifier) {
				{
				setState(1116);
				((Wildcard_referenceContext)_localctx).tb_name = match(Identifier);
				setState(1117);
				match(DOT);
				}
			}

			setState(1120);
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
		enterRule(_localctx, 162, RULE_from_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1122);
			match(FROM);
			setState(1123);
			table_reference_list();
			setState(1125);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,68,_ctx) ) {
			case 1:
				{
				setState(1124);
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
		enterRule(_localctx, 164, RULE_join_extension);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1127);
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
		enterRule(_localctx, 166, RULE_table_reference_list);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1129);
			table_primary();
			setState(1151);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,74,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(1149);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case COMMA:
						{
						{
						setState(1130);
						match(COMMA);
						setState(1132);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==LATERAL) {
							{
							setState(1131);
							lateral_modifier();
							}
						}

						setState(1134);
						table_primary();
						}
						}
						break;
					case CROSS:
					case NATURAL:
					case UNION:
						{
						{
						setState(1135);
						unqualified_join();
						setState(1137);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==LATERAL) {
							{
							setState(1136);
							lateral_modifier();
							}
						}

						setState(1139);
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
						setState(1141);
						qualified_join();
						setState(1143);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==LATERAL) {
							{
							setState(1142);
							lateral_modifier();
							}
						}

						setState(1145);
						((Table_reference_listContext)_localctx).right = table_primary();
						setState(1147);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==ON || _la==USING) {
							{
							setState(1146);
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
				setState(1153);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,74,_ctx);
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
		enterRule(_localctx, 168, RULE_join_extension_primary);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1175);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 9016000322274304L) != 0) || _la==COMMA) {
				{
				setState(1173);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case COMMA:
					{
					{
					setState(1154);
					match(COMMA);
					setState(1156);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==LATERAL) {
						{
						setState(1155);
						lateral_modifier();
						}
					}

					setState(1158);
					table_primary();
					}
					}
					break;
				case CROSS:
				case NATURAL:
				case UNION:
					{
					{
					setState(1159);
					unqualified_join();
					setState(1161);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==LATERAL) {
						{
						setState(1160);
						lateral_modifier();
						}
					}

					setState(1163);
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
					setState(1165);
					qualified_join();
					setState(1167);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==LATERAL) {
						{
						setState(1166);
						lateral_modifier();
						}
					}

					setState(1169);
					((Join_extension_primaryContext)_localctx).right = table_primary();
					setState(1171);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==ON || _la==USING) {
						{
						setState(1170);
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
				setState(1177);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1179);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 326)) & ~0x3f) == 0 && ((1L << (_la - 326)) & 7L) != 0)) {
				{
				setState(1178);
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
		enterRule(_localctx, 170, RULE_lateral_modifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1181);
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
		public Table_source_primaryContext table_source_primary() {
			return getRuleContext(Table_source_primaryContext.class,0);
		}
		public Table_relational_modifierContext table_relational_modifier() {
			return getRuleContext(Table_relational_modifierContext.class,0);
		}
		public Relation_as_clauseContext relation_as_clause() {
			return getRuleContext(Relation_as_clauseContext.class,0);
		}
		public Table_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_primary; }
	}

	public final Table_primaryContext table_primary() throws RecognitionException {
		Table_primaryContext _localctx = new Table_primaryContext(_ctx, getState());
		enterRule(_localctx, 172, RULE_table_primary);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1183);
			table_source_primary();
			setState(1185);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,82,_ctx) ) {
			case 1:
				{
				setState(1184);
				table_relational_modifier();
				}
				break;
			}
			setState(1188);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,83,_ctx) ) {
			case 1:
				{
				setState(1187);
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
	public static class Table_source_primaryContext extends ParserRuleContext {
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
		public Table_source_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_source_primary; }
	}

	public final Table_source_primaryContext table_source_primary() throws RecognitionException {
		Table_source_primaryContext _localctx = new Table_source_primaryContext(_ctx, getState());
		enterRule(_localctx, 174, RULE_table_source_primary);
		try {
			setState(1210);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,88,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1190);
				db_object_name();
				setState(1192);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,84,_ctx) ) {
				case 1:
					{
					setState(1191);
					relation_as_clause();
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1194);
				variable_identifier();
				setState(1195);
				as_clause();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1197);
				jinja_identifier();
				setState(1199);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,85,_ctx) ) {
				case 1:
					{
					setState(1198);
					relation_as_clause();
					}
					break;
				}
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1201);
				table_function_primary();
				setState(1203);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,86,_ctx) ) {
				case 1:
					{
					setState(1202);
					relation_as_clause();
					}
					break;
				}
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1205);
				values_statement_primary();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1206);
				subquery();
				setState(1208);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,87,_ctx) ) {
				case 1:
					{
					setState(1207);
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
		enterRule(_localctx, 176, RULE_relation_as_clause);
		try {
			setState(1216);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,89,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1212);
				match(AS);
				setState(1213);
				alias_identifier();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1214);
				if (!(isAllowedImplicitAlias(_input.LT(1).getText()))) throw new FailedPredicateException(this, "isAllowedImplicitAlias(_input.LT(1).getText())");
				setState(1215);
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
		public Tuple_source_primaryContext tuple_source_primary() {
			return getRuleContext(Tuple_source_primaryContext.class,0);
		}
		public Table_relational_modifierContext table_relational_modifier() {
			return getRuleContext(Table_relational_modifierContext.class,0);
		}
		public Tuple_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tuple_primary; }
	}

	public final Tuple_primaryContext tuple_primary() throws RecognitionException {
		Tuple_primaryContext _localctx = new Tuple_primaryContext(_ctx, getState());
		enterRule(_localctx, 178, RULE_tuple_primary);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1218);
			tuple_source_primary();
			setState(1220);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==UNPIVOT || _la==PIVOT) {
				{
				setState(1219);
				table_relational_modifier();
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
	public static class Tuple_source_primaryContext extends ParserRuleContext {
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
		public Tuple_source_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tuple_source_primary; }
	}

	public final Tuple_source_primaryContext tuple_source_primary() throws RecognitionException {
		Tuple_source_primaryContext _localctx = new Tuple_source_primaryContext(_ctx, getState());
		enterRule(_localctx, 180, RULE_tuple_source_primary);
		try {
			setState(1228);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,91,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1222);
				db_object_name();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1223);
				variable_identifier();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1224);
				jinja_identifier();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1225);
				table_function_primary();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1226);
				values_statement_primary();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1227);
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
		enterRule(_localctx, 182, RULE_db_object_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1230);
			identifier();
			setState(1236);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,93,_ctx) ) {
			case 1:
				{
				setState(1231);
				match(DOT);
				setState(1234);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case NUMBER:
				case Numeric_Identifier:
					{
					setState(1232);
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
				case DELETE:
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
				case FOR:
				case INCLUDE:
				case EXCLUDE:
				case UNPIVOT:
				case PIVOT:
				case Identifier:
				case Double_Quoted_Numeric_Identifier:
				case Dollar_Sign_Identifier:
					{
					setState(1233);
					identifier();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			}
			setState(1243);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,95,_ctx) ) {
			case 1:
				{
				setState(1238);
				match(DOT);
				setState(1241);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case NUMBER:
				case Numeric_Identifier:
					{
					setState(1239);
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
				case DELETE:
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
				case FOR:
				case INCLUDE:
				case EXCLUDE:
				case UNPIVOT:
				case PIVOT:
				case Identifier:
				case Double_Quoted_Numeric_Identifier:
				case Dollar_Sign_Identifier:
					{
					setState(1240);
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
		enterRule(_localctx, 184, RULE_unqualified_join);
		int _la;
		try {
			setState(1254);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CROSS:
				enterOuterAlt(_localctx, 1);
				{
				setState(1245);
				match(CROSS);
				setState(1246);
				match(JOIN);
				}
				break;
			case UNION:
				enterOuterAlt(_localctx, 2);
				{
				setState(1247);
				match(UNION);
				setState(1248);
				match(JOIN);
				}
				break;
			case NATURAL:
				enterOuterAlt(_localctx, 3);
				{
				setState(1249);
				match(NATURAL);
				setState(1251);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 8796638347264L) != 0)) {
					{
					setState(1250);
					((Unqualified_joinContext)_localctx).t = join_type();
					}
				}

				setState(1253);
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
		enterRule(_localctx, 186, RULE_qualified_join);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1257);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 8796638347264L) != 0)) {
				{
				setState(1256);
				((Qualified_joinContext)_localctx).t = join_type();
				}
			}

			setState(1259);
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
		enterRule(_localctx, 188, RULE_join_type);
		int _la;
		try {
			setState(1266);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INNER:
				enterOuterAlt(_localctx, 1);
				{
				setState(1261);
				match(INNER);
				}
				break;
			case FULL:
			case LEFT:
			case RIGHT:
				enterOuterAlt(_localctx, 2);
				{
				setState(1262);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 8796629958656L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1264);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OUTER) {
					{
					setState(1263);
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
		enterRule(_localctx, 190, RULE_join_specification);
		try {
			setState(1270);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ON:
				enterOuterAlt(_localctx, 1);
				{
				setState(1268);
				join_condition();
				}
				break;
			case USING:
				enterOuterAlt(_localctx, 2);
				{
				setState(1269);
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
		enterRule(_localctx, 192, RULE_join_condition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1272);
			match(ON);
			setState(1273);
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
		enterRule(_localctx, 194, RULE_named_columns_join);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1275);
			using_term();
			setState(1276);
			match(LEFT_PAREN);
			setState(1277);
			((Named_columns_joinContext)_localctx).f = column_reference_list();
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
	public static class Using_termContext extends ParserRuleContext {
		public TerminalNode USING() { return getToken(SQLSelectParserParser.USING, 0); }
		public Using_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_using_term; }
	}

	public final Using_termContext using_term() throws RecognitionException {
		Using_termContext _localctx = new Using_termContext(_ctx, getState());
		enterRule(_localctx, 196, RULE_using_term);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1280);
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
	public static class Table_relational_modifierContext extends ParserRuleContext {
		public Unpivot_clauseContext unpivot_clause() {
			return getRuleContext(Unpivot_clauseContext.class,0);
		}
		public Pivot_clauseContext pivot_clause() {
			return getRuleContext(Pivot_clauseContext.class,0);
		}
		public Table_relational_modifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_relational_modifier; }
	}

	public final Table_relational_modifierContext table_relational_modifier() throws RecognitionException {
		Table_relational_modifierContext _localctx = new Table_relational_modifierContext(_ctx, getState());
		enterRule(_localctx, 198, RULE_table_relational_modifier);
		try {
			setState(1284);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case UNPIVOT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1282);
				unpivot_clause();
				}
				break;
			case PIVOT:
				enterOuterAlt(_localctx, 2);
				{
				setState(1283);
				pivot_clause();
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
	public static class Unpivot_clauseContext extends ParserRuleContext {
		public TerminalNode UNPIVOT() { return getToken(SQLSelectParserParser.UNPIVOT, 0); }
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public Relational_modifier_value_columnContext relational_modifier_value_column() {
			return getRuleContext(Relational_modifier_value_columnContext.class,0);
		}
		public TerminalNode FOR() { return getToken(SQLSelectParserParser.FOR, 0); }
		public Relational_modifier_name_columnContext relational_modifier_name_column() {
			return getRuleContext(Relational_modifier_name_columnContext.class,0);
		}
		public TerminalNode IN() { return getToken(SQLSelectParserParser.IN, 0); }
		public Relational_modifier_listContext relational_modifier_list() {
			return getRuleContext(Relational_modifier_listContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Unpivot_null_policyContext unpivot_null_policy() {
			return getRuleContext(Unpivot_null_policyContext.class,0);
		}
		public Relation_as_clauseContext relation_as_clause() {
			return getRuleContext(Relation_as_clauseContext.class,0);
		}
		public Unpivot_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unpivot_clause; }
	}

	public final Unpivot_clauseContext unpivot_clause() throws RecognitionException {
		Unpivot_clauseContext _localctx = new Unpivot_clauseContext(_ctx, getState());
		enterRule(_localctx, 200, RULE_unpivot_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1286);
			match(UNPIVOT);
			setState(1288);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==INCLUDE || _la==EXCLUDE) {
				{
				setState(1287);
				unpivot_null_policy();
				}
			}

			setState(1290);
			match(LEFT_PAREN);
			setState(1291);
			relational_modifier_value_column();
			setState(1292);
			match(FOR);
			setState(1293);
			relational_modifier_name_column();
			setState(1294);
			match(IN);
			setState(1295);
			relational_modifier_list();
			setState(1296);
			match(RIGHT_PAREN);
			setState(1298);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,104,_ctx) ) {
			case 1:
				{
				setState(1297);
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
	public static class Relational_modifier_listContext extends ParserRuleContext {
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public List<Relational_modifier_in_itemContext> relational_modifier_in_item() {
			return getRuleContexts(Relational_modifier_in_itemContext.class);
		}
		public Relational_modifier_in_itemContext relational_modifier_in_item(int i) {
			return getRuleContext(Relational_modifier_in_itemContext.class,i);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public List<TerminalNode> COMMA() { return getTokens(SQLSelectParserParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SQLSelectParserParser.COMMA, i);
		}
		public Relational_modifier_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relational_modifier_list; }
	}

	public final Relational_modifier_listContext relational_modifier_list() throws RecognitionException {
		Relational_modifier_listContext _localctx = new Relational_modifier_listContext(_ctx, getState());
		enterRule(_localctx, 202, RULE_relational_modifier_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1300);
			match(LEFT_PAREN);
			setState(1301);
			relational_modifier_in_item();
			setState(1306);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1302);
				match(COMMA);
				setState(1303);
				relational_modifier_in_item();
				}
				}
				setState(1308);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1309);
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
	public static class Unpivot_null_policyContext extends ParserRuleContext {
		public TerminalNode INCLUDE() { return getToken(SQLSelectParserParser.INCLUDE, 0); }
		public TerminalNode NULLS() { return getToken(SQLSelectParserParser.NULLS, 0); }
		public TerminalNode EXCLUDE() { return getToken(SQLSelectParserParser.EXCLUDE, 0); }
		public Unpivot_null_policyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unpivot_null_policy; }
	}

	public final Unpivot_null_policyContext unpivot_null_policy() throws RecognitionException {
		Unpivot_null_policyContext _localctx = new Unpivot_null_policyContext(_ctx, getState());
		enterRule(_localctx, 204, RULE_unpivot_null_policy);
		try {
			setState(1315);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INCLUDE:
				enterOuterAlt(_localctx, 1);
				{
				setState(1311);
				match(INCLUDE);
				setState(1312);
				match(NULLS);
				}
				break;
			case EXCLUDE:
				enterOuterAlt(_localctx, 2);
				{
				setState(1313);
				match(EXCLUDE);
				setState(1314);
				match(NULLS);
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
	public static class Relational_modifier_value_columnContext extends ParserRuleContext {
		public Alias_identifierContext alias_identifier() {
			return getRuleContext(Alias_identifierContext.class,0);
		}
		public Relational_modifier_value_columnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relational_modifier_value_column; }
	}

	public final Relational_modifier_value_columnContext relational_modifier_value_column() throws RecognitionException {
		Relational_modifier_value_columnContext _localctx = new Relational_modifier_value_columnContext(_ctx, getState());
		enterRule(_localctx, 206, RULE_relational_modifier_value_column);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1317);
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
	public static class Relational_modifier_name_columnContext extends ParserRuleContext {
		public Alias_identifierContext alias_identifier() {
			return getRuleContext(Alias_identifierContext.class,0);
		}
		public Relational_modifier_name_columnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relational_modifier_name_column; }
	}

	public final Relational_modifier_name_columnContext relational_modifier_name_column() throws RecognitionException {
		Relational_modifier_name_columnContext _localctx = new Relational_modifier_name_columnContext(_ctx, getState());
		enterRule(_localctx, 208, RULE_relational_modifier_name_column);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1319);
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
	public static class Relational_modifier_in_itemContext extends ParserRuleContext {
		public Column_referenceContext column_reference() {
			return getRuleContext(Column_referenceContext.class,0);
		}
		public Relational_modifier_aliasContext relational_modifier_alias() {
			return getRuleContext(Relational_modifier_aliasContext.class,0);
		}
		public Relational_modifier_in_itemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relational_modifier_in_item; }
	}

	public final Relational_modifier_in_itemContext relational_modifier_in_item() throws RecognitionException {
		Relational_modifier_in_itemContext _localctx = new Relational_modifier_in_itemContext(_ctx, getState());
		enterRule(_localctx, 210, RULE_relational_modifier_in_item);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1321);
			column_reference();
			setState(1323);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -855661835887575038L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -292734320500539713L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -2097169L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -162133984631980033L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 8796093038591L) != 0) || ((((_la - 325)) & ~0x3f) == 0 && ((1L << (_la - 325)) & -8286623383079100159L) != 0)) {
				{
				setState(1322);
				relational_modifier_alias();
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
	public static class Relational_modifier_aliasContext extends ParserRuleContext {
		public Alias_identifierContext alias_identifier() {
			return getRuleContext(Alias_identifierContext.class,0);
		}
		public TerminalNode Character_String_Literal() { return getToken(SQLSelectParserParser.Character_String_Literal, 0); }
		public TerminalNode AS() { return getToken(SQLSelectParserParser.AS, 0); }
		public Relational_modifier_aliasContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relational_modifier_alias; }
	}

	public final Relational_modifier_aliasContext relational_modifier_alias() throws RecognitionException {
		Relational_modifier_aliasContext _localctx = new Relational_modifier_aliasContext(_ctx, getState());
		enterRule(_localctx, 212, RULE_relational_modifier_alias);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1326);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS) {
				{
				setState(1325);
				match(AS);
				}
			}

			setState(1330);
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
			case NUMBER:
			case Bracket_Identifier:
			case DELETE:
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
			case FOR:
			case INCLUDE:
			case EXCLUDE:
			case UNPIVOT:
			case PIVOT:
			case Identifier:
			case Numeric_Identifier:
			case Double_Quoted_Numeric_Identifier:
				{
				setState(1328);
				alias_identifier();
				}
				break;
			case Character_String_Literal:
				{
				setState(1329);
				match(Character_String_Literal);
				}
				break;
			default:
				throw new NoViableAltException(this);
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
	public static class Pivot_clauseContext extends ParserRuleContext {
		public TerminalNode PIVOT() { return getToken(SQLSelectParserParser.PIVOT, 0); }
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public Pivot_aggregateContext pivot_aggregate() {
			return getRuleContext(Pivot_aggregateContext.class,0);
		}
		public TerminalNode FOR() { return getToken(SQLSelectParserParser.FOR, 0); }
		public Relational_modifier_name_columnContext relational_modifier_name_column() {
			return getRuleContext(Relational_modifier_name_columnContext.class,0);
		}
		public TerminalNode IN() { return getToken(SQLSelectParserParser.IN, 0); }
		public Relational_modifier_listContext relational_modifier_list() {
			return getRuleContext(Relational_modifier_listContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Relation_as_clauseContext relation_as_clause() {
			return getRuleContext(Relation_as_clauseContext.class,0);
		}
		public Pivot_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pivot_clause; }
	}

	public final Pivot_clauseContext pivot_clause() throws RecognitionException {
		Pivot_clauseContext _localctx = new Pivot_clauseContext(_ctx, getState());
		enterRule(_localctx, 214, RULE_pivot_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1332);
			match(PIVOT);
			setState(1333);
			match(LEFT_PAREN);
			setState(1334);
			pivot_aggregate();
			setState(1335);
			match(FOR);
			setState(1336);
			relational_modifier_name_column();
			setState(1337);
			match(IN);
			setState(1338);
			relational_modifier_list();
			setState(1339);
			match(RIGHT_PAREN);
			setState(1341);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,110,_ctx) ) {
			case 1:
				{
				setState(1340);
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
	public static class Pivot_aggregateContext extends ParserRuleContext {
		public Set_function_typeContext set_function_type() {
			return getRuleContext(Set_function_typeContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public Relational_modifier_value_columnContext relational_modifier_value_column() {
			return getRuleContext(Relational_modifier_value_columnContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Pivot_aggregateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pivot_aggregate; }
	}

	public final Pivot_aggregateContext pivot_aggregate() throws RecognitionException {
		Pivot_aggregateContext _localctx = new Pivot_aggregateContext(_ctx, getState());
		enterRule(_localctx, 216, RULE_pivot_aggregate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1343);
			set_function_type();
			setState(1344);
			match(LEFT_PAREN);
			setState(1345);
			relational_modifier_value_column();
			setState(1346);
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
		enterRule(_localctx, 218, RULE_table_function_primary);
		try {
			setState(1354);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TABLE:
				enterOuterAlt(_localctx, 1);
				{
				setState(1348);
				match(TABLE);
				setState(1349);
				match(LEFT_PAREN);
				setState(1350);
				table_function();
				setState(1351);
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
			case DELETE:
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
			case FOR:
			case INCLUDE:
			case EXCLUDE:
			case UNPIVOT:
			case PIVOT:
			case Identifier:
			case Double_Quoted_Numeric_Identifier:
			case Dollar_Sign_Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(1353);
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
		enterRule(_localctx, 220, RULE_table_function);
		try {
			setState(1362);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,112,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1356);
				flatten_table_function();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1357);
				generator_table_function();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1358);
				result_scan_table_function();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1359);
				infer_schema_table_function();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1360);
				validate_table_function();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1361);
				generic_table_function();
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
		enterRule(_localctx, 222, RULE_flatten_table_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1364);
			flatten_function_name();
			setState(1365);
			match(LEFT_PAREN);
			setState(1366);
			flatten_argument_list();
			setState(1367);
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
		enterRule(_localctx, 224, RULE_flatten_argument_list);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1369);
			flatten_argument();
			setState(1374);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,113,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1370);
					match(COMMA);
					setState(1371);
					flatten_argument();
					}
					} 
				}
				setState(1376);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,113,_ctx);
			}
			setState(1377);
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
		enterRule(_localctx, 226, RULE_flatten_argument);
		try {
			setState(1396);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INPUT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1379);
				match(INPUT);
				setState(1380);
				match(IMPLIES);
				setState(1381);
				flatten_argument_value();
				}
				break;
			case PATH:
				enterOuterAlt(_localctx, 2);
				{
				setState(1382);
				match(PATH);
				setState(1383);
				match(IMPLIES);
				setState(1384);
				flatten_argument_value();
				}
				break;
			case OUTER:
				enterOuterAlt(_localctx, 3);
				{
				setState(1385);
				match(OUTER);
				setState(1386);
				match(IMPLIES);
				setState(1387);
				flatten_argument_value();
				}
				break;
			case RECURSIVE:
				enterOuterAlt(_localctx, 4);
				{
				setState(1388);
				match(RECURSIVE);
				setState(1389);
				match(IMPLIES);
				setState(1390);
				flatten_argument_value();
				}
				break;
			case MODE:
				enterOuterAlt(_localctx, 5);
				{
				setState(1391);
				match(MODE);
				setState(1392);
				match(IMPLIES);
				setState(1393);
				((Flatten_argumentContext)_localctx).m = table_argument_literal();
				setState(1394);
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
		enterRule(_localctx, 228, RULE_flatten_argument_value);
		try {
			setState(1401);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,115,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1398);
				value_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1399);
				table_argument_literal();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1400);
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
		enterRule(_localctx, 230, RULE_flatten_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1403);
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
		enterRule(_localctx, 232, RULE_table_argument_literal);
		try {
			setState(1407);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Character_String_Literal:
				enterOuterAlt(_localctx, 1);
				{
				setState(1405);
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
				setState(1406);
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
		enterRule(_localctx, 234, RULE_table_argument_boolean);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1409);
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
		enterRule(_localctx, 236, RULE_generator_table_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1411);
			generator_function_name();
			setState(1412);
			match(LEFT_PAREN);
			setState(1413);
			generator_argument_list();
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
		enterRule(_localctx, 238, RULE_generator_argument_list);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1416);
			generator_argument();
			setState(1421);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,117,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1417);
					match(COMMA);
					setState(1418);
					generator_argument();
					}
					} 
				}
				setState(1423);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,117,_ctx);
			}
			setState(1424);
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
		enterRule(_localctx, 240, RULE_generator_argument);
		try {
			setState(1432);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ROWCOUNT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1426);
				match(ROWCOUNT);
				setState(1427);
				match(IMPLIES);
				setState(1428);
				generator_argument_value();
				}
				break;
			case TIMELIMIT:
				enterOuterAlt(_localctx, 2);
				{
				setState(1429);
				match(TIMELIMIT);
				setState(1430);
				match(IMPLIES);
				setState(1431);
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
		enterRule(_localctx, 242, RULE_generator_argument_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1434);
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
		enterRule(_localctx, 244, RULE_generator_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1436);
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
		enterRule(_localctx, 246, RULE_result_scan_table_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1438);
			result_scan_function_name();
			setState(1439);
			match(LEFT_PAREN);
			setState(1441);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -848827245298417264L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -288230377259008257L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -2097169L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -162133984631980033L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 8947490635775L) != 0) || ((((_la - 325)) & ~0x3f) == 0 && ((1L << (_la - 325)) & -7277817066548101649L) != 0)) {
				{
				setState(1440);
				value_expression();
				}
			}

			setState(1443);
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
		enterRule(_localctx, 248, RULE_result_scan_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1445);
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
		enterRule(_localctx, 250, RULE_infer_schema_table_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1447);
			infer_schema_function_name();
			setState(1448);
			match(LEFT_PAREN);
			setState(1449);
			infer_schema_argument_list();
			setState(1450);
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
		enterRule(_localctx, 252, RULE_infer_schema_argument_list);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1452);
			infer_schema_argument();
			setState(1457);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,120,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1453);
					match(COMMA);
					setState(1454);
					infer_schema_argument();
					}
					} 
				}
				setState(1459);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,120,_ctx);
			}
			setState(1460);
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
		enterRule(_localctx, 254, RULE_infer_schema_argument);
		try {
			setState(1483);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LOCATION:
				enterOuterAlt(_localctx, 1);
				{
				setState(1462);
				match(LOCATION);
				setState(1463);
				match(IMPLIES);
				setState(1464);
				infer_schema_argument_value();
				}
				break;
			case FILE_FORMAT:
				enterOuterAlt(_localctx, 2);
				{
				setState(1465);
				match(FILE_FORMAT);
				setState(1466);
				match(IMPLIES);
				setState(1467);
				infer_schema_argument_value();
				}
				break;
			case FILES:
				enterOuterAlt(_localctx, 3);
				{
				setState(1468);
				match(FILES);
				setState(1469);
				match(IMPLIES);
				setState(1470);
				infer_schema_argument_value();
				}
				break;
			case IGNORE_CASE:
				enterOuterAlt(_localctx, 4);
				{
				setState(1471);
				match(IGNORE_CASE);
				setState(1472);
				match(IMPLIES);
				setState(1473);
				infer_schema_argument_value();
				}
				break;
			case MAX_FILE_COUNT:
				enterOuterAlt(_localctx, 5);
				{
				setState(1474);
				match(MAX_FILE_COUNT);
				setState(1475);
				match(IMPLIES);
				setState(1476);
				infer_schema_argument_value();
				}
				break;
			case MAX_RECORDS_PER_FILE:
				enterOuterAlt(_localctx, 6);
				{
				setState(1477);
				match(MAX_RECORDS_PER_FILE);
				setState(1478);
				match(IMPLIES);
				setState(1479);
				infer_schema_argument_value();
				}
				break;
			case KIND:
				enterOuterAlt(_localctx, 7);
				{
				setState(1480);
				match(KIND);
				setState(1481);
				match(IMPLIES);
				setState(1482);
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
		enterRule(_localctx, 256, RULE_infer_schema_argument_value);
		try {
			setState(1489);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,122,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1485);
				table_argument_literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1486);
				infer_schema_files_argument();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1487);
				additive_expression();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1488);
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
		enterRule(_localctx, 258, RULE_infer_schema_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1491);
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
		enterRule(_localctx, 260, RULE_infer_schema_files_argument);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1493);
			match(LEFT_PAREN);
			setState(1494);
			match(Character_String_Literal);
			setState(1499);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1495);
				match(COMMA);
				setState(1496);
				match(Character_String_Literal);
				}
				}
				setState(1501);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1502);
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
		enterRule(_localctx, 262, RULE_validate_table_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1504);
			validate_function_name();
			setState(1505);
			match(LEFT_PAREN);
			setState(1506);
			db_object_name();
			setState(1507);
			match(COMMA);
			setState(1508);
			match(JOB_ID);
			setState(1509);
			match(IMPLIES);
			setState(1510);
			table_argument_literal();
			setState(1511);
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
		enterRule(_localctx, 264, RULE_validate_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1513);
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
		enterRule(_localctx, 266, RULE_generic_table_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1515);
			table_function_name();
			setState(1516);
			match(LEFT_PAREN);
			setState(1518);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -848827245298417264L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -288230377259008257L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -2097169L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -162133984631980033L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 8947490635775L) != 0) || ((((_la - 325)) & ~0x3f) == 0 && ((1L << (_la - 325)) & -7277817066548101649L) != 0)) {
				{
				setState(1517);
				table_function_argument_list();
				}
			}

			setState(1520);
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
		enterRule(_localctx, 268, RULE_table_function_name);
		try {
			setState(1526);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,125,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1522);
				match(SPLIT_TO_TABLE);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1523);
				match(STRTOK_SPLIT_TO_TABLE);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1524);
				match(QUERY_HISTORY);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1525);
				identifier();
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
		enterRule(_localctx, 270, RULE_table_function_argument_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1528);
			value_expression();
			setState(1533);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1529);
				match(COMMA);
				setState(1530);
				value_expression();
				}
				}
				setState(1535);
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
		enterRule(_localctx, 272, RULE_column_reference_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1536);
			column_reference();
			setState(1541);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1537);
				match(COMMA);
				setState(1538);
				column_reference();
				}
				}
				setState(1543);
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
		enterRule(_localctx, 274, RULE_column_reference);
		try {
			int _alt;
			setState(1561);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,130,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1547);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,128,_ctx) ) {
				case 1:
					{
					setState(1544);
					((Column_referenceContext)_localctx).tb_name = identifier();
					setState(1545);
					match(DOT);
					}
					break;
				}
				setState(1549);
				((Column_referenceContext)_localctx).name = identifier();
				setState(1554);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,129,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1550);
						match(COLON);
						setState(1551);
						((Column_referenceContext)_localctx).identifier = identifier();
						((Column_referenceContext)_localctx).path_name.add(((Column_referenceContext)_localctx).identifier);
						}
						} 
					}
					setState(1556);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,129,_ctx);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1557);
				((Column_referenceContext)_localctx).tb_name = identifier();
				setState(1558);
				match(DOT);
				setState(1559);
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
		enterRule(_localctx, 276, RULE_column_primary);
		int _la;
		try {
			setState(1581);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,133,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1566);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,131,_ctx) ) {
				case 1:
					{
					setState(1563);
					((Column_primaryContext)_localctx).tb_name = identifier();
					setState(1564);
					match(DOT);
					}
					break;
				}
				setState(1568);
				((Column_primaryContext)_localctx).name = identifier();
				setState(1573);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COLON) {
					{
					{
					setState(1569);
					match(COLON);
					setState(1570);
					((Column_primaryContext)_localctx).identifier = identifier();
					((Column_primaryContext)_localctx).path_name.add(((Column_primaryContext)_localctx).identifier);
					}
					}
					setState(1575);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1576);
				((Column_primaryContext)_localctx).tb_name = identifier();
				setState(1577);
				match(DOT);
				setState(1578);
				((Column_primaryContext)_localctx).substitution = variable_identifier();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1580);
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
		enterRule(_localctx, 278, RULE_predicand_primary);
		try {
			setState(1593);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,134,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1583);
				value_expression_primary();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1584);
				string_value_expression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1585);
				sign();
				setState(1586);
				numeric_primary();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1588);
				trim_function();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1589);
				null_literal();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1590);
				variable_identifier();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1591);
				puml_constant_identifier();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1592);
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
		public List<TerminalNode> CAST_OPERATOR() { return getTokens(SQLSelectParserParser.CAST_OPERATOR); }
		public TerminalNode CAST_OPERATOR(int i) {
			return getToken(SQLSelectParserParser.CAST_OPERATOR, i);
		}
		public List<Data_typeContext> data_type() {
			return getRuleContexts(Data_typeContext.class);
		}
		public Data_typeContext data_type(int i) {
			return getRuleContext(Data_typeContext.class,i);
		}
		public Nonparenthesized_value_expression_primaryContext nonparenthesized_value_expression_primary() {
			return getRuleContext(Nonparenthesized_value_expression_primaryContext.class,0);
		}
		public Null_literalContext null_literal() {
			return getRuleContext(Null_literalContext.class,0);
		}
		public Value_expression_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value_expression_primary; }
	}

	public final Value_expression_primaryContext value_expression_primary() throws RecognitionException {
		Value_expression_primaryContext _localctx = new Value_expression_primaryContext(_ctx, getState());
		enterRule(_localctx, 280, RULE_value_expression_primary);
		try {
			int _alt;
			setState(1615);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,137,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1595);
				parenthesized_value_expression();
				setState(1600);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,135,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1596);
						match(CAST_OPERATOR);
						setState(1597);
						data_type();
						}
						} 
					}
					setState(1602);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,135,_ctx);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1603);
				nonparenthesized_value_expression_primary();
				setState(1608);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,136,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1604);
						match(CAST_OPERATOR);
						setState(1605);
						data_type();
						}
						} 
					}
					setState(1610);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,136,_ctx);
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1611);
				null_literal();
				setState(1612);
				match(CAST_OPERATOR);
				setState(1613);
				data_type();
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
		enterRule(_localctx, 282, RULE_parenthesized_value_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1617);
			match(LEFT_PAREN);
			setState(1618);
			value_expression();
			setState(1619);
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
		enterRule(_localctx, 284, RULE_nonparenthesized_value_expression_primary);
		try {
			setState(1630);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,138,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1621);
				unsigned_literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1622);
				column_reference();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1623);
				aggregate_function();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1624);
				case_expression();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1625);
				cast_function_expression();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1626);
				routine_invocation();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1627);
				position_function();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1628);
				window_over_partition_expression();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(1629);
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
		enterRule(_localctx, 286, RULE_predicand_subquery);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1632);
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
		enterRule(_localctx, 288, RULE_aggregate_function);
		int _la;
		try {
			setState(1650);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,141,_ctx) ) {
			case 1:
				_localctx = new Count_all_aggregateContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(1634);
				match(COUNT);
				setState(1635);
				match(LEFT_PAREN);
				setState(1636);
				wildcard_reference();
				setState(1637);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				_localctx = new General_set_functionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(1641);
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
				case FLATTEN:
				case SPLIT_TO_TABLE:
				case STRTOK_SPLIT_TO_TABLE:
				case GENERATOR:
				case INFER_SCHEMA:
				case VALIDATE:
				case RESULT_SCAN:
				case QUERY_HISTORY:
					{
					setState(1639);
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
					setState(1640);
					set_qualifier_type();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(1643);
				match(LEFT_PAREN);
				setState(1645);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ALL || _la==DISTINCT) {
					{
					setState(1644);
					set_qualifier();
					}
				}

				setState(1647);
				value_expression();
				setState(1648);
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
		public TerminalNode FLATTEN() { return getToken(SQLSelectParserParser.FLATTEN, 0); }
		public TerminalNode GENERATOR() { return getToken(SQLSelectParserParser.GENERATOR, 0); }
		public TerminalNode INFER_SCHEMA() { return getToken(SQLSelectParserParser.INFER_SCHEMA, 0); }
		public TerminalNode VALIDATE() { return getToken(SQLSelectParserParser.VALIDATE, 0); }
		public TerminalNode RESULT_SCAN() { return getToken(SQLSelectParserParser.RESULT_SCAN, 0); }
		public TerminalNode SPLIT_TO_TABLE() { return getToken(SQLSelectParserParser.SPLIT_TO_TABLE, 0); }
		public TerminalNode STRTOK_SPLIT_TO_TABLE() { return getToken(SQLSelectParserParser.STRTOK_SPLIT_TO_TABLE, 0); }
		public TerminalNode QUERY_HISTORY() { return getToken(SQLSelectParserParser.QUERY_HISTORY, 0); }
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
		enterRule(_localctx, 290, RULE_set_function_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1652);
			_la = _input.LA(1);
			if ( !(((((_la - 61)) & ~0x3f) == 0 && ((1L << (_la - 61)) & 38358112536625921L) != 0) || ((((_la - 127)) & ~0x3f) == 0 && ((1L << (_la - 127)) & -2046809071L) != 0) || ((((_la - 191)) & ~0x3f) == 0 && ((1L << (_la - 191)) & 32767L) != 0) || ((((_la - 338)) & ~0x3f) == 0 && ((1L << (_la - 338)) & 255L) != 0)) ) {
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
		enterRule(_localctx, 292, RULE_set_qualifier_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1654);
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
		enterRule(_localctx, 294, RULE_case_expression);
		int _la;
		try {
			setState(1671);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,144,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1656);
				match(CASE);
				setState(1657);
				value_expression();
				setState(1658);
				when_value_list();
				setState(1660);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSE) {
					{
					setState(1659);
					else_clause();
					}
				}

				setState(1662);
				match(END);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1664);
				match(CASE);
				setState(1665);
				when_clause_list();
				setState(1667);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSE) {
					{
					setState(1666);
					else_clause();
					}
				}

				setState(1669);
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
		enterRule(_localctx, 296, RULE_when_clause_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1674); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(1673);
				searched_when_clause();
				}
				}
				setState(1676); 
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
		enterRule(_localctx, 298, RULE_searched_when_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1678);
			match(WHEN);
			setState(1679);
			((Searched_when_clauseContext)_localctx).c = value_expression();
			setState(1680);
			match(THEN);
			setState(1681);
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
		enterRule(_localctx, 300, RULE_when_value_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1684); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(1683);
				when_value_clause();
				}
				}
				setState(1686); 
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
		enterRule(_localctx, 302, RULE_when_value_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1688);
			match(WHEN);
			setState(1689);
			((When_value_clauseContext)_localctx).c = value_expression();
			setState(1690);
			match(THEN);
			setState(1691);
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
		enterRule(_localctx, 304, RULE_else_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1693);
			match(ELSE);
			setState(1694);
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
		enterRule(_localctx, 306, RULE_case_result);
		try {
			setState(1698);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,147,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1696);
				value_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1697);
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
		enterRule(_localctx, 308, RULE_null_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1700);
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
		enterRule(_localctx, 310, RULE_cast_function_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1702);
			cast_function_name();
			setState(1703);
			match(LEFT_PAREN);
			setState(1704);
			value_expression();
			setState(1705);
			match(AS);
			setState(1706);
			data_type();
			setState(1707);
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
		enterRule(_localctx, 312, RULE_cast_function_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1709);
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
		enterRule(_localctx, 314, RULE_window_over_partition_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1711);
			window_function();
			setState(1712);
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
		enterRule(_localctx, 316, RULE_window_function);
		int _la;
		try {
			setState(1731);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,151,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1714);
				set_function_type();
				setState(1715);
				match(LEFT_PAREN);
				setState(1717);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -848827245298417264L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -288230377259008257L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -2097169L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -162133984631980033L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 8947490635775L) != 0) || ((((_la - 325)) & ~0x3f) == 0 && ((1L << (_la - 325)) & -7277817066548101649L) != 0)) {
					{
					setState(1716);
					sql_argument_list();
					}
				}

				setState(1719);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1721);
				item_select_function();
				setState(1722);
				match(LEFT_PAREN);
				setState(1723);
				sql_argument_list();
				setState(1724);
				match(RIGHT_PAREN);
				setState(1729);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 4398047690752L) != 0)) {
					{
					setState(1726);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==FROM) {
						{
						setState(1725);
						select_direction();
						}
					}

					setState(1728);
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
		enterRule(_localctx, 318, RULE_over_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1733);
			match(OVER);
			setState(1734);
			match(LEFT_PAREN);
			{
			setState(1736);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PARTITION) {
				{
				setState(1735);
				partition_by_clause();
				}
			}

			setState(1739);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ORDER) {
				{
				setState(1738);
				orderby_clause();
				}
			}

			setState(1742);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==RANGE || _la==ROWS) {
				{
				setState(1741);
				bracket_frame_clause();
				}
			}

			}
			setState(1744);
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
		enterRule(_localctx, 320, RULE_partition_by_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1746);
			match(PARTITION);
			setState(1747);
			match(BY);
			setState(1748);
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
		enterRule(_localctx, 322, RULE_bracket_frame_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1750);
			rows_or_range();
			setState(1751);
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
		enterRule(_localctx, 324, RULE_rows_or_range);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1753);
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
		enterRule(_localctx, 326, RULE_bracket_frame_definition);
		try {
			setState(1758);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BETWEEN:
				enterOuterAlt(_localctx, 1);
				{
				setState(1755);
				between_frame_definition();
				}
				break;
			case UNBOUNDED:
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(1756);
				preceding_frame_edge();
				}
				break;
			case CURRENT:
				enterOuterAlt(_localctx, 3);
				{
				setState(1757);
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
		enterRule(_localctx, 328, RULE_between_frame_definition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1760);
			match(BETWEEN);
			setState(1761);
			frame_edge();
			setState(1762);
			match(AND);
			setState(1763);
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
		enterRule(_localctx, 330, RULE_frame_edge);
		try {
			setState(1768);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,156,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1765);
				preceding_frame_edge();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1766);
				following_frame_edge();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1767);
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
		enterRule(_localctx, 332, RULE_preceding_frame_edge);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1770);
			bracket_constraint();
			setState(1771);
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
		enterRule(_localctx, 334, RULE_following_frame_edge);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1773);
			bracket_constraint();
			setState(1774);
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
		enterRule(_localctx, 336, RULE_current_row_edge);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1776);
			match(CURRENT);
			setState(1777);
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
		enterRule(_localctx, 338, RULE_bracket_constraint);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1779);
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
		enterRule(_localctx, 340, RULE_item_select_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1781);
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
		enterRule(_localctx, 342, RULE_select_direction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1783);
			match(FROM);
			setState(1784);
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
		enterRule(_localctx, 344, RULE_null_handling);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1786);
			_la = _input.LA(1);
			if ( !(_la==IGNORE || _la==RESPECT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(1787);
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
		enterRule(_localctx, 346, RULE_value_expression);
		try {
			setState(1793);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,157,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1789);
				common_value_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1790);
				row_value_expression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1791);
				variable_identifier();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1792);
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
		enterRule(_localctx, 348, RULE_common_value_expression);
		try {
			setState(1798);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,158,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1795);
				additive_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1796);
				string_value_expression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1797);
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
		enterRule(_localctx, 350, RULE_additive_expression);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(1800);
			((Additive_expressionContext)_localctx).left = multiplicative_expression();
			setState(1805);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,159,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1801);
					_la = _input.LA(1);
					if ( !(_la==PLUS || _la==MINUS) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(1802);
					((Additive_expressionContext)_localctx).right = multiplicative_expression();
					}
					} 
				}
				setState(1807);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,159,_ctx);
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
		enterRule(_localctx, 352, RULE_multiplicative_expression);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1808);
			((Multiplicative_expressionContext)_localctx).left = factor();
			setState(1813);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,160,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1809);
					_la = _input.LA(1);
					if ( !(((((_la - 290)) & ~0x3f) == 0 && ((1L << (_la - 290)) & 7L) != 0)) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(1810);
					((Multiplicative_expressionContext)_localctx).right = factor();
					}
					} 
				}
				setState(1815);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,160,_ctx);
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
		enterRule(_localctx, 354, RULE_factor);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1817);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PLUS || _la==MINUS) {
				{
				setState(1816);
				sign();
				}
			}

			setState(1819);
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
		enterRule(_localctx, 356, RULE_numeric_primary);
		try {
			setState(1823);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,162,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1821);
				value_expression_primary();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1822);
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
		enterRule(_localctx, 358, RULE_sign);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1825);
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
		enterRule(_localctx, 360, RULE_extract_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1827);
			match(EXTRACT);
			setState(1828);
			match(LEFT_PAREN);
			setState(1829);
			((Extract_expressionContext)_localctx).extract_field_string = extract_field();
			setState(1830);
			match(FROM);
			setState(1831);
			extract_source();
			setState(1832);
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
		enterRule(_localctx, 362, RULE_extract_field);
		try {
			setState(1837);
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
				setState(1834);
				primary_datetime_field();
				}
				break;
			case TIMEZONE:
			case TIMEZONE_HOUR:
			case TIMEZONE_MINUTE:
				enterOuterAlt(_localctx, 2);
				{
				setState(1835);
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
				setState(1836);
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
		enterRule(_localctx, 364, RULE_time_zone_field);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1839);
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
		enterRule(_localctx, 366, RULE_extract_source);
		try {
			setState(1843);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,164,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1841);
				column_reference();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1842);
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
		enterRule(_localctx, 368, RULE_string_value_expression);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1845);
			character_primary();
			setState(1850);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,165,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1846);
					match(CONCATENATION_OPERATOR);
					setState(1847);
					character_primary();
					}
					} 
				}
				setState(1852);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,165,_ctx);
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
		enterRule(_localctx, 370, RULE_character_primary);
		try {
			setState(1855);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,166,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1853);
				value_expression_primary();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1854);
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
		public TerminalNode CAST_OPERATOR() { return getToken(SQLSelectParserParser.CAST_OPERATOR, 0); }
		public Data_typeContext data_type() {
			return getRuleContext(Data_typeContext.class,0);
		}
		public Trim_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_trim_function; }
	}

	public final Trim_functionContext trim_function() throws RecognitionException {
		Trim_functionContext _localctx = new Trim_functionContext(_ctx, getState());
		enterRule(_localctx, 372, RULE_trim_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1857);
			trim_function_name();
			setState(1858);
			match(LEFT_PAREN);
			setState(1859);
			trim_operands();
			setState(1860);
			match(RIGHT_PAREN);
			setState(1863);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,167,_ctx) ) {
			case 1:
				{
				setState(1861);
				match(CAST_OPERATOR);
				setState(1862);
				data_type();
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
	public static class Trim_function_nameContext extends ParserRuleContext {
		public TerminalNode TRIM() { return getToken(SQLSelectParserParser.TRIM, 0); }
		public Trim_function_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_trim_function_name; }
	}

	public final Trim_function_nameContext trim_function_name() throws RecognitionException {
		Trim_function_nameContext _localctx = new Trim_function_nameContext(_ctx, getState());
		enterRule(_localctx, 374, RULE_trim_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1865);
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
		enterRule(_localctx, 376, RULE_trim_operands);
		int _la;
		try {
			setState(1881);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,171,_ctx) ) {
			case 1:
				_localctx = new Mysql_trim_operandsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(1874);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,170,_ctx) ) {
				case 1:
					{
					setState(1868);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1125900175278144L) != 0)) {
						{
						setState(1867);
						trim_specification();
						}
					}

					setState(1871);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -848827253888351856L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -288230377259008257L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -2097169L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -162133984631980033L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 8934605733887L) != 0) || ((((_la - 325)) & ~0x3f) == 0 && ((1L << (_la - 325)) & -7277817066548101663L) != 0)) {
						{
						setState(1870);
						((Mysql_trim_operandsContext)_localctx).trim_character = string_value_expression();
						}
					}

					setState(1873);
					match(FROM);
					}
					break;
				}
				setState(1876);
				((Mysql_trim_operandsContext)_localctx).trim_source = value_expression();
				}
				break;
			case 2:
				_localctx = new Other_trim_operandsContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(1877);
				((Other_trim_operandsContext)_localctx).trim_source = value_expression();
				setState(1878);
				match(COMMA);
				setState(1879);
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
		enterRule(_localctx, 378, RULE_trim_specification);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1883);
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
		enterRule(_localctx, 380, RULE_position_function);
		int _la;
		try {
			setState(1907);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,174,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1885);
				position_function_name();
				setState(1886);
				match(LEFT_PAREN);
				setState(1887);
				((Position_functionContext)_localctx).search_string = string_value_expression();
				setState(1888);
				match(IN);
				setState(1889);
				((Position_functionContext)_localctx).source_string = string_value_expression();
				setState(1890);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1895);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case POSITION:
					{
					setState(1892);
					position_function_name();
					}
					break;
				case INSTR:
					{
					setState(1893);
					instr_function_name();
					}
					break;
				case CHARINDEX:
					{
					setState(1894);
					charindex_name();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(1897);
				match(LEFT_PAREN);
				setState(1898);
				((Position_functionContext)_localctx).search_string = string_value_expression();
				setState(1899);
				match(COMMA);
				setState(1900);
				((Position_functionContext)_localctx).source_string = string_value_expression();
				setState(1903);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(1901);
					match(COMMA);
					setState(1902);
					((Position_functionContext)_localctx).start_position = numeric_primary();
					}
				}

				setState(1905);
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
		enterRule(_localctx, 382, RULE_position_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1909);
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
		enterRule(_localctx, 384, RULE_instr_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1911);
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
		enterRule(_localctx, 386, RULE_charindex_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1913);
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
		enterRule(_localctx, 388, RULE_boolean_value_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1915);
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
		enterRule(_localctx, 390, RULE_or_predicate);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1917);
			and_predicate();
			setState(1922);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,175,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1918);
					match(OR);
					setState(1919);
					and_predicate();
					}
					} 
				}
				setState(1924);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,175,_ctx);
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
		enterRule(_localctx, 392, RULE_and_predicate);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1925);
			negative_predicate();
			setState(1930);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,176,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1926);
					match(AND);
					setState(1927);
					negative_predicate();
					}
					} 
				}
				setState(1932);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,176,_ctx);
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
		enterRule(_localctx, 394, RULE_negative_predicate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1934);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(1933);
				not();
				}
			}

			setState(1936);
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
		enterRule(_localctx, 396, RULE_parenthetical_predicate);
		try {
			setState(1946);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,179,_ctx) ) {
			case 1:
				_localctx = new Paren_clauseContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(1938);
				match(LEFT_PAREN);
				setState(1939);
				boolean_value_expression();
				setState(1940);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				_localctx = new Basic_predicate_clauseContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(1942);
				boolean_primary();
				setState(1944);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,178,_ctx) ) {
				case 1:
					{
					setState(1943);
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
		enterRule(_localctx, 398, RULE_boolean_primary);
		try {
			setState(1950);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,180,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1948);
				predicate();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1949);
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
		enterRule(_localctx, 400, RULE_predicate);
		try {
			setState(1959);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,181,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1952);
				comparison_predicate();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1953);
				between_predicate();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1954);
				in_predicate();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1955);
				like_any_predicate();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1956);
				null_predicate();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1957);
				exists_predicate();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1958);
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
		enterRule(_localctx, 402, RULE_substitution_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1961);
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
		enterRule(_localctx, 404, RULE_row_value_expression);
		try {
			setState(1965);
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
			case DELETE:
			case IFF:
			case MD5:
			case REVERSE:
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
			case FOR:
			case INCLUDE:
			case EXCLUDE:
			case UNPIVOT:
			case PIVOT:
			case Identifier:
			case Scientific_Numeric_Literal:
			case Double_Quoted_Numeric_Identifier:
			case Dollar_Sign_Identifier:
			case Character_String_Literal:
				enterOuterAlt(_localctx, 1);
				{
				setState(1963);
				nonparenthesized_value_expression_primary();
				}
				break;
			case NULL:
				enterOuterAlt(_localctx, 2);
				{
				setState(1964);
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
		enterRule(_localctx, 406, RULE_row_value_predicand);
		try {
			setState(1970);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,183,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1967);
				nonparenthesized_value_expression_primary();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1968);
				common_value_expression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1969);
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
		enterRule(_localctx, 408, RULE_where_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1972);
			match(WHERE);
			setState(1973);
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
		enterRule(_localctx, 410, RULE_search_condition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1975);
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
		enterRule(_localctx, 412, RULE_orderby_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1977);
			match(ORDER);
			setState(1978);
			match(BY);
			setState(1979);
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
		enterRule(_localctx, 414, RULE_sort_specifier_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1981);
			sort_specifier();
			setState(1986);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1982);
				match(COMMA);
				setState(1983);
				sort_specifier();
				}
				}
				setState(1988);
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
		enterRule(_localctx, 416, RULE_sort_specifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1989);
			((Sort_specifierContext)_localctx).key = row_value_predicand();
			setState(1991);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ASC || _la==DESC) {
				{
				setState(1990);
				((Sort_specifierContext)_localctx).order = order_specification();
				}
			}

			setState(1994);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NULLS) {
				{
				setState(1993);
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
		enterRule(_localctx, 418, RULE_order_specification);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1996);
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
		enterRule(_localctx, 420, RULE_null_ordering);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1998);
			match(NULLS);
			setState(1999);
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
		enterRule(_localctx, 422, RULE_null_first_last);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2001);
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
		enterRule(_localctx, 424, RULE_limit_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2003);
			match(LIMIT);
			setState(2004);
			((Limit_clauseContext)_localctx).e = additive_expression();
			setState(2007);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OFFSET) {
				{
				setState(2005);
				match(OFFSET);
				setState(2006);
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
		enterRule(_localctx, 426, RULE_groupby_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2009);
			match(GROUP);
			setState(2010);
			match(BY);
			setState(2013);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,188,_ctx) ) {
			case 1:
				{
				setState(2011);
				grouping_element_list();
				}
				break;
			case 2:
				{
				setState(2012);
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
		enterRule(_localctx, 428, RULE_grouping_element_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2015);
			grouping_element();
			setState(2020);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(2016);
				match(COMMA);
				setState(2017);
				grouping_element();
				}
				}
				setState(2022);
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
		enterRule(_localctx, 430, RULE_grouping_element);
		try {
			setState(2027);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,190,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2023);
				rollup_list();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2024);
				cube_list();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2025);
				empty_grouping_set();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2026);
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
		enterRule(_localctx, 432, RULE_ordinary_grouping_set_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2029);
			ordinary_grouping_set();
			setState(2034);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(2030);
				match(COMMA);
				setState(2031);
				ordinary_grouping_set();
				}
				}
				setState(2036);
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
		enterRule(_localctx, 434, RULE_ordinary_grouping_set);
		try {
			setState(2042);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,192,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2037);
				row_value_predicand();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2038);
				match(LEFT_PAREN);
				setState(2039);
				row_value_predicand_list();
				setState(2040);
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
		enterRule(_localctx, 436, RULE_rollup_list);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2044);
			match(ROLLUP);
			setState(2045);
			match(LEFT_PAREN);
			setState(2046);
			((Rollup_listContext)_localctx).c = ordinary_grouping_set_list();
			setState(2047);
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
		enterRule(_localctx, 438, RULE_cube_list);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2049);
			match(CUBE);
			setState(2050);
			match(LEFT_PAREN);
			setState(2051);
			((Cube_listContext)_localctx).c = ordinary_grouping_set_list();
			setState(2052);
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
		enterRule(_localctx, 440, RULE_empty_grouping_set);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2054);
			match(LEFT_PAREN);
			setState(2055);
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
		enterRule(_localctx, 442, RULE_having_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2057);
			match(HAVING);
			setState(2058);
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
		enterRule(_localctx, 444, RULE_qualify_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2060);
			match(QUALIFY);
			setState(2061);
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
		enterRule(_localctx, 446, RULE_row_value_predicand_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2063);
			row_value_predicand();
			setState(2068);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(2064);
				match(COMMA);
				setState(2065);
				row_value_predicand();
				}
				}
				setState(2070);
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
		enterRule(_localctx, 448, RULE_comparison_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2071);
			((Comparison_predicateContext)_localctx).left = row_value_predicand();
			setState(2072);
			((Comparison_predicateContext)_localctx).c = comparison_operator();
			setState(2073);
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
		enterRule(_localctx, 450, RULE_comparison_operator);
		int _la;
		try {
			setState(2081);
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
				setState(2075);
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
				setState(2077);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(2076);
					not();
					}
				}

				setState(2079);
				relative_comp_op();
				}
				break;
			case Similar_To:
			case Not_Similar_To:
			case Similar_To_Case_Insensitive:
			case Not_Similar_To_Case_Insensitive:
				enterOuterAlt(_localctx, 3);
				{
				setState(2080);
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
		enterRule(_localctx, 452, RULE_relative_comp_op);
		try {
			setState(2089);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LIKE:
				enterOuterAlt(_localctx, 1);
				{
				setState(2083);
				match(LIKE);
				}
				break;
			case ILIKE:
				enterOuterAlt(_localctx, 2);
				{
				setState(2084);
				match(ILIKE);
				}
				break;
			case SIMILAR:
				enterOuterAlt(_localctx, 3);
				{
				setState(2085);
				match(SIMILAR);
				setState(2086);
				match(TO);
				}
				break;
			case REGEXP:
				enterOuterAlt(_localctx, 4);
				{
				setState(2087);
				match(REGEXP);
				}
				break;
			case RLIKE:
				enterOuterAlt(_localctx, 5);
				{
				setState(2088);
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
		enterRule(_localctx, 454, RULE_similarity_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2091);
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
		enterRule(_localctx, 456, RULE_comp_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2093);
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
		enterRule(_localctx, 458, RULE_between_predicate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2095);
			row_value_predicand();
			setState(2097);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(2096);
				not();
				}
			}

			setState(2099);
			match(BETWEEN);
			setState(2101);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ASYMMETRIC || _la==SYMMETRIC) {
				{
				setState(2100);
				symmetry();
				}
			}

			setState(2103);
			((Between_predicateContext)_localctx).begin = row_value_predicand();
			setState(2104);
			match(AND);
			setState(2105);
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
		enterRule(_localctx, 460, RULE_symmetry);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2107);
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
		enterRule(_localctx, 462, RULE_in_predicate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2109);
			row_value_predicand();
			setState(2111);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(2110);
				not();
				}
			}

			setState(2113);
			match(IN);
			setState(2114);
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
		enterRule(_localctx, 464, RULE_like_any_predicate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2116);
			row_value_predicand();
			setState(2118);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(2117);
				not();
				}
			}

			setState(2120);
			like_any_operator();
			setState(2121);
			in_predicate_value();
			setState(2123);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,201,_ctx) ) {
			case 1:
				{
				setState(2122);
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
		enterRule(_localctx, 466, RULE_like_any_operator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2125);
			_la = _input.LA(1);
			if ( !(_la==ILIKE || _la==LIKE) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(2126);
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
		enterRule(_localctx, 468, RULE_in_predicate_value);
		try {
			setState(2134);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,202,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2128);
				subquery();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2129);
				match(LEFT_PAREN);
				setState(2130);
				in_value_list();
				setState(2131);
				match(RIGHT_PAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2133);
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
		enterRule(_localctx, 470, RULE_in_value_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2136);
			row_value_expression();
			setState(2141);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(2137);
				match(COMMA);
				setState(2138);
				row_value_expression();
				}
				}
				setState(2143);
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
		enterRule(_localctx, 472, RULE_escape_character_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2144);
			match(ESCAPE);
			setState(2145);
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
		enterRule(_localctx, 474, RULE_values_statement_primary);
		try {
			setState(2150);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,204,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2147);
				fully_defined_values_statement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2148);
				aliased_values_statement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2149);
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
		enterRule(_localctx, 476, RULE_fully_defined_values_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2152);
			values_statement();
			setState(2153);
			as_clause();
			setState(2154);
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
		enterRule(_localctx, 478, RULE_aliased_values_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2156);
			values_statement();
			setState(2157);
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
		enterRule(_localctx, 480, RULE_values_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2159);
			match(LEFT_PAREN);
			setState(2160);
			match(VALUES);
			setState(2161);
			values_matrix();
			setState(2162);
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
		enterRule(_localctx, 482, RULE_values_matrix);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2164);
			values_row();
			setState(2169);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(2165);
				match(COMMA);
				setState(2166);
				values_row();
				}
				}
				setState(2171);
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
		enterRule(_localctx, 484, RULE_values_row);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2172);
			match(LEFT_PAREN);
			setState(2173);
			in_value_list();
			setState(2174);
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
		enterRule(_localctx, 486, RULE_values_aliases);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2176);
			match(LEFT_PAREN);
			setState(2177);
			values_aliases_list();
			setState(2178);
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
		enterRule(_localctx, 488, RULE_values_aliases_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2180);
			alias_identifier();
			setState(2185);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(2181);
				match(COMMA);
				setState(2182);
				alias_identifier();
				}
				}
				setState(2187);
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
		enterRule(_localctx, 490, RULE_insert_values_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2188);
			match(VALUES);
			setState(2189);
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
		enterRule(_localctx, 492, RULE_exists_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2191);
			exists_operator();
			setState(2192);
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
		enterRule(_localctx, 494, RULE_exists_operator);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2194);
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
		enterRule(_localctx, 496, RULE_exists_predicate_value);
		try {
			setState(2198);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LEFT_PAREN:
				enterOuterAlt(_localctx, 1);
				{
				setState(2196);
				subquery();
				}
				break;
			case Variable_Identifier:
			case Extended_Variable_Identifier:
			case Mixed_Variable_Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(2197);
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
		enterRule(_localctx, 498, RULE_null_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2200);
			row_value_predicand();
			setState(2201);
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
		enterRule(_localctx, 500, RULE_is_null_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2203);
			match(IS);
			setState(2205);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(2204);
				((Is_null_clauseContext)_localctx).n = match(NOT);
				}
			}

			setState(2207);
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
		enterRule(_localctx, 502, RULE_is_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2209);
			match(IS);
			setState(2211);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(2210);
				not();
				}
			}

			setState(2213);
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
		enterRule(_localctx, 504, RULE_truth_value);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2215);
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
		enterRule(_localctx, 506, RULE_not);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2217);
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
		enterRule(_localctx, 508, RULE_quantified_comparison_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2219);
			((Quantified_comparison_predicateContext)_localctx).l = additive_expression();
			setState(2220);
			((Quantified_comparison_predicateContext)_localctx).c = comp_op();
			setState(2221);
			((Quantified_comparison_predicateContext)_localctx).q = quantifier();
			setState(2222);
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
		enterRule(_localctx, 510, RULE_quantifier);
		try {
			setState(2226);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ALL:
				enterOuterAlt(_localctx, 1);
				{
				setState(2224);
				all();
				}
				break;
			case ANY:
			case SOME:
				enterOuterAlt(_localctx, 2);
				{
				setState(2225);
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
		enterRule(_localctx, 512, RULE_all);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2228);
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
		enterRule(_localctx, 514, RULE_some);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2230);
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
		enterRule(_localctx, 516, RULE_unique_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2232);
			match(UNIQUE);
			setState(2233);
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
		enterRule(_localctx, 518, RULE_primary_datetime_field);
		try {
			setState(2237);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DAY:
			case HOUR:
			case MINUTE:
			case MONTH:
			case YEAR:
				enterOuterAlt(_localctx, 1);
				{
				setState(2235);
				non_second_primary_datetime_field();
				}
				break;
			case SECOND:
				enterOuterAlt(_localctx, 2);
				{
				setState(2236);
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
		enterRule(_localctx, 520, RULE_non_second_primary_datetime_field);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2239);
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
		enterRule(_localctx, 522, RULE_extended_datetime_field);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2241);
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
		enterRule(_localctx, 524, RULE_routine_invocation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2243);
			function_name();
			setState(2244);
			match(LEFT_PAREN);
			setState(2246);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -848827245298417264L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -288230377259008257L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -2097169L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -162133984631980033L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 8947490635775L) != 0) || ((((_la - 325)) & ~0x3f) == 0 && ((1L << (_la - 325)) & -7277817066548101649L) != 0)) {
				{
				setState(2245);
				sql_argument_list();
				}
			}

			setState(2248);
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
		enterRule(_localctx, 526, RULE_function_name);
		int _la;
		try {
			setState(2256);
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
			case DELETE:
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
			case FOR:
			case INCLUDE:
			case EXCLUDE:
			case UNPIVOT:
			case PIVOT:
			case Identifier:
			case Double_Quoted_Numeric_Identifier:
			case Dollar_Sign_Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(2250);
				identifier();
				setState(2253);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOT) {
					{
					setState(2251);
					match(DOT);
					setState(2252);
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
				setState(2255);
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
		enterRule(_localctx, 528, RULE_function_names_for_reserved_words);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2258);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 8796634087424L) != 0) || ((((_la - 335)) & ~0x3f) == 0 && ((1L << (_la - 335)) & 7L) != 0)) ) {
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
		enterRule(_localctx, 530, RULE_sql_argument_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2260);
			value_expression();
			setState(2265);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(2261);
				match(COMMA);
				setState(2262);
				value_expression();
				}
				}
				setState(2267);
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
		enterRule(_localctx, 532, RULE_identifier);
		try {
			setState(2273);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(2268);
				simple_identifier();
				}
				break;
			case Bracket_Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(2269);
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
			case DELETE:
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
			case FOR:
			case INCLUDE:
			case EXCLUDE:
			case UNPIVOT:
			case PIVOT:
				enterOuterAlt(_localctx, 3);
				{
				setState(2270);
				nonreserved_keywords();
				}
				break;
			case Double_Quoted_Numeric_Identifier:
				enterOuterAlt(_localctx, 4);
				{
				setState(2271);
				snowflake_quoted_numeric_identifier();
				}
				break;
			case Dollar_Sign_Identifier:
				enterOuterAlt(_localctx, 5);
				{
				setState(2272);
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
		enterRule(_localctx, 534, RULE_alias_identifier);
		try {
			setState(2280);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(2275);
				simple_identifier();
				}
				break;
			case Bracket_Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(2276);
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
			case DELETE:
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
			case FOR:
			case INCLUDE:
			case EXCLUDE:
			case UNPIVOT:
			case PIVOT:
				enterOuterAlt(_localctx, 3);
				{
				setState(2277);
				nonreserved_keywords();
				}
				break;
			case NUMBER:
			case Numeric_Identifier:
				enterOuterAlt(_localctx, 4);
				{
				setState(2278);
				simple_numeric_identifier();
				}
				break;
			case Double_Quoted_Numeric_Identifier:
				enterOuterAlt(_localctx, 5);
				{
				setState(2279);
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
		enterRule(_localctx, 536, RULE_variable_identifier);
		try {
			setState(2284);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Variable_Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(2282);
				simple_variable_identifier();
				}
				break;
			case Extended_Variable_Identifier:
			case Mixed_Variable_Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(2283);
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
		enterRule(_localctx, 538, RULE_simple_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2286);
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
		enterRule(_localctx, 540, RULE_logical_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2288);
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
		enterRule(_localctx, 542, RULE_simple_variable_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2290);
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
		enterRule(_localctx, 544, RULE_extended_variable_identifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2292);
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
		enterRule(_localctx, 546, RULE_jinja_identifier);
		try {
			setState(2302);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,219,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2294);
				match(JINJA_OPEN);
				setState(2295);
				jinja_function_call();
				setState(2296);
				match(JINJA_CLOSE);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2298);
				match(JINJA_OPEN);
				setState(2299);
				jinja_variable_access();
				setState(2300);
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
		enterRule(_localctx, 548, RULE_jinja_function_call);
		int _la;
		try {
			setState(2322);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,222,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2304);
				((Jinja_function_callContext)_localctx).func_name = identifier();
				setState(2305);
				if (!(isJinjaPrimaryFunction((((Jinja_function_callContext)_localctx).func_name!=null?_input.getText(((Jinja_function_callContext)_localctx).func_name.start,((Jinja_function_callContext)_localctx).func_name.stop):null)))) throw new FailedPredicateException(this, "isJinjaPrimaryFunction($func_name.text)");
				setState(2306);
				match(LEFT_PAREN);
				setState(2308);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -855661835887575040L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -292734320500539713L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -2097169L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -162133984631980033L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 8796093038591L) != 0) || ((((_la - 325)) & ~0x3f) == 0 && ((1L << (_la - 325)) & -7421932254623964927L) != 0)) {
					{
					setState(2307);
					jinja_arg_list();
					}
				}

				setState(2310);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2312);
				((Jinja_function_callContext)_localctx).object_name = identifier();
				setState(2313);
				match(DOT);
				setState(2314);
				((Jinja_function_callContext)_localctx).method_name = identifier();
				setState(2315);
				if (!(isJinjaObjectMethod((((Jinja_function_callContext)_localctx).object_name!=null?_input.getText(((Jinja_function_callContext)_localctx).object_name.start,((Jinja_function_callContext)_localctx).object_name.stop):null), (((Jinja_function_callContext)_localctx).method_name!=null?_input.getText(((Jinja_function_callContext)_localctx).method_name.start,((Jinja_function_callContext)_localctx).method_name.stop):null)))) throw new FailedPredicateException(this, "isJinjaObjectMethod($object_name.text, $method_name.text)");
				setState(2316);
				match(LEFT_PAREN);
				setState(2318);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -855661835887575040L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -292734320500539713L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -2097169L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -162133984631980033L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 8796093038591L) != 0) || ((((_la - 325)) & ~0x3f) == 0 && ((1L << (_la - 325)) & -7421932254623964927L) != 0)) {
					{
					setState(2317);
					jinja_arg_list();
					}
				}

				setState(2320);
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
		enterRule(_localctx, 550, RULE_jinja_arg_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2324);
			jinja_arg();
			setState(2329);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(2325);
				match(COMMA);
				setState(2326);
				jinja_arg();
				}
				}
				setState(2331);
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
		enterRule(_localctx, 552, RULE_jinja_arg);
		try {
			setState(2342);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,224,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2332);
				match(Character_String_Literal);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2333);
				match(NUMBER);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2334);
				((Jinja_argContext)_localctx).kw_name = identifier();
				setState(2335);
				match(EQUAL);
				setState(2336);
				match(Character_String_Literal);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2338);
				((Jinja_argContext)_localctx).kw_name = identifier();
				setState(2339);
				match(EQUAL);
				setState(2340);
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
		enterRule(_localctx, 554, RULE_jinja_variable_access);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2344);
			jinja_name();
			setState(2349);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DOT) {
				{
				{
				setState(2345);
				match(DOT);
				setState(2346);
				jinja_name();
				}
				}
				setState(2351);
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
		enterRule(_localctx, 556, RULE_jinja_name);
		try {
			setState(2354);
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
			case DELETE:
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
			case FOR:
			case INCLUDE:
			case EXCLUDE:
			case UNPIVOT:
			case PIVOT:
			case Identifier:
			case Double_Quoted_Numeric_Identifier:
			case Dollar_Sign_Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(2352);
				identifier();
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(2353);
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
		enterRule(_localctx, 558, RULE_simple_numeric_identifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2356);
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
		enterRule(_localctx, 560, RULE_snowflake_quoted_numeric_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2358);
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
		enterRule(_localctx, 562, RULE_snowflake_dollar_function_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2360);
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
		public TerminalNode DELETE() { return getToken(SQLSelectParserParser.DELETE, 0); }
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
		public TerminalNode FLATTEN() { return getToken(SQLSelectParserParser.FLATTEN, 0); }
		public TerminalNode GENERATOR() { return getToken(SQLSelectParserParser.GENERATOR, 0); }
		public TerminalNode INFER_SCHEMA() { return getToken(SQLSelectParserParser.INFER_SCHEMA, 0); }
		public TerminalNode VALIDATE() { return getToken(SQLSelectParserParser.VALIDATE, 0); }
		public TerminalNode RESULT_SCAN() { return getToken(SQLSelectParserParser.RESULT_SCAN, 0); }
		public TerminalNode SPLIT_TO_TABLE() { return getToken(SQLSelectParserParser.SPLIT_TO_TABLE, 0); }
		public TerminalNode STRTOK_SPLIT_TO_TABLE() { return getToken(SQLSelectParserParser.STRTOK_SPLIT_TO_TABLE, 0); }
		public TerminalNode QUERY_HISTORY() { return getToken(SQLSelectParserParser.QUERY_HISTORY, 0); }
		public TerminalNode PIVOT() { return getToken(SQLSelectParserParser.PIVOT, 0); }
		public TerminalNode UNPIVOT() { return getToken(SQLSelectParserParser.UNPIVOT, 0); }
		public TerminalNode FOR() { return getToken(SQLSelectParserParser.FOR, 0); }
		public TerminalNode INCLUDE() { return getToken(SQLSelectParserParser.INCLUDE, 0); }
		public TerminalNode EXCLUDE() { return getToken(SQLSelectParserParser.EXCLUDE, 0); }
		public Nonreserved_keywordsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nonreserved_keywords; }
	}

	public final Nonreserved_keywordsContext nonreserved_keywords() throws RecognitionException {
		Nonreserved_keywordsContext _localctx = new Nonreserved_keywordsContext(_ctx, getState());
		enterRule(_localctx, 564, RULE_nonreserved_keywords);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2362);
			_la = _input.LA(1);
			if ( !(((((_la - 20)) & ~0x3f) == 0 && ((1L << (_la - 20)) & -5630315556929535L) != 0) || ((((_la - 84)) & ~0x3f) == 0 && ((1L << (_la - 84)) & -281754149913649L) != 0) || ((((_la - 148)) & ~0x3f) == 0 && ((1L << (_la - 148)) & -2305843009213693955L) != 0) || ((((_la - 212)) & ~0x3f) == 0 && ((1L << (_la - 212)) & 288230221528694783L) != 0) || ((((_la - 333)) & ~0x3f) == 0 && ((1L << (_la - 333)) & 140737219928033L) != 0)) ) {
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
		enterRule(_localctx, 566, RULE_signed_numeric_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2365);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PLUS || _la==MINUS) {
				{
				setState(2364);
				sign();
				}
			}

			setState(2367);
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
		enterRule(_localctx, 568, RULE_unsigned_literal);
		try {
			setState(2371);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DOT:
			case NUMBER:
			case Scientific_Numeric_Literal:
				enterOuterAlt(_localctx, 1);
				{
				setState(2369);
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
				setState(2370);
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
		enterRule(_localctx, 570, RULE_unsigned_numeric_literal);
		try {
			setState(2375);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,229,_ctx) ) {
			case 1:
				_localctx = new Ordinal_numberContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(2373);
				match(NUMBER);
				}
				break;
			case 2:
				_localctx = new Real_numberContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(2374);
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
		enterRule(_localctx, 572, RULE_real_number_def);
		try {
			setState(2393);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,233,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2377);
				match(NUMBER);
				setState(2378);
				match(DOT);
				setState(2380);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,230,_ctx) ) {
				case 1:
					{
					setState(2379);
					match(NUMBER);
					}
					break;
				}
				setState(2383);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,231,_ctx) ) {
				case 1:
					{
					setState(2382);
					exponent();
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2385);
				match(DOT);
				setState(2386);
				match(NUMBER);
				setState(2388);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,232,_ctx) ) {
				case 1:
					{
					setState(2387);
					exponent();
					}
					break;
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2390);
				match(NUMBER);
				setState(2391);
				exponent();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2392);
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
		enterRule(_localctx, 574, RULE_exponent);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2395);
			match(EXPONEN);
			setState(2396);
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
		enterRule(_localctx, 576, RULE_general_literal);
		try {
			setState(2401);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Character_String_Literal:
				enterOuterAlt(_localctx, 1);
				{
				setState(2398);
				character_literal();
				}
				break;
			case DATE:
			case TIME:
			case TIMESTAMP:
				enterOuterAlt(_localctx, 2);
				{
				setState(2399);
				datetime_literal();
				}
				break;
			case FALSE:
			case TRUE:
			case UNKNOWN:
				enterOuterAlt(_localctx, 3);
				{
				setState(2400);
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
		enterRule(_localctx, 578, RULE_character_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2403);
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
		enterRule(_localctx, 580, RULE_datetime_literal);
		try {
			setState(2408);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TIMESTAMP:
				enterOuterAlt(_localctx, 1);
				{
				setState(2405);
				timestamp_literal();
				}
				break;
			case TIME:
				enterOuterAlt(_localctx, 2);
				{
				setState(2406);
				time_literal();
				}
				break;
			case DATE:
				enterOuterAlt(_localctx, 3);
				{
				setState(2407);
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
		enterRule(_localctx, 582, RULE_time_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2410);
			match(TIME);
			setState(2411);
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
		enterRule(_localctx, 584, RULE_timestamp_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2413);
			match(TIMESTAMP);
			setState(2414);
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
		enterRule(_localctx, 586, RULE_date_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2416);
			match(DATE);
			setState(2417);
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
		enterRule(_localctx, 588, RULE_boolean_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2419);
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
		enterRule(_localctx, 590, RULE_data_type);
		try {
			setState(2424);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,236,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2421);
				variable_size_data_type();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2422);
				precision_scale_data_type();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2423);
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
		enterRule(_localctx, 592, RULE_variable_size_data_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2426);
			variable_data_type_name();
			setState(2428);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,237,_ctx) ) {
			case 1:
				{
				setState(2427);
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
		enterRule(_localctx, 594, RULE_variable_data_type_name);
		try {
			setState(2464);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,238,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2430);
				match(CHARACTER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2431);
				match(CHAR);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2432);
				match(CHARACTER);
				setState(2433);
				match(VARYING);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2434);
				match(CHAR);
				setState(2435);
				match(VARYING);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2436);
				match(VARCHAR);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2437);
				match(VARCHAR2);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(2438);
				match(NATIONAL);
				setState(2439);
				match(CHARACTER);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(2440);
				match(NATIONAL);
				setState(2441);
				match(CHAR);
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(2442);
				match(NCHAR);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(2443);
				match(NATIONAL);
				setState(2444);
				match(CHARACTER);
				setState(2445);
				match(VARYING);
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(2446);
				match(NATIONAL);
				setState(2447);
				match(CHAR);
				setState(2448);
				match(VARYING);
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(2449);
				match(NCHAR);
				setState(2450);
				match(VARYING);
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(2451);
				match(NVARCHAR);
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(2452);
				match(BLOB);
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				setState(2453);
				match(BYTEA);
				}
				break;
			case 16:
				enterOuterAlt(_localctx, 16);
				{
				setState(2454);
				match(BIT);
				}
				break;
			case 17:
				enterOuterAlt(_localctx, 17);
				{
				setState(2455);
				match(VARBIT);
				}
				break;
			case 18:
				enterOuterAlt(_localctx, 18);
				{
				setState(2456);
				match(BIT);
				setState(2457);
				match(VARYING);
				}
				break;
			case 19:
				enterOuterAlt(_localctx, 19);
				{
				setState(2458);
				match(BINARY);
				}
				break;
			case 20:
				enterOuterAlt(_localctx, 20);
				{
				setState(2459);
				match(BINARY);
				setState(2460);
				match(VARYING);
				}
				break;
			case 21:
				enterOuterAlt(_localctx, 21);
				{
				setState(2461);
				match(VARBINARY);
				}
				break;
			case 22:
				enterOuterAlt(_localctx, 22);
				{
				setState(2462);
				match(INTERVAL);
				}
				break;
			case 23:
				enterOuterAlt(_localctx, 23);
				{
				setState(2463);
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
		enterRule(_localctx, 596, RULE_type_length);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2466);
			match(LEFT_PAREN);
			setState(2467);
			match(NUMBER);
			setState(2468);
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
		enterRule(_localctx, 598, RULE_precision_scale_data_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2470);
			precision_data_type_name();
			setState(2472);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,239,_ctx) ) {
			case 1:
				{
				setState(2471);
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
		enterRule(_localctx, 600, RULE_precision_data_type_name);
		try {
			setState(2485);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,240,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2474);
				match(NUMERIC);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2475);
				match(NUMBER);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2476);
				match(DECIMAL);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2477);
				match(DEC);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2478);
				match(FLOAT);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2479);
				match(DOUBLE);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(2480);
				match(DOUBLE);
				setState(2481);
				match(PRECISION);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(2482);
				match(TIMESTAMP_LTZ);
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(2483);
				match(TIMESTAMP_NTZ);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(2484);
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
		enterRule(_localctx, 602, RULE_precision_param);
		try {
			setState(2495);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,241,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2487);
				match(LEFT_PAREN);
				setState(2488);
				((Precision_paramContext)_localctx).precision = match(NUMBER);
				setState(2489);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2490);
				match(LEFT_PAREN);
				setState(2491);
				((Precision_paramContext)_localctx).precision = match(NUMBER);
				setState(2492);
				match(COMMA);
				setState(2493);
				((Precision_paramContext)_localctx).scale = match(NUMBER);
				setState(2494);
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
		enterRule(_localctx, 604, RULE_static_data_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2497);
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
		enterRule(_localctx, 606, RULE_static_data_type_name);
		try {
			setState(2560);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,242,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2499);
				match(TEXT);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2500);
				match(NAME);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2501);
				match(INET4);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2502);
				match(INET);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2503);
				match(CIDR);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2504);
				match(STRUCT);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(2505);
				match(UNION);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(2506);
				match(VARIANT);
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(2507);
				match(OBJECT);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(2508);
				match(JSON);
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(2509);
				match(JSONB);
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(2510);
				match(OID);
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(2511);
				match(XID);
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(2512);
				match(UUID);
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				setState(2513);
				match(PG_LSN);
				}
				break;
			case 16:
				enterOuterAlt(_localctx, 16);
				{
				setState(2514);
				match(PG_NODE_TREE);
				}
				break;
			case 17:
				enterOuterAlt(_localctx, 17);
				{
				setState(2515);
				match(REGPROC);
				}
				break;
			case 18:
				enterOuterAlt(_localctx, 18);
				{
				setState(2516);
				match(MACADDR);
				}
				break;
			case 19:
				enterOuterAlt(_localctx, 19);
				{
				setState(2517);
				match(INT1);
				}
				break;
			case 20:
				enterOuterAlt(_localctx, 20);
				{
				setState(2518);
				match(TINYINT);
				}
				break;
			case 21:
				enterOuterAlt(_localctx, 21);
				{
				setState(2519);
				match(INT2);
				}
				break;
			case 22:
				enterOuterAlt(_localctx, 22);
				{
				setState(2520);
				match(SMALLINT);
				}
				break;
			case 23:
				enterOuterAlt(_localctx, 23);
				{
				setState(2521);
				match(INT4);
				}
				break;
			case 24:
				enterOuterAlt(_localctx, 24);
				{
				setState(2522);
				match(INT);
				}
				break;
			case 25:
				enterOuterAlt(_localctx, 25);
				{
				setState(2523);
				match(INTEGER);
				}
				break;
			case 26:
				enterOuterAlt(_localctx, 26);
				{
				setState(2524);
				match(INT8);
				}
				break;
			case 27:
				enterOuterAlt(_localctx, 27);
				{
				setState(2525);
				match(BIGINT);
				}
				break;
			case 28:
				enterOuterAlt(_localctx, 28);
				{
				setState(2526);
				match(BIGSERIAL);
				}
				break;
			case 29:
				enterOuterAlt(_localctx, 29);
				{
				setState(2527);
				match(SMALLSERIAL);
				}
				break;
			case 30:
				enterOuterAlt(_localctx, 30);
				{
				setState(2528);
				match(SERIAL);
				}
				break;
			case 31:
				enterOuterAlt(_localctx, 31);
				{
				setState(2529);
				match(MONEY);
				}
				break;
			case 32:
				enterOuterAlt(_localctx, 32);
				{
				setState(2530);
				match(NUMBER_TYPE);
				}
				break;
			case 33:
				enterOuterAlt(_localctx, 33);
				{
				setState(2531);
				match(FLOAT4);
				}
				break;
			case 34:
				enterOuterAlt(_localctx, 34);
				{
				setState(2532);
				match(REAL);
				}
				break;
			case 35:
				enterOuterAlt(_localctx, 35);
				{
				setState(2533);
				match(FLOAT8);
				}
				break;
			case 36:
				enterOuterAlt(_localctx, 36);
				{
				setState(2534);
				match(BOOLEAN);
				}
				break;
			case 37:
				enterOuterAlt(_localctx, 37);
				{
				setState(2535);
				match(BOOL);
				}
				break;
			case 38:
				enterOuterAlt(_localctx, 38);
				{
				setState(2536);
				match(DATE);
				}
				break;
			case 39:
				enterOuterAlt(_localctx, 39);
				{
				setState(2537);
				match(DATETIME);
				}
				break;
			case 40:
				enterOuterAlt(_localctx, 40);
				{
				setState(2538);
				match(TIME);
				}
				break;
			case 41:
				enterOuterAlt(_localctx, 41);
				{
				setState(2539);
				match(TIME);
				setState(2540);
				match(WITH);
				setState(2541);
				match(TIME);
				setState(2542);
				match(ZONE);
				}
				break;
			case 42:
				enterOuterAlt(_localctx, 42);
				{
				setState(2543);
				match(TIMETZ);
				}
				break;
			case 43:
				enterOuterAlt(_localctx, 43);
				{
				setState(2544);
				match(TIMESTAMP_LTZ);
				}
				break;
			case 44:
				enterOuterAlt(_localctx, 44);
				{
				setState(2545);
				match(TIMESTAMP_NTZ);
				}
				break;
			case 45:
				enterOuterAlt(_localctx, 45);
				{
				setState(2546);
				match(TIMESTAMP_TZ);
				}
				break;
			case 46:
				enterOuterAlt(_localctx, 46);
				{
				setState(2547);
				match(TIMESTAMP);
				}
				break;
			case 47:
				enterOuterAlt(_localctx, 47);
				{
				setState(2548);
				match(TIMESTAMP);
				setState(2549);
				match(WITH);
				setState(2550);
				match(TIME);
				setState(2551);
				match(ZONE);
				}
				break;
			case 48:
				enterOuterAlt(_localctx, 48);
				{
				setState(2552);
				match(TIMESTAMP);
				setState(2553);
				match(WITHOUT);
				setState(2554);
				match(TIME);
				setState(2555);
				match(ZONE);
				}
				break;
			case 49:
				enterOuterAlt(_localctx, 49);
				{
				setState(2556);
				match(TIMESTAMPTZ);
				}
				break;
			case 50:
				enterOuterAlt(_localctx, 50);
				{
				setState(2557);
				match(ABSTIME);
				}
				break;
			case 51:
				enterOuterAlt(_localctx, 51);
				{
				setState(2558);
				match(ARRAY);
				}
				break;
			case 52:
				enterOuterAlt(_localctx, 52);
				{
				setState(2559);
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
		enterRule(_localctx, 608, RULE_puml_constant_identifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2562);
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
		case 88:
			return relation_as_clause_sempred((Relation_as_clauseContext)_localctx, predIndex);
		case 112:
			return flatten_argument_list_sempred((Flatten_argument_listContext)_localctx, predIndex);
		case 113:
			return flatten_argument_sempred((Flatten_argumentContext)_localctx, predIndex);
		case 119:
			return generator_argument_list_sempred((Generator_argument_listContext)_localctx, predIndex);
		case 126:
			return infer_schema_argument_list_sempred((Infer_schema_argument_listContext)_localctx, predIndex);
		case 274:
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

	private static final String _serializedATNSegment0 =
		"\u0004\u0001\u0189\u0a05\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
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
		"\u0119\u0002\u011a\u0007\u011a\u0002\u011b\u0007\u011b\u0002\u011c\u0007"+
		"\u011c\u0002\u011d\u0007\u011d\u0002\u011e\u0007\u011e\u0002\u011f\u0007"+
		"\u011f\u0002\u0120\u0007\u0120\u0002\u0121\u0007\u0121\u0002\u0122\u0007"+
		"\u0122\u0002\u0123\u0007\u0123\u0002\u0124\u0007\u0124\u0002\u0125\u0007"+
		"\u0125\u0002\u0126\u0007\u0126\u0002\u0127\u0007\u0127\u0002\u0128\u0007"+
		"\u0128\u0002\u0129\u0007\u0129\u0002\u012a\u0007\u012a\u0002\u012b\u0007"+
		"\u012b\u0002\u012c\u0007\u012c\u0002\u012d\u0007\u012d\u0002\u012e\u0007"+
		"\u012e\u0002\u012f\u0007\u012f\u0002\u0130\u0007\u0130\u0001\u0000\u0005"+
		"\u0000\u0264\b\u0000\n\u0000\f\u0000\u0267\t\u0000\u0001\u0000\u0003\u0000"+
		"\u026a\b\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0003\u0001\u0272\b\u0001\u0001\u0001\u0003\u0001\u0275\b"+
		"\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0003\u0003\u027e\b\u0003\u0001\u0003\u0003\u0003\u0281"+
		"\b\u0003\u0001\u0004\u0001\u0004\u0003\u0004\u0285\b\u0004\u0001\u0004"+
		"\u0003\u0004\u0288\b\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\n\u0001"+
		"\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0003\f"+
		"\u02a3\b\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0003\u0012\u02c4\b\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0003\u0013\u02ca\b\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0003\u0014\u02d0\b\u0014\u0001\u0015\u0001\u0015\u0003\u0015\u02d4\b"+
		"\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0017\u0001"+
		"\u0017\u0003\u0017\u02dc\b\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0005"+
		"\u0017\u02e1\b\u0017\n\u0017\f\u0017\u02e4\t\u0017\u0001\u0018\u0001\u0018"+
		"\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018"+
		"\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0003\u0018"+
		"\u02f3\b\u0018\u0001\u0018\u0003\u0018\u02f6\b\u0018\u0003\u0018\u02f8"+
		"\b\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001"+
		"\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001"+
		"\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001b\u0001\u001b\u0001"+
		"\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001c\u0001"+
		"\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0003\u001c\u0315\b\u001c\u0001"+
		"\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001d\u0001"+
		"\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0003\u001d\u0321\b\u001d\u0001"+
		"\u001d\u0001\u001d\u0001\u001d\u0001\u001e\u0001\u001e\u0001\u001e\u0001"+
		"\u001e\u0001\u001e\u0003\u001e\u032b\b\u001e\u0001\u001e\u0001\u001e\u0001"+
		"\u001e\u0001\u001e\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001"+
		"\u001f\u0001 \u0001 \u0001 \u0001 \u0003 \u033a\b \u0001!\u0001!\u0001"+
		"!\u0001!\u0003!\u0340\b!\u0001\"\u0001\"\u0001\"\u0001\"\u0003\"\u0346"+
		"\b\"\u0001#\u0001#\u0001#\u0001#\u0003#\u034c\b#\u0001$\u0001$\u0001$"+
		"\u0001$\u0003$\u0352\b$\u0001%\u0001%\u0001%\u0001%\u0001%\u0003%\u0359"+
		"\b%\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001"+
		"&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0003&\u036b\b&\u0001\'\u0001"+
		"\'\u0001(\u0001(\u0001)\u0004)\u0372\b)\u000b)\f)\u0373\u0001*\u0004*"+
		"\u0377\b*\u000b*\f*\u0378\u0001+\u0001+\u0001+\u0001,\u0001,\u0001,\u0001"+
		",\u0005,\u0382\b,\n,\f,\u0385\t,\u0001-\u0001-\u0001-\u0001-\u0001-\u0001"+
		"-\u0001-\u0001-\u0003-\u038f\b-\u0001.\u0001.\u0003.\u0393\b.\u0001/\u0001"+
		"/\u0001/\u00010\u00010\u00010\u00010\u00010\u00030\u039d\b0\u00011\u0001"+
		"1\u00012\u00012\u00012\u00012\u00013\u00013\u00013\u00033\u03a8\b3\u0001"+
		"3\u00013\u00013\u00013\u00033\u03ae\b3\u00013\u00033\u03b1\b3\u00014\u0001"+
		"4\u00034\u03b5\b4\u00015\u00015\u00035\u03b9\b5\u00015\u00015\u00016\u0001"+
		"6\u00016\u00036\u03c0\b6\u00017\u00017\u00017\u00017\u00017\u00037\u03c7"+
		"\b7\u00017\u00037\u03ca\b7\u00017\u00037\u03cd\b7\u00018\u00018\u0003"+
		"8\u03d1\b8\u00019\u00019\u00019\u00019\u00039\u03d7\b9\u00019\u00039\u03da"+
		"\b9\u0001:\u0001:\u0001:\u0001:\u0003:\u03e0\b:\u0001:\u0003:\u03e3\b"+
		":\u0001:\u0001:\u0001;\u0001;\u0001;\u0001<\u0001<\u0001<\u0001=\u0001"+
		"=\u0001=\u0001>\u0001>\u0001>\u0005>\u03f3\b>\n>\f>\u03f6\t>\u0001?\u0001"+
		"?\u0001?\u0001?\u0001@\u0001@\u0001A\u0001A\u0001A\u0001A\u0005A\u0402"+
		"\bA\nA\fA\u0405\tA\u0001B\u0001B\u0003B\u0409\bB\u0001C\u0001C\u0001D"+
		"\u0001D\u0001D\u0001D\u0005D\u0411\bD\nD\fD\u0414\tD\u0001E\u0001E\u0003"+
		"E\u0418\bE\u0001F\u0001F\u0001G\u0001G\u0001G\u0003G\u041f\bG\u0001H\u0001"+
		"H\u0001H\u0001H\u0001I\u0001I\u0003I\u0427\bI\u0001I\u0003I\u042a\bI\u0001"+
		"I\u0001I\u0001I\u0003I\u042f\bI\u0001I\u0003I\u0432\bI\u0001I\u0003I\u0435"+
		"\bI\u0001I\u0003I\u0438\bI\u0001I\u0003I\u043b\bI\u0001I\u0003I\u043e"+
		"\bI\u0003I\u0440\bI\u0001J\u0001J\u0001J\u0001K\u0001K\u0001L\u0001L\u0001"+
		"L\u0005L\u044a\bL\nL\fL\u044d\tL\u0001M\u0001M\u0003M\u0451\bM\u0001M"+
		"\u0003M\u0454\bM\u0001N\u0003N\u0457\bN\u0001N\u0001N\u0001O\u0001O\u0001"+
		"P\u0001P\u0003P\u045f\bP\u0001P\u0001P\u0001Q\u0001Q\u0001Q\u0003Q\u0466"+
		"\bQ\u0001R\u0001R\u0001S\u0001S\u0001S\u0003S\u046d\bS\u0001S\u0001S\u0001"+
		"S\u0003S\u0472\bS\u0001S\u0001S\u0001S\u0001S\u0003S\u0478\bS\u0001S\u0001"+
		"S\u0003S\u047c\bS\u0005S\u047e\bS\nS\fS\u0481\tS\u0001T\u0001T\u0003T"+
		"\u0485\bT\u0001T\u0001T\u0001T\u0003T\u048a\bT\u0001T\u0001T\u0001T\u0001"+
		"T\u0003T\u0490\bT\u0001T\u0001T\u0003T\u0494\bT\u0005T\u0496\bT\nT\fT"+
		"\u0499\tT\u0001T\u0003T\u049c\bT\u0001U\u0001U\u0001V\u0001V\u0003V\u04a2"+
		"\bV\u0001V\u0003V\u04a5\bV\u0001W\u0001W\u0003W\u04a9\bW\u0001W\u0001"+
		"W\u0001W\u0001W\u0001W\u0003W\u04b0\bW\u0001W\u0001W\u0003W\u04b4\bW\u0001"+
		"W\u0001W\u0001W\u0003W\u04b9\bW\u0003W\u04bb\bW\u0001X\u0001X\u0001X\u0001"+
		"X\u0003X\u04c1\bX\u0001Y\u0001Y\u0003Y\u04c5\bY\u0001Z\u0001Z\u0001Z\u0001"+
		"Z\u0001Z\u0001Z\u0003Z\u04cd\bZ\u0001[\u0001[\u0001[\u0001[\u0003[\u04d3"+
		"\b[\u0003[\u04d5\b[\u0001[\u0001[\u0001[\u0003[\u04da\b[\u0003[\u04dc"+
		"\b[\u0001\\\u0001\\\u0001\\\u0001\\\u0001\\\u0001\\\u0003\\\u04e4\b\\"+
		"\u0001\\\u0003\\\u04e7\b\\\u0001]\u0003]\u04ea\b]\u0001]\u0001]\u0001"+
		"^\u0001^\u0001^\u0003^\u04f1\b^\u0003^\u04f3\b^\u0001_\u0001_\u0003_\u04f7"+
		"\b_\u0001`\u0001`\u0001`\u0001a\u0001a\u0001a\u0001a\u0001a\u0001b\u0001"+
		"b\u0001c\u0001c\u0003c\u0505\bc\u0001d\u0001d\u0003d\u0509\bd\u0001d\u0001"+
		"d\u0001d\u0001d\u0001d\u0001d\u0001d\u0001d\u0003d\u0513\bd\u0001e\u0001"+
		"e\u0001e\u0001e\u0005e\u0519\be\ne\fe\u051c\te\u0001e\u0001e\u0001f\u0001"+
		"f\u0001f\u0001f\u0003f\u0524\bf\u0001g\u0001g\u0001h\u0001h\u0001i\u0001"+
		"i\u0003i\u052c\bi\u0001j\u0003j\u052f\bj\u0001j\u0001j\u0003j\u0533\b"+
		"j\u0001k\u0001k\u0001k\u0001k\u0001k\u0001k\u0001k\u0001k\u0001k\u0003"+
		"k\u053e\bk\u0001l\u0001l\u0001l\u0001l\u0001l\u0001m\u0001m\u0001m\u0001"+
		"m\u0001m\u0001m\u0003m\u054b\bm\u0001n\u0001n\u0001n\u0001n\u0001n\u0001"+
		"n\u0003n\u0553\bn\u0001o\u0001o\u0001o\u0001o\u0001o\u0001p\u0001p\u0001"+
		"p\u0005p\u055d\bp\np\fp\u0560\tp\u0001p\u0001p\u0001q\u0001q\u0001q\u0001"+
		"q\u0001q\u0001q\u0001q\u0001q\u0001q\u0001q\u0001q\u0001q\u0001q\u0001"+
		"q\u0001q\u0001q\u0001q\u0003q\u0575\bq\u0001r\u0001r\u0001r\u0003r\u057a"+
		"\br\u0001s\u0001s\u0001t\u0001t\u0003t\u0580\bt\u0001u\u0001u\u0001v\u0001"+
		"v\u0001v\u0001v\u0001v\u0001w\u0001w\u0001w\u0005w\u058c\bw\nw\fw\u058f"+
		"\tw\u0001w\u0001w\u0001x\u0001x\u0001x\u0001x\u0001x\u0001x\u0003x\u0599"+
		"\bx\u0001y\u0001y\u0001z\u0001z\u0001{\u0001{\u0001{\u0003{\u05a2\b{\u0001"+
		"{\u0001{\u0001|\u0001|\u0001}\u0001}\u0001}\u0001}\u0001}\u0001~\u0001"+
		"~\u0001~\u0005~\u05b0\b~\n~\f~\u05b3\t~\u0001~\u0001~\u0001\u007f\u0001"+
		"\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001"+
		"\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001"+
		"\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001\u007f\u0001"+
		"\u007f\u0001\u007f\u0003\u007f\u05cc\b\u007f\u0001\u0080\u0001\u0080\u0001"+
		"\u0080\u0001\u0080\u0003\u0080\u05d2\b\u0080\u0001\u0081\u0001\u0081\u0001"+
		"\u0082\u0001\u0082\u0001\u0082\u0001\u0082\u0005\u0082\u05da\b\u0082\n"+
		"\u0082\f\u0082\u05dd\t\u0082\u0001\u0082\u0001\u0082\u0001\u0083\u0001"+
		"\u0083\u0001\u0083\u0001\u0083\u0001\u0083\u0001\u0083\u0001\u0083\u0001"+
		"\u0083\u0001\u0083\u0001\u0084\u0001\u0084\u0001\u0085\u0001\u0085\u0001"+
		"\u0085\u0003\u0085\u05ef\b\u0085\u0001\u0085\u0001\u0085\u0001\u0086\u0001"+
		"\u0086\u0001\u0086\u0001\u0086\u0003\u0086\u05f7\b\u0086\u0001\u0087\u0001"+
		"\u0087\u0001\u0087\u0005\u0087\u05fc\b\u0087\n\u0087\f\u0087\u05ff\t\u0087"+
		"\u0001\u0088\u0001\u0088\u0001\u0088\u0005\u0088\u0604\b\u0088\n\u0088"+
		"\f\u0088\u0607\t\u0088\u0001\u0089\u0001\u0089\u0001\u0089\u0003\u0089"+
		"\u060c\b\u0089\u0001\u0089\u0001\u0089\u0001\u0089\u0005\u0089\u0611\b"+
		"\u0089\n\u0089\f\u0089\u0614\t\u0089\u0001\u0089\u0001\u0089\u0001\u0089"+
		"\u0001\u0089\u0003\u0089\u061a\b\u0089\u0001\u008a\u0001\u008a\u0001\u008a"+
		"\u0003\u008a\u061f\b\u008a\u0001\u008a\u0001\u008a\u0001\u008a\u0005\u008a"+
		"\u0624\b\u008a\n\u008a\f\u008a\u0627\t\u008a\u0001\u008a\u0001\u008a\u0001"+
		"\u008a\u0001\u008a\u0001\u008a\u0003\u008a\u062e\b\u008a\u0001\u008b\u0001"+
		"\u008b\u0001\u008b\u0001\u008b\u0001\u008b\u0001\u008b\u0001\u008b\u0001"+
		"\u008b\u0001\u008b\u0001\u008b\u0003\u008b\u063a\b\u008b\u0001\u008c\u0001"+
		"\u008c\u0001\u008c\u0005\u008c\u063f\b\u008c\n\u008c\f\u008c\u0642\t\u008c"+
		"\u0001\u008c\u0001\u008c\u0001\u008c\u0005\u008c\u0647\b\u008c\n\u008c"+
		"\f\u008c\u064a\t\u008c\u0001\u008c\u0001\u008c\u0001\u008c\u0001\u008c"+
		"\u0003\u008c\u0650\b\u008c\u0001\u008d\u0001\u008d\u0001\u008d\u0001\u008d"+
		"\u0001\u008e\u0001\u008e\u0001\u008e\u0001\u008e\u0001\u008e\u0001\u008e"+
		"\u0001\u008e\u0001\u008e\u0001\u008e\u0003\u008e\u065f\b\u008e\u0001\u008f"+
		"\u0001\u008f\u0001\u0090\u0001\u0090\u0001\u0090\u0001\u0090\u0001\u0090"+
		"\u0001\u0090\u0001\u0090\u0003\u0090\u066a\b\u0090\u0001\u0090\u0001\u0090"+
		"\u0003\u0090\u066e\b\u0090\u0001\u0090\u0001\u0090\u0001\u0090\u0003\u0090"+
		"\u0673\b\u0090\u0001\u0091\u0001\u0091\u0001\u0092\u0001\u0092\u0001\u0093"+
		"\u0001\u0093\u0001\u0093\u0001\u0093\u0003\u0093\u067d\b\u0093\u0001\u0093"+
		"\u0001\u0093\u0001\u0093\u0001\u0093\u0001\u0093\u0003\u0093\u0684\b\u0093"+
		"\u0001\u0093\u0001\u0093\u0003\u0093\u0688\b\u0093\u0001\u0094\u0004\u0094"+
		"\u068b\b\u0094\u000b\u0094\f\u0094\u068c\u0001\u0095\u0001\u0095\u0001"+
		"\u0095\u0001\u0095\u0001\u0095\u0001\u0096\u0004\u0096\u0695\b\u0096\u000b"+
		"\u0096\f\u0096\u0696\u0001\u0097\u0001\u0097\u0001\u0097\u0001\u0097\u0001"+
		"\u0097\u0001\u0098\u0001\u0098\u0001\u0098\u0001\u0099\u0001\u0099\u0003"+
		"\u0099\u06a3\b\u0099\u0001\u009a\u0001\u009a\u0001\u009b\u0001\u009b\u0001"+
		"\u009b\u0001\u009b\u0001\u009b\u0001\u009b\u0001\u009b\u0001\u009c\u0001"+
		"\u009c\u0001\u009d\u0001\u009d\u0001\u009d\u0001\u009e\u0001\u009e\u0001"+
		"\u009e\u0003\u009e\u06b6\b\u009e\u0001\u009e\u0001\u009e\u0001\u009e\u0001"+
		"\u009e\u0001\u009e\u0001\u009e\u0001\u009e\u0003\u009e\u06bf\b\u009e\u0001"+
		"\u009e\u0003\u009e\u06c2\b\u009e\u0003\u009e\u06c4\b\u009e\u0001\u009f"+
		"\u0001\u009f\u0001\u009f\u0003\u009f\u06c9\b\u009f\u0001\u009f\u0003\u009f"+
		"\u06cc\b\u009f\u0001\u009f\u0003\u009f\u06cf\b\u009f\u0001\u009f\u0001"+
		"\u009f\u0001\u00a0\u0001\u00a0\u0001\u00a0\u0001\u00a0\u0001\u00a1\u0001"+
		"\u00a1\u0001\u00a1\u0001\u00a2\u0001\u00a2\u0001\u00a3\u0001\u00a3\u0001"+
		"\u00a3\u0003\u00a3\u06df\b\u00a3\u0001\u00a4\u0001\u00a4\u0001\u00a4\u0001"+
		"\u00a4\u0001\u00a4\u0001\u00a5\u0001\u00a5\u0001\u00a5\u0003\u00a5\u06e9"+
		"\b\u00a5\u0001\u00a6\u0001\u00a6\u0001\u00a6\u0001\u00a7\u0001\u00a7\u0001"+
		"\u00a7\u0001\u00a8\u0001\u00a8\u0001\u00a8\u0001\u00a9\u0001\u00a9\u0001"+
		"\u00aa\u0001\u00aa\u0001\u00ab\u0001\u00ab\u0001\u00ab\u0001\u00ac\u0001"+
		"\u00ac\u0001\u00ac\u0001\u00ad\u0001\u00ad\u0001\u00ad\u0001\u00ad\u0003"+
		"\u00ad\u0702\b\u00ad\u0001\u00ae\u0001\u00ae\u0001\u00ae\u0003\u00ae\u0707"+
		"\b\u00ae\u0001\u00af\u0001\u00af\u0001\u00af\u0005\u00af\u070c\b\u00af"+
		"\n\u00af\f\u00af\u070f\t\u00af\u0001\u00b0\u0001\u00b0\u0001\u00b0\u0005"+
		"\u00b0\u0714\b\u00b0\n\u00b0\f\u00b0\u0717\t\u00b0\u0001\u00b1\u0003\u00b1"+
		"\u071a\b\u00b1\u0001\u00b1\u0001\u00b1\u0001\u00b2\u0001\u00b2\u0003\u00b2"+
		"\u0720\b\u00b2\u0001\u00b3\u0001\u00b3\u0001\u00b4\u0001\u00b4\u0001\u00b4"+
		"\u0001\u00b4\u0001\u00b4\u0001\u00b4\u0001\u00b4\u0001\u00b5\u0001\u00b5"+
		"\u0001\u00b5\u0003\u00b5\u072e\b\u00b5\u0001\u00b6\u0001\u00b6\u0001\u00b7"+
		"\u0001\u00b7\u0003\u00b7\u0734\b\u00b7\u0001\u00b8\u0001\u00b8\u0001\u00b8"+
		"\u0005\u00b8\u0739\b\u00b8\n\u00b8\f\u00b8\u073c\t\u00b8\u0001\u00b9\u0001"+
		"\u00b9\u0003\u00b9\u0740\b\u00b9\u0001\u00ba\u0001\u00ba\u0001\u00ba\u0001"+
		"\u00ba\u0001\u00ba\u0001\u00ba\u0003\u00ba\u0748\b\u00ba\u0001\u00bb\u0001"+
		"\u00bb\u0001\u00bc\u0003\u00bc\u074d\b\u00bc\u0001\u00bc\u0003\u00bc\u0750"+
		"\b\u00bc\u0001\u00bc\u0003\u00bc\u0753\b\u00bc\u0001\u00bc\u0001\u00bc"+
		"\u0001\u00bc\u0001\u00bc\u0001\u00bc\u0003\u00bc\u075a\b\u00bc\u0001\u00bd"+
		"\u0001\u00bd\u0001\u00be\u0001\u00be\u0001\u00be\u0001\u00be\u0001\u00be"+
		"\u0001\u00be\u0001\u00be\u0001\u00be\u0001\u00be\u0001\u00be\u0003\u00be"+
		"\u0768\b\u00be\u0001\u00be\u0001\u00be\u0001\u00be\u0001\u00be\u0001\u00be"+
		"\u0001\u00be\u0003\u00be\u0770\b\u00be\u0001\u00be\u0001\u00be\u0003\u00be"+
		"\u0774\b\u00be\u0001\u00bf\u0001\u00bf\u0001\u00c0\u0001\u00c0\u0001\u00c1"+
		"\u0001\u00c1\u0001\u00c2\u0001\u00c2\u0001\u00c3\u0001\u00c3\u0001\u00c3"+
		"\u0005\u00c3\u0781\b\u00c3\n\u00c3\f\u00c3\u0784\t\u00c3\u0001\u00c4\u0001"+
		"\u00c4\u0001\u00c4\u0005\u00c4\u0789\b\u00c4\n\u00c4\f\u00c4\u078c\t\u00c4"+
		"\u0001\u00c5\u0003\u00c5\u078f\b\u00c5\u0001\u00c5\u0001\u00c5\u0001\u00c6"+
		"\u0001\u00c6\u0001\u00c6\u0001\u00c6\u0001\u00c6\u0001\u00c6\u0003\u00c6"+
		"\u0799\b\u00c6\u0003\u00c6\u079b\b\u00c6\u0001\u00c7\u0001\u00c7\u0003"+
		"\u00c7\u079f\b\u00c7\u0001\u00c8\u0001\u00c8\u0001\u00c8\u0001\u00c8\u0001"+
		"\u00c8\u0001\u00c8\u0001\u00c8\u0003\u00c8\u07a8\b\u00c8\u0001\u00c9\u0001"+
		"\u00c9\u0001\u00ca\u0001\u00ca\u0003\u00ca\u07ae\b\u00ca\u0001\u00cb\u0001"+
		"\u00cb\u0001\u00cb\u0003\u00cb\u07b3\b\u00cb\u0001\u00cc\u0001\u00cc\u0001"+
		"\u00cc\u0001\u00cd\u0001\u00cd\u0001\u00ce\u0001\u00ce\u0001\u00ce\u0001"+
		"\u00ce\u0001\u00cf\u0001\u00cf\u0001\u00cf\u0005\u00cf\u07c1\b\u00cf\n"+
		"\u00cf\f\u00cf\u07c4\t\u00cf\u0001\u00d0\u0001\u00d0\u0003\u00d0\u07c8"+
		"\b\u00d0\u0001\u00d0\u0003\u00d0\u07cb\b\u00d0\u0001\u00d1\u0001\u00d1"+
		"\u0001\u00d2\u0001\u00d2\u0001\u00d2\u0001\u00d3\u0001\u00d3\u0001\u00d4"+
		"\u0001\u00d4\u0001\u00d4\u0001\u00d4\u0003\u00d4\u07d8\b\u00d4\u0001\u00d5"+
		"\u0001\u00d5\u0001\u00d5\u0001\u00d5\u0003\u00d5\u07de\b\u00d5\u0001\u00d6"+
		"\u0001\u00d6\u0001\u00d6\u0005\u00d6\u07e3\b\u00d6\n\u00d6\f\u00d6\u07e6"+
		"\t\u00d6\u0001\u00d7\u0001\u00d7\u0001\u00d7\u0001\u00d7\u0003\u00d7\u07ec"+
		"\b\u00d7\u0001\u00d8\u0001\u00d8\u0001\u00d8\u0005\u00d8\u07f1\b\u00d8"+
		"\n\u00d8\f\u00d8\u07f4\t\u00d8\u0001\u00d9\u0001\u00d9\u0001\u00d9\u0001"+
		"\u00d9\u0001\u00d9\u0003\u00d9\u07fb\b\u00d9\u0001\u00da\u0001\u00da\u0001"+
		"\u00da\u0001\u00da\u0001\u00da\u0001\u00db\u0001\u00db\u0001\u00db\u0001"+
		"\u00db\u0001\u00db\u0001\u00dc\u0001\u00dc\u0001\u00dc\u0001\u00dd\u0001"+
		"\u00dd\u0001\u00dd\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00df\u0001"+
		"\u00df\u0001\u00df\u0005\u00df\u0813\b\u00df\n\u00df\f\u00df\u0816\t\u00df"+
		"\u0001\u00e0\u0001\u00e0\u0001\u00e0\u0001\u00e0\u0001\u00e1\u0001\u00e1"+
		"\u0003\u00e1\u081e\b\u00e1\u0001\u00e1\u0001\u00e1\u0003\u00e1\u0822\b"+
		"\u00e1\u0001\u00e2\u0001\u00e2\u0001\u00e2\u0001\u00e2\u0001\u00e2\u0001"+
		"\u00e2\u0003\u00e2\u082a\b\u00e2\u0001\u00e3\u0001\u00e3\u0001\u00e4\u0001"+
		"\u00e4\u0001\u00e5\u0001\u00e5\u0003\u00e5\u0832\b\u00e5\u0001\u00e5\u0001"+
		"\u00e5\u0003\u00e5\u0836\b\u00e5\u0001\u00e5\u0001\u00e5\u0001\u00e5\u0001"+
		"\u00e5\u0001\u00e6\u0001\u00e6\u0001\u00e7\u0001\u00e7\u0003\u00e7\u0840"+
		"\b\u00e7\u0001\u00e7\u0001\u00e7\u0001\u00e7\u0001\u00e8\u0001\u00e8\u0003"+
		"\u00e8\u0847\b\u00e8\u0001\u00e8\u0001\u00e8\u0001\u00e8\u0003\u00e8\u084c"+
		"\b\u00e8\u0001\u00e9\u0001\u00e9\u0001\u00e9\u0001\u00ea\u0001\u00ea\u0001"+
		"\u00ea\u0001\u00ea\u0001\u00ea\u0001\u00ea\u0003\u00ea\u0857\b\u00ea\u0001"+
		"\u00eb\u0001\u00eb\u0001\u00eb\u0005\u00eb\u085c\b\u00eb\n\u00eb\f\u00eb"+
		"\u085f\t\u00eb\u0001\u00ec\u0001\u00ec\u0001\u00ec\u0001\u00ed\u0001\u00ed"+
		"\u0001\u00ed\u0003\u00ed\u0867\b\u00ed\u0001\u00ee\u0001\u00ee\u0001\u00ee"+
		"\u0001\u00ee\u0001\u00ef\u0001\u00ef\u0001\u00ef\u0001\u00f0\u0001\u00f0"+
		"\u0001\u00f0\u0001\u00f0\u0001\u00f0\u0001\u00f1\u0001\u00f1\u0001\u00f1"+
		"\u0005\u00f1\u0878\b\u00f1\n\u00f1\f\u00f1\u087b\t\u00f1\u0001\u00f2\u0001"+
		"\u00f2\u0001\u00f2\u0001\u00f2\u0001\u00f3\u0001\u00f3\u0001\u00f3\u0001"+
		"\u00f3\u0001\u00f4\u0001\u00f4\u0001\u00f4\u0005\u00f4\u0888\b\u00f4\n"+
		"\u00f4\f\u00f4\u088b\t\u00f4\u0001\u00f5\u0001\u00f5\u0001\u00f5\u0001"+
		"\u00f6\u0001\u00f6\u0001\u00f6\u0001\u00f7\u0001\u00f7\u0001\u00f8\u0001"+
		"\u00f8\u0003\u00f8\u0897\b\u00f8\u0001\u00f9\u0001\u00f9\u0001\u00f9\u0001"+
		"\u00fa\u0001\u00fa\u0003\u00fa\u089e\b\u00fa\u0001\u00fa\u0001\u00fa\u0001"+
		"\u00fb\u0001\u00fb\u0003\u00fb\u08a4\b\u00fb\u0001\u00fb\u0001\u00fb\u0001"+
		"\u00fc\u0001\u00fc\u0001\u00fd\u0001\u00fd\u0001\u00fe\u0001\u00fe\u0001"+
		"\u00fe\u0001\u00fe\u0001\u00fe\u0001\u00ff\u0001\u00ff\u0003\u00ff\u08b3"+
		"\b\u00ff\u0001\u0100\u0001\u0100\u0001\u0101\u0001\u0101\u0001\u0102\u0001"+
		"\u0102\u0001\u0102\u0001\u0103\u0001\u0103\u0003\u0103\u08be\b\u0103\u0001"+
		"\u0104\u0001\u0104\u0001\u0105\u0001\u0105\u0001\u0106\u0001\u0106\u0001"+
		"\u0106\u0003\u0106\u08c7\b\u0106\u0001\u0106\u0001\u0106\u0001\u0107\u0001"+
		"\u0107\u0001\u0107\u0003\u0107\u08ce\b\u0107\u0001\u0107\u0003\u0107\u08d1"+
		"\b\u0107\u0001\u0108\u0001\u0108\u0001\u0109\u0001\u0109\u0001\u0109\u0005"+
		"\u0109\u08d8\b\u0109\n\u0109\f\u0109\u08db\t\u0109\u0001\u010a\u0001\u010a"+
		"\u0001\u010a\u0001\u010a\u0001\u010a\u0003\u010a\u08e2\b\u010a\u0001\u010b"+
		"\u0001\u010b\u0001\u010b\u0001\u010b\u0001\u010b\u0003\u010b\u08e9\b\u010b"+
		"\u0001\u010c\u0001\u010c\u0003\u010c\u08ed\b\u010c\u0001\u010d\u0001\u010d"+
		"\u0001\u010e\u0001\u010e\u0001\u010f\u0001\u010f\u0001\u0110\u0001\u0110"+
		"\u0001\u0111\u0001\u0111\u0001\u0111\u0001\u0111\u0001\u0111\u0001\u0111"+
		"\u0001\u0111\u0001\u0111\u0003\u0111\u08ff\b\u0111\u0001\u0112\u0001\u0112"+
		"\u0001\u0112\u0001\u0112\u0003\u0112\u0905\b\u0112\u0001\u0112\u0001\u0112"+
		"\u0001\u0112\u0001\u0112\u0001\u0112\u0001\u0112\u0001\u0112\u0001\u0112"+
		"\u0003\u0112\u090f\b\u0112\u0001\u0112\u0001\u0112\u0003\u0112\u0913\b"+
		"\u0112\u0001\u0113\u0001\u0113\u0001\u0113\u0005\u0113\u0918\b\u0113\n"+
		"\u0113\f\u0113\u091b\t\u0113\u0001\u0114\u0001\u0114\u0001\u0114\u0001"+
		"\u0114\u0001\u0114\u0001\u0114\u0001\u0114\u0001\u0114\u0001\u0114\u0001"+
		"\u0114\u0003\u0114\u0927\b\u0114\u0001\u0115\u0001\u0115\u0001\u0115\u0005"+
		"\u0115\u092c\b\u0115\n\u0115\f\u0115\u092f\t\u0115\u0001\u0116\u0001\u0116"+
		"\u0003\u0116\u0933\b\u0116\u0001\u0117\u0001\u0117\u0001\u0118\u0001\u0118"+
		"\u0001\u0119\u0001\u0119\u0001\u011a\u0001\u011a\u0001\u011b\u0003\u011b"+
		"\u093e\b\u011b\u0001\u011b\u0001\u011b\u0001\u011c\u0001\u011c\u0003\u011c"+
		"\u0944\b\u011c\u0001\u011d\u0001\u011d\u0003\u011d\u0948\b\u011d\u0001"+
		"\u011e\u0001\u011e\u0001\u011e\u0003\u011e\u094d\b\u011e\u0001\u011e\u0003"+
		"\u011e\u0950\b\u011e\u0001\u011e\u0001\u011e\u0001\u011e\u0003\u011e\u0955"+
		"\b\u011e\u0001\u011e\u0001\u011e\u0001\u011e\u0003\u011e\u095a\b\u011e"+
		"\u0001\u011f\u0001\u011f\u0001\u011f\u0001\u0120\u0001\u0120\u0001\u0120"+
		"\u0003\u0120\u0962\b\u0120\u0001\u0121\u0001\u0121\u0001\u0122\u0001\u0122"+
		"\u0001\u0122\u0003\u0122\u0969\b\u0122\u0001\u0123\u0001\u0123\u0001\u0123"+
		"\u0001\u0124\u0001\u0124\u0001\u0124\u0001\u0125\u0001\u0125\u0001\u0125"+
		"\u0001\u0126\u0001\u0126\u0001\u0127\u0001\u0127\u0001\u0127\u0003\u0127"+
		"\u0979\b\u0127\u0001\u0128\u0001\u0128\u0003\u0128\u097d\b\u0128\u0001"+
		"\u0129\u0001\u0129\u0001\u0129\u0001\u0129\u0001\u0129\u0001\u0129\u0001"+
		"\u0129\u0001\u0129\u0001\u0129\u0001\u0129\u0001\u0129\u0001\u0129\u0001"+
		"\u0129\u0001\u0129\u0001\u0129\u0001\u0129\u0001\u0129\u0001\u0129\u0001"+
		"\u0129\u0001\u0129\u0001\u0129\u0001\u0129\u0001\u0129\u0001\u0129\u0001"+
		"\u0129\u0001\u0129\u0001\u0129\u0001\u0129\u0001\u0129\u0001\u0129\u0001"+
		"\u0129\u0001\u0129\u0001\u0129\u0001\u0129\u0003\u0129\u09a1\b\u0129\u0001"+
		"\u012a\u0001\u012a\u0001\u012a\u0001\u012a\u0001\u012b\u0001\u012b\u0003"+
		"\u012b\u09a9\b\u012b\u0001\u012c\u0001\u012c\u0001\u012c\u0001\u012c\u0001"+
		"\u012c\u0001\u012c\u0001\u012c\u0001\u012c\u0001\u012c\u0001\u012c\u0001"+
		"\u012c\u0003\u012c\u09b6\b\u012c\u0001\u012d\u0001\u012d\u0001\u012d\u0001"+
		"\u012d\u0001\u012d\u0001\u012d\u0001\u012d\u0001\u012d\u0003\u012d\u09c0"+
		"\b\u012d\u0001\u012e\u0001\u012e\u0001\u012f\u0001\u012f\u0001\u012f\u0001"+
		"\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001"+
		"\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001"+
		"\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001"+
		"\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001"+
		"\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001"+
		"\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001"+
		"\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001"+
		"\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001"+
		"\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0001"+
		"\u012f\u0001\u012f\u0001\u012f\u0001\u012f\u0003\u012f\u0a01\b\u012f\u0001"+
		"\u0130\u0001\u0130\u0001\u0130\u0000\u0000\u0131\u0000\u0002\u0004\u0006"+
		"\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,."+
		"02468:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086\u0088"+
		"\u008a\u008c\u008e\u0090\u0092\u0094\u0096\u0098\u009a\u009c\u009e\u00a0"+
		"\u00a2\u00a4\u00a6\u00a8\u00aa\u00ac\u00ae\u00b0\u00b2\u00b4\u00b6\u00b8"+
		"\u00ba\u00bc\u00be\u00c0\u00c2\u00c4\u00c6\u00c8\u00ca\u00cc\u00ce\u00d0"+
		"\u00d2\u00d4\u00d6\u00d8\u00da\u00dc\u00de\u00e0\u00e2\u00e4\u00e6\u00e8"+
		"\u00ea\u00ec\u00ee\u00f0\u00f2\u00f4\u00f6\u00f8\u00fa\u00fc\u00fe\u0100"+
		"\u0102\u0104\u0106\u0108\u010a\u010c\u010e\u0110\u0112\u0114\u0116\u0118"+
		"\u011a\u011c\u011e\u0120\u0122\u0124\u0126\u0128\u012a\u012c\u012e\u0130"+
		"\u0132\u0134\u0136\u0138\u013a\u013c\u013e\u0140\u0142\u0144\u0146\u0148"+
		"\u014a\u014c\u014e\u0150\u0152\u0154\u0156\u0158\u015a\u015c\u015e\u0160"+
		"\u0162\u0164\u0166\u0168\u016a\u016c\u016e\u0170\u0172\u0174\u0176\u0178"+
		"\u017a\u017c\u017e\u0180\u0182\u0184\u0186\u0188\u018a\u018c\u018e\u0190"+
		"\u0192\u0194\u0196\u0198\u019a\u019c\u019e\u01a0\u01a2\u01a4\u01a6\u01a8"+
		"\u01aa\u01ac\u01ae\u01b0\u01b2\u01b4\u01b6\u01b8\u01ba\u01bc\u01be\u01c0"+
		"\u01c2\u01c4\u01c6\u01c8\u01ca\u01cc\u01ce\u01d0\u01d2\u01d4\u01d6\u01d8"+
		"\u01da\u01dc\u01de\u01e0\u01e2\u01e4\u01e6\u01e8\u01ea\u01ec\u01ee\u01f0"+
		"\u01f2\u01f4\u01f6\u01f8\u01fa\u01fc\u01fe\u0200\u0202\u0204\u0206\u0208"+
		"\u020a\u020c\u020e\u0210\u0212\u0214\u0216\u0218\u021a\u021c\u021e\u0220"+
		"\u0222\u0224\u0226\u0228\u022a\u022c\u022e\u0230\u0232\u0234\u0236\u0238"+
		"\u023a\u023c\u023e\u0240\u0242\u0244\u0246\u0248\u024a\u024c\u024e\u0250"+
		"\u0252\u0254\u0256\u0258\u025a\u025c\u025e\u0260\u0000 \u0001\u0000\u011f"+
		"\u011f\u0001\u0000\u0115\u0115\u0002\u0000\u000e\u000e55\u0002\u0000\u0002"+
		"\u0002\u000b\u000b\u0003\u0000\u0010\u0010\u001d\u001d++\u0002\u0000\u000f"+
		"\u000f33\u000f\u0000==EFXXddfgkkpptt\u007f\u007f\u0083\u0083\u0089\u008a"+
		"\u008c\u008c\u0098\u0099\u009e\u00cd\u0152\u0159\u0006\u0000\u0004\u0004"+
		"..BBRR[[aa\u0002\u0000\b\b44\u0002\u0000~~\u0085\u0085\u0002\u0000\u0095"+
		"\u0095\u012b\u012b\u0004\u0000XXddfgtt\u0002\u0000WWee\u0002\u0000\u0014"+
		"\u0014**\u0001\u0000\u0120\u0121\u0001\u0000\u0122\u0124\u0001\u0000\u008f"+
		"\u0091\u0003\u0000\u0006\u0006\u001c\u001c22\u0002\u0000<<LL\u0001\u0000"+
		"\u010e\u0111\u0002\u0000\u0113\u0113\u0118\u011c\u0002\u0000\u0005\u0005"+
		"//\u0002\u0000\u0015\u0015\u001e\u001e\u0003\u0000\u000f\u000f33\u0096"+
		"\u0096\u0002\u0000\u0004\u0004..\u0004\u0000II^^qr\u009c\u009c\b\u0000"+
		"@@KKMNPPbcmo}}\u009b\u009b\u0004\u0000\u0016\u0016\u001d\u001d++\u014f"+
		"\u0151\u0001\u0000\u0147\u0148\u0002\u0000\u012b\u012b\u017f\u017f\u0018"+
		"\u0000\u0014\u0014#$**,,55::<EGGIWZ]_ceegsuy{\u0083\u0085\u0094\u0096"+
		"\u00d0\u00d2\u00e9\u00eb\u00f5\u00f7\u00f8\u00fa\u010d\u014d\u014d\u0152"+
		"\u0159\u0169\u017b\u0001\u0000\u012c\u0144\u0a84\u0000\u0265\u0001\u0000"+
		"\u0000\u0000\u0002\u0271\u0001\u0000\u0000\u0000\u0004\u0276\u0001\u0000"+
		"\u0000\u0000\u0006\u027d\u0001\u0000\u0000\u0000\b\u0284\u0001\u0000\u0000"+
		"\u0000\n\u028b\u0001\u0000\u0000\u0000\f\u028e\u0001\u0000\u0000\u0000"+
		"\u000e\u0291\u0001\u0000\u0000\u0000\u0010\u0294\u0001\u0000\u0000\u0000"+
		"\u0012\u0297\u0001\u0000\u0000\u0000\u0014\u029a\u0001\u0000\u0000\u0000"+
		"\u0016\u029d\u0001\u0000\u0000\u0000\u0018\u02a2\u0001\u0000\u0000\u0000"+
		"\u001a\u02a6\u0001\u0000\u0000\u0000\u001c\u02a9\u0001\u0000\u0000\u0000"+
		"\u001e\u02ac\u0001\u0000\u0000\u0000 \u02af\u0001\u0000\u0000\u0000\""+
		"\u02b2\u0001\u0000\u0000\u0000$\u02c3\u0001\u0000\u0000\u0000&\u02c5\u0001"+
		"\u0000\u0000\u0000(\u02cb\u0001\u0000\u0000\u0000*\u02d3\u0001\u0000\u0000"+
		"\u0000,\u02d5\u0001\u0000\u0000\u0000.\u02d9\u0001\u0000\u0000\u00000"+
		"\u02f7\u0001\u0000\u0000\u00002\u02f9\u0001\u0000\u0000\u00004\u0302\u0001"+
		"\u0000\u0000\u00006\u0308\u0001\u0000\u0000\u00008\u030f\u0001\u0000\u0000"+
		"\u0000:\u031b\u0001\u0000\u0000\u0000<\u0325\u0001\u0000\u0000\u0000>"+
		"\u0330\u0001\u0000\u0000\u0000@\u0335\u0001\u0000\u0000\u0000B\u033b\u0001"+
		"\u0000\u0000\u0000D\u0341\u0001\u0000\u0000\u0000F\u0347\u0001\u0000\u0000"+
		"\u0000H\u034d\u0001\u0000\u0000\u0000J\u0353\u0001\u0000\u0000\u0000L"+
		"\u036a\u0001\u0000\u0000\u0000N\u036c\u0001\u0000\u0000\u0000P\u036e\u0001"+
		"\u0000\u0000\u0000R\u0371\u0001\u0000\u0000\u0000T\u0376\u0001\u0000\u0000"+
		"\u0000V\u037a\u0001\u0000\u0000\u0000X\u037d\u0001\u0000\u0000\u0000Z"+
		"\u038e\u0001\u0000\u0000\u0000\\\u0392\u0001\u0000\u0000\u0000^\u0394"+
		"\u0001\u0000\u0000\u0000`\u039c\u0001\u0000\u0000\u0000b\u039e\u0001\u0000"+
		"\u0000\u0000d\u03a0\u0001\u0000\u0000\u0000f\u03a7\u0001\u0000\u0000\u0000"+
		"h\u03b2\u0001\u0000\u0000\u0000j\u03b6\u0001\u0000\u0000\u0000l\u03bf"+
		"\u0001\u0000\u0000\u0000n\u03c1\u0001\u0000\u0000\u0000p\u03d0\u0001\u0000"+
		"\u0000\u0000r\u03d2\u0001\u0000\u0000\u0000t\u03db\u0001\u0000\u0000\u0000"+
		"v\u03e6\u0001\u0000\u0000\u0000x\u03e9\u0001\u0000\u0000\u0000z\u03ec"+
		"\u0001\u0000\u0000\u0000|\u03ef\u0001\u0000\u0000\u0000~\u03f7\u0001\u0000"+
		"\u0000\u0000\u0080\u03fb\u0001\u0000\u0000\u0000\u0082\u03fd\u0001\u0000"+
		"\u0000\u0000\u0084\u0406\u0001\u0000\u0000\u0000\u0086\u040a\u0001\u0000"+
		"\u0000\u0000\u0088\u040c\u0001\u0000\u0000\u0000\u008a\u0415\u0001\u0000"+
		"\u0000\u0000\u008c\u0419\u0001\u0000\u0000\u0000\u008e\u041e\u0001\u0000"+
		"\u0000\u0000\u0090\u0420\u0001\u0000\u0000\u0000\u0092\u0424\u0001\u0000"+
		"\u0000\u0000\u0094\u0441\u0001\u0000\u0000\u0000\u0096\u0444\u0001\u0000"+
		"\u0000\u0000\u0098\u0446\u0001\u0000\u0000\u0000\u009a\u0453\u0001\u0000"+
		"\u0000\u0000\u009c\u0456\u0001\u0000\u0000\u0000\u009e\u045a\u0001\u0000"+
		"\u0000\u0000\u00a0\u045e\u0001\u0000\u0000\u0000\u00a2\u0462\u0001\u0000"+
		"\u0000\u0000\u00a4\u0467\u0001\u0000\u0000\u0000\u00a6\u0469\u0001\u0000"+
		"\u0000\u0000\u00a8\u0497\u0001\u0000\u0000\u0000\u00aa\u049d\u0001\u0000"+
		"\u0000\u0000\u00ac\u049f\u0001\u0000\u0000\u0000\u00ae\u04ba\u0001\u0000"+
		"\u0000\u0000\u00b0\u04c0\u0001\u0000\u0000\u0000\u00b2\u04c2\u0001\u0000"+
		"\u0000\u0000\u00b4\u04cc\u0001\u0000\u0000\u0000\u00b6\u04ce\u0001\u0000"+
		"\u0000\u0000\u00b8\u04e6\u0001\u0000\u0000\u0000\u00ba\u04e9\u0001\u0000"+
		"\u0000\u0000\u00bc\u04f2\u0001\u0000\u0000\u0000\u00be\u04f6\u0001\u0000"+
		"\u0000\u0000\u00c0\u04f8\u0001\u0000\u0000\u0000\u00c2\u04fb\u0001\u0000"+
		"\u0000\u0000\u00c4\u0500\u0001\u0000\u0000\u0000\u00c6\u0504\u0001\u0000"+
		"\u0000\u0000\u00c8\u0506\u0001\u0000\u0000\u0000\u00ca\u0514\u0001\u0000"+
		"\u0000\u0000\u00cc\u0523\u0001\u0000\u0000\u0000\u00ce\u0525\u0001\u0000"+
		"\u0000\u0000\u00d0\u0527\u0001\u0000\u0000\u0000\u00d2\u0529\u0001\u0000"+
		"\u0000\u0000\u00d4\u052e\u0001\u0000\u0000\u0000\u00d6\u0534\u0001\u0000"+
		"\u0000\u0000\u00d8\u053f\u0001\u0000\u0000\u0000\u00da\u054a\u0001\u0000"+
		"\u0000\u0000\u00dc\u0552\u0001\u0000\u0000\u0000\u00de\u0554\u0001\u0000"+
		"\u0000\u0000\u00e0\u0559\u0001\u0000\u0000\u0000\u00e2\u0574\u0001\u0000"+
		"\u0000\u0000\u00e4\u0579\u0001\u0000\u0000\u0000\u00e6\u057b\u0001\u0000"+
		"\u0000\u0000\u00e8\u057f\u0001\u0000\u0000\u0000\u00ea\u0581\u0001\u0000"+
		"\u0000\u0000\u00ec\u0583\u0001\u0000\u0000\u0000\u00ee\u0588\u0001\u0000"+
		"\u0000\u0000\u00f0\u0598\u0001\u0000\u0000\u0000\u00f2\u059a\u0001\u0000"+
		"\u0000\u0000\u00f4\u059c\u0001\u0000\u0000\u0000\u00f6\u059e\u0001\u0000"+
		"\u0000\u0000\u00f8\u05a5\u0001\u0000\u0000\u0000\u00fa\u05a7\u0001\u0000"+
		"\u0000\u0000\u00fc\u05ac\u0001\u0000\u0000\u0000\u00fe\u05cb\u0001\u0000"+
		"\u0000\u0000\u0100\u05d1\u0001\u0000\u0000\u0000\u0102\u05d3\u0001\u0000"+
		"\u0000\u0000\u0104\u05d5\u0001\u0000\u0000\u0000\u0106\u05e0\u0001\u0000"+
		"\u0000\u0000\u0108\u05e9\u0001\u0000\u0000\u0000\u010a\u05eb\u0001\u0000"+
		"\u0000\u0000\u010c\u05f6\u0001\u0000\u0000\u0000\u010e\u05f8\u0001\u0000"+
		"\u0000\u0000\u0110\u0600\u0001\u0000\u0000\u0000\u0112\u0619\u0001\u0000"+
		"\u0000\u0000\u0114\u062d\u0001\u0000\u0000\u0000\u0116\u0639\u0001\u0000"+
		"\u0000\u0000\u0118\u064f\u0001\u0000\u0000\u0000\u011a\u0651\u0001\u0000"+
		"\u0000\u0000\u011c\u065e\u0001\u0000\u0000\u0000\u011e\u0660\u0001\u0000"+
		"\u0000\u0000\u0120\u0672\u0001\u0000\u0000\u0000\u0122\u0674\u0001\u0000"+
		"\u0000\u0000\u0124\u0676\u0001\u0000\u0000\u0000\u0126\u0687\u0001\u0000"+
		"\u0000\u0000\u0128\u068a\u0001\u0000\u0000\u0000\u012a\u068e\u0001\u0000"+
		"\u0000\u0000\u012c\u0694\u0001\u0000\u0000\u0000\u012e\u0698\u0001\u0000"+
		"\u0000\u0000\u0130\u069d\u0001\u0000\u0000\u0000\u0132\u06a2\u0001\u0000"+
		"\u0000\u0000\u0134\u06a4\u0001\u0000\u0000\u0000\u0136\u06a6\u0001\u0000"+
		"\u0000\u0000\u0138\u06ad\u0001\u0000\u0000\u0000\u013a\u06af\u0001\u0000"+
		"\u0000\u0000\u013c\u06c3\u0001\u0000\u0000\u0000\u013e\u06c5\u0001\u0000"+
		"\u0000\u0000\u0140\u06d2\u0001\u0000\u0000\u0000\u0142\u06d6\u0001\u0000"+
		"\u0000\u0000\u0144\u06d9\u0001\u0000\u0000\u0000\u0146\u06de\u0001\u0000"+
		"\u0000\u0000\u0148\u06e0\u0001\u0000\u0000\u0000\u014a\u06e8\u0001\u0000"+
		"\u0000\u0000\u014c\u06ea\u0001\u0000\u0000\u0000\u014e\u06ed\u0001\u0000"+
		"\u0000\u0000\u0150\u06f0\u0001\u0000\u0000\u0000\u0152\u06f3\u0001\u0000"+
		"\u0000\u0000\u0154\u06f5\u0001\u0000\u0000\u0000\u0156\u06f7\u0001\u0000"+
		"\u0000\u0000\u0158\u06fa\u0001\u0000\u0000\u0000\u015a\u0701\u0001\u0000"+
		"\u0000\u0000\u015c\u0706\u0001\u0000\u0000\u0000\u015e\u0708\u0001\u0000"+
		"\u0000\u0000\u0160\u0710\u0001\u0000\u0000\u0000\u0162\u0719\u0001\u0000"+
		"\u0000\u0000\u0164\u071f\u0001\u0000\u0000\u0000\u0166\u0721\u0001\u0000"+
		"\u0000\u0000\u0168\u0723\u0001\u0000\u0000\u0000\u016a\u072d\u0001\u0000"+
		"\u0000\u0000\u016c\u072f\u0001\u0000\u0000\u0000\u016e\u0733\u0001\u0000"+
		"\u0000\u0000\u0170\u0735\u0001\u0000\u0000\u0000\u0172\u073f\u0001\u0000"+
		"\u0000\u0000\u0174\u0741\u0001\u0000\u0000\u0000\u0176\u0749\u0001\u0000"+
		"\u0000\u0000\u0178\u0759\u0001\u0000\u0000\u0000\u017a\u075b\u0001\u0000"+
		"\u0000\u0000\u017c\u0773\u0001\u0000\u0000\u0000\u017e\u0775\u0001\u0000"+
		"\u0000\u0000\u0180\u0777\u0001\u0000\u0000\u0000\u0182\u0779\u0001\u0000"+
		"\u0000\u0000\u0184\u077b\u0001\u0000\u0000\u0000\u0186\u077d\u0001\u0000"+
		"\u0000\u0000\u0188\u0785\u0001\u0000\u0000\u0000\u018a\u078e\u0001\u0000"+
		"\u0000\u0000\u018c\u079a\u0001\u0000\u0000\u0000\u018e\u079e\u0001\u0000"+
		"\u0000\u0000\u0190\u07a7\u0001\u0000\u0000\u0000\u0192\u07a9\u0001\u0000"+
		"\u0000\u0000\u0194\u07ad\u0001\u0000\u0000\u0000\u0196\u07b2\u0001\u0000"+
		"\u0000\u0000\u0198\u07b4\u0001\u0000\u0000\u0000\u019a\u07b7\u0001\u0000"+
		"\u0000\u0000\u019c\u07b9\u0001\u0000\u0000\u0000\u019e\u07bd\u0001\u0000"+
		"\u0000\u0000\u01a0\u07c5\u0001\u0000\u0000\u0000\u01a2\u07cc\u0001\u0000"+
		"\u0000\u0000\u01a4\u07ce\u0001\u0000\u0000\u0000\u01a6\u07d1\u0001\u0000"+
		"\u0000\u0000\u01a8\u07d3\u0001\u0000\u0000\u0000\u01aa\u07d9\u0001\u0000"+
		"\u0000\u0000\u01ac\u07df\u0001\u0000\u0000\u0000\u01ae\u07eb\u0001\u0000"+
		"\u0000\u0000\u01b0\u07ed\u0001\u0000\u0000\u0000\u01b2\u07fa\u0001\u0000"+
		"\u0000\u0000\u01b4\u07fc\u0001\u0000\u0000\u0000\u01b6\u0801\u0001\u0000"+
		"\u0000\u0000\u01b8\u0806\u0001\u0000\u0000\u0000\u01ba\u0809\u0001\u0000"+
		"\u0000\u0000\u01bc\u080c\u0001\u0000\u0000\u0000\u01be\u080f\u0001\u0000"+
		"\u0000\u0000\u01c0\u0817\u0001\u0000\u0000\u0000\u01c2\u0821\u0001\u0000"+
		"\u0000\u0000\u01c4\u0829\u0001\u0000\u0000\u0000\u01c6\u082b\u0001\u0000"+
		"\u0000\u0000\u01c8\u082d\u0001\u0000\u0000\u0000\u01ca\u082f\u0001\u0000"+
		"\u0000\u0000\u01cc\u083b\u0001\u0000\u0000\u0000\u01ce\u083d\u0001\u0000"+
		"\u0000\u0000\u01d0\u0844\u0001\u0000\u0000\u0000\u01d2\u084d\u0001\u0000"+
		"\u0000\u0000\u01d4\u0856\u0001\u0000\u0000\u0000\u01d6\u0858\u0001\u0000"+
		"\u0000\u0000\u01d8\u0860\u0001\u0000\u0000\u0000\u01da\u0866\u0001\u0000"+
		"\u0000\u0000\u01dc\u0868\u0001\u0000\u0000\u0000\u01de\u086c\u0001\u0000"+
		"\u0000\u0000\u01e0\u086f\u0001\u0000\u0000\u0000\u01e2\u0874\u0001\u0000"+
		"\u0000\u0000\u01e4\u087c\u0001\u0000\u0000\u0000\u01e6\u0880\u0001\u0000"+
		"\u0000\u0000\u01e8\u0884\u0001\u0000\u0000\u0000\u01ea\u088c\u0001\u0000"+
		"\u0000\u0000\u01ec\u088f\u0001\u0000\u0000\u0000\u01ee\u0892\u0001\u0000"+
		"\u0000\u0000\u01f0\u0896\u0001\u0000\u0000\u0000\u01f2\u0898\u0001\u0000"+
		"\u0000\u0000\u01f4\u089b\u0001\u0000\u0000\u0000\u01f6\u08a1\u0001\u0000"+
		"\u0000\u0000\u01f8\u08a7\u0001\u0000\u0000\u0000\u01fa\u08a9\u0001\u0000"+
		"\u0000\u0000\u01fc\u08ab\u0001\u0000\u0000\u0000\u01fe\u08b2\u0001\u0000"+
		"\u0000\u0000\u0200\u08b4\u0001\u0000\u0000\u0000\u0202\u08b6\u0001\u0000"+
		"\u0000\u0000\u0204\u08b8\u0001\u0000\u0000\u0000\u0206\u08bd\u0001\u0000"+
		"\u0000\u0000\u0208\u08bf\u0001\u0000\u0000\u0000\u020a\u08c1\u0001\u0000"+
		"\u0000\u0000\u020c\u08c3\u0001\u0000\u0000\u0000\u020e\u08d0\u0001\u0000"+
		"\u0000\u0000\u0210\u08d2\u0001\u0000\u0000\u0000\u0212\u08d4\u0001\u0000"+
		"\u0000\u0000\u0214\u08e1\u0001\u0000\u0000\u0000\u0216\u08e8\u0001\u0000"+
		"\u0000\u0000\u0218\u08ec\u0001\u0000\u0000\u0000\u021a\u08ee\u0001\u0000"+
		"\u0000\u0000\u021c\u08f0\u0001\u0000\u0000\u0000\u021e\u08f2\u0001\u0000"+
		"\u0000\u0000\u0220\u08f4\u0001\u0000\u0000\u0000\u0222\u08fe\u0001\u0000"+
		"\u0000\u0000\u0224\u0912\u0001\u0000\u0000\u0000\u0226\u0914\u0001\u0000"+
		"\u0000\u0000\u0228\u0926\u0001\u0000\u0000\u0000\u022a\u0928\u0001\u0000"+
		"\u0000\u0000\u022c\u0932\u0001\u0000\u0000\u0000\u022e\u0934\u0001\u0000"+
		"\u0000\u0000\u0230\u0936\u0001\u0000\u0000\u0000\u0232\u0938\u0001\u0000"+
		"\u0000\u0000\u0234\u093a\u0001\u0000\u0000\u0000\u0236\u093d\u0001\u0000"+
		"\u0000\u0000\u0238\u0943\u0001\u0000\u0000\u0000\u023a\u0947\u0001\u0000"+
		"\u0000\u0000\u023c\u0959\u0001\u0000\u0000\u0000\u023e\u095b\u0001\u0000"+
		"\u0000\u0000\u0240\u0961\u0001\u0000\u0000\u0000\u0242\u0963\u0001\u0000"+
		"\u0000\u0000\u0244\u0968\u0001\u0000\u0000\u0000\u0246\u096a\u0001\u0000"+
		"\u0000\u0000\u0248\u096d\u0001\u0000\u0000\u0000\u024a\u0970\u0001\u0000"+
		"\u0000\u0000\u024c\u0973\u0001\u0000\u0000\u0000\u024e\u0978\u0001\u0000"+
		"\u0000\u0000\u0250\u097a\u0001\u0000\u0000\u0000\u0252\u09a0\u0001\u0000"+
		"\u0000\u0000\u0254\u09a2\u0001\u0000\u0000\u0000\u0256\u09a6\u0001\u0000"+
		"\u0000\u0000\u0258\u09b5\u0001\u0000\u0000\u0000\u025a\u09bf\u0001\u0000"+
		"\u0000\u0000\u025c\u09c1\u0001\u0000\u0000\u0000\u025e\u0a00\u0001\u0000"+
		"\u0000\u0000\u0260\u0a02\u0001\u0000\u0000\u0000\u0262\u0264\u0003\u0002"+
		"\u0001\u0000\u0263\u0262\u0001\u0000\u0000\u0000\u0264\u0267\u0001\u0000"+
		"\u0000\u0000\u0265\u0263\u0001\u0000\u0000\u0000\u0265\u0266\u0001\u0000"+
		"\u0000\u0000\u0266\u0269\u0001\u0000\u0000\u0000\u0267\u0265\u0001\u0000"+
		"\u0000\u0000\u0268\u026a\u0003\u0002\u0001\u0000\u0269\u0268\u0001\u0000"+
		"\u0000\u0000\u0269\u026a\u0001\u0000\u0000\u0000\u026a\u026b\u0001\u0000"+
		"\u0000\u0000\u026b\u026c\u0005\u0000\u0000\u0001\u026c\u0001\u0001\u0000"+
		"\u0000\u0000\u026d\u0272\u0003\u0006\u0003\u0000\u026e\u0272\u0003r9\u0000"+
		"\u026f\u0272\u0003V+\u0000\u0270\u0272\u0003`0\u0000\u0271\u026d\u0001"+
		"\u0000\u0000\u0000\u0271\u026e\u0001\u0000\u0000\u0000\u0271\u026f\u0001"+
		"\u0000\u0000\u0000\u0271\u0270\u0001\u0000\u0000\u0000\u0272\u0274\u0001"+
		"\u0000\u0000\u0000\u0273\u0275\u0005\u0115\u0000\u0000\u0274\u0273\u0001"+
		"\u0000\u0000\u0000\u0274\u0275\u0001\u0000\u0000\u0000\u0275\u0003\u0001"+
		"\u0000\u0000\u0000\u0276\u0277\u0003\u0006\u0003\u0000\u0277\u0278\u0005"+
		"\u0000\u0000\u0001\u0278\u0005\u0001\u0000\u0000\u0000\u0279\u027e\u0003"+
		"$\u0012\u0000\u027a\u027e\u0003&\u0013\u0000\u027b\u027e\u0003(\u0014"+
		"\u0000\u027c\u027e\u0003*\u0015\u0000\u027d\u0279\u0001\u0000\u0000\u0000"+
		"\u027d\u027a\u0001\u0000\u0000\u0000\u027d\u027b\u0001\u0000\u0000\u0000"+
		"\u027d\u027c\u0001\u0000\u0000\u0000\u027e\u0280\u0001\u0000\u0000\u0000"+
		"\u027f\u0281\u0005\u0115\u0000\u0000\u0280\u027f\u0001\u0000\u0000\u0000"+
		"\u0280\u0281\u0001\u0000\u0000\u0000\u0281\u0007\u0001\u0000\u0000\u0000"+
		"\u0282\u0285\u0003V+\u0000\u0283\u0285\u0003`0\u0000\u0284\u0282\u0001"+
		"\u0000\u0000\u0000\u0284\u0283\u0001\u0000\u0000\u0000\u0285\u0287\u0001"+
		"\u0000\u0000\u0000\u0286\u0288\u0005\u0115\u0000\u0000\u0287\u0286\u0001"+
		"\u0000\u0000\u0000\u0287\u0288\u0001\u0000\u0000\u0000\u0288\u0289\u0001"+
		"\u0000\u0000\u0000\u0289\u028a\u0005\u0000\u0000\u0001\u028a\t\u0001\u0000"+
		"\u0000\u0000\u028b\u028c\u0003\u0114\u008a\u0000\u028c\u028d\u0005\u0000"+
		"\u0000\u0001\u028d\u000b\u0001\u0000\u0000\u0000\u028e\u028f\u0003\u0116"+
		"\u008b\u0000\u028f\u0290\u0005\u0000\u0000\u0001\u0290\r\u0001\u0000\u0000"+
		"\u0000\u0291\u0292\u0003\u01d4\u00ea\u0000\u0292\u0293\u0005\u0000\u0000"+
		"\u0001\u0293\u000f\u0001\u0000\u0000\u0000\u0294\u0295\u0003\u015a\u00ad"+
		"\u0000\u0295\u0296\u0005\u0000\u0000\u0001\u0296\u0011\u0001\u0000\u0000"+
		"\u0000\u0297\u0298\u0003\u00b2Y\u0000\u0298\u0299\u0005\u0000\u0000\u0001"+
		"\u0299\u0013\u0001\u0000\u0000\u0000\u029a\u029b\u0003`0\u0000\u029b\u029c"+
		"\u0005\u0000\u0000\u0001\u029c\u0015\u0001\u0000\u0000\u0000\u029d\u029e"+
		"\u0003\u00a8T\u0000\u029e\u029f\u0005\u0000\u0000\u0001\u029f\u0017\u0001"+
		"\u0000\u0000\u0000\u02a0\u02a3\u0003\u0236\u011b\u0000\u02a1\u02a3\u0003"+
		"\u0238\u011c\u0000\u02a2\u02a0\u0001\u0000\u0000\u0000\u02a2\u02a1\u0001"+
		"\u0000\u0000\u0000\u02a3\u02a4\u0001\u0000\u0000\u0000\u02a4\u02a5\u0005"+
		"\u0000\u0000\u0001\u02a5\u0019\u0001\u0000\u0000\u0000\u02a6\u02a7\u0003"+
		"\u01da\u00ed\u0000\u02a7\u02a8\u0005\u0000\u0000\u0001\u02a8\u001b\u0001"+
		"\u0000\u0000\u0000\u02a9\u02aa\u0003b1\u0000\u02aa\u02ab\u0005\u0000\u0000"+
		"\u0001\u02ab\u001d\u0001\u0000\u0000\u0000\u02ac\u02ad\u0003n7\u0000\u02ad"+
		"\u02ae\u0005\u0000\u0000\u0001\u02ae\u001f\u0001\u0000\u0000\u0000\u02af"+
		"\u02b0\u0003p8\u0000\u02b0\u02b1\u0005\u0000\u0000\u0001\u02b1!\u0001"+
		"\u0000\u0000\u0000\u02b2\u02b3\u0003*\u0015\u0000\u02b3\u02b4\u0005\u0000"+
		"\u0000\u0001\u02b4#\u0001\u0000\u0000\u0000\u02b5\u02c4\u00030\u0018\u0000"+
		"\u02b6\u02c4\u00032\u0019\u0000\u02b7\u02c4\u00034\u001a\u0000\u02b8\u02c4"+
		"\u00036\u001b\u0000\u02b9\u02c4\u00038\u001c\u0000\u02ba\u02c4\u0003:"+
		"\u001d\u0000\u02bb\u02c4\u0003<\u001e\u0000\u02bc\u02c4\u0003>\u001f\u0000"+
		"\u02bd\u02c4\u0003@ \u0000\u02be\u02c4\u0003B!\u0000\u02bf\u02c4\u0003"+
		"D\"\u0000\u02c0\u02c4\u0003F#\u0000\u02c1\u02c4\u0003H$\u0000\u02c2\u02c4"+
		"\u0003J%\u0000\u02c3\u02b5\u0001\u0000\u0000\u0000\u02c3\u02b6\u0001\u0000"+
		"\u0000\u0000\u02c3\u02b7\u0001\u0000\u0000\u0000\u02c3\u02b8\u0001\u0000"+
		"\u0000\u0000\u02c3\u02b9\u0001\u0000\u0000\u0000\u02c3\u02ba\u0001\u0000"+
		"\u0000\u0000\u02c3\u02bb\u0001\u0000\u0000\u0000\u02c3\u02bc\u0001\u0000"+
		"\u0000\u0000\u02c3\u02bd\u0001\u0000\u0000\u0000\u02c3\u02be\u0001\u0000"+
		"\u0000\u0000\u02c3\u02bf\u0001\u0000\u0000\u0000\u02c3\u02c0\u0001\u0000"+
		"\u0000\u0000\u02c3\u02c1\u0001\u0000\u0000\u0000\u02c3\u02c2\u0001\u0000"+
		"\u0000\u0000\u02c4%\u0001\u0000\u0000\u0000\u02c5\u02c6\u0005O\u0000\u0000"+
		"\u02c6\u02c7\u0003L&\u0000\u02c7\u02c9\u0003\u00b6[\u0000\u02c8\u02ca"+
		"\u0003N\'\u0000\u02c9\u02c8\u0001\u0000\u0000\u0000\u02c9\u02ca\u0001"+
		"\u0000\u0000\u0000\u02ca\'\u0001\u0000\u0000\u0000\u02cb\u02cc\u0005\u0169"+
		"\u0000\u0000\u02cc\u02cd\u0003L&\u0000\u02cd\u02cf\u0003\u00b6[\u0000"+
		"\u02ce\u02d0\u0003P(\u0000\u02cf\u02ce\u0001\u0000\u0000\u0000\u02cf\u02d0"+
		"\u0001\u0000\u0000\u0000\u02d0)\u0001\u0000\u0000\u0000\u02d1\u02d4\u0003"+
		",\u0016\u0000\u02d2\u02d4\u0003.\u0017\u0000\u02d3\u02d1\u0001\u0000\u0000"+
		"\u0000\u02d3\u02d2\u0001\u0000\u0000\u0000\u02d4+\u0001\u0000\u0000\u0000"+
		"\u02d5\u02d6\u0005\u014e\u0000\u0000\u02d6\u02d7\u00050\u0000\u0000\u02d7"+
		"\u02d8\u0003\u00b6[\u0000\u02d8-\u0001\u0000\u0000\u0000\u02d9\u02db\u0005"+
		"\u014e\u0000\u0000\u02da\u02dc\u00050\u0000\u0000\u02db\u02da\u0001\u0000"+
		"\u0000\u0000\u02db\u02dc\u0001\u0000\u0000\u0000\u02dc\u02dd\u0001\u0000"+
		"\u0000\u0000\u02dd\u02e2\u0003\u00b6[\u0000\u02de\u02df\u0005\u0116\u0000"+
		"\u0000\u02df\u02e1\u0003\u00b6[\u0000\u02e0\u02de\u0001\u0000\u0000\u0000"+
		"\u02e1\u02e4\u0001\u0000\u0000\u0000\u02e2\u02e0\u0001\u0000\u0000\u0000"+
		"\u02e2\u02e3\u0001\u0000\u0000\u0000\u02e3/\u0001\u0000\u0000\u0000\u02e4"+
		"\u02e2\u0001\u0000\u0000\u0000\u02e5\u02e6\u0005\t\u0000\u0000\u02e6\u02e7"+
		"\u00050\u0000\u0000\u02e7\u02e8\u0003\u00b6[\u0000\u02e8\u02e9\u0005\u0001"+
		"\u0000\u0000\u02e9\u02ea\u0003\u0080@\u0000\u02ea\u02f8\u0001\u0000\u0000"+
		"\u0000\u02eb\u02ec\u0005\t\u0000\u0000\u02ec\u02ed\u00050\u0000\u0000"+
		"\u02ed\u02f2\u0003\u00b6[\u0000\u02ee\u02ef\u0005\u011e\u0000\u0000\u02ef"+
		"\u02f0\u0003R)\u0000\u02f0\u02f1\u0005\u011f\u0000\u0000\u02f1\u02f3\u0001"+
		"\u0000\u0000\u0000\u02f2\u02ee\u0001\u0000\u0000\u0000\u02f2\u02f3\u0001"+
		"\u0000\u0000\u0000\u02f3\u02f5\u0001\u0000\u0000\u0000\u02f4\u02f6\u0003"+
		"T*\u0000\u02f5\u02f4\u0001\u0000\u0000\u0000\u02f5\u02f6\u0001\u0000\u0000"+
		"\u0000\u02f6\u02f8\u0001\u0000\u0000\u0000\u02f7\u02e5\u0001\u0000\u0000"+
		"\u0000\u02f7\u02eb\u0001\u0000\u0000\u0000\u02f81\u0001\u0000\u0000\u0000"+
		"\u02f9\u02fa\u0005\t\u0000\u0000\u02fa\u02fb\u0005_\u0000\u0000\u02fb"+
		"\u02fc\u0003\u00b6[\u0000\u02fc\u02fd\u0005&\u0000\u0000\u02fd\u02fe\u0003"+
		"\u00b6[\u0000\u02fe\u02ff\u0005\u011e\u0000\u0000\u02ff\u0300\u0003\u0110"+
		"\u0088\u0000\u0300\u0301\u0005\u011f\u0000\u0000\u03013\u0001\u0000\u0000"+
		"\u0000\u0302\u0303\u0005\t\u0000\u0000\u0303\u0304\u0005\u0176\u0000\u0000"+
		"\u0304\u0305\u0003\u00b6[\u0000\u0305\u0306\u0005\u0001\u0000\u0000\u0306"+
		"\u0307\u0003\u0080@\u0000\u03075\u0001\u0000\u0000\u0000\u0308\u0309\u0005"+
		"\t\u0000\u0000\u0309\u030a\u0005\u016e\u0000\u0000\u030a\u030b\u0005\u0176"+
		"\u0000\u0000\u030b\u030c\u0003\u00b6[\u0000\u030c\u030d\u0005\u0001\u0000"+
		"\u0000\u030d\u030e\u0003\u0080@\u0000\u030e7\u0001\u0000\u0000\u0000\u030f"+
		"\u0310\u0005\t\u0000\u0000\u0310\u0311\u0005\u016c\u0000\u0000\u0311\u0312"+
		"\u0003\u00b6[\u0000\u0312\u0314\u0005\u011e\u0000\u0000\u0313\u0315\u0003"+
		"R)\u0000\u0314\u0313\u0001\u0000\u0000\u0000\u0314\u0315\u0001\u0000\u0000"+
		"\u0000\u0315\u0316\u0001\u0000\u0000\u0000\u0316\u0317\u0005\u011f\u0000"+
		"\u0000\u0317\u0318\u0005\u0170\u0000\u0000\u0318\u0319\u0003\u024e\u0127"+
		"\u0000\u0319\u031a\u0003T*\u0000\u031a9\u0001\u0000\u0000\u0000\u031b"+
		"\u031c\u0005\t\u0000\u0000\u031c\u031d\u0005\u016f\u0000\u0000\u031d\u031e"+
		"\u0003\u00b6[\u0000\u031e\u0320\u0005\u011e\u0000\u0000\u031f\u0321\u0003"+
		"R)\u0000\u0320\u031f\u0001\u0000\u0000\u0000\u0320\u0321\u0001\u0000\u0000"+
		"\u0000\u0321\u0322\u0001\u0000\u0000\u0000\u0322\u0323\u0005\u011f\u0000"+
		"\u0000\u0323\u0324\u0003T*\u0000\u0324;\u0001\u0000\u0000\u0000\u0325"+
		"\u0326\u0005\t\u0000\u0000\u0326\u0327\u0005\u016d\u0000\u0000\u0327\u0328"+
		"\u0003\u00b6[\u0000\u0328\u032a\u0005\u011e\u0000\u0000\u0329\u032b\u0003"+
		"R)\u0000\u032a\u0329\u0001\u0000\u0000\u0000\u032a\u032b\u0001\u0000\u0000"+
		"\u0000\u032b\u032c\u0001\u0000\u0000\u0000\u032c\u032d\u0005\u011f\u0000"+
		"\u0000\u032d\u032e\u0005\u0001\u0000\u0000\u032e\u032f\u0003\u0080@\u0000"+
		"\u032f=\u0001\u0000\u0000\u0000\u0330\u0331\u0005\t\u0000\u0000\u0331"+
		"\u0332\u0005\u0173\u0000\u0000\u0332\u0333\u0003\u00b6[\u0000\u0333\u0334"+
		"\u0003T*\u0000\u0334?\u0001\u0000\u0000\u0000\u0335\u0336\u0005\t\u0000"+
		"\u0000\u0336\u0337\u0005\u0172\u0000\u0000\u0337\u0339\u0003\u00b6[\u0000"+
		"\u0338\u033a\u0003T*\u0000\u0339\u0338\u0001\u0000\u0000\u0000\u0339\u033a"+
		"\u0001\u0000\u0000\u0000\u033aA\u0001\u0000\u0000\u0000\u033b\u033c\u0005"+
		"\t\u0000\u0000\u033c\u033d\u0005\u016a\u0000\u0000\u033d\u033f\u0003\u00b6"+
		"[\u0000\u033e\u0340\u0003T*\u0000\u033f\u033e\u0001\u0000\u0000\u0000"+
		"\u033f\u0340\u0001\u0000\u0000\u0000\u0340C\u0001\u0000\u0000\u0000\u0341"+
		"\u0342\u0005\t\u0000\u0000\u0342\u0343\u0005\u0171\u0000\u0000\u0343\u0345"+
		"\u0003\u00b6[\u0000\u0344\u0346\u0003T*\u0000\u0345\u0344\u0001\u0000"+
		"\u0000\u0000\u0345\u0346\u0001\u0000\u0000\u0000\u0346E\u0001\u0000\u0000"+
		"\u0000\u0347\u0348\u0005\t\u0000\u0000\u0348\u0349\u0005\u0175\u0000\u0000"+
		"\u0349\u034b\u0003\u00b6[\u0000\u034a\u034c\u0003T*\u0000\u034b\u034a"+
		"\u0001\u0000\u0000\u0000\u034b\u034c\u0001\u0000\u0000\u0000\u034cG\u0001"+
		"\u0000\u0000\u0000\u034d\u034e\u0005\t\u0000\u0000\u034e\u034f\u0005\u0174"+
		"\u0000\u0000\u034f\u0351\u0003\u00b6[\u0000\u0350\u0352\u0003T*\u0000"+
		"\u0351\u0350\u0001\u0000\u0000\u0000\u0351\u0352\u0001\u0000\u0000\u0000"+
		"\u0352I\u0001\u0000\u0000\u0000\u0353\u0354\u0005\t\u0000\u0000\u0354"+
		"\u0355\u0005\u016b\u0000\u0000\u0355\u0356\u0005Z\u0000\u0000\u0356\u0358"+
		"\u0003\u00b6[\u0000\u0357\u0359\u0003T*\u0000\u0358\u0357\u0001\u0000"+
		"\u0000\u0000\u0358\u0359\u0001\u0000\u0000\u0000\u0359K\u0001\u0000\u0000"+
		"\u0000\u035a\u036b\u00050\u0000\u0000\u035b\u036b\u0005_\u0000\u0000\u035c"+
		"\u036b\u0005\u0176\u0000\u0000\u035d\u036b\u0005\u016c\u0000\u0000\u035e"+
		"\u036b\u0005\u016f\u0000\u0000\u035f\u036b\u0005\u016d\u0000\u0000\u0360"+
		"\u036b\u0005\u0173\u0000\u0000\u0361\u036b\u0005\u0172\u0000\u0000\u0362"+
		"\u036b\u0005\u016a\u0000\u0000\u0363\u036b\u0005\u0171\u0000\u0000\u0364"+
		"\u036b\u0005\u0175\u0000\u0000\u0365\u036b\u0005\u0174\u0000\u0000\u0366"+
		"\u0367\u0005\u016b\u0000\u0000\u0367\u036b\u0005Z\u0000\u0000\u0368\u0369"+
		"\u0005\u016e\u0000\u0000\u0369\u036b\u0005\u0176\u0000\u0000\u036a\u035a"+
		"\u0001\u0000\u0000\u0000\u036a\u035b\u0001\u0000\u0000\u0000\u036a\u035c"+
		"\u0001\u0000\u0000\u0000\u036a\u035d\u0001\u0000\u0000\u0000\u036a\u035e"+
		"\u0001\u0000\u0000\u0000\u036a\u035f\u0001\u0000\u0000\u0000\u036a\u0360"+
		"\u0001\u0000\u0000\u0000\u036a\u0361\u0001\u0000\u0000\u0000\u036a\u0362"+
		"\u0001\u0000\u0000\u0000\u036a\u0363\u0001\u0000\u0000\u0000\u036a\u0364"+
		"\u0001\u0000\u0000\u0000\u036a\u0365\u0001\u0000\u0000\u0000\u036a\u0366"+
		"\u0001\u0000\u0000\u0000\u036a\u0368\u0001\u0000\u0000\u0000\u036bM\u0001"+
		"\u0000\u0000\u0000\u036c\u036d\u0003T*\u0000\u036dO\u0001\u0000\u0000"+
		"\u0000\u036e\u036f\u0003T*\u0000\u036fQ\u0001\u0000\u0000\u0000\u0370"+
		"\u0372\b\u0000\u0000\u0000\u0371\u0370\u0001\u0000\u0000\u0000\u0372\u0373"+
		"\u0001\u0000\u0000\u0000\u0373\u0371\u0001\u0000\u0000\u0000\u0373\u0374"+
		"\u0001\u0000\u0000\u0000\u0374S\u0001\u0000\u0000\u0000\u0375\u0377\b"+
		"\u0001\u0000\u0000\u0376\u0375\u0001\u0000\u0000\u0000\u0377\u0378\u0001"+
		"\u0000\u0000\u0000\u0378\u0376\u0001\u0000\u0000\u0000\u0378\u0379\u0001"+
		"\u0000\u0000\u0000\u0379U\u0001\u0000\u0000\u0000\u037a\u037b\u0003X,"+
		"\u0000\u037b\u037c\u0003`0\u0000\u037cW\u0001\u0000\u0000\u0000\u037d"+
		"\u037e\u0005:\u0000\u0000\u037e\u0383\u0003Z-\u0000\u037f\u0380\u0005"+
		"\u0116\u0000\u0000\u0380\u0382\u0003Z-\u0000\u0381\u037f\u0001\u0000\u0000"+
		"\u0000\u0382\u0385\u0001\u0000\u0000\u0000\u0383\u0381\u0001\u0000\u0000"+
		"\u0000\u0383\u0384\u0001\u0000\u0000\u0000\u0384Y\u0001\u0000\u0000\u0000"+
		"\u0385\u0383\u0001\u0000\u0000\u0000\u0386\u0387\u0003^/\u0000\u0387\u0388"+
		"\u0005\u011e\u0000\u0000\u0388\u0389\u0003\\.\u0000\u0389\u038a\u0005"+
		"\u011f\u0000\u0000\u038a\u038f\u0001\u0000\u0000\u0000\u038b\u038c\u0003"+
		"^/\u0000\u038c\u038d\u0003\u0218\u010c\u0000\u038d\u038f\u0001\u0000\u0000"+
		"\u0000\u038e\u0386\u0001\u0000\u0000\u0000\u038e\u038b\u0001\u0000\u0000"+
		"\u0000\u038f[\u0001\u0000\u0000\u0000\u0390\u0393\u0003V+\u0000\u0391"+
		"\u0393\u0003`0\u0000\u0392\u0390\u0001\u0000\u0000\u0000\u0392\u0391\u0001"+
		"\u0000\u0000\u0000\u0393]\u0001\u0000\u0000\u0000\u0394\u0395\u0003\u0214"+
		"\u010a\u0000\u0395\u0396\u0005\u0001\u0000\u0000\u0396_\u0001\u0000\u0000"+
		"\u0000\u0397\u039d\u0003\u0080@\u0000\u0398\u039d\u0003b1\u0000\u0399"+
		"\u039d\u0003n7\u0000\u039a\u039d\u0003p8\u0000\u039b\u039d\u0003\u01da"+
		"\u00ed\u0000\u039c\u0397\u0001\u0000\u0000\u0000\u039c\u0398\u0001\u0000"+
		"\u0000\u0000\u039c\u0399\u0001\u0000\u0000\u0000\u039c\u039a\u0001\u0000"+
		"\u0000\u0000\u039c\u039b\u0001\u0000\u0000\u0000\u039da\u0001\u0000\u0000"+
		"\u0000\u039e\u039f\u0003d2\u0000\u039fc\u0001\u0000\u0000\u0000\u03a0"+
		"\u03a1\u0003j5\u0000\u03a1\u03a2\u0003f3\u0000\u03a2\u03a3\u0003l6\u0000"+
		"\u03a3e\u0001\u0000\u0000\u0000\u03a4\u03a8\u0003\u00b6[\u0000\u03a5\u03a8"+
		"\u0003\u0218\u010c\u0000\u03a6\u03a8\u0003\u0222\u0111\u0000\u03a7\u03a4"+
		"\u0001\u0000\u0000\u0000\u03a7\u03a5\u0001\u0000\u0000\u0000\u03a7\u03a6"+
		"\u0001\u0000\u0000\u0000\u03a8\u03ad\u0001\u0000\u0000\u0000\u03a9\u03aa"+
		"\u0005\u011e\u0000\u0000\u03aa\u03ab\u0003\u0110\u0088\u0000\u03ab\u03ac"+
		"\u0005\u011f\u0000\u0000\u03ac\u03ae\u0001\u0000\u0000\u0000\u03ad\u03a9"+
		"\u0001\u0000\u0000\u0000\u03ad\u03ae\u0001\u0000\u0000\u0000\u03ae\u03b0"+
		"\u0001\u0000\u0000\u0000\u03af\u03b1\u0003\u00b0X\u0000\u03b0\u03af\u0001"+
		"\u0000\u0000\u0000\u03b0\u03b1\u0001\u0000\u0000\u0000\u03b1g\u0001\u0000"+
		"\u0000\u0000\u03b2\u03b4\u0003d2\u0000\u03b3\u03b5\u0003z=\u0000\u03b4"+
		"\u03b3\u0001\u0000\u0000\u0000\u03b4\u03b5\u0001\u0000\u0000\u0000\u03b5"+
		"i\u0001\u0000\u0000\u0000\u03b6\u03b8\u0005`\u0000\u0000\u03b7\u03b9\u0005"+
		"w\u0000\u0000\u03b8\u03b7\u0001\u0000\u0000\u0000\u03b8\u03b9\u0001\u0000"+
		"\u0000\u0000\u03b9\u03ba\u0001\u0000\u0000\u0000\u03ba\u03bb\u0005\u0019"+
		"\u0000\u0000\u03bbk\u0001\u0000\u0000\u0000\u03bc\u03c0\u0003\u0080@\u0000"+
		"\u03bd\u03c0\u0003\u0218\u010c\u0000\u03be\u03c0\u0003\u01ea\u00f5\u0000"+
		"\u03bf\u03bc\u0001\u0000\u0000\u0000\u03bf\u03bd\u0001\u0000\u0000\u0000"+
		"\u03bf\u03be\u0001\u0000\u0000\u0000\u03c0m\u0001\u0000\u0000\u0000\u03c1"+
		"\u03c2\u0005\u0094\u0000\u0000\u03c2\u03c3\u0003\u00acV\u0000\u03c3\u03c4"+
		"\u0005\u0087\u0000\u0000\u03c4\u03c6\u0003|>\u0000\u03c5\u03c7\u0003\u00a2"+
		"Q\u0000\u03c6\u03c5\u0001\u0000\u0000\u0000\u03c6\u03c7\u0001\u0000\u0000"+
		"\u0000\u03c7\u03c9\u0001\u0000\u0000\u0000\u03c8\u03ca\u0003\u0198\u00cc"+
		"\u0000\u03c9\u03c8\u0001\u0000\u0000\u0000\u03c9\u03ca\u0001\u0000\u0000"+
		"\u0000\u03ca\u03cc\u0001\u0000\u0000\u0000\u03cb\u03cd\u0003z=\u0000\u03cc"+
		"\u03cb\u0001\u0000\u0000\u0000\u03cc\u03cd\u0001\u0000\u0000\u0000\u03cd"+
		"o\u0001\u0000\u0000\u0000\u03ce\u03d1\u0003r9\u0000\u03cf\u03d1\u0003"+
		"t:\u0000\u03d0\u03ce\u0001\u0000\u0000\u0000\u03d0\u03cf\u0001\u0000\u0000"+
		"\u0000\u03d1q\u0001\u0000\u0000\u0000\u03d2\u03d3\u0005\u014d\u0000\u0000"+
		"\u03d3\u03d4\u0005\u0011\u0000\u0000\u03d4\u03d6\u0003\u00acV\u0000\u03d5"+
		"\u03d7\u0003x<\u0000\u03d6\u03d5\u0001\u0000\u0000\u0000\u03d6\u03d7\u0001"+
		"\u0000\u0000\u0000\u03d7\u03d9\u0001\u0000\u0000\u0000\u03d8\u03da\u0003"+
		"\u0198\u00cc\u0000\u03d9\u03d8\u0001\u0000\u0000\u0000\u03d9\u03da\u0001"+
		"\u0000\u0000\u0000\u03das\u0001\u0000\u0000\u0000\u03db\u03dc\u0005\u014d"+
		"\u0000\u0000\u03dc\u03dd\u0005\u0011\u0000\u0000\u03dd\u03df\u0003\u00ac"+
		"V\u0000\u03de\u03e0\u0003x<\u0000\u03df\u03de\u0001\u0000\u0000\u0000"+
		"\u03df\u03e0\u0001\u0000\u0000\u0000\u03e0\u03e2\u0001\u0000\u0000\u0000"+
		"\u03e1\u03e3\u0003\u0198\u00cc\u0000\u03e2\u03e1\u0001\u0000\u0000\u0000"+
		"\u03e2\u03e3\u0001\u0000\u0000\u0000\u03e3\u03e4\u0001\u0000\u0000\u0000"+
		"\u03e4\u03e5\u0003v;\u0000\u03e5u\u0001\u0000\u0000\u0000\u03e6\u03e7"+
		"\u0005,\u0000\u0000\u03e7\u03e8\u0003\u0098L\u0000\u03e8w\u0001\u0000"+
		"\u0000\u0000\u03e9\u03ea\u00057\u0000\u0000\u03ea\u03eb\u0003\u00a6S\u0000"+
		"\u03eby\u0001\u0000\u0000\u0000\u03ec\u03ed\u0005,\u0000\u0000\u03ed\u03ee"+
		"\u0005\u0122\u0000\u0000\u03ee{\u0001\u0000\u0000\u0000\u03ef\u03f4\u0003"+
		"~?\u0000\u03f0\u03f1\u0005\u0116\u0000\u0000\u03f1\u03f3\u0003~?\u0000"+
		"\u03f2\u03f0\u0001\u0000\u0000\u0000\u03f3\u03f6\u0001\u0000\u0000\u0000"+
		"\u03f4\u03f2\u0001\u0000\u0000\u0000\u03f4\u03f5\u0001\u0000\u0000\u0000"+
		"\u03f5}\u0001\u0000\u0000\u0000\u03f6\u03f4\u0001\u0000\u0000\u0000\u03f7"+
		"\u03f8\u0003\u0112\u0089\u0000\u03f8\u03f9\u0005\u0113\u0000\u0000\u03f9"+
		"\u03fa\u0003\u0196\u00cb\u0000\u03fa\u007f\u0001\u0000\u0000\u0000\u03fb"+
		"\u03fc\u0003\u0082A\u0000\u03fc\u0081\u0001\u0000\u0000\u0000\u03fd\u0403"+
		"\u0003\u0088D\u0000\u03fe\u03ff\u0003\u0084B\u0000\u03ff\u0400\u0003\u0088"+
		"D\u0000\u0400\u0402\u0001\u0000\u0000\u0000\u0401\u03fe\u0001\u0000\u0000"+
		"\u0000\u0402\u0405\u0001\u0000\u0000\u0000\u0403\u0401\u0001\u0000\u0000"+
		"\u0000\u0403\u0404\u0001\u0000\u0000\u0000\u0404\u0083\u0001\u0000\u0000"+
		"\u0000\u0405\u0403\u0001\u0000\u0000\u0000\u0406\u0408\u0003\u0086C\u0000"+
		"\u0407\u0409\u0003\u0096K\u0000\u0408\u0407\u0001\u0000\u0000\u0000\u0408"+
		"\u0409\u0001\u0000\u0000\u0000\u0409\u0085\u0001\u0000\u0000\u0000\u040a"+
		"\u040b\u0005\u0018\u0000\u0000\u040b\u0087\u0001\u0000\u0000\u0000\u040c"+
		"\u0412\u0003\u008eG\u0000\u040d\u040e\u0003\u008aE\u0000\u040e\u040f\u0003"+
		"\u008eG\u0000\u040f\u0411\u0001\u0000\u0000\u0000\u0410\u040d\u0001\u0000"+
		"\u0000\u0000\u0411\u0414\u0001\u0000\u0000\u0000\u0412\u0410\u0001\u0000"+
		"\u0000\u0000\u0412\u0413\u0001\u0000\u0000\u0000\u0413\u0089\u0001\u0000"+
		"\u0000\u0000\u0414\u0412\u0001\u0000\u0000\u0000\u0415\u0417\u0003\u008c"+
		"F\u0000\u0416\u0418\u0003\u0096K\u0000\u0417\u0416\u0001\u0000\u0000\u0000"+
		"\u0417\u0418\u0001\u0000\u0000\u0000\u0418\u008b\u0001\u0000\u0000\u0000"+
		"\u0419\u041a\u0007\u0002\u0000\u0000\u041a\u008d\u0001\u0000\u0000\u0000"+
		"\u041b\u041f\u0003\u0090H\u0000\u041c\u041f\u0003\u0092I\u0000\u041d\u041f"+
		"\u0003\u0218\u010c\u0000\u041e\u041b\u0001\u0000\u0000\u0000\u041e\u041c"+
		"\u0001\u0000\u0000\u0000\u041e\u041d\u0001\u0000\u0000\u0000\u041f\u008f"+
		"\u0001\u0000\u0000\u0000\u0420\u0421\u0005\u011e\u0000\u0000\u0421\u0422"+
		"\u0003\u0080@\u0000\u0422\u0423\u0005\u011f\u0000\u0000\u0423\u0091\u0001"+
		"\u0000\u0000\u0000\u0424\u0426\u0005-\u0000\u0000\u0425\u0427\u0003\u0094"+
		"J\u0000\u0426\u0425\u0001\u0000\u0000\u0000\u0426\u0427\u0001\u0000\u0000"+
		"\u0000\u0427\u0429\u0001\u0000\u0000\u0000\u0428\u042a\u0003\u0096K\u0000"+
		"\u0429\u0428\u0001\u0000\u0000\u0000\u0429\u042a\u0001\u0000\u0000\u0000"+
		"\u042a\u042b\u0001\u0000\u0000\u0000\u042b\u043f\u0003\u0098L\u0000\u042c"+
		"\u042e\u0003\u00a2Q\u0000\u042d\u042f\u0003\u0198\u00cc\u0000\u042e\u042d"+
		"\u0001\u0000\u0000\u0000\u042e\u042f\u0001\u0000\u0000\u0000\u042f\u0431"+
		"\u0001\u0000\u0000\u0000\u0430\u0432\u0003\u01aa\u00d5\u0000\u0431\u0430"+
		"\u0001\u0000\u0000\u0000\u0431\u0432\u0001\u0000\u0000\u0000\u0432\u0434"+
		"\u0001\u0000\u0000\u0000\u0433\u0435\u0003\u01ba\u00dd\u0000\u0434\u0433"+
		"\u0001\u0000\u0000\u0000\u0434\u0435\u0001\u0000\u0000\u0000\u0435\u0437"+
		"\u0001\u0000\u0000\u0000\u0436\u0438\u0003\u01bc\u00de\u0000\u0437\u0436"+
		"\u0001\u0000\u0000\u0000\u0437\u0438\u0001\u0000\u0000\u0000\u0438\u043a"+
		"\u0001\u0000\u0000\u0000\u0439\u043b\u0003\u019c\u00ce\u0000\u043a\u0439"+
		"\u0001\u0000\u0000\u0000\u043a\u043b\u0001\u0000\u0000\u0000\u043b\u043d"+
		"\u0001\u0000\u0000\u0000\u043c\u043e\u0003\u01a8\u00d4\u0000\u043d\u043c"+
		"\u0001\u0000\u0000\u0000\u043d\u043e\u0001\u0000\u0000\u0000\u043e\u0440"+
		"\u0001\u0000\u0000\u0000\u043f\u042c\u0001\u0000\u0000\u0000\u043f\u0440"+
		"\u0001\u0000\u0000\u0000\u0440\u0093\u0001\u0000\u0000\u0000\u0441\u0442"+
		"\u0005\u0019\u0000\u0000\u0442\u0443\u0003\u00b6[\u0000\u0443\u0095\u0001"+
		"\u0000\u0000\u0000\u0444\u0445\u0007\u0003\u0000\u0000\u0445\u0097\u0001"+
		"\u0000\u0000\u0000\u0446\u044b\u0003\u009aM\u0000\u0447\u0448\u0005\u0116"+
		"\u0000\u0000\u0448\u044a\u0003\u009aM\u0000\u0449\u0447\u0001\u0000\u0000"+
		"\u0000\u044a\u044d\u0001\u0000\u0000\u0000\u044b\u0449\u0001\u0000\u0000"+
		"\u0000\u044b\u044c\u0001\u0000\u0000\u0000\u044c\u0099\u0001\u0000\u0000"+
		"\u0000\u044d\u044b\u0001\u0000\u0000\u0000\u044e\u0450\u0003\u015a\u00ad"+
		"\u0000\u044f\u0451\u0003\u009cN\u0000\u0450\u044f\u0001\u0000\u0000\u0000"+
		"\u0450\u0451\u0001\u0000\u0000\u0000\u0451\u0454\u0001\u0000\u0000\u0000"+
		"\u0452\u0454\u0003\u009eO\u0000\u0453\u044e\u0001\u0000\u0000\u0000\u0453"+
		"\u0452\u0001\u0000\u0000\u0000\u0454\u009b\u0001\u0000\u0000\u0000\u0455"+
		"\u0457\u0005\u0001\u0000\u0000\u0456\u0455\u0001\u0000\u0000\u0000\u0456"+
		"\u0457\u0001\u0000\u0000\u0000\u0457\u0458\u0001\u0000\u0000\u0000\u0458"+
		"\u0459\u0003\u0216\u010b\u0000\u0459\u009d\u0001\u0000\u0000\u0000\u045a"+
		"\u045b\u0003\u00a0P\u0000\u045b\u009f\u0001\u0000\u0000\u0000\u045c\u045d"+
		"\u0005\u017c\u0000\u0000\u045d\u045f\u0005\u0125\u0000\u0000\u045e\u045c"+
		"\u0001\u0000\u0000\u0000\u045e\u045f\u0001\u0000\u0000\u0000\u045f\u0460"+
		"\u0001\u0000\u0000\u0000\u0460\u0461\u0005\u0122\u0000\u0000\u0461\u00a1"+
		"\u0001\u0000\u0000\u0000\u0462\u0463\u0005\u0011\u0000\u0000\u0463\u0465"+
		"\u0003\u00a6S\u0000\u0464\u0466\u0003\u00a4R\u0000\u0465\u0464\u0001\u0000"+
		"\u0000\u0000\u0465\u0466\u0001\u0000\u0000\u0000\u0466\u00a3\u0001\u0000"+
		"\u0000\u0000\u0467\u0468\u0003\u0218\u010c\u0000\u0468\u00a5\u0001\u0000"+
		"\u0000\u0000\u0469\u047f\u0003\u00acV\u0000\u046a\u046c\u0005\u0116\u0000"+
		"\u0000\u046b\u046d\u0003\u00aaU\u0000\u046c\u046b\u0001\u0000\u0000\u0000"+
		"\u046c\u046d\u0001\u0000\u0000\u0000\u046d\u046e\u0001\u0000\u0000\u0000"+
		"\u046e\u047e\u0003\u00acV\u0000\u046f\u0471\u0003\u00b8\\\u0000\u0470"+
		"\u0472\u0003\u00aaU\u0000\u0471\u0470\u0001\u0000\u0000\u0000\u0471\u0472"+
		"\u0001\u0000\u0000\u0000\u0472\u0473\u0001\u0000\u0000\u0000\u0473\u0474"+
		"\u0003\u00acV\u0000\u0474\u047e\u0001\u0000\u0000\u0000\u0475\u0477\u0003"+
		"\u00ba]\u0000\u0476\u0478\u0003\u00aaU\u0000\u0477\u0476\u0001\u0000\u0000"+
		"\u0000\u0477\u0478\u0001\u0000\u0000\u0000\u0478\u0479\u0001\u0000\u0000"+
		"\u0000\u0479\u047b\u0003\u00acV\u0000\u047a\u047c\u0003\u00be_\u0000\u047b"+
		"\u047a\u0001\u0000\u0000\u0000\u047b\u047c\u0001\u0000\u0000\u0000\u047c"+
		"\u047e\u0001\u0000\u0000\u0000\u047d\u046a\u0001\u0000\u0000\u0000\u047d"+
		"\u046f\u0001\u0000\u0000\u0000\u047d\u0475\u0001\u0000\u0000\u0000\u047e"+
		"\u0481\u0001\u0000\u0000\u0000\u047f\u047d\u0001\u0000\u0000\u0000\u047f"+
		"\u0480\u0001\u0000\u0000\u0000\u0480\u00a7\u0001\u0000\u0000\u0000\u0481"+
		"\u047f\u0001\u0000\u0000\u0000\u0482\u0484\u0005\u0116\u0000\u0000\u0483"+
		"\u0485\u0003\u00aaU\u0000\u0484\u0483\u0001\u0000\u0000\u0000\u0484\u0485"+
		"\u0001\u0000\u0000\u0000\u0485\u0486\u0001\u0000\u0000\u0000\u0486\u0496"+
		"\u0003\u00acV\u0000\u0487\u0489\u0003\u00b8\\\u0000\u0488\u048a\u0003"+
		"\u00aaU\u0000\u0489\u0488\u0001\u0000\u0000\u0000\u0489\u048a\u0001\u0000"+
		"\u0000\u0000\u048a\u048b\u0001\u0000\u0000\u0000\u048b\u048c\u0003\u00ac"+
		"V\u0000\u048c\u0496\u0001\u0000\u0000\u0000\u048d\u048f\u0003\u00ba]\u0000"+
		"\u048e\u0490\u0003\u00aaU\u0000\u048f\u048e\u0001\u0000\u0000\u0000\u048f"+
		"\u0490\u0001\u0000\u0000\u0000\u0490\u0491\u0001\u0000\u0000\u0000\u0491"+
		"\u0493\u0003\u00acV\u0000\u0492\u0494\u0003\u00be_\u0000\u0493\u0492\u0001"+
		"\u0000\u0000\u0000\u0493\u0494\u0001\u0000\u0000\u0000\u0494\u0496\u0001"+
		"\u0000\u0000\u0000\u0495\u0482\u0001\u0000\u0000\u0000\u0495\u0487\u0001"+
		"\u0000\u0000\u0000\u0495\u048d\u0001\u0000\u0000\u0000\u0496\u0499\u0001"+
		"\u0000\u0000\u0000\u0497\u0495\u0001\u0000\u0000\u0000\u0497\u0498\u0001"+
		"\u0000\u0000\u0000\u0498\u049b\u0001\u0000\u0000\u0000\u0499\u0497\u0001"+
		"\u0000\u0000\u0000\u049a\u049c\u0003\u00a4R\u0000\u049b\u049a\u0001\u0000"+
		"\u0000\u0000\u049b\u049c\u0001\u0000\u0000\u0000\u049c\u00a9\u0001\u0000"+
		"\u0000\u0000\u049d\u049e\u0005\u015f\u0000\u0000\u049e\u00ab\u0001\u0000"+
		"\u0000\u0000\u049f\u04a1\u0003\u00aeW\u0000\u04a0\u04a2\u0003\u00c6c\u0000"+
		"\u04a1\u04a0\u0001\u0000\u0000\u0000\u04a1\u04a2\u0001\u0000\u0000\u0000"+
		"\u04a2\u04a4\u0001\u0000\u0000\u0000\u04a3\u04a5\u0003\u00b0X\u0000\u04a4"+
		"\u04a3\u0001\u0000\u0000\u0000\u04a4\u04a5\u0001\u0000\u0000\u0000\u04a5"+
		"\u00ad\u0001\u0000\u0000\u0000\u04a6\u04a8\u0003\u00b6[\u0000\u04a7\u04a9"+
		"\u0003\u00b0X\u0000\u04a8\u04a7\u0001\u0000\u0000\u0000\u04a8\u04a9\u0001"+
		"\u0000\u0000\u0000\u04a9\u04bb\u0001\u0000\u0000\u0000\u04aa\u04ab\u0003"+
		"\u0218\u010c\u0000\u04ab\u04ac\u0003\u009cN\u0000\u04ac\u04bb\u0001\u0000"+
		"\u0000\u0000\u04ad\u04af\u0003\u0222\u0111\u0000\u04ae\u04b0\u0003\u00b0"+
		"X\u0000\u04af\u04ae\u0001\u0000\u0000\u0000\u04af\u04b0\u0001\u0000\u0000"+
		"\u0000\u04b0\u04bb\u0001\u0000\u0000\u0000\u04b1\u04b3\u0003\u00dam\u0000"+
		"\u04b2\u04b4\u0003\u00b0X\u0000\u04b3\u04b2\u0001\u0000\u0000\u0000\u04b3"+
		"\u04b4\u0001\u0000\u0000\u0000\u04b4\u04bb\u0001\u0000\u0000\u0000\u04b5"+
		"\u04bb\u0003\u01da\u00ed\u0000\u04b6\u04b8\u0003\u0090H\u0000\u04b7\u04b9"+
		"\u0003\u00b0X\u0000\u04b8\u04b7\u0001\u0000\u0000\u0000\u04b8\u04b9\u0001"+
		"\u0000\u0000\u0000\u04b9\u04bb\u0001\u0000\u0000\u0000\u04ba\u04a6\u0001"+
		"\u0000\u0000\u0000\u04ba\u04aa\u0001\u0000\u0000\u0000\u04ba\u04ad\u0001"+
		"\u0000\u0000\u0000\u04ba\u04b1\u0001\u0000\u0000\u0000\u04ba\u04b5\u0001"+
		"\u0000\u0000\u0000\u04ba\u04b6\u0001\u0000\u0000\u0000\u04bb\u00af\u0001"+
		"\u0000\u0000\u0000\u04bc\u04bd\u0005\u0001\u0000\u0000\u04bd\u04c1\u0003"+
		"\u0216\u010b\u0000\u04be\u04bf\u0004X\u0000\u0000\u04bf\u04c1\u0003\u0216"+
		"\u010b\u0000\u04c0\u04bc\u0001\u0000\u0000\u0000\u04c0\u04be\u0001\u0000"+
		"\u0000\u0000\u04c1\u00b1\u0001\u0000\u0000\u0000\u04c2\u04c4\u0003\u00b4"+
		"Z\u0000\u04c3\u04c5\u0003\u00c6c\u0000\u04c4\u04c3\u0001\u0000\u0000\u0000"+
		"\u04c4\u04c5\u0001\u0000\u0000\u0000\u04c5\u00b3\u0001\u0000\u0000\u0000"+
		"\u04c6\u04cd\u0003\u00b6[\u0000\u04c7\u04cd\u0003\u0218\u010c\u0000\u04c8"+
		"\u04cd\u0003\u0222\u0111\u0000\u04c9\u04cd\u0003\u00dam\u0000\u04ca\u04cd"+
		"\u0003\u01da\u00ed\u0000\u04cb\u04cd\u0003\u0090H\u0000\u04cc\u04c6\u0001"+
		"\u0000\u0000\u0000\u04cc\u04c7\u0001\u0000\u0000\u0000\u04cc\u04c8\u0001"+
		"\u0000\u0000\u0000\u04cc\u04c9\u0001\u0000\u0000\u0000\u04cc\u04ca\u0001"+
		"\u0000\u0000\u0000\u04cc\u04cb\u0001\u0000\u0000\u0000\u04cd\u00b5\u0001"+
		"\u0000\u0000\u0000\u04ce\u04d4\u0003\u0214\u010a\u0000\u04cf\u04d2\u0005"+
		"\u0125\u0000\u0000\u04d0\u04d3\u0003\u022e\u0117\u0000\u04d1\u04d3\u0003"+
		"\u0214\u010a\u0000\u04d2\u04d0\u0001\u0000\u0000\u0000\u04d2\u04d1\u0001"+
		"\u0000\u0000\u0000\u04d3\u04d5\u0001\u0000\u0000\u0000\u04d4\u04cf\u0001"+
		"\u0000\u0000\u0000\u04d4\u04d5\u0001\u0000\u0000\u0000\u04d5\u04db\u0001"+
		"\u0000\u0000\u0000\u04d6\u04d9\u0005\u0125\u0000\u0000\u04d7\u04da\u0003"+
		"\u022e\u0117\u0000\u04d8\u04da\u0003\u0214\u010a\u0000\u04d9\u04d7\u0001"+
		"\u0000\u0000\u0000\u04d9\u04d8\u0001\u0000\u0000\u0000\u04da\u04dc\u0001"+
		"\u0000\u0000\u0000\u04db\u04d6\u0001\u0000\u0000\u0000\u04db\u04dc\u0001"+
		"\u0000\u0000\u0000\u04dc\u00b7\u0001\u0000\u0000\u0000\u04dd\u04de\u0005"+
		"\n\u0000\u0000\u04de\u04e7\u0005\u001b\u0000\u0000\u04df\u04e0\u00055"+
		"\u0000\u0000\u04e0\u04e7\u0005\u001b\u0000\u0000\u04e1\u04e3\u0005 \u0000"+
		"\u0000\u04e2\u04e4\u0003\u00bc^\u0000\u04e3\u04e2\u0001\u0000\u0000\u0000"+
		"\u04e3\u04e4\u0001\u0000\u0000\u0000\u04e4\u04e5\u0001\u0000\u0000\u0000"+
		"\u04e5\u04e7\u0005\u001b\u0000\u0000\u04e6\u04dd\u0001\u0000\u0000\u0000"+
		"\u04e6\u04df\u0001\u0000\u0000\u0000\u04e6\u04e1\u0001\u0000\u0000\u0000"+
		"\u04e7\u00b9\u0001\u0000\u0000\u0000\u04e8\u04ea\u0003\u00bc^\u0000\u04e9"+
		"\u04e8\u0001\u0000\u0000\u0000\u04e9\u04ea\u0001\u0000\u0000\u0000\u04ea"+
		"\u04eb\u0001\u0000\u0000\u0000\u04eb\u04ec\u0005\u001b\u0000\u0000\u04ec"+
		"\u00bb\u0001\u0000\u0000\u0000\u04ed\u04f3\u0005\u0017\u0000\u0000\u04ee"+
		"\u04f0\u0007\u0004\u0000\u0000\u04ef\u04f1\u0005\'\u0000\u0000\u04f0\u04ef"+
		"\u0001\u0000\u0000\u0000\u04f0\u04f1\u0001\u0000\u0000\u0000\u04f1\u04f3"+
		"\u0001\u0000\u0000\u0000\u04f2\u04ed\u0001\u0000\u0000\u0000\u04f2\u04ee"+
		"\u0001\u0000\u0000\u0000\u04f3\u00bd\u0001\u0000\u0000\u0000\u04f4\u04f7"+
		"\u0003\u00c0`\u0000\u04f5\u04f7\u0003\u00c2a\u0000\u04f6\u04f4\u0001\u0000"+
		"\u0000\u0000\u04f6\u04f5\u0001\u0000\u0000\u0000\u04f7\u00bf\u0001\u0000"+
		"\u0000\u0000\u04f8\u04f9\u0005&\u0000\u0000\u04f9\u04fa\u0003\u019a\u00cd"+
		"\u0000\u04fa\u00c1\u0001\u0000\u0000\u0000\u04fb\u04fc\u0003\u00c4b\u0000"+
		"\u04fc\u04fd\u0005\u011e\u0000\u0000\u04fd\u04fe\u0003\u0110\u0088\u0000"+
		"\u04fe\u04ff\u0005\u011f\u0000\u0000\u04ff\u00c3\u0001\u0000\u0000\u0000"+
		"\u0500\u0501\u00057\u0000\u0000\u0501\u00c5\u0001\u0000\u0000\u0000\u0502"+
		"\u0505\u0003\u00c8d\u0000\u0503\u0505\u0003\u00d6k\u0000\u0504\u0502\u0001"+
		"\u0000\u0000\u0000\u0504\u0503\u0001\u0000\u0000\u0000\u0505\u00c7\u0001"+
		"\u0000\u0000\u0000\u0506\u0508\u0005\u017a\u0000\u0000\u0507\u0509\u0003"+
		"\u00ccf\u0000\u0508\u0507\u0001\u0000\u0000\u0000\u0508\u0509\u0001\u0000"+
		"\u0000\u0000\u0509\u050a\u0001\u0000\u0000\u0000\u050a\u050b\u0005\u011e"+
		"\u0000\u0000\u050b\u050c\u0003\u00ceg\u0000\u050c\u050d\u0005\u0177\u0000"+
		"\u0000\u050d\u050e\u0003\u00d0h\u0000\u050e\u050f\u0005\u0016\u0000\u0000"+
		"\u050f\u0510\u0003\u00cae\u0000\u0510\u0512\u0005\u011f\u0000\u0000\u0511"+
		"\u0513\u0003\u00b0X\u0000\u0512\u0511\u0001\u0000\u0000\u0000\u0512\u0513"+
		"\u0001\u0000\u0000\u0000\u0513\u00c9\u0001\u0000\u0000\u0000\u0514\u0515"+
		"\u0005\u011e\u0000\u0000\u0515\u051a\u0003\u00d2i\u0000\u0516\u0517\u0005"+
		"\u0116\u0000\u0000\u0517\u0519\u0003\u00d2i\u0000\u0518\u0516\u0001\u0000"+
		"\u0000\u0000\u0519\u051c\u0001\u0000\u0000\u0000\u051a\u0518\u0001\u0000"+
		"\u0000\u0000\u051a\u051b\u0001\u0000\u0000\u0000\u051b\u051d\u0001\u0000"+
		"\u0000\u0000\u051c\u051a\u0001\u0000\u0000\u0000\u051d\u051e\u0005\u011f"+
		"\u0000\u0000\u051e\u00cb\u0001\u0000\u0000\u0000\u051f\u0520\u0005\u0178"+
		"\u0000\u0000\u0520\u0524\u0005#\u0000\u0000\u0521\u0522\u0005\u0179\u0000"+
		"\u0000\u0522\u0524\u0005#\u0000\u0000\u0523\u051f\u0001\u0000\u0000\u0000"+
		"\u0523\u0521\u0001\u0000\u0000\u0000\u0524\u00cd\u0001\u0000\u0000\u0000"+
		"\u0525\u0526\u0003\u0216\u010b\u0000\u0526\u00cf\u0001\u0000\u0000\u0000"+
		"\u0527\u0528\u0003\u0216\u010b\u0000\u0528\u00d1\u0001\u0000\u0000\u0000"+
		"\u0529\u052b\u0003\u0112\u0089\u0000\u052a\u052c\u0003\u00d4j\u0000\u052b"+
		"\u052a\u0001\u0000\u0000\u0000\u052b\u052c\u0001\u0000\u0000\u0000\u052c"+
		"\u00d3\u0001\u0000\u0000\u0000\u052d\u052f\u0005\u0001\u0000\u0000\u052e"+
		"\u052d\u0001\u0000\u0000\u0000\u052e\u052f\u0001\u0000\u0000\u0000\u052f"+
		"\u0532\u0001\u0000\u0000\u0000\u0530\u0533\u0003\u0216\u010b\u0000\u0531"+
		"\u0533\u0005\u0184\u0000\u0000\u0532\u0530\u0001\u0000\u0000\u0000\u0532"+
		"\u0531\u0001\u0000\u0000\u0000\u0533\u00d5\u0001\u0000\u0000\u0000\u0534"+
		"\u0535\u0005\u017b\u0000\u0000\u0535\u0536\u0005\u011e\u0000\u0000\u0536"+
		"\u0537\u0003\u00d8l\u0000\u0537\u0538\u0005\u0177\u0000\u0000\u0538\u0539"+
		"\u0003\u00d0h\u0000\u0539\u053a\u0005\u0016\u0000\u0000\u053a\u053b\u0003"+
		"\u00cae\u0000\u053b\u053d\u0005\u011f\u0000\u0000\u053c\u053e\u0003\u00b0"+
		"X\u0000\u053d\u053c\u0001\u0000\u0000\u0000\u053d\u053e\u0001\u0000\u0000"+
		"\u0000\u053e\u00d7\u0001\u0000\u0000\u0000\u053f\u0540\u0003\u0122\u0091"+
		"\u0000\u0540\u0541\u0005\u011e\u0000\u0000\u0541\u0542\u0003\u00ceg\u0000"+
		"\u0542\u0543\u0005\u011f\u0000\u0000\u0543\u00d9\u0001\u0000\u0000\u0000"+
		"\u0544\u0545\u00050\u0000\u0000\u0545\u0546\u0005\u011e\u0000\u0000\u0546"+
		"\u0547\u0003\u00dcn\u0000\u0547\u0548\u0005\u011f\u0000\u0000\u0548\u054b"+
		"\u0001\u0000\u0000\u0000\u0549\u054b\u0003\u00dcn\u0000\u054a\u0544\u0001"+
		"\u0000\u0000\u0000\u054a\u0549\u0001\u0000\u0000\u0000\u054b\u00db\u0001"+
		"\u0000\u0000\u0000\u054c\u0553\u0003\u00deo\u0000\u054d\u0553\u0003\u00ec"+
		"v\u0000\u054e\u0553\u0003\u00f6{\u0000\u054f\u0553\u0003\u00fa}\u0000"+
		"\u0550\u0553\u0003\u0106\u0083\u0000\u0551\u0553\u0003\u010a\u0085\u0000"+
		"\u0552\u054c\u0001\u0000\u0000\u0000\u0552\u054d\u0001\u0000\u0000\u0000"+
		"\u0552\u054e\u0001\u0000\u0000\u0000\u0552\u054f\u0001\u0000\u0000\u0000"+
		"\u0552\u0550\u0001\u0000\u0000\u0000\u0552\u0551\u0001\u0000\u0000\u0000"+
		"\u0553\u00dd\u0001\u0000\u0000\u0000\u0554\u0555\u0003\u00e6s\u0000\u0555"+
		"\u0556\u0005\u011e\u0000\u0000\u0556\u0557\u0003\u00e0p\u0000\u0557\u0558"+
		"\u0005\u011f\u0000\u0000\u0558\u00df\u0001\u0000\u0000\u0000\u0559\u055e"+
		"\u0003\u00e2q\u0000\u055a\u055b\u0005\u0116\u0000\u0000\u055b\u055d\u0003"+
		"\u00e2q\u0000\u055c\u055a\u0001\u0000\u0000\u0000\u055d\u0560\u0001\u0000"+
		"\u0000\u0000\u055e\u055c\u0001\u0000\u0000\u0000\u055e\u055f\u0001\u0000"+
		"\u0000\u0000\u055f\u0561\u0001\u0000\u0000\u0000\u0560\u055e\u0001\u0000"+
		"\u0000\u0000\u0561\u0562\u0004p\u0001\u0001\u0562\u00e1\u0001\u0000\u0000"+
		"\u0000\u0563\u0564\u0005\u015b\u0000\u0000\u0564\u0565\u0005\u011d\u0000"+
		"\u0000\u0565\u0575\u0003\u00e4r\u0000\u0566\u0567\u0005\u015c\u0000\u0000"+
		"\u0567\u0568\u0005\u011d\u0000\u0000\u0568\u0575\u0003\u00e4r\u0000\u0569"+
		"\u056a\u0005\'\u0000\u0000\u056a\u056b\u0005\u011d\u0000\u0000\u056b\u0575"+
		"\u0003\u00e4r\u0000\u056c\u056d\u0005\u015d\u0000\u0000\u056d\u056e\u0005"+
		"\u011d\u0000\u0000\u056e\u0575\u0003\u00e4r\u0000\u056f\u0570\u0005\u015e"+
		"\u0000\u0000\u0570\u0571\u0005\u011d\u0000\u0000\u0571\u0572\u0003\u00e8"+
		"t\u0000\u0572\u0573\u0004q\u0002\u0001\u0573\u0575\u0001\u0000\u0000\u0000"+
		"\u0574\u0563\u0001\u0000\u0000\u0000\u0574\u0566\u0001\u0000\u0000\u0000"+
		"\u0574\u0569\u0001\u0000\u0000\u0000\u0574\u056c\u0001\u0000\u0000\u0000"+
		"\u0574\u056f\u0001\u0000\u0000\u0000\u0575\u00e3\u0001\u0000\u0000\u0000"+
		"\u0576\u057a\u0003\u015a\u00ad\u0000\u0577\u057a\u0003\u00e8t\u0000\u0578"+
		"\u057a\u0003\u00eau\u0000\u0579\u0576\u0001\u0000\u0000\u0000\u0579\u0577"+
		"\u0001\u0000\u0000\u0000\u0579\u0578\u0001\u0000\u0000\u0000\u057a\u00e5"+
		"\u0001\u0000\u0000\u0000\u057b\u057c\u0005\u0152\u0000\u0000\u057c\u00e7"+
		"\u0001\u0000\u0000\u0000\u057d\u0580\u0005\u0184\u0000\u0000\u057e\u0580"+
		"\u0003\u0236\u011b\u0000\u057f\u057d\u0001\u0000\u0000\u0000\u057f\u057e"+
		"\u0001\u0000\u0000\u0000\u0580\u00e9\u0001\u0000\u0000\u0000\u0581\u0582"+
		"\u0007\u0005\u0000\u0000\u0582\u00eb\u0001\u0000\u0000\u0000\u0583\u0584"+
		"\u0003\u00f4z\u0000\u0584\u0585\u0005\u011e\u0000\u0000\u0585\u0586\u0003"+
		"\u00eew\u0000\u0586\u0587\u0005\u011f\u0000\u0000\u0587\u00ed\u0001\u0000"+
		"\u0000\u0000\u0588\u058d\u0003\u00f0x\u0000\u0589\u058a\u0005\u0116\u0000"+
		"\u0000\u058a\u058c\u0003\u00f0x\u0000\u058b\u0589\u0001\u0000\u0000\u0000"+
		"\u058c\u058f\u0001\u0000\u0000\u0000\u058d\u058b\u0001\u0000\u0000\u0000"+
		"\u058d\u058e\u0001\u0000\u0000\u0000\u058e\u0590\u0001\u0000\u0000\u0000"+
		"\u058f\u058d\u0001\u0000\u0000\u0000\u0590\u0591\u0004w\u0003\u0001\u0591"+
		"\u00ef\u0001\u0000\u0000\u0000\u0592\u0593\u0005\u0160\u0000\u0000\u0593"+
		"\u0594\u0005\u011d\u0000\u0000\u0594\u0599\u0003\u00f2y\u0000\u0595\u0596"+
		"\u0005\u0161\u0000\u0000\u0596\u0597\u0005\u011d\u0000\u0000\u0597\u0599"+
		"\u0003\u00f2y\u0000\u0598\u0592\u0001\u0000\u0000\u0000\u0598\u0595\u0001"+
		"\u0000\u0000\u0000\u0599\u00f1\u0001\u0000\u0000\u0000\u059a\u059b\u0003"+
		"\u015e\u00af\u0000\u059b\u00f3\u0001\u0000\u0000\u0000\u059c\u059d\u0005"+
		"\u0155\u0000\u0000\u059d\u00f5\u0001\u0000\u0000\u0000\u059e\u059f\u0003"+
		"\u00f8|\u0000\u059f\u05a1\u0005\u011e\u0000\u0000\u05a0\u05a2\u0003\u015a"+
		"\u00ad\u0000\u05a1\u05a0\u0001\u0000\u0000\u0000\u05a1\u05a2\u0001\u0000"+
		"\u0000\u0000\u05a2\u05a3\u0001\u0000\u0000\u0000\u05a3\u05a4\u0005\u011f"+
		"\u0000\u0000\u05a4\u00f7\u0001\u0000\u0000\u0000\u05a5\u05a6\u0005\u0158"+
		"\u0000\u0000\u05a6\u00f9\u0001\u0000\u0000\u0000\u05a7\u05a8\u0003\u0102"+
		"\u0081\u0000\u05a8\u05a9\u0005\u011e\u0000\u0000\u05a9\u05aa\u0003\u00fc"+
		"~\u0000\u05aa\u05ab\u0005\u011f\u0000\u0000\u05ab\u00fb\u0001\u0000\u0000"+
		"\u0000\u05ac\u05b1\u0003\u00fe\u007f\u0000\u05ad\u05ae\u0005\u0116\u0000"+
		"\u0000\u05ae\u05b0\u0003\u00fe\u007f\u0000\u05af\u05ad\u0001\u0000\u0000"+
		"\u0000\u05b0\u05b3\u0001\u0000\u0000\u0000\u05b1\u05af\u0001\u0000\u0000"+
		"\u0000\u05b1\u05b2\u0001\u0000\u0000\u0000\u05b2\u05b4\u0001\u0000\u0000"+
		"\u0000\u05b3\u05b1\u0001\u0000\u0000\u0000\u05b4\u05b5\u0004~\u0004\u0001"+
		"\u05b5\u00fd\u0001\u0000\u0000\u0000\u05b6\u05b7\u0005j\u0000\u0000\u05b7"+
		"\u05b8\u0005\u011d\u0000\u0000\u05b8\u05cc\u0003\u0100\u0080\u0000\u05b9"+
		"\u05ba\u0005\u0163\u0000\u0000\u05ba\u05bb\u0005\u011d\u0000\u0000\u05bb"+
		"\u05cc\u0003\u0100\u0080\u0000\u05bc\u05bd\u0005\u0162\u0000\u0000\u05bd"+
		"\u05be\u0005\u011d\u0000\u0000\u05be\u05cc\u0003\u0100\u0080\u0000\u05bf"+
		"\u05c0\u0005\u0164\u0000\u0000\u05c0\u05c1\u0005\u011d\u0000\u0000\u05c1"+
		"\u05cc\u0003\u0100\u0080\u0000\u05c2\u05c3\u0005\u0165\u0000\u0000\u05c3"+
		"\u05c4\u0005\u011d\u0000\u0000\u05c4\u05cc\u0003\u0100\u0080\u0000\u05c5"+
		"\u05c6\u0005\u0166\u0000\u0000\u05c6\u05c7\u0005\u011d\u0000\u0000\u05c7"+
		"\u05cc\u0003\u0100\u0080\u0000\u05c8\u05c9\u0005\u0167\u0000\u0000\u05c9"+
		"\u05ca\u0005\u011d\u0000\u0000\u05ca\u05cc\u0003\u0100\u0080\u0000\u05cb"+
		"\u05b6\u0001\u0000\u0000\u0000\u05cb\u05b9\u0001\u0000\u0000\u0000\u05cb"+
		"\u05bc\u0001\u0000\u0000\u0000\u05cb\u05bf\u0001\u0000\u0000\u0000\u05cb"+
		"\u05c2\u0001\u0000\u0000\u0000\u05cb\u05c5\u0001\u0000\u0000\u0000\u05cb"+
		"\u05c8\u0001\u0000\u0000\u0000\u05cc\u00ff\u0001\u0000\u0000\u0000\u05cd"+
		"\u05d2\u0003\u00e8t\u0000\u05ce\u05d2\u0003\u0104\u0082\u0000\u05cf\u05d2"+
		"\u0003\u015e\u00af\u0000\u05d0\u05d2\u0003\u00eau\u0000\u05d1\u05cd\u0001"+
		"\u0000\u0000\u0000\u05d1\u05ce\u0001\u0000\u0000\u0000\u05d1\u05cf\u0001"+
		"\u0000\u0000\u0000\u05d1\u05d0\u0001\u0000\u0000\u0000\u05d2\u0101\u0001"+
		"\u0000\u0000\u0000\u05d3\u05d4\u0005\u0156\u0000\u0000\u05d4\u0103\u0001"+
		"\u0000\u0000\u0000\u05d5\u05d6\u0005\u011e\u0000\u0000\u05d6\u05db\u0005"+
		"\u0184\u0000\u0000\u05d7\u05d8\u0005\u0116\u0000\u0000\u05d8\u05da\u0005"+
		"\u0184\u0000\u0000\u05d9\u05d7\u0001\u0000\u0000\u0000\u05da\u05dd\u0001"+
		"\u0000\u0000\u0000\u05db\u05d9\u0001\u0000\u0000\u0000\u05db\u05dc\u0001"+
		"\u0000\u0000\u0000\u05dc\u05de\u0001\u0000\u0000\u0000\u05dd\u05db\u0001"+
		"\u0000\u0000\u0000\u05de\u05df\u0005\u011f\u0000\u0000\u05df\u0105\u0001"+
		"\u0000\u0000\u0000\u05e0\u05e1\u0003\u0108\u0084\u0000\u05e1\u05e2\u0005"+
		"\u011e\u0000\u0000\u05e2\u05e3\u0003\u00b6[\u0000\u05e3\u05e4\u0005\u0116"+
		"\u0000\u0000\u05e4\u05e5\u0005\u0168\u0000\u0000\u05e5\u05e6\u0005\u011d"+
		"\u0000\u0000\u05e6\u05e7\u0003\u00e8t\u0000\u05e7\u05e8\u0005\u011f\u0000"+
		"\u0000\u05e8\u0107\u0001\u0000\u0000\u0000\u05e9\u05ea\u0005\u0157\u0000"+
		"\u0000\u05ea\u0109\u0001\u0000\u0000\u0000\u05eb\u05ec\u0003\u010c\u0086"+
		"\u0000\u05ec\u05ee\u0005\u011e\u0000\u0000\u05ed\u05ef\u0003\u010e\u0087"+
		"\u0000\u05ee\u05ed\u0001\u0000\u0000\u0000\u05ee\u05ef\u0001\u0000\u0000"+
		"\u0000\u05ef\u05f0\u0001\u0000\u0000\u0000\u05f0\u05f1\u0005\u011f\u0000"+
		"\u0000\u05f1\u010b\u0001\u0000\u0000\u0000\u05f2\u05f7\u0005\u0153\u0000"+
		"\u0000\u05f3\u05f7\u0005\u0154\u0000\u0000\u05f4\u05f7\u0005\u0159\u0000"+
		"\u0000\u05f5\u05f7\u0003\u0214\u010a\u0000\u05f6\u05f2\u0001\u0000\u0000"+
		"\u0000\u05f6\u05f3\u0001\u0000\u0000\u0000\u05f6\u05f4\u0001\u0000\u0000"+
		"\u0000\u05f6\u05f5\u0001\u0000\u0000\u0000\u05f7\u010d\u0001\u0000\u0000"+
		"\u0000\u05f8\u05fd\u0003\u015a\u00ad\u0000\u05f9\u05fa\u0005\u0116\u0000"+
		"\u0000\u05fa\u05fc\u0003\u015a\u00ad\u0000\u05fb\u05f9\u0001\u0000\u0000"+
		"\u0000\u05fc\u05ff\u0001\u0000\u0000\u0000\u05fd\u05fb\u0001\u0000\u0000"+
		"\u0000\u05fd\u05fe\u0001\u0000\u0000\u0000\u05fe\u010f\u0001\u0000\u0000"+
		"\u0000\u05ff\u05fd\u0001\u0000\u0000\u0000\u0600\u0605\u0003\u0112\u0089"+
		"\u0000\u0601\u0602\u0005\u0116\u0000\u0000\u0602\u0604\u0003\u0112\u0089"+
		"\u0000\u0603\u0601\u0001\u0000\u0000\u0000\u0604\u0607\u0001\u0000\u0000"+
		"\u0000\u0605\u0603\u0001\u0000\u0000\u0000\u0605\u0606\u0001\u0000\u0000"+
		"\u0000\u0606\u0111\u0001\u0000\u0000\u0000\u0607\u0605\u0001\u0000\u0000"+
		"\u0000\u0608\u0609\u0003\u0214\u010a\u0000\u0609\u060a\u0005\u0125\u0000"+
		"\u0000\u060a\u060c\u0001\u0000\u0000\u0000\u060b\u0608\u0001\u0000\u0000"+
		"\u0000\u060b\u060c\u0001\u0000\u0000\u0000\u060c\u060d\u0001\u0000\u0000"+
		"\u0000\u060d\u0612\u0003\u0214\u010a\u0000\u060e\u060f\u0005\u0114\u0000"+
		"\u0000\u060f\u0611\u0003\u0214\u010a\u0000\u0610\u060e\u0001\u0000\u0000"+
		"\u0000\u0611\u0614\u0001\u0000\u0000\u0000\u0612\u0610\u0001\u0000\u0000"+
		"\u0000\u0612\u0613\u0001\u0000\u0000\u0000\u0613\u061a\u0001\u0000\u0000"+
		"\u0000\u0614\u0612\u0001\u0000\u0000\u0000\u0615\u0616\u0003\u0214\u010a"+
		"\u0000\u0616\u0617\u0005\u0125\u0000\u0000\u0617\u0618\u0003\u0218\u010c"+
		"\u0000\u0618\u061a\u0001\u0000\u0000\u0000\u0619\u060b\u0001\u0000\u0000"+
		"\u0000\u0619\u0615\u0001\u0000\u0000\u0000\u061a\u0113\u0001\u0000\u0000"+
		"\u0000\u061b\u061c\u0003\u0214\u010a\u0000\u061c\u061d\u0005\u0125\u0000"+
		"\u0000\u061d\u061f\u0001\u0000\u0000\u0000\u061e\u061b\u0001\u0000\u0000"+
		"\u0000\u061e\u061f\u0001\u0000\u0000\u0000\u061f\u0620\u0001\u0000\u0000"+
		"\u0000\u0620\u0625\u0003\u0214\u010a\u0000\u0621\u0622\u0005\u0114\u0000"+
		"\u0000\u0622\u0624\u0003\u0214\u010a\u0000\u0623\u0621\u0001\u0000\u0000"+
		"\u0000\u0624\u0627\u0001\u0000\u0000\u0000\u0625\u0623\u0001\u0000\u0000"+
		"\u0000\u0625\u0626\u0001\u0000\u0000\u0000\u0626\u062e\u0001\u0000\u0000"+
		"\u0000\u0627\u0625\u0001\u0000\u0000\u0000\u0628\u0629\u0003\u0214\u010a"+
		"\u0000\u0629\u062a\u0005\u0125\u0000\u0000\u062a\u062b\u0003\u0218\u010c"+
		"\u0000\u062b\u062e\u0001\u0000\u0000\u0000\u062c\u062e\u0003\u0218\u010c"+
		"\u0000\u062d\u061e\u0001\u0000\u0000\u0000\u062d\u0628\u0001\u0000\u0000"+
		"\u0000\u062d\u062c\u0001\u0000\u0000\u0000\u062e\u0115\u0001\u0000\u0000"+
		"\u0000\u062f\u063a\u0003\u0118\u008c\u0000\u0630\u063a\u0003\u0170\u00b8"+
		"\u0000\u0631\u0632\u0003\u0166\u00b3\u0000\u0632\u0633\u0003\u0164\u00b2"+
		"\u0000\u0633\u063a\u0001\u0000\u0000\u0000\u0634\u063a\u0003\u0174\u00ba"+
		"\u0000\u0635\u063a\u0003\u0134\u009a\u0000\u0636\u063a\u0003\u0218\u010c"+
		"\u0000\u0637\u063a\u0003\u0260\u0130\u0000\u0638\u063a\u0003\u017c\u00be"+
		"\u0000\u0639\u062f\u0001\u0000\u0000\u0000\u0639\u0630\u0001\u0000\u0000"+
		"\u0000\u0639\u0631\u0001\u0000\u0000\u0000\u0639\u0634\u0001\u0000\u0000"+
		"\u0000\u0639\u0635\u0001\u0000\u0000\u0000\u0639\u0636\u0001\u0000\u0000"+
		"\u0000\u0639\u0637\u0001\u0000\u0000\u0000\u0639\u0638\u0001\u0000\u0000"+
		"\u0000\u063a\u0117\u0001\u0000\u0000\u0000\u063b\u0640\u0003\u011a\u008d"+
		"\u0000\u063c\u063d\u0005\u012a\u0000\u0000\u063d\u063f\u0003\u024e\u0127"+
		"\u0000\u063e\u063c\u0001\u0000\u0000\u0000\u063f\u0642\u0001\u0000\u0000"+
		"\u0000\u0640\u063e\u0001\u0000\u0000\u0000\u0640\u0641\u0001\u0000\u0000"+
		"\u0000\u0641\u0650\u0001\u0000\u0000\u0000\u0642\u0640\u0001\u0000\u0000"+
		"\u0000\u0643\u0648\u0003\u011c\u008e\u0000\u0644\u0645\u0005\u012a\u0000"+
		"\u0000\u0645\u0647\u0003\u024e\u0127\u0000\u0646\u0644\u0001\u0000\u0000"+
		"\u0000\u0647\u064a\u0001\u0000\u0000\u0000\u0648\u0646\u0001\u0000\u0000"+
		"\u0000\u0648\u0649\u0001\u0000\u0000\u0000\u0649\u0650\u0001\u0000\u0000"+
		"\u0000\u064a\u0648\u0001\u0000\u0000\u0000\u064b\u064c\u0003\u0134\u009a"+
		"\u0000\u064c\u064d\u0005\u012a\u0000\u0000\u064d\u064e\u0003\u024e\u0127"+
		"\u0000\u064e\u0650\u0001\u0000\u0000\u0000\u064f\u063b\u0001\u0000\u0000"+
		"\u0000\u064f\u0643\u0001\u0000\u0000\u0000\u064f\u064b\u0001\u0000\u0000"+
		"\u0000\u0650\u0119\u0001\u0000\u0000\u0000\u0651\u0652\u0005\u011e\u0000"+
		"\u0000\u0652\u0653\u0003\u015a\u00ad\u0000\u0653\u0654\u0005\u011f\u0000"+
		"\u0000\u0654\u011b\u0001\u0000\u0000\u0000\u0655\u065f\u0003\u0238\u011c"+
		"\u0000\u0656\u065f\u0003\u0112\u0089\u0000\u0657\u065f\u0003\u0120\u0090"+
		"\u0000\u0658\u065f\u0003\u0126\u0093\u0000\u0659\u065f\u0003\u0136\u009b"+
		"\u0000\u065a\u065f\u0003\u020c\u0106\u0000\u065b\u065f\u0003\u017c\u00be"+
		"\u0000\u065c\u065f\u0003\u013a\u009d\u0000\u065d\u065f\u0003\u011e\u008f"+
		"\u0000\u065e\u0655\u0001\u0000\u0000\u0000\u065e\u0656\u0001\u0000\u0000"+
		"\u0000\u065e\u0657\u0001\u0000\u0000\u0000\u065e\u0658\u0001\u0000\u0000"+
		"\u0000\u065e\u0659\u0001\u0000\u0000\u0000\u065e\u065a\u0001\u0000\u0000"+
		"\u0000\u065e\u065b\u0001\u0000\u0000\u0000\u065e\u065c\u0001\u0000\u0000"+
		"\u0000\u065e\u065d\u0001\u0000\u0000\u0000\u065f\u011d\u0001\u0000\u0000"+
		"\u0000\u0660\u0661\u0003\u0090H\u0000\u0661\u011f\u0001\u0000\u0000\u0000"+
		"\u0662\u0663\u0005E\u0000\u0000\u0663\u0664\u0005\u011e\u0000\u0000\u0664"+
		"\u0665\u0003\u00a0P\u0000\u0665\u0666\u0005\u011f\u0000\u0000\u0666\u0673"+
		"\u0001\u0000\u0000\u0000\u0667\u066a\u0003\u0122\u0091\u0000\u0668\u066a"+
		"\u0003\u0124\u0092\u0000\u0669\u0667\u0001\u0000\u0000\u0000\u0669\u0668"+
		"\u0001\u0000\u0000\u0000\u066a\u066b\u0001\u0000\u0000\u0000\u066b\u066d"+
		"\u0005\u011e\u0000\u0000\u066c\u066e\u0003\u0096K\u0000\u066d\u066c\u0001"+
		"\u0000\u0000\u0000\u066d\u066e\u0001\u0000\u0000\u0000\u066e\u066f\u0001"+
		"\u0000\u0000\u0000\u066f\u0670\u0003\u015a\u00ad\u0000\u0670\u0671\u0005"+
		"\u011f\u0000\u0000\u0671\u0673\u0001\u0000\u0000\u0000\u0672\u0662\u0001"+
		"\u0000\u0000\u0000\u0672\u0669\u0001\u0000\u0000\u0000\u0673\u0121\u0001"+
		"\u0000\u0000\u0000\u0674\u0675\u0007\u0006\u0000\u0000\u0675\u0123\u0001"+
		"\u0000\u0000\u0000\u0676\u0677\u0007\u0007\u0000\u0000\u0677\u0125\u0001"+
		"\u0000\u0000\u0000\u0678\u0679\u0005\u0007\u0000\u0000\u0679\u067a\u0003"+
		"\u015a\u00ad\u0000\u067a\u067c\u0003\u012c\u0096\u0000\u067b\u067d\u0003"+
		"\u0130\u0098\u0000\u067c\u067b\u0001\u0000\u0000\u0000\u067c\u067d\u0001"+
		"\u0000\u0000\u0000\u067d\u067e\u0001\u0000\u0000\u0000\u067e\u067f\u0005"+
		"\f\u0000\u0000\u067f\u0688\u0001\u0000\u0000\u0000\u0680\u0681\u0005\u0007"+
		"\u0000\u0000\u0681\u0683\u0003\u0128\u0094\u0000\u0682\u0684\u0003\u0130"+
		"\u0098\u0000\u0683\u0682\u0001\u0000\u0000\u0000\u0683\u0684\u0001\u0000"+
		"\u0000\u0000\u0684\u0685\u0001\u0000\u0000\u0000\u0685\u0686\u0005\f\u0000"+
		"\u0000\u0686\u0688\u0001\u0000\u0000\u0000\u0687\u0678\u0001\u0000\u0000"+
		"\u0000\u0687\u0680\u0001\u0000\u0000\u0000\u0688\u0127\u0001\u0000\u0000"+
		"\u0000\u0689\u068b\u0003\u012a\u0095\u0000\u068a\u0689\u0001\u0000\u0000"+
		"\u0000\u068b\u068c\u0001\u0000\u0000\u0000\u068c\u068a\u0001\u0000\u0000"+
		"\u0000\u068c\u068d\u0001\u0000\u0000\u0000\u068d\u0129\u0001\u0000\u0000"+
		"\u0000\u068e\u068f\u00058\u0000\u0000\u068f\u0690\u0003\u015a\u00ad\u0000"+
		"\u0690\u0691\u00051\u0000\u0000\u0691\u0692\u0003\u0132\u0099\u0000\u0692"+
		"\u012b\u0001\u0000\u0000\u0000\u0693\u0695\u0003\u012e\u0097\u0000\u0694"+
		"\u0693\u0001\u0000\u0000\u0000\u0695\u0696\u0001\u0000\u0000\u0000\u0696"+
		"\u0694\u0001\u0000\u0000\u0000\u0696\u0697\u0001\u0000\u0000\u0000\u0697"+
		"\u012d\u0001\u0000\u0000\u0000\u0698\u0699\u00058\u0000\u0000\u0699\u069a"+
		"\u0003\u015a\u00ad\u0000\u069a\u069b\u00051\u0000\u0000\u069b\u069c\u0003"+
		"\u0132\u0099\u0000\u069c\u012f\u0001\u0000\u0000\u0000\u069d\u069e\u0005"+
		"\r\u0000\u0000\u069e\u069f\u0003\u0132\u0099\u0000\u069f\u0131\u0001\u0000"+
		"\u0000\u0000\u06a0\u06a3\u0003\u015a\u00ad\u0000\u06a1\u06a3\u0003\u0134"+
		"\u009a\u0000\u06a2\u06a0\u0001\u0000\u0000\u0000\u06a2\u06a1\u0001\u0000"+
		"\u0000\u0000\u06a3\u0133\u0001\u0000\u0000\u0000\u06a4\u06a5\u0005\"\u0000"+
		"\u0000\u06a5\u0135\u0001\u0000\u0000\u0000\u06a6\u06a7\u0003\u0138\u009c"+
		"\u0000\u06a7\u06a8\u0005\u011e\u0000\u0000\u06a8\u06a9\u0003\u015a\u00ad"+
		"\u0000\u06a9\u06aa\u0005\u0001\u0000\u0000\u06aa\u06ab\u0003\u024e\u0127"+
		"\u0000\u06ab\u06ac\u0005\u011f\u0000\u0000\u06ac\u0137\u0001\u0000\u0000"+
		"\u0000\u06ad\u06ae\u0007\b\u0000\u0000\u06ae\u0139\u0001\u0000\u0000\u0000"+
		"\u06af\u06b0\u0003\u013c\u009e\u0000\u06b0\u06b1\u0003\u013e\u009f\u0000"+
		"\u06b1\u013b\u0001\u0000\u0000\u0000\u06b2\u06b3\u0003\u0122\u0091\u0000"+
		"\u06b3\u06b5\u0005\u011e\u0000\u0000\u06b4\u06b6\u0003\u0212\u0109\u0000"+
		"\u06b5\u06b4\u0001\u0000\u0000\u0000\u06b5\u06b6\u0001\u0000\u0000\u0000"+
		"\u06b6\u06b7\u0001\u0000\u0000\u0000\u06b7\u06b8\u0005\u011f\u0000\u0000"+
		"\u06b8\u06c4\u0001\u0000\u0000\u0000\u06b9\u06ba\u0003\u0154\u00aa\u0000"+
		"\u06ba\u06bb\u0005\u011e\u0000\u0000\u06bb\u06bc\u0003\u0212\u0109\u0000"+
		"\u06bc\u06c1\u0005\u011f\u0000\u0000\u06bd\u06bf\u0003\u0156\u00ab\u0000"+
		"\u06be\u06bd\u0001\u0000\u0000\u0000\u06be\u06bf\u0001\u0000\u0000\u0000"+
		"\u06bf\u06c0\u0001\u0000\u0000\u0000\u06c0\u06c2\u0003\u0158\u00ac\u0000"+
		"\u06c1\u06be\u0001\u0000\u0000\u0000\u06c1\u06c2\u0001\u0000\u0000\u0000"+
		"\u06c2\u06c4\u0001\u0000\u0000\u0000\u06c3\u06b2\u0001\u0000\u0000\u0000"+
		"\u06c3\u06b9\u0001\u0000\u0000\u0000\u06c4\u013d\u0001\u0000\u0000\u0000"+
		"\u06c5\u06c6\u0005v\u0000\u0000\u06c6\u06c8\u0005\u011e\u0000\u0000\u06c7"+
		"\u06c9\u0003\u0140\u00a0\u0000\u06c8\u06c7\u0001\u0000\u0000\u0000\u06c8"+
		"\u06c9\u0001\u0000\u0000\u0000\u06c9\u06cb\u0001\u0000\u0000\u0000\u06ca"+
		"\u06cc\u0003\u019c\u00ce\u0000\u06cb\u06ca\u0001\u0000\u0000\u0000\u06cb"+
		"\u06cc\u0001\u0000\u0000\u0000\u06cc\u06ce\u0001\u0000\u0000\u0000\u06cd"+
		"\u06cf\u0003\u0142\u00a1\u0000\u06ce\u06cd\u0001\u0000\u0000\u0000\u06ce"+
		"\u06cf\u0001\u0000\u0000\u0000\u06cf\u06d0\u0001\u0000\u0000\u0000\u06d0"+
		"\u06d1\u0005\u011f\u0000\u0000\u06d1\u013f\u0001\u0000\u0000\u0000\u06d2"+
		"\u06d3\u0005x\u0000\u0000\u06d3\u06d4\u0005?\u0000\u0000\u06d4\u06d5\u0003"+
		"\u0212\u0109\u0000\u06d5\u0141\u0001\u0000\u0000\u0000\u06d6\u06d7\u0003"+
		"\u0144\u00a2\u0000\u06d7\u06d8\u0003\u0146\u00a3\u0000\u06d8\u0143\u0001"+
		"\u0000\u0000\u0000\u06d9\u06da\u0007\t\u0000\u0000\u06da\u0145\u0001\u0000"+
		"\u0000\u0000\u06db\u06df\u0003\u0148\u00a4\u0000\u06dc\u06df\u0003\u014c"+
		"\u00a6\u0000\u06dd\u06df\u0003\u0150\u00a8\u0000\u06de\u06db\u0001\u0000"+
		"\u0000\u0000\u06de\u06dc\u0001\u0000\u0000\u0000\u06de\u06dd\u0001\u0000"+
		"\u0000\u0000\u06df\u0147\u0001\u0000\u0000\u0000\u06e0\u06e1\u0005>\u0000"+
		"\u0000\u06e1\u06e2\u0003\u014a\u00a5\u0000\u06e2\u06e3\u0005\u0003\u0000"+
		"\u0000\u06e3\u06e4\u0003\u014a\u00a5\u0000\u06e4\u0149\u0001\u0000\u0000"+
		"\u0000\u06e5\u06e9\u0003\u014c\u00a6\u0000\u06e6\u06e9\u0003\u014e\u00a7"+
		"\u0000\u06e7\u06e9\u0003\u0150\u00a8\u0000\u06e8\u06e5\u0001\u0000\u0000"+
		"\u0000\u06e8\u06e6\u0001\u0000\u0000\u0000\u06e8\u06e7\u0001\u0000\u0000"+
		"\u0000\u06e9\u014b\u0001\u0000\u0000\u0000\u06ea\u06eb\u0003\u0152\u00a9"+
		"\u0000\u06eb\u06ec\u0005z\u0000\u0000\u06ec\u014d\u0001\u0000\u0000\u0000"+
		"\u06ed\u06ee\u0003\u0152\u00a9\u0000\u06ee\u06ef\u0005Y\u0000\u0000\u06ef"+
		"\u014f\u0001\u0000\u0000\u0000\u06f0\u06f1\u0005H\u0000\u0000\u06f1\u06f2"+
		"\u0005\u0084\u0000\u0000\u06f2\u0151\u0001\u0000\u0000\u0000\u06f3\u06f4"+
		"\u0007\n\u0000\u0000\u06f4\u0153\u0001\u0000\u0000\u0000\u06f5\u06f6\u0007"+
		"\u000b\u0000\u0000\u06f6\u0155\u0001\u0000\u0000\u0000\u06f7\u06f8\u0005"+
		"\u0011\u0000\u0000\u06f8\u06f9\u0007\f\u0000\u0000\u06f9\u0157\u0001\u0000"+
		"\u0000\u0000\u06fa\u06fb\u0007\r\u0000\u0000\u06fb\u06fc\u0005#\u0000"+
		"\u0000\u06fc\u0159\u0001\u0000\u0000\u0000\u06fd\u0702\u0003\u015c\u00ae"+
		"\u0000\u06fe\u0702\u0003\u0194\u00ca\u0000\u06ff\u0702\u0003\u0218\u010c"+
		"\u0000\u0700\u0702\u0003\u0184\u00c2\u0000\u0701\u06fd\u0001\u0000\u0000"+
		"\u0000\u0701\u06fe\u0001\u0000\u0000\u0000\u0701\u06ff\u0001\u0000\u0000"+
		"\u0000\u0701\u0700\u0001\u0000\u0000\u0000\u0702\u015b\u0001\u0000\u0000"+
		"\u0000\u0703\u0707\u0003\u015e\u00af\u0000\u0704\u0707\u0003\u0170\u00b8"+
		"\u0000\u0705\u0707\u0003\u0134\u009a\u0000\u0706\u0703\u0001\u0000\u0000"+
		"\u0000\u0706\u0704\u0001\u0000\u0000\u0000\u0706\u0705\u0001\u0000\u0000"+
		"\u0000\u0707\u015d\u0001\u0000\u0000\u0000\u0708\u070d\u0003\u0160\u00b0"+
		"\u0000\u0709\u070a\u0007\u000e\u0000\u0000\u070a\u070c\u0003\u0160\u00b0"+
		"\u0000\u070b\u0709\u0001\u0000\u0000\u0000\u070c\u070f\u0001\u0000\u0000"+
		"\u0000\u070d\u070b\u0001\u0000\u0000\u0000\u070d\u070e\u0001\u0000\u0000"+
		"\u0000\u070e\u015f\u0001\u0000\u0000\u0000\u070f\u070d\u0001\u0000\u0000"+
		"\u0000\u0710\u0715\u0003\u0162\u00b1\u0000\u0711\u0712\u0007\u000f\u0000"+
		"\u0000\u0712\u0714\u0003\u0162\u00b1\u0000\u0713\u0711\u0001\u0000\u0000"+
		"\u0000\u0714\u0717\u0001\u0000\u0000\u0000\u0715\u0713\u0001\u0000\u0000"+
		"\u0000\u0715\u0716\u0001\u0000\u0000\u0000\u0716\u0161\u0001\u0000\u0000"+
		"\u0000\u0717\u0715\u0001\u0000\u0000\u0000\u0718\u071a\u0003\u0166\u00b3"+
		"\u0000\u0719\u0718\u0001\u0000\u0000\u0000\u0719\u071a\u0001\u0000\u0000"+
		"\u0000\u071a\u071b\u0001\u0000\u0000\u0000\u071b\u071c\u0003\u0164\u00b2"+
		"\u0000\u071c\u0163\u0001\u0000\u0000\u0000\u071d\u0720\u0003\u0118\u008c"+
		"\u0000\u071e\u0720\u0003\u0168\u00b4\u0000\u071f\u071d\u0001\u0000\u0000"+
		"\u0000\u071f\u071e\u0001\u0000\u0000\u0000\u0720\u0165\u0001\u0000\u0000"+
		"\u0000\u0721\u0722\u0007\u000e\u0000\u0000\u0722\u0167\u0001\u0000\u0000"+
		"\u0000\u0723\u0724\u0005U\u0000\u0000\u0724\u0725\u0005\u011e\u0000\u0000"+
		"\u0725\u0726\u0003\u016a\u00b5\u0000\u0726\u0727\u0005\u0011\u0000\u0000"+
		"\u0727\u0728\u0003\u016e\u00b7\u0000\u0728\u0729\u0005\u011f\u0000\u0000"+
		"\u0729\u0169\u0001\u0000\u0000\u0000\u072a\u072e\u0003\u0206\u0103\u0000"+
		"\u072b\u072e\u0003\u016c\u00b6\u0000\u072c\u072e\u0003\u020a\u0105\u0000"+
		"\u072d\u072a\u0001\u0000\u0000\u0000\u072d\u072b\u0001\u0000\u0000\u0000"+
		"\u072d\u072c\u0001\u0000\u0000\u0000\u072e\u016b\u0001\u0000\u0000\u0000"+
		"\u072f\u0730\u0007\u0010\u0000\u0000\u0730\u016d\u0001\u0000\u0000\u0000"+
		"\u0731\u0734\u0003\u0112\u0089\u0000\u0732\u0734\u0003\u0244\u0122\u0000"+
		"\u0733\u0731\u0001\u0000\u0000\u0000\u0733\u0732\u0001\u0000\u0000\u0000"+
		"\u0734\u016f\u0001\u0000\u0000\u0000\u0735\u073a\u0003\u0172\u00b9\u0000"+
		"\u0736\u0737\u0005\u0117\u0000\u0000\u0737\u0739\u0003\u0172\u00b9\u0000"+
		"\u0738\u0736\u0001\u0000\u0000\u0000\u0739\u073c\u0001\u0000\u0000\u0000"+
		"\u073a\u0738\u0001\u0000\u0000\u0000\u073a\u073b\u0001\u0000\u0000\u0000"+
		"\u073b\u0171\u0001\u0000\u0000\u0000\u073c\u073a\u0001\u0000\u0000\u0000"+
		"\u073d\u0740\u0003\u0118\u008c\u0000\u073e\u0740\u0003\u0174\u00ba\u0000"+
		"\u073f\u073d\u0001\u0000\u0000\u0000\u073f\u073e\u0001\u0000\u0000\u0000"+
		"\u0740\u0173\u0001\u0000\u0000\u0000\u0741\u0742\u0003\u0176\u00bb\u0000"+
		"\u0742\u0743\u0005\u011e\u0000\u0000\u0743\u0744\u0003\u0178\u00bc\u0000"+
		"\u0744\u0747\u0005\u011f\u0000\u0000\u0745\u0746\u0005\u012a\u0000\u0000"+
		"\u0746\u0748\u0003\u024e\u0127\u0000\u0747\u0745\u0001\u0000\u0000\u0000"+
		"\u0747\u0748\u0001\u0000\u0000\u0000\u0748\u0175\u0001\u0000\u0000\u0000"+
		"\u0749\u074a\u0005\u0092\u0000\u0000\u074a\u0177\u0001\u0000\u0000\u0000"+
		"\u074b\u074d\u0003\u017a\u00bd\u0000\u074c\u074b\u0001\u0000\u0000\u0000"+
		"\u074c\u074d\u0001\u0000\u0000\u0000\u074d\u074f\u0001\u0000\u0000\u0000"+
		"\u074e\u0750\u0003\u0170\u00b8\u0000\u074f\u074e\u0001\u0000\u0000\u0000"+
		"\u074f\u0750\u0001\u0000\u0000\u0000\u0750\u0751\u0001\u0000\u0000\u0000"+
		"\u0751\u0753\u0005\u0011\u0000\u0000\u0752\u074c\u0001\u0000\u0000\u0000"+
		"\u0752\u0753\u0001\u0000\u0000\u0000\u0753\u0754\u0001\u0000\u0000\u0000"+
		"\u0754\u075a\u0003\u015a\u00ad\u0000\u0755\u0756\u0003\u015a\u00ad\u0000"+
		"\u0756\u0757\u0005\u0116\u0000\u0000\u0757\u0758\u0003\u0170\u00b8\u0000"+
		"\u0758\u075a\u0001\u0000\u0000\u0000\u0759\u0752\u0001\u0000\u0000\u0000"+
		"\u0759\u0755\u0001\u0000\u0000\u0000\u075a\u0179\u0001\u0000\u0000\u0000"+
		"\u075b\u075c\u0007\u0011\u0000\u0000\u075c\u017b\u0001\u0000\u0000\u0000"+
		"\u075d\u075e\u0003\u017e\u00bf\u0000\u075e\u075f\u0005\u011e\u0000\u0000"+
		"\u075f\u0760\u0003\u0170\u00b8\u0000\u0760\u0761\u0005\u0016\u0000\u0000"+
		"\u0761\u0762\u0003\u0170\u00b8\u0000\u0762\u0763\u0005\u011f\u0000\u0000"+
		"\u0763\u0774\u0001\u0000\u0000\u0000\u0764\u0768\u0003\u017e\u00bf\u0000"+
		"\u0765\u0768\u0003\u0180\u00c0\u0000\u0766\u0768\u0003\u0182\u00c1\u0000"+
		"\u0767\u0764\u0001\u0000\u0000\u0000\u0767\u0765\u0001\u0000\u0000\u0000"+
		"\u0767\u0766\u0001\u0000\u0000\u0000\u0768\u0769\u0001\u0000\u0000\u0000"+
		"\u0769\u076a\u0005\u011e\u0000\u0000\u076a\u076b\u0003\u0170\u00b8\u0000"+
		"\u076b\u076c\u0005\u0116\u0000\u0000\u076c\u076f\u0003\u0170\u00b8\u0000"+
		"\u076d\u076e\u0005\u0116\u0000\u0000\u076e\u0770\u0003\u0164\u00b2\u0000"+
		"\u076f\u076d\u0001\u0000\u0000\u0000\u076f\u0770\u0001\u0000\u0000\u0000"+
		"\u0770\u0771\u0001\u0000\u0000\u0000\u0771\u0772\u0005\u011f\u0000\u0000"+
		"\u0772\u0774\u0001\u0000\u0000\u0000\u0773\u075d\u0001\u0000\u0000\u0000"+
		"\u0773\u0767\u0001\u0000\u0000\u0000\u0774\u017d\u0001\u0000\u0000\u0000"+
		"\u0775\u0776\u0005\u014a\u0000\u0000\u0776\u017f\u0001\u0000\u0000\u0000"+
		"\u0777\u0778\u0005\u014c\u0000\u0000\u0778\u0181\u0001\u0000\u0000\u0000"+
		"\u0779\u077a\u0005\u014b\u0000\u0000\u077a\u0183\u0001\u0000\u0000\u0000"+
		"\u077b\u077c\u0003\u0186\u00c3\u0000\u077c\u0185\u0001\u0000\u0000\u0000"+
		"\u077d\u0782\u0003\u0188\u00c4\u0000\u077e\u077f\u0005(\u0000\u0000\u077f"+
		"\u0781\u0003\u0188\u00c4\u0000\u0780\u077e\u0001\u0000\u0000\u0000\u0781"+
		"\u0784\u0001\u0000\u0000\u0000\u0782\u0780\u0001\u0000\u0000\u0000\u0782"+
		"\u0783\u0001\u0000\u0000\u0000\u0783\u0187\u0001\u0000\u0000\u0000\u0784"+
		"\u0782\u0001\u0000\u0000\u0000\u0785\u078a\u0003\u018a\u00c5\u0000\u0786"+
		"\u0787\u0005\u0003\u0000\u0000\u0787\u0789\u0003\u018a\u00c5\u0000\u0788"+
		"\u0786\u0001\u0000\u0000\u0000\u0789\u078c\u0001\u0000\u0000\u0000\u078a"+
		"\u0788\u0001\u0000\u0000\u0000\u078a\u078b\u0001\u0000\u0000\u0000\u078b"+
		"\u0189\u0001\u0000\u0000\u0000\u078c\u078a\u0001\u0000\u0000\u0000\u078d"+
		"\u078f\u0003\u01fa\u00fd\u0000\u078e\u078d\u0001\u0000\u0000\u0000\u078e"+
		"\u078f\u0001\u0000\u0000\u0000\u078f\u0790\u0001\u0000\u0000\u0000\u0790"+
		"\u0791\u0003\u018c\u00c6\u0000\u0791\u018b\u0001\u0000\u0000\u0000\u0792"+
		"\u0793\u0005\u011e\u0000\u0000\u0793\u0794\u0003\u0184\u00c2\u0000\u0794"+
		"\u0795\u0005\u011f\u0000\u0000\u0795\u079b\u0001\u0000\u0000\u0000\u0796"+
		"\u0798\u0003\u018e\u00c7\u0000\u0797\u0799\u0003\u01f6\u00fb\u0000\u0798"+
		"\u0797\u0001\u0000\u0000\u0000\u0798\u0799\u0001\u0000\u0000\u0000\u0799"+
		"\u079b\u0001\u0000\u0000\u0000\u079a\u0792\u0001\u0000\u0000\u0000\u079a"+
		"\u0796\u0001\u0000\u0000\u0000\u079b\u018d\u0001\u0000\u0000\u0000\u079c"+
		"\u079f\u0003\u0190\u00c8\u0000\u079d\u079f\u0003\u011c\u008e\u0000\u079e"+
		"\u079c\u0001\u0000\u0000\u0000\u079e\u079d\u0001\u0000\u0000\u0000\u079f"+
		"\u018f\u0001\u0000\u0000\u0000\u07a0\u07a8\u0003\u01c0\u00e0\u0000\u07a1"+
		"\u07a8\u0003\u01ca\u00e5\u0000\u07a2\u07a8\u0003\u01ce\u00e7\u0000\u07a3"+
		"\u07a8\u0003\u01d0\u00e8\u0000\u07a4\u07a8\u0003\u01f2\u00f9\u0000\u07a5"+
		"\u07a8\u0003\u01ec\u00f6\u0000\u07a6\u07a8\u0003\u0192\u00c9\u0000\u07a7"+
		"\u07a0\u0001\u0000\u0000\u0000\u07a7\u07a1\u0001\u0000\u0000\u0000\u07a7"+
		"\u07a2\u0001\u0000\u0000\u0000\u07a7\u07a3\u0001\u0000\u0000\u0000\u07a7"+
		"\u07a4\u0001\u0000\u0000\u0000\u07a7\u07a5\u0001\u0000\u0000\u0000\u07a7"+
		"\u07a6\u0001\u0000\u0000\u0000\u07a8\u0191\u0001\u0000\u0000\u0000\u07a9"+
		"\u07aa\u0003\u0218\u010c\u0000\u07aa\u0193\u0001\u0000\u0000\u0000\u07ab"+
		"\u07ae\u0003\u011c\u008e\u0000\u07ac\u07ae\u0003\u0134\u009a\u0000\u07ad"+
		"\u07ab\u0001\u0000\u0000\u0000\u07ad\u07ac\u0001\u0000\u0000\u0000\u07ae"+
		"\u0195\u0001\u0000\u0000\u0000\u07af\u07b3\u0003\u011c\u008e\u0000\u07b0"+
		"\u07b3\u0003\u015c\u00ae\u0000\u07b1\u07b3\u0003\u0218\u010c\u0000\u07b2"+
		"\u07af\u0001\u0000\u0000\u0000\u07b2\u07b0\u0001\u0000\u0000\u0000\u07b2"+
		"\u07b1\u0001\u0000\u0000\u0000\u07b3\u0197\u0001\u0000\u0000\u0000\u07b4"+
		"\u07b5\u00059\u0000\u0000\u07b5\u07b6\u0003\u019a\u00cd\u0000\u07b6\u0199"+
		"\u0001\u0000\u0000\u0000\u07b7\u07b8\u0003\u015a\u00ad\u0000\u07b8\u019b"+
		"\u0001\u0000\u0000\u0000\u07b9\u07ba\u0005)\u0000\u0000\u07ba\u07bb\u0005"+
		"?\u0000\u0000\u07bb\u07bc\u0003\u019e\u00cf\u0000\u07bc\u019d\u0001\u0000"+
		"\u0000\u0000\u07bd\u07c2\u0003\u01a0\u00d0\u0000\u07be\u07bf\u0005\u0116"+
		"\u0000\u0000\u07bf\u07c1\u0003\u01a0\u00d0\u0000\u07c0\u07be\u0001\u0000"+
		"\u0000\u0000\u07c1\u07c4\u0001\u0000\u0000\u0000\u07c2\u07c0\u0001\u0000"+
		"\u0000\u0000\u07c2\u07c3\u0001\u0000\u0000\u0000\u07c3\u019f\u0001\u0000"+
		"\u0000\u0000\u07c4\u07c2\u0001\u0000\u0000\u0000\u07c5\u07c7\u0003\u0196"+
		"\u00cb\u0000\u07c6\u07c8\u0003\u01a2\u00d1\u0000\u07c7\u07c6\u0001\u0000"+
		"\u0000\u0000\u07c7\u07c8\u0001\u0000\u0000\u0000\u07c8\u07ca\u0001\u0000"+
		"\u0000\u0000\u07c9\u07cb\u0003\u01a4\u00d2\u0000\u07ca\u07c9\u0001\u0000"+
		"\u0000\u0000\u07ca\u07cb\u0001\u0000\u0000\u0000\u07cb\u01a1\u0001\u0000"+
		"\u0000\u0000\u07cc\u07cd\u0007\u0012\u0000\u0000\u07cd\u01a3\u0001\u0000"+
		"\u0000\u0000\u07ce\u07cf\u0005#\u0000\u0000\u07cf\u07d0\u0003\u01a6\u00d3"+
		"\u0000\u07d0\u01a5\u0001\u0000\u0000\u0000\u07d1\u07d2\u0007\f\u0000\u0000"+
		"\u07d2\u01a7\u0001\u0000\u0000\u0000\u07d3\u07d4\u0005\u001f\u0000\u0000"+
		"\u07d4\u07d7\u0003\u015e\u00af\u0000\u07d5\u07d6\u0005%\u0000\u0000\u07d6"+
		"\u07d8\u0003\u015e\u00af\u0000\u07d7\u07d5\u0001\u0000\u0000\u0000\u07d7"+
		"\u07d8\u0001\u0000\u0000\u0000\u07d8\u01a9\u0001\u0000\u0000\u0000\u07d9"+
		"\u07da\u0005\u0012\u0000\u0000\u07da\u07dd\u0005?\u0000\u0000\u07db\u07de"+
		"\u0003\u01ac\u00d6\u0000\u07dc\u07de\u0003\u0098L\u0000\u07dd\u07db\u0001"+
		"\u0000\u0000\u0000\u07dd\u07dc\u0001\u0000\u0000\u0000\u07de\u01ab\u0001"+
		"\u0000\u0000\u0000\u07df\u07e4\u0003\u01ae\u00d7\u0000\u07e0\u07e1\u0005"+
		"\u0116\u0000\u0000\u07e1\u07e3\u0003\u01ae\u00d7\u0000\u07e2\u07e0\u0001"+
		"\u0000\u0000\u0000\u07e3\u07e6\u0001\u0000\u0000\u0000\u07e4\u07e2\u0001"+
		"\u0000\u0000\u0000\u07e4\u07e5\u0001\u0000\u0000\u0000\u07e5\u01ad\u0001"+
		"\u0000\u0000\u0000\u07e6\u07e4\u0001\u0000\u0000\u0000\u07e7\u07ec\u0003"+
		"\u01b4\u00da\u0000\u07e8\u07ec\u0003\u01b6\u00db\u0000\u07e9\u07ec\u0003"+
		"\u01b8\u00dc\u0000\u07ea\u07ec\u0003\u01b2\u00d9\u0000\u07eb\u07e7\u0001"+
		"\u0000\u0000\u0000\u07eb\u07e8\u0001\u0000\u0000\u0000\u07eb\u07e9\u0001"+
		"\u0000\u0000\u0000\u07eb\u07ea\u0001\u0000\u0000\u0000\u07ec\u01af\u0001"+
		"\u0000\u0000\u0000\u07ed\u07f2\u0003\u01b2\u00d9\u0000\u07ee\u07ef\u0005"+
		"\u0116\u0000\u0000\u07ef\u07f1\u0003\u01b2\u00d9\u0000\u07f0\u07ee\u0001"+
		"\u0000\u0000\u0000\u07f1\u07f4\u0001\u0000\u0000\u0000\u07f2\u07f0\u0001"+
		"\u0000\u0000\u0000\u07f2\u07f3\u0001\u0000\u0000\u0000\u07f3\u01b1\u0001"+
		"\u0000\u0000\u0000\u07f4\u07f2\u0001\u0000\u0000\u0000\u07f5\u07fb\u0003"+
		"\u0196\u00cb\u0000\u07f6\u07f7\u0005\u011e\u0000\u0000\u07f7\u07f8\u0003"+
		"\u01be\u00df\u0000\u07f8\u07f9\u0005\u011f\u0000\u0000\u07f9\u07fb\u0001"+
		"\u0000\u0000\u0000\u07fa\u07f5\u0001\u0000\u0000\u0000\u07fa\u07f6\u0001"+
		"\u0000\u0000\u0000\u07fb\u01b3\u0001\u0000\u0000\u0000\u07fc\u07fd\u0005"+
		"\u0082\u0000\u0000\u07fd\u07fe\u0005\u011e\u0000\u0000\u07fe\u07ff\u0003"+
		"\u01b0\u00d8\u0000\u07ff\u0800\u0005\u011f\u0000\u0000\u0800\u01b5\u0001"+
		"\u0000\u0000\u0000\u0801\u0802\u0005G\u0000\u0000\u0802\u0803\u0005\u011e"+
		"\u0000\u0000\u0803\u0804\u0003\u01b0\u00d8\u0000\u0804\u0805\u0005\u011f"+
		"\u0000\u0000\u0805\u01b7\u0001\u0000\u0000\u0000\u0806\u0807\u0005\u011e"+
		"\u0000\u0000\u0807\u0808\u0005\u011f\u0000\u0000\u0808\u01b9\u0001\u0000"+
		"\u0000\u0000\u0809\u080a\u0005\u0013\u0000\u0000\u080a\u080b\u0003\u0184"+
		"\u00c2\u0000\u080b\u01bb\u0001\u0000\u0000\u0000\u080c\u080d\u0005\u0149"+
		"\u0000\u0000\u080d\u080e\u0003\u019a\u00cd\u0000\u080e\u01bd\u0001\u0000"+
		"\u0000\u0000\u080f\u0814\u0003\u0196\u00cb\u0000\u0810\u0811\u0005\u0116"+
		"\u0000\u0000\u0811\u0813\u0003\u0196\u00cb\u0000\u0812\u0810\u0001\u0000"+
		"\u0000\u0000\u0813\u0816\u0001\u0000\u0000\u0000\u0814\u0812\u0001\u0000"+
		"\u0000\u0000\u0814\u0815\u0001\u0000\u0000\u0000\u0815\u01bf\u0001\u0000"+
		"\u0000\u0000\u0816\u0814\u0001\u0000\u0000\u0000\u0817\u0818\u0003\u0196"+
		"\u00cb\u0000\u0818\u0819\u0003\u01c2\u00e1\u0000\u0819\u081a\u0003\u0196"+
		"\u00cb\u0000\u081a\u01c1\u0001\u0000\u0000\u0000\u081b\u0822\u0003\u01c8"+
		"\u00e4\u0000\u081c\u081e\u0003\u01fa\u00fd\u0000\u081d\u081c\u0001\u0000"+
		"\u0000\u0000\u081d\u081e\u0001\u0000\u0000\u0000\u081e\u081f\u0001\u0000"+
		"\u0000\u0000\u081f\u0822\u0003\u01c4\u00e2\u0000\u0820\u0822\u0003\u01c6"+
		"\u00e3\u0000\u0821\u081b\u0001\u0000\u0000\u0000\u0821\u081d\u0001\u0000"+
		"\u0000\u0000\u0821\u0820\u0001\u0000\u0000\u0000\u0822\u01c3\u0001\u0000"+
		"\u0000\u0000\u0823\u082a\u0005\u001e\u0000\u0000\u0824\u082a\u0005\u0015"+
		"\u0000\u0000\u0825\u0826\u0005\u0088\u0000\u0000\u0826\u082a\u0005\u0093"+
		"\u0000\u0000\u0827\u082a\u0005\u0080\u0000\u0000\u0828\u082a\u0005\u0081"+
		"\u0000\u0000\u0829\u0823\u0001\u0000\u0000\u0000\u0829\u0824\u0001\u0000"+
		"\u0000\u0000\u0829\u0825\u0001\u0000\u0000\u0000\u0829\u0827\u0001\u0000"+
		"\u0000\u0000\u0829\u0828\u0001\u0000\u0000\u0000\u082a\u01c5\u0001\u0000"+
		"\u0000\u0000\u082b\u082c\u0007\u0013\u0000\u0000\u082c\u01c7\u0001\u0000"+
		"\u0000\u0000\u082d\u082e\u0007\u0014\u0000\u0000\u082e\u01c9\u0001\u0000"+
		"\u0000\u0000\u082f\u0831\u0003\u0196\u00cb\u0000\u0830\u0832\u0003\u01fa"+
		"\u00fd\u0000\u0831\u0830\u0001\u0000\u0000\u0000\u0831\u0832\u0001\u0000"+
		"\u0000\u0000\u0832\u0833\u0001\u0000\u0000\u0000\u0833\u0835\u0005>\u0000"+
		"\u0000\u0834\u0836\u0003\u01cc\u00e6\u0000\u0835\u0834\u0001\u0000\u0000"+
		"\u0000\u0835\u0836\u0001\u0000\u0000\u0000\u0836\u0837\u0001\u0000\u0000"+
		"\u0000\u0837\u0838\u0003\u0196\u00cb\u0000\u0838\u0839\u0005\u0003\u0000"+
		"\u0000\u0839\u083a\u0003\u0196\u00cb\u0000\u083a\u01cb\u0001\u0000\u0000"+
		"\u0000\u083b\u083c\u0007\u0015\u0000\u0000\u083c\u01cd\u0001\u0000\u0000"+
		"\u0000\u083d\u083f\u0003\u0196\u00cb\u0000\u083e\u0840\u0003\u01fa\u00fd"+
		"\u0000\u083f\u083e\u0001\u0000\u0000\u0000\u083f\u0840\u0001\u0000\u0000"+
		"\u0000\u0840\u0841\u0001\u0000\u0000\u0000\u0841\u0842\u0005\u0016\u0000"+
		"\u0000\u0842\u0843\u0003\u01d4\u00ea\u0000\u0843\u01cf\u0001\u0000\u0000"+
		"\u0000\u0844\u0846\u0003\u0196\u00cb\u0000\u0845\u0847\u0003\u01fa\u00fd"+
		"\u0000\u0846\u0845\u0001\u0000\u0000\u0000\u0846\u0847\u0001\u0000\u0000"+
		"\u0000\u0847\u0848\u0001\u0000\u0000\u0000\u0848\u0849\u0003\u01d2\u00e9"+
		"\u0000\u0849\u084b\u0003\u01d4\u00ea\u0000\u084a\u084c\u0003\u01d8\u00ec"+
		"\u0000\u084b\u084a\u0001\u0000\u0000\u0000\u084b\u084c\u0001\u0000\u0000"+
		"\u0000\u084c\u01d1\u0001\u0000\u0000\u0000\u084d\u084e\u0007\u0016\u0000"+
		"\u0000\u084e\u084f\u0005\u0004\u0000\u0000\u084f\u01d3\u0001\u0000\u0000"+
		"\u0000\u0850\u0857\u0003\u0090H\u0000\u0851\u0852\u0005\u011e\u0000\u0000"+
		"\u0852\u0853\u0003\u01d6\u00eb\u0000\u0853\u0854\u0005\u011f\u0000\u0000"+
		"\u0854\u0857\u0001\u0000\u0000\u0000\u0855\u0857\u0003\u0218\u010c\u0000"+
		"\u0856\u0850\u0001\u0000\u0000\u0000\u0856\u0851\u0001\u0000\u0000\u0000"+
		"\u0856\u0855\u0001\u0000\u0000\u0000\u0857\u01d5\u0001\u0000\u0000\u0000"+
		"\u0858\u085d\u0003\u0194\u00ca\u0000\u0859\u085a\u0005\u0116\u0000\u0000"+
		"\u085a\u085c\u0003\u0194\u00ca\u0000\u085b\u0859\u0001\u0000\u0000\u0000"+
		"\u085c\u085f\u0001\u0000\u0000\u0000\u085d\u085b\u0001\u0000\u0000\u0000"+
		"\u085d\u085e\u0001\u0000\u0000\u0000\u085e\u01d7\u0001\u0000\u0000\u0000"+
		"\u085f\u085d\u0001\u0000\u0000\u0000\u0860\u0861\u0005Q\u0000\u0000\u0861"+
		"\u0862\u0005\u0184\u0000\u0000\u0862\u01d9\u0001\u0000\u0000\u0000\u0863"+
		"\u0867\u0003\u01dc\u00ee\u0000\u0864\u0867\u0003\u01de\u00ef\u0000\u0865"+
		"\u0867\u0003\u01e0\u00f0\u0000\u0866\u0863\u0001\u0000\u0000\u0000\u0866"+
		"\u0864\u0001\u0000\u0000\u0000\u0866\u0865\u0001\u0000\u0000\u0000\u0867"+
		"\u01db\u0001\u0000\u0000\u0000\u0868\u0869\u0003\u01e0\u00f0\u0000\u0869"+
		"\u086a\u0003\u009cN\u0000\u086a\u086b\u0003\u01e6\u00f3\u0000\u086b\u01dd"+
		"\u0001\u0000\u0000\u0000\u086c\u086d\u0003\u01e0\u00f0\u0000\u086d\u086e"+
		"\u0003\u009cN\u0000\u086e\u01df\u0001\u0000\u0000\u0000\u086f\u0870\u0005"+
		"\u011e\u0000\u0000\u0870\u0871\u0005\u0097\u0000\u0000\u0871\u0872\u0003"+
		"\u01e2\u00f1\u0000\u0872\u0873\u0005\u011f\u0000\u0000\u0873\u01e1\u0001"+
		"\u0000\u0000\u0000\u0874\u0879\u0003\u01e4\u00f2\u0000\u0875\u0876\u0005"+
		"\u0116\u0000\u0000\u0876\u0878\u0003\u01e4\u00f2\u0000\u0877\u0875\u0001"+
		"\u0000\u0000\u0000\u0878\u087b\u0001\u0000\u0000\u0000\u0879\u0877\u0001"+
		"\u0000\u0000\u0000\u0879\u087a\u0001\u0000\u0000\u0000\u087a\u01e3\u0001"+
		"\u0000\u0000\u0000\u087b\u0879\u0001\u0000\u0000\u0000\u087c\u087d\u0005"+
		"\u011e\u0000\u0000\u087d\u087e\u0003\u01d6\u00eb\u0000\u087e\u087f\u0005"+
		"\u011f\u0000\u0000\u087f\u01e5\u0001\u0000\u0000\u0000\u0880\u0881\u0005"+
		"\u011e\u0000\u0000\u0881\u0882\u0003\u01e8\u00f4\u0000\u0882\u0883\u0005"+
		"\u011f\u0000\u0000\u0883\u01e7\u0001\u0000\u0000\u0000\u0884\u0889\u0003"+
		"\u0216\u010b\u0000\u0885\u0886\u0005\u0116\u0000\u0000\u0886\u0888\u0003"+
		"\u0216\u010b\u0000\u0887\u0885\u0001\u0000\u0000\u0000\u0888\u088b\u0001"+
		"\u0000\u0000\u0000\u0889\u0887\u0001\u0000\u0000\u0000\u0889\u088a\u0001"+
		"\u0000\u0000\u0000\u088a\u01e9\u0001\u0000\u0000\u0000\u088b\u0889\u0001"+
		"\u0000\u0000\u0000\u088c\u088d\u0005\u0097\u0000\u0000\u088d\u088e\u0003"+
		"\u01e2\u00f1\u0000\u088e\u01eb\u0001\u0000\u0000\u0000\u088f\u0890\u0003"+
		"\u01ee\u00f7\u0000\u0890\u0891\u0003\u01f0\u00f8\u0000\u0891\u01ed\u0001"+
		"\u0000\u0000\u0000\u0892\u0893\u0005S\u0000\u0000\u0893\u01ef\u0001\u0000"+
		"\u0000\u0000\u0894\u0897\u0003\u0090H\u0000\u0895\u0897\u0003\u0218\u010c"+
		"\u0000\u0896\u0894\u0001\u0000\u0000\u0000\u0896\u0895\u0001\u0000\u0000"+
		"\u0000\u0897\u01f1\u0001\u0000\u0000\u0000\u0898\u0899\u0003\u0196\u00cb"+
		"\u0000\u0899\u089a\u0003\u01f4\u00fa\u0000\u089a\u01f3\u0001\u0000\u0000"+
		"\u0000\u089b\u089d\u0005\u001a\u0000\u0000\u089c\u089e\u0005!\u0000\u0000"+
		"\u089d\u089c\u0001\u0000\u0000\u0000\u089d\u089e\u0001\u0000\u0000\u0000"+
		"\u089e\u089f\u0001\u0000\u0000\u0000\u089f\u08a0\u0005\"\u0000\u0000\u08a0"+
		"\u01f5\u0001\u0000\u0000\u0000\u08a1\u08a3\u0005\u001a\u0000\u0000\u08a2"+
		"\u08a4\u0003\u01fa\u00fd\u0000\u08a3\u08a2\u0001\u0000\u0000\u0000\u08a3"+
		"\u08a4\u0001\u0000\u0000\u0000\u08a4\u08a5\u0001\u0000\u0000\u0000\u08a5"+
		"\u08a6\u0003\u01f8\u00fc\u0000\u08a6\u01f7\u0001\u0000\u0000\u0000\u08a7"+
		"\u08a8\u0007\u0017\u0000\u0000\u08a8\u01f9\u0001\u0000\u0000\u0000\u08a9"+
		"\u08aa\u0005!\u0000\u0000\u08aa\u01fb\u0001\u0000\u0000\u0000\u08ab\u08ac"+
		"\u0003\u015e\u00af\u0000\u08ac\u08ad\u0003\u01c8\u00e4\u0000\u08ad\u08ae"+
		"\u0003\u01fe\u00ff\u0000\u08ae\u08af\u0003\u0090H\u0000\u08af\u01fd\u0001"+
		"\u0000\u0000\u0000\u08b0\u08b3\u0003\u0200\u0100\u0000\u08b1\u08b3\u0003"+
		"\u0202\u0101\u0000\u08b2\u08b0\u0001\u0000\u0000\u0000\u08b2\u08b1\u0001"+
		"\u0000\u0000\u0000\u08b3\u01ff\u0001\u0000\u0000\u0000\u08b4\u08b5\u0005"+
		"\u0002\u0000\u0000\u08b5\u0201\u0001\u0000\u0000\u0000\u08b6\u08b7\u0007"+
		"\u0018\u0000\u0000\u08b7\u0203\u0001\u0000\u0000\u0000\u08b8\u08b9\u0005"+
		"6\u0000\u0000\u08b9\u08ba\u0003\u0090H\u0000\u08ba\u0205\u0001\u0000\u0000"+
		"\u0000\u08bb\u08be\u0003\u0208\u0104\u0000\u08bc\u08be\u0005\u0086\u0000"+
		"\u0000\u08bd\u08bb\u0001\u0000\u0000\u0000\u08bd\u08bc\u0001\u0000\u0000"+
		"\u0000\u08be\u0207\u0001\u0000\u0000\u0000\u08bf\u08c0\u0007\u0019\u0000"+
		"\u0000\u08c0\u0209\u0001\u0000\u0000\u0000\u08c1\u08c2\u0007\u001a\u0000"+
		"\u0000\u08c2\u020b\u0001\u0000\u0000\u0000\u08c3\u08c4\u0003\u020e\u0107"+
		"\u0000\u08c4\u08c6\u0005\u011e\u0000\u0000\u08c5\u08c7\u0003\u0212\u0109"+
		"\u0000\u08c6\u08c5\u0001\u0000\u0000\u0000\u08c6\u08c7\u0001\u0000\u0000"+
		"\u0000\u08c7\u08c8\u0001\u0000\u0000\u0000\u08c8\u08c9\u0005\u011f\u0000"+
		"\u0000\u08c9\u020d\u0001\u0000\u0000\u0000\u08ca\u08cd\u0003\u0214\u010a"+
		"\u0000\u08cb\u08cc\u0005\u0125\u0000\u0000\u08cc\u08ce\u0003\u0214\u010a"+
		"\u0000\u08cd\u08cb\u0001\u0000\u0000\u0000\u08cd\u08ce\u0001\u0000\u0000"+
		"\u0000\u08ce\u08d1\u0001\u0000\u0000\u0000\u08cf\u08d1\u0003\u0210\u0108"+
		"\u0000\u08d0\u08ca\u0001\u0000\u0000\u0000\u08d0\u08cf\u0001\u0000\u0000"+
		"\u0000\u08d1\u020f\u0001\u0000\u0000\u0000\u08d2\u08d3\u0007\u001b\u0000"+
		"\u0000\u08d3\u0211\u0001\u0000\u0000\u0000\u08d4\u08d9\u0003\u015a\u00ad"+
		"\u0000\u08d5\u08d6\u0005\u0116\u0000\u0000\u08d6\u08d8\u0003\u015a\u00ad"+
		"\u0000\u08d7\u08d5\u0001\u0000\u0000\u0000\u08d8\u08db\u0001\u0000\u0000"+
		"\u0000\u08d9\u08d7\u0001\u0000\u0000\u0000\u08d9\u08da\u0001\u0000\u0000"+
		"\u0000\u08da\u0213\u0001\u0000\u0000\u0000\u08db\u08d9\u0001\u0000\u0000"+
		"\u0000\u08dc\u08e2\u0003\u021a\u010d\u0000\u08dd\u08e2\u0003\u021c\u010e"+
		"\u0000\u08de\u08e2\u0003\u0234\u011a\u0000\u08df\u08e2\u0003\u0230\u0118"+
		"\u0000\u08e0\u08e2\u0003\u0232\u0119\u0000\u08e1\u08dc\u0001\u0000\u0000"+
		"\u0000\u08e1\u08dd\u0001\u0000\u0000\u0000\u08e1\u08de\u0001\u0000\u0000"+
		"\u0000\u08e1\u08df\u0001\u0000\u0000\u0000\u08e1\u08e0\u0001\u0000\u0000"+
		"\u0000\u08e2\u0215\u0001\u0000\u0000\u0000\u08e3\u08e9\u0003\u021a\u010d"+
		"\u0000\u08e4\u08e9\u0003\u021c\u010e\u0000\u08e5\u08e9\u0003\u0234\u011a"+
		"\u0000\u08e6\u08e9\u0003\u022e\u0117\u0000\u08e7\u08e9\u0003\u0230\u0118"+
		"\u0000\u08e8\u08e3\u0001\u0000\u0000\u0000\u08e8\u08e4\u0001\u0000\u0000"+
		"\u0000\u08e8\u08e5\u0001\u0000\u0000\u0000\u08e8\u08e6\u0001\u0000\u0000"+
		"\u0000\u08e8\u08e7\u0001\u0000\u0000\u0000\u08e9\u0217\u0001\u0000\u0000"+
		"\u0000\u08ea\u08ed\u0003\u021e\u010f\u0000\u08eb\u08ed\u0003\u0220\u0110"+
		"\u0000\u08ec\u08ea\u0001\u0000\u0000\u0000\u08ec\u08eb\u0001\u0000\u0000"+
		"\u0000\u08ed\u0219\u0001\u0000\u0000\u0000\u08ee\u08ef\u0005\u017c\u0000"+
		"\u0000\u08ef\u021b\u0001\u0000\u0000\u0000\u08f0\u08f1\u0005\u0145\u0000"+
		"\u0000\u08f1\u021d\u0001\u0000\u0000\u0000\u08f2\u08f3\u0005\u0146\u0000"+
		"\u0000\u08f3\u021f\u0001\u0000\u0000\u0000\u08f4\u08f5\u0007\u001c\u0000"+
		"\u0000\u08f5\u0221\u0001\u0000\u0000\u0000\u08f6\u08f7\u0005\u0187\u0000"+
		"\u0000\u08f7\u08f8\u0003\u0224\u0112\u0000\u08f8\u08f9\u0005\u0188\u0000"+
		"\u0000\u08f9\u08ff\u0001\u0000\u0000\u0000\u08fa\u08fb\u0005\u0187\u0000"+
		"\u0000\u08fb\u08fc\u0003\u022a\u0115\u0000\u08fc\u08fd\u0005\u0188\u0000"+
		"\u0000\u08fd\u08ff\u0001\u0000\u0000\u0000\u08fe\u08f6\u0001\u0000\u0000"+
		"\u0000\u08fe\u08fa\u0001\u0000\u0000\u0000\u08ff\u0223\u0001\u0000\u0000"+
		"\u0000\u0900\u0901\u0003\u0214\u010a\u0000\u0901\u0902\u0004\u0112\u0005"+
		"\u0001\u0902\u0904\u0005\u011e\u0000\u0000\u0903\u0905\u0003\u0226\u0113"+
		"\u0000\u0904\u0903\u0001\u0000\u0000\u0000\u0904\u0905\u0001\u0000\u0000"+
		"\u0000\u0905\u0906\u0001\u0000\u0000\u0000\u0906\u0907\u0005\u011f\u0000"+
		"\u0000\u0907\u0913\u0001\u0000\u0000\u0000\u0908\u0909\u0003\u0214\u010a"+
		"\u0000\u0909\u090a\u0005\u0125\u0000\u0000\u090a\u090b\u0003\u0214\u010a"+
		"\u0000\u090b\u090c\u0004\u0112\u0006\u0001\u090c\u090e\u0005\u011e\u0000"+
		"\u0000\u090d\u090f\u0003\u0226\u0113\u0000\u090e\u090d\u0001\u0000\u0000"+
		"\u0000\u090e\u090f\u0001\u0000\u0000\u0000\u090f\u0910\u0001\u0000\u0000"+
		"\u0000\u0910\u0911\u0005\u011f\u0000\u0000\u0911\u0913\u0001\u0000\u0000"+
		"\u0000\u0912\u0900\u0001\u0000\u0000\u0000\u0912\u0908\u0001\u0000\u0000"+
		"\u0000\u0913\u0225\u0001\u0000\u0000\u0000\u0914\u0919\u0003\u0228\u0114"+
		"\u0000\u0915\u0916\u0005\u0116\u0000\u0000\u0916\u0918\u0003\u0228\u0114"+
		"\u0000\u0917\u0915\u0001\u0000\u0000\u0000\u0918\u091b\u0001\u0000\u0000"+
		"\u0000\u0919\u0917\u0001\u0000\u0000\u0000\u0919\u091a\u0001\u0000\u0000"+
		"\u0000\u091a\u0227\u0001\u0000\u0000\u0000\u091b\u0919\u0001\u0000\u0000"+
		"\u0000\u091c\u0927\u0005\u0184\u0000\u0000\u091d\u0927\u0005\u012b\u0000"+
		"\u0000\u091e\u091f\u0003\u0214\u010a\u0000\u091f\u0920\u0005\u0113\u0000"+
		"\u0000\u0920\u0921\u0005\u0184\u0000\u0000\u0921\u0927\u0001\u0000\u0000"+
		"\u0000\u0922\u0923\u0003\u0214\u010a\u0000\u0923\u0924\u0005\u0113\u0000"+
		"\u0000\u0924\u0925\u0005\u012b\u0000\u0000\u0925\u0927\u0001\u0000\u0000"+
		"\u0000\u0926\u091c\u0001\u0000\u0000\u0000\u0926\u091d\u0001\u0000\u0000"+
		"\u0000\u0926\u091e\u0001\u0000\u0000\u0000\u0926\u0922\u0001\u0000\u0000"+
		"\u0000\u0927\u0229\u0001\u0000\u0000\u0000\u0928\u092d\u0003\u022c\u0116"+
		"\u0000\u0929\u092a\u0005\u0125\u0000\u0000\u092a\u092c\u0003\u022c\u0116"+
		"\u0000\u092b\u0929\u0001\u0000\u0000\u0000\u092c\u092f\u0001\u0000\u0000"+
		"\u0000\u092d\u092b\u0001\u0000\u0000\u0000\u092d\u092e\u0001\u0000\u0000"+
		"\u0000\u092e\u022b\u0001\u0000\u0000\u0000\u092f\u092d\u0001\u0000\u0000"+
		"\u0000\u0930\u0933\u0003\u0214\u010a\u0000\u0931\u0933\u0005\u012b\u0000"+
		"\u0000\u0932\u0930\u0001\u0000\u0000\u0000\u0932\u0931\u0001\u0000\u0000"+
		"\u0000\u0933\u022d\u0001\u0000\u0000\u0000\u0934\u0935\u0007\u001d\u0000"+
		"\u0000\u0935\u022f\u0001\u0000\u0000\u0000\u0936\u0937\u0005\u0180\u0000"+
		"\u0000\u0937\u0231\u0001\u0000\u0000\u0000\u0938\u0939\u0005\u0181\u0000"+
		"\u0000\u0939\u0233\u0001\u0000\u0000\u0000\u093a\u093b\u0007\u001e\u0000"+
		"\u0000\u093b\u0235\u0001\u0000\u0000\u0000\u093c\u093e\u0003\u0166\u00b3"+
		"\u0000\u093d\u093c\u0001\u0000\u0000\u0000\u093d\u093e\u0001\u0000\u0000"+
		"\u0000\u093e\u093f\u0001\u0000\u0000\u0000\u093f\u0940\u0003\u023a\u011d"+
		"\u0000\u0940\u0237\u0001\u0000\u0000\u0000\u0941\u0944\u0003\u023a\u011d"+
		"\u0000\u0942\u0944\u0003\u0240\u0120\u0000\u0943\u0941\u0001\u0000\u0000"+
		"\u0000\u0943\u0942\u0001\u0000\u0000\u0000\u0944\u0239\u0001\u0000\u0000"+
		"\u0000\u0945\u0948\u0005\u012b\u0000\u0000\u0946\u0948\u0003\u023c\u011e"+
		"\u0000\u0947\u0945\u0001\u0000\u0000\u0000\u0947\u0946\u0001\u0000\u0000"+
		"\u0000\u0948\u023b\u0001\u0000\u0000\u0000\u0949\u094a\u0005\u012b\u0000"+
		"\u0000\u094a\u094c\u0005\u0125\u0000\u0000\u094b\u094d\u0005\u012b\u0000"+
		"\u0000\u094c\u094b\u0001\u0000\u0000\u0000\u094c\u094d\u0001\u0000\u0000"+
		"\u0000\u094d\u094f\u0001\u0000\u0000\u0000\u094e\u0950\u0003\u023e\u011f"+
		"\u0000\u094f\u094e\u0001\u0000\u0000\u0000\u094f\u0950\u0001\u0000\u0000"+
		"\u0000\u0950\u095a\u0001\u0000\u0000\u0000\u0951\u0952\u0005\u0125\u0000"+
		"\u0000\u0952\u0954\u0005\u012b\u0000\u0000\u0953\u0955\u0003\u023e\u011f"+
		"\u0000\u0954\u0953\u0001\u0000\u0000\u0000\u0954\u0955\u0001\u0000\u0000"+
		"\u0000\u0955\u095a\u0001\u0000\u0000\u0000\u0956\u0957\u0005\u012b\u0000"+
		"\u0000\u0957\u095a\u0003\u023e\u011f\u0000\u0958\u095a\u0005\u017e\u0000"+
		"\u0000\u0959\u0949\u0001\u0000\u0000\u0000\u0959\u0951\u0001\u0000\u0000"+
		"\u0000\u0959\u0956\u0001\u0000\u0000\u0000\u0959\u0958\u0001\u0000\u0000"+
		"\u0000\u095a\u023d\u0001\u0000\u0000\u0000\u095b\u095c\u0005\u017d\u0000"+
		"\u0000\u095c\u095d\u0005\u012b\u0000\u0000\u095d\u023f\u0001\u0000\u0000"+
		"\u0000\u095e\u0962\u0003\u0242\u0121\u0000\u095f\u0962\u0003\u0244\u0122"+
		"\u0000\u0960\u0962\u0003\u024c\u0126\u0000\u0961\u095e\u0001\u0000\u0000"+
		"\u0000\u0961\u095f\u0001\u0000\u0000\u0000\u0961\u0960\u0001\u0000\u0000"+
		"\u0000\u0962\u0241\u0001\u0000\u0000\u0000\u0963\u0964\u0005\u0184\u0000"+
		"\u0000\u0964\u0243\u0001\u0000\u0000\u0000\u0965\u0969\u0003\u0248\u0124"+
		"\u0000\u0966\u0969\u0003\u0246\u0123\u0000\u0967\u0969\u0003\u024a\u0125"+
		"\u0000\u0968\u0965\u0001\u0000\u0000\u0000\u0968\u0966\u0001\u0000\u0000"+
		"\u0000\u0968\u0967\u0001\u0000\u0000\u0000\u0969\u0245\u0001\u0000\u0000"+
		"\u0000\u096a\u096b\u0005\u00ff\u0000\u0000\u096b\u096c\u0005\u0184\u0000"+
		"\u0000\u096c\u0247\u0001\u0000\u0000\u0000\u096d\u096e\u0005\u0101\u0000"+
		"\u0000\u096e\u096f\u0005\u0184\u0000\u0000\u096f\u0249\u0001\u0000\u0000"+
		"\u0000\u0970\u0971\u0005\u00fd\u0000\u0000\u0971\u0972\u0005\u0184\u0000"+
		"\u0000\u0972\u024b\u0001\u0000\u0000\u0000\u0973\u0974\u0007\u0017\u0000"+
		"\u0000\u0974\u024d\u0001\u0000\u0000\u0000\u0975\u0979\u0003\u0250\u0128"+
		"\u0000\u0976\u0979\u0003\u0256\u012b\u0000\u0977\u0979\u0003\u025c\u012e"+
		"\u0000\u0978\u0975\u0001\u0000\u0000\u0000\u0978\u0976\u0001\u0000\u0000"+
		"\u0000\u0978\u0977\u0001\u0000\u0000\u0000\u0979\u024f\u0001\u0000\u0000"+
		"\u0000\u097a\u097c\u0003\u0252\u0129\u0000\u097b\u097d\u0003\u0254\u012a"+
		"\u0000\u097c\u097b\u0001\u0000\u0000\u0000\u097c\u097d\u0001\u0000\u0000"+
		"\u0000\u097d\u0251\u0001\u0000\u0000\u0000\u097e\u09a1\u0005A\u0000\u0000"+
		"\u097f\u09a1\u0005\u00f7\u0000\u0000\u0980\u0981\u0005A\u0000\u0000\u0981"+
		"\u09a1\u0005\u009a\u0000\u0000\u0982\u0983\u0005\u00f7\u0000\u0000\u0983"+
		"\u09a1\u0005\u009a\u0000\u0000\u0984\u09a1\u0005\u00f8\u0000\u0000\u0985"+
		"\u09a1\u0005\u00f9\u0000\u0000\u0986\u0987\u0005s\u0000\u0000\u0987\u09a1"+
		"\u0005A\u0000\u0000\u0988\u0989\u0005s\u0000\u0000\u0989\u09a1\u0005\u00f7"+
		"\u0000\u0000\u098a\u09a1\u0005\u00fa\u0000\u0000\u098b\u098c\u0005s\u0000"+
		"\u0000\u098c\u098d\u0005A\u0000\u0000\u098d\u09a1\u0005\u009a\u0000\u0000"+
		"\u098e\u098f\u0005s\u0000\u0000\u098f\u0990\u0005\u00f7\u0000\u0000\u0990"+
		"\u09a1\u0005\u009a\u0000\u0000\u0991\u0992\u0005\u00fa\u0000\u0000\u0992"+
		"\u09a1\u0005\u009a\u0000\u0000\u0993\u09a1\u0005\u00fb\u0000\u0000\u0994"+
		"\u09a1\u0005\u0109\u0000\u0000\u0995\u09a1\u0005\u010a\u0000\u0000\u0996"+
		"\u09a1\u0005\u00d3\u0000\u0000\u0997\u09a1\u0005\u00d4\u0000\u0000\u0998"+
		"\u0999\u0005\u00d3\u0000\u0000\u0999\u09a1\u0005\u009a\u0000\u0000\u099a"+
		"\u09a1\u0005\u0107\u0000\u0000\u099b\u099c\u0005\u0107\u0000\u0000\u099c"+
		"\u09a1\u0005\u009a\u0000\u0000\u099d\u09a1\u0005\u0108\u0000\u0000\u099e"+
		"\u09a1\u0005\u00d8\u0000\u0000\u099f\u09a1\u0005\u00fc\u0000\u0000\u09a0"+
		"\u097e\u0001\u0000\u0000\u0000\u09a0\u097f\u0001\u0000\u0000\u0000\u09a0"+
		"\u0980\u0001\u0000\u0000\u0000\u09a0\u0982\u0001\u0000\u0000\u0000\u09a0"+
		"\u0984\u0001\u0000\u0000\u0000\u09a0\u0985\u0001\u0000\u0000\u0000\u09a0"+
		"\u0986\u0001\u0000\u0000\u0000\u09a0\u0988\u0001\u0000\u0000\u0000\u09a0"+
		"\u098a\u0001\u0000\u0000\u0000\u09a0\u098b\u0001\u0000\u0000\u0000\u09a0"+
		"\u098e\u0001\u0000\u0000\u0000\u09a0\u0991\u0001\u0000\u0000\u0000\u09a0"+
		"\u0993\u0001\u0000\u0000\u0000\u09a0\u0994\u0001\u0000\u0000\u0000\u09a0"+
		"\u0995\u0001\u0000\u0000\u0000\u09a0\u0996\u0001\u0000\u0000\u0000\u09a0"+
		"\u0997\u0001\u0000\u0000\u0000\u09a0\u0998\u0001\u0000\u0000\u0000\u09a0"+
		"\u099a\u0001\u0000\u0000\u0000\u09a0\u099b\u0001\u0000\u0000\u0000\u09a0"+
		"\u099d\u0001\u0000\u0000\u0000\u09a0\u099e\u0001\u0000\u0000\u0000\u09a0"+
		"\u099f\u0001\u0000\u0000\u0000\u09a1\u0253\u0001\u0000\u0000\u0000\u09a2"+
		"\u09a3\u0005\u011e\u0000\u0000\u09a3\u09a4\u0005\u012b\u0000\u0000\u09a4"+
		"\u09a5\u0005\u011f\u0000\u0000\u09a5\u0255\u0001\u0000\u0000\u0000\u09a6"+
		"\u09a8\u0003\u0258\u012c\u0000\u09a7\u09a9\u0003\u025a\u012d\u0000\u09a8"+
		"\u09a7\u0001\u0000\u0000\u0000\u09a8\u09a9\u0001\u0000\u0000\u0000\u09a9"+
		"\u0257\u0001\u0000\u0000\u0000\u09aa\u09b6\u0005\u00f5\u0000\u0000\u09ab"+
		"\u09b6\u0005\u012b\u0000\u0000\u09ac\u09b6\u0005\u00f6\u0000\u0000\u09ad"+
		"\u09b6\u0005J\u0000\u0000\u09ae\u09b6\u0005\u00f3\u0000\u0000\u09af\u09b6"+
		"\u0005\u00f4\u0000\u0000\u09b0\u09b1\u0005\u00f4\u0000\u0000\u09b1\u09b6"+
		"\u0005{\u0000\u0000\u09b2\u09b6\u0005\u0102\u0000\u0000\u09b3\u09b6\u0005"+
		"\u0103\u0000\u0000\u09b4\u09b6\u0005\u0104\u0000\u0000\u09b5\u09aa\u0001"+
		"\u0000\u0000\u0000\u09b5\u09ab\u0001\u0000\u0000\u0000\u09b5\u09ac\u0001"+
		"\u0000\u0000\u0000\u09b5\u09ad\u0001\u0000\u0000\u0000\u09b5\u09ae\u0001"+
		"\u0000\u0000\u0000\u09b5\u09af\u0001\u0000\u0000\u0000\u09b5\u09b0\u0001"+
		"\u0000\u0000\u0000\u09b5\u09b2\u0001\u0000\u0000\u0000\u09b5\u09b3\u0001"+
		"\u0000\u0000\u0000\u09b5\u09b4\u0001\u0000\u0000\u0000\u09b6\u0259\u0001"+
		"\u0000\u0000\u0000\u09b7\u09b8\u0005\u011e\u0000\u0000\u09b8\u09b9\u0005"+
		"\u012b\u0000\u0000\u09b9\u09c0\u0005\u011f\u0000\u0000\u09ba\u09bb\u0005"+
		"\u011e\u0000\u0000\u09bb\u09bc\u0005\u012b\u0000\u0000\u09bc\u09bd\u0005"+
		"\u0116\u0000\u0000\u09bd\u09be\u0005\u012b\u0000\u0000\u09be\u09c0\u0005"+
		"\u011f\u0000\u0000\u09bf\u09b7\u0001\u0000\u0000\u0000\u09bf\u09ba\u0001"+
		"\u0000\u0000\u0000\u09c0\u025b\u0001\u0000\u0000\u0000\u09c1\u09c2\u0003"+
		"\u025e\u012f\u0000\u09c2\u025d\u0001\u0000\u0000\u0000\u09c3\u0a01\u0005"+
		"\u0106\u0000\u0000\u09c4\u0a01\u0005\u00e0\u0000\u0000\u09c5\u0a01\u0005"+
		"\u00d7\u0000\u0000\u09c6\u0a01\u0005\u00d6\u0000\u0000\u09c7\u0a01\u0005"+
		"\u00d5\u0000\u0000\u09c8\u0a01\u0005\u010c\u0000\u0000\u09c9\u0a01\u0005"+
		"5\u0000\u0000\u09ca\u0a01\u0005\u010d\u0000\u0000\u09cb\u0a01\u0005\u010b"+
		"\u0000\u0000\u09cc\u0a01\u0005\u00dd\u0000\u0000\u09cd\u0a01\u0005\u00de"+
		"\u0000\u0000\u09ce\u0a01\u0005\u00e1\u0000\u0000\u09cf\u0a01\u0005\u00e5"+
		"\u0000\u0000\u09d0\u0a01\u0005\u00e6\u0000\u0000\u09d1\u0a01\u0005\u00e2"+
		"\u0000\u0000\u09d2\u0a01\u0005\u00e3\u0000\u0000\u09d3\u0a01\u0005\u00e4"+
		"\u0000\u0000\u09d4\u0a01\u0005\u00df\u0000\u0000\u09d5\u0a01\u0005\u00d9"+
		"\u0000\u0000\u09d6\u0a01\u0005\u00e7\u0000\u0000\u09d7\u0a01\u0005\u00da"+
		"\u0000\u0000\u09d8\u0a01\u0005\u00e8\u0000\u0000\u09d9\u0a01\u0005\u00db"+
		"\u0000\u0000\u09da\u0a01\u0005\u00e9\u0000\u0000\u09db\u0a01\u0005\u00ea"+
		"\u0000\u0000\u09dc\u0a01\u0005\u00dc\u0000\u0000\u09dd\u0a01\u0005\u00eb"+
		"\u0000\u0000\u09de\u0a01\u0005\u00ec\u0000\u0000\u09df\u0a01\u0005\u00ed"+
		"\u0000\u0000\u09e0\u0a01\u0005\u00ee\u0000\u0000\u09e1\u0a01\u0005\u00ef"+
		"\u0000\u0000\u09e2\u0a01\u0005$\u0000\u0000\u09e3\u0a01\u0005\u00f0\u0000"+
		"\u0000\u09e4\u0a01\u0005\u00f2\u0000\u0000\u09e5\u0a01\u0005\u00f1\u0000"+
		"\u0000\u09e6\u0a01\u0005\u00d1\u0000\u0000\u09e7\u0a01\u0005\u00d2\u0000"+
		"\u0000\u09e8\u0a01\u0005\u00fd\u0000\u0000\u09e9\u0a01\u0005\u00fe\u0000"+
		"\u0000\u09ea\u0a01\u0005\u00ff\u0000\u0000\u09eb\u09ec\u0005\u00ff\u0000"+
		"\u0000\u09ec\u09ed\u0005:\u0000\u0000\u09ed\u09ee\u0005\u00ff\u0000\u0000"+
		"\u09ee\u0a01\u0005\u009d\u0000\u0000\u09ef\u0a01\u0005\u0100\u0000\u0000"+
		"\u09f0\u0a01\u0005\u0102\u0000\u0000\u09f1\u0a01\u0005\u0103\u0000\u0000"+
		"\u09f2\u0a01\u0005\u0104\u0000\u0000\u09f3\u0a01\u0005\u0101\u0000\u0000"+
		"\u09f4\u09f5\u0005\u0101\u0000\u0000\u09f5\u09f6\u0005:\u0000\u0000\u09f6"+
		"\u09f7\u0005\u00ff\u0000\u0000\u09f7\u0a01\u0005\u009d\u0000\u0000\u09f8"+
		"\u09f9\u0005\u0101\u0000\u0000\u09f9\u09fa\u0005;\u0000\u0000\u09fa\u09fb"+
		"\u0005\u00ff\u0000\u0000\u09fb\u0a01\u0005\u009d\u0000\u0000\u09fc\u0a01"+
		"\u0005\u0105\u0000\u0000\u09fd\u0a01\u0005\u00ce\u0000\u0000\u09fe\u0a01"+
		"\u0005\u00d0\u0000\u0000\u09ff\u0a01\u0005\u00cf\u0000\u0000\u0a00\u09c3"+
		"\u0001\u0000\u0000\u0000\u0a00\u09c4\u0001\u0000\u0000\u0000\u0a00\u09c5";
	private static final String _serializedATNSegment1 =
		"\u0001\u0000\u0000\u0000\u0a00\u09c6\u0001\u0000\u0000\u0000\u0a00\u09c7"+
		"\u0001\u0000\u0000\u0000\u0a00\u09c8\u0001\u0000\u0000\u0000\u0a00\u09c9"+
		"\u0001\u0000\u0000\u0000\u0a00\u09ca\u0001\u0000\u0000\u0000\u0a00\u09cb"+
		"\u0001\u0000\u0000\u0000\u0a00\u09cc\u0001\u0000\u0000\u0000\u0a00\u09cd"+
		"\u0001\u0000\u0000\u0000\u0a00\u09ce\u0001\u0000\u0000\u0000\u0a00\u09cf"+
		"\u0001\u0000\u0000\u0000\u0a00\u09d0\u0001\u0000\u0000\u0000\u0a00\u09d1"+
		"\u0001\u0000\u0000\u0000\u0a00\u09d2\u0001\u0000\u0000\u0000\u0a00\u09d3"+
		"\u0001\u0000\u0000\u0000\u0a00\u09d4\u0001\u0000\u0000\u0000\u0a00\u09d5"+
		"\u0001\u0000\u0000\u0000\u0a00\u09d6\u0001\u0000\u0000\u0000\u0a00\u09d7"+
		"\u0001\u0000\u0000\u0000\u0a00\u09d8\u0001\u0000\u0000\u0000\u0a00\u09d9"+
		"\u0001\u0000\u0000\u0000\u0a00\u09da\u0001\u0000\u0000\u0000\u0a00\u09db"+
		"\u0001\u0000\u0000\u0000\u0a00\u09dc\u0001\u0000\u0000\u0000\u0a00\u09dd"+
		"\u0001\u0000\u0000\u0000\u0a00\u09de\u0001\u0000\u0000\u0000\u0a00\u09df"+
		"\u0001\u0000\u0000\u0000\u0a00\u09e0\u0001\u0000\u0000\u0000\u0a00\u09e1"+
		"\u0001\u0000\u0000\u0000\u0a00\u09e2\u0001\u0000\u0000\u0000\u0a00\u09e3"+
		"\u0001\u0000\u0000\u0000\u0a00\u09e4\u0001\u0000\u0000\u0000\u0a00\u09e5"+
		"\u0001\u0000\u0000\u0000\u0a00\u09e6\u0001\u0000\u0000\u0000\u0a00\u09e7"+
		"\u0001\u0000\u0000\u0000\u0a00\u09e8\u0001\u0000\u0000\u0000\u0a00\u09e9"+
		"\u0001\u0000\u0000\u0000\u0a00\u09ea\u0001\u0000\u0000\u0000\u0a00\u09eb"+
		"\u0001\u0000\u0000\u0000\u0a00\u09ef\u0001\u0000\u0000\u0000\u0a00\u09f0"+
		"\u0001\u0000\u0000\u0000\u0a00\u09f1\u0001\u0000\u0000\u0000\u0a00\u09f2"+
		"\u0001\u0000\u0000\u0000\u0a00\u09f3\u0001\u0000\u0000\u0000\u0a00\u09f4"+
		"\u0001\u0000\u0000\u0000\u0a00\u09f8\u0001\u0000\u0000\u0000\u0a00\u09fc"+
		"\u0001\u0000\u0000\u0000\u0a00\u09fd\u0001\u0000\u0000\u0000\u0a00\u09fe"+
		"\u0001\u0000\u0000\u0000\u0a00\u09ff\u0001\u0000\u0000\u0000\u0a01\u025f"+
		"\u0001\u0000\u0000\u0000\u0a02\u0a03\u0007\u001f\u0000\u0000\u0a03\u0261"+
		"\u0001\u0000\u0000\u0000\u00f3\u0265\u0269\u0271\u0274\u027d\u0280\u0284"+
		"\u0287\u02a2\u02c3\u02c9\u02cf\u02d3\u02db\u02e2\u02f2\u02f5\u02f7\u0314"+
		"\u0320\u032a\u0339\u033f\u0345\u034b\u0351\u0358\u036a\u0373\u0378\u0383"+
		"\u038e\u0392\u039c\u03a7\u03ad\u03b0\u03b4\u03b8\u03bf\u03c6\u03c9\u03cc"+
		"\u03d0\u03d6\u03d9\u03df\u03e2\u03f4\u0403\u0408\u0412\u0417\u041e\u0426"+
		"\u0429\u042e\u0431\u0434\u0437\u043a\u043d\u043f\u044b\u0450\u0453\u0456"+
		"\u045e\u0465\u046c\u0471\u0477\u047b\u047d\u047f\u0484\u0489\u048f\u0493"+
		"\u0495\u0497\u049b\u04a1\u04a4\u04a8\u04af\u04b3\u04b8\u04ba\u04c0\u04c4"+
		"\u04cc\u04d2\u04d4\u04d9\u04db\u04e3\u04e6\u04e9\u04f0\u04f2\u04f6\u0504"+
		"\u0508\u0512\u051a\u0523\u052b\u052e\u0532\u053d\u054a\u0552\u055e\u0574"+
		"\u0579\u057f\u058d\u0598\u05a1\u05b1\u05cb\u05d1\u05db\u05ee\u05f6\u05fd"+
		"\u0605\u060b\u0612\u0619\u061e\u0625\u062d\u0639\u0640\u0648\u064f\u065e"+
		"\u0669\u066d\u0672\u067c\u0683\u0687\u068c\u0696\u06a2\u06b5\u06be\u06c1"+
		"\u06c3\u06c8\u06cb\u06ce\u06de\u06e8\u0701\u0706\u070d\u0715\u0719\u071f"+
		"\u072d\u0733\u073a\u073f\u0747\u074c\u074f\u0752\u0759\u0767\u076f\u0773"+
		"\u0782\u078a\u078e\u0798\u079a\u079e\u07a7\u07ad\u07b2\u07c2\u07c7\u07ca"+
		"\u07d7\u07dd\u07e4\u07eb\u07f2\u07fa\u0814\u081d\u0821\u0829\u0831\u0835"+
		"\u083f\u0846\u084b\u0856\u085d\u0866\u0879\u0889\u0896\u089d\u08a3\u08b2"+
		"\u08bd\u08c6\u08cd\u08d0\u08d9\u08e1\u08e8\u08ec\u08fe\u0904\u090e\u0912"+
		"\u0919\u0926\u092d\u0932\u093d\u0943\u0947\u094c\u094f\u0954\u0959\u0961"+
		"\u0968\u0978\u097c\u09a0\u09a8\u09b5\u09bf\u0a00";
	public static final String _serializedATN = Utils.join(
		new String[] {
			_serializedATNSegment0,
			_serializedATNSegment1
		},
		""
	);
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}