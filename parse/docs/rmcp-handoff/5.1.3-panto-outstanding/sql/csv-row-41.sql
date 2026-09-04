--with results as
--(
--select t.* ,row_number() over(partition by t.firstname order by t.firstname) rn from (
 -- Original query
-- Added new ACS fields - 08/17/2021 - Anjaly
-- Added changes to enable parent marketing  - 08/26/2021 - Anjaly
-- Added INITCAP for parent funnel status and added test.eab.com filter - Adam 8/31/21
  select distinct
NULLIF(TRIM(Con.first_name), '') as firstname,
NULLIF(TRIM(Con.last_name), '') as lastname,
TRIM(Con.<Email Address>) as email,
NULLIF(TRIM(SCCA.ADDR_1), '') as address1,
NULLIF(TRIM(SCCA.ADDR_2), '') as address2,
NULLIF(TRIM(SCCA.<City>), '') as city,
NULLIF(TRIM(state_translation.eabstandard_value), '') as state, 
TRIM(Case when INITCAP(SCCA.<Country>)='Unknown' or lower(SCCA.<Country>)='unmapped value' then NULL else SCCA.<Country> end ) as country,
TRIM(Case when Lower(SCCA.<Country>)='united states' then SCCA.ZIP5 else NULL end) as zipcode,
TRIM(SCCA.CREATE_DT) as CREATE_DTS,
TRIM(Con.<Phone Number>) as mobile, 
TRIM(Con.<Distance from Campus>) as dist_from_campus_calc,
TRIM(AC.<Is Deleted>) as is_deleted,
TRIM(race_stdz.race) as race_stdzd,
TRIM(Con.<Ethnicity>) as ethnicity_stdzd,
null as religion_partner,
TRIM(ac_blnd.academic_interest_blnd) as academic_field_intrst_bln, 
TRIM(Con.<Contact Key>) as eab_contact_id,
NULLIF(TRIM(Con.<Contact Type>), '')  as  eab_contact_type,
INITCAP(NULLIF(TRIM(Con.<Funnel Status Calc>), '')) as funnel_stage_calc,
TRIM(AC.<Student Application Status>) as student_app_status,
TRIM(AC.<Form Student First Name>) as form_student_first_name,
TRIM(AC.<Form Student Last Name>) as form_student_last_name,
TRIM(AC.<Form Parent First Name>) as form_parent_first_name,
TRIM(AC.<Form Parent Last Name>) as form_parent_last_name,
TRIM(AC.<Form Parent Email>) as form_parent_email,
TRIM(AC.<Form Referred By EAB ID>) as form_referred_by_EAB_ID,
TRIM(label.source_label)  as og_contact_source_calc, 
TRIM(all_source.all_sources_blnd) as all_sources_blnd, 
TRIM(AC.FORM_H_SCHOOL_NAME) as form_h_school_name,
TRIM(AC.FORM_H_SCHOOL_ADDR) as form_h_school_addr,
TRIM(AC.FORM_H_SCHOOL_CITY) as form_h_school_city,
TRIM(AC.FORM_H_SCHOOL_STATEPROV) as form_h_school_stateprov, 
--TRIM(AC.FORM_H_SCHOOL_POST_CODE) as form_h_school_post_code,  
TRIM(AC.FORM_H_SCHOOL_COUNTRY) as form_h_school_country,
TRIM(AC.FORM_H_SCHOOL_GRAD_YEAR	) as form_h_school_grad_year,
TRIM(AC.FORM_ENTRY_YEAR) as form_entry_year,
TRIM(Con.grade_level) as  h_school_grade_level_calc, -- current_grade_level from contact 
--TRIM(Con.graduation_year) as h_school_grad_year_calc, 
TRIM(max_values.sat_max) as max_sat_score_rng_calc, 
TRIM(max_values.act_max) as max_act_score_rng_calc,
TRIM(max_values.psat_max) as max_psat_score_rng_calc,
TRIM(max_values.preact_max) as max_preact_score_rng_calc,
null as fafsa_submit_date_partner,
TRIM(Con.concontact_rand) as random_number,
TRIM(AC.FORM_BOT_HONEYPOT) as form_bot_honeypot,
CASE 
	WHEN AC.ACS_REPLICATION_DATE IS NOT NULL THEN CONCAT(LEFT(REPLACE(TRIM(AC.ACS_REPLICATION_DATE), ' ', 'T'), 19), '+00:00') 
	ELSE NULL
END as acs_replication_date,
TRIM(CAC.<Print Display ID>) as print_display_id,
TRIM(PD.school_panto_id ) as es_partner_id ,
TRIM(AC.FORM_ACADEMIC_FIELD_INTRS) as Form_Academic_Field_Intrs ,
TRIM(AC.FORM_EMAIL_UPDATE_CONTACT_KEY) as FORM_EMAIL_UPDATED_EAB_ID,
TRIM(AC.Form_Student_Addr)        as FORM_STUDENT_ADDR,       
TRIM(AC.Form_Student_City)        as FORM_STUDENT_CITY,       
TRIM(AC.Form_Student_Stateprov)   as form_student_stateprov,  
TRIM(AC.Form_Student_Post_Code)   as form_student_post_code,  
TRIM(AC.Form_Student_Country)     as form_student_country,    
TRIM(AC.Form_Sms_Opt_In)          as form_sms_opt_in,         
TRIM(AC.Engaged_Inq_Cmpgn_Acsl)   as engaged_inq_cmpgn_acsl,  
TRIM(AC.Engaged_Reeng_Cmpgn_Acsl) as engaged_reeng_cmpgn_acsl, 
TRIM(parent.FIRST_NAME)        as child_first_name_calc,
TRIM(parent.LAST_NAME) as child_last_name_calc,
TRIM(CASE WHEN Con.suppress_mail_indicator=1 THEN 'Y' else 'N' END) as do_not_direct_mail_calc,
Case 
when  lower(optin_sms_indicator) = 'false' then 'false'
when  lower(optin_sms_indicator) = 'true' then 'true' else null end as sms_opt_in_calc,
--TRIM(CASE WHEN PHN.optin_sms_indicator=1 THEN 'Y' ELSE 'N' END)  as sms_opt_in_calc,

TRIM(CASE WHEN Con.suppress_email_indicator=1 THEN 'TRUE' else 'FALSE' END) as dnc_email,
NULL as dnc_email_comment,--TRIM(CON.dnc_email_comment) as dnc_email_comment -- not added at Panto in Contact table yet

--Case  when  lower(optin_sms_indicator) = 'false' then 'true'
  --    when  lower(optin_sms_indicator) = 'true' then 'false' else null end  as dnc_sms,
--PHN.dnc_sms_comment AS dnc_sms_comment ,
--Case when  lower(optin_sms_indicator) = 'false' then current_timestamp else null end as dnc_sms_date,
PHN.optin_sms_indicator,
/* Commented out the code to release the sms changes later */
TRIM(CASE WHEN PHN.optin_sms_indicator=1 THEN 'FALSE' 
	 WHEN PHN.optin_sms_indicator=0 THEN 'TRUE'
	 ELSE 'TRUE' 
	 END)  as dnc_sms,
TRIM(CASE WHEN PHN.optin_sms_indicator=1 THEN TRIM(PHN.DNC_SMS_COMMENT) 
	 WHEN PHN.optin_sms_indicator=0 THEN TRIM(PHN.DNC_SMS_COMMENT) 
	 ELSE 'Default Optout'
	 END)  as dnc_sms_comment,
current_timestamp as dnc_sms_date,
/*Anjaly - Commented out the code to release the sms changes later */ 		   
parent.stu_hs_grade_level_calc as stu_hs_grade_level_calc, 
TRIM(Con.graduation_year) as high_school_grad_year_calculated,
NULL as student_funnel_stage_calc, -- null
-- New acs fields - Anjaly - 08/17/2021			   
--TRIM(Con.grade_level) as stu_hs_grade_level_calc,
INITCAP(NULLIF(TRIM(parent.student_funnel_stage_calc), '')) as stu_funnel_stage_calc, -- populated for parent
TRIM(AC.<Student Application Status>) as  stu_eab_app_status,
TRIM(ac_blnd.academic_interest_blnd) as  stu_aca_field_intrst_blnd,
TRIM(race_stdz.race) as stu_race_stdzd,
TRIM(Con.<Ethnicity>) as stu_ethnicity_stdzd,
Null as stu_religion_partner,
parent.high_school_grad_year_calculated as  stu_hs_grad_year_calc,
ac.eng_app_par_gen_acsl as eng_app_par_gen_acsl,
ac.eng_app_stu_gen_acsl as eng_app_stu_gen_acsl,
ac.eng_app_stu_paper_acsl as eng_app_stu_paper_acsl,
ac.eng_app_stu_sub_acsl as eng_app_stu_sub_acsl,
ac.eng_cul_par_inq_acsl as eng_cul_par_inq_acsl,
ac.eng_cul_par_nur_acsl as eng_cul_par_nur_acsl,
ac.eng_cul_stu_inq_acsl as eng_cul_stu_inq_acsl,
ac.eng_cul_stu_paper_acsl as eng_cul_stu_paper_acsl,
ac.eng_cul_stu_nur_acsl as eng_cul_stu_nur_acsl,
ac.eng_cul_stu_reen_acsl as eng_cul_stu_reen_acsl,
ac.eng_cul_stu_j_reen_acsl as eng_cul_stu_j_reen_acsl,
ac.eng_cul_stu_sms_acsl as eng_cul_stu_sms_acsl,
ac.st_dt_app_par_gen_acsl as st_dt_app_par_gen_acsl,
ac.st_dt_app_stu_gen_acsl as st_dt_app_stu_gen_acsl,
ac.st_dt_app_stu_paper_acsl as st_dt_app_stu_paper_acsl,
ac.st_dt_app_stu_sub_acsl as st_dt_app_stu_sub_acsl,
ac.st_dt_cul_par_inq_acsl as st_dt_cul_par_inq_acsl,
ac.st_dt_cul_par_nur_acsl as st_dt_cul_par_nur_acsl,
ac.st_dt_cul_stu_inq_acsl as st_dt_cul_stu_inq_acsl,
ac.st_dt_cul_stu_paper_acsl as st_dt_cul_stu_paper_acsl,
ac.st_dt_cul_stu_nur_acsl as st_dt_cul_stu_nur_acsl,
ac.st_dt_cul_stu_j_reen_acsl as st_dt_cul_stu_j_reen_acsl,
ac.st_dt_cul_stu_reen_acsl as st_dt_cul_stu_reen_acsl,
ac.st_dt_cul_stu_sms_acsl as st_dt_cul_stu_sms_acsl
-- New acs fields 08/17/2021

from
<[Partner_Data_Platform].[Contact]> as Con 
 Left join  <[Partner_Data_Platform].[Contact Address Clean]> as SCCA 
 on Con.CLEAN_ADDR_KEY=SCCA.CLEAN_ADDR_KEY
--AND SCCA.ADDR_TYPE='Mailing'--<Address Type>
Left join <[Partner_Data_Platform].[Contact Acquia Crosswalk]> CAC
on Con.<Contact Key> = CAC.<Contact Key>
 Left join <[Partner_Data_Platform].[ACS Contacts]> as AC 
 on CAC.acs_contact_id = AC.acs_contact_id--AC.SOURCE_CONTACT_ID
LEFT JOIN  <[Partner_Data_Platform].[Phone]> as PHN
ON PHN.phone_number=Con.<Phone Number>
left join  (
select Con.FIRST_NAME , Con.LAST_NAME,Con.CONTACT_KEY from
<[Partner_Data_Platform].[Contact]> as Con 
left join  <[Partner_Data_Platform].[Contact Relations]> as d
on Con.CONTACT_KEY=d.student_contact_key
where initcap(Con.<Contact Type>) = 'Parent/Guardian') as child_names
on child_names.CONTACT_KEY=Con.CONTACT_KEY
left join (select CTS_PIVOT.contact_key as contact_key, CTS_PIVOT.sourcecontact_id as sourcecontact_id, max(CTS_PIVOT.sat_max) as sat_max,
max(CTS_PIVOT.act_max) as act_max,
max(CTS_PIVOT.psat_max) as psat_max,
max(CTS_PIVOT.preact_max) as preact_max
from (
select CTS.<Contact Key> as contact_key,CTS.<Source Contact ID> as sourcecontact_id,
case when UPPER(CTS.<Test Type>)='SAT' then CTS.<Score Range Maximum>  else null  end as sat_max, 
case when UPPER(CTS.<Test Type>)='ACT' then CTS.<Score Range Maximum>  else null end as act_max,
case when UPPER(CTS.<Test Type>)='PSAT' then CTS.<Score Range Maximum>  else null  end as psat_max, 
case when UPPER(CTS.<Test Type>)='PREACT' then CTS.<Score Range Maximum>  else null end as preact_max
from <[Partner_Data_Platform].[Contact Test Scores]> as CTS ) as CTS_PIVOT
group by CTS_PIVOT.contact_key,CTS_PIVOT.sourcecontact_id) max_values
on Con.<Contact Key> = max_values.contact_key
and Con.active_source_contact_id=max_values.sourcecontact_id
left join <[Partner_Data_Platform].[Sourcecontact Sourcetype]> AS srccontact_srctype
ON srccontact_srctype.<Source Contact ID> = Con.active_source_contact_id
left JOIN pdp.crf__intake_type AS srctype
ON srccontact_srctype.<Source Type Key> = srctype.intake_type_key

LEFT OUTER JOIN es_pdp_common.common.common_source_standard_mappings AS state_translation
ON  UPPER(state_translation.source_type) = 'GENERAL'
AND Upper(state_translation.Category) = 'STATE'
AND Lower(state_translation.SOURCE_FIELD_NAME) = 'state'
AND	lower(state_translation.eabstandard_value) = lower(SCCA.<State> )
AND lower(state_translation.source_description) = lower(SCCA.<Country>)
			   

Left join (SELECT  a.contact_key, LISTAGG(a.<EAB standard Value>, '|') as academic_interest_blnd
from ( select distinct b.contact_key, b.<EAB standard Value>,b.attribute_type from <[Partner_Data_Platform].[Contact Attributes Combined]> b 
) a
  where Lower(a.attribute_type) = 'academic_interest'-- and  initcap(a.<EAB standard Value>) not in ('Unmapped Value')
group by a.contact_key ) ac_blnd
on Con.<Contact Key>=ac_blnd.contact_key 

Left join 

(SELECT  a.contact_key, a.source_contact_id, LISTAGG(a.<EAB standard Value>, '|') as race
from ( select distinct b.contact_key, b.source_contact_id,b.<EAB standard Value>,b.attribute_type from <[Partner_Data_Platform].[Contact Attributes Combined]> b 
) a
  where Lower(a.attribute_type) = 'race'
group by a.contact_key, a.source_contact_id ) race_stdz
on Con.<Contact Key>=race_stdz.contact_key 
and Con.active_source_contact_id=race_stdz.source_contact_id


Left Join (SELECT list_sources.contact_key, LISTAGG(list_sources.all_source_bln_list_agg, '|') AS all_sources_blnd 
			FROM (SELECT contact_key, CASE WHEN lower(intake_type_label) = 'prenames' THEN 'Migrated Contact'
										  ELSE NULL
									  END AS all_source_bln_list_agg
				  FROM <[Partner_Data_Platform].[Contact Sourcecontacts]> AS CSCS
				  LEFT JOIN pdp.crf__intake_type AS intake
					  ON CSCS.source_type_key = intake.intake_type_key
				  WHERE LOWER(CSCS.active_indicator)= 'true'
				  AND lower(intake_type_label) = 'prenames'
				  UNION
				  SELECT distinct contact_key, source_label AS all_source_bln_list_agg
				  FROM <[Partner_Data_Platform].[Contact Sourcecontacts]> AS CSCS
				  INNER JOIN pdp.crf__source AS srctype
					  ON CSCS.<Source Key> = srctype.source_key
				  WHERE LOWER(CSCS.active_indicator)= 'true'
			) AS list_sources
			GROUP BY list_sources.contact_key) as all_source
		 
	on Con.<Contact Key>=all_source.contact_key

left join
(
select  con3.first_name, con3.last_name, con3.parent_contact_key, con3.student_contact_key,
con3.stu_hs_grade_level_calc as stu_hs_grade_level_calc,
con3.high_school_grad_year_calculated as high_school_grad_year_calculated,
con3.student_funnel_stage_calc as student_funnel_stage_calc
from
	(
		select distinct con2.first_name, con2.last_name, con2.parent_contact_key, con2.student_contact_key,
				con2.stu_hs_grade_level_calc as stu_hs_grade_level_calc,
				con2.high_school_grad_year_calculated as high_school_grad_year_calculated,
				con2.student_funnel_stage_calc as student_funnel_stage_calc, row_number() over (partition by con2.parent_contact_key order by con2.sourcecontact_intake_dt)as rankid2
		   from
		   (
				   SELECT distinct rel.parent_contact_key, rel.student_contact_key,scst.create_date sourcecontact_intake_dt,
						  con1.<Grade Level> as stu_hs_grade_level_calc,
						  con1.<Graduation Year> as high_school_grad_year_calculated,
			 			  con1.<First Name> as first_name, con1.<Last Name> as last_name,
						  con1.<Funnel Status Calc> as student_funnel_stage_calc, row_number() over (partition by rel.parent_contact_key order by int.INTAKE_PRIORITY) as rankid
					FROM <[Partner_Data_Platform].[Contact]> as con
					inner join <[Partner_Data_Platform].[Contact Sourcecontacts]> as src on
						 con.<Contact Key>=src.<Contact Key> inner join pdp.crf__intake_type int on int.intake_type_key=src.source_type_key
						 inner join <[Partner_Data_Platform].[SourceContact SourceType]> scst on src.<Source Contact ID>=scst.<Source Contact ID>
			   LEFT JOIN <[Partner_Data_Platform].[Contact Relations]> as rel on rel.parent_contact_key = con.contact_key
			   LEFT JOIN <[Partner_Data_Platform].[Contact]> as con1 on rel.student_contact_key = con1.contact_key
				  WHERE con.contact_type = 'Parent/Guardian'
				
		   )con2
    where rankid=1
	)con3
where rankid2=1) parent
on Con.<Contact Key> = parent.parent_contact_key
/*Left join 

(SELECT a.contact_key , a.source_contact_id ,LISTAGG(a.SOURCE_TYPE_LABEL, '|') as all_sources_blnd,a.ACTIVE_INDICATOR   
 from  (
 SELECT distinct CSCS.contact_key , CSCS.source_contact_id ,srctype.SOURCE_TYPE_LABEL ,CSCS.ACTIVE_INDICATOR 
 from 
 <[Partner_Data_Platform].[Contact Sourcecontacts]> as  CSCS
INNER JOIN pdp.crf__source_type AS srctype
ON CSCS.<Source Type Key> = srctype.source_type_key ) a
group by a.contact_key, a.source_contact_id,a.SOURCE_TYPE_LABEL,a.ACTIVE_INDICATOR) all_source
on con.<Contact Key>=all_source.contact_key 
and con.active_source_contact_id=all_source.source_contact_id
and Lower(all_source.ACTIVE_INDICATOR)='true' */



LEFT JOIN 
(SELECT 
sub_1.contact_key,
sub_1.source_label as source_label
FROM

(
	SELECT
	ROW_NUMBER() OVER(PARTITION BY contact_key ORDER BY CSCTS.<Create Date>) as row_id,
	CSCTS.contact_key,
	srctype.source_label
	 from 
 <[Partner_Data_Platform].[Contact Sourcecontacts]> as  CSCTS
INNER JOIN pdp.crf__source AS srctype
ON CSCTS.<Source Key> = srctype.source_key 
) as sub_1
WHERE row_id = 1) label
ON Con.<Contact Key>=label.contact_key
inner join <[Partner_Data_Platform].[Partner Details]> as PD  --) as  t  ) 
			   where --NULLIF(TRIM(Con.<Contact Type>), '') != 'Parent/Guardian' 
			   
			   --NULLIF(TRIM(Con.<Contact Type>), '') IN  ('Deliverability Seed' ,'Ride Along' )
-- Exclude the Students who have null source label 			   
			   (--NULLIF(TRIM(Con.<Contact Type>), '') != 'Parent/Guardian' 
			   --and 
				 (TRIM(label.source_label)  is not null 
			   AND  TRIM(all_source.all_sources_blnd) is not null) ) --and lower(Con.<Email Address>) not like '%test.eab.com'
			   OR 
			   (NULLIF(TRIM(Con.<Contact Type>), '') IN  ('Deliverability Seed' ,'Ride Along' ) --AND TRIM(label.source_label)  is  null 
			   AND  TRIM(all_source.all_sources_blnd) is null)
			   
--		where con.contact_key != '9d0fb0ea-5a34-4ed8-8069-d8d30cd3876a'
--order by eab_contact_id			    
		
/*select FIRSTNAME,	LASTNAME,	EMAIL,	ADDRESS1,	ADDRESS2,	CITY,	STATE,	COUNTRY,	ZIPCODE,	CREATE_DTS,	MOBILE,	DIST_FROM_CAMPUS_CALC,	IS_DELETED,	RACE_STDZD,	ETHNICITY_STDZD,	RELIGION_PARTNER,	ACADEMIC_FIELD_INTRST_BLND,	EAB_CONTACT_ID,	EAB_CONTACT_TYPE,	FUNNEL_STAGE_CALC,	STUDENT_APPLICATION_STATUS,	FORM_STUDENT_FIRST_NAME,	FORM_STUDENT_LAST_NAME,	FORM_PARENT_FIRST_NAME,	FORM_PARENT_LAST_NAME,	FORM_PARENT_EMAIL,	FORM_REFERRED_BY_EAB_ID,	FIRST_SOURCE_CALC,	ALL_SOURCES_BLND,	FORM_H_SCHOOL_NAME,	FORM_H_SCHOOL_ADDR,	FORM_H_SCHOOL_CITY,	FORM_H_SCHOOL_STATEPROV,	FORM_H_SCHOOL_POST_CODE,	FORM_H_SCHOOL_COUNTRY,	FORM_H_SCHOOL_GRAD_YEAR,	FORM_ENTRY_YEAR,	GRADE_LEVEL,	H_SCHOOL_GRAD_YEAR_CALC,	MAX_SAT_SCORE_CALC,	MAX_ACT_SCORE_CALC,	MAX_PSAT_SCORE_CALC,	MAX_PREACT_SCORE_CALC,	FAFSA_SUBMIT_DATE_PARTNER,	RANDOM_NUMBER,	FORM_BOT_HONEYPOT,	ACS_REPLICATION_DT,	PRINT_DISPLAY_ID,	ES_PARTNER_ID,	FORM_ACADEMIC_FIELD_INTRS,	FORM_EMAIL_UPDATED_EAB_ID,	FORM_STUDENT_ADDR,	FORM_STUDENT_CITY,	FORM_STUDENT_STATEPROV,	FORM_STUDENT_POST_CODE,	FORM_STUDENT_COUNTRY,	FORM_SMS_OPT_IN,	ENGAGED_INQ_CMPGN_ACSL,	ENGAGED_REENG_CMPGN_ACSL,	CHILD_FIRST_NAME_CALC,	CHILD_LAST_NAME_CALC,	DO_NOT_DIRECT_MAIL_CALC,	SMS_OPT_IN_CALC,	DNC_EMAIL,	DNC_EMAIL_COMMENT
  from results where results.rn=1*/