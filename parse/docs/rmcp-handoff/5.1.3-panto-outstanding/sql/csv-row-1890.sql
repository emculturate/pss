/**************************************
Student SIS
***************************************/

--First Generation Indicator
select 
'eab_first_gen_ind' as field_name,
<eab_first_gen_ind> as eab_standard_value,
first_generation_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student SIS].{fulfillment}> stud
group by <eab_first_gen_ind>,
first_generation_indicator

union all

--Preferred Gender
select 
'eab_pref_gender' as field_name,
<eab_pref_gender> as eab_standard_value,
preferred_gender as partner_value,
count(*) as record_count
from <[Enroll360].[Student SIS].{fulfillment}> stud
group by <eab_pref_gender>,
preferred_gender

union all

--International Indicator
select 
'eab_international_ind' as field_name,
<eab_international_ind> as eab_standard_value,
international_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student SIS].{fulfillment}> stud
group by <eab_international_ind>,
international_indicator

union all

--Military Service or Veteran Status
select 
'eab_military_veteran_ind' as field_name,
<eab_military_veteran_ind> as eab_standard_value,
military_service_or_veteran_status as partner_value,
count(*) as record_count
from <[Enroll360].[Student SIS].{fulfillment}> stud
group by <eab_military_veteran_ind>,
military_service_or_veteran_status

union all

--Alumni Relationship Indicator
select 
'eab_alumni_relation_ind' as field_name,
<eab_alumni_relation_ind> as eab_standard_value,
alumni_relationship_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student SIS].{fulfillment}> stud
group by <eab_alumni_relation_ind>,
alumni_relationship_indicator

union all

--Ethnicity
select 
'eab_ethnicity' as field_name,
<eab_ethnicity> as eab_standard_value,
ethnicity as partner_value,
count(*) as record_count
from <[Enroll360].[Student SIS].{fulfillment}> stud
group by <eab_ethnicity>,
ethnicity

union all

--Campus Visit Indicator
select 
'eab_campus_visit_ind' as field_name,
<eab_campus_visit_ind> as eab_standard_value,
campus_visit_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student SIS].{fulfillment}> stud
group by <eab_campus_visit_ind>,
campus_visit_indicator

union all

--Mail Opt Out Indicator
select 
'eab_mail_optout_ind' as field_name,
<eab_mail_optout_ind> as eab_standard_value,
mail_opt_out_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student SIS].{fulfillment}> stud
group by <eab_mail_optout_ind>,
mail_opt_out_indicator

union all

--Email Opt Out Indicator
select 
'eab_email_optout_ind' as field_name,
<eab_email_optout_ind> as eab_standard_value,
email_opt_out_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student SIS].{fulfillment}> stud
group by <eab_email_optout_ind>,
email_opt_out_indicator

union all

--Phone Opt Out Indicator
select 
'eab_phone_optout_ind' as field_name,
<eab_phone_optout_ind> as eab_standard_value,
phone_opt_out_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student SIS].{fulfillment}> stud
group by <eab_phone_optout_ind>,
phone_opt_out_indicator

union all

--SMS Opt In Indicator
select 
'eab_sms_optin_ind' as field_name,
<eab_sms_optin_ind> as eab_standard_value,
sms_opt_in_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student SIS].{fulfillment}> stud
group by <eab_sms_optin_ind>,
sms_opt_in_indicator

union all

--Opt Out Indicator General
select 
'eab_opt_out_ind' as field_name,
<eab_opt_out_ind> as eab_standard_value,
opt_out_indicator_general as partner_value,
count(*) as record_count
from <[Enroll360].[Student SIS].{fulfillment}> stud
group by <eab_opt_out_ind>,
opt_out_indicator_general

union all

/**************************************
Student Test Scores SIS
***************************************/


--Exam Type
select  
'eab_exam_type' as field_name,
<eab_exam_type> as eab_standard_value,
exam_code as partner_value,
count(*) as record_count
from <[Enroll360].[Student Test Scores SIS].{fulfillment}> stud_test_scores
where exam_score is not null
group by <eab_exam_type>,
exam_code

union all

--eab exam score
select  
'eab_exam_score' as field_name,
<eab_exam_score> as eab_standard_value,
exam_score as partner_value,
count(*) as record_count
from <[Enroll360].[Student Test Scores SIS].{fulfillment}> stud_test_scores
where exam_score is not null
group by <eab_exam_score>,
exam_score

union all

--eab_self_reported_score
select  
'eab_self_reported_score' as field_name,
<eab_self_reported_score> as eab_standard_value,
self_reported_score as partner_value,
count(*) as record_count
from <[Enroll360].[Student Test Scores SIS].{fulfillment}> stud_test_scores
group by <eab_self_reported_score>,
self_reported_score

union all

--eab_exam_code /*2025-04-28: updated to pull code values from all 4 datasets - ccreekman*/
/*select  
'eab_exam_code' as field_name,
<eab_exam_code> as eab_standard_value,
exam_code as partner_value,
count(*) as record_count
from <[Enroll360].[Student Test Scores SIS].{fulfillment}> stud_test_scores
group by <eab_exam_code>,
exam_code

union all*/

--EAB Exam Code
select  
'eab_exam_code' as field_name,
<eab_exam_code> as eab_standard_value,
partner_value,
sum(record_count) as record_count
from (

--set 1
select 
exam_code as partner_value,
count(*) as record_count
from <[Enroll360].[Student Test Scores SIS].{fulfillment}> stud_test_scores
where exam_score is not null
group by exam_code 

union all

--set 2
select 
calculated_field_1 as partner_value,
count(*) as record_count
from <[Enroll360].[Student Test Scores SIS].{fulfillment}> stud_test_scores
where calculated_field_2 is not null
group by calculated_field_1
  
union all

--set 3
select 
calculated_field_4 as partner_value,
count(*) as record_count
from <[Enroll360].[Student Test Scores SIS].{fulfillment}> stud_test_scores
where calculated_field_5 is not null
group by calculated_field_4
  
union all

--set 4
select 
calculated_field_7 as partner_value,
count(*) as record_count
from <[Enroll360].[Student Test Scores SIS].{fulfillment}> stud_test_scores
where calculated_field_8 is not null
group by calculated_field_7
  
) agg

group by <eab_exam_code>,
partner_value

union all

--calculated_field_1
Select 
'SATR Composite Score' as field_name,
<eab_exam_score> as eab_standard_value,
calculated_field_1 AS partner_value,
count(*) as record_count
from <[Enroll360].[Student Test Scores SIS].{fulfillment}> stud_test_scores
group by <eab_exam_score>,
calculated_field_1

union all

--ACT Composite Score
Select 
'ACT Composite Score' as field_name,
<eab_exam_score> as eab_standard_value,
exam_score AS partner_value,
count(*) as record_count
from <[Enroll360].[Student Test Scores SIS].{fulfillment}> stud_test_scores
group by <eab_exam_score>,
exam_score

union all

/**************************************
Student Year Fin Aid Application
***************************************/

--CSS Profile Submitted Indicator
select 
'eab_css_profile_submitted_ind' as field_name,
<eab_css_profile_submitted_ind> as eab_standard_value,
css_profile_submitted_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Year Finaid Application].{fulfillment}> stud_yr_fin_aid
group by <eab_css_profile_submitted_ind>,
css_profile_submitted_indicator

union all

--FAFSA Submitted
select 
'eab_FAFSA_submitted_ind' as field_name,
<eab_FAFSA_submitted_ind> as eab_standard_value,
fafsa_submitted as partner_value,
count(*) as record_count
from <[Enroll360].[Student Year Finaid Application].{fulfillment}> stud_yr_fin_aid
group by <eab_FAFSA_submitted_ind>,
fafsa_submitted

union all

--Financial Aid Award Sent Indicator
select 
'eab_fin_aid_award_sent_ind' as field_name,
<eab_fin_aid_award_sent_ind> as eab_standard_value,
financial_aid_award_sent_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Year Finaid Application].{fulfillment}> stud_yr_fin_aid
group by <eab_fin_aid_award_sent_ind>,
financial_aid_award_sent_indicator

union all

--eab_fafsa_dependency
select 
'eab_fafsa_dependency' as field_name,
<eab_fafsa_dependency> as eab_standard_value,
fafsa_dependency_status as partner_value,
count(*) as record_count
from <[Enroll360].[Student Year Finaid Application].{fulfillment}> stud_yr_fin_aid
group by <eab_fafsa_dependency>,
fafsa_dependency_status

union all

--eab_aid_package_complete_ind
select 
'eab_aid_package_complete_ind' as field_name,
<eab_aid_package_complete_ind> as eab_standard_value,
aid_package_complete_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Year Finaid Application].{fulfillment}> stud_yr_fin_aid
group by <eab_aid_package_complete_ind>,
aid_package_complete_indicator

union all

--eab_fin_aid_app_complete_ind
select 
'eab_fin_aid_app_complete_ind' as field_name,
<eab_fin_aid_app_complete_ind> as eab_standard_value,
financial_aid_application_complete_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Year Finaid Application].{fulfillment}> stud_yr_fin_aid
group by <eab_fin_aid_app_complete_ind>,
financial_aid_application_complete_indicator

union all

--eab_verification_ind
select 
'eab_verification_ind' as field_name,
<eab_verification_ind> as eab_standard_value,
verification_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Year Finaid Application].{fulfillment}> stud_yr_fin_aid
group by <eab_verification_ind>,
verification_indicator

union all

--eab_verification_status
select 
'eab_verification_status' as field_name,
<eab_verification_status> as eab_standard_value,
verification_status as partner_value,
count(*) as record_count
from <[Enroll360].[Student Year Finaid Application].{fulfillment}> stud_yr_fin_aid
group by <eab_verification_status>,
verification_status

union all

--eab_professional_judgement_ind
select 
'eab_professional_judgement_ind' as field_name,
<eab_professional_judgement_ind> as eab_standard_value,
professional_judgement_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Year Finaid Application].{fulfillment}> stud_yr_fin_aid
group by <eab_professional_judgement_ind>,
professional_judgement_indicator

union all

--eab_pell_eligible_ind
select 
'eab_pell_eligible_ind' as field_name,
<eab_pell_eligible_ind> as eab_standard_value,
pell_eligible_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Year Finaid Application].{fulfillment}> stud_yr_fin_aid
group by <eab_pell_eligible_ind>,
pell_eligible_indicator

union all

--eab_financial_sai
select 
'eab_financial_sai' as field_name,
<eab_financial_sai> as eab_standard_value,
fafsa_sai as partner_value,
count(*) as record_count
from <[Enroll360].[Student Year Finaid Application].{fulfillment}> stud_yr_fin_aid
group by <eab_financial_sai>,
fafsa_sai

union all

--eab_financial_need
select 
'eab_financial_need' as field_name,
<eab_financial_need> as eab_standard_value,
fafsa_need as partner_value,
count(*) as record_count
from <[Enroll360].[Student Year Finaid Application].{fulfillment}> stud_yr_fin_aid
group by <eab_financial_need>,
fafsa_need

union all

--eab_expense_group_key
select 
'eab_expense_group_key' as field_name,
<eab_expense_group_key> as eab_standard_value,
agg.partner_value,
sum(record_count) as record_count
from (
--expense_group_key
select 
expense_group_key as partner_value,
count(*) as record_count
from <[Enroll360].[Partner Expense Breakdown].{fulfillment}> stud_test_scores
group by expense_group_key
) agg
group by <eab_expense_group_key>,
partner_value

union all

--eab_independent_ind
select 
'eab_independent_ind' as field_name,
<eab_independent_ind> as eab_standard_value,
fafsa_dependency_status as partner_value,
count(*) as record_count
from <[Enroll360].[Student Year Finaid Application].{fulfillment}> stud_yr_fin_aid
group by <eab_independent_ind>,
fafsa_dependency_status

union all

/**************************************
Student Term SIS
***************************************/

--Athletic Recruit Indicator
select 
'eab_athletic_recruit_ind' as field_name,
<eab_athletic_recruit_ind_sis> as eab_standard_value,
athletic_recruit_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term SIS].{fulfillment}> stud_term
group by <eab_athletic_recruit_ind_sis>,
athletic_recruit_indicator

union all

--Inquiry Indicator
select 
'eab_inquiry_ind' as field_name,
<eab_inquiry_ind> as eab_standard_value,
inquiry_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term SIS].{fulfillment}> stud_term
group by <eab_inquiry_ind>,
inquiry_indicator

union all

-- Alternative Academic Rank
select 
'eab_alternative_academic_rank' as field_name,
<eab_alternative_academic_rank> as eab_standard_value,
alternative_academic_rank as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term SIS].{fulfillment}> stud_term
group by <eab_alternative_academic_rank>,
alternative_academic_rank

union all

--First Source
select 
'eab_first_source' as field_name,
null as eab_standard_value,
first_source as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term SIS].{fulfillment}> stud_term
group by first_source
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
from <[Enroll360].[Student Term SIS].{fulfillment}> stud_term
group by current_entry_term

union all

/**************************************
Student Term Application SIS
***************************************/

select  
current_entry_term as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term
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
stud_term.current_entry_year_as_academic_year as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term SIS].{fulfillment}> stud_term
group by stud_term.current_entry_year_as_academic_year
  
union all
  
--Student Term Application
select
stud_term_app.current_entry_year_as_academic_year as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term_app
group by stud_term_app.current_entry_year_as_academic_year  

union all

/*3-8-2023: added reference to Fin Aid table to pull any unique raw values to be converted - ccreekman*/
  
--Student Year Financial Aid
select
finaid.current_entry_year_as_academic_year as partner_value,
count(*) as record_count
from <[Enroll360].[Student Year Finaid Application].{fulfillment}> finaid
group by finaid.current_entry_year_as_academic_year  
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
from <[Enroll360].[Student Term SIS].{fulfillment}> stud_term
group by stud_term.student_type
) stype
group by <eab_student_type>,
partner_value

union all

--App Submit Ind
select 
'eab_app_submit_ind' as field_name,
<eab_app_submit_ind> as eab_standard_value,
APPLICATION_INCOMPLETE_INDICATOR as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term
group by <eab_app_submit_ind>,
APPLICATION_INCOMPLETE_INDICATOR

union all

--App Withdrawal Ind
select 
'eab_app_withdrawal_ind' as field_name,
<eab_app_withdrawal_ind> as eab_standard_value,
application_withdrawal_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term
group by <eab_app_withdrawal_ind>,
application_withdrawal_indicator

union all

--Deferral Ind
select 
'eab_deferral_ind' as field_name,
<eab_deferral_ind> as eab_standard_value,
deferral_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term
group by <eab_deferral_ind>,
deferral_indicator

union all

--Precipice Admission Bin Status
select 
'eab_precipice_admission_bin_status' as field_name,
<eab_precipice_admission_bin_status> as eab_standard_value,
precipice_admission_bin_status as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term
group by <eab_precipice_admission_bin_status>,
precipice_admission_bin_status

union all

--Free App Day Ind
select 
'eab_free_app_day_ind' as field_name,
<eab_free_app_day_ind> as eab_standard_value,
free_application_day_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term
group by <eab_free_app_day_ind>,
free_application_day_indicator

union all

--Major
select 
'eab_major' as field_name,
<eab_major> as eab_standard_value,
area_of_academic_interest_or_major_for_undergraduate_school as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term
group by <eab_major>,
area_of_academic_interest_or_major_for_undergraduate_school

union all

--eab_merit_award_at_admission
select 
'eab_merit_award_at_admission' as field_name,
<eab_merit_award_at_admission> as eab_standard_value,
merit_award_at_admission as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term
group by <eab_merit_award_at_admission>,
merit_award_at_admission

union all

/**************************************
Student Address SIS
***************************************/

--Address Type /*2025-04-28: updated to pull code values from both address datasets - ccreekman*/
/*select  
'address_type' as field_name,
<eab_address_type> as eab_standard_value,
address_type as partner_value,
count(*) as record_count
from <[Enroll360].[Student Address SIS].{fulfillment}> stud_addr
where address_line_1 is not null
group by <eab_address_type>,
address_type

union all*/

--Country
/*select 
'eab_country' as field_name,
null as eab_standard_value,
country as partner_value,
count(*) as record_count
from <[Enroll360].[Student Address SIS].{fulfillment}> stud_addr
group by country

union all*/

--Address Type
select  
'address_type' as field_name,
<eab_address_type> as eab_standard_value,
partner_value,
sum(record_count) as record_count
from (

--set 1
select 
address_type as partner_value,
count(*) as record_count
from <[Enroll360].[Student Address SIS].{fulfillment}> stud_addr
where address_line_1 is not null
group by address_type 

union all

--set 2
select 
calculated_field_8 as partner_value,
count(*) as record_count
from <[Enroll360].[Student Address SIS].{fulfillment}> stud_addr
where calculated_field_1 is not null
group by calculated_field_8
  
) agg

group by <eab_address_type>,
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
from <[Enroll360].[Student Address SIS].{fulfillment}> stud_addr
group by country

union all
  
--Address Set 2 Country
select  
calculated_field_7 as country,
count(*) as record_count
from <[Enroll360].[Student Address SIS].{fulfillment}> stud_addr
group by calculated_field_7
) countries
group by country

union all

/**************************************
Student Race SIS
***************************************/

--Race
select 
'eab_race' as field_name,
<eab_race> as eab_standard_value,
race as partner_value,
count(*) as record_count
from <[Enroll360].[Student Race SIS].{fulfillment}> stud
group by <eab_race>,
race

union all

/**************************************
Student Term Application SIS
***************************************/

--Withdrawal Indicator
select 
'eab_withdrawal_ind' as field_name,
<eab_withdrawal_ind> as eab_standard_value,
withdrawal_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term_app
group by <eab_withdrawal_ind>,
withdrawal_indicator

union all

--Waitlisted Indicator
select 
'eab_waitlist_ind' as field_name,
<eab_waitlist_ind> as eab_standard_value,
waitlist_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term_app
group by <eab_waitlist_ind>,
waitlist_indicator

union all

--Admit Indicator
select 
'eab_admit_ind' as field_name,
<eab_admit_ind> as eab_standard_value,
admit_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term_app
group by <eab_admit_ind>,
admit_indicator

union all

--Conditional Admit Indicator
select 
'eab_cond_admit_ind' as field_name,
<eab_cond_admit_ind> as eab_standard_value,
conditional_admit_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term_app
group by <eab_cond_admit_ind>,
conditional_admit_indicator

union all

--Fulltime or Parttime Status
select 
'eab_full_part_time' as field_name,
<eab_full_part_time> as eab_standard_value,
fulltime_or_parttime_status as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term_app
group by <eab_full_part_time>,
fulltime_or_parttime_status

union all

--Deposit Confirmed Indicator
select 
'eab_deposit_ind' as field_name,
<eab_deposit_ind> as eab_standard_value,
deposit_confirmed_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term_app
group by <eab_deposit_ind>,
deposit_confirmed_indicator

union all

--Test Optional Indicator
select 
'eab_test_optional_ind' as field_name,
<eab_test_optional_ind> as eab_standard_value,
test_optional_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term_app
group by <eab_test_optional_ind>,
test_optional_indicator

union all

--Application Started Indicator
select 
'eab_app_start_ind' as field_name,
<eab_app_start_ind> as eab_standard_value,
application_started_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term_app
group by <eab_app_start_ind>,
application_started_indicator

union all

--Application Incomplete Indicator
select 
'eab_app_incomplete_ind' as field_name,
<eab_app_incomplete_ind> as eab_standard_value,
application_incomplete_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term_app
group by <eab_app_incomplete_ind>,
application_incomplete_indicator

union all

--Application Completed Indicator
select 
'eab_app_complete_ind' as field_name,
<eab_app_complete_ind> as eab_standard_value,
application_completed_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term_app
group by <eab_app_complete_ind>,
application_completed_indicator

union all

--Application Type
select 
'eab_app_type' as field_name,
<eab_app_type> as eab_standard_value,
application_type as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term_app
group by <eab_app_type>,
application_type

union all

--Application Decision Type
select 
'eab_app_decision_type' as field_name,
<eab_app_decision_type> as eab_standard_value,
application_decision_type_code as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term_app
group by <eab_app_decision_type>,
application_decision_type_code

union all

--Campus Resident or Commuter Indicator
select 
'eab_commuter_ind' as field_name,
<eab_commuter_ind> as eab_standard_value,
campus_resident_or_commuter_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term_app
group by <eab_commuter_ind>,
campus_resident_or_commuter_indicator

union all

--Previous Deferral Indicator
select 
'eab_deferral_prev_ind' as field_name,
<eab_deferral_prev_ind> as eab_standard_value,
deferral_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term_app
group by <eab_deferral_prev_ind>,
deferral_indicator

union all

--Future Deferral Indicator
select 
'eab_deferral_future_ind' as field_name,
<eab_deferral_future_ind> as eab_standard_value,
deferral_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term_app
group by <eab_deferral_future_ind>,
deferral_indicator

union all

--Deny Indicator
select 
'eab_deny_ind' as field_name,
<eab_deny_ind> as eab_standard_value,
deny_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term_app
group by <eab_deny_ind>,
deny_indicator

union all

--Enrollment Indicator
select 
'eab_enroll_ind' as field_name,
<eab_enroll_ind> as eab_standard_value,
enrollment_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term_app
group by <eab_enroll_ind>,
enrollment_indicator

union all

--Housing Deposit Paid or Waived Indicator
select 
'eab_housing_deposit_ind' as field_name,
<eab_housing_deposit_ind> as eab_standard_value,
housing_deposit_paid_or_waived_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Application SIS].{fulfillment}> stud_term_app
group by <eab_housing_deposit_ind>,
housing_deposit_paid_or_waived_indicator

union all

/**************************************
Student High School SIS
***************************************/

--eab_self_reported_gpa
select 
'eab_self_reported_gpa' as field_name,
<eab_self_reported_gpa> as eab_standard_value,
self_reported_gpa as partner_value,
count(*) as record_count
from <[Enroll360].[Student High School SIS].{fulfillment}> stud_term_app
group by <eab_self_reported_gpa>,
self_reported_gpa

union all

--eab_official_gpa
select 
'eab_official_gpa' as field_name,
<eab_official_gpa> as eab_standard_value,
official_gpa as partner_value,
count(*) as record_count
from <[Enroll360].[Student High School SIS].{fulfillment}> stud_term_app
group by <eab_official_gpa>,
official_gpa

union all

--eab_awarded_gpa
select 
'eab_awarded_gpa' as field_name,
<eab_awarded_gpa> as eab_standard_value,
awarded_gpa as partner_value,
count(*) as record_count
from <[Enroll360].[Student High School SIS].{fulfillment}> stud_term_app
group by <eab_awarded_gpa>,
awarded_gpa

union all

--eab_hs_gpa_type
select 
'eab_hs_gpa_type' as field_name,
<eab_hs_gpa_type> as eab_standard_value,
eab_high_school_gpa_type as partner_value, --non-standard field not available 
count(*) as record_count
from <[Enroll360].[Student High School SIS].{fulfillment}> stud_term_app
group by <eab_hs_gpa_type>,
eab_high_school_gpa_type

union all

--eab_hs_gpa
select 
'eab_hs_gpa' as field_name,
<eab_hs_gpa> as eab_standard_value,
HIGH_SCHOOL_GPA as partner_value,
count(*) as record_count
from <[Enroll360].[Student High School SIS].{fulfillment}> stud_term_app
group by <eab_hs_gpa>,
HIGH_SCHOOL_GPA

union all

--eab_class_rank
select 
'eab_class_rank' as field_name,
<eab_class_rank> as eab_standard_value,
high_school_class_rank as partner_value,
count(*) as record_count
from <[Enroll360].[Student High School SIS].{fulfillment}> stud_term_app
group by <eab_class_rank>,
high_school_class_rank

union all
--Added as part of ESAE-8125
--eab_point_record_ind
select 
'eab_point_record_ind' as field_name,
<eab_point_record_ind> as eab_standard_value,
eab_point_record_indicator as partner_value,
count(*) as record_count
from <[Enroll360].[Student High School SIS].{fulfillment}> stud_term_app
group by <eab_point_record_ind>,
eab_point_record_indicator

union all

/**************************************
Student Term Attributes
***************************************/

-- Attribute Group
select 
'eab_attribute_group' as field_name,
<eab_attribute_group> as eab_standard_value,
source_attribute_type as partner_value, --non-standart field not available
count(*) as record_count
from <[Enroll360].[Student Term Attributes SIS].{fulfillment}> stud_term_app
group by <eab_attribute_group>,
source_attribute_type

union all

-- Attribute Type
select 
'eab_attribute_type' as field_name,
<eab_attribute_type> as eab_standard_value,
source_attribute_type as partner_value, --non-standart field not available
count(*) as record_count
from <[Enroll360].[Student Term Attributes SIS].{fulfillment}> stud_term_app
group by <eab_attribute_type>,
source_attribute_type

union all

-- Standard Value
select 
'eab_std_value' as field_name,
<eab_std_value> as eab_standard_value,
source_attribute_value as partner_value,
count(*) as record_count
from <[Enroll360].[Student Term Attributes SIS].{fulfillment}> stud_term_app
group by <eab_std_value>,
source_attribute_value

union all

/**************************************
Student Retention
***************************************/

--eab_enrolled_term
select 
'eab_enrolled_term' as field_name,
<eab_enrolled_term> as eab_standard_value,
term_enrolled as partner_value,
count(*) as record_count
from <[Enroll360].[Student Retention].{fulfillment}> stud_term_app
group by <eab_enrolled_term>,
term_enrolled

union all

/**************************************
Student Year Funds
***************************************/

--eab_fund_type
select 
'eab_fund_type' as field_name,
<eab_fund_type> as eab_standard_value,
fund_type as partner_value,
count(*) as record_count
from <[Enroll360].[Student Year Funds].{fulfillment}> stud_term_app
group by <eab_fund_type>,
fund_type

union all

--eab_simulation_fund_type
select 
'eab_simulation_fund_type' as field_name,
<eab_simulation_fund_type> as eab_standard_value,
fund_type as partner_value,
count(*) as record_count
from <[Enroll360].[Student Year Funds].{fulfillment}> stud_term_app
where LOWER(fund_type) = 'simulation'
group by <eab_simulation_fund_type>,
fund_type

union all

--eab_cohort_fund_type
select 
'eab_cohort_fund_type' as field_name,
<eab_cohort_fund_type> as eab_standard_value,
fund_type as partner_value,
count(*) as record_count
from <[Enroll360].[Student Year Funds].{fulfillment}> stud_term_app
where LOWER(fund_type) = 'cohort'
group by <eab_cohort_fund_type>,
fund_type

union all

--eab_summary_fund_type
select 
'eab_summary_fund_type' as field_name,
<eab_summary_fund_type> as eab_standard_value,
fund_type as partner_value,
count(*) as record_count
from <[Enroll360].[Student Year Funds].{fulfillment}> stud_term_app
where LOWER(fund_type) = 'summary'
group by <eab_summary_fund_type>,
fund_type

union all

--eab_discount_ind
Select
'eab_discount_ind' as field_name,
<eab_discount_ind> as eab_standard_value,
fund_type as partner_value,
count(*) as record_count
from <[Enroll360].[Student Year Funds].{fulfillment}> stud_term_app
group by <eab_discount_ind>,
fund_type