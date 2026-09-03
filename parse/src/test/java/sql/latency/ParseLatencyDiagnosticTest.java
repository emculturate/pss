package sql.latency;

import org.junit.Test;

import sql.walker.SetOpTimingProbeFixtures;

import static mumble.SQLParserEndPoints.SQLPARSER_SQL_TREE_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Phase 2.8 diagnostic driver — points the latency service at representative
 * Panto timeout fixtures and prints a breakdown of lex / parse / walk / finalize
 * time plus ANTLR4 SLL→LL fallback counts.
 *
 * <h2>How to read the output</h2>
 * <pre>
 * endpoint=SQL          chars=  3142  lex=   3ms  parse= 90002ms  walk=     8ms  …  sllFallback=847
 *   └─ parse dominates + sllFallback > 0  →  grammar prediction problem
 *
 * endpoint=SQL          chars=  3142  lex=   2ms  parse=    12ms  walk= 89990ms  …  sllFallback=0
 *   └─ walk dominates + sllFallback=0     →  event-walker O(n²) problem
 * </pre>
 *
 * <p>Run a single test with:
 * <pre>
 *   mvn -f parse/pom.xml -Dtest=ParseLatencyDiagnosticTest#pantRow28_acquia test
 * </pre>
 *
 * <p>Run all with:
 * <pre>
 *   mvn -f parse/pom.xml -Dtest=ParseLatencyDiagnosticTest test
 * </pre>
 */
public class ParseLatencyDiagnosticTest {

    private static final long WARN_MS = 5_000L;

    // ── harness ───────────────────────────────────────────────────────────────

    private static void run(String label, String query, String endpoint) {
        System.out.println("\n=== " + label + " ===");
        ParseLatencyReport r = ParseLatencyDiagnosticService.diagnose(query, endpoint);
        System.out.println(r.summary());

        if (r.parseMs > WARN_MS) {
            System.out.println("  *** SLOW PARSE — likely SLL→LL fallback in grammar (sllFallbackCount=" +
                    r.sllFallbackCount + ")");
        }
        if (r.walkMs > WARN_MS) {
            System.out.println("  *** SLOW WALK — likely O(n²) in event walker");
        }
        if (r.sllFallbackCount > 0) {
            System.out.println("  *** " + r.sllFallbackCount + " SLL→LL fallbacks detected — grammar has prediction ambiguity");
        }
        if (r.ambiguityCount > 0) {
            System.out.println("  *** " + r.ambiguityCount + " grammar ambiguities");
        }
    }

    // ── Row 28: PDP_Acquia_Export (representative of the 5-row Acquia family) ─

    @Test
    public void pantoRow28_acquia() {
        run("Panto row 28 — Acquia PDP_Acquia_Export", PANTO_ROW_28, SQLPARSER_SQL_TREE_KEY);
    }

    // ── Row 475: EAB.Country (248 UNION of country literals — 2.8-1 canary) ───

    /** E2 gate: full Panto row must finish well under the 90s RMCP kill (5.0.0-3 ~7.4s walk). */
    private static final long PANTO_ROW_475_E2_TIMEOUT_MS = 90_000L;

    @Test
    public void pantoRow475_eabCountry() throws Exception {
        String sql = PantoOutstandingSqlFixtures.sqlForCsvRow(475);
        assertE2Gate("Panto row 475 — EAB.Country (full 248 UNION)", sql);
    }

    @Test
    public void pantoRow475_eabCountry_trimSmoke() {
        run("Panto row 475 — EAB.Country (20-branch trim smoke)", buildUnion(20), SQLPARSER_SQL_TREE_KEY);
    }

    // ── Row 1837: PCM convert UNION ALL (2.8-2 canary) ───────────────────────

    @Test
    public void pantoRow1837_pcmConvert() throws Exception {
        String sql = PantoOutstandingSqlFixtures.sqlForCsvRow(1837);
        System.out.println("\n=== Panto row 1837 — Enroll360.Partner Code Mapping.convert (2.8-2 canary) ===");
        ParseLatencyReport report = ParseLatencyDiagnosticService.diagnose(sql, SQLPARSER_SQL_TREE_KEY);
        System.out.println(report.summary());
        assertUnderE3Timeout("Panto row 1837", report);
    }

    private static void assertUnderE3Timeout(String label, ParseLatencyReport report) {
        System.out.printf(
                "E3_GATE %s walkMs=%d totalMs=%d walkerFatal=%d%n",
                label,
                report.walkMs,
                report.totalMs,
                report.walkerFatalCount);
        assertTrue(
                "expected walk under 90s: " + label + " walkMs=" + report.walkMs,
                report.walkMs < PantoTimeoutCorpusE3GateTest.E3_TIMEOUT_MS);
        assertTrue(
                "expected total under 90s: " + label + " totalMs=" + report.totalMs,
                report.totalMs < PantoTimeoutCorpusE3GateTest.E3_TIMEOUT_MS);
    }

    private static void assertE2Gate(String label, String sql) {
        System.out.println("\n=== " + label + " ===");
        ParseLatencyReport report = ParseLatencyDiagnosticService.diagnose(sql, SQLPARSER_SQL_TREE_KEY);
        System.out.println(report.summary());
        if (report.walkMs > WARN_MS) {
            System.out.println("  *** SLOW WALK — likely O(n²) in event walker");
        }
        System.out.printf(
                "E2_GATE %s chars=%d lexMs=%d parseMs=%d walkMs=%d totalMs=%d walkerFatal=%d%n",
                label,
                report.querySizeChars,
                report.lexMs,
                report.parseMs,
                report.walkMs,
                report.totalMs,
                report.walkerFatalCount);
        assertEquals("expected no walker fatals: " + label, 0, report.walkerFatalCount);
        assertTrue(
                "expected walk under " + PANTO_ROW_475_E2_TIMEOUT_MS + "ms: " + label,
                report.walkMs < PANTO_ROW_475_E2_TIMEOUT_MS);
        assertTrue(
                "expected total under " + PANTO_ROW_475_E2_TIMEOUT_MS + "ms: " + label,
                report.totalMs < PANTO_ROW_475_E2_TIMEOUT_MS);
    }

    // ── Row 130: ALR Applicant transformation (large CASE expression) ─────────

    @Test
    public void pantoRow130_alrApplicant() {
        run("Panto row 130 — ALR Applicant (large CASE)", PANTO_ROW_130, SQLPARSER_SQL_TREE_KEY);
    }

    // ── Rows 4176/4177: MSU Bozeman fixed-width export (2.8-12 giant CASE bucket) ─

    /** Regression gate: bucket 2.8-12; production path must stay well under 90s kill. */
    private static final long PANTO_ROW_4176_ACCESS_WARN_MS = 10_000L;

    @Test
    public void pantoRow4176_msuBozemanApplyResponder() throws Exception {
        String sql = PantoOutstandingSqlFixtures.sqlForCsvRow(4176);
        run("Panto row 4176 — MSUBozeman.ApplyResponder (diagnostic)", sql, SQLPARSER_SQL_TREE_KEY);
        runProductionAccess("Panto row 4176 — MSUBozeman.ApplyResponder (SqlParserAccess)", sql);
    }

    @Test
    public void pantoRow4177_msuBozemanCultivateResponder() throws Exception {
        String sql = PantoOutstandingSqlFixtures.sqlForCsvRow(4177);
        run("Panto row 4177 — MSUBozeman.CultivateResponder (diagnostic)", sql, SQLPARSER_SQL_TREE_KEY);
        runProductionAccess("Panto row 4177 — MSUBozeman.CultivateResponder (SqlParserAccess)", sql);
    }

    private static void runProductionAccess(String label, String query) {
        System.out.println("\n=== " + label + " ===");
        long t0 = System.nanoTime();
        access.SqlParserAccess parserAccess = new access.SqlParserAccess(false, false, false);
        parserAccess.executeTheParse(query, SQLPARSER_SQL_TREE_KEY);
        long totalMs = (System.nanoTime() - t0) / 1_000_000L;
        access.Snippet snippet = parserAccess.getSnippet();
        int fatals = snippet == null ? -1 : snippet.getFatalErrorCount();
        System.out.printf(
                "accessTotalMs=%d  fatal=%d  tableDictKeys=%d%n",
                totalMs,
                fatals,
                snippet == null || snippet.getTableDictionary() == null ? -1 : snippet.getTableDictionary().size());
        if (totalMs > PANTO_ROW_4176_ACCESS_WARN_MS) {
            System.out.println("  *** SLOW ACCESS PATH — investigate parse prediction or walk");
        }
        if (fatals > 0) {
            System.out.println("  *** ACCESS PATH FATAL diagnostics");
        }
        org.junit.Assert.assertTrue("expected no FATAL diagnostics: " + label, fatals == 0);
        org.junit.Assert.assertTrue(
                "expected bound table in tableDictionary: " + label,
                snippet != null
                        && snippet.getTableDictionary() != null
                        && !snippet.getTableDictionary().isEmpty());
        org.junit.Assert.assertTrue(
                "expected access path under " + PANTO_ROW_4176_ACCESS_WARN_MS + "ms: " + label,
                totalMs < PANTO_ROW_4176_ACCESS_WARN_MS);
    }

    // ── Smoke test: tiny query, verifies the harness itself works ─────────────

    @Test
    public void smokeTest_tinyQuery() {
        run("smoke — trivial SELECT", "select 1 as x", SQLPARSER_SQL_TREE_KEY);
    }

    // ── Minimal LISTAGG probe: isolates LISTAGG + WITHIN GROUP lookahead ─────

    @Test
    public void probe_listaggWithoutWithinGroup() {
        String q = "select listagg(val, '|') as blnd from t group by id";
        run("probe — LISTAGG without WITHIN GROUP", q, SQLPARSER_SQL_TREE_KEY);
    }

    @Test
    public void probe_listaggWithWithinGroup() {
        String q = "select listagg(val, '|') within group (order by val) as blnd from t group by id";
        run("probe — LISTAGG with WITHIN GROUP", q, SQLPARSER_SQL_TREE_KEY);
    }

    // ── Minimal UNION probe: isolates set-op member prediction cost ───────────

    @Test
    public void probe_union10() {
        run("probe — 10-term UNION", buildUnion(10), SQLPARSER_SQL_TREE_KEY);
    }

    @Test
    public void probe_union50() {
        run("probe — 50-term UNION", buildUnion(50), SQLPARSER_SQL_TREE_KEY);
    }

    @Test
    public void probe_union250() {
        run("probe — 250-term UNION (same size as EAB.Country)", buildUnion(250), SQLPARSER_SQL_TREE_KEY);
    }

    // ── Phase 2.8 convert-egress probes (qualified FROM + ORDER BY; parse vs walk split) ──

    @Test
    public void probe_setOpConvertEgress_distinctVsShared_N50_M20_unionAll() {
        System.out.println("\n=== DISTINCT vs SHARED table mode (N=50 M=20 UNION ALL) ===");
        long distinctWalkMs = runWalkOnlyMillis(
                "distinct",
                SetOpTimingProbeFixtures.buildQuery(
                        "UNION ALL", 50, 20,
                        SetOpTimingProbeFixtures.orderByCountForSelectCount(20),
                        SetOpTimingProbeFixtures.BranchTableMode.DISTINCT_PER_BRANCH));
        long sharedWalkMs = runWalkOnlyMillis(
                "shared",
                SetOpTimingProbeFixtures.buildQuery(
                        "UNION ALL", 50, 20,
                        SetOpTimingProbeFixtures.orderByCountForSelectCount(20),
                        SetOpTimingProbeFixtures.BranchTableMode.SHARED_SINGLE_TABLE));
        double ratio = distinctWalkMs == 0L ? 0.0d : (sharedWalkMs * 1.0d) / distinctWalkMs;
        System.out.println(
                "TABLE_MODE_COMPARE N=50 M=20 UNION ALL"
                        + " distinctWalkMs=" + distinctWalkMs
                        + " sharedWalkMs=" + sharedWalkMs
                        + " sharedOverDistinct=" + String.format("%.2f", ratio));
    }

    private static long runWalkOnlyMillis(String label, String query) {
        ParseLatencyReport report = ParseLatencyDiagnosticService.diagnose(query, SQLPARSER_SQL_TREE_KEY);
        System.out.println(
                "TABLE_MODE_PROBE label=" + label
                        + " walkMs=" + report.walkMs
                        + " parseMs=" + report.parseMs
                        + " totalMs=" + report.totalMs);
        return report.walkMs;
    }

    @Test
    public void probe_setOpConvertEgress_distinctTables_N50_M20_unionAll() {
        run("probe — convert egress DISTINCT tables N=50 M=20 UNION ALL",
                SetOpTimingProbeFixtures.buildQuery(
                        "UNION ALL", 50, 20,
                        SetOpTimingProbeFixtures.orderByCountForSelectCount(20),
                        SetOpTimingProbeFixtures.BranchTableMode.DISTINCT_PER_BRANCH),
                SQLPARSER_SQL_TREE_KEY);
    }

    @Test
    public void probe_setOpConvertEgress_sharedTable_N50_M20_unionAll() {
        run("probe — convert egress SHARED table N=50 M=20 UNION ALL",
                SetOpTimingProbeFixtures.buildQuery(
                        "UNION ALL", 50, 20,
                        SetOpTimingProbeFixtures.orderByCountForSelectCount(20),
                        SetOpTimingProbeFixtures.BranchTableMode.SHARED_SINGLE_TABLE),
                SQLPARSER_SQL_TREE_KEY);
    }

    @Test
    public void probe_setOpConvertEgress_distinctTables_N50_M20_intersect() {
        run("probe — convert egress DISTINCT tables N=50 M=20 INTERSECT",
                SetOpTimingProbeFixtures.buildQuery(
                        "INTERSECT", 50, 20,
                        SetOpTimingProbeFixtures.orderByCountForSelectCount(20),
                        SetOpTimingProbeFixtures.BranchTableMode.DISTINCT_PER_BRANCH),
                SQLPARSER_SQL_TREE_KEY);
    }

    @Test
    public void probe_setOpConvertEgress_sharedTable_N50_M20_intersect() {
        run("probe — convert egress SHARED table N=50 M=20 INTERSECT",
                SetOpTimingProbeFixtures.buildQuery(
                        "INTERSECT", 50, 20,
                        SetOpTimingProbeFixtures.orderByCountForSelectCount(20),
                        SetOpTimingProbeFixtures.BranchTableMode.SHARED_SINGLE_TABLE),
                SQLPARSER_SQL_TREE_KEY);
    }

    private static String buildUnion(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if (i > 0) sb.append(" union\n");
            sb.append("select '").append(i).append("' as val, '").append(i * 2).append("' as val2");
        }
        return sb.toString();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Query fixtures — trimmed from parse/docs/rmcp-handoff/5.1.3-panto-outstanding/panto-513-parse-timeouts-2026-08-19.md
    // Row 28 and 130 embedded here; row 475 loads from sql/csv-row-475.sql via PantoOutstandingSqlFixtures.
    // ═════════════════════════════════════════════════════════════════════════

    // Row 28 — PDP_Acquia_Export (full text)
    private static final String PANTO_ROW_28 =
        "  select distinct\n" +
        "NULLIF(TRIM(Con.first_name), '') as firstname,\n" +
        "NULLIF(TRIM(Con.last_name), '') as lastname,\n" +
        "TRIM(Con.<Email Address>) as email,\n" +
        "NULLIF(TRIM(SCCA.ADDR_1), '') as address1,\n" +
        "NULLIF(TRIM(SCCA.ADDR_2), '') as address2,\n" +
        "NULLIF(TRIM(SCCA.<City>), '') as city,\n" +
        "NULLIF(TRIM(state_translation.eabstandard_value), '') as state,\n" +
        "TRIM(Case when INITCAP(SCCA.<Country>)='Unknown' or lower(SCCA.<Country>)='unmapped value' then NULL else SCCA.<Country> end ) as country,\n" +
        "TRIM(Case when Lower(SCCA.<Country>)='united states' then SCCA.ZIP5 else NULL end) as zipcode,\n" +
        "TRIM(SCCA.CREATE_DT) as CREATE_DTS,\n" +
        "TRIM(Con.<Phone Number>) as mobile,\n" +
        "TRIM(Con.<Distance from Campus>) as dist_from_campus_calc,\n" +
        "TRIM(AC.<Is Deleted>) as is_deleted,\n" +
        "TRIM(race_stdz.race) as race_stdzd,\n" +
        "TRIM(Con.<Ethnicity>) as ethnicity_stdzd,\n" +
        "null as religion_partner,\n" +
        "TRIM(ac_blnd.academic_interest_blnd) as academic_field_intrst_bln,\n" +
        "TRIM(Con.<Contact Key>) as eab_contact_id,\n" +
        "NULLIF(TRIM(Con.<Contact Type>), '')  as  eab_contact_type,\n" +
        "INITCAP(NULLIF(TRIM(Con.<Funnel Status Calc>), '')) as funnel_stage_calc,\n" +
        "TRIM(AC.<Student Application Status>) as student_app_status,\n" +
        "TRIM(AC.<Form Student First Name>) as form_student_first_name,\n" +
        "TRIM(AC.<Form Student Last Name>) as form_student_last_name,\n" +
        "TRIM(AC.<Form Parent First Name>) as form_parent_first_name,\n" +
        "TRIM(AC.<Form Parent Last Name>) as form_parent_last_name,\n" +
        "TRIM(AC.<Form Parent Email>) as form_parent_email,\n" +
        "TRIM(AC.<Form Referred By EAB ID>) as form_referred_by_EAB_ID,\n" +
        "TRIM(label.source_label)  as og_contact_source_calc,\n" +
        "TRIM(all_source.all_sources_blnd) as all_sources_blnd,\n" +
        "TRIM(AC.FORM_H_SCHOOL_NAME) as form_h_school_name,\n" +
        "TRIM(AC.FORM_H_SCHOOL_ADDR) as form_h_school_addr,\n" +
        "TRIM(AC.FORM_H_SCHOOL_CITY) as form_h_school_city,\n" +
        "TRIM(AC.FORM_H_SCHOOL_STATEPROV) as form_h_school_stateprov,\n" +
        "TRIM(AC.FORM_H_SCHOOL_POST_CODE) as form_h_school_post_code,\n" +
        "TRIM(AC.FORM_H_SCHOOL_COUNTRY) as form_h_school_country,\n" +
        "TRIM(AC.FORM_H_SCHOOL_GRAD_YEAR) as form_h_school_grad_year,\n" +
        "TRIM(AC.FORM_ENTRY_YEAR) as form_entry_year,\n" +
        "TRIM(Con.grade_level) as  h_school_grade_level_calc,\n" +
        "TRIM(max_values.sat_max) as max_sat_score_rng_calc,\n" +
        "TRIM(max_values.act_max) as max_act_score_rng_calc,\n" +
        "TRIM(max_values.psat_max) as max_psat_score_rng_calc,\n" +
        "TRIM(max_values.preact_max) as max_preact_score_rng_calc,\n" +
        "null as fafsa_submit_date_partner,\n" +
        "TRIM(Con.concontact_rand) as random_number,\n" +
        "TRIM(AC.FORM_BOT_HONEYPOT) as form_bot_honeypot,\n" +
        "CASE\n" +
        "    WHEN AC.ACS_REPLICATION_DATE IS NOT NULL THEN CONCAT(LEFT(REPLACE(TRIM(AC.ACS_REPLICATION_DATE), ' ', 'T'), 19), '+00:00')\n" +
        "    ELSE NULL\n" +
        "END as acs_replication_date,\n" +
        "TRIM(CAC.<Print Display ID>) as print_display_id,\n" +
        "TRIM(PD.school_panto_id ) as es_partner_id ,\n" +
        "TRIM(AC.FORM_ACADEMIC_FIELD_INTRS) as Form_Academic_Field_Intrs ,\n" +
        "TRIM(AC.FORM_EMAIL_UPDATE_CONTACT_KEY) as FORM_EMAIL_UPDATED_EAB_ID,\n" +
        "TRIM(AC.Form_Student_Addr) as FORM_STUDENT_ADDR,\n" +
        "TRIM(AC.Form_Student_City) as FORM_STUDENT_CITY,\n" +
        "TRIM(AC.Form_Student_Stateprov) as form_student_stateprov,\n" +
        "TRIM(AC.Form_Student_Post_Code) as form_student_post_code,\n" +
        "TRIM(AC.Form_Student_Country) as form_student_country,\n" +
        "TRIM(AC.Form_Sms_Opt_In) as form_sms_opt_in,\n" +
        "TRIM(AC.Engaged_Inq_Cmpgn_Acsl) as engaged_inq_cmpgn_acsl,\n" +
        "TRIM(AC.Engaged_Reeng_Cmpgn_Acsl) as engaged_reeng_cmpgn_acsl,\n" +
        "TRIM(parent.FIRST_NAME) as child_first_name_calc,\n" +
        "TRIM(parent.LAST_NAME) as child_last_name_calc,\n" +
        "TRIM(CASE WHEN Con.suppress_mail_indicator=1 THEN 'Y' else 'N' END) as do_not_direct_mail_calc,\n" +
        "Case\n" +
        "when  lower(optin_sms_indicator) = 'false' then 'false'\n" +
        "when  lower(optin_sms_indicator) = 'true' then 'true' else null end as sms_opt_in_calc,\n" +
        "TRIM(CASE WHEN Con.suppress_email_indicator=1 THEN 'TRUE' else 'FALSE' END) as dnc_email,\n" +
        "NULL as dnc_email_comment,\n" +
        "PHN.optin_sms_indicator,\n" +
        "parent.stu_hs_grade_level_calc as stu_hs_grade_level_calc,\n" +
        "TRIM(Con.graduation_year) as high_school_grad_year_calculated,\n" +
        "NULL as student_funnel_stage_calc,\n" +
        "INITCAP(parent.student_funnel_stage_calc) as stu_funnel_stage_calc,\n" +
        "TRIM(AC.<Student Application Status>) as  stu_eab_app_status,\n" +
        "TRIM(ac_blnd.academic_interest_blnd) as  stu_aca_field_intrst_blnd,\n" +
        "TRIM(race_stdz.race) as stu_race_stdzd,\n" +
        "TRIM(Con.<Ethnicity>) as stu_ethnicity_stdzd,\n" +
        "Null as stu_religion_partner,\n" +
        "parent.high_school_grad_year_calculated as  stu_hs_grad_year_calc,\n" +
        "ac.eng_app_par_gen_acsl as eng_app_par_gen_acsl,\n" +
        "ac.eng_app_stu_gen_acsl as eng_app_stu_gen_acsl,\n" +
        "ac.eng_app_stu_paper_acsl as eng_app_stu_paper_acsl,\n" +
        "ac.eng_app_stu_sub_acsl as eng_app_stu_sub_acsl,\n" +
        "ac.eng_cul_par_inq_acsl as eng_cul_par_inq_acsl,\n" +
        "ac.eng_cul_par_nur_acsl as eng_cul_par_nur_acsl,\n" +
        "ac.eng_cul_stu_inq_acsl as eng_cul_stu_inq_acsl,\n" +
        "ac.eng_cul_stu_paper_acsl as eng_cul_stu_paper_acsl,\n" +
        "ac.eng_cul_stu_nur_acsl as eng_cul_stu_nur_acsl,\n" +
        "ac.eng_cul_stu_reen_acsl as eng_cul_stu_reen_acsl,\n" +
        "ac.eng_cul_stu_j_reen_acsl as eng_cul_stu_j_reen_acsl,\n" +
        "ac.eng_cul_stu_sms_acsl as eng_cul_stu_sms_acsl,\n" +
        "ac.st_dt_app_par_gen_acsl as st_dt_app_par_gen_acsl,\n" +
        "ac.st_dt_app_stu_gen_acsl as st_dt_app_stu_gen_acsl,\n" +
        "ac.st_dt_app_stu_paper_acsl as st_dt_app_stu_paper_acsl,\n" +
        "ac.st_dt_app_stu_sub_acsl as st_dt_app_stu_sub_acsl,\n" +
        "ac.st_dt_cul_par_inq_acsl as st_dt_cul_par_inq_acsl,\n" +
        "ac.st_dt_cul_par_nur_acsl as st_dt_cul_par_nur_acsl,\n" +
        "ac.st_dt_cul_stu_inq_acsl as st_dt_cul_stu_inq_acsl,\n" +
        "ac.st_dt_cul_stu_paper_acsl as st_dt_cul_stu_paper_acsl,\n" +
        "ac.st_dt_cul_stu_nur_acsl as st_dt_cul_stu_nur_acsl,\n" +
        "ac.st_dt_cul_stu_j_reen_acsl as st_dt_cul_stu_j_reen_acsl,\n" +
        "ac.st_dt_cul_stu_reen_acsl as st_dt_cul_stu_reen_acsl,\n" +
        "ac.st_dt_cul_stu_sms_acsl as st_dt_cul_stu_sms_acsl\n" +
        "from\n" +
        "<[Partner_Data_Platform].[Contact]> as Con\n" +
        "Left join  <[Partner_Data_Platform].[Contact Address Clean]> as SCCA\n" +
        "  on Con.CLEAN_ADDR_KEY=SCCA.CLEAN_ADDR_KEY\n" +
        "Left join <[Partner_Data_Platform].[Contact Acquia Crosswalk]> CAC\n" +
        "  on Con.<Contact Key> = CAC.<Contact Key>\n" +
        "Left join <[Partner_Data_Platform].[ACS Contacts]> as AC\n" +
        "  on CAC.acs_contact_id = AC.acs_contact_id\n" +
        "LEFT JOIN  <[Partner_Data_Platform].[Phone]> as PHN\n" +
        "  ON PHN.phone_number=Con.<Phone Number>\n" +
        "left join  (\n" +
        "  select Con.FIRST_NAME , Con.LAST_NAME,Con.CONTACT_KEY from\n" +
        "  <[Partner_Data_Platform].[Contact]> as Con\n" +
        "  left join  <[Partner_Data_Platform].[Contact Relations]> as d\n" +
        "    on Con.CONTACT_KEY=d.student_contact_key\n" +
        "  where initcap(Con.<Contact Type>) = 'Parent/Guardian') as child_names\n" +
        "  on child_names.CONTACT_KEY=Con.CONTACT_KEY\n" +
        "left join (select CTS_PIVOT.contact_key as contact_key, max(CTS_PIVOT.sat_max) as sat_max,\n" +
        "  max(CTS_PIVOT.act_max) as act_max\n" +
        "  from (\n" +
        "    select CTS.<Contact Key> as contact_key,\n" +
        "      case when UPPER(CTS.<Test Type>)='SAT' then CTS.<Score Range Maximum> else null end as sat_max,\n" +
        "      case when UPPER(CTS.<Test Type>)='ACT' then CTS.<Score Range Maximum> else null end as act_max\n" +
        "    from <[Partner_Data_Platform].[Contact Test Scores]> as CTS ) as CTS_PIVOT\n" +
        "  group by CTS_PIVOT.contact_key) max_values\n" +
        "  on Con.<Contact Key> = max_values.contact_key\n" +
        "Left join (SELECT  a.contact_key, LISTAGG(a.<EAB standard Value>, '|') as academic_interest_blnd\n" +
        "  from ( select distinct b.contact_key, b.<EAB standard Value>,b.attribute_type\n" +
        "         from <[Partner_Data_Platform].[Contact Attributes Combined]> b ) a\n" +
        "  where Lower(a.attribute_type) = 'academic_interest'\n" +
        "  group by a.contact_key ) ac_blnd\n" +
        "  on Con.<Contact Key>=ac_blnd.contact_key\n" +
        "Left join\n" +
        "(SELECT  a.contact_key, a.source_contact_id, LISTAGG(a.<EAB standard Value>, '|') as race\n" +
        "  from ( select distinct b.contact_key, b.source_contact_id, b.<EAB standard Value>, b.attribute_type\n" +
        "         from <[Partner_Data_Platform].[Contact Attributes Combined]> b ) a\n" +
        "  where Lower(a.attribute_type) = 'race'\n" +
        "  group by a.contact_key, a.source_contact_id ) race_stdz\n" +
        "  on Con.<Contact Key>=race_stdz.contact_key\n" +
        "  and Con.active_source_contact_id=race_stdz.source_contact_id\n" +
        "Left Join (SELECT list_sources.contact_key, LISTAGG(list_sources.all_source_bln_list_agg, '|') AS all_sources_blnd\n" +
        "    FROM (SELECT contact_key,\n" +
        "                 CASE WHEN lower(intake_type_label) = 'prenames' THEN 'Migrated Contact' ELSE NULL END AS all_source_bln_list_agg\n" +
        "          FROM <[Partner_Data_Platform].[Contact Sourcecontacts]> AS CSCS\n" +
        "          LEFT JOIN pdp.crf__intake_type AS intake ON CSCS.source_type_key = intake.intake_type_key\n" +
        "          WHERE LOWER(CSCS.active_indicator)= 'true' AND lower(intake_type_label) = 'prenames'\n" +
        "          UNION\n" +
        "          SELECT distinct contact_key, source_label AS all_source_bln_list_agg\n" +
        "          FROM <[Partner_Data_Platform].[Contact Sourcecontacts]> AS CSCS\n" +
        "          INNER JOIN pdp.crf__source AS srctype ON CSCS.<Source Key> = srctype.source_key\n" +
        "          WHERE LOWER(CSCS.active_indicator)= 'true'\n" +
        "    ) AS list_sources\n" +
        "    GROUP BY list_sources.contact_key) as all_source\n" +
        "  on Con.<Contact Key>=all_source.contact_key\n" +
        "left join\n" +
        "( select con3.first_name, con3.last_name, con3.parent_contact_key, con3.student_contact_key,\n" +
        "         con3.stu_hs_grade_level_calc, con3.high_school_grad_year_calculated, con3.student_funnel_stage_calc\n" +
        "  from (\n" +
        "    select distinct con2.first_name, con2.last_name, con2.parent_contact_key, con2.student_contact_key,\n" +
        "           con2.stu_hs_grade_level_calc, con2.high_school_grad_year_calculated, con2.student_funnel_stage_calc,\n" +
        "           row_number() over (partition by con2.parent_contact_key order by con2.sourcecontact_intake_dt) as rankid2\n" +
        "    from (\n" +
        "      SELECT distinct rel.parent_contact_key, rel.student_contact_key, scst.create_date sourcecontact_intake_dt,\n" +
        "             con1.<Grade Level> as stu_hs_grade_level_calc,\n" +
        "             con1.<Graduation Year> as high_school_grad_year_calculated,\n" +
        "             con1.<First Name> as first_name, con1.<Last Name> as last_name,\n" +
        "             con1.<Funnel Status Calc> as student_funnel_stage_calc,\n" +
        "             row_number() over (partition by rel.parent_contact_key order by int.INTAKE_PRIORITY) as rankid\n" +
        "      FROM <[Partner_Data_Platform].[Contact]> as con\n" +
        "      inner join <[Partner_Data_Platform].[Contact Sourcecontacts]> as src\n" +
        "        on con.<Contact Key>=src.<Contact Key>\n" +
        "      inner join pdp.crf__intake_type int on int.intake_type_key=src.source_type_key\n" +
        "      inner join <[Partner_Data_Platform].[SourceContact SourceType]> scst on src.<Source Contact ID>=scst.<Source Contact ID>\n" +
        "      LEFT JOIN <[Partner_Data_Platform].[Contact Relations]> as rel on rel.parent_contact_key = con.contact_key\n" +
        "      LEFT JOIN <[Partner_Data_Platform].[Contact]> as con1 on rel.student_contact_key = con1.contact_key\n" +
        "      WHERE con.contact_type = 'Parent/Guardian'\n" +
        "    ) con2\n" +
        "    where rankid=1\n" +
        "  ) con3\n" +
        "  where rankid2=1) parent\n" +
        "  on Con.<Contact Key> = parent.parent_contact_key\n" +
        "LEFT JOIN\n" +
        "( SELECT sub_1.contact_key, sub_1.source_label as source_label\n" +
        "  FROM (\n" +
        "    SELECT ROW_NUMBER() OVER(PARTITION BY contact_key ORDER BY CSCTS.<Create Date>) as row_id,\n" +
        "           CSCTS.contact_key, srctype.source_label\n" +
        "    from <[Partner_Data_Platform].[Contact Sourcecontacts]> as  CSCTS\n" +
        "    INNER JOIN pdp.crf__source AS srctype ON CSCTS.<Source Key> = srctype.source_key\n" +
        "  ) as sub_1\n" +
        "  WHERE row_id = 1) label\n" +
        "  ON Con.<Contact Key>=label.contact_key\n" +
        "inner join <[Partner_Data_Platform].[Partner Details]> as PD\n" +
        "where\n" +
        "  ( (TRIM(label.source_label) is not null\n" +
        "     AND TRIM(all_source.all_sources_blnd) is not null)\n" +
        "  ) and lower(Con.<Email Address>) not like '%test.eab.com'\n" +
        "  OR\n" +
        "  (NULLIF(TRIM(Con.<Contact Type>), '') IN ('Deliverability Seed','Ride Along')\n" +
        "   AND TRIM(all_source.all_sources_blnd) is null)";

    // Row 130 — ALR Applicant (large CASE with ~400 WHEN branches)
    private static final String PANTO_ROW_130 =
        "select\n" +
        "CASE WHEN ADMIT_SOURCE_VALUE IN ('Y') THEN 1 ELSE 0 END ADMIT_INDICATOR\n" +
        ",CASE WHEN LEFT(ENTRY_TERM_SOURCE_VALUE,4) >= 2021 AND INQUIRY_SOURCE_VALUE = 'UG' AND STUDENT_TYPE_SOURCE_VALUE IN ('A','D') THEN 1 ELSE 0 END INCLUDE_IN_ANALYSIS\n" +
        ",CASE WHEN INQUIRY_SOURCE_VALUE = 'UG' AND APPLICATION_SUBMIT_DATE IS NOT NULL THEN 'PAPPL' ELSE NULL END AUDIENCE_CODE\n" +
        ",CASE WHEN LENGTH(REGEXP_REPLACE(CELL_PHONE,'[^0-9]','')) = 10 THEN REGEXP_REPLACE(CELL_PHONE,'[^0-9]','') ELSE NULL END CELL_PHONE\n" +
        ",CASE WHEN COUNTRY_NAME = '157' THEN 'USA' ELSE NULL END COUNTRY_NAME\n" +
        ",CASE WHEN LEFT(DENIED_SOURCE_VALUE,1) = 'V' THEN 1 ELSE DEFER_INDICATOR END DEFER_INDICATOR\n" +
        ",CASE WHEN DENIED_SOURCE_VALUE = 'Y' THEN 1 ELSE DENIED_INDICATOR END DENIED_INDICATOR\n" +
        ",CASE WHEN ENROLLED_SOURCE_VALUE = 'Y' AND ADMIT_SOURCE_VALUE = 'Y' THEN 1 ELSE ENROLLED_INDICATOR END ENROLLED_INDICATOR\n" +
        ",CASE\n" +
        " WHEN RIGHT(ENTRY_TERM_SOURCE_VALUE,2) = '40' THEN 'Fall'\n" +
        " WHEN RIGHT(ENTRY_TERM_SOURCE_VALUE,2) = '35' THEN 'Summer'\n" +
        " WHEN RIGHT(ENTRY_TERM_SOURCE_VALUE,2) = '70' THEN 'Spring'\n" +
        " ELSE NULL END ENTRY_TERM\n" +
        ",CASE WHEN GENDER_SOURCE_VALUE IS NOT NULL THEN GENDER_SOURCE_VALUE ELSE GENDER END GENDER\n" +
        ",TO_NUMBER(CASE WHEN HISPANIC_LATINO_SOURCE_VALUE IS NOT NULL THEN HISPANIC_LATINO_SOURCE_VALUE ELSE HISPANIC_LATINO END) HISPANIC_LATINO\n" +
        ",CASE\n" +
        " WHEN school_program = 'AAADULTINSTR' THEN 'AA Adult Instruction/Training'\n" +
        " WHEN school_program = 'AAAPPLIEDCHE' THEN 'AA Applied Chemistry'\n" +
        " WHEN school_program = 'AABUSADM' THEN 'AA Business Administration'\n" +
        " WHEN school_program = 'BABIOLOGY' THEN 'BA Biology'\n" +
        " WHEN school_program = 'BACHEMISTRY' THEN 'BA Chemistry'\n" +
        " WHEN school_program = 'BAENGLISH' THEN 'BA English'\n" +
        " WHEN school_program = 'BAHISTORY' THEN 'BA History'\n" +
        " WHEN school_program = 'BAMATH' THEN 'BA Mathematics'\n" +
        " WHEN school_program = 'BAMUSIC' THEN 'BA Music'\n" +
        " WHEN school_program = 'BAPSYC' THEN 'BA Psychology'\n" +
        " WHEN school_program = 'BASPANISH' THEN 'BA Spanish'\n" +
        " WHEN school_program = 'BBAMKTG' THEN 'BBA Marketing'\n" +
        " WHEN school_program = 'BBAMGMT' THEN 'BBA Management'\n" +
        " WHEN school_program = 'BBAFINANCE' THEN 'BBA Finance'\n" +
        " WHEN school_program = 'BSCRIMJUST' THEN 'BS Criminal Justice'\n" +
        " WHEN school_program = 'BSMATH' THEN 'BS Mathematics'\n" +
        " WHEN school_program = 'BSBIOLOGY' THEN 'BS Biology'\n" +
        " WHEN school_program = 'MBA989031' THEN 'MBA Business'\n" +
        " WHEN school_program = 'MSACCOUNTING' THEN 'MS Accounting'\n" +
        " WHEN school_program = 'MSBIOLOGY' THEN 'MS Biology'\n" +
        " ELSE SCHOOL_PROGRAM END SCHOOL_PROGRAM\n" +
        ",CASE WHEN LEN(STATE) = 2 AND LEN(REGEXP_REPLACE(LEFT(ZIP5,5),'[^0-9]','')) = 5 THEN STATE ELSE NULL END STATE\n" +
        ",CASE WHEN LEN(STATE) = 2 AND LEN(REGEXP_REPLACE(LEFT(ZIP5,5),'[^0-9]','')) = 5 THEN LEFT(ZIP5,5) ELSE NULL END ZIP5\n" +
        "from <[ALR].[Applicant]> as a";
}
