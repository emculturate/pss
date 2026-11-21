select * from
(
select distinct
nullif(trim(regexp_replace(agg2.eab_student_id,'"|[\\000-\\037\\177]','')),'') as eab_student_id,
nullif(trim(regexp_replace(agg2.eab_student_id_type,'"|[\\000-\\037\\177]','')),'') as eab_student_id_type,
nullif(trim(regexp_replace(agg2.partnercontact_id,'"|[\\000-\\037\\177]','')),'') as partnercontact_id,
nullif(trim(regexp_replace(agg2.partner_system_name,'"|[\\000-\\037\\177]','')),'') as partner_system_name,
agg2.curr_entry_year_academic,
agg2.eab_entry_year_academic,
agg2.student_type, /*not passed to PDP currently*/
agg2.eab_student_type, /*not passed to PDP currently*/
agg2.fin_aid_intent_ind,
agg2.FAFSA_submitted_ind,
agg2.eab_fafsa_submitted_ind,
agg2.FAFSA_submitted_dt,
agg2.first_FAFSA_submitted_dt,
agg2.current_FAFSA_submitted_dt,
agg2.fin_aid_award_sent_ind,
agg2.eab_fin_aid_award_sent_ind,
agg2.fin_aid_award_sent_dt,
agg2.css_profile_submitted_ind,
agg2.eab_css_profile_submitted_ind,
agg2.css_profile_submitted_dt,
agg2.total_attend_cost,
agg2.cost_of_attendance,
agg2.tuition,
agg2.tuition_and_fees,
agg2.fafsa_sai,
agg2.fafsa_need,
agg2.fafsa_student_sai,
agg2.fafsa_parent_sai,
agg2.fafsa_dependency_status,
agg2.eab_fafsa_dependency,
agg2.fafsa_student_net_worth,
agg2.fafsa_parent_net_worth,
agg2.fafsa_student_agi,
agg2.fafsa_parent_agi,
agg2.aid_package_status,
agg2.aid_package_complete_ind,
agg2.eab_aid_package_complete_ind,
agg2.aid_review_status,
agg2.fin_aid_app_status,
agg2.fin_aid_app_complete_ind,
agg2.eab_fin_aid_app_complete_ind,
agg2.fin_aid_complete_dt,
agg2.fafsa_student_farm,
agg2.fafsa_student_bus,
agg2.fafsa_student_cash,
agg2.fafsa_student_invest,
agg2.fafsa_student_bus_farm,
agg2.fafsa_parent_farm,
agg2.fafsa_parent_bus,
agg2.fafsa_parent_cash,
agg2.fafsa_parent_invest,
agg2.fafsa_parent_bus_farm,
agg2.verification_ind,
agg2.eab_verification_ind,
agg2.verification_status,
agg2.eab_verification_status,
agg2.current_fin_aid_award_sent_dt,
agg2.number_of_award_letters,
agg2.number_in_college,
agg2.number_in_family,
agg2.parent_marital_status,
agg2.eab_single_parent_ind,
agg2.fafsa_student_wages,
agg2.fafsa_student_salary,
agg2.fafsa_student_income,
agg2.fafsa_student_adjusted_bus_farm_eqty,
agg2.fafsa_parent_wages,
agg2.fafsa_parent_salary,
agg2.fafsa_parent_income,
agg2.fafsa_parent_adjusted_bus_farm_eqty,
agg2.par1_grade_level,
agg2.par2_grade_level,
agg2.fafsa_change_dt,
agg2.housing_app_status,
agg2.tuition_residency,
agg2.professional_judgement_ind,
agg2.eab_professional_judgement_ind,
agg2.pell_eligible_ind,
agg2.eab_pell_eligible_ind,
agg2.appeal_ind,
agg2.eab_appeal_ind,
agg2.css_profile_sai,
agg2.css_profile_need,
agg2.css_profile_stu_sai,
agg2.css_profile_par_sai,
agg2.aid_app_ind, /*not passed to PDP currently*/
agg2.ti_grant, /*not passed to PDP currently*/
agg2.tgrant, /*not passed to PDP currently*/
agg2.eab_aid_app_ind,
agg2.eab_financial_sai,
agg2.eab_financial_need,
agg2.expense_group_key, /*not passed to PDP currently*/
agg2.eab_expense_group_key,
agg2.eab_independent_ind,
agg2.intake_dt

from (
	select agg.*,
	row_number() over (partition by 
	nullif(trim(regexp_replace(agg.partnercontact_id,'"|[\\000-\\037\\177]','')),''), 
	nullif(trim(regexp_replace(agg.partner_system_name,'"|[\\000-\\037\\177]','')),''), 
	agg.eab_entry_year_academic
	order by agg.intake_dt desc nulls last, agg.eab_student_type
	) rn

from (



select distinct 
/*Student Term Fields*/
nullif(trim(regexp_replace(stud_term.eab_student_id, '"|[\\000-\\037\\177]', '')), '') as eab_student_id,  
nullif(trim(regexp_replace(stud_term.eab_student_id_type, '"|[\\000-\\037\\177]', '')), '') as eab_student_id_type, 
nullif(trim(regexp_replace(stud_term.partnercontact_id, '"|[\\000-\\037\\177]', '')), '') as partnercontact_id,  
nullif(trim(regexp_replace(stud_term.partner_system_name, '"|[\\000-\\037\\177]', '')), '') as partner_system_name, 
stud_term.curr_entry_year_academic,
stud_term.eab_entry_year_academic,
stud_term.student_type,/*not passed to PDP currently*/
stud_term.eab_student_type,/*not passed to PDP currently*/

/*Student Year Finaid fields*/
stud_yr_fin_aid.intent_to_file_for_financial_aid_indicator as fin_aid_intent_ind,
stud_yr_fin_aid.fafsa_submitted as FAFSA_submitted_ind,
try_to_timestamp(substring(stud_yr_fin_aid.fafsa_submitted_date,1,19)) as FAFSA_submitted_dt,
try_to_timestamp(substring(stud_yr_fin_aid.first_fafsa_submitted_date,1,19)) as first_FAFSA_submitted_dt,
try_to_timestamp(substring(stud_yr_fin_aid.most_recent_fafsa_submitted_date,1,19)) as current_FAFSA_submitted_dt,
stud_yr_fin_aid.financial_aid_award_sent_indicator as fin_aid_award_sent_ind,
try_to_timestamp(substring(stud_yr_fin_aid.financial_aid_award_sent_date,1,19)) as fin_aid_award_sent_dt,
stud_yr_fin_aid.css_profile_submitted_indicator as css_profile_submitted_ind,
try_to_timestamp(substring(stud_yr_fin_aid.css_profile_submitted_date,1,19)) as css_profile_submitted_dt,
stud_yr_fin_aid.cost_of_attendance as cost_of_attendance,
stud_yr_fin_aid.total_attend_cost,
stud_yr_fin_aid.tuition,
stud_yr_fin_aid.tuition_and_fees,
stud_yr_fin_aid.fafsa_sai,
stud_yr_fin_aid.fafsa_need,
stud_yr_fin_aid.fafsa_student_sai,
stud_yr_fin_aid.fafsa_parent_sai,
stud_yr_fin_aid.fafsa_dependency_status,
stud_yr_fin_aid.fafsa_student_net_worth,
stud_yr_fin_aid.fafsa_parent_net_worth,
stud_yr_fin_aid.fafsa_student_agi,
stud_yr_fin_aid.fafsa_parent_agi,
stud_yr_fin_aid.aid_package_status,
stud_yr_fin_aid.aid_package_complete_indicator as aid_package_complete_ind,
stud_yr_fin_aid.aid_review_status,
stud_yr_fin_aid.financial_aid_application_status as fin_aid_app_status,
stud_yr_fin_aid.financial_aid_application_complete_indicator as fin_aid_app_complete_ind,
try_to_timestamp(substring(stud_yr_fin_aid.financial_aid_complete_date,1,19)) as fin_aid_complete_dt,
stud_yr_fin_aid.fafsa_student_farm,
stud_yr_fin_aid.fafsa_student_business as fafsa_student_bus,
stud_yr_fin_aid.fafsa_student_cash,
stud_yr_fin_aid.fafsa_student_invest,
stud_yr_fin_aid.fafsa_student_business_farm as fafsa_student_bus_farm,
stud_yr_fin_aid.fafsa_parent_farm,
stud_yr_fin_aid.fafsa_parent_business as fafsa_parent_bus,
stud_yr_fin_aid.fafsa_parent_cash,
stud_yr_fin_aid.fafsa_parent_invest,
stud_yr_fin_aid.fafsa_parent_business_farm as fafsa_parent_bus_farm,
stud_yr_fin_aid.verification_indicator as verification_ind,
stud_yr_fin_aid.verification_status,
try_to_timestamp(substring(stud_yr_fin_aid.current_financial_aid_award_sent_date,1,19)) as current_fin_aid_award_sent_dt,
stud_yr_fin_aid.number_of_award_letters,
stud_yr_fin_aid.number_in_college,
stud_yr_fin_aid.number_in_family,
stud_yr_fin_aid.parent_marital_status,
stud_yr_fin_aid.fafsa_student_wages,
stud_yr_fin_aid.fafsa_student_salary,
stud_yr_fin_aid.fafsa_student_income,
stud_yr_fin_aid.fafsa_student_adjusted_business_farm_equity as fafsa_student_adjusted_bus_farm_eqty,
stud_yr_fin_aid.fafsa_parent_wages,
stud_yr_fin_aid.fafsa_parent_salary,
stud_yr_fin_aid.fafsa_parent_income,
stud_yr_fin_aid.fafsa_parent_adjusted_business_farm_equity as fafsa_parent_adjusted_bus_farm_eqty,
stud_yr_fin_aid.parent_1_grade_level as par1_grade_level,
stud_yr_fin_aid.parent_2_grade_level as par2_grade_level,
try_to_timestamp(substring(stud_yr_fin_aid.fafsa_change_date,1,19)) as fafsa_change_dt,
stud_yr_fin_aid.housing_application_status as housing_app_status,
stud_yr_fin_aid.tuition_residency,
stud_yr_fin_aid.professional_judgement_indicator as professional_judgement_ind,
stud_yr_fin_aid.pell_eligible_indicator as pell_eligible_ind,
stud_yr_fin_aid.appeal_indicator as appeal_ind,
stud_yr_fin_aid.eab_appeal_indicator as eab_appeal_ind,
stud_yr_fin_aid.css_profile_sai,
stud_yr_fin_aid.css_profile_need,
stud_yr_fin_aid.css_profile_student_sai css_profile_stu_sai,
stud_yr_fin_aid.css_profile_parent_sai as css_profile_par_sai,	
calc_grants.ti_grant,
calc_grants.tgrant,

case when pcm_fin_aid_app_complete_ind.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_fin_aid_app_complete_ind.eab_standard_value)
end as eab_fin_aid_app_complete_ind,

case when pcm_verification_ind.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_verification_ind.eab_standard_value)
end as eab_verification_ind,

case when pcm_verification_status.eab_standard_value = 'Unmapped Value'
then null
else pcm_verification_status.eab_standard_value
end as eab_verification_status,
				
case when pcm_professional_judgement_ind.eab_standard_value = 'Unmapped Value'
then null
else pcm_professional_judgement_ind.eab_standard_value
end as eab_professional_judgement_ind,

<fao_calc.eab_pell_eligible_ind> as eab_pell_eligible_ind,

case when pcm_aid_package_complete_ind.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_aid_package_complete_ind.eab_standard_value)
end as eab_aid_package_complete_ind,

stud_yr_fin_aid.aid_application_indicator as aid_app_ind,
<eab_aid_app_ind> as eab_aid_app_ind,

/*edited logic to use direct variable*/														 
CAST(<eab_financial_sai> as DECIMAL (12,2)) as eab_financial_sai,

/*edited logic to use direct variable*/	
CAST(<eab_financial_need>  as DECIMAL (12,2)) as eab_financial_need,
				
stud_yr_fin_aid.expense_group_key,
<eab_expense_group_key> as eab_expense_group_key,						 

case when pcm_independent_ind.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_independent_ind.eab_standard_value)
end as eab_independent_ind,

case when pcm_fafsa_dependency.eab_standard_value = 'Unmapped Value'
then null
else pcm_fafsa_dependency.eab_standard_value
end as eab_fafsa_dependency,

case when pcm_css_profile.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_css_profile.eab_standard_value)
end as eab_css_profile_submitted_ind,

case when pcm_fafsa_sub.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_fafsa_sub.eab_standard_value)
end as eab_FAFSA_submitted_ind,

case when pcm_fa_award_sent.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_fa_award_sent.eab_standard_value)
end as eab_fin_aid_award_sent_ind,

case when pcm_single_parent.eab_standard_value = 'Unmapped Value'
then try_to_boolean(false)
else try_to_boolean(pcm_single_parent.eab_standard_value)
end as eab_single_parent_ind,

try_to_timestamp(stud_yr_fin_aid.intake_dt) as intake_dt

from <[Enroll360].[Student Term SIS].{final}> stud_term

--<join_extension.Student Year Finaid Application.test>

/*******join extension start******/
left join (
	select stud_yr_fin_aid.*,
	case when pcm_entry_year.eab_standard_value = 'Unmapped Value'
	then null
	else try_to_number(pcm_entry_year.eab_standard_value)
	end as eab_entry_year_academic,

	case when pcm_stud_type.eab_standard_value = 'Unmapped Value'
	then 'Unknown'
	else pcm_stud_type.eab_standard_value
	end as eab_student_type

	from <[Enroll360].[Student Year Finaid Application].{fulfillment}> as stud_yr_fin_aid
	left outer join <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_entry_year
	on (pcm_entry_year.field_name = 'eab_entry_year_academic'
	and coalesce(stud_yr_fin_aid.current_entry_year_as_academic_year,'') = coalesce(pcm_entry_year.partner_value,''))

	left outer join <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_stud_type
	on (pcm_stud_type.field_name = 'eab_student_type'
	and coalesce(stud_yr_fin_aid.student_type,'') = coalesce(pcm_stud_type.partner_value,''))
) stud_yr_fin_aid
/*edit syfa join fields as needed*/
on (stud_term.partnercontact_id = stud_yr_fin_aid.eab_student_id /*change linking ID fields as needed*/
and coalesce(stud_term.partner_system_name,'') = coalesce(stud_yr_fin_aid.eab_student_id_type,'') /*change linking ID fields as needed*/
--and coalesce(stud_term.eab_student_type,'') = coalesce(stud_yr_fin_aid.eab_student_type,'') /*comment out if SYFA does not have student type populated*/
and coalesce(stud_term.eab_entry_year_academic,0) = coalesce(stud_yr_fin_aid.eab_entry_year_academic,0) /*comment out if SYFA does not have entry year populated*/
)
/*conversion joins*/
left outer join <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_css_profile
on (pcm_css_profile.field_name = 'eab_css_profile_submitted_ind'
and coalesce(stud_yr_fin_aid.css_profile_submitted_indicator,'') = coalesce(pcm_css_profile.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_fafsa_sub
on (pcm_fafsa_sub.field_name = 'eab_FAFSA_submitted_ind'
and coalesce(stud_yr_fin_aid.fafsa_submitted,'') = coalesce(pcm_fafsa_sub.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_fa_award_sent
on (pcm_fa_award_sent.field_name = 'eab_fin_aid_award_sent_ind'
and coalesce(stud_yr_fin_aid.financial_aid_award_sent_indicator,'') = coalesce(pcm_fa_award_sent.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_fin_aid_app_complete_ind
on (pcm_fin_aid_app_complete_ind.field_name = 'eab_fin_aid_app_complete_ind'
and coalesce(stud_yr_fin_aid.financial_aid_application_complete_indicator,'') = coalesce(pcm_fin_aid_app_complete_ind.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_verification_ind
on (pcm_verification_ind.field_name = 'eab_verification_ind'
and coalesce(stud_yr_fin_aid.verification_indicator,'') = coalesce(pcm_verification_ind.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_verification_status
on (pcm_verification_status.field_name = 'eab_verification_status'
and coalesce(stud_yr_fin_aid.verification_status,'') = coalesce(pcm_verification_status.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_professional_judgement_ind
on (pcm_professional_judgement_ind.field_name = 'eab_professional_judgement_ind'
and coalesce(stud_yr_fin_aid.professional_judgement_indicator,'') = coalesce(pcm_professional_judgement_ind.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_pell_eligible_ind
on (pcm_pell_eligible_ind.field_name = 'eab_pell_eligible_ind'
and coalesce(stud_yr_fin_aid.pell_eligible_indicator,'') = coalesce(pcm_pell_eligible_ind.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_aid_package_complete_ind
on (pcm_aid_package_complete_ind.field_name = 'eab_aid_package_complete_ind'
and coalesce(stud_yr_fin_aid.aid_package_complete_indicator,'') = coalesce(pcm_aid_package_complete_ind.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_independent_ind
on (pcm_independent_ind.field_name = 'eab_independent_ind'
and coalesce(stud_yr_fin_aid.eab_independent_indicator,'') = coalesce(pcm_independent_ind.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_fafsa_dependency
on (pcm_fafsa_dependency.field_name = 'eab_fafsa_dependency'
and coalesce(stud_yr_fin_aid.fafsa_dependency_status,'') = coalesce(pcm_fafsa_dependency.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_expense_group
on (pcm_expense_group.field_name = 'eab_expense_group_key'
and coalesce(stud_yr_fin_aid.expense_group_key,'') = coalesce(pcm_expense_group.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_single_parent
on (pcm_single_parent.field_name = 'eab_single_parent_ind'
and coalesce(stud_yr_fin_aid.parent_marital_status,'') = coalesce(pcm_single_parent.partner_value,''))
																								
/***standard calculated tables - do not delete. Can reorder in join extension as needed***/
--Partner Expense Breakdown incl Budget
left join <[Enroll360].[Partner Expense Breakdown].{fulfillment}> AS peb
on (<eab_expense_group_key> = peb.EXPENSE_GROUP_KEY									
	and stud_term.eab_entry_year_academic = peb.ENTRY_YEAR_AS_ACADEMIC_YEAR_EAB)

--Partner ID Xwalk
left join (
	select *
	from <[Enroll360].[Partner ID Xwalk].[Last Validated].{final}> id_xwalk
	where id_xwalk.source_partner_system_name = 'primary'
) id_xwalk
on stud_yr_fin_aid.eab_student_id = id_xwalk.target_partnercontact_id
and stud_yr_fin_aid.eab_student_id_type = id_xwalk.target_partner_system_name

--TGRANT & TIGRANT & total_funds per student note - TGRANT query variable references other variables:
  --<fao_calc.query.total_fund_amount_per_fund_type> | <fao_calc.tgrant> | <fao_calc.ti_grant>
left join (
	<fao_calc.query.tgrant_tigrant>
	) calc_grants
on (stud_term.partnercontact_id = calc_grants.partnercontact_id
and coalesce(stud_term.partner_system_name,'') =  coalesce(calc_grants.partner_system_name,'')
--and coalesce(stud_term.eab_student_type,'') = coalesce(calc_grants.eab_student_type,'')
and coalesce(stud_term.eab_entry_year_academic,0) = coalesce(calc_grants.eab_entry_year_academic,0))

/*for <eab_pell_eligible_ind> - join to table of students with non-imputed non-zero Pell Grants*/
left join (
  select partnercontact_id, partner_system_name, eab_entry_year_academic, 
  sum(agg_fund_amount) as agg_fund_amount
  from 
	(<fao_calc.query.total_fund_amount_per_fund_type>) fund_agg
	where eab_fund_type = 'pell_grant' and coalesce(imputed_flag,false) = false
	group by partnercontact_id, partner_system_name, eab_entry_year_academic
) pell_fund_agg
on (stud_term.partnercontact_id = pell_fund_agg.partnercontact_id
and coalesce(stud_term.partner_system_name,'') = coalesce(pell_fund_agg.partner_system_name,'')
and coalesce(stud_term.eab_entry_year_academic,0) = coalesce(pell_fund_agg.eab_entry_year_academic,0))

--Student Year Funds (aggregate fund types per student per type)
left join (
	<fao_calc.query.total_fund_amount_per_fund_type>
	) agg_funds_per_type
on (stud_term.partnercontact_id = agg_funds_per_type.partnercontact_id
and coalesce(stud_term.partner_system_name,'') =   coalesce(agg_funds_per_type.partner_system_name,'')
--and coalesce(stud_term.eab_student_type,'') = coalesce(agg_funds_per_type.eab_student_type,'')
and coalesce(stud_term.eab_entry_year_academic,0) = coalesce(agg_funds_per_type.eab_entry_year_academic,0))

/**********************/

)agg

) agg2

where agg2.rn = 1
and coalesce(agg2.curr_entry_year_academic,'') ||
coalesce(to_varchar(agg2.eab_fafsa_submitted_ind),'') ||
coalesce(to_varchar(agg2.eab_fin_aid_award_sent_ind),'') ||
coalesce(to_varchar(agg2.eab_css_profile_submitted_ind),'') ||
coalesce(agg2.cost_of_attendance,'') ||
coalesce(agg2.tuition,'') ||
coalesce(agg2.tuition_and_fees,'') ||
coalesce(agg2.fafsa_sai,'') ||
coalesce(agg2.fafsa_need,'') ||
coalesce(agg2.fafsa_student_sai,'') ||
coalesce(agg2.fafsa_parent_sai,'') ||
coalesce(agg2.fafsa_dependency_status,'') ||
coalesce(agg2.eab_fafsa_dependency,'') ||
coalesce(agg2.fafsa_student_net_worth,'') ||
coalesce(agg2.fafsa_parent_net_worth,'') ||
coalesce(agg2.fafsa_student_agi,'') ||
coalesce(agg2.fafsa_parent_agi,'') ||
coalesce(agg2.aid_package_status,'') ||
coalesce(to_varchar(agg2.aid_package_complete_ind),'') ||
coalesce(to_varchar(agg2.eab_aid_package_complete_ind),'') ||
coalesce(agg2.aid_review_status,'') ||
coalesce(agg2.fin_aid_app_status,'') ||
coalesce(to_varchar(agg2.fin_aid_app_complete_ind),'') ||
coalesce(to_varchar(agg2.eab_fin_aid_app_complete_ind),'') ||
coalesce(to_varchar(agg2.fin_aid_complete_dt),'') ||
coalesce(agg2.fafsa_student_farm,'') ||
coalesce(agg2.fafsa_student_bus,'') ||
coalesce(agg2.fafsa_student_cash,'') ||
coalesce(agg2.fafsa_student_invest,'') ||
coalesce(agg2.fafsa_student_bus_farm,'') ||
coalesce(agg2.fafsa_parent_farm,'') ||
coalesce(agg2.fafsa_parent_bus,'') ||
coalesce(agg2.fafsa_parent_cash,'') ||
coalesce(agg2.fafsa_parent_invest,'') ||
coalesce(agg2.fafsa_parent_bus_farm,'') ||
coalesce(to_varchar(agg2.verification_ind),'') ||
coalesce(to_varchar(agg2.eab_verification_ind),'') ||
coalesce(agg2.verification_status,'') ||
coalesce(agg2.eab_verification_status,'') ||
coalesce(to_varchar(agg2.current_fin_aid_award_sent_dt),'') ||
coalesce(agg2.number_of_award_letters,'') ||
coalesce(agg2.number_in_college,'') ||
coalesce(agg2.number_in_family,'') ||
coalesce(agg2.parent_marital_status,'') ||
coalesce(agg2.fafsa_student_wages,'') ||
coalesce(agg2.fafsa_student_salary,'') ||
coalesce(agg2.fafsa_student_income,'') ||
coalesce(agg2.fafsa_student_adjusted_bus_farm_eqty,'') ||
coalesce(agg2.fafsa_parent_wages,'') ||
coalesce(agg2.fafsa_parent_salary,'') ||
coalesce(agg2.fafsa_parent_income,'') ||
coalesce(agg2.fafsa_parent_adjusted_bus_farm_eqty,'') ||
coalesce(agg2.par1_grade_level,'') ||
coalesce(agg2.par2_grade_level,'') ||
coalesce(to_varchar(agg2.fafsa_change_dt),'') ||
coalesce(agg2.housing_app_status,'') ||
coalesce(agg2.tuition_residency,'') ||
coalesce(to_varchar(agg2.professional_judgement_ind),'') ||
coalesce(to_varchar(agg2.eab_professional_judgement_ind),'') ||
coalesce(to_varchar(agg2.pell_eligible_ind),'') ||
coalesce(to_varchar(agg2.eab_pell_eligible_ind),'') ||
coalesce(to_varchar(agg2.appeal_ind),'') ||
coalesce(to_varchar(agg2.eab_appeal_ind),'') ||
coalesce(agg2.css_profile_sai,'') ||
coalesce(agg2.css_profile_need,'') ||
coalesce(agg2.css_profile_stu_sai,'') ||
coalesce(agg2.css_profile_par_sai,'') ||
coalesce(to_varchar(agg2.eab_aid_app_ind),'') ||
coalesce(to_varchar(agg2.eab_financial_sai),'') ||
coalesce(to_varchar(agg2.eab_financial_need),'') ||
coalesce(agg2.eab_expense_group_key,'') ||
coalesce(to_varchar(agg2.eab_independent_ind),'') ||
coalesce(to_varchar(agg2.intake_dt),'') ||
coalesce(to_varchar(agg2.fin_aid_intent_ind),'') ||
coalesce(to_varchar(agg2.FAFSA_submitted_ind),'') ||
coalesce(to_varchar(agg2.FAFSA_submitted_dt),'') ||
coalesce(to_varchar(agg2.first_FAFSA_submitted_dt),'') ||
coalesce(to_varchar(agg2.current_FAFSA_submitted_dt),'') ||
coalesce(to_varchar(agg2.fin_aid_award_sent_ind),'') ||
coalesce(to_varchar(agg2.fin_aid_award_sent_dt),'') ||
coalesce(to_varchar(agg2.css_profile_submitted_ind),'') ||
coalesce(to_varchar(agg2.css_profile_submitted_dt),'') ||
coalesce(to_varchar(agg2.aid_app_ind),'') ||
coalesce(to_varchar(agg2.expense_group_key),'') ||
coalesce(to_varchar(agg2.total_attend_cost),'') <> ''
) agg3
where coalesce(agg3.partnercontact_id,'') <> ''
and coalesce(agg3.partner_system_name,'') <> ''