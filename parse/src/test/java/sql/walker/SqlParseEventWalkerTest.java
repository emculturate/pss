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

		final String cond = "SELECT aa.scbcrse_coll_code FROM scbcrse aa "
				+ " WHERE aa.scbcrse_subj_code = courses.subj_code " + " AND (aa.scbcrse_crse_numb = courses.crse_numb "
				+ " or aa.scbcrse_crse_numb = courses.crse_numb) ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void subqueryParseTest() {

		final String cond = "SELECT aa.scbcrse_coll_code FROM scbcrse aa "
				+ " WHERE aa.scbcrse_subj_code = courses.subj_code " + " AND aa.scbcrse_crse_numb = courses.crse_numb "
				+ " AND aa.scbcrse_eff_term = ( " + " SELECT MAX(scbcrse_eff_term) " + " FROM scbcrse "
				+ " WHERE scbcrse_subj_code = courses.subj_code " + " AND scbcrse_crse_numb = courses.crse_numb "
				+ " AND scbcrse_eff_term <= courses.term) ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	@Test
	public void biggerQueryParseTest() {

		final String cond = " select spriden_id, spriden_pidm, terms.max_term, spriden_first_name, spriden_last_name, "
				+ "spriden_mi, SGBSTDN_TERM_CODE_ADMIT FROM ( "
				+ " SELECT spriden_id, spriden_pidm, spriden_first_name, spriden_last_name, spriden_mi FROM spriden "
				+ " WHERE spriden_change_ind is null) spriden " 
				+ " JOIN (  SELECT pidm, max(term) AS max_term FROM ( "
				+ " SELECT shrtgpa_pidm AS pidm, shrtgpa_term_code AS term  "
				+ " FROM shrtgpa WHERE shrtgpa_levl_code = 'UG' "
				+ " UNION ALL SELECT shrtrce_pidm AS pidm, shrtrce_term_code_eff AS term FROM shrtrce WHERE shrtrce_levl_code = 'UG' "
				+ " UNION ALL SELECT sfrstca_pidm AS pidm, sfrstca_term_code AS term   FROM sfrstca "
				+ " JOIN stvterm ON stvterm_code = sfrstca_term_code " + " AND stvterm_end_date > SYSDATE - 365  "
				+ " UNION ALL SELECT sgbstdn_pidm AS pidm, sgbstdn_term_code_eff AS term "
				+ " FROM sgbstdn WHERE sgbstdn_levl_code = 'UG'  ) x GROUP BY pidm "
				+ " ) terms ON spriden.spriden_pidm = terms.pidm "
				+ " JOIN STVTERM termDates ON termDates.STVTERM_CODE = terms.max_term "
				+ " JOIN (SELECT sgbstdn_pidm, MIN(SGBSTDN_TERM_CODE_ADMIT) AS SGBSTDN_TERM_CODE_ADMIT from sgbstdn "
				+ " WHERE sgbstdn_levl_code = 'UG' GROUP BY sgbstdn_pidm "
				+ " ) undergradOnly ON undergradOnly.sgbstdn_pidm = spriden.spriden_pidm "
				+ " GROUP BY spriden_id, spriden_pidm, terms.max_term, spriden_first_name, spriden_last_name, spriden_mi, SGBSTDN_TERM_CODE_ADMIT "
				+ " HAVING max(max_term) >= 201310 ";

		final SQLSelectParserParser parser = parse(cond);
		runParsertest(cond, parser);
	}

	private void runParsertest(final String cond, final SQLSelectParserParser parser) {
		try {
			// There should be zero errors
			SqlContext tree = parser.sql();
			final int numErrors = parser.getNumberOfSyntaxErrors();
			Assert.assertEquals("Expected no failures with " + cond, numErrors, 0);

			SqlParseEventWalker extractor = new SqlParseEventWalker();
			ParseTreeWalker.DEFAULT.walk(extractor, tree);
			System.out.println(tree);

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
