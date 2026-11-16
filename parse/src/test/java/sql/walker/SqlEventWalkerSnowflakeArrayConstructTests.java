package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

public class SqlEventWalkerSnowflakeArrayConstructTests extends AbstractSqlParseEventWalkerTest {

	@Test
	public void snowflakeArrayConstructSelectFromCsvShapeV0Test() {
		final String query =
				"SELECT md5(to_varchar(array_construct('Custom Contacts', e.email, e.first_name, <Universal Salt>))) AS sourcecontact_id "
						+ "FROM enrollment_services__ccp e";

		final SQLSelectParserParser parser = parse(query);
		final SqlParseEventWalker extractor = runParsertest(query, parser);
		final Snippet snippet = extractor.getSnippet();

		Assert.assertEquals(snippet.getParserDiagnosticList().toString(), 1, snippet.getParserDiagnosticList().size());
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_construct' at (l:1 c:22) is recognized as Snowflake-specific grammar.",
				null,
				1,
				22);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={parameters={1={function={parameters={1={function={parameters={1={literal='Custom Contacts'}, 2={column={name=email, table_ref=e}}, 3={column={name=first_name, table_ref=e}}, 4={substitution={name=<Universal Salt>, type=predicand}}}, function_name=array_construct}}}, function_name=to_varchar}}}, function_name=md5}, alias=sourcecontact_id}}, from={table={alias=e, table=enrollment_services__ccp}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[sourcecontact_id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<Universal Salt>=predicand}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{enrollment_services__ccp={first_name=[[@13,66:66='e',<419>,1:66]], email=[[@9,57:57='e',<419>,1:57]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={sourcecontact_id=[[@22,103:118='sourcecontact_id',<419>,1:103]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={sourcecontact_id=[[@22,103:118='sourcecontact_id',<419>,1:103]]}, table_dictionary={enrollment_services__ccp={first_name=[[@13,66:66='e',<419>,1:66]], email=[[@9,57:57='e',<419>,1:57]]}}, interface={sourcecontact_id=[{name=email, table_ref=e}, {name=first_name, table_ref=e}, {name=<Universal Salt>, type=predicand}]}, table_alias={e=enrollment_services__ccp}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void snowflakeArrayConstructJoinConditionFromCsvShapeV0Test() {
		final String query =
				"SELECT p.sourcecontact_id "
						+ "FROM enrollment_services__ccp e "
						+ "JOIN pdp__custom_contacts p "
						+ "ON md5(to_varchar(array_construct('Custom Contacts', e.email, e.first_name, <Universal Salt>))) = p.sourcecontact_id";

		final SQLSelectParserParser parser = parse(query);
		final SqlParseEventWalker extractor = runParsertest(query, parser);
		final Snippet snippet = extractor.getSnippet();

		Assert.assertEquals(snippet.getParserDiagnosticList().toString(), 1, snippet.getParserDiagnosticList().size());
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_construct' at (l:1 c:104) is recognized as Snowflake-specific grammar.",
				null,
				1,
				104);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=sourcecontact_id, table_ref=p}}}, from={join={1={table={alias=e, table=enrollment_services__ccp}}, 2={join=JOIN, on={condition={left={function={parameters={1={function={parameters={1={function={parameters={1={literal='Custom Contacts'}, 2={column={name=email, table_ref=e}}, 3={column={name=first_name, table_ref=e}}, 4={substitution={name=<Universal Salt>, type=predicand}}}, function_name=array_construct}}}, function_name=to_varchar}}}, function_name=md5}}, right={column={name=sourcecontact_id, table_ref=p}}, operator==}}}, 3={table={alias=p, table=pdp__custom_contacts}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[sourcecontact_id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<Universal Salt>=predicand}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{enrollment_services__ccp={first_name=[[@23,148:148='e',<419>,1:148]], email=[[@19,139:139='e',<419>,1:139]]}, pdp__custom_contacts={sourcecontact_id=[[@1,7:7='p',<419>,1:7], [@32,184:184='p',<419>,1:184]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={sourcecontact_id=[[@3,9:24='sourcecontact_id',<419>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={sourcecontact_id=[[@3,9:24='sourcecontact_id',<419>,1:9]]}, table_dictionary={enrollment_services__ccp={first_name=[[@23,148:148='e',<419>,1:148]], email=[[@19,139:139='e',<419>,1:139]]}, pdp__custom_contacts={sourcecontact_id=[[@1,7:7='p',<419>,1:7], [@32,184:184='p',<419>,1:184]]}}, filters=[{name=email, table_ref=e}, {name=first_name, table_ref=e}, {name=<Universal Salt>, type=predicand}, {name=sourcecontact_id, table_ref=p}], interface={sourcecontact_id=[{name=sourcecontact_id, table_ref=p}]}, table_alias={p=pdp__custom_contacts, e=enrollment_services__ccp}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void snowflakeArrayConstructMixedDialectFatalV0Test() {
		final String query = "SELECT array_construct(t.col1), extract(isodow from t.col1) FROM tab1 t";

		final SQLSelectParserParser parser = parse(query);
		final SqlParseEventWalker extractor = runParsertest(query, parser);
		final Snippet snippet = extractor.getSnippet();

		Assert.assertEquals("Expected one fatal (mixed dialect)", 1, snippet.getFatalErrorStringList().size());
		assertDiagnosticAtPosition(
				snippet,
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				ParseDiagnostic.Severity.WARNING,
				"routine 'array_construct' at (l:1 c:7) is recognized as Snowflake-specific grammar.",
				null,
				1,
				7);
		assertFatalDiagnosticAtPosition(
				snippet,
				"STATEMENT_MIXED_DIALECT_GRAMMAR",
				"unlikely to run on either engine",
				null,
				1,
				32);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={parameters={1={column={name=col1, table_ref=t}}}, function_name=array_construct}}, 2={datetime_function={function_name=extract, part_form=KEYWORD, part=isodow, source={column={name=col1, table_ref=t}}}}}, from={table={alias=t, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_1, unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col1=[[@3,23:23='t',<419>,1:23], [@12,52:52='t',<419>,1:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_1=[[@15,58:58=')',<288>,1:58]], unnamed_0=[[@6,29:29=')',<288>,1:29]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_1=[[@15,58:58=')',<288>,1:58]], unnamed_0=[[@6,29:29=')',<288>,1:29]]}, table_dictionary={tab1={col1=[[@3,23:23='t',<419>,1:23], [@12,52:52='t',<419>,1:52]]}}, interface={unnamed_1=[{name=col1, table_ref=t}], unnamed_0=[{name=col1, table_ref=t}]}, table_alias={t=tab1}}}",
				extractor.getSymbolTable().toString());
	}
}
