-- jyothi 09/15/2022  V0.1 Added bound query for donor_audience_segments in AMS domain
-- Chitti 11/04/2022  V0.2 Rename query name
-- kkhanuja 11/17/2022 -- V0.1 Added bound query for donor_attributes in AMS domain
-- csugguna 2/10/2023 added eab_attribute_type field from AMS Donor attributes
-- blang 04/01/2024 added union all to pull in data from donor attributes segmentations
-- gmartin 10/27/2025 added custom segment to identify ('Parent of Graduate','Parent of Non-Graduate') donor categories to exclude for CYE26

select
  CAST(src_donor_id AS VARCHAR) AS src_donor_id ,
  CAST(attribute_type AS VARCHAR) AS attribute_type,
  ROW_NUMBER() OVER(ORDER BY src_donor_id) as attribute_key,
  CAST(src_donor_attribute_value AS VARCHAR) AS src_donor_attribute_value,
  CAST(effective_dt AS TIMESTAMP) AS effective_dt,
  CAST(eab_attribute_type AS VARCHAR) AS eab_attribute_type,
  effective_end_date,
  <source_partner_system_name_donor_attributes> as source_partner_system_name,
  <source_eab_system_type> as source_eab_system_type
  from (

select donor_attribute.src_donor_id as src_donor_id,
	'Athletics' as attribute_type,
    'Athletics' as src_donor_attribute_value, 
	donor_attribute.effective_date as effective_dt,
    'giving_society' as eab_attribute_type,
	NULL as effective_end_date
from <[AMS].[donor_attributes].{fulfillment}> as donor_attribute
where donor_attribute.calculated_field_2 in(
'1513-001'
,'4021-001'
,'4201-001'
,'2675-001'
,'1402-001'
,'3284-001'
,'2045-001'
,'4539-001'
,'4050-001'
,'4595-001'
,'2036-001'
,'3630-001'
,'2951-001'
,'3210-001'
,'4331-001'
,'2949-001'
,'3405-001'
,'2507-002'
,'2931-002'
,'2987-001'
,'3939-001'
,'4477-001'
,'4673-001'
,'1988-001'
,'1424-001'
,'1829-001'
,'2357-001'
,'4277-002'
,'3662-001'
,'2961-001'
,'1509-001'
,'1917-013'
,'8096-001'
,'2696-002'
,'3149-001'
,'3952-001'
,'4020-001'
,'4030-002'
,'4979-001'
,'4836-001'
,'4781-001'
,'4175-001'
,'4824-001'
,'3123-001'
,'4047-001'
,'1170-001'
,'4602-001'
,'1794-001'
,'4291-001'
,'4307-002'
,'2952-001'
,'3405-002'
,'1827-001'
,'4547-001'
,'1917-014'
,'3498-001'
,'3118-001'
,'2897-002'
,'2996-001'
,'4366-001'
,'2957-001'
,'1916-020'
,'2110-001'
,'5000-001'
,'4333-001'
,'3659-001'
,'2950-001'
,'3701-001'
,'1028-001'
,'3242-001'
,'4148-001'
,'3025-015'
,'3286-001'
,'1799-006'
,'8507-001'
,'2104-001'
,'3865-001'
,'3617-001'
,'3280-001'
,'4882-001'
,'3812-001'
,'4598-001'
,'4232-001'
,'4965-001'
,'2943-001'
,'2945-001'
,'3011-001'
,'4663-001'
,'4603-001'
,'1462-001'
,'4039-001'
,'3348-003'
,'2955-001'
,'3286-002'
,'1175-001'
,'2694-001'
,'2931-001'
,'1885-001'
,'3014-001'
,'1492-001'
,'3268-001'
,'4443-001'
,'4156-001'
,'4168-002'
,'4978-001'
,'4458-001'
,'2746-001'
,'2116-001'
,'2559-001'
,'3010-001'
,'1526-001'
,'2838-001'
,'2582-001'
,'3352-001'
,'3958-001'
,'3070-001'
,'4713-001'
,'4336-001'
,'4269-001'
,'1988-002'
,'3025-007'
,'4069-001'
,'1355-001'
,'2946-001'
,'4696-001'
,'3971-001'
,'3820-001'
,'3749-002'
,'4030-003'
,'1332-001'
,'2291-001'
,'3140-001'
,'3656-001'
,'4064-001'
,'4970-001'
,'4391-001'
,'3269-001'
,'4156-002'
,'1901-001'
,'2586-010'
,'2446-001'
,'5004-001'
,'1858-001'
,'4406-002'
,'4457-001'
,'3867-001'
,'3881-002'
,'2958-001'
,'2861-001'
,'3459-001'
,'4030-001'
,'3153-001'
,'4422-001'
,'1799-028'
,'2944-001'
,'2935-001'
,'4245-001'
,'2665-001'
,'1710-001'
,'1268-001'
,'1917-016'
,'3812-003'
,'2972-001'
,'1821-001'
,'1038-001'
,'2730-001'
,'2562-001'
,'3751-001'
,'3069-001'
,'2696-001'
,'2948-001'
,'3090-001'
,'4331-002'
,'2390-001'
,'4601-001'
,'3227-001'
,'3873-001'
,'2587-001'
,'3425-001'
,'3230-001'
,'3868-001'
,'3726-001'
,'3168-001'
,'2956-001'
,'3894-001'
,'1187-001'
,'3400-001'
,'4473-001'
,'3760-001'
,'2430-001'
,'4406-001'
,'3289-001'
,'2954-001'
,'4407-001'
,'2972-002'
,'4277-001'
,'4566-001'
,'3654-001'
,'4312-001'
,'2762-001'
,'1810-001'
,'2947-001'
,'2390-002'
,'2507-001'
,'4500-001'
,'3655-001'
,'3281-001'
,'3229-001'
,'2377-001'
,'3010-002'
,'3689-001'
,'4103-001'
,'3486-001'
,'1706-001'
,'1086-001'
,'3013-001'
,'3170-001'
,'4337-001'
,'2930-001'
,'4191-001'
,'3881-001'
,'2074-032'
,'2074-026'
,'4139-001'
,'3386-001'
,'4047-003'
,'4447-001'
,'3749-001'
,'4980-001'
,'4168-001'
,'3474-001'
,'3118-002'
,'4307-001'
,'4772-001'
,'2897-001'
,'4269-002'
,'2774-001'
,'4304-001'
,'1916-016'
,'2030-001'
,'4286-001'
,'3025-013'
,'5029-001'
,'5028-001'
,'5034-001'
,'5066-001'
,'5048-001'
,'4389-001'
,'5089-001'
,'5085-001'
,'5085-002'
,'5123-001'
,'5126-001'
,'5134-001'
,'5145-001'
,'5148-001'
,'5149-001'
,'5150-001'
,'5155-001'
,'5141-001'
,'5161-001'
,'5180-001'
,'5188-001'
,'2366-001'
,'4017-001'
,'5338-001'
,'5357-001'
,'5356-001'
,'5355-001'
,'3227-002'
,'5389-001'
,'4772-002'
,'5404-001'
,'5405-001'
,'5432-001'
,'5436-001'
,'5437-001'
,'5438-001'
,'5437-002'
,'5461-001'
,'5470-001'
,'2359-001'
,'3233-001'
,'3348-001'
,'3355-001'
,'3356-001'
,'3357-001'
,'3358-001'
,'3359-001'
,'3360-001'
,'3361-001'
,'3362-001'
,'3363-001'
,'3364-001'
,'3365-001'
,'3366-001'
,'3367-001'
,'3368-001'
,'3369-001'
,'1333-001'
,'1336-001'
,'1336-003'
,'1337-001'
,'1338-001'
,'1340-001'
,'1341-001'
,'1342-001'
,'1343-001'
,'1344-001'
,'1345-001'
,'1346-001'
,'1493-001'
,'1494-001'
,'1684-001'
,'1953-001'
,'2555-001'
,'2586-001'
,'2586-012'
,'2772-001'
,'3056-001'
,'3143-001'
,'4430-001'
,'4473-002'
,'4670-001'
,'4671-001'
,'4672-001'
,'4713-002'
,'5030-001'
,'5220-001'
,' 5358-001')
	
union all
	
select donor_attribute.src_donor_id as src_donor_id,
	'AC' as attribute_type,
    'AC' as src_donor_attribute_value, 
	donor_attribute.effective_date as effective_dt,
    'giving_society' as eab_attribute_type,
	NULL as effective_end_date
from <[AMS].[donor_attributes].{fulfillment}> as donor_attribute
where donor_attribute.calculated_field_3 = 'AC'
	
/*
union all

	select donor_attribute.src_donor_id as src_donor_id,donor_attribute.attribute_type as attribute_type ,
        donor_attribute.src_donor_attribute_value as src_donor_attribute_value, donor_attribute.effective_date as effective_dt,
        NULL as eab_attribute_type
from <[AMS].[donor_attributes].{fulfillment}> as donor_attribute
*/
	
union all

 select donor_attribute.src_donor_id as src_donor_id,
		'VIP' as attribute_type ,
        'VIP' as src_donor_attribute_value, 
		donor_attribute.effective_date as effective_dt,
        'giving_society' as eab_attribute_type,
		NULL as effective_end_date
from <[AMS].[donor_attributes].{fulfillment}> as donor_attribute
where donor_attribute.calculated_field_1 = 'Y'
	
union all

 select donor_attribute.src_donor_id as src_donor_id,
		'Affinity' as attribute_type ,
        'Affinity' as src_donor_attribute_value, 
		donor_attribute.effective_date as effective_dt,
        'giving_society' as eab_attribute_type,
		NULL as effective_end_date
from <[AMS].[donor_attributes].{fulfillment}> as donor_attribute
	where donor_attribute.calculated_field_3 = 'Y'
	
union all

 select donor_attribute.src_donor_id as src_donor_id,
		'General' as attribute_type ,
        'General' as src_donor_attribute_value, 
		donor_attribute.effective_date as effective_dt,
        'giving_society' as eab_attribute_type,
		NULL as effective_end_date
from <[AMS].[donor_attributes].{fulfillment}> as donor_attribute
	where donor_attribute.calculated_field_5 in ('Undeclared','Col of Liberal Arts & Appl Sci','Used for Academic Standing','Interdisciplinary Studies') OR donor_attribute.calculated_field_5 is null

	
union all

 select donor_attribute.src_donor_id as src_donor_id,
		'Graduate School' as attribute_type ,
        'Graduate School' as src_donor_attribute_value, 
		donor_attribute.effective_date as effective_dt,
        'giving_society' as eab_attribute_type,
		NULL as effective_end_date
from <[AMS].[donor_attributes].{fulfillment}> as donor_attribute
	where donor_attribute.calculated_field_4 = 'Alumni w/Graduate Degree'
	
union all


 select donor_attribute.src_donor_id as src_donor_id,
		'College of Arts and Science' as attribute_type ,
        'College of Arts and Science' as src_donor_attribute_value, 
		donor_attribute.effective_date as effective_dt,
        'giving_society' as eab_attribute_type,
		NULL as effective_end_date
from <[AMS].[donor_attributes].{fulfillment}> as donor_attribute
	where donor_attribute.calculated_field_5 = 'College of Arts and Science'

union all


 select donor.src_donor_id as src_donor_id,
		'No Contact Parents' as attribute_type ,
        'no contact parents' as src_donor_attribute_value, 
		current_date as effective_dt,
        'custom_segment' as eab_attribute_type,
		NULL as effective_end_date
from <[AMS].[donor].{fulfillment}> as donor
	where donor.DONOR_CATEGORY_DESCRIPTION in ('Parent of Graduate','Parent of Non-Graduate')	
	
union all

 select donor_attribute.src_donor_id as src_donor_id,
		'College of Creative Arts' as attribute_type ,
        'College of Creative Arts' as src_donor_attribute_value, 
		donor_attribute.effective_date as effective_dt,
        'giving_society' as eab_attribute_type,
		NULL as effective_end_date
from <[AMS].[donor_attributes].{fulfillment}> as donor_attribute
	where donor_attribute.calculated_field_5 = 'College of Creative Arts'
	
union all

 select donor_attribute.src_donor_id as src_donor_id,
		'Engineering And Computing' as attribute_type ,
        'Engineering And Computing' as src_donor_attribute_value, 
		donor_attribute.effective_date as effective_dt,
        'giving_society' as eab_attribute_type,
		NULL as effective_end_date
from <[AMS].[donor_attributes].{fulfillment}> as donor_attribute
	where donor_attribute.calculated_field_5 = 'Col of Engineering & Computing'   
	
union all

 select donor_attribute.src_donor_id as src_donor_id,
		'Farmer School Of Business' as attribute_type ,
        'Farmer School Of Business' as src_donor_attribute_value, 
		donor_attribute.effective_date as effective_dt,
        'giving_society' as eab_attribute_type,
		NULL as effective_end_date
from <[AMS].[donor_attributes].{fulfillment}> as donor_attribute
	where donor_attribute.calculated_field_5 = 'Farmer School of Business'
	
union all

 select donor_attribute.src_donor_id as src_donor_id,
		'Regionals' as attribute_type ,
        'Regionals' as src_donor_attribute_value, 
		donor_attribute.effective_date as effective_dt,
        'giving_society' as eab_attribute_type,
		NULL as effective_end_date
from <[AMS].[donor_attributes].{fulfillment}> as donor_attribute
	where donor_attribute.calculated_field_6 = 'Y'
	
union all

 select donor_attribute.src_donor_id as src_donor_id,
		'College of Eduation, Health, And Society' as attribute_type ,
        'College of Eduation, Health, And Society' as src_donor_attribute_value, 
		donor_attribute.effective_date as effective_dt,
        'giving_society' as eab_attribute_type,
		NULL as effective_end_date
from <[AMS].[donor_attributes].{fulfillment}> as donor_attribute
	where donor_attribute.calculated_field_5 = 'Col of Educ, Health & Society'
	
union all

 select donor_attribute.src_donor_id as src_donor_id,
		'Liberal Arts and Appl Sci' as attribute_type ,
        'Liberal Arts and Appl Sci' as src_donor_attribute_value, 
		donor_attribute.effective_date as effective_dt,
        'giving_society' as eab_attribute_type,
		NULL as effective_end_date
from <[AMS].[donor_attributes].{fulfillment}> as donor_attribute
	where donor_attribute.calculated_field_5 = 'Col of Liberal Arts & Appl Sci'

union all

 select donor_attribute.src_donor_id as src_donor_id,
		'Interdisciplinary Studies' as attribute_type ,
        'Interdisciplinary Studies' as src_donor_attribute_value, 
		donor_attribute.effective_date as effective_dt,
        'giving_society' as eab_attribute_type,
		NULL as effective_end_date
from <[AMS].[donor_attributes].{fulfillment}> as donor_attribute
	where donor_attribute.calculated_field_5 = 'Interdisciplinary Studies'
	
union all

 select donor_attribute.src_donor_id as src_donor_id,
		'European Center' as attribute_type ,
        'European Center' as src_donor_attribute_value, 
		donor_attribute.effective_date as effective_dt,
        'giving_society' as eab_attribute_type,
		NULL as effective_end_date
from <[AMS].[donor_attributes].{fulfillment}> as donor_attribute
	where donor_attribute.calculated_field_7 = 'Y'

union all

 select donor_attribute.src_donor_id as src_donor_id,
		'Student_Athlete' as attribute_type ,
        'Student_Athlete' as src_donor_attribute_value, 
		donor_attribute.effective_date as effective_dt,
        'giving_society' as eab_attribute_type,
		NULL as effective_end_date
from <[AMS].[donor_attributes].{fulfillment}> as donor_attribute
	where donor_attribute.calculated_field_8 is not null
	
union all
	
 select donor_attribute.src_donor_id as src_donor_id,
		'Current_Parent' as attribute_type,
		'Current_Parent' as src_donor_attribute_value,
		donor_attribute.effective_date as effective_dt,
		'giving_society' as eab_attribute_type,
		null as effective_end_date
from <[AMS].[donor_attributes].{fulfillment}> as donor_attribute
	where donor_attribute.calculated_field_4 = 'Parent of Current Student'


union all
 
select src_donor_id,
		   attribute_type,
		   src_donor_attribute_value,
		   effective_dt,
		   eab_attribute_type,
		   effective_end_date
	from (
		select donor_attributes_segmentations.src_donor_id as src_donor_id,
			  donor_attributes_segmentations.attribute_type as attribute_type,
			  donor_attributes_segmentations.src_donor_attribute_value as src_donor_attribute_value,
			  donor_attributes_segmentations.effective_date as effective_dt,
			  'custom_segment' as eab_attribute_type,
			  donor_attributes_segmentations.effective_end_date  as effective_end_date,
			  ROW_NUMBER() OVER (PARTITION BY 
				donor_attributes_segmentations.src_donor_id, 
				LOWER(donor_attributes_segmentations.attribute_type), 
				LOWER(donor_attributes_segmentations.src_donor_attribute_value) 
				ORDER BY donor_attributes_segmentations.effective_date DESC) AS rn
	  from <[AMS].[donor_attributes_segmentations].{fulfillment}> as donor_attributes_segmentations
	  ) as a 
		where rn = 1

	
  ) as ds  where src_donor_id is not null and attribute_type is not null order by attribute_key,src_donor_id,attribute_type