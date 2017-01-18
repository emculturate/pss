grammar PUML3;
options {
language= Java;
}

// Java acion, ehods, tmstadditions

@header {
package pss.parse;

}


@members
{	
}
// Parser Elements
/* GRAMMAR */


// BOOLEAN CONDITIONS
condition
	:	condition_statement
	;
	
condition_statement
	:	and_condition_statement (OR and_condition_statement)* 
	;
	
and_condition_statement
	:	negative_condition_statement(AND negative_condition_statement)* 
	;


negative_condition_statement
	:	NOT  condition_parenthetical					# NEGATIVE_CONDITION_STMT
	|	condition_parenthetical						# CONDITION_STMT	;
	
condition_parenthetical
	:	condition_expression						# CONDITION_EXP
	|	LEFT_PARA condition_statement RIGHT_PARA 	# PARENTHETICAL_CONDITION
	;
	
condition_expression
	:	truth_value	
	|	if_function_call
	|	equation_formula boolean_comparator equation_formula
	|	boolean_function_call 
	|	equation_formula unit_boolean_comparator	

	;

unit_boolean_comparator
	:	IS_NULL | IS_NOT_NULL 
	;
	
truth_value	:	TRUE | FALSE
	;
	
// Equations
equation 
	:	 equation_formula;
	
equation_formula
	:	multdiv_equation_formula (add_sub_operator multdiv_equation_formula)* 
	;
	
multdiv_equation_formula
	:	power_equation_formula (mult_div_operator power_equation_formula)* 
	;

power_equation_formula
	:	string_equation_formula (power_operator string_equation_formula)* 
	;

string_equation_formula
	:	equation_parenthetical (string_operator equation_parenthetical)* 
	;

equation_parenthetical
	:	LEFT_PARA equation_formula RIGHT_PARA 	# PARENTHETICAL_EXPRESSION
	|	expression_term							# BARE_EXPRESSION
	;


expression_term
	:	number_term
	|	string_constant
	|	puml_builtin
	|	generic_reference
	|	function_call
	|	if_function_call
	//|	condition_statement -- an equation formula should be resolvable to a TRUE FALSE condition, but this makes the grammar left recursive...
	;

// FUNCTION CALLS

function_call
	:	puml_function_id  LEFT_PARA (bound_function_argument? (COMMA bound_function_argument)*) RIGHT_PARA 		# PUML_FUNCTION
	|	lookup_function_id LEFT_PARA lookup_var_ref (COMMA lookup_var_ref COMMA equation_formula)+ RIGHT_PARA	# LOOKUP_FUNCTION
	;
	
if_function_call
	:	if_function_id LEFT_PARA condition_statement COMMA bound_function_argument COMMA bound_function_argument RIGHT_PARA 
	;

boolean_function_call
	:	boolean_function_id  LEFT_PARA (bound_function_argument) RIGHT_PARA 
	//|	if_function_call
	;

bound_function_argument 
	: 	truth_value
	|	equation_formula
	;
	

lookup_var_ref
	:	transformation_ref DOT generic_reference DOT generic_reference # TRANSREF
	;
	
transformation_ref 
	:	 COLON generic_reference
	;
	
puml_function_id
	: DATE_ADD | DATE_SUB | CONCAT | COUNT | DATESTR | INITCAP | INSTR | LOG | POWER | SQRT | ABORT 
	| LOWER | UPPER | REPLACECHR | REPLACESTR | DATE_COMPARE | DATE_DIFF | DECODE | ERROR | GET_DATE_PART 
	| LAST_DAY | LENGTH | LPAD  | REVERSE | COALESCE | REGEXP_REPLACE | REGEXP_EXTRACT
	| LTRIM | MAX | MIN | ROUND | RPAD | RTRIM | SET_DATE_PART | SUBSTRING | SUM | TO_CHAR | TO_DATE 
	| TO_DECIMAL | TO_FLOAT | TO_INTEGER | TRUNC
	;

puml_builtin
	: SYSDATE | DD_INSERT | DD_UPDATE | DD_REJECT | DD_DELETE | NULL
	;
	
boolean_function_id
	:	IS_DATE | IS_NUMBER | IS_SPACES | IS_NULL | IS_EMPTY | IS_NOT_EMPTY		
	;
	
lookup_function_id
	: 	LOOKUP
	;

if_function_id
	: 	IF
	;
	
// Operators

mult_div_operator
	:	multiply | divide
	;

power_operator
	:	modulo | power
	;
	
add_sub_operator
	:	 minus | plus
	;

string_operator
	:	concat
	;
	
// operators
plus		:	PLUS;
minus		:	DASH;
multiply	:	STAR;
divide		:	SLASH;
concat		:	BARBAR;
modulo		:	MOD;
power		: 	HAT;


boolean_comparator	
	:	equals | not_equals | less_than | less_or_equal 
	| greater_than | greater_or_equal 
	| CONTAINS | ENDS_WITH | STARTS_WITH | MATCHES 
	| NOT_CONTAINS | NOT_ENDS_WITH | NOT_STARTS_WITH | NOT_MATCHES
	;

// boolean operators
equals		:	EQU;
not_equals	:	NOT_EQU;
less_than	:	LT;
less_or_equal	:	LE;
greater_than	:	GT;
greater_or_equal	:	GE;	
	
	
// SHARED

number_term
	:	DASH number # NEGATIVE
	|	number		# POSITIVE
	;

number
	:	INT | LONG | FLOAT | DOUBLE
	;
	
generic_reference
	:	(BRCKT_ID)  	# ATTR_REF
	|   PUML_ID			# VAR_REF
	;

string_constant 
	:	QUOTED_CONSTANT
	;


//char_list
//	:	CHARACTERS *
//	;

/* LEXER */
	
// Special Tokens Imaginary tokens OBSOLETE in V4?
/*
ARGUMENT_LIST	: 	'argument_list';
TRANSREF	: 	'transref';
LKP		:	'lkp';
LKPLIST		:	'lkplist';
COND		:	'cond';
NEGATIVE	:	'negative';
VARIABLE_REF	:	'variable_ref';
ATTR_REF	:	'attr_ref';
PARENTHETICAL
	:	'parenthetical'	; */
	
// Primary tokens
AND		:	('A'|'a')('N'|'n')('D'|'d');
BARBAR	:	'||';
FALSE	:	('F'|'f')('A'|'a')('L'|'l')('S'|'s')('E'|'e');
GE		:	'>=';
LE		:	'<=';
NOT		:	('N'|'n')('O'|'o')('T'|'t');
NOT_EQU	:	'!=';
OR		:	('O'|'o')('R'|'r');
TRUE	:	('T'|'t')('R'|'r')('U'|'u')('E'|'e');

LEFT_PARA	:	'(';
DOT		:	'.';
COMMA		:	',';
RIGHT_PARA	:	')';
UNDERSCORE	:	'_';
BAR		:	'|';	
COLON	:	':';
DASH	: 	'-';
EQU		:	'=';
GT		:	'>';
HAT		:	'^';
LT		:	'<';
MOD		:	'%';
EXCLAIM	:	'!';
PLUS	:	'+';
SINGLE_QUOTE	:	'\'';
DBL_QUOTE	:	'"';
SLASH		:	'/';
STAR		:	'*';

// PUML CONSTANTS/BUILTIN VALUES

SYSDATE 	: ('S'|'s')('Y'|'y')('S'|'s')('D'|'d')('A'|'a')('T'|'t')('E'|'e');
DD_INSERT	: ('D'|'d')('D'|'d')'_'('I'|'i')('N'|'n')('S'|'s')('E'|'e')('R'|'r')('T'|'t');
DD_UPDATE	: ('D'|'d')('D'|'d')'_'('U'|'u')('P'|'p')('D'|'d')('A'|'a')('T'|'t')('E'|'e');
DD_REJECT	: ('D'|'d')('D'|'d')'_'('R'|'r')('E'|'e')('J'|'j')('E'|'e')('C'|'c')('T'|'t');
DD_DELETE	: ('D'|'d')('D'|'d')'_'('D'|'d')('E'|'e')('L'|'l')('E'|'e')('T'|'t')('E'|'e');
NULL		: ('N'|'n')('U'|'u')('L'|'l')('L'|'l');

// PUML Functions
COALESCE	: ('C'|'c')('O'|'o')('A'|'a')('L'|'l')('E'|'e')('S'|'s')('C'|'c')('E'|'e');
DATESTR 	: ('D'|'d')('A'|'a')('T'|'t')('E'|'e')('S'|'s')('T'|'t')('R'|'r');
REGEXP_REPLACE	: ('R'|'r')('E'|'e')('G'|'g')('E'|'e')('X'|'x')('P'|'p')'_'('R'|'r')('E'|'e')('P'|'p')('L'|'l')('A'|'a')('C'|'c')('E'|'e');
REGEXP_EXTRACT	: ('R'|'r')('E'|'e')('G'|'g')('E'|'e')('X'|'x')('P'|'p')'_'('E'|'e')('X'|'x')('T'|'t')('R'|'r')('A'|'a')('C'|'c')('T'|'t');
DATE_ADD 	: ('D'|'d')('A'|'a')('T'|'t')('E'|'e')'_'('A'|'a')('D'|'d')('D'|'d');
DATE_SUB 	: ('D'|'d')('A'|'a')('T'|'t')('E'|'e')'_'('S'|'s')('U'|'u')('B'|'b');
CONCAT 		: ('C'|'c')('O'|'o')('N'|'n')('C'|'c')('A'|'a')('T'|'t');
COUNT 		: ('C'|'c')('O'|'o')('U'|'u')('N'|'n')('T'|'t');
IN 			: ('I'|'i')('N'|'n');
INITCAP 	: ('I'|'i')('N'|'n')('I'|'i')('T'|'t')('C'|'c')('A'|'a')('P'|'p');
INSTR 		: ('I'|'i')('N'|'n')('S'|'s')('T'|'t')('R'|'r');
LOG 		: ('L'|'l')('O'|'o')('G'|'g');
POWER 		: ('P'|'p')('O'|'o')('W'|'w')('E'|'e')('R'|'r');
SQRT 		: ('S'|'s')('Q'|'q')('R'|'r')('T'|'t');
ABORT 		: ('A'|'a')('B'|'b')('O'|'o')('R'|'r')('T'|'t');
LOWER 		: ('L'|'l')('O'|'o')('W'|'w')('E'|'e')('R'|'r');
UPPER 		: ('U'|'u')('P'|'p')('P'|'p')('E'|'e')('R'|'r');
REPLACECHR 	: ('R'|'r')('E'|'e')('P'|'p')('L'|'l')('A'|'a')('C'|'c')('E'|'e')('C'|'c')('H'|'h')('R'|'r');
DATE_COMPARE 	: ('D'|'d')('A'|'a')('T'|'t')('E'|'e')'_'('C'|'c')('O'|'o')('M'|'m')('P'|'p')('A'|'a')('R'|'r')('E'|'e');
DATE_DIFF 	: ('D'|'d')('A'|'a')('T'|'t')('E'|'e')'_'('D'|'d')('I'|'i')('F'|'f')('F'|'f');
DECODE 		: ('D'|'d')('E'|'e')('C'|'c')('O'|'o')('D'|'d')('E'|'e');
ERROR 		: ('E'|'e')('R'|'r')('R'|'r')('O'|'o')('R'|'r');
GET_DATE_PART 	: ('G'|'g')('E'|'e')('T'|'t')'_'('D'|'d')('A'|'a')('T'|'t')('E'|'e')'_'('P'|'p')('A'|'a')('R'|'r')('T'|'t');
IF 		: ('I'|'i')('F'|'f');
IS_DATE 	: ('I'|'i')('S'|'s')'_'('D'|'d')('A'|'a')('T'|'t')('E'|'e');
IS_NUMBER 	: ('I'|'i'('S'|'s'))'_'('N'|'n')('U'|'u')('M'|'m')('B'|'b')('E'|'e')('R'|'r');
IS_SPACES 	: ('I'|'i')('S'|'s')'_'('S'|'s')('P'|'p')('A'|'a')('C'|'c')('E'|'e')('S'|'s');
LAST_DAY 	: ('L'|'l')('A'|'a')('S'|'s')('T'|'t')'_'('D'|'d')('A'|'a')('Y'|'y');
LENGTH 		: ('L'|'l')('E'|'e')('N'|'n')('G'|'g')('T'|'t')('H'|'h');
LOOKUP 		: ('L'|'l')('O'|'o')('O'|'o')('K'|'k')('U'|'u')('P'|'p');
LPAD 		: ('L'|'l')('P'|'p')('A'|'a')('D'|'d');
LTRIM 		: ('L'|'l')('T'|'t')('R'|'r')('I'|'i')('M'|'m');
MAX 		: ('M'|'m')('A'|'a')('X'|'x');
MIN 		: ('M'|'m')('I'|'i')('N'|'n');
REPLACESTR 	: ('R'|'r')('E'|'e')('P'|'p')('L'|'l')('A'|'a')('C'|'c')('E'|'e')('S'|'s')('T'|'t')('R'|'r');
REVERSE 	: ('R'|'r')('E'|'e')('V'|'v')('E'|'e')('R'|'r')('S'|'s')('E'|'e');
ROUND 		: ('R'|'r')('O'|'o')('U'|'u')('N'|'n')('D'|'d');
RPAD 		: ('R'|'r')('P'|'p')('A'|'a')('D'|'d');
RTRIM 		: ('R'|'r')('T'|'t')('R'|'r')('I'|'i')('M'|'m');
SET_DATE_PART 	: ('S'|'s')('E'|'e')('T'|'t')'_'('D'|'d')('A'|'a')('T'|'t')('E'|'e')'_'('P'|'p')('A'|'a')('R'|'r')('T'|'t');
SUBSTRING	: ('S'|'s')('U'|'u')('B'|'b')('S'|'s')('T'|'t')('R'|'r')('I'|'i')('N'|'n')('G'|'g');
SUM 		: ('S'|'s')('U'|'u')('M'|'m');
TO_CHAR 	: ('T'|'t')('O'|'o')'_'('C'|'c')('H'|'h')('A'|'a')('R'|'r');
TO_DATE 	: ('T'|'t')('O'|'o')'_'('D'|'d')('A'|'a')('T'|'t')('E'|'e');
TO_DECIMAL 	: ('T'|'t')('O'|'o')'_'('D'|'d')('E'|'e')('C'|'c')('I'|'i')('M'|'m')('A'|'a')('L'|'l');
TO_FLOAT 	: ('T'|'t')('O'|'o')'_'('F'|'f')('L'|'l')('O'|'o')('A'|'a')('T'|'t');
TO_INTEGER 	: ('T'|'t')('O'|'o')'_'('I'|'i')('N'|'n')('T'|'t')('E'|'e')('G'|'g')('E'|'e')('R'|'r');
TRUNC 		: ('T'|'t')('R'|'r')('U'|'u')('N'|'n')('C'|'c');

CONTAINS	: ('C'|'c')('O'|'o')('N'|'n')('T'|'t')('A'|'a')('I'|'i')('N'|'n')('S'|'s');
ENDS_WITH	: ('E'|'e')('N'|'n')('D'|'d')('S'|'s')' '+('W'|'w')('I'|'i')('T'|'t')('H'|'h');
MATCHES		: ('M'|'m')('A'|'a')('T'|'t')('C'|'c')('H'|'h')('E'|'e')('S'|'s');
STARTS_WITH	: ('S'|'s')('T'|'t')('A'|'a')('R'|'r')('T'|'t')('S'|'s')' '+('W'|'w')('I'|'i')('T'|'t')('H'|'h');
NOT_CONTAINS	: ('N'|'n')('O'|'o')('T'|'t')' '+('C'|'c')('O'|'o')('N'|'n')('T'|'t')('A'|'a')('I'|'i')('N'|'n')('S'|'s');
NOT_ENDS_WITH	: ('N'|'n')('O'|'o')('T'|'t')' '+('E'|'e')('N'|'n')('D'|'d')('S'|'s')' '+('W'|'w')('I'|'i')('T'|'t')('H'|'h');
NOT_MATCHES		: ('N'|'n')('O'|'o')('T'|'t')' '+('M'|'m')('A'|'a')('T'|'t')('C'|'c')('H'|'h')('E'|'e')('S'|'s');
NOT_STARTS_WITH	: ('N'|'n')('O'|'o')('T'|'t')' '+('S'|'s')('T'|'t')('A'|'a')('R'|'r')('T'|'t')('S'|'s')' '+('W'|'w')('I'|'i')('T'|'t')('H'|'h');

IS_EMPTY	: ('I'|'i')('S'|'s')' '+('E'|'e')('M'|'m')('P'|'p')('T'|'t')('Y'|'y');
IS_NOT_EMPTY: ('I'|'i')('S'|'s')' '+('N'|'n')('O'|'o')('T'|'t')' '+('E'|'e')('M'|'m')('P'|'p')('T'|'t')('Y'|'y');
IS_NULL 	: ('I'|'i')('S'|'s')' '+('N'|'n')('U'|'u')('L'|'l')('L'|'l');
IS_NOT_NULL	: ('I'|'i')('S'|'s')' '+('N'|'n')('O'|'o')('T'|'t')' '+('N'|'n')('U'|'u')('L'|'l')('L'|'l');


// standard token rules
	
INT :	('0'..'9')+'I'?
    ;

LONG :	('0'..'9')  + 'L'
    ;

DOUBLE	:	FLOAT 'D';

FLOAT
    :   ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    |   '.' ('0'..'9')+ EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ;

BRCKT_ID	
	:	'['('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|'0'..'9'|'_'|' ')*']'
	;

PUML_ID	
	:	('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|'0'..'9'|'_')*
	;

STRING_VALUE 
	:	'"'('A'..'Z'|'a'..'z'|'0'..'9'|'_'|' '|'|'|'['|']'|'+')*'"'
	;

QUOTED_CONSTANT
	: SINGLE_QUOTE (.)*? SINGLE_QUOTE 
	;

// skip these

COMMENT
    :   '--' ~('\n'|'\r')* ((('\r')*'\n')+ | EOF ) -> skip
    ;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) -> skip
    ;

//Reusable fragments

fragment
EXPONENT : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

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


