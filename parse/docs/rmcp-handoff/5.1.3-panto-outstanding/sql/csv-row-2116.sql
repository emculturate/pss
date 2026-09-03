/**Source of Truth = this Query: Enroll360.Project Atlas Migration Checks.st_sta_data*/
/*this is the table that is used for subsequent migration check queries*/

(
select 
stud_term.partnercontact_id, stud_term.partner_system_name,
stud_term.partnercontact_id_sis, stud_term.partner_system_name_sis,
stud_term.partnercontact_id_crm, stud_term.partner_system_name_crm,
stud_term.eab_student_type,
stud_term.eab_entry_term,
stud_term.eab_entry_year_academic,
stud_term_app.eab_full_part_time,
stud_term_app.eab_app_decision_type,
stud_term.eab_alternative_academic_rank,
cast(coalesce(pdc.points,0) as decimal(18,2)) as alt_ac_rank_points,
cast(coalesce(pdc.adj_points,0) as decimal(18,2)) as adj_alt_ac_rank_points,
case when stud_term.eab_current_funnel_status = 'Withdraw' and stud_term_app.eab_admit_ind = true then 'Withdrawn Admit' 
  else stud_term.eab_current_funnel_status end as eab_current_funnel_status_adj,
stud.eab_campus_visit_ind,
stud_term_app.intake_dt as stapp_file_date
from <[Enroll360].[Student Term PDP Delivery].[Last Validated].{final}> stud_term

inner join <[Enroll360].[Student PDP Delivery].[Last Validated].{final}> stud
    on (stud_term.partnercontact_id = stud.partnercontact_id
    and coalesce(stud_term.partner_system_name,'') = coalesce(stud.partner_system_name,''))

left outer join 
    (select stud_term_app.*,
    row_number () over (partition by stud_term_app.partnercontact_id, 
	coalesce(stud_term_app.partner_system_name,''),
    coalesce(stud_term_app.eab_student_type,''),
    coalesce(stud_term_app.eab_entry_term,''),
    coalesce(stud_term_app.eab_entry_year_academic,0)
    order by stud_term_app.eab_enroll_ind desc nulls last,
         stud_term_app.eab_deny_ind desc nulls last,
         stud_term_app.eab_deferral_future_ind desc nulls last,
         stud_term_app.eab_deferral_prev_ind desc nulls last,
         stud_term_app.eab_deposit_ind desc nulls last,
         stud_term_app.eab_withdrawal_ind desc nulls last,
         stud_term_app.eab_admit_ind desc nulls last,
         stud_term_app.eab_cond_admit_ind desc nulls last,
		 stud_term_app.intake_dt) rn
    from <[Enroll360].[Student Term Application PDP Delivery].[Last Validated].{final}> as stud_term_app) as stud_term_app
    on (stud_term.partnercontact_id = stud_term_app.partnercontact_id
	and coalesce(stud_term.partner_system_name,'') = coalesce(stud_term_app.partner_system_name,'')
	and coalesce(stud_term.eab_student_type,'') = coalesce(stud_term_app.eab_student_type,'')
    and coalesce(stud_term.eab_entry_term,'') = coalesce(stud_term_app.eab_entry_term,'')
    and coalesce(stud_term.eab_entry_year_academic,0) = coalesce(stud_term_app.eab_entry_year_academic,0)
    and stud_term_app.rn = 1)

--	left join <cte05_acrk_display> pdc
	left join <[Enroll360].[Partner Configurations].{key_configs}> pdc
	on (pdc.configuration_type = 'alternative_academic_pointing'
	and coalesce(stud_term.eab_student_type,'') = coalesce(pdc.eab_student_type,'')
	and coalesce(try_to_number(stud_term.eab_entry_year_academic),0) = coalesce(try_to_number(pdc.eab_entry_year_academic),0)
	and coalesce(stud_term.eab_alternative_academic_rank,0) between coalesce(pdc.stored_value_exact_or_range_min,0) and coalesce(pdc.stored_value_range_max,99999999))

	join <cte00_pop_definition> pop_year
	on (pop_year.field_name = 'eab_entry_year_academic'
	and coalesce(try_to_number(stud_term.eab_entry_year_academic),0) = coalesce(try_to_number(pop_year.value),0))

	join <cte00_pop_definition> pop_term
	on (pop_term.field_name = 'eab_entry_term'
	and coalesce(stud_term.eab_entry_term,'') = coalesce(pop_term.value,''))

	join <cte00_pop_definition> pop_stype
	on (pop_stype.field_name = 'eab_student_type'
	and coalesce(stud_term.eab_student_type,'') = coalesce(pop_stype.value,''))

	join <cte00_pop_definition> pop_ac_load
	on (pop_ac_load.field_name = 'eab_full_part_time'
	and coalesce(stud_term_app.eab_full_part_time,'') = coalesce(pop_ac_load.value,''))

where 1=1 
/*filtering here to narrow the data set to admits and up*/
and eab_current_funnel_status_adj not in ('Prospect','Inquiry','Started Applicant','Incomplete Applicant','Completed Applicant','Waitlist','Deny','Withdraw')
)