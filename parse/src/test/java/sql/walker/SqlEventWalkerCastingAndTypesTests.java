package sql.walker;
import org.junit.Assert;
import org.junit.Test;

import sql.SQLSelectParserParser;

public class SqlEventWalkerCastingAndTypesTests extends AbstractSqlParseEventWalkerTest {

	@Test
	public void basicSelectListCasting1Test() {
		final String query = " SELECT 1 + 2 as a,(1+2)::varchar b, (d)::integer as c FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=a, calc={left={literal=1}, right={literal=2}, operator=+}}, 2={function={function_name=cast, data_type={type=VARCHAR}, type=CAST, value={parentheses={calc={left={literal=1}, right={literal=2}, operator=+}}}}, alias=b}, 3={function={function_name=cast, data_type={type=INTEGER}, type=CAST, value={parentheses={column={name=d, table_ref=null}}}}, alias=c}}, from={table={alias=null, table=tab1}}}}",
			extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b, c]",
			extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
			extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={d=[[@17,38:38='d',<392>,1:38]]}}",
			extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@5,17:17='a',<392>,1:17]], b=[[@14,34:34='b',<392>,1:34]], c=[[@22,53:53='c',<392>,1:53]]}}",
			extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={a=[[@5,17:17='a',<392>,1:17]], b=[[@14,34:34='b',<392>,1:34]], c=[[@22,53:53='c',<392>,1:53]]}, table_dictionary={tab1={d=[[@17,38:38='d',<392>,1:38]]}}, interface={a=[], b=[], c=[{name=d, table_ref=tab1}]}}}",
			extractor.getSymbolTable().toString());
	}


	@Test
	public void inlineCastOnColumnPredicandTest() {
		final String query = "SELECT columnName::varchar FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=VARCHAR}, type=CAST, value={column={name=columnName, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@3,19:25='varchar',<249>,1:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@3,19:25='varchar',<249>,1:19]]}, table_dictionary={tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}, interface={unnamed_0=[{name=columnName, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inlineCastToIntegerTest() {
		final String query = "SELECT columnName::integer FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=INTEGER}, type=CAST, value={column={name=columnName, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@3,19:25='integer',<235>,1:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@3,19:25='integer',<235>,1:19]]}, table_dictionary={tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}, interface={unnamed_0=[{name=columnName, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inlineCastToBigintTest() {
		final String query = "SELECT columnName::bigint FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=BIGINT}, type=CAST, value={column={name=columnName, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@3,19:24='bigint',<236>,1:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@3,19:24='bigint',<236>,1:19]]}, table_dictionary={tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}, interface={unnamed_0=[{name=columnName, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inlineCastToBooleanTest() {
		final String query = "SELECT columnName::boolean FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=BOOLEAN}, type=CAST, value={column={name=columnName, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@3,19:25='boolean',<210>,1:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@3,19:25='boolean',<210>,1:19]]}, table_dictionary={tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}, interface={unnamed_0=[{name=columnName, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inlineCastToDateTest() {
		final String query = "SELECT columnName::date FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=DATE}, type=CAST, value={column={name=columnName, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@3,19:22='date',<254>,1:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@3,19:22='date',<254>,1:19]]}, table_dictionary={tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}, interface={unnamed_0=[{name=columnName, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inlineCastToTimestampTest() {
		final String query = "SELECT columnName::timestamp FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=TIMESTAMP}, type=CAST, value={column={name=columnName, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@3,19:27='timestamp',<258>,1:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@3,19:27='timestamp',<258>,1:19]]}, table_dictionary={tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}, interface={unnamed_0=[{name=columnName, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inlineCastToTextTest() {
		final String query = "SELECT columnName::text FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=TEXT}, type=CAST, value={column={name=columnName, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@3,19:22='text',<263>,1:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@3,19:22='text',<263>,1:19]]}, table_dictionary={tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}, interface={unnamed_0=[{name=columnName, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inlineCastToCharNoLengthTest() {
		final String query = "SELECT columnName::char FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=CHAR}, type=CAST, value={column={name=columnName, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@3,19:22='char',<248>,1:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@3,19:22='char',<248>,1:19]]}, table_dictionary={tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}, interface={unnamed_0=[{name=columnName, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inlineCastToVarcharWithLengthTest() {
		final String query = "SELECT columnName::varchar(50) FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={length=50, type=VARCHAR}, type=CAST, value={column={name=columnName, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@6,29:29=')',<288>,1:29]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@6,29:29=')',<288>,1:29]]}, table_dictionary={tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}, interface={unnamed_0=[{name=columnName, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inlineCastToCharWithLengthTest() {
		final String query = "SELECT columnName::char(10) FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={length=10, type=CHAR}, type=CAST, value={column={name=columnName, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@6,26:26=')',<288>,1:26]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@6,26:26=')',<288>,1:26]]}, table_dictionary={tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}, interface={unnamed_0=[{name=columnName, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inlineCastToNumericNoParamsTest() {
		final String query = "SELECT columnName::numeric FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=NUMERIC}, type=CAST, value={column={name=columnName, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@3,19:25='numeric',<246>,1:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@3,19:25='numeric',<246>,1:19]]}, table_dictionary={tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}, interface={unnamed_0=[{name=columnName, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inlineCastToFloatNoParamsTest() {
		final String query = "SELECT columnName::float FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=FLOAT}, type=CAST, value={column={name=columnName, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@3,19:23='float',<244>,1:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@3,19:23='float',<244>,1:19]]}, table_dictionary={tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}, interface={unnamed_0=[{name=columnName, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inlineCastToNumericWithPrecisionTest() {
		final String query = "SELECT columnName::numeric(10) FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={precision=10, type=NUMERIC}, type=CAST, value={column={name=columnName, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@6,29:29=')',<288>,1:29]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@6,29:29=')',<288>,1:29]]}, table_dictionary={tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}, interface={unnamed_0=[{name=columnName, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inlineCastToNumericWithPrecisionAndScaleTest() {
		final String query = "SELECT columnName::numeric(10,2) FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={precision=10, scale=2, type=NUMERIC}, type=CAST, value={column={name=columnName, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@8,31:31=')',<288>,1:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@8,31:31=')',<288>,1:31]]}, table_dictionary={tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}, interface={unnamed_0=[{name=columnName, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inlineCastToDecimalWithPrecisionAndScaleTest() {
		final String query = "SELECT columnName::decimal(15,4) FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={precision=15, scale=4, type=DECIMAL}, type=CAST, value={column={name=columnName, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@8,31:31=')',<288>,1:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@8,31:31=')',<288>,1:31]]}, table_dictionary={tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}, interface={unnamed_0=[{name=columnName, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inlineCastToVariantSnowflakeTest() {
		final String query = "SELECT columnName::variant FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=VARIANT}, type=CAST, value={column={name=columnName, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@3,19:25='variant',<270>,1:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@3,19:25='variant',<270>,1:19]]}, table_dictionary={tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}, interface={unnamed_0=[{name=columnName, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inlineCastToTimestampNtzSnowflakeTest() {
		final String query = "SELECT columnName::timestamp_ntz FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=TIMESTAMP_NTZ}, type=CAST, value={column={name=columnName, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@3,19:31='timestamp_ntz',<260>,1:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@3,19:31='timestamp_ntz',<260>,1:19]]}, table_dictionary={tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}, interface={unnamed_0=[{name=columnName, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inlineCastToTimestampTzSnowflakeTest() {
		final String query = "SELECT columnName::timestamp_tz FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=TIMESTAMP_TZ}, type=CAST, value={column={name=columnName, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@3,19:30='timestamp_tz',<261>,1:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@3,19:30='timestamp_tz',<261>,1:19]]}, table_dictionary={tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}, interface={unnamed_0=[{name=columnName, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inlineCastToTimestampLtzSnowflakeTest() {
		final String query = "SELECT columnName::timestamp_ltz FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=TIMESTAMP_LTZ}, type=CAST, value={column={name=columnName, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@3,19:31='timestamp_ltz',<259>,1:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@3,19:31='timestamp_ltz',<259>,1:19]]}, table_dictionary={tab1={columnName=[[@1,7:16='columnName',<392>,1:7]]}}, interface={unnamed_0=[{name=columnName, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void inlineCastOnFormulaInWherePredicateTest() {
		final String query = "SELECT * FROM tab1 WHERE (a + b)::varchar = 'x'";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=tab1}}, where={condition={left={function={function_name=cast, data_type={type=VARCHAR}, type=CAST, value={parentheses={calc={left={column={name=a, table_ref=null}}, right={column={name=b, table_ref=null}}, operator=+}}}}}, right={literal='x'}, operator==}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[[@6,26:26='a',<392>,1:26]], b=[[@8,30:30='b',<392>,1:30]], *=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={a=[[@6,26:26='a',<392>,1:26]], b=[[@8,30:30='b',<392>,1:30]], *=[[@1,7:7='*',<291>,1:7]]}}, filters=[{name=a, table_ref=tab1}, {name=b, table_ref=tab1}], interface={*=[{name=*, table_ref=*}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicSelectListCasting2Test() {
		final String query = " SELECT cast(col1 as boolean) a,cast(col2 as varchar(2)) b, cast(col3 as numeric(9,3)) as c FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=BOOLEAN}, type=CAST, value={column={name=col1, table_ref=null}}}, alias=a}, 2={function={function_name=cast, data_type={length=2, type=VARCHAR}, type=CAST, value={column={name=col2, table_ref=null}}}, alias=b}, 3={function={function_name=cast, data_type={precision=9, scale=3, type=NUMERIC}, type=CAST, value={column={name=col3, table_ref=null}}}, alias=c}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b, c]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col2=[[@11,37:40='col2',<392>,1:37]], col3=[[@22,65:68='col3',<392>,1:65]], col1=[[@3,13:16='col1',<392>,1:13]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@7,30:30='a',<392>,1:30]], b=[[@18,57:57='b',<392>,1:57]], c=[[@32,90:90='c',<392>,1:90]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={a=[[@7,30:30='a',<392>,1:30]], b=[[@18,57:57='b',<392>,1:57]], c=[[@32,90:90='c',<392>,1:90]]}, table_dictionary={tab1={col2=[[@11,37:40='col2',<392>,1:37]], col3=[[@22,65:68='col3',<392>,1:65]], col1=[[@3,13:16='col1',<392>,1:13]]}}, interface={a=[{name=col1, table_ref=tab1}], b=[{name=col2, table_ref=tab1}], c=[{name=col3, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicSelectListTryCasting2Test() {
		// ITEM 66: Snowflake's TRY_CAST function
		final String query = " SELECT TRY_cast(col1 as boolean) a,cast(col2 as varchar(2)) b, cast(col3 as numeric(9,3)) as c FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=TRY_cast, data_type={type=BOOLEAN}, type=TRY_CAST, value={column={name=col1, table_ref=null}}}, alias=a}, 2={function={function_name=cast, data_type={length=2, type=VARCHAR}, type=CAST, value={column={name=col2, table_ref=null}}}, alias=b}, 3={function={function_name=cast, data_type={precision=9, scale=3, type=NUMERIC}, type=CAST, value={column={name=col3, table_ref=null}}}, alias=c}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b, c]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col2=[[@11,41:44='col2',<392>,1:41]], col3=[[@22,69:72='col3',<392>,1:69]], col1=[[@3,17:20='col1',<392>,1:17]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@7,34:34='a',<392>,1:34]], b=[[@18,61:61='b',<392>,1:61]], c=[[@32,94:94='c',<392>,1:94]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={a=[[@7,34:34='a',<392>,1:34]], b=[[@18,61:61='b',<392>,1:61]], c=[[@32,94:94='c',<392>,1:94]]}, table_dictionary={tab1={col2=[[@11,41:44='col2',<392>,1:41]], col3=[[@22,69:72='col3',<392>,1:69]], col1=[[@3,17:20='col1',<392>,1:17]]}}, interface={a=[{name=col1, table_ref=tab1}], b=[{name=col2, table_ref=tab1}], c=[{name=col3, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void castInDifferentContextsWhereConditionTest() {
		final String query = " SELECT colu FROM tab1 where cast(cola as boolean) is true"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=colu, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={function={function_name=cast, data_type={type=BOOLEAN}, type=CAST, value={column={name=cola, table_ref=null}}}}}, operator=is true}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[colu]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={colu=[[@1,8:11='colu',<392>,1:8]], cola=[[@7,34:37='cola',<392>,1:34]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={colu=[[@1,8:11='colu',<392>,1:8]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={colu=[[@1,8:11='colu',<392>,1:8]]}, table_dictionary={tab1={colu=[[@1,8:11='colu',<392>,1:8]], cola=[[@7,34:37='cola',<392>,1:34]]}}, filters=[{name=cola, table_ref=tab1}], interface={colu=[{name=colu, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void castInDifferentContextsCalculationTest() {
		final String query = " SELECT colu + cast(cola as numeric (9)) FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={calc={left={column={name=colu, table_ref=null}}, right={function={function_name=cast, data_type={precision=9, type=NUMERIC}, type=CAST, value={column={name=cola, table_ref=null}}}}, operator=+}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={colu=[[@1,8:11='colu',<392>,1:8]], cola=[[@5,20:23='cola',<392>,1:20]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@11,39:39=')',<288>,1:39]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@11,39:39=')',<288>,1:39]]}, table_dictionary={tab1={colu=[[@1,8:11='colu',<392>,1:8]], cola=[[@5,20:23='cola',<392>,1:20]]}}, interface={unnamed_0=[{name=colu, table_ref=tab1}, {name=cola, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void castInDifferentContextsJoinConditionTest() {
		final String query = " SELECT tab1.colu FROM tab1 join tab2 on tab1.cola = cast(tab2.cola as CHARACTER VARYING)"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=colu, table_ref=tab1}}}, from={join={1={table={alias=null, table=tab1}}, 2={join=join, on={condition={left={column={name=cola, table_ref=tab1}}, right={function={function_name=cast, data_type={type=CHARACTER VARYING}, type=CAST, value={column={name=cola, table_ref=tab2}}}}, operator==}}}, 3={table={alias=null, table=tab2}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[colu]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={colu=[[@1,8:11='tab1',<392>,1:8]], cola=[[@9,41:44='tab1',<392>,1:41]]}, tab2={cola=[[@15,58:61='tab2',<392>,1:58]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={colu=[[@3,13:16='colu',<392>,1:13]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={colu=[[@3,13:16='colu',<392>,1:13]]}, table_dictionary={tab1={colu=[[@1,8:11='tab1',<392>,1:8]], cola=[[@9,41:44='tab1',<392>,1:41]]}, tab2={cola=[[@15,58:61='tab2',<392>,1:58]]}}, filters=[{name=cola, table_ref=tab1}, {name=cola, table_ref=tab2}], interface={colu=[{name=colu, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void castInDifferentContextsGroupByTest() {
		final String query = " SELECT cast(cola as boolean) a, max(cast(cola as boolean)) b FROM tab1 group by cast(cola as boolean)"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=BOOLEAN}, type=CAST, value={column={name=cola, table_ref=null}}}, alias=a}, 2={function={function_name=max, qualifier=null, parameters={function={function_name=cast, data_type={type=BOOLEAN}, type=CAST, value={column={name=cola, table_ref=null}}}}}, alias=b}}, from={table={alias=null, table=tab1}}, groupby={1={function={function_name=cast, data_type={type=BOOLEAN}, type=CAST, value={column={name=cola, table_ref=null}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={cola=[[@3,13:16='cola',<392>,1:13], [@13,42:45='cola',<392>,1:42], [@25,86:89='cola',<392>,1:86]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@7,30:30='a',<392>,1:30]], b=[[@18,60:60='b',<392>,1:60]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={a=[[@7,30:30='a',<392>,1:30]], b=[[@18,60:60='b',<392>,1:60]]}, table_dictionary={tab1={cola=[[@3,13:16='cola',<392>,1:13], [@13,42:45='cola',<392>,1:42], [@25,86:89='cola',<392>,1:86]]}}, grouped_by=[{name=cola, table_ref=null}], interface={a=[{name=cola, table_ref=tab1}], b=[{name=cola, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void castInDifferentContextsOrderByTest() {
		final String query = " SELECT a, b FROM tab1 order by cast(cola as boolean)"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}, orderby={1={null_order=null, predicand={function={function_name=cast, data_type={type=BOOLEAN}, type=CAST, value={column={name=cola, table_ref=null}}}}, sort_order=ASC}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[[@1,8:8='a',<392>,1:8]], b=[[@3,11:11='b',<392>,1:11]], cola=[[@10,37:40='cola',<392>,1:37]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@1,8:8='a',<392>,1:8]], b=[[@3,11:11='b',<392>,1:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={a=[[@1,8:8='a',<392>,1:8]], b=[[@3,11:11='b',<392>,1:11]]}, table_dictionary={tab1={a=[[@1,8:8='a',<392>,1:8]], b=[[@3,11:11='b',<392>,1:11]], cola=[[@10,37:40='cola',<392>,1:37]]}}, ordered_by=[{name=cola, table_ref=null}], interface={a=[{name=a, table_ref=tab1}], b=[{name=b, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicCastingVariableTypesWithLengthsTest() {
		final String query = " SELECT cast('a' as character varying (10)) a,"
				+ " cast('a' as national character) b,"
				+ " cast('a' as national character (256)) c,"
				+ " cast('a' as national character varying) as d,"
				+ " cast('a' as national character varying (1000)) as e,"
				+ " cast(valid_from_dt as TIMESTAMP_LTZ(9)) as f"
				+ " FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={length=10, type=CHARACTER VARYING}, type=CAST, value={literal='a'}}, alias=a}, 2={function={function_name=cast, data_type={type=NATIONAL CHARACTER}, type=CAST, value={literal='a'}}, alias=b}, 3={function={function_name=cast, data_type={length=256, type=NATIONAL CHARACTER}, type=CAST, value={literal='a'}}, alias=c}, 4={function={function_name=cast, data_type={type=NATIONAL CHARACTER VARYING}, type=CAST, value={literal='a'}}, alias=d}, 5={function={function_name=cast, data_type={length=1000, type=NATIONAL CHARACTER VARYING}, type=CAST, value={literal='a'}}, alias=e}, 6={function={function_name=cast, data_type={precision=9, type=TIMESTAMP_LTZ}, type=CAST, value={column={name=valid_from_dt, table_ref=null}}}, alias=f}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b, c, d, e, f]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={valid_from_dt=[[@61,227:239='valid_from_dt',<392>,1:227]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@11,44:44='a',<392>,1:44]], b=[[@20,79:79='b',<392>,1:79]], c=[[@32,120:120='c',<392>,1:120]], d=[[@43,166:166='d',<392>,1:166]], e=[[@57,219:219='e',<392>,1:219]], f=[[@69,265:265='f',<392>,1:265]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={a=[[@11,44:44='a',<392>,1:44]], b=[[@20,79:79='b',<392>,1:79]], c=[[@32,120:120='c',<392>,1:120]], d=[[@43,166:166='d',<392>,1:166]], e=[[@57,219:219='e',<392>,1:219]], f=[[@69,265:265='f',<392>,1:265]]}, table_dictionary={tab1={valid_from_dt=[[@61,227:239='valid_from_dt',<392>,1:227]]}}, interface={a=[], b=[], c=[], d=[], e=[], f=[{name=valid_from_dt, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicCastingPrecisionTypesTest() {
		final String query = " SELECT cast('a' as numeric) a,"
				+ " cast('a' as double precision) b,"
				+ " cast('a' as decimal (9, 7)) c,"
				+ " cast('a' as double   precision (98,7)) as d,"
				+ " cast('a' as FloaT(2)) as e"
				+ " FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=NUMERIC}, type=CAST, value={literal='a'}}, alias=a}, 2={function={function_name=cast, data_type={type=DOUBLE PRECISION}, type=CAST, value={literal='a'}}, alias=b}, 3={function={function_name=cast, data_type={precision=9, scale=7, type=DECIMAL}, type=CAST, value={literal='a'}}, alias=c}, 4={function={function_name=cast, data_type={precision=98, scale=7, type=DOUBLE PRECISION}, type=CAST, value={literal='a'}}, alias=d}, 5={function={function_name=cast, data_type={precision=2, type=FLOAT}, type=CAST, value={literal='a'}}, alias=e}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b, c, d, e]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@7,29:29='a',<392>,1:29]], b=[[@16,62:62='b',<392>,1:62]], c=[[@29,93:93='c',<392>,1:93]], d=[[@44,138:138='d',<392>,1:138]], e=[[@56,166:166='e',<392>,1:166]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={a=[[@7,29:29='a',<392>,1:29]], b=[[@16,62:62='b',<392>,1:62]], c=[[@29,93:93='c',<392>,1:93]], d=[[@44,138:138='d',<392>,1:138]], e=[[@56,166:166='e',<392>,1:166]]}, table_dictionary={tab1={}}, interface={a=[], b=[], c=[], d=[], e=[]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicCastingCompundStaticTypesTest() {
		final String query = " SELECT cast('a' as text) a,"
				+ " cast('a' as float4) b,"
				+ " cast('a' as time with  time zone) c,"
				+ " cast('a' as timestamp  with time  zone) as d,"
				+ " cast('a' as inet4) as e"
				+ " FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=TEXT}, type=CAST, value={literal='a'}}, alias=a}, 2={function={function_name=cast, data_type={type=FLOAT4}, type=CAST, value={literal='a'}}, alias=b}, 3={function={function_name=cast, data_type={type=TIME WITH TIME ZONE}, type=CAST, value={literal='a'}}, alias=c}, 4={function={function_name=cast, data_type={type=TIMESTAMP WITH TIME ZONE}, type=CAST, value={literal='a'}}, alias=d}, 5={function={function_name=cast, data_type={type=INET4}, type=CAST, value={literal='a'}}, alias=e}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b, c, d, e]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@7,26:26='a',<392>,1:26]], b=[[@15,49:49='b',<392>,1:49]], c=[[@26,86:86='c',<392>,1:86]], d=[[@38,132:132='d',<392>,1:132]], e=[[@47,157:157='e',<392>,1:157]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={a=[[@7,26:26='a',<392>,1:26]], b=[[@15,49:49='b',<392>,1:49]], c=[[@26,86:86='c',<392>,1:86]], d=[[@38,132:132='d',<392>,1:132]], e=[[@47,157:157='e',<392>,1:157]]}, table_dictionary={tab1={}}, interface={a=[], b=[], c=[], d=[], e=[]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void nullCastingTest() {
		final String query = " SELECT cast(null as string) a"
				+ " FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=STRING}, type=CAST, value={null_literal=null}}, alias=a}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@7,29:29='a',<392>,1:29]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={a=[[@7,29:29='a',<392>,1:29]]}, table_dictionary={tab1={}}, interface={a=[]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void castingWithPredicandVariableTest() {
		// ITEM 104: Cast statements with embedded variables 
		final String query = " SELECT cast(<var1> as string) a"
				+ " FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=STRING}, type=CAST, value={substitution={name=<var1>, type=predicand}}}, alias=a}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<var1>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@7,31:31='a',<392>,1:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={a=[[@7,31:31='a',<392>,1:31]]}, table_dictionary={tab1={}}, interface={a=[{name=<var1>, type=predicand}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void castingWithColumnVariableTest() {
		// ITEM 104: Cast statements with embedded variables 
		final String query = " SELECT cast(tab1.<var1> as string) a"
				+ " FROM tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=STRING}, type=CAST, value={column={substitution={name=<var1>, type=column}, table_ref=tab1}}}, alias=a}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<var1>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={<var1>=[[@3,13:16='tab1',<392>,1:13]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a=[[@9,36:36='a',<392>,1:36]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={a=[[@9,36:36='a',<392>,1:36]]}, table_dictionary={tab1={<var1>=[[@3,13:16='tab1',<392>,1:13]]}}, interface={a=[{substitution={name=<var1>, type=column}, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void inlineCastOfNullToIntegerTest() {
		final String query = "SELECT NULL::integer FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=INTEGER}, type=CAST, value={null_literal=null}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@3,13:19='integer',<235>,1:13]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@3,13:19='integer',<235>,1:13]]}, table_dictionary={tab1={}}, interface={unnamed_0=[]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void inlineCastOfNullToVarcharTest() {
		final String query = "SELECT NULL::varchar FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=VARCHAR}, type=CAST, value={null_literal=null}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@3,13:19='varchar',<249>,1:13]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@3,13:19='varchar',<249>,1:13]]}, table_dictionary={tab1={}}, interface={unnamed_0=[]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void chainedInlineCastTest() {
		final String query = "SELECT col::text::varchar FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=VARCHAR}, type=CAST, value={function={function_name=cast, data_type={type=TEXT}, type=CAST, value={column={name=col, table_ref=null}}}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col=[[@1,7:9='col',<392>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@5,18:24='varchar',<249>,1:18]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@5,18:24='varchar',<249>,1:18]]}, table_dictionary={tab1={col=[[@1,7:9='col',<392>,1:7]]}}, interface={unnamed_0=[{name=col, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void trimSpecialSyntaxWithInlineCastTest() {
		final String query = "SELECT TRIM(LEADING ' ' FROM col)::text FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=TEXT}, type=CAST, value={function={function_name=TRIM, parameters={qualifier=LEADING, trim_character={literal=' '}, value={column={name=col, table_ref=null}}}}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col=[[@6,29:31='col',<392>,1:29]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@9,35:38='text',<263>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@9,35:38='text',<263>,1:35]]}, table_dictionary={tab1={col=[[@6,29:31='col',<392>,1:29]]}}, interface={unnamed_0=[{name=col, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void tripleChainedInlineCastTest() {
		final String query = "SELECT col ::text::  varchar :: integer FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=INTEGER}, type=CAST, value={function={function_name=cast, data_type={type=VARCHAR}, type=CAST, value={function={function_name=cast, data_type={type=TEXT}, type=CAST, value={column={name=col, table_ref=null}}}}}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col=[[@1,7:9='col',<392>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@7,32:38='integer',<235>,1:32]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@7,32:38='integer',<235>,1:32]]}, table_dictionary={tab1={col=[[@1,7:9='col',<392>,1:7]]}}, interface={unnamed_0=[{name=col, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void quadrupleChainedInlineCastTest() {
		final String query = "SELECT col::text::varchar::integer::bigint FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=BIGINT}, type=CAST, value={function={function_name=cast, data_type={type=INTEGER}, type=CAST, value={function={function_name=cast, data_type={type=VARCHAR}, type=CAST, value={function={function_name=cast, data_type={type=TEXT}, type=CAST, value={column={name=col, table_ref=null}}}}}}}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col=[[@1,7:9='col',<392>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@9,36:41='bigint',<236>,1:36]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@9,36:41='bigint',<236>,1:36]]}, table_dictionary={tab1={col=[[@1,7:9='col',<392>,1:7]]}}, interface={unnamed_0=[{name=col, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void quintupleChainedInlineCastTest() {
		final String query = "SELECT col::text::varchar::integer::bigint::float FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=cast, data_type={type=FLOAT}, type=CAST, value={function={function_name=cast, data_type={type=BIGINT}, type=CAST, value={function={function_name=cast, data_type={type=INTEGER}, type=CAST, value={function={function_name=cast, data_type={type=VARCHAR}, type=CAST, value={function={function_name=cast, data_type={type=TEXT}, type=CAST, value={column={name=col, table_ref=null}}}}}}}}}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col=[[@1,7:9='col',<392>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@11,44:48='float',<244>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@11,44:48='float',<244>,1:44]]}, table_dictionary={tab1={col=[[@1,7:9='col',<392>,1:7]]}}, interface={unnamed_0=[{name=col, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}

}
