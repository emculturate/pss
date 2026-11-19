-- jyothi 09/15/2022  V0.1 Added bound query for donor_audience_segments in AMS domain
-- Chitti 11/04/2022  V0.2 Rename query name
-- kkhanuja 11/17/2022 -- V0.1 Added bound query for donor_attributes in AMS domain
-- csugguna 2/10/2023 added eab_attribute_type field from AMS Donor attributes

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


 select donor_attribute.src_donor_id as src_donor_id,donor_attribute.attribute_type as attribute_type ,
        donor_attribute.src_donor_attribute_value as src_donor_attribute_value, donor_attribute.effective_date as effective_dt,
        NULL as eab_attribute_type, NULL as effective_end_date
from <[AMS].[donor_attributes].{fulfillment}> as donor_attribute
	
union all
	
select distinct src_donor_id
	, 'FormerStudentAth' as donor_attribute
	, 'FormerStudentAth' as src_donor_attribute_value
	, da.effective_date as effective_dt
	, 'giving_society' as eab_attribute_type,
	NULL as effective_end_date
	from <[AMS].[donor_attributes].{fulfillment}> as da
	where 1=1
	and (da.calculated_field_1 is not null
		 or da.calculated_field_2 is not null
		 or da.calculated_field_3 is not null
		 or da.calculated_field_4 is not null)

union all

select distinct src_donor_id
	, 'SeasonTicketHold' as donor_attribute
	, 'SeasonTicketHold' as src_donor_attribute_value
	, d.effective_date as effective_dt
	, 'giving_society' as eab_attribute_type,
	NULL as effective_end_date
	from <[AMS].[donor_attributes].{fulfillment}> as d
	where 1=1
	and (d.calculated_field_5 is not null
	or d.calculated_field_6 is not null
	or d.calculated_field_7 is not null
	or d.calculated_field_8 is not null
	or d.calculated_field_9 is not null)

union all
	
select distinct src_donor_id
	, 'obsdconsecutive' as donor_attribute
	, 'obsdconsecutive' as src_donor_attribute_value
	, current_date as effective_dt
	, 'giving_society' as eab_attribute_type,
	NULL as effective_end_date
	from <[AMS].[gifts].{fulfillment}> as g
	where 1=1
	and g.appeal_code like '%OBSD%'
    and year(cast(g.gift_date as date)) in ('2024','2025')
    group by g.src_donor_id
    having count (distinct year(cast(g.gift_date as date))) = 2

union all
									 
select distinct src_donor_id
	, 'obsdfirst' as donor_attribute
	, 'obsdfirst' as src_donor_attribute_value
	, current_date as effective_dt
	, 'giving_society' as eab_attribute_type,
	NULL as effective_end_date
	from <[AMS].[gifts].{fulfillment}> as g
	where 1=1
	and g.appeal_code like '%OBSD%'
	group by 1
	having 
	count(distinct year(cast(g.gift_date as date)) = 1)
	and max( year(cast(g.gift_date as date))) = 2025

union all
					   
select distinct src_donor_id
	, 'athletics' as donor_attribute
	, 'athletics' as src_donor_attribute_value
	, current_date as effective_dt
	, 'giving_society' as eab_attribute_type,
	NULL as effective_end_date
	from <[AMS].[gifts].{fulfillment}> as g
	where 1=1
	and g.fund_code in ('1379065','755870','440039','790521','1291511','1291667','478857','99255','1019694','365671','363968','1417178','1345590','1347234','945496','364210','1417179','1379382','1048961','1340171','917179','1047700','1021474','917163','1009689','1048956','942039','1292601','1292281','917177','947174','690574','994747','941847','915788','941896','1292106','1048959','1292279','1292483','1292280','1021475','941895','917165','996559','1021462','941849','004590','008010','008010A','008010B','008020','008020A','008020B','008030','008030A','008030B','008040','008040A','008040B','008050','008060','008060A','008070','008090','008090A','008090B','008100','008110','008110A','008110B','008120','008130','008140','008150','008160','008160A','008160B','008170','008180','008190','008190B','008200','008210','008220','008220B','008230','008240','008250','008260','008270','008280','008290','008300','008310','008310A','008320','008330','008340','008350','008350A','008350B','008360','008360A','008360B','008370','008380','008390','008390A','008400','008410','008420','008430','008440','008450','008460','008470','008480','008490','008500','008500A','008510','008520','008530','008540','008550','008560','008570','008580','008590','008600','008610','008620','008630','008630A','008640','008650','008660','008670','008680','008690','008700','008710','008720','008730','008740','008750','008760','008770','008780','008790','008800','008810','008820','008830','008840','008850','008860','008870','008880','008890','008900','008910','008920','008930','008940','008950','008960','008970','008980','008990','086050','086050A','086130','097010','097020','097020A','097020B','097030','097040','097050','097050A','097060','097070','097070A','097080','097080A','097080B','097090','097100','097100A','097100B','097110','097110A','097120','097120B','097130','097180','097190','097310','097320','097330','097330A','097340','097400','097500','097510','097520','097530','097540','097550','097560','097570','097590','097600','097800','300130','301040','302800','600400','800310','800340') and year(cast(g.gift_date as date)) >=2016

union all

 select donor_attributes_segmentations.src_donor_id as src_donor_id,
        donor_attributes_segmentations.attribute_type as attribute_type,
        donor_attributes_segmentations.src_donor_attribute_value as src_donor_attribute_value,
        donor_attributes_segmentations.effective_date as effective_dt,
        'custom_segment' as eab_attribute_type,
       donor_attributes_segmentations.effective_end_date  as effective_end_date
from <[AMS].[donor_attributes_segmentations].{fulfillment}> as donor_attributes_segmentations
	
  ) as ds  where src_donor_id is not null and attribute_type is not null order by attribute_key,src_donor_id,attribute_type