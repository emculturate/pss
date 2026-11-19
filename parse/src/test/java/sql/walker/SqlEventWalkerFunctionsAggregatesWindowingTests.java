package sql.walker;
import java.util.Arrays;

import org.antlr.v4.runtime.tree.Trees;
import org.junit.Assert;
import org.junit.Test;

import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

/**
 * Window-function and aggregate coverage for physical-table queries (no PIVOT/UNPIVOT).
 * <p>
 * <b>Grammar vs ANSI:</b> {@code SQLSelectParser.g4} also allows {@code window_over_partition_expression}
 * under WHERE / HAVING / GROUP BY / JOIN ON / UPDATE SET via permissive {@code search_condition} /
 * {@code row_value_predicand} rules. Those sites are <em>not</em> ANSI-standard for windows and are
 * intentionally <b>untested</b> here — do not add walker golden tests for inline {@code OVER} in those
 * clauses unless product policy explicitly requires it.
 */
public class SqlEventWalkerFunctionsAggregatesWindowingTests extends AbstractSqlParseEventWalkerTest {

	private void assertExactDiagnostics(SqlParseEventWalker extractor, ParseDiagnostic... expected) {
		Assert.assertEquals("Diagnostics are wrong",
				Arrays.asList(expected), extractor.getSnippet().getParserDiagnosticList());
	}

	private void assertOutputs(SqlParseEventWalker extractor, String ast, String outputInterface, String substitutions,
			String tableDictionary, String queryDictionary, String symbolTable) {
		Assert.assertEquals("AST is wrong", ast, extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", outputInterface, extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", substitutions, extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", tableDictionary, extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", queryDictionary, extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", symbolTable, extractor.getSymbolTable().toString());
	}

	@Test
	public void aggregateFunctionWithDistinctQualifierTest() {
		final String query = " SELECT max(distinct a) FROM  tab1"; 

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=max, qualifier=distinct, parameters={column={name=a, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[[@4,21:21='a',<459>,1:21]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@5,22:22=')',<288>,1:22]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@5,22:22=')',<288>,1:22]]}, table_dictionary={tab1={a=[[@4,21:21='a',<459>,1:21]]}}, interface={unnamed_0=[{name=a, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionWindowOverPartitionByV7() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, "
			+ "row_number() over (partition by F4.col12 order by t.col3 desc) as rn FROM "
			+ "\n  (select * from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={alias=rn, window_function={over={partition_by={1={column={name=col12, table_ref=F4}}}, orderby={1={null_order=null, predicand={column={name=col3, table_ref=t}}, sort_order=desc}}}, function={function_name=row_number, parameters=null}}}}, from={join={1={table={alias=F4, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, tcol2, rn]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@35,127:127='*',<291>,2:10]]}, fourth={col5=[[@56,194:194='t',<459>,3:50]], col2=[[@7,25:25='t',<459>,1:25], [@48,173:173='t',<459>,3:29]], col3=[[@25,92:92='t',<459>,1:92]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@35,127:127='*',<291>,2:10]], col4=[[@52,184:185='F4',<459>,3:40]], col12=[[@20,74:75='F4',<459>,1:74]], col1=[[@1,8:9='F4',<459>,1:8], [@44,163:164='F4',<459>,3:19]]}, query1={last=[[@5,19:22='last',<102>,1:19]], rn=[[@31,108:109='rn',<459>,1:108]], tcol2=[[@11,35:39='tcol2',<459>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={window_ordered_by=[{name=col3, table_ref=t}], query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<459>,1:35]], rn=[[@31,108:109='rn',<459>,1:108]]}, table_dictionary={fourth={col5=[[@56,194:194='t',<459>,3:50]], col2=[[@7,25:25='t',<459>,1:25], [@48,173:173='t',<459>,3:29]], col3=[[@25,92:92='t',<459>,1:92]]}}, window_partition_by=[{name=col12, table_ref=F4}], def_query0={query_dictionary={col12=[[@20,74:75='F4',<459>,1:74]], *=[[@35,127:127='*',<291>,2:10]], col4=[[@52,184:185='F4',<459>,3:40]], col1=[[@1,8:9='F4',<459>,1:8], [@44,163:164='F4',<459>,3:19]]}, table_dictionary={third={*=[[@35,127:127='*',<291>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], rn=[{name=col12, table_ref=F4}, {name=col3, table_ref=t}]}, table_alias={F4=query0, t=fourth}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionWindowOverOrderByV8() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, "
			+ "row_number() over (partition by F4.col1 order by F4.col12 desc, F4.col1 asc) as rn FROM "
			+ "\n  (select * from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={alias=rn, window_function={over={partition_by={1={column={name=col1, table_ref=F4}}}, orderby={1={null_order=null, predicand={column={name=col12, table_ref=F4}}, sort_order=desc}, 2={null_order=null, predicand={column={name=col1, table_ref=F4}}, sort_order=asc}}}, function={function_name=row_number, parameters=null}}}}, from={join={1={table={alias=F4, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, tcol2, rn]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@40,141:141='*',<291>,2:10]]}, fourth={col5=[[@61,208:208='t',<459>,3:50]], col2=[[@7,25:25='t',<459>,1:25], [@53,187:187='t',<459>,3:29]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", 
		"{query0={*=[[@40,141:141='*',<291>,2:10]], col4=[[@57,198:199='F4',<459>,3:40]], col12=[[@25,91:92='F4',<459>,1:91]], col1=[[@1,8:9='F4',<459>,1:8], [@20,74:75='F4',<459>,1:74], [@30,106:107='F4',<459>,1:106], [@49,177:178='F4',<459>,3:19]]}, query1={last=[[@5,19:22='last',<102>,1:19]], rn=[[@36,122:123='rn',<459>,1:122]], tcol2=[[@11,35:39='tcol2',<459>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={window_ordered_by=[{name=col12, table_ref=F4}, {name=col1, table_ref=F4}], query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<459>,1:35]], rn=[[@36,122:123='rn',<459>,1:122]]}, table_dictionary={fourth={col5=[[@61,208:208='t',<459>,3:50]], col2=[[@7,25:25='t',<459>,1:25], [@53,187:187='t',<459>,3:29]]}}, window_partition_by=[{name=col1, table_ref=F4}], def_query0={query_dictionary={col12=[[@25,91:92='F4',<459>,1:91]], *=[[@40,141:141='*',<291>,2:10]], col4=[[@57,198:199='F4',<459>,3:40]], col1=[[@1,8:9='F4',<459>,1:8], [@20,74:75='F4',<459>,1:74], [@30,106:107='F4',<459>,1:106], [@49,177:178='F4',<459>,3:19]]}, table_dictionary={third={*=[[@40,141:141='*',<291>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], rn=[{name=col1, table_ref=F4}, {name=col12, table_ref=F4}]}, table_alias={F4=query0, t=fourth}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionWindowOverTableAliasV9() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, "
			+ "row_number() over (partition by t.col9 order by t.col3 desc, t.col9 asc) as rn FROM "
			+ "\n  (select * from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={alias=rn, window_function={over={partition_by={1={column={name=col9, table_ref=t}}}, orderby={1={null_order=null, predicand={column={name=col3, table_ref=t}}, sort_order=desc}, 2={null_order=null, predicand={column={name=col9, table_ref=t}}, sort_order=asc}}}, function={function_name=row_number, parameters=null}}}}, from={join={1={table={alias=F4, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, tcol2, rn]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@40,137:137='*',<291>,2:10]]}, fourth={col9=[[@20,74:74='t',<459>,1:74], [@30,103:103='t',<459>,1:103]], col5=[[@61,204:204='t',<459>,3:50]], col2=[[@7,25:25='t',<459>,1:25], [@53,183:183='t',<459>,3:29]], col3=[[@25,90:90='t',<459>,1:90]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={*=[[@40,137:137='*',<291>,2:10]], col4=[[@57,194:195='F4',<459>,3:40]], col1=[[@1,8:9='F4',<459>,1:8], [@49,173:174='F4',<459>,3:19]]}, query1={last=[[@5,19:22='last',<102>,1:19]], rn=[[@36,118:119='rn',<459>,1:118]], tcol2=[[@11,35:39='tcol2',<459>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={window_ordered_by=[{name=col3, table_ref=t}, {name=col9, table_ref=t}], query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<459>,1:35]], rn=[[@36,118:119='rn',<459>,1:118]]}, table_dictionary={fourth={col9=[[@20,74:74='t',<459>,1:74], [@30,103:103='t',<459>,1:103]], col5=[[@61,204:204='t',<459>,3:50]], col2=[[@7,25:25='t',<459>,1:25], [@53,183:183='t',<459>,3:29]], col3=[[@25,90:90='t',<459>,1:90]]}}, window_partition_by=[{name=col9, table_ref=t}], def_query0={query_dictionary={*=[[@40,137:137='*',<291>,2:10]], col4=[[@57,194:195='F4',<459>,3:40]], col1=[[@1,8:9='F4',<459>,1:8], [@49,173:174='F4',<459>,3:19]]}, table_dictionary={third={*=[[@40,137:137='*',<291>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], rn=[{name=col9, table_ref=t}, {name=col3, table_ref=t}]}, table_alias={F4=query0, t=fourth}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionWindowOverMixedV10() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, "
			+ "row_number() over (partition by F4.col1 order by F4.col12 desc) as rn1, "
			+ "sum(t.col9) over (partition by t.col2 order by F4.col1 asc) as rn2 FROM "
			+ "\n  (select * from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={alias=rn1, window_function={over={partition_by={1={column={name=col1, table_ref=F4}}}, orderby={1={null_order=null, predicand={column={name=col12, table_ref=F4}}, sort_order=desc}}}, function={function_name=row_number, parameters=null}}}, 4={alias=rn2, window_function={over={partition_by={1={column={name=col2, table_ref=t}}}, orderby={1={null_order=null, predicand={column={name=col1, table_ref=F4}}, sort_order=asc}}}, function={function_name=sum, parameters={1={column={name=col9, table_ref=t}}}}}}}, from={join={1={table={alias=F4, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, rn1, tcol2, rn2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@58,197:197='*',<291>,2:10]]}, fourth={col9=[[@35,118:118='t',<459>,1:118]], col5=[[@79,264:264='t',<459>,3:50]], col2=[[@7,25:25='t',<459>,1:25], [@43,145:145='t',<459>,1:145], [@71,243:243='t',<459>,3:29]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={*=[[@58,197:197='*',<291>,2:10]], col4=[[@75,254:255='F4',<459>,3:40]], col12=[[@25,91:92='F4',<459>,1:91]], col1=[[@1,8:9='F4',<459>,1:8], [@20,74:75='F4',<459>,1:74], [@48,161:162='F4',<459>,1:161], [@67,233:234='F4',<459>,3:19]]}, query1={last=[[@5,19:22='last',<102>,1:19]], rn1=[[@31,109:111='rn1',<459>,1:109]], tcol2=[[@11,35:39='tcol2',<459>,1:35]], rn2=[[@54,177:179='rn2',<459>,1:177]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={window_ordered_by=[{name=col12, table_ref=F4}, {name=col1, table_ref=F4}], query_dictionary={last=[[@5,19:22='last',<102>,1:19]], rn1=[[@31,109:111='rn1',<459>,1:109]], tcol2=[[@11,35:39='tcol2',<459>,1:35]], rn2=[[@54,177:179='rn2',<459>,1:177]]}, table_dictionary={fourth={col9=[[@35,118:118='t',<459>,1:118]], col5=[[@79,264:264='t',<459>,3:50]], col2=[[@7,25:25='t',<459>,1:25], [@43,145:145='t',<459>,1:145], [@71,243:243='t',<459>,3:29]]}}, window_partition_by=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}], def_query0={query_dictionary={col12=[[@25,91:92='F4',<459>,1:91]], *=[[@58,197:197='*',<291>,2:10]], col4=[[@75,254:255='F4',<459>,3:40]], col1=[[@1,8:9='F4',<459>,1:8], [@20,74:75='F4',<459>,1:74], [@48,161:162='F4',<459>,1:161], [@67,233:234='F4',<459>,3:19]]}, table_dictionary={third={*=[[@58,197:197='*',<291>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], rn1=[{name=col1, table_ref=F4}, {name=col12, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], rn2=[{name=col2, table_ref=t}, {name=col1, table_ref=F4}, {name=col9, table_ref=t}]}, table_alias={F4=query0, t=fourth}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionWindowOverPartitionByV17() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, "
			+ "row_number() over (partition by F4.col12 order by t.col3 desc) as rn FROM "
			+ "\n  (select a as col1, col4, col12 from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={alias=rn, window_function={over={partition_by={1={column={name=col12, table_ref=F4}}}, orderby={1={null_order=null, predicand={column={name=col3, table_ref=t}}, sort_order=desc}}}, function={function_name=row_number, parameters=null}}}}, from={join={1={table={alias=F4, query={select={1={column={name=a, table_ref=null}, alias=col1}, 2={column={name=col4, table_ref=null}}, 3={column={name=col12, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, tcol2, rn]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@35,127:127='a',<459>,2:10]], col12=[[@41,144:148='col12',<459>,2:27]], col4=[[@39,138:141='col4',<459>,2:21]]}, fourth={col5=[[@62,215:215='t',<459>,3:50]], col2=[[@7,25:25='t',<459>,1:25], [@54,194:194='t',<459>,3:29]], col3=[[@25,92:92='t',<459>,1:92]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={col12=[[@41,144:148='col12',<459>,2:27], [@20,74:75='F4',<459>,1:74]], col4=[[@39,138:141='col4',<459>,2:21], [@58,205:206='F4',<459>,3:40]], col1=[[@37,132:135='col1',<459>,2:15], [@1,8:9='F4',<459>,1:8], [@50,184:185='F4',<459>,3:19]]}, query1={last=[[@5,19:22='last',<102>,1:19]], rn=[[@31,108:109='rn',<459>,1:108]], tcol2=[[@11,35:39='tcol2',<459>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={window_ordered_by=[{name=col3, table_ref=t}], query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<459>,1:35]], rn=[[@31,108:109='rn',<459>,1:108]]}, table_dictionary={fourth={col5=[[@62,215:215='t',<459>,3:50]], col2=[[@7,25:25='t',<459>,1:25], [@54,194:194='t',<459>,3:29]], col3=[[@25,92:92='t',<459>,1:92]]}}, window_partition_by=[{name=col12, table_ref=F4}], def_query0={query_dictionary={col12=[[@41,144:148='col12',<459>,2:27], [@20,74:75='F4',<459>,1:74]], col4=[[@39,138:141='col4',<459>,2:21], [@58,205:206='F4',<459>,3:40]], col1=[[@37,132:135='col1',<459>,2:15], [@1,8:9='F4',<459>,1:8], [@50,184:185='F4',<459>,3:19]]}, table_dictionary={third={a=[[@35,127:127='a',<459>,2:10]], col12=[[@41,144:148='col12',<459>,2:27]], col4=[[@39,138:141='col4',<459>,2:21]]}}, interface={col12=[{name=col12, table_ref=third}], col4=[{name=col4, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], rn=[{name=col12, table_ref=F4}, {name=col3, table_ref=t}]}, table_alias={F4=query0, t=fourth}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionWindowOverOrderByV18() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, "
			+ "row_number() over (partition by F4.col1 order by F4.col12 desc, F4.col1 asc) as rn FROM "
			+ "\n  (select a as col1, col4, col12 from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={alias=rn, window_function={over={partition_by={1={column={name=col1, table_ref=F4}}}, orderby={1={null_order=null, predicand={column={name=col12, table_ref=F4}}, sort_order=desc}, 2={null_order=null, predicand={column={name=col1, table_ref=F4}}, sort_order=asc}}}, function={function_name=row_number, parameters=null}}}}, from={join={1={table={alias=F4, query={select={1={column={name=a, table_ref=null}, alias=col1}, 2={column={name=col4, table_ref=null}}, 3={column={name=col12, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, tcol2, rn]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@40,141:141='a',<459>,2:10]], col12=[[@46,158:162='col12',<459>,2:27]], col4=[[@44,152:155='col4',<459>,2:21]]}, fourth={col5=[[@67,229:229='t',<459>,3:50]], col2=[[@7,25:25='t',<459>,1:25], [@59,208:208='t',<459>,3:29]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={col12=[[@46,158:162='col12',<459>,2:27], [@25,91:92='F4',<459>,1:91]], col4=[[@44,152:155='col4',<459>,2:21], [@63,219:220='F4',<459>,3:40]], col1=[[@42,146:149='col1',<459>,2:15], [@1,8:9='F4',<459>,1:8], [@20,74:75='F4',<459>,1:74], [@30,106:107='F4',<459>,1:106], [@55,198:199='F4',<459>,3:19]]}, query1={last=[[@5,19:22='last',<102>,1:19]], rn=[[@36,122:123='rn',<459>,1:122]], tcol2=[[@11,35:39='tcol2',<459>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={window_ordered_by=[{name=col12, table_ref=F4}, {name=col1, table_ref=F4}], query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<459>,1:35]], rn=[[@36,122:123='rn',<459>,1:122]]}, table_dictionary={fourth={col5=[[@67,229:229='t',<459>,3:50]], col2=[[@7,25:25='t',<459>,1:25], [@59,208:208='t',<459>,3:29]]}}, window_partition_by=[{name=col1, table_ref=F4}], def_query0={query_dictionary={col12=[[@46,158:162='col12',<459>,2:27], [@25,91:92='F4',<459>,1:91]], col4=[[@44,152:155='col4',<459>,2:21], [@63,219:220='F4',<459>,3:40]], col1=[[@42,146:149='col1',<459>,2:15], [@1,8:9='F4',<459>,1:8], [@20,74:75='F4',<459>,1:74], [@30,106:107='F4',<459>,1:106], [@55,198:199='F4',<459>,3:19]]}, table_dictionary={third={a=[[@40,141:141='a',<459>,2:10]], col12=[[@46,158:162='col12',<459>,2:27]], col4=[[@44,152:155='col4',<459>,2:21]]}}, interface={col12=[{name=col12, table_ref=third}], col4=[{name=col4, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], rn=[{name=col1, table_ref=F4}, {name=col12, table_ref=F4}]}, table_alias={F4=query0, t=fourth}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionWindowOverTableAliasV19() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, "
			+ "row_number() over (partition by t.col9 order by t.col3 desc, t.col9 asc) as rn FROM "
			+ "\n  (select a as col1, col4 from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={alias=rn, window_function={over={partition_by={1={column={name=col9, table_ref=t}}}, orderby={1={null_order=null, predicand={column={name=col3, table_ref=t}}, sort_order=desc}, 2={null_order=null, predicand={column={name=col9, table_ref=t}}, sort_order=asc}}}, function={function_name=row_number, parameters=null}}}}, from={join={1={table={alias=F4, query={select={1={column={name=a, table_ref=null}, alias=col1}, 2={column={name=col4, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, tcol2, rn]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@40,137:137='a',<459>,2:10]], col4=[[@44,148:151='col4',<459>,2:21]]}, fourth={col9=[[@20,74:74='t',<459>,1:74], [@30,103:103='t',<459>,1:103]], col5=[[@65,218:218='t',<459>,3:50]], col2=[[@7,25:25='t',<459>,1:25], [@57,197:197='t',<459>,3:29]], col3=[[@25,90:90='t',<459>,1:90]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={col4=[[@44,148:151='col4',<459>,2:21], [@61,208:209='F4',<459>,3:40]], col1=[[@42,142:145='col1',<459>,2:15], [@1,8:9='F4',<459>,1:8], [@53,187:188='F4',<459>,3:19]]}, query1={last=[[@5,19:22='last',<102>,1:19]], rn=[[@36,118:119='rn',<459>,1:118]], tcol2=[[@11,35:39='tcol2',<459>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={window_ordered_by=[{name=col3, table_ref=t}, {name=col9, table_ref=t}], query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<459>,1:35]], rn=[[@36,118:119='rn',<459>,1:118]]}, table_dictionary={fourth={col9=[[@20,74:74='t',<459>,1:74], [@30,103:103='t',<459>,1:103]], col5=[[@65,218:218='t',<459>,3:50]], col2=[[@7,25:25='t',<459>,1:25], [@57,197:197='t',<459>,3:29]], col3=[[@25,90:90='t',<459>,1:90]]}}, window_partition_by=[{name=col9, table_ref=t}], def_query0={query_dictionary={col4=[[@44,148:151='col4',<459>,2:21], [@61,208:209='F4',<459>,3:40]], col1=[[@42,142:145='col1',<459>,2:15], [@1,8:9='F4',<459>,1:8], [@53,187:188='F4',<459>,3:19]]}, table_dictionary={third={a=[[@40,137:137='a',<459>,2:10]], col4=[[@44,148:151='col4',<459>,2:21]]}}, interface={col4=[{name=col4, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], rn=[{name=col9, table_ref=t}, {name=col3, table_ref=t}]}, table_alias={F4=query0, t=fourth}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionWindowOverMixedV20() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, "
			+ "row_number() over (partition by F4.col1 order by F4.col12 desc) as rn1, "
			+ "sum(t.col9) over (partition by t.col2 order by F4.col1 asc) as rn2 FROM "
			+ "\n  (select a as col1, col4, col12 from third) F4"
			+ "\n  join fourth t on F4.col1 = t.col2 and F4.col4 = t.col5";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={alias=rn1, window_function={over={partition_by={1={column={name=col1, table_ref=F4}}}, orderby={1={null_order=null, predicand={column={name=col12, table_ref=F4}}, sort_order=desc}}}, function={function_name=row_number, parameters=null}}}, 4={alias=rn2, window_function={over={partition_by={1={column={name=col2, table_ref=t}}}, orderby={1={null_order=null, predicand={column={name=col1, table_ref=F4}}, sort_order=asc}}}, function={function_name=sum, parameters={1={column={name=col9, table_ref=t}}}}}}}, from={join={1={table={alias=F4, query={select={1={column={name=a, table_ref=null}, alias=col1}, 2={column={name=col4, table_ref=null}}, 3={column={name=col12, table_ref=null}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, table=fourth}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, rn1, tcol2, rn2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@58,197:197='a',<459>,2:10]], col12=[[@64,214:218='col12',<459>,2:27]], col4=[[@62,208:211='col4',<459>,2:21]]}, fourth={col9=[[@35,118:118='t',<459>,1:118]], col5=[[@85,285:285='t',<459>,3:50]], col2=[[@7,25:25='t',<459>,1:25], [@43,145:145='t',<459>,1:145], [@77,264:264='t',<459>,3:29]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={col12=[[@64,214:218='col12',<459>,2:27], [@25,91:92='F4',<459>,1:91]], col4=[[@62,208:211='col4',<459>,2:21], [@81,275:276='F4',<459>,3:40]], col1=[[@60,202:205='col1',<459>,2:15], [@1,8:9='F4',<459>,1:8], [@20,74:75='F4',<459>,1:74], [@48,161:162='F4',<459>,1:161], [@73,254:255='F4',<459>,3:19]]}, query1={last=[[@5,19:22='last',<102>,1:19]], rn1=[[@31,109:111='rn1',<459>,1:109]], tcol2=[[@11,35:39='tcol2',<459>,1:35]], rn2=[[@54,177:179='rn2',<459>,1:177]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={window_ordered_by=[{name=col12, table_ref=F4}, {name=col1, table_ref=F4}], query_dictionary={last=[[@5,19:22='last',<102>,1:19]], rn1=[[@31,109:111='rn1',<459>,1:109]], tcol2=[[@11,35:39='tcol2',<459>,1:35]], rn2=[[@54,177:179='rn2',<459>,1:177]]}, table_dictionary={fourth={col9=[[@35,118:118='t',<459>,1:118]], col5=[[@85,285:285='t',<459>,3:50]], col2=[[@7,25:25='t',<459>,1:25], [@43,145:145='t',<459>,1:145], [@77,264:264='t',<459>,3:29]]}}, window_partition_by=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}], def_query0={query_dictionary={col12=[[@64,214:218='col12',<459>,2:27], [@25,91:92='F4',<459>,1:91]], col4=[[@62,208:211='col4',<459>,2:21], [@81,275:276='F4',<459>,3:40]], col1=[[@60,202:205='col1',<459>,2:15], [@1,8:9='F4',<459>,1:8], [@20,74:75='F4',<459>,1:74], [@48,161:162='F4',<459>,1:161], [@73,254:255='F4',<459>,3:19]]}, table_dictionary={third={a=[[@58,197:197='a',<459>,2:10]], col12=[[@64,214:218='col12',<459>,2:27]], col4=[[@62,208:211='col4',<459>,2:21]]}}, interface={col12=[{name=col12, table_ref=third}], col4=[{name=col4, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], rn1=[{name=col1, table_ref=F4}, {name=col12, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], rn2=[{name=col2, table_ref=t}, {name=col1, table_ref=F4}, {name=col9, table_ref=t}]}, table_alias={F4=query0, t=fourth}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionWindowOverPartitionBySubqueryJoinV27() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, "
			+ "row_number() over (partition by F4.col12 order by t.col3 desc) as rn FROM "
			+ "\n  (select * from third) F4"
			+ "\n  join (select * from fourth) as t on F4.col1 = t.col2 and F4.col4 = t.col5";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={alias=rn, window_function={over={partition_by={1={column={name=col12, table_ref=F4}}}, orderby={1={null_order=null, predicand={column={name=col3, table_ref=t}}, sort_order=desc}}}, function={function_name=row_number, parameters=null}}}}, from={join={1={table={alias=F4, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=fourth}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, tcol2, rn]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@35,127:127='*',<291>,2:10]]}, fourth={*=[[@43,159:159='*',<291>,3:15]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={*=[[@35,127:127='*',<291>,2:10]], col4=[[@58,203:204='F4',<459>,3:59]], col12=[[@20,74:75='F4',<459>,1:74]], col1=[[@1,8:9='F4',<459>,1:8], [@50,182:183='F4',<459>,3:38]]}, query1={*=[[@43,159:159='*',<291>,3:15]], col5=[[@62,213:213='t',<459>,3:69]], col2=[[@7,25:25='t',<459>,1:25], [@54,192:192='t',<459>,3:48]], col3=[[@25,92:92='t',<459>,1:92]]}, query2={last=[[@5,19:22='last',<102>,1:19]], rn=[[@31,108:109='rn',<459>,1:108]], tcol2=[[@11,35:39='tcol2',<459>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={window_ordered_by=[{name=col3, table_ref=t}], query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<459>,1:35]], rn=[[@31,108:109='rn',<459>,1:108]]}, window_partition_by=[{name=col12, table_ref=F4}], def_query1={query_dictionary={*=[[@43,159:159='*',<291>,3:15]], col5=[[@62,213:213='t',<459>,3:69]], col2=[[@7,25:25='t',<459>,1:25], [@54,192:192='t',<459>,3:48]], col3=[[@25,92:92='t',<459>,1:92]]}, table_dictionary={fourth={*=[[@43,159:159='*',<291>,3:15]]}}, interface={*=[{name=*, table_ref=*}]}}, def_query0={query_dictionary={col12=[[@20,74:75='F4',<459>,1:74]], *=[[@35,127:127='*',<291>,2:10]], col4=[[@58,203:204='F4',<459>,3:59]], col1=[[@1,8:9='F4',<459>,1:8], [@50,182:183='F4',<459>,3:38]]}, table_dictionary={third={*=[[@35,127:127='*',<291>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], rn=[{name=col12, table_ref=F4}, {name=col3, table_ref=t}]}, table_alias={F4=query0, t=query1}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionWindowOverOrderBySubqueryJoinV28() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, "
			+ "row_number() over (partition by F4.col1 order by F4.col12 desc, F4.col1 asc) as rn FROM "
			+ "\n  (select * from third) F4"
			+ "\n  join (select * from fourth) as t on F4.col1 = t.col2 and F4.col4 = t.col5";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={alias=rn, window_function={over={partition_by={1={column={name=col1, table_ref=F4}}}, orderby={1={null_order=null, predicand={column={name=col12, table_ref=F4}}, sort_order=desc}, 2={null_order=null, predicand={column={name=col1, table_ref=F4}}, sort_order=asc}}}, function={function_name=row_number, parameters=null}}}}, from={join={1={table={alias=F4, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=fourth}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, tcol2, rn]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@40,141:141='*',<291>,2:10]]}, fourth={*=[[@48,173:173='*',<291>,3:15]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={*=[[@40,141:141='*',<291>,2:10]], col4=[[@63,217:218='F4',<459>,3:59]], col12=[[@25,91:92='F4',<459>,1:91]], col1=[[@1,8:9='F4',<459>,1:8], [@20,74:75='F4',<459>,1:74], [@30,106:107='F4',<459>,1:106], [@55,196:197='F4',<459>,3:38]]}, query1={col2=[[@7,25:25='t',<459>,1:25], [@59,206:206='t',<459>,3:48]], *=[[@48,173:173='*',<291>,3:15]], col5=[[@67,227:227='t',<459>,3:69]]}, query2={last=[[@5,19:22='last',<102>,1:19]], rn=[[@36,122:123='rn',<459>,1:122]], tcol2=[[@11,35:39='tcol2',<459>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={window_ordered_by=[{name=col12, table_ref=F4}, {name=col1, table_ref=F4}], query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<459>,1:35]], rn=[[@36,122:123='rn',<459>,1:122]]}, window_partition_by=[{name=col1, table_ref=F4}], def_query1={query_dictionary={*=[[@48,173:173='*',<291>,3:15]], col5=[[@67,227:227='t',<459>,3:69]], col2=[[@7,25:25='t',<459>,1:25], [@59,206:206='t',<459>,3:48]]}, table_dictionary={fourth={*=[[@48,173:173='*',<291>,3:15]]}}, interface={*=[{name=*, table_ref=*}]}}, def_query0={query_dictionary={col12=[[@25,91:92='F4',<459>,1:91]], *=[[@40,141:141='*',<291>,2:10]], col4=[[@63,217:218='F4',<459>,3:59]], col1=[[@1,8:9='F4',<459>,1:8], [@20,74:75='F4',<459>,1:74], [@30,106:107='F4',<459>,1:106], [@55,196:197='F4',<459>,3:38]]}, table_dictionary={third={*=[[@40,141:141='*',<291>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], rn=[{name=col1, table_ref=F4}, {name=col12, table_ref=F4}]}, table_alias={F4=query0, t=query1}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionWindowOverTableAliasSubqueryJoinV29() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, "
			+ "row_number() over (partition by t.col9 order by t.col3 desc, t.col9 asc) as rn FROM "
			+ "\n  (select * from third) F4"
			+ "\n  join (select * from fourth) as t on F4.col1 = t.col2 and F4.col4 = t.col5";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={alias=rn, window_function={over={partition_by={1={column={name=col9, table_ref=t}}}, orderby={1={null_order=null, predicand={column={name=col3, table_ref=t}}, sort_order=desc}, 2={null_order=null, predicand={column={name=col9, table_ref=t}}, sort_order=asc}}}, function={function_name=row_number, parameters=null}}}}, from={join={1={table={alias=F4, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=fourth}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, tcol2, rn]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@40,137:137='*',<291>,2:10]]}, fourth={*=[[@48,169:169='*',<291>,3:15]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={*=[[@40,137:137='*',<291>,2:10]], col4=[[@63,213:214='F4',<459>,3:59]], col1=[[@1,8:9='F4',<459>,1:8], [@55,192:193='F4',<459>,3:38]]}, query1={*=[[@48,169:169='*',<291>,3:15]], col5=[[@67,223:223='t',<459>,3:69]], col2=[[@7,25:25='t',<459>,1:25], [@59,202:202='t',<459>,3:48]], col3=[[@25,90:90='t',<459>,1:90]], col9=[[@20,74:74='t',<459>,1:74], [@30,103:103='t',<459>,1:103]]}, query2={last=[[@5,19:22='last',<102>,1:19]], rn=[[@36,118:119='rn',<459>,1:118]], tcol2=[[@11,35:39='tcol2',<459>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={window_ordered_by=[{name=col3, table_ref=t}, {name=col9, table_ref=t}], query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<459>,1:35]], rn=[[@36,118:119='rn',<459>,1:118]]}, window_partition_by=[{name=col9, table_ref=t}], def_query1={query_dictionary={col9=[[@20,74:74='t',<459>,1:74], [@30,103:103='t',<459>,1:103]], *=[[@48,169:169='*',<291>,3:15]], col5=[[@67,223:223='t',<459>,3:69]], col2=[[@7,25:25='t',<459>,1:25], [@59,202:202='t',<459>,3:48]], col3=[[@25,90:90='t',<459>,1:90]]}, table_dictionary={fourth={*=[[@48,169:169='*',<291>,3:15]]}}, interface={*=[{name=*, table_ref=*}]}}, def_query0={query_dictionary={*=[[@40,137:137='*',<291>,2:10]], col4=[[@63,213:214='F4',<459>,3:59]], col1=[[@1,8:9='F4',<459>,1:8], [@55,192:193='F4',<459>,3:38]]}, table_dictionary={third={*=[[@40,137:137='*',<291>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], rn=[{name=col9, table_ref=t}, {name=col3, table_ref=t}]}, table_alias={F4=query0, t=query1}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void subqueryDictionaryExtensionWindowOverMixedSubqueryJoinV30() {
		final String query = " SELECT F4.col1 as last, t.col2 as tcol2, "
			+ "row_number() over (partition by F4.col1 order by F4.col12 desc) as rn1, "
			+ "sum(t.col9) over (partition by t.col2 order by F4.col1 asc) as rn2 FROM "
			+ "\n  (select * from third) F4"
			+ "\n  join (select * from fourth) as t on F4.col1 = t.col2 and F4.col4 = t.col5";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=F4}, alias=last}, 2={column={name=col2, table_ref=t}, alias=tcol2}, 3={alias=rn1, window_function={over={partition_by={1={column={name=col1, table_ref=F4}}}, orderby={1={null_order=null, predicand={column={name=col12, table_ref=F4}}, sort_order=desc}}}, function={function_name=row_number, parameters=null}}}, 4={alias=rn2, window_function={over={partition_by={1={column={name=col2, table_ref=t}}}, orderby={1={null_order=null, predicand={column={name=col1, table_ref=F4}}, sort_order=asc}}}, function={function_name=sum, parameters={1={column={name=col9, table_ref=t}}}}}}}, from={join={1={table={alias=F4, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=third}}}}}, 2={join=join, on={and={1={condition={left={column={name=col1, table_ref=F4}}, right={column={name=col2, table_ref=t}}, operator==}}, 2={condition={left={column={name=col4, table_ref=F4}}, right={column={name=col5, table_ref=t}}, operator==}}}}}, 3={table={alias=t, query={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=fourth}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[last, rn1, tcol2, rn2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@58,197:197='*',<291>,2:10]]}, fourth={*=[[@66,229:229='*',<291>,3:15]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={*=[[@58,197:197='*',<291>,2:10]], col4=[[@81,273:274='F4',<459>,3:59]], col12=[[@25,91:92='F4',<459>,1:91]], col1=[[@1,8:9='F4',<459>,1:8], [@20,74:75='F4',<459>,1:74], [@48,161:162='F4',<459>,1:161], [@73,252:253='F4',<459>,3:38]]}, query1={*=[[@66,229:229='*',<291>,3:15]], col5=[[@85,283:283='t',<459>,3:69]], col2=[[@7,25:25='t',<459>,1:25], [@43,145:145='t',<459>,1:145], [@77,262:262='t',<459>,3:48]], col9=[[@35,118:118='t',<459>,1:118]]}, query2={last=[[@5,19:22='last',<102>,1:19]], rn1=[[@31,109:111='rn1',<459>,1:109]], tcol2=[[@11,35:39='tcol2',<459>,1:35]], rn2=[[@54,177:179='rn2',<459>,1:177]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={window_ordered_by=[{name=col12, table_ref=F4}, {name=col1, table_ref=F4}], query_dictionary={last=[[@5,19:22='last',<102>,1:19]], rn1=[[@31,109:111='rn1',<459>,1:109]], tcol2=[[@11,35:39='tcol2',<459>,1:35]], rn2=[[@54,177:179='rn2',<459>,1:177]]}, window_partition_by=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}], def_query1={query_dictionary={col9=[[@35,118:118='t',<459>,1:118]], *=[[@66,229:229='*',<291>,3:15]], col5=[[@85,283:283='t',<459>,3:69]], col2=[[@7,25:25='t',<459>,1:25], [@43,145:145='t',<459>,1:145], [@77,262:262='t',<459>,3:48]]}, table_dictionary={fourth={*=[[@66,229:229='*',<291>,3:15]]}}, interface={*=[{name=*, table_ref=*}]}}, def_query0={query_dictionary={col12=[[@25,91:92='F4',<459>,1:91]], *=[[@58,197:197='*',<291>,2:10]], col4=[[@81,273:274='F4',<459>,3:59]], col1=[[@1,8:9='F4',<459>,1:8], [@20,74:75='F4',<459>,1:74], [@48,161:162='F4',<459>,1:161], [@73,252:253='F4',<459>,3:38]]}, table_dictionary={third={*=[[@58,197:197='*',<291>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], rn1=[{name=col1, table_ref=F4}, {name=col12, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], rn2=[{name=col2, table_ref=t}, {name=col1, table_ref=F4}, {name=col9, table_ref=t}]}, table_alias={F4=query0, t=query1}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void rankReservedWordTest() {
		final String query = "SELECT rank() OVER (partition by k_stfd, kppi order by OBSERVATION_TM desc, row_num desc) AS key_rank from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={partition_by={1={column={name=k_stfd, table_ref=null}}, 2={column={name=kppi, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}, 2={null_order=null, predicand={column={name=row_num, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters=null}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[key_rank]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={k_stfd=[[@8,33:38='k_stfd',<459>,1:33]], row_num=[[@16,76:82='row_num',<459>,1:76]], kppi=[[@10,41:44='kppi',<459>,1:41]], OBSERVATION_TM=[[@13,55:68='OBSERVATION_TM',<459>,1:55]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={key_rank=[[@20,93:100='key_rank',<459>,1:93]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=OBSERVATION_TM, table_ref=null}, {name=row_num, table_ref=null}], query_dictionary={key_rank=[[@20,93:100='key_rank',<459>,1:93]]}, table_dictionary={tab1={k_stfd=[[@8,33:38='k_stfd',<459>,1:33]], row_num=[[@16,76:82='row_num',<459>,1:76]], kppi=[[@10,41:44='kppi',<459>,1:41]], OBSERVATION_TM=[[@13,55:68='OBSERVATION_TM',<459>,1:55]]}}, window_partition_by=[{name=k_stfd, table_ref=null}, {name=kppi, table_ref=null}], interface={key_rank=[{name=k_stfd, table_ref=tab1}, {name=kppi, table_ref=tab1}, {name=OBSERVATION_TM, table_ref=tab1}, {name=row_num, table_ref=tab1}]}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void queryHasRankAsBothColumnAndReservedWordTest() {
		final String query = "SELECT rank() OVER (partition by k_stfd, kppi order by OBSERVATION_TM desc, row_num desc) AS rank from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=rank, window_function={over={partition_by={1={column={name=k_stfd, table_ref=null}}, 2={column={name=kppi, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}, 2={null_order=null, predicand={column={name=row_num, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters=null}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[rank]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={k_stfd=[[@8,33:38='k_stfd',<459>,1:33]], row_num=[[@16,76:82='row_num',<459>,1:76]], kppi=[[@10,41:44='kppi',<459>,1:41]], OBSERVATION_TM=[[@13,55:68='OBSERVATION_TM',<459>,1:55]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={rank=[[@20,93:96='rank',<128>,1:93]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=OBSERVATION_TM, table_ref=null}, {name=row_num, table_ref=null}], query_dictionary={rank=[[@20,93:96='rank',<128>,1:93]]}, table_dictionary={tab1={k_stfd=[[@8,33:38='k_stfd',<459>,1:33]], row_num=[[@16,76:82='row_num',<459>,1:76]], kppi=[[@10,41:44='kppi',<459>,1:41]], OBSERVATION_TM=[[@13,55:68='OBSERVATION_TM',<459>,1:55]]}}, window_partition_by=[{name=k_stfd, table_ref=null}, {name=kppi, table_ref=null}], interface={rank=[{name=k_stfd, table_ref=tab1}, {name=kppi, table_ref=tab1}, {name=OBSERVATION_TM, table_ref=tab1}, {name=row_num, table_ref=tab1}]}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void complexCaseExplicitConditionExpressionWithPredicandSubstitutionInQueryTest() {
		String sql = "select case when <column1> then 'Y' when <column2> = false then 'N' else 'N' end from tab1";
		final SQLSelectParserParser parser = parse(sql);
		
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={case={clauses={1={then={literal='Y'}, when={substitution={name=<column1>, type=condition}}}, 2={then={literal='N'}, when={condition={left={substitution={name=<column2>, type=predicand}}, right={literal=false}, operator==}}}}, else={literal='N'}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<column1>=condition, <column2>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@14,77:79='end',<12>,1:77]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={unnamed_0=[[@14,77:79='end',<12>,1:77]]}, table_dictionary={tab1={}}, interface={unnamed_0=[{name=<column2>, type=predicand}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void caseExpressionStatementParseTest() {
		final String query = " SELECT CASE WHEN a < b THEN 'Y' WHEN a = b THEN 'N' "
				+ " ELSE 'N' END as case_one " 
				+ " FROM sgbstdn ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=case_one, case={clauses={1={then={literal='Y'}, when={condition={left={column={name=a, table_ref=null}}, right={column={name=b, table_ref=null}}, operator=<}}}, 2={then={literal='N'}, when={condition={left={column={name=a, table_ref=null}}, right={column={name=b, table_ref=null}}, operator==}}}}, else={literal='N'}}}}, from={table={alias=null, table=sgbstdn}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[case_one]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{sgbstdn={a=[[@3,18:18='a',<459>,1:18], [@9,38:38='a',<459>,1:38]], b=[[@5,22:22='b',<459>,1:22], [@11,42:42='b',<459>,1:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={case_one=[[@18,70:77='case_one',<459>,1:70]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={case_one=[[@18,70:77='case_one',<459>,1:70]]}, table_dictionary={sgbstdn={a=[[@3,18:18='a',<459>,1:18], [@9,38:38='a',<459>,1:38]], b=[[@5,22:22='b',<459>,1:22], [@11,42:42='b',<459>,1:42]]}}, interface={case_one=[{name=a, table_ref=sgbstdn}, {name=b, table_ref=sgbstdn}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void complexCaseFunctionTest() {

		final String query = " SELECT " + " CASE   " + " WHEN s948.OBSERVATION_TM THEN S948.t_student_last_name   "
				+ " WHEN COALESCE( S949.OBSERVATION_TM>=S948.OBSERVATION_TM , FALSE) THEN S949.t_student_last_name   "
				+ " ELSE COALESCE(S948.t_student_last_name, S949.t_student_last_name) END AS t_student_last_name "
				+ " FROM my.234 as s948, my.other5 as s949";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=t_student_last_name, case={clauses={1={then={column={name=t_student_last_name, table_ref=S948}}, when={column={name=OBSERVATION_TM, table_ref=s948}}}, 2={then={column={name=t_student_last_name, table_ref=S949}}, when={function={parameters={1={condition={left={column={name=OBSERVATION_TM, table_ref=S949}}, right={column={name=OBSERVATION_TM, table_ref=S948}}, operator=>=}}, 2={literal=FALSE}}, function_name=COALESCE}}}}, else={function={parameters={1={column={name=t_student_last_name, table_ref=S948}}, 2={column={name=t_student_last_name, table_ref=S949}}}, function_name=COALESCE}}}}}, from={join={1={table={alias=s948, schema=my, table=234}}, 2={table={alias=s949, schema=my, table=other5}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[t_student_last_name]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{my.other5={t_student_last_name=[[@24,145:148='S949',<459>,1:145], [@34,213:216='S949',<459>,1:213]], OBSERVATION_TM=[[@13,90:93='S949',<459>,1:90]]}, my.234={t_student_last_name=[[@7,47:50='S948',<459>,1:47], [@30,187:190='S948',<459>,1:187]], OBSERVATION_TM=[[@3,22:25='s948',<459>,1:22], [@17,111:114='S948',<459>,1:111]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={t_student_last_name=[[@40,246:264='t_student_last_name',<459>,1:246]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={t_student_last_name=[[@40,246:264='t_student_last_name',<459>,1:246]]}, table_dictionary={my.other5={t_student_last_name=[[@24,145:148='S949',<459>,1:145], [@34,213:216='S949',<459>,1:213]], OBSERVATION_TM=[[@13,90:93='S949',<459>,1:90]]}, my.234={t_student_last_name=[[@7,47:50='S948',<459>,1:47], [@30,187:190='S948',<459>,1:187]], OBSERVATION_TM=[[@3,22:25='s948',<459>,1:22], [@17,111:114='S948',<459>,1:111]]}}, interface={t_student_last_name=[{name=t_student_last_name, table_ref=S948}, {name=OBSERVATION_TM, table_ref=s948}, {name=t_student_last_name, table_ref=S949}, {name=OBSERVATION_TM, table_ref=S949}, {name=OBSERVATION_TM, table_ref=S948}]}, table_alias={s949=my.other5, s948=my.234}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void caseStatementParseTest() {

		final String query = " SELECT CASE WHEN true THEN 'Y'  WHEN false THEN 'N' "
				+ " ELSE 'N' END as case_one, CASE  col WHEN 'a' THEN 'b'	 " 
				+ " ELSE null END as case_two "
				+ " FROM sgbstdn ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=case_one, case={clauses={1={then={literal='Y'}, when={literal=true}}, 2={then={literal='N'}, when={literal=false}}}, else={literal='N'}}}, 2={alias=case_two, case={item={column={name=col, table_ref=null}}, clauses={1={then={literal='b'}, when={literal='a'}}}, else={null_literal=null}}}}, from={table={alias=null, table=sgbstdn}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[case_two, case_one]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{sgbstdn={col=[[@17,86:88='col',<459>,1:86]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={case_one=[[@14,70:77='case_one',<459>,1:70]], case_two=[[@26,127:134='case_two',<459>,1:127]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={case_two=[[@26,127:134='case_two',<459>,1:127]], case_one=[[@14,70:77='case_one',<459>,1:70]]}, table_dictionary={sgbstdn={col=[[@17,86:88='col',<459>,1:86]]}}, interface={case_two=[{name=col, table_ref=sgbstdn}], case_one=[]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void trimFunctionVariationsTest() {
		final String query = "SELECT trim(leading '0' from field1), trim('0' || field2,'0') "
				+ " FROM scbcrse";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=trim, parameters={qualifier=leading, trim_character={literal='0'}, value={column={name=field1, table_ref=null}}}}}, 2={function={parameters={1={concatenate={1={literal='0'}, 2={column={name=field2, table_ref=null}}}}, 2={literal='0'}}, function_name=trim}}}, from={table={alias=null, table=scbcrse}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_1, unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={field1=[[@6,29:34='field1',<459>,1:29]], field2=[[@13,50:55='field2',<459>,1:50]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_1=[[@16,60:60=')',<288>,1:60]], unnamed_0=[[@7,35:35=')',<288>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_1=[[@16,60:60=')',<288>,1:60]], unnamed_0=[[@7,35:35=')',<288>,1:35]]}, table_dictionary={scbcrse={field1=[[@6,29:34='field1',<459>,1:29]], field2=[[@13,50:55='field2',<459>,1:50]]}}, interface={unnamed_1=[{name=field2, table_ref=scbcrse}], unnamed_0=[{name=field1, table_ref=scbcrse}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void trimFunctionColumnSubstitutionsTest() {
		// Item 38 - Trim Functions recognize column and predicand variables
		final String query = "SELECT trim(leading '0' from a.<field1>), trim('0' || a.<field2>,'0') "
				+ " FROM scbcrse as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=trim, parameters={qualifier=leading, trim_character={literal='0'}, value={column={substitution={name=<field1>, type=column}, table_ref=a}}}}}, 2={function={parameters={1={concatenate={1={literal='0'}, 2={column={substitution={name=<field2>, type=column}, table_ref=a}}}}, 2={literal='0'}}, function_name=trim}}}, from={table={alias=a, table=scbcrse}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_1, unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<field2>=column, <field1>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={<field2>=[[@15,54:54='a',<459>,1:54]], <field1>=[[@6,29:29='a',<459>,1:29]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={unnamed_1=[[@20,68:68=')',<288>,1:68]], unnamed_0=[[@9,39:39=')',<288>,1:39]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		 "{def_query0={query_dictionary={unnamed_1=[[@20,68:68=')',<288>,1:68]], unnamed_0=[[@9,39:39=')',<288>,1:39]]}, table_dictionary={scbcrse={<field2>=[[@15,54:54='a',<459>,1:54]], <field1>=[[@6,29:29='a',<459>,1:29]]}}, interface={unnamed_1=[{substitution={name=<field2>, type=column}, table_ref=a}], unnamed_0=[{substitution={name=<field1>, type=column}, table_ref=a}]}, table_alias={a=scbcrse}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void trimFunctionPredicandSubstitutionsTest() {
		// Item 38 - Trim Functions recognize column and predicand variables
		final String query = "SELECT trim(leading '0' from <field1>), trim(<field2>,'0') "
				+ " FROM scbcrse as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=trim, parameters={qualifier=leading, trim_character={literal='0'}, value={substitution={name=<field1>, type=predicand}}}}}, 2={function={parameters={1={substitution={name=<field2>, type=predicand}}, 2={literal='0'}}, function_name=trim}}}, from={table={alias=a, table=scbcrse}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_1, unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<field2>=predicand, <field1>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={unnamed_1=[[@14,57:57=')',<288>,1:57]], unnamed_0=[[@7,37:37=')',<288>,1:37]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={unnamed_1=[[@14,57:57=')',<288>,1:57]], unnamed_0=[[@7,37:37=')',<288>,1:37]]}, table_dictionary={scbcrse={}}, interface={unnamed_1=[{name=<field2>, type=predicand}], unnamed_0=[{name=<field1>, type=predicand}]}, table_alias={a=scbcrse}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicAggregateQueryTest() {
		final String query = "SELECT apple, count(*) from tab1 group by apple";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}}}, from={table={alias=null, table=tab1}}, groupby={1={column={name=apple, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple, unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<459>,1:7], [@11,42:46='apple',<459>,1:42]], *=[[@5,20:20='*',<291>,1:20]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<459>,1:7]], unnamed_0=[[@6,21:21=')',<288>,1:21]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<459>,1:7]], unnamed_0=[[@6,21:21=')',<288>,1:21]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<459>,1:7], [@11,42:46='apple',<459>,1:42]], *=[[@5,20:20='*',<291>,1:20]]}}, grouped_by=[{name=apple, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}], unnamed_0=[]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicAggregateQueryWithColumnVariableTest() {
		// Item 37 - Group by recognizes Column variables
		// Note, this query is semantically incorrect because it does not include the same unaggregated columns in the select and group by
		final String query = "SELECT apple, count(*) from tab1 group by tab1.<other>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}}}, from={table={alias=null, table=tab1}}, groupby={1={column={substitution={name=<other>, type=column}, table_ref=tab1}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple, unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<other>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<459>,1:7]], <other>=[[@11,42:45='tab1',<459>,1:42]], *=[[@5,20:20='*',<291>,1:20]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<459>,1:7]], unnamed_0=[[@6,21:21=')',<288>,1:21]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<459>,1:7]], unnamed_0=[[@6,21:21=')',<288>,1:21]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<459>,1:7]], <other>=[[@11,42:45='tab1',<459>,1:42]], *=[[@5,20:20='*',<291>,1:20]]}}, grouped_by=[{substitution={name=<other>, type=column}, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}], unnamed_0=[]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicAggregateQueryWithMultiplePredicandsTest() {
		// Note, this query is semantically incorrect because it does not include the same unaggregated columns in the select and group by
		final String query = "SELECT apple, count(*) from tab1 group by tab1.<other>, <predicand>, (a+b*c)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}}}, from={table={alias=null, table=tab1}}, groupby={1={column={substitution={name=<other>, type=column}, table_ref=tab1}}, 2={substitution={name=<predicand>, type=predicand}}, 3={parentheses={calc={left={column={name=a, table_ref=null}}, right={calc={left={column={name=b, table_ref=null}}, right={column={name=c, table_ref=null}}, operator=*}}, operator=+}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple, unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<predicand>=predicand, <other>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<459>,1:7]], a=[[@18,70:70='a',<459>,1:70]], b=[[@20,72:72='b',<459>,1:72]], c=[[@22,74:74='c',<459>,1:74]], <other>=[[@11,42:45='tab1',<459>,1:42]], *=[[@5,20:20='*',<291>,1:20]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={apple=[[@1,7:11='apple',<459>,1:7]], unnamed_0=[[@6,21:21=')',<288>,1:21]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		 "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<459>,1:7]], unnamed_0=[[@6,21:21=')',<288>,1:21]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<459>,1:7]], a=[[@18,70:70='a',<459>,1:70]], b=[[@20,72:72='b',<459>,1:72]], c=[[@22,74:74='c',<459>,1:74]], <other>=[[@11,42:45='tab1',<459>,1:42]], *=[[@5,20:20='*',<291>,1:20]]}}, grouped_by=[{substitution={name=<other>, type=column}, table_ref=tab1}, {name=<predicand>, type=predicand}, {name=a, table_ref=null}, {name=b, table_ref=null}, {name=c, table_ref=null}], interface={apple=[{name=apple, table_ref=tab1}], unnamed_0=[]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicAggregateQueryWithPredicandVariableTest() {
		// Item 37 - Group by recognizes Predicand variables
		// Note, this query is semantically incorrect because it does not include the same unaggregated columns in the select and group by
		final String query = "SELECT apple, count(*) from tab1 group by <other>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}}}, from={table={alias=null, table=tab1}}, groupby={1={substitution={name=<other>, type=predicand}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple, unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<other>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<459>,1:7]], *=[[@5,20:20='*',<291>,1:20]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<459>,1:7]], unnamed_0=[[@6,21:21=')',<288>,1:21]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<459>,1:7]], unnamed_0=[[@6,21:21=')',<288>,1:21]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<459>,1:7]], *=[[@5,20:20='*',<291>,1:20]]}}, grouped_by=[{name=<other>, type=predicand}], interface={apple=[{name=apple, table_ref=tab1}], unnamed_0=[]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicAggregateQueryWithCountOverCalcTest() {
		// ITEM 67 - Count function over calculation
		final String query = "SELECT apple, count(subj + object) from tab1 group by apple";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}, 2={function={function_name=count, qualifier=null, parameters={calc={left={column={name=subj, table_ref=null}}, right={column={name=object, table_ref=null}}, operator=+}}}}}, from={table={alias=null, table=tab1}}, groupby={1={column={name=apple, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple, unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<459>,1:7], [@13,54:58='apple',<459>,1:54]], subj=[[@5,20:23='subj',<459>,1:20]], object=[[@7,27:32='object',<268>,1:27]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<459>,1:7]], unnamed_0=[[@8,33:33=')',<288>,1:33]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<459>,1:7]], unnamed_0=[[@8,33:33=')',<288>,1:33]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<459>,1:7], [@13,54:58='apple',<459>,1:54]], subj=[[@5,20:23='subj',<459>,1:20]], object=[[@7,27:32='object',<268>,1:27]]}}, grouped_by=[{name=apple, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}], unnamed_0=[{name=subj, table_ref=tab1}, {name=object, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicAggregateQueryWithCountOverColumnVariableTest() {
		// Item 39 - Count function over Column variables
		final String query = "SELECT apple, count(tab1.<other>) from tab1 group by apple";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}, 2={function={function_name=count, qualifier=null, parameters={column={substitution={name=<other>, type=column}, table_ref=tab1}}}}}, from={table={alias=null, table=tab1}}, groupby={1={column={name=apple, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple, unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<other>=column}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<459>,1:7], [@13,53:57='apple',<459>,1:53]], <other>=[[@5,20:23='tab1',<459>,1:20]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<459>,1:7]], unnamed_0=[[@8,32:32=')',<288>,1:32]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<459>,1:7]], unnamed_0=[[@8,32:32=')',<288>,1:32]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<459>,1:7], [@13,53:57='apple',<459>,1:53]], <other>=[[@5,20:23='tab1',<459>,1:20]]}}, grouped_by=[{name=apple, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}], unnamed_0=[{substitution={name=<other>, type=column}, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void basicAggregateQueryWithCountOverPredicandVariableTest() {
		// Item 39 - Count function over Predicand variables
		final String query = "SELECT apple, count(<other>) from tab1 group by apple";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=apple, table_ref=null}}, 2={function={function_name=count, qualifier=null, parameters={substitution={name=<other>, type=predicand}}}}}, from={table={alias=null, table=tab1}}, groupby={1={column={name=apple, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[apple, unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<other>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<459>,1:7], [@11,48:52='apple',<459>,1:48]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<459>,1:7]], unnamed_0=[[@6,27:27=')',<288>,1:27]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={apple=[[@1,7:11='apple',<459>,1:7]], unnamed_0=[[@6,27:27=')',<288>,1:27]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<459>,1:7], [@11,48:52='apple',<459>,1:48]]}}, grouped_by=[{name=apple, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}], unnamed_0=[{name=<other>, type=predicand}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void snowflakeAggregate_ANY_VALUE_QueryTest() {
		final String query = "SELECT ANY_VALUE(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=ANY_VALUE, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_CORR_QueryTest() {
		final String query = "SELECT CORR(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=CORR, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_COVAR_POP_QueryTest() {
		final String query = "SELECT COVAR_POP(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=COVAR_POP, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_COVAR_SAMP_QueryTest() {
		final String query = "SELECT COVAR_SAMP(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=COVAR_SAMP, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_LISTAGG_QueryTest() {
		final String query = "SELECT LISTAGG(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=LISTAGG, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_MEDIAN_QueryTest() {
		final String query = "SELECT MEDIAN(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=MEDIAN, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_PERCENTILE_CONT_QueryTest() {
		final String query = "SELECT PERCENTILE_CONT(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=PERCENTILE_CONT, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_PERCENTILE_DISC_QueryTest() {
		final String query = "SELECT PERCENTILE_DISC(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=PERCENTILE_DISC, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_STDDEV_QueryTest() {
		final String query = "SELECT STDDEV(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=STDDEV, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_VARIANCE_POP_QueryTest() {
		final String query = "SELECT VARIANCE_POP(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=VARIANCE_POP, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_VARIANCE_QueryTest() {
		final String query = "SELECT VARIANCE(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=VARIANCE, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_VARIANCE_SAMP_QueryTest() {
		final String query = "SELECT VARIANCE_SAMP(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=VARIANCE_SAMP, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_CUME_DIST_QueryTest() {
		final String query = "SELECT CUME_DIST(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=CUME_DIST, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_DENSE_RANK_QueryTest() {
		final String query = "SELECT DENSE_RANK(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=DENSE_RANK, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_NTILE_QueryTest() {
		final String query = "SELECT NTILE(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=NTILE, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_PERCENT_RANK_QueryTest() {
		final String query = "SELECT PERCENT_RANK(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=PERCENT_RANK, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_WIDTH_BUCKET_QueryTest() {
		final String query = "SELECT WIDTH_BUCKET(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=WIDTH_BUCKET, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_BITAND_AGG_QueryTest() {
		final String query = "SELECT BITAND_AGG(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=BITAND_AGG, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_BITOR_AGG_QueryTest() {
		final String query = "SELECT BITOR_AGG(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=BITOR_AGG, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_BITXOR_AGG_QueryTest() {
		final String query = "SELECT BITXOR_AGG(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=BITXOR_AGG, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_HASH_AGG_QueryTest() {
		final String query = "SELECT HASH_AGG(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=HASH_AGG, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_ARRAY_AGG_QueryTest() {
		final String query = "SELECT ARRAY_AGG(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=ARRAY_AGG, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_OBJECT_AGG_QueryTest() {
		final String query = "SELECT OBJECT_AGG(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=OBJECT_AGG, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_REGR_AVGX_QueryTest() {
		final String query = "SELECT REGR_AVGX(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=REGR_AVGX, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_REGR_AVGY_QueryTest() {
		final String query = "SELECT REGR_AVGY(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=REGR_AVGY, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_REGR_COUNT_QueryTest() {
		final String query = "SELECT REGR_COUNT(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=REGR_COUNT, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_REGR_INTERCEPT_QueryTest() {
		final String query = "SELECT REGR_INTERCEPT(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=REGR_INTERCEPT, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_REGR_R2_QueryTest() {
		final String query = "SELECT REGR_R2(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=REGR_R2, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_REGR_SLOPE_QueryTest() {
		final String query = "SELECT REGR_SLOPE(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=REGR_SLOPE, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_REGR_SXX_QueryTest() {
		final String query = "SELECT REGR_SXX(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=REGR_SXX, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_REGR_SXY_QueryTest() {
		final String query = "SELECT REGR_SXY(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=REGR_SXY, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_REGR_SYY_QueryTest() {
		final String query = "SELECT REGR_SYY(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=REGR_SYY, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_APPROX_COUNT_DISTINCT_QueryTest() {
		final String query = "SELECT APPROX_COUNT_DISTINCT(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=APPROX_COUNT_DISTINCT, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_HLL_QueryTest() {
		final String query = "SELECT HLL(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=HLL, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_HLL_ACCUMULATE_QueryTest() {
		final String query = "SELECT HLL_ACCUMULATE(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=HLL_ACCUMULATE, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_HLL_COMBINE_QueryTest() {
		final String query = "SELECT HLL_COMBINE(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=HLL_COMBINE, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_HLL_EXPORT_QueryTest() {
		final String query = "SELECT HLL_EXPORT(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=HLL_EXPORT, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_HLL_IMPORT_QueryTest() {
		final String query = "SELECT HLL_IMPORT(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=HLL_IMPORT, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_APPROXIMATE_JACCARD_INDEX_QueryTest() {
		final String query = "SELECT APPROXIMATE_JACCARD_INDEX(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=APPROXIMATE_JACCARD_INDEX, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_APPROXIMATE_SIMILARITY_QueryTest() {
		final String query = "SELECT APPROXIMATE_SIMILARITY(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=APPROXIMATE_SIMILARITY, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_MINHASH_QueryTest() {
		final String query = "SELECT MINHASH(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=MINHASH, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_MINHASH_COMBINE_QueryTest() {
		final String query = "SELECT MINHASH_COMBINE(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=MINHASH_COMBINE, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_APPROX_TOP_K_QueryTest() {
		final String query = "SELECT APPROX_TOP_K(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=APPROX_TOP_K, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_APPROX_TOP_K_ACCUMULATE_QueryTest() {
		final String query = "SELECT APPROX_TOP_K_ACCUMULATE(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=APPROX_TOP_K_ACCUMULATE, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_APPROX_TOP_K_COMBINE_QueryTest() {
		final String query = "SELECT APPROX_TOP_K_COMBINE(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=APPROX_TOP_K_COMBINE, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_APPROX_PERCENTILE_QueryTest() {
		final String query = "SELECT APPROX_PERCENTILE(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=APPROX_PERCENTILE, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_APPROX_PERCENTILE_ACCUMULATE_QueryTest() {
		final String query = "SELECT APPROX_PERCENTILE_ACCUMULATE(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=APPROX_PERCENTILE_ACCUMULATE, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_APPROX_PERCENTILE_COMBINE_QueryTest() {
		final String query = "SELECT APPROX_PERCENTILE_COMBINE(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=APPROX_PERCENTILE_COMBINE, qualifier=null, parameters={column={name=col1, table_ref=null}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_GROUPING_SingleParameterQueryTest() {
		final String query = "SELECT GROUPING(col1) from tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={parameters={1={column={name=col1, table_ref=null}}}, function_name=GROUPING}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void snowflakeAggregate_GROUPING_MultipleParameterQueryTest() {
		final String query = "SELECT col, GROUPING(col1, col2) from tab1 group by col";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col, table_ref=null}}, 2={function={parameters={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, function_name=GROUPING}}}, from={table={alias=null, table=tab1}}, groupby={1={column={name=col, table_ref=null}}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void leadOverPartitionTest() {
		// Item 26 - Window function property "spriden_id" appearing in Interface improperly;
		final String query = "SELECT func(item), lead(code,1) over (partition by spriden_id order by code)"
				+ " from tab1 ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={parameters={1={column={name=item, table_ref=null}}}, function_name=func}}, 2={window_function={over={partition_by={1={column={name=spriden_id, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=code, table_ref=null}}, sort_order=ASC}}}, function={function_name=lead, parameters={1={column={name=code, table_ref=null}}, 2={literal=1}}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_1, unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={item=[[@3,12:15='item',<459>,1:12]], code=[[@8,24:27='code',<459>,1:24], [@19,71:74='code',<459>,1:71]], spriden_id=[[@16,51:60='spriden_id',<459>,1:51]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_1=[[@20,75:75=')',<288>,1:75]], unnamed_0=[[@4,16:16=')',<288>,1:16]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=code, table_ref=null}], query_dictionary={unnamed_1=[[@20,75:75=')',<288>,1:75]], unnamed_0=[[@4,16:16=')',<288>,1:16]]}, table_dictionary={tab1={item=[[@3,12:15='item',<459>,1:12]], code=[[@8,24:27='code',<459>,1:24], [@19,71:74='code',<459>,1:71]], spriden_id=[[@16,51:60='spriden_id',<459>,1:51]]}}, window_partition_by=[{name=spriden_id, table_ref=null}], interface={unnamed_1=[{name=spriden_id, table_ref=tab1}, {name=code, table_ref=tab1}], unnamed_0=[{name=item, table_ref=tab1}]}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void rankPartitionSyntaxTest() {
		final String query = " SELECT "
				+ " rank() OVER (partition by k_stfd, kppi order by OBSERVATION_TM desc, row_num desc) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={partition_by={1={column={name=k_stfd, table_ref=null}}, 2={column={name=kppi, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}, 2={null_order=null, predicand={column={name=row_num, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters=null}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[key_rank]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={k_stfd=[[@8,35:40='k_stfd',<459>,1:35]], row_num=[[@16,78:84='row_num',<459>,1:78]], kppi=[[@10,43:46='kppi',<459>,1:43]], OBSERVATION_TM=[[@13,57:70='OBSERVATION_TM',<459>,1:57]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={key_rank=[[@20,95:102='key_rank',<459>,1:95]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=OBSERVATION_TM, table_ref=null}, {name=row_num, table_ref=null}], query_dictionary={key_rank=[[@20,95:102='key_rank',<459>,1:95]]}, table_dictionary={tab1={k_stfd=[[@8,35:40='k_stfd',<459>,1:35]], row_num=[[@16,78:84='row_num',<459>,1:78]], kppi=[[@10,43:46='kppi',<459>,1:43]], OBSERVATION_TM=[[@13,57:70='OBSERVATION_TM',<459>,1:57]]}}, window_partition_by=[{name=k_stfd, table_ref=null}, {name=kppi, table_ref=null}], interface={key_rank=[{name=k_stfd, table_ref=tab1}, {name=kppi, table_ref=tab1}, {name=OBSERVATION_TM, table_ref=tab1}, {name=row_num, table_ref=tab1}]}, table_alias={a=tab1}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void rankWithParameterPartitionSyntaxTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd, kppi order by OBSERVATION_TM desc, row_num desc) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={partition_by={1={column={name=k_stfd, table_ref=null}}, 2={column={name=kppi, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}, 2={null_order=null, predicand={column={name=row_num, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[key_rank]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={k_stfd=[[@9,39:44='k_stfd',<459>,1:39]], parm=[[@3,14:17='parm',<459>,1:14]], row_num=[[@17,82:88='row_num',<459>,1:82]], kppi=[[@11,47:50='kppi',<459>,1:47]], OBSERVATION_TM=[[@14,61:74='OBSERVATION_TM',<459>,1:61]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={key_rank=[[@21,99:106='key_rank',<459>,1:99]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=OBSERVATION_TM, table_ref=null}, {name=row_num, table_ref=null}], query_dictionary={key_rank=[[@21,99:106='key_rank',<459>,1:99]]}, table_dictionary={tab1={k_stfd=[[@9,39:44='k_stfd',<459>,1:39]], parm=[[@3,14:17='parm',<459>,1:14]], row_num=[[@17,82:88='row_num',<459>,1:82]], kppi=[[@11,47:50='kppi',<459>,1:47]], OBSERVATION_TM=[[@14,61:74='OBSERVATION_TM',<459>,1:61]]}}, window_partition_by=[{name=k_stfd, table_ref=null}, {name=kppi, table_ref=null}], interface={key_rank=[{name=k_stfd, table_ref=tab1}, {name=kppi, table_ref=tab1}, {name=OBSERVATION_TM, table_ref=tab1}, {name=row_num, table_ref=tab1}, {name=parm, table_ref=tab1}]}, table_alias={a=tab1}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void selectPartitionDownfillTest() {
		String query = " SELECT  "
				+ "   first_value(major_cd) over (partition by student_id, value_partition order by term_row) as major_cd_fill "
				+ " , first_value(college_cd) over (partition by student_id, value_partition order by term_row) as college_cd_fill "
				+ " , first_value(degree_cd) over (partition by student_id, value_partition order by term_row) as degree_cd_fill "
				+ " , first_value(concentration_cd) over (partition by student_id, value_partition order by term_row) as concentration_cd_fill "
				+ " , first_value(major_cd_2) over (partition by student_id, value_partition order by term_row) as major_cd_2_fill "
				+ " , first_value(college_cd_2) over (partition by student_id, value_partition order by term_row) as college_cd_2_fill "
				+ " , first_value(degree_cd_2) over (partition by student_id, value_partition order by term_row) as degree_cd_2_fill "
				+ " , first_value(concentration_cd_2) over (partition by student_id, value_partition order by term_row) as concentration_cd_2_fill "
				+ " FROM student_term_major where major_cd is null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("Interface is wrong", "[degree_cd_2_fill, concentration_cd_fill, college_cd_fill, major_cd_2_fill, college_cd_2_fill, degree_cd_fill, concentration_cd_2_fill, major_cd_fill]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={degree_cd_2_fill=[[@125,788:803='degree_cd_2_fill',<459>,1:788]], concentration_cd_fill=[[@71,441:461='concentration_cd_fill',<459>,1:441]], college_cd_fill=[[@35,213:227='college_cd_fill',<459>,1:213]], major_cd_2_fill=[[@89,559:573='major_cd_2_fill',<459>,1:559]], college_cd_2_fill=[[@107,673:689='college_cd_2_fill',<459>,1:673]], degree_cd_fill=[[@53,324:337='degree_cd_fill',<459>,1:324]], concentration_cd_2_fill=[[@143,909:931='concentration_cd_2_fill',<459>,1:909]], major_cd_fill=[[@17,103:115='major_cd_fill',<459>,1:103]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
	}


	@Test
	public void lagWindowIgnoreNullsTest() {
		String query = " SELECT  "
				+ "   lag(major_cd) ignore nulls over (partition by student_id, value_partition order by term_row) as major_cd_fill "
				+ " FROM student_term_major where major_cd is null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=major_cd_fill, window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=ignore, function_name=lag, parameters={1={column={name=major_cd, table_ref=null}}}}}}}, from={table={alias=null, table=student_term_major}}, where={condition={left={column={name=major_cd, table_ref=null}}, operator=is null}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[major_cd_fill]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={value_partition=[[@13,70:84='value_partition',<459>,1:70]], term_row=[[@16,95:102='term_row',<459>,1:95]], major_cd=[[@3,16:23='major_cd',<459>,1:16], [@23,153:160='major_cd',<459>,1:153]], student_id=[[@11,58:67='student_id',<459>,1:58]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={major_cd_fill=[[@19,108:120='major_cd_fill',<459>,1:108]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=term_row, table_ref=null}], query_dictionary={major_cd_fill=[[@19,108:120='major_cd_fill',<459>,1:108]]}, table_dictionary={student_term_major={value_partition=[[@13,70:84='value_partition',<459>,1:70]], term_row=[[@16,95:102='term_row',<459>,1:95]], major_cd=[[@3,16:23='major_cd',<459>,1:16], [@23,153:160='major_cd',<459>,1:153]], student_id=[[@11,58:67='student_id',<459>,1:58]]}}, window_partition_by=[{name=student_id, table_ref=null}, {name=value_partition, table_ref=null}], filters=[{name=major_cd, table_ref=student_term_major}], interface={major_cd_fill=[{name=student_id, table_ref=student_term_major}, {name=value_partition, table_ref=student_term_major}, {name=term_row, table_ref=student_term_major}, {name=major_cd, table_ref=student_term_major}]}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void leadWindowIgnoreNullsTest() {
		String query = " SELECT  "
				+ "   lead(major_cd) ignore nulls over (partition by student_id, value_partition order by term_row) as major_cd_fill "
				+ " FROM student_term_major where major_cd is null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=major_cd_fill, window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=ignore, function_name=lead, parameters={1={column={name=major_cd, table_ref=null}}}}}}}, from={table={alias=null, table=student_term_major}}, where={condition={left={column={name=major_cd, table_ref=null}}, operator=is null}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[major_cd_fill]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={value_partition=[[@13,71:85='value_partition',<459>,1:71]], term_row=[[@16,96:103='term_row',<459>,1:96]], major_cd=[[@3,17:24='major_cd',<459>,1:17], [@23,154:161='major_cd',<459>,1:154]], student_id=[[@11,59:68='student_id',<459>,1:59]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={major_cd_fill=[[@19,109:121='major_cd_fill',<459>,1:109]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=term_row, table_ref=null}], query_dictionary={major_cd_fill=[[@19,109:121='major_cd_fill',<459>,1:109]]}, table_dictionary={student_term_major={value_partition=[[@13,71:85='value_partition',<459>,1:71]], term_row=[[@16,96:103='term_row',<459>,1:96]], major_cd=[[@3,17:24='major_cd',<459>,1:17], [@23,154:161='major_cd',<459>,1:154]], student_id=[[@11,59:68='student_id',<459>,1:59]]}}, window_partition_by=[{name=student_id, table_ref=null}, {name=value_partition, table_ref=null}], filters=[{name=major_cd, table_ref=student_term_major}], interface={major_cd_fill=[{name=student_id, table_ref=student_term_major}, {name=value_partition, table_ref=student_term_major}, {name=term_row, table_ref=student_term_major}, {name=major_cd, table_ref=student_term_major}]}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void lastValueWindowIgnoreNullsTest() {
		String query = " SELECT  "
				+ "   last_value(major_cd) ignore nulls over (partition by student_id, value_partition order by term_row) as major_cd_fill "
				+ " FROM student_term_major where major_cd is null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=major_cd_fill, window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=ignore, function_name=last_value, parameters={1={column={name=major_cd, table_ref=null}}}}}}}, from={table={alias=null, table=student_term_major}}, where={condition={left={column={name=major_cd, table_ref=null}}, operator=is null}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[major_cd_fill]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={value_partition=[[@13,77:91='value_partition',<459>,1:77]], term_row=[[@16,102:109='term_row',<459>,1:102]], major_cd=[[@3,23:30='major_cd',<459>,1:23], [@23,160:167='major_cd',<459>,1:160]], student_id=[[@11,65:74='student_id',<459>,1:65]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={major_cd_fill=[[@19,115:127='major_cd_fill',<459>,1:115]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=term_row, table_ref=null}], query_dictionary={major_cd_fill=[[@19,115:127='major_cd_fill',<459>,1:115]]}, table_dictionary={student_term_major={value_partition=[[@13,77:91='value_partition',<459>,1:77]], term_row=[[@16,102:109='term_row',<459>,1:102]], major_cd=[[@3,23:30='major_cd',<459>,1:23], [@23,160:167='major_cd',<459>,1:160]], student_id=[[@11,65:74='student_id',<459>,1:65]]}}, window_partition_by=[{name=student_id, table_ref=null}, {name=value_partition, table_ref=null}], filters=[{name=major_cd, table_ref=student_term_major}], interface={major_cd_fill=[{name=student_id, table_ref=student_term_major}, {name=value_partition, table_ref=student_term_major}, {name=term_row, table_ref=student_term_major}, {name=major_cd, table_ref=student_term_major}]}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void lastValueWindowRespectNullsTest() {
		String query = " SELECT  "
				+ "   last_value(major_cd) respect nulls over (partition by student_id, value_partition order by term_row) as major_cd_fill "
				+ " FROM student_term_major where major_cd is null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=major_cd_fill, window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=respect, function_name=last_value, parameters={1={column={name=major_cd, table_ref=null}}}}}}}, from={table={alias=null, table=student_term_major}}, where={condition={left={column={name=major_cd, table_ref=null}}, operator=is null}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[major_cd_fill]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={value_partition=[[@13,78:92='value_partition',<459>,1:78]], term_row=[[@16,103:110='term_row',<459>,1:103]], major_cd=[[@3,23:30='major_cd',<459>,1:23], [@23,161:168='major_cd',<459>,1:161]], student_id=[[@11,66:75='student_id',<459>,1:66]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={major_cd_fill=[[@19,116:128='major_cd_fill',<459>,1:116]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=term_row, table_ref=null}], query_dictionary={major_cd_fill=[[@19,116:128='major_cd_fill',<459>,1:116]]}, table_dictionary={student_term_major={value_partition=[[@13,78:92='value_partition',<459>,1:78]], term_row=[[@16,103:110='term_row',<459>,1:103]], major_cd=[[@3,23:30='major_cd',<459>,1:23], [@23,161:168='major_cd',<459>,1:161]], student_id=[[@11,66:75='student_id',<459>,1:66]]}}, window_partition_by=[{name=student_id, table_ref=null}, {name=value_partition, table_ref=null}], filters=[{name=major_cd, table_ref=student_term_major}], interface={major_cd_fill=[{name=student_id, table_ref=student_term_major}, {name=value_partition, table_ref=student_term_major}, {name=term_row, table_ref=student_term_major}, {name=major_cd, table_ref=student_term_major}]}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void firstValueWindowIgnoreNullsTest() {
		String query = " SELECT  "
				+ "   first_value(major_cd) ignore nulls over (partition by student_id, value_partition order by term_row) as major_cd_fill "
				+ " FROM student_term_major where major_cd is null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=major_cd_fill, window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=ignore, function_name=first_value, parameters={1={column={name=major_cd, table_ref=null}}}}}}}, from={table={alias=null, table=student_term_major}}, where={condition={left={column={name=major_cd, table_ref=null}}, operator=is null}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[major_cd_fill]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={value_partition=[[@13,78:92='value_partition',<459>,1:78]], term_row=[[@16,103:110='term_row',<459>,1:103]], major_cd=[[@3,24:31='major_cd',<459>,1:24], [@23,161:168='major_cd',<459>,1:161]], student_id=[[@11,66:75='student_id',<459>,1:66]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={major_cd_fill=[[@19,116:128='major_cd_fill',<459>,1:116]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=term_row, table_ref=null}], query_dictionary={major_cd_fill=[[@19,116:128='major_cd_fill',<459>,1:116]]}, table_dictionary={student_term_major={value_partition=[[@13,78:92='value_partition',<459>,1:78]], term_row=[[@16,103:110='term_row',<459>,1:103]], major_cd=[[@3,24:31='major_cd',<459>,1:24], [@23,161:168='major_cd',<459>,1:161]], student_id=[[@11,66:75='student_id',<459>,1:66]]}}, window_partition_by=[{name=student_id, table_ref=null}, {name=value_partition, table_ref=null}], filters=[{name=major_cd, table_ref=student_term_major}], interface={major_cd_fill=[{name=student_id, table_ref=student_term_major}, {name=value_partition, table_ref=student_term_major}, {name=term_row, table_ref=student_term_major}, {name=major_cd, table_ref=student_term_major}]}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void firstValueWindowRespectNullsTest() {
		String query = " SELECT  "
				+ "   first_value(major_cd) respect nulls over (partition by student_id, value_partition order by term_row) as major_cd_fill "
				+ " FROM student_term_major where major_cd is null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=major_cd_fill, window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=respect, function_name=first_value, parameters={1={column={name=major_cd, table_ref=null}}}}}}}, from={table={alias=null, table=student_term_major}}, where={condition={left={column={name=major_cd, table_ref=null}}, operator=is null}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[major_cd_fill]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={value_partition=[[@13,79:93='value_partition',<459>,1:79]], term_row=[[@16,104:111='term_row',<459>,1:104]], major_cd=[[@3,24:31='major_cd',<459>,1:24], [@23,162:169='major_cd',<459>,1:162]], student_id=[[@11,67:76='student_id',<459>,1:67]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={major_cd_fill=[[@19,117:129='major_cd_fill',<459>,1:117]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=term_row, table_ref=null}], query_dictionary={major_cd_fill=[[@19,117:129='major_cd_fill',<459>,1:117]]}, table_dictionary={student_term_major={value_partition=[[@13,79:93='value_partition',<459>,1:79]], term_row=[[@16,104:111='term_row',<459>,1:104]], major_cd=[[@3,24:31='major_cd',<459>,1:24], [@23,162:169='major_cd',<459>,1:162]], student_id=[[@11,67:76='student_id',<459>,1:67]]}}, window_partition_by=[{name=student_id, table_ref=null}, {name=value_partition, table_ref=null}], filters=[{name=major_cd, table_ref=student_term_major}], interface={major_cd_fill=[{name=student_id, table_ref=student_term_major}, {name=value_partition, table_ref=student_term_major}, {name=term_row, table_ref=student_term_major}, {name=major_cd, table_ref=student_term_major}]}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void nthValueWindowIgnoreNullsTest() {
		String query = " SELECT  "
				+ "   nth_value(major_cd, 2) ignore nulls over (partition by student_id, value_partition order by term_row) as major_cd_fill "
				+ " FROM student_term_major where major_cd is null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=major_cd_fill, window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=ignore, function_name=nth_value, parameters={1={column={name=major_cd, table_ref=null}}, 2={literal=2}}}}}}, from={table={alias=null, table=student_term_major}}, where={condition={left={column={name=major_cd, table_ref=null}}, operator=is null}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[major_cd_fill]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={value_partition=[[@15,79:93='value_partition',<459>,1:79]], term_row=[[@18,104:111='term_row',<459>,1:104]], major_cd=[[@3,22:29='major_cd',<459>,1:22], [@25,162:169='major_cd',<459>,1:162]], student_id=[[@13,67:76='student_id',<459>,1:67]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={major_cd_fill=[[@21,117:129='major_cd_fill',<459>,1:117]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=term_row, table_ref=null}], query_dictionary={major_cd_fill=[[@21,117:129='major_cd_fill',<459>,1:117]]}, table_dictionary={student_term_major={value_partition=[[@15,79:93='value_partition',<459>,1:79]], term_row=[[@18,104:111='term_row',<459>,1:104]], major_cd=[[@3,22:29='major_cd',<459>,1:22], [@25,162:169='major_cd',<459>,1:162]], student_id=[[@13,67:76='student_id',<459>,1:67]]}}, window_partition_by=[{name=student_id, table_ref=null}, {name=value_partition, table_ref=null}], filters=[{name=major_cd, table_ref=student_term_major}], interface={major_cd_fill=[{name=student_id, table_ref=student_term_major}, {name=value_partition, table_ref=student_term_major}, {name=term_row, table_ref=student_term_major}, {name=major_cd, table_ref=student_term_major}]}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void nthValueWindowRespectNullsTest() {
		String query = " SELECT  "
				+ "   nth_value(major_cd, 2) respect nulls over (partition by student_id, value_partition order by term_row) as major_cd_fill "
				+ " FROM student_term_major where major_cd is null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=major_cd_fill, window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=respect, function_name=nth_value, parameters={1={column={name=major_cd, table_ref=null}}, 2={literal=2}}}}}}, from={table={alias=null, table=student_term_major}}, where={condition={left={column={name=major_cd, table_ref=null}}, operator=is null}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[major_cd_fill]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={value_partition=[[@15,80:94='value_partition',<459>,1:80]], term_row=[[@18,105:112='term_row',<459>,1:105]], major_cd=[[@3,22:29='major_cd',<459>,1:22], [@25,163:170='major_cd',<459>,1:163]], student_id=[[@13,68:77='student_id',<459>,1:68]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={major_cd_fill=[[@21,118:130='major_cd_fill',<459>,1:118]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=term_row, table_ref=null}], query_dictionary={major_cd_fill=[[@21,118:130='major_cd_fill',<459>,1:118]]}, table_dictionary={student_term_major={value_partition=[[@15,80:94='value_partition',<459>,1:80]], term_row=[[@18,105:112='term_row',<459>,1:105]], major_cd=[[@3,22:29='major_cd',<459>,1:22], [@25,163:170='major_cd',<459>,1:163]], student_id=[[@13,68:77='student_id',<459>,1:68]]}}, window_partition_by=[{name=student_id, table_ref=null}, {name=value_partition, table_ref=null}], filters=[{name=major_cd, table_ref=student_term_major}], interface={major_cd_fill=[{name=student_id, table_ref=student_term_major}, {name=value_partition, table_ref=student_term_major}, {name=term_row, table_ref=student_term_major}, {name=major_cd, table_ref=student_term_major}]}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void nthValueWindowIgnoreNullsFromFirstTest() {
		String query = " SELECT  "
				+ "   nth_value(major_cd, 2) from first ignore nulls over (partition by student_id, value_partition order by term_row) as major_cd_fill "
				+ " FROM student_term_major where major_cd is null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=major_cd_fill, window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=ignore, function_name=nth_value, select_from=first, parameters={1={column={name=major_cd, table_ref=null}}, 2={literal=2}}}}}}, from={table={alias=null, table=student_term_major}}, where={condition={left={column={name=major_cd, table_ref=null}}, operator=is null}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[major_cd_fill]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={value_partition=[[@17,90:104='value_partition',<459>,1:90]], term_row=[[@20,115:122='term_row',<459>,1:115]], major_cd=[[@3,22:29='major_cd',<459>,1:22], [@27,173:180='major_cd',<459>,1:173]], student_id=[[@15,78:87='student_id',<459>,1:78]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={major_cd_fill=[[@23,128:140='major_cd_fill',<459>,1:128]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=term_row, table_ref=null}], query_dictionary={major_cd_fill=[[@23,128:140='major_cd_fill',<459>,1:128]]}, table_dictionary={student_term_major={value_partition=[[@17,90:104='value_partition',<459>,1:90]], term_row=[[@20,115:122='term_row',<459>,1:115]], major_cd=[[@3,22:29='major_cd',<459>,1:22], [@27,173:180='major_cd',<459>,1:173]], student_id=[[@15,78:87='student_id',<459>,1:78]]}}, window_partition_by=[{name=student_id, table_ref=null}, {name=value_partition, table_ref=null}], filters=[{name=major_cd, table_ref=student_term_major}], interface={major_cd_fill=[{name=student_id, table_ref=student_term_major}, {name=value_partition, table_ref=student_term_major}, {name=term_row, table_ref=student_term_major}, {name=major_cd, table_ref=student_term_major}]}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void nthValueWindowIgnoreNullsFromLastTest() {
		String query = " SELECT  "
				+ "   nth_value(major_cd, 2) from last ignore nulls over (partition by student_id, value_partition order by term_row) as major_cd_fill "
				+ " FROM student_term_major where major_cd is null";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=major_cd_fill, window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={null_handle=ignore, function_name=nth_value, select_from=last, parameters={1={column={name=major_cd, table_ref=null}}, 2={literal=2}}}}}}, from={table={alias=null, table=student_term_major}}, where={condition={left={column={name=major_cd, table_ref=null}}, operator=is null}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[major_cd_fill]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={value_partition=[[@17,89:103='value_partition',<459>,1:89]], term_row=[[@20,114:121='term_row',<459>,1:114]], major_cd=[[@3,22:29='major_cd',<459>,1:22], [@27,172:179='major_cd',<459>,1:172]], student_id=[[@15,77:86='student_id',<459>,1:77]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={major_cd_fill=[[@23,127:139='major_cd_fill',<459>,1:127]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=term_row, table_ref=null}], query_dictionary={major_cd_fill=[[@23,127:139='major_cd_fill',<459>,1:127]]}, table_dictionary={student_term_major={value_partition=[[@17,89:103='value_partition',<459>,1:89]], term_row=[[@20,114:121='term_row',<459>,1:114]], major_cd=[[@3,22:29='major_cd',<459>,1:22], [@27,172:179='major_cd',<459>,1:172]], student_id=[[@15,77:86='student_id',<459>,1:77]]}}, window_partition_by=[{name=student_id, table_ref=null}, {name=value_partition, table_ref=null}], filters=[{name=major_cd, table_ref=student_term_major}], interface={major_cd_fill=[{name=student_id, table_ref=student_term_major}, {name=value_partition, table_ref=student_term_major}, {name=term_row, table_ref=student_term_major}, {name=major_cd, table_ref=student_term_major}]}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void sqlModeExcludesPredicandSubstitutionsFromTableDictionaryRegressionTest() {
		// Regression contract: in SQL endpoint mode, predicand substitutions must remain
		// substitutions only and must not be materialized into unresolved/table dictionary columns.
		String query = " SELECT  "
				+ "   count_if(<expression1> = 'Y') over (partition by <expression2> order by <expression3> asc rows between unbounded preceding and current row) "
				+ " FROM dual";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={window_function={over={bracket={type=rows, between={end={value=CURRENT ROW}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={substitution={name=<expression2>, type=predicand}}}, orderby={1={null_order=null, predicand={substitution={name=<expression3>, type=predicand}}, sort_order=asc}}}, function={function_name=count_if, parameters={1={condition={left={substitution={name=<expression1>, type=predicand}}, right={literal='Y'}, operator==}}}}}}}, from={table={alias=null, table=dual}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unnamed_0]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<expression2>=predicand, <expression1>=predicand, <expression3>=predicand}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{dual={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@23,150:150=')',<288>,1:150]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=<expression3>, type=predicand}], query_dictionary={unnamed_0=[[@23,150:150=')',<288>,1:150]]}, table_dictionary={dual={}}, window_partition_by=[{name=<expression2>, type=predicand}], interface={unnamed_0=[{name=<expression2>, type=predicand}, {name=<expression3>, type=predicand}, {name=<expression1>, type=predicand}]}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void caseWhenParenthesizedConditionSubstitutionTest() {
		final String sql = "select case when (<column1>) then 'Y' else 'N' end from tab1";
		final SQLSelectParserParser parser = parse(sql);
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={case={clauses={1={then={literal='Y'}, when={parentheses={substitution={name=<column1>, type=condition}}}}}, else={literal='N'}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<column1>=condition}",
				extractor.getSubstitutionsMap().toString());
	}


	@Test
	public void windowFunctionArgParenthesizedPredicandTest() {
		final String query = "SELECT rank((<columnParam>)) OVER (partition by k_stfd, kppi order by row_num desc) from tab1";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={window_function={over={partition_by={1={column={name=k_stfd, table_ref=null}}, 2={column={name=kppi, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=row_num, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={parentheses={substitution={name=<columnParam>, type=predicand}}}}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<columnParam>=predicand}",
				extractor.getSubstitutionsMap().toString());
	}


	@Test
	public void windowPartitionByParenthesizedPredicandTest() {
		final String query = "SELECT rank(column) OVER (partition by (<k_stfd>), kppi order by row_num desc) from tab1";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={window_function={over={partition_by={1={parentheses={substitution={name=<k_stfd>, type=predicand}}}, 2={column={name=kppi, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=row_num, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=column, table_ref=null}}}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<k_stfd>=predicand}",
				extractor.getSubstitutionsMap().toString());
	}


	@Test
	public void windowOrderByParenthesizedPredicandTest() {
		final String query = "SELECT rank(column) OVER (partition by k_stfd, kppi order by (<row_num>) desc) from tab1";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={window_function={over={partition_by={1={column={name=k_stfd, table_ref=null}}, 2={column={name=kppi, table_ref=null}}}, orderby={1={null_order=null, predicand={substitution={name=<row_num>, type=predicand}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=column, table_ref=null}}}}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<row_num>=predicand}",
				extractor.getSubstitutionsMap().toString());
	}


	@Test
	public void windowWithUnboundedBoundingFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " rows between unbounded preceding and unbounded following) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=rows, between={end={value=unbounded, direction=FOLLOWING}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[key_rank]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={k_stfd=[[@9,39:44='k_stfd',<459>,1:39]], parm=[[@3,14:17='parm',<459>,1:14]], OBSERVATION_TM=[[@12,55:68='OBSERVATION_TM',<459>,1:55]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={key_rank=[[@23,137:144='key_rank',<459>,1:137]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=OBSERVATION_TM, table_ref=null}], query_dictionary={key_rank=[[@23,137:144='key_rank',<459>,1:137]]}, table_dictionary={tab1={k_stfd=[[@9,39:44='k_stfd',<459>,1:39]], parm=[[@3,14:17='parm',<459>,1:14]], OBSERVATION_TM=[[@12,55:68='OBSERVATION_TM',<459>,1:55]]}}, window_partition_by=[{name=k_stfd, table_ref=null}], interface={key_rank=[{name=k_stfd, table_ref=tab1}, {name=OBSERVATION_TM, table_ref=tab1}, {name=parm, table_ref=tab1}]}, table_alias={a=tab1}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void windowWithReversedUnboundedBoundingFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " rows between unbounded following and unbounded preceding) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=rows, between={end={value=unbounded, direction=PRECEDING}, begin={value=unbounded, direction=FOLLOWING}}}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={key_rank=[[@23,137:144='key_rank',<459>,1:137]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
	}


	@Test
	public void windowOrderByNullsLastInOverStatementTest() {
	// Item 100 - Order by accepts null operations
		final String query = " Select "
			+ "first_value(a.<Classification Description>) over (partition by a.<Student Classification Code> "
			+ " order by a.<Classification Description> nulls last) as Classification_Description "
			+ " From <[HEDGSS].[student_class_lkp]> as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
	
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=Classification_Description, window_function={over={partition_by={1={column={substitution={name=<Student Classification Code>, type=column}, table_ref=a}}}, orderby={1={null_order=last, predicand={column={substitution={name=<Classification Description>, type=column}, table_ref=a}}, sort_order=ASC}}}, function={function_name=first_value, parameters={1={column={substitution={name=<Classification Description>, type=column}, table_ref=a}}}}}}}, from={table={alias=a, substitution={name=<[HEDGSS].[student_class_lkp]>, parts={1=[HEDGSS], 2=[student_class_lkp]}, type=tuple}}}}}",
			extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[Classification_Description]", 
			extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[HEDGSS].[student_class_lkp]>=tuple, <Classification Description>=column, <Student Classification Code>=column}", 
			extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<[HEDGSS].[student_class_lkp]>={<Classification Description>=[[@3,20:20='a',<459>,1:20], [@16,113:113='a',<459>,1:113]], <Student Classification Code>=[[@11,71:71='a',<459>,1:71]]}}",
			extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={Classification_Description=[[@23,159:184='Classification_Description',<459>,1:159]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{substitution={name=<Classification Description>, type=column}, table_ref=a}], query_dictionary={Classification_Description=[[@23,159:184='Classification_Description',<459>,1:159]]}, table_dictionary={<[HEDGSS].[student_class_lkp]>={<Classification Description>=[[@3,20:20='a',<459>,1:20], [@16,113:113='a',<459>,1:113]], <Student Classification Code>=[[@11,71:71='a',<459>,1:71]]}}, window_partition_by=[{substitution={name=<Student Classification Code>, type=column}, table_ref=a}], interface={Classification_Description=[{substitution={name=<Student Classification Code>, type=column}, table_ref=a}, {substitution={name=<Classification Description>, type=column}, table_ref=a}]}, table_alias={a=<[HEDGSS].[student_class_lkp]>}}}", extractor.getSymbolTable().toString());
	}


	@Test
	public void windowWithLeftBoundRightUnboundedFrameTest() {
		final String query = " SELECT "
			+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
			+ " rows between 100 preceding and unbounded following) AS key_rank "
			+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
	
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=rows, between={end={value=unbounded, direction=FOLLOWING}, begin={value=100, direction=PRECEDING}}}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
			extractor.getAsTree().toString());
	}


	@Test
	public void windowWithLeftUnboundRightBoundFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " rows between unbounded preceding and 25 following) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=rows, between={end={value=25, direction=FOLLOWING}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void windowWithLeftBoundRightBoundFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " rows between 10 preceding and 25 following) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=rows, between={end={value=25, direction=FOLLOWING}, begin={value=10, direction=PRECEDING}}}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void windowWithLeftCurrentRowRightBoundFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " rows between current row and 25 following) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=rows, between={end={value=25, direction=FOLLOWING}, begin={value=CURRENT ROW}}}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void windowWithLeftUnboundRightCurrentRowFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " rows between unbounded preceding and current row) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=rows, between={end={value=CURRENT ROW}, begin={value=unbounded, direction=PRECEDING}}}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void windowWithPrecedingUnboundFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " rows unbounded preceding) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=rows, value=unbounded, direction=PRECEDING}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void windowWithPrecedingBoundFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " rows 30 preceding) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=rows, value=30, direction=PRECEDING}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void windowWithCurrentRowFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " rows current row) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=rows, value=CURRENT ROW}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void windowWithPrecedingUnboundRangeFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " range unboundED preceding) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=range, value=unboundED, direction=PRECEDING}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void windowWithPrecedingBoundRangeFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " range 30 preceding) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=range, value=30, direction=PRECEDING}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void windowWithCurrentRowRangeFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " range current row) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=range, value=CURRENT ROW}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void windowWithLeftBoundRightBoundRangeFrameTest() {
		final String query = " SELECT "
				+ " rank(parm) OVER (partition by k_stfd order by OBSERVATION_TM desc "
				+ " range between 10 preceding and 10 following) AS key_rank "
				+ " FROM tab1 as a";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=key_rank, window_function={over={bracket={type=range, between={end={value=10, direction=FOLLOWING}, begin={value=10, direction=PRECEDING}}}, partition_by={1={column={name=k_stfd, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=OBSERVATION_TM, table_ref=null}}, sort_order=desc}}}, function={function_name=rank, parameters={1={column={name=parm, table_ref=null}}}}}}}, from={table={alias=a, table=tab1}}}}",
				extractor.getAsTree().toString());
	}


	@Test
	public void aggregateParseTest() {

		final String query = " SELECT scbcrse_subj_code as subj_code, count(*), MAX(scbcrse_eff_term) "
				+ " FROM scbcrse " + " group by scbcrse_subj_code " + " order by 2, scbcrse_subj_code, 1 ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=scbcrse_subj_code, table_ref=null}, alias=subj_code}, 2={function={function_name=COUNT, qualifier=null, parameters=*}}, 3={function={function_name=MAX, qualifier=null, parameters={column={name=scbcrse_eff_term, table_ref=null}}}}}, orderby={1={null_order=null, predicand={literal=2}, sort_order=ASC}, 2={null_order=null, predicand={column={name=scbcrse_subj_code, table_ref=null}}, sort_order=ASC}, 3={null_order=null, predicand={literal=1}, sort_order=ASC}}, from={table={alias=null, table=scbcrse}}, groupby={1={column={name=scbcrse_subj_code, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[subj_code, unnamed_1, unnamed_0]", extractor.getInterface().toString());
		Assert.assertTrue("Substitution List is wrong", extractor.getSubstitutionsMap().isEmpty());
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={scbcrse_subj_code=[[@1,8:24='scbcrse_subj_code',<459>,1:8], [@18,96:112='scbcrse_subj_code',<459>,1:96], [@23,127:143='scbcrse_subj_code',<459>,1:127]], scbcrse_eff_term=[[@12,54:69='scbcrse_eff_term',<459>,1:54]], *=[[@7,46:46='*',<291>,1:46]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_1=[[@13,70:70=')',<288>,1:70]], subj_code=[[@3,29:37='subj_code',<459>,1:29]], unnamed_0=[[@8,47:47=')',<288>,1:47]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		 "{def_query0={query_dictionary={subj_code=[[@3,29:37='subj_code',<459>,1:29]], unnamed_1=[[@13,70:70=')',<288>,1:70]], unnamed_0=[[@8,47:47=')',<288>,1:47]]}, table_dictionary={scbcrse={scbcrse_subj_code=[[@1,8:24='scbcrse_subj_code',<459>,1:8], [@18,96:112='scbcrse_subj_code',<459>,1:96], [@23,127:143='scbcrse_subj_code',<459>,1:127]], scbcrse_eff_term=[[@12,54:69='scbcrse_eff_term',<459>,1:54]], *=[[@7,46:46='*',<291>,1:46]]}}, grouped_by=[{name=scbcrse_subj_code, table_ref=null}], ordered_by=[{name=scbcrse_subj_code, table_ref=null}], interface={subj_code=[{name=scbcrse_subj_code, table_ref=scbcrse}], unnamed_1=[{name=scbcrse_eff_term, table_ref=scbcrse}], unnamed_0=[]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void aggregateWithAliasParseTest() {

		final String query = " SELECT scbcrse_subj_code as subj_code, count(*) as total, MAX(scbcrse_eff_term) as maximum"
				+ " FROM scbcrse " + " group by scbcrse_subj_code " + " order by 2, scbcrse_subj_code, 1 ";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
	}


	@Test
	public void getCoalesceWithLiteralTest() {
		
		String query = "select CAST(COALESCE(acs__form_submissions.referrer_url, " 
				+ "	acs__form_metadata.submission_referer) as varchar) as referrer_url, "
				+ " CAST(COALESCE(acs__form_submissions.referrer_url, '0') as varchar) as referrer_url2 "
				+ " from acs__form_submissions left join acs__form_metadata on acs__form_submissions.form_id = acs__form_metadata.form_id ";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
	}


	@Test
	public void selectFirst_valueWindowFunctionOverTest() {
		String sql =  " SELECT " 
				+ " first_value(major_cd) over (partition by student_id, value_partition order by term_row) as major_cd_fill "
				+ " FROM standard_term";

		final SQLSelectParserParser parser = parse(sql);
			
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
	
		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=major_cd_fill, window_function={over={partition_by={1={column={name=student_id, table_ref=null}}, 2={column={name=value_partition, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=term_row, table_ref=null}}, sort_order=ASC}}}, function={function_name=first_value, parameters={1={column={name=major_cd, table_ref=null}}}}}}}, from={table={alias=null, table=standard_term}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[major_cd_fill]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{standard_term={value_partition=[[@11,62:76='value_partition',<459>,1:62]], term_row=[[@14,87:94='term_row',<459>,1:87]], major_cd=[[@3,21:28='major_cd',<459>,1:21]], student_id=[[@9,50:59='student_id',<459>,1:50]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={major_cd_fill=[[@17,100:112='major_cd_fill',<459>,1:100]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=term_row, table_ref=null}], query_dictionary={major_cd_fill=[[@17,100:112='major_cd_fill',<459>,1:100]]}, table_dictionary={standard_term={value_partition=[[@11,62:76='value_partition',<459>,1:62]], term_row=[[@14,87:94='term_row',<459>,1:87]], major_cd=[[@3,21:28='major_cd',<459>,1:21]], student_id=[[@9,50:59='student_id',<459>,1:50]]}}, window_partition_by=[{name=student_id, table_ref=null}, {name=value_partition, table_ref=null}], interface={major_cd_fill=[{name=student_id, table_ref=standard_term}, {name=value_partition, table_ref=standard_term}, {name=term_row, table_ref=standard_term}, {name=major_cd, table_ref=standard_term}]}}}", extractor.getSymbolTable().toString());
	}

	// ===== POSITION / CHARINDEX / INSTR tests (V1 naming) =====

	@Test
	public void positionV1TestWith2ParametersAndInStmtSelectlistTest() {
		final String query = "SELECT position('a' IN col1) AS p FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=position, parameters={1={literal='a'}, 2={column={name=col1, table_ref=null}}}, operator=IN}, alias=p}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[p]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col1=[[@5,23:26='col1',<459>,1:23]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={p=[[@8,32:32='p',<459>,1:32]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={p=[[@8,32:32='p',<459>,1:32]]}, table_dictionary={tab1={col1=[[@5,23:26='col1',<459>,1:23]]}}, interface={p=[{name=col1, table_ref=tab1}]}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void positionV2TestWith2ParametersNoInStmtSelectlistTest() {
		final String query = "SELECT position('a', col1) AS p FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=position, parameters={1={literal='a'}, 2={column={name=col1, table_ref=null}}}, operator=IN}, alias=p}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[p]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col1=[[@5,21:24='col1',<459>,1:21]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={p=[[@8,30:30='p',<459>,1:30]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={p=[[@8,30:30='p',<459>,1:30]]}, table_dictionary={tab1={col1=[[@5,21:24='col1',<459>,1:21]]}}, interface={p=[{name=col1, table_ref=tab1}]}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void positionV3TestWith2ParametersAndInStmtJoinOnConditionTest() {
		final String query = "SELECT t1.col1 FROM tab1 t1 JOIN tab2 t2 ON position('a' IN t1.col1) = 1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=t1}}}, from={join={1={table={alias=t1, table=tab1}}, 2={join=JOIN, on={condition={left={function={function_name=position, parameters={1={literal='a'}, 2={column={name=col1, table_ref=t1}}}, operator=IN}}, right={literal=1}, operator==}}}, 3={table={alias=t2, table=tab2}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col1=[[@1,7:8='t1',<459>,1:7], [@15,60:61='t1',<459>,1:60]]}, tab2={}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col1=[[@3,10:13='col1',<459>,1:10]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={col1=[[@3,10:13='col1',<459>,1:10]]}, table_dictionary={tab1={col1=[[@1,7:8='t1',<459>,1:7], [@15,60:61='t1',<459>,1:60]]}, tab2={}}, filters=[{name=col1, table_ref=t1}], interface={col1=[{name=col1, table_ref=t1}]}, table_alias={t1=tab1, t2=tab2}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void positionV4TestWith2ParametersAndInStmtWhereConditionTest() {
		final String query = "SELECT col1 FROM tab1 WHERE position('a' IN col1) = 1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=null}}}, from={table={alias=null, table=tab1}}, where={condition={left={function={function_name=position, parameters={1={literal='a'}, 2={column={name=col1, table_ref=null}}}, operator=IN}}, right={literal=1}, operator==}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col1=[[@1,7:10='col1',<459>,1:7], [@9,44:47='col1',<459>,1:44]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col1=[[@1,7:10='col1',<459>,1:7]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={col1=[[@1,7:10='col1',<459>,1:7]]}, table_dictionary={tab1={col1=[[@1,7:10='col1',<459>,1:7], [@9,44:47='col1',<459>,1:44]]}}, filters=[{name=col1, table_ref=tab1}], interface={col1=[{name=col1, table_ref=tab1}]}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void positionV5TestWith2ParametersAndInStmtHavingConditionTest() {
		final String query = "SELECT col1 FROM tab1 GROUP BY col1 HAVING position('a' IN col1) > 0";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=null}}}, having={condition={left={function={function_name=position, parameters={1={literal='a'}, 2={column={name=col1, table_ref=null}}}, operator=IN}}, right={literal=0}, operator=>}}, from={table={alias=null, table=tab1}}, groupby={1={column={name=col1, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col1=[[@1,7:10='col1',<459>,1:7], [@6,31:34='col1',<459>,1:31], [@12,59:62='col1',<459>,1:59]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col1=[[@1,7:10='col1',<459>,1:7]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={col1=[[@1,7:10='col1',<459>,1:7]]}, table_dictionary={tab1={col1=[[@1,7:10='col1',<459>,1:7], [@6,31:34='col1',<459>,1:31], [@12,59:62='col1',<459>,1:59]]}}, grouped_by=[{name=col1, table_ref=tab1}], filters=[{name=col1, table_ref=tab1}], interface={col1=[{name=col1, table_ref=tab1}]}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void positionV6TestWith2ParametersAndInStmtQualifyConditionTest() {
		final String query = "SELECT row_number() over (partition by col1 order by col1) as rn FROM tab1 QUALIFY position('a' IN col1) > 0";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=rn, window_function={over={partition_by={1={column={name=col1, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=col1, table_ref=null}}, sort_order=ASC}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=null, table=tab1}}, qualify={condition={left={function={function_name=position, parameters={1={literal='a'}, 2={column={name=col1, table_ref=null}}}, operator=IN}}, right={literal=0}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[rn]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col1=[[@8,39:42='col1',<459>,1:39], [@11,53:56='col1',<459>,1:53], [@22,99:102='col1',<459>,1:99]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={rn=[[@14,62:63='rn',<459>,1:62]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=col1, table_ref=null}], query_dictionary={rn=[[@14,62:63='rn',<459>,1:62]]}, table_dictionary={tab1={col1=[[@8,39:42='col1',<459>,1:39], [@11,53:56='col1',<459>,1:53], [@22,99:102='col1',<459>,1:99]]}}, window_partition_by=[{name=col1, table_ref=null}], filters=[{name=col1, table_ref=tab1}], interface={rn=[{name=col1, table_ref=tab1}]}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void positionV7TestWith2ParametersAndInStmtGroupByClauseTest() {
		final String query = "SELECT position('a' IN col1) AS p FROM tab1 GROUP BY position('a' IN col1)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=position, parameters={1={literal='a'}, 2={column={name=col1, table_ref=null}}}, operator=IN}, alias=p}}, from={table={alias=null, table=tab1}}, groupby={1={function={function_name=position, parameters={1={literal='a'}, 2={column={name=col1, table_ref=null}}}, operator=IN}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[p]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col1=[[@5,23:26='col1',<459>,1:23], [@17,69:72='col1',<459>,1:69]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={p=[[@8,32:32='p',<459>,1:32]]}}",
		 extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={p=[[@8,32:32='p',<459>,1:32]]}, table_dictionary={tab1={col1=[[@5,23:26='col1',<459>,1:23], [@17,69:72='col1',<459>,1:69]]}}, grouped_by=[{name=col1, table_ref=null}], interface={p=[{name=col1, table_ref=tab1}]}}}",
		 extractor.getSymbolTable().toString());
	}

	@Test
	public void positionV8TestWith2ParametersAndInStmtOrderByClauseTest() {
		final String query = "SELECT col1 FROM tab1 ORDER BY position('a' IN col1)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=col1, table_ref=null}}}, orderby={1={null_order=null, predicand={function={function_name=position, parameters={1={literal='a'}, 2={column={name=col1, table_ref=null}}}, operator=IN}}, sort_order=ASC}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col1]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col1=[[@1,7:10='col1',<459>,1:7], [@10,47:50='col1',<459>,1:47]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col1=[[@1,7:10='col1',<459>,1:7]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={col1=[[@1,7:10='col1',<459>,1:7]]}, table_dictionary={tab1={col1=[[@1,7:10='col1',<459>,1:7], [@10,47:50='col1',<459>,1:47]]}}, ordered_by=[{name=col1, table_ref=tab1}], interface={col1=[{name=col1, table_ref=tab1}]}}}", extractor.getSymbolTable().toString());
	}

	/** ANSI SQL:2003+ — window expression as query {@code ORDER BY} sort key (not in select list). */
	@Test
	public void windowFunctionInQueryOrderByClauseV17_6_9Test() {
		final String query =
				"SELECT col1, col2 FROM tab1 "
						+ "ORDER BY ROW_NUMBER() OVER (PARTITION BY col1 ORDER BY col2 DESC)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, orderby={1={null_order=null, predicand={window_function={over={partition_by={1={column={name=col1, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=col2, table_ref=null}}, sort_order=DESC}}}, function={function_name=ROW_NUMBER, parameters=null}}}, sort_order=ASC}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[col2, col1]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={col2=[[@3,13:16='col2',<459>,1:13], [@18,83:86='col2',<459>,1:83]], col1=[[@1,7:10='col1',<459>,1:7], [@15,69:72='col1',<459>,1:69]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={col2=[[@3,13:16='col2',<459>,1:13]], col1=[[@1,7:10='col1',<459>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={window_ordered_by=[{name=col2, table_ref=tab1}], query_dictionary={col2=[[@3,13:16='col2',<459>,1:13]], col1=[[@1,7:10='col1',<459>,1:7]]}, table_dictionary={tab1={col2=[[@3,13:16='col2',<459>,1:13], [@18,83:86='col2',<459>,1:83]], col1=[[@1,7:10='col1',<459>,1:7], [@15,69:72='col1',<459>,1:69]]}}, window_partition_by=[{name=col1, table_ref=tab1}], ordered_by=[{name=col1, table_ref=tab1}, {name=col2, table_ref=tab1}], interface={col2=[{name=col2, table_ref=tab1}], col1=[{name=col1, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void positionV9TestWith3ParametersSelectListTest() {
		final String query = "SELECT position('b', col1, 3) AS p FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=position, parameters={1={literal='b'}, 2={column={name=col1, table_ref=null}}, 3={literal=3}}}, alias=p}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[p]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col1=[[@5,21:24='col1',<459>,1:21]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={p=[[@10,33:33='p',<459>,1:33]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={p=[[@10,33:33='p',<459>,1:33]]}, table_dictionary={tab1={col1=[[@5,21:24='col1',<459>,1:21]]}}, interface={p=[{name=col1, table_ref=tab1}]}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void positionV10CharindexSelectListTest() {
		final String query = "SELECT charindex('a', col1, 1) AS p FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=charindex, parameters={1={literal='a'}, 2={column={name=col1, table_ref=null}}, 3={literal=1}}}, alias=p}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[p]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col1=[[@5,22:25='col1',<459>,1:22]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={p=[[@10,34:34='p',<459>,1:34]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={p=[[@10,34:34='p',<459>,1:34]]}, table_dictionary={tab1={col1=[[@5,22:25='col1',<459>,1:22]]}}, interface={p=[{name=col1, table_ref=tab1}]}}}", extractor.getSymbolTable().toString());
	}

	@Test
	public void positionV11InstrSelectListTest() {
		final String query = "SELECT instr('a', col1, 1) AS p FROM tab1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={function={function_name=instr, parameters={1={literal='a'}, 2={column={name=col1, table_ref=null}}, 3={literal=1}}}, alias=p}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[p]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col1=[[@5,18:21='col1',<459>,1:18]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={p=[[@10,30:30='p',<459>,1:30]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={p=[[@10,30:30='p',<459>,1:30]]}, table_dictionary={tab1={col1=[[@5,18:21='col1',<459>,1:18]]}}, interface={p=[{name=col1, table_ref=tab1}]}}}", extractor.getSymbolTable().toString());
	}

	// ===== end POSITION / CHARINDEX / INSTR tests =====

	@Test
	public void selectListAliasReferencedInPartitionByTest() {
		// Minimal Phase 13.4 window proof: later OVER PARTITION BY binds an earlier true
		// select-list output alias (ingress + convert-egress skip).
		final String query =
				"SELECT a AS alias_from_select_list, ROW_NUMBER() OVER (PARTITION BY alias_from_select_list) AS rn FROM tab1";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals(
				"AST is wrong",
				"{SQL={select={1={column={name=a, table_ref=null}, alias=alias_from_select_list}, 2={alias=rn, window_function={over={partition_by={1={column={name=alias_from_select_list, table_ref=null}}}}, function={function_name=ROW_NUMBER, parameters=null}}}}, from={table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[alias_from_select_list, rn]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals(
				"Query Column Dictionary is wrong",
				"{query0={rn=[[@15,95:96='rn',<459>,1:95]], alias_from_select_list=[[@3,12:33='alias_from_select_list',<459>,1:12], [@12,68:89='alias_from_select_list',<459>,1:68]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals(
				"Table Dictionary is wrong",
				"{tab1={a=[[@1,7:7='a',<459>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals(
				"Symbol Table is wrong",
				"{def_query0={query_dictionary={alias_from_select_list=[[@3,12:33='alias_from_select_list',<459>,1:12], [@12,68:89='alias_from_select_list',<459>,1:68]], rn=[[@15,95:96='rn',<459>,1:95]]}, table_dictionary={tab1={a=[[@1,7:7='a',<459>,1:7]]}}, window_partition_by=[{name=alias_from_select_list, table_ref=query0}], interface={alias_from_select_list=[{name=a, table_ref=tab1}], rn=[{name=alias_from_select_list, table_ref=query0}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void predicandSelectListAliasReferencedInPartitionByTest() {
		// Predicand substitution output aliases must be grounded so a later OVER PARTITION BY
		// can bind an earlier select-list alias (donor-email style).
		final String query =
				"SELECT <partner_system_name> AS alias_from_predicand, ROW_NUMBER() OVER (PARTITION BY alias_from_predicand) AS rn FROM tab1";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertNoFatalErrors(extractor);

		Assert.assertEquals(
				"Substitution List is wrong",
				"{<partner_system_name>=predicand}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals(
				"Symbol Table is wrong",
				"{def_query0={query_dictionary={alias_from_predicand=[[@3,32:51='alias_from_predicand',<459>,1:32], [@12,86:105='alias_from_predicand',<459>,1:86]], rn=[[@15,111:112='rn',<459>,1:111]]}, table_dictionary={tab1={}}, window_partition_by=[{name=alias_from_predicand, table_ref=query0}], interface={alias_from_predicand=[{name=<partner_system_name>, type=predicand}], rn=[{name=alias_from_predicand, table_ref=query0}]}}}",
				extractor.getSymbolTable().toString());
	}

	/**
	 * Walker coverage T1.8 — {@code exitRow_value_predicand_list} via {@code GROUP BY (a, b)}.
	 */
	@Test
	public void groupByRowValuePredicandListT1_8Test() {
		final String query = "SELECT a, b, SUM(c) FROM tab1 GROUP BY (a, b)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals(
				"AST is wrong",
				"{SQL={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={function={function_name=SUM, qualifier=null, parameters={column={name=c, table_ref=null}}}}}, from={table={alias=null, table=tab1}}, groupby={set={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b, unnamed_0]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals(
				"Table Dictionary is wrong",
				"{tab1={a=[[@1,7:7='a',<459>,1:7], [@14,40:40='a',<459>,1:40]], b=[[@3,10:10='b',<459>,1:10], [@16,43:43='b',<459>,1:43]], c=[[@7,17:17='c',<459>,1:17]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals(
				"Query Column Dictionary is wrong",
				"{query0={a=[[@1,7:7='a',<459>,1:7]], b=[[@3,10:10='b',<459>,1:10]], unnamed_0=[[@8,18:18=')',<288>,1:18]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals(
				"Symbol Table is wrong",
				"{def_query0={query_dictionary={a=[[@1,7:7='a',<459>,1:7]], b=[[@3,10:10='b',<459>,1:10]], unnamed_0=[[@8,18:18=')',<288>,1:18]]}, table_dictionary={tab1={a=[[@1,7:7='a',<459>,1:7], [@14,40:40='a',<459>,1:40]], b=[[@3,10:10='b',<459>,1:10], [@16,43:43='b',<459>,1:43]], c=[[@7,17:17='c',<459>,1:17]]}}, grouped_by=[{name=a, table_ref=tab1}, {name=b, table_ref=tab1}], interface={a=[{name=a, table_ref=tab1}], b=[{name=b, table_ref=tab1}], unnamed_0=[{name=c, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}

	/** Walker coverage T1.9 — removed: comma {@code TRIM} parses as {@code routine_invocation}, not {@code trim_operands}. */

	/** Walker coverage T1.10 — {@code exitOrdinary_grouping_set_list} via {@code ROLLUP((a,b), c)}. */
	@Test
	public void rollupOrdinaryGroupingSetListT1_10Test() {
		final String query = "SELECT a, b, c, SUM(d) FROM tab1 GROUP BY ROLLUP((a, b), c)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals(
				"AST is wrong",
				"{SQL={select={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}, 3={column={name=c, table_ref=null}}, 4={function={function_name=SUM, qualifier=null, parameters={column={name=d, table_ref=null}}}}}, from={table={alias=null, table=tab1}}, groupby={rollup={set={1={set={1={column={name=a, table_ref=null}}, 2={column={name=b, table_ref=null}}}}, 2={column={name=c, table_ref=null}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a, b, c, unnamed_0]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals(
				"Table Dictionary is wrong",
				"{tab1={a=[[@1,7:7='a',<459>,1:7], [@18,50:50='a',<459>,1:50]], b=[[@3,10:10='b',<459>,1:10], [@20,53:53='b',<459>,1:53]], c=[[@5,13:13='c',<459>,1:13], [@23,57:57='c',<459>,1:57]], d=[[@9,20:20='d',<459>,1:20]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals(
				"Query Column Dictionary is wrong",
				"{query0={a=[[@1,7:7='a',<459>,1:7]], b=[[@3,10:10='b',<459>,1:10]], c=[[@5,13:13='c',<459>,1:13]], unnamed_0=[[@10,21:21=')',<288>,1:21]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals(
				"Symbol Table is wrong",
				"{def_query0={query_dictionary={a=[[@1,7:7='a',<459>,1:7]], b=[[@3,10:10='b',<459>,1:10]], c=[[@5,13:13='c',<459>,1:13]], unnamed_0=[[@10,21:21=')',<288>,1:21]]}, table_dictionary={tab1={a=[[@1,7:7='a',<459>,1:7], [@18,50:50='a',<459>,1:50]], b=[[@3,10:10='b',<459>,1:10], [@20,53:53='b',<459>,1:53]], c=[[@5,13:13='c',<459>,1:13], [@23,57:57='c',<459>,1:57]], d=[[@9,20:20='d',<459>,1:20]]}}, grouped_by=[{name=a, table_ref=tab1}, {name=b, table_ref=tab1}, {name=c, table_ref=tab1}], interface={a=[{name=a, table_ref=tab1}], b=[{name=b, table_ref=tab1}], c=[{name=c, table_ref=tab1}], unnamed_0=[{name=d, table_ref=tab1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void arC026_array_containsSelectPlacementV0Test() {
		final String query = "SELECT a.id, array_contains(a.needle, a.tags) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@21,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]], tags=[[@11,38:38='a',<459>,1:38]]}}",
				"{query0={result_value=[[@16,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]], tags=[[@11,38:38='a',<459>,1:38]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsWherePlacementV0Test() {
		final String query = "SELECT a.id, a.label FROM events a WHERE array_contains(a.needle, a.tags) IS NOT NULL AND a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:41) is recognized as Snowflake-specific grammar.",
						1, 41, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=a}}}, from={table={alias=a, table=events}}, where={and={1={condition={left={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}}, operator=IS NOT NULL}}, 2={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[id, label]",
				"{}",
				"{events={active=[[@26,90:90='a',<459>,1:90]], id=[[@1,7:7='a',<459>,1:7]], label=[[@5,13:13='a',<459>,1:13]], needle=[[@14,56:56='a',<459>,1:56]], tags=[[@18,66:66='a',<459>,1:66]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={events={active=[[@26,90:90='a',<459>,1:90]], id=[[@1,7:7='a',<459>,1:7]], label=[[@5,13:13='a',<459>,1:13]], needle=[[@14,56:56='a',<459>,1:56]], tags=[[@18,66:66='a',<459>,1:66]]}}, filters=[{name=needle, table_ref=a}, {name=tags, table_ref=a}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsJoinPlacementV0Test() {
		final String query = "SELECT a.id, b.label FROM events a JOIN labels b ON array_contains(a.needle, a.tags) = b.ref_value AND a.id = b.event_id WHERE b.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:52) is recognized as Snowflake-specific grammar.",
						1, 52, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=b}}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={and={1={condition={left={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}}, right={column={name=ref_value, table_ref=b}}, operator==}}, 2={condition={left={column={name=id, table_ref=a}}, right={column={name=event_id, table_ref=b}}, operator==}}}}}, 3={table={alias=b, table=labels}}}}, where={condition={left={column={name=active, table_ref=b}}, right={literal=1}, operator==}}}}",
				"[id, label]",
				"{}",
				"{events={id=[[@1,7:7='a',<459>,1:7], [@30,103:103='a',<459>,1:103]], needle=[[@17,67:67='a',<459>,1:67]], tags=[[@21,77:77='a',<459>,1:77]]}, labels={ref_value=[[@26,87:87='b',<459>,1:87]], event_id=[[@34,110:110='b',<459>,1:110]], active=[[@38,127:127='b',<459>,1:127]], label=[[@5,13:13='b',<459>,1:13]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={events={id=[[@1,7:7='a',<459>,1:7], [@30,103:103='a',<459>,1:103]], needle=[[@17,67:67='a',<459>,1:67]], tags=[[@21,77:77='a',<459>,1:77]]}, labels={ref_value=[[@26,87:87='b',<459>,1:87]], event_id=[[@34,110:110='b',<459>,1:110]], active=[[@38,127:127='b',<459>,1:127]], label=[[@5,13:13='b',<459>,1:13]]}}, filters=[{name=needle, table_ref=a}, {name=tags, table_ref=a}, {name=ref_value, table_ref=b}, {name=id, table_ref=a}, {name=event_id, table_ref=b}, {name=active, table_ref=b}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=b}]}, table_alias={a=events, b=labels}}}");
	}

	@Test
	public void arC026_array_containsHavingPlacementV0Test() {
		final String query = "SELECT a.id, count(*) AS total FROM events a GROUP BY a.id, a.needle, a.tags HAVING array_contains(a.needle, a.tags) IS NOT NULL";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:84) is recognized as Snowflake-specific grammar.",
						1, 84, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={function_name=COUNT, qualifier=null, parameters=*}, alias=total}}, having={condition={left={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}}, operator=IS NOT NULL}}, from={table={alias=a, table=events}}, groupby={1={column={name=id, table_ref=a}}, 2={column={name=needle, table_ref=a}}, 3={column={name=tags, table_ref=a}}}}}",
				"[total, id]",
				"{}",
				"{events={*=[[@7,19:19='*',<291>,1:19]], id=[[@1,7:7='a',<459>,1:7], [@16,54:54='a',<459>,1:54]], needle=[[@20,60:60='a',<459>,1:60], [@30,99:99='a',<459>,1:99]], tags=[[@24,70:70='a',<459>,1:70], [@34,109:109='a',<459>,1:109]]}}",
				"{query0={total=[[@10,25:29='total',<459>,1:25]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={total=[[@10,25:29='total',<459>,1:25]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={*=[[@7,19:19='*',<291>,1:19]], id=[[@1,7:7='a',<459>,1:7], [@16,54:54='a',<459>,1:54]], needle=[[@20,60:60='a',<459>,1:60], [@30,99:99='a',<459>,1:99]], tags=[[@24,70:70='a',<459>,1:70], [@34,109:109='a',<459>,1:109]]}}, grouped_by=[{name=id, table_ref=a}, {name=needle, table_ref=a}, {name=tags, table_ref=a}], filters=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], interface={total=[], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsQualifyPlacementV0Test() {
		final String query = "SELECT a.id, row_number() over (partition by a.group_id order by a.id) AS rn FROM events a QUALIFY array_contains(a.needle, a.tags) IS NOT NULL AND rn <= 5";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:99) is recognized as Snowflake-specific grammar.",
						1, 99, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={alias=rn, window_function={over={partition_by={1={column={name=group_id, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=id, table_ref=a}}, sort_order=ASC}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=a, table=events}}, qualify={and={1={condition={left={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}}, operator=IS NOT NULL}}, 2={condition={left={column={name=rn, table_ref=null}}, right={literal=5}, operator=<=}}}}}}",
				"[id, rn]",
				"{}",
				"{events={group_id=[[@12,45:45='a',<459>,1:45]], id=[[@1,7:7='a',<459>,1:7], [@17,65:65='a',<459>,1:65]], needle=[[@29,114:114='a',<459>,1:114]], tags=[[@33,124:124='a',<459>,1:124]]}}",
				"{query0={rn=[[@22,74:75='rn',<459>,1:74], [@41,148:149='rn',<459>,1:148]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={window_ordered_by=[{name=id, table_ref=a}], query_dictionary={id=[[@3,9:10='id',<459>,1:9]], rn=[[@22,74:75='rn',<459>,1:74], [@41,148:149='rn',<459>,1:148]]}, table_dictionary={events={group_id=[[@12,45:45='a',<459>,1:45]], id=[[@1,7:7='a',<459>,1:7], [@17,65:65='a',<459>,1:65]], needle=[[@29,114:114='a',<459>,1:114]], tags=[[@33,124:124='a',<459>,1:124]]}}, window_partition_by=[{name=group_id, table_ref=a}], filters=[{name=needle, table_ref=a}, {name=tags, table_ref=a}, {name=rn, table_ref=query0}], interface={id=[{name=id, table_ref=a}], rn=[{name=group_id, table_ref=a}, {name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsOrderByPlacementV0Test() {
		final String query = "SELECT a.id FROM events a WHERE a.active = 1 ORDER BY array_contains(a.needle, a.tags), a.id";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:54) is recognized as Snowflake-specific grammar.",
						1, 54, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, orderby={1={null_order=null, predicand={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=id, table_ref=a}}, sort_order=ASC}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id]",
				"{}",
				"{events={active=[[@8,32:32='a',<459>,1:32]], id=[[@1,7:7='a',<459>,1:7], [@26,88:88='a',<459>,1:88]], needle=[[@17,69:69='a',<459>,1:69]], tags=[[@21,79:79='a',<459>,1:79]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@8,32:32='a',<459>,1:32]], id=[[@1,7:7='a',<459>,1:7], [@26,88:88='a',<459>,1:88]], needle=[[@17,69:69='a',<459>,1:69]], tags=[[@21,79:79='a',<459>,1:79]]}}, ordered_by=[{name=needle, table_ref=a}, {name=tags, table_ref=a}, {name=id, table_ref=a}], filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsValuesPlacementV0Test() {
		final String query = "VALUES (7, array_contains('x'::VARIANT, ['x', 'y']))";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:40) is recognized as Snowflake-specific grammar.",
						1, 40, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={values={matrix={1={row={1={literal=7}, 2={function={parameters={1={function={function_name=cast, data_type={type=VARIANT}, type=CAST, value={literal=x}}}, 2={function={function_name=array_construct, parameters={1={literal='x'}, 2={literal='y'}}}}}, function_name=array_contains}}}}}}}}",
				"[$1, $2]",
				"{}",
				"{}",
				"{values0={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}}",
				"{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[], $2=[]}}}");
	}

	@Test
	public void arC026_array_containsInsertPlacementV0Test() {
		final String query = "INSERT INTO results (id, result_value) SELECT a.id, array_contains(a.needle, a.tags) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:52) is recognized as Snowflake-specific grammar.",
						1, 52, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}}, target_table={table={alias=null, table=results}}, columns={1={column={name=id, table_ref=null}}, 2={column={name=result_value, table_ref=null}}}}}}",
				"[result_value, id]",
				"{}",
				"{results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, events={active=[[@29,121:121='a',<459>,1:121]], id=[[@9,46:46='a',<459>,1:46]], needle=[[@15,67:67='a',<459>,1:67]], tags=[[@19,77:77='a',<459>,1:77]]}}",
				"{query0={result_value=[[@24,88:99='result_value',<459>,1:88]], id=[[@11,48:49='id',<459>,1:48]]}, insert1={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}",
				"{def_insert1={query_dictionary={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, table_dictionary={results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}, def_query0={query_dictionary={result_value=[[@24,88:99='result_value',<459>,1:88]], id=[[@11,48:49='id',<459>,1:48]]}, table_dictionary={events={active=[[@29,121:121='a',<459>,1:121]], id=[[@9,46:46='a',<459>,1:46]], needle=[[@15,67:67='a',<459>,1:67]], tags=[[@19,77:77='a',<459>,1:77]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={id=[{name=id, table_ref=query0}], result_value=[{name=result_value, table_ref=query0}]}}}");
	}

	@Test
	public void arC026_array_containsUpdatePlacementV0Test() {
		final String query = "UPDATE events SET result_value = array_contains(needle, tags) WHERE active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:33) is recognized as Snowflake-specific grammar.",
						1, 33, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={update={table={alias=null, table=events}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}, assignments={1={set={column={name=result_value, table_ref=null}}, to={function={parameters={1={column={name=needle, table_ref=null}}, 2={column={name=tags, table_ref=null}}}, function_name=array_contains}}}}}}}",
				"[result_value]",
				"{}",
				"{events={active=[[@12,68:73='active',<459>,1:68]], needle=[[@7,48:53='needle',<459>,1:48], [@10,60:60=')',<288>,1:60]], tags=[[@9,56:59='tags',<459>,1:56], [@10,60:60=')',<288>,1:60]]}}",
				"{update0={result_value=[[@3,18:29='result_value',<459>,1:18]]}}",
				"{def_update0={assignments={result_value=[{name=needle, table_ref=events}, {name=tags, table_ref=events}]}, table_dictionary={events={active=[[@12,68:73='active',<459>,1:68]], needle=[[@7,48:53='needle',<459>,1:48], [@10,60:60=')',<288>,1:60]], tags=[[@9,56:59='tags',<459>,1:56], [@10,60:60=')',<288>,1:60]]}}, update_dictionary={result_value=[[@3,18:29='result_value',<459>,1:18]]}, target_table={events={result_value=[[@3,18:29='result_value',<459>,1:18]]}}, filters=[{name=active, table_ref=events}], lhs_unresolved_columns={result_value={column={name=result_value, table_ref=null}, locations=[[@3,18:29='result_value',<459>,1:18]]}}}}");
	}

	@Test
	public void arC026_array_containsDeletePlacementV0Test() {
		final String query = "DELETE FROM events WHERE array_contains(needle, tags) IS NULL AND active = 0";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:25) is recognized as Snowflake-specific grammar.",
						1, 25, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={delete={table={alias=null, table=events}, where={and={1={condition={left={function={parameters={1={column={name=needle, table_ref=null}}, 2={column={name=tags, table_ref=null}}}, function_name=array_contains}}, operator=IS NULL}}, 2={condition={left={column={name=active, table_ref=null}}, right={literal=0}, operator==}}}}}}}",
				"[]",
				"{}",
				"{events={active=[[@13,66:71='active',<459>,1:66]], needle=[[@6,40:45='needle',<459>,1:40]], tags=[[@8,48:51='tags',<459>,1:48]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={active=[[@13,66:71='active',<459>,1:66]], needle=[[@6,40:45='needle',<459>,1:40]], tags=[[@8,48:51='tags',<459>,1:48]]}}, filters=[{name=needle, table_ref=events}, {name=tags, table_ref=events}, {name=active, table_ref=events}], interface=null}}");
	}

	@Test
	public void arC026_array_containsDdlPlacementV0Test() {
		final String query = "CREATE VIEW v_array_contains AS SELECT a.id, array_contains(a.needle, a.tags) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.DdlContext tree = parser.ddl();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:45) is recognized as Snowflake-specific grammar.",
						1, 45, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{DDL={create={type=VIEW, name={table=v_array_contains}, query={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[]",
				"{}",
				"{events={active=[[@25,114:114='a',<459>,1:114]], id=[[@5,39:39='a',<459>,1:39]], needle=[[@11,60:60='a',<459>,1:60]], tags=[[@15,70:70='a',<459>,1:70]]}}",
				"{query0={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@7,41:42='id',<459>,1:41]]}}",
				"{def_create1={def_query0={query_dictionary={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@7,41:42='id',<459>,1:41]]}, table_dictionary={events={active=[[@25,114:114='a',<459>,1:114]], id=[[@5,39:39='a',<459>,1:39]], needle=[[@11,60:60='a',<459>,1:60]], tags=[[@15,70:70='a',<459>,1:70]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}");
	}

	@Test
	public void arC026_array_containsScriptPlacementV0Test() {
		final String query = "SELECT a.id, array_contains(a.needle, a.tags) AS result_value FROM events a WHERE a.active = 1;\nSELECT b.id FROM orders b WHERE b.active = 1;\nSELECT c.id, array_contains(c.needle, c.tags) AS other_value FROM archive_events c WHERE c.active = 1;";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.ScriptContext tree = parser.script();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 2,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:3 c:13) is recognized as Snowflake-specific grammar.",
						3, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SCRIPT={1={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, 2={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=orders}}, where={condition={left={column={name=active, table_ref=b}}, right={literal=1}, operator==}}}, 3={select={1={column={name=id, table_ref=c}}, 2={function={parameters={1={column={name=needle, table_ref=c}}, 2={column={name=tags, table_ref=c}}}, function_name=array_contains}, alias=other_value}}, from={table={alias=c, table=archive_events}}, where={condition={left={column={name=active, table_ref=c}}, right={literal=1}, operator==}}}}}",
				"[]",
				"{SCRIPT={1={}, 2={}, 3={}}}",
				"{SCRIPT={1={events={active=[[@21,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]], tags=[[@11,38:38='a',<459>,1:38]]}}, 2={orders={active=[[@35,128:128='b',<459>,2:32]], id=[[@28,103:103='b',<459>,2:7]]}}, 3={archive_events={active=[[@62,231:231='c',<459>,3:89]], id=[[@42,149:149='c',<459>,3:7]], needle=[[@48,170:170='c',<459>,3:28]], tags=[[@52,180:180='c',<459>,3:38]]}}}}",
				"{SCRIPT={1={query0={result_value=[[@16,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}}, 2={query0={id=[[@30,105:106='id',<459>,2:9]]}}, 3={query0={other_value=[[@57,191:201='other_value',<459>,3:49]], id=[[@44,151:152='id',<459>,3:9]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={result_value=[[@16,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]], tags=[[@11,38:38='a',<459>,1:38]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}, 2={def_query0={query_dictionary={id=[[@30,105:106='id',<459>,2:9]]}, table_dictionary={orders={active=[[@35,128:128='b',<459>,2:32]], id=[[@28,103:103='b',<459>,2:7]]}}, filters=[{name=active, table_ref=b}], interface={id=[{name=id, table_ref=b}]}, table_alias={b=orders}}}, 3={def_query0={query_dictionary={other_value=[[@57,191:201='other_value',<459>,3:49]], id=[[@44,151:152='id',<459>,3:9]]}, table_dictionary={archive_events={active=[[@62,231:231='c',<459>,3:89]], id=[[@42,149:149='c',<459>,3:7]], needle=[[@48,170:170='c',<459>,3:28]], tags=[[@52,180:180='c',<459>,3:38]]}}, filters=[{name=active, table_ref=c}], interface={other_value=[{name=needle, table_ref=c}, {name=tags, table_ref=c}], id=[{name=id, table_ref=c}]}, table_alias={c=archive_events}}}}}");
	}

	@Test
	public void arC026_array_containsArg1UnqualifiedV0Test() {
		final String query = "SELECT a.id, array_contains(needle, a.tags) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=null}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@19,80:80='a',<459>,1:80]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:33='needle',<459>,1:28]], tags=[[@9,36:36='a',<459>,1:36]]}}",
				"{query0={result_value=[[@14,47:58='result_value',<459>,1:47]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@14,47:58='result_value',<459>,1:47]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@19,80:80='a',<459>,1:80]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:33='needle',<459>,1:28]], tags=[[@9,36:36='a',<459>,1:36]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=events}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsArg1UnresolvedV0Test() {
		final String query = "SELECT 7 AS marker, array_contains(needle, ['x', 'y']) AS result_value";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:43) is recognized as Snowflake-specific grammar.",
						1, 43, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [needle [(l:1 c:35)]]",
						1, 35, "SqlASTWalkerHelper", null, "needle", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={alias=marker, literal=7}, 2={function={parameters={1={column={name=needle, table_ref=null}}, 2={function={function_name=array_construct, parameters={1={literal='x'}, 2={literal='y'}}}}}, function_name=array_contains}, alias=result_value}}}}",
				"[marker, result_value]",
				"{}",
				"{}",
				"{query0={marker=[[@3,12:17='marker',<459>,1:12]], result_value=[[@16,58:69='result_value',<459>,1:58]]}}",
				"{def_query0={query_dictionary={marker=[[@3,12:17='marker',<459>,1:12]], result_value=[[@16,58:69='result_value',<459>,1:58]]}, interface={marker=[], result_value=[{name=needle, table_ref=null}]}}}");
	}

	@Test
	public void arC026_array_containsArg1AmbiguousV0Test() {
		final String query = "SELECT a.id, array_contains(needle, a.tags) AS result_value FROM events a JOIN labels b ON a.id = b.event_id WHERE b.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.SEVERE_WARNING,
						"AMBIGUOUS_COLUMN_REFERENCE",
						"Ambiguous column reference 'needle' at (l:1 c:28). Possible sources: [events, labels]",
						1, 28, "SqlASTWalkerHelper", null, "needle", true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [needle [(l:1 c:28)]]",
						1, 28, "SqlASTWalkerHelper", null, "needle", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=null}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=event_id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=labels}}}}, where={condition={left={column={name=active, table_ref=b}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={id=[[@1,7:7='a',<459>,1:7], [@22,91:91='a',<459>,1:91]], tags=[[@9,36:36='a',<459>,1:36]]}, labels={event_id=[[@26,98:98='b',<459>,1:98]], active=[[@30,115:115='b',<459>,1:115]]}}",
				"{query0={result_value=[[@14,47:58='result_value',<459>,1:47]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@14,47:58='result_value',<459>,1:47]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={id=[[@1,7:7='a',<459>,1:7], [@22,91:91='a',<459>,1:91]], tags=[[@9,36:36='a',<459>,1:36]]}, labels={event_id=[[@26,98:98='b',<459>,1:98]], active=[[@30,115:115='b',<459>,1:115]]}}, filters=[{name=id, table_ref=a}, {name=event_id, table_ref=b}, {name=active, table_ref=b}], interface={result_value=[{name=needle, table_ref=null}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events, b=labels}}}");
	}

	@Test
	public void arC026_array_containsArg1MissingAliasV0Test() {
		final String query = "SELECT a.id, array_contains(z.needle, a.tags) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'needle' at (l:1 c:49). No alias or table called 'z'.",
						1, 49, "SqlASTWalkerHelper", null, "needle", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=z}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@21,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@11,38:38='a',<459>,1:38]]}}",
				"{query0={result_value=[[@16,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@11,38:38='a',<459>,1:38]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=z}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsArg1HiddenAliasV0Test() {
		final String query = "SELECT q.id, array_contains(a.needle, q.tags) AS result_value FROM (SELECT a.id, a.needle, a.tags FROM events a WHERE a.active = 1) q WHERE q.id > 0";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'needle' at (l:1 c:49). No alias or table called 'a'.",
						1, 49, "SqlASTWalkerHelper", null, "needle", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=q}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=q}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=q, query={select={1={column={name=id, table_ref=a}}, 2={column={name=needle, table_ref=a}}, 3={column={name=tags, table_ref=a}}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}, where={condition={left={column={name=id, table_ref=q}}, right={literal=0}, operator=>}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@35,118:118='a',<459>,1:118]], id=[[@20,75:75='a',<459>,1:75]], needle=[[@24,81:81='a',<459>,1:81]], tags=[[@28,91:91='a',<459>,1:91]]}}",
				"{query0={needle=[[@26,83:88='needle',<459>,1:83]], id=[[@22,77:78='id',<459>,1:77], [@1,7:7='q',<459>,1:7], [@43,140:140='q',<459>,1:140]], tags=[[@30,93:96='tags',<459>,1:93], [@11,38:38='q',<459>,1:38]]}, query1={result_value=[[@16,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query1={query_dictionary={result_value=[[@16,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={}, def_query0={query_dictionary={id=[[@22,77:78='id',<459>,1:77], [@1,7:7='q',<459>,1:7], [@43,140:140='q',<459>,1:140]], needle=[[@26,83:88='needle',<459>,1:83]], tags=[[@30,93:96='tags',<459>,1:93], [@11,38:38='q',<459>,1:38]]}, table_dictionary={events={active=[[@35,118:118='a',<459>,1:118]], id=[[@20,75:75='a',<459>,1:75]], needle=[[@24,81:81='a',<459>,1:81]], tags=[[@28,91:91='a',<459>,1:91]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], needle=[{name=needle, table_ref=a}], tags=[{name=tags, table_ref=a}]}, table_alias={a=events}}, filters=[{name=id, table_ref=q}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=q}], id=[{name=id, table_ref=q}]}, table_alias={q=query0}}}");
	}

	@Test
	public void arC026_array_containsArg1PredicandVariableV0Test() {
		final String query = "SELECT a.id, array_contains(<needle_expr>, a.tags) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={substitution={name=<needle_expr>, type=predicand}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<needle_expr>=predicand}",
				"{events={active=[[@19,87:87='a',<459>,1:87]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@9,43:43='a',<459>,1:43]]}}",
				"{query0={result_value=[[@14,54:65='result_value',<459>,1:54]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@14,54:65='result_value',<459>,1:54]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@19,87:87='a',<459>,1:87]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@9,43:43='a',<459>,1:43]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=<needle_expr>, type=predicand}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsArg1ColumnVariableV0Test() {
		final String query = "SELECT a.id, array_contains(a.<needle_col>, a.tags) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={substitution={name=<needle_col>, type=column}, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<needle_col>=column}",
				"{events={<needle_col>=[[@7,28:28='a',<459>,1:28]], active=[[@21,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@11,44:44='a',<459>,1:44]]}}",
				"{query0={result_value=[[@16,55:66='result_value',<459>,1:55]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,55:66='result_value',<459>,1:55]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={<needle_col>=[[@7,28:28='a',<459>,1:28]], active=[[@21,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@11,44:44='a',<459>,1:44]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{substitution={name=<needle_col>, type=column}, table_ref=a}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsArg1NullV0Test() {
		final String query = "SELECT a.id, array_contains(NULL, a.tags) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={null_literal=null}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@19,78:78='a',<459>,1:78]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@9,34:34='a',<459>,1:34]]}}",
				"{query0={result_value=[[@14,45:56='result_value',<459>,1:45]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@14,45:56='result_value',<459>,1:45]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@19,78:78='a',<459>,1:78]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@9,34:34='a',<459>,1:34]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsArg2UnqualifiedV0Test() {
		final String query = "SELECT a.id, array_contains(a.needle, tags) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=null}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@19,80:80='a',<459>,1:80]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]], tags=[[@11,38:41='tags',<459>,1:38]]}}",
				"{query0={result_value=[[@14,47:58='result_value',<459>,1:47]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@14,47:58='result_value',<459>,1:47]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@19,80:80='a',<459>,1:80]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]], tags=[[@11,38:41='tags',<459>,1:38]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=events}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsArg2UnresolvedV0Test() {
		final String query = "SELECT 7 AS marker, array_contains('x'::VARIANT, tags) AS result_value";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:20) is recognized as Snowflake-specific grammar.",
						1, 20, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [tags [(l:1 c:49)]]",
						1, 49, "SqlASTWalkerHelper", null, "tags", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={alias=marker, literal=7}, 2={function={parameters={1={function={function_name=cast, data_type={type=VARIANT}, type=CAST, value={literal=x}}}, 2={column={name=tags, table_ref=null}}}, function_name=array_contains}, alias=result_value}}}}",
				"[marker, result_value]",
				"{}",
				"{}",
				"{query0={marker=[[@3,12:17='marker',<459>,1:12]], result_value=[[@14,58:69='result_value',<459>,1:58]]}}",
				"{def_query0={query_dictionary={marker=[[@3,12:17='marker',<459>,1:12]], result_value=[[@14,58:69='result_value',<459>,1:58]]}, interface={marker=[], result_value=[{name=tags, table_ref=null}]}}}");
	}

	@Test
	public void arC026_array_containsArg2AmbiguousV0Test() {
		final String query = "SELECT a.id, array_contains(a.needle, tags) AS result_value FROM events a JOIN labels b ON a.id = b.event_id WHERE b.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.SEVERE_WARNING,
						"AMBIGUOUS_COLUMN_REFERENCE",
						"Ambiguous column reference 'tags' at (l:1 c:38). Possible sources: [events, labels]",
						1, 38, "SqlASTWalkerHelper", null, "tags", true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.ERROR,
						"UNRESOLVED_UNQUALIFIED_COLUMNS",
						"Unresolved unqualified column reference(s): [tags [(l:1 c:38)]]",
						1, 38, "SqlASTWalkerHelper", null, "tags", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=null}}}, function_name=array_contains}, alias=result_value}}, from={join={1={table={alias=a, table=events}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=a}}, right={column={name=event_id, table_ref=b}}, operator==}}}, 3={table={alias=b, table=labels}}}}, where={condition={left={column={name=active, table_ref=b}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={id=[[@1,7:7='a',<459>,1:7], [@22,91:91='a',<459>,1:91]], needle=[[@7,28:28='a',<459>,1:28]]}, labels={event_id=[[@26,98:98='b',<459>,1:98]], active=[[@30,115:115='b',<459>,1:115]]}}",
				"{query0={result_value=[[@14,47:58='result_value',<459>,1:47]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@14,47:58='result_value',<459>,1:47]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={id=[[@1,7:7='a',<459>,1:7], [@22,91:91='a',<459>,1:91]], needle=[[@7,28:28='a',<459>,1:28]]}, labels={event_id=[[@26,98:98='b',<459>,1:98]], active=[[@30,115:115='b',<459>,1:115]]}}, filters=[{name=id, table_ref=a}, {name=event_id, table_ref=b}, {name=active, table_ref=b}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=null}], id=[{name=id, table_ref=a}]}, table_alias={a=events, b=labels}}}");
	}

	@Test
	public void arC026_array_containsArg2MissingAliasV0Test() {
		final String query = "SELECT a.id, array_contains(a.needle, z.tags) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'tags' at (l:1 c:49). No alias or table called 'z'.",
						1, 49, "SqlASTWalkerHelper", null, "tags", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=z}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@21,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]]}}",
				"{query0={result_value=[[@16,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=z}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsArg2HiddenAliasV0Test() {
		final String query = "SELECT q.id, array_contains(q.needle, a.tags) AS result_value FROM (SELECT a.id, a.needle, a.tags FROM events a WHERE a.active = 1) q WHERE q.id > 0";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
						"Source Table not found for Column 'tags' at (l:1 c:49). No alias or table called 'a'.",
						1, 49, "SqlASTWalkerHelper", null, "tags", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=q}}, 2={function={parameters={1={column={name=needle, table_ref=q}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=q, query={select={1={column={name=id, table_ref=a}}, 2={column={name=needle, table_ref=a}}, 3={column={name=tags, table_ref=a}}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}, where={condition={left={column={name=id, table_ref=q}}, right={literal=0}, operator=>}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@35,118:118='a',<459>,1:118]], id=[[@20,75:75='a',<459>,1:75]], needle=[[@24,81:81='a',<459>,1:81]], tags=[[@28,91:91='a',<459>,1:91]]}}",
				"{query0={needle=[[@26,83:88='needle',<459>,1:83], [@7,28:28='q',<459>,1:28]], id=[[@22,77:78='id',<459>,1:77], [@1,7:7='q',<459>,1:7], [@43,140:140='q',<459>,1:140]], tags=[[@30,93:96='tags',<459>,1:93]]}, query1={result_value=[[@16,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query1={query_dictionary={result_value=[[@16,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={}, def_query0={query_dictionary={id=[[@22,77:78='id',<459>,1:77], [@1,7:7='q',<459>,1:7], [@43,140:140='q',<459>,1:140]], needle=[[@26,83:88='needle',<459>,1:83], [@7,28:28='q',<459>,1:28]], tags=[[@30,93:96='tags',<459>,1:93]]}, table_dictionary={events={active=[[@35,118:118='a',<459>,1:118]], id=[[@20,75:75='a',<459>,1:75]], needle=[[@24,81:81='a',<459>,1:81]], tags=[[@28,91:91='a',<459>,1:91]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], needle=[{name=needle, table_ref=a}], tags=[{name=tags, table_ref=a}]}, table_alias={a=events}}, filters=[{name=id, table_ref=q}], interface={result_value=[{name=needle, table_ref=q}, {name=tags, table_ref=a}], id=[{name=id, table_ref=q}]}, table_alias={q=query0}}}");
	}

	@Test
	public void arC026_array_containsArg2PredicandVariableV0Test() {
		final String query = "SELECT a.id, array_contains(a.needle, <tags_expr>) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={substitution={name=<tags_expr>, type=predicand}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<tags_expr>=predicand}",
				"{events={active=[[@19,87:87='a',<459>,1:87]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]]}}",
				"{query0={result_value=[[@14,54:65='result_value',<459>,1:54]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@14,54:65='result_value',<459>,1:54]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@19,87:87='a',<459>,1:87]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=<tags_expr>, type=predicand}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsArg2ColumnVariableV0Test() {
		final String query = "SELECT a.id, array_contains(a.needle, a.<tags_col>) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={substitution={name=<tags_col>, type=column}, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<tags_col>=column}",
				"{events={active=[[@21,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]], <tags_col>=[[@11,38:38='a',<459>,1:38]], needle=[[@7,28:28='a',<459>,1:28]]}}",
				"{query0={result_value=[[@16,55:66='result_value',<459>,1:55]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,55:66='result_value',<459>,1:55]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,88:88='a',<459>,1:88]], id=[[@1,7:7='a',<459>,1:7]], <tags_col>=[[@11,38:38='a',<459>,1:38]], needle=[[@7,28:28='a',<459>,1:28]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {substitution={name=<tags_col>, type=column}, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsArg2NullV0Test() {
		final String query = "SELECT a.id, array_contains(a.needle, NULL) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={null_literal=null}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@19,80:80='a',<459>,1:80]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]]}}",
				"{query0={result_value=[[@14,47:58='result_value',<459>,1:47]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@14,47:58='result_value',<459>,1:47]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@19,80:80='a',<459>,1:80]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsUnqualifiedOperandsV0Test() {
		final String query = "SELECT a.id, array_contains(needle, tags) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=null}}, 2={column={name=tags, table_ref=null}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@17,78:78='a',<459>,1:78]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:33='needle',<459>,1:28]], tags=[[@9,36:39='tags',<459>,1:36]]}}",
				"{query0={result_value=[[@12,45:56='result_value',<459>,1:45]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@12,45:56='result_value',<459>,1:45]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@17,78:78='a',<459>,1:78]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:33='needle',<459>,1:28]], tags=[[@9,36:39='tags',<459>,1:36]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=events}, {name=tags, table_ref=events}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsAllPredicandVariablesV0Test() {
		final String query = "SELECT a.id, array_contains(<needle_predicand>, <tags_predicand>) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={substitution={name=<needle_predicand>, type=predicand}}, 2={substitution={name=<tags_predicand>, type=predicand}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<tags_predicand>=predicand, <needle_predicand>=predicand}",
				"{events={active=[[@17,102:102='a',<459>,1:102]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@12,69:80='result_value',<459>,1:69]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@12,69:80='result_value',<459>,1:69]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@17,102:102='a',<459>,1:102]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=<needle_predicand>, type=predicand}, {name=<tags_predicand>, type=predicand}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsAllColumnVariablesV0Test() {
		final String query = "SELECT a.id, array_contains(a.<needle_column>, a.<tags_column>) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={substitution={name=<needle_column>, type=column}, table_ref=a}}, 2={column={substitution={name=<tags_column>, type=column}, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<needle_column>=column, <tags_column>=column}",
				"{events={active=[[@21,100:100='a',<459>,1:100]], <needle_column>=[[@7,28:28='a',<459>,1:28]], id=[[@1,7:7='a',<459>,1:7]], <tags_column>=[[@11,47:47='a',<459>,1:47]]}}",
				"{query0={result_value=[[@16,67:78='result_value',<459>,1:67]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,67:78='result_value',<459>,1:67]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,100:100='a',<459>,1:100]], <needle_column>=[[@7,28:28='a',<459>,1:28]], id=[[@1,7:7='a',<459>,1:7]], <tags_column>=[[@11,47:47='a',<459>,1:47]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{substitution={name=<needle_column>, type=column}, table_ref=a}, {substitution={name=<tags_column>, type=column}, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsNestedExpressionV0Test() {
		final String query = "SELECT a.id, coalesce(array_contains(a.needle, a.tags), a.fallback_value) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 2,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:22) is recognized as Snowflake-specific grammar.",
						1, 22, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}}, 2={column={name=fallback_value, table_ref=a}}}, function_name=coalesce}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={fallback_value=[[@18,56:56='a',<459>,1:56]], active=[[@28,110:110='a',<459>,1:110]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@9,37:37='a',<459>,1:37]], tags=[[@13,47:47='a',<459>,1:47]]}}",
				"{query0={result_value=[[@23,77:88='result_value',<459>,1:77]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@23,77:88='result_value',<459>,1:77]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={fallback_value=[[@18,56:56='a',<459>,1:56]], active=[[@28,110:110='a',<459>,1:110]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@9,37:37='a',<459>,1:37]], tags=[[@13,47:47='a',<459>,1:47]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=a}, {name=fallback_value, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsDerivedOutputV0Test() {
		final String query = "SELECT q.id, q.result_value FROM (SELECT a.id, array_contains(a.needle, a.tags) AS result_value FROM events a WHERE a.active = 1) q WHERE q.result_value IS NOT NULL ORDER BY q.id";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:47) is recognized as Snowflake-specific grammar.",
						1, 47, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=q}}, 2={column={name=result_value, table_ref=q}}}, orderby={1={null_order=null, predicand={column={name=id, table_ref=q}}, sort_order=ASC}}, from={table={alias=q, query={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}, where={condition={left={column={name=result_value, table_ref=q}}, operator=IS NOT NULL}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@31,116:116='a',<459>,1:116]], id=[[@11,41:41='a',<459>,1:41]], needle=[[@17,62:62='a',<459>,1:62]], tags=[[@21,72:72='a',<459>,1:72]]}}",
				"{query0={result_value=[[@26,83:94='result_value',<459>,1:83], [@5,13:13='q',<459>,1:13], [@39,138:138='q',<459>,1:138]], id=[[@13,43:44='id',<459>,1:43], [@1,7:7='q',<459>,1:7], [@47,174:174='q',<459>,1:174]]}, query1={result_value=[[@7,15:26='result_value',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query1={query_dictionary={result_value=[[@7,15:26='result_value',<459>,1:15]], id=[[@3,9:10='id',<459>,1:9]]}, def_query0={query_dictionary={result_value=[[@26,83:94='result_value',<459>,1:83], [@5,13:13='q',<459>,1:13], [@39,138:138='q',<459>,1:138]], id=[[@13,43:44='id',<459>,1:43], [@1,7:7='q',<459>,1:7], [@47,174:174='q',<459>,1:174]]}, table_dictionary={events={active=[[@31,116:116='a',<459>,1:116]], id=[[@11,41:41='a',<459>,1:41]], needle=[[@17,62:62='a',<459>,1:62]], tags=[[@21,72:72='a',<459>,1:72]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, ordered_by=[{name=id, table_ref=q}], filters=[{name=result_value, table_ref=q}], interface={result_value=[{name=result_value, table_ref=q}], id=[{name=id, table_ref=q}]}, table_alias={q=query0}}}");
	}

	@Test
	public void arC026_array_containsSetOpSiblingIsolationV0Test() {
		final String query = "SELECT a.id AS left_id, array_contains(a.needle, a.tags) AS left_value FROM events a WHERE a.active = 1 UNION ALL SELECT b.id AS right_id, b.other_value AS right_value FROM archive_events b WHERE b.archived = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:24) is recognized as Snowflake-specific grammar.",
						1, 24, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={union={1={select={1={column={name=id, table_ref=a}, alias=left_id}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=left_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={select={1={column={name=id, table_ref=b}, alias=right_id}, 2={column={name=other_value, table_ref=b}, alias=right_value}}, from={table={alias=b, table=archive_events}}, where={condition={left={column={name=archived, table_ref=b}}, right={literal=1}, operator==}}}}}}",
				"[left_value, left_id]",
				"{}",
				"{archive_events={archived=[[@46,196:196='b',<459>,1:196]], other_value=[[@37,139:139='b',<459>,1:139]], id=[[@31,121:121='b',<459>,1:121]]}, events={active=[[@23,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@9,39:39='a',<459>,1:39]], tags=[[@13,49:49='a',<459>,1:49]]}}",
				"{query0={left_id=[[@5,15:21='left_id',<459>,1:15]], left_value=[[@18,60:69='left_value',<459>,1:60]]}, query1={right_id=[[@35,129:136='right_id',<459>,1:129]], right_value=[[@41,156:166='right_value',<459>,1:156]]}}",
				"{def_union2={def_query1={query_dictionary={right_id=[[@35,129:136='right_id',<459>,1:129]], right_value=[[@41,156:166='right_value',<459>,1:156]]}, table_dictionary={archive_events={archived=[[@46,196:196='b',<459>,1:196]], other_value=[[@37,139:139='b',<459>,1:139]], id=[[@31,121:121='b',<459>,1:121]]}}, setop=UNION, filters=[{name=archived, table_ref=b}], interface={right_id=[{name=id, table_ref=b}], right_value=[{name=other_value, table_ref=b}]}, table_alias={b=archive_events}}, def_query0={query_dictionary={left_value=[[@18,60:69='left_value',<459>,1:60]], left_id=[[@5,15:21='left_id',<459>,1:15]]}, table_dictionary={events={active=[[@23,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@9,39:39='a',<459>,1:39]], tags=[[@13,49:49='a',<459>,1:49]]}}, filters=[{name=active, table_ref=a}], interface={left_value=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], left_id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={left_value=query_column, left_id=query_column}}}");
	}

	@Test
	public void arC026_array_containsQualifiedUdfNeutralV0Test() {
		final String query = "SELECT a.id, util.array_contains(a.needle, a.tags) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={schema=util, parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@23,87:87='a',<459>,1:87]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@9,33:33='a',<459>,1:33]], tags=[[@13,43:43='a',<459>,1:43]]}}",
				"{query0={result_value=[[@18,54:65='result_value',<459>,1:54]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,54:65='result_value',<459>,1:54]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@23,87:87='a',<459>,1:87]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@9,33:33='a',<459>,1:33]], tags=[[@13,43:43='a',<459>,1:43]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsSameNamedColumnNeutralV0Test() {
		final String query = "SELECT a.id, a.array_contains AS stored_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 0,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor);
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=array_contains, table_ref=a}, alias=stored_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[id, stored_value]",
				"{}",
				"{events={active=[[@14,66:66='a',<459>,1:66]], array_contains=[[@5,13:13='a',<459>,1:13]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], stored_value=[[@9,33:44='stored_value',<459>,1:33]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], stored_value=[[@9,33:44='stored_value',<459>,1:33]]}, table_dictionary={events={active=[[@14,66:66='a',<459>,1:66]], array_contains=[[@5,13:13='a',<459>,1:13]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], stored_value=[{name=array_contains, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsUppercaseV0Test() {
		final String query = "SELECT a.id, ARRAY_CONTAINS(a.needle, a.tags) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'ARRAY_CONTAINS' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=ARRAY_CONTAINS}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@21,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]], tags=[[@11,38:38='a',<459>,1:38]]}}",
				"{query0={result_value=[[@16,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]], tags=[[@11,38:38='a',<459>,1:38]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsMixedDialectV0Test() {
		final String query = "SELECT a.id, array_contains(a.needle, a.tags) AS result_value, extract(isodow from a.created_at) AS weekday FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"STATEMENT_MIXED_DIALECT_GRAMMAR",
						"Statement mixes Snowflake-only and PostgreSQL-only grammar at (l:1 c:63); unlikely to run on either engine.",
						1, 63, "SqlASTWalkerHelper", null, null, false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}, 3={alias=weekday, datetime_function={function_name=extract, part_form=KEYWORD, part=isodow, source={column={name=created_at, table_ref=a}}}}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[weekday, result_value, id]",
				"{}",
				"{events={active=[[@32,128:128='a',<459>,1:128]], created_at=[[@22,83:83='a',<459>,1:83]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]], tags=[[@11,38:38='a',<459>,1:38]]}}",
				"{query0={weekday=[[@27,100:106='weekday',<459>,1:100]], result_value=[[@16,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={weekday=[[@27,100:106='weekday',<459>,1:100]], result_value=[[@16,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@32,128:128='a',<459>,1:128]], created_at=[[@22,83:83='a',<459>,1:83]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]], tags=[[@11,38:38='a',<459>,1:38]]}}, filters=[{name=active, table_ref=a}], interface={weekday=[{name=created_at, table_ref=a}], result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsTrailingCommaSyntaxV0Test() {
		final String query = "SELECT array_contains(a.needle, a.tags,) AS result_value FROM events a";
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
						"Line 1:39 - unexpected input: ')' in rule value_expression: SELECT array_contains(a.needle, a.tags,) AS result_value FROM events a",
						1, 39, "ParseErrorCollector", "value_expression", ")", false, "parse.strategy", "NoViableAltException", java.util.Map.of("contextSnippet", "SELECT array_contains(a.needle, a.tags,) AS result_value FROM events a", "parserRules", "value_expression,select_item,select_list", "syntaxClass", "GRAMMAR_GAP")),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER",
						"Line 1:39 - Syntax error, attempting recovery",
						1, 39, "ParseErrorCollector", null, ")", true, "parse.strategy", "NoViableAltException", null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER_INLINE",
						"Line 1:21 - Invalid syntax near '('",
						1, 21, "ParseErrorCollector", null, "(", true, "parse.strategy", null, null)), diagnostics);
	}

	@Test
	public void arC026_array_containsMissingRightParenthesisSyntaxV0Test() {
		final String query = "SELECT array_contains(a.needle, a.tags AS result_value FROM events a";
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
						"Line 1:39 - unexpected input: 'AS' in rule value_expression: SELECT array_contains(a.needle, a.tags AS result_value FROM events a",
						1, 39, "ParseErrorCollector", "value_expression", "AS", false, "parse.strategy", "NoViableAltException", java.util.Map.of("contextSnippet", "SELECT array_contains(a.needle, a.tags AS result_value FROM events a", "parserRules", "value_expression,select_item,select_list", "syntaxClass", "GRAMMAR_GAP")),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER",
						"Line 1:39 - Syntax error, attempting recovery",
						1, 39, "ParseErrorCollector", null, "AS", true, "parse.strategy", "NoViableAltException", null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER_INLINE",
						"Line 1:21 - Invalid syntax near '('",
						1, 21, "ParseErrorCollector", null, "(", true, "parse.strategy", null, null)), diagnostics);
	}

	@Test
	public void arC026_array_containsMissingCommaSyntaxV0Test() {
		final String query = "SELECT array_contains(a.needle a.tags) AS result_value FROM events a";
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
						"Line 1:31 - unexpected input: 'a' in rule value_expression: SELECT array_contains(a.needle a.tags) AS result_value FROM events a",
						1, 31, "ParseErrorCollector", "value_expression", "a", false, "parse.strategy", "NoViableAltException", java.util.Map.of("contextSnippet", "SELECT array_contains(a.needle a.tags) AS result_value FROM events a", "parserRules", "value_expression,select_item,select_list", "syntaxClass", "GRAMMAR_GAP")),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER",
						"Line 1:31 - Syntax error, attempting recovery",
						1, 31, "ParseErrorCollector", null, "a", true, "parse.strategy", "NoViableAltException", null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"RECOVER_INLINE",
						"Line 1:21 - Invalid syntax near '('",
						1, 21, "ParseErrorCollector", null, "(", true, "parse.strategy", null, null)), diagnostics);
	}

	@Test
	public void arC026_array_containsVariantPostfixCastV0Test() {
		final String query = "SELECT a.id, array_contains(a.needle::VARIANT, a.tags) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={function={function_name=cast, data_type={type=VARIANT}, type=CAST, value={column={name=needle, table_ref=a}}}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@23,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]], tags=[[@13,47:47='a',<459>,1:47]]}}",
				"{query0={result_value=[[@18,58:69='result_value',<459>,1:58]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,58:69='result_value',<459>,1:58]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@23,91:91='a',<459>,1:91]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]], tags=[[@13,47:47='a',<459>,1:47]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsVariantCastFunctionV0Test() {
		final String query = "SELECT a.id, array_contains(CAST(a.needle AS VARIANT), a.tags) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={function={function_name=CAST, data_type={type=VARIANT}, type=CAST, value={column={name=needle, table_ref=a}}}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@26,99:99='a',<459>,1:99]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@9,33:33='a',<459>,1:33]], tags=[[@16,55:55='a',<459>,1:55]]}}",
				"{query0={result_value=[[@21,66:77='result_value',<459>,1:66]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@21,66:77='result_value',<459>,1:66]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@26,99:99='a',<459>,1:99]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@9,33:33='a',<459>,1:33]], tags=[[@16,55:55='a',<459>,1:55]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsVariantSubstitutionV0Test() {
		final String query = "SELECT a.id, array_contains(<needle_expr>::VARIANT, a.tags) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={function={function_name=cast, data_type={type=VARIANT}, type=CAST, value={substitution={name=<needle_expr>, type=predicand}}}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<needle_expr>=predicand}",
				"{events={active=[[@21,96:96='a',<459>,1:96]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@11,52:52='a',<459>,1:52]]}}",
				"{query0={result_value=[[@16,63:74='result_value',<459>,1:63]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,63:74='result_value',<459>,1:63]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,96:96='a',<459>,1:96]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@11,52:52='a',<459>,1:52]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=<needle_expr>, type=predicand}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsVariantQualifiedColumnSubstitutionV0Test() {
		final String query = "SELECT a.id, array_contains(CAST(a.<needle_col> AS VARIANT), a.tags) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={function={function_name=CAST, data_type={type=VARIANT}, type=CAST, value={column={substitution={name=<needle_col>, type=column}, table_ref=a}}}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<needle_col>=column}",
				"{events={<needle_col>=[[@9,33:33='a',<459>,1:33]], active=[[@26,105:105='a',<459>,1:105]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@16,61:61='a',<459>,1:61]]}}",
				"{query0={result_value=[[@21,72:83='result_value',<459>,1:72]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@21,72:83='result_value',<459>,1:72]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={<needle_col>=[[@9,33:33='a',<459>,1:33]], active=[[@26,105:105='a',<459>,1:105]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@16,61:61='a',<459>,1:61]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{substitution={name=<needle_col>, type=column}, table_ref=a}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsVariantPostfixColumnSubstitutionV0Test() {
		final String query = "SELECT a.id, array_contains(a.<needle_col>::VARIANT, a.tags) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={function={function_name=cast, data_type={type=VARIANT}, type=CAST, value={column={substitution={name=<needle_col>, type=column}, table_ref=a}}}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<needle_col>=column}",
				"{events={<needle_col>=[[@7,28:28='a',<459>,1:28]], active=[[@23,97:97='a',<459>,1:97]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@13,53:53='a',<459>,1:53]]}}",
				"{query0={result_value=[[@18,64:75='result_value',<459>,1:64]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,64:75='result_value',<459>,1:64]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={<needle_col>=[[@7,28:28='a',<459>,1:28]], active=[[@23,97:97='a',<459>,1:97]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@13,53:53='a',<459>,1:53]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{substitution={name=<needle_col>, type=column}, table_ref=a}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsVariantParenthesizedSubstitutionV0Test() {
		final String query = "SELECT a.id, array_contains((<needle_expr>::VARIANT), a.tags) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={parentheses={function={function_name=cast, data_type={type=VARIANT}, type=CAST, value={substitution={name=<needle_expr>, type=predicand}}}}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{<needle_expr>=predicand}",
				"{events={active=[[@23,98:98='a',<459>,1:98]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@13,54:54='a',<459>,1:54]]}}",
				"{query0={result_value=[[@18,65:76='result_value',<459>,1:65]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@18,65:76='result_value',<459>,1:65]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@23,98:98='a',<459>,1:98]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@13,54:54='a',<459>,1:54]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=<needle_expr>, type=predicand}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsEmptyArrayV0Test() {
		final String query = "SELECT a.id, array_contains('x'::VARIANT, []) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:42) is recognized as Snowflake-specific grammar.",
						1, 42, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={function={function_name=cast, data_type={type=VARIANT}, type=CAST, value={literal=x}}}, 2={function={function_name=array_construct, parameters={}}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@20,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@15,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@15,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@20,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsSqlNullElementV0Test() {
		final String query = "SELECT a.id, array_contains(NULL, ['x', NULL]) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:34) is recognized as Snowflake-specific grammar.",
						1, 34, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={null_literal=null}, 2={function={function_name=array_construct, parameters={1={literal='x'}, 2={null_literal=null}}}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@21,83:83='a',<459>,1:83]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@16,50:61='result_value',<459>,1:50]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,50:61='result_value',<459>,1:50]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,83:83='a',<459>,1:83]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsJsonNullElementV0Test() {
		final String query = "SELECT a.id, array_contains(parse_json('null'), [parse_json('null'), 'x']) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 3,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:48) is recognized as Snowflake-specific grammar.",
						1, 48, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={function={parameters={1={literal='null'}}, function_name=parse_json}}, 2={function={function_name=array_construct, parameters={1={function={parameters={1={literal='null'}}, function_name=parse_json}}, 2={literal='x'}}}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@27,111:111='a',<459>,1:111]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@22,78:89='result_value',<459>,1:78]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@22,78:89='result_value',<459>,1:78]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@27,111:111='a',<459>,1:111]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsBothNullV0Test() {
		final String query = "SELECT a.id, array_contains(NULL, NULL) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={null_literal=null}, 2={null_literal=null}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@17,76:76='a',<459>,1:76]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@12,43:54='result_value',<459>,1:43]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@12,43:54='result_value',<459>,1:43]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@17,76:76='a',<459>,1:76]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsNumericNeedleV0Test() {
		final String query = "SELECT a.id, array_contains(1, [1, 2]) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:31) is recognized as Snowflake-specific grammar.",
						1, 31, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={literal=1}, 2={function={function_name=array_construct, parameters={1={literal=1}, 2={literal=2}}}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@21,75:75='a',<459>,1:75]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@16,42:53='result_value',<459>,1:42]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,42:53='result_value',<459>,1:42]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,75:75='a',<459>,1:75]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsBooleanNeedleV0Test() {
		final String query = "SELECT a.id, array_contains(TRUE, [TRUE, FALSE]) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:34) is recognized as Snowflake-specific grammar.",
						1, 34, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={literal=TRUE}, 2={function={function_name=array_construct, parameters={1={literal=TRUE}, 2={literal=FALSE}}}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@21,85:85='a',<459>,1:85]], id=[[@1,7:7='a',<459>,1:7]]}}",
				"{query0={result_value=[[@16,52:63='result_value',<459>,1:52]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,52:63='result_value',<459>,1:52]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,85:85='a',<459>,1:85]], id=[[@1,7:7='a',<459>,1:7]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsDateVariantV0Test() {
		final String query = "SELECT a.id, array_contains(DATE '2026-11-19'::VARIANT, a.tags) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={function={function_name=cast, data_type={type=VARIANT}, type=CAST, value={source_type=DATE, literal=2026-11-19}}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@22,100:100='a',<459>,1:100]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@12,56:56='a',<459>,1:56]]}}",
				"{query0={result_value=[[@17,67:78='result_value',<459>,1:67]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@17,67:78='result_value',<459>,1:67]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@22,100:100='a',<459>,1:100]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@12,56:56='a',<459>,1:56]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsUncastStringRuntimeBoundaryV0Test() {
		final String query = "SELECT a.id, array_contains('x', a.tags) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={literal='x'}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@19,77:77='a',<459>,1:77]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@9,33:33='a',<459>,1:33]]}}",
				"{query0={result_value=[[@14,44:55='result_value',<459>,1:44]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@14,44:55='result_value',<459>,1:44]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@19,77:77='a',<459>,1:77]], id=[[@1,7:7='a',<459>,1:7]], tags=[[@9,33:33='a',<459>,1:33]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsOneArgumentCompatibilityV0Test() {
		final String query = "SELECT a.id, array_contains(a.needle) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@17,74:74='a',<459>,1:74]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]]}}",
				"{query0={result_value=[[@12,41:52='result_value',<459>,1:41]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@12,41:52='result_value',<459>,1:41]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@17,74:74='a',<459>,1:74]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsThreeArgumentCompatibilityV0Test() {
		final String query = "SELECT a.id, array_contains(a.needle, a.tags, a.extra_value) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}, 3={column={name=extra_value, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={extra_value=[[@15,46:46='a',<459>,1:46]], active=[[@25,97:97='a',<459>,1:97]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]], tags=[[@11,38:38='a',<459>,1:38]]}}",
				"{query0={result_value=[[@20,64:75='result_value',<459>,1:64]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@20,64:75='result_value',<459>,1:64]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={extra_value=[[@15,46:46='a',<459>,1:46]], active=[[@25,97:97='a',<459>,1:97]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]], tags=[[@11,38:38='a',<459>,1:38]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=a}, {name=extra_value, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsPredicateBooleanV0Test() {
		final String query = "SELECT a.id, a.label FROM events a WHERE array_contains(a.needle::VARIANT, a.tags) AND a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:41) is recognized as Snowflake-specific grammar.",
						1, 41, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={column={name=label, table_ref=a}}}, from={table={alias=a, table=events}}, where={and={1={function={parameters={1={function={function_name=cast, data_type={type=VARIANT}, type=CAST, value={column={name=needle, table_ref=a}}}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}}, 2={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[id, label]",
				"{}",
				"{events={active=[[@25,87:87='a',<459>,1:87]], id=[[@1,7:7='a',<459>,1:7]], label=[[@5,13:13='a',<459>,1:13]], needle=[[@14,56:56='a',<459>,1:56]], tags=[[@20,75:75='a',<459>,1:75]]}}",
				"{query0={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}}",
				"{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]], label=[[@7,15:19='label',<459>,1:15]]}, table_dictionary={events={active=[[@25,87:87='a',<459>,1:87]], id=[[@1,7:7='a',<459>,1:7]], label=[[@5,13:13='a',<459>,1:13]], needle=[[@14,56:56='a',<459>,1:56]], tags=[[@20,75:75='a',<459>,1:75]]}}, filters=[{name=needle, table_ref=a}, {name=tags, table_ref=a}, {name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}], label=[{name=label, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsCaseProjectionV0Test() {
		final String query = "SELECT a.id, CASE WHEN array_contains(a.needle::VARIANT, a.tags) THEN a.label ELSE a.fallback_value END AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:23) is recognized as Snowflake-specific grammar.",
						1, 23, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}, 2={alias=result_value, case={clauses={1={then={column={name=label, table_ref=a}}, when={function={parameters={1={function={function_name=cast, data_type={type=VARIANT}, type=CAST, value={column={name=needle, table_ref=a}}}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}}}}, else={column={name=fallback_value, table_ref=a}}}}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={fallback_value=[[@24,83:83='a',<459>,1:83]], active=[[@34,140:140='a',<459>,1:140]], id=[[@1,7:7='a',<459>,1:7]], label=[[@20,70:70='a',<459>,1:70]], needle=[[@9,38:38='a',<459>,1:38]], tags=[[@15,57:57='a',<459>,1:57]]}}",
				"{query0={result_value=[[@29,107:118='result_value',<459>,1:107]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@29,107:118='result_value',<459>,1:107]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={fallback_value=[[@24,83:83='a',<459>,1:83]], active=[[@34,140:140='a',<459>,1:140]], id=[[@1,7:7='a',<459>,1:7]], label=[[@20,70:70='a',<459>,1:70]], needle=[[@9,38:38='a',<459>,1:38]], tags=[[@15,57:57='a',<459>,1:57]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=label, table_ref=a}, {name=needle, table_ref=a}, {name=tags, table_ref=a}, {name=fallback_value, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsCteRenameChainV0Test() {
		final String query = "WITH seed AS (SELECT a.id, array_contains(a.needle, a.tags) AS result_value FROM events a WHERE a.active = 1), renamed AS (SELECT id, result_value AS exported_value FROM seed) SELECT r.id, r.exported_value FROM renamed r WHERE r.exported_value IS NOT NULL";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:27) is recognized as Snowflake-specific grammar.",
						1, 27, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=seed}, 2={cte={select={1={column={name=id, table_ref=null}}, 2={column={name=result_value, table_ref=null}, alias=exported_value}}, from={table={alias=null, table=seed}}}, alias=renamed}}, query={select={1={column={name=id, table_ref=r}}, 2={column={name=exported_value, table_ref=r}}}, from={table={alias=r, table=renamed}}, where={condition={left={column={name=exported_value, table_ref=r}}, operator=IS NOT NULL}}}}}",
				"[exported_value, id]",
				"{}",
				"{events={active=[[@25,96:96='a',<459>,1:96]], id=[[@5,21:21='a',<459>,1:21]], needle=[[@11,42:42='a',<459>,1:42]], tags=[[@15,52:52='a',<459>,1:52]]}}",
				"{query0={result_value=[[@20,63:74='result_value',<459>,1:63], [@38,134:145='result_value',<459>,1:134]], id=[[@7,23:24='id',<459>,1:23], [@36,130:131='id',<459>,1:130]]}, query1={exported_value=[[@40,150:163='exported_value',<459>,1:150], [@49,189:189='r',<459>,1:189], [@56,227:227='r',<459>,1:227]], id=[[@36,130:131='id',<459>,1:130], [@45,183:183='r',<459>,1:183]]}, query2={exported_value=[[@51,191:204='exported_value',<459>,1:191]], id=[[@47,185:186='id',<459>,1:185]]}}",
				"{def_query2={context_list={seed=query0, renamed=query1, r=query1}, query_dictionary={exported_value=[[@51,191:204='exported_value',<459>,1:191]], id=[[@47,185:186='id',<459>,1:185]]}, def_query1={context_list={seed=query0}, query_dictionary={exported_value=[[@40,150:163='exported_value',<459>,1:150], [@49,189:189='r',<459>,1:189], [@56,227:227='r',<459>,1:227]], id=[[@36,130:131='id',<459>,1:130], [@45,183:183='r',<459>,1:183]]}, interface={exported_value=[{name=result_value, table_ref=query0}], id=[{name=id, table_ref=query0}]}, table_alias={seed=query0}}, def_query0={query_dictionary={result_value=[[@20,63:74='result_value',<459>,1:63], [@38,134:145='result_value',<459>,1:134]], id=[[@7,23:24='id',<459>,1:23], [@36,130:131='id',<459>,1:130]]}, table_dictionary={events={active=[[@25,96:96='a',<459>,1:96]], id=[[@5,21:21='a',<459>,1:21]], needle=[[@11,42:42='a',<459>,1:42]], tags=[[@15,52:52='a',<459>,1:52]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, filters=[{name=exported_value, table_ref=r}], interface={exported_value=[{name=exported_value, table_ref=r}], id=[{name=id, table_ref=r}]}, table_alias={r=query1, seed=query0, renamed=query1}}}");
	}

	@Test
	public void arC026_array_containsCteMissingExportV0Test() {
		final String query = "WITH seed AS (SELECT a.id FROM events a WHERE a.active = 1) SELECT s.id, array_contains(s.needle, s.tags) AS result_value FROM seed s";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:73) is recognized as Snowflake-specific grammar.",
						1, 73, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
						"Qualified column 'needle' at (l:1 c:88) was not found in output interface of query alias 's'.",
						1, 88, "SqlASTWalkerHelper", null, "s.needle", false, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.FATAL,
						"QUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIAS",
						"Qualified column 'tags' at (l:1 c:98) was not found in output interface of query alias 's'.",
						1, 98, "SqlASTWalkerHelper", null, "s.tags", false, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={with={1={cte={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, alias=seed}}, query={select={1={column={name=id, table_ref=s}}, 2={function={parameters={1={column={name=needle, table_ref=s}}, 2={column={name=tags, table_ref=s}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=s, table=seed}}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@12,46:46='a',<459>,1:46]], id=[[@5,21:21='a',<459>,1:21]]}}",
				"{query0={id=[[@7,23:24='id',<459>,1:23], [@19,67:67='s',<459>,1:67]]}, query1={result_value=[[@34,109:120='result_value',<459>,1:109]], id=[[@21,69:70='id',<459>,1:69]]}}",
				"{def_query1={context_list={seed=query0, s=query0}, query_dictionary={result_value=[[@34,109:120='result_value',<459>,1:109]], id=[[@21,69:70='id',<459>,1:69]]}, def_query0={query_dictionary={id=[[@7,23:24='id',<459>,1:23], [@19,67:67='s',<459>,1:67]]}, table_dictionary={events={active=[[@12,46:46='a',<459>,1:46]], id=[[@5,21:21='a',<459>,1:21]]}}, filters=[{name=active, table_ref=a}], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={result_value=[{name=needle, table_ref=s}, {name=tags, table_ref=s}], id=[{name=id, table_ref=s}]}, table_alias={s=query0, seed=query0}}}");
	}

	@Test
	public void arC026_array_containsCorrelatedSubqueryV0Test() {
		final String query = "SELECT a.id FROM events a WHERE EXISTS (SELECT b.id FROM orders b WHERE b.event_id = a.id AND array_contains(a.needle, a.tags) IS NOT NULL)";
		final SQLSelectParserParser parser = parse(query);
		final SQLSelectParserParser.SqlContext tree = parser.sql();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:94) is recognized as Snowflake-specific grammar.",
						1, 94, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SQL={select={1={column={name=id, table_ref=a}}}, from={table={alias=a, table=events}}, where={exists={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=orders}}, where={and={1={condition={left={column={name=event_id, table_ref=b}}, right={column={name=id, table_ref=a}}, operator==}}, 2={condition={left={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}}, operator=IS NOT NULL}}}}, operator=EXISTS}}}}",
				"[id]",
				"{}",
				"{orders={event_id=[[@18,72:72='b',<459>,1:72]], id=[[@11,47:47='b',<459>,1:47]]}, events={id=[[@1,7:7='a',<459>,1:7], [@22,85:85='a',<459>,1:85]], needle=[[@28,109:109='a',<459>,1:109]], tags=[[@32,119:119='a',<459>,1:119]]}}",
				"{query0={id=[[@13,49:50='id',<459>,1:49]]}, query2={id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query2={query_dictionary={id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={id=[[@1,7:7='a',<459>,1:7], [@22,85:85='a',<459>,1:85]], needle=[[@28,109:109='a',<459>,1:109]], tags=[[@32,119:119='a',<459>,1:119]]}}, dependent_queries={exists1={query=query0, type=filters}}, def_query0={query_dictionary={id=[[@13,49:50='id',<459>,1:49]]}, table_dictionary={orders={event_id=[[@18,72:72='b',<459>,1:72]], id=[[@11,47:47='b',<459>,1:47]]}}, filters=[{name=event_id, table_ref=b}, {name=id, table_ref=a}, {name=needle, table_ref=a}, {name=tags, table_ref=a}], interface={id=[{name=id, table_ref=b}]}, table_alias={b=orders}}, filters=[], interface={id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
	}

	@Test
	public void arC026_array_containsPREDICANDSnippetV0Test() {
		final String query = "array_contains(<needle_expr>, <tags_expr>)";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.predicand_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:0) is recognized as Snowflake-specific grammar.",
						1, 0, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{PREDICAND={function={parameters={1={substitution={name=<needle_expr>, type=predicand}}, 2={substitution={name=<tags_expr>, type=predicand}}}, function_name=array_contains}}}",
				"[]",
				"{<needle_expr>=predicand, <tags_expr>=predicand}",
				"{}",
				"{}",
				"{}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "PREDICAND");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:0) is recognized as Snowflake-specific grammar.",
						1, 0, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{PREDICAND={function={parameters={1={substitution={name=<needle_expr>, type=predicand}}, 2={substitution={name=<tags_expr>, type=predicand}}}, function_name=array_contains}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{<needle_expr>=predicand, <tags_expr>=predicand}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC026_array_containsCONDITIONSnippetV0Test() {
		final String query = "array_contains(<needle_expr>, <tags_expr>) IS NOT NULL AND 1 = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.condition_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:0) is recognized as Snowflake-specific grammar.",
						1, 0, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{CONDITION={and={1={condition={left={function={parameters={1={substitution={name=<needle_expr>, type=predicand}}, 2={substitution={name=<tags_expr>, type=predicand}}}, function_name=array_contains}}, operator=IS NOT NULL}}, 2={condition={left={literal=1}, right={literal=1}, operator==}}}}}",
				"[]",
				"{<needle_expr>=predicand, <tags_expr>=predicand}",
				"{}",
				"{}",
				"{}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "CONDITION");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:0) is recognized as Snowflake-specific grammar.",
						1, 0, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{CONDITION={and={1={condition={left={function={parameters={1={substitution={name=<needle_expr>, type=predicand}}, 2={substitution={name=<tags_expr>, type=predicand}}}, function_name=array_contains}}, operator=IS NOT NULL}}, 2={condition={left={literal=1}, right={literal=1}, operator==}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{<needle_expr>=predicand, <tags_expr>=predicand}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC026_array_containsQUERYSnippetV0Test() {
		final String query = "SELECT a.id, array_contains(a.needle, a.tags) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.query_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{QUERY={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@21,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]], tags=[[@11,38:38='a',<459>,1:38]]}}",
				"{query0={result_value=[[@16,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query0={query_dictionary={result_value=[[@16,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]], tags=[[@11,38:38='a',<459>,1:38]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "QUERY");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{QUERY={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[result_value, id]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={active=[[@21,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]], tags=[[@11,38:38='a',<459>,1:38]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{query0={result_value=[[@16,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_query0={query_dictionary={result_value=[[@16,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]], tags=[[@11,38:38='a',<459>,1:38]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC026_array_containsTUPLESnippetV0Test() {
		final String query = "(SELECT a.id, array_contains(a.needle, a.tags) AS result_value FROM events a WHERE a.active = 1)";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.tuple_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:14) is recognized as Snowflake-specific grammar.",
						1, 14, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{TUPLE={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}}}",
				"[result_value, id]",
				"{}",
				"{events={active=[[@22,83:83='a',<459>,1:83]], id=[[@2,8:8='a',<459>,1:8]], needle=[[@8,29:29='a',<459>,1:29]], tags=[[@12,39:39='a',<459>,1:39]]}}",
				"{query0={result_value=[[@17,50:61='result_value',<459>,1:50]], id=[[@4,10:11='id',<459>,1:10]]}}",
				"{def_query0={query_dictionary={result_value=[[@17,50:61='result_value',<459>,1:50]], id=[[@4,10:11='id',<459>,1:10]]}, table_dictionary={events={active=[[@22,83:83='a',<459>,1:83]], id=[[@2,8:8='a',<459>,1:8]], needle=[[@8,29:29='a',<459>,1:29]], tags=[[@12,39:39='a',<459>,1:39]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, table_alias={query0=query0}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "TUPLE");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:14) is recognized as Snowflake-specific grammar.",
						1, 14, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{TUPLE={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[result_value, id]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={active=[[@22,83:83='a',<459>,1:83]], id=[[@2,8:8='a',<459>,1:8]], needle=[[@8,29:29='a',<459>,1:29]], tags=[[@12,39:39='a',<459>,1:39]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{query0={result_value=[[@17,50:61='result_value',<459>,1:50]], id=[[@4,10:11='id',<459>,1:10]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_query0={query_dictionary={result_value=[[@17,50:61='result_value',<459>,1:50]], id=[[@4,10:11='id',<459>,1:10]]}, table_dictionary={events={active=[[@22,83:83='a',<459>,1:83]], id=[[@2,8:8='a',<459>,1:8]], needle=[[@8,29:29='a',<459>,1:29]], tags=[[@12,39:39='a',<459>,1:39]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, table_alias={query0=query0}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC026_array_containsIN_LISTSnippetV0Test() {
		final String query = "(array_contains(<needle_expr>, <tags_expr>), NULL)";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.in_list_predicate_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:1) is recognized as Snowflake-specific grammar.",
						1, 1, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{IN_LIST={list={1={function={parameters={1={substitution={name=<needle_expr>, type=predicand}}, 2={substitution={name=<tags_expr>, type=predicand}}}, function_name=array_contains}}, 2={null_literal=null}}}}",
				"[]",
				"{<needle_expr>=predicand, <tags_expr>=predicand}",
				"{}",
				"{}",
				"{}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "IN_LIST");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:1) is recognized as Snowflake-specific grammar.",
						1, 1, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{IN_LIST={list={1={function={parameters={1={substitution={name=<needle_expr>, type=predicand}}, 2={substitution={name=<tags_expr>, type=predicand}}}, function_name=array_contains}}, 2={null_literal=null}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{<needle_expr>=predicand, <tags_expr>=predicand}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC026_array_containsJOIN_EXTENSIONSnippetV0Test() {
		final String query = "JOIN events a ON array_contains(a.needle, a.tags) IS NOT NULL AND a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.join_extension_value();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:17) is recognized as Snowflake-specific grammar.",
						1, 17, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{JOIN_EXTENSION={1={join=JOIN, on={and={1={condition={left={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}}, operator=IS NOT NULL}}, 2={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}, 2={table={alias=a, table=events}}}}",
				"[]",
				"{}",
				"{events={active=[[@18,66:66='a',<459>,1:66]], needle=[[@6,32:32='a',<459>,1:32]], tags=[[@10,42:42='a',<459>,1:42]]}}",
				"{}",
				"{table_dictionary={events={active=[[@18,66:66='a',<459>,1:66]], needle=[[@6,32:32='a',<459>,1:32]], tags=[[@10,42:42='a',<459>,1:42]]}}, filters=[{name=needle, table_ref=a}, {name=tags, table_ref=a}, {name=active, table_ref=a}], table_alias={a=events}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "JOIN_EXTENSION");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:17) is recognized as Snowflake-specific grammar.",
						1, 17, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{JOIN_EXTENSION={1={join=JOIN, on={and={1={condition={left={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}}, operator=IS NOT NULL}}, 2={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}, 2={table={alias=a, table=events}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={active=[[@18,66:66='a',<459>,1:66]], needle=[[@6,32:32='a',<459>,1:32]], tags=[[@10,42:42='a',<459>,1:42]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{table_dictionary={events={active=[[@18,66:66='a',<459>,1:66]], needle=[[@6,32:32='a',<459>,1:32]], tags=[[@10,42:42='a',<459>,1:42]]}}, filters=[{name=needle, table_ref=a}, {name=tags, table_ref=a}, {name=active, table_ref=a}], table_alias={a=events}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC026_array_containsVALUESSnippetV0Test() {
		final String query = "VALUES (7, array_contains('x'::VARIANT, ['x', 'y']))";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.values_statement_end();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"array literal at (l:1 c:40) is recognized as Snowflake-specific grammar.",
						1, 40, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{VALUES={values={matrix={1={row={1={literal=7}, 2={function={parameters={1={function={function_name=cast, data_type={type=VARIANT}, type=CAST, value={literal=x}}}, 2={function={function_name=array_construct, parameters={1={literal='x'}, 2={literal='y'}}}}}, function_name=array_contains}}}}}}}}",
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
						"array literal at (l:1 c:40) is recognized as Snowflake-specific grammar.",
						1, 40, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{VALUES={values={matrix={1={row={1={literal=7}, 2={function={parameters={1={function={function_name=cast, data_type={type=VARIANT}, type=CAST, value={literal=x}}}, 2={function={function_name=array_construct, parameters={1={literal='x'}, 2={literal='y'}}}}}, function_name=array_contains}}}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[$1, $2]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{values0={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_values0={query_dictionary={$1=[[@1,7:7='(',<287>,1:7]], $2=[[@1,7:7='(',<287>,1:7]]}, interface={$1=[], $2=[]}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC026_array_containsINSERTSnippetV0Test() {
		final String query = "INSERT INTO results (id, result_value) SELECT a.id, array_contains(a.needle, a.tags) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.insert_end_point();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:52) is recognized as Snowflake-specific grammar.",
						1, 52, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{INSERT={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}}, target_table={table={alias=null, table=results}}, columns={1={column={name=id, table_ref=null}}, 2={column={name=result_value, table_ref=null}}}}}}",
				"[result_value, id]",
				"{}",
				"{results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, events={active=[[@29,121:121='a',<459>,1:121]], id=[[@9,46:46='a',<459>,1:46]], needle=[[@15,67:67='a',<459>,1:67]], tags=[[@19,77:77='a',<459>,1:77]]}}",
				"{query0={result_value=[[@24,88:99='result_value',<459>,1:88]], id=[[@11,48:49='id',<459>,1:48]]}, insert1={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}",
				"{def_insert1={query_dictionary={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, table_dictionary={results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}, def_query0={query_dictionary={result_value=[[@24,88:99='result_value',<459>,1:88]], id=[[@11,48:49='id',<459>,1:48]]}, table_dictionary={events={active=[[@29,121:121='a',<459>,1:121]], id=[[@9,46:46='a',<459>,1:46]], needle=[[@15,67:67='a',<459>,1:67]], tags=[[@19,77:77='a',<459>,1:77]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={id=[{name=id, table_ref=query0}], result_value=[{name=result_value, table_ref=query0}]}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "INSERT");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:52) is recognized as Snowflake-specific grammar.",
						1, 52, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{INSERT={insert={preamble=insert_into, from={from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}, select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}}, target_table={table={alias=null, table=results}}, columns={1={column={name=id, table_ref=null}}, 2={column={name=result_value, table_ref=null}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[result_value, id]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, events={active=[[@29,121:121='a',<459>,1:121]], id=[[@9,46:46='a',<459>,1:46]], needle=[[@15,67:67='a',<459>,1:67]], tags=[[@19,77:77='a',<459>,1:77]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{query0={result_value=[[@24,88:99='result_value',<459>,1:88]], id=[[@11,48:49='id',<459>,1:48]]}, insert1={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_insert1={query_dictionary={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}, table_dictionary={results={result_value=[[@6,25:36='result_value',<459>,1:25]], id=[[@4,21:22='id',<459>,1:21]]}}, def_query0={query_dictionary={result_value=[[@24,88:99='result_value',<459>,1:88]], id=[[@11,48:49='id',<459>,1:48]]}, table_dictionary={events={active=[[@29,121:121='a',<459>,1:121]], id=[[@9,46:46='a',<459>,1:46]], needle=[[@15,67:67='a',<459>,1:67]], tags=[[@19,77:77='a',<459>,1:77]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}, interface={id=[{name=id, table_ref=query0}], result_value=[{name=result_value, table_ref=query0}]}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC026_array_containsUPDATESnippetV0Test() {
		final String query = "UPDATE events SET result_value = array_contains(needle, tags) WHERE active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.update_end_point();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:33) is recognized as Snowflake-specific grammar.",
						1, 33, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{UPDATE={update={table={alias=null, table=events}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}, assignments={1={set={column={name=result_value, table_ref=null}}, to={function={parameters={1={column={name=needle, table_ref=null}}, 2={column={name=tags, table_ref=null}}}, function_name=array_contains}}}}}}}",
				"[result_value]",
				"{}",
				"{events={active=[[@12,68:73='active',<459>,1:68]], needle=[[@7,48:53='needle',<459>,1:48], [@10,60:60=')',<288>,1:60]], tags=[[@9,56:59='tags',<459>,1:56], [@10,60:60=')',<288>,1:60]]}}",
				"{update0={result_value=[[@3,18:29='result_value',<459>,1:18]]}}",
				"{def_update0={assignments={result_value=[{name=needle, table_ref=events}, {name=tags, table_ref=events}]}, table_dictionary={events={active=[[@12,68:73='active',<459>,1:68]], needle=[[@7,48:53='needle',<459>,1:48], [@10,60:60=')',<288>,1:60]], tags=[[@9,56:59='tags',<459>,1:56], [@10,60:60=')',<288>,1:60]]}}, update_dictionary={result_value=[[@3,18:29='result_value',<459>,1:18]]}, target_table={events={result_value=[[@3,18:29='result_value',<459>,1:18]]}}, filters=[{name=active, table_ref=events}], lhs_unresolved_columns={result_value={column={name=result_value, table_ref=null}, locations=[[@3,18:29='result_value',<459>,1:18]]}}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "UPDATE");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:33) is recognized as Snowflake-specific grammar.",
						1, 33, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{UPDATE={update={table={alias=null, table=events}, where={condition={left={column={name=active, table_ref=null}}, right={literal=1}, operator==}}, assignments={1={set={column={name=result_value, table_ref=null}}, to={function={parameters={1={column={name=needle, table_ref=null}}, 2={column={name=tags, table_ref=null}}}, function_name=array_contains}}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[result_value]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={active=[[@12,68:73='active',<459>,1:68]], needle=[[@7,48:53='needle',<459>,1:48], [@10,60:60=')',<288>,1:60]], tags=[[@9,56:59='tags',<459>,1:56], [@10,60:60=')',<288>,1:60]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{update0={result_value=[[@3,18:29='result_value',<459>,1:18]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_update0={assignments={result_value=[{name=needle, table_ref=events}, {name=tags, table_ref=events}]}, table_dictionary={events={active=[[@12,68:73='active',<459>,1:68]], needle=[[@7,48:53='needle',<459>,1:48], [@10,60:60=')',<288>,1:60]], tags=[[@9,56:59='tags',<459>,1:56], [@10,60:60=')',<288>,1:60]]}}, update_dictionary={result_value=[[@3,18:29='result_value',<459>,1:18]]}, target_table={events={result_value=[[@3,18:29='result_value',<459>,1:18]]}}, filters=[{name=active, table_ref=events}], lhs_unresolved_columns={result_value={column={name=result_value, table_ref=null}, locations=[[@3,18:29='result_value',<459>,1:18]]}}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC026_array_containsDELETESnippetV0Test() {
		final String query = "DELETE FROM events WHERE array_contains(needle, tags) IS NULL AND active = 0";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.delete_end_point();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:25) is recognized as Snowflake-specific grammar.",
						1, 25, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{DELETE={delete={table={alias=null, table=events}, where={and={1={condition={left={function={parameters={1={column={name=needle, table_ref=null}}, 2={column={name=tags, table_ref=null}}}, function_name=array_contains}}, operator=IS NULL}}, 2={condition={left={column={name=active, table_ref=null}}, right={literal=0}, operator==}}}}}}}",
				"[]",
				"{}",
				"{events={active=[[@13,66:71='active',<459>,1:66]], needle=[[@6,40:45='needle',<459>,1:40]], tags=[[@8,48:51='tags',<459>,1:48]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={active=[[@13,66:71='active',<459>,1:66]], needle=[[@6,40:45='needle',<459>,1:40]], tags=[[@8,48:51='tags',<459>,1:48]]}}, filters=[{name=needle, table_ref=events}, {name=tags, table_ref=events}, {name=active, table_ref=events}], interface=null}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "DELETE");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:25) is recognized as Snowflake-specific grammar.",
						1, 25, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{DELETE={delete={table={alias=null, table=events}, where={and={1={condition={left={function={parameters={1={column={name=needle, table_ref=null}}, 2={column={name=tags, table_ref=null}}}, function_name=array_contains}}, operator=IS NULL}}, 2={condition={left={column={name=active, table_ref=null}}, right={literal=0}, operator==}}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={active=[[@13,66:71='active',<459>,1:66]], needle=[[@6,40:45='needle',<459>,1:40]], tags=[[@8,48:51='tags',<459>,1:48]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_delete0={query_dictionary={}, table_dictionary={events={active=[[@13,66:71='active',<459>,1:66]], needle=[[@6,40:45='needle',<459>,1:40]], tags=[[@8,48:51='tags',<459>,1:48]]}}, filters=[{name=needle, table_ref=events}, {name=tags, table_ref=events}, {name=active, table_ref=events}], interface=null}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC026_array_containsDDLSnippetV0Test() {
		final String query = "CREATE VIEW v_array_contains AS SELECT a.id, array_contains(a.needle, a.tags) AS result_value FROM events a WHERE a.active = 1";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.ddl();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 1,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:45) is recognized as Snowflake-specific grammar.",
						1, 45, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{DDL={create={type=VIEW, name={table=v_array_contains}, query={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}",
				"[]",
				"{}",
				"{events={active=[[@25,114:114='a',<459>,1:114]], id=[[@5,39:39='a',<459>,1:39]], needle=[[@11,60:60='a',<459>,1:60]], tags=[[@15,70:70='a',<459>,1:70]]}}",
				"{query0={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@7,41:42='id',<459>,1:41]]}}",
				"{def_create1={def_query0={query_dictionary={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@7,41:42='id',<459>,1:41]]}, table_dictionary={events={active=[[@25,114:114='a',<459>,1:114]], id=[[@5,39:39='a',<459>,1:39]], needle=[[@11,60:60='a',<459>,1:60]], tags=[[@15,70:70='a',<459>,1:70]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "DDL");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:45) is recognized as Snowflake-specific grammar.",
						1, 45, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{DDL={create={type=VIEW, name={table=v_array_contains}, query={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{events={active=[[@25,114:114='a',<459>,1:114]], id=[[@5,39:39='a',<459>,1:39]], needle=[[@11,60:60='a',<459>,1:60]], tags=[[@15,70:70='a',<459>,1:70]]}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{query0={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@7,41:42='id',<459>,1:41]]}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{def_create1={def_query0={query_dictionary={result_value=[[@20,81:92='result_value',<459>,1:81]], id=[[@7,41:42='id',<459>,1:41]]}, table_dictionary={events={active=[[@25,114:114='a',<459>,1:114]], id=[[@5,39:39='a',<459>,1:39]], needle=[[@11,60:60='a',<459>,1:60]], tags=[[@15,70:70='a',<459>,1:70]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}", accessParser.getSnippet().getSymbolTable().toString());
	}

	@Test
	public void arC026_array_containsSCRIPTSnippetV0Test() {
		final String query = "SELECT a.id, array_contains(a.needle, a.tags) AS result_value FROM events a WHERE a.active = 1;\nSELECT b.id FROM orders b;\nSELECT a.id, array_contains(a.needle, a.tags) AS result_value FROM events a WHERE a.active = 1;";
		final SQLSelectParserParser parser = parse(query);
		final org.antlr.v4.runtime.ParserRuleContext tree = parser.script();
		Assert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());
		Assert.assertEquals("Unexpected primary array function routing", 2,
				Trees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_routine_invocation).size());
		final SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);
		assertNoFatalErrors(extractor);
		assertExactDiagnostics(extractor,
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:3 c:13) is recognized as Snowflake-specific grammar.",
						3, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null));
		assertOutputs(extractor,
				"{SCRIPT={1={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, 2={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=orders}}}, 3={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}",
				"[]",
				"{SCRIPT={1={}, 2={}, 3={}}}",
				"{SCRIPT={1={events={active=[[@21,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]], tags=[[@11,38:38='a',<459>,1:38]]}}, 2={orders={id=[[@28,103:103='b',<459>,2:7]]}}, 3={events={active=[[@56,205:205='a',<459>,3:82]], id=[[@36,130:130='a',<459>,3:7]], needle=[[@42,151:151='a',<459>,3:28]], tags=[[@46,161:161='a',<459>,3:38]]}}}}",
				"{SCRIPT={1={query0={result_value=[[@16,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}}, 2={query0={id=[[@30,105:106='id',<459>,2:9]]}}, 3={query0={result_value=[[@51,172:183='result_value',<459>,3:49]], id=[[@38,132:133='id',<459>,3:9]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={result_value=[[@16,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]], tags=[[@11,38:38='a',<459>,1:38]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}, 2={def_query0={query_dictionary={id=[[@30,105:106='id',<459>,2:9]]}, table_dictionary={orders={id=[[@28,103:103='b',<459>,2:7]]}}, interface={id=[{name=id, table_ref=b}]}, table_alias={b=orders}}}, 3={def_query0={query_dictionary={result_value=[[@51,172:183='result_value',<459>,3:49]], id=[[@38,132:133='id',<459>,3:9]]}, table_dictionary={events={active=[[@56,205:205='a',<459>,3:82]], id=[[@36,130:130='a',<459>,3:7]], needle=[[@42,151:151='a',<459>,3:28]], tags=[[@46,161:161='a',<459>,3:38]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}}");
		final access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
		accessParser.executeTheParse(query, "SCRIPT");
		Assert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals("Access diagnostics are wrong", Arrays.asList(new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:1 c:13) is recognized as Snowflake-specific grammar.",
						1, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null),
				new ParseDiagnostic(ParseDiagnostic.Severity.WARNING,
						"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
						"routine 'array_contains' at (l:3 c:13) is recognized as Snowflake-specific grammar.",
						3, 13, "SqlASTWalkerHelper", null, null, true, "ast-walk", null, null)), accessParser.getAllDiagnostics());
		Assert.assertEquals("Access AST is wrong", "{SCRIPT={1={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}, 2={select={1={column={name=id, table_ref=b}}}, from={table={alias=b, table=orders}}}, 3={select={1={column={name=id, table_ref=a}}, 2={function={parameters={1={column={name=needle, table_ref=a}}, 2={column={name=tags, table_ref=a}}}, function_name=array_contains}, alias=result_value}}, from={table={alias=a, table=events}}, where={condition={left={column={name=active, table_ref=a}}, right={literal=1}, operator==}}}}}", accessParser.getSnippet().getSqlAbstractTree().toString());
		Assert.assertEquals("Access Interface is wrong", "[]", accessParser.getSnippet().getQueryInterface().toString());
		Assert.assertEquals("Access Substitution List is wrong", "{SCRIPT={1={}, 2={}, 3={}}}", accessParser.getSnippet().getSubstitutionsMap().toString());
		Assert.assertEquals("Access Table Dictionary is wrong", "{SCRIPT={1={events={active=[[@21,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]], tags=[[@11,38:38='a',<459>,1:38]]}}, 2={orders={id=[[@28,103:103='b',<459>,2:7]]}}, 3={events={active=[[@56,205:205='a',<459>,3:82]], id=[[@36,130:130='a',<459>,3:7]], needle=[[@42,151:151='a',<459>,3:28]], tags=[[@46,161:161='a',<459>,3:38]]}}}}", accessParser.getSnippet().getTableDictionary().toString());
		Assert.assertEquals("Access Query Column Dictionary is wrong", "{SCRIPT={1={query0={result_value=[[@16,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}}, 2={query0={id=[[@30,105:106='id',<459>,2:9]]}}, 3={query0={result_value=[[@51,172:183='result_value',<459>,3:49]], id=[[@38,132:133='id',<459>,3:9]]}}}}", accessParser.getSnippet().getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Access Symbol Table is wrong", "{SCRIPT={1={def_query0={query_dictionary={result_value=[[@16,49:60='result_value',<459>,1:49]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={events={active=[[@21,82:82='a',<459>,1:82]], id=[[@1,7:7='a',<459>,1:7]], needle=[[@7,28:28='a',<459>,1:28]], tags=[[@11,38:38='a',<459>,1:38]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}, 2={def_query0={query_dictionary={id=[[@30,105:106='id',<459>,2:9]]}, table_dictionary={orders={id=[[@28,103:103='b',<459>,2:7]]}}, interface={id=[{name=id, table_ref=b}]}, table_alias={b=orders}}}, 3={def_query0={query_dictionary={result_value=[[@51,172:183='result_value',<459>,3:49]], id=[[@38,132:133='id',<459>,3:9]]}, table_dictionary={events={active=[[@56,205:205='a',<459>,3:82]], id=[[@36,130:130='a',<459>,3:7]], needle=[[@42,151:151='a',<459>,3:28]], tags=[[@46,161:161='a',<459>,3:38]]}}, filters=[{name=active, table_ref=a}], interface={result_value=[{name=needle, table_ref=a}, {name=tags, table_ref=a}], id=[{name=id, table_ref=a}]}, table_alias={a=events}}}}}", accessParser.getSnippet().getSymbolTable().toString());
	}
}
