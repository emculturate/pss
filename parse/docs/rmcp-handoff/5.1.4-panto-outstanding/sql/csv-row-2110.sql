/**Source of Truth = this Query: Enroll360.Project Atlas Migration Checks.prelim_stud_data_sliced*/
/*2026-04-30: aligned with <cte09_prelim_stud_data_sliced>*/
/*this table has duplicate records - 1 per slice category*/

select 
  prelim.*,
  null AS slice,
  'All' as category
  from <[Enroll360].[Partner Configurations].{prelim_stud_data}> prelim

union all

select 
  prelim.*,
  stud.eab_pref_gender AS slice,
  'Gender' as category
  from <[Enroll360].[Partner Configurations].{prelim_stud_data}> prelim
left join  <[Enroll360].[Student PDP Delivery].[Last Validated].{final}> stud
    on (prelim.partnercontact_id = stud.partnercontact_id
    and coalesce(prelim.partner_system_name,'') = coalesce(stud.partner_system_name,''))

union all

select 
  prelim.*,
  stud.eab_ethnicity AS slice,
  'Ethnicity' as category
  from <[Enroll360].[Partner Configurations].{prelim_stud_data}> prelim
left join  <[Enroll360].[Student PDP Delivery].[Last Validated].{final}> stud
    on (prelim.partnercontact_id = stud.partnercontact_id
    and coalesce(prelim.partner_system_name,'') = coalesce(stud.partner_system_name,''))

union all

select 
  prelim.*,
  coalesce(stud_race.POC_rollup,'non-POC') AS slice,
  'Race' as category
  from <[Enroll360].[Partner Configurations].{prelim_stud_data}> prelim
left join  <cte01_race> stud_race
    on (prelim.partnercontact_id = stud_race.partnercontact_id
    and coalesce(prelim.partner_system_name,'') = coalesce(stud_race.partner_system_name,''))


union all

select 
  prelim.*,
  case when model_population = true then 'Model' else 'Non Model' end AS slice,
  'ModelPop' as category
  from <[Enroll360].[Partner Configurations].{prelim_stud_data}> prelim