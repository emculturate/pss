SELECT distinct
NULL AS contact_key,
cap_contacts.<First Name> AS first_name ,
cap_contacts.<Last Name> AS last_name ,
NULL AS middle_name ,
NULL AS suffix,
NULLIF(TRIM(cap_contacts.<Email Address>), '') AS email_address,
CASE WHEN NULLIF(TRIM(cap_contacts.<Email Address>), '') REGEXP '[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,3}' THEN 1
	ELSE 0
END AS email_valid_indicator,
cap_contacts.<Phone> AS phone_number , 
addr_clean.clean_addr_key AS clean_addr_key,
CASE WHEN (cap_contacts.<Gender> IS NULL OR TRIM(cap_contacts.<Gender>) = '') AND (gender_translation.eabstandard_value IS NULL) THEN 'Unknown'
	WHEN (cap_contacts.<Gender> IS NOT NULL OR TRIM(cap_contacts.<Gender>) <> '') AND (gender_translation.eabstandard_value IS NULL) THEN 'Unmapped Value'
	ELSE gender_translation.eabstandard_value
END  AS gender,
TRUNC(lpad(trunc(cap_contacts.<High School Grad Year>) , 4 , '200')  ) AS graduation_year ,
NULLIF(trunc(12 - (lpad(trunc(cap_contacts.<High School Grad Year>) , 4 , '200') 
			 -  CASE WHEN DATE_PART(MONTH, GETDATE()) >= 7 THEN DATE_PART(YEAR,GETDATE()) + 1 ELSE DATE_PART(YEAR,GETDATE())
END) ),NULL) as grade_level ,
NULL AS first_generation_indicator,
cap_contacts.<High School ceeb> as high_school_ceeb , 
cap_contacts.<Date of Birth> AS birth_date ,
NULL AS ethnicity,
cap_contacts.religion AS religion,
'Student' as contact_type, 
NULL AS active_source_contact_id,
CASE WHEN UPPER(addr_clean.<Country>) = 'UNITED STATES' THEN 0
	ELSE 1
END AS international_indicator, 
/*CEIL(ABS(3963.10 * (ATAN(SQRT(1 - SQUARE(((SIN(lat_1 / 57.29577951) * SIN(lat_2 / 57.29577951)) + (COS(lat_1 / 57.29577951) * COS(lat_2 / 57.29577951) * COS(ABS(long_2 - long_1)/57.29577951))))) / ((SIN(lat_1 / 57.29577951) * SIN(lat_2 / 57.29577951)) + (COS(lat_1 / 57.29577951) * COS(lat_2 / 57.29577951) * COS(ABS(long_2 - long_1)/57.29577951))) ))))*/ NULL AS distance_from_campus,
NULL AS fafsa_submitted_dt, 
CASE WHEN LOWER(record_type) = 'candidate' THEN 'Prospect'
WHEN LOWER(record_type)= 'inquiry' THEN 'Inquiry'
ELSE 'Prospect' END  AS funnel_status_calc, 
NULL AS sat_rng_max, 
NULL AS act_rng_max, 
NULL AS psat_rng_max, 
NULL AS preact_rng_max, 
NULL AS suppress_mail_indicator, 
NULL AS suppress_email_indicator,
NULL AS suppresing_contact_key,	
NULL AS web_application_status,
NULL AS concontact_rand,
cap_contacts.<Create Datetime> AS create_dt,
srccontact_srctype.<Source Contact ID> as sourcecontact_id,
srctype.intake_type_key as source_type_key,
srctype.intake_priority as src_priority,
NULL AS phone_type,
NULL AS acs_replication_date,
NULL AS suppress_phone_indicator,
NULL AS optin_sms_ind,
Case when lower(RECORD_SOURCE) IN ('lamp', 'consignment') then 'Cappex'||' '||Record_type||' '||'(External)'
else 'Cappex'||' '||Record_type end as source_type_label  ,
null as dnc_sms_comment
FROM <[Partner_Data_Platform].[Sourcecontact Sourcetype]> AS srccontact_srctype
INNER JOIN <[Partner_Data_Platform].[Cappex Contacts]> AS cap_contacts
	ON srccontact_srctype.<Source Contact ID> = cap_contacts.<Sourcecontact Id>
INNER JOIN <[Partner_Data_Platform].[Contact Address Clean]> AS addr_clean
	ON srccontact_srctype.<Source Contact ID> = addr_clean.<sourcecontact_id>
INNER JOIN pdp.crf__intake_type AS srctype
	ON srccontact_srctype.<Source Type Key> = srctype.intake_type_key
LEFT OUTER JOIN es_pdp_common.common.common_source_standard_mappings AS gender_translation
	ON UPPER(srctype.intake_type_label) = UPPER(gender_translation.SOURCE_TYPE)
	AND UPPER(gender_translation.Category) = 'GENDER'
	AND INITCAP(gender_translation.SOURCE_FIELD_NAME) = 'Gender'
	AND UPPER(gender_translation.SOURCE_CODE) =  UPPER(cap_contacts.<Gender>) 
/*LEFT OUTER JOIN (select distinct to_number (coalesce(s.SOURCE_CODE,0))as SOURCE_CODE, SOURCE_TYPE ,Category, SOURCE_FIELD_NAME, EABSTANDARD_VALUE
                 from  es_pdp_common.common.common_source_standard_mappings	as s 
		        where  s.Category = 'CAPPEX'
  				and s.SOURCE_FIELD_NAME = 'Race_ethnicity'																  							  
				and s.source_type  = 'CAPPEX' 
) as ethnicity_translation
on UPPER(srctype.intake_type_label) = UPPER(ethnicity_translation.SOURCE_TYPE) 								  
--AND	ethnicity_translation.SOURCE_CODE = cap_contacts.<Race Ethinicity>																						 
AND to_number (coalesce(ethnicity_translation.SOURCE_CODE,0))= to_number(coalesce(cap_contacts.<Race Ethinicity>,0))
where UPPER(srctype.intake_type_label) = 'CAPPEX'
*/