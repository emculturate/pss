/*This query source data from partner tables & deliver to <[Enroll360].[Census Student Term Attributes].[Last Validated].{final}>
It chooses the most recent record across all entities for use in the individual data points. 

Note: All CTEs include WHERE clauses to exclude records where source_value is NULL, 
ensuring only meaningful data is getting prioritized in row_number.

JOIN Strategy: Changed all joins from LEFT JOIN to INNER JOIN in the final UNION statements 
because we only want records with populated attribute data. LEFT JOINs were creating rows 
with NULL attribute values when students had no data for specific attribute categories, 
which does not provide useful information for reporting and analysis. */


/* These CTEs are for getting these individual tables
on the correct granularity so that they can join to
par__student_term without duplicates.  This is
accomplished by assigning latest_record = 1 to the
most recent record via intake_dt */


WITH 
-- 1 Expense Group
cte_expense_group AS (
    SELECT
        pst.partnercontact_id, 
        pst.partner_system_name, 
        pst.eab_entry_year_academic, 
        pst.eab_entry_term, 
        pst.eab_student_type,
        'fao_expense' AS eab_attribute_group,
        exp.expense_group_key AS src_attribute_type, -- Maps to source attribute type identifier for expense group categorization. As per documentation, we do not have any column as expense_key. this can be a hardcoded value if required.
        'expense_group' AS eab_attribute_type,
        exp.expense_group_label AS source_value,
        exp.expense_group_key AS eab_std_value,
        exp.intake_dt AS attribute_dt,
        ROW_NUMBER() OVER (PARTITION BY pst.partnercontact_id, pst.partner_system_name, pst.eab_entry_year_academic, pst.eab_entry_term, pst.eab_student_type ORDER BY exp.intake_dt DESC NULLS LAST) AS latest_record
    FROM <[PDP_UG].[par__student_term].{final}> AS pst
    INNER JOIN <[PDP_UG].[par__student_year_financial_aid].{final}> AS psyfa --joining to this table to get exp_grp_key & to be able to join to exp
        ON pst.partnercontact_id     = psyfa.partnercontact_id
        AND pst.partner_system_name   = psyfa.partner_system_name
        AND pst.eab_entry_year_academic = psyfa.eab_entry_year_academic
    INNER JOIN <[PDP_UG].[par__expense_breakdown].{final}> AS exp
        ON LOWER(exp.expense_group_label) = LOWER(psyfa.eab_expense_group_key)  -- Join to bring attribute values
        AND exp.eab_entry_year_academic   = psyfa.eab_entry_year_academic      -- Added to avoid duplicates
    WHERE exp.expense_group_label IS NOT NULL
),

-- 2️ Funds
cte_funds AS (
    SELECT
        psf.partnercontact_id, 
        psf.partner_system_name, 
        psf.eab_entry_year_academic,
        'funds' AS eab_attribute_group,
        psf.EAB_COHORT_FUND_TYPE AS src_attribute_type, -- This is as per documentation. It maps to source attribute type identifier for cohort fund
        psf.EAB_FUND_TYPE AS eab_attribute_type,
        psf.eab_fund_amount AS source_value,
        psf.eab_fund_amount AS eab_std_value,
        psf.intake_dt AS attribute_dt,
        ROW_NUMBER() OVER(PARTITION BY psf.partnercontact_id, psf.partner_system_name, psf.eab_entry_year_academic ORDER BY psf.intake_dt DESC NULLS LAST) AS latest_record
    FROM <[PDP_UG].[par__student_year_funds].{final}> AS psf
    WHERE psf.eab_fund_amount IS NOT NULL
),

-- 3️ Ability to Pay
cte_ability_to_pay AS (
    SELECT
        finaid.partnercontact_id,
        finaid.partner_system_name,
        finaid.eab_entry_year_academic,
        'ability_to_pay' AS eab_attribute_group,
        finaid.EAB_FINANCIAL_NEED AS src_attribute_type,
        'financial_need' AS eab_attribute_type,
        COALESCE(finaid.css_profile_need, 0) + COALESCE(finaid.fafsa_need, 0) AS source_value,
        finaid.eab_financial_need AS eab_std_value,
        finaid.intake_dt AS attribute_dt,
        ROW_NUMBER() OVER (PARTITION BY finaid.partnercontact_id, finaid.partner_system_name, finaid.eab_entry_year_academic ORDER BY finaid.intake_dt DESC NULLS LAST) AS latest_record
    FROM <[PDP_UG].[par__student_year_finaid_application].{final}> AS finaid
    WHERE (COALESCE(finaid.css_profile_need, 0) + COALESCE(finaid.fafsa_need, 0)) IS NOT NULL
),

-- 4️ HS GPA
cte_hs_gpa AS (
    SELECT
        phs.partnercontact_id,
        phs.partner_system_name,
        'academic_index' AS eab_attribute_group,
        phs.eab_hs_gpa_type AS src_attribute_type,
        'hs_gpa_score' AS eab_attribute_type,
        phs.hs_gpa_nbr AS source_value,
        phs.hs_gpa_nbr AS eab_std_value, --eab_hs_gpa_nbr is not present in this table.therefore, taking its source value
        phs.intake_dt AS attribute_dt,
        ROW_NUMBER() OVER (PARTITION BY phs.partnercontact_id, phs.partner_system_name ORDER BY phs.intake_dt DESC NULLS LAST) AS latest_record
    FROM <[PDP_UG].[par__student_high_school].{final}> AS phs
    WHERE phs.hs_gpa_nbr IS NOT NULL

),

-- 5️ Test Scores
cte_test_scores AS (
    SELECT
        pts.partnercontact_id,
        pts.partner_system_name,
        'academic_index' AS eab_attribute_group,
        pts.exam_code AS src_attribute_type,
        pts.eab_exam_type AS eab_attribute_type,
        pts.exam_score AS source_value,
        pts.eab_exam_score AS eab_std_value,
        pts.intake_dt AS attribute_dt,
        ROW_NUMBER() OVER (PARTITION BY pts.partnercontact_id, pts.partner_system_name ORDER BY pts.intake_dt DESC NULLS LAST) AS latest_record
    FROM <[PDP_UG].[par__student_test_scores].{final}> AS pts
    WHERE pts.exam_score IS NOT NULL
),

-- 6️ Class Rank
cte_class_rank AS (
    SELECT
        phs.partnercontact_id,
        phs.partner_system_name,
        'academic_index' AS eab_attribute_group,
        'class_rank_score' AS src_attribute_type,
        'class_rank_score' AS eab_attribute_type,
        phs.hs_class_rank AS source_value,
        phs.eab_class_rank AS eab_std_value,
        phs.intake_dt AS attribute_dt,
        ROW_NUMBER() OVER (PARTITION BY phs.partnercontact_id, phs.partner_system_name ORDER BY phs.intake_dt DESC NULLS LAST) AS latest_record
    FROM <[PDP_UG].[par__student_high_school].{final}> AS phs
    WHERE phs.hs_class_rank IS NOT NULL
),

-- 7️ Alternative Academic Score
cte_alt_academic AS (
    SELECT
        pst.partnercontact_id, 
        pst.partner_system_name,
        pst.eab_entry_year_academic,
        pst.eab_entry_term,
        pst.eab_student_type,
        'academic_index' AS eab_attribute_group,
        'alternative_academic_score' AS src_attribute_type,
        'alternative_academic_score' AS eab_attribute_type,
        pst.alternative_academic_rank AS source_value,
        pst.eab_alternative_academic_rank AS eab_std_value,
        pst.intake_dt AS attribute_dt,
        ROW_NUMBER() OVER (PARTITION BY pst.partnercontact_id, pst.partner_system_name, pst.eab_entry_year_academic, pst.eab_entry_term, pst.eab_student_type ORDER BY pst.intake_dt DESC NULLS LAST) AS latest_record
    FROM <[PDP_UG].[par__student_term].{final}> AS pst 
    WHERE pst.alternative_academic_rank IS NOT NULL   
),

-- 8️ Aid App
cte_aid_app AS (
    SELECT
         finaid.partnercontact_id,
         finaid.partner_system_name,
         finaid.eab_entry_year_academic,
        'aid_app' AS eab_attribute_group,
        'aid_app' AS src_attribute_type,
        'aid_app' AS eab_attribute_type,
         finaid.fin_aid_app_status AS source_value,
         finaid.eab_aid_app_ind AS eab_std_value,
         finaid.intake_dt AS attribute_dt,
         ROW_NUMBER() OVER (PARTITION BY finaid.partnercontact_id, finaid.partner_system_name, finaid.eab_entry_year_academic ORDER BY finaid.intake_dt DESC NULLS LAST) AS latest_record
    FROM <[PDP_UG].[par__student_year_finaid_application].{final}> AS finaid
    WHERE finaid.fin_aid_app_status IS NOT NULL
),

-- 9️ Race
cte_race AS (
    SELECT
        psr.partnercontact_id,
        psr.partner_system_name,
        'demographic' AS eab_attribute_group,
        'race' AS src_attribute_type,
        'race' AS eab_attribute_type,
        psr.race AS source_value,
        psr.eab_race AS eab_std_value,
        psr.intake_dt AS attribute_dt,
        ROW_NUMBER() OVER (PARTITION BY psr.partnercontact_id,psr.partner_system_name ORDER BY psr.intake_dt DESC NULLS LAST) AS latest_record
    FROM <[PDP_UG].[par__student_race].{final}> AS psr
    WHERE psr.race IS NOT NULL
),

-- 10 Academic Interest
cte_academic_interest AS (
    SELECT DISTINCT
        psta.partnercontact_id,
        psta.partner_system_name,
        psta.eab_entry_term,
        psta.eab_entry_year_academic,
        psta.eab_student_type,
        'pdp_calculation' AS eab_attribute_group,
        'academic_interest' AS src_attribute_type,
        'academic_interest' AS eab_attribute_type,
        psta.major AS source_value,
        psta.eab_major AS eab_std_value,
        psta.intake_dt AS attribute_dt,
        ROW_NUMBER() OVER (PARTITION BY psta.partnercontact_id, psta.partner_system_name, psta.eab_entry_term, psta.eab_entry_year_academic, psta.eab_student_type ORDER BY psta.intake_dt DESC NULLS LAST) AS latest_record
    FROM  <[PDP_UG].[par__student_term_application].{final}> AS psta
    WHERE psta.major IS NOT NULL    
),

-- 11 Campus Visit
cte_campus_visit AS (
    SELECT
        ps.partnercontact_id,
        ps.partner_system_name,
        'pdp_calculation' AS eab_attribute_group,
        'campus_visit_date' AS src_attribute_type,
        'campus_visit_date' AS eab_attribute_type,
        ps.first_campus_visit_dt AS source_value,
        ps.first_campus_visit_dt AS eab_std_value,
        ps.intake_dt AS attribute_dt,
        ROW_NUMBER() OVER (PARTITION BY ps.partnercontact_id,ps.partner_system_name ORDER BY ps.intake_dt DESC NULLS LAST) AS latest_record
    FROM <[PDP_UG].[par__student].{final}> AS ps
    WHERE ps.first_campus_visit_dt IS NOT NULL
),
-- to get the details of common columns in final union & prevent re-writting the code multiple times
cte_common_stu_info AS
( 
    SELECT 
        ps.primary_student_id,
        pst.partnercontact_id,
        pst.partner_system_name,
        pst.curr_entry_year_academic AS entry_year_academic,
        pst.eab_entry_year_academic,	
        pst.curr_entry_term AS entry_term,	
        pst.eab_entry_term, 
        pst.student_type,	
        pst.eab_student_type
    FROM <[PDP_UG].[par__student_term].{final}> AS pst
    INNER JOIN <[PDP_UG].[par__student].{final}> AS ps
        ON pst.partnercontact_id = ps.partnercontact_id
    AND pst.partner_system_name = ps.partner_system_name
)

-- Final Union (All columns consistently casted)
SELECT 
    CAST(stu_info.primary_student_id AS VARCHAR) AS primary_student_id,	
    CAST(stu_info.entry_year_academic AS VARCHAR) AS entry_year_academic,
    CAST(stu_info.eab_entry_year_academic AS INTEGER) AS eab_entry_year_academic,	
    CAST(stu_info.entry_term AS VARCHAR) AS entry_term,	
    CAST(stu_info.eab_entry_term AS VARCHAR) AS eab_entry_term, 
    CAST(stu_info.student_type AS VARCHAR) AS student_type,	
    CAST(stu_info.eab_student_type AS VARCHAR) AS eab_student_type,	
    CAST(exp.eab_attribute_group AS VARCHAR) AS eab_attribute_group,	
    CAST(exp.src_attribute_type AS VARCHAR) AS src_attribute_type,	
    CAST(exp.eab_attribute_type AS VARCHAR) AS eab_attribute_type,	
    CAST(exp.source_value AS VARCHAR) AS source_value,	
    CAST(exp.eab_std_value AS VARCHAR) AS eab_std_value,	
    CAST(exp.attribute_dt AS TIMESTAMP) AS attribute_dt
FROM cte_common_stu_info AS stu_info
INNER JOIN cte_expense_group AS exp
    ON stu_info.partnercontact_id = exp.partnercontact_id
    AND stu_info.partner_system_name = exp.partner_system_name
    AND stu_info.eab_entry_year_academic = exp.eab_entry_year_academic
    AND stu_info.eab_entry_term = exp.eab_entry_term
    AND stu_info.eab_student_type = exp.eab_student_type
    AND exp.latest_record = 1

UNION ALL

SELECT 
    CAST(stu_info.primary_student_id AS VARCHAR) AS primary_student_id,
    CAST(stu_info.entry_year_academic AS VARCHAR) AS entry_year_academic,
    CAST(stu_info.eab_entry_year_academic AS INTEGER) AS eab_entry_year_academic,
    CAST(stu_info.entry_term AS VARCHAR) AS entry_term,
    CAST(stu_info.eab_entry_term AS VARCHAR) AS eab_entry_term,
    CAST(stu_info.student_type AS VARCHAR) AS student_type,
    CAST(stu_info.eab_student_type AS VARCHAR) AS eab_student_type,
    CAST(funds.eab_attribute_group AS VARCHAR) AS eab_attribute_group,
    CAST(funds.src_attribute_type AS VARCHAR) AS src_attribute_type,
    CAST(funds.eab_attribute_type AS VARCHAR) AS eab_attribute_type,
    CAST(funds.source_value AS VARCHAR) AS source_value,
    CAST(funds.eab_std_value AS VARCHAR) AS eab_std_value,
    CAST(funds.attribute_dt AS TIMESTAMP) AS attribute_dt
FROM cte_common_stu_info AS stu_info
INNER JOIN cte_funds AS funds
    ON stu_info.partnercontact_id = funds.partnercontact_id
    AND stu_info.partner_system_name = funds.partner_system_name
    AND stu_info.eab_entry_year_academic = funds.eab_entry_year_academic
    AND funds.latest_record = 1

UNION ALL

SELECT 
    CAST(stu_info.primary_student_id AS VARCHAR) AS primary_student_id,
    CAST(stu_info.entry_year_academic AS VARCHAR) AS entry_year_academic,
    CAST(stu_info.eab_entry_year_academic AS INTEGER) AS eab_entry_year_academic,
    CAST(stu_info.entry_term AS VARCHAR) AS entry_term,
    CAST(stu_info.eab_entry_term AS VARCHAR) AS eab_entry_term,
    CAST(stu_info.student_type AS VARCHAR) AS student_type,
    CAST(stu_info.eab_student_type AS VARCHAR) AS eab_student_type,
    CAST(finaid.eab_attribute_group AS VARCHAR) AS eab_attribute_group,
    CAST(finaid.src_attribute_type AS VARCHAR) AS src_attribute_type,
    CAST(finaid.eab_attribute_type AS VARCHAR) AS eab_attribute_type,
    CAST(finaid.source_value AS VARCHAR) AS source_value,
    CAST(finaid.eab_std_value AS VARCHAR) AS eab_std_value,
    CAST(finaid.attribute_dt AS TIMESTAMP) AS attribute_dt
FROM cte_common_stu_info AS stu_info
INNER JOIN cte_ability_to_pay AS finaid
    ON stu_info.partnercontact_id = finaid.partnercontact_id
    AND stu_info.partner_system_name = finaid.partner_system_name
    AND stu_info.eab_entry_year_academic = finaid.eab_entry_year_academic
    AND finaid.latest_record = 1

UNION ALL

SELECT 
    CAST(stu_info.primary_student_id AS VARCHAR) AS primary_student_id,
    CAST(stu_info.entry_year_academic AS VARCHAR) AS entry_year_academic,
    CAST(stu_info.eab_entry_year_academic AS INTEGER) AS eab_entry_year_academic,
    CAST(stu_info.entry_term AS VARCHAR) AS entry_term,
    CAST(stu_info.eab_entry_term AS VARCHAR) AS eab_entry_term,
    CAST(stu_info.student_type AS VARCHAR) AS student_type,
    CAST(stu_info.eab_student_type AS VARCHAR) AS eab_student_type,
    CAST(hs_gpa.eab_attribute_group AS VARCHAR) AS eab_attribute_group,
    CAST(hs_gpa.src_attribute_type AS VARCHAR) AS src_attribute_type,
    CAST(hs_gpa.eab_attribute_type AS VARCHAR) AS eab_attribute_type,
    CAST(hs_gpa.source_value AS VARCHAR) AS source_value,
    CAST(hs_gpa.eab_std_value AS VARCHAR) AS eab_std_value,
    CAST(hs_gpa.attribute_dt AS TIMESTAMP) AS attribute_dt
FROM cte_common_stu_info AS stu_info
INNER JOIN cte_hs_gpa AS hs_gpa
    ON stu_info.partnercontact_id = hs_gpa.partnercontact_id
    AND stu_info.partner_system_name = hs_gpa.partner_system_name
    AND hs_gpa.latest_record = 1

UNION ALL

SELECT 
    CAST(stu_info.primary_student_id AS VARCHAR) AS primary_student_id,
    CAST(stu_info.entry_year_academic AS VARCHAR) AS entry_year_academic,
    CAST(stu_info.eab_entry_year_academic AS INTEGER) AS eab_entry_year_academic,
    CAST(stu_info.entry_term AS VARCHAR) AS entry_term,
    CAST(stu_info.eab_entry_term AS VARCHAR) AS eab_entry_term,
    CAST(stu_info.student_type AS VARCHAR) AS student_type,
    CAST(stu_info.eab_student_type AS VARCHAR) AS eab_student_type,
    CAST(test_scr.eab_attribute_group AS VARCHAR) AS eab_attribute_group,
    CAST(test_scr.src_attribute_type AS VARCHAR) AS src_attribute_type,
    CAST(test_scr.eab_attribute_type AS VARCHAR) AS eab_attribute_type,
    CAST(test_scr.source_value AS VARCHAR) AS source_value,
    CAST(test_scr.eab_std_value AS VARCHAR) AS eab_std_value,
    CAST(test_scr.attribute_dt AS TIMESTAMP) AS attribute_dt
FROM cte_common_stu_info AS stu_info
INNER JOIN cte_test_scores AS test_scr
    ON stu_info.partnercontact_id = test_scr.partnercontact_id
    AND stu_info.partner_system_name = test_scr.partner_system_name
    AND test_scr.latest_record = 1

UNION ALL

SELECT 
    CAST(stu_info.primary_student_id AS VARCHAR) AS primary_student_id,
    CAST(stu_info.entry_year_academic AS VARCHAR) AS entry_year_academic,
    CAST(stu_info.eab_entry_year_academic AS INTEGER) AS eab_entry_year_academic,
    CAST(stu_info.entry_term AS VARCHAR) AS entry_term,
    CAST(stu_info.eab_entry_term AS VARCHAR) AS eab_entry_term,
    CAST(stu_info.student_type AS VARCHAR) AS student_type,
    CAST(stu_info.eab_student_type AS VARCHAR) AS eab_student_type,
    CAST(rank.eab_attribute_group AS VARCHAR) AS eab_attribute_group,
    CAST(rank.src_attribute_type AS VARCHAR) AS src_attribute_type,
    CAST(rank.eab_attribute_type AS VARCHAR) AS eab_attribute_type,
    CAST(rank.source_value AS VARCHAR) AS source_value,
    CAST(rank.eab_std_value AS VARCHAR) AS eab_std_value,
    CAST(rank.attribute_dt AS TIMESTAMP) AS attribute_dt
FROM cte_common_stu_info AS stu_info
INNER JOIN cte_class_rank AS rank
    ON stu_info.partnercontact_id = rank.partnercontact_id
    AND stu_info.partner_system_name = rank.partner_system_name
    AND rank.latest_record = 1

UNION ALL

SELECT 
    CAST(stu_info.primary_student_id AS VARCHAR) AS primary_student_id,
    CAST(stu_info.entry_year_academic AS VARCHAR) AS entry_year_academic,
    CAST(stu_info.eab_entry_year_academic AS INTEGER) AS eab_entry_year_academic,
    CAST(stu_info.entry_term AS VARCHAR) AS entry_term,
    CAST(stu_info.eab_entry_term AS VARCHAR) AS eab_entry_term,
    CAST(stu_info.student_type AS VARCHAR) AS student_type,
    CAST(stu_info.eab_student_type AS VARCHAR) AS eab_student_type,
    CAST(academic.eab_attribute_group AS VARCHAR) AS eab_attribute_group,
    CAST(academic.src_attribute_type AS VARCHAR) AS src_attribute_type,
    CAST(academic.eab_attribute_type AS VARCHAR) AS eab_attribute_type,
    CAST(academic.source_value AS VARCHAR) AS source_value,
    CAST(academic.eab_std_value AS VARCHAR) AS eab_std_value,
    CAST(academic.attribute_dt AS TIMESTAMP) AS attribute_dt
FROM cte_common_stu_info AS stu_info
INNER JOIN cte_alt_academic AS academic
    ON stu_info.partnercontact_id = academic.partnercontact_id
    AND stu_info.partner_system_name = academic.partner_system_name
    AND stu_info.eab_entry_year_academic = academic.eab_entry_year_academic
    AND stu_info.eab_entry_term = academic.eab_entry_term
    AND stu_info.eab_student_type = academic.eab_student_type
    AND academic.latest_record = 1

UNION ALL

SELECT 
    CAST(stu_info.primary_student_id AS VARCHAR) AS primary_student_id,
    CAST(stu_info.entry_year_academic AS VARCHAR) AS entry_year_academic,
    CAST(stu_info.eab_entry_year_academic AS INTEGER) AS eab_entry_year_academic,
    CAST(stu_info.entry_term AS VARCHAR) AS entry_term,
    CAST(stu_info.eab_entry_term AS VARCHAR) AS eab_entry_term,
    CAST(stu_info.student_type AS VARCHAR) AS student_type,
    CAST(stu_info.eab_student_type AS VARCHAR) AS eab_student_type,
    CAST(aid_app.eab_attribute_group AS VARCHAR) AS eab_attribute_group,
    CAST(aid_app.src_attribute_type AS VARCHAR) AS src_attribute_type,
    CAST(aid_app.eab_attribute_type AS VARCHAR) AS eab_attribute_type,
    CAST(aid_app.source_value AS VARCHAR) AS source_value,
    CAST(aid_app.eab_std_value AS VARCHAR) AS eab_std_value,
    CAST(aid_app.attribute_dt AS TIMESTAMP) AS attribute_dt
FROM cte_common_stu_info AS stu_info
INNER JOIN cte_aid_app AS aid_app
    ON stu_info.partnercontact_id = aid_app.partnercontact_id
    AND stu_info.partner_system_name = aid_app.partner_system_name
    AND stu_info.eab_entry_year_academic = aid_app.eab_entry_year_academic
    AND aid_app.latest_record = 1

UNION ALL

SELECT 
    CAST(stu_info.primary_student_id AS VARCHAR) AS primary_student_id,
    CAST(stu_info.entry_year_academic AS VARCHAR) AS entry_year_academic,
    CAST(stu_info.eab_entry_year_academic AS INTEGER) AS eab_entry_year_academic,
    CAST(stu_info.entry_term AS VARCHAR) AS entry_term,
    CAST(stu_info.eab_entry_term AS VARCHAR) AS eab_entry_term,
    CAST(stu_info.student_type AS VARCHAR) AS student_type,
    CAST(stu_info.eab_student_type AS VARCHAR) AS eab_student_type,
    CAST(race.eab_attribute_group AS VARCHAR) AS eab_attribute_group,
    CAST(race.src_attribute_type AS VARCHAR) AS src_attribute_type,
    CAST(race.eab_attribute_type AS VARCHAR) AS eab_attribute_type,
    CAST(race.source_value AS VARCHAR) AS source_value,
    CAST(race.eab_std_value AS VARCHAR) AS eab_std_value,
    CAST(race.attribute_dt AS TIMESTAMP) AS attribute_dt
FROM cte_common_stu_info AS stu_info
INNER JOIN cte_race AS race
    ON stu_info.partnercontact_id = race.partnercontact_id
    AND stu_info.partner_system_name = race.partner_system_name
    AND race.latest_record = 1

UNION ALL

SELECT 
    CAST(stu_info.primary_student_id AS VARCHAR) AS primary_student_id,
    CAST(stu_info.entry_year_academic AS VARCHAR) AS entry_year_academic,
    CAST(stu_info.eab_entry_year_academic AS INTEGER) AS eab_entry_year_academic,
    CAST(stu_info.entry_term AS VARCHAR) AS entry_term,
    CAST(stu_info.eab_entry_term AS VARCHAR) AS eab_entry_term,
    CAST(stu_info.student_type AS VARCHAR) AS student_type,
    CAST(stu_info.eab_student_type AS VARCHAR) AS eab_student_type,
    CAST(ai.eab_attribute_group AS VARCHAR) AS eab_attribute_group,
    CAST(ai.src_attribute_type AS VARCHAR) AS src_attribute_type,
    CAST(ai.eab_attribute_type AS VARCHAR) AS eab_attribute_type,
    CAST(ai.source_value AS VARCHAR) AS source_value,
    CAST(ai.eab_std_value AS VARCHAR) AS eab_std_value,
    CAST(ai.attribute_dt AS TIMESTAMP) AS attribute_dt
FROM cte_common_stu_info AS stu_info
INNER JOIN cte_academic_interest AS ai
    ON stu_info.partnercontact_id = ai.partnercontact_id
    AND stu_info.partner_system_name = ai.partner_system_name
    AND stu_info.eab_entry_year_academic = ai.eab_entry_year_academic
    AND stu_info.eab_entry_term = ai.eab_entry_term
    AND stu_info.eab_student_type = ai.eab_student_type
    AND ai.latest_record = 1

UNION ALL

SELECT 
    CAST(stu_info.primary_student_id AS VARCHAR) AS primary_student_id,
    CAST(stu_info.entry_year_academic AS VARCHAR) AS entry_year_academic,
    CAST(stu_info.eab_entry_year_academic AS INTEGER) AS eab_entry_year_academic,
    CAST(stu_info.entry_term AS VARCHAR) AS entry_term,
    CAST(stu_info.eab_entry_term AS VARCHAR) AS eab_entry_term,
    CAST(stu_info.student_type AS VARCHAR) AS student_type,
    CAST(stu_info.eab_student_type AS VARCHAR) AS eab_student_type,
    CAST(camp_vst.eab_attribute_group AS VARCHAR) AS eab_attribute_group,
    CAST(camp_vst.src_attribute_type AS VARCHAR) AS src_attribute_type,
    CAST(camp_vst.eab_attribute_type AS VARCHAR) AS eab_attribute_type,
    CAST(camp_vst.source_value AS VARCHAR) AS source_value,
    CAST(camp_vst.eab_std_value AS VARCHAR) AS eab_std_value,
    CAST(camp_vst.attribute_dt AS TIMESTAMP) AS attribute_dt
FROM cte_common_stu_info AS stu_info
INNER JOIN cte_campus_visit AS camp_vst
    ON stu_info.partnercontact_id = camp_vst.partnercontact_id
    AND stu_info.partner_system_name = camp_vst.partner_system_name
    AND camp_vst.latest_record = 1