SELECT src_donor_id,
	  case 
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Future Donor') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Future Donor' 
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Student') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Student'
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Student Masters') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Student Masters'
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Spouse of Trustee') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Spouse of Trustee'
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Charitable Giving Fund') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Charitable Giving Fund'
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('College') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'College'
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Corporate Foundation') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Corporate Foundation'
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Corporation') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Corporation'
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Family Foundation/Trust') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Family Foundation/Trust'
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Federal or State Grant') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Federal or State Grant'
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Fundraising Consortium') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Fundraising Consortium'
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Government Agency') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Government Agency'
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Matching Gift Company') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Matching Gift Company'
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Member Organization') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Member Organization'
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Non Profit Organization') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Non Profit Organization'
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Religious Organization') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Religious Organization'
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('School') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'School'
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Sister of Mercy') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Sister of Mercy'
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Sister Other') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Sister Other'
	  else
	  case
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Alumni Masters') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Alumni Masters' 
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Alumni') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Alumni' 
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Parent') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Parent' 
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Parent of Alumni') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Parent of Alumni' 
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Spouse of Alumni') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Spouse of Alumni' 
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Emeritus Faculty') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Emeritus Faculty' 
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Former Trustee') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Former Trustee' 
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Employee') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Employee' 
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Former Employee') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Former Employee' 
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Spouse of Emeritus Trustee') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Spouse of Emeritus Trustee' 
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Spouse of Former Trustee') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Spouse of Former Trustee' 
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Spouse of Employee') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Spouse of Employee' 
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Spouse of Former Employee') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Spouse of Former Employee' 
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Former Student') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Former Student' 
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Heritage Club') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Heritage Club'
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Athletic Supporter') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Athletic Supporter'
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Aramark') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Aramark'
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Distant Neighbor') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Distant Neighbor'
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Child of Alumni') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Child of Alumni'
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Donor') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Donor'
	  when (('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) 
	  AND ('Friend') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)) THEN 'Friend'
	  else constituent_code end end as donor_category_desc
FROM <[AMS].[donor_source]> as d