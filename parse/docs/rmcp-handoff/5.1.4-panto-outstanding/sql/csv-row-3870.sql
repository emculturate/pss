/*2025-07-07: added partnercontact_id, partner_system_name per ESANALYTIC-50473; corrected where statement - ccreekman*/
WITH student_term_crm AS 
(
	SELECT
  		xw.*,
		stu_term.primary_student_id AS eab_student_id_crm,
		CAST('primary' AS VARCHAR) AS eab_student_id_type_crm,
		stu_term.eab_entry_year_academic AS eab_entry_year_academic_crm,
		stu_term.eab_entry_term	AS eab_entry_term_crm,
		stu_term.curr_entry_term AS curr_entry_term_crm,
		stu_term.curr_entry_year_academic AS curr_entry_year_academic_crm,
		stu_term.student_type AS student_type_crm,
		stu_term.inquiry_ind AS inquiry_ind_crm,
		stu_term.recruitment_status AS recruitment_status_crm,
		stu_term.state_resident AS state_resident_crm,
		stu_term.first_source AS	first_source_crm,
		stu_term.first_source_dt AS first_source_dt_crm,
		stu_term.athletic_recruit_ind AS athletic_recruit_ind_crm,
		stu_term.athletic_recruit_sport AS athletic_recruit_sport_crm,
		stu_term.years_employed AS years_employed_crm,
		stu_term.academic_rating AS academic_rating_crm,
		stu_term.interest_rating AS interest_rating_crm,
		stu_term.student_level AS student_level_crm,
		stu_term.curr_entry_year_calendar AS curr_entry_year_calendar_crm,
		stu_term.eab_inquiry_ind AS eab_inquiry_ind_crm,
		stu_term.eab_athletic_recruit_ind AS eab_athletic_recruit_ind_crm,
		stu_term.eab_current_funnel_status AS eab_current_funnel_status_crm,
		stu_term.eab_first_source AS eab_first_source_crm,
		stu_term.eab_student_type AS eab_student_type_crm,
		stu_term.intake_dt AS intake_dt_crm,
		stu_term.primary_student_id AS primary_student_id_crm
--select *
	FROM <[Enroll360].[Student Term].[Last Validated].{final}> as stu_term

/***************************************************************************/
left join (
select partnercontact_id_01, partner_system_name_01, partnercontact_id_02, partner_system_name_02 
from (
  select distinct *, row_number() over(partition by partnercontact_id_01, partner_system_name_01, partnercontact_id_02, partner_system_name_02 order by intake_dt desc) as rn
  from
	(select xw.source_partnercontact_id as partnercontact_id_01,
 	xw.source_partner_system_name as partner_system_name_01,
 	xw.target_partnercontact_id as partnercontact_id_02,
 	xw.target_partner_system_name as partner_system_name_02,
 	xw.intake_dt
	from <[Enroll360].[Partner ID Xwalk].{final}> AS xw
	union all 
	select xw.target_partnercontact_id as partnercontact_id_01,
 	xw.target_partner_system_name as partner_system_name_01,
 	xw.source_partnercontact_id as partnercontact_id_02,
 	xw.source_partner_system_name as partner_system_name_02,
 	xw.intake_dt
	from <[Enroll360].[Partner ID Xwalk].{final}> AS xw
	union all
	select xw.source_partnercontact_id as partnercontact_id_01,
 	xw.source_partner_system_name as partner_system_name_01,
 	xw.source_partnercontact_id as partnercontact_id_02,
 	xw.source_partner_system_name as partner_system_name_02,
 	xw.intake_dt
	from <[Enroll360].[Partner ID Xwalk].{final}> AS xw
	union all 
	select xw.target_partnercontact_id as partnercontact_id_01,
 	xw.target_partner_system_name as partner_system_name_01,
 	xw.target_partnercontact_id as partnercontact_id_02,
 	xw.target_partner_system_name as partner_system_name_02,
 	xw.intake_dt
 	from <[Enroll360].[Partner ID Xwalk].{final}> AS xw
	) xw_full 
  ) xw
where rn = 1
)xw

on 
--(stud.primary_student_id = xw.partnercontact_id_01 and xw.partner_system_name_01 = 'sis_banner_1')
(stu_term.partnercontact_id = xw.partnercontact_id_01 and coalesce(stu_term.partner_system_name,'') =  coalesce(xw.partner_system_name_01,''))
/***************************************************************************/
)

SELECT * FROM (

SELECT 
	stud_term_crm.eab_student_id_crm,
    stu_term_sis.eab_student_id AS eab_student_id_sis,
    <eab_student_id> AS eab_student_id,
	<eab_student_id> AS partnercontact_id,

    stud_term_crm.eab_student_id_type_crm,
	stu_term_sis.eab_student_id_type AS eab_student_id_type_sis,
    <eab_student_id_type> AS eab_student_id_type,
	<eab_student_id_type> AS partner_system_name,
	
	stud_term_crm.primary_student_id_crm AS primary_student_id,
	stu_term_sis.alternative_academic_rank,
	to_number(stu_term_sis.eab_alternative_academic_rank) AS eab_alternative_academic_rank,

	stud_term_crm.eab_entry_year_academic_crm AS eab_entry_year_academic_crm,
	stu_term_sis.eab_entry_year_academic AS eab_entry_year_academic_sis,
	<eab_entry_year_academic> AS eab_entry_year_academic,
	
	stud_term_crm.eab_entry_term_crm,
	stu_term_sis.eab_entry_term AS eab_entry_term_sis,
	<eab_entry_term> AS eab_entry_term,
	
	stud_term_crm.curr_entry_term_crm,
	stu_term_sis.curr_entry_term AS curr_entry_term_sis,
	<curr_entry_term> AS curr_entry_term,
	
	stud_term_crm.curr_entry_year_academic_crm,
	stu_term_sis.curr_entry_year_academic AS curr_entry_year_academic_sis,
	<curr_entry_year_academic> AS curr_entry_year_academic,
	
	stud_term_crm.student_type_crm,
	stu_term_sis.student_type AS student_type_sis,
	<student_type> AS student_type,
	
	stud_term_crm.inquiry_ind_crm,
	stu_term_sis.inquiry_ind AS inquiry_ind_sis,
	<inquiry_ind> AS inquiry_ind,
	
	stud_term_crm.recruitment_status_crm,
	stu_term_sis.recruitment_status AS recruitment_status_sis,
	<recruitment_status> AS recruitment_status,
	
	stud_term_crm.state_resident_crm,
	stu_term_sis.state_resident AS state_resident_sis,
	<state_resident> AS state_resident,
	
	stud_term_crm.first_source_crm,
	stu_term_sis.first_source AS first_source_sis,
	<first_source> AS first_source,
	
	to_date(stud_term_crm.first_source_dt_crm) AS first_source_dt_crm,
	to_date(stu_term_sis.first_source_dt) AS first_source_dt_sis,
	to_date(<first_source_dt>) AS first_source_dt,
	
	stud_term_crm.athletic_recruit_ind_crm,
	stu_term_sis.athletic_recruit_ind AS athletic_recruit_ind_sis,
	<athletic_recruit_ind> AS athletic_recruit_ind,
	
	stud_term_crm.athletic_recruit_sport_crm,
	stu_term_sis.athletic_recruit_sport AS athletic_recruit_sport_sis,
	<athletic_recruit_sport> AS athletic_recruit_sport,
	
	stud_term_crm.years_employed_crm,
	stu_term_sis.years_employed AS years_employed_sis,
	<years_employed> AS years_employed,
	
	stud_term_crm.academic_rating_crm,
	stu_term_sis.academic_rating AS academic_rating_sis,
	<academic_rating> AS academic_rating,
	
	stud_term_crm.interest_rating_crm,
	stu_term_sis.interest_rating AS interest_rating_sis,
	<interest_rating> AS interest_rating,
	
	stud_term_crm.student_level_crm,
	stu_term_sis.student_level AS student_level_sis,
	<student_level> AS student_level,
	
	stud_term_crm.curr_entry_year_calendar_crm,
	stu_term_sis.curr_entry_year_calendar AS curr_entry_year_calendar_sis,
	<curr_entry_year_calendar> AS curr_entry_year_calendar,
	
	to_boolean(stud_term_crm.eab_inquiry_ind_crm) AS eab_inquiry_ind_crm,
	to_boolean(stu_term_sis.eab_inquiry_ind) AS eab_inquiry_ind_sis,
	to_boolean(<eab_inquiry_ind>) AS eab_inquiry_ind,
	
	to_boolean(stud_term_crm.eab_athletic_recruit_ind_crm) AS eab_athletic_recruit_ind_crm,
	to_boolean(stu_term_sis.eab_athletic_recruit_ind) AS eab_athletic_recruit_ind_sis,
	to_boolean(<eab_athletic_recruit_ind>) AS eab_athletic_recruit_ind,
	
	stud_term_crm.eab_current_funnel_status_crm,
	stu_term_sis.eab_current_funnel_status AS eab_current_funnel_status_sis,
	<eab_current_funnel_status> AS eab_current_funnel_status,
	
	stud_term_crm.eab_first_source_crm,
	stu_term_sis.eab_first_source AS eab_first_source_sis,
	<eab_first_source> AS eab_first_source,
	
	stud_term_crm.eab_student_type_crm,
	stu_term_sis.eab_student_type AS eab_student_type_sis,
	<eab_student_type> AS eab_student_type,
	
	to_timestamp(stud_term_crm.intake_dt_crm) AS intake_dt_crm ,
	to_timestamp(stu_term_sis.intake_dt) AS intake_dt_sis,
	to_timestamp(<intake_dt>) AS intake_dt
	
FROM <[Enroll360].[Student Term SIS].[Last Validated].{final}> as stu_term_sis 
 
FULL OUTER JOIN student_term_crm AS stud_term_crm
			ON stu_term_sis.partnercontact_id = stud_term_crm.partnercontact_id_02
			AND stu_term_sis.partner_system_name = stud_term_crm.partner_system_name_02
			AND COALESCE(stu_term_sis.eab_entry_year_academic, 0) = COALESCE(stud_term_crm.eab_entry_year_academic_CRM, 0)
 			AND COALESCE(stu_term_sis.eab_student_type, '') = COALESCE(stud_term_crm.eab_student_type_crm, '')
			AND COALESCE(stu_term_sis.eab_entry_term,'') = COALESCE(stud_term_crm.eab_entry_term_crm,'')
WHERE (stu_term_sis.eab_student_id IS NOT NULL AND stu_term_sis.eab_student_id_type IS NOT NULL) OR (stud_term_crm.eab_student_id_crm IS NOT NULL AND stud_term_crm.eab_student_id_type_crm IS NOT NULL)
) final
where 1=1
  and (EAB_STUDENT_ID_SIS IS NOT NULL AND eab_student_id_crm IS NOT NULL)
  AND EAB_CURRENT_FUNNEL_STATUS_SIS <> EAB_CURRENT_FUNNEL_STATUS_CRM