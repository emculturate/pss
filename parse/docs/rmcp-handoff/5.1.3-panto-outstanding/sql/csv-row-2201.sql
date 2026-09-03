/*2026-02-03: updated to standard for id Xwalk ESANALYTIC-56375 - blang*/
/*2025-07-07: added join extension - ccreekman*/
/*2025-06-19: added partnercontact_id, partner_system_name per ESANALYTIC-50473 - ccreekman*/
/*2025-03-24: added eab_student_id_type - ccreekman*/
/*2025-03-04: eab_opt_out_ind updated to align with other opt fields and null 'Unmapped Value' - ccreekman*/

select distinct * from (

select distinct
nullif(trim(regexp_replace(agg2.primary_student_id,'"|[\\000-\\037\\177]','')),'') as primary_student_id,
nullif(trim(regexp_replace(agg2.eab_student_id,'"|[\\000-\\037\\177]','')),'') as eab_student_id,
--nullif(trim(regexp_replace(agg2.eab_student_id_type,'"|[\\000-\\037\\177]','')),'') as eab_student_id_type,
nullif(trim(regexp_replace(lower(regexp_replace(coalesce(agg2.partner_system_name,'primary'),'[\\s]','')),'"|[\\000-\\037\\177]','')),'') as eab_student_id_type,
nullif(trim(regexp_replace(<partnercontact_id.crm_ug>,'"|[\\000-\\037\\177]','')),'') as partnercontact_id,
nullif(trim(regexp_replace(<partner_system_name.crm_ug>,'"|[\\000-\\037\\177]','')),'') as partner_system_name,

agg2.sis_student_id,
agg2.crm_student_id,
agg2.other_student_id,

/*3-2-2023 Updated the name fields to remove problem characters. Copied from master student.final*/

nullif(trim(regexp_replace(regexp_replace(agg2.fname,'[\\s]',' '),'[^[:ascii:]]|[\\0]|"|[\\000-\\037\\177]','')),'') as fname,
nullif(trim(regexp_replace(regexp_replace(agg2.mname,'[\\s]',' '),'[^[:ascii:]]|[\\0]|"|[\\000-\\037\\177]','')),'') as mname,
nullif(trim(regexp_replace(regexp_replace(agg2.lname,'[\\s]',' '),'[^[:ascii:]]|[\\0]|"|[\\000-\\037\\177]','')),'') as lname,
nullif(trim(regexp_replace(regexp_replace(agg2.preferred_fname,'[\\s]',' '),'[^[:ascii:]]|[\\0]|"|[\\000-\\037\\177]','')),'') as preferred_fname,


agg2.us_citizenship_status,
agg2.international_ind,
agg2.birth_dt,
agg2.gender,
agg2.pref_gender,
agg2.religious_affiliation,
substr(try_to_date(agg2.hs_grad_year,'yyyy'),1,4) as hs_grad_year,
agg2.origin_source,
agg2.origin_source_dt,
agg2.first_gen_ind,
agg2.military_service_status,
agg2.eab_first_gen_ind,
agg2.eab_pref_gender,
agg2.eab_international_ind,
agg2.eab_military_veteran_ind,
agg2.alumni_relation_ind,
agg2.eab_alumni_relation_ind,
agg2.eab_ethnicity,
agg2.hispanic_ind,
agg2.ethnicity,
agg2.campus_visit_cnt,
agg2.campus_visit_ind,
agg2.first_campus_visit_dt,
agg2.last_campus_visit_dt,
agg2.eab_campus_visit_ind,
agg2.mail_optout_ind,
agg2.eab_mail_optout_ind,
agg2.email_optout_ind,
agg2.phone_optout_ind,
agg2.sms_optin_ind,
agg2.sms_optout_ind,
agg2.eab_email_optout_ind,
agg2.eab_phone_optout_ind,
agg2.eab_sms_optin_ind,
agg2.eab_opt_out_ind,
agg2.opt_out_ind,
agg2.intake_dt

from (

select agg.*,
row_number() over (partition by agg.primary_student_id order by agg.rn) rn2
from (

select distinct
1 as rn,
stud.primary_student_id,
stud.sis_student_id,
--stud.eab_student_id, /*10-18-24: Project Atlas - returning primary_student_id in eab_student_id until engineering directs otherwise - ccreekman
stud.primary_student_id as eab_student_id,
'primary' as eab_student_id_type,
stud.crm_student_id,
stud.other_student_id,
stud.first_name as fname,
stud.middle_name as mname,
stud.last_name as lname,
stud.preferred_first_name as preferred_fname,
stud.us_citizenship_status as us_citizenship_status,
stud.international_indicator as international_ind,
try_to_timestamp(substring(stud.birth_date,1,19)) as birth_dt,
stud.gender,
stud.preferred_gender as pref_gender,
stud.religious_affiliation as religious_affiliation,
stud.high_school_grad_year as hs_grad_year,
stud.origin_source_code as origin_source,
try_to_timestamp(substring(stud.origin_source_date,1,19)) as origin_source_dt,
stud.first_generation_indicator as first_gen_ind,
stud.military_service_or_veteran_status as military_service_status,

case when pcm_first_gen.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_first_gen.eab_standard_value)
end as eab_first_gen_ind,

pcm_pref_gen.eab_standard_value as eab_pref_gender,

case when pcm_int_ind.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_int_ind.eab_standard_value)
end as eab_international_ind,

case when pcm_mil_vet_ind.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_mil_vet_ind.eab_standard_value)
end as eab_military_veteran_ind,

stud.alumni_relationship_indicator as alumni_relation_ind,

case when pcm_alum_rel_ind.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_alum_rel_ind.eab_standard_value)
end as eab_alumni_relation_ind,

pcm_ethn.eab_standard_value as eab_ethnicity,
stud.hispanic_indicator as hispanic_ind,
stud.ethnicity,

event_agg.event_ct as campus_visit_cnt,
case when event_agg.primary_student_id_ev is not null then 'TRUE' else stud.campus_visit_indicator end as campus_visit_ind,
event_agg.min_event_date as first_campus_visit_dt,
event_agg.max_event_date as last_campus_visit_dt,

case when event_agg.primary_student_id_ev is not null
then try_to_boolean(true)
when pcm_campus_visit_ind.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_campus_visit_ind.eab_standard_value)
end as eab_campus_visit_ind,

stud.mail_opt_out_indicator as mail_optout_ind,

case when pcm_mail_optout_ind.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_mail_optout_ind.eab_standard_value)
end as eab_mail_optout_ind,

stud.email_opt_out_indicator as email_optout_ind,
stud.phone_opt_out_indicator as phone_optout_ind,
stud.sms_opt_in_indicator as sms_optin_ind,
stud.sms_opt_out_indicator as sms_optout_ind,

case when pcm_email_optout_ind.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_email_optout_ind.eab_standard_value)
end as eab_email_optout_ind,

case when pcm_phone_optout_ind.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_phone_optout_ind.eab_standard_value)
end as eab_phone_optout_ind,

case when pcm_sms_optin_ind.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_sms_optin_ind.eab_standard_value)
end as eab_sms_optin_ind,

case when pcm_optout_ind.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_optout_ind.eab_standard_value)
end as eab_opt_out_ind,

stud.partner_system_name,
stud.opt_out_indicator_general as opt_out_ind,
stud.intake_date as intake_dt

from <[Enroll360].[Student].{fulfillment_ug_applicants}> stud

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_first_gen
on (pcm_first_gen.field_name = 'eab_first_gen_ind'
and coalesce(stud.first_generation_indicator,'') = coalesce(pcm_first_gen.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_pref_gen
on (pcm_pref_gen.field_name = 'eab_pref_gender'
and coalesce(stud.preferred_gender,'') = coalesce(pcm_pref_gen.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_int_ind
on (pcm_int_ind.field_name = 'eab_international_ind'
and coalesce(stud.international_indicator,'') = coalesce(pcm_int_ind.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_mil_vet_ind
on (pcm_mil_vet_ind.field_name = 'eab_military_veteran_ind'
and coalesce(stud.military_service_or_veteran_status,'') = coalesce(pcm_mil_vet_ind.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_alum_rel_ind
on (pcm_alum_rel_ind.field_name = 'eab_alumni_relation_ind'
and coalesce(stud.alumni_relationship_indicator,'') = coalesce(pcm_alum_rel_ind.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_ethn
on (pcm_ethn.field_name = 'eab_ethnicity'
and coalesce(stud.ethnicity,'') = coalesce(pcm_ethn.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_campus_visit_ind
on (pcm_campus_visit_ind.field_name = 'eab_campus_visit_ind'
and coalesce(stud.campus_visit_indicator,'') = coalesce(pcm_campus_visit_ind.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_mail_optout_ind
on (pcm_mail_optout_ind.field_name = 'eab_mail_optout_ind'
and coalesce(stud.mail_opt_out_indicator,'') = coalesce(pcm_mail_optout_ind.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_email_optout_ind
on (pcm_email_optout_ind.field_name = 'eab_email_optout_ind'
and coalesce(stud.email_opt_out_indicator,'') = coalesce(pcm_email_optout_ind.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_phone_optout_ind
on (pcm_phone_optout_ind.field_name = 'eab_phone_optout_ind'
and coalesce(stud.phone_opt_out_indicator,'') = coalesce(pcm_phone_optout_ind.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_sms_optin_ind
on (pcm_sms_optin_ind.field_name = 'eab_sms_optin_ind'
and coalesce(stud.sms_opt_in_indicator,'') = coalesce(pcm_sms_optin_ind.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_optout_ind
on (pcm_optout_ind.field_name = 'eab_opt_out_ind'
and coalesce(stud.opt_out_indicator_general,'') = coalesce(pcm_optout_ind.partner_value,''))

left outer join 
    (
    select events2.primary_student_id as primary_student_id_ev, count(events2.primary_student_id) as event_ct, 
    min(events2.event_date) as min_event_date, max(events2.event_date) as max_event_date 
    from (
    	select distinct * from (
	    select se.primary_student_id, se.event_dt as event_date
    	from <[Enroll360].[Student Events].{final}> as se
	    union all
	    select s.primary_student_id, try_to_timestamp(substring(s.first_campus_visit_date,1,19)) as event_date
	    from <[Enroll360].[Student].{fulfillment_ug_applicants}> as s
	    union all
	    select s.primary_student_id, try_to_timestamp(substring(s.MOST_RECENT_CAMPUS_VISIT_DATE,1,19)) as event_date
	    from <[Enroll360].[Student].{fulfillment_ug_applicants}> as s
		union all
		select s.primary_student_id, try_to_timestamp(substring(s.first_campus_visit_date,1,19)) as event_date
	    from <[Enroll360].[Student].{fulfillment_ug_inquiries}> as s
	    union all
	    select s.primary_student_id, try_to_timestamp(substring(s.MOST_RECENT_CAMPUS_VISIT_DATE,1,19)) as event_date
	    from <[Enroll360].[Student].{fulfillment_ug_inquiries}> as s
    	) events 
    where 1=1
    and events.event_date is not null
    ) events2
    group by primary_student_id_ev
) event_agg
on stud.primary_student_id = event_agg.primary_student_id_ev

<join_extension.Student>
														   
where <where_statement>
														   
union all

select distinct
2 as rn,
stud.primary_student_id,
stud.sis_student_id,
--stud.eab_student_id, /*10-18-24: Project Atlas - returning primary_student_id in eab_student_id until engineering directs otherwise - ccreekman
stud.primary_student_id as eab_student_id,
'primary' as eab_student_id_type,
stud.crm_student_id,
stud.other_student_id,
stud.first_name as fname,
stud.middle_name as mname,
stud.last_name as lname,
stud.preferred_first_name as preferred_fname,
stud.us_citizenship_status as us_citizenship_status,
stud.international_indicator as international_ind,
try_to_timestamp(substring(stud.birth_date,1,19)) as birth_dt,
stud.gender,
stud.preferred_gender as pref_gender,
stud.religious_affiliation as religious_affiliation,
stud.high_school_grad_year as hs_grad_year,
stud.origin_source_code as origin_source,
try_to_timestamp(substring(stud.origin_source_date,1,19)) as origin_source_dt,
stud.first_generation_indicator as first_gen_ind,
stud.military_service_or_veteran_status as military_service_status,

case when pcm_first_gen.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_first_gen.eab_standard_value)
end as eab_first_gen_ind,

pcm_pref_gen.eab_standard_value as eab_pref_gender,

case when pcm_int_ind.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_int_ind.eab_standard_value)
end as eab_international_ind,

case when pcm_mil_vet_ind.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_mil_vet_ind.eab_standard_value)
end as eab_military_veteran_ind,

stud.alumni_relationship_indicator as alumni_relation_ind,

case when pcm_alum_rel_ind.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_alum_rel_ind.eab_standard_value)
end as eab_alumni_relation_ind,

pcm_ethn.eab_standard_value as eab_ethnicity,
stud.hispanic_indicator as hispanic_ind,
stud.ethnicity,

event_agg.event_ct as campus_visit_cnt,
case when event_agg.primary_student_id_ev is not null then 'TRUE' else stud.campus_visit_indicator end as campus_visit_ind,
event_agg.min_event_date as first_campus_visit_dt,
event_agg.max_event_date as last_campus_visit_dt,


case when event_agg.primary_student_id_ev is not null
then try_to_boolean(true)
when pcm_campus_visit_ind.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_campus_visit_ind.eab_standard_value)
end as eab_campus_visit_ind,

stud.mail_opt_out_indicator as mail_optout_ind,

case when pcm_mail_optout_ind.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_mail_optout_ind.eab_standard_value)
end as eab_mail_optout_ind,

stud.email_opt_out_indicator as email_optout_ind,
stud.phone_opt_out_indicator as phone_optout_ind,
stud.sms_opt_in_indicator as sms_optin_ind,
stud.sms_opt_out_indicator as sms_optout_ind,

case when pcm_email_optout_ind.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_email_optout_ind.eab_standard_value)
end as eab_email_optout_ind,

case when pcm_phone_optout_ind.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_phone_optout_ind.eab_standard_value)
end as eab_phone_optout_ind,

case when pcm_sms_optin_ind.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_sms_optin_ind.eab_standard_value)
end as eab_sms_optin_ind,

case when pcm_optout_ind.eab_standard_value = 'Unmapped Value'
then null
else try_to_boolean(pcm_optout_ind.eab_standard_value)
end as eab_opt_out_ind,

stud.partner_system_name,
stud.opt_out_indicator_general as opt_out_ind,
stud.intake_date as intake_dt

from <[Enroll360].[Student].{fulfillment_ug_inquiries}> stud

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_first_gen
on (pcm_first_gen.field_name = 'eab_first_gen_ind'
and coalesce(stud.first_generation_indicator,'') = coalesce(pcm_first_gen.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_pref_gen
on (pcm_pref_gen.field_name = 'eab_pref_gender'
and coalesce(stud.preferred_gender,'') = coalesce(pcm_pref_gen.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_int_ind
on (pcm_int_ind.field_name = 'eab_international_ind'
and coalesce(stud.international_indicator,'') = coalesce(pcm_int_ind.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_mil_vet_ind
on (pcm_mil_vet_ind.field_name = 'eab_military_veteran_ind'
and coalesce(stud.military_service_or_veteran_status,'') = coalesce(pcm_mil_vet_ind.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_alum_rel_ind
on (pcm_alum_rel_ind.field_name = 'eab_alumni_relation_ind'
and coalesce(stud.alumni_relationship_indicator,'') = coalesce(pcm_alum_rel_ind.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_ethn
on (pcm_ethn.field_name = 'eab_ethnicity'
and coalesce(stud.ethnicity,'') = coalesce(pcm_ethn.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_campus_visit_ind
on (pcm_campus_visit_ind.field_name = 'eab_campus_visit_ind'
and coalesce(stud.campus_visit_indicator,'') = coalesce(pcm_campus_visit_ind.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_mail_optout_ind
on (pcm_mail_optout_ind.field_name = 'eab_mail_optout_ind'
and coalesce(stud.mail_opt_out_indicator,'') = coalesce(pcm_mail_optout_ind.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_email_optout_ind
on (pcm_email_optout_ind.field_name = 'eab_email_optout_ind'
and coalesce(stud.email_opt_out_indicator,'') = coalesce(pcm_email_optout_ind.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_phone_optout_ind
on (pcm_phone_optout_ind.field_name = 'eab_phone_optout_ind'
and coalesce(stud.phone_opt_out_indicator,'') = coalesce(pcm_phone_optout_ind.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_sms_optin_ind
on (pcm_sms_optin_ind.field_name = 'eab_sms_optin_ind'
and coalesce(stud.sms_opt_in_indicator,'') = coalesce(pcm_sms_optin_ind.partner_value,''))

left outer join <[Enroll360].[Partner Code Mapping].{convert}> pcm_optout_ind
on (pcm_optout_ind.field_name = 'eab_opt_out_ind'
and coalesce(stud.opt_out_indicator_general,'') = coalesce(pcm_optout_ind.partner_value,''))

left outer join 
    (
    select events2.primary_student_id as primary_student_id_ev, count(events2.primary_student_id) as event_ct, 
    min(events2.event_date) as min_event_date, max(events2.event_date) as max_event_date 
    from (
    	select distinct * from (
	    select se.primary_student_id, se.event_dt as event_date
    	from <[Enroll360].[Student Events].{final}> as se
	    union all
	    select s.primary_student_id, try_to_timestamp(substring(s.first_campus_visit_date,1,19)) as event_date
	    from <[Enroll360].[Student].{fulfillment_ug_applicants}> as s
	    union all
	    select s.primary_student_id, try_to_timestamp(substring(s.MOST_RECENT_CAMPUS_VISIT_DATE,1,19)) as event_date
	    from <[Enroll360].[Student].{fulfillment_ug_applicants}> as s
		union all
		select s.primary_student_id, try_to_timestamp(substring(s.first_campus_visit_date,1,19)) as event_date
	    from <[Enroll360].[Student].{fulfillment_ug_inquiries}> as s
	    union all
	    select s.primary_student_id, try_to_timestamp(substring(s.MOST_RECENT_CAMPUS_VISIT_DATE,1,19)) as event_date
	    from <[Enroll360].[Student].{fulfillment_ug_inquiries}> as s
    	) events 
    where 1=1
    and events.event_date is not null
    ) events2
    group by primary_student_id_ev
) event_agg
on stud.primary_student_id = event_agg.primary_student_id_ev
														   
<join_extension.Student>

where <where_statement>
														   
) agg
	
) agg2
													  
where agg2.rn2 = 1

) agg3
where coalesce(agg3.partnercontact_id,'') <> ''
and coalesce(agg3.partner_system_name,'') <> ''