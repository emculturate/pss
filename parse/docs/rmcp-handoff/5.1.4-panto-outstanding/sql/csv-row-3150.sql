with race_data as (
select distinct
nullif(trim(regexp_replace(stud_race.primary_student_id,'"|[\\000-\\037\\177]','')),'') as primary_student_id,
nullif(trim(stud_race.race),'') as race,
  
case when pcm_race.eab_standard_value = 'Unmapped Value'
then 'Unknown'
else pcm_race.eab_standard_value
end as eab_race,
  
case when pcm_race_amer_indian.eab_standard_value = 'Unmapped Value'
then null
else pcm_race_amer_indian.eab_standard_value
end as eab_race_amer_indian,

case when pcm_race_asian.eab_standard_value = 'Unmapped Value'
then null
else pcm_race_asian.eab_standard_value
end as eab_race_asian,

case when pcm_race_black.eab_standard_value = 'Unmapped Value'
then null
else pcm_race_black.eab_standard_value
end as eab_race_black,

case when pcm_race_native_hawaiian.eab_standard_value = 'Unmapped Value'
then null
else pcm_race_native_hawaiian.eab_standard_value
end as eab_race_native_hawaiian,

case when pcm_race_white.eab_standard_value = 'Unmapped Value'
then null
else pcm_race_white.eab_standard_value
end as eab_race_white,

case when pcm_race_middle_eastern.eab_standard_value = 'Unmapped Value'
then null
else pcm_race_middle_eastern.eab_standard_value
end as eab_race_middle_eastern,

case when pcm_race_multiracial.eab_standard_value = 'Unmapped Value'
then null
else pcm_race_multiracial.eab_standard_value
end as eab_race_multiracial,
  
case when pcm_race_other.eab_standard_value = 'Unmapped Value'
then null
else pcm_race_other.eab_standard_value
end as eab_race_other,

case when pcm_race_prefer_not_respond.eab_standard_value = 'Unmapped Value'
then null
else pcm_race_prefer_not_respond.eab_standard_value
end as eab_race_prefer_not_respond,

case when pcm_race_unknown.eab_standard_value = 'Unmapped Value'
then null
else pcm_race_unknown.eab_standard_value
end as eab_race_unknown,
stud_race.intake_date as intake_dt			

from <[Enroll360].[Student Race].{fulfillment_ug_inquiry}> as stud_race
  
left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_race
on (pcm_race.field_name = 'eab_race'
and coalesce(stud_race.race,'') = coalesce(pcm_race.partner_value,''))
  
left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_race_amer_indian
on (pcm_race_amer_indian.field_name = 'eab_race_amer_indian'
and coalesce(stud_race.calculated_field_1,'') = coalesce(pcm_race_amer_indian.partner_value,''))
  
left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_race_asian
on (pcm_race_asian.field_name = 'eab_race_asian'
and coalesce(stud_race.calculated_field_2,'') = coalesce(pcm_race_asian.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_race_black
on (pcm_race_black.field_name = 'eab_race_black'
and coalesce(stud_race.calculated_field_3,'') = coalesce(pcm_race_black.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_race_native_hawaiian
on (pcm_race_native_hawaiian.field_name = 'eab_race_native_hawaiian'
and coalesce(stud_race.calculated_field_4,'') = coalesce(pcm_race_native_hawaiian.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_race_white
on (pcm_race_white.field_name = 'eab_race_white'
and coalesce(stud_race.calculated_field_5,'') = coalesce(pcm_race_white.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_race_middle_eastern
on (pcm_race_middle_eastern.field_name = 'eab_race_middle_eastern'
and coalesce(stud_race.calculated_field_6,'') = coalesce(pcm_race_middle_eastern.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_race_multiracial
on (pcm_race_multiracial.field_name = 'eab_race_multiracial'
and coalesce(stud_race.calculated_field_7,'') = coalesce(pcm_race_multiracial.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_race_other
on (pcm_race_other.field_name = 'eab_race_other'
and coalesce(stud_race.calculated_field_8,'') = coalesce(pcm_race_other.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_race_prefer_not_respond
on (pcm_race_prefer_not_respond.field_name = 'eab_race_prefer_not_respond'
and coalesce(stud_race.calculated_field_9,'') = coalesce(pcm_race_prefer_not_respond.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_race_unknown
on (pcm_race_unknown.field_name = 'eab_race_unknown'
and coalesce(stud_race.calculated_field_10,'') = coalesce(pcm_race_unknown.partner_value,''))
														  
where <where_statement>			
and coalesce(stud_race.primary_student_id,'') <> ''
														  
UNION
														  
select distinct
nullif(trim(regexp_replace(stud_race.primary_student_id,'"|[\\000-\\037\\177]','')),'') as primary_student_id,
nullif(trim(stud_race.race),'') as race,
  
case when pcm_race.eab_standard_value = 'Unmapped Value'
then 'Unknown'
else pcm_race.eab_standard_value
end as eab_race,
  
case when pcm_race_amer_indian.eab_standard_value = 'Unmapped Value'
then null
else pcm_race_amer_indian.eab_standard_value
end as eab_race_amer_indian,

case when pcm_race_asian.eab_standard_value = 'Unmapped Value'
then null
else pcm_race_asian.eab_standard_value
end as eab_race_asian,

case when pcm_race_black.eab_standard_value = 'Unmapped Value'
then null
else pcm_race_black.eab_standard_value
end as eab_race_black,

case when pcm_race_native_hawaiian.eab_standard_value = 'Unmapped Value'
then null
else pcm_race_native_hawaiian.eab_standard_value
end as eab_race_native_hawaiian,

case when pcm_race_white.eab_standard_value = 'Unmapped Value'
then null
else pcm_race_white.eab_standard_value
end as eab_race_white,

case when pcm_race_middle_eastern.eab_standard_value = 'Unmapped Value'
then null
else pcm_race_middle_eastern.eab_standard_value
end as eab_race_middle_eastern,

case when pcm_race_multiracial.eab_standard_value = 'Unmapped Value'
then null
else pcm_race_multiracial.eab_standard_value
end as eab_race_multiracial,
  
case when pcm_race_other.eab_standard_value = 'Unmapped Value'
then null
else pcm_race_other.eab_standard_value
end as eab_race_other,

case when pcm_race_prefer_not_respond.eab_standard_value = 'Unmapped Value'
then null
else pcm_race_prefer_not_respond.eab_standard_value
end as eab_race_prefer_not_respond,

case when pcm_race_unknown.eab_standard_value = 'Unmapped Value'
then null
else pcm_race_unknown.eab_standard_value
end as eab_race_unknown,
stud_race.intake_date as intake_dt			

from <[Enroll360].[Student Race].{fulfillment_ug_applicant}> as stud_race
  
left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_race
on (pcm_race.field_name = 'eab_race'
and coalesce(stud_race.race,'') = coalesce(pcm_race.partner_value,''))
  
left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_race_amer_indian
on (pcm_race_amer_indian.field_name = 'eab_race_amer_indian'
and coalesce(stud_race.calculated_field_1,'') = coalesce(pcm_race_amer_indian.partner_value,''))
  
left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_race_asian
on (pcm_race_asian.field_name = 'eab_race_asian'
and coalesce(stud_race.calculated_field_2,'') = coalesce(pcm_race_asian.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_race_black
on (pcm_race_black.field_name = 'eab_race_black'
and coalesce(stud_race.calculated_field_3,'') = coalesce(pcm_race_black.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_race_native_hawaiian
on (pcm_race_native_hawaiian.field_name = 'eab_race_native_hawaiian'
and coalesce(stud_race.calculated_field_4,'') = coalesce(pcm_race_native_hawaiian.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_race_white
on (pcm_race_white.field_name = 'eab_race_white'
and coalesce(stud_race.calculated_field_5,'') = coalesce(pcm_race_white.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_race_middle_eastern
on (pcm_race_middle_eastern.field_name = 'eab_race_middle_eastern'
and coalesce(stud_race.calculated_field_6,'') = coalesce(pcm_race_middle_eastern.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_race_multiracial
on (pcm_race_multiracial.field_name = 'eab_race_multiracial'
and coalesce(stud_race.calculated_field_7,'') = coalesce(pcm_race_multiracial.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_race_other
on (pcm_race_other.field_name = 'eab_race_other'
and coalesce(stud_race.calculated_field_8,'') = coalesce(pcm_race_other.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_race_prefer_not_respond
on (pcm_race_prefer_not_respond.field_name = 'eab_race_prefer_not_respond'
and coalesce(stud_race.calculated_field_9,'') = coalesce(pcm_race_prefer_not_respond.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_race_unknown
on (pcm_race_unknown.field_name = 'eab_race_unknown'
and coalesce(stud_race.calculated_field_10,'') = coalesce(pcm_race_unknown.partner_value,''))
														  
where <where_statement>
and coalesce(stud_race.primary_student_id,'') <> ''														  
)
select distinct primary_student_id,
race,
eab_race,
intake_dt
from (
	
select primary_student_id
 , race 
--, 'Asian'
, case 
    when eab_race in ('Asian/Asian American','Asian') then 'Asian'
    else eab_race 
    end as eab_race
,intake_dt
from race_data
where eab_race is not null and race <> ''
	
union all

select primary_student_id
, EAB_RACE_AMER_INDIAN
, EAB_RACE_AMER_INDIAN
,intake_dt  
from race_data
where EAB_RACE_AMER_INDIAN is not null
	
union all

select primary_student_id
, EAB_RACE_ASIAN
--, 'Asian'
, case 
    when EAB_RACE_ASIAN in ('Asian/Asian American','Asian') then 'Asian'
    else null
    end as EAB_RACE_ASIAN
,intake_dt
from race_data
where EAB_RACE_ASIAN is not null
	
union all

select primary_student_id
, EAB_RACE_BLACK
, EAB_RACE_BLACK
,intake_dt  
from race_data
where EAB_RACE_BLACK is not null

union all

select primary_student_id
, EAB_RACE_NATIVE_HAWAIIAN
, EAB_RACE_NATIVE_HAWAIIAN
,intake_dt  
from race_data
where EAB_RACE_NATIVE_HAWAIIAN is not null

union all

select primary_student_id
, EAB_RACE_WHITE
, EAB_RACE_WHITE
,intake_dt  
from race_data
where EAB_RACE_WHITE is not null

union all

select primary_student_id
, EAB_RACE_MIDDLE_EASTERN
, EAB_RACE_MIDDLE_EASTERN
,intake_dt  
from race_data
where EAB_RACE_MIDDLE_EASTERN is not null

union all

select primary_student_id
, EAB_RACE_MULTIRACIAL
, EAB_RACE_MULTIRACIAL
,intake_dt  
from race_data
where EAB_RACE_MULTIRACIAL is not null

union all

select primary_student_id
, EAB_RACE_OTHER
, EAB_RACE_OTHER
,intake_dt  
from race_data
where EAB_RACE_OTHER is not null

union all

select primary_student_id
, EAB_RACE_PREFER_NOT_RESPOND
, EAB_RACE_PREFER_NOT_RESPOND
,intake_dt  
from race_data
where EAB_RACE_PREFER_NOT_RESPOND is not null

union all

select primary_student_id
, EAB_RACE_UNKNOWN
, EAB_RACE_UNKNOWN
,intake_dt  
from race_data
where EAB_RACE_UNKNOWN is not null
  ) agg