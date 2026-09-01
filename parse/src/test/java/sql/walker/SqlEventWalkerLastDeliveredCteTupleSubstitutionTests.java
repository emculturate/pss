package sql.walker;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * Phase 2.7 — {@code last_delivered_cte} + {@code ead} (tuple substitution or physical table) first in
 * {@code FROM}, joined to the CTE via conditionless join shapes, with column and substitution-column
 * references introduced in the final query clause ({@code WHERE}, {@code GROUP BY}, {@code HAVING},
 * {@code QUALIFY}, {@code ORDER BY}, scalar predicand subquery).
 */
public class SqlEventWalkerLastDeliveredCteTupleSubstitutionTests extends AbstractSqlParseEventWalkerTest {

	private static final String CTE = "WITH last_delivered_cte AS ("
			+ " SELECT MAX(log_del.contact_deleted_dt) AS last_del"
			+ " FROM PDP_UG.log__acs_contact_deletions AS log_del"
			+ ") ";

	private static final String TUPLE_SELECT = "SELECT"
			+ " CAST(ead.<ES Partner ID> AS varchar(64)) AS es_partner_id,"
			+ " CAST(ead.<ACS Contact ID> AS varchar(50)) AS acs_contact_id";

	private static final String PHYSICAL_SELECT = "SELECT ead.es_partner_id, ead.acs_contact_id";

	private static final String TUPLE_FROM = " FROM <[Acquia].[exp__acquia_deletions].{fulfillment}> AS ead";

	private static final String PHYSICAL_FROM = " FROM exp__acquia_deletions AS ead";

	private static final String TUPLE_TABLE_KEY = "<[Acquia].[exp__acquia_deletions].{fulfillment}>";

	private static final String PHYSICAL_TABLE_KEY = "exp__acquia_deletions";

	private void assertLastDeliveredOutputs(SqlParseEventWalker extractor, String expectedAst,
			String expectedInterface, String expectedSubstitutions, String expectedTableDictionary,
			String expectedQueryColumnDictionary, String expectedSymbolTable) {
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", expectedAst, extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", expectedInterface, extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", expectedSubstitutions,
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", expectedTableDictionary,
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", expectedQueryColumnDictionary,
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", expectedSymbolTable,
				extractor.getSymbolTable().toString());
	}

	@SuppressWarnings("unchecked")
	private void assertGlobalTableDictionaryContains(SqlParseEventWalker extractor,
			String tableKey, String... columnKeys) {
		Map<String, Object> global = extractor.getTableColumnDictionaryMap();
		Assert.assertTrue("Global tableDictionary missing key " + tableKey, global.containsKey(tableKey));
		String entry = global.get(tableKey).toString();
		for (String columnKey : columnKeys) {
			Assert.assertTrue("Global tableDictionary[" + tableKey + "] missing " + columnKey,
					entry.contains(columnKey + "="));
		}
	}

	@Test
	public void lastDeliveredTupleSubstitutionCaseInWhereCrossJoinTest() {
		final String query = "WITH last_delivered_cte AS ("
				+ " SELECT MAX(log_del.contact_deleted_dt) AS last_del"
				+ " FROM PDP_UG.log__acs_contact_deletions AS log_del"
				+ ") SELECT"
				+ " CAST(ead.<ES Partner ID> AS varchar(64)) AS es_partner_id,"
				+ " CAST(ead.<ACS Contact ID> AS varchar(50)) AS acs_contact_id"
				+ " FROM <[Acquia].[exp__acquia_deletions].{fulfillment}> AS ead"
				+ " CROSS JOIN last_delivered_cte"
				+ " WHERE CASE WHEN last_delivered_cte.last_del IS NULL THEN 1=1"
				+ " ELSE ead.<Contact Deleted Dt> > last_delivered_cte.last_del END";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertLastDeliveredOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=contact_deleted_dt, table_ref=log_del}}}, alias=last_del}}, from={table={alias=log_del, schema=PDP_UG, table=log__acs_contact_deletions}}}, alias=last_delivered_cte}}, query={select={1={function={function_name=CAST, data_type={length=64, type=VARCHAR}, type=CAST, value={column={substitution={name=<ES Partner ID>, type=column}, table_ref=ead}}}, alias=es_partner_id}, 2={function={function_name=CAST, data_type={length=50, type=VARCHAR}, type=CAST, value={column={substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}}}, alias=acs_contact_id}}, from={join={1={table={alias=ead, substitution={name=<[Acquia].[exp__acquia_deletions].{fulfillment}>, parts={1=[Acquia], 2=[exp__acquia_deletions], 3={fulfillment}}, type=tuple}}}, 2={join=CROSSJOIN}, 3={table={alias=null, table=last_delivered_cte}}}}, where={case={clauses={1={then={condition={left={literal=1}, right={literal=1}, operator==}}, when={condition={left={column={name=last_del, table_ref=last_delivered_cte}}, operator=IS NULL}}}}, else={condition={left={column={substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}}, right={column={name=last_del, table_ref=last_delivered_cte}}, operator=>}}}}}}}",
				"[es_partner_id, acs_contact_id]", "{<ACS Contact ID>=column, <[Acquia].[exp__acquia_deletions].{fulfillment}>=tuple, <ES Partner ID>=column, <Contact Deleted Dt>=column}",
				"{pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}, <[Acquia].[exp__acquia_deletions].{fulfillment}>={<ACS Contact ID>=[[@37,202:204='ead',<393>,1:202]], <ES Partner ID>=[[@23,143:145='ead',<393>,1:143]], <Contact Deleted Dt>=[[@68,414:416='ead',<393>,1:414]]}}",
				"{query0={last_del=[[@12,71:78='last_del',<393>,1:71], [@58,364:381='last_delivered_cte',<393>,1:364], [@72,441:458='last_delivered_cte',<393>,1:441]]}, query1={es_partner_id=[[@33,182:194='es_partner_id',<393>,1:182]], acs_contact_id=[[@47,242:255='acs_contact_id',<393>,1:242]]}}",
				"{def_query1={context_list={last_delivered_cte=query0}, query_dictionary={es_partner_id=[[@33,182:194='es_partner_id',<393>,1:182]], acs_contact_id=[[@47,242:255='acs_contact_id',<393>,1:242]]}, table_dictionary={<[Acquia].[exp__acquia_deletions].{fulfillment}>={<ACS Contact ID>=[[@37,202:204='ead',<393>,1:202]], <ES Partner ID>=[[@23,143:145='ead',<393>,1:143]], <Contact Deleted Dt>=[[@68,414:416='ead',<393>,1:414]]}}, def_query0={query_dictionary={last_del=[[@12,71:78='last_del',<393>,1:71], [@58,364:381='last_delivered_cte',<393>,1:364], [@72,441:458='last_delivered_cte',<393>,1:441]]}, table_dictionary={pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}}, interface={last_del=[{name=contact_deleted_dt, table_ref=log_del}]}, table_alias={log_del=PDP_UG.log__acs_contact_deletions}}, filters=[{name=last_del, table_ref=last_delivered_cte}, {substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}], interface={es_partner_id=[{substitution={name=<ES Partner ID>, type=column}, table_ref=ead}], acs_contact_id=[{substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}]}, table_alias={last_delivered_cte=query0, ead=<[Acquia].[exp__acquia_deletions].{fulfillment}>}}}");
		assertGlobalTableDictionaryContains(extractor, TUPLE_TABLE_KEY, "<Contact Deleted Dt>");
	}

	@Test
	public void lastDeliveredPhysicalColumnCaseInWhereCrossJoinTest() {
		final String query = "WITH last_delivered_cte AS ("
				+ " SELECT MAX(log_del.contact_deleted_dt) AS last_del"
				+ " FROM PDP_UG.log__acs_contact_deletions AS log_del"
				+ ") SELECT ead.es_partner_id, ead.acs_contact_id"
				+ " FROM exp__acquia_deletions AS ead"
				+ " CROSS JOIN last_delivered_cte"
				+ " WHERE CASE WHEN last_delivered_cte.last_del IS NULL THEN 1=1"
				+ " ELSE ead.contact_deleted_dt > last_delivered_cte.last_del END";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertLastDeliveredOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=contact_deleted_dt, table_ref=log_del}}}, alias=last_del}}, from={table={alias=log_del, schema=PDP_UG, table=log__acs_contact_deletions}}}, alias=last_delivered_cte}}, query={select={1={column={name=es_partner_id, table_ref=ead}}, 2={column={name=acs_contact_id, table_ref=ead}}}, from={join={1={table={alias=ead, table=exp__acquia_deletions}}, 2={join=CROSSJOIN}, 3={table={alias=null, table=last_delivered_cte}}}}, where={case={clauses={1={then={condition={left={literal=1}, right={literal=1}, operator==}}, when={condition={left={column={name=last_del, table_ref=last_delivered_cte}}, operator=IS NULL}}}}, else={condition={left={column={name=contact_deleted_dt, table_ref=ead}}, right={column={name=last_del, table_ref=last_delivered_cte}}, operator=>}}}}}}}",
				"[es_partner_id, acs_contact_id]", "{}",
				"{exp__acquia_deletions={contact_deleted_dt=[[@48,306:308='ead',<393>,1:306]], es_partner_id=[[@21,138:140='ead',<393>,1:138]], acs_contact_id=[[@25,157:159='ead',<393>,1:157]]}, pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}}",
				"{query0={last_del=[[@12,71:78='last_del',<393>,1:71], [@38,256:273='last_delivered_cte',<393>,1:256], [@52,331:348='last_delivered_cte',<393>,1:331]]}, query1={es_partner_id=[[@23,142:154='es_partner_id',<393>,1:142]], acs_contact_id=[[@27,161:174='acs_contact_id',<393>,1:161]]}}",
				"{def_query1={context_list={last_delivered_cte=query0}, query_dictionary={es_partner_id=[[@23,142:154='es_partner_id',<393>,1:142]], acs_contact_id=[[@27,161:174='acs_contact_id',<393>,1:161]]}, table_dictionary={exp__acquia_deletions={contact_deleted_dt=[[@48,306:308='ead',<393>,1:306]], es_partner_id=[[@21,138:140='ead',<393>,1:138]], acs_contact_id=[[@25,157:159='ead',<393>,1:157]]}}, def_query0={query_dictionary={last_del=[[@12,71:78='last_del',<393>,1:71], [@38,256:273='last_delivered_cte',<393>,1:256], [@52,331:348='last_delivered_cte',<393>,1:331]]}, table_dictionary={pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}}, interface={last_del=[{name=contact_deleted_dt, table_ref=log_del}]}, table_alias={log_del=PDP_UG.log__acs_contact_deletions}}, filters=[{name=last_del, table_ref=last_delivered_cte}, {name=contact_deleted_dt, table_ref=ead}], interface={es_partner_id=[{name=es_partner_id, table_ref=ead}], acs_contact_id=[{name=acs_contact_id, table_ref=ead}]}, table_alias={last_delivered_cte=query0, ead=exp__acquia_deletions}}}");
		assertGlobalTableDictionaryContains(extractor, PHYSICAL_TABLE_KEY, "contact_deleted_dt");
	}

	@Test
	public void lastDeliveredTupleSubstitutionPlainWhereCrossJoinTest() {
		final String query = "WITH last_delivered_cte AS ("
				+ " SELECT MAX(log_del.contact_deleted_dt) AS last_del"
				+ " FROM PDP_UG.log__acs_contact_deletions AS log_del"
				+ ") SELECT"
				+ " CAST(ead.<ES Partner ID> AS varchar(64)) AS es_partner_id,"
				+ " CAST(ead.<ACS Contact ID> AS varchar(50)) AS acs_contact_id"
				+ " FROM <[Acquia].[exp__acquia_deletions].{fulfillment}> AS ead"
				+ " CROSS JOIN last_delivered_cte"
				+ " WHERE ead.<Contact Deleted Dt> > last_delivered_cte.last_del";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertLastDeliveredOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=contact_deleted_dt, table_ref=log_del}}}, alias=last_del}}, from={table={alias=log_del, schema=PDP_UG, table=log__acs_contact_deletions}}}, alias=last_delivered_cte}}, query={select={1={function={function_name=CAST, data_type={length=64, type=VARCHAR}, type=CAST, value={column={substitution={name=<ES Partner ID>, type=column}, table_ref=ead}}}, alias=es_partner_id}, 2={function={function_name=CAST, data_type={length=50, type=VARCHAR}, type=CAST, value={column={substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}}}, alias=acs_contact_id}}, from={join={1={table={alias=ead, substitution={name=<[Acquia].[exp__acquia_deletions].{fulfillment}>, parts={1=[Acquia], 2=[exp__acquia_deletions], 3={fulfillment}}, type=tuple}}}, 2={join=CROSSJOIN}, 3={table={alias=null, table=last_delivered_cte}}}}, where={condition={left={column={substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}}, right={column={name=last_del, table_ref=last_delivered_cte}}, operator=>}}}}}",
				"[es_partner_id, acs_contact_id]", "{<ACS Contact ID>=column, <[Acquia].[exp__acquia_deletions].{fulfillment}>=tuple, <ES Partner ID>=column, <Contact Deleted Dt>=column}",
				"{pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}, <[Acquia].[exp__acquia_deletions].{fulfillment}>={<ACS Contact ID>=[[@37,202:204='ead',<393>,1:202]], <ES Partner ID>=[[@23,143:145='ead',<393>,1:143]], <Contact Deleted Dt>=[[@56,354:356='ead',<393>,1:354]]}}",
				"{query0={last_del=[[@12,71:78='last_del',<393>,1:71], [@60,381:398='last_delivered_cte',<393>,1:381]]}, query1={es_partner_id=[[@33,182:194='es_partner_id',<393>,1:182]], acs_contact_id=[[@47,242:255='acs_contact_id',<393>,1:242]]}}",
				"{def_query1={context_list={last_delivered_cte=query0}, query_dictionary={es_partner_id=[[@33,182:194='es_partner_id',<393>,1:182]], acs_contact_id=[[@47,242:255='acs_contact_id',<393>,1:242]]}, table_dictionary={<[Acquia].[exp__acquia_deletions].{fulfillment}>={<ACS Contact ID>=[[@37,202:204='ead',<393>,1:202]], <ES Partner ID>=[[@23,143:145='ead',<393>,1:143]], <Contact Deleted Dt>=[[@56,354:356='ead',<393>,1:354]]}}, def_query0={query_dictionary={last_del=[[@12,71:78='last_del',<393>,1:71], [@60,381:398='last_delivered_cte',<393>,1:381]]}, table_dictionary={pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}}, interface={last_del=[{name=contact_deleted_dt, table_ref=log_del}]}, table_alias={log_del=PDP_UG.log__acs_contact_deletions}}, filters=[{substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}, {name=last_del, table_ref=last_delivered_cte}], interface={es_partner_id=[{substitution={name=<ES Partner ID>, type=column}, table_ref=ead}], acs_contact_id=[{substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}]}, table_alias={last_delivered_cte=query0, ead=<[Acquia].[exp__acquia_deletions].{fulfillment}>}}}");
		assertGlobalTableDictionaryContains(extractor, TUPLE_TABLE_KEY, "<Contact Deleted Dt>");
	}

	@Test
	public void lastDeliveredTupleSubstitutionCastInCaseCrossJoinTest() {
		final String query = "WITH last_delivered_cte AS ("
				+ " SELECT MAX(log_del.contact_deleted_dt) AS last_del"
				+ " FROM PDP_UG.log__acs_contact_deletions AS log_del"
				+ ") SELECT"
				+ " CAST(ead.<ES Partner ID> AS varchar(64)) AS es_partner_id,"
				+ " CAST(ead.<ACS Contact ID> AS varchar(50)) AS acs_contact_id"
				+ " FROM <[Acquia].[exp__acquia_deletions].{fulfillment}> AS ead"
				+ " CROSS JOIN last_delivered_cte"
				+ " WHERE CASE WHEN last_delivered_cte.last_del IS NULL THEN 1=1"
				+ " ELSE CAST(ead.<Contact Deleted Dt> AS date) > last_delivered_cte.last_del END";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertLastDeliveredOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=contact_deleted_dt, table_ref=log_del}}}, alias=last_del}}, from={table={alias=log_del, schema=PDP_UG, table=log__acs_contact_deletions}}}, alias=last_delivered_cte}}, query={select={1={function={function_name=CAST, data_type={length=64, type=VARCHAR}, type=CAST, value={column={substitution={name=<ES Partner ID>, type=column}, table_ref=ead}}}, alias=es_partner_id}, 2={function={function_name=CAST, data_type={length=50, type=VARCHAR}, type=CAST, value={column={substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}}}, alias=acs_contact_id}}, from={join={1={table={alias=ead, substitution={name=<[Acquia].[exp__acquia_deletions].{fulfillment}>, parts={1=[Acquia], 2=[exp__acquia_deletions], 3={fulfillment}}, type=tuple}}}, 2={join=CROSSJOIN}, 3={table={alias=null, table=last_delivered_cte}}}}, where={case={clauses={1={then={condition={left={literal=1}, right={literal=1}, operator==}}, when={condition={left={column={name=last_del, table_ref=last_delivered_cte}}, operator=IS NULL}}}}, else={condition={left={function={function_name=CAST, data_type={type=DATE}, type=CAST, value={column={substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}}}}, right={column={name=last_del, table_ref=last_delivered_cte}}, operator=>}}}}}}}",
				"[es_partner_id, acs_contact_id]", "{<ACS Contact ID>=column, <[Acquia].[exp__acquia_deletions].{fulfillment}>=tuple, <ES Partner ID>=column, <Contact Deleted Dt>=column}",
				"{pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}, <[Acquia].[exp__acquia_deletions].{fulfillment}>={<ACS Contact ID>=[[@37,202:204='ead',<393>,1:202]], <ES Partner ID>=[[@23,143:145='ead',<393>,1:143]], <Contact Deleted Dt>=[[@70,419:421='ead',<393>,1:419]]}}",
				"{query0={last_del=[[@12,71:78='last_del',<393>,1:71], [@58,364:381='last_delivered_cte',<393>,1:364], [@77,455:472='last_delivered_cte',<393>,1:455]]}, query1={es_partner_id=[[@33,182:194='es_partner_id',<393>,1:182]], acs_contact_id=[[@47,242:255='acs_contact_id',<393>,1:242]]}}",
				"{def_query1={context_list={last_delivered_cte=query0}, query_dictionary={es_partner_id=[[@33,182:194='es_partner_id',<393>,1:182]], acs_contact_id=[[@47,242:255='acs_contact_id',<393>,1:242]]}, table_dictionary={<[Acquia].[exp__acquia_deletions].{fulfillment}>={<ACS Contact ID>=[[@37,202:204='ead',<393>,1:202]], <ES Partner ID>=[[@23,143:145='ead',<393>,1:143]], <Contact Deleted Dt>=[[@70,419:421='ead',<393>,1:419]]}}, def_query0={query_dictionary={last_del=[[@12,71:78='last_del',<393>,1:71], [@58,364:381='last_delivered_cte',<393>,1:364], [@77,455:472='last_delivered_cte',<393>,1:455]]}, table_dictionary={pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}}, interface={last_del=[{name=contact_deleted_dt, table_ref=log_del}]}, table_alias={log_del=PDP_UG.log__acs_contact_deletions}}, filters=[{name=last_del, table_ref=last_delivered_cte}, {substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}], interface={es_partner_id=[{substitution={name=<ES Partner ID>, type=column}, table_ref=ead}], acs_contact_id=[{substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}]}, table_alias={last_delivered_cte=query0, ead=<[Acquia].[exp__acquia_deletions].{fulfillment}>}}}");
		assertGlobalTableDictionaryContains(extractor, TUPLE_TABLE_KEY, "<Contact Deleted Dt>");
	}

	@Test
	public void lastDeliveredTupleSubstitutionCoalesceInWhereCrossJoinTest() {
		final String query = "WITH last_delivered_cte AS ("
				+ " SELECT MAX(log_del.contact_deleted_dt) AS last_del"
				+ " FROM PDP_UG.log__acs_contact_deletions AS log_del"
				+ ") SELECT"
				+ " CAST(ead.<ES Partner ID> AS varchar(64)) AS es_partner_id,"
				+ " CAST(ead.<ACS Contact ID> AS varchar(50)) AS acs_contact_id"
				+ " FROM <[Acquia].[exp__acquia_deletions].{fulfillment}> AS ead"
				+ " CROSS JOIN last_delivered_cte"
				+ " WHERE COALESCE(ead.<Contact Deleted Dt>, last_delivered_cte.last_del)"
				+ " > last_delivered_cte.last_del";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertLastDeliveredOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=contact_deleted_dt, table_ref=log_del}}}, alias=last_del}}, from={table={alias=log_del, schema=PDP_UG, table=log__acs_contact_deletions}}}, alias=last_delivered_cte}}, query={select={1={function={function_name=CAST, data_type={length=64, type=VARCHAR}, type=CAST, value={column={substitution={name=<ES Partner ID>, type=column}, table_ref=ead}}}, alias=es_partner_id}, 2={function={function_name=CAST, data_type={length=50, type=VARCHAR}, type=CAST, value={column={substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}}}, alias=acs_contact_id}}, from={join={1={table={alias=ead, substitution={name=<[Acquia].[exp__acquia_deletions].{fulfillment}>, parts={1=[Acquia], 2=[exp__acquia_deletions], 3={fulfillment}}, type=tuple}}}, 2={join=CROSSJOIN}, 3={table={alias=null, table=last_delivered_cte}}}}, where={condition={left={function={parameters={1={column={substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}}, 2={column={name=last_del, table_ref=last_delivered_cte}}}, function_name=COALESCE}}, right={column={name=last_del, table_ref=last_delivered_cte}}, operator=>}}}}}",
				"[es_partner_id, acs_contact_id]", "{<ACS Contact ID>=column, <[Acquia].[exp__acquia_deletions].{fulfillment}>=tuple, <ES Partner ID>=column, <Contact Deleted Dt>=column}",
				"{pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}, <[Acquia].[exp__acquia_deletions].{fulfillment}>={<ACS Contact ID>=[[@37,202:204='ead',<393>,1:202]], <ES Partner ID>=[[@23,143:145='ead',<393>,1:143]], <Contact Deleted Dt>=[[@58,363:365='ead',<393>,1:363]]}}",
				"{query0={last_del=[[@12,71:78='last_del',<393>,1:71], [@62,389:406='last_delivered_cte',<393>,1:389], [@67,420:437='last_delivered_cte',<393>,1:420]]}, query1={es_partner_id=[[@33,182:194='es_partner_id',<393>,1:182]], acs_contact_id=[[@47,242:255='acs_contact_id',<393>,1:242]]}}",
				"{def_query1={context_list={last_delivered_cte=query0}, query_dictionary={es_partner_id=[[@33,182:194='es_partner_id',<393>,1:182]], acs_contact_id=[[@47,242:255='acs_contact_id',<393>,1:242]]}, table_dictionary={<[Acquia].[exp__acquia_deletions].{fulfillment}>={<ACS Contact ID>=[[@37,202:204='ead',<393>,1:202]], <ES Partner ID>=[[@23,143:145='ead',<393>,1:143]], <Contact Deleted Dt>=[[@58,363:365='ead',<393>,1:363]]}}, def_query0={query_dictionary={last_del=[[@12,71:78='last_del',<393>,1:71], [@62,389:406='last_delivered_cte',<393>,1:389], [@67,420:437='last_delivered_cte',<393>,1:420]]}, table_dictionary={pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}}, interface={last_del=[{name=contact_deleted_dt, table_ref=log_del}]}, table_alias={log_del=PDP_UG.log__acs_contact_deletions}}, filters=[{substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}, {name=last_del, table_ref=last_delivered_cte}], interface={es_partner_id=[{substitution={name=<ES Partner ID>, type=column}, table_ref=ead}], acs_contact_id=[{substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}]}, table_alias={last_delivered_cte=query0, ead=<[Acquia].[exp__acquia_deletions].{fulfillment}>}}}");
		assertGlobalTableDictionaryContains(extractor, TUPLE_TABLE_KEY, "<Contact Deleted Dt>");
	}

	@Test
	public void lastDeliveredTupleCrossJoinGroupByTest() {
		final String query = CTE + TUPLE_SELECT + TUPLE_FROM + " CROSS JOIN last_delivered_cte" + " GROUP BY ead.<ES Partner ID>, ead.<Contact Deleted Dt>, last_delivered_cte.last_del";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertLastDeliveredOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=contact_deleted_dt, table_ref=log_del}}}, alias=last_del}}, from={table={alias=log_del, schema=PDP_UG, table=log__acs_contact_deletions}}}, alias=last_delivered_cte}}, query={select={1={function={function_name=CAST, data_type={length=64, type=VARCHAR}, type=CAST, value={column={substitution={name=<ES Partner ID>, type=column}, table_ref=ead}}}, alias=es_partner_id}, 2={function={function_name=CAST, data_type={length=50, type=VARCHAR}, type=CAST, value={column={substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}}}, alias=acs_contact_id}}, from={join={1={table={alias=ead, substitution={name=<[Acquia].[exp__acquia_deletions].{fulfillment}>, parts={1=[Acquia], 2=[exp__acquia_deletions], 3={fulfillment}}, type=tuple}}}, 2={join=CROSSJOIN}, 3={table={alias=null, table=last_delivered_cte}}}}, groupby={1={column={substitution={name=<ES Partner ID>, type=column}, table_ref=ead}}, 2={column={substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}}, 3={column={name=last_del, table_ref=last_delivered_cte}}}}}}",
				"[es_partner_id, acs_contact_id]", "{<ACS Contact ID>=column, <[Acquia].[exp__acquia_deletions].{fulfillment}>=tuple, <ES Partner ID>=column, <Contact Deleted Dt>=column}",
				"{pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}, <[Acquia].[exp__acquia_deletions].{fulfillment}>={<ACS Contact ID>=[[@37,202:204='ead',<393>,1:202]], <ES Partner ID>=[[@23,143:145='ead',<393>,1:143], [@57,357:359='ead',<393>,1:357]], <Contact Deleted Dt>=[[@61,378:380='ead',<393>,1:378]]}}",
				"{query0={last_del=[[@12,71:78='last_del',<393>,1:71], [@65,404:421='last_delivered_cte',<393>,1:404]]}, query1={es_partner_id=[[@33,182:194='es_partner_id',<393>,1:182]], acs_contact_id=[[@47,242:255='acs_contact_id',<393>,1:242]]}}",
				"{def_query1={context_list={last_delivered_cte=query0}, query_dictionary={es_partner_id=[[@33,182:194='es_partner_id',<393>,1:182]], acs_contact_id=[[@47,242:255='acs_contact_id',<393>,1:242]]}, table_dictionary={<[Acquia].[exp__acquia_deletions].{fulfillment}>={<ACS Contact ID>=[[@37,202:204='ead',<393>,1:202]], <ES Partner ID>=[[@23,143:145='ead',<393>,1:143], [@57,357:359='ead',<393>,1:357]], <Contact Deleted Dt>=[[@61,378:380='ead',<393>,1:378]]}}, grouped_by=[{substitution={name=<ES Partner ID>, type=column}, table_ref=ead}, {substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}, {name=last_del, table_ref=last_delivered_cte}], def_query0={query_dictionary={last_del=[[@12,71:78='last_del',<393>,1:71], [@65,404:421='last_delivered_cte',<393>,1:404]]}, table_dictionary={pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}}, interface={last_del=[{name=contact_deleted_dt, table_ref=log_del}]}, table_alias={log_del=PDP_UG.log__acs_contact_deletions}}, interface={es_partner_id=[{substitution={name=<ES Partner ID>, type=column}, table_ref=ead}], acs_contact_id=[{substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}]}, table_alias={last_delivered_cte=query0, ead=<[Acquia].[exp__acquia_deletions].{fulfillment}>}}}");
		assertGlobalTableDictionaryContains(extractor, TUPLE_TABLE_KEY,
				"<ES Partner ID>", "<Contact Deleted Dt>");
	}

	@Test
	public void lastDeliveredTupleCrossJoinHavingTest() {
		final String query = CTE + TUPLE_SELECT + TUPLE_FROM + " CROSS JOIN last_delivered_cte" + " GROUP BY ead.<ES Partner ID>, ead.<ACS Contact ID>" + " HAVING MAX(ead.<Contact Deleted Dt>) > MAX(last_delivered_cte.last_del)";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertLastDeliveredOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=contact_deleted_dt, table_ref=log_del}}}, alias=last_del}}, from={table={alias=log_del, schema=PDP_UG, table=log__acs_contact_deletions}}}, alias=last_delivered_cte}}, query={select={1={function={function_name=CAST, data_type={length=64, type=VARCHAR}, type=CAST, value={column={substitution={name=<ES Partner ID>, type=column}, table_ref=ead}}}, alias=es_partner_id}, 2={function={function_name=CAST, data_type={length=50, type=VARCHAR}, type=CAST, value={column={substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}}}, alias=acs_contact_id}}, having={condition={left={function={function_name=MAX, qualifier=null, parameters={column={substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}}}}, right={function={function_name=MAX, qualifier=null, parameters={column={name=last_del, table_ref=last_delivered_cte}}}}, operator=>}}, from={join={1={table={alias=ead, substitution={name=<[Acquia].[exp__acquia_deletions].{fulfillment}>, parts={1=[Acquia], 2=[exp__acquia_deletions], 3={fulfillment}}, type=tuple}}}, 2={join=CROSSJOIN}, 3={table={alias=null, table=last_delivered_cte}}}}, groupby={1={column={substitution={name=<ES Partner ID>, type=column}, table_ref=ead}}, 2={column={substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}}}}}}",
				"[es_partner_id, acs_contact_id]", "{<ACS Contact ID>=column, <[Acquia].[exp__acquia_deletions].{fulfillment}>=tuple, <ES Partner ID>=column, <Contact Deleted Dt>=column}",
				"{pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}, <[Acquia].[exp__acquia_deletions].{fulfillment}>={<ACS Contact ID>=[[@37,202:204='ead',<393>,1:202], [@61,378:380='ead',<393>,1:378]], <ES Partner ID>=[[@23,143:145='ead',<393>,1:143], [@57,357:359='ead',<393>,1:357]], <Contact Deleted Dt>=[[@67,410:412='ead',<393>,1:410]]}}",
				"{query0={last_del=[[@12,71:78='last_del',<393>,1:71], [@74,442:459='last_delivered_cte',<393>,1:442]]}, query1={es_partner_id=[[@33,182:194='es_partner_id',<393>,1:182]], acs_contact_id=[[@47,242:255='acs_contact_id',<393>,1:242]]}}",
				"{def_query1={context_list={last_delivered_cte=query0}, query_dictionary={es_partner_id=[[@33,182:194='es_partner_id',<393>,1:182]], acs_contact_id=[[@47,242:255='acs_contact_id',<393>,1:242]]}, table_dictionary={<[Acquia].[exp__acquia_deletions].{fulfillment}>={<ACS Contact ID>=[[@37,202:204='ead',<393>,1:202], [@61,378:380='ead',<393>,1:378]], <ES Partner ID>=[[@23,143:145='ead',<393>,1:143], [@57,357:359='ead',<393>,1:357]], <Contact Deleted Dt>=[[@67,410:412='ead',<393>,1:410]]}}, grouped_by=[{substitution={name=<ES Partner ID>, type=column}, table_ref=ead}, {substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}], def_query0={query_dictionary={last_del=[[@12,71:78='last_del',<393>,1:71], [@74,442:459='last_delivered_cte',<393>,1:442]]}, table_dictionary={pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}}, interface={last_del=[{name=contact_deleted_dt, table_ref=log_del}]}, table_alias={log_del=PDP_UG.log__acs_contact_deletions}}, filters=[{substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}, {name=last_del, table_ref=last_delivered_cte}], interface={es_partner_id=[{substitution={name=<ES Partner ID>, type=column}, table_ref=ead}], acs_contact_id=[{substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}]}, table_alias={last_delivered_cte=query0, ead=<[Acquia].[exp__acquia_deletions].{fulfillment}>}}}");
		assertGlobalTableDictionaryContains(extractor, TUPLE_TABLE_KEY,
				"<ES Partner ID>", "<Contact Deleted Dt>");
	}

	@Test
	public void lastDeliveredTupleNaturalJoinQualifyTest() {
		final String query = CTE + TUPLE_SELECT + ", ROW_NUMBER() OVER (PARTITION BY ead.<ES Partner ID> ORDER BY ead.<Contact Deleted Dt>) AS rn" + TUPLE_FROM + " NATURAL JOIN last_delivered_cte" + " QUALIFY rn = 1 AND ead.<Contact Deleted Dt> > last_delivered_cte.last_del";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertLastDeliveredOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=contact_deleted_dt, table_ref=log_del}}}, alias=last_del}}, from={table={alias=log_del, schema=PDP_UG, table=log__acs_contact_deletions}}}, alias=last_delivered_cte}}, query={select={1={function={function_name=CAST, data_type={length=64, type=VARCHAR}, type=CAST, value={column={substitution={name=<ES Partner ID>, type=column}, table_ref=ead}}}, alias=es_partner_id}, 2={function={function_name=CAST, data_type={length=50, type=VARCHAR}, type=CAST, value={column={substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}}}, alias=acs_contact_id}, 3={alias=rn, window_function={over={partition_by={1={column={substitution={name=<ES Partner ID>, type=column}, table_ref=ead}}}, orderby={1={null_order=null, predicand={column={substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}}, sort_order=ASC}}}, function={function_name=ROW_NUMBER, parameters=null}}}}, from={join={1={table={alias=ead, substitution={name=<[Acquia].[exp__acquia_deletions].{fulfillment}>, parts={1=[Acquia], 2=[exp__acquia_deletions], 3={fulfillment}}, type=tuple}}}, 2={join=NATURALJOIN}, 3={table={alias=null, table=last_delivered_cte}}}}, qualify={and={1={condition={left={column={name=rn, table_ref=null}}, right={literal=1}, operator==}}, 2={condition={left={column={substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}}, right={column={name=last_del, table_ref=last_delivered_cte}}, operator=>}}}}}}}",
				"[es_partner_id, rn, acs_contact_id]", "{<ACS Contact ID>=column, <[Acquia].[exp__acquia_deletions].{fulfillment}>=tuple, <ES Partner ID>=column, <Contact Deleted Dt>=column}",
				"{pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}, <[Acquia].[exp__acquia_deletions].{fulfillment}>={<ACS Contact ID>=[[@37,202:204='ead',<393>,1:202]], <ES Partner ID>=[[@23,143:145='ead',<393>,1:143], [@56,290:292='ead',<393>,1:290]], <Contact Deleted Dt>=[[@61,319:321='ead',<393>,1:319], [@79,463:465='ead',<393>,1:463]]}}",
				"{query0={last_del=[[@12,71:78='last_del',<393>,1:71], [@83,490:507='last_delivered_cte',<393>,1:490]]}, query1={es_partner_id=[[@33,182:194='es_partner_id',<393>,1:182]], rn=[[@66,348:349='rn',<393>,1:348], [@75,452:453='rn',<393>,1:452]], acs_contact_id=[[@47,242:255='acs_contact_id',<393>,1:242]]}}",
				"{def_query1={context_list={last_delivered_cte=query0}, window_ordered_by=[{substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}], query_dictionary={es_partner_id=[[@33,182:194='es_partner_id',<393>,1:182]], rn=[[@66,348:349='rn',<393>,1:348], [@75,452:453='rn',<393>,1:452]], acs_contact_id=[[@47,242:255='acs_contact_id',<393>,1:242]]}, table_dictionary={<[Acquia].[exp__acquia_deletions].{fulfillment}>={<ACS Contact ID>=[[@37,202:204='ead',<393>,1:202]], <ES Partner ID>=[[@23,143:145='ead',<393>,1:143], [@56,290:292='ead',<393>,1:290]], <Contact Deleted Dt>=[[@61,319:321='ead',<393>,1:319], [@79,463:465='ead',<393>,1:463]]}}, window_partition_by=[{substitution={name=<ES Partner ID>, type=column}, table_ref=ead}], def_query0={query_dictionary={last_del=[[@12,71:78='last_del',<393>,1:71], [@83,490:507='last_delivered_cte',<393>,1:490]]}, table_dictionary={pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}}, interface={last_del=[{name=contact_deleted_dt, table_ref=log_del}]}, table_alias={log_del=PDP_UG.log__acs_contact_deletions}}, filters=[{name=rn, table_ref=query1}, {substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}, {name=last_del, table_ref=last_delivered_cte}], interface={es_partner_id=[{substitution={name=<ES Partner ID>, type=column}, table_ref=ead}], rn=[{substitution={name=<ES Partner ID>, type=column}, table_ref=ead}, {substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}], acs_contact_id=[{substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}]}, table_alias={last_delivered_cte=query0, ead=<[Acquia].[exp__acquia_deletions].{fulfillment}>}}}");
		assertGlobalTableDictionaryContains(extractor, TUPLE_TABLE_KEY,
				"<ES Partner ID>", "<Contact Deleted Dt>");
	}

	@Test
	public void lastDeliveredTupleNaturalLeftOrderByTest() {
		final String query = CTE + TUPLE_SELECT + TUPLE_FROM + " NATURAL LEFT JOIN last_delivered_cte" + " ORDER BY ead.<Contact Deleted Dt>, ead.<ES Partner ID>";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertLastDeliveredOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=contact_deleted_dt, table_ref=log_del}}}, alias=last_del}}, from={table={alias=log_del, schema=PDP_UG, table=log__acs_contact_deletions}}}, alias=last_delivered_cte}}, query={select={1={function={function_name=CAST, data_type={length=64, type=VARCHAR}, type=CAST, value={column={substitution={name=<ES Partner ID>, type=column}, table_ref=ead}}}, alias=es_partner_id}, 2={function={function_name=CAST, data_type={length=50, type=VARCHAR}, type=CAST, value={column={substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}}}, alias=acs_contact_id}}, orderby={1={null_order=null, predicand={column={substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}}, sort_order=ASC}, 2={null_order=null, predicand={column={substitution={name=<ES Partner ID>, type=column}, table_ref=ead}}, sort_order=ASC}}, from={join={1={table={alias=ead, substitution={name=<[Acquia].[exp__acquia_deletions].{fulfillment}>, parts={1=[Acquia], 2=[exp__acquia_deletions], 3={fulfillment}}, type=tuple}}}, 2={join=NATURALJOIN}, 3={table={alias=null, table=last_delivered_cte}}}}}}}",
				"[es_partner_id, acs_contact_id]", "{<ACS Contact ID>=column, <[Acquia].[exp__acquia_deletions].{fulfillment}>=tuple, <ES Partner ID>=column, <Contact Deleted Dt>=column}",
				"{pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}, <[Acquia].[exp__acquia_deletions].{fulfillment}>={<ACS Contact ID>=[[@37,202:204='ead',<393>,1:202]], <ES Partner ID>=[[@23,143:145='ead',<393>,1:143], [@62,390:392='ead',<393>,1:390]], <Contact Deleted Dt>=[[@58,364:366='ead',<393>,1:364]]}}",
				"{query0={last_del=[[@12,71:78='last_del',<393>,1:71]]}, query1={es_partner_id=[[@33,182:194='es_partner_id',<393>,1:182]], acs_contact_id=[[@47,242:255='acs_contact_id',<393>,1:242]]}}",
				"{def_query1={context_list={last_delivered_cte=query0}, query_dictionary={es_partner_id=[[@33,182:194='es_partner_id',<393>,1:182]], acs_contact_id=[[@47,242:255='acs_contact_id',<393>,1:242]]}, table_dictionary={<[Acquia].[exp__acquia_deletions].{fulfillment}>={<ACS Contact ID>=[[@37,202:204='ead',<393>,1:202]], <ES Partner ID>=[[@23,143:145='ead',<393>,1:143], [@62,390:392='ead',<393>,1:390]], <Contact Deleted Dt>=[[@58,364:366='ead',<393>,1:364]]}}, def_query0={query_dictionary={last_del=[[@12,71:78='last_del',<393>,1:71]]}, table_dictionary={pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}}, interface={last_del=[{name=contact_deleted_dt, table_ref=log_del}]}, table_alias={log_del=PDP_UG.log__acs_contact_deletions}}, ordered_by=[{substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}, {substitution={name=<ES Partner ID>, type=column}, table_ref=ead}], interface={es_partner_id=[{substitution={name=<ES Partner ID>, type=column}, table_ref=ead}], acs_contact_id=[{substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}]}, table_alias={last_delivered_cte=query0, ead=<[Acquia].[exp__acquia_deletions].{fulfillment}>}}}");
		assertGlobalTableDictionaryContains(extractor, TUPLE_TABLE_KEY,
				"<ES Partner ID>", "<Contact Deleted Dt>");
	}

	@Test
	public void lastDeliveredTupleNaturalRightScalarSubqueryTest() {
		final String query = CTE + TUPLE_SELECT + TUPLE_FROM + " NATURAL RIGHT JOIN last_delivered_cte" + " WHERE (SELECT MAX(ld2.last_del) FROM last_delivered_cte AS ld2" + " WHERE ld2.last_del > ead.<Contact Deleted Dt>) IS NOT NULL";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertLastDeliveredOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=contact_deleted_dt, table_ref=log_del}}}, alias=last_del}}, from={table={alias=log_del, schema=PDP_UG, table=log__acs_contact_deletions}}}, alias=last_delivered_cte}}, query={select={1={function={function_name=CAST, data_type={length=64, type=VARCHAR}, type=CAST, value={column={substitution={name=<ES Partner ID>, type=column}, table_ref=ead}}}, alias=es_partner_id}, 2={function={function_name=CAST, data_type={length=50, type=VARCHAR}, type=CAST, value={column={substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}}}, alias=acs_contact_id}}, from={join={1={table={alias=ead, substitution={name=<[Acquia].[exp__acquia_deletions].{fulfillment}>, parts={1=[Acquia], 2=[exp__acquia_deletions], 3={fulfillment}}, type=tuple}}}, 2={join=NATURALJOIN}, 3={table={alias=null, table=last_delivered_cte}}}}, where={condition={left={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=last_del, table_ref=ld2}}}}}, from={table={alias=ld2, table=last_delivered_cte}}, where={condition={left={column={name=last_del, table_ref=ld2}}, right={column={substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}}, operator=>}}}, operator=IS NOT NULL}}}}}",
				"[es_partner_id, acs_contact_id]", "{<ACS Contact ID>=column, <[Acquia].[exp__acquia_deletions].{fulfillment}>=tuple, <ES Partner ID>=column, <Contact Deleted Dt>=column}",
				"{pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}, <[Acquia].[exp__acquia_deletions].{fulfillment}>={<ACS Contact ID>=[[@37,202:204='ead',<393>,1:202]], <ES Partner ID>=[[@23,143:145='ead',<393>,1:143]], <Contact Deleted Dt>=[[@74,440:442='ead',<393>,1:440]]}}",
				"{query0={last_del=[[@12,71:78='last_del',<393>,1:71], [@61,374:376='ld2',<393>,1:374], [@70,425:427='ld2',<393>,1:425]]}, query1={unnamed_0=[[@64,386:386=')',<288>,1:386]]}, query3={es_partner_id=[[@33,182:194='es_partner_id',<393>,1:182]], acs_contact_id=[[@47,242:255='acs_contact_id',<393>,1:242]]}}",
				"{def_query3={context_list={last_delivered_cte=query0}, query_dictionary={es_partner_id=[[@33,182:194='es_partner_id',<393>,1:182]], acs_contact_id=[[@47,242:255='acs_contact_id',<393>,1:242]]}, table_dictionary={<[Acquia].[exp__acquia_deletions].{fulfillment}>={<ACS Contact ID>=[[@37,202:204='ead',<393>,1:202]], <ES Partner ID>=[[@23,143:145='ead',<393>,1:143]], <Contact Deleted Dt>=[[@74,440:442='ead',<393>,1:440]]}}, def_query1={context_list={last_delivered_cte=query0, ld2=query0}, query_dictionary={unnamed_0=[[@64,386:386=')',<288>,1:386]]}, filters=[{name=last_del, table_ref=ld2}, {substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}], interface={unnamed_0=[{name=last_del, table_ref=ld2}]}, table_alias={last_delivered_cte=query0, ld2=query0}}, dependent_queries={predicand2={query=query1, type=filters}}, def_query0={query_dictionary={last_del=[[@12,71:78='last_del',<393>,1:71], [@61,374:376='ld2',<393>,1:374], [@70,425:427='ld2',<393>,1:425]]}, table_dictionary={pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}}, interface={last_del=[{name=contact_deleted_dt, table_ref=log_del}]}, table_alias={log_del=PDP_UG.log__acs_contact_deletions}}, filters=[], interface={es_partner_id=[{substitution={name=<ES Partner ID>, type=column}, table_ref=ead}], acs_contact_id=[{substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}]}, table_alias={last_delivered_cte=query0, ead=<[Acquia].[exp__acquia_deletions].{fulfillment}>}}}");
		assertGlobalTableDictionaryContains(extractor, TUPLE_TABLE_KEY,
				"<ES Partner ID>", "<Contact Deleted Dt>");
	}

	@Test
	public void lastDeliveredTupleBareJoinGroupByTest() {
		final String query = CTE + TUPLE_SELECT + TUPLE_FROM + " JOIN last_delivered_cte" + " GROUP BY ead.<ES Partner ID>, ead.<Contact Deleted Dt>, ead.<ACS Contact ID>";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertLastDeliveredOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=contact_deleted_dt, table_ref=log_del}}}, alias=last_del}}, from={table={alias=log_del, schema=PDP_UG, table=log__acs_contact_deletions}}}, alias=last_delivered_cte}}, query={select={1={function={function_name=CAST, data_type={length=64, type=VARCHAR}, type=CAST, value={column={substitution={name=<ES Partner ID>, type=column}, table_ref=ead}}}, alias=es_partner_id}, 2={function={function_name=CAST, data_type={length=50, type=VARCHAR}, type=CAST, value={column={substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}}}, alias=acs_contact_id}}, from={join={1={table={alias=ead, substitution={name=<[Acquia].[exp__acquia_deletions].{fulfillment}>, parts={1=[Acquia], 2=[exp__acquia_deletions], 3={fulfillment}}, type=tuple}}}, 2={join=JOIN}, 3={table={alias=null, table=last_delivered_cte}}}}, groupby={1={column={substitution={name=<ES Partner ID>, type=column}, table_ref=ead}}, 2={column={substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}}, 3={column={substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}}}}}}",
				"[es_partner_id, acs_contact_id]", "{<ACS Contact ID>=column, <[Acquia].[exp__acquia_deletions].{fulfillment}>=tuple, <ES Partner ID>=column, <Contact Deleted Dt>=column}",
				"{pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}, <[Acquia].[exp__acquia_deletions].{fulfillment}>={<ACS Contact ID>=[[@37,202:204='ead',<393>,1:202], [@64,398:400='ead',<393>,1:398]], <ES Partner ID>=[[@23,143:145='ead',<393>,1:143], [@56,351:353='ead',<393>,1:351]], <Contact Deleted Dt>=[[@60,372:374='ead',<393>,1:372]]}}",
				"{query0={last_del=[[@12,71:78='last_del',<393>,1:71]]}, query1={es_partner_id=[[@33,182:194='es_partner_id',<393>,1:182]], acs_contact_id=[[@47,242:255='acs_contact_id',<393>,1:242]]}}",
				"{def_query1={context_list={last_delivered_cte=query0}, query_dictionary={es_partner_id=[[@33,182:194='es_partner_id',<393>,1:182]], acs_contact_id=[[@47,242:255='acs_contact_id',<393>,1:242]]}, table_dictionary={<[Acquia].[exp__acquia_deletions].{fulfillment}>={<ACS Contact ID>=[[@37,202:204='ead',<393>,1:202], [@64,398:400='ead',<393>,1:398]], <ES Partner ID>=[[@23,143:145='ead',<393>,1:143], [@56,351:353='ead',<393>,1:351]], <Contact Deleted Dt>=[[@60,372:374='ead',<393>,1:372]]}}, grouped_by=[{substitution={name=<ES Partner ID>, type=column}, table_ref=ead}, {substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}, {substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}], def_query0={query_dictionary={last_del=[[@12,71:78='last_del',<393>,1:71]]}, table_dictionary={pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}}, interface={last_del=[{name=contact_deleted_dt, table_ref=log_del}]}, table_alias={log_del=PDP_UG.log__acs_contact_deletions}}, interface={es_partner_id=[{substitution={name=<ES Partner ID>, type=column}, table_ref=ead}], acs_contact_id=[{substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}]}, table_alias={last_delivered_cte=query0, ead=<[Acquia].[exp__acquia_deletions].{fulfillment}>}}}");
		assertGlobalTableDictionaryContains(extractor, TUPLE_TABLE_KEY, "<Contact Deleted Dt>", "<ACS Contact ID>");
	}

	@Test
	public void lastDeliveredTupleCommaHavingTest() {
		final String query = CTE + TUPLE_SELECT + TUPLE_FROM + ", last_delivered_cte" + " GROUP BY ead.<ES Partner ID>, ead.<ACS Contact ID>" + " HAVING MAX(ead.<Contact Deleted Dt>) > MAX(last_delivered_cte.last_del)";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertLastDeliveredOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=contact_deleted_dt, table_ref=log_del}}}, alias=last_del}}, from={table={alias=log_del, schema=PDP_UG, table=log__acs_contact_deletions}}}, alias=last_delivered_cte}}, query={select={1={function={function_name=CAST, data_type={length=64, type=VARCHAR}, type=CAST, value={column={substitution={name=<ES Partner ID>, type=column}, table_ref=ead}}}, alias=es_partner_id}, 2={function={function_name=CAST, data_type={length=50, type=VARCHAR}, type=CAST, value={column={substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}}}, alias=acs_contact_id}}, having={condition={left={function={function_name=MAX, qualifier=null, parameters={column={substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}}}}, right={function={function_name=MAX, qualifier=null, parameters={column={name=last_del, table_ref=last_delivered_cte}}}}, operator=>}}, from={join={1={table={alias=ead, substitution={name=<[Acquia].[exp__acquia_deletions].{fulfillment}>, parts={1=[Acquia], 2=[exp__acquia_deletions], 3={fulfillment}}, type=tuple}}}, 2={table={alias=null, table=last_delivered_cte}}}}, groupby={1={column={substitution={name=<ES Partner ID>, type=column}, table_ref=ead}}, 2={column={substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}}}}}}",
				"[es_partner_id, acs_contact_id]", "{<ACS Contact ID>=column, <[Acquia].[exp__acquia_deletions].{fulfillment}>=tuple, <ES Partner ID>=column, <Contact Deleted Dt>=column}",
				"{pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}, <[Acquia].[exp__acquia_deletions].{fulfillment}>={<ACS Contact ID>=[[@37,202:204='ead',<393>,1:202], [@60,368:370='ead',<393>,1:368]], <ES Partner ID>=[[@23,143:145='ead',<393>,1:143], [@56,347:349='ead',<393>,1:347]], <Contact Deleted Dt>=[[@66,400:402='ead',<393>,1:400]]}}",
				"{query0={last_del=[[@12,71:78='last_del',<393>,1:71], [@73,432:449='last_delivered_cte',<393>,1:432]]}, query1={es_partner_id=[[@33,182:194='es_partner_id',<393>,1:182]], acs_contact_id=[[@47,242:255='acs_contact_id',<393>,1:242]]}}",
				"{def_query1={context_list={last_delivered_cte=query0}, query_dictionary={es_partner_id=[[@33,182:194='es_partner_id',<393>,1:182]], acs_contact_id=[[@47,242:255='acs_contact_id',<393>,1:242]]}, table_dictionary={<[Acquia].[exp__acquia_deletions].{fulfillment}>={<ACS Contact ID>=[[@37,202:204='ead',<393>,1:202], [@60,368:370='ead',<393>,1:368]], <ES Partner ID>=[[@23,143:145='ead',<393>,1:143], [@56,347:349='ead',<393>,1:347]], <Contact Deleted Dt>=[[@66,400:402='ead',<393>,1:400]]}}, grouped_by=[{substitution={name=<ES Partner ID>, type=column}, table_ref=ead}, {substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}], def_query0={query_dictionary={last_del=[[@12,71:78='last_del',<393>,1:71], [@73,432:449='last_delivered_cte',<393>,1:432]]}, table_dictionary={pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}}, interface={last_del=[{name=contact_deleted_dt, table_ref=log_del}]}, table_alias={log_del=PDP_UG.log__acs_contact_deletions}}, filters=[{substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}, {name=last_del, table_ref=last_delivered_cte}], interface={es_partner_id=[{substitution={name=<ES Partner ID>, type=column}, table_ref=ead}], acs_contact_id=[{substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}]}, table_alias={last_delivered_cte=query0, ead=<[Acquia].[exp__acquia_deletions].{fulfillment}>}}}");
		assertGlobalTableDictionaryContains(extractor, TUPLE_TABLE_KEY, "<Contact Deleted Dt>");
	}

	@Test
	public void lastDeliveredTupleInnerJoinOnQualifyTest() {
		final String query = CTE + TUPLE_SELECT + ", ROW_NUMBER() OVER (PARTITION BY ead.<ES Partner ID> ORDER BY ead.<Contact Deleted Dt>) AS rn" + TUPLE_FROM + " INNER JOIN last_delivered_cte ON 1 = 1" + " QUALIFY rn = 1 AND ead.<Contact Deleted Dt> > last_delivered_cte.last_del";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertLastDeliveredOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=contact_deleted_dt, table_ref=log_del}}}, alias=last_del}}, from={table={alias=log_del, schema=PDP_UG, table=log__acs_contact_deletions}}}, alias=last_delivered_cte}}, query={select={1={function={function_name=CAST, data_type={length=64, type=VARCHAR}, type=CAST, value={column={substitution={name=<ES Partner ID>, type=column}, table_ref=ead}}}, alias=es_partner_id}, 2={function={function_name=CAST, data_type={length=50, type=VARCHAR}, type=CAST, value={column={substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}}}, alias=acs_contact_id}, 3={alias=rn, window_function={over={partition_by={1={column={substitution={name=<ES Partner ID>, type=column}, table_ref=ead}}}, orderby={1={null_order=null, predicand={column={substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}}, sort_order=ASC}}}, function={function_name=ROW_NUMBER, parameters=null}}}}, from={join={1={table={alias=ead, substitution={name=<[Acquia].[exp__acquia_deletions].{fulfillment}>, parts={1=[Acquia], 2=[exp__acquia_deletions], 3={fulfillment}}, type=tuple}}}, 2={join=INNER, on={condition={left={literal=1}, right={literal=1}, operator==}}}, 3={table={alias=null, table=last_delivered_cte}}}}, qualify={and={1={condition={left={column={name=rn, table_ref=null}}, right={literal=1}, operator==}}, 2={condition={left={column={substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}}, right={column={name=last_del, table_ref=last_delivered_cte}}, operator=>}}}}}}}",
				"[es_partner_id, rn, acs_contact_id]", "{<ACS Contact ID>=column, <[Acquia].[exp__acquia_deletions].{fulfillment}>=tuple, <ES Partner ID>=column, <Contact Deleted Dt>=column}",
				"{pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}, <[Acquia].[exp__acquia_deletions].{fulfillment}>={<ACS Contact ID>=[[@37,202:204='ead',<393>,1:202]], <ES Partner ID>=[[@23,143:145='ead',<393>,1:143], [@56,290:292='ead',<393>,1:290]], <Contact Deleted Dt>=[[@61,319:321='ead',<393>,1:319], [@83,470:472='ead',<393>,1:470]]}}",
				"{query0={last_del=[[@12,71:78='last_del',<393>,1:71], [@87,497:514='last_delivered_cte',<393>,1:497]]}, query1={es_partner_id=[[@33,182:194='es_partner_id',<393>,1:182]], rn=[[@66,348:349='rn',<393>,1:348], [@79,459:460='rn',<393>,1:459]], acs_contact_id=[[@47,242:255='acs_contact_id',<393>,1:242]]}}",
				"{def_query1={context_list={last_delivered_cte=query0}, window_ordered_by=[{substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}], query_dictionary={es_partner_id=[[@33,182:194='es_partner_id',<393>,1:182]], rn=[[@66,348:349='rn',<393>,1:348], [@79,459:460='rn',<393>,1:459]], acs_contact_id=[[@47,242:255='acs_contact_id',<393>,1:242]]}, table_dictionary={<[Acquia].[exp__acquia_deletions].{fulfillment}>={<ACS Contact ID>=[[@37,202:204='ead',<393>,1:202]], <ES Partner ID>=[[@23,143:145='ead',<393>,1:143], [@56,290:292='ead',<393>,1:290]], <Contact Deleted Dt>=[[@61,319:321='ead',<393>,1:319], [@83,470:472='ead',<393>,1:470]]}}, window_partition_by=[{substitution={name=<ES Partner ID>, type=column}, table_ref=ead}], def_query0={query_dictionary={last_del=[[@12,71:78='last_del',<393>,1:71], [@87,497:514='last_delivered_cte',<393>,1:497]]}, table_dictionary={pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}}, interface={last_del=[{name=contact_deleted_dt, table_ref=log_del}]}, table_alias={log_del=PDP_UG.log__acs_contact_deletions}}, filters=[{name=rn, table_ref=query1}, {substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}, {name=last_del, table_ref=last_delivered_cte}], interface={es_partner_id=[{substitution={name=<ES Partner ID>, type=column}, table_ref=ead}], rn=[{substitution={name=<ES Partner ID>, type=column}, table_ref=ead}, {substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}], acs_contact_id=[{substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}]}, table_alias={last_delivered_cte=query0, ead=<[Acquia].[exp__acquia_deletions].{fulfillment}>}}}");
		assertGlobalTableDictionaryContains(extractor, TUPLE_TABLE_KEY, "<Contact Deleted Dt>");
	}
}
