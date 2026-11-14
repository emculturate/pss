package sql.walker;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import access.SqlParserAccess;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

public class SqlEventWalkerQualifiedColumnVariableTests extends AbstractSqlParseEventWalkerTest {
	private static final String BINDING_WARNING = "QUALIFIED_COLUMN_VARIABLE_REQUIRES_BINDING";

	private Snippet parse(String sql, String endpoint) {
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(sql, endpoint);
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertNotNull(parser.getSnippet());
		return parser.getSnippet();
	}

	private List<ParseDiagnostic> diagnostics(Snippet snippet, String code) {
		return snippet.getParserDiagnosticList().stream()
				.filter(d -> code.equals(d.code()))
				.collect(Collectors.toList());
	}

	private void assertDeferred(Snippet snippet, String variable, String alias) {
		Assert.assertEquals(snippet.getFatalErrorStringList().toString(), 0, snippet.getFatalErrorCount());
		List<ParseDiagnostic> warnings = diagnostics(snippet, BINDING_WARNING);
		Assert.assertEquals(warnings.toString(), 1, warnings.size());
		ParseDiagnostic warning = warnings.get(0);
		Assert.assertEquals(ParseDiagnostic.Severity.WARNING, warning.severity());
		Assert.assertEquals(String.format(
				"Column variable '%s' at (l:%s c:%s) must be explicitly bound to a column exported by query alias '%s'; this binding has not been validated.",
				variable, warning.line(), warning.charPositionInLine(), alias), warning.message());
		Assert.assertNotNull(warning.line());
		Assert.assertNotNull(warning.charPositionInLine());
		Assert.assertEquals("column", snippet.getSubstitutionsMap().get(variable));
	}

	@Test
	public void qualifiedColumnVariableSelectGoldenTest() {
		final String query = "SELECT sub.<Contact ID> AS selected FROM (SELECT id FROM t) sub";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertDiagnosticAtPosition(extractor.getSnippet(), BINDING_WARNING, ParseDiagnostic.Severity.WARNING,
				"Column variable '<Contact ID>' at (l:1 c:7) must be explicitly bound to a column exported by query alias 'sub'; this binding has not been validated.",
				"sub.<Contact ID>", 1, 7);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={substitution={name=<Contact ID>, type=column}, table_ref=sub}, alias=selected}}, from={table={alias=sub, query={select={1={column={name=id, table_ref=null}}}, from={table={alias=null, table=t}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[selected]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<Contact ID>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={id=[[@9,49:50='id',<419>,1:49]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={id=[[@9,49:50='id',<419>,1:49]]}, query1={selected=[[@5,27:34='selected',<419>,1:27]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={selected=[[@5,27:34='selected',<419>,1:27]]}, def_query0={query_dictionary={id=[[@9,49:50='id',<419>,1:49]]}, table_dictionary={t={id=[[@9,49:50='id',<419>,1:49]]}}, interface={id=[{name=id, table_ref=t}]}}, interface={selected=[{substitution={name=<Contact ID>, type=column}, table_ref=sub}]}, table_alias={sub=query0}}}", extractor.getSymbolTable().toString());
		Assert.assertEquals("Array Output Collectors are wrong", "{SQL={queryInterface=[selected]}}", extractor.getArrayOutputCollectorsMap().toString());
		Assert.assertEquals("Warning is wrong", "Column variable '<Contact ID>' at (l:1 c:7) must be explicitly bound to a column exported by query alias 'sub'; this binding has not been validated.", diagnostics(extractor.getSnippet(), BINDING_WARNING).get(0).message());
		Assert.assertEquals(1, extractor.getSnippet().getParserDiagnosticList().size());
	}

	@Test
	public void qualifiedColumnVariableJoinGoldenTest() {
		final String query = "SELECT sub.id FROM (SELECT id FROM t) sub JOIN b ON sub.<Contact ID> = b.<Contact ID>";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertDiagnosticAtPosition(extractor.getSnippet(), BINDING_WARNING, ParseDiagnostic.Severity.WARNING,
				"Column variable '<Contact ID>' at (l:1 c:52) must be explicitly bound to a column exported by query alias 'sub'; this binding has not been validated.",
				"sub.<Contact ID>", 1, 52);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=id, table_ref=sub}}}, from={join={1={table={alias=sub, query={select={1={column={name=id, table_ref=null}}}, from={table={alias=null, table=t}}}}}, 2={join=JOIN, on={condition={left={column={substitution={name=<Contact ID>, type=column}, table_ref=sub}}, right={column={substitution={name=<Contact ID>, type=column}, table_ref=b}}, operator==}}}, 3={table={alias=null, table=b}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[id]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<Contact ID>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{b={<Contact ID>=[[@19,71:71='b',<419>,1:71]]}, t={id=[[@7,27:28='id',<419>,1:27]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={id=[[@7,27:28='id',<419>,1:27], [@1,7:9='sub',<419>,1:7]]}, query1={id=[[@3,11:12='id',<419>,1:11]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={id=[[@3,11:12='id',<419>,1:11]]}, table_dictionary={b={<Contact ID>=[[@19,71:71='b',<419>,1:71]]}}, def_query0={query_dictionary={id=[[@7,27:28='id',<419>,1:27], [@1,7:9='sub',<419>,1:7]]}, table_dictionary={t={id=[[@7,27:28='id',<419>,1:27]]}}, interface={id=[{name=id, table_ref=t}]}}, filters=[{substitution={name=<Contact ID>, type=column}, table_ref=sub}, {substitution={name=<Contact ID>, type=column}, table_ref=b}], interface={id=[{name=id, table_ref=sub}]}, table_alias={sub=query0}}}", extractor.getSymbolTable().toString());
		Assert.assertEquals("Array Output Collectors are wrong", "{SQL={queryInterface=[id]}}", extractor.getArrayOutputCollectorsMap().toString());
		Assert.assertEquals("Warning is wrong", "Column variable '<Contact ID>' at (l:1 c:52) must be explicitly bound to a column exported by query alias 'sub'; this binding has not been validated.", diagnostics(extractor.getSnippet(), BINDING_WARNING).get(0).message());
		Assert.assertEquals(1, extractor.getSnippet().getParserDiagnosticList().size());
	}

	@Test
	public void derivedAndCteColumnsDeferBindingTest() {
		assertDeferred(parse("SELECT sub.<Contact ID> FROM (SELECT id FROM t) sub", "SQL"),
				"<Contact ID>", "sub");
		assertDeferred(parse("WITH sub AS (SELECT id FROM t) SELECT sub.<Contact ID> FROM sub", "SQL"),
				"<Contact ID>", "sub");
		assertDeferred(parse("(SELECT sub.id FROM (SELECT id FROM t) sub "
				+ "JOIN b ON sub.<Contact ID> = b.<Contact ID>)", "TUPLE"),
				"<Contact ID>", "sub");
	}

	@Test
	public void missingAliasAndConcreteColumnRemainFatalTest() {
		Snippet missingAlias = parse("SELECT ghost.<Contact ID> FROM (SELECT id FROM t) sub", "SQL");
		Assert.assertTrue(missingAlias.getFatalErrorCount() > 0);
		Assert.assertTrue(diagnostics(missingAlias, BINDING_WARNING).isEmpty());

		Snippet missingColumn = parse("SELECT sub.missing FROM (SELECT id FROM t) sub", "SQL");
		Assert.assertTrue(missingColumn.getFatalErrorCount() > 0);
		Assert.assertFalse(diagnostics(missingColumn, "QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS").isEmpty());
	}

	@Test
	public void physicalColumnVariablesDoNotRequireQueryBindingTest() {
		Snippet snippet = parse("SELECT t.<Contact ID> FROM tbl t", "SQL");
		Assert.assertEquals(0, snippet.getFatalErrorCount());
		Assert.assertTrue(diagnostics(snippet, BINDING_WARNING).isEmpty());
	}

	@Test
	public void dottedVariableAndRepeatedReferencesTest() {
		Snippet snippet = parse("SELECT sub.id FROM (SELECT id FROM t) sub "
				+ "WHERE sub.<Contact.ID> = sub.<Contact.ID>", "SQL");
		assertDeferred(snippet, "<Contact.ID>", "sub");
	}

	@Test
	public void variableReferencePreservesInterfaceAndLineageTest() {
		Snippet snippet = parse("SELECT sub.<Contact ID> AS selected FROM (SELECT id FROM t) sub", "SQL");
		assertDeferred(snippet, "<Contact ID>", "sub");
		Assert.assertEquals(Integer.valueOf(1), diagnostics(snippet, BINDING_WARNING).get(0).line());
		Assert.assertEquals(Integer.valueOf(7), diagnostics(snippet, BINDING_WARNING).get(0).charPositionInLine());
	}

	@Test
	public void inaccessibleChildAliasAndMirrorImageRemainFatalTest() {
		Snippet childAlias = parse("SELECT inner_t.<Contact ID> FROM "
				+ "(SELECT inner_t.id FROM t inner_t) sub", "SQL");
		Assert.assertTrue(childAlias.getFatalErrorStringList().toString(), childAlias.getFatalErrorCount() > 0);
		Assert.assertTrue(diagnostics(childAlias, BINDING_WARNING).isEmpty());
		Snippet siblingAlias = parse("SELECT a.id FROM "
				+ "(SELECT id FROM t) a JOIN (SELECT a.<Contact ID> AS id FROM u) b ON a.id = b.id", "SQL");
		Assert.assertTrue(siblingAlias.getFatalErrorStringList().toString(), siblingAlias.getFatalErrorCount() > 0);
		Assert.assertTrue(diagnostics(siblingAlias, BINDING_WARNING).isEmpty());

		Snippet mirror = parse("SELECT sub.id FROM (SELECT t.<Contact ID> FROM t) sub", "SQL");
		Assert.assertTrue(mirror.getFatalErrorStringList().toString(), mirror.getFatalErrorCount() > 0);
		Assert.assertTrue(diagnostics(mirror, BINDING_WARNING).isEmpty());
	}

	@Test
	public void openInterfaceAndMatchingVariableKeepExistingBehaviorTest() {
		Snippet wildcard = parse("SELECT sub.<Contact ID> FROM (SELECT * FROM t) sub", "SQL");
		Assert.assertEquals(0, wildcard.getFatalErrorCount());
		Assert.assertEquals(0, diagnostics(wildcard, BINDING_WARNING).size());
		Snippet matching = parse("SELECT sub.<Contact ID> FROM (SELECT t.<Contact ID> FROM t) sub", "SQL");
		Assert.assertEquals(0, matching.getFatalErrorCount());
		Assert.assertEquals(0, diagnostics(matching, BINDING_WARNING).size());
	}

	@Test
	public void nestedAndCorrelatedQueryVariablesTest() {
		assertDeferred(parse("SELECT sub.id FROM (SELECT id FROM t) sub "
				+ "WHERE EXISTS (SELECT b.id FROM b WHERE b.id = sub.<Contact ID>)", "SQL"),
				"<Contact ID>", "sub");
		Snippet siblings = parse("SELECT a.id FROM "
				+ "(SELECT sub.<Contact ID> AS id FROM (SELECT id FROM t) sub) a "
				+ "JOIN (SELECT sub.<Contact ID> AS id FROM (SELECT id FROM u) sub) b ON a.id = b.id", "SQL");
		Assert.assertEquals(siblings.getFatalErrorStringList().toString(), 0, siblings.getFatalErrorCount());
		Assert.assertEquals(diagnostics(siblings, BINDING_WARNING).toString(), 2, diagnostics(siblings, BINDING_WARNING).size());
	}

	@Test
	public void qualifiedColumnVariableWhereClauseGoldenTest() {
		final String query = "SELECT sub.id FROM (SELECT id FROM t) sub WHERE sub.<Contact ID> = 1";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertDiagnosticAtPosition(extractor.getSnippet(), BINDING_WARNING, ParseDiagnostic.Severity.WARNING,
				"Column variable '<Contact ID>' at (l:1 c:48) must be explicitly bound to a column exported by query alias 'sub'; this binding has not been validated.",
				"sub.<Contact ID>", 1, 48);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=id, table_ref=sub}}}, from={table={alias=sub, query={select={1={column={name=id, table_ref=null}}}, from={table={alias=null, table=t}}}}}, where={condition={left={column={substitution={name=<Contact ID>, type=column}, table_ref=sub}}, right={literal=1}, operator==}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[id]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<Contact ID>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={id=[[@7,27:28='id',<419>,1:27]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={id=[[@7,27:28='id',<419>,1:27], [@1,7:9='sub',<419>,1:7]]}, query1={id=[[@3,11:12='id',<419>,1:11]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={id=[[@3,11:12='id',<419>,1:11]]}, def_query0={query_dictionary={id=[[@7,27:28='id',<419>,1:27], [@1,7:9='sub',<419>,1:7]]}, table_dictionary={t={id=[[@7,27:28='id',<419>,1:27]]}}, interface={id=[{name=id, table_ref=t}]}}, filters=[{substitution={name=<Contact ID>, type=column}, table_ref=sub}], interface={id=[{name=id, table_ref=sub}]}, table_alias={sub=query0}}}", extractor.getSymbolTable().toString());
		Assert.assertEquals("Array Output Collectors are wrong", "{SQL={queryInterface=[id]}}", extractor.getArrayOutputCollectorsMap().toString());
		Assert.assertEquals(1, extractor.getSnippet().getParserDiagnosticList().size());
	}

	@Test
	public void qualifiedColumnVariableGroupByClauseGoldenTest() {
		final String query = "SELECT COUNT(*) FROM (SELECT id FROM t) sub GROUP BY sub.<Contact ID>";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertDiagnosticAtPosition(extractor.getSnippet(), BINDING_WARNING, ParseDiagnostic.Severity.WARNING,
				"Column variable '<Contact ID>' at (l:1 c:53) must be explicitly bound to a column exported by query alias 'sub'; this binding has not been validated.",
				"sub.<Contact ID>", 1, 53);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=COUNT, qualifier=null, parameters=*}}}, from={table={alias=sub, query={select={1={column={name=id, table_ref=null}}}, from={table={alias=null, table=t}}}}}, groupby={1={column={substitution={name=<Contact ID>, type=column}, table_ref=sub}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<Contact ID>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={id=[[@8,29:30='id',<419>,1:29]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={id=[[@8,29:30='id',<419>,1:29]]}, query1={unnamed_0=[[@4,14:14=')',<288>,1:14]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={unnamed_0=[[@4,14:14=')',<288>,1:14]]}, grouped_by=[{substitution={name=<Contact ID>, type=column}, table_ref=sub}], def_query0={query_dictionary={id=[[@8,29:30='id',<419>,1:29]]}, table_dictionary={t={id=[[@8,29:30='id',<419>,1:29]]}}, interface={id=[{name=id, table_ref=t}]}}, interface={unnamed_0=[]}, table_alias={sub=query0}}}", extractor.getSymbolTable().toString());
		Assert.assertEquals("Array Output Collectors are wrong", "{SQL={queryInterface=[unnamed_0]}}", extractor.getArrayOutputCollectorsMap().toString());
		Assert.assertEquals(1, extractor.getSnippet().getParserDiagnosticList().size());
	}

	@Test
	public void qualifiedColumnVariableHavingClauseGoldenTest() {
		final String query = "SELECT COUNT(*) AS c FROM (SELECT id FROM t) sub GROUP BY sub.id HAVING sub.<Contact ID> IS NOT NULL";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertDiagnosticAtPosition(extractor.getSnippet(), BINDING_WARNING, ParseDiagnostic.Severity.WARNING,
				"Column variable '<Contact ID>' at (l:1 c:72) must be explicitly bound to a column exported by query alias 'sub'; this binding has not been validated.",
				"sub.<Contact ID>", 1, 72);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=COUNT, qualifier=null, parameters=*}, alias=c}}, having={condition={left={column={substitution={name=<Contact ID>, type=column}, table_ref=sub}}, operator=IS NOT NULL}}, from={table={alias=sub, query={select={1={column={name=id, table_ref=null}}}, from={table={alias=null, table=t}}}}}, groupby={1={column={name=id, table_ref=sub}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[c]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<Contact ID>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={id=[[@10,34:35='id',<419>,1:34]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={id=[[@10,34:35='id',<419>,1:34], [@17,58:60='sub',<419>,1:58]]}, query1={c=[[@6,19:19='c',<419>,1:19]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={c=[[@6,19:19='c',<419>,1:19]]}, grouped_by=[{name=id, table_ref=sub}], def_query0={query_dictionary={id=[[@10,34:35='id',<419>,1:34], [@17,58:60='sub',<419>,1:58]]}, table_dictionary={t={id=[[@10,34:35='id',<419>,1:34]]}}, interface={id=[{name=id, table_ref=t}]}}, filters=[{substitution={name=<Contact ID>, type=column}, table_ref=sub}], interface={c=[]}, table_alias={sub=query0}}}", extractor.getSymbolTable().toString());
		Assert.assertEquals("Array Output Collectors are wrong", "{SQL={queryInterface=[c]}}", extractor.getArrayOutputCollectorsMap().toString());
		Assert.assertEquals(1, extractor.getSnippet().getParserDiagnosticList().size());
	}

	@Test
	public void qualifiedColumnVariableOrderByClauseGoldenTest() {
		final String query = "SELECT sub.id FROM (SELECT id FROM t) sub ORDER BY sub.<Contact ID>";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertDiagnosticAtPosition(extractor.getSnippet(), BINDING_WARNING, ParseDiagnostic.Severity.WARNING,
				"Column variable '<Contact ID>' at (l:1 c:51) must be explicitly bound to a column exported by query alias 'sub'; this binding has not been validated.",
				"sub.<Contact ID>", 1, 51);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=id, table_ref=sub}}}, orderby={1={null_order=null, predicand={column={substitution={name=<Contact ID>, type=column}, table_ref=sub}}, sort_order=ASC}}, from={table={alias=sub, query={select={1={column={name=id, table_ref=null}}}, from={table={alias=null, table=t}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[id]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<Contact ID>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={id=[[@7,27:28='id',<419>,1:27]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={id=[[@7,27:28='id',<419>,1:27], [@1,7:9='sub',<419>,1:7]]}, query1={id=[[@3,11:12='id',<419>,1:11]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={id=[[@3,11:12='id',<419>,1:11]]}, def_query0={query_dictionary={id=[[@7,27:28='id',<419>,1:27], [@1,7:9='sub',<419>,1:7]]}, table_dictionary={t={id=[[@7,27:28='id',<419>,1:27]]}}, interface={id=[{name=id, table_ref=t}]}}, ordered_by=[{substitution={name=<Contact ID>, type=column}, table_ref=sub}], interface={id=[{name=id, table_ref=sub}]}, table_alias={sub=query0}}}", extractor.getSymbolTable().toString());
		Assert.assertEquals("Array Output Collectors are wrong", "{SQL={queryInterface=[id]}}", extractor.getArrayOutputCollectorsMap().toString());
		Assert.assertEquals(1, extractor.getSnippet().getParserDiagnosticList().size());
	}

	@Test
	public void qualifiedColumnVariableWindowClauseGoldenTest() {
		final String query = "SELECT ROW_NUMBER() OVER (PARTITION BY sub.<Contact ID> ORDER BY sub.id) AS rn FROM (SELECT id FROM t) sub";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertDiagnosticAtPosition(extractor.getSnippet(), BINDING_WARNING, ParseDiagnostic.Severity.WARNING,
				"Column variable '<Contact ID>' at (l:1 c:39) must be explicitly bound to a column exported by query alias 'sub'; this binding has not been validated.",
				"sub.<Contact ID>", 1, 39);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=rn, window_function={over={partition_by={1={column={substitution={name=<Contact ID>, type=column}, table_ref=sub}}}, orderby={1={null_order=null, predicand={column={name=id, table_ref=sub}}, sort_order=ASC}}}, function={function_name=ROW_NUMBER, parameters=null}}}}, from={table={alias=sub, query={select={1={column={name=id, table_ref=null}}}, from={table={alias=null, table=t}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[rn]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<Contact ID>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={id=[[@22,92:93='id',<419>,1:92]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={id=[[@22,92:93='id',<419>,1:92], [@13,65:67='sub',<419>,1:65]]}, query1={rn=[[@18,76:77='rn',<419>,1:76]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={window_ordered_by=[{name=id, table_ref=sub}], query_dictionary={rn=[[@18,76:77='rn',<419>,1:76]]}, window_partition_by=[{substitution={name=<Contact ID>, type=column}, table_ref=sub}], def_query0={query_dictionary={id=[[@22,92:93='id',<419>,1:92], [@13,65:67='sub',<419>,1:65]]}, table_dictionary={t={id=[[@22,92:93='id',<419>,1:92]]}}, interface={id=[{name=id, table_ref=t}]}}, interface={rn=[{substitution={name=<Contact ID>, type=column}, table_ref=sub}, {name=id, table_ref=sub}]}, table_alias={sub=query0}}}", extractor.getSymbolTable().toString());
		Assert.assertEquals("Array Output Collectors are wrong", "{SQL={queryInterface=[rn]}}", extractor.getArrayOutputCollectorsMap().toString());
		Assert.assertEquals(1, extractor.getSnippet().getParserDiagnosticList().size());
	}

	@Test
	public void qualifiedColumnVariableFunctionArgumentGoldenTest() {
		final String query = "SELECT COALESCE(sub.<Contact ID>, 'x') AS c FROM (SELECT id FROM t) sub";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertDiagnosticAtPosition(extractor.getSnippet(), BINDING_WARNING, ParseDiagnostic.Severity.WARNING,
				"Column variable '<Contact ID>' at (l:1 c:16) must be explicitly bound to a column exported by query alias 'sub'; this binding has not been validated.",
				"sub.<Contact ID>", 1, 16);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={parameters={1={column={substitution={name=<Contact ID>, type=column}, table_ref=sub}}, 2={literal='x'}}, function_name=COALESCE}, alias=c}}, from={table={alias=sub, query={select={1={column={name=id, table_ref=null}}}, from={table={alias=null, table=t}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[c]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<Contact ID>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={id=[[@14,57:58='id',<419>,1:57]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={id=[[@14,57:58='id',<419>,1:57]]}, query1={c=[[@10,42:42='c',<419>,1:42]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={c=[[@10,42:42='c',<419>,1:42]]}, def_query0={query_dictionary={id=[[@14,57:58='id',<419>,1:57]]}, table_dictionary={t={id=[[@14,57:58='id',<419>,1:57]]}}, interface={id=[{name=id, table_ref=t}]}}, interface={c=[{substitution={name=<Contact ID>, type=column}, table_ref=sub}]}, table_alias={sub=query0}}}", extractor.getSymbolTable().toString());
		Assert.assertEquals("Array Output Collectors are wrong", "{SQL={queryInterface=[c]}}", extractor.getArrayOutputCollectorsMap().toString());
		Assert.assertEquals(1, extractor.getSnippet().getParserDiagnosticList().size());
	}

	@Test
	public void qualifiedColumnVariableDatetimeArgumentGoldenTest() {
		final String query = "SELECT DATEADD(day, 1, sub.<Contact ID>) AS shifted FROM (SELECT id FROM t) sub";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertDiagnosticAtPosition(extractor.getSnippet(), "STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR", ParseDiagnostic.Severity.WARNING,
				"snowflake_datetime_add_expression at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null, 1, 7);
		assertDiagnosticAtPosition(extractor.getSnippet(), BINDING_WARNING, ParseDiagnostic.Severity.WARNING,
				"Column variable '<Contact ID>' at (l:1 c:23) must be explicitly bound to a column exported by query alias 'sub'; this binding has not been validated.",
				"sub.<Contact ID>", 1, 23);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=shifted, datetime_function={function_name=DATEADD, level={canonical=DAY, text=day, form=KEYWORD}, increment={literal=1}, value={column={substitution={name=<Contact ID>, type=column}, table_ref=sub}}}}}, from={table={alias=sub, query={select={1={column={name=id, table_ref=null}}}, from={table={alias=null, table=t}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[shifted]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<Contact ID>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={id=[[@16,65:66='id',<419>,1:65]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={id=[[@16,65:66='id',<419>,1:65]]}, query1={shifted=[[@12,44:50='shifted',<419>,1:44]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={shifted=[[@12,44:50='shifted',<419>,1:44]]}, def_query0={query_dictionary={id=[[@16,65:66='id',<419>,1:65]]}, table_dictionary={t={id=[[@16,65:66='id',<419>,1:65]]}}, interface={id=[{name=id, table_ref=t}]}}, interface={shifted=[{substitution={name=<Contact ID>, type=column}, table_ref=sub}]}, table_alias={sub=query0}}}", extractor.getSymbolTable().toString());
		Assert.assertEquals("Array Output Collectors are wrong", "{SQL={queryInterface=[shifted]}}", extractor.getArrayOutputCollectorsMap().toString());
		Assert.assertEquals(2, extractor.getSnippet().getParserDiagnosticList().size());
	}

	@Test
	public void qualifiedColumnVariableCteNestedSubqueryGoldenTest() {
		final String query = "WITH base AS (SELECT id FROM t) SELECT q.id FROM (SELECT b.id FROM base b WHERE b.<Contact ID> = 1) q";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertDiagnosticAtPosition(extractor.getSnippet(), BINDING_WARNING, ParseDiagnostic.Severity.WARNING,
				"Column variable '<Contact ID>' at (l:1 c:80) must be explicitly bound to a column exported by query alias 'b'; this binding has not been validated.",
				"b.<Contact ID>", 1, 80);
		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=id, table_ref=null}}}, from={table={alias=null, table=t}}}, alias=base}}, query={select={1={column={name=id, table_ref=q}}}, from={table={alias=q, query={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=base}}, where={condition={left={column={substitution={name=<Contact ID>, type=column}, table_ref=b}}, right={literal=1}, operator==}}}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[id]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<Contact ID>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={id=[[@5,21:22='id',<419>,1:21]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={id=[[@5,21:22='id',<419>,1:21], [@16,57:57='b',<419>,1:57]]}, query1={id=[[@18,59:60='id',<419>,1:59], [@10,39:39='q',<419>,1:39]]}, query2={id=[[@12,41:42='id',<419>,1:41]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={context_list={base=query0}, query_dictionary={id=[[@12,41:42='id',<419>,1:41]]}, def_query1={context_list={base=query0, b=query0}, query_dictionary={id=[[@18,59:60='id',<419>,1:59], [@10,39:39='q',<419>,1:39]]}, filters=[{substitution={name=<Contact ID>, type=column}, table_ref=b}], interface={id=[{name=id, table_ref=b}]}, table_alias={b=query0, base=query0}}, def_query0={query_dictionary={id=[[@5,21:22='id',<419>,1:21], [@16,57:57='b',<419>,1:57]]}, table_dictionary={t={id=[[@5,21:22='id',<419>,1:21]]}}, interface={id=[{name=id, table_ref=t}]}}, interface={id=[{name=id, table_ref=q}]}, table_alias={q=query1, base=query0}}}", extractor.getSymbolTable().toString());
		Assert.assertEquals("Array Output Collectors are wrong", "{SQL={queryInterface=[id]}}", extractor.getArrayOutputCollectorsMap().toString());
		Assert.assertEquals(1, extractor.getSnippet().getParserDiagnosticList().size());
	}

	@Test
	public void qualifiedColumnVariableInsertFromSubqueryGoldenTest() {
		final String query = "INSERT INTO target (id) SELECT src.<Contact ID> FROM (SELECT id FROM t) src";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertDiagnosticAtPosition(extractor.getSnippet(), BINDING_WARNING, ParseDiagnostic.Severity.WARNING,
				"Column variable '<Contact ID>' at (l:1 c:31) must be explicitly bound to a column exported by query alias 'src'; this binding has not been validated.",
				"src.<Contact ID>", 1, 31);
		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={from={table={alias=src, query={select={1={column={name=id, table_ref=null}}}, from={table={alias=null, table=t}}}}}, select={1={column={substitution={name=<Contact ID>, type=column}, table_ref=src}}}}, target_table={table={alias=null, table=target}}, columns={1={column={name=id, table_ref=null}}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[id]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<Contact ID>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={id=[[@13,61:62='id',<419>,1:61]]}, target={id=[[@4,20:21='id',<419>,1:20]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={id=[[@13,61:62='id',<419>,1:61]]}, query1={<Contact ID>=[[@9,35:46='<Contact ID>',<327>,1:35]]}, insert2={id=[[@4,20:21='id',<419>,1:20]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert2={query_dictionary={id=[[@4,20:21='id',<419>,1:20]]}, table_dictionary={target={id=[[@4,20:21='id',<419>,1:20]]}}, def_query1={query_dictionary={<Contact ID>=[[@9,35:46='<Contact ID>',<327>,1:35]]}, def_query0={query_dictionary={id=[[@13,61:62='id',<419>,1:61]]}, table_dictionary={t={id=[[@13,61:62='id',<419>,1:61]]}}, interface={id=[{name=id, table_ref=t}]}}, interface={<Contact ID>=[{substitution={name=<Contact ID>, type=column}, table_ref=src}]}, table_alias={src=query0}}, interface={id=[{name=<Contact ID>, table_ref=query1}]}}}", extractor.getSymbolTable().toString());
		Assert.assertEquals("Array Output Collectors are wrong", "{SQL={queryInterface=[id]}}", extractor.getArrayOutputCollectorsMap().toString());
		Assert.assertEquals(1, extractor.getSnippet().getParserDiagnosticList().size());
	}

	@Test
	public void qualifiedColumnVariableUpdateFromSubqueryGoldenTest() {
		final String query = "UPDATE target SET id = src.<Contact ID> FROM (SELECT id FROM t) src WHERE target.id = src.id";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertDiagnosticAtPosition(extractor.getSnippet(), BINDING_WARNING, ParseDiagnostic.Severity.WARNING,
				"Column variable '<Contact ID>' at (l:1 c:23) must be explicitly bound to a column exported by query alias 'src'; this binding has not been validated.",
				"src.<Contact ID>", 1, 23);
		Assert.assertEquals("AST is wrong", "{SQL={update={from={table={alias=src, query={select={1={column={name=id, table_ref=null}}}, from={table={alias=null, table=t}}}}}, where={condition={left={column={name=id, table_ref=target}}, right={column={name=id, table_ref=src}}, operator==}}, assignments={1={set={column={name=id, table_ref=null}}, to={column={substitution={name=<Contact ID>, type=column}, table_ref=src}}}}, table={alias=null, table=target}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[id]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<Contact ID>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={id=[[@11,53:54='id',<419>,1:53]]}, target={id=[[@17,74:79='target',<419>,1:74], [@3,18:19='id',<419>,1:18]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={id=[[@11,53:54='id',<419>,1:53], [@21,86:88='src',<419>,1:86]]}, update1={id=[[@3,18:19='id',<419>,1:18]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_update1={assignments={id=[{substitution={name=<Contact ID>, type=column}, table_ref=src}]}, table_dictionary={target={id=[[@3,18:19='id',<419>,1:18], [@17,74:79='target',<419>,1:74]]}}, update_dictionary={id=[[@3,18:19='id',<419>,1:18]]}, def_query0={query_dictionary={id=[[@11,53:54='id',<419>,1:53], [@21,86:88='src',<419>,1:86]]}, table_dictionary={t={id=[[@11,53:54='id',<419>,1:53]]}}, interface={id=[{name=id, table_ref=t}]}}, filters=[{name=id, table_ref=target}, {name=id, table_ref=src}], table_alias={src=query0}}}", extractor.getSymbolTable().toString());
		Assert.assertEquals("Array Output Collectors are wrong", "{SQL={queryInterface=[id]}}", extractor.getArrayOutputCollectorsMap().toString());
		Assert.assertEquals(1, extractor.getSnippet().getParserDiagnosticList().size());
	}

	@Test
	public void qualifiedColumnVariableDeleteUsingSubqueryGoldenTest() {
		final String query = "DELETE FROM target USING (SELECT id FROM t) src WHERE target.id = src.<Contact ID>";
		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertDiagnosticAtPosition(extractor.getSnippet(), BINDING_WARNING, ParseDiagnostic.Severity.WARNING,
				"Column variable '<Contact ID>' at (l:1 c:66) must be explicitly bound to a column exported by query alias 'src'; this binding has not been validated.",
				"src.<Contact ID>", 1, 66);
		Assert.assertEquals("AST is wrong", "{SQL={delete={table={alias=null, table=target}, using={1={table={alias=src, query={select={1={column={name=id, table_ref=null}}}, from={table={alias=null, table=t}}}}}}, where={condition={left={column={name=id, table_ref=target}}, right={column={substitution={name=<Contact ID>, type=column}, table_ref=src}}, operator==}}}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<Contact ID>=column}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{t={id=[[@6,33:34='id',<419>,1:33]]}, target={id=[[@12,54:59='target',<419>,1:54]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={id=[[@6,33:34='id',<419>,1:33]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_delete1={query_dictionary={}, table_dictionary={target={id=[[@12,54:59='target',<419>,1:54]]}}, def_query0={query_dictionary={id=[[@6,33:34='id',<419>,1:33]]}, table_dictionary={t={id=[[@6,33:34='id',<419>,1:33]]}}, interface={id=[{name=id, table_ref=t}]}}, filters=[{name=id, table_ref=target}, {substitution={name=<Contact ID>, type=column}, table_ref=src}], interface=null, table_alias={src=query0}}}", extractor.getSymbolTable().toString());
		Assert.assertEquals("Array Output Collectors are wrong", "{SQL={queryInterface=[]}}", extractor.getArrayOutputCollectorsMap().toString());
		Assert.assertEquals(2, extractor.getSnippet().getParserDiagnosticList().size());
	}
}
