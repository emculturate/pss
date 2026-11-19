select
ADMIT_DATE
,regexp_replace(ADDRESS_LINE_1,'"','') ADDRESS_LINE_1
,regexp_replace(ADDRESS_LINE_2,'"','') ADDRESS_LINE_2
,ADMIT_INDICATOR
,ANALYSIS_CUSTOM_FIELD_1
,ANALYSIS_CUSTOM_FIELD_2
,ANALYSIS_CUSTOM_FIELD_3
,ANALYSIS_CUSTOM_FIELD_4
,ANALYSIS_CUSTOM_FIELD_5
,INCLUDE_IN_ANALYSIS
,APPLICATION_COMPLETION_DATE
,APPLICATION_START_DATE
,APPLICATION_SUBMIT_DATE
,APPLICATION_TYPE
,APPLICATION_TYPE_SOURCE_VALUE
,CASE
WHEN regexp_replace(GENERIC_STAGING_FIELD_01,'"','') = 'PGS' AND regexp_replace(GENERIC_STAGING_FIELD_02,'"','') = 'Yes' AND (regexp_replace(GENERIC_STAGING_FIELD_03,'"','') IN ('First year','First Year International','Transfer','Transfer International','Re- Admit') OR regexp_replace(GENERIC_STAGING_FIELD_03,'"','') = '') AND (regexp_replace(SCHOOL_PROGRAM,'"','') IN ('Accounting','Art – Studio Art','Biology','Business Management','Business Management – Entrepreneurship','Business Management - Marketing Management','Communications','Computer Information Systems','
Criminal Justice','Cybersecurity','Education','Finance','Finance - Corporate','Finance - Personal Financial Management','General Studies','Health Care Management','Human Services','Health Sciences','Humanities','Liberal Studies','Philosophy and Religion','Psychology','Psychology-Counseling and Mental Health','Social Science','Sociology','Supply Chain Management','Urban Studies','Undecided') OR regexp_replace(SCHOOL_PROGRAM,'"','') = '') THEN 'INQ' 
ELSE NULL END AUDIENCE_CODE
,CASE WHEN SUBSTRING(BIRTH_DAY,2,10) = '"' THEN NULL ELSE DAY(TRY_TO_DATE(SUBSTRING(BIRTH_DAY,2,10))) END BIRTH_DAY
,CASE WHEN SUBSTRING(BIRTH_MONTH,2,10) = '""' THEN NULL ELSE MONTH(TRY_TO_DATE(SUBSTRING(BIRTH_DAY,2,10))) END BIRTH_MONTH
,CASE WHEN SUBSTRING(BIRTH_YEAR,2,10) = '"' THEN NULL ELSE YEAR(TRY_TO_DATE(SUBSTRING(BIRTH_DAY,2,10))) END BIRTH_YEAR
,CAMPAIGN_YEAR
,CAMPUS_SOURCE_VALUE CAMPUS
,CAMPUS_TYPE
,HIGH_SCHOOL_CEEB_CODE
,CASE WHEN LENGTH(REGEXP_REPLACE(CELL_PHONE,'[^0-9]','')) = 11 AND STARTSWITH(REGEXP_REPLACE(CELL_PHONE,'[^0-9]',''),'1') THEN RIGHT(REGEXP_REPLACE(CELL_PHONE,'[^0-9]',''),10) ELSE NULL END CELL_PHONE
,regexp_replace(CITY,'"','') CITY
,PARTNER_FILE_NAME CLIENT_FILE_NAME
,regexp_replace(SOURCE_SYSTEM_ID_FOR_THE_PERSON,'"','') SOURCE_SYSTEM_ID_FOR_THE_PERSON
,COHORT_DESC
,COMPLETED_APPLICATION_INDICATOR
,CASE WHEN regexp_replace(COUNTRY_NAME,'"','') = 'United States' THEN 'USA' ELSE NULL END COUNTRY_NAME
,CURRENT_UNDERGRAD_COLLEGE_SENIOR_INDICATOR
,CUSTOM_SEGMENT_01
,CUSTOM_SEGMENT_02
,CUSTOM_SEGMENT_03
,CUSTOM_SEGMENT_04
,DECISION_TYPE
,DEFER_INDICATOR
,DEGREE_TYPE
,DEGREE_TYPE_SOURCE_VALUE
,DENIED_INDICATOR
,DEPOSIT_2_DATE
,DEPOSIT_2_INDICATOR
,DEPOSIT_DATE
,DEPOSIT_INDICATOR
,regexp_replace(EMAIL_ADDRESS,'"','') EMAIL_ADDRESS
,EMAIL_OPT_OUT_INDICATOR
,EMPLOYER
,ENROLL_DATE
,ENROLLED_INDICATOR
,ENROLL_TYPE
,CASE 
WHEN REGEXP_SUBSTR(ENTRY_TERM_SOURCE_VALUE,'March') = 'March' OR REGEXP_SUBSTR(ENTRY_TERM_SOURCE_VALUE,'January') = 'January' OR REGEXP_SUBSTR(ENTRY_TERM_SOURCE_VALUE,'Spring') = 'Spring' THEN 'Spring' 
WHEN REGEXP_SUBSTR(ENTRY_TERM_SOURCE_VALUE,'August') = 'August' OR REGEXP_SUBSTR(ENTRY_TERM_SOURCE_VALUE,'October') = 'October' OR REGEXP_SUBSTR(ENTRY_TERM_SOURCE_VALUE,'Fall') = 'Fall' THEN 'Fall' 
WHEN REGEXP_SUBSTR(ENTRY_TERM_SOURCE_VALUE,'Summer') = 'Summer' THEN 'Summer'
ELSE NULL END ENTRY_TERM
,regexp_replace(ENTRY_TERM_SOURCE_VALUE,'"','') ENTRY_TERM_SOURCE_VALUE
,CASE 
WHEN TRY_TO_NUMBER(LEFT(regexp_replace(ENTRY_YEAR_SOURCE_VALUE,'"',''),4)) IS NOT NULL THEN LEFT(regexp_replace(ENTRY_YEAR_SOURCE_VALUE,'"',''),4)
WHEN TRY_TO_NUMBER(SUBSTRING(regexp_replace(ENTRY_YEAR_SOURCE_VALUE,'"',''),7,4)) IS NOT NULL THEN SUBSTRING(regexp_replace(ENTRY_YEAR_SOURCE_VALUE,'"',''),7,4)
ELSE NULL END ENTRY_YEAR
,regexp_replace(ENTRY_YEAR_SOURCE_VALUE,'"','') ENTRY_YEAR_SOURCE_VALUE
,regexp_replace(RACE,'"','') RACE
,RACE_2
,RACE_3
,RACE_4
,RACE_5
,RACE_6
,CASE 
WHEN regexp_instr(RACE,',') > 1 THEN 'M' 
WHEN regexp_replace(RACE,'"','') = '' THEN 'U' 
WHEN regexp_replace(RACE,'"','') = 'White' THEN 'E' 
WHEN regexp_replace(RACE,'"','') = 'Asian' THEN 'B' 
WHEN regexp_replace(RACE,'"','') = 'Black or African American' THEN 'E' 
WHEN regexp_replace(RACE,'"','') = 'Native Hawaiian or other Pacific Islander' THEN 'A' 
WHEN regexp_replace(RACE,'"','') = 'American Indian or Alaska Native' THEN 'A' 
ELSE 'U' END ETHNICITY_ROLLUP
,DATE_FILE_WAS_RECEIVED_FROM_PARTNER
,regexp_replace(FIRST_NAME,'"','') FIRST_NAME
,FIRST_SOURCE_DESCRIPTION
,regexp_replace(FIRST_SOURCE_SOURCE_VALUE,'"','') FIRST_SOURCE_SOURCE_VALUE
,regexp_replace(FULL_PART_TIME,'"','') FULL_PART_TIME
,CASE WHEN regexp_replace(GENDER_SOURCE_VALUE,'"','') = '' THEN 'U' ELSE regexp_replace(GENDER_SOURCE_VALUE,'"','') END GENDER
,GMAT_COMPOSITE_SCORE
,GMAT_MAJOR
,GMAT_TEST_DATE
,GRE_COMPOSITE_SCORE
,GRE_EXAM_DATE
,GRE_MAJOR
,CASE WHEN LENGTH(REGEXP_REPLACE(HOME_PHONE,'[^0-9]','')) = 11 AND STARTSWITH(REGEXP_REPLACE(HOME_PHONE,'[^0-9]',''),'1') THEN RIGHT(REGEXP_REPLACE(HOME_PHONE,'[^0-9]',''),10) ELSE NULL END HOME_PHONE
,INCOME
,CONCAT(TRY_TO_DATE(regexp_replace(INQUIRY_DATE,'"','')),' 00:00:00') INQUIRY_DATE
,INQUIRY_INDICATOR
,regexp_replace(LAST_NAME,'"','') LAST_NAME
,LEAD_ELIGIBLE_FOR_MARKETING_EXPORT_INDICATOR
,LEAD_ELIGIBLE_FOR_PARTNER_ANALYSIS_INDICATOR
,LSAT_ACCOUNT_IDENTIFIER
,LSAT_COMPOSITE_SCORE
,LSAT_EXAM_DATE
,MAIL_OPT_OUT_INDICATOR
,CASE
WHEN regexp_replace(GENERIC_STAGING_FIELD_01,'"','') = 'PGS' AND regexp_replace(GENERIC_STAGING_FIELD_02,'"','') = 'Yes' AND (regexp_replace(GENERIC_STAGING_FIELD_03,'"','') IN ('First year','First Year International','Transfer','Transfer International','Re- Admit') OR regexp_replace(GENERIC_STAGING_FIELD_03,'"','') = '') AND (regexp_replace(SCHOOL_PROGRAM,'"','') IN ('Accounting','Art – Studio Art','Biology','Business Management','Business Management – Entrepreneurship','Business Management - Marketing Management','Communications','Computer Information Systems','
Criminal Justice','Cybersecurity','Education','Finance','Finance - Corporate','Finance - Personal Financial Management','General Studies','Health Care Management','Human Services','Health Sciences','Humanities','Liberal Studies','Philosophy and Religion','Psychology','Psychology-Counseling and Mental Health','Social Science','Sociology','Supply Chain Management','Urban Studies','Undecided') OR regexp_replace(SCHOOL_PROGRAM,'"','') = '') THEN 1 
ELSE MARKETING_INDICATOR END MARKETING_INDICATOR
,MCAT_ACCOUNT_IDENTIFIER
,MCAT_COMP_SCORE
,MCAT_EXAM_DATE
,regexp_replace(MIDDLE_NAME,'"','') MIDDLE_NAME
,MILITARY_SERVICE_STATUS
,NCH_FILE_INDICATOR
,OPT_OUT_INDICATOR
,OTHER_AUDIENCE_INDICATOR
,OTHER_EXAM_DATE
,OTHER_EXAM_IDENTIFIER
,OTHER_EXAM_SCORE
,regexp_replace(PARENT_EMAIL,'"','') PARENT_EMAIL
,regexp_replace(PARENT_FIRST_NAME,'"','') PARENT_FIRST_NAME
,regexp_replace(PARENT_LAST_NAME,'"','') PARENT_LAST_NAME
,EAB_ID_FROM_PARTNER
,PCAT_ACCOUNT_IDENTIFIER
,PCAT_COMP_SCORE
,PCAT_EXAM_DATE
,regexp_replace(PREFERRED_NAME,'"','') PREFERRED_NAME
,PREFIX
,NULL PREVIOUS_COLLEGE_CEEB_CODE_1
,NULL PREVIOUS_COLLEGE_CEEB_CODE_2
,PREVIOUS_COLLEGE_CEEB_CODE_3
,NULL PREVIOUS_COLLEGE_GPA_1
,NULL PREVIOUS_COLLEGE_GPA_2
,PREVIOUS_COLLEGE_GPA_3
,PREVIOUS_COLLEGE_GRAD_YEAR
,NULL PREVIOUS_COLLEGE_GRAD_YEAR_1
,NULL PREVIOUS_COLLEGE_GRAD_YEAR_2
,PREVIOUS_COLLEGE_GRAD_YEAR_3
,PREVIOUS_COLLEGE_MAJOR
,NULL PREVIOUS_COLLEGE_NAME_1
,NULL PREVIOUS_COLLEGE_NAME_2
,PREVIOUS_COLLEGE_NAME_3
,PREVIOUSLY_ENROLLED_INDICATOR
,HIGH_SCHOOL_GPA
,CASE WHEN regexp_replace(RACE_HISPANIC_LATINO_SOURCE_VALUE,'"','') = 'Yes' THEN 1 ELSE 0 END RACE_HISPANIC_LATINO
,SCHOOL_CONCENTRATION
,SCHOOL_IDENTIFIER
,SCHOOL_NAME
,SCHOOL_OF_STUDY
,CASE WHEN regexp_replace(SCHOOL_PROGRAM,'"','') = '' THEN 'unknown' ELSE regexp_replace(SCHOOL_PROGRAM,'"','') END SCHOOL_PROGRAM
,STARTED_APPLICATION_INDICATOR
,CASE WHEN LEN(regexp_replace(STATE,'"','')) = 2 THEN regexp_replace(STATE,'"','') ELSE NULL END STATE
,STUDENT_TYPE
,regexp_replace(STUDENT_TYPE_SOURCE_VALUE,'"','') STUDENT_TYPE_SOURCE_VALUE
,SUBMITTED_APPLICATION_INDICATOR
,SUFFIX
,TEXT_MESSAGE_OPT_OUT_INDICATOR
,regexp_replace(US_CITIZENSHIP_STATUS,'"','') US_CITIZENSHIP_STATUS
,WITHDRAW_DATE
,WITHDRAWN_INDICATOR
,YEARS_EMPLOYED
,YOUNG_ALUMNI_INDICATOR
,CASE WHEN regexp_replace(ZIP5,'"','') <> '' AND LEN(REGEXP_REPLACE(LEFT(regexp_replace(ZIP5,'"',''),5),'[^0-9]','')) = 5 THEN REGEXP_REPLACE(LEFT(regexp_replace(ZIP5,'"',''),5),'[^0-9]','') ELSE NULL END ZIP5
																				   
--select *
from
<[ALR].[Inquiry]> as a
WHERE regexp_replace(GENERIC_STAGING_FIELD_01,'"','') = 'PGS' AND regexp_replace(GENERIC_STAGING_FIELD_02,'"','') = 'Yes' AND (regexp_replace(GENERIC_STAGING_FIELD_03,'"','') IN ('First year','First Year International','Transfer','Transfer International','Re- Admit') OR regexp_replace(GENERIC_STAGING_FIELD_03,'"','') = '') AND (regexp_replace(SCHOOL_PROGRAM,'"','') IN ('Accounting','Art – Studio Art','Biology','Business Management','Business Management – Entrepreneurship','Business Management - Marketing Management','Communications','Computer Information Systems','Criminal Justice','Cybersecurity','Education','Finance','Finance - Corporate','Finance - Personal Financial Management','General Studies','Health Care Management','Human Services','Health Sciences','Humanities','Liberal Studies','Philosophy and Religion','Psychology','Psychology-Counseling and Mental Health','Social Science','Sociology','Supply Chain Management','Urban Studies','Undecided') OR regexp_replace(SCHOOL_PROGRAM,'"','') = '')