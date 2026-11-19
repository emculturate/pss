SELECT 
    stud.PRIMARY_STUDENT_ID as src_primary_student_id,
    'program' as src_attribute_type,
    ROW_NUMBER() OVER (ORDER BY 1) AS attribute_key,
	CASE
    WHEN stud.program IN ('Doctor of Nurse Anesthesia Practice (DNAP)', 'Master of Science in Biomedical Science') THEN 'hp_hsci'
	WHEN stud.program IN ('Master of Science in Data Science') THEN 'hp_datatech'
    WHEN stud.program IN ('Master of Social Work') THEN 'hp_sbs'
	WHEN stud.program IN ('Master of Science in Education') THEN 'hp_edu'
	WHEN stud.program IN ('Master of Business Administration','Doctor of Business Administration') THEN 'hp_bus'
	WHEN stud.program IN ('Master of Theology','Master of Theological Studies') THEN 'hp_rel'
    ELSE NULL end   as   dynamic_segment_name,
    '2026-01-15' as effective_dt,
    'dynamic_custom_segmentation' as eab_attribute_type,
	null as effective_end_date,
	s.INTAKE_DT as intake_dt,
	null as eab_std_value,
	null as eab_attribute_group
FROM <[ALR].[Student Program].{fulfillment}> stud
LEFT JOIN <[ALR].[Student].{fulfillment}> s
    ON stud.PRIMARY_STUDENT_ID = s.PRIMARY_STUDENT_ID
WHERE
    CASE
    WHEN stud.program IN ('Doctor of Nurse Anesthesia Practice (DNAP)', 'Master of Science in Biomedical Science') THEN 'hp_hsci'
	WHEN stud.program IN ('Master of Science in Data Science') THEN ' hp_datatech'
    WHEN stud.program IN ('Master of Social Work') THEN 'hp_sbs'
	WHEN stud.program IN ('Master of Science in Education') THEN 'hp_edu'
	WHEN stud.program IN ('Master of Business Administration','Doctor of Business Administration') THEN 'hp_bus'
	WHEN stud.program IN ('Master of Theology','Master of Theological Studies') THEN 'hp_rel'
        ELSE NULL
    END IS NOT NULL
	AND stud.STUDENT_TYPE IS NOT NULL;