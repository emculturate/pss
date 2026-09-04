package sql.walker;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import sql.SQLSelectParserParser;

/**
 * Phase 2.7 — comprehensive WITH CTE walker characterization (**complete**): conditionless join finalizer
 * ({@code amount_cte} / {@code orders_tbl}), subquery row-source variants, {@code last_delivered_cte}
 * tuple substitution with assorted join types and final-clause column/variable introductions, and
 * window-scope isolation across CTE boundaries.
 * <p>
 * Global {@code tableDictionary} holds physical and tuple substitution sources only; CTE aliases are
 * documented in {@code query_dictionary} and symbol-table {@code def_*} structures, not in the
 * global physical dictionary.
 */
public class SqlEventWalkerWithCteTupleSubstitutionTests extends AbstractSqlParseEventWalkerTest {


	// --- last_delivered_cte tuple substitution fixtures ---


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


	// --- window scope isolation fixtures ---


	private static final String RANKED_JOIN_FROM =
			"SELECT rsc.id, cbsc.contact_key "
					+ "FROM rsc_tab rsc "
					+ "INNER JOIN cbsc_tab cbsc ON rsc.source_id = cbsc.source_id ";

	private static final String WINDOW_OVER =
			"OVER (PARTITION BY contact_key ORDER BY cbsc.priority ASC)";

	private static final String RANKED_ROW_NUMBER_CTE = RANKED_JOIN_FROM
			+ "QUALIFY ROW_NUMBER() " + WINDOW_OVER + " = 1";

	private static final String SIMPLE_FROM = "SELECT id, col1, col2 FROM tab1 ";

	/** CTE body alone vs WITH-wrapped outer query must produce the same fatal count. */
	private void assertCteScopeDoesNotLeakFatals(String cteBody, String outerQuerySuffix) {
		SqlParseEventWalker cteOnly = runParsertest(cteBody, parse(cteBody));
		String fullQuery = "WITH cte AS (" + cteBody + ") " + outerQuerySuffix;
		SqlParseEventWalker full = runParsertest(fullQuery, parse(fullQuery));
		assertNoFatalErrors(cteOnly);
		assertNoFatalErrors(full);
		Assert.assertEquals(
				"Outer query must not add fatals beyond the isolated CTE body",
				fatalCount(cteOnly),
				fatalCount(full));
	}

	private static int fatalCount(SqlParseEventWalker extractor) {
		if (extractor.getSnippet() == null || extractor.getSnippet().getFatalErrorStringList() == null) {
			return 0;
		}
		return extractor.getSnippet().getFatalErrorStringList().size();
	}


	private void assertWalkerGoldenOutputs(SqlParseEventWalker extractor, String expectedAst,
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

	private void assertNaturalFullOuterJoinUnsupportedFatal(String query, int naturalLine, int naturalCharPos) {
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPositionWithFullMessage(
				snippet,
				"NATURAL_FULL_OUTER_JOIN_UNSUPPORTED",
				"NATURAL FULL OUTER JOIN at (l:" + naturalLine + " c:" + naturalCharPos + ") is not supported.",
				"NATURAL FULL OUTER JOIN",
				naturalLine,
				naturalCharPos);
		assertFatalDiagnosticCount(snippet, "NATURAL_FULL_OUTER_JOIN_UNSUPPORTED", null, null, 1);
	}

	// --- last_delivered_cte tuple substitution (join + final-clause) ---

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
		assertWalkerGoldenOutputs(extractor,
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
		assertWalkerGoldenOutputs(extractor,
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
		assertWalkerGoldenOutputs(extractor,
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
		assertWalkerGoldenOutputs(extractor,
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
		assertWalkerGoldenOutputs(extractor,
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
		assertWalkerGoldenOutputs(extractor,
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
		assertWalkerGoldenOutputs(extractor,
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
		assertWalkerGoldenOutputs(extractor,
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
		assertWalkerGoldenOutputs(extractor,
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
		assertWalkerGoldenOutputs(extractor,
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
		assertWalkerGoldenOutputs(extractor,
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
		assertWalkerGoldenOutputs(extractor,
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
		assertWalkerGoldenOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=contact_deleted_dt, table_ref=log_del}}}, alias=last_del}}, from={table={alias=log_del, schema=PDP_UG, table=log__acs_contact_deletions}}}, alias=last_delivered_cte}}, query={select={1={function={function_name=CAST, data_type={length=64, type=VARCHAR}, type=CAST, value={column={substitution={name=<ES Partner ID>, type=column}, table_ref=ead}}}, alias=es_partner_id}, 2={function={function_name=CAST, data_type={length=50, type=VARCHAR}, type=CAST, value={column={substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}}}, alias=acs_contact_id}, 3={alias=rn, window_function={over={partition_by={1={column={substitution={name=<ES Partner ID>, type=column}, table_ref=ead}}}, orderby={1={null_order=null, predicand={column={substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}}, sort_order=ASC}}}, function={function_name=ROW_NUMBER, parameters=null}}}}, from={join={1={table={alias=ead, substitution={name=<[Acquia].[exp__acquia_deletions].{fulfillment}>, parts={1=[Acquia], 2=[exp__acquia_deletions], 3={fulfillment}}, type=tuple}}}, 2={join=INNER, on={condition={left={literal=1}, right={literal=1}, operator==}}}, 3={table={alias=null, table=last_delivered_cte}}}}, qualify={and={1={condition={left={column={name=rn, table_ref=null}}, right={literal=1}, operator==}}, 2={condition={left={column={substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}}, right={column={name=last_del, table_ref=last_delivered_cte}}, operator=>}}}}}}}",
				"[es_partner_id, rn, acs_contact_id]", "{<ACS Contact ID>=column, <[Acquia].[exp__acquia_deletions].{fulfillment}>=tuple, <ES Partner ID>=column, <Contact Deleted Dt>=column}",
				"{pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}, <[Acquia].[exp__acquia_deletions].{fulfillment}>={<ACS Contact ID>=[[@37,202:204='ead',<393>,1:202]], <ES Partner ID>=[[@23,143:145='ead',<393>,1:143], [@56,290:292='ead',<393>,1:290]], <Contact Deleted Dt>=[[@61,319:321='ead',<393>,1:319], [@83,470:472='ead',<393>,1:470]]}}",
				"{query0={last_del=[[@12,71:78='last_del',<393>,1:71], [@87,497:514='last_delivered_cte',<393>,1:497]]}, query1={es_partner_id=[[@33,182:194='es_partner_id',<393>,1:182]], rn=[[@66,348:349='rn',<393>,1:348], [@79,459:460='rn',<393>,1:459]], acs_contact_id=[[@47,242:255='acs_contact_id',<393>,1:242]]}}",
				"{def_query1={context_list={last_delivered_cte=query0}, window_ordered_by=[{substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}], query_dictionary={es_partner_id=[[@33,182:194='es_partner_id',<393>,1:182]], rn=[[@66,348:349='rn',<393>,1:348], [@79,459:460='rn',<393>,1:459]], acs_contact_id=[[@47,242:255='acs_contact_id',<393>,1:242]]}, table_dictionary={<[Acquia].[exp__acquia_deletions].{fulfillment}>={<ACS Contact ID>=[[@37,202:204='ead',<393>,1:202]], <ES Partner ID>=[[@23,143:145='ead',<393>,1:143], [@56,290:292='ead',<393>,1:290]], <Contact Deleted Dt>=[[@61,319:321='ead',<393>,1:319], [@83,470:472='ead',<393>,1:470]]}}, window_partition_by=[{substitution={name=<ES Partner ID>, type=column}, table_ref=ead}], def_query0={query_dictionary={last_del=[[@12,71:78='last_del',<393>,1:71], [@87,497:514='last_delivered_cte',<393>,1:497]]}, table_dictionary={pdp_ug.log__acs_contact_deletions={contact_deleted_dt=[[@7,40:46='log_del',<393>,1:40]]}}, interface={last_del=[{name=contact_deleted_dt, table_ref=log_del}]}, table_alias={log_del=PDP_UG.log__acs_contact_deletions}}, filters=[{name=rn, table_ref=query1}, {substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}, {name=last_del, table_ref=last_delivered_cte}], interface={es_partner_id=[{substitution={name=<ES Partner ID>, type=column}, table_ref=ead}], rn=[{substitution={name=<ES Partner ID>, type=column}, table_ref=ead}, {substitution={name=<Contact Deleted Dt>, type=column}, table_ref=ead}], acs_contact_id=[{substitution={name=<ACS Contact ID>, type=column}, table_ref=ead}]}, table_alias={last_delivered_cte=query0, ead=<[Acquia].[exp__acquia_deletions].{fulfillment}>}}}");
		assertGlobalTableDictionaryContains(extractor, TUPLE_TABLE_KEY, "<Contact Deleted Dt>");
	}

	// --- amount_cte conditionless join finalizer (physical orders_tbl) ---

	@Test
	public void withFinalQueryCteFirstCrossJoinTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT ord.partner_id, ord.contact_id FROM amount_cte CROSS JOIN orders_tbl AS ord "
				+ "WHERE ord.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWalkerGoldenOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=ord}}, 2={column={name=contact_id, table_ref=ord}}}, from={join={1={table={alias=null, table=amount_cte}}, 2={join=CROSSJOIN}, 3={table={alias=ord, table=orders_tbl}}}}, where={condition={left={column={name=order_dt, table_ref=ord}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@34,172:174='ord',<393>,1:172]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@38,187:196='amount_cte',<393>,1:187]]}, query1={contact_id=[[@25,110:119='contact_id',<393>,1:110]], partner_id=[[@21,94:103='partner_id',<393>,1:94]]}}",
				"{def_query1={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,94:103='partner_id',<393>,1:94]], contact_id=[[@25,110:119='contact_id',<393>,1:110]]}, table_dictionary={orders_tbl={order_dt=[[@34,172:174='ord',<393>,1:172]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@38,187:196='amount_cte',<393>,1:187]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=ord}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=ord}], contact_id=[{name=contact_id, table_ref=ord}]}, table_alias={ord=orders_tbl, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryOuterFirstCrossJoinCteTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT ord.partner_id, ord.contact_id FROM orders_tbl AS ord CROSS JOIN amount_cte "
				+ "WHERE ord.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWalkerGoldenOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=ord}}, 2={column={name=contact_id, table_ref=ord}}}, from={join={1={table={alias=ord, table=orders_tbl}}, 2={join=CROSSJOIN}, 3={table={alias=null, table=amount_cte}}}}, where={condition={left={column={name=order_dt, table_ref=ord}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@34,172:174='ord',<393>,1:172]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@38,187:196='amount_cte',<393>,1:187]]}, query1={contact_id=[[@25,110:119='contact_id',<393>,1:110]], partner_id=[[@21,94:103='partner_id',<393>,1:94]]}}",
				"{def_query1={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,94:103='partner_id',<393>,1:94]], contact_id=[[@25,110:119='contact_id',<393>,1:110]]}, table_dictionary={orders_tbl={order_dt=[[@34,172:174='ord',<393>,1:172]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@38,187:196='amount_cte',<393>,1:187]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=ord}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=ord}], contact_id=[{name=contact_id, table_ref=ord}]}, table_alias={ord=orders_tbl, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryCteFirstNaturalJoinTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT ord.partner_id, ord.contact_id FROM amount_cte NATURAL JOIN orders_tbl AS ord "
				+ "WHERE ord.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWalkerGoldenOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=ord}}, 2={column={name=contact_id, table_ref=ord}}}, from={join={1={table={alias=null, table=amount_cte}}, 2={join=NATURALJOIN}, 3={table={alias=ord, table=orders_tbl}}}}, where={condition={left={column={name=order_dt, table_ref=ord}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@34,174:176='ord',<393>,1:174]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@38,189:198='amount_cte',<393>,1:189]]}, query1={contact_id=[[@25,110:119='contact_id',<393>,1:110]], partner_id=[[@21,94:103='partner_id',<393>,1:94]]}}",
				"{def_query1={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,94:103='partner_id',<393>,1:94]], contact_id=[[@25,110:119='contact_id',<393>,1:110]]}, table_dictionary={orders_tbl={order_dt=[[@34,174:176='ord',<393>,1:174]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@38,189:198='amount_cte',<393>,1:189]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=ord}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=ord}], contact_id=[{name=contact_id, table_ref=ord}]}, table_alias={ord=orders_tbl, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryCteFirstNaturalLeftJoinTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT ord.partner_id, ord.contact_id FROM amount_cte NATURAL LEFT JOIN orders_tbl AS ord "
				+ "WHERE ord.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWalkerGoldenOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=ord}}, 2={column={name=contact_id, table_ref=ord}}}, from={join={1={table={alias=null, table=amount_cte}}, 2={join=NATURALJOIN}, 3={table={alias=ord, table=orders_tbl}}}}, where={condition={left={column={name=order_dt, table_ref=ord}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@35,179:181='ord',<393>,1:179]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@39,194:203='amount_cte',<393>,1:194]]}, query1={contact_id=[[@25,110:119='contact_id',<393>,1:110]], partner_id=[[@21,94:103='partner_id',<393>,1:94]]}}",
				"{def_query1={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,94:103='partner_id',<393>,1:94]], contact_id=[[@25,110:119='contact_id',<393>,1:110]]}, table_dictionary={orders_tbl={order_dt=[[@35,179:181='ord',<393>,1:179]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@39,194:203='amount_cte',<393>,1:194]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=ord}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=ord}], contact_id=[{name=contact_id, table_ref=ord}]}, table_alias={ord=orders_tbl, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryCteFirstNaturalRightJoinTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT ord.partner_id, ord.contact_id FROM amount_cte NATURAL RIGHT JOIN orders_tbl AS ord "
				+ "WHERE ord.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWalkerGoldenOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=ord}}, 2={column={name=contact_id, table_ref=ord}}}, from={join={1={table={alias=null, table=amount_cte}}, 2={join=NATURALJOIN}, 3={table={alias=ord, table=orders_tbl}}}}, where={condition={left={column={name=order_dt, table_ref=ord}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@35,180:182='ord',<393>,1:180]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@39,195:204='amount_cte',<393>,1:195]]}, query1={contact_id=[[@25,110:119='contact_id',<393>,1:110]], partner_id=[[@21,94:103='partner_id',<393>,1:94]]}}",
				"{def_query1={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,94:103='partner_id',<393>,1:94]], contact_id=[[@25,110:119='contact_id',<393>,1:110]]}, table_dictionary={orders_tbl={order_dt=[[@35,180:182='ord',<393>,1:180]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@39,195:204='amount_cte',<393>,1:195]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=ord}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=ord}], contact_id=[{name=contact_id, table_ref=ord}]}, table_alias={ord=orders_tbl, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryCteFirstBareJoinTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT ord.partner_id, ord.contact_id FROM amount_cte JOIN orders_tbl AS ord "
				+ "WHERE ord.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWalkerGoldenOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=ord}}, 2={column={name=contact_id, table_ref=ord}}}, from={join={1={table={alias=null, table=amount_cte}}, 2={join=JOIN}, 3={table={alias=ord, table=orders_tbl}}}}, where={condition={left={column={name=order_dt, table_ref=ord}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@33,166:168='ord',<393>,1:166]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@37,181:190='amount_cte',<393>,1:181]]}, query1={contact_id=[[@25,110:119='contact_id',<393>,1:110]], partner_id=[[@21,94:103='partner_id',<393>,1:94]]}}",
				"{def_query1={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,94:103='partner_id',<393>,1:94]], contact_id=[[@25,110:119='contact_id',<393>,1:110]]}, table_dictionary={orders_tbl={order_dt=[[@33,166:168='ord',<393>,1:166]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@37,181:190='amount_cte',<393>,1:181]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=ord}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=ord}], contact_id=[{name=contact_id, table_ref=ord}]}, table_alias={ord=orders_tbl, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryCteFirstCommaJoinTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT ord.partner_id, ord.contact_id FROM amount_cte, orders_tbl AS ord "
				+ "WHERE ord.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWalkerGoldenOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=ord}}, 2={column={name=contact_id, table_ref=ord}}}, from={join={1={table={alias=null, table=amount_cte}}, 2={table={alias=ord, table=orders_tbl}}}}, where={condition={left={column={name=order_dt, table_ref=ord}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@33,162:164='ord',<393>,1:162]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@37,177:186='amount_cte',<393>,1:177]]}, query1={contact_id=[[@25,110:119='contact_id',<393>,1:110]], partner_id=[[@21,94:103='partner_id',<393>,1:94]]}}",
				"{def_query1={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,94:103='partner_id',<393>,1:94]], contact_id=[[@25,110:119='contact_id',<393>,1:110]]}, table_dictionary={orders_tbl={order_dt=[[@33,162:164='ord',<393>,1:162]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@37,177:186='amount_cte',<393>,1:177]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=ord}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=ord}], contact_id=[{name=contact_id, table_ref=ord}]}, table_alias={ord=orders_tbl, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryOuterFirstInnerJoinOnControlTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT ord.partner_id, ord.contact_id FROM orders_tbl AS ord INNER JOIN amount_cte ON 1 = 1 "
				+ "WHERE ord.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWalkerGoldenOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=ord}}, 2={column={name=contact_id, table_ref=ord}}}, from={join={1={table={alias=ord, table=orders_tbl}}, 2={join=INNER, on={condition={left={literal=1}, right={literal=1}, operator==}}}, 3={table={alias=null, table=amount_cte}}}}, where={condition={left={column={name=order_dt, table_ref=ord}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@38,181:183='ord',<393>,1:181]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@42,196:205='amount_cte',<393>,1:196]]}, query1={contact_id=[[@25,110:119='contact_id',<393>,1:110]], partner_id=[[@21,94:103='partner_id',<393>,1:94]]}}",
				"{def_query1={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,94:103='partner_id',<393>,1:94]], contact_id=[[@25,110:119='contact_id',<393>,1:110]]}, table_dictionary={orders_tbl={order_dt=[[@38,181:183='ord',<393>,1:181]], partner_id=[[@19,90:92='ord',<393>,1:90]], contact_id=[[@23,106:108='ord',<393>,1:106]]}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@42,196:205='amount_cte',<393>,1:196]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=ord}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=ord}], contact_id=[{name=contact_id, table_ref=ord}]}, table_alias={ord=orders_tbl, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryCteFirstNaturalFullOuterJoinTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT ord.partner_id, ord.contact_id FROM amount_cte NATURAL FULL OUTER JOIN orders_tbl AS ord "
				+ "WHERE ord.order_dt > amount_cte.max_amount";
		assertNaturalFullOuterJoinUnsupportedFatal(query, 1, 137);
	}

	// --- amount_cte conditionless join finalizer (subquery orders_tbl) ---

	@Test
	public void withFinalQueryCteFirstCrossJoinSubqueryOrdersTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT orders_tbl.partner_id, orders_tbl.contact_id FROM amount_cte CROSS JOIN "
				+ "(SELECT partner_id, contact_id, order_dt FROM orders_tbl) AS orders_tbl "
				+ "WHERE orders_tbl.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWalkerGoldenOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=orders_tbl}}, 2={column={name=contact_id, table_ref=orders_tbl}}}, from={join={1={table={alias=null, table=amount_cte}}, 2={join=CROSSJOIN}, 3={table={alias=orders_tbl, query={select={1={column={name=partner_id, table_ref=null}}, 2={column={name=contact_id, table_ref=null}}, 3={column={name=order_dt, table_ref=null}}}, from={table={alias=null, table=orders_tbl}}}}}}}, where={condition={left={column={name=order_dt, table_ref=orders_tbl}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@36,194:201='order_dt',<393>,1:194]], partner_id=[[@32,170:179='partner_id',<393>,1:170]], contact_id=[[@34,182:191='contact_id',<393>,1:182]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@47,262:271='amount_cte',<393>,1:262]]}, query1={order_dt=[[@36,194:201='order_dt',<393>,1:194], [@43,240:249='orders_tbl',<393>,1:240]], contact_id=[[@34,182:191='contact_id',<393>,1:182], [@23,113:122='orders_tbl',<393>,1:113]], partner_id=[[@32,170:179='partner_id',<393>,1:170], [@19,90:99='orders_tbl',<393>,1:90]]}, query2={contact_id=[[@25,124:133='contact_id',<393>,1:124]], partner_id=[[@21,101:110='partner_id',<393>,1:101]]}}",
				"{def_query2={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,101:110='partner_id',<393>,1:101]], contact_id=[[@25,124:133='contact_id',<393>,1:124]]}, def_query1={context_list={amount_cte=query0}, query_dictionary={order_dt=[[@36,194:201='order_dt',<393>,1:194], [@43,240:249='orders_tbl',<393>,1:240]], partner_id=[[@32,170:179='partner_id',<393>,1:170], [@19,90:99='orders_tbl',<393>,1:90]], contact_id=[[@34,182:191='contact_id',<393>,1:182], [@23,113:122='orders_tbl',<393>,1:113]]}, table_dictionary={orders_tbl={order_dt=[[@36,194:201='order_dt',<393>,1:194]], partner_id=[[@32,170:179='partner_id',<393>,1:170]], contact_id=[[@34,182:191='contact_id',<393>,1:182]]}}, interface={order_dt=[{name=order_dt, table_ref=orders_tbl}], partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={amount_cte=query0}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@47,262:271='amount_cte',<393>,1:262]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=orders_tbl}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={orders_tbl=query1, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryOuterFirstCrossJoinCteSubqueryOrdersTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT orders_tbl.partner_id, orders_tbl.contact_id FROM "
				+ "(SELECT partner_id, contact_id, order_dt FROM orders_tbl) AS orders_tbl CROSS JOIN amount_cte "
				+ "WHERE orders_tbl.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWalkerGoldenOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=orders_tbl}}, 2={column={name=contact_id, table_ref=orders_tbl}}}, from={join={1={table={alias=orders_tbl, query={select={1={column={name=partner_id, table_ref=null}}, 2={column={name=contact_id, table_ref=null}}, 3={column={name=order_dt, table_ref=null}}}, from={table={alias=null, table=orders_tbl}}}}}, 2={join=CROSSJOIN}, 3={table={alias=null, table=amount_cte}}}}, where={condition={left={column={name=order_dt, table_ref=orders_tbl}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@33,172:179='order_dt',<393>,1:172]], partner_id=[[@29,148:157='partner_id',<393>,1:148]], contact_id=[[@31,160:169='contact_id',<393>,1:160]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@47,262:271='amount_cte',<393>,1:262]]}, query1={order_dt=[[@33,172:179='order_dt',<393>,1:172], [@43,240:249='orders_tbl',<393>,1:240]], contact_id=[[@31,160:169='contact_id',<393>,1:160], [@23,113:122='orders_tbl',<393>,1:113]], partner_id=[[@29,148:157='partner_id',<393>,1:148], [@19,90:99='orders_tbl',<393>,1:90]]}, query2={contact_id=[[@25,124:133='contact_id',<393>,1:124]], partner_id=[[@21,101:110='partner_id',<393>,1:101]]}}",
				"{def_query2={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,101:110='partner_id',<393>,1:101]], contact_id=[[@25,124:133='contact_id',<393>,1:124]]}, def_query1={context_list={amount_cte=query0}, query_dictionary={order_dt=[[@33,172:179='order_dt',<393>,1:172], [@43,240:249='orders_tbl',<393>,1:240]], partner_id=[[@29,148:157='partner_id',<393>,1:148], [@19,90:99='orders_tbl',<393>,1:90]], contact_id=[[@31,160:169='contact_id',<393>,1:160], [@23,113:122='orders_tbl',<393>,1:113]]}, table_dictionary={orders_tbl={order_dt=[[@33,172:179='order_dt',<393>,1:172]], partner_id=[[@29,148:157='partner_id',<393>,1:148]], contact_id=[[@31,160:169='contact_id',<393>,1:160]]}}, interface={order_dt=[{name=order_dt, table_ref=orders_tbl}], partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={amount_cte=query0}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@47,262:271='amount_cte',<393>,1:262]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=orders_tbl}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={orders_tbl=query1, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryCteFirstNaturalJoinSubqueryOrdersTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT orders_tbl.partner_id, orders_tbl.contact_id FROM amount_cte NATURAL JOIN "
				+ "(SELECT partner_id, contact_id, order_dt FROM orders_tbl) AS orders_tbl "
				+ "WHERE orders_tbl.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWalkerGoldenOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=orders_tbl}}, 2={column={name=contact_id, table_ref=orders_tbl}}}, from={join={1={table={alias=null, table=amount_cte}}, 2={join=NATURALJOIN}, 3={table={alias=orders_tbl, query={select={1={column={name=partner_id, table_ref=null}}, 2={column={name=contact_id, table_ref=null}}, 3={column={name=order_dt, table_ref=null}}}, from={table={alias=null, table=orders_tbl}}}}}}}, where={condition={left={column={name=order_dt, table_ref=orders_tbl}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@36,196:203='order_dt',<393>,1:196]], partner_id=[[@32,172:181='partner_id',<393>,1:172]], contact_id=[[@34,184:193='contact_id',<393>,1:184]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@47,264:273='amount_cte',<393>,1:264]]}, query1={order_dt=[[@36,196:203='order_dt',<393>,1:196], [@43,242:251='orders_tbl',<393>,1:242]], contact_id=[[@34,184:193='contact_id',<393>,1:184], [@23,113:122='orders_tbl',<393>,1:113]], partner_id=[[@32,172:181='partner_id',<393>,1:172], [@19,90:99='orders_tbl',<393>,1:90]]}, query2={contact_id=[[@25,124:133='contact_id',<393>,1:124]], partner_id=[[@21,101:110='partner_id',<393>,1:101]]}}",
				"{def_query2={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,101:110='partner_id',<393>,1:101]], contact_id=[[@25,124:133='contact_id',<393>,1:124]]}, def_query1={context_list={amount_cte=query0}, query_dictionary={order_dt=[[@36,196:203='order_dt',<393>,1:196], [@43,242:251='orders_tbl',<393>,1:242]], partner_id=[[@32,172:181='partner_id',<393>,1:172], [@19,90:99='orders_tbl',<393>,1:90]], contact_id=[[@34,184:193='contact_id',<393>,1:184], [@23,113:122='orders_tbl',<393>,1:113]]}, table_dictionary={orders_tbl={order_dt=[[@36,196:203='order_dt',<393>,1:196]], partner_id=[[@32,172:181='partner_id',<393>,1:172]], contact_id=[[@34,184:193='contact_id',<393>,1:184]]}}, interface={order_dt=[{name=order_dt, table_ref=orders_tbl}], partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={amount_cte=query0}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@47,264:273='amount_cte',<393>,1:264]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=orders_tbl}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={orders_tbl=query1, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryCteFirstNaturalLeftJoinSubqueryOrdersTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT orders_tbl.partner_id, orders_tbl.contact_id FROM amount_cte NATURAL LEFT JOIN "
				+ "(SELECT partner_id, contact_id, order_dt FROM orders_tbl) AS orders_tbl "
				+ "WHERE orders_tbl.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWalkerGoldenOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=orders_tbl}}, 2={column={name=contact_id, table_ref=orders_tbl}}}, from={join={1={table={alias=null, table=amount_cte}}, 2={join=NATURALJOIN}, 3={table={alias=orders_tbl, query={select={1={column={name=partner_id, table_ref=null}}, 2={column={name=contact_id, table_ref=null}}, 3={column={name=order_dt, table_ref=null}}}, from={table={alias=null, table=orders_tbl}}}}}}}, where={condition={left={column={name=order_dt, table_ref=orders_tbl}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@37,201:208='order_dt',<393>,1:201]], partner_id=[[@33,177:186='partner_id',<393>,1:177]], contact_id=[[@35,189:198='contact_id',<393>,1:189]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@48,269:278='amount_cte',<393>,1:269]]}, query1={order_dt=[[@37,201:208='order_dt',<393>,1:201], [@44,247:256='orders_tbl',<393>,1:247]], contact_id=[[@35,189:198='contact_id',<393>,1:189], [@23,113:122='orders_tbl',<393>,1:113]], partner_id=[[@33,177:186='partner_id',<393>,1:177], [@19,90:99='orders_tbl',<393>,1:90]]}, query2={contact_id=[[@25,124:133='contact_id',<393>,1:124]], partner_id=[[@21,101:110='partner_id',<393>,1:101]]}}",
				"{def_query2={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,101:110='partner_id',<393>,1:101]], contact_id=[[@25,124:133='contact_id',<393>,1:124]]}, def_query1={context_list={amount_cte=query0}, query_dictionary={order_dt=[[@37,201:208='order_dt',<393>,1:201], [@44,247:256='orders_tbl',<393>,1:247]], partner_id=[[@33,177:186='partner_id',<393>,1:177], [@19,90:99='orders_tbl',<393>,1:90]], contact_id=[[@35,189:198='contact_id',<393>,1:189], [@23,113:122='orders_tbl',<393>,1:113]]}, table_dictionary={orders_tbl={order_dt=[[@37,201:208='order_dt',<393>,1:201]], partner_id=[[@33,177:186='partner_id',<393>,1:177]], contact_id=[[@35,189:198='contact_id',<393>,1:189]]}}, interface={order_dt=[{name=order_dt, table_ref=orders_tbl}], partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={amount_cte=query0}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@48,269:278='amount_cte',<393>,1:269]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=orders_tbl}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={orders_tbl=query1, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryCteFirstNaturalRightJoinSubqueryOrdersTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT orders_tbl.partner_id, orders_tbl.contact_id FROM amount_cte NATURAL RIGHT JOIN "
				+ "(SELECT partner_id, contact_id, order_dt FROM orders_tbl) AS orders_tbl "
				+ "WHERE orders_tbl.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWalkerGoldenOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=orders_tbl}}, 2={column={name=contact_id, table_ref=orders_tbl}}}, from={join={1={table={alias=null, table=amount_cte}}, 2={join=NATURALJOIN}, 3={table={alias=orders_tbl, query={select={1={column={name=partner_id, table_ref=null}}, 2={column={name=contact_id, table_ref=null}}, 3={column={name=order_dt, table_ref=null}}}, from={table={alias=null, table=orders_tbl}}}}}}}, where={condition={left={column={name=order_dt, table_ref=orders_tbl}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@37,202:209='order_dt',<393>,1:202]], partner_id=[[@33,178:187='partner_id',<393>,1:178]], contact_id=[[@35,190:199='contact_id',<393>,1:190]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@48,270:279='amount_cte',<393>,1:270]]}, query1={order_dt=[[@37,202:209='order_dt',<393>,1:202], [@44,248:257='orders_tbl',<393>,1:248]], contact_id=[[@35,190:199='contact_id',<393>,1:190], [@23,113:122='orders_tbl',<393>,1:113]], partner_id=[[@33,178:187='partner_id',<393>,1:178], [@19,90:99='orders_tbl',<393>,1:90]]}, query2={contact_id=[[@25,124:133='contact_id',<393>,1:124]], partner_id=[[@21,101:110='partner_id',<393>,1:101]]}}",
				"{def_query2={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,101:110='partner_id',<393>,1:101]], contact_id=[[@25,124:133='contact_id',<393>,1:124]]}, def_query1={context_list={amount_cte=query0}, query_dictionary={order_dt=[[@37,202:209='order_dt',<393>,1:202], [@44,248:257='orders_tbl',<393>,1:248]], partner_id=[[@33,178:187='partner_id',<393>,1:178], [@19,90:99='orders_tbl',<393>,1:90]], contact_id=[[@35,190:199='contact_id',<393>,1:190], [@23,113:122='orders_tbl',<393>,1:113]]}, table_dictionary={orders_tbl={order_dt=[[@37,202:209='order_dt',<393>,1:202]], partner_id=[[@33,178:187='partner_id',<393>,1:178]], contact_id=[[@35,190:199='contact_id',<393>,1:190]]}}, interface={order_dt=[{name=order_dt, table_ref=orders_tbl}], partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={amount_cte=query0}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@48,270:279='amount_cte',<393>,1:270]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=orders_tbl}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={orders_tbl=query1, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryCteFirstBareJoinSubqueryOrdersTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT orders_tbl.partner_id, orders_tbl.contact_id FROM amount_cte JOIN "
				+ "(SELECT partner_id, contact_id, order_dt FROM orders_tbl) AS orders_tbl "
				+ "WHERE orders_tbl.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWalkerGoldenOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=orders_tbl}}, 2={column={name=contact_id, table_ref=orders_tbl}}}, from={join={1={table={alias=null, table=amount_cte}}, 2={join=JOIN}, 3={table={alias=orders_tbl, query={select={1={column={name=partner_id, table_ref=null}}, 2={column={name=contact_id, table_ref=null}}, 3={column={name=order_dt, table_ref=null}}}, from={table={alias=null, table=orders_tbl}}}}}}}, where={condition={left={column={name=order_dt, table_ref=orders_tbl}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@35,188:195='order_dt',<393>,1:188]], partner_id=[[@31,164:173='partner_id',<393>,1:164]], contact_id=[[@33,176:185='contact_id',<393>,1:176]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@46,256:265='amount_cte',<393>,1:256]]}, query1={order_dt=[[@35,188:195='order_dt',<393>,1:188], [@42,234:243='orders_tbl',<393>,1:234]], contact_id=[[@33,176:185='contact_id',<393>,1:176], [@23,113:122='orders_tbl',<393>,1:113]], partner_id=[[@31,164:173='partner_id',<393>,1:164], [@19,90:99='orders_tbl',<393>,1:90]]}, query2={contact_id=[[@25,124:133='contact_id',<393>,1:124]], partner_id=[[@21,101:110='partner_id',<393>,1:101]]}}",
				"{def_query2={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,101:110='partner_id',<393>,1:101]], contact_id=[[@25,124:133='contact_id',<393>,1:124]]}, def_query1={context_list={amount_cte=query0}, query_dictionary={order_dt=[[@35,188:195='order_dt',<393>,1:188], [@42,234:243='orders_tbl',<393>,1:234]], partner_id=[[@31,164:173='partner_id',<393>,1:164], [@19,90:99='orders_tbl',<393>,1:90]], contact_id=[[@33,176:185='contact_id',<393>,1:176], [@23,113:122='orders_tbl',<393>,1:113]]}, table_dictionary={orders_tbl={order_dt=[[@35,188:195='order_dt',<393>,1:188]], partner_id=[[@31,164:173='partner_id',<393>,1:164]], contact_id=[[@33,176:185='contact_id',<393>,1:176]]}}, interface={order_dt=[{name=order_dt, table_ref=orders_tbl}], partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={amount_cte=query0}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@46,256:265='amount_cte',<393>,1:256]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=orders_tbl}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={orders_tbl=query1, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryCteFirstCommaJoinSubqueryOrdersTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT orders_tbl.partner_id, orders_tbl.contact_id FROM amount_cte, "
				+ "(SELECT partner_id, contact_id, order_dt FROM orders_tbl) AS orders_tbl "
				+ "WHERE orders_tbl.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWalkerGoldenOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=orders_tbl}}, 2={column={name=contact_id, table_ref=orders_tbl}}}, from={join={1={table={alias=null, table=amount_cte}}, 2={table={alias=orders_tbl, query={select={1={column={name=partner_id, table_ref=null}}, 2={column={name=contact_id, table_ref=null}}, 3={column={name=order_dt, table_ref=null}}}, from={table={alias=null, table=orders_tbl}}}}}}}, where={condition={left={column={name=order_dt, table_ref=orders_tbl}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@35,184:191='order_dt',<393>,1:184]], partner_id=[[@31,160:169='partner_id',<393>,1:160]], contact_id=[[@33,172:181='contact_id',<393>,1:172]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@46,252:261='amount_cte',<393>,1:252]]}, query1={order_dt=[[@35,184:191='order_dt',<393>,1:184], [@42,230:239='orders_tbl',<393>,1:230]], contact_id=[[@33,172:181='contact_id',<393>,1:172], [@23,113:122='orders_tbl',<393>,1:113]], partner_id=[[@31,160:169='partner_id',<393>,1:160], [@19,90:99='orders_tbl',<393>,1:90]]}, query2={contact_id=[[@25,124:133='contact_id',<393>,1:124]], partner_id=[[@21,101:110='partner_id',<393>,1:101]]}}",
				"{def_query2={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,101:110='partner_id',<393>,1:101]], contact_id=[[@25,124:133='contact_id',<393>,1:124]]}, def_query1={context_list={amount_cte=query0}, query_dictionary={order_dt=[[@35,184:191='order_dt',<393>,1:184], [@42,230:239='orders_tbl',<393>,1:230]], partner_id=[[@31,160:169='partner_id',<393>,1:160], [@19,90:99='orders_tbl',<393>,1:90]], contact_id=[[@33,172:181='contact_id',<393>,1:172], [@23,113:122='orders_tbl',<393>,1:113]]}, table_dictionary={orders_tbl={order_dt=[[@35,184:191='order_dt',<393>,1:184]], partner_id=[[@31,160:169='partner_id',<393>,1:160]], contact_id=[[@33,172:181='contact_id',<393>,1:172]]}}, interface={order_dt=[{name=order_dt, table_ref=orders_tbl}], partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={amount_cte=query0}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@46,252:261='amount_cte',<393>,1:252]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=orders_tbl}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={orders_tbl=query1, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryOuterFirstInnerJoinOnSubqueryOrdersControlTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT orders_tbl.partner_id, orders_tbl.contact_id FROM "
				+ "(SELECT partner_id, contact_id, order_dt FROM orders_tbl) AS orders_tbl INNER JOIN amount_cte ON 1 = 1 "
				+ "WHERE orders_tbl.order_dt > amount_cte.max_amount";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWalkerGoldenOutputs(extractor,
				"{SQL={with={1={cte={select={1={function={function_name=MAX, qualifier=null, parameters={column={name=amount_val, table_ref=bt}}}, alias=max_amount}}, from={table={alias=bt, table=base_table}}}, alias=amount_cte}}, query={select={1={column={name=partner_id, table_ref=orders_tbl}}, 2={column={name=contact_id, table_ref=orders_tbl}}}, from={join={1={table={alias=orders_tbl, query={select={1={column={name=partner_id, table_ref=null}}, 2={column={name=contact_id, table_ref=null}}, 3={column={name=order_dt, table_ref=null}}}, from={table={alias=null, table=orders_tbl}}}}}, 2={join=INNER, on={condition={left={literal=1}, right={literal=1}, operator==}}}, 3={table={alias=null, table=amount_cte}}}}, where={condition={left={column={name=order_dt, table_ref=orders_tbl}}, right={column={name=max_amount, table_ref=amount_cte}}, operator=>}}}}}",
				"[partner_id, contact_id]", "{}",
				"{orders_tbl={order_dt=[[@33,172:179='order_dt',<393>,1:172]], partner_id=[[@29,148:157='partner_id',<393>,1:148]], contact_id=[[@31,160:169='contact_id',<393>,1:160]]}, base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}",
				"{query0={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@51,271:280='amount_cte',<393>,1:271]]}, query1={order_dt=[[@33,172:179='order_dt',<393>,1:172], [@47,249:258='orders_tbl',<393>,1:249]], contact_id=[[@31,160:169='contact_id',<393>,1:160], [@23,113:122='orders_tbl',<393>,1:113]], partner_id=[[@29,148:157='partner_id',<393>,1:148], [@19,90:99='orders_tbl',<393>,1:90]]}, query2={contact_id=[[@25,124:133='contact_id',<393>,1:124]], partner_id=[[@21,101:110='partner_id',<393>,1:101]]}}",
				"{def_query2={context_list={amount_cte=query0}, query_dictionary={partner_id=[[@21,101:110='partner_id',<393>,1:101]], contact_id=[[@25,124:133='contact_id',<393>,1:124]]}, def_query1={context_list={amount_cte=query0}, query_dictionary={order_dt=[[@33,172:179='order_dt',<393>,1:172], [@47,249:258='orders_tbl',<393>,1:249]], partner_id=[[@29,148:157='partner_id',<393>,1:148], [@19,90:99='orders_tbl',<393>,1:90]], contact_id=[[@31,160:169='contact_id',<393>,1:160], [@23,113:122='orders_tbl',<393>,1:113]]}, table_dictionary={orders_tbl={order_dt=[[@33,172:179='order_dt',<393>,1:172]], partner_id=[[@29,148:157='partner_id',<393>,1:148]], contact_id=[[@31,160:169='contact_id',<393>,1:160]]}}, interface={order_dt=[{name=order_dt, table_ref=orders_tbl}], partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={amount_cte=query0}}, def_query0={query_dictionary={max_amount=[[@12,49:58='max_amount',<393>,1:49], [@51,271:280='amount_cte',<393>,1:271]]}, table_dictionary={base_table={amount_val=[[@7,31:32='bt',<393>,1:31]]}}, interface={max_amount=[{name=amount_val, table_ref=bt}]}, table_alias={bt=base_table}}, filters=[{name=order_dt, table_ref=orders_tbl}, {name=max_amount, table_ref=amount_cte}], interface={partner_id=[{name=partner_id, table_ref=orders_tbl}], contact_id=[{name=contact_id, table_ref=orders_tbl}]}, table_alias={orders_tbl=query1, amount_cte=query0}}}");
	}

	@Test
	public void withFinalQueryCteFirstNaturalFullOuterJoinSubqueryOrdersTest() {
		final String query = "WITH amount_cte AS (SELECT MAX(bt.amount_val) AS max_amount FROM base_table AS bt) "
				+ "SELECT orders_tbl.partner_id, orders_tbl.contact_id FROM amount_cte NATURAL FULL OUTER JOIN "
				+ "(SELECT partner_id, contact_id, order_dt FROM orders_tbl) AS orders_tbl "
				+ "WHERE orders_tbl.order_dt > amount_cte.max_amount";
		assertNaturalFullOuterJoinUnsupportedFatal(query, 1, 151);
	}

	// --- window scope isolation across CTE boundaries ---

	// --- QUALIFY + assorted window functions (join body with partition/order refs) ---

	@Test
	public void qualifyRowNumberPartitionOrderCteScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(RANKED_ROW_NUMBER_CTE, "SELECT contact_key FROM cte");
	}

	@Test
	public void qualifyRankPartitionOrderCteScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				RANKED_JOIN_FROM + "QUALIFY RANK() " + WINDOW_OVER + " = 1",
				"SELECT contact_key FROM cte");
	}

	@Test
	public void qualifyDenseRankPartitionOrderCteScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				RANKED_JOIN_FROM + "QUALIFY DENSE_RANK() " + WINDOW_OVER + " = 1",
				"SELECT contact_key FROM cte");
	}

	@Test
	public void qualifyPercentRankPartitionOrderCteScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				RANKED_JOIN_FROM + "QUALIFY PERCENT_RANK() " + WINDOW_OVER + " = 1",
				"SELECT contact_key FROM cte");
	}

	@Test
	public void qualifySumOverPartitionOrderCteScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				RANKED_JOIN_FROM + "QUALIFY SUM(rsc.id) " + WINDOW_OVER + " > 0",
				"SELECT contact_key FROM cte");
	}

	@Test
	public void qualifyRowNumberJoinPartnerScopeV0Test() {
		String full =
				"SELECT a.contact_key FROM (" + RANKED_ROW_NUMBER_CTE + ") a "
						+ "JOIN plain_tab p ON a.id = p.id";
		SqlParseEventWalker cteOnly = runParsertest(RANKED_ROW_NUMBER_CTE, parse(RANKED_ROW_NUMBER_CTE));
		SqlParseEventWalker fullWalker = runParsertest(full, parse(full));
		assertNoFatalErrors(cteOnly);
		assertNoFatalErrors(fullWalker);
	}

	@Test
	public void qualifyRowNumberTwoCteFinalJoinScopeV0Test() {
		String full =
				"WITH ranked AS (" + RANKED_ROW_NUMBER_CTE + "), "
						+ "plain AS (SELECT id, label FROM plain_tab) "
						+ "SELECT r.contact_key, p.label FROM ranked r JOIN plain p ON r.id = p.id";
		assertNoFatalErrors(runParsertest(full, parse(full)));
	}

	// --- Simple tab1 QUALIFY variants (single-table window refs) ---

	@Test
	public void qualifyRankSimpleTableCteScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				SIMPLE_FROM + "QUALIFY RANK() OVER (PARTITION BY col1 ORDER BY col2) = 1",
				"SELECT col1 FROM cte");
	}

	@Test
	public void qualifyLagSimpleTableCteScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				SIMPLE_FROM + "QUALIFY LAG(col2, 1) OVER (PARTITION BY col1 ORDER BY col2) IS NOT NULL",
				"SELECT col1 FROM cte");
	}

	// --- CTE bodies ending with each trailing clause type (non-window control cases) ---

	@Test
	public void cteEndingWhereScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				SIMPLE_FROM + "WHERE col1 > 0",
				"SELECT col1 FROM cte");
	}

	@Test
	public void cteEndingGroupByScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				"SELECT col1, SUM(col2) AS total FROM tab1 GROUP BY col1",
				"SELECT col1 FROM cte");
	}

	@Test
	public void cteEndingHavingScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				"SELECT col1, SUM(col2) AS total FROM tab1 GROUP BY col1 HAVING SUM(col2) > 0",
				"SELECT col1 FROM cte");
	}

	@Test
	public void cteEndingOrderByScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				SIMPLE_FROM + "ORDER BY col1, col2",
				"SELECT col1 FROM cte");
	}

	@Test
	public void cteEndingLimitScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				SIMPLE_FROM + "LIMIT 10",
				"SELECT id FROM cte");
	}

	// --- Post-SELECT clauses that embed OVER (same latch risk as QUALIFY) ---

	@Test
	public void cteEndingHavingWithWindowScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				"SELECT col1, SUM(col2) AS total FROM tab1 "
						+ "GROUP BY col1 "
						+ "HAVING ROW_NUMBER() OVER (PARTITION BY col1 ORDER BY col2) = 1",
				"SELECT col1 FROM cte");
	}

	@Test
	public void cteEndingOrderByWithWindowScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				SIMPLE_FROM + "ORDER BY ROW_NUMBER() OVER (PARTITION BY col1 ORDER BY col2)",
				"SELECT col1 FROM cte");
	}

	@Test
	public void cteEndingQualifyRankScopeV0Test() {
		assertCteScopeDoesNotLeakFatals(
				SIMPLE_FROM + "QUALIFY RANK() OVER (PARTITION BY col1 ORDER BY col2) <= 1",
				"SELECT col1 FROM cte");
	}

	// --- Published outer-interface lineage must not inherit QUALIFY OVER partition/order deps ---

	@Test
	public void qualifyRowNumberCteOuterInterfaceLineageV0Test() {
		String query =
				"WITH ranked AS (SELECT id, col1, col2 FROM tab1 "
						+ "QUALIFY ROW_NUMBER() OVER (PARTITION BY col1 ORDER BY col2) = 1) "
						+ "SELECT col1 FROM ranked";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={select={1={column={name=id, table_ref=null}}, 2={column={name=col1, table_ref=null}}, 3={column={name=col2, table_ref=null}}}, from={table={alias=null, table=tab1}}, qualify={condition={left={window_function={over={partition_by={1={column={name=col1, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=col2, table_ref=null}}, sort_order=ASC}}}, function={function_name=ROW_NUMBER, parameters=null}}}, right={literal=1}, operator==}}}, alias=ranked}}, query={select={1={column={name=col1, table_ref=null}}}, from={table={alias=null, table=ranked}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={id=[[@5,23:24='id',<393>,1:23]], col2=[[@9,33:36='col2',<393>,1:33], [@23,102:105='col2',<393>,1:102]], col1=[[@7,27:30='col1',<393>,1:27], [@20,88:91='col1',<393>,1:88]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={col2=[[@9,33:36='col2',<393>,1:33]], id=[[@5,23:24='id',<393>,1:23]], col1=[[@7,27:30='col1',<393>,1:27], [@29,120:123='col1',<393>,1:120]]}, query1={col1=[[@29,120:123='col1',<393>,1:120]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={context_list={ranked=query0}, query_dictionary={col1=[[@29,120:123='col1',<393>,1:120]]}, def_query0={window_ordered_by=[{name=col2, table_ref=tab1}], query_dictionary={id=[[@5,23:24='id',<393>,1:23]], col2=[[@9,33:36='col2',<393>,1:33]], col1=[[@7,27:30='col1',<393>,1:27], [@29,120:123='col1',<393>,1:120]]}, table_dictionary={tab1={id=[[@5,23:24='id',<393>,1:23]], col2=[[@9,33:36='col2',<393>,1:33], [@23,102:105='col2',<393>,1:102]], col1=[[@7,27:30='col1',<393>,1:27], [@20,88:91='col1',<393>,1:88]]}}, window_partition_by=[{name=col1, table_ref=tab1}], filters=[{name=col1, table_ref=tab1}, {name=col2, table_ref=tab1}], interface={id=[{name=id, table_ref=tab1}], col2=[{name=col2, table_ref=tab1}], col1=[{name=col1, table_ref=tab1}]}}, interface={col1=[{name=col1, table_ref=query0}]}, table_alias={ranked=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void qualifyRowNumberSubstitutionCteOuterInterfaceLineageV0Test() {
		String query = "with wrapped as ( "
				+ " select cec.<select column> "
				+ "from <[Enrollment Services].[Client Entering Class]> cec "
				+ "qualify row_number() over (partition by cec.non_variable_col, cec.<where column> order by cec.non_variable_col) = 1 "
				+ ") "
				+ "select cec.<select column> from wrapped cec";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}}, from={table={alias=cec, substitution={name=<[Enrollment Services].[Client Entering Class]>, parts={1=[Enrollment Services], 2=[Client Entering Class]}, type=tuple}}}, qualify={condition={left={window_function={over={partition_by={1={column={name=non_variable_col, table_ref=cec}}, 2={column={substitution={name=<where column>, type=column}, table_ref=cec}}}, orderby={1={null_order=null, predicand={column={name=non_variable_col, table_ref=cec}}, sort_order=ASC}}}, function={function_name=row_number, parameters=null}}}, right={literal=1}, operator==}}}, alias=wrapped}}, query={select={1={column={substitution={name=<select column>, type=column}, table_ref=cec}}}, from={table={alias=cec, table=wrapped}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[<select column>]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong",
				"{<[Enrollment Services].[Client Entering Class]>=tuple, <select column>=column, <where column>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Enrollment Services].[Client Entering Class]>={<select column>=[[@5,26:28='cec',<393>,1:26]], non_variable_col=[[@19,143:145='cec',<393>,1:143], [@28,193:195='cec',<393>,1:193]], <where column>=[[@23,165:167='cec',<393>,1:165]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={<select column>=[[@7,30:44='<select column>',<327>,1:30], [@36,228:230='cec',<393>,1:228]]}, query1={<select column>=[[@38,232:246='<select column>',<327>,1:232]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={context_list={wrapped=query0, cec=query0}, query_dictionary={<select column>=[[@38,232:246='<select column>',<327>,1:232]]}, def_query0={window_ordered_by=[{name=non_variable_col, table_ref=cec}], query_dictionary={<select column>=[[@7,30:44='<select column>',<327>,1:30], [@36,228:230='cec',<393>,1:228]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@5,26:28='cec',<393>,1:26]], non_variable_col=[[@19,143:145='cec',<393>,1:143], [@28,193:195='cec',<393>,1:193]], <where column>=[[@23,165:167='cec',<393>,1:165]]}}, window_partition_by=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], filters=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}, interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=query0, wrapped=query0}}}",
				extractor.getSymbolTable().toString());
	}
}
