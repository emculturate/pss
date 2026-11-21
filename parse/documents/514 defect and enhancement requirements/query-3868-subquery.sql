select partnercontact_id,
       partner_system_name,
       eab_entry_year_academic,
       sum(agg_fund_amount) as agg_fund_amount
from (
    <fao_calc.query.total_fund_amount_per_fund_type>
) fund_agg
where eab_fund_type = 'pell_grant'
  and coalesce(imputed_flag, false) = false
group by partnercontact_id,
         partner_system_name,
         eab_entry_year_academic
