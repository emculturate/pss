package sql.walker;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

public class SqlEventWalkerQueryVariablePseudoScopeTests extends AbstractSqlParseEventWalkerTest {

	private static final String CODE = "QUERY_VARIABLE_PRESUMED_OUTPUT_COLUMNS";

	@Test
	public void qualifiedReferencesPopulatePseudoQueryInterfaceTest() {
		SqlParseEventWalker extractor = parseQuery(
				"SELECT q.id, q.name FROM (<source_query>) q WHERE q.active = 1");
		assertPresumedOutputDiagnostic(extractor, "<source_query>", "[active, name, id]", 26);
		assertFullGoldens(extractor,
				"{SQL={select={1={column={name=id, table_ref=q}}, 2={column={name=name, table_ref=q}}}, from={table={alias=q, substitution={name=<source_query>, type=query}}}, where={condition={left={column={name=active, table_ref=q}}, right={literal=1}, operator==}}}}",
				"[name, id]",
				"{<source_query>=query}",
				"{}",
				"{query0={name=[[@5,13:13='q',<459>,1:13]], active=[[@14,50:50='q',<459>,1:50]], id=[[@1,7:7='q',<459>,1:7]]}, query1={name=[[@7,15:18='name',<225>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query1={query_dictionary={name=[[@7,15:18='name',<225>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}, def_query0={query_dictionary={name=[[@5,13:13='q',<459>,1:13]], active=[[@14,50:50='q',<459>,1:50]], id=[[@1,7:7='q',<459>,1:7]]}, interface={name=[], active=[], id=[]}}, filters=[{name=active, table_ref=q}], interface={name=[{name=name, table_ref=q}], id=[{name=id, table_ref=q}]}, table_alias={q=query0}}}");
	}

	@Test
	public void unqualifiedReferencesPopulateSolePseudoQueryInterfaceTest() {
		SqlParseEventWalker extractor = parseQuery(
				"SELECT id, name FROM (<source_query>) q WHERE active = 1");
		assertPresumedOutputDiagnostic(extractor, "<source_query>", "[id, name, active]", 22);
		assertFullGoldens(extractor,
				"{SQL={select={1={column={name=id, table_ref=null}}, 2={column={name=name, table_ref=null}}}, from={table={alias=q, substitution={name=<source_query>, type=query}}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[name, id]",
				"{<source_query>=query}",
				"{}",
				"{query0={name=[[@3,11:14='name',<225>,1:11]], active=[[@10,46:51='active',<459>,1:46]], id=[[@1,7:8='id',<459>,1:7]]}, query1={name=[[@3,11:14='name',<225>,1:11]], id=[[@1,7:8='id',<459>,1:7]]}}",
				"{def_query1={query_dictionary={name=[[@3,11:14='name',<225>,1:11]], id=[[@1,7:8='id',<459>,1:7]]}, def_query0={query_dictionary={name=[[@3,11:14='name',<225>,1:11]], active=[[@10,46:51='active',<459>,1:46]], id=[[@1,7:8='id',<459>,1:7]]}, interface={name=[], active=[], id=[]}}, filters=[{name=active, table_ref=query0}], interface={name=[{name=name, table_ref=query0}], id=[{name=id, table_ref=query0}]}, table_alias={q=query0}}}");
	}

	@Test
	public void cteQueryVariablePopulatesPseudoQueryInterfaceTest() {
		SqlParseEventWalker extractor = parseQuery(
				"WITH q AS (<source_query>) SELECT q.id FROM q WHERE q.active = 1");
		assertPresumedOutputDiagnostic(extractor, "<source_query>", "[active, id]", 11);
		assertFullGoldens(extractor,
				"{SQL={with={1={cte={substitution={name=<source_query>, type=query}}, alias=q}}, query={select={1={column={name=id, table_ref=q}}}, from={table={alias=null, table=q}}, where={condition={left={column={name=active, table_ref=q}}, right={literal=1}, operator==}}}}}",
				"[id]",
				"{<source_query>=query}",
				"{}",
				"{query0={active=[[@13,52:52='q',<459>,1:52]], id=[[@7,34:34='q',<459>,1:34]]}, query1={id=[[@9,36:37='id',<459>,1:36]]}}",
				"{def_query1={context_list={q=query0}, query_dictionary={id=[[@9,36:37='id',<459>,1:36]]}, def_query0={query_dictionary={active=[[@13,52:52='q',<459>,1:52]], id=[[@7,34:34='q',<459>,1:34]]}, interface={active=[], id=[]}}, filters=[{name=active, table_ref=q}], interface={id=[{name=id, table_ref=q}]}, table_alias={q=query0}}}");
	}

	@Test
	public void productionQueryVariableRequirementsPopulatePseudoQueryInterfaceTest() {
		SqlParseEventWalker extractor = parseQuery(
				"select partnercontact_id,\n"
				+ "       partner_system_name,\n"
				+ "       eab_entry_year_academic,\n"
				+ "       sum(agg_fund_amount) as agg_fund_amount\n"
				+ "from (\n"
				+ "    <fao_calc.query.total_fund_amount_per_fund_type>\n"
				+ ") fund_agg\n"
				+ "where eab_fund_type = 'pell_grant'\n"
				+ "  and coalesce(imputed_flag, false) = false\n"
				+ "group by partnercontact_id,\n"
				+ "         partner_system_name,\n"
				+ "         eab_entry_year_academic");
		assertPresumedOutputDiagnostic(
				extractor,
				"<fao_calc.query.total_fund_amount_per_fund_type>",
				"[partnercontact_id, partner_system_name, eab_entry_year_academic, agg_fund_amount, imputed_flag, eab_fund_type]",
				6,
				4);
		assertFullGoldens(extractor,
				"{SQL={select={1={column={name=partnercontact_id, table_ref=null}}, 2={column={name=partner_system_name, table_ref=null}}, 3={column={name=eab_entry_year_academic, table_ref=null}}, 4={function={function_name=sum, qualifier=null, parameters={column={name=agg_fund_amount, table_ref=null}}}, alias=agg_fund_amount}}, from={table={alias=fund_agg, substitution={name=<fao_calc.query.total_fund_amount_per_fund_type>, type=query}}}, where={and={1={condition={left={column={name=eab_fund_type, table_ref=null}}, right={literal='pell_grant'}, operator==}}, 2={condition={left={function={parameters={1={column={name=imputed_flag, table_ref=null}}, 2={literal=false}}, function_name=coalesce}}, right={literal=false}, operator==}}}}, groupby={1={column={name=partnercontact_id, table_ref=null}}, 2={column={name=partner_system_name, table_ref=null}}, 3={column={name=eab_entry_year_academic, table_ref=null}}}}}",
				"[partnercontact_id, eab_entry_year_academic, partner_system_name, agg_fund_amount]",
				"{<fao_calc.query.total_fund_amount_per_fund_type>=query}",
				"{}",
				"{query0={imputed_flag=[[@25,254:265='imputed_flag',<459>,9:15]], eab_fund_type=[[@19,210:222='eab_fund_type',<459>,8:6]], partnercontact_id=[[@1,7:23='partnercontact_id',<459>,1:7], [@33,292:308='partnercontact_id',<459>,10:9]], eab_entry_year_academic=[[@5,61:83='eab_entry_year_academic',<459>,3:7], [@37,350:372='eab_entry_year_academic',<459>,12:9]], partner_system_name=[[@3,33:51='partner_system_name',<459>,2:7], [@35,320:338='partner_system_name',<459>,11:9]], agg_fund_amount=[[@9,97:111='agg_fund_amount',<459>,4:11]]}, query1={partnercontact_id=[[@1,7:23='partnercontact_id',<459>,1:7]], eab_entry_year_academic=[[@5,61:83='eab_entry_year_academic',<459>,3:7]], partner_system_name=[[@3,33:51='partner_system_name',<459>,2:7]], agg_fund_amount=[[@12,117:131='agg_fund_amount',<459>,4:31]]}}",
				"{def_query1={query_dictionary={partnercontact_id=[[@1,7:23='partnercontact_id',<459>,1:7]], eab_entry_year_academic=[[@5,61:83='eab_entry_year_academic',<459>,3:7]], partner_system_name=[[@3,33:51='partner_system_name',<459>,2:7]], agg_fund_amount=[[@12,117:131='agg_fund_amount',<459>,4:31]]}, grouped_by=[{name=partnercontact_id, table_ref=query0}, {name=partner_system_name, table_ref=query0}, {name=eab_entry_year_academic, table_ref=query0}], def_query0={query_dictionary={imputed_flag=[[@25,254:265='imputed_flag',<459>,9:15]], eab_fund_type=[[@19,210:222='eab_fund_type',<459>,8:6]], partnercontact_id=[[@1,7:23='partnercontact_id',<459>,1:7], [@33,292:308='partnercontact_id',<459>,10:9]], eab_entry_year_academic=[[@5,61:83='eab_entry_year_academic',<459>,3:7], [@37,350:372='eab_entry_year_academic',<459>,12:9]], partner_system_name=[[@3,33:51='partner_system_name',<459>,2:7], [@35,320:338='partner_system_name',<459>,11:9]], agg_fund_amount=[[@9,97:111='agg_fund_amount',<459>,4:11]]}, interface={imputed_flag=[], eab_fund_type=[], partnercontact_id=[], eab_entry_year_academic=[], partner_system_name=[], agg_fund_amount=[]}}, filters=[{name=eab_fund_type, table_ref=query0}, {name=imputed_flag, table_ref=query0}], interface={partnercontact_id=[{name=partnercontact_id, table_ref=query0}], eab_entry_year_academic=[{name=eab_entry_year_academic, table_ref=query0}], partner_system_name=[{name=partner_system_name, table_ref=query0}], agg_fund_amount=[{name=agg_fund_amount, table_ref=query0}]}, table_alias={fund_agg=query0}}}");
	}

	@Test
	public void ambiguousUnqualifiedReferenceIsNotCreditedToEitherPseudoQueryTest() {
		SqlParseEventWalker extractor = parseQuery(
				"SELECT id, COUNT(score) AS c "
				+ "FROM (<left_query>) l JOIN (<right_query>) r ON join_id = join_id "
				+ "WHERE active = 1 GROUP BY id HAVING COUNT(score) > 0 ORDER BY rank_col");
		Assert.assertEquals(0, extractor.getSnippet().getParserDiagnosticList().stream()
				.filter(diagnostic -> diagnostic != null && CODE.equals(diagnostic.code()))
				.count());
		assertAmbiguousDiagnostics(
				extractor,
				"[query0, query1]",
				new String[] {"id", "score", "rank_col", "join_id", "active", "join_id", "score", "id"},
				new int[] {7, 17, 157, 77, 101, 87, 137, 121});
		assertUnresolvedDiagnostic(
				extractor,
				"Unresolved unqualified column reference(s): [score [(l:1 c:17), (l:1 c:137)], "
				+ "rank_col [(l:1 c:157)], join_id [(l:1 c:77), (l:1 c:87)], "
				+ "active [(l:1 c:101)], id [(l:1 c:7), (l:1 c:121)]]");
		assertFullGoldens(extractor,
				"{SQL={select={1={column={name=id, table_ref=null}}, 2={function={function_name=COUNT, qualifier=null, parameters={column={name=score, table_ref=null}}}, alias=c}}, having={condition={left={function={function_name=COUNT, qualifier=null, parameters={column={name=score, table_ref=null}}}}, right={literal=0}, operator=>}}, orderby={1={null_order=null, predicand={column={name=rank_col, table_ref=null}}, sort_order=ASC}}, from={join={1={table={alias=l, substitution={name=<left_query>, type=query}}}, 2={join=JOIN, on={condition={left={column={name=join_id, table_ref=null}}, right={column={name=join_id, table_ref=null}}, operator==}}}, 3={table={alias=r, substitution={name=<right_query>, type=query}}}}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}, groupby={1={column={name=id, table_ref=null}}}}}",
				"[c, id]",
				"{<right_query>=query, <left_query>=query}",
				"{}",
				"{query0={}, query1={}, query2={c=[[@8,27:27='c',<459>,1:27]], id=[[@1,7:8='id',<459>,1:7]]}}",
				"{def_query2={query_dictionary={c=[[@8,27:27='c',<459>,1:27]], id=[[@1,7:8='id',<459>,1:7]]}, grouped_by=[{name=id, table_ref=null}], def_query1={query_dictionary={}, interface={}}, def_query0={query_dictionary={}, interface={}}, ordered_by=[{name=rank_col, table_ref=null}], filters=[{name=join_id, table_ref=null}, {name=active, table_ref=null}, {name=score, table_ref=null}], interface={c=[{name=score, table_ref=null}], id=[{name=id, table_ref=null}]}, table_alias={l=query0, r=query1}}}");
	}

	@Test
	public void multipleQueryVariablesKeepQualifiedColumnsInTheirOwnPseudoScopesTest() {
		SqlParseEventWalker extractor = parseQuery(
				"SELECT l.id, r.name, "
				+ "ROW_NUMBER() OVER (PARTITION BY l.region_id ORDER BY r.event_time) AS rn "
				+ "FROM (<left_query>) l JOIN (<right_query>) r ON l.join_id = r.join_id "
				+ "WHERE l.active = 1 AND r.status = 'A' ORDER BY r.rank_col");
		assertFullGoldens(extractor,
				"{SQL={select={1={column={name=id, table_ref=l}}, 2={column={name=name, table_ref=r}}, 3={alias=rn, window_function={over={partition_by={1={column={name=region_id, table_ref=l}}}, orderby={1={null_order=null, predicand={column={name=event_time, table_ref=r}}, sort_order=ASC}}}, function={function_name=ROW_NUMBER, parameters=null}}}}, orderby={1={null_order=null, predicand={column={name=rank_col, table_ref=r}}, sort_order=ASC}}, from={join={1={table={alias=l, substitution={name=<left_query>, type=query}}}, 2={join=JOIN, on={condition={left={column={name=join_id, table_ref=l}}, right={column={name=join_id, table_ref=r}}, operator==}}}, 3={table={alias=r, substitution={name=<right_query>, type=query}}}}}, where={and={1={condition={left={column={name=active, table_ref=l}}, right={literal=1}, operator==}}, 2={condition={left={column={name=status, table_ref=r}}, right={literal='A'}, operator==}}}}}}",
				"[name, id, rn]",
				"{<right_query>=query, <left_query>=query}",
				"{}",
				"{query0={region_id=[[@16,53:53='l',<459>,1:53]], join_id=[[@38,142:142='l',<459>,1:142]], active=[[@46,170:170='l',<459>,1:170]], id=[[@1,7:7='l',<459>,1:7]]}, query1={rank_col=[[@59,211:211='r',<459>,1:211]], name=[[@5,13:13='r',<459>,1:13]], join_id=[[@42,154:154='r',<459>,1:154]], event_time=[[@21,74:74='r',<459>,1:74]], status=[[@52,187:187='r',<459>,1:187]]}, query2={name=[[@7,15:18='name',<225>,1:15]], rn=[[@26,91:92='rn',<459>,1:91]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query2={window_ordered_by=[{name=event_time, table_ref=r}], query_dictionary={name=[[@7,15:18='name',<225>,1:15]], id=[[@3,9:10='id',<459>,1:9]], rn=[[@26,91:92='rn',<459>,1:91]]}, window_partition_by=[{name=region_id, table_ref=l}], def_query1={query_dictionary={rank_col=[[@59,211:211='r',<459>,1:211]], name=[[@5,13:13='r',<459>,1:13]], join_id=[[@42,154:154='r',<459>,1:154]], event_time=[[@21,74:74='r',<459>,1:74]], status=[[@52,187:187='r',<459>,1:187]]}, interface={rank_col=[], name=[], join_id=[], event_time=[], status=[]}}, def_query0={query_dictionary={region_id=[[@16,53:53='l',<459>,1:53]], join_id=[[@38,142:142='l',<459>,1:142]], active=[[@46,170:170='l',<459>,1:170]], id=[[@1,7:7='l',<459>,1:7]]}, interface={region_id=[], join_id=[], active=[], id=[]}}, ordered_by=[{name=rank_col, table_ref=r}], filters=[{name=join_id, table_ref=l}, {name=join_id, table_ref=r}, {name=active, table_ref=l}, {name=status, table_ref=r}], interface={name=[{name=name, table_ref=r}], id=[{name=id, table_ref=l}], rn=[{name=region_id, table_ref=l}, {name=event_time, table_ref=r}]}, table_alias={l=query0, r=query1}}}");
	}

	@Test
	public void tupleAndQueryVariableLeaveUnqualifiedColumnsAmbiguousTest() {
		SqlParseEventWalker extractor = parseQuery(
				"SELECT id FROM <physical_tuple> t JOIN (<source_query>) q "
				+ "ON join_id = join_id WHERE active = 1 ORDER BY rank_col");
		assertAmbiguousDiagnostics(
				extractor,
				"[<physical_tuple>, query0]",
				new String[] {"id", "rank_col", "join_id", "active", "join_id"},
				new int[] {7, 105, 61, 85, 71});
		assertUnresolvedDiagnostic(
				extractor,
				"Unresolved unqualified column reference(s): [rank_col [(l:1 c:105)], "
				+ "join_id [(l:1 c:61), (l:1 c:71)], active [(l:1 c:85)], id [(l:1 c:7)]]");
		assertFullGoldens(extractor,
				"{SQL={select={1={column={name=id, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=rank_col, table_ref=null}}, sort_order=ASC}}, from={join={1={table={alias=t, substitution={name=<physical_tuple>, type=tuple}}}, 2={join=JOIN, on={condition={left={column={name=join_id, table_ref=null}}, right={column={name=join_id, table_ref=null}}, operator==}}}, 3={table={alias=q, substitution={name=<source_query>, type=query}}}}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[id]",
				"{<source_query>=query, <physical_tuple>=tuple}",
				"{<physical_tuple>={rank_col=[[@20,105:112='rank_col',<459>,1:105]], join_id=[[@11,61:67='join_id',<459>,1:61], [@13,71:77='join_id',<459>,1:71]], active=[[@15,85:90='active',<459>,1:85]], id=[[@1,7:8='id',<459>,1:7]]}}",
				"{query0={}, query1={id=[[@1,7:8='id',<459>,1:7]]}}",
				"{def_query1={query_dictionary={id=[[@1,7:8='id',<459>,1:7]]}, table_dictionary={<physical_tuple>={rank_col=[[@20,105:112='rank_col',<459>,1:105]], join_id=[[@11,61:67='join_id',<459>,1:61], [@13,71:77='join_id',<459>,1:71]], active=[[@15,85:90='active',<459>,1:85]], id=[[@1,7:8='id',<459>,1:7]]}}, def_query0={query_dictionary={}, interface={}}, ordered_by=[{name=rank_col, table_ref=null}], filters=[{name=join_id, table_ref=null}, {name=active, table_ref=null}], interface={id=[{name=id, table_ref=null}]}, table_alias={q=query0, t=<physical_tuple>}}}");
	}

	@Test
	public void singleQueryVariableCteCollectsUnqualifiedClauseAndWindowColumnsTest() {
		SqlParseEventWalker extractor = parseQuery(
				"WITH src AS (<source_query>) "
				+ "SELECT id, ROW_NUMBER() OVER (PARTITION BY region_id ORDER BY event_time) AS rn "
				+ "FROM src WHERE active = 1 ORDER BY rank_col");
		assertFullGoldens(extractor,
				"{SQL={with={1={cte={substitution={name=<source_query>, type=query}}, alias=src}}, query={select={1={column={name=id, table_ref=null}}, 2={alias=rn, window_function={over={partition_by={1={column={name=region_id, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=event_time, table_ref=null}}, sort_order=ASC}}}, function={function_name=ROW_NUMBER, parameters=null}}}}, orderby={1={null_order=null, predicand={column={name=rank_col, table_ref=null}}, sort_order=ASC}}, from={table={alias=null, table=src}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}}",
				"[id, rn]",
				"{<source_query>=query}",
				"{}",
				"{query0={rank_col=[[@31,144:151='rank_col',<459>,1:144]], region_id=[[@16,72:80='region_id',<459>,1:72]], active=[[@26,124:129='active',<459>,1:124]], id=[[@7,36:37='id',<459>,1:36]], event_time=[[@19,91:100='event_time',<459>,1:91]]}, query1={rn=[[@22,106:107='rn',<459>,1:106]], id=[[@7,36:37='id',<459>,1:36]]}}",
				"{def_query1={context_list={src=query0}, window_ordered_by=[{name=event_time, table_ref=query0}], query_dictionary={id=[[@7,36:37='id',<459>,1:36]], rn=[[@22,106:107='rn',<459>,1:106]]}, window_partition_by=[{name=region_id, table_ref=query0}], def_query0={query_dictionary={rank_col=[[@31,144:151='rank_col',<459>,1:144]], region_id=[[@16,72:80='region_id',<459>,1:72]], active=[[@26,124:129='active',<459>,1:124]], id=[[@7,36:37='id',<459>,1:36]], event_time=[[@19,91:100='event_time',<459>,1:91]]}, interface={rank_col=[], region_id=[], active=[], id=[], event_time=[]}}, ordered_by=[{name=rank_col, table_ref=query0}], filters=[{name=active, table_ref=query0}], interface={id=[{name=id, table_ref=query0}], rn=[{name=region_id, table_ref=query0}, {name=event_time, table_ref=query0}]}, table_alias={src=query0}}}");
	}

	@Test
	public void multipleQueryVariableCtesKeepQualifiedColumnsInTheirOwnPseudoScopesTest() {
		SqlParseEventWalker extractor = parseQuery(
				"WITH left_src AS (<left_query>), right_src AS (<right_query>) "
				+ "SELECT l.id, COUNT(r.amount) AS c "
				+ "FROM left_src l JOIN right_src r ON l.join_id = r.join_id "
				+ "WHERE l.active = 1 GROUP BY l.id HAVING COUNT(r.amount) > 0 ORDER BY r.rank_col");
		assertFullGoldens(extractor,
				"{SQL={with={1={cte={substitution={name=<left_query>, type=query}}, alias=left_src}, 2={cte={substitution={name=<right_query>, type=query}}, alias=right_src}}, query={select={1={column={name=id, table_ref=l}}, 2={function={function_name=COUNT, qualifier=null, parameters={column={name=amount, table_ref=r}}}, alias=c}}, having={condition={left={function={function_name=COUNT, qualifier=null, parameters={column={name=amount, table_ref=r}}}}, right={literal=0}, operator=>}}, orderby={1={null_order=null, predicand={column={name=rank_col, table_ref=r}}, sort_order=ASC}}, from={join={1={table={alias=l, table=left_src}}, 2={join=JOIN, on={condition={left={column={name=join_id, table_ref=l}}, right={column={name=join_id, table_ref=r}}, operator==}}}, 3={table={alias=r, table=right_src}}}}, where={condition={left={column={name=active, table_ref=l}}, right={literal=1}, operator==}}, groupby={1={column={name=id, table_ref=l}}}}}}",
				"[c, id]",
				"{<right_query>=query, <left_query>=query}",
				"{}",
				"{query0={join_id=[[@32,132:132='l',<459>,1:132]], active=[[@40,160:160='l',<459>,1:160]], id=[[@13,69:69='l',<459>,1:69], [@47,182:182='l',<459>,1:182]]}, query1={amount=[[@19,81:81='r',<459>,1:81], [@53,200:200='r',<459>,1:200]], rank_col=[[@61,223:223='r',<459>,1:223]], join_id=[[@36,144:144='r',<459>,1:144]]}, query2={c=[[@24,94:94='c',<459>,1:94]], id=[[@15,71:72='id',<459>,1:71]]}}",
				"{def_query2={context_list={left_src=query0, right_src=query1, l=query0, r=query1}, query_dictionary={c=[[@24,94:94='c',<459>,1:94]], id=[[@15,71:72='id',<459>,1:71]]}, grouped_by=[{name=id, table_ref=l}], def_query1={query_dictionary={amount=[[@19,81:81='r',<459>,1:81], [@53,200:200='r',<459>,1:200]], rank_col=[[@61,223:223='r',<459>,1:223]], join_id=[[@36,144:144='r',<459>,1:144]]}, interface={amount=[], rank_col=[], join_id=[]}}, def_query0={query_dictionary={join_id=[[@32,132:132='l',<459>,1:132]], active=[[@40,160:160='l',<459>,1:160]], id=[[@13,69:69='l',<459>,1:69], [@47,182:182='l',<459>,1:182]]}, interface={join_id=[], active=[], id=[]}}, ordered_by=[{name=rank_col, table_ref=r}], filters=[{name=join_id, table_ref=l}, {name=join_id, table_ref=r}, {name=active, table_ref=l}, {name=amount, table_ref=r}], interface={c=[{name=amount, table_ref=r}], id=[{name=id, table_ref=l}]}, table_alias={r=query1, l=query0, left_src=query0, right_src=query1}}}");
	}

	@Test
	public void multipleQueryVariableCtesLeaveUnqualifiedColumnsAmbiguousTest() {
		SqlParseEventWalker extractor = parseQuery(
				"WITH left_src AS (<left_query>), right_src AS (<right_query>) "
				+ "SELECT id FROM left_src l JOIN right_src r "
				+ "ON join_id = join_id WHERE active = 1");
		assertAmbiguousDiagnostics(
				extractor,
				"[query1, query0]",
				new String[] {"id", "join_id", "active", "join_id"},
				new int[] {69, 108, 132, 118});
		assertUnresolvedDiagnostic(
				extractor,
				"Unresolved unqualified column reference(s): [join_id [(l:1 c:108), (l:1 c:118)], "
				+ "active [(l:1 c:132)], id [(l:1 c:69)]]");
		assertFullGoldens(extractor,
				"{SQL={with={1={cte={substitution={name=<left_query>, type=query}}, alias=left_src}, 2={cte={substitution={name=<right_query>, type=query}}, alias=right_src}}, query={select={1={column={name=id, table_ref=null}}}, from={join={1={table={alias=l, table=left_src}}, 2={join=JOIN, on={condition={left={column={name=join_id, table_ref=null}}, right={column={name=join_id, table_ref=null}}, operator==}}}, 3={table={alias=r, table=right_src}}}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}}",
				"[id]",
				"{<right_query>=query, <left_query>=query}",
				"{}",
				"{query0={}, query1={}, query2={id=[[@13,69:70='id',<459>,1:69]]}}",
				"{def_query2={context_list={left_src=query0, right_src=query1, l=query0, r=query1}, query_dictionary={id=[[@13,69:70='id',<459>,1:69]]}, def_query1={query_dictionary={}, interface={}}, def_query0={query_dictionary={}, interface={}}, filters=[{name=join_id, table_ref=null}, {name=active, table_ref=null}], interface={id=[{name=id, table_ref=null}]}, table_alias={r=query1, l=query0, left_src=query0, right_src=query1}}}");
	}

	@Test
	public void tupleVariableRetainsPhysicalTableDictionaryModelTest() {
		SqlParseEventWalker extractor = parseQuery(
				"SELECT id FROM <source_tuple> q WHERE active = 1");
		Assert.assertEquals(0, extractor.getSnippet().getParserDiagnosticList().stream()
				.filter(diagnostic -> diagnostic != null && CODE.equals(diagnostic.code()))
				.count());
		assertFullGoldens(extractor,
				"{SQL={select={1={column={name=id, table_ref=null}}}, from={table={alias=q, substitution={name=<source_tuple>, type=tuple}}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[id]",
				"{<source_tuple>=tuple}",
				"{<source_tuple>={active=[[@6,38:43='active',<459>,1:38]], id=[[@1,7:8='id',<459>,1:7]]}}",
				"{query0={id=[[@1,7:8='id',<459>,1:7]]}}",
				"{def_query0={query_dictionary={id=[[@1,7:8='id',<459>,1:7]]}, table_dictionary={<source_tuple>={active=[[@6,38:43='active',<459>,1:38]], id=[[@1,7:8='id',<459>,1:7]]}}, filters=[{name=active, table_ref=<source_tuple>}], interface={id=[{name=id, table_ref=<source_tuple>}]}, table_alias={q=<source_tuple>}}}");
	}

	private void assertPresumedOutputDiagnostic(
			SqlParseEventWalker extractor,
			String variableName,
			String columns,
			int character) {
		assertPresumedOutputDiagnostic(extractor, variableName, columns, 1, character);
	}

	private void assertPresumedOutputDiagnostic(
			SqlParseEventWalker extractor,
			String variableName,
			String columns,
			int line,
			int character) {
		List<ParseDiagnostic> diagnostics = extractor.getSnippet().getParserDiagnosticList().stream()
				.filter(diagnostic -> diagnostic != null && CODE.equals(diagnostic.code()))
				.collect(Collectors.toList());
		Assert.assertEquals(1, diagnostics.size());
		ParseDiagnostic diagnostic = diagnostics.get(0);
		Assert.assertEquals(ParseDiagnostic.Severity.SEVERE_WARNING, diagnostic.severity());
		Assert.assertEquals(
				"Columns presumed output of Query Variable " + variableName + " include: " + columns,
				diagnostic.message());
		Assert.assertEquals(Integer.valueOf(line), diagnostic.line());
		Assert.assertEquals(Integer.valueOf(character), diagnostic.charPositionInLine());
		Assert.assertEquals(variableName, diagnostic.tokenText());
	}

	private void assertAmbiguousDiagnostics(
			SqlParseEventWalker extractor,
			String possibleSources,
			String[] columnNames,
			int[] characterPositions) {
		List<ParseDiagnostic> diagnostics = extractor.getSnippet().getParserDiagnosticList().stream()
				.filter(diagnostic -> diagnostic != null
						&& "AMBIGUOUS_COLUMN_REFERENCE".equals(diagnostic.code()))
				.collect(Collectors.toList());
		Assert.assertEquals(columnNames.length, characterPositions.length);
		Assert.assertEquals(columnNames.length, diagnostics.size());
		for (int index = 0; index < columnNames.length; index++) {
			ParseDiagnostic diagnostic = diagnostics.get(index);
			Assert.assertEquals(ParseDiagnostic.Severity.SEVERE_WARNING, diagnostic.severity());
			Assert.assertEquals(
					"Ambiguous column reference '" + columnNames[index] + "' at (l:1 c:"
							+ characterPositions[index] + "). Possible sources: " + possibleSources,
					diagnostic.message());
			Assert.assertEquals(Integer.valueOf(1), diagnostic.line());
			Assert.assertEquals(Integer.valueOf(characterPositions[index]), diagnostic.charPositionInLine());
			Assert.assertEquals(columnNames[index], diagnostic.tokenText());
		}
	}

	private void assertUnresolvedDiagnostic(SqlParseEventWalker extractor, String expectedMessage) {
		List<ParseDiagnostic> diagnostics = extractor.getSnippet().getParserDiagnosticList().stream()
				.filter(diagnostic -> diagnostic != null
						&& "UNRESOLVED_UNQUALIFIED_COLUMNS".equals(diagnostic.code()))
				.collect(Collectors.toList());
		Assert.assertEquals(1, diagnostics.size());
		Assert.assertEquals(ParseDiagnostic.Severity.ERROR, diagnostics.get(0).severity());
		Assert.assertEquals(expectedMessage, diagnostics.get(0).message());
	}

	private SqlParseEventWalker parseQuery(String query) {
		SQLSelectParserParser parser = parse(query);
		return runParsertest(query, parser);
	}

	private void assertFullGoldens(
			SqlParseEventWalker extractor,
			String ast,
			String queryInterface,
			String substitutions,
			String tableDictionary,
			String queryDictionary,
			String symbolTable) {
		Assert.assertEquals("AST is wrong", ast, extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", queryInterface, extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", substitutions, extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", tableDictionary,
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", queryDictionary,
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", symbolTable, extractor.getSymbolTable().toString());
	}
}
