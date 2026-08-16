package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import sql.SQLSelectParserParser;

/**
 * Phase 2.3 — {@code WITHIN GROUP (ORDER BY …)} on ordered aggregates (LISTAGG and peers).
 */
public class SqlEventWalkerWithinGroupOrderedAggregateTests extends AbstractSqlParseEventWalkerTest {

	private static final String PRIOR_ALIAS = "prior_alias";

	private static String listaggWithinGroupOver(String tableAlias) {
		return "LISTAGG(" + tableAlias + ".program, '||')"
				+ " WITHIN GROUP (ORDER BY " + tableAlias + ".funnel_priority, " + tableAlias + ".sort_order ASC)"
				+ " OVER (PARTITION BY " + tableAlias + ".contact_key)";
	}

	private void assertWithinGroupOrderedByPriorAlias(SqlParseEventWalker extractor) {
		String symbolTable = extractor.getSymbolTable().toString();
		Assert.assertTrue(
				"expected within_group_ordered_by prior_alias@queryN: " + symbolTable,
				symbolTable.contains("within_group_ordered_by=[")
						&& symbolTable.matches("(?s).*within_group_ordered_by=\\[.*\\{name="
								+ PRIOR_ALIAS + ", table_ref=query\\d+\\}.*"));
	}

	private void assertWindowPartitionByPriorAlias(SqlParseEventWalker extractor) {
		String symbolTable = extractor.getSymbolTable().toString();
		Assert.assertTrue(
				"expected window_partition_by prior_alias@queryN: " + symbolTable,
				symbolTable.contains("window_partition_by=[")
						&& symbolTable.matches("(?s).*window_partition_by=\\[.*\\{name="
								+ PRIOR_ALIAS + ", table_ref=query\\d+\\}.*"));
	}

	@Test
	public void listaggWithinGroupOverPartitionBySelectListV0Test() {
		final String query =
				"SELECT LISTAGG(program, '||')"
				+ " WITHIN GROUP (ORDER BY funnel_priority, sort_order ASC)"
				+ " OVER (PARTITION BY contact_key) AS fun_program_agg"
				+ " FROM all_funnel_status_sort";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={ordered_aggregate={within_group={orderby={1={null_order=null, predicand={column={name=funnel_priority, table_ref=null}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=sort_order, table_ref=null}}, sort_order=ASC}}}, over={partition_by={1={column={name=contact_key, table_ref=null}}}}, function={function_name=LISTAGG, parameters={1={column={name=program, table_ref=null}}, 2={literal='||'}}}}, alias=fun_program_agg}}, from={table={alias=null, table=all_funnel_status_sort}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[fun_program_agg]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{all_funnel_status_sort={funnel_priority=[[@12,53:67='funnel_priority',<394>,1:53]], contact_key=[[@21,105:115='contact_key',<394>,1:105]], program=[[@3,15:21='program',<394>,1:15]], sort_order=[[@14,70:79='sort_order',<394>,1:70]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={fun_program_agg=[[@24,121:135='fun_program_agg',<394>,1:121]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={within_group_ordered_by=[{name=funnel_priority, table_ref=null}, {name=sort_order, table_ref=null}], query_dictionary={fun_program_agg=[[@24,121:135='fun_program_agg',<394>,1:121]]}, table_dictionary={all_funnel_status_sort={funnel_priority=[[@12,53:67='funnel_priority',<394>,1:53]], contact_key=[[@21,105:115='contact_key',<394>,1:105]], program=[[@3,15:21='program',<394>,1:15]], sort_order=[[@14,70:79='sort_order',<394>,1:70]]}}, window_partition_by=[{name=contact_key, table_ref=null}], interface={fun_program_agg=[{name=funnel_priority, table_ref=all_funnel_status_sort}, {name=sort_order, table_ref=all_funnel_status_sort}, {name=contact_key, table_ref=all_funnel_status_sort}, {name=program, table_ref=all_funnel_status_sort}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void listaggWithinGroupAllSelectClausesSymbolTableV1Test() {
		// Same ordered-aggregate shape in SELECT, WHERE, HAVING, QUALIFY, and ORDER BY.
		// WITHIN GROUP sort keys -> within_group_ordered_by; PARTITION BY -> window_partition_by;
		// predicate sites -> filters; GROUP BY keys -> grouped_by; trailing ORDER BY -> ordered_by.
		final String agg = listaggWithinGroupOver("t");
		final String query =
				"SELECT t.program,"
				+ " " + agg + " AS fun_agg"
				+ " FROM all_funnel_status_sort t"
				+ " WHERE t.program = 'a' AND " + agg + " = 'b'"
				+ " GROUP BY t.program, t.funnel_priority, t.sort_order"
				+ " HAVING " + agg + " IS NOT NULL"
				+ " QUALIFY " + agg + " = 'c'"
				+ " ORDER BY t.sort_order";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Assert.assertEquals("Interface is wrong", "[program, fun_agg]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{all_funnel_status_sort={funnel_priority=[[@18,66:66='t',<394>,1:66], [@60,252:252='t',<394>,1:252], [@85,349:349='t',<394>,1:349], [@106,436:436='t',<394>,1:436], [@140,575:575='t',<394>,1:575]], contact_key=[[@31,122:122='t',<394>,1:122], [@73,308:308='t',<394>,1:308], [@119,492:492='t',<394>,1:492], [@153,631:631='t',<394>,1:631]], program=[[@1,7:7='t',<394>,1:7], [@7,26:26='t',<394>,1:26], [@41,184:184='t',<394>,1:184], [@49,212:212='t',<394>,1:212], [@81,338:338='t',<394>,1:338], [@95,396:396='t',<394>,1:396], [@129,535:535='t',<394>,1:535]], sort_order=[[@22,85:85='t',<394>,1:85], [@64,271:271='t',<394>,1:271], [@89,368:368='t',<394>,1:368], [@110,455:455='t',<394>,1:455], [@144,594:594='t',<394>,1:594], [@161,661:661='t',<394>,1:661]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={fun_agg=[[@36,140:146='fun_agg',<394>,1:140]], program=[[@3,9:15='program',<394>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={within_group_ordered_by=[{name=funnel_priority, table_ref=t}, {name=sort_order, table_ref=t}], query_dictionary={program=[[@3,9:15='program',<394>,1:9]], fun_agg=[[@36,140:146='fun_agg',<394>,1:140]]}, table_dictionary={all_funnel_status_sort={funnel_priority=[[@18,66:66='t',<394>,1:66], [@60,252:252='t',<394>,1:252], [@85,349:349='t',<394>,1:349], [@106,436:436='t',<394>,1:436], [@140,575:575='t',<394>,1:575]], contact_key=[[@31,122:122='t',<394>,1:122], [@73,308:308='t',<394>,1:308], [@119,492:492='t',<394>,1:492], [@153,631:631='t',<394>,1:631]], program=[[@1,7:7='t',<394>,1:7], [@7,26:26='t',<394>,1:26], [@41,184:184='t',<394>,1:184], [@49,212:212='t',<394>,1:212], [@81,338:338='t',<394>,1:338], [@95,396:396='t',<394>,1:396], [@129,535:535='t',<394>,1:535]], sort_order=[[@22,85:85='t',<394>,1:85], [@64,271:271='t',<394>,1:271], [@89,368:368='t',<394>,1:368], [@110,455:455='t',<394>,1:455], [@144,594:594='t',<394>,1:594], [@161,661:661='t',<394>,1:661]]}}, grouped_by=[{name=program, table_ref=t}, {name=funnel_priority, table_ref=t}, {name=sort_order, table_ref=t}], window_partition_by=[{name=contact_key, table_ref=t}], ordered_by=[{name=sort_order, table_ref=t}], filters=[{name=program, table_ref=t}, {name=funnel_priority, table_ref=t}, {name=sort_order, table_ref=t}, {name=contact_key, table_ref=t}], interface={program=[{name=program, table_ref=t}], fun_agg=[{name=funnel_priority, table_ref=t}, {name=sort_order, table_ref=t}, {name=contact_key, table_ref=t}, {name=program, table_ref=t}]}, table_alias={t=all_funnel_status_sort}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void listaggWithinGroupJoinOnClauseSymbolTableV2Test() {
		final String aggT = listaggWithinGroupOver("t");
		final String aggU = listaggWithinGroupOver("u");
		final String query =
				"SELECT t.program"
				+ " FROM all_funnel_status_sort t"
				+ " JOIN all_funnel_status_sort u ON " + aggT + " = " + aggU;

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Assert.assertEquals("Interface is wrong", "[program]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{all_funnel_status_sort={funnel_priority=[[@55,249:249='u',<394>,1:249], [@24,128:128='t',<394>,1:128]], contact_key=[[@68,305:305='u',<394>,1:305], [@37,184:184='t',<394>,1:184]], program=[[@44,209:209='u',<394>,1:209], [@1,7:7='t',<394>,1:7], [@13,88:88='t',<394>,1:88]], sort_order=[[@28,147:147='t',<394>,1:147], [@59,268:268='u',<394>,1:268]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={program=[[@3,9:15='program',<394>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={within_group_ordered_by=[{name=funnel_priority, table_ref=t}, {name=sort_order, table_ref=t}, {name=funnel_priority, table_ref=u}, {name=sort_order, table_ref=u}], query_dictionary={program=[[@3,9:15='program',<394>,1:9]]}, table_dictionary={all_funnel_status_sort={funnel_priority=[[@55,249:249='u',<394>,1:249], [@24,128:128='t',<394>,1:128]], contact_key=[[@68,305:305='u',<394>,1:305], [@37,184:184='t',<394>,1:184]], program=[[@44,209:209='u',<394>,1:209], [@1,7:7='t',<394>,1:7], [@13,88:88='t',<394>,1:88]], sort_order=[[@28,147:147='t',<394>,1:147], [@59,268:268='u',<394>,1:268]]}}, window_partition_by=[{name=contact_key, table_ref=t}, {name=contact_key, table_ref=u}], filters=[{name=funnel_priority, table_ref=t}, {name=sort_order, table_ref=t}, {name=contact_key, table_ref=t}, {name=program, table_ref=t}, {name=funnel_priority, table_ref=u}, {name=sort_order, table_ref=u}, {name=contact_key, table_ref=u}, {name=program, table_ref=u}], interface={program=[{name=program, table_ref=t}]}, table_alias={t=all_funnel_status_sort, u=all_funnel_status_sort}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void listaggWithinGroupUpdateSetSymbolTableV3Test() {
		final String agg = listaggWithinGroupOver("src");
		final String query =
				"UPDATE employees e SET agg_col = " + agg + " FROM all_funnel_status_sort src"
				+ " WHERE e.emp_id = src.contact_key AND " + agg + " = 'x'";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Assert.assertEquals("Interface is wrong", "[agg_col]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{all_funnel_status_sort={funnel_priority=[[@19,83:85='src',<394>,1:83], [@61,279:281='src',<394>,1:279]], contact_key=[[@32,143:145='src',<394>,1:143], [@44,209:211='src',<394>,1:209], [@74,339:341='src',<394>,1:339]], program=[[@8,41:43='src',<394>,1:41], [@50,237:239='src',<394>,1:237]], sort_order=[[@23,104:106='src',<394>,1:104], [@65,300:302='src',<394>,1:300]]}, employees={agg_col=[[@4,23:29='agg_col',<394>,1:23]], emp_id=[[@40,198:198='e',<394>,1:198]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{update0={agg_col=[[@4,23:29='agg_col',<394>,1:23]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update0={within_group_ordered_by=[{name=funnel_priority, table_ref=src}, {name=sort_order, table_ref=src}], assignments={agg_col=[{name=funnel_priority, table_ref=src}, {name=sort_order, table_ref=src}, {name=contact_key, table_ref=src}, {name=program, table_ref=src}]}, table_dictionary={all_funnel_status_sort={funnel_priority=[[@19,83:85='src',<394>,1:83], [@61,279:281='src',<394>,1:279]], contact_key=[[@32,143:145='src',<394>,1:143], [@44,209:211='src',<394>,1:209], [@74,339:341='src',<394>,1:339]], program=[[@8,41:43='src',<394>,1:41], [@50,237:239='src',<394>,1:237]], sort_order=[[@23,104:106='src',<394>,1:104], [@65,300:302='src',<394>,1:300]]}, employees={agg_col=[[@4,23:29='agg_col',<394>,1:23]], emp_id=[[@40,198:198='e',<394>,1:198]]}}, window_partition_by=[{name=contact_key, table_ref=src}], update_dictionary={agg_col=[[@4,23:29='agg_col',<394>,1:23]]}, filters=[{name=emp_id, table_ref=e}, {name=contact_key, table_ref=src}, {name=funnel_priority, table_ref=src}, {name=sort_order, table_ref=src}, {name=program, table_ref=src}], table_alias={e=employees, src=all_funnel_status_sort}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void listaggWithinGroupInsertSelectSymbolTableV4Test() {
		final String agg = listaggWithinGroupOver("src");
		final String query =
				"INSERT INTO employees (col1, agg_col) SELECT src.program, " + agg
				+ " FROM all_funnel_status_sort src WHERE " + agg + " IS NOT NULL";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Assert.assertEquals("Interface is wrong", "[agg_col, col1]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{all_funnel_status_sort={funnel_priority=[[@26,108:110='src',<394>,1:108], [@60,273:275='src',<394>,1:273]], contact_key=[[@39,168:170='src',<394>,1:168], [@73,333:335='src',<394>,1:333]], program=[[@9,45:47='src',<394>,1:45], [@15,66:68='src',<394>,1:66], [@49,231:233='src',<394>,1:231]], sort_order=[[@30,129:131='src',<394>,1:129], [@64,294:296='src',<394>,1:294]]}, employees={agg_col=[[@6,29:35='agg_col',<394>,1:29]], col1=[[@4,23:26='col1',<394>,1:23]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={program=[[@11,49:55='program',<394>,1:49]], unnamed_0=[[@42,183:183=')',<288>,1:183]]}, insert1={agg_col=[[@6,29:35='agg_col',<394>,1:29]], col1=[[@4,23:26='col1',<394>,1:23]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert1={query_dictionary={agg_col=[[@6,29:35='agg_col',<394>,1:29]], col1=[[@4,23:26='col1',<394>,1:23]]}, table_dictionary={employees={agg_col=[[@6,29:35='agg_col',<394>,1:29]], col1=[[@4,23:26='col1',<394>,1:23]]}}, def_query0={within_group_ordered_by=[{name=funnel_priority, table_ref=src}, {name=sort_order, table_ref=src}], query_dictionary={program=[[@11,49:55='program',<394>,1:49]], unnamed_0=[[@42,183:183=')',<288>,1:183]]}, table_dictionary={all_funnel_status_sort={funnel_priority=[[@26,108:110='src',<394>,1:108], [@60,273:275='src',<394>,1:273]], contact_key=[[@39,168:170='src',<394>,1:168], [@73,333:335='src',<394>,1:333]], program=[[@9,45:47='src',<394>,1:45], [@15,66:68='src',<394>,1:66], [@49,231:233='src',<394>,1:231]], sort_order=[[@30,129:131='src',<394>,1:129], [@64,294:296='src',<394>,1:294]]}}, window_partition_by=[{name=contact_key, table_ref=src}], filters=[{name=funnel_priority, table_ref=src}, {name=sort_order, table_ref=src}, {name=contact_key, table_ref=src}, {name=program, table_ref=src}], interface={program=[{name=program, table_ref=src}], unnamed_0=[{name=funnel_priority, table_ref=src}, {name=sort_order, table_ref=src}, {name=contact_key, table_ref=src}, {name=program, table_ref=src}]}, table_alias={src=all_funnel_status_sort}}, interface={col1=[{name=program, table_ref=query0}], agg_col=[{name=unnamed_0, table_ref=query0}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void listaggWithinGroupDeleteWhereSymbolTableV5Test() {
		final String agg = listaggWithinGroupOver("e");
		final String query =
				"DELETE FROM employees e WHERE " + agg + " = 'y' AND e.emp_id = 1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{employees={funnel_priority=[[@18,78:78='e',<394>,1:78]], contact_key=[[@31,134:134='e',<394>,1:134]], program=[[@7,38:38='e',<394>,1:38]], sort_order=[[@22,97:97='e',<394>,1:97]], emp_id=[[@38,159:159='e',<394>,1:159]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_delete0={within_group_ordered_by=[{name=funnel_priority, table_ref=e}, {name=sort_order, table_ref=e}], query_dictionary={}, table_dictionary={employees={funnel_priority=[[@18,78:78='e',<394>,1:78]], contact_key=[[@31,134:134='e',<394>,1:134]], program=[[@7,38:38='e',<394>,1:38]], sort_order=[[@22,97:97='e',<394>,1:97]], emp_id=[[@38,159:159='e',<394>,1:159]]}}, window_partition_by=[{name=contact_key, table_ref=e}], filters=[{name=funnel_priority, table_ref=e}, {name=sort_order, table_ref=e}, {name=contact_key, table_ref=e}, {name=program, table_ref=e}, {name=emp_id, table_ref=e}], interface=null, table_alias={e=employees}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void listaggWithinGroupPriorSelectListAliasSymbolTableV6Test() {
		final String query =
				"SELECT t.funnel_priority AS " + PRIOR_ALIAS + ","
				+ " LISTAGG(t.program, '||')"
				+ " WITHIN GROUP (ORDER BY " + PRIOR_ALIAS + ", t.sort_order ASC)"
				+ " OVER (PARTITION BY " + PRIOR_ALIAS + ") AS fun_agg"
				+ " FROM all_funnel_status_sort t";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Assert.assertEquals("Interface is wrong", "[" + PRIOR_ALIAS + ", fun_agg]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{all_funnel_status_sort={funnel_priority=[[@1,7:7='t',<394>,1:7]], program=[[@9,49:49='t',<394>,1:49]], sort_order=[[@22,102:102='t',<394>,1:102]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={fun_agg=[[@34,155:161='fun_agg',<394>,1:155]], prior_alias=[[@5,28:38='prior_alias',<394>,1:28], [@20,89:99='prior_alias',<394>,1:89], [@31,139:149='prior_alias',<394>,1:139]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		assertWithinGroupOrderedByPriorAlias(extractor);
		assertWindowPartitionByPriorAlias(extractor);
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={within_group_ordered_by=[{name=prior_alias, table_ref=query0}, {name=sort_order, table_ref=t}], query_dictionary={prior_alias=[[@5,28:38='prior_alias',<394>,1:28], [@20,89:99='prior_alias',<394>,1:89], [@31,139:149='prior_alias',<394>,1:139]], fun_agg=[[@34,155:161='fun_agg',<394>,1:155]]}, table_dictionary={all_funnel_status_sort={funnel_priority=[[@1,7:7='t',<394>,1:7]], program=[[@9,49:49='t',<394>,1:49]], sort_order=[[@22,102:102='t',<394>,1:102]]}}, window_partition_by=[{name=prior_alias, table_ref=query0}], interface={prior_alias=[{name=funnel_priority, table_ref=t}], fun_agg=[{name=prior_alias, table_ref=query0}, {name=sort_order, table_ref=t}, {name=program, table_ref=t}]}, table_alias={t=all_funnel_status_sort}}}",
				extractor.getSymbolTable().toString());
	}

}
