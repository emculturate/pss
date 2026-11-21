package sql.walker;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class SqlEventWalkerCrossScopeColumnCaseTests extends AbstractSqlParseEventWalkerTest {

	private void assertOutputs(
			SqlParseEventWalker extractor,
			String ast,
			String queryInterface,
			String substitutions,
			String tableDictionary,
			String queryDictionary,
			String symbolTable) {
		assertNoWalkerDiagnostics(extractor, Set.of());
		Assert.assertEquals("All diagnostics", 0, extractor.getSnippet().getParserDiagnosticList().size());
		Assert.assertEquals("AST", ast, extractor.getAsTree().toString());
		Assert.assertEquals("Interface", queryInterface, extractor.getInterface().toString());
		Assert.assertEquals("Substitutions", substitutions, extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table dictionary", tableDictionary, extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query dictionary", queryDictionary, extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol table", symbolTable, extractor.getSymbolTable().toString());
	}

	@Test
	public void sameScopeMixedCaseReferencesShareOnePhysicalColumnKeyTest() {
		String query = "select a.SRC_DONOR_ID from <[AMS].[donor].{fulfillment}> as a where a.src_donor_id is not null";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));

		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=SRC_DONOR_ID, table_ref=a}}}, from={table={alias=a, substitution={name=<[AMS].[donor].{fulfillment}>, parts={1=[AMS], 2=[donor], 3={fulfillment}}, type=tuple}}}, where={condition={left={column={name=src_donor_id, table_ref=a}}, operator=is not null}}}}",
				"[SRC_DONOR_ID]",
				"{<[AMS].[donor].{fulfillment}>=tuple}",
				"{<[AMS].[donor].{fulfillment}>={SRC_DONOR_ID=[[@1,7:7='a',<459>,1:7], [@9,68:68='a',<459>,1:68]]}}",
				"{query0={SRC_DONOR_ID=[[@3,9:20='SRC_DONOR_ID',<459>,1:9]]}}",
				"{def_query0={query_dictionary={SRC_DONOR_ID=[[@3,9:20='SRC_DONOR_ID',<459>,1:9]]}, table_dictionary={<[AMS].[donor].{fulfillment}>={SRC_DONOR_ID=[[@1,7:7='a',<459>,1:7], [@9,68:68='a',<459>,1:68]]}}, filters=[{name=src_donor_id, table_ref=a}], interface={SRC_DONOR_ID=[{name=SRC_DONOR_ID, table_ref=a}]}, table_alias={a=<[AMS].[donor].{fulfillment}>}}}");
	}

	@Test
	public void predicateSubqueryMixedCaseReferencesShareOnePhysicalColumnKeyTest() {
		String query = "select a.SRC_DONOR_ID from <[AMS].[donor].{fulfillment}> as a where a.spouse_id in (select d.src_donor_id from <[AMS].[donor].{fulfillment}> as d)";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));

		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=SRC_DONOR_ID, table_ref=a}}}, from={table={alias=a, substitution={name=<[AMS].[donor].{fulfillment}>, parts={1=[AMS], 2=[donor], 3={fulfillment}}, type=tuple}}}, where={in={item={column={name=spouse_id, table_ref=a}}, in_list={select={1={column={name=src_donor_id, table_ref=d}}}, from={table={alias=d, substitution={name=<[AMS].[donor].{fulfillment}>, parts={1=[AMS], 2=[donor], 3={fulfillment}}, type=tuple}}}}}}}}",
				"[SRC_DONOR_ID]",
				"{<[AMS].[donor].{fulfillment}>=tuple}",
				"{<[AMS].[donor].{fulfillment}>={spouse_id=[[@9,68:68='a',<459>,1:68]], src_donor_id=[[@15,91:91='d',<459>,1:91], [@1,7:7='a',<459>,1:7]]}}",
				"{query0={src_donor_id=[[@17,93:104='src_donor_id',<459>,1:93]]}, query2={SRC_DONOR_ID=[[@3,9:20='SRC_DONOR_ID',<459>,1:9]]}}",
				"{def_query2={query_dictionary={SRC_DONOR_ID=[[@3,9:20='SRC_DONOR_ID',<459>,1:9]]}, table_dictionary={<[AMS].[donor].{fulfillment}>={SRC_DONOR_ID=[[@1,7:7='a',<459>,1:7]], spouse_id=[[@9,68:68='a',<459>,1:68]]}}, dependent_queries={in_list1={query=query0, type=filters}}, def_query0={query_dictionary={src_donor_id=[[@17,93:104='src_donor_id',<459>,1:93]]}, table_dictionary={<[AMS].[donor].{fulfillment}>={src_donor_id=[[@15,91:91='d',<459>,1:91], [@1,7:7='a',<459>,1:7]]}}, interface={src_donor_id=[{name=src_donor_id, table_ref=d}]}, table_alias={d=<[AMS].[donor].{fulfillment}>}}, filters=[{name=spouse_id, table_ref=a}], interface={SRC_DONOR_ID=[{name=SRC_DONOR_ID, table_ref=a}]}, table_alias={a=<[AMS].[donor].{fulfillment}>}}}");
	}

	@Test
	public void predicateSubqueryCaseSwappedReferencesShareOnePhysicalColumnKeyTest() {
		String query = "select a.src_donor_id from <[AMS].[donor].{fulfillment}> as a where a.spouse_id in (select d.SRC_DONOR_ID from <[AMS].[donor].{fulfillment}> as d)";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));

		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=src_donor_id, table_ref=a}}}, from={table={alias=a, substitution={name=<[AMS].[donor].{fulfillment}>, parts={1=[AMS], 2=[donor], 3={fulfillment}}, type=tuple}}}, where={in={item={column={name=spouse_id, table_ref=a}}, in_list={select={1={column={name=SRC_DONOR_ID, table_ref=d}}}, from={table={alias=d, substitution={name=<[AMS].[donor].{fulfillment}>, parts={1=[AMS], 2=[donor], 3={fulfillment}}, type=tuple}}}}}}}}",
				"[src_donor_id]",
				"{<[AMS].[donor].{fulfillment}>=tuple}",
				"{<[AMS].[donor].{fulfillment}>={SRC_DONOR_ID=[[@15,91:91='d',<459>,1:91], [@1,7:7='a',<459>,1:7]], spouse_id=[[@9,68:68='a',<459>,1:68]]}}",
				"{query0={SRC_DONOR_ID=[[@17,93:104='SRC_DONOR_ID',<459>,1:93]]}, query2={src_donor_id=[[@3,9:20='src_donor_id',<459>,1:9]]}}",
				"{def_query2={query_dictionary={src_donor_id=[[@3,9:20='src_donor_id',<459>,1:9]]}, table_dictionary={<[AMS].[donor].{fulfillment}>={spouse_id=[[@9,68:68='a',<459>,1:68]], src_donor_id=[[@1,7:7='a',<459>,1:7]]}}, dependent_queries={in_list1={query=query0, type=filters}}, def_query0={query_dictionary={SRC_DONOR_ID=[[@17,93:104='SRC_DONOR_ID',<459>,1:93]]}, table_dictionary={<[AMS].[donor].{fulfillment}>={SRC_DONOR_ID=[[@15,91:91='d',<459>,1:91], [@1,7:7='a',<459>,1:7]]}}, interface={SRC_DONOR_ID=[{name=SRC_DONOR_ID, table_ref=d}]}, table_alias={d=<[AMS].[donor].{fulfillment}>}}, filters=[{name=spouse_id, table_ref=a}], interface={src_donor_id=[{name=src_donor_id, table_ref=a}]}, table_alias={a=<[AMS].[donor].{fulfillment}>}}}");
	}

	@Test
	public void cteCrossScopeMixedCaseReferencesShareOnePhysicalColumnKeyTest() {
		String query = "with inner_source as (select d.src_donor_id from <[AMS].[donor].{fulfillment}> as d) select a.SRC_DONOR_ID from <[AMS].[donor].{fulfillment}> as a cross join inner_source as i";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));

		assertOutputs(
				extractor,
				"{SQL={with={1={cte={select={1={column={name=src_donor_id, table_ref=d}}}, from={table={alias=d, substitution={name=<[AMS].[donor].{fulfillment}>, parts={1=[AMS], 2=[donor], 3={fulfillment}}, type=tuple}}}}, alias=inner_source}}, query={select={1={column={name=SRC_DONOR_ID, table_ref=a}}}, from={join={1={table={alias=a, substitution={name=<[AMS].[donor].{fulfillment}>, parts={1=[AMS], 2=[donor], 3={fulfillment}}, type=tuple}}}, 2={join=crossjoin}, 3={table={alias=i, table=inner_source}}}}}}}",
				"[SRC_DONOR_ID]",
				"{<[AMS].[donor].{fulfillment}>=tuple}",
				"{<[AMS].[donor].{fulfillment}>={src_donor_id=[[@5,29:29='d',<459>,1:29], [@14,92:92='a',<459>,1:92]]}}",
				"{query0={src_donor_id=[[@7,31:42='src_donor_id',<459>,1:31]]}, query1={SRC_DONOR_ID=[[@16,94:105='SRC_DONOR_ID',<459>,1:94]]}}",
				"{def_query1={context_list={inner_source=query0, i=query0}, query_dictionary={SRC_DONOR_ID=[[@16,94:105='SRC_DONOR_ID',<459>,1:94]]}, table_dictionary={<[AMS].[donor].{fulfillment}>={SRC_DONOR_ID=[[@14,92:92='a',<459>,1:92]]}}, def_query0={query_dictionary={src_donor_id=[[@7,31:42='src_donor_id',<459>,1:31]]}, table_dictionary={<[AMS].[donor].{fulfillment}>={src_donor_id=[[@5,29:29='d',<459>,1:29], [@14,92:92='a',<459>,1:92]]}}, interface={src_donor_id=[{name=src_donor_id, table_ref=d}]}, table_alias={d=<[AMS].[donor].{fulfillment}>}}, interface={SRC_DONOR_ID=[{name=SRC_DONOR_ID, table_ref=a}]}, table_alias={a=<[AMS].[donor].{fulfillment}>, inner_source=query0, i=query0}}}");
	}

	@Test
	public void quotedCaseVariantsRemainDistinctAcrossPredicateSubqueryScopesTest() {
		String query = "select a.\"SRC_DONOR_ID\" from <[AMS].[donor].{fulfillment}> as a where a.spouse_id in (select d.\"src_donor_id\" from <[AMS].[donor].{fulfillment}> as d)";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));

		assertOutputs(
				extractor,
				"{SQL={select={1={column={name=\"SRC_DONOR_ID\", table_ref=a}}}, from={table={alias=a, substitution={name=<[AMS].[donor].{fulfillment}>, parts={1=[AMS], 2=[donor], 3={fulfillment}}, type=tuple}}}, where={in={item={column={name=spouse_id, table_ref=a}}, in_list={select={1={column={name=\"src_donor_id\", table_ref=d}}}, from={table={alias=d, substitution={name=<[AMS].[donor].{fulfillment}>, parts={1=[AMS], 2=[donor], 3={fulfillment}}, type=tuple}}}}}}}}",
				"[\"SRC_DONOR_ID\"]",
				"{<[AMS].[donor].{fulfillment}>=tuple}",
				"{<[AMS].[donor].{fulfillment}>={\"SRC_DONOR_ID\"=[[@1,7:7='a',<459>,1:7]], spouse_id=[[@9,70:70='a',<459>,1:70]], \"src_donor_id\"=[[@15,93:93='d',<459>,1:93]]}}",
				"{query0={\"src_donor_id\"=[[@17,95:108='\"src_donor_id\"',<459>,1:95]]}, query2={\"SRC_DONOR_ID\"=[[@3,9:22='\"SRC_DONOR_ID\"',<459>,1:9]]}}",
				"{def_query2={query_dictionary={\"SRC_DONOR_ID\"=[[@3,9:22='\"SRC_DONOR_ID\"',<459>,1:9]]}, table_dictionary={<[AMS].[donor].{fulfillment}>={\"SRC_DONOR_ID\"=[[@1,7:7='a',<459>,1:7]], spouse_id=[[@9,70:70='a',<459>,1:70]]}}, dependent_queries={in_list1={query=query0, type=filters}}, def_query0={query_dictionary={\"src_donor_id\"=[[@17,95:108='\"src_donor_id\"',<459>,1:95]]}, table_dictionary={<[AMS].[donor].{fulfillment}>={\"src_donor_id\"=[[@15,93:93='d',<459>,1:93]]}}, interface={\"src_donor_id\"=[{name=\"src_donor_id\", table_ref=d}]}, table_alias={d=<[AMS].[donor].{fulfillment}>}}, filters=[{name=spouse_id, table_ref=a}], interface={\"SRC_DONOR_ID\"=[{name=\"SRC_DONOR_ID\", table_ref=a}]}, table_alias={a=<[AMS].[donor].{fulfillment}>}}}");
	}

	@Test
	public void productionShapedQueryEndpointSharesOnePhysicalColumnKeyTest() {
		String query = "select a.SRC_DONOR_ID, case when a.head_of_household_primary = 'Yes' and a.spouse_id in (select d.src_donor_id from <[AMS].[donor].{fulfillment}> as d) then false else true end as hh_primary from <[AMS].[donor].{fulfillment}> as a";
		SqlParseEventWalker extractor = runQueryParsertest(query, parse(query));

		assertOutputs(
				extractor,
				"{QUERY={select={1={column={name=SRC_DONOR_ID, table_ref=a}}, 2={alias=hh_primary, case={clauses={1={then={literal=false}, when={and={1={condition={left={column={name=head_of_household_primary, table_ref=a}}, right={literal='Yes'}, operator==}}, 2={in={item={column={name=spouse_id, table_ref=a}}, in_list={select={1={column={name=src_donor_id, table_ref=d}}}, from={table={alias=d, substitution={name=<[AMS].[donor].{fulfillment}>, parts={1=[AMS], 2=[donor], 3={fulfillment}}, type=tuple}}}}}}}}}}, else={literal=true}}}}, from={table={alias=a, substitution={name=<[AMS].[donor].{fulfillment}>, parts={1=[AMS], 2=[donor], 3={fulfillment}}, type=tuple}}}}}",
				"[SRC_DONOR_ID, hh_primary]",
				"{<[AMS].[donor].{fulfillment}>=tuple}",
				"{<[AMS].[donor].{fulfillment}>={spouse_id=[[@13,73:73='a',<459>,1:73]], head_of_household_primary=[[@7,33:33='a',<459>,1:33]], src_donor_id=[[@19,96:96='d',<459>,1:96], [@1,7:7='a',<459>,1:7]]}}",
				"{query0={src_donor_id=[[@21,98:109='src_donor_id',<459>,1:98]]}, query2={hh_primary=[[@33,180:189='hh_primary',<459>,1:180]], SRC_DONOR_ID=[[@3,9:20='SRC_DONOR_ID',<459>,1:9]]}}",
				"{def_query2={query_dictionary={SRC_DONOR_ID=[[@3,9:20='SRC_DONOR_ID',<459>,1:9]], hh_primary=[[@33,180:189='hh_primary',<459>,1:180]]}, table_dictionary={<[AMS].[donor].{fulfillment}>={SRC_DONOR_ID=[[@1,7:7='a',<459>,1:7]], spouse_id=[[@13,73:73='a',<459>,1:73]], head_of_household_primary=[[@7,33:33='a',<459>,1:33]]}}, dependent_queries={in_list1={query=query0, type=interface}}, def_query0={query_dictionary={src_donor_id=[[@21,98:109='src_donor_id',<459>,1:98]]}, table_dictionary={<[AMS].[donor].{fulfillment}>={src_donor_id=[[@19,96:96='d',<459>,1:96], [@1,7:7='a',<459>,1:7]]}}, interface={src_donor_id=[{name=src_donor_id, table_ref=d}]}, table_alias={d=<[AMS].[donor].{fulfillment}>}}, interface={SRC_DONOR_ID=[{name=SRC_DONOR_ID, table_ref=a}], hh_primary=[{name=head_of_household_primary, table_ref=a}, {name=spouse_id, table_ref=a}]}, table_alias={a=<[AMS].[donor].{fulfillment}>}}}");
	}
}
