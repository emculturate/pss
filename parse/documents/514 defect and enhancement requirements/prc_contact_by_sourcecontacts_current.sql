SELECT
common_format.SOURCECONTACT_ID AS sourcecontact_id,
contact_def.CONTACT_KEY AS contact_key,
common_format.EMAIL_ADDRESS AS email_address,
intake_type.INTAKE_TYPE_KEY AS intake_type_key,
source_type.source_type_key AS source_type_key,
common_format.INTAKE_DT AS first_sourced_dt,
1 AS contact_priority
FROM
     {{ ref ( 'prc_dmp_contact_common_format') }} common_format
INNER JOIN
     {{ ref ( 'prc_contact_definitions') }} contact_def
on UPPER(common_format.EMAIL_ADDRESS) = UPPER(contact_def.EMAIL_ADDRESS)
INNER JOIN
     {{ source('COMMON', 'ref__intake_types_alr') }} intake_type
on UPPER(common_format.INTAKE_TYPE) = UPPER(intake_type.INTAKE_TYPE_LABEL)
LEFT JOIN
     {{ source('COMMON', 'ref__source_types_alr') }} source_type
on UPPER(common_format.LAST_SOURCE_TYPE_LABEL) = UPPER(source_type.SOURCE_TYPE_LABEL)