/*Student Year Funds query but with eab_fund_amount and syf_join_extension variables replaced with bundle code
purpose of eab_fund_amount logic is to ensure final Fund totals include or exclude award amounts per partner logic
	- if award should be included in fund totals - return the row's award amount 
	- if award should not be included - set it to 0

General Set Up Instructions:
1. Review legacy logic and identify all data points necessary for calculations and note which Entity tables contain this data
2. If necessary data points are bound to tables other than Student Year Funds:
	- To start: Update "<join_extension.Student Year Funds> bundle code" section with simple left joins to these Entity fulfillment tables
3. Build custom case statement in "<eab_fund_amount>" section, replicating legacy logic
4. Adjust "<join_extension.Student Year Funds> bundle code" section as needed 
5. Validate counts using queries at end
6. When all counts are validated: update <eab_fund_amount> and <join_extension.Student Year Funds> with your code

*/

WITH funds_data AS (
    SELECT * FROM (
        SELECT DISTINCT
            nullif(trim(regexp_replace(syf.eab_student_id,'"|[\\000-\\037\\177]','')),'') AS eab_student_id
          , nullif(trim(regexp_replace(syf.eab_student_id_type,'"|[\\000-\\037\\177]','')),'') AS eab_student_id_type
          , case when pcm_entry_year_academic.eab_standard_value = 'Unmapped Value'
				then null
				else pcm_entry_year_academic.eab_standard_value
				end as eab_entry_year_academic
          , nullif(trim(regexp_replace(syf.fund_code,'"|[\\000-\\037\\177]','')),'') AS fund_code
          , syf.current_entry_year_academic AS curr_entry_year_academic
          , syf.offered_amount AS offered_amt
          , CAST(syf.offered_date AS timestamp) AS offered_dt
          , syf.accept_amount AS accept_amt
          , CAST(syf.accept_date AS timestamp) AS accept_dt
          , syf.cancel_amount AS cancel_amt
          , CAST(syf.cancel_date AS timestamp) AS cancel_dt
          , syf.declined_amount AS declined_amt
          , CAST(syf.declined_date AS timestamp) AS declined_dt
          , syf.fund_name AS fund_name
          , syf.fund_type AS fund_type						
          , CASE WHEN pcm_eab_fund_type.eab_standard_value = 'Unmapped Value' THEN 'Unknown'
				ELSE pcm_eab_fund_type.eab_standard_value
				END AS eab_fund_type
		  , CAST(case when accept_dt IS NOT NULL then accept_dt 
				 when offered_dt IS NOT NULL then offered_dt 
				 when declined_dt IS NOT NULL then declined_dt 
				 else cancel_dt end AS date) AS eab_fund_date
          , CASE WHEN pcm_eab_discount_ind.eab_standard_value = 'Unmapped Value' THEN 'Unknown'
				ELSE CAST(pcm_eab_discount_ind.eab_standard_value AS Boolean)
				END AS eab_discount_ind
          , CAST(syf.intake_dt AS timestamp) AS intake_dt								
          --, case when coalesce(syf.calculated_field_1,'') in ('E') then syf.calculated_field_2 else null end AS estimated_amt
          --, case when coalesce(syf.calculated_field_1,'') in ('NA') then syf.calculated_field_2 else null end as na_amt		


						
/***************************************************************************************************
eab_fund_amount - customize per partner logic
***************************************************************************************************/
		  , CAST(case
				 when syf.CALCULATED_FIELD_1 in ('A','O')
				 	and acceptinstid.eab_student_id is not null
				 	and pcm_eab_fund_type.eab_standard_value in ('Endowed Award', 'Institutional Merit', 'Institutional Need', 'Institutional Award', 'Institutional Stack', 'Tuition Waiver', 'Athletic Scholarship')
				 then CALCULATED_FIELD_2
				 when acceptinstid.eab_student_id is null
				 	and pcm_eab_fund_type.eab_standard_value in ('Endowed Award', 'Institutional Merit', 'Institutional Need', 'Institutional Award', 'Institutional Stack', 'Tuition Waiver', 'Athletic Scholarship', 'Federal Grant', 'Pell Grant', 'State Grant')
				 	and acceptgovtid.eab_student_id is null
				 then CALCULATED_FIELD_2
				 when syf.CALCULATED_FIELD_1 in ('A','O')
				 	and pcm_eab_fund_type.eab_standard_value in ('Federal Grant', 'Pell Grant', 'State Grant')
				 	and acceptgovtid.eab_student_id is not null
				 then CALCULATED_FIELD_2
				 when pcm_eab_fund_type.eab_standard_value in ('Need Loan', 'No Need Loan', 'Work Study', 'Outside Scholarship')
				 then CALCULATED_FIELD_2
				 else '0'
			
/*				case when accept_amt IS NOT NULL AND accept_amt > 0 
				then TRY_CAST(accept_amt AS number)
				when offered_amt IS NOT NULL AND offered_amt > 0 
				then TRY_CAST(offered_amt AS number)
				when declined_amt IS NOT NULL AND declined_amt > 0 
				then TRY_CAST(declined_amt AS number)
				else TRY_CAST(cancel_amt AS number)
					*/
			end AS decimal (10,2)) as eab_fund_amount		
			
/*************************************************************************************************/

        FROM <[Enroll360].[Student Year Funds].{fulfillment}> AS syf   
											   
        LEFT OUTER JOIN <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_eab_fund_type
                     ON (pcm_eab_fund_type.field_name = 'eab_fund_type'
                    AND COALESCE(syf.fund_type,'') = COALESCE(pcm_eab_fund_type.partner_value,''))
        LEFT OUTER JOIN <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_eab_discount_ind
                     ON (pcm_eab_discount_ind.field_name = 'eab_discount_ind'
                    AND COALESCE(syf.fund_type,'') = COALESCE(pcm_eab_discount_ind.partner_value,''))
		left outer join <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_entry_year_academic
					on (pcm_entry_year_academic.field_name = 'eab_entry_year_academic'
					and coalesce(syf.current_entry_year_academic,'') = coalesce(pcm_entry_year_academic.partner_value,''))

/***************************************************************************************************************************
<join_extension.Student Year Funds> bundle code 
- add joins to other fulfillment tables here
***************************************************************************************************************************/

left join (
  select distinct *
  from (
	select distinct eab_student_id, CALCULATED_FIELD_1, FUND_TYPE,
  
	CASE WHEN pcm_eab_fund_type.eab_standard_value = 'Unmapped Value' THEN 'Unknown'
				ELSE pcm_eab_fund_type.eab_standard_value
				END AS eab_fund_type
  
	from <[Enroll360].[Student Year Funds].{fulfillment}> syf
	LEFT OUTER JOIN <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_eab_fund_type
                     ON (pcm_eab_fund_type.field_name = 'eab_fund_type'
                    AND COALESCE(syf.fund_type,'') = COALESCE(pcm_eab_fund_type.partner_value,''))
	) acceptinstid
	where 1=1
	and CALCULATED_FIELD_1 = 'A'
	and EAB_FUND_TYPE in ('Endowed Award', 'Institutional Merit', 'Institutional Need', 'Institutional Award', 'Institutional Stack', 'Tuition Waiver', 'Athletic Scholarship')
			  
  ) acceptinstid
  on syf.eab_student_id = acceptinstid.eab_student_id
	
left join (
  select distinct *
  from (
	select distinct eab_student_id, CALCULATED_FIELD_1, FUND_TYPE,
  
	CASE WHEN pcm_eab_fund_type.eab_standard_value = 'Unmapped Value' THEN 'Unknown'
				ELSE pcm_eab_fund_type.eab_standard_value
				END AS eab_fund_type
  
	from <[Enroll360].[Student Year Funds].{fulfillment}> syf
	LEFT OUTER JOIN <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_eab_fund_type
                     ON (pcm_eab_fund_type.field_name = 'eab_fund_type'
                    AND COALESCE(syf.fund_type,'') = COALESCE(pcm_eab_fund_type.partner_value,''))
	) acceptgovtid
	where 1=1
	and CALCULATED_FIELD_1 = 'A'
	and EAB_FUND_TYPE in ('Federal Grant', 'Pell Grant', 'State Grant')

  ) acceptgovtid
  on syf.eab_student_id = acceptgovtid.eab_student_id  


/**************************************************************************************************************************/
		
    ) sq
    WHERE COALESCE(sq.eab_student_id,'')<>''
      AND COALESCE(sq.eab_student_id_type,'')<>''
      AND COALESCE(sq.eab_fund_type,'')<>''
)

																				
/**********************************************************************************************
above cte produces funds_data table which replicates <[Enroll360].[Student Year Funds].{final}>
use queries below to pull final table data as desired
**********************************************************************************************/

/*Total Funds per EAB_Fund_Type rollup*/
--comment out this section before trying to run other query
--edit criteria to select desired Fund category
select 
EAB_FUND_TYPE, syf.EAB_ENTRY_YEAR_ACADEMIC
, sum(EAB_FUND_AMOUNT) as total_aid_calc
, to_number(sum(coalesce(ACCEPT_AMT,0)+coalesce(OFFERED_AMT,0)+coalesce(CANCEL_AMT,0)+coalesce(DECLINED_AMT,0)),13,2) as total_aid_raw
--, to_number(sum(coalesce(ACCEPT_AMT,0)+coalesce(OFFERED_AMT,0)+coalesce(CANCEL_AMT,0)+coalesce(DECLINED_AMT,0)+coalesce(estimated_AMT,0)+coalesce(na_AMT,0)),13,2) as total_aid_raw_with_na_e
, count(distinct syf.eab_student_id) as total_unique_stud
, cast((total_aid_calc/total_unique_stud) as decimal (10,2)) as avg_aid_per_unique_stud
, to_number(sum(case when EAB_FUND_AMOUNT <> 0 then ACCEPT_AMT else 0 end),13,2) as total_accepted_calc
, to_number(sum(case when EAB_FUND_AMOUNT <> 0 then OFFERED_AMT else 0 end),13,2) as total_offer_calc
, to_number(sum(case when EAB_FUND_AMOUNT <> 0 then CANCEL_AMT else 0 end),13,2) as total_cancelled_calc
, to_number(sum(case when EAB_FUND_AMOUNT <> 0 then DECLINED_AMT else 0 end),13,2) as total_declined_calc
, sum(ACCEPT_AMT) as total_accepted
, sum(OFFERED_AMT) as total_offer
, sum(CANCEL_AMT) as total_canceled
, sum(DECLINED_AMT) as total_declined
--, sum(estimated_AMT) as total_est
--, sum(na_AMT) as total_na
													  
from funds_data syf

join  <[Enroll360].[Student Term SIS].{final}> stud_term
on (syf.eab_student_id = stud_term.eab_student_id
	--and coalesce(syf.eab_entry_year_academic,0) = coalesce(stud_term.eab_entry_year_academic,0)
   )

left outer join <[Enroll360].[Student Term Application SIS].{final}> stud_term_app
	on (stud_term.eab_student_id = stud_term_app.eab_student_id
	and coalesce(stud_term.eab_student_type,'') = 'Freshman'
	and coalesce(stud_term.eab_entry_term,'') = coalesce(stud_term_app.eab_entry_term,'')
	and coalesce(stud_term.eab_entry_year_academic,0) = coalesce(stud_term_app.eab_entry_year_academic,0))
													  
where 1=1
and stud_term.eab_student_type = 'Freshman'

--and eab_fund_type  in ('Insitutional Award')

group by syf.EAB_FUND_TYPE, syf.EAB_ENTRY_YEAR_ACADEMIC
order by syf.EAB_ENTRY_YEAR_ACADEMIC desc, syf.eab_fund_type

/*Student Level data per fund*/
--comment in this query and comment out above Total Funds per EAB_Fund_Type rollup query to run this query*/


/*select 
syf.eab_student_id, EAB_FUND_TYPE, syf.EAB_ENTRY_YEAR_ACADEMIC
, stud_term.eab_student_type, fund_code, stud_term_app.eab_admit_ind, stud_term_app.eab_deposit_ind, stud_term.eab_current_funnel_status
, sum(EAB_FUND_AMOUNT) as total_aid_calc
, to_number(sum(coalesce(ACCEPT_AMT,0)+coalesce(OFFERED_AMT,0)+coalesce(CANCEL_AMT,0)+coalesce(DECLINED_AMT,0)),13,2) as total_aid_raw
--, to_number(sum(coalesce(ACCEPT_AMT,0)+coalesce(OFFERED_AMT,0)+coalesce(CANCEL_AMT,0)+coalesce(DECLINED_AMT,0)+coalesce(estimated_AMT,0)+coalesce(na_AMT,0)),13,2) as total_aid_raw_w_na_e
, to_number(sum(case when EAB_FUND_AMOUNT <> 0 then ACCEPT_AMT else 0 end),13,2) as total_accepted_calc
, to_number(sum(case when EAB_FUND_AMOUNT <> 0 then OFFERED_AMT else 0 end),13,2) as total_offer_calc
, to_number(sum(case when EAB_FUND_AMOUNT <> 0 then CANCEL_AMT else 0 end),13,2) as total_cancelled_calc
, to_number(sum(case when EAB_FUND_AMOUNT <> 0 then DECLINED_AMT else 0 end),13,2) as total_declined_calc
, sum(ACCEPT_AMT) as total_accepted
, sum(OFFERED_AMT) as total_offer
, sum(CANCEL_AMT) as total_canceled
, sum(DECLINED_AMT) as total_declined
													  
from funds_data syf

join  <[Enroll360].[Student Term SIS].{final}> stud_term
on (syf.eab_student_id = stud_term.eab_student_id)

left outer join <[Enroll360].[Student Term Application SIS].{final}> stud_term_app
	on (stud_term.eab_student_id = stud_term_app.eab_student_id
	and coalesce(stud_term.eab_student_type,'') = 'Freshman'
	and coalesce(stud_term.eab_entry_term,'') = coalesce(stud_term_app.eab_entry_term,'')
	and coalesce(stud_term.eab_entry_year_academic,0) = coalesce(stud_term_app.eab_entry_year_academic,0))
													  
where 1=1
and stud_term.eab_student_type = 'Freshman'
--and eab_fund_type  = 'Institutional Award'

group by syf.EAB_FUND_TYPE, syf.EAB_ENTRY_YEAR_ACADEMIC
, stud_term.eab_student_type, fund_code
, syf.eab_student_id
, stud_term_app.eab_admit_ind, stud_term_app.eab_deposit_ind
, stud_term.eab_current_funnel_status
order by syf.EAB_ENTRY_YEAR_ACADEMIC desc, syf.eab_student_id, syf.eab_fund_type
, stud_term.eab_student_type, syf.EAB_FUND_TYPE, stud_term.eab_current_funnel_status
, fund_code

limit 5000*/										 
/***********************************************************************/