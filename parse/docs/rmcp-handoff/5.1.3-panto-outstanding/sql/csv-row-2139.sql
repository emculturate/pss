WITH
latest_applications AS (
    SELECT
        app_ranked.id AS id,
        app_ranked.prospect_id AS prospect_id,
        app_ranked.updated_at AS updated_at,
        app_ranked.created_at AS created_at,
        app_ranked.sis_student_id AS sis_student_id,
        app_ranked.first_name AS first_name,
        app_ranked.middle_name AS middle_name,
        app_ranked.last_name AS last_name,
        app_ranked.us_citizenship_status AS us_citizenship_status,
        app_ranked.international_indicator AS international_indicator,
        app_ranked.date_of_birth AS date_of_birth,
        app_ranked.gender AS gender,
        app_ranked.gender_identity AS gender_identity,
        app_ranked.religious_affiliation AS religious_affiliation,
        app_ranked.high_school_graduation_year AS high_school_graduation_year,
        app_ranked.first_generation_college AS first_generation_college,
        app_ranked.military_status AS military_status,
        app_ranked.alumni_relation_indicator AS alumni_relation_indicator,
        app_ranked.ethnicity AS ethnicity,
        app_ranked.campus_visit_count AS campus_visit_count,
        app_ranked.campus_visit_indicator AS campus_visit_indicator,
        app_ranked.first_campus_visit_date AS first_campus_visit_date,
        app_ranked.last_campus_visit_date AS last_campus_visit_date,
        app_ranked.ok_to_email AS ok_to_email,
        app_ranked.ok_to_call AS ok_to_call,
        app_ranked.ok_to_text AS ok_to_text
    FROM (
        SELECT
            app_src.id AS id,
            app_src.prospect_id AS prospect_id,
            app_src.updated_at AS updated_at,
            app_src.created_at AS created_at,
            app_src.sis_student_id AS sis_student_id,
            app_src.first_name AS first_name,
            app_src.middle_name AS middle_name,
            app_src.last_name AS last_name,
            app_src.us_citizenship_status AS us_citizenship_status,
            app_src.international_indicator AS international_indicator,
            app_src.date_of_birth AS date_of_birth,
            app_src.gender AS gender,
            app_src.gender_identity AS gender_identity,
            app_src.religious_affiliation AS religious_affiliation,
            app_src.high_school_graduation_year AS high_school_graduation_year,
            app_src.first_generation_college AS first_generation_college,
            app_src.military_status AS military_status,
            app_src.alumni_relation_indicator AS alumni_relation_indicator,
            app_src.ethnicity AS ethnicity,
            app_src.campus_visit_count AS campus_visit_count,
            app_src.campus_visit_indicator AS campus_visit_indicator,
            app_src.first_campus_visit_date AS first_campus_visit_date,
            app_src.last_campus_visit_date AS last_campus_visit_date,
            app_src.ok_to_email AS ok_to_email,
            app_src.ok_to_call AS ok_to_call,
            app_src.ok_to_text AS ok_to_text,
            ROW_NUMBER() OVER (
                PARTITION BY app_src.prospect_id
                ORDER BY TRY_TO_TIMESTAMP_NTZ(app_src.updated_at) DESC NULLS LAST,
                TRY_TO_TIMESTAMP_NTZ(app_src.created_at) DESC NULLS LAST,
                TO_VARCHAR(app_src.id) DESC
            ) AS application_row_num
        FROM <[ECRM].[Applications].{fulfillment}> AS app_src
    ) AS app_ranked
    WHERE app_ranked.application_row_num = 1
),
activity_prospect_map AS (
    SELECT
        COALESCE(
            IFF(act_src.activitable_type ILIKE '%prospect%', NULLIF(TRIM(TO_VARCHAR(act_src.activitable_id)), ''), NULL),
            NULLIF(TRIM(TO_VARCHAR(app_for_act.prospect_id)), '')
        ) AS prospect_id,
        act_src.activity_type AS activity_type,
        act_src.description AS description,
        act_src.activity_date AS activity_date
    FROM <[ECRM].[Activities].{fulfillment}> AS act_src
    LEFT JOIN <[ECRM].[Applications].{fulfillment}> AS app_for_act
        ON NULLIF(TRIM(TO_VARCHAR(app_for_act.id)), '') = NULLIF(TRIM(TO_VARCHAR(act_src.activitable_id)), '')
       AND act_src.activitable_type ILIKE '%application%'
    WHERE COALESCE(
        IFF(act_src.activitable_type ILIKE '%prospect%', NULLIF(TRIM(TO_VARCHAR(act_src.activitable_id)), ''), NULL),
        NULLIF(TRIM(TO_VARCHAR(app_for_act.prospect_id)), '')
    ) IS NOT NULL
),
campus_visit_activity AS (
    SELECT
        apm.prospect_id AS prospect_id,
        COUNT(*) AS campus_visit_count_from_activities,
        MIN(TRY_TO_DATE(apm.activity_date)) AS first_campus_visit_date_from_activities,
        MAX(TRY_TO_DATE(apm.activity_date)) AS last_campus_visit_date_from_activities
    FROM activity_prospect_map AS apm
    WHERE apm.activity_type ILIKE '%campus%'
      AND (apm.activity_type ILIKE '%visit%' OR apm.description ILIKE '%visit%')
    GROUP BY apm.prospect_id
)
SELECT
    null as ALUMNI_RELATIONSHIP_EAB,
    COALESCE(NULLIF(TRIM(TO_VARCHAR(la.alumni_relation_indicator)), ''), NULLIF(TRIM(TO_VARCHAR(p.legacy_status)), '')) as ALUMNI_RELATIONSHIP_INDICATOR,
    TRY_TO_DATE(COALESCE(NULLIF(TRIM(TO_VARCHAR(la.date_of_birth)), ''), NULLIF(TRIM(TO_VARCHAR(p.date_of_birth)), ''))) as BIRTH_DATE,
    NULLIF(TRIM(TO_VARCHAR(p.id)), '') as CRM_STUDENT_ID,
    null as CALCULATED_FIELD_1,
    null as CALCULATED_FIELD_2,
    null as CALCULATED_FIELD_3,
    null as CALCULATED_FIELD_4,
    null as CALCULATED_FIELD_5,
    null as CALCULATED_FIELD_6,
    null as CALCULATED_FIELD_7,
    null as CALCULATED_FIELD_8,
    null as CALCULATED_ID_FIELD,
    cva.campus_visit_count_from_activities as CAMPUS_VISIT_COUNT,
    case when cva.campus_visit_count_from_activities > 0 then 'true' else 'false' end as CAMPUS_VISIT_INDICATOR,
    null as CAMPUS_VISIT_INDICATOR_EAB,
    null as EAB_STUDENT_ID,
    CASE
		WHEN NULLIF(TRIM(TO_VARCHAR(p.status)), '') = 'Current Student' THEN '1'
        WHEN COALESCE(NULLIF(TRIM(TO_VARCHAR(la.ok_to_email)), ''), NULLIF(TRIM(TO_VARCHAR(p.ok_to_email)), '')) IS NULL THEN NULL
        WHEN CAST(COALESCE(NULLIF(TRIM(TO_VARCHAR(la.ok_to_email)), ''), NULLIF(TRIM(TO_VARCHAR(p.ok_to_email)), '')) AS VARCHAR) = '0' then '1'
        WHEN CAST(COALESCE(NULLIF(TRIM(TO_VARCHAR(la.ok_to_email)), ''), NULLIF(TRIM(TO_VARCHAR(p.ok_to_email)), '')) AS VARCHAR) = '1' then '0'
        ELSE NULL
    END as EMAIL_OPT_OUT_INDICATOR,
    null as EMAIL_OPT_OUT_INDICATOR_EAB,
    COALESCE(NULLIF(TRIM(TO_VARCHAR(la.ethnicity)), ''), NULLIF(TRIM(TO_VARCHAR(p.ethnicity)), '')) as ETHNICITY,
    null as ETHNICITY_EAB,
    cva.first_campus_visit_date_from_activities as FIRST_CAMPUS_VISIT_DATE,
    COALESCE(NULLIF(TRIM(TO_VARCHAR(la.first_generation_college)), ''), NULLIF(TRIM(TO_VARCHAR(p.first_generation_college)), '')) as FIRST_GENERATION_INDICATOR,
    null as FIRST_GENERATION_INDICATOR_EAB,
    COALESCE(NULLIF(TRIM(TO_VARCHAR(la.first_name)), ''), NULLIF(TRIM(TO_VARCHAR(p.first_name)), '')) as FIRST_NAME,
    COALESCE(NULLIF(TRIM(TO_VARCHAR(la.gender)), ''), NULLIF(TRIM(TO_VARCHAR(p.gender)), '')) as GENDER,
    COALESCE(NULLIF(TRIM(TO_VARCHAR(la.high_school_graduation_year)), ''), NULLIF(TRIM(TO_VARCHAR(p.high_school_graduation_year)), '')) as HIGH_SCHOOL_GRAD_YEAR,
    CASE
        WHEN COALESCE(NULLIF(TRIM(TO_VARCHAR(la.ethnicity)), ''), NULLIF(TRIM(TO_VARCHAR(p.ethnicity)), '')) IS NULL THEN NULL
        WHEN COALESCE(NULLIF(TRIM(TO_VARCHAR(la.ethnicity)), ''), NULLIF(TRIM(TO_VARCHAR(p.ethnicity)), '')) ILIKE '%hispanic%'
          OR COALESCE(NULLIF(TRIM(TO_VARCHAR(la.ethnicity)), ''), NULLIF(TRIM(TO_VARCHAR(p.ethnicity)), '')) ILIKE '%latino%'
          OR COALESCE(NULLIF(TRIM(TO_VARCHAR(la.ethnicity)), ''), NULLIF(TRIM(TO_VARCHAR(p.ethnicity)), '')) ILIKE '%latina%'
          OR COALESCE(NULLIF(TRIM(TO_VARCHAR(la.ethnicity)), ''), NULLIF(TRIM(TO_VARCHAR(p.ethnicity)), '')) ILIKE '%latinx%'
        THEN 'TRUE'
        ELSE 'FALSE'
    END as HISPANIC_INDICATOR,
    p.intake_date as INTAKE_DATE,
    COALESCE(NULLIF(TRIM(TO_VARCHAR(la.international_indicator)), ''), NULLIF(TRIM(TO_VARCHAR(p.international_ind)), '')) as INTERNATIONAL_INDICATOR,
    null as INTERNATIONAL_INDICATOR_EAB,
    COALESCE(NULLIF(TRIM(TO_VARCHAR(la.last_name)), ''), NULLIF(TRIM(TO_VARCHAR(p.last_name)), '')) as LAST_NAME,
    CASE
		WHEN NULLIF(TRIM(TO_VARCHAR(p.status)), '') = 'Current Student' THEN '1'
        WHEN NULLIF(TRIM(TO_VARCHAR(p.mail_optout_ind)), '') IS NULL THEN NULL
        WHEN CAST(NULLIF(TRIM(TO_VARCHAR(p.mail_optout_ind)), '') AS VARCHAR) = '0' then '1'
        WHEN CAST(NULLIF(TRIM(TO_VARCHAR(p.mail_optout_ind)), '') AS VARCHAR) = '1' then '0'
        ELSE NULL
    END as MAIL_OPT_OUT_INDICATOR,
    null as MAIL_OPT_OUT_INDICATOR_EAB,
    COALESCE(NULLIF(TRIM(TO_VARCHAR(la.middle_name)), ''), NULLIF(TRIM(TO_VARCHAR(p.middle_name)), '')) as MIDDLE_NAME,
    null as MILITARY_SERVICE_OR_VETERAN_INDICATOR_EAB,
    COALESCE(NULLIF(TRIM(TO_VARCHAR(la.military_status)), ''), NULLIF(TRIM(TO_VARCHAR(p.military_status)), '')) as MILITARY_SERVICE_OR_VETERAN_STATUS,
    cva.last_campus_visit_date_from_activities as MOST_RECENT_CAMPUS_VISIT_DATE,
    CASE
		WHEN NULLIF(TRIM(TO_VARCHAR(p.status)), '') = 'Current Student' THEN '1'
        WHEN COALESCE(NULLIF(TRIM(TO_VARCHAR(la.ok_to_email)), ''), NULLIF(TRIM(TO_VARCHAR(p.ok_to_email)), '')) IS NULL THEN NULL
        WHEN CAST(COALESCE(NULLIF(TRIM(TO_VARCHAR(la.ok_to_email)), ''), NULLIF(TRIM(TO_VARCHAR(p.ok_to_email)), '')) AS VARCHAR) = '0' then '1'
        WHEN CAST(COALESCE(NULLIF(TRIM(TO_VARCHAR(la.ok_to_email)), ''), NULLIF(TRIM(TO_VARCHAR(p.ok_to_email)), '')) AS VARCHAR) = '1' then '0'
        ELSE NULL
    END as OPT_OUT_INDICATOR_GENERAL,
    null as OPT_OUT_INDICATOR_GENERAL_EAB,
    NULLIF(TRIM(TO_VARCHAR(p.PRIMARY_SOURCE)), '') as ORIGIN_SOURCE_CODE,
    TRY_TO_DATE(NULLIF(TRIM(TO_VARCHAR(p.created_at)), '')) as ORIGIN_SOURCE_DATE,
    COALESCE(NULLIF(TRIM(TO_VARCHAR(p.external_id)), ''), NULLIF(TRIM(TO_VARCHAR(p.sis_integration_id)), '')) as OTHER_STUDENT_ID,
    'navigate_crm_1' as PARTNER_SYSTEM_NAME,
    CASE
		WHEN NULLIF(TRIM(TO_VARCHAR(p.status)), '') = 'Current Student' THEN '1'
        WHEN COALESCE(NULLIF(TRIM(TO_VARCHAR(la.ok_to_call)), ''), NULLIF(TRIM(TO_VARCHAR(p.ok_to_call)), '')) IS NULL THEN NULL
        WHEN CAST(COALESCE(NULLIF(TRIM(TO_VARCHAR(la.ok_to_call)), ''), NULLIF(TRIM(TO_VARCHAR(p.ok_to_call)), '')) AS VARCHAR) = '0' then '1'
        WHEN CAST(COALESCE(NULLIF(TRIM(TO_VARCHAR(la.ok_to_call)), ''), NULLIF(TRIM(TO_VARCHAR(p.ok_to_call)), '')) AS VARCHAR) = '1' then '0'
        ELSE NULL
    END as PHONE_OPT_OUT_INDICATOR,
    null as PHONE_OPT_OUT_INDICATOR_EAB,
    null as POPULATION_NAME,
    null as PREFERRED_FIRST_NAME,
    COALESCE(NULLIF(TRIM(TO_VARCHAR(la.gender_identity)), ''),NULLIF(TRIM(TO_VARCHAR(la.gender)), ''), NULLIF(TRIM(TO_VARCHAR(p.gender)), '')) as PREFERRED_GENDER,
    null as PREFERRED_GENDER_EAB,
    NULLIF(TRIM(TO_VARCHAR(p.id)), '') as PRIMARY_STUDENT_ID,
    COALESCE(NULLIF(TRIM(TO_VARCHAR(la.religious_affiliation)), ''), NULLIF(TRIM(TO_VARCHAR(p.religious_affiliation)), '')) as RELIGIOUS_AFFILIATION,
    COALESCE(NULLIF(TRIM(TO_VARCHAR(la.sis_student_id)), ''), NULLIF(TRIM(TO_VARCHAR(p.sis_student_id)), '')) as SIS_STUDENT_ID,
    COALESCE(NULLIF(TRIM(TO_VARCHAR(la.ok_to_text)), ''), NULLIF(TRIM(TO_VARCHAR(p.ok_to_text)), '')) as SMS_OPT_IN_INDICATOR,
    null as SMS_OPT_IN_INDICATOR_EAB,
    CASE
		WHEN NULLIF(TRIM(TO_VARCHAR(p.status)), '') = 'Current Student' THEN '1'
        WHEN COALESCE(NULLIF(TRIM(TO_VARCHAR(la.ok_to_text)), ''), NULLIF(TRIM(TO_VARCHAR(p.ok_to_text)), '')) IS NULL THEN NULL
        WHEN CAST(COALESCE(NULLIF(TRIM(TO_VARCHAR(la.ok_to_text)), ''), NULLIF(TRIM(TO_VARCHAR(p.ok_to_text)), '')) AS VARCHAR) = '0' then '1'
        WHEN CAST(COALESCE(NULLIF(TRIM(TO_VARCHAR(la.ok_to_text)), ''), NULLIF(TRIM(TO_VARCHAR(p.ok_to_text)), '')) AS VARCHAR) = '1' then '0'
        ELSE NULL
    END as SMS_OPT_OUT_INDICATOR,
    COALESCE(NULLIF(TRIM(TO_VARCHAR(la.us_citizenship_status)), ''), NULLIF(TRIM(TO_VARCHAR(p.us_citizenship_status)), '')) as US_CITIZENSHIP_STATUS
FROM <[ECRM].[Prospects].{fulfillment}> AS p
LEFT JOIN latest_applications AS la
    ON la.prospect_id = p.id
LEFT JOIN campus_visit_activity AS cva
    ON cva.prospect_id = NULLIF(TRIM(TO_VARCHAR(p.id)), '')
<student_join_extension>
where <student_where>