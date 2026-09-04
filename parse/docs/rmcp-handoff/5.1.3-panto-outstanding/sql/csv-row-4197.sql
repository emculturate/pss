/*******************************************************************************
Check 1 - Fulfillment Feed Review
*******************************************************************************/

/**************************************
Check 1 - 2. Fulfillment counts
a. Check count of primary keys across
entities in comparison to the core entity
***************************************/

select 
count(distinct pd_core.primary_student_id || pd_core.student_type || pd_core.current_entry_term || pd_core.current_entry_year_as_academic_year) as total_record_count,
count(distinct pd_acad.primary_student_id || pd_acad.student_type || pd_acad.current_entry_term || pd_acad.current_entry_year_as_academic_year) as pd_acad_count,
count(distinct pd_addr.primary_student_id || pd_addr.student_type || pd_addr.current_entry_term || pd_addr.current_entry_year_as_academic_year) as pd_addr_count,
count(distinct pd_app_funn.primary_student_id || pd_app_funn.student_type || pd_app_funn.current_entry_term || pd_app_funn.current_entry_year_as_academic_year) as pd_app_funn_count,
count(distinct pd_cont.primary_student_id || pd_cont.student_type || pd_cont.current_entry_term || pd_cont.current_entry_year_as_academic_year) as pd_cont_count,
count(distinct pd_fin_aid.primary_student_id || pd_fin_aid.student_type || pd_fin_aid.current_entry_term || pd_fin_aid.current_entry_year_as_academic_year) as pd_fin_aid_count,
count(distinct pd_int.primary_student_id || pd_int.student_type || pd_int.current_entry_term || pd_int.current_entry_year_as_academic_year) as pd_int_count,
count(distinct pd_race.primary_student_id || pd_race.student_type || pd_race.current_entry_term || pd_race.current_entry_year_as_academic_year) as pd_race_count,
count(distinct pd_rship.primary_student_id || pd_rship.student_type || pd_rship.current_entry_term || pd_rship.current_entry_year_as_academic_year) as pd_rship_count,
count(distinct pd_test.primary_student_id || pd_test.student_type || pd_test.current_entry_term || pd_test.current_entry_year_as_academic_year) as pd_test_count
from <[enrollment_services].[Partner Data Core Record]> as pd_core
left outer join <[enrollment_services].[Partner Data Academic]> as pd_acad
on (coalesce(pd_core.primary_student_id,'') = coalesce(pd_acad.primary_student_id,'')
    	and	coalesce(pd_core.student_type,'') = coalesce(pd_acad.student_type,'')
	and coalesce(pd_core.current_entry_term,'') = coalesce(pd_acad.current_entry_term,'')
and coalesce(pd_core.current_entry_year_as_academic_year,'') = 
coalesce(pd_acad.current_entry_year_as_academic_year,''))
left outer join <[enrollment_services].[Partner Data Address]> as pd_addr
on (coalesce(pd_core.primary_student_id,'') = coalesce(pd_addr.primary_student_id,'')
and coalesce(pd_core.student_type,'') = coalesce(pd_addr.student_type,'')
and coalesce(pd_core.current_entry_term,'') = coalesce(pd_addr.current_entry_term,'')
and coalesce(pd_core.current_entry_year_as_academic_year,'') = 
coalesce(pd_addr.current_entry_year_as_academic_year,''))
left outer join <[enrollment_services].[Partner Data Application Funnel]> as pd_app_funn
on (coalesce(pd_core.primary_student_id,'') = coalesce(pd_app_funn.primary_student_id,'')
    	and coalesce(pd_core.student_type,'') = coalesce(pd_app_funn.student_type,'')
	and coalesce(pd_core.current_entry_term,'') = coalesce(pd_app_funn.current_entry_term,'')
	and coalesce(pd_core.current_entry_year_as_academic_year,'') = 
coalesce(pd_app_funn.current_entry_year_as_academic_year,''))
left outer join <[enrollment_services].[Partner Data Contact]> as pd_cont
on (coalesce(pd_core.primary_student_id,'') = coalesce(pd_cont.primary_student_id,'')
    	and coalesce(pd_core.student_type,'') = coalesce(pd_cont.student_type,'')
	and coalesce(pd_core.current_entry_term,'') = coalesce(pd_cont.current_entry_term,'')
	and coalesce(pd_core.current_entry_year_as_academic_year,'') = 
coalesce(pd_cont.current_entry_year_as_academic_year,''))
left outer join <[enrollment_services].[Partner Data Financial Aid]> as pd_fin_aid
on (coalesce(pd_core.primary_student_id,'') = coalesce(pd_fin_aid.primary_student_id,'')
    	and coalesce(pd_core.student_type,'') = coalesce(pd_fin_aid.student_type,'')
	and coalesce(pd_core.current_entry_term,'') = coalesce(pd_fin_aid.current_entry_term,'')
	and coalesce(pd_core.current_entry_year_as_academic_year,'') = 
coalesce(pd_fin_aid.current_entry_year_as_academic_year,''))
left outer join <[enrollment_services].[Partner Data Interaction]> as pd_int
on (coalesce(pd_core.primary_student_id,'') = coalesce(pd_int.primary_student_id,'')
    	and coalesce(pd_core.student_type,'') = coalesce(pd_int.student_type,'')
	and coalesce(pd_core.current_entry_term,'') = coalesce(pd_int.current_entry_term,'')
	and coalesce(pd_core.current_entry_year_as_academic_year,'') = 
coalesce(pd_int.current_entry_year_as_academic_year,''))
left outer join <[enrollment_services].[Partner Data Race and Ethnicity]> as pd_race
on (coalesce(pd_core.primary_student_id,'') = coalesce(pd_race.primary_student_id,'')
    	and coalesce(pd_core.student_type,'') = coalesce(pd_race.student_type,'')
	and coalesce(pd_core.current_entry_term,'') = coalesce(pd_race.current_entry_term,'')
	and coalesce(pd_core.current_entry_year_as_academic_year,'') = 
coalesce(pd_race.current_entry_year_as_academic_year,''))
left outer join <[enrollment_services].[Partner Data Relationship]> as pd_rship
on (coalesce(pd_core.primary_student_id,'') = coalesce(pd_rship.primary_student_id,'')
    	and coalesce(pd_core.student_type,'') = coalesce(pd_rship.student_type,'')
	and coalesce(pd_core.current_entry_term,'') = coalesce(pd_rship.current_entry_term,'')
	and coalesce(pd_core.current_entry_year_as_academic_year,'') = 
coalesce(pd_rship.current_entry_year_as_academic_year,''))
left outer join <[enrollment_services].[Partner Data Test Scores]> as pd_test
on (coalesce(pd_core.primary_student_id,'') = coalesce(pd_test.primary_student_id,'')
    	and coalesce(pd_core.student_type,'') = coalesce(pd_test.student_type,'')
	and coalesce(pd_core.current_entry_term,'') = coalesce(pd_test.current_entry_term,'')
	and coalesce(pd_core.current_entry_year_as_academic_year,'') = 
coalesce(pd_test.current_entry_year_as_academic_year,''))

/**************************************
Check 1 - 2. Fulfillment counts
b. Check total record count for each entity

*To run, uncomment and comment to run the desired table
***************************************/
					
select count(*) as record_count
from <[enrollment_services].[Partner Data Core Record]> as pd_core
--from <[enrollment_services].[Partner Data Academic]> as pd_acad
--from <[enrollment_services].[Partner Data Address]> as pd_addr
--from <[enrollment_services].[Partner Data Application Funnel]> as pd_app_funn
--from <[enrollment_services].[Partner Data Contact]> as pd_cont
--from <[enrollment_services].[Partner Data Financial Aid]> as pd_fin_aid
--from <[enrollment_services].[Partner Data Interaction]> as pd_int
--from <[enrollment_services].[Partner Data Race and Ethnicity]> as pd_race
--from <[enrollment_services].[Partner Data Relationship]> as pd_rship
--from <[enrollment_services].[Partner Data Test Scores]> as pd_test


 
/*******************************************************************************
Check 2 - Transformation Review
*******************************************************************************/

/**************************************
1. Check funnel counts
																		
a. Compare EC stats between Panto and 
CEC table
***************************************/

--view counts by funnel status rollup
select funnel_status_eab, count(*) as record_count
from <[enrollment_services].[Partner Data Core Record]> pd_core
group by funnel_status_eab

--view counts scross the entire funnel
select 
sum(case when pd_core.prospect_indicator is not null then 1 else 0 end) as prospect_indicator,
sum(case when pd_core.inquiry_indicator_eab <> 'Unmapped Value' 
then to_number(pd_core.inquiry_indicator_eab) else null end) as inquiry_indicator_eab,
sum(case when pd_app_funn.application_incomplete_indicator_eab <> 'Unmapped Value' 
then to_number(pd_app_funn.application_incomplete_indicator_eab) else null end) 
as application_incomplete_indicator_eab,
sum(case when pd_app_funn.application_completed_indicator_eab <> 'Unmapped Value' 
then to_number(pd_app_funn.application_completed_indicator_eab) else null end) 
as application_completed_indicator_eab,
sum(case when pd_app_funn.admit_indicator_eab <> 'Unmapped Value' 
then to_number(pd_app_funn.admit_indicator_eab) else null end) as admit_indicator_eab,
sum(case when pd_app_funn.deposit_confirmed_indicator_eab <> 'Unmapped Value' 
then to_number(pd_app_funn.deposit_confirmed_indicator_eab) else null end) as 
deposit_confirmed_indicator_eab,
sum(case when pd_app_funn.enrollment_indicator_eab <> 'Unmapped Value' 
then to_number(pd_app_funn.enrollment_indicator_eab) else null end) as enrollment_indicator_eab
from <[enrollment_services].[Partner Data Core Record]> as pd_core
left outer join <[enrollment_services].[Partner Data Application Funnel].{final}> pd_app_funn
on (coalesce(pd_core.primary_student_id,'') = coalesce(pd_app_funn.primary_student_id,'')
    	and coalesce(pd_core.student_type_eab,'') = coalesce(pd_app_funn.student_type_eab,'')
	and coalesce(pd_core.entry_term_eab,'') = coalesce(pd_app_funn.entry_term_eab,'')
	and coalesce(pd_core.entry_year_as_academic_year_eab,'') = 
coalesce(pd_app_funn.entry_year_as_academic_year_eab,''))
where pd_core.student_type_eab = 'Freshman'
and pd_core.entry_term_eab = 'Fall'
and pd_core.entry_year_as_academic_year_eab = '2021'

											
 
/**************************************
2. Conversion and attribute count review; the latter are loaded to ACS
Contact entity
*To run, uncomment and comment to run the desired attributes
***************************************/
--Check conversions

select email_opt_out_eab, email_opt_out, count(*) as record_count
--sms_opt_in_eab, sms_opt_in, phone_opt_out_eab, phone_opt_out,\
from <[enrollment_services].[Partner Data Contact]> as pd_cont
group by email_opt_out_eab, email_opt_out
--phone_opt_out_eab, phone_opt_out, sms_opt_in_eab, sms_opt_in

--Check email counts

select 
case when email_address is not null then 'Populated' else 'Not populated' end as email_address, 
count(*) as record_count
from <[enrollment_services].[Partner Data Contact]> as pd_cont
group by case when email_address is not null then 'Populated' else 'Not populated' end


/**************************************
2. Attribute count review; this is loaded into ACS
Relationship entity
***************************************/
select 
case when parent_or_guardian_1_email_address is not null then 'Populated' else 'Not populated' end 
as parent_or_guardian_1_email_address,
case when parent_or_guardian_2_email_address is not null then 'Populated' else 'Not populated' end 
as parent_or_guardian_2_email_address,
count(*) as record_count
--select *
from <[enrollment_services].[Partner Data Relationship]> as pd_rship
group by 
case when parent_or_guardian_1_email_address is not null then 'Populated' else 'Not populated' end,
case when parent_or_guardian_2_email_address is not null then 'Populated' else 'Not populated' end
																	
/**************************************
2. Attribute count review; this is loaded into ACS
Academic entity
***************************************/
select 
case when high_school_ceeb_code is not null then 'Populated' else 'Not populated' end 
as high_school_ceeb_code,
case when high_school_grad_year is not null then 'Populated' else 'Not populated' end 
as high_school_grad_year,
case when primary_undergraduate_institution_ceeb_code is not null 
then 'Populated' else 'Not populated' end as primary_undergraduate_institution_ceeb_code,
count(*) as record_count
from <[enrollment_services].[Partner Data Academic]> as pd_acad
group by 
case when high_school_ceeb_code is not null then 'Populated' else 'Not populated' end,
case when high_school_grad_year is not null then 'Populated' else 'Not populated' end,
case when primary_undergraduate_institution_ceeb_code is not null then 'Populated' 
else 'Not populated' end
order by high_school_ceeb_code, high_school_grad_year, primary_undergraduate_institution_ceeb_code

/**************************************
2. Attribute count review; this is loaded into ACS
Financial aid entity
***************************************/
select 
case when financial_aid_student_id is not null then 'Populated' else 'Not populated' end 
as financial_aid_student_id,
count(*) as record_count
from <[enrollment_services].[Partner Data Financial Aid]> as pd_fin_aid
group by 
case when financial_aid_student_id is not null then 'Populated' else 'Not populated' end

/**************************************
2. Conversion count review
Interaction entity
***************************************/
select campus_visit_flag_eab, campus_visit_flag, count(*) as record_count
from <[enrollment_services].[Partner Data Interaction]> as pd_int
group by campus_visit_flag_eab, campus_visit_flag

/**************************************
2. Attribute count review
Test scores entity
***************************************/
select act_best_composite_score, count(*) as record_count
from <[enrollment_services].[Partner Data Test Scores]> as pd_test
group by act_best_composite_score
order by act_best_composite_score

select sat_r_best_1600_composite_score, count(*) as record_count
from <[enrollment_services].[Partner Data Test Scores]> as pd_test
group by sat_r_best_1600_composite_score
order by sat_r_best_1600_composite_score

/**************************************
2. Conversion count review
Address entity
*To run, uncomment and comment to run the desired attributes
***************************************/
select mail_opt_out_eab, mail_opt_out, count(*) as record_count
--mailing_country_eab, mailing_country,
from <[enrollment_services].[Partner Data Address]> as pd_addr
group by mail_opt_out_eab, mail_opt_out
--mailing_country_eab, mailing_country

 
/**************************************
2. Conversion count review
Race and ethnicity entity
*To run, uncomment and comment to run the desired attributes
***************************************/
select american_indian_or_alaska_native_eab, american_indian_or_alaska_native, race,
--asian_eab, asian, race,
--black_or_african_american_eab, black_or_african_american, race,
--middle_eastern_eab, middle_eastern, race,
--multiracial_eab, multiracial, race,
--prefer_not_to_respond_eab, prefer_not_to_respond, race,
--other_race_eab, other_race, race,
--native_hawaiian_or_other_pacific_islander_eab, native_hawaiian_or_other_pacific_islander, race,
--unknown_eab, unknown, race,
--white_eab, white, race,
--ethnicity_eab, ethnicity, hispanic,
count(*) as record_count
from <[enrollment_services].[Partner Data Race and Ethnicity]> as pd_race
group by american_indian_or_alaska_native_eab, american_indian_or_alaska_native, race
--asian_eab, asian, race
--black_or_african_american_eab, black_or_african_american, race
--middle_eastern_eab, middle_eastern, race
--multiracial_eab, multiracial, race
--prefer_not_to_respond_eab, prefer_not_to_respond, race
--other_race_eab, other_race, race
--native_hawaiian_or_other_pacific_islander_eab, native_hawaiian_or_other_pacific_islander, race
--unknown_eab, unknown, race
--white_eab, white, race
--ethnicity_eab, ethnicity, hispanic

/**************************************
2. Conversion count review
Core record entity
*To run, uncomment and comment to run the desired attributes
***************************************/
select entry_term_eab, current_entry_term,
--entry_year_as_academic_year_eab, current_entry_year_as_academic_year,
--international_indicator_eab, international_indicator_code,
--preferred_gender_eab, preferred_gender,
--inquiry_indicator_eab, inquiry_indicator,
--inquiry_first_source_code_eab, inquiry_first_source_code,
--athletic_recruit_eab, athletic_recruit,
--alumni_relationship_eab, alumni_relationship,
--first_generation_indicator_eab, first_generation,
--military_service_or_veteran_indicator_eab, military_service_or_veteran_status,
--opt_out_indicator_general_eab, opt_out_indicator_general,
count(*) as record_count
from <[enrollment_services].[Partner Data Core Record]> as pd_core
group by entry_term_eab, current_entry_term
--entry_year_as_academic_year_eab, current_entry_year_as_academic_year
--international_indicator_eab, international_indicator_code
--preferred_gender_eab, preferred_gender
--inquiry_indicator_eab, inquiry_indicator
--inquiry_first_source_code_eab, inquiry_first_source_code
--athletic_recruit_eab, athletic_recruit
--alumni_relationship_eab, alumni_relationship
--first_generation_indicator_eab, first_generation
--military_service_or_veteran_indicator_eab, military_service_or_veteran_status
--opt_out_indicator_general_eab, opt_out_indicator_general

/**************************************
2. Conversion count review
App funnel entity
*To run, uncomment and comment to run the desired attributes
***************************************/
Select application_type_eab, application_type,
--application_decision_type_eab, application_decision_type_code,
--fulltime_or_parttime_status_eab, fulltime_or_parttime_status,
--campus_resident_or_commuter_indicator_eab, campus_resident_or_commuter_indicator,
--application_started_indicator_eab, application_started_indicator,
--application_incomplete_indicator_eab, application_incomplete_indicator,
--application_completed_indicator_eab, application_completed_indicator,
--admit_indicator_eab, admit_indicator,
--conditional_admit_indicator_eab, conditional_admit_indicator,
--deny_indicator_eab, deny_indicator,
--future_deferral_indicator_eab, previous_deferral_indicator_eab, deferral_indicator,
--waitlisted_indicator_eab, waitlist_indicator,
--deposit_confirmed_indicator_eab, deposit_confirmed_indicator,
--housing_deposit_paid_or_waived_indicator_eab, housing_deposit_paid_or_waived_indicator,
--enrollment_indicator_eab, enrollment_indicator,
--withdrawal_indicator_eab, withdrawal_indicator,
--test_optional_eab, test_optional,
count(*) as record_count
from <[enrollment_services].[Partner Data Application Funnel]> as pd_app_funn
group by application_type_eab, application_type
--application_decision_type_eab, application_decision_type_code
--fulltime_or_parttime_status_eab, fulltime_or_parttime_status
--campus_resident_or_commuter_indicator_eab, campus_resident_or_commuter_indicator
--application_started_indicator_eab, application_started_indicator
--application_incomplete_indicator_eab, application_incomplete_indicator
--application_completed_indicator_eab, application_completed_indicator
--admit_indicator_eab, admit_indicator
--conditional_admit_indicator_eab, conditional_admit_indicator
--deny_indicator_eab, deny_indicator
--future_deferral_indicator_eab, previous_deferral_indicator_eab, deferral_indicator
--waitlisted_indicator_eab, waitlist_indicator
--deposit_confirmed_indicator_eab, deposit_confirmed_indicator
--housing_deposit_paid_or_waived_indicator_eab, housing_deposit_paid_or_waived_indicator
--enrollment_indicator_eab, enrollment_indicator
--withdrawal_indicator_eab, withdrawal_indicator
--test_optional_eab, test_optional

 
/**************************************
2. Sample record review
***************************************/
select 
pd_core.primary_student_id, 
pd_core.entry_term_eab, 
pd_core.entry_year_as_academic_year_eab,
pd_core.student_type_eab, 
pd_core.birth_date, 
pd_core.first_name, 
pd_core.last_name,
pd_core.opt_out_indicator_general_eab, 
pd_app_funn.application_type_eab, 
pd_app_funn.area_of_academic_interest_or_major_for_undergraduate_school, pd_app_funn.admit_indicator_eab,
pd_app_funn.admit_date, 
pd_app_funn.withdrawal_indicator_eab, 
pd_app_funn.withdrawal_date,
pd_app_funn.fulltime_or_parttime_status_eab, 
pd_cont.email_address, 
pd_addr.mailing_address_line_1,
pd_addr.mailing_country_eab, 
pd_rship.parent_or_guardian_1_email_address, 
pd_rship.admissions_counselor_first_name, 
pd_test.sat_r_best_1600_composite_score,
pd_test.act_best_composite_score, 
pd_int.campus_visit_flag, 
pd_int.campus_visit_date,
pd_fin_aid.financial_aid_student_id, 
pd_fin_aid.fafsa_submitted_eab, pd_acad.high_school_ceeb_code,
pd_acad.high_school_gpa, 
pd_race.ethnicity_eab, 
pd_race.race
from <[enrollment_services].[Partner Data Core Record]> as pd_core
left outer join <[enrollment_services].[Partner Data Academic]> as pd_acad
on (pd_core.primary_student_id = pd_acad.primary_student_id
    	and coalesce(pd_core.student_type_eab,'') = coalesce(pd_acad.student_type_eab,'')
	and coalesce(pd_core.entry_term_eab,'') = coalesce(pd_acad.entry_term_eab,'')
	and coalesce(pd_core.entry_year_as_academic_year_eab,'') = 
coalesce(pd_acad.entry_year_as_academic_year_eab,''))
left outer join <[enrollment_services].[Partner Data Address]> as pd_addr
on (pd_core.primary_student_id = pd_addr.primary_student_id
    	and coalesce(pd_core.student_type_eab,'') = coalesce(pd_addr.student_type_eab,'')
	and coalesce(pd_core.entry_term_eab,'') = coalesce(pd_addr.entry_term_eab,'')
	and coalesce(pd_core.entry_year_as_academic_year_eab,'') = 
coalesce(pd_addr.entry_year_as_academic_year_eab,''))
left outer join <[enrollment_services].[Partner Data Application Funnel]> as pd_app_funn
on (pd_core.primary_student_id = pd_app_funn.primary_student_id
    	and coalesce(pd_core.student_type_eab,'') = coalesce(pd_app_funn.student_type_eab,'')
	and coalesce(pd_core.entry_term_eab,'') = coalesce(pd_app_funn.entry_term_eab,'')
	and coalesce(pd_core.entry_year_as_academic_year_eab,'') = 
coalesce(pd_app_funn.entry_year_as_academic_year_eab,''))
left outer join <[enrollment_services].[Partner Data Contact]> as pd_cont
on (pd_core.primary_student_id = pd_cont.primary_student_id
    	and coalesce(pd_core.student_type_eab,'') = coalesce(pd_cont.student_type_eab,'')
	and coalesce(pd_core.entry_term_eab,'') = coalesce(pd_cont.entry_term_eab,'')
	and coalesce(pd_core.entry_year_as_academic_year_eab,'') = 
coalesce(pd_cont.entry_year_as_academic_year_eab,''))
left outer join <[enrollment_services].[Partner Data Financial Aid]> as pd_fin_aid
on (pd_core.primary_student_id = pd_fin_aid.primary_student_id
    	and coalesce(pd_core.student_type_eab,'') = coalesce(pd_fin_aid.student_type_eab,'')
	and coalesce(pd_core.entry_term_eab,'') = coalesce(pd_fin_aid.entry_term_eab,'')
	and coalesce(pd_core.entry_year_as_academic_year_eab,'') = 
coalesce(pd_fin_aid.entry_year_as_academic_year_eab,''))
left outer join <[enrollment_services].[Partner Data Interaction]> as pd_int
on (pd_core.primary_student_id = pd_int.primary_student_id
    	and coalesce(pd_core.student_type_eab,'') = coalesce(pd_int.student_type_eab,'')
	and coalesce(pd_core.entry_term_eab,'') = coalesce(pd_int.entry_term_eab,'')
	and coalesce(pd_core.entry_year_as_academic_year_eab,'') = 
coalesce(pd_int.entry_year_as_academic_year_eab,''))
left outer join <[enrollment_services].[Partner Data Race and Ethnicity]> as pd_race
on (pd_core.primary_student_id = pd_race.primary_student_id
    	and coalesce(pd_core.student_type_eab,'') = coalesce(pd_race.student_type_eab,'')
	and coalesce(pd_core.entry_term_eab,'') = coalesce(pd_race.entry_term_eab,'')
	and coalesce(pd_core.entry_year_as_academic_year_eab,'') = 
coalesce(pd_race.entry_year_as_academic_year_eab,''))
left outer join <[enrollment_services].[Partner Data Relationship]> as pd_rship
on (pd_core.primary_student_id = pd_rship.primary_student_id
    	and coalesce(pd_core.student_type_eab,'') = coalesce(pd_rship.student_type_eab,'')
	and coalesce(pd_core.entry_term_eab,'') = coalesce(pd_rship.entry_term_eab,'')
	and coalesce(pd_core.entry_year_as_academic_year_eab,'') = 
coalesce(pd_rship.entry_year_as_academic_year_eab,''))
left outer join <[enrollment_services].[Partner Data Test Scores]> as pd_test
on (pd_core.primary_student_id = pd_test.primary_student_id
    	and coalesce(pd_core.student_type_eab,'') = coalesce(pd_test.student_type_eab,'')
	and coalesce(pd_core.entry_term_eab,'') = coalesce(pd_test.entry_term_eab,'')
	and coalesce(pd_core.entry_year_as_academic_year_eab,'') = 
coalesce(pd_test.entry_year_as_academic_year_eab,''))