SELECT 
SEC_TERM AS term_code
, '' AS session_code
, SEC_SYNONYM AS course_reference_number
, COALESCE(SEC_LOCATION,'NA') AS campus_code
, SEC_SUBJECT AS subject_code
, SEC_COURSE_NO AS course_number
, SEC_CRED_TYPE AS course_type_code
, REPLACE(SEC_SHORT_TITLE,'ý',' ') AS section_title
, SEC_NO AS section_name
, CAST(COALESCE(SEC_SCHED_CAPACITY,0) as INTEGER) AS seats_offered
, NULL AS waitlist_capacity
, COALESCE(SEC_CAPACITY,0)  AS current_capacity
, NULL AS gradable_ind
, SEC_CRED_TYPE AS instructional_method_code
, NULL AS enrollment_census
, NULL AS enrollment_census_date
, '' AS crosslist_group
, '' AS section_tag
, CSF.CSF_FACULTY AS primary_instructor_id
, '{"SectionId":"' || CS.COURSE_SECTIONS_ID || '", "CourseId":"' || CS.SEC_COURSE || '"}' AS sis_properties
, CAST(COALESCE(SEC_OVR_REG_START_DATE,T.TERM_REG_START_DATE) as DATE) AS drop_start_date
, CAST(COALESCE(CS.SEC_OVR_DROP_END_DATE,T.TERM_DROP_END_DATE)as DATE) AS drop_end_date
, CAST(SEC_OVR_DROP_END_DATE as DATE) AS withdrawal_end_date
, '' AS section_attribute
, '' AS credit_hours
, NULL AS hidden_ind
, current_date AS create_date
, current_date  AS update_date
, CASE
	WHEN s.SEC_STATUS = 'A' THEN TRUE
	ELSE FALSE
	END AS active_ind 

FROM (select * from 
	(select *,row_number() over (partition by COURSE_SECTIONS_ID order by observation_tm desc) as rn from <passthrough.[Colleague].[COURSE_SECTIONS]> as COURSE_SECTIONS
	where COURSE_SECTIONS.observation_tm > <PIT_START_TIME> and COURSE_SECTIONS.observation_tm<= <PIT_END_TIME>
	) CS where CS.rn=1
	)CS 

LEFT JOIN (select * from 
		(select *,row_number() over (partition by COURSE_SECTIONS_ID,POS order by observation_tm desc) as rn from <passthrough.[Colleague].[SEC_STATUSES]> as s
		where s.observation_tm > <PIT_START_TIME> and s.observation_tm<= <PIT_END_TIME>
		)s where s.rn=1  
		)s on CS.COURSE_SECTIONS_ID = s.COURSE_SECTIONS_ID and s.POS = 1

LEFT OUTER JOIN (
	SELECT *
	, ROW_NUMBER() OVER (PARTITION BY CSF_COURSE_SECTION ORDER BY CSF_FACULTY_LOAD DESC) rn
	FROM (select *,row_number() over (partition by COURSE_SEC_FACULTY_ID order by observation_tm desc) as dup_rn from <passthrough.[Colleague].[COURSE_SEC_FACULTY]> as CSF
		where CSF.observation_tm > <PIT_START_TIME> and CSF.observation_tm<= <PIT_END_TIME>
	) CSF where CSF.dup_rn=1
	)CSF ON CSF.CSF_COURSE_SECTION=CS.COURSE_SECTIONS_ID AND CSF.rn = 1

LEFT JOIN (select * from 
		  (select *,row_number() over (partition by TERMS_ID order by observation_tm desc) as rn from <passthrough.[Colleague].[TERMS]> as T
		  where T.observation_tm > <PIT_START_TIME> and T.observation_tm<= <PIT_END_TIME>
		  )T where T.rn=1
		  ) T ON CS.SEC_TERM = T.TERMS_ID 

WHERE 1=1
AND CS.SEC_TERM IS NOT NULL
--and CS.SEC_ACAD_LEVEL = 'UG'
and s.SEC_STATUS = 'A'

ORDER BY 1 DESC, 2