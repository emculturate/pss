select spriden_id, spriden_pidm, terms.max_term, spriden_first_name, 
spriden_last_name, spriden_mi, SGBSTDN_TERM_CODE_ADMIT 
FROM 
  (SELECT spriden_id, spriden_pidm, spriden_first_name, spriden_last_name, spriden_mi 
   FROM spriden  WHERE spriden_change_ind is null) spriden  
JOIN 
  (SELECT pidm, max(term) AS max_term 
   FROM 
     (SELECT shrtgpa_pidm AS pidm, shrtgpa_term_code AS term
      FROM shrtgpa WHERE shrtgpa_levl_code = 'UG'  
      UNION ALL 
      SELECT shrtrce_pidm AS pidm, shrtrce_term_code_eff AS term 
      FROM shrtrce WHERE shrtrce_levl_code = 'UG'
      UNION ALL 
      SELECT sfrstca_pidm AS pidm, sfrstca_term_code AS term
      FROM sfrstca
      JOIN stvterm ON stvterm_code = sfrstca_term_code  AND stvterm_end_date > SYSDATE - 365
      UNION ALL 
      SELECT sgbstdn_pidm AS pidm, sgbstdn_term_code_eff AS term
      FROM sgbstdn WHERE sgbstdn_levl_code = 'UG'
     ) x GROUP BY pidm
  ) terms   ON spriden.spriden_pidm = terms.pidm
JOIN STVTERM termDates 
  ON termDates.STVTERM_CODE = terms.max_term  
JOIN 
  (SELECT sgbstdn_pidm, MIN(SGBSTDN_TERM_CODE_ADMIT) AS SGBSTDN_TERM_CODE_ADMIT 
   from sgbstdn
   WHERE sgbstdn_levl_code = 'UG' GROUP BY sgbstdn_pidm
  ) undergradOnly 
  ON undergradOnly.sgbstdn_pidm = spriden.spriden_pidm
GROUP BY spriden_id, spriden_pidm, terms.max_term, spriden_first_name, spriden_last_name, spriden_mi, SGBSTDN_TERM_CODE_ADMIT
HAVING max(max_term) >= 201310

SELECT sfrstca_pidm AS pidm, sfrstca_term_code AS term   FROM sfrstca  JOIN stvterm ON stvterm_code = sfrstca_term_code  AND stvterm_end_date > SYSDATE - 365