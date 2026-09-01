SELECT 
               population.spriden_id AS STUDENT_ID
               , courses.TERM AS TERM_ID
               , courses.CAMP_CODE AS CAMPUS_CD
               , CASE WHEN courses.TERM_TYPE IN ('ACTUAL','TRANSFER') THEN courses.COLL_CODE
                              ELSE (
                              SELECT aa.scbcrse_coll_code
                              FROM scbcrse aa
                              WHERE aa.scbcrse_subj_code = courses.subj_code AND aa.scbcrse_crse_numb = courses.crse_numb
                                AND aa.scbcrse_eff_term = (
                                             SELECT MAX(scbcrse_eff_term)
                                             FROM scbcrse
                                             WHERE scbcrse_subj_code = courses.subj_code AND scbcrse_crse_numb = courses.crse_numb
                                               AND scbcrse_eff_term <= courses.term)
                              ) END AS COLLEGE_CD
               , CASE WHEN courses.TERM_TYPE IN ('ACTUAL','TRANSFER') THEN courses.DEPT_CODE
                              ELSE (
                              SELECT aa.scbcrse_dept_code
                              FROM scbcrse aa
                              WHERE aa.scbcrse_subj_code = courses.subj_code AND aa.scbcrse_crse_numb = courses.crse_numb
                                AND aa.scbcrse_eff_term = (
                                             SELECT MAX(scbcrse_eff_term)
                                             FROM scbcrse
                                             WHERE scbcrse_subj_code = courses.subj_code AND scbcrse_crse_numb = courses.crse_numb
                                               AND scbcrse_eff_term <= courses.term)
                              ) END AS DEPARTMENT_CD
               , COALESCE(grades.shrgrde_gpa_ind, courses.COUNT_IN_GPA_IND) AS GPA_INCLUDE_IND
               , COALESCE(actualCredits.shrtckg_credit_hours, courses.COURSE_CREDITS, 0) AS COURSE_CREDITS
               , courses.SUBJ_CODE || courses.CRSE_NUMB AS COURSE_CD
               , courses.COURSE_REF_NO AS COURSE_REF_NO
               , CASE 
                              WHEN regInd.stvrsts_incl_sect_enrl = 'Y'                                                                                                                                                                                                                                                                --MODIFY PER MEMBER
                                             OR courses.TERM_TYPE = 'TRANSFER' THEN 'Y'
                              ELSE 'N'
                              END AS REGISTERED_IND                     
               , reg.sfrstcr_rsts_code AS REGISTRATION_STATUS_CD
               , TO_CHAR(COALESCE(reg.sfrstcr_rsts_date, courses.TRANS_REG_DATE, actualCredits.shrtckg_final_grde_chg_date),'yyyymmdd') AS REGISTRATION_STATUS_DATE
               , '' AS FIELD13
               , COALESCE(
                              actualCredits.shrtckg_hours_attempted   --ACTUAL
                              , actualCredits.shrtckg_credit_hours   --TRANSFER
                              , courses.COURSE_CREDITS               --REGISTERED
                              , 0) AS ATTEMPTED_CREDITS
               , COALESCE(
                              actualCredits.shrtckg_credit_hours * DECODE(grades.shrgrde_completed_ind,'Y',1,0) * DECODE(courses.REPEAT_COURSE_IND,'E',0,'A',0,1)   --ACTUAL
                              , courses.COURSE_CREDITS * DECODE(grades.shrgrde_completed_ind,'Y',1,'N',0) * DECODE(courses.REPEAT_COURSE_IND,'E',0,1)      --TRANSFER
                              , 0)                                  --REGISTERED
                              AS EARNED_CREDITS                  --MODIFY PER MEMBER 
               , '' AS FIELD16
               , '' AS FIELD17
               , sectionsMain.ssbsect_gradable_ind AS GRADABLE_IND
               , NVL(reg.sfrstcr_grde_code_mid, '') AS MIDTERM_GRADE
               , COALESCE(actualCredits.shrtckg_grde_code_final, courses.TRANSFER_GRADE,'') AS FINAL_GRADE
               , TO_CHAR(COALESCE(actualCredits.shrtckg_final_grde_chg_date, courses.TRANS_REG_DATE, NULL),'yyyymmdd') AS FINAL_GRADE_DATE
               , CASE WHEN courses.TERM_TYPE IN ('ACTUAL','TRANSFER') THEN 'Y' ELSE 'N' END AS FINAL_GRADE_OFFICIAL_IND
               , '' AS GRADE_POINTS
               , CASE WHEN courses.TERM_TYPE IN ('ACTUAL','REGISTERED') THEN 'Y' ELSE 'N' END AS INST_COURSE_IND
               , CASE WHEN courses.TERM_TYPE IN ('TRANSFER') THEN 'Y' ELSE 'N' END AS TRANSFER_COURSE_IND
               , CASE WHEN courses.TERM_TYPE IN ('REGISTERED') THEN 'Y' ELSE 'N' END AS in_progress_ind
               , CASE WHEN courses.TERM_TYPE = 'TRANSFER' THEN 'TRANSFER'
                              ELSE NVL(sectionsMain.ssbsect_schd_code, 'DEFAULT') END                  -- MODIFY PER MEMBER
                              AS COURSE_TYPE_CD
               , COALESCE(courses.LEVEL_CODE, gradeLink.shrtckl_levl_code, CASE WHEN courses.TERM_TYPE = 'TRANSFER' THEN 'UG' ELSE '' END                --MODIFY PER MEMBER
                              ) AS STUDENT_LEVEL_CD
               , courses.TRANS_INST AS TRANSFERRING_INST
               , NVL(sectionsMain.ssbsect_insm_code, '') AS INSTRUCTION_METHOD
               , courses.REPEAT_COURSE_IND AS REPEAT_IND
               , NVL(TO_CHAR(sectionsMain.ssbsect_ptrm_start_date,'YYYYMMDD'), '') AS SECTION_START_DATE
               , NVL(TO_CHAR(sectionsMain.ssbsect_ptrm_end_date,'YYYYMMDD'), '') AS SECTION_END_DATE
               , NVL(sectionsMain.SSBSECT_MAX_ENRL, '') AS ENROLLMENT_MAX
               , NVL(sectionsMain.SSBSECT_ENRL, '') AS ENROLLMENT_CURRENT
               , NVL(TO_CHAR(sectionsMain.ssbsect_census_enrl_date,'YYYYMMDD'), '') AS ENROLLMENT_CENSUS_DATE
               , instructor.SPRIDEN_ID AS INSTRUCTOR_ID
               , instructor.SPRIDEN_FIRST_NAME AS INSTRUCTOR_FIRST_NAME
               , instructor.SPRIDEN_LAST_NAME AS INSTRUCTOR_LAST_NAME
               , CASE WHEN Core.ssrattr_term_code IS NOT NULL THEN 'Y' ELSE 'N' END AS CORE_IND                                                 
               , CASE WHEN Honors.ssrattr_term_code IS NOT NULL THEN 'Y' ELSE 'N' END AS HONORS_IND                                               
               , CASE WHEN sectionsMain.SSBSECT_INSM_CODE in ('WEB') THEN 'Y' ELSE 'N' END AS ONLINE_IND                                                                  --MODIFY PER MEMBER 
               , '' AS SECTION_ATTRIBUTE
               
FROM ( --STUDENT POPULATION
                              SELECT 
                                             spriden_id, spriden_pidm, terms.max_term, spriden_first_name, spriden_last_name, spriden_mi, SGBSTDN_TERM_CODE_ADMIT
                              FROM (
                                             SELECT spriden_id, spriden_pidm, spriden_first_name, spriden_last_name, spriden_mi FROM spriden WHERE spriden_change_ind is null
                                                            ) spriden
                                             JOIN (
                                                            SELECT pidm, max(term) AS max_term
                                                            FROM (
                                                                           SELECT shrtgpa_pidm AS pidm, shrtgpa_term_code AS term                                                                            FROM shrtgpa WHERE shrtgpa_levl_code = 'UG'   -- MODIFY PER MEMBER
                                                                           UNION ALL SELECT shrtrce_pidm AS pidm, shrtrce_term_code_eff AS term FROM shrtrce WHERE shrtrce_levl_code = 'UG'     -- MODIFY PER MEMBER                                                          
                                                                           UNION ALL SELECT sfrstca_pidm AS pidm, sfrstca_term_code AS term                               FROM sfrstca
                                                                                          JOIN stvterm ON stvterm_code = sfrstca_term_code
                                                                                          AND stvterm_end_date > SYSDATE - 365                                               --INSTATED TO REDUCE RUN TIME                          GOBINTL_NATN_CODE_LEGAL    
                                                                           UNION ALL SELECT sgbstdn_pidm AS pidm, sgbstdn_term_code_eff AS term                FROM sgbstdn WHERE sgbstdn_levl_code = 'UG'  -- MODIFY PER MEMBER
                                                                           ) x
                                                            GROUP BY pidm
                                                            ) terms ON spriden.spriden_pidm = terms.pidm
                                             JOIN STVTERM termDates ON termDates.STVTERM_CODE = terms.max_term
                                             --'WERE THEY EVER UNDERGRADUATES?' FILTER
                                             JOIN (SELECT sgbstdn_pidm, MIN(SGBSTDN_TERM_CODE_ADMIT) AS SGBSTDN_TERM_CODE_ADMIT from sgbstdn 
                                                            WHERE sgbstdn_levl_code = 'UG' GROUP BY sgbstdn_pidm
                                                            ) undergradOnly ON undergradOnly.sgbstdn_pidm = spriden.spriden_pidm
                              GROUP BY spriden_id, spriden_pidm, terms.max_term, spriden_first_name, spriden_last_name, spriden_mi, SGBSTDN_TERM_CODE_ADMIT
               --HISTORICAL FILTER
                              HAVING max(max_term) >= 201310                                                                                                                                                 --ADJUST THIS LINE BY LOOKING UP THE FALL 2000 TERM
               --DAILY FILTER
                              --HAVING max(termDates.STVTERM_START_DATE) > SYSDATE - 365                                          --ADJUST IF WANT DIFFERENT THAN 1 YEAR
                              ) population
               
               --TERM INFO, BACKBONE OF COURSES
               JOIN (    --'ACTUAL' COURSEWORK
                              SELECT 
                                             CASE
                                                            WHEN a.shrtckn_pidm IS NOT NULL THEN 'ACTUAL' 
                                                            ELSE 'REGISTERED'
                                                            END AS TERM_TYPE
                                             , COALESCE(a.shrtckn_pidm,b.sfrstcr_pidm) AS PIDM
                                             , COALESCE(a.shrtckn_term_code,b.sfrstcr_term_code) AS TERM
                                             , COALESCE(a.shrtckn_camp_code,b.sfrstcr_camp_code) AS CAMP_CODE
                                             , COALESCE(a.shrtckn_subj_code,sections.ssbsect_subj_code) AS SUBJ_CODE
                                             , COALESCE(a.shrtckn_crse_numb,sections.ssbsect_crse_numb) AS CRSE_NUMB
                                             , NVL(shrtckn_repeat_course_ind,'N') AS REPEAT_COURSE_IND
                                             , '' COUNT_IN_GPA_IND
                                             , COALESCE(a.shrtckn_crn,b.sfrstcr_crn) AS COURSE_REF_NO
                                             , a.shrtckn_seq_no AS TCKN_SEQ_NO
                                             , b.sfrstcr_credit_hr AS COURSE_CREDITS
                                             , '' AS TRANSFER_GRADE 
                                             , NULL AS TRANS_REG_DATE
                                             , a.shrtckn_dept_code AS DEPT_CODE
                                             , a.shrtckn_coll_code AS COLL_CODE
                                             , '' AS TRANS_INST
                                             , sfrstcr_levl_code as LEVEL_CODE
                                             
                                FROM shrtckn a
                                             FULL OUTER JOIN (
                                                            SELECT * FROM sfrstcr WHERE sfrstcr_levl_code = 'UG'      --MODIFY PER MEMBER
                                                            ) b
                                                            ON a.shrtckn_pidm = b.sfrstcr_pidm
                                                                           AND a.shrtckn_term_code = b.sfrstcr_term_code
                                                                           AND a.shrtckn_crn = b.sfrstcr_crn
                                             LEFT JOIN ( 
                                                            SELECT ssbsect_subj_code, ssbsect_crse_numb, ssbsect_term_code, ssbsect_crn
                                                            FROM ssbsect
                                                            ) sections ON sections.ssbsect_term_code = b.sfrstcr_term_code AND sections.ssbsect_crn = b.sfrstcr_crn
                                             
                              UNION ALL ( --TRANSFER COURSEWORK
                                SELECT
                                             'TRANSFER' AS TERM_TYPE
                                             , shrtrce_pidm AS PIDM
                                             , CASE WHEN b.shrtrcr_term_code = '000000' THEN shrtrce_term_code_eff
                                                            ELSE COALESCE(b.shrtrcr_term_code, shrtrce_term_code_eff,'') END AS TERM
                                             , '' AS CAMP_CODE
                                             , shrtrce_subj_code AS SUBJ_CODE
                                             , shrtrce_crse_numb AS CRSE_NUMB
                                             , shrtrce_repeat_course AS REPEAT_COURSE_IND
                                             , shrtrce_count_in_gpa_ind AS COUNT_IN_GPA_IND
                                             , SHRTRCE_TRIT_SEQ_NO || SHRTRCE_TRAM_SEQ_NO || SHRTRCE_SEQ_NO || SHRTRCE_TRCR_SEQ_NO AS COURSE_REF_NO
                                             , 0 AS TCKN_SEQ_NO
                                             , shrtrce_credit_hours AS COURSE_CREDITS
                                             , shrtrce_grde_code AS TRANSFER_GRADE
                                             , STVTERM_START_DATE AS TRANS_REG_DATE
                                             , '' AS DEPT_CODE
                                             , '' AS COLL_CODE
                                             , transInst.STVSBGI_DESC as TRANS_INST
                                             , shrtrce_levl_code as LEVEL_CODE
                                               
                                 FROM shrtrce a
                                                            LEFT JOIN (
                                                                           SELECT shrtrcr_pidm, shrtrcr_trit_seq_no, shrtrcr_tram_seq_no, shrtrcr_seq_no, shrtrcr_term_code, STVTERM_START_DATE
                                                                           FROM shrtrcr 
                                                                                          JOIN stvterm ON stvterm_code = shrtrcr_term_code
                                                                           WHERE shrtrcr_levl_code = 'UG'     -- MODIFY PER MEMBER
                                                                           ) b ON b.shrtrcr_pidm = a.shrtrce_pidm AND b.shrtrcr_trit_seq_no = a.shrtrce_trit_seq_no 
                                                                                          AND b.shrtrcr_tram_seq_no = a.shrtrce_tram_seq_no AND b.shrtrcr_seq_no = a.shrtrce_trcr_seq_no
                                                            LEFT JOIN (
                                                                           SELECT SHRTRIT_PIDM, SHRTRIT_SEQ_NO, STVSBGI.STVSBGI_DESC
                                                                           FROM SHRTRIT
                                                                                          JOIN STVSBGI ON STVSBGI.STVSBGI_CODE = SHRTRIT_SBGI_CODE
                                                                           ) transInst ON transInst.SHRTRIT_PIDM = a.shrtrce_pidm AND transInst.SHRTRIT_SEQ_NO = a.shrtrce_trit_seq_no
                                             WHERE shrtrce_levl_code = 'UG'           -- MODIFY PER MEMBER
                              )
                              ) courses 
                                             ON courses.PIDM = population.spriden_pidm
                                                            AND courses.TERM <= population.max_term
                              
               --'ACTUAL' COURSE CREDITS
               LEFT OUTER JOIN (
                              SELECT gr.shrtckg_pidm, gr.shrtckg_term_code, gr.shrtckg_tckn_seq_no, gr.shrtckg_credit_hours
                                             , gr.shrtckg_grde_code_final, gr.shrtckg_final_grde_chg_date, gr.shrtckg_hours_attempted
                              FROM shrtckg gr
                              WHERE shrtckg_seq_no = (
                                             SELECT MAX(shrtckg_seq_no) FROM shrtckg bb WHERE bb.shrtckg_pidm = gr.shrtckg_pidm 
                                                            AND bb.shrtckg_tckn_seq_no = gr.shrtckg_tckn_seq_no AND bb.shrtckg_term_code = gr.shrtckg_term_code)
                              ) actualCredits ON actualCredits.shrtckg_pidm = courses.PIDM AND actualCredits.shrtckg_term_code = courses.TERM 
                                             AND actualCredits.shrtckg_tckn_seq_no = courses.TCKN_SEQ_NO 
               
               --REGISTRATION INFO
               LEFT OUTER JOIN (
                              SELECT a.sfrstcr_pidm, a.sfrstcr_term_code, a.sfrstcr_crn, a.sfrstcr_rsts_code, a.sfrstcr_rsts_date, a.sfrstcr_grde_code_mid 
                              FROM sfrstcr a
                              WHERE sfrstcr_rsts_date = (
                                SELECT MAX(sfrstcr_rsts_date) FROM sfrstcr bb WHERE bb.sfrstcr_pidm = a.sfrstcr_pidm 
                                             AND bb.sfrstcr_term_code = a.sfrstcr_term_code AND bb.sfrstcr_crn = a.sfrstcr_crn)
                              ) reg ON reg.sfrstcr_pidm = courses.PIDM AND reg.sfrstcr_term_code = courses.TERM AND reg.sfrstcr_crn = courses.COURSE_REF_NO
               
               --REGISTERED INDICATOR
               LEFT OUTER JOIN stvrsts regInd
                              ON regInd.stvrsts_code = reg.sfrstcr_rsts_code
               
               --LINK TO GET GRADES
               LEFT OUTER JOIN (
                              SELECT shrtckl_pidm, shrtckl_term_code, shrtckl_tckn_seq_no, shrtckl_levl_code 
                              FROM shrtckl
                              ) gradeLink ON gradeLink.shrtckl_pidm = courses.PIDM AND gradeLink.shrtckl_term_code = courses.TERM 
                                             AND gradeLink.shrtckl_tckn_seq_no = courses.TCKN_SEQ_NO
               
               --'ACTUAL' COURSE GRADES
               LEFT OUTER JOIN (
                              SELECT DISTINCT a.shrgrde_code, a.shrgrde_levl_code, a.shrgrde_completed_ind, a.shrgrde_gpa_ind
                              FROM shrgrde a
                              WHERE shrgrde_term_code_effective = (
                                             SELECT MAX(shrgrde_term_code_effective) FROM shrgrde bb WHERE bb.shrgrde_code = a.shrgrde_code 
                                                            AND shrgrde_levl_code = a.shrgrde_levl_code)
                              ) grades ON grades.shrgrde_code = COALESCE(actualCredits.shrtckg_grde_code_final, courses.TRANSFER_GRADE)
                                             AND grades.shrgrde_levl_code = COALESCE(
                                                            gradeLink.shrtckl_levl_code, CASE WHEN courses.TERM_TYPE = 'TRANSFER' THEN 'UG' ELSE '' END)                                                        --MODIFY PER MEMBER
               
               --SECTION START/END DATES, ENROLLMENT, AND GRADABLE_IND
               LEFT OUTER JOIN (
                              SELECT 
                                             ssbsect_schd_code, ssbsect_term_code, ssbsect_crn, SSBSECT_INSM_CODE, SSBSECT_PTRM_START_DATE
                                             , SSBSECT_PTRM_END_DATE, SSBSECT_MAX_ENRL, SSBSECT_ENRL, ssbsect_census_enrl_date, SSBSECT_GRADABLE_IND 
                              FROM ssbsect
                              ) sectionsMain ON sectionsMain.ssbsect_term_code = courses.TERM AND sectionsMain.ssbsect_crn = courses.COURSE_REF_NO
                              
               --CORE
               LEFT OUTER JOIN (
                              SELECT  DISTINCT ssrattr_crn, ssrattr_term_code
                              FROM SSRATTR
                              WHERE ssrattr_attr_code IN ('CORE')
                              ) Core ON Core.ssrattr_term_code = courses.TERM AND Core.ssrattr_crn = courses.COURSE_REF_NO
                              
               --HONORS
               LEFT OUTER JOIN (
                              SELECT DISTINCT ssrattr_crn, ssrattr_term_code
                              FROM SSRATTR
                              WHERE ssrattr_attr_code IN ('HC','HP')
                              ) Honors ON Honors.ssrattr_term_code = courses.TERM AND Honors.ssrattr_crn = courses.COURSE_REF_NO
                              
               --INSTRUCTOR ID, FIRST NAME, LAST NAME
               LEFT OUTER JOIN (
                              SELECT SIRASGN_TERM_CODE, SIRASGN_CRN, SIRASGN_PIDM, SIRASGN_PRIMARY_IND, SPRIDEN_ID, SPRIDEN_FIRST_NAME, SPRIDEN_LAST_NAME
                              FROM SIRASGN a
                                             JOIN SPRIDEN b ON a.sirasgn_pidm = b.spriden_pidm AND b.spriden_change_ind IS NULL  
      WHERE SIRASGN_CATEGORY = '01' AND SIRASGN_PRIMARY_IND = 'Y'
                              ) instructor ON instructor.sirasgn_term_code = courses.term AND instructor.sirasgn_crn = courses.COURSE_REF_NO             
                              
WHERE 1=1
  --AND population.spriden_id = '900070789'
  --AND courses.TERM = '200203'

ORDER BY  1, 2
