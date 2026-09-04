grammar PUMLLanguage;
options {
language= Java;
output=AST;
}

// Java acion, ehods, tmstadditions

@header {
package com.advisory.smap.parser;

}
@lexer::header {
package com.advisory.smap.parser;

}

@members
{	
}
// Parser Elements
/* GRAMMAR */

prog 	
	
	:	analysis
	;

analysis 
	
	:	ANALYSIS_HEAD 
		analysis_name 
		(analysis_def)* 
		ANALYSIS_END 
	;
	
analysis_name 
	:	COMPLETE_ID
	;

analysis_def	
	:	entity 
	|	population 
	;
	
population
	:	population_id 
		population_options 
		population_body 
	;

population_options
	:  (POPULATION_OPT entity_id) 
	   population_of 
	   (POPULATION_CONS condition_statement)?
	   (storage_option)?
	;

population_of 
	:	POPULATION_DEF condition_statement
	|	POPULATION_DEF population_id set_operation b = population_id
	|	SUBPOPULATION_OPT population_id
	;


entity 	
	:	entity_id	population_body
	;
	
population_body
	:	LEFT_PARA RIGHT_PARA 
	|	LEFT_PARA (attribute_reference -> attribute_reference) (COMMA a=attribute_reference -> ^(POP_BODY $population_body $a))* RIGHT_PARA   
	;

set_operation 
	:	UNION 
	| 	INTERSECTION 
	;
	
storage_option	
	// returns [String storageStyle]
	:	STORAGE_OPT storage_type
	;

storage_type : OBJECT | TRANSACTION | OBSERVATION | EVENT | SPAN;


// Universal Quantification
universal_quantification
	:	 universal_quant_prolog+ LEFT_PARA condition_statement RIGHT_PARA
	;

universal_quant_prolog
	:	(FOR entity_id column_id?) -> ^(FOR entity_id  column_id?)
	;
	
// RULES
rules
	: rule_expression*
	| quantified_rules
	;

quantified_rules
	:	universal_quant_prolog+ LEFT_BRACE (rule_expression)+ RIGHT_BRACE -> ^(RULE_FAMILY ^(RULE_QUANT universal_quant_prolog+) ^(DEPENDENT_RULES rule_expression+));


// Rule Expression
rule_expression
	:	rule_prolog^ rule_body
	;

rule_prolog
	:	(RULE rule_id CONDITION clause_id? LEFT_PARA condition_statement RIGHT_PARA) -> ^(RULE rule_id ^(CONDITION condition_statement clause_id?))
	;

rule_body
	: 	(before_expression | after_expression)+  samples_expression*
	;
	
before_expression
	:	(BEFORE clause_id? LEFT_PARA condition_statement RIGHT_PARA) -> ^(BEFORE condition_statement clause_id?)
	;

after_expression
	:	(AFTER clause_id? LEFT_PARA condition_statement RIGHT_PARA) -> ^(AFTER condition_statement clause_id?)
	;
	
samples_expression
	:	(SAMPLES clause_id? OF entity_id LEFT_PARA condition_statement RIGHT_PARA)  -> ^(SAMPLES entity_id condition_statement clause_id?)
	;

// BINDINGS
	
// Condition Binding
condition_binding
	:	condition_id GIVEN_BY LEFT_PARA condition_statement RIGHT_PARA;

// equation Binding
equation_binding
	:	equation_id GIVEN_BY LEFT_PARA equation_formula RIGHT_PARA;



// BOOLEAN CONDITIONS
condition 
	:	condition_statement
	;
	
condition_statement
	:	(and_condition_statement -> and_condition_statement) (OR a=and_condition_statement -> ^(OR $condition_statement $a))* 
	;
	
and_condition_statement
	:	(negative_condition_statement -> negative_condition_statement)(AND o=negative_condition_statement -> ^(AND $and_condition_statement $o))* 
	;

negative_condition_statement
	:	NOT condition_parenthetical  ->  ^(NOT condition_parenthetical)
	|	condition_parenthetical
	;
	
condition_parenthetical
// if it looks like an expression, then interpret it that way, else, see if it's a nested statement
	:	(condition_expression) => condition_expression
	|	LEFT_PARA condition_statement RIGHT_PARA -> condition_statement
	;
	
condition_expression
	:	(equation_formula -> equation_formula) (boolean_comparator a=equation_formula -> ^(boolean_comparator $condition_expression $a))* 
	// (equation_formula boolean_comparator) => equation_formula boolean_comparator equation_formula -> ^(boolean_comparator equation_formula+)
	//|	(equation_formula IN) => equation_formula IN LEFT_PARA  equation_formula (COMMA equation_formula)* RIGHT_PARA -> ^(IN equation_formula equation_formula+)
	|	(equation_formula BETWEEN) => equation_formula BETWEEN m=equation_formula COLON x=equation_formula -> ^(BETWEEN equation_formula ^(RANGE equation_formula+))
	//|	equation_formula
	|	(condition_id (IS_FALSE | IS_TRUE)) => condition_id (IS_FALSE | IS_TRUE)^
	|	at_least_clause
		//|	attribute_reference boolean_comparator attribute_reference -> ^(boolean_comparator attribute_reference ^(ATTR_ID attribute_reference))
	//|	attribute_reference boolean_comparator^ expression_term
	//|	attribute_reference string_comparator^ string_constant 
	//|	attribute_reference unary_condition^
	;

at_least_clause
	: 	at_header INT (entity_id) column_id? WITH LEFT_PARA condition_statement RIGHT_PARA 
	-> ^(at_header INT entity_id  ^(WITH condition_statement) column_id?)
	| 	ANY (entity_id) column_id? WITH LEFT_PARA condition_statement RIGHT_PARA 
	-> ^(AT_LEAST {new CommonTree(new CommonToken(INT, "1"))} entity_id  ^(WITH condition_statement) column_id?)
	| 	NOSUCH (entity_id) column_id? WITH LEFT_PARA condition_statement RIGHT_PARA 
	-> ^(AT_MOST {new CommonTree(new CommonToken(INT, "0"))} entity_id  ^(WITH condition_statement) column_id?)
	;
	
at_header
	:	AT_LEAST | AT_MOST | EXACTLY
	;
	
attribute_reference 
	:	entity_id DOT attribute_id  -> ^(ATTR_ID attribute_id entity_id)
	|	population_id DOT attribute_id  -> ^(POP_ATTR_ID attribute_id population_id)
	|	attribute_id
	;

condition_reference 
	:	entity_id condition_id  -> ^(COND_ID condition_id entity_id)
	|	population_id condition_id  -> ^(POP_COND_ID condition_id population_id)
	|	condition_id  -> ^(COND_ID condition_id)
	;

column_reference
	: 	(record_id DOT)? column_id -> ^(COL_ID column_id record_id?)
	;
	
unary_condition
	:	IS_EMPTY | IS_NOT_EMPTY  | ISNULL | IS_NOT_NULL;


string_comparator
	:	CONTAINS | ENDS_WITH | STARTS_WITH | MATCHES
	;
	

truth_value	:	TRUE | FALSE
	;
	
// Equations
equation 
	:	 equation_formula;
	
equation_formula
	:	(multdiv_equation_formula -> multdiv_equation_formula) (add_sub_operator a=multdiv_equation_formula -> ^(add_sub_operator $equation_formula $a))* 
	;
	
multdiv_equation_formula
	:	(power_equation_formula -> power_equation_formula)(mult_div_operator o=power_equation_formula -> ^(mult_div_operator $multdiv_equation_formula $o))* 
	;

power_equation_formula
	:	(string_equation_formula -> string_equation_formula)(power_operator o=string_equation_formula -> ^(power_operator $power_equation_formula $o))* 
	;

string_equation_formula
	:	(equation_parenthetical -> equation_parenthetical)(string_operator o=equation_parenthetical -> ^(string_operator $string_equation_formula $o))* 
	;

equation_parenthetical
	:	expression_term
	|	LEFT_PARA equation_formula RIGHT_PARA -> equation_formula
	;


expression_term
	:	number_term
	|	string_constant
	|	builtin
	|	truth_value
	|	equation_id 
	|	(attribute_reference) => attribute_reference
	|	(condition_reference) => condition_reference 
	|	function_call
	|	lookup_function_call
	| 	INDEPENDENT_VALUE
	|	column_reference
	//|	condition_statement
	;

// FUNCTION CALLS
function_call_list	:	function_call+  -> ^(FUNC_ROOT function_call+);

function_call
	:	function_id  bound_function_argument_list -> ^(function_id bound_function_argument_list)
	;


lookup_function_call
	:	lkp_function_id lookup_function_name  bound_function_argument_list -> 
		^(lkp_function_id lookup_function_name  bound_function_argument_list)
	;


bound_function_argument_list 
	:	LEFT_PARA (condition_statement? (COMMA condition_statement?)*) RIGHT_PARA -> ^(ARGUMENT_LIST condition_statement+)
	;

function_id
	: 	infa_function_id | puml_function_id | iif_function_id
	//| boolean_function_id 
	;
	
infa_function_id
	: ADD_TO_DATE | CONCAT | COUNT | INITCAP | INSTR | LOG | POWER | SQRT | ABORT 
	| LOWER | UPPER | REPLACECHR | REPLACESTR | DATE_COMPARE | DATE_DIFF | DECODE | ERROR | GET_DATE_PART 
	| LAST_DAY | LENGTH | LPAD | REVERSE
	| LTRIM | MAX | MIN | ROUND | RPAD | RTRIM | SET_DATE_PART | SUBSTR | SUM | TO_CHAR | TO_DATE 
	| TO_DECIMAL | TO_FLOAT | TO_INTEGER | TRUNC 
	| GREATEST | LEAST | METAPHONE | REG_EXTRACT | REG_MATCH | REG_REPLACE | SETCOUNTVARIABLE 
	| SETMAXVARIABLE | SETMINVARIABLE | SETVARIABLE | SOUNDEX | TO_BIGINT
	| 	IS_DATE | IS_NUMBER | IS_SPACES | IN | ISNULL 
	;

puml_function_id
	: DOWNFILL | BACKFILL  
	;

builtin
	: infa_builtin | puml_builtin;

infa_builtin
	: SYSDATE | DD_INSERT | DD_UPDATE | DD_REJECT | DD_DELETE | NULL
	;

puml_builtin
	: DATA_SPACE | MEMBER
	;

iif_function_id
	: 	IIF
	;

not_function_id
	:	NOT
	;

lkp_function_id
	:	LKP
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


boolean_comparator	:	equals | not_equals | less_than | less_or_equal | greater_than | greater_or_equal;

// boolean operators
equals		:	EQU;
not_equals	:	NOT_EQU;
less_than	:	LT;
less_or_equal	:	LE;
greater_than	:	GT;
greater_or_equal	:	GE;	
	
	
// SHARED

number_term
	:	negative_number_term -> ^(NEGATIVE negative_number_term)
	|	positive_number_term -> positive_number_term
	|	number_base
	;

positive_number_term
	:	PLUS number_base -> number_base
	;

negative_number_term
	:	DASH number_base
	;

number_base
	: INT | LONG | FLOAT | DOUBLE
	;

//generic_reference
//	:	COMPLETE_ID -> ^(PORT_ID COMPLETE_ID)
//	;

string_constant 
	:	QUOTED_CONSTANT
	;

// IDENTIFIERS

// [ENTITY_NAME]
entity_id
	:	BRCKT_ID
	;

attribute_id 
	:	BRCKT_ID
	;

// .<CONDITION_NAME>
condition_id  
	:	DOT LT COMPLETE_ID GT -> COMPLETE_ID
	;

// #EQUATION NAME#
equation_id  
	:	CONCEPTUAL_CONSTANT
	;

// [POPULATION_NAME]
population_id
	:	BRACE_ID
	;

record_id
	:	COMPLETE_ID
	;

column_id
	:	COMPLETE_ID
	;

lookup_function_name
	:	COMPLETE_ID
	;


rule_id :	COMPLETE_ID;

clause_id
	:	COMPLETE_ID;
	

/* LEXER */
	
// Special Tokens Imaginary tokens

ARGUMENT_LIST	: 	'argument_list';
COND		:	'cond';
NEGATIVE	:	'negative';

RANGE		: 'range' ; // Range between two values
FUNC_ROOT	: 'function root'; // Top a function call or reference, should have a list of children, possibly empty
ATTR_ID		: 'attr_id';
POP_ATTR_ID	: 'pop_attr_id';
COL_ID		: 'column_id';
POP_BODY	: 'pop_body';
POP_COND_ID	: 'pop_cond_id';
COND_ID		: 'cond_id';
ENTITY_ALIAS	: 'entity_alias';
RULE_FAMILY	: 'rule_family';
RULE_QUANT	: 'rule_quant';
DEPENDENT_RULES 
	:	  'dependent rule list';


// INFA CONSTANTS/BUILTIN VALUES

SYSDATE 	: ('S'|'s')('Y'|'y')('S'|'s')('D'|'d')('A'|'a')('T'|'t')('E'|'e');
DD_INSERT	: ('D'|'d')('D'|'d')'_'('I'|'i')('N'|'n')('S'|'s')('E'|'e')('R'|'r')('T'|'t');
DD_UPDATE	: ('D'|'d')('D'|'d')'_'('U'|'u')('P'|'p')('D'|'d')('A'|'a')('T'|'t')('E'|'e');
DD_REJECT	: ('D'|'d')('D'|'d')'_'('R'|'r')('E'|'e')('J'|'j')('E'|'e')('C'|'c')('T'|'t');
DD_DELETE	: ('D'|'d')('D'|'d')'_'('D'|'d')('E'|'e')('L'|'l')('E'|'e')('T'|'t')('E'|'e');
NULL		: ('N'|'n')('U'|'u')('L'|'l')('L'|'l');

// PUML Keywords

DOWNFILL	: ('D'|'d')('O'|'o')('W'|'w')('N'|'n')('F'|'f')('I'|'i')('L'|'l')('L'|'l');
BACKFILL	: ('B'|'b')('A'|'a')('C'|'c')('K'|'k')('F'|'f')('I'|'i')('L'|'l')('L'|'l');
DATA_SPACE	: ('D'|'d')('A'|'a')('T'|'t')('A'|'a')'_'('S'|'s')('P'|'p')('A'|'a')('C'|'c')('E'|'e');
MEMBER		: ('M'|'m')('E'|'e')('M'|'m')('B'|'b')('E'|'e')('R'|'r');
ABSOLUTE_VALUE	: ('A'|'a')('B'|'b')('S'|'s')('O'|'o')('L'|'l')('U'|'u')('T'|'t')('E'|'e');
AFTER		: ('A'|'a')('F'|'f')('T'|'t')('E'|'e')('R'|'r');
ANALYSIS_END	: ('E'|'e')('N'|'n')('D'|'d')' '+('A'|'a')('N'|'n')('A'|'a')('L'|'l')('Y'|'y')('S'|'s')('I'|'i')('S'|'s');
ANALYSIS_HEAD	: ('A'|'a')('N'|'n')('A'|'a')('L'|'l')('Y'|'y')('S'|'s')('I'|'i')('S'|'s');
AND		: ('A'|'a')('N'|'n')('D'|'d');
ANY		: ('A'|'a')('N'|'n')('Y'|'y');
AT_LEAST	: ('A'|'a')('T'|'t')' '+('L'|'l')('E'|'e')('A'|'a')('S'|'s')('T'|'t');
AT_MOST		: ('A'|'a')('T'|'t')' '+('M'|'m')('O'|'o')('S'|'s')('T'|'t');
BEFORE		: ('B'|'b')('E'|'e')('F'|'f')('O'|'o')('R'|'r')('E'|'e');
BETWEEN		: ('B'|'b')('E'|'e')('T'|'t')('W'|'w')('E'|'e')('E'|'e')('N'|'n');
COMPARABLE_TO	: ('C'|'c')('O'|'o')('M'|'m')('P'|'p')('A'|'a')('R'|'r')('A'|'a')('B'|'b')('L'|'l')('E'|'e')' '+('T'|'t')('O'|'o');
CONDITION	: ('C'|'c')('O'|'o')('N'|'n')('D'|'d')('I'|'i')('T'|'t')('I'|'i')('O'|'o')('N'|'n');
CONTAINS	: ('C'|'c')('O'|'o')('N'|'n')('T'|'t')('A'|'a')('I'|'i')('N'|'n')('S'|'s');
ENDS_WITH	: ('E'|'e')('N'|'n')('D'|'d')('S'|'s')' '+('W'|'w')('I'|'i')('T'|'t')('H'|'h');
EVENT		: ('E'|'e')('V'|'v')('E'|'e')('N'|'n')('T'|'t');
EXACTLY		: ('E'|'e')('X'|'x')('A'|'a')('C'|'c')('T'|'t')('L'|'l')('Y'|'y');
FALSE		: ('F'|'f')('A'|'a')('L'|'l')('S'|'s')('E'|'e');
FOR		: ('F'|'f')('O'|'o')('R'|'r');
GIVEN_BY	: ('G'|'g')('I'|'i')('V'|'v')('E'|'e')('N'|'n')' '+('B'|'b')('Y'|'y');
INTERSECTION	: ('I'|'i')('N'|'n')('T'|'t')('E'|'e')('R'|'r')('S'|'s')('E'|'e')('C'|'c')('T'|'t')('I'|'i')('O'|'o')('N'|'n');
IS_A		: ('I'|'i')('S'|'s')' '+('A'|'a');
IS_EMPTY	: ('I'|'i')('S'|'s')' '+('E'|'e')('M'|'m')('P'|'p')('T'|'t')('Y'|'y');
IS_FALSE	: ('I'|'i')('S'|'s')' '+('F'|'f')('A'|'a')('L'|'l')('S'|'s')('E'|'e');
IS_NOT_EMPTY	: ('I'|'i')('S'|'s')' '+('N'|'n')('O'|'o')('T'|'t')' '+('E'|'e')('M'|'m')('P'|'p')('T'|'t')('Y'|'y');
IS_NOT_NULL	: ('I'|'i')('S'|'s')' '+('N'|'n')('O'|'o')('T'|'t')' '+('N'|'n')('U'|'u')('L'|'l')('L'|'l');
IS_TRUE		: ('I'|'i')('S'|'s')' '+('T'|'t')('R'|'r')('U'|'u')('E'|'e');
JOIN		: ('J'|'j')('O'|'o')('I'|'i')('N'|'n');
MATCHES		: ('M'|'m')('A'|'a')('T'|'t')('C'|'c')('H'|'h')('E'|'e')('S'|'s');
NOSUCH		: ('N'|'n')('O'|'o')' '+('S'|'s')('U'|'u')('C'|'c')('H'|'h');
NOT		: ('N'|'n')('O'|'o')('T'|'t');
OBJECT		: ('O'|'o')('B'|'b')('J'|'j')('E'|'e')('C'|'c')('T'|'t');
OBSERVATION	: ('O'|'o')('B'|'b')('S'|'s')('E'|'e')('R'|'r')('V'|'v')('A'|'a')('T'|'t')('I'|'i')('O'|'o')('N'|'n');
OF		: ('O'|'o')('F'|'f');
OR		: ('O'|'o')('R'|'r');
POPULATION_CONS	: '@'('C'|'c')('O'|'o')('N'|'n')('S'|'s')('T'|'t')('R'|'r')('A'|'a')('I'|'i')('N'|'n')('T'|'t');
POPULATION_DEF	: '@'('P'|'p')('O'|'o')('P'|'p')'_'('D'|'d')('E'|'e')('F'|'f');
POPULATION_OPT	: '@'('P'|'p')('O'|'o')('P'|'p')('U'|'u')('L'|'l')('A'|'a')('T'|'t')('I'|'i')('O'|'o')('N'|'n');
RULE		: ('R'|'r')('U'|'u')('L'|'l')('E'|'e');
SAMPLES		: ('S'|'s')('A'|'a')('M'|'m')('P'|'p')('L'|'l')('E'|'e')('S'|'s');
SPAN		: ('S'|'s')('P'|'p')('A'|'a')('N'|'n');
STARTS_WITH	: ('S'|'s')('T'|'t')('A'|'a')('R'|'r')('T'|'t')('S'|'s')' '+('W'|'w')('I'|'i')('T'|'t')('H'|'h');
STORAGE_OPT	: '@'('S'|'s')('T'|'t')('O'|'o')('R'|'r')('A'|'a')('G'|'g')('E'|'e');
SUBPOPULATION_OPT : '@'('S'|'s')('U'|'u')('B'|'b')('P'|'p')('O'|'o')('P'|'p')('U'|'u')('L'|'l')('A'|'a')('T'|'t')('I'|'i')('O'|'o')('N'|'n')'_'('O'|'o')('F'|'f');
TRANSACTION	: ('T'|'t')('R'|'r')('A'|'a')('N'|'n')('S'|'s')('A'|'a')('C'|'c')('T'|'t')('I'|'i')('O'|'o')('N'|'n');
TRUE		: ('T'|'t')('R'|'r')('U'|'u')('E'|'e');
UNION		: ('U'|'u')('N'|'n')('I'|'i')('O'|'o')('N'|'n');
USING		: ('U'|'u')('S'|'s')('I'|'i')('N'|'n')('G'|'g');
WITH		: ('W'|'w')('I'|'i')('T'|'t')('H'|'h');


// INFA Functions
	
ABORT 		: ('A'|'a')('B'|'b')('O'|'o')('R'|'r')('T'|'t');
ADD_TO_DATE 	: ('A'|'a')('D'|'d')('D'|'d')'_'('T'|'t')('O'|'o')'_'('D'|'d')('A'|'a')('T'|'t')('E'|'e');
CONCAT 		: ('C'|'c')('O'|'o')('N'|'n')('C'|'c')('A'|'a')('T'|'t');
COUNT 		: ('C'|'c')('O'|'o')('U'|'u')('N'|'n')('T'|'t');
DATE_COMPARE 	: ('D'|'d')('A'|'a')('T'|'t')('E'|'e')'_'('C'|'c')('O'|'o')('M'|'m')('P'|'p')('A'|'a')('R'|'r')('E'|'e');
DATE_DIFF 	: ('D'|'d')('A'|'a')('T'|'t')('E'|'e')'_'('D'|'d')('I'|'i')('F'|'f')('F'|'f');
DECODE 		: ('D'|'d')('E'|'e')('C'|'c')('O'|'o')('D'|'d')('E'|'e');
ERROR 		: ('E'|'e')('R'|'r')('R'|'r')('O'|'o')('R'|'r');
GET_DATE_PART 	: ('G'|'g')('E'|'e')('T'|'t')'_'('D'|'d')('A'|'a')('T'|'t')('E'|'e')'_'('P'|'p')('A'|'a')('R'|'r')('T'|'t');
IIF 		: ('I'|'i')('I'|'i')('F'|'f');
IN 		: ('I'|'i')('N'|'n');
INITCAP 	: ('I'|'i')('N'|'n')('I'|'i')('T'|'t')('C'|'c')('A'|'a')('P'|'p');
INSTR 		: ('I'|'i')('N'|'n')('S'|'s')('T'|'t')('R'|'r');
ISNULL 		: ('I'|'i')('S'|'s')('N'|'n')('U'|'u')('L'|'l')('L'|'l');
IS_DATE 	: ('I'|'i')('S'|'s')'_'('D'|'d')('A'|'a')('T'|'t')('E'|'e');
IS_NUMBER 	: ('I'|'i')('S'|'s')'_'('N'|'n')('U'|'u')('M'|'m')('B'|'b')('E'|'e')('R'|'r');
IS_SPACES 	: ('I'|'i')('S'|'s')'_'('S'|'s')('P'|'p')('A'|'a')('C'|'c')('E'|'e')('S'|'s');
LAST_DAY 	: ('L'|'l')('A'|'a')('S'|'s')('T'|'t')'_'('D'|'d')('A'|'a')('Y'|'y');
LENGTH 		: ('L'|'l')('E'|'e')('N'|'n')('G'|'g')('T'|'t')('H'|'h');
LKP		: ':'('L'|'l')('K'|'k')('P'|'p')'.';
LOG 		: ('L'|'l')('O'|'o')('G'|'g');
LOOKUP 		: ('L'|'l')('O'|'o')('O'|'o')('K'|'k')('U'|'u')('P'|'p');
LOWER 		: ('L'|'l')('O'|'o')('W'|'w')('E'|'e')('R'|'r');
LPAD 		: ('L'|'l')('P'|'p')('A'|'a')('D'|'d');
LTRIM 		: ('L'|'l')('T'|'t')('R'|'r')('I'|'i')('M'|'m');
MAX 		: ('M'|'m')('A'|'a')('X'|'x');
MIN 		: ('M'|'m')('I'|'i')('N'|'n');
POWER 		: ('P'|'p')('O'|'o')('W'|'w')('E'|'e')('R'|'r');
REPLACECHR 	: ('R'|'r')('E'|'e')('P'|'p')('L'|'l')('A'|'a')('C'|'c')('E'|'e')('C'|'c')('H'|'h')('R'|'r');
REPLACESTR 	: ('R'|'r')('E'|'e')('P'|'p')('L'|'l')('A'|'a')('C'|'c')('E'|'e')('S'|'s')('T'|'t')('R'|'r');
REVERSE 	: ('R'|'r')('E'|'e')('V'|'v')('E'|'e')('R'|'r')('S'|'s')('E'|'e');
ROUND 		: ('R'|'r')('O'|'o')('U'|'u')('N'|'n')('D'|'d');
RPAD 		: ('R'|'r')('P'|'p')('A'|'a')('D'|'d');
RTRIM 		: ('R'|'r')('T'|'t')('R'|'r')('I'|'i')('M'|'m');
SET_DATE_PART 	: ('S'|'s')('E'|'e')('T'|'t')'_'('D'|'d')('A'|'a')('T'|'t')('E'|'e')'_'('P'|'p')('A'|'a')('R'|'r')('T'|'t');
SQRT 		: ('S'|'s')('Q'|'q')('R'|'r')('T'|'t');
SUBSTR 		: ('S'|'s')('U'|'u')('B'|'b')('S'|'s')('T'|'t')('R'|'r');
SUM 		: ('S'|'s')('U'|'u')('M'|'m');
TO_CHAR 	: ('T'|'t')('O'|'o')'_'('C'|'c')('H'|'h')('A'|'a')('R'|'r');
TO_DATE 	: ('T'|'t')('O'|'o')'_'('D'|'d')('A'|'a')('T'|'t')('E'|'e');
TO_DECIMAL 	: ('T'|'t')('O'|'o')'_'('D'|'d')('E'|'e')('C'|'c')('I'|'i')('M'|'m')('A'|'a')('L'|'l');
TO_FLOAT 	: ('T'|'t')('O'|'o')'_'('F'|'f')('L'|'l')('O'|'o')('A'|'a')('T'|'t');
TO_INTEGER 	: ('T'|'t')('O'|'o')'_'('I'|'i')('N'|'n')('T'|'t')('E'|'e')('G'|'g')('E'|'e')('R'|'r');
TRUNC 		: ('T'|'t')('R'|'r')('U'|'u')('N'|'n')('C'|'c');
UPPER 		: ('U'|'u')('P'|'p')('P'|'p')('E'|'e')('R'|'r');

GREATEST	: ('G'|'g')('R'|'r')('E'|'e')('A'|'a')('T'|'t')('E'|'e')('S'|'s')('T'|'t');
LEAST		: ('L'|'l')('E'|'e')('A'|'a')('S'|'s')('T'|'t');
METAPHONE	: ('M'|'m')('E'|'e')('T'|'t')('A'|'a')('P'|'p')('H'|'h')('O'|'o')('N'|'n')('E'|'e');
REG_EXTRACT	: ('R'|'r')('E'|'e')('G'|'g')'_'('E'|'e')('X'|'x')('T'|'t')('R'|'r')('A'|'a')('C'|'c')('T'|'t');
REG_MATCH	: ('R'|'r')('E'|'e')('G'|'g')'_'('M'|'m')('A'|'a')('T'|'t')('C'|'c')('H'|'h');
REG_REPLACE	: ('R'|'r')('E'|'e')('G'|'g')'_'('R'|'r')('E'|'e')('P'|'p')('L'|'l')('A'|'a')('C'|'c')('E'|'e');
SETCOUNTVARIABLE	: ('S'|'s')('E'|'e')('T'|'t')('C'|'c')('O'|'o')('U'|'u')('N'|'n')('T'|'t')('V'|'v')('A'|'a')('R'|'r')('I'|'i')('A'|'a')('B'|'b')('L'|'l')('E'|'e');
SETMAXVARIABLE	: ('S'|'s')('E'|'e')('T'|'t')('M'|'m')('A'|'a')('X'|'x')('V'|'v')('A'|'a')('R'|'r')('I'|'i')('A'|'a')('B'|'b')('L'|'l')('E'|'e');
SETMINVARIABLE	: ('S'|'s')('E'|'e')('T'|'t')('M'|'m')('I'|'i')('N'|'n')('V'|'v')('A'|'a')('R'|'r')('I'|'i')('A'|'a')('B'|'b')('L'|'l')('E'|'e');
SETVARIABLE	: ('S'|'s')('E'|'e')('T'|'t')('V'|'v')('A'|'a')('R'|'r')('I'|'i')('A'|'a')('B'|'b')('L'|'l')('E'|'e');
SOUNDEX		: ('S'|'s')('O'|'o')('U'|'u')('N'|'n')('D'|'d')('E'|'e')('X'|'x');
TO_BIGINT	: ('T'|'t')('O'|'o')'_'('B'|'b')('I'|'i')('G'|'g')('I'|'i')('N'|'n')('T'|'t');



// Primary tokens



BARBAR		:	'||';
BINDING 	: 	'<-';
GE		:	'>=';
LE		:	'<=';
NOT_EQU		:	'!=' | '<>';

LEFT_PARA	:	'(';
DOT		:	'.';
COMMA		:	',';
RIGHT_PARA	:	')';
UNDERSCORE	:	'_';
BAR		:	'|';	
COLON		:	':';
DASH		: 	'-';
AT_SIGN		:	'@';
EQU		:	'=';
GT		:	'>';
HASH		:	'#';
HAT		:	'^';
LT		:	'<';
MOD		:	'%';
EXCLAIM		:	'!';
PLUS		:	'+';
SINGLE_QUOTE	:	'\'';
DBL_QUOTE	:	'"';
SLASH		:	'/';
STAR		:	'*';
LEFT_BRACE	:	'{';
LEFT_BRKT	:	'[';
RIGHT_BRACE	:	'}';
RIGHT_BRKT	:	']';


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

BRACE_ID	
	:	'{'('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|'0'..'9'|'_'|' ')*'}'
	;

//POINT_ID	
	//:	'<'('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|'0'..'9'|'_'|' ')*'>'
	//;


CONCEPTUAL_CONSTANT
	:	'#'('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|'0'..'9'|'_'|' ')*'#'
	;
	
INDEPENDENT_VALUE
	:	'##'('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|'0'..'9'|'_'|' ')*'#'
	;

COMPLETE_ID	
	:	('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|'0'..'9'|'_')*
	;

//FLEX_ID	
	//:	('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|'0'..'9'|'_'|' ')*
	//;

QUOTED_CONSTANT
	: SINGLE_QUOTE ( options {greedy=false;} : . )* SINGLE_QUOTE 
	;

// skip these

COMMENT
    :   '--' ~('\n'|'\r')* ((('\r')*'\n')+ | EOF ) {$channel=HIDDEN;}
    ;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
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

