/**Source of Truth = this Query: Enroll360.Project Atlas Migration Checks.stud_race_eth*/

select 
prelim.partnercontact_id,
prelim.partner_system_name,
prelim.partnercontact_id_sis,
prelim.partner_system_name_sis,
prelim.partnercontact_id_crm,
prelim.partner_system_name_crm,
prelim.eab_student_type,
prelim.eab_entry_term,
prelim.eab_entry_year_academic,
prelim.eab_full_part_time,
stud.eab_ethnicity,
stud.eab_international_ind,
stud_race.agg_race,

case
when stud_race.agg_race ilike '%American Indian/Alaska Native%'
then true
else null
end as amer_ind,

case
when stud_race.agg_race ilike '%Asian/Asian American%'
then true
else null
end as asn,

case
when stud_race.agg_race ilike '%Black/African American%'
then true
else null
end as blk,

case
when stud_race.agg_race ilike '%Middle Eastern%'
then true
else null
end as middle_eastern,

case
when stud_race.agg_race ilike '%Multiracial%'
then true
else null
end as multiracial,

case
when stud_race.agg_race ilike '%Native Hawaiian/Pacific Islander%'
then true
else null
end as native_hawaiian,

case
when stud_race.agg_race ilike '%Other%'
then true
else null
end as other,

case
when stud_race.agg_race ilike '%Prefer Not to Respond%'
then true
else null
end as prefer_not_to_respond,

case
when stud_race.agg_race ilike '%White%'
then true
else null
end as wht,

case when (coalesce(stud.eab_ethnicity,'Unknown') = 'Hispanic or Latino/a/e/x' or coalesce(stud_race_agg.POC_rollup,'non-POC') = 'POC')
then 'POC'
else 'non-POC'
end as ofcolor_rollup_new,

case when coalesce(eab_international_ind,false) = false 
	and (coalesce(stud.eab_ethnicity,'Unknown') = 'Hispanic or Latino/a/e/x' or coalesce(stud_race_agg.POC_rollup,'non-POC') = 'POC')
then 'POC'
else 'non-POC'
end as ofcolor_rollup_legacy

from <[Enroll360].[Partner Configurations].{prelim_stud_data}> prelim
left join  <[Enroll360].[Student PDP Delivery].[Last Validated].{final}> stud
    on (prelim.partnercontact_id = stud.partnercontact_id
    and coalesce(prelim.partner_system_name,'') = coalesce(stud.partner_system_name,''))

left join (select 
		partnercontact_id, partner_system_name,
		listagg(eab_race,'|') as agg_race 
  		from (
			select distinct
			stud_race.partnercontact_id, stud_race.partner_system_name, stud_race.eab_race
			from <[Enroll360].[Student Race PDP Delivery].[Last Validated].{final}> stud_race
		) agg
	group by partnercontact_id, partner_system_name ) stud_race
    on (prelim.partnercontact_id = stud_race.partnercontact_id
    and coalesce(prelim.partner_system_name,'') = coalesce(stud_race.partner_system_name,''))

left join  <cte01_race> stud_race_agg
on (prelim.partnercontact_id = stud_race_agg.partnercontact_id
and coalesce(prelim.partner_system_name,'') = coalesce(stud_race_agg.partner_system_name,''))

/*left join (
select distinct
partnercontact_id, partner_system_name,
 case when coalesce(agg_race,'') like any ('%Black%','%Asian%','%Native%','%Middle%','%Multi%','%Other%') then 'POC' else 'non-POC' end as POC_rollup
from (
	select 
		partnercontact_id, partner_system_name,
		listagg(eab_race,'|') as agg_race 
  		from (
			select distinct
			stud_race.partnercontact_id, stud_race.partner_system_name, stud_race.eab_race
			from <[Enroll360].[Student Race PDP Delivery].[Last Validated].{final}> stud_race
		) agg
	group by partnercontact_id, partner_system_name
) agg2
) stud_race_agg
    on (prelim.partnercontact_id = stud_race_agg.partnercontact_id
    and coalesce(prelim.partner_system_name,'') = coalesce(stud_race_agg.partner_system_name,''))*/