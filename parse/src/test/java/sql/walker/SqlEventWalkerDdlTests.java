package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import sql.SQLSelectParserParser;

/**
 * Dedicated DDL golden tests. DDL scopes are thin (type/name/options/query), so these
 * assert AST, symbol table, and diagnostics — not table/query dictionaries.
 */
public class SqlEventWalkerDdlTests extends AbstractSqlParseEventWalkerTest {

	@Test
	public void dropTableIfExistsOptionsVerbatimTest() {
		final String query = "drop table mydb.myschema.tab1 if exists";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);

		Assert.assertEquals("AST is wrong",
				"{DDL={drop={type=table, name={schema=myschema, dbname=mydb, table=tab1}, options=if exists}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_drop0={}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals("Diagnostics are wrong", "[]",
				extractor.getSnippet().getParserDiagnosticList().toString());
	}

	@Test
	public void dropTableOptionsPreserveCaseAndParensTest() {
		final String query = "DROP TABLE demo.old IF EXISTS (CASCADE)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);

		Assert.assertEquals("AST is wrong",
				"{DDL={drop={type=table, name={schema=demo, table=old}, options=IF EXISTS (CASCADE)}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_drop0={}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals("Diagnostics are wrong", "[]",
				extractor.getSnippet().getParserDiagnosticList().toString());
	}

	@Test
	public void dropTableMultilineOptionsVerbatimTest() {
		final String query = "drop table demo.old\n  if exists\n  cascade";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);

		Assert.assertEquals("AST is wrong",
				"{DDL={drop={type=table, name={schema=demo, table=old}, options=if exists\n  cascade}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_drop0={}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals("Diagnostics are wrong", "[]",
				extractor.getSnippet().getParserDiagnosticList().toString());
	}

	@Test
	public void alterTableRenameOptionsVerbatimTest() {
		final String query = "alter table mydb.myschema.tab1 rename to tab2";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);

		Assert.assertEquals("AST is wrong",
				"{DDL={alter={type=table, name={schema=myschema, dbname=mydb, table=tab1}, options=rename to tab2}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_alter0={}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals("Diagnostics are wrong", "[]",
				extractor.getSnippet().getParserDiagnosticList().toString());
	}

	@Test
	public void createSequenceOptionsWithCommaAndParenVerbatimTest() {
		final String query = "create sequence mydb.myschema.seq1 START WITH 1, INCREMENT BY 1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);

		Assert.assertEquals("AST is wrong",
				"{DDL={create={type=sequence, name={schema=myschema, dbname=mydb, table=seq1}, clauses=START WITH 1, INCREMENT BY 1}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_create0={}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals("Diagnostics are wrong", "[]",
				extractor.getSnippet().getParserDiagnosticList().toString());
	}

	@Test
	public void createFunctionNestedParenParametersVerbatimTest() {
		final String query = "create function myschema.fn1(arg1 int, arg2 varchar(10)) returns int language sql";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);

		Assert.assertEquals("AST is wrong",
				"{DDL={create={type=function, name={schema=myschema, table=fn1}, parameters=arg1 int, arg2 varchar(10), data_type={type=INT}, clauses=language sql}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_create0={}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals("Diagnostics are wrong", "[]",
				extractor.getSnippet().getParserDiagnosticList().toString());
	}
}
