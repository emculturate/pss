select distinct
nullif(trim(regexp_replace(agg2.primary_student_id,'"|[\\000-\\037\\177]','')),'') as primary_student_id,
/*3-14-2023: replacing all whitespace characters with a single space, this should convert tabs, carriage returns, line feeds, and space to a space
then replaces any non-ascii [^[:ascii:]] or NUL characters [\\0] with '' - ccreekman*/
nullif(trim(regexp_replace(regexp_replace(agg2.addr1,'[\\s]',' '),'[^[:ascii:]]|[\\0]|"|[\\000-\\037\\177]','')),'') as addr1,
nullif(trim(regexp_replace(regexp_replace(agg2.addr2,'[\\s]',' '),'[^[:ascii:]]|[\\0]|"|[\\000-\\037\\177]','')),'') as addr2,
nullif(trim(regexp_replace(regexp_replace(agg2.city,'[\\s]',' '),'[^[:ascii:]]|[\\0]|"|[\\000-\\037\\177]','')),'') as city,
nullif(trim(regexp_replace(regexp_replace(agg2.state,'[\\s]',' '),'[^[:ascii:]]|[\\0]|"|[\\000-\\037\\177]','')),'') as state,
nullif(trim(regexp_replace(regexp_replace(agg2.county,'[\\s]',' '),'[^[:ascii:]]|[\\0]|"|[\\000-\\037\\177]','')),'') as county,
case
	when regexp_like(agg2.zip,'[\\d]{5,5}.*[\\d]{4,4}.*') 
	then regexp_extract(agg2.zip,'[\\d]{5,5}',1,1)||'-'||regexp_extract(agg2.zip,'[\\d]{4,4}',6,1)
	else nullif(trim(regexp_replace(regexp_replace(agg2.zip,'[\\s]',' '),'[^[:ascii:]]|[\\0]|"|[\\000-\\037\\177]','')),'')
	end as zip,
nullif(trim(regexp_replace(regexp_replace(agg2.country,'[\\s]',' '),'[^[:ascii:]]|[\\0]|"|[\\000-\\037\\177]','')),'') as country,
agg2.address_type,
agg2.eab_country,
agg2.observation_time as intake_dt,
agg2.observation_time

from (
  
--Address set 1
select distinct
stud_addr.primary_student_id,
stud_addr.address_line_1 as addr1,
stud_addr.address_line_2 as addr2,
stud_addr.city,
stud_addr.state_or_province as state,
stud_addr.county,
stud_addr.zip_code as zip,
stud_addr.country,
stud_addr.observation_time,

case when pcm_addr_type.eab_standard_value = 'Unmapped Value'
then 'Unknown'
else pcm_addr_type.eab_standard_value
end as address_type,

case when pcm_country.eab_standard_value = 'Virgin Islands US'
then 'Virgin Islands U.S.'
else pcm_country.eab_standard_value 
end as eab_country
--select *
from <[ALR].[Student Address].{fulfillment}> stud_addr


left outer join <[ALR].[Partner Code Mapping].{convert}> pcm_addr_type
on (pcm_addr_type.field_name = 'address_type'
and coalesce(stud_addr.address_type,'') = coalesce(pcm_addr_type.partner_value,''))
												   

----------------------------------------
												   
left outer join 
(select agg.field_name, 

case when agg.eab_standard_value is null
then 'Unmapped Value'
else agg.eab_standard_value
end as eab_standard_value,

agg.partner_value, agg.record_count

from (

select pcm_country.field_name, pcm_country.eab_standard_value, pcm_country.partner_value, pcm_country.record_count,
row_number() over (partition by pcm_country.partner_value order by pcm_country.eab_standard_value nulls last) as rn
from <[ALR].[Partner Code Mapping].{convert_country}> pcm_country

) agg

where agg.rn = 1) pcm_country


on coalesce(stud_addr.country,'') = coalesce(pcm_country.partner_value,'')

----------------------------------------
where stud_addr.calculated_field_9 = 'Transfer'
and stud_addr.calculated_field_10 = 'Continuing Education (UG)'   												   
--where <where_statement>

												   

union all

--Address set 2
select distinct
stud_addr.primary_student_id,
stud_addr.calculated_field_1 as addr1,
stud_addr.calculated_field_2 as addr2,
stud_addr.calculated_field_3 as city,
stud_addr.calculated_field_4 as state,
stud_addr.calculated_field_5 as county,
stud_addr.calculated_field_6 as zip,
stud_addr.calculated_field_7 as country,
stud_addr.observation_time,

case when pcm_addr_type.eab_standard_value = 'Unmapped Value'
then 'Unknown'
else pcm_addr_type.eab_standard_value
end as address_type,

case when pcm_country.eab_standard_value = 'Virgin Islands US'
then 'Virgin Islands U.S.'
else pcm_country.eab_standard_value 
end as eab_country

from <[ALR].[Student Address].{fulfillment}> stud_addr

left outer join <[ALR].[Partner Code Mapping].{convert}> pcm_addr_type
on (pcm_addr_type.field_name = 'address_type'
and coalesce(stud_addr.calculated_field_8,'') = coalesce(pcm_addr_type.partner_value,''))
													 
----------------------------------------
												   
left outer join 
(select agg.field_name, 

case when agg.eab_standard_value is null
then 'Unmapped Value'
else agg.eab_standard_value
end as eab_standard_value,

agg.partner_value, agg.record_count

from (

select pcm_country.field_name, pcm_country.eab_standard_value, pcm_country.partner_value, pcm_country.record_count,
row_number() over (partition by pcm_country.partner_value order by pcm_country.eab_standard_value nulls last) as rn
from <[ALR].[Partner Code Mapping].{convert_country}> pcm_country

) agg

where agg.rn = 1) pcm_country

on coalesce(stud_addr.calculated_field_7,'') = coalesce(pcm_country.partner_value,'')

----------------------------------------

--where <where_statement>  

) agg2

where coalesce(agg2.addr1,'') ||
coalesce(agg2.addr2,'') ||
coalesce(agg2.city,'') ||
coalesce(agg2.state,'') ||
coalesce(agg2.county,'') ||
coalesce(agg2.zip,'') ||
coalesce(agg2.country,'') <> ''
and coalesce(agg2.primary_student_id,'') <> ''