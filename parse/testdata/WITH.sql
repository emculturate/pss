WITH

-- ============================================================
-- CTE 1: max-effective scbcrse per subject+course
-- Replaces the correlated subquery in the final view's WHERE
-- using ROW_NUMBER (want exactly 1 row per subject/course
-- at the highest eff_term ROW_NUMBER chosen over RANK to
-- guarantee a single row when two terms share the same max).
-- ============================================================
scbcrse_ranked AS (
  SELECT
    scbcrse_subj_code
  , scbcrse_crse_numb
  , scbcrse_eff_term
  , scbcrse_title
  , scbcrse_coll_code
  , COALESCE(scbcrse_dept_code, 'NA')  AS department_code
  , scbcrse_csta_code
  , scbcrse_credit_hr_low
  , scbcrse_credit_hr_high
  , ROW_NUMBER() OVER (
      PARTITION BY scbcrse_subj_code, scbcrse_crse_numb
      ORDER BY scbcrse_eff_term DESC
    ) AS rn
  FROM <[BANNER_INTAKE].[Course SCBCRSE]> as a
)

-- ============================================================
-- CTE 2: most-recent scbdesc per subject+course (LEFT-joinable)
-- catawba_override uses sh.sh = 1 on a ROW_NUMBER partitioned
-- by subj/crse ordered by eff_term DESC — replicate exactly.
-- ============================================================
, scbdesc_ranked AS (
  SELECT
    scbdesc_subj_code
  , scbdesc_crse_numb
  , scbdesc_text_narrative
  , ROW_NUMBER() OVER (
      PARTITION BY scbdesc_subj_code, scbdesc_crse_numb
      ORDER BY scbdesc_term_code_eff DESC
    ) AS rn
  FROM <[BANNER_INTAKE].[Course Catalog Description Narrative Text Table SCBDESC]> as a
)

-- ============================================================
-- CTE 3: cat_course equivalent — inlines
--        catawba_override_banner_cat_course logic.
-- Only columns consumed by the final view are retained:
--   subject_code, course_number, term_code_eff,
--   college_code, department_code, course_status_code,
--   course_min_credits, course_max_credits
-- (course_desc / course_title retained for completeness of
--  the cat_course grain but not projected to final output.)
-- ============================================================
, cat_course_inline AS (
  SELECT DISTINCT
    s.scbcrse_subj_code                                    AS subject_code
  , s.scbcrse_crse_numb                                    AS course_number
  , s.scbcrse_eff_term                                     AS term_code_eff
  , s.scbcrse_title                                        AS course_title
  , s.scbcrse_coll_code                                    AS college_code
  , s.department_code                                      AS department_code
  , s.scbcrse_csta_code                                    AS course_status_code
  , s.scbcrse_credit_hr_low                                AS course_min_credits
  , s.scbcrse_credit_hr_high                               AS course_max_credits
  , CASE
      WHEN s.scbcrse_csta_code = 'A' THEN TRUE
      ELSE FALSE
    END                                                    AS active_ind
  FROM scbcrse_ranked s
  LEFT JOIN scbdesc_ranked sh
    ON  s.scbcrse_subj_code = sh.scbdesc_subj_code
    AND s.scbcrse_crse_numb = sh.scbdesc_crse_numb
    AND sh.rn = 1
  WHERE s.rn = 1   -- max-effective term row only
)

-- ============================================================
-- CTE 4: cat_section equivalent — inlines banner_cat_section.
-- Only columns consumed by the final view are retained:
--   subject_code, course_number, course_type_code, active_ind
-- All other columns (instructor, dates, capacity, etc.) are
-- pruned per Rule 1.
-- ============================================================
, cat_section_inline AS (
  SELECT DISTINCT
    eab_ssb.ssbsect_subj_code                              AS subject_code
  , eab_ssb.ssbsect_crse_numb                             AS course_number
  , eab_ssb.ssbsect_schd_code                             AS course_type_code
  , CASE
      WHEN eab_ssb.ssbsect_ssts_code IN ('X','I','C') THEN FALSE
      ELSE TRUE
    END                                                    AS active_ind
  FROM <[BANNER_INTAKE].[Section General Information SSBSECT]> eab_ssb
  -- sirasgn, spriden, stvterm, sfrrsts, stvrsts, ssrxlst joins
  -- all pruned: none feed subject_code / course_number /
  -- course_type_code / active_ind columns used by the final view.
  -- (See ai_notes for per-join keep/drop rationale.)
)

-- ============================================================
-- CTE 5: section source union — mirrors the inline UNION ALL
-- in the final view's subquery alias 's'.
--   Branch A: section-level rows from cat_section_inline
--   Branch B: course-level rows synthesised from cat_course
--             (subject/course with 'NA' type and active=TRUE)
-- UNION ALL preserved: branches are structurally distinct
-- (different course_type_code values) and downstream DISTINCT
-- in the final view deduplicates the final grain.
-- ============================================================
, section_union AS (
  SELECT DISTINCT
    subject_code
  , course_number
  , course_type_code
  , active_ind
  FROM cat_section_inline

  UNION ALL

  SELECT DISTINCT
    subject_code
  , course_number
  , 'NA'   AS course_type_code
  , TRUE   AS active_ind
  FROM cat_course_inline
)

-- ============================================================
-- Final SELECT — mirrors campus_course view exactly.
-- ============================================================
SELECT DISTINCT
  c.college_code         AS College_Cd
, c.subject_code         AS Subject_Cd
, c.course_number        AS Course_No
, c.course_title         AS Title
, c.course_min_credits   AS Credit_Min
, c.course_max_credits   AS Credit_Max
, c.department_code      AS Department_Cd
, c.course_status_code   AS Course_Status_Cd
, s.course_type_code     AS Course_Type_Cd

FROM cat_course_inline c
JOIN section_union s
  ON  c.subject_code  = s.subject_code
  AND c.course_number = s.course_number
  AND s.active_ind IS TRUE

ORDER BY 1;