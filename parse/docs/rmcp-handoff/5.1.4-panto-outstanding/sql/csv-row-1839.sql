/**************************************
Student
***************************************/

--First Generation Indicator
select 
'eab_first_gen_ind' as field_name,
<eab_first_gen_ind> as eab_standard_value,
first_generation_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student].{fulfillment}> stud
group by <eab_first_gen_ind>,
first_generation_indicator

union all

--Preferred Gender
select 
'eab_pref_gender' as field_name,
<eab_pref_gender> as eab_standard_value,
preferred_gender as partner_value,
count(*) as record_count
from <[Enroll360].[Student].{fulfillment}> stud
group by <eab_pref_gender>,
preferred_gender

union all

--International Indicator
select 
'eab_international_ind' as field_name,
<eab_international_ind> as eab_standard_value,
international_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student].{fulfillment}> stud
group by <eab_international_ind>,
international_indicator

union all

--Military Service or Veteran Status
select 
'eab_military_veteran_ind' as field_name,
<eab_military_veteran_ind> as eab_standard_value,
military_service_or_veteran_status as partner_value,
count(*) as record_count
from <[Enroll360].[Student].{fulfillment}> stud
group by <eab_military_veteran_ind>,
military_service_or_veteran_status

union all

--Alumni Relationship Indicator
select 
'eab_alumni_relation_ind' as field_name,
<eab_alumni_relation_ind> as eab_standard_value,
alumni_relationship_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student].{fulfillment}> stud
group by <eab_alumni_relation_ind>,
alumni_relationship_indicator

union all

--Ethnicity
select 
'eab_ethnicity' as field_name,
<eab_ethnicity> as eab_standard_value,
ethnicity as partner_value,
count(*) as record_count
from <[Enroll360].[Student].{fulfillment}> stud
group by <eab_ethnicity>,
ethnicity

union all

--Campus Visit Indicator
select 
'eab_campus_visit_ind' as field_name,
<eab_campus_visit_ind> as eab_standard_value,
campus_visit_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student].{fulfillment}> stud
group by <eab_campus_visit_ind>,
campus_visit_indicator

union all

--Mail Opt Out Indicator
select 
'eab_mail_optout_ind' as field_name,
<eab_mail_optout_ind> as eab_standard_value,
mail_opt_out_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student].{fulfillment}> stud
group by <eab_mail_optout_ind>,
mail_opt_out_indicator

union all

--Email Opt Out Indicator
select 
'eab_email_optout_ind' as field_name,
<eab_email_optout_ind> as eab_standard_value,
email_opt_out_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student].{fulfillment}> stud
group by <eab_email_optout_ind>,
email_opt_out_indicator

union all

--Phone Opt Out Indicator
select 
'eab_phone_optout_ind' as field_name,
<eab_phone_optout_ind> as eab_standard_value,
phone_opt_out_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student].{fulfillment}> stud
group by <eab_phone_optout_ind>,
phone_opt_out_indicator

union all

--SMS Opt In Indicator
select 
'eab_sms_optin_ind' as field_name,
<eab_sms_optin_ind> as eab_standard_value,
sms_opt_in_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student].{fulfillment}> stud
group by <eab_sms_optin_ind>,
sms_opt_in_indicator

union all

--Opt Out Indicator General
select 
'eab_opt_out_ind' as field_name,
<eab_opt_out_ind> as eab_standard_value,
opt_out_indicator_general as partner_value,
count(*) as record_count
from <[Enroll360].[Student].{fulfillment}> stud
group by <eab_opt_out_ind>,
opt_out_indicator_general

union all

/**************************************
Student Phone
***************************************/

--Phone Type
select  
'eab_phone_type' as field_name,
<eab_phone_type> as eab_standard_value,
partner_value,
sum(record_count) as record_count
from (

--set 1
select 
phone_number_type as partner_value,
count(*) as record_count
from <[Enroll360].[Student Phone].{fulfillment}> stud_phone
where phone_number is not null
group by phone_number_type 

union all

--set 2
select 
calculated_field_2 as partner_value,
count(*) as record_count
from <[Enroll360].[Student Phone].{fulfillment}> stud_phone
where calculated_field_1 is not null
group by calculated_field_2
) agg

group by <eab_phone_type>,
partner_value

union all

/**************************************
Student Email
***************************************/

--Email Type
select  
'email_type' as field_name,
<email_type> as eab_standard_value,
partner_value,
sum(record_count) as record_count
from (

--set 1
select 
email_type as partner_value,
count(*) as record_count
from <[Enroll360].[Student Email].{fulfillment}> stud_email
where email_address is not null
group by email_type 

union all

--set 2
select 
calculated_field_2 as partner_value,
count(*) as record_count
from <[Enroll360].[Student Email].{fulfillment}> stud_email
where calculated_field_1 is not null
group by calculated_field_2
  
union all

--set 3
select 
calculated_field_4 as partner_value,
count(*) as record_count
from <[Enroll360].[Student Email].{fulfillment}> stud_email
where calculated_field_3 is not null
group by calculated_field_4
  
) agg

group by <email_type>,
partner_value

union all

/**************************************
Student Test Scores
***************************************/

--Exam Type
select  
'eab_exam_type' as field_name,
<eab_exam_type> as eab_standard_value,
partner_value,
sum(record_count) as record_count
from (

--set 1
select 
exam_code as partner_value,
count(*) as record_count
from <[Enroll360].[Student Test Scores].{fulfillment}> stud_test_scores
where score is not null
group by exam_code 

union all

--set 2
select 
calculated_field_1 as partner_value,
count(*) as record_count
from <[Enroll360].[Student Test Scores].{fulfillment}> stud_test_scores
where calculated_field_2 is not null
group by calculated_field_1
  
union all

--set 3
select 
calculated_field_4 as partner_value,
count(*) as record_count
from <[Enroll360].[Student Test Scores].{fulfillment}> stud_test_scores
where calculated_field_5 is not null
group by calculated_field_4
  
union all

--set 4
select 
calculated_field_7 as partner_value,
count(*) as record_count
from <[Enroll360].[Student Test Scores].{fulfillment}> stud_test_scores
where calculated_field_8 is not null
group by calculated_field_7
  
) agg

group by <eab_exam_type>,
partner_value

union all

/**************************************
Student Relationship
***************************************/

--Relationship to Student
select  
'relationship_to_student' as field_name,
<relationship_to_student> as eab_standard_value,
partner_value,
sum(record_count) as record_count
from (

--set 1
select 
relationship_to_student as partner_value,
count(*) as record_count
from <[Enroll360].[Student Relationship].{fulfillment}> stud_rship
where relationship_email_address is not null
or relationship_first_name is not null
or relationship_last_name is not null
group by relationship_to_student 

union all

--set 2
select 
calculated_field_3 as partner_value,
count(*) as record_count
from <[Enroll360].[Student Relationship].{fulfillment}> stud_rship
where calculated_field_2 is not null
or calculated_field_4 is not null
or calculated_field_5 is not null
group by calculated_field_3
  
union all

--set 3
select 
calculated_field_13 as partner_value,
count(*) as record_count
from <[Enroll360].[Student Relationship].{fulfillment}> stud_rship
where calculated_field_12 is not null
or calculated_field_14 is not null
or calculated_field_15 is not null
group by calculated_field_13
  
) agg

group by <relationship_to_student>,
partner_value

union all

/**************************************
Student Year Financial Aid
***************************************/

--CSS Profile Submitted Indicator
select 
'eab_css_profile_submitted_ind' as field_name,
<eab_css_profile_submitted_ind> as eab_standard_value,
css_profile_submitted_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Year Financial Aid].{fulfillment}> stud_yr_fin_aid
group by <eab_css_profile_submitted_ind>,
css_profile_submitted_indicator

union all

--FAFSA Submitted
select 
'eab_FAFSA_submitted_ind' as field_name,
<eab_FAFSA_submitted_ind> as eab_standard_value,
fafsa_submitted as partner_value,
count(*) as record_count
from <[Enroll360].[Student Year Financial Aid].{fulfillment}> stud_yr_fin_aid
group by <eab_FAFSA_submitted_ind>,
fafsa_submitted

union all

--Financial Aid Award Sent Indicator
select 
'eab_fin_aid_award_sent_ind' as field_name,
<eab_fin_aid_award_sent_ind> as eab_standard_value,
financial_aid_award_sent_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Year Financial Aid].{fulfillment}> stud_yr_fin_aid
group by <eab_fin_aid_award_sent_ind>,
financial_aid_award_sent_indicator


union all


/**************************************
Student Term
***************************************/

--Athletic Recruit Indicator
select 
'eab_athletic_recruit_ind' as field_name,
<eab_athletic_recruit_ind> as eab_standard_value,
athletic_recruit as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term].{fulfillment}> stud_term
group by <eab_athletic_recruit_ind>,
athletic_recruit

union all

--Inquiry Indicator
select 
'eab_inquiry_ind' as field_name,
<eab_inquiry_ind> as eab_standard_value,
inquiry_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term].{fulfillment}> stud_term
group by <eab_inquiry_ind>,
inquiry_indicator

union all

--Current Entry Term
select 
'eab_entry_term' as field_name,
<eab_entry_term> as eab_standard_value,
partner_value,
sum(record_count) as record_count
from
(
--Student Term  
select  
current_entry_term as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term].{fulfillment}> stud_term
group by current_entry_term

union all
  
--Student Term Application
select  
current_entry_term as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application].{fulfillment}> stud_term
group by current_entry_term
  

) et
group by <eab_entry_term>,
partner_value

union all

--Current Entry Year as Academic Year
select 
'eab_entry_year_academic' as field_name,
<eab_entry_year_academic> as eab_standard_value,
partner_value,
sum(record_count) as record_count
from
(
--Student Term
select
current_entry_year_as_academic_year as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term].{fulfillment}> stud_term
group by current_entry_year_as_academic_year
  
union all
  
--Student Term Application
select
current_entry_year_as_academic_year as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application].{fulfillment}> stud_term
group by current_entry_year_as_academic_year  

union all
  
--Student Year Financial Aid
select
current_entry_year_as_academic_year as partner_value,
count(*) as record_count
from <[Enroll360].[Student Year Financial Aid].{fulfillment}> stud_term
group by current_entry_year_as_academic_year

)  ey
group by <eab_entry_year_academic>,
partner_value

union all

--Student Term Student Type  
select  
'eab_student_type' as field_name,
<eab_student_type> as eab_standard_value,
partner_value,
sum(record_count) as record_count
from (

--Student Term
select 
stud_term.student_type as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term].{fulfillment}> stud_term
group by stud_term.student_type

union all
  
--Student Term Application 
select 
stud_term.student_type as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application].{fulfillment}> stud_term
group by stud_term.student_type  

union all 

/*3-8-2023: added reference to Fin Aid table to pull any unique raw values to be converted - ccreekman*/
  
--Student Year Financial Aid
select 
stud_term.student_type as partner_value,
count(*) as record_count
from <[Enroll360].[Student Year Financial Aid].{fulfillment}> stud_term
group by stud_term.student_type  
  
) stype
group by <eab_student_type>,
partner_value

union all

--First Source
select 
'eab_first_source' as field_name,
null as eab_standard_value,
inquiry_first_source_code as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term].{fulfillment}> stud_term
group by inquiry_first_source_code

union all

/**************************************
Student Address
***************************************/

--Address Type
select  
'address_type' as field_name,
<address_type> as eab_standard_value,
partner_value,
sum(record_count) as record_count
from (

--set 1
select 
address_type as partner_value,
count(*) as record_count
from <[Enroll360].[Student Address].{fulfillment}> stud_addr
where address_line_1 is not null
group by address_type 

union all

--set 2
select 
calculated_field_8 as partner_value,
count(*) as record_count
from <[Enroll360].[Student Address].{fulfillment}> stud_addr
where calculated_field_1 is not null
group by calculated_field_8
  
) agg

group by <address_type>,
partner_value

union all

--Country
select 
'eab_country' as field_name,
null as eab_standard_value,
country as partner_value,
sum(record_count) as record_count
from
(
--Address Set 1 Country
select  
country,
count(*) as record_count
from <[Enroll360].[Student Address].{fulfillment}> stud_addr
group by country

union all
  
--Address Set 2 Country
select  
calculated_field_7 as country,
count(*) as record_count
from <[Enroll360].[Student Address].{fulfillment}> stud_addr
group by calculated_field_7
) countries
group by country

union all

/**************************************
Student Race
***************************************/

--Race
select 
'eab_race' as field_name,
<eab_race> as eab_standard_value,
race as partner_value,
count(*) as record_count
from <[Enroll360].[Student Race].{fulfillment}> stud
group by <eab_race>,
race

union all

--Race - American Indian/Alaska Native
select 
'eab_race_amer_indian' as field_name,
<eab_race_amer_indian> as eab_standard_value,
calculated_field_1 as partner_value,
count(*) as record_count
from <[Enroll360].[Student Race].{fulfillment}> stud
group by <eab_race_amer_indian>,
calculated_field_1

union all

--Race - Asian/Asian American
select 
'eab_race_asian' as field_name,
<eab_race_asian> as eab_standard_value,
calculated_field_2 as partner_value,
count(*) as record_count
from <[Enroll360].[Student Race].{fulfillment}> stud
group by <eab_race_asian>,
calculated_field_2

union all

--Race - Black/African American
select 
'eab_race_black' as field_name,
<eab_race_black> as eab_standard_value,
calculated_field_3 as partner_value,
count(*) as record_count
from <[Enroll360].[Student Race].{fulfillment}> stud
group by <eab_race_black>,
calculated_field_3

union all

--Race - Native Hawaiian/Pacific Islander
select 
'eab_race_native_hawaiian' as field_name,
<eab_race_native_hawaiian> as eab_standard_value,
calculated_field_4 as partner_value,
count(*) as record_count
from <[Enroll360].[Student Race].{fulfillment}> stud
group by <eab_race_native_hawaiian>,
calculated_field_4

union all

--Race - White
select 
'eab_race_white' as field_name,
<eab_race_white> as eab_standard_value,
calculated_field_5 as partner_value,
count(*) as record_count
from <[Enroll360].[Student Race].{fulfillment}> stud
group by <eab_race_white>,
calculated_field_5

union all

--Race - Middle Eastern
select 
'eab_race_middle_eastern' as field_name,
<eab_race_middle_eastern> as eab_standard_value,
calculated_field_6 as partner_value,
count(*) as record_count
from <[Enroll360].[Student Race].{fulfillment}> stud
group by <eab_race_middle_eastern>,
calculated_field_6

union all

--Race - Multiracial
select 
'eab_race_multiracial' as field_name,
<eab_race_multiracial> as eab_standard_value,
calculated_field_7 as partner_value,
count(*) as record_count
from <[Enroll360].[Student Race].{fulfillment}> stud
group by <eab_race_multiracial>,
calculated_field_7

union all

--Race - Other
select 
'eab_race_other' as field_name,
<eab_race_other> as eab_standard_value,
calculated_field_8 as partner_value,
count(*) as record_count
from <[Enroll360].[Student Race].{fulfillment}> stud
group by <eab_race_other>,
calculated_field_8

union all

--Race - Prefer Not to Respond
select 
'eab_race_prefer_not_respond' as field_name,
<eab_race_prefer_not_respond> as eab_standard_value,
calculated_field_9 as partner_value,
count(*) as record_count
from <[Enroll360].[Student Race].{fulfillment}> stud
group by <eab_race_prefer_not_respond>,
calculated_field_9

union all

--Race - Unknown
select 
'eab_race_unknown' as field_name,
<eab_race_unknown> as eab_standard_value,
calculated_field_10 as partner_value,
count(*) as record_count
from <[Enroll360].[Student Race].{fulfillment}> stud
group by <eab_race_unknown>,
calculated_field_10

union all

/**************************************
Student Term Application
***************************************/

--Withdrawal Indicator
select 
'eab_withdrawal_ind' as field_name,
<eab_withdrawal_ind> as eab_standard_value,
withdrawal_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application].{fulfillment}> stud_term_app
group by <eab_withdrawal_ind>,
withdrawal_indicator

union all

--Waitlisted Indicator
select 
'eab_waitlist_ind' as field_name,
<eab_waitlist_ind> as eab_standard_value,
waitlist_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application].{fulfillment}> stud_term_app
group by <eab_waitlist_ind>,
waitlist_indicator

union all

--Admit Indicator
select 
'eab_admit_ind' as field_name,
<eab_admit_ind> as eab_standard_value,
admit_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application].{fulfillment}> stud_term_app
group by <eab_admit_ind>,
admit_indicator

union all
/*
--Readmit Indicator
select 
'eab_readmit_ind' as field_name,
<eab_readmit_ind> as eab_standard_value,
readmit_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application].{fulfillment}> stud_term_app
group by <eab_readmit_ind>,
readmit_indicator

union all

*/

--Conditional Admit Indicator
select 
'eab_cond_admit_ind' as field_name,
<eab_cond_admit_ind> as eab_standard_value,
conditional_admit_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application].{fulfillment}> stud_term_app
group by <eab_cond_admit_ind>,
conditional_admit_indicator

union all

--Fulltime or Parttime Status
select 
'eab_full_part_time' as field_name,
<eab_full_part_time> as eab_standard_value,
fulltime_or_parttime_status as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application].{fulfillment}> stud_term_app
group by <eab_full_part_time>,
fulltime_or_parttime_status

union all

--Deposit Confirmed Indicator
select 
'eab_deposit_ind' as field_name,
<eab_deposit_ind> as eab_standard_value,
deposit_confirmed_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application].{fulfillment}> stud_term_app
group by <eab_deposit_ind>,
deposit_confirmed_indicator

union all

--Test Optional Indicator
select 
'eab_test_optional_ind' as field_name,
<eab_test_optional_ind> as eab_standard_value,
test_optional_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application].{fulfillment}> stud_term_app
group by <eab_test_optional_ind>,
test_optional_indicator

union all

--Application Started Indicator
select 
'eab_app_start_ind' as field_name,
<eab_app_start_ind> as eab_standard_value,
application_started_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application].{fulfillment}> stud_term_app
group by <eab_app_start_ind>,
application_started_indicator

union all

--Application Incomplete Indicator
select 
'eab_app_incomplete_ind' as field_name,
<eab_app_incomplete_ind> as eab_standard_value,
application_incomplete_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application].{fulfillment}> stud_term_app
group by <eab_app_incomplete_ind>,
application_incomplete_indicator

union all

--Application Completed Indicator
select 
'eab_app_complete_ind' as field_name,
<eab_app_complete_ind> as eab_standard_value,
application_completed_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application].{fulfillment}> stud_term_app
group by <eab_app_complete_ind>,
application_completed_indicator

union all

--Application Type
select 
'eab_app_type' as field_name,
<eab_app_type> as eab_standard_value,
application_type as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application].{fulfillment}> stud_term_app
group by <eab_app_type>,
application_type

union all

--Application Decision Type
select 
'eab_app_decision_type' as field_name,
<eab_app_decision_type> as eab_standard_value,
application_decision_type_code as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application].{fulfillment}> stud_term_app
group by <eab_app_decision_type>,
application_decision_type_code

union all

--Campus Resident or Commuter Indicator
select 
'eab_commuter_ind' as field_name,
<eab_commuter_ind> as eab_standard_value,
campus_resident_or_commuter_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application].{fulfillment}> stud_term_app
group by <eab_commuter_ind>,
campus_resident_or_commuter_indicator

union all

--Previous Deferral Indicator
select 
'eab_deferral_prev_ind' as field_name,
<eab_deferral_prev_ind> as eab_standard_value,
deferral_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application].{fulfillment}> stud_term_app
group by <eab_deferral_prev_ind>,
deferral_indicator

union all

--Future Deferral Indicator
select 
'eab_deferral_future_ind' as field_name,
<eab_deferral_future_ind> as eab_standard_value,
deferral_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application].{fulfillment}> stud_term_app
group by <eab_deferral_future_ind>,
deferral_indicator

union all

--Deny Indicator
select 
'eab_deny_ind' as field_name,
<eab_deny_ind> as eab_standard_value,
deny_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application].{fulfillment}> stud_term_app
group by <eab_deny_ind>,
deny_indicator

union all

--Enrollment Indicator
select 
'eab_enroll_ind' as field_name,
<eab_enroll_ind> as eab_standard_value,
enrollment_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application].{fulfillment}> stud_term_app
group by <eab_enroll_ind>,
enrollment_indicator

union all

--Housing Deposit Paid or Waived Indicator
select 
'eab_housing_deposit_ind' as field_name,
<eab_housing_deposit_ind> as eab_standard_value,
housing_deposit_paid_or_waived_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application].{fulfillment}> stud_term_app
group by <eab_housing_deposit_ind>,
housing_deposit_paid_or_waived_indicator