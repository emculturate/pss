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
  SQL statement (Start Symbol
===============================================================================
*/
sql
  : (with_query
  | create_table_as_expression
  ) (SEMI_COLON)? EOF
  ;
  
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
 * POSTGRES:
[ WITH [ RECURSIVE ] with_query [, ...] ]
INSERT INTO table_name [ AS alias ] [ ( column_name [, ...] ) ]
    { DEFAULT VALUES | VALUES ( { expression | DEFAULT } [, ...] ) [, ...] | query }
    [ ON CONFLICT [ conflict_target ] conflict_action ]
    [ RETURNING * | output_expression [ [ AS ] output_name ] [, ...] ]
 */
insert_expression
  : INSERT INTO table_primary (LEFT_PAREN column_reference_list RIGHT_PAREN)? VALUES subquery returning?
  ;
  
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
  : UPDATE table_primary SET assignment_expression_list from_clause? where_clause? returning?
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
    
create_table_as_expression
  : CREATE TABLE AS query_expression
  ;
  
/*
===============================================================================
  7.13 <query expression>
===============================================================================
*/
query_expression
  : intersected_query
  ;
   
intersected_query
  : unionized_query (intersect_clause unionized_query)*
  ;

intersect_clause
  : interset_operator set_qualifier?
  ;
  
interset_operator
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

query_primary
  : query_specification
  | subquery
  ;

subquery
  :  LEFT_PAREN query_expression RIGHT_PAREN
  ;

query_specification
  : SELECT set_qualifier? select_list 
  ( from_clause
    where_clause?
    groupby_clause?
    having_clause?
    orderby_clause?
    limit_clause?)?
  ;

from_clause
  : FROM table_reference_list
  ;

table_reference_list
  : table_primary 
    ((COMMA table_primary)
     | (unqualified_join right=table_primary)
     | (qualified_join right=table_primary s=join_specification))*
  ;

table_primary
  : table_or_query_name as_clause?
  | subquery as_clause? 
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
  
// DETAILS OF QUERY

select_list
  : select_item (COMMA select_item)*
  ;

select_item
  : value_expression as_clause?
  | select_all_columns
  ;

select_all_columns
  : (tb_name=Identifier DOT)? MULTIPLY
  ;

table_or_query_name
  : identifier   (DOT  (simple_numeric_identifier|identifier))?  (DOT  (simple_numeric_identifier|identifier))?
  ;

set_qualifier
  : DISTINCT
  | ALL
  ;

as_clause
  : (AS)? identifier
  ;

column_reference_list
  : column_reference (COMMA column_reference)*
  ;

column_reference
  : (tb_name=identifier DOT)? name=identifier
  ;

/*
===============================================================================
  6.3 <value_expression_primary>
===============================================================================
*/
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
  | subquery
  | case_expression
  | cast_specification
  | routine_invocation
  | window_over_partition_expression
  ;

aggregate_function
  : COUNT LEFT_PAREN MULTIPLY RIGHT_PAREN	# count_all_aggregate
  | (set_function_type|set_qualifier_type) LEFT_PAREN set_qualifier? value_expression RIGHT_PAREN filter_clause?		# general_set_function 
  ;

set_function_type
  : AVG
  | FIRST_VALUE
  | LAST_VALUE
  | MAX
  | MIN
  | SUM
  | COUNT
  | RANK
  | ROW_NUMBER
  | STDDEV_POP
  | STDDEV_SAMP
  | VAR_SAMP
  | VAR_POP
  ;

set_qualifier_type  
  : EVERY
  | ANY
  | SOME
  | COLLECT
  | FUSION
  | INTERSECTION
  ;

filter_clause
  : FILTER LEFT_PAREN WHERE search_condition RIGHT_PAREN
  ;

grouping_operation
  : GROUPING LEFT_PAREN column_reference_list RIGHT_PAREN
  ;

/*
===============================================================================
  6.11 <case expression>
===============================================================================
*/

case_expression
  : CASE boolean_value_expression when_clause_list ( else_clause  )? END
  | CASE when_clause_list (else_clause)? END
  ;

when_clause_list
   : (searched_when_clause)+ 
   ;

searched_when_clause
  : WHEN c=search_condition THEN r=result
  ;

else_clause
  : ELSE r=result
  ;

result
  : value_expression | null_literal
  ;

null_literal
  : NULL
  ;
  
  /*
   * Functions over partitions
   * rank() OVER (partition by k_stfd order by OBSERVATION_TM desc, row_num desc)
   */
 window_over_partition_expression
   : window_function over_clause
   ;
   
window_function
   : set_function_type LEFT_PAREN value_expression? RIGHT_PAREN
   ;
   
over_clause
   : OVER LEFT_PAREN (partition_by_clause? orderby_clause?) RIGHT_PAREN
   ;
    
partition_by_clause
   : PARTITION BY select_list
   ;
   
/*
===============================================================================
  6.12 <cast specification>
===============================================================================
*/

cast_specification
  : CAST LEFT_PAREN value_expression AS data_type RIGHT_PAREN
  ;


/*
===============================================================================
  6.25 <value expression>
===============================================================================
*/
value_expression
  : common_value_expression
  | row_value_expression
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
  : value_expression_primary (CAST_EXPRESSION data_type)*
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
     trim_source=string_value_expression  # mysql_trim_operands
  | trim_source=string_value_expression COMMA 
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
  : boolean_primary is_clause?						# basic_clause
  | LEFT_PAREN boolean_value_expression RIGHT_PAREN # paren_clause
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
  | pattern_matching_predicate // like predicate and other similar predicates
  | null_predicate
  | exists_predicate
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
  ;

/*
===============================================================================
  7.5 <from clause>
===============================================================================
*/

column_name_list
  :  identifier  ( COMMA identifier  )*
  ;

/*
===============================================================================
  7.8 <where clause>
===============================================================================
*/
where_clause
  : WHERE search_condition
  ;

search_condition
  : value_expression // instead of boolean_value_expression, we use value_expression for more flexibility.
  ;

orderby_clause
  : ORDER BY sort_specifier_list
  ;

/*
===============================================================================
  7.9 <group by clause>
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
  : left=row_value_predicand c=comp_op right=row_value_predicand
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
  8.3 <between predicate>
===============================================================================
*/

between_predicate
  : predicand=row_value_predicand between_predicate_part_2
  ;

between_predicate_part_2
  : (not)? BETWEEN symmetry? begin=row_value_predicand AND end=row_value_predicand
  ;

symmetry
   : (ASYMMETRIC | SYMMETRIC)
   ;

/*
===============================================================================
  8.4 <in predicate>
===============================================================================
*/

in_predicate
  : additive_expression  not? IN in_predicate_value
  ;

in_predicate_value
  : subquery
  | LEFT_PAREN in_value_list RIGHT_PAREN
  ;

in_value_list
  : row_value_expression  ( COMMA row_value_expression )*
  ;

/*
===============================================================================
  8.5, 8.6 <pattern matching predicate>

  Specify a pattern-matching comparison.
===============================================================================
*/

pattern_matching_predicate
  : f=row_value_predicand pattern_matcher s=Character_String_Literal
  ;

pattern_matcher
  : not? negativable_matcher
  | regex_matcher
  ;

negativable_matcher
  : LIKE
  | ILIKE
  | SIMILAR TO
  | REGEXP
  | RLIKE
  ;

regex_matcher
  : Similar_To
  | Not_Similar_To
  | Similar_To_Case_Insensitive
  | Not_Similar_To_Case_Insensitive
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
  14.1 <declare cursor>
===============================================================================
*/

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

limit_clause
  : LIMIT e=additive_expression
  ;

null_ordering
  : NULL FIRST
  | NULL LAST
  ;

/*
===============================================================================
  5.2 <token and separator>

  Specifying lexical units (tokens and separators) that participate in SQL language
===============================================================================
*/

identifier
  : simple_identifier
  | puml_identifier
  | nonreserved_keywords
  ;

simple_identifier
   :	Identifier
   ;

puml_identifier
   :    Bracket_Identifier
   ;
 
simple_numeric_identifier
   :	Numeric_Identifier
   |	NUMBER
   ;
   
nonreserved_keywords
  : AVG
  | BETWEEN
  | BY
  | CENTURY
  | CHARACTER
  | COALESCE
  | COLLECT
  | COLUMN
  | COUNT
  | CUBE
  | DAY
  | DEC
  | DECADE
  | DOW
  | DOY
  | DROP
  | EPOCH
  | EVERY
  | EXISTS
  | EXTERNAL
  | EXTRACT
  | FILTER
  | FIRST
  | FORMAT
  | FUSION
  | GROUPING
  | HASH
  | INDEX
  | INSERT
  | INTERSECTION
  | ISODOW
  | ISOYEAR
  | LAST
  | LESS
  | LIST
  | LOCATION
  | MAX
  | MAXVALUE
  | MICROSECONDS
  | MILLENNIUM
  | MILLISECONDS
  | MIN
  | MINUTE
  | MONTH
  | NATIONAL
  | NULLIF
  | OVER
  | OVERWRITE
  | PARTITION
  | PARTITIONS
  | PRECISION
  | PURGE
  | QUARTER
  | RANGE
  | REGEXP
  | RETURNING
  | RLIKE
  | ROLLUP
  | SECOND
  | SET
  | SIMILAR
  | STDDEV_POP
  | STDDEV_SAMP
  | SUBPARTITION
  | SUM
  | TABLESPACE
  | THAN
  | TIMEZONE
  | TIMEZONE_HOUR
  | TIMEZONE_MINUTE
  | TRIM
  | TO
  | UNKNOWN
  | UPDATE
  | VALUES
  | VAR_POP
  | VAR_SAMP
  | VARYING
  | WEEK
  | YEAR
  | ZONE

  | BIGINT
  | BIT
  | BLOB
  | BOOL
  | BOOLEAN
  | BYTEA
  | CHAR
  | DATE
  | DECIMAL
  | DOUBLE
  | FLOAT
  | FLOAT4
  | FLOAT8
  | INET4
  | INT
  | INT1
  | INT2
  | INT4
  | INT8
  | INTEGER
  | NCHAR
  | NUMERIC
  | NVARCHAR
  | REAL
  | SMALLINT
  | TEXT
  | TIME
  | TIMESTAMP
  | TIMESTAMPTZ
  | TIMETZ
  | TINYINT
  | VARBINARY
  | VARBIT
  | VARCHAR
  ;

/*
===============================================================================
  6.4 <unsigned value specification>
===============================================================================
*/

signed_numerical_literal
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
  6.1 <data types>
===============================================================================
*/

data_type
  : character_string_type
  | national_character_string_type
  | binary_large_object_string_type
  | numeric_type
  | boolean_type
  | datetime_type
  | bit_type
  | binary_type
  | network_type
  ;

network_type
  : INET4
  ;

character_string_type
  : CHARACTER type_length?
  | CHAR type_length?
  | CHARACTER VARYING type_length?
  | CHAR VARYING type_length?
  | VARCHAR type_length?
  | TEXT
  ;

type_length
  : LEFT_PAREN NUMBER RIGHT_PAREN
  ;

national_character_string_type
  : NATIONAL CHARACTER type_length?
  | NATIONAL CHAR type_length?
  | NCHAR type_length?
  | NATIONAL CHARACTER VARYING type_length?
  | NATIONAL CHAR VARYING type_length?
  | NCHAR VARYING type_length?
  | NVARCHAR type_length?
  ;

binary_large_object_string_type
  : BLOB type_length?
  | BYTEA type_length?
  ;

numeric_type
  : exact_numeric_type | approximate_numeric_type
  ;

exact_numeric_type
  : NUMERIC (precision_param)?
  | DECIMAL (precision_param)?
  | DEC (precision_param)?
  | INT1
  | TINYINT
  | INT2
  | SMALLINT
  | INT4
  | INT
  | INTEGER
  | INT8
  | BIGINT
  ;

approximate_numeric_type
  : FLOAT (precision_param)?
  | FLOAT4
  | REAL
  | FLOAT8
  | DOUBLE
  | DOUBLE PRECISION
  ;

precision_param
  : LEFT_PAREN precision=NUMBER RIGHT_PAREN
  | LEFT_PAREN precision=NUMBER COMMA scale=NUMBER RIGHT_PAREN
  ;

boolean_type
  : BOOLEAN
  | BOOL
  ;

datetime_type
  : DATE
  | TIME
  | TIME WITH TIME ZONE
  | TIMETZ
  | TIMESTAMP
  | TIMESTAMP WITH TIME ZONE
  | TIMESTAMPTZ
  ;

bit_type
  : BIT type_length?
  | VARBIT type_length?
  | BIT VARYING type_length?
  ;

binary_type
  : BINARY type_length?
  | BINARY VARYING type_length?
  | VARBINARY type_length?
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
ASC : A S C;

BOTH : B O T H;

CASE : C A S E;
CAST : C A S T;
CREATE : C R E A T E;
CROSS : C R O S S;

DESC : D E S C;
DISTINCT : D I S T I N C T;

END : E N D;
ELSE : E L S E;
EXCEPT : E X C E P T;

FALSE : F A L S E;
FULL : F U L L;
FROM : F R O M;

GROUP : G R O U P;

HAVING : H A V I N G;

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

ON : O N;
OUTER : O U T E R;
OR : O R;
ORDER : O R D E R;
RIGHT : R I G H T;
RETURNING : R E T U R N I N G;
SELECT : S E L E C T;
SOME : S O M E;
SYMMETRIC : S Y M M E T R I C;

TABLE : T A B L E;
THEN : T H E N;
TRAILING : T R A I L I N G;
TRUE : T R U E;

UNION : U N I O N;
UNIQUE : U N I Q U E;
USING : U S I N G;

WHEN : W H E N;
WHERE : W H E R E;
WITH : W I T H;

/*
===============================================================================
  Non Reserved Keywords
===============================================================================
*/
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

DAY : D A Y;
DEC : D E C;
DECADE : D E C A D E;
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
FIRST_VALUE : F I R S T '_' V A L U E;
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

LAST : L A S T;
LAST_VALUE : L A S T '_' V A L U E;
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
NULLIF : N U L L I F;

OVER : O V E R;
OVERWRITE : O V E R W R I T E;

PARTITION : P A R T I T I O N;
PARTITIONS : P A R T I T I O N S;
PRECISION : P R E C I S I O N;
PURGE : P U R G E;

QUARTER : Q U A R T E R;

RANGE : R A N G E;
RANK : R A N K;
REGEXP : R E G E X P;
RLIKE : R L I K E;
ROLLUP : R O L L U P;
ROW_NUMBER : R O W '_' N U M B E R;

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
UNKNOWN : U N K N O W N;

VALUES : V A L U E S;
VAR_SAMP : V A R UNDERLINE S A M P;
VAR_POP : V A R UNDERLINE P O P;
VARYING : V A R Y I N G;

WEEK : W E E K;

YEAR : Y E A R;

ZONE : Z O N E;


/*
===============================================================================
  Data Type Tokens
===============================================================================
*/
BOOLEAN : B O O L E A N;
BOOL : B O O L;
BIT : B I T;
VARBIT : V A R B I T;

INT1 : I N T '1';
INT2 : I N T '2';
INT4 : I N T '4';
INT8 : I N T '8';

TINYINT : T I N Y I N T; // alias for INT1
SMALLINT : S M A L L I N T; // alias for INT2
INT : I N T; // alias for INT4
INTEGER : I N T E G E R; // alias - INT4
BIGINT : B I G I N T; // alias for INT8

FLOAT4 : F L O A T '4';
FLOAT8 : F L O A T '8';

REAL : R E A L; // alias for FLOAT4
FLOAT : F L O A T; // alias for FLOAT8
DOUBLE : D O U B L E; // alias for FLOAT8

NUMERIC : N U M E R I C;
DECIMAL : D E C I M A L; // alias for number

CHAR : C H A R;
VARCHAR : V A R C H A R;
NCHAR : N C H A R;
NVARCHAR : N V A R C H A R;

DATE : D A T E;
TIME : T I M E;
TIMETZ : T I M E T Z;
TIMESTAMP : T I M E S T A M P;
TIMESTAMPTZ : T I M E S T A M P T Z;

TEXT : T E X T;

BINARY : B I N A R Y;
VARBINARY : V A R B I N A R Y;
BLOB : B L O B;
BYTEA : B Y T E A; // alias for BLOB

INET4 : I N E T '4';

// Operators
Similar_To : '~';
Not_Similar_To : '!~';
Similar_To_Case_Insensitive : '~*';
Not_Similar_To_Case_Insensitive : '!~*';

// Cast Operator
CAST_EXPRESSION
  : COLON COLON
  ;

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
 

NUMBER : Digit+;

/*
===============================================================================
 Identifiers
===============================================================================
*/
Bracket_Identifier	
	:	'['('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|'0'..'9'|'_'|' ')*']'
	;

Identifier
  : ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|Digit|'_')*
  ;

EXPONEN : E ('+' | '-')?;
  
Numeric_Identifier
  :  Digit+ ('a'..'z'|'A'..'Z'|Digit|'_')*
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