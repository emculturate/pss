/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//parser 
grammar SQLSelectParser;

options {
	language=Java;
//	tokenVocab=SQLLexer;
}

@header {
}

@members {
}

/*
===============================================================================
  Start Statements: SQL, Condition, Predicand and Literal
===============================================================================
*/
sql
  : (with_query
  | create_table_as_expression
  ) (SEMI_COLON)? EOF
  ;
  
  
/*
===============================================================================
  Column Start Symbol
===============================================================================
*/
column_value
  : column_primary EOF
  ;
  
/*
===============================================================================
  Predicand Start Symbol
===============================================================================
*/
predicand_value
  : predicand_primary EOF
  ;
 
/*
===============================================================================
  In List Predicate Start Symbol
===============================================================================
*/
in_list_predicate_value
  : in_predicate_value EOF
  ;

/*
===============================================================================
  Condition Start Symbol
===============================================================================
*/
condition_value
  : search_condition EOF
  ;

/*
===============================================================================
  Tuple Start Symbol
===============================================================================
*/
tuple_value
  : tuple_primary EOF
  ;

/*
===============================================================================
  Query Start Symbol
===============================================================================
*/
query_value
  : query EOF
  ;

/*
===============================================================================
  Join Extension Start Symbol
===============================================================================
*/
join_extension_value
  :  join_extension_primary EOF
  ;
  
/*
===============================================================================
  Literal Value Start Symbol
===============================================================================
*/
literal_value
  : (signed_numeric_literal | unsigned_literal) EOF
  ;
 
/*
===============================================================================
  Dependent Grammar Rules
===============================================================================
*/
/*
===============================================================================
  WITH Statement <with query>
===============================================================================
*/

with_query
  : with_clause? query
  ;

with_clause
  : WITH with_list_item (COMMA with_list_item)*
  ;
  
with_list_item
  : query_alias (LEFT_PAREN query RIGHT_PAREN)
  ;
  
query_alias
  : identifier AS
  ;

query
  : query_expression 
  | insert_expression 
  | update_expression
  ;

/*
===============================================================================
  INSERT Statement <insert expression>
===============================================================================
*/

/*
 * POSTGRES:
[ WITH [ RECURSIVE ] with_query [, ...] ]
INSERT INTO table_name [ AS alias ] [ ( column_name [, ...] ) ]
    { DEFAULT VALUES | VALUES ( { expression | DEFAULT } [, ...] ) [, ...] | query }
    [ ON CONFLICT [ conflict_target ] conflict_action ]
    [ RETURNING * | output_expression [ [ AS ] output_name ] [, ...] ]

* HIVE:
* Standard syntax:
INSERT OVERWRITE TABLE tablename1 [PARTITION (partcol1=val1, partcol2=val2 ...) [IF NOT EXISTS]] select_statement1 FROM from_statement;
INSERT INTO TABLE tablename1 [PARTITION (partcol1=val1, partcol2=val2 ...)] select_statement1 FROM from_statement;
INSERT INTO TABLE tablename1 [PARTITION (partcol1=val1, partcol2=val2 ...)] (z,y) select_statement1 FROM from_statement;

Original Parser: 
insert_statement
  : INSERT (OVERWRITE)? INTO table_name (LEFT_PAREN column_name_list RIGHT_PAREN)? query_expression
  | INSERT (OVERWRITE)? INTO LOCATION path=Character_String_Literal (USING file_type=identifier (param_clause)?)? query_expression
  ;
    
 
column_name_list
  :  identifier  ( COMMA identifier  )*
  ;
 
  
 */
insert_expression
  : INSERT INTO 
  table_primary 
  (LEFT_PAREN column_reference_list RIGHT_PAREN)? 
  VALUES 
  subquery 
  returning?
  ;
  
/*
===============================================================================
  UPDATE Statement <update expression>
===============================================================================
*/
  
/*
 * POSTGRES:
[ WITH [ RECURSIVE ] with_query [, ...] ]
UPDATE [ ONLY ] table [ * ] [ [ AS ] alias ]
    SET { column = { expression | DEFAULT } |
          ( column [, ...] ) = ( { expression | DEFAULT } [, ...] ) } [, ...]
    [ FROM from_list ]
    [ WHERE condition | WHERE CURRENT OF cursor_name ]
    [ RETURNING * | output_expression [ [ AS ] output_name ] [, ...] ]
 */
update_expression
  : UPDATE 
  table_primary 
  SET assignment_expression_list 
  from_clause? 
  where_clause? 
  returning?
  ;

returning
  : RETURNING MULTIPLY
  ;
  
assignment_expression_list
  :   assignment_expression (COMMA assignment_expression)*
  ;
  
assignment_expression
  : column_reference EQUAL row_value_predicand
  ;
   
/*
===============================================================================
  CREATE TABLE
===============================================================================
*/
    
create_table_as_expression
  : CREATE TABLE AS query_expression
  ;
  
/*
===============================================================================
  QUERY EXPRESSION
===============================================================================
*/
// Nested, structured query construction that preserves precedence order:  Intersect then Union

query_expression
  : intersected_query
  ;
   
intersected_query
  : unionized_query (intersect_clause unionized_query)*
  ;

intersect_clause
  : intersect_operator set_qualifier?
  ;
  
intersect_operator
  : (INTERSECT)
  ;

unionized_query
  : query_primary (union_clause query_primary)*
  ;

union_clause
  : union_operator set_qualifier?
  ;

union_operator
   : (UNION | EXCEPT)
   ;

/*
===============================================================================
  SELECT Statement <query primary>
===============================================================================
*/

query_primary
  : subquery
  | query_specification
  | variable_identifier
  ;

subquery
  :  LEFT_PAREN query_expression RIGHT_PAREN
  ;

query_specification
  : SELECT into_list? set_qualifier? select_list 
  ( from_clause
    where_clause?
    groupby_clause?
    having_clause?
    orderby_clause?
    limit_clause?)?
  ;

/*
===============================================================================
  SELECT Details
===============================================================================
*/


into_list
  : INTO table_or_query_name
  ;

set_qualifier
  : DISTINCT
  | ALL
  ;

select_list
  : select_item (COMMA select_item)*
  ;

select_item
  : value_expression as_clause?
  | select_all_columns
  ;

as_clause
  : (AS)? alias_identifier
  ;

select_all_columns
  : (tb_name=Identifier DOT)? MULTIPLY
  ;

/*
===============================================================================
  FROM Statement <from clause>
===============================================================================
*/

from_clause
  : FROM table_reference_list join_extension?
  ;
  
join_extension
  : variable_identifier
  ;
  
table_reference_list
  : table_primary ((COMMA table_primary)
     | (unqualified_join right=table_primary)
     | (qualified_join right=table_primary s=join_specification))*
  ;
  
join_extension_primary
  : ((COMMA table_primary)
     | (unqualified_join right=table_primary)
     | (qualified_join right=table_primary s=join_specification))*  join_extension?
  ;

table_primary
  : table_or_query_name as_clause?
  | subquery as_clause? 
  | variable_identifier as_clause
  ;

// Used ONLY in the TUPLE Variable Substitution end point
tuple_primary
  : table_or_query_name
  | subquery
  | variable_identifier
  ;

table_or_query_name
  : identifier   (DOT  (simple_numeric_identifier|identifier))?  (DOT  (simple_numeric_identifier|identifier))?
  ;

unqualified_join
  : CROSS JOIN
  | UNION JOIN
  | NATURAL (t=join_type)? JOIN
  ;
  
qualified_join
  : (t=join_type)? JOIN
  ;

join_type
  : INNER
  | (LEFT   | RIGHT  | FULL) OUTER?
  ;

join_specification
  : join_condition
  | named_columns_join
  ;

join_condition
  : ON search_condition
  ;
  
named_columns_join
  : using_term LEFT_PAREN f=column_reference_list RIGHT_PAREN
  ;

using_term
  : USING
  ;
  
/*
===============================================================================
  Column List clauses
===============================================================================
*/

column_reference_list
  : column_reference (COMMA column_reference)*
  ;

column_reference
  : (tb_name=identifier DOT)? name=identifier
  | tb_name=identifier DOT substitution=variable_identifier
  ;

column_primary
  : (tb_name=identifier DOT)? name=identifier
  | tb_name=identifier DOT substitution=variable_identifier
  | substitution=variable_identifier
  ;

/*
===============================================================================
  Predicands <value expression primary>
===============================================================================
*/
   
predicand_primary
  : value_expression_primary (CAST_OPERATOR data_type)?
  | trim_function
  | null_literal
  | variable_identifier
  ;

value_expression_primary
  : parenthesized_value_expression
  | nonparenthesized_value_expression_primary
  ;

parenthesized_value_expression
  : LEFT_PAREN value_expression RIGHT_PAREN
  ;

nonparenthesized_value_expression_primary
  : unsigned_literal
  | column_reference
  | aggregate_function
  | case_expression
  | cast_function_expression
  | routine_invocation
  | window_over_partition_expression
  | subquery
  ;

/*
===============================================================================
  Aggregate Over Sets Functions
===============================================================================
*/
aggregate_function
  : COUNT LEFT_PAREN MULTIPLY RIGHT_PAREN	# count_all_aggregate
  | (set_function_type|set_qualifier_type) LEFT_PAREN set_qualifier? value_expression RIGHT_PAREN   # general_set_function
  // Next variation not supported, limited SQL dialects only
  // | (set_function_type|set_qualifier_type) LEFT_PAREN set_qualifier? value_expression RIGHT_PAREN filter_clause?
  ;
  
set_function_type
  : AVG
  | FIRST_VALUE
  | LAG
  | LAST_VALUE
  | LEAD
  | MAX
  | MIN
  | NTH_VALUE
  | SUM
  | COUNT
  | RANK
  | ROW_NUMBER
  | STDDEV_POP
  | STDDEV_SAMP
  | VAR_SAMP
  | VAR_POP
  // Snowflake Set Functions
  | ANY_VALUE
  | CORR
  | COVAR_POP
  | COVAR_SAMP
  | LISTAGG
  | MEDIAN
  | PERCENTILE_CONT
  | PERCENTILE_DISC
  | STDDEV
  | VARIANCE_POP
  | VARIANCE
  | VARIANCE_SAMP
  | CUME_DIST
  | DENSE_RANK
  | NTILE
  | PERCENT_RANK
  | WIDTH_BUCKET
  | BITAND_AGG
  | BITOR_AGG
  | BITXOR_AGG
  | HASH_AGG
  | ARRAY_AGG
  | OBJECT_AGG
  | REGR_AVGX
  | REGR_AVGY
  | REGR_COUNT
  | REGR_INTERCEPT
  | REGR_R2
  | REGR_SLOPE
  | REGR_SXX
  | REGR_SXY
  | REGR_SYY
  | APPROX_COUNT_DISTINCT
  | HLL
  | HLL_ACCUMULATE
  | HLL_COMBINE
  | HLL_EXPORT
  | HLL_IMPORT
  | APPROXIMATE_JACCARD_INDEX
  | APPROXIMATE_SIMILARITY
  | MINHASH
  | MINHASH_COMBINE
  | APPROX_TOP_K
  | APPROX_TOP_K_ACCUMULATE
  | APPROX_TOP_K_COMBINE
  | APPROX_PERCENTILE
  | APPROX_PERCENTILE_ACCUMULATE
  | APPROX_PERCENTILE_COMBINE
  ;

set_qualifier_type  
  : EVERY
  | ANY
  | SOME
  | COLLECT
  | FUSION
  | INTERSECTION
  ;

//  TODO: filter clause variant on aggregate functions not currently supported
// filter_clause
//   : FILTER LEFT_PAREN WHERE search_condition RIGHT_PAREN
//   ;


/*
===============================================================================
 CASE Clause <case expression>
===============================================================================
*/

case_expression
  : CASE value_expression when_value_list ( else_clause  )? END
  | CASE when_clause_list (else_clause)? END
  ;

when_clause_list
   : (searched_when_clause)+ 
   ;

searched_when_clause
  : WHEN c=search_condition THEN r=case_result
  ;

when_value_list
   : (when_value_clause)+ 
   ;

when_value_clause
  : WHEN c=value_expression THEN r=case_result
  ;

else_clause
  : ELSE r=case_result
  ;

case_result
  : value_expression | null_literal
  ;

null_literal
  : NULL
  ;
  
/*
===============================================================================
  CAST Function
===============================================================================
*/

cast_function_expression
  : cast_function_name LEFT_PAREN value_expression AS data_type RIGHT_PAREN
  ;
 
cast_function_name
  : CAST | TRYCAST
  ;
   
/*
===============================================================================
 WINDOW Functions
===============================================================================
*/
  /*
   * Functions over partitions
   * rank() OVER (partition by k_stfd order by OBSERVATION_TM desc, row_num desc)
   * last_value(column) over (partition by other_column rows between 2 preceding and unbounded following)
   */
window_over_partition_expression
   : window_function over_clause
   ;
   
window_function
   : set_function_type LEFT_PAREN sql_argument_list? RIGHT_PAREN
   | item_select_function LEFT_PAREN sql_argument_list RIGHT_PAREN (select_direction? null_handling)?
   ;
   
over_clause
   : OVER LEFT_PAREN (partition_by_clause? orderby_clause? bracket_frame_clause?) RIGHT_PAREN
   ;
    
partition_by_clause
   : PARTITION BY sql_argument_list
   ;
   
bracket_frame_clause
   : rows_or_range bracket_frame_definition
   ;
      
rows_or_range
   : ROWS  // unbound preceding, unbound following; 1 preceding, current row, interval '1' month preceding
   | RANGE 
   ;
   
bracket_frame_definition
   : between_frame_definition
   | preceding_frame_edge
   | current_row_edge
   ;
   
between_frame_definition
   : BETWEEN frame_edge AND frame_edge
   ;
   
frame_edge
   : preceding_frame_edge
   | following_frame_edge
   | current_row_edge
   ;
   
preceding_frame_edge
   : bracket_constraint PRECEDING
   ;
   
following_frame_edge
   : bracket_constraint FOLLOWING
   ;
   
current_row_edge
   : CURRENT ROW
   ;
   
bracket_constraint
   : NUMBER
   | UNBOUNDED
   ;
   
    
   // SNOWFLAKE ITEM SELECTION SYNTAX
item_select_function
  : FIRST_VALUE
  | LAST_VALUE
  | NTH_VALUE
  | LAG
  | LEAD
  ;
   
select_direction
   :  FROM (FIRST | LAST)
   ;
   
null_handling
   :  (IGNORE | RESPECT) NULLS
   ;
   
   


/*
===============================================================================
  <value expression>
===============================================================================
*/
value_expression
  : common_value_expression
  | row_value_expression
  // variables identified here are Predicand variables
  | variable_identifier
  // variables encountered after this would be condition variables
  | boolean_value_expression
  ;

common_value_expression
  : additive_expression
  | string_value_expression
  | null_literal
  ;

/*
===============================================================================
  6.26 <numeric value expression>

  Specify a comparison of two row values.
===============================================================================
*/

additive_expression
  : (left=multiplicative_expression ((PLUS|MINUS) right=multiplicative_expression)*)
  ;

multiplicative_expression
  : left=factor ((MULTIPLY|DIVIDE|MODULAR) right=factor)*
  ;

factor
  : (sign)? numeric_primary
  ;

numeric_primary
  : value_expression_primary
  | extract_expression
  ;

sign
  : PLUS | MINUS
  ;

/*
===============================================================================
  6.27 <numeric value function>
===============================================================================
*/

/* numeric_value_function
  : extract_expression
  ; */

extract_expression
  : EXTRACT LEFT_PAREN extract_field_string=extract_field FROM extract_source RIGHT_PAREN
  ;

extract_field
  : primary_datetime_field
  | time_zone_field
  | extended_datetime_field
  ;

time_zone_field
  : TIMEZONE | TIMEZONE_HOUR | TIMEZONE_MINUTE
  ;

extract_source
  : column_reference
  | datetime_literal
  ;

/*
===============================================================================
  6.28 <string value expression>
===============================================================================
*/

string_value_expression
  : character_primary (CONCATENATION_OPERATOR character_primary)*
  ;

character_primary
  : value_expression_primary
  | trim_function
  ;

trim_function
  : trim_function_name LEFT_PAREN trim_operands RIGHT_PAREN
  ;

trim_function_name
  : TRIM
  ;

trim_operands
  : ((trim_specification)? (trim_character=string_value_expression)? FROM)? 
     trim_source=value_expression  # mysql_trim_operands
  | trim_source=value_expression COMMA 
     trim_character=string_value_expression # other_trim_operands
  ;

trim_specification
  : LEADING | TRAILING | BOTH
  ;

/*
===============================================================================
  6.34 <boolean value expression>
===============================================================================
*/

boolean_value_expression
  : or_predicate
  ;

or_predicate
  : and_predicate (OR and_predicate)*
  ;

and_predicate
  : negative_predicate (AND negative_predicate)*
  ;

negative_predicate
  : not? parenthetical_predicate
  ;

parenthetical_predicate
  : LEFT_PAREN boolean_value_expression RIGHT_PAREN # paren_clause
  | boolean_primary is_clause?						# basic_predicate_clause
  ;

boolean_primary
  : predicate
  | nonparenthesized_value_expression_primary
  ;


/*
===============================================================================
  8.1 <predicate>
===============================================================================
*/

predicate
  : comparison_predicate
  | between_predicate
  | in_predicate
  | null_predicate
  | exists_predicate
  | substitution_predicate
  ;

substitution_predicate
	: variable_identifier
	;

/*
===============================================================================
  7.2 <row value expression>
===============================================================================
*/

row_value_expression
  : nonparenthesized_value_expression_primary
  | null_literal
  ;

row_value_predicand
  : nonparenthesized_value_expression_primary
  | common_value_expression
  | variable_identifier
  ;

/*
===============================================================================
  WHERE <where clause>
===============================================================================
*/
where_clause
  : WHERE search_condition
  ;

search_condition
  : value_expression // instead of boolean_value_expression, we use value_expression for more flexibility.
  ;

/*
===============================================================================
  ORDER BY <order_by clause>
===============================================================================
*/

orderby_clause
  : ORDER BY sort_specifier_list
  ;

sort_specifier_list
  : sort_specifier (COMMA sort_specifier)*
  ;

sort_specifier
  : key=row_value_predicand order=order_specification? null_order=null_ordering?
  ;

order_specification
  : ASC
  | DESC
  ;

null_ordering
  : NULLS null_first_last 
  ;
  
null_first_last
  : FIRST | LAST
  ;

/*
===============================================================================
  LIMIT <limit clause>
===============================================================================
*/

limit_clause
  : LIMIT e=additive_expression
  ;


/*
===============================================================================
  GROUP BY <group by clause>
===============================================================================
*/

groupby_clause
  : GROUP BY (grouping_element_list | select_list)
  ;

grouping_element_list
  : grouping_element (COMMA grouping_element)*
  ;

grouping_element
  : rollup_list
  | cube_list
  | empty_grouping_set
  | ordinary_grouping_set
  ;

ordinary_grouping_set_list
  : ordinary_grouping_set (COMMA ordinary_grouping_set)*
  ;

ordinary_grouping_set
  : row_value_predicand
  | LEFT_PAREN row_value_predicand_list RIGHT_PAREN
  ;

rollup_list
  : ROLLUP LEFT_PAREN c=ordinary_grouping_set_list RIGHT_PAREN
  ;

cube_list
  : CUBE LEFT_PAREN c=ordinary_grouping_set_list RIGHT_PAREN
  ;

empty_grouping_set
  : LEFT_PAREN RIGHT_PAREN
  ;

having_clause
  : HAVING boolean_value_expression
  ;

row_value_predicand_list
  : row_value_predicand (COMMA row_value_predicand)*
  ;


/*
===============================================================================
  8.2 <comparison predicate>

  Specify a comparison of two row values.
===============================================================================
*/
comparison_predicate
  : left=row_value_predicand c=comparison_operator right=row_value_predicand
  ;

comparison_operator
  : comp_op
  | not? relative_comp_op
  | similarity_op
  ;
  
relative_comp_op
  : LIKE
  | ILIKE
  | SIMILAR TO
  | REGEXP
  | RLIKE
;

similarity_op
  : Similar_To
  | Not_Similar_To
  | Similar_To_Case_Insensitive
  | Not_Similar_To_Case_Insensitive
  ;

comp_op
  : EQUAL
  | NOT_EQUAL
  | LTH
  | LEQ
  | GTH
  | GEQ
;
  
/*
===============================================================================
  <between predicate>
===============================================================================
*/

between_predicate
  : row_value_predicand  (not)? BETWEEN symmetry? begin=row_value_predicand AND end=row_value_predicand
  ;

symmetry
   : (ASYMMETRIC | SYMMETRIC)
   ;

/*
===============================================================================
  <in predicate>
===============================================================================
*/

in_predicate
//  : additive_expression  not? IN in_predicate_value
  : row_value_predicand  not? IN in_predicate_value
  ;
  
  

in_predicate_value
  : subquery
  | LEFT_PAREN in_value_list RIGHT_PAREN
  | variable_identifier
  ;

in_value_list
  : row_value_expression  ( COMMA row_value_expression )*
  ;


/*
===============================================================================
  8.7 <null predicate>

  Specify a test for a null value.
===============================================================================
*/

null_predicate
  : row_value_predicand is_null_clause
  ;

is_null_clause
  : IS (n=NOT)? NULL
  ;
 
is_clause
  : IS not? truth_value
  ;

truth_value
  : TRUE | FALSE | UNKNOWN
  ;

not
  : NOT
  ;

  
/*
==============================================================================================
  8.8 <quantified comparison predicate>

  Specify a quantified comparison.
==============================================================================================
*/

quantified_comparison_predicate
  : l=additive_expression  c=comp_op q=quantifier s=subquery
  ;

quantifier : all  | some ;

all : ALL;

some : SOME | ANY;

/*
==============================================================================================
  8.9 <exists predicate>

  Specify a test for a non_empty set.
==============================================================================================
*/

exists_predicate
  : NOT? EXISTS s=subquery
  ;


/*
==============================================================================================
  8.10 <unique predicate>

  Specify a test for the absence of duplicate rows
==============================================================================================
*/

unique_predicate
  : UNIQUE s=subquery
  ;

/*
===============================================================================
  10.1 <interval qualifier>

  Specify the precision of an interval data type.
===============================================================================
*/

primary_datetime_field
	:	non_second_primary_datetime_field
	|	SECOND
	;

non_second_primary_datetime_field
  : YEAR | MONTH | DAY | HOUR | MINUTE
  ;

extended_datetime_field
  : CENTURY | DECADE | DOW | DOY | EPOCH | ISODOW | ISOYEAR | MICROSECONDS | MILLENNIUM | MILLISECONDS | QUARTER | WEEK
  ;

/*
===============================================================================
  10.4 <routine invocation>

  Invoke an SQL-invoked routine.
===============================================================================
*/

routine_invocation
  : function_name LEFT_PAREN sql_argument_list? RIGHT_PAREN
  ;

function_name
  : identifier (DOT identifier)?
  | function_names_for_reserved_words
  ;

function_names_for_reserved_words
  : LEFT
  | RIGHT
  ;

sql_argument_list
  : value_expression (COMMA value_expression)*
  ;


/*
===============================================================================
  5.2 <token and separator>

  Specifying lexical units (tokens and separators) that participate in SQL language
===============================================================================
*/

identifier
  : simple_identifier
  | logical_identifier
  | nonreserved_keywords
  | snowflake_quoted_numeric_identifier
  ;

alias_identifier
   :simple_identifier
  | logical_identifier
  | nonreserved_keywords
  | simple_numeric_identifier
  | snowflake_quoted_numeric_identifier
  ;

variable_identifier
   : simple_variable_identifier
   | extended_variable_identifier
   ;

simple_identifier
   :	Identifier
   ;

logical_identifier
   :    Bracket_Identifier
   ;
   
simple_variable_identifier
	:	Variable_Identifier
	;

extended_variable_identifier
	:  Extended_Variable_Identifier
	;
 
simple_numeric_identifier
   :	Numeric_Identifier
   |	NUMBER
   ;
   
snowflake_quoted_numeric_identifier
   :	Double_Quoted_Numeric_Identifier
   ;
   
nonreserved_keywords
  : AVG
  | 	ABSTIME			// POSTGRES
  | 	ANYARRAY		// POSTGRES
  | 	ARRAY			// POSTGRES
  | 	ASC
  | 	BETWEEN
  | 	BIGINT
  | 	BIGSERIAL
  | 	BINARY 
  | 	BIT
  | 	BLOB
  | 	BOOL
  | 	BY
  | 	BYTEA
  | 	CENTURY
  | 	CHAR
  | 	CHARACTER
  | 	CIDR	 // POSTGRES
  | 	COALESCE
  | 	COLLECT
  | 	COLUMN
  | 	COUNT
  | 	CUBE
  | 	DATE
  | 	DATETIME     // SNOWFLAKE
  | 	DAY
  | 	DEC
  | 	DECADE
  | 	DESC
  | 	DOUBLE
  | 	DOW
  | 	DOY
  | 	DROP
  | 	EPOCH
  | 	EVERY
  | 	EXISTS
  | 	EXTERNAL
  | 	EXTRACT
  | 	FILTER
  | 	FIRST
  | 	FLOAT
  | 	FLOAT4
  | 	FLOAT8
  | 	FORMAT
  | 	FUSION
  | 	GROUPING
  | 	HASH
  | 	INDEX
  | 	INET	 // POSTGRES
  | 	INET4
  | 	INSERT
  | 	INT
  | 	INT1
  | 	INT2
  | 	INT4
  | 	INT8
  | 	INTERSECTION
  | 	INTERVAL // POSTGRES
  | 	ISODOW
  | 	ISOYEAR
  | 	JSON     // POSTGRES
  | 	JSONB    // POSTGRES
  | 	LAST
  | 	LESS
  | 	LIST
  | 	LOCATION
  | 	MACADDR  // POSTGRES
  | 	MAX
  | 	MAXVALUE
  | 	MICROSECONDS
  | 	MILLENNIUM
  | 	MILLISECONDS
  | 	MIN
  | 	MINUTE
  | 	MONEY
  | 	MONTH
  | 	NAME	 // POSTGRES
  | 	NATIONAL
  | 	NCHAR
  | 	NULLIF
  | 	NUMBER_TYPE
  | 	NUMERIC
  | 	NVARCHAR
  | 	OBJECT   // SNOWFLAKE
  | 	OID	     // POSTGRES
  | 	OVER
  | 	OVERWRITE
  | 	PARTITION
  | 	PARTITIONS
  | 	PG_LSN   // POSTGRES
  | 	PG_NODE_TREE  // POSTGRES
  | 	PRECISION
  | 	PURGE
  | 	QUARTER
  | 	RANGE
  | 	RANK
  | 	REAL
  | 	REGEXP
  | 	REGPROC  // POSTGRES
  | 	RETURNING
  | 	RLIKE
  | 	ROLLUP
  | 	ROWS
  | 	SECOND
  | 	SERIAL
  | 	SET
  | 	SIMILAR
  | 	SMALLINT
  | 	SMALLSERIAL
  | 	STDDEV_POP
  | 	STDDEV_SAMP
  |     STRING
  | 	STRUCT   // HIVE
  | 	SUBPARTITION
  | 	SUM
  | 	TABLESPACE
  | 	TEXT
  | 	THAN
  | 	TIME
  | 	TIMESTAMP
  | 	TIMESTAMP_LTZ     // SNOWFLAKE
  | 	TIMESTAMP_NTZ     // SNOWFLAKE
  | 	TIMESTAMP_TZ     // SNOWFLAKE
  | 	TIMESTAMPTZ
  | 	TIMETZ
  | 	TIMEZONE
  | 	TIMEZONE_HOUR
  | 	TIMEZONE_MINUTE
  | 	TINYINT
  | 	TO
  | 	TRIM
  | 	UNION    // HIVE
  | 	UNKNOWN
  | 	UPDATE
  | 	UUID	 // POSTGRES
  | 	VALUES
  | 	VAR_POP
  | 	VAR_SAMP
  | 	VARBINARY
  | 	VARBIT 
  | 	VARCHAR
  | 	VARIANT  // SNOWFLAKE
  | 	VARYING
  | 	WEEK
  | 	WITH
  | 	XID	     // POSTGRES
  | 	YEAR
  | 	ZONE 
    // Snowflake Set Functions
  | ANY_VALUE
  | CORR
  | COVAR_POP
  | COVAR_SAMP
  | LISTAGG
  | MEDIAN
  | PERCENTILE_CONT
  | PERCENTILE_DISC
  | STDDEV
  | VARIANCE_POP
  | VARIANCE
  | VARIANCE_SAMP
  | CUME_DIST
  | DENSE_RANK
  | NTILE
  | PERCENT_RANK
  | WIDTH_BUCKET
  | BITAND_AGG
  | BITOR_AGG
  | BITXOR_AGG
  | HASH_AGG
  | ARRAY_AGG
  | OBJECT_AGG
  | REGR_AVGX
  | REGR_AVGY
  | REGR_COUNT
  | REGR_INTERCEPT
  | REGR_R2
  | REGR_SLOPE
  | REGR_SXX
  | REGR_SXY
  | REGR_SYY
  | APPROX_COUNT_DISTINCT
  | HLL
  | HLL_ACCUMULATE
  | HLL_COMBINE
  | HLL_EXPORT
  | HLL_IMPORT
  | APPROXIMATE_JACCARD_INDEX
  | APPROXIMATE_SIMILARITY
  | MINHASH
  | MINHASH_COMBINE
  | APPROX_TOP_K
  | APPROX_TOP_K_ACCUMULATE
  | APPROX_TOP_K_COMBINE
  | APPROX_PERCENTILE
  | APPROX_PERCENTILE_ACCUMULATE
  | APPROX_PERCENTILE_COMBINE
  // Snowflake Last Value Options
  |     IGNORE
  |     RESPECT
  |		NULLS
  
  ;

/*
===============================================================================
  LITERAL  Value Rules
===============================================================================
*/

signed_numeric_literal
  : sign? unsigned_numeric_literal
  ;

unsigned_literal
  : unsigned_numeric_literal
  | general_literal
  ;

unsigned_numeric_literal
  : NUMBER					# ordinal_number
  | real_number_def				# real_number
  ;


real_number_def
    :   NUMBER DOT NUMBER? exponent?
    |   DOT NUMBER exponent?
    |   NUMBER exponent
    ;

exponent : EXPONEN   NUMBER ;

general_literal
  : character_literal
  | datetime_literal
  | boolean_literal
  ;

character_literal
  : Character_String_Literal
  ;

datetime_literal
  : timestamp_literal
  | time_literal
  | date_literal
  ;

time_literal
  : TIME time_string=Character_String_Literal
  ;

timestamp_literal
  : TIMESTAMP timestamp_string=Character_String_Literal
  ;

date_literal
  : DATE date_string=Character_String_Literal
  ;

boolean_literal
  : TRUE | FALSE | UNKNOWN
  ;

/*
===============================================================================
  DATA TYPES  <data types>
  * Parser has been modified to support multiple DBMS engine variations.
  * Not all data types are permitted in every engine

===============================================================================
*/

data_type
  : variable_size_data_type
  | precision_scale_data_type
  | static_data_type
  ;

variable_size_data_type
  : variable_data_type_name type_length?
  ;
  
variable_data_type_name
  : CHARACTER
  | CHAR
  | CHARACTER VARYING
  | CHAR VARYING
  | VARCHAR
  | VARCHAR2  // Classic Oracle
  | NATIONAL CHARACTER
  | NATIONAL CHAR
  | NCHAR
  | NATIONAL CHARACTER VARYING
  | NATIONAL CHAR VARYING
  | NCHAR VARYING
  | NVARCHAR
  | BLOB
  | BYTEA
  // bit_type
  | BIT 
  | VARBIT 
  | BIT VARYING 
  // binary_type
  | BINARY 
  | BINARY VARYING 
  | VARBINARY 
  // weird types
  | INTERVAL // POSTGRES
  | STRING
  ;
 
type_length
  : LEFT_PAREN NUMBER RIGHT_PAREN
  ;
   
precision_scale_data_type
  : precision_data_type_name precision_param?
  ;
  
precision_data_type_name
  : NUMERIC
  | NUMBER    // SNOWFLAKE
  | DECIMAL
  | DEC
  | FLOAT
  | DOUBLE
  | DOUBLE PRECISION
  ;  

precision_param
  : LEFT_PAREN precision=NUMBER RIGHT_PAREN
  | LEFT_PAREN precision=NUMBER COMMA scale=NUMBER RIGHT_PAREN
  ;
  
static_data_type
  : static_data_type_name
  ;

static_data_type_name  
  : TEXT
  | NAME	 // POSTGRES
  | INET4
  | INET	 // POSTGRES
  | CIDR	 // POSTGRES
  | STRUCT   // HIVE
  | UNION    // HIVE
  | VARIANT  // SNOWFLAKE
  | OBJECT   // SNOWFLAKE
  | JSON     // POSTGRES
  | JSONB    // POSTGRES
  | OID	     // POSTGRES
  | XID	     // POSTGRES
  | UUID	 // POSTGRES
  | PG_LSN   // POSTGRES
  | PG_NODE_TREE  // POSTGRES
  | REGPROC  // POSTGRES
  | MACADDR  // POSTGRES
  // Numeric
  | INT1
  | TINYINT  // HIVE
  | INT2
  | SMALLINT
  | INT4
  | INT
  | INTEGER
  | INT8
  | BIGINT
  | BIGSERIAL
  | SMALLSERIAL
  | SERIAL
  | MONEY
  | NUMBER_TYPE
  | FLOAT4
  | REAL
  | FLOAT8
  // Boolean
  | BOOLEAN
  | BOOL
  // datetime_type
  | DATE
  | DATETIME     // SNOWFLAKE
  | TIME
  | TIME WITH TIME ZONE
  | TIMETZ
  | TIMESTAMP_LTZ     // SNOWFLAKE
  | TIMESTAMP_NTZ     // SNOWFLAKE
  | TIMESTAMP_TZ     // SNOWFLAKE
  | TIMESTAMP
  | TIMESTAMP WITH TIME ZONE
  | TIMESTAMP WITHOUT TIME ZONE
  | TIMESTAMPTZ
  | ABSTIME			// POSTGRES
  // array_type
  | ARRAY			// POSTGRES
  | ANYARRAY		// POSTGRES
  ;  
  
  
  /**********************************************************
   * 
   * 
   * 
   ***********************************************************/
 /*
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

/* lexer grammar SQLLexer;

@header {
}

@members {
}
*/

/*
===============================================================================
  Tokens for Case Insensitive Keywords
===============================================================================
*/
fragment A
	:	'A' | 'a';

fragment B
	:	'B' | 'b';

fragment C
	:	'C' | 'c';

fragment D
	:	'D' | 'd';

fragment E
	:	'E' | 'e';

fragment F
	:	'F' | 'f';

fragment G
	:	'G' | 'g';

fragment H
	:	'H' | 'h';

fragment I
	:	'I' | 'i';

fragment J
	:	'J' | 'j';

fragment K
	:	'K' | 'k';

fragment L
	:	'L' | 'l';

fragment M
	:	'M' | 'm';

fragment N
	:	'N' | 'n';

fragment O
	:	'O' | 'o';

fragment P
	:	'P' | 'p';

fragment Q
	:	'Q' | 'q';

fragment R
	:	'R' | 'r';

fragment S
	:	'S' | 's';

fragment T
	:	'T' | 't';

fragment U
	:	'U' | 'u';

fragment V
	:	'V' | 'v';

fragment W
	:	'W' | 'w';

fragment X
	:	'X' | 'x';

fragment Y
	:	'Y' | 'y';

fragment Z
	:	'Z' | 'z';

/*
===============================================================================
  Reserved Keywords
===============================================================================
*/

AS : A S;
ALL : A L L;
AND : A N D;
ANY : A N Y;
ASYMMETRIC : A S Y M M E T R I C;

BOTH : B O T H;

CASE : C A S E;
CAST : C A S T;
CREATE : C R E A T E;
CROSS : C R O S S;

DISTINCT : D I S T I N C T;

END : E N D;
ELSE : E L S E;
EXCEPT : E X C E P T;

FALSE : F A L S E;
FULL : F U L L;
FROM : F R O M;

GROUP : G R O U P;

HAVING : H A V I N G;

IGNORE: I G N O R E;
ILIKE : I L I K E;
IN : I N;
INNER : I N N E R;
INTERSECT : I N T E R S E C T;
INTO : I N T O;
IS : I S;

JOIN : J O I N;

LEADING : L E A D I N G;
LEFT : L E F T;
LIKE : L I K E;
LIMIT : L I M I T;

NATURAL : N A T U R A L;
NOT : N O T;
NULL : N U L L;
NULLS : N U L L S;
NUMBER_TYPE : N U M B E R;

ON : O N;
OUTER : O U T E R;
OR : O R;
ORDER : O R D E R;

RESPECT : R E S P E C T;
RIGHT : R I G H T;
RETURNING : R E T U R N I N G;

SELECT : S E L E C T;
SOME : S O M E;
SYMMETRIC : S Y M M E T R I C;

TABLE : T A B L E;
THEN : T H E N;
TRAILING : T R A I L I N G;
TRUE : T R U E;
TRYCAST : T R Y UNDERLINE C A S T;


UNION : U N I O N;
UNIQUE : U N I Q U E;
USING : U S I N G;

WHEN : W H E N;
WHERE : W H E R E;
WITH : W I T H;
WITHOUT : W I T H O U T;

/*
===============================================================================
  Non Reserved Keywords
===============================================================================
*/
ASC : A S C;
AVG : A V G;

BETWEEN : B E T W E E N;
BY : B Y;

CENTURY : C E N T U R Y;
CHARACTER : C H A R A C T E R;
COLLECT : C O L L E C T;
COALESCE : C O A L E S C E;
COLUMN : C O L U M N;
COUNT : C O U N T;
CUBE : C U B E;
CURRENT : C U R R E N T;

DAY : D A Y;
DEC : D E C;
DECADE : D E C A D E;
DESC : D E S C;
DOW : D O W;
DOY : D O Y;
DROP : D R O P;

EPOCH : E P O C H;
EVERY : E V E R Y;
EXISTS : E X I S T S;
EXTERNAL : E X T E R N A L;
EXTRACT : E X T R A C T;


FILTER : F I L T E R;
FIRST : F I R S T;
FIRST_VALUE : F I R S T UNDERLINE V A L U E;
FOLLOWING : F O L L O W I N G;
FORMAT : F O R M A T;
FUSION : F U S I O N;

GROUPING : G R O U P I N G;

HASH : H A S H;
HOUR : H O U R;

INDEX : I N D E X;
INSERT : I N S E R T;
INTERSECTION : I N T E R S E C T I O N;
ISODOW : I S O D O W;
ISOYEAR : I S O Y E A R;

LAG : L A G;
LAST : L A S T;
LAST_VALUE : L A S T UNDERLINE V A L U E;
LEAD : L E A D;
LESS : L E S S;
LIST : L I S T;
LOCATION : L O C A T I O N;

MAX : M A X;
MAXVALUE : M A X V A L U E;
MICROSECONDS : M I C R O S E C O N D S;
MILLENNIUM : M I L L E N N I U M;
MILLISECONDS : M I L L I S E C O N D S;
MIN : M I N;
MINUTE : M I N U T E;
MONTH : M O N T H;

NATIONAL : N A T I O N A L;
NTH_VALUE : N T H UNDERLINE V A L U E;
NULLIF : N U L L I F;

OVER : O V E R;
OVERWRITE : O V E R W R I T E;

PARTITION : P A R T I T I O N;
PARTITIONS : P A R T I T I O N S;
PRECEDING : P R E C E D I N G;
PRECISION : P R E C I S I O N;
PURGE : P U R G E;

QUARTER : Q U A R T E R;

RANGE : R A N G E;
RANK : R A N K;
REGEXP : R E G E X P;
RLIKE : R L I K E;
ROLLUP : R O L L U P;
ROW_NUMBER : R O W UNDERLINE N U M B E R;
ROW : R O W;
ROWS : R O W S;

SECOND : S E C O N D;
SET : S E T;
SIMILAR : S I M I L A R;
STDDEV_POP : S T D D E V UNDERLINE P O P;
STDDEV_SAMP : S T D D E V UNDERLINE S A M P;
SUBPARTITION : S U B P A R T I T I O N;
SUM : S U M;

TABLESPACE : T A B L E S P A C E;
THAN : T H A N;
TIMEZONE: T I M E Z O N E;
TIMEZONE_HOUR: T I M E Z O N E UNDERLINE H O U R;
TIMEZONE_MINUTE: T I M E Z O N E UNDERLINE M I N U T E;
TRIM : T R I M;
TO : T O;

UPDATE : U P D A T E;
UNBOUNDED : U N B O U N D E D;
UNKNOWN : U N K N O W N;

VALUES : V A L U E S;
VAR_SAMP : V A R UNDERLINE S A M P;
VAR_POP : V A R UNDERLINE P O P;
VARYING : V A R Y I N G;

WEEK : W E E K;

YEAR : Y E A R;

ZONE : Z O N E;

  // Snowflake Set Functions
  
ANY_VALUE : A N Y UNDERLINE V A L U E;
CORR : C O R R;
COVAR_POP : C O V A R UNDERLINE P O P;
COVAR_SAMP : C O V A R UNDERLINE S A M P;
LISTAGG : L I S T A G G;
MEDIAN : M E D I A N;
PERCENTILE_CONT : P E R C E N T I L E UNDERLINE C O N T;
PERCENTILE_DISC : P E R C E N T I L E UNDERLINE D I S C;
STDDEV : S T D D E V;
VARIANCE_POP : V A R I A N C E UNDERLINE P O P;
VARIANCE : V A R I A N C E;
VARIANCE_SAMP : V A R I A N C E UNDERLINE S A M P;
CUME_DIST : C U M E UNDERLINE D I S T;
DENSE_RANK : D E N S E UNDERLINE R A N K;
NTILE : N T I L E;
PERCENT_RANK : P E R C E N T UNDERLINE R A N K;
WIDTH_BUCKET : W I D T H UNDERLINE B U C K E T;
BITAND_AGG : B I T A N D UNDERLINE A G G;
BITOR_AGG : B I T O R UNDERLINE A G G;
BITXOR_AGG : B I T X O R UNDERLINE A G G;
HASH_AGG : H A S H UNDERLINE A G G;
ARRAY_AGG : A R R A Y UNDERLINE A G G;
OBJECT_AGG : O B J E C T UNDERLINE A G G;
REGR_AVGX : R E G R UNDERLINE A V G X;
REGR_AVGY : R E G R UNDERLINE A V G Y;
REGR_COUNT : R E G R UNDERLINE C O U N T;
REGR_INTERCEPT : R E G R UNDERLINE I N T E R C E P T;
REGR_R2 : R E G R UNDERLINE R '2';
REGR_SLOPE : R E G R UNDERLINE S L O P E;
REGR_SXX : R E G R UNDERLINE S X X;
REGR_SXY : R E G R UNDERLINE S X Y;
REGR_SYY : R E G R UNDERLINE S Y Y;
APPROX_COUNT_DISTINCT : A P P R O X UNDERLINE C O U N T UNDERLINE D I S T I N C T;
HLL : H L L;
HLL_ACCUMULATE : H L L UNDERLINE A C C U M U L A T E;
HLL_COMBINE : H L L UNDERLINE C O M B I N E;
HLL_EXPORT : H L L UNDERLINE E X P O R T;
HLL_IMPORT : H L L UNDERLINE I M P O R T;
APPROXIMATE_JACCARD_INDEX : A P P R O X I M A T E UNDERLINE J A C C A R D UNDERLINE I N D E X;
APPROXIMATE_SIMILARITY : A P P R O X I M A T E UNDERLINE S I M I L A R I T Y;
MINHASH : M I N H A S H;
MINHASH_COMBINE : M I N H A S H UNDERLINE C O M B I N E;
APPROX_TOP_K : A P P R O X UNDERLINE T O P UNDERLINE K;
APPROX_TOP_K_ACCUMULATE : A P P R O X UNDERLINE T O P UNDERLINE K UNDERLINE A C C U M U L A T E;
APPROX_TOP_K_COMBINE : A P P R O X UNDERLINE T O P UNDERLINE K UNDERLINE C O M B I N E;
APPROX_PERCENTILE : A P P R O X UNDERLINE P E R C E N T I L E;
APPROX_PERCENTILE_ACCUMULATE : A P P R O X UNDERLINE P E R C E N T I L E UNDERLINE A C C U M U L A T E;
APPROX_PERCENTILE_COMBINE : A P P R O X UNDERLINE P E R C E N T I L E UNDERLINE C O M B I N E;


/*
===============================================================================
  Data Type Tokens
===============================================================================
*/


ABSTIME : A B S T I M E; // POSTGRES
ANYARRAY : A N Y A R R A Y;  // POSTGRES
ARRAY : A R R A Y;  // HIVE and Snowflake

BOOLEAN : B O O L E A N;
BOOL : B O O L;
BIT : B I T;
VARBIT : V A R B I T;

CIDR : C I D R; // POSTGRES
INET : I N E T; // POSTGRES
INET4 : I N E T '4';
INTERVAL: I N T E R V A L; // POSTGRES
INT1 : I N T '1';
INT2 : I N T '2';
INT4 : I N T '4';
INT8 : I N T '8';

JSON : J S O N; // POSTGRES
JSONB : J S O N B; // POSTGRES
MACADDR : M A C A D D R; // POSTGRES
NAME : N A M E; // POSTGRES
OID : O I D; // POSTGRES
PG_LSN : P G UNDERLINE L S N; // POSTGRES
PG_NODE_TREE : P G UNDERLINE N O D E UNDERLINE T R E E; // POSTGRES
REGPROC : R E G P R O C; // POSTGRES
XID : X I D; // POSTGRES
UUID : U U I D;  // POSTGRES

TINYINT : T I N Y I N T; // alias for INT1
SMALLINT : S M A L L I N T; // alias for INT2
INT : I N T; // alias for INT4
INTEGER : I N T E G E R; // alias - INT4
BIGINT : B I G I N T; // alias for INT8
BIGSERIAL : B I G S E R I A L; // POSTGRES
SMALLSERIAL : S M A L L S E R I A L; // POSTGRES
SERIAL : S E R I A L; // POSTGRES
MONEY : M O N E Y; // POSTGRES


FLOAT4 : F L O A T '4';
FLOAT8 : F L O A T '8';

REAL : R E A L; // alias for FLOAT4
FLOAT : F L O A T; // alias for FLOAT8
DOUBLE : D O U B L E; // alias for FLOAT8

NUMERIC : N U M E R I C;
DECIMAL : D E C I M A L; // alias for number

CHAR : C H A R;
VARCHAR : V A R C H A R;
VARCHAR2 : V A R C H A R '2';
NCHAR : N C H A R;
NVARCHAR : N V A R C H A R;
STRING : S T R I N G;

DATE : D A T E;
DATETIME : D A T E T I M E;
TIME : T I M E;
TIMETZ : T I M E T Z;
TIMESTAMP : T I M E S T A M P;
TIMESTAMP_LTZ : T I M E S T A M P UNDERLINE L T Z;
TIMESTAMP_NTZ : T I M E S T A M P UNDERLINE N T Z;
TIMESTAMP_TZ : T I M E S T A M P UNDERLINE T Z;
TIMESTAMPTZ : T I M E S T A M P T Z;

TEXT : T E X T;

BINARY : B I N A R Y;
VARBINARY : V A R B I N A R Y;
BLOB : B L O B;
BYTEA : B Y T E A; // alias for BLOB
OBJECT : O B J E C T;
STRUCT : S T R U C T; 
VARIANT : V A R I A N T;

// Operators
Similar_To : '~';
Not_Similar_To : '!~';
Similar_To_Case_Insensitive : '~*';
Not_Similar_To_Case_Insensitive : '!~*';


ASSIGN  : ':=';
EQUAL  : '=';
COLON :  ':';
SEMI_COLON :  ';';
COMMA : ',';
CONCATENATION_OPERATOR : VERTICAL_BAR VERTICAL_BAR;
NOT_EQUAL  : '<>' | '!=' | '~='| '^=' ;
LTH : '<' ;
LEQ : '<=';
GTH   : '>';
GEQ   : '>=';
LEFT_PAREN :  '(';
RIGHT_PAREN : ')';
PLUS  : '+';
MINUS : '-';
MULTIPLY: '*';
DIVIDE  : '/';
MODULAR : '%';
DOT		: Period;
UNDERLINE : '_';
VERTICAL_BAR : '|';
QUOTE : '\'';
DOUBLE_QUOTE : '"';
// Cast Operator
CAST_OPERATOR : '::';
 

NUMBER : Digit+;

/*
===============================================================================
 Identifiers
===============================================================================
*/
Bracket_Identifier	
	:	'['('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|Digit|'_'|' '|'.'|'-')*']'
	;

Variable_Identifier	
	:	LTH ('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|Digit|'_'|' '|'.'|'-')* GTH
	;

Extended_Variable_Identifier	
	:	LTH '['('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|Digit|'_'|' '|'.'|'-')* ']' ('.''['('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|Digit|'_'|' '|'.'|'-')* ']')? ('.''['('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|Digit|'_'|' '|'.'|'-')* ']')? GTH
	;

Population_Identifier	
	:	'{'('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|Digit|'_'|' '|'-'|'.')*'}'
	;


Identifier
  : ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|Digit|'_')*
  | DOUBLE_QUOTE ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|Digit|'_'|'-')* DOUBLE_QUOTE
  ;

EXPONEN : E ('+' | '-')?;
  
Numeric_Identifier
  :  Digit+ ('a'..'z'|'A'..'Z'|Digit|'_')*
  ;

Double_Quoted_Numeric_Identifier
  : DOUBLE_QUOTE Digit+ ('a'..'z'|'A'..'Z'|Digit|'_'|'-')* DOUBLE_QUOTE
  ;

BlockComment
    :   '/*' .*? '*/' -> skip
    ;

LineComment
    :   '--' ~[\r\n]* -> skip
    ;


fragment
Digit : '0'..'9';


fragment
Period : '.';


/*
===============================================================================
 Literal
===============================================================================
*/

// Some Unicode Character Ranges
fragment
Control_Characters                  :   '\u0001' .. '\u001F';
fragment
Extended_Control_Characters         :   '\u0080' .. '\u009F';

Character_String_Literal
  : QUOTE ( ESC_SEQ | ~('\\'|'\'') )* QUOTE
  ;

fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UNICODE_ESC
    |   OCTAL_ESC
    ;

fragment
OCTAL_ESC
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UNICODE_ESC
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;


/*
===============================================================================
 Whitespace Tokens
===============================================================================
*/

Space
  : ' ' -> skip
  ;

White_Space
  :	( Control_Characters  | Extended_Control_Characters )+ -> skip
  ;


BAD
  : . -> skip
  ; 