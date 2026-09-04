With ChosenContact_Combined as (select cont_srccont.contact_key, cont_srccont.source_contact_id, comb_common.source_type_key, priority.src_con_priority  as src_con_priority, ST.INTAKE_TYPE_LABEL, funnel_status.FUNNEL_PRIORITY, comb_common.src_priority, comb_common.create_dt, priority.acs_inquiry_override_ind
 from <[Partner_Data_Platform].[Contact].{combined_common_format}> as comb_common inner join <[Partner_Data_Platform].[Contact Sourcecontacts]> as cont_srccont on comb_common.sourcecontact_id = cont_srccont.source_contact_id inner join pdp.crf__intake_type as ST on ST.intake_type_key = comb_common.source_type_key inner join es_pdp_common.common.dim_funnel_status as funnel_status on lower(funnel_status.FUNNEL_STATUS_LABEL) = lower(comb_common.FUNNEL_STATUS_CALC) inner join es_pdp_common.common.dim__sourcecontact_priority as priority on priority.intake_type_key = comb_common.source_type_key and priority.funnel_status_key = funnel_status.funnel_status_key
 where comb_common.sourcecontact_id not in (select comb_common_1.sourcecontact_id
 from <[Partner_Data_Platform].[Contact].{combined_common_format}> as comb_common_1 inner join pdp.crf__intake_type as ST_1 on ST_1.intake_type_key = comb_common.source_type_key inner join PDP.acs__contacts ACON
		on comb_common_1.sourcecontact_id = ACON.sourcecontact_id
 where UPPER(ST_1.intake_type_label) = 'ACQUIA' and UPPER(comb_common_1.FUNNEL_STATUS_CALC) = 'INQUIRY' and ACON.eab_contact_id is not null
)
) select c.contact_key, c.source_contact_id, c.source_type_key, c.acs_inquiry_override_ind
 from (select a.contact_key, a.source_contact_id, a.source_type_key, a.src_con_priority, src_priority, create_dt, FUNNEL_PRIORITY, INTAKE_TYPE_LABEL, acs_inquiry_override_ind, ROW_NUMBER() over (partition by a.contact_key order by a.src_con_priority  DESC, create_dt  asc)  as priority_ranked
 from ChosenContact_Combined as a
) as c
 where c.priority_ranked = 1