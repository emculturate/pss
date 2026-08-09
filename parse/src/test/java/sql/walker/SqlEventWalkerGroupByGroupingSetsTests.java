package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import sql.SQLSelectParserParser;

/**
 * GROUP BY / ROLLUP / CUBE / GROUPING SETS AST shapes ({@code set}, {@code rollup}, {@code cube},
 * {@code grouping_sets}) and {@code grouped_by} clause harvesting.
 */
public class SqlEventWalkerGroupByGroupingSetsTests extends AbstractSqlParseEventWalkerTest {

	private void assertGroupByWalkerOutputs(SqlParseEventWalker extractor,
			String expectedAst, String expectedInterface, String expectedSubstitutions,
			String expectedTableDictionary, String expectedQueryColumnDictionary,
			String expectedSymbolTable) {
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
	public void groupByTwoColumnsCommaTable() {
		final String query = "SELECT a, b, SUM(c) AS s FROM tab1 GROUP BY a, b";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertGroupByWalkerOutputs(extractor,
				"{SQL={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={function={function_name=SUM, qualifier=null, parameters={column={name=c, table_ref=null}}}, alias=s}}, from={table={alias=null, table=tab1}}, groupby={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}}}",
				"[a, b, s]",
				"{}",
				"{tab1={a=[[@1,7:7='a',<392>,1:7], [@15,44:44='a',<392>,1:44]], b=[[@3,10:10='b',<392>,1:10], [@17,47:47='b',<392>,1:47]], c=[[@7,17:17='c',<392>,1:17]]}}",
				"{query0={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], s=[[@10,23:23='s',<392>,1:23]]}}",
				"{def_query0={query_dictionary={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], s=[[@10,23:23='s',<392>,1:23]]}, table_dictionary={tab1={a=[[@1,7:7='a',<392>,1:7], [@15,44:44='a',<392>,1:44]], b=[[@3,10:10='b',<392>,1:10], [@17,47:47='b',<392>,1:47]], c=[[@7,17:17='c',<392>,1:17]]}}, grouped_by=[{name=a, table_ref=tab1}, {name=b, table_ref=tab1}], interface={a=[{name=a, table_ref=tab1}], b=[{name=b, table_ref=tab1}], s=[{name=c, table_ref=tab1}]}}}");
	}

	@Test
	public void groupByParenthesizedPairTable() {
		final String query = "SELECT a, b, SUM(c) AS s FROM tab1 GROUP BY (a, b)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertGroupByWalkerOutputs(extractor,
				"{SQL={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={function={function_name=SUM, qualifier=null, parameters={column={name=c, table_ref=null}}}, alias=s}}, from={table={alias=null, table=tab1}}, groupby={set={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}}}}",
				"[a, b, s]",
				"{}",
				"{tab1={a=[[@1,7:7='a',<392>,1:7], [@16,45:45='a',<392>,1:45]], b=[[@3,10:10='b',<392>,1:10], [@18,48:48='b',<392>,1:48]], c=[[@7,17:17='c',<392>,1:17]]}}",
				"{query0={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], s=[[@10,23:23='s',<392>,1:23]]}}",
				"{def_query0={query_dictionary={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], s=[[@10,23:23='s',<392>,1:23]]}, table_dictionary={tab1={a=[[@1,7:7='a',<392>,1:7], [@16,45:45='a',<392>,1:45]], b=[[@3,10:10='b',<392>,1:10], [@18,48:48='b',<392>,1:48]], c=[[@7,17:17='c',<392>,1:17]]}}, grouped_by=[{name=a, table_ref=tab1}, {name=b, table_ref=tab1}], interface={a=[{name=a, table_ref=tab1}], b=[{name=b, table_ref=tab1}], s=[{name=c, table_ref=tab1}]}}}");
	}

	@Test
	public void groupByRollupTwoColumnsTable() {
		final String query = "SELECT a, b, SUM(c) AS s FROM tab1 GROUP BY ROLLUP(a, b)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertGroupByWalkerOutputs(extractor,
				"{SQL={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={function={function_name=SUM, qualifier=null, parameters={column={name=c, table_ref=null}}}, alias=s}}, from={table={alias=null, table=tab1}}, groupby={rollup={set={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}}}}}",
				"[a, b, s]",
				"{}",
				"{tab1={a=[[@1,7:7='a',<392>,1:7], [@17,51:51='a',<392>,1:51]], b=[[@3,10:10='b',<392>,1:10], [@19,54:54='b',<392>,1:54]], c=[[@7,17:17='c',<392>,1:17]]}}",
				"{query0={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], s=[[@10,23:23='s',<392>,1:23]]}}",
				"{def_query0={query_dictionary={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], s=[[@10,23:23='s',<392>,1:23]]}, table_dictionary={tab1={a=[[@1,7:7='a',<392>,1:7], [@17,51:51='a',<392>,1:51]], b=[[@3,10:10='b',<392>,1:10], [@19,54:54='b',<392>,1:54]], c=[[@7,17:17='c',<392>,1:17]]}}, grouped_by=[{name=a, table_ref=tab1}, {name=b, table_ref=tab1}], interface={a=[{name=a, table_ref=tab1}], b=[{name=b, table_ref=tab1}], s=[{name=c, table_ref=tab1}]}}}");
	}

	@Test
	public void groupByRollupCompositeAndSingleTable() {
		final String query = "SELECT a, b, c, SUM(d) AS s FROM tab1 GROUP BY ROLLUP((a, b), c)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertGroupByWalkerOutputs(extractor,
				"{SQL={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={column={name=c, table_ref=null}}, 4={function={function_name=SUM, qualifier=null, parameters={column={name=d, table_ref=null}}}, alias=s}}, from={table={alias=null, table=tab1}}, groupby={rollup={set={1={set={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}}, 2={column={name=c, table_ref=null}}}}}}}",
				"[a, b, c, s]",
				"{}",
				"{tab1={a=[[@1,7:7='a',<392>,1:7], [@20,55:55='a',<392>,1:55]], b=[[@3,10:10='b',<392>,1:10], [@22,58:58='b',<392>,1:58]], c=[[@5,13:13='c',<392>,1:13], [@25,62:62='c',<392>,1:62]], d=[[@9,20:20='d',<392>,1:20]]}}",
				"{query0={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], c=[[@5,13:13='c',<392>,1:13]], s=[[@12,26:26='s',<392>,1:26]]}}",
				"{def_query0={query_dictionary={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], c=[[@5,13:13='c',<392>,1:13]], s=[[@12,26:26='s',<392>,1:26]]}, table_dictionary={tab1={a=[[@1,7:7='a',<392>,1:7], [@20,55:55='a',<392>,1:55]], b=[[@3,10:10='b',<392>,1:10], [@22,58:58='b',<392>,1:58]], c=[[@5,13:13='c',<392>,1:13], [@25,62:62='c',<392>,1:62]], d=[[@9,20:20='d',<392>,1:20]]}}, grouped_by=[{name=a, table_ref=tab1}, {name=b, table_ref=tab1}, {name=c, table_ref=tab1}], interface={a=[{name=a, table_ref=tab1}], b=[{name=b, table_ref=tab1}], c=[{name=c, table_ref=tab1}], s=[{name=d, table_ref=tab1}]}}}");
	}

	@Test
	public void groupByCubeSingleColumnTable() {
		final String query = "SELECT a, SUM(b) AS s FROM tab1 GROUP BY CUBE(a)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertGroupByWalkerOutputs(extractor,
				"{SQL={select={1={column={name=a, table_ref=null}}, 2={function={function_name=SUM, qualifier=null, parameters={column={name=b, table_ref=null}}}, alias=s}}, from={table={alias=null, table=tab1}}, groupby={cube={set={1={column={name=a, table_ref=null}}}}}}}",
				"[a, s]",
				"{}",
				"{tab1={a=[[@1,7:7='a',<392>,1:7], [@15,46:46='a',<392>,1:46]], b=[[@5,14:14='b',<392>,1:14]]}}",
				"{query0={a=[[@1,7:7='a',<392>,1:7]], s=[[@8,20:20='s',<392>,1:20]]}}",
				"{def_query0={query_dictionary={a=[[@1,7:7='a',<392>,1:7]], s=[[@8,20:20='s',<392>,1:20]]}, table_dictionary={tab1={a=[[@1,7:7='a',<392>,1:7], [@15,46:46='a',<392>,1:46]], b=[[@5,14:14='b',<392>,1:14]]}}, grouped_by=[{name=a, table_ref=tab1}], interface={a=[{name=a, table_ref=tab1}], s=[{name=b, table_ref=tab1}]}}}");
	}

	@Test
	public void groupByCubeTwoColumnsTable() {
		final String query = "SELECT a, b, SUM(c) AS s FROM tab1 GROUP BY CUBE(a, b)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertGroupByWalkerOutputs(extractor,
				"{SQL={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={function={function_name=SUM, qualifier=null, parameters={column={name=c, table_ref=null}}}, alias=s}}, from={table={alias=null, table=tab1}}, groupby={cube={set={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}}}}}",
				"[a, b, s]",
				"{}",
				"{tab1={a=[[@1,7:7='a',<392>,1:7], [@17,49:49='a',<392>,1:49]], b=[[@3,10:10='b',<392>,1:10], [@19,52:52='b',<392>,1:52]], c=[[@7,17:17='c',<392>,1:17]]}}",
				"{query0={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], s=[[@10,23:23='s',<392>,1:23]]}}",
				"{def_query0={query_dictionary={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], s=[[@10,23:23='s',<392>,1:23]]}, table_dictionary={tab1={a=[[@1,7:7='a',<392>,1:7], [@17,49:49='a',<392>,1:49]], b=[[@3,10:10='b',<392>,1:10], [@19,52:52='b',<392>,1:52]], c=[[@7,17:17='c',<392>,1:17]]}}, grouped_by=[{name=a, table_ref=tab1}, {name=b, table_ref=tab1}], interface={a=[{name=a, table_ref=tab1}], b=[{name=b, table_ref=tab1}], s=[{name=c, table_ref=tab1}]}}}");
	}

	@Test
	public void groupByGroupingSetsTwoSetsTable() {
		final String query = "SELECT a, SUM(b) AS s FROM tab1 GROUP BY GROUPING SETS ((a), (a, b))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertGroupByWalkerOutputs(extractor,
				"{SQL={select={1={column={name=a, table_ref=null}}, 2={function={function_name=SUM, qualifier=null, parameters={column={name=b, table_ref=null}}}, alias=s}}, from={table={alias=null, table=tab1}}, groupby={grouping_sets={set={1={set={1={column={name=a, table_ref=null}}}}, 2={set={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}}}}}}}",
				"[a, s]",
				"{}",
				"{tab1={a=[[@1,7:7='a',<392>,1:7], [@17,57:57='a',<392>,1:57], [@21,62:62='a',<392>,1:62]], b=[[@5,14:14='b',<392>,1:14], [@23,65:65='b',<392>,1:65]]}}",
				"{query0={a=[[@1,7:7='a',<392>,1:7]], s=[[@8,20:20='s',<392>,1:20]]}}",
				"{def_query0={query_dictionary={a=[[@1,7:7='a',<392>,1:7]], s=[[@8,20:20='s',<392>,1:20]]}, table_dictionary={tab1={a=[[@1,7:7='a',<392>,1:7], [@17,57:57='a',<392>,1:57], [@21,62:62='a',<392>,1:62]], b=[[@5,14:14='b',<392>,1:14], [@23,65:65='b',<392>,1:65]]}}, grouped_by=[{name=a, table_ref=tab1}, {name=b, table_ref=null}], interface={a=[{name=a, table_ref=tab1}], s=[{name=b, table_ref=tab1}]}}}");
	}

	@Test
	public void groupByGroupingSetsThreeOperandsTable() {
		final String query =
				"SELECT a, b, c, SUM(d) AS s FROM tab1 GROUP BY GROUPING SETS ((a), (b), (c))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertGroupByWalkerOutputs(extractor,
				"{SQL={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={column={name=c, table_ref=null}}, 4={function={function_name=SUM, qualifier=null, parameters={column={name=d, table_ref=null}}}, alias=s}}, from={table={alias=null, table=tab1}}, groupby={grouping_sets={set={1={set={1={column={name=a, table_ref=null}}}}, 2={set={1={column={name=b, table_ref=null}}}}, 3={set={1={column={name=c, table_ref=null}}}}}}}}}",
				"[a, b, c, s]",
				"{}",
				"{tab1={a=[[@1,7:7='a',<392>,1:7], [@21,63:63='a',<392>,1:63]], b=[[@3,10:10='b',<392>,1:10], [@25,68:68='b',<392>,1:68]], c=[[@5,13:13='c',<392>,1:13], [@29,73:73='c',<392>,1:73]], d=[[@9,20:20='d',<392>,1:20]]}}",
				"{query0={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], c=[[@5,13:13='c',<392>,1:13]], s=[[@12,26:26='s',<392>,1:26]]}}",
				"{def_query0={query_dictionary={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], c=[[@5,13:13='c',<392>,1:13]], s=[[@12,26:26='s',<392>,1:26]]}, table_dictionary={tab1={a=[[@1,7:7='a',<392>,1:7], [@21,63:63='a',<392>,1:63]], b=[[@3,10:10='b',<392>,1:10], [@25,68:68='b',<392>,1:68]], c=[[@5,13:13='c',<392>,1:13], [@29,73:73='c',<392>,1:73]], d=[[@9,20:20='d',<392>,1:20]]}}, grouped_by=[{name=a, table_ref=tab1}, {name=b, table_ref=tab1}, {name=c, table_ref=tab1}], interface={a=[{name=a, table_ref=tab1}], b=[{name=b, table_ref=tab1}], c=[{name=c, table_ref=tab1}], s=[{name=d, table_ref=tab1}]}}}");
	}

	@Test
	public void groupByRollupThreeOperandsTable() {
		final String query = "SELECT a, b, c, SUM(d) AS s FROM tab1 GROUP BY ROLLUP((a, b), c, d)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertGroupByWalkerOutputs(extractor,
				"{SQL={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={column={name=c, table_ref=null}}, 4={function={function_name=SUM, qualifier=null, parameters={column={name=d, table_ref=null}}}, alias=s}}, from={table={alias=null, table=tab1}}, groupby={rollup={set={1={set={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}}, 2={column={name=c, table_ref=null}}, 3={column={name=d, table_ref=null}}}}}}}",
				"[a, b, c, s]",
				"{}",
				"{tab1={a=[[@1,7:7='a',<392>,1:7], [@20,55:55='a',<392>,1:55]], b=[[@3,10:10='b',<392>,1:10], [@22,58:58='b',<392>,1:58]], c=[[@5,13:13='c',<392>,1:13], [@25,62:62='c',<392>,1:62]], d=[[@9,20:20='d',<392>,1:20], [@27,65:65='d',<392>,1:65]]}}",
				"{query0={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], c=[[@5,13:13='c',<392>,1:13]], s=[[@12,26:26='s',<392>,1:26]]}}",
				"{def_query0={query_dictionary={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], c=[[@5,13:13='c',<392>,1:13]], s=[[@12,26:26='s',<392>,1:26]]}, table_dictionary={tab1={a=[[@1,7:7='a',<392>,1:7], [@20,55:55='a',<392>,1:55]], b=[[@3,10:10='b',<392>,1:10], [@22,58:58='b',<392>,1:58]], c=[[@5,13:13='c',<392>,1:13], [@25,62:62='c',<392>,1:62]], d=[[@9,20:20='d',<392>,1:20], [@27,65:65='d',<392>,1:65]]}}, grouped_by=[{name=a, table_ref=tab1}, {name=b, table_ref=tab1}, {name=c, table_ref=tab1}, {name=d, table_ref=null}], interface={a=[{name=a, table_ref=tab1}], b=[{name=b, table_ref=tab1}], c=[{name=c, table_ref=tab1}], s=[{name=d, table_ref=tab1}]}}}");
	}

	@Test
	public void groupByMixedCommaAndParenthesizedSetTable() {
		final String query = "SELECT a, b, c, SUM(d) AS s FROM tab1 GROUP BY a, (b, c)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertGroupByWalkerOutputs(extractor,
				"{SQL={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={column={name=c, table_ref=null}}, 4={function={function_name=SUM, qualifier=null, parameters={column={name=d, table_ref=null}}}, alias=s}}, from={table={alias=null, table=tab1}}, groupby={1={column={name=a, table_ref=null}}, 2={set={1={column={name=b, table_ref=null}}, 2={column={name=c, table_ref=null}}}}}}}",
				"[a, b, c, s]",
				"{}",
				"{tab1={a=[[@1,7:7='a',<392>,1:7], [@17,47:47='a',<392>,1:47]], b=[[@3,10:10='b',<392>,1:10], [@20,51:51='b',<392>,1:51]], c=[[@5,13:13='c',<392>,1:13], [@22,54:54='c',<392>,1:54]], d=[[@9,20:20='d',<392>,1:20]]}}",
				"{query0={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], c=[[@5,13:13='c',<392>,1:13]], s=[[@12,26:26='s',<392>,1:26]]}}",
				"{def_query0={query_dictionary={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], c=[[@5,13:13='c',<392>,1:13]], s=[[@12,26:26='s',<392>,1:26]]}, table_dictionary={tab1={a=[[@1,7:7='a',<392>,1:7], [@17,47:47='a',<392>,1:47]], b=[[@3,10:10='b',<392>,1:10], [@20,51:51='b',<392>,1:51]], c=[[@5,13:13='c',<392>,1:13], [@22,54:54='c',<392>,1:54]], d=[[@9,20:20='d',<392>,1:20]]}}, grouped_by=[{name=a, table_ref=tab1}, {name=b, table_ref=tab1}, {name=c, table_ref=tab1}], interface={a=[{name=a, table_ref=tab1}], b=[{name=b, table_ref=tab1}], c=[{name=c, table_ref=tab1}], s=[{name=d, table_ref=tab1}]}}}");
	}

	@Test
	public void groupByRollupFourOperandsTable() {
		final String query = "SELECT a, b, c, d, SUM(e) AS s FROM tab1 GROUP BY ROLLUP(a, b, c, d)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertGroupByWalkerOutputs(extractor,
				"{SQL={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={column={name=c, table_ref=null}}, 4={column={name=d, table_ref=null}}, 5={function={function_name=SUM, qualifier=null, parameters={column={name=e, table_ref=null}}}, alias=s}}, from={table={alias=null, table=tab1}}, groupby={rollup={set={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={column={name=c, table_ref=null}}, 4={column={name=d, table_ref=null}}}}}}}",
				"[a, b, c, s, d]",
				"{}",
				"{tab1={a=[[@1,7:7='a',<392>,1:7], [@21,57:57='a',<392>,1:57]], b=[[@3,10:10='b',<392>,1:10], [@23,60:60='b',<392>,1:60]], c=[[@5,13:13='c',<392>,1:13], [@25,63:63='c',<392>,1:63]], d=[[@7,16:16='d',<392>,1:16], [@27,66:66='d',<392>,1:66]], e=[[@11,23:23='e',<392>,1:23]]}}",
				"{query0={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], c=[[@5,13:13='c',<392>,1:13]], s=[[@14,29:29='s',<392>,1:29]], d=[[@7,16:16='d',<392>,1:16]]}}",
				"{def_query0={query_dictionary={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], c=[[@5,13:13='c',<392>,1:13]], s=[[@14,29:29='s',<392>,1:29]], d=[[@7,16:16='d',<392>,1:16]]}, table_dictionary={tab1={a=[[@1,7:7='a',<392>,1:7], [@21,57:57='a',<392>,1:57]], b=[[@3,10:10='b',<392>,1:10], [@23,60:60='b',<392>,1:60]], c=[[@5,13:13='c',<392>,1:13], [@25,63:63='c',<392>,1:63]], d=[[@7,16:16='d',<392>,1:16], [@27,66:66='d',<392>,1:66]], e=[[@11,23:23='e',<392>,1:23]]}}, grouped_by=[{name=a, table_ref=tab1}, {name=b, table_ref=tab1}, {name=c, table_ref=tab1}, {name=d, table_ref=tab1}], interface={a=[{name=a, table_ref=tab1}], b=[{name=b, table_ref=tab1}], c=[{name=c, table_ref=tab1}], s=[{name=e, table_ref=tab1}], d=[{name=d, table_ref=tab1}]}}}");
	}

	@Test
	public void groupByCubeParenthesizedPairTable() {
		final String query = "SELECT a, b, SUM(c) AS s FROM tab1 GROUP BY CUBE((a, b))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertGroupByWalkerOutputs(extractor,
				"{SQL={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={function={function_name=SUM, qualifier=null, parameters={column={name=c, table_ref=null}}}, alias=s}}, from={table={alias=null, table=tab1}}, groupby={cube={set={1={set={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}}}}}}}",
				"[a, b, s]",
				"{}",
				"{tab1={a=[[@1,7:7='a',<392>,1:7], [@18,50:50='a',<392>,1:50]], b=[[@3,10:10='b',<392>,1:10], [@20,53:53='b',<392>,1:53]], c=[[@7,17:17='c',<392>,1:17]]}}",
				"{query0={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], s=[[@10,23:23='s',<392>,1:23]]}}",
				"{def_query0={query_dictionary={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], s=[[@10,23:23='s',<392>,1:23]]}, table_dictionary={tab1={a=[[@1,7:7='a',<392>,1:7], [@18,50:50='a',<392>,1:50]], b=[[@3,10:10='b',<392>,1:10], [@20,53:53='b',<392>,1:53]], c=[[@7,17:17='c',<392>,1:17]]}}, grouped_by=[{name=a, table_ref=tab1}, {name=b, table_ref=tab1}], interface={a=[{name=a, table_ref=tab1}], b=[{name=b, table_ref=tab1}], s=[{name=c, table_ref=tab1}]}}}");
	}

	@Test
	public void groupByGroupingSetsFourSetsTable() {
		final String query =
				"SELECT a, b, SUM(c) AS s FROM tab1 GROUP BY GROUPING SETS ((a), (b), (a, b), (b, a))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertGroupByWalkerOutputs(extractor,
				"{SQL={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={function={function_name=SUM, qualifier=null, parameters={column={name=c, table_ref=null}}}, alias=s}}, from={table={alias=null, table=tab1}}, groupby={grouping_sets={set={1={set={1={column={name=a, table_ref=null}}}}, 2={set={1={column={name=b, table_ref=null}}}}, 3={set={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}}, 4={set={1={column={name=b, table_ref=null}}, 2={column={name=a, table_ref=null}}}}}}}}}",
				"[a, b, s]",
				"{}",
				"{tab1={a=[[@1,7:7='a',<392>,1:7], [@19,60:60='a',<392>,1:60], [@27,70:70='a',<392>,1:70], [@35,81:81='a',<392>,1:81]], b=[[@3,10:10='b',<392>,1:10], [@23,65:65='b',<392>,1:65], [@29,73:73='b',<392>,1:73], [@33,78:78='b',<392>,1:78]], c=[[@7,17:17='c',<392>,1:17]]}}",
				"{query0={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], s=[[@10,23:23='s',<392>,1:23]]}}",
				"{def_query0={query_dictionary={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], s=[[@10,23:23='s',<392>,1:23]]}, table_dictionary={tab1={a=[[@1,7:7='a',<392>,1:7], [@19,60:60='a',<392>,1:60], [@27,70:70='a',<392>,1:70], [@35,81:81='a',<392>,1:81]], b=[[@3,10:10='b',<392>,1:10], [@23,65:65='b',<392>,1:65], [@29,73:73='b',<392>,1:73], [@33,78:78='b',<392>,1:78]], c=[[@7,17:17='c',<392>,1:17]]}}, grouped_by=[{name=a, table_ref=tab1}, {name=b, table_ref=tab1}], interface={a=[{name=a, table_ref=tab1}], b=[{name=b, table_ref=tab1}], s=[{name=c, table_ref=tab1}]}}}");
	}

	@Test
	public void groupByTwoColumnsSubquery() {
		final String query =
				"SELECT x, y, SUM(z) AS s FROM (SELECT a AS x, b AS y, c AS z FROM tab1) q GROUP BY x, y";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertGroupByWalkerOutputs(extractor,
				"{SQL={select={1={column={name=x, table_ref=null}}, 2={column={name=y, table_ref=null}}, 3={function={function_name=SUM, qualifier=null, parameters={column={name=z, table_ref=null}}}, alias=s}}, from={table={alias=q, query={select={1={column={name=a, table_ref=null}, alias=x}, 2={column={name=b, table_ref=null}, alias=y}, 3={column={name=c, table_ref=null}, alias=z}}, from={table={alias=null, table=tab1}}}}}, groupby={1={column={name=x, table_ref=null}}, 2={column={name=y, table_ref=null}}}}}",
				"[s, x, y]",
				"{}",
				"{tab1={a=[[@14,38:38='a',<392>,1:38]], b=[[@18,46:46='b',<392>,1:46]], c=[[@22,54:54='c',<392>,1:54]]}}",
				"{query0={x=[[@16,43:43='x',<392>,1:43], [@1,7:7='x',<392>,1:7], [@31,83:83='x',<392>,1:83]], y=[[@20,51:51='y',<392>,1:51], [@3,10:10='y',<392>,1:10], [@33,86:86='y',<392>,1:86]], z=[[@24,59:59='z',<392>,1:59], [@7,17:17='z',<392>,1:17]]}, query1={x=[[@1,7:7='x',<392>,1:7]], y=[[@3,10:10='y',<392>,1:10]], s=[[@10,23:23='s',<392>,1:23]]}}",
				"{def_query1={query_dictionary={s=[[@10,23:23='s',<392>,1:23]], x=[[@1,7:7='x',<392>,1:7]], y=[[@3,10:10='y',<392>,1:10]]}, grouped_by=[{name=x, table_ref=query0}, {name=y, table_ref=query0}], def_query0={query_dictionary={x=[[@16,43:43='x',<392>,1:43], [@1,7:7='x',<392>,1:7], [@31,83:83='x',<392>,1:83]], y=[[@20,51:51='y',<392>,1:51], [@3,10:10='y',<392>,1:10], [@33,86:86='y',<392>,1:86]], z=[[@24,59:59='z',<392>,1:59], [@7,17:17='z',<392>,1:17]]}, table_dictionary={tab1={a=[[@14,38:38='a',<392>,1:38]], b=[[@18,46:46='b',<392>,1:46]], c=[[@22,54:54='c',<392>,1:54]]}}, interface={x=[{name=a, table_ref=tab1}], y=[{name=b, table_ref=tab1}], z=[{name=c, table_ref=tab1}]}}, interface={s=[{name=z, table_ref=query0}], x=[{name=x, table_ref=query0}], y=[{name=y, table_ref=query0}]}, table_alias={q=query0}}}");
	}

	@Test
	public void groupByParenthesizedPairSubquery() {
		final String query =
				"SELECT x, y, SUM(z) AS s FROM (SELECT a AS x, b AS y, c AS z FROM tab1) q GROUP BY (x, y)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertGroupByWalkerOutputs(extractor,
				"{SQL={select={1={column={name=x, table_ref=null}}, 2={column={name=y, table_ref=null}}, 3={function={function_name=SUM, qualifier=null, parameters={column={name=z, table_ref=null}}}, alias=s}}, from={table={alias=q, query={select={1={column={name=a, table_ref=null}, alias=x}, 2={column={name=b, table_ref=null}, alias=y}, 3={column={name=c, table_ref=null}, alias=z}}, from={table={alias=null, table=tab1}}}}}, groupby={set={1={column={name=x, table_ref=null}}, 2={column={name=y, table_ref=null}}}}}}",
				"[s, x, y]",
				"{}",
				"{tab1={a=[[@14,38:38='a',<392>,1:38]], b=[[@18,46:46='b',<392>,1:46]], c=[[@22,54:54='c',<392>,1:54]]}}",
				"{query0={x=[[@16,43:43='x',<392>,1:43], [@1,7:7='x',<392>,1:7], [@32,84:84='x',<392>,1:84]], y=[[@20,51:51='y',<392>,1:51], [@3,10:10='y',<392>,1:10], [@34,87:87='y',<392>,1:87]], z=[[@24,59:59='z',<392>,1:59], [@7,17:17='z',<392>,1:17]]}, query1={x=[[@1,7:7='x',<392>,1:7]], y=[[@3,10:10='y',<392>,1:10]], s=[[@10,23:23='s',<392>,1:23]]}}",
				"{def_query1={query_dictionary={s=[[@10,23:23='s',<392>,1:23]], x=[[@1,7:7='x',<392>,1:7]], y=[[@3,10:10='y',<392>,1:10]]}, grouped_by=[{name=x, table_ref=query0}, {name=y, table_ref=query0}], def_query0={query_dictionary={x=[[@16,43:43='x',<392>,1:43], [@1,7:7='x',<392>,1:7], [@32,84:84='x',<392>,1:84]], y=[[@20,51:51='y',<392>,1:51], [@3,10:10='y',<392>,1:10], [@34,87:87='y',<392>,1:87]], z=[[@24,59:59='z',<392>,1:59], [@7,17:17='z',<392>,1:17]]}, table_dictionary={tab1={a=[[@14,38:38='a',<392>,1:38]], b=[[@18,46:46='b',<392>,1:46]], c=[[@22,54:54='c',<392>,1:54]]}}, interface={x=[{name=a, table_ref=tab1}], y=[{name=b, table_ref=tab1}], z=[{name=c, table_ref=tab1}]}}, interface={s=[{name=z, table_ref=query0}], x=[{name=x, table_ref=query0}], y=[{name=y, table_ref=query0}]}, table_alias={q=query0}}}");
	}

	@Test
	public void groupByRollupTwoColumnsSubquery() {
		final String query =
				"SELECT x, y, SUM(z) AS s FROM (SELECT a AS x, b AS y, c AS z FROM tab1) q GROUP BY ROLLUP(x, y)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertGroupByWalkerOutputs(extractor,
				"{SQL={select={1={column={name=x, table_ref=null}}, 2={column={name=y, table_ref=null}}, 3={function={function_name=SUM, qualifier=null, parameters={column={name=z, table_ref=null}}}, alias=s}}, from={table={alias=q, query={select={1={column={name=a, table_ref=null}, alias=x}, 2={column={name=b, table_ref=null}, alias=y}, 3={column={name=c, table_ref=null}, alias=z}}, from={table={alias=null, table=tab1}}}}}, groupby={rollup={set={1={column={name=x, table_ref=null}}, 2={column={name=y, table_ref=null}}}}}}}",
				"[s, x, y]",
				"{}",
				"{tab1={a=[[@14,38:38='a',<392>,1:38]], b=[[@18,46:46='b',<392>,1:46]], c=[[@22,54:54='c',<392>,1:54]]}}",
				"{query0={x=[[@16,43:43='x',<392>,1:43], [@1,7:7='x',<392>,1:7], [@33,90:90='x',<392>,1:90]], y=[[@20,51:51='y',<392>,1:51], [@3,10:10='y',<392>,1:10], [@35,93:93='y',<392>,1:93]], z=[[@24,59:59='z',<392>,1:59], [@7,17:17='z',<392>,1:17]]}, query1={x=[[@1,7:7='x',<392>,1:7]], y=[[@3,10:10='y',<392>,1:10]], s=[[@10,23:23='s',<392>,1:23]]}}",
				"{def_query1={query_dictionary={s=[[@10,23:23='s',<392>,1:23]], x=[[@1,7:7='x',<392>,1:7]], y=[[@3,10:10='y',<392>,1:10]]}, grouped_by=[{name=x, table_ref=query0}, {name=y, table_ref=query0}], def_query0={query_dictionary={x=[[@16,43:43='x',<392>,1:43], [@1,7:7='x',<392>,1:7], [@33,90:90='x',<392>,1:90]], y=[[@20,51:51='y',<392>,1:51], [@3,10:10='y',<392>,1:10], [@35,93:93='y',<392>,1:93]], z=[[@24,59:59='z',<392>,1:59], [@7,17:17='z',<392>,1:17]]}, table_dictionary={tab1={a=[[@14,38:38='a',<392>,1:38]], b=[[@18,46:46='b',<392>,1:46]], c=[[@22,54:54='c',<392>,1:54]]}}, interface={x=[{name=a, table_ref=tab1}], y=[{name=b, table_ref=tab1}], z=[{name=c, table_ref=tab1}]}}, interface={s=[{name=z, table_ref=query0}], x=[{name=x, table_ref=query0}], y=[{name=y, table_ref=query0}]}, table_alias={q=query0}}}");
	}

	@Test
	public void groupByRollupCompositeSubquery() {
		final String query =
				"SELECT x, y, w, SUM(z) AS s FROM (SELECT a AS x, b AS y, c AS w, d AS z FROM tab1) q GROUP BY ROLLUP((x, y), w)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertGroupByWalkerOutputs(extractor,
				"{SQL={select={1={column={name=x, table_ref=null}}, 2={column={name=y, table_ref=null}}, 3={column={name=w, table_ref=null}}, 4={function={function_name=SUM, qualifier=null, parameters={column={name=z, table_ref=null}}}, alias=s}}, from={table={alias=q, query={select={1={column={name=a, table_ref=null}, alias=x}, 2={column={name=b, table_ref=null}, alias=y}, 3={column={name=c, table_ref=null}, alias=w}, 4={column={name=d, table_ref=null}, alias=z}}, from={table={alias=null, table=tab1}}}}}, groupby={rollup={set={1={set={1={column={name=x, table_ref=null}}, 2={column={name=y, table_ref=null}}}}, 2={column={name=w, table_ref=null}}}}}}}",
				"[s, w, x, y]",
				"{}",
				"{tab1={a=[[@16,41:41='a',<392>,1:41]], b=[[@20,49:49='b',<392>,1:49]], c=[[@24,57:57='c',<392>,1:57]], d=[[@28,65:65='d',<392>,1:65]]}}",
				"{query0={x=[[@18,46:46='x',<392>,1:46], [@1,7:7='x',<392>,1:7], [@40,102:102='x',<392>,1:102]], y=[[@22,54:54='y',<392>,1:54], [@3,10:10='y',<392>,1:10], [@42,105:105='y',<392>,1:105]], z=[[@30,70:70='z',<392>,1:70], [@9,20:20='z',<392>,1:20]], w=[[@26,62:62='w',<392>,1:62], [@5,13:13='w',<392>,1:13], [@45,109:109='w',<392>,1:109]]}, query1={x=[[@1,7:7='x',<392>,1:7]], y=[[@3,10:10='y',<392>,1:10]], s=[[@12,26:26='s',<392>,1:26]], w=[[@5,13:13='w',<392>,1:13]]}}",
				"{def_query1={query_dictionary={s=[[@12,26:26='s',<392>,1:26]], w=[[@5,13:13='w',<392>,1:13]], x=[[@1,7:7='x',<392>,1:7]], y=[[@3,10:10='y',<392>,1:10]]}, grouped_by=[{name=x, table_ref=query0}, {name=y, table_ref=query0}, {name=w, table_ref=query0}], def_query0={query_dictionary={w=[[@26,62:62='w',<392>,1:62], [@5,13:13='w',<392>,1:13], [@45,109:109='w',<392>,1:109]], x=[[@18,46:46='x',<392>,1:46], [@1,7:7='x',<392>,1:7], [@40,102:102='x',<392>,1:102]], y=[[@22,54:54='y',<392>,1:54], [@3,10:10='y',<392>,1:10], [@42,105:105='y',<392>,1:105]], z=[[@30,70:70='z',<392>,1:70], [@9,20:20='z',<392>,1:20]]}, table_dictionary={tab1={a=[[@16,41:41='a',<392>,1:41]], b=[[@20,49:49='b',<392>,1:49]], c=[[@24,57:57='c',<392>,1:57]], d=[[@28,65:65='d',<392>,1:65]]}}, interface={w=[{name=c, table_ref=tab1}], x=[{name=a, table_ref=tab1}], y=[{name=b, table_ref=tab1}], z=[{name=d, table_ref=tab1}]}}, interface={s=[{name=z, table_ref=query0}], w=[{name=w, table_ref=query0}], x=[{name=x, table_ref=query0}], y=[{name=y, table_ref=query0}]}, table_alias={q=query0}}}");
	}

	@Test
	public void groupByCubeSubquery() {
		final String query =
				"SELECT x, SUM(y) AS s FROM (SELECT a AS x, b AS y FROM tab1) q GROUP BY CUBE(x)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertGroupByWalkerOutputs(extractor,
				"{SQL={select={1={column={name=x, table_ref=null}}, 2={function={function_name=SUM, qualifier=null, parameters={column={name=y, table_ref=null}}}, alias=s}}, from={table={alias=q, query={select={1={column={name=a, table_ref=null}, alias=x}, 2={column={name=b, table_ref=null}, alias=y}}, from={table={alias=null, table=tab1}}}}}, groupby={cube={set={1={column={name=x, table_ref=null}}}}}}}",
				"[s, x]",
				"{}",
				"{tab1={a=[[@12,35:35='a',<392>,1:35]], b=[[@16,43:43='b',<392>,1:43]]}}",
				"{query0={x=[[@14,40:40='x',<392>,1:40], [@1,7:7='x',<392>,1:7], [@27,77:77='x',<392>,1:77]], y=[[@18,48:48='y',<392>,1:48], [@5,14:14='y',<392>,1:14]]}, query1={x=[[@1,7:7='x',<392>,1:7]], s=[[@8,20:20='s',<392>,1:20]]}}",
				"{def_query1={query_dictionary={s=[[@8,20:20='s',<392>,1:20]], x=[[@1,7:7='x',<392>,1:7]]}, grouped_by=[{name=x, table_ref=query0}], def_query0={query_dictionary={x=[[@14,40:40='x',<392>,1:40], [@1,7:7='x',<392>,1:7], [@27,77:77='x',<392>,1:77]], y=[[@18,48:48='y',<392>,1:48], [@5,14:14='y',<392>,1:14]]}, table_dictionary={tab1={a=[[@12,35:35='a',<392>,1:35]], b=[[@16,43:43='b',<392>,1:43]]}}, interface={x=[{name=a, table_ref=tab1}], y=[{name=b, table_ref=tab1}]}}, interface={s=[{name=y, table_ref=query0}], x=[{name=x, table_ref=query0}]}, table_alias={q=query0}}}");
	}

	@Test
	public void groupByGroupingSetsSubquery() {
		final String query =
				"SELECT x, y, SUM(z) AS s FROM (SELECT a AS x, b AS y, c AS z FROM tab1) q GROUP BY GROUPING SETS ((x, y), (x))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertGroupByWalkerOutputs(extractor,
				"{SQL={select={1={column={name=x, table_ref=null}}, 2={column={name=y, table_ref=null}}, 3={function={function_name=SUM, qualifier=null, parameters={column={name=z, table_ref=null}}}, alias=s}}, from={table={alias=q, query={select={1={column={name=a, table_ref=null}, alias=x}, 2={column={name=b, table_ref=null}, alias=y}, 3={column={name=c, table_ref=null}, alias=z}}, from={table={alias=null, table=tab1}}}}}, groupby={grouping_sets={set={1={set={1={column={name=x, table_ref=null}}, 2={column={name=y, table_ref=null}}}}, 2={set={1={column={name=x, table_ref=null}}}}}}}}}",
				"[s, x, y]",
				"{}",
				"{tab1={a=[[@14,38:38='a',<392>,1:38]], b=[[@18,46:46='b',<392>,1:46]], c=[[@22,54:54='c',<392>,1:54]]}}",
				"{query0={x=[[@16,43:43='x',<392>,1:43], [@1,7:7='x',<392>,1:7], [@35,99:99='x',<392>,1:99], [@41,107:107='x',<392>,1:107]], y=[[@20,51:51='y',<392>,1:51], [@3,10:10='y',<392>,1:10], [@37,102:102='y',<392>,1:102]], z=[[@24,59:59='z',<392>,1:59], [@7,17:17='z',<392>,1:17]]}, query1={x=[[@1,7:7='x',<392>,1:7]], y=[[@3,10:10='y',<392>,1:10]], s=[[@10,23:23='s',<392>,1:23]]}}",
				"{def_query1={query_dictionary={s=[[@10,23:23='s',<392>,1:23]], x=[[@1,7:7='x',<392>,1:7]], y=[[@3,10:10='y',<392>,1:10]]}, grouped_by=[{name=x, table_ref=query0}, {name=y, table_ref=query0}], def_query0={query_dictionary={x=[[@16,43:43='x',<392>,1:43], [@1,7:7='x',<392>,1:7], [@35,99:99='x',<392>,1:99], [@41,107:107='x',<392>,1:107]], y=[[@20,51:51='y',<392>,1:51], [@3,10:10='y',<392>,1:10], [@37,102:102='y',<392>,1:102]], z=[[@24,59:59='z',<392>,1:59], [@7,17:17='z',<392>,1:17]]}, table_dictionary={tab1={a=[[@14,38:38='a',<392>,1:38]], b=[[@18,46:46='b',<392>,1:46]], c=[[@22,54:54='c',<392>,1:54]]}}, interface={x=[{name=a, table_ref=tab1}], y=[{name=b, table_ref=tab1}], z=[{name=c, table_ref=tab1}]}}, interface={s=[{name=z, table_ref=query0}], x=[{name=x, table_ref=query0}], y=[{name=y, table_ref=query0}]}, table_alias={q=query0}}}");
	}

	@Test
	public void groupByRollupThreeOperandsSubquery() {
		final String query =
				"SELECT p, q, r, SUM(s) AS tot FROM (SELECT a AS p, b AS q, c AS r, d AS s FROM tab1) v GROUP BY ROLLUP((p, q), r, s)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertGroupByWalkerOutputs(extractor,
				"{SQL={select={1={column={name=p, table_ref=null}}, 2={column={name=q, table_ref=null}}, 3={column={name=r, table_ref=null}}, 4={function={function_name=SUM, qualifier=null, parameters={column={name=s, table_ref=null}}}, alias=tot}}, from={table={alias=v, query={select={1={column={name=a, table_ref=null}, alias=p}, 2={column={name=b, table_ref=null}, alias=q}, 3={column={name=c, table_ref=null}, alias=r}, 4={column={name=d, table_ref=null}, alias=s}}, from={table={alias=null, table=tab1}}}}}, groupby={rollup={set={1={set={1={column={name=p, table_ref=null}}, 2={column={name=q, table_ref=null}}}}, 2={column={name=r, table_ref=null}}, 3={column={name=s, table_ref=null}}}}}}}",
				"[p, q, r, tot]",
				"{}",
				"{tab1={a=[[@16,43:43='a',<392>,1:43]], b=[[@20,51:51='b',<392>,1:51]], c=[[@24,59:59='c',<392>,1:59]], d=[[@28,67:67='d',<392>,1:67]]}}",
				"{query0={p=[[@18,48:48='p',<392>,1:48], [@1,7:7='p',<392>,1:7], [@40,104:104='p',<392>,1:104]], q=[[@22,56:56='q',<392>,1:56], [@3,10:10='q',<392>,1:10], [@42,107:107='q',<392>,1:107]], r=[[@26,64:64='r',<392>,1:64], [@5,13:13='r',<392>,1:13], [@45,111:111='r',<392>,1:111]], s=[[@30,72:72='s',<392>,1:72], [@9,20:20='s',<392>,1:20], [@47,114:114='s',<392>,1:114]]}, query1={p=[[@1,7:7='p',<392>,1:7]], tot=[[@12,26:28='tot',<392>,1:26]], q=[[@3,10:10='q',<392>,1:10]], r=[[@5,13:13='r',<392>,1:13]]}}",
				"{def_query1={query_dictionary={p=[[@1,7:7='p',<392>,1:7]], q=[[@3,10:10='q',<392>,1:10]], r=[[@5,13:13='r',<392>,1:13]], tot=[[@12,26:28='tot',<392>,1:26]]}, grouped_by=[{name=p, table_ref=query0}, {name=q, table_ref=query0}, {name=r, table_ref=query0}, {name=s, table_ref=query0}], def_query0={query_dictionary={p=[[@18,48:48='p',<392>,1:48], [@1,7:7='p',<392>,1:7], [@40,104:104='p',<392>,1:104]], q=[[@22,56:56='q',<392>,1:56], [@3,10:10='q',<392>,1:10], [@42,107:107='q',<392>,1:107]], r=[[@26,64:64='r',<392>,1:64], [@5,13:13='r',<392>,1:13], [@45,111:111='r',<392>,1:111]], s=[[@30,72:72='s',<392>,1:72], [@9,20:20='s',<392>,1:20], [@47,114:114='s',<392>,1:114]]}, table_dictionary={tab1={a=[[@16,43:43='a',<392>,1:43]], b=[[@20,51:51='b',<392>,1:51]], c=[[@24,59:59='c',<392>,1:59]], d=[[@28,67:67='d',<392>,1:67]]}}, interface={p=[{name=a, table_ref=tab1}], q=[{name=b, table_ref=tab1}], r=[{name=c, table_ref=tab1}], s=[{name=d, table_ref=tab1}]}}, interface={p=[{name=p, table_ref=query0}], q=[{name=q, table_ref=query0}], r=[{name=r, table_ref=query0}], tot=[{name=s, table_ref=query0}]}, table_alias={v=query0}}}");
	}

	@Test
	public void groupByMixedCommaParenSubquery() {
		final String query =
				"SELECT p, q, r, SUM(s) AS tot FROM (SELECT a AS p, b AS q, c AS r, d AS s FROM tab1) v GROUP BY p, (q, r)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertGroupByWalkerOutputs(extractor,
				"{SQL={select={1={column={name=p, table_ref=null}}, 2={column={name=q, table_ref=null}}, 3={column={name=r, table_ref=null}}, 4={function={function_name=SUM, qualifier=null, parameters={column={name=s, table_ref=null}}}, alias=tot}}, from={table={alias=v, query={select={1={column={name=a, table_ref=null}, alias=p}, 2={column={name=b, table_ref=null}, alias=q}, 3={column={name=c, table_ref=null}, alias=r}, 4={column={name=d, table_ref=null}, alias=s}}, from={table={alias=null, table=tab1}}}}}, groupby={1={column={name=p, table_ref=null}}, 2={set={1={column={name=q, table_ref=null}}, 2={column={name=r, table_ref=null}}}}}}}",
				"[p, q, r, tot]",
				"{}",
				"{tab1={a=[[@16,43:43='a',<392>,1:43]], b=[[@20,51:51='b',<392>,1:51]], c=[[@24,59:59='c',<392>,1:59]], d=[[@28,67:67='d',<392>,1:67]]}}",
				"{query0={p=[[@18,48:48='p',<392>,1:48], [@1,7:7='p',<392>,1:7], [@37,96:96='p',<392>,1:96]], q=[[@22,56:56='q',<392>,1:56], [@3,10:10='q',<392>,1:10], [@40,100:100='q',<392>,1:100]], r=[[@26,64:64='r',<392>,1:64], [@5,13:13='r',<392>,1:13], [@42,103:103='r',<392>,1:103]], s=[[@30,72:72='s',<392>,1:72], [@9,20:20='s',<392>,1:20]]}, query1={p=[[@1,7:7='p',<392>,1:7]], tot=[[@12,26:28='tot',<392>,1:26]], q=[[@3,10:10='q',<392>,1:10]], r=[[@5,13:13='r',<392>,1:13]]}}",
				"{def_query1={query_dictionary={p=[[@1,7:7='p',<392>,1:7]], q=[[@3,10:10='q',<392>,1:10]], r=[[@5,13:13='r',<392>,1:13]], tot=[[@12,26:28='tot',<392>,1:26]]}, grouped_by=[{name=p, table_ref=query0}, {name=q, table_ref=query0}, {name=r, table_ref=query0}], def_query0={query_dictionary={p=[[@18,48:48='p',<392>,1:48], [@1,7:7='p',<392>,1:7], [@37,96:96='p',<392>,1:96]], q=[[@22,56:56='q',<392>,1:56], [@3,10:10='q',<392>,1:10], [@40,100:100='q',<392>,1:100]], r=[[@26,64:64='r',<392>,1:64], [@5,13:13='r',<392>,1:13], [@42,103:103='r',<392>,1:103]], s=[[@30,72:72='s',<392>,1:72], [@9,20:20='s',<392>,1:20]]}, table_dictionary={tab1={a=[[@16,43:43='a',<392>,1:43]], b=[[@20,51:51='b',<392>,1:51]], c=[[@24,59:59='c',<392>,1:59]], d=[[@28,67:67='d',<392>,1:67]]}}, interface={p=[{name=a, table_ref=tab1}], q=[{name=b, table_ref=tab1}], r=[{name=c, table_ref=tab1}], s=[{name=d, table_ref=tab1}]}}, interface={p=[{name=p, table_ref=query0}], q=[{name=q, table_ref=query0}], r=[{name=r, table_ref=query0}], tot=[{name=s, table_ref=query0}]}, table_alias={v=query0}}}");
	}

	@Test
	public void groupByGroupingSetsFourSetsSubquery() {
		final String query =
				"SELECT x, y, SUM(z) AS s FROM (SELECT a AS x, b AS y, c AS z FROM tab1) q GROUP BY GROUPING SETS ((x), (y), (x, y), (y, x))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertGroupByWalkerOutputs(extractor,
				"{SQL={select={1={column={name=x, table_ref=null}}, 2={column={name=y, table_ref=null}}, 3={function={function_name=SUM, qualifier=null, parameters={column={name=z, table_ref=null}}}, alias=s}}, from={table={alias=q, query={select={1={column={name=a, table_ref=null}, alias=x}, 2={column={name=b, table_ref=null}, alias=y}, 3={column={name=c, table_ref=null}, alias=z}}, from={table={alias=null, table=tab1}}}}}, groupby={grouping_sets={set={1={set={1={column={name=x, table_ref=null}}}}, 2={set={1={column={name=y, table_ref=null}}}}, 3={set={1={column={name=x, table_ref=null}}, 2={column={name=y, table_ref=null}}}}, 4={set={1={column={name=y, table_ref=null}}, 2={column={name=x, table_ref=null}}}}}}}}}",
				"[s, x, y]",
				"{}",
				"{tab1={a=[[@14,38:38='a',<392>,1:38]], b=[[@18,46:46='b',<392>,1:46]], c=[[@22,54:54='c',<392>,1:54]]}}",
				"{query0={x=[[@16,43:43='x',<392>,1:43], [@1,7:7='x',<392>,1:7], [@35,99:99='x',<392>,1:99], [@43,109:109='x',<392>,1:109], [@51,120:120='x',<392>,1:120]], y=[[@20,51:51='y',<392>,1:51], [@3,10:10='y',<392>,1:10], [@39,104:104='y',<392>,1:104], [@45,112:112='y',<392>,1:112], [@49,117:117='y',<392>,1:117]], z=[[@24,59:59='z',<392>,1:59], [@7,17:17='z',<392>,1:17]]}, query1={x=[[@1,7:7='x',<392>,1:7]], y=[[@3,10:10='y',<392>,1:10]], s=[[@10,23:23='s',<392>,1:23]]}}",
				"{def_query1={query_dictionary={s=[[@10,23:23='s',<392>,1:23]], x=[[@1,7:7='x',<392>,1:7]], y=[[@3,10:10='y',<392>,1:10]]}, grouped_by=[{name=x, table_ref=query0}, {name=y, table_ref=query0}], def_query0={query_dictionary={x=[[@16,43:43='x',<392>,1:43], [@1,7:7='x',<392>,1:7], [@35,99:99='x',<392>,1:99], [@43,109:109='x',<392>,1:109], [@51,120:120='x',<392>,1:120]], y=[[@20,51:51='y',<392>,1:51], [@3,10:10='y',<392>,1:10], [@39,104:104='y',<392>,1:104], [@45,112:112='y',<392>,1:112], [@49,117:117='y',<392>,1:117]], z=[[@24,59:59='z',<392>,1:59], [@7,17:17='z',<392>,1:17]]}, table_dictionary={tab1={a=[[@14,38:38='a',<392>,1:38]], b=[[@18,46:46='b',<392>,1:46]], c=[[@22,54:54='c',<392>,1:54]]}}, interface={x=[{name=a, table_ref=tab1}], y=[{name=b, table_ref=tab1}], z=[{name=c, table_ref=tab1}]}}, interface={s=[{name=z, table_ref=query0}], x=[{name=x, table_ref=query0}], y=[{name=y, table_ref=query0}]}, table_alias={q=query0}}}");
	}

	/** Snowflake-style {@code GROUP BY ALL} — non-aggregated select columns implied; AST records {@code option=ALL}. */
	@Test
	public void groupByAllTable() {
		final String query = "SELECT a, b, SUM(c) AS s FROM tab1 GROUP BY ALL";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertGroupByWalkerOutputs(extractor,
				"{SQL={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={function={function_name=SUM, qualifier=null, parameters={column={name=c, table_ref=null}}}, alias=s}}, from={table={alias=null, table=tab1}}, groupby={option=ALL}}}",
				"[a, b, s]",
				"{}",
				"{tab1={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], c=[[@7,17:17='c',<392>,1:17]]}}",
				"{query0={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], s=[[@10,23:23='s',<392>,1:23]]}}",
				"{def_query0={query_dictionary={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], s=[[@10,23:23='s',<392>,1:23]]}, table_dictionary={tab1={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], c=[[@7,17:17='c',<392>,1:17]]}}, grouped_by=[], interface={a=[{name=a, table_ref=tab1}], b=[{name=b, table_ref=tab1}], s=[{name=c, table_ref=tab1}]}}}");
	}

	/** Postgres-style {@code GROUP BY DISTINCT} with explicit column list. */
	@Test
	public void groupByDistinctTwoColumnsTable() {
		final String query = "SELECT a, b, SUM(c) AS s FROM tab1 GROUP BY DISTINCT a, b";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertGroupByWalkerOutputs(extractor,
				"{SQL={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={function={function_name=SUM, qualifier=null, parameters={column={name=c, table_ref=null}}}, alias=s}}, from={table={alias=null, table=tab1}}, groupby={set={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, option=DISTINCT}}}",
				"[a, b, s]",
				"{}",
				"{tab1={a=[[@1,7:7='a',<392>,1:7], [@16,53:53='a',<392>,1:53]], b=[[@3,10:10='b',<392>,1:10], [@18,56:56='b',<392>,1:56]], c=[[@7,17:17='c',<392>,1:17]]}}",
				"{query0={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], s=[[@10,23:23='s',<392>,1:23]]}}",
				"{def_query0={query_dictionary={a=[[@1,7:7='a',<392>,1:7]], b=[[@3,10:10='b',<392>,1:10]], s=[[@10,23:23='s',<392>,1:23]]}, table_dictionary={tab1={a=[[@1,7:7='a',<392>,1:7], [@16,53:53='a',<392>,1:53]], b=[[@3,10:10='b',<392>,1:10], [@18,56:56='b',<392>,1:56]], c=[[@7,17:17='c',<392>,1:17]]}}, grouped_by=[{name=a, table_ref=tab1}, {name=b, table_ref=tab1}], interface={a=[{name=a, table_ref=tab1}], b=[{name=b, table_ref=tab1}], s=[{name=c, table_ref=tab1}]}}}");
	}

	/** Postgres-style {@code GROUP BY DISTINCT} over columns from a subquery in {@code FROM}. */
	@Test
	public void groupByDistinctTwoColumnsSubquery() {
		final String query =
				"SELECT x, y, SUM(z) AS s FROM (SELECT a AS x, b AS y, c AS z FROM tab1) q GROUP BY DISTINCT x, y";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertGroupByWalkerOutputs(extractor,
				"{SQL={select={1={column={name=x, table_ref=null}}, 2={column={name=y, table_ref=null}}, 3={function={function_name=SUM, qualifier=null, parameters={column={name=z, table_ref=null}}}, alias=s}}, from={table={alias=q, query={select={1={column={name=a, table_ref=null}, alias=x}, 2={column={name=b, table_ref=null}, alias=y}, 3={column={name=c, table_ref=null}, alias=z}}, from={table={alias=null, table=tab1}}}}}, groupby={set={1={column={name=x, table_ref=null}}, 2={column={name=y, table_ref=null}}}, option=DISTINCT}}}",
				"[s, x, y]",
				"{}",
				"{tab1={a=[[@14,38:38='a',<392>,1:38]], b=[[@18,46:46='b',<392>,1:46]], c=[[@22,54:54='c',<392>,1:54]]}}",
				"{query0={x=[[@16,43:43='x',<392>,1:43], [@1,7:7='x',<392>,1:7], [@32,92:92='x',<392>,1:92]], y=[[@20,51:51='y',<392>,1:51], [@3,10:10='y',<392>,1:10], [@34,95:95='y',<392>,1:95]], z=[[@24,59:59='z',<392>,1:59], [@7,17:17='z',<392>,1:17]]}, query1={x=[[@1,7:7='x',<392>,1:7]], y=[[@3,10:10='y',<392>,1:10]], s=[[@10,23:23='s',<392>,1:23]]}}",
				"{def_query1={query_dictionary={s=[[@10,23:23='s',<392>,1:23]], x=[[@1,7:7='x',<392>,1:7]], y=[[@3,10:10='y',<392>,1:10]]}, grouped_by=[{name=x, table_ref=query0}, {name=y, table_ref=query0}], def_query0={query_dictionary={x=[[@16,43:43='x',<392>,1:43], [@1,7:7='x',<392>,1:7], [@32,92:92='x',<392>,1:92]], y=[[@20,51:51='y',<392>,1:51], [@3,10:10='y',<392>,1:10], [@34,95:95='y',<392>,1:95]], z=[[@24,59:59='z',<392>,1:59], [@7,17:17='z',<392>,1:17]]}, table_dictionary={tab1={a=[[@14,38:38='a',<392>,1:38]], b=[[@18,46:46='b',<392>,1:46]], c=[[@22,54:54='c',<392>,1:54]]}}, interface={x=[{name=a, table_ref=tab1}], y=[{name=b, table_ref=tab1}], z=[{name=c, table_ref=tab1}]}}, interface={s=[{name=z, table_ref=query0}], x=[{name=x, table_ref=query0}], y=[{name=y, table_ref=query0}]}, table_alias={q=query0}}}");
	}

}
