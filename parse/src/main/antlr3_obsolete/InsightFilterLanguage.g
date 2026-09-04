grammar InsightFilterLanguage;
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
	:	CAP_ID
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

// {POPULATION_NAME}
population_id 
	:	LEFT_BRACE CAP_ID RIGHT_BRACE
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
	|	LEFT_PARA attribute_reference 
		(COMMA attribute_reference )* RIGHT_PARA 
	;
	
set_operation 
	// returns [AbstractFunction setop]
	:	UNION 
	// { $setop = new UnionList(0); }
	//| INTERSECTION | MINUS | JOIN
	;
	
storage_option	
	// returns [String storageStyle]
	:	STORAGE_OPT storage_type
	;

storage_type : OBJECT | TRANSACTION | OBSERVATION | EVENT | SPAN;


// FUNCTION CALLS
function_call_list	:	function_call+  -> ^(FUNC_ROOT function_call+);

function_call
	:	function_id  bound_function_argument_list -> ^(function_id bound_function_argument_list)
	;

// F_function or fBUILTIN
function_id
	:	FUNCTION_ID
	|	BUILT_IN_FUNCTION
	;


bound_function_argument_list 
	:	LEFT_PARA (bound_function_argument? (COMMA bound_function_argument?)*) RIGHT_PARA -> ^(ARGUMENT_LIST bound_function_argument+)
	;

bound_function_argument 
	:  attribute_reference
	//| population_id
	| truth_value
	| generic_reference
	//| id_function_id
	| expression_term
	| function_call DOT generic_reference
	| BUILT_IN_FUNCTION_ARGUMENT
	;

// Universal Quantification
universal_quantification
	:	 universal_quant_prolog+ LEFT_PARA condition_statement RIGHT_PARA
	;

universal_quant_prolog
	:	(FOR entity_id generic_reference?) -> ^(FOR entity_id  generic_reference?)
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
	:	(SAMPLES clause_id? OF entity_reference LEFT_PARA condition_statement RIGHT_PARA)  -> ^(SAMPLES entity_reference condition_statement clause_id?)
	;

rule_id :	generic_reference;

clause_id
	:	generic_reference;

// CONDITIONS
condition 
	:	 condition_statement;
	
// Condition Binding
condition_binding
	:	condition_id GIVEN_BY LEFT_PARA condition_statement RIGHT_PARA;


condition_statement
	:	(and_condition_statement -> and_condition_statement) (OR a=and_condition_statement -> ^(OR $condition_statement $a))* 
	|	condition_list
	;
	
and_condition_statement
	:	(condition_parenthetical -> condition_parenthetical)(AND o=condition_parenthetical -> ^(AND $and_condition_statement $o))* 
	;

condition_parenthetical
	:	NOT LEFT_PARA condition_statement  RIGHT_PARA ->  ^(NOT condition_statement)
	|	LEFT_PARA condition_statement RIGHT_PARA -> condition_statement
	;
	
//condition_list 
//	:	condition_expression (conjunction condition_expression)* -> ^(COND_ROOT condition_expression ^(conjunction condition_expression)*)
//	;
//(factor -> factor) ('*' f=factor -> ^('*' $expr $f))*

condition_list
	:	(and_condition_expression -> and_condition_expression) (OR o=and_condition_expression -> ^(OR $condition_list $o))*
	;

and_condition_expression
	:	(condition_expression -> condition_expression) (AND a=condition_expression -> ^(AND $and_condition_expression $a))*
	;

condition_expression
	:	condition_id  -> ^(IS_TRUE condition_id)
	|	condition_id (IS_FALSE | IS_TRUE)^
	|	truth_value
	|	condition_reference comparator condition_reference -> ^(comparator condition_reference ^(ATTR_ID condition_reference))
	|	condition_reference comparator^ expression_term
	|	condition_reference string_comparator^ STRING_VALUE 
	|	condition_reference BETWEEN^ range
	|	condition_reference unary_condition^
	|	attribute_member_phrase
	|	at_least_clause
	;

condition_reference 
	:	 attribute_reference | generic_reference;
	
	
at_least_clause
	: 	at_header INT (entity_reference) generic_reference? WITH LEFT_PARA condition_statement RIGHT_PARA 
	-> ^(at_header INT entity_reference  ^(WITH condition_statement) generic_reference?)
	| 	ANY (entity_reference) generic_reference? WITH LEFT_PARA condition_statement RIGHT_PARA 
	-> ^(AT_LEAST {new CommonTree(new CommonToken(INT, "1"))} entity_reference  ^(WITH condition_statement) generic_reference?)
	| 	NOSUCH (entity_reference) generic_reference? WITH LEFT_PARA condition_statement RIGHT_PARA 
	-> ^(AT_MOST {new CommonTree(new CommonToken(INT, "0"))} entity_reference  ^(WITH condition_statement) generic_reference?)
	;
	
at_header
	:	AT_LEAST | AT_MOST | EXACTLY
	;
	
attribute_reference 
	:	(entity_reference)? attribute_id  -> ^(ATTR_ID attribute_id entity_reference?)
	|	(entity_reference)? condition_id  -> ^(COND_ID condition_id entity_reference?)
	|	generic_reference attribute_id	  -> ^(ATTR_ID attribute_id generic_reference)
	|	generic_reference condition_id  -> ^(COND_ID condition_id generic_reference)
	;
	
generic_reference
	:	n=CAP_ID -> {new CommonTree(new CommonToken(CAP_ID, $n.text.trim()))}
	;

entity_reference
	:	entity_id;


range	: expression_term COLON expression_term -> ^(RANGE expression_term expression_term)
	;

attribute_member_phrase
	:	condition_reference IN^ INDEPENDENT_VALUE
	|	condition_reference IN LEFT_PARA expression_term (COMMA expression_term)* RIGHT_PARA -> ^(IN condition_reference expression_term+)
	|	condition_reference NOT_IN^ INDEPENDENT_VALUE
	|	condition_reference NOT_IN LEFT_PARA expression_term (COMMA expression_term)* RIGHT_PARA -> ^(NOT_IN condition_reference expression_term+)
	
	;
	
unary_condition
	:	IS_EMPTY | IS_NOT_EMPTY  | IS_NULL | IS_NOT_NULL;
	
comparator
	:	EQUALS | LESS_THAN | LESS_OR_EQUAL | GREATER_THAN | GREATER_OR_EQUAL
	|	COMPARABLE_TO | IS_A | NOT_EQUALS
	;
	
string_comparator
	:	CONTAINS | ENDS_WITH | STARTS_WITH | MATCHES
	|	NOT_CONTAINS | NOT_ENDS_WITH | NOT_STARTS_WITH | NOT_MATCHES 
	;
	
truth_value	:	TRUE | FALSE
	;
	
	// EquationS

equation: equation_formula ;

equation_formula	
	:	equation_sum ;

equation_sum
	:	(equation_product -> equation_product) (add_sub_operator a=equation_product -> ^(add_sub_operator $equation_sum $a))* ;

equation_product
	:	(equation_unary -> equation_unary) (mult_div_operator a=equation_unary -> ^(mult_div_operator $equation_product $a))* ;

equation_unary 
	:	(equation_expression | unary_operator^? LEFT_PARA! equation_formula RIGHT_PARA!) ;
	
// equation Binding
equation_binding
	:	equation_id GIVEN_BY LEFT_PARA equation_formula RIGHT_PARA;


equation_expression
	:	equation_id 
	|	attribute_reference 
	|	generic_reference
	|	number_term
	;


unary_operator
	:	SQUARED | SQUARE_ROOT | NEGATED | ABSOLUTE_VALUE;
	
mult_div_operator
	:	MULTIPLY | DIVIDE | MODULO
	;

add_sub_operator
	:	PLUS | minus
	;

minus	:	MINUS | DASH;

	
// SHARED
expression_term
	:	number_term
	|	STRING_VALUE
	;
	
number_term
	:	INT | LONG
	|	FLOAT | DOUBLE
	|	CONCEPTUAL_CONSTANT
	|	INDEPENDENT_VALUE
	;
	

attribute_id 
	:	DOT_LEFT_BRKT! CAP_ID RIGHT_BRKT! 
	|	ID
	;

// .<CONDITION_NAME>
condition_id  
	:	DOT_LEFT_POINT! CAP_ID RIGHT_POINT! 
	;

// #EQUAITION NAME#>
equation_id  
	:	HASH CAP_ID 
	;

// [ENTITY_NAME]
entity_id
	:	LEFT_BRKT! CAP_ID RIGHT_BRKT!
	;
	
	
conjunction
	:   AND //{ $conjunctionFunc = DeltaTerm.AND_LIST_FUNC; }
	|   OR  //{ $conjunctionFunc = DeltaTerm.OR_LIST_FUNC; }
	;
	


/* LEXER */
	
// Special Tokens Imaginary tokens

RANGE	: ; // Range between two values
FUNC_ROOT	: 'function root'; // Top a function call or reference, should have a list of children, possibly empty
ATTR_ID		: 'attr_id';
COND_ID		: 'cond_id';
ARGUMENT_LIST	: 'argument_list';
ENTITY_ALIAS	: 'entity_alias';
RULE_FAMILY	: 'rule_family';
RULE_QUANT	: 'rule_quant';
DEPENDENT_RULES 
	:	  'dependent rule list';



// Primary tokanes
ABSOLUTE_VALUE	:	'absolute';
AFTER		:	'after';
ANALYSIS_HEAD	:	'ANALYSIS:';
ANALYSIS_END	:	'END ANALYSIS';
AND		:	'and';
ANY		:	'any';
AT_LEAST	:	'at least' | 'At Least';
AT_MOST		:	'at most' | 'At most';
AT_SIGN		:	'@';
BEFORE		:	'before';
BETWEEN		: 	'between';
BINDING 	: 	LEFT_BRKT DASH;
COLON		:	':';
COMMA		:	',';
COMPARABLE_TO	:	'comparable to';
CONDITION	:	'condition';
CONTAINS	:	'contains';
NOT_CONTAINS	:	NOT ' contains';
DASH		: 	'-';
DIVIDE		:	'divide' | '/';
DOT		:	'.';
DOT_LEFT_BRKT	:	DOT LEFT_BRKT;
DOT_LEFT_POINT	:	DOT LEFT_POINT;
ENDS_WITH	:	'ends with';
NOT_ENDS_WITH	:	NOT ' ends with';
EQU		:	'=';
EQUALS		:	'equals';
EVENT		:	'event' | 'EVENT';
EXACTLY		:	'exactly';
FALSE		:	'false';
FOR		:	'for';
GIVEN_BY	:	'given by';
GREATER_THAN	:	'greater than';
GREATER_OR_EQUAL	:	'greater or equal';
HASH		:	'#';
ID		:	DOT LEFT_BRKT 'ID' RIGHT_BRKT;
IN		:	'in';
NOT_IN		:	NOT ' in';
IS_EMPTY	:	'is empty';
IS_NOT_EMPTY	:	'is ' NOT ' empty';
IS_FALSE	:	'is false';
IS_TRUE		:	'is true';
IS_A		:	'is a';
IS_NULL		:	'is null';
IS_NOT_NULL	:	'is ' NOT ' null';
INTERSECTION	:	'intersection';
JOIN		:	'join';
LEFT_BRACE	:	'{';
LEFT_BRKT	:	'[';
LEFT_POINT	:	'<';
LEFT_PARA	:	'(';
LESS_THAN	:	'less than';
LESS_OR_EQUAL	:	'less or equal';
MATCHES		:	'matches';
NOT_MATCHES	:	NOT ' matches';
MINUS		:	'minus';
MODULO		:	'modulo' | 'mod';
MULTIPLY	:	'multiply' | '*';
NEGATED		:	'negate' | 'negated';
NOSUCH		:	'no such';
NOT		:	'not';
NOT_EQU		:	'!=';
NOT_EQUALS	:	NOT ' equals';
OBJECT		:	'object' | 'OBJECT';
OBSERVATION	:	'observation' | 'OBSERVATION';
OF		:	'of';
OR		:	'or';
PLUS		:	'plus' | '+';
POPULATION_CONS	:	'@CONSTRAINT';
POPULATION_DEF	:	'@POP_DEF';
POPULATION_OPT	:	'@POPULATION';
RIGHT_BRACE	:	'}';
RIGHT_BRKT	:	']';
RIGHT_PARA	:	')';
RIGHT_POINT	:	'>';
RULE		:	'Rule' | 'rule';
SAMPLES		:	'samples';
SPAN		:	'span' | 'SPAN';
SQUARED 	:	'squared' | 'square';
SQUARE_ROOT 	:	'square root';
STARTS_WITH	:	'starts with';
NOT_STARTS_WITH	:	NOT ' starts with';
STORAGE_OPT	:	'@STORAGE';
SUBPOPULATION_OPT :	'@SUBPOPULATION_OF';
TRANSACTION	:	'transaction' | 'TRANSACTION';
TRUE		:	'true';
UNION		:	'union';
USING		:	'using';
WITH		:	'with';
	
//OPTION

BUILT_IN_FUNCTION
	:	'fCONCAT' | 'fADD' | 'fSUBTRACT' | 'fDIVIDE' | 'fMULTIPLY' | LEFT_BRKT 'ID' RIGHT_BRKT
	|	'fMAX' | 'fMIN' | 'fSUM' | 'fFIRST' | 'fLAST' | 'fCOUNT' | 'fFIRSTNN' | 'fLASTNN' | 'fDROP' |'fLKUP'
	;

FUNCTION_ID  :	'f_'('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;


BUILT_IN_FUNCTION_ARGUMENT
	:	'_COMBINATION_' | '_ADDEND_' | '_SUM_' | '_PRODUCT_' | '_QUOTIENT_' | '_REMAINDER_' | '_DIFFERENCE_' | '_DIVISOR_' 
	| '_CONCATENATION_';

CONCEPTUAL_CONSTANT
	:	HASH CAP_ID+ HASH;
	
INDEPENDENT_VALUE
	:	HASH CONCEPTUAL_CONSTANT;
	

CAP_ID  :	('A'..'Z')('A'..'Z'|'0'..'9'|'_'|' ')*
    ;

/* ANTLR Generated */
//ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
//    ;

INT :	'0'..'9'+'I'?
    ;

LONG :	('0'..'9')+'L'
    ;

DOUBLE	:	FLOAT 'D';

FLOAT
    :   ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    |   '.' ('0'..'9')+ EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ;

COMMENT
    :   '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    |   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    ;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;

STRING_VALUE 
	:	'"'('A'..'Z'|'a'..'z'|'0'..'9'|'_'|' '|'|')*'"'
	;

//STRING_VALUE
//    :  '"' ( ESC_SEQ | ~('\\'|'"') )* '"'
 //   ;

CHAR:  '\'' ( ESC_SEQ | ~('\''|'\\') ) '\''
    ;

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


