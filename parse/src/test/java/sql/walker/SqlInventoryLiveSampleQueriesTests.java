package sql.walker;
import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

public class SqlInventoryLiveSampleQueriesTests extends AbstractSqlParseEventWalkerTest {

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
		Assert.assertEquals("Table Dictionary is wrong", "{schl_type={value=[[@34,154:162='schl_type',<329>,1:154], [@40,181:189='schl_type',<329>,1:181], [@50,236:244='schl_type',<329>,1:236]], field_record=[[@357,1828:1836='schl_type',<329>,1:1828]]}, school={school_id=[[@4,28:28='s',<329>,1:28]], school_country=[[@133,625:625='s',<329>,1:625], [@139,653:653='s',<329>,1:653], [@149,712:712='s',<329>,1:712]], school_name=[[@100,467:467='s',<329>,1:467], [@106,492:492='s',<329>,1:492], [@116,553:553='s',<329>,1:553]], school_city=[[@199,948:948='s',<329>,1:948], [@205,973:973='s',<329>,1:973], [@215,1026:1026='s',<329>,1:1026]], school_region=[[@166,790:790='s',<329>,1:790], [@172,817:817='s',<329>,1:817], [@182,874:874='s',<329>,1:874]], school_key=[[@18,82:82='s',<329>,1:82], [@302,1475:1475='s',<329>,1:1475], [@314,1553:1553='s',<329>,1:1553]]}, <slate_lookup_school>={lookup_school_name=[[@112,526:527='sl',<329>,1:526]], lookup_school_id=[[@298,1453:1454='sl',<329>,1:1453]]}, <slate_address>={address_county=[[@229,1085:1085='a',<329>,1:1085]], address_record=[[@340,1724:1724='a',<329>,1:1724]], address_region=[[@178,852:852='a',<329>,1:852]], address_street=[[@257,1205:1205='a',<329>,1:1205]], address_country=[[@145,689:689='a',<329>,1:689]], address_rank_overall=[[@348,1770:1770='a',<329>,1:1770]], address_zip=[[@243,1148:1148='a',<329>,1:1148]], address_city=[[@211,1006:1006='a',<329>,1:1006]]}, <slate_dataset>={dataset_id=[[@322,1605:1605='d',<329>,1:1605]], dataset_name=[[@46,216:216='d',<329>,1:216], [@79,370:370='d',<329>,1:370], [@330,1652:1652='d',<329>,1:1652]]}, schl_cat={value=[[@67,310:317='schl_cat',<329>,1:310], [@73,336:343='schl_cat',<329>,1:336], [@83,390:397='schl_cat',<329>,1:390]], field_record=[[@368,1903:1910='schl_cat',<329>,1:1903]]}, <slate_dataset_row>={dataset_row_created=[[@271,1270:1271='dr',<329>,1:1270]], dataset_row_id=[[@344,1743:1744='dr',<329>,1:1743], [@361,1853:1854='dr',<329>,1:1853], [@372,1927:1928='dr',<329>,1:1927]], dataset_row_updated=[[@282,1337:1338='dr',<329>,1:1337]], dataset_row_key=[[@310,1532:1533='dr',<329>,1:1532]], dataset_row_dataset=[[@326,1620:1621='dr',<329>,1:1620]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={school_type=[[@61,276:286='school_type',<329>,1:276]], crm_created_at=[[@278,1310:1323='crm_created_at',<329>,1:1310]], school_name=[[@127,591:601='school_name',<329>,1:591]], school_city=[[@225,1061:1071='school_city',<329>,1:1061]], school_category=[[@94,429:443='school_category',<329>,1:429]], school_state=[[@193,913:924='school_state',<329>,1:913]], school_id=[[@14,60:68='school_id',<329>,1:60]], school_country=[[@160,753:766='school_country',<329>,1:753]], school_ceeb_code=[[@28,115:130='school_ceeb_code',<329>,1:115]], school_address=[[@267,1243:1256='school_address',<329>,1:1243]], school_county=[[@239,1122:1134='school_county',<329>,1:1122]], crm_updated_at=[[@289,1377:1390='crm_updated_at',<329>,1:1377]], school_zip=[[@253,1182:1191='school_zip',<329>,1:1182]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={school_type=[[@61,276:286='school_type',<329>,1:276]], crm_created_at=[[@278,1310:1323='crm_created_at',<329>,1:1310]], school_name=[[@127,591:601='school_name',<329>,1:591]], school_city=[[@225,1061:1071='school_city',<329>,1:1061]], school_category=[[@94,429:443='school_category',<329>,1:429]], school_state=[[@193,913:924='school_state',<329>,1:913]], school_id=[[@14,60:68='school_id',<329>,1:60]], school_country=[[@160,753:766='school_country',<329>,1:753]], school_ceeb_code=[[@28,115:130='school_ceeb_code',<329>,1:115]], school_address=[[@267,1243:1256='school_address',<329>,1:1243]], school_county=[[@239,1122:1134='school_county',<329>,1:1122]], crm_updated_at=[[@289,1377:1390='crm_updated_at',<329>,1:1377]], school_zip=[[@253,1182:1191='school_zip',<329>,1:1182]]}, table_dictionary={schl_type={value=[[@34,154:162='schl_type',<329>,1:154], [@40,181:189='schl_type',<329>,1:181], [@50,236:244='schl_type',<329>,1:236]], field_record=[[@357,1828:1836='schl_type',<329>,1:1828]]}, school={school_id=[[@4,28:28='s',<329>,1:28]], school_country=[[@133,625:625='s',<329>,1:625], [@139,653:653='s',<329>,1:653], [@149,712:712='s',<329>,1:712]], school_name=[[@100,467:467='s',<329>,1:467], [@106,492:492='s',<329>,1:492], [@116,553:553='s',<329>,1:553]], school_city=[[@199,948:948='s',<329>,1:948], [@205,973:973='s',<329>,1:973], [@215,1026:1026='s',<329>,1:1026]], school_region=[[@166,790:790='s',<329>,1:790], [@172,817:817='s',<329>,1:817], [@182,874:874='s',<329>,1:874]], school_key=[[@18,82:82='s',<329>,1:82], [@302,1475:1475='s',<329>,1:1475], [@314,1553:1553='s',<329>,1:1553]]}, <slate_lookup_school>={lookup_school_name=[[@112,526:527='sl',<329>,1:526]], lookup_school_id=[[@298,1453:1454='sl',<329>,1:1453]]}, <slate_address>={address_county=[[@229,1085:1085='a',<329>,1:1085]], address_record=[[@340,1724:1724='a',<329>,1:1724]], address_region=[[@178,852:852='a',<329>,1:852]], address_street=[[@257,1205:1205='a',<329>,1:1205]], address_country=[[@145,689:689='a',<329>,1:689]], address_rank_overall=[[@348,1770:1770='a',<329>,1:1770]], address_zip=[[@243,1148:1148='a',<329>,1:1148]], address_city=[[@211,1006:1006='a',<329>,1:1006]]}, <slate_dataset>={dataset_id=[[@322,1605:1605='d',<329>,1:1605]], dataset_name=[[@46,216:216='d',<329>,1:216], [@79,370:370='d',<329>,1:370], [@330,1652:1652='d',<329>,1:1652]]}, schl_cat={value=[[@67,310:317='schl_cat',<329>,1:310], [@73,336:343='schl_cat',<329>,1:336], [@83,390:397='schl_cat',<329>,1:390]], field_record=[[@368,1903:1910='schl_cat',<329>,1:1903]]}, <slate_dataset_row>={dataset_row_created=[[@271,1270:1271='dr',<329>,1:1270]], dataset_row_id=[[@344,1743:1744='dr',<329>,1:1743], [@361,1853:1854='dr',<329>,1:1853], [@372,1927:1928='dr',<329>,1:1927]], dataset_row_updated=[[@282,1337:1338='dr',<329>,1:1337]], dataset_row_key=[[@310,1532:1533='dr',<329>,1:1532]], dataset_row_dataset=[[@326,1620:1621='dr',<329>,1:1620]]}}, filters=[{name=lookup_school_id, table_ref=sl}, {name=school_key, table_ref=s}, {name=dataset_row_key, table_ref=dr}, {name=dataset_id, table_ref=d}, {name=dataset_row_dataset, table_ref=dr}, {name=dataset_name, table_ref=d}, {name=address_record, table_ref=a}, {name=dataset_row_id, table_ref=dr}, {name=address_rank_overall, table_ref=a}, {name=field_record, table_ref=schl_type}, {name=field_record, table_ref=schl_cat}], interface={school_type=[{name=dataset_name, table_ref=d}, {name=value, table_ref=schl_type}], crm_created_at=[{name=dataset_row_created, table_ref=dr}], school_name=[{name=lookup_school_name, table_ref=sl}, {name=school_name, table_ref=s}], school_city=[{name=address_city, table_ref=a}, {name=school_city, table_ref=s}], school_category=[{name=dataset_name, table_ref=d}, {name=value, table_ref=schl_cat}], school_state=[{name=address_region, table_ref=a}, {name=school_region, table_ref=s}], school_id=[{name=school_id, table_ref=s}], school_country=[{name=address_country, table_ref=a}, {name=school_country, table_ref=s}], school_ceeb_code=[{name=school_key, table_ref=s}], school_address=[{name=address_street, table_ref=a}], school_county=[{name=address_county, table_ref=a}], crm_updated_at=[{name=dataset_row_updated, table_ref=dr}], school_zip=[{name=address_zip, table_ref=a}]}, table_alias={a=<slate_address>, s=school, d=<slate_dataset>, sl=<slate_lookup_school>, dr=<slate_dataset_row>}}}",
				extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{<[Enrollment Services].[Client Entering Class]>={*=[[@1,8:10='cec',<329>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,12:12='*',<289>,1:12]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@3,12:12='*',<289>,1:12]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={*=[[@1,8:10='cec',<329>,1:8]]}}, filters=[{name=<Permanent Country>, type=predicand}, {name=<College Attendance Status>, type=predicand}, {name=<Graduation Year>, type=predicand}, {name=<Application Admissions Status>, type=predicand}, {name=<Term Of Interest>, type=predicand}, {name=<Date Submitted>, type=predicand}], interface={*=[{name=*, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{<[Enrollment Services].[Client Entering Class]>={<Term Of Interest>=[[@57,460:462='cec',<329>,1:460], [@63,494:496='cec',<329>,1:494]], <Graduation Year>=[[@29,244:246='cec',<329>,1:244], [@35,277:279='cec',<329>,1:277]], <Permanent Country>=[[@9,79:81='cec',<329>,1:79], [@15,114:116='cec',<329>,1:114]], *=[[@1,8:10='cec',<329>,1:8]], <Date Submitted>=[[@71,550:552='cec',<329>,1:550], [@77,582:584='cec',<329>,1:582]], <Application Admissions Status>=[[@43,331:333='cec',<329>,1:331], [@49,378:380='cec',<329>,1:378]], <College Attendance Status>=[[@22,171:173='cec',<329>,1:171]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,12:12='*',<289>,1:12]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@3,12:12='*',<289>,1:12]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={*=[[@1,8:10='cec',<329>,1:8]]}}, filters=[{substitution={name=<Permanent Country>, type=column}, table_ref=cec}, {substitution={name=<College Attendance Status>, type=column}, table_ref=cec}, {substitution={name=<Graduation Year>, type=column}, table_ref=cec}, {substitution={name=<Application Admissions Status>, type=column}, table_ref=cec}, {substitution={name=<Term Of Interest>, type=column}, table_ref=cec}, {substitution={name=<Date Submitted>, type=column}, table_ref=cec}], interface={*=[{name=*, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{<fulfill.[domain].[entity].[file category]>={*=[[@1,8:10='cec',<329>,1:8]]}, <fulfill.[domain].[entity]>={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,12:12='*',<289>,1:12]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@3,12:12='*',<289>,1:12]]}, table_dictionary={<fulfill.[domain].[entity].[file category]>={*=[[@1,8:10='cec',<329>,1:8]]}, <fulfill.[domain].[entity]>={}}, interface={*=[{name=*, table_ref=cec}]}, table_alias={cec=<fulfill.[domain].[entity].[file category]>, oth=<fulfill.[domain].[entity]>}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{<fulfill.[domain].[entity].[file category].{snapshot}>={*=[[@1,8:10='oth',<329>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,12:12='*',<289>,1:12]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query0={query_dictionary={*=[[@3,12:12='*',<289>,1:12]]}, table_dictionary={<fulfill.[domain].[entity].[file category].{snapshot}>={*=[[@1,8:10='oth',<329>,1:8]]}}, interface={*=[{name=*, table_ref=oth}]}, table_alias={oth=<fulfill.[domain].[entity].[file category].{snapshot}>}}}",
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
		assertDiagnosticCountBySeverity(snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				null,
				null,
				16);
		assertDiagnosticCountBySeverity(snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				null,
				3);
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
	}


	@Test
	public void getTermSqlTest() {
		/*
		 * Term COLUMNS: RECORD_TYPE, ACTION, EXTERNAL_ID, NAME, BEGIN_DATE,
		 * END_DATE
		 */
		String query = "select term.record_type as RECORD_TYPE, term.action as ACTION,  term.external_id as EXTERNAL_ID, term.name as NAME, datestr(term.begin_date, "
				+ " 'TERM_SOURCE_DATE_FORMAT', 'SSCPLUS_DEFAULT_DATE_FORMAT') as BEGIN_DATE, datestr(term.end_date, 'TERM_SOURCE_DATE_FORMAT', "
				+ "'SSCPLUS_DEFAULT_DATE_FORMAT') as END_DATE from academicPeriodTbl " + " term";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
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
	}


	@Test
	public void getSectionSqlV6Test() {
		/*
		 * Section COLUMNS: RECORD_TYPE, ACTION, TERM_ID, COURSE_EXTERNAL_ID,
		 * SECTION_NAME, SECTION_TAGS
		 */
		final String query = "select s.record_type as RECORD_TYPE, " + "s.action as ACTION, "
				+ "s.term_code as TERM_ID, " + "concat_ws('-',s.subject_code,s.course_number) as COURSE_EXTERNAL_ID, "
				+ "case  " + "when s.section_name is null or length(trim(s.section_name))=0  " + "then ''  "
				+ "else s.section_name  " + "end as SECTION_NAME, " + "s.section_tag as SECTION_TAGS "
				+ "from sectionTbl s  " + "inner join termFilterTbl tf  " + "on s.term_code = tf.term_id ";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
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
		assertNoFatalErrors(extractor);
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
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
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
		assertDiagnosticCountBySeverity(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'term_row' at (l:3 c:254). Possible sources: [query0, student_major_term]",
				"term_row",
				1);
		assertDiagnosticCountBySeverity(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s): [degree_cd_2 [(l:3 c:127)], concentration_cd [(l:3 c:77)], term_row [(l:3 c:254), (l:3 c:286)], concentration_cd_2 [(l:3 c:142)], major_cd [(l:3 c:23), (l:3 c:178)], student_id [(l:3 c:9), (l:3 c:234)], college_cd [(l:3 c:50)], degree_cd [(l:3 c:64)], college_cd_2 [(l:3 c:111)], major_cd_2 [(l:3 c:97)]]",
				"degree_cd_2",
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
		assertDiagnosticCountBySeverity(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'term_row' at (l:1 c:37). Possible sources: [query0, student_major_term]",
				"term_row",
				1);
		assertDiagnosticCountBySeverity(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s): [term_row [(l:1 c:37)]]",
				"term_row",
				1);
	
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=term_id, table_ref=smt}}, 2={column={name=term_id, table_ref=terms}}, 3={column={name=term_row, table_ref=null}}}, orderby={1={null_order=null, predicand={literal=1}, sort_order=ASC}, 2={null_order=null, predicand={literal=2}, sort_order=desc}}, from={join={1={table={alias=smt, table=student_major_term}}, 2={join=left, on={condition={left={column={name=term_id, table_ref=terms}}, right={column={name=term_id, table_ref=smt}}, operator==}}}, 3={table={alias=terms, query={select={1={alias=term_row, window_function={over={orderby={1={null_order=null, predicand={column={name=start_date, table_ref=null}}, sort_order=asc}}}, function={function_name=row_number, parameters=null}}}, 2={column={name=term_id, table_ref=null}}}, from={table={alias=null, table=standard_term}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[term_row, term_id]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{standard_term={term_id=[[@30,152:158='term_id',<329>,2:75]], start_date=[[@24,123:132='start_date',<329>,2:46]]}, student_major_term={term_id=[[@1,9:11='smt',<329>,1:9], [@40,208:210='smt',<329>,3:47]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={term_row=[[@28,142:149='term_row',<329>,2:65]], term_id=[[@30,152:158='term_id',<329>,2:75], [@5,22:26='terms',<329>,1:22], [@36,192:196='terms',<329>,3:31]]}, query1={term_row=[[@9,37:44='term_row',<329>,1:37]], term_id=[[@3,13:19='term_id',<329>,1:13], [@7,28:34='term_id',<329>,1:28]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{query1={query_dictionary={term_row=[[@9,37:44='term_row',<329>,1:37]], term_id=[[@3,13:19='term_id',<329>,1:13], [@7,28:34='term_id',<329>,1:28]]}, table_dictionary={student_major_term={term_id=[[@1,9:11='smt',<329>,1:9], [@40,208:210='smt',<329>,3:47]]}}, def_query0={query_dictionary={term_row=[[@28,142:149='term_row',<329>,2:65]], term_id=[[@30,152:158='term_id',<329>,2:75], [@5,22:26='terms',<329>,1:22], [@36,192:196='terms',<329>,3:31]]}, table_dictionary={standard_term={term_id=[[@30,152:158='term_id',<329>,2:75]], start_date=[[@24,123:132='start_date',<329>,2:46]]}}, interface={term_row=[{name=start_date, table_ref=standard_term}], term_id=[{name=term_id, table_ref=standard_term}]}}, ordered_by=[], filters=[{name=term_id, table_ref=terms}, {name=term_id, table_ref=smt}], interface={term_row=[{name=term_row, table_ref=null}], term_id=[{name=term_id, table_ref=terms}]}, table_alias={terms=query0, smt=student_major_term}}}",
				extractor.getSymbolTable().toString());
	}

}
