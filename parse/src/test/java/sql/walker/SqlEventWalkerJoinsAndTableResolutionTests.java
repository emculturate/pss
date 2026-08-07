package sql.walker;
import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

public class SqlEventWalkerJoinsAndTableResolutionTests extends AbstractSqlParseEventWalkerTest {

	/*
	 * JOIN … USING (column list) — T1.1 / named_columns_join scenarios.
	 * Variation naming: <scenarioBaseName>V<n>Test (V0 = initial golden for that scenario).
	 */

	@Test
	public void basicJoinWithUsingSingleColumnV0Test() {
		final String query = " SELECT a.* FROM third a join fourth b using (a) ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={using={1={column={name=a, table_ref=null}}}, join=join}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@12,46:46='a',<391>,1:46]], *=[[@1,8:8='a',<391>,1:8]]}, fourth={a=[[@12,46:46='a',<391>,1:46]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,10:10='*',<291>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@3,10:10='*',<291>,1:10]]}, table_dictionary={third={a=[[@12,46:46='a',<391>,1:46]], *=[[@1,8:8='a',<391>,1:8]]}, fourth={a=[[@12,46:46='a',<391>,1:46]]}}, filters=[{name=a, table_ref=a}, {name=a, table_ref=b}], interface={*=[{name=*, table_ref=a}]}, table_alias={a=third, b=fourth}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void basicJoinWithUsingTwoColumnsV0Test() {
		final String query = " SELECT a.* FROM third a join fourth b using (a, b) ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={using={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, join=join}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@12,46:46='a',<391>,1:46]], b=[[@14,49:49='b',<391>,1:49]], *=[[@1,8:8='a',<391>,1:8]]}, fourth={a=[[@12,46:46='a',<391>,1:46]], b=[[@14,49:49='b',<391>,1:49]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,10:10='*',<291>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@3,10:10='*',<291>,1:10]]}, table_dictionary={third={a=[[@12,46:46='a',<391>,1:46]], b=[[@14,49:49='b',<391>,1:49]], *=[[@1,8:8='a',<391>,1:8]]}, fourth={a=[[@12,46:46='a',<391>,1:46]], b=[[@14,49:49='b',<391>,1:49]]}}, filters=[{name=a, table_ref=a}, {name=a, table_ref=b}, {name=b, table_ref=a}, {name=b, table_ref=b}], interface={*=[{name=*, table_ref=a}]}, table_alias={a=third, b=fourth}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void joinUsingTwoColumnsLeftSubquerySourceV0Test() {
		final String query = " SELECT a.* FROM (SELECT a, b FROM third) a join fourth b using (a, b) ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, query={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={using={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, join=join}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@7,25:25='a',<391>,1:25]], b=[[@9,28:28='b',<391>,1:28]]}, fourth={a=[[@19,65:65='a',<391>,1:65]], b=[[@21,68:68='b',<391>,1:68]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={a=[[@7,25:25='a',<391>,1:25], [@19,65:65='a',<391>,1:65]], b=[[@9,28:28='b',<391>,1:28], [@21,68:68='b',<391>,1:68]], *=[[@1,8:8='a',<391>,1:8]]}, query1={*=[[@3,10:10='*',<291>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={*=[[@3,10:10='*',<291>,1:10]]}, table_dictionary={fourth={a=[[@19,65:65='a',<391>,1:65]], b=[[@21,68:68='b',<391>,1:68]]}}, def_query0={query_dictionary={a=[[@7,25:25='a',<391>,1:25], [@19,65:65='a',<391>,1:65]], b=[[@9,28:28='b',<391>,1:28], [@21,68:68='b',<391>,1:68]], *=[[@1,8:8='a',<391>,1:8]]}, table_dictionary={third={a=[[@7,25:25='a',<391>,1:25]], b=[[@9,28:28='b',<391>,1:28]]}}, interface={a=[{name=a, table_ref=third}], b=[{name=b, table_ref=third}], *=wildcard}}, filters=[{name=a, table_ref=a}, {name=a, table_ref=b}, {name=b, table_ref=a}, {name=b, table_ref=b}], interface={*=[{name=*, table_ref=a}]}, table_alias={a=query0, b=fourth}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void basicLeftJoinWithUsingAndWhereV0Test() {
		final String query = " SELECT a.* FROM third a left join fourth b using (a) "
				+ "\n where b.c = 1 ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={using={1={column={name=a, table_ref=null}}}, join=left}, 3={table={alias=b, table=fourth}}}}, where={condition={left={column={name=c, table_ref=b}}, right={literal=1}, operator==}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@13,51:51='a',<391>,1:51]], *=[[@1,8:8='a',<391>,1:8]]}, fourth={a=[[@13,51:51='a',<391>,1:51]], c=[[@16,62:62='b',<391>,2:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,10:10='*',<291>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@3,10:10='*',<291>,1:10]]}, table_dictionary={third={a=[[@13,51:51='a',<391>,1:51]], *=[[@1,8:8='a',<391>,1:8]]}, fourth={a=[[@13,51:51='a',<391>,1:51]], c=[[@16,62:62='b',<391>,2:7]]}}, filters=[{name=a, table_ref=a}, {name=a, table_ref=b}, {name=c, table_ref=b}], interface={*=[{name=*, table_ref=a}]}, table_alias={a=third, b=fourth}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void joinSubquerySourcesWithUsingV0Test() {
		final String query = "SELECT x.* FROM (SELECT a FROM third) x JOIN (SELECT a FROM fourth) y USING (a)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=x}}}, from={join={1={table={alias=x, query={select={1={column={name=a, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 3={table={alias=y, query={select={1={column={name=a, table_ref=null}}}, from={table={alias=null, table=fourth}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@7,24:24='a',<391>,1:24]]}, fourth={a=[[@15,53:53='a',<391>,1:53]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={a=[[@7,24:24='a',<391>,1:24], [@22,77:77='a',<391>,1:77]], *=[[@1,7:7='x',<391>,1:7]]}, query1={a=[[@15,53:53='a',<391>,1:53], [@22,77:77='a',<391>,1:77]]}, query2={*=[[@3,9:9='*',<291>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={*=[[@3,9:9='*',<291>,1:9]]}, def_query1={query_dictionary={a=[[@15,53:53='a',<391>,1:53], [@22,77:77='a',<391>,1:77]]}, table_dictionary={fourth={a=[[@15,53:53='a',<391>,1:53]]}}, interface={a=[{name=a, table_ref=fourth}]}}, def_query0={query_dictionary={a=[[@7,24:24='a',<391>,1:24], [@22,77:77='a',<391>,1:77]], *=[[@1,7:7='x',<391>,1:7]]}, table_dictionary={third={a=[[@7,24:24='a',<391>,1:24]]}}, interface={a=[{name=a, table_ref=third}], *=wildcard}}, filters=[{name=a, table_ref=x}, {name=a, table_ref=y}], interface={*=[{name=*, table_ref=x}]}, table_alias={x=query0, y=query1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void joinUsingQualifiedColumnFatalV0Test() {
		final String query = " SELECT a.* FROM third a JOIN fourth b USING (a.a) ";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={*=[[@1,8:8='a',<391>,1:8]]}, fourth={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,10:10='*',<291>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@3,10:10='*',<291>,1:10]]}, table_dictionary={third={*=[[@1,8:8='a',<391>,1:8]]}, fourth={}}, interface={*=[{name=*, table_ref=a}]}, table_alias={a=third, b=fourth}}}",
				extractor.getSymbolTable().toString());

		assertFatalDiagnosticAtPositionWithFullMessage(
				snippet,
				"QUALIFIED_COLUMN_IN_JOIN_USING",
				"Join Using column 'a.a' at (l:1 c:46) must not be qualified.",
				"a.a",
				1,
				46);
	}

	@Test
	public void joinUsingMissingColumnOnSubqueryOperandFatalV0ATest() {
		final String query = "SELECT x.* FROM (SELECT a FROM third) x JOIN (SELECT b FROM fourth) y USING (a)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=x}}}, from={join={1={table={alias=x, query={select={1={column={name=a, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 3={table={alias=y, query={select={1={column={name=b, table_ref=null}}}, from={table={alias=null, table=fourth}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@7,24:24='a',<391>,1:24]]}, fourth={b=[[@15,53:53='b',<391>,1:53]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={a=[[@7,24:24='a',<391>,1:24], [@22,77:77='a',<391>,1:77]], *=[[@1,7:7='x',<391>,1:7]]}, query1={b=[[@15,53:53='b',<391>,1:53]]}, query2={*=[[@3,9:9='*',<291>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={*=[[@3,9:9='*',<291>,1:9]]}, def_query1={query_dictionary={b=[[@15,53:53='b',<391>,1:53]]}, table_dictionary={fourth={b=[[@15,53:53='b',<391>,1:53]]}}, interface={b=[{name=b, table_ref=fourth}]}}, def_query0={query_dictionary={a=[[@7,24:24='a',<391>,1:24], [@22,77:77='a',<391>,1:77]], *=[[@1,7:7='x',<391>,1:7]]}, table_dictionary={third={a=[[@7,24:24='a',<391>,1:24]]}}, interface={a=[{name=a, table_ref=third}], *=wildcard}}, filters=[{name=a, table_ref=x}], interface={*=[{name=*, table_ref=x}]}, table_alias={x=query0, y=query1}}}",
				extractor.getSymbolTable().toString());

		assertFatalDiagnosticAtPositionWithFullMessage(
				snippet,
				"JOIN_USING_COLUMN_NOT_FOUND",
				"Join Using column 'a' at (l:1 c:77) not found in Join Sources (y). ",
				"a",
				1,
				77);
	}

	@Test
	public void joinUsingMissingColumnOnLeftSubqueryOperandFatalV0BTest() {
		final String query = "SELECT x.* FROM (SELECT b FROM third) x JOIN (SELECT a FROM fourth) y USING (a)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=x}}}, from={join={1={table={alias=x, query={select={1={column={name=b, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 3={table={alias=y, query={select={1={column={name=a, table_ref=null}}}, from={table={alias=null, table=fourth}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={b=[[@7,24:24='b',<391>,1:24]]}, fourth={a=[[@15,53:53='a',<391>,1:53]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={b=[[@7,24:24='b',<391>,1:24]], *=[[@1,7:7='x',<391>,1:7]]}, query1={a=[[@15,53:53='a',<391>,1:53], [@22,77:77='a',<391>,1:77]]}, query2={*=[[@3,9:9='*',<291>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={*=[[@3,9:9='*',<291>,1:9]]}, def_query1={query_dictionary={a=[[@15,53:53='a',<391>,1:53], [@22,77:77='a',<391>,1:77]]}, table_dictionary={fourth={a=[[@15,53:53='a',<391>,1:53]]}}, interface={a=[{name=a, table_ref=fourth}]}}, def_query0={query_dictionary={b=[[@7,24:24='b',<391>,1:24]], *=[[@1,7:7='x',<391>,1:7]]}, table_dictionary={third={b=[[@7,24:24='b',<391>,1:24]]}}, interface={b=[{name=b, table_ref=third}], *=wildcard}}, filters=[{name=a, table_ref=y}], interface={*=[{name=*, table_ref=x}]}, table_alias={x=query0, y=query1}}}",
				extractor.getSymbolTable().toString());

		assertFatalDiagnosticAtPositionWithFullMessage(
				snippet,
				"JOIN_USING_COLUMN_NOT_FOUND",
				"Join Using column 'a' at (l:1 c:77) not found in Join Sources (x). ",
				"a",
				1,
				77);
	}

	@Test
	public void joinUsingMissingColumnOnBothSubqueryOperandsFatalV0Test() {
		final String query = "SELECT x.* FROM (SELECT b FROM third) x JOIN (SELECT c FROM fourth) y USING (a)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=x}}}, from={join={1={table={alias=x, query={select={1={column={name=b, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 3={table={alias=y, query={select={1={column={name=c, table_ref=null}}}, from={table={alias=null, table=fourth}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={b=[[@7,24:24='b',<391>,1:24]]}, fourth={c=[[@15,53:53='c',<391>,1:53]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={b=[[@7,24:24='b',<391>,1:24]], *=[[@1,7:7='x',<391>,1:7]]}, query1={c=[[@15,53:53='c',<391>,1:53]]}, query2={*=[[@3,9:9='*',<291>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={*=[[@3,9:9='*',<291>,1:9]]}, def_query1={query_dictionary={c=[[@15,53:53='c',<391>,1:53]]}, table_dictionary={fourth={c=[[@15,53:53='c',<391>,1:53]]}}, interface={c=[{name=c, table_ref=fourth}]}}, def_query0={query_dictionary={b=[[@7,24:24='b',<391>,1:24]], *=[[@1,7:7='x',<391>,1:7]]}, table_dictionary={third={b=[[@7,24:24='b',<391>,1:24]]}}, interface={b=[{name=b, table_ref=third}], *=wildcard}}, interface={*=[{name=*, table_ref=x}]}, table_alias={x=query0, y=query1}}}",
				extractor.getSymbolTable().toString());

		assertFatalDiagnosticAtPositionWithFullMessage(
				snippet,
				"JOIN_USING_COLUMN_NOT_FOUND",
				"Join Using column 'a' at (l:1 c:77) not found in Join Sources (x, y). ",
				"a",
				1,
				77);
	}

	/*
	 * V1: same scenarios as V0 but outer SELECT lists explicit qualified columns (no alias.*).
	 * Select-list aliases disambiguate the same logical column name from both join operands.
	 */

	@Test
	public void basicJoinWithUsingSingleColumnV1Test() {
		final String query = " SELECT a.a AS ca, b.a AS da, b.b FROM third a JOIN fourth b USING (a) ";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a, table_ref=a}, alias=ca}, 2={column={name=a, table_ref=b}, alias=da}, 3={column={name=b, table_ref=b}}}, from={join={1={table={alias=a, table=third}}, 2={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[b, da, ca]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@1,8:8='a',<391>,1:8], [@24,68:68='a',<391>,1:68]]}, fourth={a=[[@7,19:19='b',<391>,1:19], [@24,68:68='a',<391>,1:68]], b=[[@13,30:30='b',<391>,1:30]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={b=[[@15,32:32='b',<391>,1:32]], da=[[@11,26:27='da',<391>,1:26]], ca=[[@5,15:16='ca',<391>,1:15]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={b=[[@15,32:32='b',<391>,1:32]], da=[[@11,26:27='da',<391>,1:26]], ca=[[@5,15:16='ca',<391>,1:15]]}, table_dictionary={third={a=[[@1,8:8='a',<391>,1:8], [@24,68:68='a',<391>,1:68]]}, fourth={a=[[@7,19:19='b',<391>,1:19], [@24,68:68='a',<391>,1:68]], b=[[@13,30:30='b',<391>,1:30]]}}, filters=[{name=a, table_ref=a}, {name=a, table_ref=b}], interface={b=[{name=b, table_ref=b}], da=[{name=a, table_ref=b}], ca=[{name=a, table_ref=a}]}, table_alias={a=third, b=fourth}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void basicJoinWithUsingTwoColumnsV1Test() {
		final String query = " SELECT a.a AS ca, a.b AS cb, b.a AS da, b.b AS db FROM third a JOIN fourth b USING (a, b) ";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a, table_ref=a}, alias=ca}, 2={column={name=b, table_ref=a}, alias=cb}, 3={column={name=a, table_ref=b}, alias=da}, 4={column={name=b, table_ref=b}, alias=db}}, from={join={1={table={alias=a, table=third}}, 2={using={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, join=JOIN}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[da, ca, db, cb]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@1,8:8='a',<391>,1:8], [@32,85:85='a',<391>,1:85]], b=[[@7,19:19='a',<391>,1:19], [@34,88:88='b',<391>,1:88]]}, fourth={a=[[@13,30:30='b',<391>,1:30], [@32,85:85='a',<391>,1:85]], b=[[@19,41:41='b',<391>,1:41], [@34,88:88='b',<391>,1:88]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={da=[[@17,37:38='da',<391>,1:37]], ca=[[@5,15:16='ca',<391>,1:15]], db=[[@23,48:49='db',<391>,1:48]], cb=[[@11,26:27='cb',<391>,1:26]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={da=[[@17,37:38='da',<391>,1:37]], ca=[[@5,15:16='ca',<391>,1:15]], db=[[@23,48:49='db',<391>,1:48]], cb=[[@11,26:27='cb',<391>,1:26]]}, table_dictionary={third={a=[[@1,8:8='a',<391>,1:8], [@32,85:85='a',<391>,1:85]], b=[[@7,19:19='a',<391>,1:19], [@34,88:88='b',<391>,1:88]]}, fourth={a=[[@13,30:30='b',<391>,1:30], [@32,85:85='a',<391>,1:85]], b=[[@19,41:41='b',<391>,1:41], [@34,88:88='b',<391>,1:88]]}}, filters=[{name=a, table_ref=a}, {name=a, table_ref=b}, {name=b, table_ref=a}, {name=b, table_ref=b}], interface={da=[{name=a, table_ref=b}], ca=[{name=a, table_ref=a}], db=[{name=b, table_ref=b}], cb=[{name=b, table_ref=a}]}, table_alias={a=third, b=fourth}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void joinUsingTwoColumnsRightSubquerySourceV1Test() {
		final String query = " SELECT a.a AS ca, a.b AS cb, b.a AS da, b.b AS db FROM third a JOIN (SELECT a, b FROM fourth) b USING (a, b) ";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a, table_ref=a}, alias=ca}, 2={column={name=b, table_ref=a}, alias=cb}, 3={column={name=a, table_ref=b}, alias=da}, 4={column={name=b, table_ref=b}, alias=db}}, from={join={1={table={alias=a, table=third}}, 2={using={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, join=JOIN}, 3={table={alias=b, query={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, from={table={alias=null, table=fourth}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[da, ca, db, cb]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@1,8:8='a',<391>,1:8], [@39,104:104='a',<391>,1:104]], b=[[@7,19:19='a',<391>,1:19], [@41,107:107='b',<391>,1:107]]}, fourth={a=[[@30,77:77='a',<391>,1:77]], b=[[@32,80:80='b',<391>,1:80]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={a=[[@30,77:77='a',<391>,1:77], [@13,30:30='b',<391>,1:30], [@39,104:104='a',<391>,1:104]], b=[[@32,80:80='b',<391>,1:80], [@19,41:41='b',<391>,1:41], [@41,107:107='b',<391>,1:107]]}, query1={da=[[@17,37:38='da',<391>,1:37]], ca=[[@5,15:16='ca',<391>,1:15]], db=[[@23,48:49='db',<391>,1:48]], cb=[[@11,26:27='cb',<391>,1:26]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={da=[[@17,37:38='da',<391>,1:37]], ca=[[@5,15:16='ca',<391>,1:15]], db=[[@23,48:49='db',<391>,1:48]], cb=[[@11,26:27='cb',<391>,1:26]]}, table_dictionary={third={a=[[@1,8:8='a',<391>,1:8], [@39,104:104='a',<391>,1:104]], b=[[@7,19:19='a',<391>,1:19], [@41,107:107='b',<391>,1:107]]}}, def_query0={query_dictionary={a=[[@30,77:77='a',<391>,1:77], [@13,30:30='b',<391>,1:30], [@39,104:104='a',<391>,1:104]], b=[[@32,80:80='b',<391>,1:80], [@19,41:41='b',<391>,1:41], [@41,107:107='b',<391>,1:107]]}, table_dictionary={fourth={a=[[@30,77:77='a',<391>,1:77]], b=[[@32,80:80='b',<391>,1:80]]}}, interface={a=[{name=a, table_ref=fourth}], b=[{name=b, table_ref=fourth}]}}, filters=[{name=a, table_ref=a}, {name=a, table_ref=b}, {name=b, table_ref=a}, {name=b, table_ref=b}], interface={da=[{name=a, table_ref=b}], ca=[{name=a, table_ref=a}], db=[{name=b, table_ref=b}], cb=[{name=b, table_ref=a}]}, table_alias={a=third, b=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void basicLeftJoinWithUsingAndWhereV1Test() {
		final String query = " SELECT a.a AS ca, b.a AS da, b.c FROM third a LEFT JOIN fourth b USING (a) "
				+ "\n WHERE b.c = 1 ";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a, table_ref=a}, alias=ca}, 2={column={name=a, table_ref=b}, alias=da}, 3={column={name=c, table_ref=b}}}, from={join={1={table={alias=a, table=third}}, 2={using={1={column={name=a, table_ref=null}}}, join=LEFT}, 3={table={alias=b, table=fourth}}}}, where={condition={left={column={name=c, table_ref=b}}, right={literal=1}, operator==}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[c, da, ca]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@1,8:8='a',<391>,1:8], [@25,73:73='a',<391>,1:73]]}, fourth={a=[[@7,19:19='b',<391>,1:19], [@25,73:73='a',<391>,1:73]], c=[[@13,30:30='b',<391>,1:30], [@28,84:84='b',<391>,2:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={c=[[@15,32:32='c',<391>,1:32]], da=[[@11,26:27='da',<391>,1:26]], ca=[[@5,15:16='ca',<391>,1:15]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={c=[[@15,32:32='c',<391>,1:32]], da=[[@11,26:27='da',<391>,1:26]], ca=[[@5,15:16='ca',<391>,1:15]]}, table_dictionary={third={a=[[@1,8:8='a',<391>,1:8], [@25,73:73='a',<391>,1:73]]}, fourth={a=[[@7,19:19='b',<391>,1:19], [@25,73:73='a',<391>,1:73]], c=[[@13,30:30='b',<391>,1:30], [@28,84:84='b',<391>,2:7]]}}, filters=[{name=a, table_ref=a}, {name=a, table_ref=b}, {name=c, table_ref=b}], interface={c=[{name=c, table_ref=b}], da=[{name=a, table_ref=b}], ca=[{name=a, table_ref=a}]}, table_alias={a=third, b=fourth}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void joinSubquerySourcesWithUsingV1Test() {
		final String query = "SELECT x.a AS xa, x.d, y.a AS ya, y.e FROM (SELECT a, d FROM third) x "
				+ "JOIN (SELECT a, e FROM fourth) y USING (a)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a, table_ref=x}, alias=xa}, 2={column={name=d, table_ref=x}}, 3={column={name=a, table_ref=y}, alias=ya}, 4={column={name=e, table_ref=y}}}, from={join={1={table={alias=x, query={select={1={column={name=a, table_ref=null}}, 2={column={name=d, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 3={table={alias=y, query={select={1={column={name=a, table_ref=null}}, 2={column={name=e, table_ref=null}}}, from={table={alias=null, table=fourth}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[d, e, ya, xa]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@23,51:51='a',<391>,1:51]], d=[[@25,54:54='d',<391>,1:54]]}, fourth={a=[[@33,83:83='a',<391>,1:83]], e=[[@35,86:86='e',<391>,1:86]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={d=[[@25,54:54='d',<391>,1:54], [@7,18:18='x',<391>,1:18]], a=[[@23,51:51='a',<391>,1:51], [@1,7:7='x',<391>,1:7], [@42,110:110='a',<391>,1:110]]}, query1={a=[[@33,83:83='a',<391>,1:83], [@11,23:23='y',<391>,1:23], [@42,110:110='a',<391>,1:110]], e=[[@35,86:86='e',<391>,1:86], [@17,34:34='y',<391>,1:34]]}, query2={ya=[[@15,30:31='ya',<391>,1:30]], xa=[[@5,14:15='xa',<391>,1:14]], d=[[@9,20:20='d',<391>,1:20]], e=[[@19,36:36='e',<391>,1:36]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={d=[[@9,20:20='d',<391>,1:20]], e=[[@19,36:36='e',<391>,1:36]], ya=[[@15,30:31='ya',<391>,1:30]], xa=[[@5,14:15='xa',<391>,1:14]]}, def_query1={query_dictionary={a=[[@33,83:83='a',<391>,1:83], [@11,23:23='y',<391>,1:23], [@42,110:110='a',<391>,1:110]], e=[[@35,86:86='e',<391>,1:86], [@17,34:34='y',<391>,1:34]]}, table_dictionary={fourth={a=[[@33,83:83='a',<391>,1:83]], e=[[@35,86:86='e',<391>,1:86]]}}, interface={a=[{name=a, table_ref=fourth}], e=[{name=e, table_ref=fourth}]}}, def_query0={query_dictionary={a=[[@23,51:51='a',<391>,1:51], [@1,7:7='x',<391>,1:7], [@42,110:110='a',<391>,1:110]], d=[[@25,54:54='d',<391>,1:54], [@7,18:18='x',<391>,1:18]]}, table_dictionary={third={a=[[@23,51:51='a',<391>,1:51]], d=[[@25,54:54='d',<391>,1:54]]}}, interface={a=[{name=a, table_ref=third}], d=[{name=d, table_ref=third}]}}, filters=[{name=a, table_ref=x}, {name=a, table_ref=y}], interface={d=[{name=d, table_ref=x}], e=[{name=e, table_ref=y}], ya=[{name=a, table_ref=y}], xa=[{name=a, table_ref=x}]}, table_alias={x=query0, y=query1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void joinUsingQualifiedColumnFatalV1Test() {
		final String query = " SELECT a.a AS ca, a.b, b.b FROM third a JOIN fourth b USING (a.a) ";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a, table_ref=a}, alias=ca}, 2={column={name=b, table_ref=a}}, 3={column={name=b, table_ref=b}}}, from={join={1={table={alias=a, table=third}}, 2={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[b, ca]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@1,8:8='a',<391>,1:8]], b=[[@7,19:19='a',<391>,1:19]]}, fourth={b=[[@11,24:24='b',<391>,1:24]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={b=[[@9,21:21='b',<391>,1:21], [@13,26:26='b',<391>,1:26]], ca=[[@5,15:16='ca',<391>,1:15]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={b=[[@9,21:21='b',<391>,1:21], [@13,26:26='b',<391>,1:26]], ca=[[@5,15:16='ca',<391>,1:15]]}, table_dictionary={third={a=[[@1,8:8='a',<391>,1:8]], b=[[@7,19:19='a',<391>,1:19]]}, fourth={b=[[@11,24:24='b',<391>,1:24]]}}, interface={b=[{name=b, table_ref=b}], ca=[{name=a, table_ref=a}]}, table_alias={a=third, b=fourth}}}",
				extractor.getSymbolTable().toString());

		assertFatalDiagnosticAtPositionWithFullMessage(
				snippet,
				"QUALIFIED_COLUMN_IN_JOIN_USING",
				"Join Using column 'a.a' at (l:1 c:62) must not be qualified.",
				"a.a",
				1,
				62);
	}

	@Test
	public void joinUsingMissingColumnOnSubqueryOperandFatalV1Test() {
		final String query = "SELECT x.a AS xa, x.d, y.b FROM (SELECT a, d FROM third) x "
				+ "JOIN (SELECT b, e FROM fourth) y USING (a)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a, table_ref=x}, alias=xa}, 2={column={name=d, table_ref=x}}, 3={column={name=b, table_ref=y}}}, from={join={1={table={alias=x, query={select={1={column={name=a, table_ref=null}}, 2={column={name=d, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 3={table={alias=y, query={select={1={column={name=b, table_ref=null}}, 2={column={name=e, table_ref=null}}}, from={table={alias=null, table=fourth}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[b, d, xa]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@17,40:40='a',<391>,1:40]], d=[[@19,43:43='d',<391>,1:43]]}, fourth={b=[[@27,72:72='b',<391>,1:72]], e=[[@29,75:75='e',<391>,1:75]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={d=[[@19,43:43='d',<391>,1:43], [@7,18:18='x',<391>,1:18]], a=[[@17,40:40='a',<391>,1:40], [@1,7:7='x',<391>,1:7], [@36,99:99='a',<391>,1:99]]}, query1={e=[[@29,75:75='e',<391>,1:75]], b=[[@27,72:72='b',<391>,1:72], [@11,23:23='y',<391>,1:23]]}, query2={xa=[[@5,14:15='xa',<391>,1:14]], b=[[@13,25:25='b',<391>,1:25]], d=[[@9,20:20='d',<391>,1:20]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={b=[[@13,25:25='b',<391>,1:25]], d=[[@9,20:20='d',<391>,1:20]], xa=[[@5,14:15='xa',<391>,1:14]]}, def_query1={query_dictionary={b=[[@27,72:72='b',<391>,1:72], [@11,23:23='y',<391>,1:23]], e=[[@29,75:75='e',<391>,1:75]]}, table_dictionary={fourth={b=[[@27,72:72='b',<391>,1:72]], e=[[@29,75:75='e',<391>,1:75]]}}, interface={b=[{name=b, table_ref=fourth}], e=[{name=e, table_ref=fourth}]}}, def_query0={query_dictionary={a=[[@17,40:40='a',<391>,1:40], [@1,7:7='x',<391>,1:7], [@36,99:99='a',<391>,1:99]], d=[[@19,43:43='d',<391>,1:43], [@7,18:18='x',<391>,1:18]]}, table_dictionary={third={a=[[@17,40:40='a',<391>,1:40]], d=[[@19,43:43='d',<391>,1:43]]}}, interface={a=[{name=a, table_ref=third}], d=[{name=d, table_ref=third}]}}, filters=[{name=a, table_ref=x}], interface={b=[{name=b, table_ref=y}], d=[{name=d, table_ref=x}], xa=[{name=a, table_ref=x}]}, table_alias={x=query0, y=query1}}}",
				extractor.getSymbolTable().toString());

		assertFatalDiagnosticAtPositionWithFullMessage(
				snippet,
				"JOIN_USING_COLUMN_NOT_FOUND",
				"Join Using column 'a' at (l:1 c:99) not found in Join Sources (y). ",
				"a",
				1,
				99);
	}

	@Test
	public void joinUsingMissingColumnOnLeftSubqueryOperandFatalV1Test() {
		final String query = "SELECT x.b AS xb, x.d, y.a AS ya, y.e FROM (SELECT b, d FROM third) x "
				+ "JOIN (SELECT a, e FROM fourth) y USING (a)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=b, table_ref=x}, alias=xb}, 2={column={name=d, table_ref=x}}, 3={column={name=a, table_ref=y}, alias=ya}, 4={column={name=e, table_ref=y}}}, from={join={1={table={alias=x, query={select={1={column={name=b, table_ref=null}}, 2={column={name=d, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 3={table={alias=y, query={select={1={column={name=a, table_ref=null}}, 2={column={name=e, table_ref=null}}}, from={table={alias=null, table=fourth}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[d, e, ya, xb]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={b=[[@23,51:51='b',<391>,1:51]], d=[[@25,54:54='d',<391>,1:54]]}, fourth={a=[[@33,83:83='a',<391>,1:83]], e=[[@35,86:86='e',<391>,1:86]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={d=[[@25,54:54='d',<391>,1:54], [@7,18:18='x',<391>,1:18]], b=[[@23,51:51='b',<391>,1:51], [@1,7:7='x',<391>,1:7]]}, query1={a=[[@33,83:83='a',<391>,1:83], [@11,23:23='y',<391>,1:23], [@42,110:110='a',<391>,1:110]], e=[[@35,86:86='e',<391>,1:86], [@17,34:34='y',<391>,1:34]]}, query2={ya=[[@15,30:31='ya',<391>,1:30]], xb=[[@5,14:15='xb',<391>,1:14]], d=[[@9,20:20='d',<391>,1:20]], e=[[@19,36:36='e',<391>,1:36]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={d=[[@9,20:20='d',<391>,1:20]], e=[[@19,36:36='e',<391>,1:36]], ya=[[@15,30:31='ya',<391>,1:30]], xb=[[@5,14:15='xb',<391>,1:14]]}, def_query1={query_dictionary={a=[[@33,83:83='a',<391>,1:83], [@11,23:23='y',<391>,1:23], [@42,110:110='a',<391>,1:110]], e=[[@35,86:86='e',<391>,1:86], [@17,34:34='y',<391>,1:34]]}, table_dictionary={fourth={a=[[@33,83:83='a',<391>,1:83]], e=[[@35,86:86='e',<391>,1:86]]}}, interface={a=[{name=a, table_ref=fourth}], e=[{name=e, table_ref=fourth}]}}, def_query0={query_dictionary={b=[[@23,51:51='b',<391>,1:51], [@1,7:7='x',<391>,1:7]], d=[[@25,54:54='d',<391>,1:54], [@7,18:18='x',<391>,1:18]]}, table_dictionary={third={b=[[@23,51:51='b',<391>,1:51]], d=[[@25,54:54='d',<391>,1:54]]}}, interface={b=[{name=b, table_ref=third}], d=[{name=d, table_ref=third}]}}, filters=[{name=a, table_ref=y}], interface={d=[{name=d, table_ref=x}], e=[{name=e, table_ref=y}], ya=[{name=a, table_ref=y}], xb=[{name=b, table_ref=x}]}, table_alias={x=query0, y=query1}}}",
				extractor.getSymbolTable().toString());

		assertFatalDiagnosticAtPositionWithFullMessage(
				snippet,
				"JOIN_USING_COLUMN_NOT_FOUND",
				"Join Using column 'a' at (l:1 c:110) not found in Join Sources (x). ",
				"a",
				1,
				110);
	}

	@Test
	public void joinUsingMissingColumnOnBothSubqueryOperandsFatalV1Test() {
		final String query = "SELECT x.b AS xb, x.d, y.c AS yc, y.e FROM (SELECT b, d FROM third) x "
				+ "JOIN (SELECT c, e FROM fourth) y USING (a)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=b, table_ref=x}, alias=xb}, 2={column={name=d, table_ref=x}}, 3={column={name=c, table_ref=y}, alias=yc}, 4={column={name=e, table_ref=y}}}, from={join={1={table={alias=x, query={select={1={column={name=b, table_ref=null}}, 2={column={name=d, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 3={table={alias=y, query={select={1={column={name=c, table_ref=null}}, 2={column={name=e, table_ref=null}}}, from={table={alias=null, table=fourth}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[d, e, xb, yc]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={b=[[@23,51:51='b',<391>,1:51]], d=[[@25,54:54='d',<391>,1:54]]}, fourth={c=[[@33,83:83='c',<391>,1:83]], e=[[@35,86:86='e',<391>,1:86]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={d=[[@25,54:54='d',<391>,1:54], [@7,18:18='x',<391>,1:18]], b=[[@23,51:51='b',<391>,1:51], [@1,7:7='x',<391>,1:7]]}, query1={e=[[@35,86:86='e',<391>,1:86], [@17,34:34='y',<391>,1:34]], c=[[@33,83:83='c',<391>,1:83], [@11,23:23='y',<391>,1:23]]}, query2={xb=[[@5,14:15='xb',<391>,1:14]], yc=[[@15,30:31='yc',<391>,1:30]], d=[[@9,20:20='d',<391>,1:20]], e=[[@19,36:36='e',<391>,1:36]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={d=[[@9,20:20='d',<391>,1:20]], e=[[@19,36:36='e',<391>,1:36]], xb=[[@5,14:15='xb',<391>,1:14]], yc=[[@15,30:31='yc',<391>,1:30]]}, def_query1={query_dictionary={c=[[@33,83:83='c',<391>,1:83], [@11,23:23='y',<391>,1:23]], e=[[@35,86:86='e',<391>,1:86], [@17,34:34='y',<391>,1:34]]}, table_dictionary={fourth={c=[[@33,83:83='c',<391>,1:83]], e=[[@35,86:86='e',<391>,1:86]]}}, interface={c=[{name=c, table_ref=fourth}], e=[{name=e, table_ref=fourth}]}}, def_query0={query_dictionary={b=[[@23,51:51='b',<391>,1:51], [@1,7:7='x',<391>,1:7]], d=[[@25,54:54='d',<391>,1:54], [@7,18:18='x',<391>,1:18]]}, table_dictionary={third={b=[[@23,51:51='b',<391>,1:51]], d=[[@25,54:54='d',<391>,1:54]]}}, interface={b=[{name=b, table_ref=third}], d=[{name=d, table_ref=third}]}}, interface={d=[{name=d, table_ref=x}], e=[{name=e, table_ref=y}], xb=[{name=b, table_ref=x}], yc=[{name=c, table_ref=y}]}, table_alias={x=query0, y=query1}}}",
				extractor.getSymbolTable().toString());

		assertFatalDiagnosticAtPositionWithFullMessage(
				snippet,
				"JOIN_USING_COLUMN_NOT_FOUND",
				"Join Using column 'a' at (l:1 c:110) not found in Join Sources (x, y). ",
				"a",
				1,
				110);
	}

	@Test
	public void joinUsingTwoColumnsRightSubquerySourceV2Test() {
		final String query = "SELECT a.a AS ca, a.b AS cb, b.a AS da, b.b AS db FROM third a JOIN (VALUES (1, 2), (3, 4)) AS b(a, b) USING (a, b)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a, table_ref=a}, alias=ca}, 2={column={name=b, table_ref=a}, alias=cb}, 3={column={name=a, table_ref=b}, alias=da}, 4={column={name=b, table_ref=b}, alias=db}}, from={join={1={table={alias=a, table=third}}, 2={using={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, join=JOIN}, 3={values={columns={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, alias=b, matrix={1={row={1={literal=1}, 2={literal=2}}}, 2={row={1={literal=3}, 2={literal=4}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[da, ca, db, cb]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@1,7:7='a',<391>,1:7], [@51,110:110='a',<391>,1:110]], b=[[@7,18:18='a',<391>,1:18], [@53,113:113='b',<391>,1:113]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{values0={a=[[@45,97:97='a',<391>,1:97], [@13,29:29='b',<391>,1:29], [@51,110:110='a',<391>,1:110]], b=[[@47,100:100='b',<391>,1:100], [@19,40:40='b',<391>,1:40], [@53,113:113='b',<391>,1:113]]}, query1={da=[[@17,36:37='da',<391>,1:36]], ca=[[@5,14:15='ca',<391>,1:14]], db=[[@23,47:48='db',<391>,1:47]], cb=[[@11,25:26='cb',<391>,1:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={da=[[@17,36:37='da',<391>,1:36]], ca=[[@5,14:15='ca',<391>,1:14]], db=[[@23,47:48='db',<391>,1:47]], cb=[[@11,25:26='cb',<391>,1:25]]}, table_dictionary={third={a=[[@1,7:7='a',<391>,1:7], [@51,110:110='a',<391>,1:110]], b=[[@7,18:18='a',<391>,1:18], [@53,113:113='b',<391>,1:113]]}}, def_values0={query_dictionary={a=[[@45,97:97='a',<391>,1:97], [@13,29:29='b',<391>,1:29], [@51,110:110='a',<391>,1:110]], b=[[@47,100:100='b',<391>,1:100], [@19,40:40='b',<391>,1:40], [@53,113:113='b',<391>,1:113]]}, interface={a=[], b=[]}}, filters=[{name=a, table_ref=a}, {name=a, table_ref=b}, {name=b, table_ref=a}, {name=b, table_ref=b}], interface={da=[{name=a, table_ref=b}], ca=[{name=a, table_ref=a}], db=[{name=b, table_ref=b}], cb=[{name=b, table_ref=a}]}, table_alias={a=third, b=values0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinUsingSingleColumnSubqueryOperandsV2Test() {
		final String query = "SELECT x.a AS xa, x.d, y.a AS ya, y.e FROM (SELECT a, d FROM third) x JOIN (VALUES (10, 20)) AS y(a, e) USING (a)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a, table_ref=x}, alias=xa}, 2={column={name=d, table_ref=x}}, 3={column={name=a, table_ref=y}, alias=ya}, 4={column={name=e, table_ref=y}}}, from={join={1={table={alias=x, query={select={1={column={name=a, table_ref=null}}, 2={column={name=d, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 3={values={columns={1={column={name=a, table_ref=null}}, 2={column={name=e, table_ref=null}}}, alias=y, matrix={1={row={1={literal=10}, 2={literal=20}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[d, e, ya, xa]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@23,51:51='a',<391>,1:51]], d=[[@25,54:54='d',<391>,1:54]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{values1={a=[[@42,98:98='a',<391>,1:98], [@11,23:23='y',<391>,1:23], [@48,111:111='a',<391>,1:111]], e=[[@44,101:101='e',<391>,1:101], [@17,34:34='y',<391>,1:34]]}, query0={d=[[@25,54:54='d',<391>,1:54], [@7,18:18='x',<391>,1:18]], a=[[@23,51:51='a',<391>,1:51], [@1,7:7='x',<391>,1:7], [@48,111:111='a',<391>,1:111]]}, query2={ya=[[@15,30:31='ya',<391>,1:30]], xa=[[@5,14:15='xa',<391>,1:14]], d=[[@9,20:20='d',<391>,1:20]], e=[[@19,36:36='e',<391>,1:36]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={d=[[@9,20:20='d',<391>,1:20]], e=[[@19,36:36='e',<391>,1:36]], ya=[[@15,30:31='ya',<391>,1:30]], xa=[[@5,14:15='xa',<391>,1:14]]}, def_values1={query_dictionary={a=[[@42,98:98='a',<391>,1:98], [@11,23:23='y',<391>,1:23], [@48,111:111='a',<391>,1:111]], e=[[@44,101:101='e',<391>,1:101], [@17,34:34='y',<391>,1:34]]}, interface={a=[], e=[]}}, def_query0={query_dictionary={a=[[@23,51:51='a',<391>,1:51], [@1,7:7='x',<391>,1:7], [@48,111:111='a',<391>,1:111]], d=[[@25,54:54='d',<391>,1:54], [@7,18:18='x',<391>,1:18]]}, table_dictionary={third={a=[[@23,51:51='a',<391>,1:51]], d=[[@25,54:54='d',<391>,1:54]]}}, interface={a=[{name=a, table_ref=third}], d=[{name=d, table_ref=third}]}}, filters=[{name=a, table_ref=x}, {name=a, table_ref=y}], interface={d=[{name=d, table_ref=x}], e=[{name=e, table_ref=y}], ya=[{name=a, table_ref=y}], xa=[{name=a, table_ref=x}]}, table_alias={x=query0, y=values1}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinSubquerySourcesWithUsingV2Test() {
		final String query = "SELECT x.a AS xa, x.d, y.a AS ya, y.e FROM (VALUES (1, 3)) AS x(a, d) JOIN (VALUES (2, 4)) AS y(a, e) USING (a)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a, table_ref=x}, alias=xa}, 2={column={name=d, table_ref=x}}, 3={column={name=a, table_ref=y}, alias=ya}, 4={column={name=e, table_ref=y}}}, from={join={1={values={columns={1={column={name=a, table_ref=null}}, 2={column={name=d, table_ref=null}}}, alias=x, matrix={1={row={1={literal=1}, 2={literal=3}}}}}}, 2={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 3={values={columns={1={column={name=a, table_ref=null}}, 2={column={name=e, table_ref=null}}}, alias=y, matrix={1={row={1={literal=2}, 2={literal=4}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[d, e, ya, xa]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{values0={d=[[@34,67:67='d',<391>,1:67], [@7,18:18='x',<391>,1:18]], a=[[@32,64:64='a',<391>,1:64], [@1,7:7='x',<391>,1:7]]}, values1={a=[[@48,96:96='a',<391>,1:96], [@11,23:23='y',<391>,1:23]], e=[[@50,99:99='e',<391>,1:99], [@17,34:34='y',<391>,1:34]]}, query2={ya=[[@15,30:31='ya',<391>,1:30]], xa=[[@5,14:15='xa',<391>,1:14]], d=[[@9,20:20='d',<391>,1:20]], e=[[@19,36:36='e',<391>,1:36]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={join_using_operand_token_by_name={a=[@54,109:109='a',<391>,1:109]}, query_dictionary={d=[[@9,20:20='d',<391>,1:20]], e=[[@19,36:36='e',<391>,1:36]], ya=[[@15,30:31='ya',<391>,1:30]], xa=[[@5,14:15='xa',<391>,1:14]]}, def_values1={query_dictionary={a=[[@48,96:96='a',<391>,1:96], [@11,23:23='y',<391>,1:23]], e=[[@50,99:99='e',<391>,1:99], [@17,34:34='y',<391>,1:34]]}, interface={a=[], e=[]}}, def_values0={query_dictionary={a=[[@32,64:64='a',<391>,1:64], [@1,7:7='x',<391>,1:7]], d=[[@34,67:67='d',<391>,1:67], [@7,18:18='x',<391>,1:18]]}, interface={a=[], d=[]}}, join_using_operand_token_refs={a=[[@54,109:109='a',<391>,1:109]]}, interface={d=[{name=d, table_ref=x}], e=[{name=e, table_ref=y}], ya=[{name=a, table_ref=y}], xa=[{name=a, table_ref=x}]}, table_alias={x=values0, y=values1}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinUsingTwoColumnsRightSubquerySourceV3Test() {
		final String query = "SELECT a.empid AS ca, a.jan_sales_SUM AS cb, b.empid AS da, "
				+ "\n b.jan_sales_SUM AS db FROM (SELECT empid, sales_amount AS "
				+ "\n jan_sales_SUM FROM monthly_sales_long WHERE month_name = "
				+ "\n 'jan_sales') a JOIN (SELECT src.empid, jan_sales_SUM FROM "
				+ "\n (SELECT empid, month_name, sales_amount FROM "
				+ "\n monthly_sales_long) src PIVOT (SUM(sales_amount) FOR "
				+ "\n month_name IN ('jan_sales')) p) b USING (empid, "
				+ "\n jan_sales_SUM)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=a}, alias=ca}, 2={column={name=jan_sales_SUM, table_ref=a}, alias=cb}, 3={column={name=empid, table_ref=b}, alias=da}, 4={column={name=jan_sales_SUM, table_ref=b}, alias=db}}, from={join={1={table={alias=a, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}, alias=jan_sales_SUM}}, from={table={alias=null, table=monthly_sales_long}}, where={condition={left={column={name=month_name, table_ref=null}}, right={literal='jan_sales'}, operator==}}}}}, 2={using={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales_SUM, table_ref=null}}}, join=JOIN}, 3={table={alias=b, query={select={1={column={name=empid, table_ref=src}}, 2={column={name=jan_sales_SUM, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=p, table={alias=src, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}}, from={table={alias=null, table=monthly_sales_long}}}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[da, ca, db, cb]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{src={month_name=[[@67,343:352='month_name',<391>,7:1]], sales_amount=[[@64,323:334='sales_amount',<391>,6:36]]}, monthly_sales_long={empid=[[@27,97:101='empid',<391>,2:36], [@51,249:253='empid',<391>,5:9]], month_name=[[@35,166:175='month_name',<391>,3:45], [@53,256:265='month_name',<391>,5:16]], sales_amount=[[@29,104:115='sales_amount',<391>,2:43], [@55,268:279='sales_amount',<391>,5:28]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={empid=[[@27,97:101='empid',<391>,2:36], [@1,7:7='a',<391>,1:7], [@78,384:388='empid',<391>,7:42]], jan_sales_SUM=[[@31,122:134='jan_sales_SUM',<391>,3:1], [@7,22:22='a',<391>,1:22], [@80,393:405='jan_sales_SUM',<391>,8:1]]}, query1={empid=[[@51,249:253='empid',<391>,5:9], [@43,209:211='src',<391>,4:29]], month_name=[[@53,256:265='month_name',<391>,5:16]], sales_amount=[[@55,268:279='sales_amount',<391>,5:28]]}, query2={empid=[[@45,213:217='empid',<391>,4:33], [@13,45:45='b',<391>,1:45], [@78,384:388='empid',<391>,7:42]], jan_sales_SUM=[[@47,220:232='jan_sales_SUM',<391>,4:40], [@62,319:321='SUM',<141>,6:32], [@70,358:368=''jan_sales'',<399>,7:16], [@19,62:62='b',<391>,2:1], [@80,393:405='jan_sales_SUM',<391>,8:1]]}, query3={da=[[@17,56:57='da',<391>,1:56]], ca=[[@5,18:19='ca',<391>,1:18]], db=[[@23,81:82='db',<391>,2:20]], cb=[[@11,41:42='cb',<391>,1:41]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query3={query_dictionary={da=[[@17,56:57='da',<391>,1:56]], ca=[[@5,18:19='ca',<391>,1:18]], db=[[@23,81:82='db',<391>,2:20]], cb=[[@11,41:42='cb',<391>,1:41]]}, def_query0={query_dictionary={empid=[[@27,97:101='empid',<391>,2:36], [@1,7:7='a',<391>,1:7], [@78,384:388='empid',<391>,7:42]], jan_sales_SUM=[[@31,122:134='jan_sales_SUM',<391>,3:1], [@7,22:22='a',<391>,1:22], [@80,393:405='jan_sales_SUM',<391>,8:1]]}, table_dictionary={monthly_sales_long={empid=[[@27,97:101='empid',<391>,2:36], [@51,249:253='empid',<391>,5:9]], month_name=[[@35,166:175='month_name',<391>,3:45], [@53,256:265='month_name',<391>,5:16]], sales_amount=[[@29,104:115='sales_amount',<391>,2:43], [@55,268:279='sales_amount',<391>,5:28]]}}, filters=[{name=month_name, table_ref=monthly_sales_long}], interface={empid=[{name=empid, table_ref=monthly_sales_long}], jan_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}]}}, filters=[{name=empid, table_ref=a}, {name=empid, table_ref=b}, {name=jan_sales_SUM, table_ref=a}, {name=jan_sales_SUM, table_ref=b}], interface={da=[{name=empid, table_ref=b}], ca=[{name=empid, table_ref=a}], db=[{name=jan_sales_SUM, table_ref=b}], cb=[{name=jan_sales_SUM, table_ref=a}]}, table_alias={a=query0, b=query2}, def_query2={query_dictionary={empid=[[@45,213:217='empid',<391>,4:33], [@13,45:45='b',<391>,1:45], [@78,384:388='empid',<391>,7:42]], jan_sales_SUM=[[@47,220:232='jan_sales_SUM',<391>,4:40], [@62,319:321='SUM',<141>,6:32], [@70,358:368=''jan_sales'',<399>,7:16], [@19,62:62='b',<391>,2:1], [@80,393:405='jan_sales_SUM',<391>,8:1]]}, table_dictionary={src={month_name=[[@67,343:352='month_name',<391>,7:1]], sales_amount=[[@64,323:334='sales_amount',<391>,6:36]]}}, def_query1={query_dictionary={empid=[[@51,249:253='empid',<391>,5:9], [@43,209:211='src',<391>,4:29]], month_name=[[@53,256:265='month_name',<391>,5:16]], sales_amount=[[@55,268:279='sales_amount',<391>,5:28]]}, table_dictionary={monthly_sales_long={empid=[[@51,249:253='empid',<391>,5:9]], month_name=[[@53,256:265='month_name',<391>,5:16]], sales_amount=[[@55,268:279='sales_amount',<391>,5:28]]}}, interface={empid=[{name=empid, table_ref=monthly_sales_long}], month_name=[{name=month_name, table_ref=monthly_sales_long}], sales_amount=[{name=sales_amount, table_ref=monthly_sales_long}]}}, derivation={source_columns={p=[{name=month_name, table_ref=src}, {name=sales_amount, table_ref=src}]}, derived_columns={p={jan_sales_SUM=[[@62,319:321='SUM',<141>,6:32], [@70,358:368=''jan_sales'',<399>,7:16]]}}}, interface={empid=[{name=empid, table_ref=src}], jan_sales_SUM=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=src}, {name=sales_amount, table_ref=src}]}, table_alias={src=query1, p=src}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinUsingSingleColumnSubqueryOperandsV3Test() {
		final String query = "SELECT x.empid AS xa, x.jan_sales_SUM AS xd, y.empid AS ya, "
				+ "\n y.jan_sales_SUM AS ye FROM (SELECT empid, sales_amount AS "
				+ "\n jan_sales_SUM FROM monthly_sales_long WHERE month_name = "
				+ "\n 'jan_sales') x JOIN (SELECT src.empid, jan_sales_SUM FROM "
				+ "\n (SELECT empid, month_name, sales_amount FROM "
				+ "\n monthly_sales_long) src PIVOT (SUM(sales_amount) FOR "
				+ "\n month_name IN ('jan_sales')) p) y USING (empid, "
				+ "\n jan_sales_SUM)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=x}, alias=xa}, 2={column={name=jan_sales_SUM, table_ref=x}, alias=xd}, 3={column={name=empid, table_ref=y}, alias=ya}, 4={column={name=jan_sales_SUM, table_ref=y}, alias=ye}}, from={join={1={table={alias=x, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}, alias=jan_sales_SUM}}, from={table={alias=null, table=monthly_sales_long}}, where={condition={left={column={name=month_name, table_ref=null}}, right={literal='jan_sales'}, operator==}}}}}, 2={using={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales_SUM, table_ref=null}}}, join=JOIN}, 3={table={alias=y, query={select={1={column={name=empid, table_ref=src}}, 2={column={name=jan_sales_SUM, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=p, table={alias=src, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}}, from={table={alias=null, table=monthly_sales_long}}}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ya, xa, xd, ye]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{src={month_name=[[@67,343:352='month_name',<391>,7:1]], sales_amount=[[@64,323:334='sales_amount',<391>,6:36]]}, monthly_sales_long={empid=[[@27,97:101='empid',<391>,2:36], [@51,249:253='empid',<391>,5:9]], month_name=[[@35,166:175='month_name',<391>,3:45], [@53,256:265='month_name',<391>,5:16]], sales_amount=[[@29,104:115='sales_amount',<391>,2:43], [@55,268:279='sales_amount',<391>,5:28]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={empid=[[@27,97:101='empid',<391>,2:36], [@1,7:7='x',<391>,1:7], [@78,384:388='empid',<391>,7:42]], jan_sales_SUM=[[@31,122:134='jan_sales_SUM',<391>,3:1], [@7,22:22='x',<391>,1:22], [@80,393:405='jan_sales_SUM',<391>,8:1]]}, query1={empid=[[@51,249:253='empid',<391>,5:9], [@43,209:211='src',<391>,4:29]], month_name=[[@53,256:265='month_name',<391>,5:16]], sales_amount=[[@55,268:279='sales_amount',<391>,5:28]]}, query2={empid=[[@45,213:217='empid',<391>,4:33], [@13,45:45='y',<391>,1:45], [@78,384:388='empid',<391>,7:42]], jan_sales_SUM=[[@47,220:232='jan_sales_SUM',<391>,4:40], [@62,319:321='SUM',<141>,6:32], [@70,358:368=''jan_sales'',<399>,7:16], [@19,62:62='y',<391>,2:1], [@80,393:405='jan_sales_SUM',<391>,8:1]]}, query3={ya=[[@17,56:57='ya',<391>,1:56]], xa=[[@5,18:19='xa',<391>,1:18]], xd=[[@11,41:42='xd',<391>,1:41]], ye=[[@23,81:82='ye',<391>,2:20]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query3={query_dictionary={ya=[[@17,56:57='ya',<391>,1:56]], xa=[[@5,18:19='xa',<391>,1:18]], xd=[[@11,41:42='xd',<391>,1:41]], ye=[[@23,81:82='ye',<391>,2:20]]}, def_query0={query_dictionary={empid=[[@27,97:101='empid',<391>,2:36], [@1,7:7='x',<391>,1:7], [@78,384:388='empid',<391>,7:42]], jan_sales_SUM=[[@31,122:134='jan_sales_SUM',<391>,3:1], [@7,22:22='x',<391>,1:22], [@80,393:405='jan_sales_SUM',<391>,8:1]]}, table_dictionary={monthly_sales_long={empid=[[@27,97:101='empid',<391>,2:36], [@51,249:253='empid',<391>,5:9]], month_name=[[@35,166:175='month_name',<391>,3:45], [@53,256:265='month_name',<391>,5:16]], sales_amount=[[@29,104:115='sales_amount',<391>,2:43], [@55,268:279='sales_amount',<391>,5:28]]}}, filters=[{name=month_name, table_ref=monthly_sales_long}], interface={empid=[{name=empid, table_ref=monthly_sales_long}], jan_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}]}}, filters=[{name=empid, table_ref=x}, {name=empid, table_ref=y}, {name=jan_sales_SUM, table_ref=x}, {name=jan_sales_SUM, table_ref=y}], interface={ya=[{name=empid, table_ref=y}], xa=[{name=empid, table_ref=x}], xd=[{name=jan_sales_SUM, table_ref=x}], ye=[{name=jan_sales_SUM, table_ref=y}]}, table_alias={x=query0, y=query2}, def_query2={query_dictionary={empid=[[@45,213:217='empid',<391>,4:33], [@13,45:45='y',<391>,1:45], [@78,384:388='empid',<391>,7:42]], jan_sales_SUM=[[@47,220:232='jan_sales_SUM',<391>,4:40], [@62,319:321='SUM',<141>,6:32], [@70,358:368=''jan_sales'',<399>,7:16], [@19,62:62='y',<391>,2:1], [@80,393:405='jan_sales_SUM',<391>,8:1]]}, table_dictionary={src={month_name=[[@67,343:352='month_name',<391>,7:1]], sales_amount=[[@64,323:334='sales_amount',<391>,6:36]]}}, def_query1={query_dictionary={empid=[[@51,249:253='empid',<391>,5:9], [@43,209:211='src',<391>,4:29]], month_name=[[@53,256:265='month_name',<391>,5:16]], sales_amount=[[@55,268:279='sales_amount',<391>,5:28]]}, table_dictionary={monthly_sales_long={empid=[[@51,249:253='empid',<391>,5:9]], month_name=[[@53,256:265='month_name',<391>,5:16]], sales_amount=[[@55,268:279='sales_amount',<391>,5:28]]}}, interface={empid=[{name=empid, table_ref=monthly_sales_long}], month_name=[{name=month_name, table_ref=monthly_sales_long}], sales_amount=[{name=sales_amount, table_ref=monthly_sales_long}]}}, derivation={source_columns={p=[{name=month_name, table_ref=src}, {name=sales_amount, table_ref=src}]}, derived_columns={p={jan_sales_SUM=[[@62,319:321='SUM',<141>,6:32], [@70,358:368=''jan_sales'',<399>,7:16]]}}}, interface={empid=[{name=empid, table_ref=src}], jan_sales_SUM=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=src}, {name=sales_amount, table_ref=src}]}, table_alias={src=query1, p=src}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinSubquerySourcesWithUsingV3Test() {
		final String query = "SELECT x.empid AS xa, x.jan_sales_SUM AS xd, y.empid AS ya, "
				+ "\n y.jan_sales_SUM AS ye FROM (SELECT empid, sales_amount AS "
				+ "\n jan_sales_SUM FROM monthly_sales_long WHERE month_name = "
				+ "\n 'jan_sales') x JOIN (SELECT src.empid, jan_sales_SUM FROM "
				+ "\n (SELECT empid, month_name, sales_amount FROM "
				+ "\n monthly_sales_long) src PIVOT (SUM(sales_amount) FOR "
				+ "\n month_name IN ('jan_sales')) p) y USING (empid, "
				+ "\n jan_sales_SUM)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=x}, alias=xa}, 2={column={name=jan_sales_SUM, table_ref=x}, alias=xd}, 3={column={name=empid, table_ref=y}, alias=ya}, 4={column={name=jan_sales_SUM, table_ref=y}, alias=ye}}, from={join={1={table={alias=x, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}, alias=jan_sales_SUM}}, from={table={alias=null, table=monthly_sales_long}}, where={condition={left={column={name=month_name, table_ref=null}}, right={literal='jan_sales'}, operator==}}}}}, 2={using={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales_SUM, table_ref=null}}}, join=JOIN}, 3={table={alias=y, query={select={1={column={name=empid, table_ref=src}}, 2={column={name=jan_sales_SUM, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=p, table={alias=src, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}}, from={table={alias=null, table=monthly_sales_long}}}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ya, xa, xd, ye]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{src={month_name=[[@67,343:352='month_name',<391>,7:1]], sales_amount=[[@64,323:334='sales_amount',<391>,6:36]]}, monthly_sales_long={empid=[[@27,97:101='empid',<391>,2:36], [@51,249:253='empid',<391>,5:9]], month_name=[[@35,166:175='month_name',<391>,3:45], [@53,256:265='month_name',<391>,5:16]], sales_amount=[[@29,104:115='sales_amount',<391>,2:43], [@55,268:279='sales_amount',<391>,5:28]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={empid=[[@27,97:101='empid',<391>,2:36], [@1,7:7='x',<391>,1:7], [@78,384:388='empid',<391>,7:42]], jan_sales_SUM=[[@31,122:134='jan_sales_SUM',<391>,3:1], [@7,22:22='x',<391>,1:22], [@80,393:405='jan_sales_SUM',<391>,8:1]]}, query1={empid=[[@51,249:253='empid',<391>,5:9], [@43,209:211='src',<391>,4:29]], month_name=[[@53,256:265='month_name',<391>,5:16]], sales_amount=[[@55,268:279='sales_amount',<391>,5:28]]}, query2={empid=[[@45,213:217='empid',<391>,4:33], [@13,45:45='y',<391>,1:45], [@78,384:388='empid',<391>,7:42]], jan_sales_SUM=[[@47,220:232='jan_sales_SUM',<391>,4:40], [@62,319:321='SUM',<141>,6:32], [@70,358:368=''jan_sales'',<399>,7:16], [@19,62:62='y',<391>,2:1], [@80,393:405='jan_sales_SUM',<391>,8:1]]}, query3={ya=[[@17,56:57='ya',<391>,1:56]], xa=[[@5,18:19='xa',<391>,1:18]], xd=[[@11,41:42='xd',<391>,1:41]], ye=[[@23,81:82='ye',<391>,2:20]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query3={query_dictionary={ya=[[@17,56:57='ya',<391>,1:56]], xa=[[@5,18:19='xa',<391>,1:18]], xd=[[@11,41:42='xd',<391>,1:41]], ye=[[@23,81:82='ye',<391>,2:20]]}, def_query0={query_dictionary={empid=[[@27,97:101='empid',<391>,2:36], [@1,7:7='x',<391>,1:7], [@78,384:388='empid',<391>,7:42]], jan_sales_SUM=[[@31,122:134='jan_sales_SUM',<391>,3:1], [@7,22:22='x',<391>,1:22], [@80,393:405='jan_sales_SUM',<391>,8:1]]}, table_dictionary={monthly_sales_long={empid=[[@27,97:101='empid',<391>,2:36], [@51,249:253='empid',<391>,5:9]], month_name=[[@35,166:175='month_name',<391>,3:45], [@53,256:265='month_name',<391>,5:16]], sales_amount=[[@29,104:115='sales_amount',<391>,2:43], [@55,268:279='sales_amount',<391>,5:28]]}}, filters=[{name=month_name, table_ref=monthly_sales_long}], interface={empid=[{name=empid, table_ref=monthly_sales_long}], jan_sales_SUM=[{name=sales_amount, table_ref=monthly_sales_long}]}}, filters=[{name=empid, table_ref=x}, {name=empid, table_ref=y}, {name=jan_sales_SUM, table_ref=x}, {name=jan_sales_SUM, table_ref=y}], interface={ya=[{name=empid, table_ref=y}], xa=[{name=empid, table_ref=x}], xd=[{name=jan_sales_SUM, table_ref=x}], ye=[{name=jan_sales_SUM, table_ref=y}]}, table_alias={x=query0, y=query2}, def_query2={query_dictionary={empid=[[@45,213:217='empid',<391>,4:33], [@13,45:45='y',<391>,1:45], [@78,384:388='empid',<391>,7:42]], jan_sales_SUM=[[@47,220:232='jan_sales_SUM',<391>,4:40], [@62,319:321='SUM',<141>,6:32], [@70,358:368=''jan_sales'',<399>,7:16], [@19,62:62='y',<391>,2:1], [@80,393:405='jan_sales_SUM',<391>,8:1]]}, table_dictionary={src={month_name=[[@67,343:352='month_name',<391>,7:1]], sales_amount=[[@64,323:334='sales_amount',<391>,6:36]]}}, def_query1={query_dictionary={empid=[[@51,249:253='empid',<391>,5:9], [@43,209:211='src',<391>,4:29]], month_name=[[@53,256:265='month_name',<391>,5:16]], sales_amount=[[@55,268:279='sales_amount',<391>,5:28]]}, table_dictionary={monthly_sales_long={empid=[[@51,249:253='empid',<391>,5:9]], month_name=[[@53,256:265='month_name',<391>,5:16]], sales_amount=[[@55,268:279='sales_amount',<391>,5:28]]}}, interface={empid=[{name=empid, table_ref=monthly_sales_long}], month_name=[{name=month_name, table_ref=monthly_sales_long}], sales_amount=[{name=sales_amount, table_ref=monthly_sales_long}]}}, derivation={source_columns={p=[{name=month_name, table_ref=src}, {name=sales_amount, table_ref=src}]}, derived_columns={p={jan_sales_SUM=[[@62,319:321='SUM',<141>,6:32], [@70,358:368=''jan_sales'',<399>,7:16]]}}}, interface={empid=[{name=empid, table_ref=src}], jan_sales_SUM=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=src}, {name=sales_amount, table_ref=src}]}, table_alias={src=query1, p=src}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinUsingTwoColumnsRightSubquerySourceV4Test() {
		final String query = "SELECT la.empid AS ca, la.jan_sales_SUM AS cb, rb.empid AS "
				+ "\n da, rb.jan_sales_SUM AS db FROM monthly_sales_long la PIVOT "
				+ "\n (SUM(sales_amount) FOR month_name IN ('jan_sales')) la JOIN "
				+ "\n monthly_sales_long rb PIVOT (SUM(sales_amount) FOR "
				+ "\n month_name IN ('jan_sales')) rb USING (empid, jan_sales_SUM)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=la}, alias=ca}, 2={column={name=jan_sales_SUM, table_ref=la}, alias=cb}, 3={column={name=empid, table_ref=rb}, alias=da}, 4={column={name=jan_sales_SUM, table_ref=rb}, alias=db}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=la, table={alias=la, table=monthly_sales_long}}, 2={using={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales_SUM, table_ref=null}}}, join=JOIN}, 3={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=rb, table={alias=rb, table=monthly_sales_long}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[da, ca, db, cb]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{monthly_sales_long={empid=[[@1,7:8='la',<391>,1:7], [@60,277:281='empid',<391>,5:40], [@13,47:48='rb',<391>,1:47]], month_name=[[@51,238:247='month_name',<391>,5:1], [@34,146:155='month_name',<391>,3:24]], sales_amount=[[@48,218:229='sales_amount',<391>,4:34], [@31,128:139='sales_amount',<391>,3:6]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={da=[[@17,61:62='da',<391>,2:1]], ca=[[@5,19:20='ca',<391>,1:19]], db=[[@23,85:86='db',<391>,2:25]], cb=[[@11,43:44='cb',<391>,1:43]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={da=[[@17,61:62='da',<391>,2:1]], ca=[[@5,19:20='ca',<391>,1:19]], db=[[@23,85:86='db',<391>,2:25]], cb=[[@11,43:44='cb',<391>,1:43]]}, table_dictionary={monthly_sales_long={month_name=[[@51,238:247='month_name',<391>,5:1], [@34,146:155='month_name',<391>,3:24]], empid=[[@1,7:8='la',<391>,1:7], [@60,277:281='empid',<391>,5:40], [@13,47:48='rb',<391>,1:47]], sales_amount=[[@48,218:229='sales_amount',<391>,4:34], [@31,128:139='sales_amount',<391>,3:6]]}}, derivation={source_columns={rb=[{name=month_name, table_ref=rb}, {name=sales_amount, table_ref=rb}], la=[{name=month_name, table_ref=la}, {name=sales_amount, table_ref=la}]}, derived_columns={rb={jan_sales_SUM=[[@46,214:216='SUM',<141>,4:30], [@54,253:263=''jan_sales'',<399>,5:16]]}, la={jan_sales_SUM=[[@29,124:126='SUM',<141>,3:2], [@37,161:171=''jan_sales'',<399>,3:39]]}}}, filters=[{name=empid, table_ref=la}, {name=empid, table_ref=rb}, {name=jan_sales_SUM, table_ref=la}, {name=jan_sales_SUM, table_ref=rb}], interface={da=[{name=empid, table_ref=rb}], ca=[{name=empid, table_ref=la}], db=[{name=jan_sales_SUM, table_ref=rb}], cb=[{name=jan_sales_SUM, table_ref=la}]}, table_alias={rb=monthly_sales_long, la=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinUsingSingleColumnSubqueryOperandsV4Test() {
		final String query = "SELECT la.empid AS xa, la.month_name AS xd, rb.empid AS ya, "
				+ "\n rb.jan_sales_SUM AS ye FROM monthly_sales_long la JOIN "
				+ "\n monthly_sales_long rb PIVOT (SUM(sales_amount) FOR "
				+ "\n month_name IN ('jan_sales')) rb USING (empid, jan_sales_SUM)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=la}, alias=xa}, 2={column={name=month_name, table_ref=la}, alias=xd}, 3={column={name=empid, table_ref=rb}, alias=ya}, 4={column={name=jan_sales_SUM, table_ref=rb}, alias=ye}}, from={join={1={table={alias=la, table=monthly_sales_long}}, 2={using={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales_SUM, table_ref=null}}}, join=JOIN}, 3={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=rb, table={alias=rb, table=monthly_sales_long}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ya, xa, xd, ye]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{monthly_sales_long={empid=[[@1,7:8='la',<391>,1:7], [@46,211:215='empid',<391>,4:40], [@13,44:45='rb',<391>,1:44]], month_name=[[@7,23:24='la',<391>,1:23], [@37,172:181='month_name',<391>,4:1]], sales_amount=[[@34,152:163='sales_amount',<391>,3:34]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={ya=[[@17,56:57='ya',<391>,1:56]], xa=[[@5,19:20='xa',<391>,1:19]], xd=[[@11,40:41='xd',<391>,1:40]], ye=[[@23,82:83='ye',<391>,2:21]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={ya=[[@17,56:57='ya',<391>,1:56]], xa=[[@5,19:20='xa',<391>,1:19]], xd=[[@11,40:41='xd',<391>,1:40]], ye=[[@23,82:83='ye',<391>,2:21]]}, table_dictionary={monthly_sales_long={month_name=[[@37,172:181='month_name',<391>,4:1], [@7,23:24='la',<391>,1:23]], empid=[[@1,7:8='la',<391>,1:7], [@46,211:215='empid',<391>,4:40], [@13,44:45='rb',<391>,1:44]], sales_amount=[[@34,152:163='sales_amount',<391>,3:34]]}}, derivation={source_columns={rb=[{name=month_name, table_ref=rb}, {name=sales_amount, table_ref=rb}]}, derived_columns={rb={jan_sales_SUM=[[@32,148:150='SUM',<141>,3:30], [@40,187:197=''jan_sales'',<399>,4:16]]}}}, filters=[{name=empid, table_ref=la}, {name=empid, table_ref=rb}, {name=jan_sales_SUM, table_ref=rb}, {name=month_name, table_ref=rb}, {name=sales_amount, table_ref=rb}], interface={ya=[{name=empid, table_ref=rb}], xa=[{name=empid, table_ref=la}], xd=[{name=month_name, table_ref=la}], ye=[{name=jan_sales_SUM, table_ref=rb}]}, table_alias={rb=monthly_sales_long, la=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinSubquerySourcesWithUsingV4Test() {
		final String query = "SELECT la.empid AS xa, la.jan_sales_SUM AS xd, rb.empid AS "
				+ "\n ya, rb.jan_sales_SUM AS ye FROM monthly_sales_long la PIVOT "
				+ "\n (SUM(sales_amount) FOR month_name IN ('jan_sales')) la JOIN "
				+ "\n monthly_sales_long rb PIVOT (SUM(sales_amount) FOR "
				+ "\n month_name IN ('jan_sales')) rb USING (empid, jan_sales_SUM)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=la}, alias=xa}, 2={column={name=jan_sales_SUM, table_ref=la}, alias=xd}, 3={column={name=empid, table_ref=rb}, alias=ya}, 4={column={name=jan_sales_SUM, table_ref=rb}, alias=ye}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=la, table={alias=la, table=monthly_sales_long}}, 2={using={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales_SUM, table_ref=null}}}, join=JOIN}, 3={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=rb, table={alias=rb, table=monthly_sales_long}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ya, xa, xd, ye]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{monthly_sales_long={empid=[[@1,7:8='la',<391>,1:7], [@60,277:281='empid',<391>,5:40], [@13,47:48='rb',<391>,1:47]], month_name=[[@51,238:247='month_name',<391>,5:1], [@34,146:155='month_name',<391>,3:24]], sales_amount=[[@48,218:229='sales_amount',<391>,4:34], [@31,128:139='sales_amount',<391>,3:6]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={ya=[[@17,61:62='ya',<391>,2:1]], xa=[[@5,19:20='xa',<391>,1:19]], xd=[[@11,43:44='xd',<391>,1:43]], ye=[[@23,85:86='ye',<391>,2:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={ya=[[@17,61:62='ya',<391>,2:1]], xa=[[@5,19:20='xa',<391>,1:19]], xd=[[@11,43:44='xd',<391>,1:43]], ye=[[@23,85:86='ye',<391>,2:25]]}, table_dictionary={monthly_sales_long={month_name=[[@51,238:247='month_name',<391>,5:1], [@34,146:155='month_name',<391>,3:24]], empid=[[@1,7:8='la',<391>,1:7], [@60,277:281='empid',<391>,5:40], [@13,47:48='rb',<391>,1:47]], sales_amount=[[@48,218:229='sales_amount',<391>,4:34], [@31,128:139='sales_amount',<391>,3:6]]}}, derivation={source_columns={rb=[{name=month_name, table_ref=rb}, {name=sales_amount, table_ref=rb}], la=[{name=month_name, table_ref=la}, {name=sales_amount, table_ref=la}]}, derived_columns={rb={jan_sales_SUM=[[@46,214:216='SUM',<141>,4:30], [@54,253:263=''jan_sales'',<399>,5:16]]}, la={jan_sales_SUM=[[@29,124:126='SUM',<141>,3:2], [@37,161:171=''jan_sales'',<399>,3:39]]}}}, filters=[{name=empid, table_ref=la}, {name=empid, table_ref=rb}, {name=jan_sales_SUM, table_ref=la}, {name=jan_sales_SUM, table_ref=rb}], interface={ya=[{name=empid, table_ref=rb}], xa=[{name=empid, table_ref=la}], xd=[{name=jan_sales_SUM, table_ref=la}], ye=[{name=jan_sales_SUM, table_ref=rb}]}, table_alias={rb=monthly_sales_long, la=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinUsingTwoColumnsRightSubquerySourceV5Test() {
		final String query = "SELECT a.a AS ca, a.b AS cb, b.seq AS da, b.value AS db FROM "
				+ "\n third a JOIN TABLE(SPLIT_TO_TABLE('1,2', ',')) b USING (seq, "
				+ "\n value)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a, table_ref=a}, alias=ca}, 2={column={name=b, table_ref=a}, alias=cb}, 3={column={name=seq, table_ref=b}, alias=da}, 4={column={name=value, table_ref=b}, alias=db}}, from={join={1={table={alias=a, table=third}}, 2={using={1={column={name=seq, table_ref=null}}, 2={column={name=value, table_ref=null}}}, join=JOIN}, 3={table={alias=b, table_function={function={function_name=SPLIT_TO_TABLE, parameters={1={literal='1,2'}, 2={literal=','}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[da, ca, db, cb]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@1,7:7='a',<391>,1:7]], b=[[@7,18:18='a',<391>,1:18]], value=[[@42,126:130='value',<391>,3:1]], seq=[[@40,119:121='seq',<391>,2:57]]}, table_function0={value=[[@19,42:42='b',<391>,1:42], [@42,126:130='value',<391>,3:1]], seq=[[@13,29:29='b',<391>,1:29], [@40,119:121='seq',<391>,2:57]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={da=[[@17,38:39='da',<391>,1:38]], ca=[[@5,14:15='ca',<391>,1:14]], db=[[@23,53:54='db',<391>,1:53]], cb=[[@11,25:26='cb',<391>,1:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={da=[[@17,38:39='da',<391>,1:38]], ca=[[@5,14:15='ca',<391>,1:14]], db=[[@23,53:54='db',<391>,1:53]], cb=[[@11,25:26='cb',<391>,1:25]]}, table_dictionary={third={a=[[@1,7:7='a',<391>,1:7]], b=[[@7,18:18='a',<391>,1:18]], value=[[@42,126:130='value',<391>,3:1]], seq=[[@40,119:121='seq',<391>,2:57]]}, table_function0={value=[[@19,42:42='b',<391>,1:42], [@42,126:130='value',<391>,3:1]], seq=[[@13,29:29='b',<391>,1:29], [@40,119:121='seq',<391>,2:57]]}}, filters=[{name=seq, table_ref=a}, {name=seq, table_ref=b}, {name=value, table_ref=a}, {name=value, table_ref=b}], interface={da=[{name=seq, table_ref=b}], ca=[{name=a, table_ref=a}], db=[{name=value, table_ref=b}], cb=[{name=b, table_ref=a}]}, table_alias={a=third, b=table_function0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinUsingSingleColumnSubqueryOperandsV5Test() {
		final String query = "SELECT x.a AS xa, x.d, y.seq AS ya, y.value AS ye FROM "
				+ "\n (SELECT a, d FROM third) x JOIN "
				+ "\n TABLE(SPLIT_TO_TABLE('10,20', ',')) y USING (a)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a, table_ref=x}, alias=xa}, 2={column={name=d, table_ref=x}}, 3={column={name=seq, table_ref=y}, alias=ya}, 4={column={name=value, table_ref=y}, alias=ye}}, from={join={1={table={alias=x, query={select={1={column={name=a, table_ref=null}}, 2={column={name=d, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 3={table={alias=y, table_function={function={function_name=SPLIT_TO_TABLE, parameters={1={literal='10,20'}, 2={literal=','}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[d, ya, xa, ye]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@25,65:65='a',<391>,2:9]], d=[[@27,68:68='d',<391>,2:12]]}, table_function0={a=[[@45,136:136='a',<391>,3:46]], value=[[@17,36:36='y',<391>,1:36]], seq=[[@11,23:23='y',<391>,1:23]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={d=[[@27,68:68='d',<391>,2:12], [@7,18:18='x',<391>,1:18]], a=[[@25,65:65='a',<391>,2:9], [@1,7:7='x',<391>,1:7], [@45,136:136='a',<391>,3:46]]}, query1={ya=[[@15,32:33='ya',<391>,1:32]], xa=[[@5,14:15='xa',<391>,1:14]], d=[[@9,20:20='d',<391>,1:20]], ye=[[@21,47:48='ye',<391>,1:47]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={d=[[@9,20:20='d',<391>,1:20]], ya=[[@15,32:33='ya',<391>,1:32]], xa=[[@5,14:15='xa',<391>,1:14]], ye=[[@21,47:48='ye',<391>,1:47]]}, table_dictionary={table_function0={a=[[@45,136:136='a',<391>,3:46]], value=[[@17,36:36='y',<391>,1:36]], seq=[[@11,23:23='y',<391>,1:23]]}}, def_query0={query_dictionary={a=[[@25,65:65='a',<391>,2:9], [@1,7:7='x',<391>,1:7], [@45,136:136='a',<391>,3:46]], d=[[@27,68:68='d',<391>,2:12], [@7,18:18='x',<391>,1:18]]}, table_dictionary={third={a=[[@25,65:65='a',<391>,2:9]], d=[[@27,68:68='d',<391>,2:12]]}}, interface={a=[{name=a, table_ref=third}], d=[{name=d, table_ref=third}]}}, filters=[{name=a, table_ref=x}, {name=a, table_ref=y}], interface={d=[{name=d, table_ref=x}], ya=[{name=seq, table_ref=y}], xa=[{name=a, table_ref=x}], ye=[{name=value, table_ref=y}]}, table_alias={x=query0, y=table_function0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinSubquerySourcesWithUsingV5Test() {
		final String query = "SELECT x.a AS xa, x.d, y.value AS ye FROM (VALUES (1, 3)) AS "
				+ "\n x(a, d) JOIN TABLE(SPLIT_TO_TABLE('2,4', ',')) y USING "
				+ "\n (value)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a, table_ref=x}, alias=xa}, 2={column={name=d, table_ref=x}}, 3={column={name=value, table_ref=y}, alias=ye}}, from={join={1={values={columns={1={column={name=a, table_ref=null}}, 2={column={name=d, table_ref=null}}}, alias=x, matrix={1={row={1={literal=1}, 2={literal=3}}}}}}, 2={using={1={column={name=value, table_ref=null}}}, join=JOIN}, 3={table={alias=y, table_function={function={function_name=SPLIT_TO_TABLE, parameters={1={literal='2,4'}, 2={literal=','}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[d, xa, ye]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{table_function0={value=[[@11,23:23='y',<391>,1:23]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{values0={d=[[@30,68:68='d',<391>,2:6], [@7,18:18='x',<391>,1:18]], a=[[@28,65:65='a',<391>,2:3], [@1,7:7='x',<391>,1:7]]}, query1={xa=[[@5,14:15='xa',<391>,1:14]], d=[[@9,20:20='d',<391>,1:20]], ye=[[@15,34:35='ye',<391>,1:34]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={join_using_operand_token_by_name={value=[@45,121:125='value',<391>,3:2]}, query_dictionary={d=[[@9,20:20='d',<391>,1:20]], xa=[[@5,14:15='xa',<391>,1:14]], ye=[[@15,34:35='ye',<391>,1:34]]}, table_dictionary={table_function0={value=[[@11,23:23='y',<391>,1:23]]}}, def_values0={query_dictionary={a=[[@28,65:65='a',<391>,2:3], [@1,7:7='x',<391>,1:7]], d=[[@30,68:68='d',<391>,2:6], [@7,18:18='x',<391>,1:18]]}, interface={a=[], d=[]}}, join_using_operand_token_refs={value=[[@45,121:125='value',<391>,3:2]]}, interface={d=[{name=d, table_ref=x}], xa=[{name=a, table_ref=x}], ye=[{name=value, table_ref=y}]}, table_alias={x=values0, y=table_function0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinUsingTwoColumnsRightSubquerySourceV6Test() {
		final String query = "SELECT a.empid AS ca, a.sales_amount AS cb, b.empid AS da, "
				+ "\n b.sales_amount AS db FROM (SELECT empid, sales_amount FROM "
				+ "\n monthly_sales_long WHERE month_name = 'jan_sales') a JOIN "
				+ "\n (SELECT src.empid, sales_amount FROM (SELECT empid, "
				+ "\n jan_sales, feb_sales FROM monthly_sales) src UNPIVOT "
				+ "\n (sales_amount FOR month_name IN (jan_sales)) u) b USING "
				+ "\n (empid, sales_amount)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=a}, alias=ca}, 2={column={name=sales_amount, table_ref=a}, alias=cb}, 3={column={name=empid, table_ref=b}, alias=da}, 4={column={name=sales_amount, table_ref=b}, alias=db}}, from={join={1={table={alias=a, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}}, from={table={alias=null, table=monthly_sales_long}}, where={condition={left={column={name=month_name, table_ref=null}}, right={literal='jan_sales'}, operator==}}}}}, 2={using={1={column={name=empid, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}}, join=JOIN}, 3={table={alias=b, query={select={1={column={name=empid, table_ref=src}}, 2={column={name=sales_amount, table_ref=null}}}, from={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}}}, alias=u, table={alias=src, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}}, from={table={alias=null, table=monthly_sales}}}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[da, ca, db, cb]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{monthly_sales={jan_sales=[[@51,236:244='jan_sales',<391>,5:1]], empid=[[@49,227:231='empid',<391>,4:46]], feb_sales=[[@53,247:255='feb_sales',<391>,5:12]]}, src={jan_sales=[[@65,324:332='jan_sales',<391>,6:34]]}, monthly_sales_long={empid=[[@27,95:99='empid',<391>,2:35]], month_name=[[@33,147:156='month_name',<391>,3:26]], sales_amount=[[@29,102:113='sales_amount',<391>,2:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={empid=[[@27,95:99='empid',<391>,2:35], [@1,7:7='a',<391>,1:7], [@73,350:354='empid',<391>,7:2]], sales_amount=[[@29,102:113='sales_amount',<391>,2:42], [@7,22:22='a',<391>,1:22], [@75,357:368='sales_amount',<391>,7:9]]}, query1={jan_sales=[[@51,236:244='jan_sales',<391>,5:1]], empid=[[@49,227:231='empid',<391>,4:46], [@41,190:192='src',<391>,4:9]], feb_sales=[[@53,247:255='feb_sales',<391>,5:12]]}, query2={empid=[[@43,194:198='empid',<391>,4:13], [@13,44:44='b',<391>,1:44], [@73,350:354='empid',<391>,7:2]], sales_amount=[[@45,201:212='sales_amount',<391>,4:20], [@60,292:303='sales_amount',<391>,6:2], [@19,61:61='b',<391>,2:1], [@75,357:368='sales_amount',<391>,7:9]]}, query3={da=[[@17,55:56='da',<391>,1:55]], ca=[[@5,18:19='ca',<391>,1:18]], db=[[@23,79:80='db',<391>,2:19]], cb=[[@11,40:41='cb',<391>,1:40]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query3={query_dictionary={da=[[@17,55:56='da',<391>,1:55]], ca=[[@5,18:19='ca',<391>,1:18]], db=[[@23,79:80='db',<391>,2:19]], cb=[[@11,40:41='cb',<391>,1:40]]}, def_query0={query_dictionary={empid=[[@27,95:99='empid',<391>,2:35], [@1,7:7='a',<391>,1:7], [@73,350:354='empid',<391>,7:2]], sales_amount=[[@29,102:113='sales_amount',<391>,2:42], [@7,22:22='a',<391>,1:22], [@75,357:368='sales_amount',<391>,7:9]]}, table_dictionary={monthly_sales_long={empid=[[@27,95:99='empid',<391>,2:35]], month_name=[[@33,147:156='month_name',<391>,3:26]], sales_amount=[[@29,102:113='sales_amount',<391>,2:42]]}}, filters=[{name=month_name, table_ref=monthly_sales_long}], interface={empid=[{name=empid, table_ref=monthly_sales_long}], sales_amount=[{name=sales_amount, table_ref=monthly_sales_long}]}}, filters=[{name=empid, table_ref=a}, {name=empid, table_ref=b}, {name=sales_amount, table_ref=a}, {name=sales_amount, table_ref=b}], interface={da=[{name=empid, table_ref=b}], ca=[{name=empid, table_ref=a}], db=[{name=sales_amount, table_ref=b}], cb=[{name=sales_amount, table_ref=a}]}, table_alias={a=query0, b=query2}, def_query2={query_dictionary={empid=[[@43,194:198='empid',<391>,4:13], [@13,44:44='b',<391>,1:44], [@73,350:354='empid',<391>,7:2]], sales_amount=[[@45,201:212='sales_amount',<391>,4:20], [@60,292:303='sales_amount',<391>,6:2], [@19,61:61='b',<391>,2:1], [@75,357:368='sales_amount',<391>,7:9]]}, table_dictionary={src={jan_sales=[[@65,324:332='jan_sales',<391>,6:34]]}}, def_query1={query_dictionary={jan_sales=[[@51,236:244='jan_sales',<391>,5:1]], empid=[[@49,227:231='empid',<391>,4:46], [@41,190:192='src',<391>,4:9]], feb_sales=[[@53,247:255='feb_sales',<391>,5:12]]}, table_dictionary={monthly_sales={jan_sales=[[@51,236:244='jan_sales',<391>,5:1]], empid=[[@49,227:231='empid',<391>,4:46]], feb_sales=[[@53,247:255='feb_sales',<391>,5:12]]}}, interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales}], empid=[{name=empid, table_ref=monthly_sales}], feb_sales=[{name=feb_sales, table_ref=monthly_sales}]}}, derivation={source_columns={u=[{name=jan_sales, table_ref=src}]}, derived_columns={u={sales_amount=[[@60,292:303='sales_amount',<391>,6:2]], month_name=[[@62,309:318='month_name',<391>,6:19]]}}}, interface={empid=[{name=empid, table_ref=src}], sales_amount=[{name=jan_sales, table_ref=u}]}, table_alias={src=query1, u=src}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinUsingSingleColumnSubqueryOperandsV6Test() {
		final String query = "SELECT x.empid AS xa, x.sales_amount AS xd, y.empid AS ya, "
				+ "\n y.sales_amount AS ye FROM (SELECT empid, sales_amount FROM "
				+ "\n monthly_sales_long WHERE month_name = 'jan_sales') x JOIN "
				+ "\n (SELECT src.empid, sales_amount FROM (SELECT empid, "
				+ "\n jan_sales, feb_sales FROM monthly_sales) src UNPIVOT "
				+ "\n (sales_amount FOR month_name IN (jan_sales)) u) y USING "
				+ "\n (empid, sales_amount)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=x}, alias=xa}, 2={column={name=sales_amount, table_ref=x}, alias=xd}, 3={column={name=empid, table_ref=y}, alias=ya}, 4={column={name=sales_amount, table_ref=y}, alias=ye}}, from={join={1={table={alias=x, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}}, from={table={alias=null, table=monthly_sales_long}}, where={condition={left={column={name=month_name, table_ref=null}}, right={literal='jan_sales'}, operator==}}}}}, 2={using={1={column={name=empid, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}}, join=JOIN}, 3={table={alias=y, query={select={1={column={name=empid, table_ref=src}}, 2={column={name=sales_amount, table_ref=null}}}, from={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}}}, alias=u, table={alias=src, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}}, from={table={alias=null, table=monthly_sales}}}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ya, xa, xd, ye]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{monthly_sales={jan_sales=[[@51,236:244='jan_sales',<391>,5:1]], empid=[[@49,227:231='empid',<391>,4:46]], feb_sales=[[@53,247:255='feb_sales',<391>,5:12]]}, src={jan_sales=[[@65,324:332='jan_sales',<391>,6:34]]}, monthly_sales_long={empid=[[@27,95:99='empid',<391>,2:35]], month_name=[[@33,147:156='month_name',<391>,3:26]], sales_amount=[[@29,102:113='sales_amount',<391>,2:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={empid=[[@27,95:99='empid',<391>,2:35], [@1,7:7='x',<391>,1:7], [@73,350:354='empid',<391>,7:2]], sales_amount=[[@29,102:113='sales_amount',<391>,2:42], [@7,22:22='x',<391>,1:22], [@75,357:368='sales_amount',<391>,7:9]]}, query1={jan_sales=[[@51,236:244='jan_sales',<391>,5:1]], empid=[[@49,227:231='empid',<391>,4:46], [@41,190:192='src',<391>,4:9]], feb_sales=[[@53,247:255='feb_sales',<391>,5:12]]}, query2={empid=[[@43,194:198='empid',<391>,4:13], [@13,44:44='y',<391>,1:44], [@73,350:354='empid',<391>,7:2]], sales_amount=[[@45,201:212='sales_amount',<391>,4:20], [@60,292:303='sales_amount',<391>,6:2], [@19,61:61='y',<391>,2:1], [@75,357:368='sales_amount',<391>,7:9]]}, query3={ya=[[@17,55:56='ya',<391>,1:55]], xa=[[@5,18:19='xa',<391>,1:18]], xd=[[@11,40:41='xd',<391>,1:40]], ye=[[@23,79:80='ye',<391>,2:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query3={query_dictionary={ya=[[@17,55:56='ya',<391>,1:55]], xa=[[@5,18:19='xa',<391>,1:18]], xd=[[@11,40:41='xd',<391>,1:40]], ye=[[@23,79:80='ye',<391>,2:19]]}, def_query0={query_dictionary={empid=[[@27,95:99='empid',<391>,2:35], [@1,7:7='x',<391>,1:7], [@73,350:354='empid',<391>,7:2]], sales_amount=[[@29,102:113='sales_amount',<391>,2:42], [@7,22:22='x',<391>,1:22], [@75,357:368='sales_amount',<391>,7:9]]}, table_dictionary={monthly_sales_long={empid=[[@27,95:99='empid',<391>,2:35]], month_name=[[@33,147:156='month_name',<391>,3:26]], sales_amount=[[@29,102:113='sales_amount',<391>,2:42]]}}, filters=[{name=month_name, table_ref=monthly_sales_long}], interface={empid=[{name=empid, table_ref=monthly_sales_long}], sales_amount=[{name=sales_amount, table_ref=monthly_sales_long}]}}, filters=[{name=empid, table_ref=x}, {name=empid, table_ref=y}, {name=sales_amount, table_ref=x}, {name=sales_amount, table_ref=y}], interface={ya=[{name=empid, table_ref=y}], xa=[{name=empid, table_ref=x}], xd=[{name=sales_amount, table_ref=x}], ye=[{name=sales_amount, table_ref=y}]}, table_alias={x=query0, y=query2}, def_query2={query_dictionary={empid=[[@43,194:198='empid',<391>,4:13], [@13,44:44='y',<391>,1:44], [@73,350:354='empid',<391>,7:2]], sales_amount=[[@45,201:212='sales_amount',<391>,4:20], [@60,292:303='sales_amount',<391>,6:2], [@19,61:61='y',<391>,2:1], [@75,357:368='sales_amount',<391>,7:9]]}, table_dictionary={src={jan_sales=[[@65,324:332='jan_sales',<391>,6:34]]}}, def_query1={query_dictionary={jan_sales=[[@51,236:244='jan_sales',<391>,5:1]], empid=[[@49,227:231='empid',<391>,4:46], [@41,190:192='src',<391>,4:9]], feb_sales=[[@53,247:255='feb_sales',<391>,5:12]]}, table_dictionary={monthly_sales={jan_sales=[[@51,236:244='jan_sales',<391>,5:1]], empid=[[@49,227:231='empid',<391>,4:46]], feb_sales=[[@53,247:255='feb_sales',<391>,5:12]]}}, interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales}], empid=[{name=empid, table_ref=monthly_sales}], feb_sales=[{name=feb_sales, table_ref=monthly_sales}]}}, derivation={source_columns={u=[{name=jan_sales, table_ref=src}]}, derived_columns={u={sales_amount=[[@60,292:303='sales_amount',<391>,6:2]], month_name=[[@62,309:318='month_name',<391>,6:19]]}}}, interface={empid=[{name=empid, table_ref=src}], sales_amount=[{name=jan_sales, table_ref=u}]}, table_alias={src=query1, u=src}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinUsingTwoColumnsRightSubquerySourceV7Test() {
		final String query = "SELECT la.empid AS ca, la.sales_amount AS cb, rb.empid AS "
				+ "\n da, rb.sales_amount AS db FROM monthly_sales la UNPIVOT "
				+ "\n (sales_amount FOR month_name IN (jan_sales)) la JOIN "
				+ "\n monthly_sales rb UNPIVOT (sales_amount FOR month_name IN "
				+ "\n (jan_sales)) rb USING (empid, sales_amount)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=la}, alias=ca}, 2={column={name=sales_amount, table_ref=la}, alias=cb}, 3={column={name=empid, table_ref=rb}, alias=da}, 4={column={name=sales_amount, table_ref=rb}, alias=db}}, from={join={1={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}}}, alias=la, table={alias=la, table=monthly_sales}}, 2={using={1={column={name=empid, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}}, join=JOIN}, 3={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}}}, alias=rb, table={alias=rb, table=monthly_sales}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[da, ca, db, cb]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{monthly_sales={jan_sales=[[@48,233:241='jan_sales',<391>,5:2], [@34,151:159='jan_sales',<391>,3:34]], empid=[[@1,7:8='la',<391>,1:7], [@54,255:259='empid',<391>,5:24], [@13,46:47='rb',<391>,1:46]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={da=[[@17,60:61='da',<391>,2:1]], ca=[[@5,19:20='ca',<391>,1:19]], db=[[@23,83:84='db',<391>,2:24]], cb=[[@11,42:43='cb',<391>,1:42]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={da=[[@17,60:61='da',<391>,2:1]], ca=[[@5,19:20='ca',<391>,1:19]], db=[[@23,83:84='db',<391>,2:24]], cb=[[@11,42:43='cb',<391>,1:42]]}, table_dictionary={monthly_sales={jan_sales=[[@48,233:241='jan_sales',<391>,5:2], [@34,151:159='jan_sales',<391>,3:34]], empid=[[@1,7:8='la',<391>,1:7], [@54,255:259='empid',<391>,5:24], [@13,46:47='rb',<391>,1:46]]}}, derivation={source_columns={rb=[{name=jan_sales, table_ref=rb}], la=[{name=jan_sales, table_ref=la}]}, derived_columns={rb={sales_amount=[[@43,199:210='sales_amount',<391>,4:27]], month_name=[[@45,216:225='month_name',<391>,4:44]]}, la={sales_amount=[[@29,119:130='sales_amount',<391>,3:2]], month_name=[[@31,136:145='month_name',<391>,3:19]]}}}, filters=[{name=empid, table_ref=la}, {name=empid, table_ref=rb}, {name=sales_amount, table_ref=la}, {name=sales_amount, table_ref=rb}], interface={da=[{name=empid, table_ref=rb}], ca=[{name=empid, table_ref=la}], db=[{name=sales_amount, table_ref=rb}], cb=[{name=sales_amount, table_ref=la}]}, table_alias={rb=monthly_sales, la=monthly_sales}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinUsingSingleColumnSubqueryOperandsV7Test() {
		final String query = "SELECT la.empid AS xa, la.sales_amount AS xd, rb.empid AS "
				+ "\n ya, rb.sales_amount AS ye FROM monthly_sales_long la JOIN "
				+ "\n monthly_sales rb UNPIVOT (sales_amount FOR month_name IN "
				+ "\n (jan_sales)) rb USING (empid, sales_amount)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=la}, alias=xa}, 2={column={name=sales_amount, table_ref=la}, alias=xd}, 3={column={name=empid, table_ref=rb}, alias=ya}, 4={column={name=sales_amount, table_ref=rb}, alias=ye}}, from={join={1={table={alias=la, table=monthly_sales_long}}, 2={using={1={column={name=empid, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}}, join=JOIN}, 3={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}}}, alias=rb, table={alias=rb, table=monthly_sales}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ya, xa, xd, ye]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{monthly_sales={jan_sales=[[@37,180:188='jan_sales',<391>,4:2]], empid=[[@13,46:47='rb',<391>,1:46], [@43,202:206='empid',<391>,4:24]]}, monthly_sales_long={empid=[[@1,7:8='la',<391>,1:7], [@43,202:206='empid',<391>,4:24]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={ya=[[@17,60:61='ya',<391>,2:1]], xa=[[@5,19:20='xa',<391>,1:19]], xd=[[@11,42:43='xd',<391>,1:42]], ye=[[@23,83:84='ye',<391>,2:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={ya=[[@17,60:61='ya',<391>,2:1]], xa=[[@5,19:20='xa',<391>,1:19]], xd=[[@11,42:43='xd',<391>,1:42]], ye=[[@23,83:84='ye',<391>,2:24]]}, table_dictionary={monthly_sales={jan_sales=[[@37,180:188='jan_sales',<391>,4:2]], empid=[[@13,46:47='rb',<391>,1:46], [@43,202:206='empid',<391>,4:24]]}, monthly_sales_long={empid=[[@1,7:8='la',<391>,1:7], [@43,202:206='empid',<391>,4:24]]}}, derivation={source_columns={rb=[{name=jan_sales, table_ref=rb}]}, derived_columns={rb={sales_amount=[[@32,146:157='sales_amount',<391>,3:27]], month_name=[[@34,163:172='month_name',<391>,3:44]]}}}, filters=[{name=empid, table_ref=la}, {name=empid, table_ref=rb}, {name=sales_amount, table_ref=rb}, {name=jan_sales, table_ref=rb}], interface={ya=[{name=empid, table_ref=rb}], xa=[{name=empid, table_ref=la}], xd=[{name=jan_sales, table_ref=rb}], ye=[{name=jan_sales, table_ref=rb}]}, table_alias={rb=monthly_sales, la=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinSubquerySourcesWithUsingV6Test() {
		final String query = "SELECT x.empid AS xa, x.sales_amount AS xd, y.empid AS ya, "
				+ "\n y.sales_amount AS ye FROM (SELECT empid, sales_amount FROM "
				+ "\n monthly_sales_long WHERE month_name = 'jan_sales') x JOIN "
				+ "\n (SELECT src.empid, sales_amount FROM (SELECT empid, "
				+ "\n jan_sales, feb_sales FROM monthly_sales) src UNPIVOT "
				+ "\n (sales_amount FOR month_name IN (jan_sales)) u) y USING "
				+ "\n (empid, sales_amount)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=x}, alias=xa}, 2={column={name=sales_amount, table_ref=x}, alias=xd}, 3={column={name=empid, table_ref=y}, alias=ya}, 4={column={name=sales_amount, table_ref=y}, alias=ye}}, from={join={1={table={alias=x, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}}, from={table={alias=null, table=monthly_sales_long}}, where={condition={left={column={name=month_name, table_ref=null}}, right={literal='jan_sales'}, operator==}}}}}, 2={using={1={column={name=empid, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}}, join=JOIN}, 3={table={alias=y, query={select={1={column={name=empid, table_ref=src}}, 2={column={name=sales_amount, table_ref=null}}}, from={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}}}, alias=u, table={alias=src, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}}, from={table={alias=null, table=monthly_sales}}}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ya, xa, xd, ye]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{monthly_sales={jan_sales=[[@51,236:244='jan_sales',<391>,5:1]], empid=[[@49,227:231='empid',<391>,4:46]], feb_sales=[[@53,247:255='feb_sales',<391>,5:12]]}, src={jan_sales=[[@65,324:332='jan_sales',<391>,6:34]]}, monthly_sales_long={empid=[[@27,95:99='empid',<391>,2:35]], month_name=[[@33,147:156='month_name',<391>,3:26]], sales_amount=[[@29,102:113='sales_amount',<391>,2:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={empid=[[@27,95:99='empid',<391>,2:35], [@1,7:7='x',<391>,1:7], [@73,350:354='empid',<391>,7:2]], sales_amount=[[@29,102:113='sales_amount',<391>,2:42], [@7,22:22='x',<391>,1:22], [@75,357:368='sales_amount',<391>,7:9]]}, query1={jan_sales=[[@51,236:244='jan_sales',<391>,5:1]], empid=[[@49,227:231='empid',<391>,4:46], [@41,190:192='src',<391>,4:9]], feb_sales=[[@53,247:255='feb_sales',<391>,5:12]]}, query2={empid=[[@43,194:198='empid',<391>,4:13], [@13,44:44='y',<391>,1:44], [@73,350:354='empid',<391>,7:2]], sales_amount=[[@45,201:212='sales_amount',<391>,4:20], [@60,292:303='sales_amount',<391>,6:2], [@19,61:61='y',<391>,2:1], [@75,357:368='sales_amount',<391>,7:9]]}, query3={ya=[[@17,55:56='ya',<391>,1:55]], xa=[[@5,18:19='xa',<391>,1:18]], xd=[[@11,40:41='xd',<391>,1:40]], ye=[[@23,79:80='ye',<391>,2:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query3={query_dictionary={ya=[[@17,55:56='ya',<391>,1:55]], xa=[[@5,18:19='xa',<391>,1:18]], xd=[[@11,40:41='xd',<391>,1:40]], ye=[[@23,79:80='ye',<391>,2:19]]}, def_query0={query_dictionary={empid=[[@27,95:99='empid',<391>,2:35], [@1,7:7='x',<391>,1:7], [@73,350:354='empid',<391>,7:2]], sales_amount=[[@29,102:113='sales_amount',<391>,2:42], [@7,22:22='x',<391>,1:22], [@75,357:368='sales_amount',<391>,7:9]]}, table_dictionary={monthly_sales_long={empid=[[@27,95:99='empid',<391>,2:35]], month_name=[[@33,147:156='month_name',<391>,3:26]], sales_amount=[[@29,102:113='sales_amount',<391>,2:42]]}}, filters=[{name=month_name, table_ref=monthly_sales_long}], interface={empid=[{name=empid, table_ref=monthly_sales_long}], sales_amount=[{name=sales_amount, table_ref=monthly_sales_long}]}}, filters=[{name=empid, table_ref=x}, {name=empid, table_ref=y}, {name=sales_amount, table_ref=x}, {name=sales_amount, table_ref=y}], interface={ya=[{name=empid, table_ref=y}], xa=[{name=empid, table_ref=x}], xd=[{name=sales_amount, table_ref=x}], ye=[{name=sales_amount, table_ref=y}]}, table_alias={x=query0, y=query2}, def_query2={query_dictionary={empid=[[@43,194:198='empid',<391>,4:13], [@13,44:44='y',<391>,1:44], [@73,350:354='empid',<391>,7:2]], sales_amount=[[@45,201:212='sales_amount',<391>,4:20], [@60,292:303='sales_amount',<391>,6:2], [@19,61:61='y',<391>,2:1], [@75,357:368='sales_amount',<391>,7:9]]}, table_dictionary={src={jan_sales=[[@65,324:332='jan_sales',<391>,6:34]]}}, def_query1={query_dictionary={jan_sales=[[@51,236:244='jan_sales',<391>,5:1]], empid=[[@49,227:231='empid',<391>,4:46], [@41,190:192='src',<391>,4:9]], feb_sales=[[@53,247:255='feb_sales',<391>,5:12]]}, table_dictionary={monthly_sales={jan_sales=[[@51,236:244='jan_sales',<391>,5:1]], empid=[[@49,227:231='empid',<391>,4:46]], feb_sales=[[@53,247:255='feb_sales',<391>,5:12]]}}, interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales}], empid=[{name=empid, table_ref=monthly_sales}], feb_sales=[{name=feb_sales, table_ref=monthly_sales}]}}, derivation={source_columns={u=[{name=jan_sales, table_ref=src}]}, derived_columns={u={sales_amount=[[@60,292:303='sales_amount',<391>,6:2]], month_name=[[@62,309:318='month_name',<391>,6:19]]}}}, interface={empid=[{name=empid, table_ref=src}], sales_amount=[{name=jan_sales, table_ref=u}]}, table_alias={src=query1, u=src}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinSubquerySourcesWithUsingV7Test() {
		final String query = "SELECT la.empid AS xa, la.sales_amount AS xd, rb.empid AS "
				+ "\n ya, rb.sales_amount AS ye FROM monthly_sales la UNPIVOT "
				+ "\n (sales_amount FOR month_name IN (jan_sales)) la JOIN "
				+ "\n monthly_sales rb UNPIVOT (sales_amount FOR month_name IN "
				+ "\n (jan_sales)) rb USING (empid, sales_amount)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=la}, alias=xa}, 2={column={name=sales_amount, table_ref=la}, alias=xd}, 3={column={name=empid, table_ref=rb}, alias=ya}, 4={column={name=sales_amount, table_ref=rb}, alias=ye}}, from={join={1={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}}}, alias=la, table={alias=la, table=monthly_sales}}, 2={using={1={column={name=empid, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}}, join=JOIN}, 3={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}}}, alias=rb, table={alias=rb, table=monthly_sales}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[ya, xa, xd, ye]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{monthly_sales={jan_sales=[[@48,233:241='jan_sales',<391>,5:2], [@34,151:159='jan_sales',<391>,3:34]], empid=[[@1,7:8='la',<391>,1:7], [@54,255:259='empid',<391>,5:24], [@13,46:47='rb',<391>,1:46]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={ya=[[@17,60:61='ya',<391>,2:1]], xa=[[@5,19:20='xa',<391>,1:19]], xd=[[@11,42:43='xd',<391>,1:42]], ye=[[@23,83:84='ye',<391>,2:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={ya=[[@17,60:61='ya',<391>,2:1]], xa=[[@5,19:20='xa',<391>,1:19]], xd=[[@11,42:43='xd',<391>,1:42]], ye=[[@23,83:84='ye',<391>,2:24]]}, table_dictionary={monthly_sales={jan_sales=[[@48,233:241='jan_sales',<391>,5:2], [@34,151:159='jan_sales',<391>,3:34]], empid=[[@1,7:8='la',<391>,1:7], [@54,255:259='empid',<391>,5:24], [@13,46:47='rb',<391>,1:46]]}}, derivation={source_columns={rb=[{name=jan_sales, table_ref=rb}], la=[{name=jan_sales, table_ref=la}]}, derived_columns={rb={sales_amount=[[@43,199:210='sales_amount',<391>,4:27]], month_name=[[@45,216:225='month_name',<391>,4:44]]}, la={sales_amount=[[@29,119:130='sales_amount',<391>,3:2]], month_name=[[@31,136:145='month_name',<391>,3:19]]}}}, filters=[{name=empid, table_ref=la}, {name=empid, table_ref=rb}, {name=sales_amount, table_ref=la}, {name=sales_amount, table_ref=rb}], interface={ya=[{name=empid, table_ref=rb}], xa=[{name=empid, table_ref=la}], xd=[{name=sales_amount, table_ref=la}], ye=[{name=sales_amount, table_ref=rb}]}, table_alias={rb=monthly_sales, la=monthly_sales}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinUsingTwoColumnsRightTupleSubstitutionSourceV8Test() {
		final String query = "SELECT a.a AS ca, a.b AS cb, b.a AS da, b.b AS db FROM third a JOIN <[Join Right].[Tuple Feed]> b USING (a, b)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a, table_ref=a}, alias=ca}, 2={column={name=b, table_ref=a}, alias=cb}, 3={column={name=a, table_ref=b}, alias=da}, 4={column={name=b, table_ref=b}, alias=db}}, from={join={1={table={alias=a, table=third}}, 2={using={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, join=JOIN}, 3={table={alias=b, substitution={name=<[Join Right].[Tuple Feed]>, parts={1=[Join Right], 2=[Tuple Feed]}, type=tuple}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[da, ca, db, cb]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[Join Right].[Tuple Feed]>=tuple}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@1,7:7='a',<391>,1:7], [@32,105:105='a',<391>,1:105]], b=[[@7,18:18='a',<391>,1:18], [@34,108:108='b',<391>,1:108]]}, <[Join Right].[Tuple Feed]>={a=[[@13,29:29='b',<391>,1:29], [@32,105:105='a',<391>,1:105]], b=[[@19,40:40='b',<391>,1:40], [@34,108:108='b',<391>,1:108]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={da=[[@17,36:37='da',<391>,1:36]], ca=[[@5,14:15='ca',<391>,1:14]], db=[[@23,47:48='db',<391>,1:47]], cb=[[@11,25:26='cb',<391>,1:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={da=[[@17,36:37='da',<391>,1:36]], ca=[[@5,14:15='ca',<391>,1:14]], db=[[@23,47:48='db',<391>,1:47]], cb=[[@11,25:26='cb',<391>,1:25]]}, table_dictionary={third={a=[[@1,7:7='a',<391>,1:7], [@32,105:105='a',<391>,1:105]], b=[[@7,18:18='a',<391>,1:18], [@34,108:108='b',<391>,1:108]]}, <[Join Right].[Tuple Feed]>={a=[[@32,105:105='a',<391>,1:105], [@13,29:29='b',<391>,1:29]], b=[[@34,108:108='b',<391>,1:108], [@19,40:40='b',<391>,1:40]]}}, filters=[{name=a, table_ref=a}, {name=a, table_ref=b}, {name=b, table_ref=a}, {name=b, table_ref=b}], interface={da=[{name=a, table_ref=b}], ca=[{name=a, table_ref=a}], db=[{name=b, table_ref=b}], cb=[{name=b, table_ref=a}]}, table_alias={a=third, b=<[Join Right].[Tuple Feed]>}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void joinUsingSingleColumnSubqueryOperandsTupleSubstitutionV8Test() {
		final String query = "SELECT x.a AS xa, x.d, y.a AS ya, y.e FROM (SELECT a, d FROM third) x JOIN <[Join Right].[Tuple Feed]> y USING (a)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a, table_ref=x}, alias=xa}, 2={column={name=d, table_ref=x}}, 3={column={name=a, table_ref=y}, alias=ya}, 4={column={name=e, table_ref=y}}}, from={join={1={table={alias=x, query={select={1={column={name=a, table_ref=null}}, 2={column={name=d, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 3={table={alias=y, substitution={name=<[Join Right].[Tuple Feed]>, parts={1=[Join Right], 2=[Tuple Feed]}, type=tuple}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[d, e, ya, xa]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[Join Right].[Tuple Feed]>=tuple}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@23,51:51='a',<391>,1:51]], d=[[@25,54:54='d',<391>,1:54]]}, <[Join Right].[Tuple Feed]>={a=[[@11,23:23='y',<391>,1:23], [@35,112:112='a',<391>,1:112]], e=[[@17,34:34='y',<391>,1:34]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={d=[[@25,54:54='d',<391>,1:54], [@7,18:18='x',<391>,1:18]], a=[[@23,51:51='a',<391>,1:51], [@1,7:7='x',<391>,1:7], [@35,112:112='a',<391>,1:112]]}, query1={ya=[[@15,30:31='ya',<391>,1:30]], xa=[[@5,14:15='xa',<391>,1:14]], d=[[@9,20:20='d',<391>,1:20]], e=[[@19,36:36='e',<391>,1:36]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={d=[[@9,20:20='d',<391>,1:20]], e=[[@19,36:36='e',<391>,1:36]], ya=[[@15,30:31='ya',<391>,1:30]], xa=[[@5,14:15='xa',<391>,1:14]]}, table_dictionary={<[Join Right].[Tuple Feed]>={a=[[@35,112:112='a',<391>,1:112], [@11,23:23='y',<391>,1:23]], e=[[@17,34:34='y',<391>,1:34]]}}, def_query0={query_dictionary={a=[[@23,51:51='a',<391>,1:51], [@1,7:7='x',<391>,1:7], [@35,112:112='a',<391>,1:112]], d=[[@25,54:54='d',<391>,1:54], [@7,18:18='x',<391>,1:18]]}, table_dictionary={third={a=[[@23,51:51='a',<391>,1:51]], d=[[@25,54:54='d',<391>,1:54]]}}, interface={a=[{name=a, table_ref=third}], d=[{name=d, table_ref=third}]}}, filters=[{name=a, table_ref=x}, {name=a, table_ref=y}], interface={d=[{name=d, table_ref=x}], e=[{name=e, table_ref=y}], ya=[{name=a, table_ref=y}], xa=[{name=a, table_ref=x}]}, table_alias={x=query0, y=<[Join Right].[Tuple Feed]>}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void joinSubquerySourcesWithUsingTupleSubstitutionV8Test() {
		final String query = "SELECT x.a AS xa, x.d, y.a AS ya, y.e FROM <[Join Left].[Tuple Feed]> x JOIN (SELECT a, e FROM fourth) y USING (a)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a, table_ref=x}, alias=xa}, 2={column={name=d, table_ref=x}}, 3={column={name=a, table_ref=y}, alias=ya}, 4={column={name=e, table_ref=y}}}, from={join={1={table={alias=x, substitution={name=<[Join Left].[Tuple Feed]>, parts={1=[Join Left], 2=[Tuple Feed]}, type=tuple}}}, 2={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 3={table={alias=y, query={select={1={column={name=a, table_ref=null}}, 2={column={name=e, table_ref=null}}}, from={table={alias=null, table=fourth}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[d, e, ya, xa]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[Join Left].[Tuple Feed]>=tuple}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Join Left].[Tuple Feed]>={a=[[@1,7:7='x',<391>,1:7], [@35,112:112='a',<391>,1:112]], d=[[@7,18:18='x',<391>,1:18]]}, fourth={a=[[@26,85:85='a',<391>,1:85]], e=[[@28,88:88='e',<391>,1:88]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={a=[[@26,85:85='a',<391>,1:85], [@11,23:23='y',<391>,1:23], [@35,112:112='a',<391>,1:112]], e=[[@28,88:88='e',<391>,1:88], [@17,34:34='y',<391>,1:34]]}, query1={ya=[[@15,30:31='ya',<391>,1:30]], xa=[[@5,14:15='xa',<391>,1:14]], d=[[@9,20:20='d',<391>,1:20]], e=[[@19,36:36='e',<391>,1:36]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={d=[[@9,20:20='d',<391>,1:20]], e=[[@19,36:36='e',<391>,1:36]], ya=[[@15,30:31='ya',<391>,1:30]], xa=[[@5,14:15='xa',<391>,1:14]]}, table_dictionary={<[Join Left].[Tuple Feed]>={a=[[@35,112:112='a',<391>,1:112], [@1,7:7='x',<391>,1:7]], d=[[@7,18:18='x',<391>,1:18]]}}, def_query0={query_dictionary={a=[[@26,85:85='a',<391>,1:85], [@11,23:23='y',<391>,1:23], [@35,112:112='a',<391>,1:112]], e=[[@28,88:88='e',<391>,1:88], [@17,34:34='y',<391>,1:34]]}, table_dictionary={fourth={a=[[@26,85:85='a',<391>,1:85]], e=[[@28,88:88='e',<391>,1:88]]}}, interface={a=[{name=a, table_ref=fourth}], e=[{name=e, table_ref=fourth}]}}, filters=[{name=a, table_ref=x}, {name=a, table_ref=y}], interface={d=[{name=d, table_ref=x}], e=[{name=e, table_ref=y}], ya=[{name=a, table_ref=y}], xa=[{name=a, table_ref=x}]}, table_alias={x=<[Join Left].[Tuple Feed]>, y=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void joinUsingTwoColumnsRightJinjaTableSourceV9Test() {
		final String query = "SELECT a.a AS ca, a.b AS cb, b.a AS da, b.b AS db FROM third a JOIN {{ ref('fourth') }} b USING (a, b)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a, table_ref=a}, alias=ca}, 2={column={name=b, table_ref=a}, alias=cb}, 3={column={name=a, table_ref=b}, alias=da}, 4={column={name=b, table_ref=b}, alias=db}}, from={join={1={table={alias=a, table=third}}, 2={using={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, join=JOIN}, 3={table={alias=b, substitution={name={{ ref('fourth') }}, parts={jinja_table={function_name=ref, parameters={1={literal='fourth'}}}}, type=tuple}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[da, ca, db, cb]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ ref('fourth') }}=tuple}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@1,7:7='a',<391>,1:7], [@37,97:97='a',<391>,1:97]], b=[[@7,18:18='a',<391>,1:18], [@39,100:100='b',<391>,1:100]]}, {{ ref('fourth') }}={a=[[@13,29:29='b',<391>,1:29], [@37,97:97='a',<391>,1:97]], b=[[@19,40:40='b',<391>,1:40], [@39,100:100='b',<391>,1:100]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={da=[[@17,36:37='da',<391>,1:36]], ca=[[@5,14:15='ca',<391>,1:14]], db=[[@23,47:48='db',<391>,1:47]], cb=[[@11,25:26='cb',<391>,1:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={da=[[@17,36:37='da',<391>,1:36]], ca=[[@5,14:15='ca',<391>,1:14]], db=[[@23,47:48='db',<391>,1:47]], cb=[[@11,25:26='cb',<391>,1:25]]}, table_dictionary={third={a=[[@1,7:7='a',<391>,1:7], [@37,97:97='a',<391>,1:97]], b=[[@7,18:18='a',<391>,1:18], [@39,100:100='b',<391>,1:100]]}, {{ ref('fourth') }}={a=[[@37,97:97='a',<391>,1:97], [@13,29:29='b',<391>,1:29]], b=[[@39,100:100='b',<391>,1:100], [@19,40:40='b',<391>,1:40]]}}, filters=[{name=a, table_ref=a}, {name=a, table_ref=b}, {name=b, table_ref=a}, {name=b, table_ref=b}], interface={da=[{name=a, table_ref=b}], ca=[{name=a, table_ref=a}], db=[{name=b, table_ref=b}], cb=[{name=b, table_ref=a}]}, table_alias={a=third, b={{ ref('fourth') }}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void joinUsingSingleColumnSubqueryOperandsJinjaTableV9Test() {
		final String query = "SELECT x.a AS xa, x.d, y.a AS ya, y.e FROM (SELECT a, d FROM third) x JOIN {{ ref('fourth') }} y USING (a)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a, table_ref=x}, alias=xa}, 2={column={name=d, table_ref=x}}, 3={column={name=a, table_ref=y}, alias=ya}, 4={column={name=e, table_ref=y}}}, from={join={1={table={alias=x, query={select={1={column={name=a, table_ref=null}}, 2={column={name=d, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 3={table={alias=y, substitution={name={{ ref('fourth') }}, parts={jinja_table={function_name=ref, parameters={1={literal='fourth'}}}}, type=tuple}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[d, e, ya, xa]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ ref('fourth') }}=tuple}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@23,51:51='a',<391>,1:51]], d=[[@25,54:54='d',<391>,1:54]]}, {{ ref('fourth') }}={a=[[@11,23:23='y',<391>,1:23], [@40,104:104='a',<391>,1:104]], e=[[@17,34:34='y',<391>,1:34]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={d=[[@25,54:54='d',<391>,1:54], [@7,18:18='x',<391>,1:18]], a=[[@23,51:51='a',<391>,1:51], [@1,7:7='x',<391>,1:7], [@40,104:104='a',<391>,1:104]]}, query1={ya=[[@15,30:31='ya',<391>,1:30]], xa=[[@5,14:15='xa',<391>,1:14]], d=[[@9,20:20='d',<391>,1:20]], e=[[@19,36:36='e',<391>,1:36]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={d=[[@9,20:20='d',<391>,1:20]], e=[[@19,36:36='e',<391>,1:36]], ya=[[@15,30:31='ya',<391>,1:30]], xa=[[@5,14:15='xa',<391>,1:14]]}, table_dictionary={{{ ref('fourth') }}={a=[[@40,104:104='a',<391>,1:104], [@11,23:23='y',<391>,1:23]], e=[[@17,34:34='y',<391>,1:34]]}}, def_query0={query_dictionary={a=[[@23,51:51='a',<391>,1:51], [@1,7:7='x',<391>,1:7], [@40,104:104='a',<391>,1:104]], d=[[@25,54:54='d',<391>,1:54], [@7,18:18='x',<391>,1:18]]}, table_dictionary={third={a=[[@23,51:51='a',<391>,1:51]], d=[[@25,54:54='d',<391>,1:54]]}}, interface={a=[{name=a, table_ref=third}], d=[{name=d, table_ref=third}]}}, filters=[{name=a, table_ref=x}, {name=a, table_ref=y}], interface={d=[{name=d, table_ref=x}], e=[{name=e, table_ref=y}], ya=[{name=a, table_ref=y}], xa=[{name=a, table_ref=x}]}, table_alias={x=query0, y={{ ref('fourth') }}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void joinSubquerySourcesWithUsingJinjaTableV9Test() {
		final String query = "SELECT x.a AS xa, x.d, y.a AS ya, y.e FROM {{ ref('third') }} x JOIN (SELECT a, e FROM fourth) y USING (a)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a, table_ref=x}, alias=xa}, 2={column={name=d, table_ref=x}}, 3={column={name=a, table_ref=y}, alias=ya}, 4={column={name=e, table_ref=y}}}, from={join={1={table={alias=x, substitution={name={{ ref('third') }}, parts={jinja_table={function_name=ref, parameters={1={literal='third'}}}}, type=tuple}}}, 2={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 3={table={alias=y, query={select={1={column={name=a, table_ref=null}}, 2={column={name=e, table_ref=null}}}, from={table={alias=null, table=fourth}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[d, e, ya, xa]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ ref('third') }}=tuple}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{{{ ref('third') }}={a=[[@1,7:7='x',<391>,1:7], [@40,104:104='a',<391>,1:104]], d=[[@7,18:18='x',<391>,1:18]]}, fourth={a=[[@31,77:77='a',<391>,1:77]], e=[[@33,80:80='e',<391>,1:80]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={a=[[@31,77:77='a',<391>,1:77], [@11,23:23='y',<391>,1:23], [@40,104:104='a',<391>,1:104]], e=[[@33,80:80='e',<391>,1:80], [@17,34:34='y',<391>,1:34]]}, query1={ya=[[@15,30:31='ya',<391>,1:30]], xa=[[@5,14:15='xa',<391>,1:14]], d=[[@9,20:20='d',<391>,1:20]], e=[[@19,36:36='e',<391>,1:36]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={d=[[@9,20:20='d',<391>,1:20]], e=[[@19,36:36='e',<391>,1:36]], ya=[[@15,30:31='ya',<391>,1:30]], xa=[[@5,14:15='xa',<391>,1:14]]}, table_dictionary={{{ ref('third') }}={a=[[@40,104:104='a',<391>,1:104], [@1,7:7='x',<391>,1:7]], d=[[@7,18:18='x',<391>,1:18]]}}, def_query0={query_dictionary={a=[[@31,77:77='a',<391>,1:77], [@11,23:23='y',<391>,1:23], [@40,104:104='a',<391>,1:104]], e=[[@33,80:80='e',<391>,1:80], [@17,34:34='y',<391>,1:34]]}, table_dictionary={fourth={a=[[@31,77:77='a',<391>,1:77]], e=[[@33,80:80='e',<391>,1:80]]}}, interface={a=[{name=a, table_ref=fourth}], e=[{name=e, table_ref=fourth}]}}, filters=[{name=a, table_ref=x}, {name=a, table_ref=y}], interface={d=[{name=d, table_ref=x}], e=[{name=e, table_ref=y}], ya=[{name=a, table_ref=y}], xa=[{name=a, table_ref=x}]}, table_alias={x={{ ref('third') }}, y=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void joinUsingTwoColumnsNestedCompositeJoinSourceV10Test() {
		final String query = "SELECT t.a AS ca, t.b AS cb, x.a AS da, x.b AS db FROM third t JOIN fourth f USING (a, b) JOIN (SELECT a, b FROM fifth) x USING (a)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a, table_ref=t}, alias=ca}, 2={column={name=b, table_ref=t}, alias=cb}, 3={column={name=a, table_ref=x}, alias=da}, 4={column={name=b, table_ref=x}, alias=db}}, from={join={1={table={alias=t, table=third}}, 2={using={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, join=JOIN}, 3={table={alias=f, table=fourth}}, 4={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 5={table={alias=x, query={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, from={table={alias=null, table=fifth}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[da, ca, db, cb]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@1,7:7='t',<391>,1:7], [@48,129:129='a',<391>,1:129]], b=[[@7,18:18='t',<391>,1:18], [@34,87:87='b',<391>,1:87]]}, fifth={a=[[@39,103:103='a',<391>,1:103]], b=[[@41,106:106='b',<391>,1:106]]}, fourth={a=[[@48,129:129='a',<391>,1:129]], b=[[@34,87:87='b',<391>,1:87]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={a=[[@39,103:103='a',<391>,1:103], [@13,29:29='x',<391>,1:29]], b=[[@41,106:106='b',<391>,1:106], [@19,40:40='x',<391>,1:40]]}, query1={da=[[@17,36:37='da',<391>,1:36]], ca=[[@5,14:15='ca',<391>,1:14]], db=[[@23,47:48='db',<391>,1:47]], cb=[[@11,25:26='cb',<391>,1:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={da=[[@17,36:37='da',<391>,1:36]], ca=[[@5,14:15='ca',<391>,1:14]], db=[[@23,47:48='db',<391>,1:47]], cb=[[@11,25:26='cb',<391>,1:25]]}, table_dictionary={third={a=[[@1,7:7='t',<391>,1:7], [@48,129:129='a',<391>,1:129]], b=[[@7,18:18='t',<391>,1:18], [@34,87:87='b',<391>,1:87]]}, fourth={a=[[@48,129:129='a',<391>,1:129]], b=[[@34,87:87='b',<391>,1:87]]}}, def_query0={query_dictionary={a=[[@39,103:103='a',<391>,1:103], [@13,29:29='x',<391>,1:29]], b=[[@41,106:106='b',<391>,1:106], [@19,40:40='x',<391>,1:40]]}, table_dictionary={fifth={a=[[@39,103:103='a',<391>,1:103]], b=[[@41,106:106='b',<391>,1:106]]}}, interface={a=[{name=a, table_ref=fifth}], b=[{name=b, table_ref=fifth}]}}, filters=[{name=a, table_ref=t}, {name=a, table_ref=f}, {name=b, table_ref=t}, {name=b, table_ref=f}], interface={da=[{name=a, table_ref=x}], ca=[{name=a, table_ref=t}], db=[{name=b, table_ref=x}], cb=[{name=b, table_ref=t}]}, table_alias={t=third, f=fourth, x=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void joinUsingSingleColumnNestedCompositeJoinOperandsV10Test() {
		final String query = "SELECT t.a AS xa, t.d, x.a AS ya, x.e FROM third t JOIN fourth f USING (a) JOIN (SELECT a, e FROM fifth) x USING (a)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a, table_ref=t}, alias=xa}, 2={column={name=d, table_ref=t}}, 3={column={name=a, table_ref=x}, alias=ya}, 4={column={name=e, table_ref=x}}}, from={join={1={table={alias=t, table=third}}, 2={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 3={table={alias=f, table=fourth}}, 4={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 5={table={alias=x, query={select={1={column={name=a, table_ref=null}}, 2={column={name=e, table_ref=null}}}, from={table={alias=null, table=fifth}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[d, e, ya, xa]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@1,7:7='t',<391>,1:7], [@42,114:114='a',<391>,1:114]], d=[[@7,18:18='t',<391>,1:18]]}, fifth={a=[[@33,88:88='a',<391>,1:88]], e=[[@35,91:91='e',<391>,1:91]]}, fourth={a=[[@42,114:114='a',<391>,1:114]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={a=[[@33,88:88='a',<391>,1:88], [@11,23:23='x',<391>,1:23]], e=[[@35,91:91='e',<391>,1:91], [@17,34:34='x',<391>,1:34]]}, query1={ya=[[@15,30:31='ya',<391>,1:30]], xa=[[@5,14:15='xa',<391>,1:14]], d=[[@9,20:20='d',<391>,1:20]], e=[[@19,36:36='e',<391>,1:36]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={d=[[@9,20:20='d',<391>,1:20]], e=[[@19,36:36='e',<391>,1:36]], ya=[[@15,30:31='ya',<391>,1:30]], xa=[[@5,14:15='xa',<391>,1:14]]}, table_dictionary={third={a=[[@1,7:7='t',<391>,1:7], [@42,114:114='a',<391>,1:114]], d=[[@7,18:18='t',<391>,1:18]]}, fourth={a=[[@42,114:114='a',<391>,1:114]]}}, def_query0={query_dictionary={a=[[@33,88:88='a',<391>,1:88], [@11,23:23='x',<391>,1:23]], e=[[@35,91:91='e',<391>,1:91], [@17,34:34='x',<391>,1:34]]}, table_dictionary={fifth={a=[[@33,88:88='a',<391>,1:88]], e=[[@35,91:91='e',<391>,1:91]]}}, interface={a=[{name=a, table_ref=fifth}], e=[{name=e, table_ref=fifth}]}}, filters=[{name=a, table_ref=t}, {name=a, table_ref=f}], interface={d=[{name=d, table_ref=t}], e=[{name=e, table_ref=x}], ya=[{name=a, table_ref=x}], xa=[{name=a, table_ref=t}]}, table_alias={t=third, f=fourth, x=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void joinSubquerySourcesWithUsingNestedCompositeJoinV10Test() {
		final String query = "SELECT t.a AS xa, t.d, x.a AS ya, x.e FROM third t JOIN (SELECT a, d FROM fourth) f USING (a) JOIN (SELECT a, e FROM fifth) x USING (a)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a, table_ref=t}, alias=xa}, 2={column={name=d, table_ref=t}}, 3={column={name=a, table_ref=x}, alias=ya}, 4={column={name=e, table_ref=x}}}, from={join={1={table={alias=t, table=third}}, 2={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 3={table={alias=f, query={select={1={column={name=a, table_ref=null}}, 2={column={name=d, table_ref=null}}}, from={table={alias=null, table=fourth}}}}}, 4={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 5={table={alias=x, query={select={1={column={name=a, table_ref=null}}, 2={column={name=e, table_ref=null}}}, from={table={alias=null, table=fifth}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[d, e, ya, xa]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@1,7:7='t',<391>,1:7], [@49,133:133='a',<391>,1:133]], d=[[@7,18:18='t',<391>,1:18]]}, fifth={a=[[@40,107:107='a',<391>,1:107]], e=[[@42,110:110='e',<391>,1:110]]}, fourth={a=[[@26,64:64='a',<391>,1:64]], d=[[@28,67:67='d',<391>,1:67]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={d=[[@28,67:67='d',<391>,1:67]], a=[[@26,64:64='a',<391>,1:64], [@49,133:133='a',<391>,1:133]]}, query1={a=[[@40,107:107='a',<391>,1:107], [@11,23:23='x',<391>,1:23]], e=[[@42,110:110='e',<391>,1:110], [@17,34:34='x',<391>,1:34]]}, query2={ya=[[@15,30:31='ya',<391>,1:30]], xa=[[@5,14:15='xa',<391>,1:14]], d=[[@9,20:20='d',<391>,1:20]], e=[[@19,36:36='e',<391>,1:36]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={d=[[@9,20:20='d',<391>,1:20]], e=[[@19,36:36='e',<391>,1:36]], ya=[[@15,30:31='ya',<391>,1:30]], xa=[[@5,14:15='xa',<391>,1:14]]}, table_dictionary={third={a=[[@1,7:7='t',<391>,1:7], [@49,133:133='a',<391>,1:133]], d=[[@7,18:18='t',<391>,1:18]]}}, def_query1={query_dictionary={a=[[@40,107:107='a',<391>,1:107], [@11,23:23='x',<391>,1:23]], e=[[@42,110:110='e',<391>,1:110], [@17,34:34='x',<391>,1:34]]}, table_dictionary={fifth={a=[[@40,107:107='a',<391>,1:107]], e=[[@42,110:110='e',<391>,1:110]]}}, interface={a=[{name=a, table_ref=fifth}], e=[{name=e, table_ref=fifth}]}}, def_query0={query_dictionary={a=[[@26,64:64='a',<391>,1:64], [@49,133:133='a',<391>,1:133]], d=[[@28,67:67='d',<391>,1:67]]}, table_dictionary={fourth={a=[[@26,64:64='a',<391>,1:64]], d=[[@28,67:67='d',<391>,1:67]]}}, interface={a=[{name=a, table_ref=fourth}], d=[{name=d, table_ref=fourth}]}}, filters=[{name=a, table_ref=t}, {name=a, table_ref=f}], interface={d=[{name=d, table_ref=t}], e=[{name=e, table_ref=x}], ya=[{name=a, table_ref=x}], xa=[{name=a, table_ref=t}]}, table_alias={t=third, f=query0, x=query1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void joinUsingTwoColumnsWithCteNestedJoinInSecondCteV11Test() {
		final String query = "WITH base AS (SELECT a, b FROM third), pair AS (SELECT a.a AS ca, a.b AS cb, b.a AS da, b.b AS db FROM base a JOIN fourth b USING (a, b)) SELECT ca, cb, da, db FROM pair";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, from={table={alias=null, table=third}}}, alias=base}, 2={cte={select={1={column={name=a, table_ref=a}, alias=ca}, 2={column={name=b, table_ref=a}, alias=cb}, 3={column={name=a, table_ref=b}, alias=da}, 4={column={name=b, table_ref=b}, alias=db}}, from={join={1={table={alias=a, table=base}}, 2={using={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, join=JOIN}, 3={table={alias=b, table=fourth}}}}}, alias=pair}}, query={select={1={column={name=ca, table_ref=null}}, 2={column={name=cb, table_ref=null}}, 3={column={name=da, table_ref=null}}, 4={column={name=db, table_ref=null}}}, from={table={alias=null, table=pair}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[da, ca, db, cb]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@5,21:21='a',<391>,1:21]], b=[[@7,24:24='b',<391>,1:24]]}, fourth={a=[[@28,77:77='b',<391>,1:77], [@47,131:131='a',<391>,1:131]], b=[[@34,88:88='b',<391>,1:88], [@49,134:134='b',<391>,1:134]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={a=[[@5,21:21='a',<391>,1:21], [@16,55:55='a',<391>,1:55], [@47,131:131='a',<391>,1:131]], b=[[@7,24:24='b',<391>,1:24], [@22,66:66='a',<391>,1:66], [@49,134:134='b',<391>,1:134]]}, query1={da=[[@32,84:85='da',<391>,1:84], [@57,153:154='da',<391>,1:153]], ca=[[@20,62:63='ca',<391>,1:62], [@53,145:146='ca',<391>,1:145]], db=[[@38,95:96='db',<391>,1:95], [@59,157:158='db',<391>,1:157]], cb=[[@26,73:74='cb',<391>,1:73], [@55,149:150='cb',<391>,1:149]]}, query2={da=[[@57,153:154='da',<391>,1:153]], ca=[[@53,145:146='ca',<391>,1:145]], db=[[@59,157:158='db',<391>,1:157]], cb=[[@55,149:150='cb',<391>,1:149]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={context_list={base=query0, pair=query1}, query_dictionary={da=[[@57,153:154='da',<391>,1:153]], ca=[[@53,145:146='ca',<391>,1:145]], db=[[@59,157:158='db',<391>,1:157]], cb=[[@55,149:150='cb',<391>,1:149]]}, def_query1={context_list={base=query0, a=query0}, query_dictionary={da=[[@32,84:85='da',<391>,1:84], [@57,153:154='da',<391>,1:153]], ca=[[@20,62:63='ca',<391>,1:62], [@53,145:146='ca',<391>,1:145]], db=[[@38,95:96='db',<391>,1:95], [@59,157:158='db',<391>,1:157]], cb=[[@26,73:74='cb',<391>,1:73], [@55,149:150='cb',<391>,1:149]]}, table_dictionary={fourth={a=[[@28,77:77='b',<391>,1:77], [@47,131:131='a',<391>,1:131]], b=[[@34,88:88='b',<391>,1:88], [@49,134:134='b',<391>,1:134]]}}, filters=[{name=a, table_ref=a}, {name=a, table_ref=b}, {name=b, table_ref=a}, {name=b, table_ref=b}], interface={da=[{name=a, table_ref=b}], ca=[{name=a, table_ref=a}], db=[{name=b, table_ref=b}], cb=[{name=b, table_ref=a}]}, table_alias={a=query0, b=fourth, base=query0}}, def_query0={query_dictionary={a=[[@5,21:21='a',<391>,1:21], [@16,55:55='a',<391>,1:55], [@47,131:131='a',<391>,1:131]], b=[[@7,24:24='b',<391>,1:24], [@22,66:66='a',<391>,1:66], [@49,134:134='b',<391>,1:134]]}, table_dictionary={third={a=[[@5,21:21='a',<391>,1:21]], b=[[@7,24:24='b',<391>,1:24]]}}, interface={a=[{name=a, table_ref=third}], b=[{name=b, table_ref=third}]}}, interface={da=[{name=da, table_ref=query1}], ca=[{name=ca, table_ref=query1}], db=[{name=db, table_ref=query1}], cb=[{name=cb, table_ref=query1}]}, table_alias={pair=query1, base=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void joinUsingSingleColumnWithCteNestedJoinInSecondCteV11Test() {
		final String query = "WITH left_rows AS (SELECT a, d FROM third), joined AS (SELECT x.a AS xa, x.d, y.a AS ya, y.e FROM left_rows x JOIN (SELECT a, e FROM fourth) y USING (a)) SELECT xa, d, ya, e FROM joined";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={select={1={column={name=a, table_ref=null}}, 2={column={name=d, table_ref=null}}}, from={table={alias=null, table=third}}}, alias=left_rows}, 2={cte={select={1={column={name=a, table_ref=x}, alias=xa}, 2={column={name=d, table_ref=x}}, 3={column={name=a, table_ref=y}, alias=ya}, 4={column={name=e, table_ref=y}}}, from={join={1={table={alias=x, table=left_rows}}, 2={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 3={table={alias=y, query={select={1={column={name=a, table_ref=null}}, 2={column={name=e, table_ref=null}}}, from={table={alias=null, table=fourth}}}}}}}}, alias=joined}}, query={select={1={column={name=xa, table_ref=null}}, 2={column={name=d, table_ref=null}}, 3={column={name=ya, table_ref=null}}, 4={column={name=e, table_ref=null}}}, from={table={alias=null, table=joined}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[d, e, ya, xa]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@5,26:26='a',<391>,1:26]], d=[[@7,29:29='d',<391>,1:29]]}, fourth={a=[[@41,123:123='a',<391>,1:123]], e=[[@43,126:126='e',<391>,1:126]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={d=[[@7,29:29='d',<391>,1:29], [@22,73:73='x',<391>,1:73]], a=[[@5,26:26='a',<391>,1:26], [@16,62:62='x',<391>,1:62], [@50,150:150='a',<391>,1:150]]}, query1={a=[[@41,123:123='a',<391>,1:123], [@26,78:78='y',<391>,1:78], [@50,150:150='a',<391>,1:150]], e=[[@43,126:126='e',<391>,1:126], [@32,89:89='y',<391>,1:89]]}, query2={ya=[[@30,85:86='ya',<391>,1:85], [@58,168:169='ya',<391>,1:168]], xa=[[@20,69:70='xa',<391>,1:69], [@54,161:162='xa',<391>,1:161]], d=[[@24,75:75='d',<391>,1:75], [@56,165:165='d',<391>,1:165]], e=[[@34,91:91='e',<391>,1:91], [@60,172:172='e',<391>,1:172]]}, query3={ya=[[@58,168:169='ya',<391>,1:168]], xa=[[@54,161:162='xa',<391>,1:161]], d=[[@56,165:165='d',<391>,1:165]], e=[[@60,172:172='e',<391>,1:172]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query3={context_list={left_rows=query0, joined=query2}, query_dictionary={d=[[@56,165:165='d',<391>,1:165]], e=[[@60,172:172='e',<391>,1:172]], ya=[[@58,168:169='ya',<391>,1:168]], xa=[[@54,161:162='xa',<391>,1:161]]}, def_query0={query_dictionary={a=[[@5,26:26='a',<391>,1:26], [@16,62:62='x',<391>,1:62], [@50,150:150='a',<391>,1:150]], d=[[@7,29:29='d',<391>,1:29], [@22,73:73='x',<391>,1:73]]}, table_dictionary={third={a=[[@5,26:26='a',<391>,1:26]], d=[[@7,29:29='d',<391>,1:29]]}}, interface={a=[{name=a, table_ref=third}], d=[{name=d, table_ref=third}]}}, interface={d=[{name=d, table_ref=query2}], e=[{name=e, table_ref=query2}], ya=[{name=ya, table_ref=query2}], xa=[{name=xa, table_ref=query2}]}, table_alias={left_rows=query0, joined=query2}, def_query2={context_list={left_rows=query0, x=query0}, query_dictionary={d=[[@24,75:75='d',<391>,1:75], [@56,165:165='d',<391>,1:165]], e=[[@34,91:91='e',<391>,1:91], [@60,172:172='e',<391>,1:172]], ya=[[@30,85:86='ya',<391>,1:85], [@58,168:169='ya',<391>,1:168]], xa=[[@20,69:70='xa',<391>,1:69], [@54,161:162='xa',<391>,1:161]]}, def_query1={context_list={left_rows=query0}, query_dictionary={a=[[@41,123:123='a',<391>,1:123], [@26,78:78='y',<391>,1:78], [@50,150:150='a',<391>,1:150]], e=[[@43,126:126='e',<391>,1:126], [@32,89:89='y',<391>,1:89]]}, table_dictionary={fourth={a=[[@41,123:123='a',<391>,1:123]], e=[[@43,126:126='e',<391>,1:126]]}}, interface={a=[{name=a, table_ref=fourth}], e=[{name=e, table_ref=fourth}]}, table_alias={left_rows=query0}}, filters=[{name=a, table_ref=x}, {name=a, table_ref=y}], interface={d=[{name=d, table_ref=x}], e=[{name=e, table_ref=y}], ya=[{name=a, table_ref=y}], xa=[{name=a, table_ref=x}]}, table_alias={left_rows=query0, x=query0, y=query1}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void joinSubquerySourcesWithUsingCteNestedJoinInSecondCteV11Test() {
		final String query = "WITH left_rows AS (SELECT a, d FROM third), right_rows AS (SELECT a, e FROM fourth), joined AS (SELECT x.a AS xa, x.d, y.a AS ya, y.e FROM left_rows x JOIN right_rows y USING (a)) SELECT xa, d, ya, e FROM joined";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={select={1={column={name=a, table_ref=null}}, 2={column={name=d, table_ref=null}}}, from={table={alias=null, table=third}}}, alias=left_rows}, 2={cte={select={1={column={name=a, table_ref=null}}, 2={column={name=e, table_ref=null}}}, from={table={alias=null, table=fourth}}}, alias=right_rows}, 3={cte={select={1={column={name=a, table_ref=x}, alias=xa}, 2={column={name=d, table_ref=x}}, 3={column={name=a, table_ref=y}, alias=ya}, 4={column={name=e, table_ref=y}}}, from={join={1={table={alias=x, table=left_rows}}, 2={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 3={table={alias=y, table=right_rows}}}}}, alias=joined}}, query={select={1={column={name=xa, table_ref=null}}, 2={column={name=d, table_ref=null}}, 3={column={name=ya, table_ref=null}}, 4={column={name=e, table_ref=null}}}, from={table={alias=null, table=joined}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[d, e, ya, xa]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@5,26:26='a',<391>,1:26]], d=[[@7,29:29='d',<391>,1:29]]}, fourth={a=[[@16,66:66='a',<391>,1:66]], e=[[@18,69:69='e',<391>,1:69]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={d=[[@7,29:29='d',<391>,1:29], [@33,114:114='x',<391>,1:114]], a=[[@5,26:26='a',<391>,1:26], [@27,103:103='x',<391>,1:103], [@54,176:176='a',<391>,1:176]]}, query1={a=[[@16,66:66='a',<391>,1:66], [@37,119:119='y',<391>,1:119], [@54,176:176='a',<391>,1:176]], e=[[@18,69:69='e',<391>,1:69], [@43,130:130='y',<391>,1:130]]}, query2={ya=[[@41,126:127='ya',<391>,1:126], [@62,194:195='ya',<391>,1:194]], xa=[[@31,110:111='xa',<391>,1:110], [@58,187:188='xa',<391>,1:187]], d=[[@35,116:116='d',<391>,1:116], [@60,191:191='d',<391>,1:191]], e=[[@45,132:132='e',<391>,1:132], [@64,198:198='e',<391>,1:198]]}, query3={ya=[[@62,194:195='ya',<391>,1:194]], xa=[[@58,187:188='xa',<391>,1:187]], d=[[@60,191:191='d',<391>,1:191]], e=[[@64,198:198='e',<391>,1:198]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query3={context_list={left_rows=query0, right_rows=query1, joined=query2}, query_dictionary={d=[[@60,191:191='d',<391>,1:191]], e=[[@64,198:198='e',<391>,1:198]], ya=[[@62,194:195='ya',<391>,1:194]], xa=[[@58,187:188='xa',<391>,1:187]]}, def_query1={context_list={left_rows=query0}, query_dictionary={a=[[@16,66:66='a',<391>,1:66], [@37,119:119='y',<391>,1:119], [@54,176:176='a',<391>,1:176]], e=[[@18,69:69='e',<391>,1:69], [@43,130:130='y',<391>,1:130]]}, table_dictionary={fourth={a=[[@16,66:66='a',<391>,1:66]], e=[[@18,69:69='e',<391>,1:69]]}}, interface={a=[{name=a, table_ref=fourth}], e=[{name=e, table_ref=fourth}]}, table_alias={left_rows=query0}}, def_query0={query_dictionary={a=[[@5,26:26='a',<391>,1:26], [@27,103:103='x',<391>,1:103], [@54,176:176='a',<391>,1:176]], d=[[@7,29:29='d',<391>,1:29], [@33,114:114='x',<391>,1:114]]}, table_dictionary={third={a=[[@5,26:26='a',<391>,1:26]], d=[[@7,29:29='d',<391>,1:29]]}}, interface={a=[{name=a, table_ref=third}], d=[{name=d, table_ref=third}]}}, interface={d=[{name=d, table_ref=query2}], e=[{name=e, table_ref=query2}], ya=[{name=ya, table_ref=query2}], xa=[{name=xa, table_ref=query2}]}, table_alias={left_rows=query0, right_rows=query1, joined=query2}, def_query2={context_list={left_rows=query0, right_rows=query1, x=query0, y=query1}, query_dictionary={d=[[@35,116:116='d',<391>,1:116], [@60,191:191='d',<391>,1:191]], e=[[@45,132:132='e',<391>,1:132], [@64,198:198='e',<391>,1:198]], ya=[[@41,126:127='ya',<391>,1:126], [@62,194:195='ya',<391>,1:194]], xa=[[@31,110:111='xa',<391>,1:110], [@58,187:188='xa',<391>,1:187]]}, filters=[{name=a, table_ref=x}, {name=a, table_ref=y}], interface={d=[{name=d, table_ref=x}], e=[{name=e, table_ref=y}], ya=[{name=a, table_ref=y}], xa=[{name=a, table_ref=x}]}, table_alias={left_rows=query0, right_rows=query1, x=query0, y=query1}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void joinUsingTwoColumnsWithCtePairInFinalQueryV12Test() {
		final String query = "WITH left_cte AS (SELECT a, b FROM third), right_cte AS (SELECT a, b FROM fourth) SELECT l.a AS ca, l.b AS cb, r.a AS da, r.b AS db FROM left_cte l JOIN right_cte r USING (a, b)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, from={table={alias=null, table=third}}}, alias=left_cte}, 2={cte={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, from={table={alias=null, table=fourth}}}, alias=right_cte}}, query={select={1={column={name=a, table_ref=l}, alias=ca}, 2={column={name=b, table_ref=l}, alias=cb}, 3={column={name=a, table_ref=r}, alias=da}, 4={column={name=b, table_ref=r}, alias=db}}, from={join={1={table={alias=l, table=left_cte}}, 2={using={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, join=JOIN}, 3={table={alias=r, table=right_cte}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[da, ca, db, cb]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@5,25:25='a',<391>,1:25]], b=[[@7,28:28='b',<391>,1:28]]}, fourth={a=[[@16,64:64='a',<391>,1:64]], b=[[@18,67:67='b',<391>,1:67]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={a=[[@5,25:25='a',<391>,1:25], [@23,89:89='l',<391>,1:89], [@54,172:172='a',<391>,1:172]], b=[[@7,28:28='b',<391>,1:28], [@29,100:100='l',<391>,1:100], [@56,175:175='b',<391>,1:175]]}, query1={a=[[@16,64:64='a',<391>,1:64], [@35,111:111='r',<391>,1:111], [@54,172:172='a',<391>,1:172]], b=[[@18,67:67='b',<391>,1:67], [@41,122:122='r',<391>,1:122], [@56,175:175='b',<391>,1:175]]}, query2={da=[[@39,118:119='da',<391>,1:118]], ca=[[@27,96:97='ca',<391>,1:96]], db=[[@45,129:130='db',<391>,1:129]], cb=[[@33,107:108='cb',<391>,1:107]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={context_list={left_cte=query0, right_cte=query1, l=query0, r=query1}, query_dictionary={da=[[@39,118:119='da',<391>,1:118]], ca=[[@27,96:97='ca',<391>,1:96]], db=[[@45,129:130='db',<391>,1:129]], cb=[[@33,107:108='cb',<391>,1:107]]}, def_query1={context_list={left_cte=query0}, query_dictionary={a=[[@16,64:64='a',<391>,1:64], [@35,111:111='r',<391>,1:111], [@54,172:172='a',<391>,1:172]], b=[[@18,67:67='b',<391>,1:67], [@41,122:122='r',<391>,1:122], [@56,175:175='b',<391>,1:175]]}, table_dictionary={fourth={a=[[@16,64:64='a',<391>,1:64]], b=[[@18,67:67='b',<391>,1:67]]}}, interface={a=[{name=a, table_ref=fourth}], b=[{name=b, table_ref=fourth}]}, table_alias={left_cte=query0}}, def_query0={query_dictionary={a=[[@5,25:25='a',<391>,1:25], [@23,89:89='l',<391>,1:89], [@54,172:172='a',<391>,1:172]], b=[[@7,28:28='b',<391>,1:28], [@29,100:100='l',<391>,1:100], [@56,175:175='b',<391>,1:175]]}, table_dictionary={third={a=[[@5,25:25='a',<391>,1:25]], b=[[@7,28:28='b',<391>,1:28]]}}, interface={a=[{name=a, table_ref=third}], b=[{name=b, table_ref=third}]}}, filters=[{name=a, table_ref=l}, {name=a, table_ref=r}, {name=b, table_ref=l}, {name=b, table_ref=r}], interface={da=[{name=a, table_ref=r}], ca=[{name=a, table_ref=l}], db=[{name=b, table_ref=r}], cb=[{name=b, table_ref=l}]}, table_alias={r=query1, l=query0, left_cte=query0, right_cte=query1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void joinUsingSingleColumnWithCtePairInFinalQueryV12Test() {
		final String query = "WITH left_cte AS (SELECT a, d FROM third), right_cte AS (SELECT a, e FROM fourth) SELECT l.a AS xa, l.d, r.a AS ya, r.e FROM left_cte l JOIN right_cte r USING (a)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={select={1={column={name=a, table_ref=null}}, 2={column={name=d, table_ref=null}}}, from={table={alias=null, table=third}}}, alias=left_cte}, 2={cte={select={1={column={name=a, table_ref=null}}, 2={column={name=e, table_ref=null}}}, from={table={alias=null, table=fourth}}}, alias=right_cte}}, query={select={1={column={name=a, table_ref=l}, alias=xa}, 2={column={name=d, table_ref=l}}, 3={column={name=a, table_ref=r}, alias=ya}, 4={column={name=e, table_ref=r}}}, from={join={1={table={alias=l, table=left_cte}}, 2={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 3={table={alias=r, table=right_cte}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[d, e, ya, xa]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@5,25:25='a',<391>,1:25]], d=[[@7,28:28='d',<391>,1:28]]}, fourth={a=[[@16,64:64='a',<391>,1:64]], e=[[@18,67:67='e',<391>,1:67]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={d=[[@7,28:28='d',<391>,1:28], [@29,100:100='l',<391>,1:100]], a=[[@5,25:25='a',<391>,1:25], [@23,89:89='l',<391>,1:89], [@50,160:160='a',<391>,1:160]]}, query1={a=[[@16,64:64='a',<391>,1:64], [@33,105:105='r',<391>,1:105], [@50,160:160='a',<391>,1:160]], e=[[@18,67:67='e',<391>,1:67], [@39,116:116='r',<391>,1:116]]}, query2={ya=[[@37,112:113='ya',<391>,1:112]], xa=[[@27,96:97='xa',<391>,1:96]], d=[[@31,102:102='d',<391>,1:102]], e=[[@41,118:118='e',<391>,1:118]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={context_list={left_cte=query0, right_cte=query1, l=query0, r=query1}, query_dictionary={d=[[@31,102:102='d',<391>,1:102]], e=[[@41,118:118='e',<391>,1:118]], ya=[[@37,112:113='ya',<391>,1:112]], xa=[[@27,96:97='xa',<391>,1:96]]}, def_query1={context_list={left_cte=query0}, query_dictionary={a=[[@16,64:64='a',<391>,1:64], [@33,105:105='r',<391>,1:105], [@50,160:160='a',<391>,1:160]], e=[[@18,67:67='e',<391>,1:67], [@39,116:116='r',<391>,1:116]]}, table_dictionary={fourth={a=[[@16,64:64='a',<391>,1:64]], e=[[@18,67:67='e',<391>,1:67]]}}, interface={a=[{name=a, table_ref=fourth}], e=[{name=e, table_ref=fourth}]}, table_alias={left_cte=query0}}, def_query0={query_dictionary={a=[[@5,25:25='a',<391>,1:25], [@23,89:89='l',<391>,1:89], [@50,160:160='a',<391>,1:160]], d=[[@7,28:28='d',<391>,1:28], [@29,100:100='l',<391>,1:100]]}, table_dictionary={third={a=[[@5,25:25='a',<391>,1:25]], d=[[@7,28:28='d',<391>,1:28]]}}, interface={a=[{name=a, table_ref=third}], d=[{name=d, table_ref=third}]}}, filters=[{name=a, table_ref=l}, {name=a, table_ref=r}], interface={d=[{name=d, table_ref=l}], e=[{name=e, table_ref=r}], ya=[{name=a, table_ref=r}], xa=[{name=a, table_ref=l}]}, table_alias={r=query1, l=query0, left_cte=query0, right_cte=query1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void joinSubquerySourcesWithUsingCtePairInFinalQueryV12Test() {
		final String query = "WITH left_cte AS (SELECT a, d FROM third), right_cte AS (SELECT a, e FROM fourth) SELECT x.a AS xa, x.d, y.a AS ya, y.e FROM left_cte x JOIN right_cte y USING (a)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={select={1={column={name=a, table_ref=null}}, 2={column={name=d, table_ref=null}}}, from={table={alias=null, table=third}}}, alias=left_cte}, 2={cte={select={1={column={name=a, table_ref=null}}, 2={column={name=e, table_ref=null}}}, from={table={alias=null, table=fourth}}}, alias=right_cte}}, query={select={1={column={name=a, table_ref=x}, alias=xa}, 2={column={name=d, table_ref=x}}, 3={column={name=a, table_ref=y}, alias=ya}, 4={column={name=e, table_ref=y}}}, from={join={1={table={alias=x, table=left_cte}}, 2={using={1={column={name=a, table_ref=null}}}, join=JOIN}, 3={table={alias=y, table=right_cte}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[d, e, ya, xa]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{third={a=[[@5,25:25='a',<391>,1:25]], d=[[@7,28:28='d',<391>,1:28]]}, fourth={a=[[@16,64:64='a',<391>,1:64]], e=[[@18,67:67='e',<391>,1:67]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={d=[[@7,28:28='d',<391>,1:28], [@29,100:100='x',<391>,1:100]], a=[[@5,25:25='a',<391>,1:25], [@23,89:89='x',<391>,1:89], [@50,160:160='a',<391>,1:160]]}, query1={a=[[@16,64:64='a',<391>,1:64], [@33,105:105='y',<391>,1:105], [@50,160:160='a',<391>,1:160]], e=[[@18,67:67='e',<391>,1:67], [@39,116:116='y',<391>,1:116]]}, query2={ya=[[@37,112:113='ya',<391>,1:112]], xa=[[@27,96:97='xa',<391>,1:96]], d=[[@31,102:102='d',<391>,1:102]], e=[[@41,118:118='e',<391>,1:118]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={context_list={left_cte=query0, right_cte=query1, x=query0, y=query1}, query_dictionary={d=[[@31,102:102='d',<391>,1:102]], e=[[@41,118:118='e',<391>,1:118]], ya=[[@37,112:113='ya',<391>,1:112]], xa=[[@27,96:97='xa',<391>,1:96]]}, def_query1={context_list={left_cte=query0}, query_dictionary={a=[[@16,64:64='a',<391>,1:64], [@33,105:105='y',<391>,1:105], [@50,160:160='a',<391>,1:160]], e=[[@18,67:67='e',<391>,1:67], [@39,116:116='y',<391>,1:116]]}, table_dictionary={fourth={a=[[@16,64:64='a',<391>,1:64]], e=[[@18,67:67='e',<391>,1:67]]}}, interface={a=[{name=a, table_ref=fourth}], e=[{name=e, table_ref=fourth}]}, table_alias={left_cte=query0}}, def_query0={query_dictionary={a=[[@5,25:25='a',<391>,1:25], [@23,89:89='x',<391>,1:89], [@50,160:160='a',<391>,1:160]], d=[[@7,28:28='d',<391>,1:28], [@29,100:100='x',<391>,1:100]]}, table_dictionary={third={a=[[@5,25:25='a',<391>,1:25]], d=[[@7,28:28='d',<391>,1:28]]}}, interface={a=[{name=a, table_ref=third}], d=[{name=d, table_ref=third}]}}, filters=[{name=a, table_ref=x}, {name=a, table_ref=y}], interface={d=[{name=d, table_ref=x}], e=[{name=e, table_ref=y}], ya=[{name=a, table_ref=y}], xa=[{name=a, table_ref=x}]}, table_alias={x=query0, y=query1, left_cte=query0, right_cte=query1}}}",
				extractor.getSymbolTable().toString());
	}


	// Join USING Tests end here

	/**************************************************** */
	// CROSS / NATURAL join with invalid ON or USING
	/**************************************************** */

	@Test
	public void crossJoinUsingInvalidConditionFatalTest() {
		final String query = "SELECT * FROM third a CROSS JOIN fourth b USING (a)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertFatalDiagnosticAtPositionWithFullMessage(
				snippet,
				"CROSS_NATURAL_JOIN_INVALID_CONDITION",
				"CROSS JOIN at (l:1 c:23) has invalid USING condition (l:1 c:43).",
				"CROSS JOIN",
				1,
				23);
	}

	@Test
	public void crossJoinOnInvalidConditionFatalTest() {
		final String query = "SELECT * FROM third a CROSS JOIN fourth b ON a.a = b.a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertFatalDiagnosticAtPositionWithFullMessage(
				snippet,
				"CROSS_NATURAL_JOIN_INVALID_CONDITION",
				"CROSS JOIN at (l:1 c:23) has invalid ON condition (l:1 c:43).",
				"CROSS JOIN",
				1,
				23);
	}

	@Test
	public void naturalJoinUsingInvalidConditionFatalTest() {
		final String query = "SELECT * FROM third a NATURAL JOIN fourth b USING (a)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertFatalDiagnosticAtPositionWithFullMessage(
				snippet,
				"CROSS_NATURAL_JOIN_INVALID_CONDITION",
				"NATURAL JOIN at (l:1 c:23) has invalid USING condition (l:1 c:45).",
				"NATURAL JOIN",
				1,
				23);
	}

	@Test
	public void naturalJoinOnInvalidConditionFatalTest() {
		final String query = "SELECT * FROM third a NATURAL JOIN fourth b ON a.a = b.a";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertFatalDiagnosticAtPositionWithFullMessage(
				snippet,
				"CROSS_NATURAL_JOIN_INVALID_CONDITION",
				"NATURAL JOIN at (l:1 c:23) has invalid ON condition (l:1 c:45).",
				"NATURAL JOIN",
				1,
				23);
	}

	/**************************************************** */
	// Join ON Tests start here
	/**************************************************** */
	@Test
	public void basicJoinWithOnTest() {
		final String query = " SELECT a.* FROM third a join fourth b on  a.a = b.b "; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={join=join, on={condition={left={column={name=a, table_ref=a}}, right={column={name=b, table_ref=b}}, operator==}}}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@11,43:43='a',<391>,1:43]], *=[[@1,8:8='a',<391>,1:8]]}, fourth={b=[[@15,49:49='b',<391>,1:49]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,10:10='*',<291>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@3,10:10='*',<291>,1:10]]}, table_dictionary={third={a=[[@11,43:43='a',<391>,1:43]], *=[[@1,8:8='a',<391>,1:8]]}, fourth={b=[[@15,49:49='b',<391>,1:49]]}}, filters=[{name=a, table_ref=a}, {name=b, table_ref=b}], interface={*=[{name=*, table_ref=a}]}, table_alias={a=third, b=fourth}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicLeftJoinWithOnTest() {
		final String query = " SELECT a.* FROM third a left join fourth b on  a.a = b.b "; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={join=left, on={condition={left={column={name=a, table_ref=a}}, right={column={name=b, table_ref=b}}, operator==}}}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@12,48:48='a',<391>,1:48]], *=[[@1,8:8='a',<391>,1:8]]}, fourth={b=[[@16,54:54='b',<391>,1:54]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,10:10='*',<291>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@3,10:10='*',<291>,1:10]]}, table_dictionary={third={a=[[@12,48:48='a',<391>,1:48]], *=[[@1,8:8='a',<391>,1:8]]}, fourth={b=[[@16,54:54='b',<391>,1:54]]}}, filters=[{name=a, table_ref=a}, {name=b, table_ref=b}], interface={*=[{name=*, table_ref=a}]}, table_alias={a=third, b=fourth}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicLeftJoinWithOnAndWhereTest() {
		final String query = " SELECT a.* FROM third a left join fourth b on  a.a = b.b "
				+ "\n where b.c = 1 "; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={join=left, on={condition={left={column={name=a, table_ref=a}}, right={column={name=b, table_ref=b}}, operator==}}}, 3={table={alias=b, table=fourth}}}}, where={condition={left={column={name=c, table_ref=b}}, right={literal=1}, operator==}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@12,48:48='a',<391>,1:48]], *=[[@1,8:8='a',<391>,1:8]]}, fourth={b=[[@16,54:54='b',<391>,1:54]], c=[[@20,66:66='b',<391>,2:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,10:10='*',<291>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@3,10:10='*',<291>,1:10]]}, table_dictionary={third={a=[[@12,48:48='a',<391>,1:48]], *=[[@1,8:8='a',<391>,1:8]]}, fourth={b=[[@16,54:54='b',<391>,1:54]], c=[[@20,66:66='b',<391>,2:7]]}}, filters=[{name=a, table_ref=a}, {name=b, table_ref=b}, {name=c, table_ref=b}], interface={*=[{name=*, table_ref=a}]}, table_alias={a=third, b=fourth}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicJoinWithOnParenthesisTest() {
		// Item 4 - Normal join ON Condition in parentheses should drop the parenthetical
		final String query = " SELECT a.* FROM third a join fourth b on (a.a = b.b)"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={join=join, on={condition={left={column={name=a, table_ref=a}}, right={column={name=b, table_ref=b}}, operator==}}}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@12,43:43='a',<391>,1:43]], *=[[@1,8:8='a',<391>,1:8]]}, fourth={b=[[@16,49:49='b',<391>,1:49]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,10:10='*',<291>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@3,10:10='*',<291>,1:10]]}, table_dictionary={third={a=[[@12,43:43='a',<391>,1:43]], *=[[@1,8:8='a',<391>,1:8]]}, fourth={b=[[@16,49:49='b',<391>,1:49]]}}, filters=[{name=a, table_ref=a}, {name=b, table_ref=b}], interface={*=[{name=*, table_ref=a}]}, table_alias={a=third, b=fourth}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicJoinWithOnOnConditionVariableTest() {
		// Item 46 - Condition Variable not typed or captured
		final String query = " SELECT a.* FROM third a join fourth b on <OnJoinCondition> "; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={join=join, on={substitution={name=<OnJoinCondition>, type=condition}}}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<OnJoinCondition>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@1,8:8='a',<391>,1:8]]}, fourth={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,10:10='*',<291>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@3,10:10='*',<291>,1:10]]}, table_dictionary={third={*=[[@1,8:8='a',<391>,1:8]]}, fourth={}}, filters=[], interface={*=[{name=*, table_ref=a}]}, table_alias={a=third, b=fourth}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void joinOnArithmeticSubtractionComparisonPredicandTest() {
		final String query = "SELECT a.* FROM third a JOIN fourth b ON <a> - 20 >= 50";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={join=JOIN, on={condition={left={calc={left={substitution={name=<a>, type=predicand}}, right={literal=20}, operator=-}}, right={literal=50}, operator=>=}}}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<a>=predicand}",
				extractor.getSubstitutionsMap().toString());
	}

	@Test
	public void joinOnComparisonPredicandOperandTest() {
		final String query = "SELECT a.* FROM third a JOIN fourth b ON a.col1 = <predicand>";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={join=JOIN, on={condition={left={column={name=col1, table_ref=a}}, right={substitution={name=<predicand>, type=predicand}}, operator==}}}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<predicand>=predicand}",
				extractor.getSubstitutionsMap().toString());
	}


	@Test
	public void basicJoinWithOnConditionVariableInParenthesisTest() {
		//  Item 47 - Condition Variable in parenthetical ON statement not typed or captured
		final String query = " SELECT a.* FROM third a join fourth b on (<OnJoinCondition>)"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={join=join, on={substitution={name=<OnJoinCondition>, type=condition}}}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<OnJoinCondition>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@1,8:8='a',<391>,1:8]]}, fourth={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,10:10='*',<291>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@3,10:10='*',<291>,1:10]]}, table_dictionary={third={*=[[@1,8:8='a',<391>,1:8]]}, fourth={}}, filters=[], interface={*=[{name=*, table_ref=a}]}, table_alias={a=third, b=fourth}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicJoinWithOnTwoConditionVariablesTest() {
		//  Condition Variables in an AND clause are labeled and captured correctly
		final String query = " SELECT a.* FROM third a join fourth b on <OnJoinCondition> and <OtherJoinCondition>"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=a}}}, from={join={1={table={alias=a, table=third}}, 2={join=join, on={and={1={substitution={name=<OnJoinCondition>, type=condition}}, 2={substitution={name=<OtherJoinCondition>, type=condition}}}}}, 3={table={alias=b, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<OtherJoinCondition>=condition, <OnJoinCondition>=condition}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@1,8:8='a',<391>,1:8]]}, fourth={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,10:10='*',<291>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@3,10:10='*',<291>,1:10]]}, table_dictionary={third={*=[[@1,8:8='a',<391>,1:8]]}, fourth={}}, filters=[], interface={*=[{name=*, table_ref=a}]}, table_alias={a=third, b=fourth}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinQualifiedWithTupleVariableT1() {
		// ITEM 34 - Qualified Joins (e.g., cross, natural, union) do not parse when tuple substitution variables are included
		final String query = " SELECT * FROM <tuple1> as T3 join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=T3, substitution={name=<tuple1>, type=tuple}}}, 2={join=join}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<tuple1>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<tuple1>={*=[[@1,8:8='*',<291>,1:8]]}, fourth={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={<tuple1>={*=[[@1,8:8='*',<291>,1:8]]}, fourth={*=[[@1,8:8='*',<291>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={F4=fourth, T3=<tuple1>}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void joinQualifiedWithTupleVariableT1Except(){
		// ITEM 34 - Qualified Joins (e.g., cross, natural, union) do not parse when tuple substitution variables are included
		final String query = " SELECT * FROM <tuple1> as T3 join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=T3, substitution={name=<tuple1>, type=tuple}}}, 2={join=join}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<tuple1>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<tuple1>={*=[[@1,8:8='*',<291>,1:8]]}, fourth={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={<tuple1>={*=[[@1,8:8='*',<291>,1:8]]}, fourth={*=[[@1,8:8='*',<291>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={F4=fourth, T3=<tuple1>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinQualifiedWithTupleVariableT2() {
		// ITEM 34 - Qualified Joins (e.g., left) do not parse when tuple substitution variables are included
		final String query = " SELECT * FROM <tuple1> as T3 left outer join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=T3, substitution={name=<tuple1>, type=tuple}}}, 2={join=leftouter}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<tuple1>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<tuple1>={*=[[@1,8:8='*',<291>,1:8]]}, fourth={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={<tuple1>={*=[[@1,8:8='*',<291>,1:8]]}, fourth={*=[[@1,8:8='*',<291>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={F4=fourth, T3=<tuple1>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinUnQualifiedWithTupleVariableT2() {
		// ITEM 34 - Qualified Joins (e.g., cross, natural, union) do not parse when tuple substitution variables are included
		final String query = " SELECT * FROM <tuple1> as T3 cross join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=T3, substitution={name=<tuple1>, type=tuple}}}, 2={join=crossjoin}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<tuple1>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<tuple1>={*=[[@1,8:8='*',<291>,1:8]]}, fourth={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={<tuple1>={*=[[@1,8:8='*',<291>,1:8]]}, fourth={*=[[@1,8:8='*',<291>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={F4=fourth, T3=<tuple1>}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void joinUnQualifiedWithTupleVariableT2Except(){
		// ITEM 34 - Qualified Joins (e.g., cross, natural, union) do not parse when tuple substitution variables are included
		final String query = " SELECT * FROM <tuple1> as T3 cross join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=T3, substitution={name=<tuple1>, type=tuple}}}, 2={join=crossjoin}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<tuple1>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<tuple1>={*=[[@1,8:8='*',<291>,1:8]]}, fourth={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={<tuple1>={*=[[@1,8:8='*',<291>,1:8]]}, fourth={*=[[@1,8:8='*',<291>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={F4=fourth, T3=<tuple1>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinUnQualifiedWithTupleVariableT3() {
		// ITEM 34 - Qualified Joins (e.g., cross, natural, union) do not parse when tuple substitution variables are included
		final String query = " SELECT * FROM <tuple1> as T3 natural join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=T3, substitution={name=<tuple1>, type=tuple}}}, 2={join=naturaljoin}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<tuple1>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<tuple1>={*=[[@1,8:8='*',<291>,1:8]]}, fourth={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={<tuple1>={*=[[@1,8:8='*',<291>,1:8]]}, fourth={*=[[@1,8:8='*',<291>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={F4=fourth, T3=<tuple1>}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void joinUnQualifiedWithTupleVariableT3Except(){
		// ITEM 34 - Qualified Joins (e.g., cross, natural, union) do not parse when tuple substitution variables are included
		final String query = " SELECT * FROM <tuple1> as T3 natural join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=T3, substitution={name=<tuple1>, type=tuple}}}, 2={join=naturaljoin}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<tuple1>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<tuple1>={*=[[@1,8:8='*',<291>,1:8]]}, fourth={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={<tuple1>={*=[[@1,8:8='*',<291>,1:8]]}, fourth={*=[[@1,8:8='*',<291>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={F4=fourth, T3=<tuple1>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinUnQualifiedWithTupleVariableT4() {
		// ITEM 34 - Qualified Joins (e.g., cross, natural, union) do not parse when tuple substitution variables are included
		final String query = " SELECT * FROM <tuple1> as T3 union join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=T3, substitution={name=<tuple1>, type=tuple}}}, 2={join=unionjoin}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<tuple1>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<tuple1>={*=[[@1,8:8='*',<291>,1:8]]}, fourth={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={<tuple1>={*=[[@1,8:8='*',<291>,1:8]]}, fourth={*=[[@1,8:8='*',<291>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={F4=fourth, T3=<tuple1>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void handlingSelectStarWithExplicitTableRef1() {
		final String query = " SELECT t3.* FROM <tuple1> as T3 union join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=t3}}}, from={join={1={table={alias=T3, substitution={name=<tuple1>, type=tuple}}}, 2={join=unionjoin}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<tuple1>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<tuple1>={*=[[@1,8:9='t3',<391>,1:8]]}, fourth={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@3,11:11='*',<291>,1:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@3,11:11='*',<291>,1:11]]}, table_dictionary={<tuple1>={*=[[@1,8:9='t3',<391>,1:8]]}, fourth={}}, interface={*=[{name=*, table_ref=t3}]}, table_alias={F4=fourth, T3=<tuple1>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void handlingRepeatingColumnNamesInTheInterfaceV1() {
		final String query = " SELECT T3.col1, F4.col1 FROM third as T3 join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=T3}}, 2={column={name=col1, table_ref=F4}}}, from={join={1={table={alias=T3, table=third}}, 2={join=join}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={col1=[[@1,8:9='T3',<391>,1:8]]}, fourth={col1=[[@5,17:18='F4',<391>,1:17]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col1=[[@3,11:14='col1',<391>,1:11], [@7,20:23='col1',<391>,1:20]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Tree is wrong", "{def_query0={query_dictionary={col1=[[@3,11:14='col1',<391>,1:11], [@7,20:23='col1',<391>,1:20]]}, table_dictionary={third={col1=[[@1,8:9='T3',<391>,1:8]]}, fourth={col1=[[@5,17:18='F4',<391>,1:17]]}}, interface={col1=[{name=col1, table_ref=F4}]}, table_alias={F4=fourth, T3=third}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(snippet, "DUPLICATE_INTERFACE_COLUMNS",
				"Duplicate interface columns defined: T3.col1 at (l:1 c:11) and F4.col1 at (l:1 c:20).",
				"T3.col1,F4.col1", 1, 11);
	}


	@Test
	public void handlingRepeatingColumnNamesInTheInterfaceV2() {
		final String query = " SELECT col1, T3.col1, F4.col1 FROM third as T3 join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=null}}, 2={column={name=col1, table_ref=T3}}, 3={column={name=col1, table_ref=F4}}}, from={join={1={table={alias=T3, table=third}}, 2={join=join}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={col1=[[@3,14:15='T3',<391>,1:14]]}, fourth={col1=[[@7,23:24='F4',<391>,1:23]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col1=[[@1,8:11='col1',<391>,1:8], [@5,17:20='col1',<391>,1:17], [@9,26:29='col1',<391>,1:26]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Tree is wrong", "{def_query0={query_dictionary={col1=[[@1,8:11='col1',<391>,1:8], [@5,17:20='col1',<391>,1:17], [@9,26:29='col1',<391>,1:26]]}, table_dictionary={third={col1=[[@3,14:15='T3',<391>,1:14]]}, fourth={col1=[[@7,23:24='F4',<391>,1:23]]}}, interface={col1=[{name=col1, table_ref=F4}]}, table_alias={F4=fourth, T3=third}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(snippet, "DUPLICATE_INTERFACE_COLUMNS",
				"Duplicate interface columns defined: col1 at (l:1 c:8) and T3.col1 at (l:1 c:17).",
				"col1,T3.col1", 1, 8);
		assertFatalDiagnosticAtPosition(snippet, "DUPLICATE_INTERFACE_COLUMNS",
				"Duplicate interface columns defined: T3.col1 at (l:1 c:8) and F4.col1 at (l:1 c:26).",
				"T3.col1,F4.col1", 1, 8);
		assertFatalDiagnosticCount(snippet,
				"DUPLICATE_INTERFACE_COLUMNS",
				"Duplicate interface columns defined",
				null,
				2);
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s): [col1",
				"col1", 1, 8);
	}


	@Test
	public void handlingRepeatingColumnNamesInTheInterfaceV3() {
		final String query = " SELECT T3.col1, (F4.x + T3.y) col1 FROM third as T3 join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=T3}}, 2={parentheses={calc={left={column={name=x, table_ref=F4}}, right={column={name=y, table_ref=T3}}, operator=+}}, alias=col1}}, from={join={1={table={alias=T3, table=third}}, 2={join=join}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={y=[[@10,25:26='T3',<391>,1:25]], col1=[[@1,8:9='T3',<391>,1:8]]}, fourth={x=[[@6,18:19='F4',<391>,1:18]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col1=[[@3,11:14='col1',<391>,1:11], [@14,31:34='col1',<391>,1:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Tree is wrong", "{def_query0={query_dictionary={col1=[[@3,11:14='col1',<391>,1:11], [@14,31:34='col1',<391>,1:31]]}, table_dictionary={third={y=[[@10,25:26='T3',<391>,1:25]], col1=[[@1,8:9='T3',<391>,1:8]]}, fourth={x=[[@6,18:19='F4',<391>,1:18]]}}, interface={col1=[{name=x, table_ref=F4}, {name=y, table_ref=T3}]}, table_alias={F4=fourth, T3=third}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(snippet, "DUPLICATE_INTERFACE_COLUMNS",
				"Duplicate interface columns defined: T3.col1 at (l:1 c:11) and F4.x at (l:1 c:31).",
				"T3.col1,F4.x", 1, 11);
		// assertFatalDiagnosticAtPosition(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
		// 		"Source Table not found for Column 'col1' at (l:1 c:8). No alias or table called 'T3'.",
		// 		"col1", 1, 8);
	}


	@Test
	public void joinSubqueryTableV1() {
		// ITEM 92 - Join with subquery and table shouldn't need on statement
		final String query = " SELECT * FROM (select * from third) as T3 join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=T3, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}}}, 2={join=join}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@5,23:23='*',<291>,1:23]]}, fourth={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@5,23:23='*',<291>,1:23]]}, query1={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={fourth={*=[[@1,8:8='*',<291>,1:8]]}}, def_query0={query_dictionary={*=[[@5,23:23='*',<291>,1:23]]}, table_dictionary={third={*=[[@5,23:23='*',<291>,1:23]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={T3=query0, F4=fourth}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinSubqueryWithColumnsTableV1() {
		// ITEM 92 - Join with subquery and table shouldn't need on statement
		final String query = " SELECT T3.transcol as outercol, F4.tablecol FROM (select innercol as transcol, othercol from third) as T3 join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=transcol, table_ref=T3}, alias=outercol}, 2={column={name=tablecol, table_ref=F4}}}, from={join={1={table={alias=T3, query={select={1={column={name=innercol, table_ref=null}, alias=transcol}, 2={column={name=othercol, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={join=join}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[tablecol, outercol]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={othercol=[[@17,80:87='othercol',<391>,1:80]], innercol=[[@13,58:65='innercol',<391>,1:58]]}, fourth={tablecol=[[@7,33:34='F4',<391>,1:33]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={transcol=[[@15,70:77='transcol',<391>,1:70], [@1,8:9='T3',<391>,1:8]], othercol=[[@17,80:87='othercol',<391>,1:80]]}, query1={tablecol=[[@9,36:43='tablecol',<391>,1:36]], outercol=[[@5,23:30='outercol',<391>,1:23]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={tablecol=[[@9,36:43='tablecol',<391>,1:36]], outercol=[[@5,23:30='outercol',<391>,1:23]]}, table_dictionary={fourth={tablecol=[[@7,33:34='F4',<391>,1:33]]}}, def_query0={query_dictionary={transcol=[[@15,70:77='transcol',<391>,1:70], [@1,8:9='T3',<391>,1:8]], othercol=[[@17,80:87='othercol',<391>,1:80]]}, table_dictionary={third={othercol=[[@17,80:87='othercol',<391>,1:80]], innercol=[[@13,58:65='innercol',<391>,1:58]]}}, interface={transcol=[{name=innercol, table_ref=third}], othercol=[{name=othercol, table_ref=third}]}}, interface={tablecol=[{name=tablecol, table_ref=F4}], outercol=[{name=transcol, table_ref=T3}]}, table_alias={T3=query0, F4=fourth}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinSubqueryTableV2() {
		// ITEM 92 - Join with subquery and table shouldn't need on statement
		final String query = " SELECT * FROM (select * from third) as T3 cross join fourth as F4";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=T3, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}}}, 2={join=crossjoin}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@5,23:23='*',<291>,1:23]]}, fourth={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@5,23:23='*',<291>,1:23]]}, query1={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={fourth={*=[[@1,8:8='*',<291>,1:8]]}}, def_query0={query_dictionary={*=[[@5,23:23='*',<291>,1:23]]}, table_dictionary={third={*=[[@5,23:23='*',<291>,1:23]]}}, interface={*=[{name=*, table_ref=*}]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={T3=query0, F4=fourth}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void fromListTest() {
		final String query = " SELECT * FROM third ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={third={*=[[@1,8:8='*',<291>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void tableListWithTupleVariableV1() {
		final String query = " SELECT * FROM third, <tuple variable> as two ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=null, table=third}}, 2={table={alias=two, substitution={name=<tuple variable>, type=tuple}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<tuple variable>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<tuple variable>={*=[[@1,8:8='*',<291>,1:8]]}, third={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={<tuple variable>={*=[[@1,8:8='*',<291>,1:8]]}, third={*=[[@1,8:8='*',<291>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={two=<tuple variable>}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void oneTableWithJoinExtensionVariableV1() {
		//  ITEM 17 - Doesn't recognize optional join tree additions after the on clause
		final String query = " SELECT * FROM third <extension> ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={extension={substitution={name=<extension>, type=join_extension}}, table={alias=null, table=third}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<extension>=join_extension}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={third={*=[[@1,8:8='*',<291>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinlessJoinExtensionVariableV1() {
		//  ITEM 17 - Doesn't recognize optional join tree additions after the on clause
		final String query = " SELECT * FROM third as T3, fourth as F4 <extension> ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={extension={substitution={name=<extension>, type=join_extension}}, join={1={table={alias=T3, table=third}}, 2={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<extension>=join_extension}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@1,8:8='*',<291>,1:8]]}, fourth={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={third={*=[[@1,8:8='*',<291>,1:8]]}, fourth={*=[[@1,8:8='*',<291>,1:8]]}}, interface={*=[{name=*, table_ref=*}]}, table_alias={F4=fourth, T3=third}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinExtensionVariableV1() {
		//  ITEM 17 - Doesn't recognize optional join tree additions after the on clause
		final String query = " SELECT * FROM third as T3 join fourth as F4 on <third_fourth_join_condition> <extension> ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={extension={substitution={name=<extension>, type=join_extension}}, join={1={table={alias=T3, table=third}}, 2={join=join, on={substitution={name=<third_fourth_join_condition>, type=condition}}}, 3={table={alias=F4, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<third_fourth_join_condition>=condition, <extension>=join_extension}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@1,8:8='*',<291>,1:8]]}, fourth={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={third={*=[[@1,8:8='*',<291>,1:8]]}, fourth={*=[[@1,8:8='*',<291>,1:8]]}}, filters=[], interface={*=[{name=*, table_ref=*}]}, table_alias={F4=fourth, T3=third}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void joinWithDuplicateColumnNameTest() {
		// Both sides of the join expose the same unqualified output column names
		// (category, is_active, nk, rank, desc, student), so the walker emits
		// AMBIGUOUS_COLUMN_REFERENCE diagnostics for each of those outer select columns.
		final String query = "SELECT 'Guide' AS app_name,  category, is_active, nk, rank, desc, student " + 
				"FROM  <Guide> AS Guide_Student_Conditions " + 
				"\n join " + 
				"\n (SELECT 'Nav' AS app_name, category, is_active, nk, rank, desc, student " + 
				"FROM <NAV> AS  Nav_Ss) AS Nav_Student_Conditions on 1=1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=app_name, literal='Guide'}, 2={column={name=category, table_ref=null}}, 3={column={name=is_active, table_ref=null}}, 4={column={name=nk, table_ref=null}}, 5={column={name=rank, table_ref=null}}, 6={column={name=desc, table_ref=null}}, 7={column={name=student, table_ref=null}}}, from={join={1={table={alias=Guide_Student_Conditions, substitution={name=<Guide>, type=tuple}}}, 2={join=join, on={condition={left={literal=1}, right={literal=1}, operator==}}}, 3={table={alias=Nav_Student_Conditions, query={select={1={alias=app_name, literal='Nav'}, 2={column={name=category, table_ref=null}}, 3={column={name=is_active, table_ref=null}}, 4={column={name=nk, table_ref=null}}, 5={column={name=rank, table_ref=null}}, 6={column={name=desc, table_ref=null}}, 7={column={name=student, table_ref=null}}}, from={table={alias=Nav_Ss, substitution={name=<NAV>, type=tuple}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[app_name, is_active, student, rank, category, nk, desc]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<Guide>=tuple, <NAV>=tuple}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<Guide>={is_active=[[@7,39:47='is_active',<391>,1:39]], student=[[@15,66:72='student',<391>,1:66]], rank=[[@11,54:57='rank',<128>,1:54]], category=[[@5,29:36='category',<391>,1:29]], nk=[[@9,50:51='nk',<391>,1:50]], desc=[[@13,60:63='desc',<77>,1:60]]}, <NAV>={is_active=[[@29,162:170='is_active',<391>,3:38]], student=[[@37,189:195='student',<391>,3:65]], rank=[[@33,177:180='rank',<128>,3:53]], category=[[@27,152:159='category',<391>,3:28]], nk=[[@31,173:174='nk',<391>,3:49]], desc=[[@35,183:186='desc',<77>,3:59]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={app_name=[[@25,142:149='app_name',<391>,3:18]], is_active=[[@29,162:170='is_active',<391>,3:38]], student=[[@37,189:195='student',<391>,3:65]], rank=[[@33,177:180='rank',<128>,3:53]], category=[[@27,152:159='category',<391>,3:28]], nk=[[@31,173:174='nk',<391>,3:49]], desc=[[@35,183:186='desc',<77>,3:59]]}, query1={app_name=[[@3,18:25='app_name',<391>,1:18]], is_active=[[@7,39:47='is_active',<391>,1:39]], student=[[@15,66:72='student',<391>,1:66]], rank=[[@11,54:57='rank',<128>,1:54]], category=[[@5,29:36='category',<391>,1:29]], nk=[[@9,50:51='nk',<391>,1:50]], desc=[[@13,60:63='desc',<77>,1:60]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={app_name=[[@3,18:25='app_name',<391>,1:18]], is_active=[[@7,39:47='is_active',<391>,1:39]], student=[[@15,66:72='student',<391>,1:66]], rank=[[@11,54:57='rank',<128>,1:54]], category=[[@5,29:36='category',<391>,1:29]], nk=[[@9,50:51='nk',<391>,1:50]], desc=[[@13,60:63='desc',<77>,1:60]]}, table_dictionary={<Guide>={is_active=[[@7,39:47='is_active',<391>,1:39]], student=[[@15,66:72='student',<391>,1:66]], rank=[[@11,54:57='rank',<128>,1:54]], category=[[@5,29:36='category',<391>,1:29]], nk=[[@9,50:51='nk',<391>,1:50]], desc=[[@13,60:63='desc',<77>,1:60]]}}, def_query0={query_dictionary={app_name=[[@25,142:149='app_name',<391>,3:18]], is_active=[[@29,162:170='is_active',<391>,3:38]], student=[[@37,189:195='student',<391>,3:65]], rank=[[@33,177:180='rank',<128>,3:53]], category=[[@27,152:159='category',<391>,3:28]], nk=[[@31,173:174='nk',<391>,3:49]], desc=[[@35,183:186='desc',<77>,3:59]]}, table_dictionary={<NAV>={is_active=[[@29,162:170='is_active',<391>,3:38]], student=[[@37,189:195='student',<391>,3:65]], rank=[[@33,177:180='rank',<128>,3:53]], category=[[@27,152:159='category',<391>,3:28]], nk=[[@31,173:174='nk',<391>,3:49]], desc=[[@35,183:186='desc',<77>,3:59]]}}, interface={app_name=[], is_active=[{name=is_active, table_ref=<NAV>}], student=[{name=student, table_ref=<NAV>}], rank=[{name=rank, table_ref=<NAV>}], category=[{name=category, table_ref=<NAV>}], nk=[{name=nk, table_ref=<NAV>}], desc=[{name=desc, table_ref=<NAV>}]}, table_alias={Nav_Ss=<NAV>}}, filters=[], interface={app_name=[], is_active=[{name=is_active, table_ref=null}], student=[{name=student, table_ref=null}], rank=[{name=rank, table_ref=null}], category=[{name=category, table_ref=null}], nk=[{name=nk, table_ref=null}], desc=[{name=desc, table_ref=null}]}, table_alias={Guide_Student_Conditions=<Guide>, Nav_Student_Conditions=query0}}}",
				extractor.getSymbolTable().toString());
		Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'category'",
				"category",
				1,
				29);
		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'is_active'",
				"is_active",
				1,
				39);
		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'nk'",
				null,
				1,
				50);
		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'rank'",
				"rank",
				1,
				54);
		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'desc'",
				"desc",
				1,
				60);
		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'student'",
				"student",
				1,
				66);
	}


	@Test
	public void simpleQuotedTableNameTest() {
		final String query = "SELECT * FROM \"Name\"";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=\"Name\"}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{\"Name\"={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={\"Name\"={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void simpleQuotedSchemaAndTableNameTest() {
		final String query = "SELECT * FROM \"scheme\".\"Name\"";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={schema=\"scheme\", alias=null, table=\"Name\"}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{\"scheme\".\"Name\"={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={\"scheme\".\"Name\"={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void simpleQuotedDatabaseSchemaAndTableNameTest() {
		final String query = "SELECT * FROM \"db\".\"scheme\".\"Name\"";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={schema=\"scheme\", dbname=\"db\", alias=null, table=\"Name\"}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{\"db\".\"scheme\".\"Name\"={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={\"db\".\"scheme\".\"Name\"={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void quotedGuidDatabaseNameUnquotedSchemaAndUnquotedTableNameTest() {
		final String query = "SELECT * FROM \"PROD-3beb02cb-f710-4d2d-a6a1-40c229e4a40e\".panto.\"1234_987654\"";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={schema=panto, dbname=\"PROD-3beb02cb-f710-4d2d-a6a1-40c229e4a40e\", alias=null, table=\"1234_987654\"}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{\"PROD-3beb02cb-f710-4d2d-a6a1-40c229e4a40e\".panto.\"1234_987654\"={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={\"PROD-3beb02cb-f710-4d2d-a6a1-40c229e4a40e\".panto.\"1234_987654\"={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void simpleQuotedColumnNameTest() {
		final String query = "SELECT \"ColUmn_Name\" as cOl1, Col2 FROM \"Name\" where \"cOlumn_nAME\" > 5 and cOL2 < 10";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=\"ColUmn_Name\", table_ref=null}, alias=cOl1}, 2={column={name=Col2, table_ref=null}}}, from={table={alias=null, table=\"Name\"}}, where={and={1={condition={left={column={name=\"cOlumn_nAME\", table_ref=null}}, right={literal=5}, operator=>}}, 2={condition={left={column={name=cOL2, table_ref=null}}, right={literal=10}, operator=<}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[Col2, cOl1]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		// Quoted column names are case-sensitive: "ColUmn_Name" and "cOlumn_nAME" produce two distinct entries.
		// Unquoted column names are case-insensitive: Col2 and cOL2 merge into one entry (col2) with two token refs.
		Assert.assertEquals("Table Dictionary is wrong",
				"{\"Name\"={Col2=[[@5,30:33='Col2',<391>,1:30], [@13,75:78='cOL2',<391>,1:75]], \"ColUmn_Name\"=[[@1,7:19='\"ColUmn_Name\"',<391>,1:7]], \"cOlumn_nAME\"=[[@9,53:65='\"cOlumn_nAME\"',<391>,1:53]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={Col2=[[@5,30:33='Col2',<391>,1:30]], cOl1=[[@3,24:27='cOl1',<391>,1:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={Col2=[[@5,30:33='Col2',<391>,1:30]], cOl1=[[@3,24:27='cOl1',<391>,1:24]]}, table_dictionary={\"Name\"={Col2=[[@5,30:33='Col2',<391>,1:30], [@13,75:78='cOL2',<391>,1:75]], \"ColUmn_Name\"=[[@1,7:19='\"ColUmn_Name\"',<391>,1:7]], \"cOlumn_nAME\"=[[@9,53:65='\"cOlumn_nAME\"',<391>,1:53]]}}, filters=[{name=\"cOlumn_nAME\", table_ref=\"Name\"}, {name=cOL2, table_ref=\"Name\"}], interface={Col2=[{name=Col2, table_ref=\"Name\"}], cOl1=[{name=\"ColUmn_Name\", table_ref=\"Name\"}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void variation4ColumnVariableTest() {
		// 3 nested subqueries with correlated subquery at each nested query
		// under join with column variable in middle subquery
		final String query = "select apple from "
			+ "\n (SELECT apple from "
			+" \n (SELECT apple, banana from tab1 where tab2.<other> > 20) b "
			+ "\n where tab2.<middle>) a"
			+ "\n join tab2 on a.apple = tab2.pickle";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={join={1={table={alias=a, query={select={1={column={name=apple, table_ref=null}}}, from={table={alias=b, query={select={1={column={name=apple, table_ref=null}}, 2={column={name=banana, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={column={substitution={name=<other>, type=column}, table_ref=tab2}}, right={literal=20}, operator=>}}}}}, where={column={substitution={name=<middle>, type=column}, table_ref=tab2}}}}}, 2={join=join, on={condition={left={column={name=apple, table_ref=a}}, right={column={name=pickle, table_ref=tab2}}, operator==}}}, 3={table={alias=null, table=tab2}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<middle>=column, <other>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={banana=[[@11,57:62='banana',<391>,3:16]], apple=[[@9,50:54='apple',<391>,3:9]]}, tab2={apple=[[@1,7:11='apple',<391>,1:7]], <middle>=[[@23,109:112='tab2',<391>,4:7]], <other>=[[@15,80:83='tab2',<391>,3:39]], pickle=[[@35,150:153='tab2',<391>,5:24]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={banana=[[@11,57:62='banana',<391>,3:16]], apple=[[@9,50:54='apple',<391>,3:9], [@5,28:32='apple',<391>,2:9]]}, query1={apple=[[@5,28:32='apple',<391>,2:9], [@31,140:140='a',<391>,5:14]]}, query2={apple=[[@1,7:11='apple',<391>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={apple=[[@1,7:11='apple',<391>,1:7]]}, table_dictionary={tab2={apple=[[@1,7:11='apple',<391>,1:7]], <middle>=[[@23,109:112='tab2',<391>,4:7]], <other>=[[@15,80:83='tab2',<391>,3:39]], pickle=[[@35,150:153='tab2',<391>,5:24]]}}, def_query1={query_dictionary={apple=[[@5,28:32='apple',<391>,2:9], [@31,140:140='a',<391>,5:14]]}, def_query0={query_dictionary={banana=[[@11,57:62='banana',<391>,3:16]], apple=[[@9,50:54='apple',<391>,3:9], [@5,28:32='apple',<391>,2:9]]}, table_dictionary={tab1={banana=[[@11,57:62='banana',<391>,3:16]], apple=[[@9,50:54='apple',<391>,3:9]]}}, filters=[{substitution={name=<other>, type=column}, table_ref=tab2}], interface={banana=[{name=banana, table_ref=tab1}], apple=[{name=apple, table_ref=tab1}]}}, filters=[{substitution={name=<middle>, type=column}, table_ref=tab2}], interface={apple=[{name=apple, table_ref=query0}]}, table_alias={b=query0}}, filters=[{name=apple, table_ref=a}, {name=pickle, table_ref=tab2}], interface={apple=[{name=apple, table_ref=null}]}, table_alias={a=query1}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'apple'",
				"apple",
				1,
				7);
	}


	@Test
	public void variation4_2ColumnVariableTest() {
		// 3 nested subqueries with correlated subquery at each nested query
		// under join with column variable in middle subquery but repeat of tab2.apple
		// at the top level join.
		final String query = "select apple from "
			+ "\n (SELECT apple from "
			+" \n (SELECT apple, banana from tab1 where tab2.<other> > 20) b "
			+ "\n where tab2.<middle>) a"
			+ "\n join tab2 on a.apple = tab2.apple";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}}, from={join={1={table={alias=a, query={select={1={column={name=apple, table_ref=null}}}, from={table={alias=b, query={select={1={column={name=apple, table_ref=null}}, 2={column={name=banana, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={column={substitution={name=<other>, type=column}, table_ref=tab2}}, right={literal=20}, operator=>}}}}}, where={column={substitution={name=<middle>, type=column}, table_ref=tab2}}}}}, 2={join=join, on={condition={left={column={name=apple, table_ref=a}}, right={column={name=apple, table_ref=tab2}}, operator==}}}, 3={table={alias=null, table=tab2}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<middle>=column, <other>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={banana=[[@11,57:62='banana',<391>,3:16]], apple=[[@9,50:54='apple',<391>,3:9]]}, tab2={apple=[[@35,150:153='tab2',<391>,5:24], [@1,7:11='apple',<391>,1:7]], <middle>=[[@23,109:112='tab2',<391>,4:7]], <other>=[[@15,80:83='tab2',<391>,3:39]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={banana=[[@11,57:62='banana',<391>,3:16]], apple=[[@9,50:54='apple',<391>,3:9], [@5,28:32='apple',<391>,2:9]]}, query1={apple=[[@5,28:32='apple',<391>,2:9], [@31,140:140='a',<391>,5:14]]}, query2={apple=[[@1,7:11='apple',<391>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={apple=[[@1,7:11='apple',<391>,1:7]]}, table_dictionary={tab2={apple=[[@35,150:153='tab2',<391>,5:24], [@1,7:11='apple',<391>,1:7]], <middle>=[[@23,109:112='tab2',<391>,4:7]], <other>=[[@15,80:83='tab2',<391>,3:39]]}}, def_query1={query_dictionary={apple=[[@5,28:32='apple',<391>,2:9], [@31,140:140='a',<391>,5:14]]}, def_query0={query_dictionary={banana=[[@11,57:62='banana',<391>,3:16]], apple=[[@9,50:54='apple',<391>,3:9], [@5,28:32='apple',<391>,2:9]]}, table_dictionary={tab1={banana=[[@11,57:62='banana',<391>,3:16]], apple=[[@9,50:54='apple',<391>,3:9]]}}, filters=[{substitution={name=<other>, type=column}, table_ref=tab2}], interface={banana=[{name=banana, table_ref=tab1}], apple=[{name=apple, table_ref=tab1}]}}, filters=[{substitution={name=<middle>, type=column}, table_ref=tab2}], interface={apple=[{name=apple, table_ref=query0}]}, table_alias={b=query0}}, filters=[{name=apple, table_ref=a}, {name=apple, table_ref=tab2}], interface={apple=[{name=apple, table_ref=null}]}, table_alias={a=query1}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'apple' at (l:1 c:7). Possible sources: [tab2, query1]",
				"apple",
				1,
				7);
	}


	@Test
	public void simpleColumnAllocationTest() {
		String query = " select a aa,b,c from tab1 dd";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}, alias=aa}, 2={column={name=b, table_ref=null}}, 3={column={name=c, table_ref=null}}}, from={table={alias=dd, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, b, c]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[[@1,8:8='a',<391>,1:8]], b=[[@4,13:13='b',<391>,1:13]], c=[[@6,15:15='c',<391>,1:15]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={aa=[[@2,10:11='aa',<391>,1:10]], b=[[@4,13:13='b',<391>,1:13]], c=[[@6,15:15='c',<391>,1:15]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={aa=[[@2,10:11='aa',<391>,1:10]], b=[[@4,13:13='b',<391>,1:13]], c=[[@6,15:15='c',<391>,1:15]]}, table_dictionary={tab1={a=[[@1,8:8='a',<391>,1:8]], b=[[@4,13:13='b',<391>,1:13]], c=[[@6,15:15='c',<391>,1:15]]}}, interface={aa=[{name=a, table_ref=tab1}], b=[{name=b, table_ref=tab1}], c=[{name=c, table_ref=tab1}]}, table_alias={dd=tab1}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void explicitButPartialColumnAllocationTest() {
		String query = " select dd.a aa, dd.b, \n c from tab1 dd";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=dd}, alias=aa}, 2={column={name=b, table_ref=dd}}, 3={column={name=c, table_ref=null}}}, from={table={alias=dd, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, b, c]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[[@1,8:9='dd',<391>,1:8]], b=[[@6,17:18='dd',<391>,1:17]], c=[[@10,25:25='c',<391>,2:1]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={aa=[[@4,13:14='aa',<391>,1:13]], b=[[@8,20:20='b',<391>,1:20]], c=[[@10,25:25='c',<391>,2:1]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={aa=[[@4,13:14='aa',<391>,1:13]], b=[[@8,20:20='b',<391>,1:20]], c=[[@10,25:25='c',<391>,2:1]]}, table_dictionary={tab1={a=[[@1,8:9='dd',<391>,1:8]], b=[[@6,17:18='dd',<391>,1:17]], c=[[@10,25:25='c',<391>,2:1]]}}, interface={aa=[{name=a, table_ref=dd}], b=[{name=b, table_ref=dd}], c=[{name=c, table_ref=tab1}]}, table_alias={dd=tab1}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void ambiguousColumnAllocationTest() {
		String query = " select dd.a aa, cc.b, c from tab1 dd join tab2 cc";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=dd}, alias=aa}, 2={column={name=b, table_ref=cc}}, 3={column={name=c, table_ref=null}}}, from={join={1={table={alias=dd, table=tab1}}, 2={join=join}, 3={table={alias=cc, table=tab2}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, b, c]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[[@1,8:9='dd',<391>,1:8]]}, tab2={b=[[@6,17:18='cc',<391>,1:17]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={aa=[[@4,13:14='aa',<391>,1:13]], b=[[@8,20:20='b',<391>,1:20]], c=[[@10,23:23='c',<391>,1:23]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={aa=[[@4,13:14='aa',<391>,1:13]], b=[[@8,20:20='b',<391>,1:20]], c=[[@10,23:23='c',<391>,1:23]]}, table_dictionary={tab1={a=[[@1,8:9='dd',<391>,1:8]]}, tab2={b=[[@6,17:18='cc',<391>,1:17]]}}, interface={aa=[{name=a, table_ref=dd}], b=[{name=b, table_ref=cc}], c=[{name=c, table_ref=null}]}, table_alias={dd=tab1, cc=tab2}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'c'",
				"c", 1, 23);
		assertUnresolvedUnknownColumnsDiagnostic(snippet, 1, 23, ParseDiagnostic.Severity.ERROR, "c");
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s): [c",
				"c", 1, 23);
	}


	@Test
	public void ambiguousColumnAllocationCollectsFatalDiagnosticTest() {
		String query = " select dd.a aa, cc.b, c from tab1 dd join tab2 cc";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertDiagnosticAtPosition(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'c'",
				"c", 1, 23);
		assertUnresolvedUnknownColumnsDiagnostic(snippet, 1, 23, ParseDiagnostic.Severity.ERROR, "c");
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s): [c",
				"c", 1, 23);
	}


	@Test
	public void unresolvedUnknownSymbolTableWithSimpleSubqueryTest() {
		String query = " select a aa, b, c from (select a, e as b from ee where 1=1) dd";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}, alias=aa}, 2={column={name=b, table_ref=null}}, 3={column={name=c, table_ref=null}}}, from={table={alias=dd, query={select={1={column={name=a, table_ref=null}}, 2={column={name=e, table_ref=null}, alias=b}}, from={table={alias=null, table=ee}}, where={condition={left={literal=1}, right={literal=1}, operator==}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[aa, b, c]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{ee={a=[[@10,32:32='a',<391>,1:32]], e=[[@12,35:35='e',<391>,1:35]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@10,32:32='a',<391>,1:32], [@1,8:8='a',<391>,1:8]], b=[[@14,40:40='b',<391>,1:40], [@4,14:14='b',<391>,1:14]]}, query1={aa=[[@2,10:11='aa',<391>,1:10]], b=[[@4,14:14='b',<391>,1:14]], c=[[@6,17:17='c',<391>,1:17]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={aa=[[@2,10:11='aa',<391>,1:10]], b=[[@4,14:14='b',<391>,1:14]], c=[[@6,17:17='c',<391>,1:17]]}, def_query0={query_dictionary={a=[[@10,32:32='a',<391>,1:32], [@1,8:8='a',<391>,1:8]], b=[[@14,40:40='b',<391>,1:40], [@4,14:14='b',<391>,1:14]]}, table_dictionary={ee={a=[[@10,32:32='a',<391>,1:32]], e=[[@12,35:35='e',<391>,1:35]]}}, filters=[], interface={a=[{name=a, table_ref=ee}], b=[{name=e, table_ref=ee}]}}, interface={aa=[{name=a, table_ref=query0}], b=[{name=b, table_ref=query0}], c=[{name=c, table_ref=null}]}, table_alias={dd=query0}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertUnresolvedUnknownColumnsDiagnostic(snippet, 1, 17, ParseDiagnostic.Severity.ERROR, "c");
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s)",
				"c", 1, 17);
	}


	@Test
	public void unresolvedUnknownSymbolTableWithSimpleSubqueryCollectsFatalDiagnosticTest() {
		String query = " select a aa, b, c from (select a, e as b from ee where 1=1) dd";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertUnresolvedUnknownColumnsDiagnostic(snippet, 1, 17, ParseDiagnostic.Severity.ERROR, "c");
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved",
				"c", 1, 17);
	}


	@Test
	public void ambiguousQueryColumnWithCompetingSubqueryAliasesCollectsFatalDiagnosticV1Test() {
		String query = "SELECT a FROM (SELECT x AS a FROM tab1) dd JOIN (SELECT z AS a FROM tab2) cc ON dd.a = cc.a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertDiagnosticAtPosition(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "Ambiguous column reference 'a'", "a", 1, 7);
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s)",
				"a", 1, 7);
	}


	@Test
	public void ambiguousQueryColumnWithCompetingSubqueryAliasesCollectsFatalDiagnosticV2Test() {
		String query = "SELECT a FROM (SELECT * FROM tab1) dd JOIN (SELECT z AS a FROM tab2) cc ON dd.a = cc.a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertDiagnosticAtPosition(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "Ambiguous column reference 'a'", "a", 1, 7);
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s)",
				"a", 1, 7);
	}


	@Test
	public void ambiguousQueryColumnWithCompetingSubqueryAliasesCollectsFatalDiagnosticV3Test() {
		String query = "SELECT a FROM (SELECT * FROM tab1) dd JOIN (SELECT * FROM tab2) cc ON dd.a = cc.a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertDiagnosticAtPosition(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "Ambiguous column reference 'a'", "a", 1, 7);
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s)",
				"a", 1, 7);
	}


	@Test
	public void ambiguousQueryColumnWithCompetingSubqueryAliasesCollectsFatalDiagnosticV4Test() {
		String query = "SELECT a FROM tab1 dd JOIN tab2 cc ON dd.a = cc.a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertDiagnosticAtPosition(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "Ambiguous column reference 'a'", "a", 1, 7);
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s)",
				"a", 1, 7);
	}


	@Test
	public void ambiguousQueryColumnWithCompetingSubqueryAliasesCollectsFatalDiagnosticV5Test() {
		String query = "SELECT a FROM tab1 dd JOIN tab2 cc ON dd.a = cc.a"
			+ " JOIN (select * from tab3) ee ON dd.a = ee.a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertDiagnosticAtPosition(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "Ambiguous column reference 'a'", "a", 1, 7);
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s)",
				"a", 1, 7);
	}


	@Test
	public void ambiguousQueryColumnWithCompetingSubqueryAliasesCollectsFatalDiagnosticV6Test() {
		String query = "SELECT a FROM tab1 dd JOIN tab2 cc ON dd.a = cc.a"
			+ " JOIN (select x as a from tab3) ee ON dd.a = ee.a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertDiagnosticAtPosition(snippet, "AMBIGUOUS_COLUMN_REFERENCE", ParseDiagnostic.Severity.SEVERE_WARNING, "Ambiguous column reference 'a'", "a", 1, 7);
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s)",
				"a", 1, 7);
	}


	@Test
	public void simpleFromListType3ParseTest() {

		final String query = " SELECT * FROM third cross join fourth "
				+ " union join fifth natural join sixth natural inner join seventh";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
	}


	@Test
	public void simpleFromListType4ParseTest() {

		final String query = " SELECT * FROM third join fourth on a = b " 
		+ " left outer join fifth on b = d ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
				
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=null, table=third}}, 2={join=join, on={condition={left={column={name=a, table_ref=null}}, right={column={name=b, table_ref=null}}, operator==}}}, 3={table={alias=null, table=fourth}}, 4={join=leftouter, on={condition={left={column={name=b, table_ref=null}}, right={column={name=d, table_ref=null}}, operator==}}}, 5={table={alias=null, table=fifth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@1,8:8='*',<291>,1:8]]}, fifth={*=[[@1,8:8='*',<291>,1:8]]}, fourth={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={third={*=[[@1,8:8='*',<291>,1:8]]}, fifth={*=[[@1,8:8='*',<291>,1:8]]}, fourth={*=[[@1,8:8='*',<291>,1:8]]}}, filters=[{name=a, table_ref=null}, {name=b, table_ref=null}, {name=d, table_ref=null}], interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s): [a [(l:1 c:36)], b [(l:1 c:40), (l:1 c:68)], d [(l:1 c:72)]]",
				"a", 1, 36);

	}


	@Test
	public void simpleFromListType5ParseTest() {

		final String query = " SELECT * FROM third join (select x from sixth where m.issing > 0) as fourth on a = b " 
		+ " left outer join fifth on b = d ";
		
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
				
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={join={1={table={alias=null, table=third}}, 2={join=join, on={condition={left={column={name=a, table_ref=null}}, right={column={name=b, table_ref=null}}, operator==}}}, 3={table={alias=fourth, query={select={1={column={name=x, table_ref=null}}}, from={table={alias=null, table=sixth}}, where={condition={left={column={name=issing, table_ref=m}}, right={literal=0}, operator=>}}}}}, 4={join=leftouter, on={condition={left={column={name=b, table_ref=null}}, right={column={name=d, table_ref=null}}, operator==}}}, 5={table={alias=null, table=fifth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={x=[[@7,34:34='x',<391>,1:34]], *=[[@1,8:8='*',<291>,1:8]]}, query1={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{sixth={x=[[@7,34:34='x',<391>,1:34]]}, third={*=[[@1,8:8='*',<291>,1:8]]}, fifth={*=[[@1,8:8='*',<291>,1:8]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={*=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={third={*=[[@1,8:8='*',<291>,1:8]]}, fifth={*=[[@1,8:8='*',<291>,1:8]]}}, def_query0={query_dictionary={x=[[@7,34:34='x',<391>,1:34]], *=[[@1,8:8='*',<291>,1:8]]}, table_dictionary={sixth={x=[[@7,34:34='x',<391>,1:34]]}}, filters=[{name=issing, table_ref=m}], interface={x=[{name=x, table_ref=sixth}]}}, filters=[{name=a, table_ref=null}, {name=b, table_ref=null}, {name=d, table_ref=null}], interface={*=[{name=*, table_ref=*}]}, table_alias={fourth=query0}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"Source Table not found for Column 'issing' at (l:1 c:53). No alias or table called 'm'.",
				"issing", 1, 53);
		assertDiagnosticAtPosition(snippet, "UNRESOLVED_UNQUALIFIED_COLUMNS", ParseDiagnostic.Severity.ERROR,
				"Unresolved unqualified column reference(s): [a [(l:1 c:80)], b [(l:1 c:84), (l:1 c:112)], d [(l:1 c:116)]]",
				"a", 1, 80);
		assertFatalDiagnosticCount(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"Source Table not found for Column 'issing'",
				"issing",
				1);

	}


	@Test
	public void simpleFromListType6ParseTest() {

		final String query = " select x from sixth where m.issing > y";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
				
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=x, table_ref=null}}}, from={table={alias=null, table=sixth}}, where={condition={left={column={name=issing, table_ref=m}}, right={column={name=y, table_ref=null}}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[x]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={x=[[@1,8:8='x',<391>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{sixth={x=[[@1,8:8='x',<391>,1:8]], y=[[@9,38:38='y',<391>,1:38]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={x=[[@1,8:8='x',<391>,1:8]]}, table_dictionary={sixth={x=[[@1,8:8='x',<391>,1:8]], y=[[@9,38:38='y',<391>,1:38]]}}, filters=[{name=issing, table_ref=m}, {name=y, table_ref=sixth}], interface={x=[{name=x, table_ref=sixth}]}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(snippet, "QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"Source Table not found for Column 'issing' at (l:1 c:27). No alias or table called 'm'.",
				"issing", 1, 27);
	}


	@Test
	public void sameTableDifferentSchemaQualifiedReferencesV1() {
		String sql =  " Select  aaa.col1, bbb.col1 FROM sch1.aaa aaa join sch2.bbb bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=aaa}}, 2={column={name=col1, table_ref=bbb}}}, from={join={1={table={alias=aaa, schema=sch1, table=aaa}}, 2={join=join, on={condition={left={column={name=col1, table_ref=aaa}}, right={column={name=col2, table_ref=bbb}}, operator==}}}, 3={table={alias=bbb, schema=sch2, table=bbb}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{sch2.bbb={col2=[[@23,78:80='bbb',<391>,1:78]], col1=[[@5,19:21='bbb',<391>,1:19]]}, sch1.aaa={col1=[[@1,9:11='aaa',<391>,1:9], [@19,67:69='aaa',<391>,1:67]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col1=[[@3,13:16='col1',<391>,1:13], [@7,23:26='col1',<391>,1:23]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={col1=[[@3,13:16='col1',<391>,1:13], [@7,23:26='col1',<391>,1:23]]}, table_dictionary={sch2.bbb={col2=[[@23,78:80='bbb',<391>,1:78]], col1=[[@5,19:21='bbb',<391>,1:19]]}, sch1.aaa={col1=[[@1,9:11='aaa',<391>,1:9], [@19,67:69='aaa',<391>,1:67]]}}, filters=[{name=col1, table_ref=aaa}, {name=col2, table_ref=bbb}], interface={col1=[{name=col1, table_ref=bbb}]}, table_alias={aaa=sch1.aaa, bbb=sch2.bbb}}}",
				extractor.getSymbolTable().toString());

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(
				snippet,
				"DUPLICATE_INTERFACE_COLUMNS",
				"Duplicate interface columns defined: aaa.col1 at (l:1 c:13) and bbb.col1 at (l:1 c:23).",
				"aaa.col1,bbb.col1",
				1,
				13);
	}


	@Test
	public void sameTableDifferentSchemaQualifiedReferencesV2() {
		String sql =  " Select  aaa.col1, bbb.col2 FROM sch1.aaa aaa join sch2.bbb bbb on aaa.col1 = bbb.col2";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=aaa}}, 2={column={name=col2, table_ref=bbb}}}, from={join={1={table={alias=aaa, schema=sch1, table=aaa}}, 2={join=join, on={condition={left={column={name=col1, table_ref=aaa}}, right={column={name=col2, table_ref=bbb}}, operator==}}}, 3={table={alias=bbb, schema=sch2, table=bbb}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{sch2.bbb={col2=[[@5,19:21='bbb',<391>,1:19], [@23,78:80='bbb',<391>,1:78]]}, sch1.aaa={col1=[[@1,9:11='aaa',<391>,1:9], [@19,67:69='aaa',<391>,1:67]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col2=[[@7,23:26='col2',<391>,1:23]], col1=[[@3,13:16='col1',<391>,1:13]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={col2=[[@7,23:26='col2',<391>,1:23]], col1=[[@3,13:16='col1',<391>,1:13]]}, table_dictionary={sch2.bbb={col2=[[@5,19:21='bbb',<391>,1:19], [@23,78:80='bbb',<391>,1:78]]}, sch1.aaa={col1=[[@1,9:11='aaa',<391>,1:9], [@19,67:69='aaa',<391>,1:67]]}}, filters=[{name=col1, table_ref=aaa}, {name=col2, table_ref=bbb}], interface={col2=[{name=col2, table_ref=bbb}], col1=[{name=col1, table_ref=aaa}]}, table_alias={aaa=sch1.aaa, bbb=sch2.bbb}}}",
				extractor.getSymbolTable().toString());

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
	}


	@Test
	public void sameTableDifferentSchemaQualifiedReferencesV3() {
		String sql =  " Select  aaa.col1 FROM sch1.aaa aaa "
			+	"\n join (select col1 from sch2.bbb) bbb on aaa.col1 = bbb.col1";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=aaa}}}, from={join={1={table={alias=aaa, schema=sch1, table=aaa}}, 2={join=join, on={condition={left={column={name=col1, table_ref=aaa}}, right={column={name=col1, table_ref=bbb}}, operator==}}}, 3={table={alias=bbb, query={select={1={column={name=col1, table_ref=null}}}, from={table={schema=sch2, alias=null, table=bbb}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{sch2.bbb={col1=[[@12,51:54='col1',<391>,2:14]]}, sch1.aaa={col1=[[@1,9:11='aaa',<391>,1:9], [@20,78:80='aaa',<391>,2:41]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col1=[[@12,51:54='col1',<391>,2:14], [@24,89:91='bbb',<391>,2:52]]}, query1={col1=[[@3,13:16='col1',<391>,1:13]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={col1=[[@3,13:16='col1',<391>,1:13]]}, table_dictionary={sch1.aaa={col1=[[@1,9:11='aaa',<391>,1:9], [@20,78:80='aaa',<391>,2:41]]}}, def_query0={query_dictionary={col1=[[@12,51:54='col1',<391>,2:14], [@24,89:91='bbb',<391>,2:52]]}, table_dictionary={sch2.bbb={col1=[[@12,51:54='col1',<391>,2:14]]}}, interface={col1=[{name=col1, table_ref=sch2.bbb}]}}, filters=[{name=col1, table_ref=aaa}, {name=col1, table_ref=bbb}], interface={col1=[{name=col1, table_ref=aaa}]}, table_alias={aaa=sch1.aaa, bbb=query0}}}",
				extractor.getSymbolTable().toString());

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
	}


	@Test
	public void sameTableDifferentSchemaQualifiedReferencesV4() {
		String sql =  " Select  aaa.col1 FROM sch1.aaa aaa union select bbb.col1 from sch2.bbb bbb";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);

		Assert.assertEquals("AST is wrong", "{SQL={union={1={select={1={column={name=col1, table_ref=aaa}}}, from={table={alias=aaa, schema=sch1, table=aaa}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=col1, table_ref=bbb}}}, from={table={alias=bbb, schema=sch2, table=bbb}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{sch2.bbb={col1=[[@11,49:51='bbb',<391>,1:49]]}, sch1.aaa={col1=[[@1,9:11='aaa',<391>,1:9]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col1=[[@3,13:16='col1',<391>,1:13]]}, query1={col1=[[@13,53:56='col1',<391>,1:53]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_union2={def_query1={query_dictionary={col1=[[@13,53:56='col1',<391>,1:53]]}, table_dictionary={sch2.bbb={col1=[[@11,49:51='bbb',<391>,1:49]]}}, setop=UNION, interface={col1=[{name=col1, table_ref=bbb}]}, table_alias={bbb=sch2.bbb}}, def_query0={query_dictionary={col1=[[@3,13:16='col1',<391>,1:13]]}, table_dictionary={sch1.aaa={col1=[[@1,9:11='aaa',<391>,1:9]]}}, interface={col1=[{name=col1, table_ref=aaa}]}, table_alias={aaa=sch1.aaa}}, interface={col1=query_column}}}",
				extractor.getSymbolTable().toString());

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void sameTableDifferentSchemaQualifiedReferencesV4Except(){
		String sql =  " Select  aaa.col1 FROM sch1.aaa aaa except select bbb.col1 from sch2.bbb bbb";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);

		Assert.assertEquals("AST is wrong", "{SQL={union={1={select={1={column={name=col1, table_ref=aaa}}}, from={table={alias=aaa, schema=sch1, table=aaa}}}, 2={union={qualifier=null, operator=except}}, 3={select={1={column={name=col1, table_ref=bbb}}}, from={table={alias=bbb, schema=sch2, table=bbb}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{sch2.bbb={col1=[[@11,50:52='bbb',<391>,1:50]]}, sch1.aaa={col1=[[@1,9:11='aaa',<391>,1:9]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col1=[[@3,13:16='col1',<391>,1:13]]}, query1={col1=[[@13,54:57='col1',<391>,1:54]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_union2={def_query1={query_dictionary={col1=[[@13,54:57='col1',<391>,1:54]]}, table_dictionary={sch2.bbb={col1=[[@11,50:52='bbb',<391>,1:50]]}}, setop=EXCEPT, interface={col1=[{name=col1, table_ref=bbb}]}, table_alias={bbb=sch2.bbb}}, def_query0={query_dictionary={col1=[[@3,13:16='col1',<391>,1:13]]}, table_dictionary={sch1.aaa={col1=[[@1,9:11='aaa',<391>,1:9]]}}, interface={col1=[{name=col1, table_ref=aaa}]}, table_alias={aaa=sch1.aaa}}, interface={col1=query_column}}}",
				extractor.getSymbolTable().toString());

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
	}

	@Test
	public void simplifiedQualifiedWildcardOverUnionInMiddleLayerTest() {
		final String query = "select mid.id, mid.c1 "
				+ "\n from (select u.* from ("
				+ "\n select t1.id, t1.c1, t1.c2 from tab1 t1 "
				+ "\n union "
				+ "\n select t2.id, t2.c1, t2.c2 from tab2 t2"
				+ "\n ) u) mid "
				+ "\n where mid.id > 0";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=id, table_ref=mid}}, 2={column={name=c1, table_ref=mid}}}, from={table={alias=mid, query={select={1={column={name=*, table_ref=u}}}, from={table={alias=u, query={union={1={select={1={column={name=id, table_ref=t1}}, 2={column={name=c1, table_ref=t1}}, 3={column={name=c2, table_ref=t1}}}, from={table={alias=t1, table=tab1}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=id, table_ref=t2}}, 2={column={name=c1, table_ref=t2}}, 3={column={name=c2, table_ref=t2}}}, from={table={alias=t2, table=tab2}}}}}}}}}}, where={condition={left={column={name=id, table_ref=mid}}, right={literal=0}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[id, c1]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={id=[[@17,56:57='t1',<391>,3:8]], c1=[[@21,63:64='t1',<391>,3:15]], c2=[[@25,70:71='t1',<391>,3:22]]}, tab2={id=[[@33,106:107='t2',<391>,5:8]], c1=[[@37,113:114='t2',<391>,5:15]], c2=[[@41,120:121='t2',<391>,5:22]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{union2={*=[[@11,37:37='u',<391>,2:14]]}, query4={c1=[[@7,19:20='c1',<391>,1:19]], id=[[@3,11:12='id',<391>,1:11]]}, query0={id=[[@19,59:60='id',<391>,3:11]], c1=[[@23,66:67='c1',<391>,3:18]], c2=[[@27,73:74='c2',<391>,3:25]]}, query1={id=[[@35,109:110='id',<391>,5:11]], c1=[[@39,116:117='c1',<391>,5:18]], c2=[[@43,123:124='c2',<391>,5:25]]}, query3={*=[[@13,39:39='*',<291>,2:16]], c1=[[@5,15:17='mid',<391>,1:15]], id=[[@1,7:9='mid',<391>,1:7], [@52,157:159='mid',<391>,7:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query4={query_dictionary={id=[[@3,11:12='id',<391>,1:11]], c1=[[@7,19:20='c1',<391>,1:19]]}, filters=[{name=id, table_ref=mid}], interface={id=[{name=id, table_ref=mid}], c1=[{name=c1, table_ref=mid}]}, def_query3={def_union2={query_dictionary={*=[[@11,37:37='u',<391>,2:14]]}, def_query1={query_dictionary={id=[[@35,109:110='id',<391>,5:11]], c1=[[@39,116:117='c1',<391>,5:18]], c2=[[@43,123:124='c2',<391>,5:25]]}, table_dictionary={tab2={id=[[@33,106:107='t2',<391>,5:8]], c1=[[@37,113:114='t2',<391>,5:15]], c2=[[@41,120:121='t2',<391>,5:22]]}}, setop=UNION, interface={id=[{name=id, table_ref=t2}], c1=[{name=c1, table_ref=t2}], c2=[{name=c2, table_ref=t2}]}, table_alias={t2=tab2}}, def_query0={query_dictionary={id=[[@19,59:60='id',<391>,3:11]], c1=[[@23,66:67='c1',<391>,3:18]], c2=[[@27,73:74='c2',<391>,3:25]]}, table_dictionary={tab1={id=[[@17,56:57='t1',<391>,3:8]], c1=[[@21,63:64='t1',<391>,3:15]], c2=[[@25,70:71='t1',<391>,3:22]]}}, interface={id=[{name=id, table_ref=t1}], c1=[{name=c1, table_ref=t1}], c2=[{name=c2, table_ref=t1}]}, table_alias={t1=tab1}}, interface={id=query_column, c1=query_column, c2=query_column, *=wildcard}}, query_dictionary={*=[[@13,39:39='*',<291>,2:16]], id=[[@1,7:9='mid',<391>,1:7], [@52,157:159='mid',<391>,7:7]], c1=[[@5,15:17='mid',<391>,1:15]]}, interface={*=[{name=*, table_ref=u}]}, table_alias={u=union2}}, table_alias={mid=query3}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void simplifiedQualifiedWildcardOverExceptInMiddleLayerTest(){
		final String query = "select mid.id, mid.c1 "
				+ "\n from (select u.* from ("
				+ "\n select t1.id, t1.c1, t1.c2 from tab1 t1 "
				+ "\n except "
				+ "\n select t2.id, t2.c1, t2.c2 from tab2 t2"
				+ "\n ) u) mid "
				+ "\n where mid.id > 0";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=id, table_ref=mid}}, 2={column={name=c1, table_ref=mid}}}, from={table={alias=mid, query={select={1={column={name=*, table_ref=u}}}, from={table={alias=u, query={union={1={select={1={column={name=id, table_ref=t1}}, 2={column={name=c1, table_ref=t1}}, 3={column={name=c2, table_ref=t1}}}, from={table={alias=t1, table=tab1}}}, 2={union={qualifier=null, operator=except}}, 3={select={1={column={name=id, table_ref=t2}}, 2={column={name=c1, table_ref=t2}}, 3={column={name=c2, table_ref=t2}}}, from={table={alias=t2, table=tab2}}}}}}}}}}, where={condition={left={column={name=id, table_ref=mid}}, right={literal=0}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[id, c1]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={id=[[@17,56:57='t1',<391>,3:8]], c1=[[@21,63:64='t1',<391>,3:15]], c2=[[@25,70:71='t1',<391>,3:22]]}, tab2={id=[[@33,107:108='t2',<391>,5:8]], c1=[[@37,114:115='t2',<391>,5:15]], c2=[[@41,121:122='t2',<391>,5:22]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{union2={*=[[@11,37:37='u',<391>,2:14]]}, query4={c1=[[@7,19:20='c1',<391>,1:19]], id=[[@3,11:12='id',<391>,1:11]]}, query0={id=[[@19,59:60='id',<391>,3:11]], c1=[[@23,66:67='c1',<391>,3:18]], c2=[[@27,73:74='c2',<391>,3:25]]}, query1={id=[[@35,110:111='id',<391>,5:11]], c1=[[@39,117:118='c1',<391>,5:18]], c2=[[@43,124:125='c2',<391>,5:25]]}, query3={*=[[@13,39:39='*',<291>,2:16]], c1=[[@5,15:17='mid',<391>,1:15]], id=[[@1,7:9='mid',<391>,1:7], [@52,158:160='mid',<391>,7:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query4={query_dictionary={id=[[@3,11:12='id',<391>,1:11]], c1=[[@7,19:20='c1',<391>,1:19]]}, filters=[{name=id, table_ref=mid}], interface={id=[{name=id, table_ref=mid}], c1=[{name=c1, table_ref=mid}]}, def_query3={def_union2={query_dictionary={*=[[@11,37:37='u',<391>,2:14]]}, def_query1={query_dictionary={id=[[@35,110:111='id',<391>,5:11]], c1=[[@39,117:118='c1',<391>,5:18]], c2=[[@43,124:125='c2',<391>,5:25]]}, table_dictionary={tab2={id=[[@33,107:108='t2',<391>,5:8]], c1=[[@37,114:115='t2',<391>,5:15]], c2=[[@41,121:122='t2',<391>,5:22]]}}, setop=EXCEPT, interface={id=[{name=id, table_ref=t2}], c1=[{name=c1, table_ref=t2}], c2=[{name=c2, table_ref=t2}]}, table_alias={t2=tab2}}, def_query0={query_dictionary={id=[[@19,59:60='id',<391>,3:11]], c1=[[@23,66:67='c1',<391>,3:18]], c2=[[@27,73:74='c2',<391>,3:25]]}, table_dictionary={tab1={id=[[@17,56:57='t1',<391>,3:8]], c1=[[@21,63:64='t1',<391>,3:15]], c2=[[@25,70:71='t1',<391>,3:22]]}}, interface={id=[{name=id, table_ref=t1}], c1=[{name=c1, table_ref=t1}], c2=[{name=c2, table_ref=t1}]}, table_alias={t1=tab1}}, interface={id=query_column, c1=query_column, c2=query_column, *=wildcard}}, query_dictionary={*=[[@13,39:39='*',<291>,2:16]], id=[[@1,7:9='mid',<391>,1:7], [@52,158:160='mid',<391>,7:7]], c1=[[@5,15:17='mid',<391>,1:15]]}, interface={*=[{name=*, table_ref=u}]}, table_alias={u=union2}}, table_alias={mid=query3}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void simplifiedQualifiedWildcardOverIntersectionInMiddleLayerTest() {
		final String query = "select mid.id, mid.c1 "
				+ "\n from (select u.* from ("
				+ "\n select t1.id, t1.c1, t1.c2 from tab1 t1 "
				+ "\n intersect "
				+ "\n select t2.id, t2.c1, t2.c2 from tab2 t2"
				+ "\n ) u) mid "
				+ "\n where mid.id > 0";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=id, table_ref=mid}}, 2={column={name=c1, table_ref=mid}}}, from={table={alias=mid, query={select={1={column={name=*, table_ref=u}}}, from={table={alias=u, query={intersect={1={select={1={column={name=id, table_ref=t1}}, 2={column={name=c1, table_ref=t1}}, 3={column={name=c2, table_ref=t1}}}, from={table={alias=t1, table=tab1}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=id, table_ref=t2}}, 2={column={name=c1, table_ref=t2}}, 3={column={name=c2, table_ref=t2}}}, from={table={alias=t2, table=tab2}}}}}}}}}}, where={condition={left={column={name=id, table_ref=mid}}, right={literal=0}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[id, c1]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={id=[[@17,56:57='t1',<391>,3:8]], c1=[[@21,63:64='t1',<391>,3:15]], c2=[[@25,70:71='t1',<391>,3:22]]}, tab2={id=[[@33,110:111='t2',<391>,5:8]], c1=[[@37,117:118='t2',<391>,5:15]], c2=[[@41,124:125='t2',<391>,5:22]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{intersect2={*=[[@11,37:37='u',<391>,2:14]]}, query4={c1=[[@7,19:20='c1',<391>,1:19]], id=[[@3,11:12='id',<391>,1:11]]}, query0={id=[[@19,59:60='id',<391>,3:11]], c1=[[@23,66:67='c1',<391>,3:18]], c2=[[@27,73:74='c2',<391>,3:25]]}, query1={id=[[@35,113:114='id',<391>,5:11]], c1=[[@39,120:121='c1',<391>,5:18]], c2=[[@43,127:128='c2',<391>,5:25]]}, query3={*=[[@13,39:39='*',<291>,2:16]], c1=[[@5,15:17='mid',<391>,1:15]], id=[[@1,7:9='mid',<391>,1:7], [@52,161:163='mid',<391>,7:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query4={query_dictionary={id=[[@3,11:12='id',<391>,1:11]], c1=[[@7,19:20='c1',<391>,1:19]]}, filters=[{name=id, table_ref=mid}], interface={id=[{name=id, table_ref=mid}], c1=[{name=c1, table_ref=mid}]}, def_query3={query_dictionary={*=[[@13,39:39='*',<291>,2:16]], id=[[@1,7:9='mid',<391>,1:7], [@52,161:163='mid',<391>,7:7]], c1=[[@5,15:17='mid',<391>,1:15]]}, def_intersect2={query_dictionary={*=[[@11,37:37='u',<391>,2:14]]}, def_query1={query_dictionary={id=[[@35,113:114='id',<391>,5:11]], c1=[[@39,120:121='c1',<391>,5:18]], c2=[[@43,127:128='c2',<391>,5:25]]}, table_dictionary={tab2={id=[[@33,110:111='t2',<391>,5:8]], c1=[[@37,117:118='t2',<391>,5:15]], c2=[[@41,124:125='t2',<391>,5:22]]}}, setop=INTERSECTION, interface={id=[{name=id, table_ref=t2}], c1=[{name=c1, table_ref=t2}], c2=[{name=c2, table_ref=t2}]}, table_alias={t2=tab2}}, def_query0={query_dictionary={id=[[@19,59:60='id',<391>,3:11]], c1=[[@23,66:67='c1',<391>,3:18]], c2=[[@27,73:74='c2',<391>,3:25]]}, table_dictionary={tab1={id=[[@17,56:57='t1',<391>,3:8]], c1=[[@21,63:64='t1',<391>,3:15]], c2=[[@25,70:71='t1',<391>,3:22]]}}, interface={id=[{name=id, table_ref=t1}], c1=[{name=c1, table_ref=t1}], c2=[{name=c2, table_ref=t1}]}, table_alias={t1=tab1}}, interface={id=query_column, c1=query_column, c2=query_column, *=wildcard}}, interface={*=[{name=*, table_ref=u}]}, table_alias={u=intersect2}}, table_alias={mid=query3}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void simplifiedQualifiedWildcardOverExceptionInMiddleLayerTest() {
		final String query = "select mid.id, mid.c1 "
				+ "\n from (select u.* from ("
				+ "\n select t1.id, t1.c1, t1.c2 from tab1 t1 "
				+ "\n except "
				+ "\n select t2.id, t2.c1, t2.c2 from tab2 t2"
				+ "\n ) u) mid "
				+ "\n where mid.id > 0";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=id, table_ref=mid}}, 2={column={name=c1, table_ref=mid}}}, from={table={alias=mid, query={select={1={column={name=*, table_ref=u}}}, from={table={alias=u, query={union={1={select={1={column={name=id, table_ref=t1}}, 2={column={name=c1, table_ref=t1}}, 3={column={name=c2, table_ref=t1}}}, from={table={alias=t1, table=tab1}}}, 2={union={qualifier=null, operator=except}}, 3={select={1={column={name=id, table_ref=t2}}, 2={column={name=c1, table_ref=t2}}, 3={column={name=c2, table_ref=t2}}}, from={table={alias=t2, table=tab2}}}}}}}}}}, where={condition={left={column={name=id, table_ref=mid}}, right={literal=0}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[id, c1]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={id=[[@17,56:57='t1',<391>,3:8]], c1=[[@21,63:64='t1',<391>,3:15]], c2=[[@25,70:71='t1',<391>,3:22]]}, tab2={id=[[@33,107:108='t2',<391>,5:8]], c1=[[@37,114:115='t2',<391>,5:15]], c2=[[@41,121:122='t2',<391>,5:22]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{union2={*=[[@11,37:37='u',<391>,2:14]]}, query4={c1=[[@7,19:20='c1',<391>,1:19]], id=[[@3,11:12='id',<391>,1:11]]}, query0={id=[[@19,59:60='id',<391>,3:11]], c1=[[@23,66:67='c1',<391>,3:18]], c2=[[@27,73:74='c2',<391>,3:25]]}, query1={id=[[@35,110:111='id',<391>,5:11]], c1=[[@39,117:118='c1',<391>,5:18]], c2=[[@43,124:125='c2',<391>,5:25]]}, query3={*=[[@13,39:39='*',<291>,2:16]], c1=[[@5,15:17='mid',<391>,1:15]], id=[[@1,7:9='mid',<391>,1:7], [@52,158:160='mid',<391>,7:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query4={query_dictionary={id=[[@3,11:12='id',<391>,1:11]], c1=[[@7,19:20='c1',<391>,1:19]]}, filters=[{name=id, table_ref=mid}], interface={id=[{name=id, table_ref=mid}], c1=[{name=c1, table_ref=mid}]}, def_query3={def_union2={query_dictionary={*=[[@11,37:37='u',<391>,2:14]]}, def_query1={query_dictionary={id=[[@35,110:111='id',<391>,5:11]], c1=[[@39,117:118='c1',<391>,5:18]], c2=[[@43,124:125='c2',<391>,5:25]]}, table_dictionary={tab2={id=[[@33,107:108='t2',<391>,5:8]], c1=[[@37,114:115='t2',<391>,5:15]], c2=[[@41,121:122='t2',<391>,5:22]]}}, setop=EXCEPT, interface={id=[{name=id, table_ref=t2}], c1=[{name=c1, table_ref=t2}], c2=[{name=c2, table_ref=t2}]}, table_alias={t2=tab2}}, def_query0={query_dictionary={id=[[@19,59:60='id',<391>,3:11]], c1=[[@23,66:67='c1',<391>,3:18]], c2=[[@27,73:74='c2',<391>,3:25]]}, table_dictionary={tab1={id=[[@17,56:57='t1',<391>,3:8]], c1=[[@21,63:64='t1',<391>,3:15]], c2=[[@25,70:71='t1',<391>,3:22]]}}, interface={id=[{name=id, table_ref=t1}], c1=[{name=c1, table_ref=t1}], c2=[{name=c2, table_ref=t1}]}, table_alias={t1=tab1}}, interface={id=query_column, c1=query_column, c2=query_column, *=wildcard}}, query_dictionary={*=[[@13,39:39='*',<291>,2:16]], id=[[@1,7:9='mid',<391>,1:7], [@52,158:160='mid',<391>,7:7]], c1=[[@5,15:17='mid',<391>,1:15]]}, interface={*=[{name=*, table_ref=u}]}, table_alias={u=union2}}, table_alias={mid=query3}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void simplifiedQualifiedWildcardOverJoinInMiddleLayerTest() {
		final String query = "select mid.id, mid.c1 "
				+ "\n from (select u.* from ("
				+ "\n select t1.id, t1.d1, t1.d2 from tab1 t1 "
				+ "\n join tab2 t2 on t1.d3 = t2.c3"
				+ "\n ) u) mid "
				+ "\n where mid.id > 0";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=id, table_ref=mid}}, 2={column={name=c1, table_ref=mid}}}, from={table={alias=mid, query={select={1={column={name=*, table_ref=u}}}, from={table={alias=u, query={select={1={column={name=id, table_ref=t1}}, 2={column={name=d1, table_ref=t1}}, 3={column={name=d2, table_ref=t1}}}, from={join={1={table={alias=t1, table=tab1}}, 2={join=join, on={condition={left={column={name=d3, table_ref=t1}}, right={column={name=c3, table_ref=t2}}, operator==}}}, 3={table={alias=t2, table=tab2}}}}}}}}}}, where={condition={left={column={name=id, table_ref=mid}}, right={literal=0}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[id, c1]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={id=[[@17,56:57='t1',<391>,3:8]], d1=[[@21,63:64='t1',<391>,3:15]], d2=[[@25,70:71='t1',<391>,3:22]], d3=[[@35,107:108='t1',<391>,4:17]]}, tab2={c3=[[@39,115:116='t2',<391>,4:25]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@11,37:37='u',<391>,2:14]], id=[[@19,59:60='id',<391>,3:11]], d1=[[@23,66:67='d1',<391>,3:18]], d2=[[@27,73:74='d2',<391>,3:25]]}, query1={*=[[@13,39:39='*',<291>,2:16]], c1=[[@5,15:17='mid',<391>,1:15]], id=[[@1,7:9='mid',<391>,1:7], [@47,139:141='mid',<391>,6:7]]}, query2={c1=[[@7,19:20='c1',<391>,1:19]], id=[[@3,11:12='id',<391>,1:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={id=[[@3,11:12='id',<391>,1:11]], c1=[[@7,19:20='c1',<391>,1:19]]}, def_query1={query_dictionary={*=[[@13,39:39='*',<291>,2:16]], id=[[@1,7:9='mid',<391>,1:7], [@47,139:141='mid',<391>,6:7]], c1=[[@5,15:17='mid',<391>,1:15]]}, def_query0={query_dictionary={*=[[@11,37:37='u',<391>,2:14]], id=[[@19,59:60='id',<391>,3:11]], d1=[[@23,66:67='d1',<391>,3:18]], d2=[[@27,73:74='d2',<391>,3:25]]}, table_dictionary={tab1={id=[[@17,56:57='t1',<391>,3:8]], d1=[[@21,63:64='t1',<391>,3:15]], d2=[[@25,70:71='t1',<391>,3:22]], d3=[[@35,107:108='t1',<391>,4:17]]}, tab2={c3=[[@39,115:116='t2',<391>,4:25]]}}, filters=[{name=d3, table_ref=t1}, {name=c3, table_ref=t2}], interface={*=wildcard, id=[{name=id, table_ref=t1}], d1=[{name=d1, table_ref=t1}], d2=[{name=d2, table_ref=t1}]}, table_alias={t1=tab1, t2=tab2}}, interface={*=[{name=*, table_ref=u}]}, table_alias={u=query0}}, filters=[{name=id, table_ref=mid}], interface={id=[{name=id, table_ref=mid}], c1=[{name=c1, table_ref=mid}]}, table_alias={mid=query1}}}",
				extractor.getSymbolTable().toString());
	}



	@Test
	public void simplifiedqualifiedNestedColumnResolutionOverUnionsTest() {
		final String query = "with contact_streams_product_subproduct as (\n"
				+ "  select cs.contact_key, str.product, cs.first_marketing_activity_segment_id,\n"
				+ "         cs.latest_marketing_activity_segment_id, cs.first_marketing_activity_dt as first_contact_dt\n"
				+ "  from {{ ref('contact_streams') }} as cs join {{ ref('streams') }} as str on cs.stream_key = str.stream_key\n"
				+ "), unioned_first_latest_audience as (\n"
				+ "  select csps.contact_key, csps.product, csps.first_marketing_activity_segment_id as segment_id, csps.first_contact_dt from contact_streams_product_subproduct as csps\n"
				+ "  union\n"
				+ "  select csps.contact_key, csps.product, csps.latest_marketing_activity_segment_id as segment_id, csps.first_contact_dt from contact_streams_product_subproduct as csps\n"
				+ ")\n"
				+ "select ufla.contact_key, ufla.segment_id, ufla.product from unioned_first_latest_audience as ufla";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

	}

	@Test
	public void simplifiedqualifiedNestedColumnResolutionOverExceptsTest(){
		final String query = "with contact_streams_product_subproduct as (\n"
				+ "  select cs.contact_key, str.product, cs.first_marketing_activity_segment_id,\n"
				+ "         cs.latest_marketing_activity_segment_id, cs.first_marketing_activity_dt as first_contact_dt\n"
				+ "  from {{ ref('contact_streams') }} as cs join {{ ref('streams') }} as str on cs.stream_key = str.stream_key\n"
				+ "), unioned_first_latest_audience as (\n"
				+ "  select csps.contact_key, csps.product, csps.first_marketing_activity_segment_id as segment_id, csps.first_contact_dt from contact_streams_product_subproduct as csps\n"
				+ "  except\n"
				+ "  select csps.contact_key, csps.product, csps.latest_marketing_activity_segment_id as segment_id, csps.first_contact_dt from contact_streams_product_subproduct as csps\n"
				+ ")\n"
				+ "select ufla.contact_key, ufla.segment_id, ufla.product from unioned_first_latest_audience as ufla";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

	}
}
