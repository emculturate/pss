package sql.walker;

import java.util.Arrays;

import org.antlr.v4.runtime.tree.Trees;
import org.junit.Assert;
import org.junit.Test;

import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

public class SqlEventWalkerSnowflakeArrayHigherOrderTests extends AbstractSqlParseEventWalkerTest {

	private void assertExactDiagnostics(SqlParseEventWalker extractor, ParseDiagnostic... expected) {
		Assert.assertEquals("Diagnostics are wrong",
				Arrays.asList(expected), extractor.getSnippet().getParserDiagnosticList());
	}

	private SqlParseEventWalker runQuery(String query) {
		final SQLSelectParserParser parser = parse(query);
		return runParsertest(query, parser);
	}

	private SqlParseEventWalker runDedicatedFunctionQuery(String query, int ruleIndex) {
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Expected dedicated higher-order routing", 1,
				Trees.findAllRuleNodes(tree, ruleIndex).size());
		return runAnyParsertest(query, parser, tree, true);
	}

	private void assertOutputs(SqlParseEventWalker extractor, String ast, String outputInterface,
			String substitutions, String tableDictionary, String queryDictionary, String symbolTable) {
		Assert.assertEquals("AST is wrong", ast, extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", outputInterface, extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", substitutions, extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", tableDictionary, extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", queryDictionary, extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", symbolTable, extractor.getSymbolTable().toString());
	}

	@Test
	public void arC029_filterSelectPlacementV0Test() {
		final String query = "SELECT a.id, filter(a.tags, x -> x > a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@25,86:86='a',<459>,1:86]], threshold=[[@15,37:37='a',<459>,1:37]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,86:86='a',<459>,1:86]], threshold=[[@15,37:37='a',<459>,1:37]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterWherePlacementV0Test() {
		final String query = "SELECT a.id, a.label FROM events a WHERE filter(a.tags, x -> x > a.threshold) IS NOT NULL AND a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:41) is recognized as Snowflake-specific grammar.",
						1, 41, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=a}}}, from={table={alias=a, table=events}}, where={and={1={condition={left={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[id, label]",
				"{}",
				"{events={active=[[@30,94:94='a',<459>,1:94]], threshold=[[@22,65:65='a',<459>,1:65]], id=[[@1,7:7='a',<459>,1:7]], label=[[@5,13:13='a',<459>,1:13]], tags=[[@14,48:48='a',<459>,1:48]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={events={active=[[@30,94:94='a',<459>,1:94]], threshold=[[@22,65:65='a',<459>,1:65]], id=[[@1,7:7='a',<459>,1:7]], label=[[@5,13:13='a',<459>,1:13]], tags=[[@14,48:48='a',<459>,1:48]]}}, filters=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterJoinPlacementV0Test() {
		final String query = "SELECT a.id, b.label FROM events a JOIN labels b ON filter(a.tags, x -> x > a.threshold) IS NOT NULL AND a.id = b.event_id";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:52) is recognized as Snowflake-specific grammar.",
						1, 52, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={and={1={condition={left={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=id, table_ref=a}}, right={column={name=event_id, table_ref=b}}, operator==}}}}}, 3={table={alias=b, table=labels}}}}}}",
				"[id, label]",
				"{}",
				"{events={threshold=[[@25,76:76='a',<459>,1:76]], id=[[@1,7:7='a',<459>,1:7], [@33,105:105='a',<459>,1:105]], tags=[[@17,59:59='a',<459>,1:59]]}, labels={event_id=[[@37,112:112='b',<459>,1:112]], label=[[@5,13:13='b',<459>,1:13]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={events={threshold=[[@25,76:76='a',<459>,1:76]], id=[[@1,7:7='a',<459>,1:7], [@33,105:105='a',<459>,1:105]], tags=[[@17,59:59='a',<459>,1:59]]}, labels={event_id=[[@37,112:112='b',<459>,1:112]], label=[[@5,13:13='b',<459>,1:13]]}}, filters=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}, {name=id, table_ref=a}, {name=event_id, table_ref=b}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=labels}}}");
	}

	@Test
	public void arC029_filterHavingPlacementV0Test() {
		final String query = "SELECT a.id, count(*) AS total FROM events a GROUP BY a.id, a.tags, a.threshold HAVING filter(a.tags, x -> x > a.threshold) IS NOT NULL";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:87) is recognized as Snowflake-specific grammar.",
						1, 87, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}, alias=total}}, having={condition={left={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}}, operator=IS NOT NULL}}, from={table={alias=a, table=events}}, groupby={1={column={name=id, table_ref=a}}, 2={column={name=tags, table_ref=a}}, 3={column={name=threshold, table_ref=a}}}}}",
				"[total, id]",
				"{}",
				"{events={threshold=[[@24,68:68='a',<459>,1:68], [@38,111:111='a',<459>,1:111]], *=[[@7,19:19='*',<291>,1:19]], id=[[@1,7:7='a',<459>,1:7], [@16,54:54='a',<459>,1:54]], tags=[[@20,60:60='a',<459>,1:60], [@30,94:94='a',<459>,1:94]]}}",
				"{query0={total=[[@10,25:29='total',<459>,1:25]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={total=[[@10,25:29='total',<459>,1:25]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={*=[[@7,19:19='*',<291>,1:19]], threshold=[[@24,68:68='a',<459>,1:68], [@38,111:111='a',<459>,1:111]], id=[[@1,7:7='a',<459>,1:7], [@16,54:54='a',<459>,1:54]], tags=[[@20,60:60='a',<459>,1:60], [@30,94:94='a',<459>,1:94]]}}, grouped_by=[{name=id, table_ref=a}, {name=tags, table_ref=a}, {name=threshold, table_ref=a}], filters=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], interface={total=[], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterQualifyPlacementV0Test() {
		final String query = "SELECT a.id, row_number() over (partition by a.group_id order by a.id) AS rn FROM events a QUALIFY filter(a.tags, x -> x > a.threshold) IS NOT NULL AND rn <= 5";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:99) is recognized as Snowflake-specific grammar.",
						1, 99, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={alias=rn, window_function={over={partition_by={1={column={name=group_id, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=id, table_ref=a}}, sort_order=ASC}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=a, table=events}}, qualify={and={1={condition={left={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=rn, table_ref=null}}, right={literal=5}, operator=<=}}}}}}",
				"[id, rn]",
				"{}",
				"{events={group_id=[[@12,45:45='a',<459>,1:45]], threshold=[[@37,123:123='a',<459>,1:123]], id=[[@1,7:7='a',<459>,1:7], [@17,65:65='a',<459>,1:65]], tags=[[@29,106:106='a',<459>,1:106]]}}",
				"{query0={rn=[[@22,74:75='rn',<459>,1:74], [@45,152:153='rn',<459>,1:152]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={window_ordered_by=[{name=id, table_ref=a}], query_dictionary={id=[[@3,9:10='id',<459>,1:9]], rn=[[@22,74:75='rn',<459>,1:74], [@45,152:153='rn',<459>,1:152]]}, table_dictionary={events={group_id=[[@12,45:45='a',<459>,1:45]], threshold=[[@37,123:123='a',<459>,1:123]], id=[[@1,7:7='a',<459>,1:7], [@17,65:65='a',<459>,1:65]], tags=[[@29,106:106='a',<459>,1:106]]}}, window_partition_by=[{name=group_id, table_ref=a}], filters=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}, {name=rn, table_ref=query0}], interface={id=[{name=id, table_ref=a}], rn=[{name=group_id, table_ref=a}, {name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterOrderByPlacementV0Test() {
		final String query = "SELECT a.id FROM events a WHERE a.active = 1 ORDER BY filter(a.tags, x -> x > a.threshold), a.id";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:54) is recognized as Snowflake-specific grammar.",
						1, 54, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, orderby={1={null_order=null, predicand={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=id, table_ref=a}}, sort_order=ASC}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id]",
				"{}",
				"{events={active=[[@8,32:32='a',<459>,1:32]], threshold=[[@25,78:78='a',<459>,1:78]], id=[[@1,7:7='a',<459>,1:7], [@30,92:92='a',<459>,1:92]], tags=[[@17,61:61='a',<459>,1:61]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@8,32:32='a',<459>,1:32]], threshold=[[@25,78:78='a',<459>,1:78]], id=[[@1,7:7='a',<459>,1:7], [@30,92:92='a',<459>,1:92]], tags=[[@17,61:61='a',<459>,1:61]]}}, ordered_by=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}, {name=id, table_ref=a}], filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterValuesPlacementV0Test() {
		final String query = "VALUES (7, filter([1, NULL, 3], x -> x > 2))";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:18) is recognized as Snowflake-specific grammar.",
						1, 18, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={values={matrix={1={row={1={literal=7}, 2={function={function_name=filter, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={null_literal=null}, 3={literal=3}}}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={literal=2}, operator=>}}}}}}}}}}}}}",
				"[$1, $2]",
				"{}",
				"{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[], $2=[]}}}");
	}

	@Test
	public void arC029_filterInsertPlacementV0Test() {
		final String query = "INSERT INTO results (id, result_value) SELECT a.id, filter(a.tags, x -> x > a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:52) is recognized as Snowflake-specific grammar.",
						1, 52, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}}, target_table={table={alias=null, table=results}}, columns={1={column={name=id, table_ref=null}}, 2={column={name=result_value, table_ref=null}}}}}}",
				"[result_value, id]",
				"{}",
				"{results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, events={active=[[@33,125:125='a',<459>,1:125]], threshold=[[@23,76:76='a',<459>,1:76]], id=[[@9,46:46='a',<459>,1:46]], tags=[[@15,59:59='a',<459>,1:59]]}}",
				"{query0={result_value=[[@28,92:103='result_value',<459>,1:92]], id=[[@11,48:49='id',<459>,1:48]]}, insert1={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}",
				"{def_insert1={query_dictionary={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, table_dictionary={results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}, def_query0={query_dictionary={result_value=[[@28,92:103='result_value',<459>,1:92]], id=[[@11,48:49='id',<459>,1:48]]}, table_dictionary={events={active=[[@33,125:125='a',<459>,1:125]], threshold=[[@23,76:76='a',<459>,1:76]], id=[[@9,46:46='a',<459>,1:46]], tags=[[@15,59:59='a',<459>,1:59]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={id=[{name=id, table_ref=query0}], result_value=[{name=result_value, table_ref=query0}]}}}");
	}

	@Test
	public void arC029_filterUpdatePlacementV0Test() {
		final String query = "UPDATE events SET result_value = filter(tags, x -> x > threshold) WHERE active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:33) is recognized as Snowflake-specific grammar.",
						1, 33, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={update={table={alias=null, table=events}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}, assignments={1={set={column={name=result_value, table_ref=null}}, to={function={function_name=filter, parameters={1={column={name=tags, table_ref=null}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=null}}, operator=>}}}}}}}}}}}}",
				"[result_value]",
				"{}",
				"{events={active=[[@16,72:77='active',<459>,1:72]], threshold=[[@13,55:63='threshold',<459>,1:55], [@14,64:64=')',<288>,1:64]], tags=[[@7,40:43='tags',<459>,1:40], [@14,64:64=')',<288>,1:64]]}}",
				"{update0={result_value=[[@3,18:29='result_value',<459>,1:18]]}}",
				"{def_update0={assignments={result_value=[{name=tags, table_ref=events}, {name=threshold, table_ref=events}]}, table_dictionary={events={active=[[@16,72:77='active',<459>,1:72]], threshold=[[@13,55:63='threshold',<459>,1:55], [@14,64:64=')',<288>,1:64]], tags=[[@7,40:43='tags',<459>,1:40], [@14,64:64=')',<288>,1:64]]}}, update_dictionary={result_value=[[@3,18:29='result_value',<459>,1:18]]}, target_table={events={result_value=[[@3,18:29='result_value',<459>,1:18]]}}, filters=[{name=active, table_ref=events}], lhs_unresolved_columns={result_value={column={name=result_value, table_ref=null}, locations=[[@3,18:29='result_value',<459>,1:18]]}}}}");
	}

	@Test
	public void arC029_filterDeletePlacementV0Test() {
		final String query = "DELETE FROM events WHERE filter(tags, x -> x > threshold) IS NULL AND active = 0";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:25) is recognized as Snowflake-specific grammar.",
						1, 25, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={delete={table={alias=null, table=events}, where={and={1={condition={left={function={function_name=filter, parameters={1={column={name=tags, table_ref=null}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=null}}, operator=>}}}}}}}, operator=IS NULL}}, 2={condition={left={column={name=active, table_ref=null}}, right={literal=0}, operator==}}}}}}}",
				"[]",
				"{}",
				"{events={active=[[@17,70:75='active',<459>,1:70]], threshold=[[@12,47:55='threshold',<459>,1:47]], tags=[[@6,32:35='tags',<459>,1:32]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={active=[[@17,70:75='active',<459>,1:70]], threshold=[[@12,47:55='threshold',<459>,1:47]], tags=[[@6,32:35='tags',<459>,1:32]]}}, filters=[{name=tags, table_ref=events}, {name=threshold, table_ref=events}, {name=active, table_ref=events}], interface=null}}");
	}

	@Test
	public void arC029_filterDdlPlacementV0Test() {
		final String query = "CREATE VIEW v_filter AS SELECT a.id, filter(a.tags, x -> x > a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.DdlContext tree = parser.ddl();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:37) is recognized as Snowflake-specific grammar.",
						1, 37, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{DDL={create={type=VIEW, name={table=v_filter}, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[]",
				"{}",
				"{events={active=[[@29,110:110='a',<459>,1:110]], threshold=[[@19,61:61='a',<459>,1:61]], id=[[@5,31:31='a',<459>,1:31]], tags=[[@11,44:44='a',<459>,1:44]]}}",
				"{query0={result_value=[[@24,77:88='result_value',<459>,1:77]], id=[[@7,33:34='id',<459>,1:33]]}}",
				"{def_create1={def_query0={query_dictionary={result_value=[[@24,77:88='result_value',<459>,1:77]], id=[[@7,33:34='id',<459>,1:33]]}, table_dictionary={events={active=[[@29,110:110='a',<459>,1:110]], threshold=[[@19,61:61='a',<459>,1:61]], id=[[@5,31:31='a',<459>,1:31]], tags=[[@11,44:44='a',<459>,1:44]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}");
	}

	@Test
	public void arC029_filterScriptPlacementV0Test() {
		final String query = "SELECT a.id, filter(a.tags, x -> x > a.threshold) AS result_value FROM events a WHERE a.active = 1;\nSELECT b.id FROM orders b WHERE b.active = 1;\nSELECT a.id, filter(a.tags, x -> x > a.threshold) AS result_value FROM events a WHERE a.active = 1;";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.ScriptContext tree = parser.script();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 2,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:3 c:13) is recognized as Snowflake-specific grammar.",
						3, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SCRIPT={1={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, 2={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=orders}}, where={condition={left={column={name=active, table_ref=b}}, right={literal=1}, operator==}}}, 3={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}",
				"[]",
				"{SCRIPT={1={}, 2={}, 3={}}}",
				"{SCRIPT={1={events={active=[[@25,86:86='a',<459>,1:86]], threshold=[[@15,37:37='a',<459>,1:37]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, 2={orders={active=[[@39,132:132='b',<459>,2:32]], id=[[@32,107:107='b',<459>,2:7]]}}, 3={events={active=[[@70,232:232='a',<459>,3:86]], threshold=[[@60,183:183='a',<459>,3:37]], id=[[@46,153:153='a',<459>,3:7]], tags=[[@52,166:166='a',<459>,3:20]]}}}}",
				"{SCRIPT={1={query0={result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}}, 2={query0={id=[[@34,109:110='id',<459>,2:9]]}}, 3={query0={result_value=[[@65,199:210='result_value',<459>,3:53]], id=[[@48,155:156='id',<459>,3:9]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,86:86='a',<459>,1:86]], threshold=[[@15,37:37='a',<459>,1:37]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}, 2={def_query0={query_dictionary={id=[[@34,109:110='id',<459>,2:9]]}, table_dictionary={orders={active=[[@39,132:132='b',<459>,2:32]], id=[[@32,107:107='b',<459>,2:7]]}}, filters=[{name=active, table_ref=b}], interface={id=[{name=id, table_ref=b}]}, table_alias={b=orders}}}, 3={def_query0={query_dictionary={result_value=[[@65,199:210='result_value',<459>,3:53]], id=[[@48,155:156='id',<459>,3:9]]}, table_dictionary={events={active=[[@70,232:232='a',<459>,3:86]], threshold=[[@60,183:183='a',<459>,3:37]], id=[[@46,153:153='a',<459>,3:7]], tags=[[@52,166:166='a',<459>,3:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}}");
	}

	@Test
	public void arC029_filterArrayQualifiedV0Test() {
		final String query = "SELECT a.id, filter(a.tags, x -> x > a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@25,86:86='a',<459>,1:86]], threshold=[[@15,37:37='a',<459>,1:37]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,86:86='a',<459>,1:86]], threshold=[[@15,37:37='a',<459>,1:37]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterArrayUnqualifiedV0Test() {
		final String query = "SELECT a.id, filter(tags, x -> x > a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=null}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@23,84:84='a',<459>,1:84]], threshold=[[@13,35:35='a',<459>,1:35]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:23='tags',<459>,1:20]]}}",
				"{query0={result_value=[[@18,51:62='result_value',<459>,1:51]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,51:62='result_value',<459>,1:51]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@23,84:84='a',<459>,1:84]], threshold=[[@13,35:35='a',<459>,1:35]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:23='tags',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=events}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterArrayAmbiguousV0Test() {
		final String query = "SELECT a.id, filter(tags, x -> x > a.threshold) AS result_value FROM events a JOIN labels b ON a.id = b.event_id";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.SEVERE_WARNING,
						"AMBIGUOUS_COLUMN_REFERENCE",
						"Ambiguous column reference 'tags' at (l:1 c:20). Possible sources: [events, labels]",
						1, 20, "SqlASTWalkerHelper", null, "tags", true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [tags [(l:1 c:20)]]",
						1, 20, "SqlASTWalkerHelper", null, "tags", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=null}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=event_id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=labels}}}}}}",
				"[result_value, id]",
				"{}",
				"{events={threshold=[[@13,35:35='a',<459>,1:35]], id=[[@1,7:7='a',<459>,1:7], [@26,95:95='a',<459>,1:95]]}, labels={event_id=[[@30,102:102='b',<459>,1:102]]}}",
				"{query0={result_value=[[@18,51:62='result_value',<459>,1:51]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,51:62='result_value',<459>,1:51]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={threshold=[[@13,35:35='a',<459>,1:35]], id=[[@1,7:7='a',<459>,1:7], [@26,95:95='a',<459>,1:95]]}, labels={event_id=[[@30,102:102='b',<459>,1:102]]}}, filters=[{name=id, table_ref=a}, {name=event_id, table_ref=b}], interface={result_value=[{name=tags, table_ref=null}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events, b=labels}}}");
	}

	@Test
	public void arC029_filterArrayMissingAliasV0Test() {
		final String query = "SELECT a.id, filter(z.tags, x -> x > a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'tags' at (l:1 c:53). No alias or table called 'z'.",
						1, 53, "SqlASTWalkerHelper", null, "tags", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=z}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@25,86:86='a',<459>,1:86]], threshold=[[@15,37:37='a',<459>,1:37]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,86:86='a',<459>,1:86]], threshold=[[@15,37:37='a',<459>,1:37]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=z}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterArrayHiddenAliasV0Test() {
		final String query = "SELECT q.id, filter(hidden.tags, x -> x > a.threshold) AS result_value FROM (SELECT hidden.id, hidden.tags, hidden.threshold FROM events hidden) q JOIN events a ON q.id = a.id";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'tags' at (l:1 c:58). No alias or table called 'hidden'.",
						1, 58, "SqlASTWalkerHelper", null, "tags", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=q}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=hidden}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={join={1={table={alias=q, query={select={1={column={name=id, table_ref=hidden}}, 2={column={name=tags, table_ref=hidden}}, 3={column={name=threshold, table_ref=hidden}}}, from={table={alias=hidden, table=events}}}}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=q}}, right={column={name=id, table_ref=a}}, operator==}}}, 3={table={alias=a, table=events}}}}}}",
				"[result_value, id]",
				"{}",
				"{events={threshold=[[@32,108:113='hidden',<459>,1:108], [@15,42:42='a',<459>,1:42]], id=[[@24,84:89='hidden',<459>,1:84], [@48,171:171='a',<459>,1:171]], tags=[[@28,95:100='hidden',<459>,1:95]]}}",
				"{query0={threshold=[[@34,115:123='threshold',<459>,1:115]], id=[[@26,91:92='id',<459>,1:91], [@1,7:7='q',<459>,1:7], [@44,164:164='q',<459>,1:164]], tags=[[@30,102:105='tags',<459>,1:102]]}, query1={result_value=[[@20,58:69='result_value',<459>,1:58]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query1={query_dictionary={result_value=[[@20,58:69='result_value',<459>,1:58]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={threshold=[[@15,42:42='a',<459>,1:42]], id=[[@48,171:171='a',<459>,1:171]]}}, def_query0={query_dictionary={threshold=[[@34,115:123='threshold',<459>,1:115]], id=[[@26,91:92='id',<459>,1:91], [@1,7:7='q',<459>,1:7], [@44,164:164='q',<459>,1:164]], tags=[[@30,102:105='tags',<459>,1:102]]}, table_dictionary={events={threshold=[[@32,108:113='hidden',<459>,1:108], [@15,42:42='a',<459>,1:42]], id=[[@24,84:89='hidden',<459>,1:84], [@48,171:171='a',<459>,1:171]], tags=[[@28,95:100='hidden',<459>,1:95]]}}, interface={threshold=[{name=threshold, table_ref=hidden}], id=[{name=id, table_ref=hidden}], tags=[{name=tags, table_ref=hidden}]}, table_alias={hidden=events}}, filters=[{name=id, table_ref=q}, {name=id, table_ref=a}], interface={result_value=[{name=tags, table_ref=hidden}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=q}]}, table_alias={q=query0, a=events}}}");
	}

	@Test
	public void arC029_filterArrayUnresolvedV0Test() {
		final String query = "SELECT filter(tags, x -> x > 1) AS result_value";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [tags [(l:1 c:14)]]",
						1, 14, "SqlASTWalkerHelper", null, "tags", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={function={function_name=filter, parameters={1={column={name=tags, table_ref=null}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={literal=1}, operator=>}}}}}}, alias=result_value}}}}",
				"[result_value]",
				"{}",
				"{}",
				"{query0={result_value=[[@12,35:46='result_value',<459>,1:35]]}}",
				"{def_query0={query_dictionary={result_value=[[@12,35:46='result_value',<459>,1:35]]}, interface={result_value=[{name=tags, table_ref=null}]}}}");
	}

	@Test
	public void arC029_filterArrayPredicandVariableV0Test() {
		final String query = "SELECT a.id, filter(<tags_expr>, x -> x > a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={substitution={name=<tags_expr>, type=predicand}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<tags_expr>=predicand}",
				"{events={active=[[@23,91:91='a',<459>,1:91]], threshold=[[@13,42:42='a',<459>,1:42]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@18,58:69='result_value',<459>,1:58]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,58:69='result_value',<459>,1:58]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@23,91:91='a',<459>,1:91]], threshold=[[@13,42:42='a',<459>,1:42]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=<tags_expr>, type=predicand}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterArrayColumnVariableV0Test() {
		final String query = "SELECT a.id, filter(a.<tags_col>, x -> x > a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={substitution={name=<tags_col>, type=column}, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<tags_col>=column}",
				"{events={active=[[@25,92:92='a',<459>,1:92]], threshold=[[@15,43:43='a',<459>,1:43]], id=[[@1,7:7='a',<459>,1:7]], <tags_col>=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@20,59:70='result_value',<459>,1:59]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,59:70='result_value',<459>,1:59]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,92:92='a',<459>,1:92]], threshold=[[@15,43:43='a',<459>,1:43]], id=[[@1,7:7='a',<459>,1:7]], <tags_col>=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{substitution={name=<tags_col>, type=column}, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterArrayNullV0Test() {
		final String query = "SELECT a.id, filter(NULL, x -> x > a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={null_literal=null}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@23,84:84='a',<459>,1:84]], threshold=[[@13,35:35='a',<459>,1:35]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@18,51:62='result_value',<459>,1:51]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,51:62='result_value',<459>,1:51]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@23,84:84='a',<459>,1:84]], threshold=[[@13,35:35='a',<459>,1:35]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterBodyQualifiedV0Test() {
		final String query = "SELECT a.id, filter(a.tags, x -> x > a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@25,86:86='a',<459>,1:86]], threshold=[[@15,37:37='a',<459>,1:37]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,86:86='a',<459>,1:86]], threshold=[[@15,37:37='a',<459>,1:37]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterBodyUnqualifiedV0Test() {
		final String query = "SELECT a.id, filter(a.tags, x -> x > threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=null}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@23,84:84='a',<459>,1:84]], threshold=[[@15,37:45='threshold',<459>,1:37]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@18,51:62='result_value',<459>,1:51]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,51:62='result_value',<459>,1:51]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@23,84:84='a',<459>,1:84]], threshold=[[@15,37:45='threshold',<459>,1:37]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=events}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterBodyAmbiguousV0Test() {
		final String query = "SELECT a.id, filter(a.tags, x -> x > threshold) AS result_value FROM events a JOIN labels b ON a.id = b.event_id";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.SEVERE_WARNING,
						"AMBIGUOUS_COLUMN_REFERENCE",
						"Ambiguous column reference 'threshold' at (l:1 c:37). Possible sources: [events, labels]",
						1, 37, "SqlASTWalkerHelper", null, "threshold", true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [threshold [(l:1 c:37)]]",
						1, 37, "SqlASTWalkerHelper", null, "threshold", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=null}}, operator=>}}}}}}, alias=result_value}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=event_id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=labels}}}}}}",
				"[result_value, id]",
				"{}",
				"{events={id=[[@1,7:7='a',<459>,1:7], [@26,95:95='a',<459>,1:95]], tags=[[@7,20:20='a',<459>,1:20]]}, labels={event_id=[[@30,102:102='b',<459>,1:102]]}}",
				"{query0={result_value=[[@18,51:62='result_value',<459>,1:51]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,51:62='result_value',<459>,1:51]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={id=[[@1,7:7='a',<459>,1:7], [@26,95:95='a',<459>,1:95]], tags=[[@7,20:20='a',<459>,1:20]]}, labels={event_id=[[@30,102:102='b',<459>,1:102]]}}, filters=[{name=id, table_ref=a}, {name=event_id, table_ref=b}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=null}], id=[{name=id, table_ref=a}]}, table_alias={a=events, b=labels}}}");
	}

	@Test
	public void arC029_filterBodyMissingAliasV0Test() {
		final String query = "SELECT a.id, filter(a.tags, x -> x > z.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'threshold' at (l:1 c:53). No alias or table called 'z'.",
						1, 53, "SqlASTWalkerHelper", null, "threshold", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=z}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@25,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=z}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterBodyHiddenAliasV0Test() {
		final String query = "SELECT q.id, filter(a.tags, x -> x > hidden.threshold) AS result_value FROM (SELECT hidden.id, hidden.tags, hidden.threshold FROM events hidden) q JOIN events a ON q.id = a.id";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'threshold' at (l:1 c:58). No alias or table called 'hidden'.",
						1, 58, "SqlASTWalkerHelper", null, "threshold", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=q}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=hidden}}, operator=>}}}}}}, alias=result_value}}, from={join={1={table={alias=q, query={select={1={column={name=id, table_ref=hidden}}, 2={column={name=tags, table_ref=hidden}}, 3={column={name=threshold, table_ref=hidden}}}, from={table={alias=hidden, table=events}}}}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=q}}, right={column={name=id, table_ref=a}}, operator==}}}, 3={table={alias=a, table=events}}}}}}",
				"[result_value, id]",
				"{}",
				"{events={threshold=[[@32,108:113='hidden',<459>,1:108]], id=[[@24,84:89='hidden',<459>,1:84], [@48,171:171='a',<459>,1:171]], tags=[[@28,95:100='hidden',<459>,1:95], [@7,20:20='a',<459>,1:20]]}}",
				"{query0={threshold=[[@34,115:123='threshold',<459>,1:115]], id=[[@26,91:92='id',<459>,1:91], [@1,7:7='q',<459>,1:7], [@44,164:164='q',<459>,1:164]], tags=[[@30,102:105='tags',<459>,1:102]]}, query1={result_value=[[@20,58:69='result_value',<459>,1:58]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query1={query_dictionary={result_value=[[@20,58:69='result_value',<459>,1:58]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={id=[[@48,171:171='a',<459>,1:171]], tags=[[@7,20:20='a',<459>,1:20]]}}, def_query0={query_dictionary={threshold=[[@34,115:123='threshold',<459>,1:115]], id=[[@26,91:92='id',<459>,1:91], [@1,7:7='q',<459>,1:7], [@44,164:164='q',<459>,1:164]], tags=[[@30,102:105='tags',<459>,1:102]]}, table_dictionary={events={threshold=[[@32,108:113='hidden',<459>,1:108]], id=[[@24,84:89='hidden',<459>,1:84], [@48,171:171='a',<459>,1:171]], tags=[[@28,95:100='hidden',<459>,1:95], [@7,20:20='a',<459>,1:20]]}}, interface={threshold=[{name=threshold, table_ref=hidden}], id=[{name=id, table_ref=hidden}], tags=[{name=tags, table_ref=hidden}]}, table_alias={hidden=events}}, filters=[{name=id, table_ref=q}, {name=id, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=hidden}], id=[{name=id, table_ref=q}]}, table_alias={q=query0, a=events}}}");
	}

	@Test
	public void arC029_filterBodyUnresolvedV0Test() {
		final String query = "SELECT filter([1,2], x -> x > threshold) AS result_value";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:14) is recognized as Snowflake-specific grammar.",
						1, 14, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [threshold [(l:1 c:30)]]",
						1, 30, "SqlASTWalkerHelper", null, "threshold", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={function={function_name=filter, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=null}}, operator=>}}}}}}, alias=result_value}}}}",
				"[result_value]",
				"{}",
				"{}",
				"{query0={result_value=[[@16,44:55='result_value',<459>,1:44]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,44:55='result_value',<459>,1:44]]}, interface={result_value=[{name=threshold, table_ref=null}]}}}");
	}

	@Test
	public void arC029_filterBodyPredicandVariableV0Test() {
		final String query = "SELECT a.id, filter(a.tags, x -> x > <threshold_expr>) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={substitution={name=<threshold_expr>, type=predicand}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<threshold_expr>=predicand}",
				"{events={active=[[@23,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@18,58:69='result_value',<459>,1:58]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,58:69='result_value',<459>,1:58]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@23,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=<threshold_expr>, type=predicand}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterBodyColumnVariableV0Test() {
		final String query = "SELECT a.id, filter(a.tags, x -> x > a.<threshold_col>) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={substitution={name=<threshold_col>, type=column}, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<threshold_col>=column}",
				"{events={<threshold_col>=[[@15,37:37='a',<459>,1:37]], active=[[@25,92:92='a',<459>,1:92]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@20,59:70='result_value',<459>,1:59]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,59:70='result_value',<459>,1:59]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={<threshold_col>=[[@15,37:37='a',<459>,1:37]], active=[[@25,92:92='a',<459>,1:92]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {substitution={name=<threshold_col>, type=column}, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterBodyNullV0Test() {
		final String query = "SELECT a.id, filter(a.tags, x -> x > NULL) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={null_literal=null}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@23,79:79='a',<459>,1:79]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@18,46:57='result_value',<459>,1:46]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,46:57='result_value',<459>,1:46]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@23,79:79='a',<459>,1:79]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterLocalOnlyNoSourceV0Test() {
		final String query = "SELECT filter([1,2,3], x -> x > 0) AS result_value";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:14) is recognized as Snowflake-specific grammar.",
						1, 14, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={function={function_name=filter, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}, 3={literal=3}}}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={literal=0}, operator=>}}}}}}, alias=result_value}}}}",
				"[result_value]",
				"{}",
				"{}",
				"{query0={result_value=[[@18,38:49='result_value',<459>,1:38]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,38:49='result_value',<459>,1:38]]}, interface={result_value=[]}}}");
	}

	@Test
	public void arC029_filterTypedBindingsV0Test() {
		final String query = "SELECT a.id, filter(a.tags, x INT -> x > 0) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x, data_type={type=INT}}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={literal=0}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@24,80:80='a',<459>,1:80]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@19,47:58='result_value',<459>,1:47]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@19,47:58='result_value',<459>,1:47]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@24,80:80='a',<459>,1:80]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterParenthesizedBindingsV0Test() {
		final String query = "SELECT a.id, filter(a.tags, (x) -> x > 0) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={literal=0}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@25,78:78='a',<459>,1:78]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@20,45:56='result_value',<459>,1:45]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,45:56='result_value',<459>,1:45]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,78:78='a',<459>,1:78]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterLocalJsonPathV0Test() {
		final String query = "SELECT a.id, filter(a.tags, x -> x:amount > a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0, json_path=amount}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@27,93:93='a',<459>,1:93]], threshold=[[@17,44:44='a',<459>,1:44]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@22,60:71='result_value',<459>,1:60]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@22,60:71='result_value',<459>,1:60]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@27,93:93='a',<459>,1:93]], threshold=[[@17,44:44='a',<459>,1:44]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterLocalNameMatchesColumnV0Test() {
		final String query = "SELECT a.id, filter(a.tags, x -> x > a.x) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=x, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={x=[[@15,37:37='a',<459>,1:37]], active=[[@25,78:78='a',<459>,1:78]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@20,45:56='result_value',<459>,1:45]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,45:56='result_value',<459>,1:45]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={x=[[@15,37:37='a',<459>,1:37]], active=[[@25,78:78='a',<459>,1:78]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=x, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterLocalNameMatchesTableAliasV0Test() {
		final String query = "SELECT x.id, filter(x.tags, x -> x > x.threshold) AS result_value FROM events x WHERE x.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=x}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=x}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=x}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=x, table=events}}, where={condition={left={column={name=active, table_ref=x}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@25,86:86='x',<459>,1:86]], threshold=[[@15,37:37='x',<459>,1:37]], id=[[@1,7:7='x',<459>,1:7]], tags=[[@7,20:20='x',<459>,1:20]]}}",
				"{query0={result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,86:86='x',<459>,1:86]], threshold=[[@15,37:37='x',<459>,1:37]], id=[[@1,7:7='x',<459>,1:7]], tags=[[@7,20:20='x',<459>,1:20]]}}, filters=[{name=active, table_ref=x}], interface={result_value=[{name=tags, table_ref=x}, {name=threshold, table_ref=x}], id=[{name=id, table_ref=x}]}, table_alias={x=events}}}");
	}

	@Test
	public void arC029_filterNoLeakAfterCallV0Test() {
		final String query = "SELECT a.id, filter(a.tags, x -> x > 0) AS result_value, x AS physical_x FROM events a";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={literal=0}, operator=>}}}}}}, alias=result_value}, 3={column={name=x, table_ref=null}, alias=physical_x}}, from={table={alias=a, table=events}}}}",
				"[physical_x, result_value, id]",
				"{}",
				"{events={x=[[@20,57:57='x',<459>,1:57]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@18,43:54='result_value',<459>,1:43]], physical_x=[[@22,62:71='physical_x',<459>,1:62]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={physical_x=[[@22,62:71='physical_x',<459>,1:62]], result_value=[[@18,43:54='result_value',<459>,1:43]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={x=[[@20,57:57='x',<459>,1:57]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, interface={physical_x=[{name=x, table_ref=events}], result_value=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterNoBindingInArrayOperandV0Test() {
		final String query = "SELECT a.id, filter(x, x -> x > 0) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=x, table_ref=null}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={literal=0}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={x=[[@7,20:20='x',<459>,1:20]], active=[[@21,71:71='a',<459>,1:71]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@16,38:49='result_value',<459>,1:38]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,38:49='result_value',<459>,1:38]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={x=[[@7,20:20='x',<459>,1:20]], active=[[@21,71:71='a',<459>,1:71]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=x, table_ref=events}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterNestedShadowingV0Test() {
		final String query = "SELECT a.id, filter(a.tags, x -> filter(a.other_tags, x -> x > a.threshold) IS NOT NULL AND x > 0) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 2,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:33) is recognized as Snowflake-specific grammar.",
						1, 33, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={and={1={condition={left={function={function_name=filter, parameters={1={column={name=other_tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={lambda_reference={name=x, depth=0}}, right={literal=0}, operator=>}}}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@41,135:135='a',<459>,1:135]], threshold=[[@23,63:63='a',<459>,1:63]], id=[[@1,7:7='a',<459>,1:7]], other_tags=[[@15,40:40='a',<459>,1:40]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@36,102:113='result_value',<459>,1:102]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@36,102:113='result_value',<459>,1:102]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@41,135:135='a',<459>,1:135]], threshold=[[@23,63:63='a',<459>,1:63]], id=[[@1,7:7='a',<459>,1:7]], other_tags=[[@15,40:40='a',<459>,1:40]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=other_tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterNestedOuterCaptureV0Test() {
		final String query = "SELECT a.id, filter(a.tags, x -> filter(a.other_tags, y -> y > x + a.threshold) IS NOT NULL) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 2,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:33) is recognized as Snowflake-specific grammar.",
						1, 33, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={function={function_name=filter, parameters={1={column={name=other_tags, table_ref=a}}, 2={lambda={parameters={1={name=y}}, body={condition={left={lambda_reference={name=y, depth=0}}, right={calc={left={lambda_reference={name=x, depth=1}}, right={column={name=threshold, table_ref=a}}, operator=+}}, operator=>}}}}}}}, operator=IS NOT NULL}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@39,129:129='a',<459>,1:129]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], other_tags=[[@15,40:40='a',<459>,1:40]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@34,96:107='result_value',<459>,1:96]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@34,96:107='result_value',<459>,1:96]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@39,129:129='a',<459>,1:129]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], other_tags=[[@15,40:40='a',<459>,1:40]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=other_tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterEmptyArrayV0Test() {
		final String query = "SELECT a.id, filter([], x -> x > 0) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:20) is recognized as Snowflake-specific grammar.",
						1, 20, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={function={function_name=array_construct, parameters={}}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={literal=0}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@22,72:72='a',<459>,1:72]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@17,39:50='result_value',<459>,1:39]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@17,39:50='result_value',<459>,1:39]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@22,72:72='a',<459>,1:72]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterNullArrayV0Test() {
		final String query = "SELECT a.id, filter(NULL, x -> x > 0) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={null_literal=null}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={literal=0}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@21,74:74='a',<459>,1:74]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@16,41:52='result_value',<459>,1:41]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,41:52='result_value',<459>,1:41]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,74:74='a',<459>,1:74]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterNullBodyRuntimeBoundaryV0Test() {
		final String query = "SELECT a.id, filter(a.tags, x -> NULL) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={null_literal=null}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@21,75:75='a',<459>,1:75]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@16,42:53='result_value',<459>,1:42]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,42:53='result_value',<459>,1:42]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,75:75='a',<459>,1:75]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterBareBodySubstitutionV0Test() {
		final String query = "SELECT a.id, filter(a.tags, x -> <lambda_body>) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={substitution={name=<lambda_body>, type=predicand}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<lambda_body>=predicand}",
				"{events={active=[[@21,84:84='a',<459>,1:84]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@16,51:62='result_value',<459>,1:51]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,51:62='result_value',<459>,1:51]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,84:84='a',<459>,1:84]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=<lambda_body>, type=predicand}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterCombinedSubstitutionsV0Test() {
		final String query = "SELECT a.id, filter(<array_expr>, x -> x > <external_expr>) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={substitution={name=<array_expr>, type=predicand}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={substitution={name=<external_expr>, type=predicand}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<external_expr>=predicand, <array_expr>=predicand}",
				"{events={active=[[@21,96:96='a',<459>,1:96]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@16,63:74='result_value',<459>,1:63]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,63:74='result_value',<459>,1:63]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,96:96='a',<459>,1:96]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=<array_expr>, type=predicand}, {name=<external_expr>, type=predicand}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterDerivedOutputV0Test() {
		final String query = "SELECT q.id, q.result_value FROM (SELECT a.id, filter(a.tags, x -> x > a.threshold) AS result_value FROM events a WHERE a.active = 1) q WHERE q.result_value IS NOT NULL";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:47) is recognized as Snowflake-specific grammar.",
						1, 47, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=q}}, 2={column={name=result_value, table_ref=q}}}, from={table={alias=q, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}, where={condition={left={column={name=result_value, table_ref=q}}, operator=IS NOT NULL}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@35,120:120='a',<459>,1:120]], threshold=[[@25,71:71='a',<459>,1:71]], id=[[@11,41:41='a',<459>,1:41]], tags=[[@17,54:54='a',<459>,1:54]]}}",
				"{query0={result_value=[[@30,87:98='result_value',<459>,1:87], [@5,13:13='q',<459>,1:13], [@43,142:142='q',<459>,1:142]], id=[[@13,43:44='id',<459>,1:43], [@1,7:7='q',<459>,1:7]]}, query1={result_value=[[@7,15:26='result_value',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query1={query_dictionary={result_value=[[@7,15:26='result_value',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}, def_query0={query_dictionary={result_value=[[@30,87:98='result_value',<459>,1:87], [@5,13:13='q',<459>,1:13], [@43,142:142='q',<459>,1:142]], id=[[@13,43:44='id',<459>,1:43], [@1,7:7='q',<459>,1:7]]}, table_dictionary={events={active=[[@35,120:120='a',<459>,1:120]], threshold=[[@25,71:71='a',<459>,1:71]], id=[[@11,41:41='a',<459>,1:41]], tags=[[@17,54:54='a',<459>,1:54]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=result_value, table_ref=q}], interface={result_value=[{name=result_value, table_ref=q}], id=[{name=id, table_ref=q}]}, table_alias={q=query0}}}");
	}

	@Test
	public void arC029_filterCteRenameChainV0Test() {
		final String query = "WITH seed AS (SELECT a.id, filter(a.tags, x -> x > a.threshold) AS result_value FROM events a WHERE a.active = 1), renamed AS (SELECT id, result_value AS exported_value FROM seed) SELECT r.id, r.exported_value FROM renamed r";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:27) is recognized as Snowflake-specific grammar.",
						1, 27, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=seed}, 2={cte={select={1={column={name=id, table_ref=null}}, 2={column={name=result_value, table_ref=null}, alias=exported_value}}, from={table={alias=null, table=seed}}}, alias=renamed}}, query={select={1={column={name=id, table_ref=r}}, 2={column={name=exported_value, table_ref=r}}}, from={table={alias=r, table=renamed}}}}}",
				"[exported_value, id]",
				"{}",
				"{events={active=[[@29,100:100='a',<459>,1:100]], threshold=[[@19,51:51='a',<459>,1:51]], id=[[@5,21:21='a',<459>,1:21]], tags=[[@11,34:34='a',<459>,1:34]]}}",
				"{query0={result_value=[[@24,67:78='result_value',<459>,1:67], [@42,138:149='result_value',<459>,1:138]], id=[[@7,23:24='id',<459>,1:23], [@40,134:135='id',<459>,1:134]]}, query1={exported_value=[[@44,154:167='exported_value',<459>,1:154], [@53,193:193='r',<459>,1:193]], id=[[@40,134:135='id',<459>,1:134], [@49,187:187='r',<459>,1:187]]}, query2={exported_value=[[@55,195:208='exported_value',<459>,1:195]], id=[[@51,189:190='id',<459>,1:189]]}}",
				"{def_query2={context_list={seed=query0, renamed=query1, r=query1}, query_dictionary={exported_value=[[@55,195:208='exported_value',<459>,1:195]], id=[[@51,189:190='id',<459>,1:189]]}, def_query1={context_list={seed=query0}, query_dictionary={exported_value=[[@44,154:167='exported_value',<459>,1:154], [@53,193:193='r',<459>,1:193]], id=[[@40,134:135='id',<459>,1:134], [@49,187:187='r',<459>,1:187]]}, interface={exported_value=[{name=result_value, table_ref=query0}], id=[{name=id, table_ref=query0}]}, table_alias={seed=query0}}, def_query0={query_dictionary={result_value=[[@24,67:78='result_value',<459>,1:67], [@42,138:149='result_value',<459>,1:138]], id=[[@7,23:24='id',<459>,1:23], [@40,134:135='id',<459>,1:134]]}, table_dictionary={events={active=[[@29,100:100='a',<459>,1:100]], threshold=[[@19,51:51='a',<459>,1:51]], id=[[@5,21:21='a',<459>,1:21]], tags=[[@11,34:34='a',<459>,1:34]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={exported_value=[{name=exported_value, table_ref=r}], id=[{name=id, table_ref=r}]}, table_alias={r=query1, seed=query0, renamed=query1}}}");
	}

	@Test
	public void arC029_filterCteMissingExportV0Test() {
		final String query = "WITH seed AS (SELECT a.id FROM events a) SELECT a.id, filter(a.tags, x -> x > 0) AS result_value FROM seed a";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:54) is recognized as Snowflake-specific grammar.",
						1, 54, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
						"Qualified column 'tags' at (l:1 c:61) was not found in output interface of query alias 'a'.",
						1, 61, "SqlASTWalkerHelper", null, "a.tags", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}}, alias=seed}}, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={literal=0}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=seed}}}}}",
				"[result_value, id]",
				"{}",
				"{events={id=[[@5,21:21='a',<459>,1:21]]}}",
				"{query0={id=[[@7,23:24='id',<459>,1:23], [@13,48:48='a',<459>,1:48]]}, query1={result_value=[[@30,84:95='result_value',<459>,1:84]], id=[[@15,50:51='id',<459>,1:50]]}}",
				"{def_query1={context_list={seed=query0, a=query0}, query_dictionary={result_value=[[@30,84:95='result_value',<459>,1:84]], id=[[@15,50:51='id',<459>,1:50]]}, def_query0={query_dictionary={id=[[@7,23:24='id',<459>,1:23], [@13,48:48='a',<459>,1:48]]}, table_dictionary={events={id=[[@5,21:21='a',<459>,1:21]]}}, interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={result_value=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=query0, seed=query0}}}");
	}

	@Test
	public void arC029_filterSetOpSiblingIsolationV0Test() {
		final String query = "SELECT a.id, filter(a.tags, x -> x > a.threshold) AS result_value FROM events a WHERE a.active = 1 UNION ALL SELECT b.id AS other_id, b.stored_value AS other_value FROM archive_events b";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={union={1={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={select={1={column={name=id, table_ref=b}, alias=other_id}, 2={column={name=stored_value, table_ref=b}, alias=other_value}}, from={table={alias=b, table=archive_events}}}}}}",
				"[result_value, id]",
				"{}",
				"{archive_events={stored_value=[[@39,134:134='b',<459>,1:134]], id=[[@33,116:116='b',<459>,1:116]]}, events={active=[[@25,86:86='a',<459>,1:86]], threshold=[[@15,37:37='a',<459>,1:37]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}, query1={other_value=[[@43,152:162='other_value',<459>,1:152]], other_id=[[@37,124:131='other_id',<459>,1:124]]}}",
				"{def_union2={def_query1={query_dictionary={other_value=[[@43,152:162='other_value',<459>,1:152]], other_id=[[@37,124:131='other_id',<459>,1:124]]}, table_dictionary={archive_events={stored_value=[[@39,134:134='b',<459>,1:134]], id=[[@33,116:116='b',<459>,1:116]]}}, setop=UNION, interface={other_value=[{name=stored_value, table_ref=b}], other_id=[{name=id, table_ref=b}]}, table_alias={b=archive_events}}, def_query0={query_dictionary={result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,86:86='a',<459>,1:86]], threshold=[[@15,37:37='a',<459>,1:37]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={result_value=query_column, id=query_column}}}");
	}

	@Test
	public void arC029_filterCorrelatedSubqueryV0Test() {
		final String query = "SELECT a.id FROM events a WHERE EXISTS (SELECT b.id FROM labels b WHERE b.event_id = a.id AND filter(a.tags, x -> x > a.threshold) IS NOT NULL)";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:94) is recognized as Snowflake-specific grammar.",
						1, 94, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}, where={exists={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=labels}}, where={and={1={condition={left={column={name=event_id, table_ref=b}}, right={column={name=id, table_ref=a}}, operator==}}, 2={condition={left={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}}, operator=IS NOT NULL}}}}, operator=EXISTS}}}}",
				"[id]",
				"{}",
				"{events={threshold=[[@36,118:118='a',<459>,1:118]], id=[[@1,7:7='a',<459>,1:7], [@22,85:85='a',<459>,1:85]], tags=[[@28,101:101='a',<459>,1:101]]}, labels={event_id=[[@18,72:72='b',<459>,1:72]], id=[[@11,47:47='b',<459>,1:47]]}}",
				"{query0={id=[[@13,49:50='id',<459>,1:49]]}, query2={id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query2={query_dictionary={id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={threshold=[[@36,118:118='a',<459>,1:118]], id=[[@1,7:7='a',<459>,1:7], [@22,85:85='a',<459>,1:85]], tags=[[@28,101:101='a',<459>,1:101]]}}, dependent_queries={exists1={query=query0, type=filters}}, def_query0={query_dictionary={id=[[@13,49:50='id',<459>,1:49]]}, table_dictionary={labels={event_id=[[@18,72:72='b',<459>,1:72]], id=[[@11,47:47='b',<459>,1:47]]}}, filters=[{name=event_id, table_ref=b}, {name=id, table_ref=a}, {name=tags, table_ref=a}, {name=threshold, table_ref=a}], interface={id=[{name=id, table_ref=b}]}, table_alias={b=labels}}, filters=[], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterGenericCompatibilityV0Test() {
		final String query = "SELECT a.id, filter(a.tags, a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 0,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=tags, table_ref=a}}, 2={column={name=threshold, table_ref=a}}}, function_name=filter}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@21,77:77='a',<459>,1:77]], threshold=[[@11,28:28='a',<459>,1:28]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@16,44:55='result_value',<459>,1:44]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,44:55='result_value',<459>,1:44]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,77:77='a',<459>,1:77]], threshold=[[@11,28:28='a',<459>,1:28]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterNullLambdaCompatibilityV0Test() {
		final String query = "SELECT a.id, filter(a.tags, NULL) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 0,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=tags, table_ref=a}}, 2={null_literal=null}}, function_name=filter}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@19,70:70='a',<459>,1:70]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@14,37:48='result_value',<459>,1:37]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@14,37:48='result_value',<459>,1:37]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@19,70:70='a',<459>,1:70]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterQualifiedUdfV0Test() {
		final String query = "SELECT a.id, util.filter(a.tags, a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 0,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={schema=util, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=threshold, table_ref=a}}}, function_name=filter}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@23,82:82='a',<459>,1:82]], threshold=[[@13,33:33='a',<459>,1:33]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@9,25:25='a',<459>,1:25]]}}",
				"{query0={result_value=[[@18,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@23,82:82='a',<459>,1:82]], threshold=[[@13,33:33='a',<459>,1:33]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@9,25:25='a',<459>,1:25]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterSameNamedColumnV0Test() {
		final String query = "SELECT a.id, a.filter AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 0,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=filter, table_ref=a}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={filter=[[@5,13:13='a',<459>,1:13]], active=[[@14,58:58='a',<459>,1:58]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@9,25:36='result_value',<459>,1:25]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@9,25:36='result_value',<459>,1:25]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={filter=[[@5,13:13='a',<459>,1:13]], active=[[@14,58:58='a',<459>,1:58]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=filter, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterUppercaseV0Test() {
		final String query = "SELECT a.id, FILTER(a.tags, x -> x > a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=FILTER, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@25,86:86='a',<459>,1:86]], threshold=[[@15,37:37='a',<459>,1:37]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,86:86='a',<459>,1:86]], threshold=[[@15,37:37='a',<459>,1:37]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterMixedDialectV0Test() {
		final String query = "SELECT a.id, filter(a.tags, x -> x > a.threshold) AS result_value, extract(isodow from a.created_at) AS weekday FROM events a";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"STATEMENT_MIXED_DIALECT_GRAMMAR",
						"Statement mixes Snowflake-only and PostgreSQL-only grammar at (l:1 c:67); unlikely to run on either engine.",
						1, 67, "SqlASTWalkerHelper", null, null, false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}, 3={alias=weekday, datetime_function={function_name=extract, part_form=KEYWORD, part=isodow, source={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}",
				"[weekday, result_value, id]",
				"{}",
				"{events={created_at=[[@26,87:87='a',<459>,1:87]], threshold=[[@15,37:37='a',<459>,1:37]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={weekday=[[@31,104:110='weekday',<459>,1:104]], result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={weekday=[[@31,104:110='weekday',<459>,1:104]], result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={created_at=[[@26,87:87='a',<459>,1:87]], threshold=[[@15,37:37='a',<459>,1:37]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, interface={weekday=[{name=created_at, table_ref=a}], result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC029_filterPREDICANDSnippetV0Test() {
		final String query = "filter([1,2], x -> x > 0)";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.predicand_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{PREDICAND={function={function_name=filter, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={literal=0}, operator=>}}}}}}}}",
				"[]",
				"{}",
				"{}",
				"{}",
				"{}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "PREDICAND");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{PREDICAND={function={function_name=filter, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={literal=0}, operator=>}}}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC029_filterCONDITIONSnippetV0Test() {
		final String query = "filter([1,2], x -> x > 0) IS NOT NULL AND 1 = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.condition_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{CONDITION={and={1={condition={left={function={function_name=filter, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={literal=0}, operator=>}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={literal=1}, right={literal=1}, operator==}}}}}",
				"[]",
				"{}",
				"{}",
				"{}",
				"{}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "CONDITION");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{CONDITION={and={1={condition={left={function={function_name=filter, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={literal=0}, operator=>}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={literal=1}, right={literal=1}, operator==}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC029_filterQUERYSnippetV0Test() {
		final String query = "SELECT a.id, filter(a.tags, x -> x > a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.query_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{QUERY={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@25,86:86='a',<459>,1:86]], threshold=[[@15,37:37='a',<459>,1:37]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,86:86='a',<459>,1:86]], threshold=[[@15,37:37='a',<459>,1:37]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "QUERY");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{QUERY={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[result_value, id]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={active=[[@25,86:86='a',<459>,1:86]], threshold=[[@15,37:37='a',<459>,1:37]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{query0={result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_query0={query_dictionary={result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,86:86='a',<459>,1:86]], threshold=[[@15,37:37='a',<459>,1:37]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC029_filterTUPLESnippetV0Test() {
		final String query = "(SELECT a.id, filter(a.tags, x -> x > a.threshold) AS result_value FROM events a WHERE a.active = 1)";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.tuple_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:14) is recognized as Snowflake-specific grammar.",
						1, 14, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{TUPLE={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@26,87:87='a',<459>,1:87]], threshold=[[@16,38:38='a',<459>,1:38]], id=[[@2,8:8='a',<459>,1:8]], tags=[[@8,21:21='a',<459>,1:21]]}}",
				"{query0={result_value=[[@21,54:65='result_value',<459>,1:54]], id=[[@4,10:11='id',<459>,1:10]]}}",
				"{def_query0={query_dictionary={result_value=[[@21,54:65='result_value',<459>,1:54]], id=[[@4,10:11='id',<459>,1:10]]}, table_dictionary={events={active=[[@26,87:87='a',<459>,1:87]], threshold=[[@16,38:38='a',<459>,1:38]], id=[[@2,8:8='a',<459>,1:8]], tags=[[@8,21:21='a',<459>,1:21]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, table_alias={query0=query0}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "TUPLE");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:14) is recognized as Snowflake-specific grammar.",
						1, 14, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{TUPLE={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[result_value, id]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={active=[[@26,87:87='a',<459>,1:87]], threshold=[[@16,38:38='a',<459>,1:38]], id=[[@2,8:8='a',<459>,1:8]], tags=[[@8,21:21='a',<459>,1:21]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{query0={result_value=[[@21,54:65='result_value',<459>,1:54]], id=[[@4,10:11='id',<459>,1:10]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_query0={query_dictionary={result_value=[[@21,54:65='result_value',<459>,1:54]], id=[[@4,10:11='id',<459>,1:10]]}, table_dictionary={events={active=[[@26,87:87='a',<459>,1:87]], threshold=[[@16,38:38='a',<459>,1:38]], id=[[@2,8:8='a',<459>,1:8]], tags=[[@8,21:21='a',<459>,1:21]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, table_alias={query0=query0}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC029_filterIN_LISTSnippetV0Test() {
		final String query = "(filter([1,2], x -> x > 0), NULL)";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.in_list_predicate_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:8) is recognized as Snowflake-specific grammar.",
						1, 8, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{IN_LIST={list={1={function={function_name=filter, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={literal=0}, operator=>}}}}}}}, 2={null_literal=null}}}}",
				"[]",
				"{}",
				"{}",
				"{}",
				"{}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "IN_LIST");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:8) is recognized as Snowflake-specific grammar.",
						1, 8, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{IN_LIST={list={1={function={function_name=filter, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={literal=0}, operator=>}}}}}}}, 2={null_literal=null}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC029_filterJOIN_EXTENSIONSnippetV0Test() {
		final String query = "JOIN events a ON filter(a.tags, x -> x > a.threshold) IS NOT NULL AND a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.join_extension_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:17) is recognized as Snowflake-specific grammar.",
						1, 17, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{JOIN_EXTENSION={1={join=JOIN, on={and={1={condition={left={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}, 2={table={alias=a, table=events}}}}",
				"[]",
				"{}",
				"{events={active=[[@22,70:70='a',<459>,1:70]], threshold=[[@14,41:41='a',<459>,1:41]], tags=[[@6,24:24='a',<459>,1:24]]}}",
				"{}",
				"{table_dictionary={events={active=[[@22,70:70='a',<459>,1:70]], threshold=[[@14,41:41='a',<459>,1:41]], tags=[[@6,24:24='a',<459>,1:24]]}}, filters=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}, {name=active, table_ref=a}], table_alias={a=events}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "JOIN_EXTENSION");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:17) is recognized as Snowflake-specific grammar.",
						1, 17, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{JOIN_EXTENSION={1={join=JOIN, on={and={1={condition={left={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}, 2={table={alias=a, table=events}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={active=[[@22,70:70='a',<459>,1:70]], threshold=[[@14,41:41='a',<459>,1:41]], tags=[[@6,24:24='a',<459>,1:24]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{table_dictionary={events={active=[[@22,70:70='a',<459>,1:70]], threshold=[[@14,41:41='a',<459>,1:41]], tags=[[@6,24:24='a',<459>,1:24]]}}, filters=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}, {name=active, table_ref=a}], table_alias={a=events}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC029_filterVALUESSnippetV0Test() {
		final String query = "VALUES (7, filter([1,2], x -> x > 0))";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.values_statement_end();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:18) is recognized as Snowflake-specific grammar.",
						1, 18, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{VALUES={values={matrix={1={row={1={literal=7}, 2={function={function_name=filter, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={literal=0}, operator=>}}}}}}}}}}}}}",
				"[$1, $2]",
				"{}",
				"{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[], $2=[]}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "VALUES");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:18) is recognized as Snowflake-specific grammar.",
						1, 18, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{VALUES={values={matrix={1={row={1={literal=7}, 2={function={function_name=filter, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={literal=0}, operator=>}}}}}}}}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[$1, $2]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{values0={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[], $2=[]}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC029_filterINSERTSnippetV0Test() {
		final String query = "INSERT INTO results (id, result_value) SELECT a.id, filter(a.tags, x -> x > a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.insert_end_point();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:52) is recognized as Snowflake-specific grammar.",
						1, 52, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{INSERT={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}}, target_table={table={alias=null, table=results}}, columns={1={column={name=id, table_ref=null}}, 2={column={name=result_value, table_ref=null}}}}}}",
				"[result_value, id]",
				"{}",
				"{results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, events={active=[[@33,125:125='a',<459>,1:125]], threshold=[[@23,76:76='a',<459>,1:76]], id=[[@9,46:46='a',<459>,1:46]], tags=[[@15,59:59='a',<459>,1:59]]}}",
				"{query0={result_value=[[@28,92:103='result_value',<459>,1:92]], id=[[@11,48:49='id',<459>,1:48]]}, insert1={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}",
				"{def_insert1={query_dictionary={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, table_dictionary={results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}, def_query0={query_dictionary={result_value=[[@28,92:103='result_value',<459>,1:92]], id=[[@11,48:49='id',<459>,1:48]]}, table_dictionary={events={active=[[@33,125:125='a',<459>,1:125]], threshold=[[@23,76:76='a',<459>,1:76]], id=[[@9,46:46='a',<459>,1:46]], tags=[[@15,59:59='a',<459>,1:59]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={id=[{name=id, table_ref=query0}], result_value=[{name=result_value, table_ref=query0}]}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "INSERT");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:52) is recognized as Snowflake-specific grammar.",
						1, 52, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{INSERT={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}}, target_table={table={alias=null, table=results}}, columns={1={column={name=id, table_ref=null}}, 2={column={name=result_value, table_ref=null}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[result_value, id]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, events={active=[[@33,125:125='a',<459>,1:125]], threshold=[[@23,76:76='a',<459>,1:76]], id=[[@9,46:46='a',<459>,1:46]], tags=[[@15,59:59='a',<459>,1:59]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{query0={result_value=[[@28,92:103='result_value',<459>,1:92]], id=[[@11,48:49='id',<459>,1:48]]}, insert1={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_insert1={query_dictionary={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, table_dictionary={results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}, def_query0={query_dictionary={result_value=[[@28,92:103='result_value',<459>,1:92]], id=[[@11,48:49='id',<459>,1:48]]}, table_dictionary={events={active=[[@33,125:125='a',<459>,1:125]], threshold=[[@23,76:76='a',<459>,1:76]], id=[[@9,46:46='a',<459>,1:46]], tags=[[@15,59:59='a',<459>,1:59]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={id=[{name=id, table_ref=query0}], result_value=[{name=result_value, table_ref=query0}]}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC029_filterUPDATESnippetV0Test() {
		final String query = "UPDATE events SET result_value = filter(tags, x -> x > threshold) WHERE active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.update_end_point();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:33) is recognized as Snowflake-specific grammar.",
						1, 33, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{UPDATE={update={table={alias=null, table=events}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}, assignments={1={set={column={name=result_value, table_ref=null}}, to={function={function_name=filter, parameters={1={column={name=tags, table_ref=null}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=null}}, operator=>}}}}}}}}}}}}",
				"[result_value]",
				"{}",
				"{events={active=[[@16,72:77='active',<459>,1:72]], threshold=[[@13,55:63='threshold',<459>,1:55], [@14,64:64=')',<288>,1:64]], tags=[[@7,40:43='tags',<459>,1:40], [@14,64:64=')',<288>,1:64]]}}",
				"{update0={result_value=[[@3,18:29='result_value',<459>,1:18]]}}",
				"{def_update0={assignments={result_value=[{name=tags, table_ref=events}, {name=threshold, table_ref=events}]}, table_dictionary={events={active=[[@16,72:77='active',<459>,1:72]], threshold=[[@13,55:63='threshold',<459>,1:55], [@14,64:64=')',<288>,1:64]], tags=[[@7,40:43='tags',<459>,1:40], [@14,64:64=')',<288>,1:64]]}}, update_dictionary={result_value=[[@3,18:29='result_value',<459>,1:18]]}, target_table={events={result_value=[[@3,18:29='result_value',<459>,1:18]]}}, filters=[{name=active, table_ref=events}], lhs_unresolved_columns={result_value={column={name=result_value, table_ref=null}, locations=[[@3,18:29='result_value',<459>,1:18]]}}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "UPDATE");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:33) is recognized as Snowflake-specific grammar.",
						1, 33, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{UPDATE={update={table={alias=null, table=events}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}, assignments={1={set={column={name=result_value, table_ref=null}}, to={function={function_name=filter, parameters={1={column={name=tags, table_ref=null}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=null}}, operator=>}}}}}}}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[result_value]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={active=[[@16,72:77='active',<459>,1:72]], threshold=[[@13,55:63='threshold',<459>,1:55], [@14,64:64=')',<288>,1:64]], tags=[[@7,40:43='tags',<459>,1:40], [@14,64:64=')',<288>,1:64]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{update0={result_value=[[@3,18:29='result_value',<459>,1:18]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_update0={assignments={result_value=[{name=tags, table_ref=events}, {name=threshold, table_ref=events}]}, table_dictionary={events={active=[[@16,72:77='active',<459>,1:72]], threshold=[[@13,55:63='threshold',<459>,1:55], [@14,64:64=')',<288>,1:64]], tags=[[@7,40:43='tags',<459>,1:40], [@14,64:64=')',<288>,1:64]]}}, update_dictionary={result_value=[[@3,18:29='result_value',<459>,1:18]]}, target_table={events={result_value=[[@3,18:29='result_value',<459>,1:18]]}}, filters=[{name=active, table_ref=events}], lhs_unresolved_columns={result_value={column={name=result_value, table_ref=null}, locations=[[@3,18:29='result_value',<459>,1:18]]}}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC029_filterDELETESnippetV0Test() {
		final String query = "DELETE FROM events WHERE filter(tags, x -> x > threshold) IS NULL AND active = 0";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.delete_end_point();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:25) is recognized as Snowflake-specific grammar.",
						1, 25, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{DELETE={delete={table={alias=null, table=events}, where={and={1={condition={left={function={function_name=filter, parameters={1={column={name=tags, table_ref=null}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=null}}, operator=>}}}}}}}, operator=IS NULL}}, 2={condition={left={column={name=active, table_ref=null}}, right={literal=0}, operator==}}}}}}}",
				"[]",
				"{}",
				"{events={active=[[@17,70:75='active',<459>,1:70]], threshold=[[@12,47:55='threshold',<459>,1:47]], tags=[[@6,32:35='tags',<459>,1:32]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={active=[[@17,70:75='active',<459>,1:70]], threshold=[[@12,47:55='threshold',<459>,1:47]], tags=[[@6,32:35='tags',<459>,1:32]]}}, filters=[{name=tags, table_ref=events}, {name=threshold, table_ref=events}, {name=active, table_ref=events}], interface=null}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "DELETE");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:25) is recognized as Snowflake-specific grammar.",
						1, 25, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{DELETE={delete={table={alias=null, table=events}, where={and={1={condition={left={function={function_name=filter, parameters={1={column={name=tags, table_ref=null}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=null}}, operator=>}}}}}}}, operator=IS NULL}}, 2={condition={left={column={name=active, table_ref=null}}, right={literal=0}, operator==}}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={active=[[@17,70:75='active',<459>,1:70]], threshold=[[@12,47:55='threshold',<459>,1:47]], tags=[[@6,32:35='tags',<459>,1:32]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_delete0={query_dictionary={}, table_dictionary={events={active=[[@17,70:75='active',<459>,1:70]], threshold=[[@12,47:55='threshold',<459>,1:47]], tags=[[@6,32:35='tags',<459>,1:32]]}}, filters=[{name=tags, table_ref=events}, {name=threshold, table_ref=events}, {name=active, table_ref=events}], interface=null}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC029_filterDDLSnippetV0Test() {
		final String query = "CREATE VIEW v_filter AS SELECT a.id, filter(a.tags, x -> x > a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.ddl();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:37) is recognized as Snowflake-specific grammar.",
						1, 37, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{DDL={create={type=VIEW, name={table=v_filter}, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[]",
				"{}",
				"{events={active=[[@29,110:110='a',<459>,1:110]], threshold=[[@19,61:61='a',<459>,1:61]], id=[[@5,31:31='a',<459>,1:31]], tags=[[@11,44:44='a',<459>,1:44]]}}",
				"{query0={result_value=[[@24,77:88='result_value',<459>,1:77]], id=[[@7,33:34='id',<459>,1:33]]}}",
				"{def_create1={def_query0={query_dictionary={result_value=[[@24,77:88='result_value',<459>,1:77]], id=[[@7,33:34='id',<459>,1:33]]}, table_dictionary={events={active=[[@29,110:110='a',<459>,1:110]], threshold=[[@19,61:61='a',<459>,1:61]], id=[[@5,31:31='a',<459>,1:31]], tags=[[@11,44:44='a',<459>,1:44]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "DDL");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:37) is recognized as Snowflake-specific grammar.",
						1, 37, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{DDL={create={type=VIEW, name={table=v_filter}, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={active=[[@29,110:110='a',<459>,1:110]], threshold=[[@19,61:61='a',<459>,1:61]], id=[[@5,31:31='a',<459>,1:31]], tags=[[@11,44:44='a',<459>,1:44]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{query0={result_value=[[@24,77:88='result_value',<459>,1:77]], id=[[@7,33:34='id',<459>,1:33]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_create1={def_query0={query_dictionary={result_value=[[@24,77:88='result_value',<459>,1:77]], id=[[@7,33:34='id',<459>,1:33]]}, table_dictionary={events={active=[[@29,110:110='a',<459>,1:110]], threshold=[[@19,61:61='a',<459>,1:61]], id=[[@5,31:31='a',<459>,1:31]], tags=[[@11,44:44='a',<459>,1:44]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC029_filterSCRIPTSnippetV0Test() {
		final String query = "SELECT a.id, filter(a.tags, x -> x > a.threshold) AS result_value FROM events a WHERE a.active = 1;\nSELECT b.id FROM orders b WHERE b.active = 1;\nSELECT a.id, filter(a.tags, x -> x > a.threshold) AS result_value FROM events a WHERE a.active = 1;";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.script();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 2,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:3 c:13) is recognized as Snowflake-specific grammar.",
						3, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SCRIPT={1={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, 2={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=orders}}, where={condition={left={column={name=active, table_ref=b}}, right={literal=1}, operator==}}}, 3={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}",
				"[]",
				"{SCRIPT={1={}, 2={}, 3={}}}",
				"{SCRIPT={1={events={active=[[@25,86:86='a',<459>,1:86]], threshold=[[@15,37:37='a',<459>,1:37]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, 2={orders={active=[[@39,132:132='b',<459>,2:32]], id=[[@32,107:107='b',<459>,2:7]]}}, 3={events={active=[[@70,232:232='a',<459>,3:86]], threshold=[[@60,183:183='a',<459>,3:37]], id=[[@46,153:153='a',<459>,3:7]], tags=[[@52,166:166='a',<459>,3:20]]}}}}",
				"{SCRIPT={1={query0={result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}}, 2={query0={id=[[@34,109:110='id',<459>,2:9]]}}, 3={query0={result_value=[[@65,199:210='result_value',<459>,3:53]], id=[[@48,155:156='id',<459>,3:9]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,86:86='a',<459>,1:86]], threshold=[[@15,37:37='a',<459>,1:37]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}, 2={def_query0={query_dictionary={id=[[@34,109:110='id',<459>,2:9]]}, table_dictionary={orders={active=[[@39,132:132='b',<459>,2:32]], id=[[@32,107:107='b',<459>,2:7]]}}, filters=[{name=active, table_ref=b}], interface={id=[{name=id, table_ref=b}]}, table_alias={b=orders}}}, 3={def_query0={query_dictionary={result_value=[[@65,199:210='result_value',<459>,3:53]], id=[[@48,155:156='id',<459>,3:9]]}, table_dictionary={events={active=[[@70,232:232='a',<459>,3:86]], threshold=[[@60,183:183='a',<459>,3:37]], id=[[@46,153:153='a',<459>,3:7]], tags=[[@52,166:166='a',<459>,3:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "SCRIPT");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'filter' at (l:3 c:13) is recognized as Snowflake-specific grammar.",
						3, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{SCRIPT={1={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, 2={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=orders}}, where={condition={left={column={name=active, table_ref=b}}, right={literal=1}, operator==}}}, 3={select={1={column={name=id, table_ref=a}}, 2={function={function_name=filter, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={condition={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=>}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{SCRIPT={1={}, 2={}, 3={}}}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{SCRIPT={1={events={active=[[@25,86:86='a',<459>,1:86]], threshold=[[@15,37:37='a',<459>,1:37]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, 2={orders={active=[[@39,132:132='b',<459>,2:32]], id=[[@32,107:107='b',<459>,2:7]]}}, 3={events={active=[[@70,232:232='a',<459>,3:86]], threshold=[[@60,183:183='a',<459>,3:37]], id=[[@46,153:153='a',<459>,3:7]], tags=[[@52,166:166='a',<459>,3:20]]}}}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{SCRIPT={1={query0={result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}}, 2={query0={id=[[@34,109:110='id',<459>,2:9]]}}, 3={query0={result_value=[[@65,199:210='result_value',<459>,3:53]], id=[[@48,155:156='id',<459>,3:9]]}}}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{SCRIPT={1={def_query0={query_dictionary={result_value=[[@20,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,86:86='a',<459>,1:86]], threshold=[[@15,37:37='a',<459>,1:37]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}, 2={def_query0={query_dictionary={id=[[@34,109:110='id',<459>,2:9]]}, table_dictionary={orders={active=[[@39,132:132='b',<459>,2:32]], id=[[@32,107:107='b',<459>,2:7]]}}, filters=[{name=active, table_ref=b}], interface={id=[{name=id, table_ref=b}]}, table_alias={b=orders}}}, 3={def_query0={query_dictionary={result_value=[[@65,199:210='result_value',<459>,3:53]], id=[[@48,155:156='id',<459>,3:9]]}, table_dictionary={events={active=[[@70,232:232='a',<459>,3:86]], threshold=[[@60,183:183='a',<459>,3:37]], id=[[@46,153:153='a',<459>,3:7]], tags=[[@52,166:166='a',<459>,3:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC029_filterTrailingCommaSyntaxV0Test() {
		final String query = "SELECT filter(a.tags, x -> x > a.threshold,) AS result_value FROM events a";
		final SQLSelectParserParser parser = parse(query);
		parser.sql();
		final errorhandling.ParseErrorCollector collector =
				(errorhandling.ParseErrorCollector) parser.getErrorHandler();
		Assert.assertEquals("Parser error count is wrong", 3, collector.getErrorCount());
		final java.util.List<ParseDiagnostic> diagnostics = new java.util.ArrayList<>(collector.getDiagnostics());
		diagnostics.addAll(collectParserListenerDiagnostics(parser));
		Assert.assertEquals("Parser diagnostics are wrong", Arrays.asList(
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"REPORT_ERROR",
						"Line 1:42 - unexpected input: ',' in rule value_expression: SELECT filter(a.tags, x -> x > a.threshold,) AS result_value FROM events a",
						1, 42, "ParseErrorCollector", "value_expression", ",", false, "parse.strategy", "NoViableAltException", java.util.Map.of("contextSnippet", "SELECT filter(a.tags, x -> x > a.threshold,) AS result_value FROM events a", "parserRules", "value_expression,select_item,select_list", "syntaxClass", "GRAMMAR_GAP")),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER",
						"Line 1:42 - Syntax error, attempting recovery",
						1, 42, "ParseErrorCollector", null, ",", true, "parse.strategy", "NoViableAltException", null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER_INLINE",
						"Line 1:13 - Invalid syntax near '('",
						1, 13, "ParseErrorCollector", null, "(", true, "parse.strategy", null, null)), diagnostics);
	}

	@Test
	public void arC029_filterMissingParenthesisSyntaxV0Test() {
		final String query = "SELECT filter(a.tags, x -> x > a.threshold AS result_value FROM events a";
		final SQLSelectParserParser parser = parse(query);
		parser.sql();
		final errorhandling.ParseErrorCollector collector =
				(errorhandling.ParseErrorCollector) parser.getErrorHandler();
		Assert.assertEquals("Parser error count is wrong", 3, collector.getErrorCount());
		final java.util.List<ParseDiagnostic> diagnostics = new java.util.ArrayList<>(collector.getDiagnostics());
		diagnostics.addAll(collectParserListenerDiagnostics(parser));
		Assert.assertEquals("Parser diagnostics are wrong", Arrays.asList(
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"REPORT_ERROR",
						"Line 1:43 - unexpected input: 'AS' in rule value_expression: SELECT filter(a.tags, x -> x > a.threshold AS result_value FROM events a",
						1, 43, "ParseErrorCollector", "value_expression", "AS", false, "parse.strategy", "NoViableAltException", java.util.Map.of("contextSnippet", "SELECT filter(a.tags, x -> x > a.threshold AS result_value FROM events a", "parserRules", "value_expression,select_item,select_list", "syntaxClass", "GRAMMAR_GAP")),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER",
						"Line 1:43 - Syntax error, attempting recovery",
						1, 43, "ParseErrorCollector", null, "AS", true, "parse.strategy", "NoViableAltException", null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER_INLINE",
						"Line 1:13 - Invalid syntax near '('",
						1, 13, "ParseErrorCollector", null, "(", true, "parse.strategy", null, null)), diagnostics);
	}

	@Test
	public void arC029_filterMissingLambdaBodySyntaxV0Test() {
		final String query = "SELECT filter(a.tags, x -> ) AS result_value FROM events a";
		final SQLSelectParserParser parser = parse(query);
		parser.sql();
		final errorhandling.ParseErrorCollector collector =
				(errorhandling.ParseErrorCollector) parser.getErrorHandler();
		Assert.assertEquals("Parser error count is wrong", 3, collector.getErrorCount());
		final java.util.List<ParseDiagnostic> diagnostics = new java.util.ArrayList<>(collector.getDiagnostics());
		diagnostics.addAll(collectParserListenerDiagnostics(parser));
		Assert.assertEquals("Parser diagnostics are wrong", Arrays.asList(
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"REPORT_ERROR",
						"Line 1:27 - unexpected input: ')' in rule value_expression: SELECT filter(a.tags, x -> ) AS result_value FROM events a",
						1, 27, "ParseErrorCollector", "value_expression", ")", false, "parse.strategy", "NoViableAltException", java.util.Map.of("contextSnippet", "SELECT filter(a.tags, x -> ) AS result_value FROM events a", "parserRules", "value_expression,select_item,select_list", "syntaxClass", "GRAMMAR_GAP")),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER",
						"Line 1:27 - Syntax error, attempting recovery",
						1, 27, "ParseErrorCollector", null, ")", true, "parse.strategy", "NoViableAltException", null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER_INLINE",
						"Line 1:13 - Invalid syntax near '('",
						1, 13, "ParseErrorCollector", null, "(", true, "parse.strategy", null, null)), diagnostics);
	}

	@Test
	public void arC030_transformSelectPlacementV0Test() {
		final String query = "SELECT a.id, transform(a.tags, x -> x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@25,89:89='a',<459>,1:89]], threshold=[[@15,40:40='a',<459>,1:40]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}",
				"{query0={result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,89:89='a',<459>,1:89]], threshold=[[@15,40:40='a',<459>,1:40]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformWherePlacementV0Test() {
		final String query = "SELECT a.id, a.label FROM events a WHERE transform(a.tags, x -> x + a.threshold) IS NOT NULL AND a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:41) is recognized as Snowflake-specific grammar.",
						1, 41, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=a}}}, from={table={alias=a, table=events}}, where={and={1={condition={left={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[id, label]",
				"{}",
				"{events={active=[[@30,97:97='a',<459>,1:97]], threshold=[[@22,68:68='a',<459>,1:68]], id=[[@1,7:7='a',<459>,1:7]], label=[[@5,13:13='a',<459>,1:13]], tags=[[@14,51:51='a',<459>,1:51]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={events={active=[[@30,97:97='a',<459>,1:97]], threshold=[[@22,68:68='a',<459>,1:68]], id=[[@1,7:7='a',<459>,1:7]], label=[[@5,13:13='a',<459>,1:13]], tags=[[@14,51:51='a',<459>,1:51]]}}, filters=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformJoinPlacementV0Test() {
		final String query = "SELECT a.id, b.label FROM events a JOIN labels b ON transform(a.tags, x -> x + a.threshold) IS NOT NULL AND a.id = b.event_id";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:52) is recognized as Snowflake-specific grammar.",
						1, 52, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={and={1={condition={left={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=id, table_ref=a}}, right={column={name=event_id, table_ref=b}}, operator==}}}}}, 3={table={alias=b, table=labels}}}}}}",
				"[id, label]",
				"{}",
				"{events={threshold=[[@25,79:79='a',<459>,1:79]], id=[[@1,7:7='a',<459>,1:7], [@33,108:108='a',<459>,1:108]], tags=[[@17,62:62='a',<459>,1:62]]}, labels={event_id=[[@37,115:115='b',<459>,1:115]], label=[[@5,13:13='b',<459>,1:13]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={events={threshold=[[@25,79:79='a',<459>,1:79]], id=[[@1,7:7='a',<459>,1:7], [@33,108:108='a',<459>,1:108]], tags=[[@17,62:62='a',<459>,1:62]]}, labels={event_id=[[@37,115:115='b',<459>,1:115]], label=[[@5,13:13='b',<459>,1:13]]}}, filters=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}, {name=id, table_ref=a}, {name=event_id, table_ref=b}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=labels}}}");
	}

	@Test
	public void arC030_transformHavingPlacementV0Test() {
		final String query = "SELECT a.id, count(*) AS total FROM events a GROUP BY a.id, a.tags, a.threshold HAVING transform(a.tags, x -> x + a.threshold) IS NOT NULL";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:87) is recognized as Snowflake-specific grammar.",
						1, 87, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}, alias=total}}, having={condition={left={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}}, operator=IS NOT NULL}}, from={table={alias=a, table=events}}, groupby={1={column={name=id, table_ref=a}}, 2={column={name=tags, table_ref=a}}, 3={column={name=threshold, table_ref=a}}}}}",
				"[total, id]",
				"{}",
				"{events={threshold=[[@24,68:68='a',<459>,1:68], [@38,114:114='a',<459>,1:114]], *=[[@7,19:19='*',<291>,1:19]], id=[[@1,7:7='a',<459>,1:7], [@16,54:54='a',<459>,1:54]], tags=[[@20,60:60='a',<459>,1:60], [@30,97:97='a',<459>,1:97]]}}",
				"{query0={total=[[@10,25:29='total',<459>,1:25]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={total=[[@10,25:29='total',<459>,1:25]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={*=[[@7,19:19='*',<291>,1:19]], threshold=[[@24,68:68='a',<459>,1:68], [@38,114:114='a',<459>,1:114]], id=[[@1,7:7='a',<459>,1:7], [@16,54:54='a',<459>,1:54]], tags=[[@20,60:60='a',<459>,1:60], [@30,97:97='a',<459>,1:97]]}}, grouped_by=[{name=id, table_ref=a}, {name=tags, table_ref=a}, {name=threshold, table_ref=a}], filters=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], interface={total=[], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformQualifyPlacementV0Test() {
		final String query = "SELECT a.id, row_number() over (partition by a.group_id order by a.id) AS rn FROM events a QUALIFY transform(a.tags, x -> x + a.threshold) IS NOT NULL AND rn <= 5";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:99) is recognized as Snowflake-specific grammar.",
						1, 99, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={alias=rn, window_function={over={partition_by={1={column={name=group_id, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=id, table_ref=a}}, sort_order=ASC}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=a, table=events}}, qualify={and={1={condition={left={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=rn, table_ref=null}}, right={literal=5}, operator=<=}}}}}}",
				"[id, rn]",
				"{}",
				"{events={group_id=[[@12,45:45='a',<459>,1:45]], threshold=[[@37,126:126='a',<459>,1:126]], id=[[@1,7:7='a',<459>,1:7], [@17,65:65='a',<459>,1:65]], tags=[[@29,109:109='a',<459>,1:109]]}}",
				"{query0={rn=[[@22,74:75='rn',<459>,1:74], [@45,155:156='rn',<459>,1:155]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={window_ordered_by=[{name=id, table_ref=a}], query_dictionary={id=[[@3,9:10='id',<459>,1:9]], rn=[[@22,74:75='rn',<459>,1:74], [@45,155:156='rn',<459>,1:155]]}, table_dictionary={events={group_id=[[@12,45:45='a',<459>,1:45]], threshold=[[@37,126:126='a',<459>,1:126]], id=[[@1,7:7='a',<459>,1:7], [@17,65:65='a',<459>,1:65]], tags=[[@29,109:109='a',<459>,1:109]]}}, window_partition_by=[{name=group_id, table_ref=a}], filters=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}, {name=rn, table_ref=query0}], interface={id=[{name=id, table_ref=a}], rn=[{name=group_id, table_ref=a}, {name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformOrderByPlacementV0Test() {
		final String query = "SELECT a.id FROM events a WHERE a.active = 1 ORDER BY transform(a.tags, x -> x + a.threshold), a.id";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:54) is recognized as Snowflake-specific grammar.",
						1, 54, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, orderby={1={null_order=null, predicand={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=id, table_ref=a}}, sort_order=ASC}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id]",
				"{}",
				"{events={active=[[@8,32:32='a',<459>,1:32]], threshold=[[@25,81:81='a',<459>,1:81]], id=[[@1,7:7='a',<459>,1:7], [@30,95:95='a',<459>,1:95]], tags=[[@17,64:64='a',<459>,1:64]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@8,32:32='a',<459>,1:32]], threshold=[[@25,81:81='a',<459>,1:81]], id=[[@1,7:7='a',<459>,1:7], [@30,95:95='a',<459>,1:95]], tags=[[@17,64:64='a',<459>,1:64]]}}, ordered_by=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}, {name=id, table_ref=a}], filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformValuesPlacementV0Test() {
		final String query = "VALUES (7, transform([1, NULL, 3], x -> x + 2))";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:21) is recognized as Snowflake-specific grammar.",
						1, 21, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={values={matrix={1={row={1={literal=7}, 2={function={function_name=transform, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={null_literal=null}, 3={literal=3}}}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={literal=2}, operator=+}}}}}}}}}}}}}",
				"[$1, $2]",
				"{}",
				"{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[], $2=[]}}}");
	}

	@Test
	public void arC030_transformInsertPlacementV0Test() {
		final String query = "INSERT INTO results (id, result_value) SELECT a.id, transform(a.tags, x -> x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:52) is recognized as Snowflake-specific grammar.",
						1, 52, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}}, target_table={table={alias=null, table=results}}, columns={1={column={name=id, table_ref=null}}, 2={column={name=result_value, table_ref=null}}}}}}",
				"[result_value, id]",
				"{}",
				"{results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, events={active=[[@33,128:128='a',<459>,1:128]], threshold=[[@23,79:79='a',<459>,1:79]], id=[[@9,46:46='a',<459>,1:46]], tags=[[@15,62:62='a',<459>,1:62]]}}",
				"{query0={result_value=[[@28,95:106='result_value',<459>,1:95]], id=[[@11,48:49='id',<459>,1:48]]}, insert1={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}",
				"{def_insert1={query_dictionary={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, table_dictionary={results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}, def_query0={query_dictionary={result_value=[[@28,95:106='result_value',<459>,1:95]], id=[[@11,48:49='id',<459>,1:48]]}, table_dictionary={events={active=[[@33,128:128='a',<459>,1:128]], threshold=[[@23,79:79='a',<459>,1:79]], id=[[@9,46:46='a',<459>,1:46]], tags=[[@15,62:62='a',<459>,1:62]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={id=[{name=id, table_ref=query0}], result_value=[{name=result_value, table_ref=query0}]}}}");
	}

	@Test
	public void arC030_transformUpdatePlacementV0Test() {
		final String query = "UPDATE events SET result_value = transform(tags, x -> x + threshold) WHERE active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:33) is recognized as Snowflake-specific grammar.",
						1, 33, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={update={table={alias=null, table=events}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}, assignments={1={set={column={name=result_value, table_ref=null}}, to={function={function_name=transform, parameters={1={column={name=tags, table_ref=null}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=null}}, operator=+}}}}}}}}}}}}",
				"[result_value]",
				"{}",
				"{events={active=[[@16,75:80='active',<459>,1:75]], threshold=[[@13,58:66='threshold',<459>,1:58], [@14,67:67=')',<288>,1:67]], tags=[[@7,43:46='tags',<459>,1:43], [@14,67:67=')',<288>,1:67]]}}",
				"{update0={result_value=[[@3,18:29='result_value',<459>,1:18]]}}",
				"{def_update0={assignments={result_value=[{name=tags, table_ref=events}, {name=threshold, table_ref=events}]}, table_dictionary={events={active=[[@16,75:80='active',<459>,1:75]], threshold=[[@13,58:66='threshold',<459>,1:58], [@14,67:67=')',<288>,1:67]], tags=[[@7,43:46='tags',<459>,1:43], [@14,67:67=')',<288>,1:67]]}}, update_dictionary={result_value=[[@3,18:29='result_value',<459>,1:18]]}, target_table={events={result_value=[[@3,18:29='result_value',<459>,1:18]]}}, filters=[{name=active, table_ref=events}], lhs_unresolved_columns={result_value={column={name=result_value, table_ref=null}, locations=[[@3,18:29='result_value',<459>,1:18]]}}}}");
	}

	@Test
	public void arC030_transformDeletePlacementV0Test() {
		final String query = "DELETE FROM events WHERE transform(tags, x -> x + threshold) IS NULL AND active = 0";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:25) is recognized as Snowflake-specific grammar.",
						1, 25, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={delete={table={alias=null, table=events}, where={and={1={condition={left={function={function_name=transform, parameters={1={column={name=tags, table_ref=null}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=null}}, operator=+}}}}}}}, operator=IS NULL}}, 2={condition={left={column={name=active, table_ref=null}}, right={literal=0}, operator==}}}}}}}",
				"[]",
				"{}",
				"{events={active=[[@17,73:78='active',<459>,1:73]], threshold=[[@12,50:58='threshold',<459>,1:50]], tags=[[@6,35:38='tags',<459>,1:35]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={active=[[@17,73:78='active',<459>,1:73]], threshold=[[@12,50:58='threshold',<459>,1:50]], tags=[[@6,35:38='tags',<459>,1:35]]}}, filters=[{name=tags, table_ref=events}, {name=threshold, table_ref=events}, {name=active, table_ref=events}], interface=null}}");
	}

	@Test
	public void arC030_transformDdlPlacementV0Test() {
		final String query = "CREATE VIEW v_transform AS SELECT a.id, transform(a.tags, x -> x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.DdlContext tree = parser.ddl();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:40) is recognized as Snowflake-specific grammar.",
						1, 40, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{DDL={create={type=VIEW, name={table=v_transform}, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[]",
				"{}",
				"{events={active=[[@29,116:116='a',<459>,1:116]], threshold=[[@19,67:67='a',<459>,1:67]], id=[[@5,34:34='a',<459>,1:34]], tags=[[@11,50:50='a',<459>,1:50]]}}",
				"{query0={result_value=[[@24,83:94='result_value',<459>,1:83]], id=[[@7,36:37='id',<459>,1:36]]}}",
				"{def_create1={def_query0={query_dictionary={result_value=[[@24,83:94='result_value',<459>,1:83]], id=[[@7,36:37='id',<459>,1:36]]}, table_dictionary={events={active=[[@29,116:116='a',<459>,1:116]], threshold=[[@19,67:67='a',<459>,1:67]], id=[[@5,34:34='a',<459>,1:34]], tags=[[@11,50:50='a',<459>,1:50]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}");
	}

	@Test
	public void arC030_transformScriptPlacementV0Test() {
		final String query = "SELECT a.id, transform(a.tags, x -> x + a.threshold) AS result_value FROM events a WHERE a.active = 1;\nSELECT b.id FROM orders b WHERE b.active = 1;\nSELECT a.id, transform(a.tags, x -> x + a.threshold) AS result_value FROM events a WHERE a.active = 1;";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.ScriptContext tree = parser.script();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 2,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:3 c:13) is recognized as Snowflake-specific grammar.",
						3, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SCRIPT={1={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, 2={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=orders}}, where={condition={left={column={name=active, table_ref=b}}, right={literal=1}, operator==}}}, 3={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}",
				"[]",
				"{SCRIPT={1={}, 2={}, 3={}}}",
				"{SCRIPT={1={events={active=[[@25,89:89='a',<459>,1:89]], threshold=[[@15,40:40='a',<459>,1:40]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, 2={orders={active=[[@39,135:135='b',<459>,2:32]], id=[[@32,110:110='b',<459>,2:7]]}}, 3={events={active=[[@70,238:238='a',<459>,3:89]], threshold=[[@60,189:189='a',<459>,3:40]], id=[[@46,156:156='a',<459>,3:7]], tags=[[@52,172:172='a',<459>,3:23]]}}}}",
				"{SCRIPT={1={query0={result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}}, 2={query0={id=[[@34,112:113='id',<459>,2:9]]}}, 3={query0={result_value=[[@65,205:216='result_value',<459>,3:56]], id=[[@48,158:159='id',<459>,3:9]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,89:89='a',<459>,1:89]], threshold=[[@15,40:40='a',<459>,1:40]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}, 2={def_query0={query_dictionary={id=[[@34,112:113='id',<459>,2:9]]}, table_dictionary={orders={active=[[@39,135:135='b',<459>,2:32]], id=[[@32,110:110='b',<459>,2:7]]}}, filters=[{name=active, table_ref=b}], interface={id=[{name=id, table_ref=b}]}, table_alias={b=orders}}}, 3={def_query0={query_dictionary={result_value=[[@65,205:216='result_value',<459>,3:56]], id=[[@48,158:159='id',<459>,3:9]]}, table_dictionary={events={active=[[@70,238:238='a',<459>,3:89]], threshold=[[@60,189:189='a',<459>,3:40]], id=[[@46,156:156='a',<459>,3:7]], tags=[[@52,172:172='a',<459>,3:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}}");
	}

	@Test
	public void arC030_transformArrayQualifiedV0Test() {
		final String query = "SELECT a.id, transform(a.tags, x -> x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@25,89:89='a',<459>,1:89]], threshold=[[@15,40:40='a',<459>,1:40]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}",
				"{query0={result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,89:89='a',<459>,1:89]], threshold=[[@15,40:40='a',<459>,1:40]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformArrayUnqualifiedV0Test() {
		final String query = "SELECT a.id, transform(tags, x -> x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=null}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@23,87:87='a',<459>,1:87]], threshold=[[@13,38:38='a',<459>,1:38]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:26='tags',<459>,1:23]]}}",
				"{query0={result_value=[[@18,54:65='result_value',<459>,1:54]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,54:65='result_value',<459>,1:54]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@23,87:87='a',<459>,1:87]], threshold=[[@13,38:38='a',<459>,1:38]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:26='tags',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=events}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformArrayAmbiguousV0Test() {
		final String query = "SELECT a.id, transform(tags, x -> x + a.threshold) AS result_value FROM events a JOIN labels b ON a.id = b.event_id";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.SEVERE_WARNING,
						"AMBIGUOUS_COLUMN_REFERENCE",
						"Ambiguous column reference 'tags' at (l:1 c:23). Possible sources: [events, labels]",
						1, 23, "SqlASTWalkerHelper", null, "tags", true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [tags [(l:1 c:23)]]",
						1, 23, "SqlASTWalkerHelper", null, "tags", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=null}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=event_id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=labels}}}}}}",
				"[result_value, id]",
				"{}",
				"{events={threshold=[[@13,38:38='a',<459>,1:38]], id=[[@1,7:7='a',<459>,1:7], [@26,98:98='a',<459>,1:98]]}, labels={event_id=[[@30,105:105='b',<459>,1:105]]}}",
				"{query0={result_value=[[@18,54:65='result_value',<459>,1:54]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,54:65='result_value',<459>,1:54]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={threshold=[[@13,38:38='a',<459>,1:38]], id=[[@1,7:7='a',<459>,1:7], [@26,98:98='a',<459>,1:98]]}, labels={event_id=[[@30,105:105='b',<459>,1:105]]}}, filters=[{name=id, table_ref=a}, {name=event_id, table_ref=b}], interface={result_value=[{name=tags, table_ref=null}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events, b=labels}}}");
	}

	@Test
	public void arC030_transformArrayMissingAliasV0Test() {
		final String query = "SELECT a.id, transform(z.tags, x -> x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'tags' at (l:1 c:56). No alias or table called 'z'.",
						1, 56, "SqlASTWalkerHelper", null, "tags", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=z}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@25,89:89='a',<459>,1:89]], threshold=[[@15,40:40='a',<459>,1:40]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,89:89='a',<459>,1:89]], threshold=[[@15,40:40='a',<459>,1:40]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=z}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformArrayHiddenAliasV0Test() {
		final String query = "SELECT q.id, transform(hidden.tags, x -> x + a.threshold) AS result_value FROM (SELECT hidden.id, hidden.tags, hidden.threshold FROM events hidden) q JOIN events a ON q.id = a.id";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'tags' at (l:1 c:61). No alias or table called 'hidden'.",
						1, 61, "SqlASTWalkerHelper", null, "tags", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=q}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=hidden}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={join={1={table={alias=q, query={select={1={column={name=id, table_ref=hidden}}, 2={column={name=tags, table_ref=hidden}}, 3={column={name=threshold, table_ref=hidden}}}, from={table={alias=hidden, table=events}}}}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=q}}, right={column={name=id, table_ref=a}}, operator==}}}, 3={table={alias=a, table=events}}}}}}",
				"[result_value, id]",
				"{}",
				"{events={threshold=[[@32,111:116='hidden',<459>,1:111], [@15,45:45='a',<459>,1:45]], id=[[@24,87:92='hidden',<459>,1:87], [@48,174:174='a',<459>,1:174]], tags=[[@28,98:103='hidden',<459>,1:98]]}}",
				"{query0={threshold=[[@34,118:126='threshold',<459>,1:118]], id=[[@26,94:95='id',<459>,1:94], [@1,7:7='q',<459>,1:7], [@44,167:167='q',<459>,1:167]], tags=[[@30,105:108='tags',<459>,1:105]]}, query1={result_value=[[@20,61:72='result_value',<459>,1:61]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query1={query_dictionary={result_value=[[@20,61:72='result_value',<459>,1:61]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={threshold=[[@15,45:45='a',<459>,1:45]], id=[[@48,174:174='a',<459>,1:174]]}}, def_query0={query_dictionary={threshold=[[@34,118:126='threshold',<459>,1:118]], id=[[@26,94:95='id',<459>,1:94], [@1,7:7='q',<459>,1:7], [@44,167:167='q',<459>,1:167]], tags=[[@30,105:108='tags',<459>,1:105]]}, table_dictionary={events={threshold=[[@32,111:116='hidden',<459>,1:111], [@15,45:45='a',<459>,1:45]], id=[[@24,87:92='hidden',<459>,1:87], [@48,174:174='a',<459>,1:174]], tags=[[@28,98:103='hidden',<459>,1:98]]}}, interface={threshold=[{name=threshold, table_ref=hidden}], id=[{name=id, table_ref=hidden}], tags=[{name=tags, table_ref=hidden}]}, table_alias={hidden=events}}, filters=[{name=id, table_ref=q}, {name=id, table_ref=a}], interface={result_value=[{name=tags, table_ref=hidden}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=q}]}, table_alias={q=query0, a=events}}}");
	}

	@Test
	public void arC030_transformArrayUnresolvedV0Test() {
		final String query = "SELECT transform(tags, x -> x + 1) AS result_value";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [tags [(l:1 c:17)]]",
						1, 17, "SqlASTWalkerHelper", null, "tags", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={function={function_name=transform, parameters={1={column={name=tags, table_ref=null}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={literal=1}, operator=+}}}}}}, alias=result_value}}}}",
				"[result_value]",
				"{}",
				"{}",
				"{query0={result_value=[[@12,38:49='result_value',<459>,1:38]]}}",
				"{def_query0={query_dictionary={result_value=[[@12,38:49='result_value',<459>,1:38]]}, interface={result_value=[{name=tags, table_ref=null}]}}}");
	}

	@Test
	public void arC030_transformArrayPredicandVariableV0Test() {
		final String query = "SELECT a.id, transform(<tags_expr>, x -> x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={substitution={name=<tags_expr>, type=predicand}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<tags_expr>=predicand}",
				"{events={active=[[@23,94:94='a',<459>,1:94]], threshold=[[@13,45:45='a',<459>,1:45]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@18,61:72='result_value',<459>,1:61]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,61:72='result_value',<459>,1:61]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@23,94:94='a',<459>,1:94]], threshold=[[@13,45:45='a',<459>,1:45]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=<tags_expr>, type=predicand}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformArrayColumnVariableV0Test() {
		final String query = "SELECT a.id, transform(a.<tags_col>, x -> x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={substitution={name=<tags_col>, type=column}, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<tags_col>=column}",
				"{events={active=[[@25,95:95='a',<459>,1:95]], threshold=[[@15,46:46='a',<459>,1:46]], id=[[@1,7:7='a',<459>,1:7]], <tags_col>=[[@7,23:23='a',<459>,1:23]]}}",
				"{query0={result_value=[[@20,62:73='result_value',<459>,1:62]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,62:73='result_value',<459>,1:62]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,95:95='a',<459>,1:95]], threshold=[[@15,46:46='a',<459>,1:46]], id=[[@1,7:7='a',<459>,1:7]], <tags_col>=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{substitution={name=<tags_col>, type=column}, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformArrayNullV0Test() {
		final String query = "SELECT a.id, transform(NULL, x -> x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={null_literal=null}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@23,87:87='a',<459>,1:87]], threshold=[[@13,38:38='a',<459>,1:38]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@18,54:65='result_value',<459>,1:54]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,54:65='result_value',<459>,1:54]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@23,87:87='a',<459>,1:87]], threshold=[[@13,38:38='a',<459>,1:38]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformBodyQualifiedV0Test() {
		final String query = "SELECT a.id, transform(a.tags, x -> x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@25,89:89='a',<459>,1:89]], threshold=[[@15,40:40='a',<459>,1:40]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}",
				"{query0={result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,89:89='a',<459>,1:89]], threshold=[[@15,40:40='a',<459>,1:40]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformBodyUnqualifiedV0Test() {
		final String query = "SELECT a.id, transform(a.tags, x -> x + threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=null}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@23,87:87='a',<459>,1:87]], threshold=[[@15,40:48='threshold',<459>,1:40]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}",
				"{query0={result_value=[[@18,54:65='result_value',<459>,1:54]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,54:65='result_value',<459>,1:54]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@23,87:87='a',<459>,1:87]], threshold=[[@15,40:48='threshold',<459>,1:40]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=events}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformBodyAmbiguousV0Test() {
		final String query = "SELECT a.id, transform(a.tags, x -> x + threshold) AS result_value FROM events a JOIN labels b ON a.id = b.event_id";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.SEVERE_WARNING,
						"AMBIGUOUS_COLUMN_REFERENCE",
						"Ambiguous column reference 'threshold' at (l:1 c:40). Possible sources: [events, labels]",
						1, 40, "SqlASTWalkerHelper", null, "threshold", true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [threshold [(l:1 c:40)]]",
						1, 40, "SqlASTWalkerHelper", null, "threshold", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=null}}, operator=+}}}}}}, alias=result_value}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=event_id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=labels}}}}}}",
				"[result_value, id]",
				"{}",
				"{events={id=[[@1,7:7='a',<459>,1:7], [@26,98:98='a',<459>,1:98]], tags=[[@7,23:23='a',<459>,1:23]]}, labels={event_id=[[@30,105:105='b',<459>,1:105]]}}",
				"{query0={result_value=[[@18,54:65='result_value',<459>,1:54]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,54:65='result_value',<459>,1:54]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={id=[[@1,7:7='a',<459>,1:7], [@26,98:98='a',<459>,1:98]], tags=[[@7,23:23='a',<459>,1:23]]}, labels={event_id=[[@30,105:105='b',<459>,1:105]]}}, filters=[{name=id, table_ref=a}, {name=event_id, table_ref=b}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=null}], id=[{name=id, table_ref=a}]}, table_alias={a=events, b=labels}}}");
	}

	@Test
	public void arC030_transformBodyMissingAliasV0Test() {
		final String query = "SELECT a.id, transform(a.tags, x -> x + z.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'threshold' at (l:1 c:56). No alias or table called 'z'.",
						1, 56, "SqlASTWalkerHelper", null, "threshold", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=z}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@25,89:89='a',<459>,1:89]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}",
				"{query0={result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,89:89='a',<459>,1:89]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=z}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformBodyHiddenAliasV0Test() {
		final String query = "SELECT q.id, transform(a.tags, x -> x + hidden.threshold) AS result_value FROM (SELECT hidden.id, hidden.tags, hidden.threshold FROM events hidden) q JOIN events a ON q.id = a.id";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'threshold' at (l:1 c:61). No alias or table called 'hidden'.",
						1, 61, "SqlASTWalkerHelper", null, "threshold", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=q}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=hidden}}, operator=+}}}}}}, alias=result_value}}, from={join={1={table={alias=q, query={select={1={column={name=id, table_ref=hidden}}, 2={column={name=tags, table_ref=hidden}}, 3={column={name=threshold, table_ref=hidden}}}, from={table={alias=hidden, table=events}}}}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=q}}, right={column={name=id, table_ref=a}}, operator==}}}, 3={table={alias=a, table=events}}}}}}",
				"[result_value, id]",
				"{}",
				"{events={threshold=[[@32,111:116='hidden',<459>,1:111]], id=[[@24,87:92='hidden',<459>,1:87], [@48,174:174='a',<459>,1:174]], tags=[[@28,98:103='hidden',<459>,1:98], [@7,23:23='a',<459>,1:23]]}}",
				"{query0={threshold=[[@34,118:126='threshold',<459>,1:118]], id=[[@26,94:95='id',<459>,1:94], [@1,7:7='q',<459>,1:7], [@44,167:167='q',<459>,1:167]], tags=[[@30,105:108='tags',<459>,1:105]]}, query1={result_value=[[@20,61:72='result_value',<459>,1:61]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query1={query_dictionary={result_value=[[@20,61:72='result_value',<459>,1:61]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={id=[[@48,174:174='a',<459>,1:174]], tags=[[@7,23:23='a',<459>,1:23]]}}, def_query0={query_dictionary={threshold=[[@34,118:126='threshold',<459>,1:118]], id=[[@26,94:95='id',<459>,1:94], [@1,7:7='q',<459>,1:7], [@44,167:167='q',<459>,1:167]], tags=[[@30,105:108='tags',<459>,1:105]]}, table_dictionary={events={threshold=[[@32,111:116='hidden',<459>,1:111]], id=[[@24,87:92='hidden',<459>,1:87], [@48,174:174='a',<459>,1:174]], tags=[[@28,98:103='hidden',<459>,1:98], [@7,23:23='a',<459>,1:23]]}}, interface={threshold=[{name=threshold, table_ref=hidden}], id=[{name=id, table_ref=hidden}], tags=[{name=tags, table_ref=hidden}]}, table_alias={hidden=events}}, filters=[{name=id, table_ref=q}, {name=id, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=hidden}], id=[{name=id, table_ref=q}]}, table_alias={q=query0, a=events}}}");
	}

	@Test
	public void arC030_transformBodyUnresolvedV0Test() {
		final String query = "SELECT transform([1,2], x -> x + threshold) AS result_value";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:17) is recognized as Snowflake-specific grammar.",
						1, 17, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [threshold [(l:1 c:33)]]",
						1, 33, "SqlASTWalkerHelper", null, "threshold", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={function={function_name=transform, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=null}}, operator=+}}}}}}, alias=result_value}}}}",
				"[result_value]",
				"{}",
				"{}",
				"{query0={result_value=[[@16,47:58='result_value',<459>,1:47]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,47:58='result_value',<459>,1:47]]}, interface={result_value=[{name=threshold, table_ref=null}]}}}");
	}

	@Test
	public void arC030_transformBodyPredicandVariableV0Test() {
		final String query = "SELECT a.id, transform(a.tags, x -> x + <threshold_expr>) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={substitution={name=<threshold_expr>, type=predicand}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<threshold_expr>=predicand}",
				"{events={active=[[@23,94:94='a',<459>,1:94]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}",
				"{query0={result_value=[[@18,61:72='result_value',<459>,1:61]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,61:72='result_value',<459>,1:61]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@23,94:94='a',<459>,1:94]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=<threshold_expr>, type=predicand}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformBodyColumnVariableV0Test() {
		final String query = "SELECT a.id, transform(a.tags, x -> x + a.<threshold_col>) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={substitution={name=<threshold_col>, type=column}, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<threshold_col>=column}",
				"{events={<threshold_col>=[[@15,40:40='a',<459>,1:40]], active=[[@25,95:95='a',<459>,1:95]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}",
				"{query0={result_value=[[@20,62:73='result_value',<459>,1:62]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,62:73='result_value',<459>,1:62]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={<threshold_col>=[[@15,40:40='a',<459>,1:40]], active=[[@25,95:95='a',<459>,1:95]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {substitution={name=<threshold_col>, type=column}, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformBodyNullV0Test() {
		final String query = "SELECT a.id, transform(a.tags, x -> x + NULL) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={null_literal=null}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@23,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}",
				"{query0={result_value=[[@18,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@23,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformLocalOnlyNoSourceV0Test() {
		final String query = "SELECT transform([1,2,3], x -> x * 2) AS result_value";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:17) is recognized as Snowflake-specific grammar.",
						1, 17, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={function={function_name=transform, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}, 3={literal=3}}}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={literal=2}, operator=*}}}}}}, alias=result_value}}}}",
				"[result_value]",
				"{}",
				"{}",
				"{query0={result_value=[[@18,41:52='result_value',<459>,1:41]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,41:52='result_value',<459>,1:41]]}, interface={result_value=[]}}}");
	}

	@Test
	public void arC030_transformTypedBindingsV0Test() {
		final String query = "SELECT a.id, transform(a.tags, x INT -> x * 2) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x, data_type={type=INT}}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={literal=2}, operator=*}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@24,83:83='a',<459>,1:83]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}",
				"{query0={result_value=[[@19,50:61='result_value',<459>,1:50]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@19,50:61='result_value',<459>,1:50]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@24,83:83='a',<459>,1:83]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformParenthesizedBindingsV0Test() {
		final String query = "SELECT a.id, transform(a.tags, (x) -> x * 2) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={literal=2}, operator=*}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@25,81:81='a',<459>,1:81]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}",
				"{query0={result_value=[[@20,48:59='result_value',<459>,1:48]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,48:59='result_value',<459>,1:48]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,81:81='a',<459>,1:81]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformLocalJsonPathV0Test() {
		final String query = "SELECT a.id, transform(a.tags, x -> x:amount + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0, json_path=amount}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@27,96:96='a',<459>,1:96]], threshold=[[@17,47:47='a',<459>,1:47]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}",
				"{query0={result_value=[[@22,63:74='result_value',<459>,1:63]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@22,63:74='result_value',<459>,1:63]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@27,96:96='a',<459>,1:96]], threshold=[[@17,47:47='a',<459>,1:47]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformLocalNameMatchesColumnV0Test() {
		final String query = "SELECT a.id, transform(a.tags, x -> x + a.x) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=x, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={x=[[@15,40:40='a',<459>,1:40]], active=[[@25,81:81='a',<459>,1:81]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}",
				"{query0={result_value=[[@20,48:59='result_value',<459>,1:48]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,48:59='result_value',<459>,1:48]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={x=[[@15,40:40='a',<459>,1:40]], active=[[@25,81:81='a',<459>,1:81]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=x, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformLocalNameMatchesTableAliasV0Test() {
		final String query = "SELECT x.id, transform(x.tags, x -> x + x.threshold) AS result_value FROM events x WHERE x.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=x}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=x}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=x}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=x, table=events}}, where={condition={left={column={name=active, table_ref=x}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@25,89:89='x',<459>,1:89]], threshold=[[@15,40:40='x',<459>,1:40]], id=[[@1,7:7='x',<459>,1:7]], tags=[[@7,23:23='x',<459>,1:23]]}}",
				"{query0={result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,89:89='x',<459>,1:89]], threshold=[[@15,40:40='x',<459>,1:40]], id=[[@1,7:7='x',<459>,1:7]], tags=[[@7,23:23='x',<459>,1:23]]}}, filters=[{name=active, table_ref=x}], interface={result_value=[{name=tags, table_ref=x}, {name=threshold, table_ref=x}], id=[{name=id, table_ref=x}]}, table_alias={x=events}}}");
	}

	@Test
	public void arC030_transformNoLeakAfterCallV0Test() {
		final String query = "SELECT a.id, transform(a.tags, x -> x * 2) AS result_value, x AS physical_x FROM events a";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={literal=2}, operator=*}}}}}}, alias=result_value}, 3={column={name=x, table_ref=null}, alias=physical_x}}, from={table={alias=a, table=events}}}}",
				"[physical_x, result_value, id]",
				"{}",
				"{events={x=[[@20,60:60='x',<459>,1:60]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}",
				"{query0={result_value=[[@18,46:57='result_value',<459>,1:46]], physical_x=[[@22,65:74='physical_x',<459>,1:65]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={physical_x=[[@22,65:74='physical_x',<459>,1:65]], result_value=[[@18,46:57='result_value',<459>,1:46]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={x=[[@20,60:60='x',<459>,1:60]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, interface={physical_x=[{name=x, table_ref=events}], result_value=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformNoBindingInArrayOperandV0Test() {
		final String query = "SELECT a.id, transform(x, x -> x * 2) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=x, table_ref=null}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={literal=2}, operator=*}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={x=[[@7,23:23='x',<459>,1:23]], active=[[@21,74:74='a',<459>,1:74]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@16,41:52='result_value',<459>,1:41]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,41:52='result_value',<459>,1:41]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={x=[[@7,23:23='x',<459>,1:23]], active=[[@21,74:74='a',<459>,1:74]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=x, table_ref=events}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformNestedShadowingV0Test() {
		final String query = "SELECT a.id, transform(a.tags, x -> transform(a.other_tags, x -> x + a.threshold)) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 2,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:36) is recognized as Snowflake-specific grammar.",
						1, 36, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={function={function_name=transform, parameters={1={column={name=other_tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@34,119:119='a',<459>,1:119]], threshold=[[@23,69:69='a',<459>,1:69]], id=[[@1,7:7='a',<459>,1:7]], other_tags=[[@15,46:46='a',<459>,1:46]], tags=[[@7,23:23='a',<459>,1:23]]}}",
				"{query0={result_value=[[@29,86:97='result_value',<459>,1:86]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@29,86:97='result_value',<459>,1:86]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@34,119:119='a',<459>,1:119]], threshold=[[@23,69:69='a',<459>,1:69]], id=[[@1,7:7='a',<459>,1:7]], other_tags=[[@15,46:46='a',<459>,1:46]], tags=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=other_tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformNestedOuterCaptureV0Test() {
		final String query = "SELECT a.id, transform(a.tags, x -> transform(a.other_tags, y -> y + x + a.threshold)) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 2,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:36) is recognized as Snowflake-specific grammar.",
						1, 36, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={function={function_name=transform, parameters={1={column={name=other_tags, table_ref=a}}, 2={lambda={parameters={1={name=y}}, body={calc={left={calc={left={lambda_reference={name=y, depth=0}}, right={lambda_reference={name=x, depth=1}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@36,123:123='a',<459>,1:123]], threshold=[[@25,73:73='a',<459>,1:73]], id=[[@1,7:7='a',<459>,1:7]], other_tags=[[@15,46:46='a',<459>,1:46]], tags=[[@7,23:23='a',<459>,1:23]]}}",
				"{query0={result_value=[[@31,90:101='result_value',<459>,1:90]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@31,90:101='result_value',<459>,1:90]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@36,123:123='a',<459>,1:123]], threshold=[[@25,73:73='a',<459>,1:73]], id=[[@1,7:7='a',<459>,1:7]], other_tags=[[@15,46:46='a',<459>,1:46]], tags=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=other_tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformEmptyArrayV0Test() {
		final String query = "SELECT a.id, transform([], x -> x * 2) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:23) is recognized as Snowflake-specific grammar.",
						1, 23, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={function={function_name=array_construct, parameters={}}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={literal=2}, operator=*}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@22,75:75='a',<459>,1:75]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@17,42:53='result_value',<459>,1:42]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@17,42:53='result_value',<459>,1:42]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@22,75:75='a',<459>,1:75]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformNullArrayV0Test() {
		final String query = "SELECT a.id, transform(NULL, x -> x * 2) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={null_literal=null}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={literal=2}, operator=*}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@21,77:77='a',<459>,1:77]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@16,44:55='result_value',<459>,1:44]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,44:55='result_value',<459>,1:44]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,77:77='a',<459>,1:77]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformNullBodyRuntimeBoundaryV0Test() {
		final String query = "SELECT a.id, transform(a.tags, x -> NULL) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={null_literal=null}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@21,78:78='a',<459>,1:78]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}",
				"{query0={result_value=[[@16,45:56='result_value',<459>,1:45]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,45:56='result_value',<459>,1:45]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,78:78='a',<459>,1:78]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformBareBodySubstitutionV0Test() {
		final String query = "SELECT a.id, transform(a.tags, x -> <lambda_body>) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={substitution={name=<lambda_body>, type=predicand}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<lambda_body>=predicand}",
				"{events={active=[[@21,87:87='a',<459>,1:87]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}",
				"{query0={result_value=[[@16,54:65='result_value',<459>,1:54]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,54:65='result_value',<459>,1:54]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,87:87='a',<459>,1:87]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=<lambda_body>, type=predicand}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformCombinedSubstitutionsV0Test() {
		final String query = "SELECT a.id, transform(<array_expr>, x -> x + <external_expr>) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={substitution={name=<array_expr>, type=predicand}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={substitution={name=<external_expr>, type=predicand}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<external_expr>=predicand, <array_expr>=predicand}",
				"{events={active=[[@21,99:99='a',<459>,1:99]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@16,66:77='result_value',<459>,1:66]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,66:77='result_value',<459>,1:66]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,99:99='a',<459>,1:99]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=<array_expr>, type=predicand}, {name=<external_expr>, type=predicand}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformDerivedOutputV0Test() {
		final String query = "SELECT q.id, q.result_value FROM (SELECT a.id, transform(a.tags, x -> x + a.threshold) AS result_value FROM events a WHERE a.active = 1) q WHERE q.result_value IS NOT NULL";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:47) is recognized as Snowflake-specific grammar.",
						1, 47, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=q}}, 2={column={name=result_value, table_ref=q}}}, from={table={alias=q, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}, where={condition={left={column={name=result_value, table_ref=q}}, operator=IS NOT NULL}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@35,123:123='a',<459>,1:123]], threshold=[[@25,74:74='a',<459>,1:74]], id=[[@11,41:41='a',<459>,1:41]], tags=[[@17,57:57='a',<459>,1:57]]}}",
				"{query0={result_value=[[@30,90:101='result_value',<459>,1:90], [@5,13:13='q',<459>,1:13], [@43,145:145='q',<459>,1:145]], id=[[@13,43:44='id',<459>,1:43], [@1,7:7='q',<459>,1:7]]}, query1={result_value=[[@7,15:26='result_value',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query1={query_dictionary={result_value=[[@7,15:26='result_value',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}, def_query0={query_dictionary={result_value=[[@30,90:101='result_value',<459>,1:90], [@5,13:13='q',<459>,1:13], [@43,145:145='q',<459>,1:145]], id=[[@13,43:44='id',<459>,1:43], [@1,7:7='q',<459>,1:7]]}, table_dictionary={events={active=[[@35,123:123='a',<459>,1:123]], threshold=[[@25,74:74='a',<459>,1:74]], id=[[@11,41:41='a',<459>,1:41]], tags=[[@17,57:57='a',<459>,1:57]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=result_value, table_ref=q}], interface={result_value=[{name=result_value, table_ref=q}], id=[{name=id, table_ref=q}]}, table_alias={q=query0}}}");
	}

	@Test
	public void arC030_transformCteRenameChainV0Test() {
		final String query = "WITH seed AS (SELECT a.id, transform(a.tags, x -> x + a.threshold) AS result_value FROM events a WHERE a.active = 1), renamed AS (SELECT id, result_value AS exported_value FROM seed) SELECT r.id, r.exported_value FROM renamed r";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:27) is recognized as Snowflake-specific grammar.",
						1, 27, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=seed}, 2={cte={select={1={column={name=id, table_ref=null}}, 2={column={name=result_value, table_ref=null}, alias=exported_value}}, from={table={alias=null, table=seed}}}, alias=renamed}}, query={select={1={column={name=id, table_ref=r}}, 2={column={name=exported_value, table_ref=r}}}, from={table={alias=r, table=renamed}}}}}",
				"[exported_value, id]",
				"{}",
				"{events={active=[[@29,103:103='a',<459>,1:103]], threshold=[[@19,54:54='a',<459>,1:54]], id=[[@5,21:21='a',<459>,1:21]], tags=[[@11,37:37='a',<459>,1:37]]}}",
				"{query0={result_value=[[@24,70:81='result_value',<459>,1:70], [@42,141:152='result_value',<459>,1:141]], id=[[@7,23:24='id',<459>,1:23], [@40,137:138='id',<459>,1:137]]}, query1={exported_value=[[@44,157:170='exported_value',<459>,1:157], [@53,196:196='r',<459>,1:196]], id=[[@40,137:138='id',<459>,1:137], [@49,190:190='r',<459>,1:190]]}, query2={exported_value=[[@55,198:211='exported_value',<459>,1:198]], id=[[@51,192:193='id',<459>,1:192]]}}",
				"{def_query2={context_list={seed=query0, renamed=query1, r=query1}, query_dictionary={exported_value=[[@55,198:211='exported_value',<459>,1:198]], id=[[@51,192:193='id',<459>,1:192]]}, def_query1={context_list={seed=query0}, query_dictionary={exported_value=[[@44,157:170='exported_value',<459>,1:157], [@53,196:196='r',<459>,1:196]], id=[[@40,137:138='id',<459>,1:137], [@49,190:190='r',<459>,1:190]]}, interface={exported_value=[{name=result_value, table_ref=query0}], id=[{name=id, table_ref=query0}]}, table_alias={seed=query0}}, def_query0={query_dictionary={result_value=[[@24,70:81='result_value',<459>,1:70], [@42,141:152='result_value',<459>,1:141]], id=[[@7,23:24='id',<459>,1:23], [@40,137:138='id',<459>,1:137]]}, table_dictionary={events={active=[[@29,103:103='a',<459>,1:103]], threshold=[[@19,54:54='a',<459>,1:54]], id=[[@5,21:21='a',<459>,1:21]], tags=[[@11,37:37='a',<459>,1:37]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={exported_value=[{name=exported_value, table_ref=r}], id=[{name=id, table_ref=r}]}, table_alias={r=query1, seed=query0, renamed=query1}}}");
	}

	@Test
	public void arC030_transformCteMissingExportV0Test() {
		final String query = "WITH seed AS (SELECT a.id FROM events a) SELECT a.id, transform(a.tags, x -> x * 2) AS result_value FROM seed a";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:54) is recognized as Snowflake-specific grammar.",
						1, 54, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
						"Qualified column 'tags' at (l:1 c:64) was not found in output interface of query alias 'a'.",
						1, 64, "SqlASTWalkerHelper", null, "a.tags", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}}, alias=seed}}, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={literal=2}, operator=*}}}}}}, alias=result_value}}, from={table={alias=a, table=seed}}}}}",
				"[result_value, id]",
				"{}",
				"{events={id=[[@5,21:21='a',<459>,1:21]]}}",
				"{query0={id=[[@7,23:24='id',<459>,1:23], [@13,48:48='a',<459>,1:48]]}, query1={result_value=[[@30,87:98='result_value',<459>,1:87]], id=[[@15,50:51='id',<459>,1:50]]}}",
				"{def_query1={context_list={seed=query0, a=query0}, query_dictionary={result_value=[[@30,87:98='result_value',<459>,1:87]], id=[[@15,50:51='id',<459>,1:50]]}, def_query0={query_dictionary={id=[[@7,23:24='id',<459>,1:23], [@13,48:48='a',<459>,1:48]]}, table_dictionary={events={id=[[@5,21:21='a',<459>,1:21]]}}, interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={result_value=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=query0, seed=query0}}}");
	}

	@Test
	public void arC030_transformSetOpSiblingIsolationV0Test() {
		final String query = "SELECT a.id, transform(a.tags, x -> x + a.threshold) AS result_value FROM events a WHERE a.active = 1 UNION ALL SELECT b.id AS other_id, b.stored_value AS other_value FROM archive_events b";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={union={1={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={select={1={column={name=id, table_ref=b}, alias=other_id}, 2={column={name=stored_value, table_ref=b}, alias=other_value}}, from={table={alias=b, table=archive_events}}}}}}",
				"[result_value, id]",
				"{}",
				"{archive_events={stored_value=[[@39,137:137='b',<459>,1:137]], id=[[@33,119:119='b',<459>,1:119]]}, events={active=[[@25,89:89='a',<459>,1:89]], threshold=[[@15,40:40='a',<459>,1:40]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}",
				"{query0={result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}, query1={other_value=[[@43,155:165='other_value',<459>,1:155]], other_id=[[@37,127:134='other_id',<459>,1:127]]}}",
				"{def_union2={def_query1={query_dictionary={other_value=[[@43,155:165='other_value',<459>,1:155]], other_id=[[@37,127:134='other_id',<459>,1:127]]}, table_dictionary={archive_events={stored_value=[[@39,137:137='b',<459>,1:137]], id=[[@33,119:119='b',<459>,1:119]]}}, setop=UNION, interface={other_value=[{name=stored_value, table_ref=b}], other_id=[{name=id, table_ref=b}]}, table_alias={b=archive_events}}, def_query0={query_dictionary={result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,89:89='a',<459>,1:89]], threshold=[[@15,40:40='a',<459>,1:40]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={result_value=query_column, id=query_column}}}");
	}

	@Test
	public void arC030_transformCorrelatedSubqueryV0Test() {
		final String query = "SELECT a.id FROM events a WHERE EXISTS (SELECT b.id FROM labels b WHERE b.event_id = a.id AND transform(a.tags, x -> x + a.threshold) IS NOT NULL)";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:94) is recognized as Snowflake-specific grammar.",
						1, 94, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}, where={exists={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=labels}}, where={and={1={condition={left={column={name=event_id, table_ref=b}}, right={column={name=id, table_ref=a}}, operator==}}, 2={condition={left={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}}, operator=IS NOT NULL}}}}, operator=EXISTS}}}}",
				"[id]",
				"{}",
				"{events={threshold=[[@36,121:121='a',<459>,1:121]], id=[[@1,7:7='a',<459>,1:7], [@22,85:85='a',<459>,1:85]], tags=[[@28,104:104='a',<459>,1:104]]}, labels={event_id=[[@18,72:72='b',<459>,1:72]], id=[[@11,47:47='b',<459>,1:47]]}}",
				"{query0={id=[[@13,49:50='id',<459>,1:49]]}, query2={id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query2={query_dictionary={id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={threshold=[[@36,121:121='a',<459>,1:121]], id=[[@1,7:7='a',<459>,1:7], [@22,85:85='a',<459>,1:85]], tags=[[@28,104:104='a',<459>,1:104]]}}, dependent_queries={exists1={query=query0, type=filters}}, def_query0={query_dictionary={id=[[@13,49:50='id',<459>,1:49]]}, table_dictionary={labels={event_id=[[@18,72:72='b',<459>,1:72]], id=[[@11,47:47='b',<459>,1:47]]}}, filters=[{name=event_id, table_ref=b}, {name=id, table_ref=a}, {name=tags, table_ref=a}, {name=threshold, table_ref=a}], interface={id=[{name=id, table_ref=b}]}, table_alias={b=labels}}, filters=[], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformGenericCompatibilityV0Test() {
		final String query = "SELECT a.id, transform(a.tags, a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 0,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=tags, table_ref=a}}, 2={column={name=threshold, table_ref=a}}}, function_name=transform}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@21,80:80='a',<459>,1:80]], threshold=[[@11,31:31='a',<459>,1:31]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}",
				"{query0={result_value=[[@16,47:58='result_value',<459>,1:47]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,47:58='result_value',<459>,1:47]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,80:80='a',<459>,1:80]], threshold=[[@11,31:31='a',<459>,1:31]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformNullLambdaCompatibilityV0Test() {
		final String query = "SELECT a.id, transform(a.tags, NULL) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 0,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=tags, table_ref=a}}, 2={null_literal=null}}, function_name=transform}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@19,73:73='a',<459>,1:73]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}",
				"{query0={result_value=[[@14,40:51='result_value',<459>,1:40]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@14,40:51='result_value',<459>,1:40]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@19,73:73='a',<459>,1:73]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformQualifiedUdfV0Test() {
		final String query = "SELECT a.id, util.transform(a.tags, a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 0,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={schema=util, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=threshold, table_ref=a}}}, function_name=transform}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@23,85:85='a',<459>,1:85]], threshold=[[@13,36:36='a',<459>,1:36]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@9,28:28='a',<459>,1:28]]}}",
				"{query0={result_value=[[@18,52:63='result_value',<459>,1:52]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,52:63='result_value',<459>,1:52]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@23,85:85='a',<459>,1:85]], threshold=[[@13,36:36='a',<459>,1:36]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@9,28:28='a',<459>,1:28]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformSameNamedColumnV0Test() {
		final String query = "SELECT a.id, a.transform AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 0,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=transform, table_ref=a}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={transform=[[@5,13:13='a',<459>,1:13]], active=[[@14,61:61='a',<459>,1:61]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@9,28:39='result_value',<459>,1:28]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@9,28:39='result_value',<459>,1:28]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={transform=[[@5,13:13='a',<459>,1:13]], active=[[@14,61:61='a',<459>,1:61]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=transform, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformUppercaseV0Test() {
		final String query = "SELECT a.id, TRANSFORM(a.tags, x -> x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=TRANSFORM, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@25,89:89='a',<459>,1:89]], threshold=[[@15,40:40='a',<459>,1:40]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}",
				"{query0={result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,89:89='a',<459>,1:89]], threshold=[[@15,40:40='a',<459>,1:40]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformMixedDialectV0Test() {
		final String query = "SELECT a.id, transform(a.tags, x -> x + a.threshold) AS result_value, extract(isodow from a.created_at) AS weekday FROM events a";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"STATEMENT_MIXED_DIALECT_GRAMMAR",
						"Statement mixes Snowflake-only and PostgreSQL-only grammar at (l:1 c:70); unlikely to run on either engine.",
						1, 70, "SqlASTWalkerHelper", null, null, false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}, 3={alias=weekday, datetime_function={function_name=extract, part_form=KEYWORD, part=isodow, source={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}",
				"[weekday, result_value, id]",
				"{}",
				"{events={created_at=[[@26,90:90='a',<459>,1:90]], threshold=[[@15,40:40='a',<459>,1:40]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}",
				"{query0={weekday=[[@31,107:113='weekday',<459>,1:107]], result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={weekday=[[@31,107:113='weekday',<459>,1:107]], result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={created_at=[[@26,90:90='a',<459>,1:90]], threshold=[[@15,40:40='a',<459>,1:40]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, interface={weekday=[{name=created_at, table_ref=a}], result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC030_transformPREDICANDSnippetV0Test() {
		final String query = "transform([1,2], x -> x * 2)";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.predicand_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:10) is recognized as Snowflake-specific grammar.",
						1, 10, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{PREDICAND={function={function_name=transform, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={literal=2}, operator=*}}}}}}}}",
				"[]",
				"{}",
				"{}",
				"{}",
				"{}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "PREDICAND");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:10) is recognized as Snowflake-specific grammar.",
						1, 10, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{PREDICAND={function={function_name=transform, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={literal=2}, operator=*}}}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC030_transformCONDITIONSnippetV0Test() {
		final String query = "transform([1,2], x -> x * 2) IS NOT NULL AND 1 = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.condition_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:10) is recognized as Snowflake-specific grammar.",
						1, 10, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{CONDITION={and={1={condition={left={function={function_name=transform, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={literal=2}, operator=*}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={literal=1}, right={literal=1}, operator==}}}}}",
				"[]",
				"{}",
				"{}",
				"{}",
				"{}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "CONDITION");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:10) is recognized as Snowflake-specific grammar.",
						1, 10, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{CONDITION={and={1={condition={left={function={function_name=transform, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={literal=2}, operator=*}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={literal=1}, right={literal=1}, operator==}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC030_transformQUERYSnippetV0Test() {
		final String query = "SELECT a.id, transform(a.tags, x -> x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.query_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{QUERY={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@25,89:89='a',<459>,1:89]], threshold=[[@15,40:40='a',<459>,1:40]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}",
				"{query0={result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,89:89='a',<459>,1:89]], threshold=[[@15,40:40='a',<459>,1:40]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "QUERY");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{QUERY={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[result_value, id]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={active=[[@25,89:89='a',<459>,1:89]], threshold=[[@15,40:40='a',<459>,1:40]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{query0={result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_query0={query_dictionary={result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,89:89='a',<459>,1:89]], threshold=[[@15,40:40='a',<459>,1:40]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC030_transformTUPLESnippetV0Test() {
		final String query = "(SELECT a.id, transform(a.tags, x -> x + a.threshold) AS result_value FROM events a WHERE a.active = 1)";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.tuple_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:14) is recognized as Snowflake-specific grammar.",
						1, 14, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{TUPLE={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@26,90:90='a',<459>,1:90]], threshold=[[@16,41:41='a',<459>,1:41]], id=[[@2,8:8='a',<459>,1:8]], tags=[[@8,24:24='a',<459>,1:24]]}}",
				"{query0={result_value=[[@21,57:68='result_value',<459>,1:57]], id=[[@4,10:11='id',<459>,1:10]]}}",
				"{def_query0={query_dictionary={result_value=[[@21,57:68='result_value',<459>,1:57]], id=[[@4,10:11='id',<459>,1:10]]}, table_dictionary={events={active=[[@26,90:90='a',<459>,1:90]], threshold=[[@16,41:41='a',<459>,1:41]], id=[[@2,8:8='a',<459>,1:8]], tags=[[@8,24:24='a',<459>,1:24]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, table_alias={query0=query0}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "TUPLE");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:14) is recognized as Snowflake-specific grammar.",
						1, 14, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{TUPLE={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[result_value, id]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={active=[[@26,90:90='a',<459>,1:90]], threshold=[[@16,41:41='a',<459>,1:41]], id=[[@2,8:8='a',<459>,1:8]], tags=[[@8,24:24='a',<459>,1:24]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{query0={result_value=[[@21,57:68='result_value',<459>,1:57]], id=[[@4,10:11='id',<459>,1:10]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_query0={query_dictionary={result_value=[[@21,57:68='result_value',<459>,1:57]], id=[[@4,10:11='id',<459>,1:10]]}, table_dictionary={events={active=[[@26,90:90='a',<459>,1:90]], threshold=[[@16,41:41='a',<459>,1:41]], id=[[@2,8:8='a',<459>,1:8]], tags=[[@8,24:24='a',<459>,1:24]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, table_alias={query0=query0}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC030_transformIN_LISTSnippetV0Test() {
		final String query = "(transform([1,2], x -> x * 2), NULL)";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.in_list_predicate_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:11) is recognized as Snowflake-specific grammar.",
						1, 11, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{IN_LIST={list={1={function={function_name=transform, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={literal=2}, operator=*}}}}}}}, 2={null_literal=null}}}}",
				"[]",
				"{}",
				"{}",
				"{}",
				"{}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "IN_LIST");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:11) is recognized as Snowflake-specific grammar.",
						1, 11, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{IN_LIST={list={1={function={function_name=transform, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={literal=2}, operator=*}}}}}}}, 2={null_literal=null}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC030_transformJOIN_EXTENSIONSnippetV0Test() {
		final String query = "JOIN events a ON transform(a.tags, x -> x + a.threshold) IS NOT NULL AND a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.join_extension_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:17) is recognized as Snowflake-specific grammar.",
						1, 17, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{JOIN_EXTENSION={1={join=JOIN, on={and={1={condition={left={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}, 2={table={alias=a, table=events}}}}",
				"[]",
				"{}",
				"{events={active=[[@22,73:73='a',<459>,1:73]], threshold=[[@14,44:44='a',<459>,1:44]], tags=[[@6,27:27='a',<459>,1:27]]}}",
				"{}",
				"{table_dictionary={events={active=[[@22,73:73='a',<459>,1:73]], threshold=[[@14,44:44='a',<459>,1:44]], tags=[[@6,27:27='a',<459>,1:27]]}}, filters=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}, {name=active, table_ref=a}], table_alias={a=events}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "JOIN_EXTENSION");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:17) is recognized as Snowflake-specific grammar.",
						1, 17, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{JOIN_EXTENSION={1={join=JOIN, on={and={1={condition={left={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}, 2={table={alias=a, table=events}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={active=[[@22,73:73='a',<459>,1:73]], threshold=[[@14,44:44='a',<459>,1:44]], tags=[[@6,27:27='a',<459>,1:27]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{table_dictionary={events={active=[[@22,73:73='a',<459>,1:73]], threshold=[[@14,44:44='a',<459>,1:44]], tags=[[@6,27:27='a',<459>,1:27]]}}, filters=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}, {name=active, table_ref=a}], table_alias={a=events}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC030_transformVALUESSnippetV0Test() {
		final String query = "VALUES (7, transform([1,2], x -> x * 2))";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.values_statement_end();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:21) is recognized as Snowflake-specific grammar.",
						1, 21, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{VALUES={values={matrix={1={row={1={literal=7}, 2={function={function_name=transform, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={literal=2}, operator=*}}}}}}}}}}}}}",
				"[$1, $2]",
				"{}",
				"{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[], $2=[]}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "VALUES");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:21) is recognized as Snowflake-specific grammar.",
						1, 21, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{VALUES={values={matrix={1={row={1={literal=7}, 2={function={function_name=transform, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={literal=2}, operator=*}}}}}}}}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[$1, $2]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{values0={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[], $2=[]}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC030_transformINSERTSnippetV0Test() {
		final String query = "INSERT INTO results (id, result_value) SELECT a.id, transform(a.tags, x -> x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.insert_end_point();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:52) is recognized as Snowflake-specific grammar.",
						1, 52, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{INSERT={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}}, target_table={table={alias=null, table=results}}, columns={1={column={name=id, table_ref=null}}, 2={column={name=result_value, table_ref=null}}}}}}",
				"[result_value, id]",
				"{}",
				"{results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, events={active=[[@33,128:128='a',<459>,1:128]], threshold=[[@23,79:79='a',<459>,1:79]], id=[[@9,46:46='a',<459>,1:46]], tags=[[@15,62:62='a',<459>,1:62]]}}",
				"{query0={result_value=[[@28,95:106='result_value',<459>,1:95]], id=[[@11,48:49='id',<459>,1:48]]}, insert1={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}",
				"{def_insert1={query_dictionary={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, table_dictionary={results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}, def_query0={query_dictionary={result_value=[[@28,95:106='result_value',<459>,1:95]], id=[[@11,48:49='id',<459>,1:48]]}, table_dictionary={events={active=[[@33,128:128='a',<459>,1:128]], threshold=[[@23,79:79='a',<459>,1:79]], id=[[@9,46:46='a',<459>,1:46]], tags=[[@15,62:62='a',<459>,1:62]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={id=[{name=id, table_ref=query0}], result_value=[{name=result_value, table_ref=query0}]}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "INSERT");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:52) is recognized as Snowflake-specific grammar.",
						1, 52, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{INSERT={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}}, target_table={table={alias=null, table=results}}, columns={1={column={name=id, table_ref=null}}, 2={column={name=result_value, table_ref=null}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[result_value, id]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, events={active=[[@33,128:128='a',<459>,1:128]], threshold=[[@23,79:79='a',<459>,1:79]], id=[[@9,46:46='a',<459>,1:46]], tags=[[@15,62:62='a',<459>,1:62]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{query0={result_value=[[@28,95:106='result_value',<459>,1:95]], id=[[@11,48:49='id',<459>,1:48]]}, insert1={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_insert1={query_dictionary={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, table_dictionary={results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}, def_query0={query_dictionary={result_value=[[@28,95:106='result_value',<459>,1:95]], id=[[@11,48:49='id',<459>,1:48]]}, table_dictionary={events={active=[[@33,128:128='a',<459>,1:128]], threshold=[[@23,79:79='a',<459>,1:79]], id=[[@9,46:46='a',<459>,1:46]], tags=[[@15,62:62='a',<459>,1:62]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={id=[{name=id, table_ref=query0}], result_value=[{name=result_value, table_ref=query0}]}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC030_transformUPDATESnippetV0Test() {
		final String query = "UPDATE events SET result_value = transform(tags, x -> x + threshold) WHERE active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.update_end_point();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:33) is recognized as Snowflake-specific grammar.",
						1, 33, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{UPDATE={update={table={alias=null, table=events}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}, assignments={1={set={column={name=result_value, table_ref=null}}, to={function={function_name=transform, parameters={1={column={name=tags, table_ref=null}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=null}}, operator=+}}}}}}}}}}}}",
				"[result_value]",
				"{}",
				"{events={active=[[@16,75:80='active',<459>,1:75]], threshold=[[@13,58:66='threshold',<459>,1:58], [@14,67:67=')',<288>,1:67]], tags=[[@7,43:46='tags',<459>,1:43], [@14,67:67=')',<288>,1:67]]}}",
				"{update0={result_value=[[@3,18:29='result_value',<459>,1:18]]}}",
				"{def_update0={assignments={result_value=[{name=tags, table_ref=events}, {name=threshold, table_ref=events}]}, table_dictionary={events={active=[[@16,75:80='active',<459>,1:75]], threshold=[[@13,58:66='threshold',<459>,1:58], [@14,67:67=')',<288>,1:67]], tags=[[@7,43:46='tags',<459>,1:43], [@14,67:67=')',<288>,1:67]]}}, update_dictionary={result_value=[[@3,18:29='result_value',<459>,1:18]]}, target_table={events={result_value=[[@3,18:29='result_value',<459>,1:18]]}}, filters=[{name=active, table_ref=events}], lhs_unresolved_columns={result_value={column={name=result_value, table_ref=null}, locations=[[@3,18:29='result_value',<459>,1:18]]}}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "UPDATE");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:33) is recognized as Snowflake-specific grammar.",
						1, 33, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{UPDATE={update={table={alias=null, table=events}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}, assignments={1={set={column={name=result_value, table_ref=null}}, to={function={function_name=transform, parameters={1={column={name=tags, table_ref=null}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=null}}, operator=+}}}}}}}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[result_value]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={active=[[@16,75:80='active',<459>,1:75]], threshold=[[@13,58:66='threshold',<459>,1:58], [@14,67:67=')',<288>,1:67]], tags=[[@7,43:46='tags',<459>,1:43], [@14,67:67=')',<288>,1:67]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{update0={result_value=[[@3,18:29='result_value',<459>,1:18]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_update0={assignments={result_value=[{name=tags, table_ref=events}, {name=threshold, table_ref=events}]}, table_dictionary={events={active=[[@16,75:80='active',<459>,1:75]], threshold=[[@13,58:66='threshold',<459>,1:58], [@14,67:67=')',<288>,1:67]], tags=[[@7,43:46='tags',<459>,1:43], [@14,67:67=')',<288>,1:67]]}}, update_dictionary={result_value=[[@3,18:29='result_value',<459>,1:18]]}, target_table={events={result_value=[[@3,18:29='result_value',<459>,1:18]]}}, filters=[{name=active, table_ref=events}], lhs_unresolved_columns={result_value={column={name=result_value, table_ref=null}, locations=[[@3,18:29='result_value',<459>,1:18]]}}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC030_transformDELETESnippetV0Test() {
		final String query = "DELETE FROM events WHERE transform(tags, x -> x + threshold) IS NULL AND active = 0";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.delete_end_point();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:25) is recognized as Snowflake-specific grammar.",
						1, 25, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{DELETE={delete={table={alias=null, table=events}, where={and={1={condition={left={function={function_name=transform, parameters={1={column={name=tags, table_ref=null}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=null}}, operator=+}}}}}}}, operator=IS NULL}}, 2={condition={left={column={name=active, table_ref=null}}, right={literal=0}, operator==}}}}}}}",
				"[]",
				"{}",
				"{events={active=[[@17,73:78='active',<459>,1:73]], threshold=[[@12,50:58='threshold',<459>,1:50]], tags=[[@6,35:38='tags',<459>,1:35]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={active=[[@17,73:78='active',<459>,1:73]], threshold=[[@12,50:58='threshold',<459>,1:50]], tags=[[@6,35:38='tags',<459>,1:35]]}}, filters=[{name=tags, table_ref=events}, {name=threshold, table_ref=events}, {name=active, table_ref=events}], interface=null}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "DELETE");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:25) is recognized as Snowflake-specific grammar.",
						1, 25, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{DELETE={delete={table={alias=null, table=events}, where={and={1={condition={left={function={function_name=transform, parameters={1={column={name=tags, table_ref=null}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=null}}, operator=+}}}}}}}, operator=IS NULL}}, 2={condition={left={column={name=active, table_ref=null}}, right={literal=0}, operator==}}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={active=[[@17,73:78='active',<459>,1:73]], threshold=[[@12,50:58='threshold',<459>,1:50]], tags=[[@6,35:38='tags',<459>,1:35]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_delete0={query_dictionary={}, table_dictionary={events={active=[[@17,73:78='active',<459>,1:73]], threshold=[[@12,50:58='threshold',<459>,1:50]], tags=[[@6,35:38='tags',<459>,1:35]]}}, filters=[{name=tags, table_ref=events}, {name=threshold, table_ref=events}, {name=active, table_ref=events}], interface=null}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC030_transformDDLSnippetV0Test() {
		final String query = "CREATE VIEW v_transform AS SELECT a.id, transform(a.tags, x -> x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.ddl();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:40) is recognized as Snowflake-specific grammar.",
						1, 40, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{DDL={create={type=VIEW, name={table=v_transform}, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[]",
				"{}",
				"{events={active=[[@29,116:116='a',<459>,1:116]], threshold=[[@19,67:67='a',<459>,1:67]], id=[[@5,34:34='a',<459>,1:34]], tags=[[@11,50:50='a',<459>,1:50]]}}",
				"{query0={result_value=[[@24,83:94='result_value',<459>,1:83]], id=[[@7,36:37='id',<459>,1:36]]}}",
				"{def_create1={def_query0={query_dictionary={result_value=[[@24,83:94='result_value',<459>,1:83]], id=[[@7,36:37='id',<459>,1:36]]}, table_dictionary={events={active=[[@29,116:116='a',<459>,1:116]], threshold=[[@19,67:67='a',<459>,1:67]], id=[[@5,34:34='a',<459>,1:34]], tags=[[@11,50:50='a',<459>,1:50]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "DDL");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:40) is recognized as Snowflake-specific grammar.",
						1, 40, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{DDL={create={type=VIEW, name={table=v_transform}, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={active=[[@29,116:116='a',<459>,1:116]], threshold=[[@19,67:67='a',<459>,1:67]], id=[[@5,34:34='a',<459>,1:34]], tags=[[@11,50:50='a',<459>,1:50]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{query0={result_value=[[@24,83:94='result_value',<459>,1:83]], id=[[@7,36:37='id',<459>,1:36]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_create1={def_query0={query_dictionary={result_value=[[@24,83:94='result_value',<459>,1:83]], id=[[@7,36:37='id',<459>,1:36]]}, table_dictionary={events={active=[[@29,116:116='a',<459>,1:116]], threshold=[[@19,67:67='a',<459>,1:67]], id=[[@5,34:34='a',<459>,1:34]], tags=[[@11,50:50='a',<459>,1:50]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC030_transformSCRIPTSnippetV0Test() {
		final String query = "SELECT a.id, transform(a.tags, x -> x + a.threshold) AS result_value FROM events a WHERE a.active = 1;\nSELECT b.id FROM orders b WHERE b.active = 1;\nSELECT a.id, transform(a.tags, x -> x + a.threshold) AS result_value FROM events a WHERE a.active = 1;";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.script();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 2,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:3 c:13) is recognized as Snowflake-specific grammar.",
						3, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SCRIPT={1={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, 2={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=orders}}, where={condition={left={column={name=active, table_ref=b}}, right={literal=1}, operator==}}}, 3={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}",
				"[]",
				"{SCRIPT={1={}, 2={}, 3={}}}",
				"{SCRIPT={1={events={active=[[@25,89:89='a',<459>,1:89]], threshold=[[@15,40:40='a',<459>,1:40]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, 2={orders={active=[[@39,135:135='b',<459>,2:32]], id=[[@32,110:110='b',<459>,2:7]]}}, 3={events={active=[[@70,238:238='a',<459>,3:89]], threshold=[[@60,189:189='a',<459>,3:40]], id=[[@46,156:156='a',<459>,3:7]], tags=[[@52,172:172='a',<459>,3:23]]}}}}",
				"{SCRIPT={1={query0={result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}}, 2={query0={id=[[@34,112:113='id',<459>,2:9]]}}, 3={query0={result_value=[[@65,205:216='result_value',<459>,3:56]], id=[[@48,158:159='id',<459>,3:9]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,89:89='a',<459>,1:89]], threshold=[[@15,40:40='a',<459>,1:40]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}, 2={def_query0={query_dictionary={id=[[@34,112:113='id',<459>,2:9]]}, table_dictionary={orders={active=[[@39,135:135='b',<459>,2:32]], id=[[@32,110:110='b',<459>,2:7]]}}, filters=[{name=active, table_ref=b}], interface={id=[{name=id, table_ref=b}]}, table_alias={b=orders}}}, 3={def_query0={query_dictionary={result_value=[[@65,205:216='result_value',<459>,3:56]], id=[[@48,158:159='id',<459>,3:9]]}, table_dictionary={events={active=[[@70,238:238='a',<459>,3:89]], threshold=[[@60,189:189='a',<459>,3:40]], id=[[@46,156:156='a',<459>,3:7]], tags=[[@52,172:172='a',<459>,3:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "SCRIPT");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'transform' at (l:3 c:13) is recognized as Snowflake-specific grammar.",
						3, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{SCRIPT={1={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, 2={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=orders}}, where={condition={left={column={name=active, table_ref=b}}, right={literal=1}, operator==}}}, 3={select={1={column={name=id, table_ref=a}}, 2={function={function_name=transform, parameters={1={column={name=tags, table_ref=a}}, 2={lambda={parameters={1={name=x}}, body={calc={left={lambda_reference={name=x, depth=0}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{SCRIPT={1={}, 2={}, 3={}}}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{SCRIPT={1={events={active=[[@25,89:89='a',<459>,1:89]], threshold=[[@15,40:40='a',<459>,1:40]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, 2={orders={active=[[@39,135:135='b',<459>,2:32]], id=[[@32,110:110='b',<459>,2:7]]}}, 3={events={active=[[@70,238:238='a',<459>,3:89]], threshold=[[@60,189:189='a',<459>,3:40]], id=[[@46,156:156='a',<459>,3:7]], tags=[[@52,172:172='a',<459>,3:23]]}}}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{SCRIPT={1={query0={result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}}, 2={query0={id=[[@34,112:113='id',<459>,2:9]]}}, 3={query0={result_value=[[@65,205:216='result_value',<459>,3:56]], id=[[@48,158:159='id',<459>,3:9]]}}}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{SCRIPT={1={def_query0={query_dictionary={result_value=[[@20,56:67='result_value',<459>,1:56]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@25,89:89='a',<459>,1:89]], threshold=[[@15,40:40='a',<459>,1:40]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,23:23='a',<459>,1:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}, 2={def_query0={query_dictionary={id=[[@34,112:113='id',<459>,2:9]]}, table_dictionary={orders={active=[[@39,135:135='b',<459>,2:32]], id=[[@32,110:110='b',<459>,2:7]]}}, filters=[{name=active, table_ref=b}], interface={id=[{name=id, table_ref=b}]}, table_alias={b=orders}}}, 3={def_query0={query_dictionary={result_value=[[@65,205:216='result_value',<459>,3:56]], id=[[@48,158:159='id',<459>,3:9]]}, table_dictionary={events={active=[[@70,238:238='a',<459>,3:89]], threshold=[[@60,189:189='a',<459>,3:40]], id=[[@46,156:156='a',<459>,3:7]], tags=[[@52,172:172='a',<459>,3:23]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC030_transformTrailingCommaSyntaxV0Test() {
		final String query = "SELECT transform(a.tags, x -> x + a.threshold,) AS result_value FROM events a";
		final SQLSelectParserParser parser = parse(query);
		parser.sql();
		final errorhandling.ParseErrorCollector collector =
				(errorhandling.ParseErrorCollector) parser.getErrorHandler();
		Assert.assertEquals("Parser error count is wrong", 3, collector.getErrorCount());
		final java.util.List<ParseDiagnostic> diagnostics = new java.util.ArrayList<>(collector.getDiagnostics());
		diagnostics.addAll(collectParserListenerDiagnostics(parser));
		Assert.assertEquals("Parser diagnostics are wrong", Arrays.asList(
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"REPORT_ERROR",
						"Line 1:45 - unexpected input: ',' in rule value_expression: SELECT transform(a.tags, x -> x + a.threshold,) AS result_value FROM events a",
						1, 45, "ParseErrorCollector", "value_expression", ",", false, "parse.strategy", "NoViableAltException", java.util.Map.of("contextSnippet", "SELECT transform(a.tags, x -> x + a.threshold,) AS result_value FROM events a", "parserRules", "value_expression,select_item,select_list", "syntaxClass", "GRAMMAR_GAP")),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER",
						"Line 1:45 - Syntax error, attempting recovery",
						1, 45, "ParseErrorCollector", null, ",", true, "parse.strategy", "NoViableAltException", null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER_INLINE",
						"Line 1:16 - Invalid syntax near '('",
						1, 16, "ParseErrorCollector", null, "(", true, "parse.strategy", null, null)), diagnostics);
	}

	@Test
	public void arC030_transformMissingParenthesisSyntaxV0Test() {
		final String query = "SELECT transform(a.tags, x -> x + a.threshold AS result_value FROM events a";
		final SQLSelectParserParser parser = parse(query);
		parser.sql();
		final errorhandling.ParseErrorCollector collector =
				(errorhandling.ParseErrorCollector) parser.getErrorHandler();
		Assert.assertEquals("Parser error count is wrong", 3, collector.getErrorCount());
		final java.util.List<ParseDiagnostic> diagnostics = new java.util.ArrayList<>(collector.getDiagnostics());
		diagnostics.addAll(collectParserListenerDiagnostics(parser));
		Assert.assertEquals("Parser diagnostics are wrong", Arrays.asList(
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"REPORT_ERROR",
						"Line 1:46 - unexpected input: 'AS' in rule value_expression: SELECT transform(a.tags, x -> x + a.threshold AS result_value FROM events a",
						1, 46, "ParseErrorCollector", "value_expression", "AS", false, "parse.strategy", "NoViableAltException", java.util.Map.of("contextSnippet", "SELECT transform(a.tags, x -> x + a.threshold AS result_value FROM events a", "parserRules", "value_expression,select_item,select_list", "syntaxClass", "GRAMMAR_GAP")),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER",
						"Line 1:46 - Syntax error, attempting recovery",
						1, 46, "ParseErrorCollector", null, "AS", true, "parse.strategy", "NoViableAltException", null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER_INLINE",
						"Line 1:16 - Invalid syntax near '('",
						1, 16, "ParseErrorCollector", null, "(", true, "parse.strategy", null, null)), diagnostics);
	}

	@Test
	public void arC030_transformMissingLambdaBodySyntaxV0Test() {
		final String query = "SELECT transform(a.tags, x -> ) AS result_value FROM events a";
		final SQLSelectParserParser parser = parse(query);
		parser.sql();
		final errorhandling.ParseErrorCollector collector =
				(errorhandling.ParseErrorCollector) parser.getErrorHandler();
		Assert.assertEquals("Parser error count is wrong", 3, collector.getErrorCount());
		final java.util.List<ParseDiagnostic> diagnostics = new java.util.ArrayList<>(collector.getDiagnostics());
		diagnostics.addAll(collectParserListenerDiagnostics(parser));
		Assert.assertEquals("Parser diagnostics are wrong", Arrays.asList(
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"REPORT_ERROR",
						"Line 1:30 - unexpected input: ')' in rule value_expression: SELECT transform(a.tags, x -> ) AS result_value FROM events a",
						1, 30, "ParseErrorCollector", "value_expression", ")", false, "parse.strategy", "NoViableAltException", java.util.Map.of("contextSnippet", "SELECT transform(a.tags, x -> ) AS result_value FROM events a", "parserRules", "value_expression,select_item,select_list", "syntaxClass", "GRAMMAR_GAP")),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER",
						"Line 1:30 - Syntax error, attempting recovery",
						1, 30, "ParseErrorCollector", null, ")", true, "parse.strategy", "NoViableAltException", null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER_INLINE",
						"Line 1:16 - Invalid syntax near '('",
						1, 16, "ParseErrorCollector", null, "(", true, "parse.strategy", null, null)), diagnostics);
	}

	@Test
	public void arC031_reduceSelectPlacementV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceWherePlacementV0Test() {
		final String query = "SELECT a.id, a.label FROM events a WHERE reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) IS NOT NULL AND a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:41) is recognized as Snowflake-specific grammar.",
						1, 41, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=a}}}, from={table={alias=a, table=events}}, where={and={1={condition={left={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[id, label]",
				"{}",
				"{events={initial_value=[[@18,56:56='a',<459>,1:56]], active=[[@40,124:124='a',<459>,1:124]], threshold=[[@32,95:95='a',<459>,1:95]], id=[[@1,7:7='a',<459>,1:7]], label=[[@5,13:13='a',<459>,1:13]], tags=[[@14,48:48='a',<459>,1:48]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={events={initial_value=[[@18,56:56='a',<459>,1:56]], active=[[@40,124:124='a',<459>,1:124]], threshold=[[@32,95:95='a',<459>,1:95]], id=[[@1,7:7='a',<459>,1:7]], label=[[@5,13:13='a',<459>,1:13]], tags=[[@14,48:48='a',<459>,1:48]]}}, filters=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceJoinPlacementV0Test() {
		final String query = "SELECT a.id, b.label FROM events a JOIN labels b ON reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) IS NOT NULL AND a.id = b.event_id";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:52) is recognized as Snowflake-specific grammar.",
						1, 52, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={and={1={condition={left={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=id, table_ref=a}}, right={column={name=event_id, table_ref=b}}, operator==}}}}}, 3={table={alias=b, table=labels}}}}}}",
				"[id, label]",
				"{}",
				"{events={initial_value=[[@21,67:67='a',<459>,1:67]], threshold=[[@35,106:106='a',<459>,1:106]], id=[[@1,7:7='a',<459>,1:7], [@43,135:135='a',<459>,1:135]], tags=[[@17,59:59='a',<459>,1:59]]}, labels={event_id=[[@47,142:142='b',<459>,1:142]], label=[[@5,13:13='b',<459>,1:13]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={events={initial_value=[[@21,67:67='a',<459>,1:67]], threshold=[[@35,106:106='a',<459>,1:106]], id=[[@1,7:7='a',<459>,1:7], [@43,135:135='a',<459>,1:135]], tags=[[@17,59:59='a',<459>,1:59]]}, labels={event_id=[[@47,142:142='b',<459>,1:142]], label=[[@5,13:13='b',<459>,1:13]]}}, filters=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}, {name=id, table_ref=a}, {name=event_id, table_ref=b}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=labels}}}");
	}

	@Test
	public void arC031_reduceHavingPlacementV0Test() {
		final String query = "SELECT a.id, count(*) AS total FROM events a GROUP BY a.id, a.tags, a.threshold, a.initial_value HAVING reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) IS NOT NULL";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:104) is recognized as Snowflake-specific grammar.",
						1, 104, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}, alias=total}}, having={condition={left={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}}, operator=IS NOT NULL}}, from={table={alias=a, table=events}}, groupby={1={column={name=id, table_ref=a}}, 2={column={name=tags, table_ref=a}}, 3={column={name=threshold, table_ref=a}}, 4={column={name=initial_value, table_ref=a}}}}}",
				"[total, id]",
				"{}",
				"{events={initial_value=[[@28,81:81='a',<459>,1:81], [@38,119:119='a',<459>,1:119]], threshold=[[@24,68:68='a',<459>,1:68], [@52,158:158='a',<459>,1:158]], *=[[@7,19:19='*',<291>,1:19]], id=[[@1,7:7='a',<459>,1:7], [@16,54:54='a',<459>,1:54]], tags=[[@20,60:60='a',<459>,1:60], [@34,111:111='a',<459>,1:111]]}}",
				"{query0={total=[[@10,25:29='total',<459>,1:25]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={total=[[@10,25:29='total',<459>,1:25]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@28,81:81='a',<459>,1:81], [@38,119:119='a',<459>,1:119]], *=[[@7,19:19='*',<291>,1:19]], threshold=[[@24,68:68='a',<459>,1:68], [@52,158:158='a',<459>,1:158]], id=[[@1,7:7='a',<459>,1:7], [@16,54:54='a',<459>,1:54]], tags=[[@20,60:60='a',<459>,1:60], [@34,111:111='a',<459>,1:111]]}}, grouped_by=[{name=id, table_ref=a}, {name=tags, table_ref=a}, {name=threshold, table_ref=a}, {name=initial_value, table_ref=a}], filters=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], interface={total=[], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceQualifyPlacementV0Test() {
		final String query = "SELECT a.id, row_number() over (partition by a.group_id order by a.id) AS rn FROM events a QUALIFY reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) IS NOT NULL AND rn <= 5";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:99) is recognized as Snowflake-specific grammar.",
						1, 99, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={alias=rn, window_function={over={partition_by={1={column={name=group_id, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=id, table_ref=a}}, sort_order=ASC}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=a, table=events}}, qualify={and={1={condition={left={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=rn, table_ref=null}}, right={literal=5}, operator=<=}}}}}}",
				"[id, rn]",
				"{}",
				"{events={initial_value=[[@33,114:114='a',<459>,1:114]], group_id=[[@12,45:45='a',<459>,1:45]], threshold=[[@47,153:153='a',<459>,1:153]], id=[[@1,7:7='a',<459>,1:7], [@17,65:65='a',<459>,1:65]], tags=[[@29,106:106='a',<459>,1:106]]}}",
				"{query0={rn=[[@22,74:75='rn',<459>,1:74], [@55,182:183='rn',<459>,1:182]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={window_ordered_by=[{name=id, table_ref=a}], query_dictionary={id=[[@3,9:10='id',<459>,1:9]], rn=[[@22,74:75='rn',<459>,1:74], [@55,182:183='rn',<459>,1:182]]}, table_dictionary={events={initial_value=[[@33,114:114='a',<459>,1:114]], group_id=[[@12,45:45='a',<459>,1:45]], threshold=[[@47,153:153='a',<459>,1:153]], id=[[@1,7:7='a',<459>,1:7], [@17,65:65='a',<459>,1:65]], tags=[[@29,106:106='a',<459>,1:106]]}}, window_partition_by=[{name=group_id, table_ref=a}], filters=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}, {name=rn, table_ref=query0}], interface={id=[{name=id, table_ref=a}], rn=[{name=group_id, table_ref=a}, {name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceOrderByPlacementV0Test() {
		final String query = "SELECT a.id FROM events a WHERE a.active = 1 ORDER BY reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold), a.id";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:54) is recognized as Snowflake-specific grammar.",
						1, 54, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, orderby={1={null_order=null, predicand={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=id, table_ref=a}}, sort_order=ASC}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id]",
				"{}",
				"{events={initial_value=[[@21,69:69='a',<459>,1:69]], active=[[@8,32:32='a',<459>,1:32]], threshold=[[@35,108:108='a',<459>,1:108]], id=[[@1,7:7='a',<459>,1:7], [@40,122:122='a',<459>,1:122]], tags=[[@17,61:61='a',<459>,1:61]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@21,69:69='a',<459>,1:69]], active=[[@8,32:32='a',<459>,1:32]], threshold=[[@35,108:108='a',<459>,1:108]], id=[[@1,7:7='a',<459>,1:7], [@40,122:122='a',<459>,1:122]], tags=[[@17,61:61='a',<459>,1:61]]}}, ordered_by=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}, {name=id, table_ref=a}], filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceValuesPlacementV0Test() {
		final String query = "VALUES (7, reduce([1, NULL, 3], 0, (acc, x) -> acc + x + 2))";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:18) is recognized as Snowflake-specific grammar.",
						1, 18, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={values={matrix={1={row={1={literal=7}, 2={function={function_name=reduce, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={null_literal=null}, 3={literal=3}}}}, 2={literal=0}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={literal=2}, operator=+}}}}}}}}}}}}}",
				"[$1, $2]",
				"{}",
				"{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[], $2=[]}}}");
	}

	@Test
	public void arC031_reduceInsertPlacementV0Test() {
		final String query = "INSERT INTO results (id, result_value) SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:52) is recognized as Snowflake-specific grammar.",
						1, 52, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}}, target_table={table={alias=null, table=results}}, columns={1={column={name=id, table_ref=null}}, 2={column={name=result_value, table_ref=null}}}}}}",
				"[result_value, id]",
				"{}",
				"{results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, events={initial_value=[[@19,67:67='a',<459>,1:67]], active=[[@43,155:155='a',<459>,1:155]], threshold=[[@33,106:106='a',<459>,1:106]], id=[[@9,46:46='a',<459>,1:46]], tags=[[@15,59:59='a',<459>,1:59]]}}",
				"{query0={result_value=[[@38,122:133='result_value',<459>,1:122]], id=[[@11,48:49='id',<459>,1:48]]}, insert1={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}",
				"{def_insert1={query_dictionary={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, table_dictionary={results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}, def_query0={query_dictionary={result_value=[[@38,122:133='result_value',<459>,1:122]], id=[[@11,48:49='id',<459>,1:48]]}, table_dictionary={events={initial_value=[[@19,67:67='a',<459>,1:67]], active=[[@43,155:155='a',<459>,1:155]], threshold=[[@33,106:106='a',<459>,1:106]], id=[[@9,46:46='a',<459>,1:46]], tags=[[@15,59:59='a',<459>,1:59]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={id=[{name=id, table_ref=query0}], result_value=[{name=result_value, table_ref=query0}]}}}");
	}

	@Test
	public void arC031_reduceUpdatePlacementV0Test() {
		final String query = "UPDATE events SET result_value = reduce(tags, initial_value, (acc, x) -> acc + x + threshold) WHERE active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:33) is recognized as Snowflake-specific grammar.",
						1, 33, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={update={table={alias=null, table=events}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}, assignments={1={set={column={name=result_value, table_ref=null}}, to={function={function_name=reduce, parameters={1={column={name=tags, table_ref=null}}, 2={column={name=initial_value, table_ref=null}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=null}}, operator=+}}}}}}}}}}}}",
				"[result_value]",
				"{}",
				"{events={initial_value=[[@9,46:58='initial_value',<459>,1:46], [@22,92:92=')',<288>,1:92]], active=[[@24,100:105='active',<459>,1:100]], threshold=[[@21,83:91='threshold',<459>,1:83], [@22,92:92=')',<288>,1:92]], tags=[[@7,40:43='tags',<459>,1:40], [@22,92:92=')',<288>,1:92]]}}",
				"{update0={result_value=[[@3,18:29='result_value',<459>,1:18]]}}",
				"{def_update0={assignments={result_value=[{name=tags, table_ref=events}, {name=initial_value, table_ref=events}, {name=threshold, table_ref=events}]}, table_dictionary={events={initial_value=[[@9,46:58='initial_value',<459>,1:46], [@22,92:92=')',<288>,1:92]], active=[[@24,100:105='active',<459>,1:100]], threshold=[[@21,83:91='threshold',<459>,1:83], [@22,92:92=')',<288>,1:92]], tags=[[@7,40:43='tags',<459>,1:40], [@22,92:92=')',<288>,1:92]]}}, update_dictionary={result_value=[[@3,18:29='result_value',<459>,1:18]]}, target_table={events={result_value=[[@3,18:29='result_value',<459>,1:18]]}}, filters=[{name=active, table_ref=events}], lhs_unresolved_columns={result_value={column={name=result_value, table_ref=null}, locations=[[@3,18:29='result_value',<459>,1:18]]}}}}");
	}

	@Test
	public void arC031_reduceDeletePlacementV0Test() {
		final String query = "DELETE FROM events WHERE reduce(tags, initial_value, (acc, x) -> acc + x + threshold) IS NULL AND active = 0";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:25) is recognized as Snowflake-specific grammar.",
						1, 25, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={delete={table={alias=null, table=events}, where={and={1={condition={left={function={function_name=reduce, parameters={1={column={name=tags, table_ref=null}}, 2={column={name=initial_value, table_ref=null}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=null}}, operator=+}}}}}}}, operator=IS NULL}}, 2={condition={left={column={name=active, table_ref=null}}, right={literal=0}, operator==}}}}}}}",
				"[]",
				"{}",
				"{events={initial_value=[[@8,38:50='initial_value',<459>,1:38]], active=[[@25,98:103='active',<459>,1:98]], threshold=[[@20,75:83='threshold',<459>,1:75]], tags=[[@6,32:35='tags',<459>,1:32]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={initial_value=[[@8,38:50='initial_value',<459>,1:38]], active=[[@25,98:103='active',<459>,1:98]], threshold=[[@20,75:83='threshold',<459>,1:75]], tags=[[@6,32:35='tags',<459>,1:32]]}}, filters=[{name=tags, table_ref=events}, {name=initial_value, table_ref=events}, {name=threshold, table_ref=events}, {name=active, table_ref=events}], interface=null}}");
	}

	@Test
	public void arC031_reduceDdlPlacementV0Test() {
		final String query = "CREATE VIEW v_reduce AS SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.DdlContext tree = parser.ddl();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:37) is recognized as Snowflake-specific grammar.",
						1, 37, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{DDL={create={type=VIEW, name={table=v_reduce}, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[]",
				"{}",
				"{events={initial_value=[[@15,52:52='a',<459>,1:52]], active=[[@39,140:140='a',<459>,1:140]], threshold=[[@29,91:91='a',<459>,1:91]], id=[[@5,31:31='a',<459>,1:31]], tags=[[@11,44:44='a',<459>,1:44]]}}",
				"{query0={result_value=[[@34,107:118='result_value',<459>,1:107]], id=[[@7,33:34='id',<459>,1:33]]}}",
				"{def_create1={def_query0={query_dictionary={result_value=[[@34,107:118='result_value',<459>,1:107]], id=[[@7,33:34='id',<459>,1:33]]}, table_dictionary={events={initial_value=[[@15,52:52='a',<459>,1:52]], active=[[@39,140:140='a',<459>,1:140]], threshold=[[@29,91:91='a',<459>,1:91]], id=[[@5,31:31='a',<459>,1:31]], tags=[[@11,44:44='a',<459>,1:44]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}");
	}

	@Test
	public void arC031_reduceScriptPlacementV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1;\nSELECT b.id FROM orders b WHERE b.active = 1;\nSELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1;";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.ScriptContext tree = parser.script();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 2,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:3 c:13) is recognized as Snowflake-specific grammar.",
						3, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SCRIPT={1={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, 2={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=orders}}, where={condition={left={column={name=active, table_ref=b}}, right={literal=1}, operator==}}}, 3={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}",
				"[]",
				"{SCRIPT={1={}, 2={}, 3={}}}",
				"{SCRIPT={1={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, 2={orders={active=[[@49,162:162='b',<459>,2:32]], id=[[@42,137:137='b',<459>,2:7]]}}, 3={events={initial_value=[[@66,204:204='a',<459>,3:28]], active=[[@90,292:292='a',<459>,3:116]], threshold=[[@80,243:243='a',<459>,3:67]], id=[[@56,183:183='a',<459>,3:7]], tags=[[@62,196:196='a',<459>,3:20]]}}}}",
				"{SCRIPT={1={query0={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}}, 2={query0={id=[[@44,139:140='id',<459>,2:9]]}}, 3={query0={result_value=[[@85,259:270='result_value',<459>,3:83]], id=[[@58,185:186='id',<459>,3:9]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}, 2={def_query0={query_dictionary={id=[[@44,139:140='id',<459>,2:9]]}, table_dictionary={orders={active=[[@49,162:162='b',<459>,2:32]], id=[[@42,137:137='b',<459>,2:7]]}}, filters=[{name=active, table_ref=b}], interface={id=[{name=id, table_ref=b}]}, table_alias={b=orders}}}, 3={def_query0={query_dictionary={result_value=[[@85,259:270='result_value',<459>,3:83]], id=[[@58,185:186='id',<459>,3:9]]}, table_dictionary={events={initial_value=[[@66,204:204='a',<459>,3:28]], active=[[@90,292:292='a',<459>,3:116]], threshold=[[@80,243:243='a',<459>,3:67]], id=[[@56,183:183='a',<459>,3:7]], tags=[[@62,196:196='a',<459>,3:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}}");
	}

	@Test
	public void arC031_reduceArrayQualifiedV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceArrayUnqualifiedV0Test() {
		final String query = "SELECT a.id, reduce(tags, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=null}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@9,26:26='a',<459>,1:26]], active=[[@33,114:114='a',<459>,1:114]], threshold=[[@23,65:65='a',<459>,1:65]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:23='tags',<459>,1:20]]}}",
				"{query0={result_value=[[@28,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@28,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@9,26:26='a',<459>,1:26]], active=[[@33,114:114='a',<459>,1:114]], threshold=[[@23,65:65='a',<459>,1:65]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:23='tags',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=events}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceArrayAmbiguousV0Test() {
		final String query = "SELECT a.id, reduce(tags, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a JOIN labels b ON a.id = b.event_id";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.SEVERE_WARNING,
						"AMBIGUOUS_COLUMN_REFERENCE",
						"Ambiguous column reference 'tags' at (l:1 c:20). Possible sources: [events, labels]",
						1, 20, "SqlASTWalkerHelper", null, "tags", true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [tags [(l:1 c:20)]]",
						1, 20, "SqlASTWalkerHelper", null, "tags", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=null}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=event_id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=labels}}}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@9,26:26='a',<459>,1:26]], threshold=[[@23,65:65='a',<459>,1:65]], id=[[@1,7:7='a',<459>,1:7], [@36,125:125='a',<459>,1:125]]}, labels={event_id=[[@40,132:132='b',<459>,1:132]]}}",
				"{query0={result_value=[[@28,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@28,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@9,26:26='a',<459>,1:26]], threshold=[[@23,65:65='a',<459>,1:65]], id=[[@1,7:7='a',<459>,1:7], [@36,125:125='a',<459>,1:125]]}, labels={event_id=[[@40,132:132='b',<459>,1:132]]}}, filters=[{name=id, table_ref=a}, {name=event_id, table_ref=b}], interface={result_value=[{name=tags, table_ref=null}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events, b=labels}}}");
	}

	@Test
	public void arC031_reduceArrayMissingAliasV0Test() {
		final String query = "SELECT a.id, reduce(z.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'tags' at (l:1 c:83). No alias or table called 'z'.",
						1, 83, "SqlASTWalkerHelper", null, "tags", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=z}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=z}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceArrayHiddenAliasV0Test() {
		final String query = "SELECT q.id, reduce(hidden.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM (SELECT hidden.id, hidden.tags, hidden.threshold, hidden.initial_value FROM events hidden) q JOIN events a ON q.id = a.id";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'tags' at (l:1 c:88). No alias or table called 'hidden'.",
						1, 88, "SqlASTWalkerHelper", null, "tags", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=q}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=hidden}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={join={1={table={alias=q, query={select={1={column={name=id, table_ref=hidden}}, 2={column={name=tags, table_ref=hidden}}, 3={column={name=threshold, table_ref=hidden}}, 4={column={name=initial_value, table_ref=hidden}}}, from={table={alias=hidden, table=events}}}}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=q}}, right={column={name=id, table_ref=a}}, operator==}}}, 3={table={alias=a, table=events}}}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@46,156:161='hidden',<459>,1:156], [@11,33:33='a',<459>,1:33]], threshold=[[@42,138:143='hidden',<459>,1:138], [@25,72:72='a',<459>,1:72]], id=[[@34,114:119='hidden',<459>,1:114], [@62,223:223='a',<459>,1:223]], tags=[[@38,125:130='hidden',<459>,1:125]]}}",
				"{query0={initial_value=[[@48,163:175='initial_value',<459>,1:163]], threshold=[[@44,145:153='threshold',<459>,1:145]], id=[[@36,121:122='id',<459>,1:121], [@1,7:7='q',<459>,1:7], [@58,216:216='q',<459>,1:216]], tags=[[@40,132:135='tags',<459>,1:132]]}, query1={result_value=[[@30,88:99='result_value',<459>,1:88]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query1={query_dictionary={result_value=[[@30,88:99='result_value',<459>,1:88]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,33:33='a',<459>,1:33]], threshold=[[@25,72:72='a',<459>,1:72]], id=[[@62,223:223='a',<459>,1:223]]}}, def_query0={query_dictionary={initial_value=[[@48,163:175='initial_value',<459>,1:163]], threshold=[[@44,145:153='threshold',<459>,1:145]], id=[[@36,121:122='id',<459>,1:121], [@1,7:7='q',<459>,1:7], [@58,216:216='q',<459>,1:216]], tags=[[@40,132:135='tags',<459>,1:132]]}, table_dictionary={events={initial_value=[[@46,156:161='hidden',<459>,1:156], [@11,33:33='a',<459>,1:33]], threshold=[[@42,138:143='hidden',<459>,1:138], [@25,72:72='a',<459>,1:72]], id=[[@34,114:119='hidden',<459>,1:114], [@62,223:223='a',<459>,1:223]], tags=[[@38,125:130='hidden',<459>,1:125]]}}, interface={initial_value=[{name=initial_value, table_ref=hidden}], threshold=[{name=threshold, table_ref=hidden}], id=[{name=id, table_ref=hidden}], tags=[{name=tags, table_ref=hidden}]}, table_alias={hidden=events}}, filters=[{name=id, table_ref=q}, {name=id, table_ref=a}], interface={result_value=[{name=tags, table_ref=hidden}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=q}]}, table_alias={q=query0, a=events}}}");
	}

	@Test
	public void arC031_reduceArrayUnresolvedV0Test() {
		final String query = "SELECT reduce(tags, 0, (acc, x) -> acc + x + 1) AS result_value";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [tags [(l:1 c:14)]]",
						1, 14, "SqlASTWalkerHelper", null, "tags", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={function={function_name=reduce, parameters={1={column={name=tags, table_ref=null}}, 2={literal=0}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={literal=1}, operator=+}}}}}}, alias=result_value}}}}",
				"[result_value]",
				"{}",
				"{}",
				"{query0={result_value=[[@20,51:62='result_value',<459>,1:51]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,51:62='result_value',<459>,1:51]]}, interface={result_value=[{name=tags, table_ref=null}]}}}");
	}

	@Test
	public void arC031_reduceArrayPredicandVariableV0Test() {
		final String query = "SELECT a.id, reduce(<tags_expr>, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={substitution={name=<tags_expr>, type=predicand}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<tags_expr>=predicand}",
				"{events={initial_value=[[@9,33:33='a',<459>,1:33]], active=[[@33,121:121='a',<459>,1:121]], threshold=[[@23,72:72='a',<459>,1:72]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@28,88:99='result_value',<459>,1:88]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@28,88:99='result_value',<459>,1:88]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@9,33:33='a',<459>,1:33]], active=[[@33,121:121='a',<459>,1:121]], threshold=[[@23,72:72='a',<459>,1:72]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=<tags_expr>, type=predicand}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceArrayColumnVariableV0Test() {
		final String query = "SELECT a.id, reduce(a.<tags_col>, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={substitution={name=<tags_col>, type=column}, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<tags_col>=column}",
				"{events={initial_value=[[@11,34:34='a',<459>,1:34]], active=[[@35,122:122='a',<459>,1:122]], threshold=[[@25,73:73='a',<459>,1:73]], id=[[@1,7:7='a',<459>,1:7]], <tags_col>=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@30,89:100='result_value',<459>,1:89]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@30,89:100='result_value',<459>,1:89]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,34:34='a',<459>,1:34]], active=[[@35,122:122='a',<459>,1:122]], threshold=[[@25,73:73='a',<459>,1:73]], id=[[@1,7:7='a',<459>,1:7]], <tags_col>=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{substitution={name=<tags_col>, type=column}, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceArrayNullV0Test() {
		final String query = "SELECT a.id, reduce(NULL, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={null_literal=null}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@9,26:26='a',<459>,1:26]], active=[[@33,114:114='a',<459>,1:114]], threshold=[[@23,65:65='a',<459>,1:65]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@28,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@28,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@9,26:26='a',<459>,1:26]], active=[[@33,114:114='a',<459>,1:114]], threshold=[[@23,65:65='a',<459>,1:65]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceInitialQualifiedV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceInitialUnqualifiedV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=null}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@11,28:40='initial_value',<459>,1:28]], active=[[@33,114:114='a',<459>,1:114]], threshold=[[@23,65:65='a',<459>,1:65]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@28,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@28,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:40='initial_value',<459>,1:28]], active=[[@33,114:114='a',<459>,1:114]], threshold=[[@23,65:65='a',<459>,1:65]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=events}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceInitialAmbiguousV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a JOIN labels b ON a.id = b.event_id";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.SEVERE_WARNING,
						"AMBIGUOUS_COLUMN_REFERENCE",
						"Ambiguous column reference 'initial_value' at (l:1 c:28). Possible sources: [events, labels]",
						1, 28, "SqlASTWalkerHelper", null, "initial_value", true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [initial_value [(l:1 c:28)]]",
						1, 28, "SqlASTWalkerHelper", null, "initial_value", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=null}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=event_id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=labels}}}}}}",
				"[result_value, id]",
				"{}",
				"{events={threshold=[[@23,65:65='a',<459>,1:65]], id=[[@1,7:7='a',<459>,1:7], [@36,125:125='a',<459>,1:125]], tags=[[@7,20:20='a',<459>,1:20]]}, labels={event_id=[[@40,132:132='b',<459>,1:132]]}}",
				"{query0={result_value=[[@28,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@28,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={threshold=[[@23,65:65='a',<459>,1:65]], id=[[@1,7:7='a',<459>,1:7], [@36,125:125='a',<459>,1:125]], tags=[[@7,20:20='a',<459>,1:20]]}, labels={event_id=[[@40,132:132='b',<459>,1:132]]}}, filters=[{name=id, table_ref=a}, {name=event_id, table_ref=b}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=null}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events, b=labels}}}");
	}

	@Test
	public void arC031_reduceInitialMissingAliasV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, z.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'initial_value' at (l:1 c:83). No alias or table called 'z'.",
						1, 83, "SqlASTWalkerHelper", null, "initial_value", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=z}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=z}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceInitialHiddenAliasV0Test() {
		final String query = "SELECT q.id, reduce(a.tags, hidden.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM (SELECT hidden.id, hidden.tags, hidden.threshold, hidden.initial_value FROM events hidden) q JOIN events a ON q.id = a.id";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'initial_value' at (l:1 c:88). No alias or table called 'hidden'.",
						1, 88, "SqlASTWalkerHelper", null, "initial_value", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=q}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=hidden}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={join={1={table={alias=q, query={select={1={column={name=id, table_ref=hidden}}, 2={column={name=tags, table_ref=hidden}}, 3={column={name=threshold, table_ref=hidden}}, 4={column={name=initial_value, table_ref=hidden}}}, from={table={alias=hidden, table=events}}}}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=q}}, right={column={name=id, table_ref=a}}, operator==}}}, 3={table={alias=a, table=events}}}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@46,156:161='hidden',<459>,1:156]], threshold=[[@42,138:143='hidden',<459>,1:138], [@25,72:72='a',<459>,1:72]], id=[[@34,114:119='hidden',<459>,1:114], [@62,223:223='a',<459>,1:223]], tags=[[@38,125:130='hidden',<459>,1:125], [@7,20:20='a',<459>,1:20]]}}",
				"{query0={initial_value=[[@48,163:175='initial_value',<459>,1:163]], threshold=[[@44,145:153='threshold',<459>,1:145]], id=[[@36,121:122='id',<459>,1:121], [@1,7:7='q',<459>,1:7], [@58,216:216='q',<459>,1:216]], tags=[[@40,132:135='tags',<459>,1:132]]}, query1={result_value=[[@30,88:99='result_value',<459>,1:88]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query1={query_dictionary={result_value=[[@30,88:99='result_value',<459>,1:88]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={threshold=[[@25,72:72='a',<459>,1:72]], id=[[@62,223:223='a',<459>,1:223]], tags=[[@7,20:20='a',<459>,1:20]]}}, def_query0={query_dictionary={initial_value=[[@48,163:175='initial_value',<459>,1:163]], threshold=[[@44,145:153='threshold',<459>,1:145]], id=[[@36,121:122='id',<459>,1:121], [@1,7:7='q',<459>,1:7], [@58,216:216='q',<459>,1:216]], tags=[[@40,132:135='tags',<459>,1:132]]}, table_dictionary={events={initial_value=[[@46,156:161='hidden',<459>,1:156]], threshold=[[@42,138:143='hidden',<459>,1:138], [@25,72:72='a',<459>,1:72]], id=[[@34,114:119='hidden',<459>,1:114], [@62,223:223='a',<459>,1:223]], tags=[[@38,125:130='hidden',<459>,1:125], [@7,20:20='a',<459>,1:20]]}}, interface={initial_value=[{name=initial_value, table_ref=hidden}], threshold=[{name=threshold, table_ref=hidden}], id=[{name=id, table_ref=hidden}], tags=[{name=tags, table_ref=hidden}]}, table_alias={hidden=events}}, filters=[{name=id, table_ref=q}, {name=id, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=hidden}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=q}]}, table_alias={q=query0, a=events}}}");
	}

	@Test
	public void arC031_reduceInitialUnresolvedV0Test() {
		final String query = "SELECT reduce([1,2], initial_value, (acc, x) -> acc + x + 1) AS result_value";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:14) is recognized as Snowflake-specific grammar.",
						1, 14, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [initial_value [(l:1 c:21)]]",
						1, 21, "SqlASTWalkerHelper", null, "initial_value", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={function={function_name=reduce, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={column={name=initial_value, table_ref=null}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={literal=1}, operator=+}}}}}}, alias=result_value}}}}",
				"[result_value]",
				"{}",
				"{}",
				"{query0={result_value=[[@24,64:75='result_value',<459>,1:64]]}}",
				"{def_query0={query_dictionary={result_value=[[@24,64:75='result_value',<459>,1:64]]}, interface={result_value=[{name=initial_value, table_ref=null}]}}}");
	}

	@Test
	public void arC031_reduceInitialPredicandVariableV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, <initial_value_expr>, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={substitution={name=<initial_value_expr>, type=predicand}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<initial_value_expr>=predicand}",
				"{events={active=[[@33,121:121='a',<459>,1:121]], threshold=[[@23,72:72='a',<459>,1:72]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@28,88:99='result_value',<459>,1:88]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@28,88:99='result_value',<459>,1:88]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@33,121:121='a',<459>,1:121]], threshold=[[@23,72:72='a',<459>,1:72]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=<initial_value_expr>, type=predicand}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceInitialColumnVariableV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.<initial_value_col>, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={substitution={name=<initial_value_col>, type=column}, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<initial_value_col>=column}",
				"{events={<initial_value_col>=[[@11,28:28='a',<459>,1:28]], active=[[@35,122:122='a',<459>,1:122]], threshold=[[@25,73:73='a',<459>,1:73]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@30,89:100='result_value',<459>,1:89]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@30,89:100='result_value',<459>,1:89]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={<initial_value_col>=[[@11,28:28='a',<459>,1:28]], active=[[@35,122:122='a',<459>,1:122]], threshold=[[@25,73:73='a',<459>,1:73]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {substitution={name=<initial_value_col>, type=column}, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceInitialNullV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, NULL, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={null_literal=null}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@33,105:105='a',<459>,1:105]], threshold=[[@23,56:56='a',<459>,1:56]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@28,72:83='result_value',<459>,1:72]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@28,72:83='result_value',<459>,1:72]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@33,105:105='a',<459>,1:105]], threshold=[[@23,56:56='a',<459>,1:56]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceBodyQualifiedV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceBodyUnqualifiedV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=null}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@33,114:114='a',<459>,1:114]], threshold=[[@25,67:75='threshold',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@28,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@28,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@33,114:114='a',<459>,1:114]], threshold=[[@25,67:75='threshold',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=events}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceBodyAmbiguousV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + threshold) AS result_value FROM events a JOIN labels b ON a.id = b.event_id";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.SEVERE_WARNING,
						"AMBIGUOUS_COLUMN_REFERENCE",
						"Ambiguous column reference 'threshold' at (l:1 c:67). Possible sources: [events, labels]",
						1, 67, "SqlASTWalkerHelper", null, "threshold", true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [threshold [(l:1 c:67)]]",
						1, 67, "SqlASTWalkerHelper", null, "threshold", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=null}}, operator=+}}}}}}, alias=result_value}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=event_id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=labels}}}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@11,28:28='a',<459>,1:28]], id=[[@1,7:7='a',<459>,1:7], [@36,125:125='a',<459>,1:125]], tags=[[@7,20:20='a',<459>,1:20]]}, labels={event_id=[[@40,132:132='b',<459>,1:132]]}}",
				"{query0={result_value=[[@28,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@28,81:92='result_value',<459>,1:81]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], id=[[@1,7:7='a',<459>,1:7], [@36,125:125='a',<459>,1:125]], tags=[[@7,20:20='a',<459>,1:20]]}, labels={event_id=[[@40,132:132='b',<459>,1:132]]}}, filters=[{name=id, table_ref=a}, {name=event_id, table_ref=b}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=null}], id=[{name=id, table_ref=a}]}, table_alias={a=events, b=labels}}}");
	}

	@Test
	public void arC031_reduceBodyMissingAliasV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + z.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'threshold' at (l:1 c:83). No alias or table called 'z'.",
						1, 83, "SqlASTWalkerHelper", null, "threshold", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=z}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=z}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceBodyHiddenAliasV0Test() {
		final String query = "SELECT q.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + hidden.threshold) AS result_value FROM (SELECT hidden.id, hidden.tags, hidden.threshold, hidden.initial_value FROM events hidden) q JOIN events a ON q.id = a.id";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'threshold' at (l:1 c:88). No alias or table called 'hidden'.",
						1, 88, "SqlASTWalkerHelper", null, "threshold", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=q}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=hidden}}, operator=+}}}}}}, alias=result_value}}, from={join={1={table={alias=q, query={select={1={column={name=id, table_ref=hidden}}, 2={column={name=tags, table_ref=hidden}}, 3={column={name=threshold, table_ref=hidden}}, 4={column={name=initial_value, table_ref=hidden}}}, from={table={alias=hidden, table=events}}}}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=q}}, right={column={name=id, table_ref=a}}, operator==}}}, 3={table={alias=a, table=events}}}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@46,156:161='hidden',<459>,1:156], [@11,28:28='a',<459>,1:28]], threshold=[[@42,138:143='hidden',<459>,1:138]], id=[[@34,114:119='hidden',<459>,1:114], [@62,223:223='a',<459>,1:223]], tags=[[@38,125:130='hidden',<459>,1:125], [@7,20:20='a',<459>,1:20]]}}",
				"{query0={initial_value=[[@48,163:175='initial_value',<459>,1:163]], threshold=[[@44,145:153='threshold',<459>,1:145]], id=[[@36,121:122='id',<459>,1:121], [@1,7:7='q',<459>,1:7], [@58,216:216='q',<459>,1:216]], tags=[[@40,132:135='tags',<459>,1:132]]}, query1={result_value=[[@30,88:99='result_value',<459>,1:88]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query1={query_dictionary={result_value=[[@30,88:99='result_value',<459>,1:88]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], id=[[@62,223:223='a',<459>,1:223]], tags=[[@7,20:20='a',<459>,1:20]]}}, def_query0={query_dictionary={initial_value=[[@48,163:175='initial_value',<459>,1:163]], threshold=[[@44,145:153='threshold',<459>,1:145]], id=[[@36,121:122='id',<459>,1:121], [@1,7:7='q',<459>,1:7], [@58,216:216='q',<459>,1:216]], tags=[[@40,132:135='tags',<459>,1:132]]}, table_dictionary={events={initial_value=[[@46,156:161='hidden',<459>,1:156], [@11,28:28='a',<459>,1:28]], threshold=[[@42,138:143='hidden',<459>,1:138]], id=[[@34,114:119='hidden',<459>,1:114], [@62,223:223='a',<459>,1:223]], tags=[[@38,125:130='hidden',<459>,1:125], [@7,20:20='a',<459>,1:20]]}}, interface={initial_value=[{name=initial_value, table_ref=hidden}], threshold=[{name=threshold, table_ref=hidden}], id=[{name=id, table_ref=hidden}], tags=[{name=tags, table_ref=hidden}]}, table_alias={hidden=events}}, filters=[{name=id, table_ref=q}, {name=id, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=hidden}], id=[{name=id, table_ref=q}]}, table_alias={q=query0, a=events}}}");
	}

	@Test
	public void arC031_reduceBodyUnresolvedV0Test() {
		final String query = "SELECT reduce([1,2], 0, (acc, x) -> acc + x + threshold) AS result_value";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:14) is recognized as Snowflake-specific grammar.",
						1, 14, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [threshold [(l:1 c:46)]]",
						1, 46, "SqlASTWalkerHelper", null, "threshold", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={function={function_name=reduce, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={literal=0}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=null}}, operator=+}}}}}}, alias=result_value}}}}",
				"[result_value]",
				"{}",
				"{}",
				"{query0={result_value=[[@24,60:71='result_value',<459>,1:60]]}}",
				"{def_query0={query_dictionary={result_value=[[@24,60:71='result_value',<459>,1:60]]}, interface={result_value=[{name=threshold, table_ref=null}]}}}");
	}

	@Test
	public void arC031_reduceBodyPredicandVariableV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + <threshold_expr>) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={substitution={name=<threshold_expr>, type=predicand}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<threshold_expr>=predicand}",
				"{events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@33,121:121='a',<459>,1:121]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@28,88:99='result_value',<459>,1:88]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@28,88:99='result_value',<459>,1:88]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@33,121:121='a',<459>,1:121]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=<threshold_expr>, type=predicand}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceBodyColumnVariableV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.<threshold_col>) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={substitution={name=<threshold_col>, type=column}, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<threshold_col>=column}",
				"{events={initial_value=[[@11,28:28='a',<459>,1:28]], <threshold_col>=[[@25,67:67='a',<459>,1:67]], active=[[@35,122:122='a',<459>,1:122]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@30,89:100='result_value',<459>,1:89]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@30,89:100='result_value',<459>,1:89]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], <threshold_col>=[[@25,67:67='a',<459>,1:67]], active=[[@35,122:122='a',<459>,1:122]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {substitution={name=<threshold_col>, type=column}, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceBodyNullV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + NULL) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={null_literal=null}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@33,109:109='a',<459>,1:109]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@28,76:87='result_value',<459>,1:76]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@28,76:87='result_value',<459>,1:76]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@33,109:109='a',<459>,1:109]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceLocalOnlyNoSourceV0Test() {
		final String query = "SELECT reduce([1,2,3], 0, (acc, x) -> acc + x) AS result_value";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:14) is recognized as Snowflake-specific grammar.",
						1, 14, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={function={function_name=reduce, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}, 3={literal=3}}}}, 2={literal=0}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}}}}}, alias=result_value}}}}",
				"[result_value]",
				"{}",
				"{}",
				"{query0={result_value=[[@24,50:61='result_value',<459>,1:50]]}}",
				"{def_query0={query_dictionary={result_value=[[@24,50:61='result_value',<459>,1:50]]}, interface={result_value=[]}}}");
	}

	@Test
	public void arC031_reduceTypedBindingsV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.initial_value, (acc INT, x INT) -> acc + x) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc, data_type={type=INT}}, 2={name=x, data_type={type=INT}}}, body={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@33,110:110='a',<459>,1:110]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@28,77:88='result_value',<459>,1:77]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@28,77:88='result_value',<459>,1:77]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@33,110:110='a',<459>,1:110]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceParenthesizedBindingsV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceUnparenthesizedBindingsV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.initial_value, acc, x -> acc + x) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@29,100:100='a',<459>,1:100]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@24,67:78='result_value',<459>,1:67]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@24,67:78='result_value',<459>,1:67]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@29,100:100='a',<459>,1:100]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceLocalJsonPathV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x:amount) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0, json_path=amount}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@33,109:109='a',<459>,1:109]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@28,76:87='result_value',<459>,1:76]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@28,76:87='result_value',<459>,1:76]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@33,109:109='a',<459>,1:109]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceLocalNameMatchesColumnV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.x) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=x, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@11,28:28='a',<459>,1:28]], x=[[@25,67:67='a',<459>,1:67]], active=[[@35,108:108='a',<459>,1:108]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@30,75:86='result_value',<459>,1:75]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@30,75:86='result_value',<459>,1:75]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], x=[[@25,67:67='a',<459>,1:67]], active=[[@35,108:108='a',<459>,1:108]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=x, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceLocalNameMatchesTableAliasV0Test() {
		final String query = "SELECT x.id, reduce(x.tags, x.initial_value, (acc, x) -> acc + x + x.threshold) AS result_value FROM events x WHERE x.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=x}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=x}}, 2={column={name=initial_value, table_ref=x}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=x}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=x, table=events}}, where={condition={left={column={name=active, table_ref=x}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@11,28:28='x',<459>,1:28]], active=[[@35,116:116='x',<459>,1:116]], threshold=[[@25,67:67='x',<459>,1:67]], id=[[@1,7:7='x',<459>,1:7]], tags=[[@7,20:20='x',<459>,1:20]]}}",
				"{query0={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='x',<459>,1:28]], active=[[@35,116:116='x',<459>,1:116]], threshold=[[@25,67:67='x',<459>,1:67]], id=[[@1,7:7='x',<459>,1:7]], tags=[[@7,20:20='x',<459>,1:20]]}}, filters=[{name=active, table_ref=x}], interface={result_value=[{name=tags, table_ref=x}, {name=initial_value, table_ref=x}, {name=threshold, table_ref=x}], id=[{name=id, table_ref=x}]}, table_alias={x=events}}}");
	}

	@Test
	public void arC031_reduceNoLeakAfterCallV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x) AS result_value, x AS physical_x FROM events a";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}}}}}, alias=result_value}, 3={column={name=x, table_ref=null}, alias=physical_x}}, from={table={alias=a, table=events}}}}",
				"[physical_x, result_value, id]",
				"{}",
				"{events={initial_value=[[@11,28:28='a',<459>,1:28]], x=[[@28,83:83='x',<459>,1:83]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@26,69:80='result_value',<459>,1:69]], physical_x=[[@30,88:97='physical_x',<459>,1:88]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={physical_x=[[@30,88:97='physical_x',<459>,1:88]], result_value=[[@26,69:80='result_value',<459>,1:69]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], x=[[@28,83:83='x',<459>,1:83]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, interface={physical_x=[{name=x, table_ref=events}], result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceNoBindingInArrayOperandV0Test() {
		final String query = "SELECT a.id, reduce(x, a.initial_value, (acc, x) -> acc + x) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=x, table_ref=null}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@9,23:23='a',<459>,1:23]], x=[[@7,20:20='x',<459>,1:20]], active=[[@29,97:97='a',<459>,1:97]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@24,64:75='result_value',<459>,1:64]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@24,64:75='result_value',<459>,1:64]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@9,23:23='a',<459>,1:23]], x=[[@7,20:20='x',<459>,1:20]], active=[[@29,97:97='a',<459>,1:97]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=x, table_ref=events}, {name=initial_value, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceNoBindingInInitialOperandV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, acc, (acc, x) -> acc + x) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=acc, table_ref=null}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={acc=[[@11,28:30='acc',<459>,1:28]], active=[[@29,90:90='a',<459>,1:90]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@24,57:68='result_value',<459>,1:57]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@24,57:68='result_value',<459>,1:57]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={acc=[[@11,28:30='acc',<459>,1:28]], active=[[@29,90:90='a',<459>,1:90]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=acc, table_ref=events}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceNestedShadowingV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + reduce(a.other_tags, a.seed, (acc, x) -> acc + x + a.threshold) + x) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 2,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:63) is recognized as Snowflake-specific grammar.",
						1, 63, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={function={function_name=reduce, parameters={1={column={name=other_tags, table_ref=a}}, 2={column={name=seed, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}}, operator=+}}, right={lambda_reference={name=x, depth=0}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@11,28:28='a',<459>,1:28]], seed=[[@29,84:84='a',<459>,1:84]], active=[[@56,168:168='a',<459>,1:168]], threshold=[[@43,114:114='a',<459>,1:114]], id=[[@1,7:7='a',<459>,1:7]], other_tags=[[@25,70:70='a',<459>,1:70]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@51,135:146='result_value',<459>,1:135]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@51,135:146='result_value',<459>,1:135]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], seed=[[@29,84:84='a',<459>,1:84]], active=[[@56,168:168='a',<459>,1:168]], threshold=[[@43,114:114='a',<459>,1:114]], id=[[@1,7:7='a',<459>,1:7]], other_tags=[[@25,70:70='a',<459>,1:70]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=other_tags, table_ref=a}, {name=seed, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceNestedOuterCaptureV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + reduce(a.other_tags, 0, (total, y) -> total + y + x + acc)) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 2,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:63) is recognized as Snowflake-specific grammar.",
						1, 63, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={lambda_reference={name=acc, depth=0}}, right={function={function_name=reduce, parameters={1={column={name=other_tags, table_ref=a}}, 2={literal=0}, 3={lambda={parameters={1={name=total}, 2={name=y}}, body={calc={left={calc={left={calc={left={lambda_reference={name=total, depth=0}}, right={lambda_reference={name=y, depth=0}}, operator=+}}, right={lambda_reference={name=x, depth=1}}, operator=+}}, right={lambda_reference={name=acc, depth=1}}, operator=+}}}}}}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@52,159:159='a',<459>,1:159]], id=[[@1,7:7='a',<459>,1:7]], other_tags=[[@25,70:70='a',<459>,1:70]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@47,126:137='result_value',<459>,1:126]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@47,126:137='result_value',<459>,1:126]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@52,159:159='a',<459>,1:159]], id=[[@1,7:7='a',<459>,1:7]], other_tags=[[@25,70:70='a',<459>,1:70]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=other_tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceEmptyArrayV0Test() {
		final String query = "SELECT a.id, reduce([], 0, (acc, x) -> acc + x) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:20) is recognized as Snowflake-specific grammar.",
						1, 20, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={function={function_name=array_construct, parameters={}}}, 2={literal=0}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@28,84:84='a',<459>,1:84]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@23,51:62='result_value',<459>,1:51]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@23,51:62='result_value',<459>,1:51]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@28,84:84='a',<459>,1:84]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceNullArrayV0Test() {
		final String query = "SELECT a.id, reduce(NULL, 0, (acc, x) -> acc + x) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={null_literal=null}, 2={literal=0}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@27,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@22,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@22,53:64='result_value',<459>,1:53]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@27,86:86='a',<459>,1:86]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceNullBodyRuntimeBoundaryV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> NULL) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={null_literal=null}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@29,99:99='a',<459>,1:99]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@24,66:77='result_value',<459>,1:66]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@24,66:77='result_value',<459>,1:66]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@29,99:99='a',<459>,1:99]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceBareBodySubstitutionV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> <lambda_body>) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={substitution={name=<lambda_body>, type=predicand}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<lambda_body>=predicand}",
				"{events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@29,108:108='a',<459>,1:108]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@24,75:86='result_value',<459>,1:75]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@24,75:86='result_value',<459>,1:75]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@29,108:108='a',<459>,1:108]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=<lambda_body>, type=predicand}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceCombinedSubstitutionsV0Test() {
		final String query = "SELECT a.id, reduce(<array_expr>, <initial_expr>, (acc, x) -> acc + x + <external_expr>) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={substitution={name=<array_expr>, type=predicand}}, 2={substitution={name=<initial_expr>, type=predicand}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={substitution={name=<external_expr>, type=predicand}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<external_expr>=predicand, <array_expr>=predicand, <initial_expr>=predicand}",
				"{events={active=[[@29,125:125='a',<459>,1:125]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@24,92:103='result_value',<459>,1:92]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@24,92:103='result_value',<459>,1:92]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@29,125:125='a',<459>,1:125]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=<array_expr>, type=predicand}, {name=<initial_expr>, type=predicand}, {name=<external_expr>, type=predicand}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceDerivedOutputV0Test() {
		final String query = "SELECT q.id, q.result_value FROM (SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1) q WHERE q.result_value IS NOT NULL";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:47) is recognized as Snowflake-specific grammar.",
						1, 47, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=q}}, 2={column={name=result_value, table_ref=q}}}, from={table={alias=q, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}, where={condition={left={column={name=result_value, table_ref=q}}, operator=IS NOT NULL}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@21,62:62='a',<459>,1:62]], active=[[@45,150:150='a',<459>,1:150]], threshold=[[@35,101:101='a',<459>,1:101]], id=[[@11,41:41='a',<459>,1:41]], tags=[[@17,54:54='a',<459>,1:54]]}}",
				"{query0={result_value=[[@40,117:128='result_value',<459>,1:117], [@5,13:13='q',<459>,1:13], [@53,172:172='q',<459>,1:172]], id=[[@13,43:44='id',<459>,1:43], [@1,7:7='q',<459>,1:7]]}, query1={result_value=[[@7,15:26='result_value',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query1={query_dictionary={result_value=[[@7,15:26='result_value',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}, def_query0={query_dictionary={result_value=[[@40,117:128='result_value',<459>,1:117], [@5,13:13='q',<459>,1:13], [@53,172:172='q',<459>,1:172]], id=[[@13,43:44='id',<459>,1:43], [@1,7:7='q',<459>,1:7]]}, table_dictionary={events={initial_value=[[@21,62:62='a',<459>,1:62]], active=[[@45,150:150='a',<459>,1:150]], threshold=[[@35,101:101='a',<459>,1:101]], id=[[@11,41:41='a',<459>,1:41]], tags=[[@17,54:54='a',<459>,1:54]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=result_value, table_ref=q}], interface={result_value=[{name=result_value, table_ref=q}], id=[{name=id, table_ref=q}]}, table_alias={q=query0}}}");
	}

	@Test
	public void arC031_reduceCteRenameChainV0Test() {
		final String query = "WITH seed AS (SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1), renamed AS (SELECT id, result_value AS exported_value FROM seed) SELECT r.id, r.exported_value FROM renamed r";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:27) is recognized as Snowflake-specific grammar.",
						1, 27, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=seed}, 2={cte={select={1={column={name=id, table_ref=null}}, 2={column={name=result_value, table_ref=null}, alias=exported_value}}, from={table={alias=null, table=seed}}}, alias=renamed}}, query={select={1={column={name=id, table_ref=r}}, 2={column={name=exported_value, table_ref=r}}}, from={table={alias=r, table=renamed}}}}}",
				"[exported_value, id]",
				"{}",
				"{events={initial_value=[[@15,42:42='a',<459>,1:42]], active=[[@39,130:130='a',<459>,1:130]], threshold=[[@29,81:81='a',<459>,1:81]], id=[[@5,21:21='a',<459>,1:21]], tags=[[@11,34:34='a',<459>,1:34]]}}",
				"{query0={result_value=[[@34,97:108='result_value',<459>,1:97], [@52,168:179='result_value',<459>,1:168]], id=[[@7,23:24='id',<459>,1:23], [@50,164:165='id',<459>,1:164]]}, query1={exported_value=[[@54,184:197='exported_value',<459>,1:184], [@63,223:223='r',<459>,1:223]], id=[[@50,164:165='id',<459>,1:164], [@59,217:217='r',<459>,1:217]]}, query2={exported_value=[[@65,225:238='exported_value',<459>,1:225]], id=[[@61,219:220='id',<459>,1:219]]}}",
				"{def_query2={context_list={seed=query0, renamed=query1, r=query1}, query_dictionary={exported_value=[[@65,225:238='exported_value',<459>,1:225]], id=[[@61,219:220='id',<459>,1:219]]}, def_query1={context_list={seed=query0}, query_dictionary={exported_value=[[@54,184:197='exported_value',<459>,1:184], [@63,223:223='r',<459>,1:223]], id=[[@50,164:165='id',<459>,1:164], [@59,217:217='r',<459>,1:217]]}, interface={exported_value=[{name=result_value, table_ref=query0}], id=[{name=id, table_ref=query0}]}, table_alias={seed=query0}}, def_query0={query_dictionary={result_value=[[@34,97:108='result_value',<459>,1:97], [@52,168:179='result_value',<459>,1:168]], id=[[@7,23:24='id',<459>,1:23], [@50,164:165='id',<459>,1:164]]}, table_dictionary={events={initial_value=[[@15,42:42='a',<459>,1:42]], active=[[@39,130:130='a',<459>,1:130]], threshold=[[@29,81:81='a',<459>,1:81]], id=[[@5,21:21='a',<459>,1:21]], tags=[[@11,34:34='a',<459>,1:34]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={exported_value=[{name=exported_value, table_ref=r}], id=[{name=id, table_ref=r}]}, table_alias={r=query1, seed=query0, renamed=query1}}}");
	}

	@Test
	public void arC031_reduceCteMissingExportV0Test() {
		final String query = "WITH seed AS (SELECT a.id FROM events a) SELECT a.id, reduce(a.tags, 0, (acc, x) -> acc + x) AS result_value FROM seed a";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:54) is recognized as Snowflake-specific grammar.",
						1, 54, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
						"Qualified column 'tags' at (l:1 c:61) was not found in output interface of query alias 'a'.",
						1, 61, "SqlASTWalkerHelper", null, "a.tags", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}}, alias=seed}}, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={literal=0}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=seed}}}}}",
				"[result_value, id]",
				"{}",
				"{events={id=[[@5,21:21='a',<459>,1:21]]}}",
				"{query0={id=[[@7,23:24='id',<459>,1:23], [@13,48:48='a',<459>,1:48]]}, query1={result_value=[[@36,96:107='result_value',<459>,1:96]], id=[[@15,50:51='id',<459>,1:50]]}}",
				"{def_query1={context_list={seed=query0, a=query0}, query_dictionary={result_value=[[@36,96:107='result_value',<459>,1:96]], id=[[@15,50:51='id',<459>,1:50]]}, def_query0={query_dictionary={id=[[@7,23:24='id',<459>,1:23], [@13,48:48='a',<459>,1:48]]}, table_dictionary={events={id=[[@5,21:21='a',<459>,1:21]]}}, interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={result_value=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=query0, seed=query0}}}");
	}

	@Test
	public void arC031_reduceSetOpSiblingIsolationV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1 UNION ALL SELECT b.id AS other_id, b.stored_value AS other_value FROM archive_events b";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={union={1={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={select={1={column={name=id, table_ref=b}, alias=other_id}, 2={column={name=stored_value, table_ref=b}, alias=other_value}}, from={table={alias=b, table=archive_events}}}}}}",
				"[result_value, id]",
				"{}",
				"{archive_events={stored_value=[[@49,164:164='b',<459>,1:164]], id=[[@43,146:146='b',<459>,1:146]]}, events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}, query1={other_value=[[@53,182:192='other_value',<459>,1:182]], other_id=[[@47,154:161='other_id',<459>,1:154]]}}",
				"{def_union2={def_query1={query_dictionary={other_value=[[@53,182:192='other_value',<459>,1:182]], other_id=[[@47,154:161='other_id',<459>,1:154]]}, table_dictionary={archive_events={stored_value=[[@49,164:164='b',<459>,1:164]], id=[[@43,146:146='b',<459>,1:146]]}}, setop=UNION, interface={other_value=[{name=stored_value, table_ref=b}], other_id=[{name=id, table_ref=b}]}, table_alias={b=archive_events}}, def_query0={query_dictionary={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={result_value=query_column, id=query_column}}}");
	}

	@Test
	public void arC031_reduceCorrelatedSubqueryV0Test() {
		final String query = "SELECT a.id FROM events a WHERE EXISTS (SELECT b.id FROM labels b WHERE b.event_id = a.id AND reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) IS NOT NULL)";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:94) is recognized as Snowflake-specific grammar.",
						1, 94, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}, where={exists={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=labels}}, where={and={1={condition={left={column={name=event_id, table_ref=b}}, right={column={name=id, table_ref=a}}, operator==}}, 2={condition={left={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}}, operator=IS NOT NULL}}}}, operator=EXISTS}}}}",
				"[id]",
				"{}",
				"{events={initial_value=[[@32,109:109='a',<459>,1:109]], threshold=[[@46,148:148='a',<459>,1:148]], id=[[@1,7:7='a',<459>,1:7], [@22,85:85='a',<459>,1:85]], tags=[[@28,101:101='a',<459>,1:101]]}, labels={event_id=[[@18,72:72='b',<459>,1:72]], id=[[@11,47:47='b',<459>,1:47]]}}",
				"{query0={id=[[@13,49:50='id',<459>,1:49]]}, query2={id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query2={query_dictionary={id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@32,109:109='a',<459>,1:109]], threshold=[[@46,148:148='a',<459>,1:148]], id=[[@1,7:7='a',<459>,1:7], [@22,85:85='a',<459>,1:85]], tags=[[@28,101:101='a',<459>,1:101]]}}, dependent_queries={exists1={query=query0, type=filters}}, def_query0={query_dictionary={id=[[@13,49:50='id',<459>,1:49]]}, table_dictionary={labels={event_id=[[@18,72:72='b',<459>,1:72]], id=[[@11,47:47='b',<459>,1:47]]}}, filters=[{name=event_id, table_ref=b}, {name=id, table_ref=a}, {name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], interface={id=[{name=id, table_ref=b}]}, table_alias={b=labels}}, filters=[], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceGenericCompatibilityV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 0,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=tags, table_ref=a}}, 2={column={name=threshold, table_ref=a}}}, function_name=reduce}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@21,77:77='a',<459>,1:77]], threshold=[[@11,28:28='a',<459>,1:28]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@16,44:55='result_value',<459>,1:44]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,44:55='result_value',<459>,1:44]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,77:77='a',<459>,1:77]], threshold=[[@11,28:28='a',<459>,1:28]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceNullLambdaCompatibilityV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.initial_value, NULL) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 0,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={null_literal=null}}, function_name=reduce}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@23,87:87='a',<459>,1:87]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@18,54:65='result_value',<459>,1:54]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,54:65='result_value',<459>,1:54]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@23,87:87='a',<459>,1:87]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceQualifiedUdfV0Test() {
		final String query = "SELECT a.id, util.reduce(a.tags, a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 0,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={schema=util, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=threshold, table_ref=a}}}, function_name=reduce}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@23,82:82='a',<459>,1:82]], threshold=[[@13,33:33='a',<459>,1:33]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@9,25:25='a',<459>,1:25]]}}",
				"{query0={result_value=[[@18,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@23,82:82='a',<459>,1:82]], threshold=[[@13,33:33='a',<459>,1:33]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@9,25:25='a',<459>,1:25]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceSameNamedColumnV0Test() {
		final String query = "SELECT a.id, a.reduce AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 0,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=reduce, table_ref=a}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={reduce=[[@5,13:13='a',<459>,1:13]], active=[[@14,58:58='a',<459>,1:58]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@9,25:36='result_value',<459>,1:25]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@9,25:36='result_value',<459>,1:25]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={reduce=[[@5,13:13='a',<459>,1:13]], active=[[@14,58:58='a',<459>,1:58]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=reduce, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceUppercaseV0Test() {
		final String query = "SELECT a.id, REDUCE(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=REDUCE, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reduceMixedDialectV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value, extract(isodow from a.created_at) AS weekday FROM events a";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"STATEMENT_MIXED_DIALECT_GRAMMAR",
						"Statement mixes Snowflake-only and PostgreSQL-only grammar at (l:1 c:97); unlikely to run on either engine.",
						1, 97, "SqlASTWalkerHelper", null, null, false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}, 3={alias=weekday, datetime_function={function_name=extract, part_form=KEYWORD, part=isodow, source={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}}}",
				"[weekday, result_value, id]",
				"{}",
				"{events={initial_value=[[@11,28:28='a',<459>,1:28]], created_at=[[@36,117:117='a',<459>,1:117]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={weekday=[[@41,134:140='weekday',<459>,1:134]], result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={weekday=[[@41,134:140='weekday',<459>,1:134]], result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], created_at=[[@36,117:117='a',<459>,1:117]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, interface={weekday=[{name=created_at, table_ref=a}], result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC031_reducePREDICANDSnippetV0Test() {
		final String query = "reduce([1,2], 0, (acc, x) -> acc + x)";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.predicand_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{PREDICAND={function={function_name=reduce, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={literal=0}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}}}}}}}",
				"[]",
				"{}",
				"{}",
				"{}",
				"{}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "PREDICAND");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{PREDICAND={function={function_name=reduce, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={literal=0}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC031_reduceCONDITIONSnippetV0Test() {
		final String query = "reduce([1,2], 0, (acc, x) -> acc + x) IS NOT NULL AND 1 = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.condition_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{CONDITION={and={1={condition={left={function={function_name=reduce, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={literal=0}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={literal=1}, right={literal=1}, operator==}}}}}",
				"[]",
				"{}",
				"{}",
				"{}",
				"{}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "CONDITION");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:7) is recognized as Snowflake-specific grammar.",
						1, 7, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{CONDITION={and={1={condition={left={function={function_name=reduce, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={literal=0}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={literal=1}, right={literal=1}, operator==}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC031_reduceQUERYSnippetV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.query_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{QUERY={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}",
				"{query0={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "QUERY");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{QUERY={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[result_value, id]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{query0={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_query0={query_dictionary={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC031_reduceTUPLESnippetV0Test() {
		final String query = "(SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1)";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.tuple_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:14) is recognized as Snowflake-specific grammar.",
						1, 14, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{TUPLE={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}}}",
				"[result_value, id]",
				"{}",
				"{events={initial_value=[[@12,29:29='a',<459>,1:29]], active=[[@36,117:117='a',<459>,1:117]], threshold=[[@26,68:68='a',<459>,1:68]], id=[[@2,8:8='a',<459>,1:8]], tags=[[@8,21:21='a',<459>,1:21]]}}",
				"{query0={result_value=[[@31,84:95='result_value',<459>,1:84]], id=[[@4,10:11='id',<459>,1:10]]}}",
				"{def_query0={query_dictionary={result_value=[[@31,84:95='result_value',<459>,1:84]], id=[[@4,10:11='id',<459>,1:10]]}, table_dictionary={events={initial_value=[[@12,29:29='a',<459>,1:29]], active=[[@36,117:117='a',<459>,1:117]], threshold=[[@26,68:68='a',<459>,1:68]], id=[[@2,8:8='a',<459>,1:8]], tags=[[@8,21:21='a',<459>,1:21]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, table_alias={query0=query0}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "TUPLE");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:14) is recognized as Snowflake-specific grammar.",
						1, 14, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{TUPLE={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[result_value, id]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={initial_value=[[@12,29:29='a',<459>,1:29]], active=[[@36,117:117='a',<459>,1:117]], threshold=[[@26,68:68='a',<459>,1:68]], id=[[@2,8:8='a',<459>,1:8]], tags=[[@8,21:21='a',<459>,1:21]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{query0={result_value=[[@31,84:95='result_value',<459>,1:84]], id=[[@4,10:11='id',<459>,1:10]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_query0={query_dictionary={result_value=[[@31,84:95='result_value',<459>,1:84]], id=[[@4,10:11='id',<459>,1:10]]}, table_dictionary={events={initial_value=[[@12,29:29='a',<459>,1:29]], active=[[@36,117:117='a',<459>,1:117]], threshold=[[@26,68:68='a',<459>,1:68]], id=[[@2,8:8='a',<459>,1:8]], tags=[[@8,21:21='a',<459>,1:21]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, table_alias={query0=query0}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC031_reduceIN_LISTSnippetV0Test() {
		final String query = "(reduce([1,2], 0, (acc, x) -> acc + x), NULL)";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.in_list_predicate_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:8) is recognized as Snowflake-specific grammar.",
						1, 8, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{IN_LIST={list={1={function={function_name=reduce, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={literal=0}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}}}}}}, 2={null_literal=null}}}}",
				"[]",
				"{}",
				"{}",
				"{}",
				"{}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "IN_LIST");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:8) is recognized as Snowflake-specific grammar.",
						1, 8, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{IN_LIST={list={1={function={function_name=reduce, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={literal=0}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}}}}}}, 2={null_literal=null}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC031_reduceJOIN_EXTENSIONSnippetV0Test() {
		final String query = "JOIN events a ON reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) IS NOT NULL AND a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.join_extension_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:17) is recognized as Snowflake-specific grammar.",
						1, 17, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{JOIN_EXTENSION={1={join=JOIN, on={and={1={condition={left={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}, 2={table={alias=a, table=events}}}}",
				"[]",
				"{}",
				"{events={initial_value=[[@10,32:32='a',<459>,1:32]], active=[[@32,100:100='a',<459>,1:100]], threshold=[[@24,71:71='a',<459>,1:71]], tags=[[@6,24:24='a',<459>,1:24]]}}",
				"{}",
				"{table_dictionary={events={initial_value=[[@10,32:32='a',<459>,1:32]], active=[[@32,100:100='a',<459>,1:100]], threshold=[[@24,71:71='a',<459>,1:71]], tags=[[@6,24:24='a',<459>,1:24]]}}, filters=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}, {name=active, table_ref=a}], table_alias={a=events}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "JOIN_EXTENSION");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:17) is recognized as Snowflake-specific grammar.",
						1, 17, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{JOIN_EXTENSION={1={join=JOIN, on={and={1={condition={left={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}}, operator=IS NOT NULL}}, 2={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}, 2={table={alias=a, table=events}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={initial_value=[[@10,32:32='a',<459>,1:32]], active=[[@32,100:100='a',<459>,1:100]], threshold=[[@24,71:71='a',<459>,1:71]], tags=[[@6,24:24='a',<459>,1:24]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{table_dictionary={events={initial_value=[[@10,32:32='a',<459>,1:32]], active=[[@32,100:100='a',<459>,1:100]], threshold=[[@24,71:71='a',<459>,1:71]], tags=[[@6,24:24='a',<459>,1:24]]}}, filters=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}, {name=active, table_ref=a}], table_alias={a=events}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC031_reduceVALUESSnippetV0Test() {
		final String query = "VALUES (7, reduce([1,2], 0, (acc, x) -> acc + x))";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.values_statement_end();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:18) is recognized as Snowflake-specific grammar.",
						1, 18, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{VALUES={values={matrix={1={row={1={literal=7}, 2={function={function_name=reduce, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={literal=0}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}}}}}}}}}}}}",
				"[$1, $2]",
				"{}",
				"{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[], $2=[]}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "VALUES");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:18) is recognized as Snowflake-specific grammar.",
						1, 18, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{VALUES={values={matrix={1={row={1={literal=7}, 2={function={function_name=reduce, parameters={1={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}, 2={literal=0}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}}}}}}}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[$1, $2]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{values0={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[], $2=[]}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC031_reduceINSERTSnippetV0Test() {
		final String query = "INSERT INTO results (id, result_value) SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.insert_end_point();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:52) is recognized as Snowflake-specific grammar.",
						1, 52, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{INSERT={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}}, target_table={table={alias=null, table=results}}, columns={1={column={name=id, table_ref=null}}, 2={column={name=result_value, table_ref=null}}}}}}",
				"[result_value, id]",
				"{}",
				"{results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, events={initial_value=[[@19,67:67='a',<459>,1:67]], active=[[@43,155:155='a',<459>,1:155]], threshold=[[@33,106:106='a',<459>,1:106]], id=[[@9,46:46='a',<459>,1:46]], tags=[[@15,59:59='a',<459>,1:59]]}}",
				"{query0={result_value=[[@38,122:133='result_value',<459>,1:122]], id=[[@11,48:49='id',<459>,1:48]]}, insert1={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}",
				"{def_insert1={query_dictionary={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, table_dictionary={results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}, def_query0={query_dictionary={result_value=[[@38,122:133='result_value',<459>,1:122]], id=[[@11,48:49='id',<459>,1:48]]}, table_dictionary={events={initial_value=[[@19,67:67='a',<459>,1:67]], active=[[@43,155:155='a',<459>,1:155]], threshold=[[@33,106:106='a',<459>,1:106]], id=[[@9,46:46='a',<459>,1:46]], tags=[[@15,59:59='a',<459>,1:59]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={id=[{name=id, table_ref=query0}], result_value=[{name=result_value, table_ref=query0}]}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "INSERT");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:52) is recognized as Snowflake-specific grammar.",
						1, 52, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{INSERT={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}}, target_table={table={alias=null, table=results}}, columns={1={column={name=id, table_ref=null}}, 2={column={name=result_value, table_ref=null}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[result_value, id]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, events={initial_value=[[@19,67:67='a',<459>,1:67]], active=[[@43,155:155='a',<459>,1:155]], threshold=[[@33,106:106='a',<459>,1:106]], id=[[@9,46:46='a',<459>,1:46]], tags=[[@15,59:59='a',<459>,1:59]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{query0={result_value=[[@38,122:133='result_value',<459>,1:122]], id=[[@11,48:49='id',<459>,1:48]]}, insert1={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_insert1={query_dictionary={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, table_dictionary={results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}, def_query0={query_dictionary={result_value=[[@38,122:133='result_value',<459>,1:122]], id=[[@11,48:49='id',<459>,1:48]]}, table_dictionary={events={initial_value=[[@19,67:67='a',<459>,1:67]], active=[[@43,155:155='a',<459>,1:155]], threshold=[[@33,106:106='a',<459>,1:106]], id=[[@9,46:46='a',<459>,1:46]], tags=[[@15,59:59='a',<459>,1:59]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={id=[{name=id, table_ref=query0}], result_value=[{name=result_value, table_ref=query0}]}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC031_reduceUPDATESnippetV0Test() {
		final String query = "UPDATE events SET result_value = reduce(tags, initial_value, (acc, x) -> acc + x + threshold) WHERE active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.update_end_point();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:33) is recognized as Snowflake-specific grammar.",
						1, 33, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{UPDATE={update={table={alias=null, table=events}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}, assignments={1={set={column={name=result_value, table_ref=null}}, to={function={function_name=reduce, parameters={1={column={name=tags, table_ref=null}}, 2={column={name=initial_value, table_ref=null}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=null}}, operator=+}}}}}}}}}}}}",
				"[result_value]",
				"{}",
				"{events={initial_value=[[@9,46:58='initial_value',<459>,1:46], [@22,92:92=')',<288>,1:92]], active=[[@24,100:105='active',<459>,1:100]], threshold=[[@21,83:91='threshold',<459>,1:83], [@22,92:92=')',<288>,1:92]], tags=[[@7,40:43='tags',<459>,1:40], [@22,92:92=')',<288>,1:92]]}}",
				"{update0={result_value=[[@3,18:29='result_value',<459>,1:18]]}}",
				"{def_update0={assignments={result_value=[{name=tags, table_ref=events}, {name=initial_value, table_ref=events}, {name=threshold, table_ref=events}]}, table_dictionary={events={initial_value=[[@9,46:58='initial_value',<459>,1:46], [@22,92:92=')',<288>,1:92]], active=[[@24,100:105='active',<459>,1:100]], threshold=[[@21,83:91='threshold',<459>,1:83], [@22,92:92=')',<288>,1:92]], tags=[[@7,40:43='tags',<459>,1:40], [@22,92:92=')',<288>,1:92]]}}, update_dictionary={result_value=[[@3,18:29='result_value',<459>,1:18]]}, target_table={events={result_value=[[@3,18:29='result_value',<459>,1:18]]}}, filters=[{name=active, table_ref=events}], lhs_unresolved_columns={result_value={column={name=result_value, table_ref=null}, locations=[[@3,18:29='result_value',<459>,1:18]]}}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "UPDATE");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:33) is recognized as Snowflake-specific grammar.",
						1, 33, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{UPDATE={update={table={alias=null, table=events}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}, assignments={1={set={column={name=result_value, table_ref=null}}, to={function={function_name=reduce, parameters={1={column={name=tags, table_ref=null}}, 2={column={name=initial_value, table_ref=null}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=null}}, operator=+}}}}}}}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[result_value]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={initial_value=[[@9,46:58='initial_value',<459>,1:46], [@22,92:92=')',<288>,1:92]], active=[[@24,100:105='active',<459>,1:100]], threshold=[[@21,83:91='threshold',<459>,1:83], [@22,92:92=')',<288>,1:92]], tags=[[@7,40:43='tags',<459>,1:40], [@22,92:92=')',<288>,1:92]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{update0={result_value=[[@3,18:29='result_value',<459>,1:18]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_update0={assignments={result_value=[{name=tags, table_ref=events}, {name=initial_value, table_ref=events}, {name=threshold, table_ref=events}]}, table_dictionary={events={initial_value=[[@9,46:58='initial_value',<459>,1:46], [@22,92:92=')',<288>,1:92]], active=[[@24,100:105='active',<459>,1:100]], threshold=[[@21,83:91='threshold',<459>,1:83], [@22,92:92=')',<288>,1:92]], tags=[[@7,40:43='tags',<459>,1:40], [@22,92:92=')',<288>,1:92]]}}, update_dictionary={result_value=[[@3,18:29='result_value',<459>,1:18]]}, target_table={events={result_value=[[@3,18:29='result_value',<459>,1:18]]}}, filters=[{name=active, table_ref=events}], lhs_unresolved_columns={result_value={column={name=result_value, table_ref=null}, locations=[[@3,18:29='result_value',<459>,1:18]]}}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC031_reduceDELETESnippetV0Test() {
		final String query = "DELETE FROM events WHERE reduce(tags, initial_value, (acc, x) -> acc + x + threshold) IS NULL AND active = 0";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.delete_end_point();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:25) is recognized as Snowflake-specific grammar.",
						1, 25, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{DELETE={delete={table={alias=null, table=events}, where={and={1={condition={left={function={function_name=reduce, parameters={1={column={name=tags, table_ref=null}}, 2={column={name=initial_value, table_ref=null}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=null}}, operator=+}}}}}}}, operator=IS NULL}}, 2={condition={left={column={name=active, table_ref=null}}, right={literal=0}, operator==}}}}}}}",
				"[]",
				"{}",
				"{events={initial_value=[[@8,38:50='initial_value',<459>,1:38]], active=[[@25,98:103='active',<459>,1:98]], threshold=[[@20,75:83='threshold',<459>,1:75]], tags=[[@6,32:35='tags',<459>,1:32]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={initial_value=[[@8,38:50='initial_value',<459>,1:38]], active=[[@25,98:103='active',<459>,1:98]], threshold=[[@20,75:83='threshold',<459>,1:75]], tags=[[@6,32:35='tags',<459>,1:32]]}}, filters=[{name=tags, table_ref=events}, {name=initial_value, table_ref=events}, {name=threshold, table_ref=events}, {name=active, table_ref=events}], interface=null}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "DELETE");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:25) is recognized as Snowflake-specific grammar.",
						1, 25, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{DELETE={delete={table={alias=null, table=events}, where={and={1={condition={left={function={function_name=reduce, parameters={1={column={name=tags, table_ref=null}}, 2={column={name=initial_value, table_ref=null}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=null}}, operator=+}}}}}}}, operator=IS NULL}}, 2={condition={left={column={name=active, table_ref=null}}, right={literal=0}, operator==}}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={initial_value=[[@8,38:50='initial_value',<459>,1:38]], active=[[@25,98:103='active',<459>,1:98]], threshold=[[@20,75:83='threshold',<459>,1:75]], tags=[[@6,32:35='tags',<459>,1:32]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_delete0={query_dictionary={}, table_dictionary={events={initial_value=[[@8,38:50='initial_value',<459>,1:38]], active=[[@25,98:103='active',<459>,1:98]], threshold=[[@20,75:83='threshold',<459>,1:75]], tags=[[@6,32:35='tags',<459>,1:32]]}}, filters=[{name=tags, table_ref=events}, {name=initial_value, table_ref=events}, {name=threshold, table_ref=events}, {name=active, table_ref=events}], interface=null}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC031_reduceDDLSnippetV0Test() {
		final String query = "CREATE VIEW v_reduce AS SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.ddl();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:37) is recognized as Snowflake-specific grammar.",
						1, 37, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{DDL={create={type=VIEW, name={table=v_reduce}, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[]",
				"{}",
				"{events={initial_value=[[@15,52:52='a',<459>,1:52]], active=[[@39,140:140='a',<459>,1:140]], threshold=[[@29,91:91='a',<459>,1:91]], id=[[@5,31:31='a',<459>,1:31]], tags=[[@11,44:44='a',<459>,1:44]]}}",
				"{query0={result_value=[[@34,107:118='result_value',<459>,1:107]], id=[[@7,33:34='id',<459>,1:33]]}}",
				"{def_create1={def_query0={query_dictionary={result_value=[[@34,107:118='result_value',<459>,1:107]], id=[[@7,33:34='id',<459>,1:33]]}, table_dictionary={events={initial_value=[[@15,52:52='a',<459>,1:52]], active=[[@39,140:140='a',<459>,1:140]], threshold=[[@29,91:91='a',<459>,1:91]], id=[[@5,31:31='a',<459>,1:31]], tags=[[@11,44:44='a',<459>,1:44]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "DDL");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:37) is recognized as Snowflake-specific grammar.",
						1, 37, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{DDL={create={type=VIEW, name={table=v_reduce}, query={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={initial_value=[[@15,52:52='a',<459>,1:52]], active=[[@39,140:140='a',<459>,1:140]], threshold=[[@29,91:91='a',<459>,1:91]], id=[[@5,31:31='a',<459>,1:31]], tags=[[@11,44:44='a',<459>,1:44]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{query0={result_value=[[@34,107:118='result_value',<459>,1:107]], id=[[@7,33:34='id',<459>,1:33]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_create1={def_query0={query_dictionary={result_value=[[@34,107:118='result_value',<459>,1:107]], id=[[@7,33:34='id',<459>,1:33]]}, table_dictionary={events={initial_value=[[@15,52:52='a',<459>,1:52]], active=[[@39,140:140='a',<459>,1:140]], threshold=[[@29,91:91='a',<459>,1:91]], id=[[@5,31:31='a',<459>,1:31]], tags=[[@11,44:44='a',<459>,1:44]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC031_reduceSCRIPTSnippetV0Test() {
		final String query = "SELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1;\nSELECT b.id FROM orders b WHERE b.active = 1;\nSELECT a.id, reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold) AS result_value FROM events a WHERE a.active = 1;";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.script();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 2,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_array_higher_order_function).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:3 c:13) is recognized as Snowflake-specific grammar.",
						3, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SCRIPT={1={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, 2={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=orders}}, where={condition={left={column={name=active, table_ref=b}}, right={literal=1}, operator==}}}, 3={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}",
				"[]",
				"{SCRIPT={1={}, 2={}, 3={}}}",
				"{SCRIPT={1={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, 2={orders={active=[[@49,162:162='b',<459>,2:32]], id=[[@42,137:137='b',<459>,2:7]]}}, 3={events={initial_value=[[@66,204:204='a',<459>,3:28]], active=[[@90,292:292='a',<459>,3:116]], threshold=[[@80,243:243='a',<459>,3:67]], id=[[@56,183:183='a',<459>,3:7]], tags=[[@62,196:196='a',<459>,3:20]]}}}}",
				"{SCRIPT={1={query0={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}}, 2={query0={id=[[@44,139:140='id',<459>,2:9]]}}, 3={query0={result_value=[[@85,259:270='result_value',<459>,3:83]], id=[[@58,185:186='id',<459>,3:9]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}, 2={def_query0={query_dictionary={id=[[@44,139:140='id',<459>,2:9]]}, table_dictionary={orders={active=[[@49,162:162='b',<459>,2:32]], id=[[@42,137:137='b',<459>,2:7]]}}, filters=[{name=active, table_ref=b}], interface={id=[{name=id, table_ref=b}]}, table_alias={b=orders}}}, 3={def_query0={query_dictionary={result_value=[[@85,259:270='result_value',<459>,3:83]], id=[[@58,185:186='id',<459>,3:9]]}, table_dictionary={events={initial_value=[[@66,204:204='a',<459>,3:28]], active=[[@90,292:292='a',<459>,3:116]], threshold=[[@80,243:243='a',<459>,3:67]], id=[[@56,183:183='a',<459>,3:7]], tags=[[@62,196:196='a',<459>,3:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "SCRIPT");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'reduce' at (l:3 c:13) is recognized as Snowflake-specific grammar.",
						3, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{SCRIPT={1={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, 2={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=orders}}, where={condition={left={column={name=active, table_ref=b}}, right={literal=1}, operator==}}}, 3={select={1={column={name=id, table_ref=a}}, 2={function={function_name=reduce, parameters={1={column={name=tags, table_ref=a}}, 2={column={name=initial_value, table_ref=a}}, 3={lambda={parameters={1={name=acc}, 2={name=x}}, body={calc={left={calc={left={lambda_reference={name=acc, depth=0}}, right={lambda_reference={name=x, depth=0}}, operator=+}}, right={column={name=threshold, table_ref=a}}, operator=+}}}}}}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{SCRIPT={1={}, 2={}, 3={}}}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{SCRIPT={1={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, 2={orders={active=[[@49,162:162='b',<459>,2:32]], id=[[@42,137:137='b',<459>,2:7]]}}, 3={events={initial_value=[[@66,204:204='a',<459>,3:28]], active=[[@90,292:292='a',<459>,3:116]], threshold=[[@80,243:243='a',<459>,3:67]], id=[[@56,183:183='a',<459>,3:7]], tags=[[@62,196:196='a',<459>,3:20]]}}}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{SCRIPT={1={query0={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}}, 2={query0={id=[[@44,139:140='id',<459>,2:9]]}}, 3={query0={result_value=[[@85,259:270='result_value',<459>,3:83]], id=[[@58,185:186='id',<459>,3:9]]}}}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{SCRIPT={1={def_query0={query_dictionary={result_value=[[@30,83:94='result_value',<459>,1:83]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={initial_value=[[@11,28:28='a',<459>,1:28]], active=[[@35,116:116='a',<459>,1:116]], threshold=[[@25,67:67='a',<459>,1:67]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@7,20:20='a',<459>,1:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}, 2={def_query0={query_dictionary={id=[[@44,139:140='id',<459>,2:9]]}, table_dictionary={orders={active=[[@49,162:162='b',<459>,2:32]], id=[[@42,137:137='b',<459>,2:7]]}}, filters=[{name=active, table_ref=b}], interface={id=[{name=id, table_ref=b}]}, table_alias={b=orders}}}, 3={def_query0={query_dictionary={result_value=[[@85,259:270='result_value',<459>,3:83]], id=[[@58,185:186='id',<459>,3:9]]}, table_dictionary={events={initial_value=[[@66,204:204='a',<459>,3:28]], active=[[@90,292:292='a',<459>,3:116]], threshold=[[@80,243:243='a',<459>,3:67]], id=[[@56,183:183='a',<459>,3:7]], tags=[[@62,196:196='a',<459>,3:20]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}, {name=initial_value, table_ref=a}, {name=threshold, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC031_reduceTrailingCommaSyntaxV0Test() {
		final String query = "SELECT reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold,) AS result_value FROM events a";
		final SQLSelectParserParser parser = parse(query);
		parser.sql();
		final errorhandling.ParseErrorCollector collector =
				(errorhandling.ParseErrorCollector) parser.getErrorHandler();
		Assert.assertEquals("Parser error count is wrong", 3, collector.getErrorCount());
		final java.util.List<ParseDiagnostic> diagnostics = new java.util.ArrayList<>(collector.getDiagnostics());
		diagnostics.addAll(collectParserListenerDiagnostics(parser));
		Assert.assertEquals("Parser diagnostics are wrong", Arrays.asList(
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"REPORT_ERROR",
						"Line 1:72 - unexpected input: ',' in rule value_expression: SELECT reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold,) AS result_value FROM events a",
						1, 72, "ParseErrorCollector", "value_expression", ",", false, "parse.strategy", "NoViableAltException", java.util.Map.of("contextSnippet", "SELECT reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold,) AS result_value FROM events a", "parserRules", "value_expression,select_item,select_list", "syntaxClass", "GRAMMAR_GAP")),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER",
						"Line 1:72 - Syntax error, attempting recovery",
						1, 72, "ParseErrorCollector", null, ",", true, "parse.strategy", "NoViableAltException", null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER_INLINE",
						"Line 1:13 - Invalid syntax near '('",
						1, 13, "ParseErrorCollector", null, "(", true, "parse.strategy", null, null)), diagnostics);
	}

	@Test
	public void arC031_reduceMissingParenthesisSyntaxV0Test() {
		final String query = "SELECT reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold AS result_value FROM events a";
		final SQLSelectParserParser parser = parse(query);
		parser.sql();
		final errorhandling.ParseErrorCollector collector =
				(errorhandling.ParseErrorCollector) parser.getErrorHandler();
		Assert.assertEquals("Parser error count is wrong", 3, collector.getErrorCount());
		final java.util.List<ParseDiagnostic> diagnostics = new java.util.ArrayList<>(collector.getDiagnostics());
		diagnostics.addAll(collectParserListenerDiagnostics(parser));
		Assert.assertEquals("Parser diagnostics are wrong", Arrays.asList(
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"REPORT_ERROR",
						"Line 1:73 - unexpected input: 'AS' in rule value_expression: SELECT reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold AS result_value FROM events a",
						1, 73, "ParseErrorCollector", "value_expression", "AS", false, "parse.strategy", "NoViableAltException", java.util.Map.of("contextSnippet", "SELECT reduce(a.tags, a.initial_value, (acc, x) -> acc + x + a.threshold AS result_value FROM events a", "parserRules", "value_expression,select_item,select_list", "syntaxClass", "GRAMMAR_GAP")),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER",
						"Line 1:73 - Syntax error, attempting recovery",
						1, 73, "ParseErrorCollector", null, "AS", true, "parse.strategy", "NoViableAltException", null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER_INLINE",
						"Line 1:13 - Invalid syntax near '('",
						1, 13, "ParseErrorCollector", null, "(", true, "parse.strategy", null, null)), diagnostics);
	}

	@Test
	public void arC031_reduceMissingLambdaBodySyntaxV0Test() {
		final String query = "SELECT reduce(a.tags, a.initial_value, (acc, x) -> ) AS result_value FROM events a";
		final SQLSelectParserParser parser = parse(query);
		parser.sql();
		final errorhandling.ParseErrorCollector collector =
				(errorhandling.ParseErrorCollector) parser.getErrorHandler();
		Assert.assertEquals("Parser error count is wrong", 3, collector.getErrorCount());
		final java.util.List<ParseDiagnostic> diagnostics = new java.util.ArrayList<>(collector.getDiagnostics());
		diagnostics.addAll(collectParserListenerDiagnostics(parser));
		Assert.assertEquals("Parser diagnostics are wrong", Arrays.asList(
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"REPORT_ERROR",
						"Line 1:51 - unexpected input: ')' in rule value_expression: SELECT reduce(a.tags, a.initial_value, (acc, x) -> ) AS result_value FROM events a",
						1, 51, "ParseErrorCollector", "value_expression", ")", false, "parse.strategy", "NoViableAltException", java.util.Map.of("contextSnippet", "SELECT reduce(a.tags, a.initial_value, (acc, x) -> ) AS result_value FROM events a", "parserRules", "value_expression,select_item,select_list", "syntaxClass", "GRAMMAR_GAP")),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER",
						"Line 1:51 - Syntax error, attempting recovery",
						1, 51, "ParseErrorCollector", null, ")", true, "parse.strategy", "NoViableAltException", null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER_INLINE",
						"Line 1:13 - Invalid syntax near '('",
						1, 13, "ParseErrorCollector", null, "(", true, "parse.strategy", null, null)), diagnostics);
	}
}
