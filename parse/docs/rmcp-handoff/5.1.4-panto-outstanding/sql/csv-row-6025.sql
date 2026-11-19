-- Vaishnavi 01/07/2025 - removed test email generation for shared email
-- Navya 09/21/22 -- Adding query
---kkhanuja 10/10/2022 adding change for donor_type_primary_label
--Navya 10/11/22 - Adding status_mail_partner and status_email_partner
-- #terukula 10/21/2022 AMS-182 V0.1 Added pledge_status_partner,mailing_addressee_name and athletic_donation_status_partner columns
-- #kkhanuja 10/21/2022 AMS-175 V0.1 Added ask_partner column
--#jgogineni 11/01/2022 AMS-181 Added changes to handle invalid email
-- #terukula 11/15/2022 AMS-196 Removed household_id column
--navya 12/20 - removed eab_contact_type - need further testing
--navya 12/22 - added back eab_contact_type
--Chitti 12/30 -- added logic to generate dummy email for source_partnercontact_id's who doesn't have email
--syeravadekar removing max(case when) from email picking logic to send all emails associated witha  source_partnercontact_id ams 571
--jgogineni 03/20 adding hh_primary and source_partnercontact_id in row_number logic to pick consistent email for same emails with different source_partnercontact_ids AMS-730
--navya - 08/08/2023 - added lower() to email_type so that any case for primary and seconday matches.
--navya -10/16/2023 - AMS-1176 Incorporating intake_dt from donor email to prioritize based on the latest date
--jgogineni -10/31/2023 - AMS-1215 added coalesce to intake_dt to handle nulls
--jgogineni -11/21/2023 - AMS-1237 Adding email_intake_dt as new column from donor email
--navya - 11/22/2023 - AMS-1238 Adding rank in donor_email_CTE to generate test emails for shared email records
--navya - 06/0/2025 - AMS-2186 Adding union block to retain test emails that gets generated when no email is present for a donor, even after a valid email comes in.
--abiradar - 07/30/2025 AMS-2313 updated all joins involving source_partner_contact_id now also included source_partner_system_name as a join key respectively.
--jgogineni - 08/14/2025 AMS-2350 Updated the select statement to fetch all columns from the donor table except for the email column,Removed the join on source_partner_system_name between rsc_partner_donors and donors
--jgogineni - 08/21/2025 added coalesce in select statement to avoid nulls in source_partner_system_name



/*"CTE for calculating count of distinct intake dates for each src donor id.
  This is later used to calculate active emails specifically when we get a null primary email and a valid secondary email"*/
WITH count_intake_dt_cte as
 (SELECT source_partnercontact_id,source_partner_system_name,count(distinct coalesce(intake_dt,current_date())) as cnt_intake_dt
  FROM <[PDP_AMS].[donor_email].{final}> b
    GROUP BY source_partnercontact_id,source_partner_system_name),
 donor_email_CTE as
(
  SELECT sq_email.source_partnercontact_id,
  sq_email.email,
/* below logic used to calculate row no based on latest intake_dt when multiple emails present for same donor id.
  This is used later in the query to set email_optout_ind to true for older emails */
  ROW_NUMBER() OVER(PARTITION BY sq_email.source_partnercontact_id,sq_email.source_partner_system_name ORDER BY sq_email.intake_dt desc nulls last,
                    IFF(cnt_intake_dt=1 and email_type = 'primary' and RIGHT(sq_email.email, 13) = '@test.eab.com',2,1),
                    sq_email.email_type asc,
					IFF(RIGHT(email, 4) ='.com' and RIGHT(email, 13) != '@test.eab.com',1,2), email asc) as RN_latest_email,

/* below logic used to filter out records when same email comes in for multiple donors */
  ROW_NUMBER() OVER(PARTITION BY sq_email.email,sq_email.source_partnercontact_id,sq_email.source_partner_system_name ORDER BY sq_email.intake_dt desc nulls last, sq_email.email_type asc) as RN,
sq_email.email_type,
sq_email.intake_dt as email_intake_dt,
sq_email.raw_email,
sq_email.source_partner_system_name
from
(
  SELECT cl_email.source_partnercontact_id,
  CASE WHEN CAST(cl_email.clean_email as VARCHAR) REGEXP '[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,3}'
			AND length(cl_email.clean_email) <= 64
			AND cl_email.clean_email <> ''
		    THEN CAST(cl_email.clean_email as VARCHAR)
            ELSE CAST(CONCAT(HASH(CAST(cl_email.source_partnercontact_id as varchar) ),'@test.eab.com') as VARCHAR(64) )
            END as email,cl_email.intake_dt,cl_email.email_type,cl_email.hh_primary,
            cl_email.cnt_intake_dt,
            cl_email.raw_email,
			cl_email.source_partner_system_name
  from
  (
	SELECT tmp_email.source_partnercontact_id,
		TRIM(REGEXP_REPLACE(REGEXP_REPLACE(SPLIT_PART(tmp_email.email, '@', 1), '(\\.){2,}', '.'),'[^a-zA-Z0-9._%+-]') ,'. ') AS p1,
  		TRIM(REGEXP_REPLACE(REGEXP_REPLACE(SPLIT_PART(tmp_email.email, '@', 2), '(\\.){2,}', '.'),'[^a-zA-Z0-9._%+-]') , '. ') AS p2,
  		CONCAT(p1,'@',p2) AS clean_email,
        tmp_email.intake_dt,
        tmp_email.email_type,
        tmp_email.hh_primary,
        tmp_email.cnt_intake_dt,
        tmp_email.raw_email,
		tmp_email.source_partner_system_name
        from  (
		select donor.source_partnercontact_id as source_partnercontact_id,
		--email.primary_email as primary_email, -- depricating this logic as we no longer use the columns primary_email and secondary_email
		--email.secondary_email as secondary_email,
        email.email as email,
        email.email as raw_email,
		email.intake_dt as intake_dt,
        email.email_type as email_type,
        donor.hh_primary as hh_primary,
		cid.cnt_intake_dt as cnt_intake_dt,
    -- added coalesce to fetch source_partner_system_name if we dont have record in donor_email
		coalesce(email.source_partner_system_name,donor.source_partner_system_name) as source_partner_system_name
	   from <[PDP_AMS].[donor].{final}> as donor
	   left join
   (SELECT source_partnercontact_id,
--case when (lower(a.email_type) ='primary') then LOWER(a.email) else NULL end as primary_email,
--case when (lower(a.email_type)='secondary') then LOWER(a.email) else NULL end as secondary_email,
lower(a.email) as email,
a.intake_dt,
a.email_type,
a.source_partner_system_name
   from <[PDP_AMS].[donor_email].{final}> as a
) email
	on donor.source_partnercontact_id=email.source_partnercontact_id
--commented as part of AMS-2367
--	and COALESCE(donor.source_partner_system_name,'')=COALESCE(email.source_partner_system_name,'')
left join count_intake_dt_cte cid 
		  on donor.source_partnercontact_id=cid.source_partnercontact_id
      --commented as part of AMS-2367
		  --  and COALESCE(donor.source_partner_system_name,'')=COALESCE(cid.source_partner_system_name,'')
	   )  as tmp_email)as cl_email
  )as sq_email
)

SELECT
CAST(
	   md5(
		 TO_VARCHAR(
		   ARRAY_CONSTRUCT(
			 'partner',
			 CAST(d.source_partnercontact_id as varchar),d_email.email, <tenant_salt>
		  )
		 )
		) as VARCHAR(50) ) as sourcecontact_id,
CAST(d.source_partnercontact_id as varchar)as source_partnercontact_id,
CAST(d.donor_category_cd as varchar) as donor_category_cd,
CAST(d.donor_category_desc as varchar) as donor_category_desc,
CAST(d.donor_status_partner as varchar) as donor_status_partner,
CAST(d.gender as varchar) as gender,
CAST(d.birth_dt as datetime) as birth_dt,
CAST(d.donor_deceased_ind as boolean) as donor_deceased_ind,
CAST(d.donor_joint_mailing_ind as boolean) as donor_joint_mailing_ind,
CAST(d.individual_salutation as varchar) as individual_salutation,
CAST(d.joint_salutation as varchar) as joint_salutation,
CAST(d_email.email as varchar) as email,
CAST(d_email.email_type as varchar) as email_type,
CAST(d.hh_primary as varchar) as hh_primary,
CAST(d.fname as varchar) as fname,
CAST(d.lname as varchar) as lname,
CAST(d.mname as varchar) as mname,
CAST(d.prefix as varchar) as prefix,
CAST(d.suffix as varchar) as suffix,
CAST(d.nickname as varchar) as nickname,
CAST(d.preferred_full_name as varchar) as preferred_full_name,
CAST(d.preferred_joint_name as varchar) as preferred_joint_name,
CAST(d.preferred_joint_name_second as varchar) as preferred_joint_name_second,
CAST(d.spouse_id as varchar) as spouse_id,
CAST(d.audience_segment_cd as varchar) as audience_segment_cd,
CAST(d.giving_society_name as varchar) as giving_society_name,
CAST(d.giving_officer_name as varchar) as giving_officer_name,
CASE WHEN RN_latest_email !=1 THEN true
ELSE CAST(d.eab_email_optout_ind as boolean) END as eab_email_optout_ind,
CAST(d.eab_mail_optout_ind as boolean) as eab_mail_optout_ind,
CAST(d.eab_solicit_optout_ind as boolean) as eab_solicit_optout_ind,
CAST(d.eab_contact_optout_ind as boolean) as eab_contact_optout_ind,
CAST(d.intake_dt as datetime) as intake_dt,
CAST('partner' as VARCHAR(50) ) as intake_type,
CAST(d.salutation_email_partner as varchar) as salutation_email_partner,
CAST(d.salutation_mail_partner as varchar) as salutation_mail_partner,
CAST(d.eab_contact_type as varchar) as donor_type_primary_label,
d.ask_partner as ask_partner,
d.pledge_status_partner  as pledge_status_partner,
d.athletic_donation_status_partner as athletic_donation_status_partner,
d.mailing_addressee_name as mailing_addressee_name,
CAST(d_email.email_intake_dt as datetime) as email_intake_dt,
d.source_partner_system_name as source_partner_system_name,
d.source_eab_system_type as source_eab_system_type
FROM <[PDP_AMS].[donor].{final}>  as d
LEFT JOIN donor_email_CTE as d_email
ON d.source_partnercontact_id = d_email.source_partnercontact_id
--commented as part of AMS-2367
--AND COALESCE(d.source_partner_system_name, '') = COALESCE(d_email.source_partner_system_name, '')
where coalesce(d.source_partnercontact_id, '') <> '' and d_email.RN = 1
																   
UNION
-- Handle Situations where a Donor has Test and Valid Email
SELECT DISTINCT
rps.sourcecontact_id,
CAST(d.source_partnercontact_id as varchar)as source_partnercontact_id,
CAST(d.donor_category_cd as varchar) as donor_category_cd,
CAST(d.donor_category_desc as varchar) as donor_category_desc,
CAST(d.donor_status_partner as varchar) as donor_status_partner,
CAST(d.gender as varchar) as gender,
CAST(d.birth_dt as datetime) as birth_dt,
CAST(d.donor_deceased_ind as boolean) as donor_deceased_ind,
CAST(d.donor_joint_mailing_ind as boolean) as donor_joint_mailing_ind,
CAST(d.individual_salutation as varchar) as individual_salutation,
CAST(d.joint_salutation as varchar) as joint_salutation,
rps.email,
rps.email_type,
CAST(d.hh_primary as varchar) as hh_primary,
CAST(d.fname as varchar) as fname,
CAST(d.lname as varchar) as lname,
CAST(d.mname as varchar) as mname,
CAST(d.prefix as varchar) as prefix,
CAST(d.suffix as varchar) as suffix,
CAST(d.nickname as varchar) as nickname,
CAST(d.preferred_full_name as varchar) as preferred_full_name,
CAST(d.preferred_joint_name as varchar) as preferred_joint_name,
CAST(d.preferred_joint_name_second as varchar) as preferred_joint_name_second,
CAST(d.spouse_id as varchar) as spouse_id,
CAST(d.audience_segment_cd as varchar) as audience_segment_cd,
CAST(d.giving_society_name as varchar) as giving_society_name,
CAST(d.giving_officer_name as varchar) as giving_officer_name,
TRUE as eab_email_optout_ind, --Older emails are not used for email marketing
CAST(d.eab_mail_optout_ind as boolean) as eab_mail_optout_ind,
CAST(d.eab_solicit_optout_ind as boolean) as eab_solicit_optout_ind,
CAST(d.eab_contact_optout_ind as boolean) as eab_contact_optout_ind,
CAST(d.intake_dt as datetime) as intake_dt,
CAST('partner' as VARCHAR(50) ) as intake_type,
CAST(d.salutation_email_partner as varchar) as salutation_email_partner,
CAST(d.salutation_mail_partner as varchar) as salutation_mail_partner,
CAST(d.eab_contact_type as varchar) as donor_type_primary_label,
d.ask_partner,
d.pledge_status_partner,
d.athletic_donation_status_partner,
d.mailing_addressee_name,
rps.email_intake_dt,
d.source_partner_system_name,
d.source_eab_system_type
FROM pdp_ams.rsc_partner_donors AS rps
LEFT JOIN <[PDP_AMS].[donor].{final}> as d
      ON d.source_partnercontact_id = rps.source_partnercontact_id
    --  AND COALESCE(d.source_partner_system_name,'') = COALESCE(rps.source_partner_system_name,'')
LEFT JOIN donor_email_CTE AS partner_contacts
       ON rps.source_partnercontact_id = partner_contacts.source_partnercontact_id
     --  AND COALESCE(rps.source_partner_system_name,'')  = COALESCE(partner_contacts.source_partner_system_name,'')
--       AND rps.email = partner_contacts.email
    WHERE rps.email ILIKE '%@test.eab.com%' -- Have previously generated a test email address
     AND partner_contacts.raw_email IS NOT NULL -- Now, have a record in donor_email with valid email
     AND NOT partner_contacts.email ILIKE '%@test.eab.com%' -- Exclude test emails generated in current run
     AND d.source_partnercontact_id IS NOT NULL -- We don't want the record to be populated if it get removed from donor
     AND partner_contacts.RN = 1