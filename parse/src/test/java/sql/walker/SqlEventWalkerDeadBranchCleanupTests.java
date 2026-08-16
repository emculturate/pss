package sql.walker;

import org.junit.Assert;
import org.junit.Test;

/**
 * Walker cleanup: remove grammar-unreachable branches in {@code SqlParseEventWalker}
 * (assignment list parent check, insert preamble else, static type child-count ladders,
 * DDL options map unwrap). {@code exitSet_operation_member} predicand substitution branch retained
 * (GROUP BY ({@code <a>}) uses {@code subquery} → {@code set_operation_member} under {@code predicand_subquery}).
 */
public class SqlEventWalkerDeadBranchCleanupTests extends AbstractSqlParseEventWalkerTest {

	private void assertWalkerOutputs(SqlParseEventWalker extractor, String expectedAst,
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
	public void insertOverwritePreambleDeadBranchCleanupTest() {
		final String query = "INSERT OVERWRITE INTO staging.dst SELECT score FROM src";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWalkerOutputs(extractor,
				"{SQL={insert={preamble=insert_overwrite_into, from={from={table={alias=null, table=src}}, select={1={column={name=score, table_ref=null}}}}, target_table={table={schema=staging, alias=null, table=dst}}}}}",
				"[score]", "{}",
				"{src={score=[[@7,41:45='score',<393>,1:41]]}, staging.dst={score=[[@7,41:45='score',<393>,1:41]]}}",
				"{query0={score=[[@7,41:45='score',<393>,1:41]]}, insert1={score=[[@7,41:45='score',<393>,1:41]]}}",
				"{def_insert1={query_dictionary={score=[[@7,41:45='score',<393>,1:41]]}, table_dictionary={staging.dst={score=[[@7,41:45='score',<393>,1:41]]}}, def_query0={query_dictionary={score=[[@7,41:45='score',<393>,1:41]]}, table_dictionary={src={score=[[@7,41:45='score',<393>,1:41]]}}, interface={score=[{name=score, table_ref=src}]}}, interface={score=[{name=score, table_ref=query0}]}}}");
	}

	@Test
	public void jinjaKeywordArgDeadBranchCleanupTest() {
		final String query = "SELECT * FROM {{ ref('my_model', v=2) }}";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWalkerOutputs(extractor,
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={table={substitution={name={{ ref('my_model', v=2) }}, parts={jinja_table={function_name=ref, parameters={1={literal='my_model'}}}}, type=tuple}, alias=null}}}}",
				"[*]", "{{{ ref('my_model', v=2) }}=tuple}",
				"{{{ ref('my_model', v=2) }}={*=[[@1,7:7='*',<291>,1:7]]}}",
				"{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				"{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={{{ ref('my_model', v=2) }}={*=[[@1,7:7='*',<291>,1:7]]}}, interface={*=[{name=*, table_ref=*}]}}}");
	}

	@Test
	public void castTimestampWithoutTimeZoneStaticTypeDeadBranchCleanupTest() {
		final String query = "SELECT CAST('x' AS TIMESTAMP WITHOUT TIME ZONE) AS t FROM tab1";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertWalkerOutputs(extractor,
				"{SQL={select={1={function={function_name=CAST, data_type={type=TIMESTAMP WITHOUT TIME ZONE}, type=CAST, value={literal='x'}}, alias=t}}, from={table={alias=null, table=tab1}}}}",
				"[t]", "{}",
				"{tab1={}}",
				"{query0={t=[[@11,51:51='t',<393>,1:51]]}}",
				"{def_query0={query_dictionary={t=[[@11,51:51='t',<393>,1:51]]}, table_dictionary={tab1={}}, interface={t=[]}}}");
	}
}
