package sql.walker;

import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.tree.Trees;
import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import access.SqlParserAccess;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

public class SqlEventWalkerContextualKeywordTests extends AbstractSqlParseEventWalkerTest {

	private String lastQuery;

	private SqlParseEventWalker runQuery(String query) {
		lastQuery = query;
		return runParsertest(query, parse(query));
	}

	private SqlParseEventWalker runDedicatedFunctionQuery(String query, int rule) {
		lastQuery = query;
		SQLSelectParserParser parser = parse(query);
		SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Unexpected keyword routing", 1, Trees.findAllRuleNodes(tree, rule).size());
		return runAnyParsertest(query, parser, tree, true);
	}

	private void assertExactDiagnostics(SqlParseEventWalker extractor, ParseDiagnostic... expected) {
		Assert.assertEquals("Diagnostics are wrong", Arrays.asList(expected),
				extractor.getSnippet().getParserDiagnosticList());
	}

	private void assertOutputs(SqlParseEventWalker extractor, String... expected) {
		List<String> goldens = Arrays.asList(expected);
		Assert.assertEquals("Walker outputs are wrong", goldens, List.of(
				extractor.getAsTree().toString(), extractor.getInterface().toString(),
				extractor.getSubstitutionsMap().toString(), extractor.getTableColumnDictionaryMap().toString(),
				extractor.getQueryColumnDictionaryMap().toString(), extractor.getSymbolTable().toString()));

		SqlParserAccess access = new SqlParserAccess(false, false, false);
		access.executeTheParse(lastQuery, "SQL");
		Assert.assertEquals("Access syntax errors", 0, access.getParser().getNumberOfSyntaxErrors());
		Snippet snippet = access.getSnippet();
		Assert.assertEquals("Access diagnostics are wrong", extractor.getSnippet().getParserDiagnosticList(),
				snippet.getParserDiagnosticList());
		Assert.assertEquals("Access outputs are wrong", goldens, List.of(
				snippet.getSqlAbstractTree().toString(), snippet.getQueryInterface().toString(),
				snippet.getSubstitutionsMap().toString(), snippet.getTableDictionary().toString(),
				snippet.getQueryColumnDictionaryMap().toString(), snippet.getSymbolTable().toString()));
	}

	@Test
	public void contextualKeywordQUALIFYBareColumnV0Test() {
		final String query = "select qualify as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=qualify, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@9,53:58='active',<459>,1:53]], id=[[@5,32:33='id',<459>,1:32]], qualify=[[@1,7:13='qualify',<330>,1:7]]}}",
				"{query0={result_value=[[@3,18:29='result_value',<459>,1:18]], id=[[@5,32:33='id',<459>,1:32]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,18:29='result_value',<459>,1:18]], id=[[@5,32:33='id',<459>,1:32]]}, table_dictionary={events={active=[[@9,53:58='active',<459>,1:53]], id=[[@5,32:33='id',<459>,1:32]], qualify=[[@1,7:13='qualify',<330>,1:7]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=qualify, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordQUALIFYQualifiedColumnV0Test() {
		final String query = "select a.qualify as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=qualify, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,34:34='b',<459>,1:34]], id=[[@21,90:90='b',<459>,1:90]], enabled=[[@31,118:118='b',<459>,1:118]]}, events={account_id=[[@17,75:75='a',<459>,1:75]], active=[[@25,101:101='a',<459>,1:101]], qualify=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@5,20:31='result_value',<459>,1:20]], label=[[@9,36:40='label',<459>,1:36]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,20:31='result_value',<459>,1:20]], label=[[@9,36:40='label',<459>,1:36]]}, table_dictionary={accounts={label=[[@7,34:34='b',<459>,1:34]], id=[[@21,90:90='b',<459>,1:90]], enabled=[[@31,118:118='b',<459>,1:118]]}, events={account_id=[[@17,75:75='a',<459>,1:75]], active=[[@25,101:101='a',<459>,1:101]], qualify=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=qualify, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordQUALIFYExplicitColumnAliasV0Test() {
		final String query = "select a.id as qualify, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=qualify}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[label, qualify]",
				"{}",
				"{accounts={label=[[@7,24:24='b',<459>,1:24]], id=[[@21,80:80='b',<459>,1:80]], enabled=[[@31,108:108='b',<459>,1:108]]}, events={account_id=[[@17,65:65='a',<459>,1:65]], active=[[@25,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={qualify=[[@5,15:21='qualify',<330>,1:15]], label=[[@9,26:30='label',<459>,1:26]]}}",
				"{def_query0={query_dictionary={label=[[@9,26:30='label',<459>,1:26]], qualify=[[@5,15:21='qualify',<330>,1:15]]}, table_dictionary={accounts={label=[[@7,24:24='b',<459>,1:24]], id=[[@21,80:80='b',<459>,1:80]], enabled=[[@31,108:108='b',<459>,1:108]]}, events={account_id=[[@17,65:65='a',<459>,1:65]], active=[[@25,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={label=[{name=label, table_ref=b}], qualify=[{name=id, table_ref=a}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordQUALIFYImplicitColumnAliasV0Test() {
		final String query = "select a.id qualify, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=qualify}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[label, qualify]",
				"{}",
				"{accounts={label=[[@6,21:21='b',<459>,1:21]], id=[[@20,77:77='b',<459>,1:77]], enabled=[[@30,105:105='b',<459>,1:105]]}, events={account_id=[[@16,62:62='a',<459>,1:62]], active=[[@24,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={qualify=[[@4,12:18='qualify',<330>,1:12]], label=[[@8,23:27='label',<459>,1:23]]}}",
				"{def_query0={query_dictionary={label=[[@8,23:27='label',<459>,1:23]], qualify=[[@4,12:18='qualify',<330>,1:12]]}, table_dictionary={accounts={label=[[@6,21:21='b',<459>,1:21]], id=[[@20,77:77='b',<459>,1:77]], enabled=[[@30,105:105='b',<459>,1:105]]}, events={account_id=[[@16,62:62='a',<459>,1:62]], active=[[@24,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={label=[{name=label, table_ref=b}], qualify=[{name=id, table_ref=a}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordQUALIFYPhysicalTableV0Test() {
		final String query = "select a.id, b.label from qualify a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=qualify}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,70:70='b',<459>,1:70]]}, qualify={account_id=[[@15,55:55='a',<459>,1:55]], active=[[@23,81:81='a',<459>,1:81]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,70:70='b',<459>,1:70]]}, qualify={account_id=[[@15,55:55='a',<459>,1:55]], active=[[@23,81:81='a',<459>,1:81]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=qualify, b=accounts}}}");
	}

	@Test
	public void contextualKeywordQUALIFYQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.qualify a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=qualify}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{analytics.qualify={account_id=[[@17,65:65='a',<459>,1:65]], active=[[@25,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,80:80='b',<459>,1:80]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={analytics.qualify={account_id=[[@17,65:65='a',<459>,1:65]], active=[[@25,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,80:80='b',<459>,1:80]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.qualify, b=accounts}}}");
	}

	@Test
	public void contextualKeywordQUALIFYExplicitTableAliasV0Test() {
		final String query = "select qualify.id, b.label from events as qualify join accounts b on qualify.account_id = b.id where qualify.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=qualify}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=qualify, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=qualify}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=qualify}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,19:19='b',<459>,1:19]], id=[[@20,90:90='b',<459>,1:90]]}, events={account_id=[[@16,69:75='qualify',<330>,1:69]], active=[[@24,101:107='qualify',<330>,1:101]], id=[[@1,7:13='qualify',<330>,1:7]]}}",
				"{query0={id=[[@3,15:16='id',<459>,1:15]], label=[[@7,21:25='label',<459>,1:21]]}}",
				"{def_query0={query_dictionary={id=[[@3,15:16='id',<459>,1:15]], label=[[@7,21:25='label',<459>,1:21]]}, table_dictionary={accounts={label=[[@5,19:19='b',<459>,1:19]], id=[[@20,90:90='b',<459>,1:90]]}, events={account_id=[[@16,69:75='qualify',<330>,1:69]], active=[[@24,101:107='qualify',<330>,1:101]], id=[[@1,7:13='qualify',<330>,1:7]]}}, filters=[{name=account_id, table_ref=qualify}, {name=id, table_ref=b}, {name=active, table_ref=qualify}], interface={id=[{name=id, table_ref=qualify}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, qualify=events}}}");
	}

	@Test
	public void contextualKeywordQUALIFYImplicitTableAliasV0Test() {
		final String query = "select qualify.id, b.label from events qualify join accounts b on qualify.account_id = b.id where qualify.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=qualify}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=qualify, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=qualify}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=qualify}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,19:19='b',<459>,1:19]], id=[[@19,87:87='b',<459>,1:87]]}, events={account_id=[[@15,66:72='qualify',<330>,1:66]], active=[[@23,98:104='qualify',<330>,1:98]], id=[[@1,7:13='qualify',<330>,1:7]]}}",
				"{query0={id=[[@3,15:16='id',<459>,1:15]], label=[[@7,21:25='label',<459>,1:21]]}}",
				"{def_query0={query_dictionary={id=[[@3,15:16='id',<459>,1:15]], label=[[@7,21:25='label',<459>,1:21]]}, table_dictionary={accounts={label=[[@5,19:19='b',<459>,1:19]], id=[[@19,87:87='b',<459>,1:87]]}, events={account_id=[[@15,66:72='qualify',<330>,1:66]], active=[[@23,98:104='qualify',<330>,1:98]], id=[[@1,7:13='qualify',<330>,1:7]]}}, filters=[{name=account_id, table_ref=qualify}, {name=id, table_ref=b}, {name=active, table_ref=qualify}], interface={id=[{name=id, table_ref=qualify}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, qualify=events}}}");
	}

	@Test
	public void contextualKeywordQUALIFYCteLabelAndOutputAliasV0Test() {
		final String query = "with qualify as (select a.amount as qualify, a.id as row_id from events a where a.active = 1) select qualify.qualify as result_value, qualify.row_id from qualify where qualify.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=qualify}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=qualify}}, query={select={1={column={name=qualify, table_ref=qualify}, alias=result_value}, 2={column={name=row_id, table_ref=qualify}}}, from={table={alias=null, table=qualify}}, where={condition={left={column={name=row_id, table_ref=qualify}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,24:24='a',<459>,1:24]], active=[[@20,80:80='a',<459>,1:80]], id=[[@11,45:45='a',<459>,1:45]]}}",
				"{query0={row_id=[[@15,53:58='row_id',<459>,1:53], [@33,134:140='qualify',<330>,1:134], [@39,168:174='qualify',<330>,1:168]], qualify=[[@9,36:42='qualify',<330>,1:36], [@27,101:107='qualify',<330>,1:101]]}, query1={row_id=[[@35,142:147='row_id',<459>,1:142]], result_value=[[@31,120:131='result_value',<459>,1:120]]}}",
				"{def_query1={context_list={qualify=query0}, query_dictionary={result_value=[[@31,120:131='result_value',<459>,1:120]], row_id=[[@35,142:147='row_id',<459>,1:142]]}, def_query0={query_dictionary={row_id=[[@15,53:58='row_id',<459>,1:53], [@33,134:140='qualify',<330>,1:134], [@39,168:174='qualify',<330>,1:168]], qualify=[[@9,36:42='qualify',<330>,1:36], [@27,101:107='qualify',<330>,1:101]]}, table_dictionary={events={amount=[[@5,24:24='a',<459>,1:24]], active=[[@20,80:80='a',<459>,1:80]], id=[[@11,45:45='a',<459>,1:45]]}}, filters=[{name=active, table_ref=a}], interface={row_id=[{name=id, table_ref=a}], qualify=[{name=amount, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=qualify}], interface={result_value=[{name=qualify, table_ref=qualify}], row_id=[{name=row_id, table_ref=qualify}]}, table_alias={qualify=query0}}}");
	}

	@Test
	public void contextualKeywordQUALIFYWildcardQualifierV0Test() {
		final String query = "select qualify.* from events as qualify where qualify.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=qualify}}}, from={table={alias=qualify, table=events}}, where={condition={left={column={name=active, table_ref=qualify}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,46:52='qualify',<330>,1:46]], *=[[@1,7:13='qualify',<330>,1:7]]}}",
				"{query0={*=[[@3,15:15='*',<291>,1:15]]}}",
				"{def_query0={query_dictionary={*=[[@3,15:15='*',<291>,1:15]]}, table_dictionary={events={active=[[@9,46:52='qualify',<330>,1:46]], *=[[@1,7:13='qualify',<330>,1:7]]}}, filters=[{name=active, table_ref=qualify}], interface={*=[{name=*, table_ref=qualify}]}, table_alias={qualify=events}}}");
	}

	@Test
	public void contextualKeywordCHARINDEXBareColumnV0Test() {
		final String query = "select charindex as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=charindex, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@9,55:60='active',<459>,1:55]], id=[[@5,34:35='id',<459>,1:34]], charindex=[[@1,7:15='charindex',<332>,1:7]]}}",
				"{query0={result_value=[[@3,20:31='result_value',<459>,1:20]], id=[[@5,34:35='id',<459>,1:34]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,20:31='result_value',<459>,1:20]], id=[[@5,34:35='id',<459>,1:34]]}, table_dictionary={events={active=[[@9,55:60='active',<459>,1:55]], id=[[@5,34:35='id',<459>,1:34]], charindex=[[@1,7:15='charindex',<332>,1:7]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=charindex, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordCHARINDEXQualifiedColumnV0Test() {
		final String query = "select a.charindex as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=charindex, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,36:36='b',<459>,1:36]], id=[[@21,92:92='b',<459>,1:92]], enabled=[[@31,120:120='b',<459>,1:120]]}, events={account_id=[[@17,77:77='a',<459>,1:77]], active=[[@25,103:103='a',<459>,1:103]], charindex=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@5,22:33='result_value',<459>,1:22]], label=[[@9,38:42='label',<459>,1:38]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,22:33='result_value',<459>,1:22]], label=[[@9,38:42='label',<459>,1:38]]}, table_dictionary={accounts={label=[[@7,36:36='b',<459>,1:36]], id=[[@21,92:92='b',<459>,1:92]], enabled=[[@31,120:120='b',<459>,1:120]]}, events={account_id=[[@17,77:77='a',<459>,1:77]], active=[[@25,103:103='a',<459>,1:103]], charindex=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=charindex, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordCHARINDEXExplicitColumnAliasV0Test() {
		final String query = "select a.id as charindex, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=charindex}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[label, charindex]",
				"{}",
				"{accounts={label=[[@7,26:26='b',<459>,1:26]], id=[[@21,82:82='b',<459>,1:82]], enabled=[[@31,110:110='b',<459>,1:110]]}, events={account_id=[[@17,67:67='a',<459>,1:67]], active=[[@25,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={charindex=[[@5,15:23='charindex',<332>,1:15]], label=[[@9,28:32='label',<459>,1:28]]}}",
				"{def_query0={query_dictionary={label=[[@9,28:32='label',<459>,1:28]], charindex=[[@5,15:23='charindex',<332>,1:15]]}, table_dictionary={accounts={label=[[@7,26:26='b',<459>,1:26]], id=[[@21,82:82='b',<459>,1:82]], enabled=[[@31,110:110='b',<459>,1:110]]}, events={account_id=[[@17,67:67='a',<459>,1:67]], active=[[@25,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={label=[{name=label, table_ref=b}], charindex=[{name=id, table_ref=a}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordCHARINDEXImplicitColumnAliasV0Test() {
		final String query = "select a.id charindex, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=charindex}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[label, charindex]",
				"{}",
				"{accounts={label=[[@6,23:23='b',<459>,1:23]], id=[[@20,79:79='b',<459>,1:79]], enabled=[[@30,107:107='b',<459>,1:107]]}, events={account_id=[[@16,64:64='a',<459>,1:64]], active=[[@24,90:90='a',<459>,1:90]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={charindex=[[@4,12:20='charindex',<332>,1:12]], label=[[@8,25:29='label',<459>,1:25]]}}",
				"{def_query0={query_dictionary={label=[[@8,25:29='label',<459>,1:25]], charindex=[[@4,12:20='charindex',<332>,1:12]]}, table_dictionary={accounts={label=[[@6,23:23='b',<459>,1:23]], id=[[@20,79:79='b',<459>,1:79]], enabled=[[@30,107:107='b',<459>,1:107]]}, events={account_id=[[@16,64:64='a',<459>,1:64]], active=[[@24,90:90='a',<459>,1:90]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={label=[{name=label, table_ref=b}], charindex=[{name=id, table_ref=a}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordCHARINDEXPhysicalTableV0Test() {
		final String query = "select a.id, b.label from charindex a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=charindex}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,72:72='b',<459>,1:72]]}, charindex={account_id=[[@15,57:57='a',<459>,1:57]], active=[[@23,83:83='a',<459>,1:83]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,72:72='b',<459>,1:72]]}, charindex={account_id=[[@15,57:57='a',<459>,1:57]], active=[[@23,83:83='a',<459>,1:83]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=charindex, b=accounts}}}");
	}

	@Test
	public void contextualKeywordCHARINDEXQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.charindex a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=charindex}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,82:82='b',<459>,1:82]]}, analytics.charindex={account_id=[[@17,67:67='a',<459>,1:67]], active=[[@25,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,82:82='b',<459>,1:82]]}, analytics.charindex={account_id=[[@17,67:67='a',<459>,1:67]], active=[[@25,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.charindex, b=accounts}}}");
	}

	@Test
	public void contextualKeywordCHARINDEXExplicitTableAliasV0Test() {
		final String query = "select charindex.id, b.label from events as charindex join accounts b on charindex.account_id = b.id where charindex.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=charindex}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=charindex, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=charindex}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=charindex}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,21:21='b',<459>,1:21]], id=[[@20,96:96='b',<459>,1:96]]}, events={account_id=[[@16,73:81='charindex',<332>,1:73]], active=[[@24,107:115='charindex',<332>,1:107]], id=[[@1,7:15='charindex',<332>,1:7]]}}",
				"{query0={id=[[@3,17:18='id',<459>,1:17]], label=[[@7,23:27='label',<459>,1:23]]}}",
				"{def_query0={query_dictionary={id=[[@3,17:18='id',<459>,1:17]], label=[[@7,23:27='label',<459>,1:23]]}, table_dictionary={accounts={label=[[@5,21:21='b',<459>,1:21]], id=[[@20,96:96='b',<459>,1:96]]}, events={account_id=[[@16,73:81='charindex',<332>,1:73]], active=[[@24,107:115='charindex',<332>,1:107]], id=[[@1,7:15='charindex',<332>,1:7]]}}, filters=[{name=account_id, table_ref=charindex}, {name=id, table_ref=b}, {name=active, table_ref=charindex}], interface={id=[{name=id, table_ref=charindex}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, charindex=events}}}");
	}

	@Test
	public void contextualKeywordCHARINDEXImplicitTableAliasV0Test() {
		final String query = "select charindex.id, b.label from events charindex join accounts b on charindex.account_id = b.id where charindex.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=charindex}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=charindex, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=charindex}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=charindex}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,21:21='b',<459>,1:21]], id=[[@19,93:93='b',<459>,1:93]]}, events={account_id=[[@15,70:78='charindex',<332>,1:70]], active=[[@23,104:112='charindex',<332>,1:104]], id=[[@1,7:15='charindex',<332>,1:7]]}}",
				"{query0={id=[[@3,17:18='id',<459>,1:17]], label=[[@7,23:27='label',<459>,1:23]]}}",
				"{def_query0={query_dictionary={id=[[@3,17:18='id',<459>,1:17]], label=[[@7,23:27='label',<459>,1:23]]}, table_dictionary={accounts={label=[[@5,21:21='b',<459>,1:21]], id=[[@19,93:93='b',<459>,1:93]]}, events={account_id=[[@15,70:78='charindex',<332>,1:70]], active=[[@23,104:112='charindex',<332>,1:104]], id=[[@1,7:15='charindex',<332>,1:7]]}}, filters=[{name=account_id, table_ref=charindex}, {name=id, table_ref=b}, {name=active, table_ref=charindex}], interface={id=[{name=id, table_ref=charindex}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, charindex=events}}}");
	}

	@Test
	public void contextualKeywordCHARINDEXCteLabelAndOutputAliasV0Test() {
		final String query = "with charindex as (select a.amount as charindex, a.id as row_id from events a where a.active = 1) select charindex.charindex as result_value, charindex.row_id from charindex where charindex.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=charindex}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=charindex}}, query={select={1={column={name=charindex, table_ref=charindex}, alias=result_value}, 2={column={name=row_id, table_ref=charindex}}}, from={table={alias=null, table=charindex}}, where={condition={left={column={name=row_id, table_ref=charindex}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,26:26='a',<459>,1:26]], active=[[@20,84:84='a',<459>,1:84]], id=[[@11,49:49='a',<459>,1:49]]}}",
				"{query0={row_id=[[@15,57:62='row_id',<459>,1:57], [@33,142:150='charindex',<332>,1:142], [@39,180:188='charindex',<332>,1:180]], charindex=[[@9,38:46='charindex',<332>,1:38], [@27,105:113='charindex',<332>,1:105]]}, query1={row_id=[[@35,152:157='row_id',<459>,1:152]], result_value=[[@31,128:139='result_value',<459>,1:128]]}}",
				"{def_query1={context_list={charindex=query0}, query_dictionary={result_value=[[@31,128:139='result_value',<459>,1:128]], row_id=[[@35,152:157='row_id',<459>,1:152]]}, def_query0={query_dictionary={row_id=[[@15,57:62='row_id',<459>,1:57], [@33,142:150='charindex',<332>,1:142], [@39,180:188='charindex',<332>,1:180]], charindex=[[@9,38:46='charindex',<332>,1:38], [@27,105:113='charindex',<332>,1:105]]}, table_dictionary={events={amount=[[@5,26:26='a',<459>,1:26]], active=[[@20,84:84='a',<459>,1:84]], id=[[@11,49:49='a',<459>,1:49]]}}, filters=[{name=active, table_ref=a}], interface={row_id=[{name=id, table_ref=a}], charindex=[{name=amount, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=charindex}], interface={result_value=[{name=charindex, table_ref=charindex}], row_id=[{name=row_id, table_ref=charindex}]}, table_alias={charindex=query0}}}");
	}

	@Test
	public void contextualKeywordCHARINDEXWildcardQualifierV0Test() {
		final String query = "select charindex.* from events as charindex where charindex.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=charindex}}}, from={table={alias=charindex, table=events}}, where={condition={left={column={name=active, table_ref=charindex}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,50:58='charindex',<332>,1:50]], *=[[@1,7:15='charindex',<332>,1:7]]}}",
				"{query0={*=[[@3,17:17='*',<291>,1:17]]}}",
				"{def_query0={query_dictionary={*=[[@3,17:17='*',<291>,1:17]]}, table_dictionary={events={active=[[@9,50:58='charindex',<332>,1:50]], *=[[@1,7:15='charindex',<332>,1:7]]}}, filters=[{name=active, table_ref=charindex}], interface={*=[{name=*, table_ref=charindex}]}, table_alias={charindex=events}}}");
	}

	@Test
	public void contextualKeywordINSTRBareColumnV0Test() {
		final String query = "select instr as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=instr, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={instr=[[@1,7:11='instr',<333>,1:7]], active=[[@9,51:56='active',<459>,1:51]], id=[[@5,30:31='id',<459>,1:30]]}}",
				"{query0={result_value=[[@3,16:27='result_value',<459>,1:16]], id=[[@5,30:31='id',<459>,1:30]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,16:27='result_value',<459>,1:16]], id=[[@5,30:31='id',<459>,1:30]]}, table_dictionary={events={instr=[[@1,7:11='instr',<333>,1:7]], active=[[@9,51:56='active',<459>,1:51]], id=[[@5,30:31='id',<459>,1:30]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=instr, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordINSTRQualifiedColumnV0Test() {
		final String query = "select a.instr as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=instr, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,32:32='b',<459>,1:32]], id=[[@21,88:88='b',<459>,1:88]], enabled=[[@31,116:116='b',<459>,1:116]]}, events={account_id=[[@17,73:73='a',<459>,1:73]], instr=[[@1,7:7='a',<459>,1:7]], active=[[@25,99:99='a',<459>,1:99]]}}",
				"{query0={result_value=[[@5,18:29='result_value',<459>,1:18]], label=[[@9,34:38='label',<459>,1:34]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,18:29='result_value',<459>,1:18]], label=[[@9,34:38='label',<459>,1:34]]}, table_dictionary={accounts={label=[[@7,32:32='b',<459>,1:32]], id=[[@21,88:88='b',<459>,1:88]], enabled=[[@31,116:116='b',<459>,1:116]]}, events={account_id=[[@17,73:73='a',<459>,1:73]], instr=[[@1,7:7='a',<459>,1:7]], active=[[@25,99:99='a',<459>,1:99]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=instr, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordINSTRExplicitColumnAliasV0Test() {
		final String query = "select a.id as instr, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=instr}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[instr, label]",
				"{}",
				"{accounts={label=[[@7,22:22='b',<459>,1:22]], id=[[@21,78:78='b',<459>,1:78]], enabled=[[@31,106:106='b',<459>,1:106]]}, events={account_id=[[@17,63:63='a',<459>,1:63]], active=[[@25,89:89='a',<459>,1:89]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={instr=[[@5,15:19='instr',<333>,1:15]], label=[[@9,24:28='label',<459>,1:24]]}}",
				"{def_query0={query_dictionary={instr=[[@5,15:19='instr',<333>,1:15]], label=[[@9,24:28='label',<459>,1:24]]}, table_dictionary={accounts={label=[[@7,22:22='b',<459>,1:22]], id=[[@21,78:78='b',<459>,1:78]], enabled=[[@31,106:106='b',<459>,1:106]]}, events={account_id=[[@17,63:63='a',<459>,1:63]], active=[[@25,89:89='a',<459>,1:89]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={instr=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordINSTRImplicitColumnAliasV0Test() {
		final String query = "select a.id instr, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=instr}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[instr, label]",
				"{}",
				"{accounts={label=[[@6,19:19='b',<459>,1:19]], id=[[@20,75:75='b',<459>,1:75]], enabled=[[@30,103:103='b',<459>,1:103]]}, events={account_id=[[@16,60:60='a',<459>,1:60]], active=[[@24,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={instr=[[@4,12:16='instr',<333>,1:12]], label=[[@8,21:25='label',<459>,1:21]]}}",
				"{def_query0={query_dictionary={instr=[[@4,12:16='instr',<333>,1:12]], label=[[@8,21:25='label',<459>,1:21]]}, table_dictionary={accounts={label=[[@6,19:19='b',<459>,1:19]], id=[[@20,75:75='b',<459>,1:75]], enabled=[[@30,103:103='b',<459>,1:103]]}, events={account_id=[[@16,60:60='a',<459>,1:60]], active=[[@24,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={instr=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordINSTRPhysicalTableV0Test() {
		final String query = "select a.id, b.label from instr a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=instr}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{instr={account_id=[[@15,53:53='a',<459>,1:53]], active=[[@23,79:79='a',<459>,1:79]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,68:68='b',<459>,1:68]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={instr={account_id=[[@15,53:53='a',<459>,1:53]], active=[[@23,79:79='a',<459>,1:79]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,68:68='b',<459>,1:68]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=instr, b=accounts}}}");
	}

	@Test
	public void contextualKeywordINSTRQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.instr a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=instr}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{analytics.instr={account_id=[[@17,63:63='a',<459>,1:63]], active=[[@25,89:89='a',<459>,1:89]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,78:78='b',<459>,1:78]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={analytics.instr={account_id=[[@17,63:63='a',<459>,1:63]], active=[[@25,89:89='a',<459>,1:89]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,78:78='b',<459>,1:78]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.instr, b=accounts}}}");
	}

	@Test
	public void contextualKeywordINSTRExplicitTableAliasV0Test() {
		final String query = "select instr.id, b.label from events as instr join accounts b on instr.account_id = b.id where instr.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=instr}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=instr, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=instr}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=instr}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,17:17='b',<459>,1:17]], id=[[@20,84:84='b',<459>,1:84]]}, events={account_id=[[@16,65:69='instr',<333>,1:65]], active=[[@24,95:99='instr',<333>,1:95]], id=[[@1,7:11='instr',<333>,1:7]]}}",
				"{query0={id=[[@3,13:14='id',<459>,1:13]], label=[[@7,19:23='label',<459>,1:19]]}}",
				"{def_query0={query_dictionary={id=[[@3,13:14='id',<459>,1:13]], label=[[@7,19:23='label',<459>,1:19]]}, table_dictionary={accounts={label=[[@5,17:17='b',<459>,1:17]], id=[[@20,84:84='b',<459>,1:84]]}, events={account_id=[[@16,65:69='instr',<333>,1:65]], active=[[@24,95:99='instr',<333>,1:95]], id=[[@1,7:11='instr',<333>,1:7]]}}, filters=[{name=account_id, table_ref=instr}, {name=id, table_ref=b}, {name=active, table_ref=instr}], interface={id=[{name=id, table_ref=instr}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, instr=events}}}");
	}

	@Test
	public void contextualKeywordINSTRImplicitTableAliasV0Test() {
		final String query = "select instr.id, b.label from events instr join accounts b on instr.account_id = b.id where instr.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=instr}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=instr, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=instr}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=instr}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,17:17='b',<459>,1:17]], id=[[@19,81:81='b',<459>,1:81]]}, events={account_id=[[@15,62:66='instr',<333>,1:62]], active=[[@23,92:96='instr',<333>,1:92]], id=[[@1,7:11='instr',<333>,1:7]]}}",
				"{query0={id=[[@3,13:14='id',<459>,1:13]], label=[[@7,19:23='label',<459>,1:19]]}}",
				"{def_query0={query_dictionary={id=[[@3,13:14='id',<459>,1:13]], label=[[@7,19:23='label',<459>,1:19]]}, table_dictionary={accounts={label=[[@5,17:17='b',<459>,1:17]], id=[[@19,81:81='b',<459>,1:81]]}, events={account_id=[[@15,62:66='instr',<333>,1:62]], active=[[@23,92:96='instr',<333>,1:92]], id=[[@1,7:11='instr',<333>,1:7]]}}, filters=[{name=account_id, table_ref=instr}, {name=id, table_ref=b}, {name=active, table_ref=instr}], interface={id=[{name=id, table_ref=instr}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, instr=events}}}");
	}

	@Test
	public void contextualKeywordINSTRCteLabelAndOutputAliasV0Test() {
		final String query = "with instr as (select a.amount as instr, a.id as row_id from events a where a.active = 1) select instr.instr as result_value, instr.row_id from instr where instr.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=instr}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=instr}}, query={select={1={column={name=instr, table_ref=instr}, alias=result_value}, 2={column={name=row_id, table_ref=instr}}}, from={table={alias=null, table=instr}}, where={condition={left={column={name=row_id, table_ref=instr}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,22:22='a',<459>,1:22]], active=[[@20,76:76='a',<459>,1:76]], id=[[@11,41:41='a',<459>,1:41]]}}",
				"{query0={row_id=[[@15,49:54='row_id',<459>,1:49], [@33,126:130='instr',<333>,1:126], [@39,156:160='instr',<333>,1:156]], instr=[[@9,34:38='instr',<333>,1:34], [@27,97:101='instr',<333>,1:97]]}, query1={row_id=[[@35,132:137='row_id',<459>,1:132]], result_value=[[@31,112:123='result_value',<459>,1:112]]}}",
				"{def_query1={context_list={instr=query0}, query_dictionary={result_value=[[@31,112:123='result_value',<459>,1:112]], row_id=[[@35,132:137='row_id',<459>,1:132]]}, def_query0={query_dictionary={instr=[[@9,34:38='instr',<333>,1:34], [@27,97:101='instr',<333>,1:97]], row_id=[[@15,49:54='row_id',<459>,1:49], [@33,126:130='instr',<333>,1:126], [@39,156:160='instr',<333>,1:156]]}, table_dictionary={events={amount=[[@5,22:22='a',<459>,1:22]], active=[[@20,76:76='a',<459>,1:76]], id=[[@11,41:41='a',<459>,1:41]]}}, filters=[{name=active, table_ref=a}], interface={instr=[{name=amount, table_ref=a}], row_id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=instr}], interface={result_value=[{name=instr, table_ref=instr}], row_id=[{name=row_id, table_ref=instr}]}, table_alias={instr=query0}}}");
	}

	@Test
	public void contextualKeywordINSTRWildcardQualifierV0Test() {
		final String query = "select instr.* from events as instr where instr.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=instr}}}, from={table={alias=instr, table=events}}, where={condition={left={column={name=active, table_ref=instr}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,42:46='instr',<333>,1:42]], *=[[@1,7:11='instr',<333>,1:7]]}}",
				"{query0={*=[[@3,13:13='*',<291>,1:13]]}}",
				"{def_query0={query_dictionary={*=[[@3,13:13='*',<291>,1:13]]}, table_dictionary={events={active=[[@9,42:46='instr',<333>,1:42]], *=[[@1,7:11='instr',<333>,1:7]]}}, filters=[{name=active, table_ref=instr}], interface={*=[{name=*, table_ref=instr}]}, table_alias={instr=events}}}");
	}

	@Test
	public void contextualKeywordTRUNCATEBareColumnV0Test() {
		final String query = "select truncate as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=truncate, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={truncate=[[@1,7:14='truncate',<335>,1:7]], active=[[@9,54:59='active',<459>,1:54]], id=[[@5,33:34='id',<459>,1:33]]}}",
				"{query0={result_value=[[@3,19:30='result_value',<459>,1:19]], id=[[@5,33:34='id',<459>,1:33]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,19:30='result_value',<459>,1:19]], id=[[@5,33:34='id',<459>,1:33]]}, table_dictionary={events={truncate=[[@1,7:14='truncate',<335>,1:7]], active=[[@9,54:59='active',<459>,1:54]], id=[[@5,33:34='id',<459>,1:33]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=truncate, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordTRUNCATEQualifiedColumnV0Test() {
		final String query = "select a.truncate as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=truncate, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,35:35='b',<459>,1:35]], id=[[@21,91:91='b',<459>,1:91]], enabled=[[@31,119:119='b',<459>,1:119]]}, events={account_id=[[@17,76:76='a',<459>,1:76]], truncate=[[@1,7:7='a',<459>,1:7]], active=[[@25,102:102='a',<459>,1:102]]}}",
				"{query0={result_value=[[@5,21:32='result_value',<459>,1:21]], label=[[@9,37:41='label',<459>,1:37]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,21:32='result_value',<459>,1:21]], label=[[@9,37:41='label',<459>,1:37]]}, table_dictionary={accounts={label=[[@7,35:35='b',<459>,1:35]], id=[[@21,91:91='b',<459>,1:91]], enabled=[[@31,119:119='b',<459>,1:119]]}, events={account_id=[[@17,76:76='a',<459>,1:76]], truncate=[[@1,7:7='a',<459>,1:7]], active=[[@25,102:102='a',<459>,1:102]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=truncate, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordTRUNCATEExplicitColumnAliasV0Test() {
		final String query = "select a.id as truncate, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=truncate}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[truncate, label]",
				"{}",
				"{accounts={label=[[@7,25:25='b',<459>,1:25]], id=[[@21,81:81='b',<459>,1:81]], enabled=[[@31,109:109='b',<459>,1:109]]}, events={account_id=[[@17,66:66='a',<459>,1:66]], active=[[@25,92:92='a',<459>,1:92]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={truncate=[[@5,15:22='truncate',<335>,1:15]], label=[[@9,27:31='label',<459>,1:27]]}}",
				"{def_query0={query_dictionary={truncate=[[@5,15:22='truncate',<335>,1:15]], label=[[@9,27:31='label',<459>,1:27]]}, table_dictionary={accounts={label=[[@7,25:25='b',<459>,1:25]], id=[[@21,81:81='b',<459>,1:81]], enabled=[[@31,109:109='b',<459>,1:109]]}, events={account_id=[[@17,66:66='a',<459>,1:66]], active=[[@25,92:92='a',<459>,1:92]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={truncate=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordTRUNCATEImplicitColumnAliasV0Test() {
		final String query = "select a.id truncate, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=truncate}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[truncate, label]",
				"{}",
				"{accounts={label=[[@6,22:22='b',<459>,1:22]], id=[[@20,78:78='b',<459>,1:78]], enabled=[[@30,106:106='b',<459>,1:106]]}, events={account_id=[[@16,63:63='a',<459>,1:63]], active=[[@24,89:89='a',<459>,1:89]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={truncate=[[@4,12:19='truncate',<335>,1:12]], label=[[@8,24:28='label',<459>,1:24]]}}",
				"{def_query0={query_dictionary={truncate=[[@4,12:19='truncate',<335>,1:12]], label=[[@8,24:28='label',<459>,1:24]]}, table_dictionary={accounts={label=[[@6,22:22='b',<459>,1:22]], id=[[@20,78:78='b',<459>,1:78]], enabled=[[@30,106:106='b',<459>,1:106]]}, events={account_id=[[@16,63:63='a',<459>,1:63]], active=[[@24,89:89='a',<459>,1:89]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={truncate=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordTRUNCATEPhysicalTableV0Test() {
		final String query = "select a.id, b.label from truncate a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=truncate}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{truncate={account_id=[[@15,56:56='a',<459>,1:56]], active=[[@23,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,71:71='b',<459>,1:71]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={truncate={account_id=[[@15,56:56='a',<459>,1:56]], active=[[@23,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,71:71='b',<459>,1:71]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=truncate, b=accounts}}}");
	}

	@Test
	public void contextualKeywordTRUNCATEQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.truncate a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=truncate}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{analytics.truncate={account_id=[[@17,66:66='a',<459>,1:66]], active=[[@25,92:92='a',<459>,1:92]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,81:81='b',<459>,1:81]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={analytics.truncate={account_id=[[@17,66:66='a',<459>,1:66]], active=[[@25,92:92='a',<459>,1:92]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,81:81='b',<459>,1:81]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.truncate, b=accounts}}}");
	}

	@Test
	public void contextualKeywordTRUNCATEExplicitTableAliasV0Test() {
		final String query = "select truncate.id, b.label from events as truncate join accounts b on truncate.account_id = b.id where truncate.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=truncate}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=truncate, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=truncate}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=truncate}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,20:20='b',<459>,1:20]], id=[[@20,93:93='b',<459>,1:93]]}, events={account_id=[[@16,71:78='truncate',<335>,1:71]], active=[[@24,104:111='truncate',<335>,1:104]], id=[[@1,7:14='truncate',<335>,1:7]]}}",
				"{query0={id=[[@3,16:17='id',<459>,1:16]], label=[[@7,22:26='label',<459>,1:22]]}}",
				"{def_query0={query_dictionary={id=[[@3,16:17='id',<459>,1:16]], label=[[@7,22:26='label',<459>,1:22]]}, table_dictionary={accounts={label=[[@5,20:20='b',<459>,1:20]], id=[[@20,93:93='b',<459>,1:93]]}, events={account_id=[[@16,71:78='truncate',<335>,1:71]], active=[[@24,104:111='truncate',<335>,1:104]], id=[[@1,7:14='truncate',<335>,1:7]]}}, filters=[{name=account_id, table_ref=truncate}, {name=id, table_ref=b}, {name=active, table_ref=truncate}], interface={id=[{name=id, table_ref=truncate}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, truncate=events}}}");
	}

	@Test
	public void contextualKeywordTRUNCATEImplicitTableAliasV0Test() {
		final String query = "select truncate.id, b.label from events truncate join accounts b on truncate.account_id = b.id where truncate.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=truncate}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=truncate, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=truncate}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=truncate}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,20:20='b',<459>,1:20]], id=[[@19,90:90='b',<459>,1:90]]}, events={account_id=[[@15,68:75='truncate',<335>,1:68]], active=[[@23,101:108='truncate',<335>,1:101]], id=[[@1,7:14='truncate',<335>,1:7]]}}",
				"{query0={id=[[@3,16:17='id',<459>,1:16]], label=[[@7,22:26='label',<459>,1:22]]}}",
				"{def_query0={query_dictionary={id=[[@3,16:17='id',<459>,1:16]], label=[[@7,22:26='label',<459>,1:22]]}, table_dictionary={accounts={label=[[@5,20:20='b',<459>,1:20]], id=[[@19,90:90='b',<459>,1:90]]}, events={account_id=[[@15,68:75='truncate',<335>,1:68]], active=[[@23,101:108='truncate',<335>,1:101]], id=[[@1,7:14='truncate',<335>,1:7]]}}, filters=[{name=account_id, table_ref=truncate}, {name=id, table_ref=b}, {name=active, table_ref=truncate}], interface={id=[{name=id, table_ref=truncate}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, truncate=events}}}");
	}

	@Test
	public void contextualKeywordTRUNCATECteLabelAndOutputAliasV0Test() {
		final String query = "with truncate as (select a.amount as truncate, a.id as row_id from events a where a.active = 1) select truncate.truncate as result_value, truncate.row_id from truncate where truncate.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=truncate}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=truncate}}, query={select={1={column={name=truncate, table_ref=truncate}, alias=result_value}, 2={column={name=row_id, table_ref=truncate}}}, from={table={alias=null, table=truncate}}, where={condition={left={column={name=row_id, table_ref=truncate}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,25:25='a',<459>,1:25]], active=[[@20,82:82='a',<459>,1:82]], id=[[@11,47:47='a',<459>,1:47]]}}",
				"{query0={truncate=[[@9,37:44='truncate',<335>,1:37], [@27,103:110='truncate',<335>,1:103]], row_id=[[@15,55:60='row_id',<459>,1:55], [@33,138:145='truncate',<335>,1:138], [@39,174:181='truncate',<335>,1:174]]}, query1={row_id=[[@35,147:152='row_id',<459>,1:147]], result_value=[[@31,124:135='result_value',<459>,1:124]]}}",
				"{def_query1={context_list={truncate=query0}, query_dictionary={result_value=[[@31,124:135='result_value',<459>,1:124]], row_id=[[@35,147:152='row_id',<459>,1:147]]}, def_query0={query_dictionary={truncate=[[@9,37:44='truncate',<335>,1:37], [@27,103:110='truncate',<335>,1:103]], row_id=[[@15,55:60='row_id',<459>,1:55], [@33,138:145='truncate',<335>,1:138], [@39,174:181='truncate',<335>,1:174]]}, table_dictionary={events={amount=[[@5,25:25='a',<459>,1:25]], active=[[@20,82:82='a',<459>,1:82]], id=[[@11,47:47='a',<459>,1:47]]}}, filters=[{name=active, table_ref=a}], interface={truncate=[{name=amount, table_ref=a}], row_id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=truncate}], interface={result_value=[{name=truncate, table_ref=truncate}], row_id=[{name=row_id, table_ref=truncate}]}, table_alias={truncate=query0}}}");
	}

	@Test
	public void contextualKeywordTRUNCATEWildcardQualifierV0Test() {
		final String query = "select truncate.* from events as truncate where truncate.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=truncate}}}, from={table={alias=truncate, table=events}}, where={condition={left={column={name=active, table_ref=truncate}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,48:55='truncate',<335>,1:48]], *=[[@1,7:14='truncate',<335>,1:7]]}}",
				"{query0={*=[[@3,16:16='*',<291>,1:16]]}}",
				"{def_query0={query_dictionary={*=[[@3,16:16='*',<291>,1:16]]}, table_dictionary={events={active=[[@9,48:55='truncate',<335>,1:48]], *=[[@1,7:14='truncate',<335>,1:7]]}}, filters=[{name=active, table_ref=truncate}], interface={*=[{name=*, table_ref=truncate}]}, table_alias={truncate=events}}}");
	}

	@Test
	public void contextualKeywordIFFBareColumnV0Test() {
		final String query = "select iff as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=iff, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={iff=[[@1,7:9='iff',<336>,1:7]], active=[[@9,49:54='active',<459>,1:49]], id=[[@5,28:29='id',<459>,1:28]]}}",
				"{query0={result_value=[[@3,14:25='result_value',<459>,1:14]], id=[[@5,28:29='id',<459>,1:28]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,14:25='result_value',<459>,1:14]], id=[[@5,28:29='id',<459>,1:28]]}, table_dictionary={events={iff=[[@1,7:9='iff',<336>,1:7]], active=[[@9,49:54='active',<459>,1:49]], id=[[@5,28:29='id',<459>,1:28]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=iff, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordIFFQualifiedColumnV0Test() {
		final String query = "select a.iff as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=iff, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,30:30='b',<459>,1:30]], id=[[@21,86:86='b',<459>,1:86]], enabled=[[@31,114:114='b',<459>,1:114]]}, events={account_id=[[@17,71:71='a',<459>,1:71]], iff=[[@1,7:7='a',<459>,1:7]], active=[[@25,97:97='a',<459>,1:97]]}}",
				"{query0={result_value=[[@5,16:27='result_value',<459>,1:16]], label=[[@9,32:36='label',<459>,1:32]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,16:27='result_value',<459>,1:16]], label=[[@9,32:36='label',<459>,1:32]]}, table_dictionary={accounts={label=[[@7,30:30='b',<459>,1:30]], id=[[@21,86:86='b',<459>,1:86]], enabled=[[@31,114:114='b',<459>,1:114]]}, events={account_id=[[@17,71:71='a',<459>,1:71]], iff=[[@1,7:7='a',<459>,1:7]], active=[[@25,97:97='a',<459>,1:97]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=iff, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordIFFExplicitColumnAliasV0Test() {
		final String query = "select a.id as iff, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=iff}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[iff, label]",
				"{}",
				"{accounts={label=[[@7,20:20='b',<459>,1:20]], id=[[@21,76:76='b',<459>,1:76]], enabled=[[@31,104:104='b',<459>,1:104]]}, events={account_id=[[@17,61:61='a',<459>,1:61]], active=[[@25,87:87='a',<459>,1:87]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={iff=[[@5,15:17='iff',<336>,1:15]], label=[[@9,22:26='label',<459>,1:22]]}}",
				"{def_query0={query_dictionary={iff=[[@5,15:17='iff',<336>,1:15]], label=[[@9,22:26='label',<459>,1:22]]}, table_dictionary={accounts={label=[[@7,20:20='b',<459>,1:20]], id=[[@21,76:76='b',<459>,1:76]], enabled=[[@31,104:104='b',<459>,1:104]]}, events={account_id=[[@17,61:61='a',<459>,1:61]], active=[[@25,87:87='a',<459>,1:87]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={iff=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordIFFImplicitColumnAliasV0Test() {
		final String query = "select a.id iff, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=iff}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[iff, label]",
				"{}",
				"{accounts={label=[[@6,17:17='b',<459>,1:17]], id=[[@20,73:73='b',<459>,1:73]], enabled=[[@30,101:101='b',<459>,1:101]]}, events={account_id=[[@16,58:58='a',<459>,1:58]], active=[[@24,84:84='a',<459>,1:84]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={iff=[[@4,12:14='iff',<336>,1:12]], label=[[@8,19:23='label',<459>,1:19]]}}",
				"{def_query0={query_dictionary={iff=[[@4,12:14='iff',<336>,1:12]], label=[[@8,19:23='label',<459>,1:19]]}, table_dictionary={accounts={label=[[@6,17:17='b',<459>,1:17]], id=[[@20,73:73='b',<459>,1:73]], enabled=[[@30,101:101='b',<459>,1:101]]}, events={account_id=[[@16,58:58='a',<459>,1:58]], active=[[@24,84:84='a',<459>,1:84]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={iff=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordIFFPhysicalTableV0Test() {
		final String query = "select a.id, b.label from iff a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=iff}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{iff={account_id=[[@15,51:51='a',<459>,1:51]], active=[[@23,77:77='a',<459>,1:77]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,66:66='b',<459>,1:66]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={iff={account_id=[[@15,51:51='a',<459>,1:51]], active=[[@23,77:77='a',<459>,1:77]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,66:66='b',<459>,1:66]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=iff, b=accounts}}}");
	}

	@Test
	public void contextualKeywordIFFQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.iff a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=iff}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{analytics.iff={account_id=[[@17,61:61='a',<459>,1:61]], active=[[@25,87:87='a',<459>,1:87]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,76:76='b',<459>,1:76]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={analytics.iff={account_id=[[@17,61:61='a',<459>,1:61]], active=[[@25,87:87='a',<459>,1:87]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,76:76='b',<459>,1:76]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.iff, b=accounts}}}");
	}

	@Test
	public void contextualKeywordIFFExplicitTableAliasV0Test() {
		final String query = "select iff.id, b.label from events as iff join accounts b on iff.account_id = b.id where iff.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=iff}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=iff, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=iff}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=iff}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,15:15='b',<459>,1:15]], id=[[@20,78:78='b',<459>,1:78]]}, events={account_id=[[@16,61:63='iff',<336>,1:61]], active=[[@24,89:91='iff',<336>,1:89]], id=[[@1,7:9='iff',<336>,1:7]]}}",
				"{query0={id=[[@3,11:12='id',<459>,1:11]], label=[[@7,17:21='label',<459>,1:17]]}}",
				"{def_query0={query_dictionary={id=[[@3,11:12='id',<459>,1:11]], label=[[@7,17:21='label',<459>,1:17]]}, table_dictionary={accounts={label=[[@5,15:15='b',<459>,1:15]], id=[[@20,78:78='b',<459>,1:78]]}, events={account_id=[[@16,61:63='iff',<336>,1:61]], active=[[@24,89:91='iff',<336>,1:89]], id=[[@1,7:9='iff',<336>,1:7]]}}, filters=[{name=account_id, table_ref=iff}, {name=id, table_ref=b}, {name=active, table_ref=iff}], interface={id=[{name=id, table_ref=iff}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, iff=events}}}");
	}

	@Test
	public void contextualKeywordIFFImplicitTableAliasV0Test() {
		final String query = "select iff.id, b.label from events iff join accounts b on iff.account_id = b.id where iff.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=iff}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=iff, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=iff}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=iff}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,15:15='b',<459>,1:15]], id=[[@19,75:75='b',<459>,1:75]]}, events={account_id=[[@15,58:60='iff',<336>,1:58]], active=[[@23,86:88='iff',<336>,1:86]], id=[[@1,7:9='iff',<336>,1:7]]}}",
				"{query0={id=[[@3,11:12='id',<459>,1:11]], label=[[@7,17:21='label',<459>,1:17]]}}",
				"{def_query0={query_dictionary={id=[[@3,11:12='id',<459>,1:11]], label=[[@7,17:21='label',<459>,1:17]]}, table_dictionary={accounts={label=[[@5,15:15='b',<459>,1:15]], id=[[@19,75:75='b',<459>,1:75]]}, events={account_id=[[@15,58:60='iff',<336>,1:58]], active=[[@23,86:88='iff',<336>,1:86]], id=[[@1,7:9='iff',<336>,1:7]]}}, filters=[{name=account_id, table_ref=iff}, {name=id, table_ref=b}, {name=active, table_ref=iff}], interface={id=[{name=id, table_ref=iff}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, iff=events}}}");
	}

	@Test
	public void contextualKeywordIFFCteLabelAndOutputAliasV0Test() {
		final String query = "with iff as (select a.amount as iff, a.id as row_id from events a where a.active = 1) select iff.iff as result_value, iff.row_id from iff where iff.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=iff}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=iff}}, query={select={1={column={name=iff, table_ref=iff}, alias=result_value}, 2={column={name=row_id, table_ref=iff}}}, from={table={alias=null, table=iff}}, where={condition={left={column={name=row_id, table_ref=iff}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,20:20='a',<459>,1:20]], active=[[@20,72:72='a',<459>,1:72]], id=[[@11,37:37='a',<459>,1:37]]}}",
				"{query0={iff=[[@9,32:34='iff',<336>,1:32], [@27,93:95='iff',<336>,1:93]], row_id=[[@15,45:50='row_id',<459>,1:45], [@33,118:120='iff',<336>,1:118], [@39,144:146='iff',<336>,1:144]]}, query1={row_id=[[@35,122:127='row_id',<459>,1:122]], result_value=[[@31,104:115='result_value',<459>,1:104]]}}",
				"{def_query1={context_list={iff=query0}, query_dictionary={result_value=[[@31,104:115='result_value',<459>,1:104]], row_id=[[@35,122:127='row_id',<459>,1:122]]}, def_query0={query_dictionary={iff=[[@9,32:34='iff',<336>,1:32], [@27,93:95='iff',<336>,1:93]], row_id=[[@15,45:50='row_id',<459>,1:45], [@33,118:120='iff',<336>,1:118], [@39,144:146='iff',<336>,1:144]]}, table_dictionary={events={amount=[[@5,20:20='a',<459>,1:20]], active=[[@20,72:72='a',<459>,1:72]], id=[[@11,37:37='a',<459>,1:37]]}}, filters=[{name=active, table_ref=a}], interface={iff=[{name=amount, table_ref=a}], row_id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=iff}], interface={result_value=[{name=iff, table_ref=iff}], row_id=[{name=row_id, table_ref=iff}]}, table_alias={iff=query0}}}");
	}

	@Test
	public void contextualKeywordIFFWildcardQualifierV0Test() {
		final String query = "select iff.* from events as iff where iff.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=iff}}}, from={table={alias=iff, table=events}}, where={condition={left={column={name=active, table_ref=iff}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,38:40='iff',<336>,1:38]], *=[[@1,7:9='iff',<336>,1:7]]}}",
				"{query0={*=[[@3,11:11='*',<291>,1:11]]}}",
				"{def_query0={query_dictionary={*=[[@3,11:11='*',<291>,1:11]]}, table_dictionary={events={active=[[@9,38:40='iff',<336>,1:38]], *=[[@1,7:9='iff',<336>,1:7]]}}, filters=[{name=active, table_ref=iff}], interface={*=[{name=*, table_ref=iff}]}, table_alias={iff=events}}}");
	}

	@Test
	public void contextualKeywordMD5BareColumnV0Test() {
		final String query = "select md5 as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=md5, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@9,49:54='active',<459>,1:49]], id=[[@5,28:29='id',<459>,1:28]], md5=[[@1,7:9='md5',<337>,1:7]]}}",
				"{query0={result_value=[[@3,14:25='result_value',<459>,1:14]], id=[[@5,28:29='id',<459>,1:28]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,14:25='result_value',<459>,1:14]], id=[[@5,28:29='id',<459>,1:28]]}, table_dictionary={events={active=[[@9,49:54='active',<459>,1:49]], id=[[@5,28:29='id',<459>,1:28]], md5=[[@1,7:9='md5',<337>,1:7]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=md5, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordMD5QualifiedColumnV0Test() {
		final String query = "select a.md5 as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=md5, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,30:30='b',<459>,1:30]], id=[[@21,86:86='b',<459>,1:86]], enabled=[[@31,114:114='b',<459>,1:114]]}, events={account_id=[[@17,71:71='a',<459>,1:71]], active=[[@25,97:97='a',<459>,1:97]], md5=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@5,16:27='result_value',<459>,1:16]], label=[[@9,32:36='label',<459>,1:32]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,16:27='result_value',<459>,1:16]], label=[[@9,32:36='label',<459>,1:32]]}, table_dictionary={accounts={label=[[@7,30:30='b',<459>,1:30]], id=[[@21,86:86='b',<459>,1:86]], enabled=[[@31,114:114='b',<459>,1:114]]}, events={account_id=[[@17,71:71='a',<459>,1:71]], active=[[@25,97:97='a',<459>,1:97]], md5=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=md5, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordMD5ExplicitColumnAliasV0Test() {
		final String query = "select a.id as md5, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=md5}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[label, md5]",
				"{}",
				"{accounts={label=[[@7,20:20='b',<459>,1:20]], id=[[@21,76:76='b',<459>,1:76]], enabled=[[@31,104:104='b',<459>,1:104]]}, events={account_id=[[@17,61:61='a',<459>,1:61]], active=[[@25,87:87='a',<459>,1:87]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={label=[[@9,22:26='label',<459>,1:22]], md5=[[@5,15:17='md5',<337>,1:15]]}}",
				"{def_query0={query_dictionary={label=[[@9,22:26='label',<459>,1:22]], md5=[[@5,15:17='md5',<337>,1:15]]}, table_dictionary={accounts={label=[[@7,20:20='b',<459>,1:20]], id=[[@21,76:76='b',<459>,1:76]], enabled=[[@31,104:104='b',<459>,1:104]]}, events={account_id=[[@17,61:61='a',<459>,1:61]], active=[[@25,87:87='a',<459>,1:87]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={label=[{name=label, table_ref=b}], md5=[{name=id, table_ref=a}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordMD5ImplicitColumnAliasV0Test() {
		final String query = "select a.id md5, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=md5}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[label, md5]",
				"{}",
				"{accounts={label=[[@6,17:17='b',<459>,1:17]], id=[[@20,73:73='b',<459>,1:73]], enabled=[[@30,101:101='b',<459>,1:101]]}, events={account_id=[[@16,58:58='a',<459>,1:58]], active=[[@24,84:84='a',<459>,1:84]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={label=[[@8,19:23='label',<459>,1:19]], md5=[[@4,12:14='md5',<337>,1:12]]}}",
				"{def_query0={query_dictionary={label=[[@8,19:23='label',<459>,1:19]], md5=[[@4,12:14='md5',<337>,1:12]]}, table_dictionary={accounts={label=[[@6,17:17='b',<459>,1:17]], id=[[@20,73:73='b',<459>,1:73]], enabled=[[@30,101:101='b',<459>,1:101]]}, events={account_id=[[@16,58:58='a',<459>,1:58]], active=[[@24,84:84='a',<459>,1:84]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={label=[{name=label, table_ref=b}], md5=[{name=id, table_ref=a}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordMD5PhysicalTableV0Test() {
		final String query = "select a.id, b.label from md5 a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=md5}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,66:66='b',<459>,1:66]]}, md5={account_id=[[@15,51:51='a',<459>,1:51]], active=[[@23,77:77='a',<459>,1:77]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,66:66='b',<459>,1:66]]}, md5={account_id=[[@15,51:51='a',<459>,1:51]], active=[[@23,77:77='a',<459>,1:77]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=md5, b=accounts}}}");
	}

	@Test
	public void contextualKeywordMD5QualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.md5 a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=md5}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{analytics.md5={account_id=[[@17,61:61='a',<459>,1:61]], active=[[@25,87:87='a',<459>,1:87]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,76:76='b',<459>,1:76]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={analytics.md5={account_id=[[@17,61:61='a',<459>,1:61]], active=[[@25,87:87='a',<459>,1:87]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,76:76='b',<459>,1:76]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.md5, b=accounts}}}");
	}

	@Test
	public void contextualKeywordMD5ExplicitTableAliasV0Test() {
		final String query = "select md5.id, b.label from events as md5 join accounts b on md5.account_id = b.id where md5.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=md5}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=md5, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=md5}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=md5}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,15:15='b',<459>,1:15]], id=[[@20,78:78='b',<459>,1:78]]}, events={account_id=[[@16,61:63='md5',<337>,1:61]], active=[[@24,89:91='md5',<337>,1:89]], id=[[@1,7:9='md5',<337>,1:7]]}}",
				"{query0={id=[[@3,11:12='id',<459>,1:11]], label=[[@7,17:21='label',<459>,1:17]]}}",
				"{def_query0={query_dictionary={id=[[@3,11:12='id',<459>,1:11]], label=[[@7,17:21='label',<459>,1:17]]}, table_dictionary={accounts={label=[[@5,15:15='b',<459>,1:15]], id=[[@20,78:78='b',<459>,1:78]]}, events={account_id=[[@16,61:63='md5',<337>,1:61]], active=[[@24,89:91='md5',<337>,1:89]], id=[[@1,7:9='md5',<337>,1:7]]}}, filters=[{name=account_id, table_ref=md5}, {name=id, table_ref=b}, {name=active, table_ref=md5}], interface={id=[{name=id, table_ref=md5}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, md5=events}}}");
	}

	@Test
	public void contextualKeywordMD5ImplicitTableAliasV0Test() {
		final String query = "select md5.id, b.label from events md5 join accounts b on md5.account_id = b.id where md5.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=md5}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=md5, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=md5}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=md5}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,15:15='b',<459>,1:15]], id=[[@19,75:75='b',<459>,1:75]]}, events={account_id=[[@15,58:60='md5',<337>,1:58]], active=[[@23,86:88='md5',<337>,1:86]], id=[[@1,7:9='md5',<337>,1:7]]}}",
				"{query0={id=[[@3,11:12='id',<459>,1:11]], label=[[@7,17:21='label',<459>,1:17]]}}",
				"{def_query0={query_dictionary={id=[[@3,11:12='id',<459>,1:11]], label=[[@7,17:21='label',<459>,1:17]]}, table_dictionary={accounts={label=[[@5,15:15='b',<459>,1:15]], id=[[@19,75:75='b',<459>,1:75]]}, events={account_id=[[@15,58:60='md5',<337>,1:58]], active=[[@23,86:88='md5',<337>,1:86]], id=[[@1,7:9='md5',<337>,1:7]]}}, filters=[{name=account_id, table_ref=md5}, {name=id, table_ref=b}, {name=active, table_ref=md5}], interface={id=[{name=id, table_ref=md5}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, md5=events}}}");
	}

	@Test
	public void contextualKeywordMD5CteLabelAndOutputAliasV0Test() {
		final String query = "with md5 as (select a.amount as md5, a.id as row_id from events a where a.active = 1) select md5.md5 as result_value, md5.row_id from md5 where md5.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=md5}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=md5}}, query={select={1={column={name=md5, table_ref=md5}, alias=result_value}, 2={column={name=row_id, table_ref=md5}}}, from={table={alias=null, table=md5}}, where={condition={left={column={name=row_id, table_ref=md5}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,20:20='a',<459>,1:20]], active=[[@20,72:72='a',<459>,1:72]], id=[[@11,37:37='a',<459>,1:37]]}}",
				"{query0={row_id=[[@15,45:50='row_id',<459>,1:45], [@33,118:120='md5',<337>,1:118], [@39,144:146='md5',<337>,1:144]], md5=[[@9,32:34='md5',<337>,1:32], [@27,93:95='md5',<337>,1:93]]}, query1={row_id=[[@35,122:127='row_id',<459>,1:122]], result_value=[[@31,104:115='result_value',<459>,1:104]]}}",
				"{def_query1={context_list={md5=query0}, query_dictionary={result_value=[[@31,104:115='result_value',<459>,1:104]], row_id=[[@35,122:127='row_id',<459>,1:122]]}, def_query0={query_dictionary={row_id=[[@15,45:50='row_id',<459>,1:45], [@33,118:120='md5',<337>,1:118], [@39,144:146='md5',<337>,1:144]], md5=[[@9,32:34='md5',<337>,1:32], [@27,93:95='md5',<337>,1:93]]}, table_dictionary={events={amount=[[@5,20:20='a',<459>,1:20]], active=[[@20,72:72='a',<459>,1:72]], id=[[@11,37:37='a',<459>,1:37]]}}, filters=[{name=active, table_ref=a}], interface={row_id=[{name=id, table_ref=a}], md5=[{name=amount, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=md5}], interface={result_value=[{name=md5, table_ref=md5}], row_id=[{name=row_id, table_ref=md5}]}, table_alias={md5=query0}}}");
	}

	@Test
	public void contextualKeywordMD5WildcardQualifierV0Test() {
		final String query = "select md5.* from events as md5 where md5.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=md5}}}, from={table={alias=md5, table=events}}, where={condition={left={column={name=active, table_ref=md5}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,38:40='md5',<337>,1:38]], *=[[@1,7:9='md5',<337>,1:7]]}}",
				"{query0={*=[[@3,11:11='*',<291>,1:11]]}}",
				"{def_query0={query_dictionary={*=[[@3,11:11='*',<291>,1:11]]}, table_dictionary={events={active=[[@9,38:40='md5',<337>,1:38]], *=[[@1,7:9='md5',<337>,1:7]]}}, filters=[{name=active, table_ref=md5}], interface={*=[{name=*, table_ref=md5}]}, table_alias={md5=events}}}");
	}

	@Test
	public void contextualKeywordREVERSEBareColumnV0Test() {
		final String query = "select reverse as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=reverse, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@9,53:58='active',<459>,1:53]], id=[[@5,32:33='id',<459>,1:32]], reverse=[[@1,7:13='reverse',<338>,1:7]]}}",
				"{query0={result_value=[[@3,18:29='result_value',<459>,1:18]], id=[[@5,32:33='id',<459>,1:32]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,18:29='result_value',<459>,1:18]], id=[[@5,32:33='id',<459>,1:32]]}, table_dictionary={events={active=[[@9,53:58='active',<459>,1:53]], id=[[@5,32:33='id',<459>,1:32]], reverse=[[@1,7:13='reverse',<338>,1:7]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=reverse, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordREVERSEQualifiedColumnV0Test() {
		final String query = "select a.reverse as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=reverse, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,34:34='b',<459>,1:34]], id=[[@21,90:90='b',<459>,1:90]], enabled=[[@31,118:118='b',<459>,1:118]]}, events={account_id=[[@17,75:75='a',<459>,1:75]], active=[[@25,101:101='a',<459>,1:101]], reverse=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@5,20:31='result_value',<459>,1:20]], label=[[@9,36:40='label',<459>,1:36]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,20:31='result_value',<459>,1:20]], label=[[@9,36:40='label',<459>,1:36]]}, table_dictionary={accounts={label=[[@7,34:34='b',<459>,1:34]], id=[[@21,90:90='b',<459>,1:90]], enabled=[[@31,118:118='b',<459>,1:118]]}, events={account_id=[[@17,75:75='a',<459>,1:75]], active=[[@25,101:101='a',<459>,1:101]], reverse=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=reverse, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordREVERSEExplicitColumnAliasV0Test() {
		final String query = "select a.id as reverse, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=reverse}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[label, reverse]",
				"{}",
				"{accounts={label=[[@7,24:24='b',<459>,1:24]], id=[[@21,80:80='b',<459>,1:80]], enabled=[[@31,108:108='b',<459>,1:108]]}, events={account_id=[[@17,65:65='a',<459>,1:65]], active=[[@25,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={reverse=[[@5,15:21='reverse',<338>,1:15]], label=[[@9,26:30='label',<459>,1:26]]}}",
				"{def_query0={query_dictionary={label=[[@9,26:30='label',<459>,1:26]], reverse=[[@5,15:21='reverse',<338>,1:15]]}, table_dictionary={accounts={label=[[@7,24:24='b',<459>,1:24]], id=[[@21,80:80='b',<459>,1:80]], enabled=[[@31,108:108='b',<459>,1:108]]}, events={account_id=[[@17,65:65='a',<459>,1:65]], active=[[@25,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={label=[{name=label, table_ref=b}], reverse=[{name=id, table_ref=a}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordREVERSEImplicitColumnAliasV0Test() {
		final String query = "select a.id reverse, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=reverse}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[label, reverse]",
				"{}",
				"{accounts={label=[[@6,21:21='b',<459>,1:21]], id=[[@20,77:77='b',<459>,1:77]], enabled=[[@30,105:105='b',<459>,1:105]]}, events={account_id=[[@16,62:62='a',<459>,1:62]], active=[[@24,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={reverse=[[@4,12:18='reverse',<338>,1:12]], label=[[@8,23:27='label',<459>,1:23]]}}",
				"{def_query0={query_dictionary={label=[[@8,23:27='label',<459>,1:23]], reverse=[[@4,12:18='reverse',<338>,1:12]]}, table_dictionary={accounts={label=[[@6,21:21='b',<459>,1:21]], id=[[@20,77:77='b',<459>,1:77]], enabled=[[@30,105:105='b',<459>,1:105]]}, events={account_id=[[@16,62:62='a',<459>,1:62]], active=[[@24,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={label=[{name=label, table_ref=b}], reverse=[{name=id, table_ref=a}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordREVERSEPhysicalTableV0Test() {
		final String query = "select a.id, b.label from reverse a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=reverse}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,70:70='b',<459>,1:70]]}, reverse={account_id=[[@15,55:55='a',<459>,1:55]], active=[[@23,81:81='a',<459>,1:81]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,70:70='b',<459>,1:70]]}, reverse={account_id=[[@15,55:55='a',<459>,1:55]], active=[[@23,81:81='a',<459>,1:81]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=reverse, b=accounts}}}");
	}

	@Test
	public void contextualKeywordREVERSEQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.reverse a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=reverse}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{analytics.reverse={account_id=[[@17,65:65='a',<459>,1:65]], active=[[@25,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,80:80='b',<459>,1:80]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={analytics.reverse={account_id=[[@17,65:65='a',<459>,1:65]], active=[[@25,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,80:80='b',<459>,1:80]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.reverse, b=accounts}}}");
	}

	@Test
	public void contextualKeywordREVERSEExplicitTableAliasV0Test() {
		final String query = "select reverse.id, b.label from events as reverse join accounts b on reverse.account_id = b.id where reverse.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=reverse}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=reverse, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=reverse}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=reverse}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,19:19='b',<459>,1:19]], id=[[@20,90:90='b',<459>,1:90]]}, events={account_id=[[@16,69:75='reverse',<338>,1:69]], active=[[@24,101:107='reverse',<338>,1:101]], id=[[@1,7:13='reverse',<338>,1:7]]}}",
				"{query0={id=[[@3,15:16='id',<459>,1:15]], label=[[@7,21:25='label',<459>,1:21]]}}",
				"{def_query0={query_dictionary={id=[[@3,15:16='id',<459>,1:15]], label=[[@7,21:25='label',<459>,1:21]]}, table_dictionary={accounts={label=[[@5,19:19='b',<459>,1:19]], id=[[@20,90:90='b',<459>,1:90]]}, events={account_id=[[@16,69:75='reverse',<338>,1:69]], active=[[@24,101:107='reverse',<338>,1:101]], id=[[@1,7:13='reverse',<338>,1:7]]}}, filters=[{name=account_id, table_ref=reverse}, {name=id, table_ref=b}, {name=active, table_ref=reverse}], interface={id=[{name=id, table_ref=reverse}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, reverse=events}}}");
	}

	@Test
	public void contextualKeywordREVERSEImplicitTableAliasV0Test() {
		final String query = "select reverse.id, b.label from events reverse join accounts b on reverse.account_id = b.id where reverse.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=reverse}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=reverse, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=reverse}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=reverse}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,19:19='b',<459>,1:19]], id=[[@19,87:87='b',<459>,1:87]]}, events={account_id=[[@15,66:72='reverse',<338>,1:66]], active=[[@23,98:104='reverse',<338>,1:98]], id=[[@1,7:13='reverse',<338>,1:7]]}}",
				"{query0={id=[[@3,15:16='id',<459>,1:15]], label=[[@7,21:25='label',<459>,1:21]]}}",
				"{def_query0={query_dictionary={id=[[@3,15:16='id',<459>,1:15]], label=[[@7,21:25='label',<459>,1:21]]}, table_dictionary={accounts={label=[[@5,19:19='b',<459>,1:19]], id=[[@19,87:87='b',<459>,1:87]]}, events={account_id=[[@15,66:72='reverse',<338>,1:66]], active=[[@23,98:104='reverse',<338>,1:98]], id=[[@1,7:13='reverse',<338>,1:7]]}}, filters=[{name=account_id, table_ref=reverse}, {name=id, table_ref=b}, {name=active, table_ref=reverse}], interface={id=[{name=id, table_ref=reverse}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, reverse=events}}}");
	}

	@Test
	public void contextualKeywordREVERSECteLabelAndOutputAliasV0Test() {
		final String query = "with reverse as (select a.amount as reverse, a.id as row_id from events a where a.active = 1) select reverse.reverse as result_value, reverse.row_id from reverse where reverse.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=reverse}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=reverse}}, query={select={1={column={name=reverse, table_ref=reverse}, alias=result_value}, 2={column={name=row_id, table_ref=reverse}}}, from={table={alias=null, table=reverse}}, where={condition={left={column={name=row_id, table_ref=reverse}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,24:24='a',<459>,1:24]], active=[[@20,80:80='a',<459>,1:80]], id=[[@11,45:45='a',<459>,1:45]]}}",
				"{query0={reverse=[[@9,36:42='reverse',<338>,1:36], [@27,101:107='reverse',<338>,1:101]], row_id=[[@15,53:58='row_id',<459>,1:53], [@33,134:140='reverse',<338>,1:134], [@39,168:174='reverse',<338>,1:168]]}, query1={row_id=[[@35,142:147='row_id',<459>,1:142]], result_value=[[@31,120:131='result_value',<459>,1:120]]}}",
				"{def_query1={context_list={reverse=query0}, query_dictionary={result_value=[[@31,120:131='result_value',<459>,1:120]], row_id=[[@35,142:147='row_id',<459>,1:142]]}, def_query0={query_dictionary={reverse=[[@9,36:42='reverse',<338>,1:36], [@27,101:107='reverse',<338>,1:101]], row_id=[[@15,53:58='row_id',<459>,1:53], [@33,134:140='reverse',<338>,1:134], [@39,168:174='reverse',<338>,1:168]]}, table_dictionary={events={amount=[[@5,24:24='a',<459>,1:24]], active=[[@20,80:80='a',<459>,1:80]], id=[[@11,45:45='a',<459>,1:45]]}}, filters=[{name=active, table_ref=a}], interface={reverse=[{name=amount, table_ref=a}], row_id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=reverse}], interface={result_value=[{name=reverse, table_ref=reverse}], row_id=[{name=row_id, table_ref=reverse}]}, table_alias={reverse=query0}}}");
	}

	@Test
	public void contextualKeywordREVERSEWildcardQualifierV0Test() {
		final String query = "select reverse.* from events as reverse where reverse.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=reverse}}}, from={table={alias=reverse, table=events}}, where={condition={left={column={name=active, table_ref=reverse}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,46:52='reverse',<338>,1:46]], *=[[@1,7:13='reverse',<338>,1:7]]}}",
				"{query0={*=[[@3,15:15='*',<291>,1:15]]}}",
				"{def_query0={query_dictionary={*=[[@3,15:15='*',<291>,1:15]]}, table_dictionary={events={active=[[@9,46:52='reverse',<338>,1:46]], *=[[@1,7:13='reverse',<338>,1:7]]}}, filters=[{name=active, table_ref=reverse}], interface={*=[{name=*, table_ref=reverse}]}, table_alias={reverse=events}}}");
	}

	@Test
	public void contextualKeywordQUERYHSTORYBareColumnV0Test() {
		final String query = "select query_hstory as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=query_hstory, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={query_hstory=[[@1,7:18='query_hstory',<347>,1:7]], active=[[@9,58:63='active',<459>,1:58]], id=[[@5,37:38='id',<459>,1:37]]}}",
				"{query0={result_value=[[@3,23:34='result_value',<459>,1:23]], id=[[@5,37:38='id',<459>,1:37]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,23:34='result_value',<459>,1:23]], id=[[@5,37:38='id',<459>,1:37]]}, table_dictionary={events={query_hstory=[[@1,7:18='query_hstory',<347>,1:7]], active=[[@9,58:63='active',<459>,1:58]], id=[[@5,37:38='id',<459>,1:37]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=query_hstory, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordQUERYHSTORYQualifiedColumnV0Test() {
		final String query = "select a.query_hstory as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=query_hstory, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,39:39='b',<459>,1:39]], id=[[@21,95:95='b',<459>,1:95]], enabled=[[@31,123:123='b',<459>,1:123]]}, events={account_id=[[@17,80:80='a',<459>,1:80]], query_hstory=[[@1,7:7='a',<459>,1:7]], active=[[@25,106:106='a',<459>,1:106]]}}",
				"{query0={result_value=[[@5,25:36='result_value',<459>,1:25]], label=[[@9,41:45='label',<459>,1:41]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,25:36='result_value',<459>,1:25]], label=[[@9,41:45='label',<459>,1:41]]}, table_dictionary={accounts={label=[[@7,39:39='b',<459>,1:39]], id=[[@21,95:95='b',<459>,1:95]], enabled=[[@31,123:123='b',<459>,1:123]]}, events={account_id=[[@17,80:80='a',<459>,1:80]], query_hstory=[[@1,7:7='a',<459>,1:7]], active=[[@25,106:106='a',<459>,1:106]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=query_hstory, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordQUERYHSTORYExplicitColumnAliasV0Test() {
		final String query = "select a.id as query_hstory, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=query_hstory}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[query_hstory, label]",
				"{}",
				"{accounts={label=[[@7,29:29='b',<459>,1:29]], id=[[@21,85:85='b',<459>,1:85]], enabled=[[@31,113:113='b',<459>,1:113]]}, events={account_id=[[@17,70:70='a',<459>,1:70]], active=[[@25,96:96='a',<459>,1:96]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={query_hstory=[[@5,15:26='query_hstory',<347>,1:15]], label=[[@9,31:35='label',<459>,1:31]]}}",
				"{def_query0={query_dictionary={query_hstory=[[@5,15:26='query_hstory',<347>,1:15]], label=[[@9,31:35='label',<459>,1:31]]}, table_dictionary={accounts={label=[[@7,29:29='b',<459>,1:29]], id=[[@21,85:85='b',<459>,1:85]], enabled=[[@31,113:113='b',<459>,1:113]]}, events={account_id=[[@17,70:70='a',<459>,1:70]], active=[[@25,96:96='a',<459>,1:96]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={query_hstory=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordQUERYHSTORYImplicitColumnAliasV0Test() {
		final String query = "select a.id query_hstory, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=query_hstory}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[query_hstory, label]",
				"{}",
				"{accounts={label=[[@6,26:26='b',<459>,1:26]], id=[[@20,82:82='b',<459>,1:82]], enabled=[[@30,110:110='b',<459>,1:110]]}, events={account_id=[[@16,67:67='a',<459>,1:67]], active=[[@24,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={query_hstory=[[@4,12:23='query_hstory',<347>,1:12]], label=[[@8,28:32='label',<459>,1:28]]}}",
				"{def_query0={query_dictionary={query_hstory=[[@4,12:23='query_hstory',<347>,1:12]], label=[[@8,28:32='label',<459>,1:28]]}, table_dictionary={accounts={label=[[@6,26:26='b',<459>,1:26]], id=[[@20,82:82='b',<459>,1:82]], enabled=[[@30,110:110='b',<459>,1:110]]}, events={account_id=[[@16,67:67='a',<459>,1:67]], active=[[@24,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={query_hstory=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordQUERYHSTORYPhysicalTableV0Test() {
		final String query = "select a.id, b.label from query_hstory a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=query_hstory}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{query_hstory={account_id=[[@15,60:60='a',<459>,1:60]], active=[[@23,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,75:75='b',<459>,1:75]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={query_hstory={account_id=[[@15,60:60='a',<459>,1:60]], active=[[@23,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,75:75='b',<459>,1:75]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=query_hstory, b=accounts}}}");
	}

	@Test
	public void contextualKeywordQUERYHSTORYQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.query_hstory a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=query_hstory}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,85:85='b',<459>,1:85]]}, analytics.query_hstory={account_id=[[@17,70:70='a',<459>,1:70]], active=[[@25,96:96='a',<459>,1:96]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,85:85='b',<459>,1:85]]}, analytics.query_hstory={account_id=[[@17,70:70='a',<459>,1:70]], active=[[@25,96:96='a',<459>,1:96]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.query_hstory, b=accounts}}}");
	}

	@Test
	public void contextualKeywordQUERYHSTORYExplicitTableAliasV0Test() {
		final String query = "select query_hstory.id, b.label from events as query_hstory join accounts b on query_hstory.account_id = b.id where query_hstory.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=query_hstory}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=query_hstory, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=query_hstory}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=query_hstory}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,24:24='b',<459>,1:24]], id=[[@20,105:105='b',<459>,1:105]]}, events={account_id=[[@16,79:90='query_hstory',<347>,1:79]], active=[[@24,116:127='query_hstory',<347>,1:116]], id=[[@1,7:18='query_hstory',<347>,1:7]]}}",
				"{query0={id=[[@3,20:21='id',<459>,1:20]], label=[[@7,26:30='label',<459>,1:26]]}}",
				"{def_query0={query_dictionary={id=[[@3,20:21='id',<459>,1:20]], label=[[@7,26:30='label',<459>,1:26]]}, table_dictionary={accounts={label=[[@5,24:24='b',<459>,1:24]], id=[[@20,105:105='b',<459>,1:105]]}, events={account_id=[[@16,79:90='query_hstory',<347>,1:79]], active=[[@24,116:127='query_hstory',<347>,1:116]], id=[[@1,7:18='query_hstory',<347>,1:7]]}}, filters=[{name=account_id, table_ref=query_hstory}, {name=id, table_ref=b}, {name=active, table_ref=query_hstory}], interface={id=[{name=id, table_ref=query_hstory}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, query_hstory=events}}}");
	}

	@Test
	public void contextualKeywordQUERYHSTORYImplicitTableAliasV0Test() {
		final String query = "select query_hstory.id, b.label from events query_hstory join accounts b on query_hstory.account_id = b.id where query_hstory.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=query_hstory}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=query_hstory, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=query_hstory}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=query_hstory}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,24:24='b',<459>,1:24]], id=[[@19,102:102='b',<459>,1:102]]}, events={account_id=[[@15,76:87='query_hstory',<347>,1:76]], active=[[@23,113:124='query_hstory',<347>,1:113]], id=[[@1,7:18='query_hstory',<347>,1:7]]}}",
				"{query0={id=[[@3,20:21='id',<459>,1:20]], label=[[@7,26:30='label',<459>,1:26]]}}",
				"{def_query0={query_dictionary={id=[[@3,20:21='id',<459>,1:20]], label=[[@7,26:30='label',<459>,1:26]]}, table_dictionary={accounts={label=[[@5,24:24='b',<459>,1:24]], id=[[@19,102:102='b',<459>,1:102]]}, events={account_id=[[@15,76:87='query_hstory',<347>,1:76]], active=[[@23,113:124='query_hstory',<347>,1:113]], id=[[@1,7:18='query_hstory',<347>,1:7]]}}, filters=[{name=account_id, table_ref=query_hstory}, {name=id, table_ref=b}, {name=active, table_ref=query_hstory}], interface={id=[{name=id, table_ref=query_hstory}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, query_hstory=events}}}");
	}

	@Test
	public void contextualKeywordQUERYHSTORYCteLabelAndOutputAliasV0Test() {
		final String query = "with query_hstory as (select a.amount as query_hstory, a.id as row_id from events a where a.active = 1) select query_hstory.query_hstory as result_value, query_hstory.row_id from query_hstory where query_hstory.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=query_hstory}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=query_hstory}}, query={select={1={column={name=query_hstory, table_ref=query_hstory}, alias=result_value}, 2={column={name=row_id, table_ref=query_hstory}}}, from={table={alias=null, table=query_hstory}}, where={condition={left={column={name=row_id, table_ref=query_hstory}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,29:29='a',<459>,1:29]], active=[[@20,90:90='a',<459>,1:90]], id=[[@11,55:55='a',<459>,1:55]]}}",
				"{query0={row_id=[[@15,63:68='row_id',<459>,1:63], [@33,154:165='query_hstory',<347>,1:154], [@39,198:209='query_hstory',<347>,1:198]], query_hstory=[[@9,41:52='query_hstory',<347>,1:41], [@27,111:122='query_hstory',<347>,1:111]]}, query1={row_id=[[@35,167:172='row_id',<459>,1:167]], result_value=[[@31,140:151='result_value',<459>,1:140]]}}",
				"{def_query1={context_list={query_hstory=query0}, query_dictionary={result_value=[[@31,140:151='result_value',<459>,1:140]], row_id=[[@35,167:172='row_id',<459>,1:167]]}, def_query0={query_dictionary={query_hstory=[[@9,41:52='query_hstory',<347>,1:41], [@27,111:122='query_hstory',<347>,1:111]], row_id=[[@15,63:68='row_id',<459>,1:63], [@33,154:165='query_hstory',<347>,1:154], [@39,198:209='query_hstory',<347>,1:198]]}, table_dictionary={events={amount=[[@5,29:29='a',<459>,1:29]], active=[[@20,90:90='a',<459>,1:90]], id=[[@11,55:55='a',<459>,1:55]]}}, filters=[{name=active, table_ref=a}], interface={query_hstory=[{name=amount, table_ref=a}], row_id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=query_hstory}], interface={result_value=[{name=query_hstory, table_ref=query_hstory}], row_id=[{name=row_id, table_ref=query_hstory}]}, table_alias={query_hstory=query0}}}");
	}

	@Test
	public void contextualKeywordQUERYHSTORYWildcardQualifierV0Test() {
		final String query = "select query_hstory.* from events as query_hstory where query_hstory.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=query_hstory}}}, from={table={alias=query_hstory, table=events}}, where={condition={left={column={name=active, table_ref=query_hstory}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,56:67='query_hstory',<347>,1:56]], *=[[@1,7:18='query_hstory',<347>,1:7]]}}",
				"{query0={*=[[@3,20:20='*',<291>,1:20]]}}",
				"{def_query0={query_dictionary={*=[[@3,20:20='*',<291>,1:20]]}, table_dictionary={events={active=[[@9,56:67='query_hstory',<347>,1:56]], *=[[@1,7:18='query_hstory',<347>,1:7]]}}, filters=[{name=active, table_ref=query_hstory}], interface={*=[{name=*, table_ref=query_hstory}]}, table_alias={query_hstory=events}}}");
	}

	@Test
	public void contextualKeywordRECURSIVEBareColumnV0Test() {
		final String query = "select recursive as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=recursive, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@9,55:60='active',<459>,1:55]], id=[[@5,34:35='id',<459>,1:34]], recursive=[[@1,7:15='recursive',<383>,1:7]]}}",
				"{query0={result_value=[[@3,20:31='result_value',<459>,1:20]], id=[[@5,34:35='id',<459>,1:34]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,20:31='result_value',<459>,1:20]], id=[[@5,34:35='id',<459>,1:34]]}, table_dictionary={events={active=[[@9,55:60='active',<459>,1:55]], id=[[@5,34:35='id',<459>,1:34]], recursive=[[@1,7:15='recursive',<383>,1:7]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=recursive, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordRECURSIVEQualifiedColumnV0Test() {
		final String query = "select a.recursive as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=recursive, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,36:36='b',<459>,1:36]], id=[[@21,92:92='b',<459>,1:92]], enabled=[[@31,120:120='b',<459>,1:120]]}, events={account_id=[[@17,77:77='a',<459>,1:77]], active=[[@25,103:103='a',<459>,1:103]], recursive=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@5,22:33='result_value',<459>,1:22]], label=[[@9,38:42='label',<459>,1:38]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,22:33='result_value',<459>,1:22]], label=[[@9,38:42='label',<459>,1:38]]}, table_dictionary={accounts={label=[[@7,36:36='b',<459>,1:36]], id=[[@21,92:92='b',<459>,1:92]], enabled=[[@31,120:120='b',<459>,1:120]]}, events={account_id=[[@17,77:77='a',<459>,1:77]], active=[[@25,103:103='a',<459>,1:103]], recursive=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=recursive, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordRECURSIVEExplicitColumnAliasV0Test() {
		final String query = "select a.id as recursive, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=recursive}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[label, recursive]",
				"{}",
				"{accounts={label=[[@7,26:26='b',<459>,1:26]], id=[[@21,82:82='b',<459>,1:82]], enabled=[[@31,110:110='b',<459>,1:110]]}, events={account_id=[[@17,67:67='a',<459>,1:67]], active=[[@25,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={recursive=[[@5,15:23='recursive',<383>,1:15]], label=[[@9,28:32='label',<459>,1:28]]}}",
				"{def_query0={query_dictionary={label=[[@9,28:32='label',<459>,1:28]], recursive=[[@5,15:23='recursive',<383>,1:15]]}, table_dictionary={accounts={label=[[@7,26:26='b',<459>,1:26]], id=[[@21,82:82='b',<459>,1:82]], enabled=[[@31,110:110='b',<459>,1:110]]}, events={account_id=[[@17,67:67='a',<459>,1:67]], active=[[@25,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={label=[{name=label, table_ref=b}], recursive=[{name=id, table_ref=a}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordRECURSIVEImplicitColumnAliasV0Test() {
		final String query = "select a.id recursive, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=recursive}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[label, recursive]",
				"{}",
				"{accounts={label=[[@6,23:23='b',<459>,1:23]], id=[[@20,79:79='b',<459>,1:79]], enabled=[[@30,107:107='b',<459>,1:107]]}, events={account_id=[[@16,64:64='a',<459>,1:64]], active=[[@24,90:90='a',<459>,1:90]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={recursive=[[@4,12:20='recursive',<383>,1:12]], label=[[@8,25:29='label',<459>,1:25]]}}",
				"{def_query0={query_dictionary={label=[[@8,25:29='label',<459>,1:25]], recursive=[[@4,12:20='recursive',<383>,1:12]]}, table_dictionary={accounts={label=[[@6,23:23='b',<459>,1:23]], id=[[@20,79:79='b',<459>,1:79]], enabled=[[@30,107:107='b',<459>,1:107]]}, events={account_id=[[@16,64:64='a',<459>,1:64]], active=[[@24,90:90='a',<459>,1:90]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={label=[{name=label, table_ref=b}], recursive=[{name=id, table_ref=a}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordRECURSIVEPhysicalTableV0Test() {
		final String query = "select a.id, b.label from recursive a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=recursive}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,72:72='b',<459>,1:72]]}, recursive={account_id=[[@15,57:57='a',<459>,1:57]], active=[[@23,83:83='a',<459>,1:83]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,72:72='b',<459>,1:72]]}, recursive={account_id=[[@15,57:57='a',<459>,1:57]], active=[[@23,83:83='a',<459>,1:83]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=recursive, b=accounts}}}");
	}

	@Test
	public void contextualKeywordRECURSIVEQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.recursive a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=recursive}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,82:82='b',<459>,1:82]]}, analytics.recursive={account_id=[[@17,67:67='a',<459>,1:67]], active=[[@25,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,82:82='b',<459>,1:82]]}, analytics.recursive={account_id=[[@17,67:67='a',<459>,1:67]], active=[[@25,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.recursive, b=accounts}}}");
	}

	@Test
	public void contextualKeywordRECURSIVEExplicitTableAliasV0Test() {
		final String query = "select recursive.id, b.label from events as recursive join accounts b on recursive.account_id = b.id where recursive.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=recursive}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=recursive, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=recursive}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=recursive}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,21:21='b',<459>,1:21]], id=[[@20,96:96='b',<459>,1:96]]}, events={account_id=[[@16,73:81='recursive',<383>,1:73]], active=[[@24,107:115='recursive',<383>,1:107]], id=[[@1,7:15='recursive',<383>,1:7]]}}",
				"{query0={id=[[@3,17:18='id',<459>,1:17]], label=[[@7,23:27='label',<459>,1:23]]}}",
				"{def_query0={query_dictionary={id=[[@3,17:18='id',<459>,1:17]], label=[[@7,23:27='label',<459>,1:23]]}, table_dictionary={accounts={label=[[@5,21:21='b',<459>,1:21]], id=[[@20,96:96='b',<459>,1:96]]}, events={account_id=[[@16,73:81='recursive',<383>,1:73]], active=[[@24,107:115='recursive',<383>,1:107]], id=[[@1,7:15='recursive',<383>,1:7]]}}, filters=[{name=account_id, table_ref=recursive}, {name=id, table_ref=b}, {name=active, table_ref=recursive}], interface={id=[{name=id, table_ref=recursive}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, recursive=events}}}");
	}

	@Test
	public void contextualKeywordRECURSIVEImplicitTableAliasV0Test() {
		final String query = "select recursive.id, b.label from events recursive join accounts b on recursive.account_id = b.id where recursive.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=recursive}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=recursive, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=recursive}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=recursive}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,21:21='b',<459>,1:21]], id=[[@19,93:93='b',<459>,1:93]]}, events={account_id=[[@15,70:78='recursive',<383>,1:70]], active=[[@23,104:112='recursive',<383>,1:104]], id=[[@1,7:15='recursive',<383>,1:7]]}}",
				"{query0={id=[[@3,17:18='id',<459>,1:17]], label=[[@7,23:27='label',<459>,1:23]]}}",
				"{def_query0={query_dictionary={id=[[@3,17:18='id',<459>,1:17]], label=[[@7,23:27='label',<459>,1:23]]}, table_dictionary={accounts={label=[[@5,21:21='b',<459>,1:21]], id=[[@19,93:93='b',<459>,1:93]]}, events={account_id=[[@15,70:78='recursive',<383>,1:70]], active=[[@23,104:112='recursive',<383>,1:104]], id=[[@1,7:15='recursive',<383>,1:7]]}}, filters=[{name=account_id, table_ref=recursive}, {name=id, table_ref=b}, {name=active, table_ref=recursive}], interface={id=[{name=id, table_ref=recursive}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, recursive=events}}}");
	}

	@Test
	public void contextualKeywordRECURSIVECteLabelAndOutputAliasV0Test() {
		final String query = "with recursive as (select a.amount as recursive, a.id as row_id from events a where a.active = 1) select recursive.recursive as result_value, recursive.row_id from recursive where recursive.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=recursive}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=recursive}}, query={select={1={column={name=recursive, table_ref=recursive}, alias=result_value}, 2={column={name=row_id, table_ref=recursive}}}, from={table={alias=null, table=recursive}}, where={condition={left={column={name=row_id, table_ref=recursive}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,26:26='a',<459>,1:26]], active=[[@20,84:84='a',<459>,1:84]], id=[[@11,49:49='a',<459>,1:49]]}}",
				"{query0={row_id=[[@15,57:62='row_id',<459>,1:57], [@33,142:150='recursive',<383>,1:142], [@39,180:188='recursive',<383>,1:180]], recursive=[[@9,38:46='recursive',<383>,1:38], [@27,105:113='recursive',<383>,1:105]]}, query1={row_id=[[@35,152:157='row_id',<459>,1:152]], result_value=[[@31,128:139='result_value',<459>,1:128]]}}",
				"{def_query1={context_list={recursive=query0}, query_dictionary={result_value=[[@31,128:139='result_value',<459>,1:128]], row_id=[[@35,152:157='row_id',<459>,1:152]]}, def_query0={query_dictionary={row_id=[[@15,57:62='row_id',<459>,1:57], [@33,142:150='recursive',<383>,1:142], [@39,180:188='recursive',<383>,1:180]], recursive=[[@9,38:46='recursive',<383>,1:38], [@27,105:113='recursive',<383>,1:105]]}, table_dictionary={events={amount=[[@5,26:26='a',<459>,1:26]], active=[[@20,84:84='a',<459>,1:84]], id=[[@11,49:49='a',<459>,1:49]]}}, filters=[{name=active, table_ref=a}], interface={row_id=[{name=id, table_ref=a}], recursive=[{name=amount, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=recursive}], interface={result_value=[{name=recursive, table_ref=recursive}], row_id=[{name=row_id, table_ref=recursive}]}, table_alias={recursive=query0}}}");
	}

	@Test
	public void contextualKeywordRECURSIVEWildcardQualifierV0Test() {
		final String query = "select recursive.* from events as recursive where recursive.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=recursive}}}, from={table={alias=recursive, table=events}}, where={condition={left={column={name=active, table_ref=recursive}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,50:58='recursive',<383>,1:50]], *=[[@1,7:15='recursive',<383>,1:7]]}}",
				"{query0={*=[[@3,17:17='*',<291>,1:17]]}}",
				"{def_query0={query_dictionary={*=[[@3,17:17='*',<291>,1:17]]}, table_dictionary={events={active=[[@9,50:58='recursive',<383>,1:50]], *=[[@1,7:15='recursive',<383>,1:7]]}}, filters=[{name=active, table_ref=recursive}], interface={*=[{name=*, table_ref=recursive}]}, table_alias={recursive=events}}}");
	}

	@Test
	public void contextualKeywordMODEBareColumnV0Test() {
		final String query = "select mode as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=mode, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={mode=[[@1,7:10='mode',<384>,1:7]], active=[[@9,50:55='active',<459>,1:50]], id=[[@5,29:30='id',<459>,1:29]]}}",
				"{query0={result_value=[[@3,15:26='result_value',<459>,1:15]], id=[[@5,29:30='id',<459>,1:29]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,15:26='result_value',<459>,1:15]], id=[[@5,29:30='id',<459>,1:29]]}, table_dictionary={events={mode=[[@1,7:10='mode',<384>,1:7]], active=[[@9,50:55='active',<459>,1:50]], id=[[@5,29:30='id',<459>,1:29]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=mode, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordMODEQualifiedColumnV0Test() {
		final String query = "select a.mode as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=mode, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,31:31='b',<459>,1:31]], id=[[@21,87:87='b',<459>,1:87]], enabled=[[@31,115:115='b',<459>,1:115]]}, events={mode=[[@1,7:7='a',<459>,1:7]], account_id=[[@17,72:72='a',<459>,1:72]], active=[[@25,98:98='a',<459>,1:98]]}}",
				"{query0={result_value=[[@5,17:28='result_value',<459>,1:17]], label=[[@9,33:37='label',<459>,1:33]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,17:28='result_value',<459>,1:17]], label=[[@9,33:37='label',<459>,1:33]]}, table_dictionary={accounts={label=[[@7,31:31='b',<459>,1:31]], id=[[@21,87:87='b',<459>,1:87]], enabled=[[@31,115:115='b',<459>,1:115]]}, events={mode=[[@1,7:7='a',<459>,1:7]], account_id=[[@17,72:72='a',<459>,1:72]], active=[[@25,98:98='a',<459>,1:98]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=mode, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordMODEExplicitColumnAliasV0Test() {
		final String query = "select a.id as mode, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=mode}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[mode, label]",
				"{}",
				"{accounts={label=[[@7,21:21='b',<459>,1:21]], id=[[@21,77:77='b',<459>,1:77]], enabled=[[@31,105:105='b',<459>,1:105]]}, events={account_id=[[@17,62:62='a',<459>,1:62]], active=[[@25,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={mode=[[@5,15:18='mode',<384>,1:15]], label=[[@9,23:27='label',<459>,1:23]]}}",
				"{def_query0={query_dictionary={mode=[[@5,15:18='mode',<384>,1:15]], label=[[@9,23:27='label',<459>,1:23]]}, table_dictionary={accounts={label=[[@7,21:21='b',<459>,1:21]], id=[[@21,77:77='b',<459>,1:77]], enabled=[[@31,105:105='b',<459>,1:105]]}, events={account_id=[[@17,62:62='a',<459>,1:62]], active=[[@25,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={mode=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordMODEImplicitColumnAliasV0Test() {
		final String query = "select a.id mode, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=mode}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[mode, label]",
				"{}",
				"{accounts={label=[[@6,18:18='b',<459>,1:18]], id=[[@20,74:74='b',<459>,1:74]], enabled=[[@30,102:102='b',<459>,1:102]]}, events={account_id=[[@16,59:59='a',<459>,1:59]], active=[[@24,85:85='a',<459>,1:85]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={mode=[[@4,12:15='mode',<384>,1:12]], label=[[@8,20:24='label',<459>,1:20]]}}",
				"{def_query0={query_dictionary={mode=[[@4,12:15='mode',<384>,1:12]], label=[[@8,20:24='label',<459>,1:20]]}, table_dictionary={accounts={label=[[@6,18:18='b',<459>,1:18]], id=[[@20,74:74='b',<459>,1:74]], enabled=[[@30,102:102='b',<459>,1:102]]}, events={account_id=[[@16,59:59='a',<459>,1:59]], active=[[@24,85:85='a',<459>,1:85]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={mode=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordMODEPhysicalTableV0Test() {
		final String query = "select a.id, b.label from mode a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=mode}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{mode={account_id=[[@15,52:52='a',<459>,1:52]], active=[[@23,78:78='a',<459>,1:78]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,67:67='b',<459>,1:67]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={mode={account_id=[[@15,52:52='a',<459>,1:52]], active=[[@23,78:78='a',<459>,1:78]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,67:67='b',<459>,1:67]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=mode, b=accounts}}}");
	}

	@Test
	public void contextualKeywordMODEQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.mode a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=mode}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,77:77='b',<459>,1:77]]}, analytics.mode={account_id=[[@17,62:62='a',<459>,1:62]], active=[[@25,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,77:77='b',<459>,1:77]]}, analytics.mode={account_id=[[@17,62:62='a',<459>,1:62]], active=[[@25,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.mode, b=accounts}}}");
	}

	@Test
	public void contextualKeywordMODEExplicitTableAliasV0Test() {
		final String query = "select mode.id, b.label from events as mode join accounts b on mode.account_id = b.id where mode.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=mode}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=mode, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=mode}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=mode}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,16:16='b',<459>,1:16]], id=[[@20,81:81='b',<459>,1:81]]}, events={account_id=[[@16,63:66='mode',<384>,1:63]], active=[[@24,92:95='mode',<384>,1:92]], id=[[@1,7:10='mode',<384>,1:7]]}}",
				"{query0={id=[[@3,12:13='id',<459>,1:12]], label=[[@7,18:22='label',<459>,1:18]]}}",
				"{def_query0={query_dictionary={id=[[@3,12:13='id',<459>,1:12]], label=[[@7,18:22='label',<459>,1:18]]}, table_dictionary={accounts={label=[[@5,16:16='b',<459>,1:16]], id=[[@20,81:81='b',<459>,1:81]]}, events={account_id=[[@16,63:66='mode',<384>,1:63]], active=[[@24,92:95='mode',<384>,1:92]], id=[[@1,7:10='mode',<384>,1:7]]}}, filters=[{name=account_id, table_ref=mode}, {name=id, table_ref=b}, {name=active, table_ref=mode}], interface={id=[{name=id, table_ref=mode}], label=[{name=label, table_ref=b}]}, table_alias={mode=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordMODEImplicitTableAliasV0Test() {
		final String query = "select mode.id, b.label from events mode join accounts b on mode.account_id = b.id where mode.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=mode}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=mode, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=mode}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=mode}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,16:16='b',<459>,1:16]], id=[[@19,78:78='b',<459>,1:78]]}, events={account_id=[[@15,60:63='mode',<384>,1:60]], active=[[@23,89:92='mode',<384>,1:89]], id=[[@1,7:10='mode',<384>,1:7]]}}",
				"{query0={id=[[@3,12:13='id',<459>,1:12]], label=[[@7,18:22='label',<459>,1:18]]}}",
				"{def_query0={query_dictionary={id=[[@3,12:13='id',<459>,1:12]], label=[[@7,18:22='label',<459>,1:18]]}, table_dictionary={accounts={label=[[@5,16:16='b',<459>,1:16]], id=[[@19,78:78='b',<459>,1:78]]}, events={account_id=[[@15,60:63='mode',<384>,1:60]], active=[[@23,89:92='mode',<384>,1:89]], id=[[@1,7:10='mode',<384>,1:7]]}}, filters=[{name=account_id, table_ref=mode}, {name=id, table_ref=b}, {name=active, table_ref=mode}], interface={id=[{name=id, table_ref=mode}], label=[{name=label, table_ref=b}]}, table_alias={mode=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordMODECteLabelAndOutputAliasV0Test() {
		final String query = "with mode as (select a.amount as mode, a.id as row_id from events a where a.active = 1) select mode.mode as result_value, mode.row_id from mode where mode.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=mode}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=mode}}, query={select={1={column={name=mode, table_ref=mode}, alias=result_value}, 2={column={name=row_id, table_ref=mode}}}, from={table={alias=null, table=mode}}, where={condition={left={column={name=row_id, table_ref=mode}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,21:21='a',<459>,1:21]], active=[[@20,74:74='a',<459>,1:74]], id=[[@11,39:39='a',<459>,1:39]]}}",
				"{query0={mode=[[@9,33:36='mode',<384>,1:33], [@27,95:98='mode',<384>,1:95]], row_id=[[@15,47:52='row_id',<459>,1:47], [@33,122:125='mode',<384>,1:122], [@39,150:153='mode',<384>,1:150]]}, query1={row_id=[[@35,127:132='row_id',<459>,1:127]], result_value=[[@31,108:119='result_value',<459>,1:108]]}}",
				"{def_query1={context_list={mode=query0}, query_dictionary={result_value=[[@31,108:119='result_value',<459>,1:108]], row_id=[[@35,127:132='row_id',<459>,1:127]]}, def_query0={query_dictionary={mode=[[@9,33:36='mode',<384>,1:33], [@27,95:98='mode',<384>,1:95]], row_id=[[@15,47:52='row_id',<459>,1:47], [@33,122:125='mode',<384>,1:122], [@39,150:153='mode',<384>,1:150]]}, table_dictionary={events={amount=[[@5,21:21='a',<459>,1:21]], active=[[@20,74:74='a',<459>,1:74]], id=[[@11,39:39='a',<459>,1:39]]}}, filters=[{name=active, table_ref=a}], interface={mode=[{name=amount, table_ref=a}], row_id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=mode}], interface={result_value=[{name=mode, table_ref=mode}], row_id=[{name=row_id, table_ref=mode}]}, table_alias={mode=query0}}}");
	}

	@Test
	public void contextualKeywordMODEWildcardQualifierV0Test() {
		final String query = "select mode.* from events as mode where mode.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=mode}}}, from={table={alias=mode, table=events}}, where={condition={left={column={name=active, table_ref=mode}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,40:43='mode',<384>,1:40]], *=[[@1,7:10='mode',<384>,1:7]]}}",
				"{query0={*=[[@3,12:12='*',<291>,1:12]]}}",
				"{def_query0={query_dictionary={*=[[@3,12:12='*',<291>,1:12]]}, table_dictionary={events={active=[[@9,40:43='mode',<384>,1:40]], *=[[@1,7:10='mode',<384>,1:7]]}}, filters=[{name=active, table_ref=mode}], interface={*=[{name=*, table_ref=mode}]}, table_alias={mode=events}}}");
	}

	@Test
	public void contextualKeywordLATERALBareColumnV0Test() {
		final String query = "select lateral as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=lateral, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@9,53:58='active',<459>,1:53]], id=[[@5,32:33='id',<459>,1:32]], lateral=[[@1,7:13='lateral',<385>,1:7]]}}",
				"{query0={result_value=[[@3,18:29='result_value',<459>,1:18]], id=[[@5,32:33='id',<459>,1:32]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,18:29='result_value',<459>,1:18]], id=[[@5,32:33='id',<459>,1:32]]}, table_dictionary={events={active=[[@9,53:58='active',<459>,1:53]], id=[[@5,32:33='id',<459>,1:32]], lateral=[[@1,7:13='lateral',<385>,1:7]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=lateral, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordLATERALQualifiedColumnV0Test() {
		final String query = "select a.lateral as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=lateral, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,34:34='b',<459>,1:34]], id=[[@21,90:90='b',<459>,1:90]], enabled=[[@31,118:118='b',<459>,1:118]]}, events={account_id=[[@17,75:75='a',<459>,1:75]], active=[[@25,101:101='a',<459>,1:101]], lateral=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@5,20:31='result_value',<459>,1:20]], label=[[@9,36:40='label',<459>,1:36]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,20:31='result_value',<459>,1:20]], label=[[@9,36:40='label',<459>,1:36]]}, table_dictionary={accounts={label=[[@7,34:34='b',<459>,1:34]], id=[[@21,90:90='b',<459>,1:90]], enabled=[[@31,118:118='b',<459>,1:118]]}, events={account_id=[[@17,75:75='a',<459>,1:75]], active=[[@25,101:101='a',<459>,1:101]], lateral=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=lateral, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordLATERALExplicitColumnAliasV0Test() {
		final String query = "select a.id as lateral, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=lateral}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[label, lateral]",
				"{}",
				"{accounts={label=[[@7,24:24='b',<459>,1:24]], id=[[@21,80:80='b',<459>,1:80]], enabled=[[@31,108:108='b',<459>,1:108]]}, events={account_id=[[@17,65:65='a',<459>,1:65]], active=[[@25,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={lateral=[[@5,15:21='lateral',<385>,1:15]], label=[[@9,26:30='label',<459>,1:26]]}}",
				"{def_query0={query_dictionary={label=[[@9,26:30='label',<459>,1:26]], lateral=[[@5,15:21='lateral',<385>,1:15]]}, table_dictionary={accounts={label=[[@7,24:24='b',<459>,1:24]], id=[[@21,80:80='b',<459>,1:80]], enabled=[[@31,108:108='b',<459>,1:108]]}, events={account_id=[[@17,65:65='a',<459>,1:65]], active=[[@25,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={label=[{name=label, table_ref=b}], lateral=[{name=id, table_ref=a}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordLATERALImplicitColumnAliasV0Test() {
		final String query = "select a.id lateral, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=lateral}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[label, lateral]",
				"{}",
				"{accounts={label=[[@6,21:21='b',<459>,1:21]], id=[[@20,77:77='b',<459>,1:77]], enabled=[[@30,105:105='b',<459>,1:105]]}, events={account_id=[[@16,62:62='a',<459>,1:62]], active=[[@24,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={lateral=[[@4,12:18='lateral',<385>,1:12]], label=[[@8,23:27='label',<459>,1:23]]}}",
				"{def_query0={query_dictionary={label=[[@8,23:27='label',<459>,1:23]], lateral=[[@4,12:18='lateral',<385>,1:12]]}, table_dictionary={accounts={label=[[@6,21:21='b',<459>,1:21]], id=[[@20,77:77='b',<459>,1:77]], enabled=[[@30,105:105='b',<459>,1:105]]}, events={account_id=[[@16,62:62='a',<459>,1:62]], active=[[@24,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={label=[{name=label, table_ref=b}], lateral=[{name=id, table_ref=a}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordLATERALPhysicalTableV0Test() {
		final String query = "select a.id, b.label from lateral a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=lateral}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,70:70='b',<459>,1:70]]}, lateral={account_id=[[@15,55:55='a',<459>,1:55]], active=[[@23,81:81='a',<459>,1:81]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,70:70='b',<459>,1:70]]}, lateral={account_id=[[@15,55:55='a',<459>,1:55]], active=[[@23,81:81='a',<459>,1:81]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=lateral, b=accounts}}}");
	}

	@Test
	public void contextualKeywordLATERALQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.lateral a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=lateral}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{analytics.lateral={account_id=[[@17,65:65='a',<459>,1:65]], active=[[@25,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,80:80='b',<459>,1:80]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={analytics.lateral={account_id=[[@17,65:65='a',<459>,1:65]], active=[[@25,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,80:80='b',<459>,1:80]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.lateral, b=accounts}}}");
	}

	@Test
	public void contextualKeywordLATERALExplicitTableAliasV0Test() {
		final String query = "select lateral.id, b.label from events as lateral join accounts b on lateral.account_id = b.id where lateral.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=lateral}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=lateral, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=lateral}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=lateral}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,19:19='b',<459>,1:19]], id=[[@20,90:90='b',<459>,1:90]]}, events={account_id=[[@16,69:75='lateral',<385>,1:69]], active=[[@24,101:107='lateral',<385>,1:101]], id=[[@1,7:13='lateral',<385>,1:7]]}}",
				"{query0={id=[[@3,15:16='id',<459>,1:15]], label=[[@7,21:25='label',<459>,1:21]]}}",
				"{def_query0={query_dictionary={id=[[@3,15:16='id',<459>,1:15]], label=[[@7,21:25='label',<459>,1:21]]}, table_dictionary={accounts={label=[[@5,19:19='b',<459>,1:19]], id=[[@20,90:90='b',<459>,1:90]]}, events={account_id=[[@16,69:75='lateral',<385>,1:69]], active=[[@24,101:107='lateral',<385>,1:101]], id=[[@1,7:13='lateral',<385>,1:7]]}}, filters=[{name=account_id, table_ref=lateral}, {name=id, table_ref=b}, {name=active, table_ref=lateral}], interface={id=[{name=id, table_ref=lateral}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, lateral=events}}}");
	}

	@Test
	public void contextualKeywordLATERALImplicitTableAliasV0Test() {
		final String query = "select lateral.id, b.label from events lateral join accounts b on lateral.account_id = b.id where lateral.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=lateral}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=lateral, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=lateral}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=lateral}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,19:19='b',<459>,1:19]], id=[[@19,87:87='b',<459>,1:87]]}, events={account_id=[[@15,66:72='lateral',<385>,1:66]], active=[[@23,98:104='lateral',<385>,1:98]], id=[[@1,7:13='lateral',<385>,1:7]]}}",
				"{query0={id=[[@3,15:16='id',<459>,1:15]], label=[[@7,21:25='label',<459>,1:21]]}}",
				"{def_query0={query_dictionary={id=[[@3,15:16='id',<459>,1:15]], label=[[@7,21:25='label',<459>,1:21]]}, table_dictionary={accounts={label=[[@5,19:19='b',<459>,1:19]], id=[[@19,87:87='b',<459>,1:87]]}, events={account_id=[[@15,66:72='lateral',<385>,1:66]], active=[[@23,98:104='lateral',<385>,1:98]], id=[[@1,7:13='lateral',<385>,1:7]]}}, filters=[{name=account_id, table_ref=lateral}, {name=id, table_ref=b}, {name=active, table_ref=lateral}], interface={id=[{name=id, table_ref=lateral}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, lateral=events}}}");
	}

	@Test
	public void contextualKeywordLATERALCteLabelAndOutputAliasV0Test() {
		final String query = "with lateral as (select a.amount as lateral, a.id as row_id from events a where a.active = 1) select lateral.lateral as result_value, lateral.row_id from lateral where lateral.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=lateral}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=lateral}}, query={select={1={column={name=lateral, table_ref=lateral}, alias=result_value}, 2={column={name=row_id, table_ref=lateral}}}, from={table={alias=null, table=lateral}}, where={condition={left={column={name=row_id, table_ref=lateral}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,24:24='a',<459>,1:24]], active=[[@20,80:80='a',<459>,1:80]], id=[[@11,45:45='a',<459>,1:45]]}}",
				"{query0={row_id=[[@15,53:58='row_id',<459>,1:53], [@33,134:140='lateral',<385>,1:134], [@39,168:174='lateral',<385>,1:168]], lateral=[[@9,36:42='lateral',<385>,1:36], [@27,101:107='lateral',<385>,1:101]]}, query1={row_id=[[@35,142:147='row_id',<459>,1:142]], result_value=[[@31,120:131='result_value',<459>,1:120]]}}",
				"{def_query1={context_list={lateral=query0}, query_dictionary={result_value=[[@31,120:131='result_value',<459>,1:120]], row_id=[[@35,142:147='row_id',<459>,1:142]]}, def_query0={query_dictionary={row_id=[[@15,53:58='row_id',<459>,1:53], [@33,134:140='lateral',<385>,1:134], [@39,168:174='lateral',<385>,1:168]], lateral=[[@9,36:42='lateral',<385>,1:36], [@27,101:107='lateral',<385>,1:101]]}, table_dictionary={events={amount=[[@5,24:24='a',<459>,1:24]], active=[[@20,80:80='a',<459>,1:80]], id=[[@11,45:45='a',<459>,1:45]]}}, filters=[{name=active, table_ref=a}], interface={row_id=[{name=id, table_ref=a}], lateral=[{name=amount, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=lateral}], interface={result_value=[{name=lateral, table_ref=lateral}], row_id=[{name=row_id, table_ref=lateral}]}, table_alias={lateral=query0}}}");
	}

	@Test
	public void contextualKeywordLATERALWildcardQualifierV0Test() {
		final String query = "select lateral.* from events as lateral where lateral.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=lateral}}}, from={table={alias=lateral, table=events}}, where={condition={left={column={name=active, table_ref=lateral}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,46:52='lateral',<385>,1:46]], *=[[@1,7:13='lateral',<385>,1:7]]}}",
				"{query0={*=[[@3,15:15='*',<291>,1:15]]}}",
				"{def_query0={query_dictionary={*=[[@3,15:15='*',<291>,1:15]]}, table_dictionary={events={active=[[@9,46:52='lateral',<385>,1:46]], *=[[@1,7:13='lateral',<385>,1:7]]}}, filters=[{name=active, table_ref=lateral}], interface={*=[{name=*, table_ref=lateral}]}, table_alias={lateral=events}}}");
	}

	@Test
	public void contextualKeywordROWCOUNTBareColumnV0Test() {
		final String query = "select rowcount as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=rowcount, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={rowcount=[[@1,7:14='rowcount',<386>,1:7]], active=[[@9,54:59='active',<459>,1:54]], id=[[@5,33:34='id',<459>,1:33]]}}",
				"{query0={result_value=[[@3,19:30='result_value',<459>,1:19]], id=[[@5,33:34='id',<459>,1:33]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,19:30='result_value',<459>,1:19]], id=[[@5,33:34='id',<459>,1:33]]}, table_dictionary={events={rowcount=[[@1,7:14='rowcount',<386>,1:7]], active=[[@9,54:59='active',<459>,1:54]], id=[[@5,33:34='id',<459>,1:33]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=rowcount, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordROWCOUNTQualifiedColumnV0Test() {
		final String query = "select a.rowcount as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=rowcount, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,35:35='b',<459>,1:35]], id=[[@21,91:91='b',<459>,1:91]], enabled=[[@31,119:119='b',<459>,1:119]]}, events={account_id=[[@17,76:76='a',<459>,1:76]], rowcount=[[@1,7:7='a',<459>,1:7]], active=[[@25,102:102='a',<459>,1:102]]}}",
				"{query0={result_value=[[@5,21:32='result_value',<459>,1:21]], label=[[@9,37:41='label',<459>,1:37]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,21:32='result_value',<459>,1:21]], label=[[@9,37:41='label',<459>,1:37]]}, table_dictionary={accounts={label=[[@7,35:35='b',<459>,1:35]], id=[[@21,91:91='b',<459>,1:91]], enabled=[[@31,119:119='b',<459>,1:119]]}, events={account_id=[[@17,76:76='a',<459>,1:76]], rowcount=[[@1,7:7='a',<459>,1:7]], active=[[@25,102:102='a',<459>,1:102]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=rowcount, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordROWCOUNTExplicitColumnAliasV0Test() {
		final String query = "select a.id as rowcount, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=rowcount}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[rowcount, label]",
				"{}",
				"{accounts={label=[[@7,25:25='b',<459>,1:25]], id=[[@21,81:81='b',<459>,1:81]], enabled=[[@31,109:109='b',<459>,1:109]]}, events={account_id=[[@17,66:66='a',<459>,1:66]], active=[[@25,92:92='a',<459>,1:92]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={rowcount=[[@5,15:22='rowcount',<386>,1:15]], label=[[@9,27:31='label',<459>,1:27]]}}",
				"{def_query0={query_dictionary={rowcount=[[@5,15:22='rowcount',<386>,1:15]], label=[[@9,27:31='label',<459>,1:27]]}, table_dictionary={accounts={label=[[@7,25:25='b',<459>,1:25]], id=[[@21,81:81='b',<459>,1:81]], enabled=[[@31,109:109='b',<459>,1:109]]}, events={account_id=[[@17,66:66='a',<459>,1:66]], active=[[@25,92:92='a',<459>,1:92]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={rowcount=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordROWCOUNTImplicitColumnAliasV0Test() {
		final String query = "select a.id rowcount, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=rowcount}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[rowcount, label]",
				"{}",
				"{accounts={label=[[@6,22:22='b',<459>,1:22]], id=[[@20,78:78='b',<459>,1:78]], enabled=[[@30,106:106='b',<459>,1:106]]}, events={account_id=[[@16,63:63='a',<459>,1:63]], active=[[@24,89:89='a',<459>,1:89]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={rowcount=[[@4,12:19='rowcount',<386>,1:12]], label=[[@8,24:28='label',<459>,1:24]]}}",
				"{def_query0={query_dictionary={rowcount=[[@4,12:19='rowcount',<386>,1:12]], label=[[@8,24:28='label',<459>,1:24]]}, table_dictionary={accounts={label=[[@6,22:22='b',<459>,1:22]], id=[[@20,78:78='b',<459>,1:78]], enabled=[[@30,106:106='b',<459>,1:106]]}, events={account_id=[[@16,63:63='a',<459>,1:63]], active=[[@24,89:89='a',<459>,1:89]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={rowcount=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordROWCOUNTPhysicalTableV0Test() {
		final String query = "select a.id, b.label from rowcount a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=rowcount}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{rowcount={account_id=[[@15,56:56='a',<459>,1:56]], active=[[@23,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,71:71='b',<459>,1:71]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={rowcount={account_id=[[@15,56:56='a',<459>,1:56]], active=[[@23,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,71:71='b',<459>,1:71]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=rowcount, b=accounts}}}");
	}

	@Test
	public void contextualKeywordROWCOUNTQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.rowcount a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=rowcount}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{analytics.rowcount={account_id=[[@17,66:66='a',<459>,1:66]], active=[[@25,92:92='a',<459>,1:92]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,81:81='b',<459>,1:81]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={analytics.rowcount={account_id=[[@17,66:66='a',<459>,1:66]], active=[[@25,92:92='a',<459>,1:92]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,81:81='b',<459>,1:81]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.rowcount, b=accounts}}}");
	}

	@Test
	public void contextualKeywordROWCOUNTExplicitTableAliasV0Test() {
		final String query = "select rowcount.id, b.label from events as rowcount join accounts b on rowcount.account_id = b.id where rowcount.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=rowcount}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=rowcount, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=rowcount}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=rowcount}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,20:20='b',<459>,1:20]], id=[[@20,93:93='b',<459>,1:93]]}, events={account_id=[[@16,71:78='rowcount',<386>,1:71]], active=[[@24,104:111='rowcount',<386>,1:104]], id=[[@1,7:14='rowcount',<386>,1:7]]}}",
				"{query0={id=[[@3,16:17='id',<459>,1:16]], label=[[@7,22:26='label',<459>,1:22]]}}",
				"{def_query0={query_dictionary={id=[[@3,16:17='id',<459>,1:16]], label=[[@7,22:26='label',<459>,1:22]]}, table_dictionary={accounts={label=[[@5,20:20='b',<459>,1:20]], id=[[@20,93:93='b',<459>,1:93]]}, events={account_id=[[@16,71:78='rowcount',<386>,1:71]], active=[[@24,104:111='rowcount',<386>,1:104]], id=[[@1,7:14='rowcount',<386>,1:7]]}}, filters=[{name=account_id, table_ref=rowcount}, {name=id, table_ref=b}, {name=active, table_ref=rowcount}], interface={id=[{name=id, table_ref=rowcount}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, rowcount=events}}}");
	}

	@Test
	public void contextualKeywordROWCOUNTImplicitTableAliasV0Test() {
		final String query = "select rowcount.id, b.label from events rowcount join accounts b on rowcount.account_id = b.id where rowcount.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=rowcount}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=rowcount, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=rowcount}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=rowcount}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,20:20='b',<459>,1:20]], id=[[@19,90:90='b',<459>,1:90]]}, events={account_id=[[@15,68:75='rowcount',<386>,1:68]], active=[[@23,101:108='rowcount',<386>,1:101]], id=[[@1,7:14='rowcount',<386>,1:7]]}}",
				"{query0={id=[[@3,16:17='id',<459>,1:16]], label=[[@7,22:26='label',<459>,1:22]]}}",
				"{def_query0={query_dictionary={id=[[@3,16:17='id',<459>,1:16]], label=[[@7,22:26='label',<459>,1:22]]}, table_dictionary={accounts={label=[[@5,20:20='b',<459>,1:20]], id=[[@19,90:90='b',<459>,1:90]]}, events={account_id=[[@15,68:75='rowcount',<386>,1:68]], active=[[@23,101:108='rowcount',<386>,1:101]], id=[[@1,7:14='rowcount',<386>,1:7]]}}, filters=[{name=account_id, table_ref=rowcount}, {name=id, table_ref=b}, {name=active, table_ref=rowcount}], interface={id=[{name=id, table_ref=rowcount}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, rowcount=events}}}");
	}

	@Test
	public void contextualKeywordROWCOUNTCteLabelAndOutputAliasV0Test() {
		final String query = "with rowcount as (select a.amount as rowcount, a.id as row_id from events a where a.active = 1) select rowcount.rowcount as result_value, rowcount.row_id from rowcount where rowcount.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=rowcount}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=rowcount}}, query={select={1={column={name=rowcount, table_ref=rowcount}, alias=result_value}, 2={column={name=row_id, table_ref=rowcount}}}, from={table={alias=null, table=rowcount}}, where={condition={left={column={name=row_id, table_ref=rowcount}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,25:25='a',<459>,1:25]], active=[[@20,82:82='a',<459>,1:82]], id=[[@11,47:47='a',<459>,1:47]]}}",
				"{query0={rowcount=[[@9,37:44='rowcount',<386>,1:37], [@27,103:110='rowcount',<386>,1:103]], row_id=[[@15,55:60='row_id',<459>,1:55], [@33,138:145='rowcount',<386>,1:138], [@39,174:181='rowcount',<386>,1:174]]}, query1={row_id=[[@35,147:152='row_id',<459>,1:147]], result_value=[[@31,124:135='result_value',<459>,1:124]]}}",
				"{def_query1={context_list={rowcount=query0}, query_dictionary={result_value=[[@31,124:135='result_value',<459>,1:124]], row_id=[[@35,147:152='row_id',<459>,1:147]]}, def_query0={query_dictionary={rowcount=[[@9,37:44='rowcount',<386>,1:37], [@27,103:110='rowcount',<386>,1:103]], row_id=[[@15,55:60='row_id',<459>,1:55], [@33,138:145='rowcount',<386>,1:138], [@39,174:181='rowcount',<386>,1:174]]}, table_dictionary={events={amount=[[@5,25:25='a',<459>,1:25]], active=[[@20,82:82='a',<459>,1:82]], id=[[@11,47:47='a',<459>,1:47]]}}, filters=[{name=active, table_ref=a}], interface={rowcount=[{name=amount, table_ref=a}], row_id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=rowcount}], interface={result_value=[{name=rowcount, table_ref=rowcount}], row_id=[{name=row_id, table_ref=rowcount}]}, table_alias={rowcount=query0}}}");
	}

	@Test
	public void contextualKeywordROWCOUNTWildcardQualifierV0Test() {
		final String query = "select rowcount.* from events as rowcount where rowcount.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=rowcount}}}, from={table={alias=rowcount, table=events}}, where={condition={left={column={name=active, table_ref=rowcount}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,48:55='rowcount',<386>,1:48]], *=[[@1,7:14='rowcount',<386>,1:7]]}}",
				"{query0={*=[[@3,16:16='*',<291>,1:16]]}}",
				"{def_query0={query_dictionary={*=[[@3,16:16='*',<291>,1:16]]}, table_dictionary={events={active=[[@9,48:55='rowcount',<386>,1:48]], *=[[@1,7:14='rowcount',<386>,1:7]]}}, filters=[{name=active, table_ref=rowcount}], interface={*=[{name=*, table_ref=rowcount}]}, table_alias={rowcount=events}}}");
	}

	@Test
	public void contextualKeywordTIMELIMITBareColumnV0Test() {
		final String query = "select timelimit as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=timelimit, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@9,55:60='active',<459>,1:55]], id=[[@5,34:35='id',<459>,1:34]], timelimit=[[@1,7:15='timelimit',<387>,1:7]]}}",
				"{query0={result_value=[[@3,20:31='result_value',<459>,1:20]], id=[[@5,34:35='id',<459>,1:34]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,20:31='result_value',<459>,1:20]], id=[[@5,34:35='id',<459>,1:34]]}, table_dictionary={events={active=[[@9,55:60='active',<459>,1:55]], id=[[@5,34:35='id',<459>,1:34]], timelimit=[[@1,7:15='timelimit',<387>,1:7]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=timelimit, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordTIMELIMITQualifiedColumnV0Test() {
		final String query = "select a.timelimit as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=timelimit, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,36:36='b',<459>,1:36]], id=[[@21,92:92='b',<459>,1:92]], enabled=[[@31,120:120='b',<459>,1:120]]}, events={account_id=[[@17,77:77='a',<459>,1:77]], active=[[@25,103:103='a',<459>,1:103]], timelimit=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@5,22:33='result_value',<459>,1:22]], label=[[@9,38:42='label',<459>,1:38]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,22:33='result_value',<459>,1:22]], label=[[@9,38:42='label',<459>,1:38]]}, table_dictionary={accounts={label=[[@7,36:36='b',<459>,1:36]], id=[[@21,92:92='b',<459>,1:92]], enabled=[[@31,120:120='b',<459>,1:120]]}, events={account_id=[[@17,77:77='a',<459>,1:77]], active=[[@25,103:103='a',<459>,1:103]], timelimit=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=timelimit, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordTIMELIMITExplicitColumnAliasV0Test() {
		final String query = "select a.id as timelimit, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=timelimit}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[label, timelimit]",
				"{}",
				"{accounts={label=[[@7,26:26='b',<459>,1:26]], id=[[@21,82:82='b',<459>,1:82]], enabled=[[@31,110:110='b',<459>,1:110]]}, events={account_id=[[@17,67:67='a',<459>,1:67]], active=[[@25,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={label=[[@9,28:32='label',<459>,1:28]], timelimit=[[@5,15:23='timelimit',<387>,1:15]]}}",
				"{def_query0={query_dictionary={label=[[@9,28:32='label',<459>,1:28]], timelimit=[[@5,15:23='timelimit',<387>,1:15]]}, table_dictionary={accounts={label=[[@7,26:26='b',<459>,1:26]], id=[[@21,82:82='b',<459>,1:82]], enabled=[[@31,110:110='b',<459>,1:110]]}, events={account_id=[[@17,67:67='a',<459>,1:67]], active=[[@25,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={label=[{name=label, table_ref=b}], timelimit=[{name=id, table_ref=a}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordTIMELIMITImplicitColumnAliasV0Test() {
		final String query = "select a.id timelimit, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=timelimit}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[label, timelimit]",
				"{}",
				"{accounts={label=[[@6,23:23='b',<459>,1:23]], id=[[@20,79:79='b',<459>,1:79]], enabled=[[@30,107:107='b',<459>,1:107]]}, events={account_id=[[@16,64:64='a',<459>,1:64]], active=[[@24,90:90='a',<459>,1:90]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={label=[[@8,25:29='label',<459>,1:25]], timelimit=[[@4,12:20='timelimit',<387>,1:12]]}}",
				"{def_query0={query_dictionary={label=[[@8,25:29='label',<459>,1:25]], timelimit=[[@4,12:20='timelimit',<387>,1:12]]}, table_dictionary={accounts={label=[[@6,23:23='b',<459>,1:23]], id=[[@20,79:79='b',<459>,1:79]], enabled=[[@30,107:107='b',<459>,1:107]]}, events={account_id=[[@16,64:64='a',<459>,1:64]], active=[[@24,90:90='a',<459>,1:90]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={label=[{name=label, table_ref=b}], timelimit=[{name=id, table_ref=a}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordTIMELIMITPhysicalTableV0Test() {
		final String query = "select a.id, b.label from timelimit a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=timelimit}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,72:72='b',<459>,1:72]]}, timelimit={account_id=[[@15,57:57='a',<459>,1:57]], active=[[@23,83:83='a',<459>,1:83]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,72:72='b',<459>,1:72]]}, timelimit={account_id=[[@15,57:57='a',<459>,1:57]], active=[[@23,83:83='a',<459>,1:83]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=timelimit, b=accounts}}}");
	}

	@Test
	public void contextualKeywordTIMELIMITQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.timelimit a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=timelimit}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,82:82='b',<459>,1:82]]}, analytics.timelimit={account_id=[[@17,67:67='a',<459>,1:67]], active=[[@25,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,82:82='b',<459>,1:82]]}, analytics.timelimit={account_id=[[@17,67:67='a',<459>,1:67]], active=[[@25,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.timelimit, b=accounts}}}");
	}

	@Test
	public void contextualKeywordTIMELIMITExplicitTableAliasV0Test() {
		final String query = "select timelimit.id, b.label from events as timelimit join accounts b on timelimit.account_id = b.id where timelimit.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=timelimit}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=timelimit, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=timelimit}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=timelimit}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,21:21='b',<459>,1:21]], id=[[@20,96:96='b',<459>,1:96]]}, events={account_id=[[@16,73:81='timelimit',<387>,1:73]], active=[[@24,107:115='timelimit',<387>,1:107]], id=[[@1,7:15='timelimit',<387>,1:7]]}}",
				"{query0={id=[[@3,17:18='id',<459>,1:17]], label=[[@7,23:27='label',<459>,1:23]]}}",
				"{def_query0={query_dictionary={id=[[@3,17:18='id',<459>,1:17]], label=[[@7,23:27='label',<459>,1:23]]}, table_dictionary={accounts={label=[[@5,21:21='b',<459>,1:21]], id=[[@20,96:96='b',<459>,1:96]]}, events={account_id=[[@16,73:81='timelimit',<387>,1:73]], active=[[@24,107:115='timelimit',<387>,1:107]], id=[[@1,7:15='timelimit',<387>,1:7]]}}, filters=[{name=account_id, table_ref=timelimit}, {name=id, table_ref=b}, {name=active, table_ref=timelimit}], interface={id=[{name=id, table_ref=timelimit}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, timelimit=events}}}");
	}

	@Test
	public void contextualKeywordTIMELIMITImplicitTableAliasV0Test() {
		final String query = "select timelimit.id, b.label from events timelimit join accounts b on timelimit.account_id = b.id where timelimit.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=timelimit}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=timelimit, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=timelimit}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=timelimit}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,21:21='b',<459>,1:21]], id=[[@19,93:93='b',<459>,1:93]]}, events={account_id=[[@15,70:78='timelimit',<387>,1:70]], active=[[@23,104:112='timelimit',<387>,1:104]], id=[[@1,7:15='timelimit',<387>,1:7]]}}",
				"{query0={id=[[@3,17:18='id',<459>,1:17]], label=[[@7,23:27='label',<459>,1:23]]}}",
				"{def_query0={query_dictionary={id=[[@3,17:18='id',<459>,1:17]], label=[[@7,23:27='label',<459>,1:23]]}, table_dictionary={accounts={label=[[@5,21:21='b',<459>,1:21]], id=[[@19,93:93='b',<459>,1:93]]}, events={account_id=[[@15,70:78='timelimit',<387>,1:70]], active=[[@23,104:112='timelimit',<387>,1:104]], id=[[@1,7:15='timelimit',<387>,1:7]]}}, filters=[{name=account_id, table_ref=timelimit}, {name=id, table_ref=b}, {name=active, table_ref=timelimit}], interface={id=[{name=id, table_ref=timelimit}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, timelimit=events}}}");
	}

	@Test
	public void contextualKeywordTIMELIMITCteLabelAndOutputAliasV0Test() {
		final String query = "with timelimit as (select a.amount as timelimit, a.id as row_id from events a where a.active = 1) select timelimit.timelimit as result_value, timelimit.row_id from timelimit where timelimit.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=timelimit}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=timelimit}}, query={select={1={column={name=timelimit, table_ref=timelimit}, alias=result_value}, 2={column={name=row_id, table_ref=timelimit}}}, from={table={alias=null, table=timelimit}}, where={condition={left={column={name=row_id, table_ref=timelimit}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,26:26='a',<459>,1:26]], active=[[@20,84:84='a',<459>,1:84]], id=[[@11,49:49='a',<459>,1:49]]}}",
				"{query0={row_id=[[@15,57:62='row_id',<459>,1:57], [@33,142:150='timelimit',<387>,1:142], [@39,180:188='timelimit',<387>,1:180]], timelimit=[[@9,38:46='timelimit',<387>,1:38], [@27,105:113='timelimit',<387>,1:105]]}, query1={row_id=[[@35,152:157='row_id',<459>,1:152]], result_value=[[@31,128:139='result_value',<459>,1:128]]}}",
				"{def_query1={context_list={timelimit=query0}, query_dictionary={result_value=[[@31,128:139='result_value',<459>,1:128]], row_id=[[@35,152:157='row_id',<459>,1:152]]}, def_query0={query_dictionary={row_id=[[@15,57:62='row_id',<459>,1:57], [@33,142:150='timelimit',<387>,1:142], [@39,180:188='timelimit',<387>,1:180]], timelimit=[[@9,38:46='timelimit',<387>,1:38], [@27,105:113='timelimit',<387>,1:105]]}, table_dictionary={events={amount=[[@5,26:26='a',<459>,1:26]], active=[[@20,84:84='a',<459>,1:84]], id=[[@11,49:49='a',<459>,1:49]]}}, filters=[{name=active, table_ref=a}], interface={row_id=[{name=id, table_ref=a}], timelimit=[{name=amount, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=timelimit}], interface={result_value=[{name=timelimit, table_ref=timelimit}], row_id=[{name=row_id, table_ref=timelimit}]}, table_alias={timelimit=query0}}}");
	}

	@Test
	public void contextualKeywordTIMELIMITWildcardQualifierV0Test() {
		final String query = "select timelimit.* from events as timelimit where timelimit.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=timelimit}}}, from={table={alias=timelimit, table=events}}, where={condition={left={column={name=active, table_ref=timelimit}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,50:58='timelimit',<387>,1:50]], *=[[@1,7:15='timelimit',<387>,1:7]]}}",
				"{query0={*=[[@3,17:17='*',<291>,1:17]]}}",
				"{def_query0={query_dictionary={*=[[@3,17:17='*',<291>,1:17]]}, table_dictionary={events={active=[[@9,50:58='timelimit',<387>,1:50]], *=[[@1,7:15='timelimit',<387>,1:7]]}}, filters=[{name=active, table_ref=timelimit}], interface={*=[{name=*, table_ref=timelimit}]}, table_alias={timelimit=events}}}");
	}

	@Test
	public void contextualKeywordFILESBareColumnV0Test() {
		final String query = "select files as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=files, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={files=[[@1,7:11='files',<388>,1:7]], active=[[@9,51:56='active',<459>,1:51]], id=[[@5,30:31='id',<459>,1:30]]}}",
				"{query0={result_value=[[@3,16:27='result_value',<459>,1:16]], id=[[@5,30:31='id',<459>,1:30]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,16:27='result_value',<459>,1:16]], id=[[@5,30:31='id',<459>,1:30]]}, table_dictionary={events={files=[[@1,7:11='files',<388>,1:7]], active=[[@9,51:56='active',<459>,1:51]], id=[[@5,30:31='id',<459>,1:30]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=files, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordFILESQualifiedColumnV0Test() {
		final String query = "select a.files as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=files, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,32:32='b',<459>,1:32]], id=[[@21,88:88='b',<459>,1:88]], enabled=[[@31,116:116='b',<459>,1:116]]}, events={account_id=[[@17,73:73='a',<459>,1:73]], files=[[@1,7:7='a',<459>,1:7]], active=[[@25,99:99='a',<459>,1:99]]}}",
				"{query0={result_value=[[@5,18:29='result_value',<459>,1:18]], label=[[@9,34:38='label',<459>,1:34]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,18:29='result_value',<459>,1:18]], label=[[@9,34:38='label',<459>,1:34]]}, table_dictionary={accounts={label=[[@7,32:32='b',<459>,1:32]], id=[[@21,88:88='b',<459>,1:88]], enabled=[[@31,116:116='b',<459>,1:116]]}, events={account_id=[[@17,73:73='a',<459>,1:73]], files=[[@1,7:7='a',<459>,1:7]], active=[[@25,99:99='a',<459>,1:99]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=files, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordFILESExplicitColumnAliasV0Test() {
		final String query = "select a.id as files, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=files}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[files, label]",
				"{}",
				"{accounts={label=[[@7,22:22='b',<459>,1:22]], id=[[@21,78:78='b',<459>,1:78]], enabled=[[@31,106:106='b',<459>,1:106]]}, events={account_id=[[@17,63:63='a',<459>,1:63]], active=[[@25,89:89='a',<459>,1:89]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={files=[[@5,15:19='files',<388>,1:15]], label=[[@9,24:28='label',<459>,1:24]]}}",
				"{def_query0={query_dictionary={files=[[@5,15:19='files',<388>,1:15]], label=[[@9,24:28='label',<459>,1:24]]}, table_dictionary={accounts={label=[[@7,22:22='b',<459>,1:22]], id=[[@21,78:78='b',<459>,1:78]], enabled=[[@31,106:106='b',<459>,1:106]]}, events={account_id=[[@17,63:63='a',<459>,1:63]], active=[[@25,89:89='a',<459>,1:89]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={files=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordFILESImplicitColumnAliasV0Test() {
		final String query = "select a.id files, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=files}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[files, label]",
				"{}",
				"{accounts={label=[[@6,19:19='b',<459>,1:19]], id=[[@20,75:75='b',<459>,1:75]], enabled=[[@30,103:103='b',<459>,1:103]]}, events={account_id=[[@16,60:60='a',<459>,1:60]], active=[[@24,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={files=[[@4,12:16='files',<388>,1:12]], label=[[@8,21:25='label',<459>,1:21]]}}",
				"{def_query0={query_dictionary={files=[[@4,12:16='files',<388>,1:12]], label=[[@8,21:25='label',<459>,1:21]]}, table_dictionary={accounts={label=[[@6,19:19='b',<459>,1:19]], id=[[@20,75:75='b',<459>,1:75]], enabled=[[@30,103:103='b',<459>,1:103]]}, events={account_id=[[@16,60:60='a',<459>,1:60]], active=[[@24,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={files=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordFILESPhysicalTableV0Test() {
		final String query = "select a.id, b.label from files a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=files}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{files={account_id=[[@15,53:53='a',<459>,1:53]], active=[[@23,79:79='a',<459>,1:79]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,68:68='b',<459>,1:68]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={files={account_id=[[@15,53:53='a',<459>,1:53]], active=[[@23,79:79='a',<459>,1:79]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,68:68='b',<459>,1:68]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=files, b=accounts}}}");
	}

	@Test
	public void contextualKeywordFILESQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.files a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=files}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{analytics.files={account_id=[[@17,63:63='a',<459>,1:63]], active=[[@25,89:89='a',<459>,1:89]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,78:78='b',<459>,1:78]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={analytics.files={account_id=[[@17,63:63='a',<459>,1:63]], active=[[@25,89:89='a',<459>,1:89]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,78:78='b',<459>,1:78]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.files, b=accounts}}}");
	}

	@Test
	public void contextualKeywordFILESExplicitTableAliasV0Test() {
		final String query = "select files.id, b.label from events as files join accounts b on files.account_id = b.id where files.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=files}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=files, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=files}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=files}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,17:17='b',<459>,1:17]], id=[[@20,84:84='b',<459>,1:84]]}, events={account_id=[[@16,65:69='files',<388>,1:65]], active=[[@24,95:99='files',<388>,1:95]], id=[[@1,7:11='files',<388>,1:7]]}}",
				"{query0={id=[[@3,13:14='id',<459>,1:13]], label=[[@7,19:23='label',<459>,1:19]]}}",
				"{def_query0={query_dictionary={id=[[@3,13:14='id',<459>,1:13]], label=[[@7,19:23='label',<459>,1:19]]}, table_dictionary={accounts={label=[[@5,17:17='b',<459>,1:17]], id=[[@20,84:84='b',<459>,1:84]]}, events={account_id=[[@16,65:69='files',<388>,1:65]], active=[[@24,95:99='files',<388>,1:95]], id=[[@1,7:11='files',<388>,1:7]]}}, filters=[{name=account_id, table_ref=files}, {name=id, table_ref=b}, {name=active, table_ref=files}], interface={id=[{name=id, table_ref=files}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, files=events}}}");
	}

	@Test
	public void contextualKeywordFILESImplicitTableAliasV0Test() {
		final String query = "select files.id, b.label from events files join accounts b on files.account_id = b.id where files.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=files}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=files, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=files}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=files}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,17:17='b',<459>,1:17]], id=[[@19,81:81='b',<459>,1:81]]}, events={account_id=[[@15,62:66='files',<388>,1:62]], active=[[@23,92:96='files',<388>,1:92]], id=[[@1,7:11='files',<388>,1:7]]}}",
				"{query0={id=[[@3,13:14='id',<459>,1:13]], label=[[@7,19:23='label',<459>,1:19]]}}",
				"{def_query0={query_dictionary={id=[[@3,13:14='id',<459>,1:13]], label=[[@7,19:23='label',<459>,1:19]]}, table_dictionary={accounts={label=[[@5,17:17='b',<459>,1:17]], id=[[@19,81:81='b',<459>,1:81]]}, events={account_id=[[@15,62:66='files',<388>,1:62]], active=[[@23,92:96='files',<388>,1:92]], id=[[@1,7:11='files',<388>,1:7]]}}, filters=[{name=account_id, table_ref=files}, {name=id, table_ref=b}, {name=active, table_ref=files}], interface={id=[{name=id, table_ref=files}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, files=events}}}");
	}

	@Test
	public void contextualKeywordFILESCteLabelAndOutputAliasV0Test() {
		final String query = "with files as (select a.amount as files, a.id as row_id from events a where a.active = 1) select files.files as result_value, files.row_id from files where files.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=files}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=files}}, query={select={1={column={name=files, table_ref=files}, alias=result_value}, 2={column={name=row_id, table_ref=files}}}, from={table={alias=null, table=files}}, where={condition={left={column={name=row_id, table_ref=files}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,22:22='a',<459>,1:22]], active=[[@20,76:76='a',<459>,1:76]], id=[[@11,41:41='a',<459>,1:41]]}}",
				"{query0={row_id=[[@15,49:54='row_id',<459>,1:49], [@33,126:130='files',<388>,1:126], [@39,156:160='files',<388>,1:156]], files=[[@9,34:38='files',<388>,1:34], [@27,97:101='files',<388>,1:97]]}, query1={row_id=[[@35,132:137='row_id',<459>,1:132]], result_value=[[@31,112:123='result_value',<459>,1:112]]}}",
				"{def_query1={context_list={files=query0}, query_dictionary={result_value=[[@31,112:123='result_value',<459>,1:112]], row_id=[[@35,132:137='row_id',<459>,1:132]]}, def_query0={query_dictionary={files=[[@9,34:38='files',<388>,1:34], [@27,97:101='files',<388>,1:97]], row_id=[[@15,49:54='row_id',<459>,1:49], [@33,126:130='files',<388>,1:126], [@39,156:160='files',<388>,1:156]]}, table_dictionary={events={amount=[[@5,22:22='a',<459>,1:22]], active=[[@20,76:76='a',<459>,1:76]], id=[[@11,41:41='a',<459>,1:41]]}}, filters=[{name=active, table_ref=a}], interface={files=[{name=amount, table_ref=a}], row_id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=files}], interface={result_value=[{name=files, table_ref=files}], row_id=[{name=row_id, table_ref=files}]}, table_alias={files=query0}}}");
	}

	@Test
	public void contextualKeywordFILESWildcardQualifierV0Test() {
		final String query = "select files.* from events as files where files.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=files}}}, from={table={alias=files, table=events}}, where={condition={left={column={name=active, table_ref=files}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,42:46='files',<388>,1:42]], *=[[@1,7:11='files',<388>,1:7]]}}",
				"{query0={*=[[@3,13:13='*',<291>,1:13]]}}",
				"{def_query0={query_dictionary={*=[[@3,13:13='*',<291>,1:13]]}, table_dictionary={events={active=[[@9,42:46='files',<388>,1:42]], *=[[@1,7:11='files',<388>,1:7]]}}, filters=[{name=active, table_ref=files}], interface={*=[{name=*, table_ref=files}]}, table_alias={files=events}}}");
	}

	@Test
	public void contextualKeywordFILEFORMATBareColumnV0Test() {
		final String query = "select file_format as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=file_format, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@9,57:62='active',<459>,1:57]], id=[[@5,36:37='id',<459>,1:36]], file_format=[[@1,7:17='file_format',<389>,1:7]]}}",
				"{query0={result_value=[[@3,22:33='result_value',<459>,1:22]], id=[[@5,36:37='id',<459>,1:36]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,22:33='result_value',<459>,1:22]], id=[[@5,36:37='id',<459>,1:36]]}, table_dictionary={events={active=[[@9,57:62='active',<459>,1:57]], id=[[@5,36:37='id',<459>,1:36]], file_format=[[@1,7:17='file_format',<389>,1:7]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=file_format, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordFILEFORMATQualifiedColumnV0Test() {
		final String query = "select a.file_format as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=file_format, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,38:38='b',<459>,1:38]], id=[[@21,94:94='b',<459>,1:94]], enabled=[[@31,122:122='b',<459>,1:122]]}, events={account_id=[[@17,79:79='a',<459>,1:79]], active=[[@25,105:105='a',<459>,1:105]], file_format=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@5,24:35='result_value',<459>,1:24]], label=[[@9,40:44='label',<459>,1:40]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,24:35='result_value',<459>,1:24]], label=[[@9,40:44='label',<459>,1:40]]}, table_dictionary={accounts={label=[[@7,38:38='b',<459>,1:38]], id=[[@21,94:94='b',<459>,1:94]], enabled=[[@31,122:122='b',<459>,1:122]]}, events={account_id=[[@17,79:79='a',<459>,1:79]], active=[[@25,105:105='a',<459>,1:105]], file_format=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=file_format, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordFILEFORMATExplicitColumnAliasV0Test() {
		final String query = "select a.id as file_format, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=file_format}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[label, file_format]",
				"{}",
				"{accounts={label=[[@7,28:28='b',<459>,1:28]], id=[[@21,84:84='b',<459>,1:84]], enabled=[[@31,112:112='b',<459>,1:112]]}, events={account_id=[[@17,69:69='a',<459>,1:69]], active=[[@25,95:95='a',<459>,1:95]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={file_format=[[@5,15:25='file_format',<389>,1:15]], label=[[@9,30:34='label',<459>,1:30]]}}",
				"{def_query0={query_dictionary={label=[[@9,30:34='label',<459>,1:30]], file_format=[[@5,15:25='file_format',<389>,1:15]]}, table_dictionary={accounts={label=[[@7,28:28='b',<459>,1:28]], id=[[@21,84:84='b',<459>,1:84]], enabled=[[@31,112:112='b',<459>,1:112]]}, events={account_id=[[@17,69:69='a',<459>,1:69]], active=[[@25,95:95='a',<459>,1:95]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={label=[{name=label, table_ref=b}], file_format=[{name=id, table_ref=a}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordFILEFORMATImplicitColumnAliasV0Test() {
		final String query = "select a.id file_format, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=file_format}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[label, file_format]",
				"{}",
				"{accounts={label=[[@6,25:25='b',<459>,1:25]], id=[[@20,81:81='b',<459>,1:81]], enabled=[[@30,109:109='b',<459>,1:109]]}, events={account_id=[[@16,66:66='a',<459>,1:66]], active=[[@24,92:92='a',<459>,1:92]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={file_format=[[@4,12:22='file_format',<389>,1:12]], label=[[@8,27:31='label',<459>,1:27]]}}",
				"{def_query0={query_dictionary={label=[[@8,27:31='label',<459>,1:27]], file_format=[[@4,12:22='file_format',<389>,1:12]]}, table_dictionary={accounts={label=[[@6,25:25='b',<459>,1:25]], id=[[@20,81:81='b',<459>,1:81]], enabled=[[@30,109:109='b',<459>,1:109]]}, events={account_id=[[@16,66:66='a',<459>,1:66]], active=[[@24,92:92='a',<459>,1:92]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={label=[{name=label, table_ref=b}], file_format=[{name=id, table_ref=a}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordFILEFORMATPhysicalTableV0Test() {
		final String query = "select a.id, b.label from file_format a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=file_format}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,74:74='b',<459>,1:74]]}, file_format={account_id=[[@15,59:59='a',<459>,1:59]], active=[[@23,85:85='a',<459>,1:85]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,74:74='b',<459>,1:74]]}, file_format={account_id=[[@15,59:59='a',<459>,1:59]], active=[[@23,85:85='a',<459>,1:85]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=file_format, b=accounts}}}");
	}

	@Test
	public void contextualKeywordFILEFORMATQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.file_format a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=file_format}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{analytics.file_format={account_id=[[@17,69:69='a',<459>,1:69]], active=[[@25,95:95='a',<459>,1:95]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,84:84='b',<459>,1:84]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={analytics.file_format={account_id=[[@17,69:69='a',<459>,1:69]], active=[[@25,95:95='a',<459>,1:95]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,84:84='b',<459>,1:84]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.file_format, b=accounts}}}");
	}

	@Test
	public void contextualKeywordFILEFORMATExplicitTableAliasV0Test() {
		final String query = "select file_format.id, b.label from events as file_format join accounts b on file_format.account_id = b.id where file_format.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=file_format}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=file_format, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=file_format}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=file_format}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,23:23='b',<459>,1:23]], id=[[@20,102:102='b',<459>,1:102]]}, events={account_id=[[@16,77:87='file_format',<389>,1:77]], active=[[@24,113:123='file_format',<389>,1:113]], id=[[@1,7:17='file_format',<389>,1:7]]}}",
				"{query0={id=[[@3,19:20='id',<459>,1:19]], label=[[@7,25:29='label',<459>,1:25]]}}",
				"{def_query0={query_dictionary={id=[[@3,19:20='id',<459>,1:19]], label=[[@7,25:29='label',<459>,1:25]]}, table_dictionary={accounts={label=[[@5,23:23='b',<459>,1:23]], id=[[@20,102:102='b',<459>,1:102]]}, events={account_id=[[@16,77:87='file_format',<389>,1:77]], active=[[@24,113:123='file_format',<389>,1:113]], id=[[@1,7:17='file_format',<389>,1:7]]}}, filters=[{name=account_id, table_ref=file_format}, {name=id, table_ref=b}, {name=active, table_ref=file_format}], interface={id=[{name=id, table_ref=file_format}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, file_format=events}}}");
	}

	@Test
	public void contextualKeywordFILEFORMATImplicitTableAliasV0Test() {
		final String query = "select file_format.id, b.label from events file_format join accounts b on file_format.account_id = b.id where file_format.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=file_format}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=file_format, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=file_format}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=file_format}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,23:23='b',<459>,1:23]], id=[[@19,99:99='b',<459>,1:99]]}, events={account_id=[[@15,74:84='file_format',<389>,1:74]], active=[[@23,110:120='file_format',<389>,1:110]], id=[[@1,7:17='file_format',<389>,1:7]]}}",
				"{query0={id=[[@3,19:20='id',<459>,1:19]], label=[[@7,25:29='label',<459>,1:25]]}}",
				"{def_query0={query_dictionary={id=[[@3,19:20='id',<459>,1:19]], label=[[@7,25:29='label',<459>,1:25]]}, table_dictionary={accounts={label=[[@5,23:23='b',<459>,1:23]], id=[[@19,99:99='b',<459>,1:99]]}, events={account_id=[[@15,74:84='file_format',<389>,1:74]], active=[[@23,110:120='file_format',<389>,1:110]], id=[[@1,7:17='file_format',<389>,1:7]]}}, filters=[{name=account_id, table_ref=file_format}, {name=id, table_ref=b}, {name=active, table_ref=file_format}], interface={id=[{name=id, table_ref=file_format}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, file_format=events}}}");
	}

	@Test
	public void contextualKeywordFILEFORMATCteLabelAndOutputAliasV0Test() {
		final String query = "with file_format as (select a.amount as file_format, a.id as row_id from events a where a.active = 1) select file_format.file_format as result_value, file_format.row_id from file_format where file_format.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=file_format}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=file_format}}, query={select={1={column={name=file_format, table_ref=file_format}, alias=result_value}, 2={column={name=row_id, table_ref=file_format}}}, from={table={alias=null, table=file_format}}, where={condition={left={column={name=row_id, table_ref=file_format}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,28:28='a',<459>,1:28]], active=[[@20,88:88='a',<459>,1:88]], id=[[@11,53:53='a',<459>,1:53]]}}",
				"{query0={row_id=[[@15,61:66='row_id',<459>,1:61], [@33,150:160='file_format',<389>,1:150], [@39,192:202='file_format',<389>,1:192]], file_format=[[@9,40:50='file_format',<389>,1:40], [@27,109:119='file_format',<389>,1:109]]}, query1={row_id=[[@35,162:167='row_id',<459>,1:162]], result_value=[[@31,136:147='result_value',<459>,1:136]]}}",
				"{def_query1={context_list={file_format=query0}, query_dictionary={result_value=[[@31,136:147='result_value',<459>,1:136]], row_id=[[@35,162:167='row_id',<459>,1:162]]}, def_query0={query_dictionary={row_id=[[@15,61:66='row_id',<459>,1:61], [@33,150:160='file_format',<389>,1:150], [@39,192:202='file_format',<389>,1:192]], file_format=[[@9,40:50='file_format',<389>,1:40], [@27,109:119='file_format',<389>,1:109]]}, table_dictionary={events={amount=[[@5,28:28='a',<459>,1:28]], active=[[@20,88:88='a',<459>,1:88]], id=[[@11,53:53='a',<459>,1:53]]}}, filters=[{name=active, table_ref=a}], interface={row_id=[{name=id, table_ref=a}], file_format=[{name=amount, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=file_format}], interface={result_value=[{name=file_format, table_ref=file_format}], row_id=[{name=row_id, table_ref=file_format}]}, table_alias={file_format=query0}}}");
	}

	@Test
	public void contextualKeywordFILEFORMATWildcardQualifierV0Test() {
		final String query = "select file_format.* from events as file_format where file_format.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=file_format}}}, from={table={alias=file_format, table=events}}, where={condition={left={column={name=active, table_ref=file_format}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,54:64='file_format',<389>,1:54]], *=[[@1,7:17='file_format',<389>,1:7]]}}",
				"{query0={*=[[@3,19:19='*',<291>,1:19]]}}",
				"{def_query0={query_dictionary={*=[[@3,19:19='*',<291>,1:19]]}, table_dictionary={events={active=[[@9,54:64='file_format',<389>,1:54]], *=[[@1,7:17='file_format',<389>,1:7]]}}, filters=[{name=active, table_ref=file_format}], interface={*=[{name=*, table_ref=file_format}]}, table_alias={file_format=events}}}");
	}

	@Test
	public void contextualKeywordIGNORECASEBareColumnV0Test() {
		final String query = "select ignore_case as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=ignore_case, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={ignore_case=[[@1,7:17='ignore_case',<390>,1:7]], active=[[@9,57:62='active',<459>,1:57]], id=[[@5,36:37='id',<459>,1:36]]}}",
				"{query0={result_value=[[@3,22:33='result_value',<459>,1:22]], id=[[@5,36:37='id',<459>,1:36]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,22:33='result_value',<459>,1:22]], id=[[@5,36:37='id',<459>,1:36]]}, table_dictionary={events={ignore_case=[[@1,7:17='ignore_case',<390>,1:7]], active=[[@9,57:62='active',<459>,1:57]], id=[[@5,36:37='id',<459>,1:36]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=ignore_case, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordIGNORECASEQualifiedColumnV0Test() {
		final String query = "select a.ignore_case as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=ignore_case, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,38:38='b',<459>,1:38]], id=[[@21,94:94='b',<459>,1:94]], enabled=[[@31,122:122='b',<459>,1:122]]}, events={ignore_case=[[@1,7:7='a',<459>,1:7]], account_id=[[@17,79:79='a',<459>,1:79]], active=[[@25,105:105='a',<459>,1:105]]}}",
				"{query0={result_value=[[@5,24:35='result_value',<459>,1:24]], label=[[@9,40:44='label',<459>,1:40]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,24:35='result_value',<459>,1:24]], label=[[@9,40:44='label',<459>,1:40]]}, table_dictionary={accounts={label=[[@7,38:38='b',<459>,1:38]], id=[[@21,94:94='b',<459>,1:94]], enabled=[[@31,122:122='b',<459>,1:122]]}, events={ignore_case=[[@1,7:7='a',<459>,1:7]], account_id=[[@17,79:79='a',<459>,1:79]], active=[[@25,105:105='a',<459>,1:105]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=ignore_case, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordIGNORECASEExplicitColumnAliasV0Test() {
		final String query = "select a.id as ignore_case, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=ignore_case}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[ignore_case, label]",
				"{}",
				"{accounts={label=[[@7,28:28='b',<459>,1:28]], id=[[@21,84:84='b',<459>,1:84]], enabled=[[@31,112:112='b',<459>,1:112]]}, events={account_id=[[@17,69:69='a',<459>,1:69]], active=[[@25,95:95='a',<459>,1:95]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={ignore_case=[[@5,15:25='ignore_case',<390>,1:15]], label=[[@9,30:34='label',<459>,1:30]]}}",
				"{def_query0={query_dictionary={ignore_case=[[@5,15:25='ignore_case',<390>,1:15]], label=[[@9,30:34='label',<459>,1:30]]}, table_dictionary={accounts={label=[[@7,28:28='b',<459>,1:28]], id=[[@21,84:84='b',<459>,1:84]], enabled=[[@31,112:112='b',<459>,1:112]]}, events={account_id=[[@17,69:69='a',<459>,1:69]], active=[[@25,95:95='a',<459>,1:95]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={ignore_case=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordIGNORECASEImplicitColumnAliasV0Test() {
		final String query = "select a.id ignore_case, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=ignore_case}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[ignore_case, label]",
				"{}",
				"{accounts={label=[[@6,25:25='b',<459>,1:25]], id=[[@20,81:81='b',<459>,1:81]], enabled=[[@30,109:109='b',<459>,1:109]]}, events={account_id=[[@16,66:66='a',<459>,1:66]], active=[[@24,92:92='a',<459>,1:92]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={ignore_case=[[@4,12:22='ignore_case',<390>,1:12]], label=[[@8,27:31='label',<459>,1:27]]}}",
				"{def_query0={query_dictionary={ignore_case=[[@4,12:22='ignore_case',<390>,1:12]], label=[[@8,27:31='label',<459>,1:27]]}, table_dictionary={accounts={label=[[@6,25:25='b',<459>,1:25]], id=[[@20,81:81='b',<459>,1:81]], enabled=[[@30,109:109='b',<459>,1:109]]}, events={account_id=[[@16,66:66='a',<459>,1:66]], active=[[@24,92:92='a',<459>,1:92]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={ignore_case=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordIGNORECASEPhysicalTableV0Test() {
		final String query = "select a.id, b.label from ignore_case a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=ignore_case}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{ignore_case={account_id=[[@15,59:59='a',<459>,1:59]], active=[[@23,85:85='a',<459>,1:85]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,74:74='b',<459>,1:74]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={ignore_case={account_id=[[@15,59:59='a',<459>,1:59]], active=[[@23,85:85='a',<459>,1:85]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,74:74='b',<459>,1:74]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=ignore_case, b=accounts}}}");
	}

	@Test
	public void contextualKeywordIGNORECASEQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.ignore_case a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=ignore_case}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{analytics.ignore_case={account_id=[[@17,69:69='a',<459>,1:69]], active=[[@25,95:95='a',<459>,1:95]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,84:84='b',<459>,1:84]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={analytics.ignore_case={account_id=[[@17,69:69='a',<459>,1:69]], active=[[@25,95:95='a',<459>,1:95]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,84:84='b',<459>,1:84]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.ignore_case, b=accounts}}}");
	}

	@Test
	public void contextualKeywordIGNORECASEExplicitTableAliasV0Test() {
		final String query = "select ignore_case.id, b.label from events as ignore_case join accounts b on ignore_case.account_id = b.id where ignore_case.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=ignore_case}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=ignore_case, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=ignore_case}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=ignore_case}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,23:23='b',<459>,1:23]], id=[[@20,102:102='b',<459>,1:102]]}, events={account_id=[[@16,77:87='ignore_case',<390>,1:77]], active=[[@24,113:123='ignore_case',<390>,1:113]], id=[[@1,7:17='ignore_case',<390>,1:7]]}}",
				"{query0={id=[[@3,19:20='id',<459>,1:19]], label=[[@7,25:29='label',<459>,1:25]]}}",
				"{def_query0={query_dictionary={id=[[@3,19:20='id',<459>,1:19]], label=[[@7,25:29='label',<459>,1:25]]}, table_dictionary={accounts={label=[[@5,23:23='b',<459>,1:23]], id=[[@20,102:102='b',<459>,1:102]]}, events={account_id=[[@16,77:87='ignore_case',<390>,1:77]], active=[[@24,113:123='ignore_case',<390>,1:113]], id=[[@1,7:17='ignore_case',<390>,1:7]]}}, filters=[{name=account_id, table_ref=ignore_case}, {name=id, table_ref=b}, {name=active, table_ref=ignore_case}], interface={id=[{name=id, table_ref=ignore_case}], label=[{name=label, table_ref=b}]}, table_alias={ignore_case=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordIGNORECASEImplicitTableAliasV0Test() {
		final String query = "select ignore_case.id, b.label from events ignore_case join accounts b on ignore_case.account_id = b.id where ignore_case.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=ignore_case}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=ignore_case, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=ignore_case}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=ignore_case}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,23:23='b',<459>,1:23]], id=[[@19,99:99='b',<459>,1:99]]}, events={account_id=[[@15,74:84='ignore_case',<390>,1:74]], active=[[@23,110:120='ignore_case',<390>,1:110]], id=[[@1,7:17='ignore_case',<390>,1:7]]}}",
				"{query0={id=[[@3,19:20='id',<459>,1:19]], label=[[@7,25:29='label',<459>,1:25]]}}",
				"{def_query0={query_dictionary={id=[[@3,19:20='id',<459>,1:19]], label=[[@7,25:29='label',<459>,1:25]]}, table_dictionary={accounts={label=[[@5,23:23='b',<459>,1:23]], id=[[@19,99:99='b',<459>,1:99]]}, events={account_id=[[@15,74:84='ignore_case',<390>,1:74]], active=[[@23,110:120='ignore_case',<390>,1:110]], id=[[@1,7:17='ignore_case',<390>,1:7]]}}, filters=[{name=account_id, table_ref=ignore_case}, {name=id, table_ref=b}, {name=active, table_ref=ignore_case}], interface={id=[{name=id, table_ref=ignore_case}], label=[{name=label, table_ref=b}]}, table_alias={ignore_case=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordIGNORECASECteLabelAndOutputAliasV0Test() {
		final String query = "with ignore_case as (select a.amount as ignore_case, a.id as row_id from events a where a.active = 1) select ignore_case.ignore_case as result_value, ignore_case.row_id from ignore_case where ignore_case.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=ignore_case}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=ignore_case}}, query={select={1={column={name=ignore_case, table_ref=ignore_case}, alias=result_value}, 2={column={name=row_id, table_ref=ignore_case}}}, from={table={alias=null, table=ignore_case}}, where={condition={left={column={name=row_id, table_ref=ignore_case}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,28:28='a',<459>,1:28]], active=[[@20,88:88='a',<459>,1:88]], id=[[@11,53:53='a',<459>,1:53]]}}",
				"{query0={ignore_case=[[@9,40:50='ignore_case',<390>,1:40], [@27,109:119='ignore_case',<390>,1:109]], row_id=[[@15,61:66='row_id',<459>,1:61], [@33,150:160='ignore_case',<390>,1:150], [@39,192:202='ignore_case',<390>,1:192]]}, query1={row_id=[[@35,162:167='row_id',<459>,1:162]], result_value=[[@31,136:147='result_value',<459>,1:136]]}}",
				"{def_query1={context_list={ignore_case=query0}, query_dictionary={result_value=[[@31,136:147='result_value',<459>,1:136]], row_id=[[@35,162:167='row_id',<459>,1:162]]}, def_query0={query_dictionary={ignore_case=[[@9,40:50='ignore_case',<390>,1:40], [@27,109:119='ignore_case',<390>,1:109]], row_id=[[@15,61:66='row_id',<459>,1:61], [@33,150:160='ignore_case',<390>,1:150], [@39,192:202='ignore_case',<390>,1:192]]}, table_dictionary={events={amount=[[@5,28:28='a',<459>,1:28]], active=[[@20,88:88='a',<459>,1:88]], id=[[@11,53:53='a',<459>,1:53]]}}, filters=[{name=active, table_ref=a}], interface={ignore_case=[{name=amount, table_ref=a}], row_id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=ignore_case}], interface={result_value=[{name=ignore_case, table_ref=ignore_case}], row_id=[{name=row_id, table_ref=ignore_case}]}, table_alias={ignore_case=query0}}}");
	}

	@Test
	public void contextualKeywordIGNORECASEWildcardQualifierV0Test() {
		final String query = "select ignore_case.* from events as ignore_case where ignore_case.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=ignore_case}}}, from={table={alias=ignore_case, table=events}}, where={condition={left={column={name=active, table_ref=ignore_case}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,54:64='ignore_case',<390>,1:54]], *=[[@1,7:17='ignore_case',<390>,1:7]]}}",
				"{query0={*=[[@3,19:19='*',<291>,1:19]]}}",
				"{def_query0={query_dictionary={*=[[@3,19:19='*',<291>,1:19]]}, table_dictionary={events={active=[[@9,54:64='ignore_case',<390>,1:54]], *=[[@1,7:17='ignore_case',<390>,1:7]]}}, filters=[{name=active, table_ref=ignore_case}], interface={*=[{name=*, table_ref=ignore_case}]}, table_alias={ignore_case=events}}}");
	}

	@Test
	public void contextualKeywordMAXFILECOUNTBareColumnV0Test() {
		final String query = "select max_file_count as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=max_file_count, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={max_file_count=[[@1,7:20='max_file_count',<391>,1:7]], active=[[@9,60:65='active',<459>,1:60]], id=[[@5,39:40='id',<459>,1:39]]}}",
				"{query0={result_value=[[@3,25:36='result_value',<459>,1:25]], id=[[@5,39:40='id',<459>,1:39]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,25:36='result_value',<459>,1:25]], id=[[@5,39:40='id',<459>,1:39]]}, table_dictionary={events={max_file_count=[[@1,7:20='max_file_count',<391>,1:7]], active=[[@9,60:65='active',<459>,1:60]], id=[[@5,39:40='id',<459>,1:39]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=max_file_count, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordMAXFILECOUNTQualifiedColumnV0Test() {
		final String query = "select a.max_file_count as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=max_file_count, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,41:41='b',<459>,1:41]], id=[[@21,97:97='b',<459>,1:97]], enabled=[[@31,125:125='b',<459>,1:125]]}, events={max_file_count=[[@1,7:7='a',<459>,1:7]], account_id=[[@17,82:82='a',<459>,1:82]], active=[[@25,108:108='a',<459>,1:108]]}}",
				"{query0={result_value=[[@5,27:38='result_value',<459>,1:27]], label=[[@9,43:47='label',<459>,1:43]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,27:38='result_value',<459>,1:27]], label=[[@9,43:47='label',<459>,1:43]]}, table_dictionary={accounts={label=[[@7,41:41='b',<459>,1:41]], id=[[@21,97:97='b',<459>,1:97]], enabled=[[@31,125:125='b',<459>,1:125]]}, events={max_file_count=[[@1,7:7='a',<459>,1:7]], account_id=[[@17,82:82='a',<459>,1:82]], active=[[@25,108:108='a',<459>,1:108]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=max_file_count, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordMAXFILECOUNTExplicitColumnAliasV0Test() {
		final String query = "select a.id as max_file_count, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=max_file_count}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[max_file_count, label]",
				"{}",
				"{accounts={label=[[@7,31:31='b',<459>,1:31]], id=[[@21,87:87='b',<459>,1:87]], enabled=[[@31,115:115='b',<459>,1:115]]}, events={account_id=[[@17,72:72='a',<459>,1:72]], active=[[@25,98:98='a',<459>,1:98]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={max_file_count=[[@5,15:28='max_file_count',<391>,1:15]], label=[[@9,33:37='label',<459>,1:33]]}}",
				"{def_query0={query_dictionary={max_file_count=[[@5,15:28='max_file_count',<391>,1:15]], label=[[@9,33:37='label',<459>,1:33]]}, table_dictionary={accounts={label=[[@7,31:31='b',<459>,1:31]], id=[[@21,87:87='b',<459>,1:87]], enabled=[[@31,115:115='b',<459>,1:115]]}, events={account_id=[[@17,72:72='a',<459>,1:72]], active=[[@25,98:98='a',<459>,1:98]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={max_file_count=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordMAXFILECOUNTImplicitColumnAliasV0Test() {
		final String query = "select a.id max_file_count, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=max_file_count}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[max_file_count, label]",
				"{}",
				"{accounts={label=[[@6,28:28='b',<459>,1:28]], id=[[@20,84:84='b',<459>,1:84]], enabled=[[@30,112:112='b',<459>,1:112]]}, events={account_id=[[@16,69:69='a',<459>,1:69]], active=[[@24,95:95='a',<459>,1:95]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={max_file_count=[[@4,12:25='max_file_count',<391>,1:12]], label=[[@8,30:34='label',<459>,1:30]]}}",
				"{def_query0={query_dictionary={max_file_count=[[@4,12:25='max_file_count',<391>,1:12]], label=[[@8,30:34='label',<459>,1:30]]}, table_dictionary={accounts={label=[[@6,28:28='b',<459>,1:28]], id=[[@20,84:84='b',<459>,1:84]], enabled=[[@30,112:112='b',<459>,1:112]]}, events={account_id=[[@16,69:69='a',<459>,1:69]], active=[[@24,95:95='a',<459>,1:95]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={max_file_count=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordMAXFILECOUNTPhysicalTableV0Test() {
		final String query = "select a.id, b.label from max_file_count a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=max_file_count}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{max_file_count={account_id=[[@15,62:62='a',<459>,1:62]], active=[[@23,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,77:77='b',<459>,1:77]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={max_file_count={account_id=[[@15,62:62='a',<459>,1:62]], active=[[@23,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,77:77='b',<459>,1:77]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=max_file_count, b=accounts}}}");
	}

	@Test
	public void contextualKeywordMAXFILECOUNTQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.max_file_count a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=max_file_count}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{analytics.max_file_count={account_id=[[@17,72:72='a',<459>,1:72]], active=[[@25,98:98='a',<459>,1:98]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,87:87='b',<459>,1:87]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={analytics.max_file_count={account_id=[[@17,72:72='a',<459>,1:72]], active=[[@25,98:98='a',<459>,1:98]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,87:87='b',<459>,1:87]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.max_file_count, b=accounts}}}");
	}

	@Test
	public void contextualKeywordMAXFILECOUNTExplicitTableAliasV0Test() {
		final String query = "select max_file_count.id, b.label from events as max_file_count join accounts b on max_file_count.account_id = b.id where max_file_count.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=max_file_count}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=max_file_count, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=max_file_count}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=max_file_count}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,26:26='b',<459>,1:26]], id=[[@20,111:111='b',<459>,1:111]]}, events={account_id=[[@16,83:96='max_file_count',<391>,1:83]], active=[[@24,122:135='max_file_count',<391>,1:122]], id=[[@1,7:20='max_file_count',<391>,1:7]]}}",
				"{query0={id=[[@3,22:23='id',<459>,1:22]], label=[[@7,28:32='label',<459>,1:28]]}}",
				"{def_query0={query_dictionary={id=[[@3,22:23='id',<459>,1:22]], label=[[@7,28:32='label',<459>,1:28]]}, table_dictionary={accounts={label=[[@5,26:26='b',<459>,1:26]], id=[[@20,111:111='b',<459>,1:111]]}, events={account_id=[[@16,83:96='max_file_count',<391>,1:83]], active=[[@24,122:135='max_file_count',<391>,1:122]], id=[[@1,7:20='max_file_count',<391>,1:7]]}}, filters=[{name=account_id, table_ref=max_file_count}, {name=id, table_ref=b}, {name=active, table_ref=max_file_count}], interface={id=[{name=id, table_ref=max_file_count}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, max_file_count=events}}}");
	}

	@Test
	public void contextualKeywordMAXFILECOUNTImplicitTableAliasV0Test() {
		final String query = "select max_file_count.id, b.label from events max_file_count join accounts b on max_file_count.account_id = b.id where max_file_count.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=max_file_count}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=max_file_count, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=max_file_count}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=max_file_count}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,26:26='b',<459>,1:26]], id=[[@19,108:108='b',<459>,1:108]]}, events={account_id=[[@15,80:93='max_file_count',<391>,1:80]], active=[[@23,119:132='max_file_count',<391>,1:119]], id=[[@1,7:20='max_file_count',<391>,1:7]]}}",
				"{query0={id=[[@3,22:23='id',<459>,1:22]], label=[[@7,28:32='label',<459>,1:28]]}}",
				"{def_query0={query_dictionary={id=[[@3,22:23='id',<459>,1:22]], label=[[@7,28:32='label',<459>,1:28]]}, table_dictionary={accounts={label=[[@5,26:26='b',<459>,1:26]], id=[[@19,108:108='b',<459>,1:108]]}, events={account_id=[[@15,80:93='max_file_count',<391>,1:80]], active=[[@23,119:132='max_file_count',<391>,1:119]], id=[[@1,7:20='max_file_count',<391>,1:7]]}}, filters=[{name=account_id, table_ref=max_file_count}, {name=id, table_ref=b}, {name=active, table_ref=max_file_count}], interface={id=[{name=id, table_ref=max_file_count}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, max_file_count=events}}}");
	}

	@Test
	public void contextualKeywordMAXFILECOUNTCteLabelAndOutputAliasV0Test() {
		final String query = "with max_file_count as (select a.amount as max_file_count, a.id as row_id from events a where a.active = 1) select max_file_count.max_file_count as result_value, max_file_count.row_id from max_file_count where max_file_count.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=max_file_count}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=max_file_count}}, query={select={1={column={name=max_file_count, table_ref=max_file_count}, alias=result_value}, 2={column={name=row_id, table_ref=max_file_count}}}, from={table={alias=null, table=max_file_count}}, where={condition={left={column={name=row_id, table_ref=max_file_count}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,31:31='a',<459>,1:31]], active=[[@20,94:94='a',<459>,1:94]], id=[[@11,59:59='a',<459>,1:59]]}}",
				"{query0={row_id=[[@15,67:72='row_id',<459>,1:67], [@33,162:175='max_file_count',<391>,1:162], [@39,210:223='max_file_count',<391>,1:210]], max_file_count=[[@9,43:56='max_file_count',<391>,1:43], [@27,115:128='max_file_count',<391>,1:115]]}, query1={row_id=[[@35,177:182='row_id',<459>,1:177]], result_value=[[@31,148:159='result_value',<459>,1:148]]}}",
				"{def_query1={context_list={max_file_count=query0}, query_dictionary={result_value=[[@31,148:159='result_value',<459>,1:148]], row_id=[[@35,177:182='row_id',<459>,1:177]]}, def_query0={query_dictionary={max_file_count=[[@9,43:56='max_file_count',<391>,1:43], [@27,115:128='max_file_count',<391>,1:115]], row_id=[[@15,67:72='row_id',<459>,1:67], [@33,162:175='max_file_count',<391>,1:162], [@39,210:223='max_file_count',<391>,1:210]]}, table_dictionary={events={amount=[[@5,31:31='a',<459>,1:31]], active=[[@20,94:94='a',<459>,1:94]], id=[[@11,59:59='a',<459>,1:59]]}}, filters=[{name=active, table_ref=a}], interface={max_file_count=[{name=amount, table_ref=a}], row_id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=max_file_count}], interface={result_value=[{name=max_file_count, table_ref=max_file_count}], row_id=[{name=row_id, table_ref=max_file_count}]}, table_alias={max_file_count=query0}}}");
	}

	@Test
	public void contextualKeywordMAXFILECOUNTWildcardQualifierV0Test() {
		final String query = "select max_file_count.* from events as max_file_count where max_file_count.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=max_file_count}}}, from={table={alias=max_file_count, table=events}}, where={condition={left={column={name=active, table_ref=max_file_count}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,60:73='max_file_count',<391>,1:60]], *=[[@1,7:20='max_file_count',<391>,1:7]]}}",
				"{query0={*=[[@3,22:22='*',<291>,1:22]]}}",
				"{def_query0={query_dictionary={*=[[@3,22:22='*',<291>,1:22]]}, table_dictionary={events={active=[[@9,60:73='max_file_count',<391>,1:60]], *=[[@1,7:20='max_file_count',<391>,1:7]]}}, filters=[{name=active, table_ref=max_file_count}], interface={*=[{name=*, table_ref=max_file_count}]}, table_alias={max_file_count=events}}}");
	}

	@Test
	public void contextualKeywordMAXRECORDSPERFILEBareColumnV0Test() {
		final String query = "select max_records_per_file as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=max_records_per_file, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@9,66:71='active',<459>,1:66]], max_records_per_file=[[@1,7:26='max_records_per_file',<392>,1:7]], id=[[@5,45:46='id',<459>,1:45]]}}",
				"{query0={result_value=[[@3,31:42='result_value',<459>,1:31]], id=[[@5,45:46='id',<459>,1:45]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,31:42='result_value',<459>,1:31]], id=[[@5,45:46='id',<459>,1:45]]}, table_dictionary={events={active=[[@9,66:71='active',<459>,1:66]], max_records_per_file=[[@1,7:26='max_records_per_file',<392>,1:7]], id=[[@5,45:46='id',<459>,1:45]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=max_records_per_file, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordMAXRECORDSPERFILEQualifiedColumnV0Test() {
		final String query = "select a.max_records_per_file as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=max_records_per_file, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,47:47='b',<459>,1:47]], id=[[@21,103:103='b',<459>,1:103]], enabled=[[@31,131:131='b',<459>,1:131]]}, events={account_id=[[@17,88:88='a',<459>,1:88]], active=[[@25,114:114='a',<459>,1:114]], max_records_per_file=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@5,33:44='result_value',<459>,1:33]], label=[[@9,49:53='label',<459>,1:49]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,33:44='result_value',<459>,1:33]], label=[[@9,49:53='label',<459>,1:49]]}, table_dictionary={accounts={label=[[@7,47:47='b',<459>,1:47]], id=[[@21,103:103='b',<459>,1:103]], enabled=[[@31,131:131='b',<459>,1:131]]}, events={account_id=[[@17,88:88='a',<459>,1:88]], active=[[@25,114:114='a',<459>,1:114]], max_records_per_file=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=max_records_per_file, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordMAXRECORDSPERFILEExplicitColumnAliasV0Test() {
		final String query = "select a.id as max_records_per_file, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=max_records_per_file}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[max_records_per_file, label]",
				"{}",
				"{accounts={label=[[@7,37:37='b',<459>,1:37]], id=[[@21,93:93='b',<459>,1:93]], enabled=[[@31,121:121='b',<459>,1:121]]}, events={account_id=[[@17,78:78='a',<459>,1:78]], active=[[@25,104:104='a',<459>,1:104]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={max_records_per_file=[[@5,15:34='max_records_per_file',<392>,1:15]], label=[[@9,39:43='label',<459>,1:39]]}}",
				"{def_query0={query_dictionary={max_records_per_file=[[@5,15:34='max_records_per_file',<392>,1:15]], label=[[@9,39:43='label',<459>,1:39]]}, table_dictionary={accounts={label=[[@7,37:37='b',<459>,1:37]], id=[[@21,93:93='b',<459>,1:93]], enabled=[[@31,121:121='b',<459>,1:121]]}, events={account_id=[[@17,78:78='a',<459>,1:78]], active=[[@25,104:104='a',<459>,1:104]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={max_records_per_file=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordMAXRECORDSPERFILEImplicitColumnAliasV0Test() {
		final String query = "select a.id max_records_per_file, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=max_records_per_file}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[max_records_per_file, label]",
				"{}",
				"{accounts={label=[[@6,34:34='b',<459>,1:34]], id=[[@20,90:90='b',<459>,1:90]], enabled=[[@30,118:118='b',<459>,1:118]]}, events={account_id=[[@16,75:75='a',<459>,1:75]], active=[[@24,101:101='a',<459>,1:101]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={max_records_per_file=[[@4,12:31='max_records_per_file',<392>,1:12]], label=[[@8,36:40='label',<459>,1:36]]}}",
				"{def_query0={query_dictionary={max_records_per_file=[[@4,12:31='max_records_per_file',<392>,1:12]], label=[[@8,36:40='label',<459>,1:36]]}, table_dictionary={accounts={label=[[@6,34:34='b',<459>,1:34]], id=[[@20,90:90='b',<459>,1:90]], enabled=[[@30,118:118='b',<459>,1:118]]}, events={account_id=[[@16,75:75='a',<459>,1:75]], active=[[@24,101:101='a',<459>,1:101]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={max_records_per_file=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordMAXRECORDSPERFILEPhysicalTableV0Test() {
		final String query = "select a.id, b.label from max_records_per_file a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=max_records_per_file}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{max_records_per_file={account_id=[[@15,68:68='a',<459>,1:68]], active=[[@23,94:94='a',<459>,1:94]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,83:83='b',<459>,1:83]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={max_records_per_file={account_id=[[@15,68:68='a',<459>,1:68]], active=[[@23,94:94='a',<459>,1:94]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,83:83='b',<459>,1:83]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=max_records_per_file, b=accounts}}}");
	}

	@Test
	public void contextualKeywordMAXRECORDSPERFILEQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.max_records_per_file a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=max_records_per_file}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,93:93='b',<459>,1:93]]}, analytics.max_records_per_file={account_id=[[@17,78:78='a',<459>,1:78]], active=[[@25,104:104='a',<459>,1:104]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,93:93='b',<459>,1:93]]}, analytics.max_records_per_file={account_id=[[@17,78:78='a',<459>,1:78]], active=[[@25,104:104='a',<459>,1:104]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.max_records_per_file, b=accounts}}}");
	}

	@Test
	public void contextualKeywordMAXRECORDSPERFILEExplicitTableAliasV0Test() {
		final String query = "select max_records_per_file.id, b.label from events as max_records_per_file join accounts b on max_records_per_file.account_id = b.id where max_records_per_file.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=max_records_per_file}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=max_records_per_file, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=max_records_per_file}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=max_records_per_file}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,32:32='b',<459>,1:32]], id=[[@20,129:129='b',<459>,1:129]]}, events={account_id=[[@16,95:114='max_records_per_file',<392>,1:95]], active=[[@24,140:159='max_records_per_file',<392>,1:140]], id=[[@1,7:26='max_records_per_file',<392>,1:7]]}}",
				"{query0={id=[[@3,28:29='id',<459>,1:28]], label=[[@7,34:38='label',<459>,1:34]]}}",
				"{def_query0={query_dictionary={id=[[@3,28:29='id',<459>,1:28]], label=[[@7,34:38='label',<459>,1:34]]}, table_dictionary={accounts={label=[[@5,32:32='b',<459>,1:32]], id=[[@20,129:129='b',<459>,1:129]]}, events={account_id=[[@16,95:114='max_records_per_file',<392>,1:95]], active=[[@24,140:159='max_records_per_file',<392>,1:140]], id=[[@1,7:26='max_records_per_file',<392>,1:7]]}}, filters=[{name=account_id, table_ref=max_records_per_file}, {name=id, table_ref=b}, {name=active, table_ref=max_records_per_file}], interface={id=[{name=id, table_ref=max_records_per_file}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, max_records_per_file=events}}}");
	}

	@Test
	public void contextualKeywordMAXRECORDSPERFILEImplicitTableAliasV0Test() {
		final String query = "select max_records_per_file.id, b.label from events max_records_per_file join accounts b on max_records_per_file.account_id = b.id where max_records_per_file.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=max_records_per_file}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=max_records_per_file, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=max_records_per_file}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=max_records_per_file}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,32:32='b',<459>,1:32]], id=[[@19,126:126='b',<459>,1:126]]}, events={account_id=[[@15,92:111='max_records_per_file',<392>,1:92]], active=[[@23,137:156='max_records_per_file',<392>,1:137]], id=[[@1,7:26='max_records_per_file',<392>,1:7]]}}",
				"{query0={id=[[@3,28:29='id',<459>,1:28]], label=[[@7,34:38='label',<459>,1:34]]}}",
				"{def_query0={query_dictionary={id=[[@3,28:29='id',<459>,1:28]], label=[[@7,34:38='label',<459>,1:34]]}, table_dictionary={accounts={label=[[@5,32:32='b',<459>,1:32]], id=[[@19,126:126='b',<459>,1:126]]}, events={account_id=[[@15,92:111='max_records_per_file',<392>,1:92]], active=[[@23,137:156='max_records_per_file',<392>,1:137]], id=[[@1,7:26='max_records_per_file',<392>,1:7]]}}, filters=[{name=account_id, table_ref=max_records_per_file}, {name=id, table_ref=b}, {name=active, table_ref=max_records_per_file}], interface={id=[{name=id, table_ref=max_records_per_file}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, max_records_per_file=events}}}");
	}

	@Test
	public void contextualKeywordMAXRECORDSPERFILECteLabelAndOutputAliasV0Test() {
		final String query = "with max_records_per_file as (select a.amount as max_records_per_file, a.id as row_id from events a where a.active = 1) select max_records_per_file.max_records_per_file as result_value, max_records_per_file.row_id from max_records_per_file where max_records_per_file.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=max_records_per_file}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=max_records_per_file}}, query={select={1={column={name=max_records_per_file, table_ref=max_records_per_file}, alias=result_value}, 2={column={name=row_id, table_ref=max_records_per_file}}}, from={table={alias=null, table=max_records_per_file}}, where={condition={left={column={name=row_id, table_ref=max_records_per_file}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,37:37='a',<459>,1:37]], active=[[@20,106:106='a',<459>,1:106]], id=[[@11,71:71='a',<459>,1:71]]}}",
				"{query0={row_id=[[@15,79:84='row_id',<459>,1:79], [@33,186:205='max_records_per_file',<392>,1:186], [@39,246:265='max_records_per_file',<392>,1:246]], max_records_per_file=[[@9,49:68='max_records_per_file',<392>,1:49], [@27,127:146='max_records_per_file',<392>,1:127]]}, query1={row_id=[[@35,207:212='row_id',<459>,1:207]], result_value=[[@31,172:183='result_value',<459>,1:172]]}}",
				"{def_query1={context_list={max_records_per_file=query0}, query_dictionary={result_value=[[@31,172:183='result_value',<459>,1:172]], row_id=[[@35,207:212='row_id',<459>,1:207]]}, def_query0={query_dictionary={max_records_per_file=[[@9,49:68='max_records_per_file',<392>,1:49], [@27,127:146='max_records_per_file',<392>,1:127]], row_id=[[@15,79:84='row_id',<459>,1:79], [@33,186:205='max_records_per_file',<392>,1:186], [@39,246:265='max_records_per_file',<392>,1:246]]}, table_dictionary={events={amount=[[@5,37:37='a',<459>,1:37]], active=[[@20,106:106='a',<459>,1:106]], id=[[@11,71:71='a',<459>,1:71]]}}, filters=[{name=active, table_ref=a}], interface={max_records_per_file=[{name=amount, table_ref=a}], row_id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=max_records_per_file}], interface={result_value=[{name=max_records_per_file, table_ref=max_records_per_file}], row_id=[{name=row_id, table_ref=max_records_per_file}]}, table_alias={max_records_per_file=query0}}}");
	}

	@Test
	public void contextualKeywordMAXRECORDSPERFILEWildcardQualifierV0Test() {
		final String query = "select max_records_per_file.* from events as max_records_per_file where max_records_per_file.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=max_records_per_file}}}, from={table={alias=max_records_per_file, table=events}}, where={condition={left={column={name=active, table_ref=max_records_per_file}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,72:91='max_records_per_file',<392>,1:72]], *=[[@1,7:26='max_records_per_file',<392>,1:7]]}}",
				"{query0={*=[[@3,28:28='*',<291>,1:28]]}}",
				"{def_query0={query_dictionary={*=[[@3,28:28='*',<291>,1:28]]}, table_dictionary={events={active=[[@9,72:91='max_records_per_file',<392>,1:72]], *=[[@1,7:26='max_records_per_file',<392>,1:7]]}}, filters=[{name=active, table_ref=max_records_per_file}], interface={*=[{name=*, table_ref=max_records_per_file}]}, table_alias={max_records_per_file=events}}}");
	}

	@Test
	public void contextualKeywordKINDBareColumnV0Test() {
		final String query = "select kind as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=kind, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={kind=[[@1,7:10='kind',<393>,1:7]], active=[[@9,50:55='active',<459>,1:50]], id=[[@5,29:30='id',<459>,1:29]]}}",
				"{query0={result_value=[[@3,15:26='result_value',<459>,1:15]], id=[[@5,29:30='id',<459>,1:29]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,15:26='result_value',<459>,1:15]], id=[[@5,29:30='id',<459>,1:29]]}, table_dictionary={events={kind=[[@1,7:10='kind',<393>,1:7]], active=[[@9,50:55='active',<459>,1:50]], id=[[@5,29:30='id',<459>,1:29]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=kind, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordKINDQualifiedColumnV0Test() {
		final String query = "select a.kind as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=kind, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,31:31='b',<459>,1:31]], id=[[@21,87:87='b',<459>,1:87]], enabled=[[@31,115:115='b',<459>,1:115]]}, events={account_id=[[@17,72:72='a',<459>,1:72]], kind=[[@1,7:7='a',<459>,1:7]], active=[[@25,98:98='a',<459>,1:98]]}}",
				"{query0={result_value=[[@5,17:28='result_value',<459>,1:17]], label=[[@9,33:37='label',<459>,1:33]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,17:28='result_value',<459>,1:17]], label=[[@9,33:37='label',<459>,1:33]]}, table_dictionary={accounts={label=[[@7,31:31='b',<459>,1:31]], id=[[@21,87:87='b',<459>,1:87]], enabled=[[@31,115:115='b',<459>,1:115]]}, events={account_id=[[@17,72:72='a',<459>,1:72]], kind=[[@1,7:7='a',<459>,1:7]], active=[[@25,98:98='a',<459>,1:98]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=kind, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordKINDExplicitColumnAliasV0Test() {
		final String query = "select a.id as kind, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=kind}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[kind, label]",
				"{}",
				"{accounts={label=[[@7,21:21='b',<459>,1:21]], id=[[@21,77:77='b',<459>,1:77]], enabled=[[@31,105:105='b',<459>,1:105]]}, events={account_id=[[@17,62:62='a',<459>,1:62]], active=[[@25,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={kind=[[@5,15:18='kind',<393>,1:15]], label=[[@9,23:27='label',<459>,1:23]]}}",
				"{def_query0={query_dictionary={kind=[[@5,15:18='kind',<393>,1:15]], label=[[@9,23:27='label',<459>,1:23]]}, table_dictionary={accounts={label=[[@7,21:21='b',<459>,1:21]], id=[[@21,77:77='b',<459>,1:77]], enabled=[[@31,105:105='b',<459>,1:105]]}, events={account_id=[[@17,62:62='a',<459>,1:62]], active=[[@25,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={kind=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordKINDImplicitColumnAliasV0Test() {
		final String query = "select a.id kind, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=kind}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[kind, label]",
				"{}",
				"{accounts={label=[[@6,18:18='b',<459>,1:18]], id=[[@20,74:74='b',<459>,1:74]], enabled=[[@30,102:102='b',<459>,1:102]]}, events={account_id=[[@16,59:59='a',<459>,1:59]], active=[[@24,85:85='a',<459>,1:85]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={kind=[[@4,12:15='kind',<393>,1:12]], label=[[@8,20:24='label',<459>,1:20]]}}",
				"{def_query0={query_dictionary={kind=[[@4,12:15='kind',<393>,1:12]], label=[[@8,20:24='label',<459>,1:20]]}, table_dictionary={accounts={label=[[@6,18:18='b',<459>,1:18]], id=[[@20,74:74='b',<459>,1:74]], enabled=[[@30,102:102='b',<459>,1:102]]}, events={account_id=[[@16,59:59='a',<459>,1:59]], active=[[@24,85:85='a',<459>,1:85]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={kind=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordKINDPhysicalTableV0Test() {
		final String query = "select a.id, b.label from kind a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=kind}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{kind={account_id=[[@15,52:52='a',<459>,1:52]], active=[[@23,78:78='a',<459>,1:78]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,67:67='b',<459>,1:67]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={kind={account_id=[[@15,52:52='a',<459>,1:52]], active=[[@23,78:78='a',<459>,1:78]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,67:67='b',<459>,1:67]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=kind, b=accounts}}}");
	}

	@Test
	public void contextualKeywordKINDQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.kind a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=kind}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{analytics.kind={account_id=[[@17,62:62='a',<459>,1:62]], active=[[@25,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,77:77='b',<459>,1:77]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={analytics.kind={account_id=[[@17,62:62='a',<459>,1:62]], active=[[@25,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,77:77='b',<459>,1:77]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.kind, b=accounts}}}");
	}

	@Test
	public void contextualKeywordKINDExplicitTableAliasV0Test() {
		final String query = "select kind.id, b.label from events as kind join accounts b on kind.account_id = b.id where kind.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=kind}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=kind, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=kind}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=kind}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,16:16='b',<459>,1:16]], id=[[@20,81:81='b',<459>,1:81]]}, events={account_id=[[@16,63:66='kind',<393>,1:63]], active=[[@24,92:95='kind',<393>,1:92]], id=[[@1,7:10='kind',<393>,1:7]]}}",
				"{query0={id=[[@3,12:13='id',<459>,1:12]], label=[[@7,18:22='label',<459>,1:18]]}}",
				"{def_query0={query_dictionary={id=[[@3,12:13='id',<459>,1:12]], label=[[@7,18:22='label',<459>,1:18]]}, table_dictionary={accounts={label=[[@5,16:16='b',<459>,1:16]], id=[[@20,81:81='b',<459>,1:81]]}, events={account_id=[[@16,63:66='kind',<393>,1:63]], active=[[@24,92:95='kind',<393>,1:92]], id=[[@1,7:10='kind',<393>,1:7]]}}, filters=[{name=account_id, table_ref=kind}, {name=id, table_ref=b}, {name=active, table_ref=kind}], interface={id=[{name=id, table_ref=kind}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, kind=events}}}");
	}

	@Test
	public void contextualKeywordKINDImplicitTableAliasV0Test() {
		final String query = "select kind.id, b.label from events kind join accounts b on kind.account_id = b.id where kind.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=kind}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=kind, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=kind}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=kind}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,16:16='b',<459>,1:16]], id=[[@19,78:78='b',<459>,1:78]]}, events={account_id=[[@15,60:63='kind',<393>,1:60]], active=[[@23,89:92='kind',<393>,1:89]], id=[[@1,7:10='kind',<393>,1:7]]}}",
				"{query0={id=[[@3,12:13='id',<459>,1:12]], label=[[@7,18:22='label',<459>,1:18]]}}",
				"{def_query0={query_dictionary={id=[[@3,12:13='id',<459>,1:12]], label=[[@7,18:22='label',<459>,1:18]]}, table_dictionary={accounts={label=[[@5,16:16='b',<459>,1:16]], id=[[@19,78:78='b',<459>,1:78]]}, events={account_id=[[@15,60:63='kind',<393>,1:60]], active=[[@23,89:92='kind',<393>,1:89]], id=[[@1,7:10='kind',<393>,1:7]]}}, filters=[{name=account_id, table_ref=kind}, {name=id, table_ref=b}, {name=active, table_ref=kind}], interface={id=[{name=id, table_ref=kind}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, kind=events}}}");
	}

	@Test
	public void contextualKeywordKINDCteLabelAndOutputAliasV0Test() {
		final String query = "with kind as (select a.amount as kind, a.id as row_id from events a where a.active = 1) select kind.kind as result_value, kind.row_id from kind where kind.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=kind}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=kind}}, query={select={1={column={name=kind, table_ref=kind}, alias=result_value}, 2={column={name=row_id, table_ref=kind}}}, from={table={alias=null, table=kind}}, where={condition={left={column={name=row_id, table_ref=kind}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,21:21='a',<459>,1:21]], active=[[@20,74:74='a',<459>,1:74]], id=[[@11,39:39='a',<459>,1:39]]}}",
				"{query0={row_id=[[@15,47:52='row_id',<459>,1:47], [@33,122:125='kind',<393>,1:122], [@39,150:153='kind',<393>,1:150]], kind=[[@9,33:36='kind',<393>,1:33], [@27,95:98='kind',<393>,1:95]]}, query1={row_id=[[@35,127:132='row_id',<459>,1:127]], result_value=[[@31,108:119='result_value',<459>,1:108]]}}",
				"{def_query1={context_list={kind=query0}, query_dictionary={result_value=[[@31,108:119='result_value',<459>,1:108]], row_id=[[@35,127:132='row_id',<459>,1:127]]}, def_query0={query_dictionary={kind=[[@9,33:36='kind',<393>,1:33], [@27,95:98='kind',<393>,1:95]], row_id=[[@15,47:52='row_id',<459>,1:47], [@33,122:125='kind',<393>,1:122], [@39,150:153='kind',<393>,1:150]]}, table_dictionary={events={amount=[[@5,21:21='a',<459>,1:21]], active=[[@20,74:74='a',<459>,1:74]], id=[[@11,39:39='a',<459>,1:39]]}}, filters=[{name=active, table_ref=a}], interface={kind=[{name=amount, table_ref=a}], row_id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=kind}], interface={result_value=[{name=kind, table_ref=kind}], row_id=[{name=row_id, table_ref=kind}]}, table_alias={kind=query0}}}");
	}

	@Test
	public void contextualKeywordKINDWildcardQualifierV0Test() {
		final String query = "select kind.* from events as kind where kind.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=kind}}}, from={table={alias=kind, table=events}}, where={condition={left={column={name=active, table_ref=kind}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,40:43='kind',<393>,1:40]], *=[[@1,7:10='kind',<393>,1:7]]}}",
				"{query0={*=[[@3,12:12='*',<291>,1:12]]}}",
				"{def_query0={query_dictionary={*=[[@3,12:12='*',<291>,1:12]]}, table_dictionary={events={active=[[@9,40:43='kind',<393>,1:40]], *=[[@1,7:10='kind',<393>,1:7]]}}, filters=[{name=active, table_ref=kind}], interface={*=[{name=*, table_ref=kind}]}, table_alias={kind=events}}}");
	}

	@Test
	public void contextualKeywordJOBIDBareColumnV0Test() {
		final String query = "select job_id as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=job_id, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={job_id=[[@1,7:12='job_id',<394>,1:7]], active=[[@9,52:57='active',<459>,1:52]], id=[[@5,31:32='id',<459>,1:31]]}}",
				"{query0={result_value=[[@3,17:28='result_value',<459>,1:17]], id=[[@5,31:32='id',<459>,1:31]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,17:28='result_value',<459>,1:17]], id=[[@5,31:32='id',<459>,1:31]]}, table_dictionary={events={job_id=[[@1,7:12='job_id',<394>,1:7]], active=[[@9,52:57='active',<459>,1:52]], id=[[@5,31:32='id',<459>,1:31]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=job_id, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordJOBIDQualifiedColumnV0Test() {
		final String query = "select a.job_id as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=job_id, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,33:33='b',<459>,1:33]], id=[[@21,89:89='b',<459>,1:89]], enabled=[[@31,117:117='b',<459>,1:117]]}, events={account_id=[[@17,74:74='a',<459>,1:74]], job_id=[[@1,7:7='a',<459>,1:7]], active=[[@25,100:100='a',<459>,1:100]]}}",
				"{query0={result_value=[[@5,19:30='result_value',<459>,1:19]], label=[[@9,35:39='label',<459>,1:35]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,19:30='result_value',<459>,1:19]], label=[[@9,35:39='label',<459>,1:35]]}, table_dictionary={accounts={label=[[@7,33:33='b',<459>,1:33]], id=[[@21,89:89='b',<459>,1:89]], enabled=[[@31,117:117='b',<459>,1:117]]}, events={account_id=[[@17,74:74='a',<459>,1:74]], job_id=[[@1,7:7='a',<459>,1:7]], active=[[@25,100:100='a',<459>,1:100]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=job_id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordJOBIDExplicitColumnAliasV0Test() {
		final String query = "select a.id as job_id, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=job_id}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[job_id, label]",
				"{}",
				"{accounts={label=[[@7,23:23='b',<459>,1:23]], id=[[@21,79:79='b',<459>,1:79]], enabled=[[@31,107:107='b',<459>,1:107]]}, events={account_id=[[@17,64:64='a',<459>,1:64]], active=[[@25,90:90='a',<459>,1:90]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={job_id=[[@5,15:20='job_id',<394>,1:15]], label=[[@9,25:29='label',<459>,1:25]]}}",
				"{def_query0={query_dictionary={job_id=[[@5,15:20='job_id',<394>,1:15]], label=[[@9,25:29='label',<459>,1:25]]}, table_dictionary={accounts={label=[[@7,23:23='b',<459>,1:23]], id=[[@21,79:79='b',<459>,1:79]], enabled=[[@31,107:107='b',<459>,1:107]]}, events={account_id=[[@17,64:64='a',<459>,1:64]], active=[[@25,90:90='a',<459>,1:90]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={job_id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordJOBIDImplicitColumnAliasV0Test() {
		final String query = "select a.id job_id, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=job_id}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[job_id, label]",
				"{}",
				"{accounts={label=[[@6,20:20='b',<459>,1:20]], id=[[@20,76:76='b',<459>,1:76]], enabled=[[@30,104:104='b',<459>,1:104]]}, events={account_id=[[@16,61:61='a',<459>,1:61]], active=[[@24,87:87='a',<459>,1:87]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={job_id=[[@4,12:17='job_id',<394>,1:12]], label=[[@8,22:26='label',<459>,1:22]]}}",
				"{def_query0={query_dictionary={job_id=[[@4,12:17='job_id',<394>,1:12]], label=[[@8,22:26='label',<459>,1:22]]}, table_dictionary={accounts={label=[[@6,20:20='b',<459>,1:20]], id=[[@20,76:76='b',<459>,1:76]], enabled=[[@30,104:104='b',<459>,1:104]]}, events={account_id=[[@16,61:61='a',<459>,1:61]], active=[[@24,87:87='a',<459>,1:87]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={job_id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordJOBIDPhysicalTableV0Test() {
		final String query = "select a.id, b.label from job_id a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=job_id}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{job_id={account_id=[[@15,54:54='a',<459>,1:54]], active=[[@23,80:80='a',<459>,1:80]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,69:69='b',<459>,1:69]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={job_id={account_id=[[@15,54:54='a',<459>,1:54]], active=[[@23,80:80='a',<459>,1:80]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,69:69='b',<459>,1:69]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=job_id, b=accounts}}}");
	}

	@Test
	public void contextualKeywordJOBIDQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.job_id a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=job_id}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{analytics.job_id={account_id=[[@17,64:64='a',<459>,1:64]], active=[[@25,90:90='a',<459>,1:90]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,79:79='b',<459>,1:79]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={analytics.job_id={account_id=[[@17,64:64='a',<459>,1:64]], active=[[@25,90:90='a',<459>,1:90]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,79:79='b',<459>,1:79]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.job_id, b=accounts}}}");
	}

	@Test
	public void contextualKeywordJOBIDExplicitTableAliasV0Test() {
		final String query = "select job_id.id, b.label from events as job_id join accounts b on job_id.account_id = b.id where job_id.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=job_id}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=job_id, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=job_id}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=job_id}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,18:18='b',<459>,1:18]], id=[[@20,87:87='b',<459>,1:87]]}, events={account_id=[[@16,67:72='job_id',<394>,1:67]], active=[[@24,98:103='job_id',<394>,1:98]], id=[[@1,7:12='job_id',<394>,1:7]]}}",
				"{query0={id=[[@3,14:15='id',<459>,1:14]], label=[[@7,20:24='label',<459>,1:20]]}}",
				"{def_query0={query_dictionary={id=[[@3,14:15='id',<459>,1:14]], label=[[@7,20:24='label',<459>,1:20]]}, table_dictionary={accounts={label=[[@5,18:18='b',<459>,1:18]], id=[[@20,87:87='b',<459>,1:87]]}, events={account_id=[[@16,67:72='job_id',<394>,1:67]], active=[[@24,98:103='job_id',<394>,1:98]], id=[[@1,7:12='job_id',<394>,1:7]]}}, filters=[{name=account_id, table_ref=job_id}, {name=id, table_ref=b}, {name=active, table_ref=job_id}], interface={id=[{name=id, table_ref=job_id}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, job_id=events}}}");
	}

	@Test
	public void contextualKeywordJOBIDImplicitTableAliasV0Test() {
		final String query = "select job_id.id, b.label from events job_id join accounts b on job_id.account_id = b.id where job_id.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=job_id}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=job_id, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=job_id}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=job_id}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,18:18='b',<459>,1:18]], id=[[@19,84:84='b',<459>,1:84]]}, events={account_id=[[@15,64:69='job_id',<394>,1:64]], active=[[@23,95:100='job_id',<394>,1:95]], id=[[@1,7:12='job_id',<394>,1:7]]}}",
				"{query0={id=[[@3,14:15='id',<459>,1:14]], label=[[@7,20:24='label',<459>,1:20]]}}",
				"{def_query0={query_dictionary={id=[[@3,14:15='id',<459>,1:14]], label=[[@7,20:24='label',<459>,1:20]]}, table_dictionary={accounts={label=[[@5,18:18='b',<459>,1:18]], id=[[@19,84:84='b',<459>,1:84]]}, events={account_id=[[@15,64:69='job_id',<394>,1:64]], active=[[@23,95:100='job_id',<394>,1:95]], id=[[@1,7:12='job_id',<394>,1:7]]}}, filters=[{name=account_id, table_ref=job_id}, {name=id, table_ref=b}, {name=active, table_ref=job_id}], interface={id=[{name=id, table_ref=job_id}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, job_id=events}}}");
	}

	@Test
	public void contextualKeywordJOBIDCteLabelAndOutputAliasV0Test() {
		final String query = "with job_id as (select a.amount as job_id, a.id as row_id from events a where a.active = 1) select job_id.job_id as result_value, job_id.row_id from job_id where job_id.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=job_id}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=job_id}}, query={select={1={column={name=job_id, table_ref=job_id}, alias=result_value}, 2={column={name=row_id, table_ref=job_id}}}, from={table={alias=null, table=job_id}}, where={condition={left={column={name=row_id, table_ref=job_id}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,23:23='a',<459>,1:23]], active=[[@20,78:78='a',<459>,1:78]], id=[[@11,43:43='a',<459>,1:43]]}}",
				"{query0={row_id=[[@15,51:56='row_id',<459>,1:51], [@33,130:135='job_id',<394>,1:130], [@39,162:167='job_id',<394>,1:162]], job_id=[[@9,35:40='job_id',<394>,1:35], [@27,99:104='job_id',<394>,1:99]]}, query1={row_id=[[@35,137:142='row_id',<459>,1:137]], result_value=[[@31,116:127='result_value',<459>,1:116]]}}",
				"{def_query1={context_list={job_id=query0}, query_dictionary={result_value=[[@31,116:127='result_value',<459>,1:116]], row_id=[[@35,137:142='row_id',<459>,1:137]]}, def_query0={query_dictionary={job_id=[[@9,35:40='job_id',<394>,1:35], [@27,99:104='job_id',<394>,1:99]], row_id=[[@15,51:56='row_id',<459>,1:51], [@33,130:135='job_id',<394>,1:130], [@39,162:167='job_id',<394>,1:162]]}, table_dictionary={events={amount=[[@5,23:23='a',<459>,1:23]], active=[[@20,78:78='a',<459>,1:78]], id=[[@11,43:43='a',<459>,1:43]]}}, filters=[{name=active, table_ref=a}], interface={job_id=[{name=amount, table_ref=a}], row_id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=job_id}], interface={result_value=[{name=job_id, table_ref=job_id}], row_id=[{name=row_id, table_ref=job_id}]}, table_alias={job_id=query0}}}");
	}

	@Test
	public void contextualKeywordJOBIDWildcardQualifierV0Test() {
		final String query = "select job_id.* from events as job_id where job_id.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=job_id}}}, from={table={alias=job_id, table=events}}, where={condition={left={column={name=active, table_ref=job_id}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,44:49='job_id',<394>,1:44]], *=[[@1,7:12='job_id',<394>,1:7]]}}",
				"{query0={*=[[@3,14:14='*',<291>,1:14]]}}",
				"{def_query0={query_dictionary={*=[[@3,14:14='*',<291>,1:14]]}, table_dictionary={events={active=[[@9,44:49='job_id',<394>,1:44]], *=[[@1,7:12='job_id',<394>,1:7]]}}, filters=[{name=active, table_ref=job_id}], interface={*=[{name=*, table_ref=job_id}]}, table_alias={job_id=events}}}");
	}

	@Test
	public void contextualKeywordDAYOFMONTHBareColumnV0Test() {
		final String query = "select dayofmonth as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=dayofmonth, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@9,56:61='active',<459>,1:56]], id=[[@5,35:36='id',<459>,1:35]], dayofmonth=[[@1,7:16='dayofmonth',<414>,1:7]]}}",
				"{query0={result_value=[[@3,21:32='result_value',<459>,1:21]], id=[[@5,35:36='id',<459>,1:35]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,21:32='result_value',<459>,1:21]], id=[[@5,35:36='id',<459>,1:35]]}, table_dictionary={events={active=[[@9,56:61='active',<459>,1:56]], id=[[@5,35:36='id',<459>,1:35]], dayofmonth=[[@1,7:16='dayofmonth',<414>,1:7]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=dayofmonth, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordDAYOFMONTHQualifiedColumnV0Test() {
		final String query = "select a.dayofmonth as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=dayofmonth, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,37:37='b',<459>,1:37]], id=[[@21,93:93='b',<459>,1:93]], enabled=[[@31,121:121='b',<459>,1:121]]}, events={account_id=[[@17,78:78='a',<459>,1:78]], active=[[@25,104:104='a',<459>,1:104]], dayofmonth=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@5,23:34='result_value',<459>,1:23]], label=[[@9,39:43='label',<459>,1:39]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,23:34='result_value',<459>,1:23]], label=[[@9,39:43='label',<459>,1:39]]}, table_dictionary={accounts={label=[[@7,37:37='b',<459>,1:37]], id=[[@21,93:93='b',<459>,1:93]], enabled=[[@31,121:121='b',<459>,1:121]]}, events={account_id=[[@17,78:78='a',<459>,1:78]], active=[[@25,104:104='a',<459>,1:104]], dayofmonth=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=dayofmonth, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordDAYOFMONTHExplicitColumnAliasV0Test() {
		final String query = "select a.id as dayofmonth, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=dayofmonth}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[label, dayofmonth]",
				"{}",
				"{accounts={label=[[@7,27:27='b',<459>,1:27]], id=[[@21,83:83='b',<459>,1:83]], enabled=[[@31,111:111='b',<459>,1:111]]}, events={account_id=[[@17,68:68='a',<459>,1:68]], active=[[@25,94:94='a',<459>,1:94]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={label=[[@9,29:33='label',<459>,1:29]], dayofmonth=[[@5,15:24='dayofmonth',<414>,1:15]]}}",
				"{def_query0={query_dictionary={label=[[@9,29:33='label',<459>,1:29]], dayofmonth=[[@5,15:24='dayofmonth',<414>,1:15]]}, table_dictionary={accounts={label=[[@7,27:27='b',<459>,1:27]], id=[[@21,83:83='b',<459>,1:83]], enabled=[[@31,111:111='b',<459>,1:111]]}, events={account_id=[[@17,68:68='a',<459>,1:68]], active=[[@25,94:94='a',<459>,1:94]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={label=[{name=label, table_ref=b}], dayofmonth=[{name=id, table_ref=a}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordDAYOFMONTHImplicitColumnAliasV0Test() {
		final String query = "select a.id dayofmonth, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=dayofmonth}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[label, dayofmonth]",
				"{}",
				"{accounts={label=[[@6,24:24='b',<459>,1:24]], id=[[@20,80:80='b',<459>,1:80]], enabled=[[@30,108:108='b',<459>,1:108]]}, events={account_id=[[@16,65:65='a',<459>,1:65]], active=[[@24,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={label=[[@8,26:30='label',<459>,1:26]], dayofmonth=[[@4,12:21='dayofmonth',<414>,1:12]]}}",
				"{def_query0={query_dictionary={label=[[@8,26:30='label',<459>,1:26]], dayofmonth=[[@4,12:21='dayofmonth',<414>,1:12]]}, table_dictionary={accounts={label=[[@6,24:24='b',<459>,1:24]], id=[[@20,80:80='b',<459>,1:80]], enabled=[[@30,108:108='b',<459>,1:108]]}, events={account_id=[[@16,65:65='a',<459>,1:65]], active=[[@24,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={label=[{name=label, table_ref=b}], dayofmonth=[{name=id, table_ref=a}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordDAYOFMONTHPhysicalTableV0Test() {
		final String query = "select a.id, b.label from dayofmonth a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=dayofmonth}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,73:73='b',<459>,1:73]]}, dayofmonth={account_id=[[@15,58:58='a',<459>,1:58]], active=[[@23,84:84='a',<459>,1:84]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,73:73='b',<459>,1:73]]}, dayofmonth={account_id=[[@15,58:58='a',<459>,1:58]], active=[[@23,84:84='a',<459>,1:84]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=dayofmonth, b=accounts}}}");
	}

	@Test
	public void contextualKeywordDAYOFMONTHQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.dayofmonth a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=dayofmonth}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{analytics.dayofmonth={account_id=[[@17,68:68='a',<459>,1:68]], active=[[@25,94:94='a',<459>,1:94]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,83:83='b',<459>,1:83]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={analytics.dayofmonth={account_id=[[@17,68:68='a',<459>,1:68]], active=[[@25,94:94='a',<459>,1:94]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,83:83='b',<459>,1:83]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.dayofmonth, b=accounts}}}");
	}

	@Test
	public void contextualKeywordDAYOFMONTHExplicitTableAliasV0Test() {
		final String query = "select dayofmonth.id, b.label from events as dayofmonth join accounts b on dayofmonth.account_id = b.id where dayofmonth.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=dayofmonth}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=dayofmonth, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=dayofmonth}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=dayofmonth}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,22:22='b',<459>,1:22]], id=[[@20,99:99='b',<459>,1:99]]}, events={account_id=[[@16,75:84='dayofmonth',<414>,1:75]], active=[[@24,110:119='dayofmonth',<414>,1:110]], id=[[@1,7:16='dayofmonth',<414>,1:7]]}}",
				"{query0={id=[[@3,18:19='id',<459>,1:18]], label=[[@7,24:28='label',<459>,1:24]]}}",
				"{def_query0={query_dictionary={id=[[@3,18:19='id',<459>,1:18]], label=[[@7,24:28='label',<459>,1:24]]}, table_dictionary={accounts={label=[[@5,22:22='b',<459>,1:22]], id=[[@20,99:99='b',<459>,1:99]]}, events={account_id=[[@16,75:84='dayofmonth',<414>,1:75]], active=[[@24,110:119='dayofmonth',<414>,1:110]], id=[[@1,7:16='dayofmonth',<414>,1:7]]}}, filters=[{name=account_id, table_ref=dayofmonth}, {name=id, table_ref=b}, {name=active, table_ref=dayofmonth}], interface={id=[{name=id, table_ref=dayofmonth}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, dayofmonth=events}}}");
	}

	@Test
	public void contextualKeywordDAYOFMONTHImplicitTableAliasV0Test() {
		final String query = "select dayofmonth.id, b.label from events dayofmonth join accounts b on dayofmonth.account_id = b.id where dayofmonth.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=dayofmonth}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=dayofmonth, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=dayofmonth}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=dayofmonth}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,22:22='b',<459>,1:22]], id=[[@19,96:96='b',<459>,1:96]]}, events={account_id=[[@15,72:81='dayofmonth',<414>,1:72]], active=[[@23,107:116='dayofmonth',<414>,1:107]], id=[[@1,7:16='dayofmonth',<414>,1:7]]}}",
				"{query0={id=[[@3,18:19='id',<459>,1:18]], label=[[@7,24:28='label',<459>,1:24]]}}",
				"{def_query0={query_dictionary={id=[[@3,18:19='id',<459>,1:18]], label=[[@7,24:28='label',<459>,1:24]]}, table_dictionary={accounts={label=[[@5,22:22='b',<459>,1:22]], id=[[@19,96:96='b',<459>,1:96]]}, events={account_id=[[@15,72:81='dayofmonth',<414>,1:72]], active=[[@23,107:116='dayofmonth',<414>,1:107]], id=[[@1,7:16='dayofmonth',<414>,1:7]]}}, filters=[{name=account_id, table_ref=dayofmonth}, {name=id, table_ref=b}, {name=active, table_ref=dayofmonth}], interface={id=[{name=id, table_ref=dayofmonth}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, dayofmonth=events}}}");
	}

	@Test
	public void contextualKeywordDAYOFMONTHCteLabelAndOutputAliasV0Test() {
		final String query = "with dayofmonth as (select a.amount as dayofmonth, a.id as row_id from events a where a.active = 1) select dayofmonth.dayofmonth as result_value, dayofmonth.row_id from dayofmonth where dayofmonth.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=dayofmonth}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=dayofmonth}}, query={select={1={column={name=dayofmonth, table_ref=dayofmonth}, alias=result_value}, 2={column={name=row_id, table_ref=dayofmonth}}}, from={table={alias=null, table=dayofmonth}}, where={condition={left={column={name=row_id, table_ref=dayofmonth}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,27:27='a',<459>,1:27]], active=[[@20,86:86='a',<459>,1:86]], id=[[@11,51:51='a',<459>,1:51]]}}",
				"{query0={row_id=[[@15,59:64='row_id',<459>,1:59], [@33,146:155='dayofmonth',<414>,1:146], [@39,186:195='dayofmonth',<414>,1:186]], dayofmonth=[[@9,39:48='dayofmonth',<414>,1:39], [@27,107:116='dayofmonth',<414>,1:107]]}, query1={row_id=[[@35,157:162='row_id',<459>,1:157]], result_value=[[@31,132:143='result_value',<459>,1:132]]}}",
				"{def_query1={context_list={dayofmonth=query0}, query_dictionary={result_value=[[@31,132:143='result_value',<459>,1:132]], row_id=[[@35,157:162='row_id',<459>,1:157]]}, def_query0={query_dictionary={row_id=[[@15,59:64='row_id',<459>,1:59], [@33,146:155='dayofmonth',<414>,1:146], [@39,186:195='dayofmonth',<414>,1:186]], dayofmonth=[[@9,39:48='dayofmonth',<414>,1:39], [@27,107:116='dayofmonth',<414>,1:107]]}, table_dictionary={events={amount=[[@5,27:27='a',<459>,1:27]], active=[[@20,86:86='a',<459>,1:86]], id=[[@11,51:51='a',<459>,1:51]]}}, filters=[{name=active, table_ref=a}], interface={row_id=[{name=id, table_ref=a}], dayofmonth=[{name=amount, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=dayofmonth}], interface={result_value=[{name=dayofmonth, table_ref=dayofmonth}], row_id=[{name=row_id, table_ref=dayofmonth}]}, table_alias={dayofmonth=query0}}}");
	}

	@Test
	public void contextualKeywordDAYOFMONTHWildcardQualifierV0Test() {
		final String query = "select dayofmonth.* from events as dayofmonth where dayofmonth.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=dayofmonth}}}, from={table={alias=dayofmonth, table=events}}, where={condition={left={column={name=active, table_ref=dayofmonth}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,52:61='dayofmonth',<414>,1:52]], *=[[@1,7:16='dayofmonth',<414>,1:7]]}}",
				"{query0={*=[[@3,18:18='*',<291>,1:18]]}}",
				"{def_query0={query_dictionary={*=[[@3,18:18='*',<291>,1:18]]}, table_dictionary={events={active=[[@9,52:61='dayofmonth',<414>,1:52]], *=[[@1,7:16='dayofmonth',<414>,1:7]]}}, filters=[{name=active, table_ref=dayofmonth}], interface={*=[{name=*, table_ref=dayofmonth}]}, table_alias={dayofmonth=events}}}");
	}

	@Test
	public void contextualKeywordDAYOFWEEKBareColumnV0Test() {
		final String query = "select dayofweek as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=dayofweek, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={dayofweek=[[@1,7:15='dayofweek',<415>,1:7]], active=[[@9,55:60='active',<459>,1:55]], id=[[@5,34:35='id',<459>,1:34]]}}",
				"{query0={result_value=[[@3,20:31='result_value',<459>,1:20]], id=[[@5,34:35='id',<459>,1:34]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,20:31='result_value',<459>,1:20]], id=[[@5,34:35='id',<459>,1:34]]}, table_dictionary={events={dayofweek=[[@1,7:15='dayofweek',<415>,1:7]], active=[[@9,55:60='active',<459>,1:55]], id=[[@5,34:35='id',<459>,1:34]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=dayofweek, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordDAYOFWEEKQualifiedColumnV0Test() {
		final String query = "select a.dayofweek as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=dayofweek, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,36:36='b',<459>,1:36]], id=[[@21,92:92='b',<459>,1:92]], enabled=[[@31,120:120='b',<459>,1:120]]}, events={account_id=[[@17,77:77='a',<459>,1:77]], dayofweek=[[@1,7:7='a',<459>,1:7]], active=[[@25,103:103='a',<459>,1:103]]}}",
				"{query0={result_value=[[@5,22:33='result_value',<459>,1:22]], label=[[@9,38:42='label',<459>,1:38]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,22:33='result_value',<459>,1:22]], label=[[@9,38:42='label',<459>,1:38]]}, table_dictionary={accounts={label=[[@7,36:36='b',<459>,1:36]], id=[[@21,92:92='b',<459>,1:92]], enabled=[[@31,120:120='b',<459>,1:120]]}, events={account_id=[[@17,77:77='a',<459>,1:77]], dayofweek=[[@1,7:7='a',<459>,1:7]], active=[[@25,103:103='a',<459>,1:103]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=dayofweek, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordDAYOFWEEKExplicitColumnAliasV0Test() {
		final String query = "select a.id as dayofweek, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=dayofweek}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[dayofweek, label]",
				"{}",
				"{accounts={label=[[@7,26:26='b',<459>,1:26]], id=[[@21,82:82='b',<459>,1:82]], enabled=[[@31,110:110='b',<459>,1:110]]}, events={account_id=[[@17,67:67='a',<459>,1:67]], active=[[@25,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={dayofweek=[[@5,15:23='dayofweek',<415>,1:15]], label=[[@9,28:32='label',<459>,1:28]]}}",
				"{def_query0={query_dictionary={dayofweek=[[@5,15:23='dayofweek',<415>,1:15]], label=[[@9,28:32='label',<459>,1:28]]}, table_dictionary={accounts={label=[[@7,26:26='b',<459>,1:26]], id=[[@21,82:82='b',<459>,1:82]], enabled=[[@31,110:110='b',<459>,1:110]]}, events={account_id=[[@17,67:67='a',<459>,1:67]], active=[[@25,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={dayofweek=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordDAYOFWEEKImplicitColumnAliasV0Test() {
		final String query = "select a.id dayofweek, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=dayofweek}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[dayofweek, label]",
				"{}",
				"{accounts={label=[[@6,23:23='b',<459>,1:23]], id=[[@20,79:79='b',<459>,1:79]], enabled=[[@30,107:107='b',<459>,1:107]]}, events={account_id=[[@16,64:64='a',<459>,1:64]], active=[[@24,90:90='a',<459>,1:90]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={dayofweek=[[@4,12:20='dayofweek',<415>,1:12]], label=[[@8,25:29='label',<459>,1:25]]}}",
				"{def_query0={query_dictionary={dayofweek=[[@4,12:20='dayofweek',<415>,1:12]], label=[[@8,25:29='label',<459>,1:25]]}, table_dictionary={accounts={label=[[@6,23:23='b',<459>,1:23]], id=[[@20,79:79='b',<459>,1:79]], enabled=[[@30,107:107='b',<459>,1:107]]}, events={account_id=[[@16,64:64='a',<459>,1:64]], active=[[@24,90:90='a',<459>,1:90]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={dayofweek=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordDAYOFWEEKPhysicalTableV0Test() {
		final String query = "select a.id, b.label from dayofweek a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=dayofweek}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{dayofweek={account_id=[[@15,57:57='a',<459>,1:57]], active=[[@23,83:83='a',<459>,1:83]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,72:72='b',<459>,1:72]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={dayofweek={account_id=[[@15,57:57='a',<459>,1:57]], active=[[@23,83:83='a',<459>,1:83]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,72:72='b',<459>,1:72]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=dayofweek, b=accounts}}}");
	}

	@Test
	public void contextualKeywordDAYOFWEEKQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.dayofweek a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=dayofweek}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{analytics.dayofweek={account_id=[[@17,67:67='a',<459>,1:67]], active=[[@25,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,82:82='b',<459>,1:82]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={analytics.dayofweek={account_id=[[@17,67:67='a',<459>,1:67]], active=[[@25,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,82:82='b',<459>,1:82]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.dayofweek, b=accounts}}}");
	}

	@Test
	public void contextualKeywordDAYOFWEEKExplicitTableAliasV0Test() {
		final String query = "select dayofweek.id, b.label from events as dayofweek join accounts b on dayofweek.account_id = b.id where dayofweek.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=dayofweek}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=dayofweek, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=dayofweek}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=dayofweek}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,21:21='b',<459>,1:21]], id=[[@20,96:96='b',<459>,1:96]]}, events={account_id=[[@16,73:81='dayofweek',<415>,1:73]], active=[[@24,107:115='dayofweek',<415>,1:107]], id=[[@1,7:15='dayofweek',<415>,1:7]]}}",
				"{query0={id=[[@3,17:18='id',<459>,1:17]], label=[[@7,23:27='label',<459>,1:23]]}}",
				"{def_query0={query_dictionary={id=[[@3,17:18='id',<459>,1:17]], label=[[@7,23:27='label',<459>,1:23]]}, table_dictionary={accounts={label=[[@5,21:21='b',<459>,1:21]], id=[[@20,96:96='b',<459>,1:96]]}, events={account_id=[[@16,73:81='dayofweek',<415>,1:73]], active=[[@24,107:115='dayofweek',<415>,1:107]], id=[[@1,7:15='dayofweek',<415>,1:7]]}}, filters=[{name=account_id, table_ref=dayofweek}, {name=id, table_ref=b}, {name=active, table_ref=dayofweek}], interface={id=[{name=id, table_ref=dayofweek}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, dayofweek=events}}}");
	}

	@Test
	public void contextualKeywordDAYOFWEEKImplicitTableAliasV0Test() {
		final String query = "select dayofweek.id, b.label from events dayofweek join accounts b on dayofweek.account_id = b.id where dayofweek.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=dayofweek}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=dayofweek, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=dayofweek}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=dayofweek}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,21:21='b',<459>,1:21]], id=[[@19,93:93='b',<459>,1:93]]}, events={account_id=[[@15,70:78='dayofweek',<415>,1:70]], active=[[@23,104:112='dayofweek',<415>,1:104]], id=[[@1,7:15='dayofweek',<415>,1:7]]}}",
				"{query0={id=[[@3,17:18='id',<459>,1:17]], label=[[@7,23:27='label',<459>,1:23]]}}",
				"{def_query0={query_dictionary={id=[[@3,17:18='id',<459>,1:17]], label=[[@7,23:27='label',<459>,1:23]]}, table_dictionary={accounts={label=[[@5,21:21='b',<459>,1:21]], id=[[@19,93:93='b',<459>,1:93]]}, events={account_id=[[@15,70:78='dayofweek',<415>,1:70]], active=[[@23,104:112='dayofweek',<415>,1:104]], id=[[@1,7:15='dayofweek',<415>,1:7]]}}, filters=[{name=account_id, table_ref=dayofweek}, {name=id, table_ref=b}, {name=active, table_ref=dayofweek}], interface={id=[{name=id, table_ref=dayofweek}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, dayofweek=events}}}");
	}

	@Test
	public void contextualKeywordDAYOFWEEKCteLabelAndOutputAliasV0Test() {
		final String query = "with dayofweek as (select a.amount as dayofweek, a.id as row_id from events a where a.active = 1) select dayofweek.dayofweek as result_value, dayofweek.row_id from dayofweek where dayofweek.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=dayofweek}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=dayofweek}}, query={select={1={column={name=dayofweek, table_ref=dayofweek}, alias=result_value}, 2={column={name=row_id, table_ref=dayofweek}}}, from={table={alias=null, table=dayofweek}}, where={condition={left={column={name=row_id, table_ref=dayofweek}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,26:26='a',<459>,1:26]], active=[[@20,84:84='a',<459>,1:84]], id=[[@11,49:49='a',<459>,1:49]]}}",
				"{query0={row_id=[[@15,57:62='row_id',<459>,1:57], [@33,142:150='dayofweek',<415>,1:142], [@39,180:188='dayofweek',<415>,1:180]], dayofweek=[[@9,38:46='dayofweek',<415>,1:38], [@27,105:113='dayofweek',<415>,1:105]]}, query1={row_id=[[@35,152:157='row_id',<459>,1:152]], result_value=[[@31,128:139='result_value',<459>,1:128]]}}",
				"{def_query1={context_list={dayofweek=query0}, query_dictionary={result_value=[[@31,128:139='result_value',<459>,1:128]], row_id=[[@35,152:157='row_id',<459>,1:152]]}, def_query0={query_dictionary={dayofweek=[[@9,38:46='dayofweek',<415>,1:38], [@27,105:113='dayofweek',<415>,1:105]], row_id=[[@15,57:62='row_id',<459>,1:57], [@33,142:150='dayofweek',<415>,1:142], [@39,180:188='dayofweek',<415>,1:180]]}, table_dictionary={events={amount=[[@5,26:26='a',<459>,1:26]], active=[[@20,84:84='a',<459>,1:84]], id=[[@11,49:49='a',<459>,1:49]]}}, filters=[{name=active, table_ref=a}], interface={dayofweek=[{name=amount, table_ref=a}], row_id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=dayofweek}], interface={result_value=[{name=dayofweek, table_ref=dayofweek}], row_id=[{name=row_id, table_ref=dayofweek}]}, table_alias={dayofweek=query0}}}");
	}

	@Test
	public void contextualKeywordDAYOFWEEKWildcardQualifierV0Test() {
		final String query = "select dayofweek.* from events as dayofweek where dayofweek.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=dayofweek}}}, from={table={alias=dayofweek, table=events}}, where={condition={left={column={name=active, table_ref=dayofweek}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,50:58='dayofweek',<415>,1:50]], *=[[@1,7:15='dayofweek',<415>,1:7]]}}",
				"{query0={*=[[@3,17:17='*',<291>,1:17]]}}",
				"{def_query0={query_dictionary={*=[[@3,17:17='*',<291>,1:17]]}, table_dictionary={events={active=[[@9,50:58='dayofweek',<415>,1:50]], *=[[@1,7:15='dayofweek',<415>,1:7]]}}, filters=[{name=active, table_ref=dayofweek}], interface={*=[{name=*, table_ref=dayofweek}]}, table_alias={dayofweek=events}}}");
	}

	@Test
	public void contextualKeywordDAYOFWEEKISOBareColumnV0Test() {
		final String query = "select dayofweekiso as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=dayofweekiso, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={dayofweekiso=[[@1,7:18='dayofweekiso',<416>,1:7]], active=[[@9,58:63='active',<459>,1:58]], id=[[@5,37:38='id',<459>,1:37]]}}",
				"{query0={result_value=[[@3,23:34='result_value',<459>,1:23]], id=[[@5,37:38='id',<459>,1:37]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,23:34='result_value',<459>,1:23]], id=[[@5,37:38='id',<459>,1:37]]}, table_dictionary={events={dayofweekiso=[[@1,7:18='dayofweekiso',<416>,1:7]], active=[[@9,58:63='active',<459>,1:58]], id=[[@5,37:38='id',<459>,1:37]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=dayofweekiso, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordDAYOFWEEKISOQualifiedColumnV0Test() {
		final String query = "select a.dayofweekiso as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=dayofweekiso, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,39:39='b',<459>,1:39]], id=[[@21,95:95='b',<459>,1:95]], enabled=[[@31,123:123='b',<459>,1:123]]}, events={account_id=[[@17,80:80='a',<459>,1:80]], dayofweekiso=[[@1,7:7='a',<459>,1:7]], active=[[@25,106:106='a',<459>,1:106]]}}",
				"{query0={result_value=[[@5,25:36='result_value',<459>,1:25]], label=[[@9,41:45='label',<459>,1:41]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,25:36='result_value',<459>,1:25]], label=[[@9,41:45='label',<459>,1:41]]}, table_dictionary={accounts={label=[[@7,39:39='b',<459>,1:39]], id=[[@21,95:95='b',<459>,1:95]], enabled=[[@31,123:123='b',<459>,1:123]]}, events={account_id=[[@17,80:80='a',<459>,1:80]], dayofweekiso=[[@1,7:7='a',<459>,1:7]], active=[[@25,106:106='a',<459>,1:106]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=dayofweekiso, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordDAYOFWEEKISOExplicitColumnAliasV0Test() {
		final String query = "select a.id as dayofweekiso, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=dayofweekiso}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[dayofweekiso, label]",
				"{}",
				"{accounts={label=[[@7,29:29='b',<459>,1:29]], id=[[@21,85:85='b',<459>,1:85]], enabled=[[@31,113:113='b',<459>,1:113]]}, events={account_id=[[@17,70:70='a',<459>,1:70]], active=[[@25,96:96='a',<459>,1:96]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={dayofweekiso=[[@5,15:26='dayofweekiso',<416>,1:15]], label=[[@9,31:35='label',<459>,1:31]]}}",
				"{def_query0={query_dictionary={dayofweekiso=[[@5,15:26='dayofweekiso',<416>,1:15]], label=[[@9,31:35='label',<459>,1:31]]}, table_dictionary={accounts={label=[[@7,29:29='b',<459>,1:29]], id=[[@21,85:85='b',<459>,1:85]], enabled=[[@31,113:113='b',<459>,1:113]]}, events={account_id=[[@17,70:70='a',<459>,1:70]], active=[[@25,96:96='a',<459>,1:96]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={dayofweekiso=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordDAYOFWEEKISOImplicitColumnAliasV0Test() {
		final String query = "select a.id dayofweekiso, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=dayofweekiso}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[dayofweekiso, label]",
				"{}",
				"{accounts={label=[[@6,26:26='b',<459>,1:26]], id=[[@20,82:82='b',<459>,1:82]], enabled=[[@30,110:110='b',<459>,1:110]]}, events={account_id=[[@16,67:67='a',<459>,1:67]], active=[[@24,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={dayofweekiso=[[@4,12:23='dayofweekiso',<416>,1:12]], label=[[@8,28:32='label',<459>,1:28]]}}",
				"{def_query0={query_dictionary={dayofweekiso=[[@4,12:23='dayofweekiso',<416>,1:12]], label=[[@8,28:32='label',<459>,1:28]]}, table_dictionary={accounts={label=[[@6,26:26='b',<459>,1:26]], id=[[@20,82:82='b',<459>,1:82]], enabled=[[@30,110:110='b',<459>,1:110]]}, events={account_id=[[@16,67:67='a',<459>,1:67]], active=[[@24,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={dayofweekiso=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordDAYOFWEEKISOPhysicalTableV0Test() {
		final String query = "select a.id, b.label from dayofweekiso a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=dayofweekiso}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{dayofweekiso={account_id=[[@15,60:60='a',<459>,1:60]], active=[[@23,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,75:75='b',<459>,1:75]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={dayofweekiso={account_id=[[@15,60:60='a',<459>,1:60]], active=[[@23,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,75:75='b',<459>,1:75]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=dayofweekiso, b=accounts}}}");
	}

	@Test
	public void contextualKeywordDAYOFWEEKISOQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.dayofweekiso a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=dayofweekiso}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{analytics.dayofweekiso={account_id=[[@17,70:70='a',<459>,1:70]], active=[[@25,96:96='a',<459>,1:96]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,85:85='b',<459>,1:85]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={analytics.dayofweekiso={account_id=[[@17,70:70='a',<459>,1:70]], active=[[@25,96:96='a',<459>,1:96]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,85:85='b',<459>,1:85]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.dayofweekiso, b=accounts}}}");
	}

	@Test
	public void contextualKeywordDAYOFWEEKISOExplicitTableAliasV0Test() {
		final String query = "select dayofweekiso.id, b.label from events as dayofweekiso join accounts b on dayofweekiso.account_id = b.id where dayofweekiso.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=dayofweekiso}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=dayofweekiso, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=dayofweekiso}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=dayofweekiso}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,24:24='b',<459>,1:24]], id=[[@20,105:105='b',<459>,1:105]]}, events={account_id=[[@16,79:90='dayofweekiso',<416>,1:79]], active=[[@24,116:127='dayofweekiso',<416>,1:116]], id=[[@1,7:18='dayofweekiso',<416>,1:7]]}}",
				"{query0={id=[[@3,20:21='id',<459>,1:20]], label=[[@7,26:30='label',<459>,1:26]]}}",
				"{def_query0={query_dictionary={id=[[@3,20:21='id',<459>,1:20]], label=[[@7,26:30='label',<459>,1:26]]}, table_dictionary={accounts={label=[[@5,24:24='b',<459>,1:24]], id=[[@20,105:105='b',<459>,1:105]]}, events={account_id=[[@16,79:90='dayofweekiso',<416>,1:79]], active=[[@24,116:127='dayofweekiso',<416>,1:116]], id=[[@1,7:18='dayofweekiso',<416>,1:7]]}}, filters=[{name=account_id, table_ref=dayofweekiso}, {name=id, table_ref=b}, {name=active, table_ref=dayofweekiso}], interface={id=[{name=id, table_ref=dayofweekiso}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, dayofweekiso=events}}}");
	}

	@Test
	public void contextualKeywordDAYOFWEEKISOImplicitTableAliasV0Test() {
		final String query = "select dayofweekiso.id, b.label from events dayofweekiso join accounts b on dayofweekiso.account_id = b.id where dayofweekiso.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=dayofweekiso}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=dayofweekiso, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=dayofweekiso}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=dayofweekiso}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,24:24='b',<459>,1:24]], id=[[@19,102:102='b',<459>,1:102]]}, events={account_id=[[@15,76:87='dayofweekiso',<416>,1:76]], active=[[@23,113:124='dayofweekiso',<416>,1:113]], id=[[@1,7:18='dayofweekiso',<416>,1:7]]}}",
				"{query0={id=[[@3,20:21='id',<459>,1:20]], label=[[@7,26:30='label',<459>,1:26]]}}",
				"{def_query0={query_dictionary={id=[[@3,20:21='id',<459>,1:20]], label=[[@7,26:30='label',<459>,1:26]]}, table_dictionary={accounts={label=[[@5,24:24='b',<459>,1:24]], id=[[@19,102:102='b',<459>,1:102]]}, events={account_id=[[@15,76:87='dayofweekiso',<416>,1:76]], active=[[@23,113:124='dayofweekiso',<416>,1:113]], id=[[@1,7:18='dayofweekiso',<416>,1:7]]}}, filters=[{name=account_id, table_ref=dayofweekiso}, {name=id, table_ref=b}, {name=active, table_ref=dayofweekiso}], interface={id=[{name=id, table_ref=dayofweekiso}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, dayofweekiso=events}}}");
	}

	@Test
	public void contextualKeywordDAYOFWEEKISOCteLabelAndOutputAliasV0Test() {
		final String query = "with dayofweekiso as (select a.amount as dayofweekiso, a.id as row_id from events a where a.active = 1) select dayofweekiso.dayofweekiso as result_value, dayofweekiso.row_id from dayofweekiso where dayofweekiso.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=dayofweekiso}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=dayofweekiso}}, query={select={1={column={name=dayofweekiso, table_ref=dayofweekiso}, alias=result_value}, 2={column={name=row_id, table_ref=dayofweekiso}}}, from={table={alias=null, table=dayofweekiso}}, where={condition={left={column={name=row_id, table_ref=dayofweekiso}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,29:29='a',<459>,1:29]], active=[[@20,90:90='a',<459>,1:90]], id=[[@11,55:55='a',<459>,1:55]]}}",
				"{query0={row_id=[[@15,63:68='row_id',<459>,1:63], [@33,154:165='dayofweekiso',<416>,1:154], [@39,198:209='dayofweekiso',<416>,1:198]], dayofweekiso=[[@9,41:52='dayofweekiso',<416>,1:41], [@27,111:122='dayofweekiso',<416>,1:111]]}, query1={row_id=[[@35,167:172='row_id',<459>,1:167]], result_value=[[@31,140:151='result_value',<459>,1:140]]}}",
				"{def_query1={context_list={dayofweekiso=query0}, query_dictionary={result_value=[[@31,140:151='result_value',<459>,1:140]], row_id=[[@35,167:172='row_id',<459>,1:167]]}, def_query0={query_dictionary={dayofweekiso=[[@9,41:52='dayofweekiso',<416>,1:41], [@27,111:122='dayofweekiso',<416>,1:111]], row_id=[[@15,63:68='row_id',<459>,1:63], [@33,154:165='dayofweekiso',<416>,1:154], [@39,198:209='dayofweekiso',<416>,1:198]]}, table_dictionary={events={amount=[[@5,29:29='a',<459>,1:29]], active=[[@20,90:90='a',<459>,1:90]], id=[[@11,55:55='a',<459>,1:55]]}}, filters=[{name=active, table_ref=a}], interface={dayofweekiso=[{name=amount, table_ref=a}], row_id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=dayofweekiso}], interface={result_value=[{name=dayofweekiso, table_ref=dayofweekiso}], row_id=[{name=row_id, table_ref=dayofweekiso}]}, table_alias={dayofweekiso=query0}}}");
	}

	@Test
	public void contextualKeywordDAYOFWEEKISOWildcardQualifierV0Test() {
		final String query = "select dayofweekiso.* from events as dayofweekiso where dayofweekiso.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=dayofweekiso}}}, from={table={alias=dayofweekiso, table=events}}, where={condition={left={column={name=active, table_ref=dayofweekiso}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,56:67='dayofweekiso',<416>,1:56]], *=[[@1,7:18='dayofweekiso',<416>,1:7]]}}",
				"{query0={*=[[@3,20:20='*',<291>,1:20]]}}",
				"{def_query0={query_dictionary={*=[[@3,20:20='*',<291>,1:20]]}, table_dictionary={events={active=[[@9,56:67='dayofweekiso',<416>,1:56]], *=[[@1,7:18='dayofweekiso',<416>,1:7]]}}, filters=[{name=active, table_ref=dayofweekiso}], interface={*=[{name=*, table_ref=dayofweekiso}]}, table_alias={dayofweekiso=events}}}");
	}

	@Test
	public void contextualKeywordDAYOFYEARBareColumnV0Test() {
		final String query = "select dayofyear as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=dayofyear, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@9,55:60='active',<459>,1:55]], id=[[@5,34:35='id',<459>,1:34]], dayofyear=[[@1,7:15='dayofyear',<417>,1:7]]}}",
				"{query0={result_value=[[@3,20:31='result_value',<459>,1:20]], id=[[@5,34:35='id',<459>,1:34]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,20:31='result_value',<459>,1:20]], id=[[@5,34:35='id',<459>,1:34]]}, table_dictionary={events={active=[[@9,55:60='active',<459>,1:55]], id=[[@5,34:35='id',<459>,1:34]], dayofyear=[[@1,7:15='dayofyear',<417>,1:7]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=dayofyear, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordDAYOFYEARQualifiedColumnV0Test() {
		final String query = "select a.dayofyear as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=dayofyear, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,36:36='b',<459>,1:36]], id=[[@21,92:92='b',<459>,1:92]], enabled=[[@31,120:120='b',<459>,1:120]]}, events={account_id=[[@17,77:77='a',<459>,1:77]], active=[[@25,103:103='a',<459>,1:103]], dayofyear=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@5,22:33='result_value',<459>,1:22]], label=[[@9,38:42='label',<459>,1:38]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,22:33='result_value',<459>,1:22]], label=[[@9,38:42='label',<459>,1:38]]}, table_dictionary={accounts={label=[[@7,36:36='b',<459>,1:36]], id=[[@21,92:92='b',<459>,1:92]], enabled=[[@31,120:120='b',<459>,1:120]]}, events={account_id=[[@17,77:77='a',<459>,1:77]], active=[[@25,103:103='a',<459>,1:103]], dayofyear=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=dayofyear, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordDAYOFYEARExplicitColumnAliasV0Test() {
		final String query = "select a.id as dayofyear, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=dayofyear}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[label, dayofyear]",
				"{}",
				"{accounts={label=[[@7,26:26='b',<459>,1:26]], id=[[@21,82:82='b',<459>,1:82]], enabled=[[@31,110:110='b',<459>,1:110]]}, events={account_id=[[@17,67:67='a',<459>,1:67]], active=[[@25,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={label=[[@9,28:32='label',<459>,1:28]], dayofyear=[[@5,15:23='dayofyear',<417>,1:15]]}}",
				"{def_query0={query_dictionary={label=[[@9,28:32='label',<459>,1:28]], dayofyear=[[@5,15:23='dayofyear',<417>,1:15]]}, table_dictionary={accounts={label=[[@7,26:26='b',<459>,1:26]], id=[[@21,82:82='b',<459>,1:82]], enabled=[[@31,110:110='b',<459>,1:110]]}, events={account_id=[[@17,67:67='a',<459>,1:67]], active=[[@25,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={label=[{name=label, table_ref=b}], dayofyear=[{name=id, table_ref=a}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordDAYOFYEARImplicitColumnAliasV0Test() {
		final String query = "select a.id dayofyear, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=dayofyear}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[label, dayofyear]",
				"{}",
				"{accounts={label=[[@6,23:23='b',<459>,1:23]], id=[[@20,79:79='b',<459>,1:79]], enabled=[[@30,107:107='b',<459>,1:107]]}, events={account_id=[[@16,64:64='a',<459>,1:64]], active=[[@24,90:90='a',<459>,1:90]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={label=[[@8,25:29='label',<459>,1:25]], dayofyear=[[@4,12:20='dayofyear',<417>,1:12]]}}",
				"{def_query0={query_dictionary={label=[[@8,25:29='label',<459>,1:25]], dayofyear=[[@4,12:20='dayofyear',<417>,1:12]]}, table_dictionary={accounts={label=[[@6,23:23='b',<459>,1:23]], id=[[@20,79:79='b',<459>,1:79]], enabled=[[@30,107:107='b',<459>,1:107]]}, events={account_id=[[@16,64:64='a',<459>,1:64]], active=[[@24,90:90='a',<459>,1:90]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={label=[{name=label, table_ref=b}], dayofyear=[{name=id, table_ref=a}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordDAYOFYEARPhysicalTableV0Test() {
		final String query = "select a.id, b.label from dayofyear a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=dayofyear}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,72:72='b',<459>,1:72]]}, dayofyear={account_id=[[@15,57:57='a',<459>,1:57]], active=[[@23,83:83='a',<459>,1:83]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,72:72='b',<459>,1:72]]}, dayofyear={account_id=[[@15,57:57='a',<459>,1:57]], active=[[@23,83:83='a',<459>,1:83]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=dayofyear, b=accounts}}}");
	}

	@Test
	public void contextualKeywordDAYOFYEARQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.dayofyear a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=dayofyear}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,82:82='b',<459>,1:82]]}, analytics.dayofyear={account_id=[[@17,67:67='a',<459>,1:67]], active=[[@25,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,82:82='b',<459>,1:82]]}, analytics.dayofyear={account_id=[[@17,67:67='a',<459>,1:67]], active=[[@25,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.dayofyear, b=accounts}}}");
	}

	@Test
	public void contextualKeywordDAYOFYEARExplicitTableAliasV0Test() {
		final String query = "select dayofyear.id, b.label from events as dayofyear join accounts b on dayofyear.account_id = b.id where dayofyear.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=dayofyear}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=dayofyear, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=dayofyear}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=dayofyear}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,21:21='b',<459>,1:21]], id=[[@20,96:96='b',<459>,1:96]]}, events={account_id=[[@16,73:81='dayofyear',<417>,1:73]], active=[[@24,107:115='dayofyear',<417>,1:107]], id=[[@1,7:15='dayofyear',<417>,1:7]]}}",
				"{query0={id=[[@3,17:18='id',<459>,1:17]], label=[[@7,23:27='label',<459>,1:23]]}}",
				"{def_query0={query_dictionary={id=[[@3,17:18='id',<459>,1:17]], label=[[@7,23:27='label',<459>,1:23]]}, table_dictionary={accounts={label=[[@5,21:21='b',<459>,1:21]], id=[[@20,96:96='b',<459>,1:96]]}, events={account_id=[[@16,73:81='dayofyear',<417>,1:73]], active=[[@24,107:115='dayofyear',<417>,1:107]], id=[[@1,7:15='dayofyear',<417>,1:7]]}}, filters=[{name=account_id, table_ref=dayofyear}, {name=id, table_ref=b}, {name=active, table_ref=dayofyear}], interface={id=[{name=id, table_ref=dayofyear}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, dayofyear=events}}}");
	}

	@Test
	public void contextualKeywordDAYOFYEARImplicitTableAliasV0Test() {
		final String query = "select dayofyear.id, b.label from events dayofyear join accounts b on dayofyear.account_id = b.id where dayofyear.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=dayofyear}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=dayofyear, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=dayofyear}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=dayofyear}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,21:21='b',<459>,1:21]], id=[[@19,93:93='b',<459>,1:93]]}, events={account_id=[[@15,70:78='dayofyear',<417>,1:70]], active=[[@23,104:112='dayofyear',<417>,1:104]], id=[[@1,7:15='dayofyear',<417>,1:7]]}}",
				"{query0={id=[[@3,17:18='id',<459>,1:17]], label=[[@7,23:27='label',<459>,1:23]]}}",
				"{def_query0={query_dictionary={id=[[@3,17:18='id',<459>,1:17]], label=[[@7,23:27='label',<459>,1:23]]}, table_dictionary={accounts={label=[[@5,21:21='b',<459>,1:21]], id=[[@19,93:93='b',<459>,1:93]]}, events={account_id=[[@15,70:78='dayofyear',<417>,1:70]], active=[[@23,104:112='dayofyear',<417>,1:104]], id=[[@1,7:15='dayofyear',<417>,1:7]]}}, filters=[{name=account_id, table_ref=dayofyear}, {name=id, table_ref=b}, {name=active, table_ref=dayofyear}], interface={id=[{name=id, table_ref=dayofyear}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, dayofyear=events}}}");
	}

	@Test
	public void contextualKeywordDAYOFYEARCteLabelAndOutputAliasV0Test() {
		final String query = "with dayofyear as (select a.amount as dayofyear, a.id as row_id from events a where a.active = 1) select dayofyear.dayofyear as result_value, dayofyear.row_id from dayofyear where dayofyear.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=dayofyear}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=dayofyear}}, query={select={1={column={name=dayofyear, table_ref=dayofyear}, alias=result_value}, 2={column={name=row_id, table_ref=dayofyear}}}, from={table={alias=null, table=dayofyear}}, where={condition={left={column={name=row_id, table_ref=dayofyear}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,26:26='a',<459>,1:26]], active=[[@20,84:84='a',<459>,1:84]], id=[[@11,49:49='a',<459>,1:49]]}}",
				"{query0={row_id=[[@15,57:62='row_id',<459>,1:57], [@33,142:150='dayofyear',<417>,1:142], [@39,180:188='dayofyear',<417>,1:180]], dayofyear=[[@9,38:46='dayofyear',<417>,1:38], [@27,105:113='dayofyear',<417>,1:105]]}, query1={row_id=[[@35,152:157='row_id',<459>,1:152]], result_value=[[@31,128:139='result_value',<459>,1:128]]}}",
				"{def_query1={context_list={dayofyear=query0}, query_dictionary={result_value=[[@31,128:139='result_value',<459>,1:128]], row_id=[[@35,152:157='row_id',<459>,1:152]]}, def_query0={query_dictionary={row_id=[[@15,57:62='row_id',<459>,1:57], [@33,142:150='dayofyear',<417>,1:142], [@39,180:188='dayofyear',<417>,1:180]], dayofyear=[[@9,38:46='dayofyear',<417>,1:38], [@27,105:113='dayofyear',<417>,1:105]]}, table_dictionary={events={amount=[[@5,26:26='a',<459>,1:26]], active=[[@20,84:84='a',<459>,1:84]], id=[[@11,49:49='a',<459>,1:49]]}}, filters=[{name=active, table_ref=a}], interface={row_id=[{name=id, table_ref=a}], dayofyear=[{name=amount, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=dayofyear}], interface={result_value=[{name=dayofyear, table_ref=dayofyear}], row_id=[{name=row_id, table_ref=dayofyear}]}, table_alias={dayofyear=query0}}}");
	}

	@Test
	public void contextualKeywordDAYOFYEARWildcardQualifierV0Test() {
		final String query = "select dayofyear.* from events as dayofyear where dayofyear.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=dayofyear}}}, from={table={alias=dayofyear, table=events}}, where={condition={left={column={name=active, table_ref=dayofyear}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,50:58='dayofyear',<417>,1:50]], *=[[@1,7:15='dayofyear',<417>,1:7]]}}",
				"{query0={*=[[@3,17:17='*',<291>,1:17]]}}",
				"{def_query0={query_dictionary={*=[[@3,17:17='*',<291>,1:17]]}, table_dictionary={events={active=[[@9,50:58='dayofyear',<417>,1:50]], *=[[@1,7:15='dayofyear',<417>,1:7]]}}, filters=[{name=active, table_ref=dayofyear}], interface={*=[{name=*, table_ref=dayofyear}]}, table_alias={dayofyear=events}}}");
	}

	@Test
	public void contextualKeywordWEEKISOBareColumnV0Test() {
		final String query = "select weekiso as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=weekiso, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={weekiso=[[@1,7:13='weekiso',<418>,1:7]], active=[[@9,53:58='active',<459>,1:53]], id=[[@5,32:33='id',<459>,1:32]]}}",
				"{query0={result_value=[[@3,18:29='result_value',<459>,1:18]], id=[[@5,32:33='id',<459>,1:32]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,18:29='result_value',<459>,1:18]], id=[[@5,32:33='id',<459>,1:32]]}, table_dictionary={events={weekiso=[[@1,7:13='weekiso',<418>,1:7]], active=[[@9,53:58='active',<459>,1:53]], id=[[@5,32:33='id',<459>,1:32]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=weekiso, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordWEEKISOQualifiedColumnV0Test() {
		final String query = "select a.weekiso as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=weekiso, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,34:34='b',<459>,1:34]], id=[[@21,90:90='b',<459>,1:90]], enabled=[[@31,118:118='b',<459>,1:118]]}, events={weekiso=[[@1,7:7='a',<459>,1:7]], account_id=[[@17,75:75='a',<459>,1:75]], active=[[@25,101:101='a',<459>,1:101]]}}",
				"{query0={result_value=[[@5,20:31='result_value',<459>,1:20]], label=[[@9,36:40='label',<459>,1:36]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,20:31='result_value',<459>,1:20]], label=[[@9,36:40='label',<459>,1:36]]}, table_dictionary={accounts={label=[[@7,34:34='b',<459>,1:34]], id=[[@21,90:90='b',<459>,1:90]], enabled=[[@31,118:118='b',<459>,1:118]]}, events={weekiso=[[@1,7:7='a',<459>,1:7]], account_id=[[@17,75:75='a',<459>,1:75]], active=[[@25,101:101='a',<459>,1:101]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=weekiso, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordWEEKISOExplicitColumnAliasV0Test() {
		final String query = "select a.id as weekiso, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=weekiso}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[weekiso, label]",
				"{}",
				"{accounts={label=[[@7,24:24='b',<459>,1:24]], id=[[@21,80:80='b',<459>,1:80]], enabled=[[@31,108:108='b',<459>,1:108]]}, events={account_id=[[@17,65:65='a',<459>,1:65]], active=[[@25,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={weekiso=[[@5,15:21='weekiso',<418>,1:15]], label=[[@9,26:30='label',<459>,1:26]]}}",
				"{def_query0={query_dictionary={weekiso=[[@5,15:21='weekiso',<418>,1:15]], label=[[@9,26:30='label',<459>,1:26]]}, table_dictionary={accounts={label=[[@7,24:24='b',<459>,1:24]], id=[[@21,80:80='b',<459>,1:80]], enabled=[[@31,108:108='b',<459>,1:108]]}, events={account_id=[[@17,65:65='a',<459>,1:65]], active=[[@25,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={weekiso=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordWEEKISOImplicitColumnAliasV0Test() {
		final String query = "select a.id weekiso, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=weekiso}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[weekiso, label]",
				"{}",
				"{accounts={label=[[@6,21:21='b',<459>,1:21]], id=[[@20,77:77='b',<459>,1:77]], enabled=[[@30,105:105='b',<459>,1:105]]}, events={account_id=[[@16,62:62='a',<459>,1:62]], active=[[@24,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={weekiso=[[@4,12:18='weekiso',<418>,1:12]], label=[[@8,23:27='label',<459>,1:23]]}}",
				"{def_query0={query_dictionary={weekiso=[[@4,12:18='weekiso',<418>,1:12]], label=[[@8,23:27='label',<459>,1:23]]}, table_dictionary={accounts={label=[[@6,21:21='b',<459>,1:21]], id=[[@20,77:77='b',<459>,1:77]], enabled=[[@30,105:105='b',<459>,1:105]]}, events={account_id=[[@16,62:62='a',<459>,1:62]], active=[[@24,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={weekiso=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordWEEKISOPhysicalTableV0Test() {
		final String query = "select a.id, b.label from weekiso a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=weekiso}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{weekiso={account_id=[[@15,55:55='a',<459>,1:55]], active=[[@23,81:81='a',<459>,1:81]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,70:70='b',<459>,1:70]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={weekiso={account_id=[[@15,55:55='a',<459>,1:55]], active=[[@23,81:81='a',<459>,1:81]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,70:70='b',<459>,1:70]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=weekiso, b=accounts}}}");
	}

	@Test
	public void contextualKeywordWEEKISOQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.weekiso a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=weekiso}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,80:80='b',<459>,1:80]]}, analytics.weekiso={account_id=[[@17,65:65='a',<459>,1:65]], active=[[@25,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,80:80='b',<459>,1:80]]}, analytics.weekiso={account_id=[[@17,65:65='a',<459>,1:65]], active=[[@25,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.weekiso, b=accounts}}}");
	}

	@Test
	public void contextualKeywordWEEKISOExplicitTableAliasV0Test() {
		final String query = "select weekiso.id, b.label from events as weekiso join accounts b on weekiso.account_id = b.id where weekiso.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=weekiso}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=weekiso, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=weekiso}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=weekiso}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,19:19='b',<459>,1:19]], id=[[@20,90:90='b',<459>,1:90]]}, events={account_id=[[@16,69:75='weekiso',<418>,1:69]], active=[[@24,101:107='weekiso',<418>,1:101]], id=[[@1,7:13='weekiso',<418>,1:7]]}}",
				"{query0={id=[[@3,15:16='id',<459>,1:15]], label=[[@7,21:25='label',<459>,1:21]]}}",
				"{def_query0={query_dictionary={id=[[@3,15:16='id',<459>,1:15]], label=[[@7,21:25='label',<459>,1:21]]}, table_dictionary={accounts={label=[[@5,19:19='b',<459>,1:19]], id=[[@20,90:90='b',<459>,1:90]]}, events={account_id=[[@16,69:75='weekiso',<418>,1:69]], active=[[@24,101:107='weekiso',<418>,1:101]], id=[[@1,7:13='weekiso',<418>,1:7]]}}, filters=[{name=account_id, table_ref=weekiso}, {name=id, table_ref=b}, {name=active, table_ref=weekiso}], interface={id=[{name=id, table_ref=weekiso}], label=[{name=label, table_ref=b}]}, table_alias={weekiso=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordWEEKISOImplicitTableAliasV0Test() {
		final String query = "select weekiso.id, b.label from events weekiso join accounts b on weekiso.account_id = b.id where weekiso.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=weekiso}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=weekiso, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=weekiso}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=weekiso}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,19:19='b',<459>,1:19]], id=[[@19,87:87='b',<459>,1:87]]}, events={account_id=[[@15,66:72='weekiso',<418>,1:66]], active=[[@23,98:104='weekiso',<418>,1:98]], id=[[@1,7:13='weekiso',<418>,1:7]]}}",
				"{query0={id=[[@3,15:16='id',<459>,1:15]], label=[[@7,21:25='label',<459>,1:21]]}}",
				"{def_query0={query_dictionary={id=[[@3,15:16='id',<459>,1:15]], label=[[@7,21:25='label',<459>,1:21]]}, table_dictionary={accounts={label=[[@5,19:19='b',<459>,1:19]], id=[[@19,87:87='b',<459>,1:87]]}, events={account_id=[[@15,66:72='weekiso',<418>,1:66]], active=[[@23,98:104='weekiso',<418>,1:98]], id=[[@1,7:13='weekiso',<418>,1:7]]}}, filters=[{name=account_id, table_ref=weekiso}, {name=id, table_ref=b}, {name=active, table_ref=weekiso}], interface={id=[{name=id, table_ref=weekiso}], label=[{name=label, table_ref=b}]}, table_alias={weekiso=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordWEEKISOCteLabelAndOutputAliasV0Test() {
		final String query = "with weekiso as (select a.amount as weekiso, a.id as row_id from events a where a.active = 1) select weekiso.weekiso as result_value, weekiso.row_id from weekiso where weekiso.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=weekiso}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=weekiso}}, query={select={1={column={name=weekiso, table_ref=weekiso}, alias=result_value}, 2={column={name=row_id, table_ref=weekiso}}}, from={table={alias=null, table=weekiso}}, where={condition={left={column={name=row_id, table_ref=weekiso}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,24:24='a',<459>,1:24]], active=[[@20,80:80='a',<459>,1:80]], id=[[@11,45:45='a',<459>,1:45]]}}",
				"{query0={row_id=[[@15,53:58='row_id',<459>,1:53], [@33,134:140='weekiso',<418>,1:134], [@39,168:174='weekiso',<418>,1:168]], weekiso=[[@9,36:42='weekiso',<418>,1:36], [@27,101:107='weekiso',<418>,1:101]]}, query1={row_id=[[@35,142:147='row_id',<459>,1:142]], result_value=[[@31,120:131='result_value',<459>,1:120]]}}",
				"{def_query1={context_list={weekiso=query0}, query_dictionary={result_value=[[@31,120:131='result_value',<459>,1:120]], row_id=[[@35,142:147='row_id',<459>,1:142]]}, def_query0={query_dictionary={weekiso=[[@9,36:42='weekiso',<418>,1:36], [@27,101:107='weekiso',<418>,1:101]], row_id=[[@15,53:58='row_id',<459>,1:53], [@33,134:140='weekiso',<418>,1:134], [@39,168:174='weekiso',<418>,1:168]]}, table_dictionary={events={amount=[[@5,24:24='a',<459>,1:24]], active=[[@20,80:80='a',<459>,1:80]], id=[[@11,45:45='a',<459>,1:45]]}}, filters=[{name=active, table_ref=a}], interface={weekiso=[{name=amount, table_ref=a}], row_id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=weekiso}], interface={result_value=[{name=weekiso, table_ref=weekiso}], row_id=[{name=row_id, table_ref=weekiso}]}, table_alias={weekiso=query0}}}");
	}

	@Test
	public void contextualKeywordWEEKISOWildcardQualifierV0Test() {
		final String query = "select weekiso.* from events as weekiso where weekiso.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=weekiso}}}, from={table={alias=weekiso, table=events}}, where={condition={left={column={name=active, table_ref=weekiso}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,46:52='weekiso',<418>,1:46]], *=[[@1,7:13='weekiso',<418>,1:7]]}}",
				"{query0={*=[[@3,15:15='*',<291>,1:15]]}}",
				"{def_query0={query_dictionary={*=[[@3,15:15='*',<291>,1:15]]}, table_dictionary={events={active=[[@9,46:52='weekiso',<418>,1:46]], *=[[@1,7:13='weekiso',<418>,1:7]]}}, filters=[{name=active, table_ref=weekiso}], interface={*=[{name=*, table_ref=weekiso}]}, table_alias={weekiso=events}}}");
	}

	@Test
	public void contextualKeywordWEEKOFYEARBareColumnV0Test() {
		final String query = "select weekofyear as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=weekofyear, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@9,56:61='active',<459>,1:56]], id=[[@5,35:36='id',<459>,1:35]], weekofyear=[[@1,7:16='weekofyear',<419>,1:7]]}}",
				"{query0={result_value=[[@3,21:32='result_value',<459>,1:21]], id=[[@5,35:36='id',<459>,1:35]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,21:32='result_value',<459>,1:21]], id=[[@5,35:36='id',<459>,1:35]]}, table_dictionary={events={active=[[@9,56:61='active',<459>,1:56]], id=[[@5,35:36='id',<459>,1:35]], weekofyear=[[@1,7:16='weekofyear',<419>,1:7]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=weekofyear, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordWEEKOFYEARQualifiedColumnV0Test() {
		final String query = "select a.weekofyear as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=weekofyear, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,37:37='b',<459>,1:37]], id=[[@21,93:93='b',<459>,1:93]], enabled=[[@31,121:121='b',<459>,1:121]]}, events={account_id=[[@17,78:78='a',<459>,1:78]], active=[[@25,104:104='a',<459>,1:104]], weekofyear=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@5,23:34='result_value',<459>,1:23]], label=[[@9,39:43='label',<459>,1:39]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,23:34='result_value',<459>,1:23]], label=[[@9,39:43='label',<459>,1:39]]}, table_dictionary={accounts={label=[[@7,37:37='b',<459>,1:37]], id=[[@21,93:93='b',<459>,1:93]], enabled=[[@31,121:121='b',<459>,1:121]]}, events={account_id=[[@17,78:78='a',<459>,1:78]], active=[[@25,104:104='a',<459>,1:104]], weekofyear=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=weekofyear, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordWEEKOFYEARExplicitColumnAliasV0Test() {
		final String query = "select a.id as weekofyear, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=weekofyear}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[label, weekofyear]",
				"{}",
				"{accounts={label=[[@7,27:27='b',<459>,1:27]], id=[[@21,83:83='b',<459>,1:83]], enabled=[[@31,111:111='b',<459>,1:111]]}, events={account_id=[[@17,68:68='a',<459>,1:68]], active=[[@25,94:94='a',<459>,1:94]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={weekofyear=[[@5,15:24='weekofyear',<419>,1:15]], label=[[@9,29:33='label',<459>,1:29]]}}",
				"{def_query0={query_dictionary={label=[[@9,29:33='label',<459>,1:29]], weekofyear=[[@5,15:24='weekofyear',<419>,1:15]]}, table_dictionary={accounts={label=[[@7,27:27='b',<459>,1:27]], id=[[@21,83:83='b',<459>,1:83]], enabled=[[@31,111:111='b',<459>,1:111]]}, events={account_id=[[@17,68:68='a',<459>,1:68]], active=[[@25,94:94='a',<459>,1:94]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={label=[{name=label, table_ref=b}], weekofyear=[{name=id, table_ref=a}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordWEEKOFYEARImplicitColumnAliasV0Test() {
		final String query = "select a.id weekofyear, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=weekofyear}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[label, weekofyear]",
				"{}",
				"{accounts={label=[[@6,24:24='b',<459>,1:24]], id=[[@20,80:80='b',<459>,1:80]], enabled=[[@30,108:108='b',<459>,1:108]]}, events={account_id=[[@16,65:65='a',<459>,1:65]], active=[[@24,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={weekofyear=[[@4,12:21='weekofyear',<419>,1:12]], label=[[@8,26:30='label',<459>,1:26]]}}",
				"{def_query0={query_dictionary={label=[[@8,26:30='label',<459>,1:26]], weekofyear=[[@4,12:21='weekofyear',<419>,1:12]]}, table_dictionary={accounts={label=[[@6,24:24='b',<459>,1:24]], id=[[@20,80:80='b',<459>,1:80]], enabled=[[@30,108:108='b',<459>,1:108]]}, events={account_id=[[@16,65:65='a',<459>,1:65]], active=[[@24,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={label=[{name=label, table_ref=b}], weekofyear=[{name=id, table_ref=a}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordWEEKOFYEARPhysicalTableV0Test() {
		final String query = "select a.id, b.label from weekofyear a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=weekofyear}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,73:73='b',<459>,1:73]]}, weekofyear={account_id=[[@15,58:58='a',<459>,1:58]], active=[[@23,84:84='a',<459>,1:84]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,73:73='b',<459>,1:73]]}, weekofyear={account_id=[[@15,58:58='a',<459>,1:58]], active=[[@23,84:84='a',<459>,1:84]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=weekofyear, b=accounts}}}");
	}

	@Test
	public void contextualKeywordWEEKOFYEARQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.weekofyear a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=weekofyear}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{analytics.weekofyear={account_id=[[@17,68:68='a',<459>,1:68]], active=[[@25,94:94='a',<459>,1:94]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,83:83='b',<459>,1:83]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={analytics.weekofyear={account_id=[[@17,68:68='a',<459>,1:68]], active=[[@25,94:94='a',<459>,1:94]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,83:83='b',<459>,1:83]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.weekofyear, b=accounts}}}");
	}

	@Test
	public void contextualKeywordWEEKOFYEARExplicitTableAliasV0Test() {
		final String query = "select weekofyear.id, b.label from events as weekofyear join accounts b on weekofyear.account_id = b.id where weekofyear.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=weekofyear}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=weekofyear, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=weekofyear}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=weekofyear}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,22:22='b',<459>,1:22]], id=[[@20,99:99='b',<459>,1:99]]}, events={account_id=[[@16,75:84='weekofyear',<419>,1:75]], active=[[@24,110:119='weekofyear',<419>,1:110]], id=[[@1,7:16='weekofyear',<419>,1:7]]}}",
				"{query0={id=[[@3,18:19='id',<459>,1:18]], label=[[@7,24:28='label',<459>,1:24]]}}",
				"{def_query0={query_dictionary={id=[[@3,18:19='id',<459>,1:18]], label=[[@7,24:28='label',<459>,1:24]]}, table_dictionary={accounts={label=[[@5,22:22='b',<459>,1:22]], id=[[@20,99:99='b',<459>,1:99]]}, events={account_id=[[@16,75:84='weekofyear',<419>,1:75]], active=[[@24,110:119='weekofyear',<419>,1:110]], id=[[@1,7:16='weekofyear',<419>,1:7]]}}, filters=[{name=account_id, table_ref=weekofyear}, {name=id, table_ref=b}, {name=active, table_ref=weekofyear}], interface={id=[{name=id, table_ref=weekofyear}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, weekofyear=events}}}");
	}

	@Test
	public void contextualKeywordWEEKOFYEARImplicitTableAliasV0Test() {
		final String query = "select weekofyear.id, b.label from events weekofyear join accounts b on weekofyear.account_id = b.id where weekofyear.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=weekofyear}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=weekofyear, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=weekofyear}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=weekofyear}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,22:22='b',<459>,1:22]], id=[[@19,96:96='b',<459>,1:96]]}, events={account_id=[[@15,72:81='weekofyear',<419>,1:72]], active=[[@23,107:116='weekofyear',<419>,1:107]], id=[[@1,7:16='weekofyear',<419>,1:7]]}}",
				"{query0={id=[[@3,18:19='id',<459>,1:18]], label=[[@7,24:28='label',<459>,1:24]]}}",
				"{def_query0={query_dictionary={id=[[@3,18:19='id',<459>,1:18]], label=[[@7,24:28='label',<459>,1:24]]}, table_dictionary={accounts={label=[[@5,22:22='b',<459>,1:22]], id=[[@19,96:96='b',<459>,1:96]]}, events={account_id=[[@15,72:81='weekofyear',<419>,1:72]], active=[[@23,107:116='weekofyear',<419>,1:107]], id=[[@1,7:16='weekofyear',<419>,1:7]]}}, filters=[{name=account_id, table_ref=weekofyear}, {name=id, table_ref=b}, {name=active, table_ref=weekofyear}], interface={id=[{name=id, table_ref=weekofyear}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, weekofyear=events}}}");
	}

	@Test
	public void contextualKeywordWEEKOFYEARCteLabelAndOutputAliasV0Test() {
		final String query = "with weekofyear as (select a.amount as weekofyear, a.id as row_id from events a where a.active = 1) select weekofyear.weekofyear as result_value, weekofyear.row_id from weekofyear where weekofyear.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=weekofyear}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=weekofyear}}, query={select={1={column={name=weekofyear, table_ref=weekofyear}, alias=result_value}, 2={column={name=row_id, table_ref=weekofyear}}}, from={table={alias=null, table=weekofyear}}, where={condition={left={column={name=row_id, table_ref=weekofyear}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,27:27='a',<459>,1:27]], active=[[@20,86:86='a',<459>,1:86]], id=[[@11,51:51='a',<459>,1:51]]}}",
				"{query0={weekofyear=[[@9,39:48='weekofyear',<419>,1:39], [@27,107:116='weekofyear',<419>,1:107]], row_id=[[@15,59:64='row_id',<459>,1:59], [@33,146:155='weekofyear',<419>,1:146], [@39,186:195='weekofyear',<419>,1:186]]}, query1={row_id=[[@35,157:162='row_id',<459>,1:157]], result_value=[[@31,132:143='result_value',<459>,1:132]]}}",
				"{def_query1={context_list={weekofyear=query0}, query_dictionary={result_value=[[@31,132:143='result_value',<459>,1:132]], row_id=[[@35,157:162='row_id',<459>,1:157]]}, def_query0={query_dictionary={weekofyear=[[@9,39:48='weekofyear',<419>,1:39], [@27,107:116='weekofyear',<419>,1:107]], row_id=[[@15,59:64='row_id',<459>,1:59], [@33,146:155='weekofyear',<419>,1:146], [@39,186:195='weekofyear',<419>,1:186]]}, table_dictionary={events={amount=[[@5,27:27='a',<459>,1:27]], active=[[@20,86:86='a',<459>,1:86]], id=[[@11,51:51='a',<459>,1:51]]}}, filters=[{name=active, table_ref=a}], interface={weekofyear=[{name=amount, table_ref=a}], row_id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=weekofyear}], interface={result_value=[{name=weekofyear, table_ref=weekofyear}], row_id=[{name=row_id, table_ref=weekofyear}]}, table_alias={weekofyear=query0}}}");
	}

	@Test
	public void contextualKeywordWEEKOFYEARWildcardQualifierV0Test() {
		final String query = "select weekofyear.* from events as weekofyear where weekofyear.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=weekofyear}}}, from={table={alias=weekofyear, table=events}}, where={condition={left={column={name=active, table_ref=weekofyear}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,52:61='weekofyear',<419>,1:52]], *=[[@1,7:16='weekofyear',<419>,1:7]]}}",
				"{query0={*=[[@3,18:18='*',<291>,1:18]]}}",
				"{def_query0={query_dictionary={*=[[@3,18:18='*',<291>,1:18]]}, table_dictionary={events={active=[[@9,52:61='weekofyear',<419>,1:52]], *=[[@1,7:16='weekofyear',<419>,1:7]]}}, filters=[{name=active, table_ref=weekofyear}], interface={*=[{name=*, table_ref=weekofyear}]}, table_alias={weekofyear=events}}}");
	}

	@Test
	public void contextualKeywordEPOCHMICROSECONDBareColumnV0Test() {
		final String query = "select epoch_microsecond as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=epoch_microsecond, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={epoch_microsecond=[[@1,7:23='epoch_microsecond',<420>,1:7]], active=[[@9,63:68='active',<459>,1:63]], id=[[@5,42:43='id',<459>,1:42]]}}",
				"{query0={result_value=[[@3,28:39='result_value',<459>,1:28]], id=[[@5,42:43='id',<459>,1:42]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,28:39='result_value',<459>,1:28]], id=[[@5,42:43='id',<459>,1:42]]}, table_dictionary={events={epoch_microsecond=[[@1,7:23='epoch_microsecond',<420>,1:7]], active=[[@9,63:68='active',<459>,1:63]], id=[[@5,42:43='id',<459>,1:42]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=epoch_microsecond, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordEPOCHMICROSECONDQualifiedColumnV0Test() {
		final String query = "select a.epoch_microsecond as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=epoch_microsecond, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,44:44='b',<459>,1:44]], id=[[@21,100:100='b',<459>,1:100]], enabled=[[@31,128:128='b',<459>,1:128]]}, events={epoch_microsecond=[[@1,7:7='a',<459>,1:7]], account_id=[[@17,85:85='a',<459>,1:85]], active=[[@25,111:111='a',<459>,1:111]]}}",
				"{query0={result_value=[[@5,30:41='result_value',<459>,1:30]], label=[[@9,46:50='label',<459>,1:46]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,30:41='result_value',<459>,1:30]], label=[[@9,46:50='label',<459>,1:46]]}, table_dictionary={accounts={label=[[@7,44:44='b',<459>,1:44]], id=[[@21,100:100='b',<459>,1:100]], enabled=[[@31,128:128='b',<459>,1:128]]}, events={epoch_microsecond=[[@1,7:7='a',<459>,1:7]], account_id=[[@17,85:85='a',<459>,1:85]], active=[[@25,111:111='a',<459>,1:111]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=epoch_microsecond, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordEPOCHMICROSECONDExplicitColumnAliasV0Test() {
		final String query = "select a.id as epoch_microsecond, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=epoch_microsecond}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[epoch_microsecond, label]",
				"{}",
				"{accounts={label=[[@7,34:34='b',<459>,1:34]], id=[[@21,90:90='b',<459>,1:90]], enabled=[[@31,118:118='b',<459>,1:118]]}, events={account_id=[[@17,75:75='a',<459>,1:75]], active=[[@25,101:101='a',<459>,1:101]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={epoch_microsecond=[[@5,15:31='epoch_microsecond',<420>,1:15]], label=[[@9,36:40='label',<459>,1:36]]}}",
				"{def_query0={query_dictionary={epoch_microsecond=[[@5,15:31='epoch_microsecond',<420>,1:15]], label=[[@9,36:40='label',<459>,1:36]]}, table_dictionary={accounts={label=[[@7,34:34='b',<459>,1:34]], id=[[@21,90:90='b',<459>,1:90]], enabled=[[@31,118:118='b',<459>,1:118]]}, events={account_id=[[@17,75:75='a',<459>,1:75]], active=[[@25,101:101='a',<459>,1:101]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={epoch_microsecond=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordEPOCHMICROSECONDImplicitColumnAliasV0Test() {
		final String query = "select a.id epoch_microsecond, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=epoch_microsecond}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[epoch_microsecond, label]",
				"{}",
				"{accounts={label=[[@6,31:31='b',<459>,1:31]], id=[[@20,87:87='b',<459>,1:87]], enabled=[[@30,115:115='b',<459>,1:115]]}, events={account_id=[[@16,72:72='a',<459>,1:72]], active=[[@24,98:98='a',<459>,1:98]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={epoch_microsecond=[[@4,12:28='epoch_microsecond',<420>,1:12]], label=[[@8,33:37='label',<459>,1:33]]}}",
				"{def_query0={query_dictionary={epoch_microsecond=[[@4,12:28='epoch_microsecond',<420>,1:12]], label=[[@8,33:37='label',<459>,1:33]]}, table_dictionary={accounts={label=[[@6,31:31='b',<459>,1:31]], id=[[@20,87:87='b',<459>,1:87]], enabled=[[@30,115:115='b',<459>,1:115]]}, events={account_id=[[@16,72:72='a',<459>,1:72]], active=[[@24,98:98='a',<459>,1:98]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={epoch_microsecond=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordEPOCHMICROSECONDPhysicalTableV0Test() {
		final String query = "select a.id, b.label from epoch_microsecond a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=epoch_microsecond}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{epoch_microsecond={account_id=[[@15,65:65='a',<459>,1:65]], active=[[@23,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,80:80='b',<459>,1:80]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={epoch_microsecond={account_id=[[@15,65:65='a',<459>,1:65]], active=[[@23,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,80:80='b',<459>,1:80]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=epoch_microsecond, b=accounts}}}");
	}

	@Test
	public void contextualKeywordEPOCHMICROSECONDQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.epoch_microsecond a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=epoch_microsecond}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{analytics.epoch_microsecond={account_id=[[@17,75:75='a',<459>,1:75]], active=[[@25,101:101='a',<459>,1:101]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,90:90='b',<459>,1:90]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={analytics.epoch_microsecond={account_id=[[@17,75:75='a',<459>,1:75]], active=[[@25,101:101='a',<459>,1:101]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,90:90='b',<459>,1:90]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.epoch_microsecond, b=accounts}}}");
	}

	@Test
	public void contextualKeywordEPOCHMICROSECONDExplicitTableAliasV0Test() {
		final String query = "select epoch_microsecond.id, b.label from events as epoch_microsecond join accounts b on epoch_microsecond.account_id = b.id where epoch_microsecond.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=epoch_microsecond}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=epoch_microsecond, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=epoch_microsecond}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=epoch_microsecond}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,29:29='b',<459>,1:29]], id=[[@20,120:120='b',<459>,1:120]]}, events={account_id=[[@16,89:105='epoch_microsecond',<420>,1:89]], active=[[@24,131:147='epoch_microsecond',<420>,1:131]], id=[[@1,7:23='epoch_microsecond',<420>,1:7]]}}",
				"{query0={id=[[@3,25:26='id',<459>,1:25]], label=[[@7,31:35='label',<459>,1:31]]}}",
				"{def_query0={query_dictionary={id=[[@3,25:26='id',<459>,1:25]], label=[[@7,31:35='label',<459>,1:31]]}, table_dictionary={accounts={label=[[@5,29:29='b',<459>,1:29]], id=[[@20,120:120='b',<459>,1:120]]}, events={account_id=[[@16,89:105='epoch_microsecond',<420>,1:89]], active=[[@24,131:147='epoch_microsecond',<420>,1:131]], id=[[@1,7:23='epoch_microsecond',<420>,1:7]]}}, filters=[{name=account_id, table_ref=epoch_microsecond}, {name=id, table_ref=b}, {name=active, table_ref=epoch_microsecond}], interface={id=[{name=id, table_ref=epoch_microsecond}], label=[{name=label, table_ref=b}]}, table_alias={epoch_microsecond=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordEPOCHMICROSECONDImplicitTableAliasV0Test() {
		final String query = "select epoch_microsecond.id, b.label from events epoch_microsecond join accounts b on epoch_microsecond.account_id = b.id where epoch_microsecond.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=epoch_microsecond}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=epoch_microsecond, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=epoch_microsecond}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=epoch_microsecond}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,29:29='b',<459>,1:29]], id=[[@19,117:117='b',<459>,1:117]]}, events={account_id=[[@15,86:102='epoch_microsecond',<420>,1:86]], active=[[@23,128:144='epoch_microsecond',<420>,1:128]], id=[[@1,7:23='epoch_microsecond',<420>,1:7]]}}",
				"{query0={id=[[@3,25:26='id',<459>,1:25]], label=[[@7,31:35='label',<459>,1:31]]}}",
				"{def_query0={query_dictionary={id=[[@3,25:26='id',<459>,1:25]], label=[[@7,31:35='label',<459>,1:31]]}, table_dictionary={accounts={label=[[@5,29:29='b',<459>,1:29]], id=[[@19,117:117='b',<459>,1:117]]}, events={account_id=[[@15,86:102='epoch_microsecond',<420>,1:86]], active=[[@23,128:144='epoch_microsecond',<420>,1:128]], id=[[@1,7:23='epoch_microsecond',<420>,1:7]]}}, filters=[{name=account_id, table_ref=epoch_microsecond}, {name=id, table_ref=b}, {name=active, table_ref=epoch_microsecond}], interface={id=[{name=id, table_ref=epoch_microsecond}], label=[{name=label, table_ref=b}]}, table_alias={epoch_microsecond=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordEPOCHMICROSECONDCteLabelAndOutputAliasV0Test() {
		final String query = "with epoch_microsecond as (select a.amount as epoch_microsecond, a.id as row_id from events a where a.active = 1) select epoch_microsecond.epoch_microsecond as result_value, epoch_microsecond.row_id from epoch_microsecond where epoch_microsecond.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=epoch_microsecond}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=epoch_microsecond}}, query={select={1={column={name=epoch_microsecond, table_ref=epoch_microsecond}, alias=result_value}, 2={column={name=row_id, table_ref=epoch_microsecond}}}, from={table={alias=null, table=epoch_microsecond}}, where={condition={left={column={name=row_id, table_ref=epoch_microsecond}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,34:34='a',<459>,1:34]], active=[[@20,100:100='a',<459>,1:100]], id=[[@11,65:65='a',<459>,1:65]]}}",
				"{query0={row_id=[[@15,73:78='row_id',<459>,1:73], [@33,174:190='epoch_microsecond',<420>,1:174], [@39,228:244='epoch_microsecond',<420>,1:228]], epoch_microsecond=[[@9,46:62='epoch_microsecond',<420>,1:46], [@27,121:137='epoch_microsecond',<420>,1:121]]}, query1={row_id=[[@35,192:197='row_id',<459>,1:192]], result_value=[[@31,160:171='result_value',<459>,1:160]]}}",
				"{def_query1={context_list={epoch_microsecond=query0}, query_dictionary={result_value=[[@31,160:171='result_value',<459>,1:160]], row_id=[[@35,192:197='row_id',<459>,1:192]]}, def_query0={query_dictionary={epoch_microsecond=[[@9,46:62='epoch_microsecond',<420>,1:46], [@27,121:137='epoch_microsecond',<420>,1:121]], row_id=[[@15,73:78='row_id',<459>,1:73], [@33,174:190='epoch_microsecond',<420>,1:174], [@39,228:244='epoch_microsecond',<420>,1:228]]}, table_dictionary={events={amount=[[@5,34:34='a',<459>,1:34]], active=[[@20,100:100='a',<459>,1:100]], id=[[@11,65:65='a',<459>,1:65]]}}, filters=[{name=active, table_ref=a}], interface={epoch_microsecond=[{name=amount, table_ref=a}], row_id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=epoch_microsecond}], interface={result_value=[{name=epoch_microsecond, table_ref=epoch_microsecond}], row_id=[{name=row_id, table_ref=epoch_microsecond}]}, table_alias={epoch_microsecond=query0}}}");
	}

	@Test
	public void contextualKeywordEPOCHMICROSECONDWildcardQualifierV0Test() {
		final String query = "select epoch_microsecond.* from events as epoch_microsecond where epoch_microsecond.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=epoch_microsecond}}}, from={table={alias=epoch_microsecond, table=events}}, where={condition={left={column={name=active, table_ref=epoch_microsecond}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,66:82='epoch_microsecond',<420>,1:66]], *=[[@1,7:23='epoch_microsecond',<420>,1:7]]}}",
				"{query0={*=[[@3,25:25='*',<291>,1:25]]}}",
				"{def_query0={query_dictionary={*=[[@3,25:25='*',<291>,1:25]]}, table_dictionary={events={active=[[@9,66:82='epoch_microsecond',<420>,1:66]], *=[[@1,7:23='epoch_microsecond',<420>,1:7]]}}, filters=[{name=active, table_ref=epoch_microsecond}], interface={*=[{name=*, table_ref=epoch_microsecond}]}, table_alias={epoch_microsecond=events}}}");
	}

	@Test
	public void contextualKeywordEPOCHMILLISECONDBareColumnV0Test() {
		final String query = "select epoch_millisecond as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=epoch_millisecond, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={epoch_millisecond=[[@1,7:23='epoch_millisecond',<421>,1:7]], active=[[@9,63:68='active',<459>,1:63]], id=[[@5,42:43='id',<459>,1:42]]}}",
				"{query0={result_value=[[@3,28:39='result_value',<459>,1:28]], id=[[@5,42:43='id',<459>,1:42]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,28:39='result_value',<459>,1:28]], id=[[@5,42:43='id',<459>,1:42]]}, table_dictionary={events={epoch_millisecond=[[@1,7:23='epoch_millisecond',<421>,1:7]], active=[[@9,63:68='active',<459>,1:63]], id=[[@5,42:43='id',<459>,1:42]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=epoch_millisecond, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordEPOCHMILLISECONDQualifiedColumnV0Test() {
		final String query = "select a.epoch_millisecond as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=epoch_millisecond, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,44:44='b',<459>,1:44]], id=[[@21,100:100='b',<459>,1:100]], enabled=[[@31,128:128='b',<459>,1:128]]}, events={epoch_millisecond=[[@1,7:7='a',<459>,1:7]], account_id=[[@17,85:85='a',<459>,1:85]], active=[[@25,111:111='a',<459>,1:111]]}}",
				"{query0={result_value=[[@5,30:41='result_value',<459>,1:30]], label=[[@9,46:50='label',<459>,1:46]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,30:41='result_value',<459>,1:30]], label=[[@9,46:50='label',<459>,1:46]]}, table_dictionary={accounts={label=[[@7,44:44='b',<459>,1:44]], id=[[@21,100:100='b',<459>,1:100]], enabled=[[@31,128:128='b',<459>,1:128]]}, events={epoch_millisecond=[[@1,7:7='a',<459>,1:7]], account_id=[[@17,85:85='a',<459>,1:85]], active=[[@25,111:111='a',<459>,1:111]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=epoch_millisecond, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordEPOCHMILLISECONDExplicitColumnAliasV0Test() {
		final String query = "select a.id as epoch_millisecond, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=epoch_millisecond}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[epoch_millisecond, label]",
				"{}",
				"{accounts={label=[[@7,34:34='b',<459>,1:34]], id=[[@21,90:90='b',<459>,1:90]], enabled=[[@31,118:118='b',<459>,1:118]]}, events={account_id=[[@17,75:75='a',<459>,1:75]], active=[[@25,101:101='a',<459>,1:101]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={epoch_millisecond=[[@5,15:31='epoch_millisecond',<421>,1:15]], label=[[@9,36:40='label',<459>,1:36]]}}",
				"{def_query0={query_dictionary={epoch_millisecond=[[@5,15:31='epoch_millisecond',<421>,1:15]], label=[[@9,36:40='label',<459>,1:36]]}, table_dictionary={accounts={label=[[@7,34:34='b',<459>,1:34]], id=[[@21,90:90='b',<459>,1:90]], enabled=[[@31,118:118='b',<459>,1:118]]}, events={account_id=[[@17,75:75='a',<459>,1:75]], active=[[@25,101:101='a',<459>,1:101]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={epoch_millisecond=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordEPOCHMILLISECONDImplicitColumnAliasV0Test() {
		final String query = "select a.id epoch_millisecond, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=epoch_millisecond}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[epoch_millisecond, label]",
				"{}",
				"{accounts={label=[[@6,31:31='b',<459>,1:31]], id=[[@20,87:87='b',<459>,1:87]], enabled=[[@30,115:115='b',<459>,1:115]]}, events={account_id=[[@16,72:72='a',<459>,1:72]], active=[[@24,98:98='a',<459>,1:98]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={epoch_millisecond=[[@4,12:28='epoch_millisecond',<421>,1:12]], label=[[@8,33:37='label',<459>,1:33]]}}",
				"{def_query0={query_dictionary={epoch_millisecond=[[@4,12:28='epoch_millisecond',<421>,1:12]], label=[[@8,33:37='label',<459>,1:33]]}, table_dictionary={accounts={label=[[@6,31:31='b',<459>,1:31]], id=[[@20,87:87='b',<459>,1:87]], enabled=[[@30,115:115='b',<459>,1:115]]}, events={account_id=[[@16,72:72='a',<459>,1:72]], active=[[@24,98:98='a',<459>,1:98]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={epoch_millisecond=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordEPOCHMILLISECONDPhysicalTableV0Test() {
		final String query = "select a.id, b.label from epoch_millisecond a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=epoch_millisecond}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{epoch_millisecond={account_id=[[@15,65:65='a',<459>,1:65]], active=[[@23,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,80:80='b',<459>,1:80]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={epoch_millisecond={account_id=[[@15,65:65='a',<459>,1:65]], active=[[@23,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,80:80='b',<459>,1:80]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=epoch_millisecond, b=accounts}}}");
	}

	@Test
	public void contextualKeywordEPOCHMILLISECONDQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.epoch_millisecond a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=epoch_millisecond}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{analytics.epoch_millisecond={account_id=[[@17,75:75='a',<459>,1:75]], active=[[@25,101:101='a',<459>,1:101]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,90:90='b',<459>,1:90]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={analytics.epoch_millisecond={account_id=[[@17,75:75='a',<459>,1:75]], active=[[@25,101:101='a',<459>,1:101]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,90:90='b',<459>,1:90]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.epoch_millisecond, b=accounts}}}");
	}

	@Test
	public void contextualKeywordEPOCHMILLISECONDExplicitTableAliasV0Test() {
		final String query = "select epoch_millisecond.id, b.label from events as epoch_millisecond join accounts b on epoch_millisecond.account_id = b.id where epoch_millisecond.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=epoch_millisecond}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=epoch_millisecond, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=epoch_millisecond}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=epoch_millisecond}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,29:29='b',<459>,1:29]], id=[[@20,120:120='b',<459>,1:120]]}, events={account_id=[[@16,89:105='epoch_millisecond',<421>,1:89]], active=[[@24,131:147='epoch_millisecond',<421>,1:131]], id=[[@1,7:23='epoch_millisecond',<421>,1:7]]}}",
				"{query0={id=[[@3,25:26='id',<459>,1:25]], label=[[@7,31:35='label',<459>,1:31]]}}",
				"{def_query0={query_dictionary={id=[[@3,25:26='id',<459>,1:25]], label=[[@7,31:35='label',<459>,1:31]]}, table_dictionary={accounts={label=[[@5,29:29='b',<459>,1:29]], id=[[@20,120:120='b',<459>,1:120]]}, events={account_id=[[@16,89:105='epoch_millisecond',<421>,1:89]], active=[[@24,131:147='epoch_millisecond',<421>,1:131]], id=[[@1,7:23='epoch_millisecond',<421>,1:7]]}}, filters=[{name=account_id, table_ref=epoch_millisecond}, {name=id, table_ref=b}, {name=active, table_ref=epoch_millisecond}], interface={id=[{name=id, table_ref=epoch_millisecond}], label=[{name=label, table_ref=b}]}, table_alias={epoch_millisecond=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordEPOCHMILLISECONDImplicitTableAliasV0Test() {
		final String query = "select epoch_millisecond.id, b.label from events epoch_millisecond join accounts b on epoch_millisecond.account_id = b.id where epoch_millisecond.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=epoch_millisecond}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=epoch_millisecond, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=epoch_millisecond}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=epoch_millisecond}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,29:29='b',<459>,1:29]], id=[[@19,117:117='b',<459>,1:117]]}, events={account_id=[[@15,86:102='epoch_millisecond',<421>,1:86]], active=[[@23,128:144='epoch_millisecond',<421>,1:128]], id=[[@1,7:23='epoch_millisecond',<421>,1:7]]}}",
				"{query0={id=[[@3,25:26='id',<459>,1:25]], label=[[@7,31:35='label',<459>,1:31]]}}",
				"{def_query0={query_dictionary={id=[[@3,25:26='id',<459>,1:25]], label=[[@7,31:35='label',<459>,1:31]]}, table_dictionary={accounts={label=[[@5,29:29='b',<459>,1:29]], id=[[@19,117:117='b',<459>,1:117]]}, events={account_id=[[@15,86:102='epoch_millisecond',<421>,1:86]], active=[[@23,128:144='epoch_millisecond',<421>,1:128]], id=[[@1,7:23='epoch_millisecond',<421>,1:7]]}}, filters=[{name=account_id, table_ref=epoch_millisecond}, {name=id, table_ref=b}, {name=active, table_ref=epoch_millisecond}], interface={id=[{name=id, table_ref=epoch_millisecond}], label=[{name=label, table_ref=b}]}, table_alias={epoch_millisecond=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordEPOCHMILLISECONDCteLabelAndOutputAliasV0Test() {
		final String query = "with epoch_millisecond as (select a.amount as epoch_millisecond, a.id as row_id from events a where a.active = 1) select epoch_millisecond.epoch_millisecond as result_value, epoch_millisecond.row_id from epoch_millisecond where epoch_millisecond.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=epoch_millisecond}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=epoch_millisecond}}, query={select={1={column={name=epoch_millisecond, table_ref=epoch_millisecond}, alias=result_value}, 2={column={name=row_id, table_ref=epoch_millisecond}}}, from={table={alias=null, table=epoch_millisecond}}, where={condition={left={column={name=row_id, table_ref=epoch_millisecond}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,34:34='a',<459>,1:34]], active=[[@20,100:100='a',<459>,1:100]], id=[[@11,65:65='a',<459>,1:65]]}}",
				"{query0={epoch_millisecond=[[@9,46:62='epoch_millisecond',<421>,1:46], [@27,121:137='epoch_millisecond',<421>,1:121]], row_id=[[@15,73:78='row_id',<459>,1:73], [@33,174:190='epoch_millisecond',<421>,1:174], [@39,228:244='epoch_millisecond',<421>,1:228]]}, query1={row_id=[[@35,192:197='row_id',<459>,1:192]], result_value=[[@31,160:171='result_value',<459>,1:160]]}}",
				"{def_query1={context_list={epoch_millisecond=query0}, query_dictionary={result_value=[[@31,160:171='result_value',<459>,1:160]], row_id=[[@35,192:197='row_id',<459>,1:192]]}, def_query0={query_dictionary={epoch_millisecond=[[@9,46:62='epoch_millisecond',<421>,1:46], [@27,121:137='epoch_millisecond',<421>,1:121]], row_id=[[@15,73:78='row_id',<459>,1:73], [@33,174:190='epoch_millisecond',<421>,1:174], [@39,228:244='epoch_millisecond',<421>,1:228]]}, table_dictionary={events={amount=[[@5,34:34='a',<459>,1:34]], active=[[@20,100:100='a',<459>,1:100]], id=[[@11,65:65='a',<459>,1:65]]}}, filters=[{name=active, table_ref=a}], interface={epoch_millisecond=[{name=amount, table_ref=a}], row_id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=epoch_millisecond}], interface={result_value=[{name=epoch_millisecond, table_ref=epoch_millisecond}], row_id=[{name=row_id, table_ref=epoch_millisecond}]}, table_alias={epoch_millisecond=query0}}}");
	}

	@Test
	public void contextualKeywordEPOCHMILLISECONDWildcardQualifierV0Test() {
		final String query = "select epoch_millisecond.* from events as epoch_millisecond where epoch_millisecond.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=epoch_millisecond}}}, from={table={alias=epoch_millisecond, table=events}}, where={condition={left={column={name=active, table_ref=epoch_millisecond}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,66:82='epoch_millisecond',<421>,1:66]], *=[[@1,7:23='epoch_millisecond',<421>,1:7]]}}",
				"{query0={*=[[@3,25:25='*',<291>,1:25]]}}",
				"{def_query0={query_dictionary={*=[[@3,25:25='*',<291>,1:25]]}, table_dictionary={events={active=[[@9,66:82='epoch_millisecond',<421>,1:66]], *=[[@1,7:23='epoch_millisecond',<421>,1:7]]}}, filters=[{name=active, table_ref=epoch_millisecond}], interface={*=[{name=*, table_ref=epoch_millisecond}]}, table_alias={epoch_millisecond=events}}}");
	}

	@Test
	public void contextualKeywordEPOCHSECONDBareColumnV0Test() {
		final String query = "select epoch_second as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=epoch_second, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={epoch_second=[[@1,7:18='epoch_second',<422>,1:7]], active=[[@9,58:63='active',<459>,1:58]], id=[[@5,37:38='id',<459>,1:37]]}}",
				"{query0={result_value=[[@3,23:34='result_value',<459>,1:23]], id=[[@5,37:38='id',<459>,1:37]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,23:34='result_value',<459>,1:23]], id=[[@5,37:38='id',<459>,1:37]]}, table_dictionary={events={epoch_second=[[@1,7:18='epoch_second',<422>,1:7]], active=[[@9,58:63='active',<459>,1:58]], id=[[@5,37:38='id',<459>,1:37]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=epoch_second, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordEPOCHSECONDQualifiedColumnV0Test() {
		final String query = "select a.epoch_second as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=epoch_second, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,39:39='b',<459>,1:39]], id=[[@21,95:95='b',<459>,1:95]], enabled=[[@31,123:123='b',<459>,1:123]]}, events={epoch_second=[[@1,7:7='a',<459>,1:7]], account_id=[[@17,80:80='a',<459>,1:80]], active=[[@25,106:106='a',<459>,1:106]]}}",
				"{query0={result_value=[[@5,25:36='result_value',<459>,1:25]], label=[[@9,41:45='label',<459>,1:41]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,25:36='result_value',<459>,1:25]], label=[[@9,41:45='label',<459>,1:41]]}, table_dictionary={accounts={label=[[@7,39:39='b',<459>,1:39]], id=[[@21,95:95='b',<459>,1:95]], enabled=[[@31,123:123='b',<459>,1:123]]}, events={epoch_second=[[@1,7:7='a',<459>,1:7]], account_id=[[@17,80:80='a',<459>,1:80]], active=[[@25,106:106='a',<459>,1:106]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=epoch_second, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordEPOCHSECONDExplicitColumnAliasV0Test() {
		final String query = "select a.id as epoch_second, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=epoch_second}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[epoch_second, label]",
				"{}",
				"{accounts={label=[[@7,29:29='b',<459>,1:29]], id=[[@21,85:85='b',<459>,1:85]], enabled=[[@31,113:113='b',<459>,1:113]]}, events={account_id=[[@17,70:70='a',<459>,1:70]], active=[[@25,96:96='a',<459>,1:96]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={epoch_second=[[@5,15:26='epoch_second',<422>,1:15]], label=[[@9,31:35='label',<459>,1:31]]}}",
				"{def_query0={query_dictionary={epoch_second=[[@5,15:26='epoch_second',<422>,1:15]], label=[[@9,31:35='label',<459>,1:31]]}, table_dictionary={accounts={label=[[@7,29:29='b',<459>,1:29]], id=[[@21,85:85='b',<459>,1:85]], enabled=[[@31,113:113='b',<459>,1:113]]}, events={account_id=[[@17,70:70='a',<459>,1:70]], active=[[@25,96:96='a',<459>,1:96]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={epoch_second=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordEPOCHSECONDImplicitColumnAliasV0Test() {
		final String query = "select a.id epoch_second, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=epoch_second}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[epoch_second, label]",
				"{}",
				"{accounts={label=[[@6,26:26='b',<459>,1:26]], id=[[@20,82:82='b',<459>,1:82]], enabled=[[@30,110:110='b',<459>,1:110]]}, events={account_id=[[@16,67:67='a',<459>,1:67]], active=[[@24,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={epoch_second=[[@4,12:23='epoch_second',<422>,1:12]], label=[[@8,28:32='label',<459>,1:28]]}}",
				"{def_query0={query_dictionary={epoch_second=[[@4,12:23='epoch_second',<422>,1:12]], label=[[@8,28:32='label',<459>,1:28]]}, table_dictionary={accounts={label=[[@6,26:26='b',<459>,1:26]], id=[[@20,82:82='b',<459>,1:82]], enabled=[[@30,110:110='b',<459>,1:110]]}, events={account_id=[[@16,67:67='a',<459>,1:67]], active=[[@24,93:93='a',<459>,1:93]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={epoch_second=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordEPOCHSECONDPhysicalTableV0Test() {
		final String query = "select a.id, b.label from epoch_second a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=epoch_second}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{epoch_second={account_id=[[@15,60:60='a',<459>,1:60]], active=[[@23,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,75:75='b',<459>,1:75]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={epoch_second={account_id=[[@15,60:60='a',<459>,1:60]], active=[[@23,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,75:75='b',<459>,1:75]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=epoch_second, b=accounts}}}");
	}

	@Test
	public void contextualKeywordEPOCHSECONDQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.epoch_second a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=epoch_second}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{analytics.epoch_second={account_id=[[@17,70:70='a',<459>,1:70]], active=[[@25,96:96='a',<459>,1:96]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,85:85='b',<459>,1:85]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={analytics.epoch_second={account_id=[[@17,70:70='a',<459>,1:70]], active=[[@25,96:96='a',<459>,1:96]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,85:85='b',<459>,1:85]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.epoch_second, b=accounts}}}");
	}

	@Test
	public void contextualKeywordEPOCHSECONDExplicitTableAliasV0Test() {
		final String query = "select epoch_second.id, b.label from events as epoch_second join accounts b on epoch_second.account_id = b.id where epoch_second.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=epoch_second}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=epoch_second, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=epoch_second}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=epoch_second}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,24:24='b',<459>,1:24]], id=[[@20,105:105='b',<459>,1:105]]}, events={account_id=[[@16,79:90='epoch_second',<422>,1:79]], active=[[@24,116:127='epoch_second',<422>,1:116]], id=[[@1,7:18='epoch_second',<422>,1:7]]}}",
				"{query0={id=[[@3,20:21='id',<459>,1:20]], label=[[@7,26:30='label',<459>,1:26]]}}",
				"{def_query0={query_dictionary={id=[[@3,20:21='id',<459>,1:20]], label=[[@7,26:30='label',<459>,1:26]]}, table_dictionary={accounts={label=[[@5,24:24='b',<459>,1:24]], id=[[@20,105:105='b',<459>,1:105]]}, events={account_id=[[@16,79:90='epoch_second',<422>,1:79]], active=[[@24,116:127='epoch_second',<422>,1:116]], id=[[@1,7:18='epoch_second',<422>,1:7]]}}, filters=[{name=account_id, table_ref=epoch_second}, {name=id, table_ref=b}, {name=active, table_ref=epoch_second}], interface={id=[{name=id, table_ref=epoch_second}], label=[{name=label, table_ref=b}]}, table_alias={epoch_second=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordEPOCHSECONDImplicitTableAliasV0Test() {
		final String query = "select epoch_second.id, b.label from events epoch_second join accounts b on epoch_second.account_id = b.id where epoch_second.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=epoch_second}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=epoch_second, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=epoch_second}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=epoch_second}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,24:24='b',<459>,1:24]], id=[[@19,102:102='b',<459>,1:102]]}, events={account_id=[[@15,76:87='epoch_second',<422>,1:76]], active=[[@23,113:124='epoch_second',<422>,1:113]], id=[[@1,7:18='epoch_second',<422>,1:7]]}}",
				"{query0={id=[[@3,20:21='id',<459>,1:20]], label=[[@7,26:30='label',<459>,1:26]]}}",
				"{def_query0={query_dictionary={id=[[@3,20:21='id',<459>,1:20]], label=[[@7,26:30='label',<459>,1:26]]}, table_dictionary={accounts={label=[[@5,24:24='b',<459>,1:24]], id=[[@19,102:102='b',<459>,1:102]]}, events={account_id=[[@15,76:87='epoch_second',<422>,1:76]], active=[[@23,113:124='epoch_second',<422>,1:113]], id=[[@1,7:18='epoch_second',<422>,1:7]]}}, filters=[{name=account_id, table_ref=epoch_second}, {name=id, table_ref=b}, {name=active, table_ref=epoch_second}], interface={id=[{name=id, table_ref=epoch_second}], label=[{name=label, table_ref=b}]}, table_alias={epoch_second=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordEPOCHSECONDCteLabelAndOutputAliasV0Test() {
		final String query = "with epoch_second as (select a.amount as epoch_second, a.id as row_id from events a where a.active = 1) select epoch_second.epoch_second as result_value, epoch_second.row_id from epoch_second where epoch_second.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=epoch_second}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=epoch_second}}, query={select={1={column={name=epoch_second, table_ref=epoch_second}, alias=result_value}, 2={column={name=row_id, table_ref=epoch_second}}}, from={table={alias=null, table=epoch_second}}, where={condition={left={column={name=row_id, table_ref=epoch_second}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,29:29='a',<459>,1:29]], active=[[@20,90:90='a',<459>,1:90]], id=[[@11,55:55='a',<459>,1:55]]}}",
				"{query0={row_id=[[@15,63:68='row_id',<459>,1:63], [@33,154:165='epoch_second',<422>,1:154], [@39,198:209='epoch_second',<422>,1:198]], epoch_second=[[@9,41:52='epoch_second',<422>,1:41], [@27,111:122='epoch_second',<422>,1:111]]}, query1={row_id=[[@35,167:172='row_id',<459>,1:167]], result_value=[[@31,140:151='result_value',<459>,1:140]]}}",
				"{def_query1={context_list={epoch_second=query0}, query_dictionary={result_value=[[@31,140:151='result_value',<459>,1:140]], row_id=[[@35,167:172='row_id',<459>,1:167]]}, def_query0={query_dictionary={epoch_second=[[@9,41:52='epoch_second',<422>,1:41], [@27,111:122='epoch_second',<422>,1:111]], row_id=[[@15,63:68='row_id',<459>,1:63], [@33,154:165='epoch_second',<422>,1:154], [@39,198:209='epoch_second',<422>,1:198]]}, table_dictionary={events={amount=[[@5,29:29='a',<459>,1:29]], active=[[@20,90:90='a',<459>,1:90]], id=[[@11,55:55='a',<459>,1:55]]}}, filters=[{name=active, table_ref=a}], interface={epoch_second=[{name=amount, table_ref=a}], row_id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=epoch_second}], interface={result_value=[{name=epoch_second, table_ref=epoch_second}], row_id=[{name=row_id, table_ref=epoch_second}]}, table_alias={epoch_second=query0}}}");
	}

	@Test
	public void contextualKeywordEPOCHSECONDWildcardQualifierV0Test() {
		final String query = "select epoch_second.* from events as epoch_second where epoch_second.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=epoch_second}}}, from={table={alias=epoch_second, table=events}}, where={condition={left={column={name=active, table_ref=epoch_second}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,56:67='epoch_second',<422>,1:56]], *=[[@1,7:18='epoch_second',<422>,1:7]]}}",
				"{query0={*=[[@3,20:20='*',<291>,1:20]]}}",
				"{def_query0={query_dictionary={*=[[@3,20:20='*',<291>,1:20]]}, table_dictionary={events={active=[[@9,56:67='epoch_second',<422>,1:56]], *=[[@1,7:18='epoch_second',<422>,1:7]]}}, filters=[{name=active, table_ref=epoch_second}], interface={*=[{name=*, table_ref=epoch_second}]}, table_alias={epoch_second=events}}}");
	}

	@Test
	public void contextualKeywordSETSBareColumnV0Test() {
		final String query = "select sets as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=sets, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={sets=[[@1,7:10='sets',<424>,1:7]], active=[[@9,50:55='active',<459>,1:50]], id=[[@5,29:30='id',<459>,1:29]]}}",
				"{query0={result_value=[[@3,15:26='result_value',<459>,1:15]], id=[[@5,29:30='id',<459>,1:29]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,15:26='result_value',<459>,1:15]], id=[[@5,29:30='id',<459>,1:29]]}, table_dictionary={events={sets=[[@1,7:10='sets',<424>,1:7]], active=[[@9,50:55='active',<459>,1:50]], id=[[@5,29:30='id',<459>,1:29]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=sets, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordSETSQualifiedColumnV0Test() {
		final String query = "select a.sets as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=sets, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,31:31='b',<459>,1:31]], id=[[@21,87:87='b',<459>,1:87]], enabled=[[@31,115:115='b',<459>,1:115]]}, events={account_id=[[@17,72:72='a',<459>,1:72]], sets=[[@1,7:7='a',<459>,1:7]], active=[[@25,98:98='a',<459>,1:98]]}}",
				"{query0={result_value=[[@5,17:28='result_value',<459>,1:17]], label=[[@9,33:37='label',<459>,1:33]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,17:28='result_value',<459>,1:17]], label=[[@9,33:37='label',<459>,1:33]]}, table_dictionary={accounts={label=[[@7,31:31='b',<459>,1:31]], id=[[@21,87:87='b',<459>,1:87]], enabled=[[@31,115:115='b',<459>,1:115]]}, events={account_id=[[@17,72:72='a',<459>,1:72]], sets=[[@1,7:7='a',<459>,1:7]], active=[[@25,98:98='a',<459>,1:98]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=sets, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordSETSExplicitColumnAliasV0Test() {
		final String query = "select a.id as sets, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=sets}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[sets, label]",
				"{}",
				"{accounts={label=[[@7,21:21='b',<459>,1:21]], id=[[@21,77:77='b',<459>,1:77]], enabled=[[@31,105:105='b',<459>,1:105]]}, events={account_id=[[@17,62:62='a',<459>,1:62]], active=[[@25,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={sets=[[@5,15:18='sets',<424>,1:15]], label=[[@9,23:27='label',<459>,1:23]]}}",
				"{def_query0={query_dictionary={sets=[[@5,15:18='sets',<424>,1:15]], label=[[@9,23:27='label',<459>,1:23]]}, table_dictionary={accounts={label=[[@7,21:21='b',<459>,1:21]], id=[[@21,77:77='b',<459>,1:77]], enabled=[[@31,105:105='b',<459>,1:105]]}, events={account_id=[[@17,62:62='a',<459>,1:62]], active=[[@25,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={sets=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordSETSImplicitColumnAliasV0Test() {
		final String query = "select a.id sets, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=sets}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[sets, label]",
				"{}",
				"{accounts={label=[[@6,18:18='b',<459>,1:18]], id=[[@20,74:74='b',<459>,1:74]], enabled=[[@30,102:102='b',<459>,1:102]]}, events={account_id=[[@16,59:59='a',<459>,1:59]], active=[[@24,85:85='a',<459>,1:85]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={sets=[[@4,12:15='sets',<424>,1:12]], label=[[@8,20:24='label',<459>,1:20]]}}",
				"{def_query0={query_dictionary={sets=[[@4,12:15='sets',<424>,1:12]], label=[[@8,20:24='label',<459>,1:20]]}, table_dictionary={accounts={label=[[@6,18:18='b',<459>,1:18]], id=[[@20,74:74='b',<459>,1:74]], enabled=[[@30,102:102='b',<459>,1:102]]}, events={account_id=[[@16,59:59='a',<459>,1:59]], active=[[@24,85:85='a',<459>,1:85]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={sets=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordSETSPhysicalTableV0Test() {
		final String query = "select a.id, b.label from sets a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=sets}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{sets={account_id=[[@15,52:52='a',<459>,1:52]], active=[[@23,78:78='a',<459>,1:78]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,67:67='b',<459>,1:67]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={sets={account_id=[[@15,52:52='a',<459>,1:52]], active=[[@23,78:78='a',<459>,1:78]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,67:67='b',<459>,1:67]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=sets, b=accounts}}}");
	}

	@Test
	public void contextualKeywordSETSQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.sets a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=sets}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{analytics.sets={account_id=[[@17,62:62='a',<459>,1:62]], active=[[@25,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,77:77='b',<459>,1:77]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={analytics.sets={account_id=[[@17,62:62='a',<459>,1:62]], active=[[@25,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,77:77='b',<459>,1:77]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.sets, b=accounts}}}");
	}

	@Test
	public void contextualKeywordSETSExplicitTableAliasV0Test() {
		final String query = "select sets.id, b.label from events as sets join accounts b on sets.account_id = b.id where sets.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=sets}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=sets, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=sets}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=sets}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,16:16='b',<459>,1:16]], id=[[@20,81:81='b',<459>,1:81]]}, events={account_id=[[@16,63:66='sets',<424>,1:63]], active=[[@24,92:95='sets',<424>,1:92]], id=[[@1,7:10='sets',<424>,1:7]]}}",
				"{query0={id=[[@3,12:13='id',<459>,1:12]], label=[[@7,18:22='label',<459>,1:18]]}}",
				"{def_query0={query_dictionary={id=[[@3,12:13='id',<459>,1:12]], label=[[@7,18:22='label',<459>,1:18]]}, table_dictionary={accounts={label=[[@5,16:16='b',<459>,1:16]], id=[[@20,81:81='b',<459>,1:81]]}, events={account_id=[[@16,63:66='sets',<424>,1:63]], active=[[@24,92:95='sets',<424>,1:92]], id=[[@1,7:10='sets',<424>,1:7]]}}, filters=[{name=account_id, table_ref=sets}, {name=id, table_ref=b}, {name=active, table_ref=sets}], interface={id=[{name=id, table_ref=sets}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, sets=events}}}");
	}

	@Test
	public void contextualKeywordSETSImplicitTableAliasV0Test() {
		final String query = "select sets.id, b.label from events sets join accounts b on sets.account_id = b.id where sets.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=sets}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=sets, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=sets}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=sets}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,16:16='b',<459>,1:16]], id=[[@19,78:78='b',<459>,1:78]]}, events={account_id=[[@15,60:63='sets',<424>,1:60]], active=[[@23,89:92='sets',<424>,1:89]], id=[[@1,7:10='sets',<424>,1:7]]}}",
				"{query0={id=[[@3,12:13='id',<459>,1:12]], label=[[@7,18:22='label',<459>,1:18]]}}",
				"{def_query0={query_dictionary={id=[[@3,12:13='id',<459>,1:12]], label=[[@7,18:22='label',<459>,1:18]]}, table_dictionary={accounts={label=[[@5,16:16='b',<459>,1:16]], id=[[@19,78:78='b',<459>,1:78]]}, events={account_id=[[@15,60:63='sets',<424>,1:60]], active=[[@23,89:92='sets',<424>,1:89]], id=[[@1,7:10='sets',<424>,1:7]]}}, filters=[{name=account_id, table_ref=sets}, {name=id, table_ref=b}, {name=active, table_ref=sets}], interface={id=[{name=id, table_ref=sets}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, sets=events}}}");
	}

	@Test
	public void contextualKeywordSETSCteLabelAndOutputAliasV0Test() {
		final String query = "with sets as (select a.amount as sets, a.id as row_id from events a where a.active = 1) select sets.sets as result_value, sets.row_id from sets where sets.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=sets}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=sets}}, query={select={1={column={name=sets, table_ref=sets}, alias=result_value}, 2={column={name=row_id, table_ref=sets}}}, from={table={alias=null, table=sets}}, where={condition={left={column={name=row_id, table_ref=sets}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,21:21='a',<459>,1:21]], active=[[@20,74:74='a',<459>,1:74]], id=[[@11,39:39='a',<459>,1:39]]}}",
				"{query0={sets=[[@9,33:36='sets',<424>,1:33], [@27,95:98='sets',<424>,1:95]], row_id=[[@15,47:52='row_id',<459>,1:47], [@33,122:125='sets',<424>,1:122], [@39,150:153='sets',<424>,1:150]]}, query1={row_id=[[@35,127:132='row_id',<459>,1:127]], result_value=[[@31,108:119='result_value',<459>,1:108]]}}",
				"{def_query1={context_list={sets=query0}, query_dictionary={result_value=[[@31,108:119='result_value',<459>,1:108]], row_id=[[@35,127:132='row_id',<459>,1:127]]}, def_query0={query_dictionary={sets=[[@9,33:36='sets',<424>,1:33], [@27,95:98='sets',<424>,1:95]], row_id=[[@15,47:52='row_id',<459>,1:47], [@33,122:125='sets',<424>,1:122], [@39,150:153='sets',<424>,1:150]]}, table_dictionary={events={amount=[[@5,21:21='a',<459>,1:21]], active=[[@20,74:74='a',<459>,1:74]], id=[[@11,39:39='a',<459>,1:39]]}}, filters=[{name=active, table_ref=a}], interface={sets=[{name=amount, table_ref=a}], row_id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=sets}], interface={result_value=[{name=sets, table_ref=sets}], row_id=[{name=row_id, table_ref=sets}]}, table_alias={sets=query0}}}");
	}

	@Test
	public void contextualKeywordSETSWildcardQualifierV0Test() {
		final String query = "select sets.* from events as sets where sets.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=sets}}}, from={table={alias=sets, table=events}}, where={condition={left={column={name=active, table_ref=sets}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,40:43='sets',<424>,1:40]], *=[[@1,7:10='sets',<424>,1:7]]}}",
				"{query0={*=[[@3,12:12='*',<291>,1:12]]}}",
				"{def_query0={query_dictionary={*=[[@3,12:12='*',<291>,1:12]]}, table_dictionary={events={active=[[@9,40:43='sets',<424>,1:40]], *=[[@1,7:10='sets',<424>,1:7]]}}, filters=[{name=active, table_ref=sets}], interface={*=[{name=*, table_ref=sets}]}, table_alias={sets=events}}}");
	}

	@Test
	public void contextualKeywordWITHINBareColumnV0Test() {
		final String query = "select within as result_value, id from events where active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=within, table_ref=null}, alias=result_value}, 2={column={name=id, table_ref=null}}}, from={table={alias=null, table=events}}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={within=[[@1,7:12='within',<425>,1:7]], active=[[@9,52:57='active',<459>,1:52]], id=[[@5,31:32='id',<459>,1:31]]}}",
				"{query0={result_value=[[@3,17:28='result_value',<459>,1:17]], id=[[@5,31:32='id',<459>,1:31]]}}",
				"{def_query0={query_dictionary={result_value=[[@3,17:28='result_value',<459>,1:17]], id=[[@5,31:32='id',<459>,1:31]]}, table_dictionary={events={within=[[@1,7:12='within',<425>,1:7]], active=[[@9,52:57='active',<459>,1:52]], id=[[@5,31:32='id',<459>,1:31]]}}, filters=[{name=active, table_ref=events}], interface={result_value=[{name=within, table_ref=events}], id=[{name=id, table_ref=events}]}}}");
	}

	@Test
	public void contextualKeywordWITHINQualifiedColumnV0Test() {
		final String query = "select a.within as result_value, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=within, table_ref=a}, alias=result_value}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, label]",
				"{}",
				"{accounts={label=[[@7,33:33='b',<459>,1:33]], id=[[@21,89:89='b',<459>,1:89]], enabled=[[@31,117:117='b',<459>,1:117]]}, events={account_id=[[@17,74:74='a',<459>,1:74]], within=[[@1,7:7='a',<459>,1:7]], active=[[@25,100:100='a',<459>,1:100]]}}",
				"{query0={result_value=[[@5,19:30='result_value',<459>,1:19]], label=[[@9,35:39='label',<459>,1:35]]}}",
				"{def_query0={query_dictionary={result_value=[[@5,19:30='result_value',<459>,1:19]], label=[[@9,35:39='label',<459>,1:35]]}, table_dictionary={accounts={label=[[@7,33:33='b',<459>,1:33]], id=[[@21,89:89='b',<459>,1:89]], enabled=[[@31,117:117='b',<459>,1:117]]}, events={account_id=[[@17,74:74='a',<459>,1:74]], within=[[@1,7:7='a',<459>,1:7]], active=[[@25,100:100='a',<459>,1:100]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=within, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordWITHINExplicitColumnAliasV0Test() {
		final String query = "select a.id as within, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=within}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[within, label]",
				"{}",
				"{accounts={label=[[@7,23:23='b',<459>,1:23]], id=[[@21,79:79='b',<459>,1:79]], enabled=[[@31,107:107='b',<459>,1:107]]}, events={account_id=[[@17,64:64='a',<459>,1:64]], active=[[@25,90:90='a',<459>,1:90]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={within=[[@5,15:20='within',<425>,1:15]], label=[[@9,25:29='label',<459>,1:25]]}}",
				"{def_query0={query_dictionary={within=[[@5,15:20='within',<425>,1:15]], label=[[@9,25:29='label',<459>,1:25]]}, table_dictionary={accounts={label=[[@7,23:23='b',<459>,1:23]], id=[[@21,79:79='b',<459>,1:79]], enabled=[[@31,107:107='b',<459>,1:107]]}, events={account_id=[[@17,64:64='a',<459>,1:64]], active=[[@25,90:90='a',<459>,1:90]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={within=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordWITHINImplicitColumnAliasV0Test() {
		final String query = "select a.id within, b.label from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}, alias=within}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[within, label]",
				"{}",
				"{accounts={label=[[@6,20:20='b',<459>,1:20]], id=[[@20,76:76='b',<459>,1:76]], enabled=[[@30,104:104='b',<459>,1:104]]}, events={account_id=[[@16,61:61='a',<459>,1:61]], active=[[@24,87:87='a',<459>,1:87]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={within=[[@4,12:17='within',<425>,1:12]], label=[[@8,22:26='label',<459>,1:22]]}}",
				"{def_query0={query_dictionary={within=[[@4,12:17='within',<425>,1:12]], label=[[@8,22:26='label',<459>,1:22]]}, table_dictionary={accounts={label=[[@6,20:20='b',<459>,1:20]], id=[[@20,76:76='b',<459>,1:76]], enabled=[[@30,104:104='b',<459>,1:104]]}, events={account_id=[[@16,61:61='a',<459>,1:61]], active=[[@24,87:87='a',<459>,1:87]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={within=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordWITHINPhysicalTableV0Test() {
		final String query = "select a.id, b.label from within a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=within}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{within={account_id=[[@15,54:54='a',<459>,1:54]], active=[[@23,80:80='a',<459>,1:80]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,69:69='b',<459>,1:69]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={within={account_id=[[@15,54:54='a',<459>,1:54]], active=[[@23,80:80='a',<459>,1:80]], id=[[@1,7:7='a',<459>,1:7]]}, accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@19,69:69='b',<459>,1:69]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=within, b=accounts}}}");
	}

	@Test
	public void contextualKeywordWITHINQualifiedPhysicalTableV0Test() {
		final String query = "select a.id, b.label from analytics.within a join accounts b on a.account_id = b.id where a.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, schema=analytics, table=within}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,79:79='b',<459>,1:79]]}, analytics.within={account_id=[[@17,64:64='a',<459>,1:64]], active=[[@25,90:90='a',<459>,1:90]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={accounts={label=[[@5,13:13='b',<459>,1:13]], id=[[@21,79:79='b',<459>,1:79]]}, analytics.within={account_id=[[@17,64:64='a',<459>,1:64]], active=[[@25,90:90='a',<459>,1:90]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=analytics.within, b=accounts}}}");
	}

	@Test
	public void contextualKeywordWITHINExplicitTableAliasV0Test() {
		final String query = "select within.id, b.label from events as within join accounts b on within.account_id = b.id where within.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=within}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=within, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=within}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=within}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,18:18='b',<459>,1:18]], id=[[@20,87:87='b',<459>,1:87]]}, events={account_id=[[@16,67:72='within',<425>,1:67]], active=[[@24,98:103='within',<425>,1:98]], id=[[@1,7:12='within',<425>,1:7]]}}",
				"{query0={id=[[@3,14:15='id',<459>,1:14]], label=[[@7,20:24='label',<459>,1:20]]}}",
				"{def_query0={query_dictionary={id=[[@3,14:15='id',<459>,1:14]], label=[[@7,20:24='label',<459>,1:20]]}, table_dictionary={accounts={label=[[@5,18:18='b',<459>,1:18]], id=[[@20,87:87='b',<459>,1:87]]}, events={account_id=[[@16,67:72='within',<425>,1:67]], active=[[@24,98:103='within',<425>,1:98]], id=[[@1,7:12='within',<425>,1:7]]}}, filters=[{name=account_id, table_ref=within}, {name=id, table_ref=b}, {name=active, table_ref=within}], interface={id=[{name=id, table_ref=within}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, within=events}}}");
	}

	@Test
	public void contextualKeywordWITHINImplicitTableAliasV0Test() {
		final String query = "select within.id, b.label from events within join accounts b on within.account_id = b.id where within.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=within}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=within, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=within}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={condition={left={column={name=active, table_ref=within}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{accounts={label=[[@5,18:18='b',<459>,1:18]], id=[[@19,84:84='b',<459>,1:84]]}, events={account_id=[[@15,64:69='within',<425>,1:64]], active=[[@23,95:100='within',<425>,1:95]], id=[[@1,7:12='within',<425>,1:7]]}}",
				"{query0={id=[[@3,14:15='id',<459>,1:14]], label=[[@7,20:24='label',<459>,1:20]]}}",
				"{def_query0={query_dictionary={id=[[@3,14:15='id',<459>,1:14]], label=[[@7,20:24='label',<459>,1:20]]}, table_dictionary={accounts={label=[[@5,18:18='b',<459>,1:18]], id=[[@19,84:84='b',<459>,1:84]]}, events={account_id=[[@15,64:69='within',<425>,1:64]], active=[[@23,95:100='within',<425>,1:95]], id=[[@1,7:12='within',<425>,1:7]]}}, filters=[{name=account_id, table_ref=within}, {name=id, table_ref=b}, {name=active, table_ref=within}], interface={id=[{name=id, table_ref=within}], label=[{name=label, table_ref=b}]}, table_alias={b=accounts, within=events}}}");
	}

	@Test
	public void contextualKeywordWITHINCteLabelAndOutputAliasV0Test() {
		final String query = "with within as (select a.amount as within, a.id as row_id from events a where a.active = 1) select within.within as result_value, within.row_id from within where within.row_id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=amount, table_ref=a}, alias=within}, 2={column={name=id, table_ref=a}, alias=row_id}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=within}}, query={select={1={column={name=within, table_ref=within}, alias=result_value}, 2={column={name=row_id, table_ref=within}}}, from={table={alias=null, table=within}}, where={condition={left={column={name=row_id, table_ref=within}}, right={literal=0}, operator=>}}}}}",
				"[result_value, row_id]",
				"{}",
				"{events={amount=[[@5,23:23='a',<459>,1:23]], active=[[@20,78:78='a',<459>,1:78]], id=[[@11,43:43='a',<459>,1:43]]}}",
				"{query0={row_id=[[@15,51:56='row_id',<459>,1:51], [@33,130:135='within',<425>,1:130], [@39,162:167='within',<425>,1:162]], within=[[@9,35:40='within',<425>,1:35], [@27,99:104='within',<425>,1:99]]}, query1={row_id=[[@35,137:142='row_id',<459>,1:137]], result_value=[[@31,116:127='result_value',<459>,1:116]]}}",
				"{def_query1={context_list={within=query0}, query_dictionary={result_value=[[@31,116:127='result_value',<459>,1:116]], row_id=[[@35,137:142='row_id',<459>,1:137]]}, def_query0={query_dictionary={within=[[@9,35:40='within',<425>,1:35], [@27,99:104='within',<425>,1:99]], row_id=[[@15,51:56='row_id',<459>,1:51], [@33,130:135='within',<425>,1:130], [@39,162:167='within',<425>,1:162]]}, table_dictionary={events={amount=[[@5,23:23='a',<459>,1:23]], active=[[@20,78:78='a',<459>,1:78]], id=[[@11,43:43='a',<459>,1:43]]}}, filters=[{name=active, table_ref=a}], interface={within=[{name=amount, table_ref=a}], row_id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=row_id, table_ref=within}], interface={result_value=[{name=within, table_ref=within}], row_id=[{name=row_id, table_ref=within}]}, table_alias={within=query0}}}");
	}

	@Test
	public void contextualKeywordWITHINWildcardQualifierV0Test() {
		final String query = "select within.* from events as within where within.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=within}}}, from={table={alias=within, table=events}}, where={condition={left={column={name=active, table_ref=within}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,44:49='within',<425>,1:44]], *=[[@1,7:12='within',<425>,1:7]]}}",
				"{query0={*=[[@3,14:14='*',<291>,1:14]]}}",
				"{def_query0={query_dictionary={*=[[@3,14:14='*',<291>,1:14]]}, table_dictionary={events={active=[[@9,44:49='within',<425>,1:44]], *=[[@1,7:12='within',<425>,1:7]]}}, filters=[{name=active, table_ref=within}], interface={*=[{name=*, table_ref=within}]}, table_alias={within=events}}}");
	}

	@Test
	public void contextualKeywordPOSITIONExistingContextualNamesV0Test() {
		final String query = "with position as (select a.position as position, a.id from analytics.position a where a.active = 1) select position.position as result_value, position.id from position as position where position.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=position, table_ref=a}, alias=position}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=position}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=position}}, query={select={1={column={name=position, table_ref=position}, alias=result_value}, 2={column={name=id, table_ref=position}}}, from={table={alias=position, table=position}}, where={condition={left={column={name=id, table_ref=position}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.position={active=[[@20,86:86='a',<459>,1:86]], position=[[@5,25:25='a',<459>,1:25]], id=[[@11,49:49='a',<459>,1:49]]}}",
				"{query0={position=[[@9,39:46='position',<331>,1:39], [@27,107:114='position',<331>,1:107]], id=[[@13,51:52='id',<459>,1:51], [@33,142:149='position',<331>,1:142], [@41,186:193='position',<331>,1:186]]}, query1={result_value=[[@31,128:139='result_value',<459>,1:128]], id=[[@35,151:152='id',<459>,1:151]]}}",
				"{def_query1={context_list={position=query0}, query_dictionary={result_value=[[@31,128:139='result_value',<459>,1:128]], id=[[@35,151:152='id',<459>,1:151]]}, def_query0={query_dictionary={position=[[@9,39:46='position',<331>,1:39], [@27,107:114='position',<331>,1:107]], id=[[@13,51:52='id',<459>,1:51], [@33,142:149='position',<331>,1:142], [@41,186:193='position',<331>,1:186]]}, table_dictionary={analytics.position={active=[[@20,86:86='a',<459>,1:86]], position=[[@5,25:25='a',<459>,1:25]], id=[[@11,49:49='a',<459>,1:49]]}}, filters=[{name=active, table_ref=a}], interface={position=[{name=position, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.position}}, filters=[{name=id, table_ref=position}], interface={result_value=[{name=position, table_ref=position}], id=[{name=id, table_ref=position}]}, table_alias={position=query0}}}");
	}

	@Test
	public void contextualKeywordPOSITIONExistingWildcardQualifierV0Test() {
		final String query = "select position.* from events as position where position.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=position}}}, from={table={alias=position, table=events}}, where={condition={left={column={name=active, table_ref=position}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,48:55='position',<331>,1:48]], *=[[@1,7:14='position',<331>,1:7]]}}",
				"{query0={*=[[@3,16:16='*',<291>,1:16]]}}",
				"{def_query0={query_dictionary={*=[[@3,16:16='*',<291>,1:16]]}, table_dictionary={events={active=[[@9,48:55='position',<331>,1:48]], *=[[@1,7:14='position',<331>,1:7]]}}, filters=[{name=active, table_ref=position}], interface={*=[{name=*, table_ref=position}]}, table_alias={position=events}}}");
	}

	@Test
	public void contextualKeywordDELETEExistingContextualNamesV0Test() {
		final String query = "with delete as (select a.delete as delete, a.id from analytics.delete a where a.active = 1) select delete.delete as result_value, delete.id from delete as delete where delete.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=delete, table_ref=a}, alias=delete}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=delete}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=delete}}, query={select={1={column={name=delete, table_ref=delete}, alias=result_value}, 2={column={name=id, table_ref=delete}}}, from={table={alias=delete, table=delete}}, where={condition={left={column={name=id, table_ref=delete}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.delete={active=[[@20,78:78='a',<459>,1:78]], id=[[@11,43:43='a',<459>,1:43]], delete=[[@5,23:23='a',<459>,1:23]]}}",
				"{query0={delete=[[@9,35:40='delete',<334>,1:35], [@27,99:104='delete',<334>,1:99]], id=[[@13,45:46='id',<459>,1:45], [@33,130:135='delete',<334>,1:130], [@41,168:173='delete',<334>,1:168]]}, query1={result_value=[[@31,116:127='result_value',<459>,1:116]], id=[[@35,137:138='id',<459>,1:137]]}}",
				"{def_query1={context_list={delete=query0}, query_dictionary={result_value=[[@31,116:127='result_value',<459>,1:116]], id=[[@35,137:138='id',<459>,1:137]]}, def_query0={query_dictionary={id=[[@13,45:46='id',<459>,1:45], [@33,130:135='delete',<334>,1:130], [@41,168:173='delete',<334>,1:168]], delete=[[@9,35:40='delete',<334>,1:35], [@27,99:104='delete',<334>,1:99]]}, table_dictionary={analytics.delete={active=[[@20,78:78='a',<459>,1:78]], id=[[@11,43:43='a',<459>,1:43]], delete=[[@5,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], delete=[{name=delete, table_ref=a}]}, table_alias={a=analytics.delete}}, filters=[{name=id, table_ref=delete}], interface={result_value=[{name=delete, table_ref=delete}], id=[{name=id, table_ref=delete}]}, table_alias={delete=query0}}}");
	}

	@Test
	public void contextualKeywordDELETEExistingWildcardQualifierV0Test() {
		final String query = "select delete.* from events as delete where delete.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=delete}}}, from={table={alias=delete, table=events}}, where={condition={left={column={name=active, table_ref=delete}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,44:49='delete',<334>,1:44]], *=[[@1,7:12='delete',<334>,1:7]]}}",
				"{query0={*=[[@3,14:14='*',<291>,1:14]]}}",
				"{def_query0={query_dictionary={*=[[@3,14:14='*',<291>,1:14]]}, table_dictionary={events={active=[[@9,44:49='delete',<334>,1:44]], *=[[@1,7:12='delete',<334>,1:7]]}}, filters=[{name=active, table_ref=delete}], interface={*=[{name=*, table_ref=delete}]}, table_alias={delete=events}}}");
	}

	@Test
	public void contextualKeywordFLATTENExistingContextualNamesV0Test() {
		final String query = "with flatten as (select a.flatten as flatten, a.id from analytics.flatten a where a.active = 1) select flatten.flatten as result_value, flatten.id from flatten as flatten where flatten.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=flatten, table_ref=a}, alias=flatten}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=flatten}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=flatten}}, query={select={1={column={name=flatten, table_ref=flatten}, alias=result_value}, 2={column={name=id, table_ref=flatten}}}, from={table={alias=flatten, table=flatten}}, where={condition={left={column={name=id, table_ref=flatten}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.flatten={flatten=[[@5,24:24='a',<459>,1:24]], active=[[@20,82:82='a',<459>,1:82]], id=[[@11,46:46='a',<459>,1:46]]}}",
				"{query0={flatten=[[@9,37:43='flatten',<339>,1:37], [@27,103:109='flatten',<339>,1:103]], id=[[@13,48:49='id',<459>,1:48], [@33,136:142='flatten',<339>,1:136], [@41,177:183='flatten',<339>,1:177]]}, query1={result_value=[[@31,122:133='result_value',<459>,1:122]], id=[[@35,144:145='id',<459>,1:144]]}}",
				"{def_query1={context_list={flatten=query0}, query_dictionary={result_value=[[@31,122:133='result_value',<459>,1:122]], id=[[@35,144:145='id',<459>,1:144]]}, def_query0={query_dictionary={flatten=[[@9,37:43='flatten',<339>,1:37], [@27,103:109='flatten',<339>,1:103]], id=[[@13,48:49='id',<459>,1:48], [@33,136:142='flatten',<339>,1:136], [@41,177:183='flatten',<339>,1:177]]}, table_dictionary={analytics.flatten={flatten=[[@5,24:24='a',<459>,1:24]], active=[[@20,82:82='a',<459>,1:82]], id=[[@11,46:46='a',<459>,1:46]]}}, filters=[{name=active, table_ref=a}], interface={flatten=[{name=flatten, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.flatten}}, filters=[{name=id, table_ref=flatten}], interface={result_value=[{name=flatten, table_ref=flatten}], id=[{name=id, table_ref=flatten}]}, table_alias={flatten=query0}}}");
	}

	@Test
	public void contextualKeywordFLATTENExistingWildcardQualifierV0Test() {
		final String query = "select flatten.* from events as flatten where flatten.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=flatten}}}, from={table={alias=flatten, table=events}}, where={condition={left={column={name=active, table_ref=flatten}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,46:52='flatten',<339>,1:46]], *=[[@1,7:13='flatten',<339>,1:7]]}}",
				"{query0={*=[[@3,15:15='*',<291>,1:15]]}}",
				"{def_query0={query_dictionary={*=[[@3,15:15='*',<291>,1:15]]}, table_dictionary={events={active=[[@9,46:52='flatten',<339>,1:46]], *=[[@1,7:13='flatten',<339>,1:7]]}}, filters=[{name=active, table_ref=flatten}], interface={*=[{name=*, table_ref=flatten}]}, table_alias={flatten=events}}}");
	}

	@Test
	public void contextualKeywordSPLITTOTABLEExistingContextualNamesV0Test() {
		final String query = "with split_to_table as (select a.split_to_table as split_to_table, a.id from analytics.split_to_table a where a.active = 1) select split_to_table.split_to_table as result_value, split_to_table.id from split_to_table as split_to_table where split_to_table.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=split_to_table, table_ref=a}, alias=split_to_table}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=split_to_table}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=split_to_table}}, query={select={1={column={name=split_to_table, table_ref=split_to_table}, alias=result_value}, 2={column={name=id, table_ref=split_to_table}}}, from={table={alias=split_to_table, table=split_to_table}}, where={condition={left={column={name=id, table_ref=split_to_table}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.split_to_table={split_to_table=[[@5,31:31='a',<459>,1:31]], active=[[@20,110:110='a',<459>,1:110]], id=[[@11,67:67='a',<459>,1:67]]}}",
				"{query0={split_to_table=[[@9,51:64='split_to_table',<340>,1:51], [@27,131:144='split_to_table',<340>,1:131]], id=[[@13,69:70='id',<459>,1:69], [@33,178:191='split_to_table',<340>,1:178], [@41,240:253='split_to_table',<340>,1:240]]}, query1={result_value=[[@31,164:175='result_value',<459>,1:164]], id=[[@35,193:194='id',<459>,1:193]]}}",
				"{def_query1={context_list={split_to_table=query0}, query_dictionary={result_value=[[@31,164:175='result_value',<459>,1:164]], id=[[@35,193:194='id',<459>,1:193]]}, def_query0={query_dictionary={split_to_table=[[@9,51:64='split_to_table',<340>,1:51], [@27,131:144='split_to_table',<340>,1:131]], id=[[@13,69:70='id',<459>,1:69], [@33,178:191='split_to_table',<340>,1:178], [@41,240:253='split_to_table',<340>,1:240]]}, table_dictionary={analytics.split_to_table={split_to_table=[[@5,31:31='a',<459>,1:31]], active=[[@20,110:110='a',<459>,1:110]], id=[[@11,67:67='a',<459>,1:67]]}}, filters=[{name=active, table_ref=a}], interface={split_to_table=[{name=split_to_table, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.split_to_table}}, filters=[{name=id, table_ref=split_to_table}], interface={result_value=[{name=split_to_table, table_ref=split_to_table}], id=[{name=id, table_ref=split_to_table}]}, table_alias={split_to_table=query0}}}");
	}

	@Test
	public void contextualKeywordSPLITTOTABLEExistingWildcardQualifierV0Test() {
		final String query = "select split_to_table.* from events as split_to_table where split_to_table.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=split_to_table}}}, from={table={alias=split_to_table, table=events}}, where={condition={left={column={name=active, table_ref=split_to_table}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,60:73='split_to_table',<340>,1:60]], *=[[@1,7:20='split_to_table',<340>,1:7]]}}",
				"{query0={*=[[@3,22:22='*',<291>,1:22]]}}",
				"{def_query0={query_dictionary={*=[[@3,22:22='*',<291>,1:22]]}, table_dictionary={events={active=[[@9,60:73='split_to_table',<340>,1:60]], *=[[@1,7:20='split_to_table',<340>,1:7]]}}, filters=[{name=active, table_ref=split_to_table}], interface={*=[{name=*, table_ref=split_to_table}]}, table_alias={split_to_table=events}}}");
	}

	@Test
	public void contextualKeywordSTRTOKSPLITTOTABLEExistingContextualNamesV0Test() {
		final String query = "with strtok_split_to_table as (select a.strtok_split_to_table as strtok_split_to_table, a.id from analytics.strtok_split_to_table a where a.active = 1) select strtok_split_to_table.strtok_split_to_table as result_value, strtok_split_to_table.id from strtok_split_to_table as strtok_split_to_table where strtok_split_to_table.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=strtok_split_to_table, table_ref=a}, alias=strtok_split_to_table}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=strtok_split_to_table}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=strtok_split_to_table}}, query={select={1={column={name=strtok_split_to_table, table_ref=strtok_split_to_table}, alias=result_value}, 2={column={name=id, table_ref=strtok_split_to_table}}}, from={table={alias=strtok_split_to_table, table=strtok_split_to_table}}, where={condition={left={column={name=id, table_ref=strtok_split_to_table}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.strtok_split_to_table={strtok_split_to_table=[[@5,38:38='a',<459>,1:38]], active=[[@20,138:138='a',<459>,1:138]], id=[[@11,88:88='a',<459>,1:88]]}}",
				"{query0={strtok_split_to_table=[[@9,65:85='strtok_split_to_table',<341>,1:65], [@27,159:179='strtok_split_to_table',<341>,1:159]], id=[[@13,90:91='id',<459>,1:90], [@33,220:240='strtok_split_to_table',<341>,1:220], [@41,303:323='strtok_split_to_table',<341>,1:303]]}, query1={result_value=[[@31,206:217='result_value',<459>,1:206]], id=[[@35,242:243='id',<459>,1:242]]}}",
				"{def_query1={context_list={strtok_split_to_table=query0}, query_dictionary={result_value=[[@31,206:217='result_value',<459>,1:206]], id=[[@35,242:243='id',<459>,1:242]]}, def_query0={query_dictionary={strtok_split_to_table=[[@9,65:85='strtok_split_to_table',<341>,1:65], [@27,159:179='strtok_split_to_table',<341>,1:159]], id=[[@13,90:91='id',<459>,1:90], [@33,220:240='strtok_split_to_table',<341>,1:220], [@41,303:323='strtok_split_to_table',<341>,1:303]]}, table_dictionary={analytics.strtok_split_to_table={strtok_split_to_table=[[@5,38:38='a',<459>,1:38]], active=[[@20,138:138='a',<459>,1:138]], id=[[@11,88:88='a',<459>,1:88]]}}, filters=[{name=active, table_ref=a}], interface={strtok_split_to_table=[{name=strtok_split_to_table, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.strtok_split_to_table}}, filters=[{name=id, table_ref=strtok_split_to_table}], interface={result_value=[{name=strtok_split_to_table, table_ref=strtok_split_to_table}], id=[{name=id, table_ref=strtok_split_to_table}]}, table_alias={strtok_split_to_table=query0}}}");
	}

	@Test
	public void contextualKeywordSTRTOKSPLITTOTABLEExistingWildcardQualifierV0Test() {
		final String query = "select strtok_split_to_table.* from events as strtok_split_to_table where strtok_split_to_table.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=strtok_split_to_table}}}, from={table={alias=strtok_split_to_table, table=events}}, where={condition={left={column={name=active, table_ref=strtok_split_to_table}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,74:94='strtok_split_to_table',<341>,1:74]], *=[[@1,7:27='strtok_split_to_table',<341>,1:7]]}}",
				"{query0={*=[[@3,29:29='*',<291>,1:29]]}}",
				"{def_query0={query_dictionary={*=[[@3,29:29='*',<291>,1:29]]}, table_dictionary={events={active=[[@9,74:94='strtok_split_to_table',<341>,1:74]], *=[[@1,7:27='strtok_split_to_table',<341>,1:7]]}}, filters=[{name=active, table_ref=strtok_split_to_table}], interface={*=[{name=*, table_ref=strtok_split_to_table}]}, table_alias={strtok_split_to_table=events}}}");
	}

	@Test
	public void contextualKeywordGENERATORExistingContextualNamesV0Test() {
		final String query = "with generator as (select a.generator as generator, a.id from analytics.generator a where a.active = 1) select generator.generator as result_value, generator.id from generator as generator where generator.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=generator, table_ref=a}, alias=generator}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=generator}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=generator}}, query={select={1={column={name=generator, table_ref=generator}, alias=result_value}, 2={column={name=id, table_ref=generator}}}, from={table={alias=generator, table=generator}}, where={condition={left={column={name=id, table_ref=generator}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.generator={generator=[[@5,26:26='a',<459>,1:26]], active=[[@20,90:90='a',<459>,1:90]], id=[[@11,52:52='a',<459>,1:52]]}}",
				"{query0={generator=[[@9,41:49='generator',<342>,1:41], [@27,111:119='generator',<342>,1:111]], id=[[@13,54:55='id',<459>,1:54], [@33,148:156='generator',<342>,1:148], [@41,195:203='generator',<342>,1:195]]}, query1={result_value=[[@31,134:145='result_value',<459>,1:134]], id=[[@35,158:159='id',<459>,1:158]]}}",
				"{def_query1={context_list={generator=query0}, query_dictionary={result_value=[[@31,134:145='result_value',<459>,1:134]], id=[[@35,158:159='id',<459>,1:158]]}, def_query0={query_dictionary={generator=[[@9,41:49='generator',<342>,1:41], [@27,111:119='generator',<342>,1:111]], id=[[@13,54:55='id',<459>,1:54], [@33,148:156='generator',<342>,1:148], [@41,195:203='generator',<342>,1:195]]}, table_dictionary={analytics.generator={generator=[[@5,26:26='a',<459>,1:26]], active=[[@20,90:90='a',<459>,1:90]], id=[[@11,52:52='a',<459>,1:52]]}}, filters=[{name=active, table_ref=a}], interface={generator=[{name=generator, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.generator}}, filters=[{name=id, table_ref=generator}], interface={result_value=[{name=generator, table_ref=generator}], id=[{name=id, table_ref=generator}]}, table_alias={generator=query0}}}");
	}

	@Test
	public void contextualKeywordGENERATORExistingWildcardQualifierV0Test() {
		final String query = "select generator.* from events as generator where generator.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=generator}}}, from={table={alias=generator, table=events}}, where={condition={left={column={name=active, table_ref=generator}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,50:58='generator',<342>,1:50]], *=[[@1,7:15='generator',<342>,1:7]]}}",
				"{query0={*=[[@3,17:17='*',<291>,1:17]]}}",
				"{def_query0={query_dictionary={*=[[@3,17:17='*',<291>,1:17]]}, table_dictionary={events={active=[[@9,50:58='generator',<342>,1:50]], *=[[@1,7:15='generator',<342>,1:7]]}}, filters=[{name=active, table_ref=generator}], interface={*=[{name=*, table_ref=generator}]}, table_alias={generator=events}}}");
	}

	@Test
	public void contextualKeywordINFERSCHEMAExistingContextualNamesV0Test() {
		final String query = "with infer_schema as (select a.infer_schema as infer_schema, a.id from analytics.infer_schema a where a.active = 1) select infer_schema.infer_schema as result_value, infer_schema.id from infer_schema as infer_schema where infer_schema.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=infer_schema, table_ref=a}, alias=infer_schema}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=infer_schema}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=infer_schema}}, query={select={1={column={name=infer_schema, table_ref=infer_schema}, alias=result_value}, 2={column={name=id, table_ref=infer_schema}}}, from={table={alias=infer_schema, table=infer_schema}}, where={condition={left={column={name=id, table_ref=infer_schema}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.infer_schema={active=[[@20,102:102='a',<459>,1:102]], infer_schema=[[@5,29:29='a',<459>,1:29]], id=[[@11,61:61='a',<459>,1:61]]}}",
				"{query0={infer_schema=[[@9,47:58='infer_schema',<343>,1:47], [@27,123:134='infer_schema',<343>,1:123]], id=[[@13,63:64='id',<459>,1:63], [@33,166:177='infer_schema',<343>,1:166], [@41,222:233='infer_schema',<343>,1:222]]}, query1={result_value=[[@31,152:163='result_value',<459>,1:152]], id=[[@35,179:180='id',<459>,1:179]]}}",
				"{def_query1={context_list={infer_schema=query0}, query_dictionary={result_value=[[@31,152:163='result_value',<459>,1:152]], id=[[@35,179:180='id',<459>,1:179]]}, def_query0={query_dictionary={infer_schema=[[@9,47:58='infer_schema',<343>,1:47], [@27,123:134='infer_schema',<343>,1:123]], id=[[@13,63:64='id',<459>,1:63], [@33,166:177='infer_schema',<343>,1:166], [@41,222:233='infer_schema',<343>,1:222]]}, table_dictionary={analytics.infer_schema={active=[[@20,102:102='a',<459>,1:102]], infer_schema=[[@5,29:29='a',<459>,1:29]], id=[[@11,61:61='a',<459>,1:61]]}}, filters=[{name=active, table_ref=a}], interface={infer_schema=[{name=infer_schema, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.infer_schema}}, filters=[{name=id, table_ref=infer_schema}], interface={result_value=[{name=infer_schema, table_ref=infer_schema}], id=[{name=id, table_ref=infer_schema}]}, table_alias={infer_schema=query0}}}");
	}

	@Test
	public void contextualKeywordINFERSCHEMAExistingWildcardQualifierV0Test() {
		final String query = "select infer_schema.* from events as infer_schema where infer_schema.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=infer_schema}}}, from={table={alias=infer_schema, table=events}}, where={condition={left={column={name=active, table_ref=infer_schema}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,56:67='infer_schema',<343>,1:56]], *=[[@1,7:18='infer_schema',<343>,1:7]]}}",
				"{query0={*=[[@3,20:20='*',<291>,1:20]]}}",
				"{def_query0={query_dictionary={*=[[@3,20:20='*',<291>,1:20]]}, table_dictionary={events={active=[[@9,56:67='infer_schema',<343>,1:56]], *=[[@1,7:18='infer_schema',<343>,1:7]]}}, filters=[{name=active, table_ref=infer_schema}], interface={*=[{name=*, table_ref=infer_schema}]}, table_alias={infer_schema=events}}}");
	}

	@Test
	public void contextualKeywordVALIDATEExistingContextualNamesV0Test() {
		final String query = "with validate as (select a.validate as validate, a.id from analytics.validate a where a.active = 1) select validate.validate as result_value, validate.id from validate as validate where validate.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=validate, table_ref=a}, alias=validate}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=validate}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=validate}}, query={select={1={column={name=validate, table_ref=validate}, alias=result_value}, 2={column={name=id, table_ref=validate}}}, from={table={alias=validate, table=validate}}, where={condition={left={column={name=id, table_ref=validate}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.validate={active=[[@20,86:86='a',<459>,1:86]], id=[[@11,49:49='a',<459>,1:49]], validate=[[@5,25:25='a',<459>,1:25]]}}",
				"{query0={id=[[@13,51:52='id',<459>,1:51], [@33,142:149='validate',<344>,1:142], [@41,186:193='validate',<344>,1:186]], validate=[[@9,39:46='validate',<344>,1:39], [@27,107:114='validate',<344>,1:107]]}, query1={result_value=[[@31,128:139='result_value',<459>,1:128]], id=[[@35,151:152='id',<459>,1:151]]}}",
				"{def_query1={context_list={validate=query0}, query_dictionary={result_value=[[@31,128:139='result_value',<459>,1:128]], id=[[@35,151:152='id',<459>,1:151]]}, def_query0={query_dictionary={id=[[@13,51:52='id',<459>,1:51], [@33,142:149='validate',<344>,1:142], [@41,186:193='validate',<344>,1:186]], validate=[[@9,39:46='validate',<344>,1:39], [@27,107:114='validate',<344>,1:107]]}, table_dictionary={analytics.validate={active=[[@20,86:86='a',<459>,1:86]], id=[[@11,49:49='a',<459>,1:49]], validate=[[@5,25:25='a',<459>,1:25]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], validate=[{name=validate, table_ref=a}]}, table_alias={a=analytics.validate}}, filters=[{name=id, table_ref=validate}], interface={result_value=[{name=validate, table_ref=validate}], id=[{name=id, table_ref=validate}]}, table_alias={validate=query0}}}");
	}

	@Test
	public void contextualKeywordVALIDATEExistingWildcardQualifierV0Test() {
		final String query = "select validate.* from events as validate where validate.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=validate}}}, from={table={alias=validate, table=events}}, where={condition={left={column={name=active, table_ref=validate}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,48:55='validate',<344>,1:48]], *=[[@1,7:14='validate',<344>,1:7]]}}",
				"{query0={*=[[@3,16:16='*',<291>,1:16]]}}",
				"{def_query0={query_dictionary={*=[[@3,16:16='*',<291>,1:16]]}, table_dictionary={events={active=[[@9,48:55='validate',<344>,1:48]], *=[[@1,7:14='validate',<344>,1:7]]}}, filters=[{name=active, table_ref=validate}], interface={*=[{name=*, table_ref=validate}]}, table_alias={validate=events}}}");
	}

	@Test
	public void contextualKeywordRESULTSCANExistingContextualNamesV0Test() {
		final String query = "with result_scan as (select a.result_scan as result_scan, a.id from analytics.result_scan a where a.active = 1) select result_scan.result_scan as result_value, result_scan.id from result_scan as result_scan where result_scan.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=result_scan, table_ref=a}, alias=result_scan}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=result_scan}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=result_scan}}, query={select={1={column={name=result_scan, table_ref=result_scan}, alias=result_value}, 2={column={name=id, table_ref=result_scan}}}, from={table={alias=result_scan, table=result_scan}}, where={condition={left={column={name=id, table_ref=result_scan}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.result_scan={result_scan=[[@5,28:28='a',<459>,1:28]], active=[[@20,98:98='a',<459>,1:98]], id=[[@11,58:58='a',<459>,1:58]]}}",
				"{query0={result_scan=[[@9,45:55='result_scan',<345>,1:45], [@27,119:129='result_scan',<345>,1:119]], id=[[@13,60:61='id',<459>,1:60], [@33,160:170='result_scan',<345>,1:160], [@41,213:223='result_scan',<345>,1:213]]}, query1={result_value=[[@31,146:157='result_value',<459>,1:146]], id=[[@35,172:173='id',<459>,1:172]]}}",
				"{def_query1={context_list={result_scan=query0}, query_dictionary={result_value=[[@31,146:157='result_value',<459>,1:146]], id=[[@35,172:173='id',<459>,1:172]]}, def_query0={query_dictionary={result_scan=[[@9,45:55='result_scan',<345>,1:45], [@27,119:129='result_scan',<345>,1:119]], id=[[@13,60:61='id',<459>,1:60], [@33,160:170='result_scan',<345>,1:160], [@41,213:223='result_scan',<345>,1:213]]}, table_dictionary={analytics.result_scan={result_scan=[[@5,28:28='a',<459>,1:28]], active=[[@20,98:98='a',<459>,1:98]], id=[[@11,58:58='a',<459>,1:58]]}}, filters=[{name=active, table_ref=a}], interface={result_scan=[{name=result_scan, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.result_scan}}, filters=[{name=id, table_ref=result_scan}], interface={result_value=[{name=result_scan, table_ref=result_scan}], id=[{name=id, table_ref=result_scan}]}, table_alias={result_scan=query0}}}");
	}

	@Test
	public void contextualKeywordRESULTSCANExistingWildcardQualifierV0Test() {
		final String query = "select result_scan.* from events as result_scan where result_scan.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=result_scan}}}, from={table={alias=result_scan, table=events}}, where={condition={left={column={name=active, table_ref=result_scan}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,54:64='result_scan',<345>,1:54]], *=[[@1,7:17='result_scan',<345>,1:7]]}}",
				"{query0={*=[[@3,19:19='*',<291>,1:19]]}}",
				"{def_query0={query_dictionary={*=[[@3,19:19='*',<291>,1:19]]}, table_dictionary={events={active=[[@9,54:64='result_scan',<345>,1:54]], *=[[@1,7:17='result_scan',<345>,1:7]]}}, filters=[{name=active, table_ref=result_scan}], interface={*=[{name=*, table_ref=result_scan}]}, table_alias={result_scan=events}}}");
	}

	@Test
	public void contextualKeywordQUERYHISTORYExistingContextualNamesV0Test() {
		final String query = "with query_history as (select a.query_history as query_history, a.id from analytics.query_history a where a.active = 1) select query_history.query_history as result_value, query_history.id from query_history as query_history where query_history.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=query_history, table_ref=a}, alias=query_history}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=query_history}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=query_history}}, query={select={1={column={name=query_history, table_ref=query_history}, alias=result_value}, 2={column={name=id, table_ref=query_history}}}, from={table={alias=query_history, table=query_history}}, where={condition={left={column={name=id, table_ref=query_history}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.query_history={query_history=[[@5,30:30='a',<459>,1:30]], active=[[@20,106:106='a',<459>,1:106]], id=[[@11,64:64='a',<459>,1:64]]}}",
				"{query0={query_history=[[@9,49:61='query_history',<346>,1:49], [@27,127:139='query_history',<346>,1:127]], id=[[@13,66:67='id',<459>,1:66], [@33,172:184='query_history',<346>,1:172], [@41,231:243='query_history',<346>,1:231]]}, query1={result_value=[[@31,158:169='result_value',<459>,1:158]], id=[[@35,186:187='id',<459>,1:186]]}}",
				"{def_query1={context_list={query_history=query0}, query_dictionary={result_value=[[@31,158:169='result_value',<459>,1:158]], id=[[@35,186:187='id',<459>,1:186]]}, def_query0={query_dictionary={query_history=[[@9,49:61='query_history',<346>,1:49], [@27,127:139='query_history',<346>,1:127]], id=[[@13,66:67='id',<459>,1:66], [@33,172:184='query_history',<346>,1:172], [@41,231:243='query_history',<346>,1:231]]}, table_dictionary={analytics.query_history={query_history=[[@5,30:30='a',<459>,1:30]], active=[[@20,106:106='a',<459>,1:106]], id=[[@11,64:64='a',<459>,1:64]]}}, filters=[{name=active, table_ref=a}], interface={query_history=[{name=query_history, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.query_history}}, filters=[{name=id, table_ref=query_history}], interface={result_value=[{name=query_history, table_ref=query_history}], id=[{name=id, table_ref=query_history}]}, table_alias={query_history=query0}}}");
	}

	@Test
	public void contextualKeywordQUERYHISTORYExistingWildcardQualifierV0Test() {
		final String query = "select query_history.* from events as query_history where query_history.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=query_history}}}, from={table={alias=query_history, table=events}}, where={condition={left={column={name=active, table_ref=query_history}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,58:70='query_history',<346>,1:58]], *=[[@1,7:19='query_history',<346>,1:7]]}}",
				"{query0={*=[[@3,21:21='*',<291>,1:21]]}}",
				"{def_query0={query_dictionary={*=[[@3,21:21='*',<291>,1:21]]}, table_dictionary={events={active=[[@9,58:70='query_history',<346>,1:58]], *=[[@1,7:19='query_history',<346>,1:7]]}}, filters=[{name=active, table_ref=query_history}], interface={*=[{name=*, table_ref=query_history}]}, table_alias={query_history=events}}}");
	}

	@Test
	public void contextualKeywordARRAYCONSTRUCTExistingContextualNamesV0Test() {
		final String query = "with array_construct as (select a.array_construct as array_construct, a.id from analytics.array_construct a where a.active = 1) select array_construct.array_construct as result_value, array_construct.id from array_construct as array_construct where array_construct.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_construct, table_ref=a}, alias=array_construct}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_construct}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_construct}}, query={select={1={column={name=array_construct, table_ref=array_construct}, alias=result_value}, 2={column={name=id, table_ref=array_construct}}}, from={table={alias=array_construct, table=array_construct}}, where={condition={left={column={name=id, table_ref=array_construct}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_construct={array_construct=[[@5,32:32='a',<459>,1:32]], active=[[@20,114:114='a',<459>,1:114]], id=[[@11,70:70='a',<459>,1:70]]}}",
				"{query0={array_construct=[[@9,53:67='array_construct',<348>,1:53], [@27,135:149='array_construct',<348>,1:135]], id=[[@13,72:73='id',<459>,1:72], [@33,184:198='array_construct',<348>,1:184], [@41,249:263='array_construct',<348>,1:249]]}, query1={result_value=[[@31,170:181='result_value',<459>,1:170]], id=[[@35,200:201='id',<459>,1:200]]}}",
				"{def_query1={context_list={array_construct=query0}, query_dictionary={result_value=[[@31,170:181='result_value',<459>,1:170]], id=[[@35,200:201='id',<459>,1:200]]}, def_query0={query_dictionary={array_construct=[[@9,53:67='array_construct',<348>,1:53], [@27,135:149='array_construct',<348>,1:135]], id=[[@13,72:73='id',<459>,1:72], [@33,184:198='array_construct',<348>,1:184], [@41,249:263='array_construct',<348>,1:249]]}, table_dictionary={analytics.array_construct={array_construct=[[@5,32:32='a',<459>,1:32]], active=[[@20,114:114='a',<459>,1:114]], id=[[@11,70:70='a',<459>,1:70]]}}, filters=[{name=active, table_ref=a}], interface={array_construct=[{name=array_construct, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.array_construct}}, filters=[{name=id, table_ref=array_construct}], interface={result_value=[{name=array_construct, table_ref=array_construct}], id=[{name=id, table_ref=array_construct}]}, table_alias={array_construct=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYCONSTRUCTExistingWildcardQualifierV0Test() {
		final String query = "select array_construct.* from events as array_construct where array_construct.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_construct}}}, from={table={alias=array_construct, table=events}}, where={condition={left={column={name=active, table_ref=array_construct}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,62:76='array_construct',<348>,1:62]], *=[[@1,7:21='array_construct',<348>,1:7]]}}",
				"{query0={*=[[@3,23:23='*',<291>,1:23]]}}",
				"{def_query0={query_dictionary={*=[[@3,23:23='*',<291>,1:23]]}, table_dictionary={events={active=[[@9,62:76='array_construct',<348>,1:62]], *=[[@1,7:21='array_construct',<348>,1:7]]}}, filters=[{name=active, table_ref=array_construct}], interface={*=[{name=*, table_ref=array_construct}]}, table_alias={array_construct=events}}}");
	}

	@Test
	public void contextualKeywordARRAYCONSTRUCTCOMPACTExistingContextualNamesV0Test() {
		final String query = "with array_construct_compact as (select a.array_construct_compact as array_construct_compact, a.id from analytics.array_construct_compact a where a.active = 1) select array_construct_compact.array_construct_compact as result_value, array_construct_compact.id from array_construct_compact as array_construct_compact where array_construct_compact.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_construct_compact, table_ref=a}, alias=array_construct_compact}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_construct_compact}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_construct_compact}}, query={select={1={column={name=array_construct_compact, table_ref=array_construct_compact}, alias=result_value}, 2={column={name=id, table_ref=array_construct_compact}}}, from={table={alias=array_construct_compact, table=array_construct_compact}}, where={condition={left={column={name=id, table_ref=array_construct_compact}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_construct_compact={active=[[@20,146:146='a',<459>,1:146]], array_construct_compact=[[@5,40:40='a',<459>,1:40]], id=[[@11,94:94='a',<459>,1:94]]}}",
				"{query0={array_construct_compact=[[@9,69:91='array_construct_compact',<349>,1:69], [@27,167:189='array_construct_compact',<349>,1:167]], id=[[@13,96:97='id',<459>,1:96], [@33,232:254='array_construct_compact',<349>,1:232], [@41,321:343='array_construct_compact',<349>,1:321]]}, query1={result_value=[[@31,218:229='result_value',<459>,1:218]], id=[[@35,256:257='id',<459>,1:256]]}}",
				"{def_query1={context_list={array_construct_compact=query0}, query_dictionary={result_value=[[@31,218:229='result_value',<459>,1:218]], id=[[@35,256:257='id',<459>,1:256]]}, def_query0={query_dictionary={array_construct_compact=[[@9,69:91='array_construct_compact',<349>,1:69], [@27,167:189='array_construct_compact',<349>,1:167]], id=[[@13,96:97='id',<459>,1:96], [@33,232:254='array_construct_compact',<349>,1:232], [@41,321:343='array_construct_compact',<349>,1:321]]}, table_dictionary={analytics.array_construct_compact={active=[[@20,146:146='a',<459>,1:146]], array_construct_compact=[[@5,40:40='a',<459>,1:40]], id=[[@11,94:94='a',<459>,1:94]]}}, filters=[{name=active, table_ref=a}], interface={array_construct_compact=[{name=array_construct_compact, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.array_construct_compact}}, filters=[{name=id, table_ref=array_construct_compact}], interface={result_value=[{name=array_construct_compact, table_ref=array_construct_compact}], id=[{name=id, table_ref=array_construct_compact}]}, table_alias={array_construct_compact=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYCONSTRUCTCOMPACTExistingWildcardQualifierV0Test() {
		final String query = "select array_construct_compact.* from events as array_construct_compact where array_construct_compact.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_construct_compact}}}, from={table={alias=array_construct_compact, table=events}}, where={condition={left={column={name=active, table_ref=array_construct_compact}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,78:100='array_construct_compact',<349>,1:78]], *=[[@1,7:29='array_construct_compact',<349>,1:7]]}}",
				"{query0={*=[[@3,31:31='*',<291>,1:31]]}}",
				"{def_query0={query_dictionary={*=[[@3,31:31='*',<291>,1:31]]}, table_dictionary={events={active=[[@9,78:100='array_construct_compact',<349>,1:78]], *=[[@1,7:29='array_construct_compact',<349>,1:7]]}}, filters=[{name=active, table_ref=array_construct_compact}], interface={*=[{name=*, table_ref=array_construct_compact}]}, table_alias={array_construct_compact=events}}}");
	}

	@Test
	public void contextualKeywordARRAYCONTAINSExistingContextualNamesV0Test() {
		final String query = "with array_contains as (select a.array_contains as array_contains, a.id from analytics.array_contains a where a.active = 1) select array_contains.array_contains as result_value, array_contains.id from array_contains as array_contains where array_contains.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_contains, table_ref=a}, alias=array_contains}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_contains}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_contains}}, query={select={1={column={name=array_contains, table_ref=array_contains}, alias=result_value}, 2={column={name=id, table_ref=array_contains}}}, from={table={alias=array_contains, table=array_contains}}, where={condition={left={column={name=id, table_ref=array_contains}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_contains={active=[[@20,110:110='a',<459>,1:110]], array_contains=[[@5,31:31='a',<459>,1:31]], id=[[@11,67:67='a',<459>,1:67]]}}",
				"{query0={array_contains=[[@9,51:64='array_contains',<350>,1:51], [@27,131:144='array_contains',<350>,1:131]], id=[[@13,69:70='id',<459>,1:69], [@33,178:191='array_contains',<350>,1:178], [@41,240:253='array_contains',<350>,1:240]]}, query1={result_value=[[@31,164:175='result_value',<459>,1:164]], id=[[@35,193:194='id',<459>,1:193]]}}",
				"{def_query1={context_list={array_contains=query0}, query_dictionary={result_value=[[@31,164:175='result_value',<459>,1:164]], id=[[@35,193:194='id',<459>,1:193]]}, def_query0={query_dictionary={array_contains=[[@9,51:64='array_contains',<350>,1:51], [@27,131:144='array_contains',<350>,1:131]], id=[[@13,69:70='id',<459>,1:69], [@33,178:191='array_contains',<350>,1:178], [@41,240:253='array_contains',<350>,1:240]]}, table_dictionary={analytics.array_contains={active=[[@20,110:110='a',<459>,1:110]], array_contains=[[@5,31:31='a',<459>,1:31]], id=[[@11,67:67='a',<459>,1:67]]}}, filters=[{name=active, table_ref=a}], interface={array_contains=[{name=array_contains, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.array_contains}}, filters=[{name=id, table_ref=array_contains}], interface={result_value=[{name=array_contains, table_ref=array_contains}], id=[{name=id, table_ref=array_contains}]}, table_alias={array_contains=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYCONTAINSExistingWildcardQualifierV0Test() {
		final String query = "select array_contains.* from events as array_contains where array_contains.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_contains}}}, from={table={alias=array_contains, table=events}}, where={condition={left={column={name=active, table_ref=array_contains}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,60:73='array_contains',<350>,1:60]], *=[[@1,7:20='array_contains',<350>,1:7]]}}",
				"{query0={*=[[@3,22:22='*',<291>,1:22]]}}",
				"{def_query0={query_dictionary={*=[[@3,22:22='*',<291>,1:22]]}, table_dictionary={events={active=[[@9,60:73='array_contains',<350>,1:60]], *=[[@1,7:20='array_contains',<350>,1:7]]}}, filters=[{name=active, table_ref=array_contains}], interface={*=[{name=*, table_ref=array_contains}]}, table_alias={array_contains=events}}}");
	}

	@Test
	public void contextualKeywordARRAYUNIONAGGExistingContextualNamesV0Test() {
		final String query = "with array_union_agg as (select a.array_union_agg as array_union_agg, a.id from analytics.array_union_agg a where a.active = 1) select array_union_agg.array_union_agg as result_value, array_union_agg.id from array_union_agg as array_union_agg where array_union_agg.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_union_agg, table_ref=a}, alias=array_union_agg}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_union_agg}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_union_agg}}, query={select={1={column={name=array_union_agg, table_ref=array_union_agg}, alias=result_value}, 2={column={name=id, table_ref=array_union_agg}}}, from={table={alias=array_union_agg, table=array_union_agg}}, where={condition={left={column={name=id, table_ref=array_union_agg}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_union_agg={active=[[@20,114:114='a',<459>,1:114]], id=[[@11,70:70='a',<459>,1:70]], array_union_agg=[[@5,32:32='a',<459>,1:32]]}}",
				"{query0={id=[[@13,72:73='id',<459>,1:72], [@33,184:198='array_union_agg',<351>,1:184], [@41,249:263='array_union_agg',<351>,1:249]], array_union_agg=[[@9,53:67='array_union_agg',<351>,1:53], [@27,135:149='array_union_agg',<351>,1:135]]}, query1={result_value=[[@31,170:181='result_value',<459>,1:170]], id=[[@35,200:201='id',<459>,1:200]]}}",
				"{def_query1={context_list={array_union_agg=query0}, query_dictionary={result_value=[[@31,170:181='result_value',<459>,1:170]], id=[[@35,200:201='id',<459>,1:200]]}, def_query0={query_dictionary={id=[[@13,72:73='id',<459>,1:72], [@33,184:198='array_union_agg',<351>,1:184], [@41,249:263='array_union_agg',<351>,1:249]], array_union_agg=[[@9,53:67='array_union_agg',<351>,1:53], [@27,135:149='array_union_agg',<351>,1:135]]}, table_dictionary={analytics.array_union_agg={active=[[@20,114:114='a',<459>,1:114]], id=[[@11,70:70='a',<459>,1:70]], array_union_agg=[[@5,32:32='a',<459>,1:32]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], array_union_agg=[{name=array_union_agg, table_ref=a}]}, table_alias={a=analytics.array_union_agg}}, filters=[{name=id, table_ref=array_union_agg}], interface={result_value=[{name=array_union_agg, table_ref=array_union_agg}], id=[{name=id, table_ref=array_union_agg}]}, table_alias={array_union_agg=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYUNIONAGGExistingWildcardQualifierV0Test() {
		final String query = "select array_union_agg.* from events as array_union_agg where array_union_agg.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_union_agg}}}, from={table={alias=array_union_agg, table=events}}, where={condition={left={column={name=active, table_ref=array_union_agg}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,62:76='array_union_agg',<351>,1:62]], *=[[@1,7:21='array_union_agg',<351>,1:7]]}}",
				"{query0={*=[[@3,23:23='*',<291>,1:23]]}}",
				"{def_query0={query_dictionary={*=[[@3,23:23='*',<291>,1:23]]}, table_dictionary={events={active=[[@9,62:76='array_union_agg',<351>,1:62]], *=[[@1,7:21='array_union_agg',<351>,1:7]]}}, filters=[{name=active, table_ref=array_union_agg}], interface={*=[{name=*, table_ref=array_union_agg}]}, table_alias={array_union_agg=events}}}");
	}

	@Test
	public void contextualKeywordARRAYUNIQUEAGGExistingContextualNamesV0Test() {
		final String query = "with array_unique_agg as (select a.array_unique_agg as array_unique_agg, a.id from analytics.array_unique_agg a where a.active = 1) select array_unique_agg.array_unique_agg as result_value, array_unique_agg.id from array_unique_agg as array_unique_agg where array_unique_agg.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_unique_agg, table_ref=a}, alias=array_unique_agg}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_unique_agg}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_unique_agg}}, query={select={1={column={name=array_unique_agg, table_ref=array_unique_agg}, alias=result_value}, 2={column={name=id, table_ref=array_unique_agg}}}, from={table={alias=array_unique_agg, table=array_unique_agg}}, where={condition={left={column={name=id, table_ref=array_unique_agg}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_unique_agg={active=[[@20,118:118='a',<459>,1:118]], array_unique_agg=[[@5,33:33='a',<459>,1:33]], id=[[@11,73:73='a',<459>,1:73]]}}",
				"{query0={array_unique_agg=[[@9,55:70='array_unique_agg',<352>,1:55], [@27,139:154='array_unique_agg',<352>,1:139]], id=[[@13,75:76='id',<459>,1:75], [@33,190:205='array_unique_agg',<352>,1:190], [@41,258:273='array_unique_agg',<352>,1:258]]}, query1={result_value=[[@31,176:187='result_value',<459>,1:176]], id=[[@35,207:208='id',<459>,1:207]]}}",
				"{def_query1={context_list={array_unique_agg=query0}, query_dictionary={result_value=[[@31,176:187='result_value',<459>,1:176]], id=[[@35,207:208='id',<459>,1:207]]}, def_query0={query_dictionary={array_unique_agg=[[@9,55:70='array_unique_agg',<352>,1:55], [@27,139:154='array_unique_agg',<352>,1:139]], id=[[@13,75:76='id',<459>,1:75], [@33,190:205='array_unique_agg',<352>,1:190], [@41,258:273='array_unique_agg',<352>,1:258]]}, table_dictionary={analytics.array_unique_agg={active=[[@20,118:118='a',<459>,1:118]], array_unique_agg=[[@5,33:33='a',<459>,1:33]], id=[[@11,73:73='a',<459>,1:73]]}}, filters=[{name=active, table_ref=a}], interface={array_unique_agg=[{name=array_unique_agg, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.array_unique_agg}}, filters=[{name=id, table_ref=array_unique_agg}], interface={result_value=[{name=array_unique_agg, table_ref=array_unique_agg}], id=[{name=id, table_ref=array_unique_agg}]}, table_alias={array_unique_agg=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYUNIQUEAGGExistingWildcardQualifierV0Test() {
		final String query = "select array_unique_agg.* from events as array_unique_agg where array_unique_agg.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_unique_agg}}}, from={table={alias=array_unique_agg, table=events}}, where={condition={left={column={name=active, table_ref=array_unique_agg}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,64:79='array_unique_agg',<352>,1:64]], *=[[@1,7:22='array_unique_agg',<352>,1:7]]}}",
				"{query0={*=[[@3,24:24='*',<291>,1:24]]}}",
				"{def_query0={query_dictionary={*=[[@3,24:24='*',<291>,1:24]]}, table_dictionary={events={active=[[@9,64:79='array_unique_agg',<352>,1:64]], *=[[@1,7:22='array_unique_agg',<352>,1:7]]}}, filters=[{name=active, table_ref=array_unique_agg}], interface={*=[{name=*, table_ref=array_unique_agg}]}, table_alias={array_unique_agg=events}}}");
	}

	@Test
	public void contextualKeywordARRAYAPPENDExistingContextualNamesV0Test() {
		final String query = "with array_append as (select a.array_append as array_append, a.id from analytics.array_append a where a.active = 1) select array_append.array_append as result_value, array_append.id from array_append as array_append where array_append.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_append, table_ref=a}, alias=array_append}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_append}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_append}}, query={select={1={column={name=array_append, table_ref=array_append}, alias=result_value}, 2={column={name=id, table_ref=array_append}}}, from={table={alias=array_append, table=array_append}}, where={condition={left={column={name=id, table_ref=array_append}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_append={active=[[@20,102:102='a',<459>,1:102]], id=[[@11,61:61='a',<459>,1:61]], array_append=[[@5,29:29='a',<459>,1:29]]}}",
				"{query0={array_append=[[@9,47:58='array_append',<353>,1:47], [@27,123:134='array_append',<353>,1:123]], id=[[@13,63:64='id',<459>,1:63], [@33,166:177='array_append',<353>,1:166], [@41,222:233='array_append',<353>,1:222]]}, query1={result_value=[[@31,152:163='result_value',<459>,1:152]], id=[[@35,179:180='id',<459>,1:179]]}}",
				"{def_query1={context_list={array_append=query0}, query_dictionary={result_value=[[@31,152:163='result_value',<459>,1:152]], id=[[@35,179:180='id',<459>,1:179]]}, def_query0={query_dictionary={id=[[@13,63:64='id',<459>,1:63], [@33,166:177='array_append',<353>,1:166], [@41,222:233='array_append',<353>,1:222]], array_append=[[@9,47:58='array_append',<353>,1:47], [@27,123:134='array_append',<353>,1:123]]}, table_dictionary={analytics.array_append={active=[[@20,102:102='a',<459>,1:102]], id=[[@11,61:61='a',<459>,1:61]], array_append=[[@5,29:29='a',<459>,1:29]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], array_append=[{name=array_append, table_ref=a}]}, table_alias={a=analytics.array_append}}, filters=[{name=id, table_ref=array_append}], interface={result_value=[{name=array_append, table_ref=array_append}], id=[{name=id, table_ref=array_append}]}, table_alias={array_append=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYAPPENDExistingWildcardQualifierV0Test() {
		final String query = "select array_append.* from events as array_append where array_append.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_append}}}, from={table={alias=array_append, table=events}}, where={condition={left={column={name=active, table_ref=array_append}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,56:67='array_append',<353>,1:56]], *=[[@1,7:18='array_append',<353>,1:7]]}}",
				"{query0={*=[[@3,20:20='*',<291>,1:20]]}}",
				"{def_query0={query_dictionary={*=[[@3,20:20='*',<291>,1:20]]}, table_dictionary={events={active=[[@9,56:67='array_append',<353>,1:56]], *=[[@1,7:18='array_append',<353>,1:7]]}}, filters=[{name=active, table_ref=array_append}], interface={*=[{name=*, table_ref=array_append}]}, table_alias={array_append=events}}}");
	}

	@Test
	public void contextualKeywordARRAYPREPENDExistingContextualNamesV0Test() {
		final String query = "with array_prepend as (select a.array_prepend as array_prepend, a.id from analytics.array_prepend a where a.active = 1) select array_prepend.array_prepend as result_value, array_prepend.id from array_prepend as array_prepend where array_prepend.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_prepend, table_ref=a}, alias=array_prepend}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_prepend}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_prepend}}, query={select={1={column={name=array_prepend, table_ref=array_prepend}, alias=result_value}, 2={column={name=id, table_ref=array_prepend}}}, from={table={alias=array_prepend, table=array_prepend}}, where={condition={left={column={name=id, table_ref=array_prepend}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_prepend={array_prepend=[[@5,30:30='a',<459>,1:30]], active=[[@20,106:106='a',<459>,1:106]], id=[[@11,64:64='a',<459>,1:64]]}}",
				"{query0={array_prepend=[[@9,49:61='array_prepend',<354>,1:49], [@27,127:139='array_prepend',<354>,1:127]], id=[[@13,66:67='id',<459>,1:66], [@33,172:184='array_prepend',<354>,1:172], [@41,231:243='array_prepend',<354>,1:231]]}, query1={result_value=[[@31,158:169='result_value',<459>,1:158]], id=[[@35,186:187='id',<459>,1:186]]}}",
				"{def_query1={context_list={array_prepend=query0}, query_dictionary={result_value=[[@31,158:169='result_value',<459>,1:158]], id=[[@35,186:187='id',<459>,1:186]]}, def_query0={query_dictionary={array_prepend=[[@9,49:61='array_prepend',<354>,1:49], [@27,127:139='array_prepend',<354>,1:127]], id=[[@13,66:67='id',<459>,1:66], [@33,172:184='array_prepend',<354>,1:172], [@41,231:243='array_prepend',<354>,1:231]]}, table_dictionary={analytics.array_prepend={array_prepend=[[@5,30:30='a',<459>,1:30]], active=[[@20,106:106='a',<459>,1:106]], id=[[@11,64:64='a',<459>,1:64]]}}, filters=[{name=active, table_ref=a}], interface={array_prepend=[{name=array_prepend, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.array_prepend}}, filters=[{name=id, table_ref=array_prepend}], interface={result_value=[{name=array_prepend, table_ref=array_prepend}], id=[{name=id, table_ref=array_prepend}]}, table_alias={array_prepend=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYPREPENDExistingWildcardQualifierV0Test() {
		final String query = "select array_prepend.* from events as array_prepend where array_prepend.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_prepend}}}, from={table={alias=array_prepend, table=events}}, where={condition={left={column={name=active, table_ref=array_prepend}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,58:70='array_prepend',<354>,1:58]], *=[[@1,7:19='array_prepend',<354>,1:7]]}}",
				"{query0={*=[[@3,21:21='*',<291>,1:21]]}}",
				"{def_query0={query_dictionary={*=[[@3,21:21='*',<291>,1:21]]}, table_dictionary={events={active=[[@9,58:70='array_prepend',<354>,1:58]], *=[[@1,7:19='array_prepend',<354>,1:7]]}}, filters=[{name=active, table_ref=array_prepend}], interface={*=[{name=*, table_ref=array_prepend}]}, table_alias={array_prepend=events}}}");
	}

	@Test
	public void contextualKeywordARRAYINSERTExistingContextualNamesV0Test() {
		final String query = "with array_insert as (select a.array_insert as array_insert, a.id from analytics.array_insert a where a.active = 1) select array_insert.array_insert as result_value, array_insert.id from array_insert as array_insert where array_insert.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_insert, table_ref=a}, alias=array_insert}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_insert}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_insert}}, query={select={1={column={name=array_insert, table_ref=array_insert}, alias=result_value}, 2={column={name=id, table_ref=array_insert}}}, from={table={alias=array_insert, table=array_insert}}, where={condition={left={column={name=id, table_ref=array_insert}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_insert={array_insert=[[@5,29:29='a',<459>,1:29]], active=[[@20,102:102='a',<459>,1:102]], id=[[@11,61:61='a',<459>,1:61]]}}",
				"{query0={array_insert=[[@9,47:58='array_insert',<355>,1:47], [@27,123:134='array_insert',<355>,1:123]], id=[[@13,63:64='id',<459>,1:63], [@33,166:177='array_insert',<355>,1:166], [@41,222:233='array_insert',<355>,1:222]]}, query1={result_value=[[@31,152:163='result_value',<459>,1:152]], id=[[@35,179:180='id',<459>,1:179]]}}",
				"{def_query1={context_list={array_insert=query0}, query_dictionary={result_value=[[@31,152:163='result_value',<459>,1:152]], id=[[@35,179:180='id',<459>,1:179]]}, def_query0={query_dictionary={array_insert=[[@9,47:58='array_insert',<355>,1:47], [@27,123:134='array_insert',<355>,1:123]], id=[[@13,63:64='id',<459>,1:63], [@33,166:177='array_insert',<355>,1:166], [@41,222:233='array_insert',<355>,1:222]]}, table_dictionary={analytics.array_insert={array_insert=[[@5,29:29='a',<459>,1:29]], active=[[@20,102:102='a',<459>,1:102]], id=[[@11,61:61='a',<459>,1:61]]}}, filters=[{name=active, table_ref=a}], interface={array_insert=[{name=array_insert, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.array_insert}}, filters=[{name=id, table_ref=array_insert}], interface={result_value=[{name=array_insert, table_ref=array_insert}], id=[{name=id, table_ref=array_insert}]}, table_alias={array_insert=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYINSERTExistingWildcardQualifierV0Test() {
		final String query = "select array_insert.* from events as array_insert where array_insert.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_insert}}}, from={table={alias=array_insert, table=events}}, where={condition={left={column={name=active, table_ref=array_insert}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,56:67='array_insert',<355>,1:56]], *=[[@1,7:18='array_insert',<355>,1:7]]}}",
				"{query0={*=[[@3,20:20='*',<291>,1:20]]}}",
				"{def_query0={query_dictionary={*=[[@3,20:20='*',<291>,1:20]]}, table_dictionary={events={active=[[@9,56:67='array_insert',<355>,1:56]], *=[[@1,7:18='array_insert',<355>,1:7]]}}, filters=[{name=active, table_ref=array_insert}], interface={*=[{name=*, table_ref=array_insert}]}, table_alias={array_insert=events}}}");
	}

	@Test
	public void contextualKeywordARRAYREMOVEExistingContextualNamesV0Test() {
		final String query = "with array_remove as (select a.array_remove as array_remove, a.id from analytics.array_remove a where a.active = 1) select array_remove.array_remove as result_value, array_remove.id from array_remove as array_remove where array_remove.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_remove, table_ref=a}, alias=array_remove}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_remove}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_remove}}, query={select={1={column={name=array_remove, table_ref=array_remove}, alias=result_value}, 2={column={name=id, table_ref=array_remove}}}, from={table={alias=array_remove, table=array_remove}}, where={condition={left={column={name=id, table_ref=array_remove}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_remove={array_remove=[[@5,29:29='a',<459>,1:29]], active=[[@20,102:102='a',<459>,1:102]], id=[[@11,61:61='a',<459>,1:61]]}}",
				"{query0={array_remove=[[@9,47:58='array_remove',<356>,1:47], [@27,123:134='array_remove',<356>,1:123]], id=[[@13,63:64='id',<459>,1:63], [@33,166:177='array_remove',<356>,1:166], [@41,222:233='array_remove',<356>,1:222]]}, query1={result_value=[[@31,152:163='result_value',<459>,1:152]], id=[[@35,179:180='id',<459>,1:179]]}}",
				"{def_query1={context_list={array_remove=query0}, query_dictionary={result_value=[[@31,152:163='result_value',<459>,1:152]], id=[[@35,179:180='id',<459>,1:179]]}, def_query0={query_dictionary={array_remove=[[@9,47:58='array_remove',<356>,1:47], [@27,123:134='array_remove',<356>,1:123]], id=[[@13,63:64='id',<459>,1:63], [@33,166:177='array_remove',<356>,1:166], [@41,222:233='array_remove',<356>,1:222]]}, table_dictionary={analytics.array_remove={array_remove=[[@5,29:29='a',<459>,1:29]], active=[[@20,102:102='a',<459>,1:102]], id=[[@11,61:61='a',<459>,1:61]]}}, filters=[{name=active, table_ref=a}], interface={array_remove=[{name=array_remove, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.array_remove}}, filters=[{name=id, table_ref=array_remove}], interface={result_value=[{name=array_remove, table_ref=array_remove}], id=[{name=id, table_ref=array_remove}]}, table_alias={array_remove=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYREMOVEExistingWildcardQualifierV0Test() {
		final String query = "select array_remove.* from events as array_remove where array_remove.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_remove}}}, from={table={alias=array_remove, table=events}}, where={condition={left={column={name=active, table_ref=array_remove}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,56:67='array_remove',<356>,1:56]], *=[[@1,7:18='array_remove',<356>,1:7]]}}",
				"{query0={*=[[@3,20:20='*',<291>,1:20]]}}",
				"{def_query0={query_dictionary={*=[[@3,20:20='*',<291>,1:20]]}, table_dictionary={events={active=[[@9,56:67='array_remove',<356>,1:56]], *=[[@1,7:18='array_remove',<356>,1:7]]}}, filters=[{name=active, table_ref=array_remove}], interface={*=[{name=*, table_ref=array_remove}]}, table_alias={array_remove=events}}}");
	}

	@Test
	public void contextualKeywordARRAYREMOVEATExistingContextualNamesV0Test() {
		final String query = "with array_remove_at as (select a.array_remove_at as array_remove_at, a.id from analytics.array_remove_at a where a.active = 1) select array_remove_at.array_remove_at as result_value, array_remove_at.id from array_remove_at as array_remove_at where array_remove_at.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_remove_at, table_ref=a}, alias=array_remove_at}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_remove_at}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_remove_at}}, query={select={1={column={name=array_remove_at, table_ref=array_remove_at}, alias=result_value}, 2={column={name=id, table_ref=array_remove_at}}}, from={table={alias=array_remove_at, table=array_remove_at}}, where={condition={left={column={name=id, table_ref=array_remove_at}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_remove_at={array_remove_at=[[@5,32:32='a',<459>,1:32]], active=[[@20,114:114='a',<459>,1:114]], id=[[@11,70:70='a',<459>,1:70]]}}",
				"{query0={array_remove_at=[[@9,53:67='array_remove_at',<357>,1:53], [@27,135:149='array_remove_at',<357>,1:135]], id=[[@13,72:73='id',<459>,1:72], [@33,184:198='array_remove_at',<357>,1:184], [@41,249:263='array_remove_at',<357>,1:249]]}, query1={result_value=[[@31,170:181='result_value',<459>,1:170]], id=[[@35,200:201='id',<459>,1:200]]}}",
				"{def_query1={context_list={array_remove_at=query0}, query_dictionary={result_value=[[@31,170:181='result_value',<459>,1:170]], id=[[@35,200:201='id',<459>,1:200]]}, def_query0={query_dictionary={array_remove_at=[[@9,53:67='array_remove_at',<357>,1:53], [@27,135:149='array_remove_at',<357>,1:135]], id=[[@13,72:73='id',<459>,1:72], [@33,184:198='array_remove_at',<357>,1:184], [@41,249:263='array_remove_at',<357>,1:249]]}, table_dictionary={analytics.array_remove_at={array_remove_at=[[@5,32:32='a',<459>,1:32]], active=[[@20,114:114='a',<459>,1:114]], id=[[@11,70:70='a',<459>,1:70]]}}, filters=[{name=active, table_ref=a}], interface={array_remove_at=[{name=array_remove_at, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.array_remove_at}}, filters=[{name=id, table_ref=array_remove_at}], interface={result_value=[{name=array_remove_at, table_ref=array_remove_at}], id=[{name=id, table_ref=array_remove_at}]}, table_alias={array_remove_at=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYREMOVEATExistingWildcardQualifierV0Test() {
		final String query = "select array_remove_at.* from events as array_remove_at where array_remove_at.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_remove_at}}}, from={table={alias=array_remove_at, table=events}}, where={condition={left={column={name=active, table_ref=array_remove_at}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,62:76='array_remove_at',<357>,1:62]], *=[[@1,7:21='array_remove_at',<357>,1:7]]}}",
				"{query0={*=[[@3,23:23='*',<291>,1:23]]}}",
				"{def_query0={query_dictionary={*=[[@3,23:23='*',<291>,1:23]]}, table_dictionary={events={active=[[@9,62:76='array_remove_at',<357>,1:62]], *=[[@1,7:21='array_remove_at',<357>,1:7]]}}, filters=[{name=active, table_ref=array_remove_at}], interface={*=[{name=*, table_ref=array_remove_at}]}, table_alias={array_remove_at=events}}}");
	}

	@Test
	public void contextualKeywordARRAYCATExistingContextualNamesV0Test() {
		final String query = "with array_cat as (select a.array_cat as array_cat, a.id from analytics.array_cat a where a.active = 1) select array_cat.array_cat as result_value, array_cat.id from array_cat as array_cat where array_cat.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_cat, table_ref=a}, alias=array_cat}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_cat}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_cat}}, query={select={1={column={name=array_cat, table_ref=array_cat}, alias=result_value}, 2={column={name=id, table_ref=array_cat}}}, from={table={alias=array_cat, table=array_cat}}, where={condition={left={column={name=id, table_ref=array_cat}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_cat={array_cat=[[@5,26:26='a',<459>,1:26]], active=[[@20,90:90='a',<459>,1:90]], id=[[@11,52:52='a',<459>,1:52]]}}",
				"{query0={array_cat=[[@9,41:49='array_cat',<358>,1:41], [@27,111:119='array_cat',<358>,1:111]], id=[[@13,54:55='id',<459>,1:54], [@33,148:156='array_cat',<358>,1:148], [@41,195:203='array_cat',<358>,1:195]]}, query1={result_value=[[@31,134:145='result_value',<459>,1:134]], id=[[@35,158:159='id',<459>,1:158]]}}",
				"{def_query1={context_list={array_cat=query0}, query_dictionary={result_value=[[@31,134:145='result_value',<459>,1:134]], id=[[@35,158:159='id',<459>,1:158]]}, def_query0={query_dictionary={array_cat=[[@9,41:49='array_cat',<358>,1:41], [@27,111:119='array_cat',<358>,1:111]], id=[[@13,54:55='id',<459>,1:54], [@33,148:156='array_cat',<358>,1:148], [@41,195:203='array_cat',<358>,1:195]]}, table_dictionary={analytics.array_cat={array_cat=[[@5,26:26='a',<459>,1:26]], active=[[@20,90:90='a',<459>,1:90]], id=[[@11,52:52='a',<459>,1:52]]}}, filters=[{name=active, table_ref=a}], interface={array_cat=[{name=array_cat, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.array_cat}}, filters=[{name=id, table_ref=array_cat}], interface={result_value=[{name=array_cat, table_ref=array_cat}], id=[{name=id, table_ref=array_cat}]}, table_alias={array_cat=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYCATExistingWildcardQualifierV0Test() {
		final String query = "select array_cat.* from events as array_cat where array_cat.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_cat}}}, from={table={alias=array_cat, table=events}}, where={condition={left={column={name=active, table_ref=array_cat}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,50:58='array_cat',<358>,1:50]], *=[[@1,7:15='array_cat',<358>,1:7]]}}",
				"{query0={*=[[@3,17:17='*',<291>,1:17]]}}",
				"{def_query0={query_dictionary={*=[[@3,17:17='*',<291>,1:17]]}, table_dictionary={events={active=[[@9,50:58='array_cat',<358>,1:50]], *=[[@1,7:15='array_cat',<358>,1:7]]}}, filters=[{name=active, table_ref=array_cat}], interface={*=[{name=*, table_ref=array_cat}]}, table_alias={array_cat=events}}}");
	}

	@Test
	public void contextualKeywordARRAYEXCEPTExistingContextualNamesV0Test() {
		final String query = "with array_except as (select a.array_except as array_except, a.id from analytics.array_except a where a.active = 1) select array_except.array_except as result_value, array_except.id from array_except as array_except where array_except.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_except, table_ref=a}, alias=array_except}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_except}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_except}}, query={select={1={column={name=array_except, table_ref=array_except}, alias=result_value}, 2={column={name=id, table_ref=array_except}}}, from={table={alias=array_except, table=array_except}}, where={condition={left={column={name=id, table_ref=array_except}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_except={array_except=[[@5,29:29='a',<459>,1:29]], active=[[@20,102:102='a',<459>,1:102]], id=[[@11,61:61='a',<459>,1:61]]}}",
				"{query0={array_except=[[@9,47:58='array_except',<359>,1:47], [@27,123:134='array_except',<359>,1:123]], id=[[@13,63:64='id',<459>,1:63], [@33,166:177='array_except',<359>,1:166], [@41,222:233='array_except',<359>,1:222]]}, query1={result_value=[[@31,152:163='result_value',<459>,1:152]], id=[[@35,179:180='id',<459>,1:179]]}}",
				"{def_query1={context_list={array_except=query0}, query_dictionary={result_value=[[@31,152:163='result_value',<459>,1:152]], id=[[@35,179:180='id',<459>,1:179]]}, def_query0={query_dictionary={array_except=[[@9,47:58='array_except',<359>,1:47], [@27,123:134='array_except',<359>,1:123]], id=[[@13,63:64='id',<459>,1:63], [@33,166:177='array_except',<359>,1:166], [@41,222:233='array_except',<359>,1:222]]}, table_dictionary={analytics.array_except={array_except=[[@5,29:29='a',<459>,1:29]], active=[[@20,102:102='a',<459>,1:102]], id=[[@11,61:61='a',<459>,1:61]]}}, filters=[{name=active, table_ref=a}], interface={array_except=[{name=array_except, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.array_except}}, filters=[{name=id, table_ref=array_except}], interface={result_value=[{name=array_except, table_ref=array_except}], id=[{name=id, table_ref=array_except}]}, table_alias={array_except=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYEXCEPTExistingWildcardQualifierV0Test() {
		final String query = "select array_except.* from events as array_except where array_except.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_except}}}, from={table={alias=array_except, table=events}}, where={condition={left={column={name=active, table_ref=array_except}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,56:67='array_except',<359>,1:56]], *=[[@1,7:18='array_except',<359>,1:7]]}}",
				"{query0={*=[[@3,20:20='*',<291>,1:20]]}}",
				"{def_query0={query_dictionary={*=[[@3,20:20='*',<291>,1:20]]}, table_dictionary={events={active=[[@9,56:67='array_except',<359>,1:56]], *=[[@1,7:18='array_except',<359>,1:7]]}}, filters=[{name=active, table_ref=array_except}], interface={*=[{name=*, table_ref=array_except}]}, table_alias={array_except=events}}}");
	}

	@Test
	public void contextualKeywordARRAYINTERSECTIONExistingContextualNamesV0Test() {
		final String query = "with array_intersection as (select a.array_intersection as array_intersection, a.id from analytics.array_intersection a where a.active = 1) select array_intersection.array_intersection as result_value, array_intersection.id from array_intersection as array_intersection where array_intersection.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_intersection, table_ref=a}, alias=array_intersection}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_intersection}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_intersection}}, query={select={1={column={name=array_intersection, table_ref=array_intersection}, alias=result_value}, 2={column={name=id, table_ref=array_intersection}}}, from={table={alias=array_intersection, table=array_intersection}}, where={condition={left={column={name=id, table_ref=array_intersection}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_intersection={active=[[@20,126:126='a',<459>,1:126]], array_intersection=[[@5,35:35='a',<459>,1:35]], id=[[@11,79:79='a',<459>,1:79]]}}",
				"{query0={array_intersection=[[@9,59:76='array_intersection',<360>,1:59], [@27,147:164='array_intersection',<360>,1:147]], id=[[@13,81:82='id',<459>,1:81], [@33,202:219='array_intersection',<360>,1:202], [@41,276:293='array_intersection',<360>,1:276]]}, query1={result_value=[[@31,188:199='result_value',<459>,1:188]], id=[[@35,221:222='id',<459>,1:221]]}}",
				"{def_query1={context_list={array_intersection=query0}, query_dictionary={result_value=[[@31,188:199='result_value',<459>,1:188]], id=[[@35,221:222='id',<459>,1:221]]}, def_query0={query_dictionary={array_intersection=[[@9,59:76='array_intersection',<360>,1:59], [@27,147:164='array_intersection',<360>,1:147]], id=[[@13,81:82='id',<459>,1:81], [@33,202:219='array_intersection',<360>,1:202], [@41,276:293='array_intersection',<360>,1:276]]}, table_dictionary={analytics.array_intersection={active=[[@20,126:126='a',<459>,1:126]], array_intersection=[[@5,35:35='a',<459>,1:35]], id=[[@11,79:79='a',<459>,1:79]]}}, filters=[{name=active, table_ref=a}], interface={array_intersection=[{name=array_intersection, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.array_intersection}}, filters=[{name=id, table_ref=array_intersection}], interface={result_value=[{name=array_intersection, table_ref=array_intersection}], id=[{name=id, table_ref=array_intersection}]}, table_alias={array_intersection=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYINTERSECTIONExistingWildcardQualifierV0Test() {
		final String query = "select array_intersection.* from events as array_intersection where array_intersection.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_intersection}}}, from={table={alias=array_intersection, table=events}}, where={condition={left={column={name=active, table_ref=array_intersection}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,68:85='array_intersection',<360>,1:68]], *=[[@1,7:24='array_intersection',<360>,1:7]]}}",
				"{query0={*=[[@3,26:26='*',<291>,1:26]]}}",
				"{def_query0={query_dictionary={*=[[@3,26:26='*',<291>,1:26]]}, table_dictionary={events={active=[[@9,68:85='array_intersection',<360>,1:68]], *=[[@1,7:24='array_intersection',<360>,1:7]]}}, filters=[{name=active, table_ref=array_intersection}], interface={*=[{name=*, table_ref=array_intersection}]}, table_alias={array_intersection=events}}}");
	}

	@Test
	public void contextualKeywordARRAYSOVERLAPExistingContextualNamesV0Test() {
		final String query = "with arrays_overlap as (select a.arrays_overlap as arrays_overlap, a.id from analytics.arrays_overlap a where a.active = 1) select arrays_overlap.arrays_overlap as result_value, arrays_overlap.id from arrays_overlap as arrays_overlap where arrays_overlap.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=arrays_overlap, table_ref=a}, alias=arrays_overlap}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=arrays_overlap}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=arrays_overlap}}, query={select={1={column={name=arrays_overlap, table_ref=arrays_overlap}, alias=result_value}, 2={column={name=id, table_ref=arrays_overlap}}}, from={table={alias=arrays_overlap, table=arrays_overlap}}, where={condition={left={column={name=id, table_ref=arrays_overlap}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.arrays_overlap={active=[[@20,110:110='a',<459>,1:110]], id=[[@11,67:67='a',<459>,1:67]], arrays_overlap=[[@5,31:31='a',<459>,1:31]]}}",
				"{query0={id=[[@13,69:70='id',<459>,1:69], [@33,178:191='arrays_overlap',<361>,1:178], [@41,240:253='arrays_overlap',<361>,1:240]], arrays_overlap=[[@9,51:64='arrays_overlap',<361>,1:51], [@27,131:144='arrays_overlap',<361>,1:131]]}, query1={result_value=[[@31,164:175='result_value',<459>,1:164]], id=[[@35,193:194='id',<459>,1:193]]}}",
				"{def_query1={context_list={arrays_overlap=query0}, query_dictionary={result_value=[[@31,164:175='result_value',<459>,1:164]], id=[[@35,193:194='id',<459>,1:193]]}, def_query0={query_dictionary={id=[[@13,69:70='id',<459>,1:69], [@33,178:191='arrays_overlap',<361>,1:178], [@41,240:253='arrays_overlap',<361>,1:240]], arrays_overlap=[[@9,51:64='arrays_overlap',<361>,1:51], [@27,131:144='arrays_overlap',<361>,1:131]]}, table_dictionary={analytics.arrays_overlap={active=[[@20,110:110='a',<459>,1:110]], id=[[@11,67:67='a',<459>,1:67]], arrays_overlap=[[@5,31:31='a',<459>,1:31]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], arrays_overlap=[{name=arrays_overlap, table_ref=a}]}, table_alias={a=analytics.arrays_overlap}}, filters=[{name=id, table_ref=arrays_overlap}], interface={result_value=[{name=arrays_overlap, table_ref=arrays_overlap}], id=[{name=id, table_ref=arrays_overlap}]}, table_alias={arrays_overlap=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYSOVERLAPExistingWildcardQualifierV0Test() {
		final String query = "select arrays_overlap.* from events as arrays_overlap where arrays_overlap.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=arrays_overlap}}}, from={table={alias=arrays_overlap, table=events}}, where={condition={left={column={name=active, table_ref=arrays_overlap}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,60:73='arrays_overlap',<361>,1:60]], *=[[@1,7:20='arrays_overlap',<361>,1:7]]}}",
				"{query0={*=[[@3,22:22='*',<291>,1:22]]}}",
				"{def_query0={query_dictionary={*=[[@3,22:22='*',<291>,1:22]]}, table_dictionary={events={active=[[@9,60:73='arrays_overlap',<361>,1:60]], *=[[@1,7:20='arrays_overlap',<361>,1:7]]}}, filters=[{name=active, table_ref=arrays_overlap}], interface={*=[{name=*, table_ref=arrays_overlap}]}, table_alias={arrays_overlap=events}}}");
	}

	@Test
	public void contextualKeywordARRAYTOSTRINGExistingContextualNamesV0Test() {
		final String query = "with array_to_string as (select a.array_to_string as array_to_string, a.id from analytics.array_to_string a where a.active = 1) select array_to_string.array_to_string as result_value, array_to_string.id from array_to_string as array_to_string where array_to_string.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_to_string, table_ref=a}, alias=array_to_string}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_to_string}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_to_string}}, query={select={1={column={name=array_to_string, table_ref=array_to_string}, alias=result_value}, 2={column={name=id, table_ref=array_to_string}}}, from={table={alias=array_to_string, table=array_to_string}}, where={condition={left={column={name=id, table_ref=array_to_string}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_to_string={active=[[@20,114:114='a',<459>,1:114]], id=[[@11,70:70='a',<459>,1:70]], array_to_string=[[@5,32:32='a',<459>,1:32]]}}",
				"{query0={array_to_string=[[@9,53:67='array_to_string',<362>,1:53], [@27,135:149='array_to_string',<362>,1:135]], id=[[@13,72:73='id',<459>,1:72], [@33,184:198='array_to_string',<362>,1:184], [@41,249:263='array_to_string',<362>,1:249]]}, query1={result_value=[[@31,170:181='result_value',<459>,1:170]], id=[[@35,200:201='id',<459>,1:200]]}}",
				"{def_query1={context_list={array_to_string=query0}, query_dictionary={result_value=[[@31,170:181='result_value',<459>,1:170]], id=[[@35,200:201='id',<459>,1:200]]}, def_query0={query_dictionary={id=[[@13,72:73='id',<459>,1:72], [@33,184:198='array_to_string',<362>,1:184], [@41,249:263='array_to_string',<362>,1:249]], array_to_string=[[@9,53:67='array_to_string',<362>,1:53], [@27,135:149='array_to_string',<362>,1:135]]}, table_dictionary={analytics.array_to_string={active=[[@20,114:114='a',<459>,1:114]], id=[[@11,70:70='a',<459>,1:70]], array_to_string=[[@5,32:32='a',<459>,1:32]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], array_to_string=[{name=array_to_string, table_ref=a}]}, table_alias={a=analytics.array_to_string}}, filters=[{name=id, table_ref=array_to_string}], interface={result_value=[{name=array_to_string, table_ref=array_to_string}], id=[{name=id, table_ref=array_to_string}]}, table_alias={array_to_string=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYTOSTRINGExistingWildcardQualifierV0Test() {
		final String query = "select array_to_string.* from events as array_to_string where array_to_string.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_to_string}}}, from={table={alias=array_to_string, table=events}}, where={condition={left={column={name=active, table_ref=array_to_string}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,62:76='array_to_string',<362>,1:62]], *=[[@1,7:21='array_to_string',<362>,1:7]]}}",
				"{query0={*=[[@3,23:23='*',<291>,1:23]]}}",
				"{def_query0={query_dictionary={*=[[@3,23:23='*',<291>,1:23]]}, table_dictionary={events={active=[[@9,62:76='array_to_string',<362>,1:62]], *=[[@1,7:21='array_to_string',<362>,1:7]]}}, filters=[{name=active, table_ref=array_to_string}], interface={*=[{name=*, table_ref=array_to_string}]}, table_alias={array_to_string=events}}}");
	}

	@Test
	public void contextualKeywordARRAYDISTINCTExistingContextualNamesV0Test() {
		final String query = "with array_distinct as (select a.array_distinct as array_distinct, a.id from analytics.array_distinct a where a.active = 1) select array_distinct.array_distinct as result_value, array_distinct.id from array_distinct as array_distinct where array_distinct.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_distinct, table_ref=a}, alias=array_distinct}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_distinct}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_distinct}}, query={select={1={column={name=array_distinct, table_ref=array_distinct}, alias=result_value}, 2={column={name=id, table_ref=array_distinct}}}, from={table={alias=array_distinct, table=array_distinct}}, where={condition={left={column={name=id, table_ref=array_distinct}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_distinct={array_distinct=[[@5,31:31='a',<459>,1:31]], active=[[@20,110:110='a',<459>,1:110]], id=[[@11,67:67='a',<459>,1:67]]}}",
				"{query0={array_distinct=[[@9,51:64='array_distinct',<363>,1:51], [@27,131:144='array_distinct',<363>,1:131]], id=[[@13,69:70='id',<459>,1:69], [@33,178:191='array_distinct',<363>,1:178], [@41,240:253='array_distinct',<363>,1:240]]}, query1={result_value=[[@31,164:175='result_value',<459>,1:164]], id=[[@35,193:194='id',<459>,1:193]]}}",
				"{def_query1={context_list={array_distinct=query0}, query_dictionary={result_value=[[@31,164:175='result_value',<459>,1:164]], id=[[@35,193:194='id',<459>,1:193]]}, def_query0={query_dictionary={array_distinct=[[@9,51:64='array_distinct',<363>,1:51], [@27,131:144='array_distinct',<363>,1:131]], id=[[@13,69:70='id',<459>,1:69], [@33,178:191='array_distinct',<363>,1:178], [@41,240:253='array_distinct',<363>,1:240]]}, table_dictionary={analytics.array_distinct={array_distinct=[[@5,31:31='a',<459>,1:31]], active=[[@20,110:110='a',<459>,1:110]], id=[[@11,67:67='a',<459>,1:67]]}}, filters=[{name=active, table_ref=a}], interface={array_distinct=[{name=array_distinct, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.array_distinct}}, filters=[{name=id, table_ref=array_distinct}], interface={result_value=[{name=array_distinct, table_ref=array_distinct}], id=[{name=id, table_ref=array_distinct}]}, table_alias={array_distinct=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYDISTINCTExistingWildcardQualifierV0Test() {
		final String query = "select array_distinct.* from events as array_distinct where array_distinct.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_distinct}}}, from={table={alias=array_distinct, table=events}}, where={condition={left={column={name=active, table_ref=array_distinct}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,60:73='array_distinct',<363>,1:60]], *=[[@1,7:20='array_distinct',<363>,1:7]]}}",
				"{query0={*=[[@3,22:22='*',<291>,1:22]]}}",
				"{def_query0={query_dictionary={*=[[@3,22:22='*',<291>,1:22]]}, table_dictionary={events={active=[[@9,60:73='array_distinct',<363>,1:60]], *=[[@1,7:20='array_distinct',<363>,1:7]]}}, filters=[{name=active, table_ref=array_distinct}], interface={*=[{name=*, table_ref=array_distinct}]}, table_alias={array_distinct=events}}}");
	}

	@Test
	public void contextualKeywordARRAYCOMPACTExistingContextualNamesV0Test() {
		final String query = "with array_compact as (select a.array_compact as array_compact, a.id from analytics.array_compact a where a.active = 1) select array_compact.array_compact as result_value, array_compact.id from array_compact as array_compact where array_compact.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_compact, table_ref=a}, alias=array_compact}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_compact}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_compact}}, query={select={1={column={name=array_compact, table_ref=array_compact}, alias=result_value}, 2={column={name=id, table_ref=array_compact}}}, from={table={alias=array_compact, table=array_compact}}, where={condition={left={column={name=id, table_ref=array_compact}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_compact={array_compact=[[@5,30:30='a',<459>,1:30]], active=[[@20,106:106='a',<459>,1:106]], id=[[@11,64:64='a',<459>,1:64]]}}",
				"{query0={array_compact=[[@9,49:61='array_compact',<364>,1:49], [@27,127:139='array_compact',<364>,1:127]], id=[[@13,66:67='id',<459>,1:66], [@33,172:184='array_compact',<364>,1:172], [@41,231:243='array_compact',<364>,1:231]]}, query1={result_value=[[@31,158:169='result_value',<459>,1:158]], id=[[@35,186:187='id',<459>,1:186]]}}",
				"{def_query1={context_list={array_compact=query0}, query_dictionary={result_value=[[@31,158:169='result_value',<459>,1:158]], id=[[@35,186:187='id',<459>,1:186]]}, def_query0={query_dictionary={array_compact=[[@9,49:61='array_compact',<364>,1:49], [@27,127:139='array_compact',<364>,1:127]], id=[[@13,66:67='id',<459>,1:66], [@33,172:184='array_compact',<364>,1:172], [@41,231:243='array_compact',<364>,1:231]]}, table_dictionary={analytics.array_compact={array_compact=[[@5,30:30='a',<459>,1:30]], active=[[@20,106:106='a',<459>,1:106]], id=[[@11,64:64='a',<459>,1:64]]}}, filters=[{name=active, table_ref=a}], interface={array_compact=[{name=array_compact, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.array_compact}}, filters=[{name=id, table_ref=array_compact}], interface={result_value=[{name=array_compact, table_ref=array_compact}], id=[{name=id, table_ref=array_compact}]}, table_alias={array_compact=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYCOMPACTExistingWildcardQualifierV0Test() {
		final String query = "select array_compact.* from events as array_compact where array_compact.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_compact}}}, from={table={alias=array_compact, table=events}}, where={condition={left={column={name=active, table_ref=array_compact}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,58:70='array_compact',<364>,1:58]], *=[[@1,7:19='array_compact',<364>,1:7]]}}",
				"{query0={*=[[@3,21:21='*',<291>,1:21]]}}",
				"{def_query0={query_dictionary={*=[[@3,21:21='*',<291>,1:21]]}, table_dictionary={events={active=[[@9,58:70='array_compact',<364>,1:58]], *=[[@1,7:19='array_compact',<364>,1:7]]}}, filters=[{name=active, table_ref=array_compact}], interface={*=[{name=*, table_ref=array_compact}]}, table_alias={array_compact=events}}}");
	}

	@Test
	public void contextualKeywordARRAYFLATTENExistingContextualNamesV0Test() {
		final String query = "with array_flatten as (select a.array_flatten as array_flatten, a.id from analytics.array_flatten a where a.active = 1) select array_flatten.array_flatten as result_value, array_flatten.id from array_flatten as array_flatten where array_flatten.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_flatten, table_ref=a}, alias=array_flatten}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_flatten}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_flatten}}, query={select={1={column={name=array_flatten, table_ref=array_flatten}, alias=result_value}, 2={column={name=id, table_ref=array_flatten}}}, from={table={alias=array_flatten, table=array_flatten}}, where={condition={left={column={name=id, table_ref=array_flatten}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_flatten={array_flatten=[[@5,30:30='a',<459>,1:30]], active=[[@20,106:106='a',<459>,1:106]], id=[[@11,64:64='a',<459>,1:64]]}}",
				"{query0={array_flatten=[[@9,49:61='array_flatten',<365>,1:49], [@27,127:139='array_flatten',<365>,1:127]], id=[[@13,66:67='id',<459>,1:66], [@33,172:184='array_flatten',<365>,1:172], [@41,231:243='array_flatten',<365>,1:231]]}, query1={result_value=[[@31,158:169='result_value',<459>,1:158]], id=[[@35,186:187='id',<459>,1:186]]}}",
				"{def_query1={context_list={array_flatten=query0}, query_dictionary={result_value=[[@31,158:169='result_value',<459>,1:158]], id=[[@35,186:187='id',<459>,1:186]]}, def_query0={query_dictionary={array_flatten=[[@9,49:61='array_flatten',<365>,1:49], [@27,127:139='array_flatten',<365>,1:127]], id=[[@13,66:67='id',<459>,1:66], [@33,172:184='array_flatten',<365>,1:172], [@41,231:243='array_flatten',<365>,1:231]]}, table_dictionary={analytics.array_flatten={array_flatten=[[@5,30:30='a',<459>,1:30]], active=[[@20,106:106='a',<459>,1:106]], id=[[@11,64:64='a',<459>,1:64]]}}, filters=[{name=active, table_ref=a}], interface={array_flatten=[{name=array_flatten, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.array_flatten}}, filters=[{name=id, table_ref=array_flatten}], interface={result_value=[{name=array_flatten, table_ref=array_flatten}], id=[{name=id, table_ref=array_flatten}]}, table_alias={array_flatten=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYFLATTENExistingWildcardQualifierV0Test() {
		final String query = "select array_flatten.* from events as array_flatten where array_flatten.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_flatten}}}, from={table={alias=array_flatten, table=events}}, where={condition={left={column={name=active, table_ref=array_flatten}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,58:70='array_flatten',<365>,1:58]], *=[[@1,7:19='array_flatten',<365>,1:7]]}}",
				"{query0={*=[[@3,21:21='*',<291>,1:21]]}}",
				"{def_query0={query_dictionary={*=[[@3,21:21='*',<291>,1:21]]}, table_dictionary={events={active=[[@9,58:70='array_flatten',<365>,1:58]], *=[[@1,7:19='array_flatten',<365>,1:7]]}}, filters=[{name=active, table_ref=array_flatten}], interface={*=[{name=*, table_ref=array_flatten}]}, table_alias={array_flatten=events}}}");
	}

	@Test
	public void contextualKeywordARRAYGENERATERANGEExistingContextualNamesV0Test() {
		final String query = "with array_generate_range as (select a.array_generate_range as array_generate_range, a.id from analytics.array_generate_range a where a.active = 1) select array_generate_range.array_generate_range as result_value, array_generate_range.id from array_generate_range as array_generate_range where array_generate_range.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_generate_range, table_ref=a}, alias=array_generate_range}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_generate_range}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_generate_range}}, query={select={1={column={name=array_generate_range, table_ref=array_generate_range}, alias=result_value}, 2={column={name=id, table_ref=array_generate_range}}}, from={table={alias=array_generate_range, table=array_generate_range}}, where={condition={left={column={name=id, table_ref=array_generate_range}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_generate_range={array_generate_range=[[@5,37:37='a',<459>,1:37]], active=[[@20,134:134='a',<459>,1:134]], id=[[@11,85:85='a',<459>,1:85]]}}",
				"{query0={array_generate_range=[[@9,63:82='array_generate_range',<366>,1:63], [@27,155:174='array_generate_range',<366>,1:155]], id=[[@13,87:88='id',<459>,1:87], [@33,214:233='array_generate_range',<366>,1:214], [@41,294:313='array_generate_range',<366>,1:294]]}, query1={result_value=[[@31,200:211='result_value',<459>,1:200]], id=[[@35,235:236='id',<459>,1:235]]}}",
				"{def_query1={context_list={array_generate_range=query0}, query_dictionary={result_value=[[@31,200:211='result_value',<459>,1:200]], id=[[@35,235:236='id',<459>,1:235]]}, def_query0={query_dictionary={array_generate_range=[[@9,63:82='array_generate_range',<366>,1:63], [@27,155:174='array_generate_range',<366>,1:155]], id=[[@13,87:88='id',<459>,1:87], [@33,214:233='array_generate_range',<366>,1:214], [@41,294:313='array_generate_range',<366>,1:294]]}, table_dictionary={analytics.array_generate_range={array_generate_range=[[@5,37:37='a',<459>,1:37]], active=[[@20,134:134='a',<459>,1:134]], id=[[@11,85:85='a',<459>,1:85]]}}, filters=[{name=active, table_ref=a}], interface={array_generate_range=[{name=array_generate_range, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.array_generate_range}}, filters=[{name=id, table_ref=array_generate_range}], interface={result_value=[{name=array_generate_range, table_ref=array_generate_range}], id=[{name=id, table_ref=array_generate_range}]}, table_alias={array_generate_range=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYGENERATERANGEExistingWildcardQualifierV0Test() {
		final String query = "select array_generate_range.* from events as array_generate_range where array_generate_range.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_generate_range}}}, from={table={alias=array_generate_range, table=events}}, where={condition={left={column={name=active, table_ref=array_generate_range}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,72:91='array_generate_range',<366>,1:72]], *=[[@1,7:26='array_generate_range',<366>,1:7]]}}",
				"{query0={*=[[@3,28:28='*',<291>,1:28]]}}",
				"{def_query0={query_dictionary={*=[[@3,28:28='*',<291>,1:28]]}, table_dictionary={events={active=[[@9,72:91='array_generate_range',<366>,1:72]], *=[[@1,7:26='array_generate_range',<366>,1:7]]}}, filters=[{name=active, table_ref=array_generate_range}], interface={*=[{name=*, table_ref=array_generate_range}]}, table_alias={array_generate_range=events}}}");
	}

	@Test
	public void contextualKeywordARRAYPOSITIONExistingContextualNamesV0Test() {
		final String query = "with array_position as (select a.array_position as array_position, a.id from analytics.array_position a where a.active = 1) select array_position.array_position as result_value, array_position.id from array_position as array_position where array_position.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_position, table_ref=a}, alias=array_position}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_position}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_position}}, query={select={1={column={name=array_position, table_ref=array_position}, alias=result_value}, 2={column={name=id, table_ref=array_position}}}, from={table={alias=array_position, table=array_position}}, where={condition={left={column={name=id, table_ref=array_position}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_position={active=[[@20,110:110='a',<459>,1:110]], id=[[@11,67:67='a',<459>,1:67]], array_position=[[@5,31:31='a',<459>,1:31]]}}",
				"{query0={array_position=[[@9,51:64='array_position',<367>,1:51], [@27,131:144='array_position',<367>,1:131]], id=[[@13,69:70='id',<459>,1:69], [@33,178:191='array_position',<367>,1:178], [@41,240:253='array_position',<367>,1:240]]}, query1={result_value=[[@31,164:175='result_value',<459>,1:164]], id=[[@35,193:194='id',<459>,1:193]]}}",
				"{def_query1={context_list={array_position=query0}, query_dictionary={result_value=[[@31,164:175='result_value',<459>,1:164]], id=[[@35,193:194='id',<459>,1:193]]}, def_query0={query_dictionary={id=[[@13,69:70='id',<459>,1:69], [@33,178:191='array_position',<367>,1:178], [@41,240:253='array_position',<367>,1:240]], array_position=[[@9,51:64='array_position',<367>,1:51], [@27,131:144='array_position',<367>,1:131]]}, table_dictionary={analytics.array_position={active=[[@20,110:110='a',<459>,1:110]], id=[[@11,67:67='a',<459>,1:67]], array_position=[[@5,31:31='a',<459>,1:31]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], array_position=[{name=array_position, table_ref=a}]}, table_alias={a=analytics.array_position}}, filters=[{name=id, table_ref=array_position}], interface={result_value=[{name=array_position, table_ref=array_position}], id=[{name=id, table_ref=array_position}]}, table_alias={array_position=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYPOSITIONExistingWildcardQualifierV0Test() {
		final String query = "select array_position.* from events as array_position where array_position.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_position}}}, from={table={alias=array_position, table=events}}, where={condition={left={column={name=active, table_ref=array_position}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,60:73='array_position',<367>,1:60]], *=[[@1,7:20='array_position',<367>,1:7]]}}",
				"{query0={*=[[@3,22:22='*',<291>,1:22]]}}",
				"{def_query0={query_dictionary={*=[[@3,22:22='*',<291>,1:22]]}, table_dictionary={events={active=[[@9,60:73='array_position',<367>,1:60]], *=[[@1,7:20='array_position',<367>,1:7]]}}, filters=[{name=active, table_ref=array_position}], interface={*=[{name=*, table_ref=array_position}]}, table_alias={array_position=events}}}");
	}

	@Test
	public void contextualKeywordARRAYSIZEExistingContextualNamesV0Test() {
		final String query = "with array_size as (select a.array_size as array_size, a.id from analytics.array_size a where a.active = 1) select array_size.array_size as result_value, array_size.id from array_size as array_size where array_size.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_size, table_ref=a}, alias=array_size}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_size}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_size}}, query={select={1={column={name=array_size, table_ref=array_size}, alias=result_value}, 2={column={name=id, table_ref=array_size}}}, from={table={alias=array_size, table=array_size}}, where={condition={left={column={name=id, table_ref=array_size}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_size={active=[[@20,94:94='a',<459>,1:94]], id=[[@11,55:55='a',<459>,1:55]], array_size=[[@5,27:27='a',<459>,1:27]]}}",
				"{query0={array_size=[[@9,43:52='array_size',<368>,1:43], [@27,115:124='array_size',<368>,1:115]], id=[[@13,57:58='id',<459>,1:57], [@33,154:163='array_size',<368>,1:154], [@41,204:213='array_size',<368>,1:204]]}, query1={result_value=[[@31,140:151='result_value',<459>,1:140]], id=[[@35,165:166='id',<459>,1:165]]}}",
				"{def_query1={context_list={array_size=query0}, query_dictionary={result_value=[[@31,140:151='result_value',<459>,1:140]], id=[[@35,165:166='id',<459>,1:165]]}, def_query0={query_dictionary={id=[[@13,57:58='id',<459>,1:57], [@33,154:163='array_size',<368>,1:154], [@41,204:213='array_size',<368>,1:204]], array_size=[[@9,43:52='array_size',<368>,1:43], [@27,115:124='array_size',<368>,1:115]]}, table_dictionary={analytics.array_size={active=[[@20,94:94='a',<459>,1:94]], id=[[@11,55:55='a',<459>,1:55]], array_size=[[@5,27:27='a',<459>,1:27]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], array_size=[{name=array_size, table_ref=a}]}, table_alias={a=analytics.array_size}}, filters=[{name=id, table_ref=array_size}], interface={result_value=[{name=array_size, table_ref=array_size}], id=[{name=id, table_ref=array_size}]}, table_alias={array_size=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYSIZEExistingWildcardQualifierV0Test() {
		final String query = "select array_size.* from events as array_size where array_size.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_size}}}, from={table={alias=array_size, table=events}}, where={condition={left={column={name=active, table_ref=array_size}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,52:61='array_size',<368>,1:52]], *=[[@1,7:16='array_size',<368>,1:7]]}}",
				"{query0={*=[[@3,18:18='*',<291>,1:18]]}}",
				"{def_query0={query_dictionary={*=[[@3,18:18='*',<291>,1:18]]}, table_dictionary={events={active=[[@9,52:61='array_size',<368>,1:52]], *=[[@1,7:16='array_size',<368>,1:7]]}}, filters=[{name=active, table_ref=array_size}], interface={*=[{name=*, table_ref=array_size}]}, table_alias={array_size=events}}}");
	}

	@Test
	public void contextualKeywordARRAYSLICEExistingContextualNamesV0Test() {
		final String query = "with array_slice as (select a.array_slice as array_slice, a.id from analytics.array_slice a where a.active = 1) select array_slice.array_slice as result_value, array_slice.id from array_slice as array_slice where array_slice.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_slice, table_ref=a}, alias=array_slice}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_slice}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_slice}}, query={select={1={column={name=array_slice, table_ref=array_slice}, alias=result_value}, 2={column={name=id, table_ref=array_slice}}}, from={table={alias=array_slice, table=array_slice}}, where={condition={left={column={name=id, table_ref=array_slice}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_slice={array_slice=[[@5,28:28='a',<459>,1:28]], active=[[@20,98:98='a',<459>,1:98]], id=[[@11,58:58='a',<459>,1:58]]}}",
				"{query0={array_slice=[[@9,45:55='array_slice',<369>,1:45], [@27,119:129='array_slice',<369>,1:119]], id=[[@13,60:61='id',<459>,1:60], [@33,160:170='array_slice',<369>,1:160], [@41,213:223='array_slice',<369>,1:213]]}, query1={result_value=[[@31,146:157='result_value',<459>,1:146]], id=[[@35,172:173='id',<459>,1:172]]}}",
				"{def_query1={context_list={array_slice=query0}, query_dictionary={result_value=[[@31,146:157='result_value',<459>,1:146]], id=[[@35,172:173='id',<459>,1:172]]}, def_query0={query_dictionary={array_slice=[[@9,45:55='array_slice',<369>,1:45], [@27,119:129='array_slice',<369>,1:119]], id=[[@13,60:61='id',<459>,1:60], [@33,160:170='array_slice',<369>,1:160], [@41,213:223='array_slice',<369>,1:213]]}, table_dictionary={analytics.array_slice={array_slice=[[@5,28:28='a',<459>,1:28]], active=[[@20,98:98='a',<459>,1:98]], id=[[@11,58:58='a',<459>,1:58]]}}, filters=[{name=active, table_ref=a}], interface={array_slice=[{name=array_slice, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.array_slice}}, filters=[{name=id, table_ref=array_slice}], interface={result_value=[{name=array_slice, table_ref=array_slice}], id=[{name=id, table_ref=array_slice}]}, table_alias={array_slice=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYSLICEExistingWildcardQualifierV0Test() {
		final String query = "select array_slice.* from events as array_slice where array_slice.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_slice}}}, from={table={alias=array_slice, table=events}}, where={condition={left={column={name=active, table_ref=array_slice}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,54:64='array_slice',<369>,1:54]], *=[[@1,7:17='array_slice',<369>,1:7]]}}",
				"{query0={*=[[@3,19:19='*',<291>,1:19]]}}",
				"{def_query0={query_dictionary={*=[[@3,19:19='*',<291>,1:19]]}, table_dictionary={events={active=[[@9,54:64='array_slice',<369>,1:54]], *=[[@1,7:17='array_slice',<369>,1:7]]}}, filters=[{name=active, table_ref=array_slice}], interface={*=[{name=*, table_ref=array_slice}]}, table_alias={array_slice=events}}}");
	}

	@Test
	public void contextualKeywordARRAYSORTExistingContextualNamesV0Test() {
		final String query = "with array_sort as (select a.array_sort as array_sort, a.id from analytics.array_sort a where a.active = 1) select array_sort.array_sort as result_value, array_sort.id from array_sort as array_sort where array_sort.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_sort, table_ref=a}, alias=array_sort}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_sort}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_sort}}, query={select={1={column={name=array_sort, table_ref=array_sort}, alias=result_value}, 2={column={name=id, table_ref=array_sort}}}, from={table={alias=array_sort, table=array_sort}}, where={condition={left={column={name=id, table_ref=array_sort}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_sort={active=[[@20,94:94='a',<459>,1:94]], id=[[@11,55:55='a',<459>,1:55]], array_sort=[[@5,27:27='a',<459>,1:27]]}}",
				"{query0={array_sort=[[@9,43:52='array_sort',<370>,1:43], [@27,115:124='array_sort',<370>,1:115]], id=[[@13,57:58='id',<459>,1:57], [@33,154:163='array_sort',<370>,1:154], [@41,204:213='array_sort',<370>,1:204]]}, query1={result_value=[[@31,140:151='result_value',<459>,1:140]], id=[[@35,165:166='id',<459>,1:165]]}}",
				"{def_query1={context_list={array_sort=query0}, query_dictionary={result_value=[[@31,140:151='result_value',<459>,1:140]], id=[[@35,165:166='id',<459>,1:165]]}, def_query0={query_dictionary={id=[[@13,57:58='id',<459>,1:57], [@33,154:163='array_sort',<370>,1:154], [@41,204:213='array_sort',<370>,1:204]], array_sort=[[@9,43:52='array_sort',<370>,1:43], [@27,115:124='array_sort',<370>,1:115]]}, table_dictionary={analytics.array_sort={active=[[@20,94:94='a',<459>,1:94]], id=[[@11,55:55='a',<459>,1:55]], array_sort=[[@5,27:27='a',<459>,1:27]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], array_sort=[{name=array_sort, table_ref=a}]}, table_alias={a=analytics.array_sort}}, filters=[{name=id, table_ref=array_sort}], interface={result_value=[{name=array_sort, table_ref=array_sort}], id=[{name=id, table_ref=array_sort}]}, table_alias={array_sort=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYSORTExistingWildcardQualifierV0Test() {
		final String query = "select array_sort.* from events as array_sort where array_sort.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_sort}}}, from={table={alias=array_sort, table=events}}, where={condition={left={column={name=active, table_ref=array_sort}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,52:61='array_sort',<370>,1:52]], *=[[@1,7:16='array_sort',<370>,1:7]]}}",
				"{query0={*=[[@3,18:18='*',<291>,1:18]]}}",
				"{def_query0={query_dictionary={*=[[@3,18:18='*',<291>,1:18]]}, table_dictionary={events={active=[[@9,52:61='array_sort',<370>,1:52]], *=[[@1,7:16='array_sort',<370>,1:7]]}}, filters=[{name=active, table_ref=array_sort}], interface={*=[{name=*, table_ref=array_sort}]}, table_alias={array_sort=events}}}");
	}

	@Test
	public void contextualKeywordARRAYMAXExistingContextualNamesV0Test() {
		final String query = "with array_max as (select a.array_max as array_max, a.id from analytics.array_max a where a.active = 1) select array_max.array_max as result_value, array_max.id from array_max as array_max where array_max.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_max, table_ref=a}, alias=array_max}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_max}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_max}}, query={select={1={column={name=array_max, table_ref=array_max}, alias=result_value}, 2={column={name=id, table_ref=array_max}}}, from={table={alias=array_max, table=array_max}}, where={condition={left={column={name=id, table_ref=array_max}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_max={array_max=[[@5,26:26='a',<459>,1:26]], active=[[@20,90:90='a',<459>,1:90]], id=[[@11,52:52='a',<459>,1:52]]}}",
				"{query0={array_max=[[@9,41:49='array_max',<371>,1:41], [@27,111:119='array_max',<371>,1:111]], id=[[@13,54:55='id',<459>,1:54], [@33,148:156='array_max',<371>,1:148], [@41,195:203='array_max',<371>,1:195]]}, query1={result_value=[[@31,134:145='result_value',<459>,1:134]], id=[[@35,158:159='id',<459>,1:158]]}}",
				"{def_query1={context_list={array_max=query0}, query_dictionary={result_value=[[@31,134:145='result_value',<459>,1:134]], id=[[@35,158:159='id',<459>,1:158]]}, def_query0={query_dictionary={array_max=[[@9,41:49='array_max',<371>,1:41], [@27,111:119='array_max',<371>,1:111]], id=[[@13,54:55='id',<459>,1:54], [@33,148:156='array_max',<371>,1:148], [@41,195:203='array_max',<371>,1:195]]}, table_dictionary={analytics.array_max={array_max=[[@5,26:26='a',<459>,1:26]], active=[[@20,90:90='a',<459>,1:90]], id=[[@11,52:52='a',<459>,1:52]]}}, filters=[{name=active, table_ref=a}], interface={array_max=[{name=array_max, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.array_max}}, filters=[{name=id, table_ref=array_max}], interface={result_value=[{name=array_max, table_ref=array_max}], id=[{name=id, table_ref=array_max}]}, table_alias={array_max=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYMAXExistingWildcardQualifierV0Test() {
		final String query = "select array_max.* from events as array_max where array_max.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_max}}}, from={table={alias=array_max, table=events}}, where={condition={left={column={name=active, table_ref=array_max}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,50:58='array_max',<371>,1:50]], *=[[@1,7:15='array_max',<371>,1:7]]}}",
				"{query0={*=[[@3,17:17='*',<291>,1:17]]}}",
				"{def_query0={query_dictionary={*=[[@3,17:17='*',<291>,1:17]]}, table_dictionary={events={active=[[@9,50:58='array_max',<371>,1:50]], *=[[@1,7:15='array_max',<371>,1:7]]}}, filters=[{name=active, table_ref=array_max}], interface={*=[{name=*, table_ref=array_max}]}, table_alias={array_max=events}}}");
	}

	@Test
	public void contextualKeywordARRAYMINExistingContextualNamesV0Test() {
		final String query = "with array_min as (select a.array_min as array_min, a.id from analytics.array_min a where a.active = 1) select array_min.array_min as result_value, array_min.id from array_min as array_min where array_min.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_min, table_ref=a}, alias=array_min}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_min}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_min}}, query={select={1={column={name=array_min, table_ref=array_min}, alias=result_value}, 2={column={name=id, table_ref=array_min}}}, from={table={alias=array_min, table=array_min}}, where={condition={left={column={name=id, table_ref=array_min}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_min={active=[[@20,90:90='a',<459>,1:90]], array_min=[[@5,26:26='a',<459>,1:26]], id=[[@11,52:52='a',<459>,1:52]]}}",
				"{query0={array_min=[[@9,41:49='array_min',<372>,1:41], [@27,111:119='array_min',<372>,1:111]], id=[[@13,54:55='id',<459>,1:54], [@33,148:156='array_min',<372>,1:148], [@41,195:203='array_min',<372>,1:195]]}, query1={result_value=[[@31,134:145='result_value',<459>,1:134]], id=[[@35,158:159='id',<459>,1:158]]}}",
				"{def_query1={context_list={array_min=query0}, query_dictionary={result_value=[[@31,134:145='result_value',<459>,1:134]], id=[[@35,158:159='id',<459>,1:158]]}, def_query0={query_dictionary={array_min=[[@9,41:49='array_min',<372>,1:41], [@27,111:119='array_min',<372>,1:111]], id=[[@13,54:55='id',<459>,1:54], [@33,148:156='array_min',<372>,1:148], [@41,195:203='array_min',<372>,1:195]]}, table_dictionary={analytics.array_min={active=[[@20,90:90='a',<459>,1:90]], array_min=[[@5,26:26='a',<459>,1:26]], id=[[@11,52:52='a',<459>,1:52]]}}, filters=[{name=active, table_ref=a}], interface={array_min=[{name=array_min, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.array_min}}, filters=[{name=id, table_ref=array_min}], interface={result_value=[{name=array_min, table_ref=array_min}], id=[{name=id, table_ref=array_min}]}, table_alias={array_min=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYMINExistingWildcardQualifierV0Test() {
		final String query = "select array_min.* from events as array_min where array_min.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_min}}}, from={table={alias=array_min, table=events}}, where={condition={left={column={name=active, table_ref=array_min}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,50:58='array_min',<372>,1:50]], *=[[@1,7:15='array_min',<372>,1:7]]}}",
				"{query0={*=[[@3,17:17='*',<291>,1:17]]}}",
				"{def_query0={query_dictionary={*=[[@3,17:17='*',<291>,1:17]]}, table_dictionary={events={active=[[@9,50:58='array_min',<372>,1:50]], *=[[@1,7:15='array_min',<372>,1:7]]}}, filters=[{name=active, table_ref=array_min}], interface={*=[{name=*, table_ref=array_min}]}, table_alias={array_min=events}}}");
	}

	@Test
	public void contextualKeywordARRAYSZIPExistingContextualNamesV0Test() {
		final String query = "with arrays_zip as (select a.arrays_zip as arrays_zip, a.id from analytics.arrays_zip a where a.active = 1) select arrays_zip.arrays_zip as result_value, arrays_zip.id from arrays_zip as arrays_zip where arrays_zip.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=arrays_zip, table_ref=a}, alias=arrays_zip}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=arrays_zip}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=arrays_zip}}, query={select={1={column={name=arrays_zip, table_ref=arrays_zip}, alias=result_value}, 2={column={name=id, table_ref=arrays_zip}}}, from={table={alias=arrays_zip, table=arrays_zip}}, where={condition={left={column={name=id, table_ref=arrays_zip}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.arrays_zip={arrays_zip=[[@5,27:27='a',<459>,1:27]], active=[[@20,94:94='a',<459>,1:94]], id=[[@11,55:55='a',<459>,1:55]]}}",
				"{query0={arrays_zip=[[@9,43:52='arrays_zip',<373>,1:43], [@27,115:124='arrays_zip',<373>,1:115]], id=[[@13,57:58='id',<459>,1:57], [@33,154:163='arrays_zip',<373>,1:154], [@41,204:213='arrays_zip',<373>,1:204]]}, query1={result_value=[[@31,140:151='result_value',<459>,1:140]], id=[[@35,165:166='id',<459>,1:165]]}}",
				"{def_query1={context_list={arrays_zip=query0}, query_dictionary={result_value=[[@31,140:151='result_value',<459>,1:140]], id=[[@35,165:166='id',<459>,1:165]]}, def_query0={query_dictionary={arrays_zip=[[@9,43:52='arrays_zip',<373>,1:43], [@27,115:124='arrays_zip',<373>,1:115]], id=[[@13,57:58='id',<459>,1:57], [@33,154:163='arrays_zip',<373>,1:154], [@41,204:213='arrays_zip',<373>,1:204]]}, table_dictionary={analytics.arrays_zip={arrays_zip=[[@5,27:27='a',<459>,1:27]], active=[[@20,94:94='a',<459>,1:94]], id=[[@11,55:55='a',<459>,1:55]]}}, filters=[{name=active, table_ref=a}], interface={arrays_zip=[{name=arrays_zip, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.arrays_zip}}, filters=[{name=id, table_ref=arrays_zip}], interface={result_value=[{name=arrays_zip, table_ref=arrays_zip}], id=[{name=id, table_ref=arrays_zip}]}, table_alias={arrays_zip=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYSZIPExistingWildcardQualifierV0Test() {
		final String query = "select arrays_zip.* from events as arrays_zip where arrays_zip.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=arrays_zip}}}, from={table={alias=arrays_zip, table=events}}, where={condition={left={column={name=active, table_ref=arrays_zip}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,52:61='arrays_zip',<373>,1:52]], *=[[@1,7:16='arrays_zip',<373>,1:7]]}}",
				"{query0={*=[[@3,18:18='*',<291>,1:18]]}}",
				"{def_query0={query_dictionary={*=[[@3,18:18='*',<291>,1:18]]}, table_dictionary={events={active=[[@9,52:61='arrays_zip',<373>,1:52]], *=[[@1,7:16='arrays_zip',<373>,1:7]]}}, filters=[{name=active, table_ref=arrays_zip}], interface={*=[{name=*, table_ref=arrays_zip}]}, table_alias={arrays_zip=events}}}");
	}

	@Test
	public void contextualKeywordSPLITTOARRAYExistingContextualNamesV0Test() {
		final String query = "with split_to_array as (select a.split_to_array as split_to_array, a.id from analytics.split_to_array a where a.active = 1) select split_to_array.split_to_array as result_value, split_to_array.id from split_to_array as split_to_array where split_to_array.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=split_to_array, table_ref=a}, alias=split_to_array}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=split_to_array}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=split_to_array}}, query={select={1={column={name=split_to_array, table_ref=split_to_array}, alias=result_value}, 2={column={name=id, table_ref=split_to_array}}}, from={table={alias=split_to_array, table=split_to_array}}, where={condition={left={column={name=id, table_ref=split_to_array}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.split_to_array={active=[[@20,110:110='a',<459>,1:110]], id=[[@11,67:67='a',<459>,1:67]], split_to_array=[[@5,31:31='a',<459>,1:31]]}}",
				"{query0={split_to_array=[[@9,51:64='split_to_array',<374>,1:51], [@27,131:144='split_to_array',<374>,1:131]], id=[[@13,69:70='id',<459>,1:69], [@33,178:191='split_to_array',<374>,1:178], [@41,240:253='split_to_array',<374>,1:240]]}, query1={result_value=[[@31,164:175='result_value',<459>,1:164]], id=[[@35,193:194='id',<459>,1:193]]}}",
				"{def_query1={context_list={split_to_array=query0}, query_dictionary={result_value=[[@31,164:175='result_value',<459>,1:164]], id=[[@35,193:194='id',<459>,1:193]]}, def_query0={query_dictionary={id=[[@13,69:70='id',<459>,1:69], [@33,178:191='split_to_array',<374>,1:178], [@41,240:253='split_to_array',<374>,1:240]], split_to_array=[[@9,51:64='split_to_array',<374>,1:51], [@27,131:144='split_to_array',<374>,1:131]]}, table_dictionary={analytics.split_to_array={active=[[@20,110:110='a',<459>,1:110]], id=[[@11,67:67='a',<459>,1:67]], split_to_array=[[@5,31:31='a',<459>,1:31]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], split_to_array=[{name=split_to_array, table_ref=a}]}, table_alias={a=analytics.split_to_array}}, filters=[{name=id, table_ref=split_to_array}], interface={result_value=[{name=split_to_array, table_ref=split_to_array}], id=[{name=id, table_ref=split_to_array}]}, table_alias={split_to_array=query0}}}");
	}

	@Test
	public void contextualKeywordSPLITTOARRAYExistingWildcardQualifierV0Test() {
		final String query = "select split_to_array.* from events as split_to_array where split_to_array.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=split_to_array}}}, from={table={alias=split_to_array, table=events}}, where={condition={left={column={name=active, table_ref=split_to_array}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,60:73='split_to_array',<374>,1:60]], *=[[@1,7:20='split_to_array',<374>,1:7]]}}",
				"{query0={*=[[@3,22:22='*',<291>,1:22]]}}",
				"{def_query0={query_dictionary={*=[[@3,22:22='*',<291>,1:22]]}, table_dictionary={events={active=[[@9,60:73='split_to_array',<374>,1:60]], *=[[@1,7:20='split_to_array',<374>,1:7]]}}, filters=[{name=active, table_ref=split_to_array}], interface={*=[{name=*, table_ref=split_to_array}]}, table_alias={split_to_array=events}}}");
	}

	@Test
	public void contextualKeywordSTRTOKTOARRAYExistingContextualNamesV0Test() {
		final String query = "with strtok_to_array as (select a.strtok_to_array as strtok_to_array, a.id from analytics.strtok_to_array a where a.active = 1) select strtok_to_array.strtok_to_array as result_value, strtok_to_array.id from strtok_to_array as strtok_to_array where strtok_to_array.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=strtok_to_array, table_ref=a}, alias=strtok_to_array}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=strtok_to_array}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=strtok_to_array}}, query={select={1={column={name=strtok_to_array, table_ref=strtok_to_array}, alias=result_value}, 2={column={name=id, table_ref=strtok_to_array}}}, from={table={alias=strtok_to_array, table=strtok_to_array}}, where={condition={left={column={name=id, table_ref=strtok_to_array}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.strtok_to_array={strtok_to_array=[[@5,32:32='a',<459>,1:32]], active=[[@20,114:114='a',<459>,1:114]], id=[[@11,70:70='a',<459>,1:70]]}}",
				"{query0={strtok_to_array=[[@9,53:67='strtok_to_array',<375>,1:53], [@27,135:149='strtok_to_array',<375>,1:135]], id=[[@13,72:73='id',<459>,1:72], [@33,184:198='strtok_to_array',<375>,1:184], [@41,249:263='strtok_to_array',<375>,1:249]]}, query1={result_value=[[@31,170:181='result_value',<459>,1:170]], id=[[@35,200:201='id',<459>,1:200]]}}",
				"{def_query1={context_list={strtok_to_array=query0}, query_dictionary={result_value=[[@31,170:181='result_value',<459>,1:170]], id=[[@35,200:201='id',<459>,1:200]]}, def_query0={query_dictionary={strtok_to_array=[[@9,53:67='strtok_to_array',<375>,1:53], [@27,135:149='strtok_to_array',<375>,1:135]], id=[[@13,72:73='id',<459>,1:72], [@33,184:198='strtok_to_array',<375>,1:184], [@41,249:263='strtok_to_array',<375>,1:249]]}, table_dictionary={analytics.strtok_to_array={strtok_to_array=[[@5,32:32='a',<459>,1:32]], active=[[@20,114:114='a',<459>,1:114]], id=[[@11,70:70='a',<459>,1:70]]}}, filters=[{name=active, table_ref=a}], interface={strtok_to_array=[{name=strtok_to_array, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.strtok_to_array}}, filters=[{name=id, table_ref=strtok_to_array}], interface={result_value=[{name=strtok_to_array, table_ref=strtok_to_array}], id=[{name=id, table_ref=strtok_to_array}]}, table_alias={strtok_to_array=query0}}}");
	}

	@Test
	public void contextualKeywordSTRTOKTOARRAYExistingWildcardQualifierV0Test() {
		final String query = "select strtok_to_array.* from events as strtok_to_array where strtok_to_array.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=strtok_to_array}}}, from={table={alias=strtok_to_array, table=events}}, where={condition={left={column={name=active, table_ref=strtok_to_array}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,62:76='strtok_to_array',<375>,1:62]], *=[[@1,7:21='strtok_to_array',<375>,1:7]]}}",
				"{query0={*=[[@3,23:23='*',<291>,1:23]]}}",
				"{def_query0={query_dictionary={*=[[@3,23:23='*',<291>,1:23]]}, table_dictionary={events={active=[[@9,62:76='strtok_to_array',<375>,1:62]], *=[[@1,7:21='strtok_to_array',<375>,1:7]]}}, filters=[{name=active, table_ref=strtok_to_array}], interface={*=[{name=*, table_ref=strtok_to_array}]}, table_alias={strtok_to_array=events}}}");
	}

	@Test
	public void contextualKeywordTOARRAYExistingContextualNamesV0Test() {
		final String query = "with to_array as (select a.to_array as to_array, a.id from analytics.to_array a where a.active = 1) select to_array.to_array as result_value, to_array.id from to_array as to_array where to_array.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=to_array, table_ref=a}, alias=to_array}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=to_array}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=to_array}}, query={select={1={column={name=to_array, table_ref=to_array}, alias=result_value}, 2={column={name=id, table_ref=to_array}}}, from={table={alias=to_array, table=to_array}}, where={condition={left={column={name=id, table_ref=to_array}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.to_array={to_array=[[@5,25:25='a',<459>,1:25]], active=[[@20,86:86='a',<459>,1:86]], id=[[@11,49:49='a',<459>,1:49]]}}",
				"{query0={to_array=[[@9,39:46='to_array',<376>,1:39], [@27,107:114='to_array',<376>,1:107]], id=[[@13,51:52='id',<459>,1:51], [@33,142:149='to_array',<376>,1:142], [@41,186:193='to_array',<376>,1:186]]}, query1={result_value=[[@31,128:139='result_value',<459>,1:128]], id=[[@35,151:152='id',<459>,1:151]]}}",
				"{def_query1={context_list={to_array=query0}, query_dictionary={result_value=[[@31,128:139='result_value',<459>,1:128]], id=[[@35,151:152='id',<459>,1:151]]}, def_query0={query_dictionary={to_array=[[@9,39:46='to_array',<376>,1:39], [@27,107:114='to_array',<376>,1:107]], id=[[@13,51:52='id',<459>,1:51], [@33,142:149='to_array',<376>,1:142], [@41,186:193='to_array',<376>,1:186]]}, table_dictionary={analytics.to_array={to_array=[[@5,25:25='a',<459>,1:25]], active=[[@20,86:86='a',<459>,1:86]], id=[[@11,49:49='a',<459>,1:49]]}}, filters=[{name=active, table_ref=a}], interface={to_array=[{name=to_array, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.to_array}}, filters=[{name=id, table_ref=to_array}], interface={result_value=[{name=to_array, table_ref=to_array}], id=[{name=id, table_ref=to_array}]}, table_alias={to_array=query0}}}");
	}

	@Test
	public void contextualKeywordTOARRAYExistingWildcardQualifierV0Test() {
		final String query = "select to_array.* from events as to_array where to_array.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=to_array}}}, from={table={alias=to_array, table=events}}, where={condition={left={column={name=active, table_ref=to_array}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,48:55='to_array',<376>,1:48]], *=[[@1,7:14='to_array',<376>,1:7]]}}",
				"{query0={*=[[@3,16:16='*',<291>,1:16]]}}",
				"{def_query0={query_dictionary={*=[[@3,16:16='*',<291>,1:16]]}, table_dictionary={events={active=[[@9,48:55='to_array',<376>,1:48]], *=[[@1,7:14='to_array',<376>,1:7]]}}, filters=[{name=active, table_ref=to_array}], interface={*=[{name=*, table_ref=to_array}]}, table_alias={to_array=events}}}");
	}

	@Test
	public void contextualKeywordASARRAYExistingContextualNamesV0Test() {
		final String query = "with as_array as (select a.as_array as as_array, a.id from analytics.as_array a where a.active = 1) select as_array.as_array as result_value, as_array.id from as_array as as_array where as_array.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=as_array, table_ref=a}, alias=as_array}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=as_array}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=as_array}}, query={select={1={column={name=as_array, table_ref=as_array}, alias=result_value}, 2={column={name=id, table_ref=as_array}}}, from={table={alias=as_array, table=as_array}}, where={condition={left={column={name=id, table_ref=as_array}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.as_array={active=[[@20,86:86='a',<459>,1:86]], id=[[@11,49:49='a',<459>,1:49]], as_array=[[@5,25:25='a',<459>,1:25]]}}",
				"{query0={as_array=[[@9,39:46='as_array',<377>,1:39], [@27,107:114='as_array',<377>,1:107]], id=[[@13,51:52='id',<459>,1:51], [@33,142:149='as_array',<377>,1:142], [@41,186:193='as_array',<377>,1:186]]}, query1={result_value=[[@31,128:139='result_value',<459>,1:128]], id=[[@35,151:152='id',<459>,1:151]]}}",
				"{def_query1={context_list={as_array=query0}, query_dictionary={result_value=[[@31,128:139='result_value',<459>,1:128]], id=[[@35,151:152='id',<459>,1:151]]}, def_query0={query_dictionary={as_array=[[@9,39:46='as_array',<377>,1:39], [@27,107:114='as_array',<377>,1:107]], id=[[@13,51:52='id',<459>,1:51], [@33,142:149='as_array',<377>,1:142], [@41,186:193='as_array',<377>,1:186]]}, table_dictionary={analytics.as_array={active=[[@20,86:86='a',<459>,1:86]], id=[[@11,49:49='a',<459>,1:49]], as_array=[[@5,25:25='a',<459>,1:25]]}}, filters=[{name=active, table_ref=a}], interface={as_array=[{name=as_array, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.as_array}}, filters=[{name=id, table_ref=as_array}], interface={result_value=[{name=as_array, table_ref=as_array}], id=[{name=id, table_ref=as_array}]}, table_alias={as_array=query0}}}");
	}

	@Test
	public void contextualKeywordASARRAYExistingWildcardQualifierV0Test() {
		final String query = "select as_array.* from events as as_array where as_array.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=as_array}}}, from={table={alias=as_array, table=events}}, where={condition={left={column={name=active, table_ref=as_array}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,48:55='as_array',<377>,1:48]], *=[[@1,7:14='as_array',<377>,1:7]]}}",
				"{query0={*=[[@3,16:16='*',<291>,1:16]]}}",
				"{def_query0={query_dictionary={*=[[@3,16:16='*',<291>,1:16]]}, table_dictionary={events={active=[[@9,48:55='as_array',<377>,1:48]], *=[[@1,7:14='as_array',<377>,1:7]]}}, filters=[{name=active, table_ref=as_array}], interface={*=[{name=*, table_ref=as_array}]}, table_alias={as_array=events}}}");
	}

	@Test
	public void contextualKeywordISARRAYExistingContextualNamesV0Test() {
		final String query = "with is_array as (select a.is_array as is_array, a.id from analytics.is_array a where a.active = 1) select is_array.is_array as result_value, is_array.id from is_array as is_array where is_array.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=is_array, table_ref=a}, alias=is_array}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=is_array}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=is_array}}, query={select={1={column={name=is_array, table_ref=is_array}, alias=result_value}, 2={column={name=id, table_ref=is_array}}}, from={table={alias=is_array, table=is_array}}, where={condition={left={column={name=id, table_ref=is_array}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.is_array={is_array=[[@5,25:25='a',<459>,1:25]], active=[[@20,86:86='a',<459>,1:86]], id=[[@11,49:49='a',<459>,1:49]]}}",
				"{query0={is_array=[[@9,39:46='is_array',<378>,1:39], [@27,107:114='is_array',<378>,1:107]], id=[[@13,51:52='id',<459>,1:51], [@33,142:149='is_array',<378>,1:142], [@41,186:193='is_array',<378>,1:186]]}, query1={result_value=[[@31,128:139='result_value',<459>,1:128]], id=[[@35,151:152='id',<459>,1:151]]}}",
				"{def_query1={context_list={is_array=query0}, query_dictionary={result_value=[[@31,128:139='result_value',<459>,1:128]], id=[[@35,151:152='id',<459>,1:151]]}, def_query0={query_dictionary={is_array=[[@9,39:46='is_array',<378>,1:39], [@27,107:114='is_array',<378>,1:107]], id=[[@13,51:52='id',<459>,1:51], [@33,142:149='is_array',<378>,1:142], [@41,186:193='is_array',<378>,1:186]]}, table_dictionary={analytics.is_array={is_array=[[@5,25:25='a',<459>,1:25]], active=[[@20,86:86='a',<459>,1:86]], id=[[@11,49:49='a',<459>,1:49]]}}, filters=[{name=active, table_ref=a}], interface={is_array=[{name=is_array, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.is_array}}, filters=[{name=id, table_ref=is_array}], interface={result_value=[{name=is_array, table_ref=is_array}], id=[{name=id, table_ref=is_array}]}, table_alias={is_array=query0}}}");
	}

	@Test
	public void contextualKeywordISARRAYExistingWildcardQualifierV0Test() {
		final String query = "select is_array.* from events as is_array where is_array.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=is_array}}}, from={table={alias=is_array, table=events}}, where={condition={left={column={name=active, table_ref=is_array}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,48:55='is_array',<378>,1:48]], *=[[@1,7:14='is_array',<378>,1:7]]}}",
				"{query0={*=[[@3,16:16='*',<291>,1:16]]}}",
				"{def_query0={query_dictionary={*=[[@3,16:16='*',<291>,1:16]]}, table_dictionary={events={active=[[@9,48:55='is_array',<378>,1:48]], *=[[@1,7:14='is_array',<378>,1:7]]}}, filters=[{name=active, table_ref=is_array}], interface={*=[{name=*, table_ref=is_array}]}, table_alias={is_array=events}}}");
	}

	@Test
	public void contextualKeywordTRANSFORMExistingContextualNamesV0Test() {
		final String query = "with transform as (select a.transform as transform, a.id from analytics.transform a where a.active = 1) select transform.transform as result_value, transform.id from transform as transform where transform.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=transform, table_ref=a}, alias=transform}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=transform}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=transform}}, query={select={1={column={name=transform, table_ref=transform}, alias=result_value}, 2={column={name=id, table_ref=transform}}}, from={table={alias=transform, table=transform}}, where={condition={left={column={name=id, table_ref=transform}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.transform={transform=[[@5,26:26='a',<459>,1:26]], active=[[@20,90:90='a',<459>,1:90]], id=[[@11,52:52='a',<459>,1:52]]}}",
				"{query0={transform=[[@9,41:49='transform',<379>,1:41], [@27,111:119='transform',<379>,1:111]], id=[[@13,54:55='id',<459>,1:54], [@33,148:156='transform',<379>,1:148], [@41,195:203='transform',<379>,1:195]]}, query1={result_value=[[@31,134:145='result_value',<459>,1:134]], id=[[@35,158:159='id',<459>,1:158]]}}",
				"{def_query1={context_list={transform=query0}, query_dictionary={result_value=[[@31,134:145='result_value',<459>,1:134]], id=[[@35,158:159='id',<459>,1:158]]}, def_query0={query_dictionary={transform=[[@9,41:49='transform',<379>,1:41], [@27,111:119='transform',<379>,1:111]], id=[[@13,54:55='id',<459>,1:54], [@33,148:156='transform',<379>,1:148], [@41,195:203='transform',<379>,1:195]]}, table_dictionary={analytics.transform={transform=[[@5,26:26='a',<459>,1:26]], active=[[@20,90:90='a',<459>,1:90]], id=[[@11,52:52='a',<459>,1:52]]}}, filters=[{name=active, table_ref=a}], interface={transform=[{name=transform, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.transform}}, filters=[{name=id, table_ref=transform}], interface={result_value=[{name=transform, table_ref=transform}], id=[{name=id, table_ref=transform}]}, table_alias={transform=query0}}}");
	}

	@Test
	public void contextualKeywordTRANSFORMExistingWildcardQualifierV0Test() {
		final String query = "select transform.* from events as transform where transform.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=transform}}}, from={table={alias=transform, table=events}}, where={condition={left={column={name=active, table_ref=transform}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,50:58='transform',<379>,1:50]], *=[[@1,7:15='transform',<379>,1:7]]}}",
				"{query0={*=[[@3,17:17='*',<291>,1:17]]}}",
				"{def_query0={query_dictionary={*=[[@3,17:17='*',<291>,1:17]]}, table_dictionary={events={active=[[@9,50:58='transform',<379>,1:50]], *=[[@1,7:15='transform',<379>,1:7]]}}, filters=[{name=active, table_ref=transform}], interface={*=[{name=*, table_ref=transform}]}, table_alias={transform=events}}}");
	}

	@Test
	public void contextualKeywordREDUCEExistingContextualNamesV0Test() {
		final String query = "with reduce as (select a.reduce as reduce, a.id from analytics.reduce a where a.active = 1) select reduce.reduce as result_value, reduce.id from reduce as reduce where reduce.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=reduce, table_ref=a}, alias=reduce}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=reduce}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=reduce}}, query={select={1={column={name=reduce, table_ref=reduce}, alias=result_value}, 2={column={name=id, table_ref=reduce}}}, from={table={alias=reduce, table=reduce}}, where={condition={left={column={name=id, table_ref=reduce}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.reduce={reduce=[[@5,23:23='a',<459>,1:23]], active=[[@20,78:78='a',<459>,1:78]], id=[[@11,43:43='a',<459>,1:43]]}}",
				"{query0={reduce=[[@9,35:40='reduce',<380>,1:35], [@27,99:104='reduce',<380>,1:99]], id=[[@13,45:46='id',<459>,1:45], [@33,130:135='reduce',<380>,1:130], [@41,168:173='reduce',<380>,1:168]]}, query1={result_value=[[@31,116:127='result_value',<459>,1:116]], id=[[@35,137:138='id',<459>,1:137]]}}",
				"{def_query1={context_list={reduce=query0}, query_dictionary={result_value=[[@31,116:127='result_value',<459>,1:116]], id=[[@35,137:138='id',<459>,1:137]]}, def_query0={query_dictionary={reduce=[[@9,35:40='reduce',<380>,1:35], [@27,99:104='reduce',<380>,1:99]], id=[[@13,45:46='id',<459>,1:45], [@33,130:135='reduce',<380>,1:130], [@41,168:173='reduce',<380>,1:168]]}, table_dictionary={analytics.reduce={reduce=[[@5,23:23='a',<459>,1:23]], active=[[@20,78:78='a',<459>,1:78]], id=[[@11,43:43='a',<459>,1:43]]}}, filters=[{name=active, table_ref=a}], interface={reduce=[{name=reduce, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.reduce}}, filters=[{name=id, table_ref=reduce}], interface={result_value=[{name=reduce, table_ref=reduce}], id=[{name=id, table_ref=reduce}]}, table_alias={reduce=query0}}}");
	}

	@Test
	public void contextualKeywordREDUCEExistingWildcardQualifierV0Test() {
		final String query = "select reduce.* from events as reduce where reduce.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=reduce}}}, from={table={alias=reduce, table=events}}, where={condition={left={column={name=active, table_ref=reduce}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,44:49='reduce',<380>,1:44]], *=[[@1,7:12='reduce',<380>,1:7]]}}",
				"{query0={*=[[@3,14:14='*',<291>,1:14]]}}",
				"{def_query0={query_dictionary={*=[[@3,14:14='*',<291>,1:14]]}, table_dictionary={events={active=[[@9,44:49='reduce',<380>,1:44]], *=[[@1,7:12='reduce',<380>,1:7]]}}, filters=[{name=active, table_ref=reduce}], interface={*=[{name=*, table_ref=reduce}]}, table_alias={reduce=events}}}");
	}

	@Test
	public void contextualKeywordINPUTExistingContextualNamesV0Test() {
		final String query = "with input as (select a.input as input, a.id from analytics.input a where a.active = 1) select input.input as result_value, input.id from input as input where input.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=input, table_ref=a}, alias=input}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=input}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=input}}, query={select={1={column={name=input, table_ref=input}, alias=result_value}, 2={column={name=id, table_ref=input}}}, from={table={alias=input, table=input}}, where={condition={left={column={name=id, table_ref=input}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.input={input=[[@5,22:22='a',<459>,1:22]], active=[[@20,74:74='a',<459>,1:74]], id=[[@11,40:40='a',<459>,1:40]]}}",
				"{query0={input=[[@9,33:37='input',<381>,1:33], [@27,95:99='input',<381>,1:95]], id=[[@13,42:43='id',<459>,1:42], [@33,124:128='input',<381>,1:124], [@41,159:163='input',<381>,1:159]]}, query1={result_value=[[@31,110:121='result_value',<459>,1:110]], id=[[@35,130:131='id',<459>,1:130]]}}",
				"{def_query1={context_list={input=query0}, query_dictionary={result_value=[[@31,110:121='result_value',<459>,1:110]], id=[[@35,130:131='id',<459>,1:130]]}, def_query0={query_dictionary={input=[[@9,33:37='input',<381>,1:33], [@27,95:99='input',<381>,1:95]], id=[[@13,42:43='id',<459>,1:42], [@33,124:128='input',<381>,1:124], [@41,159:163='input',<381>,1:159]]}, table_dictionary={analytics.input={input=[[@5,22:22='a',<459>,1:22]], active=[[@20,74:74='a',<459>,1:74]], id=[[@11,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={input=[{name=input, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.input}}, filters=[{name=id, table_ref=input}], interface={result_value=[{name=input, table_ref=input}], id=[{name=id, table_ref=input}]}, table_alias={input=query0}}}");
	}

	@Test
	public void contextualKeywordINPUTExistingWildcardQualifierV0Test() {
		final String query = "select input.* from events as input where input.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=input}}}, from={table={alias=input, table=events}}, where={condition={left={column={name=active, table_ref=input}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,42:46='input',<381>,1:42]], *=[[@1,7:11='input',<381>,1:7]]}}",
				"{query0={*=[[@3,13:13='*',<291>,1:13]]}}",
				"{def_query0={query_dictionary={*=[[@3,13:13='*',<291>,1:13]]}, table_dictionary={events={active=[[@9,42:46='input',<381>,1:42]], *=[[@1,7:11='input',<381>,1:7]]}}, filters=[{name=active, table_ref=input}], interface={*=[{name=*, table_ref=input}]}, table_alias={input=events}}}");
	}

	@Test
	public void contextualKeywordPATHExistingContextualNamesV0Test() {
		final String query = "with path as (select a.path as path, a.id from analytics.path a where a.active = 1) select path.path as result_value, path.id from path as path where path.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=path, table_ref=a}, alias=path}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=path}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=path}}, query={select={1={column={name=path, table_ref=path}, alias=result_value}, 2={column={name=id, table_ref=path}}}, from={table={alias=path, table=path}}, where={condition={left={column={name=id, table_ref=path}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.path={path=[[@5,21:21='a',<459>,1:21]], active=[[@20,70:70='a',<459>,1:70]], id=[[@11,37:37='a',<459>,1:37]]}}",
				"{query0={path=[[@9,31:34='path',<382>,1:31], [@27,91:94='path',<382>,1:91]], id=[[@13,39:40='id',<459>,1:39], [@33,118:121='path',<382>,1:118], [@41,150:153='path',<382>,1:150]]}, query1={result_value=[[@31,104:115='result_value',<459>,1:104]], id=[[@35,123:124='id',<459>,1:123]]}}",
				"{def_query1={context_list={path=query0}, query_dictionary={result_value=[[@31,104:115='result_value',<459>,1:104]], id=[[@35,123:124='id',<459>,1:123]]}, def_query0={query_dictionary={path=[[@9,31:34='path',<382>,1:31], [@27,91:94='path',<382>,1:91]], id=[[@13,39:40='id',<459>,1:39], [@33,118:121='path',<382>,1:118], [@41,150:153='path',<382>,1:150]]}, table_dictionary={analytics.path={path=[[@5,21:21='a',<459>,1:21]], active=[[@20,70:70='a',<459>,1:70]], id=[[@11,37:37='a',<459>,1:37]]}}, filters=[{name=active, table_ref=a}], interface={path=[{name=path, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.path}}, filters=[{name=id, table_ref=path}], interface={result_value=[{name=path, table_ref=path}], id=[{name=id, table_ref=path}]}, table_alias={path=query0}}}");
	}

	@Test
	public void contextualKeywordPATHExistingWildcardQualifierV0Test() {
		final String query = "select path.* from events as path where path.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=path}}}, from={table={alias=path, table=events}}, where={condition={left={column={name=active, table_ref=path}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,40:43='path',<382>,1:40]], *=[[@1,7:10='path',<382>,1:7]]}}",
				"{query0={*=[[@3,12:12='*',<291>,1:12]]}}",
				"{def_query0={query_dictionary={*=[[@3,12:12='*',<291>,1:12]]}, table_dictionary={events={active=[[@9,40:43='path',<382>,1:40]], *=[[@1,7:10='path',<382>,1:7]]}}, filters=[{name=active, table_ref=path}], interface={*=[{name=*, table_ref=path}]}, table_alias={path=events}}}");
	}

	@Test
	public void contextualKeywordALTERExistingContextualNamesV0Test() {
		final String query = "with alter as (select a.alter as alter, a.id from analytics.alter a where a.active = 1) select alter.alter as result_value, alter.id from alter as alter where alter.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=alter, table_ref=a}, alias=alter}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=alter}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=alter}}, query={select={1={column={name=alter, table_ref=alter}, alias=result_value}, 2={column={name=id, table_ref=alter}}}, from={table={alias=alter, table=alter}}, where={condition={left={column={name=id, table_ref=alter}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.alter={active=[[@20,74:74='a',<459>,1:74]], id=[[@11,40:40='a',<459>,1:40]], alter=[[@5,22:22='a',<459>,1:22]]}}",
				"{query0={id=[[@13,42:43='id',<459>,1:42], [@33,124:128='alter',<395>,1:124], [@41,159:163='alter',<395>,1:159]], alter=[[@9,33:37='alter',<395>,1:33], [@27,95:99='alter',<395>,1:95]]}, query1={result_value=[[@31,110:121='result_value',<459>,1:110]], id=[[@35,130:131='id',<459>,1:130]]}}",
				"{def_query1={context_list={alter=query0}, query_dictionary={result_value=[[@31,110:121='result_value',<459>,1:110]], id=[[@35,130:131='id',<459>,1:130]]}, def_query0={query_dictionary={id=[[@13,42:43='id',<459>,1:42], [@33,124:128='alter',<395>,1:124], [@41,159:163='alter',<395>,1:159]], alter=[[@9,33:37='alter',<395>,1:33], [@27,95:99='alter',<395>,1:95]]}, table_dictionary={analytics.alter={active=[[@20,74:74='a',<459>,1:74]], id=[[@11,40:40='a',<459>,1:40]], alter=[[@5,22:22='a',<459>,1:22]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], alter=[{name=alter, table_ref=a}]}, table_alias={a=analytics.alter}}, filters=[{name=id, table_ref=alter}], interface={result_value=[{name=alter, table_ref=alter}], id=[{name=id, table_ref=alter}]}, table_alias={alter=query0}}}");
	}

	@Test
	public void contextualKeywordALTERExistingWildcardQualifierV0Test() {
		final String query = "select alter.* from events as alter where alter.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=alter}}}, from={table={alias=alter, table=events}}, where={condition={left={column={name=active, table_ref=alter}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,42:46='alter',<395>,1:42]], *=[[@1,7:11='alter',<395>,1:7]]}}",
				"{query0={*=[[@3,13:13='*',<291>,1:13]]}}",
				"{def_query0={query_dictionary={*=[[@3,13:13='*',<291>,1:13]]}, table_dictionary={events={active=[[@9,42:46='alter',<395>,1:42]], *=[[@1,7:11='alter',<395>,1:7]]}}, filters=[{name=active, table_ref=alter}], interface={*=[{name=*, table_ref=alter}]}, table_alias={alter=events}}}");
	}

	@Test
	public void contextualKeywordDATABASEExistingContextualNamesV0Test() {
		final String query = "with database as (select a.database as database, a.id from analytics.database a where a.active = 1) select database.database as result_value, database.id from database as database where database.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=database, table_ref=a}, alias=database}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=database}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=database}}, query={select={1={column={name=database, table_ref=database}, alias=result_value}, 2={column={name=id, table_ref=database}}}, from={table={alias=database, table=database}}, where={condition={left={column={name=id, table_ref=database}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.database={database=[[@5,25:25='a',<459>,1:25]], active=[[@20,86:86='a',<459>,1:86]], id=[[@11,49:49='a',<459>,1:49]]}}",
				"{query0={database=[[@9,39:46='database',<396>,1:39], [@27,107:114='database',<396>,1:107]], id=[[@13,51:52='id',<459>,1:51], [@33,142:149='database',<396>,1:142], [@41,186:193='database',<396>,1:186]]}, query1={result_value=[[@31,128:139='result_value',<459>,1:128]], id=[[@35,151:152='id',<459>,1:151]]}}",
				"{def_query1={context_list={database=query0}, query_dictionary={result_value=[[@31,128:139='result_value',<459>,1:128]], id=[[@35,151:152='id',<459>,1:151]]}, def_query0={query_dictionary={database=[[@9,39:46='database',<396>,1:39], [@27,107:114='database',<396>,1:107]], id=[[@13,51:52='id',<459>,1:51], [@33,142:149='database',<396>,1:142], [@41,186:193='database',<396>,1:186]]}, table_dictionary={analytics.database={database=[[@5,25:25='a',<459>,1:25]], active=[[@20,86:86='a',<459>,1:86]], id=[[@11,49:49='a',<459>,1:49]]}}, filters=[{name=active, table_ref=a}], interface={database=[{name=database, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.database}}, filters=[{name=id, table_ref=database}], interface={result_value=[{name=database, table_ref=database}], id=[{name=id, table_ref=database}]}, table_alias={database=query0}}}");
	}

	@Test
	public void contextualKeywordDATABASEExistingWildcardQualifierV0Test() {
		final String query = "select database.* from events as database where database.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=database}}}, from={table={alias=database, table=events}}, where={condition={left={column={name=active, table_ref=database}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,48:55='database',<396>,1:48]], *=[[@1,7:14='database',<396>,1:7]]}}",
				"{query0={*=[[@3,16:16='*',<291>,1:16]]}}",
				"{def_query0={query_dictionary={*=[[@3,16:16='*',<291>,1:16]]}, table_dictionary={events={active=[[@9,48:55='database',<396>,1:48]], *=[[@1,7:14='database',<396>,1:7]]}}, filters=[{name=active, table_ref=database}], interface={*=[{name=*, table_ref=database}]}, table_alias={database=events}}}");
	}

	@Test
	public void contextualKeywordFILEExistingContextualNamesV0Test() {
		final String query = "with file as (select a.file as file, a.id from analytics.file a where a.active = 1) select file.file as result_value, file.id from file as file where file.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=file, table_ref=a}, alias=file}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=file}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=file}}, query={select={1={column={name=file, table_ref=file}, alias=result_value}, 2={column={name=id, table_ref=file}}}, from={table={alias=file, table=file}}, where={condition={left={column={name=id, table_ref=file}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.file={file=[[@5,21:21='a',<459>,1:21]], active=[[@20,70:70='a',<459>,1:70]], id=[[@11,37:37='a',<459>,1:37]]}}",
				"{query0={file=[[@9,31:34='file',<397>,1:31], [@27,91:94='file',<397>,1:91]], id=[[@13,39:40='id',<459>,1:39], [@33,118:121='file',<397>,1:118], [@41,150:153='file',<397>,1:150]]}, query1={result_value=[[@31,104:115='result_value',<459>,1:104]], id=[[@35,123:124='id',<459>,1:123]]}}",
				"{def_query1={context_list={file=query0}, query_dictionary={result_value=[[@31,104:115='result_value',<459>,1:104]], id=[[@35,123:124='id',<459>,1:123]]}, def_query0={query_dictionary={file=[[@9,31:34='file',<397>,1:31], [@27,91:94='file',<397>,1:91]], id=[[@13,39:40='id',<459>,1:39], [@33,118:121='file',<397>,1:118], [@41,150:153='file',<397>,1:150]]}, table_dictionary={analytics.file={file=[[@5,21:21='a',<459>,1:21]], active=[[@20,70:70='a',<459>,1:70]], id=[[@11,37:37='a',<459>,1:37]]}}, filters=[{name=active, table_ref=a}], interface={file=[{name=file, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.file}}, filters=[{name=id, table_ref=file}], interface={result_value=[{name=file, table_ref=file}], id=[{name=id, table_ref=file}]}, table_alias={file=query0}}}");
	}

	@Test
	public void contextualKeywordFILEExistingWildcardQualifierV0Test() {
		final String query = "select file.* from events as file where file.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=file}}}, from={table={alias=file, table=events}}, where={condition={left={column={name=active, table_ref=file}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,40:43='file',<397>,1:40]], *=[[@1,7:10='file',<397>,1:7]]}}",
				"{query0={*=[[@3,12:12='*',<291>,1:12]]}}",
				"{def_query0={query_dictionary={*=[[@3,12:12='*',<291>,1:12]]}, table_dictionary={events={active=[[@9,40:43='file',<397>,1:40]], *=[[@1,7:10='file',<397>,1:7]]}}, filters=[{name=active, table_ref=file}], interface={*=[{name=*, table_ref=file}]}, table_alias={file=events}}}");
	}

	@Test
	public void contextualKeywordFUNCTIONExistingContextualNamesV0Test() {
		final String query = "with function as (select a.function as function, a.id from analytics.function a where a.active = 1) select function.function as result_value, function.id from function as function where function.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=function, table_ref=a}, alias=function}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=function}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=function}}, query={select={1={column={name=function, table_ref=function}, alias=result_value}, 2={column={name=id, table_ref=function}}}, from={table={alias=function, table=function}}, where={condition={left={column={name=id, table_ref=function}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.function={function=[[@5,25:25='a',<459>,1:25]], active=[[@20,86:86='a',<459>,1:86]], id=[[@11,49:49='a',<459>,1:49]]}}",
				"{query0={function=[[@9,39:46='function',<398>,1:39], [@27,107:114='function',<398>,1:107]], id=[[@13,51:52='id',<459>,1:51], [@33,142:149='function',<398>,1:142], [@41,186:193='function',<398>,1:186]]}, query1={result_value=[[@31,128:139='result_value',<459>,1:128]], id=[[@35,151:152='id',<459>,1:151]]}}",
				"{def_query1={context_list={function=query0}, query_dictionary={result_value=[[@31,128:139='result_value',<459>,1:128]], id=[[@35,151:152='id',<459>,1:151]]}, def_query0={query_dictionary={function=[[@9,39:46='function',<398>,1:39], [@27,107:114='function',<398>,1:107]], id=[[@13,51:52='id',<459>,1:51], [@33,142:149='function',<398>,1:142], [@41,186:193='function',<398>,1:186]]}, table_dictionary={analytics.function={function=[[@5,25:25='a',<459>,1:25]], active=[[@20,86:86='a',<459>,1:86]], id=[[@11,49:49='a',<459>,1:49]]}}, filters=[{name=active, table_ref=a}], interface={function=[{name=function, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.function}}, filters=[{name=id, table_ref=function}], interface={result_value=[{name=function, table_ref=function}], id=[{name=id, table_ref=function}]}, table_alias={function=query0}}}");
	}

	@Test
	public void contextualKeywordFUNCTIONExistingWildcardQualifierV0Test() {
		final String query = "select function.* from events as function where function.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=function}}}, from={table={alias=function, table=events}}, where={condition={left={column={name=active, table_ref=function}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,48:55='function',<398>,1:48]], *=[[@1,7:14='function',<398>,1:7]]}}",
				"{query0={*=[[@3,16:16='*',<291>,1:16]]}}",
				"{def_query0={query_dictionary={*=[[@3,16:16='*',<291>,1:16]]}, table_dictionary={events={active=[[@9,48:55='function',<398>,1:48]], *=[[@1,7:14='function',<398>,1:7]]}}, filters=[{name=active, table_ref=function}], interface={*=[{name=*, table_ref=function}]}, table_alias={function=events}}}");
	}

	@Test
	public void contextualKeywordMACROExistingContextualNamesV0Test() {
		final String query = "with macro as (select a.macro as macro, a.id from analytics.macro a where a.active = 1) select macro.macro as result_value, macro.id from macro as macro where macro.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=macro, table_ref=a}, alias=macro}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=macro}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=macro}}, query={select={1={column={name=macro, table_ref=macro}, alias=result_value}, 2={column={name=id, table_ref=macro}}}, from={table={alias=macro, table=macro}}, where={condition={left={column={name=id, table_ref=macro}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.macro={macro=[[@5,22:22='a',<459>,1:22]], active=[[@20,74:74='a',<459>,1:74]], id=[[@11,40:40='a',<459>,1:40]]}}",
				"{query0={macro=[[@9,33:37='macro',<399>,1:33], [@27,95:99='macro',<399>,1:95]], id=[[@13,42:43='id',<459>,1:42], [@33,124:128='macro',<399>,1:124], [@41,159:163='macro',<399>,1:159]]}, query1={result_value=[[@31,110:121='result_value',<459>,1:110]], id=[[@35,130:131='id',<459>,1:130]]}}",
				"{def_query1={context_list={macro=query0}, query_dictionary={result_value=[[@31,110:121='result_value',<459>,1:110]], id=[[@35,130:131='id',<459>,1:130]]}, def_query0={query_dictionary={macro=[[@9,33:37='macro',<399>,1:33], [@27,95:99='macro',<399>,1:95]], id=[[@13,42:43='id',<459>,1:42], [@33,124:128='macro',<399>,1:124], [@41,159:163='macro',<399>,1:159]]}, table_dictionary={analytics.macro={macro=[[@5,22:22='a',<459>,1:22]], active=[[@20,74:74='a',<459>,1:74]], id=[[@11,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={macro=[{name=macro, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.macro}}, filters=[{name=id, table_ref=macro}], interface={result_value=[{name=macro, table_ref=macro}], id=[{name=id, table_ref=macro}]}, table_alias={macro=query0}}}");
	}

	@Test
	public void contextualKeywordMACROExistingWildcardQualifierV0Test() {
		final String query = "select macro.* from events as macro where macro.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=macro}}}, from={table={alias=macro, table=events}}, where={condition={left={column={name=active, table_ref=macro}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,42:46='macro',<399>,1:42]], *=[[@1,7:11='macro',<399>,1:7]]}}",
				"{query0={*=[[@3,13:13='*',<291>,1:13]]}}",
				"{def_query0={query_dictionary={*=[[@3,13:13='*',<291>,1:13]]}, table_dictionary={events={active=[[@9,42:46='macro',<399>,1:42]], *=[[@1,7:11='macro',<399>,1:7]]}}, filters=[{name=active, table_ref=macro}], interface={*=[{name=*, table_ref=macro}]}, table_alias={macro=events}}}");
	}

	@Test
	public void contextualKeywordMATERIALIZEDExistingContextualNamesV0Test() {
		final String query = "with materialized as (select a.materialized as materialized, a.id from analytics.materialized a where a.active = 1) select materialized.materialized as result_value, materialized.id from materialized as materialized where materialized.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=materialized, table_ref=a}, alias=materialized}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=materialized}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=materialized}}, query={select={1={column={name=materialized, table_ref=materialized}, alias=result_value}, 2={column={name=id, table_ref=materialized}}}, from={table={alias=materialized, table=materialized}}, where={condition={left={column={name=id, table_ref=materialized}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.materialized={materialized=[[@5,29:29='a',<459>,1:29]], active=[[@20,102:102='a',<459>,1:102]], id=[[@11,61:61='a',<459>,1:61]]}}",
				"{query0={materialized=[[@9,47:58='materialized',<400>,1:47], [@27,123:134='materialized',<400>,1:123]], id=[[@13,63:64='id',<459>,1:63], [@33,166:177='materialized',<400>,1:166], [@41,222:233='materialized',<400>,1:222]]}, query1={result_value=[[@31,152:163='result_value',<459>,1:152]], id=[[@35,179:180='id',<459>,1:179]]}}",
				"{def_query1={context_list={materialized=query0}, query_dictionary={result_value=[[@31,152:163='result_value',<459>,1:152]], id=[[@35,179:180='id',<459>,1:179]]}, def_query0={query_dictionary={materialized=[[@9,47:58='materialized',<400>,1:47], [@27,123:134='materialized',<400>,1:123]], id=[[@13,63:64='id',<459>,1:63], [@33,166:177='materialized',<400>,1:166], [@41,222:233='materialized',<400>,1:222]]}, table_dictionary={analytics.materialized={materialized=[[@5,29:29='a',<459>,1:29]], active=[[@20,102:102='a',<459>,1:102]], id=[[@11,61:61='a',<459>,1:61]]}}, filters=[{name=active, table_ref=a}], interface={materialized=[{name=materialized, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.materialized}}, filters=[{name=id, table_ref=materialized}], interface={result_value=[{name=materialized, table_ref=materialized}], id=[{name=id, table_ref=materialized}]}, table_alias={materialized=query0}}}");
	}

	@Test
	public void contextualKeywordMATERIALIZEDExistingWildcardQualifierV0Test() {
		final String query = "select materialized.* from events as materialized where materialized.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=materialized}}}, from={table={alias=materialized, table=events}}, where={condition={left={column={name=active, table_ref=materialized}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,56:67='materialized',<400>,1:56]], *=[[@1,7:18='materialized',<400>,1:7]]}}",
				"{query0={*=[[@3,20:20='*',<291>,1:20]]}}",
				"{def_query0={query_dictionary={*=[[@3,20:20='*',<291>,1:20]]}, table_dictionary={events={active=[[@9,56:67='materialized',<400>,1:56]], *=[[@1,7:18='materialized',<400>,1:7]]}}, filters=[{name=active, table_ref=materialized}], interface={*=[{name=*, table_ref=materialized}]}, table_alias={materialized=events}}}");
	}

	@Test
	public void contextualKeywordPROCEDUREExistingContextualNamesV0Test() {
		final String query = "with procedure as (select a.procedure as procedure, a.id from analytics.procedure a where a.active = 1) select procedure.procedure as result_value, procedure.id from procedure as procedure where procedure.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=procedure, table_ref=a}, alias=procedure}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=procedure}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=procedure}}, query={select={1={column={name=procedure, table_ref=procedure}, alias=result_value}, 2={column={name=id, table_ref=procedure}}}, from={table={alias=procedure, table=procedure}}, where={condition={left={column={name=id, table_ref=procedure}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.procedure={active=[[@20,90:90='a',<459>,1:90]], procedure=[[@5,26:26='a',<459>,1:26]], id=[[@11,52:52='a',<459>,1:52]]}}",
				"{query0={procedure=[[@9,41:49='procedure',<401>,1:41], [@27,111:119='procedure',<401>,1:111]], id=[[@13,54:55='id',<459>,1:54], [@33,148:156='procedure',<401>,1:148], [@41,195:203='procedure',<401>,1:195]]}, query1={result_value=[[@31,134:145='result_value',<459>,1:134]], id=[[@35,158:159='id',<459>,1:158]]}}",
				"{def_query1={context_list={procedure=query0}, query_dictionary={result_value=[[@31,134:145='result_value',<459>,1:134]], id=[[@35,158:159='id',<459>,1:158]]}, def_query0={query_dictionary={procedure=[[@9,41:49='procedure',<401>,1:41], [@27,111:119='procedure',<401>,1:111]], id=[[@13,54:55='id',<459>,1:54], [@33,148:156='procedure',<401>,1:148], [@41,195:203='procedure',<401>,1:195]]}, table_dictionary={analytics.procedure={active=[[@20,90:90='a',<459>,1:90]], procedure=[[@5,26:26='a',<459>,1:26]], id=[[@11,52:52='a',<459>,1:52]]}}, filters=[{name=active, table_ref=a}], interface={procedure=[{name=procedure, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.procedure}}, filters=[{name=id, table_ref=procedure}], interface={result_value=[{name=procedure, table_ref=procedure}], id=[{name=id, table_ref=procedure}]}, table_alias={procedure=query0}}}");
	}

	@Test
	public void contextualKeywordPROCEDUREExistingWildcardQualifierV0Test() {
		final String query = "select procedure.* from events as procedure where procedure.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=procedure}}}, from={table={alias=procedure, table=events}}, where={condition={left={column={name=active, table_ref=procedure}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,50:58='procedure',<401>,1:50]], *=[[@1,7:15='procedure',<401>,1:7]]}}",
				"{query0={*=[[@3,17:17='*',<291>,1:17]]}}",
				"{def_query0={query_dictionary={*=[[@3,17:17='*',<291>,1:17]]}, table_dictionary={events={active=[[@9,50:58='procedure',<401>,1:50]], *=[[@1,7:15='procedure',<401>,1:7]]}}, filters=[{name=active, table_ref=procedure}], interface={*=[{name=*, table_ref=procedure}]}, table_alias={procedure=events}}}");
	}

	@Test
	public void contextualKeywordRETURNSExistingContextualNamesV0Test() {
		final String query = "with returns as (select a.returns as returns, a.id from analytics.returns a where a.active = 1) select returns.returns as result_value, returns.id from returns as returns where returns.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=returns, table_ref=a}, alias=returns}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=returns}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=returns}}, query={select={1={column={name=returns, table_ref=returns}, alias=result_value}, 2={column={name=id, table_ref=returns}}}, from={table={alias=returns, table=returns}}, where={condition={left={column={name=id, table_ref=returns}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.returns={active=[[@20,82:82='a',<459>,1:82]], returns=[[@5,24:24='a',<459>,1:24]], id=[[@11,46:46='a',<459>,1:46]]}}",
				"{query0={returns=[[@9,37:43='returns',<402>,1:37], [@27,103:109='returns',<402>,1:103]], id=[[@13,48:49='id',<459>,1:48], [@33,136:142='returns',<402>,1:136], [@41,177:183='returns',<402>,1:177]]}, query1={result_value=[[@31,122:133='result_value',<459>,1:122]], id=[[@35,144:145='id',<459>,1:144]]}}",
				"{def_query1={context_list={returns=query0}, query_dictionary={result_value=[[@31,122:133='result_value',<459>,1:122]], id=[[@35,144:145='id',<459>,1:144]]}, def_query0={query_dictionary={returns=[[@9,37:43='returns',<402>,1:37], [@27,103:109='returns',<402>,1:103]], id=[[@13,48:49='id',<459>,1:48], [@33,136:142='returns',<402>,1:136], [@41,177:183='returns',<402>,1:177]]}, table_dictionary={analytics.returns={active=[[@20,82:82='a',<459>,1:82]], returns=[[@5,24:24='a',<459>,1:24]], id=[[@11,46:46='a',<459>,1:46]]}}, filters=[{name=active, table_ref=a}], interface={returns=[{name=returns, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.returns}}, filters=[{name=id, table_ref=returns}], interface={result_value=[{name=returns, table_ref=returns}], id=[{name=id, table_ref=returns}]}, table_alias={returns=query0}}}");
	}

	@Test
	public void contextualKeywordRETURNSExistingWildcardQualifierV0Test() {
		final String query = "select returns.* from events as returns where returns.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=returns}}}, from={table={alias=returns, table=events}}, where={condition={left={column={name=active, table_ref=returns}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,46:52='returns',<402>,1:46]], *=[[@1,7:13='returns',<402>,1:7]]}}",
				"{query0={*=[[@3,15:15='*',<291>,1:15]]}}",
				"{def_query0={query_dictionary={*=[[@3,15:15='*',<291>,1:15]]}, table_dictionary={events={active=[[@9,46:52='returns',<402>,1:46]], *=[[@1,7:13='returns',<402>,1:7]]}}, filters=[{name=active, table_ref=returns}], interface={*=[{name=*, table_ref=returns}]}, table_alias={returns=events}}}");
	}

	@Test
	public void contextualKeywordROLEExistingContextualNamesV0Test() {
		final String query = "with role as (select a.role as role, a.id from analytics.role a where a.active = 1) select role.role as result_value, role.id from role as role where role.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=role, table_ref=a}, alias=role}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=role}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=role}}, query={select={1={column={name=role, table_ref=role}, alias=result_value}, 2={column={name=id, table_ref=role}}}, from={table={alias=role, table=role}}, where={condition={left={column={name=id, table_ref=role}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.role={role=[[@5,21:21='a',<459>,1:21]], active=[[@20,70:70='a',<459>,1:70]], id=[[@11,37:37='a',<459>,1:37]]}}",
				"{query0={role=[[@9,31:34='role',<403>,1:31], [@27,91:94='role',<403>,1:91]], id=[[@13,39:40='id',<459>,1:39], [@33,118:121='role',<403>,1:118], [@41,150:153='role',<403>,1:150]]}, query1={result_value=[[@31,104:115='result_value',<459>,1:104]], id=[[@35,123:124='id',<459>,1:123]]}}",
				"{def_query1={context_list={role=query0}, query_dictionary={result_value=[[@31,104:115='result_value',<459>,1:104]], id=[[@35,123:124='id',<459>,1:123]]}, def_query0={query_dictionary={role=[[@9,31:34='role',<403>,1:31], [@27,91:94='role',<403>,1:91]], id=[[@13,39:40='id',<459>,1:39], [@33,118:121='role',<403>,1:118], [@41,150:153='role',<403>,1:150]]}, table_dictionary={analytics.role={role=[[@5,21:21='a',<459>,1:21]], active=[[@20,70:70='a',<459>,1:70]], id=[[@11,37:37='a',<459>,1:37]]}}, filters=[{name=active, table_ref=a}], interface={role=[{name=role, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.role}}, filters=[{name=id, table_ref=role}], interface={result_value=[{name=role, table_ref=role}], id=[{name=id, table_ref=role}]}, table_alias={role=query0}}}");
	}

	@Test
	public void contextualKeywordROLEExistingWildcardQualifierV0Test() {
		final String query = "select role.* from events as role where role.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=role}}}, from={table={alias=role, table=events}}, where={condition={left={column={name=active, table_ref=role}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,40:43='role',<403>,1:40]], *=[[@1,7:10='role',<403>,1:7]]}}",
				"{query0={*=[[@3,12:12='*',<291>,1:12]]}}",
				"{def_query0={query_dictionary={*=[[@3,12:12='*',<291>,1:12]]}, table_dictionary={events={active=[[@9,40:43='role',<403>,1:40]], *=[[@1,7:10='role',<403>,1:7]]}}, filters=[{name=active, table_ref=role}], interface={*=[{name=*, table_ref=role}]}, table_alias={role=events}}}");
	}

	@Test
	public void contextualKeywordSCHEMAExistingContextualNamesV0Test() {
		final String query = "with schema as (select a.schema as schema, a.id from analytics.schema a where a.active = 1) select schema.schema as result_value, schema.id from schema as schema where schema.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=schema, table_ref=a}, alias=schema}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=schema}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=schema}}, query={select={1={column={name=schema, table_ref=schema}, alias=result_value}, 2={column={name=id, table_ref=schema}}}, from={table={alias=schema, table=schema}}, where={condition={left={column={name=id, table_ref=schema}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.schema={schema=[[@5,23:23='a',<459>,1:23]], active=[[@20,78:78='a',<459>,1:78]], id=[[@11,43:43='a',<459>,1:43]]}}",
				"{query0={schema=[[@9,35:40='schema',<404>,1:35], [@27,99:104='schema',<404>,1:99]], id=[[@13,45:46='id',<459>,1:45], [@33,130:135='schema',<404>,1:130], [@41,168:173='schema',<404>,1:168]]}, query1={result_value=[[@31,116:127='result_value',<459>,1:116]], id=[[@35,137:138='id',<459>,1:137]]}}",
				"{def_query1={context_list={schema=query0}, query_dictionary={result_value=[[@31,116:127='result_value',<459>,1:116]], id=[[@35,137:138='id',<459>,1:137]]}, def_query0={query_dictionary={schema=[[@9,35:40='schema',<404>,1:35], [@27,99:104='schema',<404>,1:99]], id=[[@13,45:46='id',<459>,1:45], [@33,130:135='schema',<404>,1:130], [@41,168:173='schema',<404>,1:168]]}, table_dictionary={analytics.schema={schema=[[@5,23:23='a',<459>,1:23]], active=[[@20,78:78='a',<459>,1:78]], id=[[@11,43:43='a',<459>,1:43]]}}, filters=[{name=active, table_ref=a}], interface={schema=[{name=schema, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.schema}}, filters=[{name=id, table_ref=schema}], interface={result_value=[{name=schema, table_ref=schema}], id=[{name=id, table_ref=schema}]}, table_alias={schema=query0}}}");
	}

	@Test
	public void contextualKeywordSCHEMAExistingWildcardQualifierV0Test() {
		final String query = "select schema.* from events as schema where schema.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=schema}}}, from={table={alias=schema, table=events}}, where={condition={left={column={name=active, table_ref=schema}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,44:49='schema',<404>,1:44]], *=[[@1,7:12='schema',<404>,1:7]]}}",
				"{query0={*=[[@3,14:14='*',<291>,1:14]]}}",
				"{def_query0={query_dictionary={*=[[@3,14:14='*',<291>,1:14]]}, table_dictionary={events={active=[[@9,44:49='schema',<404>,1:44]], *=[[@1,7:12='schema',<404>,1:7]]}}, filters=[{name=active, table_ref=schema}], interface={*=[{name=*, table_ref=schema}]}, table_alias={schema=events}}}");
	}

	@Test
	public void contextualKeywordSEQUENCEExistingContextualNamesV0Test() {
		final String query = "with sequence as (select a.sequence as sequence, a.id from analytics.sequence a where a.active = 1) select sequence.sequence as result_value, sequence.id from sequence as sequence where sequence.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=sequence, table_ref=a}, alias=sequence}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=sequence}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=sequence}}, query={select={1={column={name=sequence, table_ref=sequence}, alias=result_value}, 2={column={name=id, table_ref=sequence}}}, from={table={alias=sequence, table=sequence}}, where={condition={left={column={name=id, table_ref=sequence}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.sequence={sequence=[[@5,25:25='a',<459>,1:25]], active=[[@20,86:86='a',<459>,1:86]], id=[[@11,49:49='a',<459>,1:49]]}}",
				"{query0={sequence=[[@9,39:46='sequence',<405>,1:39], [@27,107:114='sequence',<405>,1:107]], id=[[@13,51:52='id',<459>,1:51], [@33,142:149='sequence',<405>,1:142], [@41,186:193='sequence',<405>,1:186]]}, query1={result_value=[[@31,128:139='result_value',<459>,1:128]], id=[[@35,151:152='id',<459>,1:151]]}}",
				"{def_query1={context_list={sequence=query0}, query_dictionary={result_value=[[@31,128:139='result_value',<459>,1:128]], id=[[@35,151:152='id',<459>,1:151]]}, def_query0={query_dictionary={sequence=[[@9,39:46='sequence',<405>,1:39], [@27,107:114='sequence',<405>,1:107]], id=[[@13,51:52='id',<459>,1:51], [@33,142:149='sequence',<405>,1:142], [@41,186:193='sequence',<405>,1:186]]}, table_dictionary={analytics.sequence={sequence=[[@5,25:25='a',<459>,1:25]], active=[[@20,86:86='a',<459>,1:86]], id=[[@11,49:49='a',<459>,1:49]]}}, filters=[{name=active, table_ref=a}], interface={sequence=[{name=sequence, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.sequence}}, filters=[{name=id, table_ref=sequence}], interface={result_value=[{name=sequence, table_ref=sequence}], id=[{name=id, table_ref=sequence}]}, table_alias={sequence=query0}}}");
	}

	@Test
	public void contextualKeywordSEQUENCEExistingWildcardQualifierV0Test() {
		final String query = "select sequence.* from events as sequence where sequence.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=sequence}}}, from={table={alias=sequence, table=events}}, where={condition={left={column={name=active, table_ref=sequence}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,48:55='sequence',<405>,1:48]], *=[[@1,7:14='sequence',<405>,1:7]]}}",
				"{query0={*=[[@3,16:16='*',<291>,1:16]]}}",
				"{def_query0={query_dictionary={*=[[@3,16:16='*',<291>,1:16]]}, table_dictionary={events={active=[[@9,48:55='sequence',<405>,1:48]], *=[[@1,7:14='sequence',<405>,1:7]]}}, filters=[{name=active, table_ref=sequence}], interface={*=[{name=*, table_ref=sequence}]}, table_alias={sequence=events}}}");
	}

	@Test
	public void contextualKeywordSTAGEExistingContextualNamesV0Test() {
		final String query = "with stage as (select a.stage as stage, a.id from analytics.stage a where a.active = 1) select stage.stage as result_value, stage.id from stage as stage where stage.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=stage, table_ref=a}, alias=stage}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=stage}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=stage}}, query={select={1={column={name=stage, table_ref=stage}, alias=result_value}, 2={column={name=id, table_ref=stage}}}, from={table={alias=stage, table=stage}}, where={condition={left={column={name=id, table_ref=stage}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.stage={stage=[[@5,22:22='a',<459>,1:22]], active=[[@20,74:74='a',<459>,1:74]], id=[[@11,40:40='a',<459>,1:40]]}}",
				"{query0={stage=[[@9,33:37='stage',<406>,1:33], [@27,95:99='stage',<406>,1:95]], id=[[@13,42:43='id',<459>,1:42], [@33,124:128='stage',<406>,1:124], [@41,159:163='stage',<406>,1:159]]}, query1={result_value=[[@31,110:121='result_value',<459>,1:110]], id=[[@35,130:131='id',<459>,1:130]]}}",
				"{def_query1={context_list={stage=query0}, query_dictionary={result_value=[[@31,110:121='result_value',<459>,1:110]], id=[[@35,130:131='id',<459>,1:130]]}, def_query0={query_dictionary={stage=[[@9,33:37='stage',<406>,1:33], [@27,95:99='stage',<406>,1:95]], id=[[@13,42:43='id',<459>,1:42], [@33,124:128='stage',<406>,1:124], [@41,159:163='stage',<406>,1:159]]}, table_dictionary={analytics.stage={stage=[[@5,22:22='a',<459>,1:22]], active=[[@20,74:74='a',<459>,1:74]], id=[[@11,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={stage=[{name=stage, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.stage}}, filters=[{name=id, table_ref=stage}], interface={result_value=[{name=stage, table_ref=stage}], id=[{name=id, table_ref=stage}]}, table_alias={stage=query0}}}");
	}

	@Test
	public void contextualKeywordSTAGEExistingWildcardQualifierV0Test() {
		final String query = "select stage.* from events as stage where stage.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=stage}}}, from={table={alias=stage, table=events}}, where={condition={left={column={name=active, table_ref=stage}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,42:46='stage',<406>,1:42]], *=[[@1,7:11='stage',<406>,1:7]]}}",
				"{query0={*=[[@3,13:13='*',<291>,1:13]]}}",
				"{def_query0={query_dictionary={*=[[@3,13:13='*',<291>,1:13]]}, table_dictionary={events={active=[[@9,42:46='stage',<406>,1:42]], *=[[@1,7:11='stage',<406>,1:7]]}}, filters=[{name=active, table_ref=stage}], interface={*=[{name=*, table_ref=stage}]}, table_alias={stage=events}}}");
	}

	@Test
	public void contextualKeywordUSERExistingContextualNamesV0Test() {
		final String query = "with user as (select a.user as user, a.id from analytics.user a where a.active = 1) select user.user as result_value, user.id from user as user where user.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=user, table_ref=a}, alias=user}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=user}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=user}}, query={select={1={column={name=user, table_ref=user}, alias=result_value}, 2={column={name=id, table_ref=user}}}, from={table={alias=user, table=user}}, where={condition={left={column={name=id, table_ref=user}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.user={active=[[@20,70:70='a',<459>,1:70]], id=[[@11,37:37='a',<459>,1:37]], user=[[@5,21:21='a',<459>,1:21]]}}",
				"{query0={user=[[@9,31:34='user',<407>,1:31], [@27,91:94='user',<407>,1:91]], id=[[@13,39:40='id',<459>,1:39], [@33,118:121='user',<407>,1:118], [@41,150:153='user',<407>,1:150]]}, query1={result_value=[[@31,104:115='result_value',<459>,1:104]], id=[[@35,123:124='id',<459>,1:123]]}}",
				"{def_query1={context_list={user=query0}, query_dictionary={result_value=[[@31,104:115='result_value',<459>,1:104]], id=[[@35,123:124='id',<459>,1:123]]}, def_query0={query_dictionary={id=[[@13,39:40='id',<459>,1:39], [@33,118:121='user',<407>,1:118], [@41,150:153='user',<407>,1:150]], user=[[@9,31:34='user',<407>,1:31], [@27,91:94='user',<407>,1:91]]}, table_dictionary={analytics.user={active=[[@20,70:70='a',<459>,1:70]], id=[[@11,37:37='a',<459>,1:37]], user=[[@5,21:21='a',<459>,1:21]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], user=[{name=user, table_ref=a}]}, table_alias={a=analytics.user}}, filters=[{name=id, table_ref=user}], interface={result_value=[{name=user, table_ref=user}], id=[{name=id, table_ref=user}]}, table_alias={user=query0}}}");
	}

	@Test
	public void contextualKeywordUSERExistingWildcardQualifierV0Test() {
		final String query = "select user.* from events as user where user.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=user}}}, from={table={alias=user, table=events}}, where={condition={left={column={name=active, table_ref=user}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,40:43='user',<407>,1:40]], *=[[@1,7:10='user',<407>,1:7]]}}",
				"{query0={*=[[@3,12:12='*',<291>,1:12]]}}",
				"{def_query0={query_dictionary={*=[[@3,12:12='*',<291>,1:12]]}, table_dictionary={events={active=[[@9,40:43='user',<407>,1:40]], *=[[@1,7:10='user',<407>,1:7]]}}, filters=[{name=active, table_ref=user}], interface={*=[{name=*, table_ref=user}]}, table_alias={user=events}}}");
	}

	@Test
	public void contextualKeywordVIEWExistingContextualNamesV0Test() {
		final String query = "with view as (select a.view as view, a.id from analytics.view a where a.active = 1) select view.view as result_value, view.id from view as view where view.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=view, table_ref=a}, alias=view}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=view}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=view}}, query={select={1={column={name=view, table_ref=view}, alias=result_value}, 2={column={name=id, table_ref=view}}}, from={table={alias=view, table=view}}, where={condition={left={column={name=id, table_ref=view}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.view={view=[[@5,21:21='a',<459>,1:21]], active=[[@20,70:70='a',<459>,1:70]], id=[[@11,37:37='a',<459>,1:37]]}}",
				"{query0={view=[[@9,31:34='view',<408>,1:31], [@27,91:94='view',<408>,1:91]], id=[[@13,39:40='id',<459>,1:39], [@33,118:121='view',<408>,1:118], [@41,150:153='view',<408>,1:150]]}, query1={result_value=[[@31,104:115='result_value',<459>,1:104]], id=[[@35,123:124='id',<459>,1:123]]}}",
				"{def_query1={context_list={view=query0}, query_dictionary={result_value=[[@31,104:115='result_value',<459>,1:104]], id=[[@35,123:124='id',<459>,1:123]]}, def_query0={query_dictionary={view=[[@9,31:34='view',<408>,1:31], [@27,91:94='view',<408>,1:91]], id=[[@13,39:40='id',<459>,1:39], [@33,118:121='view',<408>,1:118], [@41,150:153='view',<408>,1:150]]}, table_dictionary={analytics.view={view=[[@5,21:21='a',<459>,1:21]], active=[[@20,70:70='a',<459>,1:70]], id=[[@11,37:37='a',<459>,1:37]]}}, filters=[{name=active, table_ref=a}], interface={view=[{name=view, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.view}}, filters=[{name=id, table_ref=view}], interface={result_value=[{name=view, table_ref=view}], id=[{name=id, table_ref=view}]}, table_alias={view=query0}}}");
	}

	@Test
	public void contextualKeywordVIEWExistingWildcardQualifierV0Test() {
		final String query = "select view.* from events as view where view.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=view}}}, from={table={alias=view, table=events}}, where={condition={left={column={name=active, table_ref=view}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,40:43='view',<408>,1:40]], *=[[@1,7:10='view',<408>,1:7]]}}",
				"{query0={*=[[@3,12:12='*',<291>,1:12]]}}",
				"{def_query0={query_dictionary={*=[[@3,12:12='*',<291>,1:12]]}, table_dictionary={events={active=[[@9,40:43='view',<408>,1:40]], *=[[@1,7:10='view',<408>,1:7]]}}, filters=[{name=active, table_ref=view}], interface={*=[{name=*, table_ref=view}]}, table_alias={view=events}}}");
	}

	@Test
	public void contextualKeywordFORExistingContextualNamesV0Test() {
		final String query = "with for as (select a.for as for, a.id from analytics.for a where a.active = 1) select for.for as result_value, for.id from for as for where for.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=for, table_ref=a}, alias=for}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=for}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=for}}, query={select={1={column={name=for, table_ref=for}, alias=result_value}, 2={column={name=id, table_ref=for}}}, from={table={alias=for, table=for}}, where={condition={left={column={name=id, table_ref=for}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.for={for=[[@5,20:20='a',<459>,1:20]], active=[[@20,66:66='a',<459>,1:66]], id=[[@11,34:34='a',<459>,1:34]]}}",
				"{query0={for=[[@9,29:31='for',<409>,1:29], [@27,87:89='for',<409>,1:87]], id=[[@13,36:37='id',<459>,1:36], [@33,112:114='for',<409>,1:112], [@41,141:143='for',<409>,1:141]]}, query1={result_value=[[@31,98:109='result_value',<459>,1:98]], id=[[@35,116:117='id',<459>,1:116]]}}",
				"{def_query1={context_list={for=query0}, query_dictionary={result_value=[[@31,98:109='result_value',<459>,1:98]], id=[[@35,116:117='id',<459>,1:116]]}, def_query0={query_dictionary={for=[[@9,29:31='for',<409>,1:29], [@27,87:89='for',<409>,1:87]], id=[[@13,36:37='id',<459>,1:36], [@33,112:114='for',<409>,1:112], [@41,141:143='for',<409>,1:141]]}, table_dictionary={analytics.for={for=[[@5,20:20='a',<459>,1:20]], active=[[@20,66:66='a',<459>,1:66]], id=[[@11,34:34='a',<459>,1:34]]}}, filters=[{name=active, table_ref=a}], interface={for=[{name=for, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.for}}, filters=[{name=id, table_ref=for}], interface={result_value=[{name=for, table_ref=for}], id=[{name=id, table_ref=for}]}, table_alias={for=query0}}}");
	}

	@Test
	public void contextualKeywordFORExistingWildcardQualifierV0Test() {
		final String query = "select for.* from events as for where for.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=for}}}, from={table={alias=for, table=events}}, where={condition={left={column={name=active, table_ref=for}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,38:40='for',<409>,1:38]], *=[[@1,7:9='for',<409>,1:7]]}}",
				"{query0={*=[[@3,11:11='*',<291>,1:11]]}}",
				"{def_query0={query_dictionary={*=[[@3,11:11='*',<291>,1:11]]}, table_dictionary={events={active=[[@9,38:40='for',<409>,1:38]], *=[[@1,7:9='for',<409>,1:7]]}}, filters=[{name=active, table_ref=for}], interface={*=[{name=*, table_ref=for}]}, table_alias={for=events}}}");
	}

	@Test
	public void contextualKeywordINCLUDEExistingContextualNamesV0Test() {
		final String query = "with include as (select a.include as include, a.id from analytics.include a where a.active = 1) select include.include as result_value, include.id from include as include where include.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=include, table_ref=a}, alias=include}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=include}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=include}}, query={select={1={column={name=include, table_ref=include}, alias=result_value}, 2={column={name=id, table_ref=include}}}, from={table={alias=include, table=include}}, where={condition={left={column={name=id, table_ref=include}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.include={include=[[@5,24:24='a',<459>,1:24]], active=[[@20,82:82='a',<459>,1:82]], id=[[@11,46:46='a',<459>,1:46]]}}",
				"{query0={include=[[@9,37:43='include',<410>,1:37], [@27,103:109='include',<410>,1:103]], id=[[@13,48:49='id',<459>,1:48], [@33,136:142='include',<410>,1:136], [@41,177:183='include',<410>,1:177]]}, query1={result_value=[[@31,122:133='result_value',<459>,1:122]], id=[[@35,144:145='id',<459>,1:144]]}}",
				"{def_query1={context_list={include=query0}, query_dictionary={result_value=[[@31,122:133='result_value',<459>,1:122]], id=[[@35,144:145='id',<459>,1:144]]}, def_query0={query_dictionary={include=[[@9,37:43='include',<410>,1:37], [@27,103:109='include',<410>,1:103]], id=[[@13,48:49='id',<459>,1:48], [@33,136:142='include',<410>,1:136], [@41,177:183='include',<410>,1:177]]}, table_dictionary={analytics.include={include=[[@5,24:24='a',<459>,1:24]], active=[[@20,82:82='a',<459>,1:82]], id=[[@11,46:46='a',<459>,1:46]]}}, filters=[{name=active, table_ref=a}], interface={include=[{name=include, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.include}}, filters=[{name=id, table_ref=include}], interface={result_value=[{name=include, table_ref=include}], id=[{name=id, table_ref=include}]}, table_alias={include=query0}}}");
	}

	@Test
	public void contextualKeywordINCLUDEExistingWildcardQualifierV0Test() {
		final String query = "select include.* from events as include where include.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=include}}}, from={table={alias=include, table=events}}, where={condition={left={column={name=active, table_ref=include}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,46:52='include',<410>,1:46]], *=[[@1,7:13='include',<410>,1:7]]}}",
				"{query0={*=[[@3,15:15='*',<291>,1:15]]}}",
				"{def_query0={query_dictionary={*=[[@3,15:15='*',<291>,1:15]]}, table_dictionary={events={active=[[@9,46:52='include',<410>,1:46]], *=[[@1,7:13='include',<410>,1:7]]}}, filters=[{name=active, table_ref=include}], interface={*=[{name=*, table_ref=include}]}, table_alias={include=events}}}");
	}

	@Test
	public void contextualKeywordEXCLUDEExistingContextualNamesV0Test() {
		final String query = "with exclude as (select a.exclude as exclude, a.id from analytics.exclude a where a.active = 1) select exclude.exclude as result_value, exclude.id from exclude as exclude where exclude.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=exclude, table_ref=a}, alias=exclude}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=exclude}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=exclude}}, query={select={1={column={name=exclude, table_ref=exclude}, alias=result_value}, 2={column={name=id, table_ref=exclude}}}, from={table={alias=exclude, table=exclude}}, where={condition={left={column={name=id, table_ref=exclude}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.exclude={active=[[@20,82:82='a',<459>,1:82]], exclude=[[@5,24:24='a',<459>,1:24]], id=[[@11,46:46='a',<459>,1:46]]}}",
				"{query0={exclude=[[@9,37:43='exclude',<411>,1:37], [@27,103:109='exclude',<411>,1:103]], id=[[@13,48:49='id',<459>,1:48], [@33,136:142='exclude',<411>,1:136], [@41,177:183='exclude',<411>,1:177]]}, query1={result_value=[[@31,122:133='result_value',<459>,1:122]], id=[[@35,144:145='id',<459>,1:144]]}}",
				"{def_query1={context_list={exclude=query0}, query_dictionary={result_value=[[@31,122:133='result_value',<459>,1:122]], id=[[@35,144:145='id',<459>,1:144]]}, def_query0={query_dictionary={exclude=[[@9,37:43='exclude',<411>,1:37], [@27,103:109='exclude',<411>,1:103]], id=[[@13,48:49='id',<459>,1:48], [@33,136:142='exclude',<411>,1:136], [@41,177:183='exclude',<411>,1:177]]}, table_dictionary={analytics.exclude={active=[[@20,82:82='a',<459>,1:82]], exclude=[[@5,24:24='a',<459>,1:24]], id=[[@11,46:46='a',<459>,1:46]]}}, filters=[{name=active, table_ref=a}], interface={exclude=[{name=exclude, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.exclude}}, filters=[{name=id, table_ref=exclude}], interface={result_value=[{name=exclude, table_ref=exclude}], id=[{name=id, table_ref=exclude}]}, table_alias={exclude=query0}}}");
	}

	@Test
	public void contextualKeywordEXCLUDEExistingWildcardQualifierV0Test() {
		final String query = "select exclude.* from events as exclude where exclude.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=exclude}}}, from={table={alias=exclude, table=events}}, where={condition={left={column={name=active, table_ref=exclude}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,46:52='exclude',<411>,1:46]], *=[[@1,7:13='exclude',<411>,1:7]]}}",
				"{query0={*=[[@3,15:15='*',<291>,1:15]]}}",
				"{def_query0={query_dictionary={*=[[@3,15:15='*',<291>,1:15]]}, table_dictionary={events={active=[[@9,46:52='exclude',<411>,1:46]], *=[[@1,7:13='exclude',<411>,1:7]]}}, filters=[{name=active, table_ref=exclude}], interface={*=[{name=*, table_ref=exclude}]}, table_alias={exclude=events}}}");
	}

	@Test
	public void contextualKeywordUNPIVOTExistingContextualNamesV0Test() {
		final String query = "with unpivot as (select a.unpivot as unpivot, a.id from analytics.unpivot a where a.active = 1) select unpivot.unpivot as result_value, unpivot.id from unpivot as unpivot where unpivot.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=unpivot, table_ref=a}, alias=unpivot}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=unpivot}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=unpivot}}, query={select={1={column={name=unpivot, table_ref=unpivot}, alias=result_value}, 2={column={name=id, table_ref=unpivot}}}, from={table={alias=unpivot, table=unpivot}}, where={condition={left={column={name=id, table_ref=unpivot}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.unpivot={unpivot=[[@5,24:24='a',<459>,1:24]], active=[[@20,82:82='a',<459>,1:82]], id=[[@11,46:46='a',<459>,1:46]]}}",
				"{query0={unpivot=[[@9,37:43='unpivot',<412>,1:37], [@27,103:109='unpivot',<412>,1:103]], id=[[@13,48:49='id',<459>,1:48], [@33,136:142='unpivot',<412>,1:136], [@41,177:183='unpivot',<412>,1:177]]}, query1={result_value=[[@31,122:133='result_value',<459>,1:122]], id=[[@35,144:145='id',<459>,1:144]]}}",
				"{def_query1={context_list={unpivot=query0}, query_dictionary={result_value=[[@31,122:133='result_value',<459>,1:122]], id=[[@35,144:145='id',<459>,1:144]]}, def_query0={query_dictionary={unpivot=[[@9,37:43='unpivot',<412>,1:37], [@27,103:109='unpivot',<412>,1:103]], id=[[@13,48:49='id',<459>,1:48], [@33,136:142='unpivot',<412>,1:136], [@41,177:183='unpivot',<412>,1:177]]}, table_dictionary={analytics.unpivot={unpivot=[[@5,24:24='a',<459>,1:24]], active=[[@20,82:82='a',<459>,1:82]], id=[[@11,46:46='a',<459>,1:46]]}}, filters=[{name=active, table_ref=a}], interface={unpivot=[{name=unpivot, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.unpivot}}, filters=[{name=id, table_ref=unpivot}], interface={result_value=[{name=unpivot, table_ref=unpivot}], id=[{name=id, table_ref=unpivot}]}, table_alias={unpivot=query0}}}");
	}

	@Test
	public void contextualKeywordUNPIVOTExistingWildcardQualifierV0Test() {
		final String query = "select unpivot.* from events as unpivot where unpivot.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=unpivot}}}, from={table={alias=unpivot, table=events}}, where={condition={left={column={name=active, table_ref=unpivot}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,46:52='unpivot',<412>,1:46]], *=[[@1,7:13='unpivot',<412>,1:7]]}}",
				"{query0={*=[[@3,15:15='*',<291>,1:15]]}}",
				"{def_query0={query_dictionary={*=[[@3,15:15='*',<291>,1:15]]}, table_dictionary={events={active=[[@9,46:52='unpivot',<412>,1:46]], *=[[@1,7:13='unpivot',<412>,1:7]]}}, filters=[{name=active, table_ref=unpivot}], interface={*=[{name=*, table_ref=unpivot}]}, table_alias={unpivot=events}}}");
	}

	@Test
	public void contextualKeywordPIVOTExistingContextualNamesV0Test() {
		final String query = "with pivot as (select a.pivot as pivot, a.id from analytics.pivot a where a.active = 1) select pivot.pivot as result_value, pivot.id from pivot as pivot where pivot.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=pivot, table_ref=a}, alias=pivot}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=pivot}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=pivot}}, query={select={1={column={name=pivot, table_ref=pivot}, alias=result_value}, 2={column={name=id, table_ref=pivot}}}, from={table={alias=pivot, table=pivot}}, where={condition={left={column={name=id, table_ref=pivot}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.pivot={pivot=[[@5,22:22='a',<459>,1:22]], active=[[@20,74:74='a',<459>,1:74]], id=[[@11,40:40='a',<459>,1:40]]}}",
				"{query0={pivot=[[@9,33:37='pivot',<413>,1:33], [@27,95:99='pivot',<413>,1:95]], id=[[@13,42:43='id',<459>,1:42], [@33,124:128='pivot',<413>,1:124], [@41,159:163='pivot',<413>,1:159]]}, query1={result_value=[[@31,110:121='result_value',<459>,1:110]], id=[[@35,130:131='id',<459>,1:130]]}}",
				"{def_query1={context_list={pivot=query0}, query_dictionary={result_value=[[@31,110:121='result_value',<459>,1:110]], id=[[@35,130:131='id',<459>,1:130]]}, def_query0={query_dictionary={pivot=[[@9,33:37='pivot',<413>,1:33], [@27,95:99='pivot',<413>,1:95]], id=[[@13,42:43='id',<459>,1:42], [@33,124:128='pivot',<413>,1:124], [@41,159:163='pivot',<413>,1:159]]}, table_dictionary={analytics.pivot={pivot=[[@5,22:22='a',<459>,1:22]], active=[[@20,74:74='a',<459>,1:74]], id=[[@11,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={pivot=[{name=pivot, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.pivot}}, filters=[{name=id, table_ref=pivot}], interface={result_value=[{name=pivot, table_ref=pivot}], id=[{name=id, table_ref=pivot}]}, table_alias={pivot=query0}}}");
	}

	@Test
	public void contextualKeywordPIVOTExistingWildcardQualifierV0Test() {
		final String query = "select pivot.* from events as pivot where pivot.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=pivot}}}, from={table={alias=pivot, table=events}}, where={condition={left={column={name=active, table_ref=pivot}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,42:46='pivot',<413>,1:42]], *=[[@1,7:11='pivot',<413>,1:7]]}}",
				"{query0={*=[[@3,13:13='*',<291>,1:13]]}}",
				"{def_query0={query_dictionary={*=[[@3,13:13='*',<291>,1:13]]}, table_dictionary={events={active=[[@9,42:46='pivot',<413>,1:42]], *=[[@1,7:11='pivot',<413>,1:7]]}}, filters=[{name=active, table_ref=pivot}], interface={*=[{name=*, table_ref=pivot}]}, table_alias={pivot=events}}}");
	}

	@Test
	public void contextualKeywordDATEPARTExistingContextualNamesV0Test() {
		final String query = "with date_part as (select a.date_part as date_part, a.id from analytics.date_part a where a.active = 1) select date_part.date_part as result_value, date_part.id from date_part as date_part where date_part.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=date_part, table_ref=a}, alias=date_part}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=date_part}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=date_part}}, query={select={1={column={name=date_part, table_ref=date_part}, alias=result_value}, 2={column={name=id, table_ref=date_part}}}, from={table={alias=date_part, table=date_part}}, where={condition={left={column={name=id, table_ref=date_part}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.date_part={active=[[@20,90:90='a',<459>,1:90]], id=[[@11,52:52='a',<459>,1:52]], date_part=[[@5,26:26='a',<459>,1:26]]}}",
				"{query0={date_part=[[@9,41:49='date_part',<423>,1:41], [@27,111:119='date_part',<423>,1:111]], id=[[@13,54:55='id',<459>,1:54], [@33,148:156='date_part',<423>,1:148], [@41,195:203='date_part',<423>,1:195]]}, query1={result_value=[[@31,134:145='result_value',<459>,1:134]], id=[[@35,158:159='id',<459>,1:158]]}}",
				"{def_query1={context_list={date_part=query0}, query_dictionary={result_value=[[@31,134:145='result_value',<459>,1:134]], id=[[@35,158:159='id',<459>,1:158]]}, def_query0={query_dictionary={id=[[@13,54:55='id',<459>,1:54], [@33,148:156='date_part',<423>,1:148], [@41,195:203='date_part',<423>,1:195]], date_part=[[@9,41:49='date_part',<423>,1:41], [@27,111:119='date_part',<423>,1:111]]}, table_dictionary={analytics.date_part={active=[[@20,90:90='a',<459>,1:90]], id=[[@11,52:52='a',<459>,1:52]], date_part=[[@5,26:26='a',<459>,1:26]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], date_part=[{name=date_part, table_ref=a}]}, table_alias={a=analytics.date_part}}, filters=[{name=id, table_ref=date_part}], interface={result_value=[{name=date_part, table_ref=date_part}], id=[{name=id, table_ref=date_part}]}, table_alias={date_part=query0}}}");
	}

	@Test
	public void contextualKeywordDATEPARTExistingWildcardQualifierV0Test() {
		final String query = "select date_part.* from events as date_part where date_part.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=date_part}}}, from={table={alias=date_part, table=events}}, where={condition={left={column={name=active, table_ref=date_part}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,50:58='date_part',<423>,1:50]], *=[[@1,7:15='date_part',<423>,1:7]]}}",
				"{query0={*=[[@3,17:17='*',<291>,1:17]]}}",
				"{def_query0={query_dictionary={*=[[@3,17:17='*',<291>,1:17]]}, table_dictionary={events={active=[[@9,50:58='date_part',<423>,1:50]], *=[[@1,7:15='date_part',<423>,1:7]]}}, filters=[{name=active, table_ref=date_part}], interface={*=[{name=*, table_ref=date_part}]}, table_alias={date_part=events}}}");
	}

	@Test
	public void contextualKeywordDATEADDExistingContextualNamesV0Test() {
		final String query = "with dateadd as (select a.dateadd as dateadd, a.id from analytics.dateadd a where a.active = 1) select dateadd.dateadd as result_value, dateadd.id from dateadd as dateadd where dateadd.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=dateadd, table_ref=a}, alias=dateadd}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=dateadd}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=dateadd}}, query={select={1={column={name=dateadd, table_ref=dateadd}, alias=result_value}, 2={column={name=id, table_ref=dateadd}}}, from={table={alias=dateadd, table=dateadd}}, where={condition={left={column={name=id, table_ref=dateadd}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.dateadd={dateadd=[[@5,24:24='a',<459>,1:24]], active=[[@20,82:82='a',<459>,1:82]], id=[[@11,46:46='a',<459>,1:46]]}}",
				"{query0={dateadd=[[@9,37:43='dateadd',<426>,1:37], [@27,103:109='dateadd',<426>,1:103]], id=[[@13,48:49='id',<459>,1:48], [@33,136:142='dateadd',<426>,1:136], [@41,177:183='dateadd',<426>,1:177]]}, query1={result_value=[[@31,122:133='result_value',<459>,1:122]], id=[[@35,144:145='id',<459>,1:144]]}}",
				"{def_query1={context_list={dateadd=query0}, query_dictionary={result_value=[[@31,122:133='result_value',<459>,1:122]], id=[[@35,144:145='id',<459>,1:144]]}, def_query0={query_dictionary={dateadd=[[@9,37:43='dateadd',<426>,1:37], [@27,103:109='dateadd',<426>,1:103]], id=[[@13,48:49='id',<459>,1:48], [@33,136:142='dateadd',<426>,1:136], [@41,177:183='dateadd',<426>,1:177]]}, table_dictionary={analytics.dateadd={dateadd=[[@5,24:24='a',<459>,1:24]], active=[[@20,82:82='a',<459>,1:82]], id=[[@11,46:46='a',<459>,1:46]]}}, filters=[{name=active, table_ref=a}], interface={dateadd=[{name=dateadd, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.dateadd}}, filters=[{name=id, table_ref=dateadd}], interface={result_value=[{name=dateadd, table_ref=dateadd}], id=[{name=id, table_ref=dateadd}]}, table_alias={dateadd=query0}}}");
	}

	@Test
	public void contextualKeywordDATEADDExistingWildcardQualifierV0Test() {
		final String query = "select dateadd.* from events as dateadd where dateadd.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=dateadd}}}, from={table={alias=dateadd, table=events}}, where={condition={left={column={name=active, table_ref=dateadd}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,46:52='dateadd',<426>,1:46]], *=[[@1,7:13='dateadd',<426>,1:7]]}}",
				"{query0={*=[[@3,15:15='*',<291>,1:15]]}}",
				"{def_query0={query_dictionary={*=[[@3,15:15='*',<291>,1:15]]}, table_dictionary={events={active=[[@9,46:52='dateadd',<426>,1:46]], *=[[@1,7:13='dateadd',<426>,1:7]]}}, filters=[{name=active, table_ref=dateadd}], interface={*=[{name=*, table_ref=dateadd}]}, table_alias={dateadd=events}}}");
	}

	@Test
	public void contextualKeywordTIMEADDExistingContextualNamesV0Test() {
		final String query = "with timeadd as (select a.timeadd as timeadd, a.id from analytics.timeadd a where a.active = 1) select timeadd.timeadd as result_value, timeadd.id from timeadd as timeadd where timeadd.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=timeadd, table_ref=a}, alias=timeadd}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=timeadd}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=timeadd}}, query={select={1={column={name=timeadd, table_ref=timeadd}, alias=result_value}, 2={column={name=id, table_ref=timeadd}}}, from={table={alias=timeadd, table=timeadd}}, where={condition={left={column={name=id, table_ref=timeadd}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.timeadd={active=[[@20,82:82='a',<459>,1:82]], timeadd=[[@5,24:24='a',<459>,1:24]], id=[[@11,46:46='a',<459>,1:46]]}}",
				"{query0={timeadd=[[@9,37:43='timeadd',<427>,1:37], [@27,103:109='timeadd',<427>,1:103]], id=[[@13,48:49='id',<459>,1:48], [@33,136:142='timeadd',<427>,1:136], [@41,177:183='timeadd',<427>,1:177]]}, query1={result_value=[[@31,122:133='result_value',<459>,1:122]], id=[[@35,144:145='id',<459>,1:144]]}}",
				"{def_query1={context_list={timeadd=query0}, query_dictionary={result_value=[[@31,122:133='result_value',<459>,1:122]], id=[[@35,144:145='id',<459>,1:144]]}, def_query0={query_dictionary={timeadd=[[@9,37:43='timeadd',<427>,1:37], [@27,103:109='timeadd',<427>,1:103]], id=[[@13,48:49='id',<459>,1:48], [@33,136:142='timeadd',<427>,1:136], [@41,177:183='timeadd',<427>,1:177]]}, table_dictionary={analytics.timeadd={active=[[@20,82:82='a',<459>,1:82]], timeadd=[[@5,24:24='a',<459>,1:24]], id=[[@11,46:46='a',<459>,1:46]]}}, filters=[{name=active, table_ref=a}], interface={timeadd=[{name=timeadd, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.timeadd}}, filters=[{name=id, table_ref=timeadd}], interface={result_value=[{name=timeadd, table_ref=timeadd}], id=[{name=id, table_ref=timeadd}]}, table_alias={timeadd=query0}}}");
	}

	@Test
	public void contextualKeywordTIMEADDExistingWildcardQualifierV0Test() {
		final String query = "select timeadd.* from events as timeadd where timeadd.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=timeadd}}}, from={table={alias=timeadd, table=events}}, where={condition={left={column={name=active, table_ref=timeadd}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,46:52='timeadd',<427>,1:46]], *=[[@1,7:13='timeadd',<427>,1:7]]}}",
				"{query0={*=[[@3,15:15='*',<291>,1:15]]}}",
				"{def_query0={query_dictionary={*=[[@3,15:15='*',<291>,1:15]]}, table_dictionary={events={active=[[@9,46:52='timeadd',<427>,1:46]], *=[[@1,7:13='timeadd',<427>,1:7]]}}, filters=[{name=active, table_ref=timeadd}], interface={*=[{name=*, table_ref=timeadd}]}, table_alias={timeadd=events}}}");
	}

	@Test
	public void contextualKeywordTIMESTAMPADDExistingContextualNamesV0Test() {
		final String query = "with timestampadd as (select a.timestampadd as timestampadd, a.id from analytics.timestampadd a where a.active = 1) select timestampadd.timestampadd as result_value, timestampadd.id from timestampadd as timestampadd where timestampadd.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=timestampadd, table_ref=a}, alias=timestampadd}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=timestampadd}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=timestampadd}}, query={select={1={column={name=timestampadd, table_ref=timestampadd}, alias=result_value}, 2={column={name=id, table_ref=timestampadd}}}, from={table={alias=timestampadd, table=timestampadd}}, where={condition={left={column={name=id, table_ref=timestampadd}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.timestampadd={active=[[@20,102:102='a',<459>,1:102]], id=[[@11,61:61='a',<459>,1:61]], timestampadd=[[@5,29:29='a',<459>,1:29]]}}",
				"{query0={timestampadd=[[@9,47:58='timestampadd',<428>,1:47], [@27,123:134='timestampadd',<428>,1:123]], id=[[@13,63:64='id',<459>,1:63], [@33,166:177='timestampadd',<428>,1:166], [@41,222:233='timestampadd',<428>,1:222]]}, query1={result_value=[[@31,152:163='result_value',<459>,1:152]], id=[[@35,179:180='id',<459>,1:179]]}}",
				"{def_query1={context_list={timestampadd=query0}, query_dictionary={result_value=[[@31,152:163='result_value',<459>,1:152]], id=[[@35,179:180='id',<459>,1:179]]}, def_query0={query_dictionary={id=[[@13,63:64='id',<459>,1:63], [@33,166:177='timestampadd',<428>,1:166], [@41,222:233='timestampadd',<428>,1:222]], timestampadd=[[@9,47:58='timestampadd',<428>,1:47], [@27,123:134='timestampadd',<428>,1:123]]}, table_dictionary={analytics.timestampadd={active=[[@20,102:102='a',<459>,1:102]], id=[[@11,61:61='a',<459>,1:61]], timestampadd=[[@5,29:29='a',<459>,1:29]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], timestampadd=[{name=timestampadd, table_ref=a}]}, table_alias={a=analytics.timestampadd}}, filters=[{name=id, table_ref=timestampadd}], interface={result_value=[{name=timestampadd, table_ref=timestampadd}], id=[{name=id, table_ref=timestampadd}]}, table_alias={timestampadd=query0}}}");
	}

	@Test
	public void contextualKeywordTIMESTAMPADDExistingWildcardQualifierV0Test() {
		final String query = "select timestampadd.* from events as timestampadd where timestampadd.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=timestampadd}}}, from={table={alias=timestampadd, table=events}}, where={condition={left={column={name=active, table_ref=timestampadd}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,56:67='timestampadd',<428>,1:56]], *=[[@1,7:18='timestampadd',<428>,1:7]]}}",
				"{query0={*=[[@3,20:20='*',<291>,1:20]]}}",
				"{def_query0={query_dictionary={*=[[@3,20:20='*',<291>,1:20]]}, table_dictionary={events={active=[[@9,56:67='timestampadd',<428>,1:56]], *=[[@1,7:18='timestampadd',<428>,1:7]]}}, filters=[{name=active, table_ref=timestampadd}], interface={*=[{name=*, table_ref=timestampadd}]}, table_alias={timestampadd=events}}}");
	}

	@Test
	public void contextualKeywordDATEDIFFExistingContextualNamesV0Test() {
		final String query = "with datediff as (select a.datediff as datediff, a.id from analytics.datediff a where a.active = 1) select datediff.datediff as result_value, datediff.id from datediff as datediff where datediff.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=datediff, table_ref=a}, alias=datediff}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=datediff}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=datediff}}, query={select={1={column={name=datediff, table_ref=datediff}, alias=result_value}, 2={column={name=id, table_ref=datediff}}}, from={table={alias=datediff, table=datediff}}, where={condition={left={column={name=id, table_ref=datediff}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.datediff={datediff=[[@5,25:25='a',<459>,1:25]], active=[[@20,86:86='a',<459>,1:86]], id=[[@11,49:49='a',<459>,1:49]]}}",
				"{query0={datediff=[[@9,39:46='datediff',<429>,1:39], [@27,107:114='datediff',<429>,1:107]], id=[[@13,51:52='id',<459>,1:51], [@33,142:149='datediff',<429>,1:142], [@41,186:193='datediff',<429>,1:186]]}, query1={result_value=[[@31,128:139='result_value',<459>,1:128]], id=[[@35,151:152='id',<459>,1:151]]}}",
				"{def_query1={context_list={datediff=query0}, query_dictionary={result_value=[[@31,128:139='result_value',<459>,1:128]], id=[[@35,151:152='id',<459>,1:151]]}, def_query0={query_dictionary={datediff=[[@9,39:46='datediff',<429>,1:39], [@27,107:114='datediff',<429>,1:107]], id=[[@13,51:52='id',<459>,1:51], [@33,142:149='datediff',<429>,1:142], [@41,186:193='datediff',<429>,1:186]]}, table_dictionary={analytics.datediff={datediff=[[@5,25:25='a',<459>,1:25]], active=[[@20,86:86='a',<459>,1:86]], id=[[@11,49:49='a',<459>,1:49]]}}, filters=[{name=active, table_ref=a}], interface={datediff=[{name=datediff, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.datediff}}, filters=[{name=id, table_ref=datediff}], interface={result_value=[{name=datediff, table_ref=datediff}], id=[{name=id, table_ref=datediff}]}, table_alias={datediff=query0}}}");
	}

	@Test
	public void contextualKeywordDATEDIFFExistingWildcardQualifierV0Test() {
		final String query = "select datediff.* from events as datediff where datediff.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=datediff}}}, from={table={alias=datediff, table=events}}, where={condition={left={column={name=active, table_ref=datediff}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,48:55='datediff',<429>,1:48]], *=[[@1,7:14='datediff',<429>,1:7]]}}",
				"{query0={*=[[@3,16:16='*',<291>,1:16]]}}",
				"{def_query0={query_dictionary={*=[[@3,16:16='*',<291>,1:16]]}, table_dictionary={events={active=[[@9,48:55='datediff',<429>,1:48]], *=[[@1,7:14='datediff',<429>,1:7]]}}, filters=[{name=active, table_ref=datediff}], interface={*=[{name=*, table_ref=datediff}]}, table_alias={datediff=events}}}");
	}

	@Test
	public void contextualKeywordTIMEDIFFExistingContextualNamesV0Test() {
		final String query = "with timediff as (select a.timediff as timediff, a.id from analytics.timediff a where a.active = 1) select timediff.timediff as result_value, timediff.id from timediff as timediff where timediff.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=timediff, table_ref=a}, alias=timediff}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=timediff}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=timediff}}, query={select={1={column={name=timediff, table_ref=timediff}, alias=result_value}, 2={column={name=id, table_ref=timediff}}}, from={table={alias=timediff, table=timediff}}, where={condition={left={column={name=id, table_ref=timediff}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.timediff={timediff=[[@5,25:25='a',<459>,1:25]], active=[[@20,86:86='a',<459>,1:86]], id=[[@11,49:49='a',<459>,1:49]]}}",
				"{query0={timediff=[[@9,39:46='timediff',<430>,1:39], [@27,107:114='timediff',<430>,1:107]], id=[[@13,51:52='id',<459>,1:51], [@33,142:149='timediff',<430>,1:142], [@41,186:193='timediff',<430>,1:186]]}, query1={result_value=[[@31,128:139='result_value',<459>,1:128]], id=[[@35,151:152='id',<459>,1:151]]}}",
				"{def_query1={context_list={timediff=query0}, query_dictionary={result_value=[[@31,128:139='result_value',<459>,1:128]], id=[[@35,151:152='id',<459>,1:151]]}, def_query0={query_dictionary={timediff=[[@9,39:46='timediff',<430>,1:39], [@27,107:114='timediff',<430>,1:107]], id=[[@13,51:52='id',<459>,1:51], [@33,142:149='timediff',<430>,1:142], [@41,186:193='timediff',<430>,1:186]]}, table_dictionary={analytics.timediff={timediff=[[@5,25:25='a',<459>,1:25]], active=[[@20,86:86='a',<459>,1:86]], id=[[@11,49:49='a',<459>,1:49]]}}, filters=[{name=active, table_ref=a}], interface={timediff=[{name=timediff, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.timediff}}, filters=[{name=id, table_ref=timediff}], interface={result_value=[{name=timediff, table_ref=timediff}], id=[{name=id, table_ref=timediff}]}, table_alias={timediff=query0}}}");
	}

	@Test
	public void contextualKeywordTIMEDIFFExistingWildcardQualifierV0Test() {
		final String query = "select timediff.* from events as timediff where timediff.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=timediff}}}, from={table={alias=timediff, table=events}}, where={condition={left={column={name=active, table_ref=timediff}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,48:55='timediff',<430>,1:48]], *=[[@1,7:14='timediff',<430>,1:7]]}}",
				"{query0={*=[[@3,16:16='*',<291>,1:16]]}}",
				"{def_query0={query_dictionary={*=[[@3,16:16='*',<291>,1:16]]}, table_dictionary={events={active=[[@9,48:55='timediff',<430>,1:48]], *=[[@1,7:14='timediff',<430>,1:7]]}}, filters=[{name=active, table_ref=timediff}], interface={*=[{name=*, table_ref=timediff}]}, table_alias={timediff=events}}}");
	}

	@Test
	public void contextualKeywordTIMESTAMPDIFFExistingContextualNamesV0Test() {
		final String query = "with timestampdiff as (select a.timestampdiff as timestampdiff, a.id from analytics.timestampdiff a where a.active = 1) select timestampdiff.timestampdiff as result_value, timestampdiff.id from timestampdiff as timestampdiff where timestampdiff.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=timestampdiff, table_ref=a}, alias=timestampdiff}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=timestampdiff}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=timestampdiff}}, query={select={1={column={name=timestampdiff, table_ref=timestampdiff}, alias=result_value}, 2={column={name=id, table_ref=timestampdiff}}}, from={table={alias=timestampdiff, table=timestampdiff}}, where={condition={left={column={name=id, table_ref=timestampdiff}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.timestampdiff={active=[[@20,106:106='a',<459>,1:106]], timestampdiff=[[@5,30:30='a',<459>,1:30]], id=[[@11,64:64='a',<459>,1:64]]}}",
				"{query0={timestampdiff=[[@9,49:61='timestampdiff',<431>,1:49], [@27,127:139='timestampdiff',<431>,1:127]], id=[[@13,66:67='id',<459>,1:66], [@33,172:184='timestampdiff',<431>,1:172], [@41,231:243='timestampdiff',<431>,1:231]]}, query1={result_value=[[@31,158:169='result_value',<459>,1:158]], id=[[@35,186:187='id',<459>,1:186]]}}",
				"{def_query1={context_list={timestampdiff=query0}, query_dictionary={result_value=[[@31,158:169='result_value',<459>,1:158]], id=[[@35,186:187='id',<459>,1:186]]}, def_query0={query_dictionary={timestampdiff=[[@9,49:61='timestampdiff',<431>,1:49], [@27,127:139='timestampdiff',<431>,1:127]], id=[[@13,66:67='id',<459>,1:66], [@33,172:184='timestampdiff',<431>,1:172], [@41,231:243='timestampdiff',<431>,1:231]]}, table_dictionary={analytics.timestampdiff={active=[[@20,106:106='a',<459>,1:106]], timestampdiff=[[@5,30:30='a',<459>,1:30]], id=[[@11,64:64='a',<459>,1:64]]}}, filters=[{name=active, table_ref=a}], interface={timestampdiff=[{name=timestampdiff, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.timestampdiff}}, filters=[{name=id, table_ref=timestampdiff}], interface={result_value=[{name=timestampdiff, table_ref=timestampdiff}], id=[{name=id, table_ref=timestampdiff}]}, table_alias={timestampdiff=query0}}}");
	}

	@Test
	public void contextualKeywordTIMESTAMPDIFFExistingWildcardQualifierV0Test() {
		final String query = "select timestampdiff.* from events as timestampdiff where timestampdiff.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=timestampdiff}}}, from={table={alias=timestampdiff, table=events}}, where={condition={left={column={name=active, table_ref=timestampdiff}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,58:70='timestampdiff',<431>,1:58]], *=[[@1,7:19='timestampdiff',<431>,1:7]]}}",
				"{query0={*=[[@3,21:21='*',<291>,1:21]]}}",
				"{def_query0={query_dictionary={*=[[@3,21:21='*',<291>,1:21]]}, table_dictionary={events={active=[[@9,58:70='timestampdiff',<431>,1:58]], *=[[@1,7:19='timestampdiff',<431>,1:7]]}}, filters=[{name=active, table_ref=timestampdiff}], interface={*=[{name=*, table_ref=timestampdiff}]}, table_alias={timestampdiff=events}}}");
	}

	@Test
	public void contextualKeywordDATETRUNCExistingContextualNamesV0Test() {
		final String query = "with date_trunc as (select a.date_trunc as date_trunc, a.id from analytics.date_trunc a where a.active = 1) select date_trunc.date_trunc as result_value, date_trunc.id from date_trunc as date_trunc where date_trunc.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=date_trunc, table_ref=a}, alias=date_trunc}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=date_trunc}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=date_trunc}}, query={select={1={column={name=date_trunc, table_ref=date_trunc}, alias=result_value}, 2={column={name=id, table_ref=date_trunc}}}, from={table={alias=date_trunc, table=date_trunc}}, where={condition={left={column={name=id, table_ref=date_trunc}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.date_trunc={active=[[@20,94:94='a',<459>,1:94]], date_trunc=[[@5,27:27='a',<459>,1:27]], id=[[@11,55:55='a',<459>,1:55]]}}",
				"{query0={date_trunc=[[@9,43:52='date_trunc',<432>,1:43], [@27,115:124='date_trunc',<432>,1:115]], id=[[@13,57:58='id',<459>,1:57], [@33,154:163='date_trunc',<432>,1:154], [@41,204:213='date_trunc',<432>,1:204]]}, query1={result_value=[[@31,140:151='result_value',<459>,1:140]], id=[[@35,165:166='id',<459>,1:165]]}}",
				"{def_query1={context_list={date_trunc=query0}, query_dictionary={result_value=[[@31,140:151='result_value',<459>,1:140]], id=[[@35,165:166='id',<459>,1:165]]}, def_query0={query_dictionary={date_trunc=[[@9,43:52='date_trunc',<432>,1:43], [@27,115:124='date_trunc',<432>,1:115]], id=[[@13,57:58='id',<459>,1:57], [@33,154:163='date_trunc',<432>,1:154], [@41,204:213='date_trunc',<432>,1:204]]}, table_dictionary={analytics.date_trunc={active=[[@20,94:94='a',<459>,1:94]], date_trunc=[[@5,27:27='a',<459>,1:27]], id=[[@11,55:55='a',<459>,1:55]]}}, filters=[{name=active, table_ref=a}], interface={date_trunc=[{name=date_trunc, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.date_trunc}}, filters=[{name=id, table_ref=date_trunc}], interface={result_value=[{name=date_trunc, table_ref=date_trunc}], id=[{name=id, table_ref=date_trunc}]}, table_alias={date_trunc=query0}}}");
	}

	@Test
	public void contextualKeywordDATETRUNCExistingWildcardQualifierV0Test() {
		final String query = "select date_trunc.* from events as date_trunc where date_trunc.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=date_trunc}}}, from={table={alias=date_trunc, table=events}}, where={condition={left={column={name=active, table_ref=date_trunc}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,52:61='date_trunc',<432>,1:52]], *=[[@1,7:16='date_trunc',<432>,1:7]]}}",
				"{query0={*=[[@3,18:18='*',<291>,1:18]]}}",
				"{def_query0={query_dictionary={*=[[@3,18:18='*',<291>,1:18]]}, table_dictionary={events={active=[[@9,52:61='date_trunc',<432>,1:52]], *=[[@1,7:16='date_trunc',<432>,1:7]]}}, filters=[{name=active, table_ref=date_trunc}], interface={*=[{name=*, table_ref=date_trunc}]}, table_alias={date_trunc=events}}}");
	}

	@Test
	public void contextualKeywordTRUNCExistingContextualNamesV0Test() {
		final String query = "with trunc as (select a.trunc as trunc, a.id from analytics.trunc a where a.active = 1) select trunc.trunc as result_value, trunc.id from trunc as trunc where trunc.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=trunc, table_ref=a}, alias=trunc}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=trunc}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=trunc}}, query={select={1={column={name=trunc, table_ref=trunc}, alias=result_value}, 2={column={name=id, table_ref=trunc}}}, from={table={alias=trunc, table=trunc}}, where={condition={left={column={name=id, table_ref=trunc}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.trunc={trunc=[[@5,22:22='a',<459>,1:22]], active=[[@20,74:74='a',<459>,1:74]], id=[[@11,40:40='a',<459>,1:40]]}}",
				"{query0={trunc=[[@9,33:37='trunc',<433>,1:33], [@27,95:99='trunc',<433>,1:95]], id=[[@13,42:43='id',<459>,1:42], [@33,124:128='trunc',<433>,1:124], [@41,159:163='trunc',<433>,1:159]]}, query1={result_value=[[@31,110:121='result_value',<459>,1:110]], id=[[@35,130:131='id',<459>,1:130]]}}",
				"{def_query1={context_list={trunc=query0}, query_dictionary={result_value=[[@31,110:121='result_value',<459>,1:110]], id=[[@35,130:131='id',<459>,1:130]]}, def_query0={query_dictionary={trunc=[[@9,33:37='trunc',<433>,1:33], [@27,95:99='trunc',<433>,1:95]], id=[[@13,42:43='id',<459>,1:42], [@33,124:128='trunc',<433>,1:124], [@41,159:163='trunc',<433>,1:159]]}, table_dictionary={analytics.trunc={trunc=[[@5,22:22='a',<459>,1:22]], active=[[@20,74:74='a',<459>,1:74]], id=[[@11,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={trunc=[{name=trunc, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.trunc}}, filters=[{name=id, table_ref=trunc}], interface={result_value=[{name=trunc, table_ref=trunc}], id=[{name=id, table_ref=trunc}]}, table_alias={trunc=query0}}}");
	}

	@Test
	public void contextualKeywordTRUNCExistingWildcardQualifierV0Test() {
		final String query = "select trunc.* from events as trunc where trunc.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=trunc}}}, from={table={alias=trunc, table=events}}, where={condition={left={column={name=active, table_ref=trunc}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,42:46='trunc',<433>,1:42]], *=[[@1,7:11='trunc',<433>,1:7]]}}",
				"{query0={*=[[@3,13:13='*',<291>,1:13]]}}",
				"{def_query0={query_dictionary={*=[[@3,13:13='*',<291>,1:13]]}, table_dictionary={events={active=[[@9,42:46='trunc',<433>,1:42]], *=[[@1,7:11='trunc',<433>,1:7]]}}, filters=[{name=active, table_ref=trunc}], interface={*=[{name=*, table_ref=trunc}]}, table_alias={trunc=events}}}");
	}

	@Test
	public void contextualKeywordTIMESLICEExistingContextualNamesV0Test() {
		final String query = "with time_slice as (select a.time_slice as time_slice, a.id from analytics.time_slice a where a.active = 1) select time_slice.time_slice as result_value, time_slice.id from time_slice as time_slice where time_slice.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=time_slice, table_ref=a}, alias=time_slice}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=time_slice}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=time_slice}}, query={select={1={column={name=time_slice, table_ref=time_slice}, alias=result_value}, 2={column={name=id, table_ref=time_slice}}}, from={table={alias=time_slice, table=time_slice}}, where={condition={left={column={name=id, table_ref=time_slice}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.time_slice={time_slice=[[@5,27:27='a',<459>,1:27]], active=[[@20,94:94='a',<459>,1:94]], id=[[@11,55:55='a',<459>,1:55]]}}",
				"{query0={time_slice=[[@9,43:52='time_slice',<434>,1:43], [@27,115:124='time_slice',<434>,1:115]], id=[[@13,57:58='id',<459>,1:57], [@33,154:163='time_slice',<434>,1:154], [@41,204:213='time_slice',<434>,1:204]]}, query1={result_value=[[@31,140:151='result_value',<459>,1:140]], id=[[@35,165:166='id',<459>,1:165]]}}",
				"{def_query1={context_list={time_slice=query0}, query_dictionary={result_value=[[@31,140:151='result_value',<459>,1:140]], id=[[@35,165:166='id',<459>,1:165]]}, def_query0={query_dictionary={time_slice=[[@9,43:52='time_slice',<434>,1:43], [@27,115:124='time_slice',<434>,1:115]], id=[[@13,57:58='id',<459>,1:57], [@33,154:163='time_slice',<434>,1:154], [@41,204:213='time_slice',<434>,1:204]]}, table_dictionary={analytics.time_slice={time_slice=[[@5,27:27='a',<459>,1:27]], active=[[@20,94:94='a',<459>,1:94]], id=[[@11,55:55='a',<459>,1:55]]}}, filters=[{name=active, table_ref=a}], interface={time_slice=[{name=time_slice, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.time_slice}}, filters=[{name=id, table_ref=time_slice}], interface={result_value=[{name=time_slice, table_ref=time_slice}], id=[{name=id, table_ref=time_slice}]}, table_alias={time_slice=query0}}}");
	}

	@Test
	public void contextualKeywordTIMESLICEExistingWildcardQualifierV0Test() {
		final String query = "select time_slice.* from events as time_slice where time_slice.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=time_slice}}}, from={table={alias=time_slice, table=events}}, where={condition={left={column={name=active, table_ref=time_slice}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,52:61='time_slice',<434>,1:52]], *=[[@1,7:16='time_slice',<434>,1:7]]}}",
				"{query0={*=[[@3,18:18='*',<291>,1:18]]}}",
				"{def_query0={query_dictionary={*=[[@3,18:18='*',<291>,1:18]]}, table_dictionary={events={active=[[@9,52:61='time_slice',<434>,1:52]], *=[[@1,7:16='time_slice',<434>,1:7]]}}, filters=[{name=active, table_ref=time_slice}], interface={*=[{name=*, table_ref=time_slice}]}, table_alias={time_slice=events}}}");
	}

	@Test
	public void contextualKeywordLASTDAYExistingContextualNamesV0Test() {
		final String query = "with last_day as (select a.last_day as last_day, a.id from analytics.last_day a where a.active = 1) select last_day.last_day as result_value, last_day.id from last_day as last_day where last_day.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=last_day, table_ref=a}, alias=last_day}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=last_day}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=last_day}}, query={select={1={column={name=last_day, table_ref=last_day}, alias=result_value}, 2={column={name=id, table_ref=last_day}}}, from={table={alias=last_day, table=last_day}}, where={condition={left={column={name=id, table_ref=last_day}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.last_day={active=[[@20,86:86='a',<459>,1:86]], id=[[@11,49:49='a',<459>,1:49]], last_day=[[@5,25:25='a',<459>,1:25]]}}",
				"{query0={last_day=[[@9,39:46='last_day',<435>,1:39], [@27,107:114='last_day',<435>,1:107]], id=[[@13,51:52='id',<459>,1:51], [@33,142:149='last_day',<435>,1:142], [@41,186:193='last_day',<435>,1:186]]}, query1={result_value=[[@31,128:139='result_value',<459>,1:128]], id=[[@35,151:152='id',<459>,1:151]]}}",
				"{def_query1={context_list={last_day=query0}, query_dictionary={result_value=[[@31,128:139='result_value',<459>,1:128]], id=[[@35,151:152='id',<459>,1:151]]}, def_query0={query_dictionary={last_day=[[@9,39:46='last_day',<435>,1:39], [@27,107:114='last_day',<435>,1:107]], id=[[@13,51:52='id',<459>,1:51], [@33,142:149='last_day',<435>,1:142], [@41,186:193='last_day',<435>,1:186]]}, table_dictionary={analytics.last_day={active=[[@20,86:86='a',<459>,1:86]], id=[[@11,49:49='a',<459>,1:49]], last_day=[[@5,25:25='a',<459>,1:25]]}}, filters=[{name=active, table_ref=a}], interface={last_day=[{name=last_day, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.last_day}}, filters=[{name=id, table_ref=last_day}], interface={result_value=[{name=last_day, table_ref=last_day}], id=[{name=id, table_ref=last_day}]}, table_alias={last_day=query0}}}");
	}

	@Test
	public void contextualKeywordLASTDAYExistingWildcardQualifierV0Test() {
		final String query = "select last_day.* from events as last_day where last_day.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=last_day}}}, from={table={alias=last_day, table=events}}, where={condition={left={column={name=active, table_ref=last_day}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,48:55='last_day',<435>,1:48]], *=[[@1,7:14='last_day',<435>,1:7]]}}",
				"{query0={*=[[@3,16:16='*',<291>,1:16]]}}",
				"{def_query0={query_dictionary={*=[[@3,16:16='*',<291>,1:16]]}, table_dictionary={events={active=[[@9,48:55='last_day',<435>,1:48]], *=[[@1,7:14='last_day',<435>,1:7]]}}, filters=[{name=active, table_ref=last_day}], interface={*=[{name=*, table_ref=last_day}]}, table_alias={last_day=events}}}");
	}

	@Test
	public void contextualKeywordNEXTDAYExistingContextualNamesV0Test() {
		final String query = "with next_day as (select a.next_day as next_day, a.id from analytics.next_day a where a.active = 1) select next_day.next_day as result_value, next_day.id from next_day as next_day where next_day.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=next_day, table_ref=a}, alias=next_day}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=next_day}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=next_day}}, query={select={1={column={name=next_day, table_ref=next_day}, alias=result_value}, 2={column={name=id, table_ref=next_day}}}, from={table={alias=next_day, table=next_day}}, where={condition={left={column={name=id, table_ref=next_day}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.next_day={active=[[@20,86:86='a',<459>,1:86]], id=[[@11,49:49='a',<459>,1:49]], next_day=[[@5,25:25='a',<459>,1:25]]}}",
				"{query0={next_day=[[@9,39:46='next_day',<436>,1:39], [@27,107:114='next_day',<436>,1:107]], id=[[@13,51:52='id',<459>,1:51], [@33,142:149='next_day',<436>,1:142], [@41,186:193='next_day',<436>,1:186]]}, query1={result_value=[[@31,128:139='result_value',<459>,1:128]], id=[[@35,151:152='id',<459>,1:151]]}}",
				"{def_query1={context_list={next_day=query0}, query_dictionary={result_value=[[@31,128:139='result_value',<459>,1:128]], id=[[@35,151:152='id',<459>,1:151]]}, def_query0={query_dictionary={next_day=[[@9,39:46='next_day',<436>,1:39], [@27,107:114='next_day',<436>,1:107]], id=[[@13,51:52='id',<459>,1:51], [@33,142:149='next_day',<436>,1:142], [@41,186:193='next_day',<436>,1:186]]}, table_dictionary={analytics.next_day={active=[[@20,86:86='a',<459>,1:86]], id=[[@11,49:49='a',<459>,1:49]], next_day=[[@5,25:25='a',<459>,1:25]]}}, filters=[{name=active, table_ref=a}], interface={next_day=[{name=next_day, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.next_day}}, filters=[{name=id, table_ref=next_day}], interface={result_value=[{name=next_day, table_ref=next_day}], id=[{name=id, table_ref=next_day}]}, table_alias={next_day=query0}}}");
	}

	@Test
	public void contextualKeywordNEXTDAYExistingWildcardQualifierV0Test() {
		final String query = "select next_day.* from events as next_day where next_day.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=next_day}}}, from={table={alias=next_day, table=events}}, where={condition={left={column={name=active, table_ref=next_day}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,48:55='next_day',<436>,1:48]], *=[[@1,7:14='next_day',<436>,1:7]]}}",
				"{query0={*=[[@3,16:16='*',<291>,1:16]]}}",
				"{def_query0={query_dictionary={*=[[@3,16:16='*',<291>,1:16]]}, table_dictionary={events={active=[[@9,48:55='next_day',<436>,1:48]], *=[[@1,7:14='next_day',<436>,1:7]]}}, filters=[{name=active, table_ref=next_day}], interface={*=[{name=*, table_ref=next_day}]}, table_alias={next_day=events}}}");
	}

	@Test
	public void contextualKeywordPREVIOUSDAYExistingContextualNamesV0Test() {
		final String query = "with previous_day as (select a.previous_day as previous_day, a.id from analytics.previous_day a where a.active = 1) select previous_day.previous_day as result_value, previous_day.id from previous_day as previous_day where previous_day.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=previous_day, table_ref=a}, alias=previous_day}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=previous_day}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=previous_day}}, query={select={1={column={name=previous_day, table_ref=previous_day}, alias=result_value}, 2={column={name=id, table_ref=previous_day}}}, from={table={alias=previous_day, table=previous_day}}, where={condition={left={column={name=id, table_ref=previous_day}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.previous_day={previous_day=[[@5,29:29='a',<459>,1:29]], active=[[@20,102:102='a',<459>,1:102]], id=[[@11,61:61='a',<459>,1:61]]}}",
				"{query0={previous_day=[[@9,47:58='previous_day',<437>,1:47], [@27,123:134='previous_day',<437>,1:123]], id=[[@13,63:64='id',<459>,1:63], [@33,166:177='previous_day',<437>,1:166], [@41,222:233='previous_day',<437>,1:222]]}, query1={result_value=[[@31,152:163='result_value',<459>,1:152]], id=[[@35,179:180='id',<459>,1:179]]}}",
				"{def_query1={context_list={previous_day=query0}, query_dictionary={result_value=[[@31,152:163='result_value',<459>,1:152]], id=[[@35,179:180='id',<459>,1:179]]}, def_query0={query_dictionary={previous_day=[[@9,47:58='previous_day',<437>,1:47], [@27,123:134='previous_day',<437>,1:123]], id=[[@13,63:64='id',<459>,1:63], [@33,166:177='previous_day',<437>,1:166], [@41,222:233='previous_day',<437>,1:222]]}, table_dictionary={analytics.previous_day={previous_day=[[@5,29:29='a',<459>,1:29]], active=[[@20,102:102='a',<459>,1:102]], id=[[@11,61:61='a',<459>,1:61]]}}, filters=[{name=active, table_ref=a}], interface={previous_day=[{name=previous_day, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.previous_day}}, filters=[{name=id, table_ref=previous_day}], interface={result_value=[{name=previous_day, table_ref=previous_day}], id=[{name=id, table_ref=previous_day}]}, table_alias={previous_day=query0}}}");
	}

	@Test
	public void contextualKeywordPREVIOUSDAYExistingWildcardQualifierV0Test() {
		final String query = "select previous_day.* from events as previous_day where previous_day.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=previous_day}}}, from={table={alias=previous_day, table=events}}, where={condition={left={column={name=active, table_ref=previous_day}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,56:67='previous_day',<437>,1:56]], *=[[@1,7:18='previous_day',<437>,1:7]]}}",
				"{query0={*=[[@3,20:20='*',<291>,1:20]]}}",
				"{def_query0={query_dictionary={*=[[@3,20:20='*',<291>,1:20]]}, table_dictionary={events={active=[[@9,56:67='previous_day',<437>,1:56]], *=[[@1,7:18='previous_day',<437>,1:7]]}}, filters=[{name=active, table_ref=previous_day}], interface={*=[{name=*, table_ref=previous_day}]}, table_alias={previous_day=events}}}");
	}

	@Test
	public void contextualKeywordDATEBINExistingContextualNamesV0Test() {
		final String query = "with date_bin as (select a.date_bin as date_bin, a.id from analytics.date_bin a where a.active = 1) select date_bin.date_bin as result_value, date_bin.id from date_bin as date_bin where date_bin.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=date_bin, table_ref=a}, alias=date_bin}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=date_bin}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=date_bin}}, query={select={1={column={name=date_bin, table_ref=date_bin}, alias=result_value}, 2={column={name=id, table_ref=date_bin}}}, from={table={alias=date_bin, table=date_bin}}, where={condition={left={column={name=id, table_ref=date_bin}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.date_bin={date_bin=[[@5,25:25='a',<459>,1:25]], active=[[@20,86:86='a',<459>,1:86]], id=[[@11,49:49='a',<459>,1:49]]}}",
				"{query0={date_bin=[[@9,39:46='date_bin',<438>,1:39], [@27,107:114='date_bin',<438>,1:107]], id=[[@13,51:52='id',<459>,1:51], [@33,142:149='date_bin',<438>,1:142], [@41,186:193='date_bin',<438>,1:186]]}, query1={result_value=[[@31,128:139='result_value',<459>,1:128]], id=[[@35,151:152='id',<459>,1:151]]}}",
				"{def_query1={context_list={date_bin=query0}, query_dictionary={result_value=[[@31,128:139='result_value',<459>,1:128]], id=[[@35,151:152='id',<459>,1:151]]}, def_query0={query_dictionary={date_bin=[[@9,39:46='date_bin',<438>,1:39], [@27,107:114='date_bin',<438>,1:107]], id=[[@13,51:52='id',<459>,1:51], [@33,142:149='date_bin',<438>,1:142], [@41,186:193='date_bin',<438>,1:186]]}, table_dictionary={analytics.date_bin={date_bin=[[@5,25:25='a',<459>,1:25]], active=[[@20,86:86='a',<459>,1:86]], id=[[@11,49:49='a',<459>,1:49]]}}, filters=[{name=active, table_ref=a}], interface={date_bin=[{name=date_bin, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.date_bin}}, filters=[{name=id, table_ref=date_bin}], interface={result_value=[{name=date_bin, table_ref=date_bin}], id=[{name=id, table_ref=date_bin}]}, table_alias={date_bin=query0}}}");
	}

	@Test
	public void contextualKeywordDATEBINExistingWildcardQualifierV0Test() {
		final String query = "select date_bin.* from events as date_bin where date_bin.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=date_bin}}}, from={table={alias=date_bin, table=events}}, where={condition={left={column={name=active, table_ref=date_bin}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,48:55='date_bin',<438>,1:48]], *=[[@1,7:14='date_bin',<438>,1:7]]}}",
				"{query0={*=[[@3,16:16='*',<291>,1:16]]}}",
				"{def_query0={query_dictionary={*=[[@3,16:16='*',<291>,1:16]]}, table_dictionary={events={active=[[@9,48:55='date_bin',<438>,1:48]], *=[[@1,7:14='date_bin',<438>,1:7]]}}, filters=[{name=active, table_ref=date_bin}], interface={*=[{name=*, table_ref=date_bin}]}, table_alias={date_bin=events}}}");
	}

	@Test
	public void contextualKeywordAGEExistingContextualNamesV0Test() {
		final String query = "with age as (select a.age as age, a.id from analytics.age a where a.active = 1) select age.age as result_value, age.id from age as age where age.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=age, table_ref=a}, alias=age}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=age}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=age}}, query={select={1={column={name=age, table_ref=age}, alias=result_value}, 2={column={name=id, table_ref=age}}}, from={table={alias=age, table=age}}, where={condition={left={column={name=id, table_ref=age}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.age={active=[[@20,66:66='a',<459>,1:66]], id=[[@11,34:34='a',<459>,1:34]], age=[[@5,20:20='a',<459>,1:20]]}}",
				"{query0={age=[[@9,29:31='age',<439>,1:29], [@27,87:89='age',<439>,1:87]], id=[[@13,36:37='id',<459>,1:36], [@33,112:114='age',<439>,1:112], [@41,141:143='age',<439>,1:141]]}, query1={result_value=[[@31,98:109='result_value',<459>,1:98]], id=[[@35,116:117='id',<459>,1:116]]}}",
				"{def_query1={context_list={age=query0}, query_dictionary={result_value=[[@31,98:109='result_value',<459>,1:98]], id=[[@35,116:117='id',<459>,1:116]]}, def_query0={query_dictionary={id=[[@13,36:37='id',<459>,1:36], [@33,112:114='age',<439>,1:112], [@41,141:143='age',<439>,1:141]], age=[[@9,29:31='age',<439>,1:29], [@27,87:89='age',<439>,1:87]]}, table_dictionary={analytics.age={active=[[@20,66:66='a',<459>,1:66]], id=[[@11,34:34='a',<459>,1:34]], age=[[@5,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], age=[{name=age, table_ref=a}]}, table_alias={a=analytics.age}}, filters=[{name=id, table_ref=age}], interface={result_value=[{name=age, table_ref=age}], id=[{name=id, table_ref=age}]}, table_alias={age=query0}}}");
	}

	@Test
	public void contextualKeywordAGEExistingWildcardQualifierV0Test() {
		final String query = "select age.* from events as age where age.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=age}}}, from={table={alias=age, table=events}}, where={condition={left={column={name=active, table_ref=age}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,38:40='age',<439>,1:38]], *=[[@1,7:9='age',<439>,1:7]]}}",
				"{query0={*=[[@3,11:11='*',<291>,1:11]]}}",
				"{def_query0={query_dictionary={*=[[@3,11:11='*',<291>,1:11]]}, table_dictionary={events={active=[[@9,38:40='age',<439>,1:38]], *=[[@1,7:9='age',<439>,1:7]]}}, filters=[{name=active, table_ref=age}], interface={*=[{name=*, table_ref=age}]}, table_alias={age=events}}}");
	}

	@Test
	public void contextualKeywordMAKEDATEExistingContextualNamesV0Test() {
		final String query = "with make_date as (select a.make_date as make_date, a.id from analytics.make_date a where a.active = 1) select make_date.make_date as result_value, make_date.id from make_date as make_date where make_date.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=make_date, table_ref=a}, alias=make_date}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=make_date}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=make_date}}, query={select={1={column={name=make_date, table_ref=make_date}, alias=result_value}, 2={column={name=id, table_ref=make_date}}}, from={table={alias=make_date, table=make_date}}, where={condition={left={column={name=id, table_ref=make_date}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.make_date={active=[[@20,90:90='a',<459>,1:90]], id=[[@11,52:52='a',<459>,1:52]], make_date=[[@5,26:26='a',<459>,1:26]]}}",
				"{query0={make_date=[[@9,41:49='make_date',<440>,1:41], [@27,111:119='make_date',<440>,1:111]], id=[[@13,54:55='id',<459>,1:54], [@33,148:156='make_date',<440>,1:148], [@41,195:203='make_date',<440>,1:195]]}, query1={result_value=[[@31,134:145='result_value',<459>,1:134]], id=[[@35,158:159='id',<459>,1:158]]}}",
				"{def_query1={context_list={make_date=query0}, query_dictionary={result_value=[[@31,134:145='result_value',<459>,1:134]], id=[[@35,158:159='id',<459>,1:158]]}, def_query0={query_dictionary={id=[[@13,54:55='id',<459>,1:54], [@33,148:156='make_date',<440>,1:148], [@41,195:203='make_date',<440>,1:195]], make_date=[[@9,41:49='make_date',<440>,1:41], [@27,111:119='make_date',<440>,1:111]]}, table_dictionary={analytics.make_date={active=[[@20,90:90='a',<459>,1:90]], id=[[@11,52:52='a',<459>,1:52]], make_date=[[@5,26:26='a',<459>,1:26]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], make_date=[{name=make_date, table_ref=a}]}, table_alias={a=analytics.make_date}}, filters=[{name=id, table_ref=make_date}], interface={result_value=[{name=make_date, table_ref=make_date}], id=[{name=id, table_ref=make_date}]}, table_alias={make_date=query0}}}");
	}

	@Test
	public void contextualKeywordMAKEDATEExistingWildcardQualifierV0Test() {
		final String query = "select make_date.* from events as make_date where make_date.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=make_date}}}, from={table={alias=make_date, table=events}}, where={condition={left={column={name=active, table_ref=make_date}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,50:58='make_date',<440>,1:50]], *=[[@1,7:15='make_date',<440>,1:7]]}}",
				"{query0={*=[[@3,17:17='*',<291>,1:17]]}}",
				"{def_query0={query_dictionary={*=[[@3,17:17='*',<291>,1:17]]}, table_dictionary={events={active=[[@9,50:58='make_date',<440>,1:50]], *=[[@1,7:15='make_date',<440>,1:7]]}}, filters=[{name=active, table_ref=make_date}], interface={*=[{name=*, table_ref=make_date}]}, table_alias={make_date=events}}}");
	}

	@Test
	public void contextualKeywordMAKETIMEExistingContextualNamesV0Test() {
		final String query = "with make_time as (select a.make_time as make_time, a.id from analytics.make_time a where a.active = 1) select make_time.make_time as result_value, make_time.id from make_time as make_time where make_time.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=make_time, table_ref=a}, alias=make_time}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=make_time}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=make_time}}, query={select={1={column={name=make_time, table_ref=make_time}, alias=result_value}, 2={column={name=id, table_ref=make_time}}}, from={table={alias=make_time, table=make_time}}, where={condition={left={column={name=id, table_ref=make_time}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.make_time={make_time=[[@5,26:26='a',<459>,1:26]], active=[[@20,90:90='a',<459>,1:90]], id=[[@11,52:52='a',<459>,1:52]]}}",
				"{query0={make_time=[[@9,41:49='make_time',<441>,1:41], [@27,111:119='make_time',<441>,1:111]], id=[[@13,54:55='id',<459>,1:54], [@33,148:156='make_time',<441>,1:148], [@41,195:203='make_time',<441>,1:195]]}, query1={result_value=[[@31,134:145='result_value',<459>,1:134]], id=[[@35,158:159='id',<459>,1:158]]}}",
				"{def_query1={context_list={make_time=query0}, query_dictionary={result_value=[[@31,134:145='result_value',<459>,1:134]], id=[[@35,158:159='id',<459>,1:158]]}, def_query0={query_dictionary={make_time=[[@9,41:49='make_time',<441>,1:41], [@27,111:119='make_time',<441>,1:111]], id=[[@13,54:55='id',<459>,1:54], [@33,148:156='make_time',<441>,1:148], [@41,195:203='make_time',<441>,1:195]]}, table_dictionary={analytics.make_time={make_time=[[@5,26:26='a',<459>,1:26]], active=[[@20,90:90='a',<459>,1:90]], id=[[@11,52:52='a',<459>,1:52]]}}, filters=[{name=active, table_ref=a}], interface={make_time=[{name=make_time, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.make_time}}, filters=[{name=id, table_ref=make_time}], interface={result_value=[{name=make_time, table_ref=make_time}], id=[{name=id, table_ref=make_time}]}, table_alias={make_time=query0}}}");
	}

	@Test
	public void contextualKeywordMAKETIMEExistingWildcardQualifierV0Test() {
		final String query = "select make_time.* from events as make_time where make_time.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=make_time}}}, from={table={alias=make_time, table=events}}, where={condition={left={column={name=active, table_ref=make_time}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,50:58='make_time',<441>,1:50]], *=[[@1,7:15='make_time',<441>,1:7]]}}",
				"{query0={*=[[@3,17:17='*',<291>,1:17]]}}",
				"{def_query0={query_dictionary={*=[[@3,17:17='*',<291>,1:17]]}, table_dictionary={events={active=[[@9,50:58='make_time',<441>,1:50]], *=[[@1,7:15='make_time',<441>,1:7]]}}, filters=[{name=active, table_ref=make_time}], interface={*=[{name=*, table_ref=make_time}]}, table_alias={make_time=events}}}");
	}

	@Test
	public void contextualKeywordMAKETIMESTAMPExistingContextualNamesV0Test() {
		final String query = "with make_timestamp as (select a.make_timestamp as make_timestamp, a.id from analytics.make_timestamp a where a.active = 1) select make_timestamp.make_timestamp as result_value, make_timestamp.id from make_timestamp as make_timestamp where make_timestamp.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=make_timestamp, table_ref=a}, alias=make_timestamp}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=make_timestamp}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=make_timestamp}}, query={select={1={column={name=make_timestamp, table_ref=make_timestamp}, alias=result_value}, 2={column={name=id, table_ref=make_timestamp}}}, from={table={alias=make_timestamp, table=make_timestamp}}, where={condition={left={column={name=id, table_ref=make_timestamp}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.make_timestamp={active=[[@20,110:110='a',<459>,1:110]], id=[[@11,67:67='a',<459>,1:67]], make_timestamp=[[@5,31:31='a',<459>,1:31]]}}",
				"{query0={make_timestamp=[[@9,51:64='make_timestamp',<442>,1:51], [@27,131:144='make_timestamp',<442>,1:131]], id=[[@13,69:70='id',<459>,1:69], [@33,178:191='make_timestamp',<442>,1:178], [@41,240:253='make_timestamp',<442>,1:240]]}, query1={result_value=[[@31,164:175='result_value',<459>,1:164]], id=[[@35,193:194='id',<459>,1:193]]}}",
				"{def_query1={context_list={make_timestamp=query0}, query_dictionary={result_value=[[@31,164:175='result_value',<459>,1:164]], id=[[@35,193:194='id',<459>,1:193]]}, def_query0={query_dictionary={id=[[@13,69:70='id',<459>,1:69], [@33,178:191='make_timestamp',<442>,1:178], [@41,240:253='make_timestamp',<442>,1:240]], make_timestamp=[[@9,51:64='make_timestamp',<442>,1:51], [@27,131:144='make_timestamp',<442>,1:131]]}, table_dictionary={analytics.make_timestamp={active=[[@20,110:110='a',<459>,1:110]], id=[[@11,67:67='a',<459>,1:67]], make_timestamp=[[@5,31:31='a',<459>,1:31]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], make_timestamp=[{name=make_timestamp, table_ref=a}]}, table_alias={a=analytics.make_timestamp}}, filters=[{name=id, table_ref=make_timestamp}], interface={result_value=[{name=make_timestamp, table_ref=make_timestamp}], id=[{name=id, table_ref=make_timestamp}]}, table_alias={make_timestamp=query0}}}");
	}

	@Test
	public void contextualKeywordMAKETIMESTAMPExistingWildcardQualifierV0Test() {
		final String query = "select make_timestamp.* from events as make_timestamp where make_timestamp.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=make_timestamp}}}, from={table={alias=make_timestamp, table=events}}, where={condition={left={column={name=active, table_ref=make_timestamp}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,60:73='make_timestamp',<442>,1:60]], *=[[@1,7:20='make_timestamp',<442>,1:7]]}}",
				"{query0={*=[[@3,22:22='*',<291>,1:22]]}}",
				"{def_query0={query_dictionary={*=[[@3,22:22='*',<291>,1:22]]}, table_dictionary={events={active=[[@9,60:73='make_timestamp',<442>,1:60]], *=[[@1,7:20='make_timestamp',<442>,1:7]]}}, filters=[{name=active, table_ref=make_timestamp}], interface={*=[{name=*, table_ref=make_timestamp}]}, table_alias={make_timestamp=events}}}");
	}

	@Test
	public void contextualKeywordMAKETIMESTAMPTZExistingContextualNamesV0Test() {
		final String query = "with make_timestamptz as (select a.make_timestamptz as make_timestamptz, a.id from analytics.make_timestamptz a where a.active = 1) select make_timestamptz.make_timestamptz as result_value, make_timestamptz.id from make_timestamptz as make_timestamptz where make_timestamptz.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=make_timestamptz, table_ref=a}, alias=make_timestamptz}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=make_timestamptz}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=make_timestamptz}}, query={select={1={column={name=make_timestamptz, table_ref=make_timestamptz}, alias=result_value}, 2={column={name=id, table_ref=make_timestamptz}}}, from={table={alias=make_timestamptz, table=make_timestamptz}}, where={condition={left={column={name=id, table_ref=make_timestamptz}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.make_timestamptz={make_timestamptz=[[@5,33:33='a',<459>,1:33]], active=[[@20,118:118='a',<459>,1:118]], id=[[@11,73:73='a',<459>,1:73]]}}",
				"{query0={make_timestamptz=[[@9,55:70='make_timestamptz',<443>,1:55], [@27,139:154='make_timestamptz',<443>,1:139]], id=[[@13,75:76='id',<459>,1:75], [@33,190:205='make_timestamptz',<443>,1:190], [@41,258:273='make_timestamptz',<443>,1:258]]}, query1={result_value=[[@31,176:187='result_value',<459>,1:176]], id=[[@35,207:208='id',<459>,1:207]]}}",
				"{def_query1={context_list={make_timestamptz=query0}, query_dictionary={result_value=[[@31,176:187='result_value',<459>,1:176]], id=[[@35,207:208='id',<459>,1:207]]}, def_query0={query_dictionary={make_timestamptz=[[@9,55:70='make_timestamptz',<443>,1:55], [@27,139:154='make_timestamptz',<443>,1:139]], id=[[@13,75:76='id',<459>,1:75], [@33,190:205='make_timestamptz',<443>,1:190], [@41,258:273='make_timestamptz',<443>,1:258]]}, table_dictionary={analytics.make_timestamptz={make_timestamptz=[[@5,33:33='a',<459>,1:33]], active=[[@20,118:118='a',<459>,1:118]], id=[[@11,73:73='a',<459>,1:73]]}}, filters=[{name=active, table_ref=a}], interface={make_timestamptz=[{name=make_timestamptz, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.make_timestamptz}}, filters=[{name=id, table_ref=make_timestamptz}], interface={result_value=[{name=make_timestamptz, table_ref=make_timestamptz}], id=[{name=id, table_ref=make_timestamptz}]}, table_alias={make_timestamptz=query0}}}");
	}

	@Test
	public void contextualKeywordMAKETIMESTAMPTZExistingWildcardQualifierV0Test() {
		final String query = "select make_timestamptz.* from events as make_timestamptz where make_timestamptz.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=make_timestamptz}}}, from={table={alias=make_timestamptz, table=events}}, where={condition={left={column={name=active, table_ref=make_timestamptz}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,64:79='make_timestamptz',<443>,1:64]], *=[[@1,7:22='make_timestamptz',<443>,1:7]]}}",
				"{query0={*=[[@3,24:24='*',<291>,1:24]]}}",
				"{def_query0={query_dictionary={*=[[@3,24:24='*',<291>,1:24]]}, table_dictionary={events={active=[[@9,64:79='make_timestamptz',<443>,1:64]], *=[[@1,7:22='make_timestamptz',<443>,1:7]]}}, filters=[{name=active, table_ref=make_timestamptz}], interface={*=[{name=*, table_ref=make_timestamptz}]}, table_alias={make_timestamptz=events}}}");
	}

	@Test
	public void contextualKeywordMAKEINTERVALExistingContextualNamesV0Test() {
		final String query = "with make_interval as (select a.make_interval as make_interval, a.id from analytics.make_interval a where a.active = 1) select make_interval.make_interval as result_value, make_interval.id from make_interval as make_interval where make_interval.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=make_interval, table_ref=a}, alias=make_interval}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=make_interval}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=make_interval}}, query={select={1={column={name=make_interval, table_ref=make_interval}, alias=result_value}, 2={column={name=id, table_ref=make_interval}}}, from={table={alias=make_interval, table=make_interval}}, where={condition={left={column={name=id, table_ref=make_interval}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.make_interval={make_interval=[[@5,30:30='a',<459>,1:30]], active=[[@20,106:106='a',<459>,1:106]], id=[[@11,64:64='a',<459>,1:64]]}}",
				"{query0={make_interval=[[@9,49:61='make_interval',<444>,1:49], [@27,127:139='make_interval',<444>,1:127]], id=[[@13,66:67='id',<459>,1:66], [@33,172:184='make_interval',<444>,1:172], [@41,231:243='make_interval',<444>,1:231]]}, query1={result_value=[[@31,158:169='result_value',<459>,1:158]], id=[[@35,186:187='id',<459>,1:186]]}}",
				"{def_query1={context_list={make_interval=query0}, query_dictionary={result_value=[[@31,158:169='result_value',<459>,1:158]], id=[[@35,186:187='id',<459>,1:186]]}, def_query0={query_dictionary={make_interval=[[@9,49:61='make_interval',<444>,1:49], [@27,127:139='make_interval',<444>,1:127]], id=[[@13,66:67='id',<459>,1:66], [@33,172:184='make_interval',<444>,1:172], [@41,231:243='make_interval',<444>,1:231]]}, table_dictionary={analytics.make_interval={make_interval=[[@5,30:30='a',<459>,1:30]], active=[[@20,106:106='a',<459>,1:106]], id=[[@11,64:64='a',<459>,1:64]]}}, filters=[{name=active, table_ref=a}], interface={make_interval=[{name=make_interval, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.make_interval}}, filters=[{name=id, table_ref=make_interval}], interface={result_value=[{name=make_interval, table_ref=make_interval}], id=[{name=id, table_ref=make_interval}]}, table_alias={make_interval=query0}}}");
	}

	@Test
	public void contextualKeywordMAKEINTERVALExistingWildcardQualifierV0Test() {
		final String query = "select make_interval.* from events as make_interval where make_interval.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=make_interval}}}, from={table={alias=make_interval, table=events}}, where={condition={left={column={name=active, table_ref=make_interval}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,58:70='make_interval',<444>,1:58]], *=[[@1,7:19='make_interval',<444>,1:7]]}}",
				"{query0={*=[[@3,21:21='*',<291>,1:21]]}}",
				"{def_query0={query_dictionary={*=[[@3,21:21='*',<291>,1:21]]}, table_dictionary={events={active=[[@9,58:70='make_interval',<444>,1:58]], *=[[@1,7:19='make_interval',<444>,1:7]]}}, filters=[{name=active, table_ref=make_interval}], interface={*=[{name=*, table_ref=make_interval}]}, table_alias={make_interval=events}}}");
	}

	@Test
	public void contextualKeywordJUSTIFYDAYSExistingContextualNamesV0Test() {
		final String query = "with justify_days as (select a.justify_days as justify_days, a.id from analytics.justify_days a where a.active = 1) select justify_days.justify_days as result_value, justify_days.id from justify_days as justify_days where justify_days.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=justify_days, table_ref=a}, alias=justify_days}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=justify_days}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=justify_days}}, query={select={1={column={name=justify_days, table_ref=justify_days}, alias=result_value}, 2={column={name=id, table_ref=justify_days}}}, from={table={alias=justify_days, table=justify_days}}, where={condition={left={column={name=id, table_ref=justify_days}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.justify_days={justify_days=[[@5,29:29='a',<459>,1:29]], active=[[@20,102:102='a',<459>,1:102]], id=[[@11,61:61='a',<459>,1:61]]}}",
				"{query0={justify_days=[[@9,47:58='justify_days',<445>,1:47], [@27,123:134='justify_days',<445>,1:123]], id=[[@13,63:64='id',<459>,1:63], [@33,166:177='justify_days',<445>,1:166], [@41,222:233='justify_days',<445>,1:222]]}, query1={result_value=[[@31,152:163='result_value',<459>,1:152]], id=[[@35,179:180='id',<459>,1:179]]}}",
				"{def_query1={context_list={justify_days=query0}, query_dictionary={result_value=[[@31,152:163='result_value',<459>,1:152]], id=[[@35,179:180='id',<459>,1:179]]}, def_query0={query_dictionary={justify_days=[[@9,47:58='justify_days',<445>,1:47], [@27,123:134='justify_days',<445>,1:123]], id=[[@13,63:64='id',<459>,1:63], [@33,166:177='justify_days',<445>,1:166], [@41,222:233='justify_days',<445>,1:222]]}, table_dictionary={analytics.justify_days={justify_days=[[@5,29:29='a',<459>,1:29]], active=[[@20,102:102='a',<459>,1:102]], id=[[@11,61:61='a',<459>,1:61]]}}, filters=[{name=active, table_ref=a}], interface={justify_days=[{name=justify_days, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.justify_days}}, filters=[{name=id, table_ref=justify_days}], interface={result_value=[{name=justify_days, table_ref=justify_days}], id=[{name=id, table_ref=justify_days}]}, table_alias={justify_days=query0}}}");
	}

	@Test
	public void contextualKeywordJUSTIFYDAYSExistingWildcardQualifierV0Test() {
		final String query = "select justify_days.* from events as justify_days where justify_days.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=justify_days}}}, from={table={alias=justify_days, table=events}}, where={condition={left={column={name=active, table_ref=justify_days}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,56:67='justify_days',<445>,1:56]], *=[[@1,7:18='justify_days',<445>,1:7]]}}",
				"{query0={*=[[@3,20:20='*',<291>,1:20]]}}",
				"{def_query0={query_dictionary={*=[[@3,20:20='*',<291>,1:20]]}, table_dictionary={events={active=[[@9,56:67='justify_days',<445>,1:56]], *=[[@1,7:18='justify_days',<445>,1:7]]}}, filters=[{name=active, table_ref=justify_days}], interface={*=[{name=*, table_ref=justify_days}]}, table_alias={justify_days=events}}}");
	}

	@Test
	public void contextualKeywordJUSTIFYHOURSExistingContextualNamesV0Test() {
		final String query = "with justify_hours as (select a.justify_hours as justify_hours, a.id from analytics.justify_hours a where a.active = 1) select justify_hours.justify_hours as result_value, justify_hours.id from justify_hours as justify_hours where justify_hours.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=justify_hours, table_ref=a}, alias=justify_hours}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=justify_hours}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=justify_hours}}, query={select={1={column={name=justify_hours, table_ref=justify_hours}, alias=result_value}, 2={column={name=id, table_ref=justify_hours}}}, from={table={alias=justify_hours, table=justify_hours}}, where={condition={left={column={name=id, table_ref=justify_hours}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.justify_hours={active=[[@20,106:106='a',<459>,1:106]], justify_hours=[[@5,30:30='a',<459>,1:30]], id=[[@11,64:64='a',<459>,1:64]]}}",
				"{query0={justify_hours=[[@9,49:61='justify_hours',<446>,1:49], [@27,127:139='justify_hours',<446>,1:127]], id=[[@13,66:67='id',<459>,1:66], [@33,172:184='justify_hours',<446>,1:172], [@41,231:243='justify_hours',<446>,1:231]]}, query1={result_value=[[@31,158:169='result_value',<459>,1:158]], id=[[@35,186:187='id',<459>,1:186]]}}",
				"{def_query1={context_list={justify_hours=query0}, query_dictionary={result_value=[[@31,158:169='result_value',<459>,1:158]], id=[[@35,186:187='id',<459>,1:186]]}, def_query0={query_dictionary={justify_hours=[[@9,49:61='justify_hours',<446>,1:49], [@27,127:139='justify_hours',<446>,1:127]], id=[[@13,66:67='id',<459>,1:66], [@33,172:184='justify_hours',<446>,1:172], [@41,231:243='justify_hours',<446>,1:231]]}, table_dictionary={analytics.justify_hours={active=[[@20,106:106='a',<459>,1:106]], justify_hours=[[@5,30:30='a',<459>,1:30]], id=[[@11,64:64='a',<459>,1:64]]}}, filters=[{name=active, table_ref=a}], interface={justify_hours=[{name=justify_hours, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.justify_hours}}, filters=[{name=id, table_ref=justify_hours}], interface={result_value=[{name=justify_hours, table_ref=justify_hours}], id=[{name=id, table_ref=justify_hours}]}, table_alias={justify_hours=query0}}}");
	}

	@Test
	public void contextualKeywordJUSTIFYHOURSExistingWildcardQualifierV0Test() {
		final String query = "select justify_hours.* from events as justify_hours where justify_hours.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=justify_hours}}}, from={table={alias=justify_hours, table=events}}, where={condition={left={column={name=active, table_ref=justify_hours}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,58:70='justify_hours',<446>,1:58]], *=[[@1,7:19='justify_hours',<446>,1:7]]}}",
				"{query0={*=[[@3,21:21='*',<291>,1:21]]}}",
				"{def_query0={query_dictionary={*=[[@3,21:21='*',<291>,1:21]]}, table_dictionary={events={active=[[@9,58:70='justify_hours',<446>,1:58]], *=[[@1,7:19='justify_hours',<446>,1:7]]}}, filters=[{name=active, table_ref=justify_hours}], interface={*=[{name=*, table_ref=justify_hours}]}, table_alias={justify_hours=events}}}");
	}

	@Test
	public void contextualKeywordJUSTIFYINTERVALExistingContextualNamesV0Test() {
		final String query = "with justify_interval as (select a.justify_interval as justify_interval, a.id from analytics.justify_interval a where a.active = 1) select justify_interval.justify_interval as result_value, justify_interval.id from justify_interval as justify_interval where justify_interval.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=justify_interval, table_ref=a}, alias=justify_interval}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=justify_interval}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=justify_interval}}, query={select={1={column={name=justify_interval, table_ref=justify_interval}, alias=result_value}, 2={column={name=id, table_ref=justify_interval}}}, from={table={alias=justify_interval, table=justify_interval}}, where={condition={left={column={name=id, table_ref=justify_interval}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.justify_interval={justify_interval=[[@5,33:33='a',<459>,1:33]], active=[[@20,118:118='a',<459>,1:118]], id=[[@11,73:73='a',<459>,1:73]]}}",
				"{query0={justify_interval=[[@9,55:70='justify_interval',<447>,1:55], [@27,139:154='justify_interval',<447>,1:139]], id=[[@13,75:76='id',<459>,1:75], [@33,190:205='justify_interval',<447>,1:190], [@41,258:273='justify_interval',<447>,1:258]]}, query1={result_value=[[@31,176:187='result_value',<459>,1:176]], id=[[@35,207:208='id',<459>,1:207]]}}",
				"{def_query1={context_list={justify_interval=query0}, query_dictionary={result_value=[[@31,176:187='result_value',<459>,1:176]], id=[[@35,207:208='id',<459>,1:207]]}, def_query0={query_dictionary={justify_interval=[[@9,55:70='justify_interval',<447>,1:55], [@27,139:154='justify_interval',<447>,1:139]], id=[[@13,75:76='id',<459>,1:75], [@33,190:205='justify_interval',<447>,1:190], [@41,258:273='justify_interval',<447>,1:258]]}, table_dictionary={analytics.justify_interval={justify_interval=[[@5,33:33='a',<459>,1:33]], active=[[@20,118:118='a',<459>,1:118]], id=[[@11,73:73='a',<459>,1:73]]}}, filters=[{name=active, table_ref=a}], interface={justify_interval=[{name=justify_interval, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.justify_interval}}, filters=[{name=id, table_ref=justify_interval}], interface={result_value=[{name=justify_interval, table_ref=justify_interval}], id=[{name=id, table_ref=justify_interval}]}, table_alias={justify_interval=query0}}}");
	}

	@Test
	public void contextualKeywordJUSTIFYINTERVALExistingWildcardQualifierV0Test() {
		final String query = "select justify_interval.* from events as justify_interval where justify_interval.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=justify_interval}}}, from={table={alias=justify_interval, table=events}}, where={condition={left={column={name=active, table_ref=justify_interval}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,64:79='justify_interval',<447>,1:64]], *=[[@1,7:22='justify_interval',<447>,1:7]]}}",
				"{query0={*=[[@3,24:24='*',<291>,1:24]]}}",
				"{def_query0={query_dictionary={*=[[@3,24:24='*',<291>,1:24]]}, table_dictionary={events={active=[[@9,64:79='justify_interval',<447>,1:64]], *=[[@1,7:22='justify_interval',<447>,1:7]]}}, filters=[{name=active, table_ref=justify_interval}], interface={*=[{name=*, table_ref=justify_interval}]}, table_alias={justify_interval=events}}}");
	}

	@Test
	public void contextualKeywordCLOCKTIMESTAMPExistingContextualNamesV0Test() {
		final String query = "with clock_timestamp as (select a.clock_timestamp as clock_timestamp, a.id from analytics.clock_timestamp a where a.active = 1) select clock_timestamp.clock_timestamp as result_value, clock_timestamp.id from clock_timestamp as clock_timestamp where clock_timestamp.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=clock_timestamp, table_ref=a}, alias=clock_timestamp}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=clock_timestamp}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=clock_timestamp}}, query={select={1={column={name=clock_timestamp, table_ref=clock_timestamp}, alias=result_value}, 2={column={name=id, table_ref=clock_timestamp}}}, from={table={alias=clock_timestamp, table=clock_timestamp}}, where={condition={left={column={name=id, table_ref=clock_timestamp}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.clock_timestamp={active=[[@20,114:114='a',<459>,1:114]], id=[[@11,70:70='a',<459>,1:70]], clock_timestamp=[[@5,32:32='a',<459>,1:32]]}}",
				"{query0={clock_timestamp=[[@9,53:67='clock_timestamp',<448>,1:53], [@27,135:149='clock_timestamp',<448>,1:135]], id=[[@13,72:73='id',<459>,1:72], [@33,184:198='clock_timestamp',<448>,1:184], [@41,249:263='clock_timestamp',<448>,1:249]]}, query1={result_value=[[@31,170:181='result_value',<459>,1:170]], id=[[@35,200:201='id',<459>,1:200]]}}",
				"{def_query1={context_list={clock_timestamp=query0}, query_dictionary={result_value=[[@31,170:181='result_value',<459>,1:170]], id=[[@35,200:201='id',<459>,1:200]]}, def_query0={query_dictionary={clock_timestamp=[[@9,53:67='clock_timestamp',<448>,1:53], [@27,135:149='clock_timestamp',<448>,1:135]], id=[[@13,72:73='id',<459>,1:72], [@33,184:198='clock_timestamp',<448>,1:184], [@41,249:263='clock_timestamp',<448>,1:249]]}, table_dictionary={analytics.clock_timestamp={active=[[@20,114:114='a',<459>,1:114]], id=[[@11,70:70='a',<459>,1:70]], clock_timestamp=[[@5,32:32='a',<459>,1:32]]}}, filters=[{name=active, table_ref=a}], interface={clock_timestamp=[{name=clock_timestamp, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.clock_timestamp}}, filters=[{name=id, table_ref=clock_timestamp}], interface={result_value=[{name=clock_timestamp, table_ref=clock_timestamp}], id=[{name=id, table_ref=clock_timestamp}]}, table_alias={clock_timestamp=query0}}}");
	}

	@Test
	public void contextualKeywordCLOCKTIMESTAMPExistingWildcardQualifierV0Test() {
		final String query = "select clock_timestamp.* from events as clock_timestamp where clock_timestamp.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=clock_timestamp}}}, from={table={alias=clock_timestamp, table=events}}, where={condition={left={column={name=active, table_ref=clock_timestamp}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,62:76='clock_timestamp',<448>,1:62]], *=[[@1,7:21='clock_timestamp',<448>,1:7]]}}",
				"{query0={*=[[@3,23:23='*',<291>,1:23]]}}",
				"{def_query0={query_dictionary={*=[[@3,23:23='*',<291>,1:23]]}, table_dictionary={events={active=[[@9,62:76='clock_timestamp',<448>,1:62]], *=[[@1,7:21='clock_timestamp',<448>,1:7]]}}, filters=[{name=active, table_ref=clock_timestamp}], interface={*=[{name=*, table_ref=clock_timestamp}]}, table_alias={clock_timestamp=events}}}");
	}

	@Test
	public void contextualKeywordSTATEMENTTIMESTAMPExistingContextualNamesV0Test() {
		final String query = "with statement_timestamp as (select a.statement_timestamp as statement_timestamp, a.id from analytics.statement_timestamp a where a.active = 1) select statement_timestamp.statement_timestamp as result_value, statement_timestamp.id from statement_timestamp as statement_timestamp where statement_timestamp.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=statement_timestamp, table_ref=a}, alias=statement_timestamp}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=statement_timestamp}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=statement_timestamp}}, query={select={1={column={name=statement_timestamp, table_ref=statement_timestamp}, alias=result_value}, 2={column={name=id, table_ref=statement_timestamp}}}, from={table={alias=statement_timestamp, table=statement_timestamp}}, where={condition={left={column={name=id, table_ref=statement_timestamp}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.statement_timestamp={active=[[@20,130:130='a',<459>,1:130]], id=[[@11,82:82='a',<459>,1:82]], statement_timestamp=[[@5,36:36='a',<459>,1:36]]}}",
				"{query0={statement_timestamp=[[@9,61:79='statement_timestamp',<449>,1:61], [@27,151:169='statement_timestamp',<449>,1:151]], id=[[@13,84:85='id',<459>,1:84], [@33,208:226='statement_timestamp',<449>,1:208], [@41,285:303='statement_timestamp',<449>,1:285]]}, query1={result_value=[[@31,194:205='result_value',<459>,1:194]], id=[[@35,228:229='id',<459>,1:228]]}}",
				"{def_query1={context_list={statement_timestamp=query0}, query_dictionary={result_value=[[@31,194:205='result_value',<459>,1:194]], id=[[@35,228:229='id',<459>,1:228]]}, def_query0={query_dictionary={id=[[@13,84:85='id',<459>,1:84], [@33,208:226='statement_timestamp',<449>,1:208], [@41,285:303='statement_timestamp',<449>,1:285]], statement_timestamp=[[@9,61:79='statement_timestamp',<449>,1:61], [@27,151:169='statement_timestamp',<449>,1:151]]}, table_dictionary={analytics.statement_timestamp={active=[[@20,130:130='a',<459>,1:130]], id=[[@11,82:82='a',<459>,1:82]], statement_timestamp=[[@5,36:36='a',<459>,1:36]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], statement_timestamp=[{name=statement_timestamp, table_ref=a}]}, table_alias={a=analytics.statement_timestamp}}, filters=[{name=id, table_ref=statement_timestamp}], interface={result_value=[{name=statement_timestamp, table_ref=statement_timestamp}], id=[{name=id, table_ref=statement_timestamp}]}, table_alias={statement_timestamp=query0}}}");
	}

	@Test
	public void contextualKeywordSTATEMENTTIMESTAMPExistingWildcardQualifierV0Test() {
		final String query = "select statement_timestamp.* from events as statement_timestamp where statement_timestamp.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=statement_timestamp}}}, from={table={alias=statement_timestamp, table=events}}, where={condition={left={column={name=active, table_ref=statement_timestamp}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,70:88='statement_timestamp',<449>,1:70]], *=[[@1,7:25='statement_timestamp',<449>,1:7]]}}",
				"{query0={*=[[@3,27:27='*',<291>,1:27]]}}",
				"{def_query0={query_dictionary={*=[[@3,27:27='*',<291>,1:27]]}, table_dictionary={events={active=[[@9,70:88='statement_timestamp',<449>,1:70]], *=[[@1,7:25='statement_timestamp',<449>,1:7]]}}, filters=[{name=active, table_ref=statement_timestamp}], interface={*=[{name=*, table_ref=statement_timestamp}]}, table_alias={statement_timestamp=events}}}");
	}

	@Test
	public void contextualKeywordTRANSACTIONTIMESTAMPExistingContextualNamesV0Test() {
		final String query = "with transaction_timestamp as (select a.transaction_timestamp as transaction_timestamp, a.id from analytics.transaction_timestamp a where a.active = 1) select transaction_timestamp.transaction_timestamp as result_value, transaction_timestamp.id from transaction_timestamp as transaction_timestamp where transaction_timestamp.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=transaction_timestamp, table_ref=a}, alias=transaction_timestamp}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=transaction_timestamp}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=transaction_timestamp}}, query={select={1={column={name=transaction_timestamp, table_ref=transaction_timestamp}, alias=result_value}, 2={column={name=id, table_ref=transaction_timestamp}}}, from={table={alias=transaction_timestamp, table=transaction_timestamp}}, where={condition={left={column={name=id, table_ref=transaction_timestamp}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.transaction_timestamp={active=[[@20,138:138='a',<459>,1:138]], transaction_timestamp=[[@5,38:38='a',<459>,1:38]], id=[[@11,88:88='a',<459>,1:88]]}}",
				"{query0={transaction_timestamp=[[@9,65:85='transaction_timestamp',<450>,1:65], [@27,159:179='transaction_timestamp',<450>,1:159]], id=[[@13,90:91='id',<459>,1:90], [@33,220:240='transaction_timestamp',<450>,1:220], [@41,303:323='transaction_timestamp',<450>,1:303]]}, query1={result_value=[[@31,206:217='result_value',<459>,1:206]], id=[[@35,242:243='id',<459>,1:242]]}}",
				"{def_query1={context_list={transaction_timestamp=query0}, query_dictionary={result_value=[[@31,206:217='result_value',<459>,1:206]], id=[[@35,242:243='id',<459>,1:242]]}, def_query0={query_dictionary={transaction_timestamp=[[@9,65:85='transaction_timestamp',<450>,1:65], [@27,159:179='transaction_timestamp',<450>,1:159]], id=[[@13,90:91='id',<459>,1:90], [@33,220:240='transaction_timestamp',<450>,1:220], [@41,303:323='transaction_timestamp',<450>,1:303]]}, table_dictionary={analytics.transaction_timestamp={active=[[@20,138:138='a',<459>,1:138]], transaction_timestamp=[[@5,38:38='a',<459>,1:38]], id=[[@11,88:88='a',<459>,1:88]]}}, filters=[{name=active, table_ref=a}], interface={transaction_timestamp=[{name=transaction_timestamp, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.transaction_timestamp}}, filters=[{name=id, table_ref=transaction_timestamp}], interface={result_value=[{name=transaction_timestamp, table_ref=transaction_timestamp}], id=[{name=id, table_ref=transaction_timestamp}]}, table_alias={transaction_timestamp=query0}}}");
	}

	@Test
	public void contextualKeywordTRANSACTIONTIMESTAMPExistingWildcardQualifierV0Test() {
		final String query = "select transaction_timestamp.* from events as transaction_timestamp where transaction_timestamp.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=transaction_timestamp}}}, from={table={alias=transaction_timestamp, table=events}}, where={condition={left={column={name=active, table_ref=transaction_timestamp}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,74:94='transaction_timestamp',<450>,1:74]], *=[[@1,7:27='transaction_timestamp',<450>,1:7]]}}",
				"{query0={*=[[@3,29:29='*',<291>,1:29]]}}",
				"{def_query0={query_dictionary={*=[[@3,29:29='*',<291>,1:29]]}, table_dictionary={events={active=[[@9,74:94='transaction_timestamp',<450>,1:74]], *=[[@1,7:27='transaction_timestamp',<450>,1:7]]}}, filters=[{name=active, table_ref=transaction_timestamp}], interface={*=[{name=*, table_ref=transaction_timestamp}]}, table_alias={transaction_timestamp=events}}}");
	}

	@Test
	public void contextualKeywordTIMEOFDAYExistingContextualNamesV0Test() {
		final String query = "with timeofday as (select a.timeofday as timeofday, a.id from analytics.timeofday a where a.active = 1) select timeofday.timeofday as result_value, timeofday.id from timeofday as timeofday where timeofday.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=timeofday, table_ref=a}, alias=timeofday}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=timeofday}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=timeofday}}, query={select={1={column={name=timeofday, table_ref=timeofday}, alias=result_value}, 2={column={name=id, table_ref=timeofday}}}, from={table={alias=timeofday, table=timeofday}}, where={condition={left={column={name=id, table_ref=timeofday}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.timeofday={timeofday=[[@5,26:26='a',<459>,1:26]], active=[[@20,90:90='a',<459>,1:90]], id=[[@11,52:52='a',<459>,1:52]]}}",
				"{query0={timeofday=[[@9,41:49='timeofday',<451>,1:41], [@27,111:119='timeofday',<451>,1:111]], id=[[@13,54:55='id',<459>,1:54], [@33,148:156='timeofday',<451>,1:148], [@41,195:203='timeofday',<451>,1:195]]}, query1={result_value=[[@31,134:145='result_value',<459>,1:134]], id=[[@35,158:159='id',<459>,1:158]]}}",
				"{def_query1={context_list={timeofday=query0}, query_dictionary={result_value=[[@31,134:145='result_value',<459>,1:134]], id=[[@35,158:159='id',<459>,1:158]]}, def_query0={query_dictionary={timeofday=[[@9,41:49='timeofday',<451>,1:41], [@27,111:119='timeofday',<451>,1:111]], id=[[@13,54:55='id',<459>,1:54], [@33,148:156='timeofday',<451>,1:148], [@41,195:203='timeofday',<451>,1:195]]}, table_dictionary={analytics.timeofday={timeofday=[[@5,26:26='a',<459>,1:26]], active=[[@20,90:90='a',<459>,1:90]], id=[[@11,52:52='a',<459>,1:52]]}}, filters=[{name=active, table_ref=a}], interface={timeofday=[{name=timeofday, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.timeofday}}, filters=[{name=id, table_ref=timeofday}], interface={result_value=[{name=timeofday, table_ref=timeofday}], id=[{name=id, table_ref=timeofday}]}, table_alias={timeofday=query0}}}");
	}

	@Test
	public void contextualKeywordTIMEOFDAYExistingWildcardQualifierV0Test() {
		final String query = "select timeofday.* from events as timeofday where timeofday.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=timeofday}}}, from={table={alias=timeofday, table=events}}, where={condition={left={column={name=active, table_ref=timeofday}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,50:58='timeofday',<451>,1:50]], *=[[@1,7:15='timeofday',<451>,1:7]]}}",
				"{query0={*=[[@3,17:17='*',<291>,1:17]]}}",
				"{def_query0={query_dictionary={*=[[@3,17:17='*',<291>,1:17]]}, table_dictionary={events={active=[[@9,50:58='timeofday',<451>,1:50]], *=[[@1,7:15='timeofday',<451>,1:7]]}}, filters=[{name=active, table_ref=timeofday}], interface={*=[{name=*, table_ref=timeofday}]}, table_alias={timeofday=events}}}");
	}

	@Test
	public void contextualKeywordARRAYREVERSEExistingContextualNamesV0Test() {
		final String query = "with array_reverse as (select a.array_reverse as array_reverse, a.id from analytics.array_reverse a where a.active = 1) select array_reverse.array_reverse as result_value, array_reverse.id from array_reverse as array_reverse where array_reverse.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_reverse, table_ref=a}, alias=array_reverse}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_reverse}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_reverse}}, query={select={1={column={name=array_reverse, table_ref=array_reverse}, alias=result_value}, 2={column={name=id, table_ref=array_reverse}}}, from={table={alias=array_reverse, table=array_reverse}}, where={condition={left={column={name=id, table_ref=array_reverse}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_reverse={active=[[@20,106:106='a',<459>,1:106]], id=[[@11,64:64='a',<459>,1:64]], array_reverse=[[@5,30:30='a',<459>,1:30]]}}",
				"{query0={id=[[@13,66:67='id',<459>,1:66], [@33,172:184='array_reverse',<452>,1:172], [@41,231:243='array_reverse',<452>,1:231]], array_reverse=[[@9,49:61='array_reverse',<452>,1:49], [@27,127:139='array_reverse',<452>,1:127]]}, query1={result_value=[[@31,158:169='result_value',<459>,1:158]], id=[[@35,186:187='id',<459>,1:186]]}}",
				"{def_query1={context_list={array_reverse=query0}, query_dictionary={result_value=[[@31,158:169='result_value',<459>,1:158]], id=[[@35,186:187='id',<459>,1:186]]}, def_query0={query_dictionary={id=[[@13,66:67='id',<459>,1:66], [@33,172:184='array_reverse',<452>,1:172], [@41,231:243='array_reverse',<452>,1:231]], array_reverse=[[@9,49:61='array_reverse',<452>,1:49], [@27,127:139='array_reverse',<452>,1:127]]}, table_dictionary={analytics.array_reverse={active=[[@20,106:106='a',<459>,1:106]], id=[[@11,64:64='a',<459>,1:64]], array_reverse=[[@5,30:30='a',<459>,1:30]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], array_reverse=[{name=array_reverse, table_ref=a}]}, table_alias={a=analytics.array_reverse}}, filters=[{name=id, table_ref=array_reverse}], interface={result_value=[{name=array_reverse, table_ref=array_reverse}], id=[{name=id, table_ref=array_reverse}]}, table_alias={array_reverse=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYREVERSEExistingWildcardQualifierV0Test() {
		final String query = "select array_reverse.* from events as array_reverse where array_reverse.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_reverse}}}, from={table={alias=array_reverse, table=events}}, where={condition={left={column={name=active, table_ref=array_reverse}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,58:70='array_reverse',<452>,1:58]], *=[[@1,7:19='array_reverse',<452>,1:7]]}}",
				"{query0={*=[[@3,21:21='*',<291>,1:21]]}}",
				"{def_query0={query_dictionary={*=[[@3,21:21='*',<291>,1:21]]}, table_dictionary={events={active=[[@9,58:70='array_reverse',<452>,1:58]], *=[[@1,7:19='array_reverse',<452>,1:7]]}}, filters=[{name=active, table_ref=array_reverse}], interface={*=[{name=*, table_ref=array_reverse}]}, table_alias={array_reverse=events}}}");
	}

	@Test
	public void contextualKeywordARRAYSUMExistingContextualNamesV0Test() {
		final String query = "with array_sum as (select a.array_sum as array_sum, a.id from analytics.array_sum a where a.active = 1) select array_sum.array_sum as result_value, array_sum.id from array_sum as array_sum where array_sum.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_sum, table_ref=a}, alias=array_sum}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_sum}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_sum}}, query={select={1={column={name=array_sum, table_ref=array_sum}, alias=result_value}, 2={column={name=id, table_ref=array_sum}}}, from={table={alias=array_sum, table=array_sum}}, where={condition={left={column={name=id, table_ref=array_sum}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_sum={array_sum=[[@5,26:26='a',<459>,1:26]], active=[[@20,90:90='a',<459>,1:90]], id=[[@11,52:52='a',<459>,1:52]]}}",
				"{query0={array_sum=[[@9,41:49='array_sum',<453>,1:41], [@27,111:119='array_sum',<453>,1:111]], id=[[@13,54:55='id',<459>,1:54], [@33,148:156='array_sum',<453>,1:148], [@41,195:203='array_sum',<453>,1:195]]}, query1={result_value=[[@31,134:145='result_value',<459>,1:134]], id=[[@35,158:159='id',<459>,1:158]]}}",
				"{def_query1={context_list={array_sum=query0}, query_dictionary={result_value=[[@31,134:145='result_value',<459>,1:134]], id=[[@35,158:159='id',<459>,1:158]]}, def_query0={query_dictionary={array_sum=[[@9,41:49='array_sum',<453>,1:41], [@27,111:119='array_sum',<453>,1:111]], id=[[@13,54:55='id',<459>,1:54], [@33,148:156='array_sum',<453>,1:148], [@41,195:203='array_sum',<453>,1:195]]}, table_dictionary={analytics.array_sum={array_sum=[[@5,26:26='a',<459>,1:26]], active=[[@20,90:90='a',<459>,1:90]], id=[[@11,52:52='a',<459>,1:52]]}}, filters=[{name=active, table_ref=a}], interface={array_sum=[{name=array_sum, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.array_sum}}, filters=[{name=id, table_ref=array_sum}], interface={result_value=[{name=array_sum, table_ref=array_sum}], id=[{name=id, table_ref=array_sum}]}, table_alias={array_sum=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYSUMExistingWildcardQualifierV0Test() {
		final String query = "select array_sum.* from events as array_sum where array_sum.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_sum}}}, from={table={alias=array_sum, table=events}}, where={condition={left={column={name=active, table_ref=array_sum}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,50:58='array_sum',<453>,1:50]], *=[[@1,7:15='array_sum',<453>,1:7]]}}",
				"{query0={*=[[@3,17:17='*',<291>,1:17]]}}",
				"{def_query0={query_dictionary={*=[[@3,17:17='*',<291>,1:17]]}, table_dictionary={events={active=[[@9,50:58='array_sum',<453>,1:50]], *=[[@1,7:15='array_sum',<453>,1:7]]}}, filters=[{name=active, table_ref=array_sum}], interface={*=[{name=*, table_ref=array_sum}]}, table_alias={array_sum=events}}}");
	}

	@Test
	public void contextualKeywordARRAYSTOOBJECTExistingContextualNamesV0Test() {
		final String query = "with arrays_to_object as (select a.arrays_to_object as arrays_to_object, a.id from analytics.arrays_to_object a where a.active = 1) select arrays_to_object.arrays_to_object as result_value, arrays_to_object.id from arrays_to_object as arrays_to_object where arrays_to_object.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=arrays_to_object, table_ref=a}, alias=arrays_to_object}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=arrays_to_object}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=arrays_to_object}}, query={select={1={column={name=arrays_to_object, table_ref=arrays_to_object}, alias=result_value}, 2={column={name=id, table_ref=arrays_to_object}}}, from={table={alias=arrays_to_object, table=arrays_to_object}}, where={condition={left={column={name=id, table_ref=arrays_to_object}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.arrays_to_object={arrays_to_object=[[@5,33:33='a',<459>,1:33]], active=[[@20,118:118='a',<459>,1:118]], id=[[@11,73:73='a',<459>,1:73]]}}",
				"{query0={arrays_to_object=[[@9,55:70='arrays_to_object',<454>,1:55], [@27,139:154='arrays_to_object',<454>,1:139]], id=[[@13,75:76='id',<459>,1:75], [@33,190:205='arrays_to_object',<454>,1:190], [@41,258:273='arrays_to_object',<454>,1:258]]}, query1={result_value=[[@31,176:187='result_value',<459>,1:176]], id=[[@35,207:208='id',<459>,1:207]]}}",
				"{def_query1={context_list={arrays_to_object=query0}, query_dictionary={result_value=[[@31,176:187='result_value',<459>,1:176]], id=[[@35,207:208='id',<459>,1:207]]}, def_query0={query_dictionary={arrays_to_object=[[@9,55:70='arrays_to_object',<454>,1:55], [@27,139:154='arrays_to_object',<454>,1:139]], id=[[@13,75:76='id',<459>,1:75], [@33,190:205='arrays_to_object',<454>,1:190], [@41,258:273='arrays_to_object',<454>,1:258]]}, table_dictionary={analytics.arrays_to_object={arrays_to_object=[[@5,33:33='a',<459>,1:33]], active=[[@20,118:118='a',<459>,1:118]], id=[[@11,73:73='a',<459>,1:73]]}}, filters=[{name=active, table_ref=a}], interface={arrays_to_object=[{name=arrays_to_object, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.arrays_to_object}}, filters=[{name=id, table_ref=arrays_to_object}], interface={result_value=[{name=arrays_to_object, table_ref=arrays_to_object}], id=[{name=id, table_ref=arrays_to_object}]}, table_alias={arrays_to_object=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYSTOOBJECTExistingWildcardQualifierV0Test() {
		final String query = "select arrays_to_object.* from events as arrays_to_object where arrays_to_object.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=arrays_to_object}}}, from={table={alias=arrays_to_object, table=events}}, where={condition={left={column={name=active, table_ref=arrays_to_object}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,64:79='arrays_to_object',<454>,1:64]], *=[[@1,7:22='arrays_to_object',<454>,1:7]]}}",
				"{query0={*=[[@3,24:24='*',<291>,1:24]]}}",
				"{def_query0={query_dictionary={*=[[@3,24:24='*',<291>,1:24]]}, table_dictionary={events={active=[[@9,64:79='arrays_to_object',<454>,1:64]], *=[[@1,7:22='arrays_to_object',<454>,1:7]]}}, filters=[{name=active, table_ref=arrays_to_object}], interface={*=[{name=*, table_ref=arrays_to_object}]}, table_alias={arrays_to_object=events}}}");
	}

	@Test
	public void contextualKeywordARRAYREPEATExistingContextualNamesV0Test() {
		final String query = "with array_repeat as (select a.array_repeat as array_repeat, a.id from analytics.array_repeat a where a.active = 1) select array_repeat.array_repeat as result_value, array_repeat.id from array_repeat as array_repeat where array_repeat.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_repeat, table_ref=a}, alias=array_repeat}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_repeat}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_repeat}}, query={select={1={column={name=array_repeat, table_ref=array_repeat}, alias=result_value}, 2={column={name=id, table_ref=array_repeat}}}, from={table={alias=array_repeat, table=array_repeat}}, where={condition={left={column={name=id, table_ref=array_repeat}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_repeat={array_repeat=[[@5,29:29='a',<459>,1:29]], active=[[@20,102:102='a',<459>,1:102]], id=[[@11,61:61='a',<459>,1:61]]}}",
				"{query0={array_repeat=[[@9,47:58='array_repeat',<455>,1:47], [@27,123:134='array_repeat',<455>,1:123]], id=[[@13,63:64='id',<459>,1:63], [@33,166:177='array_repeat',<455>,1:166], [@41,222:233='array_repeat',<455>,1:222]]}, query1={result_value=[[@31,152:163='result_value',<459>,1:152]], id=[[@35,179:180='id',<459>,1:179]]}}",
				"{def_query1={context_list={array_repeat=query0}, query_dictionary={result_value=[[@31,152:163='result_value',<459>,1:152]], id=[[@35,179:180='id',<459>,1:179]]}, def_query0={query_dictionary={array_repeat=[[@9,47:58='array_repeat',<455>,1:47], [@27,123:134='array_repeat',<455>,1:123]], id=[[@13,63:64='id',<459>,1:63], [@33,166:177='array_repeat',<455>,1:166], [@41,222:233='array_repeat',<455>,1:222]]}, table_dictionary={analytics.array_repeat={array_repeat=[[@5,29:29='a',<459>,1:29]], active=[[@20,102:102='a',<459>,1:102]], id=[[@11,61:61='a',<459>,1:61]]}}, filters=[{name=active, table_ref=a}], interface={array_repeat=[{name=array_repeat, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.array_repeat}}, filters=[{name=id, table_ref=array_repeat}], interface={result_value=[{name=array_repeat, table_ref=array_repeat}], id=[{name=id, table_ref=array_repeat}]}, table_alias={array_repeat=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYREPEATExistingWildcardQualifierV0Test() {
		final String query = "select array_repeat.* from events as array_repeat where array_repeat.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_repeat}}}, from={table={alias=array_repeat, table=events}}, where={condition={left={column={name=active, table_ref=array_repeat}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,56:67='array_repeat',<455>,1:56]], *=[[@1,7:18='array_repeat',<455>,1:7]]}}",
				"{query0={*=[[@3,20:20='*',<291>,1:20]]}}",
				"{def_query0={query_dictionary={*=[[@3,20:20='*',<291>,1:20]]}, table_dictionary={events={active=[[@9,56:67='array_repeat',<455>,1:56]], *=[[@1,7:18='array_repeat',<455>,1:7]]}}, filters=[{name=active, table_ref=array_repeat}], interface={*=[{name=*, table_ref=array_repeat}]}, table_alias={array_repeat=events}}}");
	}

	@Test
	public void contextualKeywordSPLITExistingContextualNamesV0Test() {
		final String query = "with split as (select a.split as split, a.id from analytics.split a where a.active = 1) select split.split as result_value, split.id from split as split where split.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=split, table_ref=a}, alias=split}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=split}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=split}}, query={select={1={column={name=split, table_ref=split}, alias=result_value}, 2={column={name=id, table_ref=split}}}, from={table={alias=split, table=split}}, where={condition={left={column={name=id, table_ref=split}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.split={split=[[@5,22:22='a',<459>,1:22]], active=[[@20,74:74='a',<459>,1:74]], id=[[@11,40:40='a',<459>,1:40]]}}",
				"{query0={split=[[@9,33:37='split',<456>,1:33], [@27,95:99='split',<456>,1:95]], id=[[@13,42:43='id',<459>,1:42], [@33,124:128='split',<456>,1:124], [@41,159:163='split',<456>,1:159]]}, query1={result_value=[[@31,110:121='result_value',<459>,1:110]], id=[[@35,130:131='id',<459>,1:130]]}}",
				"{def_query1={context_list={split=query0}, query_dictionary={result_value=[[@31,110:121='result_value',<459>,1:110]], id=[[@35,130:131='id',<459>,1:130]]}, def_query0={query_dictionary={split=[[@9,33:37='split',<456>,1:33], [@27,95:99='split',<456>,1:95]], id=[[@13,42:43='id',<459>,1:42], [@33,124:128='split',<456>,1:124], [@41,159:163='split',<456>,1:159]]}, table_dictionary={analytics.split={split=[[@5,22:22='a',<459>,1:22]], active=[[@20,74:74='a',<459>,1:74]], id=[[@11,40:40='a',<459>,1:40]]}}, filters=[{name=active, table_ref=a}], interface={split=[{name=split, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=analytics.split}}, filters=[{name=id, table_ref=split}], interface={result_value=[{name=split, table_ref=split}], id=[{name=id, table_ref=split}]}, table_alias={split=query0}}}");
	}

	@Test
	public void contextualKeywordSPLITExistingWildcardQualifierV0Test() {
		final String query = "select split.* from events as split where split.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=split}}}, from={table={alias=split, table=events}}, where={condition={left={column={name=active, table_ref=split}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,42:46='split',<456>,1:42]], *=[[@1,7:11='split',<456>,1:7]]}}",
				"{query0={*=[[@3,13:13='*',<291>,1:13]]}}",
				"{def_query0={query_dictionary={*=[[@3,13:13='*',<291>,1:13]]}, table_dictionary={events={active=[[@9,42:46='split',<456>,1:42]], *=[[@1,7:11='split',<456>,1:7]]}}, filters=[{name=active, table_ref=split}], interface={*=[{name=*, table_ref=split}]}, table_alias={split=events}}}");
	}

	@Test
	public void contextualKeywordARRAYCONSTRUCTSTRUCTUREDExistingContextualNamesV0Test() {
		final String query = "with array_construct_structured as (select a.array_construct_structured as array_construct_structured, a.id from analytics.array_construct_structured a where a.active = 1) select array_construct_structured.array_construct_structured as result_value, array_construct_structured.id from array_construct_structured as array_construct_structured where array_construct_structured.id > 0";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=array_construct_structured, table_ref=a}, alias=array_construct_structured}, 2={column={name=id, table_ref=a}}}, from={table={alias=a, schema=analytics, table=array_construct_structured}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=array_construct_structured}}, query={select={1={column={name=array_construct_structured, table_ref=array_construct_structured}, alias=result_value}, 2={column={name=id, table_ref=array_construct_structured}}}, from={table={alias=array_construct_structured, table=array_construct_structured}}, where={condition={left={column={name=id, table_ref=array_construct_structured}}, right={literal=0}, operator=>}}}}}",
				"[result_value, id]",
				"{}",
				"{analytics.array_construct_structured={active=[[@20,158:158='a',<459>,1:158]], id=[[@11,103:103='a',<459>,1:103]], array_construct_structured=[[@5,43:43='a',<459>,1:43]]}}",
				"{query0={id=[[@13,105:106='id',<459>,1:105], [@33,250:275='array_construct_structured',<457>,1:250], [@41,348:373='array_construct_structured',<457>,1:348]], array_construct_structured=[[@9,75:100='array_construct_structured',<457>,1:75], [@27,179:204='array_construct_structured',<457>,1:179]]}, query1={result_value=[[@31,236:247='result_value',<459>,1:236]], id=[[@35,277:278='id',<459>,1:277]]}}",
				"{def_query1={context_list={array_construct_structured=query0}, query_dictionary={result_value=[[@31,236:247='result_value',<459>,1:236]], id=[[@35,277:278='id',<459>,1:277]]}, def_query0={query_dictionary={id=[[@13,105:106='id',<459>,1:105], [@33,250:275='array_construct_structured',<457>,1:250], [@41,348:373='array_construct_structured',<457>,1:348]], array_construct_structured=[[@9,75:100='array_construct_structured',<457>,1:75], [@27,179:204='array_construct_structured',<457>,1:179]]}, table_dictionary={analytics.array_construct_structured={active=[[@20,158:158='a',<459>,1:158]], id=[[@11,103:103='a',<459>,1:103]], array_construct_structured=[[@5,43:43='a',<459>,1:43]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], array_construct_structured=[{name=array_construct_structured, table_ref=a}]}, table_alias={a=analytics.array_construct_structured}}, filters=[{name=id, table_ref=array_construct_structured}], interface={result_value=[{name=array_construct_structured, table_ref=array_construct_structured}], id=[{name=id, table_ref=array_construct_structured}]}, table_alias={array_construct_structured=query0}}}");
	}

	@Test
	public void contextualKeywordARRAYCONSTRUCTSTRUCTUREDExistingWildcardQualifierV0Test() {
		final String query = "select array_construct_structured.* from events as array_construct_structured where array_construct_structured.active = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=array_construct_structured}}}, from={table={alias=array_construct_structured, table=events}}, where={condition={left={column={name=active, table_ref=array_construct_structured}}, right={literal=1}, operator==}}}}",
				"[*]",
				"{}",
				"{events={active=[[@9,84:109='array_construct_structured',<457>,1:84]], *=[[@1,7:32='array_construct_structured',<457>,1:7]]}}",
				"{query0={*=[[@3,34:34='*',<291>,1:34]]}}",
				"{def_query0={query_dictionary={*=[[@3,34:34='*',<291>,1:34]]}, table_dictionary={events={active=[[@9,84:109='array_construct_structured',<457>,1:84]], *=[[@1,7:32='array_construct_structured',<457>,1:7]]}}, filters=[{name=active, table_ref=array_construct_structured}], interface={*=[{name=*, table_ref=array_construct_structured}]}, table_alias={array_construct_structured=events}}}");
	}

	@Test
	public void contextualKeywordCHARINDEXFunctionAndColumnCoexistV0Test() {
		final String query = "select a.charindex as stored_value, charindex('x', a.label) as result_value, b.id from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=charindex, table_ref=a}, alias=stored_value}, 2={function={function_name=charindex, parameters={1={literal='x'}, 2={column={name=label, table_ref=a}}}, operator=IN}, alias=result_value}, 3={column={name=id, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, stored_value, id]",
				"{}",
				"{accounts={id=[[@18,77:77='b',<459>,1:77], [@32,130:130='b',<459>,1:130]], enabled=[[@42,158:158='b',<459>,1:158]]}, events={account_id=[[@28,115:115='a',<459>,1:115]], active=[[@36,141:141='a',<459>,1:141]], label=[[@11,51:51='a',<459>,1:51]], charindex=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@16,63:74='result_value',<459>,1:63]], stored_value=[[@5,22:33='stored_value',<459>,1:22]], id=[[@20,79:80='id',<459>,1:79]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,63:74='result_value',<459>,1:63]], stored_value=[[@5,22:33='stored_value',<459>,1:22]], id=[[@20,79:80='id',<459>,1:79]]}, table_dictionary={accounts={id=[[@18,77:77='b',<459>,1:77], [@32,130:130='b',<459>,1:130]], enabled=[[@42,158:158='b',<459>,1:158]]}, events={account_id=[[@28,115:115='a',<459>,1:115]], active=[[@36,141:141='a',<459>,1:141]], label=[[@11,51:51='a',<459>,1:51]], charindex=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=label, table_ref=a}], stored_value=[{name=charindex, table_ref=a}], id=[{name=id, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordINSTRFunctionAndColumnCoexistV0Test() {
		final String query = "select a.instr as stored_value, instr(a.label, 'x') as result_value, b.id from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=instr, table_ref=a}, alias=stored_value}, 2={function={function_name=instr, parameters={1={column={name=label, table_ref=a}}, 2={literal='x'}}, operator=IN}, alias=result_value}, 3={column={name=id, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, stored_value, id]",
				"{}",
				"{accounts={id=[[@18,69:69='b',<459>,1:69], [@32,122:122='b',<459>,1:122]], enabled=[[@42,150:150='b',<459>,1:150]]}, events={account_id=[[@28,107:107='a',<459>,1:107]], instr=[[@1,7:7='a',<459>,1:7]], active=[[@36,133:133='a',<459>,1:133]], label=[[@9,38:38='a',<459>,1:38]]}}",
				"{query0={result_value=[[@16,55:66='result_value',<459>,1:55]], stored_value=[[@5,18:29='stored_value',<459>,1:18]], id=[[@20,71:72='id',<459>,1:71]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,55:66='result_value',<459>,1:55]], stored_value=[[@5,18:29='stored_value',<459>,1:18]], id=[[@20,71:72='id',<459>,1:71]]}, table_dictionary={accounts={id=[[@18,69:69='b',<459>,1:69], [@32,122:122='b',<459>,1:122]], enabled=[[@42,150:150='b',<459>,1:150]]}, events={account_id=[[@28,107:107='a',<459>,1:107]], instr=[[@1,7:7='a',<459>,1:7]], active=[[@36,133:133='a',<459>,1:133]], label=[[@9,38:38='a',<459>,1:38]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=label, table_ref=a}], stored_value=[{name=instr, table_ref=a}], id=[{name=id, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordIFFFunctionAndColumnCoexistV0Test() {
		final String query = "select a.iff as stored_value, iff(a.active = 1, a.label, b.label) as result_value, b.id from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=iff, table_ref=a}, alias=stored_value}, 2={function={parameters={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={column={name=label, table_ref=a}}, 3={column={name=label, table_ref=b}}}, function_name=iff}, alias=result_value}, 3={column={name=id, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, stored_value, id]",
				"{}",
				"{accounts={label=[[@19,57:57='b',<459>,1:57]], id=[[@26,83:83='b',<459>,1:83], [@40,136:136='b',<459>,1:136]], enabled=[[@50,164:164='b',<459>,1:164]]}, events={account_id=[[@36,121:121='a',<459>,1:121]], iff=[[@1,7:7='a',<459>,1:7]], active=[[@9,34:34='a',<459>,1:34], [@44,147:147='a',<459>,1:147]], label=[[@15,48:48='a',<459>,1:48]]}}",
				"{query0={result_value=[[@24,69:80='result_value',<459>,1:69]], stored_value=[[@5,16:27='stored_value',<459>,1:16]], id=[[@28,85:86='id',<459>,1:85]]}}",
				"{def_query0={query_dictionary={result_value=[[@24,69:80='result_value',<459>,1:69]], stored_value=[[@5,16:27='stored_value',<459>,1:16]], id=[[@28,85:86='id',<459>,1:85]]}, table_dictionary={accounts={label=[[@19,57:57='b',<459>,1:57]], id=[[@26,83:83='b',<459>,1:83], [@40,136:136='b',<459>,1:136]], enabled=[[@50,164:164='b',<459>,1:164]]}, events={account_id=[[@36,121:121='a',<459>,1:121]], iff=[[@1,7:7='a',<459>,1:7]], active=[[@9,34:34='a',<459>,1:34], [@44,147:147='a',<459>,1:147]], label=[[@15,48:48='a',<459>,1:48]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=active, table_ref=a}, {name=label, table_ref=a}, {name=label, table_ref=b}], stored_value=[{name=iff, table_ref=a}], id=[{name=id, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordMD5FunctionAndColumnCoexistV0Test() {
		final String query = "select a.md5 as stored_value, md5(a.label) as result_value, b.id from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=md5, table_ref=a}, alias=stored_value}, 2={function={parameters={1={column={name=label, table_ref=a}}}, function_name=md5}, alias=result_value}, 3={column={name=id, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, stored_value, id]",
				"{}",
				"{accounts={id=[[@16,60:60='b',<459>,1:60], [@30,113:113='b',<459>,1:113]], enabled=[[@40,141:141='b',<459>,1:141]]}, events={account_id=[[@26,98:98='a',<459>,1:98]], active=[[@34,124:124='a',<459>,1:124]], label=[[@9,34:34='a',<459>,1:34]], md5=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@14,46:57='result_value',<459>,1:46]], stored_value=[[@5,16:27='stored_value',<459>,1:16]], id=[[@18,62:63='id',<459>,1:62]]}}",
				"{def_query0={query_dictionary={result_value=[[@14,46:57='result_value',<459>,1:46]], stored_value=[[@5,16:27='stored_value',<459>,1:16]], id=[[@18,62:63='id',<459>,1:62]]}, table_dictionary={accounts={id=[[@16,60:60='b',<459>,1:60], [@30,113:113='b',<459>,1:113]], enabled=[[@40,141:141='b',<459>,1:141]]}, events={account_id=[[@26,98:98='a',<459>,1:98]], active=[[@34,124:124='a',<459>,1:124]], label=[[@9,34:34='a',<459>,1:34]], md5=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=label, table_ref=a}], stored_value=[{name=md5, table_ref=a}], id=[{name=id, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordREVERSEFunctionAndColumnCoexistV0Test() {
		final String query = "select a.reverse as stored_value, reverse(a.label) as result_value, b.id from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=reverse, table_ref=a}, alias=stored_value}, 2={function={parameters={1={column={name=label, table_ref=a}}}, function_name=reverse}, alias=result_value}, 3={column={name=id, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, stored_value, id]",
				"{}",
				"{accounts={id=[[@16,68:68='b',<459>,1:68], [@30,121:121='b',<459>,1:121]], enabled=[[@40,149:149='b',<459>,1:149]]}, events={account_id=[[@26,106:106='a',<459>,1:106]], active=[[@34,132:132='a',<459>,1:132]], label=[[@9,42:42='a',<459>,1:42]], reverse=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@14,54:65='result_value',<459>,1:54]], stored_value=[[@5,20:31='stored_value',<459>,1:20]], id=[[@18,70:71='id',<459>,1:70]]}}",
				"{def_query0={query_dictionary={result_value=[[@14,54:65='result_value',<459>,1:54]], stored_value=[[@5,20:31='stored_value',<459>,1:20]], id=[[@18,70:71='id',<459>,1:70]]}, table_dictionary={accounts={id=[[@16,68:68='b',<459>,1:68], [@30,121:121='b',<459>,1:121]], enabled=[[@40,149:149='b',<459>,1:149]]}, events={account_id=[[@26,106:106='a',<459>,1:106]], active=[[@34,132:132='a',<459>,1:132]], label=[[@9,42:42='a',<459>,1:42]], reverse=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=label, table_ref=a}], stored_value=[{name=reverse, table_ref=a}], id=[{name=id, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordTRUNCATEFunctionAndColumnCoexistV0Test() {
		final String query = "select a.truncate as stored_value, truncate(a.amount, 2) as result_value, b.id from events a join accounts b on a.account_id = b.id where a.active = 1 and b.enabled = 1";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=truncate, table_ref=a}, alias=stored_value}, 2={function={parameters={1={column={name=amount, table_ref=a}}, 2={literal=2}}, function_name=truncate}, alias=result_value}, 3={column={name=id, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=join, on={condition={left={column={name=account_id, table_ref=a}}, right={column={name=id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=accounts}}}}, where={and={1={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, 2={condition={left={column={name=enabled, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[result_value, stored_value, id]",
				"{}",
				"{accounts={id=[[@18,74:74='b',<459>,1:74], [@32,127:127='b',<459>,1:127]], enabled=[[@42,155:155='b',<459>,1:155]]}, events={amount=[[@9,44:44='a',<459>,1:44]], account_id=[[@28,112:112='a',<459>,1:112]], truncate=[[@1,7:7='a',<459>,1:7]], active=[[@36,138:138='a',<459>,1:138]]}}",
				"{query0={result_value=[[@16,60:71='result_value',<459>,1:60]], stored_value=[[@5,21:32='stored_value',<459>,1:21]], id=[[@20,76:77='id',<459>,1:76]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,60:71='result_value',<459>,1:60]], stored_value=[[@5,21:32='stored_value',<459>,1:21]], id=[[@20,76:77='id',<459>,1:76]]}, table_dictionary={accounts={id=[[@18,74:74='b',<459>,1:74], [@32,127:127='b',<459>,1:127]], enabled=[[@42,155:155='b',<459>,1:155]]}, events={amount=[[@9,44:44='a',<459>,1:44]], account_id=[[@28,112:112='a',<459>,1:112]], truncate=[[@1,7:7='a',<459>,1:7]], active=[[@36,138:138='a',<459>,1:138]]}}, filters=[{name=account_id, table_ref=a}, {name=id, table_ref=b}, {name=active, table_ref=a}, {name=enabled, table_ref=b}], interface={result_value=[{name=amount, table_ref=a}], stored_value=[{name=truncate, table_ref=a}], id=[{name=id, table_ref=b}]}, table_alias={a=events, b=accounts}}}");
	}

	@Test
	public void contextualKeywordMODEOriginalRmcpProjectionV0Test() {
		final String query = "select a.mode as mode from t as a";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=mode, table_ref=a}, alias=mode}}, from={table={alias=a, table=t}}}}",
				"[mode]",
				"{}",
				"{t={mode=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={mode=[[@5,17:20='mode',<384>,1:17]]}}",
				"{def_query0={query_dictionary={mode=[[@5,17:20='mode',<384>,1:17]]}, table_dictionary={t={mode=[[@1,7:7='a',<459>,1:7]]}}, interface={mode=[{name=mode, table_ref=a}]}, table_alias={a=t}}}");
	}

	@Test
	public void contextualKeywordMODEOriginalRmcpCteV0Test() {
		final String query = "with mode as (select 1 as c) select mode.c from mode";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={alias=c, literal=1}}}, alias=mode}}, query={select={1={column={name=c, table_ref=mode}}}, from={table={alias=null, table=mode}}}}}",
				"[c]",
				"{}",
				"{}",
				"{query0={c=[[@7,26:26='c',<459>,1:26], [@10,36:39='mode',<384>,1:36]]}, query1={c=[[@12,41:41='c',<459>,1:41]]}}",
				"{def_query1={context_list={mode=query0}, query_dictionary={c=[[@12,41:41='c',<459>,1:41]]}, def_query0={query_dictionary={c=[[@7,26:26='c',<459>,1:26], [@10,36:39='mode',<384>,1:36]]}, interface={c=[]}}, interface={c=[{name=c, table_ref=mode}]}, table_alias={mode=query0}}}");
	}

	@Test
	public void contextualKeywordTRUNCATEOriginalRmcpProjectionV0Test() {
		final String query = "select a.truncate as truncate from t as a";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=truncate, table_ref=a}, alias=truncate}}, from={table={alias=a, table=t}}}}",
				"[truncate]",
				"{}",
				"{t={truncate=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={truncate=[[@5,21:28='truncate',<335>,1:21]]}}",
				"{def_query0={query_dictionary={truncate=[[@5,21:28='truncate',<335>,1:21]]}, table_dictionary={t={truncate=[[@1,7:7='a',<459>,1:7]]}}, interface={truncate=[{name=truncate, table_ref=a}]}, table_alias={a=t}}}");
	}

	@Test
	public void contextualKeywordTRUNCATEOriginalRmcpCteV0Test() {
		final String query = "with truncate as (select 1 as c) select truncate.c from truncate";
		final SqlParseEventWalker extractor = runQuery(query);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={alias=c, literal=1}}}, alias=truncate}}, query={select={1={column={name=c, table_ref=truncate}}}, from={table={alias=null, table=truncate}}}}}",
				"[c]",
				"{}",
				"{}",
				"{query0={c=[[@7,30:30='c',<459>,1:30], [@10,40:47='truncate',<335>,1:40]]}, query1={c=[[@12,49:49='c',<459>,1:49]]}}",
				"{def_query1={context_list={truncate=query0}, query_dictionary={c=[[@12,49:49='c',<459>,1:49]]}, def_query0={query_dictionary={c=[[@7,30:30='c',<459>,1:30], [@10,40:47='truncate',<335>,1:40]]}, interface={c=[]}}, interface={c=[{name=c, table_ref=truncate}]}, table_alias={truncate=query0}}}");
	}

	@Test
	public void contextualQualifySimpleConditionSubstitutionV0Test() {
		final String query = "select a.id, a.amount from events a QUALIFY <active filters>";
		final SqlParseEventWalker extractor = runDedicatedFunctionQuery(query, SQLSelectParserParser.RULE_qualify_clause);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"qualify_clause at (l:1 c:36) is recognized as Snowflake-specific grammar.",
						1, 36, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=amount, table_ref=a}}}, from={table={alias=a, table=events}}, qualify={substitution={name=<active filters>, type=condition}}}}",
				"[amount, id]",
				"{<active filters>=condition}",
				"{events={amount=[[@5,13:13='a',<459>,1:13]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={amount=[[@7,15:20='amount',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={amount=[[@7,15:20='amount',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={amount=[[@5,13:13='a',<459>,1:13]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[], interface={amount=[{name=amount, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void contextualQualifyExtendedConditionSubstitutionV0Test() {
		final String query = "select a.id, a.amount from events a QUALIFY <[active filters]>";
		final SqlParseEventWalker extractor = runDedicatedFunctionQuery(query, SQLSelectParserParser.RULE_qualify_clause);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"qualify_clause at (l:1 c:36) is recognized as Snowflake-specific grammar.",
						1, 36, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=amount, table_ref=a}}}, from={table={alias=a, table=events}}, qualify={substitution={name=<[active filters]>, parts={1=[active filters]}, type=condition}}}}",
				"[amount, id]",
				"{<[active filters]>=condition}",
				"{events={amount=[[@5,13:13='a',<459>,1:13]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={amount=[[@7,15:20='amount',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={amount=[[@7,15:20='amount',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={amount=[[@5,13:13='a',<459>,1:13]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[], interface={amount=[{name=amount, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void contextualQualifyMixedConditionSubstitutionV0Test() {
		final String query = "select a.id, a.amount from events a QUALIFY <filters.[active]>";
		final SqlParseEventWalker extractor = runDedicatedFunctionQuery(query, SQLSelectParserParser.RULE_qualify_clause);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"qualify_clause at (l:1 c:36) is recognized as Snowflake-specific grammar.",
						1, 36, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=amount, table_ref=a}}}, from={table={alias=a, table=events}}, qualify={substitution={name=<filters.[active]>, parts={1=filters, 2=[active]}, type=condition}}}}",
				"[amount, id]",
				"{<filters.[active]>=condition}",
				"{events={amount=[[@5,13:13='a',<459>,1:13]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={amount=[[@7,15:20='amount',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={amount=[[@7,15:20='amount',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={amount=[[@5,13:13='a',<459>,1:13]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[], interface={amount=[{name=amount, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}
}
