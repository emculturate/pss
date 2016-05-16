package sql.walker;

import java.util.HashMap;
import java.util.Map;

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
	public void queryOverEntityTest() {
		final String cond = "SELECT aa.scbcrse_coll_code as [College Code], aa.*, aa.[Attribute Name] FROM [Student Coursework] as aa, [Institutional Course] as courses "
				+ " WHERE not aa.scbcrse_subj_code = courses.subj_code "
				+ " AND aa.scbcrse_crse_numb = courses.crse_numb ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void queryOverEntitySwapTest() {
		final String cond = "SELECT aa.[College Name] as [College Code], aa.*, aa.[Attribute Name] FROM [Student Coursework] as aa, [Institutional Course] as courses "
				+ " WHERE not aa.[Subject Code] = courses.[Subject Code] "
				+ " AND aa.[Course Number] = courses.[Course Number] ";

		final SQLSelectParserParser parser = parse(cond);
		
		HashMap<String, String> entityMap = new HashMap<String, String> ();
		// load with physical table names
		entityMap.put("[Institutional Course]", "panto.1234_908");
		entityMap.put("[Student Coursework]", "panto.5637_453");
		
		
		HashMap<String, Map<String, String>> attributeMap = new HashMap<String, Map<String, String>> ();
		// [institutional course]
		HashMap<String, String> tableMap = new HashMap<String, String> ();
		attributeMap.put("[Institutional Course]", tableMap);
		tableMap.put("[Subject Code]", "scbcrse_subj_code");
		tableMap.put("[Course Number]", "crs_no");
		
		// [student coursework]
		tableMap = new HashMap<String, String> ();
		attributeMap.put("[Student Coursework]", tableMap);
		tableMap.put("[Attribute Name]", "oth_name");
		tableMap.put("[College Name]", "clg_name");
		tableMap.put("[Subject Code]", "scbcrse_subj_code");
		tableMap.put("[Course Number]", "crs_no");
		
		runParsertest(cond, parser, entityMap, attributeMap);
	}

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
				+ " FROM scbcrse " + " group by scbcrse_subj_code, xxx " + " order by 2, scbcrse_subj_code, 1 ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void simpleAndOrParseTest() {
//gyg
		final String cond = " SELECT scbcrse_subj_code FROM scbcrse " + " where a = b AND c=d  OR e=f and g=h ";

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

		final String cond = " SELECT * FROM third join fourth on a = b " + " left outer join fifth on b = d ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void simpleMultipleUnionParseTest() {

		final String cond = " SELECT first FROM third " + " union select third from fifth "
				+ " union select fourth from sixth " + " union select seventh from eighth ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void simpleMultipleIntersectParseTest() {

		final String cond = " SELECT first FROM third " + " intersect select third from fifth "
				+ " intersect select fourth from sixth " + " intersect select seventh from eighth ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void simpleUnionIntersectParseTest() {

		final String cond = " SELECT first FROM third " + " union select third from fifth "
				+ " intersect select fourth from sixth " + " union select seventh from eighth ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void nestedUnionIntersectAAParseTest() {

		final String cond = " SELECT first FROM ( " + "  select third from fifth "
				+ " intersect select fourth from sixth ) aa " + " union select seventh from eighth ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void nestedUnionIntersectParseTest() {

		final String cond = " SELECT first FROM ( " + "  select third from fifth "
				+ " intersect select fourth from sixth ) " + " union select seventh from eighth ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void numericLiteralParseTest() {
// NUMBERS MISTAKEN FOR COLUMN NAMES; SHOULD notice context. Table names can start with numbers, not column names
		final String cond = " SELECT 123 as intgr, 56.98 as decml, 34.0 e+8 as expon from h.5463_77 ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void subqueryParseTest() {
		// probably could handle unknowns from inside query better. Also, should
		// trap/notice there's no COURSES table
		final String cond = "SELECT aa.scbcrse_coll_code FROM scbcrse aa "
				+ " WHERE aa.scbcrse_subj_code = courses.subj_code " + " AND aa.scbcrse_crse_numb = courses.crse_numb "
				+ " AND aa.scbcrse_eff_term = ( " + " SELECT MAX(scbcrse_eff_term) " + " FROM scbcrse "
				+ " WHERE scbcrse_subj_code = courses.subj_code " + " AND scbcrse_crse_numb = courses.crse_numb "
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

		final String cond = "SELECT trim(leading '0' from field1), a || b, " + " trim('0' || field2,'0') "
				+ " FROM scbcrse aa " + " WHERE subj_code in ('AA', 'BB') ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void inPredicateFunctionParseTest() {
		final String cond = "SELECT * " + " FROM scbcrse aa " + " WHERE subj_code in ('AA', 'BB') "
				+ " and item in (select * from other)";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void complexCaseFunctionTest() {

		final String cond = " SELECT " + 
  " CASE   " +
  " WHEN s948.OBSERVATION_TM THEN S948.t_student_last_name   " +
  " WHEN COALESCE( S949.OBSERVATION_TM>=S948.OBSERVATION_TM , FALSE) THEN S949.t_student_last_name   " +
  " ELSE COALESCE(S948.t_student_last_name, S949.t_student_last_name) END AS t_student_last_name " + 
  " FROM my.234 as s948, my.other5 as s949";
		
		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void caseStatementParseTest() {

		final String cond = " SELECT CASE WHEN true THEN 'Y' " +
				  "  WHEN false THEN 'N' " +
		          " ELSE 'N' END as case_one, "
				+ " CASE  col WHEN 'a' THEN 'b'	 " 
		        + " ELSE null END as case_two " 
				+ " FROM sgbstdn ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void selectItemSubqueryStatementParseTest() {
		final String cond = " SELECT first_item,( " + " SELECT item " + " FROM sgbstdn "
				+ " WHERE sgbstdn_levl_code = 'US' " + " ) AS INTERNATIONAL_IND " + " FROM sgbstdn ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void nestedSymbolTableConstructionTest() {
		final String cond = " SELECT b.att1, b.att2 " + " from (SELECT a.col1 as att1, a.col2 as att2 "
				+ " FROM tab1 as a" + " WHERE a.col1 <> a.col3 " + " ) AS b ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void rankPartitionSyntaxTest() {
		final String cond = " SELECT " +
				" rank() OVER (partition by k_stfd order by OBSERVATION_TM desc, row_num desc) AS key_rank " 
				+ " FROM tab1 as a" ;

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
				+ " , population.spriden_first_name AS FIRST_NAME " + " , population.spriden_mi AS MIDDLE_INITIAL "
				+ " , population.spriden_last_name AS LAST_NAME "
				+ " , TO_CHAR(demographic.spbpers_birth_date, 'yyyymmdd') AS DATE_OF_BIRTH "
				+ " , NVL(demographic.spbpers_sex,'') AS GENDER " + " , NVL(f.goremal_email_address,'') AS EMAIL_ID "
				+ " , NVL(demographic.spbpers_ethn_code,'') AS ETHNICITY_CD "
				+ " , NVL(demographic.spbpers_dead_ind,'') AS DECEASED_IND " + " , '' AS Field10 " + " , ( "
				+ " SELECT CASE WHEN sgbstdn.sgbstdn_resd_code in ('G') THEN 'Y'	 " + " ELSE 'N' END "
				+ " FROM sgbstdn " + " JOIN ( " + " SELECT sgbstdn_pidm, max(sgbstdn_term_code_eff) AS max_term "
				+ " FROM sgbstdn " + " WHERE sgbstdn_levl_code = 'US' " + " GROUP BY sgbstdn_pidm "
				+ " ) m on sgbstdn.sgbstdn_pidm = m.sgbstdn_pidm and sgbstdn.sgbstdn_term_code_eff = m.max_term "
				+ " WHERE sgbstdn.sgbstdn_pidm = population.spriden_pidm " + " 		) AS INTERNATIONAL_IND "
				+ " , NVL(GOBINTL.GOBINTL_NATN_CODE_LEGAL,'') AS COUNTRY_CD " + " , '' AS INST_FIRST_TERM_ID "
				+ " , hs.stvsbgi_desc AS HS_NAME " + " , sobsbgi.sobsbgi_city AS HS_CITY "
				+ " , sobsbgi.sobsbgi_stat_code AS HS_STATE " + " , hs.sorhsch_class_size AS HS_SIZE "
				+ " , hs.sorhsch_percentile AS HS_PERCENTILE " + " , hs.sorhsch_class_rank AS HS_RANK "
				+ " , hs.sorhsch_gpa AS HS_GPA " + " , '' AS Field21 "
				+ " , hp.sprtele_phone_area || hp.sprtele_phone_number AS HOME_PHONE " + " , '' AS Field23 "
				+ " , cp.sprtele_phone_area || cp.sprtele_phone_number AS MOBILE_PHONE "
				+ " , address.spraddr_street_line1 AS MAIL_ADDRESS1 "
				+ " , address.spraddr_street_line2 AS MAIL_ADDRESS2 " + " , address.spraddr_city AS MAIL_CITY "
				+ " , address.spraddr_stat_code AS MAIL_STATE " + " , address.spraddr_zip AS MAIL_ZIP_CODE "
				+ " , '' AS Field30 " + " , '' AS Field31 " + " , demographic.SPBPERS_LGCY_CODE AS STUDENT_LEGACY_CD "
				+ " , ( " + " SELECT SGBSTDN_ADMT_CODE " + " FROM sgbstdn "
				+ " WHERE sgbstdn_pidm = population.spriden_pidm " + " AND sgbstdn_levl_code = 'US'			 "
				// --MODIFY PER MEMBER
				+ " AND sgbstdn_term_code_eff = ( " + " SELECT max(sgbstdn_term_code_eff) " + " FROM sgbstdn "
				+ " WHERE sgbstdn_pidm = population.spriden_pidm " + " AND sgbstdn_levl_code = 'US')		 "
				// --MODIFY PER MEMBER
				+ " 		) AS STUDENT_ADMIT_CD "
				+ " , CASE WHEN shrtrit_primary.shrtrit_sbgi_code is not null THEN 'Y' ELSE 'N' END AS TRANSFER_STUDENT_IND "
				+ " , shrtrit_primary.shrtrit_sbgi_code AS TRANSFER_INST_CD "
				+ " , NVL(demographic.SPBPERS_VERA_IND,'N') as VETERAN_IND "
				+ " , CASE WHEN readmit.saradap_pidm IS NULL THEN 'N' ELSE 'Y' END as READMIT_IND "
				+ " , CASE WHEN RCRAPP3_1.pidm IS NOT NULL THEN 'Y' ELSE 'N' END AS FIRST_GEN_IND  "
				+ " , sobsbgi.SOBSBGI_ZIP AS HS_ZIP_CODE " + " , '' AS ADMISSION_ZIP_CODE " + " , '' AS REGION_CD "
				+ " , ( " + " SELECT CASE WHEN SGBSTDN_STST_CODE = 'AS' THEN 'Y' ELSE 'N' END "
				// --MODIFY PER MEMBER
				+ " FROM sgbstdn " + " WHERE sgbstdn_pidm = population.spriden_pidm "
				+ " AND sgbstdn_levl_code = 'US'			 "
				// --MODIFY PER MEMBER
				+ " AND sgbstdn_term_code_eff = ( " + " SELECT max(sgbstdn_term_code_eff) " + " FROM sgbstdn "
				+ " WHERE sgbstdn_pidm = population.spriden_pidm " + " 	AND sgbstdn_levl_code = 'US')		 "
				// --MODIFY PER MEMBER
				+ " ) AS ACTIVE_IND "

		+ " FROM  "
		// --STUDENT POPULATION
				+ " ( " + " SELECT "
				+ " spriden_id, spriden_pidm, terms.max_term, spriden_first_name, spriden_last_name, spriden_mi "
				+ " FROM ( "
				+ " SELECT spriden_id, spriden_pidm, spriden_first_name, spriden_last_name, spriden_mi FROM spriden WHERE spriden_change_ind is null "
				+ " ) spriden " + " JOIN ( " + " SELECT pidm, max(term) AS max_term " + " FROM ( "
				+ " SELECT shrtgpa_pidm AS pidm, shrtgpa_term_code AS term "
				+ "	FROM shrtgpa WHERE shrtgpa_levl_code = 'US'	 "
				// -- MODIFY PER MEMBER
				+ " UNION ALL SELECT shrtrce_pidm AS pidm, shrtrce_term_code_eff AS term "
				+ " FROM shrtrce WHERE shrtrce_levl_code = 'US'	 "
				// -- MODIFY PER MEMBER
				+ " UNION ALL SELECT sfrstcr_pidm AS pidm, sfrstcr_term_code AS term " + " FROM sfrstcr "
				+ " JOIN stvterm ON stvterm_code = sfrstcr_term_code " + " WHERE sfrstcr_levl_code = 'US'		 "
				// -- MODIFY PER MEMBER
				+ " AND stvterm_end_date > SYSDATE - 365		 "
				// --INSTATED TO REDUCE RUN TIME
				+ " UNION ALL SELECT sgbstdn_pidm AS pidm, sgbstdn_term_code_eff AS term 	FROM sgbstdn WHERE sgbstdn_levl_code = 'US'	 "
				// -- MODIFY PER MEMBER
				+ " ) x " + " GROUP BY pidm " + " ) terms ON spriden.spriden_pidm = terms.pidm "
				/*
				 * JOIN ( REMOVED 2015 01 20 CHECK SF TICKET FOR FULL DETAILS --
				 * NEW LEVL CODE LOGIC IN ST SHOULD FILTER THESE STUDENTS SELECT
				 * sgbstdn_pidm FROM sgbstdn a WHERE sgbstdn_levl_code = 'US'
				 * AND sgbstdn_term_code_eff = ( SELECT
				 * MAX(sgbstdn_term_code_eff) FROM sgbstdn WHERE sgbstdn_pidm =
				 * a.sgbstdn_pidm ) ) UGRD ON spriden.spriden_pidm =
				 * ugrd.sgbstdn_pidm
				 */
				+ " JOIN STVTERM termDates ON termDates.STVTERM_CODE = terms.max_term "
				+ " GROUP BY spriden_id, spriden_pidm, terms.max_term, spriden_first_name, spriden_last_name, spriden_mi "
				// --DEVELOPMENT FILTER
				// --HAVING max(max_term) >= 199808 --ADJUST THIS LINE BY
				// LOOKING UP THE
				// CURRENT TERM
				// --HISTORICAL FILTER
				// --HAVING max(termDates.STVTERM_START_DATE) > SYSDATE - 3650
				// --ADJUST IF
				// WANT DIFFERENT THAN 10 YEARS
				// --DAILY FILTER
				+ " HAVING max(termDates.STVTERM_START_DATE) > SYSDATE - 730 "
				// --ADJUST IF WANT DIFFERENT THAN 2 YEARS
				+ " ) population "
				// --DEMOGRAPHIC INFORMATION
				+ " LEFT OUTER JOIN spbpers demographic " + " ON population.spriden_pidm = demographic.spbpers_pidm "
				// --ADDRESS
				+ " LEFT OUTER JOIN ( "
				+ " SELECT spraddr.spraddr_pidm, spraddr.spraddr_street_line1, spraddr.spraddr_street_line2, spraddr_city, spraddr_stat_code, spraddr_zip "
				+ " FROM spraddr " + " JOIN ( " + " SELECT spraddr_pidm, max(spraddr_seqno) AS max_seqno "
				+ " FROM spraddr " + " WHERE spraddr_atyp_code = 'MA'							 "
				// --MODIFY PER MEMBER
				+ " AND spraddr_status_ind is null " + " GROUP BY spraddr_pidm "
				+ " ) addr_max ON spraddr.spraddr_pidm = addr_max.spraddr_pidm AND spraddr.spraddr_seqno = addr_max.max_seqno "
				+ " WHERE spraddr.spraddr_atyp_code = 'MA' "
				// --MODIFY PER MEMBER
				+ " AND spraddr.spraddr_status_ind is null " + " AND spraddr_FROM_date <= sysdate "
				+ " AND (spraddr_to_date >= sysdate or spraddr_to_date is null) "
				+ " ) address ON population.spriden_pidm = address.spraddr_pidm "
				// --HOME PHONE
				+ " LEFT OUTER JOIN ( "
				+ " SELECT sprtele.sprtele_pidm, sprtele.sprtele_phone_area, sprtele.sprtele_phone_number "
				+ " FROM sprtele " + " JOIN ( " + " SELECT sprtele_pidm, max(sprtele_seqno) AS max_seqno "
				+ " FROM sprtele  " + " WHERE sprtele_tele_code = 'MA' 	 "
				// --MODIFY PER MEMBER
				+ " AND sprtele_status_ind is null " + " GROUP BY sprtele_pidm "
				+ " ) tele_max ON sprtele.sprtele_pidm = tele_max.sprtele_pidm AND sprtele.sprtele_seqno = tele_max.max_seqno "
				+ " WHERE sprtele.sprtele_tele_code = 'MA'  "
				// --MODIFY PER MEMBER
				+ " AND sprtele.sprtele_status_ind is null " + " ) hp ON population.spriden_pidm = hp.sprtele_pidm "

		// --MOBILE PHONE
				+ " LEFT OUTER JOIN ( "
				+ " SELECT sprtele.sprtele_pidm, sprtele.sprtele_phone_area, sprtele.sprtele_phone_number "
				+ " FROM sprtele " + " JOIN ( " + " SELECT sprtele_pidm, max(sprtele_seqno) as max_seqno "
				+ " FROM sprtele  " + " WHERE sprtele_tele_code = 'CP'  "
				// --MODIFY PER MEMBER
				+ " AND sprtele_status_ind is null " + " GROUP BY sprtele_pidm "
				+ " ) tele_max ON sprtele.sprtele_pidm = tele_max.sprtele_pidm AND sprtele.sprtele_seqno = tele_max.max_seqno "
				+ " WHERE sprtele.sprtele_tele_code = 'CP'  "
				// --MODIFY PER MEMBER
				+ " AND sprtele.sprtele_status_ind is null " + " ) cp ON population.spriden_pidm = cp.sprtele_pidm "
				// --EMAIL
				+ " LEFT OUTER JOIN goremal f  " + " ON population.spriden_pidm = f.goremal_pidm "
				+ " 	AND f.goremal_emal_code = 'GSU' AND f.goremal_status_ind = 'A' AND f.goremal_preferred_ind = 'Y' "
				// --MODIFY PER MEMBER
				/*
				 * LEFT OUTER JOIN ( SELECT SGBSTDN.SGBSTDN_pidm,
				 * SGBSTDN.SGBSTDN_ADMT_CODE, SGBSTDN.SGBSTDN_STST_CODE FROM
				 * SGBSTDN WHERE sgbstdn_levl_code in ('US') --MODIFY PER MEMBER
				 * ) m on population.spriden_pidm = m.sgbstdn_pidm
				 */
				// --LEGACY
				+ " LEFT OUTER JOIN stvlgcy leg " + " ON demographic.spbpers_lgcy_code = leg.stvlgcy_code "
				// --HIGH SCHOOL
				+ " LEFT OUTER JOIN ( "
				+ " SELECT sorhsch.sorhsch_pidm, sorhsch_gpa, sorhsch_class_rank, sorhsch_percentile, sorhsch_class_size, stvsbgi.stvsbgi_desc, sorhsch_sbgi_code "
				+ " FROM sorhsch " + " JOIN ( " + " SELECT sorhsch_pidm, max(sorhsch_activity_date) AS max_date "
				+ " FROM sorhsch " + " GROUP BY sorhsch_pidm "
				+ " ) crit ON sorhsch.sorhsch_pidm = crit.sorhsch_pidm AND sorhsch.sorhsch_activity_date = crit.max_date "
				+ " JOIN stvsbgi on sorhsch.sorhsch_sbgi_code = stvsbgi.stvsbgi_code "
				+ " ) hs ON population.spriden_pidm = hs.sorhsch_pidm "
				// --HIGH SCHOOL PT2
				+ " LEFT OUTER JOIN sobsbgi  " + " ON hs.sorhsch_sbgi_code = sobsbgi.sobsbgi_sbgi_code "
				// --COUNTRY CODE
				+ " LEFT OUTER JOIN gobintl  " + " 	ON gobintl.gobintl_pidm = population.spriden_pidm "
				// --TRANSFER INSTITUTION
				+ " LEFT OUTER JOIN ( " + " SELECT * " + " FROM shrtrit " + " JOIN ( "
				+ " SELECT a.shrtrit_pidm AS pidm, max(a.shrtrit_seq_no) AS max_date " + " FROM shrtrit a "
				+ " GROUP BY a.shrtrit_pidm "
				+ " ) shrtrit_max ON shrtrit.shrtrit_pidm = shrtrit_max.pidm AND shrtrit.shrtrit_seq_no = shrtrit_max.max_date "
				+ " ) shrtrit_primary ON shrtrit_primary.pidm = population.spriden_pidm "
				// --FIRST GEN INDICATOR
				+ " LEFT JOIN ( " + " SELECT DISTINCT rcrapp3.rcrapp3_pidm as pidm " + " FROM rcrapp3 " + " JOIN ( "
				+ " SELECT a.rcrapp3_pidm, max(a.rcrapp3_aidy_code) as max_aidy " + " FROM rcrapp3 a "
				+ " GROUP BY a.rcrapp3_pidm, a.rcrapp3_seq_no "
				+ " ) rcrapp3_max on rcrapp3.rcrapp3_pidm = rcrapp3_max.rcrapp3_pidm AND rcrapp3.rcrapp3_aidy_code = rcrapp3_max.max_aidy "
				+ " WHERE rcrapp3.rcrapp3_seq_no = '1' AND (rcrapp3.RCRAPP3_FATHER_HI_GRADE IN ('1','2') OR rcrapp3.RCRAPP3_MOTHER_HI_GRADE IN ('1','2')) "
				+ " ) RCRAPP3_1 ON population.spriden_pidm = RCRAPP3_1.pidm "
				// READMIT INDICATOR
				+ " LEFT OUTER JOIN ( " + " SELECT DISTINCT SARADAP_PIDM " + " FROM SARADAP  "
				+ " WHERE SARADAP_ADMT_CODE = 'RE' AND SARADAP_LEVL_CODE = 'US'  "
				// --MODIFY PER MEMBER
				+ " ) readmit ON readmit.SARADAP_PIDM = population.spriden_pidm " + " WHERE 1=1 " + " ORDER BY 1";

		final SQLSelectParserParser parser = parse(query);
		runParsertest(query, parser);
	}


	@Test
	public void complexHiveQueryJoinTest() {

		final String cond = " SELECT " + 
  " CASE   " +
  " WHEN COALESCE( S948.OBSERVATION_TM>=S949.OBSERVATION_TM , FALSE) THEN S948.t_student_last_name   " +
  " WHEN COALESCE( S949.OBSERVATION_TM>=S948.OBSERVATION_TM , FALSE) THEN S949.t_student_last_name   " +
  " ELSE COALESCE(S948.t_student_last_name, S949.t_student_last_name) END AS t_student_last_name, " + 
  " CASE   " +
  " WHEN COALESCE( S948.OBSERVATION_TM>=S949.OBSERVATION_TM , FALSE) THEN S948.t_sur_name   " +
  " WHEN COALESCE( S949.OBSERVATION_TM>=S948.OBSERVATION_TM , FALSE) THEN S949.t_sur_name " +  
  " ELSE COALESCE(S948.t_sur_name, S949.t_sur_name) END AS t_sur_name, " + 
  " CASE   " +
  " WHEN COALESCE( S948.OBSERVATION_TM>=S949.OBSERVATION_TM , FALSE) THEN S948.t_student_first_name   " +
  " WHEN COALESCE( S949.OBSERVATION_TM>=S948.OBSERVATION_TM , FALSE) THEN S949.t_student_first_name   " +
  " ELSE COALESCE(S948.t_student_first_name, S949.t_student_first_name) END AS t_student_first_name " + 
" FROM ( " +
  " SELECT  " +
     " t_student_first_name, " +
     " t_sur_name, " +
     " t_student_last_name, " +
     " k_stfd, " +
     " OBSERVATION_TM " + 
  " from ( " +
     " SELECT  " +
        " t_student_first_name, " +
        " t_sur_name, " +
        " t_student_last_name, " +
        " k_stfd, " +
        " OBSERVATION_TM,  " +
        " rank() OVER (partition by k_stfd order by OBSERVATION_TM desc, row_num desc) AS key_rank " + 
     " from ( " +
        " SELECT  " +
           " DOB AS t_student_first_name, " +
           " NAME AS t_sur_name, " +
           " LOCATION AS t_student_last_name, " +
           " NAME AS k_stfd, " +
           " OBSERVATION_TM,  " +
           " pantodev.row_num() as row_num " + 
        " FROM pantodev.23810_949 " + 
        " WHERE  " +
           " OBSERVATION_DT <= 20160321  " +
           " AND unix_timestamp(OBSERVATION_TM) <= unix_timestamp('2016-03-21 10:43:15.0') " +
     " ) a " +
    " ) b where key_rank =1 " +
" ) S949  " +
" FULL OUTER JOIN ( " +
 "  SELECT  " +
    "  t_student_first_name, " + 
     " t_sur_name, " +
     " t_student_last_name, " +
     " k_stfd, " +
     " OBSERVATION_TM " + 
  " from ( " +
     " SELECT  " +
        " t_student_first_name, " + 
        " t_sur_name, " +
        " t_student_last_name, " +
        " k_stfd, " +
        " OBSERVATION_TM,  " +
        " rank() OVER (partition by k_stfd order by OBSERVATION_TM desc, row_num desc) AS key_rank " + 
     " from ( " +
        " SELECT  " +
           " DOB AS t_student_first_name, " +
           " NAME AS t_sur_name, " +
           " LOCATION AS t_student_last_name, " +
           " NAME AS k_stfd, " +
           " OBSERVATION_TM,  " +
           " pantodev.row_num() as row_num " + 
        " FROM pantodev.23810_948 " + 
        " WHERE  " +
           " OBSERVATION_DT <= 20160309  " +
           " AND unix_timestamp(OBSERVATION_TM) <= unix_timestamp('2016-03-09 12:54:18.0') " +
        " ) a " +
       " ) b where key_rank =1 " +
 " ) S948  " +
" ON (S949.k_stfd=S948.k_stfd) " +  
" where  " +
   " (((unix_timestamp(S949.observation_tm) > unix_timestamp('1900-01-01 00:00:00.0'))  " +
    "  AND (unix_timestamp(S949.observation_tm) <= unix_timestamp('2016-03-30 11:04:40.484'))) " + 
    " OR ((unix_timestamp(S948.observation_tm) > unix_timestamp('1900-01-01 00:00:00.0')) " +
     " AND (unix_timestamp(S948.observation_tm) <= unix_timestamp('2016-03-30 11:04:40.484')))) ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void simpleInsertFromQueryTest() {

		final String cond = " insert into sch.subj.tbl (newcol1, newcol2) values (SELECT b.att1, b.att2 "
				+ " from (SELECT a.col1 as att1, a.col2 as att2 " + " FROM sch.subj.tab1 as a"
				+ " WHERE a.col1 <> a.col3 " + " ) AS b )";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}
	
	/***********************************
	 * Section covering GF Encoder Style Queries
	 */

	@Test
	public void authorizationQueryTest() {

		String sql = "select RECORD_TYPE as RECORD_TYPE, ACTION as ACTION, USER_ID as PRIMARY_USER_ID, ";
		// check authorizationTbl

		sql += "authn.is_active as IS_ACTIVE, authn.can_login as CAN_LOGIN, authn.send_activation as SEND_ACTIVATION, ";
		sql += "IS_ACTIVE, CAN_LOGIN, SEND_ACTIVATION, ";

		sql += "FIRST_NAME as FIRST_NAME, LAST_NAME as LAST_NAME, ";
		sql += "authn.alt_user_id as ALT_USER_ID, ";
		sql += "ROLE_ID as ROLE_ID"
				+ ", EMAIL as EMAIL, ALT_EMAIL as ALT_EMAIL, ADDRESS_1 as ADDRESS_1, ADDRESS_2 as ADDRESS_2, CITY as CITY, STATE as STATE, POSTAL_CODE as POSTAL_CODE, HOME_PHONE as HOME_PHONE,"
				+ " CELL_PHONE as CELL_PHONE, WORK_PHONE as WORK_PHONE, GENDER as GENDER, ETHNICITY as ETHNICITY, DATE_OF_BIRTH as DATE_OF_BIRTH, TOTAL_CREDIT_HOURS as TOTAL_CREDIT_HOURS, CREDIT_HOURS_ATTEMPTED as CREDIT_HOURS_ATTEMPTED, "
				+ " MAJOR_ID as MAJOR_ID, STUDENT_ENROLLMENT_STATUS as STUDENT_ENROLLMENT_STATUS, STUDENT_ENROLLMENT_GOAL as STUDENT_ENROLLMENT_GOAL, ";
		sql += "authn.pin as PIN, authn.sso_id as SSO_ID, ";
		sql += "'' as ACT_TOTAL, '' as ACT_ENGLISH, "
				+ " '' as ACT_READING, '' as ACT_MATH, '' as ACT_SCIENCE, '' as SAT_TOTAL, '' as SAT_VERBAL, '' as SAT_MATH, '' as HIGH_SCHOOL_GPA, "
				+ " '' as FIRST_GENERATION_IND, '' as FATHER_EDUCATION, '' as MOTHER_EDUCATION, '' as HIGH_SCHOOL_ZIP_CODE, '' as HOUSEHOLD_INCOME, "
				+ " '' as SINGLE_PARENT_FAMILY_IND, '' as TRANSFER_GPA, '' as HOME_COLLEGE, '' as RECEIVE_TXT_MESSAGE_IND "
				+ " from "
				+ "(select a.record_type as RECORD_TYPE, a.action as ACTION, a.primary_user_id as USER_ID, first_name as FIRST_NAME, last_name as LAST_NAME, a.is_active as IS_ACTIVE, a.login_ind AS CAN_LOGIN, a.activate_email_ind as SEND_ACTIVATION, role_id as ROLE_ID, '' as MAJOR_ID,total_credit_hours as TOTAL_CREDIT_HOURS, attempted_credit_hours as CREDIT_HOURS_ATTEMPTED, "
				+ " email as EMAIL, '' as ALT_EMAIL, address_1 as ADDRESS_1, address_2 as ADDRESS_2, city as CITY, state as STATE, postal_code as POSTAL_CODE, home_phone as HOME_PHONE, cell_phone as CELL_PHONE, '' as WORK_PHONE, gender as GENDER, ethnicity as ETHNICITY, "
				+ " date_of_birth as DATE_OF_BIRTH, receive_txt_message_ind as RECEIVE_TXT_MESSAGE_IND, student_enrollment_status as STUDENT_ENROLLMENT_STATUS, student_enrollment_goal as STUDENT_ENROLLMENT_GOAL from "
				+ " studentTbl a left join "
				// start of student major
				+ "(select primary_user_id, total_credit_hours,attempted_credit_hours from (select primary_user_id, total_credit_hours,attempted_credit_hours,"
				+ "rank() over (partition by primary_user_id order by b.begin_date desc ,b.end_date desc) term_rank from "
				+ " studentMajorTbl  a, academicPeriodTbl "
				+ " b where a.term_id=b.external_id and a.total_credit_hours is not null and a.attempted_credit_hours is not null and length(trim(a.total_credit_hours)) > 0 and length(trim(a.attempted_credit_hours)) > 0) tbl where term_rank =1)"
				// end of student major
				+ " b on (a.primary_user_id = b.primary_user_id) ";
		sql += " union all "
				+ " select record_type as RECORD_TYPE, action as ACTION, primary_user_id as USER_ID, first_name as FIRST_NAME, last_name as LAST_NAME, is_active as IS_ACTIVE, login_ind AS CAN_LOGIN, activate_email_ind as SEND_ACTIVATION, role_id as ROLE_ID, '' as MAJOR_ID,'' as TOTAL_CREDIT_HOURS, '' as CREDIT_HOURS_ATTEMPTED, "
				+ " email as EMAIL, alt_email as ALT_EMAIL, '' as ADDRESS_1, '' as ADDRESS_2, '' as CITY, '' as STATE, '' as POSTAL_CODE, home_phone as HOME_PHONE, cell_phone as CELL_PHONE, work_phone as WORK_PHONE, '' as GENDER, '' as ETHNICITY, '' as DATE_OF_BIRTH, '' as RECEIVE_TXT_MESSAGE_IND, '' as STUDENT_ENROLLMENT_STATUS, '' as STUDENT_ENROLLMENT_GOAL from "
				+ " advisorTbl  staff";
		sql += " union all "
				+ " select record_type as RECORD_TYPE, action as ACTION, primary_user_id as USER_ID, first_name as FIRST_NAME, last_name as LAST_NAME,is_active as IS_ACTIVE, login_ind AS CAN_LOGIN, activate_email_ind as SEND_ACTIVATION,  role_id as ROLE_ID, '' as MAJOR_ID,'' as TOTAL_CREDIT_HOURS, '' as CREDIT_HOURS_ATTEMPTED, "
				+ " email as EMAIL, alt_email as ALT_EMAIL, '' as ADDRESS_1, '' as ADDRESS_2, '' as CITY, '' as STATE, '' as POSTAL_CODE, home_phone as HOME_PHONE, cell_phone as CELL_PHONE, work_phone as WORK_PHONE, '' as GENDER, '' as ETHNICITY, '' as DATE_OF_BIRTH, '' as RECEIVE_TXT_MESSAGE_IND, '' as STUDENT_ENROLLMENT_STATUS, '' as STUDENT_ENROLLMENT_GOAL from "
				+ " instructorTbl inst";
		sql += ") user";

		sql += " left join authorizationTbl " + " authn on user.USER_ID = authn.primary_user_id";
		sql += " where user.USER_ID is not null and length(trim(user.USER_ID)) > 0";

		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void getRegistrationSqlTest() {
		/*
		 * Registration COLUMNS: RECORD_TYPE, ACTION, TERM_ID, PRIMARY_USER_ID,
		 * GROUP_ID, CLASSIFICATION, OVERALL_GPA, TERM_GPA
		 */

		String cond = " SELECT reg.record_type as RECORD_TYPE, reg.action as ACTION, reg.term_id as TERM_ID, "
				+ " reg.primary_user_id as PRIMARY_USER_ID, reg.group_id as GROUP_ID,reg.classification as CLASSIFICATION, "
				+ " gpatbl.cum_gpa as OVERALL_GPA, gpatbl.term_gpa as TERM_GPA " + " from "
				+ " studentAcademicTbl reg  left outer join "
				+ " (select coalesce(cgpa.primary_user_id,tgpa.primary_user_id) as primary_user_id, coalesce(cgpa.term_id,tgpa.term_id) as term_id, "
				+ " coalesce(cgpa.key_value,'') as cum_gpa, coalesce(tgpa.key_value,'') as term_gpa from (select * from "
				+ " studentTermDataTbl where key_column='cumGPAKey') cgpa "
				+ " full outer join (select * from  studentTermDataTbl  where key_column='termGPAKey' ) tgpa "
				+ " on (cgpa.primary_user_id=tgpa.primary_user_id and "
				+ " cgpa.term_id=tgpa.term_id ) ) gpatbl on (reg.primary_user_id=gpatbl.primary_user_id and reg.term_id=gpatbl.term_id) "
				+ " inner join termFilterTbl  tf on reg.term_id = tf.term_id";
		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void getTermSqlTest() {
		/*
		 * Term COLUMNS: RECORD_TYPE, ACTION, EXTERNAL_ID, NAME, BEGIN_DATE,
		 * END_DATE
		 */
		String cond = "select term.record_type as RECORD_TYPE, term.action as ACTION,  term.external_id as EXTERNAL_ID, term.name as NAME, datestr(term.begin_date, "
				+ " 'TERM_SOURCE_DATE_FORMAT', 'SSCPLUS_DEFAULT_DATE_FORMAT') as BEGIN_DATE, datestr(term.end_date, 'TERM_SOURCE_DATE_FORMAT', "
				+ "'SSCPLUS_DEFAULT_DATE_FORMAT') as END_DATE from academicPeriodTbl " + " term";
		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void getCourseSqlTest() {
		/*
		 * Course COLUMNS: RECORD_TYPE, ACTION, EXTERNAL_ID, COURSE_ID, TITLE,
		 * CREDIT_HOURS
		 */
		String cond = "select crs.record_type as RECORD_TYPE,crs.action as ACTION,concat_ws('-',crs.subject_code,crs.course_number) as EXTERNAL_ID, concat_ws('-',crs.subject_code,crs.course_number) as COURSE_ID, "
				+ " crs.course_title as TITLE, COALESCE(crs.credit_hour_low,crs.credit_hour_high,0) as CREDIT_HOURS from "
				+ " courseTbl crs";
		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void getSectionSqlTest() {
		//********* ERROR: Reference in Hive ARRAY object: "sched_arr[0].col1 as BEGIN_DATE"
		/*
		 * Section COLUMNS: RECORD_TYPE, ACTION, TERM_ID, COURSE_EXTERNAL_ID,
		 * SECTION_NAME, SECTION_TAGS,BEGIN_DATE, END_DATE, START_TIME,
		 * END_TIME, MEETING_DAYS, LOCATION, ADDITIONAL MEETINGS (repeat meeting
		 * info)
		 */
		String cond = "select record_type as RECORD_TYPE, action as ACTION,term_id as TERM_ID,course_external_id as COURSE_EXTERNAL_ID ,section_name as SECTION_NAME,"
				+ "'' as section_tags,  sched_arr[0].col1 as BEGIN_DATE, sched_arr[0].col2 as END_DATE, sched_arr[0].col3 as BEGIN_TIME, sched_arr[0].col4 as END_TIME, sched_arr[0].col5 as MEETING_DAYS,sched_arr[0].col6 as LOCATION from ( "
				+ "select record_type,action,term_id,course_external_id,section_name, collectarray(sched) as sched_arr from ("
				+ "select record_type,action,term_id,course_external_id,section_name,struct(begin_date,end_date,start_time,end_time,meeting_days,location) as sched from ("
				+ "select s.record_type as RECORD_TYPE,s.action as ACTION, s.term_code as TERM_ID, concat_ws('-',s.subject_code,s.course_number) as COURSE_EXTERNAL_ID,"
				+ "case when s.section_name is null or length(trim(s.section_name))=0 then '' else s.section_name end as SECTION_NAME, case when sec.meet_start_date is not null then datestr(sec.meet_start_date, '"
				+ " SECTION_SOURCE_DATE_FORMAT', 'SSCPLUS_DEFAULT_DATE_FORMAT')  else '' end as BEGIN_DATE, case when sec.meet_end_date is not null then datestr(sec.meet_end_date, '"
				+ "SECTION_SOURCE_DATE_FORMAT', 'SSCPLUS_DEFAULT_DATE_FORMAT') else ''  end as END_DATE, "
				+ "case when sec.meet_start_time is null or length(trim(sec.meet_start_time))=0 or length(trim(sec.meet_start_time)) < 4 then '' else concat_ws(':',substr(sec.meet_start_time,1,2),substr(sec.meet_start_time,3,2)) end as START_TIME, "
				+ "case when sec.meet_end_time is null or length(trim(sec.meet_end_time))=0 or length(trim(sec.meet_end_time)) < 4 then '' else concat_ws(':',substr(sec.meet_end_time,1,2),substr(sec.meet_end_time,3,2)) end as END_TIME, "
				+ "coalesce(concat(sec.meet_sunday,meet_monday,meet_tuesday,meet_wednesday,meet_thursday,meet_friday,meet_saturday),'') as MEETING_DAYS,"
				+ "case when (meet_building_code is null or length(trim(meet_building_code))=0) and (meet_room_code is null or length(trim(meet_room_code))=0) then '' "
				+ "when (meet_building_code is null or length(trim(meet_building_code))=0) or (meet_room_code is null or length(trim(meet_room_code))=0) then concat(trim(meet_building_code),trim(meet_room_code)) "
				+ " else concat_ws('-',meet_building_code,meet_room_code) end as LOCATION from "
				+ " sectionTbl s inner join termFilterTbl tf on " + "s.term_code = tf.term_id "
				+ " left join sectionMeetTbl "
				+ " sec on (sec.course_ref_no=s.course_ref_no and sec.term_code=s.term_code) "
				+ ") sub_sec) mid_sec group by record_type,action,term_id,course_external_id,section_name) main_sec";
		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void getSectionSqlV6Test() {
		/*
		 * Section COLUMNS: RECORD_TYPE, ACTION, TERM_ID, COURSE_EXTERNAL_ID,
		 * SECTION_NAME, SECTION_TAGS
		 */
		final String cond = "select s.record_type as RECORD_TYPE, " + "s.action as ACTION, "
				+ "s.term_code as TERM_ID, " + "concat_ws('-',s.subject_code,s.course_number) as COURSE_EXTERNAL_ID, "
				+ "case  " + "when s.section_name is null or length(trim(s.section_name))=0  " + "then ''  "
				+ "else s.section_name  " + "end as SECTION_NAME, " + "s.section_tag as SECTION_TAGS "
				+ "from sectionTbl s  " + "inner join termFilterTbl tf  " + "on s.term_code = tf.term_id ";
		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void getEnrollmentSqlTest() {
		/*
		 * Enrollment COLUMNS: RECORD_TYPE, ACTION, PRIMARY_USER_ID, TERM_ID,
		 * COURSE_EXTERNAL_ID, SECTION_NAME, MIDTERM_GRADE, FINAL_GRADE
		 */
		String cond = "select cw.record_type as RECORD_TYPE, cw.action as ACTION, cw.student_id as PRIMARY_USER_ID, cw.term_code as TERM_ID, concat_ws('-',s.subject_code,s.course_number) "
				+ " as COURSE_EXTERNAL_ID, s.section_name as SECTION_NAME,cw.midterm_grade as MIDTERM_GRADE,cw.final_grade as "
				+ " FINAL_GRADE"
				+ " from courseWorkTbl  cw,  sectionTbl  s inner join termFilterTbl tf on cw.term_code = tf.term_id "
				+ "where cw.course_ref_no=s.course_ref_no and cw.term_code=s.term_code and cw.registration_status_cd in ('regCodes')";
		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void getInstructionSqlTest() {
		/*
		 * Instructor Assignments COLUMNS: RECORD_TYPE, ACTION, TERM_ID,
		 * COURSE_EXTERNAL_ID, SECTION_NAME, PRIMARY_USER_ID
		 */
		String cond = "select ia.record_type as RECORD_TYPE, ia.action as ACTION, ia.term_code as TERM_ID, concat_ws('-',ia.subject_code,ia.course_number) "
				+ "as COURSE_EXTERNAL_ID,ia.section_name as SECTION_NAME ,ia.instructor_id as PRIMARY_USER_ID "
				+ "from instructorAssgnmtTbl  ia inner join termFilterTbl  tf on " + "ia.term_code = tf.term_id";
		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void getGroupingSqlTest() {
		/*
		 * Grouping COLUMNS: RECORD_TYPE, ACTION, GROUP_ID, PRIMARY_USER_ID
		 */
		String sql = " select user.secondary_record_type as RECORD_TYPE,user.action as ACTION,user.group_id as GROUP_ID,user.primary_user_id as PRIMARY_USER_ID from "
				+ " (";
		String[] groupingTbls = new String[10];
		groupingTbls[0] = "firstTable";
		groupingTbls[1] = "secondTable";
		groupingTbls[2] = "thirdTable";
		groupingTbls[3] = "fourthTable";

		String unionStatement = " select secondary_record_type,action,group_id,primary_user_id from zeroTable ";
		for (int i = 0; i < 4; i++) {
			unionStatement += " union all ";
			unionStatement += " select secondary_record_type,action,group_id,primary_user_id from " + groupingTbls[i]
					+ " ";
		}

		sql += unionStatement
				+ ") user where user.primary_user_id is not null and length(trim(user.primary_user_id)) > 0";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void getMajorSqlTest() {
		/*
		 * Major COLUMNS: RECORD_TYPE, ACTION, EXTERNAL_ID, NAME
		 */
		String cond = "select record_type as RECORD_TYPE,action as ACTION,"
				+ "trim(external_id) as EXTERNAL_ID,case when name is null or length(trim(name)) = 0 then 'Major name not available' else trim(name) end as NAME from "
				+ " majorTbl where external_id is not null and length(trim(external_id)) > 0";
		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void getDeclarationSqlTest() {
		//*************** ERROR: Hive syntax not handled: "lateral view explode"
		/*
		 * Declaration COLUMNS: RECORD_TYPE, ACTION, PRIMARY_USER_ID, MAJOR_ID
		 */
		String sql = "select record_type as record_type,action as action,primary_user_id as primary_user_id, "
				+ "maj_items.col1 as major_id from (select record_type,action,primary_user_id, "
				+ "array(struct(major_cd_1), struct(major_cd_2), struct(major_cd_3), struct(major_cd_4)) as major_arr from "
				+ "(select record_type,action, primary_user_id, major_cd_1,major_cd_2,major_cd_3, major_cd_4 from "
				+ "(select a.record_type,a.action,primary_user_id, major_cd_1,major_cd_2,major_cd_3,major_cd_4,";
		sql += "rank() over (partition by primary_user_id order by b.begin_date desc ,b.end_date desc) term_rank from "
				+ " studentMajorTbl a,  academicPeriodTbl "
				+ " b where a.term_id=b.external_id and a.major_cd_1 is not null and length(trim(a.major_cd_1))>0 ) tbl where term_rank=1) res) fin_res ";
		//sql += "lateral view explode(major_arr) exploded_table as maj_items where length(trim(maj_items.col1)) > 0";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void getTagSqlTest() {
		/*
		 * Tag COLUMNS: RECORD_TYPE, ACTION, TAG, GROUP ID, PRIMARY_USER_ID
		 */
		String sql = " select record_type as RECORD_TYPE, action as ACTION, "
				+ "tag as TAG, group_id as GROUP_ID, primary_user_id as PRIMARY_USER_ID from "
				+ " tagTbl where tag is not null and length(trim(tag)) > 0 "
				+ "and group_id is not null and length(trim(group_id)) > 0 "
				+ "and primary_user_id is not null and length(trim(primary_user_id)) > 0 ";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void getAuthorizeSqlTest() {
		/*
		 * Authorize COLUMNS: RECORD_TYPE, ACTION, ROLE_ID, PRIMARY_USER_ID
		 */
		String sql = " select record_type as RECORD_TYPE, action as ACTION, "
				+ "role_id as ROLE_ID, primary_user_id as PRIMARY_USER_ID from  authorizeTbl "
				+ " where role_id is not null and length(trim(role_id)) > 0 "
				+ "and primary_user_id is not null and length(trim(primary_user_id)) > 0 ";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void getCategorySqlTest() {
		/*
		 * Category COLUMNS: RECORD_TYPE, ACTION, EXTERNAL_ID, NAME, GROUP_ID
		 */
		String sql = " select record_type as RECORD_TYPE, action as ACTION, "
				+ "external_id as EXTERNAL_ID, name as NAME, group_id as GROUP_ID from "
				+ " categoryTbl where external_id is not null and length(trim(external_id)) > 0 "
				+ "and name is not null and length(trim(name)) > 0 "
				+ "and group_id is not null and length(trim(group_id)) > 0 ";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void getCategorizeSqlTest() {
		/*
		 * Categorize COLUMNS: RECORD_TYPE, ACTION, CATEGORY_ID, PRIMARY_ID
		 */
		String sql = " select record_type as RECORD_TYPE, action as ACTION, "
				+ "category_id as CATEGORY_ID, primary_id as PRIMARY_ID from  categorizeTbl "
				+ " where (category_id is not null and length(trim(category_id)) > 0 "
				+ "and primary_id is not null and length(trim(primary_id)) > 0) ";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void getRelationshipSqlTest() {
		/*
		 * Relationship COLUMNS: RECORD_TYPE, ACTION, NAME,
		 * PARENT_PRIMARY_USER_ID, CHILD_PRIMARY_USER_ID, GROUP_ID
		 */
		String sql = "select record_type as RECORD_TYPE, action as ACTION, "
				+ "name as NAME, parent_primary_user_id as PARENT_PRIMARY_USER_ID,"
				+ "child_primary_user_id as CHILD_PRIMARY_USER_ID, group_id as GROUP_ID from relationshipTbl "
				+ " where lower(trim(name)) in ('advisor','coach','professor','tutor') "
				+ " and parent_primary_user_id is not null and length(trim(parent_primary_user_id)) > 0"
				+ " and child_primary_user_id is not null and length(trim(child_primary_user_id)) > 0"
				+ " and group_id is not null and length(trim(group_id)) > 0";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void getSectionMeetingSqlTest() {

		String sql = "select sec.record_type as RECORD_TYPE,sec.action as ACTION, sec.term_code as TERM_ID,concat_ws('-',s.subject_code,s.course_number) as COURSE_EXTERNAL_ID, "
				+ "case when s.section_name is null or length(trim(s.section_name))=0 then '' else s.section_name end as SECTION_NAME, "
				+ "case when sec.meet_start_date is not null then datestr(sec.meet_start_date, 'SECTION_SOURCE_DATE_FORMAT', "
				+ "'SSCPLUS_DEFAULT_DATE_FORMAT')  else '' end as BEGIN_DATE, "
				+ "case when sec.meet_end_date is not null then datestr(sec.meet_end_date, 'SECTION_SOURCE_DATE_FORMAT',"
				+ " 'SSCPLUS_DEFAULT_DATE_FORMAT') else ''  end as END_DATE, "
				+ "case when sec.meet_start_time is null or length(trim(sec.meet_start_time))=0 or length(trim(sec.meet_start_time)) < 4 then '' else concat_ws(':',substr(sec.meet_start_time,1,2),substr(sec.meet_start_time,3,2)) end as START_TIME, "
				+ "case when sec.meet_end_time is null or length(trim(sec.meet_end_time))=0 or length(trim(sec.meet_end_time)) < 4 then '' else concat_ws(':',substr(sec.meet_end_time,1,2),substr(sec.meet_end_time,3,2)) end as END_TIME, "
				+ "coalesce(concat(sec.meet_sunday,meet_monday,meet_tuesday,meet_wednesday,meet_thursday,meet_friday,meet_saturday),'') as MEETING_DAYS, "
				+ "case when (meet_building_code is null or length(trim(meet_building_code))=0) and (meet_room_code is null or length(trim(meet_room_code))=0) then '' "
				+ "when (meet_building_code is null or length(trim(meet_building_code))=0) or (meet_room_code is null or length(trim(meet_room_code))=0) then concat(trim(meet_building_code),trim(meet_room_code)) "
				+ " else concat_ws('-',meet_building_code,meet_room_code) end as LOCATION from sectionMeetTbl sec inner join termFilterTbl tf on "
				+ "sec.term_code = tf.term_id " + " inner join sectionTbl "
				+ " s on (sec.course_ref_no=s.course_ref_no and sec.term_code=s.term_code)";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void createTermFilterTableTest() {
		String sql = "select external_id as term_id from " + "termStgTableName  a, (select min(term_rank) as term_rank "
				+ "from (" + "select curr_term_rank as term_rank from currentTermTableName union all "
				+ "select max(t1.term_rank) term_rank from termStgTableName  t1, "
				+ " currentTermTableName t2 where t1.term_rank < t2.curr_term_rank" + ") tbl" + ") b "
				+ "where a.term_rank >= b.term_rank";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void createCurrentTermTableTest() {
		String sql = "select min(term_rank) curr_term_rank from ( " + "select term_rank from "
				+ " termStgTableName  where unix_timestamp() between term_start and term_end " + "union all "
				+ "select max(term_rank) term_rank from termStgTableName " + " where unix_timestamp() >= term_start "
				+ ") tbl";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	@Test
	public void createTermStgTableTest() {
		String sql = "select external_id, " + "rank() over (order by begin_date, end_date) term_rank, "
				+ "unix_timestamp(begin_date,'yyyyMMdd') term_start, "
				+ "unix_timestamp(end_date,'yyyyMMdd') term_end from  hiveTableName ";
		final SQLSelectParserParser parser = parse(sql);
		runParsertest(sql, parser);
	}

	/*****************************
	 * End Of GF Encoder Tests
	 * @param entityMap 
	 */

	/*
	 * UPDATE weather SET (temp_lo, temp_hi, prcp) = (temp_lo+1, temp_lo+15,
	 * DEFAULT) WHERE city = 'San Francisco' AND date = '2003-07-03'
	 * 
	 * UPDATE accounts SET (contact_first_name, contact_last_name) = (SELECT
	 * first_name, last_name FROM salesmen WHERE salesmen.id =
	 * accounts.sales_id);
	 * 
	 * UPDATE accounts SET contact_first_name = first_name, contact_last_name =
	 * last_name FROM salesmen WHERE salesmen.id = accounts.sales_id;
	 * 
	 * UPDATE summary s SET (sum_x, sum_y, avg_x, avg_y) = (SELECT sum(x),
	 * sum(y), avg(x), avg(y) FROM data d WHERE d.group_id = s.group_id);
	 * 
	 * UPDATE employees SET sales_count = sales_count + 1 WHERE id = (SELECT
	 * sales_person FROM accounts WHERE name = 'Acme Corporation');
	 * 
	 * UPDATE weather SET temp_lo = temp_lo+1, temp_hi = temp_lo+15, prcp =
	 * DEFAULT WHERE city = 'San Francisco' AND date = '2003-07-03' RETURNING
	 * temp_lo, temp_hi, prcp;
	 * 
	 * // WITH EXAMPLES
	 * 
	 * WITH moved_rows AS ( DELETE FROM products WHERE "date" >= '2010-10-01'
	 * AND "date" < '2010-11-01' RETURNING * ) INSERT INTO products_log SELECT *
	 * FROM moved_rows;
	 * 
	 * 
	 * WITH regional_sales AS ( SELECT region, SUM(amount) AS total_sales FROM
	 * orders GROUP BY region ), top_regions AS ( SELECT region FROM
	 * regional_sales WHERE total_sales > (SELECT SUM(total_sales)/10 FROM
	 * regional_sales) ) SELECT region, product, SUM(quantity) AS product_units,
	 * SUM(amount) AS product_sales FROM orders WHERE region IN (SELECT region
	 * FROM top_regions) GROUP BY region, product;
	 * 
	 * WITH t AS ( UPDATE products SET price = price * 1.05 RETURNING * ) SELECT
	 * * FROM products;
	 * 
	 * WITH t AS ( UPDATE accounts SET (contact_first_name, contact_last_name) =
	 * (SELECT first_name, last_name FROM salesmen WHERE salesmen.id =
	 * accounts.sales_id); RETURNING * ) insert into accounts values ((SELECT
	 * first_name, last_name FROM salesmen WHERE !exists t.id =
	 * accounts.sales_id))
	 * 
	 * INSERT INTO table [ ( column [, ...] ) ] { DEFAULT VALUES | VALUES ( {
	 * expression | DEFAULT } [, ...] ) [, ...] | query } [ RETURNING * |
	 * output_expression [ AS output_name ] [, ...] ]
	 */

	private void runParsertest(final String cond, final SQLSelectParserParser parser) {
		runParsertest( cond, parser, null, null);
	}
			
	private void runParsertest(final String cond, final SQLSelectParserParser parser, 
			HashMap<String, String> entityMap, 
			HashMap<String, Map<String, String>> attributeMap) {
		try {
			System.out.println();
			// There should be zero errors
			SqlContext tree = parser.sql();
			final int numErrors = parser.getNumberOfSyntaxErrors();
			Assert.assertEquals("Expected no failures with " + cond, 0, numErrors);

			SqlParseEventWalker extractor = new SqlParseEventWalker();
			if (entityMap != null)
				extractor.setEntityTableNameMap(entityMap);
			if (attributeMap != null)
				extractor.setAttributeColumnMap(attributeMap);
			ParseTreeWalker.DEFAULT.walk(extractor, tree);
			System.out.println("Result: " + extractor.getSqlTree());
			System.out.println("Interface: " + extractor.getInterface());

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
