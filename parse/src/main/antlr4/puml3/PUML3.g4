grammar PUML3;
options {
language= Java;
}

// Java acion, ehods, tmstadditions

@header {

}


@members
{	
}
// Parser Elements
/* GRAMMAR */

// Grammar End Points

condition
	:	condition_principal EOF
	;
equation
	:	equation_principal EOF
	;

// BOOLEAN CONDITIONS
condition_principal
	:	condition_statement
	;
	
condition_statement
	:	and_condition_statement (OR and_condition_statement)* 
	;
	
and_condition_statement
	:	negative_condition_statement(AND negative_condition_statement)* 
	;


negative_condition_statement
	:	NOT  condition_parenthetical				# NEGATIVE_CONDITION_STMT
	|	condition_parenthetical						# CONDITION_STMT	;
	
condition_parenthetical
	:	condition_expression						# CONDITION_EXP
	|	LEFT_PARA condition_statement RIGHT_PARA 	# PARENTHETICAL_CONDITION
	;
	

condition_expression
	: truth_value
	| if_function_call
	| equation_formula boolean_comparator equation_formula
	| boolean_function_call
	| equation_formula IN in_list
	| equation_formula isnull_comparator
	;

in_list
	: LEFT_PARA in_list_elements RIGHT_PARA
	;

in_list_elements
	: equation_formula (COMMA equation_formula)*
	;

// ISNULL comparator (support 'is null', 'is not null')
isnull_comparator
	: IS NULL
	| IS NOT NULL
	| ISNULL
	| NOT ISNULL
	;

truth_value	:	TRUE | FALSE
	;
	
// Equations
equation_principal 
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
	:	IS_DATE | IS_NUMBER | IS_SPACES | IS NULL | IS NOT NULL | IS EMPTY | IS NOT EMPTY		
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
	: EQU
	| NOT_EQU
	| LT
	| LE
	| GT
	| GE
	| CONTAINS
	| ENDS WITH
	| STARTS WITH
	| MATCHES
	| NOT CONTAINS
	| NOT ENDS WITH
	| NOT STARTS WITH
	| NOT MATCHES
	;
	
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


/* LEXER */

/*
===============================================================================
  Primary tokens
===============================================================================
*/

TRUE    : T R U E;
FALSE   : F A L S E;
AND     : A N D;
OR      : O R;
NOT     : N O T;
IN      : I N;
DASH	: '-';
EQU     : '=';
NOT_EQU : '!=';
LT      : '<';
GT      : '>';
LE      : '<=';
GE      : '>=';
PLUS    : '+';
STAR    : '*';
SLASH   : '/';
MOD     : '%';
HAT     : '^';
BARBAR  : '||';
LEFT_PARA : '(';
RIGHT_PARA: ')';
DOT     : '.';
COMMA   : ',';
UNDERSCORE : '_';
BAR     : '|';
COLON   : ':';
EXCLAIM : '!' ;
SINGLE_QUOTE : '\'';
DBL_QUOTE : '"';

// PUML CONSTANTS/BUILTIN VALUES

SYSDATE 	: S Y S D A T E;
DD_INSERT	: D D '_' I N S E R T;
DD_UPDATE	: D D '_' U P D A T E;
DD_REJECT	: D D '_' R E J E C T;
DD_DELETE	: D D '_' D E L E T E;
NULL		: N U L L;

// PUML Functions
COALESCE	: C O A L E S C E;
DATESTR 	: D A T E S T R;
REGEXP_REPLACE	: R E G E X P '_' R E P L A C E;
REGEXP_EXTRACT	: R E G E X P '_' E X T R A C T;
DATE_ADD 	: D A T E '_' A D D;
DATE_SUB 	: D A T E '_' S U B;
CONCAT 		: C O N C A T;
COUNT 		: C O U N T;
INITCAP 	: I N I T C A P;
INSTR 		: I N S T R;
LOG 		: L O G;
POWER 		: P O W E R;
SQRT 		: S Q R T;
ABORT 		: A B O R T;
LOWER 		: L O W E R;
UPPER 		: U P P E R;
REPLACECHR 	: R E P L A C E C H R;
DATE_COMPARE 	: D A T E '_' C O M P A R E;
DATE_DIFF 	: D A T E '_' D I F F;
DECODE 		: D E C O D E;
ERROR 		: E R R O R;
GET_DATE_PART 	: G E T '_' D A T E '_' P A R T;
IF 			: I F;
IS_DATE 	: I S '_' D A T E;
IS_NUMBER 	: I S '_' N U M B E R;
IS_SPACES 	: I S '_' S P A C E S;
ISNULL	 	: I S N U L L;
LAST_DAY 	: L A S T '_' D A Y;
LENGTH 		: L E N G T H;
LOOKUP 		: L O O K U P;
LPAD 		: L P A D;
LTRIM 		: L T R I M;
MAX 		: M A X;
MIN 		: M I N;
REPLACESTR 	: R E P L A C E S T R;
REVERSE 	: R E V E R S E;
ROUND 		: R O U N D;
RPAD 		: R P A D;
RTRIM 		: R T R I M;
SET_DATE_PART 	: S E T '_' D A T E '_' P A R T;
SUBSTRING 	: S U B S T R I N G;
SUM 		: S U M;
TO_CHAR 	: T O '_' C H A R;
TO_DATE 	: T O '_' D A T E;
TO_DECIMAL 	: T O '_' D E C I M A L;
TO_FLOAT 	: T O '_' F L O A T;
TO_INTEGER 	: T O '_' I N T E G E R;
TRUNC 		: T R U N C;

ENDS 		: E N D S;
STARTS 		: S T A R T S;
WITH		: W I T H;
CONTAINS	: C O N T A I N S;
MATCHES		: M A T C H E S;

IS	: I S;
EMPTY	: E M P T Y;

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
    :   '['('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|'0'..'9'|'_'|' ')*']'
    ;
PUML_ID
    :   ('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|'0'..'9'|'_')*
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
	:   '\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')
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
  Tokens for Case Insensitive Keywords (fragments)
===============================================================================
*/
fragment A	:	'A' | 'a';
fragment B	:	'B' | 'b';
fragment C	:	'C' | 'c';
fragment D	:	'D' | 'd';
fragment E	:	'E' | 'e';
fragment F	:	'F' | 'f';
fragment G	:	'G' | 'g';
fragment H	:	'H' | 'h';
fragment I	:	'I' | 'i';
fragment J	:	'J' | 'j';
fragment K	:	'K' | 'k';
fragment L	:	'L' | 'l';
fragment M	:	'M' | 'm';
fragment N	:	'N' | 'n';
fragment O	:	'O' | 'o';
fragment P	:	'P' | 'p';
fragment Q	:	'Q' | 'q';
fragment R	:	'R' | 'r';
fragment S	:	'S' | 's';
fragment T	:	'T' | 't';
fragment U	:	'U' | 'u';
fragment V	:	'V' | 'v';
fragment W	:	'W' | 'w';
fragment X	:	'X' | 'x';
fragment Y	:	'Y' | 'y';
fragment Z	:	'Z' | 'z';

