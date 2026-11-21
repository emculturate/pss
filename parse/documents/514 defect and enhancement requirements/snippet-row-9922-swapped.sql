SELECT src_donor_id,
	  case 
	  when (('Future Donor') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Future Donor' 
	  when (('Student') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Student'
	  when (('Student Masters') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Student Masters'
	  when (('Spouse of Trustee') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Spouse of Trustee'
	  when (('Charitable Giving Fund') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Charitable Giving Fund'
	  when (('College') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'College'
	  when (('Corporate Foundation') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Corporate Foundation'
	  when (('Corporation') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Corporation'
	  when (('Family Foundation/Trust') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Family Foundation/Trust'
	  when (('Federal or State Grant') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Federal or State Grant'
	  when (('Fundraising Consortium') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Fundraising Consortium'
	  when (('Government Agency') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Government Agency'
	  when (('Matching Gift Company') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Matching Gift Company'
	  when (('Member Organization') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Member Organization'
	  when (('Non Profit Organization') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Non Profit Organization'
	  when (('Religious Organization') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Religious Organization'
	  when (('School') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'School'
	  when (('Sister of Mercy') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Sister of Mercy'
	  when (('Sister Other') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Sister Other'
	  else
	  case
	  when (('Alumni Masters') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Alumni Masters' 
	  when (('Alumni') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Alumni' 
	  when (('Parent') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Parent' 
	  when (('Parent of Alumni') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Parent of Alumni' 
	  when (('Spouse of Alumni') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Spouse of Alumni' 
	  when (('Emeritus Faculty') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Emeritus Faculty' 
	  when (('Former Trustee') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Former Trustee' 
	  when (('Employee') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Employee' 
	  when (('Former Employee') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Former Employee' 
	  when (('Spouse of Emeritus Trustee') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Spouse of Emeritus Trustee' 
	  when (('Spouse of Former Trustee') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Spouse of Former Trustee' 
	  when (('Spouse of Employee') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Spouse of Employee' 
	  when (('Spouse of Former Employee') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Spouse of Former Employee' 
	  when (('Former Student') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Former Student' 
	  when (('Heritage Club') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Heritage Club'
	  when (('Athletic Supporter') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Athletic Supporter'
	  when (('Aramark') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Aramark'
	  when (('Distant Neighbor') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Distant Neighbor'
	  when (('Child of Alumni') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Child of Alumni'
	  when (('Donor') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Donor'
	  when (('Friend') IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9)
      AND ('') IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9)) THEN 'Friend'
	  else constituent_code end end as donor_category_desc
FROM <[AMS].[donor_source]> as d