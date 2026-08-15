package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import sql.SQLSelectParserParser;

/**
 * Phase 2.2 — unparenthesized {@code VALUES … AS alias (cols)} at each
 * {@code values_statement_primary} site (no extra outer parens around VALUES).
 */
public class SqlEventWalkerUnparenthesizedValuesTests extends AbstractSqlParseEventWalkerTest {

	@Test
	public void fromClauseUnparenthesizedValuesWithAliasAndColumnsTest() {
		final String query =
				"SELECT col1, col2 FROM VALUES (100, 1) AS value_src (col1, col2)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={values={columns={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, alias=value_src, matrix={1={row={1={literal=100}, 2={literal=1}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{values0={col2=[[@16,59:62='col2',<392>,1:59], [@3,13:16='col2',<392>,1:13]], col1=[[@14,53:56='col1',<392>,1:53], [@1,7:10='col1',<392>,1:7]]}, query1={col2=[[@3,13:16='col2',<392>,1:13]], col1=[[@1,7:10='col1',<392>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={col2=[[@3,13:16='col2',<392>,1:13]], col1=[[@1,7:10='col1',<392>,1:7]]}, def_values0={query_dictionary={col2=[[@16,59:62='col2',<392>,1:59], [@3,13:16='col2',<392>,1:13]], col1=[[@14,53:56='col1',<392>,1:53], [@1,7:10='col1',<392>,1:7]]}, interface={col2=[], col1=[]}}, interface={col2=[{name=col2, table_ref=values0}], col1=[{name=col1, table_ref=values0}]}, table_alias={value_src=values0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void cteBodyUnparenthesizedValuesWithAliasAndColumnsTest() {
		final String query =
				"WITH src AS (VALUES (1, 2) AS v (col1, col2)) SELECT col1 FROM src";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={values={columns={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, alias=v, matrix={1={row={1={literal=1}, 2={literal=2}}}}}}, alias=src}}, query={select={1={column={name=col1, table_ref=null}}}, from={table={alias=null, table=src}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{values0={col2=[[@15,39:42='col2',<392>,1:39]], col1=[[@13,33:36='col1',<392>,1:33], [@19,53:56='col1',<392>,1:53]]}, query1={col1=[[@19,53:56='col1',<392>,1:53]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={context_list={src=values0}, query_dictionary={col1=[[@19,53:56='col1',<392>,1:53]]}, def_values0={query_dictionary={col2=[[@15,39:42='col2',<392>,1:39]], col1=[[@13,33:36='col1',<392>,1:33], [@19,53:56='col1',<392>,1:53]]}, interface={col2=[], col1=[]}}, interface={col1=[{name=col1, table_ref=values0}]}, table_alias={src=values0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void scriptDmlUnparenthesizedValuesWithAliasAndColumnsTest() {
		final String query = "VALUES (10), (20) AS v (n);";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runScriptParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SCRIPT={1={values={columns={1={column={name=n, table_ref=null}}}, alias=v, matrix={1={row={1={literal=10}}}, 2={row={1={literal=20}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{SCRIPT={1={}}}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{SCRIPT={1={}}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{SCRIPT={1={values0={n=[[@11,24:24='n',<392>,1:24]]}}}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{SCRIPT={1={def_values0={query_dictionary={n=[[@11,24:24='n',<392>,1:24]]}, interface={n=[]}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void valuesEndpointUnparenthesizedValuesWithAliasAndColumnsTest() {
		final String query = "VALUES (1, 'aaa') AS source (col1, col2)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runValuesStatementEndParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{VALUES={values={columns={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, alias=source, matrix={1={row={1={literal=1}, 2={literal='aaa'}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{values0={col2=[[@11,35:38='col2',<392>,1:35]], col1=[[@9,29:32='col1',<392>,1:29]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_values0={query_dictionary={col2=[[@11,35:38='col2',<392>,1:35]], col1=[[@9,29:32='col1',<392>,1:29]]}, interface={col2=[], col1=[]}}}",
				extractor.getSymbolTable().toString());
	}

}
