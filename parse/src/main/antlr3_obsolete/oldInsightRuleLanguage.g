grammar InsightRuleLanguage;
options {
language= Java;
output=AST;
}

// Java acion, ehods, tmstadditions

@header {
package InsightExpressionParser;

}
@lexer::header {
package InsightExpressionParser;

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
		//	{ $analysis = new DeltaAnalysis($analysis_name.text); }
		(analysis_def         
		//	{ $analysis.add($def.entry);}
		)* 
		ANALYSIS_END 
	;
	
analysis_name 
	:	CAP_ID
	;

analysis_def	
	returns [DeltaTerm entry]
	scope { String myDefId; }
	:	entity 
	{
		//System.out.println("entity recognized");
		$entry = $entity.et;
	}
	|	population 
	{
		//System.out.println("population recognized");
		$entry = $population.pop;
	}
	;
	
population
	returns [PopulationTerm pop]
	:	population_id 
		population_options 
	//{	$def::myDefId = $population_options.entityType; }
		body 
	/*{
		// System.out.println("Pop Found: " + $population_id.text);
		$pop = new PopulationTerm($population_id.text, $population_options.entityType);
		$pop.fill("popDef", $population_options.popDef);
		$pop.fill("popConstraints", $population_options.popConstraints);
		
		SimpleVariable x = new SimpleVariable("storageStyle");
		x.resolve(new StringTerm($population_options.storageStyle));
		
		x = new SimpleVariable("subPopName");
		x.resolve(new StringTerm($population_options.subPopName));
		
		$pop.fill("storageStyle", x);
		$pop.fill("attList", $body.atl);
		// System.out.println($pop.stdFormat());
	} */
	;

population_options
	/*returns [String entityType,
		 String subPopName,
		 DeltaTerm popDef,
		 DeltaTerm popConstraints,
		 String storageStyle] */
	:  (POPULATION_OPT entity_id) 
	   population_of 
	   (POPULATION_CONS condition_list)?
	   (storage_option)?
	  /* {
	   	$entityType = $entity_id.text;
	   	$subPopName = $population_of.subPopName;
	   	$popDef = $population_of.popDef;
	   	//$storageStyle = $storage_option.storageStyle;
	   } */
	;

population_of 
	//returns [String subPopName, DeltaTerm popDef]
	:	(POPULATION_DEF pdl = pop_def_list)
	/*{
	//	System.out.println("Population Definition Entity : ");
		$popDef = (DeltaTerm) $pdl.popDef;
	} */
	
	|	(SUBPOPULATION_OPT population_id)
	/*{
	//	System.out.println("Sub Population Definition Parent : "+ $population_id.text);
		$subPopName = $population_id.text;
	} */
	;

pop_def_list 
//	returns [DeltaTerm popDef] 
	:	head = pop_def 
//			{	$popDef = buildConjunction(null, null, $head.item); }
		( conj = CONJUNCTION   item = pop_def
//			{	$popDef = buildConjunction($popDef, $conj.text, $item.item); }
		)*
	;

pop_def
	returns [DeltaTerm item]
	:	entity_id? condition_id
	/*{ 
	  String condition = $entity_id.text + "." + $condition_id.text;
	 // System.out.println("Entity Comdition: " + condition);
	  $item = (DeltaTerm) new ConditionVariable(condition);
	 } */
	|	a = population_id op = set_operation b = population_id
	/*{
	// System.out.println("Population Statement: " + $a.text + " " + $op.text + " " + $b.text);
	  //$op.setop.add(new PopulationVariable($a.text));
	  //$op.setop.add(new PopulationVariable($b.text));
	  //$item = (DeltaTerm) $op.setop;
	 }*/
	;


entity 	
//	returns [EntityTerm et]
	:	eid=entity_id  
//	{	$def::myDefId = $entity_id.text; }
		bd=body  
/*	{
	//	System.out.println("Entity Definition");
		$et = new EntityTerm($eid.text);
		$et.fill("attList", $bd.atl);
	//	String item = $et.stdFormat();
	//	System.out.println(item);
	} */
	;
	
body
  //  returns [AttributeList atl = new AttributeList(0);]
	:	LEFT_PARA RIGHT_PARA 
	|	LEFT_PARA att_ref = attribute_reference 
		//	{ $atl.add($att_ref.at); }
		(COMMA att_ref = attribute_reference 
		//	{ $atl.add($att_ref.at); }
		)* RIGHT_PARA 
//		{
		// 	String item = $atl.stdFormat();
		//	System.out.println("HERE BODY " + item);
//		}
	;
	
condition_list
	:	 LEFT_PARA condition_expression (CONJUNCTION condition_expression)*  RIGHT_PARA
	;
	
condition_expression
	:	condition_id 
	|	expression_term comparator expression_term 
	|	expression_term BETWEEN expression_term AND expression_term
	|	NOT  LEFT_PARA condition_expression RIGHT_PARA
	|	attribute_member_phrase
	;

attribute_member_phrase
	:	expression_term IN INDEPENDENT_VALUE
	|	expression_term IN LEFT_PARA CONCEPTUAL_CONSTANT (COMMA CONCEPTUAL_CONSTANT)* RIGHT_PARA
	;
comparator
	:	 COMPARATOR;
	
expression_term
	:	attribute_reference
	|	INT
	|	FLOAT
	|	function_argument_reference
	|	TRUTH_VALUE
	|	CONCEPTUAL_CONSTANT
	|	INDEPENDENT_VALUE
	;
	
function_definition
	: general_function_definition | id_function_definition | built_in_function_definition;
	
general_function_definition
	:	FUNCTION_ID LEFT_PARA function_def_argument_list? RIGHT_PARA COLON function_type? (COLON LEFT_PARA function_def_argument_list RIGHT_PARA)?
	;
	
built_in_function_definition
	:	BUILT_IN_FUNCTION LEFT_PARA function_def_argument_list? RIGHT_PARA COLON
	;
	
id_function_definition
	:	id_function_id LEFT_PARA function_def_argument_list RIGHT_PARA COLON
	;

function_type 
	:	CAP_ID | BUILT_IN_FUNCTION;
	
function_def_argument_list
	:	 (function_def_argument (COMMA! function_def_argument)*)
	;

function_def_argument
	:	CAP_ID | BUILT_IN_FUNCTION_ARGUMENT  | ID_FUNCTION_BUILT_IN_RESULT
	;

function_call
	:	(function_id | id_function_id)  bound_function_argument_list 
	;
	
bound_function_argument_list 
	:	LEFT_PARA (bound_function_argument? (COMMA! bound_function_argument?)*) RIGHT_PARA
	;

bound_function_argument 
	: INT 
	| FLOAT
	| attribute_reference
	//| population_id
	| TRUTH_VALUE
	| function_def_argument
	//| id_function_id
	| function_argument_reference
	| function_call DOT function_def_argument
	;


function_argument_reference
	:	function_id DOT function_def_argument
	|	id_function_id DOT function_def_argument
	;


set_operation 
	// returns [AbstractFunction setop]
	:	UNION 
	// { $setop = new UnionList(0); }
	//| INTERSECTION | MINUS | JOIN
	;
// References

attribute_reference 
	//returns [AttributeVariable at]
	:	(entity_reference)? attribute_id

	/*{
		String name;
		if ($entity_id.text != null) {
			name = $entity_id.text + $attribute_id.text;
	//  		System.out.println ("Named Entity.Attribute (fully qualified): " + name ); 
		} else {
			name = $def::myDefId + $attribute_id.text;
	//    		System.out.println ("Named Entity.Attribute (default qualified): "  + name ); 
		}

		$at = new AttributeVariable(name);
	
	// 	String item = $at.stdFormat();
	// 	System.out.println(item);
	}*/
	|	(entity_reference)? condition_id
	;


entity_reference
	:	entity_id
	|	entity_alias;

// Identifiers

// .[ATTRIBUTE_NAME]
attribute_id 
	:	DOT_LEFT_BRKT CAP_ID RIGHT_BRKT
	;

// .<CONDITION_NAME>
condition_id  
	:	DOT_LEFT_POINT CAP_ID RIGHT_POINT
	;

// [ENTITY_NAME]
entity_id
	:	LEFT_BRKT CAP_ID RIGHT_BRKT
	;
	
// F_function or fBUILTIN
function_id
	:	FUNCTION_ID
	|	BUILT_IN_FUNCTION
	;

// [ENTITY].[ID] id function name
id_function_id
	:	entity_id  ID
	;

// {POPULATION_NAME}
population_id 
	:	LEFT_BRACE CAP_ID RIGHT_BRACE
	;

// ENTITY_ALIAS
entity_alias
	:	CAP_ID
	;
	
// Parameters
storage_option	
	// returns [String storageStyle]
	:	STORAGE_OPT storage_type
	;

storage_type : OBJECT | TRANSACTION | OBSERVATION | EVENT | SPAN;



/* LEXER */

COMPARATOR
	:	'equals' | 'less than' | 'less or equal' | 'greater than' | 'greater or equal'
	|	'comparable to' | IS_A
	;

CONCEPTUAL_CONSTANT
	:	HASH CAP_ID+ HASH;
	
INDEPENDENT_VALUE
	:	HASH CONCEPTUAL_CONSTANT;
	
	
CONJUNCTION
	//returns [String conjunctionFunc]
	:   AND //{ $conjunctionFunc = DeltaTerm.AND_LIST_FUNC; }
	|   OR  //{ $conjunctionFunc = DeltaTerm.OR_LIST_FUNC; }
	;
	
BUILT_IN_FUNCTION
	:	'fCONCAT' | 'fADD' | 'fSUBTRACT' | 'fDIVIDE' | 'fMULTIPLY' | LEFT_BRKT 'ID' RIGHT_BRKT
	|	'fMAX' | 'fMIN' | 'fSUM' | 'fFIRST' | 'fLAST' | 'fCOUNT' | 'fFIRSTNN' | 'fLASTNN'
	;

BUILT_IN_FUNCTION_ARGUMENT
	:	'_COMBINATION_' | '_ADDEND_' | '_SUM_' | '_PRODUCT_' | '_QUOTIENT_' | '_REMAINDER_' | '_DIFFERENCE_' | '_DIVISOR_' 
	| '_CONCATENATION_';
	
// Built In function arguments or values
ID_FUNCTION_BUILT_IN_RESULT
	:	'_KEY_VALUE_'|'_ENTITY_KEY_';

TRUTH_VALUE
	:	'true' | 'false' | 'null' | 'unknown'
	;
	
FUNCTION_ID  :	'f_'('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;

OPERATION 
	:	MINUS | 'plus' | 'multiplied by' | 'divided by'
	;

// Special Tokens
ANALYSIS_HEAD	:	'ANALYSIS:';
ANALYSIS_END	:	'END ANALYSIS';
fragment AND		:	'and';
AT_LEAST	:	'at least' | 'At Least';
AT_SIGN		:	'@';
BETWEEN		: 	'between';
BINDING 	: 	LEFT_BRKT DASH;
COLON		:	':';
COMMA		:	',';
DASH		: 	'-';
DOT		:	'.';
DOT_LEFT_BRKT	:	DOT LEFT_BRKT;
DOT_LEFT_POINT	:	DOT LEFT_POINT;
EQU		:	'=';
EVENT		:	'event' | 'EVENT';
FOR		:	'for';
GIVEN_BY	:	'given by';
HASH		:	'#';
ID		:	DOT LEFT_BRKT 'ID' RIGHT_BRKT;
IN		:	'in';
IS		:	'is';
fragment IS_A		:	'is a';
INTERSECTION	:	'intersection';
JOIN		:	'join';
LEFT_BRACE	:	'{';
LEFT_BRKT	:	'[';
LEFT_POINT	:	'<';
LEFT_PARA	:	'(';
MATCHES		:	'matches';
fragment MINUS		:	'minus';
NOT		:	'not';
NOT_EQU		:	'!=';
OBJECT		:	'object' | 'OBJECT';
OBSERVATION	:	'observation' | 'OBSERVATION';
fragment OR		:	'or';
POPULATION_CONS	:	'@CONSTRAINT';
POPULATION_DEF	:	'@POP_DEF';
POPULATION_OPT	:	'@POPULATION';
RIGHT_BRACE	:	'}';
RIGHT_BRKT	:	']';
RIGHT_PARA	:	')';
RIGHT_POINT	:	'>';
SPAN		:	'span' | 'SPAN';
STORAGE_OPT	:	'@STORAGE';
SUBPOPULATION_OPT :	'@SUBPOPULATION_OF';
TRANSACTION	:	'transaction' | 'TRANSACTION';
UNION		:	'union';
USING		:	'using';
WITH		:	'with';
	
//OPTION
//	:	AT_SIGN CAP_ID
//	;

CAP_ID  :	('A'..'Z')('A'..'Z'|'0'..'9'|'_'|' ')*
    ;



/* ANTLR Generated */
//ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
//    ;

INT :	'0'..'9'+
    ;

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

STRING
    :  '"' ( ESC_SEQ | ~('\\'|'"') )* '"'
    ;

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
