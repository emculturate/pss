package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import sql.SQLSelectParserParser;

/**
 * Golden coverage for PSS {@code Bracket_Identifier} / {@code logical_identifier} forms.
 */
public class SqlEventWalkerBracketedIdentifierTests extends AbstractSqlParseEventWalkerTest {

	@Test
	public void singleTermBracketedIdentifierTableColumnAndAliasV0Test() {
		// Single interior token only — no spaces, dots, or hyphens inside brackets.
		final String query =
				"SELECT t.score AS [Result], t.[Metric], t.*"
				+ " FROM [Entity] AS t"
				+ " WHERE t.score > 0";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=score, table_ref=t}, alias=[Result]}, 2={column={name=[Metric], table_ref=t}}, 3={column={name=*, table_ref=t}}}, from={table={alias=t, table=[Entity]}}, where={condition={left={column={name=score, table_ref=t}}, right={literal=0}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*, [Result], [Metric]]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{[Entity]={score=[[@1,7:7='t',<392>,1:7], [@25,69:69='t',<392>,1:69]], *=[[@15,40:40='t',<392>,1:40]], [Metric]=[[@9,28:28='t',<392>,1:28]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={*=[[@17,42:42='*',<291>,1:42]], [Result]=[[@7,25:25=']',<411>,1:25]], [Metric]=[[@13,37:37=']',<411>,1:37]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={*=[[@17,42:42='*',<291>,1:42]], [Result]=[[@7,25:25=']',<411>,1:25]], [Metric]=[[@13,37:37=']',<411>,1:37]]}, table_dictionary={[Entity]={score=[[@1,7:7='t',<392>,1:7], [@25,69:69='t',<392>,1:69]], *=[[@15,40:40='t',<392>,1:40]], [Metric]=[[@9,28:28='t',<392>,1:28]]}}, filters=[{name=score, table_ref=t}], interface={*=[{name=*, table_ref=t}], [Result]=[{name=score, table_ref=t}], [Metric]=[{name=[Metric], table_ref=t}]}, table_alias={t=[Entity]}}}",
				extractor.getSymbolTable().toString());
	}

}
