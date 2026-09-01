# PSS 5.1.3 outstanding issues — compact index

Companion to `panto_513_outstanding_issues.csv` (82 rows: 74 timeouts + 8 degradations). Full SQL is in that CSV (`query_sql`). Do **not** copy the 6,680-row Panto extract.

Compare baseline **5.0.0-3** vs **5.1.3**, `SQL` endpoint, 90s per-parser abort.

## Degradations (8) — **all closed** (2026-09-01, Phase 2.9)

| CSV row | Domain | Entity | Query name | Status |
| --- | --- | --- | --- | --- |
| 583 | ALR | Student | ECRM.student | **Closed (2.9-A)** — CTE `table_alias` policy |
| 2139 | Enroll360 | Student | ECRM.student | **Closed (2.9-A)** — same |
| 3150 | Enroll360 | Student Race | INACTIVE_Enroll360.Student Race.final_v2 | **Closed (2.9-D)** — set-op FATALs canonical |
| 3870 | Enroll360 | Student Term PDP Delivery | Enroll360.Student_Term_PDP_Delivery_IDXwalkTesting_20251020 | **Closed (2.9-B)** — CTE `table_alias` policy |
| 4648 | Foundation | Student Academic Summary Intermediate | old Updated INFA … Query | **Closed (2.9-C)** — CTE `table_alias` policy |
| 4726 | Foundation | Student Academic Summary Intermediate | Updated INFA … Query | **Closed (2.9-C)** — same |
| 5410 | Partner_Data_Platform | Cappex Contacts | src_intake_cappex_contacts_PDPv0.2 | **Closed (2.9-F)** — set-op FATALs canonical |
| 5455 | Partner_Data_Platform | Contact | con_contact_assigned_chosen_contacts | **Closed (2.9-E)** — CTE + tuple policy |

Policy: [global-table-dictionary-cte-alias-policy.md](../../../documents/global-table-dictionary-cte-alias-policy.md), [set-operation-interface-duplicate-output-names-policy.md](../../../documents/set-operation-interface-duplicate-output-names-policy.md).

## 5.1.3 timeouts (74)

5.0.0-3 finished; 5.1.3 was killed at ~90s with no payload.

| CSV row | Domain | Entity | Query name | 5.0.0-3 ms | 5.1.3 ms (aborted) |
| --- | --- | --- | --- | --- | --- |
| 28 | Acquia | PDP_Acquia_Contact | PDP_Acquia_Export | 1750 | 90002 |
| 30 | Acquia | PDP_Acquia_Contact | PDP_Acquia_Export | 1448 | 90001 |
| 31 | Acquia | PDP_Acquia_Contact | PDP_Acquia_Export | 1733 | 90000 |
| 32 | Acquia | PDP_Acquia_Contact | PDP_Acquia_Export | 1738 | 90002 |
| 41 | Acquia | PDP_Acquia_Contact | PDP_Acquia_Export_v2 | 1844 | 90000 |
| 130 | ALR | Applicant | transformation_query_applicants | 20107 | 90002 |
| 314 | ALR | Inquiry | transformation_query_inquiry | 1914 | 90003 |
| 315 | ALR | Inquiry | transformation_query_inquiry | 1825 | 90001 |
| 475 | ALR | Partner Code Mapping | EAB.Country | 7416 | 89997 |
| 476 | ALR | Partner Code Mapping | EAB.Country | 7120 | 89992 |
| 605 | ALR | Student Address | ALR.Student Address.final | 948 | 90002 |
| 606 | ALR | Student Address | ALR.Student Address.final | 878 | 89999 |
| 623 | ALR | Student Address | ALR.Student Address.final.BryanCollege.ExcludeAdultHistorical | 878 | 90002 |
| 635 | ALR | Student Address | EHC.ALR.Student Address.final | 882 | 90002 |
| 636 | ALR | Student Address | Goshen_ALR.Student Address.final | 1168 | 90001 |
| 652 | ALR | student_attributes | ALR.Student Attributes.Final | 420 | 90002 |
| 1436 | AMS | donor_attributes | par_intake_donor_attributes_AMS_V2.0 | 781 | 90004 |
| 1439 | AMS | donor_attributes | par_intake_donor_attributes_AMS_V2.0 | 231 | 90001 |
| 1467 | AMS | donor_attributes | par_intake_donor_attributes_AMS_V2.0 | 548 | 90006 |
| 1814 | Enroll360 | Census Student Term Attributes | Enroll360.Partner Processed Census Student Term Attributes.final | 2700 | 90001 |
| 1819 | Enroll360 | Partner Code Mapping | DataOrgPilot.Enroll360.Partner Code Mapping.convert_sis | 2417 | 90001 |
| 1820 | Enroll360 | Partner Code Mapping | DataOrgPilot.Enroll360.Partner Code Mapping.convert_sis.tenant_v2 | 2296 | 90002 |
| 1821 | Enroll360 | Partner Code Mapping | DataOrgPilot.Enroll360.Partner Code Mapping.convert_sis.Tenant.v2 | 2328 | 90002 |
| 1827 | Enroll360 | Partner Code Mapping | EAB.Country | 7111 | 90003 |
| 1828 | Enroll360 | Partner Code Mapping | EAB.Country | 7106 | 90001 |
| 1837 | Enroll360 | Partner Code Mapping | Enroll360.Partner Code Mapping.convert | 1684 | 90001 |
| 1838 | Enroll360 | Partner Code Mapping | Enroll360.Partner Code Mapping.convert | 1604 | 90001 |
| 1839 | Enroll360 | Partner Code Mapping | Enroll360.Partner Code Mapping.convert | 1635 | 90002 |
| 1851 | Enroll360 | Partner Code Mapping | Enroll360.Partner Code Mapping.convert | 1691 | 90002 |
| 1860 | Enroll360 | Partner Code Mapping | Enroll360.Partner Code Mapping.convert_CRM_pilot_v1 | 1890 | 90001 |
| 1890 | Enroll360 | Partner Code Mapping | Enroll360.Partner Code Mapping.convert_sis | 2450 | 90001 |
| 1891 | Enroll360 | Partner Code Mapping | Enroll360.Partner Code Mapping.convert_sis | 2672 | 89981 |
| 1897 | Enroll360 | Partner Code Mapping | Enroll360.Partner Code Mapping.convert_tenant | 1656 | 89982 |
| 1927 | Enroll360 | Partner Code Mapping | Enroll360.Partner Code Mapping.convert_ug_applicants | 1912 | 90001 |
| 1933 | Enroll360 | Partner Code Mapping | Enroll360.Partner Code Mapping.convert_ug_applicants_v2 | 1864 | 90000 |
| 1957 | Enroll360 | Partner Code Mapping | Enroll360.Partner Code Mapping.convert_ug_inquiries | 1814 | 90001 |
| 1986 | Enroll360 | Partner Code Mapping | INACTIVE_DataOrgPilot.Enroll360.Partner Code Mapping.convert_sis.Tenant.v1 | 2336 | 90000 |
| 1987 | Enroll360 | Partner Code Mapping | INACTIVE_DataOrgPilot.Enroll360.Partner Code Mapping.convert_sis.wStudentType | 2448 | 90000 |
| 1998 | Enroll360 | Partner Code Mapping | INACTIVE_Enroll360.Partner Code Mapping.convert | 1681 | 90000 |
| 2001 | Enroll360 | Partner Code Mapping | INACTIVE_Enroll360.Partner Code Mapping.convert | 1705 | 90001 |
| 2031 | Enroll360 | Partner Code Mapping | INACTIVE_Enroll360.Partner Code Mapping.convert_sis_pilot_v1 | 2352 | 89999 |
| 2054 | Enroll360 | Partner Code Mapping | INACTIVE_Enroll360.Partner Code Mapping.convert_ug_applicants | 1697 | 90000 |
| 2110 | Enroll360 | Partner Configurations | Enroll360.Project Atlas Migration Checks.prelim_stud_data_sliced | 296 | 90001 |
| 2116 | Enroll360 | Partner Configurations | Enroll360.Project Atlas Migration Checks.st_sta_data | 585 | 90000 |
| 2117 | Enroll360 | Partner Configurations | Enroll360.Project Atlas Migration Checks.st_sta_data_undeduped_apps | 563 | 90001 |
| 2120 | Enroll360 | Partner Configurations | Enroll360.Project Atlas Migration Checks.stud_race_eth | 503 | 90001 |
| 2201 | Enroll360 | Student | Enroll360.Student.final.multifile Bill test | 3097 | 90024 |
| 2325 | Enroll360 | Student | INACTIVE_Enroll360.Student.final | 2276 | 90001 |
| 2352 | Enroll360 | Student | INACTIVE_Enroll360.Student.final.multifile_v4 | 2899 | 90002 |
| 2444 | Enroll360 | Student | XWALK_TEST_Enroll360.Student.final.multifile.tenant | 2899 | 90002 |
| 4157 | Enroll360 | Student Year Funds | DataOrgPilot.Enroll360.StudentYearFundsLogicTesting | 958 | 90003 |
| 4158 | Enroll360 | Student Year Funds | DataOrgPilot.Enroll360.StudentYearFundsLogicTesting | 1149 | 90002 |
| 4163 | Enroll360 | Student Year Funds | FundAmountLogicTesting_Enroll360.Student Year Funds.final | 1456 | 90003 |
| 4164 | Enroll360 | Student Year Funds | funds logic testing | 915 | 89999 |
| 4170 | Enroll360 | Student Year Funds | TESTING_DataOrgPilot.Enroll360.StudentYearFundsLogicTesting | 891 | 90002 |
| 4171 | Enroll360 | Student Year Funds | TESTING_DataOrgPilot.Enroll360.StudentYearFundsLogicTestingDT | 883 | 90002 |
| 4176 | Enroll360 | Test | MSUBozeman.ApplyResponder | 3722 | 90002 |
| 4177 | Enroll360 | Test | MSUBozeman.CultivateResponder | 3326 | 90001 |
| 4197 | enrollment_services | Entering Class | Migration checks fulfillment counts | 719 | 90002 |
| 4647 | Foundation | Student Academic Summary Intermediate | Old STD | 323 | 90002 |
| 4725 | Foundation | Student Academic Summary Intermediate | Updated INFA Student Academic Summary Intermediate_m__st_student_term_downfill_backfill__ Query | 303 | 90001 |
| 5216 | Pagoda | COURSE | colleague_cat_course | 537 | 90003 |
| 5261 | Pagoda | SECTION | colleague_cat_section | 428 | 90001 |
| 5453 | Partner_Data_Platform | Contact | con_contact_assigned_cappex_common_format | 805 | 90002 |
| 5454 | Partner_Data_Platform | Contact | con_contact_assigned_cappex_common_format | 560 | 90001 |
| 5592 | Partner_Data_Platform | custom contacts | src_intake_custom_contacts_PDPv0.2 | 502 | 90001 |
| 5593 | Partner_Data_Platform | custom contacts | src_intake_custom_contacts_PDPv0.2 | 502 | 90001 |
| 5594 | Partner_Data_Platform | custom contacts | src_intake_custom_contacts_PDPv0.2_test | 507 | 90002 |
| 5860 | PDP_ALR_V2 | par__student | par_intake_student_last_validated_PDP_ALR_V2_v2.0 | 767 | 90002 |
| 5861 | PDP_ALR_V2 | par__student | par_intake_student_PDP_ALR_V2_v2.0 | 505 | 90001 |
| 5862 | PDP_ALR_V2 | par__student | par_intake_student_PDP_ALR_V2_v2.0 | 507 | 90002 |
| 5863 | PDP_ALR_V2 | par__student | par_intake_student_PDP_ALR_V2_v2.0 | 750 | 90001 |
| 6025 | PDP_AMS | rsc_partner_donors | pdp_ams_rsc_donor_intake | 1354 | 90002 |
| 6542 | Program Assistant | Inquiry Pool Lead | Hofstra  YouVisit INQ International | 420 | 90003 |
