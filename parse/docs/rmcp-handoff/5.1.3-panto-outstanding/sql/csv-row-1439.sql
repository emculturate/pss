-- jyothi 09/15/2022  V0.1 Added bound query for donor_audience_segments in AMS domain
-- Chitti 11/04/2022  V0.2 Rename query name
-- kkhanuja 11/17/2022 -- V0.1 Added bound query for donor_attributes in AMS domain
-- csugguna 2/10/2023 added eab_attribute_type field from AMS Donor attributes
--jspring 1/3/2024: added giving society for previous DoG donors 

select
  CAST(src_donor_id AS VARCHAR) AS src_donor_id ,
  CAST(attribute_type AS VARCHAR) AS attribute_type,
  ROW_NUMBER() OVER(ORDER BY src_donor_id) as attribute_key,
  CAST(src_donor_attribute_value AS VARCHAR) AS src_donor_attribute_value,
  CAST(effective_dt AS TIMESTAMP) AS effective_dt,
  CAST(eab_attribute_type AS VARCHAR) AS eab_attribute_type,
  effective_end_date
  from (
	
select distinct gifts.src_donor_id as src_donor_id,
		'Previous DOG Donor' as attribute_type ,
        'Previous DOG Donor' as src_donor_attribute_value,
		current_date as effective_dt,
        'giving_society' as eab_attribute_type,
		NULL as effective_end_date
from <[AMS].[gifts].{fulfillment}> as gifts
	where gifts.APPEAL_CODE in ('AGGDLET23', 'AG23GDPC')

union all

 select donor_attribute.src_donor_id as src_donor_id,donor_attribute.attribute_type as attribute_type ,
        donor_attribute.src_donor_attribute_value as src_donor_attribute_value, donor_attribute.effective_date as effective_dt,
        'giving_society' as eab_attribute_type,
	    NULL as effective_end_date
from <[AMS].[donor_attributes].{fulfillment}> as donor_attribute
	
union all

 select donor_attributes_segmentations.src_donor_id as src_donor_id,
        donor_attributes_segmentations.attribute_type as attribute_type,
        donor_attributes_segmentations.src_donor_attribute_value as src_donor_attribute_value,
        donor_attributes_segmentations.effective_date as effective_dt,
        'custom_segment' as eab_attribute_type,
       donor_attributes_segmentations.effective_end_date  as effective_end_date
from <[AMS].[donor_attributes_segmentations].{fulfillment}> as donor_attributes_segmentations
) as ds  where src_donor_id is not null and attribute_type is not null order by attribute_key,src_donor_id,attribute_type