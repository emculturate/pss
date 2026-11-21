# RMCP strict dual-parser assessment — snippets

- Generated: 2026-11-19T22:38:12.788Z
- Raw capture: `rmcp/tmp/strict_raw/live_snippets_11-19.raw.jsonl.gz`
- Versions: **5.0.0-3** vs **5.1.4**
- Rows assessed: **88046**
- Strict verdicts: clean **43193**, contract_change **5687**, review **39166**
- Blocking tags (drive `review`): `CANDIDATE_ONLY_FAILED`, `CANDIDATE_NULL_TREE`, `CANDIDATE_FATAL_NEW`, `CANDIDATE_SEVERE_NEW`, `TABLE_KEY_LOST`, `TABLE_COLUMN_LOST`, `SUBSTITUTION_LOST`, `SUBSTITUTION_TYPE_CHANGED`, `INTERFACE_COLUMN_LOST`, `INTERFACE_LENGTH_CHANGED`

## Blocking findings in rows the lenient compare passed

Rows the lenient compare scored as match, essentialMatch, or improvement that carry a blocking strict finding.

| Tag | Rows |
|---|---:|
| `TABLE_KEY_LOST` | 34584 |
| `INTERFACE_LENGTH_CHANGED` | 4536 |
| `TABLE_COLUMN_LOST` | 43 |
| `CANDIDATE_SEVERE_NEW` | 32 |
| `CANDIDATE_FATAL_NEW` | 25 |
| `INTERFACE_COLUMN_LOST` | 1 |

## Findings by tag

| Tag | Rows | By group | By lenient verdict |
|---|---:|---|---|
| `TABLE_KEY_LOST` ⚑ | 34594 | predicand: 30416, tuple: 2577, column: 989, condition: 431, join_extension: 134, in_list: 38, query: 9 | essentialMatch: 34132, match: 452, mixed: 7, degradation: 3 |
| `TABLE_KEY_LOST_UNKNOWN` | 4699 | predicand: 3769, condition: 516, column: 410, in_list: 4 | match: 4623, mixed: 49, essentialMatch: 27 |
| `INTERFACE_COLUMN_ADDED` | 4545 | tuple: 4484, join_extension: 54, predicand: 6, condition: 1 | essentialMatch: 4532, mixed: 8, improvement: 5 |
| `INTERFACE_LENGTH_CHANGED` ⚑ | 4544 | tuple: 4484, join_extension: 53, predicand: 6, condition: 1 | essentialMatch: 4531, mixed: 8, improvement: 5 |
| `CANDIDATE_SLOWER` | 1380 | predicand: 802, tuple: 434, query: 98, condition: 25, join_extension: 21 | match: 1058, essentialMatch: 273, mixed: 48, degradation: 1 |
| `TABLE_COLUMN_ADDED` | 646 | tuple: 625, join_extension: 9, query: 7, predicand: 5 | essentialMatch: 640, mixed: 5, improvement: 1 |
| `CANDIDATE_WARNING_NEW` | 269 | predicand: 145, tuple: 94, query: 15, join_extension: 11, condition: 3, in_list: 1 | essentialMatch: 142, mixed: 127 |
| `AST_NODE_KIND_ADDED` | 132 | predicand: 118, tuple: 11, condition: 3 | mixed: 132 |
| `TABLE_KEY_QUALIFICATION_CHANGED` | 87 | tuple: 76, query: 6, join_extension: 3, predicand: 2 | essentialMatch: 69, match: 18 |
| `TABLE_COLUMN_LOST` ⚑ | 47 | tuple: 35, query: 9, predicand: 2, join_extension: 1 | essentialMatch: 41, mixed: 3, match: 2, degradation: 1 |
| `BASELINE_DIAGNOSTIC_DROPPED` | 33 | tuple: 32, predicand: 1 | essentialMatch: 32, mixed: 1 |
| `CANDIDATE_SEVERE_NEW` ⚑ | 32 | tuple: 16, query: 10, join_extension: 5, predicand: 1 | essentialMatch: 32 |
| `CANDIDATE_FATAL_NEW` ⚑ | 29 | tuple: 24, query: 3, in_list: 1, predicand: 1 | essentialMatch: 25, error: 3, mixed: 1 |
| `SUBSTITUTION_TYPE_CHANGED` ⚑ | 28 | predicand: 28 | neutral: 21, degradation: 4, mixed: 3 |
| `BASELINE_FATAL_ONLY` | 28 | tuple: 28 | essentialMatch: 28 |
| `AST_NODE_KIND_LOST` | 21 | predicand: 16, tuple: 5 | mixed: 21 |
| `BASELINE_ONLY_FAILED` | 14 | tuple: 8, predicand: 6 | error: 14 |
| `TABLE_COLUMN_CASE_CHANGED` | 7 | query: 4, tuple: 3 | match: 4, essentialMatch: 3 |
| `FATAL_BOTH` | 5 | tuple: 4, predicand: 1 | essentialMatch: 4, mixed: 1 |
| `CANDIDATE_NULL_TREE` ⚑ | 4 | predicand: 2, in_list: 1, tuple: 1 | mixed: 2, error: 2 |
| `TABLE_KEY_ADDED` | 2 | tuple: 2 | essentialMatch: 2 |
| `INTERFACE_COLUMN_LOST` ⚑ | 1 | join_extension: 1 | essentialMatch: 1 |
| `SUBSTITUTION_LOST` ⚑ | 1 | predicand: 1 | mixed: 1 |
| `TABLE_KEY_CASE_CHANGED` | 1 | tuple: 1 | essentialMatch: 1 |

## New 5.1.4 FATAL signatures

| Signature (severity \| code \| message) | Rows |
|---|---:|
| FATAL\|UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES\|Unqualified column 'primary_student_id' at (l:# c:#) was not found in output interface of any visible query alias [cte]. | 2 |
| FATAL\|UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES\|Unqualified column 'REGISTERED_IND' at (l:# c:#) was not found in output interface of any visible query alias [ex_ex__#_#]. | 2 |
| FATAL\|UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES\|Unqualified column 'transfer_course_ind' at (l:# c:#) was not found in output interface of any visible query alias [so_sr_ey_#]. | 2 |
| FATAL\|UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES\|Unqualified column 'registered_ind' at (l:# c:#) was not found in output interface of any visible query alias [so_sr_up_#]. | 2 |
| FATAL\|UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES\|Unqualified column 'academic_period' at (l:# c:#) was not found in output interface of any visible query alias [so_sr_up_#]. | 2 |
| FATAL\|UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES\|Unqualified column 'in_grad_lkp' at (l:# c:#) was not found in output interface of any visible query alias [so_sr_up_#]. | 2 |
| FATAL\|REPORT_ERROR\|unexpected input: ']' in rule row_value_expression: 'American Indian or Alaska Native, Asian',] | 1 |
| FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: target_partnercontact_id at (l:# c:#) and target_partnercontact_id at (l:# c:#). | 1 |
| FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: target_partner_system_name at (l:# c:#) and target_partner_system_name at (l:# c:#). | 1 |
| FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: source_partnercontact_id at (l:# c:#) and source_partnercontact_id at (l:# c:#). | 1 |
| FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: source_partner_system_name at (l:# c:#) and source_partner_system_name at (l:# c:#). | 1 |
| FATAL\|SET_OPERATION_INTERFACE_COLUMN_COUNT_MISMATCH\|UNION has different column counts. Expected # columns (source_partnercontact_id, source_partner_system_name, target_partnercontact_id, target_partner_system_nam | 1 |
| FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: <Lookup Procedure_lkp_academic_period.ACADEMIC_PERIOD#> at (l:# c:#) and <Lookup Procedure_lkp_academic_period.ACADEMIC_PER | 1 |
| FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: <lookup procedure_lkp_active_courses_course_type_lkp.in_course_cd> at (l:# c:#) and <lookup procedure_lkp_active_courses_co | 1 |
| FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: <lookup procedure_lkp_apl_term_eff_dt.in_term_id_eff> at (l:# c:#) and <lookup procedure_lkp_apl_term_eff_dt.in_term_id_eff | 1 |
| FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: <lookup procedure_lkp_courses#.course_cd#> at (l:# c:#) and <lookup procedure_lkp_courses#.course_cd#> at (l:# c:#). | 1 |
| FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: <lookup procedure_lkp_degree_college_cd#.major_cd_#> at (l:# c:#) and <lookup procedure_lkp_degree_college_cd#.major_cd_#>  | 1 |
| FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: <Lookup Procedure_lkp_reg_start_end.ACADEMIC_PERIOD#> at (l:# c:#) and <Lookup Procedure_lkp_reg_start_end.ACADEMIC_PERIOD_ | 1 |
| FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: <Lookup Procedure_lkp_reg_start_end.ACADEMIC_PERIOD#> at (l:# c:#) and <Lookup Procedure_lkp_reg_start_end.ACADEMIC_PERIOD# | 1 |
| FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: <lookup procedure_lkp_reg_start_end.in_academic_period> at (l:# c:#) and <lookup procedure_lkp_reg_start_end.academic_perio | 1 |
| FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: <lookup procedure_lkp_reg_start_end.in_academic_period> at (l:# c:#) and <lookup procedure_lkp_reg_start_end.in_academic_pe | 1 |
| FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: <Lookup Procedure_LKP_SGL.FINAL_GRADE#> at (l:# c:#) and <Lookup Procedure_LKP_SGL.SHRGRDE_QUALITY_POINTS> at (l:# c:#). | 1 |
| FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: <Lookup Procedure_LKP_SGL.FINAL_GRADE#> at (l:# c:#) and <Lookup Procedure_LKP_SGL.FINAL_GRADE#> at (l:# c:#). | 1 |
| FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: <lookup procedure_unconnected_lkp_graduated_students.student_id#> at (l:# c:#) and <lookup procedure_unconnected_lkp_gradua | 1 |
| FATAL\|UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES\|Unqualified column 'REGISTERED_IND' at (l:# c:#) was not found in output interface of any visible query alias [ex_ex_#]. | 1 |

## New 5.1.4 ERROR / SEVERE_WARNING signatures

| Signature (severity \| code \| message) | Rows |
|---|---:|
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'SOURCE_CODE' at (l:# c:#). Possible sources: [<[Partner_Data_Platform].[College Board Contacts]>, query#] | 6 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'application_incomplete_indicator_eab' at (l:# c:#). Possible sources: [<[enrollment_services].[Partner Data Contact]>, <[enrollment_ | 4 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'eab_entry_year_academic' at (l:# c:#). Possible sources: [query#, query#] | 3 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'source_type' at (l:# c:#). Possible sources: [es_pdp_common.common.common_source_standard_mappings, <[Partner_Data_Platform].[Encour | 2 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'category' at (l:# c:#). Possible sources: [es_pdp_common.common.common_source_standard_mappings, <[Partner_Data_Platform].[Encoura C | 2 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'gift_id' at (l:# c:#). Possible sources: [<[AMS].[gifts].{fulfillment}>, <[AMS].[donor].{fulfillment}>] | 1 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'preferred_full_name' at (l:# c:#). Possible sources: [<[AMS].[donor_address].{fulfillment}>, <[AMS].[donor].{fulfillment}>] | 1 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'fund_type' at (l:# c:#). Possible sources: [<[Enroll#].[Student Year Funds].{fulfillment}>, <[Enroll#].[Partner Code Mapping].{conve | 1 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'calculated_field_#' at (l:# c:#). Possible sources: [<[Enroll#].[Student Year Funds].{fulfillment}>, <[Enroll#].[Partner Code Mappin | 1 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'CALCULATED_FIELD_#' at (l:# c:#). Possible sources: [<[Enroll#].[Student Year Funds].{fulfillment}>, <[Enroll#].[Partner Code Mappin | 1 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'FUND_TYPE' at (l:# c:#). Possible sources: [<[Enroll#].[Student Year Funds].{fulfillment}>, <[Enroll#].[Partner Code Mapping].{conve | 1 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'eab_fund_amount' at (l:# c:#). Possible sources: [<[Enroll#].[Student Year Funds].[Last Validated].{final}>, <cte#_pop_definition>] | 1 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'fund_type' at (l:# c:#). Possible sources: [<[Enroll#].[Student Year Funds].[Last Validated].{final}>, <cte#_pop_definition>] | 1 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'eab_entry_year_academic' at (l:# c:#). Possible sources: [<[Enroll#].[Student Year Funds].[Last Validated].{final}>, <cte#_pop_defin | 1 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'eab_full_part_time' at (l:# c:#). Possible sources: [<cte#_final_totals_sliced>, <cte#_metric_map>] | 1 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'slice' at (l:# c:#). Possible sources: [<cte#_final_totals_sliced>, <cte#_metric_map>] | 1 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'sort_order' at (l:# c:#). Possible sources: [<cte#_final_totals_sliced>, <cte#_metric_map>] | 1 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'category' at (l:# c:#). Possible sources: [<cte#_final_totals_sliced>, <cte#_metric_map>] | 1 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'institutional_gift_total' at (l:# c:#). Possible sources: [<[Enroll#].[Partner Configurations].{finaid_variable_gap}>, <[Enroll#].[P | 1 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'partnercontact_id' at (l:# c:#). Possible sources: [<[Enroll#].[Student Year Funds].[Last Validated].{final}>, <cte#_pop_definition> | 1 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'partner_system_name' at (l:# c:#). Possible sources: [<[Enroll#].[Student Year Funds].[Last Validated].{final}>, <cte#_pop_definitio | 1 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'configuration_type' at (l:# c:#). Possible sources: [<cte#_pop_definition>, <[Enroll#].[Partner Display Controls].[Last Validated].{ | 1 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'eab_exam_score' at (l:# c:#). Possible sources: [<[Enroll#].[Student Test Scores PDP Delivery].[Last Validated].{final}>, <cte#_acrk | 1 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'application_incomplete_indicator_eab' at (l:# c:#). Possible sources: [<[enrollment_services].[Partner Data Race and Ethnicity]>, <[ | 1 |
| SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'downfill_college_cd' at (l:# c:#). Possible sources: [aa#_student_term, aa#_final_code_#_values] | 1 |

## 5.0.0-3 diagnostics not reported by 5.1.4

| Signature (severity \| code \| message) | Rows |
|---|---:|
| FATAL\|\|Invalid syntax near '<EOF>' | 17 |
| FATAL\|\|Invalid syntax near ';' | 11 |
| FATAL\|\|Invalid syntax near '(' | 4 |
| FATAL\|\|Invalid syntax near '=' | 1 |

## AST node kinds

| Node kind | Rows lost | Rows added |
|---|---:|---:|
| `datetime_function` | 0 | 127 |
| `level` | 0 | 124 |
| `canonical` | 0 | 124 |
| `text` | 0 | 124 |
| `form` | 0 | 124 |
| `increment` | 0 | 122 |
| `value` | 0 | 121 |
| `function` | 10 | 1 |
| `parameters` | 10 | 1 |
| `lookup` | 7 | 0 |
| `Type` | 4 | 0 |
| `exists` | 0 | 4 |
| `part_form` | 0 | 3 |
| `part` | 0 | 3 |
| `source` | 0 | 3 |
| `function_name` | 0 | 1 |
| `operator` | 0 | 1 |

## Payload contract keys (parse object)

- 5.0.0-3 (88032 rows): `errors,fatalErrorCount,interface,messages,sqlTree,substitutions,symbolTable,tableDictionary`
- 5.1.4 (88042 rows): `queryDictionary,queryInterface,sqlTree,substitutionsMap,symbolTable,tableDictionary`
- 5.1.4 (4 rows): `errors,fatalErrorCount,hasFatalErrors,messages,ok,rmcpParserMeta,transport`

## Samples

### `TABLE_KEY_LOST`

- row 19 (column, lenient: essentialMatch) `Acquia_AMS/status_threshold_acquia_ams_acs__categories_title_characters_check:V0.1/column` — acq_ams_tbl
- row 32 (tuple, lenient: match) `Acquia_AMS/status_threshold_acquia_ams_acs__categories_title_characters_check:V0.1/tuple` — <[Acquia_AMS].[acs__categories].{fulfillment}>
- row 185 (condition, lenient: essentialMatch) `ALR/ECRM_to_Enroll360:01/condition` — inst
- row 186 (condition, lenient: essentialMatch) `ALR/ECRM_to_Enroll360:01/condition` — inst
- row 198 (condition, lenient: essentialMatch) `ALR/ECRM_to_ALR:01/condition` — app; appc
- row 199 (condition, lenient: essentialMatch) `ALR/ECRM_to_ALR:01/condition` — app; appc
- row 204 (condition, lenient: essentialMatch) `ALR/ECRM_to_ALR:01/condition` — rr
- row 205 (condition, lenient: essentialMatch) `ALR/ECRM_to_ALR:01/condition` — rr

### `TABLE_KEY_LOST_UNKNOWN`

- row 1 (column, lenient: match) `Acquia/Acquia all_sources_blnd null check:1\|Acquia all_sources_bnd null check:1/column`
- row 2 (column, lenient: match) `Acquia/Acquia duplicate email check:1\|Acquia duplicate email check bundle:1\|Acquia duplicate emial check:1/column`
- row 3 (column, lenient: match) `Acquia/Acquia funnel_stage null check:1/column`
- row 4 (column, lenient: match) `Acquia/Acquia hs_grade_level null check:1/column`
- row 5 (column, lenient: match) `Acquia/Acquia percentage check bundle:1/column`
- row 6 (column, lenient: match) `Acquia/Acquia address percentage check:1\|Acquia address percentage check bundle:1\|Acquia percentage check bundle:1/column`
- row 7 (column, lenient: match) `Acquia/Acquia Test Email check bundle:1\|Acquia test email percentage check:1/column`
- row 8 (column, lenient: match) `Acquia/Acquia birth_date percentage check:1\|Acquia birthdate percentage check bundle:1/column`

### `INTERFACE_COLUMN_ADDED`

- row 10 (tuple, lenient: essentialMatch) `Acquia/Acquia address percentage check:1\|Acquia address percentage check bundle:1\|Acquia percentage check bundle:1/tuple` — percent_address_null
- row 11 (tuple, lenient: essentialMatch) `Acquia/Acquia percentage check bundle:1/tuple` — Max_Null_Percent
- row 12 (tuple, lenient: essentialMatch) `Acquia/Acquia birth_date percentage check:1\|Acquia birthdate percentage check bundle:1/tuple` — stu_birthdate_null_percentage
- row 13 (tuple, lenient: essentialMatch) `Acquia/Acquia all_sources_blnd null check:1\|Acquia all_sources_bnd null check:1/tuple` — all_sources_blnd_null_count
- row 15 (tuple, lenient: essentialMatch) `Acquia/Acquia funnel_stage null check:1/tuple` — funnel_stage_null_count
- row 16 (tuple, lenient: essentialMatch) `Acquia/Acquia hs_grade_level null check:1/tuple` — gradelevel_percent_null
- row 17 (tuple, lenient: essentialMatch) `Acquia/Acquia not null check bundle:1\|Acquia status check:2/tuple` — total_null
- row 18 (tuple, lenient: essentialMatch) `Acquia/Acquia Test Email check bundle:1\|Acquia test email percentage check:1/tuple` — percentTestMail

### `INTERFACE_LENGTH_CHANGED`

- row 10 (tuple, lenient: essentialMatch) `Acquia/Acquia address percentage check:1\|Acquia address percentage check bundle:1\|Acquia percentage check bundle:1/tuple` — 0->1
- row 11 (tuple, lenient: essentialMatch) `Acquia/Acquia percentage check bundle:1/tuple` — 0->1
- row 12 (tuple, lenient: essentialMatch) `Acquia/Acquia birth_date percentage check:1\|Acquia birthdate percentage check bundle:1/tuple` — 0->1
- row 13 (tuple, lenient: essentialMatch) `Acquia/Acquia all_sources_blnd null check:1\|Acquia all_sources_bnd null check:1/tuple` — 0->1
- row 15 (tuple, lenient: essentialMatch) `Acquia/Acquia funnel_stage null check:1/tuple` — 0->1
- row 16 (tuple, lenient: essentialMatch) `Acquia/Acquia hs_grade_level null check:1/tuple` — 0->1
- row 17 (tuple, lenient: essentialMatch) `Acquia/Acquia not null check bundle:1\|Acquia status check:2/tuple` — 0->1
- row 18 (tuple, lenient: essentialMatch) `Acquia/Acquia Test Email check bundle:1\|Acquia test email percentage check:1/tuple` — 0->1

### `CANDIDATE_SLOWER`

- row 44 (tuple, lenient: essentialMatch) `Acquia_AMS/status_threshold_acquia_ams_acs__categories_title_characters_check:V0.1/tuple` — 410ms->913ms
- row 45 (tuple, lenient: mixed) `Acquia_AMS/status_threshold_acs_replication_date_check:V0.1/tuple` — 55ms->176ms
- row 8231 (predicand, lenient: essentialMatch) `ALR/ALR.Partner Code Mapping.All:v01/predicand` — 83ms->237ms
- row 8232 (predicand, lenient: essentialMatch) `ALR/ALR.Partner Code Mapping.All:v01/predicand` — 81ms->226ms
- row 8233 (predicand, lenient: essentialMatch) `ALR/ALR.Partner Code Mapping.All:v01/predicand` — 83ms->227ms
- row 8234 (predicand, lenient: essentialMatch) `ALR/ALR.Partner Code Mapping.All:v01/predicand` — 44ms->127ms
- row 8245 (predicand, lenient: match) `ALR/ALR.Partner Code Mapping.All:v01/predicand` — 109ms->262ms
- row 8253 (predicand, lenient: match) `ALR/ALR.Partner Code Mapping.All:v01/predicand` — 54ms->110ms

### `TABLE_COLUMN_ADDED`

- row 10 (tuple, lenient: essentialMatch) `Acquia/Acquia address percentage check:1\|Acquia address percentage check bundle:1\|Acquia percentage check bundle:1/tuple` — <[Acquia].[pdp_acquia_contact_export]>.*
- row 11 (tuple, lenient: essentialMatch) `Acquia/Acquia percentage check bundle:1/tuple` — <[Acquia].[pdp_acquia_contact_export]>.*
- row 12 (tuple, lenient: essentialMatch) `Acquia/Acquia birth_date percentage check:1\|Acquia birthdate percentage check bundle:1/tuple` — <[Acquia].[pdp_acquia_contact_export]>.*
- row 13 (tuple, lenient: essentialMatch) `Acquia/Acquia all_sources_blnd null check:1\|Acquia all_sources_bnd null check:1/tuple` — <[Acquia].[pdp_acquia_contact_export]>.*
- row 14 (tuple, lenient: essentialMatch) `Acquia/Acquia duplicate email check:1\|Acquia duplicate email check bundle:1\|Acquia duplicate emial check:1/tuple` — <[Acquia].[pdp_acquia_contact_export]>.*
- row 15 (tuple, lenient: essentialMatch) `Acquia/Acquia funnel_stage null check:1/tuple` — <[Acquia].[pdp_acquia_contact_export]>.*
- row 16 (tuple, lenient: essentialMatch) `Acquia/Acquia hs_grade_level null check:1/tuple` — <[Acquia].[pdp_acquia_contact_export]>.*
- row 17 (tuple, lenient: essentialMatch) `Acquia/Acquia not null check bundle:1\|Acquia status check:2/tuple` — <[Acquia].[pdp_acquia_contact_export]>.*

### `CANDIDATE_WARNING_NEW`

- row 45 (tuple, lenient: mixed) `Acquia_AMS/status_threshold_acs_replication_date_check:V0.1/tuple` — WARNING\|STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR\|snowflake_datetime_diff_expression at (l:# c:#) is recognized as Snowflake-specific grammar.
- row 6923 (in_list, lenient: mixed) `ALR/ALR.Partner Code Mapping.All:v01/in_list` — WARNING\|RECOVER\|Syntax error, attempting recovery; WARNING\|RECOVER_INLINE\|Invalid syntax near ''American Indian or Alaska Native, Asian, Black or African American''
- row 8416 (query, lenient: essentialMatch) `ALR/Final DMP Extract Bundle:V0.1\|Generated Final DMP Extract Bundle:Gen.V.01/query` — WARNING\|UNRESOLVED_UNQUALIFIED_COLUMNS\|Unresolved unqualified column reference(s): [primary_student_id [(l:# c:#)]]
- row 8417 (query, lenient: essentialMatch) `ALR/Final DMP Extract Bundle:V0.1/query` — WARNING\|UNRESOLVED_UNQUALIFIED_COLUMNS\|Unresolved unqualified column reference(s): [primary_student_id [(l:# c:#)]]
- row 9508 (query, lenient: essentialMatch) `AMS/AMS_BUNDLE:V1.0/query` — WARNING\|UNRESOLVED_UNQUALIFIED_COLUMNS\|Unresolved unqualified column reference(s): [preferred_full_name [(l:# c:#), (l:# c:#)]]
- row 35502 (join_extension, lenient: essentialMatch) `Enroll360/Generated Enroll360.Partner Code Mapping.All:Gen.V.01/join_extension` — WARNING\|UNRESOLVED_UNQUALIFIED_COLUMNS\|Unresolved unqualified column reference(s): [imputed_flag [(l:# c:#)], eab_fund_type [(l:# c:#)], eab_entry_year_academic [(l:# c:#), (l:# c:#)], agg_fund_amoun
- row 35579 (join_extension, lenient: essentialMatch) `Enroll360/Generated Enroll360.Partner Code Mapping.All:Gen.V.01/join_extension` — WARNING\|UNRESOLVED_UNQUALIFIED_COLUMNS\|Unresolved unqualified column reference(s): [fund_type [(l:# c:#)], calculated_field_# [(l:# c:#)]]; WARNING\|UNRESOLVED_UNQUALIFIED_COLUMNS\|Unresolved unqualified column reference(s
- row 35580 (join_extension, lenient: essentialMatch) `Enroll360/Generated Enroll360.Partner Code Mapping.All:Gen.V.01/join_extension` — WARNING\|UNRESOLVED_UNQUALIFIED_COLUMNS\|Unresolved unqualified column reference(s): [CALCULATED_FIELD_# [(l:# c:#)], FUND_TYPE [(l:# c:#)]]; WARNING\|UNRESOLVED_UNQUALIFIED_COLUMNS\|Unresolved unqualified column reference(s

### `AST_NODE_KIND_ADDED`

- row 45 (tuple, lenient: mixed) `Acquia_AMS/status_threshold_acs_replication_date_check:V0.1/tuple` — datetime_function; level; canonical; text; form
- row 35901 (predicand, lenient: mixed) `Enroll360/Enroll360.Student Term Attributes.embedded_feed.CRM:v01/predicand` — datetime_function; level; canonical; text; form
- row 35961 (predicand, lenient: mixed) `Enroll360/Enroll360.Student Term Attributes.embedded_feed.CRM:v01/predicand` — datetime_function; level; canonical; text; form; increment; value
- row 35962 (predicand, lenient: mixed) `Enroll360/Enroll360.Student Term Attributes.embedded_feed.CRM:v01/predicand` — datetime_function; level; canonical; text; form; increment; value
- row 35963 (predicand, lenient: mixed) `Enroll360/Enroll360.Student Term Attributes.embedded_feed.CRM:v01/predicand` — datetime_function; level; canonical; text; form; increment; value
- row 35964 (predicand, lenient: mixed) `Enroll360/Enroll360.Student Term Attributes.embedded_feed.CRM:v01/predicand` — datetime_function; level; canonical; text; form; increment; value
- row 36634 (predicand, lenient: mixed) `Enroll360/Enroll360.Student Term Attributes.embedded_feed.CRM:v01/predicand` — datetime_function; level; canonical; text; form; increment; value
- row 36635 (predicand, lenient: mixed) `Enroll360/Enroll360.Student Term Attributes.embedded_feed.CRM:v01/predicand` — datetime_function; level; canonical; text; form; increment; value

### `TABLE_KEY_QUALIFICATION_CHANGED`

- row 10101 (tuple, lenient: essentialMatch) `AMS/ams_mail_contacts_stream_key_appeal_code_mismatch_check:1/tuple` — stream_appeal_codes->pdp_ams.stream_appeal_codes
- row 35518 (join_extension, lenient: essentialMatch) `Enroll360/Enroll360.imputed_funds:v02/join_extension` — par__student_year_funds->pdp_ug.par__student_year_funds
- row 35519 (join_extension, lenient: essentialMatch) `Enroll360/Enroll360.imputed_funds.tenant:v01/join_extension` — par__student_year_funds->pdp_ug.par__student_year_funds
- row 35571 (join_extension, lenient: essentialMatch) `Enroll360/Enroll360.Student Term Attributes.embedded_feed.SIS:v3/join_extension` — par__student_term_attributes->pdp_ug.par__student_term_attributes
- row 37044 (query, lenient: match) `Enroll360/Enroll360.MigrationChecks:v1/query` — mkt__zip_markets->pdp_ug.mkt__zip_markets
- row 82894 (predicand, lenient: match) `MDS/add__cass_raw_address_acquia_form_answers_MDS:v0.1/predicand` — acs__contacts->pdp_ug.acs__contacts; acs__form_last_answers->pdp_ug.acs__form_last_answers; rsc__acs_contacts_derived->pdp_ug.rsc__acs_contacts_derived
- row 82895 (predicand, lenient: match) `MDS/add__cass_raw_address_partner_MDS:v0.1/predicand` — rsc__partner_contacts->pdp_ug.rsc__partner_contacts; par__student_address->pdp_ug.par__student_address
- row 85188 (tuple, lenient: essentialMatch) `Partner_Data_Platform/con_contact_attribute_combined_PDPv0.1:v1\|con_contact_attribute_combined_v1:v1/tuple` — dim_funnel_status->es_pdp_common.common.dim_funnel_status

### `TABLE_COLUMN_LOST`

- row 45 (tuple, lenient: mixed) `Acquia_AMS/status_threshold_acs_replication_date_check:V0.1/tuple` — <[Acquia_AMS].[pdp_ams_exp__acquia_contacts]>.CURRENT_TIMESTAMP
- row 8562 (tuple, lenient: essentialMatch) `ALR/Enroll360_StudentProgram_Longitudinal_EABInquiryInd:v01/tuple` — <Current Batch Tbl>.current_date; <Last Delivered Tbl>.current_date
- row 9295 (predicand, lenient: essentialMatch) `AMS/AMS_BUNDLE:V1.0/predicand` — <[AMS].[donor].{fulfillment}>.current_date
- row 9296 (predicand, lenient: essentialMatch) `AMS/AMS_BUNDLE:V1.1/predicand` — <[AMS].[donor].{fulfillment}>.current_date
- row 9590 (query, lenient: degradation) `AMS/AMS_BUNDLE:V1.0/query` — <[AMS].[donor_sub_type].{fulfillment}>.donor_category_desc
- row 9922 (query, lenient: essentialMatch) `AMS/custom_bundle_AMS:V0.1\|custom_bundle_MountMercy_ams:v0.1/query` — <[AMS].[donor_source]>.end_date; <[AMS].[donor_source]>.end_date_5; <[AMS].[donor_source]>.end_date_6; <[AMS].[donor_source]>.end_date_7; <[AMS].[donor_source]>.end_date_8; <[AMS].[donor_source]>.end_date_9; <[AMS].[dono
- row 10067 (tuple, lenient: essentialMatch) `AMS/ams_mail_contacts_unique_key_pair_check:1/tuple` — <[AMS].[mail_contacts].{final}>.count_of_key_pairs
- row 35605 (join_extension, lenient: essentialMatch) `Enroll360/Generated Enroll360.Partner Code Mapping.All:Gen.V.01/join_extension` — <fao_calc.query.total_fund_amount_per_fund_type>.imputed_flag; <fao_calc.query.total_fund_amount_per_fund_type>.partnercontact_id; <fao_calc.query.total_fund_amount_per_fund_type>.*; <fao_calc.query.total_fund_amount_per

### `BASELINE_DIAGNOSTIC_DROPPED`

- row 37566 (tuple, lenient: essentialMatch) `enrollment_services/Master Inquiry Pool Status Threshold:Master/tuple` — FATAL\|\|Invalid syntax near ';'; FATAL\|\|Invalid syntax near ';'
- row 57729 (predicand, lenient: mixed) `Foundation/m_DEGREE_8094_OSF:v10097.1/predicand` — FATAL\|\|Invalid syntax near '='; FATAL\|\|Invalid syntax near '='
- row 78288 (tuple, lenient: essentialMatch) `Foundation/Updated m_BNR_ST_STUDENT_TERM_DOWNFILL_BACKFILL_CAMPUS_OSF Bundle:v10518.1/tuple` — FATAL\|\|Invalid syntax near '<EOF>'
- row 78289 (tuple, lenient: essentialMatch) `Foundation/Part 2 m_BNR_ST_STUDENT_TERM_DOWNFILL_BACKFILL_CAMPUS_OSF Bundle:v10518.1/tuple` — FATAL\|\|Invalid syntax near '<EOF>'
- row 78422 (tuple, lenient: essentialMatch) `Foundation/m_PSFT_ST_STUDENT_TERM_STD_LOGIC_8106_Campus_OSF:v10139.1/tuple` — FATAL\|\|Invalid syntax near '<EOF>'
- row 78426 (tuple, lenient: essentialMatch) `Foundation/m_PSFT_ST_STUDENT_TERM_STD_LOGIC_8106_Campus_OSF:v10139.1/tuple` — FATAL\|\|Invalid syntax near '<EOF>'
- row 78439 (tuple, lenient: essentialMatch) `Foundation/m_PSFT_ST_STUDENT_TERM_STD_LOGIC_8654_Campus_OSF:v10503.1/tuple` — FATAL\|\|Invalid syntax near '<EOF>'
- row 78622 (tuple, lenient: essentialMatch) `Foundation/Updated m_BNR_ST_STUDENT_TERM_DOWNFILL_BACKFILL_8140_Campus_OSF Bundle:v10177.1/tuple` — FATAL\|\|Invalid syntax near '<EOF>'

### `CANDIDATE_SEVERE_NEW`

- row 9336 (predicand, lenient: essentialMatch) `AMS/AMS_BUNDLE:V1.0/predicand` — SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'gift_id' at (l:# c:#). Possible sources: [<[AMS].[gifts].{fulfillment}>, <[AMS].[donor].{fulfillment}>]
- row 9508 (query, lenient: essentialMatch) `AMS/AMS_BUNDLE:V1.0/query` — SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'preferred_full_name' at (l:# c:#). Possible sources: [<[AMS].[donor_address].{fulfillment}>, <[AMS].[donor].{fulfillment}>]
- row 35502 (join_extension, lenient: essentialMatch) `Enroll360/Generated Enroll360.Partner Code Mapping.All:Gen.V.01/join_extension` — SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'eab_entry_year_academic' at (l:# c:#). Possible sources: [query#, query#]; SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'eab_e
- row 35579 (join_extension, lenient: essentialMatch) `Enroll360/Generated Enroll360.Partner Code Mapping.All:Gen.V.01/join_extension` — SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'fund_type' at (l:# c:#). Possible sources: [<[Enroll#].[Student Year Funds].{fulfillment}>, <[Enroll#].[Partner Code Mapping].{conve; SEVERE_WARNING\|A
- row 35580 (join_extension, lenient: essentialMatch) `Enroll360/Generated Enroll360.Partner Code Mapping.All:Gen.V.01/join_extension` — SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'CALCULATED_FIELD_#' at (l:# c:#). Possible sources: [<[Enroll#].[Student Year Funds].{fulfillment}>, <[Enroll#].[Partner Code Mappin; SEVERE_WARNING\|A
- row 35582 (join_extension, lenient: essentialMatch) `Enroll360/DeSales.SYFA/SYF Test PCM.All:v1/join_extension` — SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'eab_entry_year_academic' at (l:# c:#). Possible sources: [query#, query#]; SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'eab_e
- row 35605 (join_extension, lenient: essentialMatch) `Enroll360/Generated Enroll360.Partner Code Mapping.All:Gen.V.01/join_extension` — SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'eab_entry_year_academic' at (l:# c:#). Possible sources: [query#, query#]; SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'eab_e
- row 37030 (query, lenient: essentialMatch) `Enroll360/Enroll360.MigrationChecks:v1/query` — SEVERE_WARNING\|AMBIGUOUS_COLUMN_REFERENCE\|Ambiguous column reference 'eab_fund_amount' at (l:# c:#). Possible sources: [<[Enroll#].[Student Year Funds].[Last Validated].{final}>, <cte#_pop_definition>]; SEVERE_WARNING\|AM

### `CANDIDATE_FATAL_NEW`

- row 6923 (in_list, lenient: mixed) `ALR/ALR.Partner Code Mapping.All:v01/in_list` — FATAL\|REPORT_ERROR\|unexpected input: ']' in rule row_value_expression: 'American Indian or Alaska Native, Asian',]
- row 8416 (query, lenient: essentialMatch) `ALR/Final DMP Extract Bundle:V0.1\|Generated Final DMP Extract Bundle:Gen.V.01/query` — FATAL\|UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES\|Unqualified column 'primary_student_id' at (l:# c:#) was not found in output interface of any visible query alias [cte].
- row 8417 (query, lenient: essentialMatch) `ALR/Final DMP Extract Bundle:V0.1/query` — FATAL\|UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES\|Unqualified column 'primary_student_id' at (l:# c:#) was not found in output interface of any visible query alias [cte].
- row 37022 (query, lenient: essentialMatch) `Enroll360/Enroll360.Partner Code Mapping.All:v01/query` — FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: target_partnercontact_id at (l:# c:#) and target_partnercontact_id at (l:# c:#).; FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defin
- row 78195 (tuple, lenient: essentialMatch) `Foundation/m_STUDENT_COURSE_WORK_8026_OSF:v.5001/tuple` — FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: <Lookup Procedure_lkp_academic_period.ACADEMIC_PERIOD#> at (l:# c:#) and <Lookup Procedure_lkp_academic_period.ACADEMIC_PER; FATAL\|DUPLICATE_INTERFAC
- row 78235 (tuple, lenient: essentialMatch) `Foundation/m_PSFT_SC_STUDENT_COURSE_WORK_8297_OSF:v10337.1/tuple` — FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: <lookup procedure_lkp_active_courses_course_type_lkp.in_course_cd> at (l:# c:#) and <lookup procedure_lkp_active_courses_co
- row 78305 (tuple, lenient: error) `Foundation/m_STUDENT_GROUPS_12372_OSF:v12372.1/tuple` — FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: <lookup procedure_lkp_apl_term_eff_dt.in_term_id_eff> at (l:# c:#) and <lookup procedure_lkp_apl_term_eff_dt.in_term_id_eff
- row 78309 (tuple, lenient: essentialMatch) `Foundation/m_OSF_SSF_SC_StudentCourses_8108:v10141.1/tuple` — FATAL\|DUPLICATE_INTERFACE_COLUMNS\|Duplicate interface columns defined: <lookup procedure_lkp_courses#.course_cd#> at (l:# c:#) and <lookup procedure_lkp_courses#.course_cd#> at (l:# c:#).

### `SUBSTITUTION_TYPE_CHANGED`

- row 35872 (predicand, lenient: degradation) `Enroll360/Enroll360.finaid_final_variables:v01/predicand` — <eab_financial_need>:condition->predicand
- row 36900 (predicand, lenient: degradation) `Enroll360/Enroll360.finaid_final_variables:v01/predicand` — <fao_calc.ti_grant>:condition->predicand
- row 42150 (predicand, lenient: neutral) `Foundation/m_BNR_CS_ACTIVE_COURSES_8449_CAMPUS_OSF:v10490.1/predicand` — <expression_exp_stg_1.v_subject_cd>:condition->predicand; <expression_exp_stg_1.v_course_no>:condition->predicand
- row 42151 (predicand, lenient: neutral) `Foundation/m_STUDENT_EXAM_8319_OSF:v10362.1/predicand` — <expression_exp_stg_logic.exam_cd>:condition->predicand
- row 42153 (predicand, lenient: mixed) `Foundation/Updated m_STUDENT_TERM_DOWNFILL_BACKFILL_8052_OSF Bundle:v10085.1/predicand` — <expression_exp_stud_clas_cd_set_member_specific.majr_code_conc_1_3>:condition->predicand; <expression_exp_stud_clas_cd_set_member_specific.majr_code_conc_1_2>:condition->predicand
- row 42160 (predicand, lenient: mixed) `Foundation/Updated m_STUDENT_TERM_DOWNFILL_BACKFILL_8052_OSF Bundle:v10085.1/predicand` — <expression_exp_stud_clas_cd_set_member_specific.majr_code_conc_2_3>:condition->predicand; <expression_exp_stud_clas_cd_set_member_specific.majr_code_conc_1_2>:query->predicand; <expression_exp_stud_clas_cd_set_member_sp
- row 42161 (predicand, lenient: neutral) `Foundation/m_OSF_SSF_SA_Student_Advisor_8071:v10115.1/predicand` — <expression_exptrans1.group_desc>:condition->predicand
- row 42195 (predicand, lenient: neutral) `Foundation/m_CS_ACTIVE_COURSES_8342_Campus_OSF:v10383.1/predicand` — <expression_exp_stg_1.course_no>:condition->predicand

### `BASELINE_FATAL_ONLY`

- row 37566 (tuple, lenient: essentialMatch) `enrollment_services/Master Inquiry Pool Status Threshold:Master/tuple` — FATAL\|\|Invalid syntax near ';'; FATAL\|\|Invalid syntax near ';'
- row 78288 (tuple, lenient: essentialMatch) `Foundation/Updated m_BNR_ST_STUDENT_TERM_DOWNFILL_BACKFILL_CAMPUS_OSF Bundle:v10518.1/tuple` — FATAL\|\|Invalid syntax near '<EOF>'
- row 78289 (tuple, lenient: essentialMatch) `Foundation/Part 2 m_BNR_ST_STUDENT_TERM_DOWNFILL_BACKFILL_CAMPUS_OSF Bundle:v10518.1/tuple` — FATAL\|\|Invalid syntax near '<EOF>'
- row 78422 (tuple, lenient: essentialMatch) `Foundation/m_PSFT_ST_STUDENT_TERM_STD_LOGIC_8106_Campus_OSF:v10139.1/tuple` — FATAL\|\|Invalid syntax near '<EOF>'
- row 78426 (tuple, lenient: essentialMatch) `Foundation/m_PSFT_ST_STUDENT_TERM_STD_LOGIC_8106_Campus_OSF:v10139.1/tuple` — FATAL\|\|Invalid syntax near '<EOF>'
- row 78439 (tuple, lenient: essentialMatch) `Foundation/m_PSFT_ST_STUDENT_TERM_STD_LOGIC_8654_Campus_OSF:v10503.1/tuple` — FATAL\|\|Invalid syntax near '<EOF>'
- row 78622 (tuple, lenient: essentialMatch) `Foundation/Updated m_BNR_ST_STUDENT_TERM_DOWNFILL_BACKFILL_8140_Campus_OSF Bundle:v10177.1/tuple` — FATAL\|\|Invalid syntax near '<EOF>'
- row 78848 (tuple, lenient: essentialMatch) `Foundation/m_STUDENT_TERM_STD_LOGIC_8021_OSF:v10049.1/tuple` — FATAL\|\|Invalid syntax near '<EOF>'

### `AST_NODE_KIND_LOST`

- row 35962 (predicand, lenient: mixed) `Enroll360/Enroll360.Student Term Attributes.embedded_feed.CRM:v01/predicand` — function; parameters
- row 35963 (predicand, lenient: mixed) `Enroll360/Enroll360.Student Term Attributes.embedded_feed.CRM:v01/predicand` — function; parameters
- row 35964 (predicand, lenient: mixed) `Enroll360/Enroll360.Student Term Attributes.embedded_feed.CRM:v01/predicand` — function; parameters
- row 36635 (predicand, lenient: mixed) `Enroll360/Enroll360.Student Term Attributes.embedded_feed.CRM:v01/predicand` — function; parameters
- row 36858 (predicand, lenient: mixed) `Enroll360/Enroll360.Student Term Attributes.embedded_feed.SIS:v01/predicand` — function; parameters
- row 36859 (predicand, lenient: mixed) `Enroll360/Enroll360.Student Term Attributes.embedded_feed.CRM:v01/predicand` — function; parameters
- row 36860 (predicand, lenient: mixed) `Enroll360/Enroll360.Student Term Attributes.embedded_feed.CRM:v01/predicand` — function; parameters
- row 36861 (predicand, lenient: mixed) `Enroll360/Enroll360.effective_date:v01\|Enroll360.StudentTermAttributes.AttributeLogic:v2/predicand` — function; parameters

### `BASELINE_ONLY_FAILED`

- row 78284 (tuple, lenient: error) `Foundation/m_BNR_SC_STUDENT_COURSE_WORK_8990_OSF:v11246.1/tuple` — PssSqlParserAccessServer non-JSON stdout: Exception: [Exception when walking the parse tree: class java.lang.String cannot be cast to class java.util.Map (java.lang.String and java.util.Map are in mod
- row 78305 (tuple, lenient: error) `Foundation/m_STUDENT_GROUPS_12372_OSF:v12372.1/tuple` — PssSqlParserAccessServer non-JSON stdout: Exception: [Exception when walking the parse tree: class java.lang.String cannot be cast to class java.util.Map (java.lang.String and java.util.Map are in mod
- row 78547 (tuple, lenient: error) `Foundation/m_STUDENT_COURSE_WORK_8039_OSF_Campus:v10067.1/tuple` — PssSqlParserAccessServer non-JSON stdout: Exception: [Exception when walking the parse tree: class java.lang.String cannot be cast to class java.util.Map (java.lang.String and java.util.Map are in mod
- row 78607 (tuple, lenient: error) `Foundation/m_PSFT_SC_STUDENT_COURSE_WORK_8106_Campus_OSF:v10139.1/tuple` — PssSqlParserAccessServer non-JSON stdout: Exception: [Exception when walking the parse tree: class java.lang.String cannot be cast to class java.util.Map (java.lang.String and java.util.Map are in mod
- row 78765 (tuple, lenient: error) `Foundation/m_STUDENT_COURSE_WORK_SRA_8020_OSF:v10048.1/tuple` — PssSqlParserAccessServer non-JSON stdout: Exception: [Line 2:62 - null - unexpected input: 'ORDER', Line 2:62 - Syntax error, attempting recovery, Line 1:27 - Invalid syntax near '(', Line 1:27 - Inva
- row 78853 (tuple, lenient: error) `Foundation/m_STUDENT_TERM_STD_LOGIC_5282_OSF:v5282.1/tuple` — PssSqlParserAccessServer non-JSON stdout: Exception: [Exception when walking the parse tree: class java.lang.String cannot be cast to class java.util.Map (java.lang.String and java.util.Map are in mod
- row 85093 (tuple, lenient: error) `PANTO_TEST_DOMAIN/Values statement with where condition:01/tuple` — PssSqlParserAccessServer non-JSON stdout: Key = k, Value = [@28,75:75='k',<328>,1:75]
- row 85095 (tuple, lenient: error) `PANTO_TEST_DOMAIN/Values statement with alias:01/tuple` — PssSqlParserAccessServer non-JSON stdout: Key = $1, Value = [@6,23:23='(',<285>,1:23]

### `TABLE_COLUMN_CASE_CHANGED`

- row 8418 (query, lenient: match) `ALR/Generated Final DMP Extract Bundle:Gen.V.01/query` — <[ALR].[Student Race]>.primary_student_id->PRIMARY_STUDENT_ID
- row 8419 (query, lenient: match) `ALR/Final DMP Extract Bundle:V0.1/query` — <[ALR].[Student Race]>.primary_student_id->PRIMARY_STUDENT_ID
- row 8420 (query, lenient: match) `ALR/Final DMP Extract Bundle:V0.1/query` — <[ALR].[Student Race].{final}>.primary_student_id->PRIMARY_STUDENT_ID
- row 8421 (query, lenient: match) `ALR/Final DMP Extract Bundle:V0.1/query` — <[ALR].[Student Race].{final}>.primary_student_id->PRIMARY_STUDENT_ID
- row 8665 (tuple, lenient: essentialMatch) `ALR/ALR_StudentAddress_Format_Zip:v01/tuple` — <Base Tbl>.zip->Zip
- row 37151 (tuple, lenient: essentialMatch) `Enroll360/Enroll360_StudentAddress_Format_Zip:1/tuple` — <Base Tbl>.zip->Zip
- row 37334 (tuple, lenient: essentialMatch) `Enroll360/Enroll360_StudentAddress_Format_Zip:1/tuple` — <Base Tbl>.zip->Zip

### `FATAL_BOTH`

- row 57729 (predicand, lenient: mixed) `Foundation/m_DEGREE_8094_OSF:v10097.1/predicand`
- row 78799 (tuple, lenient: essentialMatch) `Foundation/m_BNR_ST_STUDENT_TERM_DOWNFILL_BACKFILL_8990_UPDT_bkp:v11246.1/tuple`
- row 78825 (tuple, lenient: essentialMatch) `Foundation/MPLT_student_term_major_logic:v10132.1/tuple`
- row 79149 (tuple, lenient: essentialMatch) `Foundation/m_BNR_ST_STUDENT_TERM_DOWNFILL_BACKFILL_8990_UPDT_bkp:v11246.1/tuple`
- row 79154 (tuple, lenient: essentialMatch) `Foundation/m_BNR_ST_STUDENT_TERM_DOWNFILL_BACKFILL_8990_UPDT_bkp:v11246.1/tuple`

### `CANDIDATE_NULL_TREE`

- row 6923 (in_list, lenient: mixed) `ALR/ALR.Partner Code Mapping.All:v01/in_list`
- row 57729 (predicand, lenient: mixed) `Foundation/m_DEGREE_8094_OSF:v10097.1/predicand`
- row 78765 (tuple, lenient: error) `Foundation/m_STUDENT_COURSE_WORK_SRA_8020_OSF:v10048.1/tuple`
- row 88050 (predicand, lenient: error) `YouVisit/51840_School_ID_Inquiry_Pool-20484:Custom/predicand`

### `TABLE_KEY_ADDED`

- row 79475 (tuple, lenient: essentialMatch) `Foundation/m_BNR_SC_STUDENT_COURSE_WORK_CAMPUS_OSF:v1.1/tuple` — <Lookup Procedure_LKP_academic_period>
- row 80289 (tuple, lenient: essentialMatch) `Foundation/m_BNR_ST_STUDENT_TERM_STD_LOGIC_CAMPUS_OSF:v1.1/tuple` — <Lookup Procedure_LKP_reg_start_end>

### `INTERFACE_COLUMN_LOST`

- row 35573 (join_extension, lenient: essentialMatch) `Enroll360/Enroll360.Partner Code Mapping.All:v01/join_extension` — email_address; se_primary_student_id

### `SUBSTITUTION_LOST`

- row 57729 (predicand, lenient: mixed) `Foundation/m_DEGREE_8094_OSF:v10097.1/predicand` — <expression_exp_std_logic.major_cd>

### `TABLE_KEY_CASE_CHANGED`

- row 85248 (tuple, lenient: essentialMatch) `Partner_Data_Platform/prc_sourcecontact_sourcetype_combined_bundle_demo_1:1\|prc_sourcecontact_sourcetype_combined_bundle_demo_1:2/tuple` — [source type cappex]->[Source Type Cappex]

