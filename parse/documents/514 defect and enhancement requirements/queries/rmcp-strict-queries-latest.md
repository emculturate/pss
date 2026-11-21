# RMCP strict dual-parser assessment — queries

- Generated: 2026-11-19T22:34:20.723Z
- Raw capture: `rmcp/tmp/strict_raw/live_queries_11-19.raw.jsonl.gz`
- Versions: **5.0.0-3** vs **5.1.4**
- Rows assessed: **6549**
- Strict verdicts: clean **2917**, contract_change **2040**, review **1592**
- Blocking tags (drive `review`): `CANDIDATE_ONLY_FAILED`, `CANDIDATE_NULL_TREE`, `CANDIDATE_FATAL_NEW`, `CANDIDATE_SEVERE_NEW`, `TABLE_KEY_LOST`, `TABLE_COLUMN_LOST`, `SUBSTITUTION_LOST`, `SUBSTITUTION_TYPE_CHANGED`, `INTERFACE_COLUMN_LOST`, `INTERFACE_LENGTH_CHANGED`

## Blocking findings in rows the lenient compare passed

Rows the lenient compare scored as match, essentialMatch, or improvement that carry a blocking strict finding.

| Tag | Rows |
|---|---:|
| `TABLE_KEY_LOST` | 717 |
| `CANDIDATE_SEVERE_NEW` | 223 |
| `TABLE_COLUMN_LOST` | 97 |
| `CANDIDATE_FATAL_NEW` | 64 |
| `INTERFACE_LENGTH_CHANGED` | 1 |

## Findings by tag

| Tag | Rows | By group | By lenient verdict |
|---|---:|---|---|
| `CANDIDATE_SLOWER` | 1438 | SQL: 1438 | match: 966, mixed: 456, degradation: 16 |
| `AST_NODE_KIND_ADDED` | 1419 | SQL: 1419 | improvement: 638, mixed: 454, match: 327 |
| `AST_NODE_KIND_LOST` | 1313 | SQL: 1313 | improvement: 641, mixed: 345, match: 327 |
| `TABLE_KEY_LOST` ⚑ | 1192 | SQL: 1192 | improvement: 717, mixed: 472, degradation: 3 |
| `CANDIDATE_WARNING_NEW` | 749 | SQL: 749 | mixed: 436, improvement: 313 |
| `TABLE_COLUMN_ADDED` | 549 | SQL: 549 | improvement: 475, mixed: 74 |
| `TABLE_KEY_QUALIFICATION_CHANGED` | 515 | SQL: 515 | mixed: 235, improvement: 182, match: 98 |
| `CANDIDATE_SEVERE_NEW` ⚑ | 472 | SQL: 472 | mixed: 249, improvement: 223 |
| `TABLE_COLUMN_LOST` ⚑ | 314 | SQL: 314 | mixed: 192, match: 84, degradation: 25, improvement: 13 |
| `TABLE_COLUMN_CASE_CHANGED` | 281 | SQL: 281 | match: 191, mixed: 66, improvement: 20, degradation: 4 |
| `CANDIDATE_FATAL_NEW` ⚑ | 170 | SQL: 170 | mixed: 78, improvement: 64, error: 22, degradation: 6 |
| `BASELINE_DIAGNOSTIC_DROPPED` | 73 | SQL: 73 | mixed: 52, improvement: 21 |
| `CANDIDATE_NULL_TREE` ⚑ | 65 | SQL: 65 | mixed: 43, error: 22 |
| `FATAL_BOTH` | 49 | SQL: 49 | mixed: 49 |
| `INTERFACE_LENGTH_CHANGED` ⚑ | 45 | SQL: 45 | mixed: 44, improvement: 1 |
| `INTERFACE_COLUMN_LOST` ⚑ | 44 | SQL: 44 | mixed: 44 |
| `SUBSTITUTION_LOST` ⚑ | 39 | SQL: 39 | mixed: 39 |
| `BASELINE_ONLY_FAILED` | 30 | SQL: 30 | error: 30 |
| `BASELINE_FATAL_ONLY` | 24 | SQL: 24 | improvement: 21, mixed: 3 |
| `INTERFACE_COLUMN_ADDED` | 2 | SQL: 2 | mixed: 1, improvement: 1 |

## New 5.1.4 FATAL signatures

| Signature (severity \| code \| message) | Rows |
|---|---:|
| FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: EAB_RACE_AMER_INDIAN at (l:# c:#) and EAB_RACE_AMER_INDIAN at (l:# c:#). | 104 |
| FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: EAB_RACE_BLACK at (l:# c:#) and EAB_RACE_BLACK at (l:# c:#). | 104 |
| FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: EAB_RACE_NATIVE_HAWAIIAN at (l:# c:#) and EAB_RACE_NATIVE_HAWAIIAN at (l:# c:#). | 104 |
| FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: EAB_RACE_WHITE at (l:# c:#) and EAB_RACE_WHITE at (l:# c:#). | 104 |
| FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: EAB_RACE_MIDDLE_EASTERN at (l:# c:#) and EAB_RACE_MIDDLE_EASTERN at (l:# c:#). | 52 |
| FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: EAB_RACE_ASIAN at (l:# c:#) and EAB_RACE_ASIAN at (l:# c:#). | 46 |
| FATAL\|AST_WALKER_STACK_MISALIGN\|Semantic analysis aborted: walker stack mis-aligned during AST walk. | 10 |
| FATAL\|INVALID_DATETIME_PART\|Invalid date/time part "dd" for DATEDIFF at (l:# c:#). | 8 |
| FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: EAB_RACE_OTHER at (l:# c:#) and EAB_RACE_OTHER at (l:# c:#). | 6 |
| FATAL\|APPLICATION_ISSUE_FATAL\|Exception when walking the parse tree: class java.lang.String cannot be cast to class java.util.Map (java.lang.String and java.util.Map are in module java.base  | 5 |
| FATAL\|AST_WALKER_STACK_MISALIGN\|Semantic analysis aborted: walker stack mis-aligned at sql (line #, position #). | 3 |
| FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: cc_pdp.interested_institutions_list at (l:# c:#) and cc_pdp.interested_institutions_list at (l:# c:#). | 3 |
| FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: cc_pdp.desired_completion_timeframe at (l:# c:#) and cc_pdp.desired_completion_timeframe at (l:# c:#). | 3 |
| FATAL\|SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH\|UNION has different column counts. Expected # columns (rn, sourcecontact_id, act_english_score, act_math_score, act_reading_score, act_science_score, act_writin | 3 |
| FATAL\|QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE\|Source Table not found for Column 'field_name' at (l:# c:#), (l:# c:#). No alias or table called 'pcm_first_gen'. | 2 |
| FATAL\|REPORT_ERROR\|unexpected input: '<EOF>' in rule table_source_primary: /*phase #*/ --<[Enroll#].[Student Year Finaid Application].{par_orphans}> syfa_par_orphans | 2 |
| FATAL\|APPLICATION_ISSUE_FATAL\|Exception when walking the parse tree: Missing AST node map for table_source_primary at: <EOF> | 2 |
| FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: stud.origin_source_date at (l:# c:#) and stud.origin_source_date at (l:# c:#). | 2 |
| FATAL\|QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS\|Qualified column 'contact_key' at (l:# c:#) was not found in output interface of query alias 'xwalk'. | 2 |
| FATAL\|QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS\|Qualified column 'dummy_email' at (l:# c:#) was not found in output interface of query alias 'xwalk'. Locations: [(l:# c:#), (l:# c:#), (l:# c:#), (l:# c:#), (l | 2 |
| FATAL\|QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE\|Source Table not found for Column 'acs_contact_id' at (l:# c:#). No alias or table called 'CAC'. | 1 |
| FATAL\|QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE\|Source Table not found for Column '<Print Display ID>' at (l:# c:#). No alias or table called 'CAC'. | 1 |
| FATAL\|QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE\|Source Table not found for Column 'alpha_#_code' at (l:# c:#). No alias or table called 'countries'. | 1 |
| FATAL\|QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE\|Source Table not found for Column 'undergraduate_institution_city' at (l:# c:#). No alias or table called 'x'. | 1 |
| FATAL\|QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE\|Source Table not found for Column 'undergraduate_institution_transfer_credits' at (l:# c:#). No alias or table called 'x'. | 1 |

## New 5.1.4 ERROR / SEVERE_WARNING signatures

| Signature (severity \| code \| message) | Rows |
|---|---:|
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'intake_dt' at (l:# c:#). Possible sources: [<fy_credited_gifts_allocation>, union#] | 24 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'eab_military_veteran_ind' at (l:# c:#). Possible sources: [<[ALR].[Partner Code Mapping].{Countries}>, <student_prior_college>, <stu | 22 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'intake_type_label' at (l:# c:#). Possible sources: [pdp.crf__contact_sourcecontacts, pdp.crf__intake_type] | 21 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'contact_key' at (l:# c:#). Possible sources: [pdp.crf__contact_sourcecontacts, pdp.crf__intake_type] | 21 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'source_label' at (l:# c:#). Possible sources: [pdp.crf__contact_sourcecontacts, pdp.crf__source] | 21 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'contact_key' at (l:# c:#). Possible sources: [pdp.crf__contact_sourcecontacts, pdp.crf__source] | 21 |
| SEVERE_WARNING\|QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE\|Source Table not found for Column 'eab_standard_value' at (l:# c:#). No alias or table called 'pcm_deferral_prev_ind'. | 20 |
| SEVERE_WARNING\|QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE\|Source Table not found for Column 'eab_standard_value' at (l:# c:#). No alias or table called 'pcm_deferral_future_ind'. | 20 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'acs_contact_id' at (l:# c:#). Possible sources: [pdp.acs__contact_activity, pdp.acs__emails, pdp.acs__categories] | 20 |
| SEVERE_WARNING\|QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE\|Source Table not found for Column 'primary_student_id' at (l:# c:#). No alias or table called 'crm_ug'. | 15 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'primary_user_id' at (l:# c:#). Possible sources: [<[Strategic Care STC].[student_major].[Current Batch]>, <[Strategic Care STC].[ter | 15 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'total_credit_hours' at (l:# c:#). Possible sources: [<[Strategic Care STC].[student_major].[Current Batch]>, <[Strategic Care STC].[ | 15 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'attempted_credit_hours' at (l:# c:#). Possible sources: [<[Strategic Care STC].[student_major].[Current Batch]>, <[Strategic Care ST | 15 |
| SEVERE_WARNING\|JOIN_EXTENSION_REQUIRED\|Join extension variable '<join_extension.Student Term Application>' at (l:# c:#) is not optional: # table/column reference(s) not defined in the query (pcm_defe | 14 |
| SEVERE_WARNING\|QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE\|Source Table not found for Column 'max_event_date' at (l:# c:#). No alias or table called 'event_agg'. | 12 |
| SEVERE_WARNING\|QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE\|Source Table not found for Column 'event_ct' at (l:# c:#). No alias or table called 'event_agg'. | 12 |
| SEVERE_WARNING\|QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE\|Source Table not found for Column 'min_event_date' at (l:# c:#). No alias or table called 'event_agg'. | 12 |
| SEVERE_WARNING\|QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE\|Source Table not found for Column 'primary_student_id_ev' at (l:# c:#). No alias or table called 'event_agg'. | 11 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'intake_date' at (l:# c:#). Possible sources: [<[AMS].[gifts].{fulfillment}>, <eab_marketing_inclusion_ind>, <eab_reporting_inclusion | 10 |
| SEVERE_WARNING\|JOIN_EXTENSION_REQUIRED\|Join extension variable '<join_extension.Student>' at (l:# c:#) is not optional: # table/column reference(s) not defined in the query (event_agg.primary_student | 10 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'attempted_credit_hours' at (l:# c:#). Possible sources: [<[Strategic Care STC].[student].[Current Batch]>, query#] | 10 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'country' at (l:# c:#). Possible sources: [es_pdp_common.common.ref__deletion_requests, query#] | 9 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'rnum' at (l:# c:#). Possible sources: [<[PDP_UG].[gdpr__record_deletion].{final}>, query#] | 9 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'first_email_create_dt' at (l:# c:#). Possible sources: [<[Partner_Data_Platform].[Contact Address Clean]>, query#] | 8 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'intake_type_label' at (l:# c:#). Possible sources: [<[Partner_Data_Platform].[Contact Sourcecontacts]>, pdp.crf__intake_type] | 7 |

## 5.0.0-3 diagnostics not reported by 5.1.4

| Signature (severity \| code \| message) | Rows |
|---|---:|
| FATAL\|\|Invalid syntax near 'select' | 63 |
| FATAL\|\|Invalid syntax near 'case' | 2 |
| FATAL\|\|Invalid syntax near '(' | 2 |
| FATAL\|\|Invalid syntax near '.' | 1 |
| FATAL\|\|Invalid syntax near 'from' | 1 |
| FATAL\|\|Invalid syntax near 'x' | 1 |
| FATAL\|\|Invalid syntax near '<join_extension_donor_phone>' | 1 |
| FATAL\|\|null - unexpected input: '<EOF>' | 1 |
| FATAL\|\|Syntax error, attempting recovery | 1 |
| FATAL\|\|Invalid syntax near ',' | 1 |
| FATAL\|\|Invalid syntax near '<EOF>' | 1 |
| FATAL\|\|Invalid syntax near 'agg#' | 1 |
| FATAL\|\|Invalid syntax near 'Select' | 1 |

## AST node kinds

| Node kind | Rows lost | Rows added |
|---|---:|---:|
| `cte` | 0 | 1293 |
| `ST_student_term_sweep` | 159 | 0 |
| `AA10_downfill_major_cd1` | 158 | 0 |
| `AA19_new_cd_2_downfill_cd_3` | 158 | 0 |
| `AA27_final_code_4_values` | 158 | 0 |
| `AA16_new_cd_1_downfill_cd_2` | 158 | 0 |
| `AA13_final_major_cd_1_downfill_cd_1` | 158 | 0 |
| `AA12_major_cd_2_partition_cd_1` | 158 | 0 |
| `AA18_backfill_cd_2_partition_downfill_cd_3_partition` | 158 | 0 |
| `AA21_backfill_cd_3` | 158 | 0 |
| `AA15_backfill_cd_1_downfill_cd_2_partitions` | 158 | 0 |
| `AA24_downfill_code_4_partition` | 158 | 0 |
| `AA23_prev_major_cd_4` | 158 | 0 |
| `AA11_prev_downfill_major_cd1` | 158 | 0 |
| `AA22_final_3_values` | 158 | 0 |
| `AA26_backfill_code_4_partition` | 158 | 0 |
| `AA20_prev_major_cd_3` | 158 | 0 |
| `AA25_downfill_code_4_values` | 158 | 0 |
| `AA14_prev_major_cd_1` | 158 | 0 |
| `AA17_prev_major_cd_2` | 158 | 0 |
| `AA0_student_term` | 157 | 0 |
| `datetime_function` | 0 | 136 |
| `race_data` | 106 | 0 |
| `part_form` | 0 | 78 |
| `part` | 0 | 78 |
| `source` | 0 | 78 |
| `cte_credit_year` | 64 | 0 |
| `Ee_Aggregator_AGG_de_dupe` | 63 | 0 |
| `level` | 0 | 58 |
| `canonical` | 0 | 58 |
| `text` | 0 | 58 |
| `form` | 0 | 58 |
| `Ff_Aggregator_AGG_de_dupe` | 46 | 0 |
| `increment` | 0 | 41 |
| `value` | 0 | 36 |
| `gifts_allocation` | 35 | 0 |
| `Ii_Expression_EXP_stg_template_logic_2` | 31 | 0 |
| `Hh_Expression_EXP_stg_template_logic_2` | 29 | 0 |
| `Ee_Aggregator_agg_de_dupe` | 28 | 0 |
| `Gg_Expression_EXP_stg_template_logic_2` | 27 | 0 |

## Payload contract keys (parse object)

- 5.0.0-3 (6519 rows): `errors,fatalErrorCount,interface,messages,sqlTree,substitutions,symbolTable,tableDictionary`
- 5.1.4 (6484 rows): `queryDictionary,queryInterface,sqlTree,substitutionsMap,symbolTable,tableDictionary`
- 5.1.4 (65 rows): `errors,fatalErrorCount,hasFatalErrors,messages,ok,rmcpParserMeta,transport`

## Samples

### `CANDIDATE_SLOWER`

- row 1 (SQL, lenient: match) `Acquia/ACS Contact/blank acs contact entity` — 183ms->249ms
- row 4 (SQL, lenient: mixed) `Acquia/ACS Contact/Regis_Catalyst_Test` — 83ms->148ms
- row 14 (SQL, lenient: mixed) `Acquia/exp__acquia_deletions/exp__acquia_gdpr_hard_delete_requests` — 164ms->321ms
- row 15 (SQL, lenient: mixed) `Acquia/exp__acquia_gdpr_soft_delete_requests/pdp_acquia_export_gdpr_soft_deletes` — 240ms->352ms
- row 67 (SQL, lenient: mixed) `Acquia_ALR/exp__acquia_gdpr_soft_delete_requests/pdp_alr_acquia_export_gdpr_soft_deletes` — 138ms->225ms
- row 68 (SQL, lenient: match) `Acquia_ALR/pdp_acquia_contact_export/speed_to_lead_contacts_export_pdp_alr_v2` — 72ms->124ms
- row 71 (SQL, lenient: mixed) `Acquia_AMS/exp__acquia_gdpr_soft_delete_requests/pdp_ams_acquia_export_gdpr_soft_deletes` — 136ms->220ms
- row 73 (SQL, lenient: match) `ALR/Applicant/Adelphi_Transformation_Query` — 753ms->1248ms

### `AST_NODE_KIND_ADDED`

- row 13 (SQL, lenient: improvement) `Acquia/exp__acquia_deletions/exp__acquia_deletions_pdp_ug_delivery` — cte
- row 14 (SQL, lenient: mixed) `Acquia/exp__acquia_deletions/exp__acquia_gdpr_hard_delete_requests` — cte; datetime_function; level; canonical; text; form
- row 15 (SQL, lenient: mixed) `Acquia/exp__acquia_gdpr_soft_delete_requests/pdp_acquia_export_gdpr_soft_deletes` — cte
- row 27 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/PDP_Acquia_Contacts` — cte
- row 65 (SQL, lenient: improvement) `Acquia_ALR/exp__acquia_deletions/exp__acquia_deletions_pdp_alr_v2_delivery` — cte
- row 66 (SQL, lenient: mixed) `Acquia_ALR/exp__acquia_deletions/exp__acquia_gdpr_hard_delete_requests` — cte; datetime_function; level; canonical; text; form
- row 67 (SQL, lenient: mixed) `Acquia_ALR/exp__acquia_gdpr_soft_delete_requests/pdp_alr_acquia_export_gdpr_soft_deletes` — cte
- row 69 (SQL, lenient: improvement) `Acquia_AMS/exp__acquia_deletions/exp__acquia_deletions_pdp_ams_delivery` — cte

### `AST_NODE_KIND_LOST`

- row 13 (SQL, lenient: improvement) `Acquia/exp__acquia_deletions/exp__acquia_deletions_pdp_ug_delivery` — last_delivered_cte
- row 14 (SQL, lenient: mixed) `Acquia/exp__acquia_deletions/exp__acquia_gdpr_hard_delete_requests` — acs_contacts; tenant_guid_cte
- row 15 (SQL, lenient: mixed) `Acquia/exp__acquia_gdpr_soft_delete_requests/pdp_acquia_export_gdpr_soft_deletes` — rec_added_modified_dt_cte; acs_contacts; replication_dt; tenant_guid_cte
- row 27 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/PDP_Acquia_Contacts` — results
- row 65 (SQL, lenient: improvement) `Acquia_ALR/exp__acquia_deletions/exp__acquia_deletions_pdp_alr_v2_delivery` — last_delivered_cte
- row 66 (SQL, lenient: mixed) `Acquia_ALR/exp__acquia_deletions/exp__acquia_gdpr_hard_delete_requests` — acs_contacts
- row 67 (SQL, lenient: mixed) `Acquia_ALR/exp__acquia_gdpr_soft_delete_requests/pdp_alr_acquia_export_gdpr_soft_deletes` — acs_contacts
- row 69 (SQL, lenient: improvement) `Acquia_AMS/exp__acquia_deletions/exp__acquia_deletions_pdp_ams_delivery` — last_delivered_cte

### `TABLE_KEY_LOST`

- row 13 (SQL, lenient: improvement) `Acquia/exp__acquia_deletions/exp__acquia_deletions_pdp_ug_delivery` — last_delivered_cte
- row 14 (SQL, lenient: mixed) `Acquia/exp__acquia_deletions/exp__acquia_gdpr_hard_delete_requests` — acs_contacts; tenant_guid_cte
- row 15 (SQL, lenient: mixed) `Acquia/exp__acquia_gdpr_soft_delete_requests/pdp_acquia_export_gdpr_soft_deletes` — rec_added_modified_dt_cte; acs_contacts; replication_dt; tenant_guid_cte
- row 16 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/ACU Export 11_08` — ac; con
- row 17 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/ACU_Tenant_Export_11_01` — ac
- row 18 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/Creighton_Export_9_29_2` — ac
- row 19 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/Creighton_Onetime_Export_9_17` — ac
- row 20 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/Export_HBU_Academic_interest_changes` — ac; con; scca

### `CANDIDATE_WARNING_NEW`

- row 4 (SQL, lenient: mixed) `Acquia/ACS Contact/Regis_Catalyst_Test` — WARNING\|STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR\|snowflake_quoted_numeric_identifier at (l:# c:#) is recognized as Snowflake-specific grammar.
- row 14 (SQL, lenient: mixed) `Acquia/exp__acquia_deletions/exp__acquia_gdpr_hard_delete_requests` — WARNING\|STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR\|snowflake_datetime_diff_expression at (l:# c:#) is recognized as Snowflake-specific grammar.
- row 16 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/ACU Export 11_08` — WARNING\|UNRESOLVED_UNQUALIFIED_COLUMNS\|Unresolved unqualified column reference(s): [contact_key [(l:# c:#)], intake_type_label [(l:# c:#), (l:# c:#)]]; WARNING\|UNRESOLVED_UNQUALIFIED_COLUMNS\|Unresolved unqualified column
- row 17 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/ACU_Tenant_Export_11_01` — WARNING\|UNRESOLVED_UNQUALIFIED_COLUMNS\|Unresolved unqualified column reference(s): [contact_key [(l:# c:#)], intake_type_label [(l:# c:#), (l:# c:#)]]; WARNING\|UNRESOLVED_UNQUALIFIED_COLUMNS\|Unresolved unqualified column
- row 18 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/Creighton_Export_9_29_2` — WARNING\|UNRESOLVED_UNQUALIFIED_COLUMNS\|Unresolved unqualified column reference(s): [contact_key [(l:# c:#)], intake_type_label [(l:# c:#), (l:# c:#)]]; WARNING\|UNRESOLVED_UNQUALIFIED_COLUMNS\|Unresolved unqualified column
- row 19 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/Creighton_Onetime_Export_9_17` — WARNING\|UNRESOLVED_UNQUALIFIED_COLUMNS\|Unresolved unqualified column reference(s): [contact_key [(l:# c:#)], intake_type_label [(l:# c:#), (l:# c:#)]]; WARNING\|UNRESOLVED_UNQUALIFIED_COLUMNS\|Unresolved unqualified column
- row 20 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/Export_HBU_Academic_interest_changes` — WARNING\|UNRESOLVED_UNQUALIFIED_COLUMNS\|Unresolved unqualified column reference(s): [contact_key [(l:# c:#)], intake_type_label [(l:# c:#), (l:# c:#)]]; WARNING\|UNRESOLVED_UNQUALIFIED_COLUMNS\|Unresolved unqualified column
- row 21 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/Final_Changes_PDP_Export_sy` — WARNING\|UNRESOLVED_UNQUALIFIED_COLUMNS\|Unresolved unqualified column reference(s): [contact_key [(l:# c:#)], intake_type_label [(l:# c:#), (l:# c:#)]]; WARNING\|UNRESOLVED_UNQUALIFIED_COLUMNS\|Unresolved unqualified column

### `TABLE_COLUMN_ADDED`

- row 16 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/ACU Export 11_08` — pdp.acs__contacts.st_dt_cul_stu_inq_acsl; pdp.acs__contacts.st_dt_cul_stu_paper_acsl; pdp.acs__contacts.eng_app_stu_paper_acsl; pdp.acs__contacts.st_dt_cul_par_nur_acsl; pdp.acs__contacts.st_dt_cul_stu_reen_acsl; pdp.acs
- row 17 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/ACU_Tenant_Export_11_01` — pdp.acs__contacts.st_dt_cul_stu_inq_acsl; pdp.acs__contacts.st_dt_cul_stu_paper_acsl; pdp.acs__contacts.eng_app_stu_paper_acsl; pdp.acs__contacts.st_dt_cul_par_nur_acsl; pdp.acs__contacts.st_dt_cul_stu_reen_acsl; pdp.acs
- row 18 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/Creighton_Export_9_29_2` — pdp.acs__contacts.st_dt_cul_stu_inq_acsl; pdp.acs__contacts.st_dt_cul_stu_paper_acsl; pdp.acs__contacts.eng_app_stu_paper_acsl; pdp.acs__contacts.st_dt_cul_par_nur_acsl; pdp.acs__contacts.st_dt_cul_stu_reen_acsl; pdp.acs
- row 19 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/Creighton_Onetime_Export_9_17` — pdp.acs__contacts.st_dt_cul_stu_inq_acsl; pdp.acs__contacts.st_dt_cul_stu_paper_acsl; pdp.acs__contacts.eng_app_stu_paper_acsl; pdp.acs__contacts.st_dt_cul_par_nur_acsl; pdp.acs__contacts.st_dt_cul_stu_reen_acsl; pdp.acs
- row 20 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/Export_HBU_Academic_interest_changes` — pdp.acs__contacts.st_dt_cul_stu_inq_acsl; pdp.acs__contacts.st_dt_cul_stu_paper_acsl; pdp.acs__contacts.eng_app_stu_paper_acsl; pdp.acs__contacts.st_dt_cul_par_nur_acsl; pdp.acs__contacts.st_dt_cul_stu_reen_acsl; pdp.acs
- row 21 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/Final_Changes_PDP_Export_sy` — pdp.acs__contacts.st_dt_cul_stu_inq_acsl; pdp.acs__contacts.st_dt_cul_stu_paper_acsl; pdp.acs__contacts.eng_app_stu_paper_acsl; pdp.acs__contacts.st_dt_cul_par_nur_acsl; pdp.acs__contacts.st_dt_cul_stu_reen_acsl; pdp.acs
- row 22 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/Final_Changes_PDP_Export_sy` — pdp.acs__contacts.st_dt_cul_stu_inq_acsl; pdp.acs__contacts.st_dt_cul_stu_paper_acsl; pdp.acs__contacts.eng_app_stu_paper_acsl; pdp.acs__contacts.st_dt_cul_par_nur_acsl; pdp.acs__contacts.st_dt_cul_stu_reen_acsl; pdp.acs
- row 23 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/hbu_09_17_PDP_Acquia_Export_PDPv0.2.0` — pdp.acs__contacts.st_dt_cul_stu_inq_acsl; pdp.acs__contacts.st_dt_cul_stu_paper_acsl; pdp.acs__contacts.eng_app_stu_paper_acsl; pdp.acs__contacts.st_dt_cul_par_nur_acsl; pdp.acs__contacts.st_dt_cul_stu_reen_acsl; pdp.acs

### `TABLE_KEY_QUALIFICATION_CHANGED`

- row 13 (SQL, lenient: improvement) `Acquia/exp__acquia_deletions/exp__acquia_deletions_pdp_ug_delivery` — log__acs_contact_deletions->pdp_ug.log__acs_contact_deletions
- row 14 (SQL, lenient: mixed) `Acquia/exp__acquia_deletions/exp__acquia_gdpr_hard_delete_requests` — ref__deletion_requests->es_pdp_common.common.ref__deletion_requests
- row 15 (SQL, lenient: mixed) `Acquia/exp__acquia_gdpr_soft_delete_requests/pdp_acquia_export_gdpr_soft_deletes` — ref__deletion_requests->es_pdp_common.common.ref__deletion_requests
- row 16 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/ACU Export 11_08` — common_source_standard_mappings->es_pdp_common.common.common_source_standard_mappings; con__contact_assigned->pdp.con__contact_assigned; con__contact_attributes_combined->pdp.con__contact_attributes_combined; prc__partne
- row 17 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/ACU_Tenant_Export_11_01` — common_source_standard_mappings->es_pdp_common.common.common_source_standard_mappings; con__contact_assigned->pdp.con__contact_assigned; con__contact_attributes_combined->pdp.con__contact_attributes_combined; prc__partne
- row 18 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/Creighton_Export_9_29_2` — common_source_standard_mappings->es_pdp_common.common.common_source_standard_mappings; con__contact_assigned->pdp.con__contact_assigned; con__contact_attributes_combined->pdp.con__contact_attributes_combined; prc__partne
- row 19 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/Creighton_Onetime_Export_9_17` — common_source_standard_mappings->es_pdp_common.common.common_source_standard_mappings; con__contact_assigned->pdp.con__contact_assigned; con__contact_attributes_combined->pdp.con__contact_attributes_combined; prc__partne
- row 20 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/Export_HBU_Academic_interest_changes` — common_source_standard_mappings->es_pdp_common.common.common_source_standard_mappings; con__contact_assigned->pdp.con__contact_assigned; con__contact_attributes_combined->pdp.con__contact_attributes_combined; prc__partne

### `CANDIDATE_SEVERE_NEW`

- row 16 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/ACU Export 11_08` — SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'intake_type_label' at (l:# c:#). Possible sources: [pdp.crf__contact_sourcecontacts, pdp.crf__intake_type]; SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|
- row 17 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/ACU_Tenant_Export_11_01` — SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'intake_type_label' at (l:# c:#). Possible sources: [pdp.crf__contact_sourcecontacts, pdp.crf__intake_type]; SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|
- row 18 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/Creighton_Export_9_29_2` — SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'intake_type_label' at (l:# c:#). Possible sources: [pdp.crf__contact_sourcecontacts, pdp.crf__intake_type]; SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|
- row 19 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/Creighton_Onetime_Export_9_17` — SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'intake_type_label' at (l:# c:#). Possible sources: [pdp.crf__contact_sourcecontacts, pdp.crf__intake_type]; SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|
- row 20 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/Export_HBU_Academic_interest_changes` — SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'intake_type_label' at (l:# c:#). Possible sources: [pdp.crf__contact_sourcecontacts, pdp.crf__intake_type]; SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|
- row 21 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/Final_Changes_PDP_Export_sy` — SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'intake_type_label' at (l:# c:#). Possible sources: [pdp.crf__contact_sourcecontacts, pdp.crf__intake_type]; SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|
- row 22 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/Final_Changes_PDP_Export_sy` — SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'intake_type_label' at (l:# c:#). Possible sources: [pdp.crf__contact_sourcecontacts, pdp.crf__intake_type]; SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|
- row 23 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/hbu_09_17_PDP_Acquia_Export_PDPv0.2.0` — SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'intake_type_label' at (l:# c:#). Possible sources: [pdp.crf__contact_sourcecontacts, pdp.crf__intake_type]; SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|

### `TABLE_COLUMN_LOST`

- row 76 (SQL, lenient: match) `ALR/Applicant/Albertus Magnus Transform` — <[ALR].[Applicant]>.current_date
- row 87 (SQL, lenient: mixed) `ALR/Applicant/fairleigh_dickinson_transformation_query_applicants` — <[ALR].[Applicant]>.year; <[ALR].[Applicant]>.COUNTRY_NAME; <[ALR].[Applicant]>.CURRENT_TIMESTAMP
- row 90 (SQL, lenient: mixed) `ALR/Applicant/transformation_query_applicant` — <[ALR].[Applicant]>.year; <[ALR].[Applicant]>.day; <[ALR].[Applicant]>.month
- row 91 (SQL, lenient: mixed) `ALR/Applicant/transformation_query_applicant2` — <[ALR].[Applicant]>.year; <[ALR].[Applicant]>.day; <[ALR].[Applicant]>.month
- row 97 (SQL, lenient: mixed) `ALR/Applicant/transformation_query_applicants` — <[ALR].[Applicant]>.day; <[ALR].[Applicant]>.month
- row 114 (SQL, lenient: mixed) `ALR/Applicant/transformation_query_applicants` — <[ALR].[Inquiry]>.year
- row 124 (SQL, lenient: mixed) `ALR/Applicant/transformation_query_applicants` — <[ALR].[Applicant]>.year
- row 138 (SQL, lenient: mixed) `ALR/Applicant/transformation_query_applicants` — <[ALR].[Applicant]>."dd"

### `TABLE_COLUMN_CASE_CHANGED`

- row 4 (SQL, lenient: mixed) `Acquia/ACS Contact/Regis_Catalyst_Test` — "53591_346912".details__stat__dateRead->details__stat__dateread
- row 20 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/Export_HBU_Academic_interest_changes` — pdp.add__sourcecontact_clean_addr.ADDR_2->addr_2
- row 26 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/LeMoyne_Exp_11_01` — pdp.add__sourcecontact_clean_addr.ADDR_2->addr_2
- row 37 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/PDP_Acquia_Export_PDPv0.2.0` — pdp.add__sourcecontact_clean_addr.ADDR_2->addr_2
- row 40 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/PDP_Acquia_Export_PDPv0.2.0_FC_TENANT` — pdp.add__sourcecontact_clean_addr.ADDR_2->addr_2
- row 42 (SQL, lenient: improvement) `Acquia/PDP_Acquia_Contact/Regis_PDP_Acquia_Export_PDPv0_12_01` — pdp.add__sourcecontact_clean_addr.ADDR_2->addr_2
- row 75 (SQL, lenient: match) `ALR/Applicant/Albertus Magnus College Graduate Programs Transform` — <[ALR].[Applicant]>.STUDENT_TYPE_SOURCE_VALUE->student_type_source_value
- row 79 (SQL, lenient: match) `ALR/Applicant/Applicant.Transformation` — <[ALR].[Applicant]>.country_name->COUNTRY_NAME

### `CANDIDATE_FATAL_NEW`

- row 32 (SQL, lenient: mixed) `Acquia/PDP_Acquia_Contact/PDP_Acquia_Export` — FATAL\|QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE\|Source Table not found for Column 'acs_contact_id' at (l:# c:#). No alias or table called 'CAC'.; FATAL\|QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE\|Source Table not found for Column '<Pr
- row 138 (SQL, lenient: mixed) `ALR/Applicant/transformation_query_applicants` — FATAL\|INVALID_DATETIME_PART\|Invalid date/time part "dd" for DATEDIFF at (l:# c:#).; FATAL\|INVALID_DATETIME_PART\|Invalid date/time part "dd" for DATEDIFF at (l:# c:#).
- row 193 (SQL, lenient: mixed) `ALR/ApplicantStarters/transformation_query_applicationstarters` — FATAL\|INVALID_DATETIME_PART\|Invalid date/time part "dd" for DATEDIFF at (l:# c:#).
- row 306 (SQL, lenient: mixed) `ALR/Inquiry/transformation_query_inquiry` — FATAL\|INVALID_DATETIME_PART\|Invalid date/time part "dd" for DATEDIFF at (l:# c:#).; FATAL\|INVALID_DATETIME_PART\|Invalid date/time part "dd" for DATEDIFF at (l:# c:#).
- row 325 (SQL, lenient: mixed) `ALR/Inquiry/transformation_query_inquiry` — FATAL\|INVALID_DATETIME_PART\|Invalid date/time part "dd" for DATEDIFF at (l:# c:#).
- row 333 (SQL, lenient: mixed) `ALR/Inquiry/transformation_query_inquiry` — FATAL\|INVALID_DATETIME_PART\|Invalid date/time part "dd" for DATEDIFF at (l:# c:#).; FATAL\|INVALID_DATETIME_PART\|Invalid date/time part "dd" for DATEDIFF at (l:# c:#).
- row 346 (SQL, lenient: mixed) `ALR/MiscellaneousOne/transformation_query_inquirybms` — FATAL\|INVALID_DATETIME_PART\|Invalid date/time part "dd" for DATEDIFF at (l:# c:#).; FATAL\|INVALID_DATETIME_PART\|Invalid date/time part "dd" for DATEDIFF at (l:# c:#).
- row 347 (SQL, lenient: mixed) `ALR/MiscellaneousOne/transformation_query_inquirybms` — FATAL\|INVALID_DATETIME_PART\|Invalid date/time part "dd" for DATEDIFF at (l:# c:#).; FATAL\|INVALID_DATETIME_PART\|Invalid date/time part "dd" for DATEDIFF at (l:# c:#).

### `BASELINE_DIAGNOSTIC_DROPPED`

- row 469 (SQL, lenient: mixed) `ALR/Partner Code Mapping/Inquiry_Investigation` — FATAL\|\|Invalid syntax near 'select'
- row 498 (SQL, lenient: mixed) `ALR/Partner Code Mapping/Unmapped` — FATAL\|\|Invalid syntax near 'select'
- row 616 (SQL, lenient: mixed) `ALR/Student Address/ALR.Student Address.final_with_intake_dt` — FATAL\|\|Invalid syntax near 'case'; FATAL\|\|Invalid syntax near 'case'
- row 688 (SQL, lenient: mixed) `ALR/Student Phone/ALR.Student Phone.final` — FATAL\|\|Invalid syntax near '.'
- row 764 (SQL, lenient: improvement) `ALR/Student Program/ALR.Audience.Code.EAB.Translation.Comparison` — FATAL\|\|Invalid syntax near 'select'
- row 765 (SQL, lenient: improvement) `ALR/Student Program/ALR.Audience.Code.EAB.Translation.Comparison` — FATAL\|\|Invalid syntax near 'select'
- row 769 (SQL, lenient: improvement) `ALR/Student Program/ALR.Audience.Code.EAB.Translation.Comparison` — FATAL\|\|Invalid syntax near 'select'
- row 772 (SQL, lenient: improvement) `ALR/Student Program/ALR.Funnel.Status.EAB.Translation.Comparison` — FATAL\|\|Invalid syntax near 'from'

### `CANDIDATE_NULL_TREE`

- row 469 (SQL, lenient: mixed) `ALR/Partner Code Mapping/Inquiry_Investigation`
- row 498 (SQL, lenient: mixed) `ALR/Partner Code Mapping/Unmapped`
- row 1093 (SQL, lenient: mixed) `ALR/Universal/OUOnline.TestingRawData`
- row 1120 (SQL, lenient: mixed) `ALR/Universal/TestingRawData`
- row 1208 (SQL, lenient: error) `ALR/Universal/transformation_query_inquiries`
- row 1799 (SQL, lenient: mixed) `Enroll360/Partner Code Mapping/Enroll360.Partner Code Mapping.convert`
- row 1939 (SQL, lenient: mixed) `Enroll360/Partner Code Mapping/INACTIVE_Enroll360.Partner Code Mapping.convert`
- row 1999 (SQL, lenient: error) `Enroll360/Partner Code Mapping/INACTIVE_Enroll360.Partner Code Mapping.convert_ug_applicants`

### `FATAL_BOTH`

- row 469 (SQL, lenient: mixed) `ALR/Partner Code Mapping/Inquiry_Investigation`
- row 498 (SQL, lenient: mixed) `ALR/Partner Code Mapping/Unmapped`
- row 616 (SQL, lenient: mixed) `ALR/Student Address/ALR.Student Address.final_with_intake_dt`
- row 688 (SQL, lenient: mixed) `ALR/Student Phone/ALR.Student Phone.final`
- row 1093 (SQL, lenient: mixed) `ALR/Universal/OUOnline.TestingRawData`
- row 1120 (SQL, lenient: mixed) `ALR/Universal/TestingRawData`
- row 1317 (SQL, lenient: mixed) `ALR/YoungAlum/young_alumni`
- row 1799 (SQL, lenient: mixed) `Enroll360/Partner Code Mapping/Enroll360.Partner Code Mapping.convert`

### `INTERFACE_LENGTH_CHANGED`

- row 469 (SQL, lenient: mixed) `ALR/Partner Code Mapping/Inquiry_Investigation` — 6->0
- row 498 (SQL, lenient: mixed) `ALR/Partner Code Mapping/Unmapped` — 1->0
- row 1093 (SQL, lenient: mixed) `ALR/Universal/OUOnline.TestingRawData` — 1->0
- row 1120 (SQL, lenient: mixed) `ALR/Universal/TestingRawData` — 2->0
- row 1535 (SQL, lenient: mixed) `AMS/donor_email/par_intake_donor_email_AMS_V2.0` — 5->6
- row 1539 (SQL, lenient: improvement) `AMS/donor_email/SUNY_Buffalo_par_intake_donor_email_AMS_V2.0` — 4->6
- row 1799 (SQL, lenient: mixed) `Enroll360/Partner Code Mapping/Enroll360.Partner Code Mapping.convert` — 4->0
- row 1939 (SQL, lenient: mixed) `Enroll360/Partner Code Mapping/INACTIVE_Enroll360.Partner Code Mapping.convert` — 4->0

### `INTERFACE_COLUMN_LOST`

- row 469 (SQL, lenient: mixed) `ALR/Partner Code Mapping/Inquiry_Investigation` — inquiry_ind; unnamed_0; EAB_CURRENT_FUNNEL_STATUS; EAB_ENTRY_TERM; EAB_ENTRY_YEAR_ACADEMIC; EAB_INQUIRY_IND
- row 498 (SQL, lenient: mixed) `ALR/Partner Code Mapping/Unmapped` — *
- row 1093 (SQL, lenient: mixed) `ALR/Universal/OUOnline.TestingRawData` — *
- row 1120 (SQL, lenient: mixed) `ALR/Universal/TestingRawData` — unnamed_0; program
- row 1535 (SQL, lenient: mixed) `AMS/donor_email/par_intake_donor_email_AMS_V2.0` — donor_email_rank
- row 1799 (SQL, lenient: mixed) `Enroll360/Partner Code Mapping/Enroll360.Partner Code Mapping.convert` — record_count; partner_value; eab_standard_value; field_name
- row 1939 (SQL, lenient: mixed) `Enroll360/Partner Code Mapping/INACTIVE_Enroll360.Partner Code Mapping.convert` — record_count; partner_value; eab_standard_value; field_name
- row 2992 (SQL, lenient: mixed) `Enroll360/Student Race/imports_table_name` — student_type

### `SUBSTITUTION_LOST`

- row 469 (SQL, lenient: mixed) `ALR/Partner Code Mapping/Inquiry_Investigation` — <[ALR].[Student Program].{final}>
- row 498 (SQL, lenient: mixed) `ALR/Partner Code Mapping/Unmapped` — <[ALR].[Partner Code Mapping].{convert}>
- row 1093 (SQL, lenient: mixed) `ALR/Universal/OUOnline.TestingRawData` — <[ALR].[Student Term Application].{final}>
- row 1120 (SQL, lenient: mixed) `ALR/Universal/TestingRawData` — <[ALR].[Student Program].{fulfillment}>
- row 1553 (SQL, lenient: mixed) `AMS/donor_phone/par_intake_donor_phone_AMS_V2.0` — <join_extension_donor_phone>
- row 1799 (SQL, lenient: mixed) `Enroll360/Partner Code Mapping/Enroll360.Partner Code Mapping.convert` — <[Enroll360].[Student].{fulfillment}>; <eab_phone_type>; <eab_mail_optout_ind>; <[Enroll360].[Student Term Application].{fulfillment}>; <eab_pref_gender>; <[Enroll360].[Student Email].{fulfillment}>; <eab_FAFSA_submitted
- row 1939 (SQL, lenient: mixed) `Enroll360/Partner Code Mapping/INACTIVE_Enroll360.Partner Code Mapping.convert` — <[Enroll360].[Student].{fulfillment}>; <eab_race_unknown>; <eab_race_asian>; <eab_pref_gender>; <eab_full_part_time>; <eab_admit_ind>; <eab_race_amer_indian>; <eab_sms_optin_ind>; <eab_cond_admit_ind>; <eab_race_middle_e
- row 3332 (SQL, lenient: mixed) `Enroll360/Student Term/WW.TestingRawData_student_term` — <[Enroll360].[Student Term Application].{fulfillment}>

### `BASELINE_ONLY_FAILED`

- row 1117 (SQL, lenient: error) `ALR/Universal/test 1` — PssSqlParserAccessServer non-JSON stdout: Exception: [Line 2:24 - null - unexpected input: '<EOF>', Line 2:24 - Syntax error, attempting recovery, Exception when walking the parse tree: Cannot invoke 
- row 1208 (SQL, lenient: error) `ALR/Universal/transformation_query_inquiries` — PssSqlParserAccessServer non-JSON stdout: Exception: [Line 212:18 - null - unexpected input: '*', Line 212:18 - Syntax error, attempting recovery, Line 229:0 - Invalid syntax near ')', Exception when 
- row 1999 (SQL, lenient: error) `Enroll360/Partner Code Mapping/INACTIVE_Enroll360.Partner Code Mapping.convert_ug_applicants` — PssSqlParserAccessServer non-JSON stdout: Exception: [Line 357:9 - null - unexpected input: '*', Line 357:9 - Syntax error, attempting recovery, Line 357:10 - Invalid syntax near '/', Line 357:10 - In
- row 2034 (SQL, lenient: error) `Enroll360/Partner Configurations/Atlas.MigrationFeedTables` — PssSqlParserAccessServer non-JSON stdout: Exception: [Line 52:93 - null - unexpected input: '<EOF>', Line 52:93 - Syntax error, attempting recovery, Exception when walking the parse tree: Cannot invok
- row 2035 (SQL, lenient: error) `Enroll360/Partner Configurations/Atlas.MigrationFeedTables (updated)` — PssSqlParserAccessServer non-JSON stdout: Exception: [Line 52:93 - null - unexpected input: '<EOF>', Line 52:93 - Syntax error, attempting recovery, Exception when walking the parse tree: Cannot invok
- row 2460 (SQL, lenient: error) `Enroll360/Student Address/INACTIVE_Enroll360.Student Address.final_v2` — PssSqlParserAccessServer non-JSON stdout: Exception: [Line 289:0 - null - unexpected input: ',', Line 289:0 - Syntax error, attempting recovery, Line 289:2 - Invalid syntax near 'ADDR1', Exception whe
- row 2546 (SQL, lenient: error) `Enroll360/Student Email/INACTIVE_Enroll360.Student Email.final` — PssSqlParserAccessServer non-JSON stdout: Exception: [Line 2:0 - null - unexpected input: 'from', Line 2:0 - Syntax error, attempting recovery, Line 2:5 - Invalid syntax near '<[Enroll360].[Student Em
- row 2635 (SQL, lenient: error) `Enroll360/Student Events/INACTIVE_Enroll360.Student Events.final` — PssSqlParserAccessServer non-JSON stdout: Exception: [Line 2:0 - null - unexpected input: 'from', Line 2:0 - Syntax error, attempting recovery, Line 2:5 - Invalid syntax near '<[Enroll360].[Student Ev

### `BASELINE_FATAL_ONLY`

- row 764 (SQL, lenient: improvement) `ALR/Student Program/ALR.Audience.Code.EAB.Translation.Comparison` — FATAL\|\|Invalid syntax near 'select'
- row 765 (SQL, lenient: improvement) `ALR/Student Program/ALR.Audience.Code.EAB.Translation.Comparison` — FATAL\|\|Invalid syntax near 'select'
- row 769 (SQL, lenient: improvement) `ALR/Student Program/ALR.Audience.Code.EAB.Translation.Comparison` — FATAL\|\|Invalid syntax near 'select'
- row 772 (SQL, lenient: improvement) `ALR/Student Program/ALR.Funnel.Status.EAB.Translation.Comparison` — FATAL\|\|Invalid syntax near 'from'
- row 851 (SQL, lenient: improvement) `ALR/Student Program/GoshenGrad.ALR.Audience.Code.EAB.Translation.Comparison` — FATAL\|\|Invalid syntax near 'select'
- row 852 (SQL, lenient: improvement) `ALR/Student Program/Northwestern_Law_GMAT_GRE_Partner_Records` — FATAL\|\|Invalid syntax near 'select'
- row 1027 (SQL, lenient: improvement) `ALR/Universal/Admit_Ind_Missing_Admit_Dt_Validation` — FATAL\|\|Invalid syntax near '('
- row 1028 (SQL, lenient: improvement) `ALR/Universal/BSU_Grad_Data_Review` — FATAL\|\|Invalid syntax near 'select'

### `INTERFACE_COLUMN_ADDED`

- row 1535 (SQL, lenient: mixed) `AMS/donor_email/par_intake_donor_email_AMS_V2.0` — source_partner_system_name; source_eab_system_type
- row 1539 (SQL, lenient: improvement) `AMS/donor_email/SUNY_Buffalo_par_intake_donor_email_AMS_V2.0` — source_partner_system_name; source_eab_system_type

