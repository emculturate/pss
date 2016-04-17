package sql.walker;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Assert;
import org.junit.Test;

import sql.SQLSelectParserLexer;
import sql.SQLSelectParserParser;
import sql.SQLSelectParserParser.SqlContext;

public class SqlParseEventWalkerTest {

	@Test
	public void simpleParseTest() {

		final String cond = "SELECT aa.scbcrse_coll_code, aa.* FROM scbcrse as aa, mycrse as courses "
				+ " WHERE not aa.scbcrse_subj_code = courses.subj_code "
				+ " AND (aa.scbcrse_crse_numb = courses.crse_numb " + " or aa.scbcrse_crse_numb = courses.crse_numb) ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void aggregateParseTest() {

		final String cond = " SELECT scbcrse_subj_code as subj_code, count(*), MAX(scbcrse_eff_term) " 
		        + " FROM scbcrse "
				+ " group by scbcrse_subj_code, xxx " + " order by 2, scbcrse_subj_code, 1 ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void simpleAndOrParseTest() {

		final String cond = " SELECT scbcrse_subj_code FROM scbcrse " 
		+ " where a = b AND c=d  OR e=f and g=h ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void simpleFromListType1ParseTest() {

		final String cond = " SELECT * FROM third, fourth, fifth, sixth ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void simpleFromListType2ParseTest() {

		final String cond = " SELECT * FROM third cross join fourth union join fifth natural join sixth ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void simpleFromListType3ParseTest() {

		final String cond = " SELECT * FROM third cross join fourth "
				+ " union join fifth natural join sixth natural inner join seventh";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void simpleFromListType4ParseTest() {

		final String cond = " SELECT * FROM third join fourth on a = b " 
		+ " left outer join fifth on b = d ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void simpleMultipleUnionParseTest() {

		final String cond = " SELECT first FROM third " 
		+ " union select third from fifth "
				+ " union select fourth from sixth " 
		+ " union select seventh from eighth ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void simpleMultipleIntersectParseTest() {

		final String cond = " SELECT first FROM third " 
		+ " intersect select third from fifth "
				+ " intersect select fourth from sixth " 
		+ " intersect select seventh from eighth ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void simpleUnionIntersectParseTest() {

		final String cond = " SELECT first FROM third " 
		+ " union select third from fifth "
				+ " intersect select fourth from sixth " 
		+ " union select seventh from eighth ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void nestedUnionIntersectAAParseTest() {

		final String cond = " SELECT first FROM ( " 
		        + "  select third from fifth "
				+ " intersect select fourth from sixth ) aa " 
		        + " union select seventh from eighth ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void nestedUnionIntersectParseTest() {

		final String cond = " SELECT first FROM ( " 
		        + "  select third from fifth "
				+ " intersect select fourth from sixth ) " 
		        + " union select seventh from eighth ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void subqueryParseTest() {

		final String cond = "SELECT aa.scbcrse_coll_code FROM scbcrse aa "
				+ " WHERE aa.scbcrse_subj_code = courses.subj_code " 
				+ " AND aa.scbcrse_crse_numb = courses.crse_numb "
				+ " AND aa.scbcrse_eff_term = ( " 
				+ " SELECT MAX(scbcrse_eff_term) " 
				+ " FROM scbcrse "
				+ " WHERE scbcrse_subj_code = courses.subj_code " 
				+ " AND scbcrse_crse_numb = courses.crse_numb "
				+ " AND scbcrse_eff_term <= courses.term) ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void arithmeticParseTest() {

		final String cond = "SELECT -(aa.scbcrse_coll_code * 6 - other) FROM scbcrse aa "
				+ " WHERE aa.scbcrse_subj_code is not null ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void stringFunctionParseTest() {

		final String cond = "SELECT trim(leading '0' from field1), a || b, "
				+ " trim('0' || field2,'0') "
				+" FROM scbcrse aa "
				+ " WHERE subj_code in ('AA', 'BB') ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void inPredicateFunctionParseTest() {

		final String cond = "SELECT * "
				+" FROM scbcrse aa "
				+ " WHERE subj_code in ('AA', 'BB') "+" and item in (select * from other)";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void caseStatementParseTest() {

		final String cond = " SELECT CASE WHEN true THEN 'Y'	 " 
				+ " ELSE 'N' END as case_one, " +
				" CASE  col WHEN 'a' THEN 'b'	 " 
				+ " ELSE null END as case_two "
				+ " FROM sgbstdn ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void selectItemSubqueryStatementParseTest() {

		final String cond = " SELECT first_item,( "
			+ " SELECT item " 
			+ " FROM sgbstdn "
			+ " WHERE sgbstdn_levl_code = 'US' " 
			+ " ) AS INTERNATIONAL_IND "
				+ " FROM sgbstdn ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void nestedSymbolTableConstructionTest() {

		final String cond = " SELECT b.att1, b.att2 "
			+ " from (SELECT a.col1 as att1, a.col2 as att2 " 
			+ " FROM tab1 as a"
			+ " WHERE a.col1 <> a.col3 " 
			+ " ) AS b ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void biggerQueryParseTest() {

		final String cond = " select spriden_id, spriden_pidm, terms.max_term, spriden_first_name, spriden_last_name, "
				+ "spriden_mi, TERM_CODE_ADMIT FROM ( "
				+ " SELECT spriden_id, spriden_pidm, spriden_first_name, spriden_last_name, spriden_mi FROM spriden "
				+ " WHERE spriden_change_ind is null) spriden " + " JOIN (  SELECT pidm, max(term) AS max_term FROM ( "
				+ " SELECT shrtgpa_pidm AS pidm, shrtgpa_term_code AS term  "
				+ " FROM shrtgpa WHERE shrtgpa_levl_code = 'UG' "
				+ " UNION ALL SELECT shrtrce_pidm AS pidm, shrtrce_term_code_eff AS term FROM shrtrce WHERE shrtrce_levl_code = 'UG' "
				+ " UNION ALL SELECT sfrstca_pidm AS pidm, sfrstca_term_code AS term   FROM sfrstca "
				+ " JOIN stvterm ON stvterm_code = sfrstca_term_code " + " AND stvterm_end_date > SYSDATE - 365  "
				+ " UNION ALL SELECT sgbstdn_pidm AS pidm, sgbstdn_term_code_eff AS term "
				+ " FROM sgbstdn WHERE sgbstdn_levl_code = 'UG'  ) x GROUP BY pidm "
				+ " ) terms ON spriden.spriden_pidm = terms.pidm "
				+ " JOIN STVTERM termDates ON termDates.STVTERM_CODE = terms.max_term "
				+ " JOIN (SELECT sgbstdn_pidm, MIN(SGBSTDN_TERM_CODE_ADMIT) AS TERM_CODE_ADMIT from sgbstdn "
				+ " WHERE sgbstdn_levl_code = 'UG' GROUP BY sgbstdn_pidm "
				+ " ) undergradOnly ON undergradOnly.sgbstdn_pidm = spriden.spriden_pidm "
				+ " GROUP BY spriden_id, spriden_pidm, terms.max_term, spriden_first_name, spriden_last_name, spriden_mi, TERM_CODE_ADMIT "
				+ " HAVING max(max_term) >= 201310 ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void largeStudentgeneralQueryParseTest() {

	final String query = "SELECT " + " population.spriden_id AS STUDENT_ID "
			+ " , population.spriden_first_name AS FIRST_NAME " 
			+ " , population.spriden_mi AS MIDDLE_INITIAL "
			+ " , population.spriden_last_name AS LAST_NAME "
			+ " , TO_CHAR(demographic.spbpers_birth_date, 'yyyymmdd') AS DATE_OF_BIRTH "
			+ " , NVL(demographic.spbpers_sex,'') AS GENDER " 
			+ " , NVL(f.goremal_email_address,'') AS EMAIL_ID "
			+ " , NVL(demographic.spbpers_ethn_code,'') AS ETHNICITY_CD "
			+ " , NVL(demographic.spbpers_dead_ind,'') AS DECEASED_IND " 
			+ " , '' AS Field10 " 
			+ " , ( "
			+ " SELECT CASE WHEN sgbstdn.sgbstdn_resd_code in ('G') THEN 'Y'	 " 
			+ " ELSE 'N' END " 
			+ " FROM sgbstdn "
			+ " JOIN ( " 
			+ " SELECT sgbstdn_pidm, max(sgbstdn_term_code_eff) AS max_term " 
			+ " FROM sgbstdn "
			+ " WHERE sgbstdn_levl_code = 'US' " 
			+ " GROUP BY sgbstdn_pidm "
			+ " ) m on sgbstdn.sgbstdn_pidm = m.sgbstdn_pidm and sgbstdn.sgbstdn_term_code_eff = m.max_term "
			+ " WHERE sgbstdn.sgbstdn_pidm = population.spriden_pidm " 
			+ " 		) AS INTERNATIONAL_IND "
			+ " , NVL(GOBINTL.GOBINTL_NATN_CODE_LEGAL,'') AS COUNTRY_CD " 
			+ " , '' AS INST_FIRST_TERM_ID "
			+ " , hs.stvsbgi_desc AS HS_NAME " 
			+ " , sobsbgi.sobsbgi_city AS HS_CITY "
			+ " , sobsbgi.sobsbgi_stat_code AS HS_STATE " 
			+ " , hs.sorhsch_class_size AS HS_SIZE "
			+ " , hs.sorhsch_percentile AS HS_PERCENTILE " 
			+ " , hs.sorhsch_class_rank AS HS_RANK "
			+ " , hs.sorhsch_gpa AS HS_GPA " 
			+ " , '' AS Field21 "
			+ " , hp.sprtele_phone_area || hp.sprtele_phone_number AS HOME_PHONE " 
			+ " , '' AS Field23 "
			+ " , cp.sprtele_phone_area || cp.sprtele_phone_number AS MOBILE_PHONE "
			+ " , address.spraddr_street_line1 AS MAIL_ADDRESS1 " 
			+ " , address.spraddr_street_line2 AS MAIL_ADDRESS2 "
			+ " , address.spraddr_city AS MAIL_CITY " 
			+ " , address.spraddr_stat_code AS MAIL_STATE "
			+ " , address.spraddr_zip AS MAIL_ZIP_CODE " 
			+ " , '' AS Field30 " + " , '' AS Field31 "
			+ " , demographic.SPBPERS_LGCY_CODE AS STUDENT_LEGACY_CD " 
			+ " , ( " + " SELECT SGBSTDN_ADMT_CODE "
			+ " FROM sgbstdn " 
			+ " WHERE sgbstdn_pidm = population.spriden_pidm "
			+ " AND sgbstdn_levl_code = 'US'			 "
			// --MODIFY PER MEMBER
			+ " AND sgbstdn_term_code_eff = ( " 
			+ " SELECT max(sgbstdn_term_code_eff) " 
			+ " FROM sgbstdn "
			+ " WHERE sgbstdn_pidm = population.spriden_pidm " 
			+ " AND sgbstdn_levl_code = 'US')		 "
			// --MODIFY PER MEMBER
			+ " 		) AS STUDENT_ADMIT_CD "
			+ " , CASE WHEN shrtrit_primary.shrtrit_sbgi_code is not null THEN 'Y' ELSE 'N' END AS TRANSFER_STUDENT_IND "
			+ " , shrtrit_primary.shrtrit_sbgi_code AS TRANSFER_INST_CD "
			+ " , NVL(demographic.SPBPERS_VERA_IND,'N') as VETERAN_IND "
			+ " , CASE WHEN readmit.saradap_pidm IS NULL THEN 'N' ELSE 'Y' END as READMIT_IND "
			+ " , CASE WHEN RCRAPP3_1.pidm IS NOT NULL THEN 'Y' ELSE 'N' END AS FIRST_GEN_IND  "
			+ " , sobsbgi.SOBSBGI_ZIP AS HS_ZIP_CODE " 
			+ " , '' AS ADMISSION_ZIP_CODE " + " , '' AS REGION_CD "
			+ " , ( " + " SELECT CASE WHEN SGBSTDN_STST_CODE = 'AS' THEN 'Y' ELSE 'N' END "
			// --MODIFY PER MEMBER
			+ " FROM sgbstdn " 
			+ " WHERE sgbstdn_pidm = population.spriden_pidm "
			+ " AND sgbstdn_levl_code = 'US'			 "
			// --MODIFY PER MEMBER
			+ " AND sgbstdn_term_code_eff = ( " 
			+ " SELECT max(sgbstdn_term_code_eff) " 
			+ " FROM sgbstdn "
			+ " WHERE sgbstdn_pidm = population.spriden_pidm " 
			+ " 	AND sgbstdn_levl_code = 'US')		 "
			// --MODIFY PER MEMBER
			+ " ) AS ACTIVE_IND "

			+ " FROM  "
			// --STUDENT POPULATION
			+ " ( " 
			+ " SELECT "
			+ " spriden_id, spriden_pidm, terms.max_term, spriden_first_name, spriden_last_name, spriden_mi "
			+ " FROM ( "
			+ " SELECT spriden_id, spriden_pidm, spriden_first_name, spriden_last_name, spriden_mi FROM spriden WHERE spriden_change_ind is null "
			+ " ) spriden " 
			+ " JOIN ( " 
			+ " SELECT pidm, max(term) AS max_term " 
			+ " FROM ( "
			+ " SELECT shrtgpa_pidm AS pidm, shrtgpa_term_code AS term "
			+ "	FROM shrtgpa WHERE shrtgpa_levl_code = 'US'	 "
			// -- MODIFY PER MEMBER
			+ " UNION ALL SELECT shrtrce_pidm AS pidm, shrtrce_term_code_eff AS term "
			+ " FROM shrtrce WHERE shrtrce_levl_code = 'US'	 "
			// -- MODIFY PER MEMBER
			+ " UNION ALL SELECT sfrstcr_pidm AS pidm, sfrstcr_term_code AS term "
			+ " FROM sfrstcr "
			+ " JOIN stvterm ON stvterm_code = sfrstcr_term_code " 
			+ " WHERE sfrstcr_levl_code = 'US'		 "
			//			--	MODIFY PER MEMBER
			+ " AND stvterm_end_date > SYSDATE - 365		 "
			// --INSTATED TO REDUCE RUN TIME
			+ " UNION ALL SELECT sgbstdn_pidm AS pidm, sgbstdn_term_code_eff AS term 	FROM sgbstdn WHERE sgbstdn_levl_code = 'US'	 "
			// -- MODIFY PER MEMBER
			+ " ) x "
			+ " GROUP BY pidm "
			+" ) terms ON spriden.spriden_pidm = terms.pidm "
	/*
	 * JOIN ( REMOVED 2015 01 20 CHECK SF TICKET FOR FULL DETAILS -- NEW LEVL
	 * CODE LOGIC IN ST SHOULD FILTER THESE STUDENTS SELECT sgbstdn_pidm FROM
	 * sgbstdn a WHERE sgbstdn_levl_code = 'US' AND sgbstdn_term_code_eff = (
	 * SELECT MAX(sgbstdn_term_code_eff) FROM sgbstdn WHERE sgbstdn_pidm =
	 * a.sgbstdn_pidm ) ) UGRD ON spriden.spriden_pidm = ugrd.sgbstdn_pidm
	 */
	 		+ " JOIN STVTERM termDates ON termDates.STVTERM_CODE = terms.max_term "
	 		+ " GROUP BY spriden_id, spriden_pidm, terms.max_term, spriden_first_name, spriden_last_name, spriden_mi "
	// --DEVELOPMENT FILTER
	// --HAVING max(max_term) >= 199808 --ADJUST THIS LINE BY LOOKING UP THE
	// CURRENT TERM
	// --HISTORICAL FILTER
	// --HAVING max(termDates.STVTERM_START_DATE) > SYSDATE - 3650 --ADJUST IF
	// WANT DIFFERENT THAN 10 YEARS
	// --DAILY FILTER
			+ " HAVING max(termDates.STVTERM_START_DATE) > SYSDATE - 730 "
	// --ADJUST IF WANT DIFFERENT THAN 2 YEARS
			+ " ) population "
	// --DEMOGRAPHIC INFORMATION
			+ " LEFT OUTER JOIN spbpers demographic "
			+" ON population.spriden_pidm = demographic.spbpers_pidm "
	// --ADDRESS
	+" LEFT OUTER JOIN ( "
	+" SELECT spraddr.spraddr_pidm, spraddr.spraddr_street_line1, spraddr.spraddr_street_line2, spraddr_city, spraddr_stat_code, spraddr_zip "
	+" FROM spraddr "
	+" JOIN ( "
	+" SELECT spraddr_pidm, max(spraddr_seqno) AS max_seqno "
	+" FROM spraddr "
	+" WHERE spraddr_atyp_code = 'MA'							 "
	// --MODIFY PER MEMBER
	+" AND spraddr_status_ind is null "
	+" GROUP BY spraddr_pidm "
	+" ) addr_max ON spraddr.spraddr_pidm = addr_max.spraddr_pidm AND spraddr.spraddr_seqno = addr_max.max_seqno "
	+" WHERE spraddr.spraddr_atyp_code = 'MA' "
	// --MODIFY PER MEMBER
	+" AND spraddr.spraddr_status_ind is null "
	+" AND spraddr_FROM_date <= sysdate "
	+" AND (spraddr_to_date >= sysdate or spraddr_to_date is null) "
	+" ) address ON population.spriden_pidm = address.spraddr_pidm "
	// --HOME PHONE
	+" LEFT OUTER JOIN ( "
	+" SELECT sprtele.sprtele_pidm, sprtele.sprtele_phone_area, sprtele.sprtele_phone_number "
	+" FROM sprtele "
	+" JOIN ( "
	+" SELECT sprtele_pidm, max(sprtele_seqno) AS max_seqno "
	+" FROM sprtele  "
	+" WHERE sprtele_tele_code = 'MA' 	 "
	// --MODIFY PER MEMBER
	+" AND sprtele_status_ind is null "
	+" GROUP BY sprtele_pidm "
	+" ) tele_max ON sprtele.sprtele_pidm = tele_max.sprtele_pidm AND sprtele.sprtele_seqno = tele_max.max_seqno "
	+" WHERE sprtele.sprtele_tele_code = 'MA'  "
	// --MODIFY PER MEMBER
	+" AND sprtele.sprtele_status_ind is null "
	+" ) hp ON population.spriden_pidm = hp.sprtele_pidm "

	// --MOBILE PHONE
	+" LEFT OUTER JOIN ( "
	+" SELECT sprtele.sprtele_pidm, sprtele.sprtele_phone_area, sprtele.sprtele_phone_number "
	+" FROM sprtele "+" JOIN ( "
	+" SELECT sprtele_pidm, max(sprtele_seqno) as max_seqno "
	+" FROM sprtele  "
	+" WHERE sprtele_tele_code = 'CP'  "
	// --MODIFY PER MEMBER
	+" AND sprtele_status_ind is null "
	+" GROUP BY sprtele_pidm "
	+" ) tele_max ON sprtele.sprtele_pidm = tele_max.sprtele_pidm AND sprtele.sprtele_seqno = tele_max.max_seqno "
	+" WHERE sprtele.sprtele_tele_code = 'CP'  "
	// --MODIFY PER MEMBER
	+" AND sprtele.sprtele_status_ind is null "
	+" ) cp ON population.spriden_pidm = cp.sprtele_pidm "
	// --EMAIL
	+" LEFT OUTER JOIN goremal f  "
	+" ON population.spriden_pidm = f.goremal_pidm "
	+" 	AND f.goremal_emal_code = 'GSU' AND f.goremal_status_ind = 'A' AND f.goremal_preferred_ind = 'Y' "
	// --MODIFY PER MEMBER
	/*
	 * LEFT OUTER JOIN ( SELECT SGBSTDN.SGBSTDN_pidm, SGBSTDN.SGBSTDN_ADMT_CODE,
	 * SGBSTDN.SGBSTDN_STST_CODE FROM SGBSTDN WHERE sgbstdn_levl_code in ('US')
	 * --MODIFY PER MEMBER ) m on population.spriden_pidm = m.sgbstdn_pidm
	 */
	// --LEGACY
	+" LEFT OUTER JOIN stvlgcy leg "
	+" ON demographic.spbpers_lgcy_code = leg.stvlgcy_code "
	// --HIGH SCHOOL
	+" LEFT OUTER JOIN ( "
	+" SELECT sorhsch.sorhsch_pidm, sorhsch_gpa, sorhsch_class_rank, sorhsch_percentile, sorhsch_class_size, stvsbgi.stvsbgi_desc, sorhsch_sbgi_code "
	+" FROM sorhsch "
	+" JOIN ( "
	+" SELECT sorhsch_pidm, max(sorhsch_activity_date) AS max_date "
	+" FROM sorhsch "
	+" GROUP BY sorhsch_pidm "
	+" ) crit ON sorhsch.sorhsch_pidm = crit.sorhsch_pidm AND sorhsch.sorhsch_activity_date = crit.max_date "
	+" JOIN stvsbgi on sorhsch.sorhsch_sbgi_code = stvsbgi.stvsbgi_code "
	+" ) hs ON population.spriden_pidm = hs.sorhsch_pidm "
	// --HIGH SCHOOL PT2
	+" LEFT OUTER JOIN sobsbgi  "
	+" ON hs.sorhsch_sbgi_code = sobsbgi.sobsbgi_sbgi_code "
	// --COUNTRY CODE
	+" LEFT OUTER JOIN gobintl  "
	+" 	ON gobintl.gobintl_pidm = population.spriden_pidm "
	// --TRANSFER INSTITUTION
	+" LEFT OUTER JOIN ( "
	+" SELECT * "
	+" FROM shrtrit "
	+" JOIN ( "
	+" SELECT a.shrtrit_pidm AS pidm, max(a.shrtrit_seq_no) AS max_date "
	+" FROM shrtrit a "
	+" GROUP BY a.shrtrit_pidm "
	+" ) shrtrit_max ON shrtrit.shrtrit_pidm = shrtrit_max.pidm AND shrtrit.shrtrit_seq_no = shrtrit_max.max_date "
	+" ) shrtrit_primary ON shrtrit_primary.pidm = population.spriden_pidm "
	// --FIRST GEN INDICATOR
	+" LEFT JOIN ( "
	+" SELECT DISTINCT rcrapp3.rcrapp3_pidm as pidm "
	+" FROM rcrapp3 "
	+" JOIN ( "
	+" SELECT a.rcrapp3_pidm, max(a.rcrapp3_aidy_code) as max_aidy "
	+" FROM rcrapp3 a "
	+" GROUP BY a.rcrapp3_pidm, a.rcrapp3_seq_no "
	+" ) rcrapp3_max on rcrapp3.rcrapp3_pidm = rcrapp3_max.rcrapp3_pidm AND rcrapp3.rcrapp3_aidy_code = rcrapp3_max.max_aidy "
	+" WHERE rcrapp3.rcrapp3_seq_no = '1' AND (rcrapp3.RCRAPP3_FATHER_HI_GRADE IN ('1','2') OR rcrapp3.RCRAPP3_MOTHER_HI_GRADE IN ('1','2')) "
	+" ) RCRAPP3_1 ON population.spriden_pidm = RCRAPP3_1.pidm "
	// READMIT INDICATOR
	+" LEFT OUTER JOIN ( "
	+" SELECT DISTINCT SARADAP_PIDM "
	+" FROM SARADAP  "
	+" WHERE SARADAP_ADMT_CODE = 'RE' AND SARADAP_LEVL_CODE = 'US'  "
	// --MODIFY PER MEMBER
	+" ) readmit ON readmit.SARADAP_PIDM = population.spriden_pidm "
	+" WHERE 1=1 "
	+" ORDER BY 1";
	
	final SQLSelectParserParser parser = parse(query);
	runParsertest(query, parser);
}


	@Test
	public void simpleInsertFromQueryTest() {

		final String cond = " insert into sch.subj.tbl (newcol1, newcol2) values (SELECT b.att1, b.att2 "
			+ " from (SELECT a.col1 as att1, a.col2 as att2 " 
			+ " FROM sch.subj.tab1 as a"
			+ " WHERE a.col1 <> a.col3 " 
			+ " ) AS b )";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	/*
	 * UPDATE weather SET (temp_lo, temp_hi, prcp) = (temp_lo+1, temp_lo+15, DEFAULT)
  WHERE city = 'San Francisco' AND date = '2003-07-03'
  
UPDATE accounts SET (contact_first_name, contact_last_name) =
    (SELECT first_name, last_name FROM salesmen
     WHERE salesmen.id = accounts.sales_id);
     
UPDATE accounts SET contact_first_name = first_name,
                    contact_last_name = last_name
  FROM salesmen WHERE salesmen.id = accounts.sales_id;
  
UPDATE summary s SET (sum_x, sum_y, avg_x, avg_y) =
    (SELECT sum(x), sum(y), avg(x), avg(y) FROM data d
     WHERE d.group_id = s.group_id);

UPDATE employees SET sales_count = sales_count + 1 WHERE id =
  (SELECT sales_person FROM accounts WHERE name = 'Acme Corporation');

UPDATE weather SET temp_lo = temp_lo+1, temp_hi = temp_lo+15, prcp = DEFAULT
  WHERE city = 'San Francisco' AND date = '2003-07-03'
  RETURNING temp_lo, temp_hi, prcp;

// WITH EXAMPLES

WITH moved_rows AS (
    DELETE FROM products
    WHERE
        "date" >= '2010-10-01' AND
        "date" < '2010-11-01'
    RETURNING *
)
INSERT INTO products_log
SELECT * FROM moved_rows;


WITH regional_sales AS (
        SELECT region, SUM(amount) AS total_sales
        FROM orders
        GROUP BY region
     ), top_regions AS (
        SELECT region
        FROM regional_sales
        WHERE total_sales > (SELECT SUM(total_sales)/10 FROM regional_sales)
     )
SELECT region,
       product,
       SUM(quantity) AS product_units,
       SUM(amount) AS product_sales
FROM orders
WHERE region IN (SELECT region FROM top_regions)
GROUP BY region, product;

WITH t AS (
    UPDATE products SET price = price * 1.05
    RETURNING *
)
SELECT * FROM products;

WITH t AS (
    UPDATE accounts SET (contact_first_name, contact_last_name) =
        (SELECT first_name, last_name FROM salesmen
     WHERE salesmen.id = accounts.sales_id);
    RETURNING *
)
insert into accounts values ((SELECT first_name, last_name FROM salesmen
     WHERE !exists t.id = accounts.sales_id))

INSERT INTO table [ ( column [, ...] ) ]
    { DEFAULT VALUES | VALUES ( { expression | DEFAULT } [, ...] ) [, ...] | query }
    [ RETURNING * | output_expression [ AS output_name ] [, ...] ]
	 */
	
	
	
	private void runParsertest(final String cond, final SQLSelectParserParser parser) {
		try {
				System.out.println();
			// There should be zero errors
			SqlContext tree = parser.sql();
			final int numErrors = parser.getNumberOfSyntaxErrors();
			Assert.assertEquals("Expected no failures with " + cond, 0, numErrors);

			SqlParseEventWalker extractor = new SqlParseEventWalker();
			ParseTreeWalker.DEFAULT.walk(extractor, tree);
			System.out.println("Result: " + extractor.getCollector());

		} catch (RecognitionException e) {
			System.err.println("Exception parsing eqn: " + cond);
		}
	}

	private static final SQLSelectParserParser parse(final String condition) {
		CharStream input = new ANTLRInputStream(condition);
		SQLSelectParserLexer lexer = new SQLSelectParserLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		SQLSelectParserParser parser = new SQLSelectParserParser(tokens);

		return parser;
	}

}
