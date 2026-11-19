select
subject_code,
course_number,
term_code_eff,
course_title,
department_code,
college_code

from(
select distinct
C.CRS_SUBJECT as subject_code
, C.CRS_NO as course_number
, '2020SP' as term_code_eff
, CAST(REPLACE(CRS_TITLE,'ý',' ') as varchar(240))  as course_title

, COALESCE(d.SEC_DEPTS,'NA') as department_code
, DEPTS.DEPTS_DIVISION as college_code
, row_number() over(partition by C.crs_subject,C.crs_no order by C.courses_id desc) as cn
from 
(select * from
(select *,row_number() over (partition by courses_id order by observation_tm desc) as rn from <passthrough.[Colleague].[COURSES]> as C
where C.observation_tm > <PIT_START_TIME> and C.observation_tm <= <PIT_END_TIME>
)C where C.rn=1
)C
 JOIN
(
	SELECT *
	FROM (
		SELECT DISTINCT
		CRS_SUBJECT
		, c.CRS_NO
		, d.SEC_DEPTS
		, row_number() OVER (PARTITION BY CRS_SUBJECT, CRS_NO ORDER BY cs.SEC_TERM desc, d.SEC_DEPTS) rn

		FROM (select * from
						(select *,row_number() over (partition by courses_id order by observation_tm desc) as c_dup_rn from <passthrough.[Colleague].[COURSES]> as C
						where C.observation_tm > <PIT_START_TIME> and C.observation_tm <= <PIT_END_TIME>
						)C where C.c_dup_rn=1
			)C
		
		left JOIN (select * from 
					(select *,row_number() over (partition by course_sections_id order by observation_tm desc) as cs_dup_rn from <passthrough.[Colleague].[COURSE_SECTIONS]> as CS 
					where CS.observation_tm > <PIT_START_TIME> and CS.observation_tm <= <PIT_END_TIME>
					) cs where cs.cs_dup_rn=1 
				  )cs ON C.CRS_SUBJECT = cs.SEC_SUBJECT AND C.CRS_NO = cs.SEC_COURSE_NO
		
		LEFT OUTER JOIN (select * from 
						(select *,row_number() over (partition by course_sections_id,pos order by observation_tm desc) as d_dup_rn from <passthrough.[Colleague].[SEC_DEPARTMENTS]> as d
						where d.observation_tm > <PIT_START_TIME> and d.observation_tm <= <PIT_END_TIME>
						)d where d.d_dup_rn=1
						)d ON cs.COURSE_SECTIONS_ID = d.COURSE_SECTIONS_ID AND d.POS = 1 ---- changed to left join because this was filtering courses without a dept code
		
		WHERE 1=1 
	) x
	WHERE rn = 1
) d ON C.CRS_SUBJECT = d.CRS_SUBJECT AND C.CRS_NO = d.CRS_NO

LEFT OUTER JOIN (select * from
				(select * ,row_number() over (partition by depts_id order by observation_tm desc) as depts_dup_rn from <passthrough.[Colleague].[DEPTS]> as DEPTS 
				where DEPTS.observation_tm > <PIT_START_TIME> and DEPTS.observation_tm <= <PIT_END_TIME>
				)DEPTS where DEPTS.depts_dup_rn=1
				)DEPTS ON d.SEC_DEPTS = DEPTS.DEPTS_ID

--LEFT OUTER JOIN APPROVAL_STATUS aps ON C.COURSES_ID = aps.COURSES_ID AND aps.POS = 1

WHERE 1=1
--and C.CRS_ACAD_LEVEL = 'UG'
AND COALESCE(C.CRS_START_DATE,'1990-01-01') = (
												SELECT MAX(COALESCE(CRS_START_DATE,'1990-01-01'))
												FROM (select * from  
																	(select *,row_number() over (partition by courses_id order by observation_tm desc) as rn from <passthrough.[Colleague].[COURSES]> as COURSES
													 				where COURSES.observation_tm > <PIT_START_TIME> and COURSES.observation_tm <= <PIT_END_TIME>
													 				)COURSES where COURSES.rn=1
													)COURSES
												WHERE CRS_SUBJECT = C.CRS_SUBJECT
												AND CRS_NO = C.CRS_NO
												)
)courses
where cn = 1

UNION ALL
select  distinct
'TR' as subject_code
, '001' as course_number
, '2019FA' as term_code_eff
, 'Transfer Course' as course_title
, '' as department_code
, '' as college_code
 FROM (select * from  
					(select *,row_number() over (partition by courses_id order by observation_tm desc) as rn from <passthrough.[Colleague].[COURSES]> as COURSES
					where COURSES.observation_tm > <PIT_START_TIME> and COURSES.observation_tm <= <PIT_END_TIME>
					)COURSES where COURSES.rn=1
	  )COURSES;