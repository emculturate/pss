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
}


/*
===============================================================================
  SQL Script End Point: Any sequence of SQL statements, including DDL, DML, and queries

  Each semicolon-separated command is one sql_statement. Coverage:
    ddl_primary     -> CREATE, DROP, ALTER, TRUNCATE (standalone truncate_end_point uses same primaries)
    dml_primary     -> INSERT, UPDATE, DELETE (Snowflake or Postgres), VALUES-only
    with_query      -> WITH ... SELECT|INSERT|UPDATE|DELETE|VALUES (via inner query rule)
    query_expression -> SELECT pipelines without a leading WITH

  Standalone parse entry points not duplicated here (same underlying rules):
    sql, ddl, insert_end_point, update_end_point, delete_end_point, truncate_end_point,
    values_statement_end, column_value, predicand_value, literal_value, ...
===============================================================================
*/
script
  : (sql_statement)* sql_statement? EOF
  ;

sql_statement
  : (ddl_primary | dml_primary | with_query | query_expression) SEMI_COLON?
  ;

// DML statements that are full script commands (also used by insert/update/delete/truncate end points).
dml_primary
  : insert_expression
  | update_expression
  | delete_expression
  | values_statement_primary
  ;
/*
===============================================================================
  DDL Statements End Point: Create objects, delete objects, alter objects
===============================================================================
*/
ddl
  : ddl_primary  EOF
  ;

ddl_primary
  : (create_statement_primary | drop_statement_primary | alter_statement_primary | truncate_statement_primary)
   SEMI_COLON? 
   ;


 /*
===============================================================================
  Start Statements: SQL, Condition, Predicand and Literal
===============================================================================
*/
sql
  : (with_query
  | query
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
 // : search_condition EOF - search condition fails on building the table dictionary
  : value_expression EOF
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
  Values Statement Start Symbol
===============================================================================
*/
// Used only for Values end points
values_statement_end
  : values_statement_primary EOF;
 
 /*
===============================================================================
  Insert Statement Start Symbol
===============================================================================
*/
// Used only for Insert end points
insert_end_point
  : insert_expression EOF;

/*
===============================================================================
  Update Statement Start Symbol
===============================================================================
*/
// Used only for Update end points
update_end_point
  : update_expression EOF;

/*
===============================================================================
  Delete Statement Start Symbol
===============================================================================
*/
// Used only for Delete end points
delete_end_point
  : delete_expression EOF;

/*
===============================================================================
  Truncate Statement Start Symbol
===============================================================================
*/
// Used only for Truncate end points
truncate_end_point
  : truncate_statement_primary EOF;
 

/*
===============================================================================
  Dependent Grammar Rules
===============================================================================
*/
/*
===============================================================================
  DDL Statements: Create objects, delete objects, alter objects
===============================================================================
*/
create_statement_primary
  : create_table_expression
  | create_index_expression
  | create_view_expression
  | create_materialized_view_expression
  | create_function_expression
  | create_procedure_expression
  | create_macro_expression
  | create_sequence_expression
  | create_schema_expression
  | create_database_expression
  | create_role_expression
  | create_user_expression
  | create_stage_expression
  | create_file_format_expression
  ;

drop_statement_primary
   : DROP ddl_object_type db_object_name drop_options?
  ;

alter_statement_primary
  : ALTER ddl_object_type db_object_name alter_options?
  ;

truncate_statement_primary
  : truncate_snowflake_expression
  | truncate_postgres_expression
  ;

// Snowflake custom variant: TRUNCATE TABLE <name>
truncate_snowflake_expression
  : TRUNCATE TABLE db_object_name
  ;

// Postgres custom variant: TABLE keyword optional and supports multiple targets
truncate_postgres_expression
  : TRUNCATE TABLE? db_object_name (COMMA db_object_name)*
  ;
/*
  Create rules
 */

create_table_expression
  : CREATE TABLE db_object_name AS query_expression
  | CREATE TABLE db_object_name (LEFT_PAREN generic_ddl_paren_content RIGHT_PAREN)? generic_ddl_options?
  ;

create_index_expression
  : CREATE INDEX db_object_name ON db_object_name LEFT_PAREN column_reference_list RIGHT_PAREN
  ;

create_view_expression
  : CREATE VIEW db_object_name AS query_expression
  ;

create_materialized_view_expression
  : CREATE MATERIALIZED VIEW db_object_name AS query_expression
  ;

create_function_expression
  : CREATE FUNCTION db_object_name LEFT_PAREN generic_ddl_paren_content? RIGHT_PAREN RETURNS data_type generic_ddl_options
  ;

create_procedure_expression
  : CREATE PROCEDURE db_object_name LEFT_PAREN generic_ddl_paren_content? RIGHT_PAREN generic_ddl_options
  ;

create_macro_expression
  : CREATE MACRO db_object_name LEFT_PAREN generic_ddl_paren_content? RIGHT_PAREN AS query_expression
  ;

create_sequence_expression
  : CREATE SEQUENCE db_object_name generic_ddl_options
  ;

create_schema_expression
  : CREATE SCHEMA db_object_name generic_ddl_options?
  ;

create_database_expression
  : CREATE DATABASE db_object_name generic_ddl_options?
  ;

create_role_expression
  : CREATE ROLE db_object_name generic_ddl_options?
  ;

create_user_expression
  : CREATE USER db_object_name generic_ddl_options?
  ;

create_stage_expression
  : CREATE STAGE db_object_name generic_ddl_options?
  ;

create_file_format_expression
  : CREATE FILE FORMAT db_object_name generic_ddl_options?
  ;

/*
  Delete rules
 */

ddl_object_type
  : TABLE
  | INDEX
  | VIEW
  | FUNCTION
  | PROCEDURE
  | MACRO
  | SEQUENCE
  | SCHEMA
  | DATABASE
  | ROLE
  | USER
  | STAGE
  | FILE FORMAT
  | MATERIALIZED VIEW
  ;

drop_options
  : generic_ddl_options
  ;

 /* 
 Alter rules
 */

alter_options
  : generic_ddl_options
  ;

/*
  Generic DDL rules to capture content of DDL statements without fully parsing them, since DDL syntax is highly variable across dialects and not the primary focus of this parser.  These rules also allow for future extension to support more detailed parsing of DDL statements if desired.
 */
/*
   DDL options and sub-statements as default placeholder objects.
   Two flavors based on natural syntactic boundary:
     generic_ddl_paren_content - tokens inside a (...) group, with nested parentheses allowed
     generic_ddl_options       - trailing opaque tail through the next SEMI_COLON or end of input
                                 (parentheses and newlines in the source are part of the verbatim slice)
   Walker exits promote a single verbatim source-interval string (not token-rejoined text).
 */
generic_ddl_paren_content
  : ( ~(LEFT_PAREN | RIGHT_PAREN)
    | LEFT_PAREN generic_ddl_paren_content? RIGHT_PAREN
    )+
  ;

generic_ddl_options
  : (~SEMI_COLON)+
  ;

/*
===============================================================================
  WITH Statement <with query>
===============================================================================
*/

with_query
  : with_clause query
  ;

with_clause
  : WITH with_list_item (COMMA with_list_item)*
  ;
  
with_list_item
  : query_alias (LEFT_PAREN cte_body RIGHT_PAREN)
  | query_alias variable_identifier
  ;

// Snowflake: a CTE body may itself open a new WITH clause (nested WITH).
// Intentionally NOT reachable from subquery, insert_source_primary, or from_clause,
// so WITH is confined to CTE bodies only.
cte_body
  : with_query   // Snowflake nested WITH inside a CTE
  | query
  ;
  
query_alias
  : identifier AS
  ;

// Used by with_query / cte_body: SELECT-shaped or DML bodies inside a CTE.
query
  : query_expression
  | insert_expression
  | update_expression
  | delete_expression
  | values_statement_primary
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
  
Snowflake Syntax:
INSERT [ OVERWRITE ] INTO <target_table> [ ( <target_col_name> [ , ... ] ) ]
       {
         VALUES ( { <value> | DEFAULT | NULL } [ , ... ] ) [ , ( ... ) ]  |
         <query>
       }
 */
insert_expression
  : postgres_insert
;

snowflake_insert
  : insert_preamble  insert_target_table_primary insert_source_primary
  ;

// Dedicated target table rule for INSERT statements.
// Intentionally excludes table_function_primary so patterns like
// `INSERT INTO tab1 (c, d) ...` cannot be consumed as a table function call.
insert_target_table_primary
  : (db_object_name | variable_identifier | jinja_identifier) (LEFT_PAREN column_reference_list RIGHT_PAREN)? relation_as_clause?
  ;
  
postgres_insert
  : snowflake_insert on_conflict_clause? returning?
  ;

on_conflict_clause
  : ON conflict_keyword conflict_target? conflict_action
  ;

// Late-added lexer keywords (CONFLICT/DO/NOTHING) sit after Identifier; accept Identifier as fallback.
conflict_keyword
  : CONFLICT
  | Identifier
  ;

conflict_target
  : LEFT_PAREN column_reference_list RIGHT_PAREN
  ;

conflict_action
  : do_keyword nothing_keyword
  | do_keyword UPDATE SET assignment_expression_list where_clause?
  ;

do_keyword
  : DO
  | Identifier
  ;

nothing_keyword
  : NOTHING
  | Identifier
  ;

insert_default_values_statement
  : DEFAULT VALUES
  ;

insert_preamble
  : INSERT (OVERWRITE)? INTO 
  ;

insert_source_primary
  : query_expression
  | variable_identifier
  | insert_values_statement
  | insert_default_values_statement
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

/*
===============================================================================
  DELETE Statement <delete expression>
===============================================================================
*/
/*
 * POSTGRES:
[ WITH [ RECURSIVE ] with_query [, ...] ]
DELETE FROM [ ONLY ] table [ * ] [ [ AS ] alias ]
    [ USING using_list ]
    [ WHERE condition | WHERE CURRENT OF cursor_name ]
    [ RETURNING * | output_expression [ [ AS ] output_name ] [, ...] ]

 * SNOWFLAKE:
DELETE FROM <target_table>
    [USING <table_or_query_source_list>]
    [WHERE <predicate>]
 */
// Wrapper used only by delete_end_point for endpoint parsing of either dialect.
delete_expression
  : delete_snowflake_expression
  | delete_postgres_expression
  ;

// Snowflake variant: DELETE without RETURNING; does not produce row output; not valid as CTE body.
delete_snowflake_expression
  : DELETE FROM table_primary delete_using_clause? where_clause?
  ;

// Postgres variant: DELETE with RETURNING; produces row output; valid as CTE data source.
delete_postgres_expression
  : DELETE FROM table_primary delete_using_clause? where_clause? delete_returning
  ;

// Postgres RETURNING clause: reuses select_list so exitSelect_item populates the output interface.
delete_returning
  : RETURNING select_list
  ;

// Postgres/Snowflake custom overlap: USING supports joined table/query sources.
// table_reference_list preserves existing table/query dictionary collection behavior.
delete_using_clause
  : USING table_reference_list
  ;

// Postgres/Snowflake UPDATE RETURNING: reuses select_list so exitSelect_item populates the output interface.
returning
  : RETURNING select_list
  ;
  
assignment_expression_list
  :   assignment_expression (COMMA assignment_expression)*
  ;
  
assignment_expression
  : column_reference EQUAL row_value_predicand
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
  : set_operation_member (union_clause set_operation_member)*
  ;

set_operation_member
  : subquery as_clause?
  | query_specification
  | variable_identifier
  ;

union_clause
  : union_operator set_qualifier?
  ;

union_operator
   : (UNION | EXCEPT)
   ;

/*
===============================================================================
  SELECT Statement — subquery / query specification
  (former query_primary nonterminal removed: unreachable; set-op members and
   other parents reference subquery | query_specification | variable_identifier
   directly.)
===============================================================================
*/

subquery
  :  LEFT_PAREN query_expression RIGHT_PAREN
  ;

query_specification
  : SELECT into_list? set_qualifier? select_list 
  ( from_clause
    where_clause?
    groupby_clause?
    having_clause?
    qualify_clause?
    orderby_clause?
    limit_clause?)?
  ;

/*
===============================================================================
  SELECT Details
===============================================================================
*/


into_list
  : INTO db_object_name
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
  : wildcard_reference
  ;

wildcard_reference
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
  : table_primary ((COMMA lateral_modifier? table_primary)
    | (unqualified_join lateral_modifier? right=table_primary s=join_specification?)
    | (qualified_join lateral_modifier? right=table_primary s=join_specification?))*
  ;
  
  // Used for inserting optional Join Clauses to a query with a Join Extension variable
join_extension_primary
  : ((COMMA lateral_modifier? table_primary)
     | (unqualified_join lateral_modifier? right=table_primary s=join_specification?)
     | (qualified_join lateral_modifier? right=table_primary s=join_specification?))*  join_extension?
  ;

lateral_modifier
  : LATERAL
  ;

// Used anywhere a table name is expected.
// Source alias is optional; trailing modifier alias is gated by the modifier (no adjacent double-optional alias).
table_primary
  : table_source_primary relation_as_clause? (table_relational_modifier relation_as_clause?)?
  ;

// Bare relation sources only — aliases attach at table_primary (or not at all on tuple_primary).
table_source_primary
  : db_object_name
  | variable_identifier
  | jinja_identifier
  | table_function_primary
  | values_statement_primary
  | subquery
  ;

relation_as_clause
  : AS alias_identifier
  | {isAllowedImplicitAlias(_input.LT(1).getText())}? alias_identifier
  ;

// Used ONLY in the TUPLE Variable Substitution end point (no relation aliases).
tuple_primary
  : table_source_primary table_relational_modifier?
  ;



db_object_name
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
   UNPIVOT Relational Operator (Snowflake)
 */

table_relational_modifier
  : unpivot_clause
  | pivot_clause
  ;

// Postfix operator over a single table source that creates a derived relation namespace.
unpivot_clause
  : UNPIVOT unpivot_null_policy?
    LEFT_PAREN relational_modifier_value_column FOR relational_modifier_name_column 
    IN relational_modifier_list RIGHT_PAREN
  ;

relational_modifier_list
  : LEFT_PAREN relational_modifier_in_item (COMMA relational_modifier_in_item)* RIGHT_PAREN
  ;

unpivot_null_policy
  : INCLUDE NULLS
  | EXCLUDE NULLS
  ;

relational_modifier_value_column
  : relational_modifier_operand_column
  ;

relational_modifier_name_column
  : relational_modifier_operand_column
  ;


// Shared clause for both UNPIVOT and PIVOT IN lists
relational_modifier_in_item
  : column_reference relational_modifier_alias?
  ;


relational_modifier_alias
  : (AS)? (alias_identifier | Character_String_Literal)
  ;

/*
   PIVOT Relational Operator (Snowflake)
 */

// Postfix operator that rotates rows into columns over a single table source.
pivot_clause
  : PIVOT
    LEFT_PAREN pivot_aggregate_clause FOR relational_modifier_operand_column pivot_in_clause RIGHT_PAREN
    pivot_default_on_null_clause?
  ;

// Generic aggregate entry for PIVOT value position.
// Supports either a single aggregate expression (typical SQL)
// or Snowflake's documented comma-separated aggregate list.
pivot_aggregate_clause
  : pivot_aggregate
  | snowflake_pivot_aggregate_list
  ;

// <aggregate_function>(<pivot_column>) [[AS] <alias>]
pivot_aggregate
  : set_function_type LEFT_PAREN relational_modifier_operand_column RIGHT_PAREN relation_as_clause?
  ;

// Snowflake official PIVOT aggregate list support: AVG, COUNT, MAX, MIN, SUM.
snowflake_pivot_aggregate_list
  : snowflake_pivot_aggregate (COMMA snowflake_pivot_aggregate)*
  ;

snowflake_pivot_aggregate
  : snowflake_pivot_aggregate_function LEFT_PAREN relational_modifier_operand_column RIGHT_PAREN relation_as_clause?
  ;

snowflake_pivot_aggregate_function
  : AVG
  | COUNT
  | MAX
  | MIN
  | SUM
  ;


// Snowflake: IN ( <value-list> | ANY [ORDER BY ...] | <subquery> )
pivot_in_clause
  : IN LEFT_PAREN pivot_in_content RIGHT_PAREN
  ;

pivot_in_content
  : pivot_in_value_list
  | pivot_in_any
  | pivot_in_subquery
  ;

pivot_in_value_list
  : pivot_in_value (COMMA pivot_in_value)*
  ;

pivot_in_value
  : pivot_in_literal pivot_in_prefix?
  ;

// PIVOT IN entries are literal selector values, not column references.
pivot_in_literal
  : Character_String_Literal
  | identifier
  ;

// Snowflake optional prefix label for output columns.
pivot_in_prefix
  : AS alias_identifier
  ;

pivot_in_any
  : ANY orderby_clause?
  ;

pivot_in_subquery
  : query_expression
  ;

// Snowflake: [ DEFAULT ON NULL (<value>) ]
pivot_default_on_null_clause
  : DEFAULT ON NULL LEFT_PAREN value_expression RIGHT_PAREN
  ;

relational_modifier_operand_column
  : column_reference
  ;


/*
   TABLE FUNCTIONS
 */

table_function_primary
  : TABLE LEFT_PAREN table_function RIGHT_PAREN
  | table_function
  ;

table_function
  : flatten_table_function
  | generator_table_function
  | result_scan_table_function
  | infer_schema_table_function
  | validate_table_function
  | generic_table_function
  ;

flatten_table_function
  : flatten_function_name LEFT_PAREN
      flatten_argument_list
    RIGHT_PAREN
  ;

flatten_argument_list
  : flatten_argument (COMMA flatten_argument)*
    {containsFlattenInputArgument($ctx.getText())}?
  ;

flatten_argument
  : INPUT IMPLIES flatten_argument_value
  | PATH IMPLIES flatten_argument_value
  | OUTER IMPLIES flatten_argument_value
  | RECURSIVE IMPLIES flatten_argument_value
  | MODE IMPLIES m=table_argument_literal {isSnowflakeTableFunctionModeLiteral("FLATTEN", $m.text)}?
  ;

flatten_argument_value
  : value_expression
  | table_argument_literal
  | table_argument_boolean
  ;

flatten_function_name
  : FLATTEN
  ;

table_argument_literal
  : Character_String_Literal
  | signed_numeric_literal
  ;

table_argument_boolean
  : TRUE
  | FALSE
  ;

// GENERATOR( ROWCOUNT => <count> [ , TIMELIMIT => <sec> ] )
generator_table_function
  : generator_function_name LEFT_PAREN
      generator_argument_list
    RIGHT_PAREN
  ;

generator_argument_list
  : generator_argument (COMMA generator_argument)*
    {containsGeneratorRowcountArgument($ctx.getText())}?
  ;

generator_argument
  : ROWCOUNT IMPLIES generator_argument_value
  | TIMELIMIT IMPLIES generator_argument_value
  ;

generator_argument_value
  : additive_expression
  ;

generator_function_name
  : GENERATOR
  ;

// RESULT_SCAN( [ { '<query_id>' | <query_index> | LAST_QUERY_ID() } ] )
result_scan_table_function
  : result_scan_function_name LEFT_PAREN
      value_expression?
    RIGHT_PAREN
  ;

result_scan_function_name
  : RESULT_SCAN
  ;

// INFER_SCHEMA(
//   LOCATION  => '...'
//   [, FILE_FORMAT => '<name>']
//   [, FILES => ( '<file>' [, '<file>' ...] )]
//   [, IGNORE_CASE => TRUE | FALSE]
//   [, MAX_FILE_COUNT => <num>]
//   [, MAX_RECORDS_PER_FILE => <num>]
//   [, KIND => '<name>']
// )
infer_schema_table_function
  : infer_schema_function_name LEFT_PAREN
      infer_schema_argument_list
    RIGHT_PAREN
  ;

infer_schema_argument_list
  : infer_schema_argument (COMMA infer_schema_argument)*
    {containsInferSchemaLocationArgument($ctx.getText())}?
  ;

infer_schema_argument
  : LOCATION IMPLIES infer_schema_argument_value
  | FILE_FORMAT IMPLIES infer_schema_argument_value
  | FILES IMPLIES infer_schema_argument_value
  | IGNORE_CASE IMPLIES infer_schema_argument_value
  | MAX_FILE_COUNT IMPLIES infer_schema_argument_value
  | MAX_RECORDS_PER_FILE IMPLIES infer_schema_argument_value
  | KIND IMPLIES infer_schema_argument_value
  ;

infer_schema_argument_value
  : table_argument_literal
  | infer_schema_files_argument
  | additive_expression
  | table_argument_boolean
  ;

infer_schema_function_name
  : INFER_SCHEMA
  ;

infer_schema_files_argument
  : LEFT_PAREN Character_String_Literal (COMMA Character_String_Literal)* RIGHT_PAREN
  ;

// VALIDATE( [<namespace>.]<table_name> , JOB_ID => { '<query_id>' | '_last' } )
validate_table_function
  : validate_function_name LEFT_PAREN
      db_object_name
      COMMA JOB_ID IMPLIES table_argument_literal
    RIGHT_PAREN
  ;

validate_function_name
  : VALIDATE
  ;

generic_table_function
  : table_function_name LEFT_PAREN table_function_argument_list? RIGHT_PAREN
  ;

table_function_name
  : SPLIT_TO_TABLE
  | STRTOK_SPLIT_TO_TABLE
  | QUERY_HISTORY
  | identifier
  ;

table_function_argument_list
  : value_expression (COMMA value_expression)*
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
  : (tb_name=identifier DOT)? name=identifier (COLON path_name+=identifier)*
  | tb_name=identifier DOT substitution=variable_identifier
  ;

column_primary
  : (tb_name=identifier DOT)? name=identifier (COLON path_name+=identifier)*
  | tb_name=identifier DOT substitution=variable_identifier
  | substitution=variable_identifier
  ;

/*
===============================================================================
  Predicands <value expression primary>
===============================================================================
*/
   
predicand_primary
  : extract_expression
  | date_part_expression
  | value_expression_primary
  | string_value_expression
  | sign numeric_primary
  | trim_function
  | null_literal
  | variable_identifier
  | puml_constant_identifier
  | position_function
  ;

value_expression_primary
  : parenthesized_value_expression (CAST_OPERATOR data_type)*
  | nonparenthesized_value_expression_primary (CAST_OPERATOR data_type)*
  | null_literal CAST_OPERATOR data_type
  ;

parenthesized_value_expression
  : LEFT_PAREN value_expression RIGHT_PAREN
  ;

nonparenthesized_value_expression_primary
  : unsigned_literal
  | column_reference
  | variable_identifier
  | aggregate_function
  | case_expression
  | cast_function_expression
  | routine_invocation
  | position_function
  | window_over_partition_expression
  | predicand_subquery
  ;

predicand_subquery
// scalar subquery used in an anonymous location in a condition or value expression, such as in a comparison predicate 
//or in a select item expression
  : subquery
  ;
/*
===============================================================================
  Aggregate Over Sets Functions
===============================================================================
*/
aggregate_function
  : COUNT LEFT_PAREN wildcard_reference RIGHT_PAREN	# count_all_aggregate
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
  | COUNT_IF
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
  | FLATTEN
  | GENERATOR
  | INFER_SCHEMA
  | VALIDATE
  | RESULT_SCAN
  | SPLIT_TO_TABLE
  | STRTOK_SPLIT_TO_TABLE
  | QUERY_HISTORY
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
  : WHEN c=value_expression THEN r=case_result
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
  : extract_expression
  | date_part_expression
  | value_expression_primary
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

// Comma form EXTRACT(part, source) is not this rule — it parses as routine_invocation (function AST).
// Only the FROM keyword between field and source selects extract_expression (same policy as date_part_expression).

// Comma form DATE_PART(part, source) parses as routine_invocation, not this rule.
date_part_expression
  : DATE_PART LEFT_PAREN date_part_field=extract_field FROM date_part_source=extract_source RIGHT_PAREN
  ;

extract_field
  : primary_datetime_field
  | time_zone_field
  | extended_datetime_field
  | snowflake_extract_field
  | character_literal
  ;

// Snowflake EXTRACT date/time parts — must precede `Identifier` (same-length names otherwise lex as identifiers).
snowflake_extract_field
  : DAYOFMONTH | DAYOFWEEK | DAYOFWEEKISO | DAYOFYEAR
  | WEEKISO | WEEKOFYEAR
  | EPOCH_SECOND | EPOCH_MILLISECOND | EPOCH_MICROSECOND
  | identifier
  ;

time_zone_field
  : TIMEZONE | TIMEZONE_HOUR | TIMEZONE_MINUTE
  ;

extract_source
  : value_expression
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
  : trim_function_name LEFT_PAREN trim_operands RIGHT_PAREN (CAST_OPERATOR data_type)?
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
  xxx <position function>
===============================================================================
*/

position_function
  : position_function_name LEFT_PAREN search_string=string_value_expression IN source_string=string_value_expression RIGHT_PAREN
  | (position_function_name | instr_function_name | charindex_name)
      LEFT_PAREN search_string=string_value_expression 
      COMMA source_string=string_value_expression
      (COMMA start_position=numeric_primary)? RIGHT_PAREN
  ;

position_function_name
  : POSITION
  ;

instr_function_name
  : INSTR
  ;

charindex_name
  : CHARINDEX
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
  | like_any_predicate
  | null_predicate
  | exists_predicate
  | quantified_comparison_predicate
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
  : LIMIT e=additive_expression (OFFSET o=additive_expression)?
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

/*
===============================================================================
  QUALIFY <qualify clause>
  Snowflake post-window filtering (evaluated after HAVING, before ORDER BY)
===============================================================================
*/

qualify_clause
  : QUALIFY search_condition
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
  <in predicate> <like any predicate>>
===============================================================================
*/

in_predicate
  : row_value_predicand  not? IN in_predicate_value
  ;
  
  
like_any_predicate
  : row_value_predicand  not? like_any_operator in_predicate_value escape_character_clause?
  ;
  
like_any_operator
  : (LIKE | ILIKE) ANY
  ;  

in_predicate_value
  : subquery
  | LEFT_PAREN in_value_list RIGHT_PAREN
  | variable_identifier
  ;

in_value_list
  : row_value_expression  ( COMMA row_value_expression )*
  ;

escape_character_clause
  : ESCAPE Character_String_Literal
  ;

/*
===============================================================================
  ( VALUES ( <expr> [ , <expr> [ , ... ] ] ) [ , ( ... ) ] ) [ [ AS ] <table_alias> [ ( <column_alias> [, ... ] ) ] ]
  * 
  * Values statement can be used wherever a TUPLE can be referenced
  *
  *
===============================================================================
*/
 
values_statement_primary
  : fully_defined_values_statement
  | aliased_values_statement
  | values_statement
  ;

fully_defined_values_statement 
  : values_statement as_clause values_aliases
  ;

aliased_values_statement 
  : values_statement as_clause
  ;

values_statement
  :  LEFT_PAREN VALUES values_matrix RIGHT_PAREN
  ;
  
// Rows of values in a matrix for use in a Values statement  
values_matrix
  : values_row ( COMMA values_row)*
  ;
  
values_row
  : LEFT_PAREN in_value_list RIGHT_PAREN
  ;
  
// Values Aliases for the columns of a Values matrix  
values_aliases
  : (LEFT_PAREN values_aliases_list RIGHT_PAREN)
  ;
  
values_aliases_list
  :  alias_identifier ( COMMA alias_identifier)* 
  ;

// Used by substitution to insert a values matrix into a variable
insert_values_statement
  :  VALUES values_matrix
  ;

 /*
==============================================================================================
  8.9 <exists predicate>

  Specify a test for a non_empty set.
==============================================================================================
*/

exists_predicate
  : exists_operator exists_predicate_value
  ;

exists_operator
  : EXISTS
  ;  

exists_predicate_value
  : subquery
  | variable_identifier
  ;

 

/*
===============================================================================
  <null predicate>

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
  | IN
  | IFF
  | MD5
  | REVERSE
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
  | snowflake_dollar_function_identifier
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
	|  Mixed_Variable_Identifier
	;

/*
===============================================================================
  Jinja / DBT-style table reference identifiers
  Covers: {{ ref(...) }}, {{ source(...) }}, {{ stream(...) }},
          {{ var(...) }}, {{ env_var(...) }}, {{ config(...) }},
          {{ this }}, {{ this.identifier }}, {{ target.schema }}, etc.
===============================================================================
*/

jinja_identifier
  : JINJA_OPEN jinja_function_call JINJA_CLOSE
  | JINJA_OPEN jinja_variable_access JINJA_CLOSE
  ;

// Any zero-or-more-argument call: ref(...), source(...), stream(...), var(...), env_var(...), config.get(...)
jinja_function_call
  : func_name=identifier
    {isJinjaPrimaryFunction($func_name.text)}?
    LEFT_PAREN jinja_arg_list? RIGHT_PAREN
  | object_name=identifier DOT method_name=identifier
    {isJinjaObjectMethod($object_name.text, $method_name.text)}?
    LEFT_PAREN jinja_arg_list? RIGHT_PAREN
  ;

jinja_arg_list
  : jinja_arg (COMMA jinja_arg)*
  ;

// Positional string arg, numeric arg, or keyword arg (e.g. v=2 for dbt ref versioning)
jinja_arg
  : Character_String_Literal
  | NUMBER
  | kw_name=identifier EQUAL Character_String_Literal
  | kw_name=identifier EQUAL NUMBER
  ;

// Property-chain variable: this, this.identifier, target.schema, target.database, etc.
jinja_variable_access
  : jinja_name (DOT jinja_name)*
  ;

jinja_name
  : identifier
  | NUMBER
  ;
 
simple_numeric_identifier
   :	Numeric_Identifier
   |	NUMBER
   ;
   
snowflake_quoted_numeric_identifier
   :	Double_Quoted_Numeric_Identifier
   ;
   
snowflake_dollar_function_identifier
   : Dollar_Sign_Identifier
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
  | 	DATE_PART
  | 	DATETIME     // SNOWFLAKE
  | 	DAY
  | 	DEC
  | 	DECADE
  | 	DEFAULT
  | 	DESC
  | 	DOUBLE
  | 	DOW
  | 	DOY
  | 	DELETE
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
  |   LEAD
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
  | 	ROW_NUMBER
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
  // 2025 Additions
  | ESCAPE
  // DDL keywords usable as SQL identifiers (schema-qualified names, aliases, etc.)
  | ALTER
  | DATABASE
  | FILE
  | FUNCTION
  | MACRO
  | MATERIALIZED
  | PROCEDURE
  | RETURNS
  | ROLE
  | SCHEMA
  | SEQUENCE
  | STAGE
  | USER
  | VIEW
  // Snowflake table-function keywords also usable as regular identifiers
  | FLATTEN
  | GENERATOR
  | INFER_SCHEMA
  | VALIDATE
  | RESULT_SCAN
  | SPLIT_TO_TABLE
  | STRTOK_SPLIT_TO_TABLE
  | QUERY_HISTORY
  // Snowflake PIVOT / UNPIVOT contextual keywords — usable as identifiers
  | PIVOT
  | UNPIVOT
  | FOR
  | INCLUDE
  | EXCLUDE
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
    |   Scientific_Numeric_Literal
    ;

exponent : EXPONEN   NUMBER ;

general_literal
  : character_literal
  | datetime_literal
  | interval_literal
  | boolean_literal
  ;

interval_literal
  : INTERVAL interval_string=Character_String_Literal
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
  | TIMESTAMP_LTZ     // SNOWFLAKE
  | TIMESTAMP_NTZ     // SNOWFLAKE
  | TIMESTAMP_TZ     // SNOWFLAKE
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
  
   
puml_constant_identifier
  : PUML_CONSTANT_TENANT_SK
  | PUML_CONSTANT_TENANT_GUID
  | PUML_CONSTANT_TENANT_MASTER_ID
  | PUML_CONSTANT_TENANT_NAME
  | PUML_CONSTANT_TENANT_ACRONYM
  | PUML_CONSTANT_TENANT_WEB_DOMAIN
  | PUML_CONSTANT_ES_INSTITUTION_ID
  | PUML_CONSTANT_ES_INSTITUTION_CODE
  | PUML_CONSTANT_ES_INSTITUTION_NAME
  | PUML_CONSTANT_SF_COUNTER_ID
  | PUML_CONSTANT_FILE_NAME
  | PUML_CONSTANT_FILE_ID
  | PUML_CONSTANT_ROW_NUMBER
  | PUML_CONSTANT_OBSERVATION_TIME
  | PUML_CONSTANT_SYSTEM_DATE
  | PUML_CONSTANT_SYSTEM_TIME
  | PUML_CONSTANT_FEED_RUN_ID
  | PUML_CONSTANT_FEED_NAME
  | PUML_CONSTANT_TRANSACTION_RUN_ID
  | PUML_CONSTANT_TRANSACTION_NAME
  | PUML_CONSTANT_POPULATION
  | PUML_CONSTANT_TARGET_MODEL_NAME
  // Added on June 15, 2021
  | PUML_CONSTANT_TENANT_SALT 
  | PUML_CONSTANT_PIT_START_TIME 
  | PUML_CONSTANT_PIT_END_TIME
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

OFFSET : O F F S E T;
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
COUNT_IF : C O U N T '_' I F;
CUBE : C U B E;
CURRENT : C U R R E N T;

DAY : D A Y;
DEC : D E C;
DECADE : D E C A D E;
DEFAULT : D E F A U L T;
DESC : D E S C;
DOW : D O W;
DOY : D O Y;
DROP : D R O P;

EPOCH : E P O C H;
ESCAPE: E S C A P E;
EVERY : E V E R Y;
EXISTS : E X I S T S;
EXTERNAL : E X T E R N A L;
EXTRACT : [Ee] [Xx] [Tt] [Rr] [Aa] [Cc] [Tt] ;


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
IMPLIES : '=>';
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
PUML_CONSTANT_TENANT_SK : '#' T E N A N T '_' S K;
PUML_CONSTANT_TENANT_GUID : '#' T E N A N T '_' G U I D;
PUML_CONSTANT_TENANT_MASTER_ID : '#' T E N A N T '_' M A S T E R '_' I D;
PUML_CONSTANT_TENANT_NAME : '#' T E N A N T '_' N A M E;
PUML_CONSTANT_TENANT_ACRONYM : '#' T E N A N T '_' A C R O N Y M;
PUML_CONSTANT_TENANT_WEB_DOMAIN : '#' T E N A N T '_' W E B '_' D O M A I N;
PUML_CONSTANT_ES_INSTITUTION_ID : '#' E S '_' I N S T I T U T I O N '_' I D;
PUML_CONSTANT_ES_INSTITUTION_CODE : '#' E S '_' I N S T I T U T I O N '_' C O D E;
PUML_CONSTANT_ES_INSTITUTION_NAME : '#' E S '_' I N S T I T U T I O N '_' N A M E;
PUML_CONSTANT_SF_COUNTER_ID : '#' S F '_' C O U N T E R '_' I D;
  
PUML_CONSTANT_FILE_NAME : '#' S O U R C E '_' F I L E '_' N A M E;
PUML_CONSTANT_FILE_ID : '#' F I L E '_' I D;
PUML_CONSTANT_ROW_NUMBER : '#' R O W '_' I D;
PUML_CONSTANT_OBSERVATION_TIME : '#' O B S E R V A T I O N '_' T I M E;
PUML_CONSTANT_SYSTEM_DATE : '#' S Y S T E M '_' D A T E;
PUML_CONSTANT_SYSTEM_TIME : '#' S Y S T E M '_' T I M E;
PUML_CONSTANT_FEED_RUN_ID : '#' F E E D '_' R U N '_' I D;
PUML_CONSTANT_FEED_NAME : '#' F E E D '_' N A M E;
PUML_CONSTANT_TRANSACTION_RUN_ID : '#' T R A N S A C T I O N '_' R U N '_' I D;
PUML_CONSTANT_TRANSACTION_NAME : '#' T R A N S A C T I O N '_' N A M E;
PUML_CONSTANT_POPULATION : '#' P O P U L A T I O N '_' N A M E;
PUML_CONSTANT_TARGET_MODEL_NAME : '#' T A R G E T '_' M O D E L '_' N A M E;
  
PUML_CONSTANT_TENANT_SALT : '#' T E N A N T '_' S A L T;
PUML_CONSTANT_PIT_START_TIME : '#' P I T '_' S T A R T '_' T I M E;
PUML_CONSTANT_PIT_END_TIME : '#' P I T '_' E N D '_' T I M E;
  
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
	:	LTH '['('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|Digit|'_'|' '|'.'|'-')* ']'  
	    ('.' ('['('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|Digit|'_'|' '|'.'|'-')* ']') | (Period Population_Identifier))? 
	    ('.' ('['('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|Digit|'_'|' '|'.'|'-')* ']') | (Period Population_Identifier))? 
	    ('.' ('['('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|Digit|'_'|' '|'.'|'-')* ']') | (Period Population_Identifier))? 
	    GTH
	;

Mixed_Variable_Identifier	
	:	LTH ('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|Digit|'_'|' '|'.'|'-')*
	    Period '['('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|Digit|'_'|' '|'.'|'-')* ']'  
	    ('.' ('['('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|Digit|'_'|' '|'.'|'-')* ']') | (Period Population_Identifier))? 
	    ('.' ('['('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|Digit|'_'|' '|'.'|'-')* ']') | (Period Population_Identifier))? 
	    ('.' ('['('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|Digit|'_'|' '|'.'|'-')* ']') | (Period Population_Identifier))? 
	    GTH
	;

fragment
Population_Identifier	
	:	'{'('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|Digit|'_'|' '|'-'|'.')*'}'
	;

/*
===============================================================================
  Late-added reserved keywords
  Keep near Identifier to minimize token-number shifts in legacy tests.
===============================================================================
*/

QUALIFY : Q U A L I F Y;
POSITION : P O S I T I O N;
CHARINDEX : C H A R I N D E X;
INSTR : I N S T R;
DELETE : D E L E T E;
TRUNCATE : T R U N C A T E;
IFF : I F F;
MD5 : M D '5';
REVERSE : R E V E R S E;
FLATTEN : F L A T T E N;
SPLIT_TO_TABLE : S P L I T UNDERLINE T O UNDERLINE T A B L E;
STRTOK_SPLIT_TO_TABLE : S T R T O K UNDERLINE S P L I T UNDERLINE T O UNDERLINE T A B L E;
GENERATOR : G E N E R A T O R;
INFER_SCHEMA : I N F E R UNDERLINE S C H E M A;
VALIDATE : V A L I D A T E;
RESULT_SCAN : R E S U L T UNDERLINE S C A N;
QUERY_HISTORY : Q U E R Y UNDERLINE H I S T O R Y;
QUERY_HSTORY : Q U E R Y UNDERLINE H S T O R Y;
INPUT : I N P U T;
PATH : P A T H;
RECURSIVE : R E C U R S I V E;
MODE : M O D E;
LATERAL : L A T E R A L;
ROWCOUNT : R O W C O U N T;
TIMELIMIT : T I M E L I M I T;
FILES : F I L E S;
FILE_FORMAT : F I L E UNDERLINE F O R M A T;
IGNORE_CASE : I G N O R E UNDERLINE C A S E;
MAX_FILE_COUNT : M A X UNDERLINE F I L E UNDERLINE C O U N T;
MAX_RECORDS_PER_FILE : M A X UNDERLINE R E C O R D S UNDERLINE P E R UNDERLINE F I L E;
KIND : K I N D;
JOB_ID : J O B UNDERLINE I D;
ALTER : A L T E R;
DATABASE : D A T A B A S E;
FILE : F I L E;
FUNCTION : F U N C T I O N;
MACRO : M A C R O;
MATERIALIZED : M A T E R I A L I Z E D;
PROCEDURE : P R O C E D U R E;
RETURNS : R E T U R N S;
ROLE : R O L E;
SCHEMA : S C H E M A;
SEQUENCE : S E Q U E N C E;
STAGE : S T A G E;
USER : U S E R;
VIEW : V I E W;
FOR : F O R;
INCLUDE : I N C L U D E;
EXCLUDE : E X C L U D E;
UNPIVOT : U N P I V O T;
PIVOT : P I V O T;

DAYOFMONTH : D A Y O F M O N T H;
DAYOFWEEK : D A Y O F W E E K;
DAYOFWEEKISO : D A Y O F W E E K I S O;
DAYOFYEAR : D A Y O F Y E A R;
WEEKISO : W E E K I S O;
WEEKOFYEAR : W E E K O F Y E A R;
EPOCH_MICROSECOND : E P O C H UNDERLINE M I C R O S E C O N D;
EPOCH_MILLISECOND : E P O C H UNDERLINE M I L L I S E C O N D;
EPOCH_SECOND : E P O C H UNDERLINE S E C O N D;

/*
  New lexer keywords for grammar extensions: append HERE (after PIVOT/Snowflake extract
  parts, immediately before Identifier). Do NOT insert among mid-alphabet keyword blocks
  (e.g. after DATE) — that renumbers hundreds of legacy token IDs in walker test goldens.
*/
DATE_PART : [Dd] [Aa] [Tt] [Ee] '_' [Pp] [Aa] [Rr] [Tt] ;

Identifier
  : ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|Digit|'_')*
  | DOUBLE_QUOTE ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|Digit|'_'|'-')* DOUBLE_QUOTE
  ;

EXPONEN : E ('+' | '-')?;
  
Scientific_Numeric_Literal
  : Digit+ Period Digit* [eE] ('+' | '-')? Digit+
  | Period Digit+ [eE] ('+' | '-')? Digit+
  | Digit+ [eE] ('+' | '-')? Digit+
  ;

Numeric_Identifier
  :  Digit+ ('a'..'z'|'A'..'Z'|Digit|'_')*
  ;

Double_Quoted_Numeric_Identifier
  : DOUBLE_QUOTE Digit+ ('a'..'z'|'A'..'Z'|Digit|'_'|'-')* DOUBLE_QUOTE
  ;


Dollar_Sign_Identifier
  : ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|Digit|'_'|'$')*
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
  : QUOTE ( ESC_SEQ | DOUBLE_QUOTE_ESCAPE | ~('\\'|'\'') )* QUOTE
  ;

Pivot_Identifier
  : ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|Digit|'_')*
;

fragment
DOUBLE_QUOTE_ESCAPE
  : QUOTE QUOTE
  ;

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
 Whitespace Tokens
===============================================================================
*/

Space
  : ' ' -> skip
  ;

White_Space
  :	( Control_Characters  | Extended_Control_Characters )+ -> skip
  ;

/*
===============================================================================
  Late-added keywords (Postgres INSERT ON CONFLICT, etc.)
  Keep near the end to avoid renumbering legacy token IDs used in tests.
  Same policy as DATE_PART / Snowflake extract parts: new reserved words go
  immediately before Identifier (or in this trailing block after literals), never
  in the middle of the main keyword alphabet.
===============================================================================
*/
CONFLICT : C O N F L I C T;
CONSTRAINT : C O N S T R A I N T;
DO : D O;
NOTHING : N O T H I N G;

/*
===============================================================================
  Jinja delimiters
  Keep these near the end to avoid renumbering legacy token IDs used in tests.
===============================================================================
*/
JINJA_OPEN
  : '{{'
  ;

JINJA_CLOSE
  : '}}'
  ;


BAD
  : . -> skip
  ; 