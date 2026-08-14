package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import sql.SQLSelectParserParser;

/**
 * Post-{@code SELECT}-list clauses (QUALIFY, HAVING, ORDER BY, …) that contain
 * {@code OVER (PARTITION BY … ORDER BY …)} must not leave window-interface latch
 * state that bleeds into a later query scope (outer WITH body, join partner, etc.).
 */
public class SqlEventWalkerWindowScopeIsolationTests extends AbstractSqlParseEventWalkerTest {

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
				"{tab1={id=[[@5,23:24='id',<392>,1:23]], col2=[[@9,33:36='col2',<392>,1:33], [@23,102:105='col2',<392>,1:102]], col1=[[@7,27:30='col1',<392>,1:27], [@20,88:91='col1',<392>,1:88]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={id=[[@5,23:24='id',<392>,1:23]], col2=[[@9,33:36='col2',<392>,1:33]], col1=[[@7,27:30='col1',<392>,1:27], [@29,120:123='col1',<392>,1:120]]}, query1={col1=[[@29,120:123='col1',<392>,1:120]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={context_list={ranked=query0}, query_dictionary={col1=[[@29,120:123='col1',<392>,1:120]]}, def_query0={window_ordered_by=[{name=col2, table_ref=tab1}], query_dictionary={id=[[@5,23:24='id',<392>,1:23]], col2=[[@9,33:36='col2',<392>,1:33]], col1=[[@7,27:30='col1',<392>,1:27], [@29,120:123='col1',<392>,1:120]]}, table_dictionary={tab1={id=[[@5,23:24='id',<392>,1:23]], col2=[[@9,33:36='col2',<392>,1:33], [@23,102:105='col2',<392>,1:102]], col1=[[@7,27:30='col1',<392>,1:27], [@20,88:91='col1',<392>,1:88]]}}, window_partition_by=[{name=col1, table_ref=tab1}], filters=[{name=col1, table_ref=tab1}, {name=col2, table_ref=tab1}], interface={id=[{name=id, table_ref=tab1}], col2=[{name=col2, table_ref=tab1}], col1=[{name=col1, table_ref=tab1}]}}, interface={col1=[{name=col1, table_ref=query0}]}, table_alias={ranked=query0}}}",
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
				"{<[Enrollment Services].[Client Entering Class]>={<select column>=[[@5,26:28='cec',<392>,1:26]], non_variable_col=[[@19,143:145='cec',<392>,1:143], [@28,193:195='cec',<392>,1:193]], <where column>=[[@23,165:167='cec',<392>,1:165]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={<select column>=[[@7,30:44='<select column>',<327>,1:30], [@36,228:230='cec',<392>,1:228]]}, query1={<select column>=[[@38,232:246='<select column>',<327>,1:232]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={context_list={wrapped=query0, cec=query0}, query_dictionary={<select column>=[[@38,232:246='<select column>',<327>,1:232]]}, def_query0={window_ordered_by=[{name=non_variable_col, table_ref=cec}], query_dictionary={<select column>=[[@7,30:44='<select column>',<327>,1:30], [@36,228:230='cec',<392>,1:228]]}, table_dictionary={<[Enrollment Services].[Client Entering Class]>={<select column>=[[@5,26:28='cec',<392>,1:26]], non_variable_col=[[@19,143:145='cec',<392>,1:143], [@28,193:195='cec',<392>,1:193]], <where column>=[[@23,165:167='cec',<392>,1:165]]}}, window_partition_by=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], filters=[{name=non_variable_col, table_ref=cec}, {substitution={name=<where column>, type=column}, table_ref=cec}], interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=<[Enrollment Services].[Client Entering Class]>}}, interface={<select column>=[{substitution={name=<select column>, type=column}, table_ref=cec}]}, table_alias={cec=query0, wrapped=query0}}}",
				extractor.getSymbolTable().toString());
	}
}
