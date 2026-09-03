--src_intake_custom_contacts.sql
SELECT md5(TO_VARCHAR(ARRAY_CONSTRUCT('Custom Contacts', enrollment_services__ccp.<Email>, enrollment_services__ccp.<First Name>, enrollment_services__ccp.<Last Name>, enrollment_services__ccp.<Gender>, enrollment_services__ccp.<Race>, enrollment_services__ccp.<Ethnicity>, enrollment_services__ccp.<Graduation Year>, enrollment_services__ccp.<High School GPA>, enrollment_services__ccp.<Funnel Status>, enrollment_services__ccp.<Original Source>, enrollment_services__ccp.<Contact Type>, enrollment_services__ccp.<Test Type>, enrollment_services__ccp.<Score Range Minimum>, enrollment_services__ccp.<Score Range Maximum>, enrollment_services__ccp.<Geographic Region>,<Universal Salt>))) As sourcecontact_id
--,enrollment_services__ccp.email
,CASE WHEN NULLIF(TRIM(enrollment_services__ccp.email),'') REGEXP '[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,3}' THEN TRIM(enrollment_services__ccp.email)
									   ELSE CONCAT(UUID_STRING(),'@test.eab.com')  END AS email
,enrollment_services__ccp.first_name
,enrollment_services__ccp.last_name
,enrollment_services__ccp.gender
,enrollment_services__ccp.race
,enrollment_services__ccp.ethnicity
,enrollment_services__ccp.graduation_year
,enrollment_services__ccp.high_school_gpa
,enrollment_services__ccp.funnel_status
,enrollment_services__ccp.original_source
,enrollment_services__ccp.contact_type
,enrollment_services__ccp.test_type
,enrollment_services__ccp.score_range_minimum 
,enrollment_services__ccp.score_range_maximum
,enrollment_services__ccp.geographic_region
,current_timestamp() As record_create_date
FROM <[enrollment_services].[custom_contacts_primary]> enrollment_services__ccp
LEFT OUTER JOIN PDP.lsc__custom_contacts pdp__custom_contacts
ON md5(TO_VARCHAR(ARRAY_CONSTRUCT('Custom Contacts', enrollment_services__ccp.<Email>, enrollment_services__ccp.<First Name>, enrollment_services__ccp.<Last Name>, enrollment_services__ccp.<Gender>, enrollment_services__ccp.<Race>, enrollment_services__ccp.<Ethnicity>, enrollment_services__ccp.<Graduation Year>, enrollment_services__ccp.<High School GPA>, enrollment_services__ccp.<Funnel Status>, enrollment_services__ccp.<Original Source>, enrollment_services__ccp.<Contact Type>, enrollment_services__ccp.<Test Type>, enrollment_services__ccp.<Score Range Minimum>, enrollment_services__ccp.<Score Range Maximum>, enrollment_services__ccp.<Geographic Region>,<Universal Salt>))) = pdp__custom_contacts.sourcecontact_id
WHERE pdp__custom_contacts.sourcecontact_id IS NULL
UNION 
SELECT
pdp__custom_contacts.sourcecontact_id
,pdp__custom_contacts.email
,pdp__custom_contacts.fname
,pdp__custom_contacts.lname
,pdp__custom_contacts.gender
,pdp__custom_contacts.race
,pdp__custom_contacts.ethnicity
,pdp__custom_contacts.grad_year
,pdp__custom_contacts.hs_gpa
,pdp__custom_contacts.funnel_status
,pdp__custom_contacts.original_source
,pdp__custom_contacts.contact_type
,pdp__custom_contacts.test_type
,pdp__custom_contacts.score_range_min
,pdp__custom_contacts.score_range_max
,pdp__custom_contacts.geo_region
,pdp__custom_contacts.create_dt
FROM PDP.lsc__custom_contacts pdp__custom_contacts