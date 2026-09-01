package sql.walker;

import org.junit.Assert;
import org.junit.Test;

/**
 * Phase 2.7 live reproduction — {@code last_delivered_cte} + tuple {@code ead} CROSS JOIN.
 * <p>
 * Locks the requirement that tuple substitution columns referenced only after the
 * conditionless join (e.g. {@code ead.<Contact Deleted Dt>} inside a {@code WHERE CASE})
 * appear on the tuple source in global {@code tableDictionary}, not only in {@code filters}.
 */
public class SqlEventWalkerLastDeliveredCteTupleSubstitutionTests extends AbstractSqlParseEventWalkerTest {

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
	}

}
