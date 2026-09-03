/*Denver Student Year Funds query but with eab_fund_amount and syf_join_extension variables replaced with bundle code
adjust code in those two variable sections to tweak eab_fund_amount results
additional fields created for investigation purposes: 
estimated_amt = award amount if code = 'E'
na_amt = award amount if code = 'NA'
acc_eab_fund_type = aggregate of all accepted award eab_fund_types per student in the acceptawds table 
	(eg. student 12345 has acc_eab_fund_type value of Institutional Merit|Endowed Award|Athletic Scholarship)
*/

WITH funds_data AS (
    SELECT * FROM (
        SELECT DISTINCT
            nullif(trim(regexp_replace(syf.eab_student_id,'"|[\\000-\\037\\177]','')),'') AS eab_student_id
          , nullif(trim(regexp_replace(syf.eab_student_id_type,'"|[\\000-\\037\\177]','')),'') AS eab_student_id_type
		, acceptawds.eab_fund_type as acc_eab_fund_type
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
          , case when coalesce(syf.calculated_field_1,'') in ('E') then syf.calculated_field_2 else null end AS estimated_amt
          , case when coalesce(syf.calculated_field_1,'') in ('NA') then syf.calculated_field_2 else null end as na_amt						
          , syf.fund_name AS fund_name
          , syf.fund_type AS fund_type
          , CASE WHEN pcm_eab_fund_type.eab_standard_value = 'Unmapped Value' THEN 'Unknown'
				ELSE pcm_eab_fund_type.eab_standard_value
				END AS eab_fund_type

						
/********************
eab_fund_amount logic
calculated_field_1 = raw award type (A,C,O,NA,E,D)
calculated_field_2 = raw award amount
********************/
		  , CAST(case 
					when grid_merit.eab_student_id is not null then '0'
					when estgft.eab_student_id is not null then '0'
				 	when coalesce(syf.calculated_field_1,'') = 'A' 
						then syf.calculated_field_2
					when coalesce(syf.calculated_field_1,'') <> 'C' 
				 		and acceptawds.eab_student_id is not null
						then syf.calculated_field_2
					when (coalesce(syf.calculated_field_1,'') in ('A','C','NA','O','D')	and acceptawds.eab_student_id is null)
						then syf.calculated_field_2
					else '0'
					end AS decimal (10,2)) as eab_fund_amount		
/******************************************************************/

          , CAST(case when accept_dt IS NOT NULL then accept_dt when offered_dt IS NOT NULL then offered_dt when declined_dt IS NOT NULL then declined_dt else cancel_dt end AS date) AS eab_fund_date
          , CASE WHEN pcm_eab_simulation_fund_type.eab_standard_value = 'Unmapped Value' THEN 'Unknown'
				ELSE pcm_eab_simulation_fund_type.eab_standard_value 
				END AS eab_simulation_fund_type
          , CASE WHEN pcm_eab_cohort_fund_type.eab_standard_value = 'Unmapped Value' THEN 'Unknown'
				ELSE pcm_eab_cohort_fund_type.eab_standard_value 
				END AS eab_cohort_fund_type
          , CASE WHEN pcm_eab_summary_fund_type.eab_standard_value = 'Unmapped Value' THEN 'Unknown'
				ELSE pcm_eab_summary_fund_type.eab_standard_value
				END AS eab_summary_fund_type
          , CASE WHEN pcm_eab_discount_ind.eab_standard_value = 'Unmapped Value' THEN 'Unknown'
				ELSE CAST(pcm_eab_discount_ind.eab_standard_value AS Boolean)
				END AS eab_discount_ind
          , CAST(syf.intake_dt AS timestamp) AS intake_dt
        FROM <[Enroll360].[Student Year Funds].{fulfillment}> AS syf   
											   
        LEFT OUTER JOIN <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_eab_fund_type
                     ON (pcm_eab_fund_type.field_name = 'eab_fund_type'
                    AND COALESCE(syf.fund_type,'') = COALESCE(pcm_eab_fund_type.partner_value,''))
        LEFT OUTER JOIN <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_eab_simulation_fund_type
                     ON (pcm_eab_simulation_fund_type.field_name = 'eab_simulation_fund_type'
                    AND COALESCE(syf.fund_type,'') = COALESCE(pcm_eab_simulation_fund_type.partner_value,''))
        LEFT OUTER JOIN <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_eab_cohort_fund_type
                     ON (pcm_eab_cohort_fund_type.field_name = 'eab_cohort_fund_type'
                    AND COALESCE(syf.fund_type,'') = COALESCE(pcm_eab_cohort_fund_type.partner_value,''))
        LEFT OUTER JOIN <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_eab_summary_fund_type
                     ON (pcm_eab_summary_fund_type.field_name = 'eab_summary_fund_type'
                    AND COALESCE(syf.fund_type,'') = COALESCE(pcm_eab_summary_fund_type.partner_value,''))
        LEFT OUTER JOIN <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_eab_discount_ind
                     ON (pcm_eab_discount_ind.field_name = 'eab_discount_ind'
                    AND COALESCE(syf.fund_type,'') = COALESCE(pcm_eab_discount_ind.partner_value,''))
		left outer join <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_entry_year_academic
					on (pcm_entry_year_academic.field_name = 'eab_entry_year_academic'
					and coalesce(syf.current_entry_year_academic,'') = coalesce(pcm_entry_year_academic.partner_value,''))

/*****************************************************
syf_join_extension bundle code - join to acceptawds table
*****************************************************/
/*Student Year Funds - partner specific*/
LEFT JOIN ( 
	SELECT distinct eab_student_id, eab_student_id_type, listagg(eab_fund_type,'|') as eab_fund_type from
		(select distinct eab_student_id, eab_student_id_type, pcm_eab_fund_type.eab_standard_value as eab_fund_type
  		from <[Enroll360].[Student Year Funds].{fulfillment}> AS syf
		LEFT OUTER JOIN <[Enroll360].[Partner Code Mapping].{convert_sis}> pcm_eab_fund_type
		ON (pcm_eab_fund_type.field_name = 'eab_fund_type'
		AND COALESCE(syf.fund_type,'') = COALESCE(pcm_eab_fund_type.partner_value,''))
		where 1=1
		and fund_type not in ('8812FS', '8701FS')
		and calculated_field_1 = 'A'
		) syf
	where 1=1
	GROUP BY eab_student_id, eab_student_id_type
  	
  	) acceptawds
on (syf.eab_student_id = acceptawds.eab_student_id
and syf.eab_student_id_type = acceptawds.eab_student_id_type)

		 
/*Grid Merit - awards that should not be included in counts*/
LEFT JOIN ( 
	select * from (
SELECT 
    distinct eab_student_id, eab_student_id_type, fund_code, calculated_field_1, calculated_field_2
  	, row_number() over (partition by eab_student_id order by cast(calculated_field_2 as number) desc) as rn
	from <[Enroll360].[Student Year Funds].{fulfillment}> AS syf
	where 1=1
	and fund_type in ('0846FS', '0858FS', '0859FS', '7008FS', '7009FS', '7010FS', '7011FS',
					  '8150FS','8151FS', '8152FS', '8153FS','8158FS','8548FS')
order by eab_student_id, rn )agg
where 1=1
and rn > 1
) grid_merit
on (syf.eab_student_id = grid_merit.eab_student_id
and syf.eab_student_id_type = grid_merit.eab_student_id_type
and syf.fund_code = grid_merit.fund_code
and syf.calculated_field_1 = grid_merit.calculated_field_1
and syf.calculated_field_2 =  grid_merit.calculated_field_2)
		 
		 
/*If student has both fund code 7000FS and ESTGFT, exclude ESTGFT and only include fund code 7000FS
selects ESTGFTs which should be excluded*/
LEFT JOIN ( 
	select * from (
SELECT 
    distinct eab_student_id, eab_student_id_type, fund_code, calculated_field_1, calculated_field_2
  	, row_number() over (partition by eab_student_id order by fund_type) as rn
	from <[Enroll360].[Student Year Funds].{fulfillment}> AS syf
	where 1=1
	and fund_type in ('7000FS', 'ESTGFT')
order by eab_student_id, rn )agg
where 1=1
and rn > 1
) estgft
on (syf.eab_student_id = estgft.eab_student_id
and syf.eab_student_id_type = estgft.eab_student_id_type
and syf.fund_code = estgft.fund_code
and syf.calculated_field_1 = estgft.calculated_field_1
and syf.calculated_field_2 =  estgft.calculated_field_2)
/*****************************************************/
		

    ) sq
    WHERE COALESCE(sq.eab_student_id,'')<>''
      AND COALESCE(sq.eab_student_id_type,'')<>''
      AND COALESCE(sq.eab_fund_type,'')<>''
)
/**********************************************************************************************
above cte produces funds_data table which replicates <[Enroll360].[Student Year Funds].{final}>
query below to pull final table data as desired
**********************************************************************************************/

/*Total Funds per EAB_Fund_Type rollup
comment out 125-162 to run other query*/
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
, sum(estimated_AMT) as total_est
, sum(na_AMT) as total_na
													  
from funds_data syf

join  <[Enroll360].[Student Term SIS].{final}> stud_term
on (syf.eab_student_id = stud_term.eab_student_id
   )


left outer join <[Enroll360].[Student Term Application SIS].{final}> stud_term_app
	on (stud_term.eab_student_id = stud_term_app.eab_student_id
	and coalesce(stud_term.eab_student_type,'') = 'Freshman'
	and coalesce(stud_term.eab_entry_term,'') = coalesce(stud_term_app.eab_entry_term,'')
	and coalesce(stud_term.eab_entry_year_academic,0) = coalesce(stud_term_app.eab_entry_year_academic,0))
													  
where 1=1
and stud_term.eab_student_type = 'Freshman'
and eab_fund_type  in ('Insitutional Award')


group by syf.EAB_FUND_TYPE, syf.EAB_ENTRY_YEAR_ACADEMIC
order by syf.EAB_ENTRY_YEAR_ACADEMIC desc, syf.eab_fund_type

/*Student Level data       ***************************
comment in this query and comment out above Total Funds per EAB_Fund_Type rollup query to run this query*/


/*select 
syf.eab_student_id, EAB_FUND_TYPE, syf.EAB_ENTRY_YEAR_ACADEMIC
, stud_term.eab_student_type, fund_code, stud_term_app.eab_admit_ind, stud_term_app.eab_deposit_ind, stud_term.eab_current_funnel_status,acc_eab_fund_type
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
, sum(estimated_AMT) as total_est
, sum(na_AMT) as total_na
													  
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
and eab_fund_type  = 'Insitutional Award'
--and acc_eab_fund_type ilike '%Tuition Waiver%'

group by syf.EAB_FUND_TYPE, syf.EAB_ENTRY_YEAR_ACADEMIC
, stud_term.eab_student_type, fund_code
, syf.eab_student_id, acc_eab_fund_type, stud_term_app.eab_admit_ind, stud_term_app.eab_deposit_ind
, stud_term.eab_current_funnel_status
order by syf.EAB_ENTRY_YEAR_ACADEMIC desc, syf.eab_student_id, syf.eab_fund_type
, stud_term.eab_student_type, syf.EAB_FUND_TYPE, stud_term.eab_current_funnel_status--, syf.eab_student_id
, fund_code

limit 5000	*/													 
/***********************************************************************/