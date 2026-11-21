# Interface Loss Audit

Date: 2026-11-20

Scope: successful 5.0.0-3 versus 5.1.4 parses for snippet row 35573 and
bound-query rows 1535 and 1539. This was a black-box audit; no parser source was
consulted.

## Conclusions

None of the three apparent interface losses is a 5.1.4 regression.

- Row 35573 is a `JOIN_EXTENSION` endpoint contract/selection difference. Every
  joined derived query retains its own symbol-table interface in 5.1.4. The
  top-level snippet interface consistently follows the last derived query,
  whereas 5.0.0-3 selects an earlier derived query after the third join.
- Rows 1535 and 1539 are 5.1.4 interface corrections. Their outer `SELECT`
  clauses contain six outputs, and 5.1.4 reports exactly those six. 5.0.0-3
  omits the two predicand-substitution outputs; row 1535 also incorrectly leaks
  the inner `donor_email_rank` into the outer interface.

## Row 35573 — JOIN_EXTENSION

Reference:
`Enroll360/Enroll360.Partner Code Mapping.All:v01/join_extension`

The snippet appends four derived-table joins with these interfaces:

1. Student: `stud_primary_student_id`, `first_name`, `last_name`
2. Student Email: `se_primary_student_id`, `email_address`
3. Student Term: `st_primary_student_id`, `student_type`,
   `current_entry_term`, `intake_date`
4. ProgramTerm: `calculated_field_10`, `calculated_field_11`

Progressively appending joins produced:

- One join: both parsers select the Student interface.
- Two joins: both parsers select the Student Email interface.
- Three joins: 5.0.0-3 remains on Student Email; 5.1.4 selects Student Term.
- Four joins: 5.0.0-3 remains on Student Email; 5.1.4 selects ProgramTerm.

Swapping the first two joins changed the 5.0.0-3 top interface to Student but
left the 5.1.4 top interface on ProgramTerm. Qualifying every inner projection
did not change either result.

All four `def_queryN.interface` buckets are complete in 5.1.4. Wrapping the
fragment in a full SQL query and selecting both Student Email and ProgramTerm
columns produced the same four-column interface in both parsers:

```sql
select se.email_address,
       se.se_primary_student_id,
       pt.calculated_field_10,
       pt.calculated_field_11
from <[Enroll360].[Student Relationship].{fulfillment}> stud_rship
-- original four JOIN clauses
```

Classification: **expected 5.1.4 change / improvement**, not an interface loss.
The 5.0.0-to-5.1.3 upgrade guide already warns that `TUPLE` and
`JOIN_EXTENSION` snippet interfaces can differ and must not be assumed to have
the 5.0.0-3 column count.

## Row 1535 — donor email

Reference: `AMS/donor_email/par_intake_donor_email_AMS_V2.0`

The outer `SELECT` exports:

```sql
src_donor_id,
email,
email_type,
intake_dt,
<source_partner_system_name_donor_email> as source_partner_system_name,
<source_eab_system_type> as source_eab_system_type
```

5.1.4 reports all six names. Its outer symbol-table interface grounds the first
four in the derived UNION and the last two in their predicand substitutions.
`donor_email_rank` remains correctly present in the inner UNION interface and
in the outer filter, but not in the outer output interface.

5.0.0-3 reports the first four names plus `donor_email_rank`, and omits both
`source_*` outputs.

Classification: **5.1.4 improvement**. The strict `INTERFACE_COLUMN_LOST` tag
for `donor_email_rank` describes a baseline-only false output, not a candidate
loss.

## Row 1539 — SUNY Buffalo donor email

Reference: `AMS/donor_email/SUNY_Buffalo_par_intake_donor_email_AMS_V2.0`

The outer `SELECT` has the same six logical outputs as row 1535. 5.1.4 reports
and grounds all six. 5.0.0-3 reports only the four columns supplied by the
derived UNION and omits both predicand-substitution aliases.

Classification: **5.1.4 improvement**.

## Follow-up for RMCP comparison policy

These rows should not be parser-agent defect handoffs. RMCP's strict assessment
should distinguish:

- `JOIN_EXTENSION` top-interface selection changes when every nested
  `def_queryN.interface` remains intact; and
- outer interface corrections where candidate aliases exactly match the outer
  `SELECT`, especially aliases produced by predicand substitutions.

Until that refinement exists, rows 35573, 1535, and 1539 should be treated as
adjudicated false positives in interface-loss review.

## Complete reproduction inputs

These inputs are reproduced in full so the parser agent can run them without
locating the original corpus or reconstructing omitted clauses.

### Row 35573 original input

Endpoint: `JOIN_EXTENSION`

```sql
left outer join
(select primary_student_id as stud_primary_student_id,
first_name, last_name
from <[Enroll360].[Student].{fulfillment}> sc ) sc
on stud_rship.relationship_id = sc.stud_primary_student_id

left outer join (
select primary_student_id as se_primary_student_id,
email_address
from <[Enroll360].[Student Email].{fulfillment}> se) se
on stud_rship.relationship_id = se.se_primary_student_id

left outer join
(SELECT primary_student_id AS st_primary_student_id,
student_type,
current_entry_term,
intake_date
  from <[Enroll360].[Student Term].{fulfillment}> st
) st
on stud_rship.primary_student_id = st.st_primary_student_id
and st.student_type = 'High school student or traditional transfer'

left outer join (
  select calculated_field_10, calculated_field_11
  from <[Enroll360].[Student Relationship].{ProgramTerm}> pt
) pt
	on st.current_entry_term = pt.calculated_field_10
  	and pt.calculated_field_10 != 'Unknown'
  	and substring(pt.calculated_field_11,1,4) >= substring(sysdate(),1,4)
```

### Row 35573 full SQL wrapper

Endpoint: `SQL`

```sql
select se.email_address,
       se.se_primary_student_id,
       pt.calculated_field_10,
       pt.calculated_field_11
from <[Enroll360].[Student Relationship].{fulfillment}> stud_rship
left outer join
(select primary_student_id as stud_primary_student_id,
first_name, last_name
from <[Enroll360].[Student].{fulfillment}> sc ) sc
on stud_rship.relationship_id = sc.stud_primary_student_id

left outer join (
select primary_student_id as se_primary_student_id,
email_address
from <[Enroll360].[Student Email].{fulfillment}> se) se
on stud_rship.relationship_id = se.se_primary_student_id

left outer join
(SELECT primary_student_id AS st_primary_student_id,
student_type,
current_entry_term,
intake_date
  from <[Enroll360].[Student Term].{fulfillment}> st
) st
on stud_rship.primary_student_id = st.st_primary_student_id
and st.student_type = 'High school student or traditional transfer'

left outer join (
  select calculated_field_10, calculated_field_11
  from <[Enroll360].[Student Relationship].{ProgramTerm}> pt
) pt
	on st.current_entry_term = pt.calculated_field_10
  	and pt.calculated_field_10 != 'Unknown'
  	and substring(pt.calculated_field_11,1,4) >= substring(sysdate(),1,4)
```

### Row 1535 original query

Endpoint: `SQL`

```sql
-- #terukula 09/15 AMS-51 V0.1 created query to populate donor_email in AMS
-- #spatole 12/12 AMS-322 - Change references for source entities in the donor bound queries
-- #terukula 12/16 renamed secondary_email column to calculated field_1 and added case statement for email_type column
--#gsudhakar 10/6/23 added intake_dt field
--#gsudhakar 11/08/23 add logic to include null emails where email_type=primary
--#gsudhakar 03/19/25 added <join_extension_donor_email> variable
SELECT
src_donor_id,
email,
email_type,
intake_dt,
<source_partner_system_name_donor_email> as source_partner_system_name,
<source_eab_system_type> as source_eab_system_type
FROM
((SELECT
  CAST(donor_email.<src donor id> AS VARCHAR) AS src_donor_id,
  CAST(donor_email.<email> AS VARCHAR) AS email,
  CASE WHEN CAST(donor_email.<email type> AS VARCHAR) IS NULL THEN CAST('primary' AS VARCHAR)
       ELSE CAST(donor_email.<email type> AS VARCHAR)
  END AS email_type,
  donor_email.intake_date as intake_dt, ROW_NUMBER() OVER (partition by donor_email.email ORDER BY d.hh_primary DESC, donor_email.src_donor_id) as donor_email_rank
FROM <[AMS].[donor_email].{fulfillment}> AS donor_email
join <[AMS].[donor].{final}> d
on donor_email.src_donor_id = d.src_donor_id
WHERE CAST(donor_email.<src donor id> AS VARCHAR) IS NOT NULL
--AND CAST(donor_email.<email> AS VARCHAR) IS NOT NULL
)
UNION
(SELECT
  CAST(donor_email.<src donor id> AS VARCHAR) AS src_donor_id,
  CAST(donor_email.<Calculated Field 1> AS VARCHAR) AS email,
  CAST('secondary' AS VARCHAR) AS email_type,
  donor_email.intake_date as intake_dt, null as donor_email_rank
FROM <[AMS].[donor_email].{fulfillment}> AS donor_email
WHERE CAST(donor_email.<src donor id> AS VARCHAR) IS NOT NULL
AND CAST(donor_email.<Calculated Field 1> AS VARCHAR) IS NOT NULL
AND CAST(donor_email.<Calculated Field 1> AS VARCHAR) <>  CAST(NVL(donor_email.<email>,'No Email') AS VARCHAR)
)) AS donor_email
<join_extension_donor_email>
WHERE donor_email.src_donor_id IS NOT NULL AND donor_email.donor_email_rank = 1
AND ((lower(donor_email.email_type) != 'primary' and coalesce(donor_email.email,'') <> '' ) OR lower(donor_email.email_type) = 'primary')
order by donor_email.src_donor_id, donor_email.email_type
```

### Row 1539 original query

Endpoint: `SQL`

```sql
-- #terukula 09/15 AMS-51 V0.1 created query to populate donor_email in AMS
-- #spatole 12/12 AMS-322 - Change references for source entities in the donor bound queries
-- #terukula 12/16 renamed secondary_email column to calculated field_1 and added case statement for email_type column
--#gsudhakar 10/6/23 added intake_dt field
--10/22/23 - Remove line break from src_donor_id column - ID with line break - B00579348
--#gsudhakar 11/08/23 add logic to include null emails where email_type=primary
SELECT
REPLACE(REPLACE(CAST(src_donor_id AS VARCHAR),char(10)), char(13)) AS src_donor_id,
email,
email_type,
intake_dt,
<source_partner_system_name_donor_email> as source_partner_system_name,
<source_eab_system_type> as source_eab_system_type
FROM
((SELECT
  CAST(donor_email.<src donor id> AS VARCHAR) AS src_donor_id,
  CAST(donor_email.<email> AS VARCHAR) AS email,
  CASE WHEN CAST(donor_email.<email type> AS VARCHAR) IS NULL THEN CAST('primary' AS VARCHAR)
       ELSE CAST(donor_email.<email type> AS VARCHAR)
  END AS email_type,
  donor_email.intake_date as intake_dt
FROM <[AMS].[donor_email].{fulfillment}> AS donor_email
WHERE CAST(donor_email.<src donor id> AS VARCHAR) IS NOT NULL
--AND CAST(donor_email.<email> AS VARCHAR) IS NOT NULL
)
UNION
(SELECT
  CAST(donor_email.<src donor id> AS VARCHAR) AS src_donor_id,
  CAST(donor_email.<Calculated Field 1> AS VARCHAR) AS email,
  CAST('secondary' AS VARCHAR) AS email_type,
  donor_email.intake_date as intake_dt
FROM <[AMS].[donor_email].{fulfillment}> AS donor_email
WHERE CAST(donor_email.<src donor id> AS VARCHAR) IS NOT NULL
AND CAST(donor_email.<Calculated Field 1> AS VARCHAR) IS NOT NULL
AND CAST(donor_email.<Calculated Field 1> AS VARCHAR) <>  CAST(NVL(donor_email.<email>,'No Email') AS VARCHAR)
)) AS donor_email

WHERE donor_email.src_donor_id IS NOT NULL --AND donor_email.email IS NOT NULL
AND ((lower(donor_email.email_type) != 'primary' and coalesce(donor_email.email,'') <> '' ) OR lower(donor_email.email_type) = 'primary')
order by donor_email.src_donor_id, donor_email.email_type
```
