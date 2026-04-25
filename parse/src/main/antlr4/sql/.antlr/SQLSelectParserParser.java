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
		NOT_EQUAL=280, LTH=281, LEQ=282, GTH=283, GEQ=284, LEFT_PAREN=285, RIGHT_PAREN=286, 
		PLUS=287, MINUS=288, MULTIPLY=289, DIVIDE=290, MODULAR=291, DOT=292, UNDERLINE=293, 
		VERTICAL_BAR=294, QUOTE=295, DOUBLE_QUOTE=296, CAST_OPERATOR=297, NUMBER=298, 
		PUML_CONSTANT_TENANT_SK=299, PUML_CONSTANT_TENANT_GUID=300, PUML_CONSTANT_TENANT_MASTER_ID=301, 
		PUML_CONSTANT_TENANT_NAME=302, PUML_CONSTANT_TENANT_ACRONYM=303, PUML_CONSTANT_TENANT_WEB_DOMAIN=304, 
		PUML_CONSTANT_ES_INSTITUTION_ID=305, PUML_CONSTANT_ES_INSTITUTION_CODE=306, 
		PUML_CONSTANT_ES_INSTITUTION_NAME=307, PUML_CONSTANT_SF_COUNTER_ID=308, 
		PUML_CONSTANT_FILE_NAME=309, PUML_CONSTANT_FILE_ID=310, PUML_CONSTANT_ROW_NUMBER=311, 
		PUML_CONSTANT_OBSERVATION_TIME=312, PUML_CONSTANT_SYSTEM_DATE=313, PUML_CONSTANT_SYSTEM_TIME=314, 
		PUML_CONSTANT_FEED_RUN_ID=315, PUML_CONSTANT_FEED_NAME=316, PUML_CONSTANT_TRANSACTION_RUN_ID=317, 
		PUML_CONSTANT_TRANSACTION_NAME=318, PUML_CONSTANT_POPULATION=319, PUML_CONSTANT_TARGET_MODEL_NAME=320, 
		PUML_CONSTANT_TENANT_SALT=321, PUML_CONSTANT_PIT_START_TIME=322, PUML_CONSTANT_PIT_END_TIME=323, 
		Bracket_Identifier=324, Variable_Identifier=325, Extended_Variable_Identifier=326, 
		Mixed_Variable_Identifier=327, QUALIFY=328, Identifier=329, EXPONEN=330, 
		Scientific_Numeric_Literal=331, Numeric_Identifier=332, Double_Quoted_Numeric_Identifier=333, 
		Dollar_Sign_Identifier=334, BlockComment=335, LineComment=336, Character_String_Literal=337, 
		Space=338, White_Space=339, JINJA_OPEN=340, JINJA_CLOSE=341, BAD=342;
	public static final int
		RULE_sql = 0, RULE_column_value = 1, RULE_predicand_value = 2, RULE_in_list_predicate_value = 3, 
		RULE_condition_value = 4, RULE_tuple_value = 5, RULE_query_value = 6, 
		RULE_join_extension_value = 7, RULE_literal_value = 8, RULE_values_statement_end = 9, 
		RULE_insert_end_point = 10, RULE_with_query = 11, RULE_with_clause = 12, 
		RULE_with_list_item = 13, RULE_cte_body = 14, RULE_query_alias = 15, RULE_query = 16, 
		RULE_insert_expression = 17, RULE_snowflake_insert = 18, RULE_postgres_insert = 19, 
		RULE_insert_preamble = 20, RULE_insert_source_primary = 21, RULE_update_expression = 22, 
		RULE_returning = 23, RULE_assignment_expression_list = 24, RULE_assignment_expression = 25, 
		RULE_create_table_as_expression = 26, RULE_query_expression = 27, RULE_intersected_query = 28, 
		RULE_intersect_clause = 29, RULE_intersect_operator = 30, RULE_unionized_query = 31, 
		RULE_union_clause = 32, RULE_union_operator = 33, RULE_query_primary = 34, 
		RULE_subquery = 35, RULE_query_specification = 36, RULE_into_list = 37, 
		RULE_set_qualifier = 38, RULE_select_list = 39, RULE_select_item = 40, 
		RULE_as_clause = 41, RULE_select_all_columns = 42, RULE_wildcard_reference = 43, 
		RULE_from_clause = 44, RULE_join_extension = 45, RULE_table_reference_list = 46, 
		RULE_join_extension_primary = 47, RULE_table_primary = 48, RULE_relation_as_clause = 49, 
		RULE_tuple_primary = 50, RULE_table_or_query_name = 51, RULE_unqualified_join = 52, 
		RULE_qualified_join = 53, RULE_join_type = 54, RULE_join_specification = 55, 
		RULE_join_condition = 56, RULE_named_columns_join = 57, RULE_using_term = 58, 
		RULE_column_reference_list = 59, RULE_column_reference = 60, RULE_column_primary = 61, 
		RULE_predicand_primary = 62, RULE_value_expression_primary = 63, RULE_parenthesized_value_expression = 64, 
		RULE_nonparenthesized_value_expression_primary = 65, RULE_predicand_subquery = 66, 
		RULE_aggregate_function = 67, RULE_set_function_type = 68, RULE_set_qualifier_type = 69, 
		RULE_case_expression = 70, RULE_when_clause_list = 71, RULE_searched_when_clause = 72, 
		RULE_when_value_list = 73, RULE_when_value_clause = 74, RULE_else_clause = 75, 
		RULE_case_result = 76, RULE_null_literal = 77, RULE_cast_function_expression = 78, 
		RULE_cast_function_name = 79, RULE_window_over_partition_expression = 80, 
		RULE_window_function = 81, RULE_over_clause = 82, RULE_partition_by_clause = 83, 
		RULE_bracket_frame_clause = 84, RULE_rows_or_range = 85, RULE_bracket_frame_definition = 86, 
		RULE_between_frame_definition = 87, RULE_frame_edge = 88, RULE_preceding_frame_edge = 89, 
		RULE_following_frame_edge = 90, RULE_current_row_edge = 91, RULE_bracket_constraint = 92, 
		RULE_item_select_function = 93, RULE_select_direction = 94, RULE_null_handling = 95, 
		RULE_value_expression = 96, RULE_common_value_expression = 97, RULE_additive_expression = 98, 
		RULE_multiplicative_expression = 99, RULE_factor = 100, RULE_numeric_primary = 101, 
		RULE_sign = 102, RULE_extract_expression = 103, RULE_extract_field = 104, 
		RULE_time_zone_field = 105, RULE_extract_source = 106, RULE_string_value_expression = 107, 
		RULE_character_primary = 108, RULE_trim_function = 109, RULE_trim_function_name = 110, 
		RULE_trim_operands = 111, RULE_trim_specification = 112, RULE_boolean_value_expression = 113, 
		RULE_or_predicate = 114, RULE_and_predicate = 115, RULE_negative_predicate = 116, 
		RULE_parenthetical_predicate = 117, RULE_boolean_primary = 118, RULE_predicate = 119, 
		RULE_substitution_predicate = 120, RULE_row_value_expression = 121, RULE_row_value_predicand = 122, 
		RULE_where_clause = 123, RULE_search_condition = 124, RULE_orderby_clause = 125, 
		RULE_sort_specifier_list = 126, RULE_sort_specifier = 127, RULE_order_specification = 128, 
		RULE_null_ordering = 129, RULE_null_first_last = 130, RULE_limit_clause = 131, 
		RULE_groupby_clause = 132, RULE_grouping_element_list = 133, RULE_grouping_element = 134, 
		RULE_ordinary_grouping_set_list = 135, RULE_ordinary_grouping_set = 136, 
		RULE_rollup_list = 137, RULE_cube_list = 138, RULE_empty_grouping_set = 139, 
		RULE_having_clause = 140, RULE_qualify_clause = 141, RULE_row_value_predicand_list = 142, 
		RULE_comparison_predicate = 143, RULE_comparison_operator = 144, RULE_relative_comp_op = 145, 
		RULE_similarity_op = 146, RULE_comp_op = 147, RULE_between_predicate = 148, 
		RULE_symmetry = 149, RULE_in_predicate = 150, RULE_like_any_predicate = 151, 
		RULE_like_any_operator = 152, RULE_in_predicate_value = 153, RULE_in_value_list = 154, 
		RULE_escape_character_clause = 155, RULE_values_statement_primary = 156, 
		RULE_fully_defined_values_statement = 157, RULE_aliased_values_statement = 158, 
		RULE_values_statement = 159, RULE_values_matrix = 160, RULE_values_row = 161, 
		RULE_values_aliases = 162, RULE_values_aliases_list = 163, RULE_insert_values_statement = 164, 
		RULE_exists_predicate = 165, RULE_exists_operator = 166, RULE_exists_predicate_value = 167, 
		RULE_null_predicate = 168, RULE_is_null_clause = 169, RULE_is_clause = 170, 
		RULE_truth_value = 171, RULE_not = 172, RULE_quantified_comparison_predicate = 173, 
		RULE_quantifier = 174, RULE_all = 175, RULE_some = 176, RULE_unique_predicate = 177, 
		RULE_primary_datetime_field = 178, RULE_non_second_primary_datetime_field = 179, 
		RULE_extended_datetime_field = 180, RULE_routine_invocation = 181, RULE_function_name = 182, 
		RULE_function_names_for_reserved_words = 183, RULE_sql_argument_list = 184, 
		RULE_identifier = 185, RULE_alias_identifier = 186, RULE_variable_identifier = 187, 
		RULE_simple_identifier = 188, RULE_logical_identifier = 189, RULE_simple_variable_identifier = 190, 
		RULE_extended_variable_identifier = 191, RULE_jinja_identifier = 192, 
		RULE_jinja_function_call = 193, RULE_jinja_arg_list = 194, RULE_jinja_arg = 195, 
		RULE_jinja_variable_access = 196, RULE_jinja_name = 197, RULE_simple_numeric_identifier = 198, 
		RULE_snowflake_quoted_numeric_identifier = 199, RULE_snowflake_dollar_function_identifier = 200, 
		RULE_nonreserved_keywords = 201, RULE_signed_numeric_literal = 202, RULE_unsigned_literal = 203, 
		RULE_unsigned_numeric_literal = 204, RULE_real_number_def = 205, RULE_exponent = 206, 
		RULE_general_literal = 207, RULE_character_literal = 208, RULE_datetime_literal = 209, 
		RULE_time_literal = 210, RULE_timestamp_literal = 211, RULE_date_literal = 212, 
		RULE_boolean_literal = 213, RULE_data_type = 214, RULE_variable_size_data_type = 215, 
		RULE_variable_data_type_name = 216, RULE_type_length = 217, RULE_precision_scale_data_type = 218, 
		RULE_precision_data_type_name = 219, RULE_precision_param = 220, RULE_static_data_type = 221, 
		RULE_static_data_type_name = 222, RULE_puml_constant_identifier = 223;
	private static String[] makeRuleNames() {
		return new String[] {
			"sql", "column_value", "predicand_value", "in_list_predicate_value", 
			"condition_value", "tuple_value", "query_value", "join_extension_value", 
			"literal_value", "values_statement_end", "insert_end_point", "with_query", 
			"with_clause", "with_list_item", "cte_body", "query_alias", "query", 
			"insert_expression", "snowflake_insert", "postgres_insert", "insert_preamble", 
			"insert_source_primary", "update_expression", "returning", "assignment_expression_list", 
			"assignment_expression", "create_table_as_expression", "query_expression", 
			"intersected_query", "intersect_clause", "intersect_operator", "unionized_query", 
			"union_clause", "union_operator", "query_primary", "subquery", "query_specification", 
			"into_list", "set_qualifier", "select_list", "select_item", "as_clause", 
			"select_all_columns", "wildcard_reference", "from_clause", "join_extension", 
			"table_reference_list", "join_extension_primary", "table_primary", "relation_as_clause", 
			"tuple_primary", "table_or_query_name", "unqualified_join", "qualified_join", 
			"join_type", "join_specification", "join_condition", "named_columns_join", 
			"using_term", "column_reference_list", "column_reference", "column_primary", 
			"predicand_primary", "value_expression_primary", "parenthesized_value_expression", 
			"nonparenthesized_value_expression_primary", "predicand_subquery", "aggregate_function", 
			"set_function_type", "set_qualifier_type", "case_expression", "when_clause_list", 
			"searched_when_clause", "when_value_list", "when_value_clause", "else_clause", 
			"case_result", "null_literal", "cast_function_expression", "cast_function_name", 
			"window_over_partition_expression", "window_function", "over_clause", 
			"partition_by_clause", "bracket_frame_clause", "rows_or_range", "bracket_frame_definition", 
			"between_frame_definition", "frame_edge", "preceding_frame_edge", "following_frame_edge", 
			"current_row_edge", "bracket_constraint", "item_select_function", "select_direction", 
			"null_handling", "value_expression", "common_value_expression", "additive_expression", 
			"multiplicative_expression", "factor", "numeric_primary", "sign", "extract_expression", 
			"extract_field", "time_zone_field", "extract_source", "string_value_expression", 
			"character_primary", "trim_function", "trim_function_name", "trim_operands", 
			"trim_specification", "boolean_value_expression", "or_predicate", "and_predicate", 
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
			"'('", "')'", "'+'", "'-'", "'*'", "'/'", "'%'", null, "'_'", "'|'", 
			"'''", "'\"'", "'::'", null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, "' '", null, "'{{'", 
			"'}}'"
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
			"GEQ", "LEFT_PAREN", "RIGHT_PAREN", "PLUS", "MINUS", "MULTIPLY", "DIVIDE", 
			"MODULAR", "DOT", "UNDERLINE", "VERTICAL_BAR", "QUOTE", "DOUBLE_QUOTE", 
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
			"QUALIFY", "Identifier", "EXPONEN", "Scientific_Numeric_Literal", "Numeric_Identifier", 
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
			setState(451);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case WITH:
				{
				setState(448);
				with_query();
				}
				break;
			case CREATE:
				{
				setState(449);
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
				setState(450);
				query();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(454);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEMI_COLON) {
				{
				setState(453);
				match(SEMI_COLON);
				}
			}

			setState(456);
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
			setState(458);
			column_primary();
			setState(459);
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
			setState(461);
			predicand_primary();
			setState(462);
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
			setState(464);
			in_predicate_value();
			setState(465);
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
			setState(467);
			value_expression();
			setState(468);
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
			setState(470);
			tuple_primary();
			setState(471);
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
			setState(473);
			query();
			setState(474);
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
			setState(476);
			join_extension_primary();
			setState(477);
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
			setState(481);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				{
				setState(479);
				signed_numeric_literal();
				}
				break;
			case 2:
				{
				setState(480);
				unsigned_literal();
				}
				break;
			}
			setState(483);
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
			setState(485);
			values_statement_primary();
			setState(486);
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
			setState(488);
			insert_expression();
			setState(489);
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
			setState(491);
			with_clause();
			setState(492);
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
			setState(494);
			match(WITH);
			setState(495);
			with_list_item();
			setState(500);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(496);
				match(COMMA);
				setState(497);
				with_list_item();
				}
				}
				setState(502);
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
			setState(511);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(503);
				query_alias();
				{
				setState(504);
				match(LEFT_PAREN);
				setState(505);
				cte_body();
				setState(506);
				match(RIGHT_PAREN);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(508);
				query_alias();
				setState(509);
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
			setState(515);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case WITH:
				enterOuterAlt(_localctx, 1);
				{
				setState(513);
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
				setState(514);
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
			setState(517);
			identifier();
			setState(518);
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
			setState(524);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(520);
				query_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(521);
				insert_expression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(522);
				update_expression();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(523);
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
			setState(526);
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
		public Table_primaryContext table_primary() {
			return getRuleContext(Table_primaryContext.class,0);
		}
		public Insert_source_primaryContext insert_source_primary() {
			return getRuleContext(Insert_source_primaryContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(SQLSelectParserParser.LEFT_PAREN, 0); }
		public Column_reference_listContext column_reference_list() {
			return getRuleContext(Column_reference_listContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(SQLSelectParserParser.RIGHT_PAREN, 0); }
		public Snowflake_insertContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_snowflake_insert; }
	}

	public final Snowflake_insertContext snowflake_insert() throws RecognitionException {
		Snowflake_insertContext _localctx = new Snowflake_insertContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_snowflake_insert);
		try {
			setState(539);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(528);
				insert_preamble();
				setState(529);
				table_primary();
				setState(530);
				insert_source_primary();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(532);
				insert_preamble();
				setState(533);
				table_primary();
				setState(534);
				match(LEFT_PAREN);
				setState(535);
				column_reference_list();
				setState(536);
				match(RIGHT_PAREN);
				setState(537);
				insert_source_primary();
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
		enterRule(_localctx, 38, RULE_postgres_insert);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(541);
			snowflake_insert();
			setState(543);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==RETURNING) {
				{
				setState(542);
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
		enterRule(_localctx, 40, RULE_insert_preamble);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(545);
			match(INSERT);
			setState(547);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OVERWRITE) {
				{
				setState(546);
				match(OVERWRITE);
				}
			}

			setState(549);
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
		enterRule(_localctx, 42, RULE_insert_source_primary);
		try {
			setState(554);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(551);
				query_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(552);
				variable_identifier();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(553);
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
		enterRule(_localctx, 44, RULE_update_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(556);
			match(UPDATE);
			setState(557);
			table_primary();
			setState(558);
			match(SET);
			setState(559);
			assignment_expression_list();
			setState(561);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FROM) {
				{
				setState(560);
				from_clause();
				}
			}

			setState(564);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(563);
				where_clause();
				}
			}

			setState(567);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==RETURNING) {
				{
				setState(566);
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
		enterRule(_localctx, 46, RULE_returning);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(569);
			match(RETURNING);
			setState(570);
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
		enterRule(_localctx, 48, RULE_assignment_expression_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(572);
			assignment_expression();
			setState(577);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(573);
				match(COMMA);
				setState(574);
				assignment_expression();
				}
				}
				setState(579);
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
		enterRule(_localctx, 50, RULE_assignment_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(580);
			column_reference();
			setState(581);
			match(EQUAL);
			setState(582);
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
		enterRule(_localctx, 52, RULE_create_table_as_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(584);
			match(CREATE);
			setState(585);
			match(TABLE);
			setState(586);
			match(AS);
			setState(587);
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
		enterRule(_localctx, 54, RULE_query_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(589);
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
		enterRule(_localctx, 56, RULE_intersected_query);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(591);
			unionized_query();
			setState(597);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INTERSECT) {
				{
				{
				setState(592);
				intersect_clause();
				setState(593);
				unionized_query();
				}
				}
				setState(599);
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
		enterRule(_localctx, 58, RULE_intersect_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(600);
			intersect_operator();
			setState(602);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ALL || _la==DISTINCT) {
				{
				setState(601);
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
		enterRule(_localctx, 60, RULE_intersect_operator);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(604);
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
		enterRule(_localctx, 62, RULE_unionized_query);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(606);
			query_primary();
			setState(612);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EXCEPT || _la==UNION) {
				{
				{
				setState(607);
				union_clause();
				setState(608);
				query_primary();
				}
				}
				setState(614);
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
		enterRule(_localctx, 64, RULE_union_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(615);
			union_operator();
			setState(617);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ALL || _la==DISTINCT) {
				{
				setState(616);
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
		enterRule(_localctx, 66, RULE_union_operator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(619);
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
		enterRule(_localctx, 68, RULE_query_primary);
		try {
			setState(624);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LEFT_PAREN:
				enterOuterAlt(_localctx, 1);
				{
				setState(621);
				subquery();
				}
				break;
			case SELECT:
				enterOuterAlt(_localctx, 2);
				{
				setState(622);
				query_specification();
				}
				break;
			case Variable_Identifier:
			case Extended_Variable_Identifier:
			case Mixed_Variable_Identifier:
				enterOuterAlt(_localctx, 3);
				{
				setState(623);
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
		enterRule(_localctx, 70, RULE_subquery);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(626);
			match(LEFT_PAREN);
			setState(627);
			query_expression();
			setState(628);
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
		enterRule(_localctx, 72, RULE_query_specification);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(630);
			match(SELECT);
			setState(632);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==INTO) {
				{
				setState(631);
				into_list();
				}
			}

			setState(635);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ALL || _la==DISTINCT) {
				{
				setState(634);
				set_qualifier();
				}
			}

			setState(637);
			select_list();
			setState(657);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FROM) {
				{
				setState(638);
				from_clause();
				setState(640);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WHERE) {
					{
					setState(639);
					where_clause();
					}
				}

				setState(643);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==GROUP) {
					{
					setState(642);
					groupby_clause();
					}
				}

				setState(646);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HAVING) {
					{
					setState(645);
					having_clause();
					}
				}

				setState(649);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==QUALIFY) {
					{
					setState(648);
					qualify_clause();
					}
				}

				setState(652);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ORDER) {
					{
					setState(651);
					orderby_clause();
					}
				}

				setState(655);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LIMIT) {
					{
					setState(654);
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
		enterRule(_localctx, 74, RULE_into_list);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(659);
			match(INTO);
			setState(660);
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
		enterRule(_localctx, 76, RULE_set_qualifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(662);
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
		enterRule(_localctx, 78, RULE_select_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(664);
			select_item();
			setState(669);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(665);
				match(COMMA);
				setState(666);
				select_item();
				}
				}
				setState(671);
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
		enterRule(_localctx, 80, RULE_select_item);
		try {
			setState(677);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(672);
				value_expression();
				setState(674);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
				case 1:
					{
					setState(673);
					as_clause();
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(676);
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
		enterRule(_localctx, 82, RULE_as_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(680);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS) {
				{
				setState(679);
				match(AS);
				}
			}

			setState(682);
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
		enterRule(_localctx, 84, RULE_select_all_columns);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(684);
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
		enterRule(_localctx, 86, RULE_wildcard_reference);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(688);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Identifier) {
				{
				setState(686);
				((Wildcard_referenceContext)_localctx).tb_name = match(Identifier);
				setState(687);
				match(DOT);
				}
			}

			setState(690);
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
		enterRule(_localctx, 88, RULE_from_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(692);
			match(FROM);
			setState(693);
			table_reference_list();
			setState(695);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 325)) & ~0x3f) == 0 && ((1L << (_la - 325)) & 7L) != 0)) {
				{
				setState(694);
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
		enterRule(_localctx, 90, RULE_join_extension);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(697);
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
		enterRule(_localctx, 92, RULE_table_reference_list);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(699);
			table_primary();
			setState(712);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,37,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(710);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case COMMA:
						{
						{
						setState(700);
						match(COMMA);
						setState(701);
						table_primary();
						}
						}
						break;
					case CROSS:
					case NATURAL:
					case UNION:
						{
						{
						setState(702);
						unqualified_join();
						setState(703);
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
						setState(705);
						qualified_join();
						setState(706);
						((Table_reference_listContext)_localctx).right = table_primary();
						setState(708);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==ON || _la==USING) {
							{
							setState(707);
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
				setState(714);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,37,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
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
		enterRule(_localctx, 94, RULE_join_extension_primary);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(727);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 9016000322274304L) != 0) || _la==COMMA) {
				{
				setState(725);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case COMMA:
					{
					{
					setState(715);
					match(COMMA);
					setState(716);
					table_primary();
					}
					}
					break;
				case CROSS:
				case NATURAL:
				case UNION:
					{
					{
					setState(717);
					unqualified_join();
					setState(718);
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
					setState(720);
					qualified_join();
					setState(721);
					((Join_extension_primaryContext)_localctx).right = table_primary();
					setState(723);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==ON || _la==USING) {
						{
						setState(722);
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
				setState(729);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(731);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 325)) & ~0x3f) == 0 && ((1L << (_la - 325)) & 7L) != 0)) {
				{
				setState(730);
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
		enterRule(_localctx, 96, RULE_table_primary);
		try {
			setState(749);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(733);
				table_or_query_name();
				setState(735);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
				case 1:
					{
					setState(734);
					relation_as_clause();
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(737);
				variable_identifier();
				setState(738);
				as_clause();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(740);
				jinja_identifier();
				setState(742);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,43,_ctx) ) {
				case 1:
					{
					setState(741);
					relation_as_clause();
					}
					break;
				}
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(744);
				values_statement_primary();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(745);
				subquery();
				setState(747);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,44,_ctx) ) {
				case 1:
					{
					setState(746);
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
		enterRule(_localctx, 98, RULE_relation_as_clause);
		try {
			setState(755);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(751);
				match(AS);
				setState(752);
				alias_identifier();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(753);
				if (!(isAllowedImplicitAlias(_input.LT(1).getText()))) throw new FailedPredicateException(this, "isAllowedImplicitAlias(_input.LT(1).getText())");
				setState(754);
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
		enterRule(_localctx, 100, RULE_tuple_primary);
		try {
			setState(762);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(757);
				table_or_query_name();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(758);
				variable_identifier();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(759);
				jinja_identifier();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(760);
				values_statement_primary();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(761);
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
		enterRule(_localctx, 102, RULE_table_or_query_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(764);
			identifier();
			setState(770);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,49,_ctx) ) {
			case 1:
				{
				setState(765);
				match(DOT);
				setState(768);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case NUMBER:
				case Numeric_Identifier:
					{
					setState(766);
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
					setState(767);
					identifier();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			}
			setState(777);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
			case 1:
				{
				setState(772);
				match(DOT);
				setState(775);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case NUMBER:
				case Numeric_Identifier:
					{
					setState(773);
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
					setState(774);
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
		enterRule(_localctx, 104, RULE_unqualified_join);
		int _la;
		try {
			setState(788);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CROSS:
				enterOuterAlt(_localctx, 1);
				{
				setState(779);
				match(CROSS);
				setState(780);
				match(JOIN);
				}
				break;
			case UNION:
				enterOuterAlt(_localctx, 2);
				{
				setState(781);
				match(UNION);
				setState(782);
				match(JOIN);
				}
				break;
			case NATURAL:
				enterOuterAlt(_localctx, 3);
				{
				setState(783);
				match(NATURAL);
				setState(785);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 8796638347264L) != 0)) {
					{
					setState(784);
					((Unqualified_joinContext)_localctx).t = join_type();
					}
				}

				setState(787);
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
		enterRule(_localctx, 106, RULE_qualified_join);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(791);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 8796638347264L) != 0)) {
				{
				setState(790);
				((Qualified_joinContext)_localctx).t = join_type();
				}
			}

			setState(793);
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
		enterRule(_localctx, 108, RULE_join_type);
		int _la;
		try {
			setState(800);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INNER:
				enterOuterAlt(_localctx, 1);
				{
				setState(795);
				match(INNER);
				}
				break;
			case FULL:
			case LEFT:
			case RIGHT:
				enterOuterAlt(_localctx, 2);
				{
				setState(796);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 8796629958656L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(798);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OUTER) {
					{
					setState(797);
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
		enterRule(_localctx, 110, RULE_join_specification);
		try {
			setState(804);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ON:
				enterOuterAlt(_localctx, 1);
				{
				setState(802);
				join_condition();
				}
				break;
			case USING:
				enterOuterAlt(_localctx, 2);
				{
				setState(803);
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
		enterRule(_localctx, 112, RULE_join_condition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(806);
			match(ON);
			setState(807);
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
		enterRule(_localctx, 114, RULE_named_columns_join);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(809);
			using_term();
			setState(810);
			match(LEFT_PAREN);
			setState(811);
			((Named_columns_joinContext)_localctx).f = column_reference_list();
			setState(812);
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
		enterRule(_localctx, 116, RULE_using_term);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(814);
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
		enterRule(_localctx, 118, RULE_column_reference_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(816);
			column_reference();
			setState(821);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(817);
				match(COMMA);
				setState(818);
				column_reference();
				}
				}
				setState(823);
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
		enterRule(_localctx, 120, RULE_column_reference);
		try {
			setState(834);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(827);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,59,_ctx) ) {
				case 1:
					{
					setState(824);
					((Column_referenceContext)_localctx).tb_name = identifier();
					setState(825);
					match(DOT);
					}
					break;
				}
				setState(829);
				((Column_referenceContext)_localctx).name = identifier();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(830);
				((Column_referenceContext)_localctx).tb_name = identifier();
				setState(831);
				match(DOT);
				setState(832);
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
		enterRule(_localctx, 122, RULE_column_primary);
		try {
			setState(847);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,62,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(839);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,61,_ctx) ) {
				case 1:
					{
					setState(836);
					((Column_primaryContext)_localctx).tb_name = identifier();
					setState(837);
					match(DOT);
					}
					break;
				}
				setState(841);
				((Column_primaryContext)_localctx).name = identifier();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(842);
				((Column_primaryContext)_localctx).tb_name = identifier();
				setState(843);
				match(DOT);
				setState(844);
				((Column_primaryContext)_localctx).substitution = variable_identifier();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(846);
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
		public Predicand_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicand_primary; }
	}

	public final Predicand_primaryContext predicand_primary() throws RecognitionException {
		Predicand_primaryContext _localctx = new Predicand_primaryContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_predicand_primary);
		try {
			setState(858);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,63,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(849);
				value_expression_primary();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(850);
				string_value_expression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(851);
				sign();
				setState(852);
				numeric_primary();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(854);
				trim_function();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(855);
				null_literal();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(856);
				variable_identifier();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(857);
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
		enterRule(_localctx, 126, RULE_value_expression_primary);
		int _la;
		try {
			setState(870);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,66,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(860);
				parenthesized_value_expression();
				setState(863);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CAST_OPERATOR) {
					{
					setState(861);
					match(CAST_OPERATOR);
					setState(862);
					data_type();
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(865);
				nonparenthesized_value_expression_primary();
				setState(868);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CAST_OPERATOR) {
					{
					setState(866);
					match(CAST_OPERATOR);
					setState(867);
					data_type();
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
		enterRule(_localctx, 128, RULE_parenthesized_value_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(872);
			match(LEFT_PAREN);
			setState(873);
			value_expression();
			setState(874);
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
		enterRule(_localctx, 130, RULE_nonparenthesized_value_expression_primary);
		try {
			setState(884);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,67,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(876);
				unsigned_literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(877);
				column_reference();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(878);
				aggregate_function();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(879);
				case_expression();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(880);
				cast_function_expression();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(881);
				routine_invocation();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(882);
				window_over_partition_expression();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(883);
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
		enterRule(_localctx, 132, RULE_predicand_subquery);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(886);
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
		enterRule(_localctx, 134, RULE_aggregate_function);
		int _la;
		try {
			setState(904);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,70,_ctx) ) {
			case 1:
				_localctx = new Count_all_aggregateContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(888);
				match(COUNT);
				setState(889);
				match(LEFT_PAREN);
				setState(890);
				wildcard_reference();
				setState(891);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				_localctx = new General_set_functionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(895);
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
					setState(893);
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
					setState(894);
					set_qualifier_type();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(897);
				match(LEFT_PAREN);
				setState(899);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ALL || _la==DISTINCT) {
					{
					setState(898);
					set_qualifier();
					}
				}

				setState(901);
				value_expression();
				setState(902);
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
		enterRule(_localctx, 136, RULE_set_function_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(906);
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
		enterRule(_localctx, 138, RULE_set_qualifier_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(908);
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
		enterRule(_localctx, 140, RULE_case_expression);
		int _la;
		try {
			setState(925);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,73,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(910);
				match(CASE);
				setState(911);
				value_expression();
				setState(912);
				when_value_list();
				setState(914);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSE) {
					{
					setState(913);
					else_clause();
					}
				}

				setState(916);
				match(END);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(918);
				match(CASE);
				setState(919);
				when_clause_list();
				setState(921);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSE) {
					{
					setState(920);
					else_clause();
					}
				}

				setState(923);
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
		enterRule(_localctx, 142, RULE_when_clause_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(928); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(927);
				searched_when_clause();
				}
				}
				setState(930); 
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
		enterRule(_localctx, 144, RULE_searched_when_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(932);
			match(WHEN);
			setState(933);
			((Searched_when_clauseContext)_localctx).c = value_expression();
			setState(934);
			match(THEN);
			setState(935);
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
		enterRule(_localctx, 146, RULE_when_value_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(938); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(937);
				when_value_clause();
				}
				}
				setState(940); 
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
		enterRule(_localctx, 148, RULE_when_value_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(942);
			match(WHEN);
			setState(943);
			((When_value_clauseContext)_localctx).c = value_expression();
			setState(944);
			match(THEN);
			setState(945);
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
		enterRule(_localctx, 150, RULE_else_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(947);
			match(ELSE);
			setState(948);
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
		enterRule(_localctx, 152, RULE_case_result);
		try {
			setState(952);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,76,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(950);
				value_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
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
	public static class Null_literalContext extends ParserRuleContext {
		public TerminalNode NULL() { return getToken(SQLSelectParserParser.NULL, 0); }
		public Null_literalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_null_literal; }
	}

	public final Null_literalContext null_literal() throws RecognitionException {
		Null_literalContext _localctx = new Null_literalContext(_ctx, getState());
		enterRule(_localctx, 154, RULE_null_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(954);
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
		enterRule(_localctx, 156, RULE_cast_function_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(956);
			cast_function_name();
			setState(957);
			match(LEFT_PAREN);
			setState(958);
			value_expression();
			setState(959);
			match(AS);
			setState(960);
			data_type();
			setState(961);
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
		enterRule(_localctx, 158, RULE_cast_function_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(963);
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
		enterRule(_localctx, 160, RULE_window_over_partition_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(965);
			window_function();
			setState(966);
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
		enterRule(_localctx, 162, RULE_window_function);
		int _la;
		try {
			setState(985);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,80,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(968);
				set_function_type();
				setState(969);
				match(LEFT_PAREN);
				setState(971);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -848827245298417264L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -288230377259008257L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -2097169L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -162133984631980033L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 4473745326079L) != 0) || ((((_la - 324)) & ~0x3f) == 0 && ((1L << (_la - 324)) & 9903L) != 0)) {
					{
					setState(970);
					sql_argument_list();
					}
				}

				setState(973);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(975);
				item_select_function();
				setState(976);
				match(LEFT_PAREN);
				setState(977);
				sql_argument_list();
				setState(978);
				match(RIGHT_PAREN);
				setState(983);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 4398047690752L) != 0)) {
					{
					setState(980);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==FROM) {
						{
						setState(979);
						select_direction();
						}
					}

					setState(982);
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
		enterRule(_localctx, 164, RULE_over_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(987);
			match(OVER);
			setState(988);
			match(LEFT_PAREN);
			{
			setState(990);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PARTITION) {
				{
				setState(989);
				partition_by_clause();
				}
			}

			setState(993);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ORDER) {
				{
				setState(992);
				orderby_clause();
				}
			}

			setState(996);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==RANGE || _la==ROWS) {
				{
				setState(995);
				bracket_frame_clause();
				}
			}

			}
			setState(998);
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
		enterRule(_localctx, 166, RULE_partition_by_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1000);
			match(PARTITION);
			setState(1001);
			match(BY);
			setState(1002);
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
		enterRule(_localctx, 168, RULE_bracket_frame_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1004);
			rows_or_range();
			setState(1005);
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
		enterRule(_localctx, 170, RULE_rows_or_range);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1007);
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
		enterRule(_localctx, 172, RULE_bracket_frame_definition);
		try {
			setState(1012);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BETWEEN:
				enterOuterAlt(_localctx, 1);
				{
				setState(1009);
				between_frame_definition();
				}
				break;
			case UNBOUNDED:
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(1010);
				preceding_frame_edge();
				}
				break;
			case CURRENT:
				enterOuterAlt(_localctx, 3);
				{
				setState(1011);
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
		enterRule(_localctx, 174, RULE_between_frame_definition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1014);
			match(BETWEEN);
			setState(1015);
			frame_edge();
			setState(1016);
			match(AND);
			setState(1017);
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
		enterRule(_localctx, 176, RULE_frame_edge);
		try {
			setState(1022);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,85,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1019);
				preceding_frame_edge();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1020);
				following_frame_edge();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1021);
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
		enterRule(_localctx, 178, RULE_preceding_frame_edge);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1024);
			bracket_constraint();
			setState(1025);
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
		enterRule(_localctx, 180, RULE_following_frame_edge);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1027);
			bracket_constraint();
			setState(1028);
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
		enterRule(_localctx, 182, RULE_current_row_edge);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1030);
			match(CURRENT);
			setState(1031);
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
		enterRule(_localctx, 184, RULE_bracket_constraint);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1033);
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
		enterRule(_localctx, 186, RULE_item_select_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1035);
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
		enterRule(_localctx, 188, RULE_select_direction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1037);
			match(FROM);
			setState(1038);
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
		enterRule(_localctx, 190, RULE_null_handling);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1040);
			_la = _input.LA(1);
			if ( !(_la==IGNORE || _la==RESPECT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(1041);
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
		enterRule(_localctx, 192, RULE_value_expression);
		try {
			setState(1047);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,86,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1043);
				common_value_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1044);
				row_value_expression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1045);
				variable_identifier();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1046);
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
		enterRule(_localctx, 194, RULE_common_value_expression);
		try {
			setState(1052);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,87,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1049);
				additive_expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1050);
				string_value_expression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1051);
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
		enterRule(_localctx, 196, RULE_additive_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(1054);
			((Additive_expressionContext)_localctx).left = multiplicative_expression();
			setState(1059);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PLUS || _la==MINUS) {
				{
				{
				setState(1055);
				_la = _input.LA(1);
				if ( !(_la==PLUS || _la==MINUS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1056);
				((Additive_expressionContext)_localctx).right = multiplicative_expression();
				}
				}
				setState(1061);
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
		enterRule(_localctx, 198, RULE_multiplicative_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1062);
			((Multiplicative_expressionContext)_localctx).left = factor();
			setState(1067);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 289)) & ~0x3f) == 0 && ((1L << (_la - 289)) & 7L) != 0)) {
				{
				{
				setState(1063);
				_la = _input.LA(1);
				if ( !(((((_la - 289)) & ~0x3f) == 0 && ((1L << (_la - 289)) & 7L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1064);
				((Multiplicative_expressionContext)_localctx).right = factor();
				}
				}
				setState(1069);
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
		enterRule(_localctx, 200, RULE_factor);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1071);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PLUS || _la==MINUS) {
				{
				setState(1070);
				sign();
				}
			}

			setState(1073);
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
		enterRule(_localctx, 202, RULE_numeric_primary);
		try {
			setState(1077);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,91,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1075);
				value_expression_primary();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1076);
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
		enterRule(_localctx, 204, RULE_sign);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1079);
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
		enterRule(_localctx, 206, RULE_extract_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1081);
			match(EXTRACT);
			setState(1082);
			match(LEFT_PAREN);
			setState(1083);
			((Extract_expressionContext)_localctx).extract_field_string = extract_field();
			setState(1084);
			match(FROM);
			setState(1085);
			extract_source();
			setState(1086);
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
		enterRule(_localctx, 208, RULE_extract_field);
		try {
			setState(1091);
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
				setState(1088);
				primary_datetime_field();
				}
				break;
			case TIMEZONE:
			case TIMEZONE_HOUR:
			case TIMEZONE_MINUTE:
				enterOuterAlt(_localctx, 2);
				{
				setState(1089);
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
				setState(1090);
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
		enterRule(_localctx, 210, RULE_time_zone_field);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1093);
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
		enterRule(_localctx, 212, RULE_extract_source);
		try {
			setState(1097);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,93,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1095);
				column_reference();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1096);
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
		enterRule(_localctx, 214, RULE_string_value_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1099);
			character_primary();
			setState(1104);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CONCATENATION_OPERATOR) {
				{
				{
				setState(1100);
				match(CONCATENATION_OPERATOR);
				setState(1101);
				character_primary();
				}
				}
				setState(1106);
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
		enterRule(_localctx, 216, RULE_character_primary);
		try {
			setState(1109);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,95,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1107);
				value_expression_primary();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1108);
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
		enterRule(_localctx, 218, RULE_trim_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1111);
			trim_function_name();
			setState(1112);
			match(LEFT_PAREN);
			setState(1113);
			trim_operands();
			setState(1114);
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
		enterRule(_localctx, 220, RULE_trim_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1116);
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
		enterRule(_localctx, 222, RULE_trim_operands);
		int _la;
		try {
			setState(1132);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,99,_ctx) ) {
			case 1:
				_localctx = new Mysql_trim_operandsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(1125);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,98,_ctx) ) {
				case 1:
					{
					setState(1119);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1125900175278144L) != 0)) {
						{
						setState(1118);
						trim_specification();
						}
					}

					setState(1122);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -848827271068221040L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -288230377259008257L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -2097169L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -162133984631980033L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 4467302875135L) != 0) || ((((_la - 324)) & ~0x3f) == 0 && ((1L << (_la - 324)) & 9889L) != 0)) {
						{
						setState(1121);
						((Mysql_trim_operandsContext)_localctx).trim_character = string_value_expression();
						}
					}

					setState(1124);
					match(FROM);
					}
					break;
				}
				setState(1127);
				((Mysql_trim_operandsContext)_localctx).trim_source = value_expression();
				}
				break;
			case 2:
				_localctx = new Other_trim_operandsContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(1128);
				((Other_trim_operandsContext)_localctx).trim_source = value_expression();
				setState(1129);
				match(COMMA);
				setState(1130);
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
		enterRule(_localctx, 224, RULE_trim_specification);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1134);
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
		enterRule(_localctx, 226, RULE_boolean_value_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1136);
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
		enterRule(_localctx, 228, RULE_or_predicate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1138);
			and_predicate();
			setState(1143);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OR) {
				{
				{
				setState(1139);
				match(OR);
				setState(1140);
				and_predicate();
				}
				}
				setState(1145);
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
		enterRule(_localctx, 230, RULE_and_predicate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1146);
			negative_predicate();
			setState(1151);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AND) {
				{
				{
				setState(1147);
				match(AND);
				setState(1148);
				negative_predicate();
				}
				}
				setState(1153);
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
		enterRule(_localctx, 232, RULE_negative_predicate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1155);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(1154);
				not();
				}
			}

			setState(1157);
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
		enterRule(_localctx, 234, RULE_parenthetical_predicate);
		int _la;
		try {
			setState(1167);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,104,_ctx) ) {
			case 1:
				_localctx = new Paren_clauseContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(1159);
				match(LEFT_PAREN);
				setState(1160);
				boolean_value_expression();
				setState(1161);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				_localctx = new Basic_predicate_clauseContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(1163);
				boolean_primary();
				setState(1165);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IS) {
					{
					setState(1164);
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
		enterRule(_localctx, 236, RULE_boolean_primary);
		try {
			setState(1171);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,105,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1169);
				predicate();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1170);
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
		enterRule(_localctx, 238, RULE_predicate);
		try {
			setState(1180);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,106,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1173);
				comparison_predicate();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1174);
				between_predicate();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1175);
				in_predicate();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1176);
				like_any_predicate();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1177);
				null_predicate();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1178);
				exists_predicate();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1179);
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
		enterRule(_localctx, 240, RULE_substitution_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1182);
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
		enterRule(_localctx, 242, RULE_row_value_expression);
		try {
			setState(1186);
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
			case Identifier:
			case Scientific_Numeric_Literal:
			case Double_Quoted_Numeric_Identifier:
			case Dollar_Sign_Identifier:
			case Character_String_Literal:
				enterOuterAlt(_localctx, 1);
				{
				setState(1184);
				nonparenthesized_value_expression_primary();
				}
				break;
			case NULL:
				enterOuterAlt(_localctx, 2);
				{
				setState(1185);
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
		enterRule(_localctx, 244, RULE_row_value_predicand);
		try {
			setState(1191);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,108,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1188);
				nonparenthesized_value_expression_primary();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1189);
				common_value_expression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1190);
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
		enterRule(_localctx, 246, RULE_where_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1193);
			match(WHERE);
			setState(1194);
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
		enterRule(_localctx, 248, RULE_search_condition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1196);
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
		enterRule(_localctx, 250, RULE_orderby_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1198);
			match(ORDER);
			setState(1199);
			match(BY);
			setState(1200);
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
		enterRule(_localctx, 252, RULE_sort_specifier_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1202);
			sort_specifier();
			setState(1207);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1203);
				match(COMMA);
				setState(1204);
				sort_specifier();
				}
				}
				setState(1209);
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
		enterRule(_localctx, 254, RULE_sort_specifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1210);
			((Sort_specifierContext)_localctx).key = row_value_predicand();
			setState(1212);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ASC || _la==DESC) {
				{
				setState(1211);
				((Sort_specifierContext)_localctx).order = order_specification();
				}
			}

			setState(1215);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NULLS) {
				{
				setState(1214);
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
		enterRule(_localctx, 256, RULE_order_specification);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1217);
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
		enterRule(_localctx, 258, RULE_null_ordering);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1219);
			match(NULLS);
			setState(1220);
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
		enterRule(_localctx, 260, RULE_null_first_last);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1222);
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
		enterRule(_localctx, 262, RULE_limit_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1224);
			match(LIMIT);
			setState(1225);
			((Limit_clauseContext)_localctx).e = additive_expression();
			setState(1228);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OFFSET) {
				{
				setState(1226);
				match(OFFSET);
				setState(1227);
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
		enterRule(_localctx, 264, RULE_groupby_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1230);
			match(GROUP);
			setState(1231);
			match(BY);
			setState(1234);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,113,_ctx) ) {
			case 1:
				{
				setState(1232);
				grouping_element_list();
				}
				break;
			case 2:
				{
				setState(1233);
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
		enterRule(_localctx, 266, RULE_grouping_element_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1236);
			grouping_element();
			setState(1241);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1237);
				match(COMMA);
				setState(1238);
				grouping_element();
				}
				}
				setState(1243);
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
		enterRule(_localctx, 268, RULE_grouping_element);
		try {
			setState(1248);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,115,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1244);
				rollup_list();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1245);
				cube_list();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1246);
				empty_grouping_set();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1247);
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
		enterRule(_localctx, 270, RULE_ordinary_grouping_set_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1250);
			ordinary_grouping_set();
			setState(1255);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1251);
				match(COMMA);
				setState(1252);
				ordinary_grouping_set();
				}
				}
				setState(1257);
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
		enterRule(_localctx, 272, RULE_ordinary_grouping_set);
		try {
			setState(1263);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,117,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1258);
				row_value_predicand();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1259);
				match(LEFT_PAREN);
				setState(1260);
				row_value_predicand_list();
				setState(1261);
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
		enterRule(_localctx, 274, RULE_rollup_list);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1265);
			match(ROLLUP);
			setState(1266);
			match(LEFT_PAREN);
			setState(1267);
			((Rollup_listContext)_localctx).c = ordinary_grouping_set_list();
			setState(1268);
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
		enterRule(_localctx, 276, RULE_cube_list);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1270);
			match(CUBE);
			setState(1271);
			match(LEFT_PAREN);
			setState(1272);
			((Cube_listContext)_localctx).c = ordinary_grouping_set_list();
			setState(1273);
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
		enterRule(_localctx, 278, RULE_empty_grouping_set);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1275);
			match(LEFT_PAREN);
			setState(1276);
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
		enterRule(_localctx, 280, RULE_having_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1278);
			match(HAVING);
			setState(1279);
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
		enterRule(_localctx, 282, RULE_qualify_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1281);
			match(QUALIFY);
			setState(1282);
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
		enterRule(_localctx, 284, RULE_row_value_predicand_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1284);
			row_value_predicand();
			setState(1289);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1285);
				match(COMMA);
				setState(1286);
				row_value_predicand();
				}
				}
				setState(1291);
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
		enterRule(_localctx, 286, RULE_comparison_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1292);
			((Comparison_predicateContext)_localctx).left = row_value_predicand();
			setState(1293);
			((Comparison_predicateContext)_localctx).c = comparison_operator();
			setState(1294);
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
		enterRule(_localctx, 288, RULE_comparison_operator);
		int _la;
		try {
			setState(1302);
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
				setState(1296);
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
				setState(1298);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(1297);
					not();
					}
				}

				setState(1300);
				relative_comp_op();
				}
				break;
			case Similar_To:
			case Not_Similar_To:
			case Similar_To_Case_Insensitive:
			case Not_Similar_To_Case_Insensitive:
				enterOuterAlt(_localctx, 3);
				{
				setState(1301);
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
		enterRule(_localctx, 290, RULE_relative_comp_op);
		try {
			setState(1310);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LIKE:
				enterOuterAlt(_localctx, 1);
				{
				setState(1304);
				match(LIKE);
				}
				break;
			case ILIKE:
				enterOuterAlt(_localctx, 2);
				{
				setState(1305);
				match(ILIKE);
				}
				break;
			case SIMILAR:
				enterOuterAlt(_localctx, 3);
				{
				setState(1306);
				match(SIMILAR);
				setState(1307);
				match(TO);
				}
				break;
			case REGEXP:
				enterOuterAlt(_localctx, 4);
				{
				setState(1308);
				match(REGEXP);
				}
				break;
			case RLIKE:
				enterOuterAlt(_localctx, 5);
				{
				setState(1309);
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
		enterRule(_localctx, 292, RULE_similarity_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1312);
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
		enterRule(_localctx, 294, RULE_comp_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1314);
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
		enterRule(_localctx, 296, RULE_between_predicate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1316);
			row_value_predicand();
			setState(1318);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(1317);
				not();
				}
			}

			setState(1320);
			match(BETWEEN);
			setState(1322);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ASYMMETRIC || _la==SYMMETRIC) {
				{
				setState(1321);
				symmetry();
				}
			}

			setState(1324);
			((Between_predicateContext)_localctx).begin = row_value_predicand();
			setState(1325);
			match(AND);
			setState(1326);
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
		enterRule(_localctx, 298, RULE_symmetry);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1328);
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
		enterRule(_localctx, 300, RULE_in_predicate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1330);
			row_value_predicand();
			setState(1332);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(1331);
				not();
				}
			}

			setState(1334);
			match(IN);
			setState(1335);
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
		enterRule(_localctx, 302, RULE_like_any_predicate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1337);
			row_value_predicand();
			setState(1339);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(1338);
				not();
				}
			}

			setState(1341);
			like_any_operator();
			setState(1342);
			in_predicate_value();
			setState(1344);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,126,_ctx) ) {
			case 1:
				{
				setState(1343);
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
		enterRule(_localctx, 304, RULE_like_any_operator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1346);
			_la = _input.LA(1);
			if ( !(_la==ILIKE || _la==LIKE) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(1347);
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
		enterRule(_localctx, 306, RULE_in_predicate_value);
		try {
			setState(1355);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,127,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1349);
				subquery();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1350);
				match(LEFT_PAREN);
				setState(1351);
				in_value_list();
				setState(1352);
				match(RIGHT_PAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1354);
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
		enterRule(_localctx, 308, RULE_in_value_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1357);
			row_value_expression();
			setState(1362);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1358);
				match(COMMA);
				setState(1359);
				row_value_expression();
				}
				}
				setState(1364);
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
		enterRule(_localctx, 310, RULE_escape_character_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1365);
			match(ESCAPE);
			setState(1366);
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
		enterRule(_localctx, 312, RULE_values_statement_primary);
		try {
			setState(1371);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,129,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1368);
				fully_defined_values_statement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1369);
				aliased_values_statement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1370);
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
		enterRule(_localctx, 314, RULE_fully_defined_values_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1373);
			values_statement();
			setState(1374);
			as_clause();
			setState(1375);
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
		enterRule(_localctx, 316, RULE_aliased_values_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1377);
			values_statement();
			setState(1378);
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
		enterRule(_localctx, 318, RULE_values_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1380);
			match(LEFT_PAREN);
			setState(1381);
			match(VALUES);
			setState(1382);
			values_matrix();
			setState(1383);
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
		enterRule(_localctx, 320, RULE_values_matrix);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1385);
			values_row();
			setState(1390);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1386);
				match(COMMA);
				setState(1387);
				values_row();
				}
				}
				setState(1392);
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
		enterRule(_localctx, 322, RULE_values_row);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1393);
			match(LEFT_PAREN);
			setState(1394);
			in_value_list();
			setState(1395);
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
		enterRule(_localctx, 324, RULE_values_aliases);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(1397);
			match(LEFT_PAREN);
			setState(1398);
			values_aliases_list();
			setState(1399);
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
		enterRule(_localctx, 326, RULE_values_aliases_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1401);
			alias_identifier();
			setState(1406);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1402);
				match(COMMA);
				setState(1403);
				alias_identifier();
				}
				}
				setState(1408);
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
		enterRule(_localctx, 328, RULE_insert_values_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1409);
			match(VALUES);
			setState(1410);
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
		enterRule(_localctx, 330, RULE_exists_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1412);
			exists_operator();
			setState(1413);
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
		enterRule(_localctx, 332, RULE_exists_operator);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1415);
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
		enterRule(_localctx, 334, RULE_exists_predicate_value);
		try {
			setState(1419);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LEFT_PAREN:
				enterOuterAlt(_localctx, 1);
				{
				setState(1417);
				subquery();
				}
				break;
			case Variable_Identifier:
			case Extended_Variable_Identifier:
			case Mixed_Variable_Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(1418);
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
		enterRule(_localctx, 336, RULE_null_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1421);
			row_value_predicand();
			setState(1422);
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
		enterRule(_localctx, 338, RULE_is_null_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1424);
			match(IS);
			setState(1426);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(1425);
				((Is_null_clauseContext)_localctx).n = match(NOT);
				}
			}

			setState(1428);
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
		enterRule(_localctx, 340, RULE_is_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1430);
			match(IS);
			setState(1432);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(1431);
				not();
				}
			}

			setState(1434);
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
		enterRule(_localctx, 342, RULE_truth_value);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1436);
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
		enterRule(_localctx, 344, RULE_not);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1438);
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
		enterRule(_localctx, 346, RULE_quantified_comparison_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1440);
			((Quantified_comparison_predicateContext)_localctx).l = additive_expression();
			setState(1441);
			((Quantified_comparison_predicateContext)_localctx).c = comp_op();
			setState(1442);
			((Quantified_comparison_predicateContext)_localctx).q = quantifier();
			setState(1443);
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
		enterRule(_localctx, 348, RULE_quantifier);
		try {
			setState(1447);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ALL:
				enterOuterAlt(_localctx, 1);
				{
				setState(1445);
				all();
				}
				break;
			case ANY:
			case SOME:
				enterOuterAlt(_localctx, 2);
				{
				setState(1446);
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
		enterRule(_localctx, 350, RULE_all);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1449);
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
		enterRule(_localctx, 352, RULE_some);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1451);
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
		enterRule(_localctx, 354, RULE_unique_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1453);
			match(UNIQUE);
			setState(1454);
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
		enterRule(_localctx, 356, RULE_primary_datetime_field);
		try {
			setState(1458);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DAY:
			case HOUR:
			case MINUTE:
			case MONTH:
			case YEAR:
				enterOuterAlt(_localctx, 1);
				{
				setState(1456);
				non_second_primary_datetime_field();
				}
				break;
			case SECOND:
				enterOuterAlt(_localctx, 2);
				{
				setState(1457);
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
		enterRule(_localctx, 358, RULE_non_second_primary_datetime_field);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1460);
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
		enterRule(_localctx, 360, RULE_extended_datetime_field);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1462);
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
		enterRule(_localctx, 362, RULE_routine_invocation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1464);
			function_name();
			setState(1465);
			match(LEFT_PAREN);
			setState(1467);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -848827245298417264L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -288230377259008257L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -2097169L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -162133984631980033L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 4473745326079L) != 0) || ((((_la - 324)) & ~0x3f) == 0 && ((1L << (_la - 324)) & 9903L) != 0)) {
				{
				setState(1466);
				sql_argument_list();
				}
			}

			setState(1469);
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
		enterRule(_localctx, 364, RULE_function_name);
		int _la;
		try {
			setState(1477);
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
				setState(1471);
				identifier();
				setState(1474);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOT) {
					{
					setState(1472);
					match(DOT);
					setState(1473);
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
				setState(1476);
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
		enterRule(_localctx, 366, RULE_function_names_for_reserved_words);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1479);
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
		enterRule(_localctx, 368, RULE_sql_argument_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1481);
			value_expression();
			setState(1486);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1482);
				match(COMMA);
				setState(1483);
				value_expression();
				}
				}
				setState(1488);
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
		enterRule(_localctx, 370, RULE_identifier);
		try {
			setState(1494);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(1489);
				simple_identifier();
				}
				break;
			case Bracket_Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(1490);
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
				setState(1491);
				nonreserved_keywords();
				}
				break;
			case Double_Quoted_Numeric_Identifier:
				enterOuterAlt(_localctx, 4);
				{
				setState(1492);
				snowflake_quoted_numeric_identifier();
				}
				break;
			case Dollar_Sign_Identifier:
				enterOuterAlt(_localctx, 5);
				{
				setState(1493);
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
		enterRule(_localctx, 372, RULE_alias_identifier);
		try {
			setState(1501);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(1496);
				simple_identifier();
				}
				break;
			case Bracket_Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(1497);
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
				setState(1498);
				nonreserved_keywords();
				}
				break;
			case NUMBER:
			case Numeric_Identifier:
				enterOuterAlt(_localctx, 4);
				{
				setState(1499);
				simple_numeric_identifier();
				}
				break;
			case Double_Quoted_Numeric_Identifier:
				enterOuterAlt(_localctx, 5);
				{
				setState(1500);
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
		enterRule(_localctx, 374, RULE_variable_identifier);
		try {
			setState(1505);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Variable_Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(1503);
				simple_variable_identifier();
				}
				break;
			case Extended_Variable_Identifier:
			case Mixed_Variable_Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(1504);
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
		enterRule(_localctx, 376, RULE_simple_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1507);
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
		enterRule(_localctx, 378, RULE_logical_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1509);
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
		enterRule(_localctx, 380, RULE_simple_variable_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1511);
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
		enterRule(_localctx, 382, RULE_extended_variable_identifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1513);
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
		enterRule(_localctx, 384, RULE_jinja_identifier);
		try {
			setState(1523);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,144,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1515);
				match(JINJA_OPEN);
				setState(1516);
				jinja_function_call();
				setState(1517);
				match(JINJA_CLOSE);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1519);
				match(JINJA_OPEN);
				setState(1520);
				jinja_variable_access();
				setState(1521);
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
		enterRule(_localctx, 386, RULE_jinja_function_call);
		int _la;
		try {
			setState(1543);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,147,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1525);
				((Jinja_function_callContext)_localctx).func_name = identifier();
				setState(1526);
				if (!(isJinjaPrimaryFunction((((Jinja_function_callContext)_localctx).func_name!=null?_input.getText(((Jinja_function_callContext)_localctx).func_name.start,((Jinja_function_callContext)_localctx).func_name.stop):null)))) throw new FailedPredicateException(this, "isJinjaPrimaryFunction($func_name.text)");
				setState(1527);
				match(LEFT_PAREN);
				setState(1529);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 20)) & ~0x3f) == 0 && ((1L << (_la - 20)) & -5630315556929535L) != 0) || ((((_la - 84)) & ~0x3f) == 0 && ((1L << (_la - 84)) & -422491638793265L) != 0) || ((((_la - 148)) & ~0x3f) == 0 && ((1L << (_la - 148)) & -2305843009213693955L) != 0) || ((((_la - 212)) & ~0x3f) == 0 && ((1L << (_la - 212)) & 288230221528694783L) != 0) || ((((_la - 298)) & ~0x3f) == 0 && ((1L << (_la - 298)) & 655049621505L) != 0)) {
					{
					setState(1528);
					jinja_arg_list();
					}
				}

				setState(1531);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1533);
				((Jinja_function_callContext)_localctx).object_name = identifier();
				setState(1534);
				match(DOT);
				setState(1535);
				((Jinja_function_callContext)_localctx).method_name = identifier();
				setState(1536);
				if (!(isJinjaObjectMethod((((Jinja_function_callContext)_localctx).object_name!=null?_input.getText(((Jinja_function_callContext)_localctx).object_name.start,((Jinja_function_callContext)_localctx).object_name.stop):null), (((Jinja_function_callContext)_localctx).method_name!=null?_input.getText(((Jinja_function_callContext)_localctx).method_name.start,((Jinja_function_callContext)_localctx).method_name.stop):null)))) throw new FailedPredicateException(this, "isJinjaObjectMethod($object_name.text, $method_name.text)");
				setState(1537);
				match(LEFT_PAREN);
				setState(1539);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 20)) & ~0x3f) == 0 && ((1L << (_la - 20)) & -5630315556929535L) != 0) || ((((_la - 84)) & ~0x3f) == 0 && ((1L << (_la - 84)) & -422491638793265L) != 0) || ((((_la - 148)) & ~0x3f) == 0 && ((1L << (_la - 148)) & -2305843009213693955L) != 0) || ((((_la - 212)) & ~0x3f) == 0 && ((1L << (_la - 212)) & 288230221528694783L) != 0) || ((((_la - 298)) & ~0x3f) == 0 && ((1L << (_la - 298)) & 655049621505L) != 0)) {
					{
					setState(1538);
					jinja_arg_list();
					}
				}

				setState(1541);
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
		enterRule(_localctx, 388, RULE_jinja_arg_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1545);
			jinja_arg();
			setState(1550);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1546);
				match(COMMA);
				setState(1547);
				jinja_arg();
				}
				}
				setState(1552);
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
		enterRule(_localctx, 390, RULE_jinja_arg);
		try {
			setState(1563);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,149,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1553);
				match(Character_String_Literal);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1554);
				match(NUMBER);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1555);
				((Jinja_argContext)_localctx).kw_name = identifier();
				setState(1556);
				match(EQUAL);
				setState(1557);
				match(Character_String_Literal);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1559);
				((Jinja_argContext)_localctx).kw_name = identifier();
				setState(1560);
				match(EQUAL);
				setState(1561);
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
		enterRule(_localctx, 392, RULE_jinja_variable_access);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1565);
			jinja_name();
			setState(1570);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DOT) {
				{
				{
				setState(1566);
				match(DOT);
				setState(1567);
				jinja_name();
				}
				}
				setState(1572);
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
		enterRule(_localctx, 394, RULE_jinja_name);
		try {
			setState(1575);
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
				setState(1573);
				identifier();
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(1574);
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
		enterRule(_localctx, 396, RULE_simple_numeric_identifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1577);
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
		enterRule(_localctx, 398, RULE_snowflake_quoted_numeric_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1579);
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
		enterRule(_localctx, 400, RULE_snowflake_dollar_function_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1581);
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
		public TerminalNode ESCAPE() { return getToken(SQLSelectParserParser.ESCAPE, 0); }
		public Nonreserved_keywordsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nonreserved_keywords; }
	}

	public final Nonreserved_keywordsContext nonreserved_keywords() throws RecognitionException {
		Nonreserved_keywordsContext _localctx = new Nonreserved_keywordsContext(_ctx, getState());
		enterRule(_localctx, 402, RULE_nonreserved_keywords);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1583);
			_la = _input.LA(1);
			if ( !(((((_la - 20)) & ~0x3f) == 0 && ((1L << (_la - 20)) & -5630315556929535L) != 0) || ((((_la - 84)) & ~0x3f) == 0 && ((1L << (_la - 84)) & -422491638793265L) != 0) || ((((_la - 148)) & ~0x3f) == 0 && ((1L << (_la - 148)) & -2305843009213693955L) != 0) || ((((_la - 212)) & ~0x3f) == 0 && ((1L << (_la - 212)) & 288230221528694783L) != 0)) ) {
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
		enterRule(_localctx, 404, RULE_signed_numeric_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1586);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PLUS || _la==MINUS) {
				{
				setState(1585);
				sign();
				}
			}

			setState(1588);
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
		enterRule(_localctx, 406, RULE_unsigned_literal);
		try {
			setState(1592);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DOT:
			case NUMBER:
			case Scientific_Numeric_Literal:
				enterOuterAlt(_localctx, 1);
				{
				setState(1590);
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
				setState(1591);
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
		enterRule(_localctx, 408, RULE_unsigned_numeric_literal);
		try {
			setState(1596);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,154,_ctx) ) {
			case 1:
				_localctx = new Ordinal_numberContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(1594);
				match(NUMBER);
				}
				break;
			case 2:
				_localctx = new Real_numberContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(1595);
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
		enterRule(_localctx, 410, RULE_real_number_def);
		int _la;
		try {
			setState(1614);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,158,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1598);
				match(NUMBER);
				setState(1599);
				match(DOT);
				setState(1601);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,155,_ctx) ) {
				case 1:
					{
					setState(1600);
					match(NUMBER);
					}
					break;
				}
				setState(1604);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==EXPONEN) {
					{
					setState(1603);
					exponent();
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1606);
				match(DOT);
				setState(1607);
				match(NUMBER);
				setState(1609);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==EXPONEN) {
					{
					setState(1608);
					exponent();
					}
				}

				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1611);
				match(NUMBER);
				setState(1612);
				exponent();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1613);
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
		enterRule(_localctx, 412, RULE_exponent);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1616);
			match(EXPONEN);
			setState(1617);
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
		enterRule(_localctx, 414, RULE_general_literal);
		try {
			setState(1622);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Character_String_Literal:
				enterOuterAlt(_localctx, 1);
				{
				setState(1619);
				character_literal();
				}
				break;
			case DATE:
			case TIME:
			case TIMESTAMP:
				enterOuterAlt(_localctx, 2);
				{
				setState(1620);
				datetime_literal();
				}
				break;
			case FALSE:
			case TRUE:
			case UNKNOWN:
				enterOuterAlt(_localctx, 3);
				{
				setState(1621);
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
		enterRule(_localctx, 416, RULE_character_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1624);
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
		enterRule(_localctx, 418, RULE_datetime_literal);
		try {
			setState(1629);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TIMESTAMP:
				enterOuterAlt(_localctx, 1);
				{
				setState(1626);
				timestamp_literal();
				}
				break;
			case TIME:
				enterOuterAlt(_localctx, 2);
				{
				setState(1627);
				time_literal();
				}
				break;
			case DATE:
				enterOuterAlt(_localctx, 3);
				{
				setState(1628);
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
		enterRule(_localctx, 420, RULE_time_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1631);
			match(TIME);
			setState(1632);
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
		enterRule(_localctx, 422, RULE_timestamp_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1634);
			match(TIMESTAMP);
			setState(1635);
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
		enterRule(_localctx, 424, RULE_date_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1637);
			match(DATE);
			setState(1638);
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
		enterRule(_localctx, 426, RULE_boolean_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1640);
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
		enterRule(_localctx, 428, RULE_data_type);
		try {
			setState(1645);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,161,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1642);
				variable_size_data_type();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1643);
				precision_scale_data_type();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1644);
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
		enterRule(_localctx, 430, RULE_variable_size_data_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1647);
			variable_data_type_name();
			setState(1649);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LEFT_PAREN) {
				{
				setState(1648);
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
		enterRule(_localctx, 432, RULE_variable_data_type_name);
		try {
			setState(1685);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,163,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1651);
				match(CHARACTER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1652);
				match(CHAR);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1653);
				match(CHARACTER);
				setState(1654);
				match(VARYING);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1655);
				match(CHAR);
				setState(1656);
				match(VARYING);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1657);
				match(VARCHAR);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1658);
				match(VARCHAR2);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1659);
				match(NATIONAL);
				setState(1660);
				match(CHARACTER);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1661);
				match(NATIONAL);
				setState(1662);
				match(CHAR);
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(1663);
				match(NCHAR);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(1664);
				match(NATIONAL);
				setState(1665);
				match(CHARACTER);
				setState(1666);
				match(VARYING);
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(1667);
				match(NATIONAL);
				setState(1668);
				match(CHAR);
				setState(1669);
				match(VARYING);
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(1670);
				match(NCHAR);
				setState(1671);
				match(VARYING);
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(1672);
				match(NVARCHAR);
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(1673);
				match(BLOB);
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				setState(1674);
				match(BYTEA);
				}
				break;
			case 16:
				enterOuterAlt(_localctx, 16);
				{
				setState(1675);
				match(BIT);
				}
				break;
			case 17:
				enterOuterAlt(_localctx, 17);
				{
				setState(1676);
				match(VARBIT);
				}
				break;
			case 18:
				enterOuterAlt(_localctx, 18);
				{
				setState(1677);
				match(BIT);
				setState(1678);
				match(VARYING);
				}
				break;
			case 19:
				enterOuterAlt(_localctx, 19);
				{
				setState(1679);
				match(BINARY);
				}
				break;
			case 20:
				enterOuterAlt(_localctx, 20);
				{
				setState(1680);
				match(BINARY);
				setState(1681);
				match(VARYING);
				}
				break;
			case 21:
				enterOuterAlt(_localctx, 21);
				{
				setState(1682);
				match(VARBINARY);
				}
				break;
			case 22:
				enterOuterAlt(_localctx, 22);
				{
				setState(1683);
				match(INTERVAL);
				}
				break;
			case 23:
				enterOuterAlt(_localctx, 23);
				{
				setState(1684);
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
		enterRule(_localctx, 434, RULE_type_length);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1687);
			match(LEFT_PAREN);
			setState(1688);
			match(NUMBER);
			setState(1689);
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
		enterRule(_localctx, 436, RULE_precision_scale_data_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1691);
			precision_data_type_name();
			setState(1693);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LEFT_PAREN) {
				{
				setState(1692);
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
		enterRule(_localctx, 438, RULE_precision_data_type_name);
		try {
			setState(1706);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,165,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1695);
				match(NUMERIC);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1696);
				match(NUMBER);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1697);
				match(DECIMAL);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1698);
				match(DEC);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1699);
				match(FLOAT);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1700);
				match(DOUBLE);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1701);
				match(DOUBLE);
				setState(1702);
				match(PRECISION);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1703);
				match(TIMESTAMP_LTZ);
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(1704);
				match(TIMESTAMP_NTZ);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(1705);
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
		enterRule(_localctx, 440, RULE_precision_param);
		try {
			setState(1716);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,166,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1708);
				match(LEFT_PAREN);
				setState(1709);
				((Precision_paramContext)_localctx).precision = match(NUMBER);
				setState(1710);
				match(RIGHT_PAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1711);
				match(LEFT_PAREN);
				setState(1712);
				((Precision_paramContext)_localctx).precision = match(NUMBER);
				setState(1713);
				match(COMMA);
				setState(1714);
				((Precision_paramContext)_localctx).scale = match(NUMBER);
				setState(1715);
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
		enterRule(_localctx, 442, RULE_static_data_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1718);
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
		enterRule(_localctx, 444, RULE_static_data_type_name);
		try {
			setState(1781);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,167,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1720);
				match(TEXT);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1721);
				match(NAME);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1722);
				match(INET4);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1723);
				match(INET);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1724);
				match(CIDR);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1725);
				match(STRUCT);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1726);
				match(UNION);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1727);
				match(VARIANT);
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(1728);
				match(OBJECT);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(1729);
				match(JSON);
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(1730);
				match(JSONB);
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(1731);
				match(OID);
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(1732);
				match(XID);
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(1733);
				match(UUID);
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				setState(1734);
				match(PG_LSN);
				}
				break;
			case 16:
				enterOuterAlt(_localctx, 16);
				{
				setState(1735);
				match(PG_NODE_TREE);
				}
				break;
			case 17:
				enterOuterAlt(_localctx, 17);
				{
				setState(1736);
				match(REGPROC);
				}
				break;
			case 18:
				enterOuterAlt(_localctx, 18);
				{
				setState(1737);
				match(MACADDR);
				}
				break;
			case 19:
				enterOuterAlt(_localctx, 19);
				{
				setState(1738);
				match(INT1);
				}
				break;
			case 20:
				enterOuterAlt(_localctx, 20);
				{
				setState(1739);
				match(TINYINT);
				}
				break;
			case 21:
				enterOuterAlt(_localctx, 21);
				{
				setState(1740);
				match(INT2);
				}
				break;
			case 22:
				enterOuterAlt(_localctx, 22);
				{
				setState(1741);
				match(SMALLINT);
				}
				break;
			case 23:
				enterOuterAlt(_localctx, 23);
				{
				setState(1742);
				match(INT4);
				}
				break;
			case 24:
				enterOuterAlt(_localctx, 24);
				{
				setState(1743);
				match(INT);
				}
				break;
			case 25:
				enterOuterAlt(_localctx, 25);
				{
				setState(1744);
				match(INTEGER);
				}
				break;
			case 26:
				enterOuterAlt(_localctx, 26);
				{
				setState(1745);
				match(INT8);
				}
				break;
			case 27:
				enterOuterAlt(_localctx, 27);
				{
				setState(1746);
				match(BIGINT);
				}
				break;
			case 28:
				enterOuterAlt(_localctx, 28);
				{
				setState(1747);
				match(BIGSERIAL);
				}
				break;
			case 29:
				enterOuterAlt(_localctx, 29);
				{
				setState(1748);
				match(SMALLSERIAL);
				}
				break;
			case 30:
				enterOuterAlt(_localctx, 30);
				{
				setState(1749);
				match(SERIAL);
				}
				break;
			case 31:
				enterOuterAlt(_localctx, 31);
				{
				setState(1750);
				match(MONEY);
				}
				break;
			case 32:
				enterOuterAlt(_localctx, 32);
				{
				setState(1751);
				match(NUMBER_TYPE);
				}
				break;
			case 33:
				enterOuterAlt(_localctx, 33);
				{
				setState(1752);
				match(FLOAT4);
				}
				break;
			case 34:
				enterOuterAlt(_localctx, 34);
				{
				setState(1753);
				match(REAL);
				}
				break;
			case 35:
				enterOuterAlt(_localctx, 35);
				{
				setState(1754);
				match(FLOAT8);
				}
				break;
			case 36:
				enterOuterAlt(_localctx, 36);
				{
				setState(1755);
				match(BOOLEAN);
				}
				break;
			case 37:
				enterOuterAlt(_localctx, 37);
				{
				setState(1756);
				match(BOOL);
				}
				break;
			case 38:
				enterOuterAlt(_localctx, 38);
				{
				setState(1757);
				match(DATE);
				}
				break;
			case 39:
				enterOuterAlt(_localctx, 39);
				{
				setState(1758);
				match(DATETIME);
				}
				break;
			case 40:
				enterOuterAlt(_localctx, 40);
				{
				setState(1759);
				match(TIME);
				}
				break;
			case 41:
				enterOuterAlt(_localctx, 41);
				{
				setState(1760);
				match(TIME);
				setState(1761);
				match(WITH);
				setState(1762);
				match(TIME);
				setState(1763);
				match(ZONE);
				}
				break;
			case 42:
				enterOuterAlt(_localctx, 42);
				{
				setState(1764);
				match(TIMETZ);
				}
				break;
			case 43:
				enterOuterAlt(_localctx, 43);
				{
				setState(1765);
				match(TIMESTAMP_LTZ);
				}
				break;
			case 44:
				enterOuterAlt(_localctx, 44);
				{
				setState(1766);
				match(TIMESTAMP_NTZ);
				}
				break;
			case 45:
				enterOuterAlt(_localctx, 45);
				{
				setState(1767);
				match(TIMESTAMP_TZ);
				}
				break;
			case 46:
				enterOuterAlt(_localctx, 46);
				{
				setState(1768);
				match(TIMESTAMP);
				}
				break;
			case 47:
				enterOuterAlt(_localctx, 47);
				{
				setState(1769);
				match(TIMESTAMP);
				setState(1770);
				match(WITH);
				setState(1771);
				match(TIME);
				setState(1772);
				match(ZONE);
				}
				break;
			case 48:
				enterOuterAlt(_localctx, 48);
				{
				setState(1773);
				match(TIMESTAMP);
				setState(1774);
				match(WITHOUT);
				setState(1775);
				match(TIME);
				setState(1776);
				match(ZONE);
				}
				break;
			case 49:
				enterOuterAlt(_localctx, 49);
				{
				setState(1777);
				match(TIMESTAMPTZ);
				}
				break;
			case 50:
				enterOuterAlt(_localctx, 50);
				{
				setState(1778);
				match(ABSTIME);
				}
				break;
			case 51:
				enterOuterAlt(_localctx, 51);
				{
				setState(1779);
				match(ARRAY);
				}
				break;
			case 52:
				enterOuterAlt(_localctx, 52);
				{
				setState(1780);
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
		enterRule(_localctx, 446, RULE_puml_constant_identifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1783);
			_la = _input.LA(1);
			if ( !(((((_la - 299)) & ~0x3f) == 0 && ((1L << (_la - 299)) & 33554431L) != 0)) ) {
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
		case 49:
			return relation_as_clause_sempred((Relation_as_clauseContext)_localctx, predIndex);
		case 193:
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
	private boolean jinja_function_call_sempred(Jinja_function_callContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return isJinjaPrimaryFunction((((Jinja_function_callContext)_localctx).func_name!=null?_input.getText(((Jinja_function_callContext)_localctx).func_name.start,((Jinja_function_callContext)_localctx).func_name.stop):null));
		case 2:
			return isJinjaObjectMethod((((Jinja_function_callContext)_localctx).object_name!=null?_input.getText(((Jinja_function_callContext)_localctx).object_name.start,((Jinja_function_callContext)_localctx).object_name.stop):null), (((Jinja_function_callContext)_localctx).method_name!=null?_input.getText(((Jinja_function_callContext)_localctx).method_name.start,((Jinja_function_callContext)_localctx).method_name.stop):null));
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u0156\u06fa\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
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
		"\u00dd\u0002\u00de\u0007\u00de\u0002\u00df\u0007\u00df\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0003\u0000\u01c4\b\u0000\u0001\u0000\u0003\u0000\u01c7"+
		"\b\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\b\u0001\b\u0003\b\u01e2\b\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001"+
		"\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f"+
		"\u0001\f\u0001\f\u0005\f\u01f3\b\f\n\f\f\f\u01f6\t\f\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0003\r\u0200\b\r\u0001\u000e"+
		"\u0001\u000e\u0003\u000e\u0204\b\u000e\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u020d\b\u0010"+
		"\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0003\u0012\u021c\b\u0012\u0001\u0013\u0001\u0013\u0003\u0013"+
		"\u0220\b\u0013\u0001\u0014\u0001\u0014\u0003\u0014\u0224\b\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0003\u0015\u022b"+
		"\b\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0003"+
		"\u0016\u0232\b\u0016\u0001\u0016\u0003\u0016\u0235\b\u0016\u0001\u0016"+
		"\u0003\u0016\u0238\b\u0016\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0018"+
		"\u0001\u0018\u0001\u0018\u0005\u0018\u0240\b\u0018\n\u0018\f\u0018\u0243"+
		"\t\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u001a\u0001"+
		"\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001b\u0001\u001b\u0001"+
		"\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0005\u001c\u0254\b\u001c\n"+
		"\u001c\f\u001c\u0257\t\u001c\u0001\u001d\u0001\u001d\u0003\u001d\u025b"+
		"\b\u001d\u0001\u001e\u0001\u001e\u0001\u001f\u0001\u001f\u0001\u001f\u0001"+
		"\u001f\u0005\u001f\u0263\b\u001f\n\u001f\f\u001f\u0266\t\u001f\u0001 "+
		"\u0001 \u0003 \u026a\b \u0001!\u0001!\u0001\"\u0001\"\u0001\"\u0003\""+
		"\u0271\b\"\u0001#\u0001#\u0001#\u0001#\u0001$\u0001$\u0003$\u0279\b$\u0001"+
		"$\u0003$\u027c\b$\u0001$\u0001$\u0001$\u0003$\u0281\b$\u0001$\u0003$\u0284"+
		"\b$\u0001$\u0003$\u0287\b$\u0001$\u0003$\u028a\b$\u0001$\u0003$\u028d"+
		"\b$\u0001$\u0003$\u0290\b$\u0003$\u0292\b$\u0001%\u0001%\u0001%\u0001"+
		"&\u0001&\u0001\'\u0001\'\u0001\'\u0005\'\u029c\b\'\n\'\f\'\u029f\t\'\u0001"+
		"(\u0001(\u0003(\u02a3\b(\u0001(\u0003(\u02a6\b(\u0001)\u0003)\u02a9\b"+
		")\u0001)\u0001)\u0001*\u0001*\u0001+\u0001+\u0003+\u02b1\b+\u0001+\u0001"+
		"+\u0001,\u0001,\u0001,\u0003,\u02b8\b,\u0001-\u0001-\u0001.\u0001.\u0001"+
		".\u0001.\u0001.\u0001.\u0001.\u0001.\u0001.\u0003.\u02c5\b.\u0005.\u02c7"+
		"\b.\n.\f.\u02ca\t.\u0001/\u0001/\u0001/\u0001/\u0001/\u0001/\u0001/\u0001"+
		"/\u0003/\u02d4\b/\u0005/\u02d6\b/\n/\f/\u02d9\t/\u0001/\u0003/\u02dc\b"+
		"/\u00010\u00010\u00030\u02e0\b0\u00010\u00010\u00010\u00010\u00010\u0003"+
		"0\u02e7\b0\u00010\u00010\u00010\u00030\u02ec\b0\u00030\u02ee\b0\u0001"+
		"1\u00011\u00011\u00011\u00031\u02f4\b1\u00012\u00012\u00012\u00012\u0001"+
		"2\u00032\u02fb\b2\u00013\u00013\u00013\u00013\u00033\u0301\b3\u00033\u0303"+
		"\b3\u00013\u00013\u00013\u00033\u0308\b3\u00033\u030a\b3\u00014\u0001"+
		"4\u00014\u00014\u00014\u00014\u00034\u0312\b4\u00014\u00034\u0315\b4\u0001"+
		"5\u00035\u0318\b5\u00015\u00015\u00016\u00016\u00016\u00036\u031f\b6\u0003"+
		"6\u0321\b6\u00017\u00017\u00037\u0325\b7\u00018\u00018\u00018\u00019\u0001"+
		"9\u00019\u00019\u00019\u0001:\u0001:\u0001;\u0001;\u0001;\u0005;\u0334"+
		"\b;\n;\f;\u0337\t;\u0001<\u0001<\u0001<\u0003<\u033c\b<\u0001<\u0001<"+
		"\u0001<\u0001<\u0001<\u0003<\u0343\b<\u0001=\u0001=\u0001=\u0003=\u0348"+
		"\b=\u0001=\u0001=\u0001=\u0001=\u0001=\u0001=\u0003=\u0350\b=\u0001>\u0001"+
		">\u0001>\u0001>\u0001>\u0001>\u0001>\u0001>\u0001>\u0003>\u035b\b>\u0001"+
		"?\u0001?\u0001?\u0003?\u0360\b?\u0001?\u0001?\u0001?\u0003?\u0365\b?\u0003"+
		"?\u0367\b?\u0001@\u0001@\u0001@\u0001@\u0001A\u0001A\u0001A\u0001A\u0001"+
		"A\u0001A\u0001A\u0001A\u0003A\u0375\bA\u0001B\u0001B\u0001C\u0001C\u0001"+
		"C\u0001C\u0001C\u0001C\u0001C\u0003C\u0380\bC\u0001C\u0001C\u0003C\u0384"+
		"\bC\u0001C\u0001C\u0001C\u0003C\u0389\bC\u0001D\u0001D\u0001E\u0001E\u0001"+
		"F\u0001F\u0001F\u0001F\u0003F\u0393\bF\u0001F\u0001F\u0001F\u0001F\u0001"+
		"F\u0003F\u039a\bF\u0001F\u0001F\u0003F\u039e\bF\u0001G\u0004G\u03a1\b"+
		"G\u000bG\fG\u03a2\u0001H\u0001H\u0001H\u0001H\u0001H\u0001I\u0004I\u03ab"+
		"\bI\u000bI\fI\u03ac\u0001J\u0001J\u0001J\u0001J\u0001J\u0001K\u0001K\u0001"+
		"K\u0001L\u0001L\u0003L\u03b9\bL\u0001M\u0001M\u0001N\u0001N\u0001N\u0001"+
		"N\u0001N\u0001N\u0001N\u0001O\u0001O\u0001P\u0001P\u0001P\u0001Q\u0001"+
		"Q\u0001Q\u0003Q\u03cc\bQ\u0001Q\u0001Q\u0001Q\u0001Q\u0001Q\u0001Q\u0001"+
		"Q\u0003Q\u03d5\bQ\u0001Q\u0003Q\u03d8\bQ\u0003Q\u03da\bQ\u0001R\u0001"+
		"R\u0001R\u0003R\u03df\bR\u0001R\u0003R\u03e2\bR\u0001R\u0003R\u03e5\b"+
		"R\u0001R\u0001R\u0001S\u0001S\u0001S\u0001S\u0001T\u0001T\u0001T\u0001"+
		"U\u0001U\u0001V\u0001V\u0001V\u0003V\u03f5\bV\u0001W\u0001W\u0001W\u0001"+
		"W\u0001W\u0001X\u0001X\u0001X\u0003X\u03ff\bX\u0001Y\u0001Y\u0001Y\u0001"+
		"Z\u0001Z\u0001Z\u0001[\u0001[\u0001[\u0001\\\u0001\\\u0001]\u0001]\u0001"+
		"^\u0001^\u0001^\u0001_\u0001_\u0001_\u0001`\u0001`\u0001`\u0001`\u0003"+
		"`\u0418\b`\u0001a\u0001a\u0001a\u0003a\u041d\ba\u0001b\u0001b\u0001b\u0005"+
		"b\u0422\bb\nb\fb\u0425\tb\u0001c\u0001c\u0001c\u0005c\u042a\bc\nc\fc\u042d"+
		"\tc\u0001d\u0003d\u0430\bd\u0001d\u0001d\u0001e\u0001e\u0003e\u0436\b"+
		"e\u0001f\u0001f\u0001g\u0001g\u0001g\u0001g\u0001g\u0001g\u0001g\u0001"+
		"h\u0001h\u0001h\u0003h\u0444\bh\u0001i\u0001i\u0001j\u0001j\u0003j\u044a"+
		"\bj\u0001k\u0001k\u0001k\u0005k\u044f\bk\nk\fk\u0452\tk\u0001l\u0001l"+
		"\u0003l\u0456\bl\u0001m\u0001m\u0001m\u0001m\u0001m\u0001n\u0001n\u0001"+
		"o\u0003o\u0460\bo\u0001o\u0003o\u0463\bo\u0001o\u0003o\u0466\bo\u0001"+
		"o\u0001o\u0001o\u0001o\u0001o\u0003o\u046d\bo\u0001p\u0001p\u0001q\u0001"+
		"q\u0001r\u0001r\u0001r\u0005r\u0476\br\nr\fr\u0479\tr\u0001s\u0001s\u0001"+
		"s\u0005s\u047e\bs\ns\fs\u0481\ts\u0001t\u0003t\u0484\bt\u0001t\u0001t"+
		"\u0001u\u0001u\u0001u\u0001u\u0001u\u0001u\u0003u\u048e\bu\u0003u\u0490"+
		"\bu\u0001v\u0001v\u0003v\u0494\bv\u0001w\u0001w\u0001w\u0001w\u0001w\u0001"+
		"w\u0001w\u0003w\u049d\bw\u0001x\u0001x\u0001y\u0001y\u0003y\u04a3\by\u0001"+
		"z\u0001z\u0001z\u0003z\u04a8\bz\u0001{\u0001{\u0001{\u0001|\u0001|\u0001"+
		"}\u0001}\u0001}\u0001}\u0001~\u0001~\u0001~\u0005~\u04b6\b~\n~\f~\u04b9"+
		"\t~\u0001\u007f\u0001\u007f\u0003\u007f\u04bd\b\u007f\u0001\u007f\u0003"+
		"\u007f\u04c0\b\u007f\u0001\u0080\u0001\u0080\u0001\u0081\u0001\u0081\u0001"+
		"\u0081\u0001\u0082\u0001\u0082\u0001\u0083\u0001\u0083\u0001\u0083\u0001"+
		"\u0083\u0003\u0083\u04cd\b\u0083\u0001\u0084\u0001\u0084\u0001\u0084\u0001"+
		"\u0084\u0003\u0084\u04d3\b\u0084\u0001\u0085\u0001\u0085\u0001\u0085\u0005"+
		"\u0085\u04d8\b\u0085\n\u0085\f\u0085\u04db\t\u0085\u0001\u0086\u0001\u0086"+
		"\u0001\u0086\u0001\u0086\u0003\u0086\u04e1\b\u0086\u0001\u0087\u0001\u0087"+
		"\u0001\u0087\u0005\u0087\u04e6\b\u0087\n\u0087\f\u0087\u04e9\t\u0087\u0001"+
		"\u0088\u0001\u0088\u0001\u0088\u0001\u0088\u0001\u0088\u0003\u0088\u04f0"+
		"\b\u0088\u0001\u0089\u0001\u0089\u0001\u0089\u0001\u0089\u0001\u0089\u0001"+
		"\u008a\u0001\u008a\u0001\u008a\u0001\u008a\u0001\u008a\u0001\u008b\u0001"+
		"\u008b\u0001\u008b\u0001\u008c\u0001\u008c\u0001\u008c\u0001\u008d\u0001"+
		"\u008d\u0001\u008d\u0001\u008e\u0001\u008e\u0001\u008e\u0005\u008e\u0508"+
		"\b\u008e\n\u008e\f\u008e\u050b\t\u008e\u0001\u008f\u0001\u008f\u0001\u008f"+
		"\u0001\u008f\u0001\u0090\u0001\u0090\u0003\u0090\u0513\b\u0090\u0001\u0090"+
		"\u0001\u0090\u0003\u0090\u0517\b\u0090\u0001\u0091\u0001\u0091\u0001\u0091"+
		"\u0001\u0091\u0001\u0091\u0001\u0091\u0003\u0091\u051f\b\u0091\u0001\u0092"+
		"\u0001\u0092\u0001\u0093\u0001\u0093\u0001\u0094\u0001\u0094\u0003\u0094"+
		"\u0527\b\u0094\u0001\u0094\u0001\u0094\u0003\u0094\u052b\b\u0094\u0001"+
		"\u0094\u0001\u0094\u0001\u0094\u0001\u0094\u0001\u0095\u0001\u0095\u0001"+
		"\u0096\u0001\u0096\u0003\u0096\u0535\b\u0096\u0001\u0096\u0001\u0096\u0001"+
		"\u0096\u0001\u0097\u0001\u0097\u0003\u0097\u053c\b\u0097\u0001\u0097\u0001"+
		"\u0097\u0001\u0097\u0003\u0097\u0541\b\u0097\u0001\u0098\u0001\u0098\u0001"+
		"\u0098\u0001\u0099\u0001\u0099\u0001\u0099\u0001\u0099\u0001\u0099\u0001"+
		"\u0099\u0003\u0099\u054c\b\u0099\u0001\u009a\u0001\u009a\u0001\u009a\u0005"+
		"\u009a\u0551\b\u009a\n\u009a\f\u009a\u0554\t\u009a\u0001\u009b\u0001\u009b"+
		"\u0001\u009b\u0001\u009c\u0001\u009c\u0001\u009c\u0003\u009c\u055c\b\u009c"+
		"\u0001\u009d\u0001\u009d\u0001\u009d\u0001\u009d\u0001\u009e\u0001\u009e"+
		"\u0001\u009e\u0001\u009f\u0001\u009f\u0001\u009f\u0001\u009f\u0001\u009f"+
		"\u0001\u00a0\u0001\u00a0\u0001\u00a0\u0005\u00a0\u056d\b\u00a0\n\u00a0"+
		"\f\u00a0\u0570\t\u00a0\u0001\u00a1\u0001\u00a1\u0001\u00a1\u0001\u00a1"+
		"\u0001\u00a2\u0001\u00a2\u0001\u00a2\u0001\u00a2\u0001\u00a3\u0001\u00a3"+
		"\u0001\u00a3\u0005\u00a3\u057d\b\u00a3\n\u00a3\f\u00a3\u0580\t\u00a3\u0001"+
		"\u00a4\u0001\u00a4\u0001\u00a4\u0001\u00a5\u0001\u00a5\u0001\u00a5\u0001"+
		"\u00a6\u0001\u00a6\u0001\u00a7\u0001\u00a7\u0003\u00a7\u058c\b\u00a7\u0001"+
		"\u00a8\u0001\u00a8\u0001\u00a8\u0001\u00a9\u0001\u00a9\u0003\u00a9\u0593"+
		"\b\u00a9\u0001\u00a9\u0001\u00a9\u0001\u00aa\u0001\u00aa\u0003\u00aa\u0599"+
		"\b\u00aa\u0001\u00aa\u0001\u00aa\u0001\u00ab\u0001\u00ab\u0001\u00ac\u0001"+
		"\u00ac\u0001\u00ad\u0001\u00ad\u0001\u00ad\u0001\u00ad\u0001\u00ad\u0001"+
		"\u00ae\u0001\u00ae\u0003\u00ae\u05a8\b\u00ae\u0001\u00af\u0001\u00af\u0001"+
		"\u00b0\u0001\u00b0\u0001\u00b1\u0001\u00b1\u0001\u00b1\u0001\u00b2\u0001"+
		"\u00b2\u0003\u00b2\u05b3\b\u00b2\u0001\u00b3\u0001\u00b3\u0001\u00b4\u0001"+
		"\u00b4\u0001\u00b5\u0001\u00b5\u0001\u00b5\u0003\u00b5\u05bc\b\u00b5\u0001"+
		"\u00b5\u0001\u00b5\u0001\u00b6\u0001\u00b6\u0001\u00b6\u0003\u00b6\u05c3"+
		"\b\u00b6\u0001\u00b6\u0003\u00b6\u05c6\b\u00b6\u0001\u00b7\u0001\u00b7"+
		"\u0001\u00b8\u0001\u00b8\u0001\u00b8\u0005\u00b8\u05cd\b\u00b8\n\u00b8"+
		"\f\u00b8\u05d0\t\u00b8\u0001\u00b9\u0001\u00b9\u0001\u00b9\u0001\u00b9"+
		"\u0001\u00b9\u0003\u00b9\u05d7\b\u00b9\u0001\u00ba\u0001\u00ba\u0001\u00ba"+
		"\u0001\u00ba\u0001\u00ba\u0003\u00ba\u05de\b\u00ba\u0001\u00bb\u0001\u00bb"+
		"\u0003\u00bb\u05e2\b\u00bb\u0001\u00bc\u0001\u00bc\u0001\u00bd\u0001\u00bd"+
		"\u0001\u00be\u0001\u00be\u0001\u00bf\u0001\u00bf\u0001\u00c0\u0001\u00c0"+
		"\u0001\u00c0\u0001\u00c0\u0001\u00c0\u0001\u00c0\u0001\u00c0\u0001\u00c0"+
		"\u0003\u00c0\u05f4\b\u00c0\u0001\u00c1\u0001\u00c1\u0001\u00c1\u0001\u00c1"+
		"\u0003\u00c1\u05fa\b\u00c1\u0001\u00c1\u0001\u00c1\u0001\u00c1\u0001\u00c1"+
		"\u0001\u00c1\u0001\u00c1\u0001\u00c1\u0001\u00c1\u0003\u00c1\u0604\b\u00c1"+
		"\u0001\u00c1\u0001\u00c1\u0003\u00c1\u0608\b\u00c1\u0001\u00c2\u0001\u00c2"+
		"\u0001\u00c2\u0005\u00c2\u060d\b\u00c2\n\u00c2\f\u00c2\u0610\t\u00c2\u0001"+
		"\u00c3\u0001\u00c3\u0001\u00c3\u0001\u00c3\u0001\u00c3\u0001\u00c3\u0001"+
		"\u00c3\u0001\u00c3\u0001\u00c3\u0001\u00c3\u0003\u00c3\u061c\b\u00c3\u0001"+
		"\u00c4\u0001\u00c4\u0001\u00c4\u0005\u00c4\u0621\b\u00c4\n\u00c4\f\u00c4"+
		"\u0624\t\u00c4\u0001\u00c5\u0001\u00c5\u0003\u00c5\u0628\b\u00c5\u0001"+
		"\u00c6\u0001\u00c6\u0001\u00c7\u0001\u00c7\u0001\u00c8\u0001\u00c8\u0001"+
		"\u00c9\u0001\u00c9\u0001\u00ca\u0003\u00ca\u0633\b\u00ca\u0001\u00ca\u0001"+
		"\u00ca\u0001\u00cb\u0001\u00cb\u0003\u00cb\u0639\b\u00cb\u0001\u00cc\u0001"+
		"\u00cc\u0003\u00cc\u063d\b\u00cc\u0001\u00cd\u0001\u00cd\u0001\u00cd\u0003"+
		"\u00cd\u0642\b\u00cd\u0001\u00cd\u0003\u00cd\u0645\b\u00cd\u0001\u00cd"+
		"\u0001\u00cd\u0001\u00cd\u0003\u00cd\u064a\b\u00cd\u0001\u00cd\u0001\u00cd"+
		"\u0001\u00cd\u0003\u00cd\u064f\b\u00cd\u0001\u00ce\u0001\u00ce\u0001\u00ce"+
		"\u0001\u00cf\u0001\u00cf\u0001\u00cf\u0003\u00cf\u0657\b\u00cf\u0001\u00d0"+
		"\u0001\u00d0\u0001\u00d1\u0001\u00d1\u0001\u00d1\u0003\u00d1\u065e\b\u00d1"+
		"\u0001\u00d2\u0001\u00d2\u0001\u00d2\u0001\u00d3\u0001\u00d3\u0001\u00d3"+
		"\u0001\u00d4\u0001\u00d4\u0001\u00d4\u0001\u00d5\u0001\u00d5\u0001\u00d6"+
		"\u0001\u00d6\u0001\u00d6\u0003\u00d6\u066e\b\u00d6\u0001\u00d7\u0001\u00d7"+
		"\u0003\u00d7\u0672\b\u00d7\u0001\u00d8\u0001\u00d8\u0001\u00d8\u0001\u00d8"+
		"\u0001\u00d8\u0001\u00d8\u0001\u00d8\u0001\u00d8\u0001\u00d8\u0001\u00d8"+
		"\u0001\u00d8\u0001\u00d8\u0001\u00d8\u0001\u00d8\u0001\u00d8\u0001\u00d8"+
		"\u0001\u00d8\u0001\u00d8\u0001\u00d8\u0001\u00d8\u0001\u00d8\u0001\u00d8"+
		"\u0001\u00d8\u0001\u00d8\u0001\u00d8\u0001\u00d8\u0001\u00d8\u0001\u00d8"+
		"\u0001\u00d8\u0001\u00d8\u0001\u00d8\u0001\u00d8\u0001\u00d8\u0001\u00d8"+
		"\u0003\u00d8\u0696\b\u00d8\u0001\u00d9\u0001\u00d9\u0001\u00d9\u0001\u00d9"+
		"\u0001\u00da\u0001\u00da\u0003\u00da\u069e\b\u00da\u0001\u00db\u0001\u00db"+
		"\u0001\u00db\u0001\u00db\u0001\u00db\u0001\u00db\u0001\u00db\u0001\u00db"+
		"\u0001\u00db\u0001\u00db\u0001\u00db\u0003\u00db\u06ab\b\u00db\u0001\u00dc"+
		"\u0001\u00dc\u0001\u00dc\u0001\u00dc\u0001\u00dc\u0001\u00dc\u0001\u00dc"+
		"\u0001\u00dc\u0003\u00dc\u06b5\b\u00dc\u0001\u00dd\u0001\u00dd\u0001\u00de"+
		"\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de"+
		"\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de"+
		"\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de"+
		"\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de"+
		"\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de"+
		"\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de"+
		"\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de"+
		"\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de"+
		"\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de"+
		"\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de"+
		"\u0003\u00de\u06f6\b\u00de\u0001\u00df\u0001\u00df\u0001\u00df\u0000\u0000"+
		"\u00e0\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018"+
		"\u001a\u001c\u001e \"$&(*,.02468:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080"+
		"\u0082\u0084\u0086\u0088\u008a\u008c\u008e\u0090\u0092\u0094\u0096\u0098"+
		"\u009a\u009c\u009e\u00a0\u00a2\u00a4\u00a6\u00a8\u00aa\u00ac\u00ae\u00b0"+
		"\u00b2\u00b4\u00b6\u00b8\u00ba\u00bc\u00be\u00c0\u00c2\u00c4\u00c6\u00c8"+
		"\u00ca\u00cc\u00ce\u00d0\u00d2\u00d4\u00d6\u00d8\u00da\u00dc\u00de\u00e0"+
		"\u00e2\u00e4\u00e6\u00e8\u00ea\u00ec\u00ee\u00f0\u00f2\u00f4\u00f6\u00f8"+
		"\u00fa\u00fc\u00fe\u0100\u0102\u0104\u0106\u0108\u010a\u010c\u010e\u0110"+
		"\u0112\u0114\u0116\u0118\u011a\u011c\u011e\u0120\u0122\u0124\u0126\u0128"+
		"\u012a\u012c\u012e\u0130\u0132\u0134\u0136\u0138\u013a\u013c\u013e\u0140"+
		"\u0142\u0144\u0146\u0148\u014a\u014c\u014e\u0150\u0152\u0154\u0156\u0158"+
		"\u015a\u015c\u015e\u0160\u0162\u0164\u0166\u0168\u016a\u016c\u016e\u0170"+
		"\u0172\u0174\u0176\u0178\u017a\u017c\u017e\u0180\u0182\u0184\u0186\u0188"+
		"\u018a\u018c\u018e\u0190\u0192\u0194\u0196\u0198\u019a\u019c\u019e\u01a0"+
		"\u01a2\u01a4\u01a6\u01a8\u01aa\u01ac\u01ae\u01b0\u01b2\u01b4\u01b6\u01b8"+
		"\u01ba\u01bc\u01be\u0000\u001d\u0002\u0000\u000e\u000e55\u0002\u0000\u0002"+
		"\u0002\u000b\u000b\u0003\u0000\u0010\u0010\u001d\u001d++\u000e\u0000="+
		"=EFXXddfgkkpptt\u007f\u007f\u0083\u0083\u0089\u008a\u008c\u008c\u0098"+
		"\u0099\u009e\u00cd\u0006\u0000\u0004\u0004..BBRR[[aa\u0002\u0000\b\b4"+
		"4\u0002\u0000~~\u0085\u0085\u0002\u0000\u0095\u0095\u012a\u012a\u0004"+
		"\u0000XXddfgtt\u0002\u0000WWee\u0002\u0000\u0014\u0014**\u0001\u0000\u011f"+
		"\u0120\u0001\u0000\u0121\u0123\u0001\u0000\u008f\u0091\u0003\u0000\u0006"+
		"\u0006\u001c\u001c22\u0002\u0000<<LL\u0001\u0000\u010e\u0111\u0002\u0000"+
		"\u0113\u0113\u0118\u011c\u0002\u0000\u0005\u0005//\u0002\u0000\u0015\u0015"+
		"\u001e\u001e\u0003\u0000\u000f\u000f33\u0096\u0096\u0002\u0000\u0004\u0004"+
		"..\u0004\u0000II^^qr\u009c\u009c\b\u0000@@KKMNPPbcmo}}\u009b\u009b\u0003"+
		"\u0000\u0016\u0016\u001d\u001d++\u0001\u0000\u0146\u0147\u0002\u0000\u012a"+
		"\u012a\u014c\u014c\u0015\u0000\u0014\u0014#$**,,55::<EGGIWZ]_ceehsuy{"+
		"\u0082\u0085\u0094\u0096\u00d0\u00d2\u00e9\u00eb\u00f5\u00f7\u00f8\u00fa"+
		"\u010d\u0001\u0000\u012b\u0143\u074b\u0000\u01c3\u0001\u0000\u0000\u0000"+
		"\u0002\u01ca\u0001\u0000\u0000\u0000\u0004\u01cd\u0001\u0000\u0000\u0000"+
		"\u0006\u01d0\u0001\u0000\u0000\u0000\b\u01d3\u0001\u0000\u0000\u0000\n"+
		"\u01d6\u0001\u0000\u0000\u0000\f\u01d9\u0001\u0000\u0000\u0000\u000e\u01dc"+
		"\u0001\u0000\u0000\u0000\u0010\u01e1\u0001\u0000\u0000\u0000\u0012\u01e5"+
		"\u0001\u0000\u0000\u0000\u0014\u01e8\u0001\u0000\u0000\u0000\u0016\u01eb"+
		"\u0001\u0000\u0000\u0000\u0018\u01ee\u0001\u0000\u0000\u0000\u001a\u01ff"+
		"\u0001\u0000\u0000\u0000\u001c\u0203\u0001\u0000\u0000\u0000\u001e\u0205"+
		"\u0001\u0000\u0000\u0000 \u020c\u0001\u0000\u0000\u0000\"\u020e\u0001"+
		"\u0000\u0000\u0000$\u021b\u0001\u0000\u0000\u0000&\u021d\u0001\u0000\u0000"+
		"\u0000(\u0221\u0001\u0000\u0000\u0000*\u022a\u0001\u0000\u0000\u0000,"+
		"\u022c\u0001\u0000\u0000\u0000.\u0239\u0001\u0000\u0000\u00000\u023c\u0001"+
		"\u0000\u0000\u00002\u0244\u0001\u0000\u0000\u00004\u0248\u0001\u0000\u0000"+
		"\u00006\u024d\u0001\u0000\u0000\u00008\u024f\u0001\u0000\u0000\u0000:"+
		"\u0258\u0001\u0000\u0000\u0000<\u025c\u0001\u0000\u0000\u0000>\u025e\u0001"+
		"\u0000\u0000\u0000@\u0267\u0001\u0000\u0000\u0000B\u026b\u0001\u0000\u0000"+
		"\u0000D\u0270\u0001\u0000\u0000\u0000F\u0272\u0001\u0000\u0000\u0000H"+
		"\u0276\u0001\u0000\u0000\u0000J\u0293\u0001\u0000\u0000\u0000L\u0296\u0001"+
		"\u0000\u0000\u0000N\u0298\u0001\u0000\u0000\u0000P\u02a5\u0001\u0000\u0000"+
		"\u0000R\u02a8\u0001\u0000\u0000\u0000T\u02ac\u0001\u0000\u0000\u0000V"+
		"\u02b0\u0001\u0000\u0000\u0000X\u02b4\u0001\u0000\u0000\u0000Z\u02b9\u0001"+
		"\u0000\u0000\u0000\\\u02bb\u0001\u0000\u0000\u0000^\u02d7\u0001\u0000"+
		"\u0000\u0000`\u02ed\u0001\u0000\u0000\u0000b\u02f3\u0001\u0000\u0000\u0000"+
		"d\u02fa\u0001\u0000\u0000\u0000f\u02fc\u0001\u0000\u0000\u0000h\u0314"+
		"\u0001\u0000\u0000\u0000j\u0317\u0001\u0000\u0000\u0000l\u0320\u0001\u0000"+
		"\u0000\u0000n\u0324\u0001\u0000\u0000\u0000p\u0326\u0001\u0000\u0000\u0000"+
		"r\u0329\u0001\u0000\u0000\u0000t\u032e\u0001\u0000\u0000\u0000v\u0330"+
		"\u0001\u0000\u0000\u0000x\u0342\u0001\u0000\u0000\u0000z\u034f\u0001\u0000"+
		"\u0000\u0000|\u035a\u0001\u0000\u0000\u0000~\u0366\u0001\u0000\u0000\u0000"+
		"\u0080\u0368\u0001\u0000\u0000\u0000\u0082\u0374\u0001\u0000\u0000\u0000"+
		"\u0084\u0376\u0001\u0000\u0000\u0000\u0086\u0388\u0001\u0000\u0000\u0000"+
		"\u0088\u038a\u0001\u0000\u0000\u0000\u008a\u038c\u0001\u0000\u0000\u0000"+
		"\u008c\u039d\u0001\u0000\u0000\u0000\u008e\u03a0\u0001\u0000\u0000\u0000"+
		"\u0090\u03a4\u0001\u0000\u0000\u0000\u0092\u03aa\u0001\u0000\u0000\u0000"+
		"\u0094\u03ae\u0001\u0000\u0000\u0000\u0096\u03b3\u0001\u0000\u0000\u0000"+
		"\u0098\u03b8\u0001\u0000\u0000\u0000\u009a\u03ba\u0001\u0000\u0000\u0000"+
		"\u009c\u03bc\u0001\u0000\u0000\u0000\u009e\u03c3\u0001\u0000\u0000\u0000"+
		"\u00a0\u03c5\u0001\u0000\u0000\u0000\u00a2\u03d9\u0001\u0000\u0000\u0000"+
		"\u00a4\u03db\u0001\u0000\u0000\u0000\u00a6\u03e8\u0001\u0000\u0000\u0000"+
		"\u00a8\u03ec\u0001\u0000\u0000\u0000\u00aa\u03ef\u0001\u0000\u0000\u0000"+
		"\u00ac\u03f4\u0001\u0000\u0000\u0000\u00ae\u03f6\u0001\u0000\u0000\u0000"+
		"\u00b0\u03fe\u0001\u0000\u0000\u0000\u00b2\u0400\u0001\u0000\u0000\u0000"+
		"\u00b4\u0403\u0001\u0000\u0000\u0000\u00b6\u0406\u0001\u0000\u0000\u0000"+
		"\u00b8\u0409\u0001\u0000\u0000\u0000\u00ba\u040b\u0001\u0000\u0000\u0000"+
		"\u00bc\u040d\u0001\u0000\u0000\u0000\u00be\u0410\u0001\u0000\u0000\u0000"+
		"\u00c0\u0417\u0001\u0000\u0000\u0000\u00c2\u041c\u0001\u0000\u0000\u0000"+
		"\u00c4\u041e\u0001\u0000\u0000\u0000\u00c6\u0426\u0001\u0000\u0000\u0000"+
		"\u00c8\u042f\u0001\u0000\u0000\u0000\u00ca\u0435\u0001\u0000\u0000\u0000"+
		"\u00cc\u0437\u0001\u0000\u0000\u0000\u00ce\u0439\u0001\u0000\u0000\u0000"+
		"\u00d0\u0443\u0001\u0000\u0000\u0000\u00d2\u0445\u0001\u0000\u0000\u0000"+
		"\u00d4\u0449\u0001\u0000\u0000\u0000\u00d6\u044b\u0001\u0000\u0000\u0000"+
		"\u00d8\u0455\u0001\u0000\u0000\u0000\u00da\u0457\u0001\u0000\u0000\u0000"+
		"\u00dc\u045c\u0001\u0000\u0000\u0000\u00de\u046c\u0001\u0000\u0000\u0000"+
		"\u00e0\u046e\u0001\u0000\u0000\u0000\u00e2\u0470\u0001\u0000\u0000\u0000"+
		"\u00e4\u0472\u0001\u0000\u0000\u0000\u00e6\u047a\u0001\u0000\u0000\u0000"+
		"\u00e8\u0483\u0001\u0000\u0000\u0000\u00ea\u048f\u0001\u0000\u0000\u0000"+
		"\u00ec\u0493\u0001\u0000\u0000\u0000\u00ee\u049c\u0001\u0000\u0000\u0000"+
		"\u00f0\u049e\u0001\u0000\u0000\u0000\u00f2\u04a2\u0001\u0000\u0000\u0000"+
		"\u00f4\u04a7\u0001\u0000\u0000\u0000\u00f6\u04a9\u0001\u0000\u0000\u0000"+
		"\u00f8\u04ac\u0001\u0000\u0000\u0000\u00fa\u04ae\u0001\u0000\u0000\u0000"+
		"\u00fc\u04b2\u0001\u0000\u0000\u0000\u00fe\u04ba\u0001\u0000\u0000\u0000"+
		"\u0100\u04c1\u0001\u0000\u0000\u0000\u0102\u04c3\u0001\u0000\u0000\u0000"+
		"\u0104\u04c6\u0001\u0000\u0000\u0000\u0106\u04c8\u0001\u0000\u0000\u0000"+
		"\u0108\u04ce\u0001\u0000\u0000\u0000\u010a\u04d4\u0001\u0000\u0000\u0000"+
		"\u010c\u04e0\u0001\u0000\u0000\u0000\u010e\u04e2\u0001\u0000\u0000\u0000"+
		"\u0110\u04ef\u0001\u0000\u0000\u0000\u0112\u04f1\u0001\u0000\u0000\u0000"+
		"\u0114\u04f6\u0001\u0000\u0000\u0000\u0116\u04fb\u0001\u0000\u0000\u0000"+
		"\u0118\u04fe\u0001\u0000\u0000\u0000\u011a\u0501\u0001\u0000\u0000\u0000"+
		"\u011c\u0504\u0001\u0000\u0000\u0000\u011e\u050c\u0001\u0000\u0000\u0000"+
		"\u0120\u0516\u0001\u0000\u0000\u0000\u0122\u051e\u0001\u0000\u0000\u0000"+
		"\u0124\u0520\u0001\u0000\u0000\u0000\u0126\u0522\u0001\u0000\u0000\u0000"+
		"\u0128\u0524\u0001\u0000\u0000\u0000\u012a\u0530\u0001\u0000\u0000\u0000"+
		"\u012c\u0532\u0001\u0000\u0000\u0000\u012e\u0539\u0001\u0000\u0000\u0000"+
		"\u0130\u0542\u0001\u0000\u0000\u0000\u0132\u054b\u0001\u0000\u0000\u0000"+
		"\u0134\u054d\u0001\u0000\u0000\u0000\u0136\u0555\u0001\u0000\u0000\u0000"+
		"\u0138\u055b\u0001\u0000\u0000\u0000\u013a\u055d\u0001\u0000\u0000\u0000"+
		"\u013c\u0561\u0001\u0000\u0000\u0000\u013e\u0564\u0001\u0000\u0000\u0000"+
		"\u0140\u0569\u0001\u0000\u0000\u0000\u0142\u0571\u0001\u0000\u0000\u0000"+
		"\u0144\u0575\u0001\u0000\u0000\u0000\u0146\u0579\u0001\u0000\u0000\u0000"+
		"\u0148\u0581\u0001\u0000\u0000\u0000\u014a\u0584\u0001\u0000\u0000\u0000"+
		"\u014c\u0587\u0001\u0000\u0000\u0000\u014e\u058b\u0001\u0000\u0000\u0000"+
		"\u0150\u058d\u0001\u0000\u0000\u0000\u0152\u0590\u0001\u0000\u0000\u0000"+
		"\u0154\u0596\u0001\u0000\u0000\u0000\u0156\u059c\u0001\u0000\u0000\u0000"+
		"\u0158\u059e\u0001\u0000\u0000\u0000\u015a\u05a0\u0001\u0000\u0000\u0000"+
		"\u015c\u05a7\u0001\u0000\u0000\u0000\u015e\u05a9\u0001\u0000\u0000\u0000"+
		"\u0160\u05ab\u0001\u0000\u0000\u0000\u0162\u05ad\u0001\u0000\u0000\u0000"+
		"\u0164\u05b2\u0001\u0000\u0000\u0000\u0166\u05b4\u0001\u0000\u0000\u0000"+
		"\u0168\u05b6\u0001\u0000\u0000\u0000\u016a\u05b8\u0001\u0000\u0000\u0000"+
		"\u016c\u05c5\u0001\u0000\u0000\u0000\u016e\u05c7\u0001\u0000\u0000\u0000"+
		"\u0170\u05c9\u0001\u0000\u0000\u0000\u0172\u05d6\u0001\u0000\u0000\u0000"+
		"\u0174\u05dd\u0001\u0000\u0000\u0000\u0176\u05e1\u0001\u0000\u0000\u0000"+
		"\u0178\u05e3\u0001\u0000\u0000\u0000\u017a\u05e5\u0001\u0000\u0000\u0000"+
		"\u017c\u05e7\u0001\u0000\u0000\u0000\u017e\u05e9\u0001\u0000\u0000\u0000"+
		"\u0180\u05f3\u0001\u0000\u0000\u0000\u0182\u0607\u0001\u0000\u0000\u0000"+
		"\u0184\u0609\u0001\u0000\u0000\u0000\u0186\u061b\u0001\u0000\u0000\u0000"+
		"\u0188\u061d\u0001\u0000\u0000\u0000\u018a\u0627\u0001\u0000\u0000\u0000"+
		"\u018c\u0629\u0001\u0000\u0000\u0000\u018e\u062b\u0001\u0000\u0000\u0000"+
		"\u0190\u062d\u0001\u0000\u0000\u0000\u0192\u062f\u0001\u0000\u0000\u0000"+
		"\u0194\u0632\u0001\u0000\u0000\u0000\u0196\u0638\u0001\u0000\u0000\u0000"+
		"\u0198\u063c\u0001\u0000\u0000\u0000\u019a\u064e\u0001\u0000\u0000\u0000"+
		"\u019c\u0650\u0001\u0000\u0000\u0000\u019e\u0656\u0001\u0000\u0000\u0000"+
		"\u01a0\u0658\u0001\u0000\u0000\u0000\u01a2\u065d\u0001\u0000\u0000\u0000"+
		"\u01a4\u065f\u0001\u0000\u0000\u0000\u01a6\u0662\u0001\u0000\u0000\u0000"+
		"\u01a8\u0665\u0001\u0000\u0000\u0000\u01aa\u0668\u0001\u0000\u0000\u0000"+
		"\u01ac\u066d\u0001\u0000\u0000\u0000\u01ae\u066f\u0001\u0000\u0000\u0000"+
		"\u01b0\u0695\u0001\u0000\u0000\u0000\u01b2\u0697\u0001\u0000\u0000\u0000"+
		"\u01b4\u069b\u0001\u0000\u0000\u0000\u01b6\u06aa\u0001\u0000\u0000\u0000"+
		"\u01b8\u06b4\u0001\u0000\u0000\u0000\u01ba\u06b6\u0001\u0000\u0000\u0000"+
		"\u01bc\u06f5\u0001\u0000\u0000\u0000\u01be\u06f7\u0001\u0000\u0000\u0000"+
		"\u01c0\u01c4\u0003\u0016\u000b\u0000\u01c1\u01c4\u00034\u001a\u0000\u01c2"+
		"\u01c4\u0003 \u0010\u0000\u01c3\u01c0\u0001\u0000\u0000\u0000\u01c3\u01c1"+
		"\u0001\u0000\u0000\u0000\u01c3\u01c2\u0001\u0000\u0000\u0000\u01c4\u01c6"+
		"\u0001\u0000\u0000\u0000\u01c5\u01c7\u0005\u0115\u0000\u0000\u01c6\u01c5"+
		"\u0001\u0000\u0000\u0000\u01c6\u01c7\u0001\u0000\u0000\u0000\u01c7\u01c8"+
		"\u0001\u0000\u0000\u0000\u01c8\u01c9\u0005\u0000\u0000\u0001\u01c9\u0001"+
		"\u0001\u0000\u0000\u0000\u01ca\u01cb\u0003z=\u0000\u01cb\u01cc\u0005\u0000"+
		"\u0000\u0001\u01cc\u0003\u0001\u0000\u0000\u0000\u01cd\u01ce\u0003|>\u0000"+
		"\u01ce\u01cf\u0005\u0000\u0000\u0001\u01cf\u0005\u0001\u0000\u0000\u0000"+
		"\u01d0\u01d1\u0003\u0132\u0099\u0000\u01d1\u01d2\u0005\u0000\u0000\u0001"+
		"\u01d2\u0007\u0001\u0000\u0000\u0000\u01d3\u01d4\u0003\u00c0`\u0000\u01d4"+
		"\u01d5\u0005\u0000\u0000\u0001\u01d5\t\u0001\u0000\u0000\u0000\u01d6\u01d7"+
		"\u0003d2\u0000\u01d7\u01d8\u0005\u0000\u0000\u0001\u01d8\u000b\u0001\u0000"+
		"\u0000\u0000\u01d9\u01da\u0003 \u0010\u0000\u01da\u01db\u0005\u0000\u0000"+
		"\u0001\u01db\r\u0001\u0000\u0000\u0000\u01dc\u01dd\u0003^/\u0000\u01dd"+
		"\u01de\u0005\u0000\u0000\u0001\u01de\u000f\u0001\u0000\u0000\u0000\u01df"+
		"\u01e2\u0003\u0194\u00ca\u0000\u01e0\u01e2\u0003\u0196\u00cb\u0000\u01e1"+
		"\u01df\u0001\u0000\u0000\u0000\u01e1\u01e0\u0001\u0000\u0000\u0000\u01e2"+
		"\u01e3\u0001\u0000\u0000\u0000\u01e3\u01e4\u0005\u0000\u0000\u0001\u01e4"+
		"\u0011\u0001\u0000\u0000\u0000\u01e5\u01e6\u0003\u0138\u009c\u0000\u01e6"+
		"\u01e7\u0005\u0000\u0000\u0001\u01e7\u0013\u0001\u0000\u0000\u0000\u01e8"+
		"\u01e9\u0003\"\u0011\u0000\u01e9\u01ea\u0005\u0000\u0000\u0001\u01ea\u0015"+
		"\u0001\u0000\u0000\u0000\u01eb\u01ec\u0003\u0018\f\u0000\u01ec\u01ed\u0003"+
		" \u0010\u0000\u01ed\u0017\u0001\u0000\u0000\u0000\u01ee\u01ef\u0005:\u0000"+
		"\u0000\u01ef\u01f4\u0003\u001a\r\u0000\u01f0\u01f1\u0005\u0116\u0000\u0000"+
		"\u01f1\u01f3\u0003\u001a\r\u0000\u01f2\u01f0\u0001\u0000\u0000\u0000\u01f3"+
		"\u01f6\u0001\u0000\u0000\u0000\u01f4\u01f2\u0001\u0000\u0000\u0000\u01f4"+
		"\u01f5\u0001\u0000\u0000\u0000\u01f5\u0019\u0001\u0000\u0000\u0000\u01f6"+
		"\u01f4\u0001\u0000\u0000\u0000\u01f7\u01f8\u0003\u001e\u000f\u0000\u01f8"+
		"\u01f9\u0005\u011d\u0000\u0000\u01f9\u01fa\u0003\u001c\u000e\u0000\u01fa"+
		"\u01fb\u0005\u011e\u0000\u0000\u01fb\u0200\u0001\u0000\u0000\u0000\u01fc"+
		"\u01fd\u0003\u001e\u000f\u0000\u01fd\u01fe\u0003\u0176\u00bb\u0000\u01fe"+
		"\u0200\u0001\u0000\u0000\u0000\u01ff\u01f7\u0001\u0000\u0000\u0000\u01ff"+
		"\u01fc\u0001\u0000\u0000\u0000\u0200\u001b\u0001\u0000\u0000\u0000\u0201"+
		"\u0204\u0003\u0016\u000b\u0000\u0202\u0204\u0003 \u0010\u0000\u0203\u0201"+
		"\u0001\u0000\u0000\u0000\u0203\u0202\u0001\u0000\u0000\u0000\u0204\u001d"+
		"\u0001\u0000\u0000\u0000\u0205\u0206\u0003\u0172\u00b9\u0000\u0206\u0207"+
		"\u0005\u0001\u0000\u0000\u0207\u001f\u0001\u0000\u0000\u0000\u0208\u020d"+
		"\u00036\u001b\u0000\u0209\u020d\u0003\"\u0011\u0000\u020a\u020d\u0003"+
		",\u0016\u0000\u020b\u020d\u0003\u0138\u009c\u0000\u020c\u0208\u0001\u0000"+
		"\u0000\u0000\u020c\u0209\u0001\u0000\u0000\u0000\u020c\u020a\u0001\u0000"+
		"\u0000\u0000\u020c\u020b\u0001\u0000\u0000\u0000\u020d!\u0001\u0000\u0000"+
		"\u0000\u020e\u020f\u0003$\u0012\u0000\u020f#\u0001\u0000\u0000\u0000\u0210"+
		"\u0211\u0003(\u0014\u0000\u0211\u0212\u0003`0\u0000\u0212\u0213\u0003"+
		"*\u0015\u0000\u0213\u021c\u0001\u0000\u0000\u0000\u0214\u0215\u0003(\u0014"+
		"\u0000\u0215\u0216\u0003`0\u0000\u0216\u0217\u0005\u011d\u0000\u0000\u0217"+
		"\u0218\u0003v;\u0000\u0218\u0219\u0005\u011e\u0000\u0000\u0219\u021a\u0003"+
		"*\u0015\u0000\u021a\u021c\u0001\u0000\u0000\u0000\u021b\u0210\u0001\u0000"+
		"\u0000\u0000\u021b\u0214\u0001\u0000\u0000\u0000\u021c%\u0001\u0000\u0000"+
		"\u0000\u021d\u021f\u0003$\u0012\u0000\u021e\u0220\u0003.\u0017\u0000\u021f"+
		"\u021e\u0001\u0000\u0000\u0000\u021f\u0220\u0001\u0000\u0000\u0000\u0220"+
		"\'\u0001\u0000\u0000\u0000\u0221\u0223\u0005`\u0000\u0000\u0222\u0224"+
		"\u0005w\u0000\u0000\u0223\u0222\u0001\u0000\u0000\u0000\u0223\u0224\u0001"+
		"\u0000\u0000\u0000\u0224\u0225\u0001\u0000\u0000\u0000\u0225\u0226\u0005"+
		"\u0019\u0000\u0000\u0226)\u0001\u0000\u0000\u0000\u0227\u022b\u00036\u001b"+
		"\u0000\u0228\u022b\u0003\u0176\u00bb\u0000\u0229\u022b\u0003\u0148\u00a4"+
		"\u0000\u022a\u0227\u0001\u0000\u0000\u0000\u022a\u0228\u0001\u0000\u0000"+
		"\u0000\u022a\u0229\u0001\u0000\u0000\u0000\u022b+\u0001\u0000\u0000\u0000"+
		"\u022c\u022d\u0005\u0094\u0000\u0000\u022d\u022e\u0003`0\u0000\u022e\u022f"+
		"\u0005\u0087\u0000\u0000\u022f\u0231\u00030\u0018\u0000\u0230\u0232\u0003"+
		"X,\u0000\u0231\u0230\u0001\u0000\u0000\u0000\u0231\u0232\u0001\u0000\u0000"+
		"\u0000\u0232\u0234\u0001\u0000\u0000\u0000\u0233\u0235\u0003\u00f6{\u0000"+
		"\u0234\u0233\u0001\u0000\u0000\u0000\u0234\u0235\u0001\u0000\u0000\u0000"+
		"\u0235\u0237\u0001\u0000\u0000\u0000\u0236\u0238\u0003.\u0017\u0000\u0237"+
		"\u0236\u0001\u0000\u0000\u0000\u0237\u0238\u0001\u0000\u0000\u0000\u0238"+
		"-\u0001\u0000\u0000\u0000\u0239\u023a\u0005,\u0000\u0000\u023a\u023b\u0005"+
		"\u0121\u0000\u0000\u023b/\u0001\u0000\u0000\u0000\u023c\u0241\u00032\u0019"+
		"\u0000\u023d\u023e\u0005\u0116\u0000\u0000\u023e\u0240\u00032\u0019\u0000"+
		"\u023f\u023d\u0001\u0000\u0000\u0000\u0240\u0243\u0001\u0000\u0000\u0000"+
		"\u0241\u023f\u0001\u0000\u0000\u0000\u0241\u0242\u0001\u0000\u0000\u0000"+
		"\u02421\u0001\u0000\u0000\u0000\u0243\u0241\u0001\u0000\u0000\u0000\u0244"+
		"\u0245\u0003x<\u0000\u0245\u0246\u0005\u0113\u0000\u0000\u0246\u0247\u0003"+
		"\u00f4z\u0000\u02473\u0001\u0000\u0000\u0000\u0248\u0249\u0005\t\u0000"+
		"\u0000\u0249\u024a\u00050\u0000\u0000\u024a\u024b\u0005\u0001\u0000\u0000"+
		"\u024b\u024c\u00036\u001b\u0000\u024c5\u0001\u0000\u0000\u0000\u024d\u024e"+
		"\u00038\u001c\u0000\u024e7\u0001\u0000\u0000\u0000\u024f\u0255\u0003>"+
		"\u001f\u0000\u0250\u0251\u0003:\u001d\u0000\u0251\u0252\u0003>\u001f\u0000"+
		"\u0252\u0254\u0001\u0000\u0000\u0000\u0253\u0250\u0001\u0000\u0000\u0000"+
		"\u0254\u0257\u0001\u0000\u0000\u0000\u0255\u0253\u0001\u0000\u0000\u0000"+
		"\u0255\u0256\u0001\u0000\u0000\u0000\u02569\u0001\u0000\u0000\u0000\u0257"+
		"\u0255\u0001\u0000\u0000\u0000\u0258\u025a\u0003<\u001e\u0000\u0259\u025b"+
		"\u0003L&\u0000\u025a\u0259\u0001\u0000\u0000\u0000\u025a\u025b\u0001\u0000"+
		"\u0000\u0000\u025b;\u0001\u0000\u0000\u0000\u025c\u025d\u0005\u0018\u0000"+
		"\u0000\u025d=\u0001\u0000\u0000\u0000\u025e\u0264\u0003D\"\u0000\u025f"+
		"\u0260\u0003@ \u0000\u0260\u0261\u0003D\"\u0000\u0261\u0263\u0001\u0000"+
		"\u0000\u0000\u0262\u025f\u0001\u0000\u0000\u0000\u0263\u0266\u0001\u0000"+
		"\u0000\u0000\u0264\u0262\u0001\u0000\u0000\u0000\u0264\u0265\u0001\u0000"+
		"\u0000\u0000\u0265?\u0001\u0000\u0000\u0000\u0266\u0264\u0001\u0000\u0000"+
		"\u0000\u0267\u0269\u0003B!\u0000\u0268\u026a\u0003L&\u0000\u0269\u0268"+
		"\u0001\u0000\u0000\u0000\u0269\u026a\u0001\u0000\u0000\u0000\u026aA\u0001"+
		"\u0000\u0000\u0000\u026b\u026c\u0007\u0000\u0000\u0000\u026cC\u0001\u0000"+
		"\u0000\u0000\u026d\u0271\u0003F#\u0000\u026e\u0271\u0003H$\u0000\u026f"+
		"\u0271\u0003\u0176\u00bb\u0000\u0270\u026d\u0001\u0000\u0000\u0000\u0270"+
		"\u026e\u0001\u0000\u0000\u0000\u0270\u026f\u0001\u0000\u0000\u0000\u0271"+
		"E\u0001\u0000\u0000\u0000\u0272\u0273\u0005\u011d\u0000\u0000\u0273\u0274"+
		"\u00036\u001b\u0000\u0274\u0275\u0005\u011e\u0000\u0000\u0275G\u0001\u0000"+
		"\u0000\u0000\u0276\u0278\u0005-\u0000\u0000\u0277\u0279\u0003J%\u0000"+
		"\u0278\u0277\u0001\u0000\u0000\u0000\u0278\u0279\u0001\u0000\u0000\u0000"+
		"\u0279\u027b\u0001\u0000\u0000\u0000\u027a\u027c\u0003L&\u0000\u027b\u027a"+
		"\u0001\u0000\u0000\u0000\u027b\u027c\u0001\u0000\u0000\u0000\u027c\u027d"+
		"\u0001\u0000\u0000\u0000\u027d\u0291\u0003N\'\u0000\u027e\u0280\u0003"+
		"X,\u0000\u027f\u0281\u0003\u00f6{\u0000\u0280\u027f\u0001\u0000\u0000"+
		"\u0000\u0280\u0281\u0001\u0000\u0000\u0000\u0281\u0283\u0001\u0000\u0000"+
		"\u0000\u0282\u0284\u0003\u0108\u0084\u0000\u0283\u0282\u0001\u0000\u0000"+
		"\u0000\u0283\u0284\u0001\u0000\u0000\u0000\u0284\u0286\u0001\u0000\u0000"+
		"\u0000\u0285\u0287\u0003\u0118\u008c\u0000\u0286\u0285\u0001\u0000\u0000"+
		"\u0000\u0286\u0287\u0001\u0000\u0000\u0000\u0287\u0289\u0001\u0000\u0000"+
		"\u0000\u0288\u028a\u0003\u011a\u008d\u0000\u0289\u0288\u0001\u0000\u0000"+
		"\u0000\u0289\u028a\u0001\u0000\u0000\u0000\u028a\u028c\u0001\u0000\u0000"+
		"\u0000\u028b\u028d\u0003\u00fa}\u0000\u028c\u028b\u0001\u0000\u0000\u0000"+
		"\u028c\u028d\u0001\u0000\u0000\u0000\u028d\u028f\u0001\u0000\u0000\u0000"+
		"\u028e\u0290\u0003\u0106\u0083\u0000\u028f\u028e\u0001\u0000\u0000\u0000"+
		"\u028f\u0290\u0001\u0000\u0000\u0000\u0290\u0292\u0001\u0000\u0000\u0000"+
		"\u0291\u027e\u0001\u0000\u0000\u0000\u0291\u0292\u0001\u0000\u0000\u0000"+
		"\u0292I\u0001\u0000\u0000\u0000\u0293\u0294\u0005\u0019\u0000\u0000\u0294"+
		"\u0295\u0003f3\u0000\u0295K\u0001\u0000\u0000\u0000\u0296\u0297\u0007"+
		"\u0001\u0000\u0000\u0297M\u0001\u0000\u0000\u0000\u0298\u029d\u0003P("+
		"\u0000\u0299\u029a\u0005\u0116\u0000\u0000\u029a\u029c\u0003P(\u0000\u029b"+
		"\u0299\u0001\u0000\u0000\u0000\u029c\u029f\u0001\u0000\u0000\u0000\u029d"+
		"\u029b\u0001\u0000\u0000\u0000\u029d\u029e\u0001\u0000\u0000\u0000\u029e"+
		"O\u0001\u0000\u0000\u0000\u029f\u029d\u0001\u0000\u0000\u0000\u02a0\u02a2"+
		"\u0003\u00c0`\u0000\u02a1\u02a3\u0003R)\u0000\u02a2\u02a1\u0001\u0000"+
		"\u0000\u0000\u02a2\u02a3\u0001\u0000\u0000\u0000\u02a3\u02a6\u0001\u0000"+
		"\u0000\u0000\u02a4\u02a6\u0003T*\u0000\u02a5\u02a0\u0001\u0000\u0000\u0000"+
		"\u02a5\u02a4\u0001\u0000\u0000\u0000\u02a6Q\u0001\u0000\u0000\u0000\u02a7"+
		"\u02a9\u0005\u0001\u0000\u0000\u02a8\u02a7\u0001\u0000\u0000\u0000\u02a8"+
		"\u02a9\u0001\u0000\u0000\u0000\u02a9\u02aa\u0001\u0000\u0000\u0000\u02aa"+
		"\u02ab\u0003\u0174\u00ba\u0000\u02abS\u0001\u0000\u0000\u0000\u02ac\u02ad"+
		"\u0003V+\u0000\u02adU\u0001\u0000\u0000\u0000\u02ae\u02af\u0005\u0149"+
		"\u0000\u0000\u02af\u02b1\u0005\u0124\u0000\u0000\u02b0\u02ae\u0001\u0000"+
		"\u0000\u0000\u02b0\u02b1\u0001\u0000\u0000\u0000\u02b1\u02b2\u0001\u0000"+
		"\u0000\u0000\u02b2\u02b3\u0005\u0121\u0000\u0000\u02b3W\u0001\u0000\u0000"+
		"\u0000\u02b4\u02b5\u0005\u0011\u0000\u0000\u02b5\u02b7\u0003\\.\u0000"+
		"\u02b6\u02b8\u0003Z-\u0000\u02b7\u02b6\u0001\u0000\u0000\u0000\u02b7\u02b8"+
		"\u0001\u0000\u0000\u0000\u02b8Y\u0001\u0000\u0000\u0000\u02b9\u02ba\u0003"+
		"\u0176\u00bb\u0000\u02ba[\u0001\u0000\u0000\u0000\u02bb\u02c8\u0003`0"+
		"\u0000\u02bc\u02bd\u0005\u0116\u0000\u0000\u02bd\u02c7\u0003`0\u0000\u02be"+
		"\u02bf\u0003h4\u0000\u02bf\u02c0\u0003`0\u0000\u02c0\u02c7\u0001\u0000"+
		"\u0000\u0000\u02c1\u02c2\u0003j5\u0000\u02c2\u02c4\u0003`0\u0000\u02c3"+
		"\u02c5\u0003n7\u0000\u02c4\u02c3\u0001\u0000\u0000\u0000\u02c4\u02c5\u0001"+
		"\u0000\u0000\u0000\u02c5\u02c7\u0001\u0000\u0000\u0000\u02c6\u02bc\u0001"+
		"\u0000\u0000\u0000\u02c6\u02be\u0001\u0000\u0000\u0000\u02c6\u02c1\u0001"+
		"\u0000\u0000\u0000\u02c7\u02ca\u0001\u0000\u0000\u0000\u02c8\u02c6\u0001"+
		"\u0000\u0000\u0000\u02c8\u02c9\u0001\u0000\u0000\u0000\u02c9]\u0001\u0000"+
		"\u0000\u0000\u02ca\u02c8\u0001\u0000\u0000\u0000\u02cb\u02cc\u0005\u0116"+
		"\u0000\u0000\u02cc\u02d6\u0003`0\u0000\u02cd\u02ce\u0003h4\u0000\u02ce"+
		"\u02cf\u0003`0\u0000\u02cf\u02d6\u0001\u0000\u0000\u0000\u02d0\u02d1\u0003"+
		"j5\u0000\u02d1\u02d3\u0003`0\u0000\u02d2\u02d4\u0003n7\u0000\u02d3\u02d2"+
		"\u0001\u0000\u0000\u0000\u02d3\u02d4\u0001\u0000\u0000\u0000\u02d4\u02d6"+
		"\u0001\u0000\u0000\u0000\u02d5\u02cb\u0001\u0000\u0000\u0000\u02d5\u02cd"+
		"\u0001\u0000\u0000\u0000\u02d5\u02d0\u0001\u0000\u0000\u0000\u02d6\u02d9"+
		"\u0001\u0000\u0000\u0000\u02d7\u02d5\u0001\u0000\u0000\u0000\u02d7\u02d8"+
		"\u0001\u0000\u0000\u0000\u02d8\u02db\u0001\u0000\u0000\u0000\u02d9\u02d7"+
		"\u0001\u0000\u0000\u0000\u02da\u02dc\u0003Z-\u0000\u02db\u02da\u0001\u0000"+
		"\u0000\u0000\u02db\u02dc\u0001\u0000\u0000\u0000\u02dc_\u0001\u0000\u0000"+
		"\u0000\u02dd\u02df\u0003f3\u0000\u02de\u02e0\u0003b1\u0000\u02df\u02de"+
		"\u0001\u0000\u0000\u0000\u02df\u02e0\u0001\u0000\u0000\u0000\u02e0\u02ee"+
		"\u0001\u0000\u0000\u0000\u02e1\u02e2\u0003\u0176\u00bb\u0000\u02e2\u02e3"+
		"\u0003R)\u0000\u02e3\u02ee\u0001\u0000\u0000\u0000\u02e4\u02e6\u0003\u0180"+
		"\u00c0\u0000\u02e5\u02e7\u0003b1\u0000\u02e6\u02e5\u0001\u0000\u0000\u0000"+
		"\u02e6\u02e7\u0001\u0000\u0000\u0000\u02e7\u02ee\u0001\u0000\u0000\u0000"+
		"\u02e8\u02ee\u0003\u0138\u009c\u0000\u02e9\u02eb\u0003F#\u0000\u02ea\u02ec"+
		"\u0003b1\u0000\u02eb\u02ea\u0001\u0000\u0000\u0000\u02eb\u02ec\u0001\u0000"+
		"\u0000\u0000\u02ec\u02ee\u0001\u0000\u0000\u0000\u02ed\u02dd\u0001\u0000"+
		"\u0000\u0000\u02ed\u02e1\u0001\u0000\u0000\u0000\u02ed\u02e4\u0001\u0000"+
		"\u0000\u0000\u02ed\u02e8\u0001\u0000\u0000\u0000\u02ed\u02e9\u0001\u0000"+
		"\u0000\u0000\u02eea\u0001\u0000\u0000\u0000\u02ef\u02f0\u0005\u0001\u0000"+
		"\u0000\u02f0\u02f4\u0003\u0174\u00ba\u0000\u02f1\u02f2\u00041\u0000\u0000"+
		"\u02f2\u02f4\u0003\u0174\u00ba\u0000\u02f3\u02ef\u0001\u0000\u0000\u0000"+
		"\u02f3\u02f1\u0001\u0000\u0000\u0000\u02f4c\u0001\u0000\u0000\u0000\u02f5"+
		"\u02fb\u0003f3\u0000\u02f6\u02fb\u0003\u0176\u00bb\u0000\u02f7\u02fb\u0003"+
		"\u0180\u00c0\u0000\u02f8\u02fb\u0003\u0138\u009c\u0000\u02f9\u02fb\u0003"+
		"F#\u0000\u02fa\u02f5\u0001\u0000\u0000\u0000\u02fa\u02f6\u0001\u0000\u0000"+
		"\u0000\u02fa\u02f7\u0001\u0000\u0000\u0000\u02fa\u02f8\u0001\u0000\u0000"+
		"\u0000\u02fa\u02f9\u0001\u0000\u0000\u0000\u02fbe\u0001\u0000\u0000\u0000"+
		"\u02fc\u0302\u0003\u0172\u00b9\u0000\u02fd\u0300\u0005\u0124\u0000\u0000"+
		"\u02fe\u0301\u0003\u018c\u00c6\u0000\u02ff\u0301\u0003\u0172\u00b9\u0000"+
		"\u0300\u02fe\u0001\u0000\u0000\u0000\u0300\u02ff\u0001\u0000\u0000\u0000"+
		"\u0301\u0303\u0001\u0000\u0000\u0000\u0302\u02fd\u0001\u0000\u0000\u0000"+
		"\u0302\u0303\u0001\u0000\u0000\u0000\u0303\u0309\u0001\u0000\u0000\u0000"+
		"\u0304\u0307\u0005\u0124\u0000\u0000\u0305\u0308\u0003\u018c\u00c6\u0000"+
		"\u0306\u0308\u0003\u0172\u00b9\u0000\u0307\u0305\u0001\u0000\u0000\u0000"+
		"\u0307\u0306\u0001\u0000\u0000\u0000\u0308\u030a\u0001\u0000\u0000\u0000"+
		"\u0309\u0304\u0001\u0000\u0000\u0000\u0309\u030a\u0001\u0000\u0000\u0000"+
		"\u030ag\u0001\u0000\u0000\u0000\u030b\u030c\u0005\n\u0000\u0000\u030c"+
		"\u0315\u0005\u001b\u0000\u0000\u030d\u030e\u00055\u0000\u0000\u030e\u0315"+
		"\u0005\u001b\u0000\u0000\u030f\u0311\u0005 \u0000\u0000\u0310\u0312\u0003"+
		"l6\u0000\u0311\u0310\u0001\u0000\u0000\u0000\u0311\u0312\u0001\u0000\u0000"+
		"\u0000\u0312\u0313\u0001\u0000\u0000\u0000\u0313\u0315\u0005\u001b\u0000"+
		"\u0000\u0314\u030b\u0001\u0000\u0000\u0000\u0314\u030d\u0001\u0000\u0000"+
		"\u0000\u0314\u030f\u0001\u0000\u0000\u0000\u0315i\u0001\u0000\u0000\u0000"+
		"\u0316\u0318\u0003l6\u0000\u0317\u0316\u0001\u0000\u0000\u0000\u0317\u0318"+
		"\u0001\u0000\u0000\u0000\u0318\u0319\u0001\u0000\u0000\u0000\u0319\u031a"+
		"\u0005\u001b\u0000\u0000\u031ak\u0001\u0000\u0000\u0000\u031b\u0321\u0005"+
		"\u0017\u0000\u0000\u031c\u031e\u0007\u0002\u0000\u0000\u031d\u031f\u0005"+
		"\'\u0000\u0000\u031e\u031d\u0001\u0000\u0000\u0000\u031e\u031f\u0001\u0000"+
		"\u0000\u0000\u031f\u0321\u0001\u0000\u0000\u0000\u0320\u031b\u0001\u0000"+
		"\u0000\u0000\u0320\u031c\u0001\u0000\u0000\u0000\u0321m\u0001\u0000\u0000"+
		"\u0000\u0322\u0325\u0003p8\u0000\u0323\u0325\u0003r9\u0000\u0324\u0322"+
		"\u0001\u0000\u0000\u0000\u0324\u0323\u0001\u0000\u0000\u0000\u0325o\u0001"+
		"\u0000\u0000\u0000\u0326\u0327\u0005&\u0000\u0000\u0327\u0328\u0003\u00f8"+
		"|\u0000\u0328q\u0001\u0000\u0000\u0000\u0329\u032a\u0003t:\u0000\u032a"+
		"\u032b\u0005\u011d\u0000\u0000\u032b\u032c\u0003v;\u0000\u032c\u032d\u0005"+
		"\u011e\u0000\u0000\u032ds\u0001\u0000\u0000\u0000\u032e\u032f\u00057\u0000"+
		"\u0000\u032fu\u0001\u0000\u0000\u0000\u0330\u0335\u0003x<\u0000\u0331"+
		"\u0332\u0005\u0116\u0000\u0000\u0332\u0334\u0003x<\u0000\u0333\u0331\u0001"+
		"\u0000\u0000\u0000\u0334\u0337\u0001\u0000\u0000\u0000\u0335\u0333\u0001"+
		"\u0000\u0000\u0000\u0335\u0336\u0001\u0000\u0000\u0000\u0336w\u0001\u0000"+
		"\u0000\u0000\u0337\u0335\u0001\u0000\u0000\u0000\u0338\u0339\u0003\u0172"+
		"\u00b9\u0000\u0339\u033a\u0005\u0124\u0000\u0000\u033a\u033c\u0001\u0000"+
		"\u0000\u0000\u033b\u0338\u0001\u0000\u0000\u0000\u033b\u033c\u0001\u0000"+
		"\u0000\u0000\u033c\u033d\u0001\u0000\u0000\u0000\u033d\u0343\u0003\u0172"+
		"\u00b9\u0000\u033e\u033f\u0003\u0172\u00b9\u0000\u033f\u0340\u0005\u0124"+
		"\u0000\u0000\u0340\u0341\u0003\u0176\u00bb\u0000\u0341\u0343\u0001\u0000"+
		"\u0000\u0000\u0342\u033b\u0001\u0000\u0000\u0000\u0342\u033e\u0001\u0000"+
		"\u0000\u0000\u0343y\u0001\u0000\u0000\u0000\u0344\u0345\u0003\u0172\u00b9"+
		"\u0000\u0345\u0346\u0005\u0124\u0000\u0000\u0346\u0348\u0001\u0000\u0000"+
		"\u0000\u0347\u0344\u0001\u0000\u0000\u0000\u0347\u0348\u0001\u0000\u0000"+
		"\u0000\u0348\u0349\u0001\u0000\u0000\u0000\u0349\u0350\u0003\u0172\u00b9"+
		"\u0000\u034a\u034b\u0003\u0172\u00b9\u0000\u034b\u034c\u0005\u0124\u0000"+
		"\u0000\u034c\u034d\u0003\u0176\u00bb\u0000\u034d\u0350\u0001\u0000\u0000"+
		"\u0000\u034e\u0350\u0003\u0176\u00bb\u0000\u034f\u0347\u0001\u0000\u0000"+
		"\u0000\u034f\u034a\u0001\u0000\u0000\u0000\u034f\u034e\u0001\u0000\u0000"+
		"\u0000\u0350{\u0001\u0000\u0000\u0000\u0351\u035b\u0003~?\u0000\u0352"+
		"\u035b\u0003\u00d6k\u0000\u0353\u0354\u0003\u00ccf\u0000\u0354\u0355\u0003"+
		"\u00cae\u0000\u0355\u035b\u0001\u0000\u0000\u0000\u0356\u035b\u0003\u00da"+
		"m\u0000\u0357\u035b\u0003\u009aM\u0000\u0358\u035b\u0003\u0176\u00bb\u0000"+
		"\u0359\u035b\u0003\u01be\u00df\u0000\u035a\u0351\u0001\u0000\u0000\u0000"+
		"\u035a\u0352\u0001\u0000\u0000\u0000\u035a\u0353\u0001\u0000\u0000\u0000"+
		"\u035a\u0356\u0001\u0000\u0000\u0000\u035a\u0357\u0001\u0000\u0000\u0000"+
		"\u035a\u0358\u0001\u0000\u0000\u0000\u035a\u0359\u0001\u0000\u0000\u0000"+
		"\u035b}\u0001\u0000\u0000\u0000\u035c\u035f\u0003\u0080@\u0000\u035d\u035e"+
		"\u0005\u0129\u0000\u0000\u035e\u0360\u0003\u01ac\u00d6\u0000\u035f\u035d"+
		"\u0001\u0000\u0000\u0000\u035f\u0360\u0001\u0000\u0000\u0000\u0360\u0367"+
		"\u0001\u0000\u0000\u0000\u0361\u0364\u0003\u0082A\u0000\u0362\u0363\u0005"+
		"\u0129\u0000\u0000\u0363\u0365\u0003\u01ac\u00d6\u0000\u0364\u0362\u0001"+
		"\u0000\u0000\u0000\u0364\u0365\u0001\u0000\u0000\u0000\u0365\u0367\u0001"+
		"\u0000\u0000\u0000\u0366\u035c\u0001\u0000\u0000\u0000\u0366\u0361\u0001"+
		"\u0000\u0000\u0000\u0367\u007f\u0001\u0000\u0000\u0000\u0368\u0369\u0005"+
		"\u011d\u0000\u0000\u0369\u036a\u0003\u00c0`\u0000\u036a\u036b\u0005\u011e"+
		"\u0000\u0000\u036b\u0081\u0001\u0000\u0000\u0000\u036c\u0375\u0003\u0196"+
		"\u00cb\u0000\u036d\u0375\u0003x<\u0000\u036e\u0375\u0003\u0086C\u0000"+
		"\u036f\u0375\u0003\u008cF\u0000\u0370\u0375\u0003\u009cN\u0000\u0371\u0375"+
		"\u0003\u016a\u00b5\u0000\u0372\u0375\u0003\u00a0P\u0000\u0373\u0375\u0003"+
		"\u0084B\u0000\u0374\u036c\u0001\u0000\u0000\u0000\u0374\u036d\u0001\u0000"+
		"\u0000\u0000\u0374\u036e\u0001\u0000\u0000\u0000\u0374\u036f\u0001\u0000"+
		"\u0000\u0000\u0374\u0370\u0001\u0000\u0000\u0000\u0374\u0371\u0001\u0000"+
		"\u0000\u0000\u0374\u0372\u0001\u0000\u0000\u0000\u0374\u0373\u0001\u0000"+
		"\u0000\u0000\u0375\u0083\u0001\u0000\u0000\u0000\u0376\u0377\u0003F#\u0000"+
		"\u0377\u0085\u0001\u0000\u0000\u0000\u0378\u0379\u0005E\u0000\u0000\u0379"+
		"\u037a\u0005\u011d\u0000\u0000\u037a\u037b\u0003V+\u0000\u037b\u037c\u0005"+
		"\u011e\u0000\u0000\u037c\u0389\u0001\u0000\u0000\u0000\u037d\u0380\u0003"+
		"\u0088D\u0000\u037e\u0380\u0003\u008aE\u0000\u037f\u037d\u0001\u0000\u0000"+
		"\u0000\u037f\u037e\u0001\u0000\u0000\u0000\u0380\u0381\u0001\u0000\u0000"+
		"\u0000\u0381\u0383\u0005\u011d\u0000\u0000\u0382\u0384\u0003L&\u0000\u0383"+
		"\u0382\u0001\u0000\u0000\u0000\u0383\u0384\u0001\u0000\u0000\u0000\u0384"+
		"\u0385\u0001\u0000\u0000\u0000\u0385\u0386\u0003\u00c0`\u0000\u0386\u0387"+
		"\u0005\u011e\u0000\u0000\u0387\u0389\u0001\u0000\u0000\u0000\u0388\u0378"+
		"\u0001\u0000\u0000\u0000\u0388\u037f\u0001\u0000\u0000\u0000\u0389\u0087"+
		"\u0001\u0000\u0000\u0000\u038a\u038b\u0007\u0003\u0000\u0000\u038b\u0089"+
		"\u0001\u0000\u0000\u0000\u038c\u038d\u0007\u0004\u0000\u0000\u038d\u008b"+
		"\u0001\u0000\u0000\u0000\u038e\u038f\u0005\u0007\u0000\u0000\u038f\u0390"+
		"\u0003\u00c0`\u0000\u0390\u0392\u0003\u0092I\u0000\u0391\u0393\u0003\u0096"+
		"K\u0000\u0392\u0391\u0001\u0000\u0000\u0000\u0392\u0393\u0001\u0000\u0000"+
		"\u0000\u0393\u0394\u0001\u0000\u0000\u0000\u0394\u0395\u0005\f\u0000\u0000"+
		"\u0395\u039e\u0001\u0000\u0000\u0000\u0396\u0397\u0005\u0007\u0000\u0000"+
		"\u0397\u0399\u0003\u008eG\u0000\u0398\u039a\u0003\u0096K\u0000\u0399\u0398"+
		"\u0001\u0000\u0000\u0000\u0399\u039a\u0001\u0000\u0000\u0000\u039a\u039b"+
		"\u0001\u0000\u0000\u0000\u039b\u039c\u0005\f\u0000\u0000\u039c\u039e\u0001"+
		"\u0000\u0000\u0000\u039d\u038e\u0001\u0000\u0000\u0000\u039d\u0396\u0001"+
		"\u0000\u0000\u0000\u039e\u008d\u0001\u0000\u0000\u0000\u039f\u03a1\u0003"+
		"\u0090H\u0000\u03a0\u039f\u0001\u0000\u0000\u0000\u03a1\u03a2\u0001\u0000"+
		"\u0000\u0000\u03a2\u03a0\u0001\u0000\u0000\u0000\u03a2\u03a3\u0001\u0000"+
		"\u0000\u0000\u03a3\u008f\u0001\u0000\u0000\u0000\u03a4\u03a5\u00058\u0000"+
		"\u0000\u03a5\u03a6\u0003\u00c0`\u0000\u03a6\u03a7\u00051\u0000\u0000\u03a7"+
		"\u03a8\u0003\u0098L\u0000\u03a8\u0091\u0001\u0000\u0000\u0000\u03a9\u03ab"+
		"\u0003\u0094J\u0000\u03aa\u03a9\u0001\u0000\u0000\u0000\u03ab\u03ac\u0001"+
		"\u0000\u0000\u0000\u03ac\u03aa\u0001\u0000\u0000\u0000\u03ac\u03ad\u0001"+
		"\u0000\u0000\u0000\u03ad\u0093\u0001\u0000\u0000\u0000\u03ae\u03af\u0005"+
		"8\u0000\u0000\u03af\u03b0\u0003\u00c0`\u0000\u03b0\u03b1\u00051\u0000"+
		"\u0000\u03b1\u03b2\u0003\u0098L\u0000\u03b2\u0095\u0001\u0000\u0000\u0000"+
		"\u03b3\u03b4\u0005\r\u0000\u0000\u03b4\u03b5\u0003\u0098L\u0000\u03b5"+
		"\u0097\u0001\u0000\u0000\u0000\u03b6\u03b9\u0003\u00c0`\u0000\u03b7\u03b9"+
		"\u0003\u009aM\u0000\u03b8\u03b6\u0001\u0000\u0000\u0000\u03b8\u03b7\u0001"+
		"\u0000\u0000\u0000\u03b9\u0099\u0001\u0000\u0000\u0000\u03ba\u03bb\u0005"+
		"\"\u0000\u0000\u03bb\u009b\u0001\u0000\u0000\u0000\u03bc\u03bd\u0003\u009e"+
		"O\u0000\u03bd\u03be\u0005\u011d\u0000\u0000\u03be\u03bf\u0003\u00c0`\u0000"+
		"\u03bf\u03c0\u0005\u0001\u0000\u0000\u03c0\u03c1\u0003\u01ac\u00d6\u0000"+
		"\u03c1\u03c2\u0005\u011e\u0000\u0000\u03c2\u009d\u0001\u0000\u0000\u0000"+
		"\u03c3\u03c4\u0007\u0005\u0000\u0000\u03c4\u009f\u0001\u0000\u0000\u0000"+
		"\u03c5\u03c6\u0003\u00a2Q\u0000\u03c6\u03c7\u0003\u00a4R\u0000\u03c7\u00a1"+
		"\u0001\u0000\u0000\u0000\u03c8\u03c9\u0003\u0088D\u0000\u03c9\u03cb\u0005"+
		"\u011d\u0000\u0000\u03ca\u03cc\u0003\u0170\u00b8\u0000\u03cb\u03ca\u0001"+
		"\u0000\u0000\u0000\u03cb\u03cc\u0001\u0000\u0000\u0000\u03cc\u03cd\u0001"+
		"\u0000\u0000\u0000\u03cd\u03ce\u0005\u011e\u0000\u0000\u03ce\u03da\u0001"+
		"\u0000\u0000\u0000\u03cf\u03d0\u0003\u00ba]\u0000\u03d0\u03d1\u0005\u011d"+
		"\u0000\u0000\u03d1\u03d2\u0003\u0170\u00b8\u0000\u03d2\u03d7\u0005\u011e"+
		"\u0000\u0000\u03d3\u03d5\u0003\u00bc^\u0000\u03d4\u03d3\u0001\u0000\u0000"+
		"\u0000\u03d4\u03d5\u0001\u0000\u0000\u0000\u03d5\u03d6\u0001\u0000\u0000"+
		"\u0000\u03d6\u03d8\u0003\u00be_\u0000\u03d7\u03d4\u0001\u0000\u0000\u0000"+
		"\u03d7\u03d8\u0001\u0000\u0000\u0000\u03d8\u03da\u0001\u0000\u0000\u0000"+
		"\u03d9\u03c8\u0001\u0000\u0000\u0000\u03d9\u03cf\u0001\u0000\u0000\u0000"+
		"\u03da\u00a3\u0001\u0000\u0000\u0000\u03db\u03dc\u0005v\u0000\u0000\u03dc"+
		"\u03de\u0005\u011d\u0000\u0000\u03dd\u03df\u0003\u00a6S\u0000\u03de\u03dd"+
		"\u0001\u0000\u0000\u0000\u03de\u03df\u0001\u0000\u0000\u0000\u03df\u03e1"+
		"\u0001\u0000\u0000\u0000\u03e0\u03e2\u0003\u00fa}\u0000\u03e1\u03e0\u0001"+
		"\u0000\u0000\u0000\u03e1\u03e2\u0001\u0000\u0000\u0000\u03e2\u03e4\u0001"+
		"\u0000\u0000\u0000\u03e3\u03e5\u0003\u00a8T\u0000\u03e4\u03e3\u0001\u0000"+
		"\u0000\u0000\u03e4\u03e5\u0001\u0000\u0000\u0000\u03e5\u03e6\u0001\u0000"+
		"\u0000\u0000\u03e6\u03e7\u0005\u011e\u0000\u0000\u03e7\u00a5\u0001\u0000"+
		"\u0000\u0000\u03e8\u03e9\u0005x\u0000\u0000\u03e9\u03ea\u0005?\u0000\u0000"+
		"\u03ea\u03eb\u0003\u0170\u00b8\u0000\u03eb\u00a7\u0001\u0000\u0000\u0000"+
		"\u03ec\u03ed\u0003\u00aaU\u0000\u03ed\u03ee\u0003\u00acV\u0000\u03ee\u00a9"+
		"\u0001\u0000\u0000\u0000\u03ef\u03f0\u0007\u0006\u0000\u0000\u03f0\u00ab"+
		"\u0001\u0000\u0000\u0000\u03f1\u03f5\u0003\u00aeW\u0000\u03f2\u03f5\u0003"+
		"\u00b2Y\u0000\u03f3\u03f5\u0003\u00b6[\u0000\u03f4\u03f1\u0001\u0000\u0000"+
		"\u0000\u03f4\u03f2\u0001\u0000\u0000\u0000\u03f4\u03f3\u0001\u0000\u0000"+
		"\u0000\u03f5\u00ad\u0001\u0000\u0000\u0000\u03f6\u03f7\u0005>\u0000\u0000"+
		"\u03f7\u03f8\u0003\u00b0X\u0000\u03f8\u03f9\u0005\u0003\u0000\u0000\u03f9"+
		"\u03fa\u0003\u00b0X\u0000\u03fa\u00af\u0001\u0000\u0000\u0000\u03fb\u03ff"+
		"\u0003\u00b2Y\u0000\u03fc\u03ff\u0003\u00b4Z\u0000\u03fd\u03ff\u0003\u00b6"+
		"[\u0000\u03fe\u03fb\u0001\u0000\u0000\u0000\u03fe\u03fc\u0001\u0000\u0000"+
		"\u0000\u03fe\u03fd\u0001\u0000\u0000\u0000\u03ff\u00b1\u0001\u0000\u0000"+
		"\u0000\u0400\u0401\u0003\u00b8\\\u0000\u0401\u0402\u0005z\u0000\u0000"+
		"\u0402\u00b3\u0001\u0000\u0000\u0000\u0403\u0404\u0003\u00b8\\\u0000\u0404"+
		"\u0405\u0005Y\u0000\u0000\u0405\u00b5\u0001\u0000\u0000\u0000\u0406\u0407"+
		"\u0005H\u0000\u0000\u0407\u0408\u0005\u0084\u0000\u0000\u0408\u00b7\u0001"+
		"\u0000\u0000\u0000\u0409\u040a\u0007\u0007\u0000\u0000\u040a\u00b9\u0001"+
		"\u0000\u0000\u0000\u040b\u040c\u0007\b\u0000\u0000\u040c\u00bb\u0001\u0000"+
		"\u0000\u0000\u040d\u040e\u0005\u0011\u0000\u0000\u040e\u040f\u0007\t\u0000"+
		"\u0000\u040f\u00bd\u0001\u0000\u0000\u0000\u0410\u0411\u0007\n\u0000\u0000"+
		"\u0411\u0412\u0005#\u0000\u0000\u0412\u00bf\u0001\u0000\u0000\u0000\u0413"+
		"\u0418\u0003\u00c2a\u0000\u0414\u0418\u0003\u00f2y\u0000\u0415\u0418\u0003"+
		"\u0176\u00bb\u0000\u0416\u0418\u0003\u00e2q\u0000\u0417\u0413\u0001\u0000"+
		"\u0000\u0000\u0417\u0414\u0001\u0000\u0000\u0000\u0417\u0415\u0001\u0000"+
		"\u0000\u0000\u0417\u0416\u0001\u0000\u0000\u0000\u0418\u00c1\u0001\u0000"+
		"\u0000\u0000\u0419\u041d\u0003\u00c4b\u0000\u041a\u041d\u0003\u00d6k\u0000"+
		"\u041b\u041d\u0003\u009aM\u0000\u041c\u0419\u0001\u0000\u0000\u0000\u041c"+
		"\u041a\u0001\u0000\u0000\u0000\u041c\u041b\u0001\u0000\u0000\u0000\u041d"+
		"\u00c3\u0001\u0000\u0000\u0000\u041e\u0423\u0003\u00c6c\u0000\u041f\u0420"+
		"\u0007\u000b\u0000\u0000\u0420\u0422\u0003\u00c6c\u0000\u0421\u041f\u0001"+
		"\u0000\u0000\u0000\u0422\u0425\u0001\u0000\u0000\u0000\u0423\u0421\u0001"+
		"\u0000\u0000\u0000\u0423\u0424\u0001\u0000\u0000\u0000\u0424\u00c5\u0001"+
		"\u0000\u0000\u0000\u0425\u0423\u0001\u0000\u0000\u0000\u0426\u042b\u0003"+
		"\u00c8d\u0000\u0427\u0428\u0007\f\u0000\u0000\u0428\u042a\u0003\u00c8"+
		"d\u0000\u0429\u0427\u0001\u0000\u0000\u0000\u042a\u042d\u0001\u0000\u0000"+
		"\u0000\u042b\u0429\u0001\u0000\u0000\u0000\u042b\u042c\u0001\u0000\u0000"+
		"\u0000\u042c\u00c7\u0001\u0000\u0000\u0000\u042d\u042b\u0001\u0000\u0000"+
		"\u0000\u042e\u0430\u0003\u00ccf\u0000\u042f\u042e\u0001\u0000\u0000\u0000"+
		"\u042f\u0430\u0001\u0000\u0000\u0000\u0430\u0431\u0001\u0000\u0000\u0000"+
		"\u0431\u0432\u0003\u00cae\u0000\u0432\u00c9\u0001\u0000\u0000\u0000\u0433"+
		"\u0436\u0003~?\u0000\u0434\u0436\u0003\u00ceg\u0000\u0435\u0433\u0001"+
		"\u0000\u0000\u0000\u0435\u0434\u0001\u0000\u0000\u0000\u0436\u00cb\u0001"+
		"\u0000\u0000\u0000\u0437\u0438\u0007\u000b\u0000\u0000\u0438\u00cd\u0001"+
		"\u0000\u0000\u0000\u0439\u043a\u0005U\u0000\u0000\u043a\u043b\u0005\u011d"+
		"\u0000\u0000\u043b\u043c\u0003\u00d0h\u0000\u043c\u043d\u0005\u0011\u0000"+
		"\u0000\u043d\u043e\u0003\u00d4j\u0000\u043e\u043f\u0005\u011e\u0000\u0000"+
		"\u043f\u00cf\u0001\u0000\u0000\u0000\u0440\u0444\u0003\u0164\u00b2\u0000"+
		"\u0441\u0444\u0003\u00d2i\u0000\u0442\u0444\u0003\u0168\u00b4\u0000\u0443"+
		"\u0440\u0001\u0000\u0000\u0000\u0443\u0441\u0001\u0000\u0000\u0000\u0443"+
		"\u0442\u0001\u0000\u0000\u0000\u0444\u00d1\u0001\u0000\u0000\u0000\u0445"+
		"\u0446\u0007\r\u0000\u0000\u0446\u00d3\u0001\u0000\u0000\u0000\u0447\u044a"+
		"\u0003x<\u0000\u0448\u044a\u0003\u01a2\u00d1\u0000\u0449\u0447\u0001\u0000"+
		"\u0000\u0000\u0449\u0448\u0001\u0000\u0000\u0000\u044a\u00d5\u0001\u0000"+
		"\u0000\u0000\u044b\u0450\u0003\u00d8l\u0000\u044c\u044d\u0005\u0117\u0000"+
		"\u0000\u044d\u044f\u0003\u00d8l\u0000\u044e\u044c\u0001\u0000\u0000\u0000"+
		"\u044f\u0452\u0001\u0000\u0000\u0000\u0450\u044e\u0001\u0000\u0000\u0000"+
		"\u0450\u0451\u0001\u0000\u0000\u0000\u0451\u00d7\u0001\u0000\u0000\u0000"+
		"\u0452\u0450\u0001\u0000\u0000\u0000\u0453\u0456\u0003~?\u0000\u0454\u0456"+
		"\u0003\u00dam\u0000\u0455\u0453\u0001\u0000\u0000\u0000\u0455\u0454\u0001"+
		"\u0000\u0000\u0000\u0456\u00d9\u0001\u0000\u0000\u0000\u0457\u0458\u0003"+
		"\u00dcn\u0000\u0458\u0459\u0005\u011d\u0000\u0000\u0459\u045a\u0003\u00de"+
		"o\u0000\u045a\u045b\u0005\u011e\u0000\u0000\u045b\u00db\u0001\u0000\u0000"+
		"\u0000\u045c\u045d\u0005\u0092\u0000\u0000\u045d\u00dd\u0001\u0000\u0000"+
		"\u0000\u045e\u0460\u0003\u00e0p\u0000\u045f\u045e\u0001\u0000\u0000\u0000"+
		"\u045f\u0460\u0001\u0000\u0000\u0000\u0460\u0462\u0001\u0000\u0000\u0000"+
		"\u0461\u0463\u0003\u00d6k\u0000\u0462\u0461\u0001\u0000\u0000\u0000\u0462"+
		"\u0463\u0001\u0000\u0000\u0000\u0463\u0464\u0001\u0000\u0000\u0000\u0464"+
		"\u0466\u0005\u0011\u0000\u0000\u0465\u045f\u0001\u0000\u0000\u0000\u0465"+
		"\u0466\u0001\u0000\u0000\u0000\u0466\u0467\u0001\u0000\u0000\u0000\u0467"+
		"\u046d\u0003\u00c0`\u0000\u0468\u0469\u0003\u00c0`\u0000\u0469\u046a\u0005"+
		"\u0116\u0000\u0000\u046a\u046b\u0003\u00d6k\u0000\u046b\u046d\u0001\u0000"+
		"\u0000\u0000\u046c\u0465\u0001\u0000\u0000\u0000\u046c\u0468\u0001\u0000"+
		"\u0000\u0000\u046d\u00df\u0001\u0000\u0000\u0000\u046e\u046f\u0007\u000e"+
		"\u0000\u0000\u046f\u00e1\u0001\u0000\u0000\u0000\u0470\u0471\u0003\u00e4"+
		"r\u0000\u0471\u00e3\u0001\u0000\u0000\u0000\u0472\u0477\u0003\u00e6s\u0000"+
		"\u0473\u0474\u0005(\u0000\u0000\u0474\u0476\u0003\u00e6s\u0000\u0475\u0473"+
		"\u0001\u0000\u0000\u0000\u0476\u0479\u0001\u0000\u0000\u0000\u0477\u0475"+
		"\u0001\u0000\u0000\u0000\u0477\u0478\u0001\u0000\u0000\u0000\u0478\u00e5"+
		"\u0001\u0000\u0000\u0000\u0479\u0477\u0001\u0000\u0000\u0000\u047a\u047f"+
		"\u0003\u00e8t\u0000\u047b\u047c\u0005\u0003\u0000\u0000\u047c\u047e\u0003"+
		"\u00e8t\u0000\u047d\u047b\u0001\u0000\u0000\u0000\u047e\u0481\u0001\u0000"+
		"\u0000\u0000\u047f\u047d\u0001\u0000\u0000\u0000\u047f\u0480\u0001\u0000"+
		"\u0000\u0000\u0480\u00e7\u0001\u0000\u0000\u0000\u0481\u047f\u0001\u0000"+
		"\u0000\u0000\u0482\u0484\u0003\u0158\u00ac\u0000\u0483\u0482\u0001\u0000"+
		"\u0000\u0000\u0483\u0484\u0001\u0000\u0000\u0000\u0484\u0485\u0001\u0000"+
		"\u0000\u0000\u0485\u0486\u0003\u00eau\u0000\u0486\u00e9\u0001\u0000\u0000"+
		"\u0000\u0487\u0488\u0005\u011d\u0000\u0000\u0488\u0489\u0003\u00e2q\u0000"+
		"\u0489\u048a\u0005\u011e\u0000\u0000\u048a\u0490\u0001\u0000\u0000\u0000"+
		"\u048b\u048d\u0003\u00ecv\u0000\u048c\u048e\u0003\u0154\u00aa\u0000\u048d"+
		"\u048c\u0001\u0000\u0000\u0000\u048d\u048e\u0001\u0000\u0000\u0000\u048e"+
		"\u0490\u0001\u0000\u0000\u0000\u048f\u0487\u0001\u0000\u0000\u0000\u048f"+
		"\u048b\u0001\u0000\u0000\u0000\u0490\u00eb\u0001\u0000\u0000\u0000\u0491"+
		"\u0494\u0003\u00eew\u0000\u0492\u0494\u0003\u0082A\u0000\u0493\u0491\u0001"+
		"\u0000\u0000\u0000\u0493\u0492\u0001\u0000\u0000\u0000\u0494\u00ed\u0001"+
		"\u0000\u0000\u0000\u0495\u049d\u0003\u011e\u008f\u0000\u0496\u049d\u0003"+
		"\u0128\u0094\u0000\u0497\u049d\u0003\u012c\u0096\u0000\u0498\u049d\u0003"+
		"\u012e\u0097\u0000\u0499\u049d\u0003\u0150\u00a8\u0000\u049a\u049d\u0003"+
		"\u014a\u00a5\u0000\u049b\u049d\u0003\u00f0x\u0000\u049c\u0495\u0001\u0000"+
		"\u0000\u0000\u049c\u0496\u0001\u0000\u0000\u0000\u049c\u0497\u0001\u0000"+
		"\u0000\u0000\u049c\u0498\u0001\u0000\u0000\u0000\u049c\u0499\u0001\u0000"+
		"\u0000\u0000\u049c\u049a\u0001\u0000\u0000\u0000\u049c\u049b\u0001\u0000"+
		"\u0000\u0000\u049d\u00ef\u0001\u0000\u0000\u0000\u049e\u049f\u0003\u0176"+
		"\u00bb\u0000\u049f\u00f1\u0001\u0000\u0000\u0000\u04a0\u04a3\u0003\u0082"+
		"A\u0000\u04a1\u04a3\u0003\u009aM\u0000\u04a2\u04a0\u0001\u0000\u0000\u0000"+
		"\u04a2\u04a1\u0001\u0000\u0000\u0000\u04a3\u00f3\u0001\u0000\u0000\u0000"+
		"\u04a4\u04a8\u0003\u0082A\u0000\u04a5\u04a8\u0003\u00c2a\u0000\u04a6\u04a8"+
		"\u0003\u0176\u00bb\u0000\u04a7\u04a4\u0001\u0000\u0000\u0000\u04a7\u04a5"+
		"\u0001\u0000\u0000\u0000\u04a7\u04a6\u0001\u0000\u0000\u0000\u04a8\u00f5"+
		"\u0001\u0000\u0000\u0000\u04a9\u04aa\u00059\u0000\u0000\u04aa\u04ab\u0003"+
		"\u00f8|\u0000\u04ab\u00f7\u0001\u0000\u0000\u0000\u04ac\u04ad\u0003\u00c0"+
		"`\u0000\u04ad\u00f9\u0001\u0000\u0000\u0000\u04ae\u04af\u0005)\u0000\u0000"+
		"\u04af\u04b0\u0005?\u0000\u0000\u04b0\u04b1\u0003\u00fc~\u0000\u04b1\u00fb"+
		"\u0001\u0000\u0000\u0000\u04b2\u04b7\u0003\u00fe\u007f\u0000\u04b3\u04b4"+
		"\u0005\u0116\u0000\u0000\u04b4\u04b6\u0003\u00fe\u007f\u0000\u04b5\u04b3"+
		"\u0001\u0000\u0000\u0000\u04b6\u04b9\u0001\u0000\u0000\u0000\u04b7\u04b5"+
		"\u0001\u0000\u0000\u0000\u04b7\u04b8\u0001\u0000\u0000\u0000\u04b8\u00fd"+
		"\u0001\u0000\u0000\u0000\u04b9\u04b7\u0001\u0000\u0000\u0000\u04ba\u04bc"+
		"\u0003\u00f4z\u0000\u04bb\u04bd\u0003\u0100\u0080\u0000\u04bc\u04bb\u0001"+
		"\u0000\u0000\u0000\u04bc\u04bd\u0001\u0000\u0000\u0000\u04bd\u04bf\u0001"+
		"\u0000\u0000\u0000\u04be\u04c0\u0003\u0102\u0081\u0000\u04bf\u04be\u0001"+
		"\u0000\u0000\u0000\u04bf\u04c0\u0001\u0000\u0000\u0000\u04c0\u00ff\u0001"+
		"\u0000\u0000\u0000\u04c1\u04c2\u0007\u000f\u0000\u0000\u04c2\u0101\u0001"+
		"\u0000\u0000\u0000\u04c3\u04c4\u0005#\u0000\u0000\u04c4\u04c5\u0003\u0104"+
		"\u0082\u0000\u04c5\u0103\u0001\u0000\u0000\u0000\u04c6\u04c7\u0007\t\u0000"+
		"\u0000\u04c7\u0105\u0001\u0000\u0000\u0000\u04c8\u04c9\u0005\u001f\u0000"+
		"\u0000\u04c9\u04cc\u0003\u00c4b\u0000\u04ca\u04cb\u0005%\u0000\u0000\u04cb"+
		"\u04cd\u0003\u00c4b\u0000\u04cc\u04ca\u0001\u0000\u0000\u0000\u04cc\u04cd"+
		"\u0001\u0000\u0000\u0000\u04cd\u0107\u0001\u0000\u0000\u0000\u04ce\u04cf"+
		"\u0005\u0012\u0000\u0000\u04cf\u04d2\u0005?\u0000\u0000\u04d0\u04d3\u0003"+
		"\u010a\u0085\u0000\u04d1\u04d3\u0003N\'\u0000\u04d2\u04d0\u0001\u0000"+
		"\u0000\u0000\u04d2\u04d1\u0001\u0000\u0000\u0000\u04d3\u0109\u0001\u0000"+
		"\u0000\u0000\u04d4\u04d9\u0003\u010c\u0086\u0000\u04d5\u04d6\u0005\u0116"+
		"\u0000\u0000\u04d6\u04d8\u0003\u010c\u0086\u0000\u04d7\u04d5\u0001\u0000"+
		"\u0000\u0000\u04d8\u04db\u0001\u0000\u0000\u0000\u04d9\u04d7\u0001\u0000"+
		"\u0000\u0000\u04d9\u04da\u0001\u0000\u0000\u0000\u04da\u010b\u0001\u0000"+
		"\u0000\u0000\u04db\u04d9\u0001\u0000\u0000\u0000\u04dc\u04e1\u0003\u0112"+
		"\u0089\u0000\u04dd\u04e1\u0003\u0114\u008a\u0000\u04de\u04e1\u0003\u0116"+
		"\u008b\u0000\u04df\u04e1\u0003\u0110\u0088\u0000\u04e0\u04dc\u0001\u0000"+
		"\u0000\u0000\u04e0\u04dd\u0001\u0000\u0000\u0000\u04e0\u04de\u0001\u0000"+
		"\u0000\u0000\u04e0\u04df\u0001\u0000\u0000\u0000\u04e1\u010d\u0001\u0000"+
		"\u0000\u0000\u04e2\u04e7\u0003\u0110\u0088\u0000\u04e3\u04e4\u0005\u0116"+
		"\u0000\u0000\u04e4\u04e6\u0003\u0110\u0088\u0000\u04e5\u04e3\u0001\u0000"+
		"\u0000\u0000\u04e6\u04e9\u0001\u0000\u0000\u0000\u04e7\u04e5\u0001\u0000"+
		"\u0000\u0000\u04e7\u04e8\u0001\u0000\u0000\u0000\u04e8\u010f\u0001\u0000"+
		"\u0000\u0000\u04e9\u04e7\u0001\u0000\u0000\u0000\u04ea\u04f0\u0003\u00f4"+
		"z\u0000\u04eb\u04ec\u0005\u011d\u0000\u0000\u04ec\u04ed\u0003\u011c\u008e"+
		"\u0000\u04ed\u04ee\u0005\u011e\u0000\u0000\u04ee\u04f0\u0001\u0000\u0000"+
		"\u0000\u04ef\u04ea\u0001\u0000\u0000\u0000\u04ef\u04eb\u0001\u0000\u0000"+
		"\u0000\u04f0\u0111\u0001\u0000\u0000\u0000\u04f1\u04f2\u0005\u0082\u0000"+
		"\u0000\u04f2\u04f3\u0005\u011d\u0000\u0000\u04f3\u04f4\u0003\u010e\u0087"+
		"\u0000\u04f4\u04f5\u0005\u011e\u0000\u0000\u04f5\u0113\u0001\u0000\u0000"+
		"\u0000\u04f6\u04f7\u0005G\u0000\u0000\u04f7\u04f8\u0005\u011d\u0000\u0000"+
		"\u04f8\u04f9\u0003\u010e\u0087\u0000\u04f9\u04fa\u0005\u011e\u0000\u0000"+
		"\u04fa\u0115\u0001\u0000\u0000\u0000\u04fb\u04fc\u0005\u011d\u0000\u0000"+
		"\u04fc\u04fd\u0005\u011e\u0000\u0000\u04fd\u0117\u0001\u0000\u0000\u0000"+
		"\u04fe\u04ff\u0005\u0013\u0000\u0000\u04ff\u0500\u0003\u00e2q\u0000\u0500"+
		"\u0119\u0001\u0000\u0000\u0000\u0501\u0502\u0005\u0148\u0000\u0000\u0502"+
		"\u0503\u0003\u00f8|\u0000\u0503\u011b\u0001\u0000\u0000\u0000\u0504\u0509"+
		"\u0003\u00f4z\u0000\u0505\u0506\u0005\u0116\u0000\u0000\u0506\u0508\u0003"+
		"\u00f4z\u0000\u0507\u0505\u0001\u0000\u0000\u0000\u0508\u050b\u0001\u0000"+
		"\u0000\u0000\u0509\u0507\u0001\u0000\u0000\u0000\u0509\u050a\u0001\u0000"+
		"\u0000\u0000\u050a\u011d\u0001\u0000\u0000\u0000\u050b\u0509\u0001\u0000"+
		"\u0000\u0000\u050c\u050d\u0003\u00f4z\u0000\u050d\u050e\u0003\u0120\u0090"+
		"\u0000\u050e\u050f\u0003\u00f4z\u0000\u050f\u011f\u0001\u0000\u0000\u0000"+
		"\u0510\u0517\u0003\u0126\u0093\u0000\u0511\u0513\u0003\u0158\u00ac\u0000"+
		"\u0512\u0511\u0001\u0000\u0000\u0000\u0512\u0513\u0001\u0000\u0000\u0000"+
		"\u0513\u0514\u0001\u0000\u0000\u0000\u0514\u0517\u0003\u0122\u0091\u0000"+
		"\u0515\u0517\u0003\u0124\u0092\u0000\u0516\u0510\u0001\u0000\u0000\u0000"+
		"\u0516\u0512\u0001\u0000\u0000\u0000\u0516\u0515\u0001\u0000\u0000\u0000"+
		"\u0517\u0121\u0001\u0000\u0000\u0000\u0518\u051f\u0005\u001e\u0000\u0000"+
		"\u0519\u051f\u0005\u0015\u0000\u0000\u051a\u051b\u0005\u0088\u0000\u0000"+
		"\u051b\u051f\u0005\u0093\u0000\u0000\u051c\u051f\u0005\u0080\u0000\u0000"+
		"\u051d\u051f\u0005\u0081\u0000\u0000\u051e\u0518\u0001\u0000\u0000\u0000"+
		"\u051e\u0519\u0001\u0000\u0000\u0000\u051e\u051a\u0001\u0000\u0000\u0000"+
		"\u051e\u051c\u0001\u0000\u0000\u0000\u051e\u051d\u0001\u0000\u0000\u0000"+
		"\u051f\u0123\u0001\u0000\u0000\u0000\u0520\u0521\u0007\u0010\u0000\u0000"+
		"\u0521\u0125\u0001\u0000\u0000\u0000\u0522\u0523\u0007\u0011\u0000\u0000"+
		"\u0523\u0127\u0001\u0000\u0000\u0000\u0524\u0526\u0003\u00f4z\u0000\u0525"+
		"\u0527\u0003\u0158\u00ac\u0000\u0526\u0525\u0001\u0000\u0000\u0000\u0526"+
		"\u0527\u0001\u0000\u0000\u0000\u0527\u0528\u0001\u0000\u0000\u0000\u0528"+
		"\u052a\u0005>\u0000\u0000\u0529\u052b\u0003\u012a\u0095\u0000\u052a\u0529"+
		"\u0001\u0000\u0000\u0000\u052a\u052b\u0001\u0000\u0000\u0000\u052b\u052c"+
		"\u0001\u0000\u0000\u0000\u052c\u052d\u0003\u00f4z\u0000\u052d\u052e\u0005"+
		"\u0003\u0000\u0000\u052e\u052f\u0003\u00f4z\u0000\u052f\u0129\u0001\u0000"+
		"\u0000\u0000\u0530\u0531\u0007\u0012\u0000\u0000\u0531\u012b\u0001\u0000"+
		"\u0000\u0000\u0532\u0534\u0003\u00f4z\u0000\u0533\u0535\u0003\u0158\u00ac"+
		"\u0000\u0534\u0533\u0001\u0000\u0000\u0000\u0534\u0535\u0001\u0000\u0000"+
		"\u0000\u0535\u0536\u0001\u0000\u0000\u0000\u0536\u0537\u0005\u0016\u0000"+
		"\u0000\u0537\u0538\u0003\u0132\u0099\u0000\u0538\u012d\u0001\u0000\u0000"+
		"\u0000\u0539\u053b\u0003\u00f4z\u0000\u053a\u053c\u0003\u0158\u00ac\u0000"+
		"\u053b\u053a\u0001\u0000\u0000\u0000\u053b\u053c\u0001\u0000\u0000\u0000"+
		"\u053c\u053d\u0001\u0000\u0000\u0000\u053d\u053e\u0003\u0130\u0098\u0000"+
		"\u053e\u0540\u0003\u0132\u0099\u0000\u053f\u0541\u0003\u0136\u009b\u0000"+
		"\u0540\u053f\u0001\u0000\u0000\u0000\u0540\u0541\u0001\u0000\u0000\u0000"+
		"\u0541\u012f\u0001\u0000\u0000\u0000\u0542\u0543\u0007\u0013\u0000\u0000"+
		"\u0543\u0544\u0005\u0004\u0000\u0000\u0544\u0131\u0001\u0000\u0000\u0000"+
		"\u0545\u054c\u0003F#\u0000\u0546\u0547\u0005\u011d\u0000\u0000\u0547\u0548"+
		"\u0003\u0134\u009a\u0000\u0548\u0549\u0005\u011e\u0000\u0000\u0549\u054c"+
		"\u0001\u0000\u0000\u0000\u054a\u054c\u0003\u0176\u00bb\u0000\u054b\u0545"+
		"\u0001\u0000\u0000\u0000\u054b\u0546\u0001\u0000\u0000\u0000\u054b\u054a"+
		"\u0001\u0000\u0000\u0000\u054c\u0133\u0001\u0000\u0000\u0000\u054d\u0552"+
		"\u0003\u00f2y\u0000\u054e\u054f\u0005\u0116\u0000\u0000\u054f\u0551\u0003"+
		"\u00f2y\u0000\u0550\u054e\u0001\u0000\u0000\u0000\u0551\u0554\u0001\u0000"+
		"\u0000\u0000\u0552\u0550\u0001\u0000\u0000\u0000\u0552\u0553\u0001\u0000"+
		"\u0000\u0000\u0553\u0135\u0001\u0000\u0000\u0000\u0554\u0552\u0001\u0000"+
		"\u0000\u0000\u0555\u0556\u0005Q\u0000\u0000\u0556\u0557\u0005\u0151\u0000"+
		"\u0000\u0557\u0137\u0001\u0000\u0000\u0000\u0558\u055c\u0003\u013a\u009d"+
		"\u0000\u0559\u055c\u0003\u013c\u009e\u0000\u055a\u055c\u0003\u013e\u009f"+
		"\u0000\u055b\u0558\u0001\u0000\u0000\u0000\u055b\u0559\u0001\u0000\u0000"+
		"\u0000\u055b\u055a\u0001\u0000\u0000\u0000\u055c\u0139\u0001\u0000\u0000"+
		"\u0000\u055d\u055e\u0003\u013e\u009f\u0000\u055e\u055f\u0003R)\u0000\u055f"+
		"\u0560\u0003\u0144\u00a2\u0000\u0560\u013b\u0001\u0000\u0000\u0000\u0561"+
		"\u0562\u0003\u013e\u009f\u0000\u0562\u0563\u0003R)\u0000\u0563\u013d\u0001"+
		"\u0000\u0000\u0000\u0564\u0565\u0005\u011d\u0000\u0000\u0565\u0566\u0005"+
		"\u0097\u0000\u0000\u0566\u0567\u0003\u0140\u00a0\u0000\u0567\u0568\u0005"+
		"\u011e\u0000\u0000\u0568\u013f\u0001\u0000\u0000\u0000\u0569\u056e\u0003"+
		"\u0142\u00a1\u0000\u056a\u056b\u0005\u0116\u0000\u0000\u056b\u056d\u0003"+
		"\u0142\u00a1\u0000\u056c\u056a\u0001\u0000\u0000\u0000\u056d\u0570\u0001"+
		"\u0000\u0000\u0000\u056e\u056c\u0001\u0000\u0000\u0000\u056e\u056f\u0001"+
		"\u0000\u0000\u0000\u056f\u0141\u0001\u0000\u0000\u0000\u0570\u056e\u0001"+
		"\u0000\u0000\u0000\u0571\u0572\u0005\u011d\u0000\u0000\u0572\u0573\u0003"+
		"\u0134\u009a\u0000\u0573\u0574\u0005\u011e\u0000\u0000\u0574\u0143\u0001"+
		"\u0000\u0000\u0000\u0575\u0576\u0005\u011d\u0000\u0000\u0576\u0577\u0003"+
		"\u0146\u00a3\u0000\u0577\u0578\u0005\u011e\u0000\u0000\u0578\u0145\u0001"+
		"\u0000\u0000\u0000\u0579\u057e\u0003\u0174\u00ba\u0000\u057a\u057b\u0005"+
		"\u0116\u0000\u0000\u057b\u057d\u0003\u0174\u00ba\u0000\u057c\u057a\u0001"+
		"\u0000\u0000\u0000\u057d\u0580\u0001\u0000\u0000\u0000\u057e\u057c\u0001"+
		"\u0000\u0000\u0000\u057e\u057f\u0001\u0000\u0000\u0000\u057f\u0147\u0001"+
		"\u0000\u0000\u0000\u0580\u057e\u0001\u0000\u0000\u0000\u0581\u0582\u0005"+
		"\u0097\u0000\u0000\u0582\u0583\u0003\u0140\u00a0\u0000\u0583\u0149\u0001"+
		"\u0000\u0000\u0000\u0584\u0585\u0003\u014c\u00a6\u0000\u0585\u0586\u0003"+
		"\u014e\u00a7\u0000\u0586\u014b\u0001\u0000\u0000\u0000\u0587\u0588\u0005"+
		"S\u0000\u0000\u0588\u014d\u0001\u0000\u0000\u0000\u0589\u058c\u0003F#"+
		"\u0000\u058a\u058c\u0003\u0176\u00bb\u0000\u058b\u0589\u0001\u0000\u0000"+
		"\u0000\u058b\u058a\u0001\u0000\u0000\u0000\u058c\u014f\u0001\u0000\u0000"+
		"\u0000\u058d\u058e\u0003\u00f4z\u0000\u058e\u058f\u0003\u0152\u00a9\u0000"+
		"\u058f\u0151\u0001\u0000\u0000\u0000\u0590\u0592\u0005\u001a\u0000\u0000"+
		"\u0591\u0593\u0005!\u0000\u0000\u0592\u0591\u0001\u0000\u0000\u0000\u0592"+
		"\u0593\u0001\u0000\u0000\u0000\u0593\u0594\u0001\u0000\u0000\u0000\u0594"+
		"\u0595\u0005\"\u0000\u0000\u0595\u0153\u0001\u0000\u0000\u0000\u0596\u0598"+
		"\u0005\u001a\u0000\u0000\u0597\u0599\u0003\u0158\u00ac\u0000\u0598\u0597"+
		"\u0001\u0000\u0000\u0000\u0598\u0599\u0001\u0000\u0000\u0000\u0599\u059a"+
		"\u0001\u0000\u0000\u0000\u059a\u059b\u0003\u0156\u00ab\u0000\u059b\u0155"+
		"\u0001\u0000\u0000\u0000\u059c\u059d\u0007\u0014\u0000\u0000\u059d\u0157"+
		"\u0001\u0000\u0000\u0000\u059e\u059f\u0005!\u0000\u0000\u059f\u0159\u0001"+
		"\u0000\u0000\u0000\u05a0\u05a1\u0003\u00c4b\u0000\u05a1\u05a2\u0003\u0126"+
		"\u0093\u0000\u05a2\u05a3\u0003\u015c\u00ae\u0000\u05a3\u05a4\u0003F#\u0000"+
		"\u05a4\u015b\u0001\u0000\u0000\u0000\u05a5\u05a8\u0003\u015e\u00af\u0000"+
		"\u05a6\u05a8\u0003\u0160\u00b0\u0000\u05a7\u05a5\u0001\u0000\u0000\u0000"+
		"\u05a7\u05a6\u0001\u0000\u0000\u0000\u05a8\u015d\u0001\u0000\u0000\u0000"+
		"\u05a9\u05aa\u0005\u0002\u0000\u0000\u05aa\u015f\u0001\u0000\u0000\u0000"+
		"\u05ab\u05ac\u0007\u0015\u0000\u0000\u05ac\u0161\u0001\u0000\u0000\u0000"+
		"\u05ad\u05ae\u00056\u0000\u0000\u05ae\u05af\u0003F#\u0000\u05af\u0163"+
		"\u0001\u0000\u0000\u0000\u05b0\u05b3\u0003\u0166\u00b3\u0000\u05b1\u05b3"+
		"\u0005\u0086\u0000\u0000\u05b2\u05b0\u0001\u0000\u0000\u0000\u05b2\u05b1"+
		"\u0001\u0000\u0000\u0000\u05b3\u0165\u0001\u0000\u0000\u0000\u05b4\u05b5"+
		"\u0007\u0016\u0000\u0000\u05b5\u0167\u0001\u0000\u0000\u0000\u05b6\u05b7"+
		"\u0007\u0017\u0000\u0000\u05b7\u0169\u0001\u0000\u0000\u0000\u05b8\u05b9"+
		"\u0003\u016c\u00b6\u0000\u05b9\u05bb\u0005\u011d\u0000\u0000\u05ba\u05bc"+
		"\u0003\u0170\u00b8\u0000\u05bb\u05ba\u0001\u0000\u0000\u0000\u05bb\u05bc"+
		"\u0001\u0000\u0000\u0000\u05bc\u05bd\u0001\u0000\u0000\u0000\u05bd\u05be"+
		"\u0005\u011e\u0000\u0000\u05be\u016b\u0001\u0000\u0000\u0000\u05bf\u05c2"+
		"\u0003\u0172\u00b9\u0000\u05c0\u05c1\u0005\u0124\u0000\u0000\u05c1\u05c3"+
		"\u0003\u0172\u00b9\u0000\u05c2\u05c0\u0001\u0000\u0000\u0000\u05c2\u05c3"+
		"\u0001\u0000\u0000\u0000\u05c3\u05c6\u0001\u0000\u0000\u0000\u05c4\u05c6"+
		"\u0003\u016e\u00b7\u0000\u05c5\u05bf\u0001\u0000\u0000\u0000\u05c5\u05c4"+
		"\u0001\u0000\u0000\u0000\u05c6\u016d\u0001\u0000\u0000\u0000\u05c7\u05c8"+
		"\u0007\u0018\u0000\u0000\u05c8\u016f\u0001\u0000\u0000\u0000\u05c9\u05ce"+
		"\u0003\u00c0`\u0000\u05ca\u05cb\u0005\u0116\u0000\u0000\u05cb\u05cd\u0003"+
		"\u00c0`\u0000\u05cc\u05ca\u0001\u0000\u0000\u0000\u05cd\u05d0\u0001\u0000"+
		"\u0000\u0000\u05ce\u05cc\u0001\u0000\u0000\u0000\u05ce\u05cf\u0001\u0000"+
		"\u0000\u0000\u05cf\u0171\u0001\u0000\u0000\u0000\u05d0\u05ce\u0001\u0000"+
		"\u0000\u0000\u05d1\u05d7\u0003\u0178\u00bc\u0000\u05d2\u05d7\u0003\u017a"+
		"\u00bd\u0000\u05d3\u05d7\u0003\u0192\u00c9\u0000\u05d4\u05d7\u0003\u018e"+
		"\u00c7\u0000\u05d5\u05d7\u0003\u0190\u00c8\u0000\u05d6\u05d1\u0001\u0000"+
		"\u0000\u0000\u05d6\u05d2\u0001\u0000\u0000\u0000\u05d6\u05d3\u0001\u0000"+
		"\u0000\u0000\u05d6\u05d4\u0001\u0000\u0000\u0000\u05d6\u05d5\u0001\u0000"+
		"\u0000\u0000\u05d7\u0173\u0001\u0000\u0000\u0000\u05d8\u05de\u0003\u0178"+
		"\u00bc\u0000\u05d9\u05de\u0003\u017a\u00bd\u0000\u05da\u05de\u0003\u0192"+
		"\u00c9\u0000\u05db\u05de\u0003\u018c\u00c6\u0000\u05dc\u05de\u0003\u018e"+
		"\u00c7\u0000\u05dd\u05d8\u0001\u0000\u0000\u0000\u05dd\u05d9\u0001\u0000"+
		"\u0000\u0000\u05dd\u05da\u0001\u0000\u0000\u0000\u05dd\u05db\u0001\u0000"+
		"\u0000\u0000\u05dd\u05dc\u0001\u0000\u0000\u0000\u05de\u0175\u0001\u0000"+
		"\u0000\u0000\u05df\u05e2\u0003\u017c\u00be\u0000\u05e0\u05e2\u0003\u017e"+
		"\u00bf\u0000\u05e1\u05df\u0001\u0000\u0000\u0000\u05e1\u05e0\u0001\u0000"+
		"\u0000\u0000\u05e2\u0177\u0001\u0000\u0000\u0000\u05e3\u05e4\u0005\u0149"+
		"\u0000\u0000\u05e4\u0179\u0001\u0000\u0000\u0000\u05e5\u05e6\u0005\u0144"+
		"\u0000\u0000\u05e6\u017b\u0001\u0000\u0000\u0000\u05e7\u05e8\u0005\u0145"+
		"\u0000\u0000\u05e8\u017d\u0001\u0000\u0000\u0000\u05e9\u05ea\u0007\u0019"+
		"\u0000\u0000\u05ea\u017f\u0001\u0000\u0000\u0000\u05eb\u05ec\u0005\u0154"+
		"\u0000\u0000\u05ec\u05ed\u0003\u0182\u00c1\u0000\u05ed\u05ee\u0005\u0155"+
		"\u0000\u0000\u05ee\u05f4\u0001\u0000\u0000\u0000\u05ef\u05f0\u0005\u0154"+
		"\u0000\u0000\u05f0\u05f1\u0003\u0188\u00c4\u0000\u05f1\u05f2\u0005\u0155"+
		"\u0000\u0000\u05f2\u05f4\u0001\u0000\u0000\u0000\u05f3\u05eb\u0001\u0000"+
		"\u0000\u0000\u05f3\u05ef\u0001\u0000\u0000\u0000\u05f4\u0181\u0001\u0000"+
		"\u0000\u0000\u05f5\u05f6\u0003\u0172\u00b9\u0000\u05f6\u05f7\u0004\u00c1"+
		"\u0001\u0001\u05f7\u05f9\u0005\u011d\u0000\u0000\u05f8\u05fa\u0003\u0184"+
		"\u00c2\u0000\u05f9\u05f8\u0001\u0000\u0000\u0000\u05f9\u05fa\u0001\u0000"+
		"\u0000\u0000\u05fa\u05fb\u0001\u0000\u0000\u0000\u05fb\u05fc\u0005\u011e"+
		"\u0000\u0000\u05fc\u0608\u0001\u0000\u0000\u0000\u05fd\u05fe\u0003\u0172"+
		"\u00b9\u0000\u05fe\u05ff\u0005\u0124\u0000\u0000\u05ff\u0600\u0003\u0172"+
		"\u00b9\u0000\u0600\u0601\u0004\u00c1\u0002\u0001\u0601\u0603\u0005\u011d"+
		"\u0000\u0000\u0602\u0604\u0003\u0184\u00c2\u0000\u0603\u0602\u0001\u0000"+
		"\u0000\u0000\u0603\u0604\u0001\u0000\u0000\u0000\u0604\u0605\u0001\u0000"+
		"\u0000\u0000\u0605\u0606\u0005\u011e\u0000\u0000\u0606\u0608\u0001\u0000"+
		"\u0000\u0000\u0607\u05f5\u0001\u0000\u0000\u0000\u0607\u05fd\u0001\u0000"+
		"\u0000\u0000\u0608\u0183\u0001\u0000\u0000\u0000\u0609\u060e\u0003\u0186"+
		"\u00c3\u0000\u060a\u060b\u0005\u0116\u0000\u0000\u060b\u060d\u0003\u0186"+
		"\u00c3\u0000\u060c\u060a\u0001\u0000\u0000\u0000\u060d\u0610\u0001\u0000"+
		"\u0000\u0000\u060e\u060c\u0001\u0000\u0000\u0000\u060e\u060f\u0001\u0000"+
		"\u0000\u0000\u060f\u0185\u0001\u0000\u0000\u0000\u0610\u060e\u0001\u0000"+
		"\u0000\u0000\u0611\u061c\u0005\u0151\u0000\u0000\u0612\u061c\u0005\u012a"+
		"\u0000\u0000\u0613\u0614\u0003\u0172\u00b9\u0000\u0614\u0615\u0005\u0113"+
		"\u0000\u0000\u0615\u0616\u0005\u0151\u0000\u0000\u0616\u061c\u0001\u0000"+
		"\u0000\u0000\u0617\u0618\u0003\u0172\u00b9\u0000\u0618\u0619\u0005\u0113"+
		"\u0000\u0000\u0619\u061a\u0005\u012a\u0000\u0000\u061a\u061c\u0001\u0000"+
		"\u0000\u0000\u061b\u0611\u0001\u0000\u0000\u0000\u061b\u0612\u0001\u0000"+
		"\u0000\u0000\u061b\u0613\u0001\u0000\u0000\u0000\u061b\u0617\u0001\u0000"+
		"\u0000\u0000\u061c\u0187\u0001\u0000\u0000\u0000\u061d\u0622\u0003\u018a"+
		"\u00c5\u0000\u061e\u061f\u0005\u0124\u0000\u0000\u061f\u0621\u0003\u018a"+
		"\u00c5\u0000\u0620\u061e\u0001\u0000\u0000\u0000\u0621\u0624\u0001\u0000"+
		"\u0000\u0000\u0622\u0620\u0001\u0000\u0000\u0000\u0622\u0623\u0001\u0000"+
		"\u0000\u0000\u0623\u0189\u0001\u0000\u0000\u0000\u0624\u0622\u0001\u0000"+
		"\u0000\u0000\u0625\u0628\u0003\u0172\u00b9\u0000\u0626\u0628\u0005\u012a"+
		"\u0000\u0000\u0627\u0625\u0001\u0000\u0000\u0000\u0627\u0626\u0001\u0000"+
		"\u0000\u0000\u0628\u018b\u0001\u0000\u0000\u0000\u0629\u062a\u0007\u001a"+
		"\u0000\u0000\u062a\u018d\u0001\u0000\u0000\u0000\u062b\u062c\u0005\u014d"+
		"\u0000\u0000\u062c\u018f\u0001\u0000\u0000\u0000\u062d\u062e\u0005\u014e"+
		"\u0000\u0000\u062e\u0191\u0001\u0000\u0000\u0000\u062f\u0630\u0007\u001b"+
		"\u0000\u0000\u0630\u0193\u0001\u0000\u0000\u0000\u0631\u0633\u0003\u00cc"+
		"f\u0000\u0632\u0631\u0001\u0000\u0000\u0000\u0632\u0633\u0001\u0000\u0000"+
		"\u0000\u0633\u0634\u0001\u0000\u0000\u0000\u0634\u0635\u0003\u0198\u00cc"+
		"\u0000\u0635\u0195\u0001\u0000\u0000\u0000\u0636\u0639\u0003\u0198\u00cc"+
		"\u0000\u0637\u0639\u0003\u019e\u00cf\u0000\u0638\u0636\u0001\u0000\u0000"+
		"\u0000\u0638\u0637\u0001\u0000\u0000\u0000\u0639\u0197\u0001\u0000\u0000"+
		"\u0000\u063a\u063d\u0005\u012a\u0000\u0000\u063b\u063d\u0003\u019a\u00cd"+
		"\u0000\u063c\u063a\u0001\u0000\u0000\u0000\u063c\u063b\u0001\u0000\u0000"+
		"\u0000\u063d\u0199\u0001\u0000\u0000\u0000\u063e\u063f\u0005\u012a\u0000"+
		"\u0000\u063f\u0641\u0005\u0124\u0000\u0000\u0640\u0642\u0005\u012a\u0000"+
		"\u0000\u0641\u0640\u0001\u0000\u0000\u0000\u0641\u0642\u0001\u0000\u0000"+
		"\u0000\u0642\u0644\u0001\u0000\u0000\u0000\u0643\u0645\u0003\u019c\u00ce"+
		"\u0000\u0644\u0643\u0001\u0000\u0000\u0000\u0644\u0645\u0001\u0000\u0000"+
		"\u0000\u0645\u064f\u0001\u0000\u0000\u0000\u0646\u0647\u0005\u0124\u0000"+
		"\u0000\u0647\u0649\u0005\u012a\u0000\u0000\u0648\u064a\u0003\u019c\u00ce"+
		"\u0000\u0649\u0648\u0001\u0000\u0000\u0000\u0649\u064a\u0001\u0000\u0000"+
		"\u0000\u064a\u064f\u0001\u0000\u0000\u0000\u064b\u064c\u0005\u012a\u0000"+
		"\u0000\u064c\u064f\u0003\u019c\u00ce\u0000\u064d\u064f\u0005\u014b\u0000"+
		"\u0000\u064e\u063e\u0001\u0000\u0000\u0000\u064e\u0646\u0001\u0000\u0000"+
		"\u0000\u064e\u064b\u0001\u0000\u0000\u0000\u064e\u064d\u0001\u0000\u0000"+
		"\u0000\u064f\u019b\u0001\u0000\u0000\u0000\u0650\u0651\u0005\u014a\u0000"+
		"\u0000\u0651\u0652\u0005\u012a\u0000\u0000\u0652\u019d\u0001\u0000\u0000"+
		"\u0000\u0653\u0657\u0003\u01a0\u00d0\u0000\u0654\u0657\u0003\u01a2\u00d1"+
		"\u0000\u0655\u0657\u0003\u01aa\u00d5\u0000\u0656\u0653\u0001\u0000\u0000"+
		"\u0000\u0656\u0654\u0001\u0000\u0000\u0000\u0656\u0655\u0001\u0000\u0000"+
		"\u0000\u0657\u019f\u0001\u0000\u0000\u0000\u0658\u0659\u0005\u0151\u0000"+
		"\u0000\u0659\u01a1\u0001\u0000\u0000\u0000\u065a\u065e\u0003\u01a6\u00d3"+
		"\u0000\u065b\u065e\u0003\u01a4\u00d2\u0000\u065c\u065e\u0003\u01a8\u00d4"+
		"\u0000\u065d\u065a\u0001\u0000\u0000\u0000\u065d\u065b\u0001\u0000\u0000"+
		"\u0000\u065d\u065c\u0001\u0000\u0000\u0000\u065e\u01a3\u0001\u0000\u0000"+
		"\u0000\u065f\u0660\u0005\u00ff\u0000\u0000\u0660\u0661\u0005\u0151\u0000"+
		"\u0000\u0661\u01a5\u0001\u0000\u0000\u0000\u0662\u0663\u0005\u0101\u0000"+
		"\u0000\u0663\u0664\u0005\u0151\u0000\u0000\u0664\u01a7\u0001\u0000\u0000"+
		"\u0000\u0665\u0666\u0005\u00fd\u0000\u0000\u0666\u0667\u0005\u0151\u0000"+
		"\u0000\u0667\u01a9\u0001\u0000\u0000\u0000\u0668\u0669\u0007\u0014\u0000"+
		"\u0000\u0669\u01ab\u0001\u0000\u0000\u0000\u066a\u066e\u0003\u01ae\u00d7"+
		"\u0000\u066b\u066e\u0003\u01b4\u00da\u0000\u066c\u066e\u0003\u01ba\u00dd"+
		"\u0000\u066d\u066a\u0001\u0000\u0000\u0000\u066d\u066b\u0001\u0000\u0000"+
		"\u0000\u066d\u066c\u0001\u0000\u0000\u0000\u066e\u01ad\u0001\u0000\u0000"+
		"\u0000\u066f\u0671\u0003\u01b0\u00d8\u0000\u0670\u0672\u0003\u01b2\u00d9"+
		"\u0000\u0671\u0670\u0001\u0000\u0000\u0000\u0671\u0672\u0001\u0000\u0000"+
		"\u0000\u0672\u01af\u0001\u0000\u0000\u0000\u0673\u0696\u0005A\u0000\u0000"+
		"\u0674\u0696\u0005\u00f7\u0000\u0000\u0675\u0676\u0005A\u0000\u0000\u0676"+
		"\u0696\u0005\u009a\u0000\u0000\u0677\u0678\u0005\u00f7\u0000\u0000\u0678"+
		"\u0696\u0005\u009a\u0000\u0000\u0679\u0696\u0005\u00f8\u0000\u0000\u067a"+
		"\u0696\u0005\u00f9\u0000\u0000\u067b\u067c\u0005s\u0000\u0000\u067c\u0696"+
		"\u0005A\u0000\u0000\u067d\u067e\u0005s\u0000\u0000\u067e\u0696\u0005\u00f7"+
		"\u0000\u0000\u067f\u0696\u0005\u00fa\u0000\u0000\u0680\u0681\u0005s\u0000"+
		"\u0000\u0681\u0682\u0005A\u0000\u0000\u0682\u0696\u0005\u009a\u0000\u0000"+
		"\u0683\u0684\u0005s\u0000\u0000\u0684\u0685\u0005\u00f7\u0000\u0000\u0685"+
		"\u0696\u0005\u009a\u0000\u0000\u0686\u0687\u0005\u00fa\u0000\u0000\u0687"+
		"\u0696\u0005\u009a\u0000\u0000\u0688\u0696\u0005\u00fb\u0000\u0000\u0689"+
		"\u0696\u0005\u0109\u0000\u0000\u068a\u0696\u0005\u010a\u0000\u0000\u068b"+
		"\u0696\u0005\u00d3\u0000\u0000\u068c\u0696\u0005\u00d4\u0000\u0000\u068d"+
		"\u068e\u0005\u00d3\u0000\u0000\u068e\u0696\u0005\u009a\u0000\u0000\u068f"+
		"\u0696\u0005\u0107\u0000\u0000\u0690\u0691\u0005\u0107\u0000\u0000\u0691"+
		"\u0696\u0005\u009a\u0000\u0000\u0692\u0696\u0005\u0108\u0000\u0000\u0693"+
		"\u0696\u0005\u00d8\u0000\u0000\u0694\u0696\u0005\u00fc\u0000\u0000\u0695"+
		"\u0673\u0001\u0000\u0000\u0000\u0695\u0674\u0001\u0000\u0000\u0000\u0695"+
		"\u0675\u0001\u0000\u0000\u0000\u0695\u0677\u0001\u0000\u0000\u0000\u0695"+
		"\u0679\u0001\u0000\u0000\u0000\u0695\u067a\u0001\u0000\u0000\u0000\u0695"+
		"\u067b\u0001\u0000\u0000\u0000\u0695\u067d\u0001\u0000\u0000\u0000\u0695"+
		"\u067f\u0001\u0000\u0000\u0000\u0695\u0680\u0001\u0000\u0000\u0000\u0695"+
		"\u0683\u0001\u0000\u0000\u0000\u0695\u0686\u0001\u0000\u0000\u0000\u0695"+
		"\u0688\u0001\u0000\u0000\u0000\u0695\u0689\u0001\u0000\u0000\u0000\u0695"+
		"\u068a\u0001\u0000\u0000\u0000\u0695\u068b\u0001\u0000\u0000\u0000\u0695"+
		"\u068c\u0001\u0000\u0000\u0000\u0695\u068d\u0001\u0000\u0000\u0000\u0695"+
		"\u068f\u0001\u0000\u0000\u0000\u0695\u0690\u0001\u0000\u0000\u0000\u0695"+
		"\u0692\u0001\u0000\u0000\u0000\u0695\u0693\u0001\u0000\u0000\u0000\u0695"+
		"\u0694\u0001\u0000\u0000\u0000\u0696\u01b1\u0001\u0000\u0000\u0000\u0697"+
		"\u0698\u0005\u011d\u0000\u0000\u0698\u0699\u0005\u012a\u0000\u0000\u0699"+
		"\u069a\u0005\u011e\u0000\u0000\u069a\u01b3\u0001\u0000\u0000\u0000\u069b"+
		"\u069d\u0003\u01b6\u00db\u0000\u069c\u069e\u0003\u01b8\u00dc\u0000\u069d"+
		"\u069c\u0001\u0000\u0000\u0000\u069d\u069e\u0001\u0000\u0000\u0000\u069e"+
		"\u01b5\u0001\u0000\u0000\u0000\u069f\u06ab\u0005\u00f5\u0000\u0000\u06a0"+
		"\u06ab\u0005\u012a\u0000\u0000\u06a1\u06ab\u0005\u00f6\u0000\u0000\u06a2"+
		"\u06ab\u0005J\u0000\u0000\u06a3\u06ab\u0005\u00f3\u0000\u0000\u06a4\u06ab"+
		"\u0005\u00f4\u0000\u0000\u06a5\u06a6\u0005\u00f4\u0000\u0000\u06a6\u06ab"+
		"\u0005{\u0000\u0000\u06a7\u06ab\u0005\u0102\u0000\u0000\u06a8\u06ab\u0005"+
		"\u0103\u0000\u0000\u06a9\u06ab\u0005\u0104\u0000\u0000\u06aa\u069f\u0001"+
		"\u0000\u0000\u0000\u06aa\u06a0\u0001\u0000\u0000\u0000\u06aa\u06a1\u0001"+
		"\u0000\u0000\u0000\u06aa\u06a2\u0001\u0000\u0000\u0000\u06aa\u06a3\u0001"+
		"\u0000\u0000\u0000\u06aa\u06a4\u0001\u0000\u0000\u0000\u06aa\u06a5\u0001"+
		"\u0000\u0000\u0000\u06aa\u06a7\u0001\u0000\u0000\u0000\u06aa\u06a8\u0001"+
		"\u0000\u0000\u0000\u06aa\u06a9\u0001\u0000\u0000\u0000\u06ab\u01b7\u0001"+
		"\u0000\u0000\u0000\u06ac\u06ad\u0005\u011d\u0000\u0000\u06ad\u06ae\u0005"+
		"\u012a\u0000\u0000\u06ae\u06b5\u0005\u011e\u0000\u0000\u06af\u06b0\u0005"+
		"\u011d\u0000\u0000\u06b0\u06b1\u0005\u012a\u0000\u0000\u06b1\u06b2\u0005"+
		"\u0116\u0000\u0000\u06b2\u06b3\u0005\u012a\u0000\u0000\u06b3\u06b5\u0005"+
		"\u011e\u0000\u0000\u06b4\u06ac\u0001\u0000\u0000\u0000\u06b4\u06af\u0001"+
		"\u0000\u0000\u0000\u06b5\u01b9\u0001\u0000\u0000\u0000\u06b6\u06b7\u0003"+
		"\u01bc\u00de\u0000\u06b7\u01bb\u0001\u0000\u0000\u0000\u06b8\u06f6\u0005"+
		"\u0106\u0000\u0000\u06b9\u06f6\u0005\u00e0\u0000\u0000\u06ba\u06f6\u0005"+
		"\u00d7\u0000\u0000\u06bb\u06f6\u0005\u00d6\u0000\u0000\u06bc\u06f6\u0005"+
		"\u00d5\u0000\u0000\u06bd\u06f6\u0005\u010c\u0000\u0000\u06be\u06f6\u0005"+
		"5\u0000\u0000\u06bf\u06f6\u0005\u010d\u0000\u0000\u06c0\u06f6\u0005\u010b"+
		"\u0000\u0000\u06c1\u06f6\u0005\u00dd\u0000\u0000\u06c2\u06f6\u0005\u00de"+
		"\u0000\u0000\u06c3\u06f6\u0005\u00e1\u0000\u0000\u06c4\u06f6\u0005\u00e5"+
		"\u0000\u0000\u06c5\u06f6\u0005\u00e6\u0000\u0000\u06c6\u06f6\u0005\u00e2"+
		"\u0000\u0000\u06c7\u06f6\u0005\u00e3\u0000\u0000\u06c8\u06f6\u0005\u00e4"+
		"\u0000\u0000\u06c9\u06f6\u0005\u00df\u0000\u0000\u06ca\u06f6\u0005\u00d9"+
		"\u0000\u0000\u06cb\u06f6\u0005\u00e7\u0000\u0000\u06cc\u06f6\u0005\u00da"+
		"\u0000\u0000\u06cd\u06f6\u0005\u00e8\u0000\u0000\u06ce\u06f6\u0005\u00db"+
		"\u0000\u0000\u06cf\u06f6\u0005\u00e9\u0000\u0000\u06d0\u06f6\u0005\u00ea"+
		"\u0000\u0000\u06d1\u06f6\u0005\u00dc\u0000\u0000\u06d2\u06f6\u0005\u00eb"+
		"\u0000\u0000\u06d3\u06f6\u0005\u00ec\u0000\u0000\u06d4\u06f6\u0005\u00ed"+
		"\u0000\u0000\u06d5\u06f6\u0005\u00ee\u0000\u0000\u06d6\u06f6\u0005\u00ef"+
		"\u0000\u0000\u06d7\u06f6\u0005$\u0000\u0000\u06d8\u06f6\u0005\u00f0\u0000"+
		"\u0000\u06d9\u06f6\u0005\u00f2\u0000\u0000\u06da\u06f6\u0005\u00f1\u0000"+
		"\u0000\u06db\u06f6\u0005\u00d1\u0000\u0000\u06dc\u06f6\u0005\u00d2\u0000"+
		"\u0000\u06dd\u06f6\u0005\u00fd\u0000\u0000\u06de\u06f6\u0005\u00fe\u0000"+
		"\u0000\u06df\u06f6\u0005\u00ff\u0000\u0000\u06e0\u06e1\u0005\u00ff\u0000"+
		"\u0000\u06e1\u06e2\u0005:\u0000\u0000\u06e2\u06e3\u0005\u00ff\u0000\u0000"+
		"\u06e3\u06f6\u0005\u009d\u0000\u0000\u06e4\u06f6\u0005\u0100\u0000\u0000"+
		"\u06e5\u06f6\u0005\u0102\u0000\u0000\u06e6\u06f6\u0005\u0103\u0000\u0000"+
		"\u06e7\u06f6\u0005\u0104\u0000\u0000\u06e8\u06f6\u0005\u0101\u0000\u0000"+
		"\u06e9\u06ea\u0005\u0101\u0000\u0000\u06ea\u06eb\u0005:\u0000\u0000\u06eb"+
		"\u06ec\u0005\u00ff\u0000\u0000\u06ec\u06f6\u0005\u009d\u0000\u0000\u06ed"+
		"\u06ee\u0005\u0101\u0000\u0000\u06ee\u06ef\u0005;\u0000\u0000\u06ef\u06f0"+
		"\u0005\u00ff\u0000\u0000\u06f0\u06f6\u0005\u009d\u0000\u0000\u06f1\u06f6"+
		"\u0005\u0105\u0000\u0000\u06f2\u06f6\u0005\u00ce\u0000\u0000\u06f3\u06f6"+
		"\u0005\u00d0\u0000\u0000\u06f4\u06f6\u0005\u00cf\u0000\u0000\u06f5\u06b8"+
		"\u0001\u0000\u0000\u0000\u06f5\u06b9\u0001\u0000\u0000\u0000\u06f5\u06ba"+
		"\u0001\u0000\u0000\u0000\u06f5\u06bb\u0001\u0000\u0000\u0000\u06f5\u06bc"+
		"\u0001\u0000\u0000\u0000\u06f5\u06bd\u0001\u0000\u0000\u0000\u06f5\u06be"+
		"\u0001\u0000\u0000\u0000\u06f5\u06bf\u0001\u0000\u0000\u0000\u06f5\u06c0"+
		"\u0001\u0000\u0000\u0000\u06f5\u06c1\u0001\u0000\u0000\u0000\u06f5\u06c2"+
		"\u0001\u0000\u0000\u0000\u06f5\u06c3\u0001\u0000\u0000\u0000\u06f5\u06c4"+
		"\u0001\u0000\u0000\u0000\u06f5\u06c5\u0001\u0000\u0000\u0000\u06f5\u06c6"+
		"\u0001\u0000\u0000\u0000\u06f5\u06c7\u0001\u0000\u0000\u0000\u06f5\u06c8"+
		"\u0001\u0000\u0000\u0000\u06f5\u06c9\u0001\u0000\u0000\u0000\u06f5\u06ca"+
		"\u0001\u0000\u0000\u0000\u06f5\u06cb\u0001\u0000\u0000\u0000\u06f5\u06cc"+
		"\u0001\u0000\u0000\u0000\u06f5\u06cd\u0001\u0000\u0000\u0000\u06f5\u06ce"+
		"\u0001\u0000\u0000\u0000\u06f5\u06cf\u0001\u0000\u0000\u0000\u06f5\u06d0"+
		"\u0001\u0000\u0000\u0000\u06f5\u06d1\u0001\u0000\u0000\u0000\u06f5\u06d2"+
		"\u0001\u0000\u0000\u0000\u06f5\u06d3\u0001\u0000\u0000\u0000\u06f5\u06d4"+
		"\u0001\u0000\u0000\u0000\u06f5\u06d5\u0001\u0000\u0000\u0000\u06f5\u06d6"+
		"\u0001\u0000\u0000\u0000\u06f5\u06d7\u0001\u0000\u0000\u0000\u06f5\u06d8"+
		"\u0001\u0000\u0000\u0000\u06f5\u06d9\u0001\u0000\u0000\u0000\u06f5\u06da"+
		"\u0001\u0000\u0000\u0000\u06f5\u06db\u0001\u0000\u0000\u0000\u06f5\u06dc"+
		"\u0001\u0000\u0000\u0000\u06f5\u06dd\u0001\u0000\u0000\u0000\u06f5\u06de"+
		"\u0001\u0000\u0000\u0000\u06f5\u06df\u0001\u0000\u0000\u0000\u06f5\u06e0"+
		"\u0001\u0000\u0000\u0000\u06f5\u06e4\u0001\u0000\u0000\u0000\u06f5\u06e5"+
		"\u0001\u0000\u0000\u0000\u06f5\u06e6\u0001\u0000\u0000\u0000\u06f5\u06e7"+
		"\u0001\u0000\u0000\u0000\u06f5\u06e8\u0001\u0000\u0000\u0000\u06f5\u06e9"+
		"\u0001\u0000\u0000\u0000\u06f5\u06ed\u0001\u0000\u0000\u0000\u06f5\u06f1"+
		"\u0001\u0000\u0000\u0000\u06f5\u06f2\u0001\u0000\u0000\u0000\u06f5\u06f3"+
		"\u0001\u0000\u0000\u0000\u06f5\u06f4\u0001\u0000\u0000\u0000\u06f6\u01bd"+
		"\u0001\u0000\u0000\u0000\u06f7\u06f8\u0007\u001c\u0000\u0000\u06f8\u01bf"+
		"\u0001\u0000\u0000\u0000\u00a8\u01c3\u01c6\u01e1\u01f4\u01ff\u0203\u020c"+
		"\u021b\u021f\u0223\u022a\u0231\u0234\u0237\u0241\u0255\u025a\u0264\u0269"+
		"\u0270\u0278\u027b\u0280\u0283\u0286\u0289\u028c\u028f\u0291\u029d\u02a2"+
		"\u02a5\u02a8\u02b0\u02b7\u02c4\u02c6\u02c8\u02d3\u02d5\u02d7\u02db\u02df"+
		"\u02e6\u02eb\u02ed\u02f3\u02fa\u0300\u0302\u0307\u0309\u0311\u0314\u0317"+
		"\u031e\u0320\u0324\u0335\u033b\u0342\u0347\u034f\u035a\u035f\u0364\u0366"+
		"\u0374\u037f\u0383\u0388\u0392\u0399\u039d\u03a2\u03ac\u03b8\u03cb\u03d4"+
		"\u03d7\u03d9\u03de\u03e1\u03e4\u03f4\u03fe\u0417\u041c\u0423\u042b\u042f"+
		"\u0435\u0443\u0449\u0450\u0455\u045f\u0462\u0465\u046c\u0477\u047f\u0483"+
		"\u048d\u048f\u0493\u049c\u04a2\u04a7\u04b7\u04bc\u04bf\u04cc\u04d2\u04d9"+
		"\u04e0\u04e7\u04ef\u0509\u0512\u0516\u051e\u0526\u052a\u0534\u053b\u0540"+
		"\u054b\u0552\u055b\u056e\u057e\u058b\u0592\u0598\u05a7\u05b2\u05bb\u05c2"+
		"\u05c5\u05ce\u05d6\u05dd\u05e1\u05f3\u05f9\u0603\u0607\u060e\u061b\u0622"+
		"\u0627\u0632\u0638\u063c\u0641\u0644\u0649\u064e\u0656\u065d\u066d\u0671"+
		"\u0695\u069d\u06aa\u06b4\u06f5";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}