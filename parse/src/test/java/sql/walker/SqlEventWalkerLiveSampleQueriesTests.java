package sql.walker;
import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

public class SqlEventWalkerLiveSampleQueriesTests extends AbstractSqlParseEventWalkerTest {

	@Test
	public void realisticCastingTest() {
		final String query = "select distinct" + 
				"        cast(s.school_id as varchar(100)) as school_id" + 
				"      , cast(s.school_key as varchar(50))  as school_ceeb_code" + 
				"      , cast(case when schl_type.value is null or schl_type.value = ''" + 
				"          then d.dataset_name else schl_type.value end as varchar(100)) as school_type" + 
				"      , cast(case when schl_cat.value is null or schl_cat.value = ''" + 
				"          then d.dataset_name else schl_cat.value end as varchar(100)) as school_category" + 
				"      , cast(case when s.school_name is null or s.school_name = '' " + 
				"          then sl.lookup_school_name else s.school_name end as varchar(100)) as school_name" + 
				"      , cast(case when s.school_country is null or s.school_country = ''" + 
				"          then a.address_country else s.school_country end as varchar(100)) as school_country" + 
				"      , cast(case when s.school_region is null or s.school_region = ''" + 
				"          then a.address_region else s.school_region end as varchar(50)) as school_state" + 
				"      , cast(case when s.school_city is null or s.school_city = ''" + 
				"          then a.address_city else s.school_city end as varchar(255)) school_city" + 
				"      , cast(a.address_county as varchar(100)) as school_county" + 
				"      , cast(a.address_zip as varchar(100)) as school_zip" + 
				"      , cast(a.address_street as varchar(1000)) as school_address" + 
				"      , cast(dr.dataset_row_created as timestamp) as crm_created_at" + 
				"      , cast(dr.dataset_row_updated as timestamp) as crm_updated_at" + 
				"  from school s" + 
				"    left join <slate_lookup_school> sl" + 
				"      on sl.lookup_school_id = s.school_key" + 
				"    left join <slate_dataset_row> dr" + 
				"      on dr.dataset_row_key = s.school_key" + 
				"    left join <slate_dataset> d" + 
				"      on d.dataset_id = dr.dataset_row_dataset" + 
				"      and d.dataset_name = 'Organizations'" + 
				"    left join <slate_address> a" + 
				"      on a.address_record = dr.dataset_row_id" + 
				"      and a.address_rank_overall = 1" + 
				"    left join schl_type" + 
				"      on schl_type.field_record = dr.dataset_row_id" + 
				"    left join schl_cat" + 
				"        on schl_cat.field_record = dr.dataset_row_id"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={11={function={function_name=cast, data_type={length=1000, type=VARCHAR}, type=CAST, value={column={name=address_street, table_ref=a}}}, alias=school_address}, 12={function={function_name=cast, data_type={type=TIMESTAMP}, type=CAST, value={column={name=dataset_row_created, table_ref=dr}}}, alias=crm_created_at}, 13={function={function_name=cast, data_type={type=TIMESTAMP}, type=CAST, value={column={name=dataset_row_updated, table_ref=dr}}}, alias=crm_updated_at}, 1={function={function_name=cast, data_type={length=100, type=VARCHAR}, type=CAST, value={column={name=school_id, table_ref=s}}}, alias=school_id}, 2={function={function_name=cast, data_type={length=50, type=VARCHAR}, type=CAST, value={column={name=school_key, table_ref=s}}}, alias=school_ceeb_code}, 3={function={function_name=cast, data_type={length=100, type=VARCHAR}, type=CAST, value={case={clauses={1={then={column={name=dataset_name, table_ref=d}}, when={or={1={condition={left={column={name=value, table_ref=schl_type}}, operator=is null}}, 2={condition={left={column={name=value, table_ref=schl_type}}, right={literal=''}, operator==}}}}}}, else={column={name=value, table_ref=schl_type}}}}}, alias=school_type}, 4={function={function_name=cast, data_type={length=100, type=VARCHAR}, type=CAST, value={case={clauses={1={then={column={name=dataset_name, table_ref=d}}, when={or={1={condition={left={column={name=value, table_ref=schl_cat}}, operator=is null}}, 2={condition={left={column={name=value, table_ref=schl_cat}}, right={literal=''}, operator==}}}}}}, else={column={name=value, table_ref=schl_cat}}}}}, alias=school_category}, 5={function={function_name=cast, data_type={length=100, type=VARCHAR}, type=CAST, value={case={clauses={1={then={column={name=lookup_school_name, table_ref=sl}}, when={or={1={condition={left={column={name=school_name, table_ref=s}}, operator=is null}}, 2={condition={left={column={name=school_name, table_ref=s}}, right={literal=''}, operator==}}}}}}, else={column={name=school_name, table_ref=s}}}}}, alias=school_name}, 6={function={function_name=cast, data_type={length=100, type=VARCHAR}, type=CAST, value={case={clauses={1={then={column={name=address_country, table_ref=a}}, when={or={1={condition={left={column={name=school_country, table_ref=s}}, operator=is null}}, 2={condition={left={column={name=school_country, table_ref=s}}, right={literal=''}, operator==}}}}}}, else={column={name=school_country, table_ref=s}}}}}, alias=school_country}, 7={function={function_name=cast, data_type={length=50, type=VARCHAR}, type=CAST, value={case={clauses={1={then={column={name=address_region, table_ref=a}}, when={or={1={condition={left={column={name=school_region, table_ref=s}}, operator=is null}}, 2={condition={left={column={name=school_region, table_ref=s}}, right={literal=''}, operator==}}}}}}, else={column={name=school_region, table_ref=s}}}}}, alias=school_state}, 8={function={function_name=cast, data_type={length=255, type=VARCHAR}, type=CAST, value={case={clauses={1={then={column={name=address_city, table_ref=a}}, when={or={1={condition={left={column={name=school_city, table_ref=s}}, operator=is null}}, 2={condition={left={column={name=school_city, table_ref=s}}, right={literal=''}, operator==}}}}}}, else={column={name=school_city, table_ref=s}}}}}, alias=school_city}, 9={function={function_name=cast, data_type={length=100, type=VARCHAR}, type=CAST, value={column={name=address_county, table_ref=a}}}, alias=school_county}, 10={function={function_name=cast, data_type={length=100, type=VARCHAR}, type=CAST, value={column={name=address_zip, table_ref=a}}}, alias=school_zip}}, qualifier=distinct, from={join={11={table={alias=null, table=schl_type}}, 12={join=left, on={condition={left={column={name=field_record, table_ref=schl_cat}}, right={column={name=dataset_row_id, table_ref=dr}}, operator==}}}, 13={table={alias=null, table=schl_cat}}, 1={table={alias=s, table=school}}, 2={join=left, on={condition={left={column={name=lookup_school_id, table_ref=sl}}, right={column={name=school_key, table_ref=s}}, operator==}}}, 3={table={alias=sl, substitution={name=<slate_lookup_school>, type=tuple}}}, 4={join=left, on={condition={left={column={name=dataset_row_key, table_ref=dr}}, right={column={name=school_key, table_ref=s}}, operator==}}}, 5={table={alias=dr, substitution={name=<slate_dataset_row>, type=tuple}}}, 6={join=left, on={and={1={condition={left={column={name=dataset_id, table_ref=d}}, right={column={name=dataset_row_dataset, table_ref=dr}}, operator==}}, 2={condition={left={column={name=dataset_name, table_ref=d}}, right={literal='Organizations'}, operator==}}}}}, 7={table={alias=d, substitution={name=<slate_dataset>, type=tuple}}}, 8={join=left, on={and={1={condition={left={column={name=address_record, table_ref=a}}, right={column={name=dataset_row_id, table_ref=dr}}, operator==}}, 2={condition={left={column={name=address_rank_overall, table_ref=a}}, right={literal=1}, operator==}}}}}, 9={table={alias=a, substitution={name=<slate_address>, type=tuple}}}, 10={join=left, on={condition={left={column={name=field_record, table_ref=schl_type}}, right={column={name=dataset_row_id, table_ref=dr}}, operator==}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[school_type, crm_created_at, school_name, school_city, school_category, school_state, school_id, school_country, school_ceeb_code, school_address, school_county, crm_updated_at, school_zip]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<slate_lookup_school>=tuple, <slate_address>=tuple, <slate_dataset>=tuple, <slate_dataset_row>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{schl_type={value=[[@34,154:162='schl_type',<381>,1:154], [@40,181:189='schl_type',<381>,1:181], [@50,236:244='schl_type',<381>,1:236]], field_record=[[@357,1828:1836='schl_type',<381>,1:1828]]}, school={school_id=[[@4,28:28='s',<381>,1:28]], school_country=[[@133,625:625='s',<381>,1:625], [@139,653:653='s',<381>,1:653], [@149,712:712='s',<381>,1:712]], school_name=[[@100,467:467='s',<381>,1:467], [@106,492:492='s',<381>,1:492], [@116,553:553='s',<381>,1:553]], school_city=[[@199,948:948='s',<381>,1:948], [@205,973:973='s',<381>,1:973], [@215,1026:1026='s',<381>,1:1026]], school_region=[[@166,790:790='s',<381>,1:790], [@172,817:817='s',<381>,1:817], [@182,874:874='s',<381>,1:874]], school_key=[[@18,82:82='s',<381>,1:82], [@302,1475:1475='s',<381>,1:1475], [@314,1553:1553='s',<381>,1:1553]]}, <slate_lookup_school>={lookup_school_name=[[@112,526:527='sl',<381>,1:526]], lookup_school_id=[[@298,1453:1454='sl',<381>,1:1453]]}, <slate_address>={address_county=[[@229,1085:1085='a',<381>,1:1085]], address_record=[[@340,1724:1724='a',<381>,1:1724]], address_region=[[@178,852:852='a',<381>,1:852]], address_street=[[@257,1205:1205='a',<381>,1:1205]], address_country=[[@145,689:689='a',<381>,1:689]], address_rank_overall=[[@348,1770:1770='a',<381>,1:1770]], address_zip=[[@243,1148:1148='a',<381>,1:1148]], address_city=[[@211,1006:1006='a',<381>,1:1006]]}, schl_cat={value=[[@67,310:317='schl_cat',<381>,1:310], [@73,336:343='schl_cat',<381>,1:336], [@83,390:397='schl_cat',<381>,1:390]], field_record=[[@368,1903:1910='schl_cat',<381>,1:1903]]}, <slate_dataset>={dataset_id=[[@322,1605:1605='d',<381>,1:1605]], dataset_name=[[@46,216:216='d',<381>,1:216], [@79,370:370='d',<381>,1:370], [@330,1652:1652='d',<381>,1:1652]]}, <slate_dataset_row>={dataset_row_created=[[@271,1270:1271='dr',<381>,1:1270]], dataset_row_id=[[@344,1743:1744='dr',<381>,1:1743], [@361,1853:1854='dr',<381>,1:1853], [@372,1927:1928='dr',<381>,1:1927]], dataset_row_updated=[[@282,1337:1338='dr',<381>,1:1337]], dataset_row_key=[[@310,1532:1533='dr',<381>,1:1532]], dataset_row_dataset=[[@326,1620:1621='dr',<381>,1:1620]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={school_type=[[@61,276:286='school_type',<381>,1:276]], crm_created_at=[[@278,1310:1323='crm_created_at',<381>,1:1310]], school_name=[[@127,591:601='school_name',<381>,1:591]], school_city=[[@225,1061:1071='school_city',<381>,1:1061]], school_category=[[@94,429:443='school_category',<381>,1:429]], school_state=[[@193,913:924='school_state',<381>,1:913]], school_id=[[@14,60:68='school_id',<381>,1:60]], school_country=[[@160,753:766='school_country',<381>,1:753]], school_ceeb_code=[[@28,115:130='school_ceeb_code',<381>,1:115]], school_address=[[@267,1243:1256='school_address',<381>,1:1243]], school_county=[[@239,1122:1134='school_county',<381>,1:1122]], crm_updated_at=[[@289,1377:1390='crm_updated_at',<381>,1:1377]], school_zip=[[@253,1182:1191='school_zip',<381>,1:1182]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={school_type=[[@61,276:286='school_type',<381>,1:276]], crm_created_at=[[@278,1310:1323='crm_created_at',<381>,1:1310]], school_name=[[@127,591:601='school_name',<381>,1:591]], school_city=[[@225,1061:1071='school_city',<381>,1:1061]], school_category=[[@94,429:443='school_category',<381>,1:429]], school_state=[[@193,913:924='school_state',<381>,1:913]], school_id=[[@14,60:68='school_id',<381>,1:60]], school_country=[[@160,753:766='school_country',<381>,1:753]], school_ceeb_code=[[@28,115:130='school_ceeb_code',<381>,1:115]], school_address=[[@267,1243:1256='school_address',<381>,1:1243]], school_county=[[@239,1122:1134='school_county',<381>,1:1122]], crm_updated_at=[[@289,1377:1390='crm_updated_at',<381>,1:1377]], school_zip=[[@253,1182:1191='school_zip',<381>,1:1182]]}, table_dictionary={schl_type={value=[[@34,154:162='schl_type',<381>,1:154], [@40,181:189='schl_type',<381>,1:181], [@50,236:244='schl_type',<381>,1:236]], field_record=[[@357,1828:1836='schl_type',<381>,1:1828]]}, school={school_id=[[@4,28:28='s',<381>,1:28]], school_country=[[@133,625:625='s',<381>,1:625], [@139,653:653='s',<381>,1:653], [@149,712:712='s',<381>,1:712]], school_name=[[@100,467:467='s',<381>,1:467], [@106,492:492='s',<381>,1:492], [@116,553:553='s',<381>,1:553]], school_city=[[@199,948:948='s',<381>,1:948], [@205,973:973='s',<381>,1:973], [@215,1026:1026='s',<381>,1:1026]], school_region=[[@166,790:790='s',<381>,1:790], [@172,817:817='s',<381>,1:817], [@182,874:874='s',<381>,1:874]], school_key=[[@18,82:82='s',<381>,1:82], [@302,1475:1475='s',<381>,1:1475], [@314,1553:1553='s',<381>,1:1553]]}, <slate_lookup_school>={lookup_school_name=[[@112,526:527='sl',<381>,1:526]], lookup_school_id=[[@298,1453:1454='sl',<381>,1:1453]]}, <slate_address>={address_county=[[@229,1085:1085='a',<381>,1:1085]], address_record=[[@340,1724:1724='a',<381>,1:1724]], address_region=[[@178,852:852='a',<381>,1:852]], address_street=[[@257,1205:1205='a',<381>,1:1205]], address_country=[[@145,689:689='a',<381>,1:689]], address_rank_overall=[[@348,1770:1770='a',<381>,1:1770]], address_zip=[[@243,1148:1148='a',<381>,1:1148]], address_city=[[@211,1006:1006='a',<381>,1:1006]]}, <slate_dataset>={dataset_id=[[@322,1605:1605='d',<381>,1:1605]], dataset_name=[[@46,216:216='d',<381>,1:216], [@79,370:370='d',<381>,1:370], [@330,1652:1652='d',<381>,1:1652]]}, schl_cat={value=[[@67,310:317='schl_cat',<381>,1:310], [@73,336:343='schl_cat',<381>,1:336], [@83,390:397='schl_cat',<381>,1:390]], field_record=[[@368,1903:1910='schl_cat',<381>,1:1903]]}, <slate_dataset_row>={dataset_row_created=[[@271,1270:1271='dr',<381>,1:1270]], dataset_row_id=[[@344,1743:1744='dr',<381>,1:1743], [@361,1853:1854='dr',<381>,1:1853], [@372,1927:1928='dr',<381>,1:1927]], dataset_row_updated=[[@282,1337:1338='dr',<381>,1:1337]], dataset_row_key=[[@310,1532:1533='dr',<381>,1:1532]], dataset_row_dataset=[[@326,1620:1621='dr',<381>,1:1620]]}}, filters=[{name=lookup_school_id, table_ref=sl}, {name=school_key, table_ref=s}, {name=dataset_row_key, table_ref=dr}, {name=dataset_id, table_ref=d}, {name=dataset_row_dataset, table_ref=dr}, {name=dataset_name, table_ref=d}, {name=address_record, table_ref=a}, {name=dataset_row_id, table_ref=dr}, {name=address_rank_overall, table_ref=a}, {name=field_record, table_ref=schl_type}, {name=field_record, table_ref=schl_cat}], interface={school_type=[{name=dataset_name, table_ref=d}, {name=value, table_ref=schl_type}], crm_created_at=[{name=dataset_row_created, table_ref=dr}], school_name=[{name=lookup_school_name, table_ref=sl}, {name=school_name, table_ref=s}], school_city=[{name=address_city, table_ref=a}, {name=school_city, table_ref=s}], school_category=[{name=dataset_name, table_ref=d}, {name=value, table_ref=schl_cat}], school_state=[{name=address_region, table_ref=a}, {name=school_region, table_ref=s}], school_id=[{name=school_id, table_ref=s}], school_country=[{name=address_country, table_ref=a}, {name=school_country, table_ref=s}], school_ceeb_code=[{name=school_key, table_ref=s}], school_address=[{name=address_street, table_ref=a}], school_county=[{name=address_county, table_ref=a}], crm_updated_at=[{name=dataset_row_updated, table_ref=dr}], school_zip=[{name=address_zip, table_ref=a}]}, table_alias={a=<slate_address>, s=school, d=<slate_dataset>, sl=<slate_lookup_school>, dr=<slate_dataset_row>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void getMissingColumnFromTupleDictionaryTest() {
		// Column Variable Test: AMS Domain Specific Query with Column Variables for Soft Credit Amounts and IDs
		String query =
		    "\nwith gifts_allocation as" +
		    "\n(" +
		    "\n    select" +
		    "\n        cast(ams_gifts_allocation_fulfillment.<gift id> as varchar) as gift_id," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<soft credit id> as varchar) as soft_credit_id," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<gift allocation id> as varchar) as gift_allocation_id," +
		    "\n        ---cast(replace(replace(ams_gifts_allocation_fulfillment.<soft credit amount>, '$', ''),',','')  as float) as soft_credit_amount," +
		    "\n        try_cast(regexp_replace(ams_gifts_allocation_fulfillment.<soft credit amount>,'[^0-9.-]', '') as float) as  soft_credit_amount," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<intake date> as timestamp) as intake_dt," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<gift date> as timestamp) as gift_dt," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<credit fiscal year> as varchar) as credit_fiscal_year" +
		    "\n    from <[AMS].[gifts_allocation].{fulfillment}> as ams_gifts_allocation_fulfillment" +
		    "\n    where ams_gifts_allocation_fulfillment.<soft credit id> is not null" +
		    "\n      and try_cast(regexp_replace(ams_gifts_allocation_fulfillment.<soft credit amount>,'[^0-9.-]', '') as float) >= 0" +
		    "\n" +
		    "\n    union" +
		    "\n" +
		    "\n    select" +
		    "\n        cast(ams_gifts_allocation_fulfillment.<gift id> as varchar) as gift_id," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<second soft credit id> as varchar) as soft_credit_id," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<gift allocation id> as varchar) as gift_allocation_id," +
		    "\n        --cast(replace(replace(ams_gifts_allocation_fulfillment.<second soft credit amount>, '$', ''),',','')  as float) as soft_credit_amount," +
		    "\n        try_cast(regexp_replace(ams_gifts_allocation_fulfillment.<second soft credit amount>,'[^0-9.-]', '') as float) as  soft_credit_amount," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<intake date> as timestamp) as intake_dt," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<gift date> as timestamp) as gift_dt," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<credit fiscal year> as varchar) as credit_fiscal_year" +
		    "\n    from <[AMS].[gifts_allocation].{fulfillment}> as ams_gifts_allocation_fulfillment" +
		    "\n    where ams_gifts_allocation_fulfillment.<second soft credit id> is not null" +
		    "\n      and try_cast(regexp_replace(ams_gifts_allocation_fulfillment.<second soft credit amount>,'[^0-9.-]', '') as float)  >= 0" +
		    "\n" +
		    "\n    union" +
		    "\n" +
		    "\n    select" +
		    "\n        cast(ams_gifts_allocation_fulfillment.<gift id> as varchar) as gift_id," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<third soft credit id> as varchar) as soft_credit_id," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<gift allocation id> as varchar) as gift_allocation_id," +
		    "\n        --cast(replace(replace(ams_gifts_allocation_fulfillment.<third soft credit amount>, '$', ''),',','')  as float) as soft_credit_amount," +
		    "\n        try_cast(regexp_replace(ams_gifts_allocation_fulfillment.<third soft credit amount>,'[^0-9.-]', '') as float) as  soft_credit_amount," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<intake date> as timestamp) as intake_dt," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<gift date> as timestamp) as gift_dt," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<credit fiscal year> as varchar) as credit_fiscal_year" +
		    "\n    from <[AMS].[gifts_allocation].{fulfillment}> as ams_gifts_allocation_fulfillment" +
		    "\n    where ams_gifts_allocation_fulfillment.<third soft credit id> is not null" +
		    "\n      and try_cast(regexp_replace(ams_gifts_allocation_fulfillment.<third soft credit amount>,'[^0-9.-]', '') as float) >= 0" +
		    "\n" +
		    "\n    union" +
		    "\n" +
		    "\n    select" +
		    "\n        cast(ams_gifts_allocation_fulfillment.<gift id> as varchar) as gift_id," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<fourth soft credit id> as varchar) as soft_credit_id," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<gift allocation id> as varchar) as gift_allocation_id," +
		    "\n        ----cast(replace(replace(ams_gifts_allocation_fulfillment.<fourth soft credit amount>, '$', ''),',','')  as float) as soft_credit_amount," +
		    "\n        try_cast(regexp_replace(ams_gifts_allocation_fulfillment.<fourth soft credit amount>,'[^0-9.-]', '') as float) as  soft_credit_amount," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<intake date> as timestamp) as intake_dt," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<gift date> as timestamp) as gift_dt," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<credit fiscal year> as varchar) as credit_fiscal_year" +
		    "\n    from <[AMS].[gifts_allocation].{fulfillment}> as ams_gifts_allocation_fulfillment" +
		    "\n    where ams_gifts_allocation_fulfillment.<fourth soft credit id> is not null" +
		    "\n      and try_cast(regexp_replace(ams_gifts_allocation_fulfillment.<fourth soft credit amount>,'[^0-9.-]', '') as float) >= 0" +
		    "\n" +
		    "\n    union" +
		    "\n" +
		    "\n    select" +
		    "\n        cast(ams_gifts_allocation_fulfillment.<gift id> as varchar) as gift_id," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<fifth soft credit id> as varchar) as soft_credit_id," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<gift allocation id> as varchar) as gift_allocation_id," +
		    "\n        ---cast(replace(replace(ams_gifts_allocation_fulfillment.<fifth soft credit amount>, '$', ''),',','')  as float) as soft_credit_amount," +
		    "\n        try_cast(regexp_replace(ams_gifts_allocation_fulfillment.<fifth soft credit amount>,'[^0-9.-]', '') as float) as  soft_credit_amount," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<intake date> as timestamp) as intake_dt," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<gift date> as timestamp) as gift_dt," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<credit fiscal year> as varchar) as credit_fiscal_year" +
		    "\n    from <[AMS].[gifts_allocation].{fulfillment}> as ams_gifts_allocation_fulfillment" +
		    "\n    where ams_gifts_allocation_fulfillment.<fifth soft credit id> is not null" +
		    "\n      and try_cast(regexp_replace(ams_gifts_allocation_fulfillment.<fifth soft credit amount>,'[^0-9.-]', '') as float) >= 0" +
		    "\n" +
		    "\n    union" +
		    "\n" +
		    "\n    select" +
		    "\n        cast(ams_gifts_allocation_fulfillment.<gift id> as varchar) as gift_id," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<Calculated Field 1> as varchar) as soft_credit_id," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<gift allocation id> as varchar) as gift_allocation_id," +
		    "\n        ---cast(replace(replace(ams_gifts_allocation_fulfillment.<soft credit amount>, '$', ''),',','')  as float) as soft_credit_amount," +
		    "\n        try_cast(regexp_replace(ams_gifts_allocation_fulfillment.<soft credit amount>,'[^0-9.-]', '') as float) as  soft_credit_amount," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<intake date> as timestamp) as intake_dt," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<gift date> as timestamp) as gift_dt," +
		    "\n        cast(ams_gifts_allocation_fulfillment.<credit fiscal year> as varchar) as credit_fiscal_year" +
		    "\n    from <[AMS].[gifts_allocation].{fulfillment}> as ams_gifts_allocation_fulfillment" +
		    "\n    where try_cast(regexp_replace(ams_gifts_allocation_fulfillment.<Calculated Field 2>,'[^0-9.-]', '') as float) = 0" +
		    "\n      and ams_gifts_allocation_fulfillment.<soft credit id> is null" +
		    "\n      and  try_cast(regexp_replace(ams_gifts_allocation_fulfillment.<soft credit amount>,'[^0-9.-]', '') as float) >= 0" +
		    "\n)" +
		    "\nselect" +
		    "\n    ga.gift_id as gift_id," +
		    "\n    ga.soft_credit_id as soft_credit_id," +
		    "\n    ga.gift_allocation_id as gift_allocation_id," +
		    "\n    row_number() over (partition by ga.gift_id order by ga.soft_credit_id desc, intake_dt desc) as soft_credit_sequence," +
		    "\n    ga.soft_credit_amount as soft_credit_amount," +
		    "\n    ga.intake_dt as intake_dt," +
		    "\n    ga.gift_dt as gift_dt," +
		    "\n    case when TO_NUMBER(ga.credit_fiscal_year) is null then gb.credited_fy" +
		    "\n         else TO_NUMBER(ga.credit_fiscal_year)" +
		    "\n    end as credit_fiscal_year," +
		    "\n    case when" +
		    "\n        (MONTH(cast(getdate() as timestamp)) between <fy_month_1> and <fy_month_2>) then" +
		    "\n           YEAR(cast(getdate() as timestamp)) +1" +
		    "\n           else" +
		    "\n            YEAR(cast(getdate() as timestamp)) end  as  current_fiscal_year," +
		    "\n    <source_partner_system_name_gifts_allocation> as source_partner_system_name," +
		    "\n    <source_eab_system_type> as source_eab_system_type" +
		    "\nfrom gifts_allocation as ga" +
		    "\njoin <fy_credited_gifts_allocation> as gb" +
		    "\non coalesce(ga.gift_id,'') = coalesce(gb.gift_id,'')" +
		    "\n   and coalesce(ga.soft_credit_id,'') = coalesce(gb.soft_credit_id,'')" +
		    "\n" +
		    "\n<join_extension_gifts_allocation>" +
		    "\n          " +
		    "\nwhere ga.gift_id is not null" +
		    "\n  and ga.soft_credit_id is not null" +
		    "\n  and ga.soft_credit_amount is not null";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);

		Snippet snippet = extractor.getSnippet();
		// intake_dt in ORDER BY is ambiguous between the outer join alias and union6 of gifts_allocation
		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'intake_dt' at (l:97 c:80). Possible sources: [<join_extension_gifts_allocation>, <fy_credited_gifts_allocation>, union6]",
				"intake_dt",
				97,
				80);

	}


	@Test
	public void getMissingColumnFromTupleDictionaryv2Test() {
		// Column Variable Test
		String query =
		    "\nwith gifts_allocation as" +
		    "\n(" +
		    "\n    select" +
		    "\n        cast(ams_gifts_allocation_fulfillment.<Calculated Field 1> as varchar) as soft_credit_id," +
		    "\n        try_cast(regexp_replace(ams_gifts_allocation_fulfillment.<soft credit amount>,'[^0-9.-]', '') as float) as  soft_credit_amount" +
		    "\n    from <[AMS].[gifts_allocation].{fulfillment}> as ams_gifts_allocation_fulfillment" +
		    "\n    where try_cast(regexp_replace(ams_gifts_allocation_fulfillment.<Calculated Field 2>,'[^0-9.-]', '') as float) = 0" +
		    "\n      and ams_gifts_allocation_fulfillment.<soft credit id> is null" +
		    "\n      and  try_cast(regexp_replace(ams_gifts_allocation_fulfillment.<soft credit amount2>,'[^0-9.-]', '') as float) >= 0" +
		    "\n)" +
		    "\nselect" +
		    "\n    ga.soft_credit_amount as soft_credit_amount," +
		    "\n    <source_eab_system_type> as source_eab_system_type" +
		    "\nfrom gifts_allocation as ga" +
		    "\nwhere ga.soft_credit_amount is not null";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

	}


	@Test
	public void getMissingColumnFromTupleDictionaryv3Test() {
		// Column Variable Test
		String query =
		    "select" +
		    "\n        cast(ams_gifts_allocation_fulfillment.<Calculated Field 1> as varchar) as soft_credit_id," +
		    "\n        try_cast(regexp_replace(ams_gifts_allocation_fulfillment.<soft credit amount>,'[^0-9.-]', '') as float) as  soft_credit_amount" +
		    "\n    from <[AMS].[gifts_allocation].{fulfillment}> as ams_gifts_allocation_fulfillment" +
		    "\n    where try_cast(regexp_replace(ams_gifts_allocation_fulfillment.<Calculated Field 2>,'[^0-9.-]', '') as float) = 0" +
		    "\n      and ams_gifts_allocation_fulfillment.<soft credit id> is null" +
		    "\n      and  try_cast(regexp_replace(ams_gifts_allocation_fulfillment.<soft credit amount2>,'[^0-9.-]', '') as float) >= 0";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

	}

	
	@Test
	public void donorEmailWithInvalidFatalErrorOnQualifiedColumnVariableTest() {
		// Donor Email Column Variable, Invalid Fatal Unresolved Error Test on line 46, character 67
		// The test proves we have fixed an incorrect fatal unresolved error for the
		// donor email column variable by showing the variable is now correctly added to the tuple dictionary.
		String query =
		    "-- #terukula 09/15 AMS-51 V0.1 created query to populate donor_email in AMS" +
		    "\n-- #spatole 12/12 AMS-322 - Change references for source entities in the donor bound queries" +
		    "\n-- #terukula 12/16 renamed secondary_email column to calculated field_1 and added case statement for email_type column" +
		    "\n--#gsudhakar 10/6/23 added intake_dt field" +
		    "\n--#gsudhakar 11/08/23 add logic to include null emails where email_type=primary " +
		    "\n--#gsudhakar 03/19/25 added <join_extension_donor_email> variable" +
		    "\n--#bellerson 03/12/25 added replace functions around email to sanitize linebreaks, null bytes, and carriage returns" +
		    "\n" +
		    "\nSELECT src_donor_id,email,email_type,intake_dt, source_partner_system_name, source_eab_system_type FROM ( --NEW beign outer wrapper" +
		    "\nSELECT" +
		    "\nsrc_donor_id," +
		    "\nemail," +
		    "\nemail_type," +
		    "\nintake_dt," +
		    "\n<source_partner_system_name_donor_email> as source_partner_system_name," +
		    "\n<source_eab_system_type> as source_eab_system_type" +
		    "\n , ROW_NUMBER() OVER ( -- NEW: assign rank to dedupe emails" +
			// TODO: source_partner_system_name is the alias for a column appearing 3 lines earlier; This resolution
			// requires new code to check the aliases in the current select list.
		    "\n    PARTITION BY src_donor_id, donor_email.email, source_partner_system_name -- NEW: group by email " +
		    "\n    ORDER BY " +
		    "\n        CASE WHEN LOWER(donor_email.email_type) = 'primary' THEN 1 ELSE 2 END --, NEW: prioritize primary if email dupes between primary and secondary" +
		    "\n        --donor_email.intake_dt DESC -- NEW: take most recent " +
		    "\n) AS rn -- NEW: row number for deduplication " +
		    "\nFROM" +
		    "\n((SELECT" +
		    "\n  CAST(donor_email.<src donor id> AS VARCHAR) AS src_donor_id," +
		    "\n  REPLACE(REPLACE(REPLACE(CAST(donor_email.<email> AS VARCHAR), CHAR(0), ''), CHAR(10), ''), CHAR(13), '') AS email," +
		    "\n  --CAST(donor_email.<email> AS VARCHAR) AS email," +
		    "\n  CASE WHEN CAST(donor_email.<email type> AS VARCHAR) IS NULL THEN CAST('primary' AS VARCHAR)" +
		    "\n       ELSE CAST(donor_email.<email type> AS VARCHAR)" +
		    "\n  END AS email_type," +
		    "\n  donor_email.intake_date as intake_dt" +
		    "\nFROM <[AMS].[donor_email].{fulfillment}> AS donor_email" +
		    "\nWHERE CAST(donor_email.<src donor id> AS VARCHAR) IS NOT NULL" +
		    "\n--AND CAST(donor_email.<email> AS VARCHAR) IS NOT NULL" +
		    "\n)" +
		    "\nUNION" +
		    "\n(SELECT" +
		    "\n  CAST(donor_email.<src donor id> AS VARCHAR) AS src_donor_id," +
		    "\n  REPLACE(REPLACE(REPLACE(CAST(donor_email.<Calculated Field 1> AS VARCHAR), CHAR(0), ''), CHAR(10), ''), CHAR(13), '') AS email," +
		    "\n --CAST(donor_email.<Calculated Field 1> AS VARCHAR) AS email," +
		    "\n  CAST('secondary' AS VARCHAR) AS email_type," +
		    "\n  donor_email.intake_date as intake_dt" +
		    "\nFROM <[AMS].[donor_email].{fulfillment}> AS donor_email" +
		    "\nWHERE CAST(donor_email.<src donor id> AS VARCHAR) IS NOT NULL" +
		    "\nAND CAST(donor_email.<Calculated Field 1> AS VARCHAR) IS NOT NULL " +
			// next line is where the error was ocurring before the fix
		    "\nAND CAST(donor_email.<Calculated Field 1> AS VARCHAR) <>  CAST(NVL(donor_email.<email>,'No Email') AS VARCHAR)" +
		    "\n)) AS donor_email" +
		    "\n<join_extension_donor_email>" +
		    "\nWHERE donor_email.src_donor_id IS NOT NULL --AND donor_email.email IS NOT NULL" +
		    "\nAND ((lower(donor_email.email_type) != 'primary' and coalesce(donor_email.email,'') <> '' ) OR lower(donor_email.email_type) = 'primary')" +
		    "\n) t -- NEW: close outer wrapper" +
		    "\nWHERE rn = 1 -- NEW: keep only one record per email" +
		    "\norder by src_donor_id, email_type";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		// TODO: The problem in this example is that the join extension variable is being treated as a possible resolution target. I think the easiest fix will 
		// be to have the resolution logic notice that it is NOT a tuple/table or query variable and therefore not a possible resolution target. The join extension variable is a special case that is not a tuple/table or query variable, so it should be ignored in the resolution logic.
	}

	@Test
	public void getComplexPredicandVariablesTest() {
		// Predicand Variable Test
		String query = " select cec.* " + 
				"	from <[Enrollment Services].[Client Entering Class]> cec" + 
				"	where" + 
				"	(<Permanent Country> is null or <Permanent Country> in <Permanent Country List>)" + 
				"	and <College Attendance Status> in <College Attendance Status List>" + 
				"	and (<Graduation Year> is null or <Graduation Year> in <Graduation Year List>)" + 
				"	and (<Application Admissions Status> is null or <Application Admissions Status> in <Application Admissions Status list>)" + 
				"	and (<Term Of Interest> is null or <Term Of Interest> in <Term Of Interest List>)" + 
				"	and (<Date Submitted> is null or <Date Submitted> = '')";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=cec}}}, from={table={alias=cec, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}, where={and={1={parentheses={or={1={condition={left={substitution={name=<Permanent Country>, type=predicand}}, operator=is null}}, 2={in={item={substitution={name=<Permanent Country>, type=predicand}}, in_list={substitution={name=<Permanent Country List>, type=in_list}}}}}}}, 2={in={item={substitution={name=<College Attendance Status>, type=predicand}}, in_list={substitution={name=<College Attendance Status List>, type=in_list}}}}, 3={parentheses={or={1={condition={left={substitution={name=<Graduation Year>, type=predicand}}, operator=is null}}, 2={in={item={substitution={name=<Graduation Year>, type=predicand}}, in_list={substitution={name=<Graduation Year List>, type=in_list}}}}}}}, 4={parentheses={or={1={condition={left={substitution={name=<Application Admissions Status>, type=predicand}}, operator=is null}}, 2={in={item={substitution={name=<Application Admissions Status>, type=predicand}}, in_list={substitution={name=<Application Admissions Status list>, type=in_list}}}}}}}, 5={parentheses={or={1={condition={left={substitution={name=<Term Of Interest>, type=predicand}}, operator=is null}}, 2={in={item={substitution={name=<Term Of Interest>, type=predicand}}, in_list={substitution={name=<Term Of Interest List>, type=in_list}}}}}}}, 6={parentheses={or={1={condition={left={substitution={name=<Date Submitted>, type=predicand}}, operator=is null}}, 2={condition={left={substitution={name=<Date Submitted>, type=predicand}}, right={literal=''}, operator==}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<Graduation Year List>=in_list, <[Enrollment Services].[Client Entering Class]>=tuple, <Term Of Interest List>=in_list, <Term Of Interest>=predicand, <Application Admissions Status list>=in_list, <College Attendance Status List>=in_list, <Graduation Year>=predicand, <Permanent Country>=predicand, <Date Submitted>=predicand, <Application Admissions Status>=predicand, <College Attendance Status>=predicand, <Permanent Country List>=in_list}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<[Enrollment Services].[Client Entering Class]>={*=[[@1,8:10='cec',<381>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,12:12='*',<291>,1:12]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@3,12:12='*',<291>,1:12]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={*=[[@1,8:10='cec',<381>,1:8]]}}, filters=[{name=<Permanent Country>, type=predicand}, {name=<College Attendance Status>, type=predicand}, {name=<Graduation Year>, type=predicand}, {name=<Application Admissions Status>, type=predicand}, {name=<Term Of Interest>, type=predicand}, {name=<Date Submitted>, type=predicand}], interface={*=[{name=*, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void getComplexColumnVariablesTest() {
		// Column Variable Test
		String query = " select cec.* " + 
				"	from <[Enrollment Services].[Client Entering Class]> cec" + 
				"	where" + 
				"	(cec.<Permanent Country> is null or cec.<Permanent Country> in <Permanent Country List>)" + 
				"	and cec.<College Attendance Status> in <College Attendance Status List>" + 
				"	and (cec.<Graduation Year> is null or cec.<Graduation Year> in <Graduation Year List>)" + 
				"	and (cec.<Application Admissions Status> is null or cec.<Application Admissions Status> in <Application Admissions Status list>)" + 
				"	and (cec.<Term Of Interest> is null or cec.<Term Of Interest> in <Term Of Interest List>)" + 
				"	and (cec.<Date Submitted> is null or cec.<Date Submitted> = '')";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=cec}}}, from={table={alias=cec, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}, where={and={1={parentheses={or={1={condition={left={column={substitution={name=<Permanent Country>, type=column}, table_ref=cec}}, operator=is null}}, 2={in={item={column={substitution={name=<Permanent Country>, type=column}, table_ref=cec}}, in_list={substitution={name=<Permanent Country List>, type=in_list}}}}}}}, 2={in={item={column={substitution={name=<College Attendance Status>, type=column}, table_ref=cec}}, in_list={substitution={name=<College Attendance Status List>, type=in_list}}}}, 3={parentheses={or={1={condition={left={column={substitution={name=<Graduation Year>, type=column}, table_ref=cec}}, operator=is null}}, 2={in={item={column={substitution={name=<Graduation Year>, type=column}, table_ref=cec}}, in_list={substitution={name=<Graduation Year List>, type=in_list}}}}}}}, 4={parentheses={or={1={condition={left={column={substitution={name=<Application Admissions Status>, type=column}, table_ref=cec}}, operator=is null}}, 2={in={item={column={substitution={name=<Application Admissions Status>, type=column}, table_ref=cec}}, in_list={substitution={name=<Application Admissions Status list>, type=in_list}}}}}}}, 5={parentheses={or={1={condition={left={column={substitution={name=<Term Of Interest>, type=column}, table_ref=cec}}, operator=is null}}, 2={in={item={column={substitution={name=<Term Of Interest>, type=column}, table_ref=cec}}, in_list={substitution={name=<Term Of Interest List>, type=in_list}}}}}}}, 6={parentheses={or={1={condition={left={column={substitution={name=<Date Submitted>, type=column}, table_ref=cec}}, operator=is null}}, 2={condition={left={column={substitution={name=<Date Submitted>, type=column}, table_ref=cec}}, right={literal=''}, operator==}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<Graduation Year List>=in_list, <[Enrollment Services].[Client Entering Class]>=tuple, <Term Of Interest List>=in_list, <Term Of Interest>=column, <Application Admissions Status list>=in_list, <College Attendance Status List>=in_list, <Graduation Year>=column, <Permanent Country>=column, <Date Submitted>=column, <Application Admissions Status>=column, <College Attendance Status>=column, <Permanent Country List>=in_list}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<[Enrollment Services].[Client Entering Class]>={<Term Of Interest>=[[@57,460:462='cec',<381>,1:460], [@63,494:496='cec',<381>,1:494]], <Graduation Year>=[[@29,244:246='cec',<381>,1:244], [@35,277:279='cec',<381>,1:277]], <Permanent Country>=[[@9,79:81='cec',<381>,1:79], [@15,114:116='cec',<381>,1:114]], <Date Submitted>=[[@71,550:552='cec',<381>,1:550], [@77,582:584='cec',<381>,1:582]], *=[[@1,8:10='cec',<381>,1:8]], <Application Admissions Status>=[[@43,331:333='cec',<381>,1:331], [@49,378:380='cec',<381>,1:378]], <College Attendance Status>=[[@22,171:173='cec',<381>,1:171]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,12:12='*',<291>,1:12]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@3,12:12='*',<291>,1:12]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<Term Of Interest>=[[@57,460:462='cec',<381>,1:460], [@63,494:496='cec',<381>,1:494]], <Graduation Year>=[[@29,244:246='cec',<381>,1:244], [@35,277:279='cec',<381>,1:277]], <Permanent Country>=[[@9,79:81='cec',<381>,1:79], [@15,114:116='cec',<381>,1:114]], <Date Submitted>=[[@71,550:552='cec',<381>,1:550], [@77,582:584='cec',<381>,1:582]], *=[[@1,8:10='cec',<381>,1:8]], <Application Admissions Status>=[[@43,331:333='cec',<381>,1:331], [@49,378:380='cec',<381>,1:378]], <College Attendance Status>=[[@22,171:173='cec',<381>,1:171]]}}, filters=[{substitution={name=<Permanent Country>, type=column}, table_ref=cec}, {substitution={name=<College Attendance Status>, type=column}, table_ref=cec}, {substitution={name=<Graduation Year>, type=column}, table_ref=cec}, {substitution={name=<Application Admissions Status>, type=column}, table_ref=cec}, {substitution={name=<Term Of Interest>, type=column}, table_ref=cec}, {substitution={name=<Date Submitted>, type=column}, table_ref=cec}], interface={*=[{name=*, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void getMixedExtendedVariablesTest() {
		// ITEM 103: tuple variables with up unbracketed prefix and up to five name segments
		String query = " select cec.* " + 
				"	from <fulfill.[domain].[entity].[file category]> cec" + 
				"	join <fulfill.[domain].[entity]> oth";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=cec}}}, from={join={1={table={alias=cec, substitution={name=<fulfill.[domain].[entity].[file category]>, parts={1=fulfill, 2=[domain], 3=[entity], 4=[file category]}, type=tuple}}}, 2={join=join}, 3={table={alias=oth, substitution={name=<fulfill.[domain].[entity]>, parts={1=fulfill, 2=[domain], 3=[entity]}, type=tuple}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<fulfill.[domain].[entity].[file category]>=tuple, <fulfill.[domain].[entity]>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<fulfill.[domain].[entity].[file category]>={*=[[@1,8:10='cec',<381>,1:8]]}, <fulfill.[domain].[entity]>={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,12:12='*',<291>,1:12]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@3,12:12='*',<291>,1:12]]}, table_dictionary={<fulfill.[domain].[entity].[file category]>={*=[[@1,8:10='cec',<381>,1:8]]}, <fulfill.[domain].[entity]>={}}, interface={*=[{name=*, table_ref=cec}]}, table_alias={cec=<fulfill.[domain].[entity].[file category]>, oth=<fulfill.[domain].[entity]>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void getMixedExtendedVariablesV2Test() {
		// ITEM 103: tuple variables with up unbracketed prefix and up to five name segments
		String query = " select oth.* " + 
				"	from  <fulfill.[domain].[entity].[file category].{snapshot}> oth";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=oth}}}, from={table={alias=oth, substitution={name=<fulfill.[domain].[entity].[file category].{snapshot}>, parts={1=fulfill, 2=[domain], 3=[entity], 4=[file category], 5={snapshot}}, type=tuple}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<fulfill.[domain].[entity].[file category].{snapshot}>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<fulfill.[domain].[entity].[file category].{snapshot}>={*=[[@1,8:10='oth',<381>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,12:12='*',<291>,1:12]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@3,12:12='*',<291>,1:12]]}, table_dictionary={<fulfill.[domain].[entity].[file category].{snapshot}>={*=[[@1,8:10='oth',<381>,1:8]]}}, interface={*=[{name=*, table_ref=oth}]}, table_alias={oth=<fulfill.[domain].[entity].[file category].{snapshot}>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void getMajorSqlTest() {
		/*
		 * Major COLUMNS: RECORD_TYPE, ACTION, EXTERNAL_ID, NAME
		 */
		String query = "select record_type as RECORD_TYPE,action as ACTION,"
				+ "trim(external_id) as EXTERNAL_ID,case when name is null or length(trim(name)) = 0 then 'Major name not available' else trim(name) end as NAME from "
				+ " majorTbl where external_id is not null and length(trim(external_id)) > 0";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
	}


	@Test
	public void realisticRangeFrameTest() {
		final String query = "SELECT " + 
				"  st.student_id, st.term_code, st.level_code " + 
				"  , major_code_1 " + 
				"  , COALESCE(first_value(st.major_code_1) OVER (PARTITION BY st.student_id, bfdf.major1_partition_downfill ORDER BY st.student_id, st.term_code), last_value(st.major_code_1) OVER (PARTITION BY st.student_id, bfdf.major1_partition_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following), 'NA') AS major_code_1_new " + 
				"  , degree_code_1 " + 
				"  , COALESCE(first_value(st.degree_code_1) OVER (PARTITION BY st.student_id, bfdf.degree1_partition_downfill ORDER BY st.student_id, st.term_code), last_value(st.degree_code_1) OVER (PARTITION BY st.student_id, bfdf.degree1_partition_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following), 'NA') AS degree_code_1_new " + 
				"  , concentration_code_1 " + 
				"  , first_value(st.concentration_code_1) OVER (PARTITION BY st.student_id, bfdf.conc1_partition_downfill ORDER BY st.student_id, st.term_code), last_value(st.concentration_code_1) OVER (PARTITION BY st.student_id, bfdf.conc1_partition_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following) AS concentration_code_1_new " + 
				"  , campus_code " + 
				"  , COALESCE(first_value(st.campus_code) OVER (PARTITION BY st.student_id, bfdf.campus_partition_downfill ORDER BY st.student_id, st.term_code), last_value(st.campus_code) OVER (PARTITION BY st.student_id, bfdf.campus_partition_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following), 'NA') AS campus_code_new " + 
				"  , college_code " + 
				"  , COALESCE(first_value(st.college_code) OVER (PARTITION BY st.student_id, bfdf.coll1_partition_downfill ORDER BY st.student_id, st.term_code), last_value(st.college_code) OVER (PARTITION BY st.student_id, bfdf.coll1_partition_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following), 'NA') AS college_code_new " + 
				"  , department_code " + 
				"  , COALESCE(first_value(st.department_code) OVER (PARTITION BY st.student_id, bfdf.dept_partition_downfill ORDER BY st.student_id, st.term_code), last_value(st.department_code) OVER (PARTITION BY st.student_id, bfdf.dept_partition_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following), 'NA') AS department_code_new " + 
				"  , major_code_2 " + 
				"  , COALESCE(first_value(st.major_code_2) OVER (PARTITION BY st.student_id, bfdf.major2_partition_downfill ORDER BY st.student_id, st.term_code), last_value(st.major_code_2) OVER (PARTITION BY st.student_id, bfdf.major2_partition_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS major_code_2_new " + 
				"  , concentration_code_2 " + 
				"  , COALESCE(first_value(st.concentration_code_2) OVER (PARTITION BY st.student_id, bfdf.conc2_partition_downfill ORDER BY st.student_id, st.term_code), last_value(st.concentration_code_2) OVER (PARTITION BY st.student_id, bfdf.conc2_partition_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS concentration_code_2_new " + 
				"  , degree_code_2 " + 
				"  , COALESCE(first_value(st.degree_code_2) OVER (PARTITION BY st.student_id, bfdf.degree2_partition_downfill ORDER BY st.student_id, st.term_code), last_value(st.degree_code_2) OVER (PARTITION BY st.student_id, bfdf.degree2_partition_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS degree_code_2_new " + 
				"  , college_code_2 " + 
				"  , COALESCE(first_value(st.college_code_2) OVER (PARTITION BY st.student_id, bfdf.coll2_partition_downfill ORDER BY st.student_id, st.term_code), last_value(st.college_code_2) OVER (PARTITION BY st.student_id, bfdf.coll2_partition_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS college_code_2_new " + 
				"  , major_code_3 " + 
				"  , COALESCE(first_value(st.major_code_3) OVER (PARTITION BY st.student_id, bfdf.major_code_3_downfill ORDER BY st.student_id, st.term_code), last_value(st.major_code_3) OVER (PARTITION BY st.student_id, bfdf.major_code_3_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS major_code_3_new " + 
				"  , concentration_code_3 " + 
				"  , COALESCE(first_value(st.concentration_code_3) OVER (PARTITION BY st.student_id, bfdf.concentration_code_3_downfill ORDER BY st.student_id, st.term_code), last_value(st.concentration_code_3) OVER (PARTITION BY st.student_id, bfdf.concentration_code_3_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS concentration_code_3_new " + 
				"  , degree_code_3 " + 
				"  , COALESCE(first_value(st.degree_code_3) OVER (PARTITION BY st.student_id, bfdf.degree_code_3_downfill ORDER BY st.student_id, st.term_code), last_value(st.degree_code_3) OVER (PARTITION BY st.student_id, bfdf.degree_code_3_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS degree_code_3_new " + 
				"  , college_code_3 " + 
				"  , COALESCE(first_value(st.college_code_3) OVER (PARTITION BY st.student_id, bfdf.college_code_3_downfill ORDER BY st.student_id, st.term_code), last_value(st.college_code_3) OVER (PARTITION BY st.student_id, bfdf.college_code_3_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS college_code_3_new " + 
				"  , major_code_4 " + 
				"  , COALESCE(first_value(st.major_code_4) OVER (PARTITION BY st.student_id, bfdf.major_code_4_downfill ORDER BY st.student_id, st.term_code), last_value(st.major_code_4) OVER (PARTITION BY st.student_id, bfdf.major_code_4_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS major_code_4_new " + 
				"  , concentration_code_4 " + 
				"  , COALESCE(first_value(st.concentration_code_4) OVER (PARTITION BY st.student_id, bfdf.concentration_code_4_downfill ORDER BY st.student_id, st.term_code), last_value(st.concentration_code_4) OVER (PARTITION BY st.student_id, bfdf.concentration_code_4_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS concentration_code_4_new " + 
				"  , degree_code_4 " + 
				"  , COALESCE(first_value(st.degree_code_4) OVER (PARTITION BY st.student_id, bfdf.degree_code_4_downfill ORDER BY st.student_id, st.term_code), last_value(st.degree_code_4) OVER (PARTITION BY st.student_id, bfdf.degree_code_4_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS degree_code_4_new " + 
				"  , college_code_4 " + 
				"  , COALESCE(first_value(st.college_code_4) OVER (PARTITION BY st.student_id, bfdf.college_code_4_downfill ORDER BY st.student_id, st.term_code), last_value(st.college_code_4) OVER (PARTITION BY st.student_id, bfdf.college_code_4_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS college_code_4_new " + 
				"  , department_code_2 " + 
				"  , COALESCE(first_value(st.department_code_2) OVER (PARTITION BY st.student_id, bfdf.department_code_2_downfill ORDER BY st.student_id, st.term_code), last_value(st.department_code_2) OVER (PARTITION BY st.student_id, bfdf.department_code_2_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS department_code_2_new " + 
				"  , department_code_3 " + 
				"  , COALESCE(first_value(st.department_code_3) OVER (PARTITION BY st.student_id, bfdf.department_code_3_downfill ORDER BY st.student_id, st.term_code), last_value(st.department_code_3) OVER (PARTITION BY st.student_id, bfdf.department_code_3_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS department_code_3_new " + 
				"  , department_code_4 " + 
				"  , COALESCE(first_value(st.department_code_4) OVER (PARTITION BY st.student_id, bfdf.department_code_4_downfill ORDER BY st.student_id, st.term_code), last_value(st.department_code_4) OVER (PARTITION BY st.student_id, bfdf.department_code_4_backfill ORDER BY st.student_id, st.term_code rows between unbounded preceding and unbounded following)) AS department_code_4_new  , first_value(st.academic_standing_code) OVER (PARTITION BY st.student_id, bfdf.as_partition_downfill ORDER BY st.student_id, st.term_code) AS academic_standing_code_new " + 
				"  , academic_standing_code " + 
				"FROM sar_student_term st " + 
				"JOIN bf_df_values bfdf ON st.term_code = bfdf.term_code AND st.student_id = bfdf.student_id AND st.level_code = bfdf.level_code " + 
				"WHERE 1=1;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				null,
				null,
				22);
		assertDiagnosticCountBySeverity(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				null,
				1);
	}


	@Test
	public void queryOverEntityTest() {
		final String query = "SELECT aa.scbcrse_coll_code as [College Code], aa.*, aa.[Attribute Name] FROM [Student Coursework] as aa, [Institutional Course] as courses "
				+ " WHERE not aa.scbcrse_subj_code = courses.subj_code "
				+ " AND aa.scbcrse_crse_numb = courses.crse_numb ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
	}


	@Test
	public void largeStudentgeneralQueryParseTest() {
		// PSS Parser Event Walker does flag a large number of ambiguous column references. Many of the subqueries
		// do not use qualified table references so the parser is correct in throwing the errors.
		// In reality, though, if we were running the query against an actual database, these ambiguous columns would likely be resolved directly from the 
		// database schema. Hence when presenting these ambiguous column errors, it might be best to turn them into warnings downstream
		// and allow the datbase to try to resolve them. We'll consider whether this error type should
		// really be fatal or just a severe warning. 
	
		final String query = "select " + " population.spriden_id AS STUDENT_ID "	// line 1
				+ " , population.spriden_first_name AS FIRST_NAME  , population.spriden_mi AS MIDDLE_INITIAL "
				+ " , population.spriden_last_name AS LAST_NAME "
				+ " , TO_CHAR(demographic.spbpers_birth_date, 'yyyymmdd') AS DATE_OF_BIRTH "
				+ " , NVL(demographic.spbpers_sex,'') AS GENDER , NVL(f.goremal_email_address,'') AS EMAIL_ID "
				+ " , NVL(demographic.spbpers_ethn_code,'') AS ETHNICITY_CD "
				+ " , NVL(demographic.spbpers_dead_ind,'') AS DECEASED_IND , '' AS Field10  , "
				+ " \n ( select CASE WHEN sgbstdn.sgbstdn_resd_code in ('G') THEN 'Y' ELSE 'N' END " // line 2
				+ " FROM sgbstdn  JOIN ( " 
				+ " \n select sgbstdn_pidm, max(sgbstdn_term_code_eff) AS max_term "   // line 3
				+ " FROM sgbstdn WHERE sgbstdn_levl_code = 'US' GROUP BY sgbstdn_pidm "
				+ " ) m on sgbstdn.sgbstdn_pidm = m.sgbstdn_pidm and sgbstdn.sgbstdn_term_code_eff = m.max_term "
				+ " WHERE sgbstdn.sgbstdn_pidm = population.spriden_pidm ) AS INTERNATIONAL_IND "
				+ " , NVL(GOBINTL.GOBINTL_NATN_CODE_LEGAL,'') AS COUNTRY_CD , '' AS INST_FIRST_TERM_ID "
				+ " , hs.stvsbgi_desc AS HS_NAME , sobsbgi.sobsbgi_city AS HS_CITY "
				+ " , sobsbgi.sobsbgi_stat_code AS HS_STATE , hs.sorhsch_class_size AS HS_SIZE "
				+ " , hs.sorhsch_percentile AS HS_PERCENTILE , hs.sorhsch_class_rank AS HS_RANK "
				+ " , hs.sorhsch_gpa AS HS_GPA , '' AS Field21 "
				+ " , hp.sprtele_phone_area || hp.sprtele_phone_number AS HOME_PHONE , '' AS Field23 "
				+ " , cp.sprtele_phone_area || cp.sprtele_phone_number AS MOBILE_PHONE "
				+ " , address.spraddr_street_line1 AS MAIL_ADDRESS1 "
				+ " , address.spraddr_street_line2 AS MAIL_ADDRESS2 , address.spraddr_city AS MAIL_CITY "
				+ " , address.spraddr_stat_code AS MAIL_STATE , address.spraddr_zip AS MAIL_ZIP_CODE "
				+ " , '' AS Field30 , '' AS Field31 , demographic.SPBPERS_LGCY_CODE AS STUDENT_LEGACY_CD "
				+ " , \n ( select SGBSTDN_ADMT_CODE FROM sgbstdn "   // line 4
				+ " WHERE sgbstdn_pidm = population.spriden_pidm AND sgbstdn_levl_code = 'US'  "
				+ " AND sgbstdn_term_code_eff = ( " 
				+ " \n select max(sgbstdn_term_code_eff)  FROM sgbstdn "   // line 5
				+ " WHERE sgbstdn_pidm = population.spriden_pidm  AND sgbstdn_levl_code = 'US')		 "
				+ " ) AS STUDENT_ADMIT_CD "
				+ " , CASE WHEN shrtrit_primary.shrtrit_sbgi_code is not null THEN 'Y' ELSE 'N' END AS TRANSFER_STUDENT_IND "
				+ " , shrtrit_primary.shrtrit_sbgi_code AS TRANSFER_INST_CD "
				+ " , NVL(demographic.SPBPERS_VERA_IND,'N') as VETERAN_IND "
				+ " , CASE WHEN readmit.saradap_pidm IS NULL THEN 'N' ELSE 'Y' END as READMIT_IND "
				+ " , CASE WHEN RCRAPP3_1.pidm IS NOT NULL THEN 'Y' ELSE 'N' END AS FIRST_GEN_IND  "
				+ " , sobsbgi.SOBSBGI_ZIP AS HS_ZIP_CODE , '' AS ADMISSION_ZIP_CODE , '' AS REGION_CD "
				+ " , ( " 
				+ " \n select CASE WHEN SGBSTDN_STST_CODE = 'AS' THEN 'Y' ELSE 'N' END "  // line 6
				+ " FROM sgbstdn WHERE sgbstdn_pidm = population.spriden_pidm "
				+ " AND sgbstdn_levl_code = 'US'			 "
				+ " AND sgbstdn_term_code_eff = ( " 
				+ " \n select max(sgbstdn_term_code_eff) FROM sgbstdn "  // line 7
				+ " WHERE sgbstdn_pidm = population.spriden_pidm 	AND sgbstdn_levl_code = 'US') ) AS ACTIVE_IND "
				+ " FROM  "
				// --STUDENT POPULATION
				+ " ( " 
				+ " \n select "  // line 8
				+ " spriden_id, spriden_pidm, terms.max_term, spriden_first_name, spriden_last_name, spriden_mi "
				+ " FROM ( "
				+ " \n select spriden_id, spriden_pidm, spriden_first_name, spriden_last_name, spriden_mi FROM spriden WHERE spriden_change_ind is null " // line 9
				+ " ) spriden JOIN "
				+ " \n ( select pidm, max(term) AS max_term FROM ( "  // line 10
				+ " \n select shrtgpa_pidm AS pidm, shrtgpa_term_code AS term "  // line 11
				+ "	FROM shrtgpa WHERE shrtgpa_levl_code = 'US'	 "
				+ " UNION ALL "
				+ "\n select shrtrce_pidm AS pidm, shrtrce_term_code_eff AS term  FROM shrtrce WHERE shrtrce_levl_code = 'US'	 "  // line 12
				+ " UNION ALL \n select sfrstcr_pidm AS pidm, sfrstcr_term_code AS term FROM sfrstcr "  // line 13
				+ " JOIN stvterm ON stvterm_code = sfrstcr_term_code WHERE sfrstcr_levl_code = 'US'		 "
				+ " AND stvterm_end_date > SYSDATE - 365 "
				+ " UNION ALL \n select sgbstdn_pidm AS pidm, sgbstdn_term_code_eff AS term 	FROM sgbstdn WHERE sgbstdn_levl_code = 'US'	 "  // line 14
				+ " ) x  GROUP BY pidm ) terms ON spriden.spriden_pidm = terms.pidm "
				+ " JOIN STVTERM termDates ON termDates.STVTERM_CODE = terms.max_term "
				+ " GROUP BY spriden_id, spriden_pidm, terms.max_term, spriden_first_name, spriden_last_name, spriden_mi "
				+ " HAVING max(termDates.STVTERM_START_DATE) > SYSDATE - 730 "
				+ " ) population "
				// --DEMOGRAPHIC INFORMATION
				+ " LEFT OUTER JOIN spbpers demographic  ON population.spriden_pidm = demographic.spbpers_pidm "
				// --ADDRESS
				+ " LEFT OUTER JOIN ( "
				+ " \n select spraddr.spraddr_pidm, spraddr.spraddr_street_line1, spraddr.spraddr_street_line2, spraddr_city, spraddr_stat_code, spraddr_zip "
				+ " FROM spraddr  JOIN ( " // line 15
				+ " \n select spraddr_pidm, max(spraddr_seqno) AS max_seqno "  // line 16
				+ " FROM spraddr WHERE spraddr_atyp_code = 'MA'							 "
				+ " AND spraddr_status_ind is null GROUP BY spraddr_pidm "
				+ " ) addr_max ON spraddr.spraddr_pidm = addr_max.spraddr_pidm AND spraddr.spraddr_seqno = addr_max.max_seqno "
				+ " WHERE spraddr.spraddr_atyp_code = 'MA' "
				+ " AND spraddr.spraddr_status_ind is null  AND spraddr_FROM_date <= sysdate "
				+ " AND (spraddr_to_date >= sysdate or spraddr_to_date is null) "
				+ " ) address ON population.spriden_pidm = address.spraddr_pidm "
				// --HOME PHONE
				+ " LEFT OUTER JOIN ( "
				+ " \n select sprtele.sprtele_pidm, sprtele.sprtele_phone_area, sprtele.sprtele_phone_number "  // line 17
				+ " FROM sprtele  JOIN ( " 
				+ " \n select sprtele_pidm, max(sprtele_seqno) AS max_seqno "  // line 18
				+ " FROM sprtele   WHERE sprtele_tele_code = 'MA' 	 "
				+ " AND sprtele_status_ind is null GROUP BY sprtele_pidm "
				+ " ) tele_max ON sprtele.sprtele_pidm = tele_max.sprtele_pidm AND sprtele.sprtele_seqno = tele_max.max_seqno "
				+ " WHERE sprtele.sprtele_tele_code = 'MA'  "
				+ " AND sprtele.sprtele_status_ind is null  ) hp ON population.spriden_pidm = hp.sprtele_pidm "
				// --MOBILE PHONE
				+ " LEFT OUTER JOIN ( "
				+ " \n select sprtele.sprtele_pidm, sprtele.sprtele_phone_area, sprtele.sprtele_phone_number "  // line 19
				+ " FROM sprtele JOIN ( " 
				+ " \n select sprtele_pidm, max(sprtele_seqno) as max_seqno "  // line 20
				+ " FROM sprtele   WHERE sprtele_tele_code = 'CP'  "
				+ " AND sprtele_status_ind is null  GROUP BY sprtele_pidm "
				+ " ) tele_max ON sprtele.sprtele_pidm = tele_max.sprtele_pidm AND sprtele.sprtele_seqno = tele_max.max_seqno "
				+ " WHERE sprtele.sprtele_tele_code = 'CP'  "
				+ " AND sprtele.sprtele_status_ind is null  ) cp ON population.spriden_pidm = cp.sprtele_pidm "
				// --EMAIL
				+ " LEFT OUTER JOIN goremal f   ON population.spriden_pidm = f.goremal_pidm "
				+ " 	AND f.goremal_emal_code = 'GSU' AND f.goremal_status_ind = 'A' AND f.goremal_preferred_ind = 'Y' "
				+ " LEFT OUTER JOIN stvlgcy leg  ON demographic.spbpers_lgcy_code = leg.stvlgcy_code "
				// --HIGH SCHOOL
				+ " LEFT OUTER JOIN ( "
				+ " \n select sorhsch.sorhsch_pidm, sorhsch_gpa, sorhsch_class_rank, sorhsch_percentile, sorhsch_class_size, stvsbgi.stvsbgi_desc, sorhsch_sbgi_code "
				+ " FROM sorhsch  JOIN ( " // line 21
				+ " \n select sorhsch_pidm, max(sorhsch_activity_date) AS max_date "  // line 22
				+ " FROM sorhsch  GROUP BY sorhsch_pidm "
				+ " ) crit ON sorhsch.sorhsch_pidm = crit.sorhsch_pidm AND sorhsch.sorhsch_activity_date = crit.max_date "
				+ " JOIN stvsbgi on sorhsch.sorhsch_sbgi_code = stvsbgi.stvsbgi_code "
				+ " ) hs ON population.spriden_pidm = hs.sorhsch_pidm "
				// --HIGH SCHOOL PT2
				+ " LEFT OUTER JOIN sobsbgi   ON hs.sorhsch_sbgi_code = sobsbgi.sobsbgi_sbgi_code "
				// --COUNTRY CODE
				+ " LEFT OUTER JOIN gobintl   	ON gobintl.gobintl_pidm = population.spriden_pidm "
				// --TRANSFER INSTITUTION
				+ " LEFT OUTER JOIN ( " 
				+ " \n select *  FROM shrtrit  JOIN ( "  // line 23
				+ " \n select a.shrtrit_pidm AS pidm, max(a.shrtrit_seq_no) AS max_date  FROM shrtrit a "		// line 24
				+ " GROUP BY a.shrtrit_pidm "
				+ " ) shrtrit_max ON shrtrit.shrtrit_pidm = shrtrit_max.pidm AND shrtrit.shrtrit_seq_no = shrtrit_max.max_date "
				+ " ) shrtrit_primary ON shrtrit_primary.pidm = population.spriden_pidm "
				// --FIRST GEN INDICATOR
				+ " LEFT JOIN ( " 
				+ " \n select DISTINCT rcrapp3.rcrapp3_pidm as pidm  FROM rcrapp3  JOIN ( "  // line 25
				+ " \n select a.rcrapp3_pidm, max(a.rcrapp3_aidy_code) as max_aidy  FROM rcrapp3 a "  // line 26
				+ " GROUP BY a.rcrapp3_pidm, a.rcrapp3_seq_no "
				+ " ) rcrapp3_max on rcrapp3.rcrapp3_pidm = rcrapp3_max.rcrapp3_pidm AND rcrapp3.rcrapp3_aidy_code = rcrapp3_max.max_aidy "
				+ " WHERE rcrapp3.rcrapp3_seq_no = '1' AND (rcrapp3.RCRAPP3_FATHER_HI_GRADE IN ('1','2') OR rcrapp3.RCRAPP3_MOTHER_HI_GRADE IN ('1','2')) "
				+ " ) RCRAPP3_1 ON population.spriden_pidm = RCRAPP3_1.pidm "
				// READMIT INDICATOR
				+ " LEFT OUTER JOIN ( " 
				+ " \n select DISTINCT SARADAP_PIDM FROM SARADAP  "  // line 27
				+ " WHERE SARADAP_ADMT_CODE = 'RE' AND SARADAP_LEVL_CODE = 'US'  "
				// --MODIFY PER MEMBER
				+ " ) readmit ON readmit.SARADAP_PIDM = population.spriden_pidm  WHERE 1=1  ORDER BY 1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();
		assertDiagnosticListByCodeAndSeverity(snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				  "token=spriden_id line=8 char=9 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=spriden_pidm line=8 char=21 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=spriden_first_name line=8 char=51 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=spriden_last_name line=8 char=71 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=spriden_mi line=8 char=90 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=sfrstcr_pidm line=13 char=8 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=sfrstcr_term_code line=13 char=30 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=stvterm_code line=13 char=86 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=sfrstcr_levl_code line=13 char=125 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=stvterm_end_date line=13 char=157 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=SYSDATE line=13 char=176 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=sorhsch_gpa line=21 char=30 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=sorhsch_class_rank line=21 char=43 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=sorhsch_percentile line=21 char=63 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=sorhsch_class_size line=21 char=83 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=sorhsch_sbgi_code line=21 char=125 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING");
		assertDiagnosticListByCodeAndSeverity(snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				  "token=sfrstcr_pidm, sfrstcr_levl_code, sfrstcr_term_code, stvterm_end_date, stvterm_code, SYSDATE line=13 char=8 code=UNRESOLVED_UNQUALIFIED_COLUMNS severity=ERROR\n"
				+ "token=sorhsch_class_size, sorhsch_class_rank, sorhsch_gpa, sorhsch_percentile, sorhsch_sbgi_code line=21 char=83 code=UNRESOLVED_UNQUALIFIED_COLUMNS severity=ERROR");
	}


	@Test
	public void complexHiveQueryJoinTest() {
		// Part of this is the problem of correlated subquery. Each predicand is referencing a field from the outer join's 
		// from statement, but we haven't implemented that capability yet.
	
		final String query = "select " + " CASE   "
				+ " WHEN COALESCE( S948.OBSERVATION_TM>=S949.OBSERVATION_TM , FALSE) THEN S948.t_student_last_name   "
				+ " WHEN COALESCE( S949.OBSERVATION_TM>=S948.OBSERVATION_TM , FALSE) THEN S949.t_student_last_name   "
				+ " ELSE COALESCE(S948.t_student_last_name, S949.t_student_last_name) END AS t_student_last_name, "
				+ " CASE   "
				+ " WHEN COALESCE( S948.OBSERVATION_TM>=S949.OBSERVATION_TM , FALSE) THEN S948.t_sur_name   "
				+ " WHEN COALESCE( S949.OBSERVATION_TM>=S948.OBSERVATION_TM , FALSE) THEN S949.t_sur_name "
				+ " ELSE COALESCE(S948.t_sur_name, S949.t_sur_name) END AS t_sur_name, " + " CASE   "
				+ " WHEN COALESCE( S948.OBSERVATION_TM>=S949.OBSERVATION_TM , FALSE) THEN S948.t_student_first_name   "
				+ " WHEN COALESCE( S949.OBSERVATION_TM>=S948.OBSERVATION_TM , FALSE) THEN S949.t_student_first_name   "
				+ " ELSE COALESCE(S948.t_student_first_name, S949.t_student_first_name) END AS t_student_first_name "
				+ " FROM ( " 
				+ " \n select t_student_first_name, t_sur_name, t_student_last_name, k_stfd, OBSERVATION_TM from ( " 
				+ " \n select t_student_first_name, t_sur_name, t_student_last_name, k_stfd, OBSERVATION_TM,  "
				+ " rank() OVER (partition by k_stfd order by OBSERVATION_TM desc, row_num desc) AS key_rank "
				+ " from ( " 
				+ " \n select   DOB AS t_student_first_name,  NAME AS t_sur_name, "
				+ " LOCATION AS t_student_last_name,  NAME AS k_stfd,  OBSERVATION_TM,  "
				+ " pantodev.row_num() as row_num  FROM pantodev.23810_949  WHERE  "
				+ " OBSERVATION_DT <= 20160321  "
				+ " AND unix_timestamp(OBSERVATION_TM) <= unix_timestamp('2016-03-21 10:43:15.0')  ) a "
				+ " ) b where key_rank =1) S949  " 
				+ "\n FULL OUTER JOIN ( " 
				+ "  \n select t_student_first_name, t_sur_name, t_student_last_name, k_stfd, OBSERVATION_TM " 
				+ " from ( "
				+" \n select t_student_first_name, t_sur_name, t_student_last_name, k_stfd, OBSERVATION_TM,  "
				+ " rank() OVER (partition by k_stfd order by OBSERVATION_TM desc, row_num desc) AS key_rank "
				+ " from ( " 
				+ " \n select DOB AS t_student_first_name, NAME AS t_sur_name, "
				+ " LOCATION AS t_student_last_name, NAME AS k_stfd, OBSERVATION_TM,  "
				+ " pantodev.row_num() as row_num FROM pantodev.23810_948 WHERE OBSERVATION_DT <= 20160309  "
				+ " AND unix_timestamp(OBSERVATION_TM) <= unix_timestamp('2016-03-09 12:54:18.0') ) a "
				+ " ) b where key_rank =1 " + " ) S948  ON (S949.k_stfd=S948.k_stfd) where  "
				+ "\n (((unix_timestamp(S949.observation_tm) > unix_timestamp('1900-01-01 00:00:00.0'))  "
				+ "  AND (unix_timestamp(S949.observation_tm) <= unix_timestamp('2016-03-30 11:04:40.484'))) "
				+ " OR ((unix_timestamp(S948.observation_tm) > unix_timestamp('1900-01-01 00:00:00.0')) "
				+ " AND (unix_timestamp(S948.observation_tm) <= unix_timestamp('2016-03-30 11:04:40.484')))) ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
	}


	@Test
	public void authorizationQueryTest() {

		String sql = "select RECORD_TYPE as RECORD_TYPE, ACTION as ACTION, USER_ID as PRIMARY_USER_ID, ";
		sql += "authn.is_active as IS_ACTIVE, authn.can_login as CAN_LOGIN, authn.send_activation as SEND_ACTIVATION, ";
		sql += "IS_ACTIVE, CAN_LOGIN, SEND_ACTIVATION, ";
    	sql += "FIRST_NAME as FIRST_NAME, LAST_NAME as LAST_NAME, ";
		sql += "authn.alt_user_id as ALT_USER_ID, ";
		sql += "\n ROLE_ID as ROLE_ID, "
				+ "EMAIL as EMAIL, ALT_EMAIL as ALT_EMAIL, ADDRESS_1 as ADDRESS_1, ADDRESS_2 as ADDRESS_2, CITY as CITY, STATE as STATE, POSTAL_CODE as POSTAL_CODE, HOME_PHONE as HOME_PHONE,"
				+ " CELL_PHONE as CELL_PHONE, WORK_PHONE as WORK_PHONE, GENDER as GENDER, ETHNICITY as ETHNICITY, DATE_OF_BIRTH as DATE_OF_BIRTH, TOTAL_CREDIT_HOURS as TOTAL_CREDIT_HOURS, CREDIT_HOURS_ATTEMPTED as CREDIT_HOURS_ATTEMPTED, "
				+ " MAJOR_ID as MAJOR_ID, STUDENT_ENROLLMENT_STATUS as STUDENT_ENROLLMENT_STATUS, STUDENT_ENROLLMENT_GOAL as STUDENT_ENROLLMENT_GOAL, ";
		sql += "authn.pin as PIN, authn.sso_id as SSO_ID, ";
		sql += "\n '' as ACT_TOTAL, '' as ACT_ENGLISH, "
				+ " '' as ACT_READING, '' as ACT_MATH, '' as ACT_SCIENCE, '' as SAT_TOTAL, '' as SAT_VERBAL, '' as SAT_MATH, '' as HIGH_SCHOOL_GPA, "
				+ " '' as FIRST_GENERATION_IND, '' as FATHER_EDUCATION, '' as MOTHER_EDUCATION, '' as HIGH_SCHOOL_ZIP_CODE, '' as HOUSEHOLD_INCOME, "
				+ " '' as SINGLE_PARENT_FAMILY_IND, '' as TRANSFER_GPA, '' as HOME_COLLEGE, '' as RECEIVE_TXT_MESSAGE_IND "
				+ "\n from "
				+ "(select a.record_type as RECORD_TYPE, a.action as ACTION, a.primary_user_id as USER_ID, first_name as FIRST_NAME, last_name as LAST_NAME, a.is_active as IS_ACTIVE, a.login_ind AS CAN_LOGIN, a.activate_email_ind as SEND_ACTIVATION, role_id as ROLE_ID, '' as MAJOR_ID,total_credit_hours as TOTAL_CREDIT_HOURS, attempted_credit_hours as CREDIT_HOURS_ATTEMPTED, "
				+ " email as EMAIL, '' as ALT_EMAIL, address_1 as ADDRESS_1, address_2 as ADDRESS_2, city as CITY, state as STATE, postal_code as POSTAL_CODE, home_phone as HOME_PHONE, cell_phone as CELL_PHONE, '' as WORK_PHONE, gender as GENDER, ethnicity as ETHNICITY, "
				+ " date_of_birth as DATE_OF_BIRTH, receive_txt_message_ind as RECEIVE_TXT_MESSAGE_IND, student_enrollment_status as STUDENT_ENROLLMENT_STATUS, student_enrollment_goal as STUDENT_ENROLLMENT_GOAL from "
				+ " studentTbl a left join "
				// start of student major
				+ "\n (select primary_user_id, total_credit_hours,attempted_credit_hours from (select primary_user_id, total_credit_hours,attempted_credit_hours,"
				+ "rank() over (partition by primary_user_id order by b.begin_date desc ,b.end_date desc) term_rank from "
				+ " studentMajorTbl  a, academicPeriodTbl "
				+ " b where a.term_id=b.external_id and a.total_credit_hours is not null and a.attempted_credit_hours is not null and length(trim(a.total_credit_hours)) > 0 and length(trim(a.attempted_credit_hours)) > 0) tbl where term_rank =1)"
				// end of student major
				+ " b on (a.primary_user_id = b.primary_user_id) ";
		sql += "\n union all "
				+ "\n select record_type as RECORD_TYPE, action as ACTION, primary_user_id as USER_ID, first_name as FIRST_NAME, last_name as LAST_NAME, is_active as IS_ACTIVE, login_ind AS CAN_LOGIN, activate_email_ind as SEND_ACTIVATION, role_id as ROLE_ID, '' as MAJOR_ID,'' as TOTAL_CREDIT_HOURS, '' as CREDIT_HOURS_ATTEMPTED, "
				+ " email as EMAIL, alt_email as ALT_EMAIL, '' as ADDRESS_1, '' as ADDRESS_2, '' as CITY, '' as STATE, '' as POSTAL_CODE, home_phone as HOME_PHONE, cell_phone as CELL_PHONE, work_phone as WORK_PHONE, '' as GENDER, '' as ETHNICITY, '' as DATE_OF_BIRTH, '' as RECEIVE_TXT_MESSAGE_IND, '' as STUDENT_ENROLLMENT_STATUS, '' as STUDENT_ENROLLMENT_GOAL from "
				+ " advisorTbl  staff";
		sql += " union all "
				+ "\n select record_type as RECORD_TYPE, action as ACTION, primary_user_id as USER_ID, first_name as FIRST_NAME, last_name as LAST_NAME,is_active as IS_ACTIVE, login_ind AS CAN_LOGIN, activate_email_ind as SEND_ACTIVATION,  role_id as ROLE_ID, '' as MAJOR_ID,'' as TOTAL_CREDIT_HOURS, '' as CREDIT_HOURS_ATTEMPTED, "
				+ " email as EMAIL, alt_email as ALT_EMAIL, '' as ADDRESS_1, '' as ADDRESS_2, '' as CITY, '' as STATE, '' as POSTAL_CODE, home_phone as HOME_PHONE, cell_phone as CELL_PHONE, work_phone as WORK_PHONE, '' as GENDER, '' as ETHNICITY, '' as DATE_OF_BIRTH, '' as RECEIVE_TXT_MESSAGE_IND, '' as STUDENT_ENROLLMENT_STATUS, '' as STUDENT_ENROLLMENT_GOAL from "
				+ " instructorTbl inst";
		sql += ") user";

		sql += " left join authorizationTbl authn on user.USER_ID = authn.primary_user_id";
		sql += " where user.USER_ID is not null and length(trim(user.USER_ID)) > 0";

		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		Snippet snippet = extractor.getSnippet();

		Assert.assertEquals("Table dictionary is wrong",
				"{studentmajortbl={total_credit_hours=[[@393,2447:2447='a',<381>,5:318], [@411,2537:2537='a',<381>,5:408]], attempted_credit_hours=[[@400,2484:2484='a',<381>,5:355], [@423,2580:2580='a',<381>,5:451]], term_id=[[@385,2419:2419='a',<381>,5:290]]}, studenttbl={total_credit_hours=[[@265,1563:1580='total_credit_hours',<381>,4:272]], login_ind=[[@245,1461:1461='a',<381>,4:170]], gender=[[@313,1865:1870='gender',<381>,4:574]], ethnicity=[[@317,1883:1891='ethnicity',<381>,4:592]], city=[[@289,1737:1740='city',<381>,4:446]], address_1=[[@281,1689:1697='address_1',<381>,4:398]], date_of_birth=[[@321,1908:1920='date_of_birth',<381>,4:617]], address_2=[[@285,1713:1721='address_2',<381>,4:422]], student_enrollment_goal=[[@333,2048:2070='student_enrollment_goal',<381>,4:757]], student_enrollment_status=[[@329,1992:2016='student_enrollment_status',<381>,4:701]], role_id=[[@257,1528:1534='role_id',<381>,4:237]], action=[[@219,1335:1335='a',<381>,4:44]], state=[[@293,1751:1755='state',<381>,4:460]], first_name=[[@231,1385:1394='first_name',<381>,4:94]], email=[[@273,1656:1660='email',<381>,4:365]], activate_email_ind=[[@251,1487:1487='a',<381>,4:196]], home_phone=[[@301,1795:1804='home_phone',<381>,4:504]], is_active=[[@239,1435:1435='a',<381>,4:144]], attempted_credit_hours=[[@269,1605:1626='attempted_credit_hours',<381>,4:314]], last_name=[[@235,1411:1419='last_name',<381>,4:120]], record_type=[[@213,1305:1305='a',<381>,4:14]], cell_phone=[[@305,1821:1830='cell_phone',<381>,4:530]], primary_user_id=[[@225,1355:1355='a',<381>,4:64], [@440,2642:2642='a',<381>,5:513]], receive_txt_message_ind=[[@325,1940:1962='receive_txt_message_ind',<381>,4:649]], postal_code=[[@297,1767:1777='postal_code',<381>,4:476]]}, instructortbl={activate_email_ind=[[@596,3561:3578='activate_email_ind',<381>,8:179]], home_phone=[[@644,3811:3820='home_phone',<381>,8:429]], is_active=[[@588,3513:3521='is_active',<381>,8:131]], login_ind=[[@592,3537:3545='login_ind',<381>,8:155]], work_phone=[[@652,3863:3872='work_phone',<381>,8:481]], last_name=[[@584,3490:3498='last_name',<381>,8:108]], record_type=[[@568,3390:3400='record_type',<381>,8:8]], cell_phone=[[@648,3837:3846='cell_phone',<381>,8:455]], primary_user_id=[[@576,3436:3450='primary_user_id',<381>,8:54]], role_id=[[@600,3601:3607='role_id',<381>,8:219]], alt_email=[[@620,3709:3717='alt_email',<381>,8:327]], action=[[@572,3418:3423='action',<381>,8:36]], first_name=[[@580,3464:3473='first_name',<381>,8:82]], email=[[@616,3693:3697='email',<381>,8:311]]}, authorizationtbl={STUDENT_ENROLLMENT_GOAL=[[@123,797:819='STUDENT_ENROLLMENT_GOAL',<381>,2:490]], alt_user_id=[[@45,272:276='authn',<381>,1:272]], USER_ID=[[@9,53:59='USER_ID',<381>,1:53]], STATE=[[@75,430:434='STATE',<381>,2:123]], CREDIT_HOURS_ATTEMPTED=[[@111,668:689='CREDIT_HOURS_ATTEMPTED',<381>,2:361]], IS_ACTIVE=[[@31,183:191='IS_ACTIVE',<381>,1:183], [@13,81:85='authn',<381>,1:81]], LAST_NAME=[[@41,248:256='LAST_NAME',<381>,1:248]], EMAIL=[[@55,328:332='EMAIL',<381>,2:21]], ETHNICITY=[[@99,570:578='ETHNICITY',<381>,2:263]], sso_id=[[@133,867:871='authn',<381>,2:560]], FIRST_NAME=[[@37,222:231='FIRST_NAME',<381>,1:222]], CAN_LOGIN=[[@33,194:202='CAN_LOGIN',<381>,1:194], [@19,111:115='authn',<381>,1:111]], pin=[[@127,849:853='authn',<381>,2:542]], SEND_ACTIVATION=[[@35,205:219='SEND_ACTIVATION',<381>,1:205], [@25,141:145='authn',<381>,1:141]], POSTAL_CODE=[[@79,446:456='POSTAL_CODE',<381>,2:139]], ALT_EMAIL=[[@59,344:352='ALT_EMAIL',<381>,2:37]], ROLE_ID=[[@51,308:314='ROLE_ID',<381>,2:1]], GENDER=[[@95,552:557='GENDER',<381>,2:245]], HOME_PHONE=[[@83,474:483='HOME_PHONE',<381>,2:167]], DATE_OF_BIRTH=[[@103,594:606='DATE_OF_BIRTH',<381>,2:287]], STUDENT_ENROLLMENT_STATUS=[[@119,741:765='STUDENT_ENROLLMENT_STATUS',<381>,2:434]], TOTAL_CREDIT_HOURS=[[@107,626:643='TOTAL_CREDIT_HOURS',<381>,2:319]], WORK_PHONE=[[@91,526:535='WORK_PHONE',<381>,2:219]], primary_user_id=[[@693,4117:4121='authn',<381>,8:735]], CELL_PHONE=[[@87,500:509='CELL_PHONE',<381>,2:193]], ACTION=[[@5,35:40='ACTION',<381>,1:35]], CITY=[[@71,416:419='CITY',<381>,2:109]], RECORD_TYPE=[[@1,7:17='RECORD_TYPE',<381>,1:7]], MAJOR_ID=[[@115,719:726='MAJOR_ID',<381>,2:412]], ADDRESS_1=[[@63,368:376='ADDRESS_1',<381>,2:61]], ADDRESS_2=[[@67,392:400='ADDRESS_2',<381>,2:85]]}, advisortbl={activate_email_ind=[[@479,2874:2891='activate_email_ind',<381>,7:180]], home_phone=[[@527,3123:3132='home_phone',<381>,7:429]], is_active=[[@471,2826:2834='is_active',<381>,7:132]], login_ind=[[@475,2850:2858='login_ind',<381>,7:156]], work_phone=[[@535,3175:3184='work_phone',<381>,7:481]], last_name=[[@467,2802:2810='last_name',<381>,7:108]], record_type=[[@451,2702:2712='record_type',<381>,7:8]], cell_phone=[[@531,3149:3158='cell_phone',<381>,7:455]], primary_user_id=[[@459,2748:2762='primary_user_id',<381>,7:54]], role_id=[[@483,2913:2919='role_id',<381>,7:219]], alt_email=[[@503,3021:3029='alt_email',<381>,7:327]], action=[[@455,2730:2735='action',<381>,7:36]], first_name=[[@463,2776:2785='first_name',<381>,7:82]], email=[[@499,3005:3009='email',<381>,7:311]]}, academicperiodtbl={end_date=[[@372,2339:2339='b',<381>,5:210]], begin_date=[[@367,2320:2320='b',<381>,5:191]], external_id=[[@389,2429:2429='b',<381>,5:300]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query column dictionary is wrong",
				"{union5={USER_ID=[[@689,4102:4105='user',<374>,8:720], [@697,4145:4148='user',<374>,8:763], [@708,4186:4189='user',<374>,8:804]]}, query4={STUDENT_ENROLLMENT_GOAL=[[@678,4011:4033='STUDENT_ENROLLMENT_GOAL',<381>,8:629]], USER_ID=[[@578,3455:3461='USER_ID',<381>,8:73]], STATE=[[@638,3785:3789='STATE',<381>,8:403]], CREDIT_HOURS_ATTEMPTED=[[@614,3668:3689='CREDIT_HOURS_ATTEMPTED',<381>,8:286]], LAST_NAME=[[@586,3503:3511='LAST_NAME',<381>,8:121]], IS_ACTIVE=[[@590,3526:3534='IS_ACTIVE',<381>,8:144]], EMAIL=[[@618,3702:3706='EMAIL',<381>,8:320]], ETHNICITY=[[@662,3909:3917='ETHNICITY',<381>,8:527]], FIRST_NAME=[[@582,3478:3487='FIRST_NAME',<381>,8:96]], CAN_LOGIN=[[@594,3550:3558='CAN_LOGIN',<381>,8:168]], SEND_ACTIVATION=[[@598,3583:3597='SEND_ACTIVATION',<381>,8:201]], POSTAL_CODE=[[@642,3798:3808='POSTAL_CODE',<381>,8:416]], ALT_EMAIL=[[@622,3722:3730='ALT_EMAIL',<381>,8:340]], ROLE_ID=[[@602,3612:3618='ROLE_ID',<381>,8:230]], GENDER=[[@658,3895:3900='GENDER',<381>,8:513]], HOME_PHONE=[[@646,3825:3834='HOME_PHONE',<381>,8:443]], DATE_OF_BIRTH=[[@666,3926:3938='DATE_OF_BIRTH',<381>,8:544]], STUDENT_ENROLLMENT_STATUS=[[@674,3978:4002='STUDENT_ENROLLMENT_STATUS',<381>,8:596]], TOTAL_CREDIT_HOURS=[[@610,3642:3659='TOTAL_CREDIT_HOURS',<381>,8:260]], RECEIVE_TXT_MESSAGE_IND=[[@670,3947:3969='RECEIVE_TXT_MESSAGE_IND',<381>,8:565]], WORK_PHONE=[[@654,3877:3886='WORK_PHONE',<381>,8:495]], CELL_PHONE=[[@650,3851:3860='CELL_PHONE',<381>,8:469]], ACTION=[[@574,3428:3433='ACTION',<381>,8:46]], CITY=[[@634,3773:3776='CITY',<381>,8:391]], RECORD_TYPE=[[@570,3405:3415='RECORD_TYPE',<381>,8:23]], MAJOR_ID=[[@606,3627:3634='MAJOR_ID',<381>,8:245]], ADDRESS_1=[[@626,3739:3747='ADDRESS_1',<381>,8:357]], ADDRESS_2=[[@630,3756:3764='ADDRESS_2',<381>,8:374]]}, query6={ACT_ENGLISH=[[@145,916:926='ACT_ENGLISH',<381>,3:24]], STUDENT_ENROLLMENT_GOAL=[[@125,824:846='STUDENT_ENROLLMENT_GOAL',<381>,2:517]], HIGH_SCHOOL_GPA=[[@173,1041:1055='HIGH_SCHOOL_GPA',<381>,3:149]], IS_ACTIVE=[[@17,100:108='IS_ACTIVE',<381>,1:100], [@31,183:191='IS_ACTIVE',<381>,1:183]], EMAIL=[[@57,337:341='EMAIL',<381>,2:30]], ETHNICITY=[[@101,583:591='ETHNICITY',<381>,2:276]], HOME_COLLEGE=[[@205,1246:1257='HOME_COLLEGE',<381>,3:354]], ACT_MATH=[[@153,955:962='ACT_MATH',<381>,3:63]], SAT_VERBAL=[[@165,1007:1016='SAT_VERBAL',<381>,3:115]], ALT_EMAIL=[[@61,357:365='ALT_EMAIL',<381>,2:50]], ACT_SCIENCE=[[@157,971:981='ACT_SCIENCE',<381>,3:79]], FIRST_GENERATION_IND=[[@177,1065:1084='FIRST_GENERATION_IND',<381>,3:173]], STUDENT_ENROLLMENT_STATUS=[[@121,770:794='STUDENT_ENROLLMENT_STATUS',<381>,2:463]], SAT_TOTAL=[[@161,990:998='SAT_TOTAL',<381>,3:98]], SINGLE_PARENT_FAMILY_IND=[[@197,1194:1217='SINGLE_PARENT_FAMILY_IND',<381>,3:302]], ACT_TOTAL=[[@141,899:907='ACT_TOTAL',<381>,3:7]], ALT_USER_ID=[[@49,293:303='ALT_USER_ID',<381>,1:293]], CELL_PHONE=[[@89,514:523='CELL_PHONE',<381>,2:207]], SSO_ID=[[@137,883:888='SSO_ID',<381>,2:576]], FATHER_EDUCATION=[[@181,1093:1108='FATHER_EDUCATION',<381>,3:201]], ADDRESS_1=[[@65,381:389='ADDRESS_1',<381>,2:74]], ADDRESS_2=[[@69,405:413='ADDRESS_2',<381>,2:98]], STATE=[[@77,439:443='STATE',<381>,2:132]], CREDIT_HOURS_ATTEMPTED=[[@113,694:715='CREDIT_HOURS_ATTEMPTED',<381>,2:387]], TRANSFER_GPA=[[@201,1226:1237='TRANSFER_GPA',<381>,3:334]], LAST_NAME=[[@43,261:269='LAST_NAME',<381>,1:261]], SAT_MATH=[[@169,1025:1032='SAT_MATH',<381>,3:133]], FIRST_NAME=[[@39,236:245='FIRST_NAME',<381>,1:236]], MOTHER_EDUCATION=[[@185,1117:1132='MOTHER_EDUCATION',<381>,3:225]], CAN_LOGIN=[[@23,130:138='CAN_LOGIN',<381>,1:130], [@33,194:202='CAN_LOGIN',<381>,1:194]], PRIMARY_USER_ID=[[@11,64:78='PRIMARY_USER_ID',<381>,1:64]], SEND_ACTIVATION=[[@29,166:180='SEND_ACTIVATION',<381>,1:166], [@35,205:219='SEND_ACTIVATION',<381>,1:205]], POSTAL_CODE=[[@81,461:471='POSTAL_CODE',<381>,2:154]], ROLE_ID=[[@53,319:325='ROLE_ID',<381>,2:12]], GENDER=[[@97,562:567='GENDER',<381>,2:255]], HOME_PHONE=[[@85,488:497='HOME_PHONE',<381>,2:181]], DATE_OF_BIRTH=[[@105,611:623='DATE_OF_BIRTH',<381>,2:304]], ACT_READING=[[@149,936:946='ACT_READING',<381>,3:44]], TOTAL_CREDIT_HOURS=[[@109,648:665='TOTAL_CREDIT_HOURS',<381>,2:341]], RECEIVE_TXT_MESSAGE_IND=[[@209,1266:1288='RECEIVE_TXT_MESSAGE_IND',<381>,3:374]], WORK_PHONE=[[@93,540:549='WORK_PHONE',<381>,2:233]], HOUSEHOLD_INCOME=[[@193,1169:1184='HOUSEHOLD_INCOME',<381>,3:277]], ACTION=[[@7,45:50='ACTION',<381>,1:45]], CITY=[[@73,424:427='CITY',<381>,2:117]], PIN=[[@131,862:864='PIN',<381>,2:555]], RECORD_TYPE=[[@3,22:32='RECORD_TYPE',<381>,1:22]], MAJOR_ID=[[@117,731:738='MAJOR_ID',<381>,2:424]], HIGH_SCHOOL_ZIP_CODE=[[@189,1141:1160='HIGH_SCHOOL_ZIP_CODE',<381>,3:249]]}, query0={primary_user_id=[[@351,2210:2224='primary_user_id',<381>,5:81], [@343,2138:2152='primary_user_id',<381>,5:9]], term_rank=[[@377,2356:2364='term_rank',<381>,5:227], [@433,2622:2630='term_rank',<381>,5:493]], total_credit_hours=[[@353,2227:2244='total_credit_hours',<381>,5:98], [@345,2155:2172='total_credit_hours',<381>,5:26]], attempted_credit_hours=[[@355,2246:2267='attempted_credit_hours',<381>,5:117], [@347,2174:2195='attempted_credit_hours',<381>,5:45]]}, query1={primary_user_id=[[@343,2138:2152='primary_user_id',<381>,5:9], [@444,2662:2662='b',<381>,5:533]], total_credit_hours=[[@345,2155:2172='total_credit_hours',<381>,5:26]], attempted_credit_hours=[[@347,2174:2195='attempted_credit_hours',<381>,5:45]]}, query2={STUDENT_ENROLLMENT_GOAL=[[@335,2075:2097='STUDENT_ENROLLMENT_GOAL',<381>,4:784]], USER_ID=[[@229,1376:1382='USER_ID',<381>,4:85]], STATE=[[@295,1760:1764='STATE',<381>,4:469]], CREDIT_HOURS_ATTEMPTED=[[@271,1631:1652='CREDIT_HOURS_ATTEMPTED',<381>,4:340]], LAST_NAME=[[@237,1424:1432='LAST_NAME',<381>,4:133]], IS_ACTIVE=[[@243,1450:1458='IS_ACTIVE',<381>,4:159]], EMAIL=[[@275,1665:1669='EMAIL',<381>,4:374]], ETHNICITY=[[@319,1896:1904='ETHNICITY',<381>,4:605]], FIRST_NAME=[[@233,1399:1408='FIRST_NAME',<381>,4:108]], CAN_LOGIN=[[@249,1476:1484='CAN_LOGIN',<381>,4:185]], SEND_ACTIVATION=[[@255,1511:1525='SEND_ACTIVATION',<381>,4:220]], POSTAL_CODE=[[@299,1782:1792='POSTAL_CODE',<381>,4:491]], ALT_EMAIL=[[@279,1678:1686='ALT_EMAIL',<381>,4:387]], ROLE_ID=[[@259,1539:1545='ROLE_ID',<381>,4:248]], GENDER=[[@315,1875:1880='GENDER',<381>,4:584]], HOME_PHONE=[[@303,1809:1818='HOME_PHONE',<381>,4:518]], DATE_OF_BIRTH=[[@323,1925:1937='DATE_OF_BIRTH',<381>,4:634]], STUDENT_ENROLLMENT_STATUS=[[@331,2021:2045='STUDENT_ENROLLMENT_STATUS',<381>,4:730]], TOTAL_CREDIT_HOURS=[[@267,1585:1602='TOTAL_CREDIT_HOURS',<381>,4:294]], RECEIVE_TXT_MESSAGE_IND=[[@327,1967:1989='RECEIVE_TXT_MESSAGE_IND',<381>,4:676]], WORK_PHONE=[[@311,1853:1862='WORK_PHONE',<381>,4:562]], CELL_PHONE=[[@307,1835:1844='CELL_PHONE',<381>,4:544]], ACTION=[[@223,1347:1352='ACTION',<381>,4:56]], CITY=[[@291,1745:1748='CITY',<381>,4:454]], RECORD_TYPE=[[@217,1322:1332='RECORD_TYPE',<381>,4:31]], MAJOR_ID=[[@263,1554:1561='MAJOR_ID',<381>,4:263]], ADDRESS_1=[[@283,1702:1710='ADDRESS_1',<381>,4:411]], ADDRESS_2=[[@287,1726:1734='ADDRESS_2',<381>,4:435]]}, query3={STUDENT_ENROLLMENT_GOAL=[[@561,3323:3345='STUDENT_ENROLLMENT_GOAL',<381>,7:629]], USER_ID=[[@461,2767:2773='USER_ID',<381>,7:73]], STATE=[[@521,3097:3101='STATE',<381>,7:403]], CREDIT_HOURS_ATTEMPTED=[[@497,2980:3001='CREDIT_HOURS_ATTEMPTED',<381>,7:286]], LAST_NAME=[[@469,2815:2823='LAST_NAME',<381>,7:121]], IS_ACTIVE=[[@473,2839:2847='IS_ACTIVE',<381>,7:145]], EMAIL=[[@501,3014:3018='EMAIL',<381>,7:320]], ETHNICITY=[[@545,3221:3229='ETHNICITY',<381>,7:527]], FIRST_NAME=[[@465,2790:2799='FIRST_NAME',<381>,7:96]], CAN_LOGIN=[[@477,2863:2871='CAN_LOGIN',<381>,7:169]], SEND_ACTIVATION=[[@481,2896:2910='SEND_ACTIVATION',<381>,7:202]], POSTAL_CODE=[[@525,3110:3120='POSTAL_CODE',<381>,7:416]], ALT_EMAIL=[[@505,3034:3042='ALT_EMAIL',<381>,7:340]], ROLE_ID=[[@485,2924:2930='ROLE_ID',<381>,7:230]], GENDER=[[@541,3207:3212='GENDER',<381>,7:513]], HOME_PHONE=[[@529,3137:3146='HOME_PHONE',<381>,7:443]], DATE_OF_BIRTH=[[@549,3238:3250='DATE_OF_BIRTH',<381>,7:544]], STUDENT_ENROLLMENT_STATUS=[[@557,3290:3314='STUDENT_ENROLLMENT_STATUS',<381>,7:596]], TOTAL_CREDIT_HOURS=[[@493,2954:2971='TOTAL_CREDIT_HOURS',<381>,7:260]], RECEIVE_TXT_MESSAGE_IND=[[@553,3259:3281='RECEIVE_TXT_MESSAGE_IND',<381>,7:565]], WORK_PHONE=[[@537,3189:3198='WORK_PHONE',<381>,7:495]], CELL_PHONE=[[@533,3163:3172='CELL_PHONE',<381>,7:469]], ACTION=[[@457,2740:2745='ACTION',<381>,7:46]], CITY=[[@517,3085:3088='CITY',<381>,7:391]], RECORD_TYPE=[[@453,2717:2727='RECORD_TYPE',<381>,7:23]], MAJOR_ID=[[@489,2939:2946='MAJOR_ID',<381>,7:245]], ADDRESS_1=[[@509,3051:3059='ADDRESS_1',<381>,7:357]], ADDRESS_2=[[@513,3068:3076='ADDRESS_2',<381>,7:374]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Interface is wrong",
				"[ACT_ENGLISH, STUDENT_ENROLLMENT_GOAL, STATE, HIGH_SCHOOL_GPA, CREDIT_HOURS_ATTEMPTED, TRANSFER_GPA, IS_ACTIVE, LAST_NAME, EMAIL, ETHNICITY, SAT_MATH, FIRST_NAME, MOTHER_EDUCATION, CAN_LOGIN, PRIMARY_USER_ID, HOME_COLLEGE, SEND_ACTIVATION, POSTAL_CODE, ACT_MATH, SAT_VERBAL, ALT_EMAIL, ROLE_ID, GENDER, HOME_PHONE, ACT_SCIENCE, FIRST_GENERATION_IND, DATE_OF_BIRTH, STUDENT_ENROLLMENT_STATUS, ACT_READING, TOTAL_CREDIT_HOURS, SAT_TOTAL, SINGLE_PARENT_FAMILY_IND, RECEIVE_TXT_MESSAGE_IND, ACT_TOTAL, WORK_PHONE, HOUSEHOLD_INCOME, ALT_USER_ID, CELL_PHONE, SSO_ID, ACTION, CITY, PIN, RECORD_TYPE, MAJOR_ID, FATHER_EDUCATION, ADDRESS_1, HIGH_SCHOOL_ZIP_CODE, ADDRESS_2]",
				extractor.getInterface().toString());
		assertDiagnosticListByCodeAndSeverity(snippet, "DUPLICATE_INTERFACE_COLUMNS",
				ParseDiagnostic.Severity.FATAL,
				"token=authn.is_active,IS_ACTIVE line=1 char=100 code=DUPLICATE_INTERFACE_COLUMNS severity=FATAL\n"
				+ "token=authn.can_login,CAN_LOGIN line=1 char=130 code=DUPLICATE_INTERFACE_COLUMNS severity=FATAL\n"
				+ "token=authn.send_activation,SEND_ACTIVATION line=1 char=166 code=DUPLICATE_INTERFACE_COLUMNS severity=FATAL");
		assertDiagnosticListByCodeAndSeverity(snippet, "AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"token=RECORD_TYPE line=1 char=22 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=ACTION line=1 char=45 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=USER_ID line=1 char=64 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=IS_ACTIVE line=1 char=100 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=CAN_LOGIN line=1 char=130 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=SEND_ACTIVATION line=1 char=166 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=FIRST_NAME line=1 char=236 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=LAST_NAME line=1 char=261 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=ROLE_ID line=2 char=12 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=EMAIL line=2 char=30 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=ALT_EMAIL line=2 char=50 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=ADDRESS_1 line=2 char=74 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=ADDRESS_2 line=2 char=98 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=CITY line=2 char=117 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=STATE line=2 char=132 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=POSTAL_CODE line=2 char=154 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=HOME_PHONE line=2 char=181 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=CELL_PHONE line=2 char=207 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=WORK_PHONE line=2 char=233 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=GENDER line=2 char=255 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=ETHNICITY line=2 char=276 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=DATE_OF_BIRTH line=2 char=304 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=TOTAL_CREDIT_HOURS line=2 char=341 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=CREDIT_HOURS_ATTEMPTED line=2 char=387 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=MAJOR_ID line=2 char=424 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=STUDENT_ENROLLMENT_STATUS line=2 char=463 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=STUDENT_ENROLLMENT_GOAL line=2 char=517 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=total_credit_hours line=4 char=294 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=attempted_credit_hours line=4 char=340 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=primary_user_id line=5 char=81 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=total_credit_hours line=5 char=98 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING\n"
				+ "token=attempted_credit_hours line=5 char=117 code=AMBIGUOUS_COLUMN_REFERENCE severity=SEVERE_WARNING");
		assertDiagnosticListByCodeAndSeverity(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				"token=primary_user_id, total_credit_hours, attempted_credit_hours line=5 char=81 code=UNRESOLVED_UNQUALIFIED_COLUMNS severity=ERROR");
	}


	@Test
	public void getRegistrationSqlTest() {
		/*
		 * Registration COLUMNS: RECORD_TYPE, ACTION, TERM_ID, PRIMARY_USER_ID,
		 * GROUP_ID, CLASSIFICATION, OVERALL_GPA, TERM_GPA
		 */

		String query = " SELECT reg.record_type as RECORD_TYPE, reg.action as ACTION, reg.term_id as TERM_ID, "
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
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=record_type, table_ref=reg}, alias=RECORD_TYPE}, 2={column={name=action, table_ref=reg}, alias=ACTION}, 3={column={name=term_id, table_ref=reg}, alias=TERM_ID}, 4={column={name=primary_user_id, table_ref=reg}, alias=PRIMARY_USER_ID}, 5={column={name=group_id, table_ref=reg}, alias=GROUP_ID}, 6={column={name=classification, table_ref=reg}, alias=CLASSIFICATION}, 7={column={name=cum_gpa, table_ref=gpatbl}, alias=OVERALL_GPA}, 8={column={name=term_gpa, table_ref=gpatbl}, alias=TERM_GPA}}, from={join={1={table={alias=reg, table=studentAcademicTbl}}, 2={join=leftouter, on={and={1={condition={left={column={name=primary_user_id, table_ref=reg}}, right={column={name=primary_user_id, table_ref=gpatbl}}, operator==}}, 2={condition={left={column={name=term_id, table_ref=reg}}, right={column={name=term_id, table_ref=gpatbl}}, operator==}}}}}, 3={table={alias=gpatbl, query={select={1={function={parameters={1={column={name=primary_user_id, table_ref=cgpa}}, 2={column={name=primary_user_id, table_ref=tgpa}}}, function_name=coalesce}, alias=primary_user_id}, 2={function={parameters={1={column={name=term_id, table_ref=cgpa}}, 2={column={name=term_id, table_ref=tgpa}}}, function_name=coalesce}, alias=term_id}, 3={function={parameters={1={column={name=key_value, table_ref=cgpa}}, 2={literal=''}}, function_name=coalesce}, alias=cum_gpa}, 4={function={parameters={1={column={name=key_value, table_ref=tgpa}}, 2={literal=''}}, function_name=coalesce}, alias=term_gpa}}, from={join={1={table={alias=cgpa, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=studentTermDataTbl}}, where={condition={left={column={name=key_column, table_ref=null}}, right={literal='cumGPAKey'}, operator==}}}}}, 2={join=fullouter, on={and={1={condition={left={column={name=primary_user_id, table_ref=cgpa}}, right={column={name=primary_user_id, table_ref=tgpa}}, operator==}}, 2={condition={left={column={name=term_id, table_ref=cgpa}}, right={column={name=term_id, table_ref=tgpa}}, operator==}}}}}, 3={table={alias=tgpa, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=studentTermDataTbl}}, where={condition={left={column={name=key_column, table_ref=null}}, right={literal='termGPAKey'}, operator==}}}}}}}}}}, 4={join=inner, on={condition={left={column={name=term_id, table_ref=reg}}, right={column={name=term_id, table_ref=tf}}, operator==}}}, 5={table={alias=tf, table=termFilterTbl}}}}}}",
		 extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[PRIMARY_USER_ID, TERM_GPA, ACTION, OVERALL_GPA, RECORD_TYPE, CLASSIFICATION, TERM_ID, GROUP_ID]",
		 extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
		 extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
		 "{termfiltertbl={term_id=[[@176,887:888='tf',<381>,1:887]]}, studentacademictbl={primary_user_id=[[@19,87:89='reg',<381>,1:87], [@151,765:767='reg',<381>,1:765]], group_id=[[@25,127:129='reg',<381>,1:127]], action=[[@7,40:42='reg',<381>,1:40]], term_id=[[@13,62:64='reg',<381>,1:62], [@159,812:814='reg',<381>,1:812], [@172,873:875='reg',<381>,1:873]], classification=[[@31,152:154='reg',<381>,1:152]], record_type=[[@1,8:10='reg',<381>,1:8]]}, studenttermdatatbl={key_column=[[@110,553:562='key_column',<381>,1:553], [@124,641:650='key_column',<381>,1:641]], *=[[@106,520:520='*',<291>,1:520], [@120,607:607='*',<291>,1:607]]}}",
		 extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={primary_user_id=[[@58,315:318='cgpa',<381>,1:315], [@131,677:680='cgpa',<381>,1:677]], *=[[@106,520:520='*',<291>,1:520]], term_id=[[@71,387:390='cgpa',<381>,1:387], [@139,724:727='cgpa',<381>,1:724]], key_value=[[@84,436:439='cgpa',<381>,1:436]]}, query1={primary_user_id=[[@62,336:339='tgpa',<381>,1:336], [@135,698:701='tgpa',<381>,1:698]], *=[[@120,607:607='*',<291>,1:607]], term_id=[[@75,400:403='tgpa',<381>,1:400], [@143,737:740='tgpa',<381>,1:737]], key_value=[[@95,476:479='tgpa',<381>,1:476]]}, query2={primary_user_id=[[@67,361:375='primary_user_id',<381>,1:361], [@155,785:790='gpatbl',<381>,1:785]], cum_gpa=[[@91,458:464='cum_gpa',<381>,1:458], [@37,191:196='gpatbl',<381>,1:191]], term_id=[[@80,417:423='term_id',<381>,1:417], [@163,824:829='gpatbl',<381>,1:824]], term_gpa=[[@102,498:505='term_gpa',<381>,1:498], [@43,222:227='gpatbl',<381>,1:222]]}, query3={PRIMARY_USER_ID=[[@23,110:124='PRIMARY_USER_ID',<381>,1:110]], TERM_GPA=[[@47,241:248='TERM_GPA',<381>,1:241]], ACTION=[[@11,54:59='ACTION',<381>,1:54]], OVERALL_GPA=[[@41,209:219='OVERALL_GPA',<381>,1:209]], RECORD_TYPE=[[@5,27:37='RECORD_TYPE',<381>,1:27]], CLASSIFICATION=[[@35,174:187='CLASSIFICATION',<381>,1:174]], TERM_ID=[[@17,77:83='TERM_ID',<381>,1:77]], GROUP_ID=[[@29,143:150='GROUP_ID',<381>,1:143]]}}",
		 extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		 "{def_query3={query_dictionary={PRIMARY_USER_ID=[[@23,110:124='PRIMARY_USER_ID',<381>,1:110]], TERM_GPA=[[@47,241:248='TERM_GPA',<381>,1:241]], ACTION=[[@11,54:59='ACTION',<381>,1:54]], OVERALL_GPA=[[@41,209:219='OVERALL_GPA',<381>,1:209]], RECORD_TYPE=[[@5,27:37='RECORD_TYPE',<381>,1:27]], CLASSIFICATION=[[@35,174:187='CLASSIFICATION',<381>,1:174]], TERM_ID=[[@17,77:83='TERM_ID',<381>,1:77]], GROUP_ID=[[@29,143:150='GROUP_ID',<381>,1:143]]}, table_dictionary={termfiltertbl={term_id=[[@176,887:888='tf',<381>,1:887]]}, studentacademictbl={primary_user_id=[[@19,87:89='reg',<381>,1:87], [@151,765:767='reg',<381>,1:765]], group_id=[[@25,127:129='reg',<381>,1:127]], action=[[@7,40:42='reg',<381>,1:40]], term_id=[[@13,62:64='reg',<381>,1:62], [@159,812:814='reg',<381>,1:812], [@172,873:875='reg',<381>,1:873]], classification=[[@31,152:154='reg',<381>,1:152]], record_type=[[@1,8:10='reg',<381>,1:8]]}}, filters=[{name=primary_user_id, table_ref=reg}, {name=primary_user_id, table_ref=gpatbl}, {name=term_id, table_ref=reg}, {name=term_id, table_ref=gpatbl}, {name=term_id, table_ref=tf}], interface={PRIMARY_USER_ID=[{name=primary_user_id, table_ref=reg}], TERM_GPA=[{name=term_gpa, table_ref=gpatbl}], ACTION=[{name=action, table_ref=reg}], OVERALL_GPA=[{name=cum_gpa, table_ref=gpatbl}], RECORD_TYPE=[{name=record_type, table_ref=reg}], CLASSIFICATION=[{name=classification, table_ref=reg}], TERM_ID=[{name=term_id, table_ref=reg}], GROUP_ID=[{name=group_id, table_ref=reg}]}, table_alias={tf=termFilterTbl, reg=studentAcademicTbl, gpatbl=query2}, def_query2={query_dictionary={primary_user_id=[[@67,361:375='primary_user_id',<381>,1:361], [@155,785:790='gpatbl',<381>,1:785]], term_gpa=[[@102,498:505='term_gpa',<381>,1:498], [@43,222:227='gpatbl',<381>,1:222]], cum_gpa=[[@91,458:464='cum_gpa',<381>,1:458], [@37,191:196='gpatbl',<381>,1:191]], term_id=[[@80,417:423='term_id',<381>,1:417], [@163,824:829='gpatbl',<381>,1:824]]}, def_query1={query_dictionary={primary_user_id=[[@62,336:339='tgpa',<381>,1:336], [@135,698:701='tgpa',<381>,1:698]], *=[[@120,607:607='*',<291>,1:607]], term_id=[[@75,400:403='tgpa',<381>,1:400], [@143,737:740='tgpa',<381>,1:737]], key_value=[[@95,476:479='tgpa',<381>,1:476]]}, table_dictionary={studenttermdatatbl={key_column=[[@124,641:650='key_column',<381>,1:641]], *=[[@120,607:607='*',<291>,1:607]]}}, filters=[{name=key_column, table_ref=studenttermdatatbl}], interface={*=[{name=*, table_ref=*}]}}, def_query0={query_dictionary={primary_user_id=[[@58,315:318='cgpa',<381>,1:315], [@131,677:680='cgpa',<381>,1:677]], *=[[@106,520:520='*',<291>,1:520]], term_id=[[@71,387:390='cgpa',<381>,1:387], [@139,724:727='cgpa',<381>,1:724]], key_value=[[@84,436:439='cgpa',<381>,1:436]]}, table_dictionary={studenttermdatatbl={key_column=[[@110,553:562='key_column',<381>,1:553], [@124,641:650='key_column',<381>,1:641]], *=[[@106,520:520='*',<291>,1:520], [@120,607:607='*',<291>,1:607]]}}, filters=[{name=key_column, table_ref=studenttermdatatbl}], interface={*=[{name=*, table_ref=*}]}}, filters=[{name=primary_user_id, table_ref=cgpa}, {name=primary_user_id, table_ref=tgpa}, {name=term_id, table_ref=cgpa}, {name=term_id, table_ref=tgpa}], interface={primary_user_id=[{name=primary_user_id, table_ref=cgpa}, {name=primary_user_id, table_ref=tgpa}], term_gpa=[{name=key_value, table_ref=tgpa}], cum_gpa=[{name=key_value, table_ref=cgpa}], term_id=[{name=term_id, table_ref=cgpa}, {name=term_id, table_ref=tgpa}]}, table_alias={cgpa=query0, tgpa=query1}}}}",
		  extractor.getSymbolTable().toString());
	}


	@Test
	public void getTermSqlTest() {
		/*
		 * Term COLUMNS: RECORD_TYPE, ACTION, EXTERNAL_ID, NAME, BEGIN_DATE,
		 * END_DATE
		 */
		String query = "select term.record_type as RECORD_TYPE, term.action as ACTION,  term.external_id as EXTERNAL_ID, "
				+ "\n term.name as NAME, datestr(term.begin_date, "
				+ "\n 'TERM_SOURCE_DATE_FORMAT', 'SSCPLUS_DEFAULT_DATE_FORMAT') as BEGIN_DATE, datestr(term.end_date, 'TERM_SOURCE_DATE_FORMAT', "
				+ "\n 'SSCPLUS_DEFAULT_DATE_FORMAT') as END_DATE from academicPeriodTbl " + " term";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=record_type, table_ref=term}, alias=RECORD_TYPE}, 2={column={name=action, table_ref=term}, alias=ACTION}, 3={column={name=external_id, table_ref=term}, alias=EXTERNAL_ID}, 4={column={name=name, table_ref=term}, alias=NAME}, 5={function={parameters={1={column={name=begin_date, table_ref=term}}, 2={literal='TERM_SOURCE_DATE_FORMAT'}, 3={literal='SSCPLUS_DEFAULT_DATE_FORMAT'}}, function_name=datestr}, alias=BEGIN_DATE}, 6={function={parameters={1={column={name=end_date, table_ref=term}}, 2={literal='TERM_SOURCE_DATE_FORMAT'}, 3={literal='SSCPLUS_DEFAULT_DATE_FORMAT'}}, function_name=datestr}, alias=END_DATE}}, from={table={alias=term, table=academicPeriodTbl}}}}",
		 extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ACTION, BEGIN_DATE, RECORD_TYPE, EXTERNAL_ID, END_DATE, NAME]",
		 extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
		 extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
		 "{academicperiodtbl={end_date=[[@40,226:229='term',<381>,3:82]], begin_date=[[@27,126:129='term',<381>,2:28]], name=[[@19,99:102='term',<381>,2:1]], action=[[@7,40:43='term',<381>,1:40]], external_id=[[@13,64:67='term',<381>,1:64]], record_type=[[@1,7:10='term',<381>,1:7]]}}",
		 extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={ACTION=[[@11,55:60='ACTION',<381>,1:55]], BEGIN_DATE=[[@36,206:215='BEGIN_DATE',<381>,3:62]], RECORD_TYPE=[[@5,27:37='RECORD_TYPE',<381>,1:27]], EXTERNAL_ID=[[@17,84:94='EXTERNAL_ID',<381>,1:84]], END_DATE=[[@49,304:311='END_DATE',<381>,4:35]], NAME=[[@23,112:115='NAME',<225>,2:14]]}}",
		  extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		 "{def_query0={query_dictionary={ACTION=[[@11,55:60='ACTION',<381>,1:55]], BEGIN_DATE=[[@36,206:215='BEGIN_DATE',<381>,3:62]], RECORD_TYPE=[[@5,27:37='RECORD_TYPE',<381>,1:27]], EXTERNAL_ID=[[@17,84:94='EXTERNAL_ID',<381>,1:84]], END_DATE=[[@49,304:311='END_DATE',<381>,4:35]], NAME=[[@23,112:115='NAME',<225>,2:14]]}, table_dictionary={academicperiodtbl={end_date=[[@40,226:229='term',<381>,3:82]], begin_date=[[@27,126:129='term',<381>,2:28]], name=[[@19,99:102='term',<381>,2:1]], action=[[@7,40:43='term',<381>,1:40]], external_id=[[@13,64:67='term',<381>,1:64]], record_type=[[@1,7:10='term',<381>,1:7]]}}, interface={ACTION=[{name=action, table_ref=term}], BEGIN_DATE=[{name=begin_date, table_ref=term}], RECORD_TYPE=[{name=record_type, table_ref=term}], EXTERNAL_ID=[{name=external_id, table_ref=term}], END_DATE=[{name=end_date, table_ref=term}], NAME=[{name=name, table_ref=term}]}, table_alias={term=academicPeriodTbl}}}",
		 extractor.getSymbolTable().toString());
	}


	@Test
	public void getCourseSqlTest() {
		/*
		 * Course COLUMNS: RECORD_TYPE, ACTION, EXTERNAL_ID, COURSE_ID, TITLE,
		 * CREDIT_HOURS
		 */
		String query = "select crs.record_type as RECORD_TYPE,crs.action as ACTION,concat_ws('-',crs.subject_code,crs.course_number) as EXTERNAL_ID, concat_ws('-',crs.subject_code,crs.course_number) as COURSE_ID, "
				+ " crs.course_title as TITLE, COALESCE(crs.credit_hour_low,crs.credit_hour_high,0) as CREDIT_HOURS from "
				+ " courseTbl crs";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=record_type, table_ref=crs}, alias=RECORD_TYPE}, 2={column={name=action, table_ref=crs}, alias=ACTION}, 3={function={parameters={1={literal='-'}, 2={column={name=subject_code, table_ref=crs}}, 3={column={name=course_number, table_ref=crs}}}, function_name=concat_ws}, alias=EXTERNAL_ID}, 4={function={parameters={1={literal='-'}, 2={column={name=subject_code, table_ref=crs}}, 3={column={name=course_number, table_ref=crs}}}, function_name=concat_ws}, alias=COURSE_ID}, 5={column={name=course_title, table_ref=crs}, alias=TITLE}, 6={function={parameters={1={column={name=credit_hour_low, table_ref=crs}}, 2={column={name=credit_hour_high, table_ref=crs}}, 3={literal=0}}, function_name=COALESCE}, alias=CREDIT_HOURS}}, from={table={alias=crs, table=courseTbl}}}}",
		 extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ACTION, RECORD_TYPE, EXTERNAL_ID, COURSE_ID, CREDIT_HOURS, TITLE]",
		 extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
		 extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
		 "{coursetbl={subject_code=[[@17,73:75='crs',<381>,1:73], [@32,139:141='crs',<381>,1:139]], course_number=[[@21,90:92='crs',<381>,1:90], [@36,156:158='crs',<381>,1:156]], course_title=[[@43,190:192='crs',<381>,1:190]], credit_hour_low=[[@51,226:228='crs',<381>,1:226]], action=[[@7,38:40='crs',<381>,1:38]], record_type=[[@1,7:9='crs',<381>,1:7]], credit_hour_high=[[@55,246:248='crs',<381>,1:246]]}}",
		 extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={ACTION=[[@11,52:57='ACTION',<381>,1:52]], RECORD_TYPE=[[@5,26:36='RECORD_TYPE',<381>,1:26]], EXTERNAL_ID=[[@26,112:122='EXTERNAL_ID',<381>,1:112]], COURSE_ID=[[@41,178:186='COURSE_ID',<381>,1:178]], CREDIT_HOURS=[[@62,273:284='CREDIT_HOURS',<381>,1:273]], TITLE=[[@47,210:214='TITLE',<381>,1:210]]}}",
		  extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		 "{def_query0={query_dictionary={ACTION=[[@11,52:57='ACTION',<381>,1:52]], RECORD_TYPE=[[@5,26:36='RECORD_TYPE',<381>,1:26]], EXTERNAL_ID=[[@26,112:122='EXTERNAL_ID',<381>,1:112]], COURSE_ID=[[@41,178:186='COURSE_ID',<381>,1:178]], CREDIT_HOURS=[[@62,273:284='CREDIT_HOURS',<381>,1:273]], TITLE=[[@47,210:214='TITLE',<381>,1:210]]}, table_dictionary={coursetbl={subject_code=[[@17,73:75='crs',<381>,1:73], [@32,139:141='crs',<381>,1:139]], course_number=[[@21,90:92='crs',<381>,1:90], [@36,156:158='crs',<381>,1:156]], course_title=[[@43,190:192='crs',<381>,1:190]], credit_hour_low=[[@51,226:228='crs',<381>,1:226]], action=[[@7,38:40='crs',<381>,1:38]], record_type=[[@1,7:9='crs',<381>,1:7]], credit_hour_high=[[@55,246:248='crs',<381>,1:246]]}}, interface={ACTION=[{name=action, table_ref=crs}], RECORD_TYPE=[{name=record_type, table_ref=crs}], EXTERNAL_ID=[{name=subject_code, table_ref=crs}, {name=course_number, table_ref=crs}], COURSE_ID=[{name=subject_code, table_ref=crs}, {name=course_number, table_ref=crs}], CREDIT_HOURS=[{name=credit_hour_low, table_ref=crs}, {name=credit_hour_high, table_ref=crs}], TITLE=[{name=course_title, table_ref=crs}]}, table_alias={crs=courseTbl}}}",
		  extractor.getSymbolTable().toString());
	}


	@Test
	public void getSectionSqlV6Test() {
		/*
		 * Section COLUMNS: RECORD_TYPE, ACTION, TERM_ID, COURSE_EXTERNAL_ID,
		 * SECTION_NAME, SECTION_TAGS
		 */
		final String query = "select s.record_type as RECORD_TYPE, s.action as ACTION, "
				+ "\n s.term_code as TERM_ID, concat_ws('-',s.subject_code,s.course_number) as COURSE_EXTERNAL_ID, "
				+ "\n case  when s.section_name is null or length(trim(s.section_name))=0  then ''  "
				+ "\n else s.section_name  end as SECTION_NAME, s.section_tag as SECTION_TAGS "
				+ "\n from sectionTbl s  inner join termFilterTbl tf  on s.term_code = tf.term_id ";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=record_type, table_ref=s}, alias=RECORD_TYPE}, 2={column={name=action, table_ref=s}, alias=ACTION}, 3={column={name=term_code, table_ref=s}, alias=TERM_ID}, 4={function={parameters={1={literal='-'}, 2={column={name=subject_code, table_ref=s}}, 3={column={name=course_number, table_ref=s}}}, function_name=concat_ws}, alias=COURSE_EXTERNAL_ID}, 5={alias=SECTION_NAME, case={clauses={1={then={literal=''}, when={or={1={condition={left={column={name=section_name, table_ref=s}}, operator=is null}}, 2={condition={left={function={parameters={1={function={parameters={1={column={name=section_name, table_ref=s}}}, function_name=trim}}}, function_name=length}}, right={literal=0}, operator==}}}}}}, else={column={name=section_name, table_ref=s}}}}, 6={column={name=section_tag, table_ref=s}, alias=SECTION_TAGS}}, from={join={1={table={alias=s, table=sectionTbl}}, 2={join=inner, on={condition={left={column={name=term_code, table_ref=s}}, right={column={name=term_id, table_ref=tf}}, operator==}}}, 3={table={alias=tf, table=termFilterTbl}}}}}}",
		 extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[SECTION_NAME, ACTION, COURSE_EXTERNAL_ID, SECTION_TAGS, RECORD_TYPE, TERM_ID]",
		 extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
		 extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
		 "{termfiltertbl={term_id=[[@80,373:374='tf',<381>,5:66]]}, sectiontbl={subject_code=[[@23,97:97='s',<381>,2:39]], course_number=[[@27,112:112='s',<381>,2:54]], section_name=[[@36,165:165='s',<381>,3:12], [@46,203:203='s',<381>,3:50], [@56,239:239='s',<381>,4:6]], section_tag=[[@63,276:276='s',<381>,4:43]], term_code=[[@13,59:59='s',<381>,2:1], [@76,359:359='s',<381>,5:52]], action=[[@7,37:37='s',<381>,1:37]], record_type=[[@1,7:7='s',<381>,1:7]]}}",
		  extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={SECTION_NAME=[[@61,262:273='SECTION_NAME',<381>,4:29]], ACTION=[[@11,49:54='ACTION',<381>,1:49]], COURSE_EXTERNAL_ID=[[@32,132:149='COURSE_EXTERNAL_ID',<381>,2:74]], SECTION_TAGS=[[@67,293:304='SECTION_TAGS',<381>,4:60]], RECORD_TYPE=[[@5,24:34='RECORD_TYPE',<381>,1:24]], TERM_ID=[[@17,74:80='TERM_ID',<381>,2:16]]}}",
		  extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		 "{def_query0={query_dictionary={SECTION_NAME=[[@61,262:273='SECTION_NAME',<381>,4:29]], ACTION=[[@11,49:54='ACTION',<381>,1:49]], COURSE_EXTERNAL_ID=[[@32,132:149='COURSE_EXTERNAL_ID',<381>,2:74]], SECTION_TAGS=[[@67,293:304='SECTION_TAGS',<381>,4:60]], RECORD_TYPE=[[@5,24:34='RECORD_TYPE',<381>,1:24]], TERM_ID=[[@17,74:80='TERM_ID',<381>,2:16]]}, table_dictionary={termfiltertbl={term_id=[[@80,373:374='tf',<381>,5:66]]}, sectiontbl={subject_code=[[@23,97:97='s',<381>,2:39]], course_number=[[@27,112:112='s',<381>,2:54]], section_name=[[@36,165:165='s',<381>,3:12], [@46,203:203='s',<381>,3:50], [@56,239:239='s',<381>,4:6]], section_tag=[[@63,276:276='s',<381>,4:43]], term_code=[[@13,59:59='s',<381>,2:1], [@76,359:359='s',<381>,5:52]], action=[[@7,37:37='s',<381>,1:37]], record_type=[[@1,7:7='s',<381>,1:7]]}}, filters=[{name=term_code, table_ref=s}, {name=term_id, table_ref=tf}], interface={SECTION_NAME=[{name=section_name, table_ref=s}], ACTION=[{name=action, table_ref=s}], COURSE_EXTERNAL_ID=[{name=subject_code, table_ref=s}, {name=course_number, table_ref=s}], SECTION_TAGS=[{name=section_tag, table_ref=s}], RECORD_TYPE=[{name=record_type, table_ref=s}], TERM_ID=[{name=term_code, table_ref=s}]}, table_alias={tf=termFilterTbl, s=sectionTbl}}}",
		  extractor.getSymbolTable().toString());
	}


	@Test
	public void getEnrollmentSqlTest() {
		/*
		 * Enrollment COLUMNS: RECORD_TYPE, ACTION, PRIMARY_USER_ID, TERM_ID,
		 * COURSE_EXTERNAL_ID, SECTION_NAME, MIDTERM_GRADE, FINAL_GRADE
		 */
		String query = "select cw.record_type as RECORD_TYPE, cw.action as ACTION, cw.student_id as PRIMARY_USER_ID, cw.term_code as TERM_ID, concat_ws('-',s.subject_code,s.course_number) "
				+ " as COURSE_EXTERNAL_ID, s.section_name as SECTION_NAME,cw.midterm_grade as MIDTERM_GRADE,cw.final_grade as "
				+ " FINAL_GRADE"
				+ " from courseWorkTbl  cw,  sectionTbl  s inner join termFilterTbl tf on cw.term_code = tf.term_id "
				+ "where cw.course_ref_no=s.course_ref_no and cw.term_code=s.term_code and cw.registration_status_cd in ('regCodes')";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
		 "{SQL={select={1={column={name=record_type, table_ref=cw}, alias=RECORD_TYPE}, 2={column={name=action, table_ref=cw}, alias=ACTION}, 3={column={name=student_id, table_ref=cw}, alias=PRIMARY_USER_ID}, 4={column={name=term_code, table_ref=cw}, alias=TERM_ID}, 5={function={parameters={1={literal='-'}, 2={column={name=subject_code, table_ref=s}}, 3={column={name=course_number, table_ref=s}}}, function_name=concat_ws}, alias=COURSE_EXTERNAL_ID}, 6={column={name=section_name, table_ref=s}, alias=SECTION_NAME}, 7={column={name=midterm_grade, table_ref=cw}, alias=MIDTERM_GRADE}, 8={column={name=final_grade, table_ref=cw}, alias=FINAL_GRADE}}, from={join={1={table={alias=cw, table=courseWorkTbl}}, 2={table={alias=s, table=sectionTbl}}, 3={join=inner, on={condition={left={column={name=term_code, table_ref=cw}}, right={column={name=term_id, table_ref=tf}}, operator==}}}, 4={table={alias=tf, table=termFilterTbl}}}}, where={and={1={condition={left={column={name=course_ref_no, table_ref=cw}}, right={column={name=course_ref_no, table_ref=s}}, operator==}}, 2={condition={left={column={name=term_code, table_ref=cw}}, right={column={name=term_code, table_ref=s}}, operator==}}, 3={in={item={column={name=registration_status_cd, table_ref=cw}}, in_list={list={1={literal='regCodes'}}}}}}}}}",
		  extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[SECTION_NAME, PRIMARY_USER_ID, ACTION, COURSE_EXTERNAL_ID, RECORD_TYPE, MIDTERM_GRADE, FINAL_GRADE, TERM_ID]",
		 extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
		 extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
		 "{termfiltertbl={term_id=[[@72,369:370='tf',<381>,1:369]]}, sectiontbl={subject_code=[[@29,132:132='s',<381>,1:132]], course_number=[[@33,147:147='s',<381>,1:147]], section_name=[[@40,188:188='s',<381>,1:188]], term_code=[[@88,436:436='s',<381>,1:436]], course_ref_no=[[@80,403:403='s',<381>,1:403]]}, courseworktbl={midterm_grade=[[@46,219:220='cw',<381>,1:219]], final_grade=[[@52,253:254='cw',<381>,1:253]], action=[[@7,38:39='cw',<381>,1:38]], course_ref_no=[[@76,386:387='cw',<381>,1:386]], student_id=[[@13,59:60='cw',<381>,1:59]], term_code=[[@19,93:94='cw',<381>,1:93], [@68,354:355='cw',<381>,1:354], [@84,423:424='cw',<381>,1:423]], registration_status_cd=[[@92,452:453='cw',<381>,1:452]], record_type=[[@1,7:8='cw',<381>,1:7]]}}",
		  extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={SECTION_NAME=[[@44,206:217='SECTION_NAME',<381>,1:206]], PRIMARY_USER_ID=[[@17,76:90='PRIMARY_USER_ID',<381>,1:76]], ACTION=[[@11,51:56='ACTION',<381>,1:51]], COURSE_EXTERNAL_ID=[[@38,168:185='COURSE_EXTERNAL_ID',<381>,1:168]], RECORD_TYPE=[[@5,25:35='RECORD_TYPE',<381>,1:25]], MIDTERM_GRADE=[[@50,239:251='MIDTERM_GRADE',<381>,1:239]], FINAL_GRADE=[[@56,272:282='FINAL_GRADE',<381>,1:272]], TERM_ID=[[@23,109:115='TERM_ID',<381>,1:109]]}}",
		  extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		 "{def_query0={query_dictionary={SECTION_NAME=[[@44,206:217='SECTION_NAME',<381>,1:206]], PRIMARY_USER_ID=[[@17,76:90='PRIMARY_USER_ID',<381>,1:76]], ACTION=[[@11,51:56='ACTION',<381>,1:51]], COURSE_EXTERNAL_ID=[[@38,168:185='COURSE_EXTERNAL_ID',<381>,1:168]], RECORD_TYPE=[[@5,25:35='RECORD_TYPE',<381>,1:25]], MIDTERM_GRADE=[[@50,239:251='MIDTERM_GRADE',<381>,1:239]], FINAL_GRADE=[[@56,272:282='FINAL_GRADE',<381>,1:272]], TERM_ID=[[@23,109:115='TERM_ID',<381>,1:109]]}, table_dictionary={termfiltertbl={term_id=[[@72,369:370='tf',<381>,1:369]]}, sectiontbl={subject_code=[[@29,132:132='s',<381>,1:132]], course_number=[[@33,147:147='s',<381>,1:147]], section_name=[[@40,188:188='s',<381>,1:188]], term_code=[[@88,436:436='s',<381>,1:436]], course_ref_no=[[@80,403:403='s',<381>,1:403]]}, courseworktbl={midterm_grade=[[@46,219:220='cw',<381>,1:219]], final_grade=[[@52,253:254='cw',<381>,1:253]], action=[[@7,38:39='cw',<381>,1:38]], course_ref_no=[[@76,386:387='cw',<381>,1:386]], student_id=[[@13,59:60='cw',<381>,1:59]], term_code=[[@19,93:94='cw',<381>,1:93], [@68,354:355='cw',<381>,1:354], [@84,423:424='cw',<381>,1:423]], registration_status_cd=[[@92,452:453='cw',<381>,1:452]], record_type=[[@1,7:8='cw',<381>,1:7]]}}, filters=[{name=term_code, table_ref=cw}, {name=term_id, table_ref=tf}, {name=course_ref_no, table_ref=cw}, {name=course_ref_no, table_ref=s}, {name=term_code, table_ref=s}, {name=registration_status_cd, table_ref=cw}], interface={SECTION_NAME=[{name=section_name, table_ref=s}], PRIMARY_USER_ID=[{name=student_id, table_ref=cw}], ACTION=[{name=action, table_ref=cw}], COURSE_EXTERNAL_ID=[{name=subject_code, table_ref=s}, {name=course_number, table_ref=s}], RECORD_TYPE=[{name=record_type, table_ref=cw}], MIDTERM_GRADE=[{name=midterm_grade, table_ref=cw}], FINAL_GRADE=[{name=final_grade, table_ref=cw}], TERM_ID=[{name=term_code, table_ref=cw}]}, table_alias={tf=termFilterTbl, s=sectionTbl, cw=courseWorkTbl}}}",
		  extractor.getSymbolTable().toString());
	}


	@Test
	public void getInstructionSqlTest() {
		/*
		 * Instructor Assignments COLUMNS: RECORD_TYPE, ACTION, TERM_ID,
		 * COURSE_EXTERNAL_ID, SECTION_NAME, PRIMARY_USER_ID
		 */
		String query = "select ia.record_type as RECORD_TYPE, ia.action as ACTION, ia.term_code as TERM_ID, concat_ws('-',ia.subject_code,ia.course_number) "
				+ "as COURSE_EXTERNAL_ID,ia.section_name as SECTION_NAME ,ia.instructor_id as PRIMARY_USER_ID "
				+ "from instructorAssgnmtTbl  ia inner join termFilterTbl  tf on " + "ia.term_code = tf.term_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
		 "{SQL={select={1={column={name=record_type, table_ref=ia}, alias=RECORD_TYPE}, 2={column={name=action, table_ref=ia}, alias=ACTION}, 3={column={name=term_code, table_ref=ia}, alias=TERM_ID}, 4={function={parameters={1={literal='-'}, 2={column={name=subject_code, table_ref=ia}}, 3={column={name=course_number, table_ref=ia}}}, function_name=concat_ws}, alias=COURSE_EXTERNAL_ID}, 5={column={name=section_name, table_ref=ia}, alias=SECTION_NAME}, 6={column={name=instructor_id, table_ref=ia}, alias=PRIMARY_USER_ID}}, from={join={1={table={alias=ia, table=instructorAssgnmtTbl}}, 2={join=inner, on={condition={left={column={name=term_code, table_ref=ia}}, right={column={name=term_id, table_ref=tf}}, operator==}}}, 3={table={alias=tf, table=termFilterTbl}}}}}}",
		  extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[SECTION_NAME, PRIMARY_USER_ID, ACTION, COURSE_EXTERNAL_ID, RECORD_TYPE, TERM_ID]",
		 extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
		 extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
		 "{instructorassgnmttbl={subject_code=[[@23,98:99='ia',<381>,1:98]], course_number=[[@27,114:115='ia',<381>,1:114]], section_name=[[@34,154:155='ia',<381>,1:154]], instructor_id=[[@40,187:188='ia',<381>,1:187]], term_code=[[@13,59:60='ia',<381>,1:59], [@53,285:286='ia',<381>,1:285]], action=[[@7,38:39='ia',<381>,1:38]], record_type=[[@1,7:8='ia',<381>,1:7]]}, termfiltertbl={term_id=[[@57,300:301='tf',<381>,1:300]]}}",
		  extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={SECTION_NAME=[[@38,173:184='SECTION_NAME',<381>,1:173]], PRIMARY_USER_ID=[[@44,207:221='PRIMARY_USER_ID',<381>,1:207]], ACTION=[[@11,51:56='ACTION',<381>,1:51]], COURSE_EXTERNAL_ID=[[@32,135:152='COURSE_EXTERNAL_ID',<381>,1:135]], RECORD_TYPE=[[@5,25:35='RECORD_TYPE',<381>,1:25]], TERM_ID=[[@17,75:81='TERM_ID',<381>,1:75]]}}",
		  extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		 "{def_query0={query_dictionary={SECTION_NAME=[[@38,173:184='SECTION_NAME',<381>,1:173]], PRIMARY_USER_ID=[[@44,207:221='PRIMARY_USER_ID',<381>,1:207]], ACTION=[[@11,51:56='ACTION',<381>,1:51]], COURSE_EXTERNAL_ID=[[@32,135:152='COURSE_EXTERNAL_ID',<381>,1:135]], RECORD_TYPE=[[@5,25:35='RECORD_TYPE',<381>,1:25]], TERM_ID=[[@17,75:81='TERM_ID',<381>,1:75]]}, table_dictionary={instructorassgnmttbl={subject_code=[[@23,98:99='ia',<381>,1:98]], course_number=[[@27,114:115='ia',<381>,1:114]], section_name=[[@34,154:155='ia',<381>,1:154]], instructor_id=[[@40,187:188='ia',<381>,1:187]], term_code=[[@13,59:60='ia',<381>,1:59], [@53,285:286='ia',<381>,1:285]], action=[[@7,38:39='ia',<381>,1:38]], record_type=[[@1,7:8='ia',<381>,1:7]]}, termfiltertbl={term_id=[[@57,300:301='tf',<381>,1:300]]}}, filters=[{name=term_code, table_ref=ia}, {name=term_id, table_ref=tf}], interface={SECTION_NAME=[{name=section_name, table_ref=ia}], PRIMARY_USER_ID=[{name=instructor_id, table_ref=ia}], ACTION=[{name=action, table_ref=ia}], COURSE_EXTERNAL_ID=[{name=subject_code, table_ref=ia}, {name=course_number, table_ref=ia}], RECORD_TYPE=[{name=record_type, table_ref=ia}], TERM_ID=[{name=term_code, table_ref=ia}]}, table_alias={tf=termFilterTbl, ia=instructorAssgnmtTbl}}}",
		  extractor.getSymbolTable().toString());
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
			unionStatement += "\n select secondary_record_type,action,group_id,primary_user_id from " + groupingTbls[i]
					+ " ";
		}

		sql += unionStatement
				+ ") user where user.primary_user_id is not null and length(trim(user.primary_user_id)) > 0";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
		 "{SQL={select={1={column={name=secondary_record_type, table_ref=user}, alias=RECORD_TYPE}, 2={column={name=action, table_ref=user}, alias=ACTION}, 3={column={name=group_id, table_ref=user}, alias=GROUP_ID}, 4={column={name=primary_user_id, table_ref=user}, alias=PRIMARY_USER_ID}}, from={table={alias=user, query={union={1={select={1={column={name=secondary_record_type, table_ref=null}}, 2={column={name=action, table_ref=null}}, 3={column={name=group_id, table_ref=null}}, 4={column={name=primary_user_id, table_ref=null}}}, from={table={alias=null, table=zeroTable}}}, 2={union={qualifier=all, operator=union}}, 3={select={1={column={name=secondary_record_type, table_ref=null}}, 2={column={name=action, table_ref=null}}, 3={column={name=group_id, table_ref=null}}, 4={column={name=primary_user_id, table_ref=null}}}, from={table={alias=null, table=firstTable}}}, 4={union={qualifier=all, operator=union}}, 5={select={1={column={name=secondary_record_type, table_ref=null}}, 2={column={name=action, table_ref=null}}, 3={column={name=group_id, table_ref=null}}, 4={column={name=primary_user_id, table_ref=null}}}, from={table={alias=null, table=secondTable}}}, 6={union={qualifier=all, operator=union}}, 7={select={1={column={name=secondary_record_type, table_ref=null}}, 2={column={name=action, table_ref=null}}, 3={column={name=group_id, table_ref=null}}, 4={column={name=primary_user_id, table_ref=null}}}, from={table={alias=null, table=thirdTable}}}, 8={union={qualifier=all, operator=union}}, 9={select={1={column={name=secondary_record_type, table_ref=null}}, 2={column={name=action, table_ref=null}}, 3={column={name=group_id, table_ref=null}}, 4={column={name=primary_user_id, table_ref=null}}}, from={table={alias=null, table=fourthTable}}}}}}}, where={and={1={condition={left={column={name=primary_user_id, table_ref=user}}, operator=is not null}}, 2={condition={left={function={parameters={1={function={parameters={1={column={name=primary_user_id, table_ref=user}}}, function_name=trim}}}, function_name=length}}, right={literal=0}, operator=>}}}}}}",
		  extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[PRIMARY_USER_ID, ACTION, RECORD_TYPE, GROUP_ID]",
		 extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
		 extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
		 "{zerotable={primary_user_id=[[@33,191:205='primary_user_id',<381>,1:191]], group_id=[[@31,182:189='group_id',<381>,1:182]], secondary_record_type=[[@27,153:173='secondary_record_type',<381>,1:153]], action=[[@29,175:180='action',<381>,1:175]]}, firsttable={primary_user_id=[[@45,280:294='primary_user_id',<381>,2:46]], group_id=[[@43,271:278='group_id',<381>,2:37]], secondary_record_type=[[@39,242:262='secondary_record_type',<381>,2:8]], action=[[@41,264:269='action',<381>,2:30]]}, secondtable={primary_user_id=[[@57,370:384='primary_user_id',<381>,3:46]], group_id=[[@55,361:368='group_id',<381>,3:37]], secondary_record_type=[[@51,332:352='secondary_record_type',<381>,3:8]], action=[[@53,354:359='action',<381>,3:30]]}, fourthtable={primary_user_id=[[@81,551:565='primary_user_id',<381>,5:46]], group_id=[[@79,542:549='group_id',<381>,5:37]], secondary_record_type=[[@75,513:533='secondary_record_type',<381>,5:8]], action=[[@77,535:540='action',<381>,5:30]]}, thirdtable={primary_user_id=[[@69,461:475='primary_user_id',<381>,4:46]], group_id=[[@67,452:459='group_id',<381>,4:37]], secondary_record_type=[[@63,423:443='secondary_record_type',<381>,4:8]], action=[[@65,445:450='action',<381>,4:30]]}}",
		  extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{union5={primary_user_id=[[@19,98:101='user',<374>,1:98], [@87,597:600='user',<374>,5:92], [@98,646:649='user',<374>,5:141]], group_id=[[@13,72:75='user',<374>,1:72]], secondary_record_type=[[@1,8:11='user',<374>,1:8]], action=[[@7,50:53='user',<374>,1:50]]}, query4={primary_user_id=[[@81,551:565='primary_user_id',<381>,5:46]], secondary_record_type=[[@75,513:533='secondary_record_type',<381>,5:8]], action=[[@77,535:540='action',<381>,5:30]], group_id=[[@79,542:549='group_id',<381>,5:37]]}, query6={PRIMARY_USER_ID=[[@23,122:136='PRIMARY_USER_ID',<381>,1:122]], ACTION=[[@11,65:70='ACTION',<381>,1:65]], RECORD_TYPE=[[@5,38:48='RECORD_TYPE',<381>,1:38]], GROUP_ID=[[@17,89:96='GROUP_ID',<381>,1:89]]}, query0={primary_user_id=[[@33,191:205='primary_user_id',<381>,1:191]], secondary_record_type=[[@27,153:173='secondary_record_type',<381>,1:153]], action=[[@29,175:180='action',<381>,1:175]], group_id=[[@31,182:189='group_id',<381>,1:182]]}, query1={primary_user_id=[[@45,280:294='primary_user_id',<381>,2:46]], secondary_record_type=[[@39,242:262='secondary_record_type',<381>,2:8]], action=[[@41,264:269='action',<381>,2:30]], group_id=[[@43,271:278='group_id',<381>,2:37]]}, query2={primary_user_id=[[@57,370:384='primary_user_id',<381>,3:46]], secondary_record_type=[[@51,332:352='secondary_record_type',<381>,3:8]], action=[[@53,354:359='action',<381>,3:30]], group_id=[[@55,361:368='group_id',<381>,3:37]]}, query3={primary_user_id=[[@69,461:475='primary_user_id',<381>,4:46]], secondary_record_type=[[@63,423:443='secondary_record_type',<381>,4:8]], action=[[@65,445:450='action',<381>,4:30]], group_id=[[@67,452:459='group_id',<381>,4:37]]}}",
		  extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		 "{def_query6={query_dictionary={PRIMARY_USER_ID=[[@23,122:136='PRIMARY_USER_ID',<381>,1:122]], ACTION=[[@11,65:70='ACTION',<381>,1:65]], RECORD_TYPE=[[@5,38:48='RECORD_TYPE',<381>,1:38]], GROUP_ID=[[@17,89:96='GROUP_ID',<381>,1:89]]}, filters=[{name=primary_user_id, table_ref=user}], interface={PRIMARY_USER_ID=[{name=primary_user_id, table_ref=user}], ACTION=[{name=action, table_ref=user}], RECORD_TYPE=[{name=secondary_record_type, table_ref=user}], GROUP_ID=[{name=group_id, table_ref=user}]}, def_union5={query_dictionary={primary_user_id=[[@19,98:101='user',<374>,1:98], [@87,597:600='user',<374>,5:92], [@98,646:649='user',<374>,5:141]], group_id=[[@13,72:75='user',<374>,1:72]], secondary_record_type=[[@1,8:11='user',<374>,1:8]], action=[[@7,50:53='user',<374>,1:50]]}, def_query1={query_dictionary={primary_user_id=[[@45,280:294='primary_user_id',<381>,2:46]], group_id=[[@43,271:278='group_id',<381>,2:37]], secondary_record_type=[[@39,242:262='secondary_record_type',<381>,2:8]], action=[[@41,264:269='action',<381>,2:30]]}, table_dictionary={firsttable={primary_user_id=[[@45,280:294='primary_user_id',<381>,2:46]], group_id=[[@43,271:278='group_id',<381>,2:37]], secondary_record_type=[[@39,242:262='secondary_record_type',<381>,2:8]], action=[[@41,264:269='action',<381>,2:30]]}}, interface={primary_user_id=[{name=primary_user_id, table_ref=firsttable}], group_id=[{name=group_id, table_ref=firsttable}], secondary_record_type=[{name=secondary_record_type, table_ref=firsttable}], action=[{name=action, table_ref=firsttable}]}}, def_query0={query_dictionary={primary_user_id=[[@33,191:205='primary_user_id',<381>,1:191]], group_id=[[@31,182:189='group_id',<381>,1:182]], secondary_record_type=[[@27,153:173='secondary_record_type',<381>,1:153]], action=[[@29,175:180='action',<381>,1:175]]}, table_dictionary={zerotable={primary_user_id=[[@33,191:205='primary_user_id',<381>,1:191]], group_id=[[@31,182:189='group_id',<381>,1:182]], secondary_record_type=[[@27,153:173='secondary_record_type',<381>,1:153]], action=[[@29,175:180='action',<381>,1:175]]}}, interface={primary_user_id=[{name=primary_user_id, table_ref=zerotable}], group_id=[{name=group_id, table_ref=zerotable}], secondary_record_type=[{name=secondary_record_type, table_ref=zerotable}], action=[{name=action, table_ref=zerotable}]}}, interface={primary_user_id=query_column, group_id=query_column, secondary_record_type=query_column, action=query_column}, def_query4={query_dictionary={primary_user_id=[[@81,551:565='primary_user_id',<381>,5:46]], group_id=[[@79,542:549='group_id',<381>,5:37]], secondary_record_type=[[@75,513:533='secondary_record_type',<381>,5:8]], action=[[@77,535:540='action',<381>,5:30]]}, table_dictionary={fourthtable={primary_user_id=[[@81,551:565='primary_user_id',<381>,5:46]], group_id=[[@79,542:549='group_id',<381>,5:37]], secondary_record_type=[[@75,513:533='secondary_record_type',<381>,5:8]], action=[[@77,535:540='action',<381>,5:30]]}}, interface={primary_user_id=[{name=primary_user_id, table_ref=fourthtable}], group_id=[{name=group_id, table_ref=fourthtable}], secondary_record_type=[{name=secondary_record_type, table_ref=fourthtable}], action=[{name=action, table_ref=fourthtable}]}}, def_query3={query_dictionary={primary_user_id=[[@69,461:475='primary_user_id',<381>,4:46]], group_id=[[@67,452:459='group_id',<381>,4:37]], secondary_record_type=[[@63,423:443='secondary_record_type',<381>,4:8]], action=[[@65,445:450='action',<381>,4:30]]}, table_dictionary={thirdtable={primary_user_id=[[@69,461:475='primary_user_id',<381>,4:46]], group_id=[[@67,452:459='group_id',<381>,4:37]], secondary_record_type=[[@63,423:443='secondary_record_type',<381>,4:8]], action=[[@65,445:450='action',<381>,4:30]]}}, interface={primary_user_id=[{name=primary_user_id, table_ref=thirdtable}], group_id=[{name=group_id, table_ref=thirdtable}], secondary_record_type=[{name=secondary_record_type, table_ref=thirdtable}], action=[{name=action, table_ref=thirdtable}]}}, def_query2={query_dictionary={primary_user_id=[[@57,370:384='primary_user_id',<381>,3:46]], group_id=[[@55,361:368='group_id',<381>,3:37]], secondary_record_type=[[@51,332:352='secondary_record_type',<381>,3:8]], action=[[@53,354:359='action',<381>,3:30]]}, table_dictionary={secondtable={primary_user_id=[[@57,370:384='primary_user_id',<381>,3:46]], group_id=[[@55,361:368='group_id',<381>,3:37]], secondary_record_type=[[@51,332:352='secondary_record_type',<381>,3:8]], action=[[@53,354:359='action',<381>,3:30]]}}, interface={primary_user_id=[{name=primary_user_id, table_ref=secondtable}], group_id=[{name=group_id, table_ref=secondtable}], secondary_record_type=[{name=secondary_record_type, table_ref=secondtable}], action=[{name=action, table_ref=secondtable}]}}}, table_alias={user=union5}}}",
		  extractor.getSymbolTable().toString());
	}


	@Test
	public void getTagSqlTest() {
		/*
		 * Tag COLUMNS: RECORD_TYPE, ACTION, TAG, GROUP ID, PRIMARY_USER_ID
		 */
		String sql = " select rec_type as RECORD_TYPE, action_cd as ACTION, "
				+ "tag_name as TAG, grp_id as GROUP_ID, user_id as PRIMARY_USER_ID from "
				+ " tagTbl where tag_name is not null and length(trim(tag_name)) > 0 "
				+ "and grp_id is not null and length(trim(grp_id)) > 0 "
				+ "and user_id is not null and length(trim(user_id)) > 0 ";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
		 "{SQL={select={1={column={name=rec_type, table_ref=null}, alias=RECORD_TYPE}, 2={column={name=action_cd, table_ref=null}, alias=ACTION}, 3={column={name=tag_name, table_ref=null}, alias=TAG}, 4={column={name=grp_id, table_ref=null}, alias=GROUP_ID}, 5={column={name=user_id, table_ref=null}, alias=PRIMARY_USER_ID}}, from={table={alias=null, table=tagTbl}}, where={and={1={condition={left={column={name=tag_name, table_ref=null}}, operator=is not null}}, 2={condition={left={function={parameters={1={function={parameters={1={column={name=tag_name, table_ref=null}}}, function_name=trim}}}, function_name=length}}, right={literal=0}, operator=>}}, 3={condition={left={column={name=grp_id, table_ref=null}}, operator=is not null}}, 4={condition={left={function={parameters={1={function={parameters={1={column={name=grp_id, table_ref=null}}}, function_name=trim}}}, function_name=length}}, right={literal=0}, operator=>}}, 5={condition={left={column={name=user_id, table_ref=null}}, operator=is not null}}, 6={condition={left={function={parameters={1={function={parameters={1={column={name=user_id, table_ref=null}}}, function_name=trim}}}, function_name=length}}, right={literal=0}, operator=>}}}}}}",
		  extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[PRIMARY_USER_ID, ACTION, RECORD_TYPE, TAG, GROUP_ID]",
		 extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
		 extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
		 "{tagtbl={rec_type=[[@1,8:15='rec_type',<381>,1:8]], user_id=[[@17,91:97='user_id',<381>,1:91], [@53,245:251='user_id',<381>,1:245], [@62,281:287='user_id',<381>,1:281]], tag_name=[[@9,54:61='tag_name',<381>,1:54], [@23,137:144='tag_name',<381>,1:137], [@32,174:181='tag_name',<381>,1:174]], grp_id=[[@13,71:76='grp_id',<381>,1:71], [@38,193:198='grp_id',<381>,1:193], [@47,228:233='grp_id',<381>,1:228]], action_cd=[[@5,33:41='action_cd',<381>,1:33]]}}",
		  extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={PRIMARY_USER_ID=[[@19,102:116='PRIMARY_USER_ID',<381>,1:102]], TAG=[[@11,66:68='TAG',<381>,1:66]], ACTION=[[@7,46:51='ACTION',<381>,1:46]], RECORD_TYPE=[[@3,20:30='RECORD_TYPE',<381>,1:20]], GROUP_ID=[[@15,81:88='GROUP_ID',<381>,1:81]]}}",
		  extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		 "{def_query0={query_dictionary={PRIMARY_USER_ID=[[@19,102:116='PRIMARY_USER_ID',<381>,1:102]], ACTION=[[@7,46:51='ACTION',<381>,1:46]], RECORD_TYPE=[[@3,20:30='RECORD_TYPE',<381>,1:20]], TAG=[[@11,66:68='TAG',<381>,1:66]], GROUP_ID=[[@15,81:88='GROUP_ID',<381>,1:81]]}, table_dictionary={tagtbl={rec_type=[[@1,8:15='rec_type',<381>,1:8]], user_id=[[@17,91:97='user_id',<381>,1:91], [@53,245:251='user_id',<381>,1:245], [@62,281:287='user_id',<381>,1:281]], tag_name=[[@9,54:61='tag_name',<381>,1:54], [@23,137:144='tag_name',<381>,1:137], [@32,174:181='tag_name',<381>,1:174]], grp_id=[[@13,71:76='grp_id',<381>,1:71], [@38,193:198='grp_id',<381>,1:193], [@47,228:233='grp_id',<381>,1:228]], action_cd=[[@5,33:41='action_cd',<381>,1:33]]}}, filters=[{name=tag_name, table_ref=tagtbl}, {name=grp_id, table_ref=tagtbl}, {name=user_id, table_ref=tagtbl}], interface={PRIMARY_USER_ID=[{name=user_id, table_ref=tagtbl}], ACTION=[{name=action_cd, table_ref=tagtbl}], RECORD_TYPE=[{name=rec_type, table_ref=tagtbl}], TAG=[{name=tag_name, table_ref=tagtbl}], GROUP_ID=[{name=grp_id, table_ref=tagtbl}]}}}",
		  extractor.getSymbolTable().toString());
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
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
		 "{SQL={select={1={column={name=record_type, table_ref=null}, alias=RECORD_TYPE}, 2={column={name=action, table_ref=null}, alias=ACTION}, 3={column={name=role_id, table_ref=null}, alias=ROLE_ID}, 4={column={name=primary_user_id, table_ref=null}, alias=PRIMARY_USER_ID}}, from={table={alias=null, table=authorizeTbl}}, where={and={1={condition={left={column={name=role_id, table_ref=null}}, operator=is not null}}, 2={condition={left={function={parameters={1={function={parameters={1={column={name=role_id, table_ref=null}}}, function_name=trim}}}, function_name=length}}, right={literal=0}, operator=>}}, 3={condition={left={column={name=primary_user_id, table_ref=null}}, operator=is not null}}, 4={condition={left={function={parameters={1={function={parameters={1={column={name=primary_user_id, table_ref=null}}}, function_name=trim}}}, function_name=length}}, right={literal=0}, operator=>}}}}}}",
		  extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[PRIMARY_USER_ID, ACTION, RECORD_TYPE, ROLE_ID]",
		 extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
		 extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
		 "{authorizetbl={primary_user_id=[[@13,74:88='primary_user_id',<381>,1:74], [@34,189:203='primary_user_id',<381>,1:189], [@43,233:247='primary_user_id',<381>,1:233]], role_id=[[@9,54:60='role_id',<381>,1:54], [@19,135:141='role_id',<381>,1:135], [@28,171:177='role_id',<381>,1:171]], action=[[@5,36:41='action',<381>,1:36]], record_type=[[@1,8:18='record_type',<381>,1:8]]}}",
		  extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={ROLE_ID=[[@11,65:71='ROLE_ID',<381>,1:65]], PRIMARY_USER_ID=[[@15,93:107='PRIMARY_USER_ID',<381>,1:93]], ACTION=[[@7,46:51='ACTION',<381>,1:46]], RECORD_TYPE=[[@3,23:33='RECORD_TYPE',<381>,1:23]]}}",
		  extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		 "{def_query0={query_dictionary={PRIMARY_USER_ID=[[@15,93:107='PRIMARY_USER_ID',<381>,1:93]], ACTION=[[@7,46:51='ACTION',<381>,1:46]], RECORD_TYPE=[[@3,23:33='RECORD_TYPE',<381>,1:23]], ROLE_ID=[[@11,65:71='ROLE_ID',<381>,1:65]]}, table_dictionary={authorizetbl={primary_user_id=[[@13,74:88='primary_user_id',<381>,1:74], [@34,189:203='primary_user_id',<381>,1:189], [@43,233:247='primary_user_id',<381>,1:233]], role_id=[[@9,54:60='role_id',<381>,1:54], [@19,135:141='role_id',<381>,1:135], [@28,171:177='role_id',<381>,1:171]], action=[[@5,36:41='action',<381>,1:36]], record_type=[[@1,8:18='record_type',<381>,1:8]]}}, filters=[{name=role_id, table_ref=authorizetbl}, {name=primary_user_id, table_ref=authorizetbl}], interface={PRIMARY_USER_ID=[{name=primary_user_id, table_ref=authorizetbl}], ACTION=[{name=action, table_ref=authorizetbl}], RECORD_TYPE=[{name=record_type, table_ref=authorizetbl}], ROLE_ID=[{name=role_id, table_ref=authorizetbl}]}}}",
		  extractor.getSymbolTable().toString());
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
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
		 "{SQL={select={1={column={name=record_type, table_ref=null}, alias=RECORD_TYPE}, 2={column={name=action, table_ref=null}, alias=ACTION}, 3={column={name=external_id, table_ref=null}, alias=EXTERNAL_ID}, 4={column={name=name, table_ref=null}, alias=NAME}, 5={column={name=group_id, table_ref=null}, alias=GROUP_ID}}, from={table={alias=null, table=categoryTbl}}, where={and={1={condition={left={column={name=external_id, table_ref=null}}, operator=is not null}}, 2={condition={left={function={parameters={1={function={parameters={1={column={name=external_id, table_ref=null}}}, function_name=trim}}}, function_name=length}}, right={literal=0}, operator=>}}, 3={condition={left={column={name=name, table_ref=null}}, operator=is not null}}, 4={condition={left={function={parameters={1={function={parameters={1={column={name=name, table_ref=null}}}, function_name=trim}}}, function_name=length}}, right={literal=0}, operator=>}}, 5={condition={left={column={name=group_id, table_ref=null}}, operator=is not null}}, 6={condition={left={function={parameters={1={function={parameters={1={column={name=group_id, table_ref=null}}}, function_name=trim}}}, function_name=length}}, right={literal=0}, operator=>}}}}}}",
		  extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ACTION, RECORD_TYPE, EXTERNAL_ID, NAME, GROUP_ID]",
		 extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
		 extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
		 "{categorytbl={group_id=[[@17,96:103='group_id',<381>,1:96], [@53,251:258='group_id',<381>,1:251], [@62,288:295='group_id',<381>,1:288]], name=[[@13,82:85='name',<225>,1:82], [@38,203:206='name',<225>,1:203], [@47,236:239='name',<225>,1:236]], action=[[@5,36:41='action',<381>,1:36]], external_id=[[@9,54:64='external_id',<381>,1:54], [@23,141:151='external_id',<381>,1:141], [@32,181:191='external_id',<381>,1:181]], record_type=[[@1,8:18='record_type',<381>,1:8]]}}",
		  extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={ACTION=[[@7,46:51='ACTION',<381>,1:46]], RECORD_TYPE=[[@3,23:33='RECORD_TYPE',<381>,1:23]], EXTERNAL_ID=[[@11,69:79='EXTERNAL_ID',<381>,1:69]], NAME=[[@15,90:93='NAME',<225>,1:90]], GROUP_ID=[[@19,108:115='GROUP_ID',<381>,1:108]]}}",
		  extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		 "{def_query0={query_dictionary={ACTION=[[@7,46:51='ACTION',<381>,1:46]], RECORD_TYPE=[[@3,23:33='RECORD_TYPE',<381>,1:23]], EXTERNAL_ID=[[@11,69:79='EXTERNAL_ID',<381>,1:69]], NAME=[[@15,90:93='NAME',<225>,1:90]], GROUP_ID=[[@19,108:115='GROUP_ID',<381>,1:108]]}, table_dictionary={categorytbl={group_id=[[@17,96:103='group_id',<381>,1:96], [@53,251:258='group_id',<381>,1:251], [@62,288:295='group_id',<381>,1:288]], name=[[@13,82:85='name',<225>,1:82], [@38,203:206='name',<225>,1:203], [@47,236:239='name',<225>,1:236]], action=[[@5,36:41='action',<381>,1:36]], external_id=[[@9,54:64='external_id',<381>,1:54], [@23,141:151='external_id',<381>,1:141], [@32,181:191='external_id',<381>,1:181]], record_type=[[@1,8:18='record_type',<381>,1:8]]}}, filters=[{name=external_id, table_ref=categorytbl}, {name=name, table_ref=categorytbl}, {name=group_id, table_ref=categorytbl}], interface={ACTION=[{name=action, table_ref=categorytbl}], RECORD_TYPE=[{name=record_type, table_ref=categorytbl}], EXTERNAL_ID=[{name=external_id, table_ref=categorytbl}], NAME=[{name=name, table_ref=categorytbl}], GROUP_ID=[{name=group_id, table_ref=categorytbl}]}}}",
		  extractor.getSymbolTable().toString());
	}


	@Test
	public void getCategorizeSqlTest() {
		/*
		 * Categorize COLUMNS: RECORD_TYPE, ACTION, CATEGORY_ID, PRIMARY_ID
		 */
		String sql = " select record_type as RECORD_TYPE, action as ACTION, "
				+ "category_id as CATEGORy_id, primary_id as PRIMARY_ID from  categorizeTbl "
				+ " where (category_id is not null and length(trim(category_id)) > 0 "
				+ "and primary_id is not null and length(trim(primary_id)) > 0) ";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=record_type, table_ref=null}, alias=RECORD_TYPE}, 2={column={name=action, table_ref=null}, alias=ACTION}, 3={column={name=category_id, table_ref=null}, alias=CATEGORy_id}, 4={column={name=primary_id, table_ref=null}, alias=PRIMARY_ID}}, from={table={alias=null, table=categorizeTbl}}, where={parentheses={and={1={condition={left={column={name=category_id, table_ref=null}}, operator=is not null}}, 2={condition={left={function={parameters={1={function={parameters={1={column={name=category_id, table_ref=null}}}, function_name=trim}}}, function_name=length}}, right={literal=0}, operator=>}}, 3={condition={left={column={name=primary_id, table_ref=null}}, operator=is not null}}, 4={condition={left={function={parameters={1={function={parameters={1={column={name=primary_id, table_ref=null}}}, function_name=trim}}}, function_name=length}}, right={literal=0}, operator=>}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ACTION, PRIMARY_ID, RECORD_TYPE, CATEGORy_id]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{categorizetbl={category_id=[[@9,54:64='category_id',<381>,1:54], [@20,135:145='category_id',<381>,1:135], [@29,175:185='category_id',<381>,1:175]], action=[[@5,36:41='action',<381>,1:36]], primary_id=[[@13,82:91='primary_id',<381>,1:82], [@35,197:206='primary_id',<381>,1:197], [@44,236:245='primary_id',<381>,1:236]], record_type=[[@1,8:18='record_type',<381>,1:8]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={ACTION=[[@7,46:51='ACTION',<381>,1:46]], PRIMARY_ID=[[@15,96:105='PRIMARY_ID',<381>,1:96]], RECORD_TYPE=[[@3,23:33='RECORD_TYPE',<381>,1:23]], CATEGORy_id=[[@11,69:79='CATEGORy_id',<381>,1:69]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={ACTION=[[@7,46:51='ACTION',<381>,1:46]], PRIMARY_ID=[[@15,96:105='PRIMARY_ID',<381>,1:96]], RECORD_TYPE=[[@3,23:33='RECORD_TYPE',<381>,1:23]], CATEGORy_id=[[@11,69:79='CATEGORy_id',<381>,1:69]]}, table_dictionary={categorizetbl={category_id=[[@9,54:64='category_id',<381>,1:54], [@20,135:145='category_id',<381>,1:135], [@29,175:185='category_id',<381>,1:175]], action=[[@5,36:41='action',<381>,1:36]], primary_id=[[@13,82:91='primary_id',<381>,1:82], [@35,197:206='primary_id',<381>,1:197], [@44,236:245='primary_id',<381>,1:236]], record_type=[[@1,8:18='record_type',<381>,1:8]]}}, filters=[{name=category_id, table_ref=categorizetbl}, {name=primary_id, table_ref=categorizetbl}], interface={ACTION=[{name=action, table_ref=categorizetbl}], PRIMARY_ID=[{name=primary_id, table_ref=categorizetbl}], RECORD_TYPE=[{name=record_type, table_ref=categorizetbl}], CATEGORy_id=[{name=category_id, table_ref=categorizetbl}]}}}", extractor.getSymbolTable().toString());
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
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=record_type, table_ref=null}, alias=RECORD_TYPE}, 2={column={name=action, table_ref=null}, alias=ACTION}, 3={column={name=name, table_ref=null}, alias=NAME}, 4={column={name=parent_primary_user_id, table_ref=null}, alias=PARENT_PRIMARY_USER_ID}, 5={column={name=child_primary_user_id, table_ref=null}, alias=CHILD_PRIMARY_USER_ID}, 6={column={name=group_id, table_ref=null}, alias=GROUP_ID}}, from={table={alias=null, table=relationshipTbl}}, where={and={1={in={item={function={parameters={1={function={parameters={1={column={name=name, table_ref=null}}}, function_name=trim}}}, function_name=lower}}, in_list={list={1={literal='advisor'}, 2={literal='coach'}, 3={literal='professor'}, 4={literal='tutor'}}}}}, 2={condition={left={column={name=parent_primary_user_id, table_ref=null}}, operator=is not null}}, 3={condition={left={function={parameters={1={function={parameters={1={column={name=parent_primary_user_id, table_ref=null}}}, function_name=trim}}}, function_name=length}}, right={literal=0}, operator=>}}, 4={condition={left={column={name=child_primary_user_id, table_ref=null}}, operator=is not null}}, 5={condition={left={function={parameters={1={function={parameters={1={column={name=child_primary_user_id, table_ref=null}}}, function_name=trim}}}, function_name=length}}, right={literal=0}, operator=>}}, 6={condition={left={column={name=group_id, table_ref=null}}, operator=is not null}}, 7={condition={left={function={parameters={1={function={parameters={1={column={name=group_id, table_ref=null}}}, function_name=trim}}}, function_name=length}}, right={literal=0}, operator=>}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ACTION, RECORD_TYPE, PARENT_PRIMARY_USER_ID, CHILD_PRIMARY_USER_ID, NAME, GROUP_ID]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{relationshiptbl={parent_primary_user_id=[[@13,67:88='parent_primary_user_id',<381>,1:67], [@45,279:300='parent_primary_user_id',<381>,1:279], [@54,330:351='parent_primary_user_id',<381>,1:330]], group_id=[[@21,164:171='group_id',<381>,1:164], [@75,445:452='group_id',<381>,1:445], [@84,482:489='group_id',<381>,1:482]], name=[[@9,53:56='name',<225>,1:53], [@31,224:227='name',<225>,1:224]], action=[[@5,35:40='action',<381>,1:35]], child_primary_user_id=[[@17,116:136='child_primary_user_id',<381>,1:116], [@60,363:383='child_primary_user_id',<381>,1:363], [@69,413:433='child_primary_user_id',<381>,1:413]], record_type=[[@1,7:17='record_type',<381>,1:7]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={ACTION=[[@7,45:50='ACTION',<381>,1:45]], RECORD_TYPE=[[@3,22:32='RECORD_TYPE',<381>,1:22]], PARENT_PRIMARY_USER_ID=[[@15,93:114='PARENT_PRIMARY_USER_ID',<381>,1:93]], CHILD_PRIMARY_USER_ID=[[@19,141:161='CHILD_PRIMARY_USER_ID',<381>,1:141]], NAME=[[@11,61:64='NAME',<225>,1:61]], GROUP_ID=[[@23,176:183='GROUP_ID',<381>,1:176]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={ACTION=[[@7,45:50='ACTION',<381>,1:45]], RECORD_TYPE=[[@3,22:32='RECORD_TYPE',<381>,1:22]], PARENT_PRIMARY_USER_ID=[[@15,93:114='PARENT_PRIMARY_USER_ID',<381>,1:93]], CHILD_PRIMARY_USER_ID=[[@19,141:161='CHILD_PRIMARY_USER_ID',<381>,1:141]], NAME=[[@11,61:64='NAME',<225>,1:61]], GROUP_ID=[[@23,176:183='GROUP_ID',<381>,1:176]]}, table_dictionary={relationshiptbl={parent_primary_user_id=[[@13,67:88='parent_primary_user_id',<381>,1:67], [@45,279:300='parent_primary_user_id',<381>,1:279], [@54,330:351='parent_primary_user_id',<381>,1:330]], group_id=[[@21,164:171='group_id',<381>,1:164], [@75,445:452='group_id',<381>,1:445], [@84,482:489='group_id',<381>,1:482]], name=[[@9,53:56='name',<225>,1:53], [@31,224:227='name',<225>,1:224]], action=[[@5,35:40='action',<381>,1:35]], child_primary_user_id=[[@17,116:136='child_primary_user_id',<381>,1:116], [@60,363:383='child_primary_user_id',<381>,1:363], [@69,413:433='child_primary_user_id',<381>,1:413]], record_type=[[@1,7:17='record_type',<381>,1:7]]}}, filters=[{name=name, table_ref=relationshiptbl}, {name=parent_primary_user_id, table_ref=relationshiptbl}, {name=child_primary_user_id, table_ref=relationshiptbl}, {name=group_id, table_ref=relationshiptbl}], interface={ACTION=[{name=action, table_ref=relationshiptbl}], RECORD_TYPE=[{name=record_type, table_ref=relationshiptbl}], PARENT_PRIMARY_USER_ID=[{name=parent_primary_user_id, table_ref=relationshiptbl}], CHILD_PRIMARY_USER_ID=[{name=child_primary_user_id, table_ref=relationshiptbl}], NAME=[{name=name, table_ref=relationshiptbl}], GROUP_ID=[{name=group_id, table_ref=relationshiptbl}]}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void getSectionMeetingSqlTest() {

		String sql = "select sec.record_type as RECORD_TYPE,sec.action as ACTION, sec.term_code as TERM_ID,concat_ws('-',s.subject_code,s.course_number) as COURSE_EXTERNAL_ID, "
				+ "\n case when s.section_name is null or length(trim(s.section_name))=0 then '' else s.section_name end as SECTION_NAME, "
				+ "\n case when sec.meet_start_date is not null then datestr(sec.meet_start_date, 'SECTION_SOURCE_DATE_FORMAT', "
				+ "\n 'SSCPLUS_DEFAULT_DATE_FORMAT')  else '' end as BEGIN_DATE, "
				+ "\n case when sec.meet_end_date is not null then datestr(sec.meet_end_date, 'SECTION_SOURCE_DATE_FORMAT',"
				+ "\n 'SSCPLUS_DEFAULT_DATE_FORMAT') else ''  end as END_DATE, "
				+ "\n case when sec.meet_start_time is null or length(trim(sec.meet_start_time))=0 or length(trim(sec.meet_start_time)) < 4 then '' else concat_ws(':',substr(sec.meet_start_time,1,2),substr(sec.meet_start_time,3,2)) end as START_TIME, "
				+ "\n case when sec.meet_end_time is null or length(trim(sec.meet_end_time))=0 or length(trim(sec.meet_end_time)) < 4 then '' else concat_ws(':',substr(sec.meet_end_time,1,2),substr(sec.meet_end_time,3,2)) end as END_TIME, "
				+ "\n coalesce(concat(sec.meet_sunday,meet_monday,meet_tuesday,meet_wednesday,meet_thursday,meet_friday,meet_saturday),'') as MEETING_DAYS, "
				+ "\n case when (meet_building_code is null or length(trim(meet_building_code))=0) and (meet_room_code is null or length(trim(meet_room_code))=0) then '' "
				+ "\n when (meet_building_code is null or length(trim(meet_building_code))=0) or (meet_room_code is null or length(trim(meet_room_code))=0) then concat(trim(meet_building_code),trim(meet_room_code)) "
				+ "\n else concat_ws('-',meet_building_code,meet_room_code) end as LOCATION "
				+ "\n from sectionMeetTbl sec "
				+ "\n inner join termFilterTbl tf on "
				+ "\n sec.term_code = tf.term_id " 
				+ "\n inner join sectionTbl "
				+ "\n s on (sec.course_ref_no=s.course_ref_no and sec.term_code=s.term_code)";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		Snippet snippet = extractor.getSnippet();
		assertDiagnosticCountBySeverity(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				null,
				1);
		assertDiagnosticCountBySeverity(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				null,
				null,
				8);
	}


	@Test
	public void createTermFilterTableTest() {
		String sql = "select external_id as term_id from termStgTableName  a," 
				+ "\n (select min(term_rank) as term_rank "
				+ "from (" 
				+ "\n select curr_term_rank as term_rank from currentTermTableName union all "
				+ "\n select max(t1.term_rank) term_rank from termStgTableName  t1, "
				+ " currentTermTableName t2 where t1.term_rank < t2.curr_term_rank" + ") tbl" + ") b "
				+ "where a.term_rank >= b.term_rank";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
		 "{SQL={select={1={column={name=external_id, table_ref=null}, alias=term_id}}, from={join={1={table={alias=a, table=termStgTableName}}, 2={table={alias=b, query={select={1={function={function_name=min, qualifier=null, parameters={column={name=term_rank, table_ref=null}}}, alias=term_rank}}, from={table={alias=tbl, query={union={1={select={1={column={name=curr_term_rank, table_ref=null}, alias=term_rank}}, from={table={alias=null, table=currentTermTableName}}}, 2={union={qualifier=all, operator=union}}, 3={select={1={function={function_name=max, qualifier=null, parameters={column={name=term_rank, table_ref=t1}}}, alias=term_rank}}, from={join={1={table={alias=t1, table=termStgTableName}}, 2={table={alias=t2, table=currentTermTableName}}}}, where={condition={left={column={name=term_rank, table_ref=t1}}, right={column={name=curr_term_rank, table_ref=t2}}, operator=<}}}}}}}}}}}}, where={condition={left={column={name=term_rank, table_ref=a}}, right={column={name=term_rank, table_ref=b}}, operator=>=}}}}",
		  extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[term_id]",
		 extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
		 extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
		 "{termstgtablename={term_rank=[[@29,185:186='t1',<381>,4:12], [@41,267:268='t1',<381>,4:94], [@53,314:314='a',<381>,4:141]], external_id=[[@1,7:17='external_id',<381>,1:7]]}, currenttermtablename={curr_term_rank=[[@19,108:121='curr_term_rank',<381>,3:8], [@45,282:283='t2',<381>,4:109]]}}",
		  extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{union2={term_rank=[[@12,69:77='term_rank',<381>,2:13]]}, query4={term_id=[[@3,22:28='term_id',<381>,1:22]]}, query0={term_rank=[[@21,126:134='term_rank',<381>,3:26]]}, query1={term_rank=[[@33,199:207='term_rank',<381>,4:26]]}, query3={term_rank=[[@15,83:91='term_rank',<381>,2:27], [@57,329:329='b',<381>,4:156]]}}",
		  extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		 "{def_query4={query_dictionary={term_id=[[@3,22:28='term_id',<381>,1:22]]}, table_dictionary={termstgtablename={term_rank=[[@53,314:314='a',<381>,4:141]], external_id=[[@1,7:17='external_id',<381>,1:7]]}}, filters=[{name=term_rank, table_ref=a}, {name=term_rank, table_ref=b}], interface={term_id=[{name=external_id, table_ref=termstgtablename}]}, def_query3={def_union2={query_dictionary={term_rank=[[@12,69:77='term_rank',<381>,2:13]]}, def_query1={query_dictionary={term_rank=[[@33,199:207='term_rank',<381>,4:26]]}, table_dictionary={termstgtablename={term_rank=[[@29,185:186='t1',<381>,4:12], [@41,267:268='t1',<381>,4:94], [@53,314:314='a',<381>,4:141]]}, currenttermtablename={curr_term_rank=[[@45,282:283='t2',<381>,4:109]]}}, filters=[{name=term_rank, table_ref=t1}, {name=curr_term_rank, table_ref=t2}], interface={term_rank=[{name=term_rank, table_ref=t1}]}, table_alias={t1=termStgTableName, t2=currentTermTableName}}, def_query0={query_dictionary={term_rank=[[@21,126:134='term_rank',<381>,3:26]]}, table_dictionary={currenttermtablename={curr_term_rank=[[@19,108:121='curr_term_rank',<381>,3:8], [@45,282:283='t2',<381>,4:109]]}}, interface={term_rank=[{name=curr_term_rank, table_ref=currenttermtablename}]}}, interface={term_rank=query_column}}, query_dictionary={term_rank=[[@15,83:91='term_rank',<381>,2:27], [@57,329:329='b',<381>,4:156]]}, interface={term_rank=[{name=term_rank, table_ref=union2}]}, table_alias={tbl=union2}}, table_alias={a=termStgTableName, b=query3}}}",
		  extractor.getSymbolTable().toString());
	}


	@Test
	public void createCurrentTermTableTest() {
		String sql = "select min(term_rank) curr_term_rank from ( " 
				+ "select term_rank from "
				+ " termStgTableName  where unix_timestamp() between term_start and term_end " + "union all "
				+ "select max(term_rank) term_rank from termStgTableName " + " where unix_timestamp() >= term_start "
				+ ") tbl";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
		 "{SQL={select={1={function={function_name=min, qualifier=null, parameters={column={name=term_rank, table_ref=null}}}, alias=curr_term_rank}}, from={table={alias=tbl, query={union={1={select={1={column={name=term_rank, table_ref=null}}}, from={table={alias=null, table=termStgTableName}}, where={between={item={1={function_name=unix_timestamp}}, symmetry=null, end={column={name=term_end, table_ref=null}}, begin={column={name=term_start, table_ref=null}}, operator=between}}}, 2={union={qualifier=all, operator=union}}, 3={select={1={function={function_name=max, qualifier=null, parameters={column={name=term_rank, table_ref=null}}}, alias=term_rank}}, from={table={alias=null, table=termStgTableName}}, where={condition={left={1={function_name=unix_timestamp}}, right={column={name=term_start, table_ref=null}}, operator=>=}}}}}}}}}",
		  extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[curr_term_rank]",
		 extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
		 extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
		 "{termstgtablename={term_rank=[[@9,51:59='term_rank',<381>,1:51], [@25,161:169='term_rank',<381>,1:161]], term_start=[[@17,116:125='term_start',<381>,1:116], [@35,231:240='term_start',<381>,1:231]], term_end=[[@19,131:138='term_end',<381>,1:131]]}}",
		  extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{union2={term_rank=[[@3,11:19='term_rank',<381>,1:11]]}, query0={term_rank=[[@9,51:59='term_rank',<381>,1:51]]}, query1={term_rank=[[@27,172:180='term_rank',<381>,1:172]]}, query3={curr_term_rank=[[@5,22:35='curr_term_rank',<381>,1:22]]}}",
		  extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		 "{def_query3={def_union2={query_dictionary={term_rank=[[@3,11:19='term_rank',<381>,1:11]]}, def_query1={query_dictionary={term_rank=[[@27,172:180='term_rank',<381>,1:172]]}, table_dictionary={termstgtablename={term_rank=[[@25,161:169='term_rank',<381>,1:161]], term_start=[[@35,231:240='term_start',<381>,1:231]]}}, filters=[{name=term_start, table_ref=termstgtablename}], interface={term_rank=[{name=term_rank, table_ref=termstgtablename}]}}, def_query0={query_dictionary={term_rank=[[@9,51:59='term_rank',<381>,1:51]]}, table_dictionary={termstgtablename={term_rank=[[@9,51:59='term_rank',<381>,1:51], [@25,161:169='term_rank',<381>,1:161]], term_start=[[@17,116:125='term_start',<381>,1:116], [@35,231:240='term_start',<381>,1:231]], term_end=[[@19,131:138='term_end',<381>,1:131]]}}, filters=[{name=term_end, table_ref=termstgtablename}, {name=term_start, table_ref=termstgtablename}], interface={term_rank=[{name=term_rank, table_ref=termstgtablename}]}}, interface={term_rank=query_column}}, query_dictionary={curr_term_rank=[[@5,22:35='curr_term_rank',<381>,1:22]]}, interface={curr_term_rank=[{name=term_rank, table_ref=union2}]}, table_alias={tbl=union2}}}",
		  extractor.getSymbolTable().toString());
	}


	@Test
	public void createTermStgTableTest() {
		String sql = "select external_id, " + "rank() over (order by begin_date, end_date) term_rank, "
				+ "unix_timestamp(begin_date,'yyyyMMdd') term_start, "
				+ "unix_timestamp(end_date,'yyyyMMdd') term_end from  hiveTableName ";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
		 "{SQL={select={1={column={name=external_id, table_ref=null}}, 2={alias=term_rank, window_function={over={orderby={1={null_order=null, predicand={column={name=begin_date, table_ref=null}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=end_date, table_ref=null}}, sort_order=ASC}}}, function={function_name=rank, parameters=null}}}, 3={function={parameters={1={column={name=begin_date, table_ref=null}}, 2={literal='yyyyMMdd'}}, function_name=unix_timestamp}, alias=term_start}, 4={function={parameters={1={column={name=end_date, table_ref=null}}, 2={literal='yyyyMMdd'}}, function_name=unix_timestamp}, alias=term_end}}, from={table={alias=null, table=hiveTableName}}}}",
		  extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[term_rank, term_start, external_id, term_end]",
		 extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
		 extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
		 "{hivetablename={end_date=[[@12,54:61='end_date',<381>,1:54], [@26,140:147='end_date',<381>,1:140]], begin_date=[[@10,42:51='begin_date',<381>,1:42], [@18,90:99='begin_date',<381>,1:90]], external_id=[[@1,7:17='external_id',<381>,1:7]]}}",
		  extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={term_rank=[[@14,64:72='term_rank',<381>,1:64]], external_id=[[@1,7:17='external_id',<381>,1:7]], term_end=[[@30,161:168='term_end',<381>,1:161]], term_start=[[@22,113:122='term_start',<381>,1:113]]}}",
		  extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		 "{def_query0={query_dictionary={term_rank=[[@14,64:72='term_rank',<381>,1:64]], term_start=[[@22,113:122='term_start',<381>,1:113]], external_id=[[@1,7:17='external_id',<381>,1:7]], term_end=[[@30,161:168='term_end',<381>,1:161]]}, table_dictionary={hivetablename={end_date=[[@12,54:61='end_date',<381>,1:54], [@26,140:147='end_date',<381>,1:140]], begin_date=[[@10,42:51='begin_date',<381>,1:42], [@18,90:99='begin_date',<381>,1:90]], external_id=[[@1,7:17='external_id',<381>,1:7]]}}, interface={term_rank=[{name=begin_date, table_ref=hivetablename}, {name=end_date, table_ref=hivetablename}], term_start=[{name=begin_date, table_ref=hivetablename}], external_id=[{name=external_id, table_ref=hivetablename}], term_end=[{name=end_date, table_ref=hivetablename}]}}}",
		  extractor.getSymbolTable().toString());
	}


	@Test
	public void selectWorkbooksDownfillWithTest() {
		String sql = "with downfill as (  select   student_id  , student_id, term_id  , major_cd_fill "
				+ " , college_cd_fill  , degree_cd_fill  , concentration_cd_fill  , major_cd_2_fill "
				+ " , college_cd_2_fill  , degree_cd_2_fill  , concentration_cd_2_fill " 
				+ " from ( "
				+ "\n SELECT  student_id  , major_cd  , term_id  , value_partition "
				+ " , first_value(major_cd) over (partition by student_id, value_partition order by term_row) as major_cd_fill "
				+ " , first_value(college_cd) over (partition by student_id, value_partition order by term_row) as college_cd_fill "
				+ " , first_value(degree_cd) over (partition by student_id, value_partition order by term_row) as degree_cd_fill "
				+ " , first_value(concentration_cd) over (partition by student_id, value_partition order by term_row) as concentration_cd_fill "
				+ " , first_value(major_cd_2) over (partition by student_id, value_partition order by term_row) as major_cd_2_fill "
				+ " , first_value(college_cd_2) over (partition by student_id, value_partition order by term_row) as college_cd_2_fill "
				+ " , first_value(degree_cd_2) over (partition by student_id, value_partition order by term_row) as degree_cd_2_fill "
				+ " , first_value(concentration_cd_2) over (partition by student_id, value_partition order by term_row) as concentration_cd_2_fill "
				+ " FROM ( " 
				+ "\n SELECT  student_id  , major_cd  , smt.term_id  , college_cd "
				+ " , degree_cd  , concentration_cd  , major_cd_2  , college_cd_2  , degree_cd_2 "
				+ " , concentration_cd_2  , sum(case when major_cd is null then 0 else 1 end) "
				+ " over (partition by student_id order by term_row) as value_partition  , term_row "
				+ " 	  FROM student_major_term smt "
				+ "\n left join (select row_number() over(order by start_date asc) as term_row, term_id from standard_term) terms "
				+ " on terms.term_id = smt.term_id  ORDER BY 1,12 DESC  	  ) sub1  order by 1,3 desc "
				+ " ) sub2  where sub2.major_cd is null  ) " 
				+ "\n update student_major_term smt set "   // query0
				+ " major_cd = downfill.major_cd_fill  , college_cd = downfill.college_cd_fill "
				+ " , degree_cd = downfill.degree_cd_fill  , concentration_cd = downfill.concentration_cd_fill "
				+ " , major_cd_2 = downfill.major_cd_2_fill  , college_cd_2 = downfill.college_cd_2_fill "
				+ " , degree_cd_2 = downfill.degree_cd_2_fill "
				+ " 	, concentration_cd_2 = downfill.concentration_cd_2_fill  from downfill "
				+ " where smt.student_id = downfill.student_id  and smt.term_id = downfill.term_id ";

		final SQLSelectParserParser parser = parse(sql);
			
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"DUPLICATE_INTERFACE_COLUMNS",
				"Duplicate interface columns defined: student_id at (l:1 c:29) and student_id at (l:1 c:43).",
				"student_id",
				1);
		Assert.assertNotNull("AST should not be null", extractor.getAsTree());
		Assert.assertNotNull("Interface should not be null", extractor.getInterface());
		Assert.assertNotNull("Substitution map should not be null", extractor.getSubstitutionsMap());
		Assert.assertNotNull("Table dictionary should not be null", extractor.getTableColumnDictionaryMap());
		Assert.assertNotNull("Query column dictionary should not be null", extractor.getQueryColumnDictionaryMap());
		Assert.assertNotNull("Symbol table should not be null", extractor.getSymbolTable());
	}


	@Test
	public void selectEmbeddedSubqueryTest() {
		String sql =  " SELECT  smt.term_id, terms.term_id, term_row "
				+ " FROM student_major_term smt "
				+ " \n left join (select row_number() over(order by start_date asc) as term_row, term_id \n from standard_term) terms "
				+ " on terms.term_id = smt.term_id order by 1, 2 desc";

		final SQLSelectParserParser parser = parse(sql);
			
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"DUPLICATE_INTERFACE_COLUMNS",
				"Duplicate interface columns defined: smt.term_id at (l:1 c:13) and terms.term_id at (l:1 c:28).",
				"term_id",
				1);
	
		Assert.assertEquals("AST is wrong",
		 "{SQL={select={1={column={name=term_id, table_ref=smt}}, 2={column={name=term_id, table_ref=terms}}, 3={column={name=term_row, table_ref=null}}}, orderby={1={null_order=null, predicand={literal=1}, sort_order=ASC}, 2={null_order=null, predicand={literal=2}, sort_order=desc}}, from={join={1={table={alias=smt, table=student_major_term}}, 2={join=left, on={condition={left={column={name=term_id, table_ref=terms}}, right={column={name=term_id, table_ref=smt}}, operator==}}}, 3={table={alias=terms, query={select={1={alias=term_row, window_function={over={orderby={1={null_order=null, predicand={column={name=start_date, table_ref=null}}, sort_order=asc}}}, function={function_name=row_number, parameters=null}}}, 2={column={name=term_id, table_ref=null}}}, from={table={alias=null, table=standard_term}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[term_row, term_id]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
		 "{standard_term={term_id=[[@30,152:158='term_id',<381>,2:75]], start_date=[[@24,123:132='start_date',<381>,2:46]]}, student_major_term={term_row=[[@9,37:44='term_row',<381>,1:37]], term_id=[[@1,9:11='smt',<381>,1:9], [@40,208:210='smt',<381>,3:47]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={term_row=[[@28,142:149='term_row',<381>,2:65]], term_id=[[@30,152:158='term_id',<381>,2:75], [@5,22:26='terms',<381>,1:22], [@36,192:196='terms',<381>,3:31]]}, query1={term_row=[[@9,37:44='term_row',<381>,1:37]], term_id=[[@3,13:19='term_id',<381>,1:13], [@7,28:34='term_id',<381>,1:28]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		 "{def_query1={query_dictionary={term_row=[[@9,37:44='term_row',<381>,1:37]], term_id=[[@3,13:19='term_id',<381>,1:13], [@7,28:34='term_id',<381>,1:28]]}, table_dictionary={student_major_term={term_row=[[@9,37:44='term_row',<381>,1:37]], term_id=[[@1,9:11='smt',<381>,1:9], [@40,208:210='smt',<381>,3:47]]}}, def_query0={query_dictionary={term_row=[[@28,142:149='term_row',<381>,2:65]], term_id=[[@30,152:158='term_id',<381>,2:75], [@5,22:26='terms',<381>,1:22], [@36,192:196='terms',<381>,3:31]]}, table_dictionary={standard_term={term_id=[[@30,152:158='term_id',<381>,2:75]], start_date=[[@24,123:132='start_date',<381>,2:46]]}}, interface={term_row=[{name=start_date, table_ref=standard_term}], term_id=[{name=term_id, table_ref=standard_term}]}}, ordered_by=[], filters=[{name=term_id, table_ref=terms}, {name=term_id, table_ref=smt}], interface={term_row=[{name=term_row, table_ref=null}], term_id=[{name=term_id, table_ref=terms}]}, table_alias={terms=query0, smt=student_major_term}}}",
				extractor.getSymbolTable().toString());
	}



	@Test
	public void complexJINJAQueryWithWoldcardTest() {
		// From PDP DBT: 
		String sql = " with recent_sourcecontacts as\n"
				+ "(\n"
				+ "select sourcecontact_id, contact_key from\n"
				+ "(select sc_current.sourcecontact_id, sc_current.contact_key,\n"
				+ "row_number() over (partition by sc_current.contact_key order by sc_current.contact_priority asc,\n"
				+ "                    sc_current.first_sourced_dt) as rank_rc\n"
				+ "from {{ ref ( 'prc__contacts_by_sourcecontacts_current') }} as sc_current) rc\n"
				+ "where rc.rank_rc =1),\n"
				+ "cte_recent_gift_data as (select pcdx.contact_key, gifts_data.*, row_number() over\n"
				+ "    (partition by rc.contact_key,gifts_data.source_partnercontact_id\n"
				+ "                      order by gifts_data.gift_dt desc,\n"
				+ "                      CASE\n"
				+ "                       WHEN fund_desc = 'Unallocated Fund' THEN 1 ELSE 0\n"
				+ "                      END,\n"
				+ "                      gifts_data.fund_amount desc, gifts_data.fund_desc desc) as rn\n"
				+ "                      from recent_sourcecontacts rc\n"
				+ "                      inner join {{ ref ( 'prc_contact_donor_xwalk') }} pcdx\n"
				+ "                      on rc.contact_key = pcdx.contact_key\n"
				+ "                      inner join (select g.gift_id, g.source_partnercontact_id, gf.fund_amount, g.gift_dt, gf.fund_desc\n"
				+ "                                    from {{ source('PDP_AMS', 'gifts')}} g\n"
				+ "                                    inner join {{ source('PDP_AMS','gifts_funds')}} gf\n"
				+ "                                    on g.gift_id = gf.gift_id\n"
				+ "                                    where g.eab_marketing_inclusion_ind = 1 and g.gift_amount > 0\n"
				+ "                                    union\n"
				+ "                                    select ga.gift_id, ga.soft_credit_id as source_partnercontact_id, gf.fund_amount, ga.gift_dt, gf.fund_desc\n"
				+ "                                    from {{ source('PDP_AMS', 'gifts_allocation')}} ga\n"
				+ "                                    inner join {{ source('PDP_AMS', 'gifts')}} g\n"
				+ "                                    on ga.gift_id = g.gift_id\n"
				+ "                                    inner join {{ source('PDP_AMS','gifts_funds')}} gf\n"
				+ "                                    on ga.gift_id = gf.gift_id\n"
				+ "                                    where g.eab_marketing_inclusion_ind = 1 and ga.soft_credit_amount > 0) gifts_data\n"
				+ "                      on gifts_data.source_partnercontact_id = pcdx.source_partnercontact_id)\n"
				+ "select crgd.contact_key,\n"
				+ "       crgd.gift_id,\n"
				+ "       crgd.fund_desc as fund_name_mr_calc,\n"
				+ "       crgd.gift_dt as fund_date_mr_calc,\n"
				+ "       cast(ceil(crgd.fund_amount)as integer) as fund_amt_mr_calc\n"
				+ "from cte_recent_gift_data crgd\n"
				+ "where crgd.rn = 1 ";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoFatalErrors(extractor);

		Snippet snippet = extractor.getSnippet();
		// 'fund_desc' at line 15 char 81 (unqualified in ORDER BY) is ambiguous:
		// it appears in both {{ ref('prc_contact_donor_xwalk') }} and the gifts UNION subquery (union4)
		assertDiagnosticAtPosition(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'fund_desc'", "fund_desc", 15, 81);

		Assert.assertEquals("AST is wrong",
		 "{SQL={with={1={cte={select={1={column={name=sourcecontact_id, table_ref=null}}, 2={column={name=contact_key, table_ref=null}}}, from={table={alias=rc, query={select={1={column={name=sourcecontact_id, table_ref=sc_current}}, 2={column={name=contact_key, table_ref=sc_current}}, 3={alias=rank_rc, window_function={over={partition_by={1={column={name=contact_key, table_ref=sc_current}}}, orderby={1={null_order=null, predicand={column={name=contact_priority, table_ref=sc_current}}, sort_order=asc}, 2={null_order=null, predicand={column={name=first_sourced_dt, table_ref=sc_current}}, sort_order=ASC}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=sc_current, substitution={name={{ ref ( 'prc__contacts_by_sourcecontacts_current') }}, parts={jinja_table={function_name=ref, parameters={1={literal='prc__contacts_by_sourcecontacts_current'}}}}, type=tuple}}}}}}, where={condition={left={column={name=rank_rc, table_ref=rc}}, right={literal=1}, operator==}}}, alias=recent_sourcecontacts}, 2={cte={select={1={column={name=contact_key, table_ref=pcdx}}, 2={column={name=*, table_ref=gifts_data}}, 3={alias=rn, window_function={over={partition_by={1={column={name=contact_key, table_ref=rc}}, 2={column={name=source_partnercontact_id, table_ref=gifts_data}}}, orderby={1={null_order=null, predicand={column={name=gift_dt, table_ref=gifts_data}}, sort_order=desc}, 2={null_order=null, predicand={case={clauses={1={then={literal=1}, when={condition={left={column={name=fund_desc, table_ref=null}}, right={literal='Unallocated Fund'}, operator==}}}}, else={literal=0}}}, sort_order=ASC}, 3={null_order=null, predicand={column={name=fund_amount, table_ref=gifts_data}}, sort_order=desc}, 4={null_order=null, predicand={column={name=fund_desc, table_ref=gifts_data}}, sort_order=desc}}}, function={function_name=row_number, parameters=null}}}}, from={join={1={table={alias=rc, table=recent_sourcecontacts}}, 2={join=inner, on={condition={left={column={name=contact_key, table_ref=rc}}, right={column={name=contact_key, table_ref=pcdx}}, operator==}}}, 3={table={alias=pcdx, substitution={name={{ ref ( 'prc_contact_donor_xwalk') }}, parts={jinja_table={function_name=ref, parameters={1={literal='prc_contact_donor_xwalk'}}}}, type=tuple}}}, 4={join=inner, on={condition={left={column={name=source_partnercontact_id, table_ref=gifts_data}}, right={column={name=source_partnercontact_id, table_ref=pcdx}}, operator==}}}, 5={table={alias=gifts_data, query={union={1={select={1={column={name=gift_id, table_ref=g}}, 2={column={name=source_partnercontact_id, table_ref=g}}, 3={column={name=fund_amount, table_ref=gf}}, 4={column={name=gift_dt, table_ref=g}}, 5={column={name=fund_desc, table_ref=gf}}}, from={join={1={table={alias=g, substitution={name={{ source('PDP_AMS', 'gifts')}}, parts={jinja_table={function_name=source, parameters={1={literal='PDP_AMS'}, 2={literal='gifts'}}}}, type=tuple}}}, 2={join=inner, on={condition={left={column={name=gift_id, table_ref=g}}, right={column={name=gift_id, table_ref=gf}}, operator==}}}, 3={table={alias=gf, substitution={name={{ source('PDP_AMS','gifts_funds')}}, parts={jinja_table={function_name=source, parameters={1={literal='PDP_AMS'}, 2={literal='gifts_funds'}}}}, type=tuple}}}}}, where={and={1={condition={left={column={name=eab_marketing_inclusion_ind, table_ref=g}}, right={literal=1}, operator==}}, 2={condition={left={column={name=gift_amount, table_ref=g}}, right={literal=0}, operator=>}}}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=gift_id, table_ref=ga}}, 2={column={name=soft_credit_id, table_ref=ga}, alias=source_partnercontact_id}, 3={column={name=fund_amount, table_ref=gf}}, 4={column={name=gift_dt, table_ref=ga}}, 5={column={name=fund_desc, table_ref=gf}}}, from={join={1={table={alias=ga, substitution={name={{ source('PDP_AMS', 'gifts_allocation')}}, parts={jinja_table={function_name=source, parameters={1={literal='PDP_AMS'}, 2={literal='gifts_allocation'}}}}, type=tuple}}}, 2={join=inner, on={condition={left={column={name=gift_id, table_ref=ga}}, right={column={name=gift_id, table_ref=g}}, operator==}}}, 3={table={alias=g, substitution={name={{ source('PDP_AMS', 'gifts')}}, parts={jinja_table={function_name=source, parameters={1={literal='PDP_AMS'}, 2={literal='gifts'}}}}, type=tuple}}}, 4={join=inner, on={condition={left={column={name=gift_id, table_ref=ga}}, right={column={name=gift_id, table_ref=gf}}, operator==}}}, 5={table={alias=gf, substitution={name={{ source('PDP_AMS','gifts_funds')}}, parts={jinja_table={function_name=source, parameters={1={literal='PDP_AMS'}, 2={literal='gifts_funds'}}}}, type=tuple}}}}}, where={and={1={condition={left={column={name=eab_marketing_inclusion_ind, table_ref=g}}, right={literal=1}, operator==}}, 2={condition={left={column={name=soft_credit_amount, table_ref=ga}}, right={literal=0}, operator=>}}}}}}}}}}}}, alias=cte_recent_gift_data}}, query={select={1={column={name=contact_key, table_ref=crgd}}, 2={column={name=gift_id, table_ref=crgd}}, 3={column={name=fund_desc, table_ref=crgd}, alias=fund_name_mr_calc}, 4={column={name=gift_dt, table_ref=crgd}, alias=fund_date_mr_calc}, 5={function={function_name=cast, data_type={type=INTEGER}, type=CAST, value={function={parameters={1={column={name=fund_amount, table_ref=crgd}}}, function_name=ceil}}}, alias=fund_amt_mr_calc}}, from={table={alias=crgd, table=cte_recent_gift_data}}, where={condition={left={column={name=rn, table_ref=crgd}}, right={literal=1}, operator==}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[contact_key, fund_date_mr_calc, gift_id, fund_name_mr_calc, fund_amt_mr_calc]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ source('PDP_AMS','gifts_funds')}}=tuple, {{ source('PDP_AMS', 'gifts')}}=tuple, {{ ref ( 'prc__contacts_by_sourcecontacts_current') }}=tuple, {{ source('PDP_AMS', 'gifts_allocation')}}=tuple, {{ ref ( 'prc_contact_donor_xwalk') }}=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
		 "{{{ ref ( 'prc__contacts_by_sourcecontacts_current') }}={contact_key=[[@15,112:121='sc_current',<381>,4:37], [@26,168:177='sc_current',<381>,5:32]], first_sourced_dt=[[@36,253:262='sc_current',<381>,6:20]], sourcecontact_id=[[@11,83:92='sc_current',<381>,4:8]], contact_priority=[[@31,200:209='sc_current',<381>,5:64]]}, {{ source('pdp_ams', 'gifts')}}={source_partnercontact_id=[[@145,1051:1051='g',<381>,19:52]], gift_id=[[@141,1040:1040='g',<381>,19:41], [@182,1320:1320='g',<381>,22:39], [@250,1846:1846='g',<381>,28:52]], gift_dt=[[@153,1095:1095='g',<381>,19:96]], gift_amount=[[@196,1423:1423='g',<381>,23:80]], eab_marketing_inclusion_ind=[[@190,1385:1385='g',<381>,23:42], [@273,2048:2048='g',<381>,31:42]]}, {{ source('pdp_ams', 'gifts_allocation')}}={soft_credit_id=[[@207,1538:1539='ga',<381>,25:55]], soft_credit_amount=[[@279,2086:2087='ga',<381>,31:80]], gift_id=[[@203,1526:1527='ga',<381>,25:43], [@246,1833:1834='ga',<381>,28:39], [@265,1982:1983='ga',<381>,30:39]], gift_dt=[[@217,1601:1602='ga',<381>,25:118]]}, {{ source('pdp_ams','gifts_funds')}}={fund_desc=[[@157,1106:1107='gf',<381>,19:107], [@221,1613:1614='gf',<381>,25:130]], fund_amount=[[@149,1079:1080='gf',<381>,19:80], [@213,1585:1586='gf',<381>,25:102]], gift_id=[[@186,1332:1333='gf',<381>,22:51], [@269,1995:1996='gf',<381>,30:52]]}, {{ ref ( 'prc_contact_donor_xwalk') }}={fund_desc=[[@96,655:663='fund_desc',<381>,13:28]], source_partnercontact_id=[[@291,2187:2190='pcdx',<381>,32:63]], contact_key=[[@65,425:428='pcdx',<381>,9:32], [@134,982:985='pcdx',<381>,18:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{union4={fund_desc=[[@110,778:787='gifts_data',<381>,15:51]], source_partnercontact_id=[[@84,508:517='gifts_data',<381>,10:33], [@287,2149:2158='gifts_data',<381>,32:25]], fund_amount=[[@105,749:758='gifts_data',<381>,15:22]], *=[[@69,443:452='gifts_data',<381>,9:50]], gift_dt=[[@89,575:584='gifts_data',<381>,11:31]]}, query5={fund_desc=[[@304,2271:2274='crgd',<381>,35:7]], contact_key=[[@67,430:440='contact_key',<381>,9:37], [@296,2225:2228='crgd',<381>,33:7]], fund_amount=[[@320,2367:2370='crgd',<381>,37:17]], gift_id=[[@300,2250:2253='crgd',<381>,34:7]], *=[[@71,454:454='*',<291>,9:61]], rn=[[@116,808:809='rn',<381>,15:81], [@333,2453:2456='crgd',<381>,39:6]], gift_dt=[[@310,2315:2318='crgd',<381>,36:7]]}, query6={gift_id=[[@302,2255:2261='gift_id',<381>,34:12]], fund_name_mr_calc=[[@308,2289:2305='fund_name_mr_calc',<381>,35:25]], fund_amt_mr_calc=[[@328,2399:2414='fund_amt_mr_calc',<381>,37:49]], contact_key=[[@298,2230:2240='contact_key',<381>,33:12]], fund_date_mr_calc=[[@314,2331:2347='fund_date_mr_calc',<381>,36:23]]}, query0={rank_rc=[[@41,285:291='rank_rc',<381>,6:52], [@54,377:378='rc',<381>,8:6]], contact_key=[[@17,123:133='contact_key',<381>,4:48], [@7,58:68='contact_key',<381>,3:25]], sourcecontact_id=[[@13,94:109='sourcecontact_id',<381>,4:19], [@5,40:55='sourcecontact_id',<381>,3:7]]}, query1={contact_key=[[@7,58:68='contact_key',<381>,3:25], [@80,493:494='rc',<381>,10:18], [@130,965:966='rc',<381>,18:25]], sourcecontact_id=[[@5,40:55='sourcecontact_id',<381>,3:7]]}, query2={gift_id=[[@143,1042:1048='gift_id',<381>,19:43]], fund_desc=[[@159,1109:1117='fund_desc',<381>,19:110]], source_partnercontact_id=[[@147,1053:1076='source_partnercontact_id',<381>,19:54]], fund_amount=[[@151,1082:1092='fund_amount',<381>,19:83]], gift_dt=[[@155,1097:1103='gift_dt',<381>,19:98]]}, query3={gift_id=[[@205,1529:1535='gift_id',<381>,25:46]], fund_desc=[[@223,1616:1624='fund_desc',<381>,25:133]], source_partnercontact_id=[[@211,1559:1582='source_partnercontact_id',<381>,25:76]], fund_amount=[[@215,1588:1598='fund_amount',<381>,25:105]], gift_dt=[[@219,1604:1610='gift_dt',<381>,25:121]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		 "{def_query6={context_list={recent_sourcecontacts=query1, cte_recent_gift_data=query5, crgd=query5}, query_dictionary={contact_key=[[@298,2230:2240='contact_key',<381>,33:12]], fund_date_mr_calc=[[@314,2331:2347='fund_date_mr_calc',<381>,36:23]], gift_id=[[@302,2255:2261='gift_id',<381>,34:12]], fund_name_mr_calc=[[@308,2289:2305='fund_name_mr_calc',<381>,35:25]], fund_amt_mr_calc=[[@328,2399:2414='fund_amt_mr_calc',<381>,37:49]]}, def_query1={query_dictionary={contact_key=[[@7,58:68='contact_key',<381>,3:25], [@80,493:494='rc',<381>,10:18], [@130,965:966='rc',<381>,18:25]], sourcecontact_id=[[@5,40:55='sourcecontact_id',<381>,3:7]]}, def_query0={query_dictionary={contact_key=[[@17,123:133='contact_key',<381>,4:48], [@7,58:68='contact_key',<381>,3:25]], sourcecontact_id=[[@13,94:109='sourcecontact_id',<381>,4:19], [@5,40:55='sourcecontact_id',<381>,3:7]], rank_rc=[[@41,285:291='rank_rc',<381>,6:52], [@54,377:378='rc',<381>,8:6]]}, table_dictionary={{{ ref ( 'prc__contacts_by_sourcecontacts_current') }}={contact_key=[[@15,112:121='sc_current',<381>,4:37], [@26,168:177='sc_current',<381>,5:32]], first_sourced_dt=[[@36,253:262='sc_current',<381>,6:20]], sourcecontact_id=[[@11,83:92='sc_current',<381>,4:8]], contact_priority=[[@31,200:209='sc_current',<381>,5:64]]}}, interface={contact_key=[{name=contact_key, table_ref=sc_current}], sourcecontact_id=[{name=sourcecontact_id, table_ref=sc_current}], rank_rc=[{name=contact_key, table_ref=sc_current}, {name=contact_priority, table_ref=sc_current}, {name=first_sourced_dt, table_ref=sc_current}]}, table_alias={sc_current={{ ref ( 'prc__contacts_by_sourcecontacts_current') }}}}, filters=[{name=rank_rc, table_ref=rc}], interface={contact_key=[{name=contact_key, table_ref=query0}], sourcecontact_id=[{name=sourcecontact_id, table_ref=query0}]}, table_alias={rc=query0}}, filters=[{name=rn, table_ref=crgd}], interface={contact_key=[{name=contact_key, table_ref=crgd}], fund_date_mr_calc=[{name=gift_dt, table_ref=crgd}], gift_id=[{name=gift_id, table_ref=crgd}], fund_name_mr_calc=[{name=fund_desc, table_ref=crgd}], fund_amt_mr_calc=[{name=fund_amount, table_ref=crgd}]}, def_query5={context_list={recent_sourcecontacts=query1, rc=query1}, def_union4={context_list={recent_sourcecontacts=query1}, query_dictionary={fund_desc=[[@110,778:787='gifts_data',<381>,15:51]], source_partnercontact_id=[[@84,508:517='gifts_data',<381>,10:33], [@287,2149:2158='gifts_data',<381>,32:25]], fund_amount=[[@105,749:758='gifts_data',<381>,15:22]], *=[[@69,443:452='gifts_data',<381>,9:50]], gift_dt=[[@89,575:584='gifts_data',<381>,11:31]]}, interface={fund_desc=query_column, source_partnercontact_id=query_column, fund_amount=query_column, gift_id=query_column, gift_dt=query_column, *=wildcard}, table_alias={recent_sourcecontacts=query1}, def_query3={context_list={recent_sourcecontacts=query1}, query_dictionary={fund_desc=[[@223,1616:1624='fund_desc',<381>,25:133]], source_partnercontact_id=[[@211,1559:1582='source_partnercontact_id',<381>,25:76]], fund_amount=[[@215,1588:1598='fund_amount',<381>,25:105]], gift_id=[[@205,1529:1535='gift_id',<381>,25:46]], gift_dt=[[@219,1604:1610='gift_dt',<381>,25:121]]}, table_dictionary={{{ source('pdp_ams', 'gifts_allocation')}}={soft_credit_id=[[@207,1538:1539='ga',<381>,25:55]], soft_credit_amount=[[@279,2086:2087='ga',<381>,31:80]], gift_id=[[@203,1526:1527='ga',<381>,25:43], [@246,1833:1834='ga',<381>,28:39], [@265,1982:1983='ga',<381>,30:39]], gift_dt=[[@217,1601:1602='ga',<381>,25:118]]}, {{ source('pdp_ams', 'gifts')}}={gift_id=[[@250,1846:1846='g',<381>,28:52]], eab_marketing_inclusion_ind=[[@273,2048:2048='g',<381>,31:42]]}, {{ source('pdp_ams','gifts_funds')}}={fund_desc=[[@221,1613:1614='gf',<381>,25:130]], fund_amount=[[@213,1585:1586='gf',<381>,25:102]], gift_id=[[@269,1995:1996='gf',<381>,30:52]]}}, filters=[{name=gift_id, table_ref=ga}, {name=gift_id, table_ref=g}, {name=gift_id, table_ref=gf}, {name=eab_marketing_inclusion_ind, table_ref=g}, {name=soft_credit_amount, table_ref=ga}], interface={fund_desc=[{name=fund_desc, table_ref=gf}], source_partnercontact_id=[{name=soft_credit_id, table_ref=ga}], fund_amount=[{name=fund_amount, table_ref=gf}], gift_id=[{name=gift_id, table_ref=ga}], gift_dt=[{name=gift_dt, table_ref=ga}]}, table_alias={g={{ source('PDP_AMS', 'gifts')}}, recent_sourcecontacts=query1, ga={{ source('PDP_AMS', 'gifts_allocation')}}, gf={{ source('PDP_AMS','gifts_funds')}}}}, def_query2={context_list={recent_sourcecontacts=query1}, query_dictionary={fund_desc=[[@159,1109:1117='fund_desc',<381>,19:110]], source_partnercontact_id=[[@147,1053:1076='source_partnercontact_id',<381>,19:54]], fund_amount=[[@151,1082:1092='fund_amount',<381>,19:83]], gift_id=[[@143,1042:1048='gift_id',<381>,19:43]], gift_dt=[[@155,1097:1103='gift_dt',<381>,19:98]]}, table_dictionary={{{ source('pdp_ams', 'gifts')}}={source_partnercontact_id=[[@145,1051:1051='g',<381>,19:52]], gift_id=[[@141,1040:1040='g',<381>,19:41], [@182,1320:1320='g',<381>,22:39], [@250,1846:1846='g',<381>,28:52]], gift_dt=[[@153,1095:1095='g',<381>,19:96]], gift_amount=[[@196,1423:1423='g',<381>,23:80]], eab_marketing_inclusion_ind=[[@190,1385:1385='g',<381>,23:42], [@273,2048:2048='g',<381>,31:42]]}, {{ source('pdp_ams','gifts_funds')}}={fund_desc=[[@157,1106:1107='gf',<381>,19:107], [@221,1613:1614='gf',<381>,25:130]], fund_amount=[[@149,1079:1080='gf',<381>,19:80], [@213,1585:1586='gf',<381>,25:102]], gift_id=[[@186,1332:1333='gf',<381>,22:51], [@269,1995:1996='gf',<381>,30:52]]}}, filters=[{name=gift_id, table_ref=g}, {name=gift_id, table_ref=gf}, {name=eab_marketing_inclusion_ind, table_ref=g}, {name=gift_amount, table_ref=g}], interface={fund_desc=[{name=fund_desc, table_ref=gf}], source_partnercontact_id=[{name=source_partnercontact_id, table_ref=g}], fund_amount=[{name=fund_amount, table_ref=gf}], gift_id=[{name=gift_id, table_ref=g}], gift_dt=[{name=gift_dt, table_ref=g}]}, table_alias={g={{ source('PDP_AMS', 'gifts')}}, recent_sourcecontacts=query1, gf={{ source('PDP_AMS','gifts_funds')}}}}}, query_dictionary={fund_desc=[[@304,2271:2274='crgd',<381>,35:7]], contact_key=[[@67,430:440='contact_key',<381>,9:37], [@296,2225:2228='crgd',<381>,33:7]], fund_amount=[[@320,2367:2370='crgd',<381>,37:17]], gift_id=[[@300,2250:2253='crgd',<381>,34:7]], *=[[@71,454:454='*',<291>,9:61]], rn=[[@116,808:809='rn',<381>,15:81], [@333,2453:2456='crgd',<381>,39:6]], gift_dt=[[@310,2315:2318='crgd',<381>,36:7]]}, table_dictionary={{{ ref ( 'prc_contact_donor_xwalk') }}={fund_desc=[[@96,655:663='fund_desc',<381>,13:28]], source_partnercontact_id=[[@291,2187:2190='pcdx',<381>,32:63]], contact_key=[[@65,425:428='pcdx',<381>,9:32], [@134,982:985='pcdx',<381>,18:42]]}}, filters=[{name=contact_key, table_ref=rc}, {name=contact_key, table_ref=pcdx}, {name=source_partnercontact_id, table_ref=gifts_data}, {name=source_partnercontact_id, table_ref=pcdx}], interface={contact_key=[{name=contact_key, table_ref=pcdx}], *=[{name=*, table_ref=gifts_data}], rn=[{name=contact_key, table_ref=rc}, {name=source_partnercontact_id, table_ref=gifts_data}, {name=gift_dt, table_ref=gifts_data}, {name=fund_desc, table_ref=null}, {name=fund_amount, table_ref=gifts_data}, {name=fund_desc, table_ref=gifts_data}]}, table_alias={rc=query1, pcdx={{ ref ( 'prc_contact_donor_xwalk') }}, gifts_data=union4, recent_sourcecontacts=query1}}, table_alias={cte_recent_gift_data=query5, crgd=query5, recent_sourcecontacts=query1}}}",
				extractor.getSymbolTable().toString());
	}
}