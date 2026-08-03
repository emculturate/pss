package sql.walker;
import org.junit.Assert;
import org.junit.Test;

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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a=[[@4,21:21='a',<381>,1:21]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_0=[[@5,22:22=')',<288>,1:22]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_0=[[@5,22:22=')',<288>,1:22]]}, table_dictionary={tab1={a=[[@4,21:21='a',<381>,1:21]]}}, interface={unnamed_0=[{name=a, table_ref=tab1}]}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@35,127:127='*',<291>,2:10]]}, fourth={col5=[[@56,194:194='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@48,173:173='t',<381>,3:29]], col3=[[@25,92:92='t',<381>,1:92]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={*=[[@35,127:127='*',<291>,2:10]], col4=[[@52,184:185='F4',<381>,3:40]], col12=[[@20,74:75='F4',<381>,1:74]], col1=[[@1,8:9='F4',<381>,1:8], [@44,163:164='F4',<381>,3:19]]}, query1={last=[[@5,19:22='last',<102>,1:19]], rn=[[@31,108:109='rn',<381>,1:108]], tcol2=[[@11,35:39='tcol2',<381>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={window_ordered_by=[{name=col3, table_ref=t}], query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], rn=[[@31,108:109='rn',<381>,1:108]]}, table_dictionary={fourth={col5=[[@56,194:194='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@48,173:173='t',<381>,3:29]], col3=[[@25,92:92='t',<381>,1:92]]}}, window_partition_by=[{name=col12, table_ref=F4}], def_query0={query_dictionary={col12=[[@20,74:75='F4',<381>,1:74]], *=[[@35,127:127='*',<291>,2:10]], col4=[[@52,184:185='F4',<381>,3:40]], col1=[[@1,8:9='F4',<381>,1:8], [@44,163:164='F4',<381>,3:19]]}, table_dictionary={third={*=[[@35,127:127='*',<291>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], rn=[{name=col12, table_ref=F4}, {name=col3, table_ref=t}]}, table_alias={F4=query0, t=fourth}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@40,141:141='*',<291>,2:10]]}, fourth={col5=[[@61,208:208='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@53,187:187='t',<381>,3:29]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", 
		"{query0={*=[[@40,141:141='*',<291>,2:10]], col4=[[@57,198:199='F4',<381>,3:40]], col12=[[@25,91:92='F4',<381>,1:91]], col1=[[@1,8:9='F4',<381>,1:8], [@20,74:75='F4',<381>,1:74], [@30,106:107='F4',<381>,1:106], [@49,177:178='F4',<381>,3:19]]}, query1={last=[[@5,19:22='last',<102>,1:19]], rn=[[@36,122:123='rn',<381>,1:122]], tcol2=[[@11,35:39='tcol2',<381>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={window_ordered_by=[{name=col12, table_ref=F4}, {name=col1, table_ref=F4}], query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], rn=[[@36,122:123='rn',<381>,1:122]]}, table_dictionary={fourth={col5=[[@61,208:208='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@53,187:187='t',<381>,3:29]]}}, window_partition_by=[{name=col1, table_ref=F4}], def_query0={query_dictionary={col12=[[@25,91:92='F4',<381>,1:91]], *=[[@40,141:141='*',<291>,2:10]], col4=[[@57,198:199='F4',<381>,3:40]], col1=[[@1,8:9='F4',<381>,1:8], [@20,74:75='F4',<381>,1:74], [@30,106:107='F4',<381>,1:106], [@49,177:178='F4',<381>,3:19]]}, table_dictionary={third={*=[[@40,141:141='*',<291>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], rn=[{name=col1, table_ref=F4}, {name=col12, table_ref=F4}]}, table_alias={F4=query0, t=fourth}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@40,137:137='*',<291>,2:10]]}, fourth={col9=[[@20,74:74='t',<381>,1:74], [@30,103:103='t',<381>,1:103]], col5=[[@61,204:204='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@53,183:183='t',<381>,3:29]], col3=[[@25,90:90='t',<381>,1:90]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={*=[[@40,137:137='*',<291>,2:10]], col4=[[@57,194:195='F4',<381>,3:40]], col1=[[@1,8:9='F4',<381>,1:8], [@49,173:174='F4',<381>,3:19]]}, query1={last=[[@5,19:22='last',<102>,1:19]], rn=[[@36,118:119='rn',<381>,1:118]], tcol2=[[@11,35:39='tcol2',<381>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={window_ordered_by=[{name=col3, table_ref=t}, {name=col9, table_ref=t}], query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], rn=[[@36,118:119='rn',<381>,1:118]]}, table_dictionary={fourth={col9=[[@20,74:74='t',<381>,1:74], [@30,103:103='t',<381>,1:103]], col5=[[@61,204:204='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@53,183:183='t',<381>,3:29]], col3=[[@25,90:90='t',<381>,1:90]]}}, window_partition_by=[{name=col9, table_ref=t}], def_query0={query_dictionary={*=[[@40,137:137='*',<291>,2:10]], col4=[[@57,194:195='F4',<381>,3:40]], col1=[[@1,8:9='F4',<381>,1:8], [@49,173:174='F4',<381>,3:19]]}, table_dictionary={third={*=[[@40,137:137='*',<291>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], rn=[{name=col9, table_ref=t}, {name=col3, table_ref=t}]}, table_alias={F4=query0, t=fourth}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={*=[[@58,197:197='*',<291>,2:10]]}, fourth={col9=[[@35,118:118='t',<381>,1:118]], col5=[[@79,264:264='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@43,145:145='t',<381>,1:145], [@71,243:243='t',<381>,3:29]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={*=[[@58,197:197='*',<291>,2:10]], col4=[[@75,254:255='F4',<381>,3:40]], col12=[[@25,91:92='F4',<381>,1:91]], col1=[[@1,8:9='F4',<381>,1:8], [@20,74:75='F4',<381>,1:74], [@48,161:162='F4',<381>,1:161], [@67,233:234='F4',<381>,3:19]]}, query1={last=[[@5,19:22='last',<102>,1:19]], rn1=[[@31,109:111='rn1',<381>,1:109]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], rn2=[[@54,177:179='rn2',<381>,1:177]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={window_ordered_by=[{name=col12, table_ref=F4}, {name=col1, table_ref=F4}], query_dictionary={last=[[@5,19:22='last',<102>,1:19]], rn1=[[@31,109:111='rn1',<381>,1:109]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], rn2=[[@54,177:179='rn2',<381>,1:177]]}, table_dictionary={fourth={col9=[[@35,118:118='t',<381>,1:118]], col5=[[@79,264:264='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@43,145:145='t',<381>,1:145], [@71,243:243='t',<381>,3:29]]}}, window_partition_by=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}], def_query0={query_dictionary={col12=[[@25,91:92='F4',<381>,1:91]], *=[[@58,197:197='*',<291>,2:10]], col4=[[@75,254:255='F4',<381>,3:40]], col1=[[@1,8:9='F4',<381>,1:8], [@20,74:75='F4',<381>,1:74], [@48,161:162='F4',<381>,1:161], [@67,233:234='F4',<381>,3:19]]}, table_dictionary={third={*=[[@58,197:197='*',<291>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], rn1=[{name=col1, table_ref=F4}, {name=col12, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], rn2=[{name=col2, table_ref=t}, {name=col1, table_ref=F4}, {name=col9, table_ref=t}]}, table_alias={F4=query0, t=fourth}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@35,127:127='a',<381>,2:10]], col12=[[@41,144:148='col12',<381>,2:27]], col4=[[@39,138:141='col4',<381>,2:21]]}, fourth={col5=[[@62,215:215='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@54,194:194='t',<381>,3:29]], col3=[[@25,92:92='t',<381>,1:92]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={col4=[[@39,138:141='col4',<381>,2:21], [@58,205:206='F4',<381>,3:40]], col12=[[@41,144:148='col12',<381>,2:27], [@20,74:75='F4',<381>,1:74]], col1=[[@37,132:135='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@50,184:185='F4',<381>,3:19]]}, query1={last=[[@5,19:22='last',<102>,1:19]], rn=[[@31,108:109='rn',<381>,1:108]], tcol2=[[@11,35:39='tcol2',<381>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={window_ordered_by=[{name=col3, table_ref=t}], query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], rn=[[@31,108:109='rn',<381>,1:108]]}, table_dictionary={fourth={col5=[[@62,215:215='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@54,194:194='t',<381>,3:29]], col3=[[@25,92:92='t',<381>,1:92]]}}, window_partition_by=[{name=col12, table_ref=F4}], def_query0={query_dictionary={col12=[[@41,144:148='col12',<381>,2:27], [@20,74:75='F4',<381>,1:74]], col4=[[@39,138:141='col4',<381>,2:21], [@58,205:206='F4',<381>,3:40]], col1=[[@37,132:135='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@50,184:185='F4',<381>,3:19]]}, table_dictionary={third={a=[[@35,127:127='a',<381>,2:10]], col12=[[@41,144:148='col12',<381>,2:27]], col4=[[@39,138:141='col4',<381>,2:21]]}}, interface={col12=[{name=col12, table_ref=third}], col4=[{name=col4, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], rn=[{name=col12, table_ref=F4}, {name=col3, table_ref=t}]}, table_alias={F4=query0, t=fourth}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@40,141:141='a',<381>,2:10]], col12=[[@46,158:162='col12',<381>,2:27]], col4=[[@44,152:155='col4',<381>,2:21]]}, fourth={col5=[[@67,229:229='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@59,208:208='t',<381>,3:29]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={col4=[[@44,152:155='col4',<381>,2:21], [@63,219:220='F4',<381>,3:40]], col12=[[@46,158:162='col12',<381>,2:27], [@25,91:92='F4',<381>,1:91]], col1=[[@42,146:149='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@20,74:75='F4',<381>,1:74], [@30,106:107='F4',<381>,1:106], [@55,198:199='F4',<381>,3:19]]}, query1={last=[[@5,19:22='last',<102>,1:19]], rn=[[@36,122:123='rn',<381>,1:122]], tcol2=[[@11,35:39='tcol2',<381>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={window_ordered_by=[{name=col12, table_ref=F4}, {name=col1, table_ref=F4}], query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], rn=[[@36,122:123='rn',<381>,1:122]]}, table_dictionary={fourth={col5=[[@67,229:229='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@59,208:208='t',<381>,3:29]]}}, window_partition_by=[{name=col1, table_ref=F4}], def_query0={query_dictionary={col12=[[@46,158:162='col12',<381>,2:27], [@25,91:92='F4',<381>,1:91]], col4=[[@44,152:155='col4',<381>,2:21], [@63,219:220='F4',<381>,3:40]], col1=[[@42,146:149='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@20,74:75='F4',<381>,1:74], [@30,106:107='F4',<381>,1:106], [@55,198:199='F4',<381>,3:19]]}, table_dictionary={third={a=[[@40,141:141='a',<381>,2:10]], col12=[[@46,158:162='col12',<381>,2:27]], col4=[[@44,152:155='col4',<381>,2:21]]}}, interface={col12=[{name=col12, table_ref=third}], col4=[{name=col4, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], rn=[{name=col1, table_ref=F4}, {name=col12, table_ref=F4}]}, table_alias={F4=query0, t=fourth}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@40,137:137='a',<381>,2:10]], col4=[[@44,148:151='col4',<381>,2:21]]}, fourth={col9=[[@20,74:74='t',<381>,1:74], [@30,103:103='t',<381>,1:103]], col5=[[@65,218:218='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@57,197:197='t',<381>,3:29]], col3=[[@25,90:90='t',<381>,1:90]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={col4=[[@44,148:151='col4',<381>,2:21], [@61,208:209='F4',<381>,3:40]], col1=[[@42,142:145='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@53,187:188='F4',<381>,3:19]]}, query1={last=[[@5,19:22='last',<102>,1:19]], rn=[[@36,118:119='rn',<381>,1:118]], tcol2=[[@11,35:39='tcol2',<381>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={window_ordered_by=[{name=col3, table_ref=t}, {name=col9, table_ref=t}], query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], rn=[[@36,118:119='rn',<381>,1:118]]}, table_dictionary={fourth={col9=[[@20,74:74='t',<381>,1:74], [@30,103:103='t',<381>,1:103]], col5=[[@65,218:218='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@57,197:197='t',<381>,3:29]], col3=[[@25,90:90='t',<381>,1:90]]}}, window_partition_by=[{name=col9, table_ref=t}], def_query0={query_dictionary={col4=[[@44,148:151='col4',<381>,2:21], [@61,208:209='F4',<381>,3:40]], col1=[[@42,142:145='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@53,187:188='F4',<381>,3:19]]}, table_dictionary={third={a=[[@40,137:137='a',<381>,2:10]], col4=[[@44,148:151='col4',<381>,2:21]]}}, interface={col4=[{name=col4, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], rn=[{name=col9, table_ref=t}, {name=col3, table_ref=t}]}, table_alias={F4=query0, t=fourth}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{third={a=[[@58,197:197='a',<381>,2:10]], col12=[[@64,214:218='col12',<381>,2:27]], col4=[[@62,208:211='col4',<381>,2:21]]}, fourth={col9=[[@35,118:118='t',<381>,1:118]], col5=[[@85,285:285='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@43,145:145='t',<381>,1:145], [@77,264:264='t',<381>,3:29]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={col4=[[@62,208:211='col4',<381>,2:21], [@81,275:276='F4',<381>,3:40]], col12=[[@64,214:218='col12',<381>,2:27], [@25,91:92='F4',<381>,1:91]], col1=[[@60,202:205='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@20,74:75='F4',<381>,1:74], [@48,161:162='F4',<381>,1:161], [@73,254:255='F4',<381>,3:19]]}, query1={last=[[@5,19:22='last',<102>,1:19]], rn1=[[@31,109:111='rn1',<381>,1:109]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], rn2=[[@54,177:179='rn2',<381>,1:177]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={window_ordered_by=[{name=col12, table_ref=F4}, {name=col1, table_ref=F4}], query_dictionary={last=[[@5,19:22='last',<102>,1:19]], rn1=[[@31,109:111='rn1',<381>,1:109]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], rn2=[[@54,177:179='rn2',<381>,1:177]]}, table_dictionary={fourth={col9=[[@35,118:118='t',<381>,1:118]], col5=[[@85,285:285='t',<381>,3:50]], col2=[[@7,25:25='t',<381>,1:25], [@43,145:145='t',<381>,1:145], [@77,264:264='t',<381>,3:29]]}}, window_partition_by=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}], def_query0={query_dictionary={col12=[[@64,214:218='col12',<381>,2:27], [@25,91:92='F4',<381>,1:91]], col4=[[@62,208:211='col4',<381>,2:21], [@81,275:276='F4',<381>,3:40]], col1=[[@60,202:205='col1',<381>,2:15], [@1,8:9='F4',<381>,1:8], [@20,74:75='F4',<381>,1:74], [@48,161:162='F4',<381>,1:161], [@73,254:255='F4',<381>,3:19]]}, table_dictionary={third={a=[[@58,197:197='a',<381>,2:10]], col12=[[@64,214:218='col12',<381>,2:27]], col4=[[@62,208:211='col4',<381>,2:21]]}}, interface={col12=[{name=col12, table_ref=third}], col4=[{name=col4, table_ref=third}], col1=[{name=a, table_ref=third}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], rn1=[{name=col1, table_ref=F4}, {name=col12, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], rn2=[{name=col2, table_ref=t}, {name=col1, table_ref=F4}, {name=col9, table_ref=t}]}, table_alias={F4=query0, t=fourth}}}", extractor.getSymbolTable().toString());
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
		 "{query0={*=[[@35,127:127='*',<291>,2:10]], col4=[[@58,203:204='F4',<381>,3:59]], col12=[[@20,74:75='F4',<381>,1:74]], col1=[[@1,8:9='F4',<381>,1:8], [@50,182:183='F4',<381>,3:38]]}, query1={*=[[@43,159:159='*',<291>,3:15]], col5=[[@62,213:213='t',<381>,3:69]], col2=[[@7,25:25='t',<381>,1:25], [@54,192:192='t',<381>,3:48]], col3=[[@25,92:92='t',<381>,1:92]]}, query2={last=[[@5,19:22='last',<102>,1:19]], rn=[[@31,108:109='rn',<381>,1:108]], tcol2=[[@11,35:39='tcol2',<381>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={window_ordered_by=[{name=col3, table_ref=t}], query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], rn=[[@31,108:109='rn',<381>,1:108]]}, window_partition_by=[{name=col12, table_ref=F4}], def_query1={query_dictionary={*=[[@43,159:159='*',<291>,3:15]], col5=[[@62,213:213='t',<381>,3:69]], col2=[[@7,25:25='t',<381>,1:25], [@54,192:192='t',<381>,3:48]], col3=[[@25,92:92='t',<381>,1:92]]}, table_dictionary={fourth={*=[[@43,159:159='*',<291>,3:15]]}}, interface={*=[{name=*, table_ref=*}]}}, def_query0={query_dictionary={col12=[[@20,74:75='F4',<381>,1:74]], *=[[@35,127:127='*',<291>,2:10]], col4=[[@58,203:204='F4',<381>,3:59]], col1=[[@1,8:9='F4',<381>,1:8], [@50,182:183='F4',<381>,3:38]]}, table_dictionary={third={*=[[@35,127:127='*',<291>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], rn=[{name=col12, table_ref=F4}, {name=col3, table_ref=t}]}, table_alias={F4=query0, t=query1}}}", extractor.getSymbolTable().toString());
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
		 "{query0={*=[[@40,141:141='*',<291>,2:10]], col4=[[@63,217:218='F4',<381>,3:59]], col12=[[@25,91:92='F4',<381>,1:91]], col1=[[@1,8:9='F4',<381>,1:8], [@20,74:75='F4',<381>,1:74], [@30,106:107='F4',<381>,1:106], [@55,196:197='F4',<381>,3:38]]}, query1={col2=[[@7,25:25='t',<381>,1:25], [@59,206:206='t',<381>,3:48]], *=[[@48,173:173='*',<291>,3:15]], col5=[[@67,227:227='t',<381>,3:69]]}, query2={last=[[@5,19:22='last',<102>,1:19]], rn=[[@36,122:123='rn',<381>,1:122]], tcol2=[[@11,35:39='tcol2',<381>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={window_ordered_by=[{name=col12, table_ref=F4}, {name=col1, table_ref=F4}], query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], rn=[[@36,122:123='rn',<381>,1:122]]}, window_partition_by=[{name=col1, table_ref=F4}], def_query1={query_dictionary={*=[[@48,173:173='*',<291>,3:15]], col5=[[@67,227:227='t',<381>,3:69]], col2=[[@7,25:25='t',<381>,1:25], [@59,206:206='t',<381>,3:48]]}, table_dictionary={fourth={*=[[@48,173:173='*',<291>,3:15]]}}, interface={*=[{name=*, table_ref=*}]}}, def_query0={query_dictionary={col12=[[@25,91:92='F4',<381>,1:91]], *=[[@40,141:141='*',<291>,2:10]], col4=[[@63,217:218='F4',<381>,3:59]], col1=[[@1,8:9='F4',<381>,1:8], [@20,74:75='F4',<381>,1:74], [@30,106:107='F4',<381>,1:106], [@55,196:197='F4',<381>,3:38]]}, table_dictionary={third={*=[[@40,141:141='*',<291>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], rn=[{name=col1, table_ref=F4}, {name=col12, table_ref=F4}]}, table_alias={F4=query0, t=query1}}}", extractor.getSymbolTable().toString());
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
		 "{query0={*=[[@40,137:137='*',<291>,2:10]], col4=[[@63,213:214='F4',<381>,3:59]], col1=[[@1,8:9='F4',<381>,1:8], [@55,192:193='F4',<381>,3:38]]}, query1={*=[[@48,169:169='*',<291>,3:15]], col5=[[@67,223:223='t',<381>,3:69]], col2=[[@7,25:25='t',<381>,1:25], [@59,202:202='t',<381>,3:48]], col3=[[@25,90:90='t',<381>,1:90]], col9=[[@20,74:74='t',<381>,1:74], [@30,103:103='t',<381>,1:103]]}, query2={last=[[@5,19:22='last',<102>,1:19]], rn=[[@36,118:119='rn',<381>,1:118]], tcol2=[[@11,35:39='tcol2',<381>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={window_ordered_by=[{name=col3, table_ref=t}, {name=col9, table_ref=t}], query_dictionary={last=[[@5,19:22='last',<102>,1:19]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], rn=[[@36,118:119='rn',<381>,1:118]]}, window_partition_by=[{name=col9, table_ref=t}], def_query1={query_dictionary={col9=[[@20,74:74='t',<381>,1:74], [@30,103:103='t',<381>,1:103]], *=[[@48,169:169='*',<291>,3:15]], col5=[[@67,223:223='t',<381>,3:69]], col2=[[@7,25:25='t',<381>,1:25], [@59,202:202='t',<381>,3:48]], col3=[[@25,90:90='t',<381>,1:90]]}, table_dictionary={fourth={*=[[@48,169:169='*',<291>,3:15]]}}, interface={*=[{name=*, table_ref=*}]}}, def_query0={query_dictionary={*=[[@40,137:137='*',<291>,2:10]], col4=[[@63,213:214='F4',<381>,3:59]], col1=[[@1,8:9='F4',<381>,1:8], [@55,192:193='F4',<381>,3:38]]}, table_dictionary={third={*=[[@40,137:137='*',<291>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], rn=[{name=col9, table_ref=t}, {name=col3, table_ref=t}]}, table_alias={F4=query0, t=query1}}}", extractor.getSymbolTable().toString());
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
		 "{query0={*=[[@58,197:197='*',<291>,2:10]], col4=[[@81,273:274='F4',<381>,3:59]], col12=[[@25,91:92='F4',<381>,1:91]], col1=[[@1,8:9='F4',<381>,1:8], [@20,74:75='F4',<381>,1:74], [@48,161:162='F4',<381>,1:161], [@73,252:253='F4',<381>,3:38]]}, query1={*=[[@66,229:229='*',<291>,3:15]], col5=[[@85,283:283='t',<381>,3:69]], col2=[[@7,25:25='t',<381>,1:25], [@43,145:145='t',<381>,1:145], [@77,262:262='t',<381>,3:48]], col9=[[@35,118:118='t',<381>,1:118]]}, query2={last=[[@5,19:22='last',<102>,1:19]], rn1=[[@31,109:111='rn1',<381>,1:109]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], rn2=[[@54,177:179='rn2',<381>,1:177]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={window_ordered_by=[{name=col12, table_ref=F4}, {name=col1, table_ref=F4}], query_dictionary={last=[[@5,19:22='last',<102>,1:19]], rn1=[[@31,109:111='rn1',<381>,1:109]], tcol2=[[@11,35:39='tcol2',<381>,1:35]], rn2=[[@54,177:179='rn2',<381>,1:177]]}, window_partition_by=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}], def_query1={query_dictionary={col9=[[@35,118:118='t',<381>,1:118]], *=[[@66,229:229='*',<291>,3:15]], col5=[[@85,283:283='t',<381>,3:69]], col2=[[@7,25:25='t',<381>,1:25], [@43,145:145='t',<381>,1:145], [@77,262:262='t',<381>,3:48]]}, table_dictionary={fourth={*=[[@66,229:229='*',<291>,3:15]]}}, interface={*=[{name=*, table_ref=*}]}}, def_query0={query_dictionary={col12=[[@25,91:92='F4',<381>,1:91]], *=[[@58,197:197='*',<291>,2:10]], col4=[[@81,273:274='F4',<381>,3:59]], col1=[[@1,8:9='F4',<381>,1:8], [@20,74:75='F4',<381>,1:74], [@48,161:162='F4',<381>,1:161], [@73,252:253='F4',<381>,3:38]]}, table_dictionary={third={*=[[@58,197:197='*',<291>,2:10]]}}, interface={*=[{name=*, table_ref=*}]}}, filters=[{name=col1, table_ref=F4}, {name=col2, table_ref=t}, {name=col4, table_ref=F4}, {name=col5, table_ref=t}], interface={last=[{name=col1, table_ref=F4}], rn1=[{name=col1, table_ref=F4}, {name=col12, table_ref=F4}], tcol2=[{name=col2, table_ref=t}], rn2=[{name=col2, table_ref=t}, {name=col1, table_ref=F4}, {name=col9, table_ref=t}]}, table_alias={F4=query0, t=query1}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={k_stfd=[[@8,33:38='k_stfd',<381>,1:33]], row_num=[[@16,76:82='row_num',<381>,1:76]], kppi=[[@10,41:44='kppi',<381>,1:41]], OBSERVATION_TM=[[@13,55:68='OBSERVATION_TM',<381>,1:55]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={key_rank=[[@20,93:100='key_rank',<381>,1:93]]}}",
						extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=OBSERVATION_TM, table_ref=null}, {name=row_num, table_ref=null}], query_dictionary={key_rank=[[@20,93:100='key_rank',<381>,1:93]]}, table_dictionary={tab1={k_stfd=[[@8,33:38='k_stfd',<381>,1:33]], row_num=[[@16,76:82='row_num',<381>,1:76]], kppi=[[@10,41:44='kppi',<381>,1:41]], OBSERVATION_TM=[[@13,55:68='OBSERVATION_TM',<381>,1:55]]}}, window_partition_by=[{name=k_stfd, table_ref=null}, {name=kppi, table_ref=null}], interface={key_rank=[{name=k_stfd, table_ref=tab1}, {name=kppi, table_ref=tab1}, {name=OBSERVATION_TM, table_ref=tab1}, {name=row_num, table_ref=tab1}]}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={k_stfd=[[@8,33:38='k_stfd',<381>,1:33]], row_num=[[@16,76:82='row_num',<381>,1:76]], kppi=[[@10,41:44='kppi',<381>,1:41]], OBSERVATION_TM=[[@13,55:68='OBSERVATION_TM',<381>,1:55]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={rank=[[@20,93:96='rank',<128>,1:93]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=OBSERVATION_TM, table_ref=null}, {name=row_num, table_ref=null}], query_dictionary={rank=[[@20,93:96='rank',<128>,1:93]]}, table_dictionary={tab1={k_stfd=[[@8,33:38='k_stfd',<381>,1:33]], row_num=[[@16,76:82='row_num',<381>,1:76]], kppi=[[@10,41:44='kppi',<381>,1:41]], OBSERVATION_TM=[[@13,55:68='OBSERVATION_TM',<381>,1:55]]}}, window_partition_by=[{name=k_stfd, table_ref=null}, {name=kppi, table_ref=null}], interface={rank=[{name=k_stfd, table_ref=tab1}, {name=kppi, table_ref=tab1}, {name=OBSERVATION_TM, table_ref=tab1}, {name=row_num, table_ref=tab1}]}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{sgbstdn={a=[[@3,18:18='a',<381>,1:18], [@9,38:38='a',<381>,1:38]], b=[[@5,22:22='b',<381>,1:22], [@11,42:42='b',<381>,1:42]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={case_one=[[@18,70:77='case_one',<381>,1:70]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={case_one=[[@18,70:77='case_one',<381>,1:70]]}, table_dictionary={sgbstdn={a=[[@3,18:18='a',<381>,1:18], [@9,38:38='a',<381>,1:38]], b=[[@5,22:22='b',<381>,1:22], [@11,42:42='b',<381>,1:42]]}}, interface={case_one=[{name=a, table_ref=sgbstdn}, {name=b, table_ref=sgbstdn}]}}}",
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

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=t_student_last_name, case={clauses={1={then={column={name=t_student_last_name, table_ref=S948}}, when={column={name=OBSERVATION_TM, table_ref=s948}}}, 2={then={column={name=t_student_last_name, table_ref=S949}}, when={function={parameters={1={condition={left={column={name=OBSERVATION_TM, table_ref=S949}}, right={column={name=OBSERVATION_TM, table_ref=S948}}, operator=>=}}, 2={literal=FALSE}}, function_name=COALESCE}}}}, else={function={parameters={1={column={name=t_student_last_name, table_ref=S948}}, 2={column={name=t_student_last_name, table_ref=S949}}}, function_name=COALESCE}}}}}, from={join={1={table={schema=my, alias=s948, table=234}}, 2={table={schema=my, alias=s949, table=other5}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[t_student_last_name]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{my.other5={t_student_last_name=[[@24,145:148='S949',<381>,1:145], [@34,213:216='S949',<381>,1:213]], OBSERVATION_TM=[[@13,90:93='S949',<381>,1:90]]}, my.234={t_student_last_name=[[@7,47:50='S948',<381>,1:47], [@30,187:190='S948',<381>,1:187]], OBSERVATION_TM=[[@3,22:25='s948',<381>,1:22], [@17,111:114='S948',<381>,1:111]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={t_student_last_name=[[@40,246:264='t_student_last_name',<381>,1:246]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={t_student_last_name=[[@40,246:264='t_student_last_name',<381>,1:246]]}, table_dictionary={my.other5={t_student_last_name=[[@24,145:148='S949',<381>,1:145], [@34,213:216='S949',<381>,1:213]], OBSERVATION_TM=[[@13,90:93='S949',<381>,1:90]]}, my.234={t_student_last_name=[[@7,47:50='S948',<381>,1:47], [@30,187:190='S948',<381>,1:187]], OBSERVATION_TM=[[@3,22:25='s948',<381>,1:22], [@17,111:114='S948',<381>,1:111]]}}, interface={t_student_last_name=[{name=t_student_last_name, table_ref=S948}, {name=OBSERVATION_TM, table_ref=s948}, {name=t_student_last_name, table_ref=S949}, {name=OBSERVATION_TM, table_ref=S949}, {name=OBSERVATION_TM, table_ref=S948}]}, table_alias={s949=my.other5, s948=my.234}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{sgbstdn={col=[[@17,86:88='col',<381>,1:86]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={case_one=[[@14,70:77='case_one',<381>,1:70]], case_two=[[@26,127:134='case_two',<381>,1:127]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={case_two=[[@26,127:134='case_two',<381>,1:127]], case_one=[[@14,70:77='case_one',<381>,1:70]]}, table_dictionary={sgbstdn={col=[[@17,86:88='col',<381>,1:86]]}}, interface={case_two=[{name=col, table_ref=sgbstdn}], case_one=[]}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={field1=[[@6,29:34='field1',<381>,1:29]], field2=[[@13,50:55='field2',<381>,1:50]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_1=[[@16,60:60=')',<288>,1:60]], unnamed_0=[[@7,35:35=')',<288>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={unnamed_1=[[@16,60:60=')',<288>,1:60]], unnamed_0=[[@7,35:35=')',<288>,1:35]]}, table_dictionary={scbcrse={field1=[[@6,29:34='field1',<381>,1:29]], field2=[[@13,50:55='field2',<381>,1:50]]}}, interface={unnamed_1=[{name=field2, table_ref=scbcrse}], unnamed_0=[{name=field1, table_ref=scbcrse}]}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={<field2>=[[@15,54:54='a',<381>,1:54]], <field1>=[[@6,29:29='a',<381>,1:29]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={unnamed_1=[[@20,68:68=')',<288>,1:68]], unnamed_0=[[@9,39:39=')',<288>,1:39]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		 "{def_query0={query_dictionary={unnamed_1=[[@20,68:68=')',<288>,1:68]], unnamed_0=[[@9,39:39=')',<288>,1:39]]}, table_dictionary={scbcrse={<field2>=[[@15,54:54='a',<381>,1:54]], <field1>=[[@6,29:29='a',<381>,1:29]]}}, interface={unnamed_1=[{substitution={name=<field2>, type=column}, table_ref=a}], unnamed_0=[{substitution={name=<field1>, type=column}, table_ref=a}]}, table_alias={a=scbcrse}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7], [@11,42:46='apple',<381>,1:42]], *=[[@5,20:20='*',<291>,1:20]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]], unnamed_0=[[@6,21:21=')',<288>,1:21]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]], unnamed_0=[[@6,21:21=')',<288>,1:21]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7], [@11,42:46='apple',<381>,1:42]], *=[[@5,20:20='*',<291>,1:20]]}}, grouped_by=[{name=apple, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}], unnamed_0=[]}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]], <other>=[[@11,42:45='tab1',<381>,1:42]], *=[[@5,20:20='*',<291>,1:20]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]], unnamed_0=[[@6,21:21=')',<288>,1:21]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]], unnamed_0=[[@6,21:21=')',<288>,1:21]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]], <other>=[[@11,42:45='tab1',<381>,1:42]], *=[[@5,20:20='*',<291>,1:20]]}}, grouped_by=[{substitution={name=<other>, type=column}, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}], unnamed_0=[]}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]], a=[[@18,70:70='a',<381>,1:70]], b=[[@20,72:72='b',<381>,1:72]], c=[[@22,74:74='c',<381>,1:74]], <other>=[[@11,42:45='tab1',<381>,1:42]], *=[[@5,20:20='*',<291>,1:20]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={apple=[[@1,7:11='apple',<381>,1:7]], unnamed_0=[[@6,21:21=')',<288>,1:21]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		 "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]], unnamed_0=[[@6,21:21=')',<288>,1:21]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]], a=[[@18,70:70='a',<381>,1:70]], b=[[@20,72:72='b',<381>,1:72]], c=[[@22,74:74='c',<381>,1:74]], <other>=[[@11,42:45='tab1',<381>,1:42]], *=[[@5,20:20='*',<291>,1:20]]}}, grouped_by=[{substitution={name=<other>, type=column}, table_ref=tab1}, {name=<predicand>, type=predicand}, {name=a, table_ref=null}, {name=b, table_ref=null}, {name=c, table_ref=null}], interface={apple=[{name=apple, table_ref=tab1}], unnamed_0=[]}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7]], *=[[@5,20:20='*',<291>,1:20]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]], unnamed_0=[[@6,21:21=')',<288>,1:21]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]], unnamed_0=[[@6,21:21=')',<288>,1:21]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7]], *=[[@5,20:20='*',<291>,1:20]]}}, grouped_by=[{name=<other>, type=predicand}], interface={apple=[{name=apple, table_ref=tab1}], unnamed_0=[]}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7], [@13,54:58='apple',<381>,1:54]], subj=[[@5,20:23='subj',<381>,1:20]], object=[[@7,27:32='object',<268>,1:27]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]], unnamed_0=[[@8,33:33=')',<288>,1:33]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]], unnamed_0=[[@8,33:33=')',<288>,1:33]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7], [@13,54:58='apple',<381>,1:54]], subj=[[@5,20:23='subj',<381>,1:20]], object=[[@7,27:32='object',<268>,1:27]]}}, grouped_by=[{name=apple, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}], unnamed_0=[{name=subj, table_ref=tab1}, {name=object, table_ref=tab1}]}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7], [@13,53:57='apple',<381>,1:53]], <other>=[[@5,20:23='tab1',<381>,1:20]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]], unnamed_0=[[@8,32:32=')',<288>,1:32]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]], unnamed_0=[[@8,32:32=')',<288>,1:32]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7], [@13,53:57='apple',<381>,1:53]], <other>=[[@5,20:23='tab1',<381>,1:20]]}}, grouped_by=[{name=apple, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}], unnamed_0=[{substitution={name=<other>, type=column}, table_ref=tab1}]}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={apple=[[@1,7:11='apple',<381>,1:7], [@11,48:52='apple',<381>,1:48]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={apple=[[@1,7:11='apple',<381>,1:7]], unnamed_0=[[@6,27:27=')',<288>,1:27]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={apple=[[@1,7:11='apple',<381>,1:7]], unnamed_0=[[@6,27:27=')',<288>,1:27]]}, table_dictionary={tab1={apple=[[@1,7:11='apple',<381>,1:7], [@11,48:52='apple',<381>,1:48]]}}, grouped_by=[{name=apple, table_ref=tab1}], interface={apple=[{name=apple, table_ref=tab1}], unnamed_0=[{name=<other>, type=predicand}]}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={item=[[@3,12:15='item',<381>,1:12]], code=[[@8,24:27='code',<381>,1:24], [@19,71:74='code',<381>,1:71]], spriden_id=[[@16,51:60='spriden_id',<381>,1:51]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_1=[[@20,75:75=')',<288>,1:75]], unnamed_0=[[@4,16:16=')',<288>,1:16]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=code, table_ref=null}], query_dictionary={unnamed_1=[[@20,75:75=')',<288>,1:75]], unnamed_0=[[@4,16:16=')',<288>,1:16]]}, table_dictionary={tab1={item=[[@3,12:15='item',<381>,1:12]], code=[[@8,24:27='code',<381>,1:24], [@19,71:74='code',<381>,1:71]], spriden_id=[[@16,51:60='spriden_id',<381>,1:51]]}}, window_partition_by=[{name=spriden_id, table_ref=null}], interface={unnamed_1=[{name=spriden_id, table_ref=tab1}, {name=code, table_ref=tab1}], unnamed_0=[{name=item, table_ref=tab1}]}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={k_stfd=[[@8,35:40='k_stfd',<381>,1:35]], row_num=[[@16,78:84='row_num',<381>,1:78]], kppi=[[@10,43:46='kppi',<381>,1:43]], OBSERVATION_TM=[[@13,57:70='OBSERVATION_TM',<381>,1:57]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={key_rank=[[@20,95:102='key_rank',<381>,1:95]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=OBSERVATION_TM, table_ref=null}, {name=row_num, table_ref=null}], query_dictionary={key_rank=[[@20,95:102='key_rank',<381>,1:95]]}, table_dictionary={tab1={k_stfd=[[@8,35:40='k_stfd',<381>,1:35]], row_num=[[@16,78:84='row_num',<381>,1:78]], kppi=[[@10,43:46='kppi',<381>,1:43]], OBSERVATION_TM=[[@13,57:70='OBSERVATION_TM',<381>,1:57]]}}, window_partition_by=[{name=k_stfd, table_ref=null}, {name=kppi, table_ref=null}], interface={key_rank=[{name=k_stfd, table_ref=tab1}, {name=kppi, table_ref=tab1}, {name=OBSERVATION_TM, table_ref=tab1}, {name=row_num, table_ref=tab1}]}, table_alias={a=tab1}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={k_stfd=[[@9,39:44='k_stfd',<381>,1:39]], parm=[[@3,14:17='parm',<381>,1:14]], row_num=[[@17,82:88='row_num',<381>,1:82]], kppi=[[@11,47:50='kppi',<381>,1:47]], OBSERVATION_TM=[[@14,61:74='OBSERVATION_TM',<381>,1:61]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={key_rank=[[@21,99:106='key_rank',<381>,1:99]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=OBSERVATION_TM, table_ref=null}, {name=row_num, table_ref=null}], query_dictionary={key_rank=[[@21,99:106='key_rank',<381>,1:99]]}, table_dictionary={tab1={k_stfd=[[@9,39:44='k_stfd',<381>,1:39]], parm=[[@3,14:17='parm',<381>,1:14]], row_num=[[@17,82:88='row_num',<381>,1:82]], kppi=[[@11,47:50='kppi',<381>,1:47]], OBSERVATION_TM=[[@14,61:74='OBSERVATION_TM',<381>,1:61]]}}, window_partition_by=[{name=k_stfd, table_ref=null}, {name=kppi, table_ref=null}], interface={key_rank=[{name=k_stfd, table_ref=tab1}, {name=kppi, table_ref=tab1}, {name=OBSERVATION_TM, table_ref=tab1}, {name=row_num, table_ref=tab1}, {name=parm, table_ref=tab1}]}, table_alias={a=tab1}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={degree_cd_2_fill=[[@125,788:803='degree_cd_2_fill',<381>,1:788]], concentration_cd_fill=[[@71,441:461='concentration_cd_fill',<381>,1:441]], college_cd_fill=[[@35,213:227='college_cd_fill',<381>,1:213]], major_cd_2_fill=[[@89,559:573='major_cd_2_fill',<381>,1:559]], college_cd_2_fill=[[@107,673:689='college_cd_2_fill',<381>,1:673]], degree_cd_fill=[[@53,324:337='degree_cd_fill',<381>,1:324]], concentration_cd_2_fill=[[@143,909:931='concentration_cd_2_fill',<381>,1:909]], major_cd_fill=[[@17,103:115='major_cd_fill',<381>,1:103]]}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={value_partition=[[@13,70:84='value_partition',<381>,1:70]], term_row=[[@16,95:102='term_row',<381>,1:95]], major_cd=[[@3,16:23='major_cd',<381>,1:16], [@23,153:160='major_cd',<381>,1:153]], student_id=[[@11,58:67='student_id',<381>,1:58]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={major_cd_fill=[[@19,108:120='major_cd_fill',<381>,1:108]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=term_row, table_ref=null}], query_dictionary={major_cd_fill=[[@19,108:120='major_cd_fill',<381>,1:108]]}, table_dictionary={student_term_major={value_partition=[[@13,70:84='value_partition',<381>,1:70]], term_row=[[@16,95:102='term_row',<381>,1:95]], major_cd=[[@3,16:23='major_cd',<381>,1:16], [@23,153:160='major_cd',<381>,1:153]], student_id=[[@11,58:67='student_id',<381>,1:58]]}}, window_partition_by=[{name=student_id, table_ref=null}, {name=value_partition, table_ref=null}], filters=[{name=major_cd, table_ref=student_term_major}], interface={major_cd_fill=[{name=student_id, table_ref=student_term_major}, {name=value_partition, table_ref=student_term_major}, {name=term_row, table_ref=student_term_major}, {name=major_cd, table_ref=student_term_major}]}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={value_partition=[[@13,71:85='value_partition',<381>,1:71]], term_row=[[@16,96:103='term_row',<381>,1:96]], major_cd=[[@3,17:24='major_cd',<381>,1:17], [@23,154:161='major_cd',<381>,1:154]], student_id=[[@11,59:68='student_id',<381>,1:59]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={major_cd_fill=[[@19,109:121='major_cd_fill',<381>,1:109]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=term_row, table_ref=null}], query_dictionary={major_cd_fill=[[@19,109:121='major_cd_fill',<381>,1:109]]}, table_dictionary={student_term_major={value_partition=[[@13,71:85='value_partition',<381>,1:71]], term_row=[[@16,96:103='term_row',<381>,1:96]], major_cd=[[@3,17:24='major_cd',<381>,1:17], [@23,154:161='major_cd',<381>,1:154]], student_id=[[@11,59:68='student_id',<381>,1:59]]}}, window_partition_by=[{name=student_id, table_ref=null}, {name=value_partition, table_ref=null}], filters=[{name=major_cd, table_ref=student_term_major}], interface={major_cd_fill=[{name=student_id, table_ref=student_term_major}, {name=value_partition, table_ref=student_term_major}, {name=term_row, table_ref=student_term_major}, {name=major_cd, table_ref=student_term_major}]}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={value_partition=[[@13,77:91='value_partition',<381>,1:77]], term_row=[[@16,102:109='term_row',<381>,1:102]], major_cd=[[@3,23:30='major_cd',<381>,1:23], [@23,160:167='major_cd',<381>,1:160]], student_id=[[@11,65:74='student_id',<381>,1:65]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={major_cd_fill=[[@19,115:127='major_cd_fill',<381>,1:115]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=term_row, table_ref=null}], query_dictionary={major_cd_fill=[[@19,115:127='major_cd_fill',<381>,1:115]]}, table_dictionary={student_term_major={value_partition=[[@13,77:91='value_partition',<381>,1:77]], term_row=[[@16,102:109='term_row',<381>,1:102]], major_cd=[[@3,23:30='major_cd',<381>,1:23], [@23,160:167='major_cd',<381>,1:160]], student_id=[[@11,65:74='student_id',<381>,1:65]]}}, window_partition_by=[{name=student_id, table_ref=null}, {name=value_partition, table_ref=null}], filters=[{name=major_cd, table_ref=student_term_major}], interface={major_cd_fill=[{name=student_id, table_ref=student_term_major}, {name=value_partition, table_ref=student_term_major}, {name=term_row, table_ref=student_term_major}, {name=major_cd, table_ref=student_term_major}]}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={value_partition=[[@13,78:92='value_partition',<381>,1:78]], term_row=[[@16,103:110='term_row',<381>,1:103]], major_cd=[[@3,23:30='major_cd',<381>,1:23], [@23,161:168='major_cd',<381>,1:161]], student_id=[[@11,66:75='student_id',<381>,1:66]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={major_cd_fill=[[@19,116:128='major_cd_fill',<381>,1:116]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=term_row, table_ref=null}], query_dictionary={major_cd_fill=[[@19,116:128='major_cd_fill',<381>,1:116]]}, table_dictionary={student_term_major={value_partition=[[@13,78:92='value_partition',<381>,1:78]], term_row=[[@16,103:110='term_row',<381>,1:103]], major_cd=[[@3,23:30='major_cd',<381>,1:23], [@23,161:168='major_cd',<381>,1:161]], student_id=[[@11,66:75='student_id',<381>,1:66]]}}, window_partition_by=[{name=student_id, table_ref=null}, {name=value_partition, table_ref=null}], filters=[{name=major_cd, table_ref=student_term_major}], interface={major_cd_fill=[{name=student_id, table_ref=student_term_major}, {name=value_partition, table_ref=student_term_major}, {name=term_row, table_ref=student_term_major}, {name=major_cd, table_ref=student_term_major}]}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={value_partition=[[@13,78:92='value_partition',<381>,1:78]], term_row=[[@16,103:110='term_row',<381>,1:103]], major_cd=[[@3,24:31='major_cd',<381>,1:24], [@23,161:168='major_cd',<381>,1:161]], student_id=[[@11,66:75='student_id',<381>,1:66]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={major_cd_fill=[[@19,116:128='major_cd_fill',<381>,1:116]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=term_row, table_ref=null}], query_dictionary={major_cd_fill=[[@19,116:128='major_cd_fill',<381>,1:116]]}, table_dictionary={student_term_major={value_partition=[[@13,78:92='value_partition',<381>,1:78]], term_row=[[@16,103:110='term_row',<381>,1:103]], major_cd=[[@3,24:31='major_cd',<381>,1:24], [@23,161:168='major_cd',<381>,1:161]], student_id=[[@11,66:75='student_id',<381>,1:66]]}}, window_partition_by=[{name=student_id, table_ref=null}, {name=value_partition, table_ref=null}], filters=[{name=major_cd, table_ref=student_term_major}], interface={major_cd_fill=[{name=student_id, table_ref=student_term_major}, {name=value_partition, table_ref=student_term_major}, {name=term_row, table_ref=student_term_major}, {name=major_cd, table_ref=student_term_major}]}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={value_partition=[[@13,79:93='value_partition',<381>,1:79]], term_row=[[@16,104:111='term_row',<381>,1:104]], major_cd=[[@3,24:31='major_cd',<381>,1:24], [@23,162:169='major_cd',<381>,1:162]], student_id=[[@11,67:76='student_id',<381>,1:67]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={major_cd_fill=[[@19,117:129='major_cd_fill',<381>,1:117]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=term_row, table_ref=null}], query_dictionary={major_cd_fill=[[@19,117:129='major_cd_fill',<381>,1:117]]}, table_dictionary={student_term_major={value_partition=[[@13,79:93='value_partition',<381>,1:79]], term_row=[[@16,104:111='term_row',<381>,1:104]], major_cd=[[@3,24:31='major_cd',<381>,1:24], [@23,162:169='major_cd',<381>,1:162]], student_id=[[@11,67:76='student_id',<381>,1:67]]}}, window_partition_by=[{name=student_id, table_ref=null}, {name=value_partition, table_ref=null}], filters=[{name=major_cd, table_ref=student_term_major}], interface={major_cd_fill=[{name=student_id, table_ref=student_term_major}, {name=value_partition, table_ref=student_term_major}, {name=term_row, table_ref=student_term_major}, {name=major_cd, table_ref=student_term_major}]}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={value_partition=[[@15,79:93='value_partition',<381>,1:79]], term_row=[[@18,104:111='term_row',<381>,1:104]], major_cd=[[@3,22:29='major_cd',<381>,1:22], [@25,162:169='major_cd',<381>,1:162]], student_id=[[@13,67:76='student_id',<381>,1:67]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={major_cd_fill=[[@21,117:129='major_cd_fill',<381>,1:117]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=term_row, table_ref=null}], query_dictionary={major_cd_fill=[[@21,117:129='major_cd_fill',<381>,1:117]]}, table_dictionary={student_term_major={value_partition=[[@15,79:93='value_partition',<381>,1:79]], term_row=[[@18,104:111='term_row',<381>,1:104]], major_cd=[[@3,22:29='major_cd',<381>,1:22], [@25,162:169='major_cd',<381>,1:162]], student_id=[[@13,67:76='student_id',<381>,1:67]]}}, window_partition_by=[{name=student_id, table_ref=null}, {name=value_partition, table_ref=null}], filters=[{name=major_cd, table_ref=student_term_major}], interface={major_cd_fill=[{name=student_id, table_ref=student_term_major}, {name=value_partition, table_ref=student_term_major}, {name=term_row, table_ref=student_term_major}, {name=major_cd, table_ref=student_term_major}]}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={value_partition=[[@15,80:94='value_partition',<381>,1:80]], term_row=[[@18,105:112='term_row',<381>,1:105]], major_cd=[[@3,22:29='major_cd',<381>,1:22], [@25,163:170='major_cd',<381>,1:163]], student_id=[[@13,68:77='student_id',<381>,1:68]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={major_cd_fill=[[@21,118:130='major_cd_fill',<381>,1:118]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=term_row, table_ref=null}], query_dictionary={major_cd_fill=[[@21,118:130='major_cd_fill',<381>,1:118]]}, table_dictionary={student_term_major={value_partition=[[@15,80:94='value_partition',<381>,1:80]], term_row=[[@18,105:112='term_row',<381>,1:105]], major_cd=[[@3,22:29='major_cd',<381>,1:22], [@25,163:170='major_cd',<381>,1:163]], student_id=[[@13,68:77='student_id',<381>,1:68]]}}, window_partition_by=[{name=student_id, table_ref=null}, {name=value_partition, table_ref=null}], filters=[{name=major_cd, table_ref=student_term_major}], interface={major_cd_fill=[{name=student_id, table_ref=student_term_major}, {name=value_partition, table_ref=student_term_major}, {name=term_row, table_ref=student_term_major}, {name=major_cd, table_ref=student_term_major}]}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={value_partition=[[@17,90:104='value_partition',<381>,1:90]], term_row=[[@20,115:122='term_row',<381>,1:115]], major_cd=[[@3,22:29='major_cd',<381>,1:22], [@27,173:180='major_cd',<381>,1:173]], student_id=[[@15,78:87='student_id',<381>,1:78]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={major_cd_fill=[[@23,128:140='major_cd_fill',<381>,1:128]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=term_row, table_ref=null}], query_dictionary={major_cd_fill=[[@23,128:140='major_cd_fill',<381>,1:128]]}, table_dictionary={student_term_major={value_partition=[[@17,90:104='value_partition',<381>,1:90]], term_row=[[@20,115:122='term_row',<381>,1:115]], major_cd=[[@3,22:29='major_cd',<381>,1:22], [@27,173:180='major_cd',<381>,1:173]], student_id=[[@15,78:87='student_id',<381>,1:78]]}}, window_partition_by=[{name=student_id, table_ref=null}, {name=value_partition, table_ref=null}], filters=[{name=major_cd, table_ref=student_term_major}], interface={major_cd_fill=[{name=student_id, table_ref=student_term_major}, {name=value_partition, table_ref=student_term_major}, {name=term_row, table_ref=student_term_major}, {name=major_cd, table_ref=student_term_major}]}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{student_term_major={value_partition=[[@17,89:103='value_partition',<381>,1:89]], term_row=[[@20,114:121='term_row',<381>,1:114]], major_cd=[[@3,22:29='major_cd',<381>,1:22], [@27,172:179='major_cd',<381>,1:172]], student_id=[[@15,77:86='student_id',<381>,1:77]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={major_cd_fill=[[@23,127:139='major_cd_fill',<381>,1:127]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=term_row, table_ref=null}], query_dictionary={major_cd_fill=[[@23,127:139='major_cd_fill',<381>,1:127]]}, table_dictionary={student_term_major={value_partition=[[@17,89:103='value_partition',<381>,1:89]], term_row=[[@20,114:121='term_row',<381>,1:114]], major_cd=[[@3,22:29='major_cd',<381>,1:22], [@27,172:179='major_cd',<381>,1:172]], student_id=[[@15,77:86='student_id',<381>,1:77]]}}, window_partition_by=[{name=student_id, table_ref=null}, {name=value_partition, table_ref=null}], filters=[{name=major_cd, table_ref=student_term_major}], interface={major_cd_fill=[{name=student_id, table_ref=student_term_major}, {name=value_partition, table_ref=student_term_major}, {name=term_row, table_ref=student_term_major}, {name=major_cd, table_ref=student_term_major}]}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={k_stfd=[[@9,39:44='k_stfd',<381>,1:39]], parm=[[@3,14:17='parm',<381>,1:14]], OBSERVATION_TM=[[@12,55:68='OBSERVATION_TM',<381>,1:55]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={key_rank=[[@23,137:144='key_rank',<381>,1:137]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=OBSERVATION_TM, table_ref=null}], query_dictionary={key_rank=[[@23,137:144='key_rank',<381>,1:137]]}, table_dictionary={tab1={k_stfd=[[@9,39:44='k_stfd',<381>,1:39]], parm=[[@3,14:17='parm',<381>,1:14]], OBSERVATION_TM=[[@12,55:68='OBSERVATION_TM',<381>,1:55]]}}, window_partition_by=[{name=k_stfd, table_ref=null}], interface={key_rank=[{name=k_stfd, table_ref=tab1}, {name=OBSERVATION_TM, table_ref=tab1}, {name=parm, table_ref=tab1}]}, table_alias={a=tab1}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={key_rank=[[@23,137:144='key_rank',<381>,1:137]]}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{<[HEDGSS].[student_class_lkp]>={<Classification Description>=[[@3,20:20='a',<381>,1:20], [@16,113:113='a',<381>,1:113]], <Student Classification Code>=[[@11,71:71='a',<381>,1:71]]}}",
			extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={Classification_Description=[[@23,159:184='Classification_Description',<381>,1:159]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{substitution={name=<Classification Description>, type=column}, table_ref=a}], query_dictionary={Classification_Description=[[@23,159:184='Classification_Description',<381>,1:159]]}, table_dictionary={<[HEDGSS].[student_class_lkp]>={<Classification Description>=[[@3,20:20='a',<381>,1:20], [@16,113:113='a',<381>,1:113]], <Student Classification Code>=[[@11,71:71='a',<381>,1:71]]}}, window_partition_by=[{substitution={name=<Student Classification Code>, type=column}, table_ref=a}], interface={Classification_Description=[{substitution={name=<Student Classification Code>, type=column}, table_ref=a}, {substitution={name=<Classification Description>, type=column}, table_ref=a}]}, table_alias={a=<[HEDGSS].[student_class_lkp]>}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{scbcrse={scbcrse_subj_code=[[@1,8:24='scbcrse_subj_code',<381>,1:8], [@18,96:112='scbcrse_subj_code',<381>,1:96], [@23,127:143='scbcrse_subj_code',<381>,1:127]], scbcrse_eff_term=[[@12,54:69='scbcrse_eff_term',<381>,1:54]], *=[[@7,46:46='*',<291>,1:46]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={unnamed_1=[[@13,70:70=')',<288>,1:70]], unnamed_0=[[@8,47:47=')',<288>,1:47]], subj_code=[[@3,29:37='subj_code',<381>,1:29]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
		 "{def_query0={query_dictionary={subj_code=[[@3,29:37='subj_code',<381>,1:29]], unnamed_1=[[@13,70:70=')',<288>,1:70]], unnamed_0=[[@8,47:47=')',<288>,1:47]]}, table_dictionary={scbcrse={scbcrse_subj_code=[[@1,8:24='scbcrse_subj_code',<381>,1:8], [@18,96:112='scbcrse_subj_code',<381>,1:96], [@23,127:143='scbcrse_subj_code',<381>,1:127]], scbcrse_eff_term=[[@12,54:69='scbcrse_eff_term',<381>,1:54]], *=[[@7,46:46='*',<291>,1:46]]}}, grouped_by=[{name=scbcrse_subj_code, table_ref=null}], ordered_by=[{name=scbcrse_subj_code, table_ref=null}], interface={subj_code=[{name=scbcrse_subj_code, table_ref=scbcrse}], unnamed_1=[{name=scbcrse_eff_term, table_ref=scbcrse}], unnamed_0=[]}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{standard_term={value_partition=[[@11,62:76='value_partition',<381>,1:62]], term_row=[[@14,87:94='term_row',<381>,1:87]], major_cd=[[@3,21:28='major_cd',<381>,1:21]], student_id=[[@9,50:59='student_id',<381>,1:50]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={major_cd_fill=[[@17,100:112='major_cd_fill',<381>,1:100]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=term_row, table_ref=null}], query_dictionary={major_cd_fill=[[@17,100:112='major_cd_fill',<381>,1:100]]}, table_dictionary={standard_term={value_partition=[[@11,62:76='value_partition',<381>,1:62]], term_row=[[@14,87:94='term_row',<381>,1:87]], major_cd=[[@3,21:28='major_cd',<381>,1:21]], student_id=[[@9,50:59='student_id',<381>,1:50]]}}, window_partition_by=[{name=student_id, table_ref=null}, {name=value_partition, table_ref=null}], interface={major_cd_fill=[{name=student_id, table_ref=standard_term}, {name=value_partition, table_ref=standard_term}, {name=term_row, table_ref=standard_term}, {name=major_cd, table_ref=standard_term}]}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col1=[[@5,23:26='col1',<381>,1:23]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={p=[[@8,32:32='p',<381>,1:32]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={p=[[@8,32:32='p',<381>,1:32]]}, table_dictionary={tab1={col1=[[@5,23:26='col1',<381>,1:23]]}}, interface={p=[{name=col1, table_ref=tab1}]}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col1=[[@5,21:24='col1',<381>,1:21]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={p=[[@8,30:30='p',<381>,1:30]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={p=[[@8,30:30='p',<381>,1:30]]}, table_dictionary={tab1={col1=[[@5,21:24='col1',<381>,1:21]]}}, interface={p=[{name=col1, table_ref=tab1}]}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col1=[[@1,7:8='t1',<381>,1:7], [@15,60:61='t1',<381>,1:60]]}, tab2={}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col1=[[@3,10:13='col1',<381>,1:10]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={col1=[[@3,10:13='col1',<381>,1:10]]}, table_dictionary={tab1={col1=[[@1,7:8='t1',<381>,1:7], [@15,60:61='t1',<381>,1:60]]}, tab2={}}, filters=[{name=col1, table_ref=t1}], interface={col1=[{name=col1, table_ref=t1}]}, table_alias={t1=tab1, t2=tab2}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col1=[[@1,7:10='col1',<381>,1:7], [@9,44:47='col1',<381>,1:44]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col1=[[@1,7:10='col1',<381>,1:7]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={col1=[[@1,7:10='col1',<381>,1:7]]}, table_dictionary={tab1={col1=[[@1,7:10='col1',<381>,1:7], [@9,44:47='col1',<381>,1:44]]}}, filters=[{name=col1, table_ref=tab1}], interface={col1=[{name=col1, table_ref=tab1}]}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col1=[[@1,7:10='col1',<381>,1:7], [@6,31:34='col1',<381>,1:31], [@12,59:62='col1',<381>,1:59]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col1=[[@1,7:10='col1',<381>,1:7]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={col1=[[@1,7:10='col1',<381>,1:7]]}, table_dictionary={tab1={col1=[[@1,7:10='col1',<381>,1:7], [@6,31:34='col1',<381>,1:31], [@12,59:62='col1',<381>,1:59]]}}, grouped_by=[{name=col1, table_ref=tab1}], filters=[{name=col1, table_ref=tab1}], interface={col1=[{name=col1, table_ref=tab1}]}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col1=[[@8,39:42='col1',<381>,1:39], [@11,53:56='col1',<381>,1:53], [@22,99:102='col1',<381>,1:99]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={rn=[[@14,62:63='rn',<381>,1:62]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={window_ordered_by=[{name=col1, table_ref=null}], query_dictionary={rn=[[@14,62:63='rn',<381>,1:62]]}, table_dictionary={tab1={col1=[[@8,39:42='col1',<381>,1:39], [@11,53:56='col1',<381>,1:53], [@22,99:102='col1',<381>,1:99]]}}, window_partition_by=[{name=col1, table_ref=null}], filters=[{name=col1, table_ref=tab1}], interface={rn=[{name=col1, table_ref=tab1}]}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col1=[[@5,23:26='col1',<381>,1:23], [@17,69:72='col1',<381>,1:69]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={p=[[@8,32:32='p',<381>,1:32]]}}",
		 extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={p=[[@8,32:32='p',<381>,1:32]]}, table_dictionary={tab1={col1=[[@5,23:26='col1',<381>,1:23], [@17,69:72='col1',<381>,1:69]]}}, grouped_by=[{name=col1, table_ref=null}], interface={p=[{name=col1, table_ref=tab1}]}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col1=[[@1,7:10='col1',<381>,1:7], [@10,47:50='col1',<381>,1:47]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col1=[[@1,7:10='col1',<381>,1:7]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={col1=[[@1,7:10='col1',<381>,1:7]]}, table_dictionary={tab1={col1=[[@1,7:10='col1',<381>,1:7], [@10,47:50='col1',<381>,1:47]]}}, ordered_by=[{name=col1, table_ref=tab1}], interface={col1=[{name=col1, table_ref=tab1}]}}}", extractor.getSymbolTable().toString());
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
				"{tab1={col2=[[@3,13:16='col2',<381>,1:13], [@18,83:86='col2',<381>,1:83]], col1=[[@1,7:10='col1',<381>,1:7], [@15,69:72='col1',<381>,1:69]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={col2=[[@3,13:16='col2',<381>,1:13]], col1=[[@1,7:10='col1',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={window_ordered_by=[{name=col2, table_ref=tab1}], query_dictionary={col2=[[@3,13:16='col2',<381>,1:13]], col1=[[@1,7:10='col1',<381>,1:7]]}, table_dictionary={tab1={col2=[[@3,13:16='col2',<381>,1:13], [@18,83:86='col2',<381>,1:83]], col1=[[@1,7:10='col1',<381>,1:7], [@15,69:72='col1',<381>,1:69]]}}, window_partition_by=[{name=col1, table_ref=tab1}], ordered_by=[{name=col1, table_ref=tab1}, {name=col2, table_ref=tab1}], interface={col2=[{name=col2, table_ref=tab1}], col1=[{name=col1, table_ref=tab1}]}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col1=[[@5,21:24='col1',<381>,1:21]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={p=[[@10,33:33='p',<381>,1:33]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={p=[[@10,33:33='p',<381>,1:33]]}, table_dictionary={tab1={col1=[[@5,21:24='col1',<381>,1:21]]}}, interface={p=[{name=col1, table_ref=tab1}]}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col1=[[@5,22:25='col1',<381>,1:22]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={p=[[@10,34:34='p',<381>,1:34]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={p=[[@10,34:34='p',<381>,1:34]]}, table_dictionary={tab1={col1=[[@5,22:25='col1',<381>,1:22]]}}, interface={p=[{name=col1, table_ref=tab1}]}}}", extractor.getSymbolTable().toString());
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col1=[[@5,18:21='col1',<381>,1:18]]}}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={p=[[@10,30:30='p',<381>,1:30]]}}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={p=[[@10,30:30='p',<381>,1:30]]}, table_dictionary={tab1={col1=[[@5,18:21='col1',<381>,1:18]]}}, interface={p=[{name=col1, table_ref=tab1}]}}}", extractor.getSymbolTable().toString());
	}

	// ===== end POSITION / CHARINDEX / INSTR tests =====

}

