package sql.walker;
import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

public class SqlEventWalkerDmlUpdateInsertDeleteTruncateTests extends AbstractSqlParseEventWalkerTest {

	@Test
	public void updateDictionaryHandlingQualifiedColumnsFromWindowedSubqueryV1() {
		final String query = " update employees e set score = src.acct_sales_count, rank_bucket = src.rn"
				+ "\n from (select a.emp_id, a.acct_sales_count,"
				+ "\n              row_number() over (partition by a.emp_id order by a.last_update desc) as rn"
				+ "\n         from accounts a"
				+ "\n        where a.acct_sales_count > 0) src"
				+ "\n where e.emp_id = src.emp_id and src.rn = 1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={update={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={name=acct_sales_count, table_ref=a}}, 3={alias=rn, window_function={over={partition_by={1={column={name=emp_id, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=last_update, table_ref=a}}, sort_order=desc}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=a, table=accounts}}, where={condition={left={column={name=acct_sales_count, table_ref=a}}, right={literal=0}, operator=>}}}}}, where={and={1={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=src}}, operator==}}, 2={condition={left={column={name=rn, table_ref=src}}, right={literal=1}, operator==}}}}, assignments={1={set={column={name=score, table_ref=null}}, to={column={name=acct_sales_count, table_ref=src}}}, 2={set={column={name=rank_bucket, table_ref=null}}, to={column={name=rn, table_ref=src}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={acct_sales_count=[[@22,99:99='a',<381>,2:24], [@49,248:248='a',<381>,5:14]], last_update=[[@38,183:183='a',<381>,3:64]], emp_id=[[@18,89:89='a',<381>,2:14], [@33,165:165='a',<381>,3:46]]}, employees={score=[[@4,24:28='score',<381>,1:24]], rank_bucket=[[@10,54:64='rank_bucket',<381>,1:54]], emp_id=[[@57,283:283='e',<381>,6:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={acct_sales_count=[[@24,101:116='acct_sales_count',<381>,2:26]], rn=[[@44,206:207='rn',<381>,3:87], [@12,68:70='src',<381>,1:68], [@65,309:311='src',<381>,6:33]], emp_id=[[@20,91:96='emp_id',<381>,2:16], [@61,294:296='src',<381>,6:18]]}, update1={score=[[@4,24:28='score',<381>,1:24]], rank_bucket=[[@10,54:64='rank_bucket',<381>,1:54]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{update1={assignments={score=[{name=acct_sales_count, table_ref=src}], rank_bucket=[{name=rn, table_ref=src}]}, table_dictionary={employees={score=[[@4,24:28='score',<381>,1:24]], rank_bucket=[[@10,54:64='rank_bucket',<381>,1:54]], emp_id=[[@57,283:283='e',<381>,6:7]]}}, unresolved_column={src.acct_sales_count={column={name=acct_sales_count, table_ref=src}, locations=[[@6,32:34='src',<381>,1:32]]}}, update_dictionary={score=[[@4,24:28='score',<381>,1:24]], rank_bucket=[[@10,54:64='rank_bucket',<381>,1:54]]}, def_query0={query_dictionary={acct_sales_count=[[@24,101:116='acct_sales_count',<381>,2:26]], rn=[[@44,206:207='rn',<381>,3:87], [@12,68:70='src',<381>,1:68], [@65,309:311='src',<381>,6:33]], emp_id=[[@20,91:96='emp_id',<381>,2:16], [@61,294:296='src',<381>,6:18]]}, table_dictionary={accounts={acct_sales_count=[[@22,99:99='a',<381>,2:24], [@49,248:248='a',<381>,5:14]], last_update=[[@38,183:183='a',<381>,3:64]], emp_id=[[@18,89:89='a',<381>,2:14], [@33,165:165='a',<381>,3:46]]}}, filters=[{name=acct_sales_count, table_ref=a}], interface={acct_sales_count=[{name=acct_sales_count, table_ref=a}], rn=[{name=emp_id, table_ref=a}, {name=last_update, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}, {name=rn, table_ref=src}], table_alias={e=employees, src=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void updateDictionaryHandlingQualifiedColumnsAcrossWhereSubclausesV2() {
		final String query = " update employees e set quota = src.new_quota"
				+ "\n from (select emp_id, dept_id, new_quota from quota_feed) src"
				+ "\n where e.emp_id = src.emp_id and e.dept_id = src.dept_id and e.active_flag = 1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={update={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=null}}, 2={column={name=dept_id, table_ref=null}}, 3={column={name=new_quota, table_ref=null}}}, from={table={alias=null, table=quota_feed}}}}}, where={and={1={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=src}}, operator==}}, 2={condition={left={column={name=dept_id, table_ref=e}}, right={column={name=dept_id, table_ref=src}}, operator==}}, 3={condition={left={column={name=active_flag, table_ref=e}}, right={literal=1}, operator==}}}}, assignments={1={set={column={name=quota, table_ref=null}}, to={column={name=new_quota, table_ref=src}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[quota]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{quota_feed={new_quota=[[@16,77:85='new_quota',<381>,2:31]], dept_id=[[@14,68:74='dept_id',<381>,2:22]], emp_id=[[@12,60:65='emp_id',<381>,2:14]]}, employees={quota=[[@4,24:28='quota',<381>,1:24]], active_flag=[[@38,169:169='e',<381>,3:61]], dept_id=[[@30,141:141='e',<381>,3:33]], emp_id=[[@22,115:115='e',<381>,3:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={new_quota=[[@16,77:85='new_quota',<381>,2:31]], dept_id=[[@14,68:74='dept_id',<381>,2:22], [@34,153:155='src',<381>,3:45]], emp_id=[[@12,60:65='emp_id',<381>,2:14], [@26,126:128='src',<381>,3:18]]}, update1={quota=[[@4,24:28='quota',<381>,1:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{update1={assignments={quota=[{name=new_quota, table_ref=src}]}, table_dictionary={employees={active_flag=[[@38,169:169='e',<381>,3:61]], dept_id=[[@30,141:141='e',<381>,3:33]], quota=[[@4,24:28='quota',<381>,1:24]], emp_id=[[@22,115:115='e',<381>,3:7]]}}, unresolved_column={src.new_quota={column={name=new_quota, table_ref=src}, locations=[[@6,32:34='src',<381>,1:32]]}}, update_dictionary={quota=[[@4,24:28='quota',<381>,1:24]]}, def_query0={query_dictionary={new_quota=[[@16,77:85='new_quota',<381>,2:31]], dept_id=[[@14,68:74='dept_id',<381>,2:22], [@34,153:155='src',<381>,3:45]], emp_id=[[@12,60:65='emp_id',<381>,2:14], [@26,126:128='src',<381>,3:18]]}, table_dictionary={quota_feed={new_quota=[[@16,77:85='new_quota',<381>,2:31]], dept_id=[[@14,68:74='dept_id',<381>,2:22]], emp_id=[[@12,60:65='emp_id',<381>,2:14]]}}, interface={new_quota=[{name=new_quota, table_ref=quota_feed}], dept_id=[{name=dept_id, table_ref=quota_feed}], emp_id=[{name=emp_id, table_ref=quota_feed}]}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}, {name=dept_id, table_ref=e}, {name=dept_id, table_ref=src}, {name=active_flag, table_ref=e}], table_alias={e=employees, src=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void updateDictionaryHandlingUnqualifiedFallsBackToTargetTableV3() {
		final String query = " update employees e set review_flag = missing_flag"
				+ "\n from (select emp_id, score from perf_feed) src"
				+ "\n where e.emp_id = src.emp_id and src.score > 0";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={update={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=null}}, 2={column={name=score, table_ref=null}}}, from={table={alias=null, table=perf_feed}}}}}, where={and={1={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=src}}, operator==}}, 2={condition={left={column={name=score, table_ref=src}}, right={literal=0}, operator=>}}}}, assignments={1={set={column={name=review_flag, table_ref=null}}, to={column={name=missing_flag, table_ref=null}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[review_flag]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{perf_feed={score=[[@12,73:77='score',<381>,2:22]], emp_id=[[@10,65:70='emp_id',<381>,2:14]]}, employees={missing_flag=[[@6,38:49='missing_flag',<381>,1:38]], review_flag=[[@4,24:34='review_flag',<381>,1:24]], emp_id=[[@18,106:106='e',<381>,3:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@12,73:77='score',<381>,2:22], [@26,132:134='src',<381>,3:33]], emp_id=[[@10,65:70='emp_id',<381>,2:14], [@22,117:119='src',<381>,3:18]]}, update1={review_flag=[[@4,24:34='review_flag',<381>,1:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{update1={assignments={review_flag=[{name=missing_flag, table_ref=null}]}, table_dictionary={employees={review_flag=[[@4,24:34='review_flag',<381>,1:24]], missing_flag=[[@6,38:49='missing_flag',<381>,1:38]], emp_id=[[@18,106:106='e',<381>,3:7]]}}, update_dictionary={review_flag=[[@4,24:34='review_flag',<381>,1:24]]}, def_query0={query_dictionary={score=[[@12,73:77='score',<381>,2:22], [@26,132:134='src',<381>,3:33]], emp_id=[[@10,65:70='emp_id',<381>,2:14], [@22,117:119='src',<381>,3:18]]}, table_dictionary={perf_feed={score=[[@12,73:77='score',<381>,2:22]], emp_id=[[@10,65:70='emp_id',<381>,2:14]]}}, interface={score=[{name=score, table_ref=perf_feed}], emp_id=[{name=emp_id, table_ref=perf_feed}]}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}, {name=score, table_ref=src}], table_alias={e=employees, src=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void updateDictionaryHandlingUnqualifiedWithAdditionalPhysicalTableStillResolvesV4() {
		final String query = " update employees e set review_flag = src.score"
				+ "\n from (select emp_id, score from perf_feed) src"
				+ "\n join audit_flags af on src.emp_id = af.emp_id"
				+ "\n where e.emp_id = src.emp_id and e.missing_flag = af.missing_flag and missing_flag > 0";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Snippet snippet = extractor.getSnippet();
		assertNoFatalErrors(extractor);
		assertDiagnosticCountBySeverity(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				null,
				"missing_flag",
				1);

		Assert.assertEquals("AST is wrong", "{SQL={update={from={join={1={table={alias=src, query={select={1={column={name=emp_id, table_ref=null}}, 2={column={name=score, table_ref=null}}}, from={table={alias=null, table=perf_feed}}}}}, 2={join=join, on={condition={left={column={name=emp_id, table_ref=src}}, right={column={name=emp_id, table_ref=af}}, operator==}}}, 3={table={alias=af, table=audit_flags}}}}, where={and={1={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=src}}, operator==}}, 2={condition={left={column={name=missing_flag, table_ref=e}}, right={column={name=missing_flag, table_ref=af}}, operator==}}, 3={condition={left={column={name=missing_flag, table_ref=null}}, right={literal=0}, operator=>}}}}, assignments={1={set={column={name=review_flag, table_ref=null}}, to={column={name=score, table_ref=src}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[review_flag]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{perf_feed={score=[[@14,70:74='score',<381>,2:22]], emp_id=[[@12,62:67='emp_id',<381>,2:14]]}, employees={missing_flag=[[@39,176:176='e',<381>,4:33], [@47,213:224='missing_flag',<381>,4:70]], review_flag=[[@4,24:34='review_flag',<381>,1:24]], emp_id=[[@31,150:150='e',<381>,4:7]]}, audit_flags={missing_flag=[[@43,193:194='af',<381>,4:50]], emp_id=[[@27,133:134='af',<381>,3:37]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@14,70:74='score',<381>,2:22]], emp_id=[[@12,62:67='emp_id',<381>,2:14], [@23,120:122='src',<381>,3:24], [@35,161:163='src',<381>,4:18]]}, update1={review_flag=[[@4,24:34='review_flag',<381>,1:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{update1={assignments={review_flag=[{name=score, table_ref=src}]}, table_dictionary={employees={review_flag=[[@4,24:34='review_flag',<381>,1:24]], missing_flag=[[@39,176:176='e',<381>,4:33], [@47,213:224='missing_flag',<381>,4:70]], emp_id=[[@31,150:150='e',<381>,4:7]]}, audit_flags={missing_flag=[[@43,193:194='af',<381>,4:50]], emp_id=[[@27,133:134='af',<381>,3:37]]}}, unresolved_column={src.score={column={name=score, table_ref=src}, locations=[[@6,38:40='src',<381>,1:38]]}}, update_dictionary={review_flag=[[@4,24:34='review_flag',<381>,1:24]]}, def_query0={query_dictionary={score=[[@14,70:74='score',<381>,2:22]], emp_id=[[@12,62:67='emp_id',<381>,2:14], [@23,120:122='src',<381>,3:24], [@35,161:163='src',<381>,4:18]]}, table_dictionary={perf_feed={score=[[@14,70:74='score',<381>,2:22]], emp_id=[[@12,62:67='emp_id',<381>,2:14]]}}, interface={score=[{name=score, table_ref=perf_feed}], emp_id=[{name=emp_id, table_ref=perf_feed}]}}, filters=[{name=emp_id, table_ref=src}, {name=emp_id, table_ref=af}, {name=emp_id, table_ref=e}, {name=missing_flag, table_ref=e}, {name=missing_flag, table_ref=af}, {name=missing_flag, table_ref=null}], table_alias={e=employees, src=query0, af=audit_flags}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void updateDictionaryHandlingGroupByHavingSubqueryAndUnqualifiedRhsV5() {
		final String query = " update employees e set agg_score = src.total_score, stale_flag = orphan_marker"
				+ "\n from (select a.emp_id, sum(a.score) as total_score"
				+ "\n         from accounts a"
				+ "\n        group by a.emp_id"
				+ "\n       having sum(a.score) > e.score) src"
				+ "\n where e.emp_id = src.emp_id";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={update={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={function={function_name=sum, qualifier=null, parameters={column={name=score, table_ref=a}}}, alias=total_score}}, having={condition={left={function={function_name=sum, qualifier=null, parameters={column={name=score, table_ref=a}}}}, right={column={name=score, table_ref=e}}, operator=>}}, from={table={alias=a, table=accounts}}, groupby={1={column={name=emp_id, table_ref=a}}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=src}}, operator==}}, assignments={1={set={column={name=agg_score, table_ref=null}}, to={column={name=total_score, table_ref=src}}}, 2={set={column={name=stale_flag, table_ref=null}}, to={column={name=orphan_marker, table_ref=null}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[stale_flag, agg_score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@22,108:108='a',<381>,2:28], [@39,201:201='a',<381>,5:18]], emp_id=[[@16,94:94='a',<381>,2:14], [@33,174:174='a',<381>,4:17]]}, employees={score=[[@44,212:212='e',<381>,5:29]], stale_flag=[[@10,53:62='stale_flag',<381>,1:53]], agg_score=[[@4,24:32='agg_score',<381>,1:24]], emp_id=[[@50,232:232='e',<381>,6:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={total_score=[[@27,120:130='total_score',<381>,2:40]], emp_id=[[@18,96:101='emp_id',<381>,2:16], [@54,243:245='src',<381>,6:18]]}, update1={agg_score=[[@4,24:32='agg_score',<381>,1:24]], stale_flag=[[@10,53:62='stale_flag',<381>,1:53]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{update1={assignments={stale_flag=[{name=orphan_marker, table_ref=null}], agg_score=[{name=total_score, table_ref=src}]}, table_dictionary={employees={score=[[@44,212:212='e',<381>,5:29]], agg_score=[[@4,24:32='agg_score',<381>,1:24]], stale_flag=[[@10,53:62='stale_flag',<381>,1:53]], emp_id=[[@50,232:232='e',<381>,6:7]]}}, update_dictionary={stale_flag=[[@10,53:62='stale_flag',<381>,1:53]], agg_score=[[@4,24:32='agg_score',<381>,1:24]]}, def_query0={query_dictionary={total_score=[[@27,120:130='total_score',<381>,2:40]], emp_id=[[@18,96:101='emp_id',<381>,2:16], [@54,243:245='src',<381>,6:18]]}, table_dictionary={accounts={score=[[@22,108:108='a',<381>,2:28], [@39,201:201='a',<381>,5:18]], emp_id=[[@16,94:94='a',<381>,2:14], [@33,174:174='a',<381>,4:17]]}}, grouped_by=[{name=emp_id, table_ref=a}], filters=[{name=score, table_ref=a}, {name=score, table_ref=e}], interface={total_score=[{name=score, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}], table_alias={e=employees, src=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void updateDictionaryHandlingOrderBySubqueryAndUnqualifiedRhsV6() {
		final String query = " update employees e set most_recent_update = src.last_update, unknown_rhs = shadow_col"
				+ "\n from (select a.emp_id, a.last_update"
				+ "\n         from accounts a"
				+ "\n        order by a.last_update desc) src"
				+ "\n where e.emp_id = src.emp_id";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={update={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={name=last_update, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=last_update, table_ref=a}}, sort_order=desc}}, from={table={alias=a, table=accounts}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=src}}, operator==}}, assignments={1={set={column={name=most_recent_update, table_ref=null}}, to={column={name=last_update, table_ref=src}}}, 2={set={column={name=unknown_rhs, table_ref=null}}, to={column={name=shadow_col, table_ref=null}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unknown_rhs, most_recent_update]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={last_update=[[@20,111:111='a',<381>,2:24], [@28,167:167='a',<381>,4:17]], emp_id=[[@16,101:101='a',<381>,2:14]]}, employees={unknown_rhs=[[@10,62:72='unknown_rhs',<381>,1:62]], most_recent_update=[[@4,24:41='most_recent_update',<381>,1:24]], shadow_col=[[@12,76:85='shadow_col',<381>,1:76]], emp_id=[[@35,198:198='e',<381>,5:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={last_update=[[@22,113:123='last_update',<381>,2:26]], emp_id=[[@18,103:108='emp_id',<381>,2:16], [@39,209:211='src',<381>,5:18]]}, update1={most_recent_update=[[@4,24:41='most_recent_update',<381>,1:24]], unknown_rhs=[[@10,62:72='unknown_rhs',<381>,1:62]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{update1={assignments={unknown_rhs=[{name=shadow_col, table_ref=null}], most_recent_update=[{name=last_update, table_ref=src}]}, table_dictionary={employees={most_recent_update=[[@4,24:41='most_recent_update',<381>,1:24]], shadow_col=[[@12,76:85='shadow_col',<381>,1:76]], unknown_rhs=[[@10,62:72='unknown_rhs',<381>,1:62]], emp_id=[[@35,198:198='e',<381>,5:7]]}}, unresolved_column={src.last_update={column={name=last_update, table_ref=src}, locations=[[@6,45:47='src',<381>,1:45]]}}, update_dictionary={unknown_rhs=[[@10,62:72='unknown_rhs',<381>,1:62]], most_recent_update=[[@4,24:41='most_recent_update',<381>,1:24]]}, def_query0={query_dictionary={last_update=[[@22,113:123='last_update',<381>,2:26]], emp_id=[[@18,103:108='emp_id',<381>,2:16], [@39,209:211='src',<381>,5:18]]}, table_dictionary={accounts={last_update=[[@20,111:111='a',<381>,2:24], [@28,167:167='a',<381>,4:17]], emp_id=[[@16,101:101='a',<381>,2:14]]}}, ordered_by=[{name=last_update, table_ref=a}], interface={last_update=[{name=last_update, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}], table_alias={e=employees, src=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void updateDictionaryHandlingQualifySubqueryAndUnqualifiedRhsV7() {
		final String query = " update employees e set top_score = src.score, fallback_note = unqualified_note"
				+ "\n from (select a.emp_id, a.score,"
				+ "\n              row_number() over (partition by a.emp_id order by a.last_update desc) as rn"
				+ "\n         from accounts a"
				+ "\n      qualify rn = 1) src"
				+ "\n where e.emp_id = src.emp_id";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={update={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={name=score, table_ref=a}}, 3={alias=rn, window_function={over={partition_by={1={column={name=emp_id, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=last_update, table_ref=a}}, sort_order=desc}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=a, table=accounts}}, qualify={condition={left={column={name=rn, table_ref=null}}, right={literal=1}, operator==}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=src}}, operator==}}, assignments={1={set={column={name=top_score, table_ref=null}}, to={column={name=score, table_ref=src}}}, 2={set={column={name=fallback_note, table_ref=null}}, to={column={name=unqualified_note, table_ref=null}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[fallback_note, top_score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@20,104:104='a',<381>,2:24]], last_update=[[@36,177:177='a',<381>,3:64]], rn=[[@47,242:243='rn',<381>,5:14]], emp_id=[[@16,94:94='a',<381>,2:14], [@31,159:159='a',<381>,3:46]]}, employees={fallback_note=[[@10,47:59='fallback_note',<381>,1:47]], unqualified_note=[[@12,63:78='unqualified_note',<381>,1:63]], top_score=[[@4,24:32='top_score',<381>,1:24]], emp_id=[[@53,261:261='e',<381>,6:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@22,106:110='score',<381>,2:26]], rn=[[@42,200:201='rn',<381>,3:87]], emp_id=[[@18,96:101='emp_id',<381>,2:16], [@57,272:274='src',<381>,6:18]]}, update1={fallback_note=[[@10,47:59='fallback_note',<381>,1:47]], top_score=[[@4,24:32='top_score',<381>,1:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{update1={assignments={fallback_note=[{name=unqualified_note, table_ref=null}], top_score=[{name=score, table_ref=src}]}, table_dictionary={employees={fallback_note=[[@10,47:59='fallback_note',<381>,1:47]], unqualified_note=[[@12,63:78='unqualified_note',<381>,1:63]], top_score=[[@4,24:32='top_score',<381>,1:24]], emp_id=[[@53,261:261='e',<381>,6:7]]}}, unresolved_column={src.score={column={name=score, table_ref=src}, locations=[[@6,36:38='src',<381>,1:36]]}}, update_dictionary={fallback_note=[[@10,47:59='fallback_note',<381>,1:47]], top_score=[[@4,24:32='top_score',<381>,1:24]]}, def_query0={query_dictionary={score=[[@22,106:110='score',<381>,2:26]], rn=[[@42,200:201='rn',<381>,3:87]], emp_id=[[@18,96:101='emp_id',<381>,2:16], [@57,272:274='src',<381>,6:18]]}, table_dictionary={accounts={score=[[@20,104:104='a',<381>,2:24]], last_update=[[@36,177:177='a',<381>,3:64]], rn=[[@47,242:243='rn',<381>,5:14]], emp_id=[[@16,94:94='a',<381>,2:14], [@31,159:159='a',<381>,3:46]]}}, filters=[{name=rn, table_ref=accounts}], interface={score=[{name=score, table_ref=a}], rn=[{name=emp_id, table_ref=a}, {name=last_update, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}], table_alias={e=employees, src=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void updateDictionaryHandlingWhereInSubqueryWithTargetTableRefAndOrphanRhsV8() {
		final String query = " update employees e set agg_score = src.total_score, stale_flag = orphan_marker"
				+ "\n from (select a.emp_id, sum(a.score) as total_score"
				+ "\n         from accounts a"
				+ "\n        where a.score > e.min_score"
				+ "\n        group by a.emp_id) src"
				+ "\n where e.emp_id = src.emp_id";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={update={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={function={function_name=sum, qualifier=null, parameters={column={name=score, table_ref=a}}}, alias=total_score}}, from={table={alias=a, table=accounts}}, where={condition={left={column={name=score, table_ref=a}}, right={column={name=min_score, table_ref=e}}, operator=>}}, groupby={1={column={name=emp_id, table_ref=a}}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=src}}, operator==}}, assignments={1={set={column={name=agg_score, table_ref=null}}, to={column={name=total_score, table_ref=src}}}, 2={set={column={name=stale_flag, table_ref=null}}, to={column={name=orphan_marker, table_ref=null}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[stale_flag, agg_score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@22,108:108='a',<381>,2:28], [@32,171:171='a',<381>,4:14]], emp_id=[[@16,94:94='a',<381>,2:14], [@41,210:210='a',<381>,5:17]]}, employees={stale_flag=[[@10,53:62='stale_flag',<381>,1:53]], agg_score=[[@4,24:32='agg_score',<381>,1:24]], min_score=[[@36,181:181='e',<381>,4:24]], emp_id=[[@47,231:231='e',<381>,6:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={total_score=[[@27,120:130='total_score',<381>,2:40]], emp_id=[[@18,96:101='emp_id',<381>,2:16], [@51,242:244='src',<381>,6:18]]}, update1={agg_score=[[@4,24:32='agg_score',<381>,1:24]], stale_flag=[[@10,53:62='stale_flag',<381>,1:53]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{update1={assignments={stale_flag=[{name=orphan_marker, table_ref=null}], agg_score=[{name=total_score, table_ref=src}]}, table_dictionary={employees={agg_score=[[@4,24:32='agg_score',<381>,1:24]], stale_flag=[[@10,53:62='stale_flag',<381>,1:53]], min_score=[[@36,181:181='e',<381>,4:24]], emp_id=[[@47,231:231='e',<381>,6:7]]}}, update_dictionary={stale_flag=[[@10,53:62='stale_flag',<381>,1:53]], agg_score=[[@4,24:32='agg_score',<381>,1:24]]}, def_query0={query_dictionary={total_score=[[@27,120:130='total_score',<381>,2:40]], emp_id=[[@18,96:101='emp_id',<381>,2:16], [@51,242:244='src',<381>,6:18]]}, table_dictionary={accounts={score=[[@22,108:108='a',<381>,2:28], [@32,171:171='a',<381>,4:14]], emp_id=[[@16,94:94='a',<381>,2:14], [@41,210:210='a',<381>,5:17]]}}, grouped_by=[{name=emp_id, table_ref=a}], filters=[{name=score, table_ref=a}, {name=min_score, table_ref=e}], interface={total_score=[{name=score, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}], table_alias={e=employees, src=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void updateDictionaryHandlingJoinOnInSubqueryWithTargetTableRefAndOrphanRhsV9() {
		final String query = " update employees e set agg_score = src.total_score, stale_flag = orphan_marker"
				+ "\n from (select a.emp_id, a.score as total_score"
				+ "\n         from accounts a"
				+ "\n         join departments d on a.dept_id = d.dept_id and d.region = e.region) src"
				+ "\n where e.emp_id = src.emp_id";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={update={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={name=score, table_ref=a}, alias=total_score}}, from={join={1={table={alias=a, table=accounts}}, 2={join=join, on={and={1={condition={left={column={name=dept_id, table_ref=a}}, right={column={name=dept_id, table_ref=d}}, operator==}}, 2={condition={left={column={name=region, table_ref=d}}, right={column={name=region, table_ref=e}}, operator==}}}}}, 3={table={alias=d, table=departments}}}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=src}}, operator==}}, assignments={1={set={column={name=agg_score, table_ref=null}}, to={column={name=total_score, table_ref=src}}}, 2={set={column={name=stale_flag, table_ref=null}}, to={column={name=orphan_marker, table_ref=null}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[stale_flag, agg_score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@20,104:104='a',<381>,2:24]], dept_id=[[@32,183:183='a',<381>,4:31]], emp_id=[[@16,94:94='a',<381>,2:14]]}, departments={dept_id=[[@36,195:195='d',<381>,4:43]], region=[[@40,209:209='d',<381>,4:57]]}, employees={stale_flag=[[@10,53:62='stale_flag',<381>,1:53]], agg_score=[[@4,24:32='agg_score',<381>,1:24]], region=[[@44,220:220='e',<381>,4:68]], emp_id=[[@50,241:241='e',<381>,5:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={total_score=[[@24,115:125='total_score',<381>,2:35]], emp_id=[[@18,96:101='emp_id',<381>,2:16], [@54,252:254='src',<381>,5:18]]}, update1={agg_score=[[@4,24:32='agg_score',<381>,1:24]], stale_flag=[[@10,53:62='stale_flag',<381>,1:53]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{update1={assignments={stale_flag=[{name=orphan_marker, table_ref=null}], agg_score=[{name=total_score, table_ref=src}]}, table_dictionary={employees={agg_score=[[@4,24:32='agg_score',<381>,1:24]], region=[[@44,220:220='e',<381>,4:68]], stale_flag=[[@10,53:62='stale_flag',<381>,1:53]], emp_id=[[@50,241:241='e',<381>,5:7]]}}, update_dictionary={stale_flag=[[@10,53:62='stale_flag',<381>,1:53]], agg_score=[[@4,24:32='agg_score',<381>,1:24]]}, def_query0={query_dictionary={total_score=[[@24,115:125='total_score',<381>,2:35]], emp_id=[[@18,96:101='emp_id',<381>,2:16], [@54,252:254='src',<381>,5:18]]}, table_dictionary={accounts={score=[[@20,104:104='a',<381>,2:24]], dept_id=[[@32,183:183='a',<381>,4:31]], emp_id=[[@16,94:94='a',<381>,2:14]]}, departments={dept_id=[[@36,195:195='d',<381>,4:43]], region=[[@40,209:209='d',<381>,4:57]]}}, filters=[{name=dept_id, table_ref=a}, {name=dept_id, table_ref=d}, {name=region, table_ref=d}, {name=region, table_ref=e}], interface={total_score=[{name=score, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts, d=departments}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}], table_alias={e=employees, src=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void updateDictionaryHandlingQualifyInSubqueryWithTargetTableRefAndOrphanRhsV10() {
		final String query = " update employees e set top_score = src.score, stale_flag = orphan_marker"
				+ "\n from (select a.emp_id, a.score,"
				+ "\n              row_number() over (partition by a.emp_id order by a.score desc) as rn"
				+ "\n         from accounts a"
				+ "\n        qualify rn <= e.max_rank) src"
				+ "\n where e.emp_id = src.emp_id";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={update={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={name=score, table_ref=a}}, 3={alias=rn, window_function={over={partition_by={1={column={name=emp_id, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=score, table_ref=a}}, sort_order=desc}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=a, table=accounts}}, qualify={condition={left={column={name=rn, table_ref=null}}, right={column={name=max_rank, table_ref=e}}, operator=<=}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=src}}, operator==}}, assignments={1={set={column={name=top_score, table_ref=null}}, to={column={name=score, table_ref=src}}}, 2={set={column={name=stale_flag, table_ref=null}}, to={column={name=orphan_marker, table_ref=null}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[stale_flag, top_score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@20,98:98='a',<381>,2:24], [@36,171:171='a',<381>,3:64]], rn=[[@47,232:233='rn',<381>,5:16]], emp_id=[[@16,88:88='a',<381>,2:14], [@31,153:153='a',<381>,3:46]]}, employees={stale_flag=[[@10,47:56='stale_flag',<381>,1:47]], max_rank=[[@49,238:238='e',<381>,5:22]], top_score=[[@4,24:32='top_score',<381>,1:24]], emp_id=[[@55,261:261='e',<381>,6:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@22,100:104='score',<381>,2:26]], rn=[[@42,188:189='rn',<381>,3:81]], emp_id=[[@18,90:95='emp_id',<381>,2:16], [@59,272:274='src',<381>,6:18]]}, update1={stale_flag=[[@10,47:56='stale_flag',<381>,1:47]], top_score=[[@4,24:32='top_score',<381>,1:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{update1={assignments={stale_flag=[{name=orphan_marker, table_ref=null}], top_score=[{name=score, table_ref=src}]}, table_dictionary={employees={stale_flag=[[@10,47:56='stale_flag',<381>,1:47]], max_rank=[[@49,238:238='e',<381>,5:22]], top_score=[[@4,24:32='top_score',<381>,1:24]], emp_id=[[@55,261:261='e',<381>,6:7]]}}, update_dictionary={stale_flag=[[@10,47:56='stale_flag',<381>,1:47]], top_score=[[@4,24:32='top_score',<381>,1:24]]}, def_query0={query_dictionary={score=[[@22,100:104='score',<381>,2:26]], rn=[[@42,188:189='rn',<381>,3:81]], emp_id=[[@18,90:95='emp_id',<381>,2:16], [@59,272:274='src',<381>,6:18]]}, table_dictionary={accounts={score=[[@20,98:98='a',<381>,2:24], [@36,171:171='a',<381>,3:64]], rn=[[@47,232:233='rn',<381>,5:16]], emp_id=[[@16,88:88='a',<381>,2:14], [@31,153:153='a',<381>,3:46]]}}, filters=[{name=rn, table_ref=accounts}, {name=max_rank, table_ref=e}], interface={score=[{name=score, table_ref=a}], rn=[{name=emp_id, table_ref=a}, {name=score, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}], table_alias={e=employees, src=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void updateDictionaryHandlingOrderByInSubqueryWithTargetTableRefAndOrphanRhsV11() {
		final String query = " update employees e set most_recent_score = src.score, stale_flag = orphan_marker"
				+ "\n from (select a.emp_id, a.score"
				+ "\n         from accounts a"
				+ "\n        order by e.sort_priority asc, a.score desc) src"
				+ "\n where e.emp_id = src.emp_id";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={update={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={name=score, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=sort_priority, table_ref=e}}, sort_order=asc}, 2={null_order=null, predicand={column={name=score, table_ref=a}}, sort_order=desc}}, from={table={alias=a, table=accounts}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=src}}, operator==}}, assignments={1={set={column={name=most_recent_score, table_ref=null}}, to={column={name=score, table_ref=src}}}, 2={set={column={name=stale_flag, table_ref=null}}, to={column={name=orphan_marker, table_ref=null}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[stale_flag, most_recent_score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@20,106:106='a',<381>,2:24], [@33,177:177='a',<381>,4:38]], emp_id=[[@16,96:96='a',<381>,2:14]]}, employees={stale_flag=[[@10,55:64='stale_flag',<381>,1:55]], most_recent_score=[[@4,24:40='most_recent_score',<381>,1:24]], sort_priority=[[@28,156:156='e',<381>,4:17]], emp_id=[[@40,202:202='e',<381>,5:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@22,108:112='score',<381>,2:26]], emp_id=[[@18,98:103='emp_id',<381>,2:16], [@44,213:215='src',<381>,5:18]]}, update1={most_recent_score=[[@4,24:40='most_recent_score',<381>,1:24]], stale_flag=[[@10,55:64='stale_flag',<381>,1:55]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{update1={assignments={stale_flag=[{name=orphan_marker, table_ref=null}], most_recent_score=[{name=score, table_ref=src}]}, table_dictionary={employees={most_recent_score=[[@4,24:40='most_recent_score',<381>,1:24]], sort_priority=[[@28,156:156='e',<381>,4:17]], stale_flag=[[@10,55:64='stale_flag',<381>,1:55]], emp_id=[[@40,202:202='e',<381>,5:7]]}}, update_dictionary={stale_flag=[[@10,55:64='stale_flag',<381>,1:55]], most_recent_score=[[@4,24:40='most_recent_score',<381>,1:24]]}, def_query0={query_dictionary={score=[[@22,108:112='score',<381>,2:26]], emp_id=[[@18,98:103='emp_id',<381>,2:16], [@44,213:215='src',<381>,5:18]]}, table_dictionary={accounts={score=[[@20,106:106='a',<381>,2:24], [@33,177:177='a',<381>,4:38]], emp_id=[[@16,96:96='a',<381>,2:14]]}}, ordered_by=[{name=sort_priority, table_ref=e}, {name=score, table_ref=a}], interface={score=[{name=score, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}], table_alias={e=employees, src=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void updateDictionaryHandlingNoQualifiedSubqueryBodyWithQualifiedAssignmentAndOrphanRhsV12() {
		final String query = " update employees e set latest_score = src.score, stale_flag = orphan_marker"
				+ "\n from (select a.emp_id, a.score"
				+ "\n         from accounts a) src"
				+ "\n where e.emp_id = src.emp_id";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={update={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={name=score, table_ref=a}}}, from={table={alias=a, table=accounts}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=src}}, operator==}}, assignments={1={set={column={name=latest_score, table_ref=null}}, to={column={name=score, table_ref=src}}}, 2={set={column={name=stale_flag, table_ref=null}}, to={column={name=orphan_marker, table_ref=null}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[latest_score, stale_flag]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@20,101:101='a',<381>,2:24]], emp_id=[[@16,91:91='a',<381>,2:14]]}, employees={latest_score=[[@4,24:35='latest_score',<381>,1:24]], stale_flag=[[@10,50:59='stale_flag',<381>,1:50]], orphan_marker=[[@12,63:75='orphan_marker',<381>,1:63]], emp_id=[[@29,146:146='e',<381>,4:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@22,103:107='score',<381>,2:26]], emp_id=[[@18,93:98='emp_id',<381>,2:16], [@33,157:159='src',<381>,4:18]]}, update1={latest_score=[[@4,24:35='latest_score',<381>,1:24]], stale_flag=[[@10,50:59='stale_flag',<381>,1:50]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{update1={assignments={latest_score=[{name=score, table_ref=src}], stale_flag=[{name=orphan_marker, table_ref=null}]}, table_dictionary={employees={latest_score=[[@4,24:35='latest_score',<381>,1:24]], orphan_marker=[[@12,63:75='orphan_marker',<381>,1:63]], stale_flag=[[@10,50:59='stale_flag',<381>,1:50]], emp_id=[[@29,146:146='e',<381>,4:7]]}}, unresolved_column={src.score={column={name=score, table_ref=src}, locations=[[@6,39:41='src',<381>,1:39]]}}, update_dictionary={latest_score=[[@4,24:35='latest_score',<381>,1:24]], stale_flag=[[@10,50:59='stale_flag',<381>,1:50]]}, def_query0={query_dictionary={score=[[@22,103:107='score',<381>,2:26]], emp_id=[[@18,93:98='emp_id',<381>,2:16], [@33,157:159='src',<381>,4:18]]}, table_dictionary={accounts={score=[[@20,101:101='a',<381>,2:24]], emp_id=[[@16,91:91='a',<381>,2:14]]}}, interface={score=[{name=score, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}], table_alias={e=employees, src=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertDictionaryHandlingQualifiedColumnsFromWindowedSubqueryAndOrphanRhsV1() {
		final String query = " insert into employees (score, rank_bucket, orphan_sink)"
				+ "\n select src.acct_sales_count, src.rn, orphan_marker"
				+ "\n from (select a.emp_id, a.acct_sales_count,"
				+ "\n              row_number() over (partition by a.emp_id order by a.last_update desc) as rn"
				+ "\n         from accounts a"
				+ "\n        where a.acct_sales_count > 0) src";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={preamble=insert_into, from={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={name=acct_sales_count, table_ref=a}}, 3={alias=rn, window_function={over={partition_by={1={column={name=emp_id, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=last_update, table_ref=a}}, sort_order=desc}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=a, table=accounts}}, where={condition={left={column={name=acct_sales_count, table_ref=a}}, right={literal=0}, operator=>}}}}}, select={1={column={name=acct_sales_count, table_ref=src}}, 2={column={name=rn, table_ref=src}}, 3={column={name=orphan_marker, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}, 3={column={name=orphan_sink, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[orphan_sink, score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={acct_sales_count=[[@27,133:133='a',<381>,3:24], [@54,282:282='a',<381>,6:14]], last_update=[[@43,217:217='a',<381>,4:64]], emp_id=[[@23,123:123='a',<381>,3:14], [@38,199:199='a',<381>,4:46]]}, employees={orphan_sink=[[@8,44:54='orphan_sink',<381>,1:44]], score=[[@4,24:28='score',<381>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<381>,1:31]], orphan_marker=[[@19,95:107='orphan_marker',<381>,2:38]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={acct_sales_count=[[@29,135:150='acct_sales_count',<381>,3:26], [@11,65:67='src',<381>,2:8]], rn=[[@49,240:241='rn',<381>,4:87], [@15,87:89='src',<381>,2:30]], emp_id=[[@25,125:130='emp_id',<381>,3:16]]}, query1={acct_sales_count=[[@13,69:84='acct_sales_count',<381>,2:12]], orphan_marker=[[@19,95:107='orphan_marker',<381>,2:38]], rn=[[@17,91:92='rn',<381>,2:34]]}, insert2={orphan_sink=[[@8,44:54='orphan_sink',<381>,1:44]], score=[[@4,24:28='score',<381>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<381>,1:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{insert2={query_dictionary={orphan_sink=[[@8,44:54='orphan_sink',<381>,1:44]], score=[[@4,24:28='score',<381>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<381>,1:31]]}, table_dictionary={employees={orphan_sink=[[@8,44:54='orphan_sink',<381>,1:44]], score=[[@4,24:28='score',<381>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<381>,1:31]], orphan_marker=[[@19,95:107='orphan_marker',<381>,2:38]]}}, def_query1={query_dictionary={acct_sales_count=[[@13,69:84='acct_sales_count',<381>,2:12]], orphan_marker=[[@19,95:107='orphan_marker',<381>,2:38]], rn=[[@17,91:92='rn',<381>,2:34]]}, table_dictionary={}, def_query0={query_dictionary={acct_sales_count=[[@29,135:150='acct_sales_count',<381>,3:26], [@11,65:67='src',<381>,2:8]], rn=[[@49,240:241='rn',<381>,4:87], [@15,87:89='src',<381>,2:30]], emp_id=[[@25,125:130='emp_id',<381>,3:16]]}, table_dictionary={accounts={acct_sales_count=[[@27,133:133='a',<381>,3:24], [@54,282:282='a',<381>,6:14]], last_update=[[@43,217:217='a',<381>,4:64]], emp_id=[[@23,123:123='a',<381>,3:14], [@38,199:199='a',<381>,4:46]]}}, _tmp_insert_source_select_sequence=[emp_id, acct_sales_count, rn], filters=[{name=acct_sales_count, table_ref=a}], interface={acct_sales_count=[{name=acct_sales_count, table_ref=a}], rn=[{name=emp_id, table_ref=a}, {name=last_update, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, interface={acct_sales_count=[{name=acct_sales_count, table_ref=src}], orphan_marker=[{name=orphan_marker, table_ref=null}], rn=[{name=rn, table_ref=src}]}, table_alias={src=query0}}, interface={score=[{name=acct_sales_count, table_ref=query1}], rank_bucket=[{name=orphan_marker, table_ref=query1}], orphan_sink=[{name=rn, table_ref=query1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertDictionaryHandlingQualifiedColumnsAcrossWhereSubclausesAndOrphanRhsV2() {
		final String query = " insert into employees (quota, dept_id, orphan_sink)"
				+ "\n select src.new_quota, src.dept_id, orphan_marker"
				+ "\n from (select emp_id, dept_id, new_quota"
				+ "\n         from quota_feed"
				+ "\n        where active_flag = 1 and new_quota > 0) src";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={preamble=insert_into, from={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=null}}, 2={column={name=dept_id, table_ref=null}}, 3={column={name=new_quota, table_ref=null}}}, from={table={alias=null, table=quota_feed}}, where={and={1={condition={left={column={name=active_flag, table_ref=null}}, right={literal=1}, operator==}}, 2={condition={left={column={name=new_quota, table_ref=null}}, right={literal=0}, operator=>}}}}}}}, select={1={column={name=new_quota, table_ref=src}}, 2={column={name=dept_id, table_ref=src}}, 3={column={name=orphan_marker, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=quota, table_ref=null}}, 2={column={name=dept_id, table_ref=null}}, 3={column={name=orphan_sink, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[orphan_sink, quota, dept_id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{quota_feed={new_quota=[[@27,134:142='new_quota',<381>,3:31], [@35,203:211='new_quota',<381>,5:34]], active_flag=[[@31,183:193='active_flag',<381>,5:14]], dept_id=[[@25,125:131='dept_id',<381>,3:22]], emp_id=[[@23,117:122='emp_id',<381>,3:14]]}, employees={orphan_sink=[[@8,40:50='orphan_sink',<381>,1:40]], quota=[[@4,24:28='quota',<381>,1:24]], orphan_marker=[[@19,89:101='orphan_marker',<381>,2:36]], dept_id=[[@6,31:37='dept_id',<381>,1:31]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={new_quota=[[@27,134:142='new_quota',<381>,3:31], [@11,61:63='src',<381>,2:8]], dept_id=[[@25,125:131='dept_id',<381>,3:22], [@15,76:78='src',<381>,2:23]], emp_id=[[@23,117:122='emp_id',<381>,3:14]]}, query1={new_quota=[[@13,65:73='new_quota',<381>,2:12]], orphan_marker=[[@19,89:101='orphan_marker',<381>,2:36]], dept_id=[[@17,80:86='dept_id',<381>,2:27]]}, insert2={orphan_sink=[[@8,40:50='orphan_sink',<381>,1:40]], dept_id=[[@6,31:37='dept_id',<381>,1:31]], quota=[[@4,24:28='quota',<381>,1:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{insert2={query_dictionary={orphan_sink=[[@8,40:50='orphan_sink',<381>,1:40]], dept_id=[[@6,31:37='dept_id',<381>,1:31]], quota=[[@4,24:28='quota',<381>,1:24]]}, table_dictionary={employees={orphan_sink=[[@8,40:50='orphan_sink',<381>,1:40]], quota=[[@4,24:28='quota',<381>,1:24]], orphan_marker=[[@19,89:101='orphan_marker',<381>,2:36]], dept_id=[[@6,31:37='dept_id',<381>,1:31]]}}, def_query1={query_dictionary={new_quota=[[@13,65:73='new_quota',<381>,2:12]], orphan_marker=[[@19,89:101='orphan_marker',<381>,2:36]], dept_id=[[@17,80:86='dept_id',<381>,2:27]]}, table_dictionary={}, def_query0={query_dictionary={new_quota=[[@27,134:142='new_quota',<381>,3:31], [@11,61:63='src',<381>,2:8]], dept_id=[[@25,125:131='dept_id',<381>,3:22], [@15,76:78='src',<381>,2:23]], emp_id=[[@23,117:122='emp_id',<381>,3:14]]}, table_dictionary={quota_feed={new_quota=[[@27,134:142='new_quota',<381>,3:31], [@35,203:211='new_quota',<381>,5:34]], active_flag=[[@31,183:193='active_flag',<381>,5:14]], dept_id=[[@25,125:131='dept_id',<381>,3:22]], emp_id=[[@23,117:122='emp_id',<381>,3:14]]}}, _tmp_insert_source_select_sequence=[emp_id, dept_id, new_quota], filters=[{name=active_flag, table_ref=quota_feed}, {name=new_quota, table_ref=quota_feed}], interface={new_quota=[{name=new_quota, table_ref=quota_feed}], dept_id=[{name=dept_id, table_ref=quota_feed}], emp_id=[{name=emp_id, table_ref=quota_feed}]}}, interface={new_quota=[{name=new_quota, table_ref=src}], orphan_marker=[{name=orphan_marker, table_ref=null}], dept_id=[{name=dept_id, table_ref=src}]}, table_alias={src=query0}}, interface={quota=[{name=new_quota, table_ref=query1}], dept_id=[{name=orphan_marker, table_ref=query1}], orphan_sink=[{name=dept_id, table_ref=query1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertDictionaryHandlingUnqualifiedFallsBackToTargetTableV3() {
		final String query = " insert into employees (review_flag, orphan_sink)"
				+ "\n select src.score, missing_flag"
				+ "\n from (select emp_id, score from perf_feed) src";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={preamble=insert_into, from={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=null}}, 2={column={name=score, table_ref=null}}}, from={table={alias=null, table=perf_feed}}}}}, select={1={column={name=score, table_ref=src}}, 2={column={name=missing_flag, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=review_flag, table_ref=null}}, 2={column={name=orphan_sink, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[orphan_sink, review_flag]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{perf_feed={score=[[@19,104:108='score',<381>,3:22]], emp_id=[[@17,96:101='emp_id',<381>,3:14]]}, employees={orphan_sink=[[@6,37:47='orphan_sink',<381>,1:37]], missing_flag=[[@13,69:80='missing_flag',<381>,2:19]], review_flag=[[@4,24:34='review_flag',<381>,1:24]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@19,104:108='score',<381>,3:22], [@9,58:60='src',<381>,2:8]], emp_id=[[@17,96:101='emp_id',<381>,3:14]]}, query1={score=[[@11,62:66='score',<381>,2:12]], missing_flag=[[@13,69:80='missing_flag',<381>,2:19]]}, insert2={orphan_sink=[[@6,37:47='orphan_sink',<381>,1:37]], review_flag=[[@4,24:34='review_flag',<381>,1:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{insert2={query_dictionary={orphan_sink=[[@6,37:47='orphan_sink',<381>,1:37]], review_flag=[[@4,24:34='review_flag',<381>,1:24]]}, table_dictionary={employees={orphan_sink=[[@6,37:47='orphan_sink',<381>,1:37]], missing_flag=[[@13,69:80='missing_flag',<381>,2:19]], review_flag=[[@4,24:34='review_flag',<381>,1:24]]}}, def_query1={query_dictionary={score=[[@11,62:66='score',<381>,2:12]], missing_flag=[[@13,69:80='missing_flag',<381>,2:19]]}, table_dictionary={}, def_query0={query_dictionary={score=[[@19,104:108='score',<381>,3:22], [@9,58:60='src',<381>,2:8]], emp_id=[[@17,96:101='emp_id',<381>,3:14]]}, table_dictionary={perf_feed={score=[[@19,104:108='score',<381>,3:22]], emp_id=[[@17,96:101='emp_id',<381>,3:14]]}}, _tmp_insert_source_select_sequence=[emp_id, score], interface={score=[{name=score, table_ref=perf_feed}], emp_id=[{name=emp_id, table_ref=perf_feed}]}}, interface={score=[{name=score, table_ref=src}], missing_flag=[{name=missing_flag, table_ref=null}]}, table_alias={src=query0}}, interface={review_flag=[{name=score, table_ref=query1}], orphan_sink=[{name=missing_flag, table_ref=query1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertDictionaryHandlingUnqualifiedWithAdditionalPhysicalTableStillResolvesV4() {
		final String query = " insert into employees (review_flag, orphan_sink)"
				+ "\n select src.score, missing_flag"
				+ "\n from (select p.emp_id, p.score"
				+ "\n         from perf_feed p"
				+ "\n         join audit_flags af on p.emp_id = af.emp_id"
				+ "\n        where af.missing_flag > 0) src";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={preamble=insert_into, from={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=p}}, 2={column={name=score, table_ref=p}}}, from={join={1={table={alias=p, table=perf_feed}}, 2={join=join, on={condition={left={column={name=emp_id, table_ref=p}}, right={column={name=emp_id, table_ref=af}}, operator==}}}, 3={table={alias=af, table=audit_flags}}}}, where={condition={left={column={name=missing_flag, table_ref=af}}, right={literal=0}, operator=>}}}}}, select={1={column={name=score, table_ref=src}}, 2={column={name=missing_flag, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=review_flag, table_ref=null}}, 2={column={name=orphan_sink, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[orphan_sink, review_flag]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{perf_feed={score=[[@21,106:106='p',<381>,3:24]], emp_id=[[@17,96:96='p',<381>,3:14], [@31,172:172='p',<381>,5:32]]}, employees={orphan_sink=[[@6,37:47='orphan_sink',<381>,1:37]], missing_flag=[[@13,69:80='missing_flag',<381>,2:19]], review_flag=[[@4,24:34='review_flag',<381>,1:24]]}, audit_flags={missing_flag=[[@39,207:208='af',<381>,6:14]], emp_id=[[@35,183:184='af',<381>,5:43]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@23,108:112='score',<381>,3:26], [@9,58:60='src',<381>,2:8]], emp_id=[[@19,98:103='emp_id',<381>,3:16]]}, query1={score=[[@11,62:66='score',<381>,2:12]], missing_flag=[[@13,69:80='missing_flag',<381>,2:19]]}, insert2={orphan_sink=[[@6,37:47='orphan_sink',<381>,1:37]], review_flag=[[@4,24:34='review_flag',<381>,1:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{insert2={query_dictionary={orphan_sink=[[@6,37:47='orphan_sink',<381>,1:37]], review_flag=[[@4,24:34='review_flag',<381>,1:24]]}, table_dictionary={employees={orphan_sink=[[@6,37:47='orphan_sink',<381>,1:37]], missing_flag=[[@13,69:80='missing_flag',<381>,2:19]], review_flag=[[@4,24:34='review_flag',<381>,1:24]]}}, def_query1={query_dictionary={score=[[@11,62:66='score',<381>,2:12]], missing_flag=[[@13,69:80='missing_flag',<381>,2:19]]}, table_dictionary={}, def_query0={query_dictionary={score=[[@23,108:112='score',<381>,3:26], [@9,58:60='src',<381>,2:8]], emp_id=[[@19,98:103='emp_id',<381>,3:16]]}, table_dictionary={perf_feed={score=[[@21,106:106='p',<381>,3:24]], emp_id=[[@17,96:96='p',<381>,3:14], [@31,172:172='p',<381>,5:32]]}, audit_flags={missing_flag=[[@39,207:208='af',<381>,6:14]], emp_id=[[@35,183:184='af',<381>,5:43]]}}, _tmp_insert_source_select_sequence=[emp_id, score], filters=[{name=emp_id, table_ref=p}, {name=emp_id, table_ref=af}, {name=missing_flag, table_ref=af}], interface={score=[{name=score, table_ref=p}], emp_id=[{name=emp_id, table_ref=p}]}, table_alias={p=perf_feed, af=audit_flags}}, interface={score=[{name=score, table_ref=src}], missing_flag=[{name=missing_flag, table_ref=null}]}, table_alias={src=query0}}, interface={review_flag=[{name=score, table_ref=query1}], orphan_sink=[{name=missing_flag, table_ref=query1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertDictionaryHandlingGroupByHavingSubqueryAndUnqualifiedRhsV5() {
		final String query = " insert into employees (agg_score, stale_flag)"
				+ "\n select src.total_score, orphan_marker"
				+ "\n from (select a.emp_id, sum(a.score) as total_score"
				+ "\n         from accounts a"
				+ "\n        group by a.emp_id"
				+ "\n       having sum(a.score) > 0) src";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={preamble=insert_into, from={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={function={function_name=sum, qualifier=null, parameters={column={name=score, table_ref=a}}}, alias=total_score}}, having={condition={left={function={function_name=sum, qualifier=null, parameters={column={name=score, table_ref=a}}}}, right={literal=0}, operator=>}}, from={table={alias=a, table=accounts}}, groupby={1={column={name=emp_id, table_ref=a}}}}}}, select={1={column={name=total_score, table_ref=src}}, 2={column={name=orphan_marker, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=agg_score, table_ref=null}}, 2={column={name=stale_flag, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[stale_flag, agg_score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@23,114:114='a',<381>,3:28], [@40,207:207='a',<381>,6:18]], emp_id=[[@17,100:100='a',<381>,3:14], [@34,180:180='a',<381>,5:17]]}, employees={stale_flag=[[@6,35:44='stale_flag',<381>,1:35]], orphan_marker=[[@13,72:84='orphan_marker',<381>,2:25]], agg_score=[[@4,24:32='agg_score',<381>,1:24]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={total_score=[[@28,126:136='total_score',<381>,3:40], [@9,55:57='src',<381>,2:8]], emp_id=[[@19,102:107='emp_id',<381>,3:16]]}, query1={total_score=[[@11,59:69='total_score',<381>,2:12]], orphan_marker=[[@13,72:84='orphan_marker',<381>,2:25]]}, insert2={agg_score=[[@4,24:32='agg_score',<381>,1:24]], stale_flag=[[@6,35:44='stale_flag',<381>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{insert2={query_dictionary={agg_score=[[@4,24:32='agg_score',<381>,1:24]], stale_flag=[[@6,35:44='stale_flag',<381>,1:35]]}, table_dictionary={employees={stale_flag=[[@6,35:44='stale_flag',<381>,1:35]], orphan_marker=[[@13,72:84='orphan_marker',<381>,2:25]], agg_score=[[@4,24:32='agg_score',<381>,1:24]]}}, def_query1={query_dictionary={total_score=[[@11,59:69='total_score',<381>,2:12]], orphan_marker=[[@13,72:84='orphan_marker',<381>,2:25]]}, table_dictionary={}, def_query0={query_dictionary={total_score=[[@28,126:136='total_score',<381>,3:40], [@9,55:57='src',<381>,2:8]], emp_id=[[@19,102:107='emp_id',<381>,3:16]]}, table_dictionary={accounts={score=[[@23,114:114='a',<381>,3:28], [@40,207:207='a',<381>,6:18]], emp_id=[[@17,100:100='a',<381>,3:14], [@34,180:180='a',<381>,5:17]]}}, grouped_by=[{name=emp_id, table_ref=a}], _tmp_insert_source_select_sequence=[emp_id, total_score], filters=[{name=score, table_ref=a}], interface={total_score=[{name=score, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, interface={total_score=[{name=total_score, table_ref=src}], orphan_marker=[{name=orphan_marker, table_ref=null}]}, table_alias={src=query0}}, interface={agg_score=[{name=total_score, table_ref=query1}], stale_flag=[{name=orphan_marker, table_ref=query1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertDictionaryHandlingOrderBySubqueryAndUnqualifiedRhsV6() {
		final String query = " insert into employees (most_recent_update, unknown_rhs)"
				+ "\n select src.last_update, shadow_col"
				+ "\n from (select a.emp_id, a.last_update"
				+ "\n         from accounts a"
				+ "\n        order by a.last_update desc) src";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={preamble=insert_into, from={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={name=last_update, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=last_update, table_ref=a}}, sort_order=desc}}, from={table={alias=a, table=accounts}}}}}, select={1={column={name=last_update, table_ref=src}}, 2={column={name=shadow_col, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=most_recent_update, table_ref=null}}, 2={column={name=unknown_rhs, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unknown_rhs, most_recent_update]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={last_update=[[@21,117:117='a',<381>,3:24], [@29,173:173='a',<381>,5:17]], emp_id=[[@17,107:107='a',<381>,3:14]]}, employees={unknown_rhs=[[@6,44:54='unknown_rhs',<381>,1:44]], most_recent_update=[[@4,24:41='most_recent_update',<381>,1:24]], shadow_col=[[@13,82:91='shadow_col',<381>,2:25]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={last_update=[[@23,119:129='last_update',<381>,3:26], [@9,65:67='src',<381>,2:8]], emp_id=[[@19,109:114='emp_id',<381>,3:16]]}, query1={last_update=[[@11,69:79='last_update',<381>,2:12]], shadow_col=[[@13,82:91='shadow_col',<381>,2:25]]}, insert2={most_recent_update=[[@4,24:41='most_recent_update',<381>,1:24]], unknown_rhs=[[@6,44:54='unknown_rhs',<381>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{insert2={query_dictionary={most_recent_update=[[@4,24:41='most_recent_update',<381>,1:24]], unknown_rhs=[[@6,44:54='unknown_rhs',<381>,1:44]]}, table_dictionary={employees={unknown_rhs=[[@6,44:54='unknown_rhs',<381>,1:44]], most_recent_update=[[@4,24:41='most_recent_update',<381>,1:24]], shadow_col=[[@13,82:91='shadow_col',<381>,2:25]]}}, def_query1={query_dictionary={last_update=[[@11,69:79='last_update',<381>,2:12]], shadow_col=[[@13,82:91='shadow_col',<381>,2:25]]}, table_dictionary={}, def_query0={query_dictionary={last_update=[[@23,119:129='last_update',<381>,3:26], [@9,65:67='src',<381>,2:8]], emp_id=[[@19,109:114='emp_id',<381>,3:16]]}, table_dictionary={accounts={last_update=[[@21,117:117='a',<381>,3:24], [@29,173:173='a',<381>,5:17]], emp_id=[[@17,107:107='a',<381>,3:14]]}}, _tmp_insert_source_select_sequence=[emp_id, last_update], ordered_by=[{name=last_update, table_ref=a}], interface={last_update=[{name=last_update, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, interface={last_update=[{name=last_update, table_ref=src}], shadow_col=[{name=shadow_col, table_ref=null}]}, table_alias={src=query0}}, interface={most_recent_update=[{name=last_update, table_ref=query1}], unknown_rhs=[{name=shadow_col, table_ref=query1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertDictionaryHandlingQualifySubqueryAndUnqualifiedRhsV7() {
		final String query = " insert into employees (top_score, fallback_note)"
				+ "\n select src.score, unqualified_note"
				+ "\n from (select a.emp_id, a.score,"
				+ "\n              row_number() over (partition by a.emp_id order by a.last_update desc) as rn"
				+ "\n         from accounts a"
				+ "\n      qualify rn = 1) src";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={preamble=insert_into, from={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={name=score, table_ref=a}}, 3={alias=rn, window_function={over={partition_by={1={column={name=emp_id, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=last_update, table_ref=a}}, sort_order=desc}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=a, table=accounts}}, qualify={condition={left={column={name=rn, table_ref=null}}, right={literal=1}, operator==}}}}}, select={1={column={name=score, table_ref=src}}, 2={column={name=unqualified_note, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=top_score, table_ref=null}}, 2={column={name=fallback_note, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[fallback_note, top_score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@21,110:110='a',<381>,3:24]], last_update=[[@37,183:183='a',<381>,4:64]], rn=[[@48,248:249='rn',<381>,6:14]], emp_id=[[@17,100:100='a',<381>,3:14], [@32,165:165='a',<381>,4:46]]}, employees={fallback_note=[[@6,35:47='fallback_note',<381>,1:35]], unqualified_note=[[@13,69:84='unqualified_note',<381>,2:19]], top_score=[[@4,24:32='top_score',<381>,1:24]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@23,112:116='score',<381>,3:26], [@9,58:60='src',<381>,2:8]], rn=[[@43,206:207='rn',<381>,4:87]], emp_id=[[@19,102:107='emp_id',<381>,3:16]]}, query1={score=[[@11,62:66='score',<381>,2:12]], unqualified_note=[[@13,69:84='unqualified_note',<381>,2:19]]}, insert2={fallback_note=[[@6,35:47='fallback_note',<381>,1:35]], top_score=[[@4,24:32='top_score',<381>,1:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{insert2={query_dictionary={fallback_note=[[@6,35:47='fallback_note',<381>,1:35]], top_score=[[@4,24:32='top_score',<381>,1:24]]}, table_dictionary={employees={fallback_note=[[@6,35:47='fallback_note',<381>,1:35]], unqualified_note=[[@13,69:84='unqualified_note',<381>,2:19]], top_score=[[@4,24:32='top_score',<381>,1:24]]}}, def_query1={query_dictionary={score=[[@11,62:66='score',<381>,2:12]], unqualified_note=[[@13,69:84='unqualified_note',<381>,2:19]]}, table_dictionary={}, def_query0={query_dictionary={score=[[@23,112:116='score',<381>,3:26], [@9,58:60='src',<381>,2:8]], rn=[[@43,206:207='rn',<381>,4:87]], emp_id=[[@19,102:107='emp_id',<381>,3:16]]}, table_dictionary={accounts={score=[[@21,110:110='a',<381>,3:24]], last_update=[[@37,183:183='a',<381>,4:64]], rn=[[@48,248:249='rn',<381>,6:14]], emp_id=[[@17,100:100='a',<381>,3:14], [@32,165:165='a',<381>,4:46]]}}, _tmp_insert_source_select_sequence=[emp_id, score, rn], filters=[{name=rn, table_ref=accounts}], interface={score=[{name=score, table_ref=a}], rn=[{name=emp_id, table_ref=a}, {name=last_update, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, interface={score=[{name=score, table_ref=src}], unqualified_note=[{name=unqualified_note, table_ref=null}]}, table_alias={src=query0}}, interface={top_score=[{name=score, table_ref=query1}], fallback_note=[{name=unqualified_note, table_ref=query1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertDictionaryHandlingWhereInSubqueryWithOrphanRhsV8() {
		final String query = " insert into employees (agg_score, stale_flag)"
				+ "\n select src.total_score, orphan_marker"
				+ "\n from (select a.emp_id, sum(a.score) as total_score"
				+ "\n         from accounts a"
				+ "\n        where a.score > 0"
				+ "\n        group by a.emp_id) src";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={preamble=insert_into, from={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={function={function_name=sum, qualifier=null, parameters={column={name=score, table_ref=a}}}, alias=total_score}}, from={table={alias=a, table=accounts}}, where={condition={left={column={name=score, table_ref=a}}, right={literal=0}, operator=>}}, groupby={1={column={name=emp_id, table_ref=a}}}}}}, select={1={column={name=total_score, table_ref=src}}, 2={column={name=orphan_marker, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=agg_score, table_ref=null}}, 2={column={name=stale_flag, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[stale_flag, agg_score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@23,114:114='a',<381>,3:28], [@33,177:177='a',<381>,5:14]], emp_id=[[@17,100:100='a',<381>,3:14], [@40,206:206='a',<381>,6:17]]}, employees={stale_flag=[[@6,35:44='stale_flag',<381>,1:35]], orphan_marker=[[@13,72:84='orphan_marker',<381>,2:25]], agg_score=[[@4,24:32='agg_score',<381>,1:24]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={total_score=[[@28,126:136='total_score',<381>,3:40], [@9,55:57='src',<381>,2:8]], emp_id=[[@19,102:107='emp_id',<381>,3:16]]}, query1={total_score=[[@11,59:69='total_score',<381>,2:12]], orphan_marker=[[@13,72:84='orphan_marker',<381>,2:25]]}, insert2={agg_score=[[@4,24:32='agg_score',<381>,1:24]], stale_flag=[[@6,35:44='stale_flag',<381>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{insert2={query_dictionary={agg_score=[[@4,24:32='agg_score',<381>,1:24]], stale_flag=[[@6,35:44='stale_flag',<381>,1:35]]}, table_dictionary={employees={stale_flag=[[@6,35:44='stale_flag',<381>,1:35]], orphan_marker=[[@13,72:84='orphan_marker',<381>,2:25]], agg_score=[[@4,24:32='agg_score',<381>,1:24]]}}, def_query1={query_dictionary={total_score=[[@11,59:69='total_score',<381>,2:12]], orphan_marker=[[@13,72:84='orphan_marker',<381>,2:25]]}, table_dictionary={}, def_query0={query_dictionary={total_score=[[@28,126:136='total_score',<381>,3:40], [@9,55:57='src',<381>,2:8]], emp_id=[[@19,102:107='emp_id',<381>,3:16]]}, table_dictionary={accounts={score=[[@23,114:114='a',<381>,3:28], [@33,177:177='a',<381>,5:14]], emp_id=[[@17,100:100='a',<381>,3:14], [@40,206:206='a',<381>,6:17]]}}, grouped_by=[{name=emp_id, table_ref=a}], _tmp_insert_source_select_sequence=[emp_id, total_score], filters=[{name=score, table_ref=a}], interface={total_score=[{name=score, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, interface={total_score=[{name=total_score, table_ref=src}], orphan_marker=[{name=orphan_marker, table_ref=null}]}, table_alias={src=query0}}, interface={agg_score=[{name=total_score, table_ref=query1}], stale_flag=[{name=orphan_marker, table_ref=query1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertDictionaryHandlingJoinOnInSubqueryWithOrphanRhsV9() {
		final String query = " insert into employees (agg_score, stale_flag)"
				+ "\n select src.total_score, orphan_marker"
				+ "\n from (select a.emp_id, a.score as total_score"
				+ "\n         from accounts a"
				+ "\n         join departments d on a.dept_id = d.dept_id) src";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={preamble=insert_into, from={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={name=score, table_ref=a}, alias=total_score}}, from={join={1={table={alias=a, table=accounts}}, 2={join=join, on={condition={left={column={name=dept_id, table_ref=a}}, right={column={name=dept_id, table_ref=d}}, operator==}}}, 3={table={alias=d, table=departments}}}}}}}, select={1={column={name=total_score, table_ref=src}}, 2={column={name=orphan_marker, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=agg_score, table_ref=null}}, 2={column={name=stale_flag, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[stale_flag, agg_score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@21,110:110='a',<381>,3:24]], dept_id=[[@33,189:189='a',<381>,5:31]], emp_id=[[@17,100:100='a',<381>,3:14]]}, departments={dept_id=[[@37,201:201='d',<381>,5:43]]}, employees={stale_flag=[[@6,35:44='stale_flag',<381>,1:35]], orphan_marker=[[@13,72:84='orphan_marker',<381>,2:25]], agg_score=[[@4,24:32='agg_score',<381>,1:24]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={total_score=[[@25,121:131='total_score',<381>,3:35], [@9,55:57='src',<381>,2:8]], emp_id=[[@19,102:107='emp_id',<381>,3:16]]}, query1={total_score=[[@11,59:69='total_score',<381>,2:12]], orphan_marker=[[@13,72:84='orphan_marker',<381>,2:25]]}, insert2={agg_score=[[@4,24:32='agg_score',<381>,1:24]], stale_flag=[[@6,35:44='stale_flag',<381>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{insert2={query_dictionary={agg_score=[[@4,24:32='agg_score',<381>,1:24]], stale_flag=[[@6,35:44='stale_flag',<381>,1:35]]}, table_dictionary={employees={stale_flag=[[@6,35:44='stale_flag',<381>,1:35]], orphan_marker=[[@13,72:84='orphan_marker',<381>,2:25]], agg_score=[[@4,24:32='agg_score',<381>,1:24]]}}, def_query1={query_dictionary={total_score=[[@11,59:69='total_score',<381>,2:12]], orphan_marker=[[@13,72:84='orphan_marker',<381>,2:25]]}, table_dictionary={}, def_query0={query_dictionary={total_score=[[@25,121:131='total_score',<381>,3:35], [@9,55:57='src',<381>,2:8]], emp_id=[[@19,102:107='emp_id',<381>,3:16]]}, table_dictionary={accounts={score=[[@21,110:110='a',<381>,3:24]], dept_id=[[@33,189:189='a',<381>,5:31]], emp_id=[[@17,100:100='a',<381>,3:14]]}, departments={dept_id=[[@37,201:201='d',<381>,5:43]]}}, _tmp_insert_source_select_sequence=[emp_id, total_score], filters=[{name=dept_id, table_ref=a}, {name=dept_id, table_ref=d}], interface={total_score=[{name=score, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts, d=departments}}, interface={total_score=[{name=total_score, table_ref=src}], orphan_marker=[{name=orphan_marker, table_ref=null}]}, table_alias={src=query0}}, interface={agg_score=[{name=total_score, table_ref=query1}], stale_flag=[{name=orphan_marker, table_ref=query1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertDictionaryHandlingQualifyInSubqueryWithOrphanRhsV10() {
		final String query = " insert into employees (top_score, stale_flag)"
				+ "\n select src.score, orphan_marker"
				+ "\n from (select a.emp_id, a.score,"
				+ "\n              row_number() over (partition by a.emp_id order by a.score desc) as rn"
				+ "\n         from accounts a"
				+ "\n        qualify rn <= 10) src";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={preamble=insert_into, from={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={name=score, table_ref=a}}, 3={alias=rn, window_function={over={partition_by={1={column={name=emp_id, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=score, table_ref=a}}, sort_order=desc}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=a, table=accounts}}, qualify={condition={left={column={name=rn, table_ref=null}}, right={literal=10}, operator=<=}}}}}, select={1={column={name=score, table_ref=src}}, 2={column={name=orphan_marker, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=top_score, table_ref=null}}, 2={column={name=stale_flag, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[stale_flag, top_score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@21,104:104='a',<381>,3:24], [@37,177:177='a',<381>,4:64]], rn=[[@48,238:239='rn',<381>,6:16]], emp_id=[[@17,94:94='a',<381>,3:14], [@32,159:159='a',<381>,4:46]]}, employees={stale_flag=[[@6,35:44='stale_flag',<381>,1:35]], top_score=[[@4,24:32='top_score',<381>,1:24]], orphan_marker=[[@13,66:78='orphan_marker',<381>,2:19]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@23,106:110='score',<381>,3:26], [@9,55:57='src',<381>,2:8]], rn=[[@43,194:195='rn',<381>,4:81]], emp_id=[[@19,96:101='emp_id',<381>,3:16]]}, query1={score=[[@11,59:63='score',<381>,2:12]], orphan_marker=[[@13,66:78='orphan_marker',<381>,2:19]]}, insert2={stale_flag=[[@6,35:44='stale_flag',<381>,1:35]], top_score=[[@4,24:32='top_score',<381>,1:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{insert2={query_dictionary={stale_flag=[[@6,35:44='stale_flag',<381>,1:35]], top_score=[[@4,24:32='top_score',<381>,1:24]]}, table_dictionary={employees={stale_flag=[[@6,35:44='stale_flag',<381>,1:35]], top_score=[[@4,24:32='top_score',<381>,1:24]], orphan_marker=[[@13,66:78='orphan_marker',<381>,2:19]]}}, def_query1={query_dictionary={score=[[@11,59:63='score',<381>,2:12]], orphan_marker=[[@13,66:78='orphan_marker',<381>,2:19]]}, table_dictionary={}, def_query0={query_dictionary={score=[[@23,106:110='score',<381>,3:26], [@9,55:57='src',<381>,2:8]], rn=[[@43,194:195='rn',<381>,4:81]], emp_id=[[@19,96:101='emp_id',<381>,3:16]]}, table_dictionary={accounts={score=[[@21,104:104='a',<381>,3:24], [@37,177:177='a',<381>,4:64]], rn=[[@48,238:239='rn',<381>,6:16]], emp_id=[[@17,94:94='a',<381>,3:14], [@32,159:159='a',<381>,4:46]]}}, _tmp_insert_source_select_sequence=[emp_id, score, rn], filters=[{name=rn, table_ref=accounts}], interface={score=[{name=score, table_ref=a}], rn=[{name=emp_id, table_ref=a}, {name=score, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, interface={score=[{name=score, table_ref=src}], orphan_marker=[{name=orphan_marker, table_ref=null}]}, table_alias={src=query0}}, interface={top_score=[{name=score, table_ref=query1}], stale_flag=[{name=orphan_marker, table_ref=query1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertDictionaryHandlingOrderByInSubqueryWithOrphanRhsV11() {
		final String query = " insert into employees (most_recent_score, stale_flag)"
				+ "\n select src.score, orphan_marker"
				+ "\n from (select a.emp_id, a.score"
				+ "\n         from accounts a"
				+ "\n        order by a.score desc) src";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={preamble=insert_into, from={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={name=score, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=score, table_ref=a}}, sort_order=desc}}, from={table={alias=a, table=accounts}}}}}, select={1={column={name=score, table_ref=src}}, 2={column={name=orphan_marker, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=most_recent_score, table_ref=null}}, 2={column={name=stale_flag, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[stale_flag, most_recent_score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@21,112:112='a',<381>,3:24], [@29,162:162='a',<381>,5:17]], emp_id=[[@17,102:102='a',<381>,3:14]]}, employees={stale_flag=[[@6,43:52='stale_flag',<381>,1:43]], most_recent_score=[[@4,24:40='most_recent_score',<381>,1:24]], orphan_marker=[[@13,74:86='orphan_marker',<381>,2:19]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@23,114:118='score',<381>,3:26], [@9,63:65='src',<381>,2:8]], emp_id=[[@19,104:109='emp_id',<381>,3:16]]}, query1={score=[[@11,67:71='score',<381>,2:12]], orphan_marker=[[@13,74:86='orphan_marker',<381>,2:19]]}, insert2={most_recent_score=[[@4,24:40='most_recent_score',<381>,1:24]], stale_flag=[[@6,43:52='stale_flag',<381>,1:43]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{insert2={query_dictionary={most_recent_score=[[@4,24:40='most_recent_score',<381>,1:24]], stale_flag=[[@6,43:52='stale_flag',<381>,1:43]]}, table_dictionary={employees={stale_flag=[[@6,43:52='stale_flag',<381>,1:43]], most_recent_score=[[@4,24:40='most_recent_score',<381>,1:24]], orphan_marker=[[@13,74:86='orphan_marker',<381>,2:19]]}}, def_query1={query_dictionary={score=[[@11,67:71='score',<381>,2:12]], orphan_marker=[[@13,74:86='orphan_marker',<381>,2:19]]}, table_dictionary={}, def_query0={query_dictionary={score=[[@23,114:118='score',<381>,3:26], [@9,63:65='src',<381>,2:8]], emp_id=[[@19,104:109='emp_id',<381>,3:16]]}, table_dictionary={accounts={score=[[@21,112:112='a',<381>,3:24], [@29,162:162='a',<381>,5:17]], emp_id=[[@17,102:102='a',<381>,3:14]]}}, _tmp_insert_source_select_sequence=[emp_id, score], ordered_by=[{name=score, table_ref=a}], interface={score=[{name=score, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, interface={score=[{name=score, table_ref=src}], orphan_marker=[{name=orphan_marker, table_ref=null}]}, table_alias={src=query0}}, interface={most_recent_score=[{name=score, table_ref=query1}], stale_flag=[{name=orphan_marker, table_ref=query1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertDictionaryHandlingNoQualifiedSubqueryBodyWithQualifiedSelectAndOrphanRhsV12() {
		final String query = " insert into employees (latest_score, stale_flag)"
				+ "\n select src.score, orphan_marker"
				+ "\n from (select a.emp_id, a.score"
				+ "\n         from accounts a) src";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={preamble=insert_into, from={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={name=score, table_ref=a}}}, from={table={alias=a, table=accounts}}}}}, select={1={column={name=score, table_ref=src}}, 2={column={name=orphan_marker, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=latest_score, table_ref=null}}, 2={column={name=stale_flag, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[latest_score, stale_flag]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@21,107:107='a',<381>,3:24]], emp_id=[[@17,97:97='a',<381>,3:14]]}, employees={latest_score=[[@4,24:35='latest_score',<381>,1:24]], stale_flag=[[@6,38:47='stale_flag',<381>,1:38]], orphan_marker=[[@13,69:81='orphan_marker',<381>,2:19]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@23,109:113='score',<381>,3:26], [@9,58:60='src',<381>,2:8]], emp_id=[[@19,99:104='emp_id',<381>,3:16]]}, query1={score=[[@11,62:66='score',<381>,2:12]], orphan_marker=[[@13,69:81='orphan_marker',<381>,2:19]]}, insert2={latest_score=[[@4,24:35='latest_score',<381>,1:24]], stale_flag=[[@6,38:47='stale_flag',<381>,1:38]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{insert2={query_dictionary={latest_score=[[@4,24:35='latest_score',<381>,1:24]], stale_flag=[[@6,38:47='stale_flag',<381>,1:38]]}, table_dictionary={employees={latest_score=[[@4,24:35='latest_score',<381>,1:24]], stale_flag=[[@6,38:47='stale_flag',<381>,1:38]], orphan_marker=[[@13,69:81='orphan_marker',<381>,2:19]]}}, def_query1={query_dictionary={score=[[@11,62:66='score',<381>,2:12]], orphan_marker=[[@13,69:81='orphan_marker',<381>,2:19]]}, table_dictionary={}, def_query0={query_dictionary={score=[[@23,109:113='score',<381>,3:26], [@9,58:60='src',<381>,2:8]], emp_id=[[@19,99:104='emp_id',<381>,3:16]]}, table_dictionary={accounts={score=[[@21,107:107='a',<381>,3:24]], emp_id=[[@17,97:97='a',<381>,3:14]]}}, _tmp_insert_source_select_sequence=[emp_id, score], interface={score=[{name=score, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, interface={score=[{name=score, table_ref=src}], orphan_marker=[{name=orphan_marker, table_ref=null}]}, table_alias={src=query0}}, interface={latest_score=[{name=score, table_ref=query1}], stale_flag=[{name=orphan_marker, table_ref=query1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void simpleBasicUpdateTestV1() {
		String sql = "UPDATE employees e SET e.emp_sales_count = a.acct_sales_count + 1, e.redder = greener  FROM accounts as a";
		final SQLSelectParserParser parser = parse(sql);

		ParserRunResult runResult = runSQLParsertestAllowErrors(sql, parser);
		Assert.assertNull(
				"Unexpected parser execution failure: "
						+ (runResult.getFailure() == null ? "" : runResult.getFailure().getMessage()),
				runResult.getFailure());
		Assert.assertNotNull("Extractor should be available after parse attempt", runResult.getExtractor());
		Assert.assertNotNull("Parser errors should always be returned", runResult.getParserErrors());
		Assert.assertNotNull("Listener diagnostics should always be returned", runResult.getListenerDiagnostics());
		Assert.assertEquals("Unexpected parser errors: " + runResult.getParserErrors(), 0,
				runResult.getParserErrorCount());

		SqlParseEventWalker extractor = runResult.getExtractor();
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={update={table={alias=e, table=employees}, from={table={alias=a, table=accounts}}, assignments={1={set={column={name=emp_sales_count, table_ref=e}}, to={calc={left={column={name=acct_sales_count, table_ref=a}}, right={literal=1}, operator=+}}}, 2={set={column={name=redder, table_ref=e}}, to={column={name=greener, table_ref=null}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[emp_sales_count, redder]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={acct_sales_count=[[@8,43:43='a',<381>,1:43]]}, employees={emp_sales_count=[[@4,23:23='e',<381>,1:23]], redder=[[@14,67:67='e',<381>,1:67]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{update0={emp_sales_count=[[@6,25:39='emp_sales_count',<381>,1:25]], redder=[[@16,69:74='redder',<381>,1:69]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{update0={assignments={emp_sales_count=[{name=acct_sales_count, table_ref=a}], redder=[{name=greener, table_ref=null}]}, table_dictionary={accounts={acct_sales_count=[[@8,43:43='a',<381>,1:43]]}, employees={emp_sales_count=[[@4,23:23='e',<381>,1:23]], redder=[[@14,67:67='e',<381>,1:67]]}}, unresolved_column={greener={column={name=greener, table_ref=null}, locations=[[@18,78:84='greener',<381>,1:78]]}}, update_dictionary={emp_sales_count=[[@6,25:39='emp_sales_count',<381>,1:25]], redder=[[@16,69:74='redder',<381>,1:69]]}, table_alias={a=accounts, e=employees}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void simpleBasicUpdateTestV2() {
		String sql = "update this_table set outputA = column1, outputB = column2, outputC = column3 "
				+ " from that_table where this_table.key=that_table.key";
		final SQLSelectParserParser parser = parse(sql);
			
		SqlParseEventWalker extractor = runParsertest(sql, parser);
		assertNoFatalErrors(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={update={from={table={alias=null, table=that_table}}, where={condition={left={column={name=key, table_ref=this_table}}, right={column={name=key, table_ref=that_table}}, operator==}}, assignments={1={set={column={name=outputA, table_ref=null}}, to={column={name=column1, table_ref=null}}}, 2={set={column={name=outputB, table_ref=null}}, to={column={name=column2, table_ref=null}}}, 3={set={column={name=outputC, table_ref=null}}, to={column={name=column3, table_ref=null}}}}, table={alias=null, table=this_table}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[outputC, outputA, outputB]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{that_table={column1=[[@5,32:38='column1',<381>,1:32]], column3=[[@13,70:76='column3',<381>,1:70]], column2=[[@9,51:57='column2',<381>,1:51]], key=[[@21,116:125='that_table',<381>,1:116]]}, this_table={outputC=[[@11,60:66='outputC',<381>,1:60]], outputA=[[@3,22:28='outputA',<381>,1:22]], outputB=[[@7,41:47='outputB',<381>,1:41]], key=[[@17,101:110='this_table',<381>,1:101]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{update0={outputC=[[@11,60:66='outputC',<381>,1:60]], outputA=[[@3,22:28='outputA',<381>,1:22]], outputB=[[@7,41:47='outputB',<381>,1:41]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{update0={assignments={outputC=[{name=column3, table_ref=null}], outputA=[{name=column1, table_ref=null}], outputB=[{name=column2, table_ref=null}]}, table_dictionary={that_table={column1=[[@5,32:38='column1',<381>,1:32]], column3=[[@13,70:76='column3',<381>,1:70]], column2=[[@9,51:57='column2',<381>,1:51]], key=[[@21,116:125='that_table',<381>,1:116]]}, this_table={outputC=[[@11,60:66='outputC',<381>,1:60]], outputA=[[@3,22:28='outputA',<381>,1:22]], outputB=[[@7,41:47='outputB',<381>,1:41]], key=[[@17,101:110='this_table',<381>,1:101]]}}, update_dictionary={outputC=[[@11,60:66='outputC',<381>,1:60]], outputA=[[@3,22:28='outputA',<381>,1:22]], outputB=[[@7,41:47='outputB',<381>,1:41]]}, filters=[{name=key, table_ref=this_table}, {name=key, table_ref=that_table}]}}",
				extractor.getSymbolTable().toString());
}

// DELETE TESTS

	@Test
	public void deleteDictionaryHandlingPostgresReturningWindowedSubqueryV1() {
		final String query = " delete from tab1 aaa"
				+ "\n using (select bbb.b1, bbb.b2,"
				+ "\n              row_number() over (partition by bbb.b1 order by bbb.b3 desc) as ccc"
				+ "\n         from tab2 bbb"
				+ "\n        where bbb.b2 > 0) ddd"
				+ "\n where aaa.a1 = ddd.b1 and ddd.ccc = 1"
				+ "\n returning aaa.a1, aaa.a2, aaa.a3";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={delete={using={1={table={alias=ddd, query={select={1={column={name=b1, table_ref=bbb}}, 2={column={name=b2, table_ref=bbb}}, 3={alias=ccc, window_function={over={partition_by={1={column={name=b1, table_ref=bbb}}}, orderby={1={null_order=null, predicand={column={name=b3, table_ref=bbb}}, sort_order=desc}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=bbb, table=tab2}}, where={condition={left={column={name=b2, table_ref=bbb}}, right={literal=0}, operator=>}}}}}}, where={and={1={condition={left={column={name=a1, table_ref=aaa}}, right={column={name=b1, table_ref=ddd}}, operator==}}, 2={condition={left={column={name=ccc, table_ref=ddd}}, right={literal=1}, operator==}}}}, table={alias=aaa, table=tab1}, returning={1={column={name=a1, table_ref=aaa}}, 2={column={name=a2, table_ref=aaa}}, 3={column={name=a3, table_ref=aaa}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, a2, a3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@46,195:197='aaa',<381>,6:7], [@60,238:240='aaa',<381>,7:11]], a2=[[@64,246:248='aaa',<381>,7:19]], a3=[[@68,254:256='aaa',<381>,7:27]]}, tab2={b2=[[@11,45:47='bbb',<381>,2:23], [@38,172:174='bbb',<381>,5:14]], b3=[[@27,115:117='bbb',<381>,3:62]], b1=[[@7,37:39='bbb',<381>,2:15], [@22,99:101='bbb',<381>,3:46]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={b2=[[@13,49:50='b2',<381>,2:27]], ccc=[[@33,131:133='ccc',<381>,3:78], [@54,215:217='ddd',<381>,6:27]], b1=[[@9,41:42='b1',<381>,2:19], [@50,204:206='ddd',<381>,6:16]]}, delete1={a1=[[@62,242:243='a1',<381>,7:15]], a2=[[@66,250:251='a2',<381>,7:23]], a3=[[@70,258:259='a3',<381>,7:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{delete1={query_dictionary={a1=[[@62,242:243='a1',<381>,7:15]], a2=[[@66,250:251='a2',<381>,7:23]], a3=[[@70,258:259='a3',<381>,7:31]]}, table_dictionary={tab1={a1=[[@46,195:197='aaa',<381>,6:7], [@60,238:240='aaa',<381>,7:11]], a2=[[@64,246:248='aaa',<381>,7:19]], a3=[[@68,254:256='aaa',<381>,7:27]]}}, def_query0={query_dictionary={b2=[[@13,49:50='b2',<381>,2:27]], ccc=[[@33,131:133='ccc',<381>,3:78], [@54,215:217='ddd',<381>,6:27]], b1=[[@9,41:42='b1',<381>,2:19], [@50,204:206='ddd',<381>,6:16]]}, table_dictionary={tab2={b2=[[@11,45:47='bbb',<381>,2:23], [@38,172:174='bbb',<381>,5:14]], b3=[[@27,115:117='bbb',<381>,3:62]], b1=[[@7,37:39='bbb',<381>,2:15], [@22,99:101='bbb',<381>,3:46]]}}, filters=[{name=b2, table_ref=bbb}], interface={b2=[{name=b2, table_ref=bbb}], ccc=[{name=b1, table_ref=bbb}, {name=b3, table_ref=bbb}], b1=[{name=b1, table_ref=bbb}]}, table_alias={bbb=tab2}}, filters=[{name=a1, table_ref=aaa}, {name=b1, table_ref=ddd}, {name=ccc, table_ref=ddd}], interface={a1=[{name=a1, table_ref=aaa}], a2=[{name=a2, table_ref=aaa}], a3=[{name=a3, table_ref=aaa}]}, table_alias={aaa=tab1, ddd=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void deleteDictionaryHandlingPostgresReturningQualifiedAcrossWhereSubclausesV2() {
		final String query = " delete from tab1 aaa"
				+ "\n using (select b1, b2, b3 from tab2) bbb"
				+ "\n where aaa.a1 = bbb.b1 and aaa.a2 = bbb.b2 and bbb.b3 > 0"
				+ "\n returning aaa.a1, aaa.a2, aaa.a3";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={delete={using={1={table={alias=bbb, query={select={1={column={name=b1, table_ref=null}}, 2={column={name=b2, table_ref=null}}, 3={column={name=b3, table_ref=null}}}, from={table={alias=null, table=tab2}}}}}}, where={and={1={condition={left={column={name=a1, table_ref=aaa}}, right={column={name=b1, table_ref=bbb}}, operator==}}, 2={condition={left={column={name=a2, table_ref=aaa}}, right={column={name=b2, table_ref=bbb}}, operator==}}, 3={condition={left={column={name=b3, table_ref=bbb}}, right={literal=0}, operator=>}}}}, table={alias=aaa, table=tab1}, returning={1={column={name=a1, table_ref=aaa}}, 2={column={name=a2, table_ref=aaa}}, 3={column={name=a3, table_ref=aaa}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, a2, a3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@17,70:72='aaa',<381>,3:7], [@39,132:134='aaa',<381>,4:11]], a2=[[@25,90:92='aaa',<381>,3:27], [@43,140:142='aaa',<381>,4:19]], a3=[[@47,148:150='aaa',<381>,4:27]]}, tab2={b2=[[@9,41:42='b2',<381>,2:19]], b3=[[@11,45:46='b3',<381>,2:23]], b1=[[@7,37:38='b1',<381>,2:15]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={b2=[[@9,41:42='b2',<381>,2:19], [@29,99:101='bbb',<381>,3:36]], b3=[[@11,45:46='b3',<381>,2:23], [@33,110:112='bbb',<381>,3:47]], b1=[[@7,37:38='b1',<381>,2:15], [@21,79:81='bbb',<381>,3:16]]}, delete1={a1=[[@41,136:137='a1',<381>,4:15]], a2=[[@45,144:145='a2',<381>,4:23]], a3=[[@49,152:153='a3',<381>,4:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{delete1={query_dictionary={a1=[[@41,136:137='a1',<381>,4:15]], a2=[[@45,144:145='a2',<381>,4:23]], a3=[[@49,152:153='a3',<381>,4:31]]}, table_dictionary={tab1={a1=[[@17,70:72='aaa',<381>,3:7], [@39,132:134='aaa',<381>,4:11]], a2=[[@25,90:92='aaa',<381>,3:27], [@43,140:142='aaa',<381>,4:19]], a3=[[@47,148:150='aaa',<381>,4:27]]}}, def_query0={query_dictionary={b2=[[@9,41:42='b2',<381>,2:19], [@29,99:101='bbb',<381>,3:36]], b3=[[@11,45:46='b3',<381>,2:23], [@33,110:112='bbb',<381>,3:47]], b1=[[@7,37:38='b1',<381>,2:15], [@21,79:81='bbb',<381>,3:16]]}, table_dictionary={tab2={b2=[[@9,41:42='b2',<381>,2:19]], b3=[[@11,45:46='b3',<381>,2:23]], b1=[[@7,37:38='b1',<381>,2:15]]}}, interface={b2=[{name=b2, table_ref=tab2}], b3=[{name=b3, table_ref=tab2}], b1=[{name=b1, table_ref=tab2}]}}, filters=[{name=a1, table_ref=aaa}, {name=b1, table_ref=bbb}, {name=a2, table_ref=aaa}, {name=b2, table_ref=bbb}, {name=b3, table_ref=bbb}], interface={a1=[{name=a1, table_ref=aaa}], a2=[{name=a2, table_ref=aaa}], a3=[{name=a3, table_ref=aaa}]}, table_alias={aaa=tab1, bbb=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void deleteDictionaryHandlingPostgresReturningOrderBySubqueryV3() {
		final String query = " delete from tab1 aaa"
				+ "\n using (select bbb.b1, bbb.b2"
				+ "\n         from tab2 bbb"
				+ "\n        order by bbb.b3 desc) ccc"
				+ "\n where aaa.a1 = ccc.b1 and ccc.b2 > 0"
				+ "\n returning aaa.a1, aaa.a2, aaa.a3";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={delete={using={1={table={alias=ccc, query={select={1={column={name=b1, table_ref=bbb}}, 2={column={name=b2, table_ref=bbb}}}, orderby={1={null_order=null, predicand={column={name=b3, table_ref=bbb}}, sort_order=desc}}, from={table={alias=bbb, table=tab2}}}}}}, where={and={1={condition={left={column={name=a1, table_ref=aaa}}, right={column={name=b1, table_ref=ccc}}, operator==}}, 2={condition={left={column={name=b2, table_ref=ccc}}, right={literal=0}, operator=>}}}}, table={alias=aaa, table=tab1}, returning={1={column={name=a1, table_ref=aaa}}, 2={column={name=a2, table_ref=aaa}}, 3={column={name=a3, table_ref=aaa}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, a2, a3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@26,116:118='aaa',<381>,5:7], [@40,158:160='aaa',<381>,6:11]], a2=[[@44,166:168='aaa',<381>,6:19]], a3=[[@48,174:176='aaa',<381>,6:27]]}, tab2={b2=[[@11,45:47='bbb',<381>,2:23]], b3=[[@19,92:94='bbb',<381>,4:17]], b1=[[@7,37:39='bbb',<381>,2:15]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={b2=[[@13,49:50='b2',<381>,2:27], [@34,136:138='ccc',<381>,5:27]], b1=[[@9,41:42='b1',<381>,2:19], [@30,125:127='ccc',<381>,5:16]]}, delete1={a1=[[@42,162:163='a1',<381>,6:15]], a2=[[@46,170:171='a2',<381>,6:23]], a3=[[@50,178:179='a3',<381>,6:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{delete1={query_dictionary={a1=[[@42,162:163='a1',<381>,6:15]], a2=[[@46,170:171='a2',<381>,6:23]], a3=[[@50,178:179='a3',<381>,6:31]]}, table_dictionary={tab1={a1=[[@26,116:118='aaa',<381>,5:7], [@40,158:160='aaa',<381>,6:11]], a2=[[@44,166:168='aaa',<381>,6:19]], a3=[[@48,174:176='aaa',<381>,6:27]]}}, def_query0={query_dictionary={b2=[[@13,49:50='b2',<381>,2:27], [@34,136:138='ccc',<381>,5:27]], b1=[[@9,41:42='b1',<381>,2:19], [@30,125:127='ccc',<381>,5:16]]}, table_dictionary={tab2={b2=[[@11,45:47='bbb',<381>,2:23]], b3=[[@19,92:94='bbb',<381>,4:17]], b1=[[@7,37:39='bbb',<381>,2:15]]}}, ordered_by=[{name=b3, table_ref=bbb}], interface={b2=[{name=b2, table_ref=bbb}], b1=[{name=b1, table_ref=bbb}]}, table_alias={bbb=tab2}}, filters=[{name=a1, table_ref=aaa}, {name=b1, table_ref=ccc}, {name=b2, table_ref=ccc}], interface={a1=[{name=a1, table_ref=aaa}], a2=[{name=a2, table_ref=aaa}], a3=[{name=a3, table_ref=aaa}]}, table_alias={aaa=tab1, ccc=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void deleteDictionaryHandlingPostgresReturningJoinInSubqueryV4() {
		final String query = " delete from tab1 aaa"
				+ "\n using (select bbb.b1, bbb.b2"
				+ "\n         from tab2 bbb"
				+ "\n         join tab1 eee on eee.a1 = bbb.b1 and eee.a3 = bbb.b3) ccc"
				+ "\n where aaa.a1 = ccc.b1"
				+ "\n returning aaa.a1, aaa.a2, aaa.a3";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={delete={using={1={table={alias=ccc, query={select={1={column={name=b1, table_ref=bbb}}, 2={column={name=b2, table_ref=bbb}}}, from={join={1={table={alias=bbb, table=tab2}}, 2={join=join, on={and={1={condition={left={column={name=a1, table_ref=eee}}, right={column={name=b1, table_ref=bbb}}, operator==}}, 2={condition={left={column={name=a3, table_ref=eee}}, right={column={name=b3, table_ref=bbb}}, operator==}}}}}, 3={table={alias=eee, table=tab1}}}}}}}}, where={condition={left={column={name=a1, table_ref=aaa}}, right={column={name=b1, table_ref=ccc}}, operator==}}, table={alias=aaa, table=tab1}, returning={1={column={name=a1, table_ref=aaa}}, 2={column={name=a2, table_ref=aaa}}, 3={column={name=a3, table_ref=aaa}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, a2, a3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@21,101:103='eee',<381>,4:26], [@39,149:151='aaa',<381>,5:7], [@47,176:178='aaa',<381>,6:11]], a2=[[@51,184:186='aaa',<381>,6:19]], a3=[[@29,121:123='eee',<381>,4:46], [@55,192:194='aaa',<381>,6:27]]}, tab2={b2=[[@11,45:47='bbb',<381>,2:23]], b3=[[@33,130:132='bbb',<381>,4:55]], b1=[[@7,37:39='bbb',<381>,2:15], [@25,110:112='bbb',<381>,4:35]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={b2=[[@13,49:50='b2',<381>,2:27]], b1=[[@9,41:42='b1',<381>,2:19], [@43,158:160='ccc',<381>,5:16]]}, delete1={a1=[[@49,180:181='a1',<381>,6:15]], a2=[[@53,188:189='a2',<381>,6:23]], a3=[[@57,196:197='a3',<381>,6:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{delete1={query_dictionary={a1=[[@49,180:181='a1',<381>,6:15]], a2=[[@53,188:189='a2',<381>,6:23]], a3=[[@57,196:197='a3',<381>,6:31]]}, table_dictionary={tab1={a1=[[@39,149:151='aaa',<381>,5:7], [@47,176:178='aaa',<381>,6:11]], a2=[[@51,184:186='aaa',<381>,6:19]], a3=[[@55,192:194='aaa',<381>,6:27]]}}, def_query0={query_dictionary={b2=[[@13,49:50='b2',<381>,2:27]], b1=[[@9,41:42='b1',<381>,2:19], [@43,158:160='ccc',<381>,5:16]]}, table_dictionary={tab1={a1=[[@21,101:103='eee',<381>,4:26], [@39,149:151='aaa',<381>,5:7], [@47,176:178='aaa',<381>,6:11]], a3=[[@29,121:123='eee',<381>,4:46], [@55,192:194='aaa',<381>,6:27]]}, tab2={b2=[[@11,45:47='bbb',<381>,2:23]], b3=[[@33,130:132='bbb',<381>,4:55]], b1=[[@7,37:39='bbb',<381>,2:15], [@25,110:112='bbb',<381>,4:35]]}}, filters=[{name=a1, table_ref=eee}, {name=b1, table_ref=bbb}, {name=a3, table_ref=eee}, {name=b3, table_ref=bbb}], interface={b2=[{name=b2, table_ref=bbb}], b1=[{name=b1, table_ref=bbb}]}, table_alias={bbb=tab2, eee=tab1}}, filters=[{name=a1, table_ref=aaa}, {name=b1, table_ref=ccc}], interface={a1=[{name=a1, table_ref=aaa}], a2=[{name=a2, table_ref=aaa}], a3=[{name=a3, table_ref=aaa}]}, table_alias={aaa=tab1, ccc=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void deleteDictionaryHandlingPostgresReturningColumnsV5() {
		final String query = " delete from tab1 aaa"
				+ "\n using tab2 bbb"
				+ "\n where aaa.a1 = bbb.b1 and aaa.a2 = bbb.b2"
				+ "\n returning aaa.a1, aaa.a2, aaa.a3";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={delete={using={1={table={alias=bbb, table=tab2}}}, where={and={1={condition={left={column={name=a1, table_ref=aaa}}, right={column={name=b1, table_ref=bbb}}, operator==}}, 2={condition={left={column={name=a2, table_ref=aaa}}, right={column={name=b2, table_ref=bbb}}, operator==}}}}, table={alias=aaa, table=tab1}, returning={1={column={name=a1, table_ref=aaa}}, 2={column={name=a2, table_ref=aaa}}, 3={column={name=a3, table_ref=aaa}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, a2, a3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@8,45:47='aaa',<381>,3:7], [@24,92:94='aaa',<381>,4:11]], a2=[[@16,65:67='aaa',<381>,3:27], [@28,100:102='aaa',<381>,4:19]], a3=[[@32,108:110='aaa',<381>,4:27]]}, tab2={b2=[[@20,74:76='bbb',<381>,3:36]], b1=[[@12,54:56='bbb',<381>,3:16]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{delete0={a1=[[@26,96:97='a1',<381>,4:15]], a2=[[@30,104:105='a2',<381>,4:23]], a3=[[@34,112:113='a3',<381>,4:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{delete0={query_dictionary={a1=[[@26,96:97='a1',<381>,4:15]], a2=[[@30,104:105='a2',<381>,4:23]], a3=[[@34,112:113='a3',<381>,4:31]]}, table_dictionary={tab1={a1=[[@8,45:47='aaa',<381>,3:7], [@24,92:94='aaa',<381>,4:11]], a2=[[@16,65:67='aaa',<381>,3:27], [@28,100:102='aaa',<381>,4:19]], a3=[[@32,108:110='aaa',<381>,4:27]]}, tab2={b2=[[@20,74:76='bbb',<381>,3:36]], b1=[[@12,54:56='bbb',<381>,3:16]]}}, filters=[{name=a1, table_ref=aaa}, {name=b1, table_ref=bbb}, {name=a2, table_ref=aaa}, {name=b2, table_ref=bbb}], interface={a1=[{name=a1, table_ref=aaa}], a2=[{name=a2, table_ref=aaa}], a3=[{name=a3, table_ref=aaa}]}, table_alias={aaa=tab1, bbb=tab2}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void deleteDictionaryHandlingPostgresReturningColumnsFromUsingSubqueryV6() {
		final String query = " delete from tab1 aaa"
				+ "\n using (select bbb.b1, bbb.b2, bbb.b3"
				+ "\n         from tab2 bbb"
				+ "\n        where bbb.b3 > 0) ccc"
				+ "\n where aaa.a1 = ccc.b1 and aaa.a2 = ccc.b2"
				+ "\n returning aaa.a1, aaa.a2, aaa.a3";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={delete={using={1={table={alias=ccc, query={select={1={column={name=b1, table_ref=bbb}}, 2={column={name=b2, table_ref=bbb}}, 3={column={name=b3, table_ref=bbb}}}, from={table={alias=bbb, table=tab2}}, where={condition={left={column={name=b3, table_ref=bbb}}, right={literal=0}, operator=>}}}}}}, where={and={1={condition={left={column={name=a1, table_ref=aaa}}, right={column={name=b1, table_ref=ccc}}, operator==}}, 2={condition={left={column={name=a2, table_ref=aaa}}, right={column={name=b2, table_ref=ccc}}, operator==}}}}, table={alias=aaa, table=tab1}, returning={1={column={name=a1, table_ref=aaa}}, 2={column={name=a2, table_ref=aaa}}, 3={column={name=a3, table_ref=aaa}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, a2, a3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@30,120:122='aaa',<381>,5:7], [@46,167:169='aaa',<381>,6:11]], a2=[[@38,140:142='aaa',<381>,5:27], [@50,175:177='aaa',<381>,6:19]], a3=[[@54,183:185='aaa',<381>,6:27]]}, tab2={b2=[[@11,45:47='bbb',<381>,2:23]], b3=[[@15,53:55='bbb',<381>,2:31], [@22,97:99='bbb',<381>,4:14]], b1=[[@7,37:39='bbb',<381>,2:15]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={b2=[[@13,49:50='b2',<381>,2:27], [@42,149:151='ccc',<381>,5:36]], b3=[[@17,57:58='b3',<381>,2:35]], b1=[[@9,41:42='b1',<381>,2:19], [@34,129:131='ccc',<381>,5:16]]}, delete1={a1=[[@48,171:172='a1',<381>,6:15]], a2=[[@52,179:180='a2',<381>,6:23]], a3=[[@56,187:188='a3',<381>,6:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{delete1={query_dictionary={a1=[[@48,171:172='a1',<381>,6:15]], a2=[[@52,179:180='a2',<381>,6:23]], a3=[[@56,187:188='a3',<381>,6:31]]}, table_dictionary={tab1={a1=[[@30,120:122='aaa',<381>,5:7], [@46,167:169='aaa',<381>,6:11]], a2=[[@38,140:142='aaa',<381>,5:27], [@50,175:177='aaa',<381>,6:19]], a3=[[@54,183:185='aaa',<381>,6:27]]}}, def_query0={query_dictionary={b2=[[@13,49:50='b2',<381>,2:27], [@42,149:151='ccc',<381>,5:36]], b3=[[@17,57:58='b3',<381>,2:35]], b1=[[@9,41:42='b1',<381>,2:19], [@34,129:131='ccc',<381>,5:16]]}, table_dictionary={tab2={b2=[[@11,45:47='bbb',<381>,2:23]], b3=[[@15,53:55='bbb',<381>,2:31], [@22,97:99='bbb',<381>,4:14]], b1=[[@7,37:39='bbb',<381>,2:15]]}}, filters=[{name=b3, table_ref=bbb}], interface={b2=[{name=b2, table_ref=bbb}], b3=[{name=b3, table_ref=bbb}], b1=[{name=b1, table_ref=bbb}]}, table_alias={bbb=tab2}}, filters=[{name=a1, table_ref=aaa}, {name=b1, table_ref=ccc}, {name=a2, table_ref=aaa}, {name=b2, table_ref=ccc}], interface={a1=[{name=a1, table_ref=aaa}], a2=[{name=a2, table_ref=aaa}], a3=[{name=a3, table_ref=aaa}]}, table_alias={aaa=tab1, ccc=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void deleteDictionaryHandlingPostgresReturningWindowedSubqueryUnqualifiedTargetV1() {
		final String query = " delete from tab1 aaa"
				+ "\n using (select bbb.b1, bbb.b2,"
				+ "\n              row_number() over (partition by bbb.b1 order by bbb.b3 desc) as ccc"
				+ "\n         from tab2 bbb"
				+ "\n        where bbb.b2 > 0) ddd"
				+ "\n where a1 = ddd.b1 and ddd.ccc = 1"
				+ "\n returning a1, a2, a3";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={delete={using={1={table={alias=ddd, query={select={1={column={name=b1, table_ref=bbb}}, 2={column={name=b2, table_ref=bbb}}, 3={alias=ccc, window_function={over={partition_by={1={column={name=b1, table_ref=bbb}}}, orderby={1={null_order=null, predicand={column={name=b3, table_ref=bbb}}, sort_order=desc}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=bbb, table=tab2}}, where={condition={left={column={name=b2, table_ref=bbb}}, right={literal=0}, operator=>}}}}}}, where={and={1={condition={left={column={name=a1, table_ref=null}}, right={column={name=b1, table_ref=ddd}}, operator==}}, 2={condition={left={column={name=ccc, table_ref=ddd}}, right={literal=1}, operator==}}}}, table={alias=aaa, table=tab1}, returning={1={column={name=a1, table_ref=null}}, 2={column={name=a2, table_ref=null}}, 3={column={name=a3, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, a2, a3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@46,195:196='a1',<381>,6:7], [@58,234:235='a1',<381>,7:11]], a2=[[@60,238:239='a2',<381>,7:15]], a3=[[@62,242:243='a3',<381>,7:19]]}, tab2={b2=[[@11,45:47='bbb',<381>,2:23], [@38,172:174='bbb',<381>,5:14]], b3=[[@27,115:117='bbb',<381>,3:62]], b1=[[@7,37:39='bbb',<381>,2:15], [@22,99:101='bbb',<381>,3:46]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={b2=[[@13,49:50='b2',<381>,2:27]], ccc=[[@33,131:133='ccc',<381>,3:78], [@52,211:213='ddd',<381>,6:23]], b1=[[@9,41:42='b1',<381>,2:19], [@48,200:202='ddd',<381>,6:12]]}, delete1={a1=[[@58,234:235='a1',<381>,7:11]], a2=[[@60,238:239='a2',<381>,7:15]], a3=[[@62,242:243='a3',<381>,7:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{delete1={query_dictionary={a1=[[@58,234:235='a1',<381>,7:11]], a2=[[@60,238:239='a2',<381>,7:15]], a3=[[@62,242:243='a3',<381>,7:19]]}, table_dictionary={tab1={a1=[[@46,195:196='a1',<381>,6:7], [@58,234:235='a1',<381>,7:11]], a2=[[@60,238:239='a2',<381>,7:15]], a3=[[@62,242:243='a3',<381>,7:19]]}}, def_query0={query_dictionary={b2=[[@13,49:50='b2',<381>,2:27]], ccc=[[@33,131:133='ccc',<381>,3:78], [@52,211:213='ddd',<381>,6:23]], b1=[[@9,41:42='b1',<381>,2:19], [@48,200:202='ddd',<381>,6:12]]}, table_dictionary={tab2={b2=[[@11,45:47='bbb',<381>,2:23], [@38,172:174='bbb',<381>,5:14]], b3=[[@27,115:117='bbb',<381>,3:62]], b1=[[@7,37:39='bbb',<381>,2:15], [@22,99:101='bbb',<381>,3:46]]}}, filters=[{name=b2, table_ref=bbb}], interface={b2=[{name=b2, table_ref=bbb}], ccc=[{name=b1, table_ref=bbb}, {name=b3, table_ref=bbb}], b1=[{name=b1, table_ref=bbb}]}, table_alias={bbb=tab2}}, filters=[{name=a1, table_ref=tab1}, {name=b1, table_ref=ddd}, {name=ccc, table_ref=ddd}], interface={a1=[{name=a1, table_ref=tab1}], a2=[{name=a2, table_ref=tab1}], a3=[{name=a3, table_ref=tab1}]}, table_alias={aaa=tab1, ddd=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void deleteDictionaryHandlingPostgresReturningQualifiedAcrossWhereSubclausesUnqualifiedTargetV2() {
		final String query = " delete from tab1 aaa"
				+ "\n using (select b1, b2, b3 from tab2) bbb"
				+ "\n where a1 = bbb.b1 and a2 = bbb.b2 and bbb.b3 > 0"
				+ "\n returning a1, a2, a3";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={delete={using={1={table={alias=bbb, query={select={1={column={name=b1, table_ref=null}}, 2={column={name=b2, table_ref=null}}, 3={column={name=b3, table_ref=null}}}, from={table={alias=null, table=tab2}}}}}}, where={and={1={condition={left={column={name=a1, table_ref=null}}, right={column={name=b1, table_ref=bbb}}, operator==}}, 2={condition={left={column={name=a2, table_ref=null}}, right={column={name=b2, table_ref=bbb}}, operator==}}, 3={condition={left={column={name=b3, table_ref=bbb}}, right={literal=0}, operator=>}}}}, table={alias=aaa, table=tab1}, returning={1={column={name=a1, table_ref=null}}, 2={column={name=a2, table_ref=null}}, 3={column={name=a3, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, a2, a3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@17,70:71='a1',<381>,3:7], [@35,124:125='a1',<381>,4:11]], a2=[[@23,86:87='a2',<381>,3:23], [@37,128:129='a2',<381>,4:15]], a3=[[@39,132:133='a3',<381>,4:19]]}, tab2={b2=[[@9,41:42='b2',<381>,2:19]], b3=[[@11,45:46='b3',<381>,2:23]], b1=[[@7,37:38='b1',<381>,2:15]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={b2=[[@9,41:42='b2',<381>,2:19], [@25,91:93='bbb',<381>,3:28]], b3=[[@11,45:46='b3',<381>,2:23], [@29,102:104='bbb',<381>,3:39]], b1=[[@7,37:38='b1',<381>,2:15], [@19,75:77='bbb',<381>,3:12]]}, delete1={a1=[[@35,124:125='a1',<381>,4:11]], a2=[[@37,128:129='a2',<381>,4:15]], a3=[[@39,132:133='a3',<381>,4:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{delete1={query_dictionary={a1=[[@35,124:125='a1',<381>,4:11]], a2=[[@37,128:129='a2',<381>,4:15]], a3=[[@39,132:133='a3',<381>,4:19]]}, table_dictionary={tab1={a1=[[@17,70:71='a1',<381>,3:7], [@35,124:125='a1',<381>,4:11]], a2=[[@23,86:87='a2',<381>,3:23], [@37,128:129='a2',<381>,4:15]], a3=[[@39,132:133='a3',<381>,4:19]]}}, def_query0={query_dictionary={b2=[[@9,41:42='b2',<381>,2:19], [@25,91:93='bbb',<381>,3:28]], b3=[[@11,45:46='b3',<381>,2:23], [@29,102:104='bbb',<381>,3:39]], b1=[[@7,37:38='b1',<381>,2:15], [@19,75:77='bbb',<381>,3:12]]}, table_dictionary={tab2={b2=[[@9,41:42='b2',<381>,2:19]], b3=[[@11,45:46='b3',<381>,2:23]], b1=[[@7,37:38='b1',<381>,2:15]]}}, interface={b2=[{name=b2, table_ref=tab2}], b3=[{name=b3, table_ref=tab2}], b1=[{name=b1, table_ref=tab2}]}}, filters=[{name=a1, table_ref=tab1}, {name=b1, table_ref=bbb}, {name=a2, table_ref=tab1}, {name=b2, table_ref=bbb}, {name=b3, table_ref=bbb}], interface={a1=[{name=a1, table_ref=tab1}], a2=[{name=a2, table_ref=tab1}], a3=[{name=a3, table_ref=tab1}]}, table_alias={aaa=tab1, bbb=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void deleteDictionaryHandlingPostgresReturningOrderBySubqueryUnqualifiedTargetV3() {
		final String query = " delete from tab1 aaa"
				+ "\n using (select bbb.b1, bbb.b2"
				+ "\n         from tab2 bbb"
				+ "\n        order by bbb.b3 desc) ccc"
				+ "\n where a1 = ccc.b1 and ccc.b2 > 0"
				+ "\n returning a1, a2, a3";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={delete={using={1={table={alias=ccc, query={select={1={column={name=b1, table_ref=bbb}}, 2={column={name=b2, table_ref=bbb}}}, orderby={1={null_order=null, predicand={column={name=b3, table_ref=bbb}}, sort_order=desc}}, from={table={alias=bbb, table=tab2}}}}}}, where={and={1={condition={left={column={name=a1, table_ref=null}}, right={column={name=b1, table_ref=ccc}}, operator==}}, 2={condition={left={column={name=b2, table_ref=ccc}}, right={literal=0}, operator=>}}}}, table={alias=aaa, table=tab1}, returning={1={column={name=a1, table_ref=null}}, 2={column={name=a2, table_ref=null}}, 3={column={name=a3, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, a2, a3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@26,116:117='a1',<381>,5:7], [@38,154:155='a1',<381>,6:11]], a2=[[@40,158:159='a2',<381>,6:15]], a3=[[@42,162:163='a3',<381>,6:19]]}, tab2={b2=[[@11,45:47='bbb',<381>,2:23]], b3=[[@19,92:94='bbb',<381>,4:17]], b1=[[@7,37:39='bbb',<381>,2:15]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={b2=[[@13,49:50='b2',<381>,2:27], [@32,132:134='ccc',<381>,5:23]], b1=[[@9,41:42='b1',<381>,2:19], [@28,121:123='ccc',<381>,5:12]]}, delete1={a1=[[@38,154:155='a1',<381>,6:11]], a2=[[@40,158:159='a2',<381>,6:15]], a3=[[@42,162:163='a3',<381>,6:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{delete1={query_dictionary={a1=[[@38,154:155='a1',<381>,6:11]], a2=[[@40,158:159='a2',<381>,6:15]], a3=[[@42,162:163='a3',<381>,6:19]]}, table_dictionary={tab1={a1=[[@26,116:117='a1',<381>,5:7], [@38,154:155='a1',<381>,6:11]], a2=[[@40,158:159='a2',<381>,6:15]], a3=[[@42,162:163='a3',<381>,6:19]]}}, def_query0={query_dictionary={b2=[[@13,49:50='b2',<381>,2:27], [@32,132:134='ccc',<381>,5:23]], b1=[[@9,41:42='b1',<381>,2:19], [@28,121:123='ccc',<381>,5:12]]}, table_dictionary={tab2={b2=[[@11,45:47='bbb',<381>,2:23]], b3=[[@19,92:94='bbb',<381>,4:17]], b1=[[@7,37:39='bbb',<381>,2:15]]}}, ordered_by=[{name=b3, table_ref=bbb}], interface={b2=[{name=b2, table_ref=bbb}], b1=[{name=b1, table_ref=bbb}]}, table_alias={bbb=tab2}}, filters=[{name=a1, table_ref=tab1}, {name=b1, table_ref=ccc}, {name=b2, table_ref=ccc}], interface={a1=[{name=a1, table_ref=tab1}], a2=[{name=a2, table_ref=tab1}], a3=[{name=a3, table_ref=tab1}]}, table_alias={aaa=tab1, ccc=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void deleteDictionaryHandlingPostgresReturningJoinInSubqueryUnqualifiedTargetV4() {
		final String query = " delete from tab1 aaa"
				+ "\n using (select bbb.b1, bbb.b2"
				+ "\n         from tab2 bbb"
				+ "\n         join tab1 eee on eee.a1 = bbb.b1 and eee.a3 = bbb.b3) ccc"
				+ "\n where a1 = ccc.b1"
				+ "\n returning a1, a2, a3";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={delete={using={1={table={alias=ccc, query={select={1={column={name=b1, table_ref=bbb}}, 2={column={name=b2, table_ref=bbb}}}, from={join={1={table={alias=bbb, table=tab2}}, 2={join=join, on={and={1={condition={left={column={name=a1, table_ref=eee}}, right={column={name=b1, table_ref=bbb}}, operator==}}, 2={condition={left={column={name=a3, table_ref=eee}}, right={column={name=b3, table_ref=bbb}}, operator==}}}}}, 3={table={alias=eee, table=tab1}}}}}}}}, where={condition={left={column={name=a1, table_ref=null}}, right={column={name=b1, table_ref=ccc}}, operator==}}, table={alias=aaa, table=tab1}, returning={1={column={name=a1, table_ref=null}}, 2={column={name=a2, table_ref=null}}, 3={column={name=a3, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, a2, a3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@21,101:103='eee',<381>,4:26], [@39,149:150='a1',<381>,5:7], [@45,172:173='a1',<381>,6:11]], a2=[[@47,176:177='a2',<381>,6:15]], a3=[[@29,121:123='eee',<381>,4:46], [@49,180:181='a3',<381>,6:19]]}, tab2={b2=[[@11,45:47='bbb',<381>,2:23]], b3=[[@33,130:132='bbb',<381>,4:55]], b1=[[@7,37:39='bbb',<381>,2:15], [@25,110:112='bbb',<381>,4:35]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={b2=[[@13,49:50='b2',<381>,2:27]], b1=[[@9,41:42='b1',<381>,2:19], [@41,154:156='ccc',<381>,5:12]]}, delete1={a1=[[@45,172:173='a1',<381>,6:11]], a2=[[@47,176:177='a2',<381>,6:15]], a3=[[@49,180:181='a3',<381>,6:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{delete1={query_dictionary={a1=[[@45,172:173='a1',<381>,6:11]], a2=[[@47,176:177='a2',<381>,6:15]], a3=[[@49,180:181='a3',<381>,6:19]]}, table_dictionary={tab1={a1=[[@39,149:150='a1',<381>,5:7], [@45,172:173='a1',<381>,6:11]], a2=[[@47,176:177='a2',<381>,6:15]], a3=[[@49,180:181='a3',<381>,6:19]]}}, def_query0={query_dictionary={b2=[[@13,49:50='b2',<381>,2:27]], b1=[[@9,41:42='b1',<381>,2:19], [@41,154:156='ccc',<381>,5:12]]}, table_dictionary={tab1={a1=[[@21,101:103='eee',<381>,4:26], [@39,149:150='a1',<381>,5:7], [@45,172:173='a1',<381>,6:11]], a3=[[@29,121:123='eee',<381>,4:46], [@49,180:181='a3',<381>,6:19]]}, tab2={b2=[[@11,45:47='bbb',<381>,2:23]], b3=[[@33,130:132='bbb',<381>,4:55]], b1=[[@7,37:39='bbb',<381>,2:15], [@25,110:112='bbb',<381>,4:35]]}}, filters=[{name=a1, table_ref=eee}, {name=b1, table_ref=bbb}, {name=a3, table_ref=eee}, {name=b3, table_ref=bbb}], interface={b2=[{name=b2, table_ref=bbb}], b1=[{name=b1, table_ref=bbb}]}, table_alias={bbb=tab2, eee=tab1}}, filters=[{name=a1, table_ref=tab1}, {name=b1, table_ref=ccc}], interface={a1=[{name=a1, table_ref=tab1}], a2=[{name=a2, table_ref=tab1}], a3=[{name=a3, table_ref=tab1}]}, table_alias={aaa=tab1, ccc=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void deleteDictionaryHandlingPostgresReturningColumnsUnqualifiedTargetV5() {
		final String query = " delete from tab1 aaa"
				+ "\n using tab2 bbb"
				+ "\n where a1 = bbb.b1 and a2 = bbb.b2"
				+ "\n returning a1, a2, a3";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={delete={using={1={table={alias=bbb, table=tab2}}}, where={and={1={condition={left={column={name=a1, table_ref=null}}, right={column={name=b1, table_ref=bbb}}, operator==}}, 2={condition={left={column={name=a2, table_ref=null}}, right={column={name=b2, table_ref=bbb}}, operator==}}}}, table={alias=aaa, table=tab1}, returning={1={column={name=a1, table_ref=null}}, 2={column={name=a2, table_ref=null}}, 3={column={name=a3, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, a2, a3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@8,45:46='a1',<381>,3:7], [@20,84:85='a1',<381>,4:11]], a2=[[@14,61:62='a2',<381>,3:23], [@22,88:89='a2',<381>,4:15]], a3=[[@24,92:93='a3',<381>,4:19]]}, tab2={b2=[[@16,66:68='bbb',<381>,3:28]], b1=[[@10,50:52='bbb',<381>,3:12]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{delete0={a1=[[@20,84:85='a1',<381>,4:11]], a2=[[@22,88:89='a2',<381>,4:15]], a3=[[@24,92:93='a3',<381>,4:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{delete0={query_dictionary={a1=[[@20,84:85='a1',<381>,4:11]], a2=[[@22,88:89='a2',<381>,4:15]], a3=[[@24,92:93='a3',<381>,4:19]]}, table_dictionary={tab1={a1=[[@8,45:46='a1',<381>,3:7], [@20,84:85='a1',<381>,4:11]], a2=[[@14,61:62='a2',<381>,3:23], [@22,88:89='a2',<381>,4:15]], a3=[[@24,92:93='a3',<381>,4:19]]}, tab2={b2=[[@16,66:68='bbb',<381>,3:28]], b1=[[@10,50:52='bbb',<381>,3:12]]}}, filters=[{name=a1, table_ref=tab1}, {name=b1, table_ref=bbb}, {name=a2, table_ref=tab1}, {name=b2, table_ref=bbb}], interface={a1=[{name=a1, table_ref=tab1}], a2=[{name=a2, table_ref=tab1}], a3=[{name=a3, table_ref=tab1}]}, table_alias={aaa=tab1, bbb=tab2}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void deleteDictionaryHandlingPostgresReturningColumnsFromUsingSubqueryUnqualifiedTargetV6() {
		final String query = " delete from tab1 aaa"
				+ "\n using (select bbb.b1, bbb.b2, bbb.b3"
				+ "\n         from tab2 bbb"
				+ "\n        where bbb.b3 > 0) ccc"
				+ "\n where a1 = ccc.b1 and a2 = ccc.b2"
				+ "\n returning a1, a2, a3";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={delete={using={1={table={alias=ccc, query={select={1={column={name=b1, table_ref=bbb}}, 2={column={name=b2, table_ref=bbb}}, 3={column={name=b3, table_ref=bbb}}}, from={table={alias=bbb, table=tab2}}, where={condition={left={column={name=b3, table_ref=bbb}}, right={literal=0}, operator=>}}}}}}, where={and={1={condition={left={column={name=a1, table_ref=null}}, right={column={name=b1, table_ref=ccc}}, operator==}}, 2={condition={left={column={name=a2, table_ref=null}}, right={column={name=b2, table_ref=ccc}}, operator==}}}}, table={alias=aaa, table=tab1}, returning={1={column={name=a1, table_ref=null}}, 2={column={name=a2, table_ref=null}}, 3={column={name=a3, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, a2, a3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@30,120:121='a1',<381>,5:7], [@42,159:160='a1',<381>,6:11]], a2=[[@36,136:137='a2',<381>,5:23], [@44,163:164='a2',<381>,6:15]], a3=[[@46,167:168='a3',<381>,6:19]]}, tab2={b2=[[@11,45:47='bbb',<381>,2:23]], b3=[[@15,53:55='bbb',<381>,2:31], [@22,97:99='bbb',<381>,4:14]], b1=[[@7,37:39='bbb',<381>,2:15]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={b2=[[@13,49:50='b2',<381>,2:27], [@38,141:143='ccc',<381>,5:28]], b3=[[@17,57:58='b3',<381>,2:35]], b1=[[@9,41:42='b1',<381>,2:19], [@32,125:127='ccc',<381>,5:12]]}, delete1={a1=[[@42,159:160='a1',<381>,6:11]], a2=[[@44,163:164='a2',<381>,6:15]], a3=[[@46,167:168='a3',<381>,6:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{delete1={query_dictionary={a1=[[@42,159:160='a1',<381>,6:11]], a2=[[@44,163:164='a2',<381>,6:15]], a3=[[@46,167:168='a3',<381>,6:19]]}, table_dictionary={tab1={a1=[[@30,120:121='a1',<381>,5:7], [@42,159:160='a1',<381>,6:11]], a2=[[@36,136:137='a2',<381>,5:23], [@44,163:164='a2',<381>,6:15]], a3=[[@46,167:168='a3',<381>,6:19]]}}, def_query0={query_dictionary={b2=[[@13,49:50='b2',<381>,2:27], [@38,141:143='ccc',<381>,5:28]], b3=[[@17,57:58='b3',<381>,2:35]], b1=[[@9,41:42='b1',<381>,2:19], [@32,125:127='ccc',<381>,5:12]]}, table_dictionary={tab2={b2=[[@11,45:47='bbb',<381>,2:23]], b3=[[@15,53:55='bbb',<381>,2:31], [@22,97:99='bbb',<381>,4:14]], b1=[[@7,37:39='bbb',<381>,2:15]]}}, filters=[{name=b3, table_ref=bbb}], interface={b2=[{name=b2, table_ref=bbb}], b3=[{name=b3, table_ref=bbb}], b1=[{name=b1, table_ref=bbb}]}, table_alias={bbb=tab2}}, filters=[{name=a1, table_ref=tab1}, {name=b1, table_ref=ccc}, {name=a2, table_ref=tab1}, {name=b2, table_ref=ccc}], interface={a1=[{name=a1, table_ref=tab1}], a2=[{name=a2, table_ref=tab1}], a3=[{name=a3, table_ref=tab1}]}, table_alias={aaa=tab1, ccc=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void deleteDictionaryHandlingPostgresReturningColumnsFromUsingSubqueryUnqualifiedTargetV7() {
		final String query = " delete from tab1 aaa"
				+ "\n using (select bbb.b1, bbb.b2, bbb.b3"
				+ "\n         from tab2 bbb"
				+ "\n        where bbb.b3 > 0) ccc, users uuu"
				+ "\n where a1 = ccc.b1 and a2 = ccc.b2 and uuu.id = ccc.b3"
				+ "\n returning a1, a2, a3, ccc.b1, ccc.b2, ccc.b3";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={delete={using={1={join={1={table={alias=ccc, query={select={1={column={name=b1, table_ref=bbb}}, 2={column={name=b2, table_ref=bbb}}, 3={column={name=b3, table_ref=bbb}}}, from={table={alias=bbb, table=tab2}}, where={condition={left={column={name=b3, table_ref=bbb}}, right={literal=0}, operator=>}}}}}, 2={table={alias=uuu, table=users}}}}}, where={and={1={condition={left={column={name=a1, table_ref=null}}, right={column={name=b1, table_ref=ccc}}, operator==}}, 2={condition={left={column={name=a2, table_ref=null}}, right={column={name=b2, table_ref=ccc}}, operator==}}, 3={condition={left={column={name=id, table_ref=uuu}}, right={column={name=b3, table_ref=ccc}}, operator==}}}}, table={alias=aaa, table=tab1}, returning={1={column={name=a1, table_ref=null}}, 2={column={name=a2, table_ref=null}}, 3={column={name=a3, table_ref=null}}, 4={column={name=b1, table_ref=ccc}}, 5={column={name=b2, table_ref=ccc}}, 6={column={name=b3, table_ref=ccc}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, b2, a2, b3, a3, b1]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@33,131:132='a1',<381>,5:7], [@53,190:191='a1',<381>,6:11]], a2=[[@39,147:148='a2',<381>,5:23], [@55,194:195='a2',<381>,6:15]], a3=[[@57,198:199='a3',<381>,6:19]]}, tab2={b2=[[@11,45:47='bbb',<381>,2:23]], b3=[[@15,53:55='bbb',<381>,2:31], [@22,97:99='bbb',<381>,4:14]], b1=[[@7,37:39='bbb',<381>,2:15]]}, users={id=[[@45,163:165='uuu',<381>,5:39]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={b2=[[@13,49:50='b2',<381>,2:27], [@41,152:154='ccc',<381>,5:28], [@63,210:212='ccc',<381>,6:31]], b3=[[@17,57:58='b3',<381>,2:35], [@49,172:174='ccc',<381>,5:48], [@67,218:220='ccc',<381>,6:39]], b1=[[@9,41:42='b1',<381>,2:19], [@35,136:138='ccc',<381>,5:12], [@59,202:204='ccc',<381>,6:23]]}, delete1={a1=[[@53,190:191='a1',<381>,6:11]], b2=[[@65,214:215='b2',<381>,6:35]], a2=[[@55,194:195='a2',<381>,6:15]], b3=[[@69,222:223='b3',<381>,6:43]], a3=[[@57,198:199='a3',<381>,6:19]], b1=[[@61,206:207='b1',<381>,6:27]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{delete1={query_dictionary={a1=[[@53,190:191='a1',<381>,6:11]], b2=[[@65,214:215='b2',<381>,6:35]], a2=[[@55,194:195='a2',<381>,6:15]], b3=[[@69,222:223='b3',<381>,6:43]], a3=[[@57,198:199='a3',<381>,6:19]], b1=[[@61,206:207='b1',<381>,6:27]]}, table_dictionary={tab1={a1=[[@33,131:132='a1',<381>,5:7], [@53,190:191='a1',<381>,6:11]], a2=[[@39,147:148='a2',<381>,5:23], [@55,194:195='a2',<381>,6:15]], a3=[[@57,198:199='a3',<381>,6:19]]}, users={id=[[@45,163:165='uuu',<381>,5:39]]}}, def_query0={query_dictionary={b2=[[@13,49:50='b2',<381>,2:27], [@41,152:154='ccc',<381>,5:28], [@63,210:212='ccc',<381>,6:31]], b3=[[@17,57:58='b3',<381>,2:35], [@49,172:174='ccc',<381>,5:48], [@67,218:220='ccc',<381>,6:39]], b1=[[@9,41:42='b1',<381>,2:19], [@35,136:138='ccc',<381>,5:12], [@59,202:204='ccc',<381>,6:23]]}, table_dictionary={tab2={b2=[[@11,45:47='bbb',<381>,2:23]], b3=[[@15,53:55='bbb',<381>,2:31], [@22,97:99='bbb',<381>,4:14]], b1=[[@7,37:39='bbb',<381>,2:15]]}}, filters=[{name=b3, table_ref=bbb}], interface={b2=[{name=b2, table_ref=bbb}], b3=[{name=b3, table_ref=bbb}], b1=[{name=b1, table_ref=bbb}]}, table_alias={bbb=tab2}}, filters=[{name=a1, table_ref=tab1}, {name=b1, table_ref=ccc}, {name=a2, table_ref=tab1}, {name=b2, table_ref=ccc}, {name=id, table_ref=uuu}, {name=b3, table_ref=ccc}], interface={a1=[{name=a1, table_ref=tab1}], b2=[{name=b2, table_ref=ccc}], a2=[{name=a2, table_ref=tab1}], b3=[{name=b3, table_ref=ccc}], a3=[{name=a3, table_ref=tab1}], b1=[{name=b1, table_ref=ccc}]}, table_alias={aaa=tab1, ccc=query0, uuu=users}}}",
				extractor.getSymbolTable().toString());
	}


}
